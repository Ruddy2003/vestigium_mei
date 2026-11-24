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
 * Lo fijo de arriba del estante (la pared, la ventana, la cornisa y el bodegón), pintado una vez por tamaño y modo en una imagen
 * guardada que luego se pega. Va de la fila 0 a la fila entera de debajo de la corona, sobre un fondo opaco, así la junta con
 * el cuerpo no deja asomar el fondo. Se guarda solo la última (cambia de tamaño o de modo muy pocas veces). El pie se pinta en
 * vivo.
 */
internal object CapaNapoleon {
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
 * El dibujo del estante del diario de Napoleónico (lo usa `EstanteNapoleon`, en `BibliotecaContemporanea.kt`): la librería de
 * campaña de un oficial del estado mayor en 1812, armada con sus cajas de caoba apiladas en el cuarto de una casa señorial de
 * Lituania requisada para el alojamiento. Cada repisa es una caja con sus cantoneras de latón y su forro de paño verde; arriba,
 * la caja-cornisa con su friso flameado y su filete de latón; abajo, la caja-zócalo con sus patas de bollo. Contra el enlucido
 * pintado de amarillo Imperio con su friso verde de palmetas, en la esquina con el muro de la ventana (de día, la trama de sus
 * cuarterones en la pared; de noche, el cielo de afuera). Encima, el globo terrestre, los atlas, la lámpara bouillotte, el
 * nécessaire, la botella y la trompeta colgada; al pie, en el parqué de espiga, la silla de montar, el farol de hojalata calada
 * y el cesto de leña. De noche arden la bouillotte, el candelero de un hueco y el farol. La paleta y la luz están en
 * [PaletaNapoleon] y [AireNapoleon]; la pared y la ventana, en [MuroEstanteNapoleon]; el mueble, en [ObraEstanteNapoleon]; el
 * bodegón, en [CoronaEstanteNapoleon]; lo de los huecos, en [HuecosEstanteNapoleon]; el pie, en [PieEstanteNapoleon]; las
 * entradas, en [VolumenEstanteNapoleon].
 */
internal object MuebleEstanteNapoleon {
    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteNapoleon` las lee al dibujar)

    val CORONA = 0.56f
    val BASE = 0.3f
    val LADO = 0.075f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el tomo; 1, la caja de archivo; 2, el ridículo de seda. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 11)) * 31 + (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el tomo, el más alto; la caja; el ridículo, más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.85f + 0.07f * azar(semilla, 1)
        1 -> 0.82f + 0.06f * azar(semilla, 1)
        else -> 0.8f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arden las velas de la bouillotte (bajo su pantalla), en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.645f
    const val LAMP_Y = 0.335f
    /** Dónde está el farol del pie: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val FAROL_X = 0.5f
    const val FAROL_Y = 0.6f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 134f
    /** La caja-cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.1f
    /** En la base: dónde acaba el zócalo y empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.3f
    /** El candelero de un hueco: a qué alto arde (en fracción del hueco, desde abajo). */
    const val CANDIL_Y = 0.72f

    /**
     * La luz de la bouillotte: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de las velas, en anchos del mueble. La pantalla
     * de chapa echa la luz hacia abajo: por encima de su borde llega mucho menos.
     */
    fun lampara(dx: Float, dy: Float, suenos: Boolean): Float {
        val base = (if (suenos) 0.85f else 1.6f) * exp(-2.8f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.48f else 1f)))
        return (base * pantalla(dx, dy)).coerceIn(0f, 1f)
    }

    /** Cuánto sube el borde de la pantalla sobre las llamas y cuánto mide su radio, en anchos. */
    const val PANTALLA_ALTO = 0.03f
    const val PANTALLA_RADIO = 0.088f

    /**
     * Lo que deja pasar la pantalla de chapa de la bouillotte: nada por encima de la recta que va de las llamas al borde de la
     * pantalla (la luz solo sale hacia abajo y rasante a los lados), con una penumbra que se abre con la distancia; lo de la
     * sombra recibe un poco, del rebote del cuarto.
     */
    fun pantalla(dx: Float, dy: Float): Float {
        val m = PANTALLA_ALTO / PANTALLA_RADIO
        val sobre = -dy - m * kotlin.math.abs(dx)
        if (sobre <= 0f) return 1f
        val pen = 0.025f + 0.24f * kotlin.math.abs(dx)
        val t = (sobre / pen).coerceIn(0f, 1f)
        return 1f - 0.8f * t * t * (3f - 2f * t)
    }

    /** La luz de la bouillotte en la pared y en lo de arriba: más corta que en el cuerpo, para que lo lejano se hunda. */
    fun lamparaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.75f else 1.5f) * exp(-3.6f * sqrt(dx * dx + dy * dy * 1.1f)) * pantalla(dx, dy)).coerceIn(0.02f, 1f)

    /** La luz del farol del pie: baja y a puntos, para las repisas de abajo. */
    fun farol(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.85f else 1.4f) * exp(-2.2f * sqrt(dx * dx * 1.15f + dy * dy))).coerceIn(0f, 1f)

    /** La luz del candelero de un hueco: corta, para sus vecinos, el fondo y el canto de la tabla de arriba. */
    fun candil(dx: Float, dy: Float): Float = (1.25f * exp(-3.7f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** El candelero que más alumbra ([x], [y]) entre [candiles] (x, y, x, y… en anchos). */
    private fun candilMayor(x: Float, y: Float, candiles: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < candiles.size) { m = maxOf(m, candil(x - candiles[i], y - candiles[i + 1])); i += 2 }
        return m
    }

    /** Cuánta luz cálida llega a ([x], [y]) (anchos): la bouillotte, el farol a la altura [yFarol] y las [candiles]. */
    fun calor(x: Float, y: Float, yFarol: Float, suenos: Boolean, candiles: FloatArray): Float {
        val kl = lampara(x - LAMP_X, y - LAMP_Y, suenos)
        val kb = farol(x - FAROL_X, y - yFarol, suenos)
        val kc = candilMayor(x, y, candiles)
        return (1f - (1f - kl) * (1f - kb) * (1f - kc)).coerceIn(0.03f, 1f)
    }

    /** Dónde arde el farol, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yFarolNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * FAROL_Y

    /**
     * Dónde va el candelero del hueco de cada repisa (en x del lienzo), o -1 si no lleva. Arde en los huecos con sitio, pero
     * nunca en dos repisas seguidas ni en la primera (ahí alumbra la bouillotte). Lo usan el estante (para alumbrar) y los huecos.
     */
    fun candilesEn(d: DisposicionEstante, w: Float): FloatArray {
        val xs = FloatArray(d.repisas.size) { -1f }
        for (k in d.repisas.indices) {
            val oc = d.ocupado.getOrNull(k) ?: continue
            val x0 = oc + w * 0.02f; val x1 = d.derecha.left - w * 0.025f
            if (k == 0 || x1 - x0 < w * 0.17f || (k > 0 && xs[k - 1] >= 0f)) continue
            xs[k] = x0 + w * 0.05f
        }
        return xs
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireNapoleon(oscuro, noche)
        val ac = AireNapoleon(oscuro, noche, cuerpo = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yF = (b.top + b.height * FAROL_Y) / w
        // Los candeleros de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
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
        LucesHuecoNapoleon.poner(LucesHuecoNapoleon.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < ch) lamparaArriba(p.x / w - LAMP_X, p.y / w - LAMP_Y, noche)
            else calor(p.x / w, p.y / w, yF, noche, candiles)
        }
        val m = manchaNapoleon(a)
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, la ventana, la cornisa y el bodegón, en una imagen que llega al píxel entero de debajo.
        with(CapaNapoleon) {
            pegar(0, "c:$clave", 0, ceil(ch).toInt()) {
                drawRect(a.luz(PaletaNapoleon.CAOBA_OSC, 0.5f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteNapoleon) { pared(a, apoyo, cerca, m) }
                with(CoronaEstanteNapoleon) { colgado(a, apoyo, cerca, m) }
                with(ObraEstanteNapoleon) { cornisa(Rect(0f, apoyo, w, ch + 2f), a, cerca, m) }
                with(CoronaEstanteNapoleon) { encima(a, apoyo, ch, cerca, m) }
                with(MuroEstanteNapoleon) { luzDeNoche(a, ch) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, las tablas y los costados.
        with(ObraEstanteNapoleon) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, d, ac, cerca) }
        }
        with(HuecosEstanteNapoleon) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xs[k], ac, cerca)
            }
        }
        with(ObraEstanteNapoleon) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, d, ac, cerca) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, d, ac, cerca)
            costado(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, d, ac, cerca)
            d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center)) }
        }
        with(HuecosEstanteNapoleon) { d.repisas.forEachIndexed { k, r -> if (xs[k] >= 0f) llamaHueco(xs[k], r, ac) } }
        // Lo de abajo, en vivo: el zócalo y el suelo con lo que hay en él, desde el píxel entero de encima del zócalo y sobre un
        // fondo opaco, para que la junta con la última tabla no deje asomar nada.
        val arriba = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        drawRect(ac.luz(PaletaNapoleon.CAOBA_OSC, cerca(Offset(w / 2, b.top))), Offset(0f, arriba), Size(w, ceil(h) - arriba + 1f))
        val suelo = b.top + b.height * SUELO
        val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yF, noche, FloatArray(0)) }
        with(PieEstanteNapoleon) { suelo(ac, b, suelo, h, cercaPie) }
        with(ObraEstanteNapoleon) { zocalo(Rect(0f, arriba, w, suelo), ac, cercaPie) }
        with(PieEstanteNapoleon) { objetos(ac, b, suelo, h, cercaPie) }
        granoMueble(Rect(0f, ch, w, h), oscuro, 0.8f)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireNapoleon(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val candiles = if (a.lumbre) LucesHuecoNapoleon.candiles(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yFn = yFarolNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yFn, v.noche, candiles) else 1f
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, de la luz que más alumbra.
        val kl = lampara(fx - LAMP_X, fy - LAMP_Y, v.noche)
        val kb = farol(fx - FAROL_X, fy - yFn, v.noche)
        var kc = 0f; var xc = fx
        var i = 0
        while (i + 1 < candiles.size) { val e = candil(fx - candiles[i], fy - candiles[i + 1]); if (e > kc) { kc = e; xc = candiles[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kc >= kl && kc >= kb -> if (xc >= fx) 1f else -1f
            kl >= kb -> if (fx < LAMP_X) 1f else -1f
            else -> if (fx < FAROL_X) 1f else -1f
        }
        // La luna de los Sueños: cuánto le toca a su frente.
        val luna = lunaCuerpoNapoleon(a, fx, fy)
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val tinte = tonoDeDiaNapoleon(v.tono, oscuro)
        with(VolumenEstanteNapoleon) {
            when (clase(v.semilla)) {
                // Alguno, ladeado contra el vecino de la izquierda.
                0 -> if (azar(v.semilla, 21) > 0.8f) rotate(-3.4f, Offset(0f, size.height)) { tomo(v, medidor, a, kd, hacia, tinte, luna) }
                    else tomo(v, medidor, a, kd, hacia, tinte, luna)
                1 -> caja(v, medidor, a, kd, hacia, tinte, luna)
                else -> ridiculo(v, medidor, a, kd, hacia, tinte, luna)
            }
        }
    }
}

/**
 * Dónde arden los candeleros de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después,
 * en su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro del candelero en la
 * misma medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoNapoleon {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Los candeleros de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun candiles(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val lado = MuebleEstanteNapoleon.LADO
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(lado + c[k] * (1f - 2 * lado))
            l.add(MuebleEstanteNapoleon.CORONA + k * MuebleEstanteNapoleon.FILA / MuebleEstanteNapoleon.ANCHO_NOMINAL +
                (MuebleEstanteNapoleon.ALTO_REPISA.value * (1f - MuebleEstanteNapoleon.CANDIL_Y)) / MuebleEstanteNapoleon.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
