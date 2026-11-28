package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
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
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Lo fijo de arriba del estante de Abismo (la pared, el ojo de buey, el bao, la cornisa y el bodegón), pintado una vez por tamaño
 * y modo en una imagen guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de la corona, sobre un fondo opaco,
 * así la junta con el cuerpo no deja asomar el fondo. Se guarda solo la última (cambia de tamaño o de modo muy pocas veces).
 */
internal object CapaAbismo {
    private val claves = arrayOfNulls<String>(1)
    private val imagenes = arrayOfNulls<ImageBitmap>(1)

    /** Pega la imagen de las filas [arriba] a [abajo] (enteras) de todo el ancho; la pinta antes con [pintar] si cambió. */
    fun DrawScope.pegar(ranura: Int, clave: String, arriba: Int, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = abajo - arriba
        if (ancho < 1 || alto < 1 || ranura !in imagenes.indices) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                translate(0f, -arriba.toFloat()) { pintar() }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave; img = nueva
        }
        drawImage(img, Offset(0f, arriba.toFloat()))
    }
}

/**
 * El dibujo del estante del diario de Abismo (lo usa `EstanteAbismo`, en `BibliotecaContemporanea.kt`): el armario de las
 * bitácoras del patrón, en el mismo pañol de buceo del barco esponjero que el Ideario (`IdeasAbismo`), en la otra pared: el
 * costado del barco, con el mismo forro de tablas pintadas de blanco roto. Es teca, como la estantería del Ideario: la cornisa
 * con el cordón tallado, los costados gruesos, las tablas con su barandilla de mar de latón, las cantoneras y los pernos al casco,
 * el zócalo con su chapa. Arriba, el ojo de buey a la izquierda (de día, el sol de la mañana y el reverbero del agua en el bao; de
 * noche, el mar con la luz viva), el bao con su curva, la tubería de cobre de la bomba y, encima del armario, la bomba de aire de
 * dos volantes trincada a la balda, con el cabo de vida, el mirafondos, la caracola y la aceitera; colgados, la red de las
 * esponjas y el farol. Al pie, sobre la cubierta calafateada, el cofre de marinero con el sombrero de hule, el anafe de barro con
 * el cazo de café y la garrafa de mimbre. De noche arden el farol, el farolillo de un hueco y las brasas del anafe. La paleta y
 * la luz están en [PaletaAbismo] y [AireAbismo]; la pared, el ojo de buey y lo colgado, en [MuroEstanteAbismo]; el farol, en
 * [FarolEstanteAbismo]; el mueble, en
 * [ObraEstanteAbismo]; el bodegón, en [CoronaEstanteAbismo]; lo de los huecos, en [HuecosEstanteAbismo]; el pie, en
 * [PieEstanteAbismo]; las entradas, en [VolumenEstanteAbismo].
 */
internal object MuebleEstanteAbismo {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteAbismo` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.32f
    val LADO = 0.075f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el cuaderno de bitácora; 1, el petate de lona; 2, los rollos de cartas. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 11)) * 31 + (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** La bitácora, la más alta; el petate, blando, algo menos; los rollos, altos y desparejos. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.84f + 0.08f * azar(semilla, 1)
        1 -> 0.8f + 0.08f * azar(semilla, 1)
        else -> 0.86f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el farol colgado del bao, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val FAROL_X = 0.84f
    const val FAROL_Y = 0.255f
    /** Dónde está el anafe: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val LUMBRE_X = 0.56f
    const val LUMBRE_Y = 0.66f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 132f
    /** La cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.085f
    /** En la base: dónde acaba el zócalo y empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.3f
    /** El farolillo de un hueco: a qué alto arde (en fracción del hueco, desde abajo). */
    const val CANDIL_Y = 0.42f

    /** El farol: cuánta luz llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos del mueble. */
    fun farol(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.45f else 1.65f) * exp(-3.2f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.62f else 0.9f)))).coerceIn(0f, 1f)

    /** El farol en la pared y en lo de arriba: más corto que en el cuerpo, para que lo lejano se hunda. */
    fun farolArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.45f else 1.5f) * exp(-3.5f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.02f, 1f)

    /**
     * Las brasas del anafe, al pie: bajas y anaranjadas, salen por la boca del anafe hacia delante y hacia arriba; lo que queda muy
     * por encima (las repisas de en medio) recibe bastante menos.
     */
    fun lumbre(dx: Float, dy: Float, suenos: Boolean): Float {
        val arriba = (-dy / 0.12f).coerceIn(0f, 1f)
        val tapa = 1f - 0.5f * arriba * arriba * (3f - 2f * arriba)
        return ((if (suenos) 0.65f else 1.5f) * exp(-3.1f * sqrt(dx * dx * 1.2f + dy * dy)) * tapa).coerceIn(0f, 1f)
    }

    /** El farolillo de un hueco: para sus vecinos, el fondo, el costado de al lado y el canto de las tablas de arriba y de abajo. */
    fun candil(dx: Float, dy: Float): Float = (1.45f * exp(-4.2f * sqrt(dx * dx + dy * dy * 1.5f))).coerceIn(0f, 1f)

    /** El farolillo que más alumbra ([x], [y]) entre [candiles] (x, y, x, y… en anchos). */
    private fun candilMayor(x: Float, y: Float, candiles: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < candiles.size) { m = maxOf(m, candil(x - candiles[i], y - candiles[i + 1])); i += 2 }
        return m
    }

    /** Cuánta luz cálida llega a ([x], [y]) (anchos): el farol, las brasas a la altura [yLumbre] y los [candiles]. */
    fun calor(x: Float, y: Float, yLumbre: Float, suenos: Boolean, candiles: FloatArray): Float {
        val kf = farol(x - FAROL_X, y - FAROL_Y, suenos)
        val kl = lumbre(x - LUMBRE_X, y - yLumbre, suenos)
        val kc = candilMayor(x, y, candiles) * (if (suenos) 0.65f else 1f)
        return (1f - (1f - kf) * (1f - kl) * (1f - kc)).coerceIn(0.03f, 1f)
    }

    /** Dónde arden las brasas, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yLumbreNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * LUMBRE_Y

    /**
     * Dónde va el farolillo de cada repisa (en x del lienzo), o -1 si no lleva. Arde en los huecos con sitio, pero nunca en dos
     * repisas seguidas ni en la primera (ahí alumbra el farol). Lo usan el estante (para alumbrar) y los huecos.
     */
    fun candilesEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.02f; val x1 = d.derecha.left - w * 0.025f
            if (k == 0 || x1 - x0 < w * 0.17f || (k > 0 && xs[k - 1] >= 0f)) continue
            // El farolillo va hacia el fondo del hueco, junto al costado de la derecha.
            xs[k] = x1 - w * 0.06f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireAbismo(oscuro, noche)
        val ac = AireAbismo(oscuro, noche, cuerpo = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yL = (b.top + b.height * LUMBRE_Y) / w
        // Los farolillos de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(d.repisas.size) { -1f }
        val candL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        val xs = candilesEn(d, w)
        d.repisas.forEachIndexed { k, r ->
            val x = xs[k]
            if (x < 0f) return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            candL.add(x / w); candL.add((r.bottom - r.height * CANDIL_Y) / w)
        }
        val candiles = if (a.lumbre) candL.toFloatArray() else FloatArray(0)
        LucesHuecoAbismo.poner(LucesHuecoAbismo.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < apoyo - w * 0.03f) farolArriba(p.x / w - FAROL_X, p.y / w - FAROL_Y, noche)
            else calor(p.x / w, p.y / w, yL, noche, candiles)
        }
        val cercaArriba: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < apoyo - w * 0.03f) farolArriba(p.x / w - FAROL_X, p.y / w - FAROL_Y, noche)
            else calor(p.x / w, p.y / w, yL, noche, FloatArray(0))
        }
        val m = manchaAbismo(a)
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, el ojo de buey, el bao, la cornisa y el bodegón, en una imagen que llega al píxel entero de debajo.
        with(CapaAbismo) {
            pegar(0, "c:$clave", 0, ceil(ch).toInt()) {
                drawRect(a.luz(PaletaAbismo.TECA_OSC, 0.5f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteAbismo) { pared(a, apoyo, cercaArriba, m) }
                with(MuroEstanteAbismo) { colgado(a, apoyo, cercaArriba, m) }
                with(ObraEstanteAbismo) { cornisa(Rect(0f, apoyo, w, ch + 2f), a, cercaArriba, m) }
                with(CoronaEstanteAbismo) { encima(a, apoyo, cercaArriba, m) }
                with(FarolEstanteAbismo) { farolYLuz(a, apoyo, cercaArriba) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, las tablas y los costados, sobre un fondo opaco de teca de píxel entero, para que las juntas a
        // medio píxel entre casillero, tabla y costado no dejen asomar lo de detrás.
        val c0 = ceil(ch); val c1 = ceil(b.top)
        if (c1 > c0) drawRect(ac.luz(PaletaAbismo.TECA_OSC, if (a.lumbre) 0.3f else 1f), Offset(0f, c0), Size(ceil(w), c1 - c0))
        with(ObraEstanteAbismo) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, ac, cerca) }
        }
        with(HuecosEstanteAbismo) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xs[k], ac, cerca)
            }
        }
        with(ObraEstanteAbismo) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, ac, cerca) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, ac, cerca)
            costado(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, ac, cerca)
            d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center)) }
        }
        with(HuecosEstanteAbismo) { d.repisas.forEachIndexed { k, r -> if (xs[k] >= 0f) llamaHueco(xs[k], r, ac) } }
        // Lo de abajo, en vivo: el zócalo y el suelo con lo que hay en él, desde el píxel entero de encima del zócalo y sobre un
        // fondo opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(ac.luz(PaletaAbismo.TECA_OSC, cerca(Offset(w / 2, b.top))), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val suelo = b.top + b.height * SUELO
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yL, noche, FloatArray(0)) }
        with(PieEstanteAbismo) { suelo(ac, b, suelo, h, cercaPie) }
        with(ObraEstanteAbismo) { zocalo(Rect(0f, arriba, w, suelo), ac, cercaPie) }
        with(PieEstanteAbismo) { objetos(ac, b, suelo, h, cercaPie) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireAbismo(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val candiles = if (a.lumbre) LucesHuecoAbismo.candiles(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yLn = yLumbreNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yLn, v.noche, candiles) else 1f
        // De qué lado le llega la luz: de día, del ojo de buey (izquierda); de noche, de la luz que más alumbra.
        val kf = farol(fx - FAROL_X, fy - FAROL_Y, v.noche)
        val kl = lumbre(fx - LUMBRE_X, fy - yLn, v.noche)
        var kc = 0f; var xc = fx
        var i = 0
        while (i + 1 < candiles.size) { val e = candil(fx - candiles[i], fy - candiles[i + 1]); if (e > kc) { kc = e; xc = candiles[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kc >= kf && kc >= kl -> if (xc >= fx) 1f else -1f
            kf >= kl -> if (fx < FAROL_X) 1f else -1f
            else -> if (fx < LUMBRE_X) 1f else -1f
        }
        val luna = lunaCuerpoAbismo(a, fx, fy)
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val tinte = tonoDeDiaAbismo(v.tono, oscuro)
        with(VolumenEstanteAbismo) {
            when (clase(v.semilla)) {
                // Alguna bitácora, ladeada contra la vecina de la izquierda.
                0 -> if (azar(v.semilla, 21) > 0.8f) rotate(-3.2f, Offset(0f, size.height)) { cuaderno(v, medidor, a, kd, hacia, tinte, luna) }
                    else cuaderno(v, medidor, a, kd, hacia, tinte, luna)
                1 -> petate(v, medidor, a, kd, hacia, tinte, luna)
                else -> rollos(v, medidor, a, kd, hacia, tinte, luna)
            }
        }
    }
}

/**
 * Dónde arden los farolillos de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en
 * su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro del farolillo en la misma
 * medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoAbismo {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Los farolillos de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguno si no se apuntaron. */
    fun candiles(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val lado = MuebleEstanteAbismo.LADO
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(lado + c[k] * (1f - 2 * lado))
            l.add(MuebleEstanteAbismo.CORONA + k * MuebleEstanteAbismo.FILA / MuebleEstanteAbismo.ANCHO_NOMINAL +
                (MuebleEstanteAbismo.ALTO_REPISA.value * (1f - MuebleEstanteAbismo.CANDIL_Y)) / MuebleEstanteAbismo.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
