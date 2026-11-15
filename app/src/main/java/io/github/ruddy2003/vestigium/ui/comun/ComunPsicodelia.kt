package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
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
import io.github.ruddy2003.vestigium.ui.TrazosPsicodelia
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Psicodelia
// =====================================================================

/**
 * Las rayas de los renglones de un campo de varios renglones, de abajo arriba: la primera ([k] = 0) al pie del último
 * renglón, a [margenY] del canto de abajo, y luego una cada 24 sp mientras quede sitio arriba (16 dp bajo [margenY]).
 */
private inline fun DrawScope.renglonesPsi(margenY: Dp, raya: (k: Int, y: Float) -> Unit) {
    val paso = maxOf(24.sp.toPx(), 1f)
    val tope = margenY.toPx() + 16.dp.toPx()
    var y = size.height - margenY.toPx() + 1.dp.toPx()
    var k = 0
    while (y > tope && k < 200) { raya(k, y); y -= paso; k++ }
}

/** La razón de contraste entre dos colores opacos (WCAG 2): de 1 (iguales) a 21 (negro sobre blanco). */
private fun contrastePsi(a: Color, b: Color): Float {
    val la = a.luminance(); val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * El color [c] de un texto, tal cual si se lee sobre [fondo] (contraste de al menos [minimo]:1); si no, llevado poco a poco
 * hacia la tinta violeta de los carteles (sobre un fondo claro) o hacia el blanco lila de la luz negra (sobre uno oscuro)
 * hasta que se lea. Con [tenue] se apaga antes hacia el fondo, lo más que deja ese contraste.
 */
private fun legiblePsi(c: Color, fondo: Color, tenue: Boolean = false, minimo: Float = 4.6f): Color {
    val opaco = c.copy(alpha = 1f)
    if (tenue) for (k in 8 downTo 1) {
        val m = mezcla(opaco, fondo, k * 0.05f)
        if (contrastePsi(m, fondo) >= minimo) return m
    }
    if (contrastePsi(opaco, fondo) >= minimo) return opaco
    val hacia = if (fondo.luminance() > 0.18f) Color(0xFF2A1C32) else Color(0xFFF2EAF7)
    for (k in 1..20) {
        val m = mezcla(opaco, hacia, k * 0.05f)
        if (contrastePsi(m, fondo) >= minimo) return m
    }
    return hacia
}

/** El ancho, en píxeles, de [texto] en una sola línea con el mismo estilo con que lo dibuja [textoEn]. */
private fun anchoPsi(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, espaciado: Float = 0f): Float =
    if (texto.isBlank()) 0f
    else medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, fontWeight = FontWeight.Normal, letterSpacing = espaciado.sp),
        maxLines = 1, softWrap = false).size.width.toFloat()

/**
 * Un recuadro inflado como las letras burbuja de los carteles: las esquinas redondas de [radio] y los cuatro cantos apenas
 * abombados hacia fuera ([comba] en el medio de los de arriba y abajo, la mitad en los de los lados).
 */
private fun infladoPsi(r: Rect, radio: Float, comba: Float): Path = Path().apply {
    val k = minOf(radio, r.width / 2f, r.height / 2f)
    val cv = comba; val cl = comba * 0.5f
    moveTo(r.left, r.top + k)
    quadraticTo(r.left, r.top, r.left + k, r.top)
    quadraticTo(r.center.x, r.top - 2 * cv, r.right - k, r.top)
    quadraticTo(r.right, r.top, r.right, r.top + k)
    quadraticTo(r.right + 2 * cl, r.center.y, r.right, r.bottom - k)
    quadraticTo(r.right, r.bottom, r.right - k, r.bottom)
    quadraticTo(r.center.x, r.bottom + 2 * cv, r.left + k, r.bottom)
    quadraticTo(r.left, r.bottom, r.left, r.bottom - k)
    quadraticTo(r.left - 2 * cl, r.center.y, r.left, r.top + k)
    close()
}

/**
 * La silueta de los botones de la hoja rápida (`QuickActivity`, rama de Psicodelia), dentro de [r]: el botón blando de un
 * cartel que se derrite, con las esquinas infladas, el canto de arriba apenas combado y, del de abajo, dos gotas que se
 * escurren (una larga a 0.74 del ancho y otra corta a 0.3; en otras piezas, en [larga] y [corta]). [u] son los píxeles de un dp.
 */
private fun botonGoteaPsi(r: Rect, u: Float, larga: Float = 0.74f, corta: Float = 0.3f): Path = Path().apply {
    val w = r.width; val h = r.height
    val g = minOf(5.5f * u, h * 0.14f); val b = h - g
    val rr = minOf(14 * u, b / 2, w / 4); val f = minOf(1.8f * u, b * 0.05f)
    val x0 = r.left; val y0 = r.top
    fun gota(dx: Float, largo: Float, dw: Float) {
        val cuello = minOf(dw * 0.8f, largo - dw)
        lineTo(x0 + dx + dw * 1.8f, y0 + b); quadraticTo(x0 + dx + dw, y0 + b, x0 + dx + dw, y0 + b + cuello)
        lineTo(x0 + dx + dw, y0 + b + largo - dw); arcTo(Rect(Offset(x0 + dx, y0 + b + largo - dw), dw), 0f, 180f, false)
        lineTo(x0 + dx - dw, y0 + b + cuello); quadraticTo(x0 + dx - dw, y0 + b, x0 + dx - dw * 1.8f, y0 + b)
    }
    moveTo(x0, y0 + rr + f); quadraticTo(x0, y0 + f, x0 + rr, y0 + f)
    quadraticTo(x0 + w / 2, y0 - f, x0 + w - rr, y0 + f)
    quadraticTo(x0 + w, y0 + f, x0 + w, y0 + rr + f)
    lineTo(x0 + w, y0 + b - rr); quadraticTo(x0 + w, y0 + b, x0 + w - rr, y0 + b)
    if (larga > corta) {
        gota(w * larga, g, minOf(2.2f * u, g * 0.4f))
        gota(w * corta, g * 0.62f, minOf(1.6f * u, g * 0.3f))
    } else {
        gota(w * corta, g * 0.62f, minOf(1.6f * u, g * 0.3f))
        gota(w * larga, g, minOf(2.2f * u, g * 0.4f))
    }
    lineTo(x0 + rr, y0 + b); quadraticTo(x0, y0 + b, x0, y0 + b - rr)
    close()
}

/**
 * Una gota de tinta que cuelga de un canto a la altura [y], en [x]: el cuello que nace ancho del canto, se adelgaza y acaba
 * en una gota redonda de [radio], a [largo] del canto.
 */
private fun gotaPsi(x: Float, y: Float, largo: Float, radio: Float): Path {
    val r = radio.coerceAtLeast(0.1f)
    val l = maxOf(largo, r * 2.2f)
    val n = r * 0.5f
    val hombro = r * 1.9f
    val yc = y + l - r
    val cuello = Path().apply {
        moveTo(x - hombro, y)
        quadraticTo(x - n, y, x - n, y + (yc - y) * 0.5f)
        lineTo(x - n * 0.85f, yc)
        lineTo(x + n * 0.85f, yc)
        lineTo(x + n, y + (yc - y) * 0.5f)
        quadraticTo(x + n, y, x + hombro, y)
        close()
    }
    return Path.combine(PathOperation.Union, cuello, Path().apply { addOval(Rect(Offset(x, yc), r)) })
}

/** Una lágrima de tinta que cae, con la panza redonda de [r] en [c] y la punta hacia arriba. */
private fun lagrimaPsi(c: Offset, r: Float): Path = Path().apply {
    val punta = Offset(c.x, c.y - r * 2.5f)
    moveTo(punta.x, punta.y)
    quadraticTo(c.x + r * 1.05f, c.y - r * 0.9f, c.x + r, c.y)
    arcTo(Rect(c, r), 0f, 180f, false)
    quadraticTo(c.x - r * 1.05f, c.y - r * 0.9f, punta.x, punta.y)
    close()
}

/** Un polígono de esquinas blandas, como el de una letra inflada: cada esquina de [v] se redondea a [t] (fracción de sus lados). */
private fun blandoPsi(v: List<Offset>, t: Float): Path = Path().apply {
    val n = v.size
    for (i in 0 until n) {
        val p = v[(i + n - 1) % n]; val c = v[i]; val q = v[(i + 1) % n]
        val a = c + (p - c) * t; val b = c + (q - c) * t
        if (i == 0) moveTo(a.x, a.y) else lineTo(a.x, a.y)
        quadraticTo(c.x, c.y, b.x, b.y)
    }
    close()
}

/** La flecha de letra burbuja que apunta a la derecha, en un cuadro de [s] centrado en [c]: el asta gorda y la cabeza blanda. */
private fun flechaPsi(c: Offset, s: Float): Path {
    val x0 = c.x - s * 0.38f; val xj = c.x + 0.02f * s; val xt = c.x + s * 0.4f
    val th = s * 0.115f; val hh = s * 0.33f
    return blandoPsi(listOf(Offset(x0, c.y - th), Offset(xj, c.y - th), Offset(xj, c.y - hh), Offset(xt, c.y),
        Offset(xj, c.y + hh), Offset(xj, c.y + th), Offset(x0, c.y + th)), 0.32f)
}

/**
 * Una cinta horizontal de tinta de [x0] a [x1] a la altura [y], que nace de [g0] de grueso, pasa por [g1] en el medio y
 * llega con [g2]: el trazo del pincel de punta redonda.
 */
private fun cintaPsi(x0: Float, x1: Float, y: Float, g0: Float, g1: Float, g2: Float): Path = Path().apply {
    val n = 36
    fun g(t: Float) = (g0 * (1 - t) * (1 - 2 * t) + g1 * 4 * t * (1 - t) + g2 * t * (2 * t - 1)).coerceAtLeast(0.05f) / 2f
    for (k in 0..n) { val t = k / n.toFloat(); val x = x0 + (x1 - x0) * t; if (k == 0) moveTo(x, y - g(t)) else lineTo(x, y - g(t)) }
    for (k in n downTo 0) { val t = k / n.toFloat(); lineTo(x0 + (x1 - x0) * t, y + g(t)) }
    close()
}

/** Los puntos de [pts] corridos [d] hacia su normal (hacia abajo si la línea va hacia la derecha). */
private fun corridaPsi(pts: List<Offset>, d: Float): List<Offset> = List(pts.size) { i ->
    val a = pts[max(i - 1, 0)]; val b = pts[min(i + 1, pts.size - 1)]
    val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.0001f)
    Offset(pts[i].x - dy / l * d, pts[i].y + dx / l * d)
}

/**
 * La trama de puntos de imprenta dentro de [r]: puntos en tresbolillo cada [paso], grandes junto a [lleno] y cada vez más
 * chicos hasta perderse a [alcance] de él, como el degradado de un cartel impreso.
 */
private fun DrawScope.tramaPsi(r: Rect, paso: Float, color: Color, lleno: Offset, alcance: Float, maximo: Float = 0.32f) {
    if (paso < 1f || r.width <= 0f || r.height <= 0f || alcance <= 0f) return
    val p = Path()
    var y = r.top + paso / 2; var fila = 0; var n = 0
    while (y < r.bottom && n < 3000) {
        var x = r.left + (if (fila % 2 == 0) paso / 2 else paso)
        while (x < r.right && n < 3000) {
            val rr = paso * maximo * (1f - hypot(x - lleno.x, y - lleno.y) / alcance)
            if (rr > 0.3f) { p.addOval(Rect(Offset(x, y), rr)); n++ }
            x += paso
        }
        y += paso * 0.87f; fila++
    }
    drawPath(p, color)
}

/** El perfil de la hoja rápida: cada gota de su nube (centro en fracción del ancho, medio ancho y cuánto sube, en dp). */
private val GOTAS_QUICK = listOf(floatArrayOf(0.12f, 44f, 6f), floatArrayOf(0.3f, 60f, 11f), floatArrayOf(0.52f, 74f, 16f), floatArrayOf(0.74f, 56f, 11.5f), floatArrayOf(0.9f, 36f, 6f))

/** Cuánto sube en [x] el borde de la hoja rápida sobre su base (como lo calcula `QuickActivity`): las gotas fundidas suave. */
private fun altoQuickPsi(x: Float, w: Float, u: Float): Float {
    val k = 2.6f * u
    var suma = 1f
    for (g in GOTAS_QUICK) {
        val d = (x - g[0] * w) / (g[1] * u); val b = g[1] * 0.6f * u
        if (d * d < 1f) suma += exp((g[2] * u - b + b * sqrt(1f - d * d)) / k)
    }
    return k * ln(suma)
}

/**
 * Psicodelia: las piezas comunes de la mesa del dibujante de carteles, en San Francisco en 1967, y de la calle que ve por la
 * ventana. El campo es la cartela de un cartel: el recuadro inflado como las letras burbuja, impreso otra vez un poco corrido
 * en el color que vibra con el primero (con el foco, en la tinta de arcoíris del tintero partido) y su raya de tinta que se
 * escurre en una gota. Lo demás es del mismo mundo: las gotas de aceite de las luces líquidas, la flecha de letra inflada, la
 * lista de pistas con su disco, el botón de solapa, la pared de tablas con los carteles arrancados, la funda del disco vacía,
 * la radio de válvulas y el pandero con sus cintas, el boleto del baile, el cartel pegado con cinta, la ventana del
 * departamento con la niebla y las casas victorianas, la sala de baile con el retroproyector de las luces y el periódico del
 * barrio con su tinta de arcoíris. De noche es la misma ciudad dentro de la sala con luz negra: el papel se hunde en el negro
 * violeta y solo brilla lo fluorescente (el verde limón, el magenta, el violeta y el naranja), con su resplandor.
 */
object ComunPsicodelia : DisenoComun {
    private val MAGENTA = TrazosPsicodelia.MAGENTA
    private val VERDE = TrazosPsicodelia.VERDE
    private val NARANJA = TrazosPsicodelia.NARANJA
    private val AZUL = TrazosPsicodelia.AZUL
    private val AMARILLO = TrazosPsicodelia.AMARILLO
    private val VIOLETA = TrazosPsicodelia.VIOLETA
    private val LIMON = TrazosPsicodelia.LIMON
    private val FLUOR_MAGENTA = TrazosPsicodelia.FLUOR_MAGENTA
    private val FLUOR_VIOLETA = TrazosPsicodelia.FLUOR_VIOLETA
    private val FLUOR_NARANJA = TrazosPsicodelia.FLUOR_NARANJA
    private val SALA = TrazosPsicodelia.SALA
    private val VINILO = TrazosPsicodelia.VINILO
    private val TINTA = TrazosPsicodelia.TINTA
    /** El ámbar del aceite de las luces líquidas, entre el naranja y el amarillo. */
    private val AMBAR = Color(0xFFF2A51F)
    /** El papel de cartel, de día, y el mismo papel hundido en la sala con luz negra. */
    private val PAPEL = Color(0xFFFAF4EA)
    private val PAPEL_NOCHE = Color(0xFF1D1329)
    /** El blanco del papel roto bajo la luz negra: el blanqueador del papel brilla lila. */
    private val PAPEL_UV = Color(0xFFC9BCFF)

    // ---------------------------------------------------------------- los colores de la calle (de día) y de la sala (de noche)

    private fun tinta(oscuro: Boolean): Color = if (oscuro) Color(0xFFF2EAF7) else Color(0xFF2A1C32)
    private fun tenue(oscuro: Boolean): Color = if (oscuro) Color(0xFFB4A7C4) else Color(0xFF61546A)
    /** El magenta de cartel que se lee; de noche, el magenta fluorescente. */
    private fun magenta(oscuro: Boolean): Color = if (oscuro) FLUOR_MAGENTA else Color(0xFFB0246A)
    /** La tinta que manda: de día, el magenta de cartel; de noche, el verde limón de la luz negra. */
    private fun primera(oscuro: Boolean): Color = if (oscuro) LIMON else Color(0xFFB0246A)
    /** Lo que vibra junto a la tinta que manda: de día, el verde de cartel; de noche, el magenta fluorescente. */
    private fun contra(oscuro: Boolean): Color = if (oscuro) FLUOR_MAGENTA else VERDE
    private fun papel(oscuro: Boolean): Color = if (oscuro) PAPEL_NOCHE else PAPEL
    private fun borde(oscuro: Boolean): Color = if (oscuro) Color(0xFF4E3D69) else Color(0xFFB9AC9D)
    /** La sombra de lo que está encima de algo: de día, violeta y suave; de noche, el negro de la sala. */
    private fun sombra(oscuro: Boolean): Color = if (oscuro) Color.Black.copy(alpha = 0.5f) else Color(0xFF3D2350).copy(alpha = 0.13f)
    /** Las tintas del aceite de las luces líquidas: de día, las de cartel; de noche, las que brillan con la luz negra. */
    private fun aceite(oscuro: Boolean, i: Int): Color =
        (if (oscuro) listOf(LIMON, FLUOR_MAGENTA, FLUOR_VIOLETA, FLUOR_NARANJA) else listOf(MAGENTA, AMBAR, VERDE, AZUL))[i.mod(4)]
    /** Las etiquetas de los discos y los colores de los carteles, por semilla. */
    private fun cartel(oscuro: Boolean, i: Int): Color =
        (if (oscuro) listOf(LIMON, FLUOR_MAGENTA, FLUOR_NARANJA, FLUOR_VIOLETA) else listOf(MAGENTA, NARANJA, VERDE, AZUL, AMARILLO, VIOLETA)).let { it[i.mod(it.size)] }

    /** El resplandor de lo fluorescente bajo la luz negra: [p] trazado ancho y muy transparente, dos veces, debajo de su línea de [g]. */
    private fun DrawScope.resplandor(p: Path, c: Color, g: Float, fuerza: Float = 1f) {
        drawPath(p, c.copy(alpha = 0.06f * fuerza), style = Stroke(g * 6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, c.copy(alpha = 0.13f * fuerza), style = Stroke(g * 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** Un halo redondo de [c] alrededor de [centro], de radio [r]: la luz que echa lo fluorescente (o la lámpara). */
    private fun DrawScope.halo(centro: Offset, r: Float, c: Color, alfa: Float) {
        if (r <= 0f) return
        drawCircle(Brush.radialGradient(listOf(c.copy(alpha = alfa), c.copy(alpha = alfa * 0.35f), Color.Transparent), centro, r), r, centro)
    }

    // ---------------------------------------------------------------- las figuras de la mesa, de la calle y de la sala

    /**
     * Tres gotas de aceite que se tocan, como en el vidrio del retroproyector: la del medio, más grande, en la tinta que manda;
     * las de los lados en sus complementarias, mezcladas donde se pisan (de día como tintas en el papel; de noche se suman como
     * luz), cada una con su brillo. [k] es el tamaño de un dp; [f], la opacidad.
     */
    private fun DrawScope.nubeAceite(c: Offset, k: Float, oscuro: Boolean, f: Float, semilla: Int = 0) {
        val gotas = listOf(Triple(Offset(-6.2f, 0.5f), 2.6f, if (oscuro) LIMON else VERDE), Triple(Offset(0f, -0.4f), 3.6f, if (oscuro) FLUOR_MAGENTA else MAGENTA),
            Triple(Offset(6.1f, 0.6f), 2.5f, if (oscuro) FLUOR_NARANJA else AMBAR))
        val modo = if (oscuro) BlendMode.Screen else BlendMode.Multiply
        gotas.forEachIndexed { i, (d, r, col) ->
            val o = Offset(c.x + d.x * k, c.y + d.y * k)
            if (oscuro) halo(o, r * k * 2.6f, col, 0.22f * f)
            drawPath(TrazosPsicodelia.gota(o, r * k, (semilla * 0.13f + i * 0.31f) % 1f, 0.07f, 3, 32), col.copy(alpha = 0.88f * f), blendMode = modo)
        }
        gotas.forEach { (d, r, _) ->
            drawOval(Color.White.copy(alpha = (if (oscuro) 0.45f else 0.6f) * f), Offset(c.x + (d.x - r * 0.55f) * k, c.y + (d.y - r * 0.6f) * k), Size(r * 0.55f * k, r * 0.38f * k))
        }
    }

    /**
     * Un disco chico de la lista de pistas: el vinilo con tres surcos, el brillo de la lámpara, el canto y la etiqueta de
     * [etiqueta]. Con [luz] (0 a 1) la etiqueta se enciende (de noche, con su halo de luz negra).
     */
    private fun DrawScope.discoChico(c: Offset, r: Float, etiqueta: Color, oscuro: Boolean, luz: Float) {
        val u = density
        if (luz > 0f) halo(c, r * 2.4f, etiqueta, (if (oscuro) 0.4f else 0.28f) * luz)
        drawCircle(sombra(oscuro), r, Offset(c.x + 0.4f * u, c.y + 0.9f * u))
        drawCircle(if (oscuro) Color(0xFF0C0812) else VINILO, r, c)
        for (k in 1..3) drawCircle(Color.White.copy(alpha = 0.08f), r * (0.5f + k * 0.13f), c, style = Stroke(0.35f * u))
        val arco = Rect(c, r * 0.82f)
        drawArc(Color.White.copy(alpha = 0.32f), 200f, 46f, false, arco.topLeft, arco.size, style = Stroke(0.7f * u, cap = StrokeCap.Round))
        drawArc(Color.White.copy(alpha = 0.16f), 20f, 46f, false, arco.topLeft, arco.size, style = Stroke(0.7f * u, cap = StrokeCap.Round))
        drawCircle((if (oscuro) PAPEL_UV else Color.White).copy(alpha = if (oscuro) 0.3f else 0.14f), r, c, style = Stroke(0.45f * u))
        drawCircle(if (luz > 0f) mezcla(etiqueta, Color.White, 0.15f * luz) else etiqueta, r * 0.38f, c)
        drawCircle(mezcla(etiqueta, Color.Black, 0.3f), r * 0.38f, c, style = Stroke(0.3f * u))
        drawCircle(if (oscuro) Color(0xFF0C0812) else VINILO, r * 0.075f, c)
    }

    /**
     * Lo que queda de un cartel arrancado en la esquina [esquina] de la pared: el pedazo pegado, de [ancho] por [alto] hacia
     * [sx] y [sy], con su orilla rota (el blanco del papel asoma) y, encima, un trozo de su dibujo en el color que vibra: aros
     * de un sol (o, con [ondas], las franjas onduladas de unas letras). Una grapa lo sujeta todavía.
     */
    private fun DrawScope.restoCartel(esquina: Offset, sx: Float, sy: Float, ancho: Float, alto: Float, fondo: Color, dibujo: Color, roto: Color,
                                      semilla: Int, ondas: Boolean, oscuro: Boolean) {
        val u = density
        fun orilla(extra: Float): Path = Path().apply {
            moveTo(esquina.x, esquina.y)
            lineTo(esquina.x + sx * (ancho + extra), esquina.y)
            val n = 13
            for (k in 1 until n) {
                val t = k / n.toFloat()
                val x = esquina.x + sx * (ancho + extra) * (1 - t); val y = esquina.y + sy * (alto + extra) * t
                val j = (azar(semilla, k) - 0.5f) * 3.2f * u + (if (k % 4 == 1) 2.2f * u else 0f)
                lineTo(x + sx * (j * 0.7f + extra * 0.4f), y + sy * (j * 0.7f + extra * 0.4f))
            }
            lineTo(esquina.x, esquina.y + sy * (alto + extra))
            close()
        }
        val papelRoto = orilla(1.6f * u)
        val pedazo = orilla(0f)
        drawPath(papelRoto, roto)
        if (oscuro) resplandor(pedazo, fondo, 1.2f * u, 0.7f)
        drawPath(pedazo, fondo)
        clipPath(pedazo) {
            if (ondas) {
                var k = 0; var y = esquina.y + sy * 3f * u
                while (k < 12 && (if (sy > 0) y < esquina.y + alto else y > esquina.y - alto)) {
                    drawPath(TrazosPsicodelia.onda(esquina.x - ancho, esquina.x + ancho, y, 1.6f * u, 12f * u, k * 0.25f), dibujo, style = Stroke(2.4f * u, cap = StrokeCap.Round))
                    y += sy * 6f * u; k++
                }
            } else {
                var r = 4f * u; var k = 0
                while (r < maxOf(ancho, alto) * 1.5f && k < 14) { drawCircle(dibujo, r, esquina, style = Stroke(2.6f * u)); r += 6f * u; k++ }
            }
            // La luz de la calle sobre el papel, y su sombra junto a la orilla.
            drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f), Color.Transparent),
                esquina, Offset(esquina.x + sx * ancho, esquina.y + sy * alto)))
        }
        // La grapa que lo sujeta.
        val g = Offset(esquina.x + sx * ancho * 0.32f, esquina.y + sy * alto * 0.3f)
        rotate(if (sx * sy > 0) -12f else 12f, g) {
            drawRoundRect(if (oscuro) Color(0xFF8E86A0) else Color(0xFF8C8A86), Offset(g.x - 2.4f * u, g.y - 0.5f * u), Size(4.8f * u, 1f * u), CornerRadius(0.5f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.6f), Offset(g.x - 2f * u, g.y - 0.25f * u), Offset(g.x + 2f * u, g.y - 0.25f * u), 0.35f * u)
        }
    }

    /**
     * La radio de válvulas de la mesa, de [ancho] con su pie en [pie]: la caja de madera de cantos redondos, la tela de la
     * bocina con sus listones, el cuadrante del dial con su aguja y las dos perillas; de noche, el dial encendido.
     */
    private fun DrawScope.radio(pie: Offset, ancho: Float, oscuro: Boolean) {
        val k = ancho / 24f
        fun p(x: Float, y: Float) = Offset(pie.x + x * k, pie.y - y * k)
        val madera = if (oscuro) Color(0xFF4A2F3E) else Color(0xFF8A5332)
        val caja = Rect(p(-12f, 15f), p(12f, 0f))
        drawRoundRect(sombra(oscuro), Offset(caja.left + 0.6f * k, caja.top + 1.2f * k), caja.size, CornerRadius(3.2f * k))
        // La antena de cable, doblada hacia atrás.
        drawLine(if (oscuro) PAPEL_UV.copy(alpha = 0.5f) else TINTA.copy(alpha = 0.55f), p(7f, 14f), p(11.5f, 21f), 0.6f * k, StrokeCap.Round)
        drawCircle(if (oscuro) FLUOR_NARANJA else TINTA.copy(alpha = 0.7f), 0.8f * k, p(11.5f, 21f))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(madera, Color.White, if (oscuro) 0.08f else 0.22f), madera, mezcla(madera, Color.Black, 0.3f)), caja.top, caja.bottom),
            caja.topLeft, caja.size, CornerRadius(3.2f * k))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.35f), p(-9f, 14.3f), p(9f, 14.3f), 0.5f * k, StrokeCap.Round)
        // La tela de la bocina con sus listones.
        val tela = Rect(p(-10f, 12.5f), p(1f, 2.5f))
        drawRoundRect(if (oscuro) Color(0xFF2B1F33) else Color(0xFFD9C29A), tela.topLeft, tela.size, CornerRadius(1.6f * k))
        for (j in 0..3) {
            val x = tela.left + tela.width * (0.2f + j * 0.2f)
            drawLine(mezcla(madera, Color.Black, 0.15f), Offset(x, tela.top + 0.6f * k), Offset(x, tela.bottom - 0.6f * k), 0.9f * k, StrokeCap.Round)
        }
        // El dial: el cuadrante con sus marcas, la aguja y su luz.
        val dial = p(6.5f, 9f); val rd = 3.3f * k
        if (oscuro) halo(dial, rd * 3f, FLUOR_NARANJA, 0.35f)
        drawCircle(if (oscuro) mezcla(FLUOR_NARANJA, Color.White, 0.2f) else Color(0xFFF4E3B5), rd, dial)
        drawCircle(mezcla(madera, Color.Black, 0.3f), rd, dial, style = Stroke(0.5f * k))
        for (j in 0..4) {
            val a = Math.toRadians(200.0 + j * 35.0)
            drawLine(TINTA.copy(alpha = 0.6f), Offset(dial.x + rd * 0.68f * cos(a).toFloat(), dial.y + rd * 0.68f * sin(a).toFloat()),
                Offset(dial.x + rd * 0.9f * cos(a).toFloat(), dial.y + rd * 0.9f * sin(a).toFloat()), 0.3f * k)
        }
        drawLine(Color(0xFFB3261E), dial, Offset(dial.x + rd * 0.8f * cos(Math.toRadians(300.0)).toFloat(), dial.y + rd * 0.8f * sin(Math.toRadians(300.0)).toFloat()), 0.45f * k, StrokeCap.Round)
        // Las perillas.
        for (x in listOf(3.8f, 9.2f)) {
            drawCircle(mezcla(madera, Color.Black, 0.45f), 1.3f * k, p(x, 3f))
            drawCircle(Color.White.copy(alpha = if (oscuro) 0.15f else 0.35f), 0.45f * k, p(x - 0.4f, 3.4f))
        }
    }

    /**
     * El pandero con sus cintas, de radio [r] en [c]: el aro de madera con sus sonajas de latón en pares, el parche con una
     * margarita pintada y las dos cintas de colores que cuelgan.
     */
    private fun DrawScope.pandero(c: Offset, r: Float, oscuro: Boolean) {
        val u = density
        // Las cintas que cuelgan del aro, ondulando.
        listOf(Pair(-0.25f, if (oscuro) FLUOR_MAGENTA else MAGENTA), Pair(0.2f, if (oscuro) LIMON else VERDE)).forEachIndexed { i, (dx, col) ->
            val x0 = c.x + dx * r; val y0 = c.y + r * 0.9f
            val cinta = Path().apply {
                moveTo(x0, y0)
                cubicTo(x0 + r * 0.35f, y0 + r * 0.4f, x0 - r * 0.35f, y0 + r * 0.75f, x0 + r * (0.12f - i * 0.2f), y0 + r * (1.05f + i * 0.15f))
            }
            if (oscuro) resplandor(cinta, col, 0.8f * u, 0.8f)
            drawPath(cinta, col, style = Stroke(r * 0.16f, cap = StrokeCap.Round))
        }
        drawCircle(sombra(oscuro), r, Offset(c.x + 0.5f * u, c.y + 1f * u))
        val aro = if (oscuro) Color(0xFF6E4A3C) else Color(0xFFB07A45)
        drawCircle(aro, r, c)
        drawCircle(if (oscuro) Color(0xFF2E2238) else Color(0xFFF1E3C4), r * 0.8f, c)
        drawCircle(mezcla(aro, Color.Black, 0.3f), r * 0.8f, c, style = Stroke(0.4f * u))
        // La margarita pintada en el parche.
        TrazosPsicodelia.run {
            margarita(c, r * 0.5f, if (oscuro) FLUOR_MAGENTA else MAGENTA, if (oscuro) LIMON else AMARILLO, 6, 0.3f)
        }
        // Las sonajas: cuatro pares, en sus ventanas del aro.
        for (j in 0 until 4) {
            val a = Math.toRadians(-60.0 + j * 90.0)
            val q = Offset(c.x + r * 0.9f * cos(a).toFloat(), c.y + r * 0.9f * sin(a).toFloat())
            drawCircle(if (oscuro) Color(0xFFD9D0EA) else Color(0xFFC9B26A), r * 0.17f, q)
            drawCircle(Color.White.copy(alpha = 0.6f), r * 0.06f, Offset(q.x - r * 0.05f, q.y - r * 0.05f))
        }
        drawArc(Color.White.copy(alpha = if (oscuro) 0.12f else 0.35f), 200f, 60f, false, Offset(c.x - r * 0.92f, c.y - r * 0.92f), Size(r * 1.84f, r * 1.84f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
    }

    /**
     * La ventana del departamento compartido en [r]: el marco blanco de guillotina con su arco bajo y su repisa; por el vidrio,
     * la calle de enfrente con dos casas victorianas (la gris y la pintada de turquesa). Si no está [despejada], la niebla de
     * la mañana entra por la calle; despejada, la luz de la tarde. De noche, las ventanas de enfrente encendidas (una, con luz
     * negra) y la niebla oscura. En la repisa, una flor en su botella.
     */
    private fun DrawScope.ventana(r: Rect, despejada: Boolean, oscuro: Boolean, semilla: Int) {
        val u = density
        val marco = if (oscuro) Color(0xFF3F3155) else TrazosPsicodelia.CASA_BLANCA
        val m = 3f * u; val arco = minOf(5f * u, r.height * 0.1f)
        val vidrio = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        fun hueco(q: Rect, a: Float) = Path().apply {
            moveTo(q.left, q.bottom); lineTo(q.left, q.top + a); quadraticTo(q.center.x, q.top - a, q.right, q.top + a); lineTo(q.right, q.bottom); close()
        }
        val fuera = hueco(r, arco); val dentro = hueco(vidrio, arco * 0.8f)
        translate(1f * u, 1.6f * u) { drawPath(fuera, sombra(oscuro)) }
        drawPath(fuera, marco)
        // La calle, vista por el vidrio.
        clipPath(dentro) {
            val cielo = when {
                oscuro && despejada -> listOf(Color(0xFF0B0614), Color(0xFF231636))
                oscuro -> listOf(Color(0xFF2A1F3D), Color(0xFF1B1229))
                despejada -> listOf(Color(0xFFF2B98E), TrazosPsicodelia.CIELO_TARDE, Color(0xFFFBE3C8))
                else -> listOf(Color(0xFFDCE2E7), TrazosPsicodelia.NIEBLA)
            }
            drawRect(Brush.verticalGradient(cielo, vidrio.top, vidrio.bottom), vidrio.topLeft, vidrio.size)
            if (despejada && !oscuro) halo(Offset(vidrio.right - vidrio.width * 0.2f, vidrio.top + vidrio.height * 0.3f), vidrio.width * 0.5f, Color(0xFFFFE9B0), 0.6f)
            val ancho = vidrio.width * 0.66f; val alto = vidrio.height * 0.92f
            val calle = vidrio.bottom - 1f * u
            val luzVentana = if (oscuro) Color(0xFFE8B866) else Color(0xFF5E6B78)
            val gris = if (oscuro) mezcla(TrazosPsicodelia.CASA_GRIS, SALA, 0.72f) else TrazosPsicodelia.CASA_GRIS
            val pintada = if (oscuro) mezcla(TrazosPsicodelia.CASA_TURQUESA, SALA, 0.62f) else TrazosPsicodelia.CASA_TURQUESA
            val adorno = if (oscuro) mezcla(TrazosPsicodelia.CASA_BLANCA, SALA, 0.6f) else TrazosPsicodelia.CASA_BLANCA
            TrazosPsicodelia.run {
                casaVictoriana(Offset(vidrio.left + vidrio.width * 0.18f, calle), ancho, alto, gris, adorno, luzVentana)
                casaVictoriana(Offset(vidrio.left + vidrio.width * 0.9f, calle + alto * 0.02f), ancho, alto * 1.04f, pintada,
                    if (oscuro) mezcla(TrazosPsicodelia.CASA_ROSA, SALA, 0.5f) else TrazosPsicodelia.CASA_ROSA, if (oscuro) FLUOR_VIOLETA else luzVentana)
            }
            if (oscuro) halo(Offset(vidrio.left + vidrio.width * 0.9f - ancho * 0.12f, calle - alto * 0.5f), ancho * 0.5f, FLUOR_VIOLETA, 0.25f)
            if (!despejada) {
                // La niebla: un velo y unos jirones que cruzan la calle.
                val niebla = if (oscuro) Color(0xFF4A3B66) else TrazosPsicodelia.NIEBLA
                drawRect(niebla.copy(alpha = if (oscuro) 0.4f else 0.55f), vidrio.topLeft, vidrio.size)
                for (j in 0 until 4) {
                    val c = Offset(vidrio.left + vidrio.width * azar(semilla + 3, j), vidrio.top + vidrio.height * (0.3f + 0.6f * azar(semilla + 4, j)))
                    val rx = vidrio.width * (0.5f + 0.3f * azar(semilla + 5, j)); val ry = vidrio.height * 0.06f
                    scale(1f, ry / rx, c) { halo(c, rx, if (oscuro) niebla else Color.White, if (oscuro) 0.45f else 0.6f) }
                }
            }
            // El reflejo del vidrio.
            drawPath(Path().apply {
                moveTo(vidrio.left + vidrio.width * 0.55f, vidrio.top); lineTo(vidrio.left + vidrio.width * 0.72f, vidrio.top)
                lineTo(vidrio.left + vidrio.width * 0.38f, vidrio.bottom); lineTo(vidrio.left + vidrio.width * 0.21f, vidrio.bottom); close()
            }, Color.White.copy(alpha = if (oscuro) 0.04f else 0.16f))
        }
        // El travesaño de la guillotina y el filo de luz del marco.
        val yRiel = vidrio.top + vidrio.height * 0.48f
        drawRect(marco, Offset(vidrio.left, yRiel - 1.2f * u), Size(vidrio.width, 2.4f * u))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(vidrio.left, yRiel + 1.2f * u), Size(vidrio.width, 0.8f * u))
        drawPath(dentro, mezcla(marco, Color.Black, if (oscuro) 0.3f else 0.18f), style = Stroke(0.6f * u))
        // La repisa, con su sombra, y la flor en su botella.
        val repisa = Rect(r.left - 3f * u, r.bottom - 0.4f * u, r.right + 3f * u, r.bottom + 2.6f * u)
        drawRect(sombra(oscuro), Offset(repisa.left + 1f * u, repisa.bottom), Size(repisa.width, 1.6f * u))
        drawRect(Brush.verticalGradient(listOf(mezcla(marco, Color.White, 0.2f), mezcla(marco, Color.Black, 0.15f)), repisa.top, repisa.bottom), repisa.topLeft, repisa.size)
        val pie = Offset(r.left + r.width * 0.2f, repisa.top)
        val hb = minOf(9f * u, r.height * 0.18f)
        if (hb > 4f * u) {
            val vidrioB = if (oscuro) Color(0xFF4B6B5A).copy(alpha = 0.8f) else Color(0xFF7FAE95).copy(alpha = 0.8f)
            drawRoundRect(vidrioB, Offset(pie.x - hb * 0.22f, pie.y - hb * 0.6f), Size(hb * 0.44f, hb * 0.6f), CornerRadius(hb * 0.12f))
            drawRect(vidrioB, Offset(pie.x - hb * 0.08f, pie.y - hb * 0.85f), Size(hb * 0.16f, hb * 0.28f))
            drawLine(if (oscuro) LIMON.copy(alpha = 0.8f) else Color(0xFF3F8A56), Offset(pie.x, pie.y - hb * 0.7f), Offset(pie.x + hb * 0.12f, pie.y - hb * 1.3f), 0.6f * u, StrokeCap.Round)
            TrazosPsicodelia.run { flor(Offset(pie.x + hb * 0.14f, pie.y - hb * 1.45f), hb * 0.36f, if (oscuro) FLUOR_MAGENTA else MAGENTA, if (oscuro) LIMON else VERDE, 6, 0.4f) }
        }
    }

    /**
     * El retroproyector del show de luces, con su pie en [pie] y de [alto]: la caja con el vidrio de arriba, donde va el plato
     * de reloj con el aceite y la tinta de colores, el brazo y la cabeza con su lente; [luz] (0 a 1) enciende el haz que sube
     * hacia la pared.
     */
    private fun DrawScope.retroproyector(pie: Offset, alto: Float, luz: Float) {
        val k = alto / 30f
        fun p(x: Float, y: Float) = Offset(pie.x + x * k, pie.y - y * k)
        val cuerpo = Color(0xFF2D2338); val filo = Color(0xFF5A4A72)
        // El haz: un abanico de luz que sale de la lente hacia arriba a la izquierda y se apaga.
        val lente = p(-3f, 27f)
        if (luz > 0f) {
            val haz = Path().apply {
                moveTo(lente.x, lente.y - 1.2f * k); lineTo(lente.x - 70f * k, lente.y - 44f * k); lineTo(lente.x - 34f * k, lente.y + 18f * k); lineTo(lente.x, lente.y + 1.4f * k); close()
            }
            drawPath(haz, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.16f * luz), Color.White.copy(alpha = 0f)), lente, Offset(lente.x - 52f * k, lente.y - 14f * k)))
        }
        // La caja, el vidrio de arriba con el plato de aceite y el canto.
        drawRoundRect(Color.Black.copy(alpha = 0.5f), p(-13f, 9f), Size(28f * k, 10f * k), CornerRadius(1.6f * k))
        drawRoundRect(cuerpo, p(-14f, 10f), Size(28f * k, 10f * k), CornerRadius(1.6f * k))
        drawRect(filo, p(-14f, 10f), Size(28f * k, 0.8f * k))
        val plato = p(-1f, 11.2f)
        drawOval(Color.White.copy(alpha = 0.18f), Offset(plato.x - 7f * k, plato.y - 1.6f * k), Size(14f * k, 3.2f * k))
        drawOval(LIMON.copy(alpha = 0.8f * maxOf(luz, 0.4f)), Offset(plato.x - 5.6f * k, plato.y - 1.1f * k), Size(6f * k, 2f * k))
        drawOval(FLUOR_MAGENTA.copy(alpha = 0.85f * maxOf(luz, 0.4f)), Offset(plato.x - 0.6f * k, plato.y - 1.2f * k), Size(5.4f * k, 2.2f * k))
        // El brazo y la cabeza con su lente.
        drawLine(filo, p(9f, 10f), p(9f, 27f), 1.6f * k, StrokeCap.Round)
        drawLine(cuerpo, p(9f, 27f), p(0f, 27f), 2.4f * k, StrokeCap.Round)
        drawRoundRect(cuerpo, p(-4f, 30f), Size(7f * k, 5f * k), CornerRadius(1.2f * k))
        drawCircle(if (luz > 0f) mezcla(Color.White, LIMON, 0.3f) else filo, 1.3f * k, lente)
        if (luz > 0f) halo(lente, 6f * k, Color.White, 0.35f * luz)
        // La perilla del foco.
        drawCircle(filo, 0.9f * k, p(11f, 5f))
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9D90AE) else Color(0xFF6E6276)
    override fun rotuloCampo(oscuro: Boolean): Color = magenta(oscuro)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF8A80) else Color(0xFFB3261E)

    /**
     * La cartela de un cartel que se derrite, como los botones de la hoja rápida: el papel con el canto de arriba combado, la
     * trama de imprenta en la esquina y dos gotas que se escurren del canto de abajo, impreso otra vez un poco corrido abajo a la
     * derecha en el color que vibra con el primero (como el cartel que sale de la prensa con el registro corrido). El renglón es
     * una raya de pincel que nace gruesa y se afina. Con el foco, la
     * impresión corrida y la raya toman la tinta de arcoíris del tintero partido, el contorno el magenta (de noche, el verde
     * limón con su resplandor) y una margarita se pega en la esquina, como una calcomanía; con un error, todo se vuelve rojo;
     * apagado, gris y la raya de puntos.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 20 * u) return
        val foco = e.enfocado && e.habilitado
        val gris = if (oscuro) Color(0xFF2A2236) else Color(0xFFCFC8BE)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, gris, 0.6f)
        val caja = Rect(1.4f * u, 2.4f * u, w - 3f * u, h - 1.6f * u)
        val forma = botonGoteaPsi(caja, u, 0.86f, 0.2f)
        // La impresión corrida, abajo a la derecha.
        translate(2.2f * u, 2f * u) {
            when {
                !e.habilitado -> drawPath(forma, sombra(oscuro))
                e.error -> drawPath(forma, errorCampo(oscuro).copy(alpha = if (oscuro) 0.35f else 0.3f))
                foco -> drawPath(forma, TrazosPsicodelia.arcoiris(Offset(0f, 0f), Offset(w, h), if (oscuro) 0.8f else 0.75f, oscuro))
                else -> drawPath(forma, if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.26f) else VERDE.copy(alpha = 0.34f))
            }
        }
        if (foco && oscuro) resplandor(forma, LIMON, 1.2f * u, 0.8f)
        drawPath(forma, a(if (foco && !oscuro) Color.White else papel(oscuro)))
        clipPath(forma) {
            // La trama de imprenta que asoma en la esquina de arriba a la derecha, y la luz de la ventana (de día).
            tramaPsi(Rect(w * 0.5f, caja.top, caja.right, caja.bottom), 3.2f * u, a(if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.16f) else MAGENTA.copy(alpha = 0.11f)),
                Offset(caja.right, caja.top), minOf(w * 0.42f, 130 * u))
            if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.Transparent), caja.top, caja.top + caja.height * 0.5f))
        }
        val linea = when {
            e.error -> errorCampo(oscuro)
            foco -> primera(oscuro)
            else -> a(if (oscuro) Color(0xFF6B5A86) else TINTA.copy(alpha = 0.5f))
        }
        drawPath(forma, linea, style = Stroke((if (foco || e.error) 1.6f else 1.1f) * u))
        // El renglón: la raya de pincel.
        val izq = margenCampoX.toPx() - 2 * u; val der = w - margenCampoX.toPx()
        val raya: Brush = when {
            e.error -> SolidColor(errorCampo(oscuro))
            foco -> TrazosPsicodelia.arcoiris(Offset(izq, 0f), Offset(der, 0f), 1f, oscuro)
            else -> SolidColor(if (oscuro) Color(0xFF8C7BA8) else TINTA.copy(alpha = 0.5f))
        }
        fun renglon(y: Float) {
            if (der - izq < 24 * u) return
            if (!e.habilitado) {
                drawLine(tenue(oscuro).copy(alpha = 0.4f), Offset(izq, y), Offset(der, y), 0.9f * u, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(0.4f * u, 2.6f * u)))
                return
            }
            val g = (if (foco) 1.4f else 0.95f) * u
            drawPath(cintaPsi(izq, der, y, g * 0.45f, g, g * 0.3f), raya)
        }
        if (e.multilinea) renglonesPsi(margenCampoY) { _, y -> renglon(y - 1 * u) } else renglon(h - 11.5f * u)
        // Con el foco, la margarita pegada en la esquina de arriba, como calcomanía.
        if (foco && w > 120 * u) TrazosPsicodelia.run {
            flor(Offset(caja.right - 14 * u, caja.top + 6.2f * u), 6f * u, if (oscuro) FLUOR_MAGENTA else MAGENTA, if (oscuro) LIMON else VERDE, 6, 0.6f)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * Un hilo de tinta que nace finísimo en cada orilla y engorda al acercarse al medio, donde se junta en tres gotas de aceite
     * que se tocan, como en el vidrio del retroproyector: el magenta entre su verde y su ámbar (de noche, las tintas de la luz
     * negra, que se suman como luz). Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val linea = (if (oscuro) Color(0xFF8A78B0) else TINTA.copy(alpha = 0.42f)).let { it.copy(alpha = it.alpha * f) }
        if (cx - 16 * u > 8 * u) for (s in listOf(-1f, 1f)) {
            val x0 = if (s < 0) 3 * u else cx + 11 * u; val x1 = if (s < 0) cx - 11 * u else w - 3 * u
            val pincel = if (s < 0) Brush.horizontalGradient(0f to linea.copy(alpha = 0f), 0.35f to linea, 1f to linea, startX = x0, endX = x1)
            else Brush.horizontalGradient(0f to linea, 0.65f to linea, 1f to linea.copy(alpha = 0f), startX = x0, endX = x1)
            drawPath(cintaPsi(x0, x1, y, if (s < 0) 0.2f * u else 1.6f * u, 0.8f * u, if (s < 0) 1.6f * u else 0.2f * u), pincel)
        }
        nubeAceite(Offset(cx, y), 1.2f * u, oscuro, f)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = primera(oscuro)

    /**
     * El trazo de pincel bajo la palabra, que nace grueso a la izquierda, se afina y deja colgar una gota. Al presionarlo se
     * enciende detrás de la palabra una gota de luz líquida, impresa dos veces en colores que vibran; apagado, una raya de puntos.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = minOf(12 * u, w * 0.18f); val der = w - izq
        val y = h / 2 + 11 * u
        if (der - izq < 10 * u) return
        if (!e.habilitado) {
            drawLine(tenue(oscuro).copy(alpha = 0.4f), Offset(izq, y), Offset(der, y), 0.9f * u, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(0.4f * u, 2.6f * u)))
            return
        }
        if (e.presionado && h > 20 * u) {
            val c = Offset(w / 2, h / 2)
            val rx = maxOf((der - izq) * 0.58f, 20 * u); val ry = h * 0.4f
            scale(1f, ry / rx, c) {
                val gota = TrazosPsicodelia.gota(c, rx, 0.2f, 0.07f, 3, 48)
                translate(1.6f * u, 1.6f * u * rx / ry) { drawPath(gota, if (oscuro) FLUOR_MAGENTA.copy(alpha = 0.2f) else VERDE.copy(alpha = 0.3f)) }
                drawPath(gota, if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.22f) else Color(0xFFF6D3E4))
            }
        }
        val color = tintaBotonTexto(oscuro).copy(alpha = if (e.presionado) 1f else 0.62f)
        if (der - izq < 44 * u) {
            drawPath(cintaPsi(izq, der, y, 1.4f * u, 1.1f * u, 0.4f * u), color)
            return
        }
        val x0 = izq + 4 * u; val x1 = der - 4 * u
        val g = (if (e.presionado) 1.9f else 1.5f) * u
        drawPath(cintaPsi(x0, x1, y, g, g * 0.75f, 0.3f * u), color)
        drawPath(gotaPsi(x0 + (x1 - x0) * 0.3f, y - g * 0.3f, 5.4f * u, 1.5f * u), color)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 22.dp

    /**
     * Una pista de la lista del reverso del disco: el surco fino al pie, que se pierde a la derecha, y, a la izquierda, un disco
     * chico con su etiqueta de color (si la fila lleva detalle, el disco asoma de su funda, con su margarita en la portada). Al
     * tocarla, la etiqueta se enciende y su luz líquida se derrama por la fila.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val etiqueta = cartel(oscuro, e.semilla)
        if (e.presionado) {
            drawRect(Brush.horizontalGradient(listOf(etiqueta.copy(alpha = if (oscuro) 0.1f else 0.12f), Color.Transparent), 0f, w))
            drawRect(Brush.radialGradient(listOf(etiqueta.copy(alpha = if (oscuro) 0.22f else 0.2f), Color.Transparent), Offset(12 * u, cy), 110 * u))
        }
        // El surco del pie.
        val yl = h - 1.2f * u
        val x0 = margenFila.toPx() - 2 * u
        if (w - x0 > 30 * u) {
            val l = if (oscuro) Color(0xFF4E3D69) else mezcla(Color(0xFFB9AC9D), PAPEL, 0.1f)
            drawLine(Brush.horizontalGradient(0f to l, 0.7f to l, 1f to l.copy(alpha = 0f), startX = x0, endX = w - 2 * u), Offset(x0, yl), Offset(w - 2 * u, yl), 0.8f * u)
            drawLine(Brush.horizontalGradient(0f to Color.White.copy(alpha = if (oscuro) 0.05f else 0.55f), 1f to Color.Transparent, startX = x0, endX = w * 0.7f),
                Offset(x0, yl + 0.8f * u), Offset(w * 0.7f, yl + 0.8f * u), 0.5f * u)
            drawCircle(l, 1.1f * u, Offset(x0, yl))
        }
        if (h < 16 * u) return
        val luz = if (e.presionado) 1f else 0f
        if (e.conDetalle && h >= 30 * u) {
            // El disco que asoma de su funda.
            val r = 6.6f * u
            val c = Offset(14.6f * u, cy)
            discoChico(c, r, etiqueta, oscuro, luz)
            val funda = Rect(0.6f * u, cy - 6.8f * u, 13.6f * u, cy + 6.8f * u)
            val portada = mezcla(TrazosPsicodelia.complementario(etiqueta), if (oscuro) SALA else PAPEL, if (oscuro) 0.45f else 0.3f)
            drawRect(sombra(oscuro), Offset(funda.left + 0.6f * u, funda.top + 0.9f * u), funda.size)
            drawRect(portada, funda.topLeft, funda.size)
            TrazosPsicodelia.run { margarita(funda.center, 4.2f * u, etiqueta, if (oscuro) SALA else PAPEL, 8) }
            drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), funda.topLeft, Size(funda.width, funda.height * 0.3f))
            drawRect(mezcla(portada, Color.Black, 0.3f), funda.topLeft, funda.size, style = Stroke(0.4f * u))
        } else discoChico(Offset(10.6f * u, cy), 6.2f * u, etiqueta, oscuro, luz)
    }

    /** La flecha de letra burbuja: gorda y blanda, impresa otra vez un poco corrida en el color que vibra con ella, con su brillo. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val s = size.minDimension
        val c = Offset(size.width / 2 - s * 0.03f, size.height / 2 - s * 0.03f)
        val p = flechaPsi(c, s * 0.84f)
        translate(s * 0.07f, s * 0.07f) { drawPath(p, contra(oscuro).copy(alpha = if (oscuro) 0.75f else 0.7f)) }
        if (oscuro) resplandor(p, LIMON, s * 0.04f, 0.8f)
        drawPath(p, primera(oscuro))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.35f else 0.45f), Offset(c.x - s * 0.27f, c.y - s * 0.05f), Offset(c.x - s * 0.04f, c.y - s * 0.05f), s * 0.045f, StrokeCap.Round)
    }

    /**
     * El botón de solapa: fijo, el botón de verdad (la orla de franjas torcidas en naranja y azul, el disco verde y la margarita
     * que vibra), con su resplandor de noche; sin fijar, solo su contorno y la margarita en línea, sin tinta.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2)
        if (fija) {
            if (oscuro) halo(c, s * 0.58f, LIMON, 0.3f)
            TrazosPsicodelia.run { emblema(c, s * 0.45f, NARANJA, VERDE) }
        } else {
            val col = tenue(oscuro).copy(alpha = 0.6f)
            drawCircle(col, s * 0.4f, c, style = Stroke(s * 0.05f))
            drawCircle(col, s * 0.31f, c, style = Stroke(s * 0.025f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(s * 0.05f, s * 0.05f))))
            drawPath(TrazosPsicodelia.petalos(c, s * 0.25f, 8, -PI.toFloat() / 8, 0.25f, 0.1f, unidos = true), col, style = Stroke(s * 0.035f, join = StrokeJoin.Round))
        }
    }

    override val tamDibujoVacio: Dp = 48.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFB4A7C4) else Color(0xFF564A60)

    /**
     * La pared de tablas de la casa de la esquina donde se pegan los carteles, ya sin ninguno: la tablazón gris con su luz y su
     * sombra, y en dos esquinas lo que quedó de los carteles arrancados (los aros de un sol en magenta y verde arriba, las
     * franjas onduladas en naranja y azul abajo), con su orilla rota y su grapa. De noche, la misma pared con luz negra: la
     * madera se hunde y los restos de tinta fluorescente brillan.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 40 * u) return
        val r = Rect(0.8f * u, 1.2f * u, w - 2.2f * u, h - 2.8f * u)
        val forma = infladoPsi(r, 16 * u, 1f * u)
        val madera = if (oscuro) Color(0xFF1F1630) else mezcla(TrazosPsicodelia.CASA_BLANCA, TrazosPsicodelia.CASA_GRIS, 0.32f)
        translate(1.2f * u, 2f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, madera)
        clipPath(forma) {
            // La tablazón: cada tabla con su sombra abajo y la luz en el canto de arriba, y alguna veta.
            val paso = 10 * u
            var y = r.top + paso; var k = 0
            while (y < r.bottom && k < 80) {
                drawLine(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(r.left, y), Offset(r.right, y), 1.1f * u)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.035f else 0.4f), Offset(r.left, y + 1f * u), Offset(r.right, y + 1f * u), 0.6f * u)
                y += paso; k++
            }
            val vetas = Path()
            for (j in 0 until 10) {
                val vy = r.top + r.height * azar(e.semilla + 31, j); val vx = r.left + r.width * azar(e.semilla + 32, j)
                val l = (30f + 60f * azar(e.semilla + 33, j)) * u
                vetas.moveTo(vx, vy); vetas.cubicTo(vx + l * 0.3f, vy - 1.2f * u, vx + l * 0.6f, vy + 1.2f * u, vx + l, vy)
            }
            drawPath(vetas, Color.Black.copy(alpha = if (oscuro) 0.2f else 0.06f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
            if (!oscuro) drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.06f)), Offset.Zero, Offset(w, h)))
            // Lo que quedó de los carteles arrancados.
            val roto = if (oscuro) PAPEL_UV.copy(alpha = 0.7f) else Color(0xFFF7F1E6)
            restoCartel(Offset(r.left, r.top), 1f, 1f, minOf(54 * u, w * 0.2f), minOf(40 * u, h * 0.36f), if (oscuro) LIMON else MAGENTA,
                if (oscuro) FLUOR_MAGENTA else VERDE, roto, e.semilla + 5, false, oscuro)
            restoCartel(Offset(r.right, r.bottom), -1f, -1f, minOf(62 * u, w * 0.22f), minOf(34 * u, h * 0.32f), if (oscuro) FLUOR_NARANJA else NARANJA,
                if (oscuro) FLUOR_VIOLETA else AZUL, roto, e.semilla + 9, true, oscuro)
            // Una tira de papel que quedó bajo una grapa, arriba a la derecha.
            val g = Offset(r.right - minOf(46 * u, w * 0.16f), r.top + 12 * u)
            rotate(10f, g) {
                drawPath(Path().apply {
                    moveTo(g.x - 3.4f * u, g.y - 1.6f * u); lineTo(g.x + 3.4f * u, g.y - 1.6f * u); lineTo(g.x + 3.2f * u, g.y + 4.4f * u)
                    lineTo(g.x + 1.8f * u, g.y + 3.4f * u); lineTo(g.x + 0.6f * u, g.y + 6.2f * u); lineTo(g.x - 1f * u, g.y + 4f * u); lineTo(g.x - 3.4f * u, g.y + 5f * u); close()
                }, roto)
                drawRect(if (oscuro) FLUOR_NARANJA.copy(alpha = 0.7f) else NARANJA.copy(alpha = 0.8f), Offset(g.x - 3.4f * u, g.y + 2.2f * u), Size(6.6f * u, 1.4f * u))
            }
            drawRoundRect(if (oscuro) Color(0xFF8E86A0) else Color(0xFF8C8A86), Offset(g.x - 2.4f * u, g.y - 0.5f * u), Size(4.8f * u, 1f * u), CornerRadius(0.5f * u))
        }
        drawPath(forma, borde(oscuro), style = Stroke(0.9f * u))
    }

    /**
     * Lo vacío: la funda de un disco sin su disco. La portada, un poco ladeada, con la flor amarilla que vibra sobre el violeta y
     * tres franjas de arcoíris; detrás asoma la funda de papel con su agujero redondo, por donde no se ve ningún disco. De noche,
     * la portada brilla con la luz negra.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val s = size.minDimension
        val u = density
        val lado = s * 0.66f
        val x0 = size.width / 2 - s * 0.45f
        val portada = Rect(x0, s * 0.24f, x0 + lado, s * 0.24f + lado)
        // La funda de papel, que asoma a la derecha, con su agujero.
        val interior = Rect(portada.left + s * 0.24f, portada.top - s * 0.1f, portada.left + s * 0.24f + lado * 0.95f, portada.top - s * 0.1f + lado * 0.95f)
        rotate(7f, interior.center) {
            val funda = Path().apply {
                fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                addRoundRect(RoundRect(interior, CornerRadius(s * 0.02f)))
                addOval(Rect(interior.center, lado * 0.12f))
            }
            translate(s * 0.012f, s * 0.025f) { drawPath(funda, sombra(oscuro)) }
            drawPath(funda, if (oscuro) Color(0xFF3A2E52) else Color(0xFFF4EEE4))
            drawPath(funda, if (oscuro) PAPEL_UV.copy(alpha = 0.4f) else Color(0xFFB9AC9D), style = Stroke(maxOf(0.5f * u, s * 0.012f)))
            drawArc(if (oscuro) PAPEL_UV.copy(alpha = 0.3f) else Color(0xFFB9AC9D), 90f, 180f, false, Offset(interior.center.x - lado * 0.12f, interior.center.y - lado * 0.12f),
                Size(lado * 0.24f, lado * 0.24f), style = Stroke(maxOf(0.5f * u, s * 0.01f)))
        }
        // La portada.
        rotate(-5f, portada.center) {
            translate(s * 0.015f, s * 0.03f) { drawRect(sombra(oscuro), portada.topLeft, portada.size) }
            val fondo = if (oscuro) Color(0xFF2A1745) else VIOLETA
            drawRect(fondo, portada.topLeft, portada.size)
            clipPath(Path().apply { addRect(portada) }) {
                // Las franjas de arcoíris que salen de la esquina de arriba.
                val franjas = if (oscuro) listOf(FLUOR_NARANJA, FLUOR_MAGENTA, LIMON) else listOf(NARANJA, MAGENTA, AMARILLO)
                franjas.forEachIndexed { i, c ->
                    val r = lado * (0.42f - i * 0.1f)
                    drawCircle(c, r, Offset(portada.left, portada.top), style = Stroke(lado * 0.06f))
                }
                TrazosPsicodelia.run {
                    flor(Offset(portada.left + lado * 0.64f, portada.top + lado * 0.66f), lado * 0.3f, if (oscuro) LIMON else AMARILLO, if (oscuro) FLUOR_MAGENTA else Color(0xFFB9F27A), 7, 0.6f)
                }
                drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), Color.Transparent), portada.topLeft, portada.bottomRight), portada.topLeft, portada.size)
            }
            if (oscuro) resplandor(Path().apply { addRect(portada) }, FLUOR_VIOLETA, s * 0.01f, 0.6f)
            drawRect(Color.Black.copy(alpha = 0.25f), portada.topLeft, portada.size, style = Stroke(maxOf(0.5f * u, s * 0.01f)))
        }
    }

    override val margenAviso: Dp = 38.dp
    override fun tintaAviso(oscuro: Boolean): Color = tinta(oscuro)

    /**
     * El aviso es un recorte del periódico del barrio: el papel inflado con la orilla izquierda manchada por la tinta de arcoíris
     * del tintero partido y un poco de trama. En el margen, según el aviso, la radio de válvulas con su dial (la radio de noche;
     * de noche, el dial encendido) o el pandero con su margarita pintada y sus cintas.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 20 * u) return
        val forma = infladoPsi(Rect(0.6f * u, 1f * u, w - 0.6f * u, h - 1f * u), 12 * u, 0.8f * u)
        drawPath(forma, papel(oscuro))
        clipPath(forma) {
            // La orilla de la tinta de arcoíris, de arriba abajo, que se esfuma hacia el papel.
            val tintas = if (oscuro) TrazosPsicodelia.ARCOIRIS_NOCHE else TrazosPsicodelia.ARCOIRIS
            drawRect(Brush.verticalGradient(tintas, 0f, h), Offset.Zero, Size(4.4f * u, h))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, papel(oscuro)), 2f * u, 7f * u), Offset(2f * u, 0f), Size(5f * u, h))
            tramaPsi(Rect(5 * u, 0f, 40 * u, h), 3f * u, if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.12f) else MAGENTA.copy(alpha = 0.09f), Offset(5 * u, h), 46 * u)
        }
        drawPath(forma, borde(oscuro), style = Stroke(0.8f * u))
        val cy = minOf(h / 2, 21 * u)
        if (e.semilla % 2 == 0) radio(Offset(20.5f * u, cy + 7 * u), 22 * u, oscuro)
        else pandero(Offset(20.5f * u, cy - 2.4f * u), 8.4f * u, oscuro)
    }

    override val margenCifra: Dp = 12.dp
    override fun tintaCifra(oscuro: Boolean): Color = tinta(oscuro)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) FLUOR_VIOLETA else Color(0xFFA84B0A)

    /**
     * El boleto del baile: la cartulina con sus esquinas redondas, el talón a la derecha impreso con la tinta de arcoíris, la
     * línea de picado con sus dos muescas, y en la cartulina, apenas, una margarita de contorno que vibra.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 20 * u) return
        val r = Rect(0.4f * u, 0.4f * u, w - 1.2f * u, h - 1.8f * u)
        val xp = r.right - 9.2f * u
        val base = Path().apply { addRoundRect(RoundRect(r, CornerRadius(8 * u))) }
        val muescas = Path().apply { addOval(Rect(Offset(xp, r.top), 3f * u)); addOval(Rect(Offset(xp, r.bottom), 3f * u)) }
        val boleto = Path.combine(PathOperation.Difference, base, muescas)
        translate(0.8f * u, 1.4f * u) { drawPath(boleto, sombra(oscuro)) }
        val carton = if (oscuro) PAPEL_NOCHE else Color(0xFFFBF2E6)
        drawPath(boleto, carton)
        clipPath(boleto) {
            if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.Transparent), 0f, h * 0.5f))
            // La margarita de contorno, grande y tenue, abajo a la izquierda del talón.
            val c = Offset(xp - 12 * u, r.bottom - 6 * u); val rf = minOf(h * 0.42f, 24 * u)
            val petalos = TrazosPsicodelia.petalos(c, rf, 7, 0.3f, 0.3f, 0.1f, unidos = true)
            translate(1.2f * u, 1.2f * u) { drawPath(petalos, (if (oscuro) LIMON else VERDE).copy(alpha = if (oscuro) 0.14f else 0.16f), style = Stroke(1.2f * u)) }
            drawPath(petalos, (if (oscuro) FLUOR_MAGENTA else MAGENTA).copy(alpha = if (oscuro) 0.2f else 0.14f), style = Stroke(1.2f * u))
            // El talón, impreso con la tinta de arcoíris.
            val tintas = if (oscuro) TrazosPsicodelia.ARCOIRIS_NOCHE else TrazosPsicodelia.ARCOIRIS
            drawRect(Brush.verticalGradient(tintas.map { it.copy(alpha = if (oscuro) 0.75f else 0.8f) }, r.top, r.bottom), Offset(xp, r.top), Size(r.right - xp, r.height))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.2f), Offset(xp, r.top), Size(r.right - xp, r.height * 0.4f))
        }
        // La línea de picado.
        drawLine(if (oscuro) PAPEL_UV.copy(alpha = 0.45f) else TINTA.copy(alpha = 0.35f), Offset(xp, r.top + 3.6f * u), Offset(xp, r.bottom - 3.6f * u), 0.8f * u,
            StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(0.1f * u, 2.2f * u)))
        drawPath(boleto, borde(oscuro), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /**
     * El fondo donde va el texto de cada tarjeta: el papel del cartel para la cifra; la pared del departamento para lo que falta;
     * la sala de baile con luz negra (oscura también de día) para la noche; el papel del periódico para Vida.
     */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF1C1229) else Color(0xFFFBF6EE)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF181022) else Color(0xFFF1EEEA)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0E0716) else Color(0xFF1A0F27)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF1D1428) else Color(0xFFF4ECDD)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> tinta(oscuro)
        TipoTarjeta.PENDIENTE -> tinta(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFF2EAF7)
        TipoTarjeta.VIDA -> tinta(oscuro)
    }

    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> tenue(oscuro)
        TipoTarjeta.PENDIENTE -> tenue(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFBDB0CE)
        TipoTarjeta.VIDA -> tenue(oscuro)
    }

    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> primera(oscuro)
        TipoTarjeta.PENDIENTE -> if (oscuro) FLUOR_MAGENTA else Color(0xFF2E7344)
        TipoTarjeta.NOCHE -> LIMON
        TipoTarjeta.VIDA -> if (oscuro) FLUOR_VIOLETA else Color(0xFFA84B0A)
    }

    /**
     * Las tarjetas de la mesa, del departamento, de la sala y de la calle. La cifra de Hoy es el **cartel de la semana** pegado
     * con dos tiras de cinta, con su trama de imprenta; destacada, el contorno vibra en dos colores y florece una margarita a
     * la derecha. «Te falta hoy» es **la ventana del departamento**: por el vidrio, la calle con dos casas victorianas (la gris
     * y la pintada) y la niebla de la mañana, que todavía no se va; completa, la niebla se despeja y entra la luz de la tarde
     * (de noche, las ventanas de enfrente encendidas). La noche es **la sala de baile con luz negra**: las luces líquidas en la
     * pared, la gente a contraluz y el retroproyector que las echa; sellada, el haz se enciende y las luces crecen. Vida es **el
     * periódico del barrio**, con su franja de tinta de arcoíris arriba, sus columnas y su foto de trama; destacada, se pega una
     * margarita en la esquina. Al tocarlas se oscurecen.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 24 * u || size.height < 24 * u) return
        val base = fondoTarjeta(e.tipo, oscuro).let { if (e.presionado) mezcla(it, if (oscuro) Color.Black else Color(0xFF5A3F6A), 0.12f) else it }
        when (e.tipo) {
            TipoTarjeta.METRICA -> tarjetaCartel(e, base, oscuro)
            TipoTarjeta.PENDIENTE -> tarjetaVentana(e, base, oscuro)
            TipoTarjeta.NOCHE -> tarjetaSala(e, base)
            TipoTarjeta.VIDA -> tarjetaPeriodico(e, base, oscuro)
        }
    }

    /** Una tira de cinta de papel, de [largo] y [ancho], centrada en [c] y girada [grados]: translúcida, con sus cortes en zigzag. */
    private fun DrawScope.cinta(c: Offset, largo: Float, ancho: Float, grados: Float, oscuro: Boolean) {
        val u = density
        rotate(grados, c) {
            val l = largo / 2; val a = ancho / 2; val z = 0.9f * u
            val p = Path().apply {
                moveTo(c.x - l, c.y - a)
                lineTo(c.x + l, c.y - a)
                for (k in 0..4) lineTo(c.x + l + (if (k % 2 == 0) z else 0f), c.y - a + ancho * (k + 1) / 5f)
                lineTo(c.x - l, c.y + a)
                for (k in 0..4) lineTo(c.x - l - (if (k % 2 == 0) z else 0f), c.y + a - ancho * (k + 1) / 5f)
                close()
            }
            drawPath(p, if (oscuro) Color(0xFF5B4C70).copy(alpha = 0.6f) else Color(0xFFE6D3A3).copy(alpha = 0.85f))
            drawPath(p, if (oscuro) Color.Black.copy(alpha = 0.25f) else Color(0xFFB59F6A).copy(alpha = 0.45f), style = Stroke(0.4f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(c.x - l, c.y - a + 0.8f * u), Offset(c.x + l, c.y - a + 0.8f * u), 0.6f * u)
        }
    }

    /** El cartel de la semana, de la cifra de Hoy (ver [tarjeta]). */
    private fun DrawScope.tarjetaCartel(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(1f * u, 1.4f * u, w - 2.4f * u, h - 2.8f * u)
        val forma = infladoPsi(r, 14 * u, 1f * u)
        val acento = acentoTarjeta(e.tipo, oscuro)
        // La impresión corrida, que vibra en el color opuesto (destacada, más fuerte).
        translate(1.4f * u, 1.4f * u) {
            drawPath(forma, if (e.destacado) contra(oscuro).copy(alpha = if (oscuro) 0.55f else 0.5f) else sombra(oscuro))
        }
        if (e.destacado && oscuro) resplandor(forma, acento, 1.2f * u, 0.8f)
        drawPath(forma, base)
        clipPath(forma) {
            tramaPsi(Rect(w * 0.45f, h * 0.35f, w, h), 3.4f * u, if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.13f) else MAGENTA.copy(alpha = 0.09f), Offset(w, h), minOf(w * 0.6f, 130 * u))
            if (!oscuro) drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.4f), Color.Transparent), Offset.Zero, Offset(w * 0.6f, h)))
        }
        drawPath(forma, if (e.destacado) acento else borde(oscuro), style = Stroke((if (e.destacado) 1.6f else 0.9f) * u))
        if (e.destacado && w > 110 * u && h > 70 * u) {
            // La margarita que florece a la derecha, con su tallo y su hoja.
            val c = Offset(r.right - 18 * u, minOf(h * 0.44f, r.bottom - 34 * u))
            val tallo = if (oscuro) LIMON.copy(alpha = 0.8f) else Color(0xFF3F8A56)
            val t = Path().apply { moveTo(c.x, c.y); cubicTo(c.x - 4 * u, c.y + 12 * u, c.x + 5 * u, c.y + 18 * u, c.x + 1 * u, r.bottom - 1 * u) }
            drawPath(t, tallo, style = Stroke(1.4f * u, cap = StrokeCap.Round))
            drawPath(TrazosPsicodelia.gota(Offset(c.x + 4 * u, c.y + 17 * u), 2.6f * u, 0.3f, 0.12f), tallo)
            TrazosPsicodelia.run { flor(c, 11 * u, if (oscuro) FLUOR_MAGENTA else MAGENTA, if (oscuro) LIMON else VERDE, 8, 0.7f) }
        }
        // Las dos tiras de cinta que lo sujetan.
        cinta(Offset(r.left + 7 * u, r.top + 3 * u), 20 * u, 7 * u, -38f, oscuro)
        cinta(Offset(r.right - 7 * u, r.bottom - 3 * u), 20 * u, 7 * u, -38f, oscuro)
    }

    /** La ventana del departamento, de «Te falta hoy» (ver [tarjeta]). */
    private fun DrawScope.tarjetaVentana(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val forma = infladoPsi(Rect(0.6f * u, 1f * u, w - 1.2f * u, h - 2f * u), 16 * u, 0.8f * u)
        val completa = e.destacado
        translate(0.8f * u, 1.4f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, base)
        val alto = if (completa) h - 15 * u else minOf(h - 26 * u, 92 * u)
        val ancho = if (completa) alto * 0.74f else minOf(alto * 0.56f, 50 * u)
        val vr = Rect(w - 15 * u - ancho, (h - alto) / 2 - 1.6f * u, w - 15 * u, (h - alto) / 2 - 1.6f * u + alto)
        clipPath(forma) {
            // La luz que entra por la ventana y cae sobre la pared.
            val luz = if (oscuro) (if (completa) Color(0xFFE8B866) else FLUOR_VIOLETA) else (if (completa) Color(0xFFFFD9A8) else Color.White)
            halo(Offset(vr.center.x - ancho * 0.4f, h * 0.6f), maxOf(w * 0.5f, 120 * u), luz, if (oscuro) 0.1f else 0.55f)
            tramaPsi(Rect(0f, 0f, w * 0.35f, h), 3.4f * u, if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.1f) else VERDE.copy(alpha = 0.08f), Offset(0f, 0f), minOf(w * 0.3f, 90 * u))
        }
        if (alto > 22 * u) ventana(vr, completa, oscuro, e.semilla)
        drawPath(forma, if (completa) acentoTarjeta(e.tipo, oscuro) else borde(oscuro), style = Stroke((if (completa) 1.4f else 0.9f) * u))
    }

    /** La sala de baile con luz negra, de la noche (ver [tarjeta]). */
    private fun DrawScope.tarjetaSala(e: EstadoTarjeta, base: Color) {
        val u = density
        val w = size.width; val h = size.height
        val sellada = e.destacado
        val forma = infladoPsi(Rect(0.6f * u, 1f * u, w - 0.6f * u, h - 1f * u), 16 * u, 0.8f * u)
        drawPath(forma, Brush.verticalGradient(listOf(base, mezcla(base, Color(0xFF2A1640), 0.6f)), 0f, h))
        clipPath(forma) {
            // Las luces líquidas en la pared, y la penumbra del lado del texto.
            TrazosPsicodelia.run { lucesSala(((e.semilla * 0.137f) % 1f + 1f) % 1f, true, if (sellada) 1.1f else 0.5f, if (sellada) 9 else 4) }
            drawRect(Brush.horizontalGradient(0f to base.copy(alpha = 0.7f), 0.45f to base.copy(alpha = 0.32f), 0.75f to base.copy(alpha = 0f), startX = 0f, endX = w))
            // La gente que baila a contraluz, al pie, con el filo de las luces en la cabeza; alguno con los brazos arriba.
            val gente = Path(); val brazos = Path()
            val cabezas = ArrayList<Pair<Offset, Float>>()
            var x = -2f * u; var k = 0
            while (x < w * 0.74f && k < 40) {
                val rr = (2.4f + 1.1f * azar(e.semilla + 51, k)) * u
                val y = h - (8.5f + 4.5f * azar(e.semilla + 52, k)) * u
                val cabeza = Offset(x + (azar(e.semilla + 54, k) - 0.5f) * rr * 0.6f, y - rr * 1.25f)
                gente.addOval(Rect(cabeza, rr))
                gente.addRoundRect(RoundRect(Rect(x - rr * 2.3f, y - rr * 0.1f, x + rr * 2.3f, h + 4 * u), CornerRadius(rr * 1.9f)))
                cabezas.add(cabeza to rr)
                if (k % 4 == 2) {
                    val s = if (k % 8 == 2) 1f else -1f
                    val mano = Offset(x + s * rr * 3.2f, y - rr * 4.2f)
                    brazos.moveTo(x + s * rr * 1.7f, y + rr * 0.6f)
                    brazos.quadraticTo(x + s * rr * 3.3f, y - rr * 1f, mano.x, mano.y)
                    gente.addOval(Rect(mano, rr * 0.5f))
                }
                x += (9f + 6f * azar(e.semilla + 53, k)) * u; k++
            }
            drawPath(brazos, Color(0xFF07040B), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            drawPath(gente, Color(0xFF07040B))
            cabezas.forEachIndexed { i, (c, rr) ->
                val luz = if (i % 3 == 0) FLUOR_MAGENTA else if (i % 3 == 1) LIMON else FLUOR_VIOLETA
                drawArc(luz.copy(alpha = if (sellada) 0.75f else 0.35f), 205f, 110f, false, Offset(c.x - rr, c.y - rr), Size(2 * rr, 2 * rr), style = Stroke(0.6f * u, cap = StrokeCap.Round))
            }
            // El retroproyector en su mesa, a la derecha.
            val pie = Offset(w - 30 * u, h - 3 * u)
            drawRect(Color(0xFF07040B), Offset(pie.x - 22 * u, pie.y - 0.5f * u), Size(44 * u, 4 * u))
            retroproyector(pie, minOf(34 * u, h * 0.42f), if (sellada) 1f else 0.25f)
        }
        drawPath(forma, if (sellada) LIMON.copy(alpha = 0.9f) else Color(0xFF3A2A52), style = Stroke((if (sellada) 1.4f else 1f) * u))
        if (sellada) resplandor(forma, LIMON, 0.8f * u, 0.6f)
    }

    /** El periódico del barrio, de Vida (ver [tarjeta]). */
    private fun DrawScope.tarjetaPeriodico(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(0.6f * u, 1f * u, w - 1.8f * u, h - 2.4f * u)
        val forma = infladoPsi(r, 12 * u, 0.8f * u)
        translate(1f * u, 1.6f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, base)
        clipPath(forma) {
            // La franja de tinta de arcoíris de arriba, con su orilla de rodillo.
            val tintas = if (oscuro) TrazosPsicodelia.ARCOIRIS_NOCHE else TrazosPsicodelia.ARCOIRIS
            val franja = Path().apply {
                moveTo(0f, 0f); lineTo(w, 0f)
                var x = w; var k = 0
                while (x > 0f && k < 200) { lineTo(x, r.top + (4.6f + 0.9f * sin(x / (7 * u)) + 0.5f * azar(e.semilla + 61, k)) * u); x -= 3 * u; k++ }
                lineTo(0f, r.top + 4.6f * u); close()
            }
            drawPath(franja, Brush.horizontalGradient(tintas.map { it.copy(alpha = if (oscuro) 0.85f else 0.8f) }, 0f, w))
            // Las columnas del periódico y la foto de trama, a la derecha.
            val gris = if (oscuro) PAPEL_UV.copy(alpha = 0.08f) else TINTA.copy(alpha = 0.07f)
            val col0 = w * 0.62f
            for (c in 0..1) {
                val cx0 = col0 + c * (w - col0) * 0.5f; val cx1 = cx0 + (w - col0) * 0.42f
                var y = r.top + 13 * u; var k = 0
                while (y < h * 0.52f && k < 20) {
                    val fin = if (k % 5 == 4) cx0 + (cx1 - cx0) * 0.5f else cx1
                    drawLine(gris, Offset(cx0, y), Offset(fin, y), 1.6f * u, StrokeCap.Round); y += 4.2f * u; k++
                }
            }
            val foto = Offset(w - 22 * u, h - 14 * u)
            tramaPsi(Rect(col0, h * 0.56f, w, h), 2.8f * u, if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.22f) else TINTA.copy(alpha = 0.16f), foto, 30 * u, 0.36f)
            if (!oscuro) drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), Offset.Zero, Offset(w * 0.5f, h)))
        }
        if (e.destacado) {
            if (oscuro) resplandor(forma, acentoTarjeta(e.tipo, true), 1f * u, 0.7f)
            TrazosPsicodelia.run { flor(Offset(r.right - 10 * u, r.top + 13 * u), 7 * u, if (oscuro) FLUOR_NARANJA else NARANJA, if (oscuro) FLUOR_VIOLETA else AZUL, 7, 0.6f) }
        }
        drawPath(forma, if (e.destacado) acentoTarjeta(e.tipo, oscuro) else borde(oscuro), style = Stroke((if (e.destacado) 1.4f else 0.8f) * u))
    }

    // ---------------------------------------------------------------- Gastos

    /** La letra de los rótulos y de las cifras: Fredoka en negrita, redonda. */
    private val letraRotulos: FontFamily = LetraExtra.fredokaRotulo
    /** La letra de los títulos: Modak, la letra burbuja de los carteles. */
    private val letraTitulos: FontFamily = LetraExtra.modak

    override val altoBalanza: Dp = 64.dp

    /**
     * Los dos botones blandos de un cartel que se derrite, como los de la hoja rápida, cada uno con sus dos gotas que se
     * escurren: a la izquierda el gasto, con una gota que cae; a la derecha el ingreso, con burbujas que suben. Entre los dos, la
     * margarita que vibra. El elegido toma su color, impreso otra vez un poco corrido en su complementario, y se le enciende una
     * luz líquida detrás de la marca; el otro queda en papel, con la letra tenue.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 36 * u) return
        val xm = w / 2
        val hueco = 7 * u
        for (lado in 0..1) {
            val ingreso = lado == 1
            val elegido = e.ingreso == ingreso
            val color = if (ingreso) e.colorIngreso else e.colorGasto
            val r = if (ingreso) Rect(xm + hueco, 3.4f * u, w - 2.2f * u, h - 2 * u) else Rect(1f * u, 3.4f * u, xm - hueco, h - 2 * u)
            val forma = botonGoteaPsi(r, u)
            val cuerpo = r.height - minOf(5.5f * u, r.height * 0.14f)
            val fondo = if (elegido) mezcla(color, papel(oscuro), if (oscuro) 0.86f else 0.8f) else papel(oscuro)
            translate(1.4f * u, 1.6f * u) {
                drawPath(forma, if (elegido) TrazosPsicodelia.opuesto(color).copy(alpha = if (oscuro) 0.4f else 0.38f) else sombra(oscuro))
            }
            if (elegido && oscuro) resplandor(forma, color, 1.2f * u, 0.8f)
            drawPath(forma, fondo)
            val marca = Offset(r.left + 17 * u, r.top + cuerpo / 2)
            clipPath(forma) {
                if (elegido) halo(marca, cuerpo * 1.1f, color, if (oscuro) 0.3f else 0.32f)
                if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.45f), Color.Transparent), r.top, r.top + cuerpo * 0.5f))
            }
            drawPath(forma, if (elegido) color else borde(oscuro), style = Stroke((if (elegido) 1.5f else 0.9f) * u))
            // La gota que cae (gasto) o las burbujas que suben (ingreso).
            val tinta = if (elegido) legiblePsi(color, fondo, minimo = 3f) else mezcla(tenue(oscuro), fondo, 0.35f)
            if (ingreso) TrazosPsicodelia.run {
                burbuja(Offset(marca.x - 2.4f * u, marca.y + 4 * u), 4.2f * u, tinta, 1f)
                burbuja(Offset(marca.x + 3.6f * u, marca.y - 3 * u), 2.8f * u, tinta, 1f)
                burbuja(Offset(marca.x - 1.6f * u, marca.y - 8.2f * u), 1.7f * u, tinta, 1f)
            } else {
                drawPath(lagrimaPsi(Offset(marca.x, marca.y + 4.6f * u), 3.6f * u), tinta)
                drawOval(Color.White.copy(alpha = 0.55f), Offset(marca.x - 2.4f * u, marca.y + 2.6f * u), Size(1.6f * u, 2.2f * u))
                drawCircle(tinta.copy(alpha = 0.6f), 1.1f * u, Offset(marca.x, marca.y - 7.4f * u))
                drawCircle(tinta.copy(alpha = 0.35f), 0.7f * u, Offset(marca.x, marca.y - 10.6f * u))
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            textoEn(medidor, texto, Rect(marca.x + 13 * u, r.top + 2 * u, r.right - 8 * u, r.top + cuerpo - 1 * u), letraRotulos,
                legiblePsi(if (elegido) color else tenue(oscuro), fondo, tenue = !elegido), if (elegido) 18f else 15.5f)
        }
        // En medio, la margarita que vibra.
        TrazosPsicodelia.run {
            flor(Offset(xm, 3.4f * u + (h - 5.4f * u - minOf(5.5f * u, (h - 5.4f * u) * 0.14f)) / 2), 7.4f * u, if (oscuro) FLUOR_MAGENTA else MAGENTA, if (oscuro) LIMON else VERDE, 8, 0.5f)
        }
    }

    override val altoMes: Dp = 52.dp

    /**
     * El título del álbum como en un cartel: el mes en la letra burbuja, con su sombra corrida en el color que vibra, sobre una
     * cartela que se derrite (con sus dos gotas), contorneada con la tinta de arcoíris del tintero partido. A cada lado, la
     * flecha de letra inflada (hacia el mes anterior y hacia el siguiente) y dos burbujas que la unen a la cartela.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 30 * u) return
        val tam = 20f
        val ancho = anchoPsi(medidor, e.titulo, letraTitulos, tam)
        val cw = (ancho + 40 * u).coerceIn(120 * u, w - 112 * u)
        val cart = Rect(w / 2 - cw / 2, 5 * u, w / 2 + cw / 2, h - 3 * u)
        val forma = botonGoteaPsi(cart, u)
        val cuerpo = cart.height - minOf(5.5f * u, cart.height * 0.14f)
        val cara = papel(oscuro)
        val arcoiris = TrazosPsicodelia.arcoiris(Offset(cart.left, 0f), Offset(cart.right, 0f), 1f, oscuro)
        translate(1.4f * u, 1.6f * u) { drawPath(forma, if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.22f) else VERDE.copy(alpha = 0.22f)) }
        if (oscuro) resplandor(forma, FLUOR_MAGENTA, 1.2f * u, 0.6f)
        drawPath(forma, cara)
        clipPath(forma) {
            tramaPsi(cart, 3f * u, if (oscuro) FLUOR_VIOLETA.copy(alpha = 0.12f) else MAGENTA.copy(alpha = 0.08f), Offset(cart.right, cart.top), cw * 0.5f)
            if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.Transparent), cart.top, cart.top + cuerpo * 0.5f))
        }
        drawPath(forma, arcoiris, style = Stroke(1.6f * u))
        // El mes: la sombra corrida en el color que vibra y la letra encima.
        val caja = Rect(cart.left + 10 * u, cart.top + 2 * u, cart.right - 10 * u, cart.top + cuerpo - 1 * u)
        val color = legiblePsi(primera(oscuro), cara)
        textoEn(medidor, e.titulo, caja.translate(1.3f * u, 1.3f * u), letraTitulos, contra(oscuro).copy(alpha = if (oscuro) 0.55f else 0.5f), tam)
        textoEn(medidor, e.titulo, caja, letraTitulos, color, tam)
        // Las flechas y las burbujas que las unen a la cartela.
        for (s in listOf(-1f, 1f)) {
            val c = Offset(if (s < 0) 24 * u else w - 24 * u, h / 2 - 1 * u)
            scale(s, 1f, c) {
                val p = flechaPsi(c, 24 * u)
                translate(1.6f * u, 1.6f * u) { drawPath(p, contra(oscuro).copy(alpha = if (oscuro) 0.7f else 0.65f)) }
                if (oscuro) resplandor(p, LIMON, 0.9f * u, 0.8f)
                drawPath(p, primera(oscuro))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.35f else 0.45f), Offset(c.x - 6.4f * u, c.y - 1.1f * u), Offset(c.x - 1 * u, c.y - 1.1f * u), 1.1f * u, StrokeCap.Round)
            }
            val borde = if (s < 0) cart.left else cart.right
            val desde = c.x + s * 12 * u
            if (abs(borde - desde) > 12 * u) TrazosPsicodelia.run {
                burbuja(Offset(desde + (borde - desde) * 0.32f, h / 2 + 2.4f * u), 3f * u, aceite(oscuro, if (s < 0) 2 else 1), 1f)
                burbuja(Offset(desde + (borde - desde) * 0.68f, h / 2 - 3f * u), 2f * u, aceite(oscuro, if (s < 0) 1 else 2), 1f)
            }
        }
    }

    /**
     * Un renglón de la cuenta: el surco fino al pie y, al principio, la marca: si entra, dos burbujas que suben; si sale, una
     * gota de tinta que cae.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        val y = h - 1.2f * u
        val l = if (oscuro) Color(0xFF4E3D69) else Color(0xFFCFC3B5)
        drawLine(Brush.horizontalGradient(0f to l, 0.8f to l, 1f to l.copy(alpha = 0f), startX = 12 * u, endX = w - 2 * u), Offset(12 * u, y), Offset(w - 2 * u, y), 0.7f * u)
        val p = Offset(5f * u, h - 5f * u)
        if (e.ingreso) TrazosPsicodelia.run {
            val c = if (oscuro) FLUOR_VIOLETA else VERDE
            burbuja(Offset(p.x - 0.6f * u, p.y + 1.2f * u), 2.4f * u, c, 1f)
            burbuja(Offset(p.x + 2.2f * u, p.y - 3.4f * u), 1.5f * u, c, 1f)
        } else {
            val c = if (oscuro) LIMON else MAGENTA
            val g = (azar(e.semilla, 3) - 0.5f) * 16f
            rotate(g, p) {
                drawPath(lagrimaPsi(Offset(p.x, p.y + 1.6f * u), 2f * u), c.copy(alpha = 0.85f))
                drawCircle(Color.White.copy(alpha = 0.5f), 0.55f * u, Offset(p.x - 0.8f * u, p.y + 0.9f * u))
            }
        }
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * La fecha en una calcomanía de la tienda de discos, redonda en sus dos puntas e impresa otra vez un poco corrida en el
     * color que vibra, con una margarita al principio y la mayúscula solo al principio; de ella sale un hilo de tinta que corre
     * hasta el borde y deja colgar una gota.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 12 * u) return
        val fecha = texto.lowercase().replaceFirstChar { it.uppercase() }
        val tam = 12f; val esp = 0.3f
        val ancho = minOf(anchoPsi(medidor, fecha, letraRotulos, tam, esp), w * 0.6f)
        val etiqueta = Rect(3 * u, 2.5f * u, 3 * u + ancho + 28 * u, h - 3 * u)
        val forma = Path().apply { addRoundRect(RoundRect(etiqueta, CornerRadius(etiqueta.height / 2))) }
        val cara = if (oscuro) Color(0xFF33410F) else Color(0xFFF6D3E4)
        translate(1.2f * u, 1.2f * u) { drawPath(forma, contra(oscuro).copy(alpha = if (oscuro) 0.4f else 0.35f)) }
        if (oscuro) resplandor(forma, LIMON, 0.8f * u, 0.6f)
        drawPath(forma, cara)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), Offset(etiqueta.left + etiqueta.height / 2, etiqueta.top + 1 * u), Size(etiqueta.width - etiqueta.height, 1 * u))
        drawPath(forma, primera(oscuro), style = Stroke(0.9f * u))
        TrazosPsicodelia.run { margarita(Offset(etiqueta.left + 8.6f * u, etiqueta.center.y), 4f * u, primera(oscuro), if (oscuro) LIMON else AMARILLO, 6) }
        textoEn(medidor, fecha, Rect(etiqueta.left + 15 * u, etiqueta.top, etiqueta.right - 6 * u, etiqueta.bottom), letraRotulos,
            legiblePsi(if (oscuro) Color(0xFFECFFB8) else Color(0xFF3D0624), cara), tam, espaciado = esp)
        if (etiqueta.right + 30 * u < w) {
            val linea = if (oscuro) Color(0xFF8A78B0) else TINTA.copy(alpha = 0.4f)
            val x0 = etiqueta.right + 3 * u; val x1 = w - 8 * u
            drawPath(cintaPsi(x0, x1, h / 2, 1.3f * u, 0.8f * u, 0.4f * u), linea)
            drawPath(gotaPsi(x1 - 12 * u, h / 2 - 0.3f * u, 6f * u, 1.5f * u), linea)
        }
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * La hoja rápida es una nube de luces líquidas (su borde de arriba sube en cinco gotas que se funden). Cada gota se tiñe por
     * dentro con su aceite (verde, ámbar y magenta de día; las tintas de la luz negra de noche), con alguna burbuja, y en la más
     * alta va el símbolo de la paz. Por dentro del borde corre el menisco, una línea de tinta de arcoíris del tintero partido que
     * baja por los dos cantos y se escurre en una gota en cada uno (más abajo la de la izquierda). En la esquina de abajo a la
     * izquierda crecen dos margaritas que vibran y por el canto derecho suben unas burbujas.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 160 * u || h < 120 * u) return
        // El perfil de arriba, como lo corta la hoja (`QuickActivity`, rama de Psicodelia).
        val p = 16 * u
        val pts = ArrayList<Offset>()
        var x = 0f
        while (x < w) { pts.add(Offset(x, p - altoQuickPsi(x, w, u))); x += 1.5f * u }
        pts.add(Offset(w, p - altoQuickPsi(w, w, u)))
        // Las luces de dentro de cada gota.
        val nube = Path().apply { moveTo(0f, p + 20 * u); pts.forEach { lineTo(it.x, it.y) }; lineTo(w, p + 20 * u); close() }
        val colores = if (oscuro) listOf(FLUOR_NARANJA, LIMON, FLUOR_MAGENTA, FLUOR_VIOLETA, LIMON) else listOf(AMBAR, VERDE, MAGENTA, AMBAR, VERDE)
        clipPath(nube) {
            GOTAS_QUICK.forEachIndexed { i, g ->
                val c = Offset(g[0] * w, p - g[2] * u * 0.55f)
                val rr = g[1] * u * 0.95f
                val ry = p + 3 * u - c.y
                scale(1f, ry / rr, c) { halo(c, rr, colores[i], if (oscuro) 0.4f else 0.36f) }
            }
            TrazosPsicodelia.run {
                GOTAS_QUICK.forEachIndexed { i, g ->
                    val cx = g[0] * w
                    if (i != 2) burbuja(Offset(cx + g[1] * u * (if (i % 2 == 0) 0.3f else -0.35f), p - g[2] * u * 0.25f + 1.4f * u), (1.6f + g[2] * 0.12f) * u, colores[i], 0.9f)
                    if (i == 1 || i == 3) burbuja(Offset(cx + g[1] * u * (if (i == 1) 0.08f else -0.1f), p - g[2] * u * 0.5f), 1.1f * u, colores[i], 0.8f)
                }
            }
        }
        // La paz, en la gota más alta.
        val cPaz = Offset(GOTAS_QUICK[2][0] * w, p - 5.4f * u)
        if (oscuro) halo(cPaz, 10 * u, LIMON, 0.3f)
        TrazosPsicodelia.run { paz(cPaz, 4.6f * u, if (oscuro) LIMON else mezcla(MAGENTA, TINTA, 0.25f), 0.95f * u) }
        // El menisco: por dentro del borde, y por los cantos hasta su gota.
        val m = 3.4f * u
        val dentro = corridaPsi(pts, 2.8f * u).filter { it.x in (m + 6 * u)..(w - m - 6 * u) }
        if (dentro.size > 2) {
            val yIzq = p + h * 0.4f; val yDer = p + h * 0.26f
            val linea = Path().apply {
                val a = dentro.first(); val b = dentro.last()
                moveTo(m, maxOf(yIzq, a.y + 20 * u))
                lineTo(m, a.y + 6 * u)
                quadraticTo(m, a.y, a.x, a.y)
                dentro.forEach { lineTo(it.x, it.y) }
                quadraticTo(w - m, b.y, w - m, b.y + 6 * u)
                lineTo(w - m, maxOf(yDer, b.y + 20 * u))
            }
            val pincel = TrazosPsicodelia.arcoiris(Offset(0f, 0f), Offset(w, 0f), if (oscuro) 0.95f else 0.75f, oscuro)
            if (oscuro) resplandor(linea, FLUOR_VIOLETA, 1.3f * u, 0.7f)
            drawPath(linea, pincel, style = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            // Las gotas en que se escurre por cada canto.
            drawPath(lagrimaPsi(Offset(m, maxOf(yIzq, dentro.first().y + 20 * u) + 3.2f * u), 2.1f * u), pincel)
            drawPath(lagrimaPsi(Offset(w - m, maxOf(yDer, dentro.last().y + 20 * u) + 3.2f * u), 1.8f * u), pincel)
        }
        // Las margaritas de la esquina de abajo a la izquierda.
        val tallo = if (oscuro) LIMON.copy(alpha = 0.75f) else Color(0xFF3F8A56).copy(alpha = 0.85f)
        val f1 = Offset(13 * u, h - 30 * u); val f2 = Offset(23 * u, h - 17 * u)
        drawPath(Path().apply { moveTo(7 * u, h); cubicTo(7 * u, h - 10 * u, 15 * u, h - 18 * u, f1.x, f1.y) }, tallo, style = Stroke(1.3f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(11 * u, h); quadraticTo(13 * u, h - 10 * u, f2.x, f2.y) }, tallo, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawPath(TrazosPsicodelia.gota(Offset(9.6f * u, h - 9 * u), 2.4f * u, 0.4f, 0.14f), tallo)
        TrazosPsicodelia.run {
            flor(f1, 7.4f * u, if (oscuro) FLUOR_MAGENTA else MAGENTA, if (oscuro) LIMON else VERDE, 8, 0.5f)
            flor(f2, 5f * u, if (oscuro) FLUOR_NARANJA else NARANJA, if (oscuro) FLUOR_VIOLETA else AZUL, 6, 0.5f)
            // Las burbujas que suben por el canto derecho.
            burbuja(Offset(w - 9 * u, h * 0.62f), 3.2f * u, aceite(oscuro, 1), 0.9f)
            burbuja(Offset(w - 12 * u, h * 0.62f - 10 * u), 2.2f * u, aceite(oscuro, 2), 0.85f)
            burbuja(Offset(w - 8 * u, h * 0.62f - 18 * u), 1.4f * u, aceite(oscuro, 0), 0.8f)
        }
    }

    /**
     * El recuadro de Relaciones lleva bajo el canto de arriba un hilo de tinta de arcoíris que se junta al medio en tres gotas de
     * aceite; abajo a la izquierda, una margarita chica, y abajo a la derecha, dos burbujas que suben. Tenue en el centro.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        val cx = w / 2; val yt = 6 * u
        if (cx - 14 * u > 8 * u) {
            val pincel = TrazosPsicodelia.arcoiris(Offset(6 * u, 0f), Offset(w - 6 * u, 0f), if (oscuro) 0.6f else 0.5f, oscuro)
            drawPath(cintaPsi(7 * u, cx - 8 * u, yt, 0.2f * u, 0.7f * u, 1.2f * u), pincel)
            drawPath(cintaPsi(cx + 8 * u, w - 7 * u, yt, 1.2f * u, 0.7f * u, 0.2f * u), pincel)
        }
        nubeAceite(Offset(cx, yt), 0.8f * u, oscuro, 0.75f)
        TrazosPsicodelia.run {
            margarita(Offset(8 * u, h - 8 * u), 3.6f * u, e.primario.copy(alpha = e.primario.alpha * 0.5f), e.fondo, 6)
            burbuja(Offset(w - 8 * u, h - 7 * u), 2.4f * u, e.secundario, 0.6f)
            burbuja(Offset(w - 12 * u, h - 13 * u), 1.5f * u, e.secundario, 0.5f)
        }
    }

    /**
     * El icono de la cinta dentro de una margarita: ocho pétalos redondos en la tinta del icono, abiertos por el medio, y, por
     * dentro, un hilo en el color que vibra con ella, corrido apenas.
     */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2)
        val petalos = TrazosPsicodelia.petalos(c, s * 0.5f, 8, PI.toFloat() / 8, 0.25f, 0.2f, unidos = true)
        val flor = Path.combine(PathOperation.Difference, petalos, Path().apply { addOval(Rect(c, s * 0.3f)) })
        drawPath(flor, e.primario)
        drawCircle(TrazosPsicodelia.opuesto(e.secundario).copy(alpha = e.secundario.alpha * 0.55f), s * 0.29f, Offset(c.x + s * 0.02f, c.y + s * 0.02f), style = Stroke(maxOf(s * 0.035f, 0.5f)))
    }
}
