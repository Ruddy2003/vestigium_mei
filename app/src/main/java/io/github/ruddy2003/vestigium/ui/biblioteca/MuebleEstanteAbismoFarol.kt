package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble

/**
 * El farol del estante de Abismo ([MuebleEstanteAbismo]): el farol de barco del Ideario, aquí colgado del bao por su cadena, a la
 * derecha. De día, apagado; de noche, la luz del cuadro; en los Sueños oscuros, bajo.
 */
internal object FarolEstanteAbismo {
    private val P = PaletaAbismo

    /**
     * El farol de barco (el del Ideario), colgado del bao por su cadena: el gancho, la argolla, la chimenea y el sombrerete, el
     * cristal con sus costillas y su jaula, el depósito de latón. De día, apagado; de noche, encendido, y su luz por encima de lo que
     * tiene cerca: el halo cálido en la pared. En los Sueños oscuros arde bajo.
     */
    fun DrawScope.farolYLuz(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val cx = MuebleEstanteAbismo.FAROL_X * w
        val llamaY = MuebleEstanteAbismo.FAROL_Y * w
        val encendido = a.lumbre
        val k = if (a.oscuro) 1f else 0f
        val fw = w * 0.085f
        val sombT = llamaY - w * 0.105f; val sombB = llamaY - w * 0.075f
        val crT = sombB; val crB = llamaY + w * 0.04f
        val depB = crB + w * 0.035f
        val todo = Path().apply {
            addRect(Rect(cx - fw * 0.5f, sombT, cx + fw * 0.5f, depB))
            addOval(Rect(cx - fw * 0.15f, sombT - w * 0.03f, cx + fw * 0.15f, sombT))
        }
        if (!a.oscuro) with(MuroEstanteAbismo) { sombraEnPared(todo, Offset(cx, crT), apoyo, a, cerca, 0.18f) }
        // El gancho del bao, la cadena y la argolla.
        val gancho = Offset(cx, MuroEstanteAbismo.BAO * w)
        drawArc(a.dia(mezcla(P.PLOMO, P.OXIDO, 0.4f), 0f, 1f), 0f, 180f, false, Offset(cx - w * 0.008f, gancho.y - w * 0.002f), Size(w * 0.016f, w * 0.014f), style = Stroke(2f * u))
        var yc = gancho.y + w * 0.01f
        var j = 0
        while (yc < sombT - w * 0.035f) {
            val esl = if (j % 2 == 0) Rect(cx - w * 0.005f, yc, cx + w * 0.005f, yc + w * 0.016f) else Rect(cx - w * 0.0015f, yc, cx + w * 0.0015f, yc + w * 0.016f)
            drawOval(a.dia(P.LATON_OSC, 0f, maxOf(k, 0.4f)), esl.topLeft, esl.size, style = Stroke(1.4f * u))
            yc += w * 0.012f; j++
        }
        val arg = Rect(cx - w * 0.011f, sombT - w * 0.04f, cx + w * 0.011f, sombT - w * 0.016f)
        drawOval(a.dia(P.LATON, 0f, maxOf(k, 0.4f)), arg.topLeft, arg.size, style = Stroke(1.6f * u))
        // La chimenea y el sombrerete.
        val tl = if (a.oscuro) 0.9f else 1f
        val chim = Rect(cx - fw * 0.13f, sombT - w * 0.022f, cx + fw * 0.13f, sombT)
        drawRect(a.dia(P.LATON, 0f, tl), chim.topLeft, chim.size)
        drawRect(a.dia(mezcla(P.LATON, P.COBRE, 0.3f), 0f, tl), Offset(chim.left - 1.5f * u, chim.top), Size(chim.width + 3f * u, 2f * u))
        val somb = poligonoAbismo(cx - fw * 0.15f, sombT, cx + fw * 0.15f, sombT, cx + fw * 0.52f, sombB, cx - fw * 0.52f, sombB)
        drawPath(somb, a.dia(P.LATON, 0f, tl))
        clipPath(somb) {
            drawRect(if (encendido) P.LLAMA.copy(alpha = 0.18f) else luzMueble(0.32f), Offset(cx - fw * 0.5f, sombT), Size(fw * 0.3f, sombB - sombT))
            drawRect(a.sombra(0.28f), Offset(cx + fw * 0.15f, sombT), Size(fw * 0.4f, sombB - sombT))
        }
        // El cristal: de día, el vidrio con su reflejo; encendido, la llama.
        val cr = Rect(cx - fw * 0.42f, crT, cx + fw * 0.42f, crB)
        if (encendido) {
            val baja = a.oscuro && a.noche
            drawRoundRect(if (baja) Color(0xFFB9894E) else Color(0xFFFFD98E), cr.topLeft, cr.size, CornerRadius(fw * 0.12f))
            drawRect(P.LLAMA.copy(alpha = 0.6f), Offset(cr.left, cr.bottom - cr.height * 0.35f), Size(cr.width, cr.height * 0.35f))
            val lc = Offset(cx, llamaY)
            val esc = if (baja) 0.7f else 1f
            drawOval(Color(0xFFFFB24A), Offset(lc.x - fw * 0.1f * esc, lc.y - fw * 0.22f * esc), Size(fw * 0.2f * esc, fw * 0.36f * esc))
            drawOval(P.FUEGO, Offset(lc.x - fw * 0.05f * esc, lc.y - fw * 0.12f * esc), Size(fw * 0.1f * esc, fw * 0.22f * esc))
            if (!a.oscuro) drawRect(a.sombra(0.12f), cr.topLeft, cr.size)
        } else {
            drawRoundRect(mezcla(P.MAMPARO, P.VIDRIO, 0.25f), cr.topLeft, cr.size, CornerRadius(fw * 0.12f))
            drawRect(a.sombra(0.2f), Offset(cx + fw * 0.12f, cr.top), Size(fw * 0.3f, cr.height))
            drawRect(mezcla(P.TINTA, P.TECA, 0.3f), Offset(cx - fw * 0.03f, crB - cr.height * 0.3f), Size(fw * 0.06f, cr.height * 0.1f))
            drawRect(a.sombra(0.18f), Offset(cr.left, cr.top), Size(cr.width, cr.height * 0.18f))
            drawRect(luzMueble(0.65f), Offset(cx - fw * 0.3f, cr.top + 2f * u), Size(1.6f * u, cr.height * 0.7f))
        }
        listOf(-0.22f, 0f, 0.22f).forEach { fx -> drawLine(if (encendido) Color(0xFFB57A2E).copy(alpha = 0.45f) else a.sombra(0.15f), Offset(cx + fw * fx, cr.top), Offset(cx + fw * fx, cr.bottom), 0.8f * u) }
        listOf(-0.42f, -0.14f, 0.14f, 0.42f).forEach { fx -> drawLine(a.dia(mezcla(P.LATON, P.TINTA, 0.35f), 0f, tl), Offset(cx + fw * fx, cr.top), Offset(cx + fw * fx, cr.bottom), 1.1f * u) }
        listOf(0.35f, 0.7f).forEach { fy -> drawLine(a.dia(mezcla(P.LATON, P.TINTA, 0.35f), 0f, tl), Offset(cr.left - 1f * u, cr.top + cr.height * fy), Offset(cr.right + 1f * u, cr.top + cr.height * fy), 1.2f * u) }
        // El depósito de latón.
        val dep = Rect(cx - fw * 0.5f, crB, cx + fw * 0.5f, depB)
        drawRect(a.dia(P.LATON, 0f, tl), dep.topLeft, dep.size)
        clipRect(dep.left, dep.top, dep.right, dep.bottom) { cilindroAbismo(dep, a, if (a.oscuro) 0.9f else 1f, if (a.oscuro) 1f else -1f, 1.4f) }
        drawRect(a.dia(mezcla(P.LATON, P.COBRE, 0.3f), 0f, tl), Offset(dep.left - 1f * u, dep.top), Size(dep.width + 2f * u, 2f * u))
        if (encendido) drawRect(P.LLAMA.copy(alpha = 0.7f), Offset(dep.left, dep.top), Size(dep.width, 1.2f * u))
        drawOval(a.dia(P.VERDIN, 0f, tl).copy(alpha = 0.45f), Offset(dep.left + dep.width * 0.55f, dep.bottom - dep.height * 0.4f), Size(dep.width * 0.3f, dep.height * 0.3f))
        if (!a.oscuro) destelloAbismo(Offset(dep.left + dep.width * 0.25f, dep.top + dep.height * 0.35f), 1.2f * u, a, 1f)
        // La luz del farol, por encima de lo que tiene cerca.
        if (a.oscuro) {
            val f = if (a.noche) 0.38f else 1f
            val l = Offset(cx, llamaY)
            mancha(l + Offset(0f, w * 0.04f), w * 0.46f, P.LLAMA.copy(alpha = 0.09f * f))
            mancha(l + Offset(0f, w * 0.02f), w * 0.2f, P.LLAMA.copy(alpha = 0.2f * f))
            mancha(l, w * 0.06f, Color(0xFFFFE2A8).copy(alpha = 0.35f * f))
        } else if (a.noche) {
            mancha(Offset(cx, llamaY), w * 0.12f, P.LLAMA.copy(alpha = 0.16f))
        }
    }
}
