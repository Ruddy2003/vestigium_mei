package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TrazosNouveau
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Art nouveau
// =====================================================================

/**
 * Las rayas de los renglones de un campo de varios renglones, de abajo arriba: la primera ([k] = 0) al pie del último
 * renglón, a [margenY] del canto de abajo, y luego una cada 24 sp mientras quede sitio arriba (16 dp bajo [margenY]).
 */
private inline fun DrawScope.renglonesNou(margenY: Dp, raya: (k: Int, y: Float) -> Unit) {
    val paso = maxOf(24.sp.toPx(), 1f)
    val tope = margenY.toPx() + 16.dp.toPx()
    var y = size.height - margenY.toPx() + 1.dp.toPx()
    var k = 0
    while (y > tope && k < 200) { raya(k, y); y -= paso; k++ }
}

/** La razón de contraste entre dos colores opacos (WCAG 2): de 1 (iguales) a 21 (negro sobre blanco). */
private fun contrasteNou(a: Color, b: Color): Float {
    val la = a.luminance(); val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * El color [c] de un texto, tal cual si se lee sobre [fondo] (contraste de al menos [minimo]:1); si no, llevado poco a poco
 * hacia la tinta de la prensa (sobre un fondo claro) o hacia el papel crema (sobre uno oscuro) hasta que se lea. Con [tenue]
 * se apaga antes hacia el fondo, lo más que deja ese contraste.
 */
private fun legibleNou(c: Color, fondo: Color, tenue: Boolean = false, minimo: Float = 4.6f): Color {
    val opaco = c.copy(alpha = 1f)
    if (tenue) for (k in 8 downTo 1) {
        val m = mezcla(opaco, fondo, k * 0.05f)
        if (contrasteNou(m, fondo) >= minimo) return m
    }
    if (contrasteNou(opaco, fondo) >= minimo) return opaco
    val hacia = if (fondo.luminance() > 0.18f) Color(0xFF1F1A24) else Color(0xFFF4EEDF)
    for (k in 1..20) {
        val m = mezcla(opaco, hacia, k * 0.05f)
        if (contrasteNou(m, fondo) >= minimo) return m
    }
    return hacia
}

/** El ancho, en píxeles, de [texto] en una sola línea con el mismo estilo con que lo dibuja [textoEn]. */
private fun anchoNou(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f): Float =
    if (texto.isBlank()) 0f
    else medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, fontWeight = peso, letterSpacing = espaciado.sp),
        maxLines = 1, softWrap = false).size.width.toFloat()

/**
 * Un recuadro con las esquinas del tema, las del marco de un cartel que se curva como un tallo: la de arriba a la izquierda
 * y la de abajo a la derecha muy redondas ([grande]), las otras dos casi rectas ([chica]).
 */
private fun hojaNou(r: Rect, grande: Float, chica: Float): Path = Path().apply {
    addRoundRect(RoundRect(r, CornerRadius(grande), CornerRadius(chica), CornerRadius(grande), CornerRadius(chica)))
}

/**
 * Un trazo de pluma por [pts], de grueso [g]: nace a [ini] del grueso, engorda hasta el grueso entero en [pico] (de 0 a 1 a
 * lo largo) y se afina hasta [fin] al llegar, como la línea de látigo de los carteles.
 */
private fun plumaNou(pts: List<Offset>, g: Float, ini: Float = 0.25f, fin: Float = 0.3f, pico: Float = 0.45f): Path =
    TrazosNouveau.cinta(pts) { t ->
        g * (if (t < pico) ini + (1f - ini) * sin(t / pico * PI.toFloat() / 2) else 1f - (1f - fin) * ((t - pico) / (1f - pico)))
    }

/**
 * Una línea de látigo corta de [a] a [b], apenas ondulada ([comba], en fracción del largo), que al llegar a [b] se enrosca en
 * un rizo de radio [rizo] hacia el lado [horario] (en la pantalla).
 */
private fun latigoNou(a: Offset, b: Offset, rizo: Float, horario: Boolean, comba: Float = 0.06f, vueltas: Float = 1.15f): List<Offset> {
    val dx = b.x - a.x; val dy = b.y - a.y
    val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
    val nx = -dy / l; val ny = dx / l
    val c1 = Offset(a.x + dx * 0.35f + nx * l * comba, a.y + dy * 0.35f + ny * l * comba)
    val c2 = Offset(a.x + dx * 0.75f - nx * l * comba * 0.7f, a.y + dy * 0.75f - ny * l * comba * 0.7f)
    return TrazosNouveau.bezier(a, c1, c2, b, 30) + TrazosNouveau.puntosRizo(b, Offset(b.x - c2.x, b.y - c2.y), rizo, horario, vueltas, 28)
}

/**
 * Los puntos de una espiral alrededor de [c]: empieza en el ángulo [desde] (en grados; 90 = abajo) a radio [r0] y da [vueltas]
 * vueltas, en el sentido de las agujas del reloj si [horario], cerrándose hasta [r1].
 */
private fun espiralNou(c: Offset, r0: Float, r1: Float, desde: Float, vueltas: Float, horario: Boolean, n: Int = 44): List<Offset> = List(n + 1) { i ->
    val t = i / n.toFloat()
    val a = Math.toRadians((desde + (if (horario) 1f else -1f) * 360f * vueltas * t).toDouble())
    val r = r0 + (r1 - r0) * t
    Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
}

/** Los puntos de [pts] corridos [d] hacia su normal (hacia abajo si la línea va hacia la derecha). */
private fun corridaNou(pts: List<Offset>, d: Float): List<Offset> = List(pts.size) { i ->
    val a = pts[max(i - 1, 0)]; val b = pts[min(i + 1, pts.size - 1)]
    val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.0001f)
    Offset(pts[i].x - dy / l * d, pts[i].y + dx / l * d)
}

/**
 * Art nouveau: las piezas comunes del taller de litografía que imprime carteles en París, hacia 1900. El campo es la cartela
 * del cartel, el papel crema donde va la letra, en su marco de dos filetes (el hierro verde de Guimard y el oro pálido) con las
 * esquinas del tema; con el foco, el portaplumas se posa en el filete y se enciende la lámpara. Lo demás es del mismo taller y
 * de la misma calle: la línea de látigo con su capullo, el cabujón de vidrio de Nancy, la libélula de joyero, la piedra
 * litográfica recién graneada, el jarrón de Nancy, el farol del metro y la campanilla de cristal, la prueba de color, el pliego
 * con sus cruces de registro, el papel pintado con la lámpara de hongo, la calle con la entrada de metro y el vidrio grabado al
 * ácido. De noche es la misma París a oscuras, en su azul negro: la luz cálida es solo la de las lámparas, y solo cerca de ellas.
 */
object ComunNouveau : DisenoComun {
    /** La calle de noche, el azul negro donde se hunde lo que no alumbra ninguna lámpara. */
    private val NOCHE = Color(0xFF121519)
    private val HIERRO = TrazosNouveau.HIERRO
    private val HIERRO_SOMBRA = TrazosNouveau.HIERRO_SOMBRA
    private val TALLO = TrazosNouveau.TALLO
    private val TALLO_SOMBRA = TrazosNouveau.TALLO_SOMBRA
    private val IRIS = TrazosNouveau.IRIS
    private val IRIS_LENGUA = TrazosNouveau.IRIS_LENGUA
    private val MALVA = TrazosNouveau.MALVA
    private val ORO = TrazosNouveau.ORO
    private val ORO_BRILLO = TrazosNouveau.ORO_BRILLO
    private val VIDRIO_AMBAR = TrazosNouveau.VIDRIO_AMBAR
    private val VIDRIO_VIOLETA = TrazosNouveau.VIDRIO_VIOLETA
    private val VIDRIO_VERDE = TrazosNouveau.VIDRIO_VERDE
    private val NACAR = TrazosNouveau.NACAR
    private val LIBELULA = TrazosNouveau.LIBELULA
    private val PIEDRA = TrazosNouveau.PIEDRA
    private val LUZ = TrazosNouveau.LUZ
    private val LUZ_HALO = TrazosNouveau.LUZ_HALO
    private val CAFE = TrazosNouveau.CAFE
    private val PLUMA = TrazosNouveau.PLUMA
    /** El papel crema de los carteles y el mismo papel de noche. */
    private val PAPEL = Color(0xFFF5EFE1)
    private val PAPEL_NOCHE = Color(0xFF1C2024)
    /** La cara de la piedra litográfica recién graneada. */
    private val CARA_PIEDRA = Color(0xFFDAD4C5)
    /** El mango de ébano del portaplumas y el latón de su virola. */
    private val EBANO = Color(0xFF2B2422)
    private val LATON = Color(0xFFB8963E)
    /** Los tejados de zinc de París, de noche, y sus ventanas. */
    private val ZINC_NOCHE = Color(0xFF0B0E13)

    // ---------------------------------------------------------------- los colores del taller (de día y de noche)

    /** Lo que no alumbra ninguna lámpara: de noche, hundido en el azul negro de la calle (tanto como [k]). */
    private fun n(c: Color, oscuro: Boolean, k: Float = 0.78f): Color = if (oscuro) mezcla(c, NOCHE, k) else c
    /** Un color a [cerca] (0 a 1) de una lámpara: de día, tal cual; de noche, menos hundido cuanto más cerca, y entibiado por la luz. */
    private fun alLuz(c: Color, oscuro: Boolean, cerca: Float): Color {
        if (!oscuro) return c
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.82f - 0.5f * k), LUZ, 0.16f * k)
    }
    private fun tinta(oscuro: Boolean): Color = if (oscuro) Color(0xFFECE5D6) else Color(0xFF2A2530)
    private fun tenue(oscuro: Boolean): Color = if (oscuro) Color(0xFFADA79A) else Color(0xFF5E5662)
    /** El verde: de día, el salvia del tallo; de noche, el verde de los cafés. */
    private fun verde(oscuro: Boolean): Color = if (oscuro) Color(0xFF8CCBA4) else Color(0xFF4B6A4E)
    /** El violeta: de día, el iris; de noche, la lámpara de vidrio violeta. */
    private fun violeta(oscuro: Boolean): Color = if (oscuro) Color(0xFFBFA6DE) else Color(0xFF6D4C82)
    /** El oro, para que se lea: de día, el oro pálido en tinta; de noche, la luz eléctrica. */
    private fun oroTinta(oscuro: Boolean): Color = if (oscuro) Color(0xFFE8C46E) else Color(0xFF7A5810)
    /** El filo que levanta la luz: la de la ventana del taller de día; de noche, apenas el reflejo frío de la calle. */
    private fun filoLuz(oscuro: Boolean): Color = if (oscuro) Color(0xFFB8C6D0).copy(alpha = 0.1f) else Color.White.copy(alpha = 0.6f)
    /** La sombra de lo que está encima de algo. */
    private fun sombra(oscuro: Boolean): Color = Color.Black.copy(alpha = if (oscuro) 0.5f else 0.15f)
    private fun papel(oscuro: Boolean): Color = if (oscuro) PAPEL_NOCHE else PAPEL
    /** El filete de hierro de los marcos: el verde de Guimard; de noche, el mismo hierro con el filo frío de la calle. */
    private fun hierroLinea(oscuro: Boolean): Color = if (oscuro) Color(0xFF5B7264) else HIERRO
    /** El filete de oro pálido de los marcos: de noche, el oro a oscuras. */
    private fun oroLinea(oscuro: Boolean): Color = if (oscuro) mezcla(ORO, NOCHE, 0.48f) else mezcla(ORO, Color(0xFF8A6A22), 0.28f)

    // ---------------------------------------------------------------- el papel, la piedra y la pluma

    /** Las fibras y las motas del papel de los carteles dentro de [r], apenas; de noche, casi nada. */
    private fun DrawScope.fibras(r: Rect, semilla: Int, oscuro: Boolean, fuerza: Float = 1f) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        val cuantas = (r.width * r.height / (85f * u * u) * fuerza).toInt().coerceIn(0, 600)
        val claras = Path(); val oscuras = Path(); val motas = Path()
        for (k in 0 until cuantas) {
            val x = r.left + r.width * azar(semilla, k * 4); val y = r.top + r.height * azar(semilla, k * 4 + 1)
            val q = azar(semilla, k * 4 + 2)
            if (q > 0.88f) motas.addOval(Rect(Offset(x, y), (0.25f + 0.35f * azar(semilla, k * 4 + 3)) * u))
            else {
                val ang = azar(semilla, k * 4 + 3) * PI.toFloat()
                val l = (1.4f + 3.2f * q) * u
                val p = if (k % 2 == 0) claras else oscuras
                p.moveTo(x, y)
                p.quadraticTo(x + cos(ang) * l * 0.5f + sin(ang) * 0.6f * u, y + sin(ang) * l * 0.5f - cos(ang) * 0.6f * u, x + cos(ang) * l, y + sin(ang) * l)
            }
        }
        drawPath(claras, if (oscuro) Color.White.copy(alpha = 0.022f) else Color.White.copy(alpha = 0.55f), style = Stroke(0.45f * u, cap = StrokeCap.Round))
        drawPath(oscuras, if (oscuro) Color.Black.copy(alpha = 0.22f) else Color(0xFF8C7A55).copy(alpha = 0.14f), style = Stroke(0.4f * u, cap = StrokeCap.Round))
        drawPath(motas, if (oscuro) Color.Black.copy(alpha = 0.3f) else Color(0xFF8C7A55).copy(alpha = 0.16f))
    }

    /** El grano de la piedra litográfica dentro de [r]: los puntitos que deja la arena al granearla, oscuros y claros. */
    private fun DrawScope.grano(r: Rect, semilla: Int, oscuro: Boolean, densidad: Float = 1f) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        val cuantos = (r.width * r.height / (16f * u * u) * densidad).toInt().coerceIn(0, 2400)
        val oscuros = Path(); val claros = Path()
        for (k in 0 until cuantos) {
            val x = r.left + r.width * azar(semilla, k * 3); val y = r.top + r.height * azar(semilla, k * 3 + 1)
            val rr = (0.16f + 0.28f * azar(semilla, k * 3 + 2)) * u
            (if (k % 3 == 0) claros else oscuros).addOval(Rect(Offset(x, y), rr))
        }
        drawPath(oscuros, if (oscuro) Color.Black.copy(alpha = 0.32f) else Color(0xFF6E6656).copy(alpha = 0.17f))
        drawPath(claros, if (oscuro) Color.White.copy(alpha = 0.03f) else Color.White.copy(alpha = 0.55f))
    }

    /**
     * El portaplumas acostado de [x0] (la punta de la plumilla) a [x1] (el remate del mango), a la altura [y]: la plumilla de
     * acero con su hendidura y su ojo, la virola de latón con sus dos anillos y el mango de ébano que engorda hacia el medio,
     * con su brillo y su sombra corta; en la punta, una gota de tinta.
     */
    private fun DrawScope.portaplumas(x0: Float, x1: Float, y: Float, oscuro: Boolean) {
        val u = density
        if (x1 - x0 < 24 * u) return
        val acero = if (oscuro) Color(0xFF7A8288) else Color(0xFF8E959B)
        val ebano = if (oscuro) mezcla(EBANO, NOCHE, 0.15f) else EBANO
        val laton = if (oscuro) mezcla(LATON, NOCHE, 0.3f) else LATON
        translate(0.6f * u, 1.4f * u) { drawLine(sombra(oscuro), Offset(x0 + 2 * u, y), Offset(x1 - 1 * u, y), 2.8f * u, StrokeCap.Round) }
        // La plumilla: la punta afilada, los hombros, la hendidura y el ojo.
        val plumilla = Path().apply {
            moveTo(x0, y + 0.2f * u)
            quadraticTo(x0 + 3 * u, y - 1.3f * u, x0 + 7.6f * u, y - 1.45f * u)
            lineTo(x0 + 7.6f * u, y + 1.45f * u)
            quadraticTo(x0 + 3 * u, y + 1.3f * u, x0, y + 0.2f * u)
            close()
        }
        drawPath(plumilla, Brush.verticalGradient(listOf(mezcla(acero, Color.White, if (oscuro) 0.15f else 0.4f), acero, mezcla(acero, Color.Black, 0.4f)), y - 1.45f * u, y + 1.45f * u))
        drawLine(mezcla(acero, Color.Black, 0.6f), Offset(x0 + 0.7f * u, y + 0.15f * u), Offset(x0 + 4.2f * u, y), 0.35f * u)
        drawCircle(mezcla(acero, Color.Black, 0.6f), 0.5f * u, Offset(x0 + 4.7f * u, y))
        // La virola de latón.
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(laton, Color.White, if (oscuro) 0.15f else 0.45f), laton, mezcla(laton, Color.Black, 0.35f)), y - 1.7f * u, y + 1.7f * u),
            Offset(x0 + 7.2f * u, y - 1.7f * u), Size(4.6f * u, 3.4f * u), CornerRadius(0.6f * u))
        for (k in 0..1) drawLine(mezcla(laton, Color.Black, 0.45f), Offset(x0 + (8.5f + k * 1.7f) * u, y - 1.7f * u), Offset(x0 + (8.5f + k * 1.7f) * u, y + 1.7f * u), 0.3f * u)
        // El mango de ébano.
        val m0 = x0 + 11.6f * u
        val mango = Path().apply {
            moveTo(m0, y - 1.35f * u)
            cubicTo(m0 + (x1 - m0) * 0.3f, y - 2f * u, x1 - 6 * u, y - 1.8f * u, x1 - 1.2f * u, y - 1.15f * u)
            quadraticTo(x1, y, x1 - 1.2f * u, y + 1.15f * u)
            cubicTo(x1 - 6 * u, y + 1.8f * u, m0 + (x1 - m0) * 0.3f, y + 2f * u, m0, y + 1.35f * u)
            close()
        }
        drawPath(mango, Brush.verticalGradient(listOf(mezcla(ebano, Color.White, if (oscuro) 0.1f else 0.28f), ebano, mezcla(ebano, Color.Black, 0.45f)), y - 2f * u, y + 2f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.16f else 0.5f), Offset(m0 + 2 * u, y - 1f * u), Offset(x1 - 4 * u, y - 0.9f * u), 0.45f * u, StrokeCap.Round)
        drawCircle(PLUMA.copy(alpha = 0.9f), 0.6f * u, Offset(x0 + 0.3f * u, y + 0.55f * u))
    }

    // ---------------------------------------------------------------- las figuras del taller y de la calle

    /**
     * Un capullo de [base] hacia [punta], de [ancho]: el pétalo cerrado, con su mitad en sombra, y los dos sépalos que lo
     * abrazan por abajo.
     */
    private fun DrawScope.capullo(base: Offset, punta: Offset, ancho: Float, flor: Color, caliz: Color) {
        val dx = punta.x - base.x; val dy = punta.y - base.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val ux = dx / l; val uy = dy / l
        with(TrazosNouveau) {
            drawPath(petalo(base, punta, ancho), mezcla(flor, Color.Black, 0.2f))
            drawPath(petalo(Offset(base.x - uy * ancho * 0.12f, base.y + ux * ancho * 0.12f), Offset(punta.x - uy * ancho * 0.05f, punta.y + ux * ancho * 0.05f), ancho * 0.55f),
                mezcla(flor, Color.White, 0.12f))
            for (s in listOf(-1f, 1f)) {
                val g = 0.55f * s
                val vx = ux * cos(g) - uy * sin(g); val vy = ux * sin(g) + uy * cos(g)
                drawPath(hojaLanza(base, Offset(base.x + vx * l * 0.55f, base.y + vy * l * 0.55f), ancho * 0.42f, 0.12f * s), caliz)
            }
        }
    }

    /**
     * Un cabujón de vidrio de Nancy en su engaste de oro, como las joyas de 1900: el óvalo que pasa del violeta de arriba al
     * verde y al ámbar de abajo, la hondura del borde y el reflejo. Con [luz] (0 a 1) se enciende desde dentro y echa su halo.
     */
    private fun DrawScope.cabujon(c: Offset, rx: Float, ry: Float, oscuro: Boolean, luz: Float) {
        val u = density
        val e = luz.coerceIn(0f, 1f)
        val r = Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)
        val engaste = r.inflate(0.9f * u)
        if (e > 0f) drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = 0.5f * e), LUZ.copy(alpha = 0.12f * e), Color.Transparent), c, ry * 3.4f), ry * 3.4f, c)
        val oro = if (oscuro) mezcla(ORO, NOCHE, 0.5f - 0.38f * e) else mezcla(ORO, Color(0xFF8A6A22), 0.2f)
        translate(0.3f * u, 0.7f * u) { drawOval(sombra(oscuro), engaste.topLeft, engaste.size) }
        drawOval(Brush.linearGradient(listOf(mezcla(oro, ORO_BRILLO, 0.6f), oro, mezcla(oro, Color.Black, 0.4f)), engaste.topLeft, engaste.bottomRight), engaste.topLeft, engaste.size)
        fun v(c0: Color) = if (oscuro) mezcla(mezcla(c0, NOCHE, 0.58f - 0.52f * e), LUZ, 0.3f * e) else mezcla(c0, LUZ_HALO, 0.4f * e)
        drawOval(Brush.verticalGradient(listOf(v(VIDRIO_VIOLETA), v(VIDRIO_VERDE), v(VIDRIO_AMBAR)), r.top, r.bottom), r.topLeft, r.size)
        drawOval(Brush.radialGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.3f)), c, max(rx, ry)), r.topLeft, r.size)
        drawOval(Color.White.copy(alpha = if (oscuro) 0.2f + 0.35f * e else 0.7f), Offset(c.x - rx * 0.6f, c.y - ry * 0.66f), Size(rx * 0.6f, ry * 0.42f))
    }

    /** Una cruz de registro, la que el impresor pone en las esquinas del pliego para casar las piedras de color: el círculo y su cruz. */
    private fun DrawScope.registro(c: Offset, r: Float, color: Color) {
        val u = density
        drawCircle(color, r, c, style = Stroke(0.45f * u))
        drawLine(color, Offset(c.x - r * 1.7f, c.y), Offset(c.x + r * 1.7f, c.y), 0.45f * u)
        drawLine(color, Offset(c.x, c.y - r * 1.7f), Offset(c.x, c.y + r * 1.7f), 0.45f * u)
    }

    /**
     * La libélula a pluma, sin color, como el boceto de la joya: las cuatro alas de contorno, el cuerpo en una línea y la
     * cabeza. [c] es el tórax, [largo] lo que mide de la cabeza a la cola y [angulo] hacia dónde mira (0 = arriba).
     */
    private fun DrawScope.libelulaBoceto(c: Offset, largo: Float, angulo: Float, color: Color) {
        val u = density
        val k = largo / 100f
        rotate(angulo, c) {
            val trazo = Stroke(maxOf(0.6f * u, 3.2f * k), cap = StrokeCap.Round, join = StrokeJoin.Round)
            listOf(Triple(-1f, -4f, -22f), Triple(1f, -4f, -22f), Triple(-1f, 4f, 24f), Triple(1f, 4f, 24f)).forEach { (s, dy, g) ->
                val l = if (dy < 0) 54f else 50f
                val base = Offset(c.x + s * 3f * k, c.y + dy * k)
                val a = Math.toRadians(g.toDouble()).toFloat()
                val punta = Offset(base.x + s * l * k * cos(a), base.y + l * k * sin(a))
                drawPath(TrazosNouveau.petalo(base, punta, (if (dy < 0) 15f else 17f) * k), color, style = trazo)
            }
            drawLine(color, Offset(c.x, c.y - 8 * k), Offset(c.x, c.y + 72 * k), maxOf(0.9f * u, 5f * k), StrokeCap.Round)
            drawCircle(color, 6.5f * k, Offset(c.x, c.y - 12 * k), style = trazo)
        }
    }

    /**
     * La campanilla de cristal colgada de su lazo de seda: la copa de vidrio que se abre abajo en su labio, el brillo, el
     * badajo de latón que asoma y el lazo malva. [arriba] es el lazo; [alto], hasta el labio.
     */
    private fun DrawScope.campanilla(arriba: Offset, alto: Float, oscuro: Boolean) {
        val k = alto / 20f
        fun p(x: Float, y: Float) = Offset(arriba.x + x * k, arriba.y + y * k)
        val lazo = n(MALVA, oscuro, 0.45f)
        // El lazo de seda: las dos lazadas y las dos puntas que caen.
        for (s in listOf(-1f, 1f)) {
            drawPath(TrazosNouveau.petalo(p(0f, 0.6f), p(3.2f * s, -0.8f), 2.6f * k), lazo)
            drawPath(TrazosNouveau.hojaLanza(p(0f, 0.8f), p(2.2f * s, 4.4f), 1.2f * k, 0.1f * s), mezcla(lazo, Color.Black, 0.2f))
        }
        drawCircle(mezcla(lazo, Color.Black, 0.25f), 0.9f * k, p(0f, 0.6f))
        // La copa de cristal.
        val copa = Path().apply {
            moveTo(p(-1.5f, 2.6f).x, p(-1.5f, 2.6f).y)
            cubicTo(p(-4.4f, 3f).x, p(-4.4f, 3f).y, p(-4.6f, 9f).x, p(-4.6f, 9f).y, p(-5f, 13f).x, p(-5f, 13f).y)
            quadraticTo(p(-5.6f, 16f).x, p(-5.6f, 16f).y, p(-7.6f, 17.4f).x, p(-7.6f, 17.4f).y)
            quadraticTo(p(0f, 18.6f).x, p(0f, 18.6f).y, p(7.6f, 17.4f).x, p(7.6f, 17.4f).y)
            quadraticTo(p(5.6f, 16f).x, p(5.6f, 16f).y, p(5f, 13f).x, p(5f, 13f).y)
            cubicTo(p(4.6f, 9f).x, p(4.6f, 9f).y, p(4.4f, 3f).x, p(4.4f, 3f).y, p(1.5f, 2.6f).x, p(1.5f, 2.6f).y)
            close()
        }
        // El badajo, que se ve a través del vidrio.
        val laton = if (oscuro) mezcla(LATON, NOCHE, 0.4f) else LATON
        drawLine(mezcla(laton, Color.Black, 0.3f), p(0f, 3f), p(0.4f, 16.4f), 0.5f * k)
        drawCircle(laton, 1.5f * k, p(0.4f, 17.6f))
        val vidrio = if (oscuro) mezcla(mezcla(NACAR, VIDRIO_VIOLETA, 0.4f), NOCHE, 0.55f) else mezcla(NACAR, VIDRIO_VIOLETA, 0.32f)
        drawPath(copa, Brush.horizontalGradient(listOf(mezcla(vidrio, Color.White, 0.4f).copy(alpha = 0.8f), vidrio.copy(alpha = 0.55f), mezcla(vidrio, VIDRIO_VIOLETA, 0.4f).copy(alpha = 0.8f)),
            p(-6f, 0f).x, p(6f, 0f).x))
        drawPath(copa, mezcla(vidrio, if (oscuro) Color.Black else VIDRIO_VIOLETA, 0.45f), style = Stroke(0.45f * k))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.75f), p(-2.6f, 5f), p(-3.4f, 13f), 0.8f * k, StrokeCap.Round)
        drawOval(mezcla(vidrio, Color.Black, 0.3f).copy(alpha = 0.6f), p(-7.2f, 16.6f), Size(14.4f * k, 2f * k), style = Stroke(0.4f * k))
    }

    /**
     * Un jarrón de vidrio de Nancy, vacío: la panza baja, el cuello largo y la boca que se abre, el vidrio que pasa del ámbar de
     * abajo al verde y al violeta de la boca, el iris grabado al ácido en un tono más hondo, el reflejo y la boca oscura.
     * [pie] es el centro del pie; [alto], hasta la boca.
     */
    private fun DrawScope.jarron(pie: Offset, alto: Float, oscuro: Boolean) {
        val k = alto / 100f
        fun p(x: Float, y: Float) = Offset(pie.x + x * k, pie.y - y * k)
        val forma = Path().apply {
            moveTo(p(-16f, 0f).x, p(-16f, 0f).y)
            cubicTo(p(-30f, 4f).x, p(-30f, 4f).y, p(-38f, 26f).x, p(-38f, 26f).y, p(-28f, 44f).x, p(-28f, 44f).y)
            cubicTo(p(-20f, 58f).x, p(-20f, 58f).y, p(-9f, 64f).x, p(-9f, 64f).y, p(-8f, 80f).x, p(-8f, 80f).y)
            cubicTo(p(-8f, 90f).x, p(-8f, 90f).y, p(-12f, 95f).x, p(-12f, 95f).y, p(-16f, 100f).x, p(-16f, 100f).y)
            lineTo(p(16f, 100f).x, p(16f, 100f).y)
            cubicTo(p(12f, 95f).x, p(12f, 95f).y, p(8f, 90f).x, p(8f, 90f).y, p(8f, 80f).x, p(8f, 80f).y)
            cubicTo(p(9f, 64f).x, p(9f, 64f).y, p(20f, 58f).x, p(20f, 58f).y, p(28f, 44f).x, p(28f, 44f).y)
            cubicTo(p(38f, 26f).x, p(38f, 26f).y, p(30f, 4f).x, p(30f, 4f).y, p(16f, 0f).x, p(16f, 0f).y)
            close()
        }
        fun v(c: Color) = if (oscuro) mezcla(c, NOCHE, 0.6f) else c
        drawOval(sombra(oscuro), Offset(pie.x - 26 * k, pie.y - 3 * k), Size(52 * k, 6 * k))
        drawPath(forma, Brush.verticalGradient(listOf(v(mezcla(VIDRIO_VIOLETA, Color.White, 0.1f)), v(VIDRIO_VIOLETA), v(VIDRIO_VERDE), v(VIDRIO_AMBAR), v(mezcla(VIDRIO_AMBAR, Color.Black, 0.15f))),
            pie.y - 100 * k, pie.y))
        clipPath(forma) {
            // La sombra del lado de allá (la luz viene de arriba a la izquierda).
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.28f)), pie.x - 30 * k, pie.x + 36 * k),
                Offset(pie.x - 40 * k, pie.y - 100 * k), Size(80 * k, 100 * k))
            // El iris grabado al ácido: dos hojas de espada y la flor, en el tono hondo del vidrio, con su filo de luz.
            val grabado = (if (oscuro) mezcla(VIDRIO_VIOLETA, NOCHE, 0.4f) else mezcla(VIDRIO_VIOLETA, Color.Black, 0.3f)).copy(alpha = 0.7f)
            val filo = Color.White.copy(alpha = if (oscuro) 0.08f else 0.25f)
            with(TrazosNouveau) {
                listOf(Triple(p(-6f, 2f), p(-18f, 46f), 0.12f), Triple(p(-2f, 2f), p(10f, 40f), -0.1f), Triple(p(-4f, 2f), p(-3f, 52f), 0.04f)).forEach { (b, t, cb) ->
                    drawPath(hojaLanza(Offset(b.x - 0.6f * k, b.y - 0.6f * k), Offset(t.x - 0.6f * k, t.y - 0.6f * k), 7 * k, cb, espada = true), filo)
                    drawPath(hojaLanza(b, t, 7 * k, cb, espada = true), grabado)
                }
                val fc = p(-3f, 58f)
                listOf(-0.5f, 0.5f, 0f).forEach { a -> drawPath(petalo(fc, Offset(fc.x + sin(a) * 12 * k, fc.y - cos(a) * 12 * k), 9 * k), grabado) }
                listOf(-1.1f, 1.1f).forEach { a -> drawPath(petalo(fc, Offset(fc.x + sin(a) * 12 * k, fc.y + 5 * k), 8 * k), grabado) }
            }
        }
        // El reflejo largo del lado de la luz y la boca, oscura y vacía.
        drawPath(TrazosNouveau.cinta(TrazosNouveau.bezier(p(-24f, 18f), p(-30f, 30f), p(-24f, 42f), p(-14f, 54f), 16)) { t -> 3.4f * k * sin(PI.toFloat() * t) },
            Color.White.copy(alpha = if (oscuro) 0.14f else 0.5f))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f), p(-5.4f, 66f), p(-5.6f, 86f), 1.6f * k, StrokeCap.Round)
        drawOval(v(mezcla(VIDRIO_VIOLETA, Color.White, 0.2f)), Offset(pie.x - 16 * k, pie.y - 102 * k), Size(32 * k, 5 * k))
        drawOval(Color(0xFF15101C).copy(alpha = 0.9f), Offset(pie.x - 12.5f * k, pie.y - 101.2f * k), Size(25 * k, 3.4f * k))
    }

    /**
     * Los tejados de París de noche, en silueta, de [x0] a [x1] sobre la línea [suelo]: las casas de mansarda de alto
     * desparejo, con sus buhardillas y sus chimeneas de cañones, y algunas ventanas encendidas ([encendidas], 0 a 1).
     */
    private fun DrawScope.tejados(x0: Float, x1: Float, suelo: Float, alto: Float, semilla: Int, color: Color, encendidas: Float) {
        val u = density
        val siluetas = Path()
        val ventanas = ArrayList<Rect>()
        var x = x0; var k = 0
        while (x < x1 && k < 60) {
            val ancho = (16f + 16f * azar(semilla, k)) * u
            val muro = alto * (0.42f + 0.3f * azar(semilla, k + 20))
            val mansarda = alto * (0.22f + 0.12f * azar(semilla, k + 40))
            val r = minOf(x + ancho, x1)
            val techo = suelo - muro
            siluetas.moveTo(x, suelo); siluetas.lineTo(x, techo)
            siluetas.lineTo(x + 2.4f * u, techo - mansarda); siluetas.lineTo(r - 2.4f * u, techo - mansarda); siluetas.lineTo(r, techo); siluetas.lineTo(r, suelo); siluetas.close()
            // Las buhardillas de la mansarda y la chimenea con sus cañones.
            var bx = x + 5 * u
            while (bx < r - 6 * u) {
                siluetas.addRect(Rect(bx, techo - mansarda * 0.75f, bx + 2.4f * u, techo))
                siluetas.moveTo(bx - 0.4f * u, techo - mansarda * 0.75f); siluetas.lineTo(bx + 1.2f * u, techo - mansarda * 0.75f - 1.4f * u); siluetas.lineTo(bx + 2.8f * u, techo - mansarda * 0.75f); siluetas.close()
                bx += 6.5f * u
            }
            if (azar(semilla, k + 60) > 0.35f) {
                val cx = x + ancho * (0.2f + 0.6f * azar(semilla, k + 80))
                val ch = Rect(cx, techo - mansarda - 4.4f * u, cx + 3.6f * u, techo - mansarda + 0.5f * u)
                siluetas.addRect(ch)
                for (j in 0..2) siluetas.addRect(Rect(ch.left + 0.2f * u + j * 1.15f * u, ch.top - 1.2f * u, ch.left + 0.9f * u + j * 1.15f * u, ch.top))
            }
            // Las ventanas de los pisos, en hileras.
            val pisos = (muro / (4.2f * u)).toInt().coerceIn(1, 6)
            for (pi in 0 until pisos) {
                var wx = x + 2.6f * u
                while (wx < r - 3.6f * u) {
                    if (azar(semilla + pi * 31, (wx / u).toInt()) < 0.13f + 0.12f * encendidas) ventanas.add(Rect(wx, techo + 1.6f * u + pi * 4.2f * u, wx + 1.4f * u, techo + 3.8f * u + pi * 4.2f * u))
                    wx += 3.4f * u
                }
            }
            x = r; k++
        }
        drawPath(siluetas, color)
        ventanas.forEach { r ->
            drawRect(mezcla(LUZ, Color(0xFFB0803A), 0.35f).copy(alpha = 0.55f + 0.35f * encendidas), r.topLeft, r.size)
        }
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 17.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9A958B) else Color(0xFF6A6170)
    override fun rotuloCampo(oscuro: Boolean): Color = verde(oscuro)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF8B7E) else Color(0xFFA1302A)

    /**
     * La cartela del cartel: el papel crema donde va la letra, con sus fibras, en su marco con las esquinas del tema: la faja
     * de fundición verde de Guimard, con la luz en el canto, y por dentro el filete de oro pálido. El renglón es una raya de
     * pluma que remata en un rizo (uno por renglón si son varios). Con el foco, la faja se aviva, la lámpara alumbra la esquina
     * de arriba (de noche, la única luz cálida) y el portaplumas se posa en el marco; con un error, el marco y el renglón se
     * vuelven rojos; apagada, todo se va al gris.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 20 * u) return
        val gris = if (oscuro) Color(0xFF23272B) else Color(0xFFCBC5B8)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, gris, 0.55f)
        val caja = Rect(0.6f * u, 0.6f * u, w - 0.6f * u, h - 1.6f * u)
        val g = minOf(12 * u, caja.height * 0.4f); val c = 2 * u
        val forma = hojaNou(caja, g, c)
        val foco = e.enfocado && e.habilitado
        translate(0.5f * u, 1.1f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, a(if (foco && !oscuro) mezcla(papel(false), Color.White, 0.35f) else papel(oscuro)))
        clipPath(forma) {
            fibras(caja, e.semilla + 3, oscuro, if (e.habilitado) 1f else 0.5f)
            // La luz que entra de arriba (de día) y, con el foco, la de la lámpara encendida sobre la esquina.
            if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent), 0f, h * 0.6f))
            if (foco) {
                val l = Offset(w - 30 * u, -6 * u)
                val r = maxOf(w * 0.45f, 80 * u)
                drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = if (oscuro) 0.2f else 0.32f), LUZ.copy(alpha = if (oscuro) 0.05f else 0.08f), Color.Transparent), l, r), r, l)
            }
        }
        // El marco: la faja de fundición verde, con la luz en su canto de arriba, y el filete de oro por dentro.
        val hierro = a(if (e.error) errorCampo(oscuro) else if (foco) (if (oscuro) mezcla(HIERRO, CAFE, 0.3f) else HIERRO)
            else (if (oscuro) mezcla(HIERRO, NOCHE, 0.35f) else mezcla(HIERRO, PAPEL, 0.3f)))
        val oro = a(if (e.error) errorCampo(oscuro).copy(alpha = 0.6f) else if (foco) (if (oscuro) mezcla(ORO, LUZ, 0.3f) else mezcla(ORO, Color(0xFF8A6A22), 0.15f)) else oroLinea(oscuro))
        val faja = (if (foco || e.error) 3f else 2.6f) * u
        val dentro = caja.deflate(faja)
        val gd = maxOf(g - faja, 2 * u)
        val formaDentro = hojaNou(dentro, gd, 0.8f * u)
        val marco = Path().apply { fillType = PathFillType.EvenOdd; addPath(forma); addPath(formaDentro) }
        drawPath(marco, Brush.verticalGradient(listOf(mezcla(hierro, Color.White, if (oscuro) 0.08f else 0.22f), hierro, mezcla(hierro, Color.Black, if (oscuro) 0.3f else 0.18f)), 0f, h))
        drawPath(forma, mezcla(hierro, Color.Black, 0.35f).copy(alpha = 0.7f), style = Stroke(0.5f * u))
        drawPath(formaDentro, oro, style = Stroke(0.7f * u))
        drawLine(filoLuz(oscuro), Offset(caja.left + g, caja.top + 0.6f * u), Offset(caja.right - 4 * u, caja.top + 0.6f * u), 0.6f * u)
        // El renglón, de pluma, que remata en un rizo.
        val izq = margenCampoX.toPx() - 3 * u; val der = w - margenCampoX.toPx() + 3 * u
        val raya = if (e.error) errorCampo(oscuro).copy(alpha = 0.8f)
        else a((if (foco) verde(oscuro) else mezcla(verde(oscuro), papel(oscuro), if (oscuro) 0.45f else 0.35f)).copy(alpha = 0.85f))
        fun renglon(y: Float) {
            if (der - izq < 30 * u) return
            val pts = latigoNou(Offset(izq, y), Offset(der - 4 * u, y - 0.6f * u), 1.5f * u, false, 0.004f, 1f)
            drawPath(plumaNou(pts, (if (foco) 1.05f else 0.85f) * u, 0.35f, 0.35f, 0.7f), raya)
        }
        if (e.multilinea) renglonesNou(margenCampoY) { _, y -> renglon(y) } else renglon(h - 10.5f * u)
        // Con el foco, el portaplumas posado en el filete de arriba, la plumilla hacia la izquierda.
        if (foco && w > 120 * u) portaplumas(w - 84 * u, w - 22 * u, 1.6f * u, oscuro)
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * La línea de látigo de los carteles: dos trazos de pluma que nacen finísimos en las orillas, engordan hacia el centro y,
     * al llegar, se enroscan cada uno en su rizo, a los lados de un capullo de malva con su cáliz verde. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2 + 0.6f * u
        val f = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val linea = (if (oscuro) Color(0xFF6E8676) else mezcla(HIERRO, TALLO_SOMBRA, 0.35f)).copy(alpha = 0.85f * f)
        if (cx - 11 * u > 10 * u) for (s in listOf(-1f, 1f)) {
            val orilla = Offset(if (s < 0) 3 * u else w - 3 * u, y)
            val fin = Offset(cx + s * 8.6f * u, y - 0.4f * u)
            val pts = latigoNou(orilla, fin, 2.5f * u, s > 0f, 0.012f, 1.1f)
            drawPath(plumaNou(pts, 1.25f * u, 0.12f, 0.35f, 0.72f), linea)
        }
        // El capullo de malva con su cáliz, de pie entre los dos rizos.
        val flor = (if (oscuro) mezcla(MALVA, NOCHE, 0.42f) else MALVA).copy(alpha = f)
        val caliz = (if (oscuro) mezcla(TALLO_SOMBRA, NOCHE, 0.35f) else TALLO_SOMBRA).copy(alpha = f)
        drawLine(caliz, Offset(cx, y + 4.4f * u), Offset(cx, y + 1.4f * u), 0.7f * u, StrokeCap.Round)
        capullo(Offset(cx, y + 1.8f * u), Offset(cx, y - 4.6f * u), 4.2f * u, flor, caliz)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = violeta(oscuro)

    /**
     * El subrayado de pluma bajo la palabra: nace en una hojita a la izquierda, corre y remata a la derecha en un rizo que sube.
     * Al presionarlo se enciende detrás de la palabra la luz de la lámpara y la raya se vuelve de oro; apagado, queda solo una
     * raya de puntos, como un trazo de lápiz.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = minOf(12 * u, w * 0.18f); val der = w - izq
        val y = h / 2 + 11 * u
        if (der - izq < 10 * u) return
        if (!e.habilitado) {
            drawLine(tenue(oscuro).copy(alpha = 0.4f), Offset(izq, y), Offset(der, y), 0.8f * u, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(0.5f * u, 2.2f * u)))
            return
        }
        if (e.presionado && h > 20 * u) {
            val c = Offset(w / 2, h * 0.5f)
            val r = maxOf(w * 0.55f, 26 * u)
            drawOval(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = if (oscuro) 0.26f else 0.6f), LUZ.copy(alpha = if (oscuro) 0.07f else 0.12f), Color.Transparent), c, r),
                Offset(c.x - r, c.y - h * 0.46f), Size(2 * r, h * 0.92f))
        }
        val color = if (e.presionado) (if (oscuro) LUZ else mezcla(ORO, Color(0xFF7A5810), 0.45f)) else tintaBotonTexto(oscuro).copy(alpha = 0.6f)
        if (der - izq < 44 * u) {
            drawPath(plumaNou(TrazosNouveau.bezier(Offset(izq, y), Offset(izq + (der - izq) * 0.35f, y - 0.8f * u), Offset(izq + (der - izq) * 0.65f, y + 0.8f * u), Offset(der, y), 20),
                1f * u, 0.2f, 0.2f, 0.5f), color)
            return
        }
        val pts = latigoNou(Offset(izq + 6 * u, y), Offset(der - 4 * u, y - 1.2f * u), 2.2f * u, false, 0.012f, 1.05f)
        drawPath(plumaNou(pts, (if (e.presionado) 1.4f else 1.1f) * u, 0.3f, 0.3f, 0.6f), color)
        // La hojita de donde nace la raya.
        drawPath(TrazosNouveau.hojaLanza(Offset(izq + 6.6f * u, y), Offset(izq - 0.4f * u, y - 2.6f * u), 2.6f * u, -0.18f), color)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 20.dp

    /**
     * Una línea del taller: el filete fino al pie, que nace en un rizo y se pierde a la derecha, y, a la izquierda, un cabujón
     * de vidrio de Nancy en su engaste de oro (con un zarcillo y una hojita, si la fila lleva detalle). Al tocarla, el vidrio
     * se enciende desde dentro y su luz llega a la fila; de noche, sin ella, el vidrio se queda a oscuras.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        if (e.presionado) {
            drawRect(Brush.horizontalGradient(listOf(LUZ_HALO.copy(alpha = if (oscuro) 0.07f else 0.2f), Color.Transparent), 0f, w))
            drawRect(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = if (oscuro) 0.2f else 0.35f), Color.Transparent), Offset(9 * u, cy), 90 * u))
        }
        // El filete del pie.
        val yl = h - 1.2f * u
        val x0 = margenFila.toPx()
        if (w - x0 > 30 * u) {
            val l = (if (oscuro) Color(0xFF55685C) else mezcla(HIERRO, PAPEL, 0.45f))
            drawLine(Brush.horizontalGradient(0f to l, 0.75f to l, 1f to l.copy(alpha = 0f), startX = x0 + 3 * u, endX = w - 2 * u), Offset(x0 + 3 * u, yl), Offset(w - 2 * u, yl), 0.7f * u)
            drawPath(plumaNou(TrazosNouveau.puntosRizo(Offset(x0 + 3.2f * u, yl), Offset(-1f, 0f), 1.5f * u, true, 1.05f, 18), 0.8f * u, 1f, 0.35f, 0.05f), l)
        }
        if (h < 16 * u) return
        val luz = if (e.presionado) 1f else 0f
        if (e.conDetalle && h >= 30 * u) {
            // El zarcillo que baja de arriba y la hojita al pie del cabujón.
            val zar = if (oscuro) mezcla(TALLO_SOMBRA, NOCHE, 0.4f) else TALLO_SOMBRA
            val c = Offset(9.4f * u, cy + 1.6f * u)
            drawPath(plumaNou(latigoNou(Offset(9.4f * u, c.y - 6.4f * u), Offset(13 * u, c.y - 13.6f * u), 1.9f * u, true, 0.1f), 0.8f * u, 1f, 0.3f, 0.1f), zar)
            TrazosNouveau.run { hojaConNervio(Offset(9.4f * u, c.y + 6.6f * u), Offset(15.4f * u, c.y + 9.6f * u), 3 * u, -0.15f, n(TALLO, oscuro, 0.4f), zar) }
            cabujon(c, 4 * u, 5.4f * u, oscuro, luz)
        } else cabujon(Offset(9.4f * u, cy), 3.6f * u, 4.8f * u, oscuro, luz)
    }

    /** La flecha: un tallo que corre a la derecha desde un rizo y remata en una hoja en punta, con su nervio. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val s = size.minDimension / 20f
        val c = if (oscuro) Color(0xFF8FA595) else mezcla(HIERRO, tenue(false), 0.25f)
        val luz = if (oscuro) mezcla(c, NOCHE, 0.25f) else mezcla(TALLO, PAPEL, 0.1f)
        val y = size.height / 2
        val x0 = size.width / 2 - 10 * s
        drawPath(plumaNou(TrazosNouveau.puntosRizo(Offset(x0 + 6 * s, y), Offset(-1f, 0.15f), 2.2f * s, true, 1.1f, 22), 1.5f * s, 1f, 0.35f, 0.05f), c)
        drawLine(c, Offset(x0 + 6 * s, y), Offset(x0 + 11 * s, y), 1.4f * s, StrokeCap.Round)
        TrazosNouveau.run { hojaConNervio(Offset(x0 + 10 * s, y), Offset(x0 + 18.8f * s, y), 5.2f * s, 0.06f, luz, c) }
    }

    /**
     * La libélula de joyero: fija, la joya terminada, de nácar y oro con su cuerpo verde azul (como las de Lalique); sin fijar,
     * el boceto a pluma de la misma joya, sin color.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val s = size.minDimension / 24f
        val ang = -40f
        val c = Offset(size.width / 2 - 3.5f * s, size.height / 2 - 4.2f * s)
        if (fija) {
            val cuerpo = if (oscuro) mezcla(LIBELULA, CAFE, 0.25f) else LIBELULA
            val ala = if (oscuro) mezcla(NACAR, NOCHE, 0.25f) else NACAR
            val vena = if (oscuro) mezcla(ORO, LUZ, 0.3f) else mezcla(ORO, Color(0xFF8A6A22), 0.3f)
            TrazosNouveau.run { libelula(c, 19 * s, ang, 0f, cuerpo, ala, vena, mezcla(vena, Color.Black, 0.35f)) }
        } else libelulaBoceto(c, 19 * s, ang, tenue(oscuro).copy(alpha = 0.6f))
    }

    override val tamDibujoVacio: Dp = 48.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) tenue(true) else Color(0xFF4E4754)

    /**
     * La piedra litográfica recién graneada, esperando el dibujo: la cara de caliza con su grano, el canto de la piedra abajo y
     * a la derecha, y apenas, como un fantasma, lo que quedó del cartel que se borró (una línea de látigo y un tallo), con las
     * cruces de registro en dos esquinas. De noche, la misma piedra a oscuras.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 40 * u) return
        val canto = 3.2f * u
        val cara = Rect(0f, 0f, w - canto * 0.6f, h - canto)
        val piedra = if (oscuro) Color(0xFF1D2124) else CARA_PIEDRA
        // El canto de la piedra, más oscuro, y su sombra.
        drawRect(sombra(oscuro), Offset(1.2f * u, 2 * u), Size(w - 1.2f * u, h - 2 * u))
        drawPath(Path().apply {
            moveTo(0f, cara.bottom); lineTo(cara.right, cara.bottom); lineTo(cara.right, 0f); lineTo(w, canto * 0.5f); lineTo(w, h); lineTo(canto * 0.6f, h); close()
        }, Brush.verticalGradient(listOf(mezcla(piedra, Color.Black, if (oscuro) 0.25f else 0.12f), mezcla(piedra, Color.Black, if (oscuro) 0.45f else 0.28f)), h - canto, h))
        drawRect(piedra, cara.topLeft, cara.size)
        clipRect(cara.left, cara.top, cara.right, cara.bottom) {
            grano(cara, e.semilla, oscuro, 0.7f)
            if (!oscuro) drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent, Color.Black.copy(alpha = 0.05f)), Offset.Zero, Offset(w, h)))
            // El fantasma del cartel borrado: una línea de látigo larga y un tallo con su hoja.
            val fantasma = (if (oscuro) Color.Black.copy(alpha = 0.17f) else Color(0xFF6E6656).copy(alpha = 0.08f))
            val l = latigoNou(Offset(w * 0.05f, h * 0.85f), Offset(w * 0.86f, h * 0.24f), h * 0.12f, true, 0.18f, 1.2f)
            drawPath(plumaNou(l, 5 * u, 0.15f, 0.3f, 0.5f), fantasma)
            TrazosNouveau.run {
                drawPath(hojaLanza(Offset(w * 0.18f, h), Offset(w * 0.26f, h * 0.35f), 7 * u, 0.15f, espada = true), fantasma)
                drawPath(hojaLanza(Offset(w * 0.2f, h), Offset(w * 0.12f, h * 0.5f), 6 * u, -0.1f, espada = true), fantasma)
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.06f), Color.Transparent), 0f, 6 * u), Offset.Zero, Size(w, 6 * u))
        }
        drawLine(filoLuz(oscuro), Offset(0f, 0.5f * u), Offset(cara.right, 0.5f * u), 0.8f * u)
        drawLine(filoLuz(oscuro), Offset(0.5f * u, 0f), Offset(0.5f * u, cara.bottom), 0.6f * u)
        // Las cruces de registro, arriba a la izquierda y abajo a la derecha.
        val marca = if (oscuro) Color(0xFF6B7278).copy(alpha = 0.5f) else Color(0xFF5E5662).copy(alpha = 0.4f)
        registro(Offset(9 * u, 9 * u), 2.2f * u, marca)
        registro(Offset(cara.right - 9 * u, cara.bottom - 9 * u), 2.2f * u, marca)
    }

    /**
     * Lo vacío: un jarrón de vidrio de Nancy sin flores, del ámbar al violeta, con su iris grabado al ácido; al pie, un pétalo
     * de malva que se cayó. De día, con la luz de la ventana a la izquierda; de noche, el vidrio a oscuras.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val s = size.minDimension
        val cx = size.width / 2
        jarron(Offset(cx - s * 0.06f, s * 0.95f), s * 0.88f, oscuro)
        // El pétalo caído, acostado junto al pie.
        val flor = n(MALVA, oscuro, 0.5f)
        val base = Offset(cx + s * 0.24f, s * 0.95f)
        drawOval(sombra(oscuro), Offset(base.x - s * 0.02f, base.y - s * 0.015f), Size(s * 0.2f, s * 0.035f))
        drawPath(TrazosNouveau.petalo(base, Offset(base.x + s * 0.17f, base.y - s * 0.035f), s * 0.07f), mezcla(flor, Color.Black, 0.15f))
        drawPath(TrazosNouveau.petalo(Offset(base.x + s * 0.01f, base.y - s * 0.004f), Offset(base.x + s * 0.15f, base.y - s * 0.04f), s * 0.035f), mezcla(flor, Color.White, 0.15f))
    }

    override val margenAviso: Dp = 36.dp
    override fun tintaAviso(oscuro: Boolean): Color = tinta(oscuro)

    /**
     * El aviso es el papel crema de un cartelito, con su filete, junto a una pilastra de fundición verde de Guimard, moldurada.
     * En la pilastra, según el aviso, el candelabro del metro con su farol de vidrio (encendido de noche, y su luz cálida se
     * derrama solo sobre lo que tiene cerca) o la campanilla de cristal colgada de un brazo de látigo, con su tintineo.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 20 * u) return
        val forma = hojaNou(Rect(0f, 0f, w, h), 10 * u, 1.5f * u)
        val farol = e.semilla % 2 == 0
        val banda = 27 * u
        val luz = if (farol && oscuro) 1f else 0f
        drawPath(forma, papel(oscuro))
        clipPath(forma) {
            fibras(Rect(banda, 0f, w, h), e.semilla + 5, oscuro, 0.8f)
            if (luz > 0f) {
                val c = Offset(banda * 0.85f, minOf(h * 0.32f, 16 * u))
                drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = 0.22f), mezcla(LUZ, Color(0xFFF2B55A), 0.5f).copy(alpha = 0.06f), Color.Transparent), c, 90 * u), 90 * u, c)
            }
            // La pilastra de fundición: el verde con su moldura, la luz en el canto de la izquierda y la sombra sobre el papel.
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.14f), Color.Transparent), banda, banda + 5 * u), Offset(banda, 0f), Size(5 * u, h))
            val fundicion = if (luz > 0f) alLuz(HIERRO, true, 0.5f) else n(HIERRO, oscuro, 0.62f)
            drawRect(Brush.horizontalGradient(listOf(mezcla(fundicion, Color.White, if (oscuro) 0.06f else 0.2f), fundicion, mezcla(fundicion, Color.Black, 0.3f)), 0f, banda), Offset.Zero, Size(banda, h))
            for (x in listOf(4.2f * u, banda - 4.2f * u)) {
                drawLine(mezcla(fundicion, Color.Black, 0.4f), Offset(x, 0f), Offset(x, h), 0.8f * u)
                drawLine(mezcla(fundicion, Color.White, if (oscuro) 0.05f else 0.25f), Offset(x + 0.8f * u, 0f), Offset(x + 0.8f * u, h), 0.5f * u)
            }
            drawLine(mezcla(fundicion, Color.Black, 0.5f), Offset(banda, 0f), Offset(banda, h), 0.8f * u)
            if (farol) {
                // El candelabro del metro: sube por la pilastra y se dobla hacia el papel con su farol.
                val alto = minOf(h - 6 * u, 46 * u)
                TrazosNouveau.run { candelabro(Offset(10 * u, h - 2 * u), alto, 1f, if (oscuro) mezcla(HIERRO, NOCHE, 0.2f) else mezcla(HIERRO, Color.Black, 0.1f), luz) }
            } else {
                // El brazo de látigo y la campanilla que cuelga de él, con su tintineo.
                val ojo = Offset(16.5f * u, minOf(9 * u, h * 0.2f))
                val brazo = if (oscuro) mezcla(HIERRO, NOCHE, 0.15f) else mezcla(HIERRO, Color.Black, 0.15f)
                drawPath(plumaNou(latigoNou(Offset(3.6f * u, ojo.y + 3 * u), Offset(ojo.x + 3 * u, ojo.y), 2.4f * u, true, 0.15f, 1.05f), 1.6f * u, 0.8f, 0.3f, 0.3f), brazo)
                val alto = minOf(h - ojo.y - 9 * u, 25 * u)
                if (alto > 8 * u) {
                    campanilla(Offset(ojo.x + 3 * u, ojo.y + 2.4f * u), alto, oscuro)
                    val boca = Offset(ojo.x + 3 * u, ojo.y + 2.4f * u + alto * 0.9f)
                    for (j in 1..3) {
                        val r = (2.4f + 2.4f * j) * u
                        drawArc(tenue(oscuro).copy(alpha = 0.55f - j * 0.13f), -60f, 60f, false, Offset(boca.x - r, boca.y - r), Size(2 * r, 2 * r), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                    }
                }
            }
        }
        // El filete del cartelito, por dentro del papel.
        val dentro = Rect(banda + 4 * u, 3.4f * u, w - 3.4f * u, h - 3.4f * u)
        if (dentro.width > 10 * u && dentro.height > 10 * u) drawPath(hojaNou(dentro, 7 * u, 0.8f * u), oroLinea(oscuro).copy(alpha = 0.75f), style = Stroke(0.6f * u))
        drawPath(forma, (if (oscuro) Color(0xFF3E4548) else Color(0xFFB3A586)).copy(alpha = 0.9f), style = Stroke(0.8f * u))
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = tinta(oscuro)
    override fun rotuloCifra(oscuro: Boolean): Color = oroTinta(oscuro)

    /**
     * La prueba de color: el pliego grueso que el impresor saca para ver cada piedra, con las esquinas del tema; al pie, la
     * tira de pastillas de las tintas (el salvia, el violeta y el oro, una por piedra) y, arriba a la derecha, la cruz de registro.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 20 * u || h < 20 * u) return
        val r = Rect(0f, 0f, w - 0.8f * u, h - 1.4f * u)
        val forma = hojaNou(r, 8 * u, 1.2f * u)
        val cara = papel(oscuro)
        translate(0.8f * u, 1.4f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, cara)
        clipPath(forma) {
            fibras(r, e.semilla, oscuro, 0.9f)
            if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), 0f, h * 0.5f))
        }
        drawPath(forma, (if (oscuro) Color(0xFF3E4548) else Color(0xFFB3A586)), style = Stroke(0.7f * u))
        // La tira de las tintas, al pie: cada pastilla con su borde algo comido por la prensa.
        val tintas = if (oscuro) listOf(mezcla(CAFE, NOCHE, 0.35f), mezcla(Color(0xFFBFA6DE), NOCHE, 0.35f), mezcla(LUZ, NOCHE, 0.4f))
        else listOf(mezcla(TALLO, TALLO_SOMBRA, 0.4f), mezcla(IRIS, MALVA, 0.25f), ORO)
        val lado = 4.4f * u
        val y = r.bottom - lado - 2.6f * u
        if (w > 50 * u) tintas.forEachIndexed { k, c ->
            val x = w * 0.5f + k * (lado + 1.6f * u)
            if (x + lado < r.right - 4 * u) {
                drawRect(c.copy(alpha = 0.9f), Offset(x, y), Size(lado, lado))
                drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.25f), Offset(x, y), Size(lado, lado * 0.35f))
            }
        }
        // La cruz de registro, arriba a la derecha.
        registro(Offset(r.right - 7.6f * u, 7.6f * u), 2 * u, (if (oscuro) Color(0xFF8A8F94) else PLUMA).copy(alpha = 0.45f))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 13.dp

    /**
     * El fondo donde va el texto de cada tarjeta: el papel del pliego para la cifra; el papel pintado del taller para lo que
     * falta; el cielo de París de noche (oscuro también de día) para la noche; el vidrio grabado para Vida.
     */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF1B1F23) else Color(0xFFF6F0E2)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF171A1E) else Color(0xFFEDE5D1)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0F131A) else Color(0xFF172030)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF1C1E25) else Color(0xFFEEE7DA)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> tinta(oscuro)
        TipoTarjeta.PENDIENTE -> tinta(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFECE5D6)
        TipoTarjeta.VIDA -> tinta(oscuro)
    }

    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> tenue(oscuro)
        TipoTarjeta.PENDIENTE -> tenue(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFB2ADA4)
        TipoTarjeta.VIDA -> tenue(oscuro)
    }

    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> verde(oscuro)
        TipoTarjeta.PENDIENTE -> violeta(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFE8C46E)
        TipoTarjeta.VIDA -> if (oscuro) verde(true) else oroTinta(false)
    }

    /**
     * Las tarjetas del taller y de la calle. La cifra de Hoy es el **pliego recién impreso**: el papel crema en su marco de dos
     * filetes, con las cruces de registro en las esquinas rectas; destacada, el marco toma su color y florece un iris a la
     * derecha. «Te falta hoy» es la **pared del taller**, con su papel pintado de tallos y, en su repisa, la lámpara de hongo
     * de vidrio de Nancy encendida, porque todavía se trabaja; completa, la lámpara se apaga y en su pantalla se posa una
     * libélula. La noche es **la calle de París**: los tejados de mansarda con sus chimeneas, la llovizna y, abajo a la derecha, la
     * entrada de metro de Guimard con su barandilla y sus dos candelabros; sellada, se encienden los faroles y las ventanas.
     * Vida es el **vidrio de Nancy** grabado al ácido, del violeta al ámbar, con su iris en relieve; destacada, se alumbra por
     * detrás. Al tocarlas se oscurecen.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 24 * u || size.height < 24 * u) return
        val base = fondoTarjeta(e.tipo, oscuro).let { if (e.presionado) mezcla(it, if (oscuro) Color.Black else Color(0xFF6E5E44), 0.12f) else it }
        when (e.tipo) {
            TipoTarjeta.METRICA -> tarjetaPliego(e, base, oscuro)
            TipoTarjeta.PENDIENTE -> tarjetaTaller(e, base, oscuro)
            TipoTarjeta.NOCHE -> tarjetaCalle(e, base)
            TipoTarjeta.VIDA -> tarjetaVidrio(e, base, oscuro)
        }
    }

    /** El pliego recién impreso de la cifra de Hoy (ver [tarjeta]). */
    private fun DrawScope.tarjetaPliego(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(0f, 0f, w - 0.8f * u, h - 1.6f * u)
        val forma = hojaNou(r, 14 * u, 1.6f * u)
        translate(0.8f * u, 1.6f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, base)
        clipPath(forma) {
            fibras(r, e.semilla + 11, oscuro)
            if (!oscuro) drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), Offset.Zero, Offset(w * 0.6f, h)))
            else drawRect(Brush.verticalGradient(listOf(Color(0xFFB8C6D0).copy(alpha = 0.035f), Color.Transparent), 0f, h * 0.5f))
        }
        drawPath(forma, (if (oscuro) Color(0xFF3E4548) else Color(0xFFB3A586)).copy(alpha = 0.8f), style = Stroke(0.6f * u))
        // El marco de dos filetes, con las esquinas del tema.
        val acento = acentoTarjeta(e.tipo, oscuro)
        val fuera = r.deflate(4 * u); val dentro = r.deflate(6.6f * u)
        drawPath(hojaNou(fuera, 11 * u, 0.8f * u), if (e.destacado) acento.copy(alpha = 0.95f) else hierroLinea(oscuro).copy(alpha = if (oscuro) 0.7f else 0.55f),
            style = Stroke((if (e.destacado) 1.4f else 1f) * u))
        drawPath(hojaNou(dentro, 8.4f * u, 0.6f * u), oroLinea(oscuro).copy(alpha = if (oscuro) 0.8f else 0.9f), style = Stroke(0.5f * u))
        // Las cruces de registro en las dos esquinas rectas, por fuera del marco.
        val marca = (if (oscuro) Color(0xFF8A8F94) else PLUMA).copy(alpha = 0.4f)
        registro(Offset(r.right - 2.2f * u, 2.2f * u), 1.3f * u, marca)
        registro(Offset(2.2f * u, r.bottom - 2.2f * u), 1.3f * u, marca)
        if (e.destacado && w > 110 * u && h > 70 * u) {
            // El iris que florece a la derecha, de pie sobre el filete.
            val pie = Offset(fuera.right - 15 * u, minOf(h * 0.64f, fuera.bottom - 22 * u))
            val alto = minOf(38 * u, pie.y - 22 * u)
            if (alto > 14 * u) TrazosNouveau.run {
                iris(pie, alto, n(IRIS, oscuro, 0.4f), n(TALLO, oscuro, 0.5f), n(TALLO_SOMBRA, oscuro, 0.5f), if (oscuro) null else PLUMA.copy(alpha = 0.55f))
            }
        }
    }

    /** La pared del taller con su lámpara, de «Te falta hoy» (ver [tarjeta]). */
    private fun DrawScope.tarjetaTaller(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val forma = hojaNou(Rect(0f, 0f, w, h - 1.2f * u), 14 * u, 2 * u)
        val encendida = !e.destacado
        val pieLampara = if (encendida) Offset(w - 32 * u, minOf(h - 14 * u, 58 * u)) else Offset(w - 30 * u, h - 9 * u)
        val alto = if (encendida) minOf(pieLampara.y - 6 * u, 48 * u) else minOf(pieLampara.y - 5 * u, 34 * u)
        translate(0.6f * u, 1.4f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, base)
        clipPath(forma) {
            // El papel pintado: tallos que suben ondulando, con sus hojas y sus capullos, en dos tonos apenas distintos.
            val paso = 22 * u
            val tallo = if (oscuro) Color(0xFF283031) else mezcla(base, TALLO_SOMBRA, 0.16f)
            val brote = if (oscuro) Color(0xFF302A38) else mezcla(base, MALVA, 0.26f)
            var x = paso * 0.5f; var k = 0
            while (x < w + paso && k < 60) {
                val fase = (k % 2) * 0.5f
                val pts = List(30) { i -> val y = h * i / 29f; Offset(x + 2.6f * u * sin((y / (26 * u) + fase) * PI.toFloat()), y) }
                drawPath(TrazosNouveau.linea(pts), tallo, style = Stroke(0.9f * u))
                var y = (12f + 10f * (k % 3)) * u
                var j = 0
                while (y < h && j < 30) {
                    val q = Offset(x + 2.6f * u * sin((y / (26 * u) + fase) * PI.toFloat()), y)
                    val s = if (j % 2 == 0) 1f else -1f
                    drawPath(TrazosNouveau.hojaLanza(q, Offset(q.x + s * 6 * u, q.y - 4 * u), 2.6f * u, 0.15f * s), tallo)
                    if (j % 3 == 1) drawPath(TrazosNouveau.petalo(Offset(q.x - s * 0.5f * u, q.y), Offset(q.x - s * 2.6f * u, q.y - 5 * u), 3 * u), brote)
                    y += 15 * u; j++
                }
                x += paso; k++
            }
            // Lejos de la lámpara, el dibujo del papel se pierde en la penumbra (de noche, casi del todo).
            val foco = Offset(pieLampara.x, pieLampara.y - alto * 0.62f)
            val lejos = maxOf(w, h) * 0.85f
            drawRect(Brush.radialGradient(listOf(Color.Transparent, base.copy(alpha = if (oscuro) (if (encendida) 0.82f else 0.6f) else 0.45f)), foco, lejos))
            // La luz de la lámpara sobre la pared (de noche, la única luz cálida); apagada, nada.
            if (encendida) {
                val c = Offset(pieLampara.x, pieLampara.y - alto * 0.62f)
                val r = 110 * u
                drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = if (oscuro) 0.3f else 0.42f), LUZ.copy(alpha = if (oscuro) 0.08f else 0.1f), Color.Transparent), c, r), r, c)
            }
            // La moldura del zócalo de arriba.
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), Color.Transparent), 0f, 7 * u), Offset.Zero, Size(w, 7 * u))
        }
        // La repisa de madera, con su ménsula en látigo, y la lámpara de hongo encima.
        val madera = if (encendida) alLuz(Color(0xFF6B4A2E), oscuro, 0.7f) else n(Color(0xFF6B4A2E), oscuro, 0.72f)
        val repisa = Rect(pieLampara.x - 19 * u, pieLampara.y, pieLampara.x + 19 * u, pieLampara.y + 2.6f * u)
        drawRect(sombra(oscuro), Offset(repisa.left + 1 * u, repisa.bottom), Size(repisa.width, 2 * u))
        drawRect(Brush.verticalGradient(listOf(mezcla(madera, Color.White, if (oscuro) 0.1f else 0.25f), madera, mezcla(madera, Color.Black, 0.3f)), repisa.top, repisa.bottom), repisa.topLeft, repisa.size)
        if (repisa.bottom + 8 * u < h) drawPath(plumaNou(latigoNou(Offset(repisa.right - 5 * u, repisa.bottom), Offset(repisa.right - 10 * u, repisa.bottom + 6 * u), 2.2f * u, true, -0.2f), 1.6f * u, 1f, 0.35f, 0.15f),
            mezcla(madera, Color.Black, 0.2f))
        TrazosNouveau.run {
            if (encendida) lampara(pieLampara, alto, 1f)
            else lampara(pieLampara, alto, 0f, n(VIDRIO_AMBAR, oscuro, 0.62f), n(VIDRIO_VIOLETA, oscuro, 0.62f))
        }
        if (!encendida) {
            // La libélula posada en la pantalla apagada.
            TrazosNouveau.run { libelula(Offset(pieLampara.x + 4 * u, pieLampara.y - alto * 0.96f), 10 * u, 74f, 0.85f, n(LIBELULA, oscuro, 0.45f), n(NACAR, oscuro, 0.5f), n(ORO, oscuro, 0.45f)) }
            drawPath(forma, acentoTarjeta(e.tipo, oscuro).copy(alpha = 0.85f), style = Stroke(1.3f * u))
        } else drawPath(forma, (if (oscuro) Color(0xFF3E4548) else Color(0xFFB3A586)).copy(alpha = 0.8f), style = Stroke(0.7f * u))
    }

    /** La calle de París de noche, con la entrada de metro (ver [tarjeta]). */
    private fun DrawScope.tarjetaCalle(e: EstadoTarjeta, base: Color) {
        val u = density
        val w = size.width; val h = size.height
        val sellada = e.destacado
        val forma = hojaNou(Rect(0f, 0f, w, h), 14 * u, 2 * u)
        val horizonte = mezcla(base, Color(0xFF2C3446), 0.5f)
        drawPath(forma, Brush.verticalGradient(listOf(base, base, horizonte), 0f, h))
        clipPath(forma) {
            // El resplandor de la ciudad sobre los tejados y la llovizna, finísima y sesgada.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, mezcla(LUZ, Color(0xFF3A4660), 0.7f).copy(alpha = if (sellada) 0.12f else 0.07f)), h * 0.45f, h))
            val lluvia = Path()
            val gotas = (w * h / (120f * u * u)).toInt().coerceIn(10, 240)
            for (k in 0 until gotas) {
                val x = w * azar(e.semilla + 71, k); val y = h * azar(e.semilla + 72, k)
                val l = (3f + 5f * azar(e.semilla + 73, k)) * u
                lluvia.moveTo(x, y); lluvia.lineTo(x - l * 0.28f, y + l)
            }
            drawPath(lluvia, Color(0xFFB8C6D0).copy(alpha = 0.07f), style = Stroke(0.4f * u, cap = StrokeCap.Round))
            // Los tejados lejanos y, delante, los de la calle.
            val suelo = h
            tejados(-6 * u, w, suelo - 3 * u, 22 * u, e.semilla + 5, mezcla(base, Color(0xFF1E2636), 0.55f), if (sellada) 0.6f else 0f)
            tejados(-14 * u, w * 0.66f, suelo, 17 * u, e.semilla + 9, ZINC_NOCHE, if (sellada) 1f else 0.3f)
            // La entrada de metro de Guimard: la barandilla de fundición entre los dos candelabros con sus faroles.
            val x0 = w * 0.69f; val x1 = w - 8 * u
            val alto = minOf(34 * u, h * 0.36f)
            val hierro = mezcla(TrazosNouveau.HIERRO, base, 0.3f)
            val luz = if (sellada) 1f else 0f
            if (sellada) {
                val c = Offset((x0 + x1) / 2, h - alto * 0.8f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFF2B55A).copy(alpha = 0.18f), Color.Transparent), c, 80 * u), 80 * u, c)
            }
            // La acera y la barandilla de fundición, con sus balaustres en S.
            val alturaB = 7 * u
            drawRect(ZINC_NOCHE, Offset(x0 - 6 * u, h - 2.4f * u), Size(x1 - x0 + 12 * u, 2.4f * u))
            drawLine(hierro, Offset(x0, h - alturaB), Offset(x1, h - alturaB), 1.3f * u, StrokeCap.Round)
            drawLine(hierro, Offset(x0, h - 2.6f * u), Offset(x1, h - 2.6f * u), 0.8f * u)
            val balaustres = Path()
            var x = x0 + 4 * u; var k = 0
            while (x < x1 - 3 * u && k < 60) {
                balaustres.moveTo(x, h - 2.6f * u)
                balaustres.cubicTo(x - 2.2f * u, h - 3.8f * u, x + 2.2f * u, h - 5.8f * u, x, h - alturaB)
                x += 4.6f * u; k++
            }
            drawPath(balaustres, hierro, style = Stroke(0.75f * u, cap = StrokeCap.Round))
            TrazosNouveau.run {
                candelabro(Offset(x0, h - 1 * u), alto, 1f, hierro, luz)
                candelabro(Offset(x1, h - 1 * u), alto, -1f, hierro, luz)
            }
            if (sellada) {
                // El reflejo de los faroles en el adoquín mojado.
                for (xf in listOf(x0 + alto * 0.26f, x1 - alto * 0.26f)) drawRect(Brush.verticalGradient(listOf(Color(0xFFF2B55A).copy(alpha = 0.3f), Color.Transparent), h - 2.4f * u, h),
                    Offset(xf - 1.6f * u, h - 2.4f * u), Size(3.2f * u, 2.4f * u))
            }
        }
        drawPath(forma, if (sellada) Color(0xFFE8C46E).copy(alpha = 0.85f) else Color(0xFF2E3644), style = Stroke((if (sellada) 1.3f else 1f) * u))
    }

    /** El vidrio de Nancy grabado de Vida (ver [tarjeta]). */
    private fun DrawScope.tarjetaVidrio(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(0f, 0f, w - 0.8f * u, h - 1.6f * u)
        val forma = hojaNou(r, 12 * u, 2 * u)
        translate(0.8f * u, 1.6f * u) { drawPath(forma, sombra(oscuro)) }
        val arriba = if (oscuro) mezcla(base, VIDRIO_VIOLETA, 0.22f) else mezcla(base, VIDRIO_VIOLETA, 0.1f)
        val medio = if (oscuro) mezcla(base, VIDRIO_VERDE, 0.1f) else mezcla(base, VIDRIO_VERDE, 0.07f)
        val abajo = if (oscuro) mezcla(base, VIDRIO_AMBAR, 0.16f) else mezcla(base, VIDRIO_AMBAR, 0.2f)
        drawPath(forma, Brush.verticalGradient(listOf(arriba, medio, abajo), 0f, h))
        clipPath(forma) {
            // Lo de dentro del vidrio: las burbujas y las nubes del color que se funde.
            for (k in 0 until 7) {
                val c = Offset(w * azar(e.semilla + 21, k), h * azar(e.semilla + 22, k))
                val rr = (10f + 26f * azar(e.semilla + 23, k)) * u
                val col = listOf(VIDRIO_VIOLETA, VIDRIO_VERDE, VIDRIO_AMBAR)[k % 3]
                drawCircle(Brush.radialGradient(listOf(col.copy(alpha = if (oscuro) 0.1f else 0.08f), Color.Transparent), c, rr), rr, c)
            }
            for (k in 0 until 14) drawCircle(Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), (0.4f + 0.6f * azar(e.semilla + 24, k)) * u,
                Offset(w * azar(e.semilla + 25, k), h * azar(e.semilla + 26, k)))
            // Destacada, el vidrio se alumbra por detrás.
            if (e.destacado) {
                val c = Offset(w * 0.78f, h * 0.9f)
                drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = if (oscuro) 0.28f else 0.5f), LUZ.copy(alpha = 0.08f), Color.Transparent), c, w * 0.7f), w * 0.7f, c)
            }
            // El iris grabado al ácido a la derecha, en relieve: el tono hondo y, corrido arriba, su filo de luz.
            val hondo = (if (oscuro) mezcla(VIDRIO_VIOLETA, base, 0.35f) else mezcla(VIDRIO_VIOLETA, base, 0.55f)).copy(alpha = if (oscuro) 0.5f else 0.32f)
            val filo = Color.White.copy(alpha = if (oscuro) 0.06f else 0.45f)
            val pie = Offset(w - 22 * u, h + 4 * u)
            val alto = minOf(h * 1.05f, 90 * u)
            TrazosNouveau.run {
                for ((capa, d) in listOf(filo to Offset(-0.7f * u, -0.7f * u), hondo to Offset.Zero)) {
                    listOf(Triple(-0.3f, 0.95f, 0.1f), Triple(0.18f, 0.75f, -0.14f), Triple(0.05f, 0.55f, 0.06f)).forEach { (dx, a, cb) ->
                        drawPath(hojaLanza(Offset(pie.x + d.x, pie.y + d.y), Offset(pie.x + dx * alto + d.x, pie.y - a * alto + d.y), alto * 0.09f, cb, espada = true), capa)
                    }
                    val fc = Offset(pie.x + 0.02f * alto + d.x, pie.y - alto * 0.66f + d.y)
                    listOf(-0.45f, 0.45f, 0f).forEach { a -> drawPath(petalo(fc, Offset(fc.x + sin(a) * alto * 0.17f, fc.y - cos(a) * alto * 0.17f), alto * 0.12f), capa) }
                    listOf(-1.15f, 1.15f).forEach { a -> drawPath(petalo(fc, Offset(fc.x + sin(a) * alto * 0.16f, fc.y + alto * 0.07f), alto * 0.11f), capa) }
                }
            }
            // El reflejo del vidrio, una faja sesgada y suave.
            drawPath(Path().apply { moveTo(w * 0.52f, 0f); lineTo(w * 0.6f, 0f); lineTo(w * 0.44f, h); lineTo(w * 0.36f, h); close() },
                Color.White.copy(alpha = if (oscuro) 0.025f else 0.16f))
        }
        // El bisel y el filete de oro del canto.
        val oro = if (e.destacado) acentoTarjeta(e.tipo, oscuro) else oroLinea(oscuro)
        drawPath(forma, oro.copy(alpha = 0.9f), style = Stroke((if (e.destacado) 1.3f else 0.9f) * u))
        drawPath(hojaNou(r.deflate(1.8f * u), 10.4f * u, 1.2f * u), filoLuz(oscuro), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- Gastos

    /** La letra de los rótulos y de las cifras: Della Respira, la de los carteles y las entradas de metro. */
    private val letraRotulos: FontFamily = LetraExtra.dellaRespira
    /** La letra de los títulos: Federant, la de cartel, con sus curvas de pluma. */
    private val letraTitulos: FontFamily = LetraExtra.federant

    override val altoBalanza: Dp = 64.dp

    /**
     * Las dos placas de lava esmaltada de un letrero de Guimard, en su marco de fundición verde con su moldura: a la izquierda
     * el gasto, con un pétalo de malva que se cae; a la derecha el ingreso, con un capullo que brota. En medio, el marco de cada
     * placa se suelta en un rizo y los dos rizos abrazan un cabujón de vidrio de Nancy. La elegida tiene el esmalte crema
     * encendido por la luz de la lámpara, el filete de oro y la letra en su color; la otra queda a la sombra, con el esmalte
     * apagado y la letra tenue.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 36 * u) return
        val xm = w / 2
        val b = 2.8f * u
        val hueco = 9 * u
        for (lado in 0..1) {
            val ingreso = lado == 1
            val elegida = e.ingreso == ingreso
            val r = if (ingreso) Rect(xm + hueco, 2 * u, w - 0.8f * u, h - 3 * u) else Rect(0.8f * u, 2 * u, xm - hueco, h - 3 * u)
            fun placa(q: Rect, grande: Float, chica: Float) = Path().apply {
                addRoundRect(if (ingreso) RoundRect(q, CornerRadius(chica), CornerRadius(chica), CornerRadius(grande), CornerRadius(chica))
                else RoundRect(q, CornerRadius(grande), CornerRadius(chica), CornerRadius(chica), CornerRadius(chica)))
            }
            val forma = placa(r, 14 * u, 2 * u)
            translate(0.6f * u, 1.4f * u) { drawPath(forma, sombra(oscuro)) }
            // La fundición del marco, con su moldura: la luz arriba y el bocel oscuro por dentro.
            val fundicion = if (elegida) (if (oscuro) mezcla(HIERRO, NOCHE, 0.25f) else HIERRO) else (if (oscuro) mezcla(HIERRO, NOCHE, 0.55f) else mezcla(HIERRO, Color(0xFF8A9488), 0.35f))
            drawPath(forma, Brush.verticalGradient(listOf(mezcla(fundicion, Color.White, if (oscuro) 0.08f else 0.24f), fundicion, mezcla(fundicion, Color.Black, 0.3f)), r.top, r.bottom))
            drawPath(forma, mezcla(fundicion, Color.Black, 0.45f), style = Stroke(0.5f * u))
            val dentro = r.deflate(b)
            val formaDentro = placa(dentro, 11.2f * u, 1 * u)
            val esmalte = if (elegida) (if (oscuro) Color(0xFF2E2C27) else Color(0xFFF6EBCB)) else (if (oscuro) Color(0xFF1A1E22) else Color(0xFFDCD6C7))
            drawPath(formaDentro, esmalte)
            val marca = Offset(dentro.left + 15 * u, dentro.center.y)
            clipPath(formaDentro) {
                // El esmalte vidriado: el brillo de arriba y, en la elegida, la luz que lo enciende.
                drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.4f), Color.Transparent), dentro.top, dentro.top + dentro.height * 0.45f), dentro.topLeft, dentro.size)
                if (elegida) {
                    val rr = dentro.width * 0.8f
                    drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = if (oscuro) 0.26f else 0.55f), LUZ.copy(alpha = if (oscuro) 0.06f else 0.1f), Color.Transparent), marca, rr), rr, marca)
                }
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Color.Transparent), dentro.top, dentro.top + 3 * u), dentro.topLeft, Size(dentro.width, 3 * u))
            }
            drawPath(formaDentro, mezcla(fundicion, Color.Black, 0.5f), style = Stroke(0.7f * u))
            if (elegida) drawPath(placa(dentro.deflate(2.4f * u), 9 * u, 0.6f * u), (if (oscuro) mezcla(ORO, LUZ, 0.2f) else mezcla(ORO, Color(0xFF8A6A22), 0.2f)).copy(alpha = 0.8f), style = Stroke(0.6f * u))
            drawLine(filoLuz(oscuro), Offset(r.left + 8 * u, r.top + 0.6f * u), Offset(r.right - 8 * u, r.top + 0.6f * u), 0.7f * u)
            // El pétalo que cae (gasto) o el capullo que brota (ingreso).
            val color = if (ingreso) e.colorIngreso else e.colorGasto
            val apaga: (Color) -> Color = { c -> if (elegida) c else mezcla(c, esmalte, 0.55f) }
            if (ingreso) {
                val tallo = apaga(n(TALLO_SOMBRA, oscuro, 0.3f))
                drawLine(tallo, Offset(marca.x - 0.6f * u, marca.y + 11 * u), Offset(marca.x, marca.y + 1 * u), 1.1f * u, StrokeCap.Round)
                TrazosNouveau.run {
                    hojaConNervio(Offset(marca.x - 0.4f * u, marca.y + 8 * u), Offset(marca.x + 8 * u, marca.y + 4 * u), 3.6f * u, -0.18f, apaga(n(TALLO, oscuro, 0.3f)), tallo)
                    hojaConNervio(Offset(marca.x - 0.5f * u, marca.y + 6 * u), Offset(marca.x - 7 * u, marca.y + 1.6f * u), 3 * u, 0.18f, apaga(n(TALLO, oscuro, 0.3f)), tallo)
                }
                capullo(Offset(marca.x, marca.y + 2 * u), Offset(marca.x + 0.6f * u, marca.y - 10 * u), 7.4f * u, apaga(n(MALVA, oscuro, 0.25f)), tallo)
            } else {
                val flor = apaga(n(MALVA, oscuro, 0.25f))
                val rastro = apaga(tenue(oscuro)).copy(alpha = 0.55f)
                drawPath(plumaNou(latigoNou(Offset(marca.x - 9 * u, marca.y - 10 * u), Offset(marca.x - 5 * u, marca.y - 3 * u), 2 * u, false, 0.2f, 0.8f), 0.7f * u, 0.2f, 0.2f, 0.4f), rastro)
                TrazosNouveau.run {
                    drawPath(petalo(Offset(marca.x - 5 * u, marca.y - 3.4f * u), Offset(marca.x + 6 * u, marca.y + 5 * u), 7.6f * u), mezcla(flor, Color.Black, 0.18f))
                    drawPath(petalo(Offset(marca.x - 4.2f * u, marca.y - 3 * u), Offset(marca.x + 4.4f * u, marca.y + 3.6f * u), 3.8f * u), mezcla(flor, Color.White, 0.15f))
                }
                drawLine(mezcla(flor, Color.Black, 0.3f).copy(alpha = 0.5f), Offset(marca.x - 4 * u, marca.y - 2.6f * u), Offset(marca.x + 3.6f * u, marca.y + 3.4f * u), 0.4f * u)
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            textoEn(medidor, texto, Rect(marca.x + 13 * u, dentro.top + 1 * u, dentro.right - 6 * u, dentro.bottom - 1 * u), letraRotulos,
                legibleNou(color, esmalte, tenue = !elegida), if (elegida) 18f else 15.5f)
        }
        // En medio, los dos rizos de fundición que abrazan el cabujón.
        val hierro = if (oscuro) mezcla(HIERRO, NOCHE, 0.3f) else HIERRO
        val c = Offset(xm, h / 2 - 0.5f * u)
        for (s in listOf(-1f, 1f)) {
            val desde = Offset(xm + s * (hueco + 0.5f * u), h / 2 + 9 * u)
            drawPath(plumaNou(latigoNou(desde, Offset(xm + s * 4.6f * u, h / 2 + 2 * u), 3 * u, s < 0f, 0.12f, 0.9f), 1.8f * u, 1f, 0.35f, 0.25f), hierro)
            drawPath(plumaNou(latigoNou(Offset(xm + s * (hueco + 0.5f * u), h / 2 - 10 * u), Offset(xm + s * 4.4f * u, h / 2 - 4.6f * u), 2.2f * u, s > 0f, 0.12f, 0.8f), 1.4f * u, 1f, 0.35f, 0.25f), hierro)
        }
        cabujon(c, 2.8f * u, 3.8f * u, oscuro, 0f)
    }

    override val altoMes: Dp = 52.dp

    /**
     * La cartela del título de un cartel: el papel crema con su marco de oro y su filete verde, con las esquinas del tema, y el
     * mes dentro, en la letra de cartel. De cada lado sale un tallo que nace en un rizo junto a la cartela y corre hasta una hoja
     * en punta: la de la izquierda lleva al mes anterior; la de la derecha, al siguiente.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 30 * u) return
        val tam = 19f
        val ancho = anchoNou(medidor, e.titulo, letraTitulos, tam)
        val cw = (ancho + 40 * u).coerceIn(110 * u, w - 116 * u)
        val cart = Rect(w / 2 - cw / 2, 5 * u, w / 2 + cw / 2, h - 5 * u)
        val forma = hojaNou(cart, 12 * u, 1.6f * u)
        val cara = if (oscuro) PAPEL_NOCHE else PAPEL
        translate(0.6f * u, 1.4f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, cara)
        clipPath(forma) {
            fibras(cart, e.titulo.hashCode(), oscuro)
            if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), cart.top, cart.center.y), cart.topLeft, cart.size)
        }
        val oro = if (oscuro) mezcla(ORO, NOCHE, 0.35f) else mezcla(ORO, Color(0xFF8A6A22), 0.2f)
        drawPath(forma, oro, style = Stroke(1.3f * u))
        drawPath(hojaNou(cart.deflate(2.8f * u), 9.4f * u, 0.8f * u), hierroLinea(oscuro).copy(alpha = 0.7f), style = Stroke(0.55f * u))
        // Los dos tallos con su hoja en punta: hacia el mes anterior y hacia el siguiente.
        val tallo = if (oscuro) Color(0xFF7E9886) else HIERRO
        val hoja = if (oscuro) mezcla(TALLO, NOCHE, 0.3f) else TALLO
        for (s in listOf(-1f, 1f)) {
            val borde = if (s < 0) cart.left else cart.right
            val punta = Offset(if (s < 0) 7 * u else w - 7 * u, h / 2)
            val baseHoja = Offset(punta.x - s * 16 * u, h / 2)
            if (abs(borde - baseHoja.x) > 12 * u) {
                val pts = latigoNou(baseHoja, Offset(borde - s * 6.4f * u, h / 2 + 1.6f * u), 2.8f * u, s > 0f, 0.1f, 1.1f)
                drawPath(plumaNou(pts, 1.6f * u, 0.9f, 0.35f, 0.3f), tallo)
            }
            translate(0.4f * u, 0.9f * u) { drawPath(TrazosNouveau.hojaLanza(baseHoja, punta, 6.4f * u, -0.06f * s), sombra(oscuro)) }
            TrazosNouveau.run { hojaConNervio(baseHoja, punta, 6.4f * u, -0.06f * s, hoja, tallo) }
        }
        textoEn(medidor, e.titulo, Rect(cart.left + 9 * u, cart.top + 3 * u, cart.right - 9 * u, cart.bottom - 3 * u), letraTitulos, legibleNou(tinta(oscuro), cara, minimo = 7f), tam)
    }

    /**
     * Un renglón de la cuenta: el filete fino al pie y, al principio, la marca: si entra, un capullo verde que brota; si sale,
     * un pétalo de malva que se cae.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        val y = h - 1.2f * u
        val l = if (oscuro) Color(0xFF4F6156) else mezcla(HIERRO, PAPEL, 0.5f)
        drawLine(Brush.horizontalGradient(0f to l, 0.8f to l, 1f to l.copy(alpha = 0f), startX = 11 * u, endX = w - 2 * u), Offset(11 * u, y), Offset(w - 2 * u, y), 0.65f * u)
        val p = Offset(4.6f * u, h - 4 * u)
        if (e.ingreso) {
            drawLine(n(TALLO_SOMBRA, oscuro, 0.3f), Offset(p.x, p.y + 3 * u), Offset(p.x, p.y), 0.7f * u, StrokeCap.Round)
            capullo(Offset(p.x, p.y + 0.4f * u), Offset(p.x + 0.3f * u, p.y - 5.4f * u), 3.8f * u, n(TALLO, oscuro, 0.3f), n(TALLO_SOMBRA, oscuro, 0.3f))
        } else {
            val flor = n(MALVA, oscuro, 0.3f)
            val g = (azar(e.semilla, 3) - 0.5f) * 30f
            rotate(g, p) {
                drawPath(TrazosNouveau.petalo(Offset(p.x - 3 * u, p.y - 2.4f * u), Offset(p.x + 3.4f * u, p.y + 2.2f * u), 4.2f * u), mezcla(flor, Color.Black, 0.18f))
                drawPath(TrazosNouveau.petalo(Offset(p.x - 2.4f * u, p.y - 2.2f * u), Offset(p.x + 2.6f * u, p.y + 1.4f * u), 2 * u), mezcla(flor, Color.White, 0.15f))
            }
        }
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * La fecha en una etiqueta de papel crema, con las esquinas del tema y su filete de oro, con mayúscula solo al principio; de
     * ella sale una línea de látigo que corre hasta el borde y remata en un rizo con su capullo.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 12 * u) return
        val fecha = texto.lowercase().replaceFirstChar { it.uppercase() }
        val tam = 12.5f; val esp = 0.6f
        val ancho = minOf(anchoNou(medidor, fecha, letraRotulos, tam, espaciado = esp), w * 0.62f)
        val etiqueta = Rect(4 * u, 2.5f * u, 4 * u + ancho + 20 * u, h - 2.5f * u)
        val forma = hojaNou(etiqueta, 7 * u, 1 * u)
        val cara = papel(oscuro)
        translate(0.5f * u, 1 * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, cara)
        clipPath(forma) { fibras(etiqueta, texto.length + 41, oscuro, 0.8f) }
        drawPath(forma, oroLinea(oscuro), style = Stroke(0.9f * u))
        textoEn(medidor, fecha, Rect(etiqueta.left + 8 * u, etiqueta.top, etiqueta.right - 8 * u, etiqueta.bottom), letraRotulos,
            legibleNou(tinta(oscuro), cara), tam, espaciado = esp)
        if (etiqueta.right + 30 * u < w) {
            val linea = (if (oscuro) Color(0xFF6E8676) else mezcla(HIERRO, TALLO_SOMBRA, 0.35f)).copy(alpha = 0.8f)
            val fin = Offset(w - 10 * u, h / 2 - 0.6f * u)
            drawPath(plumaNou(latigoNou(Offset(etiqueta.right + 3 * u, h / 2), fin, 2.4f * u, false, 0.012f, 1.1f), 1.2f * u, 0.5f, 0.35f, 0.75f), linea)
            capullo(Offset(fin.x + 0.4f * u, fin.y - 3.6f * u), Offset(fin.x + 2.4f * u, fin.y - 10.4f * u), 4 * u, n(MALVA, oscuro, 0.4f), linea)
        }
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * La hoja rápida es el arco de un cartel de 1900 (su borde de arriba sube al centro en la curva larga de la línea de
     * látigo y, en cada pie, tiene el ojo de un rizo). Por debajo del borde corre la línea de látigo, gruesa en los hombros del
     * arco y fina en los extremos, que en cada ojo se enrosca en su rizo; la sigue por dentro un hilo de oro que termina en
     * perlas. En lo alto del arco cuelga una lámpara de tulipa de vidrio de Nancy entre dos hojas: de día, el vidrio de
     * colores; de noche, encendida, y la línea solo se dora cerca de ella. Desde los ojos, un filete corre por los hombros, baja
     * por los dos cantos y se enrosca abajo; al pie, una libélula vuela entre dos látigos y, en la esquina de la izquierda,
     * crece un iris.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 160 * u || h < 120 * u) return
        // El perfil de arriba, como lo corta la hoja (`QuickActivity`, rama de Art nouveau).
        val p = 16 * u; val ojo = 4 * u; val a = w * 0.3f; val b = w * 0.7f
        val izqArco = TrazosNouveau.bezier(Offset(a, p), Offset(a + w * 0.06f, p), Offset(a + w * 0.04f, 0f), Offset(a + w * 0.14f, 0f), 36)
        val llano = List(11) { i -> Offset(a + w * 0.14f + (b - a - w * 0.28f) * (i + 1) / 12f, 0f) }
        val derArco = TrazosNouveau.bezier(Offset(b - w * 0.14f, 0f), Offset(b - w * 0.04f, 0f), Offset(b - w * 0.06f, p), Offset(b, p), 36)
        val arco = izqArco + llano + derArco
        val d = 3.6f * u
        val dentro = corridaNou(arco, d)
        // Los dos rizos, en los ojos: la línea entra por abajo de cada ojo y se enrosca hacia dentro.
        val rizoIzq = espiralNou(Offset(a - ojo, p), d, 0.7f * u, 90f, 1.3f, true).reversed()
        val rizoDer = espiralNou(Offset(b + ojo, p), d, 0.7f * u, 90f, 1.3f, false)
        val latigo = rizoIzq + dentro + rizoDer
        val total = latigo.size
        val cuerpo = TrazosNouveau.cinta(latigo) { t ->
            val s = minOf(t, 1f - t) / 0.5f
            val sube = ((s - 0.02f) / 0.4f).coerceIn(0f, 1f)
            val baja = ((s - 0.62f) / 0.38f).coerceIn(0f, 1f)
            2.1f * u * (0.2f + 0.8f * sin(sube * PI.toFloat() / 2) - 0.35f * baja)
        }
        val lateral = if (oscuro) mezcla(TALLO, NOCHE, 0.5f) else e.primario.copy(alpha = e.primario.alpha * 0.8f)
        val cerca = if (oscuro) mezcla(LUZ, ORO, 0.3f) else mezcla(e.primario, HIERRO, 0.3f)
        val pincel = Brush.horizontalGradient(0f to lateral, 0.36f to lateral, 0.5f to cerca, 0.64f to lateral, 1f to lateral, startX = 0f, endX = w)
        if (!oscuro) drawPath(TrazosNouveau.cinta(latigo.map { Offset(it.x + 0.5f * u, it.y + 0.7f * u) }) { t ->
            val s = minOf(t, 1f - t) / 0.5f; 2.1f * u * (0.2f + 0.8f * sin(((s - 0.02f) / 0.4f).coerceIn(0f, 1f) * PI.toFloat() / 2))
        }, Color.Black.copy(alpha = 0.08f))
        drawPath(cuerpo, pincel)
        if (total > 0) {
            // Las perlas de los centros de los rizos.
            drawCircle(pincel, 0.9f * u, latigo.first()); drawCircle(pincel, 0.9f * u, latigo.last())
        }
        // El hilo de oro, por dentro del arco, con una perla en cada punta.
        val oro = if (oscuro) mezcla(ORO, NOCHE, 0.4f) else mezcla(ORO, Color(0xFF8A6A22), 0.2f)
        val hilo = corridaNou(arco, 6.6f * u).filter { it.x > a + 3 * u && it.x < b - 3 * u }
        if (hilo.size > 2) {
            val pincelOro = if (oscuro) Brush.horizontalGradient(0f to oro.copy(alpha = 0.5f), 0.42f to oro, 0.5f to mezcla(LUZ, ORO_BRILLO, 0.4f), 0.58f to oro, 1f to oro.copy(alpha = 0.5f), startX = a, endX = b)
            else Brush.horizontalGradient(listOf(oro, oro), startX = a, endX = b)
            drawPath(TrazosNouveau.linea(hilo), pincelOro, style = Stroke(0.6f * u, cap = StrokeCap.Round))
            drawCircle(pincelOro, 1.1f * u, hilo.first()); drawCircle(pincelOro, 1.1f * u, hilo.last())
        }
        // La lámpara que cuelga de lo alto del arco, entre dos hojas.
        val cx = w / 2
        val colgado = Offset(cx, d + 0.4f * u)
        val casquillo = Offset(cx, d + 3.4f * u)
        val luz = if (oscuro) 1f else 0f
        if (oscuro) {
            val c = Offset(cx, 12 * u)
            drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = 0.16f), LUZ.copy(alpha = 0.05f), Color.Transparent), c, 86 * u), 86 * u, c)
        }
        val tallo = if (oscuro) mezcla(TALLO_SOMBRA, LUZ, 0.25f) else TALLO_SOMBRA
        val hojaLuz = if (oscuro) mezcla(TALLO, LUZ, 0.25f) else TALLO
        for (s in listOf(-1f, 1f)) TrazosNouveau.run {
            hojaConNervio(Offset(cx + s * 1.6f * u, d + 0.6f * u), Offset(cx + s * 16 * u, d + 6.4f * u), 4 * u, 0.18f * s, hojaLuz, tallo)
        }
        drawLine(mezcla(ORO, Color.Black, 0.35f), colgado, casquillo, 0.9f * u)
        rotate(180f, casquillo) { TrazosNouveau.run { tulipa(casquillo, 12 * u, luz) } }
        // Los hombros y los cantos: el filete que sale de cada ojo, dobla la esquina, baja y se enrosca abajo.
        val m = 4.6f * u
        val yFin = h - 30 * u
        val filete = if (oscuro) mezcla(TALLO, NOCHE, 0.55f) else e.primario.copy(alpha = e.primario.alpha * 0.5f)
        if (yFin > p + 40 * u) for (s in listOf(-1f, 1f)) {
            val xo = if (s < 0) a - 2 * ojo - 1.6f * u else b + 2 * ojo + 1.6f * u
            val xb = if (s < 0) m else w - m
            val yh = p + d
            val r = 7 * u
            val camino = Path().apply {
                moveTo(xo, yh)
                lineTo(xb - s * r, yh)
                quadraticTo(xb, yh, xb, yh + r)
                lineTo(xb, yFin)
            }
            drawPath(camino, filete, style = Stroke(0.85f * u))
            drawCircle(filete, 1 * u, Offset(xo + s * 0.4f * u, yh))
            // El cabujón de vidrio engastado donde el filete dobla la esquina.
            cabujon(Offset(xb - s * 2.1f * u, yh + 2.1f * u), 2.2f * u, 2.2f * u, oscuro, 0f)
            val rizo = TrazosNouveau.puntosRizo(Offset(xb, yFin), Offset(0f, 1f), 3 * u, s > 0f, 1.2f, 26)
            drawPath(plumaNou(listOf(Offset(xb, yFin - 2 * u)) + rizo, 1.2f * u, 0.7f, 0.25f, 0.2f), filete)
            // El hilo de oro, por dentro del canto.
            drawLine(oro.copy(alpha = 0.55f), Offset(xb - s * 2.6f * u, yh + r + 6 * u), Offset(xb - s * 2.6f * u, yFin - 14 * u), 0.5f * u)
        }
        // Al pie: la libélula que vuela entre dos látigos.
        val yb = h - 9 * u
        val ancho = minOf(w * 0.32f, 120 * u)
        val pie = if (oscuro) mezcla(TALLO, NOCHE, 0.55f) else e.primario.copy(alpha = e.primario.alpha * 0.55f)
        for (s in listOf(-1f, 1f)) {
            val pts = latigoNou(Offset(w / 2 + s * ancho, yb), Offset(w / 2 + s * 16 * u, yb - 0.6f * u), 2.4f * u, s > 0f, 0.015f, 1.1f)
            drawPath(plumaNou(pts, 1.1f * u, 0.15f, 0.35f, 0.7f), pie)
        }
        TrazosNouveau.run {
            libelula(Offset(w / 2 - 4 * u, yb), 17 * u, 90f, 0f, n(LIBELULA, oscuro, 0.4f), n(NACAR, oscuro, 0.45f), n(ORO, oscuro, 0.4f))
            // El iris de la esquina de abajo a la izquierda.
            iris(Offset(14 * u, h - 2.5f * u), 38 * u, n(IRIS, oscuro, 0.45f), n(TALLO, oscuro, 0.5f), n(TALLO_SOMBRA, oscuro, 0.5f))
        }
    }

    /**
     * El recuadro de Relaciones lleva arriba al centro un capullo entre dos rizos, de donde sale la línea de látigo bajo el canto
     * de arriba hasta una perla en cada punta; en las dos esquinas redondas del tema, un rizo; abajo, un filete con una hojita.
     * Tenue en el centro.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        val base = if (oscuro) e.secundario else e.primario
        val linea = base.copy(alpha = base.alpha * 0.5f); val fino = base.copy(alpha = base.alpha * 0.28f)
        val cx = w / 2; val yt = 6.5f * u
        if (cx - 14 * u > 10 * u) for (s in listOf(-1f, 1f)) {
            val pts = latigoNou(Offset(cx + s * (cx - 7 * u), yt), Offset(cx + s * 6 * u, yt - 0.3f * u), 1.9f * u, s > 0f, 0.01f, 1.05f)
            drawPath(plumaNou(pts, 1.1f * u, 0.3f, 0.35f, 0.75f), linea)
            drawCircle(linea, 0.9f * u, Offset(cx + s * (cx - 5.6f * u), yt))
        }
        capullo(Offset(cx, yt + 1.6f * u), Offset(cx, yt - 4.6f * u), 3.6f * u, linea, fino)
        // Los rizos de las esquinas redondas: arriba a la izquierda y abajo a la derecha.
        drawPath(plumaNou(TrazosNouveau.puntosRizo(Offset(3 * u, h * 0.42f), Offset(0f, -1f), 3 * u, true, 1.15f, 24), 0.9f * u, 1f, 0.3f, 0.1f), fino)
        drawPath(plumaNou(TrazosNouveau.puntosRizo(Offset(w - 3 * u, h * 0.58f), Offset(0f, 1f), 3 * u, true, 1.15f, 24), 0.9f * u, 1f, 0.3f, 0.1f), fino)
        // Abajo, el filete con su hojita al centro.
        val yb = h - 5 * u
        if (cx - 10 * u > 12 * u) {
            drawLine(fino, Offset(12 * u, yb), Offset(cx - 6 * u, yb), 0.6f * u)
            drawLine(fino, Offset(cx + 6 * u, yb), Offset(w - 12 * u, yb), 0.6f * u)
        }
        drawPath(TrazosNouveau.hojaLanza(Offset(cx - 5 * u, yb), Offset(cx + 5 * u, yb - 0.6f * u), 2.6f * u, 0.14f), linea)
    }

    /** El icono de la cinta dentro del aro de látigo de oro, que nace de su yema y se enrosca abajo a la derecha. */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2)
        TrazosNouveau.run { anilloLatigo(c, s * 0.39f, s * 0.07f, e.primario, e.secundario.copy(alpha = e.secundario.alpha * 0.6f), sombra = false, filete = false) }
    }
}
