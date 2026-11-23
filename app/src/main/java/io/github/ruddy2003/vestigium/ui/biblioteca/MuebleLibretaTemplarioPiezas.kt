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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.ideario.azar

/**
 * Lo que acompaña a la regla en la libreta de Templario ([MuebleLibretaTemplario]): la lámpara de bronce de dos mecheros en su
 * pie (de noche, la luz que manda), el pan del lector con su escudilla y su cuchara de palo, y las tablillas de cera colgadas
 * de un clavo con su estilo.
 */
internal object PiezasLibretaTemplario {
    private val P = PaletaTemplario
    private val PAN = Color(0xFFB87A3E)
    private val PAN_LUZ = Color(0xFFE0AE6A)
    private val MADERA = Color(0xFF8A5A34)
    private val CERA = Color(0xFF3A3024)

    private fun k(p: Offset, s: SitioLibretaTemplario, a: AireTemplario) = MuebleLibretaTemplario.cerca(p, s, a)

    /** La lámpara de bronce: el pie de tres garras, el fuste con sus nudos, la cazoleta de dos mecheros y el asa de anilla. */
    fun DrawScope.lampara(s: SitioLibretaTemplario, a: AireTemplario) {
        scale(SitioLibretaTemplario.ESCALA, s.lampara) { lamparaEn(s, a) }
    }

    private fun DrawScope.lamparaEn(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val x = s.lampara.x; val y = s.lampara.y
        val kk = 1f
        val bronce = a.dia(P.BRONCE, 0f, kk)
        val hacia = if (a.oscuro) 1f else -1f
        // La sombra en el pretil.
        drawOval(a.sombra(0.4f), Offset(x - 14f * U + (if (a.oscuro) -6f else 4f) * U, y - 2f * U), Size(32f * U, 5f * U))
        // El pie: tres garras y la campana.
        listOf(-1f, 0f, 1f).forEach { d -> drawOval(a.dia(P.BRONCE, -0.2f, kk), Offset(x + d * 9f * U - 3.5f * U, y - 3f * U), Size(7f * U, 4f * U)) }
        val campana = Path().apply { moveTo(x - 12f * U, y - 2f * U); quadraticTo(x - 4f * U, y - 6f * U, x - 3f * U, y - 14f * U); lineTo(x + 3f * U, y - 14f * U); quadraticTo(x + 4f * U, y - 6f * U, x + 12f * U, y - 2f * U); close() }
        drawPath(campana, bronce)
        clipPath(campana) { cilindroTemplario(Rect(x - 12f * U, y - 14f * U, x + 12f * U, y), a, kk, hacia, 1.3f) }
        // El fuste con sus nudos.
        val top = 134f * U
        val fuste = Rect(x - 2f * U, top, x + 2f * U, y - 13f * U)
        drawRect(bronce, fuste.topLeft, fuste.size)
        clipRect1(fuste) { cilindroTemplario(fuste, a, kk, hacia, 1.4f) }
        listOf(0.25f, 0.6f).forEach { f ->
            val yy = fuste.top + fuste.height * f
            drawOval(a.dia(P.BRONCE, 0.05f, kk), Offset(x - 5f * U, yy - 3f * U), Size(10f * U, 6f * U))
            drawOval(a.sombra(0.35f), Offset(x - 5f * U, yy), Size(10f * U, 3f * U))
            drawOval(a.brillo(0.5f, kk), Offset(x - 3.5f * U, yy - 2.4f * U), Size(4f * U, 1.4f * U))
        }
        // El platillo bajo la cazoleta.
        drawOval(a.dia(P.BRONCE, -0.1f, kk), Offset(x - 9f * U, top - 2f * U), Size(18f * U, 4f * U))
        // La cazoleta de dos mecheros: una barca con los picos a los lados.
        val yb = top - 4f * U
        val caz = Path().apply {
            moveTo(x - 24f * U, yb - 6f * U)
            quadraticTo(x - 18f * U, yb - 4f * U, x - 12f * U, yb - 7f * U)
            quadraticTo(x, yb - 9f * U, x + 12f * U, yb - 7f * U)
            quadraticTo(x + 18f * U, yb - 4f * U, x + 24f * U, yb - 6f * U)
            lineTo(x + 22f * U, yb - 3.5f * U)
            quadraticTo(x + 14f * U, yb - 1f * U, x + 10f * U, yb + 1f * U)
            quadraticTo(x, yb + 4f * U, x - 10f * U, yb + 1f * U)
            quadraticTo(x - 14f * U, yb - 1f * U, x - 22f * U, yb - 3.5f * U); close()
        }
        drawPath(caz, a.dia(P.BRONCE, 0.05f, kk))
        clipPath(caz) {
            drawRect(a.sombra(0.35f), Offset(x - 26f * U, yb - 2f * U), Size(52f * U, 8f * U))
            drawRect(a.brillo(0.4f, kk), Offset(x - 26f * U, yb - 8f * U), Size(52f * U, 1.4f * U))
        }
        // La boca, con el aceite, y el asa de anilla.
        drawOval(a.dia(Color(0xFF3A2A12), 0f, kk), Offset(x - 7f * U, yb - 9f * U), Size(14f * U, 3.6f * U))
        drawArc(bronce, 200f, 140f, false, Offset(x - 6f * U, yb - 22f * U), Size(12f * U, 14f * U), style = Stroke(1.8f * U))
        // Las mechas y las llamas (de noche y al ocaso).
        s.llamas.forEach { p ->
            drawLine(Color(0xFF2A2018), Offset(p.x, p.y + 4f * U), Offset(p.x, p.y + 1f * U), 1.2f * U, StrokeCap.Round)
            if (a.oscuro) llama(p, 5.5f * U)
        }
        if (a.oscuro) {
            // El brillo de las llamas en el bronce.
            drawLine(P.FUEGO.copy(alpha = 0.7f), Offset(x - 20f * U, yb - 6.4f * U), Offset(x + 20f * U, yb - 6.4f * U), 1.2f * U)
            drawRect(P.LLAMA.copy(alpha = 0.45f), Offset(x - 2f * U, top), Size(1.2f * U, fuste.height))
        }
    }

    private inline fun DrawScope.clipRect1(r: Rect, crossinline block: DrawScope.() -> Unit) =
        clipRect(r.left, r.top, r.right, r.bottom) { block() }

    /** Una llama de aceite con su halo, en [p] (la base), de alto [t]. */
    private fun DrawScope.llama(p: Offset, t: Float) {
        drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.5f), P.LLAMA.copy(alpha = 0f)), p - Offset(0f, t * 0.6f), t * 3.2f), t * 3.2f, p - Offset(0f, t * 0.6f))
        val f = Path().apply {
            moveTo(p.x + t * 0.1f, p.y - t * 2.2f)
            cubicTo(p.x + t * 0.6f, p.y - t * 1.2f, p.x + t * 0.55f, p.y, p.x, p.y + t * 0.15f)
            cubicTo(p.x - t * 0.55f, p.y, p.x - t * 0.55f, p.y - t * 1f, p.x + t * 0.1f, p.y - t * 2.2f); close()
        }
        drawPath(f, Color(0xFFFFA248))
        drawOval(P.FUEGO, Offset(p.x - t * 0.22f, p.y - t * 1.1f), Size(t * 0.44f, t * 0.95f))
    }

    /** El pan del lector, la escudilla de madera y la cuchara, en el pretil delante a la izquierda. */
    fun DrawScope.pan(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val p = s.pan
        val hacia = if (a.oscuro) 1f else -1f
        // La escudilla, a la izquierda, con la cuchara de palo dentro.
        val ex = p.x - 26f * U
        val ke = k(Offset(ex, p.y - 6f * U), s, a)
        drawOval(a.sombra(0.4f), Offset(ex - 14f * U + (if (a.oscuro) -4f else 3f) * U, p.y - 2f * U), Size(28f * U, 4.5f * U))
        drawLine(a.dia(MADERA, 0.1f, ke), Offset(ex + 2f * U, p.y - 10f * U), Offset(ex + 16f * U, p.y - 24f * U), 2.4f * U, StrokeCap.Round)
        val esc = Path().apply { moveTo(ex - 13f * U, p.y - 11f * U); quadraticTo(ex - 12f * U, p.y, ex, p.y); quadraticTo(ex + 12f * U, p.y, ex + 13f * U, p.y - 11f * U); close() }
        drawPath(esc, a.dia(MADERA, 0f, ke))
        clipPath(esc) {
            cilindroTemplario(Rect(ex - 13f * U, p.y - 11f * U, ex + 13f * U, p.y), a, ke, hacia, 1.3f)
            for (j in 0 until 3) drawLine(a.sombra(0.25f), Offset(ex - 10f * U + 7f * U * j, p.y - 10f * U), Offset(ex - 8f * U + 7f * U * j, p.y - 1f * U), 0.6f * U)
        }
        drawOval(a.dia(MADERA, 0.15f, ke), Offset(ex - 13f * U, p.y - 13f * U), Size(26f * U, 4f * U))
        drawOval(a.dia(Color(0xFF4A2E18), 0f, ke), Offset(ex - 11f * U, p.y - 12.4f * U), Size(22f * U, 2.6f * U))
        // El pan: una hogaza redonda, con su corte, la corteza tostada y la harina; le falta un pedazo.
        val kp = k(Offset(p.x, p.y - 8f * U), s, a)
        drawOval(a.sombra(0.4f), Offset(p.x - 17f * U + (if (a.oscuro) -5f else 4f) * U, p.y - 2.5f * U), Size(36f * U, 5f * U))
        val hogaza = Path().apply { moveTo(p.x - 17f * U, p.y); quadraticTo(p.x - 17f * U, p.y - 18f * U, p.x, p.y - 19f * U); quadraticTo(p.x + 17f * U, p.y - 18f * U, p.x + 17f * U, p.y); close() }
        drawPath(hogaza, a.dia(PAN, 0f, kp))
        clipPath(hogaza) {
            // La miga donde se partió, a la derecha.
            drawOval(a.etiqueta(Color(0xFFEAD7AE), kp), Offset(p.x + 8f * U, p.y - 13f * U), Size(14f * U, 14f * U))
            drawOval(a.sombra(0.25f), Offset(p.x + 9f * U, p.y - 12f * U), Size(12f * U, 12f * U), style = Stroke(1f * U))
            // La luz y la sombra del bollo.
            if (!a.oscuro) drawOval(a.dia(PAN_LUZ, 0f, kp), Offset(p.x - 12f * U, p.y - 17f * U), Size(14f * U, 7f * U))
            else drawOval(a.filo(0.5f * kp), Offset(p.x - 2f * U, p.y - 18f * U), Size(16f * U, 6f * U))
            drawRect(a.sombra(0.3f), Offset(p.x - 18f * U, p.y - 5f * U), Size(36f * U, 5f * U))
            // La harina.
            for (j in 0 until 7) drawCircle(a.claro(0.5f), 0.8f * U, Offset(p.x - 12f * U + 14f * U * azar(8700, j), p.y - 16f * U + 9f * U * azar(8701, j)))
        }
        // El corte de la corteza: un tajo largo y curvo.
        drawArc(a.dia(PAN_LUZ, 0.1f, kp), 200f, 80f, false, Offset(p.x - 13f * U, p.y - 16f * U), Size(22f * U, 16f * U), style = Stroke(1.8f * U, cap = StrokeCap.Round))
        drawArc(a.sombra(0.35f), 205f, 70f, false, Offset(p.x - 13f * U, p.y - 15f * U), Size(22f * U, 16f * U), style = Stroke(0.9f * U))
    }

    /** Las tablillas de cera del lector, colgadas de un clavo por su cordón, con el estilo de hueso colgando al lado. */
    fun DrawScope.tablillas(s: SitioLibretaTemplario, a: AireTemplario) {
        val U = s.U
        val c = s.tablillas
        val kk = k(c, s, a)
        val clavo = Offset(c.x + 14f * U, c.y - 16f * U)
        rotate(4f, clavo) {
            // El cordón y la tablilla de detrás, asomando.
            drawLine(a.dia(P.PALMA, 0f, kk), clavo, Offset(c.x + 4f * U, c.y), 1f * U)
            drawLine(a.dia(P.PALMA, 0f, kk), clavo, Offset(c.x + 24f * U, c.y), 1f * U)
            val atras = Rect(c.x + 3f * U, c.y + 2f * U, c.x + 31f * U, c.y + 46f * U)
            drawRect(a.sombra(0.3f), atras.topLeft + Offset(5f * U, 3f * U), atras.size)
            drawRect(a.dia(P.CEDRO_OSC, 0f, kk), atras.topLeft, atras.size)
            val r = Rect(c.x, c.y, c.x + 28f * U, c.y + 44f * U)
            drawRect(a.sombra(0.3f), r.topLeft + Offset(3f * U, 2f * U), r.size)
            drawRect(a.dia(P.CEDRO, 0.05f, kk), r.topLeft, r.size)
            val cera = r.deflate(3.5f * U)
            drawRect(a.dia(CERA, 0f, kk), cera.topLeft, cera.size)
            // Lo apuntado en la cera: rayas cortas a renglones (no letras), y lo borrado con el canto del estilo.
            val rayas = Path()
            var y = cera.top + 4f * U; var j = 0
            while (y < cera.bottom - 3f * U) {
                var x = cera.left + 2f * U
                while (x < cera.right - 3f * U) { val l = (2f + 4f * azar(8800 + j, (x / U).toInt())) * U; rayas.moveTo(x, y); rayas.lineTo(minOf(x + l, cera.right - 2f * U), y + 0.4f * U); x += l + 1.6f * U }
                y += 4.6f * U; j++
            }
            drawPath(rayas, a.dia(Color(0xFFB0956A), 0f, kk).copy(alpha = 0.55f), style = Stroke(0.6f * U))
            drawRect(a.dia(CERA, 0.08f, kk), Offset(cera.left, cera.bottom - 10f * U), Size(cera.width, 6f * U))
            drawRect(a.brillo(0.35f, kk), r.topLeft, Size(r.width, 1f * U))
            drawRect(a.sombra(0.3f), Offset(r.right - 1.4f * U, r.top), Size(1.4f * U, r.height))
            // Las dos bisagras de cordón del lomo.
            listOf(0.25f, 0.75f).forEach { f -> drawRoundRect(a.dia(P.PALMA, 0f, kk), Offset(r.left - 1.6f * U, r.top + r.height * f - 2f * U), Size(4f * U, 4f * U), CornerRadius(2f * U)) }
        }
        // El estilo de hueso, colgando de su cordón a la derecha.
        val e0 = Offset(clavo.x + 18f * U, c.y + 8f * U)
        drawLine(a.dia(P.PALMA, 0f, kk), clavo, e0, 0.8f * U)
        drawLine(a.sombra(0.3f), e0 + Offset(3f * U, 2f * U), Offset(e0.x + 5f * U, e0.y + 34f * U), 2.4f * U, StrokeCap.Round)
        drawLine(a.dia(P.CUERNO, 0f, kk), e0, Offset(e0.x + 2f * U, e0.y + 32f * U), 2.4f * U, StrokeCap.Round)
        drawLine(a.dia(P.CUERNO, 0.1f, kk), Offset(e0.x - 2f * U, e0.y + 1f * U), Offset(e0.x + 2f * U, e0.y + 1f * U), 3.4f * U, StrokeCap.Round)
        clavoTemplario(clavo, 1.8f * U, a, kk)
    }
}
