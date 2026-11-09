package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.pantallas.simboloCalificacion
import java.util.Random

/**
 * Egipto: la Casa de la Vida y la tumba pintada, con los materiales de su biblioteca, su Ideario y su sala hipóstila:
 * arenisca, yeso, cedro, papiro y los colores de la paleta del pintor (lapislázuli, cornalina, verde de malaquita y
 * amarillo de oro), con el friso jeker, la gola y los cartuchos.
 * - Social: un banquete pintado en el muro de una tumba. Arriba, el friso jeker y las tres bandas del registro; a los
 *   lados, dos registros con las jarras de vino en sus soportes y los ramos de loto, dentro de la orla de bloques de
 *   colores; abajo, la línea de tierra y, al centro, la mesa de ofrendas con panes, una jarra, uvas y un loto.
 * - Mapas: la mesa de cedro con un papiro desenrollado bajo el mapa: sus dos rollos a los lados, las líneas de rúbrica,
 *   el Nilo (el signo del agua) abajo, los papiros que crecen en la orilla y la paleta del escriba arriba.
 * - Otras vidas: un muro de arenisca bajo la gola pintada con el disco solar alado; cada medalla va en un naos, una
 *   capilla con su pequeña gola y su marco de toro, y entre ellas el anj.
 * - Íconos: las pirámides con el sol, un escarabeo que raspa un papiro (empuja el sol, como Jepri) y tres cartuchos.
 * - Notas: un tablero de cedro con la orla de bloques; las notas alternan ostraca de caliza (con las marcas rojas del
 *   escriba) y hojas de papiro atadas con su cordel y su sello de barro.
 */
internal object VidaEgipto : DisenoVida {
    private val MADERA = Color(0xFF8E6A3E)
    private val MADERA_OSC = Color(0xFF5E4424)
    private val YESO = Color(0xFFF2EBDA)
    private val LAPIS = Color(0xFF1D3A8A)
    private val ROJO = Color(0xFFB0432A)
    private val VERDE = Color(0xFF3E7A4A)
    private val AMARILLO = Color(0xFFF2CF4A)
    private val ORO = Color(0xFFD9A93A)
    private val NEGRO = Color(0xFF1E160C)
    private val ARENISCA = Color(0xFFC9AE7A)
    private val ARENISCA_LUZ = Color(0xFFE0CA98)
    private val ARENISCA_OSC = Color(0xFF9A7E50)
    private val PAPIRO = Color(0xFFE8D9A8)
    private val PAPIRO_OSC = Color(0xFFB89E62)
    private val NILO = Color(0xFF2F6FA0)
    private val BARRO = Color(0xFF7A5A3A)
    private val PAN = Color(0xFFC9893A)
    private val UVA = Color(0xFF4A2A5A)
    private val NOCHE = Color(0xFF110D08)

    /** La caliza y el papiro de las notas: claros de día; de noche, a la luz de la lámpara (con la tinta clara de [tintaNota]). */
    private val CALIZA_CLARA = Color(0xFFEEE6D2)
    private val CALIZA_OSCURA = Color(0xFF3A3226)
    private val HOJA_CLARA = Color(0xFFE9D9A8)
    private val HOJA_OSCURA = Color(0xFF3A2E1A)

    private fun DrawScope.u() = size.minDimension / 100f
    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mez(c, NOCHE, 0.5f) else c
    /** El material sobre la superficie de la tarjeta: [k] es cuánto se ve (0, nada; 1, el material pleno). */
    private fun v(fondo: Color, c: Color, k: Float, oscuro: Boolean): Color = mez(fondo, n(c, oscuro), if (oscuro) minOf(k * 1.5f, 1f) else k)

    /** Los centros de las cinco medallas de Otras vidas (una fila repartida con 10 dp de margen) y la distancia entre ellas. */
    private fun DrawScope.centrosMedallas(): Pair<List<Float>, Float> {
        val margen = 10.dp.toPx(); val ancho = 52.dp.toPx(); val n = 5
        val hueco = ((size.width - 2 * margen - n * ancho) / (n + 1)).coerceAtLeast(0f)
        return List(n) { i -> margen + hueco * (i + 1) + ancho * (i + 0.5f) } to (hueco + ancho)
    }

    /**
     * La orla de bloques de colores que enmarca las pinturas: rectángulos de [largo] separados por una junta oscura, a lo
     * largo de [r] (en vertical si es más alta que ancha).
     */
    private fun DrawScope.orla(r: Rect, largo: Float, colores: List<Color>, junta: Color) {
        if (r.width <= 0f || r.height <= 0f || largo <= 0f) return
        drawRect(junta, r.topLeft, r.size)
        val vertical = r.height > r.width
        val total = if (vertical) r.height else r.width
        val j = largo * 0.18f
        var p = 0f; var k = 0
        while (p < total && k < 120) {
            val l = minOf(largo - j, total - p)
            if (vertical) drawRect(colores[k % colores.size], Offset(r.left + j * 0.5f, r.top + p), Size(r.width - j, l))
            else drawRect(colores[k % colores.size], Offset(r.left + p, r.top + j * 0.5f), Size(l, r.height - j))
            p += largo; k++
        }
    }

    /** El signo del agua: una línea en zigzag de [x0] a [x1] a la altura [y]. */
    private fun agua(x0: Float, x1: Float, y: Float, amp: Float, paso: Float): Path = Path().apply {
        moveTo(x0, y)
        var x = x0; var arriba = true; var k = 0
        while (x < x1 && k < 300) { x += paso / 2; lineTo(minOf(x, x1), if (arriba) y - amp else y + amp); arriba = !arriba; k++ }
    }

    /** Un loto abierto visto de lado: tres pétalos sobre el tallo. */
    private fun DrawScope.loto(c: Offset, r: Float, flor: Color, caliz: Color) {
        drawPath(Path().apply { moveTo(c.x - r, c.y - r * 1.2f); lineTo(c.x, c.y); lineTo(c.x + r, c.y - r * 1.2f); lineTo(c.x + r * 0.35f, c.y - r * 0.5f); lineTo(c.x, c.y - r * 1.4f); lineTo(c.x - r * 0.35f, c.y - r * 0.5f); close() }, flor)
        drawPath(Path().apply { moveTo(c.x - r * 0.5f, c.y - r * 0.6f); lineTo(c.x, c.y); lineTo(c.x + r * 0.5f, c.y - r * 0.6f); close() }, caliz)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val lado = minOf(w * 0.105f, 21f * u)
        val suelo = h - 5f * u
        val bloques = listOf(m(LAPIS, 0.5f), m(ROJO, 0.5f), m(VERDE, 0.5f), m(AMARILLO, 0.6f))
        val junta = m(NEGRO, 0.35f)

        // El muro de yeso, tenue en el centro, y los paneles de los lados.
        drawRect(m(YESO, 0.14f))
        drawRect(m(ARENISCA_LUZ, 0.3f), Offset.Zero, Size(lado, h))
        drawRect(m(ARENISCA_LUZ, 0.3f), Offset(w - lado, 0f), Size(lado, h))
        orla(Rect(0.5f * u, 9.5f * u, 2.5f * u, suelo), 3.2f * u, bloques, junta)
        orla(Rect(w - 2.5f * u, 9.5f * u, w - 0.5f * u, suelo), 3.2f * u, bloques, junta)

        // Los registros de los lados: la línea de tierra de cada uno.
        val medio = h * 0.52f
        listOf(medio, suelo).forEach { y ->
            drawRect(m(ROJO, 0.45f), Offset(3f * u, y - 1f * u), Size(lado - 3f * u, 1f * u))
            drawRect(m(ROJO, 0.45f), Offset(w - lado, y - 1f * u), Size(lado - 3f * u, 1f * u))
        }
        // Arriba, las jarras de vino en sus soportes; abajo, los ramos de loto.
        val xi = (3f * u + lado) / 2; val xd = w - xi
        listOf(xi, xd).forEach { x ->
            jarra(Offset(x, medio - 1f * u), u, m(ROJO, 0.45f), m(LAPIS, 0.55f), m(MADERA_OSC, 0.55f), m(VERDE, 0.55f))
            ramo(Offset(x, suelo - 1f * u), u, m(ARENISCA_OSC, 0.6f), m(VERDE, 0.55f), m(LAPIS, 0.55f), m(YESO, 0.6f))
        }

        // El friso jeker y las tres bandas del registro.
        drawRect(m(ARENISCA_LUZ, 0.45f), Offset.Zero, Size(w, 7f * u))
        var x = 1.6f * u; var k = 0
        while (x < w && k < 160) {
            val c = when (k % 3) { 0 -> m(ROJO, 0.5f); 1 -> m(LAPIS, 0.5f); else -> m(VERDE, 0.5f) }
            drawRect(c, Offset(x - 0.9f * u, 2.8f * u), Size(1.8f * u, 4f * u))
            drawCircle(c, 1.1f * u, Offset(x, 2.4f * u))
            drawLine(m(NEGRO, 0.3f), Offset(x - 0.9f * u, 4.2f * u), Offset(x + 0.9f * u, 4.2f * u), 0.35f * u)
            x += 4f * u; k++
        }
        drawRect(m(ROJO, 0.42f), Offset(0f, 7f * u), Size(w, 0.8f * u))
        drawRect(m(AMARILLO, 0.5f), Offset(0f, 7.8f * u), Size(w, 0.8f * u))
        drawRect(m(LAPIS, 0.42f), Offset(0f, 8.6f * u), Size(w, 0.8f * u))

        // La línea de tierra y la orla de abajo.
        drawRect(m(ROJO, 0.45f), Offset(0f, suelo), Size(w, 1f * u))
        drawRect(m(AMARILLO, 0.5f), Offset(0f, suelo + 1f * u), Size(w, 0.8f * u))
        orla(Rect(0f, suelo + 2f * u, w, h - 0.4f * u), 3.6f * u, bloques, junta)

        // La mesa de ofrendas en el centro: el pie, el tablero, los panes, una jarra, las uvas y un loto.
        val cx = w / 2
        drawRect(m(ARENISCA_OSC, 0.6f), Offset(cx - 1.1f * u, suelo - 5.4f * u), Size(2.2f * u, 5.4f * u))
        drawRect(m(ARENISCA_OSC, 0.6f), Offset(cx - 2.6f * u, suelo - 0.8f * u), Size(5.2f * u, 0.8f * u))
        drawRect(m(MADERA, 0.6f), Offset(cx - 9f * u, suelo - 6.3f * u), Size(18f * u, 0.9f * u))
        val mesa = suelo - 6.3f * u
        listOf(-6.8f, -4.4f, -2.2f).forEach { dx -> drawArc(m(PAN, 0.6f), 180f, 180f, true, Offset(cx + dx * u - 1.6f * u, mesa - 1.8f * u), Size(3.2f * u, 3.6f * u)) }
        drawPath(Path().apply {
            moveTo(cx - 0.5f * u, mesa - 6f * u); lineTo(cx + 0.5f * u, mesa - 6f * u)
            quadraticTo(cx + 2.4f * u, mesa - 4f * u, cx + 0.8f * u, mesa); lineTo(cx - 0.8f * u, mesa)
            quadraticTo(cx - 2.4f * u, mesa - 4f * u, cx - 0.5f * u, mesa - 6f * u); close()
        }, m(ROJO, 0.55f))
        drawLine(m(LAPIS, 0.6f), Offset(cx - 1.5f * u, mesa - 3.8f * u), Offset(cx + 1.5f * u, mesa - 3.8f * u), 0.5f * u)
        // El racimo: tres uvas arriba, dos en medio y una abajo.
        listOf(-1f to 0f, 0f to 0f, 1f to 0f, -0.5f to 1f, 0.5f to 1f, 0f to 2f).forEach { (dx, dy) ->
            drawCircle(m(UVA, 0.55f), 0.65f * u, Offset(cx + 4.4f * u + dx * 1.1f * u, mesa - 3.4f * u + dy * 1f * u))
        }
        drawPath(Path().apply { moveTo(cx + 0.4f * u, mesa - 6f * u); quadraticTo(cx + 4f * u, mesa - 9f * u, cx + 7f * u, mesa - 5.5f * u) }, m(VERDE, 0.6f), style = Stroke(0.4f * u))
        loto(Offset(cx + 7f * u, mesa - 4.2f * u), 1.4f * u, m(LAPIS, 0.6f), m(VERDE, 0.6f))
    }

    /** Una jarra de vino en su soporte cruzado, con su collar de pétalos. */
    private fun DrawScope.jarra(b: Offset, u: Float, barro: Color, collar: Color, soporte: Color, petalo: Color) {
        drawLine(soporte, Offset(b.x - 2.4f * u, b.y), Offset(b.x + 1.6f * u, b.y - 3.6f * u), 0.6f * u)
        drawLine(soporte, Offset(b.x + 2.4f * u, b.y), Offset(b.x - 1.6f * u, b.y - 3.6f * u), 0.6f * u)
        val y = b.y - 1.5f * u
        drawPath(Path().apply {
            moveTo(b.x - 1f * u, y - 13f * u); lineTo(b.x + 1f * u, y - 13f * u); lineTo(b.x + 1f * u, y - 11.5f * u)
            cubicTo(b.x + 4.2f * u, y - 10.5f * u, b.x + 3.6f * u, y - 5f * u, b.x + 0.6f * u, y - 2.4f * u)
            lineTo(b.x - 0.6f * u, y - 2.4f * u)
            cubicTo(b.x - 3.6f * u, y - 5f * u, b.x - 4.2f * u, y - 10.5f * u, b.x - 1f * u, y - 11.5f * u); close()
        }, barro)
        drawLine(collar, Offset(b.x - 2.9f * u, y - 10.4f * u), Offset(b.x + 2.9f * u, y - 10.4f * u), 0.7f * u)
        for (k in -1..1) drawPath(Path().apply {
            val px = b.x + k * 1.6f * u
            moveTo(px - 0.6f * u, y - 10f * u); lineTo(px, y - 8.4f * u); lineTo(px + 0.6f * u, y - 10f * u); close()
        }, petalo)
    }

    /** Un ramo de lotos en su vaso: los tallos en abanico, flores abiertas y capullos. */
    private fun DrawScope.ramo(b: Offset, u: Float, vaso: Color, tallo: Color, flor: Color, caliz: Color) {
        drawPath(Path().apply {
            moveTo(b.x - 1.6f * u, b.y - 3.6f * u); lineTo(b.x + 1.6f * u, b.y - 3.6f * u); lineTo(b.x + 1f * u, b.y); lineTo(b.x - 1f * u, b.y); close()
        }, vaso)
        listOf(-0.7f to 13f, -0.35f to 15.5f, 0f to 17f, 0.35f to 15.5f, 0.7f to 13f).forEachIndexed { i, (dx, alto) ->
            val tip = Offset(b.x + dx * 6f * u, b.y - alto * u)
            drawLine(tallo, Offset(b.x, b.y - 3.6f * u), tip, 0.45f * u)
            if (i % 2 == 0) loto(tip, 1.3f * u, flor, tallo)
            else drawOval(caliz, Offset(tip.x - 0.6f * u, tip.y - 1.6f * u), Size(1.2f * u, 1.8f * u))
        }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val rnd = Random(e.semilla.toLong())

        // La mesa de cedro, con las juntas de sus tablas.
        drawRect(m(MADERA, 0.26f))
        listOf(h / 3f, h * 2f / 3f).forEach { y -> drawLine(m(MADERA_OSC, 0.32f), Offset(0f, y), Offset(w, y), 0.5f * u) }

        // El papiro desenrollado bajo el mapa, con sus fibras cruzadas, casi invisibles.
        val hoja = Rect(8f * u, 2.6f * u, w - 8f * u, h - 2.6f * u)
        val papiro = m(PAPIRO, 0.36f)
        drawRect(papiro, hoja.topLeft, hoja.size)
        // Las fibras se tiñen a partir del papiro mismo, no de la tarjeta, para que de noche no salga una cuadrícula.
        val fibra = if (oscuro) Color.Black else PAPIRO_OSC
        for (k in 1 until 10) {
            val y = hoja.top + hoja.height * k / 10f + (rnd.nextFloat() - 0.5f) * u
            drawLine(mez(papiro, fibra, if (oscuro) 0.06f else 0.16f), Offset(hoja.left, y), Offset(hoja.right, y), 0.35f * u)
        }
        val nv = (hoja.width / (12f * u)).toInt().coerceIn(4, 30)
        for (k in 1 until nv) {
            val x = hoja.left + hoja.width * k / nv
            drawLine(mez(papiro, fibra, if (oscuro) 0.035f else 0.09f), Offset(x, hoja.top), Offset(x, hoja.bottom), 0.35f * u)
        }
        // Las líneas de rúbrica: rojo y negro arriba, rojo abajo.
        drawLine(m(ROJO, 0.5f), Offset(hoja.left + 1f * u, hoja.top + 1.6f * u), Offset(hoja.right - 1f * u, hoja.top + 1.6f * u), 0.6f * u)
        drawLine(m(NEGRO, 0.35f), Offset(hoja.left + 1f * u, hoja.top + 2.8f * u), Offset(hoja.right - 1f * u, hoja.top + 2.8f * u), 0.4f * u)
        drawLine(m(ROJO, 0.5f), Offset(hoja.left + 1f * u, hoja.bottom - 1.6f * u), Offset(hoja.right - 1f * u, hoja.bottom - 1.6f * u), 0.6f * u)

        // El Nilo: dos filas del signo del agua abajo.
        val nilo = m(NILO, 0.55f)
        drawPath(agua(hoja.left + 2f * u, hoja.right - 2f * u, h - 8f * u, 0.6f * u, 2f * u), nilo, style = Stroke(0.45f * u))
        drawPath(agua(hoja.left + 2f * u, hoja.right - 2f * u, h - 6.2f * u, 0.6f * u, 2f * u), nilo, style = Stroke(0.45f * u))

        // Los dos rollos a los lados, con sus extremos en espiral.
        listOf(Rect(1.4f * u, 1.2f * u, 8.6f * u, h - 1.2f * u), Rect(w - 8.6f * u, 1.2f * u, w - 1.4f * u, h - 1.2f * u)).forEach { r ->
            drawRect(Brush.horizontalGradient(listOf(m(PAPIRO_OSC, 0.6f), m(PAPIRO, 0.72f), m(PAPIRO_OSC, 0.65f)), r.left, r.right), r.topLeft, r.size)
            drawRect(m(ARENISCA_OSC, 0.5f), r.topLeft, r.size, style = Stroke(0.4f * u))
            listOf(r.top, r.bottom).forEach { y ->
                drawOval(m(PAPIRO, 0.75f), Offset(r.left, y - 1.2f * u), Size(r.width, 2.4f * u))
                drawOval(m(ARENISCA_OSC, 0.55f), Offset(r.left, y - 1.2f * u), Size(r.width, 2.4f * u), style = Stroke(0.4f * u))
                drawOval(m(ARENISCA_OSC, 0.45f), Offset(r.left + r.width * 0.3f, y - 0.5f * u), Size(r.width * 0.4f, 1f * u), style = Stroke(0.35f * u))
            }
        }

        // Arriba a la derecha, la paleta del escriba con sus dos pastillas de tinta y los cálamos.
        val pal = Rect(w - 30f * u, 3.6f * u, w - 13f * u, 6.6f * u)
        if (pal.left > hoja.left + 10f * u) {
            drawRect(m(MADERA, 0.72f), pal.topLeft, pal.size)
            drawRect(m(MADERA_OSC, 0.6f), pal.topLeft, pal.size, style = Stroke(0.35f * u))
            drawCircle(m(ROJO, 0.75f), 0.9f * u, Offset(pal.left + 1.8f * u, pal.center.y))
            drawCircle(m(NEGRO, 0.7f), 0.9f * u, Offset(pal.left + 4.2f * u, pal.center.y))
            drawLine(m(MADERA_OSC, 0.65f), Offset(pal.left + 6.4f * u, pal.center.y), Offset(pal.right - 0.8f * u, pal.center.y), 0.6f * u)
            drawLine(m(PAPIRO_OSC, 0.8f), Offset(pal.left + 7f * u, pal.center.y - 0.4f * u), Offset(pal.right + 3f * u, pal.center.y - 1.2f * u), 0.4f * u, StrokeCap.Round)
        }

        // Abajo a la izquierda, los papiros de la orilla.
        listOf(-1.2f to 13f, 0f to 15.5f, 1.4f to 12f).forEach { (dx, alto) ->
            val base = Offset(hoja.left + 4f * u + dx * u, h - 3.6f * u)
            val tip = Offset(base.x + dx * 1.4f * u, base.y - alto * u)
            drawLine(m(VERDE, 0.6f), base, tip, 0.5f * u)
            for (j in -2..2) drawLine(m(VERDE, 0.6f), tip, Offset(tip.x + j * 0.9f * u, tip.y - 2.2f * u + kotlin.math.abs(j) * 0.4f * u), 0.35f * u)
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val (centros, paso) = centrosMedallas()
        val nw = paso * 0.84f
        val gola = 11.6f * u; val toro = 14.6f * u
        val arriba = 19f * u; val abajo = 85.5f * u

        // El muro de arenisca con sus hiladas.
        drawRect(m(ARENISCA, 0.24f), Offset(0f, toro), Size(w, abajo - toro + 1f * u))
        var y = toro + 12f * u; var fila = 0
        while (y < abajo && fila < 10) {
            drawLine(m(ARENISCA_OSC, 0.22f), Offset(0f, y), Offset(w, y), 0.4f * u)
            var x = if (fila % 2 == 0) 0f else 11f * u; var k = 0
            while (x < w && k < 40) { drawLine(m(ARENISCA_OSC, 0.18f), Offset(x, y - 12f * u), Offset(x, y), 0.4f * u); x += 22f * u; k++ }
            y += 12f * u; fila++
        }

        // Cada naos: el marco de toro, el interior hundido y su pequeña gola.
        centros.forEach { cx ->
            val r = Rect(cx - nw / 2, arriba, cx + nw / 2, abajo)
            drawRect(m(LAPIS, 0.07f), r.topLeft, r.size)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.28f else 0.1f), Color.Transparent), r.top, r.top + 7f * u), r.topLeft, Size(r.width, 7f * u))
            drawRect(m(ARENISCA_OSC, 0.55f), Offset(r.left - 0.6f * u, r.top), Size(r.width + 1.2f * u, r.height), style = Stroke(1.2f * u))
            val g = Path().apply {
                moveTo(r.left - 2.2f * u, arriba - 3.8f * u); lineTo(r.right + 2.2f * u, arriba - 3.8f * u)
                lineTo(r.right + 0.6f * u, arriba); lineTo(r.left - 0.6f * u, arriba); close()
            }
            drawPath(g, m(ARENISCA_LUZ, 0.6f))
            clipPath(g) {
                var x = r.left - 2f * u; var k = 0
                while (x < r.right + 2f * u && k < 40) {
                    drawRect(if (k % 2 == 0) m(LAPIS, 0.45f) else m(VERDE, 0.45f), Offset(x, arriba - 3.4f * u), Size(1.1f * u, 3f * u))
                    x += 2f * u; k++
                }
            }
            drawPath(g, m(ARENISCA_OSC, 0.55f), style = Stroke(0.4f * u))
        }

        // El anj entre los naos y en las puntas, si cabe.
        val bordes = mutableListOf(centros.first() - paso / 2)
        centros.zipWithNext().forEach { (a, b) -> bordes += (a + b) / 2 }
        bordes += centros.last() + paso / 2
        val rA = minOf((paso - nw) * 0.4f, 4.5f * u)
        if (rA > 1.4f * u) bordes.forEach { x ->
            if (x > rA && x < w - rA) drawPath(simboloCalificacion(TemaId.EGIPTO, Offset(x, h * 0.5f), rA), m(VERDE, 0.5f))
        }

        // La gola pintada, con el toro atado debajo.
        drawRect(m(ARENISCA_LUZ, 0.55f), Offset.Zero, Size(w, gola))
        var x = 0.6f * u; var k = 0
        while (x < w && k < 160) {
            val c = when (k % 3) { 0 -> m(LAPIS, 0.42f); 1 -> m(VERDE, 0.42f); else -> m(ROJO, 0.42f) }
            drawRoundRect(c, Offset(x, 1.6f * u), Size(2.2f * u, gola - 2f * u), CornerRadius(1.1f * u))
            x += 3.2f * u; k++
        }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f)), 4f * u, gola), Offset(0f, 4f * u), Size(w, gola - 4f * u))
        drawRect(m(ARENISCA_LUZ, 0.7f), Offset.Zero, Size(w, 1.6f * u))
        drawRect(m(ARENISCA_OSC, 0.55f), Offset(0f, gola), Size(w, toro - gola))
        x = 0f; k = 0
        while (x < w && k < 160) { drawLine(m(NEGRO, 0.3f), Offset(x, toro), Offset(x + 1.6f * u, gola), 0.4f * u); x += 2f * u; k++ }

        // El disco solar alado sobre la gola, al centro.
        val cx = w / 2; val cy = 6.6f * u
        drawRect(m(ARENISCA_LUZ, 0.75f), Offset(cx - 25f * u, 1.6f * u), Size(50f * u, gola - 2.2f * u))
        listOf(-1f, 1f).forEach { sg ->
            listOf(Triple(0f, 22f, m(LAPIS, 0.6f)), Triple(1.7f, 17f, m(VERDE, 0.6f)), Triple(3.2f, 12f, m(ROJO, 0.6f))).forEach { (dy, largo, c) ->
                drawPath(Path().apply {
                    moveTo(cx + sg * 3f * u, cy - 2.4f * u + dy * u)
                    quadraticTo(cx + sg * (largo * 0.55f) * u, cy - 4.4f * u + dy * u, cx + sg * largo * u, cy - 2f * u + dy * u)
                    lineTo(cx + sg * (largo - 1.5f) * u, cy - 0.6f * u + dy * u)
                    quadraticTo(cx + sg * (largo * 0.5f) * u, cy - 2.4f * u + dy * u, cx + sg * 3f * u, cy - 0.8f * u + dy * u)
                    close()
                }, c)
            }
        }
        drawCircle(m(ROJO, 0.7f), 3f * u, Offset(cx, cy))
        drawCircle(m(ORO, 0.75f), 3f * u, Offset(cx, cy), style = Stroke(0.6f * u))
        listOf(-1f, 1f).forEach { sg -> drawArc(m(VERDE, 0.65f), if (sg < 0) 90f else 0f, 90f, false, Offset(cx + sg * 2.4f * u - 1.2f * u, cy + 1.6f * u), Size(2.4f * u, 2.6f * u), style = Stroke(0.6f * u)) }

        // El zócalo: las tres bandas del registro y la orla de bloques.
        drawRect(m(ROJO, 0.45f), Offset(0f, abajo + 0.5f * u), Size(w, 1.6f * u))
        drawRect(m(AMARILLO, 0.52f), Offset(0f, abajo + 2.1f * u), Size(w, 1.6f * u))
        drawRect(m(LAPIS, 0.45f), Offset(0f, abajo + 3.7f * u), Size(w, 1.6f * u))
        orla(Rect(0f, abajo + 6.2f * u, w, h - 0.4f * u), 5f * u, listOf(m(LAPIS, 0.45f), m(ROJO, 0.45f), m(VERDE, 0.45f), m(AMARILLO, 0.55f)), m(NEGRO, 0.3f))
    }

    // ---------------------------------------------------------------- Íconos de mapa

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val c = Offset(size.width / 2, size.height / 2)
        val s = size.height * 0.4f
        val g = s * 0.07f
        when (id) {
            "unesco" -> {
                // Las tres pirámides de Guiza con el sol, sobre la línea del desierto.
                val suelo = c.y + s * 0.85f
                fun piramide(cima: Offset, izq: Float, der: Float, luz: Color, sombra: Color) {
                    drawPath(Path().apply { moveTo(cima.x, cima.y); lineTo(izq, suelo); lineTo(der, suelo); close() }, luz)
                    drawPath(Path().apply { moveTo(cima.x, cima.y); lineTo(der, suelo); lineTo(cima.x + (der - cima.x) * 0.35f, suelo); close() }, sombra)
                }
                piramide(Offset(c.x + s * 1.25f, c.y + s * 0.15f), c.x + s * 0.8f, c.x + s * 1.7f, e.c2, mez(e.c2, e.fondo, 0.35f))
                piramide(Offset(c.x + s * 0.65f, c.y - s * 0.45f), c.x - s * 0.1f, c.x + s * 1.4f, e.c1, mez(e.c1, e.fondo, 0.3f))
                piramide(Offset(c.x - s * 0.35f, c.y - s * 0.95f), c.x - s * 1.4f, c.x + s * 0.7f, e.c1, mez(e.c1, e.fondo, 0.3f))
                drawPath(Path().apply { moveTo(c.x - s * 0.35f, c.y - s * 0.95f); lineTo(c.x - s * 0.5f, c.y - s * 0.72f); lineTo(c.x - s * 0.2f, c.y - s * 0.72f); close() }, e.c2)
                drawCircle(e.c2, s * 0.2f, Offset(c.x - s * 1.25f, c.y - s * 0.6f))
                drawLine(e.c2, Offset(c.x - s * 1.7f, suelo), Offset(c.x + s * 1.8f, suelo), g * 1.3f, StrokeCap.Round)
            }
            "rascar" -> {
                // Un papiro con una franja raspada y el escarabeo que raspa empujando el sol.
                val hoja = Rect(c.x - s * 1.5f, c.y - s * 0.8f, c.x + s * 1.2f, c.y + s * 0.8f)
                drawRect(e.c2.copy(alpha = 0.3f), hoja.topLeft, hoja.size)
                clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                    val raspa = Path().apply {
                        moveTo(c.x - s * 1.2f, c.y - s * 0.35f)
                        var x = c.x - s * 1.2f; var arriba = false; var k = 0
                        while (x < c.x + s * 0.3f && k < 20) { x += s * 0.22f; lineTo(x, if (arriba) c.y - s * 0.4f else c.y + s * 0.35f); arriba = !arriba; k++ }
                    }
                    drawPath(raspa, e.c1, style = Stroke(s * 0.2f, cap = StrokeCap.Round))
                }
                drawRect(e.c2, hoja.topLeft, hoja.size, style = Stroke(g))
                val esc = Offset(c.x + s * 0.95f, c.y + s * 0.25f)
                drawCircle(e.fondo, s * 0.55f, Offset(esc.x, esc.y - s * 0.1f))
                rotate(-25f, esc) {
                    listOf(-1f, 1f).forEach { sg ->
                        drawLine(e.c1, Offset(esc.x + sg * s * 0.15f, esc.y - s * 0.35f), Offset(esc.x + sg * s * 0.42f, esc.y - s * 0.6f), g, StrokeCap.Round)
                        drawLine(e.c1, Offset(esc.x + sg * s * 0.28f, esc.y), Offset(esc.x + sg * s * 0.52f, esc.y + s * 0.06f), g, StrokeCap.Round)
                        drawLine(e.c1, Offset(esc.x + sg * s * 0.24f, esc.y + s * 0.3f), Offset(esc.x + sg * s * 0.46f, esc.y + s * 0.56f), g, StrokeCap.Round)
                    }
                    drawOval(e.c1, Offset(esc.x - s * 0.3f, esc.y - s * 0.28f), Size(s * 0.6f, s * 0.74f))
                    drawOval(e.c1, Offset(esc.x - s * 0.21f, esc.y - s * 0.5f), Size(s * 0.42f, s * 0.27f))
                    drawCircle(e.c1, s * 0.1f, Offset(esc.x, esc.y - s * 0.55f))
                    drawLine(e.fondo, Offset(esc.x - s * 0.24f, esc.y - s * 0.25f), Offset(esc.x + s * 0.24f, esc.y - s * 0.25f), g * 0.7f)
                    drawLine(e.fondo, Offset(esc.x, esc.y - s * 0.25f), Offset(esc.x, esc.y + s * 0.44f), g * 0.7f)
                    drawCircle(e.c2, s * 0.18f, Offset(esc.x, esc.y - s * 0.83f))
                }
            }
            else -> {
                // Tus lugares: tres cartuchos unidos por un camino de puntos.
                val pts = listOf(Offset(c.x - s * 1.15f, c.y + s * 0.18f), Offset(c.x, c.y - s * 0.1f), Offset(c.x + s * 1.1f, c.y + s * 0.12f))
                val camino = Path().apply {
                    moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.6f, c.y + s * 0.75f, pts[1].x, pts[1].y)
                    quadraticTo(c.x + s * 0.6f, c.y + s * 0.75f, pts[2].x, pts[2].y)
                }
                drawPath(camino, e.c2, style = Stroke(g, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, g * 2.6f))))
                pts.forEachIndexed { i, o ->
                    val k = 1f - 0.1f * i
                    val an = s * 0.44f * k; val al = s * 1.1f * k
                    drawRoundRect(e.c1, Offset(o.x - an / 2, o.y - al / 2), Size(an, al), CornerRadius(an / 2))
                    drawRoundRect(e.fondo, Offset(o.x - an / 2 + g, o.y - al / 2 + g), Size(an - 2 * g, al - 2 * g), CornerRadius(an / 2 - g), style = Stroke(g * 0.7f))
                    drawLine(e.c1, Offset(o.x - an * 0.62f, o.y + al / 2 + g * 0.9f), Offset(o.x + an * 0.62f, o.y + al / 2 + g * 0.9f), g * 1.3f, StrokeCap.Round)
                    drawCircle(e.fondo, an * 0.16f, Offset(o.x, o.y - al * 0.22f))
                    drawPath(agua(o.x - an * 0.25f, o.x + an * 0.25f, o.y + al * 0.02f, an * 0.07f, an * 0.17f), e.fondo, style = Stroke(g * 0.6f))
                    drawLine(e.fondo, Offset(o.x, o.y + al * 0.14f), Offset(o.x, o.y + al * 0.32f), g * 0.7f)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val rnd = Random(e.semilla.toLong())

        // El tablero de cedro: tablas verticales con sus juntas y unas vetas.
        drawRect(m(MADERA, 0.32f))
        val tablas = 6
        for (k in 0 until tablas) {
            val x0 = w * k / tablas
            drawRect(m(if (rnd.nextBoolean()) MADERA_OSC else ARENISCA, 0.06f), Offset(x0, 0f), Size(w / tablas, h))
            if (k > 0) drawLine(m(MADERA_OSC, 0.45f), Offset(x0, 0f), Offset(x0, h), 0.6f * u)
            repeat(2) {
                val x = x0 + w / tablas * (0.2f + 0.6f * rnd.nextFloat())
                val amp = (0.4f + rnd.nextFloat()) * u
                drawPath(Path().apply { moveTo(x, 0f); cubicTo(x + amp, h * 0.3f, x - amp, h * 0.7f, x + amp * 0.5f, h) }, m(MADERA_OSC, 0.2f), style = Stroke(0.4f * u))
            }
        }
        // La orla de bloques de colores alrededor.
        val bloques = listOf(m(LAPIS, 0.5f), m(ROJO, 0.5f), m(VERDE, 0.5f), m(AMARILLO, 0.6f))
        val junta = m(NEGRO, 0.4f)
        val o = 3.2f * u
        orla(Rect(0f, 0f, w, o), 5f * u, bloques, junta)
        orla(Rect(0f, h - o, w, h), 5f * u, bloques.reversed(), junta)
        orla(Rect(0f, o, o, h - o), 5f * u, bloques, junta)
        orla(Rect(w - o, o, w, h - o), 5f * u, bloques.reversed(), junta)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        translate(r.left, r.top) {
            val w = r.width; val h = r.height; val u = minOf(w, h) / 100f
            val caliza = e.indice % 2 == 0
            val papel = when {
                caliza && oscuro -> CALIZA_OSCURA
                caliza -> CALIZA_CLARA
                oscuro -> HOJA_OSCURA
                else -> HOJA_CLARA
            }
            val forma = if (caliza) guijarro(Rect(0f, 0f, w, h), e.indice * 17 + 3, 0.07f) else hojaPapiro(Rect(0f, 0f, w, h), e.indice * 23 + 1)
            translate(1.2f * u, 2.2f * u) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f)) }
            drawPath(forma, papel)
            clipPath(forma) {
                if (caliza) {
                    // Una lasca más clara en un canto y las marcas rojas del escriba en el margen.
                    drawCircle(Color.White.copy(alpha = if (oscuro) 0.04f else 0.25f), h * 0.3f, Offset(w * 0.95f, h * 0.95f))
                    for (k in 0 until 3) drawLine(ROJO.copy(alpha = if (oscuro) 0.7f else 0.6f), Offset(w * 0.03f, h * (0.3f + 0.2f * k)), Offset(w * 0.09f, h * (0.3f + 0.2f * k)), 1f * u)
                } else {
                    // Las fibras del papiro, apenas.
                    for (k in 1 until 6) drawLine(PAPIRO_OSC.copy(alpha = if (oscuro) 0.12f else 0.16f), Offset(0f, h * k / 6f), Offset(w, h * k / 6f), 0.6f * u)
                    for (k in 1 until 8) drawLine(PAPIRO_OSC.copy(alpha = if (oscuro) 0.07f else 0.09f), Offset(w * k / 8f, 0f), Offset(w * k / 8f, h), 0.6f * u)
                }
            }
            val borde = when {
                e.fijada -> e.acento
                oscuro -> mez(papel, ARENISCA_LUZ, 0.35f)
                else -> mez(papel, ARENISCA_OSC, 0.6f)
            }
            drawPath(forma, borde, style = Stroke(if (e.fijada) 2f * u else 1.1f * u))
            val vivo = { c: Color -> if (oscuro) mez(c, Color.White, 0.25f) else c }

            // El papiro va atado con su cordel y un sello de barro en el margen izquierdo.
            if (!caliza) {
                val cuerda = if (oscuro) Color(0xFFCDBA8A) else Color(0xFF8A7448)
                drawLine(cuerda, Offset(w * 0.055f, 0f), Offset(w * 0.075f, h), 1f * u)
                drawLine(cuerda, Offset(w * 0.08f, 0f), Offset(w * 0.06f, h), 1f * u)
                drawCircle(vivo(BARRO), h * 0.06f, Offset(w * 0.068f, h * 0.5f))
                drawLine(Color.Black.copy(alpha = 0.35f), Offset(w * 0.068f - h * 0.03f, h * 0.5f), Offset(w * 0.068f + h * 0.03f, h * 0.5f), 0.7f * u)
            }

            // El detalle del tipo, arriba a la derecha.
            val o = Offset(w * 0.9f, h * 0.11f); val rr = h * 0.065f
            when (e.tipo) {
                "idea" -> {
                    drawCircle(vivo(ORO), rr * 0.8f, o)
                    drawCircle(vivo(ROJO), rr * 0.8f, o, style = Stroke(rr * 0.2f))
                }
                "pensamiento" -> {
                    // El ojo que protege.
                    val ojo = vivo(LAPIS)
                    drawPath(Path().apply { moveTo(o.x - rr, o.y); quadraticTo(o.x, o.y - rr * 0.9f, o.x + rr, o.y); quadraticTo(o.x, o.y + rr * 0.7f, o.x - rr, o.y); close() }, ojo, style = Stroke(rr * 0.2f))
                    drawCircle(ojo, rr * 0.3f, o)
                    drawLine(ojo, Offset(o.x - rr * 0.1f, o.y + rr * 0.35f), Offset(o.x - rr * 0.3f, o.y + rr * 1f), rr * 0.18f, StrokeCap.Round)
                }
                "proposito" -> drawPath(simboloCalificacion(TemaId.EGIPTO, o, rr), vivo(VERDE))
                "recordatorio" -> {
                    drawCircle(vivo(ROJO), rr * 0.7f, o)
                    drawCircle(if (oscuro) YESO else NEGRO, rr * 0.7f, o, style = Stroke(rr * 0.18f))
                }
                else -> {}
            }

            // Si está fijada, un terrón de barro sellado arriba al centro, con los dos cabos del cordel.
            if (e.fijada) {
                val sl = Offset(w * 0.5f, h * 0.085f)
                val cuerda = if (oscuro) Color(0xFFCDBA8A) else Color(0xFF8A7448)
                drawLine(cuerda, sl, Offset(sl.x - w * 0.05f, h * 0.15f), 0.9f * u)
                drawLine(cuerda, sl, Offset(sl.x + w * 0.04f, h * 0.16f), 0.9f * u)
                drawCircle(Color.Black.copy(alpha = 0.3f), h * 0.055f, Offset(sl.x + 0.6f * u, sl.y + 0.9f * u))
                drawCircle(vivo(BARRO), h * 0.055f, sl)
                drawCircle(Color.Black.copy(alpha = 0.3f), h * 0.03f, sl, style = Stroke(0.6f * u))
            }
        }
    }

    /** Un ostracon de caliza: una lasca de bordes redondeados, irregular, dentro de [r]. */
    private fun guijarro(r: Rect, semilla: Int, hondo: Float): Path {
        val rnd = Random(semilla.toLong() * 6007L + 11L)
        val m = minOf(r.width, r.height) * hondo
        val pts = List(12) { k ->
            val t = k / 12f
            val adentro = m * (0.2f + 0.8f * rnd.nextFloat())
            when {
                t < 0.25f -> Offset(r.left + r.width * (t / 0.25f), r.top + adentro)
                t < 0.5f -> Offset(r.right - adentro, r.top + r.height * ((t - 0.25f) / 0.25f))
                t < 0.75f -> Offset(r.right - r.width * ((t - 0.5f) / 0.25f), r.bottom - adentro)
                else -> Offset(r.left + adentro, r.bottom - r.height * ((t - 0.75f) / 0.25f))
            }
        }
        fun medio(a: Offset, b: Offset) = Offset((a.x + b.x) / 2, (a.y + b.y) / 2)
        return Path().apply {
            val p0 = medio(pts.last(), pts.first())
            moveTo(p0.x, p0.y)
            for (k in pts.indices) {
                val a = pts[k]; val b = medio(a, pts[(k + 1) % pts.size])
                quadraticTo(a.x, a.y, b.x, b.y)
            }
            close()
        }
    }

    /** Una hoja de papiro: casi rectangular, con el borde derecho deshilachado. */
    private fun hojaPapiro(r: Rect, semilla: Int): Path {
        val rnd = Random(semilla.toLong() * 3001L + 7L)
        val x0 = r.left + r.width * 0.025f; val x1 = r.right - r.width * 0.025f
        val y0 = r.top + r.height * 0.035f; val y1 = r.bottom - r.height * 0.035f
        return Path().apply {
            moveTo(x0, y0); lineTo(x1, y0)
            for (k in 1..7) lineTo(x1 - r.width * 0.022f * rnd.nextFloat(), y0 + (y1 - y0) * k / 8f)
            lineTo(x1, y1); lineTo(x0, y1); close()
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E4C0) else Color(0xFF231A0E)
}
