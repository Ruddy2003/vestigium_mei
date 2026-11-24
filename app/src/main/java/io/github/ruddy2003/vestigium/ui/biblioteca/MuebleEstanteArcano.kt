package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * El dibujo del estante del diario de Horror cósmico (lo usa `EstanteArcano`, en `BibliotecaFantasia.kt`): la librería de la sala
 * de lectura de arriba del archivo del pueblo, la que da al mar. La hizo un carpintero de ribera con las maderas de un barco que el
 * mar echó a la playa: teca de cubierta con sus tarugos y los agujeros de los pernos, restos de la pintura del casco, la regala del
 * barco por cornisa con un cordón tallado debajo, tablas gruesas de canto redondo con clavos de cobre y su verdín, una curva de
 * madera en cada esquina, el fondo de los casilleros forrado de paño ciruela y el zócalo con un cajón clavado. Contra el papel
 * pintado de escamas verde mar, con la ventana de guillotina empañada arriba a la izquierda. De noche arden el guardabrisas de
 * arriba, la vela metida en una botella en un hueco con sitio y el farol de tormenta del suelo; por la ventana pasa el haz del faro.
 * La paleta y el aire están en [PaletaArcano] y [AireArcano]; la pared, la ventana y el suelo, en [MuroEstanteArcano]; el mueble,
 * en [ObraEstanteArcano]; el bodegón de arriba, en [CoronaEstanteArcano]; lo de los huecos, en [HuecosEstanteArcano]; el pie, en
 * [PieEstanteArcano]; las entradas, en [VolumenEstanteArcano].
 */
internal object MuebleEstanteArcano {
    val LETRA: FontFamily = PaletaArcano.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteArcano` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.37f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 114.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    /** Más ancho cuanto más escribiste (con la raíz de la cuenta, lleno hacia las 100 palabras), y unos dp de más o de menos. */
    fun ancho(palabras: Int): Dp {
        val crece = (1.6f * sqrt(palabras.coerceAtLeast(0).toFloat())).coerceAtMost(16f)
        return (27f + crece + Math.floorMod(palabras * 37 + 11, 5)).dp
    }

    /** Cuál de los tres es una entrada: 0, el libro de registro; 1, la botella lacrada; 2, el fardo en un trozo de vela. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla xor 0x5a17, 61)
        return if (x < 0.42f) 0 else if (x < 0.72f) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el libro, el más alto; la botella, según su forma; el fardo, algo menos. */
    fun alto(semilla: Int, palabras: Int): Float {
        val lleno = (palabras / 800f).coerceIn(0f, 1f)
        return when (clase(semilla)) {
            0 -> 0.83f + 0.03f * lleno + 0.07f * azar(semilla, 1)
            1 -> 0.8f + 0.03f * lleno + 0.09f * azar(semilla, 1)
            else -> 0.77f + 0.05f * lleno + 0.06f * azar(semilla, 1)
        }
    }

    // ---------------------------------------------------------------- las medidas de dentro (en anchos del mueble)

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 136f
    /** La cornisa: desde donde pisan las cosas hasta el arranque de las repisas, en anchos. */
    const val CORNISA = 0.112f
    /** El zócalo, en anchos desde su arranque (de ahí abajo, el suelo). */
    const val ZOCALO = 0.16f

    /** La ventana (el hueco en la pared, sin el marco), en anchos. */
    const val VEN_X0 = 0.045f
    const val VEN_X1 = 0.235f
    const val VEN_Y0 = 0.06f
    const val VEN_Y1 = 0.385f

    // ---------------------------------------------------------------- la luz

    /** La llama del guardabrisas, en anchos desde la esquina de arriba a la izquierda. */
    const val GB_X = 0.745f
    const val GB_Y = 0.338f
    /** La llama del farol del suelo: a lo ancho, y su altura sobre el pie del lienzo, en anchos. */
    const val FAROL_X = 0.79f
    const val FAROL_Y = 0.113f
    /**
     * El farolillo de hojalata colgado en el costado de la izquierda, en la repisa de en medio: a lo ancho, y a qué alto de su
     * repisa arde (en fracción del hueco, desde arriba).
     */
    const val FL_X = 0.0425f
    const val FL_F = 0.5f
    /** La vela de un hueco: a qué alto de su repisa arde (en fracción del hueco, desde abajo). */
    const val VELA_Y = 0.74f

    /** La luz del guardabrisas: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos. En los Sueños, baja. */
    fun guardabrisas(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.95f else 1.7f) * exp(-(if (suenos) 3.0f else 2.35f) * sqrt(dx * dx * 1.15f + dy * dy * (if (dy > 0f) 0.5f else 1f)))).coerceIn(0f, 1f)

    /** La del guardabrisas en la pared y en lo de arriba: más corta, para que lo lejano se hunda. */
    fun guardabrisasArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.95f else 1.6f) * exp(-(if (suenos) 4.6f else 3.9f) * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0f, 1f)

    /** La vela de un hueco: corta, para sus vecinos, el fondo y el canto de la tabla de arriba. */
    fun vela(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.62f else 1.0f) * exp(-5.0f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** El farolillo del costado: alumbra los tramos de en medio, sobre todo los de la izquierda. */
    fun farolillo(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.62f else 1.25f) * exp(-(if (suenos) 3.6f else 2.9f) * sqrt(dx * dx * 0.9f + dy * dy * 1.3f))).coerceIn(0f, 1f)

    /** La repisa de en medio de un estante de [repisas] repisas, donde cuelga el farolillo. */
    fun repisaFarolillo(repisas: Int): Int = ((repisas - 1) / 2).coerceAtLeast(0)

    /** La altura de la llama del farolillo, en anchos, en un estante de [repisas] repisas (la que usan los volúmenes). */
    fun yFarolilloNominal(repisas: Int): Float =
        CORONA + repisaFarolillo(repisas) * FILA / ANCHO_NOMINAL + ALTO_REPISA.value * FL_F / ANCHO_NOMINAL

    /** El farol del suelo: lo de abajo, encendido; se apaga hacia arriba. */
    fun farol(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.85f else 1.55f) * exp(-(if (suenos) 2.8f else 2.5f) * sqrt(dx * dx * 1.2f + dy * dy * (if (dy < 0f) 1.15f else 0.8f)))).coerceIn(0f, 1f)

    /** El frío de la ventana en ([x], [y]), en anchos: la niebla de día, el faro de noche, la luna en los Sueños. */
    fun ventana(x: Float, y: Float, suenos: Boolean): Float {
        val dx = x - (VEN_X0 + VEN_X1) / 2; val dy = y - (VEN_Y0 + VEN_Y1) / 2
        return ((if (suenos) 1.0f else 0.8f) * exp(-(if (suenos) 2.4f else 3.0f) * sqrt(dx * dx + dy * dy * 0.75f))).coerceIn(0f, 1f)
    }

    /** La vela que más alumbra ([x], [y]) entre [velas] (x, y, x, y… en anchos). */
    private fun velaMayor(x: Float, y: Float, velas: FloatArray, suenos: Boolean): Float {
        var m = 0f
        var i = 0
        while (i + 1 < velas.size) { m = maxOf(m, vela(x - velas[i], y - velas[i + 1], suenos)); i += 2 }
        return m
    }

    /**
     * Cuánta luz cálida llega a ([x], [y]) (anchos): el guardabrisas, el farol a la altura [yF], el farolillo del costado a la
     * altura [yL] y las [velas] de los huecos.
     */
    fun calor(x: Float, y: Float, yF: Float, yL: Float, suenos: Boolean, velas: FloatArray): Float {
        val kg = guardabrisas(x - GB_X, y - GB_Y, suenos)
        val kf = farol(x - FAROL_X, y - yF, suenos)
        val kl = farolillo(x - FL_X, y - yL, suenos)
        val kv = velaMayor(x, y, velas, suenos)
        return (1f - (1f - kg) * (1f - kf) * (1f - kl) * (1f - kv)).coerceIn(0.03f, 1f)
    }

    /** El alto total del estante en anchos, con [repisas] repisas (para que cada volumen sepa dónde está el farol). */
    fun totalNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE

    /**
     * Dónde arde la vela del hueco de cada repisa (en x del lienzo), o -1 si no lleva: en los huecos con sitio para su grupo, pero
     * nunca en dos repisas seguidas. Lo usan el estante (para alumbrar) y los huecos (para dibujarla).
     */
    fun velasEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.02f; val x1 = d.derecha.left - w * 0.03f
            if (x1 - x0 < w * 0.17f || (k > 0 && xs[k - 1] >= 0f)) continue
            xs[k] = x0 + w * 0.05f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireArcano(oscuro, noche)
        val am = AireArcano(oscuro, noche, AireArcano.MUEBLE)
        val ch = d.corona.bottom
        val b = d.base
        val n = d.repisas.size
        val yF = h / w - FAROL_Y
        val yL = if (n > 0) d.repisas[repisaFarolillo(n)].let { (it.top + it.height * FL_F) / w } else -9f
        // Las velas de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val xs = velasEn(d, w)
        val centros = FloatArray(n) { -1f }
        val velasL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        d.repisas.forEachIndexed { k, r ->
            val x = xs[k]
            if (x < 0f) return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            velasL.add(x / w); velasL.add((r.bottom - r.height * VELA_Y) / w)
        }
        val velas = if (a.lumbre) velasL.toFloatArray() else FloatArray(0)
        LucesHuecoArcano.poner(LucesHuecoArcano.clave(n, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            val x = p.x / w; val y = p.y / w
            when {
                !a.lumbre -> 1f
                !oscuro -> (0.35f * calor(x, y, yF, yL, true, velas)).coerceIn(0f, 1f)
                p.y < ch -> maxOf(guardabrisasArriba(x - GB_X, y - GB_Y, noche), 0.03f)
                else -> calor(x, y, yF, yL, noche, velas)
            }
        }
        val frio: (Offset) -> Float = { p -> ventana(p.x / w, p.y / w, noche) }
        val g = MarcoArcano(w, h, ch, d.izquierda.right, d.derecha.left, b.top, n, yL * w, a, am, cerca, frio)
        val clave = "${w.toInt()}:$ch:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, la ventana, la cornisa y el bodegón, en una imagen que llega al píxel entero de debajo de la
        // corona. Lo grande se pinta con la luz entera y encima va su mapa de luz; lo que está delante (los vidrios, lo colgado, el
        // bodegón) se pinta después, cada cosa con la luz que le llega.
        with(CapaArcano) {
            pegar(0, "c:$clave", 0, ceil(ch).toInt()) {
                drawRect(am.pleno(PaletaArcano.TECA_OSC), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteArcano) { pared(g); ventanaHueco(g) }
                with(ObraEstanteArcano) { cornisa(g) }
                with(MuroEstanteArcano) { nocheArriba(g); ocasoArriba(g); vidrios(g) }
                with(CoronaEstanteArcano) { colgado(g); encima(g) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        if (n == 0) return
        // El cuerpo: los casilleros, las tablas y los costados con la luz entera; encima, su mapa de luz; lo de los huecos con su
        // luz, las llamas y los rótulos.
        val fondo = d.tablas.last().bottom
        with(ObraEstanteArcano) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, g) }
            d.tablas.forEachIndexed { k, r -> tabla(k, r, g) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, fondo), true, g)
            costado(Rect(d.derecha.left, ch, d.derecha.right, fondo), false, g)
            curvas(d, g)
            luzCuerpo(d, g, velas)
            ocasoCostado(g)
        }
        with(HuecosEstanteArcano) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xs[k], g)
            }
            farolillo(g)
        }
        with(ObraEstanteArcano) { d.placas.forEach { p -> placa(p, medidor, g) } }
        granoMueble(Rect(0f, ch, w, fondo), oscuro, 0.8f)
        // Lo de abajo, en vivo: el zócalo, el suelo y lo que hay en él, desde el píxel entero de encima de la última tabla, sobre
        // un fondo opaco del color del zócalo (la fila de la junta, que puede caer a medio píxel, no deja ver el fondo).
        val arriba = floor(minOf(b.top, fondo))
        drawRect(am.pleno(PaletaArcano.TECA_OSC), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        with(MuroEstanteArcano) { suelo(g) }
        with(ObraEstanteArcano) { zocalo(g, arriba) }
        with(MuroEstanteArcano) { nocheAbajo(g, arriba) }
        with(PieEstanteArcano) { alPie(g) }
        granoMueble(Rect(0f, arriba, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireArcano(oscuro, v.noche, AireArcano.MUEBLE)
        val fx = LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val yF = totalNominal(v.repisas) - FAROL_Y
        val yL = yFarolilloNominal(v.repisas)
        val velas = if (a.lumbre) LucesHuecoArcano.velas(v.repisas, oscuro, v.noche) else FloatArray(0)
        val k = when {
            !a.lumbre -> 1f
            !oscuro -> (0.35f * calor(fx, fy, yF, yL, true, velas)).coerceIn(0f, 1f)
            else -> maxOf(calor(fx, fy, yF, yL, v.noche, velas), 0.05f)
        }
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, de la luz que más alumbra.
        val kg = guardabrisas(fx - GB_X, fy - GB_Y, v.noche)
        val kfa = farol(fx - FAROL_X, fy - yF, v.noche)
        val kl = farolillo(fx - FL_X, fy - yL, v.noche)
        var kv = 0f; var xv = fx
        var i = 0
        while (i + 1 < velas.size) { val e = vela(fx - velas[i], fy - velas[i + 1], v.noche); if (e > kv) { kv = e; xv = velas[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kv >= kg && kv >= kfa && kv >= kl -> if (xv >= fx) 1f else -1f
            kl >= kg && kl >= kfa -> -1f
            kg >= kfa -> if (fx < GB_X) 1f else -1f
            else -> if (fx < FAROL_X) 1f else -1f
        }
        val kfr = ventana(fx, fy, v.noche)
        val x = puntoAnimoArcano(v.tono, oscuro)
        with(VolumenEstanteArcano) {
            when (clase(v.semilla)) {
                0 -> if (azar(v.semilla, 21) > 0.82f) rotate(-3f, Offset(0f, size.height)) { libro(v, medidor, a, k, hacia, kfr, x) }
                    else libro(v, medidor, a, k, hacia, kfr, x)
                1 -> botella(v, medidor, a, k, hacia, kfr, x)
                else -> fardo(v, medidor, a, k, hacia, kfr, x)
            }
        }
    }
}

/**
 * Las medidas que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los costados ([x0], [x1]), el arranque de la base ([base]), cuántas repisas hay ([n]) y a qué alto arde el farolillo
 * del costado ([yFarolillo]); el aire de la pared ([a]) y el
 * del mueble ([am]), cuánta luz cálida llega a cada punto ([cerca]) y cuánto frío de la ventana ([frio]).
 */
internal class MarcoArcano(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val base: Float, val n: Int,
                           val yFarolillo: Float, val a: AireArcano, val am: AireArcano, val cerca: (Offset) -> Float, val frio: (Offset) -> Float) {
    /** Donde pisa lo de arriba: la tapa de la cornisa. */
    val apoyo: Float get() = ch - w * MuebleEstanteArcano.CORNISA
    /** Donde acaba el zócalo y empieza el suelo. */
    val suelo: Float get() = base + w * MuebleEstanteArcano.ZOCALO
    /** Dónde arde el guardabrisas, en píxeles. */
    val guardabrisas: Offset get() = Offset(w * MuebleEstanteArcano.GB_X, w * MuebleEstanteArcano.GB_Y)
    /** Dónde arde el farol del suelo, en píxeles. */
    val farol: Offset get() = Offset(w * MuebleEstanteArcano.FAROL_X, h - w * MuebleEstanteArcano.FAROL_Y)
    /** El aire del paño del fondo y el del suelo. */
    val af: AireArcano by lazy { AireArcano(a.oscuro, a.noche, AireArcano.FONDO) }
    val asu: AireArcano by lazy { AireArcano(a.oscuro, a.noche, AireArcano.SUELO) }
}

/**
 * Lo fijo de arriba del estante (la pared, la ventana, la cornisa y el bodegón), pintado una vez por tamaño y modo en una imagen
 * guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de la corona, sobre un fondo opaco, así la junta con el
 * cuerpo no deja asomar el fondo. Se guarda solo la última: el diario y los Sueños se turnan la misma.
 */
internal object CapaArcano {
    private val claves = arrayOfNulls<String>(1)
    private val imagenes = arrayOfNulls<ImageBitmap>(1)

    fun DrawScope.pegar(ranura: Int, clave: String, arriba: Int, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = abajo - arriba
        if (ancho < 1 || alto < 1 || ranura !in imagenes.indices) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                pintar()
            }
            imagenes[ranura] = nueva; claves[ranura] = clave; img = nueva
        }
        drawImage(img, Offset(0f, arriba.toFloat()))
    }
}

/**
 * Los mapas chicos de luz del estante (lo de arriba, el cuerpo y el pie): una imagen de [cols] columnas pintada una vez por tamaño
 * y modo, estirada sobre su franja en un solo dibujo (en lugar de un degradado por pieza). Se guarda el último de cada uno.
 */
internal object MapaLuzArcano {
    private val claves = arrayOfNulls<String>(3)
    private val mapas = arrayOfNulls<ImageBitmap>(3)

    /** Pinta (si cambió) y estira sobre las filas de [y0] a [y1] (enteras hacia fuera) el mapa de la ranura [ranura]. */
    fun DrawScope.pintar(ranura: Int, clave: String, y0: Float, y1: Float, cols: Int, color: (Float, Float) -> Color) {
        val w = size.width
        val a = floor(y0).toInt(); val b = ceil(y1).toInt()
        if (b <= a || w < 2f || ranura !in 0..2) return
        val c = cols.coerceIn(4, 200)
        val filas = ((b - a) / (w / c)).toInt().coerceIn(2, 600)
        val img = mapas[ranura].takeIf { claves[ranura] == clave } ?: ImageBitmap(c, filas).also { nueva ->
            CanvasDrawScope().draw(Density(1f), layoutDirection, Canvas(nueva), Size(c.toFloat(), filas.toFloat())) {
                for (j in 0 until filas) for (i in 0 until c) {
                    val x = (i + 0.5f) / c
                    val y = (a + (j + 0.5f) / filas * (b - a)) / w
                    drawRect(color(x, y), Offset(i.toFloat(), j.toFloat()), Size(1f, 1f), blendMode = BlendMode.Src)
                }
            }
            mapas[ranura] = nueva; claves[ranura] = clave
        }
        drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset(0, a), IntSize(ceil(w).toInt(), b - a))
    }
}

/**
 * Dónde arden las velas de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en su
 * propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro de la vela en la misma medida que
 * [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoArcano {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Las velas de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun velas(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val m = MuebleEstanteArcano
        val l = ArrayList<Float>()
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(m.LADO + c[k] * (1f - 2 * m.LADO))
            l.add(m.CORONA + k * m.FILA / m.ANCHO_NOMINAL + (m.ALTO_REPISA.value * (1f - m.VELA_Y)) / m.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
