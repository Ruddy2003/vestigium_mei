package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * La pared del estante de Napoleónico ([MuebleEstanteNapoleon]): la esquina del cuarto de la casa requisada. Al fondo, el
 * enlucido pintado de amarillo Imperio, con la moldura del techo, el friso pintado de verde con palmetas y rosetas en ocre
 * entre filetes, el recuadro de filetes verdes con sus esquinas en cuarto de círculo, el rectángulo más vivo de donde quitaron
 * un cuadro (con su clavo), el humo de la lámpara, grietas y un desconchón que deja ver el enlucido pardo. A la izquierda, el
 * muro del costado en escorzo con la ventana de dos hojas de cuarterones, su falleba y su contraventana plegada: de día, el
 * sol que deja en la pared la trama de los cuarterones; de noche, el cielo de afuera.
 */
internal object MuroEstanteNapoleon {
    private val P = PaletaNapoleon

    /** La esquina con el muro de la ventana, en anchos. */
    const val ESQUINA = 0.11f
    /** El friso pintado bajo el techo, en anchos. */
    const val FRISO0 = 0.016f
    const val FRISO1 = 0.07f
    /** La altura de los ojos (para el escorzo del costado), en anchos. */
    private const val OJOS = 0.75f

    /** Un punto del costado: [p] de la esquina (0) hacia quien mira (1), a la altura [y] que tendría en la esquina. */
    fun enCostado(w: Float, p: Float, y: Float): Offset {
        val x = ESQUINA * (1f - p * (0.88f + 0.12f * p))
        val yy = OJOS + (y - OJOS) * (1f + 0.2f * p)
        return Offset(x * w, yy * w)
    }

    /** Un punto de la ventana: [fx] de lejos (0, junto a la esquina) a cerca (1), [fy] de arriba (0) a abajo (1). */
    fun enVentana(w: Float, fx: Float, fy: Float): Offset = enCostado(w, 0.2f + 0.56f * fx, -0.03f + 0.36f * fy)

    // ---------------------------------------------------------------- la pared entera (arriba)

    fun DrawScope.pared(a: AireNapoleon, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width
        val fondo = Rect(w * ESQUINA, 0f, w, apoyo + w * 0.02f)
        baseAmarillo(fondo, a, null)
        pintura(fondo, a, cerca, null)
        // La mancha de la ventana: la misma pared, alumbrada, dentro de los cuarterones.
        if (m != null) {
            clipRect(fondo.left, fondo.top, fondo.right, fondo.bottom) {
                val medio = ManchaNapoleon(m.q, m.color, m.fuerza * 0.42f)
                clipPath(m.forma(w, 0f, 0.012f)) { baseAmarillo(fondo, a, medio); pintura(fondo, a, cerca, medio) }
                clipPath(m.forma(w)) { baseAmarillo(fondo, a, m); pintura(fondo, a, cerca, m) }
            }
        }
        // La sombra de la esquina en la pared del fondo.
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.2f), Offset(w * ESQUINA, 0f), Size(w * 0.012f, fondo.bottom))
        drawRect(a.sombra(if (a.lumbre) 0.2f else 0.09f), Offset(w * ESQUINA + w * 0.012f, 0f), Size(w * 0.035f, fondo.bottom))
        costado(a, apoyo, cerca, m)
        moldura(a, cerca, m)
    }

    /** El amarillo liso de [r]: de día, más claro junto a la ventana; de noche, encendido bajo la bouillotte y hundido lejos. */
    private fun DrawScope.baseAmarillo(r: Rect, a: AireNapoleon, m: ManchaNapoleon?) {
        val w = size.width
        if (m != null && !a.oscuro) {
            val c0 = a.en(P.AMARILLO, 0f, 1f, 1f, m); val c1 = a.en(mezcla(P.AMARILLO, P.AMARILLO_SOMBRA, 0.25f), 0f, 1f, 1f, m)
            drawRect(Brush.verticalGradient(listOf(c0, c1), m.en(0f, 0f).y * w, m.en(1f, 1f).y * w), r.topLeft, r.size)
            return
        }
        if (a.lumbre && a.oscuro) {
            // El charco de la bouillotte, con la sombra de su pantalla: franjas a lo alto, cada una con su degradado de arriba
            // abajo según la luz que llega a cada punto (en la imagen guardada; se pinta una vez).
            val n = 96
            val ys = FloatArray(40) { i -> r.top + r.height * i / 39f }
            for (t in 0 until n) {
                val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1f
                val dx = ((x0 + x1) / 2) / w - MuebleEstanteNapoleon.LAMP_X
                val cols = ys.map { y -> a.luz(P.AMARILLO, MuebleEstanteNapoleon.lamparaArriba(dx, y / w - MuebleEstanteNapoleon.LAMP_Y, a.noche)) }
                drawRect(Brush.verticalGradient(*Array(ys.size) { i -> (i / 39f) to cols[i] }, startY = r.top, endY = r.bottom), Offset(x0, r.top), Size(x1 - x0, r.height))
            }
            if (m != null) drawRect(Color(0xFF7890C8).copy(alpha = (0.5f * m.fuerza).coerceAtMost(1f)), r.topLeft, r.size, blendMode = BlendMode.Screen)
            return
        }
        // De día (y al ocaso): el amarillo a la sombra del cuarto, más vivo junto a la ventana y más hondo arriba bajo el techo.
        val claro = a.dia(mezcla(P.AMARILLO, P.AMARILLO_SOMBRA, 0.12f), 0f)
        val hondo = a.dia(mezcla(P.AMARILLO, P.AMARILLO_SOMBRA, 0.62f), 0f)
        drawRect(Brush.horizontalGradient(listOf(claro, mezcla(claro, hondo, 0.45f), hondo), r.left, r.right), r.topLeft, r.size)
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.16f), a.sombra(0f)), r.top + w * FRISO1, r.top + w * 0.2f), Offset(r.left, r.top + w * FRISO1), Size(r.width, w * 0.13f))
    }

    /**
     * La pintura del amarillo (con mezcla de multiplicar, así sigue la luz que tenga debajo): las manos de pintura, el recuadro de
     * filetes verdes, el friso de palmetas, el rectángulo del cuadro quitado, el humo de la lámpara, las grietas; y el
     * desconchón, donde asoma el enlucido.
     */
    private fun DrawScope.pintura(r: Rect, a: AireNapoleon, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        // La pintura no es pareja: manchas de las manos de cal teñida y brochazos.
        val motas = Path()
        for (j in 0 until 16) {
            val mx = r.left + r.width * azar(8050, j); val my = w * (FRISO1 + 0.01f) + (r.bottom - w * FRISO1) * azar(8051, j)
            val mw = w * (0.07f + 0.12f * azar(8052, j)); val mh = mw * (0.3f + 0.3f * azar(8053, j))
            motas.addOval(Rect(mx - mw / 2, my - mh / 2, mx + mw / 2, my + mh / 2))
        }
        drawPath(motas, Color(0xFFF2E2C0).copy(alpha = 0.34f), blendMode = BlendMode.Multiply)
        val brochazos = Path()
        for (j in 0 until 40) {
            val bx = r.left + r.width * azar(8060, j); val by = w * (FRISO1 + 0.02f) + (r.bottom - w * FRISO1) * azar(8061, j)
            val bl = w * (0.06f + 0.1f * azar(8062, j))
            brochazos.moveTo(bx, by); brochazos.quadraticTo(bx + bl * 0.5f, by + w * 0.005f, bx + bl * 0.9f, by + w * 0.02f)
        }
        drawPath(brochazos, Color(0xFFEEDDBA).copy(alpha = 0.5f), style = Stroke(w * 0.005f, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        // El recuadro pintado: un filete verde ancho y otro fino dentro, con las esquinas en cuarto de círculo hacia dentro.
        val verde = Color(0xFF5C7A52)
        val rec = Rect(w * 0.19f, w * 0.1f, w * 0.965f, r.bottom + w * 0.05f)
        listOf(0f to 2.6f, 0.014f to 0.9f).forEach { (d, g) ->
            val q = Rect(rec.left + w * d, rec.top + w * d, rec.right - w * d, rec.bottom - w * d)
            val rr = w * 0.03f
            val p = Path().apply {
                moveTo(q.left + rr, q.top); lineTo(q.right - rr, q.top)
                arcTo(Rect(q.right - rr, q.top - rr, q.right + rr, q.top + rr), 180f, -90f, false)
                lineTo(q.right, q.bottom)
                moveTo(q.left, q.bottom); lineTo(q.left, q.top + rr)
                arcTo(Rect(q.left - rr, q.top - rr, q.left + rr, q.top + rr), 90f, -90f, false)
            }
            drawPath(p, verde.copy(alpha = 0.8f), style = Stroke(g * u), blendMode = BlendMode.Multiply)
        }
        // El rectángulo del cuadro que quitaron: el amarillo sin sol, más vivo, con su cerco de polvo y el clavo.
        val cuadro = Rect(w * 0.47f, w * 0.115f, w * 0.585f, w * 0.215f)
        drawRect(Color(0xFFF6D9A6).copy(alpha = 0.5f), cuadro.topLeft, cuadro.size, blendMode = BlendMode.Multiply)
        drawRect(Color(0xFFC8AE84).copy(alpha = 0.6f), cuadro.topLeft, cuadro.size, style = Stroke(1.4f * u), blendMode = BlendMode.Multiply)
        val clavo = Offset(cuadro.center.x, cuadro.top - w * 0.018f)
        drawLine(Color(0xFFCDB895).copy(alpha = 0.5f), clavo, Offset(cuadro.left + cuadro.width * 0.2f, cuadro.top), 0.8f * u, blendMode = BlendMode.Multiply)
        drawLine(Color(0xFFCDB895).copy(alpha = 0.5f), clavo, Offset(cuadro.right - cuadro.width * 0.2f, cuadro.top), 0.8f * u, blendMode = BlendMode.Multiply)
        // El humo de las velas de la bouillotte: una lengua parda que sube de la pantalla.
        val lx = w * MuebleEstanteNapoleon.LAMP_X
        val humo = Path().apply {
            moveTo(lx - w * 0.01f, w * 0.23f)
            cubicTo(lx - w * 0.04f, w * 0.17f, lx - w * 0.06f, w * 0.12f, lx - w * 0.045f, w * 0.075f)
            lineTo(lx + w * 0.07f, w * 0.075f)
            cubicTo(lx + w * 0.07f, w * 0.13f, lx + w * 0.035f, w * 0.18f, lx + w * 0.01f, w * 0.23f); close()
        }
        drawPath(humo, Color(0xFF8A7A66).copy(alpha = 0.1f), blendMode = BlendMode.Multiply)
        drawPath(Path().apply { addPath(humo, Offset(0f, -w * 0.02f)) }, Color(0xFF8A7A66).copy(alpha = 0.07f), blendMode = BlendMode.Multiply)
        // Las grietas.
        val grietas = Path()
        listOf(Offset(0.31f, 0.075f) to 0.6f, Offset(0.82f, 0.32f) to -0.4f, Offset(0.92f, 0.08f) to 0.3f).forEachIndexed { i, (o, s) ->
            var p = Offset(w * o.x, w * o.y)
            grietas.moveTo(p.x, p.y)
            for (j in 0 until 7) {
                p += Offset(w * (0.008f + 0.012f * azar(8070 + i, j)) * s, w * (0.012f + 0.01f * azar(8071 + i, j)))
                grietas.lineTo(p.x, p.y)
            }
        }
        drawPath(grietas, Color(0xFF7A5A2C).copy(alpha = 0.6f), style = Stroke(0.8f * u), blendMode = BlendMode.Multiply)
        // El friso: la banda verde entre dos filetes oscuros, con las palmetas y rosetas en ocre (gastadas).
        friso(r, a, cerca, m)
        // El desconchón, bajo el friso a la derecha: una tira de pintura caída a lo largo de su filete, irregular, donde asoma
        // el enlucido pardo con el canto claro de la pintura.
        val des = Path().apply {
            moveTo(w * 0.73f, w * 0.074f)
            lineTo(w * 0.86f, w * 0.074f)
            cubicTo(w * 0.85f, w * 0.085f, w * 0.83f, w * 0.082f, w * 0.815f, w * 0.095f)
            cubicTo(w * 0.8f, w * 0.106f, w * 0.785f, w * 0.09f, w * 0.77f, w * 0.098f)
            cubicTo(w * 0.755f, w * 0.104f, w * 0.745f, w * 0.088f, w * 0.73f, w * 0.074f); close()
        }
        val kd = cerca(Offset(w * 0.8f, w * 0.085f)); val lit = if (m != null) 1f else 0f
        drawPath(des, a.en(P.ENLUCIDO, 0f, kd, lit, m))
        clipPath(des) {
            for (j in 0 until 9) {
                val p0 = Offset(w * (0.735f + 0.12f * azar(8081, j)), w * (0.076f + 0.022f * azar(8082, j)))
                drawLine(a.en(P.ENLUCIDO_OSC, 0f, kd, lit, m), p0, p0 + Offset(w * (0.004f + 0.006f * azar(8080, j)), w * 0.001f), 1.1f * u)
            }
            drawPath(Path().apply { addPath(des, Offset(w * 0.003f, w * 0.004f)) }, a.sombra(0.3f), style = Stroke(w * 0.005f))
        }
        drawPath(des, a.en(P.AMARILLO_LUZ, 0f, kd, lit, m), style = Stroke(1f * u))
    }

    /** El friso pintado bajo el techo: verde Imperio, con palmetas y rosetas en ocre alternadas, entre filetes oscuros. */
    private fun DrawScope.friso(r: Rect, a: AireNapoleon, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val y0 = w * FRISO0; val y1 = w * FRISO1
        val banda = Rect(r.left, y0, r.right, y1)
        drawRect(Color(0xFF6E8C6A), banda.topLeft, banda.size, blendMode = BlendMode.Multiply)
        drawRect(Color(0xFF46604A).copy(alpha = 0.9f), Offset(r.left, y0), Size(r.width, 2.4f * u), blendMode = BlendMode.Multiply)
        drawRect(Color(0xFF46604A).copy(alpha = 0.9f), Offset(r.left, y1 - 2.4f * u), Size(r.width, 2.4f * u), blendMode = BlendMode.Multiply)
        // Las palmetas y las rosetas, en un solo camino cada una (ocre con multiplicar sobre el verde no se vería: se pintan
        // encima con su color, ya teñido por la luz del sitio).
        val palmetas = Path(); val rosetas = Path()
        val paso = w * 0.06f
        val cy = (y0 + y1) / 2
        var x = r.left + paso * 0.5f
        var j = 0
        while (x < r.right + paso) {
            if (j % 2 == 0) {
                // Una palmeta: un abanico de siete hojas angostas sobre su base en voluta.
                val base = Offset(x, y1 - w * 0.012f)
                for (h in 0 until 7) {
                    val an = (-90f + (h - 3) * 17f) * 0.01745f
                    val largo = w * (0.026f - 0.003f * kotlin.math.abs(h - 3))
                    val tip = Offset(base.x + largo * cos(an), base.y + largo * sin(an))
                    val nx = -sin(an) * w * 0.0035f; val ny = cos(an) * w * 0.0035f
                    palmetas.moveTo(base.x, base.y)
                    palmetas.quadraticTo((base.x + tip.x) / 2 + nx, (base.y + tip.y) / 2 + ny, tip.x, tip.y)
                    palmetas.quadraticTo((base.x + tip.x) / 2 - nx, (base.y + tip.y) / 2 - ny, base.x, base.y)
                }
                palmetas.addOval(Rect(base.x - w * 0.008f, base.y - w * 0.003f, base.x + w * 0.008f, base.y + w * 0.006f))
            } else {
                // Una roseta: un botón con ocho pétalos.
                rosetas.addOval(Rect(x - w * 0.0045f, cy - w * 0.0045f, x + w * 0.0045f, cy + w * 0.0045f))
                for (h in 0 until 8) {
                    val an = h * 0.785f
                    val c = Offset(x + w * 0.0105f * cos(an), cy + w * 0.0105f * sin(an))
                    rosetas.addOval(Rect(c.x - w * 0.0035f, c.y - w * 0.0035f, c.x + w * 0.0035f, c.y + w * 0.0035f))
                }
            }
            x += paso; j++
        }
        val k = cerca(Offset(w * 0.6f, cy))
        val lit = if (m != null) 1f else 0f
        val ocre = a.en(mezcla(P.OCRE, P.AMARILLO_SOMBRA, 0.2f), 0f, k, lit, m)
        clipRect(banda.left, banda.top, banda.right, banda.bottom) {
            drawPath(palmetas, ocre.copy(alpha = 0.85f))
            drawPath(rosetas, ocre.copy(alpha = 0.85f))
            // Lo gastado: el ocre se ha ido en algunos sitios y el verde se ha manchado de humo.
            for (q in 0 until 7) {
                val gx = r.left + r.width * azar(8090, q)
                drawOval(Color(0xFF9AA88E).copy(alpha = 0.5f), Offset(gx, y0 + (y1 - y0) * (0.15f + 0.5f * azar(8091, q))), Size(w * (0.03f + 0.05f * azar(8092, q)), (y1 - y0) * 0.35f), blendMode = BlendMode.Multiply)
            }
        }
    }

    /** La moldura del techo, arriba de todo: una gola de yeso pintado, clara, cortada por el borde. */
    private fun DrawScope.moldura(a: AireNapoleon, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val y1 = w * FRISO0
        val n = if (a.lumbre) 16 else 1
        for (t in 0 until n) {
            val x0 = w * ESQUINA + (w - w * ESQUINA) * t / n; val x1 = w * ESQUINA + (w - w * ESQUINA) * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, y1))
            drawRect(a.dia(Color(0xFFEDE2C8), 0f, k), Offset(x0, 0f), Size(x1 - x0, y1 * 0.55f))
            drawRect(a.dia(Color(0xFFCFBF9C), 0f, k), Offset(x0, y1 * 0.55f), Size(x1 - x0, y1 * 0.45f))
        }
        drawRect(a.sombra(0.45f), Offset(w * ESQUINA, y1 - 1.2f * u), Size(w, 1.2f * u))
        drawRect(a.claro(0.4f), Offset(w * ESQUINA, y1 * 0.55f), Size(w, 0.8f * u))
    }

    // ---------------------------------------------------------------- el costado con la ventana

    /**
     * El muro del costado en escorzo, con la ventana: el amarillo en sombra (la luz entra por él, no le da), el derrame hondo de
     * la ventana alumbrado por el cielo, el marco y las dos hojas de cuarterones pintados de gris, la falleba de latón, el
     * vierteaguas, la contraventana interior plegada contra el derrame de este lado y, por los vidrios, el cielo: de día, claro;
     * de noche, azul hondo con estrellas; en los Sueños, el ocaso o la luna.
     */
    private fun DrawScope.costado(a: AireNapoleon, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNapoleon?) {
        val w = size.width; val u = density
        val fondoY = apoyo + w * 0.02f
        // El muro.
        val muro = Path().apply {
            val p0 = enCostado(w, 0f, -0.1f); val p1 = enCostado(w, 0f, fondoY / w); val p2 = enCostado(w, 1.15f, fondoY / w); val p3 = enCostado(w, 1.15f, -0.1f)
            moveTo(p0.x, p0.y); lineTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close()
        }
        val km = cerca(Offset(w * 0.05f, w * 0.25f))
        drawPath(muro, a.dia(mezcla(P.AMARILLO, P.AMARILLO_HONDO, 0.45f), 0f, km))
        clipPath(muro) {
            drawRect(a.sombra(0.12f), Offset(0f, 0f), Size(w * ESQUINA, w * 0.08f))
            // La moldura y el friso, en escorzo.
            val f0a = enCostado(w, 0f, FRISO0); val f0b = enCostado(w, 1.15f, FRISO0)
            val f1a = enCostado(w, 0f, FRISO1); val f1b = enCostado(w, 1.15f, FRISO1)
            drawPath(poligonoNapoleon(f0a.x, -w, f0b.x, -w, f0b.x, f0b.y, f0a.x, f0a.y), a.dia(Color(0xFFCFBF9C), -0.2f, km))
            drawPath(poligonoNapoleon(f0a.x, f0a.y, f0b.x, f0b.y, f1b.x, f1b.y, f1a.x, f1a.y), a.dia(mezcla(P.VERDE, P.VERDE_OSC, 0.4f), 0f, km))
        }
        // El hueco de la ventana: el derrame del lado de la esquina (de cara a quien mira) y el de arriba, alumbrados por el cielo.
        val v00 = enVentana(w, 0f, 0f); val v10 = enVentana(w, 1f, 0f); val v11 = enVentana(w, 1f, 1f); val v01 = enVentana(w, 0f, 1f)
        val hondo = w * 0.024f
        val cieloLuz = if (a.oscuro) (if (a.noche) Color(0xFF7084B0) else Color(0xFF34406A)) else if (a.noche) Color(0xFFF2B68A) else Color(0xFFF4F0E2)
        val derrame = poligonoNapoleon(v00.x, v00.y, v00.x - hondo * 0.3f, v00.y + hondo * 0.6f, v01.x - hondo * 0.3f, v01.y - hondo * 0.2f, v01.x, v01.y)
        drawPath(poligonoNapoleon(v00.x, v00.y, v10.x, v10.y, v11.x, v11.y, v01.x, v01.y), a.dia(mezcla(P.AMARILLO, cieloLuz, 0.35f), 0.1f, km))
        // Los vidrios: el cielo por los cuarterones (el marco y los peinazos se pintan encima).
        val vid = poligonoNapoleon(enVentana(w, 0.1f, 0.04f).x, enVentana(w, 0.1f, 0.04f).y, enVentana(w, 0.9f, 0.04f).x, enVentana(w, 0.9f, 0.04f).y,
            enVentana(w, 0.9f, 0.94f).x, enVentana(w, 0.9f, 0.94f).y, enVentana(w, 0.1f, 0.94f).x, enVentana(w, 0.1f, 0.94f).y)
        clipPath(vid) { cielo(a) }
        // El marco, el montante de en medio y los peinazos de las dos hojas, de madera pintada de gris claro.
        val gris = a.dia(Color(0xFFCFCBBE), 0f, km)
        val grisSombra = a.dia(Color(0xFF8E8A7E), 0f, km)
        val marco = Path()
        fun barraV(fx: Float, ancho: Float) {
            val a0 = enVentana(w, fx - ancho, 0.04f); val a1 = enVentana(w, fx + ancho, 0.04f); val b1 = enVentana(w, fx + ancho, 0.94f); val b0 = enVentana(w, fx - ancho, 0.94f)
            marco.moveTo(a0.x, a0.y); marco.lineTo(a1.x, a1.y); marco.lineTo(b1.x, b1.y); marco.lineTo(b0.x, b0.y); marco.close()
        }
        fun barraH(fy: Float, alto: Float, fx0: Float, fx1: Float) {
            val a0 = enVentana(w, fx0, fy - alto); val a1 = enVentana(w, fx1, fy - alto); val b1 = enVentana(w, fx1, fy + alto); val b0 = enVentana(w, fx0, fy + alto)
            marco.moveTo(a0.x, a0.y); marco.lineTo(a1.x, a1.y); marco.lineTo(b1.x, b1.y); marco.lineTo(b0.x, b0.y); marco.close()
        }
        barraV(0.1f, 0.045f); barraV(0.9f, 0.05f); barraV(0.5f, 0.04f)
        listOf(0.265f, 0.49f, 0.715f).forEach { fy -> barraH(fy, 0.008f, 0.1f, 0.9f) }
        barraH(0.04f, 0.02f, 0.06f, 0.95f); barraH(0.94f, 0.022f, 0.06f, 0.95f)
        drawPath(Path().apply { addPath(marco, Offset(1.2f * u, 1.4f * u)) }, a.sombra(0.35f))
        drawPath(marco, gris)
        drawPath(marco, grisSombra, style = Stroke(0.6f * u))
        // La falleba de latón en el montante de en medio, con su manija.
        val f0 = enVentana(w, 0.5f, 0.08f); val f1 = enVentana(w, 0.5f, 0.9f)
        val kf = if (a.oscuro) cerca(Offset(f0.x, (f0.y + f1.y) / 2)) else 1f
        drawLine(a.sombra(0.4f), f0 + Offset(1f * u, 0f), f1 + Offset(1f * u, 0f), 1.4f * u)
        drawLine(a.luz(P.LATON, kf), f0, f1, 1.3f * u)
        val mj = enVentana(w, 0.5f, 0.56f)
        drawLine(a.luz(P.LATON, kf), mj, mj + Offset(w * 0.012f, w * 0.008f), 2f * u, StrokeCap.Round)
        drawCircle(a.brillo(0.6f, kf), 1f * u, mj)
        // El vierteaguas y el alféizar, abajo: la tabla con su canto y la sombra que echa en el muro.
        val s0 = enVentana(w, -0.08f, 1f); val s1 = enVentana(w, 1.06f, 1f)
        drawPath(poligonoNapoleon(s0.x, s0.y, s1.x, s1.y, s1.x, s1.y + w * 0.014f, s0.x, s0.y + w * 0.012f), a.dia(Color(0xFFBFB8A4), 0f, km))
        drawPath(poligonoNapoleon(s0.x, s0.y + w * 0.012f, s1.x, s1.y + w * 0.014f, s1.x, s1.y + w * 0.03f, s0.x, s0.y + w * 0.022f), a.sombra(0.3f))
        // El derrame de este lado y la contraventana plegada contra él: dos hojas de tablero pintado, con sus cuarterones.
        drawPath(derrame, a.dia(mezcla(P.AMARILLO, cieloLuz, 0.55f), 0.18f, km))
        val c0 = enVentana(w, 1.02f, -0.02f); val c1 = enVentana(w, 1.3f, -0.02f); val c2 = enVentana(w, 1.3f, 1f); val c3 = enVentana(w, 1.02f, 1f)
        val contra = poligonoNapoleon(c0.x, c0.y, c1.x, c1.y, c2.x, c2.y, c3.x, c3.y)
        drawPath(Path().apply { addPath(contra, Offset(1.6f * u, 1f * u)) }, a.sombra(0.35f))
        drawPath(contra, a.dia(Color(0xFFB9B4A2), -0.05f, km))
        clipPath(contra) {
            listOf(0.08f, 0.53f).forEach { fy ->
                val q0 = enVentana(w, 1.07f, fy); val q1 = enVentana(w, 1.25f, fy); val q2 = enVentana(w, 1.25f, fy + 0.38f); val q3 = enVentana(w, 1.07f, fy + 0.38f)
                val panel = poligonoNapoleon(q0.x, q0.y, q1.x, q1.y, q2.x, q2.y, q3.x, q3.y)
                drawPath(panel, a.sombra(0.15f))
                drawPath(panel, a.sombra(0.45f), style = Stroke(0.8f * u))
                drawLine(a.claro(0.4f), q3, q2, 0.8f * u)
            }
            drawRect(a.claro(0.25f), Offset(c0.x - w * 0.003f, 0f), Size(w * 0.003f, w))
        }
    }

    /** El cielo por los vidrios: de día, claro abajo y azul arriba; de noche, azul hondo con estrellas; al ocaso, naranja. */
    private fun DrawScope.cielo(a: AireNapoleon) {
        val w = size.width; val u = density
        val top = enVentana(w, 0.5f, 0f).y; val bot = enVentana(w, 0.5f, 1f).y
        val (alto, bajo) = when {
            a.oscuro -> P.CIELO_NOCHE_ALTO to P.CIELO_NOCHE
            a.noche -> Color(0xFF6A5C98) to Color(0xFFF2A86E)
            else -> P.CIELO_ALTO to P.CIELO
        }
        drawRect(Brush.verticalGradient(listOf(alto, bajo), top, bot), Offset(0f, top - w * 0.05f), Size(w * ESQUINA, bot - top + w * 0.1f))
        if (a.oscuro) {
            val estrellas = Path()
            for (j in 0 until 10) {
                val p = enVentana(w, 0.12f + 0.76f * azar(8120, j), 0.06f + 0.8f * azar(8121, j))
                estrellas.addOval(Rect(p.x - 0.7f * u, p.y - 0.7f * u, p.x + 0.7f * u, p.y + 0.7f * u))
            }
            drawPath(estrellas, Color(0xFFE6ECFF).copy(alpha = 0.8f))
            if (a.noche) {
                // La luna, en un cuarterón de arriba, con su halo.
                val lc = enVentana(w, 0.3f, 0.16f)
                mancha(lc, w * 0.05f, P.LUNA.copy(alpha = 0.35f))
                drawCircle(P.LUNA, w * 0.013f, lc)
                drawCircle(Color(0xFFC8D2E6), w * 0.004f, lc + Offset(w * 0.004f, w * 0.003f))
            } else {
                // El reflejo de la bouillotte en el vidrio.
                val rf = enVentana(w, 0.72f, 0.62f)
                mancha(rf, w * 0.025f, P.LLAMA.copy(alpha = 0.3f))
                drawCircle(P.FUEGO.copy(alpha = 0.6f), 1.2f * u, rf)
            }
        } else if (!a.noche) {
            // Una nube clara y el reflejo del cuarto en el vidrio de la hoja de cerca.
            mancha(enVentana(w, 0.3f, 0.5f), w * 0.04f, Color.White.copy(alpha = 0.4f))
            drawRect(Color.White.copy(alpha = 0.12f), Offset(enVentana(w, 0.55f, 0f).x, top), Size(w * 0.02f, bot - top))
        }
    }

    // ---------------------------------------------------------------- la luz de noche, por encima de lo de arriba

    /** De noche, el resplandor de la bouillotte sobre lo que tiene cerca (lo de arriba), solo hasta donde su luz se ve. */
    fun DrawScope.luzDeNoche(a: AireNapoleon, ch: Float) {
        if (!a.oscuro) return
        val w = size.width
        val c = Offset(w * MuebleEstanteNapoleon.LAMP_X, w * (MuebleEstanteNapoleon.LAMP_Y + 0.015f))
        val f = if (a.noche) 0.55f else 1f
        clipRect(0f, c.y - w * 0.04f, w, ch) {
            mancha(c, w * 0.34f, P.LLAMA.copy(alpha = 0.16f * f))
            mancha(c, w * 0.14f, P.LLAMA.copy(alpha = 0.28f * f))
        }
        mancha(c, w * 0.05f, P.FUEGO.copy(alpha = 0.4f * f))
    }
}
