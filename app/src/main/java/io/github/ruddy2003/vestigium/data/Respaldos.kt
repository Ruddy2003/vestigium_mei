@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

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

    private const val TROZO = 64 * 1024

    /** Lee el principio de [f] (lo justo para la marca). */
    private fun inicio(f: File): ByteArray = f.inputStream().use { i ->
        val b = ByteArray(MAGIA_2.size); var n = 0
        while (n < b.size) { val r = i.read(b, n, b.size - n); if (r < 0) break; n += r }
        b.copyOf(n)
    }

    fun estaCifrado(f: File): Boolean = formato(inicio(f)).let { it != 0 && f.length() > cabecera(it) + 28 }

    /**
     * Como [cifrar], pero de archivo a archivo y por trozos: el respaldo nunca está entero en memoria tres veces (antes:
     * el zip leído, el cifrado y la copia con el encabezado). El resultado es el mismo formato 2.
     */
    fun cifrarArchivo(origen: File, destino: File, contrasena: CharArray) {
        val r = SecureRandom()
        val sal = ByteArray(16).also { r.nextBytes(it) }
        val iv = ByteArray(12).also { r.nextBytes(it) }
        val cab = MAGIA_2 + ByteBuffer.allocate(4).putInt(ITERACIONES).array()
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, llave(contrasena, sal, ITERACIONES), GCMParameterSpec(128, iv))
        c.updateAAD(cab)
        destino.outputStream().buffered(TROZO).use { o ->
            o.write(cab); o.write(sal); o.write(iv)
            val buf = ByteArray(TROZO)
            origen.inputStream().use { i -> while (true) { val n = i.read(buf); if (n < 0) break; c.update(buf, 0, n)?.let { o.write(it) } } }
            o.write(c.doFinal())
        }
    }

    /**
     * Como [descifrar], de archivo a archivo. false si la contraseña no lo abre o el archivo está dañado; entonces [destino]
     * no queda. (GCM solo entrega el contenido después de comprobar la etiqueta: nunca se escribe algo sin verificar.)
     */
    fun descifrarArchivo(origen: File, destino: File, contrasena: CharArray): Boolean {
        val ok = runCatching {
            java.io.DataInputStream(origen.inputStream().buffered(TROZO)).use { d ->
                val marca = ByteArray(MAGIA_2.size).also { d.readFully(it) }
                val v = formato(marca)
                if (v == 0) return@runCatching false
                val extra = ByteArray(cabecera(v) - marca.size).also { d.readFully(it) }
                val iteraciones = if (v == 2) ByteBuffer.wrap(extra).int else ITERACIONES_1
                if (iteraciones !in ITER_MIN..ITER_MAX) return@runCatching false
                val sal = ByteArray(16).also { d.readFully(it) }
                val iv = ByteArray(12).also { d.readFully(it) }
                val c = Cipher.getInstance("AES/GCM/NoPadding")
                c.init(Cipher.DECRYPT_MODE, llave(contrasena, sal, iteraciones), GCMParameterSpec(128, iv))
                c.updateAAD(marca + extra)
                destino.outputStream().buffered(TROZO).use { o ->
                    val buf = ByteArray(TROZO)
                    while (true) { val n = d.read(buf); if (n < 0) break; c.update(buf, 0, n)?.let { o.write(it) } }
                    o.write(c.doFinal())
                }
                true
            }
        }.getOrDefault(false)
        if (!ok) destino.delete()
        return ok
    }

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

    // ------------------------------------------------------------ manifiesto (9 nov 2026)

    const val MANIFIESTO = "manifiesto.json"
    const val EXTRA = "vm_extra.json"

    @Serializable class EntradaManifiesto(val sha256: String, val bytes: Long)

    /**
     * Lo que dice el respaldo de sí mismo: cuándo y con qué versión se hizo, cuántos registros trae de cada cosa y la huella
     * (SHA-256) y el tamaño de cada archivo que lleva. Al volver a abrirlo se recalcula todo y se compara: si un solo byte
     * cambió (un corte a la mitad, una tarjeta dañada), se nota.
     */
    @Serializable class Manifiesto(
        val hecho: String, val schema_v: Int, val app: String = "",
        val conteos: Map<String, Int> = emptyMap(), val archivos: Map<String, EntradaManifiesto> = emptyMap(),
    )

    /** Lo que no está en vm_respaldo.json pero tampoco se debe perder: la papelera, los ajustes ilegibles y los registros que no se pudieron leer. */
    @Serializable class Fila(val col: String, val id: String, val fecha: String? = null, val json: String)
    @Serializable class Extra(val filas: List<Fila> = emptyList())

    /** Cuántos registros trae un respaldo, por colección (lo que se compara contra el manifiesto). */
    fun conteoDe(r: Respaldo): Map<String, Int> = linkedMapOf(
        "dias" to r.dias.size, "momentos" to r.momentos.size, "gastos" to r.gastos.size, "ingresos" to r.ingresos.size,
        "respuestas" to r.respuestas.size, "cartas" to r.cartas.size, "experimentos" to r.experimentos.size, "notas" to r.notas.size,
        "metas" to r.metas.size, "ideas" to r.ideas.size, "listas" to r.listas.size, "obras" to r.obras.size, "tramos" to r.tramos.size,
        "consumos" to r.consumos.size, "tomas" to r.tomas.size, "lugares" to r.lugares.size, "visitas" to r.visitas.size,
        "salud" to r.salud.size, "pantalla" to r.pantalla.size, "escuchas" to r.escuchas.size, "comidas" to r.comidas.size,
        "series" to r.series.size, "ascensos" to r.ascensos.size, "avisos" to r.avisos.size,
    )

    private fun hex(b: ByteArray) = b.joinToString("") { "%02x".format(it) }

    /** Huella SHA-256 y largo de todo lo que sale de [i]. */
    fun huella(i: java.io.InputStream): Pair<String, Long> {
        val md = java.security.MessageDigest.getInstance("SHA-256"); val buf = ByteArray(TROZO); var n = 0L
        while (true) { val k = i.read(buf); if (k < 0) break; md.update(buf, 0, k); n += k }
        return hex(md.digest()) to n
    }

    fun huella(f: File): String = f.inputStream().buffered(TROZO).use { huella(it).first }

    /** Cuenta y calcula la huella de lo que pasa hacia [o], sin cerrarlo nunca (el zip sigue abierto para la siguiente entrada). */
    private class Contador(private val o: java.io.OutputStream) : java.io.OutputStream() {
        val md: java.security.MessageDigest = java.security.MessageDigest.getInstance("SHA-256")
        var n = 0L
        override fun write(b: Int) { o.write(b); md.update(b.toByte()); n++ }
        override fun write(b: ByteArray, off: Int, len: Int) { o.write(b, off, len); md.update(b, off, len); n += len }
        override fun flush() = o.flush()
        override fun close() { flush() }
    }

    /** Las filas de [Extra] que hay en este teléfono. */
    private fun extra(): Extra {
        val db = Repo.db(); val l = ArrayList<Fila>()
        listOf("papelera", "config_ilegible").forEach { col -> db.filas(col) { id, fecha, json -> l += Fila(col, id, fecha, json) } }
        // Los que no se pudieron leer al abrir la app (con su texto tal cual) y los que llegaron así de un respaldo anterior.
        Repo.crudosIlegibles().forEach { (col, textos) -> textos.forEach { t -> l += Fila("ilegible_$col", hex(java.security.MessageDigest.getInstance("SHA-1").digest(t.toByteArray())), null, t) } }
        db.colecciones().filter { it.startsWith("ilegible_") }.forEach { col -> db.filas(col) { id, fecha, json -> l += Fila(col, id, fecha, json) } }
        return Extra(l.distinctBy { it.col + "|" + it.id })
    }

    /** Devuelve a la base lo de [Extra] que no esté. Cuántas filas entraron. */
    private fun aplicarExtra(e: Extra): Int {
        val db = Repo.db(); var n = 0
        db.transaccion { e.filas.forEach { f -> if (db.uno(f.col, f.id) == null) { db.poner(f.col, f.id, f.fecha, f.json); n++ } } }
        return n
    }

    private fun version(ctx: Context): String = runCatching {
        val p = ctx.packageManager.getPackageInfo(ctx.packageName, 0); "${p.versionName} (${p.longVersionCode})"
    }.getOrDefault("")

    /**
     * Lo indispensable para rehacer la app en otro teléfono: el respaldo, ubicaciones, catálogo de fotos, portadas, lo de
     * [Extra] y, al final, el manifiesto con la huella de cada archivo.
     */
    private fun escribirZip(ctx: Context, destino: File, conTablas: Boolean, r: Respaldo): Manifiesto {
        val huellas = LinkedHashMap<String, EntradaManifiesto>()
        // Cada entrada se escribe directo al zip (antes se armaba todo como texto primero: con años de ubicaciones no cabía).
        return ZipOutputStream(destino.outputStream().buffered(TROZO)).use { z ->
            fun poner(nombre: String, escribir: (java.io.OutputStream) -> Unit) {
                z.putNextEntry(ZipEntry(nombre))
                val c = Contador(z); escribir(c); c.flush()
                z.closeEntry()
                huellas[nombre] = EntradaManifiesto(hex(c.md.digest()), c.n)
            }
            poner("vm_respaldo.json") { o -> JSON.encodeToStream(Respaldo.serializer(), r, o) }
            poner("vm_ubicaciones.csv") { o -> Exportar.texto(o) { Exportar.escribirUbicaciones(it) } }
            poner("vm_fotos_catalogo.csv") { o -> Exportar.texto(o) { Exportar.escribirCatalogoFotos(it) } }
            poner(EXTRA) { o -> JSON.encodeToStream(Extra.serializer(), extra(), o) }
            if (conTablas) Exportar.archivos(ctx).filter { it.nombre !in setOf("vm_respaldo.json", "vm_ubicaciones.csv", "vm_fotos_catalogo.csv") }.forEach { a -> poner(a.nombre, a.escribir) }
            else poner("LEEME.txt") { it.write("Respaldo completo de Vestigium Mei (${Fechas.ahoraIso()}).\nPara restaurarlo: Más → Datos → Respaldo completo → Restaurar desde un archivo.\nmanifiesto.json dice qué trae y la huella de cada archivo.\n".toByteArray()) }
            Portadas.carpeta(ctx).listFiles()?.forEach { p -> poner("portadas/" + p.name) { o -> p.inputStream().use { it.copyTo(o) } } }
            FotosDia.carpeta(ctx).listFiles()?.forEach { p -> poner("fotos_dia/" + p.name) { o -> p.inputStream().use { it.copyTo(o) } } }
            val m = Manifiesto(Fechas.ahoraIso(), SCHEMA_V, version(ctx), conteoDe(r), huellas)
            z.putNextEntry(ZipEntry(MANIFIESTO)); z.write(JSON.encodeToString(Manifiesto.serializer(), m).toByteArray()); z.closeEntry()
            m
        }
    }

    /**
     * Vuelve a abrir un respaldo (.zip ya descifrado) y lo compara con su manifiesto: la huella y el tamaño de cada archivo,
     * y que vm_respaldo.json se lea completo con las mismas cifras. null si todo coincide; si no, qué falló. Un respaldo de
     * antes del 9 nov 2026 no trae manifiesto: con [estricto] eso cuenta como falla.
     */
    fun verificarZip(f: File, estricto: Boolean = true): String? = try {
        ZipFile(f).use { z ->
            val me = z.getEntry(MANIFIESTO) ?: return if (estricto) "no trae manifiesto" else null
            val m = z.getInputStream(me).use { JSON.decodeFromStream(Manifiesto.serializer(), it) }
            if (m.archivos.isEmpty()) return "el manifiesto está vacío"
            for ((n, e) in m.archivos) {
                val x = z.getEntry(n) ?: return "le falta $n"
                val (h, b) = z.getInputStream(x).use { huella(it) }
                if (h != e.sha256 || b != e.bytes) return "$n no coincide con su huella"
            }
            val rj = z.getEntry("vm_respaldo.json") ?: return "le falta vm_respaldo.json"
            // Leerlo completo pide memoria: si no alcanza, basta con que la huella coincida (los bytes son los que se escribieron).
            val r = try { z.getInputStream(rj).buffered(TROZO).use { JSON.decodeFromStream(Respaldo.serializer(), it) } } catch (e: OutOfMemoryError) { null }
            if (r != null) {
                val c = conteoDe(r)
                val distintas = m.conteos.filter { (k, v) -> c[k] != v }.keys
                if (distintas.isNotEmpty()) return "las cifras no coinciden (" + distintas.joinToString(", ") + ")"
            }
            null
        }
    } catch (e: Exception) { "no se pudo leer: ${e.message ?: e.javaClass.simpleName}" }

    /** Un respaldo listo para salir del teléfono, ya revisado, con lo que trae. */
    class Hecho(val archivo: File, val conteo: Map<String, Int>, val detalle: String)

    /**
     * El respaldo completo listo para guardar: .zip, o .vmc cifrado si pusiste contraseña. Antes de entregarlo se vuelve a
     * abrir y se compara con su manifiesto; el cifrado se descifra otra vez y debe dar el mismo zip byte por byte. Si algo no
     * coincide, lanza una excepción y no sale nada. [fecha] lo nombra; sin fecha, el nombre fijo.
     */
    fun armar(ctx: Context, fecha: String? = null, conTablas: Boolean = false): Hecho {
        val dir = File(ctx.cacheDir, "respaldo").apply { mkdirs() }
        val nombre = BASE + (fecha?.let { "_$it" } ?: "")
        val zip = File(dir, "$nombre.zip")
        val m = escribirZip(ctx, zip, conTablas, Exportar.respaldo())
        verificarZip(zip)?.let { zip.delete(); error("el respaldo recién hecho no pasó la revisión: $it") }
        val conteo = mapOf("dias" to (m.conteos["dias"] ?: 0), "momentos" to (m.conteos["momentos"] ?: 0))
        fun detalle(f: File) = porNumero(conteo["dias"] ?: 0, "1 día", "%,d días".format(conteo["dias"] ?: 0)) + " · " +
            porNumero(conteo["momentos"] ?: 0, "1 momento", "%,d momentos".format(conteo["momentos"] ?: 0)) + " · " + "%.1f MB".format(f.length() / 1_048_576.0)
        val clave = contrasena(ctx) ?: return Hecho(zip, conteo, detalle(zip))
        val vmc = File(dir, "$nombre.vmc")
        val prueba = File(dir, "$nombre.prueba")
        try {
            cifrarArchivo(zip, vmc, clave)
            if (!descifrarArchivo(vmc, prueba, clave) || huella(prueba) != huella(zip)) { vmc.delete(); error("el respaldo cifrado no se pudo volver a abrir igual") }
        } finally { clave.fill(' '); zip.delete(); prueba.delete() }
        return Hecho(vmc, conteo, detalle(vmc))
    }

    /** Como [armar], solo el archivo. */
    fun archivo(ctx: Context, fecha: String? = null, conTablas: Boolean = false): File = armar(ctx, fecha, conTablas).archivo

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

    // ------------------------------------------------------------ estado (9 nov 2026)

    enum class Luz { VERDE, AMARILLO, ROJO }

    /** Verde: la última copia buena tiene 36 h o menos; amarillo, hasta 48 h; rojo, más (o nunca). */
    fun luz(ultimo: String?, ahora: Long = System.currentTimeMillis()): Luz {
        val ms = ultimo?.let { Fechas.millis(it) }?.takeIf { it > 0 } ?: return Luz.ROJO
        val h = (ahora - ms) / 3_600_000.0
        return when { h <= 36 -> Luz.VERDE; h <= 48 -> Luz.AMARILLO; else -> Luz.ROJO }
    }

    /** La copia buena más reciente fuera del teléfono (la carpeta revisada o Drive). */
    fun ultimaFuera(c: Config = Repo.config): String? =
        // Las copias de antes del 9 nov 2026 no se revisaban: cuentan hasta que salga la primera revisada.
        listOfNotNull((c.respaldo_verificado ?: c.respaldo_ultimo)?.takeIf { c.carpeta_respaldo != null }, c.drive_ultimo?.takeIf { c.drive }).maxOrNull()

    /** Nombre de la copia que se guarda aparte cuando el respaldo nuevo trae mucho menos que el anterior. */
    const val REVISAR = "vestigium_revisar"
    const val EMPOBRECIDO = "El respaldo de hoy trae muchos menos días o momentos que el anterior. No reemplacé tus copias: lo guardé aparte como «vestigium_revisar». Si borraste a propósito, toca «Respaldar ahora» en Respaldo para aceptarlo."

    /** La subcarpeta con las copias fechadas. */
    const val COPIAS = "copias"
    const val COPIAS_DIA = 7
    const val COPIAS_SEMANA = 8
    const val COPIAS_MES = 12
    const val COPIAS_BD = 4

    /** De los [nombres] que siguen [patron], los que sobran si se conservan los [conservar] más recientes (el nombre lleva la fecha). */
    fun sobran(nombres: Collection<String>, patron: Regex, conservar: Int): List<String> =
        nombres.filter { patron.matches(it) }.distinct().sortedDescending().drop(conservar)

    /** Lo que hay en [f] de la carpeta, con su huella; null si no se pudo leer. */
    private fun huellaDe(ctx: Context, f: DocumentFile?): String? = f?.let { d ->
        runCatching { ctx.contentResolver.openInputStream(d.uri)?.buffered(TROZO)?.use { huella(it).first } }.getOrNull()
    }

    /**
     * Respaldo automático en la carpeta que elegiste. Deja:
     * - vestigium_respaldo.zip (o .vmc cifrado): el último, que se reemplaza solo cuando el nuevo ya pasó su revisión y se
     *   volvió a leer igual desde la carpeta;
     * - en «copias»: uno por día (los últimos 7), uno por semana (8), uno por mes (12) y, cada semana, la base de datos tal
     *   cual (las últimas 4). Antes había un solo archivo y un día malo pisaba la única copia buena.
     * Si el nuevo trae mucho menos que el anterior, se guarda aparte («vestigium_revisar») y no se toca nada; [forzar] lo acepta.
     * Sin contraseña también deja los CSV para R; con contraseña, solo lo cifrado.
     */
    fun aCarpeta(ctx: Context, forzar: Boolean = false): Boolean {
        fun fallo(m: String): Boolean { Repo.cambiarConfig { it.copy(respaldo_error = m) }; return false }
        if (bloqueado(ctx)) return fallo(PAUSA)
        val uriTxt = Repo.config.carpeta_respaldo ?: return false
        val dir = DocumentFile.fromTreeUri(ctx, Uri.parse(uriTxt)) ?: return fallo("No encuentro la carpeta de respaldos: vuelve a elegirla en Respaldo.")
        if (!dir.canWrite()) return fallo("La carpeta de respaldos ya no deja escribir: vuelve a elegirla en Respaldo.")
        val cifra = cifradoActivo(ctx)
        val ext = if (cifra) ".vmc" else ".zip"
        val hecho = try { armar(ctx) } catch (e: OutOfMemoryError) { return fallo("El respaldo no cupo en la memoria del teléfono.") }
            catch (e: Exception) { return fallo("No pude armar el respaldo: ${e.message ?: e.javaClass.simpleName}") }
        val a = hecho.archivo
        try {
            if (!forzar && empobrecido(Repo.config.respaldo_conteo, hecho.conteo)) {
                escribirSinRomper(ctx, dir, a, REVISAR + ext)
                runCatching { io.github.ruddy2003.vestigium.avisos.Avisos.respaldoEmpobrecido(ctx) }
                return fallo(EMPOBRECIDO)
            }
            if (!cifra) Exportar.archivos(ctx).forEach { x ->
                val nombre = x.nombre
                // Para .R se usa un tipo genérico: así el sistema no le agrega «.txt» al nombre.
                val m = when { nombre.endsWith(".csv") -> "text/csv"; nombre.endsWith(".json") -> "application/json"; nombre.endsWith(".R") -> "application/octet-stream"; else -> "text/plain" }
                val f = dir.findFile(nombre) ?: dir.createFile(m, nombre) ?: return@forEach
                ctx.contentResolver.openOutputStream(f.uri, "wt")?.buffered(TROZO)?.use { x.escribir(it) }
            }
            if (!escribirSinRomper(ctx, dir, a, BASE + ext)) return fallo("No pude escribir el respaldo en la carpeta. ¿Falta espacio?")
            val propia = huella(a)
            if (huellaDe(ctx, dir.findFile(BASE + ext)) != propia) return fallo("El respaldo de la carpeta no se lee igual que el que escribí. ¿Falta espacio?")
            val ok = runCatching { copias(ctx, dir, a, ext, cifra) }.getOrDefault(false)
            // Los archivos en claro de antes se borran solo cuando ya quedó la copia cifrada.
            if (cifra) dir.listFiles().filter { x -> x.name?.let { esArchivoPlano(it) } == true }.forEach { runCatching { it.delete() } }
            // Si cambió el modo, el archivo del otro tipo ya no sirve; el «revisar» de antes, tampoco.
            dir.findFile(BASE + if (cifra) ".zip" else ".vmc")?.let { runCatching { it.delete() } }
            listOf(".zip", ".vmc").forEach { e -> dir.findFile(REVISAR + e)?.let { runCatching { it.delete() } } }
            val ahora = Fechas.ahoraIso()
            Repo.cambiarConfig {
                it.copy(respaldo_ultimo = ahora, respaldo_verificado = ahora, respaldo_conteo = hecho.conteo, respaldo_detalle = hecho.detalle,
                    respaldo_error = if (ok) null else "El último quedó bien, pero no pude guardar las copias con fecha en «$COPIAS».")
            }
            return true
        } finally { a.delete() }
    }

    /** Las copias con fecha en «copias» y su limpieza. true si quedaron. */
    private fun copias(ctx: Context, dir: DocumentFile, a: File, ext: String, cifra: Boolean): Boolean {
        val c = dir.findFile(COPIAS)?.takeIf { it.isDirectory } ?: dir.createDirectory(COPIAS) ?: return false
        val hoy = Fechas.hoyCalendario()
        val lunes = Fechas.lunesDe(hoy)
        val nombres = c.listFiles().mapNotNull { it.name }.toMutableSet()
        var ok = escribirSinRomper(ctx, c, a, "vestigium_dia_$hoy$ext")
        if ("vestigium_semana_$lunes$ext" !in nombres) ok = escribirSinRomper(ctx, c, a, "vestigium_semana_$lunes$ext") && ok
        if ("vestigium_mes_${hoy.take(7)}$ext" !in nombres) ok = escribirSinRomper(ctx, c, a, "vestigium_mes_${hoy.take(7)}$ext") && ok
        // La base de datos tal cual, una vez por semana: la red por si algún día el JSON del respaldo no se pudiera leer.
        val bdNombre = "vestigium_bd_$lunes.sqlite" + if (cifra) ".vmc" else ""
        if (bdNombre !in nombres) runCatching {
            val tmp = File(ctx.cacheDir, "respaldo/bd.sqlite"); val tmpC = File(ctx.cacheDir, "respaldo/bd.sqlite.vmc")
            try {
                Repo.db().copiaCruda(tmp)
                val sale = if (cifra) { val k = contrasena(ctx) ?: error("sin contraseña"); try { cifrarArchivo(tmp, tmpC, k) } finally { k.fill(' ') }; tmpC } else tmp
                escribirSinRomper(ctx, c, sale, bdNombre)
            } finally { tmp.delete(); tmpC.delete() }
        }
        val ahora = c.listFiles().mapNotNull { it.name }
        val e = Regex.escape(ext)
        val sobra = sobran(ahora, Regex("vestigium_dia_\\d{4}-\\d{2}-\\d{2}$e"), COPIAS_DIA) +
            sobran(ahora, Regex("vestigium_semana_\\d{4}-\\d{2}-\\d{2}$e"), COPIAS_SEMANA) +
            sobran(ahora, Regex("vestigium_mes_\\d{4}-\\d{2}$e"), COPIAS_MES) +
            sobran(ahora, Regex("vestigium_bd_\\d{4}-\\d{2}-\\d{2}\\.sqlite(\\.vmc)?"), COPIAS_BD) +
            // Con contraseña, ninguna copia en claro se queda (podría llevar lo que antes no era privado y ahora sí).
            (if (cifra) ahora.filter { it.startsWith("vestigium_") && !it.endsWith(".vmc") } else emptyList())
        sobra.distinct().forEach { n -> c.findFile(n)?.let { runCatching { it.delete() } } }
        return ok
    }

    /**
     * El ensayo del mes: abre la última copia de la carpeta como si fuera a restaurarla (descifrándola con la contraseña
     * guardada), la compara con su manifiesto y con lo que hay en el teléfono, y anota el resultado. No cambia nada.
     */
    fun ensayo(ctx: Context): String {
        val dir = Repo.config.carpeta_respaldo?.let { DocumentFile.fromTreeUri(ctx, Uri.parse(it)) }
        val f = dir?.let { d -> d.findFile("$BASE.vmc") ?: d.findFile("$BASE.zip") }
        val resultado = if (f == null) "No encontré el respaldo en la carpeta." else when (val r = verificarUri(ctx, f.uri, null)) {
            is Lectura.Listo -> "Bien: " + r.mensaje.lines().first { it.isNotBlank() } + " " + r.mensaje.lines().last()
            is Lectura.Error -> "Falló: " + r.mensaje
            Lectura.PideContrasena -> "Falló: la contraseña guardada no abre el respaldo de la carpeta."
        }
        Repo.cambiarConfig { it.copy(respaldo_ensayo = Fechas.ahoraIso(), respaldo_ensayo_resultado = resultado) }
        return resultado
    }

    /**
     * Escribe [a] en [dir] con el nombre [nombre] sin dejar nunca el respaldo a medias: primero a un archivo aparte
     * («….nuevo») y, solo cuando está completo, toma el nombre; el anterior se borra al final. Antes se escribía encima del
     * único respaldo, y si la tarea se cortaba (o el teléfono se apagaba) quedaba truncado. Si la carpeta no deja renombrar,
     * se escribe encima como antes.
     */
    private fun escribirSinRomper(ctx: Context, dir: DocumentFile, a: java.io.File, nombre: String = a.name): Boolean {
        fun copiar(f: DocumentFile): Boolean =
            ctx.contentResolver.openOutputStream(f.uri, "wt")?.use { o -> a.inputStream().use { it.copyTo(o, TROZO) }; true } ?: false
        val nuevoNombre = "$nombre.nuevo"; val viejoNombre = "$nombre.anterior"
        dir.findFile(nuevoNombre)?.delete()
        val nuevo = dir.createFile("application/octet-stream", nuevoNombre)
        if (nuevo == null || nuevo.name != nuevoNombre) {
            nuevo?.delete()
            val f = dir.findFile(nombre) ?: dir.createFile(mime(nombre), nombre) ?: return false
            return copiar(f)
        }
        if (!runCatching { copiar(nuevo) }.getOrDefault(false)) { nuevo.delete(); return false }
        val viejo = dir.findFile(nombre)
        dir.findFile(viejoNombre)?.delete()
        if (viejo != null && !viejo.renameTo(viejoNombre)) {
            // No deja renombrar: se escribe encima, como antes (el «.nuevo» ya no sirve).
            nuevo.delete()
            return copiar(viejo)
        }
        if (!nuevo.renameTo(nombre)) {
            // El nuevo no tomó el nombre: el anterior vuelve a su lugar.
            dir.findFile(viejoNombre)?.renameTo(nombre)
            nuevo.delete()
            return false
        }
        dir.findFile(viejoNombre)?.delete()
        return true
    }

    // ------------------------------------------------------------ restaurar

    sealed class Lectura {
        data class Listo(val mensaje: String) : Lectura()
        data class Error(val mensaje: String) : Lectura()
        object PideContrasena : Lectura()
    }

    /** Copia lo que hay en [uri] a un archivo temporal, por trozos (un respaldo grande no se lee entero a memoria). */
    private fun copiarUri(ctx: Context, uri: Uri, f: File): Boolean {
        val ok = runCatching { ctx.contentResolver.openInputStream(uri)?.use { i -> f.outputStream().buffered(TROZO).use { i.copyTo(it, TROZO) }; true } }.getOrNull() == true
        if (!ok) f.delete()
        return ok
    }

    private fun esZip(f: File) = inicio(f).let { it.size > 2 && it[0] == 'P'.code.toByte() && it[1] == 'K'.code.toByte() }

    /**
     * [f] listo para leer: el mismo archivo si no está cifrado, o su versión descifrada en [claro]. Si no se puede abrir,
     * null y el porqué (pedir la contraseña o que no es la correcta).
     */
    private fun abrir(ctx: Context, f: File, claro: File, contrasena: CharArray?): Pair<File?, Lectura?> {
        if (!estaCifrado(f)) return f to null
        val clave = contrasena ?: contrasena(ctx) ?: return null to Lectura.PideContrasena
        if (!descifrarArchivo(f, claro, clave)) return null to (if (contrasena == null) Lectura.PideContrasena else Lectura.Error("La contraseña no abre este respaldo."))
        return claro to null
    }

    fun restaurarUri(ctx: Context, uri: Uri, contrasena: CharArray?): Lectura {
        val f = File(ctx.cacheDir, "restaurar_entrada")
        if (!copiarUri(ctx, uri, f)) return Lectura.Error("No pude leer el archivo.")
        return try { restaurarArchivo(ctx, f, contrasena) } finally { f.delete() }
    }

    /** Restaura un respaldo: .vmc cifrado, .zip (completo o la exportación extendida) o el JSON suelto. Lo que ya existe no se duplica. */
    fun restaurar(ctx: Context, datos: ByteArray, contrasena: CharArray?): Lectura {
        val f = File(ctx.cacheDir, "restaurar_entrada")
        f.writeBytes(datos)
        return try { restaurarArchivo(ctx, f, contrasena) } finally { f.delete() }
    }

    /**
     * Restaura desde un archivo, sin cargarlo entero en memoria: se descifra a otro archivo, el zip se lee entrada por
     * entrada, el respaldo se decodifica directo del archivo y las ubicaciones, línea por línea. Antes, entre los bytes del
     * archivo, los descifrados, el texto del JSON y su árbol, un respaldo de varios años no cabía en un teléfono nuevo.
     */
    private fun restaurarArchivo(ctx: Context, entrada: File, contrasena: CharArray?): Lectura {
        val claro = File(ctx.cacheDir, "restaurar_claro")
        val json = File(ctx.cacheDir, "restaurar_respaldo.json")
        try {
            val (f, problema) = abrir(ctx, entrada, claro, contrasena)
            if (f == null) return problema ?: Lectura.Error("No pude abrir el respaldo.")
            if (!esZip(f)) {
                val l = Importar.leer(f)
                if (l is Importar.Leido.Malo) return Lectura.Error(l.aviso)
                return Lectura.Listo(Importar.aplicar(l).mensaje)
            }
            ZipFile(f).use { z ->
                // Primero el respaldo (trae los ajustes que deciden qué fotos cuentan), luego ubicaciones, catálogo y portadas.
                val e = z.entradaQueTermina("vm_respaldo.json") ?: return Lectura.Error("Ese .zip no trae vm_respaldo.json: no es un respaldo de Vestigium Mei.")
                z.getInputStream(e).use { i -> json.outputStream().buffered(TROZO).use { i.copyTo(it, TROZO) } }
                val l = Importar.leer(json)
                json.delete()
                // De una versión más nueva (o ilegible): no se restaura nada (ni ubicaciones ni portadas), para no dejarlo a medias.
                if (l is Importar.Leido.Malo) return Lectura.Error(l.aviso)
                val msg = Importar.aplicar(l).mensaje
                val nUb = z.entradaQueTermina("vm_ubicaciones.csv")?.let { x -> z.getInputStream(x).bufferedReader().use { ubicaciones(it.lineSequence()) } } ?: 0
                val nFo = z.entradaQueTermina("vm_fotos_catalogo.csv")?.let { x -> z.getInputStream(x).bufferedReader().use { catalogo(it.lineSequence()) } } ?: 0
                if (nFo > 0) runCatching { io.github.ruddy2003.vestigium.sensores.FotosLugar.aplicar(ctx) }
                var nPo = 0
                val portadas = Portadas.carpeta(ctx)
                z.entries().asSequence().filter { !it.isDirectory && it.name.contains("portadas/") }.forEach { x ->
                    val p = File(portadas, x.name.substringAfterLast('/'))
                    if (!p.exists()) { z.getInputStream(x).use { i -> p.outputStream().use { i.copyTo(it) } }; nPo++ }
                }
                var nFd = 0
                val fotosDia = FotosDia.carpeta(ctx)
                z.entries().asSequence().filter { !it.isDirectory && it.name.contains("fotos_dia/") }.forEach { x ->
                    val p = File(fotosDia, x.name.substringAfterLast('/'))
                    if (!p.exists()) { z.getInputStream(x).use { i -> p.outputStream().use { i.copyTo(it) } }; nFd++ }
                }
                // La papelera, los ajustes ilegibles y lo que no se pudo leer (esquema 22).
                val nEx = z.entradaQueTermina(EXTRA)?.let { x -> runCatching { z.getInputStream(x).use { aplicarExtra(JSON.decodeFromStream(Extra.serializer(), it)) } }.getOrDefault(0) } ?: 0
                return Lectura.Listo(msg + " Ubicaciones nuevas: %,d. Fotos con lugar: %,d. Portadas: %d.".format(nUb, nFo, nPo) +
                    (if (nFd > 0) " Fotos del día: %,d.".format(nFd) else "") + (if (nEx > 0) " En la papelera y aparte: %,d.".format(nEx) else ""))
            }
        } catch (e: Exception) {
            return Lectura.Error("No pude abrir el respaldo: ${e.message}")
        } catch (e: OutOfMemoryError) {
            return Lectura.Error("El respaldo no cupo en la memoria del teléfono. Cierra otras apps y vuelve a intentarlo.")
        } finally { claro.delete(); json.delete() }
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
        return if (esZip) contenidoZip(b.inputStream()) else contenidoJson(b.inputStream())
    }

    /** Lo mismo, leyendo el archivo por partes. */
    fun contenido(f: File): Contenido? = if (esZip(f)) f.inputStream().buffered(TROZO).use { contenidoZip(it) } else f.inputStream().buffered(TROZO).use { contenidoJson(it) }

    private fun contenidoJson(i: java.io.InputStream): Contenido? {
        // La versión web (u otro JSON) no es un respaldo de esta app: no trae ni la hora ni el esquema.
        val r = try { JSON.decodeFromStream(Respaldo.serializer(), i) } catch (e: kotlinx.serialization.MissingFieldException) { return null }
        return if (r.exportado.isEmpty()) null else Contenido(r, 0, 0, 0)
    }

    private fun contenidoZip(i: java.io.InputStream): Contenido? {
        var r: Respaldo? = null; var ub = 0; var fo = 0; var po = 0
        java.util.zip.ZipInputStream(i).use { z ->
            while (true) {
                val e = z.nextEntry ?: break
                if (e.isDirectory) continue
                val n = e.name.substringAfterLast('/')
                when {
                    n == "vm_respaldo.json" -> r = JSON.decodeFromStream(Respaldo.serializer(), SoloEntrada(z))
                    n == "vm_ubicaciones.csv" -> ub = (z.bufferedReader().lineSequence().count() - 1).coerceAtLeast(0)
                    n == "vm_fotos_catalogo.csv" -> fo = (z.bufferedReader().lineSequence().count() - 1).coerceAtLeast(0)
                    e.name.contains("portadas/") -> po++
                }
            }
        }
        return r?.let { Contenido(it, ub, fo, po) }
    }

    /** La entrada actual de un zip, sin que leerla hasta el final ni cerrarla cierre el zip entero. */
    private class SoloEntrada(private val z: java.util.zip.ZipInputStream) : java.io.InputStream() {
        override fun read(): Int = z.read()
        override fun read(b: ByteArray, off: Int, len: Int): Int = z.read(b, off, len)
        override fun close() {}
    }

    /** Las cifras de un respaldo, en una línea cada una: qué trae y desde cuándo. */
    fun informe(c: Contenido): List<String> {
        val r = c.r
        fun n(x: Int) = "%,d".format(x)
        val dias = r.dias.map { it.fecha }.sorted()
        return listOfNotNull(
            "Hecho el ${r.exportado.take(10)} a las ${r.exportado.drop(11).take(5)} · esquema ${r.schema_v}",
            "${porNumero(r.dias.size, "1 día", "${n(r.dias.size)} días")}" + (if (dias.isNotEmpty()) " (del ${dias.first()} al ${dias.last()})" else "") + " · ${n(r.dias.count { it.nota.isNotBlank() })} con diario · ${n(r.dias.count { it.sueno_texto.isNotBlank() })} sueños",
            "${porNumero(r.momentos.size, "1 momento", "${n(r.momentos.size)} momentos")} · ${n(r.tramos.size)} tramos de enfoque · ${n(r.consumos.size)} consumos",
            "${porNumero(r.gastos.size, "1 gasto", "${n(r.gastos.size)} gastos")} · ${n(r.ingresos.size)} ingresos · ${porNumero(r.cartas.size, "1 carta", "${n(r.cartas.size)} cartas")} · ${porNumero(r.obras.size, "1 obra", "${n(r.obras.size)} obras")} · ${porNumero(r.ideas.size, "1 idea", "${n(r.ideas.size)} ideas")} · ${porNumero(r.listas.size, "1 lista", "${n(r.listas.size)} listas")}",
            "${porNumero(r.salud.size, "1 día", "${n(r.salud.size)} días")} de salud · ${n(r.pantalla.size)} de pantalla · ${porNumero(r.escuchas.size, "1 canción", "${n(r.escuchas.size)} canciones")} · ${porNumero(r.comidas.size, "1 comida", "${n(r.comidas.size)} comidas")}",
            "${porNumero(r.lugares.size, "1 lugar", "${n(r.lugares.size)} lugares")} · ${porNumero(r.visitas.size, "1 visita", "${n(r.visitas.size)} visitas")} · ${n(c.ubicaciones)} ubicaciones · ${porNumero(c.fotos, "1 foto", "${n(c.fotos)} fotos")} con lugar · ${n(c.portadas)} portadas",
            "Ajustes: ${porNumero(r.config.habitos.size, "1 hábito", "${r.config.habitos.size} hábitos")}, ${porNumero(r.config.personas.size, "1 persona", "${r.config.personas.size} personas")} en Tu gente, ${porNumero(r.config.equivalencias.size, "1 etiqueta", "${r.config.equivalencias.size} etiquetas")} unidas, tema «${r.config.tema}»",
        )
    }

    /** Abre el respaldo (descifrándolo si hace falta) y dice qué trae, comparado con lo que hay en este teléfono. No cambia nada. */
    fun verificar(ctx: Context, datos: ByteArray, contrasena: CharArray?): Lectura {
        val f = File(ctx.cacheDir, "verificar_entrada")
        f.writeBytes(datos)
        return try { verificarArchivo(ctx, f, contrasena) } finally { f.delete() }
    }

    fun verificarUri(ctx: Context, uri: Uri, contrasena: CharArray?): Lectura {
        val f = File(ctx.cacheDir, "verificar_entrada")
        if (!copiarUri(ctx, uri, f)) return Lectura.Error("No pude leer el archivo.")
        return try { verificarArchivo(ctx, f, contrasena) } finally { f.delete() }
    }

    private fun verificarArchivo(ctx: Context, entrada: File, contrasena: CharArray?): Lectura {
        val claro = File(ctx.cacheDir, "verificar_claro")
        try {
            val cifrado = estaCifrado(entrada)
            val (f, problema) = abrir(ctx, entrada, claro, contrasena)
            if (f == null) return problema ?: Lectura.Error("No pude abrir el respaldo.")
            val c = try { contenido(f) } catch (e: Exception) { return Lectura.Error("El archivo está dañado o incompleto: ${e.message}") }
                catch (e: OutOfMemoryError) { return Lectura.Error("El respaldo no cupo en la memoria del teléfono para revisarlo.") }
                ?: return Lectura.Error("Ese archivo no es un respaldo de Vestigium Mei.")
            // Con manifiesto (desde el 9 nov 2026), cada archivo se compara con su huella.
            val manifiesto = if (esZip(f)) verificarZip(f, estricto = false).let { p -> if (p == null) (if (ZipFile(f).use { it.getEntry(MANIFIESTO) != null }) "Coincide con su manifiesto, archivo por archivo.\n" else "") else return Lectura.Error("El respaldo no coincide con su manifiesto: $p") } else ""
            val aqui = "Respaldo / este teléfono: %,d / %,d días · %,d / %,d momentos · %,d / %,d gastos · %,d / %,d experimentos.".format(
                c.r.dias.size, Repo.dias.size, c.r.momentos.size, Repo.momentos.size, c.r.gastos.size, Repo.gastos.size, c.r.experimentos.size, Repo.experimentos.size)
            // «Se lee completo» es lo único que se comprobó: que no le falte nada depende de qué había cuando se hizo.
            return Lectura.Listo((if (cifrado) "Cifrado y abierto con la contraseña. " else "") + "El respaldo se lee completo.\n" + manifiesto +
                (Importar.esquemaFuturo(c.r.schema_v)?.let { "$it\n" } ?: "") + informe(c).joinToString("\n") { "· $it" } + "\n" + aqui)
        } finally { claro.delete() }
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
    private fun ubicaciones(lineas: Sequence<String>): Int {
        val filas = lineas.iterator()
        if (!filas.hasNext()) return 0
        val k = columnas(filas.next())
        val iTs = k["ts"] ?: return 0; val iLat = k["lat"] ?: return 0; val iLon = k["lon"] ?: return 0
        val iPre = k["precision_m"]; val iFu = k["fuente"]; val iMs = k["ms"]
        val db = Repo.db()
        val vacia = db.contarUbicaciones() == 0L
        var n = 0
        db.transaccion {
            for (l in filas) {
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

    private fun catalogo(lineas: Sequence<String>): Int {
        val filas = lineas.iterator()
        if (!filas.hasNext()) return 0
        val k = columnas(filas.next())
        val db = Repo.db()
        var n = 0
        db.transaccion {
            for (l in filas) {
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
