package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Laboratorio: los muebles de Vida y de Diario del laboratorio limpio, de la misma casa que su estante abierto de reactivos,
 * su armario clínico de muestras y su sala blanca de logros. Blanco clínico, acero gris, vidrio, cian y violeta, y todo se
 * mide: reglas graduadas, papel milimetrado y graduaciones en cada frasco.
 * - Social: la sala de reuniones con la mesa de vidrio en perspectiva, sus patas de acero, las sillas de laboratorio a los
 *   lados, un vaso de precipitados y un matraz en las puntas de la mesa, la regla graduada del borde de arriba y, en las
 *   esquinas, un anillo de benceno y una molécula de bolas y varillas sobre el papel milimetrado.
 * - Mapas: la mesa de luz con su marco de aluminio y el panel encendido (de noche, cian), el papel milimetrado, la regla de
 *   acrílico abajo y la de la izquierda, la escuadra violeta arriba a la derecha, dos marcas de registro y un lápiz.
 * - Otras vidas: la vitrina de muestras: un frasco de vidrio con su tapón de color y sus graduaciones por medalla, sobre la
 *   repisa de resina con canto cian, y bajo cada frasco su etiqueta impresa con franja cian y código de barras.
 * - Íconos: el edificio de investigación con su cúpula de observatorio; el portaobjetos con el cubreobjetos raspado; tres
 *   matraces como marcadores.
 * - Notas: la pizarra blanca con marco de aluminio, fórmulas abstractas (una curva en sus ejes, un anillo, una reacción con su
 *   flecha, barras) que asoman entre las notas, y la bandeja con dos marcadores y el borrador. Las notas son post-its de
 *   color pálido según el tipo, con su franja adhesiva, la esquina levantada y un imán cian si están fijadas.
 */
internal object VidaLaboratorio : DisenoVida {
    private val BLANCO = Color(0xFFF2F6F8)
    private val GRIS = Color(0xFFAFB9C1)
    private val GRIS_OSC = Color(0xFF6B7781)
    private val CIAN = Color(0xFF1FB5C9)
    private val VIOLETA = Color(0xFF7A4FD0)
    private val ROSA = Color(0xFFE0557A)
    private val AMARILLO = Color(0xFFF2C232)
    private val VERDE = Color(0xFF4AB86A)
    private val VIDRIO = Color(0xFFCFE8EE)
    private val TINTA = Color(0xFF12202B)
    /** Los tapones de los frascos: cian, violeta, rosa, amarillo y verde. */
    private val TAPONES = listOf(CIAN, VIOLETA, ROSA, AMARILLO, VERDE)

    private fun DrawScope.u() = size.minDimension / 100f
    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un material del laboratorio sobre la tarjeta: en claro, tal cual; en oscuro, hundido hacia el fondo. */
    private fun m(c: Color, e: EstadoMueble, oscuro: Boolean, hundido: Float = 0.5f): Color = if (oscuro) mezcla(c, e.fondo, hundido) else c

    // ---------------------------------------------------------------- piezas

    /** El papel milimetrado en [r]: una línea cada [paso]. */
    private fun DrawScope.cuadricula(r: Rect, paso: Float, color: Color, grosor: Float) {
        if (paso < 1f) return
        val nx = (r.width / paso).toInt().coerceIn(0, 120)
        val ny = (r.height / paso).toInt().coerceIn(0, 120)
        for (k in 1..nx) drawLine(color, Offset(r.left + k * paso, r.top), Offset(r.left + k * paso, r.bottom), grosor)
        for (k in 1..ny) drawLine(color, Offset(r.left, r.top + k * paso), Offset(r.right, r.top + k * paso), grosor)
    }

    /** Una regla graduada desde [o], de [largo]: una marca cada [paso], más larga cada cinco y cada diez. */
    private fun DrawScope.regla(o: Offset, largo: Float, paso: Float, alto: Float, color: Color, grosor: Float, vertical: Boolean = false) {
        if (paso < 0.5f) return
        val n = (largo / paso).toInt().coerceIn(0, 160)
        for (k in 0..n) {
            val l = alto * (if (k % 10 == 0) 1f else if (k % 5 == 0) 0.7f else 0.4f)
            val s = k * paso
            if (vertical) drawLine(color, Offset(o.x, o.y + s), Offset(o.x + l, o.y + s), grosor)
            else drawLine(color, Offset(o.x + s, o.y), Offset(o.x + s, o.y + l), grosor)
        }
    }

    /** El anillo de benceno: el hexágono con su círculo de enlaces por dentro. */
    private fun DrawScope.benceno(c: Offset, r: Float, color: Color, grosor: Float) {
        val p = Path()
        for (k in 0 until 6) {
            val a = Math.toRadians(30.0 + 60.0 * k)
            val x = c.x + r * cos(a).toFloat(); val y = c.y + r * sin(a).toFloat()
            if (k == 0) p.moveTo(x, y) else p.lineTo(x, y)
        }
        p.close()
        drawPath(p, color, style = Stroke(grosor, join = StrokeJoin.Round))
        drawCircle(color, r * 0.55f, c, style = Stroke(grosor * 0.8f))
    }

    /** Una molécula de bolas y varillas: el átomo central con tres enlaces (un poco distintos cada vez). */
    private fun DrawScope.molecula(c: Offset, r: Float, atomo: Color, otro: Color, enlace: Color, rnd: Random, grosor: Float) {
        listOf(-150.0, -30.0, 90.0).forEachIndexed { i, a0 ->
            val a = Math.toRadians(a0 + (rnd.nextDouble() - 0.5) * 24.0)
            val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
            drawLine(enlace, c, p, grosor)
            drawCircle(if (i == 1) otro else atomo, r * 0.3f, p)
        }
        drawCircle(atomo, r * 0.42f, c)
        drawCircle(Color.White.copy(alpha = 0.45f), r * 0.13f, Offset(c.x - r * 0.14f, c.y - r * 0.14f))
    }

    /** El contorno de un matraz de Erlenmeyer con la base en [b]. */
    private fun matrazPath(b: Offset, alto: Float): Path {
        val ancho = alto * 0.72f
        return Path().apply {
            moveTo(b.x - ancho / 2, b.y); lineTo(b.x - ancho * 0.13f, b.y - alto * 0.6f); lineTo(b.x - ancho * 0.13f, b.y - alto)
            lineTo(b.x + ancho * 0.13f, b.y - alto); lineTo(b.x + ancho * 0.13f, b.y - alto * 0.6f); lineTo(b.x + ancho / 2, b.y); close()
        }
    }

    /** Un matraz con su líquido. */
    private fun DrawScope.matraz(b: Offset, alto: Float, vidrio: Color, liquido: Color, linea: Color, grosor: Float) {
        val p = matrazPath(b, alto)
        drawPath(p, vidrio)
        clipPath(p) { drawRect(liquido, Offset(b.x - alto, b.y - alto * 0.36f), Size(alto * 2, alto * 0.36f)) }
        drawPath(p, linea, style = Stroke(grosor, join = StrokeJoin.Round))
    }

    /** Un vaso de precipitados con su pico, su líquido y tres graduaciones; la base en [b]. */
    private fun DrawScope.vaso(b: Offset, alto: Float, liquido: Color, linea: Color, grosor: Float) {
        val an = alto * 0.78f
        drawRect(liquido, Offset(b.x - an / 2, b.y - alto * 0.45f), Size(an, alto * 0.45f))
        val p = Path().apply {
            moveTo(b.x - an / 2 - alto * 0.12f, b.y - alto); lineTo(b.x - an / 2, b.y - alto * 0.88f); lineTo(b.x - an / 2, b.y)
            lineTo(b.x + an / 2, b.y); lineTo(b.x + an / 2, b.y - alto)
        }
        drawPath(p, linea, style = Stroke(grosor, join = StrokeJoin.Round))
        for (k in 1..3) drawLine(linea, Offset(b.x + an * 0.18f, b.y - alto * 0.22f * k), Offset(b.x + an / 2, b.y - alto * 0.22f * k), grosor * 0.7f)
    }

    /**
     * Una silla de laboratorio de perfil, mirando hacia [dir] (1 a la derecha, −1 a la izquierda): el respaldo, el asiento, la
     * columna de gas y la base con sus ruedas; [x] es la columna y [piso] el suelo.
     */
    private fun DrawScope.silla(x: Float, piso: Float, s: Float, dir: Float, estructura: Color, cojin: Color) {
        val xb = x - dir * 5f * s
        drawRoundRect(cojin, Offset(xb - 1.6f * s, piso - 30f * s), Size(3.2f * s, 14f * s), CornerRadius(1.6f * s))
        drawLine(estructura, Offset(xb, piso - 16.5f * s), Offset(xb + dir * 2.4f * s, piso - 14f * s), 1f * s, StrokeCap.Round)
        val x0 = minOf(xb - 1.6f * s, x + dir * 6f * s); val x1 = maxOf(xb + 1.6f * s, x + dir * 6f * s)
        drawRoundRect(cojin, Offset(x0, piso - 15.5f * s), Size(x1 - x0, 2.8f * s), CornerRadius(1.4f * s))
        drawLine(estructura, Offset(x, piso - 12.7f * s), Offset(x, piso - 3.4f * s), 1.3f * s)
        drawLine(estructura, Offset(x - 5f * s, piso - 2.4f * s), Offset(x + 5f * s, piso - 2.4f * s), 1f * s, StrokeCap.Round)
        drawCircle(estructura, 1f * s, Offset(x - 4.6f * s, piso - 1.1f * s))
        drawCircle(estructura, 1f * s, Offset(x + 4.6f * s, piso - 1.1f * s))
    }

    /** Un código de barras de [n] barras en [r]. */
    private fun DrawScope.barras(r: Rect, n: Int, color: Color, rnd: Random) {
        val paso = r.width / n.coerceAtLeast(1)
        for (k in 0 until n.coerceIn(0, 24)) {
            val g = paso * (0.25f + 0.45f * rnd.nextFloat())
            drawRect(color, Offset(r.left + k * paso, r.top), Size(g, r.height))
        }
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        val linea = e.tinta.copy(alpha = 0.3f)
        // El papel milimetrado de la pared, apenas marcado: el centro queda tranquilo para el grafo.
        cuadricula(Rect(0f, 0f, w, h), 10f * u, e.acento.copy(alpha = if (oscuro) 0.05f else 0.06f), 0.3f * u)
        // La regla graduada del borde de arriba: todo se mide.
        regla(Offset(24f * u, 1.6f * u), w - 48f * u, 2.5f * u, 3.4f * u, e.tinta.copy(alpha = 0.26f), 0.3f * u)
        drawLine(e.tinta.copy(alpha = 0.2f), Offset(24f * u, 1.6f * u), Offset(w - 24f * u, 1.6f * u), 0.35f * u)
        // En las esquinas de arriba, el anillo de benceno y la molécula de bolas y varillas.
        benceno(Offset(11f * u, 17f * u), 6.5f * u, e.acento.copy(alpha = 0.55f), 0.7f * u)
        molecula(Offset(w - 11f * u, 18f * u), 7f * u, e.secundario.copy(alpha = 0.7f), e.acento.copy(alpha = 0.8f), linea, rnd, 0.8f * u)
        // El piso de resina con su línea cian.
        drawLine(e.acento.copy(alpha = 0.35f), Offset(0f, h - 1.2f * u), Offset(w, h - 1.2f * u), 0.6f * u)
        // Las sillas de laboratorio, una a cada lado, mirando a la mesa.
        val estructura = m(GRIS_OSC, e, oscuro, 0.35f).copy(alpha = 0.75f)
        silla(11f * u, h - 1.6f * u, u, 1f, estructura, e.acento.copy(alpha = 0.35f))
        silla(w - 11f * u, h - 1.6f * u, u, -1f, estructura, e.secundario.copy(alpha = 0.35f))
        // La mesa de vidrio en perspectiva: el tablero tenue, su canto cian, los reflejos y las patas de acero.
        val yT = h - 20f * u; val yF = h - 13f * u
        val tablero = Path().apply { moveTo(27f * u, yT); lineTo(w - 27f * u, yT); lineTo(w - 18f * u, yF); lineTo(18f * u, yF); close() }
        drawPath(tablero, e.acento.copy(alpha = if (oscuro) 0.10f else 0.07f))
        drawPath(tablero, e.acento.copy(alpha = 0.45f), style = Stroke(0.6f * u, join = StrokeJoin.Round))
        drawRect(e.acento.copy(alpha = 0.4f), Offset(18f * u, yF), Size(w - 36f * u, 1.3f * u))
        val reflejo = if (oscuro) Color.White.copy(alpha = 0.12f) else e.acento.copy(alpha = 0.18f)
        drawLine(reflejo, Offset(38f * u, yT + 1f * u), Offset(34f * u, yF - 1f * u), 0.7f * u)
        drawLine(reflejo, Offset(42f * u, yT + 1f * u), Offset(39f * u, yF - 2.5f * u), 0.4f * u)
        listOf(31f * u, w - 31f * u).forEach { x ->
            drawLine(estructura, Offset(x, yF + 1.3f * u), Offset(x, h - 2.2f * u), 1.4f * u)
            drawLine(estructura, Offset(x - 3f * u, h - 2f * u), Offset(x + 3f * u, h - 2f * u), 1f * u, StrokeCap.Round)
        }
        // En las puntas de la mesa, el vaso de precipitados y el matraz.
        vaso(Offset(31f * u, yT - 0.2f * u), 6.5f * u, e.acento.copy(alpha = 0.35f), linea, 0.5f * u)
        matraz(Offset(w - 32f * u, yT - 0.2f * u), 8f * u, e.fondo.copy(alpha = 0.5f), e.secundario.copy(alpha = 0.45f), linea, 0.5f * u)
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        // La mesa de luz: el marco de aluminio y el panel encendido (de noche, el panel brilla en cian).
        drawRoundRect(mezcla(e.fondo, GRIS, if (oscuro) 0.3f else 0.5f), Offset(1.2f * u, 1.2f * u), Size(w - 2.4f * u, h - 2.4f * u), CornerRadius(3f * u))
        val panel = Rect(3.6f * u, 3.6f * u, w - 3.6f * u, h - 3.6f * u)
        drawRoundRect(if (oscuro) mezcla(e.fondo, e.acento, 0.12f) else mezcla(e.fondo, CIAN, 0.05f), panel.topLeft, panel.size, CornerRadius(1.6f * u))
        drawRoundRect(if (oscuro) e.acento.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.6f), Offset(panel.left + 6f * u, panel.top + 5f * u),
            Size(panel.width - 12f * u, panel.height - 10f * u), CornerRadius(6f * u))
        drawRoundRect(e.tinta.copy(alpha = 0.22f), panel.topLeft, panel.size, CornerRadius(1.6f * u), style = Stroke(0.4f * u))
        // El papel milimetrado sobre el panel: fino cada 5 y más marcado cada 25.
        cuadricula(panel, 5f * u, e.acento.copy(alpha = 0.06f), 0.25f * u)
        cuadricula(panel, 25f * u, e.acento.copy(alpha = 0.12f), 0.35f * u)
        // Las marcas de registro de dos esquinas.
        listOf(Offset(w - 7.5f * u, h - 7f * u), Offset(w - 7.5f * u, 33f * u)).forEach { o ->
            drawCircle(e.tinta.copy(alpha = 0.35f), 1.8f * u, o, style = Stroke(0.35f * u))
            drawLine(e.tinta.copy(alpha = 0.35f), Offset(o.x - 3f * u, o.y), Offset(o.x + 3f * u, o.y), 0.35f * u)
            drawLine(e.tinta.copy(alpha = 0.35f), Offset(o.x, o.y - 3f * u), Offset(o.x, o.y + 3f * u), 0.35f * u)
        }
        // Las reglas de acrílico: una abajo y otra a la izquierda, con sus graduaciones.
        val acrilico = if (oscuro) e.tinta.copy(alpha = 0.08f) else VIDRIO.copy(alpha = 0.7f)
        val borde = e.tinta.copy(alpha = 0.35f)
        val rh = Rect(w * 0.15f, h - 9.4f * u, w * 0.74f, h - 4.6f * u)
        drawRect(acrilico, rh.topLeft, rh.size)
        drawRect(borde, rh.topLeft, rh.size, style = Stroke(0.35f * u))
        regla(Offset(rh.left + 1f * u, rh.top), rh.width - 2f * u, 1.25f * u, 2.6f * u, e.tinta.copy(alpha = 0.5f), 0.22f * u)
        val rv = Rect(4.8f * u, h * 0.15f, 9.6f * u, h * 0.72f)
        drawRect(acrilico, rv.topLeft, rv.size)
        drawRect(borde, rv.topLeft, rv.size, style = Stroke(0.35f * u))
        regla(Offset(rv.left, rv.top + 1f * u), rv.height - 2f * u, 1.25f * u, 2.6f * u, e.tinta.copy(alpha = 0.5f), 0.22f * u, vertical = true)
        // La escuadra de acrílico violeta, arriba a la derecha, con su calado.
        val a = Offset(w - 5f * u, 5f * u)
        val tri = Path().apply { moveTo(a.x, a.y); lineTo(a.x - 30f * u, a.y); lineTo(a.x, a.y + 23f * u); close() }
        drawPath(tri, e.secundario.copy(alpha = 0.16f))
        drawPath(tri, e.secundario.copy(alpha = 0.6f), style = Stroke(0.45f * u, join = StrokeJoin.Round))
        val hueco = Path().apply { moveTo(a.x - 4f * u, a.y + 3f * u); lineTo(a.x - 16f * u, a.y + 3f * u); lineTo(a.x - 4f * u, a.y + 12.2f * u); close() }
        drawPath(hueco, e.fondo.copy(alpha = 0.45f))
        drawPath(hueco, e.secundario.copy(alpha = 0.5f), style = Stroke(0.35f * u, join = StrokeJoin.Round))
        // El lápiz, abajo a la derecha, un poco movido cada vez.
        val ly = h - 6.6f * u + rnd.nextFloat() * 0.8f * u
        val lx0 = w * 0.78f; val lx1 = w - 9f * u
        if (lx1 - lx0 > 8f * u) {
            drawRect(m(AMARILLO, e, oscuro, 0.4f), Offset(lx0 + 3f * u, ly - 1f * u), Size(lx1 - lx0 - 3f * u, 2f * u))
            drawRect(m(GRIS, e, oscuro, 0.4f), Offset(lx1 - 2.4f * u, ly - 1f * u), Size(2.4f * u, 2f * u))
            drawPath(Path().apply { moveTo(lx0 + 3f * u, ly - 1f * u); lineTo(lx0, ly); lineTo(lx0 + 3f * u, ly + 1f * u); close() }, m(GRIS_OSC, e, oscuro, 0.3f))
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        val linea = e.tinta.copy(alpha = 0.24f)
        // El gabinete: el fondo tenue de la vitrina, el marco de aluminio y la franja cian de arriba.
        val cuerpo = Rect(w * 0.015f, h * 0.035f, w * 0.985f, h * 0.975f)
        drawRoundRect(e.tinta.copy(alpha = 0.03f), cuerpo.topLeft, cuerpo.size, CornerRadius(2f * u))
        drawRoundRect(linea, cuerpo.topLeft, cuerpo.size, CornerRadius(2f * u), style = Stroke(0.7f * u))
        drawRect(e.acento.copy(alpha = 0.5f), Offset(cuerpo.left + 2f * u, cuerpo.top + 1.4f * u), Size(cuerpo.width - 4f * u, 1f * u))
        // La repisa de resina blanca con su canto cian.
        val y = h * 0.8f
        drawRect(m(BLANCO, e, oscuro, 0.7f), Offset(cuerpo.left + 1f * u, y), Size(cuerpo.width - 2f * u, 2.6f * u))
        drawLine(linea, Offset(cuerpo.left + 1f * u, y), Offset(cuerpo.right - 1f * u, y), 0.4f * u)
        drawLine(e.acento.copy(alpha = 0.7f), Offset(cuerpo.left + 1f * u, y + 2.6f * u), Offset(cuerpo.right - 1f * u, y + 2.6f * u), 0.6f * u)
        // Un frasco de muestras por medalla, con su tapón y sus graduaciones, y bajo la repisa su etiqueta.
        val paso = w * 0.92f / 5f
        val vidrio = if (oscuro) e.acento.copy(alpha = 0.06f) else VIDRIO.copy(alpha = 0.28f)
        val brillo = if (oscuro) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.85f)
        val giro = rnd.nextInt(TAPONES.size)
        repeat(5) { i ->
            val cx = w * 0.04f + paso * (i + 0.5f)
            val fw = paso * 0.86f
            val tope = h * 0.13f
            val frasco = Rect(cx - fw / 2, tope + 3.4f * u, cx + fw / 2, y - 0.3f * u)
            drawRoundRect(vidrio, frasco.topLeft, frasco.size, CornerRadius(4f * u))
            drawRoundRect(linea, frasco.topLeft, frasco.size, CornerRadius(4f * u), style = Stroke(0.5f * u))
            drawLine(brillo, Offset(frasco.left + 2.2f * u, frasco.top + 4f * u), Offset(frasco.left + 2.2f * u, frasco.bottom - 6f * u), 0.9f * u, StrokeCap.Round)
            // Las graduaciones del costado derecho.
            for (k in 1..4) {
                val gy = frasco.bottom - frasco.height * 0.16f * k
                drawLine(linea, Offset(frasco.right - (if (k % 2 == 0) 3.2f else 2f) * u, gy), Offset(frasco.right - 0.6f * u, gy), 0.3f * u)
            }
            // El cuello y el tapón de color.
            val tapon = m(TAPONES[(i + giro) % TAPONES.size], e, oscuro, 0.25f)
            drawRect(linea, Offset(cx - fw * 0.36f, tope + 2.6f * u), Size(fw * 0.72f, 0.8f * u))
            drawRoundRect(tapon.copy(alpha = 0.85f), Offset(cx - fw * 0.32f, tope), Size(fw * 0.64f, 2.8f * u), CornerRadius(0.8f * u))
            // La etiqueta impresa bajo la repisa.
            val et = Rect(cx - fw * 0.34f, y + 4.4f * u, cx + fw * 0.34f, minOf(y + 12.4f * u, cuerpo.bottom - 1.2f * u))
            if (et.height > 3f * u) {
                drawRect(m(BLANCO, e, oscuro, 0.72f), et.topLeft, et.size)
                drawRect(linea, et.topLeft, et.size, style = Stroke(0.3f * u))
                drawRect(e.acento.copy(alpha = 0.7f), et.topLeft, Size(et.width, 1f * u))
                barras(Rect(et.left + et.width * 0.1f, et.top + et.height * 0.4f, et.left + et.width * 0.62f, et.bottom - et.height * 0.18f), 9, e.tinta.copy(alpha = 0.45f), rnd)
                drawCircle(m(ROSA, e, oscuro, 0.3f), et.height * 0.16f, Offset(et.right - et.width * 0.16f, et.center.y + et.height * 0.1f))
            }
        }
    }

    // ---------------------------------------------------------------- Íconos de mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.36f
        val g = 1.6f * density
        val trazo = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (id) {
            "unesco" -> {
                // El edificio de investigación: las gradas, el cuerpo con sus ventanas, la puerta y la cúpula del observatorio.
                drawRect(e.c2, Offset(c.x - r * 1.25f, c.y + r * 0.8f), Size(r * 2.5f, r * 0.14f))
                val cuerpo = Rect(c.x - r * 1.05f, c.y - r * 0.18f, c.x + r * 1.05f, c.y + r * 0.8f)
                drawRect(e.c1.copy(alpha = 0.16f), cuerpo.topLeft, cuerpo.size)
                drawRect(e.c1, cuerpo.topLeft, cuerpo.size, style = trazo)
                for (fila in 0 until 2) for (col in 0 until 4) {
                    if (fila == 1 && (col == 1 || col == 2)) continue
                    val x = cuerpo.left + cuerpo.width * (0.12f + col * 0.21f)
                    drawRect(e.c1, Offset(x, cuerpo.top + cuerpo.height * (0.16f + fila * 0.42f)), Size(cuerpo.width * 0.13f, cuerpo.height * 0.22f))
                }
                drawRect(e.c2, Offset(c.x - r * 0.18f, c.y + r * 0.38f), Size(r * 0.36f, r * 0.42f))
                drawRect(e.c1, Offset(c.x - r * 0.62f, c.y - r * 0.32f), Size(r * 1.24f, r * 0.14f))
                drawArc(e.c2, 180f, 180f, true, Offset(c.x - r * 0.55f, c.y - r * 0.87f), Size(r * 1.1f, r * 1.1f))
                drawArc(e.c1, 180f, 180f, false, Offset(c.x - r * 0.55f, c.y - r * 0.87f), Size(r * 1.1f, r * 1.1f), style = trazo)
                drawRect(e.fondo, Offset(c.x - r * 0.07f, c.y - r * 0.82f), Size(r * 0.14f, r * 0.48f))
                drawLine(e.c1, Offset(c.x + r * 0.32f, c.y - r * 0.66f), Offset(c.x + r * 0.5f, c.y - r * 1.02f), g)
            }
            "rascar" -> {
                // El portaobjetos de vidrio: el extremo esmerilado, el cubreobjetos con su muestra y la esquina raspada.
                val vidrio = Rect(c.x - r * 1.55f, c.y - r * 0.55f, c.x + r * 1.55f, c.y + r * 0.55f)
                drawRoundRect(e.c2.copy(alpha = 0.16f), vidrio.topLeft, vidrio.size, CornerRadius(r * 0.08f))
                drawRect(e.c1.copy(alpha = 0.45f), vidrio.topLeft, Size(r * 0.62f, vidrio.height))
                for (k in 1..3) drawLine(e.fondo, Offset(vidrio.left + r * 0.12f, vidrio.top + vidrio.height * 0.22f * k + r * 0.02f),
                    Offset(vidrio.left + r * 0.5f, vidrio.top + vidrio.height * 0.22f * k + r * 0.02f), g * 0.7f)
                val cubre = Rect(c.x - r * 0.3f, c.y - r * 0.42f, c.x + r * 1.15f, c.y + r * 0.42f)
                drawRect(e.c2.copy(alpha = 0.6f), cubre.topLeft, cubre.size)
                val raspado = Path().apply {
                    moveTo(cubre.left + cubre.width * 0.35f, cubre.top); lineTo(cubre.right, cubre.top); lineTo(cubre.right, cubre.top + cubre.height * 0.78f)
                    lineTo(cubre.right - cubre.width * 0.14f, cubre.top + cubre.height * 0.6f); lineTo(cubre.right - cubre.width * 0.2f, cubre.top + cubre.height * 0.66f)
                    lineTo(cubre.right - cubre.width * 0.36f, cubre.top + cubre.height * 0.36f); lineTo(cubre.right - cubre.width * 0.44f, cubre.top + cubre.height * 0.4f)
                    lineTo(cubre.left + cubre.width * 0.5f, cubre.top + cubre.height * 0.14f); close()
                }
                drawPath(raspado, e.c1)
                drawRect(e.c1, cubre.topLeft, cubre.size, style = Stroke(g * 0.7f))
                drawRoundRect(e.c1, vidrio.topLeft, vidrio.size, CornerRadius(r * 0.08f), style = trazo)
            }
            else -> {
                // Tus lugares: tres matraces como marcadores, cada uno con su punto en el suelo.
                listOf(Offset(-0.95f, 0.2f) to 0.85f, Offset(0f, -0.15f) to 1.05f, Offset(0.95f, 0.1f) to 0.9f).forEach { (o, k) ->
                    val b = Offset(c.x + o.x * r, c.y + o.y * r + r * 0.6f)
                    drawOval(e.c1.copy(alpha = 0.25f), Offset(b.x - r * 0.42f * k, b.y - r * 0.06f), Size(r * 0.84f * k, r * 0.16f))
                    matraz(b, r * 1.15f * k, e.fondo, e.c2, e.c1, g)
                    drawCircle(e.c1, r * 0.07f, Offset(b.x, b.y + r * 0.02f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        if (u <= 0f) return
        val rnd = Random(e.semilla.toLong())
        // La pizarra blanca: el marco de aluminio y la superficie (de noche, el vidrio oscuro de la pizarra).
        drawRoundRect(mezcla(e.fondo, GRIS, if (oscuro) 0.3f else 0.55f), Offset.Zero, size, CornerRadius(2.4f * u))
        val sup = Rect(1.8f * u, 1.8f * u, w - 1.8f * u, h - 1.8f * u)
        drawRoundRect(if (oscuro) mezcla(e.fondo, Color.White, 0.04f) else Color(0xFFFDFEFE), sup.topLeft, sup.size, CornerRadius(1.2f * u))
        drawRoundRect(e.tinta.copy(alpha = 0.18f), sup.topLeft, sup.size, CornerRadius(1.2f * u), style = Stroke(0.35f * u))
        // Las fórmulas a medio borrar, en los huecos de la cuadrícula de notas: se ven donde no hay nota.
        val mx = w * 0.06f; val my = h * 0.08f; val hueco = w * 0.025f
        val cw = (w - 2 * mx - 2 * hueco) / 3f; val ch = (h - 2 * my - hueco) / 2f
        val celda = { i: Int -> Rect(mx + (i % 3) * (cw + hueco), my + (i / 3) * (ch + hueco), mx + (i % 3) * (cw + hueco) + cw, my + (i / 3) * (ch + hueco) + ch) }
        val g = 0.7f * u
        val cian = e.acento.copy(alpha = 0.4f); val violeta = e.secundario.copy(alpha = 0.4f); val negro = e.tinta.copy(alpha = 0.3f)
        // Una curva en sus ejes.
        celda(2).let { r ->
            drawLine(negro, Offset(r.left + r.width * 0.2f, r.bottom - r.height * 0.2f), Offset(r.right - r.width * 0.12f, r.bottom - r.height * 0.2f), g, StrokeCap.Round)
            drawLine(negro, Offset(r.left + r.width * 0.2f, r.bottom - r.height * 0.2f), Offset(r.left + r.width * 0.2f, r.top + r.height * 0.15f), g, StrokeCap.Round)
            val p = Path()
            for (k in 0..16) {
                val t = k / 16f
                val x = r.left + r.width * (0.2f + 0.66f * t)
                val yy = r.bottom - r.height * (0.25f + 0.5f * t * t * (3 - 2 * t)) + r.height * 0.06f * sin(t * 12f)
                if (k == 0) p.moveTo(x, yy) else p.lineTo(x, yy)
            }
            drawPath(p, cian, style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        // Un anillo de benceno con un sustituyente.
        celda(3).let { r ->
            val c = Offset(r.left + r.width * 0.42f, r.center.y)
            benceno(c, r.height * 0.24f, violeta, g)
            drawLine(violeta, Offset(c.x + r.height * 0.21f, c.y - r.height * 0.12f), Offset(c.x + r.height * 0.46f, c.y - r.height * 0.26f), g, StrokeCap.Round)
        }
        // Una reacción: dos bolitas, la flecha y el producto.
        celda(4).let { r ->
            val yy = r.center.y
            drawCircle(cian, r.height * 0.09f, Offset(r.left + r.width * 0.2f, yy), style = Stroke(g))
            drawCircle(violeta, r.height * 0.09f, Offset(r.left + r.width * 0.36f, yy), style = Stroke(g))
            drawLine(negro, Offset(r.left + r.width * 0.48f, yy), Offset(r.left + r.width * 0.68f, yy), g, StrokeCap.Round)
            drawPath(Path().apply { moveTo(r.left + r.width * 0.64f, yy - r.height * 0.06f); lineTo(r.left + r.width * 0.69f, yy); lineTo(r.left + r.width * 0.64f, yy + r.height * 0.06f) },
                negro, style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
            molecula(Offset(r.left + r.width * 0.82f, yy), r.height * 0.14f, cian, violeta, negro, rnd, g * 0.7f)
        }
        // Unas barras con su eje.
        celda(5).let { r ->
            val base = r.bottom - r.height * 0.2f
            drawLine(negro, Offset(r.left + r.width * 0.15f, base), Offset(r.right - r.width * 0.15f, base), g, StrokeCap.Round)
            for (k in 0 until 5) {
                val alto = r.height * (0.15f + 0.45f * rnd.nextFloat())
                drawRect(if (k % 2 == 0) cian else violeta, Offset(r.left + r.width * (0.2f + k * 0.13f), base - alto), Size(r.width * 0.08f, alto), style = Stroke(g))
            }
        }
        // La bandeja de los marcadores con el cian, el violeta y el borrador.
        val by = h - 1.8f * u
        val bandeja = Rect(w * 0.22f, by - 2.2f * u, w * 0.78f, by)
        drawRect(mezcla(e.fondo, GRIS, if (oscuro) 0.4f else 0.65f), bandeja.topLeft, bandeja.size)
        drawRoundRect(m(CIAN, e, oscuro, 0.2f), Offset(bandeja.left + bandeja.width * 0.1f, by - 4f * u), Size(14f * u, 1.8f * u), CornerRadius(0.9f * u))
        drawRoundRect(m(VIOLETA, e, oscuro, 0.2f), Offset(bandeja.left + bandeja.width * 0.1f + 16f * u, by - 4f * u), Size(14f * u, 1.8f * u), CornerRadius(0.9f * u))
        val borrador = Rect(bandeja.right - bandeja.width * 0.1f - 12f * u, by - 5f * u, bandeja.right - bandeja.width * 0.1f, by - 2.2f * u)
        drawRect(m(GRIS_OSC, e, oscuro, 0.3f), borrador.topLeft, borrador.size)
        drawRect(m(BLANCO, e, oscuro, 0.6f), Offset(borrador.left, borrador.bottom - 0.9f * u), Size(borrador.width, 0.9f * u))
    }

    /** El papel del post-it según el tipo de nota: pálido de día, hondo de noche (la tinta de [tintaNota] se lee en todos). */
    private fun papel(tipo: String, oscuro: Boolean): Color = when (tipo) {
        "idea" -> if (oscuro) Color(0xFF123840) else Color(0xFFD7F1F5)
        "proposito" -> if (oscuro) Color(0xFF173828) else Color(0xFFDCF1E2)
        "recordatorio" -> if (oscuro) Color(0xFF3A3416) else Color(0xFFFFF1B3)
        else -> if (oscuro) Color(0xFF28234D) else Color(0xFFE7E2FB)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val k = size.minDimension / 100f
        if (k <= 0f) return
        val papel = papel(e.tipo, oscuro)
        val hoja = Rect(r.left, r.top, r.right - 2f * k, r.bottom - 2.5f * k)
        // La sombra corta y el papel, con la franja adhesiva arriba y la esquina de abajo un poco levantada.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.10f), Offset(hoja.left + 2f * k, hoja.top + 2.5f * k), hoja.size)
        drawRect(papel, hoja.topLeft, hoja.size)
        drawRect(mezcla(papel, if (oscuro) Color.White else TINTA, 0.05f), hoja.topLeft, Size(hoja.width, hoja.height * 0.13f))
        val doblez = hoja.height * 0.16f
        val esquina = if (e.indice % 2 == 0) Path().apply {
            moveTo(hoja.right - doblez, hoja.bottom); lineTo(hoja.right, hoja.bottom - doblez); lineTo(hoja.right - doblez * 0.2f, hoja.bottom - doblez * 0.2f); close()
        } else Path().apply {
            moveTo(hoja.left + doblez, hoja.bottom); lineTo(hoja.left, hoja.bottom - doblez); lineTo(hoja.left + doblez * 0.2f, hoja.bottom - doblez * 0.2f); close()
        }
        drawPath(esquina, mezcla(papel, Color.Black, if (oscuro) 0.25f else 0.12f))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), hoja.topLeft, hoja.size, style = Stroke(0.6f * k))
        // La marca del tipo, arriba a la derecha (en el margen, lejos del texto).
        val marca = e.acento.copy(alpha = 0.85f)
        val mc = Offset(hoja.right - hoja.width * 0.07f, hoja.top + hoja.height * 0.08f)
        val s = hoja.height * 0.07f
        when (e.tipo) {
            "idea" -> {
                drawPath(matrazPath(Offset(mc.x, mc.y + s), s * 2f), marca)
            }
            "proposito" -> drawPath(Path().apply { moveTo(mc.x - s, mc.y); lineTo(mc.x - s * 0.3f, mc.y + s * 0.7f); lineTo(mc.x + s, mc.y - s * 0.8f) }, marca,
                style = Stroke(1.2f * k, cap = StrokeCap.Round, join = StrokeJoin.Round))
            "recordatorio" -> {
                drawCircle(marca, s, mc, style = Stroke(1f * k))
                drawLine(marca, mc, Offset(mc.x, mc.y - s * 0.7f), 1f * k, StrokeCap.Round)
                drawLine(marca, mc, Offset(mc.x + s * 0.55f, mc.y), 1f * k, StrokeCap.Round)
            }
            else -> {
                drawLine(marca, Offset(mc.x - s * 0.8f, mc.y + s * 0.2f), Offset(mc.x + s * 0.6f, mc.y - s * 0.5f), 0.8f * k)
                drawCircle(marca, s * 0.5f, Offset(mc.x - s * 0.8f, mc.y + s * 0.2f))
                drawCircle(marca, s * 0.36f, Offset(mc.x + s * 0.6f, mc.y - s * 0.5f))
            }
        }
        // Si está fijada, el imán redondo en el centro de la franja.
        if (e.fijada) {
            val ic = Offset(hoja.center.x, hoja.top + hoja.height * 0.07f)
            val ir = hoja.height * 0.075f
            drawCircle(Color.Black.copy(alpha = 0.25f), ir, Offset(ic.x + 0.8f * k, ic.y + 1f * k))
            drawCircle(e.acento, ir, ic)
            drawCircle(Color.White.copy(alpha = 0.5f), ir * 0.35f, Offset(ic.x - ir * 0.3f, ic.y - ir * 0.3f))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6F0F5) else TINTA
}
