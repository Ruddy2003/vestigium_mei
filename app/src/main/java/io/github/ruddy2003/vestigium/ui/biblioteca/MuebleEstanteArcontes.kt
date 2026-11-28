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

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la lámpara de encima del armario, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.8f
    val LAMP_Y get() = CORONA - CORNISA - 0.088f
    /** Dónde arde el brasero del pie: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val BRASERO_X = 0.5f
    const val BRASERO_Y = 0.76f
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

    /** La luz de la lámpara de arriba en el cuerpo: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos. */
    fun lampara(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.9f else 1.7f) * exp(-4.6f * sqrt(dx * dx * 1.05f + dy * dy * (if (dy > 0f) 0.6f else 1f)))).coerceIn(0f, 1f)

    /** La luz de la lámpara de arriba en la pared y en lo de arriba: más corta, para que lo lejano se hunda. */
    fun lamparaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.5f) * exp(-5f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.02f, 1f)

    /** La luz del brasero del pie: baja y roja, para las repisas de abajo. */
    fun brasero(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.4f) * exp(-4.2f * sqrt(dx * dx * 1.1f + dy * dy))).coerceIn(0f, 1f)

    /** La luz de la lámpara de un hueco: corta, para sus vecinos, la roca del fondo y el canto de las tablas. */
    fun vela(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.8f else 1.35f) * exp(-6.2f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** La lámpara de hueco que más alumbra ([x], [y]) entre [velas] (x, y, x, y… en anchos). */
    private fun velaMayor(x: Float, y: Float, velas: FloatArray, suenos: Boolean): Float {
        var m = 0f
        var i = 0
        while (i + 1 < velas.size) { m = maxOf(m, vela(x - velas[i], y - velas[i + 1], suenos)); i += 2 }
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
        val apoyo = ch - w * CORNISA
        val yB = (b.top + b.height * BRASERO_Y) / w
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
        val velas = if (a.lumbre) velasL.toFloatArray() else FloatArray(0)
        LucesHuecoArcontes.poner(LucesHuecoArcontes.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 0f
            else if (p.y < ch) lamparaArriba(p.x / w - LAMP_X, p.y / w - LAMP_Y, noche)
            else calor(p.x / w, p.y / w, yB, noche, velas)
        }
        val brasa: (Offset) -> Float = { p -> if (!a.lumbre || p.y < ch) 0f else rojez(p.x / w, p.y / w, yB, noche, velas) }
        val s = SitioArcontes(a, w, cerca, brasa)
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, el techo, lo colgado, el bodegón y la cornisa, en una imagen que llega al píxel entero de debajo.
        with(CapaArcontes) {
            pegar("c:$clave", 0, ceil(ch).toInt()) {
                with(RocaEstanteArcontes) { pared(s, Rect(0f, 0f, w, ch + 2f), apoyo) }
                with(CoronaEstanteArcontes) { colgado(s, apoyo) }
                with(ObraEstanteArcontes) { cornisa(Rect(0f, apoyo, w, ch + 2f), s) }
                with(CoronaEstanteArcontes) { encima(s, apoyo) }
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
        val q = a.parche
        if (q != null) a.forzar = 0f
        cuerpo(d, s, xs, grupos, medidor, d.repisas.size - 1)
        if (q != null) {
            var bajo = 0f
            var i = 1
            while (i < q.size) { bajo = maxOf(bajo, q[i]); i += 2 }
            val hasta = d.repisas.indexOfLast { it.top < (bajo + 0.03f) * w }
            if (hasta >= 0) {
                a.forzar = 1f
                clipPath(caminoManchaArcontes(q, w, true)) { cuerpo(d, s, xs, grupos, medidor, hasta) }
            }
            a.forzar = -1f
        }
        // Lo de abajo, en vivo: el zócalo y el suelo con lo que hay en él, desde el píxel entero de encima del zócalo y sobre un
        // fondo opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(s.t(Offset(w / 2, b.top), true).c(PaletaArcontes.CALIZA_SOMBRA), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val suelo = b.top + b.height * SUELO
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 0f else calor(p.x / w, p.y / w, yB, noche, FloatArray(0)) }
        val brasaPie: (Offset) -> Float = { p -> if (!a.lumbre) 0f else rojez(p.x / w, p.y / w, yB, noche, FloatArray(0)) }
        val sp = SitioArcontes(a, w, cercaPie, brasaPie)
        with(PieEstanteArcontes) { suelo(sp, b, suelo, h) }
        with(ObraEstanteArcontes) { zocalo(Rect(0f, arriba, w, suelo), sp) }
        with(PieEstanteArcontes) { objetos(sp, b, suelo, h) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    /** El cuerpo, de la repisa de arriba a la [hasta]: los huecos, lo que hay en ellos, las tablas, los costados y los rótulos. */
    private fun DrawScope.cuerpo(d: DisposicionEstante, s: SitioArcontes, xs: FloatArray, grupos: IntArray, medidor: TextMeasurer, hasta: Int) {
        val ch = d.corona.bottom
        val b = d.base
        with(ObraEstanteArcontes) { d.repisas.forEachIndexed { k, r -> if (k <= hasta) hueco(k, r, d, s) } }
        with(HuecosEstanteArcontes) {
            d.repisas.forEachIndexed { k, r ->
                if (k > hasta || grupos[k] < 0) return@forEachIndexed
                grupo(k, r, d.ocupado[k], d.derecha.left, xs[k], grupos[k], s)
            }
        }
        val fin = d.tablas.getOrNull(hasta)?.bottom ?: b.top
        with(ObraEstanteArcontes) {
            d.tablas.forEachIndexed { k, r -> if (k <= hasta) tabla(k, r, d, s) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, s)
            costado(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, s)
            d.placas.forEach { p -> if (p.r.top < fin) placa(p, medidor, s) }
        }
        with(HuecosEstanteArcontes) { d.repisas.forEachIndexed { k, r -> if (k <= hasta && xs[k] >= 0f) llamaHueco(xs[k], r, s) } }
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
        val t = a.en(fx, fy, if (a.oscuro) maxOf(k, 0.05f) else k, rojo)
        val tinte = tonoDeDiaArcontes(v.tono, oscuro)
        with(VolumenEstanteArcontes) {
            when (clase(v.semilla)) {
                0 -> codice(v, medidor, t, hacia, tinte)
                // Alguno, ladeado contra el vecino de la izquierda.
                1 -> if (azar(v.semilla, 21) > 0.72f) rotate(-3.2f, Offset(0f, size.height)) { rollo(v, medidor, t, hacia, tinte) }
                    else rollo(v, medidor, t, hacia, tinte)
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

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

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
        return l.toFloatArray()
    }
}

/** El recorte de la mancha de la boca en el lienzo de ancho [w]: el polígono de [q] con el borde movido como en [AireArcontes.mancha]. */
internal fun caminoManchaArcontes(q: FloatArray, w: Float, ondula: Boolean): androidx.compose.ui.graphics.Path {
    val p = androidx.compose.ui.graphics.Path()
    val n = q.size / 2
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
            val o = if (ondula) 0.007f * kotlin.math.sin(x * 37f + y * 23f) + 0.004f * kotlin.math.sin(x * 91f - y * 53f) else 0f
            val px = (x - nx * o) * w; val py = (y - ny * o) * w
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
    clipPath(caminoManchaArcontes(q, s.w, true)) { bloque() }
    s.a.forzar = -1f
}

