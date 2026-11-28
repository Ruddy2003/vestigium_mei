package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
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
 * Lo fijo de arriba del estante de Arcontes (la pared de la cueva, el techo, la mancha de la boca, lo colgado, el bodegón y la
 * cornisa), pintado una vez por tamaño y modo en una imagen guardada que luego se pega. Va de la fila 0 a la fila entera de
 * debajo de la corona, sobre un fondo opaco. Se guarda solo la última (el diario y los Sueños se turnan la ranura).
 */
internal object CapaArcontes {
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
 * El dibujo del estante del diario de Arcontes (lo usa `EstanteArcontes`, en `BibliotecaArcontes.kt`): el armario de libros de
 * la comunidad escondida, de olivo, metido en un entrante de la misma cueva del Ideario (`IdeasArcontes`), más adentro que los
 * nichos: la misma caliza con sus estratos, el mismo techo de roca, el mismo olivo y el mismo bronce de las arquetas, y la misma
 * luz (el sol de la boca, que queda fuera del cuadro arriba a la izquierda, y de noche las lámparas de aceite en el azul negro
 * del cielo antiguo). La cornisa de gola con su friso de muescas a cuchillo, los costados de tablón con sus chapas de bronce,
 * las tablas gruesas sin fondo (entre ellas se ve la roca) y el zócalo de sillares herodianos. Encima, la capsa de los rollos,
 * las tablillas de cera, el tintero, la lámpara y la alcuza; en la pared, el hisopo y la bolsa del escriba colgados y la
 * grieta; al pie, el ánfora del salador, el brasero y la red. La paleta y la luz están en [PaletaArcontes] y [AireArcontes]; la
 * roca, en [RocaEstanteArcontes]; el mueble, en [ObraEstanteArcontes]; el bodegón, en [CoronaEstanteArcontes]; lo de los huecos,
 * en [HuecosEstanteArcontes]; el pie, en [PieEstanteArcontes]; las entradas, en [VolumenEstanteArcontes].
 */
internal object MuebleEstanteArcontes {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteArcontes` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.34f
    val LADO = 0.08f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el códice; 1, el rollo; 2, el envoltorio de lana. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 9)) * 29 + (semilla ushr 4), 9)
        return if (k < 4) 0 else if (k < 7) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del hueco: el códice, el más alto; el rollo; el envoltorio, más bajo y blando. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.84f + 0.07f * azar(semilla, 1)
        1 -> 0.82f + 0.08f * azar(semilla, 1)
        else -> 0.78f + 0.08f * azar(semilla, 1)
    }

    /**
     * El ancho de un teléfono grande, en dp. En un estante más ancho, lo de la corona (lo colgado, el bodegón, la cornisa) y lo del
     * pie (el zócalo y lo que hay en el suelo) se queda de ese tamaño, en medio, y la pared y el suelo siguen a los lados.
     */
    const val TOPE = 440f

    /** Cuánto se achica lo de la corona y del pie en un estante de [w] píxeles con densidad [densidad]: 1 hasta el [TOPE]. */
    fun escala(w: Float, densidad: Float): Float = minOf(1f, TOPE * densidad / maxOf(1f, w))

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la lámpara de encima del armario, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.8f
    val LAMP_Y get() = CORONA - CORNISA - 0.088f
    /** Dónde arde el brasero del pie: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val BRASERO_X = 0.5f
    const val BRASERO_Y = 0.5f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 132f
    /** La cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.105f
    /** En la base: dónde acaba el zócalo de sillares y empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.3f
    /** La roca que asoma por fuera de cada costado, en anchos. */
    const val ROCA_LADO = 0.02f
    /** La lámpara de un hueco: a qué alto arde (en fracción del hueco, desde abajo). */
    const val VELA_Y = 0.32f

    /** La lámpara de la ménsula del costado derecho: dónde arde, a lo ancho (anchos) y a lo alto (en fracción de su hueco, desde arriba). */
    const val MENSULA_X = 0.985f
    const val MENSULA_Y = 0.4f

    /**
     * En qué repisa va la lámpara de la ménsula, o -1: solo cuando hay tres o más repisas seguidas, entre la de arriba y la de abajo,
     * sin lámpara de hueco ([xs]); entonces va en la de en medio de ese tramo, para que no quede a oscuras.
     */
    fun mensulaEn(xs: FloatArray): Int {
        var mejor = -1; var largo = 0
        var k = 1
        while (k < xs.size - 1) {
            if (xs[k] >= 0f) { k++; continue }
            var j = k
            while (j < xs.size - 1 && xs[j] < 0f) j++
            if (j - k > largo) { largo = j - k; mejor = k + (j - k) / 2 }
            k = j
        }
        return if (largo >= 3) mejor else -1
    }

    /** La luz de la lámpara de arriba en el cuerpo: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos. */
    fun lampara(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.3f) * exp(-5f * sqrt(dx * dx * 1.05f + dy * dy * (if (dy > 0f) 0.6f else 1f)))).coerceIn(0f, 1f)

    /** El tramo de la grieta que vuelve a asomar en el fondo de la primera repisa: a lo ancho, en anchos. */
    val GRIETA_HUECO_X get() = RocaEstanteArcontes.GRIETA_X1 - 0.012f

    /** La distancia de ([x], [y]) al tramo de ([x0], [y0]) a ([x1], [y1]). */
    private fun distanciaTramo(x: Float, y: Float, x0: Float, y0: Float, x1: Float, y1: Float): Float {
        val dx = x1 - x0; val dy = y1 - y0
        val l2 = (dx * dx + dy * dy).coerceAtLeast(1e-6f)
        val f = (((x - x0) * dx + (y - y0) * dy) / l2).coerceIn(0f, 1f)
        val ex = x - (x0 + dx * f); val ey = y - (y0 + dy * f)
        return sqrt(ex * ex + ey * ey)
    }

    /**
     * La luz fría de la grieta en ([x], [y]) (anchos, de noche): de la raja de la pared y, si [y0] < [y1], del tramo que vuelve a
     * asomar en la primera repisa. La única luz fría del cuadro: alumbra poco y de cerca.
     */
    fun frio(x: Float, y: Float, y0: Float, y1: Float): Float {
        val r = RocaEstanteArcontes
        var d = distanciaTramo(x, y, r.GRIETA_X0, r.GRIETA_Y0, r.GRIETA_X1, r.GRIETA_Y1)
        if (y1 > y0) d = minOf(d, distanciaTramo(x, y, GRIETA_HUECO_X, y0, GRIETA_HUECO_X - 0.01f, y1))
        return if (d > 0.25f) 0f else 0.85f * exp(-d / 0.045f)
    }

    /** La luz de la lámpara de arriba en la pared y en lo de arriba: más corta, para que lo lejano se hunda. */
    fun lamparaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.5f) * exp(-5f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.02f, 1f)

    /** La luz del brasero del pie: baja y roja, para las repisas de abajo. */
    fun brasero(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.4f) * exp(-4.2f * sqrt(dx * dx * 1.1f + dy * dy))).coerceIn(0f, 1f)

    /** La luz de la lámpara de un hueco: corta, para sus vecinos, la roca del fondo y el canto de las tablas. */
    fun vela(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.8f else 1.35f) * exp(-5.6f * sqrt(dx * dx + dy * dy * 0.9f))).coerceIn(0f, 1f)

    /** La lámpara de hueco que más alumbra ([x], [y]) entre [velas] (x, y, x, y… en anchos; las de las velas, divididas por [e]). */
    private fun velaMayor(x: Float, y: Float, velas: FloatArray, suenos: Boolean, e: Float = 1f): Float {
        var m = 0f
        var i = 0
        while (i + 1 < velas.size) { m = maxOf(m, vela(x - velas[i] / e, y - velas[i + 1] / e, suenos)); i += 2 }
        return m
    }

    /** Cuánta lumbre llega a ([x], [y]) (anchos): la lámpara de arriba, el brasero a la altura [yBrasero] y las [velas]. */
    fun calor(x: Float, y: Float, yBrasero: Float, suenos: Boolean, velas: FloatArray): Float {
        val kl = lampara(x - LAMP_X, y - LAMP_Y, suenos)
        val kb = brasero(x - BRASERO_X, y - yBrasero, suenos)
        val kv = velaMayor(x, y, velas, suenos)
        return (1f - (1f - kl) * (1f - kb) * (1f - kv)).coerceIn(0.02f, 1f)
    }

    /** Qué parte de la lumbre de ([x], [y]) es del brasero (para teñirla más roja). */
    fun rojez(x: Float, y: Float, yBrasero: Float, suenos: Boolean, velas: FloatArray): Float {
        val kl = lampara(x - LAMP_X, y - LAMP_Y, suenos)
        val kb = brasero(x - BRASERO_X, y - yBrasero, suenos)
        val kv = velaMayor(x, y, velas, suenos)
        return (kb / (kl + kb + kv + 0.05f)).coerceIn(0f, 1f)
    }

    /**
     * La lumbre de ([x], [y]) y la parte de ella que es brasa, de una vez, en [o] (o[0] y o[1]), con la lámpara y el brasero donde
     * los puso el estante ([a]).
     */
    fun lumbre(x: Float, y: Float, a: AireArcontes, velas: FloatArray, o: FloatArray) {
        val suenos = a.noche
        val e = a.escala
        val kl = lampara((x - a.lampX) / e, (y - a.lampY) / e, suenos)
        val kb = brasero((x - a.braseroX) / e, (y - a.braseroY) / e, suenos)
        val kv = velaMayor(x / e, y / e, velas, suenos, e)
        o[0] = (1f - (1f - kl) * (1f - kb) * (1f - kv)).coerceIn(0.02f, 1f)
        o[1] = (kb / (kl + kb + kv + 0.05f)).coerceIn(0f, 1f)
    }

    private val SIN_VELAS = FloatArray(0)

    /** Dónde arde el brasero, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yBraseroNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * BRASERO_Y

    /**
     * Dónde va la lámpara del hueco de cada repisa (en x del lienzo), o -1 si no lleva. Arde en los huecos con sitio, pero nunca
     * en dos repisas seguidas ni en la primera (ahí alumbra la de arriba). Lo usan el estante (para alumbrar) y los huecos.
     */
    fun velasEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.02f; val x1 = d.derecha.left - w * 0.025f
            if (k == 0 || x1 - x0 < w * 0.16f || xs[k - 1] >= 0f) continue
            xs[k] = x0 + w * 0.06f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireArcontes(oscuro, noche)
        val ch = d.corona.bottom
        val b = d.base
        // En un estante ancho, lo de la corona y del pie se queda del tamaño de un teléfono grande ([TOPE]), en medio.
        val e = escala(w, density)
        a.escala = e
        ObraEstanteArcontes.vetaNoche = if (oscuro) 0.45f else 1f
        val wc = w * e
        val apoyo = ch - wc * CORNISA
        val pCorona = Offset(w / 2, apoyo)
        a.lampX = 0.5f + (LAMP_X - 0.5f) * e
        a.lampY = (apoyo - 0.088f * wc) / w
        val suelo = b.top + b.height * SUELO * e
        val hPie = suelo + (h - suelo) / e
        val pPie = Offset(w / 2, suelo)
        val fuego = PieEstanteArcontes.fuego(w, suelo, hPie).let { f -> Offset(pPie.x + (f.x - pPie.x) * e, pPie.y + (f.y - pPie.y) * e) }
        a.braseroX = fuego.x / w; a.braseroY = fuego.y / w
        // La grieta que vuelve a asomar en el fondo de la primera repisa (para su luz fría).
        val r0 = d.repisas.firstOrNull()
        val g0 = if (r0 != null) (r0.top + w * ObraEstanteArcontes.TECHO * 0.6f) / w else 0f
        val g1 = if (r0 != null) (r0.top + r0.height * 0.52f) / w else 0f
        // Las lámparas de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
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
        val mensula = mensulaEn(xs)
        d.repisas.getOrNull(mensula)?.let { r -> velasL.add(MENSULA_X); velasL.add((r.top + r.height * MENSULA_Y) / w) }
        val velas = if (a.lumbre) velasL.toFloatArray() else FloatArray(0)
        LucesHuecoArcontes.poner(LucesHuecoArcontes.clave(d.repisas.size, oscuro, noche), centros, mensula)
        val s = SitioArcontes(a, w) { x, y, o ->
            if (!a.lumbre) { o[0] = 0f; o[1] = 0f }
            else if (y < ch) { o[0] = lamparaArriba((x / w - a.lampX) / e, (y / w - a.lampY) / e, noche); o[1] = 0f }
            else lumbre(x / w, y / w, a, velas, o)
            if (oscuro) o[2] = frio(x / w, y / w, g0, g1)
        }
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, el techo, lo colgado, el bodegón y la cornisa, en una imagen que llega al píxel entero de debajo.
        with(CapaArcontes) {
            pegar("c:$clave", 0, ceil(ch).toInt()) {
                with(RocaEstanteArcontes) { pared(s, Rect(0f, 0f, w, ch + 2f), apoyo) }
                achicado(s, e, pCorona) { sc -> with(CoronaEstanteArcontes) { colgado(sc, apoyo) } }
                with(ObraEstanteArcontes) { cornisa(Rect(0f, apoyo, w, ch + 2f), s, wc) }
                achicado(s, e, pCorona) { sc -> with(CoronaEstanteArcontes) { encima(sc, apoyo) } }
                with(RocaEstanteArcontes) { lumbreArriba(s, ch) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los huecos (la roca entre las tablas), lo de los huecos, las tablas y los costados. Primero fuera de la
        // mancha de la boca y, recortado por ella, otra vez dentro: la luz se quiebra en cada plano.
        val grupos = IntArray(d.repisas.size) { -1 }
        var j = (d.repisas.size + 1) % HuecosEstanteArcontes.GRUPOS
        d.repisas.forEachIndexed { k, _ ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            if (!HuecosEstanteArcontes.conSitio(oc, d.derecha.left, w)) return@forEachIndexed
            grupos[k] = j % HuecosEstanteArcontes.GRUPOS; j++
        }
        val q = if (a.parche === AireArcontes.OCASO_Q) a.parche else null
        if (q != null) a.forzar = 0f
        cuerpo(d, s, xs, grupos, medidor, null)
        if (q != null) {
            // El rectángulo que encierra la mancha (con su borde movido), en el lienzo.
            var x0 = Float.MAX_VALUE; var x1 = -Float.MAX_VALUE; var y0 = Float.MAX_VALUE; var y1 = -Float.MAX_VALUE
            var i = 0
            while (i + 1 < q.size) { x0 = minOf(x0, q[i]); x1 = maxOf(x1, q[i]); y0 = minOf(y0, q[i + 1]); y1 = maxOf(y1, q[i + 1]); i += 2 }
            val caja = Rect((x0 - 0.02f) * w, maxOf(y0 - 0.02f, 0f) * w, (x1 + 0.02f) * w, (y1 + 0.02f) * w)
            if (caja.bottom > ch) {
                a.forzar = 1f
                clipPath(caminoManchaArcontes(q, w, false)) { cuerpo(d, s, xs, grupos, medidor, caja) }
            }
            a.forzar = -1f
        }
        // Lo de abajo, en vivo: el zócalo y el suelo con lo que hay en él, desde el píxel entero de encima del zócalo y sobre un
        // fondo opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(s.t(Offset(w / 2, b.top), true).c(PaletaArcontes.CALIZA_SOMBRA), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val sp = SitioArcontes(a, w) { x, y, o -> if (!a.lumbre) { o[0] = 0f; o[1] = 0f } else lumbre(x / w, y / w, a, SIN_VELAS, o) }
        with(PieEstanteArcontes) { suelo(sp, b, suelo, h, fuego) }
        with(ObraEstanteArcontes) { zocalo(Rect(0f, arriba, w, suelo), sp, wc) }
        achicado(sp, e, pPie) { sl -> with(PieEstanteArcontes) { objetos(sl, suelo, hPie) } }
        d.repisas.forEachIndexed { k, r ->
            val x = (d.ocupado.getOrNull(k) ?: r.left).coerceIn(r.left, r.right)
            granoMueble(Rect(r.left, r.top, x, r.top + r.height * 0.26f), oscuro, 0.8f)
            granoMueble(Rect(x, r.top, r.right, r.bottom), oscuro, 0.8f)
        }
        d.tablas.forEach { r -> granoMueble(r, oscuro, 0.8f) }
        granoMueble(Rect(0f, ch, d.izquierda.right, b.top), oscuro, 0.8f)
        granoMueble(Rect(d.derecha.left, ch, w, b.top), oscuro, 0.8f)
        granoMueble(Rect(0f, b.top, w, h), oscuro, 0.8f)
    }

    /** Pinta [bloque] achicado a [e] alrededor de [centro] (con su sitio en esas medidas), o tal cual si no hace falta. */
    private inline fun DrawScope.achicado(s: SitioArcontes, e: Float, centro: Offset, bloque: DrawScope.(SitioArcontes) -> Unit) {
        if (e >= 0.999f) { bloque(s); return }
        val sc = s.escalado(e, centro)
        withTransform({ scale(e, e, centro) }) { bloque(sc) }
    }

    /**
     * El cuerpo: los huecos, lo que hay en ellos, las tablas, los costados y los rótulos. Con [solo], únicamente lo que cae en ese
     * rectángulo (la segunda pasada, dentro de la mancha de la boca).
     */
    private fun DrawScope.cuerpo(d: DisposicionEstante, s: SitioArcontes, xs: FloatArray, grupos: IntArray, medidor: TextMeasurer, solo: Rect?) {
        val ch = d.corona.bottom
        val b = d.base
        fun toca(r: Rect) = solo == null || r.overlaps(solo)
        with(ObraEstanteArcontes) { d.repisas.forEachIndexed { k, r -> if (toca(r)) hueco(k, r, d, s) } }
        with(HuecosEstanteArcontes) {
            d.repisas.forEachIndexed { k, r ->
                if (grupos[k] < 0) return@forEachIndexed
                if (!toca(Rect(d.ocupado[k], r.top, d.derecha.left, r.bottom))) return@forEachIndexed
                grupo(k, r, d.ocupado[k], d.derecha.left, xs[k], grupos[k], s)
            }
        }
        val fondo = if (solo == null) b.top else minOf(b.top, solo.bottom + 4f)
        with(ObraEstanteArcontes) {
            d.tablas.forEachIndexed { k, r -> if (toca(r)) tabla(k, r, d, s) }
            val izq = Rect(d.izquierda.left, ch, d.izquierda.right, fondo)
            val der = Rect(d.derecha.left, ch, d.derecha.right, fondo)
            if (toca(izq)) costado(izq, true, d, s)
            if (toca(der)) costado(der, false, d, s)
            d.placas.forEach { p -> if (toca(p.r)) placa(p, medidor, s) }
        }
        with(HuecosEstanteArcontes) { d.repisas.forEachIndexed { k, r -> if (xs[k] >= 0f && toca(r)) llamaHueco(xs[k], r, s) } }
        val m = mensulaEn(xs)
        d.repisas.getOrNull(m)?.let { r -> if (toca(Rect(d.derecha.left, r.top, d.derecha.right, r.bottom))) with(HuecosEstanteArcontes) { mensula(r, d.derecha, s) } }
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireArcontes(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val velas = if (a.lumbre) LucesHuecoArcontes.velas(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yBn = yBraseroNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yBn, v.noche, velas) else 0f
        val rojo = if (a.lumbre) rojez(fx, fy, yBn, v.noche, velas) else 0f
        // De qué lado le llega la luz: de día, de la boca (arriba a la izquierda); de noche, de la luz que más alumbra.
        val kl = lampara(fx - LAMP_X, fy - LAMP_Y, v.noche)
        val kb = brasero(fx - BRASERO_X, fy - yBn, v.noche)
        var kv = 0f; var xv = fx
        var i = 0
        while (i + 1 < velas.size) { val e = vela(fx - velas[i], fy - velas[i + 1], v.noche); if (e > kv) { kv = e; xv = velas[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            a.noche && a.mancha(fx, fy) > maxOf(kl, kb, kv) -> -1f
            kv >= kl && kv >= kb -> if (xv >= fx) 1f else -1f
            kl >= kb -> if (fx < LAMP_X) 1f else -1f
            else -> if (fx < BRASERO_X) 1f else -1f
        }
        // Junto a la grieta que asoma en la primera repisa, su luz fría.
        val fr = if (a.oscuro && v.repisa == 0) 0.75f * exp(-kotlin.math.abs(fx - GRIETA_HUECO_X) / 0.045f) else 0f
        val t = a.en(fx, fy, if (a.oscuro) maxOf(k, 0.05f) else k, rojo, false, fr)
        val tinte = tonoDeDiaArcontes(v.tono, oscuro)
        with(VolumenEstanteArcontes) {
            when (clase(v.semilla)) {
                0 -> codice(v, medidor, t, hacia, tinte)
                // Alguno, ladeado contra el vecino de la izquierda.
                1 -> rollo(v, medidor, t, hacia, tinte, if (azar(v.semilla, 21) > 0.72f) -3.2f else 0f)
                else -> envoltorio(v, medidor, t, hacia, tinte)
            }
        }
    }
}

/**
 * Dónde arden las lámparas de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en su
 * propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro de la lámpara en la misma medida
 * que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoArcontes {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)
    @Volatile private var mensula = -1

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray, m: Int) { centros = c; mensula = m; this.clave = clave }

    /** Las lámparas de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun velas(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val m = MuebleEstanteArcontes
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(m.LADO + c[k] * (1f - 2 * m.LADO))
            l.add(m.CORONA + k * m.FILA / m.ANCHO_NOMINAL + (m.ALTO_REPISA.value * (1f - m.VELA_Y)) / m.ANCHO_NOMINAL)
        }
        if (mensula >= 0) {
            l.add(m.MENSULA_X)
            l.add(m.CORONA + mensula * m.FILA / m.ANCHO_NOMINAL + m.ALTO_REPISA.value * m.MENSULA_Y / m.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}

/**
 * El recorte de la mancha de la boca en el lienzo de ancho [w]: el polígono de [q] con el borde movido como en [AireArcontes.mancha]
 * y agrandado [extra] anchos hacia fuera (para la penumbra). Para lo que se pinta achicado ([s] con escala), en sus medidas.
 */
internal fun caminoManchaArcontes(q: FloatArray, w: Float, ondula: Boolean, extra: Float = 0f, s: SitioArcontes? = null): androidx.compose.ui.graphics.Path {
    val p = androidx.compose.ui.graphics.Path()
    val n = q.size / 2
    val e = s?.e ?: 1f
    val pv = s?.pivote ?: Offset.Zero
    var primero = true
    for (i in 0 until n) {
        val ax = q[2 * i]; val ay = q[2 * i + 1]
        val bx = q[(2 * i + 2) % q.size]; val by = q[(2 * i + 3) % q.size]
        val dx = bx - ax; val dy = by - ay
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(1e-5f)
        // La normal hacia dentro (el polígono va en el sentido de las agujas del reloj): (-dy, dx) / l.
        val nx = -dy / l; val ny = dx / l
        val pasos = if (ondula) (l / 0.012f).toInt().coerceIn(2, 80) else 1
        for (j in 0 until pasos) {
            val t = j / pasos.toFloat()
            val x = ax + dx * t; val y = ay + dy * t
            val o = (if (ondula) 0.007f * kotlin.math.sin(x * 37f + y * 23f) + 0.004f * kotlin.math.sin(x * 91f - y * 53f) else 0f) + extra
            var px = (x - nx * o) * w; var py = (y - ny * o) * w
            if (e != 1f) { px = pv.x + (px - pv.x) / e; py = pv.y + (py - pv.y) / e }
            if (primero) { p.moveTo(px, py); primero = false } else p.lineTo(px, py)
        }
    }
    p.close()
    return p
}

/** Pinta [bloque] fuera de la mancha de la boca y, recortado por ella, otra vez dentro (la luz se quiebra en cada plano). */
internal inline fun DrawScope.conManchaArcontes(s: SitioArcontes, bloque: DrawScope.() -> Unit) {
    val q = s.a.parche
    if (q == null) { bloque(); return }
    s.a.forzar = 0f
    bloque()
    s.a.forzar = 1f
    clipPath(caminoManchaArcontes(q, s.w, true, 0f, s)) { bloque() }
    s.a.forzar = -1f
}

