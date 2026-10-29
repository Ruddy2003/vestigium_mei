package io.github.ruddy2003.vestigium.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

/**
 * El gato de la banda de Día: un gato en pixel art que camina solo por una franja de 80 dp, sobre un fondo que es
 * del tema (un mosaico, un vitral, una página del scriptorium, una terminal…), con el pelaje, el accesorio y el
 * material del tema. Además de las 13 animaciones comunes tiene 2 propias de cada tema (escribir en la tablilla,
 * sostener una vela, teclear…).
 *
 * El arte viene ya pintado de herramientas/gato/exportar.py (assets/gato): un atlas con los cuadros del gato, el
 * fondo y, si el fondo se mueve, los mosaicos de 2 dp que cambian en cada cuadro de su bucle. Aquí solo se compone.
 * Lo que hace el gato lo decide una máquina de estados con tiempos al azar (la misma de las pruebas), según cómo
 * dormiste y el ánimo del día ya llenado.
 */
object Gato {
    const val DT_MS = 80L                     // 12,5 cuadros por segundo
    private const val DT = DT_MS / 1000f

    // ---- Índice de recursos (assets/gato/gato.json).
    @Serializable class Especial(val accion: String, val nombre: String, val base: String)
    @Serializable class Sombra(val color: String, val alfa: Int, val suma: Boolean)
    @Serializable class MetaTema(
        val pantalla: Boolean = false,
        /** Por animación, sus cuadros: [x e y en el atlas, desplazamiento x e y dentro del cuadro de 64 dp, ancho, alto], en px. */
        val anims: Map<String, List<List<Int>>> = emptyMap(),
        val especiales: List<Especial> = emptyList(),
        val sombra: Sombra? = null,
        val ajustar: Boolean = false,
        val bucle: Int = 0,
        val luz: List<Int>? = null,
        val post: Boolean = false,
        /** Laboratorio: color de la retícula que sigue al gato y lo mide. */
        val reticula: String? = null,
    )
    @Serializable class Indice(val esc: Int, val ancho_dp: Int, val alto_dp: Int, val suelo_dp: Int, val lado_dp: Int, val temas: Map<String, MetaTema>)

    /** Lo que se mueve en el fondo: por cada cuadro del bucle, pares (lugar en la banda, mosaico) detrás y delante del gato. */
    class Movimiento(val bucle: Int, val lado: Int, val columnas: Int, val atras: Array<IntArray>, val delante: Array<IntArray>)

    class Recursos(
        val indice: Indice, val meta: MetaTema, val gato: Bitmap, val fondo: Bitmap, val atras: Bitmap?, val delante: Bitmap?,
        val mov: Movimiento?, val luz: List<Bitmap>, val post: Bitmap?,
    ) {
        /** El fondo del cuadro actual (base + mosaicos de atrás) y lo que va delante del gato. */
        val trabajo: Bitmap = fondo.copy(Bitmap.Config.ARGB_8888, true)
        val trabajoDelante: Bitmap? = if (mov != null && delante != null) Bitmap.createBitmap(fondo.width, fondo.height, Bitmap.Config.ARGB_8888) else null
        private var aplicado = -1
        private val src = Rect()
        private val dst = Rect()
        private val borrar = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
        internal val pintura = Paint(Paint.FILTER_BITMAP_FLAG)
        internal val suma = Paint(Paint.FILTER_BITMAP_FLAG).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD) }
        internal val multiplica = Paint(Paint.FILTER_BITMAP_FLAG).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY) }
        internal val sombra = Paint(Paint.ANTI_ALIAS_FLAG)
        internal val hud = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 2f }
        internal val hudTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 10f; typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD) }
        internal val r1 = Rect()
        internal val r2 = Rect()
        internal val ovalo = RectF()
        internal val camino = android.graphics.Path()

        private fun lugar(m: Movimiento, l: Int) { val x = (l % m.columnas) * m.lado; val y = (l / m.columnas) * m.lado; dst.set(x, y, x + m.lado, y + m.lado) }
        private fun mosaico(m: Movimiento, i: Int) { val x = (i % 64) * m.lado; val y = (i / 64) * m.lado; src.set(x, y, x + m.lado, y + m.lado) }

        /** Deja [trabajo] y [trabajoDelante] como en el cuadro [tick] del bucle: repone lo del cuadro anterior y pone lo nuevo. */
        fun ponerCuadro(tick: Int) {
            val m = mov ?: return
            val k = Math.floorMod(tick, m.bucle)
            if (k == aplicado) return
            val c = android.graphics.Canvas(trabajo)
            if (aplicado >= 0) m.atras[aplicado].let { p -> for (j in p.indices step 2) { lugar(m, p[j]); c.drawBitmap(fondo, dst, dst, null) } }
            if (atras != null) m.atras[k].let { p -> for (j in p.indices step 2) { lugar(m, p[j]); mosaico(m, p[j + 1]); c.drawBitmap(atras, src, dst, null) } }
            val td = trabajoDelante
            if (td != null && delante != null) {
                val cd = android.graphics.Canvas(td)
                if (aplicado >= 0) m.delante[aplicado].let { p -> for (j in p.indices step 2) { lugar(m, p[j]); cd.drawRect(dst, borrar) } }
                m.delante[k].let { p -> for (j in p.indices step 2) { lugar(m, p[j]); mosaico(m, p[j + 1]); cd.drawBitmap(delante, src, dst, null) } }
            }
            aplicado = k
        }
    }

    private val json = Json { ignoreUnknownKeys = true }
    @Volatile private var indice: Indice? = null

    /** Por qué no se pudo cargar el último tema (se muestra en las vistas previas y capturas). */
    var error: String? = null
        private set

    private fun indice(ctx: Context): Indice? = indice ?: runCatching {
        json.decodeFromString(Indice.serializer(), ctx.assets.open("gato/gato.json").use { it.readBytes().decodeToString() })
    }.onFailure { error = "gato.json: $it" }.getOrNull()?.also { indice = it }

    fun clave(tema: TemaId, oscuro: Boolean) = tema.id + if (oscuro || tema.soloOscuro) "_o" else "_c"

    /** El último tema cargado: volver a Día no lo decodifica otra vez. */
    @Volatile private var ultimo: Pair<String, Recursos>? = null

    fun enCache(tema: TemaId, oscuro: Boolean): Recursos? = ultimo?.takeIf { it.first == clave(tema, oscuro) }?.second

    /** Carga los recursos de un tema (unos 10 MB en memoria). Devuelve null si faltan. */
    fun cargar(ctx: Context, tema: TemaId, oscuro: Boolean): Recursos? = enCache(tema, oscuro) ?: cargarDeAssets(ctx, tema, oscuro)?.also { ultimo = clave(tema, oscuro) to it }

    private fun cargarDeAssets(ctx: Context, tema: TemaId, oscuro: Boolean): Recursos? = runCatching {
        val ind = indice(ctx) ?: return null
        val clave = clave(tema, oscuro)
        val meta = ind.temas[clave] ?: return null
        val op = BitmapFactory.Options().apply { inScaled = false; inPreferredConfig = Bitmap.Config.ARGB_8888 }
        fun img(nombre: String): Bitmap? = runCatching { ctx.assets.open("gato/${clave}_$nombre.webp").use { BitmapFactory.decodeStream(it, null, op) } }
            .onFailure { error = "${clave}_$nombre: $it" }.getOrNull()
        val gato = img("gato") ?: run { if (error == null) error = "${clave}_gato.webp no se pudo decodificar"; return null }
        val fondo = img("fondo") ?: return null
        val mov = if (meta.bucle > 0) runCatching { movimiento(ctx.assets.open("gato/${clave}_mov.bin").use { it.readBytes() }) }.getOrNull() else null
        val luz = meta.luz?.let { l -> (0..(l.maxOrNull() ?: -1)).mapNotNull { img("luz$it") } } ?: emptyList()
        error = null
        Recursos(ind, meta, gato, fondo, img("atras"), img("delante"), mov, luz, if (meta.post) img("post") else null)
    }.onFailure { error = "cargar: $it" }.getOrNull()

    private fun movimiento(b: ByteArray): Movimiento {
        val bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN)
        val bucle = bb.int; val lado = bb.int; val columnas = bb.int
        val atras = arrayOfNulls<IntArray>(bucle)
        val delante = arrayOfNulls<IntArray>(bucle)
        for (k in 0 until bucle) {
            for (capa in 0..1) {
                val n = bb.short.toInt() and 0xFFFF
                val a = IntArray(n * 2) { bb.short.toInt() and 0xFFFF }
                if (capa == 0) atras[k] = a else delante[k] = a
            }
        }
        return Movimiento(bucle, lado, columnas, Array(bucle) { atras[it]!! }, Array(bucle) { delante[it]!! })
    }

    // ---- Cómo está hoy.
    enum class Animo { NORMAL, ALEGRE, TRISTE, ENOJADO }
    data class Humor(val animo: Animo, val cansado: Boolean, val noche: Boolean)

    /**
     * El sueño cuenta desde la mañana: menos de 6 h o calidad 2 o menos es cansado. El ánimo solo cuando el día ya está
     * completo (valencia y diario, la misma regla que «Tus últimos días»), para no devolverte el ánimo mientras lo
     * calificas: 6 o más, alegre; 2 o menos, triste; si además anotaste enojo o frustración en un momento de hoy y la
     * valencia no pasa de 4, enojado. De 22:00 a 5:00 es de noche.
     */
    fun humor(hoy: String = Fechas.diaLogico()): Humor {
        val d = Repo.dias[hoy]
        val horas = Repo.salud[hoy]?.sueno_min?.let { it / 60.0 }
        val cansado = (horas != null && horas < 6.0) || (d?.sueno_calidad != null && d.sueno_calidad <= 2)
        val v = d?.valencia?.takeIf { d.nota.isNotBlank() }
        val enojo = v != null && v <= 4 && Repo.momentos.any { m -> m.fecha == hoy && Catalogo.emocionesDe(m).any { it == "enojo" || it == "frustracion" } }
        val animo = when {
            v == null -> Animo.NORMAL
            enojo -> Animo.ENOJADO
            v >= 6 -> Animo.ALEGRE
            v <= 2 -> Animo.TRISTE
            else -> Animo.NORMAL
        }
        val h = java.time.LocalTime.now().hour
        return Humor(animo, cansado, h >= 22 || h < 5)
    }

    // ---- La máquina de estados.
    /** Un cuadro: qué animación, cuál de sus cuadros, dónde (dp, en la banda de 412) y hacia dónde mira. */
    class Paso(val anim: String, val i: Int, val x: Float, val dir: Int, val dy: Float = 0f)

    private val SALTO_DX = floatArrayOf(0f, 3f, 4.5f, 4.5f, 2f, 0f)      // dp por cuadro en cada fase del salto
    private val SALTO_DY = floatArrayOf(0f, -2f, -9f, -5f, 0f, 0f)
    private val JUEGO = listOf(Triple(0, 3, 0f), Triple(1, 3, 0f), Triple(0, 3, 0f), Triple(1, 3, 0f), Triple(2, 2, 1.5f), Triple(3, 4, 0.3f), Triple(4, 3, 0f), Triple(5, 4, 0f))

    /**
     * Camina, se detiene, se sienta, se asea, se echa a dormir, salta, juega y hace las dos animaciones de su tema.
     * Alegre: juega y salta más. Triste: camina lento con la cola baja y pasa más rato sentado. Enojado: a ratos se
     * eriza. Cansado: camina al 70 %, salta menos y se echa más, con siestas más largas. De noche, casi solo duerme.
     * [desde] y [hasta] son los dp de la banda que se ven en pantalla.
     */
    class Simulacion(private val meta: MetaTema, private val humor: Humor, semilla: Long, private val desde: Float, private val hasta: Float) {
        private val rnd = java.util.Random(semilla)
        private val cola = ArrayDeque<Paso>()
        private val triste = humor.animo == Animo.TRISTE
        private val cam = if (triste) "caminar_triste" else "caminar"
        private val qui = if (triste) "quieto_triste" else "quieto"
        private val vel = 26f * (when (humor.animo) { Animo.TRISTE -> 0.6f; Animo.ALEGRE -> 1.15f; else -> 1f }) * (if (humor.cansado) 0.7f else 1f)
        private var x = (desde + hasta) / 2 - 32
        private var dir = 1
        private var estado = if (humor.noche) "dormir" else "caminar"
        private var resto = durar(estado)
        private var i = 0
        private var especial = 0

        private val tCaminar: List<Pair<String, Double>>
        private val tQuieto: List<Pair<String, Double>>
        private val tSentado: List<Pair<String, Double>>

        init {
            var c = when (humor.animo) {
                Animo.ALEGRE -> listOf("jugar" to 0.4, "saltar" to 0.3, "quieto" to 0.1, "sentarse" to 0.1, "caminar" to 0.1)
                Animo.TRISTE -> listOf("quieto" to 0.35, "sentarse" to 0.45, "caminar" to 0.2)
                Animo.ENOJADO -> listOf("erizarse" to 0.45, "quieto" to 0.25, "sentarse" to 0.15, "caminar" to 0.15)
                Animo.NORMAL -> listOf("quieto" to 0.3, "sentarse" to 0.3, "saltar" to 0.2, "jugar" to 0.1, "caminar" to 0.1)
            }
            val q = when (humor.animo) {
                Animo.ALEGRE -> listOf("jugar" to 0.4, "saltar" to 0.2, "caminar" to 0.3, "especial" to 0.1)
                Animo.TRISTE -> listOf("caminar" to 0.4, "sentarse" to 0.6)
                Animo.ENOJADO -> listOf("erizarse" to 0.4, "caminar" to 0.6)
                Animo.NORMAL -> listOf("caminar" to 0.45, "sentarse" to 0.25, "saltar" to 0.1, "jugar" to 0.1, "especial" to 0.1)
            }
            var s = when (humor.animo) {
                Animo.ALEGRE -> listOf("levantarse" to 0.5, "asearse" to 0.25, "especial" to 0.25)
                Animo.TRISTE -> listOf("echarse" to 0.5, "asearse" to 0.1, "levantarse" to 0.3, "especial" to 0.1)
                Animo.ENOJADO -> listOf("levantarse" to 0.6, "asearse" to 0.4)
                Animo.NORMAL -> listOf("asearse" to 0.35, "echarse" to 0.25, "especial" to 0.25, "levantarse" to 0.15)
            }
            if (humor.cansado) {
                // Salta y juega la mitad; lo que sobra se va a sentarse. Se echa más seguido.
                val menos = c.filter { it.first == "saltar" || it.first == "jugar" }.sumOf { it.second } / 2
                c = c.map { (n, p) -> n to if (n == "saltar" || n == "jugar") p / 2 else p } + ("sentarse" to menos)
                s = s.map { (n, p) -> n to if (n == "echarse") p + 0.2 else p }
            }
            if (humor.noche) s = s.map { (n, p) -> n to if (n == "echarse") p + 0.6 else p }
            tCaminar = c; tQuieto = q; tSentado = s
        }

        private fun durar(e: String): Int {
            fun u(a: Double, b: Double) = a + rnd.nextDouble() * (b - a)
            val seg = when (e) {
                "caminar" -> u(1.6, 3.2)
                "quieto" -> u(1.0, 2.0)
                "sentado" -> u(1.0, 2.0) * (if (triste) 1.6 else 1.0)
                "dormir" -> u(3.0, 4.5) * (if (humor.cansado) 1.5 else 1.0) * (if (humor.noche) 3.0 else 1.0)
                "erizarse" -> u(1.5, 2.4)
                else -> 0.0
            }
            return (seg / DT).toInt()
        }

        private fun elegir(op: List<Pair<String, Double>>): String {
            var u = rnd.nextDouble() * op.sumOf { it.second }
            for ((n, p) in op) { u -= p; if (u <= 0) return n }
            return op.last().first
        }

        private fun cambiar(nuevo: String) {
            var n = nuevo
            if (n == "especial") {
                // Solo las animaciones del tema que parten de esta postura (sentado o de pie).
                val base = if (estado == "sentado") "sentado" else "quieto"
                val ok = meta.especiales.indices.filter { meta.especiales[it].base == base && meta.anims.containsKey(nombreEspecial(it)) }
                if (ok.isEmpty()) {
                    n = if (base == "sentado") "asearse" else "caminar"
                } else {
                    especial = ok[rnd.nextInt(ok.size)]
                }
            }
            estado = n; i = 0; resto = durar(n)
        }

        private fun nombreEspecial(j: Int) = meta.anims.keys.firstOrNull { it.startsWith("esp_") && it.endsWith("_$j") } ?: ""

        private fun pon(anim: String, k: Int, dy: Float = 0f) = cola.addLast(Paso(anim, k, x, dir, dy))
        private fun cabe(xx: Float, d: Int) = if (d > 0) xx + 58 <= hasta else xx + 6 >= desde

        fun siguiente(): Paso {
            while (cola.isEmpty()) avanzar()
            return cola.removeFirst()
        }

        private fun avanzar() {
            when (estado) {
                "caminar" -> {
                    x += dir * vel * DT
                    if (!cabe(x, dir)) {
                        x -= dir * vel * DT
                        repeat(3) { pon("girar", 0) }; repeat(4) { pon("girar", 1) }
                        dir = -dir
                        repeat(3) { pon("girar", 0) }
                    }
                    pon(cam, i % 8); i++; resto--
                    if (resto <= 0) cambiar(elegir(tCaminar))
                }
                "quieto" -> { pon(qui, (i / 3) % 4); i++; resto--; if (resto <= 0) cambiar(elegir(tQuieto)) }
                "saltar" -> {
                    if (!cabe(x + dir * 34, dir)) { cambiar("quieto"); return }
                    for (k in 0 until 6) repeat(2) { x += dir * SALTO_DX[k]; pon("saltar", k, SALTO_DY[k]) }
                    cambiar(elegir(listOf("caminar" to 0.6, "quieto" to 0.4)))
                }
                "jugar" -> {
                    if (!cabe(x + dir * 10, dir)) { cambiar("quieto"); return }
                    for ((k, n, dx) in JUEGO) repeat(n) { x += dir * dx; pon("jugar", k, if (k == 2) -3f else 0f) }
                    cambiar(elegir(listOf("caminar" to 0.5, "quieto" to 0.2, "jugar" to if (humor.animo == Animo.ALEGRE) 0.3 else 0.0)))
                }
                "erizarse" -> { pon("enojado", (i / 2) % 3); i++; resto--; if (resto <= 0) cambiar("quieto") }
                "sentarse", "levantarse", "echarse", "despertar" -> {
                    val base = if (estado == "sentarse" || estado == "levantarse") "sentarse" else "echarse"
                    val n = meta.anims[base]?.size ?: 0
                    val orden = if (estado == "sentarse" || estado == "echarse") (0 until n) else (n - 1 downTo 0)
                    for (k in orden) repeat(2) { pon(base, k) }
                    cambiar(when (estado) { "sentarse" -> "sentado"; "echarse" -> "dormir"; "despertar" -> "sentado"; else -> "caminar" })
                }
                "sentado" -> { pon("sentado", (i / 4) % 2); i++; resto--; if (resto <= 0) cambiar(elegir(tSentado)) }
                "asearse" -> {
                    for (k in 0 until 6) repeat(2) { pon("asearse", k) }
                    for (k in 1 until 6) repeat(2) { pon("asearse", k) }
                    pon("sentado", 0)
                    estado = "sentado"
                    cambiar(elegir(listOf("echarse" to if (humor.cansado) 0.7 else 0.5, "levantarse" to 0.5)))
                }
                "dormir" -> { pon("dormir", (i / 5) % 3); i++; resto--; if (resto <= 0) cambiar("despertar") }
                "especial" -> {
                    val nombre = nombreEspecial(especial)
                    val n = meta.anims[nombre]?.size ?: 0
                    repeat(if (n == 4) 3 else 2) { for (k in 0 until n) repeat(3) { pon(nombre, k) } }
                    val sentado = meta.especiales.getOrNull(especial)?.base == "sentado"
                    repeat(3) { if (sentado) pon("sentado", 0) else pon(qui, 0) }
                    estado = if (sentado) "sentado" else "quieto"
                    cambiar(estado)
                }
                else -> cambiar("caminar")
            }
        }
    }

    // ---- Dibujo.
    private fun color(hex: String, alfa: Int) = (alfa shl 24) or (hex.toLong(16).toInt() and 0xFFFFFF)

    /** La parte de la banda (en px del arte) que se ve en un lienzo de [w]×[h]: escala, corte a la izquierda y arriba. */
    private class Vista(val s: Float, val ox: Float, val oy: Float)

    private fun vista(r: Recursos, w: Float, h: Float): Vista {
        val ancho = r.fondo.width.toFloat(); val alto = r.fondo.height.toFloat()
        val s = maxOf(h / alto, w / ancho)
        return Vista(s, (ancho - w / s) / 2f, alto - h / s)
    }

    /** Los dp de la banda (de 0 a 412) que se ven: ahí camina el gato. */
    fun ventana(r: Recursos, w: Float, h: Float): Pair<Float, Float> {
        val v = vista(r, w, h)
        val esc = r.indice.esc
        return v.ox / esc to (v.ox + w / v.s) / esc
    }

    /** Laboratorio: cuatro esquinas alrededor del gato y una etiqueta con su posición (todo se mide). */
    private fun reticula(c: android.graphics.Canvas, r: Recursos, x: Float, dy: Float, col: Int) {
        val esc = r.indice.esc.toFloat()
        r.hud.color = col; r.hudTexto.color = col
        val x0 = x + 6; val x1 = x + 58
        val y0 = r.indice.suelo_dp - 60 + dy + 13; val y1 = r.indice.suelo_dp + 1f
        val l = 4f
        for ((cx, cy, sx, sy) in listOf(floatArrayOf(x0, y0, 1f, 1f), floatArrayOf(x1, y0, -1f, 1f), floatArrayOf(x0, y1, 1f, -1f), floatArrayOf(x1, y1, -1f, -1f))) {
            c.drawLine(cx * esc, cy * esc, (cx + sx * l) * esc, cy * esc, r.hud)
            c.drawLine(cx * esc, cy * esc, cx * esc, (cy + sy * l) * esc, r.hud)
        }
        val etiqueta = "SUJETO 07 · x = " + String.format(java.util.Locale.US, "%.2f", (x + 32) / 100).replace('.', ',') + " m"
        val tx = if (x0 < r.indice.ancho_dp - 90) x0 * esc else (x1 - 86) * esc
        c.drawText(etiqueta, tx, (y0 - 4.6f) * esc - r.hudTexto.ascent(), r.hudTexto)
    }

    fun dibujar(c: android.graphics.Canvas, r: Recursos, p: Paso, tick: Int, w: Float, h: Float) {
        val ind = r.indice
        val esc = ind.esc
        val v = vista(r, w, h)
        r.ponerCuadro(tick)
        // Escalas casi enteras: pixeles nítidos; si no, un filtro suave para que no salgan pixeles disparejos.
        val nitido = kotlin.math.abs(v.s - v.s.roundToInt()) < 0.05f
        r.pintura.isFilterBitmap = !nitido; r.suma.isFilterBitmap = !nitido
        c.save()
        c.scale(v.s, v.s)
        c.translate(-v.ox, -v.oy)
        c.drawBitmap(r.trabajo, 0f, 0f, r.pintura)
        val x = if (r.meta.ajustar) p.x.roundToInt().toFloat() else p.x
        val dy = if (r.meta.ajustar) p.dy.roundToInt().toFloat() else p.dy
        r.meta.sombra?.let { sm ->
            val k = 1 + dy / 14f
            val cx = (x + if (p.dir > 0) 31 else 33) * esc
            val cy = (ind.suelo_dp + 0.4f) * esc
            r.ovalo.set(cx - 19 * esc * k, cy - 1.3f * esc * k, cx + 19 * esc * k, cy + 1.3f * esc * k)
            r.sombra.color = color(sm.color, sm.alfa)
            r.sombra.xfermode = if (sm.suma) PorterDuffXfermode(PorterDuff.Mode.ADD) else null
            c.drawOval(r.ovalo, r.sombra)
        }
        val q = r.meta.anims[p.anim]?.getOrNull(p.i) ?: r.meta.anims["sentado"]?.firstOrNull()
        if (q != null && q.size >= 6) {
            val px = (x * esc).roundToInt().toFloat()
            val py = ((ind.suelo_dp - 60 + dy) * esc).roundToInt().toFloat()
            r.r1.set(q[0], q[1], q[0] + q[4], q[1] + q[5])
            r.r2.set(q[2], q[3], q[2] + q[4], q[3] + q[5])
            c.save()
            // En las pantallas el reflejo del piso tapa las patas (se suma a la pantalla en lugar del gato).
            if (r.meta.pantalla && r.meta.sombra != null) { r.camino.reset(); r.camino.addOval(r.ovalo, android.graphics.Path.Direction.CW); c.clipOutPath(r.camino) }
            if (p.dir < 0) { c.translate(px + ind.lado_dp * esc, py); c.scale(-1f, 1f) } else c.translate(px, py)
            c.drawBitmap(r.gato, r.r1, r.r2, if (r.meta.pantalla) r.suma else r.pintura)
            c.restore()
        }
        r.meta.reticula?.let { reticula(c, r, x, dy, color(it, 255)) }
        val luz = r.meta.luz
        if (luz != null && r.luz.isNotEmpty()) {
            val l = r.luz.getOrNull(luz[Math.floorMod(tick, luz.size)]) ?: r.luz[0]
            r.r1.set(0, 0, r.fondo.width, r.fondo.height)
            c.drawBitmap(l, null, r.r1, r.multiplica)
        }
        r.trabajoDelante?.let { c.drawBitmap(it, 0f, 0f, r.pintura) }
        r.post?.let { c.drawBitmap(it, 0f, 0f, r.multiplica) }
        c.restore()
    }
}

private fun ahorroDeEnergia(ctx: Context) = (ctx.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager)?.isPowerSaveMode == true

/**
 * La banda de 80 dp con el gato. Se anima solo mientras se ve, con las animaciones encendidas y sin ahorro de energía;
 * si no, el gato queda sentado. [fijo] dibuja un cuadro concreto (para las capturas).
 */
@Composable
fun BandaGato(humor: Gato.Humor, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, fijo: Gato.Paso? = null) {
    val ctx = LocalContext.current
    val oscuro = esOscuro() || tema.soloOscuro
    val vistaPrevia = LocalInspectionMode.current
    // Sin animaciones (y en las capturas) se carga de una vez; si no, en segundo plano para no trabar la pantalla.
    val rec: Gato.Recursos? = if (vistaPrevia || !Repo.config.animaciones) remember(tema, oscuro) { Gato.cargar(ctx, tema, oscuro) }
    else produceState(Gato.enCache(tema, oscuro), tema, oscuro) {
        value = Gato.enCache(tema, oscuro) ?: run { value = null; withContext(Dispatchers.IO) { Gato.cargar(ctx, tema, oscuro) } }
    }.value
    val forma = MaterialTheme.shapes.medium
    val cs = MaterialTheme.colorScheme
    val animar = fijo == null && !vistaPrevia && Repo.config.animaciones && !ahorroDeEnergia(ctx)
    var lienzo by remember { mutableStateOf(0f to 0f) }
    val ventana = if (rec != null && lienzo.first > 0f) Gato.ventana(rec, lienzo.first, lienzo.second) else null
    val semilla = remember { System.nanoTime() }
    val sim = remember(rec, humor, ventana) { if (rec != null && ventana != null) Gato.Simulacion(rec.meta, humor, semilla, ventana.first, ventana.second) else null }
    var tick by remember { mutableIntStateOf(0) }
    var paso by remember { mutableStateOf<Gato.Paso?>(null) }
    LaunchedEffect(sim, animar) {
        paso = null
        if (sim == null || !animar) return@LaunchedEffect
        while (true) {
            withFrameMillis { }          // se detiene mientras la app no está en pantalla
            paso = sim.siguiente()
            tick++
            delay(Gato.DT_MS)
        }
    }
    Canvas(
        modifier.fillMaxWidth().height(80.dp)
            .onSizeChanged { lienzo = it.width.toFloat() to it.height.toFloat() }
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .clip(forma).border(1.dp, cs.outline, forma)
    ) {
        val r = rec
        if (r == null) {
            drawRect(cs.surfaceVariant)
            if (vistaPrevia || !Repo.config.animaciones) drawIntoCanvas { c ->
                c.nativeCanvas.drawText((Gato.error ?: "sin recursos").take(120), 12f, size.height / 2, Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 24f; color = android.graphics.Color.RED })
            }
            return@Canvas
        }
        val centro = ventana?.let { (it.first + it.second) / 2 - 32 } ?: 174f
        val p = fijo ?: paso ?: Gato.Paso("sentado", 0, centro, 1)
        drawIntoCanvas { Gato.dibujar(it.nativeCanvas, r, p, tick, size.width, size.height) }
    }
}

/** En Día (solo hoy): la banda del gato. Se apaga en Apariencia. No sale en la hoja de la noche. */
@Composable
fun GatoDelDia(hoy: String, modifier: Modifier = Modifier) {
    if (!Repo.config.gato) return
    val humor = remember(Repo.dias, Repo.salud, Repo.momentos, hoy) { Gato.humor(hoy) }
    BandaGato(humor, modifier.padding(top = 10.dp))
}
