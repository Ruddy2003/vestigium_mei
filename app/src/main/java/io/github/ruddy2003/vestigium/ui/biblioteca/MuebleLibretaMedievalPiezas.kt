package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/**
 * Lo que rodea al facistol en la libreta de Scriptorium ([MuebleLibretaMedieval]): la percha de roble con la bolsa de cuero
 * de llevar el libro y las velas de repuesto colgadas por la mecha; el candelero de hierro de pie con su vela (la luz de la
 * noche); y al pie, los cuadernillos que todavía no se han cosido al códice, atados con un cordel.
 */
internal object PiezasLibretaMedieval {
    private val P = PaletaMedieval
    private fun cerca(p: Offset, s: SitioLibretaMedieval, a: AireMedieval) = MuebleLibretaMedieval.cerca(p, s, a)

    /** Hacia dónde cae la sombra de lo que cuelga del muro: de día, abajo a la derecha; de noche, lejos de la vela. */
    private fun caida(s: SitioLibretaMedieval, a: AireMedieval) = if (a.oscuro) Offset(-4f * s.u, 3f * s.u) else Offset(4f * s.u, 4f * s.u)

    // ---------------------------------------------------------------- la percha

    /** La percha: el listón de roble clavado al muro con sus estaquillas, y lo que cuelga de ella. */
    fun DrawScope.percha(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val r = Rect(s.pilar.right - 2f * u, s.percha - 3.5f * u, s.w + 2f * u, s.percha + 3.5f * u)
        val d = caida(s, a)
        drawRect(a.sombra(0.3f), r.topLeft + d, r.size)
        drawRect(a.luz(P.NOGAL_LUZ, cerca(Offset(s.w * 0.7f, s.percha), s, a) * 0.9f), r.topLeft, r.size)
        if (a.lumbre) drawRect(Brush.horizontalGradient(listOf(a.sombra(0.5f), Color.Transparent), r.left, s.llama.x), r.topLeft, r.size)
        vetas(r, a.luz(P.NOGAL_OSC, 0.5f).copy(alpha = 0.35f), 3301, densidad = 1f, grosor = 0.5f)
        drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 1.2f * u))
        drawRect(a.frio(0.4f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        // Las estaquillas: la bolsa a la izquierda del facistol, las velas a la derecha y alguna libre.
        // La bolsa entre el pilar y el facistol; las velas de repuesto a la derecha del candelero, si hay sitio.
        val izq = (s.pilar.right + s.tablero.left) / 2
        val der = (s.candelero.x + s.w) / 2
        val hayVelas = s.w - s.candelero.x > 52f * u
        val libres = listOf(s.tablero.left + 14f * u, s.tablero.right - 12f * u)
        for (x in listOf(izq) + libres) estaquilla(Offset(x, s.percha), s, a)
        if (hayVelas) estaquilla(Offset(der, s.percha), s, a)
        bolsa(Offset(izq, s.percha + 1f * u), minOf(34f * u, (s.tablero.left - s.pilar.right) * 0.68f), s, a)
        if (hayVelas) velas(Offset(der, s.percha + 1f * u), s, a)
        for (y in listOf(r.center.y)) clavoMedieval(Offset(s.pilar.right + 7f * u, y), 1.6f * u, a.luz(P.HIERRO, 0.3f), a)
    }

    /** Una estaquilla de roble metida en la percha, vista de frente: la cabeza redonda con su luz. */
    private fun DrawScope.estaquilla(p: Offset, s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val k = cerca(p, s, a)
        drawCircle(a.sombra(0.35f), 3f * u, p + Offset(1f * u, 1.4f * u))
        drawCircle(a.luz(P.NOGAL, k), 3f * u, p)
        drawCircle(a.claro(0.35f), 1.1f * u, p - Offset(0.9f * u, 0.9f * u))
    }

    /**
     * La bolsa de cuero de llevar el libro, colgada por su correa: el cuerpo blando que se ensancha abajo, la solapa con su
     * costura y el pasador de hueso, la luz de un lado y la sombra que deja en el muro.
     */
    private fun DrawScope.bolsa(clavo: Offset, an: Float, s: SitioLibretaMedieval, a: AireMedieval) {
        if (an < 14f * s.u) return
        val u = s.u
        val top = clavo.y + s.h * 0.11f
        val alto = an * 1.15f
        val c = Offset(clavo.x, top + alto / 2)
        val k = cerca(c, s, a)
        val cuero = a.luz(mezcla(P.ALMAGRE, P.CUERO, 0.35f), k)
        val cuerpo = Path().apply {
            moveTo(c.x - an * 0.42f, top)
            lineTo(c.x + an * 0.42f, top)
            cubicTo(c.x + an * 0.52f, top + alto * 0.4f, c.x + an * 0.58f, top + alto * 0.85f, c.x + an * 0.4f, top + alto)
            quadraticTo(c.x, top + alto * 1.06f, c.x - an * 0.44f, top + alto * 0.98f)
            cubicTo(c.x - an * 0.56f, top + alto * 0.8f, c.x - an * 0.5f, top + alto * 0.4f, c.x - an * 0.42f, top)
            close()
        }
        val d = caida(s, a)
        // La correa, la sombra y el cuerpo.
        val correa = Path().apply { moveTo(c.x - an * 0.36f, top + 2f * u); lineTo(clavo.x - 1f * u, clavo.y); lineTo(clavo.x + 1f * u, clavo.y); lineTo(c.x + an * 0.36f, top + 2f * u) }
        drawPath(corridoMedieval(correa, d.x, d.y), a.sombra(0.25f), style = Stroke(2f * u))
        drawPath(corridoMedieval(cuerpo, d.x * 1.3f, d.y), a.sombra(0.3f))
        drawPath(correa, mezcla(cuero, a.hondo, 0.2f), style = Stroke(2f * u))
        drawPath(cuerpo, cuero)
        drawPath(cuerpo, Brush.horizontalGradient(listOf(a.claro(0.22f), Color.Transparent, a.frio(0.35f)), c.x - an * 0.5f, c.x + an * 0.55f))
        // Los pliegues del cuero blando, abajo.
        val pliegues = Path().apply {
            moveTo(c.x - an * 0.15f, top + alto * 0.6f); quadraticTo(c.x - an * 0.2f, top + alto * 0.85f, c.x - an * 0.1f, top + alto * 1.0f)
            moveTo(c.x + an * 0.2f, top + alto * 0.55f); quadraticTo(c.x + an * 0.3f, top + alto * 0.8f, c.x + an * 0.22f, top + alto * 0.98f)
        }
        drawPath(pliegues, a.frio(0.3f), style = Stroke(1f * u))
        // La solapa con su costura y el pasador.
        val solapa = Path().apply {
            moveTo(c.x - an * 0.44f, top - 1f * u); lineTo(c.x + an * 0.44f, top - 1f * u)
            cubicTo(c.x + an * 0.42f, top + alto * 0.3f, c.x + an * 0.2f, top + alto * 0.46f, c.x, top + alto * 0.5f)
            cubicTo(c.x - an * 0.2f, top + alto * 0.46f, c.x - an * 0.42f, top + alto * 0.3f, c.x - an * 0.44f, top - 1f * u)
            close()
        }
        drawPath(corridoMedieval(solapa, 0.8f * u, 1.4f * u), a.sombra(0.35f))
        drawPath(solapa, mezcla(cuero, a.hondo, 0.08f))
        drawPath(solapa, a.claro(0.18f), style = Stroke(0.7f * u))
        drawLine(a.luz(P.BADANA, k), Offset(c.x, top + alto * 0.44f), Offset(c.x, top + alto * 0.62f), 2.2f * u, StrokeCap.Round)
        if (a.oscuro) drawPath(cuerpo, a.filo(0.25f * k), style = Stroke(0.8f * u))
    }

    /** Dos pares de velas de repuesto colgadas de la estaquilla por la mecha anudada, con su sombra en el muro. */
    private fun DrawScope.velas(clavo: Offset, s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val d = caida(s, a)
        val k = cerca(clavo, s, a)
        val cera = a.luz(mezcla(P.BADANA, Color(0xFFF2C46B), 0.25f), k)
        for ((i, dx) in listOf(-4.5f, 3.5f).withIndex()) {
            val largo = s.h * (0.19f + 0.03f * i)
            val top = clavo.y + 6f * u + i * 2f * u
            val r = Rect(clavo.x + dx - 2.4f * u, top, clavo.x + dx + 2.4f * u, top + largo)
            rotate(if (i == 0) 3f else -2f, Offset(clavo.x, clavo.y)) {
                drawRoundRect(a.sombra(0.25f), r.topLeft + d, r.size, CornerRadius(2f * u))
                drawLine(a.luz(P.NEGRO, k), Offset(clavo.x, clavo.y + 1f * u), Offset(r.center.x, r.top + 0.5f * u), 0.7f * u)
                drawRoundRect(cera, r.topLeft, r.size, CornerRadius(2.2f * u, 1.4f * u))
                drawRect(a.frio(0.25f), Offset(r.right - 1.6f * u, r.top + 1f * u), Size(1.6f * u, r.height - 2f * u))
                drawRect(a.claro(0.4f), Offset(r.left + 0.8f * u, r.top + 2f * u), Size(0.9f * u, r.height - 5f * u))
            }
        }
    }

    // ---------------------------------------------------------------- el candelero

    /**
     * El candelero de hierro de pie, detrás del facistol: las tres patas curvas, el vástago con sus nudos, el platillo que
     * recoge la cera y la vela gruesa clavada en su púa, con sus chorreones. De día, la mecha apagada; de noche, la cera se
     * enciende arriba y el platillo toma la luz (la llama va aparte, encima de todo).
     */
    fun DrawScope.candelero(s: SitioLibretaMedieval, a: AireMedieval) {
        val u = s.u
        val b = s.candelero
        val k = cerca(Offset(b.x, s.h * 0.5f), s, a)
        val hierro = a.luz(P.HIERRO, maxOf(k, 0.5f))
        val luz = a.luz(P.HIERRO_LUZ, maxOf(k, 0.5f))
        val plato = s.llama.y + 50f * u
        val nudo = s.h * 0.86f
        contactoMedieval(Offset(b.x + 4f * u, b.y + 1f * u), 40f * u, 6f * u, a, 0.34f)
        if (!a.oscuro) drawLine(a.sombra(0.16f), Offset(b.x + 9f * u, plato + 8f * u), Offset(b.x + 18f * u, s.suelo), 2.6f * u)
        val patas = Path()
        for (dx in listOf(-15f, 15f, 4f)) {
            patas.moveTo(b.x, nudo); patas.cubicTo(b.x + dx * 0.3f * u, nudo + 4f * u, b.x + dx * 0.8f * u, b.y - 6f * u, b.x + dx * u, b.y - (if (dx == 4f) -1f else 1f) * u)
        }
        drawPath(patas, hierro, style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawLine(hierro, Offset(b.x, nudo), Offset(b.x, plato), 2.8f * u)
        drawLine(luz, Offset(b.x - 0.8f * u, nudo), Offset(b.x - 0.8f * u, plato), 0.7f * u)
        if (a.oscuro) drawLine(a.filo(0.5f), Offset(b.x - 1f * u, plato + 4f * u), Offset(b.x - 1f * u, plato + s.h * 0.2f), 0.8f * u)
        for (y in listOf(nudo, (nudo + plato) / 2)) {
            drawOval(hierro, Offset(b.x - 4.5f * u, y - 3.2f * u), Size(9f * u, 6.4f * u))
            drawCircle(luz, 1.2f * u, Offset(b.x - 1.6f * u, y - 1.3f * u))
        }
        // La vela gruesa de cera, tibia, y sus chorreones.
        val cera = a.luz(mezcla(P.BADANA, Color(0xFFF2C46B), 0.35f), maxOf(k, 0.9f))
        val vela = Rect(b.x - 5.5f * u, s.llama.y + 3f * u, b.x + 5.5f * u, plato - 1f * u)
        drawRect(cera, vela.topLeft, vela.size)
        drawRect(Brush.horizontalGradient(listOf(a.claro(0.3f), Color.Transparent, a.frio(0.32f)), vela.left, vela.right), vela.topLeft, vela.size)
        if (a.lumbre) drawRect(Brush.verticalGradient(listOf(P.FUEGO.copy(alpha = 0.55f), Color.Transparent), vela.top, vela.top + 14f * u), vela.topLeft, Size(vela.width, 14f * u))
        val chorreones = Path().apply {
            moveTo(vela.left, vela.top + 2f * u); lineTo(vela.left - 1.2f * u, vela.top + 9f * u); lineTo(vela.left + 0.4f * u, vela.top + 13f * u)
            moveTo(vela.right - 2f * u, vela.top + 1f * u); lineTo(vela.right - 1.6f * u, vela.top + 17f * u)
        }
        drawPath(chorreones, cera, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawOval(mezcla(cera, P.LLAMA, if (a.lumbre) 0.4f else 0f), Offset(vela.left, vela.top - 1.5f * u), Size(vela.width, 3f * u))
        drawOval(hierro, Offset(b.x - 12f * u, plato - 3f * u), Size(24f * u, 6f * u))
        drawArc(if (a.lumbre) P.LLAMA.copy(alpha = 0.7f) else luz, 180f, 180f, false, Offset(b.x - 12f * u, plato - 3f * u), Size(24f * u, 6f * u), style = Stroke(0.9f * u))
        drawLine(a.luz(P.NEGRO, 1f), Offset(b.x, vela.top), Offset(b.x + 0.4f * u, vela.top - 3.4f * u), 1f * u)
    }

    /** La llama de la vela y su resplandor, encima de todo, con su propio color. */
    fun DrawScope.llama(s: SitioLibretaMedieval) {
        val p = s.llama
        val t = 11f * s.u
        drawCircle(P.FUEGO.copy(alpha = 0.25f), t * 1.4f, p - Offset(0f, t * 0.5f))
        val lengua = Path().apply {
            moveTo(p.x - t * 0.24f, p.y)
            cubicTo(p.x - t * 0.36f, p.y - t * 0.42f, p.x - t * 0.06f, p.y - t * 0.72f, p.x - t * 0.02f, p.y - t * 1.25f)
            cubicTo(p.x + t * 0.14f, p.y - t * 0.66f, p.x + t * 0.34f, p.y - t * 0.38f, p.x + t * 0.22f, p.y)
            close()
        }
        drawPath(lengua, Color(0xFFFF9A34).copy(alpha = 0.92f))
        drawCircle(Color(0xFFFFF4D8).copy(alpha = 0.9f), t * 0.16f, p - Offset(0f, t * 0.24f))
    }

    // ---------------------------------------------------------------- al pie

    /** Los cuadernillos que faltan por coser, en el suelo junto al pie del facistol: doblados, apilados y atados en cruz. */
    fun DrawScope.alPie(s: SitioLibretaMedieval, a: AireMedieval, sem: Int) {
        val u = s.u
        val izq = s.pilar.right + 6f * u
        val an = minOf(44f * u, s.eje - s.w * 0.11f - izq - 6f * u)
        if (an < 18f * u) return
        val base = s.h * 0.965f
        val k = cerca(Offset(izq + an / 2, base), s, a)
        contactoMedieval(Offset(izq + an * 0.55f, base + 1f * u), an * 1.15f, 6f * u, a, 0.34f)
        val perg = a.luz(P.PERGAMINO, k)
        for (j in 0 until 4) {
            val y = base - (j + 1) * 3.6f * u
            val dx = (azar(sem, 200 + j) - 0.5f) * 5f * u
            val r = Rect(izq + dx, y, izq + an + dx, y + 3.6f * u)
            drawRect(mezcla(perg, P.PERG_OSC, 0.25f * (j % 2)), r.topLeft, r.size)
            drawLine(a.frio(0.35f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.7f * u)
            drawLine(a.claro(0.4f), Offset(r.left, r.top + 0.5f * u), Offset(r.right, r.top + 0.5f * u), 0.6f * u)
        }
        // La cara de arriba, con la pauta asomando, y el cordel rojo en cruz.
        val tope = base - 4 * 3.6f * u
        val cara = poligonoMedieval(izq + 3f * u, tope - 5f * u, izq + an + 3f * u, tope - 5f * u, izq + an, tope, izq, tope)
        drawPath(cara, perg)
        drawLine(a.luz(P.RUBRICA, k).copy(alpha = 0.4f), Offset(izq + 6f * u, tope - 2.5f * u), Offset(izq + an - 4f * u, tope - 2.5f * u), 0.5f * u)
        val cordel = a.luz(P.RUBRICA, k)
        drawLine(cordel, Offset(izq + an * 0.55f, tope - 5f * u), Offset(izq + an * 0.55f - 1f * u, base), 1.1f * u)
        drawLine(cordel, Offset(izq + 1f * u, tope - 2f * u), Offset(izq + an + 2f * u, tope - 3f * u), 1.1f * u)
        drawCircle(cordel, 1.6f * u, Offset(izq + an * 0.55f, tope - 2.6f * u))
        drawLine(cordel, Offset(izq + an * 0.55f, tope - 2.6f * u), Offset(izq + an * 0.55f + 6f * u, tope + 4f * u), 0.9f * u)
    }
}
