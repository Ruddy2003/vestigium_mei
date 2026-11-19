package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * Lo de abajo del módulo de estiba de Espacio ([MuebleEstanteEspacio]): lo que se guarda en los huecos libres de las repisas
 * (la cinta gris, el cabo enrollado, el ovillo con su labor) y el pie (el zócalo con su rejilla del aire y su cinta, y el riel
 * de los estribos).
 */
internal object HuecosEstanteEspacio {
    private val P = PaletaEstanteEspacio

    // ---------------------------------------------------------------- los huecos libres

    /**
     * Lo del taller en lo que queda libre de la repisa [k], a la derecha de [oc]: según cuánto hueco haya, el rollo de cinta
     * gris, el cabo enrollado con su mosquetón y el ovillo con su labor y sus agujas. Cada repisa escoge lo suyo, para que no
     * se repita; el cabo elástico de la repisa los sujeta a todos.
     */
    fun DrawScope.huecoLibre(k: Int, r: Rect, oc: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio) {
        val u = density
        val libre = (r.right - oc) / u
        val y = r.bottom - 1.5f * u
        when {
            libre >= 160f -> if (k % 2 == 0) {
                rolloCinta(Offset(oc + 30f * u, y), 23f * u, a, l, k)
                ovillo(Offset(oc + 96f * u, y), 23f * u, a, l, k)
                cabo(Offset(r.right - 40f * u, y), 38f * u, a, l, k)
            } else {
                cabo(Offset(oc + 34f * u, y), 38f * u, a, l, k)
                rolloCinta(Offset(oc + 92f * u, y), 22f * u, a, l, k)
                ovillo(Offset(r.right - 38f * u, y), 22f * u, a, l, k)
            }
            libre >= 92f -> when (k % 3) {
                0 -> { rolloCinta(Offset(oc + 27f * u, y), 21f * u, a, l, k); cabo(Offset(oc + 72f * u, y), 34f * u, a, l, k) }
                1 -> { ovillo(Offset(oc + 28f * u, y), 21f * u, a, l, k); rolloCinta(Offset(oc + 72f * u, y), 17f * u, a, l, k) }
                else -> { cabo(Offset(oc + 26f * u, y), 34f * u, a, l, k); ovillo(Offset(oc + 68f * u, y), 18f * u, a, l, k) }
            }
            libre >= 50f -> when (k % 3) {
                0 -> rolloCinta(Offset((oc + r.right) / 2, y), 20f * u, a, l, k)
                1 -> ovillo(Offset((oc + r.right) / 2, y), 19f * u, a, l, k)
                else -> cabo(Offset((oc + r.right) / 2, y), 32f * u, a, l, k)
            }
        }
    }

    /** Un rollo de cinta gris de canto, con su cartón y el cabo de la cinta que se despega y flota. */
    private fun DrawScope.rolloCinta(pie: Offset, rad: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, semilla: Int) {
        val u = density
        val c = Offset(pie.x, pie.y - rad)
        contactoEspacio(Offset(pie.x + rad * 0.2f, pie.y), rad * 2.2f, 4f * u, a, 0.36f)
        drawCircle(a.sombra(if (a.lumbre) 0.35f else 0.22f), rad, c + Offset(4f * u, 2f * u))
        // La punta de la cinta, que se suelta del rollo y se queda al aire.
        val punta = Path().apply {
            moveTo(c.x + rad * 0.7f, c.y - rad * 0.7f)
            cubicTo(c.x + rad * 1.3f, c.y - rad * 1.3f, c.x + rad * 0.5f, c.y - rad * 1.9f, c.x + rad * 1.2f, c.y - rad * 2.2f)
        }
        drawPath(punta, a.luz(mezcla(P.ACERO, P.TELA, 0.3f), l), style = Stroke(rad * 0.5f, cap = StrokeCap.Butt))
        drawPath(corridoEspacio(punta, -rad * 0.12f, 0f), a.claro(0.25f), style = Stroke(0.8f * u))
        drawCircle(a.luz(mezcla(P.ACERO, P.TELA, 0.3f), l), rad, c)
        for (j in 1 until 4) drawCircle(a.sombra(0.12f), rad * (0.62f + 0.11f * j), c, style = Stroke(0.5f * u))
        drawCircle(a.luz(mezcla(P.TRIGO, P.TELA, 0.35f), l), rad * 0.6f, c)
        drawCircle(a.luz(mezcla(P.CASCO_OSC, P.TINTA, 0.5f), l), rad * 0.48f, c)
        drawArc(a.claro(0.45f), 190f, 90f, false, Offset(c.x - rad * 0.92f, c.y - rad * 0.92f), Size(rad * 1.84f, rad * 1.84f), style = Stroke(1.2f * u))
        drawArc(a.sombra(0.3f), 10f, 90f, false, Offset(c.x - rad * 0.95f, c.y - rad * 0.95f), Size(rad * 1.9f, rad * 1.9f), style = Stroke(1.4f * u))
        if (a.lumbre) drawArc(a.filo(0.5f * l.k), 280f, 80f, false, Offset(c.x - rad, c.y - rad), Size(rad * 2, rad * 2), style = Stroke(1f * u))
    }

    /** Un cabo de cinta naranja enrollado, con su mosquetón de aluminio que asoma arriba. */
    private fun DrawScope.cabo(pie: Offset, alto: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, semilla: Int) {
        val u = density
        val an = alto * 0.78f
        contactoEspacio(Offset(pie.x + an * 0.1f, pie.y), an * 1.2f, 4f * u, a, 0.36f)
        drawOval(a.sombra(if (a.lumbre) 0.35f else 0.2f), Offset(pie.x - an / 2 + 4f * u, pie.y - alto * 0.8f + 2f * u), Size(an, alto * 0.8f))
        // El mosquetón: una D de aluminio con su gatillo, y la costura que lo une al cabo.
        val m = Offset(pie.x + an * 0.12f, pie.y - alto * 0.86f)
        drawRoundRect(a.luz(P.ACERO, l), Offset(m.x - an * 0.14f, m.y - alto * 0.34f), Size(an * 0.28f, alto * 0.38f), CornerRadius(an * 0.14f), style = Stroke(2f * u))
        drawLine(a.luz(mezcla(P.ACERO, P.TINTA, 0.3f), l), Offset(m.x + an * 0.14f, m.y - alto * 0.24f), Offset(m.x + an * 0.06f, m.y - alto * 0.02f), 1.4f * u)
        drawLine(a.claro(0.5f), Offset(m.x - an * 0.14f, m.y - alto * 0.24f), Offset(m.x - an * 0.14f, m.y - alto * 0.08f), 0.7f * u)
        // Las vueltas del cabo, de atrás adelante: óvalos de cinta, cada uno un poco corrido.
        for (j in 0 until 5) {
            val dy = j * alto * 0.035f
            val o = Rect(pie.x - an / 2 + j * 0.6f * u, pie.y - alto * 0.8f + dy, pie.x + an / 2 - j * 0.4f * u, pie.y - 1f * u)
            drawOval(a.luz(mezcla(P.NARANJA, if (j % 2 == 0) P.TELA else P.ROJO, 0.12f), l), o.topLeft, o.size, style = Stroke(3.2f * u))
            drawArc(a.claro(0.3f), 190f, 80f, false, o.topLeft, o.size, style = Stroke(0.8f * u))
        }
        drawLine(a.luz(P.TINTA, l).copy(alpha = 0.5f), Offset(pie.x - an * 0.2f, pie.y - alto * 0.78f), Offset(pie.x + an * 0.1f, pie.y - alto * 0.8f), 0.6f * u)
        if (a.lumbre) drawArc(a.filo(0.45f * l.k), 200f, 120f, false, Offset(pie.x - an / 2, pie.y - alto * 0.8f), Size(an, alto * 0.8f), style = Stroke(1f * u))
    }

    /** El ovillo de lana color vino, con sus dos agujas atravesadas y la labor empezada flotando, atada por su hebra. */
    private fun DrawScope.ovillo(pie: Offset, rad: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, semilla: Int) {
        val u = density
        val c = Offset(pie.x, pie.y - rad)
        val lana = mezcla(P.ROJO, P.TINTA, 0.32f)
        contactoEspacio(Offset(pie.x + rad * 0.15f, pie.y), rad * 2.2f, 4f * u, a, 0.36f)
        drawCircle(a.sombra(if (a.lumbre) 0.35f else 0.22f), rad, c + Offset(4f * u, 2f * u))
        // La labor: un paño chico de punto, al aire arriba a la derecha, con la hebra que la une al ovillo.
        val lab = Rect(c.x + rad * 0.4f, c.y - rad * 2.5f, c.x + rad * 1.5f, c.y - rad * 1.5f)
        val hebra = Path().apply { moveTo(c.x + rad * 0.5f, c.y - rad * 0.8f); cubicTo(c.x + rad * 1.2f, c.y - rad * 1.0f, c.x + rad * 0.3f, c.y - rad * 1.4f, lab.left + rad * 0.2f, lab.bottom) }
        drawPath(hebra, a.luz(lana, l), style = Stroke(0.8f * u))
        rotate(12f, lab.center) {
            drawRect(a.sombra(0.25f), lab.topLeft + Offset(2f * u, 2f * u), lab.size)
            drawRect(a.luz(mezcla(P.TELA, P.TRIGO, 0.2f), l), lab.topLeft, lab.size)
            val pv = maxOf(1f, 3f * u)
            var yy = lab.top + pv * 0.5f
            while (yy < lab.bottom) {
                var xx = lab.left + pv * 0.3f
                while (xx < lab.right - pv * 0.5f) {
                    drawLine(a.sombra(0.3f), Offset(xx, yy - pv * 0.4f), Offset(xx + pv * 0.4f, yy + pv * 0.3f), 0.5f * u)
                    drawLine(a.sombra(0.3f), Offset(xx + pv * 0.8f, yy - pv * 0.4f), Offset(xx + pv * 0.4f, yy + pv * 0.3f), 0.5f * u)
                    xx += pv
                }
                yy += pv
            }
            drawRect(a.luz(P.TELA_AZUL, l), Offset(lab.left, lab.bottom - lab.height * 0.22f), Size(lab.width, lab.height * 0.12f))
            drawLine(a.luz(mezcla(P.TRIGO, P.CASCO, 0.4f), l), Offset(lab.left - rad * 0.3f, lab.top + 1f * u), Offset(lab.right + rad * 0.3f, lab.top + 1f * u), 1.2f * u, StrokeCap.Round)
        }
        drawCircle(a.luz(lana, l), rad, c)
        // Las vueltas de la lana: arcos que cruzan el ovillo en tres direcciones.
        clipPath(Path().apply { addOval(Rect(c, rad)) }) {
            for (j in 0 until 6) {
                val g = 30f * j + 10f * azar(semilla + 80, j)
                rotate(g, c) {
                    drawOval(a.luz(mezcla(lana, P.TELA, 0.25f), l), Offset(c.x - rad * 1.1f, c.y - rad * (0.3f + 0.08f * j)), Size(rad * 2.2f, rad * (0.6f + 0.16f * j)), style = Stroke(0.8f * u))
                }
            }
            drawCircle(a.sombra(0.25f), rad, c + Offset(rad * 0.35f, rad * 0.35f), style = Stroke(rad * 0.5f))
        }
        drawOval(a.claro(0.3f), Offset(c.x - rad * 0.6f, c.y - rad * 0.65f), Size(rad * 0.5f, rad * 0.35f))
        // Las dos agujas, atravesadas.
        listOf(-0.55f to -1.15f, -0.2f to -1.3f).forEachIndexed { i, (dx, dy) ->
            val p0 = Offset(c.x - rad * 0.9f + i * rad * 0.2f, c.y + rad * 0.5f)
            val p1 = Offset(c.x + rad * (dx + 0.9f), c.y + rad * dy)
            drawLine(a.luz(mezcla(P.ACERO, P.TRIGO, 0.3f), l), p0, p1, 1.4f * u, StrokeCap.Round)
            drawCircle(a.luz(P.NARANJA, l), 1.6f * u, p1)
        }
        if (a.lumbre) drawArc(a.filo(0.45f * l.k), 280f, 90f, false, Offset(c.x - rad, c.y - rad), Size(rad * 2, rad * 2), style = Stroke(1f * u))
    }

    // ---------------------------------------------------------------- al pie

    /**
     * El pie del módulo: el zócalo de chapa con sus remaches, la rejilla del aire con la cinta que ondea (sin peso no hay
     * corrientes que suban: la cinta enseña que el aire se mueve), la tira de la luz de guardia y el riel con los dos estribos
     * de tela donde se meten los pies para trabajar sin flotar.
     */
    fun DrawScope.pie(b: Rect, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio) {
        val u = density; val w = size.width
        val zocalo = Rect(0f, b.top, w, b.top + b.height * 0.46f)
        val l = luz(Offset(w * 0.5f, zocalo.center.y))
        val tramos = if (a.lumbre) 20 else 1
        for (t in 0 until tramos) {
            val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
            drawRect(a.luz(P.CREMA, luz(Offset((xa + xb) / 2, zocalo.center.y))), Offset(xa, zocalo.top), Size(xb - xa, zocalo.height))
        }
        drawRect(a.sombra(0.35f), zocalo.topLeft, Size(w, 2.4f * u))
        drawRect(a.claro(0.35f), Offset(0f, zocalo.top + 2.4f * u), Size(w, 0.9f * u))
        drawRect(a.sombra(0.2f), Offset(0f, zocalo.bottom - 3f * u), Size(w, 3f * u))
        pinceladas(zocalo, a.sombra(1f), 981, 10, u, 0.05f)
        var x = w * 0.03f
        var j = 0
        while (x < w) {
            remacheEspacio(Offset(x, zocalo.top + zocalo.height * 0.3f), 1.2f * u, a.luz(mezcla(P.CREMA, P.ACERO, 0.35f), luz(Offset(x, zocalo.top))), a)
            if (azar(982, j) < 0.25f) drawOval(a.luz(P.ACERO, luz(Offset(x, zocalo.top))), Offset(x + 6f * u, zocalo.bottom - 6f * u), Size((4f + 6f * azar(983, j)) * u, 2.4f * u))
            x += maxOf(1f, w * 0.07f); j++
        }
        // La rejilla del aire, con su cinta que ondea hacia la derecha.
        val rej = Rect(w * 0.6f, zocalo.top + zocalo.height * 0.3f, w * 0.82f, zocalo.bottom - zocalo.height * 0.16f)
        val lr = luz(rej.center)
        drawRoundRect(a.luz(P.GOMA, lr), rej.topLeft, rej.size, CornerRadius(2f * u))
        var gx = rej.left + 3f * u
        while (gx < rej.right - 2f * u) {
            drawLine(a.luz(mezcla(P.CASCO, P.ACERO, 0.4f), lr), Offset(gx, rej.top + 1.5f * u), Offset(gx + 1.5f * u, rej.bottom - 1.5f * u), 1.2f * u)
            gx += maxOf(1f, 4f * u)
        }
        drawRect(a.claro(0.3f), Offset(rej.left, rej.top), Size(rej.width, 0.8f * u))
        val cinta = Path().apply {
            val y0 = rej.center.y - rej.height * 0.1f
            moveTo(rej.right - 2f * u, y0)
            cubicTo(rej.right + w * 0.04f, y0 - w * 0.03f, rej.right + w * 0.07f, y0 + w * 0.02f, rej.right + w * 0.13f, y0 - w * 0.02f)
        }
        drawPath(corridoEspacio(cinta, 2f * u, 3f * u), a.sombra(0.25f), style = Stroke(3f * u))
        drawPath(cinta, a.luz(P.ROJO, lr), style = Stroke(3f * u))
        drawPath(corridoEspacio(cinta, 0f, -1f * u), a.claro(0.35f), style = Stroke(0.7f * u))
        // El riel de los estribos y los dos estribos de tela, que se levantan un poco al no pesar.
        val ry = b.top + b.height * 0.6f
        val riel = Rect(0f, ry, w, ry + w * 0.026f)
        val lriel = luz(riel.center)
        listOf(0.24f to P.TELA, 0.44f to mezcla(P.TELA_AZUL, P.TELA, 0.25f)).forEachIndexed { i, (fx, col) ->
            val x0 = w * fx; val an = w * 0.14f
            val alto = w * (0.05f + 0.012f * i)
            val lazo = Path().apply {
                moveTo(x0, riel.center.y); cubicTo(x0 - an * 0.05f, riel.top - alto, x0 + an * 0.25f, riel.top - alto * 1.15f, x0 + an * 0.5f, riel.top - alto * 1.1f)
                cubicTo(x0 + an * 0.78f, riel.top - alto * 1.05f, x0 + an * 1.05f, riel.top - alto, x0 + an, riel.center.y)
            }
            val g = w * 0.019f
            drawPath(corridoEspacio(lazo, 3f * u, 3f * u), a.sombra(0.3f), style = Stroke(g))
            drawPath(lazo, a.luz(col, luz(Offset(x0, riel.top))), style = Stroke(g))
            drawPath(corridoEspacio(lazo, 0f, -g * 0.3f), a.claro(0.3f), style = Stroke(0.8f * u))
            drawPath(corridoEspacio(lazo, 0f, g * 0.3f), a.sombra(0.2f), style = Stroke(0.6f * u))
            // Las hebillas que lo sujetan al riel.
            listOf(x0, x0 + an).forEach { x -> drawRoundRect(a.luz(P.ACERO, luz(Offset(x, riel.top))), Offset(x - g * 0.8f, riel.top - g * 0.5f), Size(g * 1.6f, g * 1.4f), CornerRadius(1f * u)) }
        }
        drawRect(a.sombra(0.4f), Offset(0f, riel.bottom), Size(w, 3f * u))
        drawRect(a.luz(P.ACERO, lriel), riel.topLeft, riel.size)
        drawRect(a.claro(0.4f), riel.topLeft, Size(w, 0.9f * u))
        drawRect(a.sombra(0.5f), Offset(0f, riel.center.y - 0.8f * u), Size(w, 1.6f * u))
        var hx = w * 0.02f
        while (hx < w) { drawCircle(a.sombra(0.55f), 1.8f * u, Offset(hx, riel.center.y)); hx += maxOf(1f, 12f * u) }
    }
}
