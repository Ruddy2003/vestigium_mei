package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Terminal
// =====================================================================

/**
 * Terminal: el rincón de las máquinas en el cuarto de quien vive en la terminal, de madrugada, hacia finales de los 80. Es un
 * interior sin ventana y siempre de noche (el tema solo existe en oscuro): una pared pintada de azul pizarra con un panel
 * perforado de aglomerado, cortado por el borde izquierdo, y contra ella una estantería metálica de equipo. El bastidor es de
 * acero pintado de beis gastado, con los rieles negros perforados a los lados de cada bahía, la rejilla de ventilación
 * arriba, un mazo de cable plano gris que cuelga del costado izquierdo, un cable enrollado colgado a la derecha y, en el panel
 * de abajo, la regleta con su interruptor rojo encendido; el zócalo negro con sus patas pisa el linóleo. Encima, descentrado a
 * la izquierda, el monitor de tubo beis amarillento girado en tres cuartos (se ve su costado y la joroba del tubo), con una
 * nota adhesiva en el bisel y en la pantalla de fósforo cian el árbol de directorios, una fila en vídeo inverso y el cursor:
 * es el punto focal y la luz del cuadro. A su izquierda, un flexo de brazo apagado que sube por encima del monitor; a la
 * derecha, el teclado apoyado de canto contra la pared, el módem con su fila de pilotos (unos encendidos, otros no) y su cable
 * en espiral, una lata de refresco sin marca y una pila de archivadores de anillas con la taza y sus cercos de café. En el
 * panel, los cables sujetos con bridas, los auriculares colgados de un gancho y dos notas amarillas con garabatos; en la
 * pared lisa, una hoja de papel continuo con un listado clavada con una chincheta. Al pie, una torre tumbada sin su tapa
 * (la placa verde, la jaula de discos, el piloto de encendido) y una caja de cartón con cables, cortadas por los bordes.
 * Cada tema es una bahía del bastidor con sus orejas atornilladas a los rieles y, según la semilla, una bandeja de disco
 * extraíble (asa, cerradura de llave, piloto verde fijo, ámbar de actividad y una barra de diez segmentos que se enciende
 * según cuánto guarda), una bandeja de alambre con una pila de listados de rayas verdes (más alta cuanto más guarda) o una
 * caja de cartuchos de cinta de pie con sus etiquetas escritas a mano (uno por ficha, hasta seis); el tema va en una cinta de
 * rotular en relieve. Lo que no tiene tema va en una caja de cartón con disquetes sueltos sin etiquetar; una bahía sin tema
 * lleva su tapa ciega y un piloto apagado. Al abrir una, la bandeja sale hacia delante y su piloto de actividad se enciende.
 * La luz: el fósforo del monitor baña el teclado, el panel y la primera fila, y cada fila hacia abajo se hunde en su propio
 * color oscuro (el beis, hacia un pardo azulado); el interruptor rojo deja un charco corto en la última fila y en el suelo, y
 * los pilotos brillan con su halo. Por dentro, la pantalla negra con sus líneas de barrido y los números de línea. Las fichas
 * son archivos en su ventana, con el nombre en la barra de título.
 */
object IdeasCiber : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del cuarto
    /** La noche del cuarto: negro azulado. */
    private val NOCHE = Color(0xFF060A12)
    /** La pintura azul pizarra de la pared y el aglomerado pardo del panel perforado. */
    private val PARED = Color(0xFF34405A)
    private val TABLERO = Color(0xFF8A6E4C)
    /** El plástico y la chapa beis amarillentos de las máquinas y del bastidor, y su sombra. */
    private val BEIS = Color(0xFFDCCDA2)
    private val BEIS_OSC = Color(0xFFA8956A)
    /** El negro de los rieles, del zócalo y de los cables. */
    private val NEGRO_RACK = Color(0xFF262A33)
    private val ACERO = Color(0xFF8C939E)
    private val ACERO_OSC = Color(0xFF4E5560)
    /** El fósforo cian (el del tema) y su brillo. */
    private val CIAN = Color(0xFF00E5FF)
    private val CIAN_CLARO = Color(0xFFBFF8FF)
    /** Los pilotos. */
    private val VERDE = Color(0xFF5CFF8A)
    private val AMBAR = Color(0xFFFFB84F)
    private val ROJO = Color(0xFFFF4A3D)
    /** Las notas adhesivas, el papel continuo y sus rayas verdes. */
    private val NOTA = Color(0xFFEBD65A)
    private val PAPEL = Color(0xFFE8E4D2)
    private val RAYA = Color(0xFFA6D2A0)
    /** El gris del cable plano y el café de la taza. */
    private val CABLE = Color(0xFF9EA09B)
    private val CAFE = Color(0xFF5A3822)

    // ---------------------------------------------------------------- de la paleta de siempre (el interior y las fichas)
    private val NEGRO = Color(0xFF030708)
    private val CIAN_OSC = Color(0xFF0A6E7A)
    private val TEXTO = Color(0xFFBDEFF5)
    private val TENUE = Color(0xFF3E7880)
    private val MAGENTA = Color(0xFFFF4F8B)

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 1.3f
    override val corona = 0.64f
    override val base = 0.27f
    override val lado = 0.09f
    override val hueco = 0.05f

    /** En la corona: el fondo de la tapa del bastidor, su canto de frente y lo bajo del canto, en fracción de su alto. */
    private const val SUP = 0.865f
    private const val FRENTE = 0.905f
    private const val CANTO = 0.95f
    /** En la base: el rincón de la pared con el suelo, donde pisa el bastidor, y donde pisan las cosas del pie. */
    private const val SUELO = 0.36f
    private const val PISAN = 0.56f
    private const val PIE = 0.97f
    /** Los costados del bastidor, en fracción del ancho. */
    private const val IZQ = 0.035f
    private const val DER = 0.965f
    /** Cuánto sale la bandeja al abrirla, en fracción del alto de la bahía. */
    private const val SALIDA = 0.06f

    // ---------------------------------------------------------------- la luz (siempre de noche)

    /** Dónde está la pantalla del monitor, en fracciones del ancho. */
    private val MONITOR = Offset(0.335f, 0.36f)

    /** Cuánto le llega del fósforo a un punto [p] (en fracciones del ancho): cae con la distancia. */
    private fun luzMonitor(p: Offset) = (1.12f * (1f - (p - MONITOR).getDistance() / 1.2f)).coerceIn(0f, 1f)
    /** Y del interruptor rojo de la regleta ([r], en fracciones): un charco corto. */
    private fun luzRegleta(p: Offset, r: Offset) = (1f - (p - r).getDistance() / 0.34f).coerceIn(0f, 1f)

    /** El color propio de algo, hundido: oscuro pero de su mismo tono (el beis, a un pardo azulado), no gris ni negro plano. */
    private fun hondo(c: Color) = Color(c.red * 0.17f + 0.004f, c.green * 0.15f + 0.008f, c.blue * 0.15f + 0.026f, c.alpha)

    /** Cuánto le llega a algo del monitor ([km]) y de la regleta ([kr]), y de qué lado le viene la luz ([izq]: desde la izquierda). */
    private class Tono(val km: Float, val kr: Float, val izq: Boolean)

    private fun Tono.c(col: Color): Color {
        val k = (0.66f * km.pow(1.45f) + 0.34f * kr.pow(1.3f)).coerceIn(0f, 1f)
        val v = mezcla(hondo(col), col, k)
        return mezcla(mezcla(v, CIAN, 0.12f * km), ROJO, 0.2f * kr)
    }

    /** La sombra: la noche del cuarto. */
    private fun Tono.s(a: Float) = NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))

    /** El filo de lo que mira a la luz: cian del fósforo o rojo de la regleta; un poco siempre, para que lo negro no se pierda. */
    private fun Tono.b(a: Float): Color {
        val k = maxOf(km, kr * 0.8f)
        val col = if (kr * 0.8f > km) mezcla(ROJO, AMBAR, 0.25f) else CIAN_CLARO
        return col.copy(alpha = (a * (0.14f + 0.86f * k)).coerceIn(0f, 1f))
    }

    /** Lo que el mueble que se está pintando sabe: su ancho y dónde quedó la regleta (en fracciones del ancho). */
    private var anchoW = 1f
    private var regleta = Offset(0.13f, 3f)

    private fun tn(p: Offset): Tono {
        val f = p / anchoW
        return Tono(luzMonitor(f), luzRegleta(f, regleta), f.x > MONITOR.x)
    }

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** La sombra de algo en la pared: hacia fuera del monitor, más marcada cuanto más cerca está de él. No baja de [hasta]. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.5f) {
        val w = anchoW
        val k = luzMonitor(desde / w)
        if (k < 0.05f) return
        val dx = ((desde.x / w - MONITOR.x) / 0.3f).coerceIn(-1f, 1f)
        val dy = ((desde.y / w - MONITOR.y) / 0.3f).coerceIn(-0.3f, 1f)
        clipRect(0f, 0f, size.width, hasta) {
            translate(w * 0.03f * dx, w * (0.01f + 0.015f * dy)) { drawPath(p, NOCHE.copy(alpha = (a * k).coerceIn(0f, 0.6f))) }
        }
    }

    /** La sombra de contacto de lo que pisa algo. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float = 0.6f) =
        drawOval(NOCHE.copy(alpha = a.coerceIn(0f, 1f)), Offset(centro.x - ancho * 0.45f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r]: el claro del lado de la luz y la sombra del otro (ir dentro de su recorte). */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f) {
        val l = if (t.izq) r.left else r.right - r.width * 0.3f
        val o = if (t.izq) r.left + r.width * 0.6f else r.left
        drawRect(t.b(0.22f * fuerza), Offset(l, r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.35f * fuerza), Offset(if (t.izq) r.left + r.width * 0.14f else r.right - r.width * 0.22f, r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.25f * fuerza), Offset(o, r.top), Size(r.width * 0.4f, r.height))
    }

    /** Un piloto: encendido, con su halo chico y su brillo; apagado, del color hundido con un reflejo. */
    private fun DrawScope.piloto(c: Offset, r: Float, col: Color, encendido: Boolean, t: Tono) {
        if (encendido) {
            drawCircle(col.copy(alpha = 0.18f), r * 3.2f, c)
            drawCircle(col.copy(alpha = 0.35f), r * 1.8f, c)
            drawCircle(col, r, c)
            drawCircle(Color.White.copy(alpha = 0.75f), r * 0.38f, c - Offset(r * 0.3f, r * 0.3f))
        } else {
            drawCircle(t.c(mezcla(col, NOCHE, 0.78f)), r, c)
            drawCircle(t.b(0.45f), r * 0.32f, c - Offset(r * 0.32f, r * 0.32f))
        }
    }

    /** Un tornillo de cabeza en cruz, con su sombra. */
    private fun DrawScope.tornillo(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.4f), r * 1.05f, p + Offset(r * 0.35f, r * 0.35f))
        drawCircle(t.c(ACERO), r, p)
        drawLine(t.s(0.55f), p - Offset(r * 0.6f, 0f), p + Offset(r * 0.6f, 0f), r * 0.28f)
        drawLine(t.s(0.55f), p - Offset(0f, r * 0.6f), p + Offset(0f, r * 0.6f), r * 0.28f)
        drawCircle(t.b(0.5f), r * 0.3f, p - Offset(r * 0.45f, r * 0.45f))
    }

    /** Garabatos de bolígrafo: [n] renglones ondulados dentro de [r]. */
    private fun DrawScope.garabatos(r: Rect, n: Int, col: Color, semilla: Int, grosor: Float) {
        for (k in 0 until n) {
            val y = r.top + r.height * (k + 0.5f) / n
            val fin = r.left + r.width * (0.5f + 0.5f * azar(semilla, k))
            val p = Path()
            var x = r.left
            p.moveTo(x, y)
            var j = 0
            val paso = r.height / n * 0.45f
            while (x < fin) {
                val nx = minOf(x + paso, fin)
                val dy = r.height / n * 0.22f * (if (j % 2 == 0) -1f else 1f) * (0.5f + azar(semilla + k, j))
                p.cubicTo(x + paso * 0.3f, y + dy, nx - paso * 0.3f, y + dy, nx, y)
                x = nx; j++
            }
            drawPath(p, col, style = Stroke(grosor, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        anchoW = w
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE; val canto = ch * CANTO
        val pieA = sup + (frente - sup) * 0.3f
        val pieF = sup + (frente - sup) * 0.9f
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        regleta = Offset(0.135f, (b.top + hb * 0.2f) / w)

        // ---- la pared, el panel perforado y el suelo
        pared(w, suelo, ch)
        suelo(w, suelo, alto)
        panel(Rect(-2f * u, w * 0.03f, w * 0.79f, ch * 0.79f))

        // ---- en la pared: los cables con bridas, los auriculares, las notas y el listado
        cables(w, ch, sup)
        auriculares(Offset(w * 0.69f, w * 0.085f), sup)
        nota(Offset(w * 0.6f, w * 0.13f), w * 0.07f, 7f, 1, sup)
        nota(Offset(w * 0.05f, w * 0.27f), w * 0.064f, -6f, 2, sup)
        listado(Rect(w * 0.815f, w * 0.05f, w * 0.955f, w * 0.43f), sup)

        // ---- la sombra del bastidor en la pared (a la derecha) y en el suelo
        val xL = w * IZQ; val xR = w * DER
        drawPath(poligono(xR, canto, xR + w * 0.03f, canto + w * 0.02f, xR + w * 0.03f, suelo, xR, suelo), NOCHE.copy(alpha = 0.55f))
        drawRect(NOCHE.copy(alpha = 0.6f), Offset(xL + w * 0.01f, suelo), Size(xR - xL + w * 0.02f, pisan - suelo))

        // ---- el bastidor
        bastidor(d, ch, canto, pisan, b)

        // ---- la tapa del bastidor y su canto
        tapa(w, sup, frente, canto)

        // ---- lo de encima, de atrás adelante
        teclado(Rect(w * 0.53f, pieA - w * 0.125f, w * 0.83f, pieA))
        flexo(Offset(w * 0.09f, pieA + 1f * u), ch, sup)
        monitor(Offset(w * 0.355f, pieA + (frente - sup) * 0.25f), sup)
        modem(Rect(w * 0.555f, pieF - w * 0.052f, w * 0.775f, pieF), sup)
        lata(Offset(w * 0.505f, pieF + 1f * u), sup)
        manuales(Offset(w * 0.885f, pieA + (frente - sup) * 0.35f), sup)

        // ---- al pie: la torre tumbada sin tapa y la caja de cables, cortadas por los bordes
        torre(Offset(w * 0.1f, b.top + hb * PIE), w, hb)
        cajaCables(Offset(w * 0.92f, b.top + hb * PIE), w, hb)

        // ---- la placa de «y N más»: una placa de acero con su cinta de rotular
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.4f), Offset(r.left + 2f * u, r.top + 2.5f * u), r.size)
            drawRect(t.c(ACERO), r.topLeft, r.size)
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1f * u))
            cinta(Rect(r.left + 4f * u, r.top + 3f * u, r.right - 4f * u, r.bottom - 3f * u), mezcla(NEGRO_RACK, NOCHE, 0.3f), t, 4)
        }

        // ---- la luz de lo que alumbra, por encima de lo que tiene cerca
        val pantalla = MONITOR * w
        mancha(pantalla + Offset(0f, w * 0.06f), w * 0.78f, CIAN.copy(alpha = 0.07f))
        mancha(pantalla + Offset(0f, w * 0.02f), w * 0.36f, CIAN.copy(alpha = 0.12f))
        val rr = regleta * w
        mancha(rr, w * 0.22f, ROJO.copy(alpha = 0.2f))
        granoMueble(Rect(0f, 0f, w, alto), true)
    }

    /** La pared pintada de azul pizarra: más clara cerca del monitor, con pinceladas, rozaduras abajo y el enchufe. */
    private fun DrawScope.pared(w: Float, suelo: Float, ch: Float) {
        val u = density
        fun k(x: Float) = Tono(x, 0f, true).c(PARED)
        drawRect(Brush.radialGradient(0f to k(1f), 0.18f to k(0.75f), 0.4f to k(0.4f), 0.7f to k(0.12f), 1f to k(0f),
            center = MONITOR * w, radius = w * 1.25f), Offset.Zero, Size(w, suelo))
        pinceladas(Rect(0f, 0f, w, suelo), NOCHE, 3101, 40, u, 0.14f)
        pinceladas(Rect(0f, 0f, w, ch), CIAN, 3102, 10, u, 0.025f)
        // El rodapié y las rozaduras de abajo.
        val t = tn(Offset(w * 0.5f, suelo))
        drawRect(t.c(NEGRO_RACK), Offset(0f, suelo - w * 0.018f), Size(w, w * 0.018f))
        drawRect(t.b(0.3f), Offset(0f, suelo - w * 0.018f), Size(w, 0.8f * u))
        pinceladas(Rect(0f, suelo - w * 0.12f, w, suelo - w * 0.018f), NOCHE, 3103, 12, u, 0.2f)
    }

    /** El suelo de linóleo: las juntas de las losetas abriéndose hacia quien mira y una mancha gastada. */
    private fun DrawScope.suelo(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.4f, alto))
        val piso = mezcla(PARED, BEIS_OSC, 0.35f)
        drawRect(t.c(piso), Offset(0f, suelo), Size(w, alto - suelo))
        var y = suelo
        var paso = (alto - suelo) * 0.22f
        while (y < alto) {
            drawLine(t.s(0.3f), Offset(0f, y), Offset(w, y), 1f * u)
            y += paso; paso *= 1.45f
        }
        for (i in -2..8) {
            val x0 = w * i / 6f
            drawLine(t.s(0.3f), Offset(x0, suelo), Offset(w / 2 + (x0 - w / 2) * 1.35f, alto), 1f * u)
        }
        pinceladas(Rect(0f, suelo, w, alto), NOCHE, 3110, 14, u, 0.2f)
        drawRect(NOCHE.copy(alpha = 0.5f), Offset(0f, suelo), Size(w, 3f * u))
    }

    /**
     * El panel perforado de aglomerado: el tablero pardo, más encendido cerca del monitor, sus agujeros en rejilla (cada uno con
     * su labio de luz), el canto de la derecha con su grosor y su sombra en la pared, y los tornillos.
     */
    private fun DrawScope.panel(r: Rect) {
        val u = density
        val w = anchoW
        // La sombra en la pared y el canto.
        drawRect(NOCHE.copy(alpha = 0.5f), Offset(r.right, r.top + w * 0.012f), Size(w * 0.022f, r.height))
        drawRect(NOCHE.copy(alpha = 0.45f), Offset(r.left, r.bottom), Size(r.width + w * 0.022f, w * 0.014f))
        fun k(x: Float) = Tono(x, 0f, true).c(TABLERO)
        drawRect(Brush.radialGradient(0f to k(1f), 0.25f to k(0.72f), 0.55f to k(0.38f), 1f to k(0.1f),
            center = MONITOR * w, radius = w * 0.95f), r.topLeft, r.size)
        pinceladas(r, NOCHE, 3120, 26, u, 0.12f)
        // Los agujeros, en una sola pasada: el hueco oscuro y debajo el labio que recibe la luz.
        val paso = w * 0.031f
        val huecos = ArrayList<Offset>()
        val labios = ArrayList<Offset>()
        var y = r.top + paso * 0.6f
        while (y < r.bottom - paso * 0.3f) {
            var x = r.left + paso * 0.7f
            while (x < r.right - paso * 0.3f) { huecos.add(Offset(x, y)); labios.add(Offset(x + 0.3f * u, y + 1.3f * u)); x += paso }
            y += paso
        }
        drawPoints(labios, PointMode.Points, CIAN_CLARO.copy(alpha = 0.12f), 2.6f * u, StrokeCap.Round)
        drawPoints(huecos, PointMode.Points, NOCHE.copy(alpha = 0.85f), 2.6f * u, StrokeCap.Round)
        // El canto de la derecha y el de abajo.
        val t = tn(Offset(r.right, r.center.y))
        drawRect(t.c(mezcla(TABLERO, BEIS, 0.3f)), Offset(r.right - 2f * u, r.top), Size(2f * u, r.height))
        drawRect(t.s(0.4f), Offset(r.left, r.bottom - 1.5f * u), Size(r.width, 1.5f * u))
        drawRect(tn(Offset(r.center.x, r.top)).b(0.4f), r.topLeft, Size(r.width, 1f * u))
        listOf(Offset(r.right - w * 0.015f, r.top + w * 0.015f), Offset(r.right - w * 0.015f, r.bottom - w * 0.015f), Offset(w * 0.4f, r.bottom - w * 0.015f)).forEach {
            tornillo(it, 2f * u, tn(it))
        }
    }

    /**
     * Los cables del panel: un mazo de tres que entra por la izquierda, corre bajo el borde de arriba sujeto con bridas y baja por
     * detrás del monitor; y otro que baja por la pared, a la derecha del panel, hasta la tapa del bastidor.
     */
    private fun DrawScope.cables(w: Float, ch: Float, sup: Float) {
        val u = density
        val y0 = w * 0.06f
        val cols = listOf(NEGRO_RACK to 2.8f, CABLE to 2.2f, mezcla(BEIS_OSC, NEGRO_RACK, 0.4f) to 2.4f)
        cols.forEachIndexed { j, (col, g) ->
            val yy = y0 + j * 2.6f * u
            val p = Path().apply {
                moveTo(-4f * u, yy + 2f * u)
                cubicTo(w * 0.15f, yy + 4f * u, w * 0.3f, yy - 1f * u, w * 0.43f + j * 2f * u, yy)
                cubicTo(w * 0.5f + j * 2f * u, yy, w * 0.5f + j * 2.4f * u, yy + w * 0.03f, w * 0.49f + j * 2.4f * u, w * 0.13f)
            }
            val t = tn(Offset(w * 0.3f, yy))
            translate(w * 0.006f, w * 0.008f) { drawPath(p, NOCHE.copy(alpha = 0.4f), style = Stroke(g * u)) }
            drawPath(p, t.c(col), style = Stroke(g * u, cap = StrokeCap.Round))
            drawPath(p, t.b(0.35f), style = Stroke(g * 0.3f * u))
        }
        // Las bridas: tiras blancas que abrazan el mazo, con la cola cortada.
        listOf(0.08f, 0.2f, 0.33f).forEach { fx ->
            val x = w * fx
            val t = tn(Offset(x, y0))
            drawRect(t.s(0.4f), Offset(x + 1.2f * u, y0 - 2f * u + 1f * u), Size(2.2f * u, 10f * u))
            drawRect(t.c(PAPEL), Offset(x, y0 - 2.5f * u), Size(2.2f * u, 10.5f * u))
            drawRect(t.c(PAPEL), Offset(x + 2.2f * u, y0 - 3.5f * u), Size(2.6f * u, 2.2f * u))
            drawRect(t.b(0.4f), Offset(x, y0 - 2.5f * u), Size(0.7f * u, 10.5f * u))
        }
        // El cable que baja por la pared de la derecha, sujeto con dos grapas.
        val t = tn(Offset(w * 0.8f, ch * 0.5f))
        val p = Path().apply { moveTo(w * 0.8f, -2f * u); cubicTo(w * 0.802f, ch * 0.3f, w * 0.795f, ch * 0.6f, w * 0.805f, sup) }
        translate(w * 0.008f, w * 0.004f) { drawPath(p, NOCHE.copy(alpha = 0.45f), style = Stroke(2.6f * u)) }
        drawPath(p, t.c(NEGRO_RACK), style = Stroke(2.6f * u))
        drawPath(p, t.b(0.3f), style = Stroke(0.8f * u))
        listOf(0.25f, 0.6f).forEach { f ->
            val q = Offset(w * 0.8f, ch * f)
            drawRect(t.c(PAPEL), Offset(q.x - 2.5f * u, q.y - 1.2f * u), Size(5f * u, 2.4f * u))
        }
    }

    /** Los auriculares colgados de un gancho del panel: la diadema, las dos copas con su almohadilla y el cable que cuelga. */
    private fun DrawScope.auriculares(gancho: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(gancho + Offset(0f, w * 0.06f))
        val aw = w * 0.1f
        val diadema = Path().apply {
            moveTo(gancho.x - aw * 0.45f, gancho.y + aw * 0.75f)
            cubicTo(gancho.x - aw * 0.48f, gancho.y - aw * 0.05f, gancho.x + aw * 0.48f, gancho.y - aw * 0.05f, gancho.x + aw * 0.45f, gancho.y + aw * 0.75f)
        }
        val copaI = Rect(gancho.x - aw * 0.62f, gancho.y + aw * 0.6f, gancho.x - aw * 0.3f, gancho.y + aw * 1.1f)
        val copaD = Rect(gancho.x + aw * 0.3f, gancho.y + aw * 0.6f, gancho.x + aw * 0.62f, gancho.y + aw * 1.1f)
        val todo = Path().apply { addOval(copaI); addOval(copaD) }
        sombraEnPared(todo, gancho, sup)
        translate(w * 0.012f, w * 0.006f) { drawPath(diadema, NOCHE.copy(alpha = 0.4f), style = Stroke(3.6f * u)) }
        // El cable, que baja enroscado de la copa izquierda.
        val cab = Path().apply {
            moveTo(copaI.center.x, copaI.bottom)
            cubicTo(copaI.center.x - aw * 0.1f, copaI.bottom + aw * 0.5f, copaI.center.x + aw * 0.4f, copaI.bottom + aw * 0.4f, copaI.center.x + aw * 0.2f, copaI.bottom + aw * 1.05f)
        }
        drawPath(cab, t.c(NEGRO_RACK), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(cab, t.b(0.3f), style = Stroke(0.5f * u))
        drawRect(t.c(ACERO), Offset(copaI.center.x + aw * 0.17f, copaI.bottom + aw * 1.03f), Size(2.4f * u, 7f * u))
        // El gancho del panel.
        drawLine(t.c(ACERO), gancho + Offset(0f, -3f * u), gancho + Offset(0f, 4f * u), 2f * u, StrokeCap.Round)
        drawPath(diadema, t.c(NEGRO_RACK), style = Stroke(3.6f * u, cap = StrokeCap.Round))
        drawPath(diadema, t.b(0.55f), style = Stroke(1f * u))
        listOf(copaI, copaD).forEachIndexed { i, c ->
            drawOval(t.c(mezcla(NEGRO_RACK, ACERO_OSC, 0.3f)), c.topLeft, c.size)
            // La almohadilla de espuma beis, de cara a quien mira en la copa derecha.
            val pad = Rect(c.left + c.width * 0.18f, c.top + c.height * 0.14f, c.right - c.width * 0.18f, c.bottom - c.height * 0.14f)
            drawOval(t.c(if (i == 1) mezcla(BEIS_OSC, NEGRO_RACK, 0.3f) else NEGRO_RACK), pad.topLeft, pad.size)
            drawArc(t.b(0.6f), 150f, 110f, false, c.topLeft, c.size, style = Stroke(1f * u))
            drawArc(t.s(0.5f), -30f, 100f, false, c.topLeft, c.size, style = Stroke(1.2f * u))
        }
    }

    /** Una nota adhesiva amarilla, ladeada [g] grados, con garabatos, la punta de abajo levantada y su sombra. */
    private fun DrawScope.nota(c: Offset, lado: Float, g: Float, semilla: Int, sup: Float) {
        val u = density
        val t = tn(c)
        val r = Rect(c.x - lado / 2, c.y - lado / 2, c.x + lado / 2, c.y + lado / 2)
        rotate(g, c) {
            val esq = lado * 0.22f
            val hoja = poligono(r.left, r.top, r.right, r.top, r.right, r.bottom - esq, r.right - esq, r.bottom, r.left, r.bottom)
            sombraEnPared(hoja, c, sup, 0.45f)
            drawPath(hoja, t.c(NOTA))
            drawRect(t.c(mezcla(NOTA, BEIS_OSC, 0.3f)), r.topLeft, Size(lado, lado * 0.14f))
            garabatos(Rect(r.left + lado * 0.12f, r.top + lado * 0.22f, r.right - lado * 0.14f, r.bottom - lado * 0.2f), 3, t.c(mezcla(PARED, NOCHE, 0.4f)), 3130 + semilla, 1f * u)
            val doblez = poligono(r.right, r.bottom - esq, r.right - esq * 0.2f, r.bottom - esq * 0.2f, r.right - esq, r.bottom)
            translate(1.2f * u, 1.6f * u) { drawPath(doblez, t.s(0.35f)) }
            drawPath(doblez, t.c(mezcla(NOTA, Color.White, 0.25f)))
        }
    }

    /**
     * La hoja de papel continuo clavada en la pared con una chincheta roja: las bandas verdes, los márgenes de arrastre con sus
     * agujeros, el troquel, los renglones del listado; abajo, el pliegue de la hoja siguiente que se dobla hacia delante.
     */
    private fun DrawScope.listado(r: Rect, sup: Float) {
        val u = density
        val t = tn(r.center)
        val hoja = Path().apply {
            moveTo(r.left + 2f * u, r.top); lineTo(r.right, r.top + 1f * u)
            lineTo(r.right - 1f * u, r.bottom); lineTo(r.left, r.bottom - 1f * u); close()
        }
        translate(anchoW * 0.016f, anchoW * 0.012f) { drawPath(hoja, NOCHE.copy(alpha = 0.4f)) }
        drawPath(hoja, t.c(PAPEL))
        clipPath(hoja) {
            val margen = r.width * 0.1f
            // Las bandas verdes.
            val banda = r.width * 0.11f
            var y = r.top + banda * 0.5f
            var i = 0
            while (y < r.bottom) {
                if (i % 2 == 0) drawRect(tn(Offset(r.center.x, y)).c(RAYA), Offset(r.left + margen, y), Size(r.width - 2 * margen, banda))
                y += banda; i++
            }
            // Los renglones del listado: palabras de largo al azar.
            val renglon = banda / 2.2f
            var k = 0
            y = r.top + renglon
            while (y < r.bottom - renglon) {
                var x = r.left + margen * 1.3f + (if (k % 5 in 1..3) r.width * 0.06f else 0f)
                val fin = r.left + margen + (r.width - 2 * margen) * (0.45f + 0.5f * azar(3140, k))
                var j = 0
                while (x < fin) {
                    val l = r.width * (0.04f + 0.1f * azar(3141 + k, j))
                    drawRect(t.c(mezcla(PARED, NOCHE, 0.5f)).copy(alpha = 0.7f), Offset(x, y), Size(minOf(l, fin - x), 1.3f * u))
                    x += l + r.width * 0.03f; j++
                }
                y += renglon; k++
            }
            // Los márgenes de arrastre: el troquel y los agujeros.
            listOf(r.left + margen, r.right - margen).forEach { x ->
                var yy = r.top
                while (yy < r.bottom) { drawLine(t.s(0.3f), Offset(x, yy), Offset(x, yy + 2f * u), 0.6f * u); yy += 4f * u }
            }
            val agujeros = ArrayList<Offset>()
            var yy = r.top + margen * 0.6f
            while (yy < r.bottom) { agujeros.add(Offset(r.left + margen * 0.5f, yy)); agujeros.add(Offset(r.right - margen * 0.5f, yy)); yy += r.width * 0.075f }
            drawPoints(agujeros, PointMode.Points, mezcla(t.c(PARED), NOCHE, 0.3f), 3f * u, StrokeCap.Round)
            // La hoja se curva: el lado izquierdo, más lejos del monitor, un poco en sombra.
            drawRect(t.s(0.12f), Offset(r.right - r.width * 0.18f, r.top), Size(r.width * 0.18f, r.height))
            drawRect(t.b(0.25f), r.topLeft, Size(r.width * 0.08f, r.height))
        }
        // El pliegue de abajo: la hoja siguiente se dobla hacia delante.
        val pl = r.height * 0.07f
        val pliegue = poligono(r.left, r.bottom - 1f * u, r.right - 1f * u, r.bottom, r.right + r.width * 0.05f, r.bottom + pl, r.left + r.width * 0.04f, r.bottom + pl * 0.9f)
        translate(anchoW * 0.012f, anchoW * 0.008f) { drawPath(pliegue, NOCHE.copy(alpha = 0.45f)) }
        drawPath(pliegue, t.c(mezcla(PAPEL, BEIS_OSC, 0.45f)))
        drawLine(t.b(0.5f), Offset(r.left, r.bottom - 1f * u), Offset(r.right - 1f * u, r.bottom), 1f * u)
        // La chincheta roja.
        val ch = Offset(r.center.x, r.top + r.width * 0.06f)
        drawCircle(NOCHE.copy(alpha = 0.5f), 4f * u, ch + Offset(2f * u, 2f * u))
        drawCircle(t.c(ROJO), 3.8f * u, ch)
        drawCircle(t.b(0.7f), 1.3f * u, ch - Offset(1.2f * u, 1.2f * u))
    }

    // ---------------------------------------------------------------- el bastidor

    /**
     * El cuerpo del bastidor: el fondo de cada bahía, los montantes beis con su riel negro perforado junto a cada bahía, las
     * bandejas entre filas, el panel de arriba con la rejilla, el panel de abajo con la regleta, el zócalo con sus patas, el
     * mazo de cable plano y el cable enrollado, y la pintura gastada.
     */
    private fun DrawScope.bastidor(d: Disposicion, ch: Float, canto: Float, pisan: Float, b: Rect) {
        val u = density
        val w = anchoW
        val xL = w * IZQ; val xR = w * DER
        val cuerpo = Rect(xL, canto, xR, pisan - w * 0.03f)
        // El chasis beis, de arriba (encendido) abajo (perdido).
        drawRect(Brush.verticalGradient(List(7) { i -> tn(Offset(w * 0.3f, cuerpo.top + cuerpo.height * i / 6f)).c(BEIS) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        pinceladas(cuerpo, NOCHE, 3150, 30, u, 0.07f)
        // El costado derecho del chasis, el canto de la chapa, en sombra; el izquierdo con un filo.
        drawRect(NOCHE.copy(alpha = 0.45f), Offset(xR - w * 0.014f, cuerpo.top), Size(w * 0.014f, cuerpo.height))
        for (k in 0 until 8) {
            val y0 = cuerpo.top + cuerpo.height * k / 8f
            drawRect(tn(Offset(xL, y0)).b(0.45f), Offset(xL, y0), Size(1.2f * u, cuerpo.height / 8f))
        }

        // ---- cada bahía: el fondo hondo, los rieles negros perforados a los lados y la bandeja de abajo
        val riel = w * 0.02f
        d.huecos.forEachIndexed { i, r ->
            val t = tn(r.center)
            val m = d.separacion * 0.3f
            val bahia = Rect(r.left - riel, r.top - m, r.right + riel, r.bottom + m)
            drawRect(t.c(mezcla(NEGRO_RACK, NOCHE, 0.4f)), bahia.topLeft, bahia.size)
            // La sombra de la bandeja de arriba en el fondo.
            drawRect(t.s(0.5f), bahia.topLeft, Size(bahia.width, bahia.height * 0.12f))
            // Los rieles con sus agujeros cuadrados.
            listOf(bahia.left, bahia.right - riel).forEach { x ->
                drawRect(t.c(NEGRO_RACK), Offset(x, bahia.top), Size(riel, bahia.height))
                drawRect(t.b(0.35f), Offset(x, bahia.top), Size(0.8f * u, bahia.height))
                var y = bahia.top + riel * 0.5f
                while (y < bahia.bottom - riel * 0.5f) {
                    drawRect(NOCHE.copy(alpha = 0.9f), Offset(x + riel * 0.3f, y), Size(riel * 0.4f, riel * 0.4f))
                    drawRect(t.b(0.25f), Offset(x + riel * 0.3f, y + riel * 0.4f), Size(riel * 0.4f, 0.6f * u))
                    y += riel * 0.85f
                }
            }
            // El canto de la bandeja de abajo.
            val bd = Rect(bahia.left, bahia.bottom, bahia.right, bahia.bottom + 2.4f * u)
            drawRect(t.c(BEIS_OSC), bd.topLeft, bd.size)
            drawRect(t.b(0.6f), bd.topLeft, Size(bd.width, 0.8f * u))
            drawRect(NOCHE.copy(alpha = 0.5f), Offset(bd.left, bd.bottom), Size(bd.width, 2f * u))
        }

        // ---- el panel de arriba: la rejilla de ventilación y la placa de características
        val primero = d.huecos.first()
        val panel = Rect(xL, canto, xR, primero.top - d.separacion * 0.3f)
        if (panel.height > 6f * u) {
            val tp = tn(Offset(w * 0.3f, panel.center.y))
            val rej = Rect(w * 0.1f, panel.top + panel.height * 0.3f, w * 0.42f, panel.bottom - panel.height * 0.25f)
            var x = rej.left
            while (x < rej.right) {
                drawRect(NOCHE.copy(alpha = 0.8f), Offset(x, rej.top), Size(w * 0.006f, rej.height))
                drawRect(tp.b(0.4f), Offset(x + w * 0.006f, rej.top), Size(0.7f * u, rej.height))
                x += w * 0.014f
            }
            val tq = tn(Offset(w * 0.7f, panel.center.y))
            val pl = Rect(w * 0.62f, panel.top + panel.height * 0.28f, w * 0.78f, panel.bottom - panel.height * 0.25f)
            drawRect(tq.s(0.3f), Offset(pl.left + 1.5f * u, pl.top + 1.5f * u), pl.size)
            drawRect(tq.c(ACERO), pl.topLeft, pl.size)
            drawRect(tq.c(mezcla(ACERO_OSC, NOCHE, 0.3f)), Offset(pl.left + pl.width * 0.1f, pl.top + pl.height * 0.35f), Size(pl.width * 0.6f, 1.2f * u))
            drawRect(tq.b(0.5f), pl.topLeft, Size(pl.width, 0.8f * u))
        }

        // ---- el panel de abajo: la regleta con su interruptor rojo y la etiqueta
        val tr = tn(regleta * w)
        val reg = Rect(w * 0.1f, b.top + b.height * 0.1f, w * 0.52f, b.top + b.height * 0.3f)
        drawRect(NOCHE.copy(alpha = 0.5f), Offset(reg.left + 2f * u, reg.top + 2.5f * u), reg.size)
        drawRoundRect(tr.c(NEGRO_RACK), reg.topLeft, reg.size, CornerRadius(2f * u))
        drawRect(tr.b(0.5f), reg.topLeft, Size(reg.width, 1f * u))
        // El interruptor de balancín, encendido.
        val sw = Rect(reg.left + reg.width * 0.03f, reg.top + reg.height * 0.18f, reg.left + reg.width * 0.12f, reg.bottom - reg.height * 0.18f)
        drawRect(ROJO.copy(alpha = 0.3f), Rect(sw.left - 3f * u, sw.top - 3f * u, sw.right + 3f * u, sw.bottom + 3f * u).topLeft, Size(sw.width + 6f * u, sw.height + 6f * u))
        drawRect(mezcla(ROJO, NOCHE, 0.25f), sw.topLeft, sw.size)
        drawRect(mezcla(ROJO, Color.White, 0.35f), sw.topLeft, Size(sw.width, sw.height * 0.45f))
        // Los enchufes, dos con su clavija y su cable que baja.
        for (k in 0 until 4) {
            val c = Offset(reg.left + reg.width * (0.24f + 0.2f * k), reg.center.y)
            val tk = tn(c)
            val s = reg.height * 0.62f
            drawRoundRect(tk.c(BEIS_OSC), Offset(c.x - s / 2, c.y - s / 2), Size(s, s), CornerRadius(s * 0.2f))
            if (k == 0 || k == 2) {
                // La clavija negra.
                drawRect(tk.c(NEGRO_RACK), Offset(c.x - s * 0.4f, c.y - s * 0.38f), Size(s * 0.8f, s * 0.76f))
                drawRect(tk.b(0.5f), Offset(c.x - s * 0.4f, c.y - s * 0.38f), Size(s * 0.8f, 0.8f * u))
                val cab = Path().apply { moveTo(c.x, c.y + s * 0.38f); cubicTo(c.x, b.top + b.height * 0.5f, c.x + s * (if (k == 0) -1.5f else 2f), b.top + b.height * 0.55f, c.x + s * (if (k == 0) -2.5f else 3f), b.top + b.height * 0.9f) }
                drawPath(cab, tk.c(NEGRO_RACK), style = Stroke(2.2f * u))
                drawPath(cab, tk.b(0.35f), style = Stroke(0.6f * u))
            } else {
                drawCircle(NOCHE, s * 0.08f, c - Offset(s * 0.18f, 0f))
                drawCircle(NOCHE, s * 0.08f, c + Offset(s * 0.18f, 0f))
            }
        }
        listOf(reg.left - 3f * u, reg.right + 3f * u).forEach { x -> tornillo(Offset(x, reg.center.y), 1.8f * u, tr) }

        // ---- el zócalo negro y las patas niveladoras
        val tz = tn(Offset(w * 0.5f, pisan))
        val zocalo = Rect(xL, cuerpo.bottom, xR, pisan - 4f * u)
        drawRect(tz.c(NEGRO_RACK), zocalo.topLeft, zocalo.size)
        drawRect(tz.b(0.35f), zocalo.topLeft, Size(zocalo.width, 1f * u))
        listOf(xL + w * 0.02f, xR - w * 0.07f).forEach { x ->
            drawRect(tz.c(ACERO_OSC), Offset(x + w * 0.012f, pisan - 4f * u), Size(w * 0.026f, 3f * u))
            drawRect(tz.c(NEGRO_RACK), Offset(x, pisan - 1.5f * u), Size(w * 0.05f, 2.5f * u))
            drawRect(tz.b(0.3f), Offset(x, pisan - 1.5f * u), Size(w * 0.05f, 0.6f * u))
        }

        // ---- la pintura gastada en los cantos: asoma el acero
        val saltos = listOf(Offset(xL + w * 0.006f, canto + w * 0.02f), Offset(xL + w * 0.008f, d.filas.last().center.y),
            Offset(xR - w * 0.02f, d.filas[minOf(1, d.filas.size - 1)].bottom + 2f * u), Offset(w * 0.5f, primero.top - 3f * u), Offset(w * 0.2f, b.top + 2f * u))
        saltos.forEachIndexed { k, p ->
            val t = tn(p)
            val r = (1.6f + 2f * azar(3160, k)) * u
            drawOval(t.c(ACERO), Offset(p.x - r * 1.3f, p.y - r * 0.8f), Size(r * 2.6f, r * 1.6f))
            drawArc(t.s(0.4f), 180f, 180f, false, Offset(p.x - r * 1.3f, p.y - r * 0.8f), Size(r * 2.6f, r * 1.6f), style = Stroke(0.6f * u))
        }

        // ---- el mazo de cable plano gris, que sale de arriba y cuelga en un bucle por el costado izquierdo
        val y1 = d.filas[minOf(1, d.filas.size - 1)].center.y
        val cinta = Path().apply {
            moveTo(xL + w * 0.03f, canto + 2f * u)
            cubicTo(xL - w * 0.02f, canto + w * 0.05f, xL - w * 0.035f, y1, xL - w * 0.005f, y1 + w * 0.04f)
            cubicTo(xL + w * 0.02f, y1 + w * 0.07f, xL + w * 0.015f, y1 - w * 0.08f, xL + w * 0.01f, d.huecos.first().center.y)
        }
        val tc = tn(Offset(xL, (canto + y1) / 2))
        translate(w * 0.008f, w * 0.006f) { drawPath(cinta, NOCHE.copy(alpha = 0.45f), style = Stroke(w * 0.022f)) }
        drawPath(cinta, tc.c(CABLE), style = Stroke(w * 0.022f))
        for (k in -2..2) translate(k * w * 0.004f, 0f) { drawPath(cinta, tc.s(0.18f), style = Stroke(0.5f * u)) }
        drawPath(cinta, tc.b(0.4f), style = Stroke(w * 0.004f))
        // El hilo rojo del borde del cable plano.
        translate(-w * 0.008f, 0f) { drawPath(cinta, tc.c(mezcla(ROJO, CABLE, 0.4f)), style = Stroke(0.9f * u)) }

        // ---- el cable enrollado colgado de un gancho en el costado derecho
        val g = Offset(xR - w * 0.004f, d.filas.first().bottom + d.separacion * 0.5f)
        val tg = tn(g)
        drawLine(tg.c(ACERO), g, g + Offset(w * 0.01f, 0f), 2f * u)
        for (k in 0 until 5) {
            val rr = Rect(g.x - w * 0.012f + k * 0.6f * u, g.y - 1f * u, g.x + w * 0.045f + k * 0.8f * u, g.y + w * (0.11f + 0.006f * k))
            drawOval(NOCHE.copy(alpha = 0.3f), rr.translate(w * 0.008f, w * 0.004f).topLeft, rr.size, style = Stroke(2f * u))
            drawOval(tg.c(mezcla(NEGRO_RACK, PARED, 0.2f * (k % 2))), rr.topLeft, rr.size, style = Stroke(2f * u))
            drawArc(tg.b(0.4f), 170f, 70f, false, rr.topLeft, rr.size, style = Stroke(0.7f * u))
        }
        val cola = Path().apply { moveTo(g.x + w * 0.02f, g.y + w * 0.11f); cubicTo(g.x + w * 0.03f, g.y + w * 0.16f, g.x - w * 0.005f, g.y + w * 0.17f, g.x + w * 0.005f, g.y + w * 0.21f) }
        drawPath(cola, tg.c(NEGRO_RACK), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawRect(tg.c(ACERO), Offset(g.x - 1f * u, g.y + w * 0.21f), Size(3.5f * u, 6f * u))
    }

    /** La tapa del bastidor, vista un poco desde arriba, y su canto de frente: beis, gastada, con un cerco y rayones. */
    private fun DrawScope.tapa(w: Float, sup: Float, frente: Float, canto: Float) {
        val u = density
        val xL = w * IZQ - w * 0.005f; val xR = w * DER + w * 0.005f
        val tapa = Rect(xL, sup, xR, frente)
        drawRect(Brush.horizontalGradient(List(5) { i -> tn(Offset(xL + (xR - xL) * i / 4f, sup)).c(mezcla(BEIS, Color.White, 0.06f)) }, xL, xR), tapa.topLeft, tapa.size)
        drawRect(NOCHE.copy(alpha = 0.45f), tapa.topLeft, Size(tapa.width, 1.6f * u))
        for (k in 0 until 6) {
            val x = w * (0.06f + 0.85f * azar(3170, k))
            val t = tn(Offset(x, sup))
            drawLine(t.b(0.3f), Offset(x, sup + (frente - sup) * (0.3f + 0.4f * azar(3171, k))), Offset(x + w * 0.03f, sup + (frente - sup) * (0.4f + 0.3f * azar(3172, k))), 0.6f * u)
        }
        val labio = Rect(xL, frente, xR, canto)
        drawRect(Brush.horizontalGradient(List(5) { i -> tn(Offset(xL + (xR - xL) * i / 4f, frente)).c(BEIS_OSC) }, xL, xR), labio.topLeft, labio.size)
        drawRect(Brush.horizontalGradient(List(5) { i -> tn(Offset(xL + (xR - xL) * i / 4f, frente)).b(0.9f) }, xL, xR), Offset(xL, frente), Size(labio.width, 1.3f * u))
        drawRect(NOCHE.copy(alpha = 0.5f), Offset(xL, canto - 1f * u), Size(labio.width, 1f * u))
        listOf(0.06f, 0.47f, 0.92f).forEachIndexed { i, fx ->
            drawOval(tn(Offset(w * fx, frente)).c(ACERO), Offset(w * fx, frente + 1.4f * u), Size(w * (0.012f + 0.008f * i), (canto - frente) * 0.45f))
        }
        drawRect(NOCHE.copy(alpha = 0.55f), Offset(xL, canto), Size(xR - xL, 2.4f * u))
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * El monitor de tubo girado en tres cuartos: el pie giratorio, la cara de plástico beis con el bisel, el costado izquierdo con
     * sus ranuras y la joroba del tubo detrás; la pantalla de fósforo abombada con el árbol de directorios, una fila en vídeo
     * inverso y el cursor; abajo, la rejilla, los mandos, el piloto verde; una nota adhesiva pegada en la esquina del bisel.
     */
    private fun DrawScope.monitor(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val fw = w * 0.31f; val fh = w * 0.262f
        val baseH = w * 0.032f
        val f = Rect(pie.x - fw / 2, pie.y - baseH - fh, pie.x + fw / 2, pie.y - baseH)
        val t = Tono(1f, 0f, false)
        val dv = Offset(-fw * 0.14f, -fh * 0.07f)
        val lado = poligono(f.left, f.top, f.left + dv.x, f.top + dv.y, f.left + dv.x, f.bottom + dv.y, f.left, f.bottom)
        val arriba = poligono(f.left, f.top, f.left + dv.x, f.top + dv.y, f.right + dv.x * 0.6f, f.top + dv.y, f.right, f.top)
        val joroba = poligono(f.left + dv.x, f.top + fh * 0.1f + dv.y, f.left + dv.x - fw * 0.17f, f.top + fh * 0.24f + dv.y * 1.5f,
            f.left + dv.x - fw * 0.17f, f.bottom - fh * 0.28f + dv.y * 1.5f, f.left + dv.x, f.bottom - fh * 0.08f + dv.y)
        // El pie giratorio: el disco y el cuello.
        contacto(Offset(pie.x + fw * 0.06f, pie.y), fw * 0.85f, w * 0.018f, 0.75f)
        val disco = Rect(pie.x - fw * 0.33f, pie.y - baseH * 0.55f, pie.x + fw * 0.33f, pie.y + baseH * 0.15f)
        drawOval(t.c(BEIS_OSC), disco.translate(0f, baseH * 0.12f).topLeft, disco.size)
        drawOval(t.c(mezcla(BEIS, BEIS_OSC, 0.3f)), disco.topLeft, disco.size)
        drawArc(t.b(0.5f), 180f, 140f, false, disco.topLeft, disco.size, style = Stroke(1f * u))
        drawRect(t.c(BEIS_OSC), Offset(pie.x - fw * 0.13f, f.bottom - 2f * u), Size(fw * 0.26f, disco.center.y - f.bottom))
        drawRect(t.s(0.35f), Offset(pie.x - fw * 0.13f, f.bottom - 2f * u), Size(fw * 0.26f, baseH * 0.3f))
        // La joroba del tubo y el costado, en la sombra del propio monitor (el fósforo no los alumbra).
        drawPath(joroba, hondo(mezcla(BEIS_OSC, BEIS, 0.4f)).let { mezcla(it, BEIS_OSC, 0.25f) })
        drawPath(lado, mezcla(hondo(BEIS), BEIS_OSC, 0.4f))
        clipPath(lado) {
            for (k in 0 until 6) {
                val y = f.top + fh * (0.2f + 0.08f * k)
                drawLine(NOCHE.copy(alpha = 0.6f), Offset(f.left + dv.x * 0.2f, y + dv.y * 0.2f), Offset(f.left + dv.x * 0.8f, y + dv.y * 0.8f), 1.6f * u)
            }
        }
        drawPath(joroba, CIAN_CLARO.copy(alpha = 0.15f), style = Stroke(0.8f * u))
        drawPath(arriba, mezcla(hondo(BEIS), BEIS, 0.55f))
        drawLine(CIAN_CLARO.copy(alpha = 0.35f), Offset(f.left, f.top), Offset(f.right, f.top), 1f * u)
        // La cara: plástico beis alumbrado por su propia pantalla, más claro alrededor del vidrio.
        val cara = Path().apply { addRoundRect(RoundRect(f, CornerRadius(fh * 0.05f))) }
        drawPath(cara, mezcla(t.c(BEIS), BEIS, 0.15f))
        clipPath(cara) {
            pinceladas(f, BEIS_OSC, 3180, 10, u, 0.2f)
            drawRect(NOCHE.copy(alpha = 0.22f), Offset(f.right - fw * 0.14f, f.top), Size(fw * 0.14f, fh))
            drawRect(NOCHE.copy(alpha = 0.18f), Offset(f.left, f.bottom - fh * 0.06f), Size(fw, fh * 0.06f))
            // El amarilleo del plástico, más fuerte arriba.
            drawRect(mezcla(BEIS_OSC, AMBAR, 0.3f).copy(alpha = 0.12f), f.topLeft, Size(fw, fh * 0.3f))
        }
        // El bisel hundido y la pantalla abombada.
        val bis = Rect(f.left + fw * 0.07f, f.top + fh * 0.07f, f.right - fw * 0.07f, f.bottom - fh * 0.22f)
        drawRoundRect(mezcla(t.c(BEIS_OSC), NOCHE, 0.25f), bis.topLeft, bis.size, CornerRadius(fh * 0.06f))
        drawRoundRect(NOCHE.copy(alpha = 0.55f), bis.topLeft, Size(bis.width, 3f * u), CornerRadius(fh * 0.03f))
        drawRect(CIAN_CLARO.copy(alpha = 0.4f), Offset(bis.left + fh * 0.05f, bis.bottom - 1f * u), Size(bis.width - fh * 0.1f, 1f * u))
        val pan = Rect(bis.left + fw * 0.035f, bis.top + fw * 0.03f, bis.right - fw * 0.035f, bis.bottom - fw * 0.03f)
        val panP = Path().apply { addRoundRect(RoundRect(pan, CornerRadius(fh * 0.1f))) }
        drawPath(panP, Brush.radialGradient(listOf(Color(0xFF0F4651), Color(0xFF062126), Color(0xFF031114)), pan.center - Offset(pan.width * 0.08f, pan.height * 0.1f), pan.width * 0.7f))
        clipPath(panP) { pantalla(pan) }
        // Abajo: la rejilla del altavoz, los mandos y el piloto verde.
        val y0 = bis.bottom + fh * 0.04f
        val y1 = f.bottom - fh * 0.04f
        for (k in 0 until 7) drawRect(NOCHE.copy(alpha = 0.45f), Offset(f.left + fw * (0.12f + 0.035f * k), y0 + (y1 - y0) * 0.2f), Size(fw * 0.014f, (y1 - y0) * 0.6f))
        listOf(0.66f, 0.74f).forEach { fx ->
            val mc = Offset(f.left + fw * fx, (y0 + y1) / 2)
            val rm = (y1 - y0) * 0.26f
            drawCircle(NOCHE.copy(alpha = 0.5f), rm, mc + Offset(1f * u, 1f * u))
            drawCircle(mezcla(t.c(BEIS_OSC), NOCHE, 0.3f), rm, mc)
            drawCircle(CIAN_CLARO.copy(alpha = 0.4f), rm * 0.35f, mc - Offset(rm * 0.35f, rm * 0.35f))
        }
        val bot = Rect(f.left + fw * 0.82f, y0 + (y1 - y0) * 0.22f, f.left + fw * 0.9f, y1 - (y1 - y0) * 0.22f)
        drawRect(mezcla(t.c(BEIS_OSC), NOCHE, 0.2f), bot.topLeft, bot.size)
        drawRect(NOCHE.copy(alpha = 0.5f), Offset(bot.left, bot.bottom - 1f * u), Size(bot.width, 1f * u))
        piloto(Offset(f.left + fw * 0.94f, (y0 + y1) / 2), 1.7f * u, VERDE, true, t)
        // La nota adhesiva pegada en la esquina de arriba a la derecha del bisel.
        val nc = Offset(f.right - fw * 0.02f, f.top + fw * 0.16f)
        val nl = fw * 0.17f
        rotate(-8f, nc) {
            val r = Rect(nc.x - nl / 2, nc.y - nl / 2, nc.x + nl / 2, nc.y + nl / 2)
            drawRect(NOCHE.copy(alpha = 0.4f), r.translate(2f * u, 2.5f * u).topLeft, r.size)
            drawRect(mezcla(t.c(NOTA), NOTA, 0.2f), r.topLeft, r.size)
            drawRect(mezcla(NOTA, BEIS_OSC, 0.35f), r.topLeft, Size(nl, nl * 0.14f))
            garabatos(Rect(r.left + nl * 0.14f, r.top + nl * 0.24f, r.right - nl * 0.12f, r.bottom - nl * 0.18f), 3, mezcla(PARED, NOCHE, 0.3f), 3190, 1f * u)
        }
    }

    /** Lo que muestra la pantalla: la orden, el árbol de carpetas con sus ramas, una fila en vídeo inverso y el cursor. */
    private fun DrawScope.pantalla(pan: Rect) {
        val u = density
        val lh = pan.height / 11f
        val tinta = CIAN.copy(alpha = 0.85f)
        val tenue = CIAN.copy(alpha = 0.5f)
        val x0 = pan.left + pan.width * 0.08f
        fun palabras(x: Float, y: Float, n: Int, col: Color, semilla: Int) {
            var xx = x
            for (k in 0 until n) {
                val l = pan.width * (0.05f + 0.08f * azar(semilla, k))
                drawRect(col, Offset(xx, y - lh * 0.2f), Size(l, lh * 0.4f))
                xx += l + pan.width * 0.025f
            }
        }
        fun carpeta(x: Float, y: Float, col: Color) {
            val cw = pan.width * 0.06f; val chh = lh * 0.62f
            drawPath(poligono(x, y - chh / 2, x + cw * 0.4f, y - chh / 2, x + cw * 0.5f, y - chh * 0.3f, x + cw, y - chh * 0.3f, x + cw, y + chh / 2, x, y + chh / 2), col, style = Stroke(1.1f * u))
        }
        // La orden.
        val y1 = pan.top + lh * 1.1f
        drawPath(poligono(x0, y1 - lh * 0.25f, x0 + lh * 0.3f, y1, x0, y1 + lh * 0.25f), tinta)
        palabras(x0 + lh * 0.6f, y1, 2, tinta, 3200)
        // La raíz y el tronco.
        val yr = pan.top + lh * 2.3f
        carpeta(x0, yr, tinta)
        palabras(x0 + pan.width * 0.09f, yr, 1, tinta, 3201)
        val tx = x0 + pan.width * 0.03f
        val filas = listOf(0, 0, 1, 1, 0, 0, 1)
        val yUlt = yr + lh * filas.size
        drawLine(tenue, Offset(tx, yr + lh * 0.4f), Offset(tx, yUlt), 1f * u)
        filas.forEachIndexed { i, nivel ->
            val y = yr + lh * (i + 1)
            val bx = tx + nivel * pan.width * 0.07f
            if (nivel == 1) drawLine(tenue, Offset(tx + pan.width * 0.07f, y - lh * 0.6f), Offset(tx + pan.width * 0.07f, y), 1f * u)
            drawLine(tenue, Offset(bx, y), Offset(bx + pan.width * 0.04f, y), 1f * u)
            val fx = bx + pan.width * 0.05f
            if (i == 4) {
                // La fila elegida, en vídeo inverso.
                drawRect(CIAN.copy(alpha = 0.85f), Offset(fx - pan.width * 0.01f, y - lh * 0.42f), Size(pan.width * 0.48f, lh * 0.84f))
                carpeta(fx, y, Color(0xFF042228))
                palabras(fx + pan.width * 0.09f, y, 2, Color(0xFF042228), 3210 + i)
            } else {
                carpeta(fx, y, tinta)
                palabras(fx + pan.width * 0.09f, y, 1 + (i % 2), tinta, 3210 + i)
            }
        }
        // El prompt con el cursor de bloque.
        val yp = pan.top + lh * 10.2f
        drawPath(poligono(x0, yp - lh * 0.25f, x0 + lh * 0.3f, yp, x0, yp + lh * 0.25f), tinta)
        drawRect(CIAN_CLARO, Offset(x0 + lh * 0.65f, yp - lh * 0.38f), Size(lh * 0.5f, lh * 0.76f))
        // El barrido, el reflejo curvo del vidrio y las esquinas más hondas.
        var y = pan.top
        while (y < pan.bottom) { drawLine(NOCHE.copy(alpha = 0.2f), Offset(pan.left, y), Offset(pan.right, y), 0.6f * u); y += 2.2f * u }
        drawPath(Path().apply {
            moveTo(pan.left + pan.width * 0.06f, pan.top + pan.height * 0.55f)
            cubicTo(pan.left + pan.width * 0.06f, pan.top + pan.height * 0.1f, pan.left + pan.width * 0.3f, pan.top + pan.height * 0.06f, pan.left + pan.width * 0.6f, pan.top + pan.height * 0.06f)
        }, CIAN_CLARO.copy(alpha = 0.22f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawRoundRect(NOCHE.copy(alpha = 0.45f), pan.topLeft, pan.size, CornerRadius(pan.height * 0.1f), style = Stroke(4f * u))
    }

    /**
     * El flexo de brazo, apagado: la base pesada, los dos tramos de varillas con sus muelles, la rótula y la pantalla cónica que
     * mira hacia abajo; todo oscuro, con el filo cian del lado que mira al monitor.
     */
    private fun DrawScope.flexo(pie: Offset, ch: Float, sup: Float) {
        val u = density
        val w = anchoW
        val t = Tono(luzMonitor(Offset(0.14f, 0.3f)), 0f, false)
        val codo = Offset(w * 0.045f, w * 0.12f)
        val cabeza = Offset(w * 0.2f, w * 0.075f)
        val col = mezcla(NEGRO_RACK, PARED, 0.35f)
        // La sombra del brazo en el panel, hacia la izquierda (lejos del monitor).
        translate(-w * 0.02f, w * 0.012f) {
            drawLine(NOCHE.copy(alpha = 0.35f), pie - Offset(0f, w * 0.02f), codo, 3f * u)
            drawLine(NOCHE.copy(alpha = 0.35f), codo, cabeza, 3f * u)
        }
        // La base.
        contacto(Offset(pie.x + w * 0.01f, pie.y), w * 0.09f, w * 0.014f, 0.7f)
        val base = Rect(pie.x - w * 0.04f, pie.y - w * 0.022f, pie.x + w * 0.04f, pie.y)
        drawRoundRect(t.c(col), base.topLeft, base.size, CornerRadius(w * 0.01f))
        drawOval(t.c(mezcla(col, ACERO_OSC, 0.3f)), Offset(base.left, base.top - w * 0.008f), Size(base.width, w * 0.016f))
        drawArc(t.b(0.8f), 270f, 90f, false, Offset(base.left, base.top - w * 0.008f), Size(base.width, w * 0.016f), style = Stroke(1f * u))
        drawRect(t.b(0.5f), Offset(base.right - w * 0.012f, base.top), Size(w * 0.012f, base.height))
        // Los dos tramos: dos varillas paralelas cada uno, con su muelle.
        val p0 = Offset(pie.x, base.top - w * 0.006f)
        listOf(p0 to codo, codo to cabeza).forEachIndexed { i, (a, b) ->
            val dir = (b - a) / (b - a).getDistance()
            val nrm = Offset(-dir.y, dir.x) * (2.2f * u)
            listOf(nrm, -nrm).forEach { o ->
                drawLine(t.c(col), a + o, b + o, 1.8f * u, StrokeCap.Round)
                drawLine(t.b(0.6f), a + o + Offset(0.6f * u, 0f), b + o + Offset(0.6f * u, 0f), 0.5f * u)
            }
            // El muelle: una espiral apretada a lo largo de un tramo.
            val m0 = a + (b - a) * 0.25f; val m1 = a + (b - a) * 0.6f
            val n = 9
            for (k in 0..n) {
                val q = m0 + (m1 - m0) * (k / n.toFloat())
                drawLine(t.c(ACERO_OSC), q + nrm * 2.2f, q - nrm * 2.2f + dir * (1.2f * u), 0.8f * u)
            }
            drawLine(t.b(0.5f), m0 + nrm * 2.2f, m1 + nrm * 2.2f, 0.4f * u)
        }
        // La rótula del codo.
        drawCircle(t.c(ACERO_OSC), 3.6f * u, codo)
        drawCircle(t.b(0.6f), 3.6f * u, codo, style = Stroke(0.8f * u))
        drawCircle(t.c(NEGRO_RACK), 1.4f * u, codo)
        // La pantalla cónica, apuntando abajo a la derecha: por fuera oscura, la boca apagada.
        val hl = w * 0.085f
        rotate(32f, cabeza) {
            val cono = poligono(cabeza.x - hl * 0.18f, cabeza.y - hl * 0.15f, cabeza.x + hl * 0.18f, cabeza.y - hl * 0.15f,
                cabeza.x + hl * 0.55f, cabeza.y + hl * 0.62f, cabeza.x - hl * 0.55f, cabeza.y + hl * 0.62f)
            drawPath(cono, t.c(col))
            clipPath(cono) {
                drawRect(t.b(0.55f), Offset(cabeza.x + hl * 0.2f, cabeza.y - hl * 0.2f), Size(hl * 0.4f, hl))
                drawRect(t.s(0.35f), Offset(cabeza.x - hl * 0.6f, cabeza.y - hl * 0.2f), Size(hl * 0.35f, hl))
            }
            drawOval(t.c(mezcla(col, ACERO_OSC, 0.4f)), Offset(cabeza.x - hl * 0.55f, cabeza.y + hl * 0.55f), Size(hl * 1.1f, hl * 0.16f))
            drawOval(mezcla(t.c(BEIS_OSC), NOCHE, 0.4f), Offset(cabeza.x - hl * 0.42f, cabeza.y + hl * 0.57f), Size(hl * 0.84f, hl * 0.11f))
            drawRoundRect(t.c(ACERO_OSC), Offset(cabeza.x - hl * 0.12f, cabeza.y - hl * 0.32f), Size(hl * 0.24f, hl * 0.2f), CornerRadius(2f * u))
            drawArc(t.b(0.8f), 300f, 100f, false, Offset(cabeza.x - hl * 0.55f, cabeza.y + hl * 0.55f), Size(hl * 1.1f, hl * 0.16f), style = Stroke(1f * u))
        }
        // El cable del flexo, que baja suelto por detrás de la base.
        val cab = Path().apply { moveTo(cabeza.x - w * 0.01f, cabeza.y); cubicTo(w * 0.12f, w * 0.15f, w * 0.03f, w * 0.2f, pie.x - w * 0.03f, pie.y - w * 0.005f) }
        drawPath(cab, t.c(NEGRO_RACK), style = Stroke(1.3f * u))
    }

    /** El teclado apoyado de canto contra la pared, detrás del módem: la carcasa beis, las hileras de teclas y su sombra. */
    private fun DrawScope.teclado(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(Offset(r.left + r.width * 0.3f, r.center.y))
        // Apoyado y un poco torcido: el borde de abajo pisa la tapa, el de arriba toca la pared.
        rotate(-3f, Offset(r.left, r.bottom)) {
            val caja = poligono(r.left, r.bottom, r.left + r.width * 0.02f, r.top, r.right - r.width * 0.01f, r.top + r.height * 0.02f, r.right, r.bottom)
            translate(w * 0.025f, -w * 0.004f) { drawPath(caja, NOCHE.copy(alpha = 0.55f)) }
            drawPath(caja, t.c(BEIS))
            clipPath(caja) {
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.45f)), r.left, r.right), r.topLeft, r.size)
                drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1.2f * u))
            }
            // Las teclas: cinco hileras, con el lado de luz arriba y la sombra abajo.
            val zona = Rect(r.left + r.width * 0.04f, r.top + r.height * 0.14f, r.right - r.width * 0.04f, r.bottom - r.height * 0.1f)
            val fh = zona.height / 5f
            for (fila in 0 until 5) {
                val n = 15
                val paso = zona.width / n
                for (k in 0 until n) {
                    if (fila == 4 && k in 5..9) continue
                    val kx = zona.left + paso * k + (if (fila % 2 == 1) paso * 0.25f else 0f)
                    val kw = if (fila == 4 && k == 4) paso * 5.8f else paso * 0.82f
                    if (kx + kw > zona.right) continue
                    val ky = zona.top + fh * fila
                    val tk = tn(Offset(kx, ky))
                    val col = if ((fila == 0 && k < 2) || (fila == 2 && k == 14) || (fila == 0 && k > 12)) BEIS_OSC else mezcla(BEIS, PAPEL, 0.3f)
                    drawRect(NOCHE.copy(alpha = 0.5f), Offset(kx, ky + fh * 0.72f), Size(kw, fh * 0.16f))
                    drawRect(tk.c(col), Offset(kx, ky), Size(kw, fh * 0.75f))
                    drawRect(tk.b(0.6f), Offset(kx, ky), Size(kw, 0.7f * u))
                }
            }
        }
    }

    /** El módem: la caja beis vista un poco desde arriba, la fila de pilotos (unos encendidos, otros no) y el cable en espiral. */
    private fun DrawScope.modem(r: Rect, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(r.left, r.top))
        val dv = Offset(w * 0.012f, -w * 0.016f)
        val arriba = poligono(r.left, r.top, r.left + dv.x, r.top + dv.y, r.right + dv.x, r.top + dv.y, r.right, r.top)
        val lado = poligono(r.right, r.top, r.right + dv.x, r.top + dv.y, r.right + dv.x, r.bottom + dv.y, r.right, r.bottom)
        contacto(Offset(r.center.x + w * 0.01f, r.bottom), r.width * 1.1f, w * 0.014f, 0.7f)
        drawPath(lado, mezcla(t.c(BEIS_OSC), NOCHE, 0.45f))
        drawPath(arriba, t.c(mezcla(BEIS, Color.White, 0.08f)))
        // Las ranuras de la tapa.
        for (k in 0 until 8) {
            val x = r.left + r.width * (0.45f + 0.06f * k)
            drawLine(NOCHE.copy(alpha = 0.5f), Offset(x, r.top + dv.y * 0.3f), Offset(x + dv.x * 0.6f, r.top + dv.y * 0.85f), 1f * u)
        }
        drawRect(t.c(BEIS), r.topLeft, r.size)
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.4f)), r.left, r.right), r.topLeft, r.size)
        drawRect(t.b(0.8f), r.topLeft, Size(r.width, 1f * u))
        drawRect(NOCHE.copy(alpha = 0.4f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
        // La franja oscura de los pilotos, con sus marcas.
        val fr = Rect(r.left + r.width * 0.08f, r.top + r.height * 0.35f, r.right - r.width * 0.08f, r.top + r.height * 0.68f)
        drawRect(t.c(mezcla(NEGRO_RACK, PARED, 0.2f)), fr.topLeft, fr.size)
        val encendidos = listOf(true, true, false, true, false, false, true, false)
        val colores = listOf(VERDE, VERDE, AMBAR, AMBAR, ROJO, VERDE, VERDE, AMBAR)
        for (k in 0 until 8) {
            val c = Offset(fr.left + fr.width * (0.08f + 0.12f * k), fr.center.y)
            piloto(c, 1.6f * u, colores[k], encendidos[k], t)
            drawRect(t.c(BEIS_OSC), Offset(c.x - 1.5f * u, fr.bottom + 1.5f * u), Size(3f * u, 0.8f * u))
        }
        // El cable de teléfono en espiral, que sale por detrás y baja por la derecha.
        for (k in 0 until 7) {
            val q = Offset(r.right + dv.x + 1f * u + k * 0.6f * u, r.top + dv.y + k * 2.4f * u)
            drawOval(t.c(mezcla(BEIS_OSC, PARED, 0.3f)), Offset(q.x - 2f * u, q.y - 1.4f * u), Size(4f * u, 2.8f * u), style = Stroke(0.8f * u))
        }
    }

    /** La lata de refresco sin marca: aluminio con una franja azul pizarra, una abolladura y la anilla. */
    private fun DrawScope.lata(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val a = w * 0.036f; val al = w * 0.066f
        val t = tn(Offset(pie.x, pie.y - al / 2))
        val ry = a * 0.16f
        val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
        val cuerpo = Path().apply {
            moveTo(r.left, r.top + ry); lineTo(r.left, r.bottom - ry)
            arcTo(Rect(r.left, r.bottom - 2 * ry, r.right, r.bottom), 180f, -180f, false)
            lineTo(r.right, r.top + ry); close()
        }
        contacto(Offset(pie.x + a * 0.2f, pie.y), a * 1.5f, 3f * u, 0.7f)
        drawPath(cuerpo, t.c(ACERO))
        clipPath(cuerpo) {
            drawRect(t.c(PARED), Offset(r.left, r.top + al * 0.35f), Size(a, al * 0.3f))
            drawRect(t.c(CIAN_OSC), Offset(r.left, r.top + al * 0.47f), Size(a, al * 0.04f))
            cilindro(r, t, 1.3f)
            drawOval(t.s(0.3f), Offset(r.left + a * 0.15f, r.top + al * 0.68f), Size(a * 0.35f, al * 0.12f))
        }
        drawOval(t.c(mezcla(ACERO, Color.White, 0.2f)), Offset(r.left, r.top), Size(a, 2 * ry))
        drawOval(t.c(ACERO_OSC), Offset(r.left + a * 0.15f, r.top + ry * 0.3f), Size(a * 0.7f, ry * 1.3f))
        drawOval(t.c(ACERO), Offset(r.left + a * 0.36f, r.top + ry * 0.5f), Size(a * 0.3f, ry * 0.8f), style = Stroke(0.8f * u))
        drawArc(t.b(0.7f), 160f, 120f, false, Offset(r.left, r.top), Size(a, 2 * ry), style = Stroke(0.8f * u))
    }

    /**
     * Tres archivadores de anillas tumbados en la esquina de la tapa, con el lomo hacia quien mira y su portaetiquetas sin
     * texto; encima, la taza beis con el asa y el café, y los cercos que dejó en la tapa del archivador de arriba.
     */
    private fun DrawScope.manuales(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val aw = w * 0.17f; val gr = w * 0.03f
        val colores = listOf(mezcla(PARED, NEGRO_RACK, 0.3f), BEIS_OSC, mezcla(ROJO, NEGRO_RACK, 0.6f))
        contacto(Offset(pie.x + w * 0.01f, pie.y), aw * 1.1f, w * 0.016f, 0.7f)
        var y = pie.y
        var arribaDe = Rect.Zero
        colores.forEachIndexed { i, col ->
            val dx = w * 0.008f * (if (i == 1) -1f else if (i == 2) 0.6f else 0f)
            val lomo = Rect(pie.x - aw / 2 + dx, y - gr, pie.x + aw / 2 + dx, y)
            val t = tn(lomo.center)
            // La tapa de arriba, que asoma hacia atrás.
            val tapa = poligono(lomo.left, lomo.top, lomo.left + w * 0.01f, lomo.top - w * 0.012f, lomo.right + w * 0.006f, lomo.top - w * 0.012f, lomo.right, lomo.top)
            drawPath(tapa, t.c(mezcla(col, Color.White, 0.08f)))
            drawRoundRect(t.c(col), lomo.topLeft, lomo.size, CornerRadius(gr * 0.3f))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.5f)), lomo.left, lomo.right), lomo.topLeft, lomo.size)
            drawRect(t.b(0.55f), Offset(lomo.left + 2f * u, lomo.top), Size(lomo.width - 4f * u, 0.9f * u))
            // El portaetiquetas sin texto y el agujero para el dedo.
            val et = Rect(lomo.left + aw * 0.12f, lomo.top + gr * 0.25f, lomo.left + aw * 0.5f, lomo.bottom - gr * 0.25f)
            drawRect(t.c(mezcla(PAPEL, col, 0.2f)), et.topLeft, et.size)
            drawRect(NOCHE.copy(alpha = 0.3f), et.topLeft, Size(et.width, 1f * u))
            drawCircle(NOCHE.copy(alpha = 0.7f), gr * 0.22f, Offset(lomo.left + aw * 0.75f, lomo.center.y))
            drawArc(t.b(0.45f), 0f, 180f, false, Offset(lomo.left + aw * 0.75f - gr * 0.22f, lomo.center.y - gr * 0.22f), Size(gr * 0.44f, gr * 0.44f), style = Stroke(0.6f * u))
            arribaDe = Rect(lomo.left, lomo.top - w * 0.012f, lomo.right, lomo.top)
            y -= gr + w * 0.002f
        }
        // Los cercos de café en la tapa de arriba.
        val tt = tn(arribaDe.center)
        listOf(0.25f to 0.9f, 0.4f to 0.75f).forEach { (fx, fs) ->
            drawOval(tt.c(CAFE).copy(alpha = 0.5f), Offset(arribaDe.left + arribaDe.width * fx, arribaDe.top + arribaDe.height * 0.1f), Size(w * 0.04f * fs, arribaDe.height * 0.8f), style = Stroke(0.8f * u))
        }
        // La taza.
        val tc = Offset(arribaDe.left + arribaDe.width * 0.68f, arribaDe.bottom - arribaDe.height * 0.35f)
        val a = w * 0.042f; val al = w * 0.048f
        val t = tn(Offset(tc.x, tc.y - al / 2))
        val ry = a * 0.2f
        val r = Rect(tc.x - a / 2, tc.y - al, tc.x + a / 2, tc.y)
        contacto(Offset(tc.x + a * 0.2f, tc.y), a * 1.4f, 2.5f * u, 0.7f)
        // El asa, a la derecha.
        drawArc(t.c(BEIS_OSC), -80f, 160f, false, Offset(r.right - a * 0.25f, r.top + al * 0.2f), Size(a * 0.5f, al * 0.5f), style = Stroke(2.4f * u))
        val cuerpo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.left + a * 0.03f, r.bottom - ry)
            arcTo(Rect(r.left + a * 0.03f, r.bottom - 2 * ry, r.right - a * 0.03f, r.bottom), 180f, -180f, false)
            lineTo(r.right, r.top); close()
        }
        drawPath(cuerpo, t.c(BEIS))
        clipPath(cuerpo) {
            cilindro(r, t, 1.2f)
            // El chorreón de café que bajó por fuera y un desconchón.
            drawRect(t.c(CAFE).copy(alpha = 0.55f), Offset(r.left + a * 0.3f, r.top), Size(a * 0.07f, al * 0.45f))
            drawOval(t.c(ACERO_OSC), Offset(r.left + a * 0.62f, r.top + al * 0.1f), Size(a * 0.12f, al * 0.07f))
        }
        drawOval(t.c(mezcla(BEIS, Color.White, 0.15f)), Offset(r.left, r.top - ry), Size(a, 2 * ry))
        drawOval(t.c(CAFE), Offset(r.left + a * 0.1f, r.top - ry * 0.6f), Size(a * 0.8f, ry * 1.3f))
        drawArc(t.b(0.6f), 160f, 120f, false, Offset(r.left, r.top - ry), Size(a, 2 * ry), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- al pie

    /**
     * La torre tumbada sin su tapa, cortada por el borde izquierdo: la carcasa beis vista un poco desde arriba, dentro la placa
     * verde con sus chips, la jaula de discos, el cable plano y el ventilador; el frente a la derecha con su bahía y el piloto.
     */
    private fun DrawScope.torre(pie: Offset, w: Float, hb: Float) {
        val u = density
        val tw = w * 0.3f; val th = hb * 0.26f
        val f = Rect(pie.x - tw / 2, pie.y - th, pie.x + tw / 2, pie.y)
        val t = tn(f.center)
        val prof = hb * 0.3f
        contacto(Offset(f.center.x + w * 0.02f, pie.y), tw * 1.1f, 6f * u, 0.8f)
        // La boca de arriba (sin tapa): el fondo, la placa verde, los chips, la jaula, el cable plano.
        val boca = poligono(f.left, f.top, f.left + w * 0.02f, f.top - prof, f.right + w * 0.02f, f.top - prof, f.right, f.top)
        drawPath(boca, t.c(mezcla(NEGRO_RACK, NOCHE, 0.3f)))
        clipPath(boca) {
            val placa = Rect(f.left - w * 0.02f, f.top - prof * 0.9f, f.left + tw * 0.6f, f.top - prof * 0.1f)
            drawRect(mezcla(t.c(Color(0xFF3C7A4A)), Color(0xFF3C7A4A), 0.25f), placa.topLeft, placa.size)
            drawRect(t.b(0.3f), placa.topLeft, Size(placa.width, 0.8f * u))
            for (k in 0 until 6) {
                val c = Rect(placa.left + placa.width * (0.15f + 0.13f * k), placa.top + placa.height * (0.2f + 0.3f * (k % 2)), placa.left + placa.width * (0.24f + 0.13f * k), placa.top + placa.height * (0.42f + 0.3f * (k % 2)))
                drawRect(t.c(NEGRO_RACK), c.topLeft, c.size)
                drawRect(t.b(0.4f), c.topLeft, Size(c.width, 0.6f * u))
            }
            drawRect(t.c(ACERO), Offset(f.left + tw * 0.66f, f.top - prof * 0.95f), Size(tw * 0.28f, prof * 0.85f))
            drawRect(t.b(0.4f), Offset(f.left + tw * 0.66f, f.top - prof * 0.95f), Size(tw * 0.28f, 0.8f * u))
            drawLine(t.c(CABLE), Offset(f.left + tw * 0.3f, f.top - prof * 0.3f), Offset(f.left + tw * 0.7f, f.top - prof * 0.6f), 5f * u)
            drawCircle(t.c(ACERO_OSC), prof * 0.32f, Offset(f.left + tw * 0.1f, f.top - prof * 0.5f), style = Stroke(1.6f * u))
        }
        // El canto de la chapa del borde y la cara.
        drawLine(t.c(BEIS), Offset(f.left, f.top), Offset(f.right, f.top), 2f * u)
        drawRect(t.c(BEIS), f.topLeft, f.size)
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.35f)), f.left, f.right), f.topLeft, f.size)
        drawRect(t.b(0.6f), f.topLeft, Size(f.width, 1f * u))
        // El frente (a la derecha): la bisel con la bahía de disquete y el piloto encendido.
        val fr = poligono(f.right, f.top, f.right + w * 0.02f, f.top - prof, f.right + w * 0.02f, f.bottom - prof * 0.3f, f.right, f.bottom)
        drawPath(fr, mezcla(t.c(BEIS_OSC), NOCHE, 0.3f))
        drawLine(t.s(0.6f), Offset(f.right + w * 0.004f, f.top + th * 0.3f), Offset(f.right + w * 0.016f, f.top + th * 0.3f - prof * 0.6f), 2f * u)
        // Rejilla y tornillos del costado.
        for (k in 0 until 6) drawRect(NOCHE.copy(alpha = 0.45f), Offset(f.left + tw * (0.35f + 0.05f * k), f.top + th * 0.35f), Size(tw * 0.02f, th * 0.4f))
        tornillo(Offset(f.right - tw * 0.05f, f.top + th * 0.25f), 1.8f * u, t)
        piloto(Offset(f.right + w * 0.01f, f.bottom - th * 0.3f), 1.5f * u, VERDE, true, t)
    }

    /** La caja de cartón con cables, cortada por el borde derecho: las solapas abiertas y los cables que se salen. */
    private fun DrawScope.cajaCables(pie: Offset, w: Float, hb: Float) {
        val u = density
        val cw = w * 0.24f; val chh = hb * 0.36f
        val c = Rect(pie.x - cw / 2, pie.y - chh, pie.x + cw / 2, pie.y)
        val t = tn(c.center)
        val carton = mezcla(TABLERO, BEIS, 0.3f)
        contacto(Offset(c.center.x, pie.y), cw * 1.1f, 6f * u, 0.8f)
        // La solapa de atrás.
        drawPath(poligono(c.left + cw * 0.05f, c.top, c.left + cw * 0.1f, c.top - chh * 0.45f, c.right, c.top - chh * 0.45f, c.right, c.top), t.c(mezcla(carton, NOCHE, 0.3f)))
        // Los cables: bucles negros y grises que asoman.
        listOf(Triple(0.25f, NEGRO_RACK, 0.5f), Triple(0.5f, CABLE, 0.65f), Triple(0.72f, NEGRO_RACK, 0.4f)).forEachIndexed { i, (fx, col, alt) ->
            val p = Path().apply {
                moveTo(c.left + cw * (fx - 0.1f), c.top + 3f * u)
                cubicTo(c.left + cw * (fx - 0.14f), c.top - chh * alt, c.left + cw * (fx + 0.16f), c.top - chh * alt, c.left + cw * (fx + 0.1f), c.top + 3f * u)
            }
            drawPath(p, t.c(col), style = Stroke(2.4f * u))
            drawPath(p, t.b(0.4f), style = Stroke(0.6f * u))
        }
        // Un cable que cuelga por fuera con su clavija.
        val cuelga = Path().apply { moveTo(c.left + cw * 0.12f, c.top + 2f * u); cubicTo(c.left - cw * 0.08f, c.top + chh * 0.2f, c.left - cw * 0.04f, c.bottom - chh * 0.3f, c.left - cw * 0.12f, c.bottom - 3f * u) }
        drawPath(cuelga, t.c(NEGRO_RACK), style = Stroke(2.2f * u))
        drawRect(t.c(NEGRO_RACK), Offset(c.left - cw * 0.16f, c.bottom - 6f * u), Size(cw * 0.08f, 5f * u))
        // El frente de cartón, la solapa de delante caída y la cinta de embalar.
        drawRect(t.c(carton), c.topLeft, c.size)
        drawRect(t.b(0.3f), c.topLeft, Size(cw * 0.06f, chh))
        drawRect(t.s(0.2f), Offset(c.left, c.top), Size(cw, chh * 0.1f))
        drawPath(poligono(c.left, c.top, c.right, c.top, c.right, c.top + chh * 0.32f, c.left + cw * 0.02f, c.top + chh * 0.28f), t.c(mezcla(carton, Color.White, 0.08f)))
        drawRect(t.c(mezcla(carton, AMBAR, 0.25f)).copy(alpha = 0.7f), Offset(c.left + cw * 0.3f, c.top + chh * 0.28f), Size(cw * 0.2f, chh * 0.72f))
        pinceladas(c, NOCHE, 3220, 6, u, 0.1f)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está esta bahía en el mueble (en fracciones del ancho): cuánto le llega del monitor y de la regleta.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val baseArriba = corona + hueco + c.filas * altoH + (c.filas - 1) * hueco + hueco
        val km = if (dentro) luzMonitor(centro) else 0.75f
        val kr = if (dentro) luzRegleta(centro, Offset(0.135f, baseArriba + base * 0.2f)) else 0f
        val t = Tono(km, kr, centro.x > MONITOR.x)
        if (c.vacio) { tapaCiega(w, h, t); return }
        val m = mezclar(c.semilla)
        translate(0f, SALIDA * h * ab) {
            when {
                c.sinTema -> disquetes(w, h, c, t)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> bandejaDisco(w, h, c, m, t, ab)
                    1 -> listados(w, h, c, m, t)
                    else -> cintas(w, h, c, m, t)
                }
            }
        }
    }

    /** Las orejas de un módulo, atornilladas a los rieles: dos pletinas a los lados con dos tornillos cada una. */
    private fun DrawScope.orejas(w: Float, h: Float, top: Float, bottom: Float, col: Color, t: Tono) {
        val u = density
        val ow = w * 0.075f
        listOf(-ow * 0.6f, w - ow * 0.4f).forEach { x ->
            drawRect(t.s(0.45f), Offset(x + 1.5f * u, top + 1.5f * u), Size(ow, bottom - top))
            drawRect(t.c(col), Offset(x, top), Size(ow, bottom - top))
            drawRect(t.b(0.5f), Offset(x, top), Size(ow, 0.9f * u))
            drawRect(t.b(0.3f), Offset(x, top), Size(0.8f * u, bottom - top))
            listOf(0.22f, 0.78f).forEach { f -> tornillo(Offset(x + ow * 0.5f, top + (bottom - top) * f), 2f * u, t) }
        }
    }

    /**
     * La cinta de rotular en relieve del tema: oscura (letra clara encima), con los extremos cortados en bisel, el brillo del
     * plástico arriba y su sombra. Lo bastante oscura en todas las filas para que el título quede a más de 7:1.
     */
    private fun DrawScope.cinta(r: Rect, col: Color, t: Tono, semilla: Int) {
        val u = density
        val corte = r.height * 0.18f
        val forma = poligono(r.left + corte, r.top, r.right, r.top, r.right - corte, r.bottom, r.left, r.bottom)
        translate(1.2f * u, 1.6f * u) { drawPath(forma, NOCHE.copy(alpha = 0.6f)) }
        // El color de la cinta apenas se enciende con la luz: nunca pasa de un tercio hacia su color.
        drawPath(forma, mezcla(mezcla(col, NOCHE, 0.45f), col, 0.35f * maxOf(t.km, t.kr)))
        clipPath(forma) {
            drawRect(t.b(0.35f), r.topLeft, Size(r.width, r.height * 0.12f))
            drawRect(NOCHE.copy(alpha = 0.35f), Offset(r.left, r.bottom - r.height * 0.12f), Size(r.width, r.height * 0.12f))
            // El grano del plástico estampado.
            for (k in 0 until 5) {
                val x = r.left + r.width * azar(semilla, k)
                drawLine(t.b(0.12f), Offset(x, r.top + 1f * u), Offset(x + r.width * 0.05f, r.top + 1f * u), 0.6f * u)
            }
        }
    }

    /** El color de la cinta de rotular según la semilla: negra, azul o roja oscura. */
    private fun colorCinta(m: Int) = when (Math.floorMod(m ushr 5, 3)) { 0 -> NEGRO_RACK; 1 -> mezcla(PARED, NOCHE, 0.2f); else -> mezcla(ROJO, NOCHE, 0.62f) }

    private fun rotuloPx(w: Float, h: Float) = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)

    /**
     * La bandeja de disco duro extraíble: el frente de plástico beis con sus rejillas, la cerradura de llave, el asa, el piloto
     * verde fijo y el ámbar de actividad (encendido al abrir), y la barra de diez segmentos encendida según cuánto guarda.
     */
    private fun DrawScope.bandejaDisco(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, ab: Float) {
        val u = density
        val fr = Rect(w * 0.02f, h * 0.04f, w * 0.98f, h * 0.96f)
        orejas(w, h, fr.top, fr.bottom, BEIS_OSC, t)
        drawRect(t.s(0.5f), Offset(fr.left + 2f * u, fr.top + 2.5f * u), fr.size)
        drawRoundRect(t.c(BEIS), fr.topLeft, fr.size, CornerRadius(2f * u))
        drawRect(t.s(0.35f), Offset(fr.left, fr.top), Size(fr.width, fr.height * 0.06f))
        drawRect(t.b(0.6f), fr.topLeft, Size(fr.width, 1f * u))
        drawRect(if (t.izq) t.b(0.3f) else t.s(0.3f), fr.topLeft, Size(fr.width * 0.04f, fr.height))
        drawRect(if (t.izq) t.s(0.3f) else t.b(0.2f), Offset(fr.right - fr.width * 0.05f, fr.top), Size(fr.width * 0.05f, fr.height))
        pinceladas(fr, NOCHE, c.semilla, 4, u, 0.1f)
        // La rejilla: ranuras horizontales en el centro de arriba.
        val rej = Rect(w * 0.3f, h * 0.12f, w * 0.72f, h * 0.3f)
        var y = rej.top
        while (y < rej.bottom) {
            drawRoundRect(NOCHE.copy(alpha = 0.8f), Offset(rej.left, y), Size(rej.width, h * 0.022f), CornerRadius(h * 0.011f))
            drawRect(t.b(0.45f), Offset(rej.left + 2f * u, y + h * 0.022f), Size(rej.width - 4f * u, 0.6f * u))
            y += h * 0.045f
        }
        // La cerradura de llave, a la izquierda.
        val ce = Offset(w * 0.15f, h * 0.24f)
        val rc = h * 0.075f
        drawCircle(t.s(0.5f), rc * 1.05f, ce + Offset(1f * u, 1.2f * u))
        drawCircle(t.c(ACERO), rc, ce)
        drawCircle(t.c(mezcla(ACERO, Color.White, 0.2f)), rc * 0.72f, ce)
        drawArc(t.b(0.8f), 180f, 90f, false, Offset(ce.x - rc, ce.y - rc), Size(rc * 2, rc * 2), style = Stroke(1f * u))
        rotate(if (Math.floorMod(m ushr 9, 2) == 0) 0f else 90f, ce) {
            drawRoundRect(NOCHE, Offset(ce.x - rc * 0.12f, ce.y - rc * 0.45f), Size(rc * 0.24f, rc * 0.9f), CornerRadius(rc * 0.1f))
        }
        // Los pilotos: verde fijo y ámbar de actividad.
        piloto(Offset(w * 0.85f, h * 0.15f), 1.8f * u, VERDE, true, t)
        piloto(Offset(w * 0.85f, h * 0.27f), 1.8f * u, AMBAR, ab > 0f || Math.floorMod(m ushr 11, 3) == 0, t)
        listOf(0.15f, 0.27f).forEach { fy -> drawRect(t.c(BEIS_OSC), Offset(w * 0.79f, h * fy - 0.4f * u), Size(w * 0.03f, 0.9f * u)) }
        // La barra de diez segmentos: cuánto guarda.
        val bar = Rect(w * 0.3f, h * 0.345f, w * 0.72f, h * 0.395f)
        drawRect(t.c(mezcla(NEGRO_RACK, NOCHE, 0.3f)), Rect(bar.left - 2f * u, bar.top - 1.5f * u, bar.right + 2f * u, bar.bottom + 1.5f * u).topLeft, Size(bar.width + 4f * u, bar.height + 3f * u))
        val llenos = c.n.coerceIn(0, 10)
        val sg = bar.width / 10f
        for (k in 0 until 10) {
            val col = if (k >= 8) AMBAR else VERDE
            val on = k < llenos
            drawRect(if (on) col else t.c(mezcla(col, NOCHE, 0.82f)), Offset(bar.left + sg * k + 0.6f * u, bar.top), Size(sg - 1.2f * u, bar.height))
        }
        if (llenos > 0) drawRect(VERDE.copy(alpha = 0.18f), Offset(bar.left, bar.top - 2f * u), Size(sg * llenos, bar.height + 4f * u))
        // El asa: una barra en U con sus pies, y su sombra.
        val ay = h * 0.48f
        val asa = Path().apply { moveTo(w * 0.24f, h * 0.555f); lineTo(w * 0.25f, ay); lineTo(w * 0.75f, ay); lineTo(w * 0.76f, h * 0.555f) }
        translate(2f * u, 2.6f * u) { drawPath(asa, t.s(0.5f), style = Stroke(3.4f * u)) }
        drawPath(asa, t.c(NEGRO_RACK), style = Stroke(3.4f * u, cap = StrokeCap.Round))
        drawLine(t.b(0.7f), Offset(w * 0.26f, ay - 1.2f * u), Offset(w * 0.74f, ay - 1.2f * u), 0.9f * u)
        cinta(rotuloPx(w, h), colorCinta(m), t, c.semilla)
    }

    /**
     * La bandeja de alambre con su pila de listados de papel continuo: más alta cuanto más guarda, con las bandas verdes, los
     * márgenes perforados, la hoja de arriba que se sale por delante y el portaetiquetas de la cinta.
     */
    private fun DrawScope.listados(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val piso = h * 0.62f
        val alto = h * (0.08f + 0.042f * c.n.coerceIn(1, 10))
        val pila = Rect(w * 0.1f, piso - alto, w * 0.9f, piso)
        // La pila: hojas de canto, alternando el papel y el verde, con su lado de luz.
        // La pila de canto: los pliegues del papel continuo, cada uno un poco corrido, con su lomo de luz y su raya de sombra.
        val papel = mezcla(t.c(PAPEL), PAPEL, 0.12f)
        drawRect(papel, pila.topLeft, pila.size)
        var y = pila.top
        var k = 0
        val pliegue = 3.2f * u
        while (y < pila.bottom) {
            val dx = w * 0.014f * (if (k % 2 == 0) 1f else -1f) * (0.4f + 0.6f * azar(c.semilla, k))
            val hojaR = Rect(pila.left + dx, y, pila.right + dx, minOf(y + pliegue, pila.bottom))
            drawRoundRect(papel, hojaR.topLeft, hojaR.size, CornerRadius(pliegue / 2))
            drawRect(t.b(0.5f), Offset(hojaR.left + pliegue / 2, y + 0.3f * u), Size(hojaR.width - pliegue, 0.6f * u))
            drawRect(t.s(0.3f), Offset(hojaR.left, hojaR.bottom - 0.7f * u), Size(hojaR.width, 0.7f * u))
            // Un borde verde asoma de vez en cuando.
            if (k % 4 == 1) drawRect(t.c(RAYA), Offset(hojaR.left + hojaR.width * 0.1f, y + pliegue * 0.35f), Size(hojaR.width * 0.8f, pliegue * 0.3f))
            y += pliegue; k++
        }
        drawRect(t.s(0.2f), Offset(pila.right - pila.width * 0.1f, pila.top), Size(pila.width * 0.12f, pila.height))
        // La hoja de arriba, vista desde arriba, con sus bandas verdes y los agujeros, y la punta que cuelga por delante.
        val ta = poligono(pila.left, pila.top, pila.left + w * 0.03f, pila.top - h * 0.08f, pila.right - w * 0.01f, pila.top - h * 0.08f, pila.right, pila.top)
        drawPath(ta, mezcla(t.c(PAPEL), PAPEL, 0.2f))
        clipPath(ta) {
            for (j in 0 until 2) drawRect(mezcla(t.c(RAYA), RAYA, 0.2f), Offset(pila.left + w * 0.06f, pila.top - h * (0.068f - 0.036f * j)), Size(pila.width - w * 0.1f, h * 0.018f))
            // Los agujeros de arrastre de los márgenes.
            for (j in 0 until 3) {
                val yy = pila.top - h * (0.015f + 0.024f * j)
                drawCircle(t.c(mezcla(NEGRO_RACK, NOCHE, 0.3f)), 1.2f * u, Offset(pila.left + w * (0.03f + 0.007f * j), yy))
                drawCircle(t.c(mezcla(NEGRO_RACK, NOCHE, 0.3f)), 1.2f * u, Offset(pila.right - w * 0.025f, yy))
            }
        }
        drawLine(t.b(0.7f), Offset(pila.left, pila.top), Offset(pila.right, pila.top), 0.9f * u)
        if (c.n >= 3) {
            // La hoja de arriba se sale y cae por delante de la pila, con su sombra.
            val caida = minOf(h * 0.13f, pila.height * 0.7f)
            val cu = poligono(pila.left + w * 0.42f, pila.top, pila.left + w * 0.68f, pila.top, pila.left + w * 0.7f, pila.top + caida * 0.85f, pila.left + w * 0.44f, pila.top + caida)
            translate(2f * u, 2f * u) { drawPath(cu, t.s(0.4f)) }
            drawPath(cu, mezcla(t.c(PAPEL), PAPEL, 0.22f))
            clipPath(cu) {
                drawRect(mezcla(t.c(RAYA), RAYA, 0.2f), Offset(pila.left + w * 0.42f, pila.top + caida * 0.3f), Size(w * 0.3f, caida * 0.22f))
                drawRect(t.s(0.12f), Offset(pila.left + w * 0.62f, pila.top), Size(w * 0.08f, caida))
            }
            drawLine(t.b(0.6f), Offset(pila.left + w * 0.42f, pila.top), Offset(pila.left + w * 0.68f, pila.top), 1f * u)
        }
        // La bandeja de alambre: el marco y las varillas de delante, cromadas y con su filo.
        val band = Rect(w * 0.05f, h * 0.56f, w * 0.95f, h * 0.97f)
        val alambre = t.c(ACERO)
        drawLine(alambre, Offset(band.left, band.top), Offset(band.right, band.top), 1.8f * u, StrokeCap.Round)
        drawLine(alambre, Offset(band.left, band.bottom), Offset(band.right, band.bottom), 1.8f * u, StrokeCap.Round)
        var x = band.left
        while (x <= band.right + 0.5f) {
            drawLine(t.s(0.45f), Offset(x + 1.2f * u, band.top + 1f * u), Offset(x + 1.2f * u, band.bottom), 1.2f * u)
            drawLine(alambre, Offset(x, band.top), Offset(x, band.bottom), 1.2f * u)
            x += band.width / 12f
        }
        drawLine(t.b(0.7f), Offset(band.left, band.top - 0.8f * u), Offset(band.right, band.top - 0.8f * u), 0.7f * u)
        // El portaetiquetas de chapa colgado del alambre, con la cinta.
        val r = rotuloPx(w, h)
        val pl = Rect(r.left - 3f * u, r.top - 2.5f * u, r.right + 3f * u, r.bottom + 2.5f * u)
        drawRect(t.s(0.5f), Offset(pl.left + 1.5f * u, pl.top + 2f * u), pl.size)
        drawRect(t.c(ACERO_OSC), pl.topLeft, pl.size)
        drawRect(t.b(0.5f), pl.topLeft, Size(pl.width, 0.9f * u))
        cinta(r, colorCinta(m), t, c.semilla)
        orejas(w, h, h * 0.56f, h * 0.97f, NEGRO_RACK, t)
    }

    /**
     * La caja de cartuchos de cinta: de plástico negro, con los cartuchos de pie (uno por ficha, hasta seis) que muestran el
     * canto con su etiqueta de papel escrita a mano; alguno más alzado o ladeado; delante, la cinta del tema.
     */
    private fun DrawScope.cintas(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val caja = Rect(w * 0.06f, h * 0.48f, w * 0.94f, h * 0.96f)
        val n = c.n.coerceIn(1, 6)
        val paso = (caja.width - w * 0.06f) / 6f
        val cols = listOf(mezcla(NEGRO_RACK, PARED, 0.3f), BEIS_OSC, mezcla(PARED, NEGRO_RACK, 0.2f), mezcla(NEGRO_RACK, ACERO_OSC, 0.4f))
        // El fondo de la caja.
        drawRect(t.c(mezcla(BEIS_OSC, NOCHE, 0.55f)), Offset(caja.left + w * 0.02f, caja.top - h * 0.05f), Size(caja.width - w * 0.04f, h * 0.06f))
        for (i in 0 until n) {
            val x = caja.left + w * 0.03f + paso * i
            val alza = h * (if (azar(c.semilla, i) > 0.82f) 0.07f else 0.02f * azar(c.semilla + 1, i))
            val car = Rect(x + 1.2f * u, h * 0.1f - alza, x + paso - 1.2f * u, caja.top + h * 0.1f)
            val tc = t
            val giro = if (i == n - 1 && n < 6) 9f else 0f
            rotate(giro, Offset(car.right, car.bottom)) {
                val col = cols[Math.floorMod(m ushr (i % 4), 4)]
                drawRect(tc.c(col), car.topLeft, car.size)
                drawRect(tc.b(0.55f), car.topLeft, Size(car.width, 0.9f * u))
                drawRect(if (tc.izq) tc.b(0.3f) else tc.s(0.35f), car.topLeft, Size(car.width * 0.18f, car.height))
                drawRect(tc.s(0.45f), Offset(car.right - car.width * 0.2f, car.top), Size(car.width * 0.2f, car.height))
                // La etiqueta de papel escrita a mano, con un renglón de bolígrafo vertical.
                val et = Rect(car.left + car.width * 0.16f, car.top + car.height * 0.1f, car.right - car.width * 0.2f, car.top + car.height * 0.7f)
                drawRect(tc.c(mezcla(PAPEL, if (i % 3 == 0) NOTA else PAPEL, 0.4f)), et.topLeft, et.size)
                drawRect(tc.c(if (i % 2 == 0) RAYA else mezcla(ROJO, PAPEL, 0.4f)), et.topLeft, Size(et.width, et.height * 0.08f))
                // Lo escrito a mano, de canto: dos renglones de bolígrafo.
                rotate(90f, et.center) {
                    garabatos(Rect(et.center.x - et.height * 0.38f, et.center.y - et.width * 0.32f, et.center.x + et.height * 0.38f, et.center.y + et.width * 0.32f), 2, tc.c(mezcla(PARED, NOCHE, 0.2f)), c.semilla + i, 1f * u)
                }
                // La placa de aluminio de la base del cartucho, de canto.
                drawRect(tc.c(ACERO), Offset(car.left, car.top + car.height * 0.74f), Size(car.width, car.height * 0.1f))
                drawRect(tc.b(0.6f), Offset(car.left, car.top + car.height * 0.74f), Size(car.width, 0.7f * u))
                // La ventanilla de la cinta, abajo.
                drawRect(tc.c(mezcla(NOCHE, CAFE, 0.3f)), Offset(car.left + car.width * 0.25f, car.top + car.height * 0.86f), Size(car.width * 0.45f, car.height * 0.06f))
            }
        }
        // El frente de la caja: plástico negro con un filo y dos rayones.
        drawRect(t.s(0.5f), Offset(caja.left, caja.top - 2f * u), Size(caja.width, 3f * u))
        drawRoundRect(t.c(mezcla(BEIS_OSC, PARED, 0.25f)), caja.topLeft, caja.size, CornerRadius(2f * u))
        pinceladas(caja, NOCHE, c.semilla + 5, 4, u, 0.1f)
        drawRect(t.b(0.6f), caja.topLeft, Size(caja.width, 1f * u))
        drawRect(t.s(0.35f), Offset(caja.right - caja.width * 0.06f, caja.top), Size(caja.width * 0.06f, caja.height))
        for (k in 0 until 2) drawLine(t.b(0.3f), Offset(caja.left + caja.width * (0.1f + 0.7f * k), caja.top + caja.height * 0.12f), Offset(caja.left + caja.width * (0.18f + 0.7f * k), caja.top + caja.height * 0.08f), 0.6f * u)
        cinta(rotuloPx(w, h), colorCinta(m + 1), t, c.semilla)
    }

    /** Lo que no tiene tema: una caja de cartón con disquetes y discos sueltos sin etiquetar, ladeados (más cuanto más guarda). */
    private fun DrawScope.disquetes(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val caja = Rect(w * 0.06f, h * 0.46f, w * 0.94f, h * 0.96f)
        val carton = mezcla(TABLERO, BEIS, 0.3f)
        drawRect(t.c(mezcla(carton, NOCHE, 0.35f)), Offset(caja.left + w * 0.02f, caja.top - h * 0.06f), Size(caja.width - w * 0.04f, h * 0.07f))
        val n = (2 + c.n).coerceIn(3, 8)
        for (i in 0 until n) {
            val cx = caja.left + caja.width * (0.12f + 0.76f * i / (n - 1f))
            val g = -18f + 36f * azar(c.semilla, i)
            val grande = i % 3 == 1
            val lado = if (grande) h * 0.42f else h * 0.32f
            val r = Rect(cx - lado / 2, caja.top + h * 0.1f - lado * (0.75f + 0.2f * azar(c.semilla + 3, i)), cx + lado / 2, caja.top + h * 0.1f + lado * 0.25f)
            rotate(g, Offset(cx, caja.top + h * 0.05f)) {
                drawRect(t.s(0.45f), r.translate(2f * u, 2f * u).topLeft, r.size)
                if (grande) {
                    // El disco flexible de cinco y cuarto: funda negra, el agujero central y la ventana de lectura.
                    drawRect(t.c(NEGRO_RACK), r.topLeft, r.size)
                    drawRect(t.b(0.5f), r.topLeft, Size(r.width, 0.9f * u))
                    drawCircle(t.c(mezcla(CAFE, NEGRO_RACK, 0.5f)), lado * 0.16f, Offset(r.center.x, r.top + lado * 0.3f))
                    drawCircle(NOCHE, lado * 0.08f, Offset(r.center.x, r.top + lado * 0.3f))
                } else {
                    // El de tres y medio: plástico de color, obturador de metal y la etiqueta en blanco.
                    val col = listOf(mezcla(PARED, NEGRO_RACK, 0.2f), BEIS_OSC, mezcla(ROJO, NEGRO_RACK, 0.55f), CIAN_OSC)[Math.floorMod(c.semilla + i, 4)]
                    drawRect(t.c(col), r.topLeft, r.size)
                    drawRect(t.b(0.5f), r.topLeft, Size(r.width, 0.9f * u))
                    drawRect(t.c(ACERO), Offset(r.left + lado * 0.25f, r.top), Size(lado * 0.45f, lado * 0.3f))
                    drawRect(t.c(mezcla(PAPEL, BEIS_OSC, 0.2f)), Offset(r.left + lado * 0.1f, r.top + lado * 0.42f), Size(lado * 0.8f, lado * 0.4f))
                }
            }
        }
        // El frente de cartón, con su solapa doblada y una mancha.
        drawRect(t.c(carton), caja.topLeft, caja.size)
        drawRect(t.c(mezcla(carton, Color.White, 0.1f)), Offset(caja.left, caja.top - 1.5f * u), Size(caja.width, 3f * u))
        drawRect(if (t.izq) t.b(0.3f) else t.s(0.3f), caja.topLeft, Size(caja.width * 0.06f, caja.height))
        drawRect(t.s(0.35f), Offset(caja.right - caja.width * 0.07f, caja.top), Size(caja.width * 0.07f, caja.height))
        pinceladas(caja, NOCHE, c.semilla, 5, u, 0.1f)
        cinta(rotuloPx(w, h), mezcla(ROJO, NOCHE, 0.62f), t, c.semilla)
    }

    /** Una bahía sin tema: la tapa ciega beis atornillada a los rieles, con sus ranuras, unos rayones y el piloto apagado. */
    private fun DrawScope.tapaCiega(w: Float, h: Float, t: Tono) {
        val u = density
        val tapa = Rect(w * 0.02f, h * 0.06f, w * 0.98f, h * 0.94f)
        orejas(w, h, tapa.top, tapa.bottom, BEIS_OSC, t)
        drawRect(t.s(0.5f), Offset(tapa.left + 2f * u, tapa.top + 2.5f * u), tapa.size)
        drawRect(t.c(mezcla(BEIS, BEIS_OSC, 0.4f)), tapa.topLeft, tapa.size)
        drawRect(t.b(0.5f), tapa.topLeft, Size(tapa.width, 1f * u))
        drawRect(t.s(0.3f), Offset(tapa.right - tapa.width * 0.05f, tapa.top), Size(tapa.width * 0.05f, tapa.height))
        pinceladas(tapa, NOCHE, 3230, 6, u, 0.09f)
        // Las ranuras verticales de ventilación, a la derecha.
        var x = w * 0.5f
        while (x < w * 0.85f) {
            drawRoundRect(NOCHE.copy(alpha = 0.7f), Offset(x, h * 0.3f), Size(w * 0.016f, h * 0.4f), CornerRadius(w * 0.008f))
            drawRect(t.b(0.3f), Offset(x + w * 0.016f, h * 0.3f), Size(0.6f * u, h * 0.4f))
            x += w * 0.045f
        }
        for (k in 0 until 3) drawLine(t.b(0.3f), Offset(w * (0.1f + 0.1f * k), h * (0.75f + 0.04f * k)), Offset(w * (0.22f + 0.1f * k), h * (0.72f + 0.04f * k)), 0.6f * u)
        piloto(Offset(w * 0.18f, h * 0.22f), 2.2f * u, VERDE, false, t)
    }

    override val rotulo = Rect(0.035f, 0.63f, 0.965f, 0.9f)
    override fun tintaRotulo(oscuro: Boolean) = TEXTO
    override val letraRotulo: FontFamily = Letra.terminal
    override val salida = SALIDA
    override val placa = Rect(0.58f, 0.08f, 0.9f, 0.34f)
    override fun tintaPlaca(oscuro: Boolean) = TEXTO

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La pantalla negra con sus líneas de barrido, los números de línea y el marco.
        drawRect(NEGRO)
        var y = 0f
        while (y < h) { drawLine(CIAN.copy(alpha = 0.03f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        y = 24 * u
        while (y < h - 10 * u) { drawLine(TENUE.copy(alpha = 0.5f), Offset(10 * u, y), Offset(18 * u, y), 2 * u); y += 22 * u }
        drawLine(TENUE.copy(alpha = 0.4f), Offset(24 * u, 0f), Offset(24 * u, h), 1f * u)
        drawRect(CIAN_OSC, Offset(1 * u, 1 * u), Size(w - 2 * u, h - 2 * u), style = Stroke(1.4f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 28.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(3.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        val barra = altoBarra(grande).toPx()
        // La barra de título en cian, con los tres botones.
        drawRect(CIAN.copy(alpha = 0.85f), Offset.Zero, Size(w, barra))
        listOf(0, 1, 2).forEach { k -> drawCircle(NEGRO.copy(alpha = 0.7f), barra * 0.17f, Offset(w - barra * (0.7f + k * 0.6f), barra / 2)) }
        // El canal de los números de línea y las líneas de barrido.
        val gx = 7 * u * g
        drawLine(TENUE.copy(alpha = 0.45f), Offset(gx, barra), Offset(gx, h), 0.8f * u)
        var y = barra + 8 * u * g
        while (y < h - 4 * u) { drawLine(TENUE.copy(alpha = 0.5f), Offset(2 * u * g, y), Offset(gx - 2 * u, y), 1.2f * u); y += 14 * u * g }
        y = barra
        while (y < h) { drawLine(CIAN.copy(alpha = 0.035f), Offset(0f, y), Offset(w, y), 1f * u); y += 3 * u }
        drawRect(CIAN_OSC, Offset.Zero, size, style = Stroke(1.2f * u))
    }

    override val margenes = MargenesFicha(0.dp, 14.dp, 10.dp, 0.dp, 24.dp, 22.dp)
    override val proporcionFicha = 1.1f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.75f
    override val barra = true
    override fun altoBarra(grande: Boolean): Dp = if (grande) 30.dp else 16.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // [OK] en vídeo inverso.
            "hecho" -> selloHecho(CIAN, p.color, cuadrado = true)
            "convence" -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y); lineTo(c.x - s * 0.1f, c.y + s * 0.28f); lineTo(c.x + s * 0.38f, c.y - s * 0.32f) }, CIAN, style = Stroke(s * 0.12f))
            "piensa" -> for (k in -1..1) drawRect(CIAN, Offset(c.x + k * s * 0.3f - s * 0.08f, c.y - s * 0.08f), Size(s * 0.16f, s * 0.16f))
            "duda" -> drawRect(TENUE, Offset(c.x - s * 0.3f, c.y - s * 0.3f), Size(s * 0.6f, s * 0.6f), style = Stroke(s * 0.1f))
            "desacuerdo" -> aspa(MAGENTA)
            else -> {}
        }
    }

    /** El cursor de bloque, encendido. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRect(CIAN.copy(alpha = 0.25f), Offset(c.x - s * 0.36f, c.y - s * 0.48f), Size(s * 0.72f, s * 0.96f))
        drawRect(CIAN, Offset(c.x - s * 0.24f, c.y - s * 0.38f), Size(s * 0.48f, s * 0.76f))
    }

    override val desorden = 0f
}
