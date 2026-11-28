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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Una válvula de vacío de pie en [r] (la reliquia del fanal y de los relicarios): el zócalo de baquelita con su guía, la ampolla de
 * vidrio con su punta, el espejo plateado del getter arriba, las dos micas, la placa gris con sus nervios y su ranura, los hilos
 * de la rejilla, y el vidrio con su canto y sus brillos. [c] da el color de cada cosa con la luz que le toca (y [f], más clara o
 * más honda); [k] es la luz de vela que le llega.
 */
internal fun DrawScope.valvulaOrden(r: Rect, c: (Color, Float) -> Color, a: AireOrden, k: Float) {
    val u = density
    val p = PaletaOrden
    if (r.width < 2f || r.height < 4f) return
    val zh = r.height * 0.17f
    val zo = Rect(r.left + r.width * 0.14f, r.bottom - zh, r.right - r.width * 0.14f, r.bottom)
    val bq = c(p.BAQUELITA, 0f)
    redondoOrden(zo, bq, mezcla(bq, p.LUZ_FRIA, if (a.oscuro) 0.08f else 0.3f), mezcla(bq, p.NOCHE, 0.45f), -1f)
    drawRect(c(p.LATON, 0f), Offset(zo.left, zo.top), Size(zo.width, maxOf(1f, zh * 0.12f)))
    val amp = Rect(r.left, r.top + r.height * 0.03f, r.right, zo.top + 1f)
    val ampolla = Path().apply {
        moveTo(amp.left, amp.bottom); lineTo(amp.left, amp.top + amp.width * 0.5f)
        cubicTo(amp.left, amp.top + amp.width * 0.06f, amp.right, amp.top + amp.width * 0.06f, amp.right, amp.top + amp.width * 0.5f)
        lineTo(amp.right, amp.bottom); close()
    }
    // Por dentro: el aire del vidrio, el espejo del getter arriba y su borde.
    drawPath(ampolla, c(Color(0xFF3A4048), 0f).copy(alpha = 0.3f))
    val corte = amp.top + amp.width * 0.44f
    val casquete = Path().apply {
        moveTo(amp.left, corte); lineTo(amp.left, amp.top + amp.width * 0.5f)
        cubicTo(amp.left, amp.top + amp.width * 0.06f, amp.right, amp.top + amp.width * 0.06f, amp.right, amp.top + amp.width * 0.5f)
        lineTo(amp.right, corte); close()
    }
    drawPath(casquete, c(Color(0xFFC2C8D0), 0.1f))
    drawRect(c(Color(0xFF6A7078), 0f), Offset(amp.left, corte - amp.width * 0.1f), Size(amp.width, amp.width * 0.1f))
    drawRect(a.brillo(0.6f, maxOf(k, 0.3f)), Offset(amp.left + amp.width * 0.22f, amp.top + amp.width * 0.18f), Size(amp.width * 0.12f, amp.width * 0.18f))
    // La placa: gris, con su luz a la izquierda, dos nervios y la ranura del medio.
    val pl = Rect(amp.left + amp.width * 0.2f, amp.top + amp.height * 0.38f, amp.right - amp.width * 0.2f, amp.bottom - amp.height * 0.15f)
    val gris = c(Color(0xFF4E535B), 0f)
    redondoOrden(pl, gris, mezcla(gris, p.LUZ_FRIA, if (a.oscuro) 0.08f else 0.35f), mezcla(gris, p.NOCHE, 0.45f), -1f, 0.8f)
    listOf(0.3f, 0.7f).forEach { f -> drawLine(mezcla(gris, p.NOCHE, 0.35f), Offset(pl.left + pl.width * f, pl.top + 1f * u), Offset(pl.left + pl.width * f, pl.bottom - 1f * u), 0.7f * u) }
    drawRect(mezcla(gris, p.NOCHE, 0.6f), Offset(pl.center.x - 0.8f * u, pl.top + pl.height * 0.14f), Size(1.6f * u, pl.height * 0.72f))
    // Las dos micas, arriba y abajo de la placa.
    val mica = c(Color(0xFFE8E2D2), 0f).copy(alpha = 0.9f)
    listOf(pl.top - maxOf(1.6f * u, amp.height * 0.025f), pl.bottom).forEach { y ->
        drawRect(mica, Offset(amp.left + amp.width * 0.08f, y), Size(amp.width * 0.84f, maxOf(1.4f * u, amp.height * 0.022f)))
    }
    // Los hilos de la rejilla a los lados de la placa y los que bajan al zócalo.
    val hilo = c(p.ACERO_CLARO, 0f).copy(alpha = 0.7f)
    listOf(0.12f, 0.88f).forEach { f -> drawLine(hilo, Offset(amp.left + amp.width * f, pl.top - amp.height * 0.03f), Offset(amp.left + amp.width * f, amp.bottom), 0.6f * u) }
    // El vidrio: su canto, el brillo largo de la izquierda, uno corto a la derecha y la punta de arriba.
    drawPath(ampolla, a.luz(p.VIDRIO, k).copy(alpha = 0.6f), style = Stroke(1f * u))
    drawLine(a.brillo(0.8f, maxOf(k, 0.3f)), Offset(amp.left + amp.width * 0.16f, amp.top + amp.width * 0.45f), Offset(amp.left + amp.width * 0.16f, amp.bottom - amp.height * 0.08f), maxOf(1.2f * u, amp.width * 0.07f), StrokeCap.Round)
    drawLine(a.brillo(0.4f, maxOf(k, 0.3f)), Offset(amp.right - amp.width * 0.14f, amp.top + amp.height * 0.4f), Offset(amp.right - amp.width * 0.14f, amp.top + amp.height * 0.6f), 0.8f * u, StrokeCap.Round)
    drawLine(c(p.VIDRIO, 0f), Offset(amp.center.x, amp.top + amp.width * 0.22f), Offset(amp.center.x, amp.top - r.height * 0.025f), maxOf(1.2f * u, amp.width * 0.06f), StrokeCap.Round)
}

/**
 * La pieza que manda en la corona de Orden: el fanal de vidrio con la reliquia. La base de acero en dos escalones con el aro de
 * latón, el cojín de terciopelo carmesí con sus borlas, la válvula de vacío de pie y la campana de vidrio modelada (el reflejo
 * ancho de la luminaria a la izquierda, el reflejo en diagonal, el canto hondo de la derecha, el vidrio más grueso abajo, el arco
 * de brillo y el pomo), con su sombra en el muro. De noche, el reflejo de la vela en el vidrio y su filo cálido en el canto que la
 * mira.
 */
internal object ReliquiaEstanteOrden {
    private val P = PaletaOrden
    private val M = MuebleEstanteOrden

    /** El color de [c] en [p], con la luz del momento: de día, con la mancha de la luminaria. */
    private fun DrawScope.en(c: Color, f: Float, p: Offset, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.4f): Color {
        val w = size.width
        return if (a.oscuro) { val b = a.luz(c, cerca(p), frio(p)); if (f < 0f) mezcla(b, P.NOCHE, -f * 0.45f) else if (f > 0f) mezcla(b, P.LLAMA, f * 0.25f * cerca(p)) else b }
        else a.luminaria(a.dia(c, f, cerca(p)), M.luminaria(p.x / w, p.y / w) * dia)
    }

    /** El centro del fanal, en anchos. */
    const val X = 0.43f

    fun DrawScope.fanal(w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val cx = w * X
        val zocalo = Rect(cx - w * 0.112f, apoyo - w * 0.022f, cx + w * 0.112f, apoyo)
        val aro = Rect(cx - w * 0.1f, zocalo.top - w * 0.014f, cx + w * 0.1f, zocalo.top + 1f)
        val cojin = Rect(cx - w * 0.084f, aro.top - w * 0.04f, cx + w * 0.084f, aro.top + w * 0.006f)
        val campana = Rect(cx - w * 0.096f, apoyo - w * 0.385f, cx + w * 0.096f, aro.top + w * 0.002f)
        val cam = Path().apply { addRoundRect(RoundRect(campana, CornerRadius(campana.width / 2, campana.width / 2))) }
        val k = cerca(Offset(cx + w * 0.06f, apoyo - w * 0.14f)); val fr = frio(Offset(cx, apoyo - w * 0.14f))
        val vela = a.oscuro && !a.dormida
        // Su sombra en el muro: la del vidrio, leve; la de la base y la reliquia, franca.
        clipRect(0f, 0f, w, apoyo + 1f) {
            if (!a.oscuro || a.dormida) {
                translate(w * 0.026f, w * 0.014f) {
                    drawPath(cam, a.sombra(0.11f))
                    drawRect(a.sombra(0.3f), Offset(cojin.left, cojin.top), Size(cojin.width, apoyo - cojin.top))
                    drawRect(a.sombra(0.22f), Offset(cx - w * 0.04f, campana.top + campana.height * 0.3f), Size(w * 0.08f, campana.height * 0.6f))
                }
            } else if (a.lumbre) {
                // De noche, la vela (a la derecha) tiende su sombra hacia la izquierda.
                val v = Offset(w * M.VELA_X, w * M.VELA_Y)
                val o = (Offset(cx, apoyo - w * 0.15f) - v) * 0.12f
                withTransform({ translate(o.x, o.y); scale(1.06f, 1.06f, Offset(cx, apoyo)) }) {
                    drawPath(cam, a.sombra(0.12f * k))
                    drawRect(a.sombra(0.3f * k), Offset(cojin.left, cojin.top), Size(cojin.width, apoyo - cojin.top))
                }
            }
        }
        contactoOrden(Offset(cx, apoyo), zocalo.width * 1.06f, w * 0.012f, a, 0.55f)
        // La base: el escalón de acero con su cara de arriba en escorzo y el aro de latón con su canto de luz.
        drawOval(en(P.PAVON_LUZ, 0.05f, zocalo.center, a, cerca, frio), Offset(zocalo.left, zocalo.top - w * 0.008f), Size(zocalo.width, w * 0.016f))
        redondoOrden(zocalo, en(P.PAVON, 0f, zocalo.center, a, cerca, frio), en(P.PAVON_LUZ, 0.05f, zocalo.center, a, cerca, frio), en(P.PAVON_HONDO, 0f, zocalo.center, a, cerca, frio), if (vela) 1f else -1f, 0.7f)
        drawRect(a.sombra(0.45f), Offset(zocalo.left, zocalo.bottom - 1.6f * u), Size(zocalo.width, 1.6f * u))
        drawOval(en(P.LATON_BRILLO, 0f, aro.center, a, cerca, frio), Offset(aro.left, aro.top - w * 0.006f), Size(aro.width, w * 0.012f))
        redondoOrden(aro, en(P.LATON, 0f, aro.center, a, cerca, frio), en(P.LATON_BRILLO, 0f, aro.center, a, cerca, frio), en(P.LATON_HONDO, 0f, aro.center, a, cerca, frio), if (vela) 1f else -1f, 0.95f)
        drawRect(a.brillo(0.6f, maxOf(k, 0.3f)), Offset(aro.left, aro.top + 0.6f * u), Size(aro.width, 1.1f * u))
        drawRect(a.sombra(0.4f), Offset(aro.left, aro.bottom - 1.4f * u), Size(aro.width, 1.4f * u))
        // El cojín de terciopelo carmesí: panzón, con su luz arriba a la izquierda, su costura y sus borlas.
        val cj = Path().apply { addRoundRect(RoundRect(cojin, CornerRadius(cojin.height * 0.5f, cojin.height * 0.42f))) }
        drawPath(cj, en(P.GRANA, 0f, cojin.center, a, cerca, frio))
        clipPath(cj) {
            drawOval(en(P.GRANA_LUZ, 0f, cojin.center, a, cerca, frio).copy(alpha = 0.55f), Offset(cojin.left + cojin.width * 0.08f, cojin.top + cojin.height * 0.06f), Size(cojin.width * 0.5f, cojin.height * 0.42f))
            drawRect(en(P.GRANA_HONDA, 0f, cojin.center, a, cerca, frio).copy(alpha = 0.7f), Offset(cojin.left, cojin.bottom - cojin.height * 0.32f), Size(cojin.width, cojin.height * 0.32f))
            drawRect(en(P.GRANA_HONDA, 0f, cojin.center, a, cerca, frio).copy(alpha = 0.45f), Offset(cojin.right - cojin.width * 0.2f, cojin.top), Size(cojin.width * 0.2f, cojin.height))
            drawLine(en(P.LATON, 0f, cojin.center, a, cerca, frio).copy(alpha = 0.75f), Offset(cojin.left + 3f * u, cojin.center.y + cojin.height * 0.12f), Offset(cojin.right - 3f * u, cojin.center.y + cojin.height * 0.12f), 0.9f * u)
            if (vela) drawRect(P.LLAMA.copy(alpha = 0.3f * k), Offset(cojin.right - cojin.width * 0.25f, cojin.top), Size(cojin.width * 0.25f, cojin.height * 0.5f))
        }
        listOf(cojin.left + 2.5f * u, cojin.right - 2.5f * u).forEach { x ->
            val b0 = Offset(x, cojin.center.y + cojin.height * 0.12f)
            val bor = poligonoOrden(b0.x - w * 0.0035f, b0.y, b0.x + w * 0.0035f, b0.y, b0.x + w * 0.007f, b0.y + w * 0.028f, b0.x - w * 0.007f, b0.y + w * 0.028f)
            drawPath(bor, en(P.LATON, 0f, b0, a, cerca, frio))
            drawLine(en(P.LATON_HONDO, 0f, b0, a, cerca, frio), Offset(b0.x + w * 0.002f, b0.y + w * 0.007f), Offset(b0.x + w * 0.003f, b0.y + w * 0.028f), 0.7f * u)
            drawCircle(en(P.LATON_HONDO, 0f, b0, a, cerca, frio), w * 0.0045f, b0)
        }
        // La reliquia: la válvula de pie sobre el cojín.
        val vr = Rect(cx - w * 0.044f, cojin.top + w * 0.006f - w * 0.235f, cx + w * 0.044f, cojin.top + w * 0.008f)
        valvulaOrden(vr, { col, f -> en(col, f, vr.center, a, cerca, frio) }, a, k)
        // La campana de vidrio: casi transparente, con el muro detrás un punto más frío; sus reflejos y su canto.
        drawPath(cam, a.luz(P.VIDRIO, k * 0.5f, fr).copy(alpha = if (a.oscuro) 0.11f else 0.16f))
        clipPath(cam) {
            val luzV = when {
                a.dormida -> P.GUARDIA.copy(alpha = 0.3f)
                a.oscuro -> P.LLAMA.copy(alpha = 0.08f + 0.18f * k)
                a.noche -> P.VIGILIA.copy(alpha = 0.42f)
                else -> P.LUZ_FRIA.copy(alpha = 0.5f)
            }
            // El reflejo ancho de la izquierda, con su orilla más viva.
            drawRect(luzV.copy(alpha = luzV.alpha * 0.55f), Offset(campana.left + campana.width * 0.07f, campana.top), Size(campana.width * 0.17f, campana.height))
            drawRect(luzV, Offset(campana.left + campana.width * 0.1f, campana.top + campana.width * 0.2f), Size(campana.width * 0.06f, campana.height * 0.75f))
            // El reflejo en diagonal, de arriba a la izquierda hacia abajo a la derecha.
            val d0 = campana.top + campana.height * 0.12f
            drawPath(poligonoOrden(campana.left + campana.width * 0.3f, d0, campana.left + campana.width * 0.42f, d0,
                campana.right, d0 + campana.height * 0.46f, campana.right, d0 + campana.height * 0.6f), luzV.copy(alpha = luzV.alpha * 0.4f))
            drawPath(poligonoOrden(campana.left + campana.width * 0.46f, d0 + campana.height * 0.02f, campana.left + campana.width * 0.5f, d0 + campana.height * 0.02f,
                campana.right, d0 + campana.height * 0.42f, campana.right, d0 + campana.height * 0.47f), luzV.copy(alpha = luzV.alpha * 0.6f))
            // El canto hondo de la derecha (el vidrio de canto) y su filo claro.
            drawRect(a.sombra(0.2f), Offset(campana.right - campana.width * 0.13f, campana.top), Size(campana.width * 0.13f, campana.height))
            drawRect(a.brillo(0.35f, maxOf(k, 0.25f)), Offset(campana.right - campana.width * 0.045f, campana.top + campana.width * 0.4f), Size(campana.width * 0.02f, campana.height * 0.5f))
            // Abajo, el vidrio más grueso junto a la base.
            drawRect(a.luz(mezcla(P.VIDRIO, P.ACERO_CLARO, 0.3f), k, fr).copy(alpha = 0.35f), Offset(campana.left, campana.bottom - w * 0.012f), Size(campana.width, w * 0.012f))
            // De noche, el reflejo de la vela en el lado que la mira: una mancha larga y la llamita.
            if (vela) {
                val rx = campana.right - campana.width * 0.2f
                drawRect(P.LLAMA.copy(alpha = 0.18f + 0.3f * k), Offset(rx - campana.width * 0.03f, campana.top + campana.height * 0.22f), Size(campana.width * 0.06f, campana.height * 0.5f))
                drawOval(P.CORAZON.copy(alpha = 0.85f), Offset(rx - w * 0.004f, campana.top + campana.height * 0.36f), Size(w * 0.008f, w * 0.016f))
            }
        }
        drawPath(cam, a.luz(mezcla(P.VIDRIO, P.ACERO_CLARO, 0.35f), maxOf(k, 0.35f), fr).copy(alpha = if (a.oscuro) 0.65f else 0.7f), style = Stroke(2f * u))
        if (vela) clipRect(cx, campana.top, campana.right + 2f * u, campana.bottom) { drawPath(cam, P.LLAMA.copy(alpha = (0.75f * k).coerceAtMost(0.7f)), style = Stroke(1.6f * u)) }
        drawArc(a.brillo(0.85f, maxOf(k, 0.3f)), 196f, 64f, false, Offset(campana.left + campana.width * 0.1f, campana.top + campana.width * 0.1f), Size(campana.width * 0.8f, campana.width * 0.8f), style = Stroke(2f * u, cap = StrokeCap.Round))
        // El pomo de vidrio de arriba, con su cuello.
        val pomo = Offset(cx, campana.top - w * 0.009f)
        drawRect(a.luz(P.VIDRIO, k, fr).copy(alpha = 0.6f), Offset(cx - w * 0.005f, campana.top - w * 0.004f), Size(w * 0.01f, w * 0.006f))
        drawCircle(a.sombra(0.25f), w * 0.011f, pomo + Offset(w * 0.004f, w * 0.004f))
        drawCircle(a.luz(P.VIDRIO, k, fr).copy(alpha = 0.6f), w * 0.011f, pomo)
        drawCircle(a.luz(mezcla(P.VIDRIO, P.ACERO_CLARO, 0.35f), k, fr), w * 0.011f, pomo, style = Stroke(1.2f * u))
        drawCircle(a.brillo(0.9f, maxOf(k, 0.3f)), w * 0.0035f, pomo - Offset(w * 0.004f, w * 0.004f))
        // El canto de abajo de la campana, donde asienta en el aro.
        drawRect(a.luz(mezcla(P.VIDRIO, P.ACERO_CLARO, 0.45f), k, fr).copy(alpha = 0.8f), Offset(campana.left, campana.bottom - w * 0.005f), Size(campana.width, w * 0.005f))
    }
}
