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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/**
 * El objeto principal de arriba del estante de Scriptorium ([MuebleEstanteMedieval]): la prensa de encuadernar de roble, con
 * sus dos pies derechos acuñados en el travesaño, el husillo de madera con su barra y un libro cogido entre las tablas,
 * recién cosido y esperando sus tapas.
 */
internal object PrensaEstanteMedieval {
    private val P = PaletaMedieval

    /** La prensa, apoyada en [pie] (el centro de su base) y de [an] de ancho; [k], la luz del candil; [caida], su sombra. */
    fun DrawScope.prensa(pie: Offset, an: Float, a: AireMedieval, k: Float, caida: Offset) {
        val u = density
        val roble = a.luz(P.ROBLE, k)
        val robleOsc = a.luz(mezcla(P.ROBLE, P.NOGAL_OSC, 0.45f), k)
        val x0 = pie.x - an / 2; val x1 = pie.x + an / 2
        val base = Rect(x0, pie.y - an * 0.11f, x1, pie.y)
        val pieAncho = an * 0.13f
        val izq = Rect(x0 + an * 0.04f, pie.y - an * 0.86f, x0 + an * 0.04f + pieAncho, base.top)
        val der = Rect(x1 - an * 0.04f - pieAncho, izq.top, x1 - an * 0.04f, base.top)
        val trav = Rect(x0 - an * 0.02f, izq.top + an * 0.06f, x1 + an * 0.02f, izq.top + an * 0.2f)
        val platina = Rect(izq.right + 1f * u, pie.y - an * 0.5f, der.left - 1f * u, pie.y - an * 0.41f)
        val tornillo = Rect(pie.x - an * 0.065f, trav.top - an * 0.2f, pie.x + an * 0.065f, platina.top)
        // La sombra en el muro, con la forma de la prensa (pies, travesaño, husillo y barra) y su penumbra; de noche, del candil.
        sombraPrensa(izq, der, trav, tornillo, an, a, caida)
        contactoMedieval(Offset(pie.x + 3f * u, pie.y), an * 1.15f, 6f * u, a, 0.42f)
        // La base.
        tablon(base, roble, a, 1501, k)
        // El libro cogido entre la base y la platina.
        libro(Rect(pie.x - an * 0.3f, platina.bottom, pie.x + an * 0.3f, base.top), a, k)
        // Los pies derechos, que pasan por el travesaño y asoman arriba con su cuña.
        for (r in listOf(izq, der)) {
            drawRect(roble, r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, P.NOGAL_OSC.copy(alpha = 0.35f), 1502 + r.left.toInt(), densidad = 0.9f, vertical = true, grosor = 0.6f) }
            drawRect(a.claro(0.3f), r.topLeft, Size(2f * u, r.height))
            drawRect(a.frio(0.35f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            // La cuña que traba el pie sobre el travesaño.
            val cy = trav.top - an * 0.03f
            drawPath(poligonoMedieval(r.left - 3f * u, cy - 2f * u, r.right + 4f * u, cy - 1f * u, r.right + 4f * u, cy + 1.6f * u, r.left - 3f * u, cy + 2.6f * u), robleOsc)
            if (a.oscuro) drawRect(a.filo(0.5f * k), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
        }
        // La platina que aprieta.
        tablon(platina, roble, a, 1503, k)
        // El husillo: la rosca, que se ve entre el travesaño y la platina y por encima; la cabeza con su barra.
        husillo(tornillo, trav, a, k)
        tablon(trav, roble, a, 1504, k)
        // Los clavos de los ensambles.
        for (x in listOf(izq.center.x, der.center.x)) {
            clavoMedieval(Offset(x, trav.center.y), 1.4f * u, a.luz(P.HIERRO_LUZ, k), a, k)
            clavoMedieval(Offset(x, base.center.y), 1.3f * u, a.luz(P.HIERRO_LUZ, k), a, k)
        }
        barra(tornillo, an, a, k)
    }

    /** La sombra de la prensa en el muro: su silueta corrida, de color, con una penumbra más corrida y más tenue. */
    private fun DrawScope.sombraPrensa(izq: Rect, der: Rect, trav: Rect, tornillo: Rect, an: Float, a: AireMedieval, caida: Offset) {
        val u = density
        val color = if (a.oscuro) Color(0xFF14152A) else P.FRIO
        for ((paso, alfa) in listOf(1.35f to 0.1f, 1f to 0.2f)) {
            val c = Offset(caida.x * (if (a.oscuro) 1.1f else 2f) * paso, minOf(caida.y * paso, an * 0.03f))
            val sc = color.copy(alpha = if (a.oscuro) alfa * 1.6f else alfa)
            for (r in listOf(izq, der, trav)) drawRect(sc, r.topLeft + c, r.size)
            drawRect(sc, Offset(tornillo.left + tornillo.width * 0.15f, tornillo.top) + c, Size(tornillo.width * 0.7f, tornillo.height))
            val cabeza = Rect(tornillo.left - an * 0.025f, tornillo.top - an * 0.09f, tornillo.right + an * 0.025f, tornillo.top + an * 0.02f)
            drawRoundRect(sc, cabeza.topLeft + c, cabeza.size, CornerRadius(3f * u))
            rotate(-8f, cabeza.center + c) {
                drawLine(sc, cabeza.center + c + Offset(-an * 0.36f, 0f), cabeza.center + c + Offset(an * 0.36f, 0f), an * 0.05f, StrokeCap.Round)
            }
        }
    }

    /** Un tablón de roble: su cara, la veta, el canto de arriba con luz y el de abajo en sombra. */
    private fun DrawScope.tablon(r: Rect, color: Color, a: AireMedieval, semilla: Int, k: Float) {
        val u = density
        drawRect(color, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, P.NOGAL_OSC.copy(alpha = 0.35f), semilla, densidad = 0.8f, grosor = 0.6f) }
        drawRect(a.claro(0.35f), r.topLeft, Size(r.width, 1.2f * u))
        drawRect(a.frio(0.35f), Offset(r.left, r.bottom - r.height * 0.28f), Size(r.width, r.height * 0.28f))
        drawRect(a.frio(0.25f), Offset(r.right - r.width * 0.08f, r.top), Size(r.width * 0.08f, r.height))
        if (a.oscuro) drawRect(a.filo(0.5f * k), r.topLeft, Size(r.width, 1f * u))
    }

    /** El husillo de madera: el cilindro con la rosca en espiral, la luz a un lado y la sombra al otro. */
    private fun DrawScope.husillo(r: Rect, trav: Rect, a: AireMedieval, k: Float) {
        val u = density
        drawRect(a.luz(mezcla(P.ROBLE, P.NOGAL_LUZ, 0.3f), k), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + 1f * u
            while (y < r.bottom) {
                drawLine(a.frio(0.45f), Offset(r.left, y + 2.4f * u), Offset(r.right, y), 1.3f * u)
                drawLine(a.claro(0.3f), Offset(r.left, y + 1.2f * u), Offset(r.right - r.width * 0.4f, y + 0.4f * u), 0.7f * u)
                y += 3.6f * u
            }
            drawRect(a.claro(0.25f), Offset(r.left + r.width * 0.15f, r.top), Size(r.width * 0.15f, r.height))
            drawRect(a.frio(0.4f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
        }
        // El collar del husillo sobre la platina.
        drawRoundRect(a.luz(P.ROBLE, k), Offset(r.left - 2f * u, r.bottom - 4f * u), Size(r.width + 4f * u, 4f * u), CornerRadius(1.5f * u))
    }

    /** La cabeza del husillo, por encima del travesaño, con la barra que lo hace girar, un poco ladeada. */
    private fun DrawScope.barra(r: Rect, an: Float, a: AireMedieval, k: Float) {
        val u = density
        val cabeza = Rect(r.left - an * 0.025f, r.top - an * 0.09f, r.right + an * 0.025f, r.top + an * 0.02f)
        drawRoundRect(a.frio(0.25f), cabeza.topLeft + Offset(3f * u, 3f * u), cabeza.size, CornerRadius(3f * u))
        drawRoundRect(a.luz(P.ROBLE, k), cabeza.topLeft, cabeza.size, CornerRadius(3f * u))
        drawRect(a.claro(0.3f), Offset(cabeza.left + 2f * u, cabeza.top + 1f * u), Size(2f * u, cabeza.height - 2f * u))
        drawRect(a.frio(0.35f), Offset(cabeza.right - cabeza.width * 0.3f, cabeza.top), Size(cabeza.width * 0.3f, cabeza.height))
        val c = cabeza.center
        rotate(-8f, c) {
            val largo = an * 0.72f
            drawLine(a.frio(0.35f), c + Offset(-largo / 2 + 2f * u, 3f * u), c + Offset(largo / 2 + 2f * u, 3f * u), an * 0.05f, StrokeCap.Round)
            drawLine(a.luz(mezcla(P.ROBLE, P.NOGAL, 0.25f), k), c + Offset(-largo / 2, 0f), c + Offset(largo / 2, 0f), an * 0.05f, StrokeCap.Round)
            drawLine(a.claro(0.4f), c + Offset(-largo / 2, -an * 0.012f), c + Offset(largo / 2, -an * 0.012f), 0.9f * u, StrokeCap.Round)
            if (a.oscuro) drawLine(a.filo(0.6f * k), c + Offset(-largo / 2, -an * 0.014f), c + Offset(largo / 2, -an * 0.014f), 0.9f * u)
        }
    }

    /** El libro en la prensa, visto por la cola: las dos tablas de roble, el cuerpo de las hojas con sus cuadernillos y los nervios que asoman. */
    private fun DrawScope.libro(r: Rect, a: AireMedieval, k: Float) {
        val u = density
        val tabla = r.height * 0.16f
        drawRect(a.luz(mezcla(P.ROBLE, P.NOGAL, 0.35f), k), Offset(r.left, r.bottom - tabla), Size(r.width, tabla))
        drawRect(a.luz(mezcla(P.ROBLE, P.NOGAL, 0.35f), k), r.topLeft, Size(r.width, tabla))
        val hojas = Rect(r.left + 2f * u, r.top + tabla, r.right - 3f * u, r.bottom - tabla)
        drawRect(a.luz(P.PERG_OSC, k), hojas.topLeft, hojas.size)
        var y = hojas.top + 2f * u
        while (y < hojas.bottom) { drawLine(a.sombra(0.2f), Offset(hojas.left, y), Offset(hojas.right, y), 0.5f * u); y += 2.2f * u }
        drawRect(a.claro(0.3f), hojas.topLeft, Size(hojas.width * 0.6f, 1f * u))
        // El lomo, a la izquierda, con el hilo del cosido, y los nervios de cuero que asoman por el canto.
        drawRect(a.luz(P.BADANA, k), Offset(r.left - 3f * u, r.top + tabla * 0.6f), Size(5f * u, r.height - tabla * 1.2f))
        for (f in listOf(0.3f, 0.7f)) {
            val yy = hojas.top + hojas.height * f
            drawLine(a.luz(P.BADANA, k), Offset(r.left - 3f * u, yy), Offset(r.left - 9f * u, yy + 3f * u), 1.6f * u, StrokeCap.Round)
        }
        drawRect(a.frio(0.3f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
    }
}
