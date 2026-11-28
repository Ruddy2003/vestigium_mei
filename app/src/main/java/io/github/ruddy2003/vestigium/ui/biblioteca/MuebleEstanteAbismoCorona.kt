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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.cos
import kotlin.math.sin

/**
 * El bodegón de encima del armario de Abismo ([MuebleEstanteAbismo]). Manda la bomba de aire de dos volantes (la que da aire a la
 * escafandra del Ideario por la tubería de cobre), en [BombaEstanteAbismo]. A la izquierda, el cabo de vida de manila adujado en espiral, con la bola de vidrio de las redes en su
 * centro y la gaza que cae por la cornisa; a la derecha, bajo el farol, la caracola reina de Florida y la aceitera de la bomba; el
 * bichero apoyado en la curva. Delante, la barandilla de mar de la balda.
 */
internal object CoronaEstanteAbismo {
    private val P = PaletaAbismo

    /**
     * Lo de encima, de atrás adelante, y la barandilla. La luz de la escotilla no se repinta encima de las cosas: pinta la pared
     * (detrás) y, de la bomba, lo que mira hacia arriba (ver [BombaEstanteAbismo]).
     */
    fun DrawScope.encima(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        val w = size.width
        bichero(a, apoyo, cerca, m, 0f)
        aceitera(Offset(w * 0.805f, apoyo), a, cerca, m, 0f)
        with(BombaEstanteAbismo) { bomba(Offset(w * 0.56f, apoyo), a, apoyo, cerca, m) }
        cabo(Offset(w * 0.22f, apoyo), a, apoyo, cerca, m, 0f)
        caracola(Offset(w * 0.895f, apoyo), a, apoyo, cerca, m, 0f)
        barandilla(a, apoyo, cerca)
    }

    /** La sombra de algo en la pared de detrás (de día corrida a la derecha; de noche, hacia fuera del farol). */
    private fun DrawScope.sombraPared(p: Path, desde: Offset, apoyo: Float, a: AireAbismo, cerca: (Offset) -> Float, fuerza: Float = 0.2f, lit: Float) {
        if (lit > 0f) return
        with(MuroEstanteAbismo) { sombraEnPared(p, desde, apoyo, a, cerca, fuerza) }
    }

    // ---------------------------------------------------------------- el cabo, la bola, la caracola, la aceitera y el bichero

    /**
     * El cabo de vida de manila adujado en espiral plana sobre la balda (la aduja que se hace para que corra sin cocas): las
     * vueltas con su colchado, la bola de vidrio verde de las redes en su red de cordel, en el centro, y el gaza que cae por la
     * cornisa con su falcaceado.
     */
    private fun DrawScope.cabo(pie: Offset, a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val k = cerca(pie)
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        val rx = w * 0.09f; val ry = w * 0.03f
        val cc = Offset(pie.x, pie.y - ry - w * 0.004f)
        if (lit == 0f) contactoAbismo(Offset(pie.x + w * 0.01f, pie.y - w * 0.006f), rx * 2.2f, w * 0.022f, a, 0.4f)
        // La espiral: de fuera adentro, cada vuelta con su sombra abajo, su cara y su luz arriba.
        val g = w * 0.011f
        val esp = Path(); val tic = Path()
        val vueltas = 4.4f
        val n = 160
        for (i in 0..n) {
            val f = i / n.toFloat()
            val t = f * vueltas * 2 * Math.PI
            val rr = 1f - 0.78f * f
            val x = cc.x + rx * rr * cos(t).toFloat(); val y = cc.y + ry * rr * sin(t).toFloat()
            if (i == 0) esp.moveTo(x, y) else esp.lineTo(x, y)
            if (i % 2 == 0) { tic.moveTo(x - g * 0.3f, y - g * 0.35f); tic.lineTo(x + g * 0.25f, y + g * 0.3f) }
        }
        translate(0f, g * 0.35f) { drawPath(esp, a.sombra(0.45f), style = Stroke(g)) }
        drawPath(esp, col(P.MANILA_OSC), style = Stroke(g))
        drawPath(esp, col(P.MANILA), style = Stroke(g * 0.7f))
        drawPath(tic, col(P.MANILA_OSC).copy(alpha = 0.8f), style = Stroke(0.8f * u))
        translate(-g * 0.12f, -g * 0.22f) { drawPath(esp, a.brillo(0.35f, k), style = Stroke(g * 0.15f)) }
        // La bola de vidrio en su red, en el centro de la aduja.
        val br = w * 0.04f
        val bc = Offset(cc.x + w * 0.005f, cc.y - br * 0.75f)
        sombraPared(Path().apply { addOval(Rect(bc, br)) }, bc, apoyo, a, cerca, 0.16f, lit)
        drawOval(a.sombra(0.45f), Offset(bc.x - br * 0.8f, cc.y - br * 0.1f), Size(br * 1.8f, br * 0.45f))
        drawCircle(col(Color(0xFF2E6E56)), br, bc)
        drawCircle(col(Color(0xFF4E9A78), 0.05f), br * 0.78f, bc - Offset(br * 0.15f, br * 0.15f))
        drawCircle(col(Color(0xFF7BC0A0), 0.1f), br * 0.4f, bc - Offset(br * 0.38f, br * 0.38f))
        drawArc(a.sombra(0.4f), -10f, 160f, false, Offset(bc.x - br, bc.y - br), Size(br * 2, br * 2), style = Stroke(br * 0.18f))
        drawCircle(if (a.oscuro) P.LLAMA.copy(alpha = (0.9f * k).coerceAtMost(0.9f)) else luzMueble(0.95f), br * 0.12f, bc - Offset(br * 0.42f, br * 0.45f))
        // La red de cordel: meridianos y paralelos sobre la bola.
        val red = Path()
        listOf(-0.55f, 0f, 0.55f).forEach { f -> red.addOval(Rect(bc.x - br * kotlin.math.abs(f) - 0.5f, bc.y - br, bc.x + br * kotlin.math.abs(f) + 0.5f, bc.y + br)) }
        red.moveTo(bc.x - br * 0.95f, bc.y - br * 0.3f); red.quadraticTo(bc.x, bc.y - br * 0.05f, bc.x + br * 0.95f, bc.y - br * 0.3f)
        red.moveTo(bc.x - br * 0.9f, bc.y + br * 0.4f); red.quadraticTo(bc.x, bc.y + br * 0.65f, bc.x + br * 0.9f, bc.y + br * 0.4f)
        clipPath(Path().apply { addOval(Rect(bc, br)) }) { drawPath(red, col(P.ESPARTO), style = Stroke(1f * u)) }
        // El chicote que cae por la cornisa: la gaza con su falcaceado.
        val ch0 = Offset(cc.x + rx * 0.95f, cc.y + ry * 0.2f)
        val cae = Path().apply { moveTo(ch0.x, ch0.y); cubicTo(ch0.x + w * 0.02f, ch0.y + w * 0.01f, ch0.x + w * 0.01f, apoyo + w * 0.03f, ch0.x - w * 0.012f, apoyo + w * 0.06f) }
        if (lit == 0f) translate(w * 0.006f, w * 0.004f) { drawPath(cae, a.sombra(0.35f), style = Stroke(g)) }
        drawPath(cae, col(P.MANILA_OSC), style = Stroke(g, cap = StrokeCap.Round))
        drawPath(cae, col(P.MANILA), style = Stroke(g * 0.65f, cap = StrokeCap.Round))
        val gz = Offset(ch0.x - w * 0.016f, apoyo + w * 0.07f)
        drawOval(col(P.MANILA_OSC), Offset(gz.x - w * 0.014f, gz.y - w * 0.014f), Size(w * 0.028f, w * 0.03f), style = Stroke(g * 0.7f))
        drawRect(col(P.TINTA).copy(alpha = 0.7f), Offset(gz.x - g * 0.45f, gz.y - w * 0.026f), Size(g * 0.9f, w * 0.008f))
    }

    /**
     * La caracola reina (la de los cayos de Florida), tendida con la boca hacia delante: la espira con sus nudos a la izquierda, el
     * cuerpo con sus vueltas y el labio ancho rosado, brillante por dentro.
     */
    private fun DrawScope.caracola(pie: Offset, a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val k = cerca(pie)
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        val s = w * 0.075f
        val cx = pie.x; val by = pie.y - w * 0.004f
        val cuerpo = Path().apply {
            moveTo(cx - s * 1.15f, by - s * 0.62f)
            cubicTo(cx - s * 0.6f, by - s * 1.05f, cx + s * 0.2f, by - s * 1.12f, cx + s * 0.62f, by - s * 0.82f)
            cubicTo(cx + s * 0.95f, by - s * 0.55f, cx + s * 0.92f, by - s * 0.05f, cx + s * 0.5f, by)
            lineTo(cx - s * 0.45f, by)
            cubicTo(cx - s * 0.8f, by - s * 0.1f, cx - s * 1.05f, by - s * 0.35f, cx - s * 1.15f, by - s * 0.62f)
            close()
        }
        sombraPared(cuerpo, Offset(cx, by - s * 0.5f), apoyo, a, cerca, 0.2f, lit)
        if (lit == 0f) contactoAbismo(Offset(cx + w * 0.01f, pie.y), s * 2.1f, w * 0.018f, a, 0.4f)
        drawPath(cuerpo, col(P.CONCHA))
        clipPath(cuerpo) {
            // Las vueltas de la concha, con sus nudos.
            for (j in 0 until 4) {
                val x = cx - s * (0.95f - 0.38f * j)
                drawLine(col(P.CONCHA, -0.25f), Offset(x, by - s * 1.1f), Offset(x + s * 0.25f, by), 1.4f * u)
                drawCircle(col(P.CONCHA_LUZ, 0.1f), s * 0.1f, Offset(x + s * 0.08f, by - s * (0.78f - 0.06f * j)))
                drawCircle(a.sombra(0.2f), s * 0.1f, Offset(x + s * 0.12f, by - s * (0.74f - 0.06f * j)), style = Stroke(1f * u))
            }
            drawRect(a.sombra(0.25f), Offset(cx - s * 1.2f, by - s * 0.3f), Size(s * 2.4f, s * 0.3f))
            drawRect(a.brillo(0.3f, k), Offset(cx - s * 1.2f, by - s * 1.1f), Size(s * 2.4f, s * 0.22f))
        }
        // El labio rosado, hacia delante a la derecha.
        val labio = Path().apply {
            moveTo(cx - s * 0.1f, by - s * 0.05f)
            cubicTo(cx + s * 0.05f, by - s * 0.72f, cx + s * 0.6f, by - s * 1.0f, cx + s * 1.05f, by - s * 0.78f)
            cubicTo(cx + s * 1.25f, by - s * 0.5f, cx + s * 1.05f, by - s * 0.1f, cx + s * 0.6f, by)
            close()
        }
        drawPath(labio, col(P.CONCHA_ROSA))
        clipPath(labio) {
            drawOval(col(mezcla(P.CONCHA_ROSA, P.CONCHA_LUZ, 0.5f), 0.1f), Offset(cx + s * 0.15f, by - s * 0.75f), Size(s * 0.8f, s * 0.5f))
            drawOval(a.sombra(0.3f), Offset(cx - s * 0.05f, by - s * 0.35f), Size(s * 0.6f, s * 0.4f))
        }
        drawPath(labio, col(P.CONCHA_LUZ, 0.2f), style = Stroke(1.2f * u))
        destelloAbismo(Offset(cx + s * 0.55f, by - s * 0.68f), 1.3f * u, a, k)
    }

    /** La aceitera de la bomba: el cuerpo de hojalata cónico con su pulsador de latón y el pico largo y fino. */
    private fun DrawScope.aceitera(pie: Offset, a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val k = cerca(pie)
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        val bw = w * 0.05f; val bh = w * 0.045f
        val by = pie.y - w * 0.006f
        val cono = poligonoAbismo(pie.x - bw / 2, by, pie.x + bw / 2, by, pie.x + bw * 0.16f, by - bh, pie.x - bw * 0.16f, by - bh)
        if (lit == 0f) contactoAbismo(Offset(pie.x + w * 0.008f, pie.y), bw * 1.5f, w * 0.014f, a, 0.4f)
        drawPath(cono, col(P.HOJALATA))
        clipPath(cono) { cilindroAbismo(Rect(pie.x - bw / 2, by - bh, pie.x + bw / 2, by), a, k, if (a.oscuro) 1f else -1f, 1.2f) }
        drawRect(col(P.HOJALATA, -0.2f), Offset(pie.x - bw / 2, by - w * 0.006f), Size(bw, w * 0.006f))
        drawRect(col(P.LATON), Offset(pie.x - bw * 0.14f, by - bh - w * 0.012f), Size(bw * 0.28f, w * 0.012f))
        drawCircle(col(P.LATON_OSC), w * 0.006f, Offset(pie.x, by - bh - w * 0.014f))
        val pico = Path().apply { moveTo(pie.x + bw * 0.1f, by - bh - w * 0.006f); quadraticTo(pie.x + bw * 0.4f, by - bh - w * 0.05f, pie.x - bw * 0.6f, by - bh - w * 0.11f) }
        drawPath(pico, col(P.HOJALATA, -0.15f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawPath(pico, a.brillo(0.4f, k), style = Stroke(0.6f * u))
        drawOval(col(P.ESPONJA_OSC).copy(alpha = 0.5f), Offset(pie.x - bw * 0.3f, by - bh * 0.35f), Size(bw * 0.35f, bh * 0.25f))
    }

    /** El bichero apoyado en la curva del bao: el asta de fresno y el hierro con su gancho y su punta. */
    private fun DrawScope.bichero(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val p0 = Offset(w * 0.985f, apoyo); val p1 = Offset(w * 0.925f, w * 0.085f)
        val k = cerca(Offset(w * 0.95f, w * 0.3f))
        fun col(cc: Color, f: Float = 0f) = a.en(cc, f, k, lit, m)
        val asta = Path().apply { moveTo(p0.x, p0.y); lineTo(p1.x, p1.y) }
        if (lit == 0f) {
            val so = Path().apply { moveTo(p0.x, p0.y); lineTo(p1.x + w * 0.02f, p1.y + w * 0.012f) }
            if (!a.oscuro) drawPath(so, a.sombra(0.2f), style = Stroke(w * 0.012f))
        }
        drawPath(asta, col(P.ESPARTO, -0.1f), style = Stroke(w * 0.011f, cap = StrokeCap.Round))
        translate(-w * 0.003f, 0f) { drawPath(asta, col(P.ESPARTO, 0.2f), style = Stroke(w * 0.003f)) }
        // El hierro: el cuello, el gancho vuelto hacia la izquierda y la punta.
        val cu = Offset(p1.x - w * 0.003f, p1.y + w * 0.02f)
        drawLine(col(P.HIERRO), cu, Offset(p1.x - w * 0.006f, p1.y - w * 0.012f), w * 0.009f, StrokeCap.Round)
        val gancho = Path().apply { moveTo(p1.x - w * 0.003f, p1.y); cubicTo(p1.x - w * 0.03f, p1.y - w * 0.005f, p1.x - w * 0.03f, p1.y + w * 0.025f, p1.x - w * 0.016f, p1.y + w * 0.02f) }
        drawPath(gancho, col(P.HIERRO), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
        drawPath(gancho, a.brillo(0.35f, k), style = Stroke(0.6f * u))
        drawCircle(col(P.HIERRO_LUZ), w * 0.004f, Offset(p1.x - w * 0.006f, p1.y - w * 0.014f))
        // Las vueltas de piola que sujetan el hierro.
        for (j in 0 until 3) drawLine(col(P.MANILA), Offset(cu.x - w * 0.007f, cu.y + j * 2f * u), Offset(cu.x + w * 0.007f, cu.y + j * 2f * u - 1f * u), 1.2f * u)
    }

    // ---------------------------------------------------------------- la barandilla

    /** La barandilla de mar de la balda de arriba: los candeleros de latón y el pasamanos de teca, delante de lo que hay encima. */
    private fun DrawScope.barandilla(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val alto = w * 0.024f
        val y1 = apoyo + w * 0.006f; val y0 = y1 - alto
        listOf(0.015f, 0.19f, 0.405f, 0.7f, 0.985f).forEach { fx ->
            val x = w * fx
            val k = cerca(Offset(x, y0))
            drawRect(a.sombra(0.3f), Offset(x + 1.5f * u, y0 + 2f * u), Size(w * 0.006f, alto))
            drawRect(a.dia(P.LATON, 0f, k), Offset(x - w * 0.003f, y0), Size(w * 0.006f, alto))
            drawRect(a.brillo(0.45f, k), Offset(x - w * 0.003f, y0), Size(1f * u, alto))
            drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(x - w * 0.005f, y1 - w * 0.004f), Size(w * 0.01f, w * 0.004f))
        }
        val g = w * 0.009f
        val n = if (a.lumbre) 24 else 1
        for (t in 0 until n) {
            val x0 = w * t / n
            val k = cerca(Offset(x0 + w / n / 2, y0))
            drawRect(a.sombra(0.25f), Offset(x0, y0 + g), Size(w / n + 0.5f, 2.4f * u))
            drawRoundRect(a.dia(mezcla(P.TECA, P.TECA_LUZ, 0.3f), 0f, k), Offset(x0, y0 - g * 0.2f), Size(w / n + 0.5f, g), CornerRadius(if (n == 1) g / 2 else 0f))
            drawRect(a.brillo(0.6f, k), Offset(x0, y0 - g * 0.2f), Size(w / n + 0.5f, 1f * u))
        }
    }
}
