package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Lo fijo de arriba del estante (la pared, la ventana, el tendedero, la cornisa y el bodegón), pintado una vez por tamaño y
 * modo en una imagen guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de la corona, sobre un fondo
 * opaco, así la junta con el cuerpo no deja asomar el fondo. Se guarda solo la última (cambia de tamaño o de modo muy pocas
 * veces). El pie se pinta en vivo.
 */
internal object CapaRevolucion {
    private var clave: String? = null
    private var imagen: ImageBitmap? = null

    /** Pega la imagen de las filas [arriba] a [abajo] (enteras) de todo el ancho; la pinta antes con [pintar] si cambió. */
    fun DrawScope.pegar(nueva: String, arriba: Int, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = abajo - arriba
        if (ancho < 1 || alto < 1) return
        var img = imagen
        if (img == null || clave != nueva) {
            val lienzo = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(lienzo), Size(ancho.toFloat(), alto.toFloat())) {
                translate(0f, -arriba.toFloat()) { pintar() }
            }
            imagen = lienzo; clave = nueva; img = lienzo
        }
        drawImage(img, Offset(0f, arriba.toFloat()))
    }
}

/**
 * La mancha de luz que entra por la ventana del cajista (arriba a la izquierda) y cae en la pared, en lo que hay delante y en
 * la cornisa: los cuarterones de la vidriera proyectados, con la sombra de los listones entre ellos (el cuarterón de papel
 * aceitado deja pasar la mitad). [q] es el cuadrilátero de la mancha en el plano de la pared, en anchos (arriba izquierda,
 * arriba derecha, abajo derecha, abajo izquierda); lo que está más adelante (la cornisa, las tablas, los volúmenes) la recibe
 * corrida [FRENTE] a la izquierda. [fuerza] es lo que aclara.
 */
internal class ManchaRevolucion(val q: FloatArray, val color: Color, val fuerza: Float) {
    /** Un punto de la mancha: [fx] a lo ancho, [fy] a lo alto, de 0 a 1, en anchos, corrido [corrido]. */
    fun en(fx: Float, fy: Float, corrido: Float = 0f): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset(tx + (bx - tx) * fy + corrido, ty + (by - ty) * fy)
    }

    /** Los cuarterones proyectados ([papel]: solo el de papel aceitado; si no, los de vidrio), en un lienzo de ancho [w]. */
    fun forma(w: Float, corrido: Float = 0f, crece: Float = 0f, papel: Boolean = false): Path {
        val p = Path()
        for (c in 0 until COLS) for (f in 0 until FILAS) {
            if ((c == COLS - 1 && f == FILAS - 1) != papel) continue
            val x0 = (c + LISTON) / COLS - crece; val x1 = (c + 1 - LISTON) / COLS + crece
            val y0 = (f + LISTON) / FILAS - crece; val y1 = (f + 1 - LISTON) / FILAS + crece
            val a = en(x0, y0, corrido); val b = en(x1, y0, corrido); val d = en(x1, y1, corrido); val e = en(x0, y1, corrido)
            p.moveTo(a.x * w, a.y * w); p.lineTo(b.x * w, b.y * w); p.lineTo(d.x * w, d.y * w); p.lineTo(e.x * w, e.y * w); p.close()
        }
        return p
    }

    /** Cuánto le toca a ([x], [y]) (anchos) del plano corrido [corrido]: 1 en un cuarterón de vidrio, 0.5 en el de papel, 0 fuera. */
    fun toca(x: Float, y: Float, corrido: Float = 0f): Float {
        val ax = q[0] + corrido; val ay = q[1]
        val ux = q[2] - q[0]; val uy = q[3] - q[1]
        val vx = q[6] - q[0]; val vy = q[7] - q[1]
        val det = ux * vy - uy * vx
        if (kotlin.math.abs(det) < 1e-6f) return 0f
        val px = x - ax; val py = y - ay
        val fx = (px * vy - py * vx) / det
        val fy = (ux * py - uy * px) / det
        if (fx < 0f || fx > 1f || fy < 0f || fy > 1f) return 0f
        val cx = fx * COLS; val cy = fy * FILAS
        val rx = cx - floor(cx); val ry = cy - floor(cy)
        if (rx < LISTON || rx > 1f - LISTON || ry < LISTON || ry > 1f - LISTON) return 0f
        return if (cx >= COLS - 1 && cy >= FILAS - 1) 0.5f else 1f
    }

    companion object {
        /** Los cuarterones de la vidriera (a lo ancho y a lo alto) y lo que ocupa medio listón en cada uno. */
        const val COLS = 3
        const val FILAS = 4
        const val LISTON = 0.07f
        /** Cuánto se corre el plano del frente del mueble respecto de la pared, en anchos (la luz entra desde la izquierda). */
        const val FRENTE = -0.035f
    }
}

/** La mancha de la ventana en este momento, o null (de noche manda la lumbre y la ventana solo enseña la calle). */
internal fun manchaRevolucion(a: AireRevolucion): ManchaRevolucion? = when {
    // La luna, alta y fría: la mancha baja empinada por la pared, la cornisa y la primera repisa.
    a.oscuro && a.noche -> ManchaRevolucion(floatArrayOf(0.24f, 0.19f, 0.42f, 0.23f, 0.54f, 0.52f, 0.36f, 0.48f), PaletaRevolucion.LUNA, 0.95f)
    a.oscuro -> null
    // Al ocaso, el sol bajo: los cuarterones se tienden casi horizontales y muy largos.
    a.noche -> ManchaRevolucion(floatArrayOf(0.24f, 0.1f, 0.74f, 0.19f, 0.86f, 0.39f, 0.36f, 0.3f), PaletaRevolucion.OCASO, 1f)
    else -> ManchaRevolucion(floatArrayOf(0.25f, 0.13f, 0.47f, 0.17f, 0.6f, 0.5f, 0.38f, 0.46f), PaletaRevolucion.SOL, 1f)
}

/**
 * La luz de la ventana sobre un objeto de silueta [sil] (lo de encima de la cornisa, lo colgado): solo donde la mancha lo
 * alcanza, con el borde de la mancha cortándolo, en el plano corrido [corrido]. Se suma a lo que ya está pintado.
 */
internal fun DrawScope.luzDeVentana(sil: Path, a: AireRevolucion, m: ManchaRevolucion?, corrido: Float = ManchaRevolucion.FRENTE * 0.6f) {
    if (m == null) return
    val (col, alfa) = when {
        a.oscuro -> Color(0xFF6A82BE) to 0.32f
        a.noche -> PaletaRevolucion.OCASO to 0.42f
        else -> Color(0xFFFFD08A) to 0.34f
    }
    clipPath(sil) {
        clipPath(m.forma(size.width, corrido)) { drawRect(col.copy(alpha = alfa * m.fuerza), Offset.Zero, size, blendMode = BlendMode.Screen) }
        clipPath(m.forma(size.width, corrido, papel = true)) { drawRect(col.copy(alpha = alfa * m.fuerza * 0.5f), Offset.Zero, size, blendMode = BlendMode.Screen) }
    }
}

/**
 * El dibujo del estante del diario de Revolución (lo usa `EstanteRevolucion`, en `BibliotecaModerna.kt`): la librería de la
 * trastienda de una imprenta de un faubourg de París en el otoño de 1790, de roble pintado de rojo sangre de toro con el
 * interior de verde cardenillo, hecha al gusto de los años 80. La cornisa con su tablero, el cimacio, los dentellones y el
 * friso de canales; los costados como pilastras acanaladas; las tablas molduradas con la etiqueta impresa del mes; el cuerpo
 * bajo con dos cajones y las patas de peonza. Contra el yeso ocre rosado con su cenefa de plantilla, bajo el tendedero de los
 * pliegos y junto a la ventana del cajista (de día, su mancha de cuarterones; de noche, la calle). De noche arden la vela de
 * sebo de arriba, la de un hueco y la estufa del pie. La paleta y la luz están en [PaletaRevolucion] y [AireRevolucion]; la
 * pared, la ventana y el tendedero, en [MuroEstanteRevolucion]; el mueble, en [ObraEstanteRevolucion]; el bodegón, en
 * [CoronaEstanteRevolucion]; lo de los huecos, en [HuecosEstanteRevolucion]; el pie, en [PieEstanteRevolucion]; las entradas,
 * en [VolumenEstanteRevolucion].
 */
internal object MuebleEstanteRevolucion {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteRevolucion` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.32f
    val LADO = 0.075f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el tomo en cartoné; 1, el fajo de folletos; 2, el rollo de carteles. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 13)) * 29 + (semilla ushr 6), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el tomo, el más alto; el rollo; el fajo, más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.85f + 0.06f * azar(semilla, 1)
        1 -> 0.78f + 0.06f * azar(semilla, 1)
        else -> 0.82f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la vela de arriba, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val VELA_X = 0.665f
    const val VELA_Y = 0.335f
    /** Dónde está la boca de la estufa: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val ESTUFA_X = 0.1f
    const val ESTUFA_Y = 0.72f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 400f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 132f
    /** La cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.11f
    /** En la base: dónde acaba el cuerpo bajo (los cajones), dónde pisan las patas y empieza el suelo, en fracción de su alto. */
    const val CAJONES = 0.4f
    const val SUELO = 0.6f
    /** La vela de un hueco: a qué alto de su repisa arde (en fracción del hueco, desde abajo). */
    const val VELITA_Y = 0.5f

    /** La luz de la vela de arriba en el cuerpo: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos. */
    fun vela(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.9f else 1.6f) * exp(-3.1f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.5f else 1f)))).coerceIn(0f, 1f)

    /** La luz de la vela en la pared y en lo de arriba: más corta que en el cuerpo, para que lo lejano se hunda. */
    fun velaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.8f else 1.5f) * exp(-3.6f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.02f, 1f)

    /** La luz de la estufa del pie: roja y baja, para las repisas de abajo. */
    fun estufa(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.9f else 1.5f) * exp(-2.45f * sqrt(dx * dx * 1.2f + dy * dy * 1.3f))).coerceIn(0f, 1f)

    /** La luz de la vela de un hueco: corta, para sus vecinos, el fondo y el canto de la tabla de arriba. */
    fun velita(dx: Float, dy: Float): Float = (1.05f * exp(-4.4f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** La vela de hueco que más alumbra ([x], [y]) entre [velas] (x, y, x, y… en anchos). */
    private fun velitaMayor(x: Float, y: Float, velas: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < velas.size) { m = maxOf(m, velita(x - velas[i], y - velas[i + 1])); i += 2 }
        return m
    }

    /** Cuánta luz cálida llega a ([x], [y]) (anchos): la vela de arriba, la estufa a la altura [yEstufa] y las [velas] de los huecos. */
    fun calor(x: Float, y: Float, yEstufa: Float, suenos: Boolean, velas: FloatArray): Float {
        val kv = vela(x - VELA_X, y - VELA_Y, suenos)
        val ke = estufa(x - ESTUFA_X, y - yEstufa, suenos)
        val kh = velitaMayor(x, y, velas)
        return (1f - (1f - kv) * (1f - ke) * (1f - kh)).coerceIn(0.03f, 1f)
    }

    /** Dónde arde la estufa, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yEstufaNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * ESTUFA_Y

    /**
     * Dónde va la vela del hueco de cada repisa (en x del lienzo), o -1 si no lleva. Arde en los huecos con sitio, pero nunca
     * en dos repisas seguidas, para que no haya una reja de llamas. Lo usan el estante (para alumbrar) y los huecos.
     */
    fun velasEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.024f; val x1 = d.derecha.left - w * 0.03f
            if (x1 - x0 < w * 0.17f || (k > 0 && xs[k - 1] >= 0f)) continue
            xs[k] = x0 + w * 0.05f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireRevolucion(oscuro, noche)
        val ac = AireRevolucion(oscuro, noche, cuerpo = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yE = (b.top + b.height * ESTUFA_Y) / w
        // Las velas de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(d.repisas.size) { -1f }
        val velL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        val xs = velasEn(d, w)
        d.repisas.forEachIndexed { k, r ->
            val x = xs[k]
            if (x < 0f) return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            velL.add(x / w); velL.add((r.bottom - r.height * VELITA_Y) / w)
        }
        val velas = if (a.lumbre) velL.toFloatArray() else FloatArray(0)
        LucesHuecoRevolucion.poner(LucesHuecoRevolucion.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < ch) velaArriba(p.x / w - VELA_X, p.y / w - VELA_Y, noche)
            else calor(p.x / w, p.y / w, yE, noche, velas)
        }
        val m = manchaRevolucion(a)
        val clave = "${w.toInt()}:$ch:$oscuro:$noche:$density"

        // Lo de arriba: la pared, la ventana, el tendedero, la cornisa y el bodegón, en una imagen que llega al píxel entero de
        // debajo de la corona.
        with(CapaRevolucion) {
            pegar("c:$clave", 0, ceil(ch).toInt()) {
                drawRect(a.luz(PaletaRevolucion.SANGRE_OSC, 0.5f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteRevolucion) { pared(a, apoyo, cerca, m) }
                with(CoronaEstanteRevolucion) { atras(a, apoyo, cerca, m) }
                with(ObraEstanteRevolucion) { cornisa(Rect(0f, apoyo, w, ch + 2f), ac, cerca, m) }
                with(CoronaEstanteRevolucion) { delante(a, apoyo, cerca, m) }
                with(MuroEstanteRevolucion) { luzDeNoche(a, ch) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, lo de los huecos, las tablas, los costados y las etiquetas.
        with(ObraEstanteRevolucion) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, d, ac, cerca, m) }
        }
        with(HuecosEstanteRevolucion) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xs[k], ac, cerca)
            }
        }
        with(ObraEstanteRevolucion) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, d, ac, cerca, m) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, ac, cerca)
            costado(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, ac, cerca)
            d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center)) }
        }
        with(HuecosEstanteRevolucion) { d.repisas.forEachIndexed { k, r -> if (xs[k] >= 0f) llamaHueco(xs[k], r, ac) } }
        // Lo de abajo, en vivo: el cuerpo bajo, el suelo y lo que hay en él, desde el píxel entero de encima y sobre un fondo
        // opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(ac.luz(PaletaRevolucion.SANGRE_OSC, cerca(Offset(w / 2, b.top))), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yE, noche, FloatArray(0)) }
        with(ObraEstanteRevolucion) { cuerpoBajo(Rect(0f, arriba, w, b.top + b.height * SUELO), b.top, ac, cercaPie) }
        with(PieEstanteRevolucion) { alPie(ac, b, b.top + b.height * SUELO, h, cercaPie) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireRevolucion(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val velas = if (a.lumbre) LucesHuecoRevolucion.velas(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yEn = yEstufaNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yEn, v.noche, velas) else 1f
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, de la luz que más alumbra.
        val kv = vela(fx - VELA_X, fy - VELA_Y, v.noche)
        val ke = estufa(fx - ESTUFA_X, fy - yEn, v.noche)
        var kh = 0f; var xh = fx
        var i = 0
        while (i + 1 < velas.size) { val e = velita(fx - velas[i], fy - velas[i + 1]); if (e > kh) { kh = e; xh = velas[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kh >= kv && kh >= ke -> if (xh >= fx) 1f else -1f
            kv >= ke -> if (fx < VELA_X) 1f else -1f
            else -> if (fx < ESTUFA_X) 1f else -1f
        }
        // La luna de los Sueños: cuánto le toca a su frente.
        val mz = manchaRevolucion(a)
        val luna = if (a.oscuro && mz != null) mz.toca(fx, fy, ManchaRevolucion.FRENTE * 0.5f) else 0f
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val papel = papelRevolucion(v.tono, oscuro)
        with(VolumenEstanteRevolucion) {
            when (clase(v.semilla)) {
                // Alguno, ladeado contra el vecino de la izquierda.
                0 -> if (azar(v.semilla, 21) > 0.82f) rotate(-3f, Offset(0f, size.height)) { tomo(v, medidor, a, kd, hacia, papel, luna) }
                    else tomo(v, medidor, a, kd, hacia, papel, luna)
                1 -> fajo(v, medidor, a, kd, hacia, papel, luna)
                else -> rollo(v, medidor, a, kd, hacia, papel, luna)
            }
        }
    }
}

/**
 * Dónde arden las velas de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en su
 * propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro de la vela en la misma medida
 * que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoRevolucion {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Las velas de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun velas(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val m = MuebleEstanteRevolucion
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(m.LADO + c[k] * (1f - 2 * m.LADO))
            l.add(m.CORONA + k * m.FILA / m.ANCHO_NOMINAL + (m.ALTO_REPISA.value * (1f - m.VELITA_Y)) / m.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
