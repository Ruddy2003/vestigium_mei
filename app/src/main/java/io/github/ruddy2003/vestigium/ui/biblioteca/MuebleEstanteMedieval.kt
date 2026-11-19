package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * La paleta del estante de Scriptorium (sale del cuadro del copista y del almagre, el ocre, el verde y el pergamino del tema):
 * el sillarejo y la cal vieja del muro de la celda del armarius, el nogal pintado del armario, el hierro y la luz. Dos
 * temperaturas: lo cálido es la luz (el sol, el candil, el ocre); lo frío, la sombra de la piedra, el hierro y el verde.
 */
internal object PaletaMedieval {
    /** El sillarejo: la piedra ocre gris, su cara clara, lo hondo, la piedra más ocre y la más gris; el mortero de cal. */
    val PIEDRA = Color(0xFFADA088)
    val PIEDRA_CLARA = Color(0xFFCDC1A6)
    val PIEDRA_OSC = Color(0xFF7F7462)
    val OCRE = Color(0xFFB59563)
    val GRIS = Color(0xFF9A9993)
    val MORTERO = Color(0xFFD5CAB1)
    /** La cal: la del enlucido viejo y la del derrame de la ventana. */
    val CAL = Color(0xFFE8DFCB)
    /** La sombra fría de la piedra y del hierro (gris azulado). */
    val FRIO = Color(0xFF3A4458)
    /** El nogal del armario: su cara, su luz y lo hondo de la talla. */
    val NOGAL = Color(0xFF573A2B)
    val NOGAL_LUZ = Color(0xFF86614A)
    val NOGAL_OSC = Color(0xFF2B1C15)
    /** El roble claro de la prensa y del arca. */
    val ROBLE = Color(0xFF9A774F)
    /** El pino del entarimado. */
    val PINO = Color(0xFF9B7553)
    /** El hierro forjado de flejes, bisagras, clavos y llaves. */
    val HIERRO = Color(0xFF2B2E37)
    val HIERRO_LUZ = Color(0xFF7D818C)
    /** El bronce de la campanilla. */
    val BRONCE = Color(0xFFA7793B)
    /** Los colores de la pintura románica del armario: almagre, ocre, verde y negro, y el blanco de cal de los puntos. */
    val ALMAGRE = Color(0xFF8E4D3B)
    val OCRE_PINTURA = Color(0xFFB09564)
    val VERDE = Color(0xFF5D6B58)
    val NEGRO = Color(0xFF1E1B1C)
    /** El pergamino y su cara amarilla, la tinta y la rúbrica. */
    val PERGAMINO = Color(0xFFEFE3C5)
    val PERG_OSC = Color(0xFFD3BD8F)
    val TINTA = Color(0xFF241710)
    val RUBRICA = Color(0xFF9E2B22)
    /** La badana (cuero curtido al alumbre, sin teñir), el cuero pardo y el lino crudo. */
    val BADANA = Color(0xFFE2D3B2)
    val CUERO = Color(0xFF7A4C2F)
    val LINO = Color(0xFFC9BEA5)
    /** El barro de la alcuza y del pote de cola. */
    val BARRO = Color(0xFFAE6648)
    /** Las ceras de los sellos: la natural, la roja, la verde y la parda. */
    val CERAS = listOf(Color(0xFFC59B4A), Color(0xFF962A22), Color(0xFF4F6A3C), Color(0xFF6E4B2C))
    /** Los cielos de la ventana: el día, la noche y el anochecer (abajo y arriba). */
    val CIELO = Color(0xFFE2ECF2)
    val CIELO_NOCHE = Color(0xFF1B2444)
    val CREPUSCULO = Color(0xFFE39C6A)
    val CREPUSCULO_ALTO = Color(0xFF6C5A8C)
    /** El aire de la noche, el de los Sueños y el anochecer de los Sueños en claro; la llama del candil y la luna. */
    val NOCHE = Color(0xFF110E18)
    val NOCHE_SUENOS = Color(0xFF0E1231)
    val ANOCHECER = Color(0xFF2E2B55)
    val LLAMA = Color(0xFFFFB760)
    val FUEGO = Color(0xFFFFE1A8)
    val SOL = Color(0xFFFFF2D0)
    val LUNA = Color(0xFFE4E8F2)
    /** Lo hondo del tinte: el ánimo se tiñe un poco hacia aquí para que sea cuero o lana y no pastel. */
    val TINTE_HONDO = Color(0xFF231519)

    val LETRA: FontFamily = LetraExtra.cardo
}

/**
 * El aire del momento en el estante de Scriptorium: de día (la luz de la ventana), de noche (el modo oscuro: solo el candil)
 * y en los Sueños ([noche]: al anochecer en claro, la noche más azul en oscuro, con la luna en la ventana). De él salen el
 * color de cada cosa según cuánta luz del candil le llega, la sombra, la luz y el filo.
 */
internal class AireMedieval(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaMedieval
    /** Si arde el candil. */
    val lumbre: Boolean get() = oscuro || noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else SOMBRA_MUEBLE

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, p.ANOCHECER, 0.4f - 0.2f * k), p.LLAMA, 0.1f * k) else c
        return mezcla(mezcla(c, hondo, 0.9f - 0.92f * k.pow(0.85f)), p.LLAMA, 0.3f * k)
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría: la de la piedra, el hierro y el derrame (de noche, la misma que las demás). */
    fun frio(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else p.FRIO.copy(alpha = a.coerceIn(0f, 1f))
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (noche) a * 0.5f else a)
    /** El filo del candil en lo que lo mira; de día no hay. */
    fun filo(a: Float): Color = p.LLAMA.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga viendo en la repisa más lejana. */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(t, p.ANOCHECER, 0.16f) else t
        return mezcla(mezcla(t, hondo, 0.72f - 0.7f * k.pow(0.8f)), p.LLAMA, 0.12f * k)
    }
    /** El pergamino donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(p.PERGAMINO, p.ANOCHECER, 0.12f) else p.PERGAMINO
        return mezcla(mezcla(p.PERGAMINO, hondo, 0.42f - 0.36f * k), p.LLAMA, 0.16f * k)
    }
    val tinta: Color get() = p.TINTA
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoMedieval(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** Un vano de medio punto: de [x0] a [x1], con la clave en [arriba] y el piso en [abajo]. */
internal fun medioPuntoMedieval(x0: Float, x1: Float, arriba: Float, abajo: Float) = Path().apply {
    val r = (x1 - x0) / 2
    moveTo(x0, abajo); lineTo(x0, arriba + r)
    arcTo(Rect(x0, arriba, x1, arriba + 2 * r), 180f, 180f, false)
    lineTo(x1, abajo); close()
}

/** [p] corrido. */
internal fun corridoMedieval(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz. */
internal fun DrawScope.contactoMedieval(centro: Offset, ancho: Float, alto: Float, a: AireMedieval, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * (0.5f - 0.08f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de forja de cabeza gruesa visto de frente: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoMedieval(p: Offset, r: Float, color: Color, a: AireMedieval, filo: Float = 0f) {
    drawCircle(a.frio(0.4f), r * 1.05f, p + Offset(r * 0.35f, r * 0.45f))
    drawCircle(color, r, p)
    drawCircle(a.claro(0.5f), r * 0.36f, p - Offset(r * 0.3f, r * 0.3f))
    if (a.oscuro && filo > 0.05f) drawCircle(a.filo(0.7f * filo), r * 0.4f, p + Offset(r * 0.25f, -r * 0.25f))
}

/**
 * El dibujo del estante del diario de Scriptorium (lo usa `EstanteMedieval`, en `BibliotecaMedievo.kt`): el armarium de nogal
 * pintado de la celda del armarius, en una abadía benedictina hacia 1150, con las dos hojas de la puerta abiertas a los lados,
 * contra un muro de sillarejo con su cal vieja y la ventana abocinada arriba a la izquierda. Todo el dibujo sale de [estante]
 * y de [volumen]; la luz de la noche de los dos sale de la misma curva, [alcance]: la del candil de hierro colgado a la
 * derecha. El muro y la ventana están en [MuroEstanteMedieval], el armario en [ArmarioEstanteMedieval], los objetos en
 * [PiezasEstanteMedieval] y [PrensaEstanteMedieval], y las entradas en [VolumenEstanteMedieval].
 */
internal object MuebleEstanteMedieval {
    private val P = PaletaMedieval

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.56f
    const val BASE = 0.27f
    const val LADO = 0.1f
    val ALTO_REPISA: Dp = 114.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(20)).dp

    /** Cuál de los tres es una entrada: 0, el códice románico; 1, el libro en su camisa; 2, el legajo de cartas selladas. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan unos cuatro quintos de la repisa: el códice, el más alto; el libro en camisa, mediano; el legajo, el más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.84f + 0.08f * azar(semilla, 1)
        1 -> 0.78f + 0.08f * azar(semilla, 1)
        else -> 0.72f + 0.07f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el candil, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.86f
    const val LAMP_Y = 0.27f
    /** El centro de la ventana, en anchos. */
    const val VENT_X = 0.17f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    private const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 132f
    /** En la base: dónde empieza el suelo, en fracción de su alto. */
    const val SUELO = 0.45f

    /**
     * La curva de la luz del candil, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que está
     * a [dx] y [dy] de la llama, en anchos del mueble. Cae deprisa: el candil alumbra el rincón de la derecha y la repisa de
     * arriba; en un estante corto se acaba antes, para que la última repisa quede siempre en penumbra.
     */
    fun alcance(dx: Float, dy: Float, repisas: Int): Float {
        val radio = 0.85f + 0.1f * repisas.coerceIn(2, 5)
        return (1f - sqrt(dx * dx + dy * dy) / radio).coerceIn(0f, 1f).pow(1.3f)
    }

    /** La luz del candil en el muro de arriba: la piedra se la come antes, y el rincón de la ventana queda en lo oscuro. */
    fun alcanceMuro(dx: Float, dy: Float): Float = (1f - sqrt(dx * dx * 1.2f + dy * dy) / 0.82f).coerceIn(0f, 1f).pow(1.2f)

    /** La luz del candil en lo de arriba (el tablero y lo que hay en él): se apaga hacia la ventana algo antes que el cuerpo. */
    fun alcanceArriba(dx: Float, dy: Float): Float = (1f - sqrt(dx * dx * 1.1f + dy * dy) / 1.05f).coerceIn(0f, 1f).pow(1.25f)

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float): Float =
        alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL, repisas)

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireMedieval(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val suelo = b.top + b.height * SUELO
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * u
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        val cerca: (Offset) -> Float = { p ->
            if (p.y <= ch) alcanceArriba((p.x - llama.x) / w, (p.y - llama.y) / w) else cercaCuerpo((p.y - ch) / maxOf(1f, fila), n, p.x / w)
        }
        // Hacia dónde cae la sombra de algo en [p]: de día, a la derecha y abajo; de noche, lejos de la llama.
        val caida: (Offset, Float) -> Offset = { p, largo ->
            if (!oscuro) Offset(largo * 1.15f, largo * 0.8f)
            else { val v = p - llama; v / v.getDistance().coerceAtLeast(1f) * (largo * 2.4f) }
        }
        val gC = w * 0.04f             // el alto de los canecillos
        val gT = w * 0.046f            // el canto del tablero de arriba, con su cenefa
        val fT = w * 0.016f            // lo que se ve de su cara de arriba
        val canto = ch - gC            // donde acaba el canto del tablero
        val apoyo = canto - gT - fT * 0.45f

        with(MuroEstanteMedieval) {
            muro(a, apoyo) { p -> alcanceMuro((p.x - llama.x) / w, (p.y - llama.y) / w) }
            ventana(a)
        }
        if (a.lumbre) {
            val f = if (oscuro) 1f else 0.45f
            mancha(llama, w * 0.62f, P.LLAMA.copy(alpha = 0.2f * f))
            mancha(llama, w * 0.24f, P.FUEGO.copy(alpha = 0.3f * f))
        }
        with(MuroEstanteMedieval) { entarimado(a, suelo, w, h, cerca) }

        with(ArmarioEstanteMedieval) {
            d.repisas.forEachIndexed { k, r -> fondoRepisa(k, r, d.ocupado.getOrNull(k), a, cerca) }
            d.tablas.forEachIndexed { k, r -> tabla(r, 910 + 7 * k, a) { x -> cerca(Offset(x, r.center.y)) } }
            d.placas.forEach { p -> cartela(p, medidor, a, cerca(p.r.center)) }
            costado(d.izquierda, false, d, a, n, ch, fila, suelo)
            costado(d.derecha, true, d, a, n, ch, fila, suelo)
        }
        with(PiezasEstanteMedieval) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                huecoLibre(k, r, oc, a, cerca(Offset((oc + r.right) / 2, r.center.y)))
            }
        }
        with(ArmarioEstanteMedieval) {
            zocalo(Rect(d.izquierda.right, b.top, d.derecha.left, suelo), a, cerca)
            canecillos(Rect(0f, canto, w, ch), a, cerca)
            tablero(Rect(-2f * u, canto - gT, w + 2f * u, canto), fT, a, cerca)
            cabeza(Offset(d.izquierda.right - w * 0.017f, canto - gT - fT), w * 0.034f, a, cerca, caida)
            cabeza(Offset(d.derecha.left + w * 0.017f, canto - gT - fT), w * 0.034f, a, cerca, caida)
        }

        // ---- el rayo de la ventana, lo de arriba, el candil y lo del pie
        with(MuroEstanteMedieval) { rayo(a, apoyo) }
        with(PiezasEstanteMedieval) {
            arriba(a, apoyo, cerca, caida)
            candil(a)
            alPie(a, suelo, h, d, cerca)
            if (a.lumbre) llamaCandil(a)
        }
        with(MuroEstanteMedieval) { polvo(a, apoyo) }
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireMedieval(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, v.repisas, fx) else 1f
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, del candil.
        val luzDe = if (a.oscuro && fx > LAMP_X) -1f else if (a.oscuro) 1f else -1f
        with(VolumenEstanteMedieval) {
            when (clase(v.semilla)) {
                0 -> codice(v, medidor, a, k, luzDe)
                1 -> camisa(v, medidor, a, k, luzDe)
                else -> legajo(v, medidor, a, k, luzDe)
            }
        }
    }
}
