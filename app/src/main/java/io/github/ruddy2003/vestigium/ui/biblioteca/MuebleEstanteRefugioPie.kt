package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRefugio as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.floor

/**
 * El pie de la librería: el faldón de esmalte con su zócalo cromado y las cuatro patas cónicas abiertas con su contera; debajo, el
 * zócalo verde azulado del muro y la rejilla del suelo del Ideario. Bajo el mueble, la bandeja hidropónica con sus lechugas y la
 * lámpara de cultivo del Ideario colgada del faldón por dos cadenitas; a la izquierda, la regadera esmaltada; a la derecha, el
 * taburete de cocina con un disco encima. Se pinta en cada dibujo (lo fijo de arriba ya ocupa la imagen guardada).
 */
internal object PieEstanteRefugio {
    fun DrawScope.pie(g: MarcoRefugio) {
        val w = g.w; val u = density
        val b0 = g.fondo
        if (g.h - b0 < 4f * u) return
        val falda = b0 + w * 0.045f
        val suelo = b0 + w * 0.16f
        val pisan = g.h - w * 0.035f
        // La fila de la junta entre la última tabla y el faldón, de píxel entero.
        drawRect(g.luz(w * 0.5f, b0, 0.03f).c(P.AGUA_OSC), Offset(0f, floor(b0)), Size(w, 1f))
        muroBajo(g, falda, suelo)
        piso(g, suelo)
        patas(g, falda, pisan, atras = true)
        bandeja(g, falda, suelo)
        faldon(g, b0, falda)
        patas(g, falda, pisan, atras = false)
        regadera(g, pisan)
        taburete(g, pisan)
        // De noche, el charco rosa del tubo de cultivo en el suelo y en el zócalo.
        if (g.a.cultivo > 0f && g.a.oscuro) {
            mancha(Offset(g.cultivo.x, g.cultivo.y + w * 0.06f), w * 0.36f, P.LAMPARA.copy(alpha = 0.16f * g.a.cultivo))
            mancha(Offset(g.cultivo.x, g.cultivo.y + w * 0.02f), w * 0.15f, P.LAMPARA.copy(alpha = 0.26f * g.a.cultivo))
        }
    }

    /** El zócalo verde azulado del muro, entre las patas, con sus juntas, y la sombra franca del mueble encima. */
    private fun DrawScope.muroBajo(g: MarcoRefugio, falda: Float, suelo: Float) {
        val w = g.w; val u = density
        with(ParedEstanteRefugio) { campo(g, Rect(0f, falda - 1f, w, suelo), P.TEAL, 0f, paso = 16f * density, muestras = 9) }
        pinceladas(Rect(0f, falda, w, suelo), if (g.a.oscuro) g.a.hondo else mezcla(P.TEAL, Color.Black, 0.4f), 9801, 14, u, 0.14f)
        with(ParedEstanteRefugio) { bloques(g, falda, suelo) }
        // La sombra del mueble: franca bajo el faldón y apagándose hacia el suelo.
        drawRect(Brush.verticalGradient(listOf(g.a.sombra(if (g.a.oscuro) 0.5f else 0.42f), g.a.sombra(if (g.a.oscuro) 0.25f else 0.16f)), falda, suelo), Offset(0f, falda), Size(w, suelo - falda))
        // Un desconchón en el zócalo, donde asoma el hormigón.
        val c = Offset(w * 0.86f, falda + (suelo - falda) * 0.5f)
        val l = g.luz(c, 0f)
        val p = escamaRefugio(c, w * 0.02f, w * 0.012f, 9810, 9)
        drawPath(p, l.c(mezcla(P.HORMIGON, P.TEAL, 0.3f)))
        drawPath(p, l.s(0.3f), style = Stroke(0.7f * u))
    }

    /** La rejilla del suelo (la del Ideario): los barrotes que se abren hacia quien mira, los travesaños y la sombra del rincón. */
    private fun DrawScope.piso(g: MarcoRefugio, suelo: Float) {
        val w = g.w; val u = density
        val h = g.h
        val l = g.luz(w * 0.5f, (suelo + h) / 2, 0.03f)
        with(ParedEstanteRefugio) { campo(g, Rect(0f, suelo, w, h), mezcla(P.ACERO_OSC, P.NOCHE, 0.5f), 0.03f, paso = 16f * density, muestras = 9) }
        val trav = Path(); val travLuz = Path()
        var y = suelo + 2f * u
        var paso = (h - suelo) * 0.14f
        var j = 0
        while (y < h && j < 40) {
            trav.addRect(Rect(0f, y, w, y + 1.8f * u)); travLuz.addRect(Rect(0f, y, w, y + 0.6f * u))
            y += maxOf(2f * u, paso); paso *= 1.3f; j++
        }
        val xs = List(9) { w * it / 8f }
        drawPath(trav, Brush.horizontalGradient(xs.map { g.luz(it, (suelo + h) / 2, 0.03f).c(P.ACERO_OSC) }, 0f, w))
        drawPath(travLuz, Brush.horizontalGradient(xs.map { g.luz(it, (suelo + h) / 2, 0.03f).b(0.3f) }, 0f, w))
        val barras = Path(); val barrasLuz = Path()
        for (i in -3..27) {
            val x0 = w * i / 24f
            val x1 = w / 2 + (x0 - w / 2) * 1.3f
            barras.moveTo(x0, suelo); barras.lineTo(x1, h)
            barrasLuz.moveTo(x0 - 0.8f * u, suelo); barrasLuz.lineTo(x1 - 0.8f * u, h)
        }
        drawPath(barras, Brush.horizontalGradient(xs.map { g.luz(it, (suelo + h) / 2, 0.03f).c(mezcla(P.ACERO_OSC, P.ACERO, 0.4f)) }, 0f, w), style = Stroke(2.2f * u))
        drawPath(barrasLuz, Brush.horizontalGradient(xs.map { g.luz(it, (suelo + h) / 2, 0.03f).b(0.3f) }, 0f, w), style = Stroke(0.7f * u))
        pinceladas(Rect(0f, suelo, w, h), if (g.a.oscuro) g.a.hondo else P.ACERO_OSC, 9820, 12, u, 0.14f)
        drawRect(l.s(0.4f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(l.s(0.18f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
        // De noche, el filo rosa en los barrotes de debajo del tubo.
        if (g.a.oscuro && g.a.cultivo > 0f) {
            val filos = xs.map { g.luz(it, suelo + (h - suelo) * 0.3f, 0.03f).filo(0.6f) }
            drawPath(travLuz, Brush.horizontalGradient(filos, 0f, w))
        }
    }

    /** El faldón de esmalte verde agua bajo la última tabla, con su zócalo cromado abajo y el esmalte rozado por los pies. */
    private fun DrawScope.faldon(g: MarcoRefugio, b0: Float, falda: Float) {
        val w = g.w; val u = density
        with(ParedEstanteRefugio) { campo(g, Rect(0f, b0, w, falda), P.AGUA, 0.04f, paso = 9f * density, muestras = 9) }
        pinceladas(Rect(0f, b0, w, falda), if (g.a.oscuro) g.a.hondo else P.AGUA_OSC, 9830, 10, u, 0.1f)
        drawRect(g.a.sombra(0.35f), Offset(0f, b0), Size(w, 2f * u))
        val zc = Rect(0f, falda - w * 0.014f, w, falda)
        cromoRefugio(zc, g.luz(w * 0.3f, zc.center.y, 0.045f), brillo = 0.75f)
        if (g.a.oscuro || g.a.noche) {
            val filos = List(9) { j -> g.luz(w * j / 8f, zc.top, 0.045f).filo(0.8f) }
            drawRect(Brush.horizontalGradient(filos, 0f, w), Offset(0f, zc.top), Size(w, 1.6f * u))
            val bajos = List(9) { j -> g.luz(w * j / 8f, falda + w * 0.02f, 0.045f).filo(0.8f) }
            drawRect(Brush.horizontalGradient(bajos, 0f, w), Offset(0f, falda - 1.6f * u), Size(w, 1.6f * u))
        }
        for (i in 0 until 6) {
            val x = w * (0.05f + 0.9f * azar(9840, i))
            val ls = g.luz(x, falda - w * 0.02f, 0.045f)
            saltadoRefugio(Offset(x, b0 + (falda - b0) * (0.3f + 0.4f * azar(9841, i))), w * (0.004f + 0.004f * azar(9842, i)), 9850 + i, ls.c(P.CROMO_OSC), ls.b(0.4f))
        }
    }

    /**
     * Las patas cónicas abiertas, de esmalte verde agua, cada una con su contera cromada: las de atrás, más cortas y en la sombra del
     * mueble; las de delante, abiertas hacia fuera, con su luz de un lado y su sombra en el suelo.
     */
    private fun DrawScope.patas(g: MarcoRefugio, falda: Float, pisan: Float, atras: Boolean) {
        val w = g.w; val u = density
        val xs = if (atras) listOf(0.15f, 0.85f) else listOf(0.055f, 0.945f)
        val fin = if (atras) falda + (pisan - falda) * 0.62f else pisan
        xs.forEachIndexed { i, fx ->
            val x = w * fx
            val abre = (if (i == 0) -1f else 1f) * w * (if (atras) 0.012f else 0.024f)
            val a0 = w * 0.014f; val a1 = w * 0.007f
            val pata = poligonoRefugio(Offset(x - a0, falda), Offset(x + a0, falda), Offset(x + abre + a1, fin), Offset(x + abre - a1, fin))
            val l = g.luz(x, (falda + fin) / 2, if (atras) 0.02f else 0.06f)
            if (!atras) drawOval(l.s(0.5f), Offset(x + abre - w * 0.03f + (if (g.a.oscuro) 0f else w * 0.01f), fin - 2f * u), Size(w * 0.06f, 4f * u))
            planosRefugio(pata, mezcla(l.c(P.AGUA), g.a.hondo, if (atras) 0.35f else 0f), l, if (g.a.oscuro) g.hacia(x, fin) else -1f, 0.3f, 0.35f)
            if (l.k > 0.06f && !atras) drawPath(bordeRefugio(pata, -g.hacia(x, fin) * 1.2f * u, 0f), l.filo(0.8f))
            // La contera cromada y el regatón de goma.
            val c = Rect(x + abre - a1 * 1.15f, fin - w * 0.024f, x + abre + a1 * 1.15f, fin - w * 0.004f)
            cromoRefugio(c, l, vertical = true)
            drawRect(l.c(P.BAQUELITA), Offset(c.left, fin - w * 0.004f), Size(c.width, w * 0.004f + 0.6f * u))
            // La placa de la pata, atornillada bajo el faldón.
            if (!atras) {
                drawRect(l.c(P.CROMO_OSC), Offset(x - a0 * 1.4f, falda), Size(a0 * 2.8f, 2.4f * u))
                drawRect(l.b(0.6f), Offset(x - a0 * 1.4f, falda), Size(a0 * 2.8f, 0.7f * u))
            }
        }
    }

    /**
     * La bandeja hidropónica bajo el mueble: la artesa esmaltada sobre su caballete, las macetas de rejilla con sus lechugas, y la
     * lámpara de cultivo del Ideario (la pantalla verde azulada con su tubo rosa) colgada del faldón por dos cadenitas.
     */
    private fun DrawScope.bandeja(g: MarcoRefugio, falda: Float, suelo: Float) {
        val w = g.w; val u = density
        val x0 = w * 0.2f; val x1 = w * 0.76f
        val pie = suelo + w * 0.07f
        val alto = w * 0.062f
        val artesa = Rect(x0, pie - alto, x1, pie - w * 0.012f)
        // La luz de la artesa, tomada más abajo: su frente mira a quien mira, no al tubo de encima.
        val l = g.luz((x0 + x1) / 2, artesa.bottom + w * 0.05f, 0.03f)
        // El caballete de tubo y la sombra en el suelo.
        drawOval(l.s(0.45f), Offset(x0, pie - 2.6f * u), Size(x1 - x0, 5f * u))
        listOf(x0 + w * 0.03f, x1 - w * 0.03f).forEach { x ->
            drawLine(l.c(P.CROMO_OSC), Offset(x, artesa.bottom), Offset(x - w * 0.01f, pie), 1.8f * u)
            drawLine(l.c(P.CROMO_OSC), Offset(x, artesa.bottom), Offset(x + w * 0.01f, pie), 1.8f * u)
        }
        // Las lechugas, detrás del frente de la artesa.
        for (j in 0 until 5) {
            val cx = x0 + (x1 - x0) * (0.12f + 0.19f * j)
            val lj = g.luz(cx, artesa.top + w * 0.07f, 0.03f)
            val rr = w * (0.046f + 0.01f * azar(9900, j))
            for (k in 0 until 7) {
                val a = -2.9f + k * 0.48f + 0.2f * azar(9901 + j, k)
                val hl = rr * (0.9f + 0.4f * azar(9902 + j, k))
                val p = Offset(cx + hl * 0.55f * kotlin.math.cos(a), artesa.top - hl * 0.3f + hl * 0.4f * kotlin.math.sin(a))
                rotate(Math.toDegrees(a.toDouble()).toFloat() + 90f, p) {
                    drawOval(lj.c(mezcla(P.HOJA, Color(0xFF9CC45A), 0.4f * (k % 2))), Offset(p.x - hl * 0.32f, p.y - hl * 0.5f), Size(hl * 0.64f, hl))
                    drawLine(lj.b(0.35f), Offset(p.x, p.y - hl * 0.4f), Offset(p.x, p.y + hl * 0.35f), 0.6f * u)
                }
            }
            drawOval(lj.c(mezcla(Color(0xFF9CC45A), P.HOJA, 0.2f)), Offset(cx - rr * 0.45f, artesa.top - rr * 0.6f), Size(rr * 0.9f, rr * 0.7f))
        }
        // La artesa esmaltada mantequilla, con su borde cromado.
        val ap = Path().apply { addRoundRect(RoundRect(artesa, CornerRadius(w * 0.008f))) }
        planosRefugio(ap, l.c(P.MANTEQUILLA), l, -1f, 0.1f, 0.18f)
        cromoRefugio(Rect(artesa.left, artesa.top, artesa.right, artesa.top + 2.4f * u), l, brillo = 0.7f)
        for (j in 0 until 5) drawCircle(l.s(0.3f), 1.2f * u, Offset(x0 + (x1 - x0) * (0.12f + 0.19f * j), artesa.top + alto * 0.35f))
        // La lámpara de cultivo: dos cadenitas desde el faldón, la pantalla y el tubo encendido.
        val lc = g.cultivo
        val pw = w * 0.36f; val ph = w * 0.03f
        val lt = g.luz(lc.x, lc.y - ph, 0.03f)
        listOf(-0.3f, 0.3f).forEach { f ->
            val x = lc.x + pw * f
            var yy = falda; var k = 0
            while (yy < lc.y - ph && k < 40) { drawOval(lt.c(P.ACERO_OSC), Offset(x - 1.2f * u, yy), Size(2.4f * u, 3.2f * u), style = Stroke(0.8f * u)); yy += 2.8f * u; k++ }
        }
        val pantalla = poligonoRefugio(Offset(lc.x - pw * 0.4f, lc.y - ph), Offset(lc.x + pw * 0.4f, lc.y - ph), Offset(lc.x + pw * 0.5f, lc.y), Offset(lc.x - pw * 0.5f, lc.y))
        drawPath(pantalla, lt.c(P.TEAL))
        clipPath(pantalla) {
            drawRect(lt.b(0.35f), Offset(lc.x - pw * 0.5f, lc.y - ph), Size(pw * 0.3f, ph))
            drawRect(lt.s(0.25f), Offset(lc.x + pw * 0.2f, lc.y - ph), Size(pw * 0.3f, ph))
        }
        drawRect(lt.b(0.5f), Offset(lc.x - pw * 0.4f, lc.y - ph), Size(pw * 0.8f, 1f * u))
        val arde = if (g.a.oscuro) g.a.cultivo else 1f
        drawRoundRect(mezcla(mezcla(P.LAMPARA, Color.White, 0.35f), lt.c(P.LAMPARA), 1f - arde), Offset(lc.x - pw * 0.45f, lc.y - 1f * u), Size(pw * 0.9f, 3f * u), CornerRadius(1.5f * u))
        drawRect(P.LAMPARA.copy(alpha = 0.35f * arde), Offset(lc.x - pw * 0.48f, lc.y + 2f * u), Size(pw * 0.96f, 2f * u))
        // El rosa del tubo en las lechugas, también de día (luz propia).
        if (!g.a.oscuro) mancha(Offset(lc.x, artesa.top - w * 0.01f), w * 0.2f, P.LAMPARA.copy(alpha = 0.14f))
    }

    /** La regadera esmaltada coral, cortada por el borde izquierdo: el cuerpo, el asa de arriba, el pico largo con su alcachofa. */
    private fun DrawScope.regadera(g: MarcoRefugio, pisan: Float) {
        val w = g.w; val u = density
        val pie = pisan + w * 0.014f
        val an = w * 0.17f; val al = w * 0.155f
        val x0 = -w * 0.02f
        val r = Rect(x0, pie - al, x0 + an, pie)
        val l = g.luz(r.center, 0.07f)
        val hacia = if (g.a.oscuro) g.hacia(r.center.x, r.center.y) else -1f
        val cuerpo = Path().apply {
            moveTo(r.left + an * 0.06f, r.top + al * 0.2f)
            lineTo(r.right - an * 0.06f, r.top + al * 0.2f)
            lineTo(r.right, r.bottom - al * 0.06f)
            quadraticTo(r.right, r.bottom, r.right - an * 0.06f, r.bottom)
            lineTo(r.left + an * 0.06f, r.bottom)
            quadraticTo(r.left, r.bottom, r.left, r.bottom - al * 0.06f)
            close()
        }
        drawOval(l.s(0.5f), Offset(r.left, pie - 3f * u), Size(an * 1.2f, 6f * u))
        // El pico, que sale de abajo a la derecha y sube, con su alcachofa.
        val pico = Path().apply {
            moveTo(r.right - an * 0.1f, r.bottom - al * 0.25f)
            lineTo(r.right + an * 0.42f, r.top - al * 0.08f)
            lineTo(r.right + an * 0.36f, r.top - al * 0.15f)
            lineTo(r.right - an * 0.12f, r.bottom - al * 0.42f)
            close()
        }
        val alca = Offset(r.right + an * 0.42f, r.top - al * 0.12f)
        drawOval(l.c(mezcla(P.CORAL, P.BAQUELITA, 0.35f)), Offset(alca.x - an * 0.05f, alca.y - an * 0.07f), Size(an * 0.1f, an * 0.14f))
        drawPath(pico, l.c(mezcla(P.CORAL, P.BAQUELITA, 0.2f)))
        // El asa, arriba, de lado a lado.
        val asa = Path().apply { moveTo(r.left + an * 0.2f, r.top + al * 0.2f); cubicTo(r.left + an * 0.25f, r.top - al * 0.35f, r.right - an * 0.15f, r.top - al * 0.35f, r.right - an * 0.12f, r.top + al * 0.22f) }
        drawPath(asa, l.c(mezcla(P.CORAL, P.BAQUELITA, 0.3f)), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        drawPath(asa, l.b(0.35f), style = Stroke(1f * u))
        cilindroRefugio(cuerpo, l.c(P.CORAL), l, hacia, 0.6f)
        clipPath(cuerpo) {
            drawRect(l.c(P.VINILO), Offset(r.left, r.top + al * 0.5f), Size(an, al * 0.1f))
            drawRect(l.s(0.3f), Offset(r.left, r.top + al * 0.2f), Size(an, 2f * u))
            saltadoRefugio(Offset(r.right - an * 0.2f, r.bottom - al * 0.15f), an * 0.05f, 9870, l.c(Color(0xFF22282A)), l.b(0.4f))
            saltadoRefugio(Offset(r.left + an * 0.5f, r.top + al * 0.32f), an * 0.035f, 9871, l.c(Color(0xFF22282A)), l.b(0.4f))
        }
        drawOval(l.c(mezcla(P.CORAL, P.BAQUELITA, 0.5f)), Offset(r.left + an * 0.06f, r.top + al * 0.15f), Size(an * 0.88f, al * 0.1f))
        if (l.k > 0.06f) drawPath(bordeRefugio(cuerpo, -hacia * 1.4f * u, 0f), l.filo(0.8f))
    }

    /**
     * El taburete de cocina de la época, a la derecha: el asiento grueso de vinilo coral con su canto acolchado, el ribete cromado y
     * la cara de arriba vista un poco desde arriba; cuatro patas de tubo cromado abiertas (las de atrás, en la sombra), el aro de
     * los pies y los regatones de goma; encima, un disco en su funda, algo corrido.
     */
    private fun DrawScope.taburete(g: MarcoRefugio, pisan: Float) {
        val w = g.w; val u = density
        val pie = pisan + w * 0.022f
        val cx = w * 0.86f
        val ra = w * 0.1f
        val asiento = pie - w * 0.2f
        val grueso = w * 0.045f
        val l = g.luz(cx, asiento, 0.07f)
        val hacia = if (g.a.oscuro) g.hacia(cx, asiento) else -1f
        drawOval(l.s(0.5f), Offset(cx - ra * 1.2f + (if (g.a.oscuro) 0f else w * 0.015f), pie - 3f * u), Size(ra * 2.5f, 6f * u))
        // Las patas: de la orilla del asiento al suelo, abiertas; las de atrás, más cortas y en sombra.
        val patas = listOf(Triple(-0.5f, -0.78f, true), Triple(0.5f, 0.78f, true), Triple(-0.78f, -1.12f, false), Triple(0.78f, 1.12f, false))
        patas.forEach { (fa, fb, atras) ->
            val a = Offset(cx + ra * fa, asiento + grueso * 0.8f)
            val b = Offset(cx + ra * fb, if (atras) pie - w * 0.03f else pie - 1f * u)
            val lp = g.luz((a.x + b.x) / 2, (a.y + b.y) / 2, if (atras) 0.05f else 0.07f)
            drawLine(lp.c(if (atras) P.CROMO_OSC else P.CROMO), a, b, 3f * u, StrokeCap.Round)
            if (!atras) {
                drawLine(lp.b(0.85f), a + Offset(-1f * u, 0f), b + Offset(-1f * u, 0f), 0.8f * u)
                drawLine(lp.s(0.4f), a + Offset(1.1f * u, 0f), b + Offset(1.1f * u, 0f), 0.8f * u)
            }
            drawRoundRect(lp.c(P.BAQUELITA), Offset(b.x - 2.2f * u, b.y - 3f * u), Size(4.4f * u, 3.4f * u), CornerRadius(1.2f * u))
        }
        // El aro de los pies, a media altura: la mitad de delante, cromada.
        val aroY = pie - w * 0.075f
        val aro = Rect(cx - ra * 0.98f, aroY - w * 0.014f, cx + ra * 0.98f, aroY + w * 0.014f)
        drawArc(l.c(P.CROMO_OSC), 180f, 180f, false, aro.topLeft, aro.size, style = Stroke(1.8f * u))
        drawArc(l.c(P.CROMO), 0f, 180f, false, aro.topLeft, aro.size, style = Stroke(2.2f * u))
        drawArc(l.b(0.8f), 20f, 140f, false, aro.topLeft - Offset(0f, 0.8f * u), aro.size, style = Stroke(0.7f * u))
        // El asiento: el canto acolchado (un cilindro corto, abombado) y el ribete cromado de abajo.
        val canto = Rect(cx - ra, asiento, cx + ra, asiento + grueso)
        val cantoP = Path().apply { addRoundRect(RoundRect(canto, CornerRadius(grueso * 0.45f))) }
        drawPath(corridoRefugio(cantoP, -hacia * 1.4f * u, 2f * u), l.s(0.35f))
        cilindroRefugio(cantoP, l.c(P.CORAL), l, hacia, 0.5f)
        cromoRefugio(Rect(canto.left + 2f * u, canto.bottom - 3f * u, canto.right - 2f * u, canto.bottom - 0.6f * u), l, brillo = 0.75f)
        drawLine(l.s(0.3f), Offset(canto.left + grueso * 0.3f, canto.top + grueso * 0.45f), Offset(canto.right - grueso * 0.3f, canto.top + grueso * 0.45f), 0.8f * u)
        // La cara de arriba, vista un poco desde arriba, con el vivo del ribete y su brillo.
        val cara = Rect(cx - ra, asiento - w * 0.02f, cx + ra, asiento + w * 0.02f)
        drawOval(l.c(mezcla(P.CORAL, P.VINILO, 0.22f)), cara.topLeft, cara.size)
        drawOval(l.c(mezcla(P.CORAL, P.BAQUELITA, 0.25f)), cara.topLeft, cara.size, style = Stroke(1.2f * u))
        drawOval(l.b(0.32f), Offset(cara.left + ra * 0.25f, cara.top + 1.2f * u), Size(ra * 0.8f, cara.height * 0.4f))
        // Un disco en su funda encima, algo corrido hacia atrás.
        val d1 = Rect(cx - ra * 0.75f, cara.top - w * 0.006f, cx + ra * 0.55f, cara.top + w * 0.006f)
        drawRect(l.s(0.35f), d1.topLeft + Offset(1.4f * u, 2f * u), d1.size)
        drawRect(l.c(P.TEAL), d1.topLeft, d1.size)
        drawRect(l.c(P.MANTEQUILLA), Offset(d1.left + d1.width * 0.18f, d1.top), Size(d1.width * 0.12f, d1.height))
        drawRect(l.b(0.5f), d1.topLeft, Size(d1.width, 0.8f * u))
        if (l.k > 0.06f) drawPath(bordeRefugio(cantoP, -hacia * 1.4f * u, 0f), l.filo(0.8f))
    }
}
