package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.CARTON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HUESO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.PLASTICO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

private val VERDE_PLANTA_NEON = Color(0xFF4E8F3A)
private val VERDE_PLANTA_OSC_NEON = Color(0xFF28562A)
private val ROJO_CAJA_NEON = Color(0xFFC8322E)
private val ESMALTE_NEON = Color(0xFFE9E4DA)
private val AZUL_ESMALTE_NEON = Color(0xFF2E58A8)

/**
 * Lo que acompaña en el hueco libre de la repisa [k] (de [desde], el borde del último volumen, al costado): grupos de dos o tres
 * cosas del cuarto, grandes (del 70 al 90 % del alto) y solapadas, que cambian de un hueco a otro; en un hueco muy ancho, dos
 * grupos distintos. El televisor portátil con estática y la caja de cables; el termo de flores con las tazas de lata esmaltada;
 * la caja de herramientas con el soldador en su soporte; la planta en una botella cortada con el pulverizador.
 */
internal fun DrawScope.huecoLibreNeon(k: Int, r: Rect, desde: Float, g: MarcoNeon) {
    val w = g.w
    val x0 = desde + w * 0.012f
    val x1 = r.right - w * 0.02f
    val libre = x1 - x0
    if (libre < w * 0.17f) return
    val base = (k * 3 + 1) % 4
    val ancho = w * 0.27f
    if (libre >= ancho * 1.9f) {
        grupoNeon(base, Rect(x0, r.top, x0 + ancho, r.bottom), g, k)
        grupoNeon((base + 2) % 4, Rect(x1 - ancho, r.top, x1, r.bottom), g, k + 11)
    } else {
        val an = minOf(libre, ancho)
        grupoNeon(base, Rect(x1 - an, r.top, x1, r.bottom), g, k)
    }
}

/** Un grupo dentro de [r] (pisa en [Rect.bottom]). */
private fun DrawScope.grupoNeon(cual: Int, r: Rect, g: MarcoNeon, s: Int) {
    val h = r.height
    val suelo = r.bottom - g.w * 0.004f
    when (cual) {
        0 -> {
            televisorNeon(Rect(r.right - r.width * 0.94f, suelo - h * 0.66f, r.right - r.width * 0.02f, suelo), g)
            cajaCablesNeon(Rect(r.left - r.width * 0.06f, suelo - h * 0.3f, r.left + r.width * 0.4f, suelo), g, s)
        }
        1 -> {
            termoNeon(Rect(r.left + r.width * 0.3f, suelo - h * 0.9f, r.left + r.width * 0.66f, suelo), g)
            tazaLataNeon(Rect(r.left, suelo - h * 0.3f, r.left + r.width * 0.32f, suelo), g, false)
            tazaLataNeon(Rect(r.left + r.width * 0.62f, suelo - h * 0.28f, r.left + r.width * 0.94f, suelo), g, true)
        }
        2 -> {
            // La caja de fruta de madera con la caja de herramientas encima, y al lado el soldador en su soporte.
            val caja = Rect(r.left, suelo - h * 0.4f, r.left + r.width * 0.7f, suelo)
            cajaFrutaNeon(caja, g)
            cajaHerramientasNeon(Rect(caja.left + caja.width * 0.04f, caja.top - h * 0.44f, caja.right + r.width * 0.02f, caja.top), g)
            soldadorNeon(Rect(r.left + r.width * 0.66f, suelo - h * 0.62f, r.right, suelo), g)
        }
        else -> {
            plantaBotellaNeon(Rect(r.left + r.width * 0.02f, suelo - h * 0.9f, r.left + r.width * 0.62f, suelo), g, s)
            pulverizadorNeon(Rect(r.left + r.width * 0.56f, suelo - h * 0.56f, r.left + r.width * 0.94f, suelo), g)
        }
    }
}

/** La sombra que algo echa en el fondo del casillero, corrida hacia donde no da la luz. */
private fun DrawScope.sombraFondoNeon(r: Rect, t: TonoNeon, redondo: Float = 3f) {
    val u = density
    val dx = (if (t.izq) 1f else -1f) * r.width * 0.12f
    drawRoundRect(t.s(0.38f), Offset(r.left + dx, r.top + r.height * 0.06f), Size(r.width, r.height * 0.94f), CornerRadius(redondo * u))
    contactoNeon(Offset(r.center.x, r.bottom), r.width * 1.1f, r.width * 0.08f + 3f * u, t, 0.55f)
}

/**
 * El televisor portátil de tubo: la caja de plástico gris con sus planos, la pantalla abombada con estática (sin imágenes), que
 * echa su luz fría; la rejilla del altavoz de ranuras, las dos perillas, el asa y las antenas en abanico.
 */
private fun DrawScope.televisorNeon(r: Rect, g: MarcoNeon) {
    val u = density; val w = g.w
    val t = g.t(r.center)
    val noche = g.a.noche
    // Las antenas, detrás.
    val base = Offset(r.left + r.width * 0.62f, r.top + 1f * u)
    listOf(Offset(-0.12f, -0.17f)).forEach { (dx, dy) ->
        val punta = Offset(base.x + w * dx, base.y + w * dy)
        drawLine(t.s(0.3f), base + Offset(2f * u, 2f * u), punta + Offset(2f * u, 2f * u), 1.4f * u)
        drawLine(t.c(ALUMINIO, 1.1f), base, punta, 1.4f * u)
        drawCircle(t.c(ALUMINIO, 1.2f), 1.6f * u, punta)
    }
    drawRoundRect(t.c(HIERRO), Offset(base.x - 4f * u, base.y - 2.4f * u), Size(8f * u, 4f * u), CornerRadius(1.5f * u))
    sombraFondoNeon(r, t, 5f)
    // La caja.
    val caja = redondoNeon(r, 5f * u)
    drawPath(caja, t.c(Color(0xFF6E6A78)))
    clipPath(caja) { planosNeon(r, t); drawRect(t.b(0.55f), r.topLeft, Size(r.width, 1.4f * u)) }
    // La pantalla: el marco hundido y el vidrio con estática.
    val p = Rect(r.left + r.width * 0.07f, r.top + r.height * 0.12f, r.left + r.width * 0.68f, r.bottom - r.height * 0.14f)
    drawRoundRect(t.c(Color(0xFF26242C)), Offset(p.left - 2f * u, p.top - 2f * u), Size(p.width + 4f * u, p.height + 4f * u), CornerRadius(6f * u))
    val vidrio = redondoNeon(p, 6f * u)
    val luzTv = Color(0xFFB8E4F0)
    if (noche) {
        // En los Sueños, más tarde, el televisor está apagado: el vidrio oscuro con el reflejo y el piloto rojo de reposo.
        drawPath(vidrio, t.c(Color(0xFF1E2430), 0.8f))
        clipPath(vidrio) {
            drawOval(t.b(0.35f, 1.3f), Offset(p.left + p.width * 0.08f, p.top + p.height * 0.06f), Size(p.width * 0.4f, p.height * 0.26f))
            drawPath(poligonoNeon(p.left + p.width * 0.55f, p.top, p.left + p.width * 0.7f, p.top, p.left + p.width * 0.4f, p.bottom, p.left + p.width * 0.25f, p.bottom), Color.White.copy(alpha = 0.05f))
        }
        drawCircle(Color(0xFFFF3A2A), 1.3f * u, Offset(r.left + r.width * 0.72f, r.bottom - r.height * 0.1f))
        mancha(Offset(r.left + r.width * 0.72f, r.bottom - r.height * 0.1f), r.width * 0.06f, Color(0xFFFF3A2A).copy(alpha = 0.4f))
    } else {
    drawPath(vidrio, mezcla(luzTv, Color(0xFF20283A), 0.45f))
    clipPath(vidrio) {
        // La estática: rayas cortas claras y oscuras, fijas por la semilla.
        for (j in 0 until 46) {
            val y = p.top + p.height * azar(6101, j)
            val x = p.left + p.width * azar(6102, j)
            val l = p.width * (0.06f + 0.2f * azar(6103, j))
            drawLine(if (j % 3 == 0) Color(0xFF14182A).copy(alpha = 0.6f) else luzTv.copy(alpha = 0.75f), Offset(x, y), Offset(x + l, y), (0.8f + 0.8f * azar(6104, j)) * u)
        }
        drawRect(Color.White.copy(alpha = 0.1f), p.topLeft, Size(p.width, p.height * 0.3f))
        drawOval(Color.White.copy(alpha = 0.16f), Offset(p.left + p.width * 0.1f, p.top + p.height * 0.08f), Size(p.width * 0.35f, p.height * 0.22f))
    }
    mancha(p.center, r.width * 0.75f, luzTv.copy(alpha = 0.22f))
    }
    // El altavoz de ranuras y las perillas.
    val col = r.left + r.width * 0.76f
    for (j in 0 until 5) drawRect(t.s(0.55f), Offset(col, r.top + r.height * (0.16f + 0.07f * j)), Size(r.width * 0.17f, 1.2f * u))
    // La perilla del canal y el dial chico de rayas.
    val c = Offset(col + r.width * 0.085f, r.top + r.height * 0.66f)
    drawCircle(t.s(0.45f), r.width * 0.06f, c + Offset(1.2f * u, 1.2f * u))
    drawCircle(t.c(Color(0xFF2A2830)), r.width * 0.06f, c)
    drawLine(t.c(ALUMINIO), c, Offset(c.x + r.width * 0.035f, c.y - r.width * 0.035f), 1.2f * u)
    drawCircle(t.b(0.6f), r.width * 0.018f, c - Offset(r.width * 0.02f, r.width * 0.02f))
    drawRect(t.c(Color(0xFF2A2830)), Offset(col, r.top + r.height * 0.82f), Size(r.width * 0.17f, r.height * 0.06f))
    for (j in 0 until 4) drawLine(t.c(ALUMINIO, 0.8f), Offset(col + r.width * (0.02f + 0.04f * j), r.top + r.height * 0.83f), Offset(col + r.width * (0.02f + 0.04f * j), r.top + r.height * 0.87f), 0.7f * u)
}

/** La caja de cartón abierta con los cables y un enchufe que asoman. */
private fun DrawScope.cajaCablesNeon(r: Rect, g: MarcoNeon, s: Int) {
    val u = density
    val t = g.t(r.center)
    // Los cables que asoman, detrás de la boca.
    listOf(Color(0xFF1C1A22), Color(0xFFE8E0D0), Color(0xFFC8322E)).forEachIndexed { j, col ->
        val p = Path().apply {
            moveTo(r.left + r.width * (0.2f + 0.25f * j), r.top + 2f * u)
            cubicTo(r.left + r.width * (0.1f + 0.3f * j), r.top - r.height * (0.4f + 0.3f * azar(s, j)), r.left + r.width * (0.5f + 0.2f * j), r.top - r.height * 0.6f, r.left + r.width * (0.6f + 0.15f * j), r.top + 1f * u)
        }
        drawPath(p, t.c(col), style = Stroke(1.8f * u))
    }
    sombraFondoNeon(r, t)
    drawRect(t.c(CARTON), r.topLeft, r.size)
    clipRect(r.left, r.top, r.right, r.bottom) { planosNeon(r, t) }
    // Las solapas abiertas.
    drawPath(poligonoNeon(r.left, r.top, r.left - r.width * 0.12f, r.top - r.height * 0.3f, r.left + r.width * 0.3f, r.top - r.height * 0.26f, r.left + r.width * 0.42f, r.top), t.c(mezcla(CARTON, HUESO, 0.25f)))
    drawPath(poligonoNeon(r.right, r.top, r.right + r.width * 0.05f, r.top - r.height * 0.34f, r.right - r.width * 0.36f, r.top - r.height * 0.3f, r.right - r.width * 0.42f, r.top), t.c(CARTON, 0.75f))
    drawRect(t.c(Color(0xFFCFC4A6)), Offset(r.left, r.top + r.height * 0.42f), Size(r.width, r.height * 0.12f))
    drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
}

/**
 * El termo de flores: el cuerpo de chapa esmaltada con su banda de flores pintadas, los hombros, el tapón con su vaso, el asa de
 * alambre al costado, los planos de luz y sombra, el brillo largo del esmalte y un golpe con el hierro que asoma.
 */
private fun DrawScope.termoNeon(r: Rect, g: MarcoNeon) {
    val u = density
    val t = g.t(r.center)
    sombraFondoNeon(r, t, 8f)
    val hombro = r.top + r.height * 0.2f
    val cuerpo = Path().apply {
        moveTo(r.left + r.width * 0.3f, r.top + r.height * 0.1f)
        lineTo(r.right - r.width * 0.3f, r.top + r.height * 0.1f)
        quadraticTo(r.right, r.top + r.height * 0.12f, r.right, hombro + r.height * 0.06f)
        lineTo(r.right, r.bottom - r.height * 0.04f)
        quadraticTo(r.right, r.bottom, r.right - r.width * 0.1f, r.bottom)
        lineTo(r.left + r.width * 0.1f, r.bottom)
        quadraticTo(r.left, r.bottom, r.left, r.bottom - r.height * 0.04f)
        lineTo(r.left, hombro + r.height * 0.06f)
        quadraticTo(r.left, r.top + r.height * 0.12f, r.left + r.width * 0.3f, r.top + r.height * 0.1f)
        close()
    }
    // El asa de alambre, detrás a la derecha.
    drawArc(t.c(ALUMINIO), -70f, 140f, false, Offset(r.right - r.width * 0.3f, r.top + r.height * 0.22f), Size(r.width * 0.6f, r.height * 0.4f), style = Stroke(1.6f * u))
    drawPath(cuerpo, t.c(Color(0xFFD9453A)))
    clipPath(cuerpo) {
        // La banda blanca con las flores.
        val b0 = r.top + r.height * 0.38f; val b1 = r.top + r.height * 0.72f
        drawRect(t.c(ESMALTE_NEON), Offset(r.left, b0), Size(r.width, b1 - b0))
        for (j in 0 until 3) {
            val c = Offset(r.left + r.width * (0.22f + 0.32f * j), b0 + (b1 - b0) * (0.35f + 0.3f * (j % 2)))
            val pr = r.width * 0.1f
            for (p in 0 until 5) {
                val an = p * 1.2566f
                drawCircle(t.c(if (j == 1) Color(0xFFE8A83A) else Color(0xFFE85A7A)), pr * 0.55f, Offset(c.x + pr * 0.7f * kotlin.math.cos(an), c.y + pr * 0.7f * kotlin.math.sin(an)))
            }
            drawCircle(t.c(Color(0xFFF0D040)), pr * 0.4f, c)
            drawLine(t.c(VERDE_PLANTA_NEON), Offset(c.x + pr, c.y + pr * 0.3f), Offset(c.x + pr * 2f, c.y + pr * 0.8f), 1.4f * u)
        }
        drawRect(t.c(Color(0xFF3A8A5A)), Offset(r.left, b0), Size(r.width, 1.4f * u))
        drawRect(t.c(Color(0xFF3A8A5A)), Offset(r.left, b1 - 1.4f * u), Size(r.width, 1.4f * u))
        planosNeon(Rect(r.left, r.top, r.right, r.bottom), t)
        // El brillo largo del esmalte.
        drawRect(t.b(0.7f, 1.3f), Offset(if (t.izq) r.left + r.width * 0.16f else r.right - r.width * 0.22f, hombro), Size(r.width * 0.06f, r.height * 0.7f))
        // El golpe con el hierro que asoma.
        drawOval(t.c(HIERRO), Offset(r.left + r.width * 0.6f, r.top + r.height * 0.82f), Size(r.width * 0.16f, r.height * 0.05f))
    }
    // El tapón y su vaso.
    val tapon = Rect(r.left + r.width * 0.24f, r.top, r.right - r.width * 0.24f, r.top + r.height * 0.11f)
    drawRoundRect(t.c(Color(0xFF7A1E22)), tapon.topLeft, tapon.size, CornerRadius(2f * u))
    clipRect(tapon.left, tapon.top, tapon.right, tapon.bottom) { planosNeon(tapon, t) }
    for (j in 0 until 4) drawLine(t.s(0.4f), Offset(tapon.left, tapon.top + tapon.height * (0.25f + 0.2f * j)), Offset(tapon.right, tapon.top + tapon.height * (0.25f + 0.2f * j)), 0.7f * u)
    drawRect(t.b(0.6f), tapon.topLeft, Size(tapon.width, 1f * u))
}

/**
 * Una taza de lata esmaltada (blanca con el borde azul, con el esmalte saltado en el borde, donde se golpea, y un roce a lo largo
 * del costado: nada en medio de la cara, que se leería como un ojo); [boca] abajo, una vuelta del revés.
 */
private fun DrawScope.tazaLataNeon(r: Rect, g: MarcoNeon, boca: Boolean) {
    val u = density
    val t = g.t(r.center)
    sombraFondoNeon(r, t)
    drawArc(t.c(ESMALTE_NEON), -80f, 160f, false, Offset(r.right - r.width * 0.25f, r.top + r.height * 0.2f), Size(r.width * 0.4f, r.height * 0.5f), style = Stroke(2.4f * u))
    val cuerpo = Path().apply {
        moveTo(r.left, r.top); lineTo(r.right - r.width * 0.12f, r.top)
        lineTo(r.right - r.width * 0.16f, r.bottom); lineTo(r.left + r.width * 0.04f, r.bottom); close()
    }
    drawPath(cuerpo, t.c(ESMALTE_NEON))
    clipPath(cuerpo) {
        planosNeon(r, t)
        val yb = if (boca) r.bottom - r.height * 0.12f else r.top
        drawRect(t.c(AZUL_ESMALTE_NEON), Offset(r.left, yb), Size(r.width, r.height * 0.12f))
        // El esmalte saltado en el borde azul (el hierro negro asoma, mordiendo el borde) y el roce largo del costado.
        val xs = r.left + r.width * (if (boca) 0.18f else 0.56f)
        val yr = if (boca) r.bottom else r.top
        val sy = if (boca) -1f else 1f
        drawPath(poligonoNeon(xs, yr, xs + r.width * 0.2f, yr, xs + r.width * 0.15f, yr + sy * r.height * 0.15f, xs + r.width * 0.06f, yr + sy * r.height * 0.19f, xs - r.width * 0.02f, yr + sy * r.height * 0.08f), t.c(Color(0xFF16141C)))
        drawLine(t.s(0.3f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.42f), Offset(r.left + r.width * 0.2f, r.bottom - r.height * 0.16f), 0.9f * u)
    }
    if (!boca) drawOval(t.c(Color(0xFF3A3040), 0.6f), Offset(r.left + 1f * u, r.top - 1.6f * u), Size(r.width * 0.84f - 2f * u, 3.2f * u))
}

/**
 * El soldador en su soporte: el muelle de alambre en espiral sobre su peana, el soldador metido en diagonal (el mango de
 * plástico, la caña de metal), la esponja amarilla y el carrete de estaño con su hilo.
 */
private fun DrawScope.soldadorNeon(r: Rect, g: MarcoNeon) {
    val u = density
    val t = g.t(r.center)
    val peana = Rect(r.left, r.bottom - r.height * 0.1f, r.right, r.bottom)
    contactoNeon(Offset(peana.center.x, peana.bottom), peana.width * 1.1f, 5f * u, t)
    drawRoundRect(t.c(HIERRO), peana.topLeft, peana.size, CornerRadius(2f * u))
    drawRect(t.b(0.5f), peana.topLeft, Size(peana.width, 1f * u))
    drawRect(t.c(Color(0xFFE6C23A)), Offset(peana.left + peana.width * 0.1f, peana.top - r.height * 0.05f), Size(peana.width * 0.3f, r.height * 0.06f))
    // El muelle en espiral, inclinado.
    val a = Offset(r.left + r.width * 0.55f, peana.top); val b = Offset(r.left + r.width * 0.25f, r.top + r.height * 0.35f)
    val vueltas = 9
    for (j in 0 until vueltas) {
        val f = j / vueltas.toFloat()
        val c = a + (b - a) * f
        val rr = r.width * (0.1f + 0.06f * f)
        rotate(-35f, c) { drawOval(t.c(ALUMINIO), Offset(c.x - rr, c.y - rr * 0.3f), Size(rr * 2, rr * 0.6f), style = Stroke(1f * u)) }
    }
    // El soldador, metido en el muelle.
    val punta = a + (b - a) * 0.2f
    val mango = Offset(r.left + r.width * 0.08f, r.top)
    drawLine(t.s(0.3f), punta + Offset(3f * u, 3f * u), mango + Offset(3f * u, 3f * u), 4f * u)
    drawLine(t.c(ALUMINIO, 1.1f), punta, punta + (mango - punta) * 0.55f, 2f * u)
    drawLine(t.c(Color(0xFF2A6A8A)), punta + (mango - punta) * 0.5f, mango, 5f * u, StrokeCap.Round)
    drawLine(t.b(0.6f), punta + (mango - punta) * 0.55f + Offset(-1.2f * u, -1.2f * u), mango + Offset(-1.2f * u, -1.2f * u), 1f * u, StrokeCap.Round)
    // El carrete de estaño.
    val cc = Offset(r.right - r.width * 0.2f, peana.top - r.width * 0.14f); val cr = r.width * 0.14f
    drawCircle(t.s(0.4f), cr, cc + Offset(2f * u, 2f * u))
    drawCircle(t.c(Color(0xFF3A3A48)), cr, cc)
    drawCircle(t.c(Color(0xFFB8BCC8), 1.1f), cr * 0.72f, cc)
    for (j in 1..3) drawCircle(t.s(0.25f), cr * (0.3f + 0.12f * j), cc, style = Stroke(0.6f * u))
    drawCircle(t.c(HIERRO), cr * 0.22f, cc)
    drawLine(t.c(Color(0xFFB8BCC8)), Offset(cc.x - cr * 0.7f, cc.y), Offset(r.left + r.width * 0.45f, peana.top - 2f * u), 0.8f * u)
}

/**
 * La caja de herramientas de chapa roja: el cuerpo con su pestaña, la tapa entreabierta con la bandeja, el asa, los cierres y el
 * mango de un destornillador que asoma; la pintura saltada en los cantos.
 */
private fun DrawScope.cajaHerramientasNeon(r: Rect, g: MarcoNeon) {
    val u = density
    val t = g.t(r.center)
    val cuerpo = Rect(r.left, r.top + r.height * 0.32f, r.right, r.bottom)
    sombraFondoNeon(cuerpo, t)
    // El destornillador que asoma por la tapa entreabierta.
    drawLine(t.c(ALUMINIO), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.3f), Offset(r.left + r.width * 0.12f, r.top - r.height * 0.05f), 1.4f * u)
    drawLine(t.c(Color(0xFFE6C23A)), Offset(r.left + r.width * 0.36f, r.top + r.height * 0.42f), Offset(r.left + r.width * 0.28f, r.top + r.height * 0.26f), 4.4f * u, StrokeCap.Round)
    // El asa.
    drawRoundRect(t.c(HIERRO), Offset(r.left + r.width * 0.32f, r.top), Size(r.width * 0.36f, r.height * 0.3f), CornerRadius(3f * u), style = Stroke(2.4f * u))
    drawRect(t.c(Color(0xFF1C1A22)), Offset(r.left + r.width * 0.38f, r.top - 1.2f * u), Size(r.width * 0.24f, 3.6f * u))
    // La tapa, a dos aguas, y el cuerpo.
    val tapa = poligonoNeon(r.left + r.width * 0.02f, cuerpo.top, r.left + r.width * 0.18f, r.top + r.height * 0.16f, r.right - r.width * 0.18f, r.top + r.height * 0.16f, r.right - r.width * 0.02f, cuerpo.top)
    drawPath(tapa, t.c(ROJO_CAJA_NEON, 1.1f))
    drawPath(tapa, t.b(0.5f), style = Stroke(0.8f * u))
    drawRect(t.c(ROJO_CAJA_NEON), cuerpo.topLeft, cuerpo.size)
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        planosNeon(cuerpo, t)
        drawRect(t.s(0.35f), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.18f), Size(cuerpo.width, 1.2f * u))
        drawRect(t.b(0.3f), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.18f + 1.2f * u), Size(cuerpo.width, 0.8f * u))
    }
    // Los cierres.
    listOf(0.12f, 0.8f).forEach { fx ->
        val c = Rect(cuerpo.left + cuerpo.width * fx, cuerpo.top - 1f * u, cuerpo.left + cuerpo.width * (fx + 0.08f), cuerpo.top + cuerpo.height * 0.3f)
        drawRect(t.c(ALUMINIO), c.topLeft, c.size)
        drawRect(t.b(0.6f), c.topLeft, Size(1f * u, c.height))
    }
    // La pintura saltada en los cantos.
    listOf(0.04f, 0.5f, 0.93f).forEach { fx ->
        val x = cuerpo.left + cuerpo.width * fx
        drawPath(poligonoNeon(x, cuerpo.bottom, x + cuerpo.width * 0.06f, cuerpo.bottom, x + cuerpo.width * 0.03f, cuerpo.bottom - cuerpo.height * 0.14f), t.c(ALUMINIO, 0.8f))
    }
    drawRect(t.b(0.5f), cuerpo.topLeft, Size(cuerpo.width, 1f * u))
}

/**
 * La caja de fruta de tablillas de madera clara, con sus listones, las cabezas de los clavos, el hueco entre tablas por donde se
 * ve lo oscuro de dentro y el canto rozado.
 */
private fun DrawScope.cajaFrutaNeon(r: Rect, g: MarcoNeon) {
    val u = density
    val t = g.t(r.center)
    sombraFondoNeon(r, t)
    drawRect(t.c(Color(0xFF1A1418)), r.topLeft, r.size)
    val tablas = 3
    for (j in 0 until tablas) {
        val y0 = r.top + r.height * j / tablas + r.height * 0.03f
        val y1 = r.top + r.height * (j + 1) / tablas - r.height * 0.05f
        val tb = Rect(r.left, y0, r.right, y1)
        drawRect(t.c(Color(0xFFC8A272)), tb.topLeft, tb.size)
        clipRect(tb.left, tb.top, tb.right, tb.bottom) { planosNeon(tb, t, 0.8f) }
        drawRect(t.b(0.5f), tb.topLeft, Size(tb.width, 1f * u))
        drawRect(t.s(0.35f), Offset(tb.left, tb.bottom - 1f * u), Size(tb.width, 1f * u))
        for (v in 0 until 3) drawLine(t.c(Color(0xFF8A6A44)).copy(alpha = 0.6f), Offset(tb.left + tb.width * (0.2f + 0.3f * v), tb.top + tb.height * 0.4f), Offset(tb.left + tb.width * (0.32f + 0.3f * v), tb.top + tb.height * 0.45f), 0.7f * u)
        listOf(0.06f, 0.94f).forEach { fx -> drawCircle(t.c(Color(0xFF3A3440)), 1.1f * u, Offset(tb.left + tb.width * fx, tb.center.y)) }
    }
    // Los listones de las esquinas.
    listOf(r.left, r.right - r.width * 0.08f).forEach { x ->
        drawRect(t.c(Color(0xFFB08A5A)), Offset(x, r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.b(0.4f), Offset(x, r.top), Size(1f * u, r.height))
    }
}

/**
 * La planta en una botella de plástico cortada: la mitad de abajo de la botella, transparente, con el agua y las raíces, y las
 * hojas largas y rígidas (lengua de suegra) que suben, con sus bandas claras.
 */
private fun DrawScope.plantaBotellaNeon(r: Rect, g: MarcoNeon, s: Int) {
    val u = density
    val t = g.t(r.center)
    val bot = Rect(r.left + r.width * 0.14f, r.bottom - r.height * 0.36f, r.right - r.width * 0.14f, r.bottom)
    sombraFondoNeon(Rect(bot.left, r.top + r.height * 0.15f, bot.right, r.bottom), t, 8f)
    // Las hojas: láminas largas en punta, con sus bandas y su filo.
    val n = 6
    for (j in 0 until n) {
        val bx = bot.left + bot.width * (0.18f + 0.64f * j / (n - 1))
        val alto = r.height * (0.62f + 0.3f * azar(6201 + s, j))
        val incl = r.width * 0.14f * (j - (n - 1) / 2f) / n + r.width * 0.04f * (azar(6202 + s, j) - 0.5f)
        val an = r.width * (0.07f + 0.03f * azar(6203 + s, j))
        val hoja = Path().apply {
            moveTo(bx - an / 2, bot.top + bot.height * 0.3f)
            cubicTo(bx - an * 0.7f + incl * 0.3f, bot.bottom - alto * 0.5f, bx - an * 0.3f + incl * 0.8f, bot.bottom - alto * 0.85f, bx + incl, bot.bottom - alto)
            cubicTo(bx + an * 0.3f + incl * 0.8f, bot.bottom - alto * 0.85f, bx + an * 0.7f + incl * 0.3f, bot.bottom - alto * 0.5f, bx + an / 2, bot.top + bot.height * 0.3f)
            close()
        }
        val tj = g.t(Offset(bx + incl * 0.5f, bot.bottom - alto * 0.6f))
        drawPath(hoja, tj.c(if (j % 2 == 0) VERDE_PLANTA_NEON else VERDE_PLANTA_OSC_NEON))
        clipPath(hoja) {
            for (b in 0 until 5) {
                val y = bot.bottom - alto * (0.3f + 0.13f * b)
                drawLine(tj.c(Color(0xFFB8D07A)).copy(alpha = 0.5f), Offset(bx - an, y), Offset(bx + an, y - an * 0.4f), 1.2f * u)
            }
            drawRect(tj.s(0.3f), Offset(bx + incl * 0.5f, bot.bottom - alto), Size(an, alto))
        }
        drawPath(hoja, tj.b(0.5f), style = Stroke(0.7f * u))
    }
    // La botella cortada: el plástico transparente, el agua con las raíces y el borde cortado.
    drawRoundRect(t.c(Color(0xFFA8D0C8)).copy(alpha = 0.35f), bot.topLeft, bot.size, CornerRadius(5f * u))
    val agua = Rect(bot.left + 1.5f * u, bot.top + bot.height * 0.35f, bot.right - 1.5f * u, bot.bottom - 1.5f * u)
    drawRoundRect(t.c(Color(0xFF5A9A8A)).copy(alpha = 0.55f), agua.topLeft, agua.size, CornerRadius(4f * u))
    for (j in 0 until 6) {
        val x = agua.left + agua.width * (0.15f + 0.14f * j)
        drawLine(t.c(Color(0xFFD8C8A0)).copy(alpha = 0.8f), Offset(x, agua.top), Offset(x + agua.width * 0.06f * (azar(6210 + s, j) - 0.5f), agua.bottom - agua.height * 0.15f * azar(6211 + s, j)), 0.8f * u)
    }
    drawLine(Color.White.copy(alpha = 0.5f), Offset(agua.left, agua.top), Offset(agua.right, agua.top), 0.8f * u)
    drawRoundRect(t.b(0.6f), bot.topLeft, bot.size, CornerRadius(5f * u), style = Stroke(1f * u))
    drawRect(t.b(0.8f, 1.3f), Offset(if (t.izq) bot.left + bot.width * 0.12f else bot.right - bot.width * 0.2f, bot.top + bot.height * 0.1f), Size(bot.width * 0.07f, bot.height * 0.75f))
    for (j in 0 until 3) drawLine(t.s(0.25f), Offset(bot.left, bot.bottom - bot.height * (0.2f + 0.1f * j)), Offset(bot.right, bot.bottom - bot.height * (0.2f + 0.1f * j)), 0.6f * u)
}

/** El pulverizador de plástico: la botella de color con su agua, el gatillo y la boquilla, y la etiqueta en blanco. */
private fun DrawScope.pulverizadorNeon(r: Rect, g: MarcoNeon) {
    val u = density
    val t = g.t(r.center)
    val bot = Rect(r.left + r.width * 0.12f, r.top + r.height * 0.32f, r.right - r.width * 0.12f, r.bottom)
    sombraFondoNeon(Rect(bot.left, r.top, bot.right, r.bottom), t, 6f)
    // La cabeza con el gatillo y la boquilla.
    val cab = Rect(bot.left + bot.width * 0.1f, r.top + r.height * 0.06f, bot.right, bot.top)
    drawRoundRect(t.c(Color(0xFFE8E2D6)), cab.topLeft, cab.size, CornerRadius(3f * u))
    drawRect(t.c(Color(0xFFE8E2D6)), Offset(cab.left - bot.width * 0.32f, cab.top + cab.height * 0.1f), Size(bot.width * 0.4f, cab.height * 0.3f))
    drawRect(t.c(HIERRO), Offset(cab.left - bot.width * 0.38f, cab.top + cab.height * 0.12f), Size(bot.width * 0.08f, cab.height * 0.26f))
    drawPath(poligonoNeon(cab.left, cab.top + cab.height * 0.45f, cab.left - bot.width * 0.12f, cab.bottom + cab.height * 0.2f, cab.left + bot.width * 0.05f, cab.bottom), t.c(Color(0xFF2F7E78)))
    clipRect(cab.left, cab.top, cab.right, cab.bottom) { planosNeon(cab, t) }
    // La botella con su agua.
    val cuerpo = redondoNeon(bot, 6f * u)
    drawPath(cuerpo, t.c(Color(0xFF3F9AC8)).copy(alpha = 0.75f))
    clipPath(cuerpo) {
        drawRect(t.c(Color(0xFF2A6A9A)), Offset(bot.left, bot.top + bot.height * 0.4f), Size(bot.width, bot.height * 0.6f))
        drawRect(t.c(PLASTICO, 1.2f), Offset(bot.left, bot.top + bot.height * 0.55f), Size(bot.width, bot.height * 0.25f))
        planosNeon(bot, t)
        drawRect(t.b(0.7f, 1.3f), Offset(if (t.izq) bot.left + bot.width * 0.14f else bot.right - bot.width * 0.24f, bot.top + bot.height * 0.08f), Size(bot.width * 0.09f, bot.height * 0.8f))
    }
    drawLine(Color.White.copy(alpha = 0.45f), Offset(bot.left + 2f * u, bot.top + bot.height * 0.4f), Offset(bot.right - 2f * u, bot.top + bot.height * 0.4f), 0.8f * u)
}
