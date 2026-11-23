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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ACHIOTE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ALGODON
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ANIL
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.BEJUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.BEJUCO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CACAO_AMARILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CACAO_GRANATE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CACAO_ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CAREY
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CAREY_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.HENEQUEN
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.HENEQUEN_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.MAIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.MAIZ_ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.OCOTE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.PULPA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.SEMILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TOTOMOXTLE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// Lo de arriba del cuarto de guardar (MuebleEstanteMeso): el bodegón sobre el remate (el canasto de cacao, las mantas, el
// carey con su asta, el copal y el ocote) y lo colgado de la viga (las mazorcas de maíz y las madejas de algodón).

// ---------------------------------------------------------------- una mazorca de cacao

/** La silueta de una mazorca de cacao de [largo] y [ancho], con el pezón en (0, 0) y la punta hacia +x. */
private fun cacaoFormaMeso(largo: Float, ancho: Float): Path = Path().apply {
    moveTo(0f, 0f)
    cubicTo(0f, -ancho * 0.62f, largo * 0.62f, -ancho * 0.66f, largo, -ancho * 0.04f)
    cubicTo(largo * 0.64f, ancho * 0.66f, 0f, ancho * 0.6f, 0f, 0f)
    close()
}

/**
 * Una mazorca de cacao madura en [c] (su centro), girada [giro] grados: el cuerpo de [col] con sus cinco costillas que van del
 * pezón a la punta (cada una con su surco y su lomo de luz), el plano de luz arriba, la media luna de sombra abajo, un brillo,
 * las verrugas y el pezón del tallo. Con [abierta], partida a lo largo: la pulpa blanca con sus semillas en hilera.
 */
internal fun DrawScope.cacaoMeso(m: MesaMeso, c: Offset, largo: Float, ancho: Float, giro: Float, col: Color, semilla: Int, abierta: Boolean = false) {
    val u = density
    val a = m.a
    val k = m.k(c); val f = m.f(c)
    rotate(giro, c) {
        translate(c.x - largo * 0.5f, c.y) {
            val forma = cacaoFormaMeso(largo, ancho)
            drawPath(forma, a.c(col, k, f))
            clipPath(forma) {
                // Las costillas: surcos de sombra y lomos de luz que se juntan en la punta.
                val surcos = Path(); val lomos = Path()
                for (j in 0 until 5) {
                    val y = ancho * (-0.42f + 0.21f * j)
                    surcos.moveTo(largo * 0.03f, y * 0.7f); surcos.cubicTo(largo * 0.3f, y * 1.12f, largo * 0.7f, y * 0.9f, largo, -ancho * 0.04f)
                    lomos.moveTo(largo * 0.03f, y * 0.7f - ancho * 0.07f); lomos.cubicTo(largo * 0.3f, y * 1.12f - ancho * 0.07f, largo * 0.7f, y * 0.9f - ancho * 0.05f, largo, -ancho * 0.04f)
                }
                drawPath(surcos, a.s(0.3f), style = Stroke(1f * u))
                drawPath(lomos, a.b(0.24f, k), style = Stroke(0.8f * u))
                // El volumen: el plano de luz de arriba, la sombra de abajo y el brillo.
                drawRect(a.b(0.14f, k), Offset(0f, -ancho), Size(largo, ancho * 0.6f))
                drawPath(bordeMeso(forma, 0f, -ancho * 0.3f), a.s(0.3f))
                drawPath(bordeMeso(forma, -largo * 0.06f, -ancho * 0.12f), a.s(0.2f))
                drawOval(a.b(0.4f, k), Offset(largo * 0.28f, -ancho * 0.36f), Size(largo * 0.3f, ancho * 0.12f))
                // Las verrugas, en un solo camino.
                val verrugas = Path()
                for (j in 0 until 9) { val q = Offset(largo * (0.15f + 0.7f * azar(semilla, j)), ancho * (azar(semilla, j + 20) - 0.5f) * 0.7f); verrugas.addOval(Rect(q.x - 0.7f * u, q.y - 0.5f * u, q.x + 0.7f * u, q.y + 0.5f * u)) }
                drawPath(verrugas, a.s(0.22f))
            }
            if (abierta) {
                // Partida: la cáscara gruesa y la pulpa blanca con las semillas.
                val pulpa = Path().apply { addOval(Rect(largo * 0.1f, -ancho * 0.3f, largo * 0.86f, ancho * 0.2f)) }
                drawPath(pulpa, a.c(mezcla(col, TINTA, 0.25f), k, f), style = Stroke(1.6f * u))
                drawPath(pulpa, a.c(PULPA, k, f))
                clipPath(pulpa) {
                    for (j in 0 until 6) {
                        val q = Offset(largo * (0.2f + 0.11f * j), -ancho * 0.05f + (if (j % 2 == 0) -1f else 1f) * ancho * 0.08f)
                        drawOval(a.c(SEMILLA, k, f), Offset(q.x - largo * 0.045f, q.y - ancho * 0.07f), Size(largo * 0.09f, ancho * 0.14f))
                        drawOval(a.b(0.4f, k), Offset(q.x - largo * 0.03f, q.y - ancho * 0.06f), Size(largo * 0.04f, ancho * 0.04f))
                    }
                    drawRect(a.s(0.2f), Offset(0f, ancho * 0.05f), Size(largo, ancho * 0.2f))
                }
            }
            drawPath(forma, a.s(0.35f), style = Stroke(0.7f * u))
            filoNichoMeso(m, forma, c, ancho * 0.12f, giro)
            // El pezón del tallo.
            drawRoundRect(a.c(BEJUCO_OSC, k, f), Offset(-ancho * 0.16f, -ancho * 0.07f), Size(ancho * 0.2f, ancho * 0.14f), CornerRadius(ancho * 0.05f))
        }
    }
}

// ---------------------------------------------------------------- el bodegón sobre el remate

/**
 * El bodegón de arriba, de atrás hacia delante: el ocote apoyado, el carey con su asta, las mantas, el canasto, el vaso
 * pintado (delante, entre el canasto y las mantas), el copal.
 */
internal fun DrawScope.bodegonMeso(m: MesaMeso) {
    val w = m.w
    ocoteMeso(m, Offset(w * 0.935f, m.apoyo))
    careyMeso(m, Offset(w * 0.672f, m.apoyo))
    mantasMeso(m, Rect(w * 0.44f, m.apoyo - w * 0.1f, w * 0.65f, m.apoyo))
    canastoMeso(m, Offset(w * 0.3f, m.apoyo))
    vasoPintadoMeso(m, Offset(w * 0.478f, m.apoyo + w * 0.002f), w * 0.122f)
    copalMeso(m, Offset(w * 0.81f, m.apoyo))
    // Dos mazorcas caídas delante, en el remate.
    cacaoMeso(m, Offset(w * 0.075f, m.apoyo - w * 0.016f), w * 0.085f, w * 0.042f, -6f, CACAO_AMARILLO, 811)
    contactoMeso(Offset(w * 0.08f, m.apoyo + w * 0.004f), w * 0.08f, w * 0.01f, m.a, 0.35f)
    cacaoMeso(m, Offset(w * 0.728f, m.apoyo - w * 0.015f), w * 0.075f, w * 0.038f, 172f, CACAO_GRANATE, 812)
    contactoMeso(Offset(w * 0.728f, m.apoyo + w * 0.004f), w * 0.07f, w * 0.009f, m.a, 0.35f)
}

/**
 * El vaso pintado para el cacao, la pieza más fina del cuarto: un cilindro de barro fino, apenas abierto de boca, con engobe
 * crema bruñido; arriba, la banda roja entre filetes negros con una hilera de dientes crema; en medio, el campo de achiote con
 * franjas en diagonal (sin ningún signo); abajo, la banda negra del pie con su filete rojo; su luz de cilindro, el lustre, la
 * boca con lo hondo de dentro y su sombra corrida en lo de atrás.
 */
private fun DrawScope.vasoPintadoMeso(m: MesaMeso, pie: Offset, alto: Float) {
    val u = density
    val w = m.w; val a = m.a
    val an = alto * 0.56f
    val top = pie.y - alto
    val c = Offset(pie.x, top + alto * 0.5f)
    val k = m.k(c); val f = m.f(c)
    val der = m.der(c.x, c.y)
    val forma = Path().apply {
        moveTo(pie.x - an * 0.56f, top)
        lineTo(pie.x + an * 0.56f, top)
        cubicTo(pie.x + an * 0.5f, top + alto * 0.3f, pie.x + an * 0.5f, top + alto * 0.8f, pie.x + an * 0.47f, pie.y - alto * 0.02f)
        quadraticTo(pie.x, pie.y + alto * 0.03f, pie.x - an * 0.47f, pie.y - alto * 0.02f)
        cubicTo(pie.x - an * 0.5f, top + alto * 0.8f, pie.x - an * 0.5f, top + alto * 0.3f, pie.x - an * 0.56f, top)
        close()
    }
    // Su sombra en lo de atrás: de día, corrida a la derecha; de noche, contraria a la lumbre.
    if (!a.oscuro) drawPath(corridoMeso(forma, w * 0.014f, w * 0.004f), a.s(0.3f))
    else if (k > 0.1f) {
        val ll = m.lumbreDe(c)
        drawPath(corridoMeso(forma, if (ll.x > c.x) -w * 0.012f else w * 0.012f, w * 0.004f), MesoTinta.NOCHE_SOMBRA.copy(alpha = (0.8f * k).coerceAtMost(0.4f)))
    }
    contactoMeso(Offset(pie.x, pie.y), an * 1.2f, w * 0.01f, a, 0.5f)
    val crema = mezcla(ESTUCO, MesoTinta.AMATE, 0.4f)
    drawPath(forma, a.c(crema, k, f))
    clipPath(forma) {
        val x0 = pie.x - an; val x1 = pie.x + an
        // La banda roja de arriba, entre sus filetes, con los dientes crema.
        drawRect(a.c(CINABRIO, k, f), Offset(x0, top + alto * 0.07f), Size(an * 2, alto * 0.2f))
        drawRect(a.c(TINTA, k * 0.6f, f), Offset(x0, top + alto * 0.05f), Size(an * 2, alto * 0.025f))
        drawRect(a.c(TINTA, k * 0.6f, f), Offset(x0, top + alto * 0.27f), Size(an * 2, alto * 0.025f))
        val dientes = Path()
        var x = x0; var n = 0
        val paso = an * 0.2f
        while (x < x1 && n < 20) { dientes.moveTo(x, top + alto * 0.27f); dientes.lineTo(x + paso * 0.5f, top + alto * 0.12f); dientes.lineTo(x + paso, top + alto * 0.27f); dientes.close(); x += paso; n++ }
        drawPath(dientes, a.c(crema, k, f))
        // El campo de achiote con sus franjas en diagonal.
        drawRect(a.c(mezcla(ACHIOTE, MesoTinta.BARRO, 0.3f), k, f), Offset(x0, top + alto * 0.33f), Size(an * 2, alto * 0.43f))
        val diag = Path()
        for (q in 0 until 5) { val xx = x0 + an * 0.45f * q; diag.moveTo(xx, top + alto * 0.76f); diag.lineTo(xx + an * 0.42f, top + alto * 0.33f) }
        drawPath(diag, a.c(MesoTinta.CINABRIO_OSC, k, f), style = Stroke(an * 0.09f))
        // La banda negra del pie con su filete rojo.
        drawRect(a.c(CINABRIO, k, f), Offset(x0, top + alto * 0.76f), Size(an * 2, alto * 0.025f))
        drawRect(a.c(TINTA, k * 0.6f, f), Offset(x0, top + alto * 0.785f), Size(an * 2, alto * 0.25f))
        cilindroMeso(Rect(pie.x - an * 0.5f, top, pie.x + an * 0.5f, pie.y), a, k, 1.3f, der, f)
        // El lustre del bruñido.
        drawRoundRect(a.b(0.55f, k), Offset(if (der) pie.x + an * 0.16f else pie.x - an * 0.3f, top + alto * 0.1f), Size(an * 0.08f, alto * 0.74f), CornerRadius(an * 0.04f))
    }
    drawPath(forma, a.s(0.35f), style = Stroke(0.7f * u))
    filoNichoMeso(m, forma, c, an * 0.1f)
    // La boca: lo hondo de dentro y el canto con su luz.
    val boca = Rect(pie.x - an * 0.56f, top - alto * 0.05f, pie.x + an * 0.56f, top + alto * 0.05f)
    drawOval(a.c(mezcla(MesoTinta.BARRO_OSC, TINTA, 0.5f), k * 0.5f), boca.topLeft, boca.size)
    drawOval(a.c(crema, k, f), boca.topLeft, boca.size, style = Stroke(1.2f * u))
    drawArc(a.b(0.5f, k), 150f, 120f, false, boca.topLeft, boca.size, style = Stroke(0.8f * u))
}

/**
 * El canasto grande de bejuco, colmado de cacao: el cuerpo cosido en espiral (las vueltas de bejuco, cada una abrazada por
 * sus puntadas en diagonal, dos teñidas y una franja de dientes de puntadas oscuras), el borde grueso enrollado con su amarre
 * en espiral, el volumen redondo con su luz y su sombra, su sombra en el testero; encima, el montón de mazorcas solapadas
 * (amarillas, rojas, granates), una abierta.
 */
private fun DrawScope.canastoMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val ancho = w * 0.36f; val alto = w * 0.13f
    val boca = pie.y - alto
    val k = m.k(Offset(pie.x, boca + alto * 0.5f)); val f = m.f(Offset(pie.x, boca + alto * 0.5f))
    val cuerpo = Path().apply {
        moveTo(pie.x - ancho * 0.5f, boca)
        cubicTo(pie.x - ancho * 0.5f, boca + alto * 0.55f, pie.x - ancho * 0.42f, pie.y, pie.x - ancho * 0.3f, pie.y)
        lineTo(pie.x + ancho * 0.3f, pie.y)
        cubicTo(pie.x + ancho * 0.42f, pie.y, pie.x + ancho * 0.5f, boca + alto * 0.55f, pie.x + ancho * 0.5f, boca)
        close()
    }
    // El montón de cacao, para la sombra del conjunto en el testero.
    val monton = Path().apply { addOval(Rect(pie.x - ancho * 0.46f, boca - w * 0.075f, pie.x + ancho * 0.44f, boca + w * 0.03f)) }
    val conjunto = Path().apply { addPath(cuerpo); addPath(monton) }
    clipRect(0f, 0f, w, m.apoyo - w * 0.012f) { sombraMuroMeso(m, conjunto, Offset(pie.x, boca), 0.28f) }
    contactoMeso(Offset(pie.x + ancho * 0.04f, pie.y + w * 0.003f), ancho * 0.98f, w * 0.016f, a, 0.45f)
    // Las mazorcas de atrás (más hondas), luego las de delante; la abierta, arriba.
    val pods = listOf(
        Triple(Offset(-0.3f, -0.05f), -18f, CACAO_GRANATE), Triple(Offset(0.18f, -0.065f), 22f, CACAO_AMARILLO),
        Triple(Offset(-0.06f, -0.075f), -4f, CACAO_ROJO), Triple(Offset(0.36f, -0.02f), 38f, CACAO_ROJO),
        Triple(Offset(-0.38f, 0.005f), -30f, CACAO_AMARILLO), Triple(Offset(0.05f, -0.02f), 12f, CACAO_GRANATE),
        Triple(Offset(-0.18f, 0f), 160f, CACAO_ROJO), Triple(Offset(0.26f, 0.01f), -150f, CACAO_AMARILLO),
    )
    pods.forEachIndexed { i, (o, g, col) ->
        val c = Offset(pie.x + ancho * o.x, boca + w * o.y)
        val atras = i < 4
        cacaoMeso(m, c, w * (if (atras) 0.09f else 0.1f), w * (if (atras) 0.046f else 0.05f), g, if (atras) mezcla(col, TINTA, 0.12f) else col, 820 + i, abierta = i == 2)
    }
    // El cuerpo cosido en espiral: el fondo honda entre las vueltas, cada vuelta de bejuco con su lomo de luz y su sombra
    // debajo, y las puntadas que la abrazan en diagonal; dos vueltas teñidas (achiote y añil) y una franja de dientes hecha con
    // puntadas oscuras.
    drawPath(cuerpo, a.c(BEJUCO_OSC, k, f))
    clipPath(cuerpo) {
        val vueltas = 10
        val v = alto / vueltas
        val comba = alto * 0.07f
        val claras = Path(); val achiote = Path(); val anil = Path()
        val lomos = Path(); val bajos = Path(); val puntadas = Path(); val dientes = Path()
        val x0 = pie.x - ancho * 0.56f; val x1 = pie.x + ancho * 0.56f
        for (r in 0 until vueltas) {
            val yc = boca + v * (r + 0.55f)
            val curva = Path().apply { moveTo(x0, yc); quadraticTo(pie.x, yc + comba * 2f, x1, yc) }
            (when (r) { 3 -> achiote; 7 -> anil; else -> claras }).addPath(curva)
            lomos.addPath(curva, Offset(0f, -v * 0.24f))
            bajos.addPath(curva, Offset(0f, v * 0.4f))
            // Las puntadas: cada una abraza la vuelta en diagonal; en las vueltas 4 a 6 forman dientes oscuros.
            val paso = v * 1.25f
            val cuantas = ((x1 - x0) / paso).toInt().coerceAtMost(80)
            for (q in 0..cuantas) {
                val t = q / cuantas.toFloat()
                val px = x0 + (x1 - x0) * t + (if (r % 2 == 0) 0f else paso * 0.5f)
                val py = yc + 4f * comba * t * (1f - t)
                val oscura = r in 4..6 && (q % 8) in (6 - r)..r
                (if (oscura) dientes else puntadas).apply { moveTo(px - v * 0.22f, py + v * 0.42f); lineTo(px + v * 0.22f, py - v * 0.42f) }
            }
        }
        drawPath(claras, a.c(mezcla(BEJUCO, ESTUCO, 0.18f), k, f), style = Stroke(v * 0.86f))
        drawPath(achiote, a.c(mezcla(BEJUCO, ACHIOTE, 0.7f), k, f), style = Stroke(v * 0.86f))
        drawPath(anil, a.c(mezcla(BEJUCO, ANIL, 0.65f), k, f), style = Stroke(v * 0.86f))
        drawPath(lomos, a.b(0.3f, k), style = Stroke(v * 0.2f))
        drawPath(bajos, a.s(0.3f), style = Stroke(v * 0.14f))
        drawPath(puntadas, a.c(mezcla(BEJUCO, BEJUCO_OSC, 0.55f), k, f), style = Stroke(v * 0.2f))
        drawPath(dientes, a.c(mezcla(TINTA, BEJUCO_OSC, 0.4f), k, f), style = Stroke(v * 0.34f))
        // El volumen redondo.
        cilindroMeso(Rect(pie.x - ancho * 0.5f, boca, pie.x + ancho * 0.5f, pie.y), a, k, 1.1f, m.der(pie.x, boca), f)
        drawRect(a.s(0.25f), Offset(pie.x - ancho, pie.y - alto * 0.2f), Size(ancho * 2, alto * 0.2f))
    }
    drawPath(cuerpo, a.c(BEJUCO_OSC, k, f), style = Stroke(1f * u))
    // El borde enrollado, con su amarre en espiral.
    val borde = Rect(pie.x - ancho * 0.52f, boca - w * 0.008f, pie.x + ancho * 0.52f, boca + w * 0.014f)
    drawRoundRect(a.s(0.3f), Offset(borde.left + 1.5f * u, borde.top + 2f * u), borde.size, CornerRadius(borde.height / 2))
    drawRoundRect(a.c(mezcla(BEJUCO, ESTUCO, 0.15f), k, f), borde.topLeft, borde.size, CornerRadius(borde.height / 2))
    clipRect(borde.left, borde.top, borde.right, borde.bottom) {
        val espiral = Path()
        var x = borde.left; var n = 0
        while (x < borde.right && n < 80) { espiral.moveTo(x, borde.bottom); espiral.lineTo(x + borde.height * 0.8f, borde.top); x += w * 0.008f; n++ }
        drawPath(espiral, a.c(BEJUCO_OSC, k, f), style = Stroke(0.9f * u))
        drawRect(a.b(0.35f, k), borde.topLeft, Size(borde.width, borde.height * 0.35f))
    }
    // De noche, el filo de la lumbre del nicho en el costado del canasto y en el borde que la miran.
    filoNichoMeso(m, cuerpo, Offset(pie.x, boca + alto * 0.5f), w * 0.01f)
    filoNichoMeso(m, Path().apply { addRoundRect(RoundRect(borde, CornerRadius(borde.height / 2))) }, Offset(pie.x + ancho * 0.3f, boca), w * 0.005f)
}

/**
 * La pila de mantas de algodón dobladas: tres, una encima de otra y no del todo parejas (añil abajo, cruda en medio, roja de
 * achiote arriba). Cada una es un paño plano doblado: a la izquierda, el doblez redondo con el lomo de luz y el pliegue; a la
 * derecha, el canto de las capas (las orillas, una sobre otra); sus franjas tejidas a lo largo; su luz arriba y su sombra en la
 * de abajo. De la de arriba cae por delante una punta con sus flecos; de la de en medio cuelgan los flecos de la orilla.
 */
internal fun DrawScope.mantasMeso(m: MesaMeso, r: Rect, enMuro: Boolean = true) {
    val u = density
    val w = m.w; val a = m.a
    val capas = listOf(ANIL, ALGODON, mezcla(CINABRIO, ACHIOTE, 0.3f))
    val franjas = listOf(listOf(ALGODON, ACHIOTE), listOf(CINABRIO, ANIL), listOf(ALGODON, TINTA))
    val alto = r.height / 3f
    val silueta = Path().apply { addRect(Rect(r.left, r.top + alto * 0.1f, r.right, r.bottom)) }
    if (enMuro) clipRect(0f, 0f, w, m.apoyo - w * 0.012f) { sombraMuroMeso(m, silueta, r.center, 0.26f) }
    contactoMeso(Offset(r.center.x, r.bottom + w * 0.003f), r.width * 1.05f, w * 0.014f, a, 0.45f)
    val der = m.der(r.center.x, r.center.y)
    // De abajo (j = 0) hacia arriba: cada una echa su sombra en la de abajo, ya pintada.
    for (j in 0 until 3) {
        val i = 2 - j
        val dx = (azar(i, 831) - 0.5f) * w * 0.02f
        val c = Rect(r.left + dx + w * 0.004f * i, r.bottom - alto * (j + 1) - alto * 0.04f * j, r.right + dx - w * 0.004f * (2 - i), r.bottom - alto * j)
        val k = m.k(c.center); val f = m.f(c.center)
        val h = c.height
        val dob = h * 0.5f
        // El paño: el doblez redondo a la izquierda, el canto de las orillas a la derecha, arriba apenas combado.
        val capa = Path().apply {
            moveTo(c.left + dob, c.top)
            quadraticTo(c.center.x, c.top - h * 0.08f, c.right - h * 0.1f, c.top + h * 0.02f)
            lineTo(c.right, c.top + h * 0.14f)
            lineTo(c.right + h * 0.03f, c.bottom - h * 0.08f)
            lineTo(c.right - h * 0.06f, c.bottom)
            lineTo(c.left + dob, c.bottom)
            cubicTo(c.left - dob * 0.12f, c.bottom, c.left - dob * 0.12f, c.top, c.left + dob, c.top)
            close()
        }
        // Su sombra en la de abajo (que ya está pintada).
        if (j > 0) drawPath(corridoMeso(capa, w * 0.006f, h * 0.2f), a.s(0.3f))
        drawPath(capa, a.c(capas[j], k, f))
        clipPath(capa) {
            // Las franjas tejidas a lo largo.
            drawRect(a.c(franjas[j][0], k, f), Offset(c.left, c.top + h * 0.36f), Size(c.width, h * 0.14f))
            drawRect(a.c(franjas[j][1], k, f), Offset(c.left, c.top + h * 0.58f), Size(c.width, h * 0.07f))
            if (j == 1) {
                // Una franja de dientes chicos, tejida (sin ningún signo).
                val dientes = Path()
                var x = c.left; var n = 0
                while (x < c.right && n < 60) { dientes.moveTo(x, c.top + h * 0.26f); dientes.lineTo(x + h * 0.08f, c.top + h * 0.17f); dientes.lineTo(x + h * 0.16f, c.top + h * 0.26f); x += h * 0.16f; n++ }
                drawPath(dientes, a.c(CINABRIO, k, f), style = Stroke(0.9f * u))
            }
            // El canto de la derecha: las orillas de las capas dobladas, una sobre otra.
            val canto = Path()
            for (q in 1..3) { val y = c.top + h * q / 4f; canto.moveTo(c.right - h * (0.7f - 0.08f * q), y); canto.lineTo(c.right + 2f * u, y + h * 0.03f) }
            drawPath(canto, a.s(0.32f), style = Stroke(0.8f * u))
            drawPath(corridoMeso(canto, 0f, -0.9f * u), a.b(0.25f, k), style = Stroke(0.5f * u))
            // El doblez: el lomo de luz por fuera y el pliegue de sombra por dentro.
            drawArc(a.b(0.4f, k), 110f, 140f, false, Offset(c.left + 1f * u, c.top + 1f * u), Size(dob * 1.3f, h - 2f * u), style = Stroke(1.4f * u))
            drawArc(a.s(0.3f), 115f, 130f, false, Offset(c.left + dob * 0.45f, c.top + h * 0.2f), Size(dob * 0.9f, h * 0.6f), style = Stroke(1f * u))
            // La luz de arriba, la sombra de abajo y el costado contrario a la luz.
            drawRect(a.b(0.2f, k), c.topLeft, Size(c.width, h * 0.16f))
            drawRect(a.s(0.24f), Offset(c.left, c.bottom - h * 0.22f), Size(c.width, h * 0.22f))
            drawRect(a.s(0.14f), Offset(if (der) c.left else c.right - c.width * 0.22f, c.top), Size(c.width * 0.22f, h))
        }
        drawPath(capa, a.s(0.32f), style = Stroke(0.7f * u))
        filoNichoMeso(m, capa, c.center, h * 0.14f)
        if (j == 1) {
            // Los flecos de la orilla, a la derecha, que cuelgan sobre la de abajo.
            val flecos = Path()
            for (q in 0 until 7) { val y = c.top + h * (0.2f + 0.1f * q); flecos.moveTo(c.right - 1f * u, y); flecos.lineTo(c.right + w * (0.006f + 0.004f * azar(q, 832)), y + h * (0.5f + 0.3f * azar(q, 833))) }
            drawPath(flecos, a.c(ALGODON, k, f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        }
        if (j == 2) {
            // La punta de la de arriba, que cae por delante de las otras con sus flecos.
            val px = c.left + c.width * 0.66f
            val baja = r.bottom - alto * 0.55f
            val punta = poligonoMeso(px - c.width * 0.12f, c.top + h * 0.4f, px + c.width * 0.14f, c.top + h * 0.4f, px + c.width * 0.05f, baja, px - c.width * 0.06f, baja - h * 0.2f)
            drawPath(corridoMeso(punta, 1.6f * u, 1.8f * u), a.s(0.32f))
            drawPath(punta, a.c(capas[2], k, f))
            clipPath(punta) {
                drawRect(a.c(franjas[2][0], k, f), Offset(px - c.width * 0.2f, baja - h * 0.75f), Size(c.width * 0.4f, h * 0.14f))
                drawRect(a.c(franjas[2][1], k, f), Offset(px - c.width * 0.2f, baja - h * 0.5f), Size(c.width * 0.4f, h * 0.07f))
                drawRect(a.s(0.2f), Offset(px + c.width * 0.02f, c.top), Size(c.width * 0.2f, baja - c.top))
                drawRect(a.s(0.3f), Offset(px - c.width * 0.2f, c.top + h * 0.4f), Size(c.width * 0.4f, h * 0.25f))
            }
            drawPath(punta, a.s(0.3f), style = Stroke(0.7f * u))
            val flecos = Path()
            for (q in 0 until 6) {
                val t = q / 5f
                val p0 = Offset(px - c.width * 0.06f + c.width * 0.11f * t, baja - h * 0.2f * (1f - t))
                flecos.moveTo(p0.x, p0.y); flecos.lineTo(p0.x + w * 0.002f * (azar(q, 834) - 0.5f), p0.y + h * (0.32f + 0.18f * azar(q, 835)))
            }
            drawPath(flecos, a.c(mezcla(capas[2], ALGODON, 0.3f), k, f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        }
    }
}

/**
 * El caparazón de tortuga que se toca como tambor, apoyado en el testero y un poco ladeado: el domo con sus placas (la hilera
 * del medio, las de los lados y las del borde), cada una con su carey moteado de ámbar, la luz arriba a la izquierda, la
 * media luna de sombra y el lustre; delante, cruzada, el asta de venado con que se toca.
 */
private fun DrawScope.careyMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val an = w * 0.15f; val al = w * 0.178f
    val c = Offset(pie.x, pie.y - al * 0.5f - w * 0.002f)
    val k = m.k(c); val f = m.f(c)
    val domo = Path().apply { addOval(Rect(c.x - an / 2, c.y - al / 2, c.x + an / 2, c.y + al / 2)) }
    rotate(-9f, Offset(pie.x, pie.y)) {
        clipRect(-w, -w, w * 2, m.apoyo - w * 0.012f) { sombraMuroMeso(m, domo, c, 0.3f) }
        drawPath(domo, a.c(CAREY, k, f))
        clipPath(domo) {
            // Las placas del borde: un anillo partido.
            drawOval(a.c(mezcla(CAREY, TINTA, 0.25f), k, f), Offset(c.x - an * 0.5f, c.y - al * 0.5f), Size(an, al))
            drawOval(a.c(CAREY, k, f), Offset(c.x - an * 0.42f, c.y - al * 0.43f), Size(an * 0.84f, al * 0.86f))
            // Las placas del medio y de los lados: hexágonos que se tocan, cada uno con su mota de ámbar.
            val juntas = Path()
            for (j in 0 until 5) {
                val y = c.y - al * 0.36f + al * 0.18f * j
                val pl = Rect(c.x - an * 0.13f, y - al * 0.08f, c.x + an * 0.13f, y + al * 0.08f)
                drawOval(a.c(mezcla(CAREY, CAREY_LUZ, 0.55f + 0.3f * azar(j, 841)), k, f), Offset(pl.center.x - an * 0.06f, pl.center.y - al * 0.04f), Size(an * 0.11f, al * 0.07f))
                juntas.moveTo(pl.left, pl.top); juntas.lineTo(pl.right, pl.top)
                juntas.moveTo(pl.left, pl.top); juntas.lineTo(pl.left - an * 0.04f, pl.center.y); juntas.lineTo(pl.left, pl.bottom)
                juntas.moveTo(pl.right, pl.top); juntas.lineTo(pl.right + an * 0.04f, pl.center.y); juntas.lineTo(pl.right, pl.bottom)
            }
            for (s in listOf(-1f, 1f)) for (j in 0 until 4) {
                val y = c.y - al * 0.3f + al * 0.2f * j
                val xc = c.x + s * an * 0.3f
                drawOval(a.c(mezcla(CAREY, CAREY_LUZ, 0.4f + 0.35f * azar(j + (if (s > 0) 5 else 0), 842)), k, f), Offset(xc - an * 0.07f, y - al * 0.03f), Size(an * 0.12f, al * 0.08f))
                juntas.moveTo(c.x + s * an * 0.17f, y + al * 0.1f); juntas.lineTo(c.x + s * an * 0.45f, y + al * 0.12f)
            }
            drawPath(juntas, a.c(mezcla(CAREY, TINTA, 0.55f), k, f), style = Stroke(1.1f * u))
            // El domo: la luz arriba a la izquierda (de noche, a la derecha, de la lumbre del nicho), la sombra del otro lado y
            // el lustre.
            val s = if (a.oscuro) -1f else 1f
            drawPath(bordeMeso(domo, -s * an * 0.16f, -al * 0.1f), a.s(0.35f))
            drawPath(bordeMeso(domo, -s * an * 0.05f, -al * 0.03f), a.s(0.2f))
            drawOval(a.b(0.3f, k), Offset(c.x - s * an * 0.21f - an * 0.15f, c.y - al * 0.4f), Size(an * 0.3f, al * 0.45f))
            drawOval(a.b(0.5f, k), Offset(c.x - s * an * 0.22f - an * 0.04f, c.y - al * 0.32f), Size(an * 0.08f, al * 0.18f))
        }
        drawPath(domo, a.s(0.4f), style = Stroke(0.8f * u))
        filoNichoMeso(m, domo, c, an * 0.07f, -9f)
    }
    contactoMeso(Offset(pie.x + an * 0.05f, pie.y + w * 0.003f), an * 0.9f, w * 0.012f, a, 0.4f)
    // El asta de venado, apoyada delante y cruzada: el tronco curvo y una punta.
    val a0 = Offset(pie.x - an * 0.55f, pie.y - w * 0.004f); val a1 = Offset(pie.x + an * 0.32f, pie.y - al * 0.78f)
    val asta = Path().apply { moveTo(a0.x, a0.y); quadraticTo(pie.x - an * 0.1f, pie.y - al * 0.2f, a1.x, a1.y) }
    val punta = Path().apply { moveTo(pie.x - an * 0.04f, pie.y - al * 0.36f); quadraticTo(pie.x - an * 0.02f, pie.y - al * 0.62f, pie.x - an * 0.2f, pie.y - al * 0.78f) }
    val ka = m.k(a1)
    translate(w * 0.008f, w * 0.006f) { drawPath(asta, a.s(0.3f), style = Stroke(w * 0.012f, cap = StrokeCap.Round)); drawPath(punta, a.s(0.25f), style = Stroke(w * 0.008f, cap = StrokeCap.Round)) }
    drawPath(asta, a.c(mezcla(ESTUCO, HENEQUEN_OSC, 0.35f), ka, f), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
    drawPath(punta, a.c(mezcla(ESTUCO, HENEQUEN_OSC, 0.25f), ka, f), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
    translate(-w * 0.003f, -w * 0.002f) { drawPath(asta, a.b(0.45f, ka), style = Stroke(w * 0.003f, cap = StrokeCap.Round)) }
    drawCircle(a.c(mezcla(HENEQUEN_OSC, TINTA, 0.3f), ka, f), w * 0.009f, a0 + Offset(w * 0.004f, 0f))
}

/** El copal en una hoja seca de maíz, bajo el nicho: unos terrones de resina clara, con su brillo, y su sombra. */
private fun DrawScope.copalMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(pie); val f = m.f(pie)
    val hoja = poligonoMeso(pie.x - w * 0.05f, pie.y - w * 0.002f, pie.x - w * 0.02f, pie.y - w * 0.014f, pie.x + w * 0.04f, pie.y - w * 0.012f, pie.x + w * 0.06f, pie.y - w * 0.004f, pie.x + w * 0.02f, pie.y + w * 0.002f)
    contactoMeso(Offset(pie.x + w * 0.006f, pie.y + w * 0.002f), w * 0.11f, w * 0.008f, a, 0.4f)
    drawPath(hoja, a.c(TOTOMOXTLE, k, f))
    drawPath(bordeMeso(hoja, 0f, -1.2f * u), a.s(0.3f))
    for (j in 0 until 5) {
        val q = Offset(pie.x - w * 0.03f + w * 0.016f * j + w * 0.004f * azar(j, 851), pie.y - w * (0.016f + 0.008f * azar(j, 852)))
        val r = w * (0.009f + 0.004f * azar(j, 853))
        val terron = contornoMeso(Rect(q.x - r, q.y - r * 0.8f, q.x + r, q.y + r * 0.8f), 854 + j, 0.2f)
        drawPath(terron, a.c(mezcla(ESTUCO, MAIZ, 0.3f), k, f))
        drawPath(bordeMeso(terron, -r * 0.3f, -r * 0.3f), a.s(0.25f))
        destelloMeso(Offset(q.x - r * 0.3f, q.y - r * 0.3f), 0.8f * u, a, k)
    }
}

/**
 * El atado de rajas de ocote apoyado en el testero, en la esquina (lo corta el borde): las rajas de pino resinoso, cada una con
 * su cara de luz, su veta de resina y la punta astillada; dos amarres de henequén; su sombra larga en el azul.
 */
private fun DrawScope.ocoteMeso(m: MesaMeso, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val tope = Offset(pie.x + w * 0.075f, pie.y - w * 0.215f)
    val k = m.k(Offset((pie.x + tope.x) / 2, (pie.y + tope.y) / 2)); val f = m.f(pie)
    val rajas = 7
    val silueta = Path().apply { addPath(poligonoMeso(pie.x - w * 0.03f, pie.y, pie.x + w * 0.035f, pie.y, tope.x + w * 0.04f, tope.y, tope.x - w * 0.035f, tope.y)) }
    clipRect(0f, 0f, w, m.apoyo - w * 0.012f) { sombraMuroMeso(m, silueta, Offset(pie.x, pie.y - w * 0.1f), 0.24f) }
    for (i in 0 until rajas) {
        val t = i / (rajas - 1f) - 0.5f
        val p0 = Offset(pie.x + t * w * 0.055f, pie.y - w * 0.002f)
        val p1 = Offset(tope.x + t * w * 0.07f, tope.y + w * 0.012f * azar(i, 861))
        val col = mezcla(OCOTE, if (i % 2 == 0) ESTUCO else MesoTinta.BARRO_OSC, 0.18f + 0.1f * azar(i, 862))
        drawLine(a.c(col, k, f), p0, p1, w * 0.011f, StrokeCap.Butt)
        drawLine(a.c(mezcla(OCOTE, MesoTinta.BARRO_OSC, 0.6f), k, f).copy(alpha = 0.7f), p0 + Offset(w * 0.003f, 0f), p1 + Offset(w * 0.003f, 0f), w * 0.0025f)
        drawLine(a.b(0.3f, k), p0 - Offset(w * 0.0035f, 0f), p1 - Offset(w * 0.0035f, 0f), w * 0.002f)
        // La punta astillada.
        drawLine(a.c(mezcla(OCOTE, ESTUCO, 0.35f), k, f), p1, p1 + (p1 - p0) * 0.03f + Offset(w * 0.004f * t, 0f), w * 0.006f, StrokeCap.Round)
    }
    listOf(0.3f, 0.66f).forEach { fr ->
        val c = Offset(pie.x + (tope.x - pie.x) * fr, pie.y + (tope.y - pie.y) * fr)
        drawLine(a.s(0.35f), c + Offset(-w * 0.03f, w * 0.004f), c + Offset(w * 0.035f, -w * 0.002f), 3.4f * u)
        drawLine(a.c(HENEQUEN, k, f), c + Offset(-w * 0.032f, w * 0.002f), c + Offset(w * 0.034f, -w * 0.004f), 2.6f * u)
        drawLine(a.b(0.4f, k), c + Offset(-w * 0.032f, w * 0.0f), c + Offset(w * 0.034f, -w * 0.006f), 0.7f * u)
    }
    contactoMeso(Offset(pie.x + w * 0.01f, pie.y + w * 0.003f), w * 0.08f, w * 0.01f, a, 0.4f)
}

// ---------------------------------------------------------------- lo colgado de la viga

/** Lo colgado de la viga de amarre: dos pares de mazorcas de maíz atadas por sus hojas y dos madejas de algodón teñido. */
internal fun DrawScope.colgadoMeso(m: MesaMeso) {
    val w = m.w
    val viga = w * VIGA_Y_MESO
    mazorcasMeso(m, Offset(w * 0.042f, viga), listOf(MAIZ, MAIZ_ROJO), 871)
    mazorcasMeso(m, Offset(w * 0.11f, viga), listOf(mezcla(MAIZ, ESTUCO, 0.5f), MAIZ, MAIZ), 872)
    madejaMeso(m, Offset(w * 0.515f, viga), MesoTinta.ANIL, w * 0.12f, 873)
    madejaMeso(m, Offset(w * 0.56f, viga), ACHIOTE, w * 0.1f, 874)
}

/**
 * Un manojo de mazorcas de maíz colgado de la viga por sus hojas: el amarre de henequén que da la vuelta a la viga, las hojas
 * secas (totomoxtle) vueltas hacia arriba y, abajo, las mazorcas de [colores] con sus hileras de granos, cada una con su luz y su
 * sombra; la sombra del manojo en el testero.
 */
private fun DrawScope.mazorcasMeso(m: MesaMeso, gancho: Offset, colores: List<Color>, semilla: Int) {
    val u = density
    val w = m.w; val a = m.a
    val n = colores.size
    val largo = w * 0.105f; val grueso = w * 0.03f
    val nudo = Offset(gancho.x, gancho.y + w * 0.028f)
    val formas = List(n) { i ->
        val g = (i - (n - 1) / 2f) * 9f + (azar(semilla, i) - 0.5f) * 5f
        Triple(g, Offset(nudo.x + (i - (n - 1) / 2f) * grueso * 0.55f, nudo.y + w * 0.012f), largo * (0.9f + 0.15f * azar(semilla, i + 5)))
    }
    // La sombra del manojo en el testero (de día, honda dentro del sol).
    val sombra = Path()
    formas.forEach { (g, top, l) ->
        val p = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(top.x - grueso / 2, top.y, top.x + grueso / 2, top.y + l, grueso / 2, grueso / 2)) }
        p.transform(androidx.compose.ui.graphics.Matrix().apply { translate(top.x, top.y); rotateZ(g); translate(-top.x, -top.y) })
        sombra.addPath(p)
    }
    sombraMuroMeso(m, sombra, Offset(nudo.x, nudo.y + largo * 0.5f), 0.26f)
    // Las hojas vueltas hacia arriba, detrás del nudo.
    formas.forEach { (g, top, _) ->
        val k = m.k(top); val f = m.f(top)
        for (s in listOf(-1f, 1f)) {
            val hoja = Path().apply {
                moveTo(top.x, top.y + w * 0.01f)
                quadraticTo(top.x + s * grueso * 0.9f, top.y - w * 0.01f, top.x + s * grueso * 0.35f + g * 0.0008f * w, gancho.y - w * 0.012f)
                quadraticTo(top.x + s * grueso * 0.2f, top.y - w * 0.004f, top.x, top.y + w * 0.01f)
                close()
            }
            drawPath(hoja, a.c(if (s < 0) TOTOMOXTLE else mezcla(TOTOMOXTLE, HENEQUEN_OSC, 0.35f), k, f))
            drawPath(hoja, a.s(0.25f), style = Stroke(0.6f * u))
        }
    }
    // Las mazorcas.
    formas.forEachIndexed { i, (g, top, l) ->
        val k = m.k(Offset(top.x, top.y + l * 0.5f)); val f = m.f(Offset(top.x, top.y + l * 0.5f))
        rotate(g, top) {
            val r = Rect(top.x - grueso / 2, top.y, top.x + grueso / 2, top.y + l)
            val forma = Path().apply {
                moveTo(r.left + grueso * 0.15f, r.top)
                lineTo(r.right - grueso * 0.15f, r.top)
                cubicTo(r.right + grueso * 0.02f, r.top + l * 0.3f, r.right, r.top + l * 0.75f, r.center.x, r.bottom)
                cubicTo(r.left, r.top + l * 0.75f, r.left - grueso * 0.02f, r.top + l * 0.3f, r.left + grueso * 0.15f, r.top)
                close()
            }
            drawPath(forma, a.c(colores[i], k, f))
            clipPath(forma) {
                // Los granos: hileras a lo largo, cada grano con su luz.
                val granos = Path()
                val filas = 4; val pasoY = grueso * 0.3f
                for (fi in 0 until filas) {
                    val x = r.left + grueso * (0.12f + 0.25f * fi)
                    var y = r.top + (if (fi % 2 == 0) 0f else pasoY * 0.5f)
                    while (y < r.bottom) { granos.addOval(Rect(x, y, x + grueso * 0.22f, y + pasoY * 0.85f)); y += pasoY }
                }
                drawPath(granos, a.b(0.22f, k))
                drawPath(granos, a.s(0.25f), style = Stroke(0.5f * u))
                cilindroMeso(r, a, k, 1.2f, m.der(r.center.x, r.center.y), f)
            }
            drawPath(forma, a.s(0.35f), style = Stroke(0.6f * u))
            filoNichoMeso(m, forma, Offset(top.x, top.y + l * 0.5f), grueso * 0.16f, g)
        }
    }
    // El amarre que da la vuelta a la viga.
    val k = m.k(nudo)
    drawLine(a.c(HENEQUEN, k), Offset(gancho.x - grueso * 0.3f, gancho.y - w * 0.02f), Offset(nudo.x - grueso * 0.2f, nudo.y), 1.6f * u)
    drawLine(a.c(HENEQUEN, k), Offset(gancho.x + grueso * 0.3f, gancho.y - w * 0.02f), Offset(nudo.x + grueso * 0.2f, nudo.y), 1.6f * u)
    drawRoundRect(a.c(HENEQUEN, k), Offset(gancho.x - grueso * 0.42f, gancho.y - w * 0.021f), Size(grueso * 0.84f, w * 0.008f), CornerRadius(w * 0.004f))
    drawCircle(a.c(HENEQUEN_OSC, k), w * 0.007f, nudo)
    drawCircle(a.b(0.4f, k), w * 0.0025f, nudo - Offset(w * 0.002f, w * 0.002f))
}

/**
 * Una madeja de algodón teñido de [col], colgada de la viga: el lazo que la cuelga, el cuerpo torcido (las vueltas en diagonal,
 * de luz y de sombra), el amarre de abajo y la vuelta redonda del final; su sombra en el testero.
 */
private fun DrawScope.madejaMeso(m: MesaMeso, gancho: Offset, col: Color, largo: Float, semilla: Int) {
    val u = density
    val w = m.w; val a = m.a
    val g = w * 0.03f
    val top = Offset(gancho.x, gancho.y + w * 0.016f)
    val k = m.k(Offset(top.x, top.y + largo * 0.5f)); val f = m.f(Offset(top.x, top.y + largo * 0.5f))
    val n = 6
    val paso = largo / n
    // La silueta, para su sombra en el testero.
    val silueta = Path().apply { addRoundRect(RoundRect(top.x - g * 0.36f, top.y, top.x + g * 0.36f, top.y + largo + g * 0.4f, g * 0.36f, g * 0.36f)) }
    sombraMuroMeso(m, silueta, Offset(top.x, top.y + largo * 0.5f), 0.24f)
    // Las dos puntas que pasan por encima de la viga.
    drawArc(a.c(mezcla(col, TINTA, 0.15f), k, f), 180f, 180f, false, Offset(top.x - g * 0.42f, gancho.y - w * 0.026f), Size(g * 0.84f, w * 0.05f), style = Stroke(g * 0.32f))
    drawArc(a.b(0.3f, k), 200f, 80f, false, Offset(top.x - g * 0.42f, gancho.y - w * 0.026f), Size(g * 0.84f, w * 0.05f), style = Stroke(0.8f * u))
    // La madeja torcida: tramos ovalados que se cruzan, cada uno con sus hebras, su luz de un lado y su sombra del otro.
    for (i in 0 until n) {
        val c = Offset(top.x + (azar(semilla, i) - 0.5f) * g * 0.12f, top.y + paso * (i + 0.5f))
        val giro = if (i % 2 == 0) 24f else -24f
        rotate(giro, c) {
            val r = Rect(c.x - g * 0.42f, c.y - paso * 0.78f, c.x + g * 0.42f, c.y + paso * 0.78f)
            val tramo = Path().apply { addOval(r) }
            drawPath(tramo, a.c(col, k, f))
            clipPath(tramo) {
                val hebras = Path()
                for (j in 0 until 4) { val x = r.left + r.width * (0.18f + 0.22f * j); hebras.moveTo(x, r.top); hebras.quadraticTo(x + r.width * 0.12f, r.center.y, x, r.bottom) }
                drawPath(hebras, a.s(0.22f), style = Stroke(0.6f * u))
                drawRect(a.b(0.28f, k), r.topLeft, Size(r.width * 0.3f, r.height))
                drawRect(a.s(0.28f), Offset(r.right - r.width * 0.32f, r.top), Size(r.width * 0.32f, r.height))
            }
            drawPath(tramo, a.s(0.35f), style = Stroke(0.6f * u))
            filoNichoMeso(m, tramo, c, g * 0.1f, giro)
        }
    }
    // El ojo de abajo, donde se juntan las vueltas.
    val ojo = Rect(top.x - g * 0.32f, top.y + largo - g * 0.1f, top.x + g * 0.32f, top.y + largo + g * 0.55f)
    drawOval(a.c(col, k, f), ojo.topLeft, ojo.size, style = Stroke(g * 0.2f))
    drawArc(a.b(0.35f, k), 140f, 100f, false, ojo.topLeft, ojo.size, style = Stroke(0.7f * u))
}
