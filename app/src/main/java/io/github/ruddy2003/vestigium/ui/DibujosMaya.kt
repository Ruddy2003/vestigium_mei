package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

/**
 * Figuras del tema Mesoamérica que se repiten en varias superficies (emblema, trofeos, salón, libreta, campaña, cartas…):
 * el numeral de puntos y barras, el cartucho del glifo del día, la greca escalonada, la pirámide con su templo, la pluma
 * de quetzal, la cuenta de jade y el grano de cacao. Son estilizaciones propias, no copias de un monumento.
 */

/**
 * Un numeral maya de 0 a 19, centrado en [c] y de [alto] de alto: las barras (cinco cada una) abajo y los puntos (uno
 * cada uno) arriba, como en los códices; el cero es una concha. Fuera de 0..19 dibuja el residuo entre 20.
 */
internal fun DrawScope.numeralMaya(n: Int, c: Offset, alto: Float, color: Color) {
    val v = Math.floorMod(n, 20)
    val ancho = alto * 0.92f
    if (v == 0) {
        // La concha: un óvalo con su labio y tres rayas.
        drawOval(color, Offset(c.x - ancho / 2, c.y - alto * 0.24f), Size(ancho, alto * 0.48f), style = Stroke(alto * 0.07f))
        drawLine(color, Offset(c.x - ancho * 0.38f, c.y + alto * 0.04f), Offset(c.x + ancho * 0.38f, c.y + alto * 0.04f), alto * 0.06f)
        for (k in -1..1) drawLine(color, Offset(c.x + k * ancho * 0.18f, c.y - alto * 0.16f), Offset(c.x + k * ancho * 0.18f, c.y + alto * 0.02f), alto * 0.05f)
        return
    }
    val barras = v / 5; val puntos = v % 5
    val gruesa = alto * 0.15f; val hueco = alto * 0.07f; val rp = alto * 0.085f
    val total = barras * gruesa + (barras - 1).coerceAtLeast(0) * hueco + (if (puntos > 0) (if (barras > 0) hueco else 0f) + 2 * rp else 0f)
    var y = c.y + total / 2
    repeat(barras) {
        drawRoundRect(color, Offset(c.x - ancho / 2, y - gruesa), Size(ancho, gruesa), CornerRadius(gruesa * 0.35f))
        y -= gruesa + hueco
    }
    if (puntos > 0) {
        if (barras == 0) y += maxOf(hueco, 1f)
        val cy = y - rp
        val paso = ancho / 4.2f
        val x0 = c.x - paso * (puntos - 1) / 2f
        for (k in 0 until puntos) drawCircle(color, rp, Offset(x0 + k * paso, cy))
    }
}

/**
 * El cartucho de un signo de día del tzolk'in: el marco redondeado con su pie de tres lóbulos (el «pedestal» de los
 * glifos de día) y, dentro, una de cuatro estilizaciones según [signo]: la ventana en T del viento (Ik'), la cruz del
 * maíz maduro (K'an), el rostro del señor (Ajaw) y la flor de cuatro pétalos del sol (K'in). No son los 20 signos: es un
 * resumen que se alterna. [r] es la mitad del ancho del marco.
 */
internal fun DrawScope.cartuchoMaya(c: Offset, r: Float, marco: Color, relleno: Color, tinta: Color, signo: Int) {
    val lado = r * 2f; val alto = r * 1.8f
    val tl = Offset(c.x - r, c.y - alto * 0.56f)
    // Pie de tres lóbulos.
    for (k in -1..1) drawCircle(marco, r * 0.24f, Offset(c.x + k * r * 0.46f, tl.y + alto + r * 0.06f))
    drawRoundRect(relleno, tl, Size(lado, alto), CornerRadius(r * 0.42f))
    drawRoundRect(marco, tl, Size(lado, alto), CornerRadius(r * 0.42f), style = Stroke(r * 0.13f))
    val g = r * 0.12f
    val m = Offset(c.x, tl.y + alto / 2)
    when (Math.floorMod(signo, 4)) {
        0 -> {
            // Ik': la ventana en forma de T.
            drawPath(Path().apply {
                moveTo(m.x - r * 0.5f, m.y - r * 0.42f); lineTo(m.x + r * 0.5f, m.y - r * 0.42f); lineTo(m.x + r * 0.5f, m.y - r * 0.14f)
                lineTo(m.x + r * 0.16f, m.y - r * 0.14f); lineTo(m.x + r * 0.16f, m.y + r * 0.5f); lineTo(m.x - r * 0.16f, m.y + r * 0.5f)
                lineTo(m.x - r * 0.16f, m.y - r * 0.14f); lineTo(m.x - r * 0.5f, m.y - r * 0.14f); close()
            }, tinta)
        }
        1 -> {
            // K'an: la cruz del grano, con un punto en cada brazo.
            drawLine(tinta, Offset(m.x - r * 0.5f, m.y), Offset(m.x + r * 0.5f, m.y), g * 1.6f)
            drawLine(tinta, Offset(m.x, m.y - r * 0.5f), Offset(m.x, m.y + r * 0.5f), g * 1.6f)
            for (q in listOf(Offset(-0.3f, -0.3f), Offset(0.3f, -0.3f), Offset(-0.3f, 0.3f), Offset(0.3f, 0.3f))) drawCircle(tinta, g * 0.7f, m + q * r)
        }
        2 -> {
            // Ajaw: el rostro, dos ojos redondos y la boca abierta.
            drawCircle(tinta, r * 0.13f, m + Offset(-r * 0.26f, -r * 0.16f)); drawCircle(tinta, r * 0.13f, m + Offset(r * 0.26f, -r * 0.16f))
            drawOval(tinta, Offset(m.x - r * 0.2f, m.y + r * 0.14f), Size(r * 0.4f, r * 0.26f))
            drawArc(tinta, 200f, 140f, false, Offset(m.x - r * 0.55f, m.y - r * 0.6f), Size(r * 1.1f, r * 0.8f), style = Stroke(g))
        }
        else -> {
            // K'in: la flor de cuatro pétalos.
            for (k in 0 until 4) rotate(45f + 90f * k, m) { drawOval(tinta, Offset(m.x - r * 0.13f, m.y - r * 0.52f), Size(r * 0.26f, r * 0.42f)) }
            drawCircle(relleno, r * 0.12f, m); drawCircle(tinta, r * 0.12f, m, style = Stroke(g * 0.6f))
        }
    }
}

/** Greca escalonada (xicalcoliuhqui) de [x0] a [x1], entre [arriba] y [abajo]: tres gradas que suben y un gancho en espiral. */
internal fun DrawScope.grecaEscalonada(x0: Float, x1: Float, arriba: Float, abajo: Float, color: Color, grosor: Float) {
    val alto = abajo - arriba; val paso = alto * 1.4f
    val p = Path(); var x = x0
    while (x + paso <= x1 + 0.5f) {
        val g = alto / 4f; val s = paso / 8f
        p.moveTo(x, abajo); p.lineTo(x + s, abajo); p.lineTo(x + s, abajo - g); p.lineTo(x + 2 * s, abajo - g); p.lineTo(x + 2 * s, abajo - 2 * g)
        p.lineTo(x + 3 * s, abajo - 2 * g); p.lineTo(x + 3 * s, arriba); p.lineTo(x + 7 * s, arriba); p.lineTo(x + 7 * s, arriba + 2.6f * g)
        p.lineTo(x + 5 * s, arriba + 2.6f * g); p.lineTo(x + 5 * s, arriba + 1.3f * g); p.lineTo(x + 6 * s, arriba + 1.3f * g)
        x += maxOf(paso, 1f)
    }
    drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Square, join = StrokeJoin.Miter))
}

/**
 * Pirámide escalonada de [gradas] cuerpos con su escalinata al frente y el templo arriba, con su crestería; [base] es el
 * centro de la base. Los cuerpos se pintan con [piedra] y las aristas con [sombra].
 */
internal fun DrawScope.piramideMaya(base: Offset, ancho: Float, alto: Float, piedra: Color, sombra: Color, templo: Color, gradas: Int = 5) {
    val hCuerpo = alto * 0.72f / gradas
    for (k in 0 until gradas) {
        val a = ancho * (1f - k * 0.13f); val y = base.y - (k + 1) * hCuerpo
        drawRect(piedra, Offset(base.x - a / 2, y), Size(a, hCuerpo))
        drawLine(sombra, Offset(base.x - a / 2, y + hCuerpo * 0.35f), Offset(base.x + a / 2, y + hCuerpo * 0.35f), hCuerpo * 0.08f)
        drawRect(sombra, Offset(base.x - a / 2, y), Size(a, hCuerpo), style = Stroke(hCuerpo * 0.06f))
    }
    // Escalinata.
    val esc = ancho * 0.16f; val cima = base.y - gradas * hCuerpo
    drawRect(sombra.copy(alpha = sombra.alpha * 0.5f), Offset(base.x - esc / 2, cima), Size(esc, base.y - cima))
    var y = base.y
    while (y > cima) { drawLine(piedra, Offset(base.x - esc / 2, y), Offset(base.x + esc / 2, y), hCuerpo * 0.05f); y -= hCuerpo * 0.28f }
    // Templo y crestería.
    val tA = ancho * 0.3f; val tH = alto * 0.16f
    drawRect(templo, Offset(base.x - tA / 2, cima - tH), Size(tA, tH))
    drawRect(sombra, Offset(base.x - tA * 0.14f, cima - tH * 0.62f), Size(tA * 0.28f, tH * 0.62f))
    drawRect(templo, Offset(base.x - tA * 0.32f, cima - tH - alto * 0.12f), Size(tA * 0.64f, alto * 0.12f))
    for (k in -1..1) drawRect(sombra, Offset(base.x + k * tA * 0.18f - tA * 0.04f, cima - tH - alto * 0.1f), Size(tA * 0.08f, alto * 0.06f))
}

/** Pluma larga de quetzal que sale de [raiz] hacia [angulo] (grados; 0 = derecha), con su nervio y la punta curva. */
internal fun DrawScope.plumaQuetzal(raiz: Offset, largo: Float, angulo: Float, color: Color, nervio: Color) {
    rotate(angulo, raiz) {
        val p = Path().apply {
            moveTo(raiz.x, raiz.y)
            cubicTo(raiz.x + largo * 0.3f, raiz.y - largo * 0.13f, raiz.x + largo * 0.75f, raiz.y - largo * 0.12f, raiz.x + largo, raiz.y - largo * 0.04f)
            cubicTo(raiz.x + largo * 0.78f, raiz.y + largo * 0.04f, raiz.x + largo * 0.3f, raiz.y + largo * 0.07f, raiz.x, raiz.y)
            close()
        }
        drawPath(p, color)
        drawLine(nervio, raiz, Offset(raiz.x + largo * 0.94f, raiz.y - largo * 0.04f), largo * 0.018f, StrokeCap.Round)
    }
}

/** Una cuenta de jade, con su perforación y un brillo. */
internal fun DrawScope.cuentaJade(c: Offset, r: Float, jade: Color, brillo: Color) {
    drawCircle(jade, r, c)
    drawCircle(brillo.copy(alpha = brillo.alpha * 0.55f), r * 0.32f, c + Offset(-r * 0.36f, -r * 0.36f))
    drawCircle(Color.Black.copy(alpha = 0.25f * jade.alpha), r * 0.2f, c)
}

/**
 * El emblema del tema: el glifo del día de hoy en el tzolk'in. A la izquierda, su número (1 a 13) en puntos y barras
 * puestos de pie, como se escriben junto a un signo; a la derecha, el cartucho del signo con su pie. Detrás, el humo del
 * copal respira con [t].
 */
internal fun DrawScope.emblemaMaya(c: Offset, r: Float, marco: Color, relleno: Color, tinta: Color, numero: Color, t: Float) {
    val (n, signo) = Maya.tzolkinPartes(java.time.LocalDate.now())
    val humo = 0.5f + 0.5f * kotlin.math.sin(2 * Math.PI * t).toFloat()
    drawCircle(numero.copy(alpha = numero.alpha * (0.08f + 0.1f * humo)), r * (0.92f + 0.06f * humo), c)
    cartuchoMaya(Offset(c.x + r * 0.2f, c.y), r * 0.64f, marco, relleno, tinta, signo)
    val pn = Offset(c.x - r * 0.68f, c.y - r * 0.02f)
    rotate(90f, pn) { numeralMaya(n, pn, r * 0.5f, numero) }
}

/** Un grano de cacao: el óvalo con la raya del medio. */
internal fun DrawScope.granoCacao(c: Offset, r: Float, color: Color, raya: Color) {
    drawOval(color, Offset(c.x - r * 0.62f, c.y - r), Size(r * 1.24f, r * 2f))
    drawLine(raya, Offset(c.x, c.y - r * 0.8f), Offset(c.x, c.y + r * 0.8f), r * 0.14f, StrokeCap.Round)
}
