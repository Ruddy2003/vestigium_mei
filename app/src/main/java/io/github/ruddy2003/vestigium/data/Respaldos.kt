package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.nio.ByteBuffer
import java.security.KeyStore
import java.security.SecureRandom
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Respaldo completo en un solo archivo y su restauración de un toque.
 *
 * El archivo es un .zip con el respaldo JSON (diario, cartas, ajustes, todo), las ubicaciones, el catálogo de fotos con lugar
 * (solo hora y coordenadas, nunca la foto) y las portadas. Con contraseña, ese .zip va cifrado (AES-256-GCM, llave derivada con
 * PBKDF2) en un .vmc: aunque alguien entre a tu Drive o a la carpeta, no puede leer el diario, las cartas, los chats ni lo privado.
 *
 * La contraseña se guarda solo en este teléfono, cifrada con una llave del Android Keystore; nunca viaja en ningún respaldo.
 * Si la olvidas, los respaldos cifrados no se pueden abrir: no hay forma de recuperarla.
 */
object Respaldos {
    const val BASE = "vestigium_respaldo"
    // Formato 1 (el de las versiones anteriores): marca, sal, iv y cuerpo, siempre a 210 000 iteraciones. Ya no se escribe, pero se sigue abriendo.
    private val MAGIA_1 = "VESTIGIUM-MEI-CIFRADO-1".toByteArray()
    private const val ITERACIONES_1 = 210_000
    // Formato 2: marca, iteraciones (4 bytes, big-endian), sal, iv y cuerpo. Como las iteraciones viajan en el archivo,
    // se pueden subir más adelante sin otro formato. 600 000 es lo que recomienda OWASP para PBKDF2-HMAC-SHA256 (H-8-9).
    private val MAGIA_2 = "VESTIGIUM-MEI-CIFRADO-2".toByteArray()
    private const val ITERACIONES = 600_000
    // Lo que se acepta al leer: fuera de esto el archivo está dañado, y una cifra enorme colgaría la app derivando la llave.
    private const val ITER_MIN = 100_000
    private const val ITER_MAX = 10_000_000

    // ------------------------------------------------------------ cifrado

    /** PBKDF2 tarda a propósito (a 600 000 vueltas, un segundo o más en el teléfono): solo se llama fuera del hilo principal. */
    private fun llave(contrasena: CharArray, sal: ByteArray, iteraciones: Int): SecretKeySpec {
        val f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return SecretKeySpec(f.generateSecret(PBEKeySpec(contrasena, sal, iteraciones, 256)).encoded, "AES")
    }

    private fun empiezaCon(b: ByteArray, m: ByteArray) = b.size >= m.size && b.copyOfRange(0, m.size).contentEquals(m)

    /** 2 o 1 según la marca del archivo; 0 si no lleva ninguna (no está cifrado). */
    private fun formato(b: ByteArray): Int = when { empiezaCon(b, MAGIA_2) -> 2; empiezaCon(b, MAGIA_1) -> 1; else -> 0 }

    /** Lo que mide el encabezado antes de la sal: la marca y, en el formato 2, las iteraciones. Va completo como AAD. */
    private fun cabecera(v: Int): Int = if (v == 2) MAGIA_2.size + 4 else MAGIA_1.size

    fun estaCifrado(b: ByteArray): Boolean = formato(b).let { it != 0 && b.size > cabecera(it) + 28 }

    /** Siempre en el formato 2. */
    fun cifrar(datos: ByteArray, contrasena: CharArray): ByteArray {
        val r = SecureRandom()
        val sal = ByteArray(16).also { r.nextBytes(it) }
        val iv = ByteArray(12).also { r.nextBytes(it) }
        val cab = MAGIA_2 + ByteBuffer.allocate(4).putInt(ITERACIONES).array()
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, llave(contrasena, sal, ITERACIONES), GCMParameterSpec(128, iv))
        c.updateAAD(cab)
        val cuerpo = c.doFinal(datos)
        val o = cab.size
        val out = ByteArray(o + 28 + cuerpo.size)
        System.arraycopy(cab, 0, out, 0, o)
        System.arraycopy(sal, 0, out, o, 16)
        System.arraycopy(iv, 0, out, o + 16, 12)
        System.arraycopy(cuerpo, 0, out, o + 28, cuerpo.size)
        return out
    }

    /** null si la contraseña no es la correcta (o el archivo está dañado). Abre los dos formatos. */
    fun descifrar(b: ByteArray, contrasena: CharArray): ByteArray? = runCatching {
        val v = formato(b)
        if (v == 0) return null
        val o = cabecera(v)
        if (b.size <= o + 28) return null
        val iteraciones = if (v == 2) ByteBuffer.wrap(b, MAGIA_2.size, 4).int else ITERACIONES_1
        if (iteraciones !in ITER_MIN..ITER_MAX) return null
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.DECRYPT_MODE, llave(contrasena, b.copyOfRange(o, o + 16), iteraciones), GCMParameterSpec(128, b.copyOfRange(o + 16, o + 28)))
        c.updateAAD(b, 0, o)
        c.doFinal(b, o + 28, b.size - o - 28)
    }.getOrNull()

    // ------------------------------------------------------------ la contraseña en este teléfono

    private const val ALIAS = "vestigium_respaldo"
    private fun prefs(ctx: Context) = ctx.getSharedPreferences("respaldo_secreto", Context.MODE_PRIVATE)

    private fun llaveTelefono(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val g = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        g.init(KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build())
        return g.generateKey()
    }

    fun cifradoActivo(ctx: Context): Boolean = prefs(ctx).contains("c")

    fun ponerContrasena(ctx: Context, nueva: String?) {
        if (nueva.isNullOrEmpty()) { prefs(ctx).edit().remove("c").apply(); return }
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, llaveTelefono())
        val cuerpo = c.doFinal(nueva.toByteArray(Charsets.UTF_8))
        prefs(ctx).edit().putString("c", Base64.encodeToString(c.iv + cuerpo, Base64.NO_WRAP)).apply()
    }

    fun contrasena(ctx: Context): CharArray? = runCatching {
        val b = Base64.decode(prefs(ctx).getString("c", null) ?: return null, Base64.NO_WRAP)
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.DECRYPT_MODE, llaveTelefono(), GCMParameterSpec(128, b, 0, 12))
        String(c.doFinal(b, 12, b.size - 12), Charsets.UTF_8).toCharArray()
    }.getOrNull()

    // ------------------------------------------------------------ armar el respaldo

    /** Lo indispensable para rehacer la app en otro teléfono: el respaldo, ubicaciones, catálogo de fotos y portadas. */
    private fun escribirZip(ctx: Context, destino: File, conTablas: Boolean) {
        ZipOutputStream(destino.outputStream().buffered()).use { z ->
            fun poner(nombre: String, texto: String) { z.putNextEntry(ZipEntry(nombre)); z.write(texto.toByteArray()); z.closeEntry() }
            poner("vm_respaldo.json", Exportar.respaldoJson())
            poner("vm_ubicaciones.csv", Exportar.csvUbicaciones())
            poner("vm_fotos_catalogo.csv", Exportar.csvCatalogoFotos())
            if (conTablas) Exportar.archivos(ctx).filter { it.first !in setOf("vm_respaldo.json", "vm_ubicaciones.csv", "vm_fotos_catalogo.csv") }.forEach { (n, t) -> poner(n, t) }
            else poner("LEEME.txt", "Respaldo completo de Vestigium Mei (${Fechas.ahoraIso()}).\nPara restaurarlo: Más → Datos → Respaldo completo → Restaurar desde un archivo.\n")
            Portadas.carpeta(ctx).listFiles()?.forEach { p -> z.putNextEntry(ZipEntry("portadas/" + p.name)); p.inputStream().use { it.copyTo(z) }; z.closeEntry() }
        }
    }

    /**
     * El respaldo completo listo para guardar: .zip, o .vmc cifrado si pusiste contraseña.
     * [fecha] lo nombra (copias semanales); sin fecha, el nombre fijo se reemplaza cada día.
     */
    fun archivo(ctx: Context, fecha: String? = null, conTablas: Boolean = false): File {
        val dir = File(ctx.cacheDir, "respaldo").apply { mkdirs() }
        val nombre = BASE + (fecha?.let { "_$it" } ?: "")
        val zip = File(dir, "$nombre.zip")
        escribirZip(ctx, zip, conTablas)
        val clave = contrasena(ctx) ?: return zip
        val vmc = File(dir, "$nombre.vmc")
        vmc.writeBytes(cifrar(zip.readBytes(), clave))
        clave.fill(' ')
        zip.delete()
        return vmc
    }

    /** Una copia fechada en la carpeta que se puede compartir (Guardar en Drive, en Archivos, mandarla a la compu…). */
    fun paraCompartir(ctx: Context): File {
        val a = archivo(ctx, Fechas.hoyCalendario())
        val dir = File(ctx.cacheDir, "export").apply { mkdirs() }
        val f = File(dir, a.name)
        f.delete(); a.copyTo(f); a.delete()
        return f
    }

    fun mime(nombre: String) = if (nombre.endsWith(".vmc")) "application/octet-stream" else "application/zip"

    // ------------------------------------------------------------ respaldo automático en una carpeta

    /** Archivos sueltos que la app deja en la carpeta o en Drive (y que se borran al activar el cifrado). */
    fun esArchivoPlano(n: String) = (n.startsWith("vm_") && (n.endsWith(".csv") || n.endsWith(".json"))) || n == "LEEME.txt" || n == "vestigium.R" ||
        (n.startsWith("vestigium_") && n.endsWith(".zip"))

    /**
     * Con datos privados y sin contraseña, ningún respaldo sale del teléfono: ni a la carpeta, ni a Drive, ni al compartirlo.
     * (La exportación para R es la excepción: la pides tú, a mano, y R necesita leerla; la pantalla lo advierte.)
     */
    fun bloqueado(ctx: Context): Boolean = !cifradoActivo(ctx) && Privado.hayDatos()

    /** Lo que se compara entre un respaldo y el anterior para saber si se empobreció. */
    fun conteo(): Map<String, Int> = mapOf("dias" to Repo.dias.size, "momentos" to Repo.momentos.size)

    /**
     * El respaldo nuevo trae menos del 90 % de los días o de los momentos que el anterior (solo cuenta si el anterior tenía 20 o más).
     * Puede ser un borrado a propósito o datos que no se cargaron: en los dos casos no conviene pisar la copia buena sin preguntar.
     */
    fun empobrecido(previo: Map<String, Int>, ahora: Map<String, Int>): Boolean =
        listOf("dias", "momentos").any { k -> val p = previo[k] ?: 0; p >= 20 && (ahora[k] ?: 0) < 0.9 * p }

    const val PAUSA = "Tus respaldos están en pausa: llevan datos privados y no tienen contraseña. Ponla en Más → Datos → Respaldo completo → Cifrar con contraseña."

    /** Con contraseña: solo el .vmc cifrado (y se borran los CSV sueltos de antes). Sin contraseña: los CSV para R y el .zip completo. */
    fun aCarpeta(ctx: Context): Boolean {
        if (bloqueado(ctx)) return false
        val uriTxt = Repo.config.carpeta_respaldo ?: return false
        val dir = DocumentFile.fromTreeUri(ctx, Uri.parse(uriTxt)) ?: return false
        if (!dir.canWrite()) return false
        val cifra = cifradoActivo(ctx)
        if (!cifra) Exportar.archivos(ctx).forEach { (nombre, cont) ->
            // Para .R se usa un tipo genérico: así el sistema no le agrega «.txt» al nombre.
            val m = when { nombre.endsWith(".csv") -> "text/csv"; nombre.endsWith(".json") -> "application/json"; nombre.endsWith(".R") -> "application/octet-stream"; else -> "text/plain" }
            val f = dir.findFile(nombre) ?: dir.createFile(m, nombre) ?: return@forEach
            ctx.contentResolver.openOutputStream(f.uri, "wt")?.use { it.write(cont.toByteArray()) }
        }
        val a = archivo(ctx)
        val f = dir.findFile(a.name) ?: dir.createFile(mime(a.name), a.name) ?: run { a.delete(); return false }
        val escrito = ctx.contentResolver.openOutputStream(f.uri, "wt")?.use { o -> a.inputStream().use { it.copyTo(o) }; true } ?: false
        a.delete()
        if (!escrito) return false
        // Los archivos en claro de antes se borran solo cuando ya quedó la copia cifrada.
        if (cifra) dir.listFiles().filter { x -> x.name?.let { esArchivoPlano(it) } == true }.forEach { runCatching { it.delete() } }
        // Si cambió el modo, el archivo del otro tipo ya no sirve.
        dir.findFile(BASE + if (cifra) ".zip" else ".vmc")?.let { runCatching { it.delete() } }
        Repo.cambiarConfig { it.copy(respaldo_ultimo = Fechas.ahoraIso()) }
        return true
    }

    // ------------------------------------------------------------ restaurar

    sealed class Lectura {
        data class Listo(val mensaje: String) : Lectura()
        data class Error(val mensaje: String) : Lectura()
        object PideContrasena : Lectura()
    }

    fun restaurarUri(ctx: Context, uri: Uri, contrasena: CharArray?): Lectura {
        val b = runCatching { ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() } }.getOrNull() ?: return Lectura.Error("No pude leer el archivo.")
        return restaurar(ctx, b, contrasena)
    }

    /** Restaura un respaldo: .vmc cifrado, .zip (completo o la exportación extendida) o el JSON suelto. Lo que ya existe no se duplica. */
    fun restaurar(ctx: Context, datos: ByteArray, contrasena: CharArray?): Lectura {
        var b = datos
        if (estaCifrado(b)) {
            val clave = contrasena ?: contrasena(ctx) ?: return Lectura.PideContrasena
            b = descifrar(b, clave) ?: return if (contrasena == null) Lectura.PideContrasena else Lectura.Error("La contraseña no abre este respaldo.")
        }
        val esZip = b.size > 4 && b[0] == 'P'.code.toByte() && b[1] == 'K'.code.toByte()
        if (!esZip) {
            val t = b.toString(Charsets.UTF_8)
            val v = runCatching { JSON.parseToJsonElement(t).jsonObject["schema_v"]?.jsonPrimitive?.intOrNull }.getOrNull()
            Importar.esquemaFuturo(v)?.let { return Lectura.Error(it) }
            return Lectura.Listo(Importar.respaldoTexto(t).mensaje)
        }
        val tmp = File(ctx.cacheDir, "restaurar.zip")
        tmp.writeBytes(b)
        try {
            ZipFile(tmp).use { z ->
                // Primero el respaldo (trae los ajustes que deciden qué fotos cuentan), luego ubicaciones, catálogo y portadas.
                val json = z.entradaQueTermina("vm_respaldo.json") ?: return Lectura.Error("Ese .zip no trae vm_respaldo.json: no es un respaldo de Vestigium Mei.")
                val texto = z.getInputStream(json).use { it.readBytes().toString(Charsets.UTF_8) }
                // De una versión más nueva: no se restaura nada (ni ubicaciones ni portadas), para no dejarlo a medias.
                val v = runCatching { JSON.parseToJsonElement(texto).jsonObject["schema_v"]?.jsonPrimitive?.intOrNull }.getOrNull()
                Importar.esquemaFuturo(v)?.let { return Lectura.Error(it) }
                val msg = Importar.respaldoTexto(texto).mensaje
                val nUb = z.entradaQueTermina("vm_ubicaciones.csv")?.let { e -> z.getInputStream(e).bufferedReader().use { ubicaciones(it.readLines()) } } ?: 0
                val nFo = z.entradaQueTermina("vm_fotos_catalogo.csv")?.let { e -> z.getInputStream(e).bufferedReader().use { catalogo(it.readLines()) } } ?: 0
                if (nFo > 0) runCatching { io.github.ruddy2003.vestigium.sensores.FotosLugar.aplicar(ctx) }
                var nPo = 0
                val portadas = Portadas.carpeta(ctx)
                z.entries().asSequence().filter { !it.isDirectory && it.name.contains("portadas/") }.forEach { e ->
                    val f = File(portadas, e.name.substringAfterLast('/'))
                    if (!f.exists()) { z.getInputStream(e).use { i -> f.outputStream().use { i.copyTo(it) } }; nPo++ }
                }
                return Lectura.Listo(msg + " Ubicaciones nuevas: %,d. Fotos con lugar: %,d. Portadas: %d.".format(nUb, nFo, nPo))
            }
        } catch (e: Exception) {
            return Lectura.Error("No pude abrir el respaldo: ${e.message}")
        } finally { tmp.delete() }
    }

    // ------------------------------------------------------------ verificar sin restaurar

    /** Lo que trae un respaldo ya descifrado: sus datos y cuántas ubicaciones, fotos con lugar y portadas lleva. */
    class Contenido(val r: Respaldo, val ubicaciones: Int, val fotos: Int, val portadas: Int)

    /**
     * Abre un respaldo (.zip o el JSON suelto) en memoria y lo lee completo, sin tocar nada de la app.
     * null si no es un respaldo de Vestigium Mei. Sirve para «Verificar respaldo» y para las pruebas automáticas.
     */
    fun contenido(b: ByteArray): Contenido? {
        val esZip = b.size > 4 && b[0] == 'P'.code.toByte() && b[1] == 'K'.code.toByte()
        if (!esZip) {
            val t = b.toString(Charsets.UTF_8)
            if (!t.contains("\"momentos\"") && !t.contains("\"respuestas\"")) return null
            return Contenido(JSON.decodeFromString(Respaldo.serializer(), t), 0, 0, 0)
        }
        var r: Respaldo? = null; var ub = 0; var fo = 0; var po = 0
        java.util.zip.ZipInputStream(b.inputStream()).use { z ->
            while (true) {
                val e = z.nextEntry ?: break
                if (e.isDirectory) continue
                val n = e.name.substringAfterLast('/')
                when {
                    n == "vm_respaldo.json" -> r = JSON.decodeFromString(Respaldo.serializer(), z.readBytes().toString(Charsets.UTF_8))
                    n == "vm_ubicaciones.csv" -> ub = (z.bufferedReader().lineSequence().count() - 1).coerceAtLeast(0)
                    n == "vm_fotos_catalogo.csv" -> fo = (z.bufferedReader().lineSequence().count() - 1).coerceAtLeast(0)
                    e.name.contains("portadas/") -> po++
                }
            }
        }
        return r?.let { Contenido(it, ub, fo, po) }
    }

    /** Las cifras de un respaldo, en una línea cada una: qué trae y desde cuándo. */
    fun informe(c: Contenido): List<String> {
        val r = c.r
        fun n(x: Int) = "%,d".format(x)
        val dias = r.dias.map { it.fecha }.sorted()
        return listOfNotNull(
            "Hecho el ${r.exportado.take(10)} a las ${r.exportado.drop(11).take(5)} · esquema ${r.schema_v}",
            "${n(r.dias.size)} días" + (if (dias.isNotEmpty()) " (del ${dias.first()} al ${dias.last()})" else "") + " · ${n(r.dias.count { it.nota.isNotBlank() })} con diario · ${n(r.dias.count { it.sueno_texto.isNotBlank() })} sueños",
            "${n(r.momentos.size)} momentos · ${n(r.tramos.size)} tramos de enfoque · ${n(r.consumos.size)} consumos",
            "${n(r.gastos.size)} gastos · ${n(r.ingresos.size)} ingresos · ${n(r.cartas.size)} cartas · ${n(r.obras.size)} obras · ${n(r.ideas.size)} ideas",
            "${n(r.salud.size)} días de salud · ${n(r.pantalla.size)} de pantalla · ${n(r.escuchas.size)} canciones · ${n(r.comidas.size)} comidas",
            "${n(r.lugares.size)} lugares · ${n(r.visitas.size)} visitas · ${n(c.ubicaciones)} ubicaciones · ${n(c.fotos)} fotos con lugar · ${n(c.portadas)} portadas",
            "Ajustes: ${r.config.habitos.size} hábitos, ${r.config.personas.size} personas en Tu gente, ${r.config.equivalencias.size} etiquetas unidas, tema «${r.config.tema}»",
        )
    }

    /** Abre el respaldo (descifrándolo si hace falta) y dice qué trae, comparado con lo que hay en este teléfono. No cambia nada. */
    fun verificar(ctx: Context, datos: ByteArray, contrasena: CharArray?): Lectura {
        var b = datos
        val cifrado = estaCifrado(b)
        if (cifrado) {
            val clave = contrasena ?: contrasena(ctx) ?: return Lectura.PideContrasena
            b = descifrar(b, clave) ?: return if (contrasena == null) Lectura.PideContrasena else Lectura.Error("La contraseña no abre este respaldo.")
        }
        val c = try { contenido(b) } catch (e: Exception) { return Lectura.Error("El archivo está dañado o incompleto: ${e.message}") }
            ?: return Lectura.Error("Ese archivo no es un respaldo de Vestigium Mei.")
        val aqui = "Respaldo / este teléfono: %,d / %,d días · %,d / %,d momentos · %,d / %,d gastos · %,d / %,d experimentos.".format(
            c.r.dias.size, Repo.dias.size, c.r.momentos.size, Repo.momentos.size, c.r.gastos.size, Repo.gastos.size, c.r.experimentos.size, Repo.experimentos.size)
        // «Se lee completo» es lo único que se comprobó: que no le falte nada depende de qué había cuando se hizo.
        return Lectura.Listo((if (cifrado) "Cifrado y abierto con la contraseña. " else "") + "El respaldo se lee completo.\n" +
            (Importar.esquemaFuturo(c.r.schema_v)?.let { "$it\n" } ?: "") + informe(c).joinToString("\n") { "· $it" } + "\n" + aqui)
    }

    fun verificarUri(ctx: Context, uri: Uri, contrasena: CharArray?): Lectura {
        val b = runCatching { ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() } }.getOrNull() ?: return Lectura.Error("No pude leer el archivo.")
        return verificar(ctx, b, contrasena)
    }

    private fun ZipFile.entradaQueTermina(n: String): ZipEntry? = entries().asSequence().firstOrNull { !it.isDirectory && (it.name == n || it.name.endsWith("/$n")) }

    private fun columnas(cab: String) = cab.removePrefix("﻿").split(',').withIndex().associate { it.value.trim() to it.index }

    /** Separa una línea CSV respetando comillas. */
    private fun partir(l: String): List<String> {
        if ('"' !in l) return l.split(',')
        val out = ArrayList<String>(); val sb = StringBuilder(); var q = false; var i = 0
        while (i < l.length) {
            val ch = l[i]
            when {
                q && ch == '"' && i + 1 < l.length && l[i + 1] == '"' -> { sb.append('"'); i++ }
                ch == '"' -> q = !q
                ch == ',' && !q -> { out.add(sb.toString()); sb.clear() }
                else -> sb.append(ch)
            }
            i++
        }
        out.add(sb.toString()); return out
    }

    /** Las ubicaciones del respaldo; las de fotos se rehacen desde el catálogo. Con la tabla vacía se meten todas sin revisar una por una. */
    private fun ubicaciones(lineas: List<String>): Int {
        if (lineas.size < 2) return 0
        val k = columnas(lineas[0])
        val iTs = k["ts"] ?: return 0; val iLat = k["lat"] ?: return 0; val iLon = k["lon"] ?: return 0
        val iPre = k["precision_m"]; val iFu = k["fuente"]; val iMs = k["ms"]
        val db = Repo.db()
        val vacia = db.contarUbicaciones() == 0L
        var n = 0
        db.transaccion {
            for (l in lineas.drop(1)) {
                if (l.isBlank()) continue
                val c = l.split(',')
                val fuente = iFu?.let { c.getOrNull(it) }.orEmpty()
                if (fuente.startsWith("foto")) continue
                val ms = iMs?.let { c.getOrNull(it)?.trim()?.toLongOrNull() } ?: runCatching { Fechas.millis(c[iTs]) }.getOrNull() ?: continue
                val lat = c.getOrNull(iLat)?.toDoubleOrNull() ?: continue; val lon = c.getOrNull(iLon)?.toDoubleOrNull() ?: continue
                if (!vacia && db.existeUbicacion(ms)) continue
                db.ponerUbicacion(ms, lat, lon, iPre?.let { c.getOrNull(it)?.toFloatOrNull() }, fuente.ifBlank { "respaldo" })
                n++
            }
        }
        return n
    }

    private fun catalogo(lineas: List<String>): Int {
        if (lineas.size < 2) return 0
        val k = columnas(lineas[0])
        val db = Repo.db()
        var n = 0
        db.transaccion {
            for (l in lineas.drop(1)) {
                if (l.isBlank()) continue
                val c = partir(l)
                fun t(col: String) = k[col]?.let { c.getOrNull(it) }.orEmpty()
                val ms = t("ms").toLongOrNull() ?: continue
                val lat = t("lat").toDoubleOrNull() ?: continue; val lon = t("lon").toDoubleOrNull() ?: continue
                db.ponerFoto(Db.FotoCat(t("clave").ifBlank { "${ms / 1000}_${Math.round(lat * 1e4)}_${Math.round(lon * 1e4)}" }, ms, lat, lon, t("aparato"), t("origen"),
                    t("ms_archivo").toLongOrNull() ?: 0L, t("fuente").ifBlank { "foto" }))
                n++
            }
        }
        return n
    }
}
