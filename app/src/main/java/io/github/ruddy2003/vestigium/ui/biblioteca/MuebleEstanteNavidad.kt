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
 * Lo fijo de arriba del estante de Navidad (la pared, la ventana, la cornisa y el bodegón), pintado una vez por tamaño y modo en
 * una imagen guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de la corona, sobre un fondo opaco, así la
 * junta con el cuerpo no deja asomar el fondo. Se guarda solo la última (cambia de tamaño o de modo muy pocas veces).
 */
internal object CapaNavidad {
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
 * El dibujo del estante del diario de Navidad (lo usa `EstanteNavidad`, en `BibliotecaContemporanea.kt`): la alacena de las cartas
 * de la cocina de la casa, la misma cocina del Ideario (`IdeasNavidad`) en el otro lienzo, junto a la cocina de hierro. Es una
 * alacena abierta empotrada, de pino pintado de rojo de almagre con los filetes verdes de la misma mano que el aparador, gastada en
 * los cantos; con su cornisa, su friso con la guirnalda de acebo pintada, los costados gruesos y el zócalo recortado. Contra la
 * pared de la cocina (la pintura verdosa, la cenefa verde y el azulejo crema), con la ventana arriba a la izquierda (de día, el
 * sol de la tarde con la trama de sus vidrios; de noche, la nieve de afuera). Encima, el abeto en maceta con sus esferas y la tira
 * de bombillas, las tarjetas, el regalo, el molde de cobre, la palmatoria y la jarra de acebo; al pie, sobre el barro y la
 * alfombra trenzada, la esquina de la cocina de hierro con el hervidor, el cesto de leña y el taburete con la labor. De noche
 * arden la vela de la cornisa, la de un hueco y la lumbre de la cocina. La paleta y la luz están en [PaletaNavidad] y
 * [AireNavidad]; la pared y la ventana, en [MuroEstanteNavidad]; el mueble, en [ObraEstanteNavidad]; el bodegón, en
 * [CoronaEstanteNavidad]; lo de los huecos, en [HuecosEstanteNavidad]; el pie, en [PieEstanteNavidad]; las entradas, en
 * [VolumenEstanteNavidad].
 */
internal object MuebleEstanteNavidad {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteNavidad` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.34f
    val LADO = 0.075f
    val ALTO_REPISA: Dp = 116.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el álbum de las tarjetas; 1, el regalo; 2, el calcetín de lana. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 11)) * 31 + (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /**
     * El álbum, el más alto; el regalo, con su lazo, algo menos; el calcetín cuelga de su gancho bajo la tabla de arriba, así que
     * su lienzo llega arriba del todo (el calcetín llena unos nueve décimos).
     */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.85f + 0.07f * azar(semilla, 1)
        1 -> 0.8f + 0.08f * azar(semilla, 1)
        else -> 1f
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la vela de la cornisa, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val VELA_X = 0.815f
    const val VELA_Y = 0.36f
    /** El eje del abeto y la altura media de sus bombillas. */
    const val ARBOL_X = 0.57f
    const val ARBOL_Y = 0.3f
    /** Dónde está la lumbre de la cocina de hierro: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val LUMBRE_X = 0.13f
    const val LUMBRE_Y = 0.66f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 136f
    /** La cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.1f
    /** En la base: dónde acaba el zócalo y empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.3f
    /** La vela de un hueco: a qué alto arde (en fracción del hueco, desde abajo). */
    const val CANDIL_Y = 0.61f

    /** La vela de la cornisa: cuánta luz llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos del mueble. */
    fun vela(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.8f else 1.65f) * exp(-3.3f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.5f else 0.9f)))).coerceIn(0f, 1f)

    /** La vela en la pared y en lo de arriba: más corta que en el cuerpo, para que lo lejano se hunda. */
    fun velaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.5f) * exp(-3.8f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.02f, 1f)

    /** Las bombillas del abeto: una luz chica y tibia alrededor del árbol. */
    fun arbol(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.3f else 0.5f) * exp(-4.8f * sqrt(dx * dx * 1.4f + dy * dy * 0.8f))).coerceIn(0f, 1f)

    /**
     * La lumbre de la cocina de hierro, al pie: baja y anaranjada, sale por la rejilla hacia delante y hacia abajo; lo que queda por
     * encima de la puerta del fogón (el zócalo, las repisas de abajo) recibe bastante menos, porque la tapa de la cocina la tapa.
     */
    fun lumbre(dx: Float, dy: Float, suenos: Boolean): Float {
        val arriba = (-dy / 0.1f).coerceIn(0f, 1f)
        val tapa = 1f - 0.6f * arriba * arriba * (3f - 2f * arriba)
        return ((if (suenos) 0.85f else 1.6f) * exp(-3f * sqrt(dx * dx * 1.2f + dy * dy)) * tapa).coerceIn(0f, 1f)
    }

    /** La vela de un hueco: para sus vecinos, el fondo, el costado de al lado y el canto de las tablas de arriba y de abajo. */
    fun candil(dx: Float, dy: Float): Float = (1.5f * exp(-4.2f * sqrt(dx * dx + dy * dy * 1.5f))).coerceIn(0f, 1f)

    /** La vela de hueco que más alumbra ([x], [y]) entre [candiles] (x, y, x, y… en anchos). */
    private fun candilMayor(x: Float, y: Float, candiles: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < candiles.size) { m = maxOf(m, candil(x - candiles[i], y - candiles[i + 1])); i += 2 }
        return m
    }

    /** Cuánta luz cálida llega a ([x], [y]) (anchos): la vela de la cornisa, el abeto, la lumbre a la altura [yLumbre] y las [candiles]. */
    fun calor(x: Float, y: Float, yLumbre: Float, suenos: Boolean, candiles: FloatArray): Float {
        val kv = vela(x - VELA_X, y - VELA_Y, suenos)
        val ka = arbol(x - ARBOL_X, y - ARBOL_Y, suenos)
        val kl = lumbre(x - LUMBRE_X, y - yLumbre, suenos)
        val kc = candilMayor(x, y, candiles)
        return (1f - (1f - kv) * (1f - ka) * (1f - kl) * (1f - kc)).coerceIn(0.03f, 1f)
    }

    /** Dónde arde la lumbre, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yLumbreNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * LUMBRE_Y

    /**
     * Dónde va la vela de hueco de cada repisa (en x del lienzo), o -1 si no lleva. Arde en los huecos con sitio, pero nunca en dos
     * repisas seguidas ni en la primera (ahí alumbra la vela de la cornisa). Lo usan el estante (para alumbrar) y los huecos.
     */
    fun candilesEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.02f; val x1 = d.derecha.left - w * 0.025f
            if (k == 0 || x1 - x0 < w * 0.17f || (k > 0 && xs[k - 1] >= 0f)) continue
            // La vela va al fondo del hueco, junto al costado de la derecha: enciende el bastidor y lo que tiene al lado.
            xs[k] = x1 - w * 0.05f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireNavidad(oscuro, noche)
        val ac = AireNavidad(oscuro, noche, cuerpo = true)
        // La madera roja de la alacena: se hunde en vino, con sombras y cantos fríos del verde azulado de la cocina.
        val am = AireNavidad(oscuro, noche, cuerpo = true, madera = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yL = (b.top + b.height * LUMBRE_Y) / w
        // Las velas de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
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
        LucesHuecoNavidad.poner(LucesHuecoNavidad.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < ch) maxOf(velaArriba(p.x / w - VELA_X, p.y / w - VELA_Y, noche), arbol(p.x / w - ARBOL_X, p.y / w - ARBOL_Y, noche))
            else calor(p.x / w, p.y / w, yL, noche, candiles)
        }
        val m = manchaNavidad(a)
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, la ventana, la cornisa y el bodegón, en una imagen que llega al píxel entero de debajo.
        with(CapaNavidad) {
            pegar(0, "c:$clave", 0, ceil(ch).toInt()) {
                drawRect(a.luz(PaletaNavidad.ALMAGRE_OSC, 0.5f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteNavidad) { pared(a, apoyo, cerca, m) }
                with(MuroEstanteNavidad) { colgado(a, apoyo, cerca, m) }
                with(ObraEstanteNavidad) { cornisa(Rect(0f, apoyo, w, ch + 2f), AireNavidad(oscuro, noche, madera = true), cerca, m) }
                with(CoronaEstanteNavidad) { encima(a, apoyo, ch, cerca, m) }
                with(MuroEstanteNavidad) { luzDeNoche(a, ch) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, las tablas y los costados.
        with(ObraEstanteNavidad) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, ac, am, cerca) }
        }
        with(HuecosEstanteNavidad) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xs[k], ac, cerca)
            }
        }
        with(ObraEstanteNavidad) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, d, am, ac, cerca) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, am, cerca)
            costado(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, am, cerca)
            d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center)) }
        }
        with(HuecosEstanteNavidad) { d.repisas.forEachIndexed { k, r -> if (xs[k] >= 0f) llamaHueco(xs[k], r, ac) } }
        // Lo de abajo, en vivo: el zócalo y el suelo con lo que hay en él, desde el píxel entero de encima del zócalo y sobre un
        // fondo opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(am.luz(PaletaNavidad.ALMAGRE_OSC, cerca(Offset(w / 2, b.top))), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val suelo = b.top + b.height * SUELO
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yL, noche, FloatArray(0)) }
        with(PieEstanteNavidad) { suelo(ac, b, suelo, h, cercaPie) }
        with(ObraEstanteNavidad) { zocalo(Rect(0f, arriba, w, suelo), am, cercaPie) }
        with(PieEstanteNavidad) { objetos(ac, b, suelo, h, cercaPie) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireNavidad(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val candiles = if (a.lumbre) LucesHuecoNavidad.candiles(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yLn = yLumbreNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yLn, v.noche, candiles) else 1f
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, de la luz que más alumbra.
        val kv = vela(fx - VELA_X, fy - VELA_Y, v.noche)
        val kl = lumbre(fx - LUMBRE_X, fy - yLn, v.noche)
        var kc = 0f; var xc = fx
        var i = 0
        while (i + 1 < candiles.size) { val e = candil(fx - candiles[i], fy - candiles[i + 1]); if (e > kc) { kc = e; xc = candiles[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kc >= kv && kc >= kl -> if (xc >= fx) 1f else -1f
            kv >= kl -> if (fx < VELA_X) 1f else -1f
            else -> if (fx < LUMBRE_X) 1f else -1f
        }
        // La luna de los Sueños: cuánto le toca a su frente.
        val luna = lunaCuerpoNavidad(a, fx, fy)
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val tinte = tonoDeDiaNavidad(v.tono, oscuro)
        with(VolumenEstanteNavidad) {
            when (clase(v.semilla)) {
                // Alguno, ladeado contra el vecino de la izquierda.
                0 -> if (azar(v.semilla, 21) > 0.8f) rotate(-3.2f, Offset(0f, size.height)) { album(v, medidor, a, kd, hacia, tinte, luna) }
                    else album(v, medidor, a, kd, hacia, tinte, luna)
                1 -> regalo(v, medidor, a, kd, hacia, tinte, luna)
                else -> calcetin(v, medidor, a, kd, hacia, tinte, luna)
            }
        }
    }
}

/**
 * Dónde arden las velas de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en su
 * propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro de la vela en la misma medida que
 * [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoNavidad {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Las velas de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun candiles(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val lado = MuebleEstanteNavidad.LADO
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(lado + c[k] * (1f - 2 * lado))
            l.add(MuebleEstanteNavidad.CORONA + k * MuebleEstanteNavidad.FILA / MuebleEstanteNavidad.ANCHO_NOMINAL +
                (MuebleEstanteNavidad.ALTO_REPISA.value * (1f - MuebleEstanteNavidad.CANDIL_Y)) / MuebleEstanteNavidad.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
