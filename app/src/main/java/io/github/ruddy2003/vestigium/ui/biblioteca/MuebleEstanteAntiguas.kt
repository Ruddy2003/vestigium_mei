package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * El dibujo del estante del diario de Tierras Antiguas (lo usa `EstanteAntiguas`, en `BibliotecaFantasia.kt`): el armario de la
 * crónica de la sala de arriba de la posada del paso. Roble ahumado de carpintero de montaña: la cornisa volada con su bocel,
 * sus dentellones y el filete dorado sobre un friso de casetones con una gema de talla cuadrada en cada uno; los costados como
 * pilastras de tablero rehundido, con una cadena de rombos tallados y una roseta de latón con su gema a la altura de cada tabla;
 * las tablas gruesas de canto moldurado con clavos de latón; el zócalo de rombos sobre pies de bollo. Contra el enlucido de
 * lapislázuli con su sembrado de oro viejo, bajo la cenefa de las fases de la luna y la viga del techo. A la izquierda, en
 * escorzo, la ventana honda de vidrios en rombo emplomados: de día su sol cae en la pared con la trama de los plomos; de noche
 * arden la vela de las lunas arriba, la arandela de la pilastra de la izquierda en la repisa de en medio, un candil en cada
 * hueco con sitio y, al pie, el brasero de cobre. La paleta y el aire del momento están en [PaletaAntiguas] y [AireAntiguas]; la
 * pared y el suelo, en [MuroEstanteAntiguas]; la ventana y su paño de luz, en `MuebleEstanteAntiguasVentana.kt`; el mueble, en
 * [ObraEstanteAntiguas]; el bodegón de arriba, en [CoronaEstanteAntiguas]; lo de los huecos, en [HuecosEstanteAntiguas]; el pie,
 * en [PieEstanteAntiguas]; las entradas, en [VolumenEstanteAntiguas].
 */
internal object MuebleEstanteAntiguas {
    private val P = PaletaAntiguas
    val LETRA: FontFamily = P.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteAntiguas` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.36f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 114.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    /**
     * Más ancho cuanto más escribiste: crece rápido en las entradas cortas (con la raíz de la cuenta) y se llena hacia las 90
     * palabras; unos dp de más o de menos según la cuenta misma, para que la repisa no sea una empalizada.
     */
    fun ancho(palabras: Int): Dp {
        val crece = (1.6f * sqrt(palabras.coerceAtLeast(0).toFloat())).coerceAtMost(15f)
        return (28f + crece + Math.floorMod(palabras * 37 + 11, 5)).dp
    }

    /** Cuál de los tres es una entrada: 0, el tomo de la crónica; 1, el rollo de varillas; 2, el envoltorio de cartas. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla xor 0x2c71, 53)
        return if (x < 0.46f) 0 else if (x < 0.75f) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el tomo, el más alto; el rollo; el envoltorio, algo más bajo. */
    fun alto(semilla: Int, palabras: Int): Float {
        val lleno = (palabras / 800f).coerceIn(0f, 1f)
        return when (clase(semilla)) {
            0 -> 0.82f + 0.04f * lleno + 0.09f * azar(semilla, 1)
            1 -> 0.79f + 0.04f * lleno + 0.09f * azar(semilla, 1)
            else -> 0.76f + 0.04f * lleno + 0.07f * azar(semilla, 1)
        }
    }

    // ---------------------------------------------------------------- las medidas de dentro (en anchos del mueble)

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 136f
    /** La cornisa: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas, en anchos. */
    const val CORNISA = 0.118f
    /** En la base: el zócalo con sus pies, en anchos desde su arranque (de ahí abajo, el suelo). */
    const val ZOCALO = 0.12f
    /** Dónde termina el muro del costado (la esquina con la pared del fondo), en anchos. */
    const val ESQUINA = 0.13f
    /** El punto de fuga del escorzo de la ventana, en anchos. */
    const val FUGA_X = 0.5f
    const val FUGA_Y = 0.46f

    // ---------------------------------------------------------------- la luz

    /** La llama de la vela de las lunas, en anchos desde la esquina de arriba a la izquierda. */
    const val VELA_X = 0.672f
    const val VELA_Y = 0.292f
    /** El fuego del brasero: a lo ancho, y su altura sobre el pie del lienzo, en anchos. */
    const val BRASERO_X = 0.8f
    const val BRASERO_Y = 0.11f
    /** El candil de un hueco: a qué alto de su repisa arde (en fracción del hueco, desde abajo). */
    const val CANDIL_Y = 0.5f
    /**
     * La arandela del costado de la izquierda (la vela de leer del cronista, en su brazo de hierro): a lo ancho, y a qué alto de
     * la repisa de en medio arde (en fracción del hueco, desde arriba).
     */
    const val ARANDELA_X = 0.043f
    const val ARANDELA_F = 0.3f

    /** La repisa de en medio de un estante de [repisas] repisas, donde va la arandela. */
    fun repisaArandela(repisas: Int): Int = ((repisas - 1) / 2).coerceAtLeast(0)

    /** La altura de la llama de la arandela, en anchos, en un estante de [repisas] repisas (la que usan los volúmenes). */
    fun yArandelaNominal(repisas: Int): Float =
        CORONA + repisaArandela(repisas) * FILA / ANCHO_NOMINAL + ALTO_REPISA.value * ARANDELA_F / ANCHO_NOMINAL

    /** La luz de la arandela: alumbra los tramos de en medio, sobre todo los de la izquierda. */
    fun arandela(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.62f else 1.0f) * exp(-3.7f * sqrt(dx * dx + dy * dy * 1.25f))).coerceIn(0f, 1f)

    /** La luz de la vela: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos. En los Sueños arde baja. */
    fun vela(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 1.05f else 1.6f) * exp(-(if (suenos) 2.9f else 2.3f) * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.82f else 1f)))).coerceIn(0f, 1f)

    /** La luz de la vela en la pared y en lo de arriba: más corta que en el cuerpo, para que lo lejano se hunda. */
    fun velaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 1.05f else 1.6f) * exp(-(if (suenos) 4.6f else 4.1f) * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.03f, 1f)

    /** La luz del candil de un hueco: corta, para sus vecinos, el fondo y el canto de la tabla de arriba. */
    fun candil(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.6f else 0.95f) * exp(-5.4f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** La luz del brasero: lo de abajo, encendido; se apaga hacia arriba. En los Sueños, brasas. */
    fun brasero(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.9f else 1.4f) * exp(-2.9f * sqrt(dx * dx * 1.25f + dy * dy * (if (dy < 0f) 1.1f else 0.8f)))).coerceIn(0f, 1f)

    /** El candil que más alumbra ([x], [y]) entre [luces] (x, y, x, y… en anchos). */
    private fun candilMayor(x: Float, y: Float, luces: FloatArray, suenos: Boolean): Float {
        var m = 0f
        var i = 0
        while (i + 1 < luces.size) { m = maxOf(m, candil(x - luces[i], y - luces[i + 1], suenos)); i += 2 }
        return m
    }

    /** Dónde arde el brasero, a lo alto y en anchos, en un estante de [total] anchos de alto. */
    fun yBrasero(total: Float): Float = total - BRASERO_Y

    /**
     * Cuánta luz cálida llega a ([x], [y]) (anchos): la vela, el brasero a la altura [yB], la arandela a la altura [yA] y las
     * [luces] de los huecos.
     */
    fun calor(x: Float, y: Float, yB: Float, yA: Float, suenos: Boolean, luces: FloatArray): Float {
        val kv = vela(x - VELA_X, y - VELA_Y, suenos)
        val kb = brasero(x - BRASERO_X, y - yB, suenos)
        val ka = arandela(x - ARANDELA_X, y - yA, suenos)
        val kl = candilMayor(x, y, luces, suenos)
        return (1f - (1f - kv) * (1f - kb) * (1f - ka) * (1f - kl)).coerceIn(0.03f, 1f)
    }

    /** El alto total del estante en anchos, con [repisas] repisas (para que cada volumen sepa dónde está el brasero). */
    fun totalNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE

    /**
     * Dónde va la luz del hueco de una repisa (el candil, en x del lienzo), o null si no hay sitio para el grupo con el candil.
     * La usan el hueco (para dibujarlo) y el estante (para alumbrar con él).
     */
    fun xCandil(oc: Float, xDer: Float, w: Float): Float? {
        val x0 = oc + w * 0.015f; val x1 = xDer - w * 0.02f
        return if (x1 - x0 >= w * 0.2f) x0 + w * 0.085f else null
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireAntiguas(oscuro, noche)
        val am = AireAntiguas(oscuro, noche, mueble = true)
        val ch = d.corona.bottom
        val b = d.base
        val n = d.repisas.size
        val yB = (h / w) - BRASERO_Y
        val yA = if (n > 0) d.repisas[repisaArandela(n)].let { (it.top + it.height * ARANDELA_F) / w } else -9f
        // Los candiles de los huecos: uno en cada hueco con sitio. Se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(n) { -1f }
        val lucesL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            val x = xCandil(oc, d.derecha.left, w) ?: return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            lucesL.add(x / w); lucesL.add((r.bottom - r.height * CANDIL_Y) / w)
        }
        val luces = if (a.lumbre) lucesL.toFloatArray() else FloatArray(0)
        LucesHuecoAntiguas.poner(LucesHuecoAntiguas.clave(n, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (!oscuro) (0.35f * vela(p.x / w - VELA_X, p.y / w - VELA_Y, true)).coerceIn(0f, 1f)
            else if (p.y < ch) velaArriba(p.x / w - VELA_X, p.y / w - VELA_Y, noche)
            else calor(p.x / w, p.y / w, yB, yA, noche, luces)
        }
        val g = MarcoAntiguas(w, h, ch, d.izquierda.right, d.derecha.left, b.top, n, yA * w, a, am, cerca)
        val pano = panoAntiguas(a)
        val clave = "${w.toInt()}:$ch:${b.top}:$h:$oscuro:$noche:$density"

        // Lo de arriba: la pared, la ventana, la cornisa, lo colgado y el bodegón, en una imagen que llega al píxel entero de
        // debajo de la corona. Lo grande (la pared, la cornisa) se pinta con la luz entera y encima va su mapa de luz; lo que
        // está delante (los vidrios, lo colgado, el bodegón) se pinta después, cada cosa con la luz que le llega.
        with(CapaAntiguas) {
            pegar("c:$clave", ceil(ch).toInt()) {
                // Debajo de todo, un fondo opaco: ninguna junta de lo que se pinta encima deja la imagen transparente.
                drawRect(am.dia(P.ROBLE_OSC, 0f, 1f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteAntiguas) { pared(g) }
                ventanaAntiguas(g)
                with(ObraEstanteAntiguas) { cornisa(g) }
                with(MuroEstanteAntiguas) { nocheArriba(g) }
                vidrioAntiguas(g)
                pano?.let { panoParedAntiguas(it, g); panoCornisaAntiguas(it, g) }
                with(CoronaEstanteAntiguas) { colgado(g); encima(g, pano) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        if (n == 0) return
        // El cuerpo: los casilleros, las tablas y los costados, con la luz entera; encima, su mapa de luz (la noche, el ocaso
        // o la sombra fría del día), el paño de la ventana, lo de los huecos con su luz, las llamas y los rótulos.
        val fondo = d.tablas.last().bottom
        with(ObraEstanteAntiguas) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, d, g) }
            d.tablas.forEachIndexed { k, r -> tabla(k, r, g) }
            costado(Rect(d.izquierda.left, ch, d.izquierda.right, fondo), true, d, g)
            costado(Rect(d.derecha.left, ch, d.derecha.right, fondo), false, d, g)
            luzCuerpo(d, g, luces)
        }
        pano?.let { panoCuerpoAntiguas(it, d, g) }
        with(HuecosEstanteAntiguas) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, g)
            }
            d.repisas.forEachIndexed { k, r -> llama(k, r, d, g) }
        }
        with(ObraEstanteAntiguas) { llamaArandela(g, fondo) }
        with(ObraEstanteAntiguas) { d.placas.forEach { p -> placa(p, medidor, g) } }
        granoMueble(Rect(0f, ch, w, fondo), oscuro, 0.8f)
        // Lo de abajo: el zócalo, el suelo y lo que hay en él, desde el píxel entero de encima de la última tabla (debajo, opaco y
        // del color del zócalo: así la fila de la junta, que puede caer a medio píxel, no deja ver el fondo). Se pinta en cada
        // dibujo y no en una imagen guardada: con la corona guardada ya se llega a los 2.5 MB de una pantalla de 1080 px.
        val arriba = floor(minOf(b.top, fondo))
        drawRect(am.dia(P.ROBLE, 0f, 1f), Offset(0f, arriba), Size(w, h - arriba))
        with(MuroEstanteAntiguas) { suelo(g) }
        with(ObraEstanteAntiguas) { zocalo(g, arriba) }
        with(PieEstanteAntiguas) { alPie(g, arriba) }
        granoMueble(Rect(0f, arriba, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireAntiguas(oscuro, v.noche, mueble = true)
        val fx = LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val total = totalNominal(v.repisas)
        val yB = yBrasero(total)
        val yA = yArandelaNominal(v.repisas)
        // Los candiles de los huecos, tal como los apuntó el estante (si no las hay, solo la vela y el brasero).
        val luces = if (a.lumbre) LucesHuecoAntiguas.luces(v.repisas, oscuro, v.noche) else FloatArray(0)
        val k = when {
            !a.lumbre -> 1f
            !oscuro -> (0.35f * vela(fx - VELA_X, fy - VELA_Y, true)).coerceIn(0f, 1f)
            else -> calor(fx, fy, yB, yA, v.noche, luces)
        }
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, de la luz que más alumbra.
        val kv = vela(fx - VELA_X, fy - VELA_Y, v.noche)
        val kb = brasero(fx - BRASERO_X, fy - yB, v.noche)
        val ka = arandela(fx - ARANDELA_X, fy - yA, v.noche)
        var kl = 0f; var xl = fx
        var i = 0
        while (i + 1 < luces.size) { val e = candil(fx - luces[i], fy - luces[i + 1], v.noche); if (e > kl) { kl = e; xl = luces[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kl >= kv && kl >= kb && kl >= ka -> if (xl >= fx) 1f else -1f
            ka >= kv && ka >= kb -> -1f
            kv >= kb -> if (fx < VELA_X) 1f else -1f
            else -> if (fx < BRASERO_X) 1f else -1f
        }
        // El paño de la ventana (el ocaso o la luna de los Sueños) en el canto de arriba de la entrada.
        val pano = panoAntiguas(AireAntiguas(oscuro, v.noche))
        val fz = pano?.en(fx, CORONA + (v.repisa * FILA + ALTO_REPISA.value * 0.2f) / ANCHO_NOMINAL, PanoAntiguas.FRENTE) ?: 0f
        val f = if (a.oscuro) maxOf(lunaVentana(fx, fy, v.noche), fz) else 0f
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val tinte = materialAntiguas(v.tono, oscuro)
        with(VolumenEstanteAntiguas) {
            when (clase(v.semilla)) {
                0 -> tomo(v, medidor, a, kd, f, hacia, tinte, fz, pano)
                1 -> rollo(v, medidor, a, kd, f, hacia, tinte, fz, pano)
                else -> envoltorio(v, medidor, a, kd, f, hacia, tinte, fz, pano)
            }
        }
    }

    /** El roce frío de la luna de la ventana en ([x], [y]), en anchos: arriba a la izquierda; en los Sueños, más largo. */
    fun lunaVentana(x: Float, y: Float, suenos: Boolean): Float {
        val dx = x - 0.05f; val dy = y - 0.3f
        return ((if (suenos) 0.95f else 0.55f) * exp(-(if (suenos) 1.9f else 2.8f) * sqrt(dx * dx + dy * dy * 0.7f))).coerceIn(0f, 1f)
    }
}

/**
 * Las medidas que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los costados ([x0], [x1]), el arranque de la base ([base]), cuántas repisas hay ([n]) y a qué alto arde la arandela
 * ([yArandela]); el aire de la pared ([a]) y el del mueble ([am]), y cuánta luz cálida llega a cada punto ([cerca]).
 */
internal class MarcoAntiguas(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val base: Float, val n: Int,
                             val yArandela: Float, val a: AireAntiguas, val am: AireAntiguas, val cerca: (Offset) -> Float) {
    /** Donde pisa lo de arriba: la tapa de la cornisa. */
    val apoyo: Float get() = ch - w * MuebleEstanteAntiguas.CORNISA
    /** Donde acaba el zócalo con sus pies y empieza el suelo. */
    val suelo: Float get() = base + w * MuebleEstanteAntiguas.ZOCALO
    /** La esquina con la pared del costado, en píxeles. */
    val esquina: Float get() = w * MuebleEstanteAntiguas.ESQUINA
    /** Dónde arde la vela de las lunas, en píxeles. */
    val vela: Offset get() = Offset(w * MuebleEstanteAntiguas.VELA_X, w * MuebleEstanteAntiguas.VELA_Y)
    /** Dónde arde el brasero, en píxeles. */
    val brasero: Offset get() = Offset(w * MuebleEstanteAntiguas.BRASERO_X, h - w * MuebleEstanteAntiguas.BRASERO_Y)
    /** Dónde arde la arandela del costado, en píxeles. */
    val arandela: Offset get() = Offset(w * MuebleEstanteAntiguas.ARANDELA_X, yArandela)
}

/**
 * Lo fijo de arriba del estante (la pared, la ventana, la cornisa y el bodegón), pintado una vez por tamaño y modo en una imagen
 * guardada que luego se pega. La imagen llega hasta el píxel entero de debajo de la corona, así la junta con el cuerpo no deja
 * asomar el fondo. Se guarda solo la última (el diario y los Sueños se turnan la misma), unos 2.5 MB con 1080 px de ancho.
 */
internal object CapaAntiguas {
    private var clave: String? = null
    private var imagen: ImageBitmap? = null

    /** Pega la imagen de las filas 0 a [abajo] (enteras) de todo el ancho; la pinta antes con [pintar] si cambió. */
    fun DrawScope.pegar(clave: String, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt()
        if (ancho < 1 || abajo < 1) return
        var img = imagen
        if (img == null || this@CapaAntiguas.clave != clave) {
            val nueva = ImageBitmap(ancho, abajo)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), abajo.toFloat())) { pintar() }
            imagen = nueva; this@CapaAntiguas.clave = clave; img = nueva
        }
        drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset.Zero, IntSize(img.width, img.height))
    }
}

/**
 * Dónde arden los candiles de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después,
 * en su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro del candil en la
 * misma medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoAntiguas {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Los candiles de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun luces(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val m = MuebleEstanteAntiguas
        val l = ArrayList<Float>()
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(m.LADO + c[k] * (1f - 2 * m.LADO))
            l.add(m.CORONA + k * m.FILA / m.ANCHO_NOMINAL + (m.ALTO_REPISA.value * (1f - m.CANDIL_Y)) / m.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoAntiguas(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoAntiguas(centro: Offset, ancho: Float, alto: Float, a: AireAntiguas, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo o remache de cabeza redonda: su sombra, la cabeza ([metal]) y el punto de luz. */
internal fun DrawScope.remacheAntiguas(p: Offset, r: Float, a: AireAntiguas, k: Float, metal: Color = PaletaAntiguas.HIERRO) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.6f, k), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el latón, el vidrio o la gema (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloAntiguas(c: Offset, r: Float, a: AireAntiguas, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.9f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaAntiguas.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaAntiguas.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroAntiguas(r: Rect, a: AireAntiguas, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.3f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.24f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Las escalas del ánimo de Tierras Antiguas, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_ANTIGUAS: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.ANTIGUAS, false) to juegoTonos(TemaId.ANTIGUAS, true) }

private fun cuadradoAntiguas(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaAntiguas(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoAntiguas(t.red - a0.red - dr * f) + cuadradoAntiguas(t.green - a0.green - dg * f) + cuadradoAntiguas(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los cueros del ánimo, de bajo a alto, en el orden de la escala del tema (rubí, piedra clara, esmeralda) llevado a lo que tiñe
 * el curtidor del paso: el cordobán rojo de rubí, la vitela cruda sin teñir y el cuero verde de esmeralda. Los puntos de en medio
 * van cerca de su extremo, para que nada salga en pastel.
 */
private val CUEROS_ANTIGUAS = listOf(
    0f to Color(0xFF6E1420), 0.3f to Color(0xFFA62A30), 0.44f to Color(0xFFC79A74), 0.5f to Color(0xFFE2D4AE),
    0.56f to Color(0xFFAFBF8E), 0.7f to Color(0xFF3F8A5C), 1f to Color(0xFF17573A),
)

/**
 * El cuero de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [CUEROS_ANTIGUAS]; si
 * no está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores (el ánimo de en medio pasa de la
 * piedra clara a un casi negro): aquí la entrada es un objeto de la sala, así que primero se vuelve al color de día (el mismo
 * punto de la escala de noche en la de día) y después la luz dibujada lo apaga con la distancia. Sin ánimo, null.
 */
internal fun materialAntiguas(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_ANTIGUAS
    val x = (if (oscuro) enEscalaAntiguas(t, noche) else null) ?: enEscalaAntiguas(t, dia) ?: return t
    for (i in 0 until CUEROS_ANTIGUAS.size - 1) {
        val (f0, c0) = CUEROS_ANTIGUAS[i]; val (f1, c1) = CUEROS_ANTIGUAS[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return CUEROS_ANTIGUAS.last().second
}
