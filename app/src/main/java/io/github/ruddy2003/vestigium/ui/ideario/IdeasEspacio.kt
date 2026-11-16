package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Espacio
// =====================================================================

/**
 * Espacio: el puesto de la ingeniera de vuelo en el módulo de mantenimiento de una nave habitada desde hace quince años, la
 * pared de racks donde guarda sus registros. Es un interior sin ventana: un mamparo de paneles claros con sus juntas, sus
 * tornillos de cuarto de vuelta, tiras de velcro, cinta adhesiva de colores, rótulos de plantilla y el conducto de aire que
 * cruza arriba. De día alumbran las luminarias del techo, fuera del cuadro arriba a la izquierda. El mueble es un bastidor de
 * racks de aluminio anodizado: los dos postes con su fila de agujeros, el larguero de arriba con su plantilla y su cinta, los
 * travesaños entre filas, el zócalo de chapa con sus rejillas, las patas niveladoras atornilladas a la rejilla del suelo y una
 * cincha naranja colgando de un poste. Encima, el punto focal, descentrado a la izquierda: el casco del traje con su visor
 * dorado, sujeto en su soporte con una cincha; a su izquierda, el ukelele sujeto a la pared con otra cincha (baja por delante
 * del canto del bastidor), el calendario de papel tachado a mano con una foto pegada con cinta encima y la lámpara de turno con
 * su jaula; a la derecha, unos calcetines antideslizantes colgados del conducto con una pinza, la tableta con un diagrama
 * sujeta con velcro, la pantalla de telemetría con su onda en su brazo, las bolsas de bebida con su pajita sujetas con una goma
 * y la caja de herramientas abierta, con cada herramienta en su hueco de espuma y uno vacío. Al pie, el extintor en su
 * abrazadera (cortado por la izquierda), el contenedor de carga con sus cinchas (cortado por la derecha y por abajo) y la
 * rejilla del suelo con sus luces guía. Cada tema es, según la semilla, un módulo de rack con sus asas, sus LED de estado (más
 * encendidos cuanto más guarda), su pantallita con onda y el tema en un visor LCD; un cajón de carga de tela con cremallera, más
 * abultado cuanto más guarda, sujeto con una cincha y con su etiqueta en un parche de velcro; o una bandeja de cartuchos de
 * memoria (más cartuchos con más fichas) con su tarjeta en el frente. Lo que no tiene tema va en una bolsa de malla con cosas
 * sueltas, colgada de un mosquetón, con el rótulo en una tira de cinta. Un hueco libre es la bahía con su tapa ciega y una
 * cincha suelta. Al abrir uno, sale hacia delante. De noche (el turno de descanso) todo se hunde en azul hondo: alumbra la
 * lámpara de turno, ámbar y rasante (halo en el mamparo, el visor que recoge su reflejo, filos cálidos y las dos primeras filas
 * encendidas, las de abajo casi perdidas), las pantallas cian tiñen lo que tienen cerca y cada LED y cada luz guía lleva su
 * halo chico. Por dentro, el panel con su retícula. Las fichas son registros de misión en vidrio oscuro con corchetes en las
 * esquinas y una barra de progreso.
 */
object IdeasEspacio : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta (la del cuadro del tema)
    /** El mamparo: paneles blancos cálidos, su sombra y sus juntas. */
    private val MAMPARO = Color(0xFFDDD9CF)
    private val MAMPARO_OSC = Color(0xFFADAAA6)
    /** El aluminio anodizado del bastidor, su luz y su sombra. */
    private val ALU = Color(0xFFB8BFCB)
    private val ALU_LUZ = Color(0xFFE4E8EE)
    private val ALU_OSC = Color(0xFF6E7688)
    /** La goma, la espuma y las carcasas oscuras. */
    private val GRAFITO = Color(0xFF353A4E)
    /** La cincha, la cinta y lo que avisa. */
    private val CINCHA = Color(0xFFE8843C)
    /** La tela cruda de las bolsas y el casco blanco. */
    private val TELA = Color(0xFFE2D8C4)
    private val CASCO = Color(0xFFEDEAE2)
    /** El azul de tela de la nave. */
    private val TELA_AZUL = Color(0xFF4E6E9C)
    /** El rojo del extintor y el verde de las cosas vivas y de los LED. */
    private val ROJO = Color(0xFFD24A3C)
    private val VERDE = Color(0xFF5E9A52)
    private val LED = Color(0xFF7CE08A)
    /** Las pantallas, la luz de turno y el oro del visor. */
    private val CIAN = Color(0xFF58D8D0)
    private val AMBAR = Color(0xFFFFB25E)
    private val ORO = Color(0xFFD9A441)
    /** La madera del ukelele, el papel y la tinta. */
    private val MADERA = Color(0xFFB07A44)
    private val PAPEL = Color(0xFFF2ECDC)
    private val TINTA = Color(0xFF1E2333)
    /** El visor LCD de los módulos, verdoso pálido. */
    private val LCD = Color(0xFFDCE6D0)
    /** La noche de la nave: azul hondo. */
    private val NOCHE = Color(0xFF0D1226)

    // ---------------------------------------------------------------- de la paleta de siempre (el interior y las fichas, sin cambios)
    private val VACIO = Color(0xFF050A18)
    private val PANEL_LUZ = Color(0xFF2A3654)
    private val AZUL = Color(0xFF8AB4FF)
    private val NARANJA = Color(0xFFFFB86B)
    private val TEXTO = Color(0xFFE8ECF8)

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.45f
    override val corona = 0.66f
    override val base = 0.27f
    override val lado = 0.09f
    override val hueco = 0.045f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** La tapa del bastidor: su fondo, su canto de frente y dónde pisan las cosas, en fracciones del ancho. */
    private const val SUP = 0.578f
    private const val TOPE = 0.6f
    private const val PIE = 0.592f
    /** Los postes del bastidor, en fracciones del ancho. */
    private const val IZQ = 0.03f
    private const val DER = 0.97f
    /** Cuánto sale el contenido de un compartimento al abrirlo, en fracción de su alto. */
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- la luz y la noche

    /** La lámpara de turno (ámbar), la pantalla de telemetría y la tableta, en fracciones del ancho. */
    private val LAMPARA = Offset(0.205f, 0.375f)
    private val PANTALLA = Offset(0.86f, 0.2f)
    private val TABLETA = Offset(0.66f, 0.27f)

    /**
     * Cómo cae la luz de la lámpara por el frente del bastidor (y, k), con 9 temas: la tapa y la primera fila, encendidas; la
     * segunda, a medias; la tercera, tenue; la cuarta y la quinta, casi perdidas.
     */
    private val CAIDA = listOf(0.6f to 0.85f, 0.84f to 0.78f, 1.15f to 0.52f, 1.46f to 0.22f, 1.78f to 0.08f, 2.09f to 0.03f, 2.4f to 0.025f, 3f to 0.02f)

    /** Cuánto le llega de la lámpara ámbar a un punto [p] (en fracciones del ancho). */
    private fun ambar(p: Offset): Float {
        val dx = p.x - LAMPARA.x
        val dy = (p.y - LAMPARA.y) * (if (p.y > LAMPARA.y) 0.8f else 1f)
        val radial = 1.35f * exp(-3.3f * sqrt(dx * dx + dy * dy))
        var k = 0f
        if (p.y >= CAIDA.first().first) {
            k = CAIDA.last().second
            for (i in 0 until CAIDA.size - 1) {
                val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
                if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
            }
            k *= (1f - 0.4f * (p.x - 0.3f).coerceAtLeast(0f)).coerceIn(0.72f, 1f)
        }
        return maxOf(radial, k).coerceIn(0.02f, 1f)
    }

    /** Cuánto le llega del cian de las pantallas a un punto [p]. */
    private fun cian(p: Offset): Float {
        val a = (p - PANTALLA).getDistance(); val b = (p - TABLETA).getDistance()
        return (0.9f * exp(-4.6f * a) + 0.4f * exp(-6f * b)).coerceIn(0f, 0.9f)
    }

    /** El color propio de algo, hundido en la noche: oscuro, de su mismo tono, con el azul del aire. */
    private fun hondo(c: Color) = Color(c.red * 0.1f + NOCHE.red * 0.78f, c.green * 0.1f + NOCHE.green * 0.78f, c.blue * 0.1f + NOCHE.blue * 0.78f, c.alpha)

    /** Cómo se pinta algo según dónde está: de día ([dia], un poco más claro arriba); de noche, según [k] (ámbar) y [kc] (cian). */
    private class Tono(val o: Boolean, val k: Float, val kc: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else if (dia < 0f) mezcla(col, SOMBRA_MUEBLE, -dia) else col
        var v = mezcla(hondo(col), col, (0.98f * k + 0.5f * kc).coerceAtMost(0.88f))
        v = mezcla(v, AMBAR, 0.17f * k)
        return mezcla(v, CIAN, 0.2f * kc)
    }

    /** La sombra: de día, violeta hondo; de noche, el azul de la nave. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo ámbar de la lámpara (o el cian, junto a las pantallas). */
    private fun Tono.b(a: Float): Color {
        if (!o) return luzMueble(a)
        return if (kc > k * 0.9f) mezcla(CIAN, LUZ_MUEBLE, 0.3f).copy(alpha = (a * 0.85f * kc).coerceIn(0f, 1f))
        else AMBAR.copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))
    }

    /** Lo que sabe el mueble que se está pintando: si es de noche y su ancho. */
    private var esNoche = false
    private var anchoW = 1f

    private fun tono(o: Boolean, f: Offset, dia: Float = 0f) = Tono(o, if (o) ambar(f) else 0f, if (o) cian(f) else 0f, dia)

    /** El tono en un punto [p] en píxeles del mueble. */
    private fun tn(p: Offset, dia: Float = 0f) = tono(esNoche, p / anchoW, dia)

    // ---------------------------------------------------------------- ayudantes

    /** Un tornillo de cuarto de vuelta: la cabeza de aluminio, su ranura girada y su brillo. */
    private fun DrawScope.tornillo(c: Offset, r: Float, t: Tono, giro: Float) {
        drawCircle(t.s(0.3f), r, c + Offset(r * 0.45f, r * 0.5f))
        drawCircle(t.c(ALU_LUZ), r, c)
        drawCircle(t.c(ALU_OSC), r, c, style = Stroke(r * 0.22f))
        val d = Offset(cos(giro), sin(giro)) * (r * 0.68f)
        drawLine(t.c(GRAFITO), c - d, c + d, r * 0.32f, StrokeCap.Round)
        drawCircle(t.b(0.7f), r * 0.28f, c - Offset(r * 0.38f, r * 0.38f))
    }

    /** Un halo chico de algo que brilla por sí mismo (un LED, una luz guía): dos aros tenues y el punto, sin degradados. */
    private fun DrawScope.aureola(c: Offset, r: Float, col: Color, noche: Boolean) {
        if (noche) {
            drawCircle(col.copy(alpha = 0.09f), r * 3.4f, c)
            drawCircle(col.copy(alpha = 0.18f), r * 2.1f, c)
        }
        drawCircle(col, r, c)
        drawCircle(Color.White.copy(alpha = 0.55f), r * 0.38f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una tira de velcro: el gris peludo y unos puntitos. */
    private fun DrawScope.velcro(r: Rect, t: Tono, semilla: Int) {
        val u = density
        drawRect(t.c(mezcla(MAMPARO_OSC, ALU_OSC, 0.5f)), r.topLeft, r.size)
        for (k in 0 until 8) drawCircle(t.s(0.35f), 0.5f * u, Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla + 1, k)))
        drawRect(t.b(0.25f), r.topLeft, Size(r.width, 0.8f * u))
    }

    /** Una cincha (o una tira de cinta) de [a] a [b] y de [ancho], con los bordes cosidos y los extremos rectos. */
    private fun DrawScope.cincha(a: Offset, b: Offset, ancho: Float, col: Color, t: Tono, costura: Boolean = true) {
        val u = density
        val d = b - a; val l = d.getDistance().coerceAtLeast(1f)
        val n = Offset(-d.y / l, d.x / l) * (ancho / 2)
        val p = Path().apply { moveTo(a.x + n.x, a.y + n.y); lineTo(b.x + n.x, b.y + n.y); lineTo(b.x - n.x, b.y - n.y); lineTo(a.x - n.x, a.y - n.y); close() }
        translate(ancho * 0.25f, ancho * 0.3f) { drawPath(p, t.s(0.22f)) }
        drawPath(p, t.c(col))
        drawLine(t.b(0.45f), a - n * 0.85f, b - n * 0.85f, 0.8f * u)
        drawLine(t.s(0.3f), a + n * 0.85f, b + n * 0.85f, 0.8f * u)
        if (costura) {
            drawLine(t.c(mezcla(col, TINTA, 0.3f)).copy(alpha = 0.35f), a - n * 0.55f, b - n * 0.55f, 0.5f * u)
            drawLine(t.c(mezcla(col, TINTA, 0.3f)).copy(alpha = 0.35f), a + n * 0.55f, b + n * 0.55f, 0.5f * u)
        }
    }

    /** Las cifras de plantilla (siete segmentos con su corte), para los rótulos pintados de la nave. */
    private val SEGMENTOS = mapOf('0' to "abcdef", '1' to "bc", '2' to "abged", '3' to "abgcd", '4' to "fgbc", '5' to "afgcd", '6' to "afgedc",
        '7' to "abc", '8' to "abcdefg", '9' to "abfgcd", '-' to "g")

    private fun DrawScope.plantilla(texto: String, x: Float, y: Float, a: Float, al: Float, col: Color) {
        val g = maxOf(al * 0.14f, 0.9f * density)
        val m = al / 2
        texto.forEachIndexed { i, ch ->
            val x0 = x + i * a * 1.45f
            SEGMENTOS[ch]?.forEach { s ->
                when (s) {
                    'a' -> drawRect(col, Offset(x0 + g * 0.6f, y), Size(a - 1.2f * g, g))
                    'b' -> drawRect(col, Offset(x0 + a - g, y + g * 0.6f), Size(g, m - g * 0.9f))
                    'c' -> drawRect(col, Offset(x0 + a - g, y + m + g * 0.3f), Size(g, m - g * 0.9f))
                    'd' -> drawRect(col, Offset(x0 + g * 0.6f, y + al - g), Size(a - 1.2f * g, g))
                    'e' -> drawRect(col, Offset(x0, y + m + g * 0.3f), Size(g, m - g * 0.9f))
                    'f' -> drawRect(col, Offset(x0, y + g * 0.6f), Size(g, m - g * 0.9f))
                    'g' -> drawRect(col, Offset(x0 + g * 0.6f, y + m - g / 2), Size(a - 1.2f * g, g))
                }
            }
        }
    }

    /** Una onda de pantalla dentro de [r]: un seno con su ruido y un pico, en [col]. */
    private fun ondaPath(r: Rect, semilla: Int, picos: Int = 3): Path = Path().apply {
        val n = 40
        for (j in 0..n) {
            val f = j / n.toFloat()
            val pico = if (j % (n / picos) == n / picos / 2) -0.32f else 0f
            val y = r.center.y + r.height * (0.26f * sin(f * 6.28f * 1.6f + semilla) + 0.08f * (azar(semilla, j) - 0.5f) + pico)
            val x = r.left + r.width * f
            if (j == 0) moveTo(x, y) else lineTo(x, y)
        }
    }

    /** Las esquinas en corchete de los visores (el interior y las fichas). */
    private fun DrawScope.corchetes(r: Rect, l: Float, color: Color, grosor: Float) {
        listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.left, r.bottom) to Offset(1f, -1f), Offset(r.right, r.bottom) to Offset(-1f, -1f)).forEach { (o, s) ->
            drawLine(color, o, Offset(o.x + s.x * l, o.y), grosor)
            drawLine(color, o, Offset(o.x, o.y + s.y * l), grosor)
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        val w = size.width; val alto = size.height
        esNoche = o; anchoW = w
        val b = d.base
        val suelo = b.top + w * 0.078f
        val pisan = b.top + w * 0.112f
        fun p(x: Float, y: Float) = Offset(w * x, w * y)

        // ---- el mamparo, el suelo y el conducto
        mamparo(w, suelo, o)
        rejilla(w, suelo, alto, o)
        conducto(w, o)

        // ---- en la pared, de atrás adelante
        calendario(Rect(w * 0.135f, w * 0.1f, w * 0.265f, w * 0.265f))
        foto(Rect(w * 0.24f, w * 0.215f, w * 0.325f, w * 0.3f))
        calcetines(p(0.53f, 0.072f), w)
        tableta(Rect(w * 0.585f, w * 0.2f, w * 0.735f, w * 0.33f), o)
        telemetria(Rect(w * 0.755f, w * 0.1f, w * 0.965f, w * 0.29f), o)
        lampara(p(LAMPARA.x, LAMPARA.y), w * 0.03f, o)

        // ---- la sombra del bastidor en el mamparo (a la derecha) y en el suelo
        val xR = w * DER; val xL = w * IZQ
        val tp = tn(p(0.98f, 1.2f))
        drawPath(Path().apply { moveTo(xR, w * SUP); lineTo(xR + w * 0.028f, w * SUP + w * 0.03f); lineTo(xR + w * 0.028f, suelo); lineTo(xR, suelo); close() }, tp.s(0.26f))
        drawRect(tn(p(0.5f, suelo / w)).s(if (o) 0.55f else 0.3f), Offset(xL + w * 0.01f, suelo), Size(xR - xL + w * 0.02f, pisan - suelo + w * 0.01f))

        // ---- el bastidor
        bastidor(d, o, suelo, pisan)

        // ---- lo de encima, de atrás adelante
        caja(Rect(w * 0.675f, w * 0.5f, w * 0.95f, w * PIE))
        bolsas(w * 0.57f, w * PIE, w)
        casco(p(0.425f, 0.452f), w * 0.132f)
        ukelele(p(0.088f, 0.515f), w)

        // ---- al pie: el extintor y el contenedor, cortados por los bordes
        contenedor(Rect(w * 0.7f, b.top + w * 0.13f, w * 1.04f, alto + w * 0.02f))
        extintor(Offset(w * 0.045f, alto - w * 0.008f), w * 0.2f, w * 0.072f)

        // ---- la luz de la lámpara y de las pantallas, por encima de lo que tienen cerca
        if (o) {
            mancha(p(LAMPARA.x + 0.06f, LAMPARA.y + 0.12f), w * 0.42f, AMBAR.copy(alpha = 0.12f))
            mancha(p(LAMPARA.x, LAMPARA.y), w * 0.12f, AMBAR.copy(alpha = 0.28f))
            mancha(p(PANTALLA.x, PANTALLA.y + 0.05f), w * 0.22f, CIAN.copy(alpha = 0.12f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * El mamparo: los paneles claros con sus juntas y sus tornillos de cuarto de vuelta, la mancha de luz de las luminarias arriba
     * a la izquierda (de noche, el halo ámbar de la lámpara de turno), un panel de otro lote, cinta de colores, una plantilla y
     * lo rozado de abajo.
     */
    private fun DrawScope.mamparo(w: Float, suelo: Float, o: Boolean) {
        val u = density
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(MAMPARO, LUZ_MUEBLE, 0.35f), MAMPARO, mezcla(MAMPARO, MAMPARO_OSC, 0.45f)), 0f, w), Offset.Zero, Size(w, suelo))
            mancha(Offset(w * 0.1f, w * 0.06f), w * 0.85f, luzMueble(0.42f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.12f)), w * 0.7f, suelo), Offset(0f, w * 0.7f), Size(w, suelo - w * 0.7f))
        } else {
            val lam = Offset(w * LAMPARA.x, w * LAMPARA.y)
            drawRect(Brush.radialGradient(listOf(Tono(true, 0.95f, 0f).c(MAMPARO), Tono(true, 0.6f, 0f).c(MAMPARO), Tono(true, 0.28f, 0f).c(MAMPARO),
                Tono(true, 0.03f, 0f).c(MAMPARO)), lam, w * 0.82f), Offset.Zero, Size(w, suelo))
            mancha(Offset(w * PANTALLA.x, w * PANTALLA.y), w * 0.34f, CIAN.copy(alpha = 0.16f))
        }
        // Los paneles: uno de otro lote (más cálido) y las juntas con su bisel claro debajo.
        val xs = listOf(0f, w * 0.3f, w * 0.64f, w)
        val alturaPanel = w * 0.34f
        var y0 = -w * 0.04f
        var fila = 0
        while (y0 < suelo) {
            val y1 = minOf(y0 + alturaPanel, suelo)
            for (i in 0 until 3) {
                val r = Rect(xs[i], y0, xs[i + 1], y1)
                val t = tn(r.center)
                if (azar(2101 + fila, i) < 0.3f) drawRect(t.c(mezcla(MAMPARO, TELA, 0.6f)).copy(alpha = 0.45f), r.topLeft, r.size)
                // Lo rozado del panel: pinceladas tenues.
                pinceladas(r, if (o) NOCHE else MAMPARO_OSC, 2110 + fila * 3 + i, 4, u, 0.07f)
                // Los tornillos de las esquinas.
                val m = w * 0.022f
                listOf(Offset(r.left + m, y0 + m), Offset(r.right - m, y0 + m), Offset(r.left + m, y1 - m), Offset(r.right - m, y1 - m)).forEachIndexed { j, q ->
                    if (q.y > 0f && q.y < suelo - m * 0.5f) tornillo(q, w * 0.0065f, tn(q), 0.6f + 1.7f * azar(2120 + fila * 3 + i, j))
                }
            }
            // La junta horizontal de abajo.
            if (y1 < suelo) for (i in 0 until 3) {
                val t = tn(Offset((xs[i] + xs[i + 1]) / 2, y1))
                drawRect(t.s(0.42f), Offset(xs[i], y1 - 1f * u), Size(xs[i + 1] - xs[i], 1.6f * u))
                drawRect(t.b(0.4f), Offset(xs[i], y1 + 0.6f * u), Size(xs[i + 1] - xs[i], 0.8f * u))
            }
            y0 = y1; fila++
        }
        // Las juntas verticales, por tramos (la luz cambia de arriba abajo).
        for (x in listOf(w * 0.3f, w * 0.64f)) {
            var y = 0f
            while (y < suelo) {
                val t = tn(Offset(x, y + w * 0.05f))
                val tramo = minOf(w * 0.1f, suelo - y)
                drawRect(t.s(0.42f), Offset(x - 0.8f * u, y), Size(1.6f * u, tramo))
                drawRect(t.b(0.38f), Offset(x + 1f * u, y), Size(0.8f * u, tramo))
                y += tramo
            }
        }
        // La cinta de colores: un trozo cian en la junta y otro naranja, y la plantilla del módulo.
        cincha(Offset(w * 0.3f, w * 0.07f), Offset(w * 0.3f, w * 0.14f), w * 0.02f, CIAN, tn(Offset(w * 0.3f, w * 0.1f)), costura = false)
        cincha(Offset(w * 0.64f, w * 0.13f), Offset(w * 0.64f, w * 0.19f), w * 0.02f, CINCHA, tn(Offset(w * 0.64f, w * 0.16f)), costura = false)
        val tm = tn(Offset(w * 0.34f, w * 0.12f))
        plantilla("07", w * 0.325f, w * 0.1f, w * 0.022f, w * 0.042f, tm.c(mezcla(GRAFITO, MAMPARO, 0.25f)).copy(alpha = 0.8f))
        // Una flecha de plantilla hacia el conducto.
        drawPath(Path().apply { moveTo(w * 0.385f, w * 0.1f); lineTo(w * 0.4f, w * 0.082f); lineTo(w * 0.415f, w * 0.1f); close() }, tm.c(CINCHA).copy(alpha = 0.85f))
        drawRect(tm.c(CINCHA).copy(alpha = 0.85f), Offset(w * 0.395f, w * 0.1f), Size(w * 0.01f, w * 0.03f))
        // El roce y la mugre de abajo.
        pinceladas(Rect(0f, suelo - w * 0.12f, w, suelo), if (o) NOCHE else MAMPARO_OSC, 2140, 14, u, 0.12f)
        drawRect(tn(Offset(w * 0.5f, suelo)).s(0.3f), Offset(0f, suelo - 1.6f * u), Size(w, 1.6f * u))
    }

    /** La rejilla del suelo: las barras en perspectiva, el canal de luces guía y la sombra del bastidor. */
    private fun DrawScope.rejilla(w: Float, suelo: Float, alto: Float, o: Boolean) {
        val u = density
        val h = alto - suelo
        val t0 = tn(Offset(w * 0.5f, suelo + h * 0.5f))
        drawRect(t0.c(mezcla(ALU_OSC, GRAFITO, 0.35f)), Offset(0f, suelo), Size(w, h))
        // Las barras transversales, más separadas cuanto más cerca.
        for (k in 1 until 9) {
            val f = Math.pow(k / 9.0, 1.35).toFloat()
            val y = suelo + h * f
            drawRect(t0.c(ALU), Offset(0f, y), Size(w, (0.8f + 1.6f * f) * u))
            drawRect(t0.s(0.4f), Offset(0f, y + (0.8f + 1.6f * f) * u), Size(w, (0.6f + 1f * f) * u))
        }
        // Las barras largas, que se abren hacia quien mira.
        for (k in -6..18) {
            val x0 = w * (0.5f + (k - 6) * 0.062f)
            val x1 = w * 0.5f + (x0 - w * 0.5f) * 1.25f
            drawLine(t0.s(0.45f), Offset(x0, suelo), Offset(x1, alto), 1.2f * u)
            drawLine(t0.b(0.25f), Offset(x0 - 1.2f * u, suelo), Offset(x1 - 1.4f * u, alto), 0.6f * u)
        }
        pinceladas(Rect(0f, suelo, w, alto), if (o) NOCHE else GRAFITO, 2150, 12, u, 0.1f)
        // El canal de las luces guía.
        val yc = suelo + h * 0.5f
        drawRect(t0.c(GRAFITO), Offset(0f, yc - 2.8f * u), Size(w, 5.6f * u))
        drawRect(t0.b(0.3f), Offset(0f, yc + 2.8f * u), Size(w, 0.8f * u))
        var x = w * 0.03f
        while (x < w) {
            val c = Offset(x, yc)
            if (o) {
                drawOval(AMBAR.copy(alpha = 0.12f), Offset(x - 9f * u, yc - 4f * u), Size(18f * u, 12f * u))
                aureola(c, 1.8f * u, AMBAR, true)
            } else aureola(c, 1.6f * u, mezcla(AMBAR, PAPEL, 0.5f), false)
            x += w * 0.075f
        }
    }

    /** El conducto de aire que cruza arriba: el tubo con sus anillos, sus abrazaderas, una rejilla y su sombra en el mamparo. */
    private fun DrawScope.conducto(w: Float, o: Boolean) {
        val u = density
        val y0 = w * 0.012f; val y1 = w * 0.072f
        drawRect(tn(Offset(w * 0.3f, y1)).s(0.22f), Offset(0f, y1), Size(w, w * 0.014f))
        drawRect(tn(Offset(w * 0.3f, y1)).s(0.1f), Offset(0f, y1 + w * 0.014f), Size(w, w * 0.012f))
        // El tubo, por tramos (la luz cambia de un lado a otro).
        var x = 0f
        while (x < w) {
            val t = tn(Offset(x + w * 0.05f, (y0 + y1) / 2))
            val tramo = minOf(w * 0.1f, w - x)
            drawRect(t.c(ALU_LUZ), Offset(x, y0), Size(tramo, y1 - y0))
            drawRect(t.b(0.5f), Offset(x, y0 + (y1 - y0) * 0.12f), Size(tramo, (y1 - y0) * 0.16f))
            drawRect(t.s(0.12f), Offset(x, y0 + (y1 - y0) * 0.55f), Size(tramo, (y1 - y0) * 0.2f))
            drawRect(t.s(0.26f), Offset(x, y0 + (y1 - y0) * 0.75f), Size(tramo, (y1 - y0) * 0.25f))
            x += tramo
        }
        // Los anillos del tubo.
        var xa = w * 0.02f
        while (xa < w) {
            val t = tn(Offset(xa, y0))
            drawLine(t.s(0.25f), Offset(xa, y0), Offset(xa, y1), 1.2f * u)
            drawLine(t.b(0.35f), Offset(xa + 1.2f * u, y0), Offset(xa + 1.2f * u, y1), 0.7f * u)
            xa += w * 0.045f
        }
        // Las abrazaderas.
        listOf(0.1f, 0.47f, 0.83f).forEach { f ->
            val t = tn(Offset(w * f, y1))
            drawRect(t.c(ALU_OSC), Offset(w * f - 3f * u, y0 - 1.5f * u), Size(6f * u, y1 - y0 + 3f * u))
            drawRect(t.b(0.4f), Offset(w * f - 3f * u, y0 - 1.5f * u), Size(1f * u, y1 - y0 + 3f * u))
            drawCircle(t.c(GRAFITO), 1.4f * u, Offset(w * f, y1 + 1f * u))
        }
        // Una rejilla de impulsión.
        val tr = tn(Offset(w * 0.62f, y1))
        drawRect(tr.c(ALU_OSC), Offset(w * 0.585f, y0 + (y1 - y0) * 0.3f), Size(w * 0.07f, (y1 - y0) * 0.6f))
        for (j in 0 until 6) drawLine(tr.c(GRAFITO), Offset(w * 0.59f + j * w * 0.011f, y0 + (y1 - y0) * 0.38f), Offset(w * 0.59f + j * w * 0.011f, y1 - (y1 - y0) * 0.18f), 1.6f * u)
    }

    // ---------------------------------------------------------------- en la pared

    /** El calendario de papel: la cabecera naranja con el año del viaje, los días tachados a mano y la esquina levantada. */
    private fun DrawScope.calendario(r: Rect) {
        val u = density
        val t = tn(r.center)
        rotate(-2f, r.center) {
            drawRect(t.s(0.22f), Offset(r.left + 2.5f * u, r.top + 3f * u), r.size)
            drawRect(t.c(PAPEL), r.topLeft, r.size)
            drawRect(t.b(0.25f), r.topLeft, Size(r.width * 0.3f, r.height))
            val cab = r.height * 0.22f
            drawRect(t.c(mezcla(CINCHA, PAPEL, 0.15f)), r.topLeft, Size(r.width, cab))
            plantilla("15", r.left + r.width * 0.36f, r.top + cab * 0.2f, cab * 0.32f, cab * 0.6f, t.c(PAPEL))
            // La rejilla de días y las cruces.
            val cw = r.width / 7f; val chh = (r.height - cab - r.height * 0.05f) / 5f
            for (fy in 0 until 5) for (fx in 0 until 7) {
                val c0 = Offset(r.left + cw * fx, r.top + cab + chh * fy + r.height * 0.02f)
                drawRect(t.c(MAMPARO_OSC).copy(alpha = 0.5f), c0, Size(cw, chh), style = Stroke(0.5f * u))
                val i = fy * 7 + fx
                if (i < 30) {
                    val j = 0.15f * (azar(2201, i) - 0.5f)
                    val col = t.c(if (i % 9 == 4) ROJO else TINTA).copy(alpha = 0.75f)
                    drawLine(col, c0 + Offset(cw * (0.2f + j), chh * 0.22f), c0 + Offset(cw * (0.82f - j), chh * 0.8f), 0.9f * u, StrokeCap.Round)
                    drawLine(col, c0 + Offset(cw * (0.78f + j), chh * 0.2f), c0 + Offset(cw * (0.22f - j), chh * 0.82f), 0.9f * u, StrokeCap.Round)
                }
            }
            // La esquina de abajo a la derecha, levantada.
            val e = r.width * 0.16f
            drawPath(Path().apply { moveTo(r.right - e, r.bottom); lineTo(r.right, r.bottom - e); lineTo(r.right, r.bottom); close() }, t.c(MAMPARO))
            drawPath(Path().apply { moveTo(r.right - e, r.bottom); lineTo(r.right, r.bottom - e); lineTo(r.right - e * 0.85f, r.bottom - e * 0.85f); close() }, t.c(mezcla(PAPEL, MAMPARO_OSC, 0.4f)))
            // La pinza de arriba.
            drawRect(t.c(GRAFITO), Offset(r.center.x - r.width * 0.12f, r.top - 3f * u), Size(r.width * 0.24f, 6f * u))
            drawRect(t.b(0.5f), Offset(r.center.x - r.width * 0.12f, r.top - 3f * u), Size(r.width * 0.24f, 1f * u))
            drawArc(t.c(ALU), 180f, 180f, false, Offset(r.center.x - r.width * 0.07f, r.top - 8f * u), Size(r.width * 0.14f, 10f * u), style = Stroke(1.2f * u))
        }
        // El lápiz colgado de su cordel.
        val a = Offset(r.right - r.width * 0.05f, r.top + 2f * u)
        val pl = Offset(r.right + r.width * 0.08f, r.bottom - r.height * 0.1f)
        drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + r.width * 0.12f, a.y + r.height * 0.3f, pl.x - r.width * 0.02f, pl.y - r.height * 0.4f, pl.x, pl.y) }, t.c(GRAFITO).copy(alpha = 0.7f), style = Stroke(0.7f * u))
        drawLine(t.s(0.25f), pl + Offset(2f * u, 2f * u), pl + Offset(2f * u, r.height * 0.32f), 3.4f * u, StrokeCap.Round)
        drawLine(t.c(CINCHA), pl, pl + Offset(0f, r.height * 0.3f), 3.2f * u, StrokeCap.Butt)
        drawLine(t.b(0.5f), pl - Offset(0.8f * u, 0f), pl + Offset(-0.8f * u, r.height * 0.3f), 0.8f * u)
        drawPath(Path().apply { moveTo(pl.x - 1.6f * u, pl.y + r.height * 0.3f); lineTo(pl.x + 1.6f * u, pl.y + r.height * 0.3f); lineTo(pl.x, pl.y + r.height * 0.36f); close() }, t.c(TELA))
    }

    /** La foto de papel pegada con cinta: dos personas lejanas, sin detalle, en un campo bajo un cielo de la Tierra. */
    private fun DrawScope.foto(r: Rect) {
        val u = density
        val t = tn(r.center)
        rotate(7f, r.center) {
            drawRect(t.s(0.25f), Offset(r.left + 2f * u, r.top + 2.5f * u), r.size)
            drawRect(t.c(PAPEL), r.topLeft, r.size)
            val im = Rect(r.left + r.width * 0.08f, r.top + r.width * 0.08f, r.right - r.width * 0.08f, r.bottom - r.height * 0.2f)
            drawRect(t.c(mezcla(TELA_AZUL, PAPEL, 0.45f)), im.topLeft, im.size)
            drawRect(t.c(mezcla(VERDE, TELA, 0.25f)), Offset(im.left, im.top + im.height * 0.62f), Size(im.width, im.height * 0.38f))
            drawRect(t.c(mezcla(MADERA, TELA, 0.3f)).copy(alpha = 0.6f), Offset(im.left, im.top + im.height * 0.6f), Size(im.width, im.height * 0.05f))
            // Las dos personas: cabeza y cuerpo, sin cara.
            listOf(0.38f to TELA_AZUL, 0.6f to CINCHA).forEachIndexed { i, (fx, col) ->
                val x = im.left + im.width * fx
                val alt = if (i == 0) 0.5f else 0.42f
                drawRoundRect(t.c(mezcla(col, TINTA, 0.2f)), Offset(x - im.width * 0.07f, im.bottom - im.height * alt), Size(im.width * 0.14f, im.height * alt * 0.75f), CornerRadius(im.width * 0.05f))
                drawCircle(t.c(mezcla(MADERA, PAPEL, 0.35f)), im.width * 0.055f, Offset(x, im.bottom - im.height * alt - im.width * 0.03f))
            }
            drawRect(t.b(0.35f), im.topLeft, Size(im.width * 0.25f, im.height))
            // La cinta de arriba, translúcida.
            drawRect(t.c(PAPEL).copy(alpha = 0.6f), Offset(r.center.x - r.width * 0.25f, r.top - r.height * 0.07f), Size(r.width * 0.5f, r.height * 0.16f))
            drawRect(t.s(0.12f), Offset(r.center.x - r.width * 0.25f, r.top + r.height * 0.07f), Size(r.width * 0.5f, 0.8f * u))
        }
    }

    /** Un par de calcetines antideslizantes colgados del conducto con una pinza: gris azulado, con la puntera y los puntos de goma. */
    private fun DrawScope.calcetines(pinza: Offset, w: Float) {
        val u = density
        val t = tn(pinza + Offset(0f, w * 0.08f))
        listOf(Triple(-0.012f, 0.15f, 4f), Triple(0.012f, 0.135f, -3f)).forEachIndexed { i, (dx, largo, giro) ->
            val a = Offset(pinza.x + w * dx, pinza.y + w * 0.012f)
            val l = w * largo; val an = w * 0.024f
            val forma = Path().apply {
                moveTo(a.x - an / 2, a.y)
                lineTo(a.x - an / 2 - 0.5f * u, a.y + l * 0.72f)
                cubicTo(a.x - an / 2, a.y + l * 0.95f, a.x + an * 0.4f, a.y + l, a.x + an * 1.3f, a.y + l * 0.97f)
                cubicTo(a.x + an * 1.75f, a.y + l * 0.95f, a.x + an * 1.7f, a.y + l * 0.8f, a.x + an * 1.2f, a.y + l * 0.78f)
                lineTo(a.x + an / 2, a.y + l * 0.7f)
                lineTo(a.x + an / 2, a.y)
                close()
            }
            rotate(giro, a) {
                translate(an * 0.4f, an * 0.3f) { drawPath(forma, t.s(0.2f)) }
                val col = if (i == 0) mezcla(TELA_AZUL, MAMPARO, 0.35f) else mezcla(ALU_OSC, TELA_AZUL, 0.3f)
                drawPath(forma, t.c(col))
                clipPath(forma) {
                    drawRect(t.s(0.2f), Offset(a.x + an * 0.15f, a.y), Size(an * 2f, l))
                    drawRect(t.c(mezcla(col, PAPEL, 0.45f)), Offset(a.x - an, a.y), Size(an * 3f, l * 0.08f))
                    drawRect(t.c(CINCHA), Offset(a.x - an, a.y + l * 0.86f), Size(an * 3f, l * 0.2f))
                    // Los pliegues del punto.
                    for (j in 1..4) drawLine(t.s(0.18f), Offset(a.x - an, a.y + l * 0.12f * j + l * 0.1f), Offset(a.x + an, a.y + l * 0.12f * j + l * 0.12f), 0.6f * u)
                }
                // Los puntos de goma de la planta.
                for (j in 0 until 4) drawCircle(t.c(mezcla(CINCHA, TINTA, 0.25f)), 0.9f * u, Offset(a.x - an * 0.35f, a.y + l * (0.22f + 0.13f * j)))
                drawLine(t.b(0.45f), Offset(a.x - an * 0.35f, a.y + 2f * u), Offset(a.x - an * 0.4f, a.y + l * 0.7f), 0.8f * u)
            }
        }
        // La pinza de ropa.
        drawRect(t.s(0.3f), Offset(pinza.x - w * 0.018f + 1.5f * u, pinza.y + 1.5f * u), Size(w * 0.036f, w * 0.022f))
        drawRoundRect(t.c(CINCHA), Offset(pinza.x - w * 0.018f, pinza.y), Size(w * 0.036f, w * 0.022f), CornerRadius(1.5f * u))
        drawRect(t.b(0.5f), Offset(pinza.x - w * 0.018f, pinza.y), Size(w * 0.036f, 1f * u))
        drawLine(t.s(0.4f), Offset(pinza.x, pinza.y), Offset(pinza.x, pinza.y + w * 0.022f), 0.8f * u)
    }

    /** La tableta con su funda de goma, sujeta con velcro, con el diagrama de un circuito de refrigeración en la pantalla. */
    private fun DrawScope.tableta(r: Rect, o: Boolean) {
        val u = density
        val t = tn(r.center)
        rotate(-3f, r.center) {
            // Las tiras de velcro que asoman en las esquinas.
            listOf(Offset(r.left - 3f * u, r.top + r.height * 0.1f), Offset(r.right - r.width * 0.12f, r.bottom - 3f * u)).forEachIndexed { i, q ->
                velcro(Rect(q.x, q.y, q.x + r.width * 0.14f, q.y + 7f * u), t, 2300 + i)
            }
            drawRoundRect(t.s(0.3f), Offset(r.left + 3f * u, r.top + 4f * u), r.size, CornerRadius(5f * u))
            drawRoundRect(t.c(GRAFITO), r.topLeft, r.size, CornerRadius(5f * u))
            drawRoundRect(t.b(0.4f), r.topLeft, Size(r.width, 2f * u), CornerRadius(1f * u))
            // Las esquinas reforzadas de la funda.
            listOf(r.topLeft, Offset(r.right - 8f * u, r.top), Offset(r.left, r.bottom - 8f * u), Offset(r.right - 8f * u, r.bottom - 8f * u)).forEach {
                drawRoundRect(t.c(mezcla(GRAFITO, CINCHA, 0.55f)), it, Size(8f * u, 8f * u), CornerRadius(3f * u))
            }
            val pa = Rect(r.left + r.width * 0.07f, r.top + r.height * 0.09f, r.right - r.width * 0.07f, r.bottom - r.height * 0.09f)
            val fondo = if (o) Color(0xFF0E2A3A) else mezcla(PAPEL, CIAN, 0.12f)
            val linea = if (o) CIAN else TELA_AZUL
            drawRect(fondo, pa.topLeft, pa.size)
            // El diagrama: dos cajas, una bomba redonda, las tuberías y una flecha.
            val s = 1.1f * u
            val a1 = Rect(pa.left + pa.width * 0.08f, pa.top + pa.height * 0.18f, pa.left + pa.width * 0.34f, pa.top + pa.height * 0.5f)
            val a2 = Rect(pa.left + pa.width * 0.62f, pa.top + pa.height * 0.5f, pa.left + pa.width * 0.92f, pa.top + pa.height * 0.84f)
            drawRect(linea, a1.topLeft, a1.size, style = Stroke(s))
            drawRect(linea, a2.topLeft, a2.size, style = Stroke(s))
            val bomba = Offset(pa.left + pa.width * 0.5f, pa.top + pa.height * 0.3f)
            drawCircle(linea, pa.height * 0.1f, bomba, style = Stroke(s))
            drawLine(linea, Offset(a1.right, bomba.y), Offset(bomba.x - pa.height * 0.1f, bomba.y), s)
            drawLine(linea, Offset(bomba.x + pa.height * 0.1f, bomba.y), Offset(a2.center.x, bomba.y), s)
            drawLine(linea, Offset(a2.center.x, bomba.y), Offset(a2.center.x, a2.top), s)
            drawLine(linea, Offset(a2.left, a2.bottom - a2.height * 0.3f), Offset(a1.center.x, a2.bottom - a2.height * 0.3f), s)
            drawLine(linea, Offset(a1.center.x, a2.bottom - a2.height * 0.3f), Offset(a1.center.x, a1.bottom), s)
            drawPath(Path().apply { moveTo(a2.center.x - 3f * u, a2.top - 6f * u); lineTo(a2.center.x + 3f * u, a2.top - 6f * u); lineTo(a2.center.x, a2.top - 1f * u); close() }, if (o) AMBAR else CINCHA)
            for (j in 0 until 3) drawRect(linea.copy(alpha = 0.5f), Offset(a1.left + 2f * u, a1.top + a1.height * (0.25f + 0.22f * j)), Size(a1.width * (0.4f + 0.3f * azar(2310, j)), 1f * u))
            // El reflejo del vidrio (de día, la luminaria).
            if (!o) drawPath(Path().apply { moveTo(pa.left, pa.top); lineTo(pa.left + pa.width * 0.3f, pa.top); lineTo(pa.left, pa.top + pa.height * 0.5f); close() }, luzMueble(0.22f))
        }
    }

    /** La pantalla de telemetría en su brazo: el marco con sus tornillos, la retícula, la onda cian, una ámbar y las barras. */
    private fun DrawScope.telemetria(r: Rect, o: Boolean) {
        val u = density
        val t = tn(r.center)
        // El brazo a la pared y el cable que baja en curva.
        drawRect(t.c(ALU_OSC), Offset(r.center.x - r.width * 0.08f, r.bottom - 2f * u), Size(r.width * 0.16f, r.height * 0.1f))
        drawPath(Path().apply { moveTo(r.right - r.width * 0.12f, r.bottom); cubicTo(r.right - r.width * 0.1f, r.bottom + r.height * 0.5f, r.right + r.width * 0.12f, r.bottom + r.height * 0.2f, r.right + r.width * 0.2f, r.bottom + r.height * 0.6f) },
            t.c(GRAFITO), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawRoundRect(t.s(0.32f), Offset(r.left + 4f * u, r.top + 5f * u), r.size, CornerRadius(4f * u))
        drawRoundRect(t.c(GRAFITO), r.topLeft, r.size, CornerRadius(4f * u))
        drawRoundRect(t.c(ALU), r.topLeft, r.size, CornerRadius(4f * u), style = Stroke(2.4f * u))
        drawRect(t.b(0.55f), Offset(r.left + 3f * u, r.top + 1f * u), Size(r.width - 6f * u, 1f * u))
        listOf(Offset(r.left + 5f * u, r.top + 5f * u), Offset(r.right - 5f * u, r.top + 5f * u), Offset(r.left + 5f * u, r.bottom - 5f * u), Offset(r.right - 5f * u, r.bottom - 5f * u)).forEach {
            drawCircle(t.c(ALU_LUZ), 1.4f * u, it)
        }
        val pa = Rect(r.left + 9f * u, r.top + 9f * u, r.right - 9f * u, r.bottom - 9f * u)
        drawRect(Color(0xFF0A1830), pa.topLeft, pa.size)
        // La retícula.
        for (j in 1 until 6) drawLine(CIAN.copy(alpha = 0.14f), Offset(pa.left + pa.width * j / 6f, pa.top), Offset(pa.left + pa.width * j / 6f, pa.bottom), 0.6f * u)
        for (j in 1 until 4) drawLine(CIAN.copy(alpha = 0.14f), Offset(pa.left, pa.top + pa.height * j / 4f), Offset(pa.right, pa.top + pa.height * j / 4f), 0.6f * u)
        val zona = Rect(pa.left, pa.top + pa.height * 0.08f, pa.left + pa.width * 0.72f, pa.top + pa.height * 0.62f)
        drawPath(ondaPath(zona, 5), CIAN.copy(alpha = 0.3f), style = Stroke(3.4f * u))
        drawPath(ondaPath(zona, 5), mezcla(CIAN, Color.White, 0.25f), style = Stroke(1.3f * u, join = StrokeJoin.Round))
        drawPath(ondaPath(Rect(zona.left, zona.top + zona.height * 0.25f, zona.right, zona.bottom + zona.height * 0.2f), 9, 2), AMBAR.copy(alpha = 0.75f), style = Stroke(0.9f * u))
        // Las barras y las cifras de la derecha.
        for (j in 0 until 5) {
            val hb = pa.height * 0.26f * (0.3f + 0.7f * azar(2401, j))
            drawRect(CIAN.copy(alpha = 0.7f), Offset(pa.left + pa.width * (0.06f + 0.11f * j), pa.bottom - 3f * u - hb), Size(pa.width * 0.07f, hb))
        }
        plantilla("82", pa.left + pa.width * 0.78f, pa.top + pa.height * 0.12f, pa.width * 0.07f, pa.height * 0.18f, CIAN)
        plantilla("15", pa.left + pa.width * 0.78f, pa.top + pa.height * 0.45f, pa.width * 0.07f, pa.height * 0.18f, AMBAR)
        // El brillo del vidrio, arriba a la izquierda (de día, la luminaria).
        if (!o) drawPath(Path().apply { moveTo(pa.left, pa.top); lineTo(pa.left + pa.width * 0.35f, pa.top); lineTo(pa.left, pa.top + pa.height * 0.55f); close() }, luzMueble(0.16f))
    }

    /** La lámpara de turno: la caja atornillada al mamparo, el vidrio estriado y su jaula; de noche, encendida en ámbar. */
    private fun DrawScope.lampara(c: Offset, r: Float, o: Boolean) {
        val u = density
        val t = tn(c)
        val caja = Rect(c.x - r * 1.25f, c.y - r * 0.75f, c.x + r * 1.25f, c.y + r * 0.75f)
        drawRoundRect(t.s(0.3f), Offset(caja.left + r * 0.3f, caja.top + r * 0.4f), caja.size, CornerRadius(r * 0.5f))
        drawRoundRect(t.c(GRAFITO), caja.topLeft, caja.size, CornerRadius(r * 0.5f))
        drawRoundRect(t.b(0.45f), caja.topLeft, Size(caja.width, 1.4f * u), CornerRadius(1f * u))
        listOf(caja.left + r * 0.22f, caja.right - r * 0.22f).forEach { drawCircle(t.c(ALU_LUZ), 1.3f * u, Offset(it, c.y)) }
        val vidrio = Rect(c.x - r * 0.88f, c.y - r * 0.5f, c.x + r * 0.88f, c.y + r * 0.5f)
        if (o) {
            drawRoundRect(mezcla(AMBAR, Color.White, 0.3f), vidrio.topLeft, vidrio.size, CornerRadius(r * 0.35f))
            drawRoundRect(Color.White.copy(alpha = 0.75f), Offset(vidrio.left + r * 0.3f, c.y - r * 0.18f), Size(vidrio.width - r * 0.6f, r * 0.3f), CornerRadius(r * 0.15f))
        } else {
            drawRoundRect(mezcla(PAPEL, AMBAR, 0.22f), vidrio.topLeft, vidrio.size, CornerRadius(r * 0.35f))
            drawRect(sombraMueble(0.18f), Offset(vidrio.left, c.y), Size(vidrio.width, vidrio.height / 2))
            drawRect(luzMueble(0.7f), Offset(vidrio.left + r * 0.25f, vidrio.top + 1.5f * u), Size(vidrio.width * 0.35f, 1.2f * u))
        }
        // Las estrías del vidrio y la jaula.
        for (j in 1 until 6) drawLine((if (o) AMBAR else MAMPARO_OSC).copy(alpha = 0.45f), Offset(vidrio.left + vidrio.width * j / 6f, vidrio.top + 1f * u), Offset(vidrio.left + vidrio.width * j / 6f, vidrio.bottom - 1f * u), 0.6f * u)
        val jaula = if (o) mezcla(GRAFITO, AMBAR, 0.3f) else ALU_OSC
        drawLine(jaula, Offset(vidrio.left, c.y), Offset(vidrio.right, c.y), 1.4f * u)
        listOf(0.3f, 0.7f).forEach { f -> drawLine(jaula, Offset(vidrio.left + vidrio.width * f, vidrio.top - 1f * u), Offset(vidrio.left + vidrio.width * f, vidrio.bottom + 1f * u), 1.4f * u) }
        drawRoundRect(jaula, vidrio.topLeft, vidrio.size, CornerRadius(r * 0.35f), style = Stroke(1.4f * u))
    }

    // ---------------------------------------------------------------- el bastidor

    /**
     * El fondo de una bahía, en [r]: el mamparo que se ve por detrás, los postes traseros con sus agujeros, la sombra del
     * travesaño de arriba en escalones, el lado derecho más hondo y las guías del piso. De día, la fila de arriba un poco más
     * clara; de noche, la lámpara según lo cerca que esté.
     */
    private fun DrawScope.fondoHueco(r: Rect, fila: Int, filas: Int, col: Int, o: Boolean) {
        val u = density
        val w = r.width; val h = r.height
        val dia = if (o) 0f else 0.08f - 0.17f * (fila / maxOf(1f, filas - 1f)) + (if (col == 0) 0.02f else 0f)
        val t = tono(o, r.center / anchoW, dia)
        drawRect(t.c(mezcla(MAMPARO, ALU_OSC, 0.42f)), r.topLeft, r.size)
        drawRect(t.b(0.12f), r.topLeft, Size(w * 0.3f, h))
        drawRect(t.s(0.12f), Offset(r.right - w * 0.3f, r.top), Size(w * 0.3f, h))
        // Los postes traseros, con sus agujeros.
        listOf(r.left + w * 0.015f, r.right - w * 0.06f).forEachIndexed { i, x ->
            drawRect(t.c(if (i == 0) ALU else mezcla(ALU, ALU_OSC, 0.5f)), Offset(x, r.top), Size(w * 0.045f, h))
            var y = r.top + h * 0.08f
            while (y < r.bottom - h * 0.1f) { drawRect(t.s(0.45f), Offset(x + w * 0.017f, y), Size(w * 0.012f, w * 0.012f)); y += h * 0.1f }
        }
        drawRect(t.s(0.14f), Offset(r.right - w * 0.08f, r.top), Size(w * 0.08f, h))
        // La sombra del travesaño de arriba, en escalones.
        listOf(0.34f to 0.1f, 0.18f to 0.12f, 0.07f to 0.2f).forEach { (alto, a) -> drawRect(t.s(a), r.topLeft, Size(w, h * alto)) }
        // Las guías del piso, donde se apoya lo que hay en la bahía.
        val g = r.top + h * 0.94f
        drawRect(t.c(ALU_OSC), Offset(r.left, g), Size(w, r.bottom - g))
        drawRect(t.b(0.5f), Offset(r.left, g), Size(w, 1f * u))
    }

    /** El bastidor: los postes y el montante con sus agujeros, el larguero de arriba, los travesaños, el zócalo y las patas. */
    private fun DrawScope.bastidor(d: Disposicion, o: Boolean, suelo: Float, pisan: Float) {
        val u = density
        val w = size.width
        val b = d.base
        val xL = w * IZQ; val xR = w * DER
        val primero = d.huecos.first(); val ultimo = d.filas.last()
        val ix = primero.left; val dx = d.huecos[cols - 1].right
        val zocalo = b.top + w * 0.064f
        // El fondo de cada bahía.
        d.huecos.forEachIndexed { i, r -> fondoHueco(r, i / cols, d.filas.size, i % cols, o) }

        /** Una pieza de aluminio vertical en [x0, x1] de [y0] a [y1], por tramos, con su fila de agujeros. */
        fun vertical(x0: Float, x1: Float, y0: Float, y1: Float, oscura: Boolean, agujeros: Boolean) {
            // El color, por tramos finos (de noche la luz cae deprisa y los tramos largos se ven como escalones).
            var yf = y0
            while (yf < y1) {
                val tramo = minOf(w * 0.012f, y1 - yf)
                val t = tn(Offset((x0 + x1) / 2, yf + tramo / 2))
                drawRect(t.c(if (oscura) mezcla(ALU, ALU_OSC, 0.45f) else ALU), Offset(x0, yf), Size(x1 - x0, tramo + 0.5f))
                drawRect(t.b(0.2f), Offset(x0, yf), Size((x1 - x0) * 0.35f, tramo + 0.5f))
                drawRect(t.s(0.12f), Offset(x1 - (x1 - x0) * 0.3f, yf), Size((x1 - x0) * 0.3f, tramo + 0.5f))
                drawRect(t.b(0.6f), Offset(x0, yf), Size(1.3f * u, tramo + 0.5f))
                drawRect(t.s(0.3f), Offset(x1 - 1.6f * u, yf), Size(1.6f * u, tramo + 0.5f))
                yf += tramo
            }
            var y = y0
            while (y < y1) {
                val tramo = minOf(w * 0.04f, y1 - y)
                val t = tn(Offset((x0 + x1) / 2, y + tramo / 2))
                if (agujeros) {
                    var yh = y + w * 0.008f
                    while (yh < y + tramo - w * 0.006f) {
                        val hx = (x0 + x1) / 2 - w * 0.006f
                        drawRect(t.s(0.6f), Offset(hx, yh), Size(w * 0.012f, w * 0.01f))
                        drawRect(t.b(0.4f), Offset(hx, yh + w * 0.01f), Size(w * 0.012f, 0.7f * u))
                        yh += w * 0.027f
                    }
                }
                y += tramo
            }
        }

        // Los travesaños entre filas: el riel de aluminio con su ranura, sus agujeros y, a veces, una plantilla o cinta.
        d.filas.forEachIndexed { i, f ->
            val sig = d.filas.getOrNull(i + 1)?.top ?: (b.top)
            val r = Rect(ix, f.bottom, dx, sig)
            var x = r.left
            while (x < r.right) {
                val tramo = minOf(w * 0.05f, r.right - x)
                val t = tn(Offset(x + tramo / 2, r.center.y))
                drawRect(t.c(ALU), Offset(x, r.top), Size(tramo, r.height))
                drawRect(t.b(0.65f), Offset(x, r.top), Size(tramo, 1.3f * u))
                drawRect(t.s(0.25f), Offset(x, r.top + r.height * 0.45f), Size(tramo, 1.4f * u))
                drawRect(t.b(0.3f), Offset(x, r.top + r.height * 0.45f + 1.4f * u), Size(tramo, 0.8f * u))
                drawRect(t.s(0.3f), Offset(x, r.bottom - 1.8f * u), Size(tramo, 1.8f * u))
                x += tramo
            }
            val t = tn(r.center)
            var xh = r.left + w * 0.04f
            while (xh < r.right - w * 0.03f) { drawCircle(t.s(0.5f), 1.3f * u, Offset(xh, r.top + r.height * 0.72f)); xh += w * 0.05f }
            if (i < d.filas.size - 1) {
                // La plantilla de la fila, y un trozo de cinta de color en algunas.
                val ts = tn(Offset(r.left + w * 0.05f, r.center.y))
                plantilla("0${i + 1}", r.left + w * 0.02f, r.top + r.height * 0.1f, r.height * 0.2f, r.height * 0.32f, ts.c(mezcla(GRAFITO, ALU, 0.2f)))
                if (i % 2 == 0) cincha(Offset(r.right - w * 0.2f, r.top + r.height * 0.25f), Offset(r.right - w * 0.11f, r.top + r.height * 0.24f), r.height * 0.32f, if (i == 0) CINCHA else CIAN, tn(Offset(r.right - w * 0.15f, r.center.y)), costura = false)
            }
        }

        // El montante del centro, los postes y el larguero de arriba.
        vertical(d.huecos[0].right, d.huecos[1].left, primero.top, ultimo.bottom, true, true)
        vertical(xL, ix, w * TOPE, zocalo, false, true)
        vertical(dx, xR, w * TOPE, zocalo, true, true)
        val larguero = Rect(ix, w * TOPE, dx, primero.top)
        var x = larguero.left
        while (x < larguero.right) {
            val tramo = minOf(w * 0.012f, larguero.right - x)
            val t = tn(Offset(x + tramo / 2, larguero.center.y))
            drawRect(t.c(mezcla(ALU, ALU_LUZ, 0.25f)), Offset(x, larguero.top), Size(tramo + 0.5f, larguero.height))
            drawRect(t.s(0.2f), Offset(x, larguero.bottom - larguero.height * 0.3f), Size(tramo + 0.5f, larguero.height * 0.3f))
            drawRect(t.s(0.28f), Offset(x, larguero.top + larguero.height * 0.62f), Size(tramo + 0.5f, 1.3f * u))
            x += tramo
        }
        // En el larguero: la plantilla del rack, la cinta naranja y tres pilotos.
        val tl = tn(Offset(w * 0.2f, larguero.center.y))
        plantilla("07-3", larguero.left + w * 0.03f, larguero.top + larguero.height * 0.16f, larguero.height * 0.2f, larguero.height * 0.36f, tl.c(mezcla(GRAFITO, ALU, 0.15f)))
        cincha(Offset(w * 0.4f, larguero.top + larguero.height * 0.32f), Offset(w * 0.55f, larguero.top + larguero.height * 0.3f), larguero.height * 0.32f, CINCHA, tn(Offset(w * 0.47f, larguero.center.y)), costura = false)
        for (k in 0 until 3) {
            val c = Offset(w * (0.77f + 0.035f * k), larguero.top + larguero.height * 0.33f)
            drawCircle(tn(c).c(GRAFITO), 3f * u, c)
            if (k < 2) aureola(c, 1.7f * u, if (k == 0) LED else AMBAR, o) else drawCircle(tn(c).c(ALU_OSC), 1.7f * u, c)
        }
        // La tapa del bastidor, vista un poco desde arriba, con su canto de luz.
        val tapa = Rect(xL - w * 0.006f, w * SUP, xR + w * 0.006f, w * TOPE)
        x = tapa.left
        while (x < tapa.right) {
            val tramo = minOf(w * 0.012f, tapa.right - x)
            val t = tn(Offset(x + tramo / 2, tapa.center.y))
            drawRect(t.c(ALU_LUZ), Offset(x, tapa.top), Size(tramo + 0.5f, tapa.height))
            drawRect(t.s(0.12f), Offset(x, tapa.top), Size(tramo + 0.5f, tapa.height * 0.35f))
            drawRect(t.b(0.85f), Offset(x, tapa.bottom - 1.6f * u), Size(tramo + 0.5f, 1.4f * u))
            x += tramo
        }
        drawRect(tn(Offset(w * 0.5f, w * TOPE)).s(0.4f), Offset(xL, w * TOPE), Size(xR - xL, 1.4f * u))
        // Las marcas de uso de la tapa: un rayón y un cerco.
        drawLine(tn(Offset(w * 0.6f, w * 0.59f)).s(0.2f), Offset(w * 0.6f, w * 0.585f), Offset(w * 0.66f, w * 0.59f), 0.8f * u)

        // El zócalo de chapa con sus rejillas y la placa.
        val zr = Rect(xL, ultimo.bottom, xR, zocalo)
        val tz = tn(Offset(w * 0.5f, zr.center.y))
        drawRect(tz.c(mezcla(ALU, ALU_OSC, 0.55f)), zr.topLeft, zr.size)
        drawRect(tz.b(0.4f), zr.topLeft, Size(zr.width, 1.2f * u))
        drawRect(tz.s(0.2f), Offset(zr.right - zr.width * 0.08f, zr.top), Size(zr.width * 0.08f, zr.height))
        listOf(0.1f, 0.74f).forEach { fx ->
            for (j in 0 until 6) drawRect(tz.s(0.5f), Offset(w * fx, zr.top + zr.height * (0.25f + 0.1f * j)), Size(w * 0.14f, zr.height * 0.045f))
        }
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(tz.s(0.3f), Offset(r.left + 2f * u, r.top + 2.5f * u), r.size)
            drawRoundRect(tz.c(ALU_OSC), r.topLeft, r.size, CornerRadius(2f * u))
            val dentro = Rect(r.left + 3f * u, r.top + 3f * u, r.right - 3f * u, r.bottom - 3f * u)
            drawRect(etiqueta(o, tz.k), dentro.topLeft, dentro.size)
            drawRect(tz.c(CINCHA), dentro.topLeft, Size(dentro.width, 1.6f * u))
            listOf(r.left + 1.6f * u, r.right - 1.6f * u).forEach { xx -> drawCircle(tz.c(ALU_LUZ), 1f * u, Offset(xx, r.center.y)) }
        }
        // Las patas niveladoras: la rosca, la tuerca y el pie atornillado a la rejilla.
        listOf(xL + w * 0.03f, xR - w * 0.03f).forEach { px ->
            val t = tn(Offset(px, pisan))
            drawOval(t.s(0.45f), Offset(px - w * 0.03f, pisan - 2f * u), Size(w * 0.07f, 5f * u))
            drawRect(t.c(ALU_OSC), Offset(px - w * 0.006f, zocalo), Size(w * 0.012f, pisan - zocalo - w * 0.008f))
            for (j in 0 until 4) drawLine(t.s(0.4f), Offset(px - w * 0.006f, zocalo + (pisan - zocalo) * (0.15f + 0.15f * j)), Offset(px + w * 0.006f, zocalo + (pisan - zocalo) * (0.18f + 0.15f * j)), 0.7f * u)
            drawRect(t.c(ALU), Offset(px - w * 0.013f, zocalo + (pisan - zocalo) * 0.25f), Size(w * 0.026f, w * 0.008f))
            drawRect(t.c(GRAFITO), Offset(px - w * 0.024f, pisan - w * 0.01f), Size(w * 0.048f, w * 0.01f))
            drawRect(t.b(0.35f), Offset(px - w * 0.024f, pisan - w * 0.01f), Size(w * 0.048f, 0.8f * u))
        }
        // Una cincha naranja colgada del poste derecho, con su hebilla de leva.
        val c0 = Offset(xR - w * 0.03f, d.filas[0].bottom + d.separacion * 0.5f)
        val c1 = Offset(xR - w * 0.022f, d.filas[0].bottom + d.separacion * 0.5f + w * 0.24f)
        cincha(c0, c1, w * 0.022f, CINCHA, tn(c0 + Offset(0f, w * 0.1f)))
        val th = tn(c1)
        drawRoundRect(th.c(ALU_OSC), Offset(c1.x - w * 0.016f, c1.y - w * 0.01f), Size(w * 0.032f, w * 0.03f), CornerRadius(2f * u))
        drawRect(th.b(0.5f), Offset(c1.x - w * 0.016f, c1.y - w * 0.01f), Size(w * 0.032f, 1f * u))
        drawRect(th.c(GRAFITO), Offset(c1.x - w * 0.01f, c1.y), Size(w * 0.02f, w * 0.008f))
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * El casco del traje, el punto focal: la calota blanca con su lado de luz y su lado de sombra, la cinta naranja, los faros de
     * los costados, el visor dorado con el reflejo de la sala (de noche, la lámpara ámbar y la pantalla cian), el anillo del
     * cuello y el soporte con su cincha.
     */
    private fun DrawScope.casco(c: Offset, R: Float) {
        val u = density
        val o = esNoche
        val t = tn(c)
        val pie = anchoW * PIE
        // Su sombra en el mamparo y en la tapa.
        drawOval(t.s(0.26f), Offset(c.x - R * 0.62f, c.y - R * 0.84f), Size(R * 2.05f, R * 1.95f))
        drawOval(t.s(0.38f), Offset(c.x - R * 0.7f, pie - 3f * u), Size(R * 1.75f, 6f * u))
        // El soporte: la placa, la columna y la cuna.
        drawRect(t.c(ALU_OSC), Offset(c.x - R * 0.5f, pie - R * 0.06f), Size(R, R * 0.06f))
        drawRect(t.b(0.5f), Offset(c.x - R * 0.5f, pie - R * 0.06f), Size(R, 1f * u))
        drawRect(t.c(ALU), Offset(c.x - R * 0.08f, c.y + R * 0.85f), Size(R * 0.16f, pie - c.y - R * 0.9f))
        // La calota.
        val calota = Rect(c.x - R, c.y - R * 1.02f, c.x + R, c.y + R * 0.9f)
        val forma = Path().apply { addOval(calota) }
        drawPath(forma, t.c(mezcla(CASCO, ALU_OSC, 0.45f)))
        clipPath(forma) {
            drawOval(t.c(CASCO), Offset(calota.left - R * 0.2f, calota.top - R * 0.18f), Size(R * 1.95f, R * 1.85f))
            drawOval(t.b(0.45f), Offset(calota.left + R * 0.18f, calota.top + R * 0.12f), Size(R * 0.8f, R * 0.55f))
            // La cinta naranja de arriba, siguiendo la curva.
            drawArc(t.c(CINCHA), 200f, 70f, false, Offset(calota.left + R * 0.12f, calota.top + R * 0.1f), Size(R * 1.76f, R * 1.7f), style = Stroke(R * 0.1f))
            // Los rayones y una mancha de uso.
            drawLine(t.s(0.25f), Offset(c.x + R * 0.45f, c.y - R * 0.8f), Offset(c.x + R * 0.62f, c.y - R * 0.68f), 0.8f * u)
            drawLine(t.s(0.2f), Offset(c.x - R * 0.7f, c.y + R * 0.4f), Offset(c.x - R * 0.6f, c.y + R * 0.52f), 0.8f * u)
            // El reflejo ámbar de la lámpara, de noche, en el costado izquierdo.
            if (o) drawOval(AMBAR.copy(alpha = 0.35f), Offset(calota.left - R * 0.1f, c.y - R * 0.5f), Size(R * 0.5f, R * 1.1f))
        }
        // La visera de arriba (el párpado), entre la calota y el visor.
        val visor = Rect(c.x - R * 0.7f, c.y - R * 0.6f, c.x + R * 0.8f, c.y + R * 0.52f)
        val marco = Rect(visor.left - R * 0.09f, visor.top - R * 0.14f, visor.right + R * 0.07f, visor.bottom + R * 0.08f)
        drawOval(t.c(mezcla(CASCO, ALU_OSC, 0.25f)), marco.topLeft, marco.size)
        drawArc(t.b(0.55f), 190f, 80f, false, marco.topLeft, marco.size, style = Stroke(1.4f * u))
        // El visor dorado.
        val vforma = Path().apply { addOval(visor) }
        drawPath(vforma, t.c(ORO))
        clipPath(vforma) {
            // Arriba refleja el techo claro; abajo, el suelo oscuro; en medio, la línea del horizonte de la sala.
            drawRect(t.c(mezcla(ORO, PAPEL, 0.45f)), visor.topLeft, Size(visor.width, visor.height * 0.32f))
            drawRect(t.c(mezcla(ORO, MADERA, 0.65f)), Offset(visor.left, visor.top + visor.height * 0.55f), Size(visor.width, visor.height * 0.45f))
            drawRect(t.c(mezcla(ORO, GRAFITO, 0.55f)), Offset(visor.left, visor.top + visor.height * 0.78f), Size(visor.width, visor.height * 0.22f))
            drawRect(t.c(mezcla(ORO, PAPEL, 0.7f)), Offset(visor.left, visor.top + visor.height * 0.53f), Size(visor.width, 1.2f * u))
            // El bastidor reflejado, curvo: unas columnas oscuras.
            for (j in 0 until 5) drawRect(t.c(mezcla(ORO, GRAFITO, 0.4f)).copy(alpha = 0.6f), Offset(visor.left + visor.width * (0.5f + 0.09f * j), visor.top + visor.height * (0.35f + 0.03f * j)), Size(visor.width * 0.035f, visor.height * 0.2f))
            // La sombra del lado derecho del visor.
            drawOval(t.s(0.3f), Offset(visor.right - visor.width * 0.28f, visor.top - visor.height * 0.1f), Size(visor.width * 0.5f, visor.height * 1.2f))
            // El brillo de la luminaria (de día) o de la lámpara (de noche), arriba a la izquierda.
            if (o) {
                drawCircle(AMBAR.copy(alpha = 0.35f), R * 0.22f, Offset(visor.left + visor.width * 0.2f, visor.top + visor.height * 0.42f))
                drawCircle(mezcla(AMBAR, Color.White, 0.5f), R * 0.07f, Offset(visor.left + visor.width * 0.2f, visor.top + visor.height * 0.42f))
                drawRoundRect(CIAN.copy(alpha = 0.7f), Offset(visor.right - visor.width * 0.22f, visor.top + visor.height * 0.22f), Size(R * 0.14f, R * 0.08f), CornerRadius(1f * u))
            }
            drawArc(if (o) AMBAR.copy(alpha = 0.6f) else luzMueble(0.85f), 200f, 55f, false,
                Offset(visor.left + visor.width * 0.08f, visor.top + visor.height * 0.08f), Size(visor.width * 0.8f, visor.height * 0.8f), style = Stroke(R * 0.045f, cap = StrokeCap.Round))
            if (!o) drawCircle(luzMueble(0.9f), R * 0.035f, Offset(visor.left + visor.width * 0.27f, visor.top + visor.height * 0.16f))
        }
        drawOval(t.c(ALU_OSC), visor.topLeft, visor.size, style = Stroke(1.2f * u))
        // Los faros de los costados: el de la izquierda, a la luz; el de la derecha, en sombra.
        listOf(-1f, 1f).forEach { s ->
            val fx = c.x + s * R * 0.94f
            val fr = Rect(fx - R * 0.13f, c.y - R * 0.28f, fx + R * 0.13f, c.y + R * 0.12f)
            drawRoundRect(t.c(if (s < 0) ALU else mezcla(ALU, ALU_OSC, 0.6f)), fr.topLeft, fr.size, CornerRadius(R * 0.05f))
            drawRoundRect(t.c(GRAFITO), Offset(fr.left + R * 0.04f, fr.top + R * 0.05f), Size(fr.width - R * 0.08f, fr.height * 0.42f), CornerRadius(R * 0.03f))
            drawCircle(t.c(mezcla(PAPEL, CIAN, 0.3f)), R * 0.04f, Offset(fr.center.x, fr.top + R * 0.13f))
            if (s < 0) drawRect(t.b(0.6f), fr.topLeft, Size(1.2f * u, fr.height))
        }
        // El anillo del cuello, con su franja azul y su cierre.
        val anillo = Rect(c.x - R * 0.72f, c.y + R * 0.74f, c.x + R * 0.72f, c.y + R * 1.02f)
        drawRect(t.c(ALU), anillo.topLeft, anillo.size)
        drawRect(t.c(TELA_AZUL), Offset(anillo.left, anillo.top + anillo.height * 0.35f), Size(anillo.width, anillo.height * 0.3f))
        drawRect(t.b(0.6f), anillo.topLeft, Size(anillo.width, 1.2f * u))
        drawRect(t.s(0.3f), Offset(anillo.right - anillo.width * 0.25f, anillo.top), Size(anillo.width * 0.25f, anillo.height))
        drawRect(t.b(0.3f), anillo.topLeft, Size(anillo.width * 0.2f, anillo.height))
        drawRoundRect(t.c(ALU_OSC), Offset(c.x - R * 0.08f, anillo.top - R * 0.03f), Size(R * 0.16f, anillo.height + R * 0.06f), CornerRadius(1.5f * u))
        // Las dos gomas que lo sujetan al soporte, con sus ganchos.
        listOf(-1f, 1f).forEach { s ->
            val a = Offset(c.x + s * R * 0.48f, pie - R * 0.05f)
            val bb = Offset(c.x + s * R * 0.66f, anillo.center.y)
            drawLine(t.s(0.3f), a + Offset(2f * u, 1.5f * u), bb + Offset(2f * u, 1.5f * u), 2.6f * u, StrokeCap.Round)
            drawLine(t.c(CINCHA), a, bb, 2.6f * u, StrokeCap.Round)
            drawLine(t.b(0.45f), a - Offset(0.8f * u, 0f), bb - Offset(0.8f * u, 0f), 0.7f * u)
            drawArc(t.c(ALU_OSC), 180f, 200f, false, Offset(bb.x - 2.5f * u, bb.y - 3.5f * u), Size(5f * u, 5f * u), style = Stroke(1.3f * u))
        }
    }

    /** El ukelele, sujeto al mamparo con una cincha: la caja en ocho con su boca, el puente, el mástil, los trastes y las clavijas. */
    private fun DrawScope.ukelele(c: Offset, w: Float) {
        val u = density
        val t = tn(c)
        val bw = w * 0.13f; val bh = w * 0.19f
        val inf = Offset(c.x, c.y + bh * 0.2f); val sup = Offset(c.x, c.y - bh * 0.22f)
        val bajo = Path().apply { addOval(Rect(inf.x - bw / 2, inf.y - bw * 0.48f, inf.x + bw / 2, inf.y + bw * 0.48f)) }
        val alto = Path().apply { addOval(Rect(sup.x - bw * 0.4f, sup.y - bw * 0.38f, sup.x + bw * 0.4f, sup.y + bw * 0.38f)) }
        val cintura = Path().apply { addRect(Rect(c.x - bw * 0.3f, sup.y, c.x + bw * 0.3f, inf.y)) }
        val dos = Path().apply { op(bajo, alto, PathOperation.Union) }
        val caja = Path().apply { op(dos, cintura, PathOperation.Union) }
        val cabeza = c.y - bh * 0.22f - w * 0.2f
        rotate(-7f, c) {
            // La sombra en el mamparo.
            translate(bw * 0.18f, bw * 0.14f) {
                drawPath(caja, t.s(0.22f))
                drawRect(t.s(0.22f), Offset(c.x - bw * 0.1f, cabeza), Size(bw * 0.2f, sup.y - cabeza))
            }
            // El mástil, el diapasón y los trastes.
            drawRect(t.c(mezcla(MADERA, GRAFITO, 0.2f)), Offset(c.x - bw * 0.1f, cabeza + w * 0.04f), Size(bw * 0.2f, sup.y - cabeza - w * 0.04f))
            drawRect(t.c(mezcla(MADERA, GRAFITO, 0.6f)), Offset(c.x - bw * 0.08f, cabeza + w * 0.04f), Size(bw * 0.16f, sup.y - cabeza - w * 0.02f))
            for (j in 0 until 9) {
                val y = cabeza + w * 0.045f + (sup.y - cabeza) * 0.11f * j * (1f - 0.04f * j)
                drawLine(t.c(ALU_LUZ), Offset(c.x - bw * 0.08f, y), Offset(c.x + bw * 0.08f, y), 0.7f * u)
            }
            // La pala con sus cuatro clavijas.
            val pala = Path().apply {
                moveTo(c.x - bw * 0.11f, cabeza + w * 0.045f); lineTo(c.x - bw * 0.16f, cabeza); lineTo(c.x + bw * 0.16f, cabeza); lineTo(c.x + bw * 0.11f, cabeza + w * 0.045f); close()
            }
            drawPath(pala, t.c(mezcla(MADERA, GRAFITO, 0.35f)))
            for (j in 0 until 2) listOf(-1f, 1f).forEach { s ->
                val q = Offset(c.x + s * bw * 0.2f, cabeza + w * (0.01f + 0.02f * j))
                drawLine(t.c(ALU), Offset(c.x + s * bw * 0.12f, q.y), q, 1.2f * u)
                drawCircle(t.c(PAPEL), 2f * u, q)
            }
            // La caja.
            drawPath(caja, t.c(MADERA))
            clipPath(caja) {
                drawRect(t.s(0.25f), Offset(c.x + bw * 0.18f, c.y - bh), Size(bw, bh * 2))
                drawRect(t.b(0.3f), Offset(c.x - bw * 0.5f, c.y - bh), Size(bw * 0.22f, bh * 2))
                // La veta de la tapa.
                for (j in 0 until 6) drawLine(t.c(mezcla(MADERA, GRAFITO, 0.25f)).copy(alpha = 0.35f), Offset(c.x - bw * 0.4f + bw * 0.16f * j, c.y - bh), Offset(c.x - bw * 0.38f + bw * 0.16f * j, c.y + bh), 0.6f * u)
                // Un golpe en el borde, con la madera clara.
                drawOval(t.c(mezcla(MADERA, PAPEL, 0.4f)), Offset(inf.x + bw * 0.3f, inf.y + bw * 0.25f), Size(bw * 0.12f, bw * 0.06f))
            }
            drawPath(caja, t.c(mezcla(MADERA, PAPEL, 0.45f)), style = Stroke(1.2f * u))
            // La boca con su roseta, y el puente.
            drawCircle(t.c(mezcla(MADERA, PAPEL, 0.4f)), bw * 0.17f, Offset(c.x, sup.y + bw * 0.12f))
            drawCircle(t.c(GRAFITO), bw * 0.13f, Offset(c.x, sup.y + bw * 0.12f))
            drawRect(t.c(mezcla(MADERA, GRAFITO, 0.65f)), Offset(c.x - bw * 0.18f, inf.y + bw * 0.12f), Size(bw * 0.36f, bw * 0.07f))
            // Las cuatro cuerdas.
            for (j in 0 until 4) {
                val x = c.x - bw * 0.06f + bw * 0.04f * j
                drawLine(t.c(PAPEL).copy(alpha = 0.85f), Offset(x, cabeza + w * 0.04f), Offset(x, inf.y + bw * 0.14f), 0.5f * u)
            }
        }
        // La cincha que lo sujeta al mamparo, de un tornillo a otro, por la cintura.
        val a = Offset(c.x - bw * 0.62f, c.y - bh * 0.1f); val bb = Offset(c.x + bw * 0.68f, c.y + bh * 0.08f)
        listOf(a, bb).forEach { velcro(Rect(it.x - w * 0.009f, it.y - w * 0.014f, it.x + w * 0.009f, it.y + w * 0.014f), t, 2501) }
        cincha(a, bb, w * 0.013f, CINCHA, t)
        drawRoundRect(t.c(ALU_OSC), Offset(c.x + bw * 0.25f - w * 0.008f, c.y - w * 0.008f), Size(w * 0.016f, w * 0.018f), CornerRadius(1.5f * u))
    }

    /** Las bolsas de bebida y una de comida, de pie, con su pajita y su pinza, sujetas con una goma. */
    private fun DrawScope.bolsas(x0: Float, pie: Float, w: Float) {
        val u = density
        val t = tn(Offset(x0 + w * 0.04f, pie - w * 0.06f))
        val datos = listOf(Triple(0.0f, 0.15f, CIAN), Triple(0.036f, 0.125f, CINCHA), Triple(0.07f, 0.095f, VERDE))
        datos.forEachIndexed { i, (dx, al, banda) ->
            val r = Rect(x0 + w * dx, pie - w * al, x0 + w * (dx + 0.044f), pie)
            val giro = if (i == 1) 3f else if (i == 2) -2f else -1f
            rotate(giro, Offset(r.center.x, pie)) {
                val forma = Path().apply {
                    moveTo(r.left + 1f * u, r.top); lineTo(r.right - 1f * u, r.top)
                    cubicTo(r.right + 1f * u, r.top + r.height * 0.3f, r.right + 1.5f * u, r.bottom - r.height * 0.2f, r.right, r.bottom)
                    lineTo(r.left, r.bottom)
                    cubicTo(r.left - 1.5f * u, r.bottom - r.height * 0.2f, r.left - 1f * u, r.top + r.height * 0.3f, r.left + 1f * u, r.top)
                    close()
                }
                translate(r.width * 0.3f, 1f * u) { drawPath(forma, t.s(0.26f)) }
                val papel = if (i == 2) mezcla(TELA, MADERA, 0.25f) else ALU_LUZ
                drawPath(forma, t.c(papel))
                drawPath(forma, t.c(ALU_OSC), style = Stroke(0.8f * u))
                clipPath(forma) {
                    drawRect(t.s(0.22f), Offset(r.right - r.width * 0.35f, r.top), Size(r.width * 0.35f, r.height))
                    drawRect(t.b(0.5f), Offset(r.left + r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
                    // Las arrugas del papel de aluminio.
                    for (j in 0 until 4) drawLine(t.s(0.18f), Offset(r.left, r.top + r.height * (0.25f + 0.17f * j)), Offset(r.right, r.top + r.height * (0.3f + 0.15f * j)), 0.6f * u)
                    drawRect(t.c(banda), Offset(r.left, r.top + r.height * 0.34f), Size(r.width, r.height * 0.42f))
                    drawRect(t.s(0.2f), Offset(r.right - r.width * 0.35f, r.top + r.height * 0.34f), Size(r.width * 0.35f, r.height * 0.42f))
                    drawRect(t.c(PAPEL), Offset(r.left + r.width * 0.18f, r.top + r.height * 0.44f), Size(r.width * 0.5f, r.height * 0.05f))
                    drawRect(t.c(PAPEL).copy(alpha = 0.7f), Offset(r.left + r.width * 0.18f, r.top + r.height * 0.53f), Size(r.width * 0.35f, r.height * 0.03f))
                }
                // El sello de arriba y la pajita con su pinza.
                drawRect(t.c(mezcla(papel, ALU_OSC, 0.35f)), Offset(r.left, r.top), Size(r.width, r.height * 0.06f))
                if (i < 2) {
                    val px = r.left + r.width * 0.3f
                    drawLine(t.c(mezcla(PAPEL, CIAN, 0.2f)), Offset(px, r.top), Offset(px - r.width * 0.15f, r.top - w * 0.035f), 2.4f * u, StrokeCap.Round)
                    drawRect(t.c(if (i == 0) CINCHA else TELA_AZUL), Offset(px - r.width * 0.25f, r.top - w * 0.02f), Size(r.width * 0.3f, w * 0.008f))
                }
            }
        }
        // La goma elástica que las sujeta al mamparo.
        val y = pie - w * 0.075f
        drawPath(Path().apply { moveTo(x0 - w * 0.008f, y - w * 0.004f); cubicTo(x0 + w * 0.03f, y + w * 0.006f, x0 + w * 0.07f, y + w * 0.005f, x0 + w * 0.115f, y - w * 0.008f) },
            t.c(mezcla(TELA_AZUL, GRAFITO, 0.3f)), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawCircle(t.c(ALU), 2.2f * u, Offset(x0 - w * 0.008f, y - w * 0.004f))
    }

    /** La caja de herramientas abierta: la tapa apoyada en el mamparo con su espuma y sus destornilladores, la bandeja con su llave, su alicate y un hueco vacío. */
    private fun DrawScope.caja(r: Rect) {
        val u = density
        val t = tn(r.center)
        val w = r.width
        val boca = r.top + r.height * 0.45f
        // La tapa abierta, apoyada contra el mamparo (vista por dentro).
        val tapa = Path().apply { moveTo(r.left + w * 0.02f, boca); lineTo(r.left + w * 0.06f, r.top - r.height * 1.05f); lineTo(r.right - w * 0.04f, r.top - r.height * 1.0f); lineTo(r.right - w * 0.01f, boca); close() }
        translate(w * 0.05f, r.height * 0.06f) { drawPath(tapa, t.s(0.22f)) }
        drawPath(tapa, t.c(TELA_AZUL))
        clipPath(tapa) {
            val esp = Rect(r.left + w * 0.08f, r.top - r.height * 0.9f, r.right - w * 0.06f, boca - r.height * 0.08f)
            drawRect(t.c(GRAFITO), esp.topLeft, esp.size)
            // Los destornilladores en sus huecos, y uno que falta.
            for (j in 0 until 5) {
                val x = esp.left + esp.width * (0.12f + 0.19f * j)
                val y0 = esp.top + esp.height * 0.12f; val y1 = esp.bottom - esp.height * 0.12f
                drawRoundRect(t.s(0.6f), Offset(x - w * 0.025f, y0), Size(w * 0.05f, y1 - y0), CornerRadius(3f * u))
                if (j != 3) {
                    drawRoundRect(t.c(if (j % 2 == 0) CINCHA else CIAN), Offset(x - w * 0.02f, y0 + 1f * u), Size(w * 0.04f, (y1 - y0) * 0.45f), CornerRadius(2f * u))
                    drawRect(t.c(ALU_LUZ), Offset(x - w * 0.005f, y0 + (y1 - y0) * 0.45f), Size(w * 0.01f, (y1 - y0) * 0.52f))
                    drawRect(t.b(0.5f), Offset(x - w * 0.018f, y0 + 2f * u), Size(1f * u, (y1 - y0) * 0.4f))
                }
            }
            drawRect(t.s(0.25f), Offset(r.right - w * 0.18f, r.top - r.height * 1.1f), Size(w * 0.2f, r.height * 2f))
        }
        drawPath(tapa, t.c(mezcla(TELA_AZUL, GRAFITO, 0.4f)), style = Stroke(1.4f * u))
        // La bandeja de espuma, vista desde arriba.
        val esp = Rect(r.left, r.top, r.right, boca)
        drawRect(t.c(mezcla(GRAFITO, ALU_OSC, 0.25f)), esp.topLeft, esp.size)
        // La llave, el alicate y el hueco de la llave dinamométrica, que no está.
        drawRoundRect(t.s(0.6f), Offset(esp.left + w * 0.06f, esp.top + esp.height * 0.25f), Size(w * 0.36f, esp.height * 0.32f), CornerRadius(4f * u))
        drawRoundRect(t.c(ALU), Offset(esp.left + w * 0.08f, esp.top + esp.height * 0.32f), Size(w * 0.28f, esp.height * 0.18f), CornerRadius(2f * u))
        drawCircle(t.c(ALU), esp.height * 0.16f, Offset(esp.left + w * 0.37f, esp.top + esp.height * 0.41f))
        drawCircle(t.s(0.5f), esp.height * 0.07f, Offset(esp.left + w * 0.38f, esp.top + esp.height * 0.41f))
        drawRect(t.b(0.6f), Offset(esp.left + w * 0.08f, esp.top + esp.height * 0.32f), Size(w * 0.28f, 1f * u))
        drawRoundRect(t.s(0.65f), Offset(esp.left + w * 0.5f, esp.top + esp.height * 0.2f), Size(w * 0.42f, esp.height * 0.22f), CornerRadius(4f * u))
        drawRoundRect(t.c(ROJO), Offset(esp.left + w * 0.52f, esp.top + esp.height * 0.55f), Size(w * 0.2f, esp.height * 0.2f), CornerRadius(3f * u))
        drawRect(t.c(ALU_LUZ), Offset(esp.left + w * 0.72f, esp.top + esp.height * 0.6f), Size(w * 0.18f, esp.height * 0.1f))
        // El frente de la caja: el lado de sombra, los cierres y una pegatina vieja.
        val fr = Rect(r.left, boca, r.right, r.bottom)
        drawOval(t.s(0.35f), Offset(fr.left + w * 0.05f, fr.bottom - 3f * u), Size(w, 6f * u))
        drawRect(t.c(TELA_AZUL), fr.topLeft, fr.size)
        drawRect(t.b(0.55f), fr.topLeft, Size(fr.width, 1.4f * u))
        drawRect(t.b(0.2f), fr.topLeft, Size(fr.width * 0.15f, fr.height))
        drawRect(t.s(0.25f), Offset(fr.right - fr.width * 0.14f, fr.top), Size(fr.width * 0.14f, fr.height))
        listOf(0.18f, 0.78f).forEach { fx ->
            drawRect(t.c(ALU), Offset(fr.left + fr.width * fx, fr.top - 1f * u), Size(w * 0.06f, fr.height * 0.45f))
            drawRect(t.b(0.6f), Offset(fr.left + fr.width * fx, fr.top - 1f * u), Size(1f * u, fr.height * 0.45f))
        }
        drawRect(t.c(PAPEL), Offset(fr.left + fr.width * 0.38f, fr.top + fr.height * 0.3f), Size(w * 0.22f, fr.height * 0.42f))
        drawRect(t.c(ROJO), Offset(fr.left + fr.width * 0.38f, fr.top + fr.height * 0.3f), Size(w * 0.22f, fr.height * 0.1f))
        drawLine(t.s(0.35f), Offset(fr.left + w * 0.6f, fr.bottom - 3f * u), Offset(fr.left + w * 0.68f, fr.bottom - 2f * u), 0.8f * u)
    }

    // ---------------------------------------------------------------- al pie

    /** El extintor en su abrazadera de suelo: el cilindro rojo con su lado de sombra, la etiqueta, el manómetro, la válvula y la manguera. */
    private fun DrawScope.extintor(pie: Offset, alto: Float, ancho: Float) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - alto * 0.5f))
        val r = Rect(pie.x - ancho / 2, pie.y - alto * 0.78f, pie.x + ancho / 2, pie.y)
        drawOval(t.s(0.45f), Offset(r.left + ancho * 0.2f, pie.y - 3f * u), Size(ancho * 1.5f, 6f * u))
        drawRoundRect(t.c(ROJO), r.topLeft, r.size, CornerRadius(ancho * 0.4f, ancho * 0.3f))
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(t.s(0.3f), Offset(r.right - ancho * 0.32f, r.top), Size(ancho * 0.32f, r.height))
            drawRect(t.b(0.5f), Offset(r.left + ancho * 0.18f, r.top + ancho * 0.2f), Size(ancho * 0.1f, r.height * 0.7f))
            drawRect(t.c(PAPEL), Offset(r.left, r.top + r.height * 0.35f), Size(ancho, r.height * 0.28f))
            for (j in 0 until 4) drawRect(t.c(TINTA).copy(alpha = 0.5f), Offset(r.left + ancho * 0.25f, r.top + r.height * (0.4f + 0.05f * j)), Size(ancho * (0.3f + 0.3f * azar(2601, j)), 0.8f * u))
            drawRect(t.s(0.25f), Offset(r.right - ancho * 0.32f, r.top + r.height * 0.35f), Size(ancho * 0.32f, r.height * 0.28f))
        }
        // La abrazadera.
        drawRect(t.c(ALU_OSC), Offset(r.left - 1f * u, r.top + r.height * 0.15f), Size(ancho + 2f * u, r.height * 0.06f))
        drawRect(t.b(0.5f), Offset(r.left - 1f * u, r.top + r.height * 0.15f), Size(ancho + 2f * u, 1f * u))
        // La válvula, la palanca, el manómetro y la manguera.
        drawRect(t.c(GRAFITO), Offset(pie.x - ancho * 0.15f, r.top - alto * 0.07f), Size(ancho * 0.3f, alto * 0.08f))
        drawPath(Path().apply { moveTo(pie.x - ancho * 0.1f, r.top - alto * 0.07f); lineTo(pie.x + ancho * 0.55f, r.top - alto * 0.12f); lineTo(pie.x + ancho * 0.55f, r.top - alto * 0.1f); lineTo(pie.x + ancho * 0.05f, r.top - alto * 0.04f); close() }, t.c(ALU))
        val m = Offset(pie.x - ancho * 0.3f, r.top - alto * 0.04f)
        drawCircle(t.c(ALU_LUZ), ancho * 0.14f, m)
        drawCircle(t.c(VERDE), ancho * 0.06f, m, style = Stroke(1.2f * u))
        drawLine(t.c(TINTA), m, m + Offset(ancho * 0.05f, -ancho * 0.07f), 0.8f * u)
        drawPath(Path().apply { moveTo(pie.x + ancho * 0.1f, r.top - alto * 0.03f); cubicTo(pie.x + ancho * 0.75f, r.top, pie.x + ancho * 0.7f, r.top + alto * 0.3f, pie.x + ancho * 0.52f, r.top + alto * 0.42f) },
            t.c(GRAFITO), style = Stroke(2.6f * u, cap = StrokeCap.Round))
    }

    /** El contenedor de carga: la caja de composite blanco con sus esquineras, dos cinchas naranja con sus hebillas y su plantilla. */
    private fun DrawScope.contenedor(r: Rect) {
        val u = density
        val t = tn(Offset(r.left + r.width * 0.3f, r.top + r.height * 0.4f))
        drawRect(t.s(0.35f), Offset(r.left - r.width * 0.06f, r.top + r.height * 0.15f), Size(r.width * 0.1f, r.height))
        // La tapa vista un poco desde arriba y el frente.
        val tapa = r.height * 0.12f
        drawRect(t.c(mezcla(MAMPARO, PAPEL, 0.4f)), r.topLeft, Size(r.width, tapa))
        drawRect(t.b(0.6f), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(t.c(MAMPARO), Offset(r.left, r.top + tapa), Size(r.width, r.height - tapa))
        drawRect(t.s(0.25f), Offset(r.left, r.top + tapa), Size(r.width, 1.6f * u))
        drawRect(t.b(0.25f), Offset(r.left, r.top + tapa), Size(r.width * 0.12f, r.height))
        pinceladas(Rect(r.left, r.top + tapa, r.right, r.bottom), if (esNoche) NOCHE else MAMPARO_OSC, 2701, 8, u, 0.12f)
        // Las esquineras de goma.
        drawRect(t.c(GRAFITO), Offset(r.left, r.top), Size(r.width * 0.06f, tapa * 1.6f))
        drawRect(t.c(GRAFITO), Offset(r.left, r.top + tapa), Size(r.width * 0.035f, r.height))
        // Las cinchas, de arriba abajo, con sus hebillas.
        listOf(0.22f, 0.62f).forEach { fx ->
            val x = r.left + r.width * fx
            cincha(Offset(x, r.top), Offset(x, r.bottom), r.width * 0.07f, CINCHA, t)
            drawRoundRect(t.c(ALU), Offset(x - r.width * 0.05f, r.top + r.height * 0.35f), Size(r.width * 0.1f, r.height * 0.12f), CornerRadius(2f * u))
            drawRect(t.c(ALU_OSC), Offset(x - r.width * 0.035f, r.top + r.height * 0.38f), Size(r.width * 0.07f, r.height * 0.04f))
            drawRect(t.b(0.6f), Offset(x - r.width * 0.05f, r.top + r.height * 0.35f), Size(r.width * 0.1f, 1f * u))
        }
        val tp = tn(Offset(r.left + r.width * 0.35f, r.top + r.height * 0.6f))
        plantilla("12-4", r.left + r.width * 0.3f, r.top + r.height * 0.58f, r.height * 0.07f, r.height * 0.13f, tp.c(mezcla(GRAFITO, MAMPARO, 0.2f)))
    }

    // ---------------------------------------------------------------- los compartimentos

    /**
     * La etiqueta de los rótulos: de día, papel o visor claro; de noche, hundida hacia el azul según la luz que le llega, sin
     * pasar de 0.3 hacia la noche (la tinta queda a 4.5:1 o más en el más lejano).
     */
    private fun etiqueta(o: Boolean, k: Float, base: Color = PAPEL): Color =
        if (!o) base else mezcla(mezcla(base, NOCHE, 0.3f * (1f - k.coerceIn(0f, 1f) * 0.8f)), AMBAR, 0.12f * k)

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está esta bahía en el mueble (en fracciones del ancho), para saber cuánto le llega de la lámpara y de las pantallas.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val dia = if (oscuro) 0f else 0.08f - 0.17f * (c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f)
        val t = if (!oscuro) Tono(false, 0f, 0f, dia) else if (dentro) tono(true, centro) else Tono(true, 0.5f, 0.05f)
        // El fondo de la bahía lo pinta el mueble: aquí va solo lo que hay en ella.
        if (c.vacio) { tapaCiega(w, h, c, t); return }
        translate(0f, SALIDA * h * ab) {
            when {
                c.sinTema -> malla(w, h, c, t)
                // La variante sale de la semilla con una mezcla elegida para repartir: en la hoja de ejemplo ninguna pasa de 3 de 8
                // ni se repite entre vecinas.
                else -> when ((azar(c.semilla, 185) * 3f).toInt().coerceIn(0, 2)) {
                    0 -> modulo(w, h, c, t)
                    1 -> bolsaCarga(w, h, c, t)
                    else -> cartuchos(w, h, c, t)
                }
            }
        }
    }

    /** La caja del rótulo, en píxeles del compartimento. */
    private fun cajaRotulo(w: Float, h: Float) = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)

    /** El módulo de rack: el frente anodizado con sus asas, sus tornillos, los LED de estado (tantos como fichas), la pantallita con onda y el visor LCD con el tema. */
    private fun DrawScope.modulo(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val o = t.o
        val m = Rect(w * 0.025f, h * 0.1f, w * 0.975f, h * 0.95f)
        val col = listOf(ALU, mezcla(ALU, TELA_AZUL, 0.5f), mezcla(ALU_LUZ, MAMPARO, 0.5f), mezcla(GRAFITO, ALU_OSC, 0.4f))[(azar(c.semilla, 3) * 4f).toInt().coerceIn(0, 3)]
        drawRect(t.s(0.35f), Offset(m.left + 3f * u, m.bottom - 1f * u), Size(m.width, 4f * u))
        drawRect(t.c(col), m.topLeft, m.size)
        drawRect(t.b(0.25f), m.topLeft, Size(m.width * 0.25f, m.height))
        drawRect(t.s(0.2f), Offset(m.right - m.width * 0.12f, m.top), Size(m.width * 0.12f, m.height))
        bisel(m, t.b(0.6f), t.s(0.4f), 1.3f * u)
        // La sombra del travesaño de arriba sobre el frente.
        drawRect(t.s(0.15f), m.topLeft, Size(m.width, h * 0.05f))
        // Las asas, a los lados de arriba.
        listOf(m.left + w * 0.035f, m.right - w * 0.075f).forEachIndexed { i, x ->
            val ar = Rect(x, m.top + m.height * 0.1f, x + w * 0.04f, m.top + m.height * 0.5f)
            drawRoundRect(t.s(0.35f), Offset(ar.left + 2f * u, ar.top + 2.5f * u), ar.size, CornerRadius(4f * u), style = Stroke(w * 0.012f))
            drawRoundRect(t.c(if (i == 0) ALU_LUZ else ALU), ar.topLeft, ar.size, CornerRadius(4f * u), style = Stroke(w * 0.012f))
            drawLine(t.b(0.6f), Offset(ar.left, ar.top + 3f * u), Offset(ar.left, ar.bottom - 3f * u), 1f * u)
        }
        // Los tornillos de las esquinas.
        listOf(Offset(m.left + w * 0.055f, m.top + m.height * 0.62f), Offset(m.right - w * 0.055f, m.top + m.height * 0.62f)).forEachIndexed { i, q -> tornillo(q, w * 0.016f, t, 0.4f + i) }
        // Los LED de estado: tantos encendidos como fichas, hasta ocho.
        val k = c.n.coerceIn(0, 8)
        for (i in 0 until 8) {
            val p = Offset(m.left + w * 0.14f + i * w * 0.045f, m.top + m.height * 0.17f)
            drawCircle(t.c(GRAFITO), 2.6f * u, p)
            if (i < k) aureola(p, 1.7f * u, if (i == 7 || (i == k - 1 && c.n > 8)) AMBAR else LED, o) else drawCircle(t.c(ALU_OSC), 1.6f * u, p)
        }
        // Las rejillas de ventilación.
        for (i in 0 until 8) drawRoundRect(t.s(0.5f), Offset(m.left + w * 0.14f + i * w * 0.045f, m.top + m.height * 0.3f), Size(w * 0.022f, m.height * 0.17f), CornerRadius(2f * u))
        // La pantallita con su onda.
        val pa = Rect(m.left + w * 0.55f, m.top + m.height * 0.1f, m.right - w * 0.1f, m.top + m.height * 0.46f)
        drawRect(t.c(GRAFITO), Offset(pa.left - 2f * u, pa.top - 2f * u), Size(pa.width + 4f * u, pa.height + 4f * u))
        drawRect(Color(0xFF0A1830), pa.topLeft, pa.size)
        drawPath(ondaPath(Rect(pa.left + 2f * u, pa.top, pa.right - 2f * u, pa.bottom), c.semilla, 2), CIAN, style = Stroke(1.1f * u))
        if (!o) drawPath(Path().apply { moveTo(pa.left, pa.top); lineTo(pa.left + pa.width * 0.3f, pa.top); lineTo(pa.left, pa.bottom); close() }, luzMueble(0.15f))
        // El visor LCD con el tema, hundido en su marco.
        val r = cajaRotulo(w, h)
        drawRect(t.c(GRAFITO), Offset(r.left - 2.5f * u, r.top - 2.5f * u), Size(r.width + 5f * u, r.height + 5f * u))
        drawRect(t.b(0.5f), Offset(r.left - 2.5f * u, r.bottom + 2.5f * u), Size(r.width + 5f * u, 1f * u))
        drawRect(etiqueta(o, t.k, LCD), r.topLeft, r.size)
        drawRect(TINTA.copy(alpha = 0.12f), r.topLeft, Size(r.width, 2f * u))
        // Un rayón y una pegatina de inventario a medio despegar.
        drawLine(t.s(0.3f), Offset(m.left + w * 0.2f, m.bottom - h * 0.03f), Offset(m.left + w * 0.3f, m.bottom - h * 0.025f), 0.8f * u)
        if (azar(c.semilla, 8) < 0.5f) drawRect(t.c(CINCHA), Offset(m.left + w * 0.12f, m.top + m.height * 0.5f - h * 0.01f), Size(w * 0.06f, h * 0.035f))
    }

    /** El cajón de carga de tela: más abultado cuanto más guarda, con su cremallera, sus pliegues, su cincha y la etiqueta en un parche de velcro. */
    private fun DrawScope.bolsaCarga(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val o = t.o
        val lleno = (c.n / 7f).coerceIn(0.1f, 1f)
        val col = listOf(TELA, TELA_AZUL, mezcla(VERDE, GRAFITO, 0.35f), mezcla(CINCHA, TELA, 0.25f))[(azar(c.semilla, 4) * 4f).toInt().coerceIn(0, 3)]
        val arriba = h * (0.3f - 0.17f * lleno)
        val abajo = h * 0.95f
        val combo = h * 0.04f * lleno
        val forma = Path().apply {
            moveTo(w * 0.05f, abajo)
            cubicTo(w * 0.03f, abajo - h * 0.25f, w * 0.03f, arriba + h * 0.2f, w * 0.06f, arriba + h * 0.04f)
            cubicTo(w * 0.2f, arriba - combo, w * 0.42f, arriba + h * 0.03f, w * 0.55f, arriba)
            cubicTo(w * 0.72f, arriba - combo * 1.2f, w * 0.88f, arriba + h * 0.02f, w * 0.94f, arriba + h * 0.05f)
            cubicTo(w * 0.975f, arriba + h * 0.25f, w * 0.97f, abajo - h * 0.2f, w * 0.95f, abajo)
            close()
        }
        drawRect(t.s(0.35f), Offset(w * 0.08f, abajo - 1f * u), Size(w * 0.9f, 4f * u))
        drawPath(forma, t.c(col))
        clipPath(forma) {
            drawRect(t.s(0.22f), Offset(w * 0.78f, 0f), Size(w * 0.22f, h))
            drawRect(t.b(0.22f), Offset(0f, 0f), Size(w * 0.16f, h))
            // Los pliegues de la tela: tirones desde las esquinas y la caída del centro.
            val pl = listOf(
                listOf(0.08f, 0.0f, 0.2f, 0.12f, 0.3f, 0.3f), listOf(0.92f, 0.02f, 0.8f, 0.15f, 0.72f, 0.32f),
                listOf(0.4f, 0.04f, 0.38f, 0.14f, 0.45f, 0.28f), listOf(0.62f, 0.03f, 0.65f, 0.12f, 0.6f, 0.26f))
            pl.forEachIndexed { i, q ->
                val p = Path().apply {
                    moveTo(w * q[0], arriba + h * q[1]); cubicTo(w * q[2], arriba + h * q[3], w * q[2], arriba + h * q[3] + h * 0.05f, w * q[4], arriba + h * q[5])
                }
                drawPath(p, t.s(0.22f), style = Stroke((2.2f + azar(c.semilla, i)) * u, cap = StrokeCap.Round))
                translate(-2f * u, -1f * u) { drawPath(p, t.b(0.25f), style = Stroke(1.2f * u, cap = StrokeCap.Round)) }
            }
            // La costura del borde.
            val efe = PathEffect.dashPathEffect(floatArrayOf(2.6f * u, 1.8f * u))
            drawPath(forma, t.c(mezcla(col, TINTA, 0.35f)).copy(alpha = 0.55f), style = Stroke(1.6f * u * 2f, pathEffect = efe))
        }
        // La cremallera por arriba, con su tirador.
        val zip = Path().apply {
            moveTo(w * 0.08f, arriba + h * 0.075f)
            cubicTo(w * 0.25f, arriba + h * 0.04f - combo, w * 0.6f, arriba + h * 0.05f - combo, w * 0.92f, arriba + h * 0.085f)
        }
        drawPath(zip, t.c(GRAFITO), style = Stroke(2.6f * u))
        drawPath(zip, t.c(ALU_LUZ).copy(alpha = 0.6f), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f * u, 1.2f * u))))
        val tz = Offset(w * (0.62f + 0.2f * azar(c.semilla, 6)), arriba + h * 0.065f - combo * 0.8f)
        drawRoundRect(t.c(ALU), Offset(tz.x - 3f * u, tz.y - 2f * u), Size(6f * u, 5f * u), CornerRadius(1f * u))
        drawLine(t.c(CINCHA), Offset(tz.x, tz.y + 3f * u), Offset(tz.x + 2f * u, tz.y + h * 0.12f), 2.6f * u, StrokeCap.Round)
        // Las asas de cinta en los costados.
        listOf(w * 0.05f, w * 0.95f).forEach { x -> drawArc(t.c(mezcla(GRAFITO, col, 0.3f)), if (x < w / 2) 90f else -90f, 180f, false, Offset(x - w * 0.03f, h * 0.42f), Size(w * 0.06f, h * 0.16f), style = Stroke(2.4f * u)) }
        // La cincha que la sujeta al bastidor, cruzando por delante.
        cincha(Offset(0f, h * 0.5f), Offset(w, h * 0.49f), h * 0.07f, if (col == mezcla(CINCHA, TELA, 0.25f)) TELA_AZUL else CINCHA, t)
        drawRoundRect(t.c(ALU), Offset(w * 0.2f, h * 0.465f), Size(w * 0.06f, h * 0.06f), CornerRadius(1.5f * u))
        // El parche de velcro y la etiqueta escrita a mano.
        val r = cajaRotulo(w, h)
        velcro(Rect(r.left - 3f * u, r.top - 3f * u, r.right + 3f * u, r.bottom + 3f * u), t, c.semilla)
        drawRect(t.s(0.2f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
        drawRect(etiqueta(o, t.k), r.topLeft, r.size)
    }

    /**
     * La bandeja de cartuchos de memoria: una caja abierta de lado a lado de la bahía, con sus costados, su fondo con las
     * guías de cada ranura y el canto numerado. Más cartuchos con más fichas, de alturas distintas y el último ladeado; las
     * ranuras libres muestran su boca oscura y, algunas, su tapón de color. Con una o dos fichas, en el sitio libre, un rollo de
     * cinta sujeto con una goma. La tarjeta, en el frente.
     */
    private fun DrawScope.cartuchos(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val o = t.o
        val fr = Rect(w * 0.035f, h * 0.55f, w * 0.965f, h * 0.95f)
        val boca = h * 0.48f
        val tope = h * 0.15f
        val colTray = listOf(GRAFITO, mezcla(ALU, ALU_OSC, 0.3f), mezcla(TELA_AZUL, GRAFITO, 0.2f), mezcla(MAMPARO, ALU, 0.4f))[(azar(c.semilla, 5) * 4f).toInt().coerceIn(0, 3)]
        drawRect(t.s(0.35f), Offset(fr.left + 3f * u, fr.bottom - 1f * u), Size(fr.width, 4f * u))
        // La caja: los costados, el fondo con las guías de las ranuras y su canto de arriba.
        val lado = w * 0.035f
        val dentro = Rect(fr.left + lado, tope, fr.right - lado, fr.top)
        drawRect(t.c(mezcla(colTray, GRAFITO, 0.5f)), dentro.topLeft, Size(dentro.width, boca - tope))
        drawRect(t.s(0.25f), dentro.topLeft, Size(dentro.width, h * 0.06f))
        val total = 10
        val paso = dentro.width / total
        for (i in 1 until total) drawLine(t.s(0.35f), Offset(dentro.left + paso * i, tope + h * 0.03f), Offset(dentro.left + paso * i, boca), 1f * u)
        drawRect(t.c(mezcla(colTray, ALU_LUZ, 0.35f)), Offset(dentro.left, tope), Size(dentro.width, 1.6f * u))
        // El piso de la bandeja, visto un poco desde arriba, con la boca de cada ranura.
        drawRect(t.c(mezcla(colTray, ALU, 0.25f)), Offset(dentro.left, boca), Size(dentro.width, fr.top - boca))
        listOf(fr.left to true, fr.right - lado to false).forEach { (x, izq) ->
            drawRect(t.c(if (izq) mezcla(colTray, ALU_LUZ, 0.2f) else mezcla(colTray, GRAFITO, 0.25f)), Offset(x, tope), Size(lado, fr.top - tope))
            drawRect(t.b(if (izq) 0.55f else 0.2f), Offset(x, tope), Size(1.2f * u, fr.top - tope))
            drawRect(t.b(0.5f), Offset(x, tope), Size(lado, 1.2f * u))
        }
        val llenos = (c.n + 2).coerceIn(2, total)
        val colores = listOf(CIAN, CINCHA, ORO, LED, TELA_AZUL, ROJO, PAPEL)
        val cuerpo = listOf(mezcla(GRAFITO, ALU_OSC, 0.3f), mezcla(ALU, ALU_LUZ, 0.3f), mezcla(TELA_AZUL, GRAFITO, 0.3f), mezcla(CINCHA, MADERA, 0.4f))[(azar(c.semilla, 9) * 4f).toInt().coerceIn(0, 3)]
        // De noche, en las filas que aún reciben algo de luz, el canto de arriba de cada cartucho un punto más claro, para que se cuenten.
        val filoNoche = if (o && c.fila <= 2) mezcla(ALU_LUZ, CIAN, 0.25f).copy(alpha = 0.5f) else null
        for (i in 0 until total) {
            val x = dentro.left + paso * (i + 0.5f)
            // La boca de la ranura.
            drawRoundRect(t.s(0.6f), Offset(x - paso * 0.36f, boca + (fr.top - boca) * 0.25f), Size(paso * 0.72f, (fr.top - boca) * 0.55f), CornerRadius(1.2f * u))
            if (i < llenos) {
                val sube = h * (0.37f + 0.05f * azar(c.semilla, i))
                val giro = if (i == llenos - 1 && llenos < total) 8f else 0f
                rotate(giro, Offset(x, fr.top)) {
                    val cr = Rect(x - paso * 0.42f, fr.top - sube, x + paso * 0.42f, fr.top + 2f * u)
                    drawRect(t.s(0.2f), Offset(cr.left + paso * 0.25f, cr.top + 2f * u), cr.size)
                    drawRoundRect(t.c(cuerpo), cr.topLeft, cr.size, CornerRadius(1.5f * u))
                    val tinte = colores[(i + Math.floorMod(c.semilla, colores.size)) % colores.size]
                    drawRoundRect(t.c(tinte), cr.topLeft, Size(cr.width, sube * 0.16f), CornerRadius(1.5f * u))
                    // La muesca de agarre, la etiqueta y su renglón.
                    drawRect(t.s(0.35f), Offset(cr.left + cr.width * 0.25f, cr.top + sube * 0.2f), Size(cr.width * 0.5f, 1f * u))
                    drawRect(t.c(PAPEL), Offset(cr.left + cr.width * 0.18f, cr.top + sube * 0.3f), Size(cr.width * 0.64f, sube * 0.28f))
                    drawRect(t.c(TINTA).copy(alpha = 0.4f), Offset(cr.left + cr.width * 0.28f, cr.top + sube * 0.37f), Size(cr.width * 0.44f, 0.8f * u))
                    drawRect(t.c(TINTA).copy(alpha = 0.3f), Offset(cr.left + cr.width * 0.28f, cr.top + sube * 0.44f), Size(cr.width * 0.3f, 0.8f * u))
                    drawRect(t.b(0.55f), cr.topLeft, Size(1f * u, cr.height))
                    drawRect(t.s(0.3f), Offset(cr.right - cr.width * 0.22f, cr.top), Size(cr.width * 0.22f, cr.height))
                    if (filoNoche != null) drawRect(filoNoche, cr.topLeft, Size(cr.width, 1.2f * u))
                }
            } else if (azar(c.semilla + 13, i) < 0.45f) {
                // Un tapón de color en una ranura libre.
                val tap = colores[(i * 3 + Math.floorMod(c.semilla, colores.size)) % colores.size]
                val tp = Rect(x - paso * 0.32f, boca + (fr.top - boca) * 0.05f, x + paso * 0.32f, boca + (fr.top - boca) * 0.6f)
                drawRoundRect(t.c(tap), tp.topLeft, tp.size, CornerRadius(1.5f * u))
                drawRect(t.b(0.5f), tp.topLeft, Size(tp.width, 1f * u))
                drawRect(t.s(0.3f), Offset(tp.right - tp.width * 0.3f, tp.top), Size(tp.width * 0.3f, tp.height))
            }
        }
        // Con una o dos fichas, en el sitio libre: un rollo de cinta americana sujeto al costado con una goma.
        if (c.n <= 2) {
            val rc = Offset(dentro.right - paso * 2.2f, boca - h * 0.12f)
            val rr = h * 0.13f
            drawCircle(t.s(0.3f), rr, rc + Offset(rr * 0.25f, rr * 0.2f))
            drawCircle(t.c(ALU), rr, rc)
            drawCircle(t.b(0.4f), rr * 0.9f, rc - Offset(rr * 0.12f, rr * 0.12f), style = Stroke(rr * 0.12f))
            drawCircle(t.c(mezcla(TELA, MADERA, 0.25f)), rr * 0.5f, rc)
            drawCircle(t.c(mezcla(colTray, GRAFITO, 0.5f)), rr * 0.38f, rc)
            // La punta de la cinta, colgando.
            drawPath(Path().apply { moveTo(rc.x + rr * 0.6f, rc.y + rr * 0.78f); lineTo(rc.x + rr * 0.98f, rc.y + rr * 0.25f); lineTo(rc.x + rr * 1.05f, rc.y + rr * 0.95f); close() }, t.c(mezcla(ALU, ALU_LUZ, 0.3f)))
            // La goma, del costado derecho al rollo.
            drawLine(t.c(mezcla(TELA_AZUL, GRAFITO, 0.3f)), Offset(fr.right - lado, rc.y - rr * 0.7f), Offset(rc.x - rr * 0.2f, rc.y - rr * 0.98f), 2f * u, StrokeCap.Round)
            drawLine(t.c(mezcla(TELA_AZUL, GRAFITO, 0.3f)), Offset(fr.right - lado, rc.y + rr * 0.5f), Offset(rc.x - rr * 0.2f, rc.y + rr * 0.98f), 2f * u, StrokeCap.Round)
        }
        // El frente de la bandeja, con su lado de sombra, el tirador y la tarjeta.
        drawRect(t.c(colTray), fr.topLeft, fr.size)
        drawRect(t.b(0.6f), fr.topLeft, Size(fr.width, 1.4f * u))
        drawRect(t.b(0.2f), fr.topLeft, Size(fr.width * 0.15f, fr.height))
        drawRect(t.s(0.25f), Offset(fr.right - fr.width * 0.1f, fr.top), Size(fr.width * 0.1f, fr.height))
        // El número de cada ranura, pintado en el canto.
        for (i in 0 until total) {
            val x = dentro.left + paso * (i + 0.5f)
            plantilla("${(i + 1) % 10}", x - paso * 0.1f, fr.top + h * 0.007f, paso * 0.2f, h * 0.03f, t.c(mezcla(colTray, PAPEL, 0.6f)))
        }
        val r = cajaRotulo(w, h)
        drawRect(t.c(ALU_OSC), Offset(r.left - 2f * u, r.top - 2f * u), Size(r.width + 4f * u, r.height + 4f * u))
        drawRect(etiqueta(o, t.k), r.topLeft, r.size)
        drawRect(t.c(CIAN), r.topLeft, Size(2.4f * u, r.height))
        drawRect(t.b(0.4f), Offset(r.left, r.top), Size(r.width * 0.4f, 1f * u))
    }

    /** Lo que no tiene tema: una bolsa de malla colgada de un mosquetón, con cosas sueltas (más con más fichas) y una tira de cinta por rótulo. */
    private fun DrawScope.malla(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val o = t.o
        val gancho = Offset(w * 0.5f, h * 0.04f)
        val forma = Path().apply {
            moveTo(w * 0.44f, h * 0.15f)
            cubicTo(w * 0.2f, h * 0.22f, w * 0.06f, h * 0.45f, w * 0.08f, h * 0.8f)
            cubicTo(w * 0.1f, h * 0.95f, w * 0.9f, h * 0.96f, w * 0.92f, h * 0.8f)
            cubicTo(w * 0.94f, h * 0.45f, w * 0.8f, h * 0.22f, w * 0.56f, h * 0.15f)
            close()
        }
        translate(w * 0.02f, h * 0.02f) { drawPath(forma, t.s(0.18f)) }
        clipPath(forma) {
            drawRect(t.c(mezcla(GRAFITO, ALU_OSC, 0.4f)).copy(alpha = 0.5f), Offset.Zero, Size(w, h))
            // Lo de dentro: un rollo de cinta, una linterna, una barra de ración, un ovillo de cable, un rotulador.
            val n = (2 + c.n).coerceIn(3, 6)
            if (n > 0) { drawCircle(t.c(ALU_LUZ), h * 0.1f, Offset(w * 0.3f, h * 0.7f)); drawCircle(t.c(mezcla(GRAFITO, ALU_OSC, 0.4f)), h * 0.05f, Offset(w * 0.3f, h * 0.7f)) }
            if (n > 1) rotate(-20f, Offset(w * 0.6f, h * 0.72f)) { drawRoundRect(t.c(ROJO), Offset(w * 0.45f, h * 0.67f), Size(w * 0.3f, h * 0.1f), CornerRadius(3f * u)); drawRect(t.c(ALU), Offset(w * 0.72f, h * 0.66f), Size(w * 0.05f, h * 0.12f)) }
            if (n > 2) rotate(12f, Offset(w * 0.55f, h * 0.45f)) { drawRect(t.c(CINCHA), Offset(w * 0.4f, h * 0.4f), Size(w * 0.28f, h * 0.09f)); drawRect(t.c(PAPEL), Offset(w * 0.46f, h * 0.42f), Size(w * 0.08f, h * 0.05f)) }
            if (n > 3) for (j in 0 until 3) drawCircle(t.c(TELA_AZUL), h * (0.06f + 0.02f * j), Offset(w * 0.75f, h * 0.55f), style = Stroke(1.6f * u))
            if (n > 4) drawLine(t.c(LED), Offset(w * 0.2f, h * 0.45f), Offset(w * 0.4f, h * 0.55f), 3f * u, StrokeCap.Round)
            // La malla: dos familias de hilos en diagonal.
            val hilo = t.c(mezcla(CINCHA, PAPEL, 0.3f))
            for (j in -6..14) {
                val x = w * 0.08f * j
                drawLine(hilo, Offset(x, 0f), Offset(x + h * 0.9f, h), 0.9f * u)
                drawLine(hilo, Offset(x, 0f), Offset(x - h * 0.9f, h), 0.9f * u)
            }
            drawRect(t.s(0.22f), Offset(w * 0.75f, 0f), Size(w * 0.25f, h))
        }
        drawPath(forma, t.c(mezcla(CINCHA, GRAFITO, 0.3f)), style = Stroke(1.6f * u))
        // El cordón y el mosquetón.
        drawLine(t.c(GRAFITO), Offset(w * 0.44f, h * 0.15f), Offset(w * 0.56f, h * 0.15f), 2.4f * u, StrokeCap.Round)
        drawRoundRect(t.c(ALU), Offset(gancho.x - w * 0.025f, gancho.y - h * 0.06f), Size(w * 0.05f, h * 0.2f), CornerRadius(w * 0.025f), style = Stroke(2.2f * u))
        drawLine(t.c(ALU_OSC), Offset(gancho.x + w * 0.025f, gancho.y), Offset(gancho.x + w * 0.012f, gancho.y + h * 0.1f), 1.4f * u)
        drawLine(t.b(0.6f), Offset(gancho.x - w * 0.025f, gancho.y - h * 0.02f), Offset(gancho.x - w * 0.025f, gancho.y + h * 0.08f), 1f * u)
        // La tira de cinta del rótulo, con los extremos rotos.
        val r = cajaRotulo(w, h)
        val cin = Path().apply {
            moveTo(r.left - w * 0.015f, r.top + 1f * u); lineTo(r.right + w * 0.01f, r.top)
            lineTo(r.right + w * 0.02f, r.top + r.height * 0.3f); lineTo(r.right + w * 0.005f, r.top + r.height * 0.55f); lineTo(r.right + w * 0.018f, r.bottom)
            lineTo(r.left - w * 0.01f, r.bottom + 1f * u); lineTo(r.left - w * 0.025f, r.top + r.height * 0.6f); lineTo(r.left - w * 0.008f, r.top + r.height * 0.3f); close()
        }
        translate(1.5f * u, 2f * u) { drawPath(cin, t.s(0.2f)) }
        drawPath(cin, etiqueta(o, t.k, mezcla(ALU_LUZ, PAPEL, 0.5f)))
        drawLine(t.b(0.3f), Offset(r.left, r.top + 1.5f * u), Offset(r.right, r.top + 1f * u), 0.8f * u)
    }

    /** Una bahía sin tema: la tapa ciega con sus tornillos, su ranura de agarre y una cincha suelta que cuelga en curva. */
    private fun DrawScope.tapaCiega(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val m = Rect(w * 0.025f, h * 0.1f, w * 0.975f, h * 0.95f)
        drawRect(t.c(mezcla(ALU, MAMPARO, 0.4f)), m.topLeft, m.size)
        drawRect(t.b(0.2f), m.topLeft, Size(m.width * 0.22f, m.height))
        drawRect(t.s(0.2f), Offset(m.right - m.width * 0.12f, m.top), Size(m.width * 0.12f, m.height))
        bisel(m, t.b(0.55f), t.s(0.4f), 1.3f * u)
        drawRect(t.s(0.15f), m.topLeft, Size(m.width, h * 0.05f))
        listOf(Offset(m.left + w * 0.05f, m.top + h * 0.1f), Offset(m.right - w * 0.05f, m.top + h * 0.1f), Offset(m.left + w * 0.05f, m.bottom - h * 0.1f), Offset(m.right - w * 0.05f, m.bottom - h * 0.1f))
            .forEachIndexed { i, q -> tornillo(q, w * 0.016f, t, 0.3f + 0.9f * i) }
        drawRoundRect(t.s(0.45f), Offset(m.center.x - w * 0.12f, m.top + m.height * 0.2f), Size(w * 0.24f, h * 0.05f), CornerRadius(4f * u))
        drawLine(t.b(0.4f), Offset(m.center.x - w * 0.11f, m.top + m.height * 0.2f + h * 0.05f), Offset(m.center.x + w * 0.11f, m.top + m.height * 0.2f + h * 0.05f), 0.8f * u)
        pinceladas(m, if (t.o) NOCHE else ALU_OSC, 2801 + c.semilla % 50, 5, u, 0.08f)
        // La cincha suelta, del tornillo de arriba a la izquierda hasta abajo, con la hebilla colgando.
        val a = Offset(m.left + w * 0.05f, m.top + h * 0.1f)
        val p = Path().apply { moveTo(a.x, a.y); cubicTo(w * 0.25f, h * 0.75f, w * 0.55f, h * 0.85f, w * 0.7f, h * 0.62f) }
        translate(2f * u, 3f * u) { drawPath(p, t.s(0.25f), style = Stroke(h * 0.06f, cap = StrokeCap.Butt)) }
        drawPath(p, t.c(CINCHA), style = Stroke(h * 0.055f, cap = StrokeCap.Butt))
        drawPath(p, t.b(0.35f), style = Stroke(1f * u))
        drawRoundRect(t.c(ALU_OSC), Offset(w * 0.68f, h * 0.55f), Size(w * 0.06f, h * 0.1f), CornerRadius(1.5f * u))
        drawRect(t.b(0.5f), Offset(w * 0.68f, h * 0.55f), Size(w * 0.06f, 1f * u))
    }

    override val rotulo = Rect(0.05f, 0.6f, 0.95f, 0.86f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.orbita
    override val salida = SALIDA
    override val placa = Rect(0.33f, 0.18f, 0.67f, 0.82f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El panel oscuro con unas estrellas tenues, la retícula y los corchetes de las esquinas.
        drawRect(VACIO)
        for (k in 0 until 60) drawCircle(Color.White.copy(alpha = 0.1f + 0.25f * azar(97, k)), (0.5f + 1f * azar(98, k)) * u, Offset(w * azar(99, k), h * azar(100, k)))
        val c = Offset(w / 2, h * 0.45f)
        drawCircle(AZUL.copy(alpha = 0.1f), w * 0.3f, c, style = Stroke(1 * u))
        drawCircle(AZUL.copy(alpha = 0.08f), w * 0.18f, c, style = Stroke(1 * u))
        drawLine(AZUL.copy(alpha = 0.08f), Offset(c.x - w * 0.4f, c.y), Offset(c.x + w * 0.4f, c.y), 1 * u)
        drawLine(AZUL.copy(alpha = 0.08f), Offset(c.x, c.y - w * 0.4f), Offset(c.x, c.y + w * 0.4f), 1 * u)
        corchetes(Rect(10 * u, 10 * u, w - 10 * u, h - 10 * u), 30 * u, AZUL.copy(alpha = 0.6f), 2 * u)
    }

    override fun tintaInterior(oscuro: Boolean) = TEXTO
    override val pared: Dp = 18.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(4.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // Los corchetes del visor, una barra de progreso fina abajo y las marcas del día juliano arriba.
        val m = 3 * u * g
        corchetes(Rect(m, m, w - m, h - m), 10 * u * g, AZUL.copy(alpha = 0.7f), 1.2f * u * g)
        val by = h - 6 * u * g
        drawLine(AZUL.copy(alpha = 0.15f), Offset(w * 0.25f, by), Offset(w * 0.75f, by), 2 * u * g)
        drawLine(AZUL, Offset(w * 0.25f, by), Offset(w * (0.25f + 0.5f * (0.2f + 0.8f * azar(semilla, 1))), by), 2 * u * g)
        for (k in 0 until 12) drawLine(AZUL.copy(alpha = 0.25f), Offset(w * 0.3f + k * 4 * u * g, 3 * u * g), Offset(w * 0.3f + k * 4 * u * g, (if (k % 4 == 0) 7 else 5) * u * g), 0.6f * u)
    }

    override val margenes = MargenesFicha(13.dp, 14.dp, 14.dp, 26.dp, 26.dp, 30.dp)
    override val proporcionFicha = 1.1f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(AZUL, p.color)
            "convence" -> drawPath(estrella(c, s * 0.48f, 5, 0.42f), NARANJA)
            // Una órbita con su satélite.
            "piensa" -> {
                rotate(-25f, c) { drawOval(AZUL, Offset(c.x - s * 0.44f, c.y - s * 0.2f), Size(s * 0.88f, s * 0.4f), style = Stroke(s * 0.06f)) }
                drawCircle(AZUL, s * 0.12f, c)
                drawCircle(NARANJA, s * 0.08f, Offset(c.x + s * 0.36f, c.y - s * 0.16f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(NARANJA)
            else -> {}
        }
    }

    /** Un parche de misión. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(PANEL_LUZ, s * 0.46f, c)
        drawCircle(AZUL, s * 0.46f, c, style = Stroke(s * 0.08f))
        drawPath(estrella(c, s * 0.26f, 5, 0.42f), Color.White)
    }

    override val desorden = 0f
}
