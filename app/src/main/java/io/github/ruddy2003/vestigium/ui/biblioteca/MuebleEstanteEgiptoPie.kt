package io.github.ruddy2003.vestigium.ui.biblioteca

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
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.CARBON
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FIBRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.GRANO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.MARGA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAJA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ROJO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// El pie del almacén de la Tumba (MuebleEstanteEgipto): el costal, la medida de grano, la tinaja grande y la lámpara.

/** Lo del suelo, de atrás adelante: la tinaja grande de la derecha (cortada por el borde), la lámpara, la medida y el costal. */
internal fun DrawScope.pieEgipto(m: MesaEgipto) {
    val w = m.w
    val s = m.suelo
    tinajaGrandeEgipto(m, Offset(w * 0.95f, s + w * 0.15f))
    lamparaEgipto(m, s + w * 0.12f)
    medidaEgipto(m, Offset(w * 0.43f, s + w * 0.13f))
    costalEgipto(m, Offset(w * 0.11f, s + w * 0.16f))
}

/**
 * El costal de lino lleno de trigo, vencido contra el tronco y cortado por el borde: la trama, la franja azul tejida, los
 * pliegues del peso con su luz y su sombra, el cuello fruncido con sus orejas atado con soga, y el trigo que se ha caído.
 */
private fun DrawScope.costalEgipto(m: MesaEgipto, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val an = w * 0.3f; val al = w * 0.3f
    val c = Offset(pie.x, pie.y - al * 0.5f)
    val k = maxOf(m.k(c), if (a.oscuro) 0.06f else 0f); val f = m.f(c)
    contactoEgipto(Offset(pie.x + w * 0.03f, pie.y), an * 1.25f, w * 0.03f, a, 0.45f)
    val saco = Path().apply {
        moveTo(pie.x - an * 0.55f, pie.y)
        quadraticTo(pie.x - an * 0.66f, pie.y - al * 0.5f, pie.x - an * 0.2f, pie.y - al * 0.86f)
        quadraticTo(pie.x - an * 0.08f, pie.y - al * 0.98f, pie.x + an * 0.02f, pie.y - al * 0.88f)
        quadraticTo(pie.x + an * 0.5f, pie.y - al * 0.72f, pie.x + an * 0.55f, pie.y - al * 0.2f)
        quadraticTo(pie.x + an * 0.57f, pie.y, pie.x + an * 0.4f, pie.y)
        close()
    }
    val caja = Rect(pie.x - an * 0.62f, pie.y - al, pie.x + an * 0.56f, pie.y)
    drawPath(saco, a.c(LINO, k, f))
    clipPath(saco) {
        val trama = Path()
        var y = caja.top
        while (y < caja.bottom) { trama.moveTo(caja.left, y); trama.lineTo(caja.right, y + 2f * u); y += 2.8f * u }
        drawPath(trama, a.s(0.08f), style = Stroke(0.5f * u))
        val yb = pie.y - al * 0.44f
        drawPath(Path().apply { moveTo(caja.left, yb); quadraticTo(pie.x, yb + al * 0.08f, caja.right, yb - al * 0.03f) }, a.c(AZUL, k, f), style = Stroke(3f * u))
        drawPath(Path().apply { moveTo(caja.left, yb + 5f * u); quadraticTo(pie.x, yb + 5f * u + al * 0.08f, caja.right, yb + 5f * u - al * 0.03f) }, a.c(ROJO, k, f), style = Stroke(1.4f * u))
        // Los pliegues del peso: un plano de luz y uno de sombra a cada lado de cada pliegue.
        listOf(-0.22f to -0.32f, 0.08f to 0.2f).forEach { (x0, x1) ->
            val pl = Path().apply { moveTo(pie.x + an * x0, pie.y - al * 0.82f); quadraticTo(pie.x + an * (x0 + 0.12f), pie.y - al * 0.45f, pie.x + an * x1, pie.y - al * 0.05f) }
            drawPath(pl, a.s(0.28f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            drawPath(corridoEgipto(pl, -2f * u, 0f), a.b(0.3f, k), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
        cilindroEgipto(caja, a, k, 1.25f, m.der(pie.x), f)
        drawRect(a.s(0.2f), Offset(caja.left, pie.y - al * 0.1f), Size(caja.width, al * 0.1f))
        filoEgipto(caja, a, k, m.der(pie.x))
    }
    // El cuello fruncido con sus orejas y la soga.
    val cuello = Offset(pie.x - an * 0.09f, pie.y - al * 0.92f)
    val orejas = Path().apply {
        moveTo(cuello.x, cuello.y); quadraticTo(cuello.x - an * 0.2f, cuello.y - al * 0.16f, cuello.x - an * 0.25f, cuello.y - al * 0.07f)
        quadraticTo(cuello.x - an * 0.1f, cuello.y - al * 0.01f, cuello.x, cuello.y)
        quadraticTo(cuello.x + an * 0.12f, cuello.y - al * 0.2f, cuello.x + an * 0.21f, cuello.y - al * 0.12f)
        quadraticTo(cuello.x + an * 0.1f, cuello.y - al * 0.01f, cuello.x, cuello.y)
    }
    drawPath(orejas, a.c(mezcla(LINO, PAPIRO, 0.3f), k, f))
    drawPath(orejas, a.s(0.25f), style = Stroke(0.8f * u))
    drawLine(a.c(FIBRA, k, f), cuello + Offset(-4f * u, 1.4f * u), cuello + Offset(4f * u, 0f), 2.6f * u, cap = StrokeCap.Round)
    drawLine(a.b(0.4f, k), cuello + Offset(-3f * u, 0.6f * u), cuello + Offset(3f * u, -0.6f * u), 0.7f * u)
    // El trigo caído.
    for (j in 0 until 34) {
        val q = Offset(pie.x + w * (0.06f + 0.24f * azar(j, 91)), pie.y + w * (-0.012f + 0.04f * azar(j, 92)))
        drawOval(a.c(GRANO, m.k(q), f), Offset(q.x - 1.1f * u, q.y - 0.7f * u), Size(2.2f * u, 1.4f * u))
    }
}

/** La medida de madera para el grano, colmada: las duelas, los dos aros, el trigo en montón y lo que se derramó. */
private fun DrawScope.medidaEgipto(m: MesaEgipto, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val md = Rect(pie.x - w * 0.07f, pie.y - w * 0.13f, pie.x + w * 0.07f, pie.y)
    val k = maxOf(m.k(md.center), if (a.oscuro) 0.06f else 0f); val f = m.f(md.center)
    val der = m.der(pie.x)
    contactoEgipto(Offset(md.center.x + w * 0.02f, md.bottom), md.width * 1.4f, w * 0.022f, a, 0.45f)
    drawRect(a.c(ACACIA, k, f), md.topLeft, md.size)
    clipRect(md.left, md.top, md.right, md.bottom) {
        vetas(md, a.c(ACACIA_OSC, k).copy(alpha = 0.3f), 751, 0.8f, vertical = true, grosor = 0.6f)
        val duelas = Path()
        var x = md.left + md.width / 6
        while (x < md.right - 1f) { duelas.moveTo(x, md.top); duelas.lineTo(x, md.bottom); x += md.width / 6 }
        drawPath(duelas, a.c(ACACIA_OSC, k).copy(alpha = 0.5f), style = Stroke(0.8f * u))
        cilindroEgipto(md, a, k, 1.3f, der, f)
    }
    listOf(0.14f, 0.78f).forEach { t ->
        val y = md.top + md.height * t
        drawRect(a.c(ACACIA_OSC, k, f), Offset(md.left - 1f * u, y), Size(md.width + 2f * u, 3f * u))
        drawRect(a.b(0.4f, k), Offset(md.left - 1f * u, y), Size(md.width * 0.4f, 0.9f * u))
    }
    filoEgipto(md, a, k, der)
    // El trigo colmado, con su luz.
    drawOval(a.c(mezcla(ACACIA_OSC, PALMA_OSC, 0.3f), k, f), Offset(md.left, md.top - md.width * 0.06f), Size(md.width, md.width * 0.12f))
    val monte = Path().apply { addArc(Rect(md.left + 1f * u, md.top - md.width * 0.28f, md.right - 1f * u, md.top + md.width * 0.28f), 180f, 180f); close() }
    drawPath(monte, a.c(GRANO, k, f))
    clipPath(monte) {
        drawRect(a.b(0.3f, k), Offset(md.left, md.top - md.width * 0.3f), Size(md.width * 0.4f, md.width * 0.3f))
        drawRect(a.s(0.22f), Offset(md.left + md.width * 0.62f, md.top - md.width * 0.3f), Size(md.width * 0.4f, md.width * 0.3f))
    }
    for (j in 0 until 14) drawOval(a.c(mezcla(GRANO, PALMA_OSC, 0.3f), k, f), Offset(md.left + md.width * (0.08f + 0.065f * j), md.top - md.width * (0.03f + 0.18f * azar(j, 93))), Size(2.2f * u, 1.3f * u))
    for (j in 0 until 16) {
        val q = Offset(md.right + w * 0.05f * azar(j, 94), md.bottom - w * 0.014f * azar(j, 95))
        drawOval(a.c(GRANO, k, f), Offset(q.x - 1.1f * u, q.y - 0.7f * u), Size(2.2f * u, 1.4f * u))
    }
}

/**
 * La tinaja grande del grano, en el suelo sobre su rodete de paja y cortada por el borde derecho: la marga con la franja azul
 * y los filetes rojos, el plano de luz y el de sombra, el tapón de barro sellado y la sombra en el zócalo.
 */
private fun DrawScope.tinajaGrandeEgipto(m: MesaEgipto, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val alto = w * 0.34f; val r = alto * 0.38f
    val c = Offset(pie.x, pie.y - alto * 0.5f)
    val k = maxOf(m.k(c), if (a.oscuro) 0.06f else 0f); val f = m.f(c)
    val der = m.der(pie.x)
    // El rodete de paja.
    contactoEgipto(Offset(pie.x + r * 0.2f, pie.y + w * 0.004f), r * 2.6f, w * 0.03f, a, 0.45f)
    val rodete = Rect(pie.x - r * 0.9f, pie.y - w * 0.026f, pie.x + r * 0.9f, pie.y + w * 0.008f)
    drawOval(a.c(PAJA, k, f), rodete.topLeft, rodete.size)
    drawOval(a.s(0.25f), rodete.topLeft, rodete.size, style = Stroke(1f * u))
    val cuerpo = Path().apply {
        moveTo(pie.x - r * 0.42f, pie.y - w * 0.012f)
        cubicTo(pie.x - r * 1.2f, pie.y - alto * 0.25f, pie.x - r * 1.15f, pie.y - alto * 0.72f, pie.x - r * 0.48f, pie.y - alto * 0.88f)
        lineTo(pie.x - r * 0.38f, pie.y - alto * 0.95f); lineTo(pie.x + r * 0.38f, pie.y - alto * 0.95f); lineTo(pie.x + r * 0.48f, pie.y - alto * 0.88f)
        cubicTo(pie.x + r * 1.15f, pie.y - alto * 0.72f, pie.x + r * 1.2f, pie.y - alto * 0.25f, pie.x + r * 0.42f, pie.y - w * 0.012f)
        close()
    }
    val caja = Rect(pie.x - r * 1.15f, pie.y - alto * 0.95f, pie.x + r * 1.15f, pie.y)
    drawPath(cuerpo, a.c(MARGA, k, f))
    clipPath(cuerpo) {
        val y0 = pie.y - alto * 0.74f
        drawRect(a.c(AZUL, k, f), Offset(caja.left, y0), Size(caja.width, alto * 0.07f))
        drawRect(a.c(CARBON, k, f), Offset(caja.left, y0 - 2f * u), Size(caja.width, 1.2f * u))
        drawRect(a.c(ROJO, k, f), Offset(caja.left, y0 + alto * 0.07f + 1f * u), Size(caja.width, 2f * u))
        var x = caja.left + r * 0.1f
        while (x < caja.right) { drawOval(a.c(AZUL, k, f), Offset(x - r * 0.08f, y0 + alto * 0.07f + 3f * u), Size(r * 0.16f, r * 0.26f)); x += r * 0.22f }
        drawRect(a.c(ROJO, k, f), Offset(caja.left, pie.y - alto * 0.36f), Size(caja.width, 1.6f * u))
        drawRect(a.c(AZUL, k, f), Offset(caja.left, pie.y - alto * 0.33f), Size(caja.width, 2.6f * u))
        cilindroEgipto(caja, a, k, 1.35f, der, f)
        if (!a.oscuro) drawRect(a.b(0.12f), Offset(caja.right - caja.width * 0.1f, caja.top), Size(caja.width * 0.05f, caja.height))
    }
    destelloEgipto(Offset(pie.x - r * 0.6f, pie.y - alto * 0.6f), 1.8f * u, a, k)
    val boca = Rect(pie.x - r * 0.44f, pie.y - alto - r * 0.06f, pie.x + r * 0.44f, pie.y - alto * 0.93f)
    drawOval(a.c(mezcla(MARGA, PALMA_OSC, 0.2f), k, f), boca.topLeft, boca.size)
    val tapon = Path().apply { moveTo(boca.left, boca.center.y); cubicTo(boca.left, boca.top - r * 0.4f, boca.right, boca.top - r * 0.44f, boca.right, boca.center.y); close() }
    drawPath(tapon, a.c(mezcla(BARRO, PALMA_OSC, 0.25f), k, f))
    clipPath(tapon) { drawRect(a.b(0.3f, k), Offset(boca.left, boca.top - r * 0.5f), Size(boca.width * 0.4f, r * 0.6f)) }
    // El sello de barro en el tapón, con su cordel.
    drawOval(a.c(mezcla(BARRO, PAPIRO, 0.25f), k, f), Offset(pie.x - r * 0.16f, boca.top - r * 0.18f), Size(r * 0.32f, r * 0.24f))
    drawLine(a.c(FIBRA, k, f), Offset(boca.left, boca.center.y + 1f * u), Offset(pie.x - r * 0.2f, pie.y - alto * 0.8f), 1f * u)
}

/**
 * La lámpara del almacén, en el suelo: un pie de barro alto y hueco, ensanchado abajo y arriba, con dos rayas rojas, y encima
 * el cuenco de grasa con su pico pellizcado y la mecha de lino retorcido (de día, apagada y negra de hollín).
 */
private fun DrawScope.lamparaEgipto(m: MesaEgipto, piso: Float) {
    val u = density
    val w = m.w; val a = m.a
    val ll = m.llama
    val top = ll.y + w * 0.022f
    if (piso - top < 4f * u) return
    val k = if (a.lumbre) 0.85f else 1f
    contactoEgipto(Offset(ll.x + w * 0.016f, piso), w * 0.15f, w * 0.016f, a, 0.45f)
    val an = w * 0.055f
    val pieB = Path().apply {
        moveTo(ll.x - an, piso)
        quadraticTo(ll.x - an * 0.42f, piso - (piso - top) * 0.3f, ll.x - an * 0.38f, piso - (piso - top) * 0.55f)
        quadraticTo(ll.x - an * 0.48f, top + (piso - top) * 0.15f, ll.x - an * 0.85f, top)
        lineTo(ll.x + an * 0.85f, top)
        quadraticTo(ll.x + an * 0.48f, top + (piso - top) * 0.15f, ll.x + an * 0.38f, piso - (piso - top) * 0.55f)
        quadraticTo(ll.x + an * 0.42f, piso - (piso - top) * 0.3f, ll.x + an, piso)
        close()
    }
    val caja = Rect(ll.x - an, top, ll.x + an, piso)
    drawPath(pieB, a.c(BARRO, k))
    clipPath(pieB) {
        drawRect(a.c(ROJO, k), Offset(caja.left, top + (piso - top) * 0.2f), Size(caja.width, 1.8f * u))
        drawRect(a.c(CARBON, k), Offset(caja.left, top + (piso - top) * 0.2f + 2.6f * u), Size(caja.width, 1f * u))
        drawRect(a.c(ROJO, k), Offset(caja.left, piso - (piso - top) * 0.25f), Size(caja.width, 1.8f * u))
        if (a.lumbre) drawRect(LLAMA.copy(alpha = 0.35f), Offset(caja.left, top), Size(caja.width, (piso - top) * 0.22f))
        else cilindroEgipto(caja, a, k, 1.3f)
    }
    // El cuenco con el pico pellizcado a la izquierda.
    val cy = top + w * 0.004f
    val ac = w * 0.055f; val al = w * 0.02f
    val cuenco = Path().apply {
        moveTo(ll.x - ac - w * 0.012f, cy - al - w * 0.002f)
        lineTo(ll.x + ac, cy - al)
        cubicTo(ll.x + ac, cy - al * 0.2f, ll.x + ac * 0.5f, cy, ll.x, cy)
        cubicTo(ll.x - ac * 0.5f, cy, ll.x - ac, cy - al * 0.3f, ll.x - ac - w * 0.012f, cy - al - w * 0.002f)
        close()
    }
    drawPath(cuenco, a.c(mezcla(BARRO, PAPIRO, 0.12f), k))
    clipPath(cuenco) { cilindroEgipto(Rect(ll.x - ac, cy - al, ll.x + ac, cy), a, k, 1.3f) }
    drawOval(a.c(mezcla(BARRO, CARBON, 0.45f), k), Offset(ll.x - ac, cy - al - w * 0.006f), Size(ac * 2, w * 0.012f))
    drawArc(a.b(0.6f, k), 180f, 120f, false, Offset(ll.x - ac, cy - al - w * 0.006f), Size(ac * 2, w * 0.012f), style = Stroke(1f * u))
    // La mecha en el pico.
    drawLine(a.c(if (a.lumbre) LINO else PALMA_OSC, k), Offset(ll.x + w * 0.006f, cy - al), Offset(ll.x, ll.y + 1f * u), 1.8f * u, cap = StrokeCap.Round)
    if (!a.lumbre) drawCircle(a.c(CARBON, k), 1.4f * u, ll)
}

/** La llama de la mecha: el halo que tiñe lo de alrededor, el cuerpo y el corazón blanco; encima de todo, con su propio color. */
internal fun DrawScope.llamaEgipto(m: MesaEgipto) {
    val w = m.w; val a = m.a
    val p = m.llama
    val alto = w * (if (a.lunar) 0.032f else 0.046f)
    val fuerza = if (a.oscuro) 1f else 0.6f
    mancha(p - Offset(0f, alto * 0.4f), w * (if (a.lunar) 0.16f else 0.22f), LLAMA.copy(alpha = 0.34f * fuerza))
    mancha(p - Offset(0f, alto * 0.4f), w * 0.12f, LLAMA.copy(alpha = 0.5f * fuerza))
    drawPath(Path().apply {
        moveTo(p.x, p.y - alto)
        cubicTo(p.x + alto * 0.42f, p.y - alto * 0.3f, p.x + alto * 0.36f, p.y + alto * 0.2f, p.x, p.y + alto * 0.24f)
        cubicTo(p.x - alto * 0.36f, p.y + alto * 0.2f, p.x - alto * 0.42f, p.y - alto * 0.3f, p.x, p.y - alto)
        close()
    }, LLAMA)
    drawOval(Color(0xFFFFF4D8), Offset(p.x - alto * 0.14f, p.y - alto * 0.34f), Size(alto * 0.28f, alto * 0.5f))
}
