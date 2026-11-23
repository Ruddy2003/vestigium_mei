package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ALGODON
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.BARRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CACAO_AMARILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CACAO_ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.JADE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.JADE_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.SEMILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.VENADO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ZAPOTE_LUZ
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// Lo que hay en los huecos libres de las repisas del cuarto de guardar (MuebleEstanteMeso): cosas del escriba y de la casa,
// distintas según la repisa y el sitio que queda (de [DisposicionEstante.ocupado] al poste de la derecha).

/** Cuánto se agranda lo de los huecos (se dibuja a la medida de lo de arriba y se escala desde su pie). */
private const val ESCALA_HUECO_MESO = 1.5f

/**
 * El hueco libre de la repisa [i] ([r]), de [oc] (donde acaba el último volumen) al poste: hasta tres cosas, puestas desde el
 * poste hacia dentro mientras quepan, y en otro orden en cada repisa: el tambor de piel, el cajete trípode con granos de cacao
 * y dos mazorcas, la pila de mantas, la laja de moler con el azul maya, el huso con su malacate y el algodón, el hacha de piedra
 * verde sobre un paño, las navajas de obsidiana en su piel.
 */
internal fun DrawScope.huecoLibreMeso(m: MesaMeso, i: Int, r: Rect, oc: Float) {
    val w = m.w
    val e = ESCALA_HUECO_MESO
    val pie = r.bottom - w * 0.008f
    val tope = oc + w * 0.02f
    var x = m.posteD.left - w * 0.025f
    // Qué cosas van, en qué orden (de la del poste hacia dentro), según la repisa; cada una con su ancho y dónde va su pie.
    // Primero una alta (el tambor, las mantas o el cajete con cacao) y luego una baja, alternadas.
    val altas = listOf(5, 6, 0); val bajas = listOf(1, 2, 3, 4)
    val a0 = (i * i + i / 2) % 3
    val orden = listOf(altas[a0], bajas[i % 4], altas[(a0 + 1) % 3], bajas[(i + 1) % 4], altas[(a0 + 2) % 3], bajas[(i + 2) % 4], bajas[(i + 3) % 4])
    var puestas = 0
    for (cual in orden) {
        if (puestas >= 3) break
        val (ancho, centro) = when (cual) {
            0 -> w * 0.27f * e to 0.62f
            1 -> w * 0.17f * e to 0.5f
            2 -> w * 0.17f * e to 0.68f
            3 -> w * 0.12f * e to 0.5f
            4 -> w * 0.15f * e to 0.5f
            5 -> w * 0.16f to 0.5f
            else -> w * 0.2f to 0.5f
        }
        if (x - ancho < tope) continue
        val c = Offset(x - ancho * (1f - centro), pie)
        when (cual) {
            5 -> tamborMeso(m, c, r.height * 0.62f)
            6 -> mantasMeso(m, Rect(c.x - w * 0.09f, pie - r.height * 0.42f, c.x + w * 0.09f, pie), enMuro = false)
            else -> scale(e, e, c) {
                when (cual) {
                    0 -> cajeteCacaoMeso(m, c)
                    1 -> lajaAzulMeso(m, c)
                    2 -> husoMeso(m, c)
                    3 -> hachaMeso(m, c)
                    else -> navajasMeso(m, c)
                }
            }
        }
        x -= ancho + w * (0.03f + 0.03f * azar(i * 7 + cual, 940))
        puestas++
    }
}

/**
 * El tambor de madera, de pie: el cuerpo de chicozapote un poco entallado, con sus bandas talladas, el parche de piel de
 * venado tensado arriba y los cordeles que bajan en zigzag hasta el aro; su luz de cilindro y su sombra.
 */
private fun DrawScope.tamborMeso(m: MesaMeso, pie: Offset, alto: Float) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val an = w * 0.1f
    val e = an * 0.32f
    val top = pie.y - alto
    val cuerpo = Path().apply {
        moveTo(pie.x - an / 2, top + e / 2)
        cubicTo(pie.x - an * 0.4f, top + alto * 0.45f, pie.x - an * 0.4f, top + alto * 0.7f, pie.x - an * 0.47f, pie.y - e * 0.3f)
        quadraticTo(pie.x, pie.y + e * 0.4f, pie.x + an * 0.47f, pie.y - e * 0.3f)
        cubicTo(pie.x + an * 0.4f, top + alto * 0.7f, pie.x + an * 0.4f, top + alto * 0.45f, pie.x + an / 2, top + e / 2)
        close()
    }
    sombraHuecoMeso(m, cuerpo, pie)
    contactoMeso(pie, an * 1.1f, w * 0.012f, a, 0.45f)
    drawPath(cuerpo, a.c(MesoTinta.ZAPOTE, k, f))
    clipPath(cuerpo) {
        // Las bandas talladas: dos filetes con dientes entre ellos y el pie con su moldura.
        listOf(0.3f, 0.78f).forEach { t ->
            val y = top + alto * t
            drawRect(a.c(MesoTinta.ZAPOTE_OSC, k, f), Offset(pie.x - an, y), Size(an * 2, w * 0.006f))
            drawRect(a.b(0.3f, k), Offset(pie.x - an, y + w * 0.006f), Size(an * 2, 1f * u))
        }
        val dientes = Path()
        var x = pie.x - an * 0.55f; var n = 0
        val yd = top + alto * 0.3f + w * 0.008f
        while (x < pie.x + an * 0.55f && n < 20) { dientes.moveTo(x, yd); dientes.lineTo(x + an * 0.06f, yd + w * 0.014f); dientes.lineTo(x + an * 0.12f, yd); x += an * 0.12f; n++ }
        drawPath(dientes, a.c(CINABRIO, k, f), style = Stroke(1f * u))
        cilindroMeso(Rect(pie.x - an / 2, top, pie.x + an / 2, pie.y), a, k, 1.3f, m.der(pie.x, top), f)
    }
    drawPath(cuerpo, a.s(0.35f), style = Stroke(0.7f * u))
    // Los cordeles en zigzag, del parche al aro.
    val cordel = Path()
    val ya = top + e / 2; val yb = top + alto * 0.27f
    for (j in 0..6) {
        val x0 = pie.x - an * 0.42f + an * 0.84f * j / 6f
        cordel.moveTo(x0, ya); cordel.lineTo(x0 + an * 0.07f, yb)
        if (j < 6) { cordel.moveTo(x0 + an * 0.07f, yb); cordel.lineTo(x0 + an * 0.14f, ya) }
    }
    drawPath(cordel, a.c(MesoTinta.HENEQUEN, k, f), style = Stroke(0.9f * u))
    drawRect(a.c(MesoTinta.HENEQUEN_OSC, k, f), Offset(pie.x - an * 0.46f, yb - 0.6f * u), Size(an * 0.92f, 1.6f * u))
    // El parche, tenso, con su luz.
    drawOval(a.c(mezcla(VENADO, ESTUCO, 0.35f), k, f), Offset(pie.x - an / 2, top), Size(an, e))
    drawOval(a.s(0.25f), Offset(pie.x - an * 0.1f, top + e * 0.2f), Size(an * 0.55f, e * 0.6f))
    drawArc(a.b(0.5f, k), 160f, 110f, false, Offset(pie.x - an / 2, top), Size(an, e), style = Stroke(0.9f * u))
    drawOval(a.c(MesoTinta.ZAPOTE_OSC, k, f), Offset(pie.x - an / 2, top), Size(an, e), style = Stroke(1.2f * u))
}

/** La sombra de lo de un hueco en el fondo azul: corrida como la de los volúmenes. */
private fun DrawScope.sombraHuecoMeso(m: MesaMeso, p: Path, c: Offset) {
    val u = density
    val a = m.a
    if (!a.oscuro) drawPath(corridoMeso(p, 6f * u, -2f * u), a.s(0.24f))
    else if (m.k(c) > 0.12f) {
        val ll = m.lumbreDe(c)
        drawPath(corridoMeso(p, if (ll.x > c.x) -5f * u else 5f * u, -1.5f * u), MesoTinta.NOCHE_SOMBRA.copy(alpha = (0.9f * m.k(c)).coerceAtMost(0.45f)))
    }
}

/** El cajete trípode de engobe rojo, colmado de granos de cacao, con dos mazorcas recostadas a su lado. */
private fun DrawScope.cajeteCacaoMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val an = w * 0.13f; val al = w * 0.045f
    val boca = pie.y - al - w * 0.012f
    val k = m.k(pie); val f = m.f(pie)
    val cuenco = Path().apply {
        moveTo(pie.x - an * 0.5f, boca)
        cubicTo(pie.x - an * 0.48f, boca + al * 0.9f, pie.x - an * 0.3f, boca + al, pie.x, boca + al)
        cubicTo(pie.x + an * 0.3f, boca + al, pie.x + an * 0.48f, boca + al * 0.9f, pie.x + an * 0.5f, boca)
        close()
    }
    val granos = contornoMeso(Rect(pie.x - an * 0.46f, boca - al * 0.45f, pie.x + an * 0.46f, boca + al * 0.2f), 941, 0.08f)
    sombraHuecoMeso(m, Path().apply { addPath(cuenco); addPath(granos) }, pie)
    // Dos mazorcas recostadas a la izquierda.
    cacaoMeso(m, Offset(pie.x - an * 0.78f, pie.y - w * 0.018f), w * 0.075f, w * 0.036f, -4f, CACAO_AMARILLO, 942)
    cacaoMeso(m, Offset(pie.x - an * 0.62f, pie.y - w * 0.034f), w * 0.07f, w * 0.034f, -22f, CACAO_ROJO, 943)
    // Las tres patas.
    listOf(-0.3f, 0.3f, 0f).forEach { fx -> drawLine(a.c(BARRO_OSC, k, f), Offset(pie.x + an * fx, boca + al * 0.8f), Offset(pie.x + an * fx * 1.1f, pie.y), 2.4f * u, StrokeCap.Round) }
    contactoMeso(Offset(pie.x, pie.y), an * 1.1f, w * 0.01f, a, 0.4f)
    // Los granos, en montón, cada uno con su luz.
    drawPath(granos, a.c(SEMILLA, k, f))
    clipPath(granos) {
        val g = Path()
        for (j in 0 until 18) { val q = Offset(pie.x - an * 0.45f + an * 0.9f * azar(j, 944), boca - al * 0.4f + al * 0.5f * azar(j, 945)); g.addOval(Rect(q.x, q.y, q.x + w * 0.014f, q.y + w * 0.009f)) }
        drawPath(g, a.c(mezcla(SEMILLA, ZAPOTE_LUZ, 0.35f), k, f))
        drawPath(g, a.s(0.35f), style = Stroke(0.6f * u))
    }
    drawPath(cuenco, a.c(mezcla(CINABRIO, BARRO, 0.35f), k, f))
    clipPath(cuenco) {
        drawRect(a.c(BARRO, k, f), Offset(pie.x - an, boca + al * 0.55f), Size(an * 2, al))
        cilindroMeso(Rect(pie.x - an * 0.5f, boca, pie.x + an * 0.5f, boca + al), a, k, 1.2f, m.der(pie.x, boca), f)
    }
    drawLine(a.c(mezcla(CINABRIO, TINTA, 0.25f), k, f), Offset(pie.x - an * 0.5f, boca), Offset(pie.x + an * 0.5f, boca), 1.8f * u)
    drawLine(a.b(0.4f, k), Offset(pie.x - an * 0.48f, boca - 0.8f * u), Offset(pie.x + an * 0.2f, boca - 0.8f * u), 0.7f * u)
}

/** La laja de moler pigmento: la piedra plana con su mano, el montón de azul maya molido y una pella de azul ya amasada. */
private fun DrawScope.lajaAzulMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val laja = Rect(pie.x - w * 0.08f, pie.y - w * 0.022f, pie.x + w * 0.08f, pie.y)
    val forma = Path().apply { addRoundRect(RoundRect(laja, CornerRadius(w * 0.006f))) }
    val monte = Path().apply { moveTo(pie.x - w * 0.05f, laja.top + 0.5f * u); quadraticTo(pie.x - w * 0.015f, laja.top - w * 0.03f, pie.x + w * 0.02f, laja.top + 0.5f * u); close() }
    sombraHuecoMeso(m, Path().apply { addPath(forma); addPath(monte) }, pie)
    contactoMeso(Offset(pie.x, pie.y), w * 0.17f, w * 0.01f, a, 0.4f)
    drawPath(forma, a.c(mezcla(CALIZA, CALIZA_OSC, 0.4f), k, f))
    drawRect(a.c(mezcla(CALIZA, ESTUCO, 0.3f), k, f), laja.topLeft, Size(laja.width, w * 0.006f))
    drawRect(a.s(0.25f), Offset(laja.left, laja.bottom - w * 0.008f), Size(laja.width, w * 0.008f))
    // El azul molido y la mancha que dejó en la laja.
    val azul = Color(0xFF3E9DB8)
    drawOval(a.c(azul, k, f).copy(alpha = 0.5f), Offset(pie.x - w * 0.07f, laja.top - 0.4f * u), Size(w * 0.1f, w * 0.008f))
    drawPath(monte, a.c(azul, k, f))
    drawPath(bordeMeso(monte, w * 0.006f, 0f), a.b(0.4f, k))
    drawPath(bordeMeso(monte, -w * 0.008f, 0f), a.s(0.25f))
    // La pella ya amasada y la mano de moler.
    drawCircle(a.c(mezcla(azul, TINTA, 0.2f), k, f), w * 0.011f, Offset(pie.x + w * 0.05f, laja.top - w * 0.009f))
    drawCircle(a.b(0.4f, k), w * 0.003f, Offset(pie.x + w * 0.046f, laja.top - w * 0.013f))
    val mano = Rect(pie.x + w * 0.005f, laja.top - w * 0.012f, pie.x + w * 0.075f, laja.top + 0.5f * u)
    rotate(-8f, mano.center) {
        drawRoundRect(a.c(CALIZA, k, f), mano.topLeft, mano.size, CornerRadius(mano.height / 2))
        drawRect(a.b(0.35f, k), Offset(mano.left + mano.height * 0.4f, mano.top + mano.height * 0.15f), Size(mano.width - mano.height * 0.8f, mano.height * 0.2f))
        drawRect(a.s(0.25f), Offset(mano.left + mano.height * 0.3f, mano.bottom - mano.height * 0.35f), Size(mano.width - mano.height * 0.6f, mano.height * 0.3f))
        drawOval(a.c(azul, k, f).copy(alpha = 0.7f), Offset(mano.left + mano.height * 0.3f, mano.bottom - mano.height * 0.35f), Size(mano.width * 0.35f, mano.height * 0.3f))
    }
}

/** El huso con su malacate de barro, girando en un cajete chico, con el hilo devanado y un copo de algodón al lado. */
private fun DrawScope.husoMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val an = w * 0.065f; val al = w * 0.03f
    val boca = pie.y - al
    val cuenco = Path().apply {
        moveTo(pie.x - an * 0.5f, boca)
        cubicTo(pie.x - an * 0.48f, pie.y, pie.x + an * 0.48f, pie.y, pie.x + an * 0.5f, boca)
        close()
    }
    val copo = contornoMeso(Rect(pie.x - an * 1.6f, pie.y - w * 0.028f, pie.x - an * 0.55f, pie.y + 0.5f * u), 951, 0.3f)
    val p0 = Offset(pie.x + an * 0.05f, boca + al * 0.3f); val p1 = Offset(pie.x + an * 0.5f, boca - w * 0.1f)
    sombraHuecoMeso(m, Path().apply { addPath(cuenco); addPath(copo); addPath(poligonoMeso(p0.x - 1f, p0.y, p1.x - 1f, p1.y, p1.x + 2f, p1.y, p0.x + 2f, p0.y)) }, pie)
    contactoMeso(Offset(pie.x - an * 0.4f, pie.y), an * 2.4f, w * 0.009f, a, 0.4f)
    // El copo de algodón, esponjado.
    drawPath(copo, a.c(ALGODON, k, f))
    drawPath(bordeMeso(copo, -w * 0.006f, -w * 0.005f), a.s(0.2f))
    drawPath(bordeMeso(copo, w * 0.004f, w * 0.004f), a.b(0.5f, k))
    // El huso: la vara con el hilo devanado y el malacate abajo.
    drawLine(a.c(mezcla(ZAPOTE_LUZ, ESTUCO, 0.2f), k, f), p0, p1, 1.4f * u, StrokeCap.Round)
    val hilo = Path()
    for (j in 0 until 7) { val t = 0.25f + 0.07f * j; val q = p0 + (p1 - p0) * t; hilo.moveTo(q.x - 2.4f * u, q.y + 0.6f * u); hilo.lineTo(q.x + 2.4f * u, q.y - 0.6f * u) }
    drawPath(hilo, a.c(ALGODON, k, f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
    drawLine(a.c(ALGODON, k, f), p0 + (p1 - p0) * 0.7f, Offset(pie.x - an * 0.9f, pie.y - w * 0.02f), 0.6f * u)
    val mal = p0 + (p1 - p0) * 0.12f
    drawOval(a.c(BARRO, k, f), Offset(mal.x - w * 0.012f, mal.y - w * 0.005f), Size(w * 0.024f, w * 0.01f))
    drawOval(a.b(0.4f, k), Offset(mal.x - w * 0.009f, mal.y - w * 0.004f), Size(w * 0.012f, w * 0.003f))
    // El cajete chico.
    drawPath(cuenco, a.c(BARRO, k, f))
    clipPath(cuenco) { cilindroMeso(Rect(pie.x - an * 0.5f, boca, pie.x + an * 0.5f, pie.y), a, k, 1.2f, m.der(pie.x, boca), f) }
    drawLine(a.c(CINABRIO, k, f), Offset(pie.x - an * 0.5f, boca), Offset(pie.x + an * 0.5f, boca), 1.6f * u)
}

/** El hacha de piedra verde pulida, sobre un paño de algodón doblado: el filo, el lustre y el paño con su franja. */
private fun DrawScope.hachaMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val pano = Rect(pie.x - w * 0.055f, pie.y - w * 0.014f, pie.x + w * 0.055f, pie.y)
    val hacha = Path().apply {
        moveTo(pie.x - w * 0.045f, pie.y - w * 0.022f)
        quadraticTo(pie.x - w * 0.05f, pie.y - w * 0.038f, pie.x - w * 0.03f, pie.y - w * 0.04f)
        lineTo(pie.x + w * 0.04f, pie.y - w * 0.034f)
        quadraticTo(pie.x + w * 0.05f, pie.y - w * 0.026f, pie.x + w * 0.04f, pie.y - w * 0.016f)
        lineTo(pie.x - w * 0.03f, pie.y - w * 0.013f)
        close()
    }
    sombraHuecoMeso(m, Path().apply { addRect(pano); addPath(hacha) }, pie)
    contactoMeso(Offset(pie.x, pie.y), w * 0.12f, w * 0.009f, a, 0.4f)
    drawRect(a.c(ALGODON, k, f), pano.topLeft, pano.size)
    drawRect(a.c(CINABRIO, k, f).copy(alpha = 0.8f), Offset(pano.left, pano.top + pano.height * 0.45f), Size(pano.width, pano.height * 0.16f))
    drawRect(a.s(0.25f), Offset(pano.left, pano.bottom - pano.height * 0.3f), Size(pano.width, pano.height * 0.3f))
    drawRect(a.b(0.35f, k), pano.topLeft, Size(pano.width, 0.8f * u))
    drawPath(hacha, a.c(JADE, k, f))
    clipPath(hacha) {
        drawRect(a.c(JADE_LUZ, k, f).copy(alpha = 0.45f), Offset(pie.x - w * 0.05f, pie.y - w * 0.04f), Size(w * 0.1f, w * 0.008f))
        drawRect(a.s(0.3f), Offset(pie.x - w * 0.05f, pie.y - w * 0.022f), Size(w * 0.1f, w * 0.01f))
        // Las vetas de la piedra.
        drawLine(a.c(mezcla(JADE, TINTA, 0.4f), k, f).copy(alpha = 0.5f), Offset(pie.x - w * 0.03f, pie.y - w * 0.035f), Offset(pie.x + w * 0.02f, pie.y - w * 0.02f), 0.6f * u)
    }
    destelloMeso(Offset(pie.x - w * 0.01f, pie.y - w * 0.034f), 1f * u, a, k)
    drawPath(hacha, a.s(0.3f), style = Stroke(0.6f * u))
}

/** Las navajas de obsidiana, cuatro, en su envoltorio de piel de venado abierto: negras, con su filo de luz. */
private fun DrawScope.navajasMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val piel = contornoMeso(Rect(pie.x - w * 0.07f, pie.y - w * 0.016f, pie.x + w * 0.07f, pie.y + 0.4f * u), 961, 0.12f)
    sombraHuecoMeso(m, piel, pie)
    drawPath(piel, a.c(VENADO, k, f))
    drawPath(bordeMeso(piel, 0f, -1.4f * u), a.s(0.3f))
    for (j in 0 until 4) {
        val x = pie.x - w * 0.045f + w * 0.026f * j
        val navaja = poligonoMeso(x, pie.y - w * 0.006f, x + w * 0.004f, pie.y - w * 0.014f, x + w * 0.05f, pie.y - w * (0.016f + 0.004f * azar(j, 962)), x + w * 0.044f, pie.y - w * 0.006f)
        rotate(-8f + 6f * azar(j, 963), Offset(x, pie.y)) {
            drawPath(navaja, a.c(Color(0xFF1C1A22), k, f))
            drawLine(a.b(0.6f, k), Offset(x + w * 0.006f, pie.y - w * 0.013f), Offset(x + w * 0.046f, pie.y - w * 0.015f), 0.6f * u)
            drawLine(Color(0xFF4A5A6E).copy(alpha = 0.6f), Offset(x + w * 0.004f, pie.y - w * 0.009f), Offset(x + w * 0.04f, pie.y - w * 0.01f), 0.6f * u)
        }
    }
}
