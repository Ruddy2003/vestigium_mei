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
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Scriptorium: el monasterio del copista, con los materiales de su biblioteca, su Ideario y su sala de gestas: roble con
 * herrajes de hierro, pergamino, tinta ferrogálica, rúbrica bermellón, lapislázuli y pan de oro, el cuadrifolio y el
 * friso de escaques.
 * - Social: el refectorio. Arriba, el envigado de roble con sus canes y el friso de escaques; a los lados, las ventanas
 *   de medio punto con su vidriera emplomada; abajo, la mesa larga con su mantel de lino (orlado de bermellón y azul),
 *   los panes, los cuencos, la jarra y la vela, y el banco delante.
 * - Mapas: el atril con un mappa mundi: el pergamino grande con sus filetes rojo y azul, el orbe en T dentro de una O
 *   (apenas, bajo el mapa), cuadrifolios de oro en las esquinas, el tintero de cuerno y la pluma arriba, y la repisa de
 *   roble con su cadena abajo.
 * - Otras vidas: un armarium de roble: la crestería de cuadrifolios, un hueco de arco apuntado con su moldura para cada
 *   medalla, las hojas de las puertas abiertas entre ellos, con su tracería ciega y sus bisagras de hierro, y el zócalo.
 * - Íconos: la fachada de una catedral, el lunellum (la cuchilla de media luna con que se raspaba el pergamino) y tres
 *   capitulares con el cuadrifolio.
 * - Notas: un tablón de roble con dos fajas de hierro claveteadas; cada nota es una hoja de pergamino clavada, con los
 *   picados del pautado y la marginalia roja.
 */
internal object VidaMedieval : DisenoVida {
    private val ROBLE = Color(0xFF6E4A2A)
    private val ROBLE_LUZ = Color(0xFF8E6942)
    private val ROBLE_OSC = Color(0xFF3F2A18)
    private val VETA = Color(0xFF2B1B0E)
    private val HIERRO = Color(0xFF2B2A2D)
    private val HIERRO_LUZ = Color(0xFF6E6D73)
    private val PERGAMINO = Color(0xFFEADFBF)
    private val TINTA = Color(0xFF2A1B10)
    private val BERMELLON = Color(0xFFA5321F)
    private val LAPIZ = Color(0xFF2E4C88)
    private val ORO = Color(0xFFDDBE76)
    private val ORO_OSC = Color(0xFF9A7A2E)
    private val PIEDRA = Color(0xFFC8BCA2)
    private val PIEDRA_OSC = Color(0xFF8F8670)
    private val LINO = Color(0xFFEDE6D3)
    private val CERA = Color(0xFFF2E3B6)
    private val LLAMA = Color(0xFFF2B84A)
    private val CIELO = Color(0xFFBFD3E6)
    private val NOCHE = Color(0xFF100C08)

    /** La hoja de las notas: pergamino claro de día; de noche, a la luz de la vela (con la tinta clara de [tintaNota]). */
    private val HOJA_CLARA = Color(0xFFEFE4C6)
    private val HOJA_OSCURA = Color(0xFF3A2E1F)

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
     * clave (con [k] = 0.5, medio punto; más, apuntado).
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

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val rnd = Random(e.semilla.toLong())
        val lado = minOf(w * 0.1f, 20f * u)

        // El muro de piedra, tenue.
        drawRect(m(PIEDRA, 0.12f))

        // Las ventanas de medio punto a los lados, con su derrame y la vidriera emplomada.
        listOf(lado * 0.5f, w - lado * 0.5f).forEach { cx ->
            val an = minOf(9f * u, lado * 0.6f)
            val derrame = hueco(cx - an / 2 - 1.8f * u, cx + an / 2 + 1.8f * u, 26f * u, 64f * u, 0.5f)
            drawPath(derrame, m(PIEDRA_OSC, 0.32f))
            val vidrio = hueco(cx - an / 2, cx + an / 2, 26f * u, 62f * u, 0.5f)
            drawPath(vidrio, if (oscuro) m(LAPIZ, 0.35f) else m(CIELO, 0.6f))
            clipPath(vidrio) {
                val plomo = m(HIERRO, 0.45f)
                var d = -40f * u; var k = 0
                while (d < 60f * u && k < 30) {
                    drawLine(plomo, Offset(cx - an + d, 18f * u), Offset(cx - an + d + 44f * u, 62f * u), 0.35f * u)
                    drawLine(plomo, Offset(cx + an - d, 18f * u), Offset(cx + an - d - 44f * u, 62f * u), 0.35f * u)
                    d += 4f * u; k++
                }
            }
            drawPath(vidrio, m(PIEDRA_OSC, 0.55f), style = Stroke(0.6f * u))
            drawRect(m(PIEDRA, 0.55f), Offset(cx - an / 2 - 2.6f * u, 64f * u), Size(an + 5.2f * u, 1.4f * u))
        }

        // El friso de escaques bajo el envigado.
        val esc = 1.4f * u
        val pa = Path(); val pb = Path()
        val cols = (w / esc).toInt().coerceIn(0, 300)
        for (fila in 0 until 2) for (c in 0 until cols) {
            val r = Rect(c * esc, 4.6f * u + fila * esc, (c + 1) * esc, 4.6f * u + (fila + 1) * esc)
            if ((c + fila) % 2 == 0) pa.addRect(r) else pb.addRect(r)
        }
        drawPath(pa, m(BERMELLON, 0.42f))
        drawPath(pb, m(ORO, 0.5f))
        drawLine(m(TINTA, 0.35f), Offset(0f, 4.6f * u + 2 * esc), Offset(w, 4.6f * u + 2 * esc), 0.4f * u)

        // La viga de roble con sus canes.
        drawRect(m(ROBLE, 0.58f), Offset.Zero, Size(w, 4.6f * u))
        drawLine(m(VETA, 0.35f), Offset(0f, 1.8f * u), Offset(w, 1.6f * u + rnd.nextFloat() * u), 0.35f * u)
        drawLine(m(ROBLE_LUZ, 0.5f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 0.4f * u)
        val nCanes = (w / (26f * u)).toInt().coerceIn(2, 20)
        for (k in 0..nCanes) {
            val x = w * k / nCanes
            drawRoundRect(m(ROBLE_OSC, 0.58f), Offset(x - 1.6f * u, 4.6f * u), Size(3.2f * u, 3.8f * u), CornerRadius(1.2f * u))
        }

        // La mesa: las patas de tijera detrás, el tablero, el mantel orlado y el banco delante.
        val mesaY = h - 11f * u
        val x0 = w * 0.06f; val x1 = w * 0.94f
        listOf(x0 + 6f * u, x1 - 6f * u).forEach { x ->
            drawLine(m(ROBLE_OSC, 0.6f), Offset(x - 2.4f * u, h), Offset(x + 1.6f * u, mesaY + 1f * u), 0.9f * u)
            drawLine(m(ROBLE_OSC, 0.6f), Offset(x + 2.4f * u, h), Offset(x - 1.6f * u, mesaY + 1f * u), 0.9f * u)
        }
        drawRect(m(ROBLE, 0.62f), Offset(x0, mesaY), Size(x1 - x0, 1.2f * u))
        val mantel = Rect(x0 + 0.6f * u, mesaY + 1.2f * u, x1 - 0.6f * u, mesaY + 6.4f * u)
        drawRect(m(LINO, if (oscuro) 0.4f else 0.55f), mantel.topLeft, mantel.size)
        var x = mantel.left + 7f * u; var k = 0
        while (x < mantel.right && k < 40) { drawLine(m(PIEDRA_OSC, 0.25f), Offset(x, mantel.top), Offset(x, mantel.bottom - 1.6f * u), 0.35f * u); x += 13f * u; k++ }
        drawLine(m(BERMELLON, 0.55f), Offset(mantel.left, mantel.bottom - 1.4f * u), Offset(mantel.right, mantel.bottom - 1.4f * u), 0.6f * u)
        drawLine(m(LAPIZ, 0.55f), Offset(mantel.left, mantel.bottom - 0.6f * u), Offset(mantel.right, mantel.bottom - 0.6f * u), 0.5f * u)
        drawRect(m(ROBLE_LUZ, 0.6f), Offset(w * 0.03f, h - 4.4f * u), Size(w * 0.94f, 1.4f * u))
        listOf(w * 0.05f, w * 0.95f).forEach { bx -> drawLine(m(ROBLE_OSC, 0.6f), Offset(bx, h - 3f * u), Offset(bx, h), 0.9f * u) }

        // Lo que hay sobre la mesa: panes, cuencos, la jarra y la vela.
        val madera = m(ROBLE_LUZ, 0.6f); val pan = m(Color(0xFFB98A4E), 0.6f)
        listOf(0.18f, 0.6f, 0.82f).forEach { f -> drawArc(madera, 0f, 180f, true, Offset(w * f - 2.4f * u, mesaY - 1.8f * u), Size(4.8f * u, 3.4f * u)) }
        listOf(0.28f, 0.7f).forEach { f -> drawArc(pan, 180f, 180f, true, Offset(w * f - 2.2f * u, mesaY - 2.4f * u), Size(4.4f * u, 4.8f * u)) }
        val jx = w * 0.4f
        drawPath(Path().apply {
            moveTo(jx - 0.9f * u, mesaY - 5.4f * u); lineTo(jx + 0.9f * u, mesaY - 5.4f * u)
            quadraticTo(jx + 2.6f * u, mesaY - 2.6f * u, jx + 1.3f * u, mesaY); lineTo(jx - 1.3f * u, mesaY)
            quadraticTo(jx - 2.6f * u, mesaY - 2.6f * u, jx - 0.9f * u, mesaY - 5.4f * u); close()
        }, m(PIEDRA_OSC, 0.6f))
        drawArc(m(PIEDRA_OSC, 0.6f), -90f, 180f, false, Offset(jx + 0.8f * u, mesaY - 4.6f * u), Size(2.4f * u, 3f * u), style = Stroke(0.5f * u))
        val vx = w * 0.5f
        drawRect(m(HIERRO_LUZ, 0.6f), Offset(vx - 1.4f * u, mesaY - 0.6f * u), Size(2.8f * u, 0.6f * u))
        drawRect(m(CERA, 0.75f), Offset(vx - 0.5f * u, mesaY - 4.6f * u), Size(1f * u, 4f * u))
        drawOval(m(LLAMA, 0.85f), Offset(vx - 0.5f * u, mesaY - 6.4f * u), Size(1f * u, 1.8f * u))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val rnd = Random(e.semilla.toLong())

        // El tablero inclinado del atril, de roble, con su marco.
        drawRect(m(ROBLE, 0.28f))
        for (k in 0 until 5) {
            val y = h * (k + 0.5f) / 5f + (rnd.nextFloat() - 0.5f) * 3f * u
            drawLine(m(VETA, 0.18f), Offset(0f, y), Offset(w, y + (rnd.nextFloat() - 0.5f) * 2f * u), 0.4f * u)
        }
        drawRect(m(ROBLE_OSC, 0.55f), Offset(1.2f * u, 1.2f * u), Size(w - 2.4f * u, h - 2.4f * u), style = Stroke(2.2f * u))

        // El pergamino del mappa mundi, con sus filetes.
        val hoja = Rect(4.5f * u, 2.6f * u, w - 4.5f * u, h - 6.2f * u)
        val pergamino = m(PERGAMINO, 0.45f)
        drawRect(pergamino, hoja.topLeft, hoja.size)
        drawRect(m(BERMELLON, 0.5f), Offset(hoja.left + 1.6f * u, hoja.top + 1.6f * u), Size(hoja.width - 3.2f * u, hoja.height - 3.2f * u), style = Stroke(0.6f * u))
        drawRect(m(LAPIZ, 0.45f), Offset(hoja.left + 2.6f * u, hoja.top + 2.6f * u), Size(hoja.width - 5.2f * u, hoja.height - 5.2f * u), style = Stroke(0.4f * u))
        // El orbe en T dentro de una O, apenas, bajo el mapa: el océano alrededor, el Mediterráneo y el Don con el Nilo.
        val c = hoja.center; val ro = hoja.height * 0.42f
        val tenue = mez(pergamino, e.secundario, if (oscuro) 0.2f else 0.22f)
        drawCircle(tenue, ro, c, style = Stroke(1f * u))
        drawLine(tenue, Offset(c.x - ro, c.y), Offset(c.x + ro, c.y), 0.8f * u)
        drawLine(tenue, c, Offset(c.x, c.y + ro), 0.8f * u)
        drawCircle(mez(pergamino, e.acento, 0.3f), 1.2f * u, Offset(c.x, c.y - ro * 0.7f))
        // Los cuadrifolios de oro en las esquinas.
        listOf(Offset(hoja.left + 2.1f * u, hoja.top + 2.1f * u), Offset(hoja.right - 2.1f * u, hoja.top + 2.1f * u),
            Offset(hoja.left + 2.1f * u, hoja.bottom - 2.1f * u), Offset(hoja.right - 2.1f * u, hoja.bottom - 2.1f * u)).forEach { o ->
            drawPath(simboloCalificacion(TemaId.MEDIEVAL, o, 2f * u), m(ORO, 0.85f))
            drawPath(simboloCalificacion(TemaId.MEDIEVAL, o, 2f * u), m(ORO_OSC, 0.7f), style = Stroke(0.3f * u))
        }

        // Arriba a la izquierda, el tintero de cuerno con la pluma.
        val tintero = Offset(5f * u, 4.6f * u)
        drawCircle(m(ROBLE_OSC, 0.75f), 2.4f * u, tintero)
        drawCircle(m(TINTA, 0.85f), 1.5f * u, tintero)
        val fin = Offset(minOf(34f * u, w * 0.24f), 1.2f * u)
        val dx = fin.x - tintero.x; val dy = fin.y - tintero.y
        val largo = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val d = Offset(dx / largo, dy / largo); val nrm = Offset(-d.y, d.x)
        fun p(f: Float) = Offset(tintero.x + dx * f, tintero.y + dy * f)
        val pluma = Path().apply {
            val a = p(0.3f); val b = p(0.66f); val q = p(0.72f)
            moveTo(a.x, a.y)
            quadraticTo(b.x + nrm.x * 2.4f * u, b.y + nrm.y * 2.4f * u, fin.x, fin.y)
            quadraticTo(q.x - nrm.x * 1.2f * u, q.y - nrm.y * 1.2f * u, a.x, a.y)
            close()
        }
        drawPath(pluma, m(CERA, 0.8f))
        drawPath(pluma, m(PIEDRA_OSC, 0.6f), style = Stroke(0.3f * u))
        drawLine(m(TINTA, 0.65f), p(0.06f), fin, 0.4f * u, StrokeCap.Round)

        // La repisa de roble donde apoya la hoja, con sus dos clavijas y la cadena de los libros encadenados.
        val repisa = Rect(1.8f * u, h - 6.2f * u, w - 1.8f * u, h - 2.6f * u)
        drawRect(m(ROBLE_LUZ, 0.62f), repisa.topLeft, repisa.size)
        drawLine(m(ORO, 0.4f), Offset(repisa.left, repisa.top + 0.4f * u), Offset(repisa.right, repisa.top + 0.4f * u), 0.4f * u)
        drawLine(m(VETA, 0.5f), Offset(repisa.left, repisa.bottom), Offset(repisa.right, repisa.bottom), 0.6f * u)
        listOf(0.2f, 0.8f).forEach { f -> drawCircle(m(ROBLE_OSC, 0.7f), 1.1f * u, Offset(w * f, repisa.center.y)) }
        val hierro = m(HIERRO, 0.7f)
        for (k in 0 until 4) {
            val o = Offset(w - 14f * u + k * 2.6f * u, repisa.bottom + 1f * u + (if (k % 2 == 0) 0f else 0.4f * u))
            if (k % 2 == 0) drawOval(hierro, Offset(o.x - 1.4f * u, o.y - 0.8f * u), Size(2.8f * u, 1.6f * u), style = Stroke(0.45f * u))
            else drawLine(hierro, Offset(o.x - 1f * u, o.y), Offset(o.x + 1f * u, o.y), 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val (centros, paso) = centrosMedallas()
        val nw = paso * 0.8f
        val corona = 12f * u; val zocalo = 88f * u
        // El arranque del arco, lo bastante abajo para que la clave no se meta en la crestería (la flecha del arco es ≈ 0.61 × la luz).
        val ys = maxOf(36f * u, corona + 2f * u + nw * 0.61f)

        // El cuerpo del armario.
        drawRect(m(ROBLE, 0.34f), Offset(0f, corona), Size(w, zocalo - corona))

        // Las hojas de las puertas, abiertas entre los huecos: un tablero con su tracería ciega y las bisagras.
        val bordes = mutableListOf(0f)
        centros.forEach { bordes += it - nw / 2; bordes += it + nw / 2 }
        bordes += w
        for (i in 0 until bordes.size / 2) {
            val a = bordes[2 * i]; val b = bordes[2 * i + 1]
            if (b - a < 2f * u) continue
            val hojas = if (i == 0 || i == bordes.size / 2 - 1) listOf(a to b) else listOf(a to (a + b) / 2, (a + b) / 2 to b)
            hojas.forEach { (l, r) ->
                val pr = Rect(l + 0.5f * u, corona + 3f * u, r - 0.5f * u, zocalo - 2f * u)
                if (pr.width > 1.2f * u) {
                    drawRect(m(ROBLE_LUZ, 0.45f), pr.topLeft, pr.size)
                    drawRect(m(ROBLE_OSC, 0.5f), pr.topLeft, pr.size, style = Stroke(0.4f * u))
                    if (pr.width > 2.4f * u) drawPath(hueco(pr.left + pr.width * 0.25f, pr.right - pr.width * 0.25f, pr.top + 10f * u, pr.bottom - 4f * u, 0.7f), m(ROBLE_OSC, 0.45f), style = Stroke(0.35f * u))
                }
            }
            // Las bisagras de hierro que cruzan las hojas.
            listOf(30f * u, 70f * u).forEach { y ->
                drawRect(m(HIERRO, 0.6f), Offset(a, y - 0.7f * u), Size(b - a, 1.4f * u))
                if (b - a > 3f * u) {
                    drawCircle(m(HIERRO_LUZ, 0.7f), 0.4f * u, Offset(a + 1f * u, y))
                    drawCircle(m(HIERRO_LUZ, 0.7f), 0.4f * u, Offset(b - 1f * u, y))
                }
            }
        }

        // Los huecos de arco apuntado, cada uno con su moldura y el fondo tenue.
        centros.forEach { cx ->
            val xL = cx - nw / 2; val xR = cx + nw / 2
            val forma = hueco(xL, xR, ys, zocalo, 0.62f)
            drawPath(forma, m(ROBLE_OSC, 0.12f))
            clipPath(forma) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), corona, ys), Offset(xL, corona), Size(nw, ys - corona))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.08f), Color.Transparent), xL, xL + 3f * u), Offset(xL, corona), Size(3f * u, zocalo - corona))
            }
            drawPath(forma, m(ROBLE_OSC, 0.62f), style = Stroke(1.1f * u))
            drawPath(hueco(xL + 1.4f * u, xR - 1.4f * u, ys, zocalo, 0.62f), m(ORO_OSC, 0.35f), style = Stroke(0.35f * u))
        }

        // La crestería: la moldura y una fila de cuadrifolios calados.
        drawRect(m(ROBLE, 0.55f), Offset.Zero, Size(w, corona))
        drawRect(m(ROBLE_LUZ, 0.55f), Offset.Zero, Size(w, 1.6f * u))
        drawLine(m(VETA, 0.55f), Offset(0f, corona), Offset(w, corona), 0.6f * u)
        val q = 3.4f * u
        val nq = (w / (9f * u)).toInt().coerceIn(3, 60)
        for (k in 0 until nq) {
            val o = Offset(w * (k + 0.5f) / nq, corona * 0.56f)
            drawPath(simboloCalificacion(TemaId.MEDIEVAL, o, q), m(ROBLE_OSC, 0.6f))
            drawCircle(m(ORO, 0.55f), 0.5f * u, o)
        }

        // El zócalo.
        drawRect(m(ROBLE_OSC, 0.5f), Offset(0f, zocalo), Size(w, h - zocalo))
        drawLine(m(ROBLE_LUZ, 0.55f), Offset(0f, zocalo + 0.4f * u), Offset(w, zocalo + 0.4f * u), 0.5f * u)
        drawLine(m(VETA, 0.5f), Offset(0f, h - 4f * u), Offset(w, h - 4f * u), 0.5f * u)
    }

    // ---------------------------------------------------------------- Íconos de mapa

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val c = Offset(size.width / 2, size.height / 2)
        val s = size.height * 0.4f
        val g = s * 0.07f
        when (id) {
            "unesco" -> {
                // La fachada de una catedral: dos torres con su aguja, el hastial, el rosetón y la portada.
                val suelo = c.y + s * 0.9f
                listOf(-1f, 1f).forEach { sg ->
                    val xa = c.x + sg * s * 0.55f; val xb = c.x + sg * s * 1.1f
                    val l = minOf(xa, xb); val r = maxOf(xa, xb)
                    drawRect(e.c1, Offset(l, c.y - s * 0.4f), Size(r - l, suelo - (c.y - s * 0.4f)))
                    drawPath(Path().apply { moveTo(l - s * 0.04f, c.y - s * 0.4f); lineTo((l + r) / 2, c.y - s * 1.0f); lineTo(r + s * 0.04f, c.y - s * 0.4f); close() }, e.c1)
                    drawPath(hueco(l + (r - l) * 0.32f, r - (r - l) * 0.32f, c.y - s * 0.12f, c.y + s * 0.18f, 0.62f), e.fondo)
                    drawPath(hueco(l + (r - l) * 0.32f, r - (r - l) * 0.32f, c.y + s * 0.42f, c.y + s * 0.66f, 0.62f), e.fondo)
                }
                drawRect(e.c1, Offset(c.x - s * 0.55f, c.y - s * 0.12f), Size(s * 1.1f, suelo - (c.y - s * 0.12f)))
                drawPath(Path().apply { moveTo(c.x - s * 0.58f, c.y - s * 0.1f); lineTo(c.x, c.y - s * 0.66f); lineTo(c.x + s * 0.58f, c.y - s * 0.1f); close() }, e.c1)
                val ro = Offset(c.x, c.y + s * 0.12f)
                drawCircle(e.fondo, s * 0.24f, ro)
                drawCircle(e.c2, s * 0.15f, ro)
                for (k in 0 until 6) {
                    val a = Math.PI * k / 3
                    drawLine(e.fondo, ro, Offset(ro.x + s * 0.17f * cos(a).toFloat(), ro.y + s * 0.17f * sin(a).toFloat()), g * 0.6f)
                }
                drawPath(hueco(c.x - s * 0.2f, c.x + s * 0.2f, c.y + s * 0.66f, suelo, 0.62f), e.fondo)
                drawLine(e.c2, Offset(c.x - s * 1.5f, suelo), Offset(c.x + s * 1.5f, suelo), g * 1.3f, StrokeCap.Round)
            }
            "rascar" -> {
                // Un pergamino con una franja raspada y el lunellum, la cuchilla de media luna del pergaminero.
                val hoja = Rect(c.x - s * 1.5f, c.y - s * 0.82f, c.x + s * 1.15f, c.y + s * 0.82f)
                drawRect(e.c2.copy(alpha = 0.3f), hoja.topLeft, hoja.size)
                clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                    val raspa = Path().apply {
                        moveTo(c.x - s * 1.2f, c.y - s * 0.35f)
                        var x = c.x - s * 1.2f; var arriba = false; var k = 0
                        while (x < c.x + s * 0.35f && k < 20) { x += s * 0.22f; lineTo(x, if (arriba) c.y - s * 0.42f else c.y + s * 0.4f); arriba = !arriba; k++ }
                    }
                    drawPath(raspa, e.c1, style = Stroke(s * 0.2f, cap = StrokeCap.Round))
                }
                drawRect(e.c2, hoja.topLeft, hoja.size, style = Stroke(g))
                // La esquina de arriba a la izquierda, doblada.
                drawPath(Path().apply { moveTo(hoja.left, hoja.top + s * 0.35f); lineTo(hoja.left + s * 0.35f, hoja.top); lineTo(hoja.left + s * 0.35f, hoja.top + s * 0.35f); close() }, e.c2)
                val k0 = Offset(c.x + s * 0.95f, c.y + s * 0.3f)
                drawCircle(e.fondo, s * 0.6f, Offset(k0.x, k0.y - s * 0.1f))
                rotate(-20f, k0) {
                    val hojaCuchilla = Path().apply {
                        moveTo(k0.x - s * 0.5f, k0.y)
                        arcTo(Rect(k0.x - s * 0.5f, k0.y - s * 0.5f, k0.x + s * 0.5f, k0.y + s * 0.5f), 180f, -180f, false)
                        close()
                    }
                    drawPath(hojaCuchilla, e.c1)
                    drawArc(e.fondo, 25f, 130f, false, Offset(k0.x - s * 0.36f, k0.y - s * 0.36f), Size(s * 0.72f, s * 0.72f), style = Stroke(g * 0.6f))
                    drawLine(e.c2, Offset(k0.x, k0.y - s * 0.05f), Offset(k0.x, k0.y - s * 0.62f), s * 0.17f, StrokeCap.Round)
                    drawLine(e.c1, Offset(k0.x - s * 0.12f, k0.y - s * 0.08f), Offset(k0.x + s * 0.12f, k0.y - s * 0.08f), g, StrokeCap.Round)
                }
            }
            else -> {
                // Tus lugares: tres capitulares con el cuadrifolio y su rasgueo de pluma, unidas por un camino de puntos.
                val pts = listOf(Offset(c.x - s * 1.1f, c.y + s * 0.3f), Offset(c.x, c.y - s * 0.32f), Offset(c.x + s * 1.1f, c.y + s * 0.22f))
                val camino = Path().apply {
                    moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.6f, c.y - s * 0.45f, pts[1].x, pts[1].y)
                    quadraticTo(c.x + s * 0.6f, c.y - s * 0.45f, pts[2].x, pts[2].y)
                }
                drawPath(camino, e.c2, style = Stroke(g, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, g * 2.6f))))
                pts.forEachIndexed { i, o ->
                    val q = s * 0.66f * (1f - 0.08f * i)
                    drawPath(Path().apply { moveTo(o.x - q / 2, o.y + q / 2); quadraticTo(o.x - q * 0.7f, o.y + q * 0.9f, o.x - q * 0.25f, o.y + q * 0.95f) }, e.c2, style = Stroke(g * 0.8f, cap = StrokeCap.Round))
                    drawRect(e.c1, Offset(o.x - q / 2, o.y - q / 2), Size(q, q))
                    drawRect(e.fondo, Offset(o.x - q / 2 + g, o.y - q / 2 + g), Size(q - 2 * g, q - 2 * g), style = Stroke(g * 0.6f))
                    drawPath(simboloCalificacion(TemaId.MEDIEVAL, o, q * 0.27f), e.c2)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = v(e.fondo, c, k, oscuro)
        val rnd = Random(e.semilla.toLong())

        // El tablón: tablas de roble verticales, de tonos un poco distintos, con sus juntas y sus vetas.
        val tablas = 5
        for (k in 0 until tablas) {
            val x0 = w * k / tablas
            drawRect(m(mez(ROBLE, ROBLE_LUZ, rnd.nextFloat() * 0.6f), 0.42f), Offset(x0, 0f), Size(w / tablas + 0.5f, h))
            if (k > 0) drawLine(m(VETA, 0.55f), Offset(x0, 0f), Offset(x0, h), 0.7f * u)
            repeat(3) {
                val x = x0 + w / tablas * (0.15f + 0.7f * rnd.nextFloat())
                val amp = (0.5f + rnd.nextFloat()) * u
                drawPath(Path().apply { moveTo(x, 0f); cubicTo(x + amp, h * 0.35f, x - amp, h * 0.65f, x + amp * 0.4f, h) }, m(VETA, 0.2f), style = Stroke(0.4f * u))
            }
        }
        // Agujeros de clavos viejos, en los márgenes.
        repeat(10) {
            val o = if (rnd.nextBoolean()) Offset(w * (0.01f + 0.04f * rnd.nextFloat()), h * rnd.nextFloat()) else Offset(w * (0.95f + 0.04f * rnd.nextFloat()), h * rnd.nextFloat())
            drawCircle(m(VETA, 0.5f), 0.4f * u, o)
        }
        // Las dos fajas de hierro, claveteadas.
        listOf(1.6f * u, h - 5.2f * u).forEach { y ->
            drawRect(m(HIERRO, 0.62f), Offset(0f, y), Size(w, 3.6f * u))
            drawLine(m(HIERRO_LUZ, 0.55f), Offset(0f, y + 0.4f * u), Offset(w, y + 0.4f * u), 0.4f * u)
            val nc = (w / (14f * u)).toInt().coerceIn(2, 40)
            for (k in 0 until nc) drawCircle(m(HIERRO_LUZ, 0.75f), 0.7f * u, Offset(w * (k + 0.5f) / nc, y + 1.8f * u))
        }
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        translate(r.left, r.top) {
            val w = r.width; val h = r.height; val u = minOf(w, h) / 100f
            val papel = if (oscuro) HOJA_OSCURA else HOJA_CLARA
            val forma = hojaPergamino(Rect(0f, 0f, w, h), e.indice * 13 + 7, e.indice % 3 == 0)
            translate(1.2f * u, 2.2f * u) { drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f)) }
            drawPath(forma, papel)
            clipPath(forma) {
                // El borde, apenas más tostado.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, ROBLE.copy(alpha = if (oscuro) 0.1f else 0.08f)), h * 0.7f, h), Offset(0f, h * 0.7f), Size(w, h * 0.3f))
            }
            val borde = when {
                e.fijada -> e.acento
                oscuro -> mez(papel, PERGAMINO, 0.3f)
                else -> mez(papel, ROBLE_OSC, 0.4f)
            }
            drawPath(forma, borde, style = Stroke(if (e.fijada) 2f * u else 1f * u))
            val rojo = if (oscuro) mez(BERMELLON, Color.White, 0.3f) else BERMELLON
            val azul = if (oscuro) mez(LAPIZ, Color.White, 0.4f) else LAPIZ
            // Los picados del pautado en el margen izquierdo y la doble línea roja de la marginalia.
            for (k in 0 until 5) drawCircle(if (oscuro) PERGAMINO.copy(alpha = 0.4f) else TINTA.copy(alpha = 0.35f), 0.7f * u, Offset(w * 0.045f, h * (0.2f + 0.15f * k)))
            drawLine(rojo, Offset(w * 0.08f, h * 0.17f), Offset(w * 0.08f, h * 0.86f), 0.8f * u)
            drawLine(rojo, Offset(w * 0.095f, h * 0.17f), Offset(w * 0.095f, h * 0.86f), 0.5f * u)
            // El rasgueo rojo abajo a la derecha.
            drawPath(Path().apply {
                moveTo(w * 0.7f, h * 0.92f); quadraticTo(w * 0.8f, h * 0.86f, w * 0.86f, h * 0.92f)
                quadraticTo(w * 0.92f, h * 0.97f, w * 0.94f, h * 0.88f)
            }, rojo, style = Stroke(0.8f * u, cap = StrokeCap.Round))

            // El detalle del tipo, arriba a la derecha (o la cinta de registro, a la derecha).
            val o = Offset(w * 0.9f, h * 0.11f); val rr = h * 0.06f
            when (e.tipo) {
                "idea" -> {
                    drawCircle(ORO, rr * 0.85f, o)
                    drawCircle(ORO_OSC, rr * 0.85f, o, style = Stroke(rr * 0.2f))
                }
                "pensamiento" -> drawPath(Path().apply {
                    moveTo(o.x - rr, o.y + rr * 0.6f); quadraticTo(o.x - rr, o.y - rr, o.x, o.y - rr * 0.6f)
                    quadraticTo(o.x + rr, o.y - rr * 0.2f, o.x + rr * 0.2f, o.y + rr * 0.4f)
                }, azul, style = Stroke(rr * 0.25f, cap = StrokeCap.Round))
                "proposito" -> {
                    drawLine(rojo, Offset(o.x - rr, o.y), Offset(o.x + rr, o.y), rr * 0.35f)
                    drawLine(rojo, Offset(o.x, o.y - rr), Offset(o.x, o.y + rr), rr * 0.35f)
                }
                "recordatorio" -> {
                    val x = w * 0.9f; val an = w * 0.045f
                    drawPath(Path().apply {
                        moveTo(x - an / 2, h * 0.55f); lineTo(x + an / 2, h * 0.55f); lineTo(x + an / 2, h); lineTo(x, h - an * 0.8f); lineTo(x - an / 2, h); close()
                    }, if (oscuro) mez(e.acento, Color.White, 0.1f) else BERMELLON)
                }
                else -> {}
            }

            // Los clavos: uno de hierro arriba al centro; si está fijada, dos dorados (al centro y en la esquina de la izquierda).
            val clavos = if (e.fijada) listOf(Offset(w * 0.09f, h * 0.08f), Offset(w * 0.5f, h * 0.075f)) else listOf(Offset(w * 0.5f, h * 0.075f))
            clavos.forEach { cl ->
                drawCircle(Color.Black.copy(alpha = 0.3f), h * 0.042f, Offset(cl.x + 0.6f * u, cl.y + 0.9f * u))
                drawCircle(if (e.fijada) ORO else if (oscuro) HIERRO_LUZ else HIERRO, h * 0.04f, cl)
                if (e.fijada) drawCircle(ORO_OSC, h * 0.04f, cl, style = Stroke(0.5f * u))
                drawCircle(Color.White.copy(alpha = 0.35f), h * 0.014f, Offset(cl.x - h * 0.012f, cl.y - h * 0.012f))
            }
        }
    }

    /** Una hoja de pergamino recortada a cuchilla: casi rectangular, con un lado ondulado (el borde de la piel) si [conBorde]. */
    private fun hojaPergamino(r: Rect, semilla: Int, conBorde: Boolean): Path {
        val rnd = Random(semilla.toLong() * 4091L + 3L)
        fun j() = (rnd.nextFloat() - 0.5f) * minOf(r.width, r.height) * 0.04f
        val x0 = r.left + r.width * 0.025f; val x1 = r.right - r.width * 0.025f
        val y0 = r.top + r.height * 0.03f; val y1 = r.bottom - r.height * 0.04f
        return Path().apply {
            moveTo(x0 + j(), y0 + j()); lineTo(x1 + j(), y0 + j())
            if (conBorde) {
                val pasos = 6
                for (k in 1 until pasos) lineTo(x1 - r.width * 0.02f * (if (k % 2 == 0) 1f else 0.2f), y0 + (y1 - y0) * k / pasos)
            }
            lineTo(x1 + j(), y1 + j()); lineTo(x0 + j(), y1 + j()); close()
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFEDDFC2) else Color(0xFF2A1B10)
}
