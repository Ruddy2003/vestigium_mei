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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.LAMP_X
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.LAMP_Y
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ALMAGRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.AZUCAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CIELO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CREMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.VIDRIADO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TIERRA_VERDE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.GESSO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.HOJA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LAMPARA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOGAL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.OCRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.PERSIANA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TEJA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TELA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ULTRAMAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.YESO
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.contacto
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.corrido
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.poligono
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.redondo
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Urbe: el pie de la librería (los cajones, el suelo hidráulico, el trapo y el bolso de la obra)
// =====================================================================

/**
 * El pie de la librería: el mueble bajo de dos puertas con su cuarterón, la llave puesta con su borla y la puerta de la
 * izquierda entornada; las patas torneadas, el suelo de baldosas hidráulicas con su dibujo gastado, el bolso de lona de la
 * obra, caído, y una pila de libros grandes de arte en el suelo, cortada por el borde.
 */
internal fun DrawScope.pieUrbe(d: DisposicionEstante, suelo: Float, a: AireUrbe, cerca: (Offset) -> Float) {
    val u = density; val w = size.width; val h = size.height
    val b = d.base
    val costado = w * MuebleEstanteUrbe.COSTADO
    val pata = w * 0.016f
    val mueble = Rect(costado, b.top, w - costado, suelo - pata)
    // El suelo de baldosas hidráulicas: dos filas, en escorzo.
    baldosas(Rect(0f, suelo, w, h), a, cerca)
    // La sombra del mueble en el suelo, corrida a la derecha.
    drawRect(a.sombra(if (a.lumbre) 0.5f else 0.34f), Offset(costado + w * 0.01f, suelo), Size(w - costado, (h - suelo) * 0.3f))
    drawRect(a.sombra(0.15f), Offset(costado + w * 0.02f, suelo + (h - suelo) * 0.3f), Size(w - costado, (h - suelo) * 0.2f))
    // Las patas.
    listOf(costado + w * 0.02f, w / 2 - w * 0.015f, w - costado - w * 0.05f).forEach { x ->
        val k = cerca(Offset(x, suelo))
        drawRoundRect(a.luz(NOGAL_OSC, k), Offset(x, suelo - pata - 1f * u), Size(w * 0.03f, pata + 1f * u), CornerRadius(w * 0.01f))
        drawRect(a.claro(0.2f), Offset(x + w * 0.004f, suelo - pata), Size(w * 0.005f, pata))
    }
    // El cuerpo, por tramos (de noche, cada uno con su luz).
    val tramos = if (a.lumbre) 20 else 1
    for (t in 0 until tramos) {
        val xa = mueble.left + mueble.width * t / tramos; val xb = mueble.left + mueble.width * (t + 1) / tramos + 0.6f
        drawRect(a.luz(NOGAL, cerca(Offset((xa + xb) / 2, mueble.center.y))), Offset(xa, mueble.top), Size(xb - xa, mueble.height))
    }
    // El larguero de arriba y el zócalo de abajo.
    drawRect(a.claro(0.25f), mueble.topLeft, Size(mueble.width, 1.4f * u))
    drawRect(a.sombra(0.3f), Offset(mueble.left, mueble.top + w * 0.014f), Size(mueble.width, 1f * u))
    drawRect(a.sombra(0.3f), Offset(mueble.left, mueble.bottom - w * 0.02f), Size(mueble.width, w * 0.02f))
    drawRect(a.claro(0.2f), Offset(mueble.left, mueble.bottom - w * 0.02f), Size(mueble.width, 1f * u))
    // Los dos cajones: el de la izquierda, entreabierto, con el trapo de la obra que asoma.
    val m = w * 0.03f
    val medio = w / 2
    val cajones = listOf(Rect(mueble.left + m, mueble.top + w * 0.022f, medio - 1.2f * u, mueble.bottom - w * 0.026f), Rect(medio + 1.2f * u, mueble.top + w * 0.022f, mueble.right - m, mueble.bottom - w * 0.026f))
    cajones.forEachIndexed { i, p ->
        val k = cerca(p.center)
        val abierto = i == 0
        if (abierto) drawRect(a.luz(NOGAL_OSC, k * 0.3f), Offset(p.left, p.top - 1.5f * u), Size(p.width, 5f * u))
        drawRect(a.sombra(0.3f), Offset(p.left + 1.5f * u, p.top + 1.5f * u), p.size)
        val f = if (abierto) p.translate(0f, 2.5f * u) else p
        drawRect(a.luz(NOGAL, k), f.topLeft, f.size)
        vetas(f, a.luz(NOGAL_OSC, k).copy(alpha = 0.5f), 897 + i, densidad = 0.35f, grosor = 0.8f)
        // El tablero rehundido del frente, con su moldura.
        val cuart = f.deflate(minOf(f.width, f.height) * 0.16f)
        if (cuart.width > 2f && cuart.height > 2f) {
            drawRect(a.luz(mezcla(NOGAL, NOGAL_OSC, 0.25f), k), cuart.topLeft, cuart.size)
            vetas(cuart, a.luz(mezcla(NOGAL, CREMA, 0.2f), k).copy(alpha = 0.35f), 899 + i, densidad = 0.5f, grosor = 0.7f)
            drawRect(a.sombra(0.22f), cuart.topLeft, Size(cuart.width, 2f * u))
            drawRect(a.sombra(0.16f), cuart.topLeft, Size(2f * u, cuart.height))
            drawRect(a.claro(0.25f), Offset(cuart.left, cuart.bottom - 1.4f * u), Size(cuart.width, 1.4f * u))
            drawRect(a.claro(0.2f), Offset(cuart.right - 1.4f * u, cuart.top), Size(1.4f * u, cuart.height))
        }
        drawRect(a.claro(0.3f), f.topLeft, Size(f.width, 1f * u))
        drawRect(a.claro(0.2f), f.topLeft, Size(1f * u, f.height))
        drawRect(a.sombra(0.3f), Offset(f.right - 1.2f * u, f.top), Size(1.2f * u, f.height))
        if (a.lumbre && k > 0.05f) drawRect(a.filo(0.4f * k), f.topLeft, Size(f.width, 1f * u))
        // Los dos tiradores de latón: la bocallave de cada lado y el asa que cuelga, con el gastado de tanto abrirlo.
        listOf(0.25f, 0.75f).forEach { t ->
            val c = Offset(f.left + f.width * t, f.center.y - f.height * 0.08f)
            val kk = maxOf(k, 0.15f)
            val s = w * 0.024f
            val laton = a.luz(LATON, kk)
            val latonOsc = a.luz(mezcla(LATON, NOGAL_OSC, 0.4f), kk)
            drawOval(a.luz(mezcla(NOGAL, CREMA, 0.25f), kk).copy(alpha = 0.35f), Offset(c.x - s * 1.3f, c.y + s * 0.2f), Size(s * 2.6f, s * 0.9f))
            listOf(-1f, 1f).forEach { lado ->
                val b = Offset(c.x + lado * s, c.y)
                drawOval(a.sombra(0.35f), Offset(b.x - s * 0.22f + 1.2f * u, b.y - s * 0.3f + 1.4f * u), Size(s * 0.44f, s * 0.6f))
                drawOval(latonOsc, Offset(b.x - s * 0.22f, b.y - s * 0.3f), Size(s * 0.44f, s * 0.6f))
                drawOval(laton, Offset(b.x - s * 0.16f, b.y - s * 0.24f), Size(s * 0.3f, s * 0.42f))
                drawCircle(a.claro(0.6f), s * 0.05f, b - Offset(s * 0.06f, s * 0.1f))
            }
            val asa = Path().apply { moveTo(c.x - s, c.y); cubicTo(c.x - s, c.y + s * 0.9f, c.x + s, c.y + s * 0.9f, c.x + s, c.y) }
            drawPath(TrazoUrbe.corrido(asa, 1.4f * u, 1.8f * u), a.sombra(0.35f), style = Stroke(1.6f * u))
            drawPath(asa, latonOsc, style = Stroke(1.8f * u, cap = StrokeCap.Round))
            drawPath(TrazoUrbe.corrido(asa, -0.4f * u, -0.5f * u), laton, style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        if (abierto) trapo(f, a, k)
    }
    // El escudo de la cerradura del cajón de la derecha y la llave con su borla.
    val ce = Offset(cajones[1].center.x, cajones[1].center.y - w * 0.004f)
    val kc = maxOf(cerca(ce), 0.15f)
    drawRoundRect(a.luz(LATON, kc), Offset(ce.x - w * 0.009f, ce.y - w * 0.016f), Size(w * 0.018f, w * 0.032f), CornerRadius(w * 0.006f))
    drawCircle(a.claro(0.5f), 1f * u, ce - Offset(w * 0.004f, w * 0.009f))
    drawRect(a.luz(mezcla(LATON, TINTA, 0.3f), kc), Offset(ce.x - 0.8f * u, ce.y - w * 0.006f), Size(1.6f * u, w * 0.016f))
    drawCircle(a.luz(LATON, kc), w * 0.008f, ce + Offset(0f, -w * 0.002f), style = Stroke(1.4f * u))
    val borla = Path().apply { moveTo(ce.x, ce.y + w * 0.006f); cubicTo(ce.x + w * 0.004f, ce.y + w * 0.02f, ce.x - w * 0.002f, ce.y + w * 0.03f, ce.x + w * 0.002f, ce.y + w * 0.04f) }
    drawPath(borla, a.luz(ALMAGRA, kc), style = Stroke(1f * u))
    drawPath(poligono(ce.x - w * 0.006f, ce.y + w * 0.04f, ce.x + w * 0.01f, ce.y + w * 0.04f, ce.x + w * 0.012f, ce.y + w * 0.062f, ce.x - w * 0.008f, ce.y + w * 0.062f), a.luz(ALMAGRA, kc))
    drawRect(a.luz(mezcla(ALMAGRA, OCRE, 0.4f), kc), Offset(ce.x - w * 0.006f, ce.y + w * 0.04f), Size(w * 0.016f, w * 0.005f))
    // El bolso de lona de la obra, caído contra el cajón, y los libros grandes del suelo.
    bolso(Offset(w * 0.17f, h - (h - suelo) * 0.22f), w * 0.2f, a, maxOf(cerca(Offset(w * 0.17f, suelo)), 0.12f))
    librosTumbadosUrbe(Offset(w * 0.79f, h - (h - suelo) * 0.2f), w * 0.24f, 3, a, maxOf(cerca(Offset(w * 0.85f, suelo)), 0.12f), 2)
}

/**
 * El trapo de algodón de la obra, manchado de pigmento: sale del cajón entreabierto, se dobla en el canto y cae blando por
 * delante del frente hasta pasar de él, con sus pliegues, el dobladillo deshilachado y una punta que se tuerce.
 */
private fun DrawScope.trapo(f: Rect, a: AireUrbe, k: Float) {
    val u = density
    val x0 = f.left + f.width * 0.42f; val x1 = f.left + f.width * 0.62f
    val an = x1 - x0
    val caida = f.height * 1.1f
    val tela = Path().apply {
        moveTo(x0 - an * 0.05f, f.top - 3f * u)
        cubicTo(x0 + an * 0.3f, f.top - 5f * u, x1 - an * 0.2f, f.top - 4f * u, x1 + an * 0.02f, f.top - 2f * u)
        cubicTo(x1 + an * 0.06f, f.top + caida * 0.35f, x1 - an * 0.02f, f.top + caida * 0.7f, x1 - an * 0.1f, f.top + caida)
        cubicTo(x1 - an * 0.3f, f.top + caida * 0.92f, x1 - an * 0.45f, f.top + caida * 1.04f, x0 + an * 0.38f, f.top + caida * 0.9f)
        cubicTo(x0 + an * 0.2f, f.top + caida * 0.84f, x0 + an * 0.12f, f.top + caida * 0.95f, x0 + an * 0.04f, f.top + caida * 0.8f)
        cubicTo(x0 - an * 0.06f, f.top + caida * 0.5f, x0 - an * 0.08f, f.top + caida * 0.2f, x0 - an * 0.05f, f.top - 3f * u)
        close()
    }
    drawPath(corrido(tela, 2.4f * u, 2.6f * u), a.sombra(0.32f))
    val c = a.luz(mezcla(mezcla(YESO, TELA, 0.45f), OCRE, 0.12f), k)
    drawPath(tela, c)
    clipPath(tela) {
        // Los pliegues que bajan del canto, con su sombra y su luz.
        listOf(0.18f to -0.08f, 0.45f to 0.04f, 0.72f to 0.1f).forEach { (t, ladeo) ->
            val x = x0 + an * t
            val pl = Path().apply { moveTo(x, f.top); cubicTo(x + an * ladeo, f.top + caida * 0.35f, x - an * ladeo, f.top + caida * 0.6f, x + an * ladeo * 1.5f, f.top + caida) }
            drawPath(pl, a.sombra(0.2f), style = Stroke(2.2f * u))
            drawPath(corrido(pl, -1.6f * u, 0f), a.claro(0.35f), style = Stroke(0.9f * u))
        }
        // El doblez en el canto del cajón.
        drawRect(a.sombra(0.22f), Offset(x0 - an * 0.1f, f.top - 0.5f * u), Size(an * 1.2f, 2.4f * u))
        drawRect(a.claro(0.4f), Offset(x0 - an * 0.1f, f.top - 3f * u), Size(an * 1.2f, 1.2f * u))
        // Las manchas de pigmento.
        drawOval(a.luz(OCRE, k).copy(alpha = 0.75f), Offset(x0 + an * 0.15f, f.top + caida * 0.15f), Size(an * 0.32f, caida * 0.12f))
        drawOval(a.luz(ULTRAMAR, k).copy(alpha = 0.6f), Offset(x0 + an * 0.55f, f.top + caida * 0.36f), Size(an * 0.22f, caida * 0.09f))
        drawOval(a.luz(ALMAGRA, k).copy(alpha = 0.55f), Offset(x0 + an * 0.28f, f.top + caida * 0.58f), Size(an * 0.18f, caida * 0.07f))
        drawOval(a.luz(PaletaUrbe.TIERRA_VERDE, k).copy(alpha = 0.5f), Offset(x0 + an * 0.6f, f.top + caida * 0.7f), Size(an * 0.14f, caida * 0.06f))
    }
    // Los hilos sueltos del dobladillo.
    for (j in 0 until 6) {
        val x = x0 + an * (0.1f + 0.14f * j)
        val y = f.top + caida * (0.86f + 0.1f * azar(905, j))
        drawLine(c, Offset(x, y), Offset(x + (azar(906, j) - 0.5f) * 2f * u, y + (2f + 2.5f * azar(907, j)) * u), 0.6f * u)
    }
}

/**
 * El suelo de baldosas hidráulicas, en escorzo (tres hileras que se abren hacia delante): cada una con su flor de cuatro
 * pétalos gris azulada, los cuartos de círculo de teja de las esquinas que forman círculos con las vecinas y su filete; el
 * dibujo gastado por las pisadas, una baldosa cambiada y otra rajada, y la cera vieja.
 */
private fun DrawScope.baldosas(r: Rect, a: AireUrbe, cerca: (Offset) -> Float) {
    val u = density
    val k = if (a.lumbre) maxOf(cerca(Offset(r.center.x, r.top)), 0.08f) else 1f
    val fondo = a.luz(mezcla(CREMA, YESO, 0.5f), k)
    drawRect(fondo, r.topLeft, r.size)
    val cx = r.center.x
    val filas = listOf(0f to 0.27f, 0.27f to 0.6f, 0.6f to 1f)
    val flor = a.luz(mezcla(AZUCAR, TINTA, 0.22f), k).copy(alpha = 0.7f)
    val teja = a.luz(mezcla(TEJA, CREMA, 0.3f), k).copy(alpha = 0.6f)
    val junta = a.sombra(0.28f)
    clipRect(r.left, r.top, r.right, r.bottom) {
        filas.forEachIndexed { f, (t0, t1) ->
            val y0 = r.top + r.height * t0; val y1 = r.top + r.height * t1
            val e0 = 0.84f + 0.16f * t0; val e1 = 0.84f + 0.16f * t1
            val lado = maxOf(4f * u, r.width * 0.125f)
            var x = cx - lado * 5.5f
            var j = 0
            while (x < cx + lado * 5.5f && j < 40) {
                val xa0 = cx + (x - cx) * e0; val xb0 = cx + (x + lado - cx) * e0
                val xa1 = cx + (x - cx) * e1; val xb1 = cx + (x + lado - cx) * e1
                val c = Offset((xa0 + xb1) / 2, (y0 + y1) / 2)
                val rx = (xb1 - xa1) * 0.5f; val ry = (y1 - y0) * 0.5f
                val n = f * 20 + j
                if (azar(930, n) < 0.08f) drawPath(poligono(xa0, y0, xb0, y0, xb1, y1, xa1, y1), a.luz(mezcla(CREMA, OCRE, 0.25f), k))
                // La flor de cuatro pétalos y el botón.
                for (p in 0 until 4) {
                    val dx = if (p % 2 == 0) rx * 0.36f else -rx * 0.36f
                    val dy = if (p < 2) ry * 0.36f else -ry * 0.36f
                    drawOval(flor, Offset(c.x + dx - rx * 0.2f, c.y + dy - ry * 0.2f), Size(rx * 0.4f, ry * 0.4f))
                }
                drawOval(teja, Offset(c.x - rx * 0.14f, c.y - ry * 0.14f), Size(rx * 0.28f, ry * 0.28f))
                // Los cuartos de círculo de las esquinas.
                listOf(Offset(xa0, y0), Offset(xb0, y0), Offset(xa1, y1), Offset(xb1, y1)).forEach { q ->
                    drawOval(teja, Offset(q.x - rx * 0.32f, q.y - ry * 0.32f), Size(rx * 0.64f, ry * 0.64f))
                }
                drawLine(junta, Offset(xa0, y0), Offset(xa1, y1), 0.7f * u)
                // Una baldosa rajada.
                if (n == 23) drawPath(Path().apply { moveTo(xa0 + rx * 0.3f, y0); lineTo(c.x, c.y); lineTo(xb1 - rx * 0.2f, y1) }, a.sombra(0.4f), style = Stroke(0.7f * u))
                x += lado; j++
            }
            drawLine(junta, Offset(r.left, y1), Offset(r.right, y1), 0.7f * u)
        }
        drawLine(junta, Offset(r.left, r.top), Offset(r.right, r.top), 0.7f * u)
        // El gastado de las pisadas, más claro delante del mueble, y la cera vieja.
        pinceladas(Rect(r.left, r.top + r.height * 0.3f, r.right, r.bottom), a.luz(mezcla(CREMA, YESO, 0.3f), k), 895, 22, u, 0.3f)
        pinceladas(r, a.sombra(1f), 896, 10, u, 0.08f)
    }
}

/**
 * El bolso de lona de la obra, caído: la lona blanda que se dobla, la boca abierta con un rollo de papel y un pincel ancho que
 * asoman, el asa que cae por delante y el bolsillo con una mancha de cal.
 */
private fun DrawScope.bolso(pie: Offset, an: Float, a: AireUrbe, cerca: Float) {
    val u = density
    val al = an * 0.6f
    val lona = a.luz(TELA, cerca)
    val lonaOsc = a.luz(mezcla(TELA, NOGAL_OSC, 0.35f), cerca)
    // Lo que asoma de la boca: un rollo de papel y la brocha.
    drawLine(a.luz(CREMA, cerca), Offset(pie.x - an * 0.12f, pie.y - al * 0.8f), Offset(pie.x - an * 0.3f, pie.y - al * 1.25f), an * 0.09f, StrokeCap.Round)
    drawCircle(a.luz(mezcla(CREMA, OCRE, 0.2f), cerca), an * 0.045f, Offset(pie.x - an * 0.3f, pie.y - al * 1.25f))
    drawLine(a.luz(NOGAL, cerca), Offset(pie.x + an * 0.12f, pie.y - al * 0.7f), Offset(pie.x + an * 0.2f, pie.y - al * 1.18f), an * 0.035f, StrokeCap.Round)
    drawRect(a.luz(mezcla(TINTA, NOGAL, 0.4f), cerca), Offset(pie.x + an * 0.16f, pie.y - al * 1.3f), Size(an * 0.1f, al * 0.12f))
    contacto(Offset(pie.x + an * 0.08f, pie.y), an * 1.15f, al * 0.14f, a, 0.42f)
    // La lona: ancha abajo, la boca hundida y el lado derecho que se dobla hacia dentro.
    val cuerpo = Path().apply {
        moveTo(pie.x - an * 0.46f, pie.y)
        cubicTo(pie.x - an * 0.52f, pie.y - al * 0.4f, pie.x - an * 0.44f, pie.y - al * 0.78f, pie.x - an * 0.36f, pie.y - al * 0.86f)
        cubicTo(pie.x - an * 0.15f, pie.y - al * 0.74f, pie.x + an * 0.1f, pie.y - al * 0.78f, pie.x + an * 0.3f, pie.y - al * 0.92f)
        cubicTo(pie.x + an * 0.36f, pie.y - al * 0.7f, pie.x + an * 0.5f, pie.y - al * 0.35f, pie.x + an * 0.48f, pie.y)
        close()
    }
    drawPath(cuerpo, lona)
    clipPath(cuerpo) {
        // El pliegue grande y la sombra del lado contrario a la luz.
        val pliegue = Path().apply { moveTo(pie.x + an * 0.06f, pie.y - al * 0.8f); cubicTo(pie.x + an * 0.12f, pie.y - al * 0.5f, pie.x + an * 0.02f, pie.y - al * 0.3f, pie.x + an * 0.1f, pie.y) }
        drawPath(pliegue, lonaOsc.copy(alpha = 0.6f), style = Stroke(2.4f * u))
        drawPath(corrido(pliegue, -1.6f * u, 0f), a.claro(0.25f), style = Stroke(1f * u))
        drawRect(a.sombra(0.22f), Offset(pie.x + an * 0.22f, pie.y - al), Size(an * 0.4f, al))
        drawRect(a.claro(0.2f), Offset(pie.x - an * 0.46f, pie.y - al), Size(an * 0.12f, al))
        // La trama de la lona.
        pinceladas(Rect(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y), lonaOsc, 897, 10, u, 0.12f)
        // El bolsillo, con su costura y una mancha de cal.
        val bol = Rect(pie.x - an * 0.3f, pie.y - al * 0.5f, pie.x + an * 0.02f, pie.y - al * 0.12f)
        drawRect(mezcla(lona, lonaOsc, 0.25f), bol.topLeft, bol.size)
        drawRect(lonaOsc, bol.topLeft, Size(bol.width, 1.2f * u))
        drawOval(a.luz(GESSO, cerca).copy(alpha = 0.6f), Offset(bol.left + bol.width * 0.5f, bol.top + bol.height * 0.4f), Size(bol.width * 0.35f, bol.height * 0.3f))
        // La boca: el borde hundido, en sombra.
        drawPath(Path().apply { moveTo(pie.x - an * 0.36f, pie.y - al * 0.86f); cubicTo(pie.x - an * 0.15f, pie.y - al * 0.74f, pie.x + an * 0.1f, pie.y - al * 0.78f, pie.x + an * 0.3f, pie.y - al * 0.92f) }, lonaOsc, style = Stroke(2.6f * u))
    }
    // El asa, que cae por delante en curva floja.
    val asa = Path().apply { moveTo(pie.x - an * 0.3f, pie.y - al * 0.82f); cubicTo(pie.x - an * 0.4f, pie.y - al * 0.3f, pie.x + an * 0.05f, pie.y - al * 0.15f, pie.x + an * 0.22f, pie.y - al * 0.88f) }
    drawPath(corrido(asa, 1.5f * u, 2f * u), a.sombra(0.25f), style = Stroke(2.6f * u))
    drawPath(asa, lonaOsc, style = Stroke(2.6f * u, cap = StrokeCap.Round))
    drawPath(corrido(asa, -0.6f * u, -0.6f * u), a.claro(0.2f), style = Stroke(0.7f * u))
}
