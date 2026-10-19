package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.documentfile.provider.DocumentFile
import java.io.File
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
    private val MAGIA = "VESTIGIUM-MEI-CIFRADO-1".toByteArray()
    private const val ITERACIONES = 210_000

    // ------------------------------------------------------------ cifrado

    private fun llave(contrasena: CharArray, sal: ByteArray): SecretKeySpec {
        val f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return SecretKeySpec(f.generateSecret(PBEKeySpec(contrasena, sal, ITERACIONES, 256)).encoded, "AES")
    }

    fun estaCifrado(b: ByteArray): Boolean = b.size > MAGIA.size + 28 && b.copyOfRange(0, MAGIA.size).contentEquals(MAGIA)

    fun cifrar(datos: ByteArray, contrasena: CharArray): ByteArray {
        val r = SecureRandom()
        val sal = ByteArray(16).also { r.nextBytes(it) }
        val iv = ByteArray(12).also { r.nextBytes(it) }
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, llave(contrasena, sal), GCMParameterSpec(128, iv))
        c.updateAAD(MAGIA)
        val cuerpo = c.doFinal(datos)
        val out = ByteArray(MAGIA.size + 28 + cuerpo.size)
        System.arraycopy(MAGIA, 0, out, 0, MAGIA.size)
        System.arraycopy(sal, 0, out, MAGIA.size, 16)
        System.arraycopy(iv, 0, out, MAGIA.size + 16, 12)
        System.arraycopy(cuerpo, 0, out, MAGIA.size + 28, cuerpo.size)
        return out
    }

    /** null si la contraseña no es la correcta (o el archivo está dañado). */
    fun descifrar(b: ByteArray, contrasena: CharArray): ByteArray? = runCatching {
        val o = MAGIA.size
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.DECRYPT_MODE, llave(contrasena, b.copyOfRange(o, o + 16)), GCMParameterSpec(128, b.copyOfRange(o + 16, o + 28)))
        c.updateAAD(MAGIA)
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

    /** Con contraseña: solo el .vmc cifrado (y se borran los CSV sueltos de antes). Sin contraseña: los CSV para R y el .zip completo. */
    fun aCarpeta(ctx: Context): Boolean {
        val uriTxt = Repo.config.carpeta_respaldo ?: return false
        val dir = DocumentFile.fromTreeUri(ctx, Uri.parse(uriTxt)) ?: return false
        if (!dir.canWrite()) return false
        val cifra = cifradoActivo(ctx)
        if (cifra) dir.listFiles().filter { f -> f.name?.let { esArchivoPlano(it) } == true }.forEach { runCatching { it.delete() } }
        else Exportar.archivos(ctx).forEach { (nombre, cont) ->
            // Para .R se usa un tipo genérico: así el sistema no le agrega «.txt» al nombre.
            val m = when { nombre.endsWith(".csv") -> "text/csv"; nombre.endsWith(".json") -> "application/json"; nombre.endsWith(".R") -> "application/octet-stream"; else -> "text/plain" }
            val f = dir.findFile(nombre) ?: dir.createFile(m, nombre) ?: return@forEach
            ctx.contentResolver.openOutputStream(f.uri, "wt")?.use { it.write(cont.toByteArray()) }
        }
        val a = archivo(ctx)
        val f = dir.findFile(a.name) ?: dir.createFile(mime(a.name), a.name) ?: return false
        ctx.contentResolver.openOutputStream(f.uri, "wt")?.use { o -> a.inputStream().use { it.copyTo(o) } }
        // Si cambió el modo, el archivo del otro tipo ya no sirve.
        dir.findFile(BASE + if (cifra) ".zip" else ".vmc")?.let { runCatching { it.delete() } }
        a.delete()
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
        if (!esZip) return Lectura.Listo(Importar.respaldoTexto(b.toString(Charsets.UTF_8)).mensaje)
        val tmp = File(ctx.cacheDir, "restaurar.zip")
        tmp.writeBytes(b)
        try {
            ZipFile(tmp).use { z ->
                // Primero el respaldo (trae los ajustes que deciden qué fotos cuentan), luego ubicaciones, catálogo y portadas.
                val json = z.entradaQueTermina("vm_respaldo.json") ?: return Lectura.Error("Ese .zip no trae vm_respaldo.json: no es un respaldo de Vestigium Mei.")
                val msg = z.getInputStream(json).use { Importar.respaldoTexto(it.readBytes().toString(Charsets.UTF_8)).mensaje }
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
