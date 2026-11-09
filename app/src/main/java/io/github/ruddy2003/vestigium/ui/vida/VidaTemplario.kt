package io.github.ruddy2003.vestigium.ui.vida

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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.cruzPatada
import java.util.Random
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Templario: la encomienda de frontera, con los materiales de su biblioteca, su Ideario y su sala de armas: sillares de
 * arenisca, la sobreveste de lino blanco con la cruz patada roja, el baucent negro y blanco, hierro, roble, la arena del
 * desierto y el zócalo de almagra.
 * - Social: la sala capitular bajo la cruz patada (grande y tenue detrás de la red). A los lados, los muros de sillería
 *   con los escudos de la orden colgados; arriba, el dintel de piedra con su línea de almagra; abajo, el banco corrido de
 *   piedra y, al centro, la mesa del capítulo con el mantel de la cruz, la Regla, el cáliz y dos velas.
 * - Mapas: la mesa de campaña: un pergamino con la costa de Tierra Santa (apenas, bajo el mapa), las cantoneras de
 *   hierro, un castillo y dos estandartes como piezas, la espada tendida abajo y unos besantes de oro.
 * - Otras vidas: un muro de sillería con una hornacina apenas apuntada para cada medalla, con sus dovelas, su clave y su
 *   repisa; cruces patadas entre ellas y el zócalo de almagra abajo.
 * - Íconos: el castillo con su estandarte, una espada que raspa y tres cruces patadas.
 * - Notas: una puerta de tablas con sus bisagras de hierro de punta de lanza y su aldaba; cada nota es un pergamino con
 *   su sello de lacre (del color del tipo) clavado con un clavo o, si está fijada, con una daga.
 */
internal object VidaTemplario : DisenoVida {
    private val SILLAR = Color(0xFFC4B79C)
    private val SILLAR_LUZ = Color(0xFFDCD2BC)
    private val SILLAR_JUNTA = Color(0xFF8C826F)
    private val ROJO = Color(0xFFB0241E)
    private val BLANCO = Color(0xFFEFEADF)
    private val NEGRO = Color(0xFF17161A)
    private val CREMA = Color(0xFFEDE3CB)
    private val HIERRO = Color(0xFF4E5257)
    private val HIERRO_LUZ = Color(0xFFA8B0B8)
    private val MADERA = Color(0xFF6B4A2E)
    private val MADERA_OSC = Color(0xFF3E2A18)
    private val ARENA = Color(0xFFD8C49A)
    private val MAR = Color(0xFF4A6F8A)
    private val ALMAGRA = Color(0xFFA04A2A)
    private val ORO = Color(0xFFC99A4A)
    private val LACRE = Color(0xFF9E1B1B)
    private val CERA_AMARILLA = Color(0xFFC9962E)
    private val CERA_VERDE = Color(0xFF3E6B3A)
    private val CERA_PARDA = Color(0xFF4A3426)
    private val LLAMA = Color(0xFFF2B84A)
    private val NOCHE = Color(0xFF0F0D0B)

    /** El pergamino de las notas: claro de día; de noche, en la penumbra de la vigilia (con la tinta clara de [tintaNota]). */
    private val HOJA_CLARA = Color(0xFFF1E9D6)
    private val HOJA_OSCURA = Color(0xFF35302A)

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
     * Los puntos de un arco de [xL] a [xR] que arranca a la altura [ys]: dos arcos de radio [k] × la luz que se cortan en la
     * clave (con [k] = 0.5, medio punto; un poco más, apenas apuntado).
     */
    private fun arco(xL: Float, xR: Float, ys: Float, k: Float, pasos: Int = 8): List<Offset> {
        val luz = xR - xL; val r = luz * k; val mid = (xL + xR) / 2
        val th = acos(((mid - (xL + r)) / r).coerceIn(-1f, 1f).toDouble())
        val izq = (0..pasos).map { i ->
            val a = Math.PI + (th - Math.PI) * i / pasos
            Offset(xL + r + r * cos(a).toFloat(), ys - r * sin(a).toFloat())
        }
        val der = (1..pasos).map { i ->
            val a = (Math.PI - th) - (Math.PI - th) * i / pasos
            Offset(xR - r + r * cos(a).toFloat(), ys - r * sin(a).toFloat())
        }
        return izq + der
    }

    /** Un hueco con arco: de la base [yb] sube por las jambas hasta [ys] y cierra por el arco. */
    private fun hueco(xL: Float, xR: Float, ys: Float, yb: Float, k: Float): Path = Path().apply {
        moveTo(xL, yb)
        arco(xL, xR, ys, k).forEach { lineTo(it.x, it.y) }
        lineTo(xR, yb); close()
    }

    /** Un sello de lacre: un círculo con el borde ondulado de la cera aplastada. */
    private fun sello(c: Offset, r: Float): Path = Path().apply {
        val n = 18
        for (k in 0 until n) {
            val a = 2 * Math.PI * k / n
            val rr = if (k % 2 == 0) r else r * 0.9f
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Un muro de sillares en [r]: hiladas de [alto] con las juntas a matajunta y algún sillar un poco distinto. */
    private fun DrawScope.sillares(r: Rect, alto: Float, largo: Float, piedra: Color, junta: Color, rnd: Random, u: Float) {
        drawRect(piedra, r.topLeft, r.size)
        var y = r.top; var fila = 0
        while (y < r.bottom && fila < 30) {
            val yb = minOf(y + alto, r.bottom)
            var x = r.left - (if (fila % 2 == 0) 0f else largo / 2); var k = 0
            while (x < r.right && k < 40) {
                val x0 = maxOf(x, r.left); val x1 = minOf(x + largo, r.right)
                if (rnd.nextFloat() > 0.7f) drawRect(mez(piedra, junta, 0.12f), Offset(x0, y), Size(x1 - x0, yb - y))
                if (x > r.left) drawLine(junta, Offset(x, y), Offset(x, yb), 0.45f * u)
                x += largo; k++
            }
            if (y > r.top) drawLine(junta, Offset(r.left, y), Offset(r.right, y), 0.45f * u)
            y += alto; fila++
        }
    }

    /** Un escudo de lágrima colgado de su tiracol: blanco con la cruz roja o, si [baucent], el campo negro y blanco. */
    private fun DrawScope.escudo(c: Offset, an: Float, baucent: Boolean, blanco: Color, rojo: Color, negro: Color, borde: Color, u: Float) {
        val al = an * 1.2f
        val forma = Path().apply {
            moveTo(c.x - an / 2, c.y - al / 2); lineTo(c.x + an / 2, c.y - al / 2)
            quadraticTo(c.x + an * 0.55f, c.y + al * 0.15f, c.x, c.y + al / 2)
            quadraticTo(c.x - an * 0.55f, c.y + al * 0.15f, c.x - an / 2, c.y - al / 2); close()
        }
        drawLine(borde, Offset(c.x - an * 0.3f, c.y - al / 2), Offset(c.x, c.y - al / 2 - 2.4f * u), 0.4f * u)
        drawLine(borde, Offset(c.x + an * 0.3f, c.y - al / 2), Offset(c.x, c.y - al / 2 - 2.4f * u), 0.4f * u)
        drawCircle(borde, 0.5f * u, Offset(c.x, c.y - al / 2 - 2.4f * u))
        drawPath(forma, blanco)
        if (baucent) clipPath(forma) { drawRect(negro, Offset(c.x - an, c.y - al / 2), Size(an * 2, al * 0.38f)) }
        else drawPath(cruzPatada(Offset(c.x, c.y - al * 0.08f), an * 0.36f, 0.14f, 0.42f), rojo)
        drawPath(forma, borde, style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        fun s(c: Color, k: Float) = mez(e.fondo, c, k)
        val rnd = Random(e.semilla.toLong())
        val lado = minOf(w * 0.1f, 20f * u)
        val banco = h - 5f * u

        // La cruz patada grande y tenue en el centro: el capítulo se reúne bajo ella.
        drawPath(cruzPatada(Offset(w / 2, h * 0.5f), h * 0.4f, 0.13f, 0.42f), s(e.acento, if (oscuro) 0.07f else 0.06f))

        // Los muros de sillería a los lados, con los escudos colgados.
        val piedra = m(SILLAR, 0.3f); val junta = m(SILLAR_JUNTA, 0.42f)
        sillares(Rect(0f, 6.8f * u, lado, banco), 8f * u, lado * 0.6f, piedra, junta, rnd, u)
        sillares(Rect(w - lado, 6.8f * u, w, banco), 8f * u, lado * 0.6f, piedra, junta, rnd, u)
        val ae = minOf(lado * 0.6f, 11f * u)
        val blanco = m(BLANCO, 0.75f); val rojo = s(e.acento, 0.7f); val negro = m(NEGRO, 0.6f); val borde = m(HIERRO, 0.6f)
        escudo(Offset(lado * 0.5f, h * 0.32f), ae, false, blanco, rojo, negro, borde, u)
        escudo(Offset(lado * 0.5f, h * 0.66f), ae, true, blanco, rojo, negro, borde, u)
        escudo(Offset(w - lado * 0.5f, h * 0.32f), ae, true, blanco, rojo, negro, borde, u)
        escudo(Offset(w - lado * 0.5f, h * 0.66f), ae, false, blanco, rojo, negro, borde, u)

        // El dintel de piedra arriba, con sus juntas y la línea de almagra.
        drawRect(m(SILLAR_LUZ, 0.5f), Offset.Zero, Size(w, 6f * u))
        var x = 6f * u; var k = 0
        while (x < w && k < 60) { drawLine(junta, Offset(x, 0f), Offset(x, 6f * u), 0.45f * u); x += 14f * u; k++ }
        drawRect(m(ALMAGRA, 0.5f), Offset(0f, 6f * u), Size(w, 0.8f * u))

        // El banco corrido de piedra abajo.
        drawRect(m(SILLAR, 0.4f), Offset(0f, banco), Size(w, h - banco))
        drawLine(m(SILLAR_LUZ, 0.7f), Offset(0f, banco + 0.3f * u), Offset(w, banco + 0.3f * u), 0.5f * u)
        x = 4f * u; k = 0
        while (x < w && k < 60) { drawLine(junta, Offset(x, banco + 0.8f * u), Offset(x, h), 0.45f * u); x += 12f * u; k++ }

        // La mesa del capítulo: el tablero, el mantel con la cruz, la Regla, el cáliz y dos velas.
        val cx = w / 2; val an = minOf(w * 0.2f, 34f * u)
        val mesaY = h - 11.5f * u
        drawRect(m(MADERA, 0.62f), Offset(cx - an, mesaY), Size(an * 2, 1.2f * u))
        val mantel = Rect(cx - an + 0.6f * u, mesaY + 1.2f * u, cx + an - 0.6f * u, banco - 0.4f * u)
        drawRect(m(BLANCO, if (oscuro) 0.42f else 0.6f), mantel.topLeft, mantel.size)
        drawRect(m(SILLAR_JUNTA, 0.45f), mantel.topLeft, mantel.size, style = Stroke(0.35f * u))
        drawPath(cruzPatada(mantel.center, minOf(2.4f * u, mantel.height * 0.45f), 0.14f, 0.42f), s(e.acento, 0.65f))
        // La Regla: un libro cerrado con su canto.
        drawRect(m(ROJO, 0.5f), Offset(cx - 9f * u, mesaY - 1.8f * u), Size(6f * u, 1.8f * u))
        drawLine(m(CREMA, 0.7f), Offset(cx - 8.6f * u, mesaY - 0.9f * u), Offset(cx - 3.4f * u, mesaY - 0.9f * u), 0.4f * u)
        // El cáliz.
        val oro = m(ORO, 0.7f)
        drawArc(oro, 0f, 180f, true, Offset(cx + 1f * u, mesaY - 5.4f * u), Size(3.6f * u, 2.6f * u))
        drawLine(oro, Offset(cx + 2.8f * u, mesaY - 4.2f * u), Offset(cx + 2.8f * u, mesaY - 0.8f * u), 0.6f * u)
        drawOval(oro, Offset(cx + 1.4f * u, mesaY - 1f * u), Size(2.8f * u, 1f * u))
        // Las dos velas en sus candeleros.
        listOf(cx - an * 0.8f, cx + an * 0.8f).forEach { vx ->
            drawRect(m(HIERRO, 0.6f), Offset(vx - 1.2f * u, mesaY - 0.6f * u), Size(2.4f * u, 0.6f * u))
            drawLine(m(HIERRO, 0.6f), Offset(vx, mesaY - 0.6f * u), Offset(vx, mesaY - 2.4f * u), 0.5f * u)
            drawRect(m(CREMA, 0.8f), Offset(vx - 0.5f * u, mesaY - 5.6f * u), Size(1f * u, 3.2f * u))
            drawOval(m(LLAMA, 0.85f), Offset(vx - 0.5f * u, mesaY - 7.4f * u), Size(1f * u, 1.8f * u))
        }
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        fun s(c: Color, k: Float) = mez(e.fondo, c, k)
        val rnd = Random(e.semilla.toLong())

        // La mesa de campaña: tablas de roble con sus juntas.
        drawRect(m(MADERA, 0.3f))
        listOf(0.25f, 0.5f, 0.75f).forEach { f ->
            drawLine(m(MADERA_OSC, 0.4f), Offset(0f, h * f), Offset(w, h * f), 0.6f * u)
            val y = h * f - h * 0.12f + rnd.nextFloat() * u
            drawLine(m(MADERA_OSC, 0.15f), Offset(0f, y), Offset(w, y + (rnd.nextFloat() - 0.5f) * 2f * u), 0.4f * u)
        }

        // El pergamino con la costa de Tierra Santa, apenas: el mar a la izquierda, la arena a la derecha.
        val hoja = Rect(5f * u, 3.2f * u, w - 5f * u, h - 3.2f * u)
        drawRect(m(CREMA, 0.42f), hoja.topLeft, hoja.size)
        val costa = listOf(0.43f, 0.44f, 0.41f, 0.42f, 0.39f, 0.4f, 0.37f, 0.36f).mapIndexed { i, f -> Offset(hoja.left + hoja.width * f, hoja.top + hoja.height * i / 7f) }
        val mar = Path().apply {
            moveTo(hoja.left, hoja.top); costa.forEach { lineTo(it.x, it.y) }; lineTo(hoja.left, hoja.bottom); close()
        }
        clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
            drawRect(m(ARENA, 0.14f), hoja.topLeft, hoja.size)
            drawPath(mar, m(MAR, if (oscuro) 0.1f else 0.08f))
            drawPath(Path().apply { costa.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }, m(MADERA_OSC, 0.18f), style = Stroke(0.6f * u))
        }
        drawRect(s(e.acento, 0.5f), Offset(hoja.left + 1.4f * u, hoja.top + 1.4f * u), Size(hoja.width - 2.8f * u, hoja.height - 2.8f * u), style = Stroke(0.6f * u))

        // Las cantoneras de hierro de la mesa, con sus clavos.
        val hierro = m(HIERRO, 0.62f); val clavo = m(HIERRO_LUZ, 0.75f)
        listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, d) ->
            val l = 6f * u; val g = 1.4f * u
            drawRect(hierro, Offset(if (d.x > 0) o.x else o.x - l, if (d.y > 0) o.y else o.y - g), Size(l, g))
            drawRect(hierro, Offset(if (d.x > 0) o.x else o.x - g, if (d.y > 0) o.y else o.y - l), Size(g, l))
            drawCircle(clavo, 0.45f * u, Offset(o.x + d.x * 4.4f * u, o.y + d.y * 0.7f * u))
            drawCircle(clavo, 0.45f * u, Offset(o.x + d.x * 0.7f * u, o.y + d.y * 4.4f * u))
        }

        // Arriba a la izquierda, un castillo como pieza.
        val cb = Offset(10f * u, 8.6f * u)
        val sillar = m(SILLAR_JUNTA, 0.7f)
        drawRect(sillar, Offset(cb.x - 2.4f * u, cb.y - 1f * u), Size(4.8f * u, 1f * u))
        drawRect(sillar, Offset(cb.x - 1.7f * u, cb.y - 5f * u), Size(3.4f * u, 4f * u))
        for (j in 0 until 3) drawRect(sillar, Offset(cb.x - 1.7f * u + j * 1.25f * u, cb.y - 6.2f * u), Size(0.9f * u, 1.2f * u))
        drawRect(m(NEGRO, 0.55f), Offset(cb.x - 0.5f * u, cb.y - 2.4f * u), Size(1f * u, 1.4f * u))

        // Arriba a la derecha, dos estandartes: el de la cruz y el baucent.
        listOf(Triple(w - 8f * u, 1f, false), Triple(w - 12f * u, -1f, true)).forEach { (px, sg, bau) ->
            val base = Offset(px, 8.8f * u)
            drawOval(m(HIERRO, 0.6f), Offset(base.x - 1.4f * u, base.y - 0.6f * u), Size(2.8f * u, 1.2f * u))
            drawLine(m(MADERA_OSC, 0.7f), base, Offset(base.x, 1.4f * u), 0.5f * u)
            val bandera = Rect(minOf(base.x, base.x + sg * 4f * u), 1.6f * u, maxOf(base.x, base.x + sg * 4f * u), 4.6f * u)
            drawRect(m(BLANCO, 0.8f), bandera.topLeft, bandera.size)
            if (bau) drawRect(m(NEGRO, 0.65f), bandera.topLeft, Size(bandera.width, bandera.height * 0.45f))
            else drawPath(cruzPatada(bandera.center, 1.2f * u, 0.15f, 0.42f), s(e.acento, 0.75f))
            drawRect(m(HIERRO, 0.5f), bandera.topLeft, bandera.size, style = Stroke(0.25f * u))
        }

        // Abajo, la espada tendida y unos besantes de oro.
        val y = h - 5.2f * u
        val x0 = 7f * u; val largo = minOf(w * 0.4f, 70f * u)
        val guarda = x0 + largo * 0.74f
        val filo = Path().apply { moveTo(x0, y); lineTo(x0 + 2f * u, y - 0.8f * u); lineTo(guarda, y - 0.8f * u); lineTo(guarda, y + 0.8f * u); lineTo(x0 + 2f * u, y + 0.8f * u); close() }
        drawPath(filo, m(HIERRO_LUZ, 0.8f))
        drawPath(filo, m(HIERRO, 0.7f), style = Stroke(0.35f * u))
        drawLine(m(HIERRO, 0.5f), Offset(x0 + 3f * u, y), Offset(guarda - 1f * u, y), 0.35f * u)
        drawLine(m(HIERRO, 0.75f), Offset(guarda, y - 2.4f * u), Offset(guarda, y + 2.4f * u), 0.9f * u, StrokeCap.Round)
        drawLine(m(MADERA_OSC, 0.75f), Offset(guarda, y), Offset(x0 + largo * 0.94f, y), 1f * u)
        drawCircle(m(HIERRO, 0.75f), 1.1f * u, Offset(x0 + largo * 0.96f, y))
        listOf(Offset(w - 9f * u, h - 6.2f * u), Offset(w - 12f * u, h - 4.4f * u), Offset(w - 7.4f * u, h - 3.6f * u)).forEach { o ->
            drawCircle(m(ORO, 0.75f), 1.5f * u, o)
            drawCircle(m(MADERA_OSC, 0.45f), 1.5f * u, o, style = Stroke(0.3f * u))
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        fun s(c: Color, k: Float) = mez(e.fondo, c, k)
        val rnd = Random(e.semilla.toLong())
        val (centros, paso) = centrosMedallas()
        val nw = paso * 0.8f
        val anillo = 3f * u
        val k = 0.56f
        val cornisa = 10f * u; val repisa = 82f * u; val zocalo = 89f * u
        // El arranque, lo bastante abajo para que el trasdós no se meta en la cornisa (la flecha del arco es ≈ 0.557 × la luz).
        val ys = maxOf(34f * u, cornisa + 1f * u + (nw + 2 * anillo) * 0.557f)

        // El muro de sillería.
        sillares(Rect(0f, cornisa, w, zocalo), 11f * u, 22f * u, m(SILLAR, 0.28f), m(SILLAR_JUNTA, 0.32f), rnd, u)

        // Cada hornacina: las dovelas con su clave, el fondo hundido y la repisa.
        centros.forEach { cx ->
            val xL = cx - nw / 2; val xR = cx + nw / 2
            drawPath(hueco(xL - anillo, xR + anillo, ys, repisa, k), m(SILLAR_LUZ, 0.5f))
            val forma = hueco(xL, xR, ys, repisa, k)
            drawPath(forma, mez(e.fondo, n(NEGRO, oscuro), if (oscuro) 0.25f else 0.06f))
            clipPath(forma) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), cornisa, ys), Offset(xL, cornisa), Size(nw, ys - cornisa))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.08f), Color.Transparent), xL, xL + 3f * u), Offset(xL, cornisa), Size(3f * u, repisa - cornisa))
            }
            val dentro = arco(xL, xR, ys, k); val fuera = arco(xL - anillo, xR + anillo, ys, k)
            val junta = m(SILLAR_JUNTA, 0.55f)
            for (i in 2 until dentro.size - 1 step 2) drawLine(junta, dentro[i], fuera[i], 0.45f * u)
            val mi = dentro.size / 2
            drawPath(Path().apply {
                moveTo(dentro[mi - 1].x, dentro[mi - 1].y); lineTo(fuera[mi - 1].x, fuera[mi - 1].y - 0.6f * u)
                lineTo(fuera[mi + 1].x, fuera[mi + 1].y - 0.6f * u); lineTo(dentro[mi + 1].x, dentro[mi + 1].y); close()
            }, m(SILLAR, 0.6f))
            drawPath(hueco(xL - anillo, xR + anillo, ys, repisa, k), junta, style = Stroke(0.5f * u))
            drawPath(forma, junta, style = Stroke(0.5f * u))
            // La repisa de piedra que sobresale, con su sombra.
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(xL - 2f * u, repisa + 2.6f * u), Size(nw + 4f * u, 1.2f * u))
            drawRect(m(SILLAR_LUZ, 0.62f), Offset(xL - 2f * u, repisa), Size(nw + 4f * u, 2.6f * u))
            drawLine(junta, Offset(xL - 2f * u, repisa + 2.6f * u), Offset(xR + 2f * u, repisa + 2.6f * u), 0.5f * u)
        }

        // La cornisa, con una cruz patada sobre cada pilar.
        drawRect(m(SILLAR_LUZ, 0.5f), Offset.Zero, Size(w, cornisa))
        drawLine(m(SILLAR_JUNTA, 0.55f), Offset(0f, cornisa), Offset(w, cornisa), 0.6f * u)
        drawLine(m(SILLAR_JUNTA, 0.35f), Offset(0f, 1.4f * u), Offset(w, 1.4f * u), 0.4f * u)
        val pilares = mutableListOf(centros.first() - paso / 2)
        centros.zipWithNext().forEach { (a, b) -> pilares += (a + b) / 2 }
        pilares += centros.last() + paso / 2
        val rc = minOf(3f * u, cornisa * 0.32f)
        pilares.forEach { x -> if (x > rc && x < w - rc) drawPath(cruzPatada(Offset(x, cornisa * 0.56f), rc, 0.14f, 0.42f), s(e.acento, 0.6f)) }

        // El zócalo de almagra con su línea de cal.
        drawRect(m(ALMAGRA, 0.32f), Offset(0f, zocalo), Size(w, h - zocalo))
        drawLine(m(BLANCO, 0.7f), Offset(0f, zocalo), Offset(w, zocalo), 0.8f * u)
    }

    // ---------------------------------------------------------------- Íconos de mapa

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val c = Offset(size.width / 2, size.height / 2)
        val s = size.height * 0.4f
        val g = s * 0.07f
        when (id) {
            "unesco" -> {
                // El castillo de frontera: la torre del homenaje con su estandarte, dos torres, los lienzos y la puerta.
                val suelo = c.y + s * 0.9f
                fun almenas(x0: Float, x1: Float, y: Float, n: Int, ancho: Float) {
                    for (j in 0 until n) {
                        val x = if (n == 1) (x0 + x1) / 2 - ancho / 2 else x0 + (x1 - x0 - ancho) * j / (n - 1)
                        drawRect(e.c1, Offset(x, y - s * 0.17f), Size(ancho, s * 0.17f))
                    }
                }
                listOf(-1f, 1f).forEach { sg ->
                    val t0 = c.x + sg * s * 0.82f; val t1 = c.x + sg * s * 1.32f
                    val l = minOf(t0, t1); val r = maxOf(t0, t1)
                    drawRect(e.c1, Offset(l, c.y - s * 0.3f), Size(r - l, suelo - (c.y - s * 0.3f)))
                    almenas(l, r, c.y - s * 0.3f, 2, s * 0.16f)
                    drawRect(e.fondo, Offset((l + r) / 2 - s * 0.03f, c.y), Size(s * 0.06f, s * 0.24f))
                    val m0 = c.x + sg * s * 0.42f; val m1 = c.x + sg * s * 0.82f
                    val ml = minOf(m0, m1); val mr = maxOf(m0, m1)
                    drawRect(e.c1, Offset(ml, c.y + s * 0.08f), Size(mr - ml, suelo - (c.y + s * 0.08f)))
                    almenas(ml, mr, c.y + s * 0.08f, 1, s * 0.14f)
                }
                drawRect(e.c1, Offset(c.x - s * 0.42f, c.y - s * 0.58f), Size(s * 0.84f, suelo - (c.y - s * 0.58f)))
                almenas(c.x - s * 0.42f, c.x + s * 0.42f, c.y - s * 0.58f, 3, s * 0.2f)
                drawPath(hueco(c.x - s * 0.17f, c.x + s * 0.17f, c.y + s * 0.55f, suelo, 0.5f), e.fondo)
                drawPath(cruzPatada(Offset(c.x, c.y - s * 0.2f), s * 0.2f, 0.14f, 0.42f), e.fondo)
                drawLine(e.c2, Offset(c.x, c.y - s * 0.75f), Offset(c.x, c.y - s * 1.02f), g, StrokeCap.Round)
                drawPath(Path().apply { moveTo(c.x, c.y - s * 1.02f); lineTo(c.x + s * 0.4f, c.y - s * 0.94f); lineTo(c.x, c.y - s * 0.84f); close() }, e.c2)
                drawLine(e.c2, Offset(c.x - s * 1.6f, suelo), Offset(c.x + s * 1.6f, suelo), g * 1.3f, StrokeCap.Round)
            }
            "rascar" -> {
                // Un pergamino con una franja raspada y la espada que raspa con la punta.
                val hoja = Rect(c.x - s * 1.5f, c.y - s * 0.82f, c.x + s * 1.0f, c.y + s * 0.82f)
                drawRect(e.c2.copy(alpha = 0.3f), hoja.topLeft, hoja.size)
                clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                    val raspa = Path().apply {
                        moveTo(c.x - s * 1.2f, c.y - s * 0.35f)
                        var x = c.x - s * 1.2f; var arriba = false; var k = 0
                        while (x < c.x + s * 0.2f && k < 20) { x += s * 0.22f; lineTo(x, if (arriba) c.y - s * 0.42f else c.y + s * 0.4f); arriba = !arriba; k++ }
                    }
                    drawPath(raspa, e.c1, style = Stroke(s * 0.2f, cap = StrokeCap.Round))
                }
                drawRect(e.c2, hoja.topLeft, hoja.size, style = Stroke(g))
                val punta = Offset(c.x + s * 0.25f, c.y + s * 0.42f)
                val guarda = Offset(c.x + s * 1.05f, c.y - s * 0.42f)
                val dx = guarda.x - punta.x; val dy = guarda.y - punta.y
                val l = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
                val d = Offset(dx / l, dy / l); val nn = Offset(-d.y, d.x)
                fun p(t: Float, lado: Float) = Offset(punta.x + d.x * l * t + nn.x * lado, punta.y + d.y * l * t + nn.y * lado)
                // La hoja, con un contorno del fondo para que se despegue del pergamino.
                val hojaEspada = Path().apply {
                    val a = p(0f, 0f); val b = p(0.15f, s * 0.08f); val c2 = p(1f, s * 0.08f); val d2 = p(1f, -s * 0.08f); val e2 = p(0.15f, -s * 0.08f)
                    moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c2.x, c2.y); lineTo(d2.x, d2.y); lineTo(e2.x, e2.y); close()
                }
                drawPath(hojaEspada, e.fondo, style = Stroke(g * 1.6f))
                drawPath(hojaEspada, e.c1)
                drawLine(e.c2, p(1f, s * 0.32f), p(1f, -s * 0.32f), g * 1.6f, StrokeCap.Round)
                drawLine(e.c1, p(1f, 0f), p(1.3f, 0f), g * 1.6f, StrokeCap.Round)
                drawCircle(e.c2, s * 0.1f, p(1.36f, 0f))
            }
            else -> {
                // Tus lugares: tres cruces patadas unidas por un camino de puntos.
                val pts = listOf(Offset(c.x - s * 1.1f, c.y + s * 0.32f), Offset(c.x, c.y - s * 0.38f), Offset(c.x + s * 1.1f, c.y + s * 0.22f))
                val camino = Path().apply {
                    moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.6f, c.y - s * 0.45f, pts[1].x, pts[1].y)
                    quadraticTo(c.x + s * 0.6f, c.y - s * 0.45f, pts[2].x, pts[2].y)
                }
                drawPath(camino, e.c2, style = Stroke(g, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, g * 2.6f))))
                pts.forEachIndexed { i, o ->
                    val r = s * 0.42f * (1f - 0.08f * i)
                    drawCircle(e.fondo, r * 1.05f, o)
                    drawPath(cruzPatada(o, r, 0.15f, 0.44f), e.c1)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val rnd = Random(e.semilla.toLong())

        // La puerta: tablas verticales de roble con sus juntas y sus vetas.
        val tablas = 6
        for (k in 0 until tablas) {
            val x0 = w * k / tablas
            drawRect(m(mez(MADERA, MADERA_OSC, rnd.nextFloat() * 0.35f), 0.42f), Offset(x0, 0f), Size(w / tablas + 0.5f, h))
            if (k > 0) drawLine(m(MADERA_OSC, 0.6f), Offset(x0, 0f), Offset(x0, h), 0.8f * u)
            repeat(2) {
                val x = x0 + w / tablas * (0.2f + 0.6f * rnd.nextFloat())
                val amp = (0.5f + rnd.nextFloat()) * u
                drawPath(Path().apply { moveTo(x, 0f); cubicTo(x + amp, h * 0.3f, x - amp, h * 0.7f, x + amp * 0.4f, h) }, m(MADERA_OSC, 0.22f), style = Stroke(0.4f * u))
            }
        }
        // Las bisagras de hierro con su punta de lanza y sus clavos.
        val hierro = m(HIERRO, 0.7f); val clavo = m(HIERRO_LUZ, 0.75f)
        listOf(2f * u, h - 6f * u).forEach { y ->
            val alto = 3.4f * u; val fin = w * 0.78f
            drawRect(hierro, Offset(0f, y), Size(fin, alto))
            drawPath(Path().apply { moveTo(fin, y - 1f * u); lineTo(fin + 5f * u, y + alto / 2); lineTo(fin, y + alto + 1f * u); close() }, hierro)
            drawRect(hierro, Offset(0f, y - 0.8f * u), Size(2.6f * u, alto + 1.6f * u))
            val nc = (fin / (16f * u)).toInt().coerceIn(2, 30)
            for (k in 0 until nc) drawCircle(clavo, 0.6f * u, Offset(fin * (k + 0.5f) / nc, y + alto / 2))
        }
        // La aldaba, en el margen de la derecha.
        val ca = Offset(w - 5.2f * u, h * 0.5f)
        drawRect(hierro, Offset(ca.x - 2.4f * u, ca.y - 4.6f * u), Size(4.8f * u, 4.8f * u))
        drawCircle(clavo, 0.5f * u, Offset(ca.x, ca.y - 2.2f * u))
        drawCircle(hierro, 3.2f * u, Offset(ca.x, ca.y + 0.6f * u), style = Stroke(0.9f * u))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        translate(r.left, r.top) {
            val w = r.width; val h = r.height; val u = minOf(w, h) / 100f
            val papel = if (oscuro) HOJA_OSCURA else HOJA_CLARA
            val rnd = Random(e.indice.toLong() * 977L + 5L)
            fun j() = (rnd.nextFloat() - 0.5f) * minOf(w, h) * 0.035f
            val x0 = w * 0.025f; val x1 = w * 0.975f; val y0 = h * 0.03f; val y1 = h * 0.965f
            val forma = Path().apply { moveTo(x0 + j(), y0 + j()); lineTo(x1 + j(), y0 + j()); lineTo(x1 + j(), y1 + j()); lineTo(x0 + j(), y1 + j()); close() }
            translate(1.2f * u, 2.2f * u) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f)) }
            drawPath(forma, papel)
            clipPath(forma) {
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, MADERA.copy(alpha = if (oscuro) 0.1f else 0.07f)), h * 0.7f, h), Offset(0f, h * 0.7f), Size(w, h * 0.3f))
            }
            val borde = when {
                e.fijada -> e.acento
                oscuro -> mez(papel, CREMA, 0.3f)
                else -> mez(papel, SILLAR_JUNTA, 0.6f)
            }
            drawPath(forma, borde, style = Stroke(if (e.fijada) 2f * u else 1f * u))

            // El sello de lacre abajo a la derecha, con su cinta, del color del tipo.
            val cera = when (e.tipo) {
                "idea" -> CERA_AMARILLA
                "pensamiento" -> CERA_VERDE
                "proposito" -> LACRE
                "recordatorio" -> CERA_PARDA
                else -> LACRE
            }
            val cs = Offset(w * 0.9f, h * 0.88f); val rs = h * 0.075f
            val cinta = if (oscuro) mez(ROJO, Color.White, 0.2f) else ROJO
            drawLine(cinta, cs, Offset(cs.x - w * 0.03f, h * 0.995f), 1.4f * u)
            drawLine(cinta, cs, Offset(cs.x + w * 0.035f, h * 0.99f), 1.4f * u)
            val gota = sello(cs, rs)
            drawPath(gota, cera)
            drawPath(gota, mez(cera, Color.Black, 0.3f), style = Stroke(0.6f * u))
            drawPath(cruzPatada(cs, rs * 0.55f, 0.15f, 0.42f), mez(cera, Color.Black, 0.35f))
            drawCircle(Color.White.copy(alpha = 0.25f), rs * 0.25f, Offset(cs.x - rs * 0.4f, cs.y - rs * 0.4f))

            if (e.fijada) {
                // Clavada con una daga: el pomo, el puño de cuero, la cruz y la hoja que entra en la madera.
                val x = w * 0.5f
                val acero = if (oscuro) HIERRO_LUZ else HIERRO
                translate(0.8f * u, 1.1f * u) {
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(x - w * 0.11f, h * 0.112f), Size(w * 0.22f, h * 0.03f))
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(x - w * 0.017f, h * 0.05f), Size(w * 0.034f, h * 0.1f))
                }
                drawPath(Path().apply { moveTo(x - w * 0.022f, h * 0.142f); lineTo(x + w * 0.022f, h * 0.142f); lineTo(x + w * 0.01f, h * 0.158f); lineTo(x - w * 0.01f, h * 0.158f); close() }, acero)
                drawRect(Color.Black.copy(alpha = 0.5f), Offset(x - w * 0.016f, h * 0.155f), Size(w * 0.032f, h * 0.007f))
                drawRect(MADERA_OSC, Offset(x - w * 0.017f, h * 0.05f), Size(w * 0.034f, h * 0.065f))
                for (k in 1..3) drawLine(Color.Black.copy(alpha = 0.35f), Offset(x - w * 0.017f, h * (0.05f + 0.016f * k)), Offset(x + w * 0.017f, h * (0.056f + 0.016f * k)), 0.5f * u)
                drawRect(acero, Offset(x - w * 0.11f, h * 0.112f), Size(w * 0.22f, h * 0.03f))
                drawCircle(acero, h * 0.04f, Offset(x - w * 0.11f, h * 0.127f))
                drawCircle(acero, h * 0.04f, Offset(x + w * 0.11f, h * 0.127f))
                drawCircle(acero, h * 0.036f, Offset(x, h * 0.04f))
                drawCircle(Color.White.copy(alpha = 0.35f), h * 0.012f, Offset(x - h * 0.01f, h * 0.03f))
            } else {
                // Clavada con un clavo de hierro.
                val cl = Offset(w * 0.5f, h * 0.075f)
                drawCircle(Color.Black.copy(alpha = 0.3f), h * 0.042f, Offset(cl.x + 0.6f * u, cl.y + 0.9f * u))
                drawCircle(if (oscuro) HIERRO_LUZ else HIERRO, h * 0.04f, cl)
                drawCircle(Color.White.copy(alpha = 0.35f), h * 0.014f, Offset(cl.x - h * 0.012f, cl.y - h * 0.012f))
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFEEE7D8) else Color(0xFF201A14)
}
