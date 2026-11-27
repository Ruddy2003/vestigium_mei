package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Lo fijo de arriba del estante de Botánico (la pared, la moldura, la cornisa y el bodegón), pintado una vez por tamaño y modo
 * en una imagen guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de la corona, sobre un fondo opaco, así
 * la junta con el cuerpo no deja asomar el fondo. Se guarda solo la última (el diario y los Sueños se turnan la ranura).
 */
internal object CapaBotanico {
    private var clave: String? = null
    private var imagen: ImageBitmap? = null

    /** Pega la imagen de las filas [arriba] a [abajo] (enteras) de todo el ancho; la pinta antes con [pintar] si cambió. */
    fun DrawScope.pegar(cual: String, arriba: Int, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = abajo - arriba
        if (ancho < 1 || alto < 1) return
        var img = imagen
        if (img == null || clave != cual) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                translate(0f, -arriba.toFloat()) { pintar() }
            }
            imagen = nueva; clave = cual; img = nueva
        }
        drawImage(img, Offset(0f, arriba.toFloat()))
    }
}

/**
 * Dónde está cada cosa para la luz: el aire del momento, el ancho del mueble y cuánta lumbre llega a cada punto. Con [t] se
 * saca el tono de un sitio (ver [TonoBotanico]).
 */
internal class SitioBotanico(val a: AireBotanico, val w: Float, private val cerca: (Offset) -> Float) {
    fun k(p: Offset): Float = cerca(p)
    fun t(p: Offset, cuerpo: Boolean = false): TonoBotanico {
        val fx = p.x / w; val fy = p.y / w
        val arriba = a.oscuro && fy < MuebleEstanteBotanico.LAMP_Y - 0.03f && abs(fx - MuebleEstanteBotanico.LAMP_X) < 0.2f
        return a.en(fx, fy, cerca(p), cuerpo, arriba)
    }

    /**
     * Un degradado a lo ancho, de [x0] a [x1] a la altura [y], con el color que da [col] en cada punto: de noche la lumbre cambia
     * de un punto a otro sin escalones (un solo trazo en lugar de muchos tramos). De día, el color del medio (la luz de día cambia
     * poco a lo largo de una pieza, y un color liso pinta más barato que un degradado).
     */
    fun aLoAncho(x0: Float, x1: Float, y: Float, cuerpo: Boolean = false, col: (TonoBotanico) -> Color): Brush {
        if (x1 - x0 < 2f || !a.lumbre) return SolidColor(col(t(Offset((x0 + x1) / 2, y), cuerpo)))
        val n = ((x1 - x0) / (w * 0.07f)).toInt().coerceIn(3, 16)
        return Brush.horizontalGradient(List(n) { i -> col(t(Offset(x0 + (x1 - x0) * i / (n - 1), y), cuerpo)) }, x0, x1)
    }

    /** Lo mismo a lo alto, de [y0] a [y1] en la [x] dada. */
    fun aLoAlto(y0: Float, y1: Float, x: Float, cuerpo: Boolean = false, col: (TonoBotanico) -> Color): Brush {
        if (y1 - y0 < 2f || !a.lumbre) return SolidColor(col(t(Offset(x, (y0 + y1) / 2), cuerpo)))
        val n = ((y1 - y0) / (w * 0.07f)).toInt().coerceIn(3, 40)
        return Brush.verticalGradient(List(n) { i -> col(t(Offset(x, y0 + (y1 - y0) * i / (n - 1)), cuerpo)) }, y0, y1)
    }
}

/**
 * El dibujo del estante del diario de Botánico (lo usa `EstanteBotanico`, en `BibliotecaContemporanea.kt`): la librería
 * empotrada de roble claro del gabinete del naturalista de vuelta del viaje, hacia 1835, en el mismo cuarto que el gabinete del
 * Ideario (`IdeasBotanico`): la misma pared de verde salvia, que se ve entre las tablas porque la librería no tiene fondo, el
 * mismo roble, el mismo nogal, el mismo latón y la misma luz de arriba a la izquierda. Dos pilastras acanaladas, la cornisa con
 * su friso, las repisas con su fleco de cuero festoneado y el zócalo. Encima, la caja de Ward, los folios con el salacot, el
 * microscopio, el quinqué de pared y la caja de insectos colgada de la moldura; al pie, en el piso de tablas con su alfombra,
 * la caja de embarque, el farol de campo y la palmera. De noche arden el quinqué, la palmatoria de un hueco y el farol. La
 * paleta y la luz están en [PaletaBotanico] y [AireBotanico]; la pared, en [MuroEstanteBotanico]; el mueble, en
 * [ObraEstanteBotanico]; el bodegón, en [CoronaEstanteBotanico]; lo de los huecos, en [HuecosEstanteBotanico]; el pie, en
 * [PieEstanteBotanico]; las entradas, en [VolumenEstanteBotanico].
 */
internal object MuebleEstanteBotanico {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteBotanico` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.36f
    val LADO = 0.075f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el cuaderno de campo; 1, el frasco de semillas; 2, la talega. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 11)) * 31 + (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el cuaderno, el más alto; el frasco; la talega, más baja. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.85f + 0.07f * azar(semilla, 1)
        1 -> 0.8f + 0.08f * azar(semilla, 1)
        else -> 0.78f + 0.08f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el quinqué de pared (bajo su pantalla verde), en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.845f
    const val LAMP_Y = 0.305f
    /** Dónde está el farol del pie: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val FAROL_X = 0.41f
    const val FAROL_Y = 0.62f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 134f
    /** La cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.095f
    /** En la base: dónde acaba el zócalo y empieza el piso, en fracción de su alto. */
    const val SUELO = 0.2f
    /** La palmatoria de un hueco: a qué alto arde (en fracción del hueco, desde abajo). */
    const val VELA_Y = 0.7f

    /** El borde de la pantalla del quinqué: cuánto baja bajo la llama y su radio, en anchos. */
    private const val PANTALLA_ALTO = 0.004f
    private const val PANTALLA_RADIO = 0.066f

    /**
     * Lo que deja pasar la pantalla de vidrio verde: poco por encima de la recta que va de la llama al borde de la pantalla (la
     * luz sale hacia abajo y rasante a los lados), con una penumbra que se abre con la distancia.
     */
    private fun pantalla(dx: Float, dy: Float): Float {
        val m = PANTALLA_ALTO / PANTALLA_RADIO
        val sobre = -dy - m * abs(dx)
        if (sobre <= 0f) return 1f
        val pen = 0.03f + 0.22f * abs(dx)
        val t = (sobre / pen).coerceIn(0f, 1f)
        return 1f - 0.74f * t * t * (3f - 2f * t)
    }

    /** La luz del quinqué en el cuerpo: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos. */
    fun quinque(dx: Float, dy: Float, suenos: Boolean): Float {
        val base = (if (suenos) 1.45f else 2.86f) * exp(-3.47f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.5f else 1f)))
        return (base * pantalla(dx, dy)).coerceIn(0f, 1f)
    }

    /** La luz del quinqué en la pared y en lo de arriba: más corta, para que lo lejano se hunda. */
    fun quinqueArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.6f) * exp(-3.8f * sqrt(dx * dx + dy * dy * 1.1f)) * pantalla(dx, dy)).coerceIn(0.02f, 1f)

    /** La luz del farol del pie: baja, para las repisas de abajo. */
    fun farol(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.8f else 1.5f) * exp(-2.9f * sqrt(dx * dx * 1.15f + dy * dy))).coerceIn(0f, 1f)

    /** La luz de la palmatoria de un hueco: corta, para sus vecinos, la pared y el canto de la tabla de arriba. */
    fun vela(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.85f else 1.4f) * exp(-4.6f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** La palmatoria que más alumbra ([x], [y]) entre [velas] (x, y, x, y… en anchos). */
    private fun velaMayor(x: Float, y: Float, velas: FloatArray, suenos: Boolean): Float {
        var m = 0f
        var i = 0
        while (i + 1 < velas.size) { m = maxOf(m, vela(x - velas[i], y - velas[i + 1], suenos)); i += 2 }
        return m
    }

    /** Cuánta lumbre llega a ([x], [y]) (anchos): el quinqué, el farol a la altura [yFarol] y las [velas]. */
    fun calor(x: Float, y: Float, yFarol: Float, suenos: Boolean, velas: FloatArray): Float {
        val kq = quinque(x - LAMP_X, y - LAMP_Y, suenos)
        val kf = farol(x - FAROL_X, y - yFarol, suenos)
        val kv = velaMayor(x, y, velas, suenos)
        return (1f - (1f - kq) * (1f - kf) * (1f - kv)).coerceIn(0.02f, 1f)
    }

    /** Dónde arde el farol, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yFarolNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * FAROL_Y

    /**
     * Dónde va la palmatoria del hueco de cada repisa (en x del lienzo), o -1 si no lleva. Arde en los huecos con sitio, pero
     * nunca en dos repisas seguidas ni en la primera (ahí alumbra el quinqué). Lo usan el estante (para alumbrar) y los huecos.
     */
    fun velasEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.02f; val x1 = d.derecha.left - w * 0.025f
            if (k == 0 || x1 - x0 < w * 0.17f || xs[k - 1] >= 0f) continue
            xs[k] = x0 + w * 0.055f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireBotanico(oscuro, noche)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yF = (b.top + b.height * FAROL_Y) / w
        // Las palmatorias de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(d.repisas.size) { -1f }
        val velasL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        val xs = velasEn(d, w)
        d.repisas.forEachIndexed { k, r ->
            val x = xs[k]
            if (x < 0f) return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            velasL.add(x / w); velasL.add((r.bottom - r.height * VELA_Y) / w)
        }
        val velas = if (a.lumbre) velasL.toFloatArray() else FloatArray(0)
        LucesHuecoBotanico.poner(LucesHuecoBotanico.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 0f
            else if (p.y < ch) quinqueArriba(p.x / w - LAMP_X, p.y / w - LAMP_Y, noche)
            else calor(p.x / w, p.y / w, yF, noche, velas)
        }
        val s = SitioBotanico(a, w, cerca)
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, la moldura, la cornisa y el bodegón, en una imagen que llega al píxel entero de debajo.
        with(CapaBotanico) {
            pegar("c:$clave", 0, ceil(ch).toInt()) {
                drawRect(s.t(Offset(w / 2, ch), true).c(PaletaBotanico.ROBLE_OSC), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteBotanico) { pared(s, apoyo) }
                with(CoronaEstanteBotanico) { colgado(s, apoyo) }
                with(ObraEstanteBotanico) { cornisa(Rect(0f, apoyo, w, ch + 2f), s) }
                with(CoronaEstanteBotanico) { encima(s, apoyo) }
                with(MuroEstanteBotanico) { luzDeNoche(s, ch) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros (la pared entre las tablas), lo de los huecos, las tablas con su fleco y las pilastras.
        with(ObraEstanteBotanico) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, d, s) }
        }
        with(HuecosEstanteBotanico) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xs[k], s)
            }
        }
        with(ObraEstanteBotanico) {
            fleco(ch, d.izquierda.right, d.derecha.left, -1, s)
            d.tablas.forEachIndexed { k, r -> tabla(k, r, d, s) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, s)
            costado(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, s)
            d.placas.forEach { p -> placa(p, medidor, s) }
        }
        with(HuecosEstanteBotanico) { d.repisas.forEachIndexed { k, r -> if (xs[k] >= 0f) llamaHueco(xs[k], r, s) } }
        // Lo de abajo, en vivo: el zócalo y el piso con lo que hay en él, desde el píxel entero de encima del zócalo y sobre un
        // fondo opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(s.t(Offset(w / 2, b.top), true).c(PaletaBotanico.ROBLE_OSC), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val suelo = b.top + b.height * SUELO
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 0f else calor(p.x / w, p.y / w, yF, noche, FloatArray(0)) }
        val sp = SitioBotanico(a, w, cercaPie)
        with(PieEstanteBotanico) { piso(sp, b, suelo, h) }
        with(ObraEstanteBotanico) { zocalo(Rect(0f, arriba, w, suelo), sp) }
        with(PieEstanteBotanico) { objetos(sp, b, suelo, h) }
        granoMueble(Rect(0f, ch, w, suelo), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireBotanico(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val velas = if (a.lumbre) LucesHuecoBotanico.velas(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yFn = yFarolNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yFn, v.noche, velas) else 0f
        // De qué lado le llega la luz: de día, de arriba a la izquierda; de noche, de la luz que más alumbra.
        val kq = quinque(fx - LAMP_X, fy - LAMP_Y, v.noche)
        val kf = farol(fx - FAROL_X, fy - yFn, v.noche)
        var kv = 0f; var xv = fx
        var i = 0
        while (i + 1 < velas.size) { val e = vela(fx - velas[i], fy - velas[i + 1], v.noche); if (e > kv) { kv = e; xv = velas[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            a.noche && a.luzDia(fx, fy) > maxOf(kq, kf, kv) -> -1f
            kv >= kq && kv >= kf -> if (xv >= fx) 1f else -1f
            kq >= kf -> if (fx < LAMP_X) 1f else -1f
            else -> if (fx < FAROL_X) 1f else -1f
        }
        val t = a.en(fx, fy, if (a.oscuro) maxOf(k, 0.05f) else k)
        val tinte = tonoDeDiaBotanico(v.tono, oscuro)
        with(VolumenEstanteBotanico) {
            when (clase(v.semilla)) {
                // Alguno, ladeado contra el vecino de la izquierda.
                0 -> if (azar(v.semilla, 21) > 0.8f) rotate(-3.6f, Offset(0f, size.height)) { cuaderno(v, medidor, t, hacia, tinte) }
                    else cuaderno(v, medidor, t, hacia, tinte)
                1 -> frasco(v, medidor, t, hacia, tinte)
                else -> talega(v, medidor, t, hacia, tinte)
            }
        }
    }
}

/**
 * Dónde arden las palmatorias de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después,
 * en su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro de la palmatoria en la
 * misma medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoBotanico {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Las palmatorias de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun velas(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val m = MuebleEstanteBotanico
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(m.LADO + c[k] * (1f - 2 * m.LADO))
            l.add(m.CORONA + k * m.FILA / m.ANCHO_NOMINAL + (m.ALTO_REPISA.value * (1f - m.VELA_Y)) / m.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
