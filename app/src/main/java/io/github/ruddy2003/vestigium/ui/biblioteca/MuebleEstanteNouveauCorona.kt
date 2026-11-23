package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El bodegón de la corona del estante de Art nouveau ([MuebleEstanteNouveau]). Colgado en la pared: el panel decorativo del
 * taller (una litografía de flores sin letras, en su marco ebonizado con el filete dorado y su paspartú de arco) que cuelga de
 * la moldura con dos cordones, y el aplique eléctrico de bronce con su tulipa de vidrio opalino y su cordón por la pared.
 * Encima de la cornisa: manda el ramo grande de flores inventadas en el jarrón de vidrio camafeo (del ámbar al violeta, con la
 * flor grabada), con hojas que caen por delante de la cornisa; a la izquierda, dos libros acostados y encima el libro de las
 * flores abierto con una flor seca; a la derecha, la pila de carpetas de estampas con sus cintas colgando y, apoyado en la
 * pared detrás, el abanico abierto pintado; pétalos caídos. En las esquinas, los capullos tallados de los montantes.
 */
internal object CoronaEstanteNouveau {
    private val P = PaletaNouveau

    /** Hacia dónde cae la sombra de lo que está en [x] (anchos): de día, a la derecha (el escaparate); de noche, lejos del aplique. */
    private fun hacia(a: AireNouveau, x: Float): Float = if (!a.oscuro) 1f else if (x < MuebleEstanteNouveau.LAMP_X) -1f else 1f

    /** Cuánto le da el sol del escaparate (de día y al ocaso) a ([x], [y]) en anchos: 1 dentro del paño, 0 fuera. */
    private fun sol(a: AireNouveau, x: Float, y: Float): Float = if (a.oscuro) 0f else MuroEstanteNouveau.dentro(MuroEstanteNouveau.pano(a), x, y)

    // ---------------------------------------------------------------- lo colgado

    fun DrawScope.colgado(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        panel(a, cerca)
        aplique(a, cerca)
    }

    /** El panel decorativo en su marco, colgado de la moldura con dos cordones que suben a un gancho de latón. */
    private fun DrawScope.panel(a: AireNouveau, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val m = Rect(w * 0.565f, w * 0.118f, w * 0.75f, w * 0.39f)
        val k = cerca(m.center)
        val s = hacia(a, m.center.x / w)
        val gancho = Offset(m.center.x, w * MuroEstanteNouveau.MOLDURA - 0.6f * u)
        // Los cordones y su sombra.
        val cordon = Path().apply { moveTo(m.left + m.width * 0.12f, m.top + 2f * u); lineTo(gancho.x, gancho.y + 2f * u); lineTo(m.right - m.width * 0.12f, m.top + 2f * u) }
        translate(s * 2.4f * u, 3f * u) { drawPath(cordon, a.sombra(0.3f), style = Stroke(1.2f * u)) }
        drawPath(cordon, a.luz(P.CENEFA, k), style = Stroke(1.2f * u, join = StrokeJoin.Round))
        drawPath(cordon, a.brillo(0.3f, k), style = Stroke(0.4f * u))
        drawRoundRect(a.luz(P.LATON_OSC, k), Offset(gancho.x - 2f * u, gancho.y - 1f * u), Size(4f * u, 4.4f * u), CornerRadius(1.4f * u))
        destelloNouveau(Offset(gancho.x - 0.8f * u, gancho.y), 0.9f * u, a, k)
        // La sombra del marco en la pared (corrida y más baja abajo: el cuadro se separa de la pared por abajo).
        drawPath(poligonoNouveau(m.left + s * w * 0.008f, m.top + w * 0.006f, m.right + s * w * 0.012f, m.top + w * 0.006f,
            m.right + s * w * 0.018f, m.bottom + w * 0.014f, m.left + s * w * 0.016f, m.bottom + w * 0.014f), a.sombra(0.34f))
        // El marco ebonizado, con su canto de luz y su filete dorado.
        val ebano = Color(0xFF2B211E)
        drawRect(a.luz(ebano, k), m.topLeft, m.size)
        drawRect(a.dia(Color(0xFF6A564C), 0.1f, k), m.topLeft, Size(m.width, 1.3f * u))
        drawRect(a.dia(Color(0xFF6A564C), 0.05f, k), m.topLeft, Size(1.3f * u, m.height))
        if (a.oscuro) drawRect(a.filo(0.5f * k), Offset(m.right - 1.2f * u, m.top), Size(1.2f * u, m.height))
        val fi = m.deflate(w * 0.011f)
        drawRect(a.luz(P.ORO, k), fi.topLeft, fi.size, style = Stroke(1f * u))
        // El paspartú crema con la abertura de arco.
        val pp = fi.deflate(1.2f * u)
        drawRect(a.luz(P.PAPEL, k), pp.topLeft, pp.size)
        val ab = Rect(pp.left + pp.width * 0.12f, pp.top + pp.height * 0.07f, pp.right - pp.width * 0.12f, pp.bottom - pp.height * 0.08f)
        val arco = Path().apply {
            moveTo(ab.left, ab.bottom); lineTo(ab.left, ab.top + ab.width * 0.5f)
            cubicTo(ab.left, ab.top + ab.width * 0.05f, ab.right, ab.top + ab.width * 0.05f, ab.right, ab.top + ab.width * 0.5f)
            lineTo(ab.right, ab.bottom); close()
        }
        clipPath(arco) { estampaPanel(ab, a, k) }
        drawPath(arco, a.sombra(0.45f), style = Stroke(1.1f * u))
        drawPath(arco, a.claro(0.35f), style = Stroke(0.5f * u))
        // El brillo del vidrio del cuadro, en diagonal, de día.
        if (!a.oscuro) clipRect(pp.left, pp.top, pp.right, pp.bottom) {
            drawPath(poligonoNouveau(pp.left + pp.width * 0.55f, pp.top, pp.left + pp.width * 0.7f, pp.top, pp.left + pp.width * 0.15f, pp.bottom, pp.left, pp.bottom), luzMuebleA(0.1f))
        }
        // El sol del escaparate, si le alcanza.
        MuroEstanteNouveau.run { panoEn(a, Path().apply { addRect(m) }, -0.01f, -0.01f, 0.8f) }
    }

    private fun luzMuebleA(a: Float) = io.github.ruddy2003.vestigium.ui.muebles.luzMueble(a)

    /**
     * La litografía del panel dentro de [r]: el fondo crema, un disco de oro pálido detrás, la línea de látigo que la enmarca, una
     * amapola grande y una umbela, en tintas planas, y el borde de tierra salvia abajo.
     */
    private fun DrawScope.estampaPanel(r: Rect, a: AireNouveau, k: Float) {
        val u = density
        drawRect(a.luz(Color(0xFFEDE2C4), k), r.topLeft, r.size)
        drawCircle(a.luz(Color(0xFFE6C77E), k), r.width * 0.44f, Offset(r.center.x, r.top + r.height * 0.34f))
        drawCircle(a.luz(Color(0xFFD9B060), k), r.width * 0.44f, Offset(r.center.x, r.top + r.height * 0.34f), style = Stroke(0.8f * u))
        val tierra = Path().apply {
            moveTo(r.left, r.bottom - r.height * 0.16f)
            cubicTo(r.left + r.width * 0.4f, r.bottom - r.height * 0.24f, r.left + r.width * 0.6f, r.bottom - r.height * 0.08f, r.right, r.bottom - r.height * 0.18f)
            lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
        }
        drawPath(tierra, a.luz(Color(0xFF8FA67E), k))
        with(FloresNouveau) {
            deColor(4, Offset(r.left + r.width * 0.3f, r.top + r.height * 0.6f), r.height * 0.32f, a.luz(Color(0xFFF2E6C2), k), a.luz(P.HOJA, k), a.luz(P.AMARILLO, k), a.luz(P.HOJA_OSC, k), -6f)
            deColor(1, Offset(r.left + r.width * 0.6f, r.top + r.height * 0.5f), r.height * 0.42f, a.luz(Color(0xFFC9583E), k), a.luz(Color(0xFF5E7A55), k), a.luz(P.TINTA, k), a.luz(Color(0xFF6E2A1C), k), 4f)
        }
        // La línea de látigo que corre por el borde.
        val l = Path().apply {
            moveTo(r.left + r.width * 0.08f, r.bottom - r.height * 0.05f)
            cubicTo(r.left - r.width * 0.05f, r.top + r.height * 0.6f, r.left + r.width * 0.3f, r.top + r.height * 0.3f, r.left + r.width * 0.12f, r.top + r.height * 0.18f)
            quadraticTo(r.left + r.width * 0.04f, r.top + r.height * 0.12f, r.left + r.width * 0.14f, r.top + r.height * 0.1f)
        }
        drawPath(l, a.luz(P.IRIS_OSC, k), style = Stroke(1.2f * u, cap = StrokeCap.Round))
    }

    /**
     * El aplique eléctrico: la placa de bronce en forma de hoja en la pared, el brazo que sale en tallo y se vuelve hacia abajo, el
     * portalámparas en capullo y la tulipa de vidrio opalino (crema que se vuelve ámbar y violeta en el borde); el cordón sube por
     * la pared hasta la moldura. De noche, la tulipa arde.
     */
    private fun DrawScope.aplique(a: AireNouveau, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val lx = w * MuebleEstanteNouveau.LAMP_X; val ly = w * MuebleEstanteNouveau.LAMP_Y
        val placa = Offset(w * 0.935f, w * 0.17f)
        val k = if (a.lumbre) 1f else cerca(placa)
        // El cordón de la luz, trenzado, que sube por la pared hasta la moldura.
        val cordon = Path().apply { moveTo(placa.x + 1f * u, placa.y - w * 0.03f); cubicTo(placa.x + 3f * u, w * 0.13f, placa.x - 2f * u, w * 0.1f, placa.x + 1f * u, w * MuroEstanteNouveau.MOLDURA) }
        drawPath(cordon, a.sombra(0.35f), style = Stroke(1.6f * u))
        drawPath(cordon, a.luz(Color(0xFF5A4A3A), k), style = Stroke(1.1f * u))
        // La placa de hoja, con su sombra.
        val hoja = Path().apply {
            moveTo(placa.x, placa.y - w * 0.042f)
            cubicTo(placa.x + w * 0.024f, placa.y - w * 0.02f, placa.x + w * 0.02f, placa.y + w * 0.03f, placa.x, placa.y + w * 0.045f)
            cubicTo(placa.x - w * 0.02f, placa.y + w * 0.03f, placa.x - w * 0.024f, placa.y - w * 0.02f, placa.x, placa.y - w * 0.042f)
            close()
        }
        val sh = if (a.oscuro) 0.6f else 1.6f
        translate(sh * u, 2f * u) { drawPath(hoja, a.sombra(0.35f)) }
        drawPath(hoja, Brush.linearGradient(listOf(a.luz(P.LATON_LUZ, k), a.luz(P.LATON, k), a.luz(P.LATON_OSC, k)), Offset(placa.x - w * 0.02f, placa.y - w * 0.04f), Offset(placa.x + w * 0.02f, placa.y + w * 0.04f)))
        drawLine(a.luz(P.LATON_OSC, k), Offset(placa.x, placa.y - w * 0.034f), Offset(placa.x, placa.y + w * 0.038f), 0.8f * u)
        // El brazo: sale de la placa, se arquea a la izquierda y baja al portalámparas.
        val boca = Offset(lx, ly - w * 0.024f)
        val brazo = Path().apply {
            moveTo(placa.x, placa.y)
            cubicTo(placa.x - w * 0.03f, placa.y - w * 0.04f, lx - w * 0.04f, placa.y - w * 0.035f, lx - w * 0.01f, placa.y + w * 0.01f)
            quadraticTo(lx, boca.y - w * 0.03f, lx, boca.y)
        }
        translate(sh * u, 2.2f * u) { drawPath(brazo, a.sombra(0.35f), style = Stroke(2.4f * u, cap = StrokeCap.Round)) }
        drawPath(brazo, a.luz(P.LATON_OSC, k), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(brazo, a.brillo(0.55f, k), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        // Un rizo en el arranque del brazo.
        drawArc(a.luz(P.LATON_OSC, k), 180f, 270f, false, Offset(placa.x - w * 0.03f, placa.y - w * 0.006f), Size(w * 0.018f, w * 0.018f), style = Stroke(1.3f * u))
        // El portalámparas en capullo.
        val cap = Path().apply {
            moveTo(boca.x - w * 0.011f, boca.y - w * 0.004f); quadraticTo(boca.x, boca.y - w * 0.026f, boca.x + w * 0.011f, boca.y - w * 0.004f)
            lineTo(boca.x + w * 0.008f, boca.y + w * 0.004f); lineTo(boca.x - w * 0.008f, boca.y + w * 0.004f); close()
        }
        drawPath(cap, a.luz(P.LATON, k))
        drawPath(cap, a.sombra(0.35f), style = Stroke(0.6f * u))
        // La tulipa, boca abajo.
        val tr = w * 0.03f
        val tul = Path().apply {
            moveTo(boca.x - tr * 0.32f, boca.y + w * 0.002f)
            cubicTo(boca.x - tr * 0.9f, boca.y + tr * 0.4f, boca.x - tr * 1.25f, boca.y + tr * 1.2f, boca.x - tr * 1.05f, boca.y + tr * 1.75f)
            quadraticTo(boca.x - tr * 0.6f, boca.y + tr * 1.5f, boca.x - tr * 0.38f, boca.y + tr * 1.86f)
            quadraticTo(boca.x, boca.y + tr * 1.58f, boca.x + tr * 0.38f, boca.y + tr * 1.86f)
            quadraticTo(boca.x + tr * 0.6f, boca.y + tr * 1.5f, boca.x + tr * 1.05f, boca.y + tr * 1.75f)
            cubicTo(boca.x + tr * 1.25f, boca.y + tr * 1.2f, boca.x + tr * 0.9f, boca.y + tr * 0.4f, boca.x + tr * 0.32f, boca.y + w * 0.002f)
            close()
        }
        if (a.lumbre) {
            val f = if (a.oscuro && a.noche) 0.75f else 1f
            drawPath(tul, Brush.radialGradient(listOf(P.FUEGO, mezcla(P.LAMPARA, P.FUEGO, 0.3f * f), mezcla(P.AMBAR, P.LAMPARA, 0.3f), mezcla(P.AMBAR, P.VIOLETA, 0.35f)),
                Offset(boca.x, boca.y + tr * 1.25f), tr * 1.6f))
            drawOval(P.FUEGO.copy(alpha = 0.85f), Offset(boca.x - tr * 0.55f, boca.y + tr * 1.45f), Size(tr * 1.1f, tr * 0.36f))
        } else {
            drawPath(tul, Brush.verticalGradient(listOf(Color(0xFFF1E6CC), Color(0xFFE8C88A), mezcla(P.AMBAR, P.VIOLETA, 0.45f)), boca.y, boca.y + tr * 1.85f))
            clipPath(tul) {
                drawRect(a.sombra(0.25f), Offset(boca.x + tr * 0.2f, boca.y), Size(tr * 1.2f, tr * 2f))
                drawRect(luzMuebleA(0.5f), Offset(boca.x - tr * 0.75f, boca.y), Size(tr * 0.22f, tr * 2f))
            }
        }
        drawPath(tul, a.luz(P.LATON_OSC, k).copy(alpha = 0.6f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- lo de encima de la cornisa

    fun DrawScope.encima(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width
        val ch = apoyo + w * MuebleEstanteNouveau.CORNISA
        // El paño del escaparate en la cornisa, corrido: el frente está más cerca del escaparate que la pared.
        MuroEstanteNouveau.run { panoEn(a, Path().apply { addRect(Rect(0f, apoyo, w, ch)) }, -0.03f, -0.025f, 0.9f) }
        capullos(a, apoyo, cerca)
        abanico(a, apoyo, cerca)
        carpetas(a, apoyo, cerca)
        libros(a, apoyo, cerca)
        ramo(a, apoyo, cerca)
        petalos(a, apoyo, cerca)
    }

    /** Los montantes que pasan la cornisa como tallos y rematan en un capullo tallado, en las dos esquinas. */
    private fun DrawScope.capullos(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        listOf(w * MuebleEstanteNouveau.LADO * 0.5f, w - w * MuebleEstanteNouveau.LADO * 0.5f).forEachIndexed { i, x ->
            val k = cerca(Offset(x, apoyo - w * 0.05f))
            val s = hacia(a, x / w)
            val top = apoyo - w * 0.085f
            val g = w * 0.02f
            // El tallo y su sombra en la pared.
            val tallo = Path().apply { moveTo(x - g / 2, apoyo + 1f); lineTo(x - g * 0.36f, top + w * 0.02f); lineTo(x + g * 0.36f, top + w * 0.02f); lineTo(x + g / 2, apoyo + 1f); close() }
            translate(s * w * 0.012f, w * 0.004f) { drawPath(tallo, a.sombra(0.3f)) }
            drawPath(tallo, a.luz(P.CAOBA, k))
            drawRect(a.dia(P.CAOBA_LUZ, 0.16f, k), Offset(if (s > 0) x - g * 0.42f else x + g * 0.1f, top + w * 0.02f), Size(g * 0.3f, apoyo - top - w * 0.02f))
            // El capullo: cuatro sépalos que lo abrazan y la yema, con su luz.
            val cy = top + w * 0.012f
            val yema = Path().apply {
                moveTo(x, top - w * 0.028f)
                cubicTo(x + w * 0.02f, top - w * 0.012f, x + w * 0.02f, cy + w * 0.01f, x, cy + w * 0.016f)
                cubicTo(x - w * 0.02f, cy + w * 0.01f, x - w * 0.02f, top - w * 0.012f, x, top - w * 0.028f)
                close()
            }
            translate(s * w * 0.012f, w * 0.005f) { drawPath(yema, a.sombra(0.3f)) }
            drawPath(yema, a.luz(P.CAOBA, k))
            clipPath(yema) { drawRect(a.dia(P.CAOBA_LUZ, 0.2f, k), Offset(if (s > 0) x - w * 0.02f else x + w * 0.004f, top - w * 0.03f), Size(w * 0.012f, w * 0.06f)) }
            listOf(-1f, 1f).forEach { l ->
                val sep = Path().apply {
                    moveTo(x, cy + w * 0.016f)
                    quadraticTo(x + l * w * 0.024f, cy + w * 0.008f, x + l * w * 0.02f, cy - w * 0.016f)
                    quadraticTo(x + l * w * 0.008f, cy - w * 0.004f, x, cy + w * 0.016f); close()
                }
                drawPath(sep, a.dia(P.CAOBA_OSC, 0f, k))
                drawPath(sep, a.sombra(0.3f), style = Stroke(0.5f * u))
            }
            drawLine(a.sombra(0.3f), Offset(x, top - w * 0.02f), Offset(x, cy + w * 0.01f), 0.6f * u)
            if (a.oscuro) drawPath(yema, a.filo(0.35f * k), style = Stroke(0.7f * u))
        }
    }

    /** El abanico abierto apoyado en la pared detrás de las carpetas: el país pintado con una rama, las varillas de carey. */
    private fun DrawScope.abanico(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val c = Offset(w * 0.705f, apoyo - w * 0.058f)
        val rr = w * 0.15f
        val k = cerca(Offset(c.x, c.y - rr * 0.5f))
        val s = hacia(a, c.x / w)
        val a0 = 196f; val a1 = 344f
        fun pt(ang: Float, r: Float) = Offset(c.x + r * cos(Math.toRadians(ang.toDouble())).toFloat(), c.y + r * sin(Math.toRadians(ang.toDouble())).toFloat())
        val pais = Path().apply {
            val n = 16
            for (i in 0..n) { val p = pt(a0 + (a1 - a0) * i / n, rr); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
            for (i in n downTo 0) { val p = pt(a0 + (a1 - a0) * i / n, rr * 0.44f); lineTo(p.x, p.y) }
            close()
        }
        // La sombra del abanico en la pared.
        translate(s * w * 0.016f, w * 0.01f) { drawPath(pais, a.sombra(0.28f)) }
        drawPath(pais, a.luz(Color(0xFFEFE4CA), k))
        clipPath(pais) {
            with(FloresNouveau) {
                deColor(0, Offset(c.x - rr * 0.32f, c.y - rr * 0.68f), rr * 0.36f, a.luz(P.MALVA, k), a.luz(P.HOJA, k), a.luz(P.IRIS_OSC, k), a.luz(P.HOJA_OSC, k), -20f)
                deColor(5, Offset(c.x + rr * 0.42f, c.y - rr * 0.6f), rr * 0.3f, a.luz(Color(0xFFE7B8A8), k), a.luz(P.HOJA, k), a.luz(P.AMARILLO, k), a.luz(P.HOJA_OSC, k), 18f)
            }
            // Los pliegues: uno sí y uno no, en sombra.
            val n = 14
            for (i in 0 until n) {
                if (i % 2 == 1) continue
                val g0 = a0 + (a1 - a0) * i / n; val g1 = a0 + (a1 - a0) * (i + 1) / n
                val p0 = pt(g0, rr * 1.05f); val p1 = pt(g1, rr * 1.05f)
                drawPath(poligonoNouveau(c.x, c.y, p0.x, p0.y, p1.x, p1.y), a.sombra(0.12f))
            }
        }
        drawPath(pais, a.luz(P.CENEFA, k), style = Stroke(0.8f * u))
        // Las varillas de carey, del clavillo al país.
        val varillas = Path()
        val n = 14
        for (i in 0..n) { val g = a0 + (a1 - a0) * i / n; val p = pt(g, rr * 0.46f); varillas.moveTo(c.x, c.y); varillas.lineTo(p.x, p.y) }
        drawPath(varillas, a.luz(Color(0xFF7A4A22), k), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        drawPath(varillas, a.luz(Color(0xFFC08A4A), k).copy(alpha = 0.5f), style = Stroke(0.5f * u))
        drawCircle(a.luz(P.LATON, k), 1.8f * u, c)
    }

    /** Una carpeta de estampas acostada de [x0] a [x1], de su base [y] y su grueso [g]: tela, cantoneras y el canto de las hojas. */
    private fun DrawScope.carpeta(x0: Float, x1: Float, y: Float, g: Float, tela: Color, a: AireNouveau, k: Float, s: Int) {
        val u = density; val w = size.width
        val top = y - g
        // El canto de las hojas que asoma, crema.
        drawRect(a.luz(P.PAPEL, k), Offset(x0 + w * 0.006f, top + g * 0.25f), Size(x1 - x0 - w * 0.012f, g * 0.5f))
        drawLine(a.sombra(0.25f), Offset(x0 + w * 0.006f, top + g * 0.5f), Offset(x1 - w * 0.006f, top + g * 0.5f), 0.5f * u)
        // Las dos tapas.
        drawRect(a.luz(tela, k), Offset(x0, top), Size(x1 - x0, g * 0.26f))
        drawRect(a.luz(tela, k), Offset(x0, top + g * 0.74f), Size(x1 - x0, g * 0.26f))
        drawRect(a.dia(tela, 0.25f, k), Offset(x0, top), Size(x1 - x0, g * 0.08f))
        if (a.oscuro) drawRect(a.filo(0.45f * k), Offset(x0, top), Size(x1 - x0, 0.8f * u))
        // Las cantoneras de piel.
        listOf(x0, x1 - w * 0.02f).forEach { x -> drawRect(a.luz(P.TAFILETE, k), Offset(x, top), Size(w * 0.02f, g)) }
        drawRect(a.sombra(0.3f), Offset(x0, y - 1f * u), Size(x1 - x0, 1f * u))
        // El roce de la tela.
        drawLine(a.claro(0.25f), Offset(x0 + (x1 - x0) * azar(s, 1), top + g * 0.12f), Offset(x0 + (x1 - x0) * (azar(s, 1) + 0.15f), top + g * 0.12f), 0.8f * u)
    }

    /** La pila de carpetas de estampas, con sus cintas que cuelgan por delante de la cornisa. */
    private fun DrawScope.carpetas(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(w * 0.72f, apoyo - w * 0.03f))
        val s = hacia(a, 0.72f)
        // La sombra de la pila en la pared.
        drawRect(a.sombra(0.3f), Offset(w * 0.565f + s * w * 0.014f, apoyo - w * 0.08f), Size(w * 0.29f, w * 0.08f))
        val g = w * 0.026f
        carpeta(w * 0.56f, w * 0.86f, apoyo, g, Color(0xFF2F4A3C), a, k, 1)
        carpeta(w * 0.575f, w * 0.84f, apoyo - g, g * 0.9f, Color(0xFF5A3A5E), a, k, 2)
        carpeta(w * 0.55f, w * 0.83f, apoyo - g * 1.9f, g * 0.85f, Color(0xFF6E4E34), a, k, 3)
        // Las cintas de la de abajo, que cuelgan por delante de la cornisa.
        val cinta = a.luz(P.SEDA, k)
        listOf(w * 0.64f to 1f, w * 0.78f to -1f).forEach { (x, l) ->
            val p = Path().apply {
                moveTo(x, apoyo - g * 0.5f)
                cubicTo(x + l * w * 0.01f, apoyo + w * 0.01f, x - l * w * 0.012f, apoyo + w * 0.03f, x + l * w * 0.004f, apoyo + w * 0.048f)
            }
            translate(s * 1.4f * u, 1.6f * u) { drawPath(p, a.sombra(0.35f), style = Stroke(2.2f * u, cap = StrokeCap.Butt)) }
            drawPath(p, cinta, style = Stroke(2.2f * u, cap = StrokeCap.Butt))
            drawPath(p, a.brillo(0.35f, k), style = Stroke(0.6f * u))
        }
    }

    /** Dos libros acostados y, encima, el libro de las flores abierto, con una flor seca entre las hojas. */
    private fun DrawScope.libros(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val x0 = w * 0.09f; val x1 = w * 0.3f
        val k = cerca(Offset((x0 + x1) / 2, apoyo - w * 0.03f))
        val ks = sol(a, (x0 + x1) / 2 / w, (apoyo - w * 0.02f) / w)
        val s = hacia(a, 0.2f)
        // La sombra en la pared y los dos libros acostados.
        drawRect(a.sombra(0.28f), Offset(x0 + s * w * 0.012f, apoyo - w * 0.045f), Size(x1 - x0, w * 0.045f))
        val libros = listOf(Triple(x0, x1 - w * 0.01f, Color(0xFF3E5A44)), Triple(x0 + w * 0.012f, x1 - w * 0.024f, Color(0xFF7A3A26)))
        var y = apoyo
        libros.forEachIndexed { i, (l0, l1, c) ->
            val g = w * (0.022f - 0.004f * i)
            drawRect(a.luz(P.PAPEL_OSC, k), Offset(l0 + w * 0.004f, y - g * 0.8f), Size(l1 - l0 - w * 0.006f, g * 0.6f))
            drawRoundRect(a.luz(c, k), Offset(l0, y - g), Size(w * 0.014f, g), CornerRadius(g * 0.4f))
            drawRect(a.luz(c, k), Offset(l0, y - g), Size(l1 - l0, g * 0.22f))
            drawRect(a.luz(c, k), Offset(l0, y - g * 0.2f), Size(l1 - l0, g * 0.2f))
            drawRect(a.dia(c, 0.3f + 0.2f * ks, k), Offset(l0, y - g), Size(l1 - l0, g * 0.08f))
            drawLine(a.luz(P.ORO, k), Offset(l0 + w * 0.004f, y - g * 0.7f), Offset(l0 + w * 0.004f, y - g * 0.3f), 0.8f * u)
            y -= g
        }
        // El libro abierto, con sus dos páginas combadas, la lámina de color a la derecha y la flor seca.
        val cx = (x0 + x1) / 2 + w * 0.004f
        val ya = y
        val lw = w * 0.115f
        val tapa = Path().apply { moveTo(cx - lw, ya); lineTo(cx + lw, ya); lineTo(cx + lw * 0.97f, ya - w * 0.006f); lineTo(cx - lw * 0.97f, ya - w * 0.006f); close() }
        drawPath(tapa, a.luz(Color(0xFF4E3A5C), k))
        val hojaI = Path().apply {
            moveTo(cx, ya - w * 0.004f)
            cubicTo(cx - lw * 0.3f, ya - w * 0.03f, cx - lw * 0.7f, ya - w * 0.028f, cx - lw * 0.96f, ya - w * 0.012f)
            lineTo(cx - lw * 0.96f, ya - w * 0.004f); close()
        }
        val hojaD = Path().apply {
            moveTo(cx, ya - w * 0.004f)
            cubicTo(cx + lw * 0.3f, ya - w * 0.03f, cx + lw * 0.7f, ya - w * 0.028f, cx + lw * 0.96f, ya - w * 0.012f)
            lineTo(cx + lw * 0.96f, ya - w * 0.004f); close()
        }
        drawPath(hojaI, a.dia(P.PAPEL, 0.05f + 0.15f * ks, k))
        drawPath(hojaD, a.dia(P.PAPEL, -0.05f + 0.15f * ks, k))
        drawLine(a.sombra(0.4f), Offset(cx, ya - w * 0.026f), Offset(cx, ya - w * 0.004f), 1f * u)
        // Los renglones (sin letras) y la lámina.
        val renglones = Path()
        for (j in 0 until 4) {
            val yy = ya - w * (0.022f - 0.004f * j)
            renglones.moveTo(cx - lw * 0.86f, yy + w * 0.002f); renglones.lineTo(cx - lw * 0.18f, yy)
        }
        drawPath(renglones, a.luz(P.TINTA, k).copy(alpha = 0.28f), style = Stroke(0.6f * u))
        drawRect(a.luz(Color(0xFFD8C8A0), k), Offset(cx + lw * 0.22f, ya - w * 0.024f), Size(lw * 0.56f, w * 0.016f))
        with(FloresNouveau) {
            deColor(2, Offset(cx + lw * 0.5f, ya - w * 0.018f), w * 0.012f, a.luz(P.IRIS, k), a.luz(P.HOJA, k), a.luz(P.AMARILLO, k), a.luz(P.HOJA_OSC, k), 70f)
            // La flor seca, atravesada sobre las dos páginas.
            deColor(5, Offset(cx - lw * 0.3f, ya - w * 0.03f), w * 0.026f, a.luz(Color(0xFFC79A9A), k), a.luz(Color(0xFF8A8A5A), k), a.luz(P.AMARILLO, k), a.luz(Color(0xFF5A4A3A), k), -64f)
        }
        if (ks > 0f) drawLine(a.claro(0.6f), Offset(cx - lw * 0.9f, ya - w * 0.013f), Offset(cx - lw * 0.2f, ya - w * 0.03f), 0.8f * u)
    }

    // ---------------------------------------------------------------- el ramo en el jarrón camafeo

    /** Las flores del ramo: tipo, x e y de la flor en anchos, medio alto, color, giro y si va volteada. */
    private class FlorRamo(val tipo: Int, val x: Float, val y: Float, val r: Float, val color: Color, val giro: Float, val espejo: Boolean)

    private val RAMO = listOf(
        FlorRamo(0, 0.25f, 0.27f, 0.088f, PaletaNouveau.MALVA, -30f, true),
        FlorRamo(4, 0.585f, 0.25f, 0.072f, Color(0xFFF2E6C2), 24f, false),
        FlorRamo(3, 0.525f, 0.18f, 0.072f, Color(0xFF8E6AAE), 14f, false),
        FlorRamo(2, 0.34f, 0.18f, 0.1f, PaletaNouveau.LIRIO, -14f, true),
        FlorRamo(1, 0.445f, 0.182f, 0.09f, Color(0xFFD27A5C), 2f, false),
        FlorRamo(5, 0.33f, 0.335f, 0.062f, Color(0xFFE7B8A8), -14f, false),
        FlorRamo(2, 0.56f, 0.335f, 0.078f, PaletaNouveau.LIRIO, 36f, false),
        FlorRamo(5, 0.49f, 0.275f, 0.055f, Color(0xFFF3E0D6), 8f, false),
        FlorRamo(1, 0.28f, 0.4f, 0.058f, PaletaNouveau.IRIS, -44f, true),
        FlorRamo(0, 0.62f, 0.39f, 0.062f, PaletaNouveau.MALVA, 42f, false),
    )

    /**
     * El ramo: los tallos que salen de la boca del jarrón hacia cada flor, las flores de color con su sombra en la pared, el
     * jarrón camafeo delante (del ámbar abajo al violeta arriba, con la flor grabada en relieve y su brillo) y las hojas largas que
     * caen por delante de la boca y de la cornisa.
     */
    private fun DrawScope.ramo(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val vx = w * 0.435f
        val alto = w * 0.225f
        val boca = Offset(vx, apoyo - alto)
        val s = hacia(a, 0.435f)
        val ks = sol(a, 0.4f, (apoyo - alto * 0.5f) / w)
        // La sombra del ramo y del jarrón en la pared.
        val sombra = a.sombra(0.22f)
        val dxs = s * w * 0.028f; val dys = w * 0.012f
        with(FloresNouveau) {
            RAMO.forEach { f ->
                rotate(f.giro, Offset(f.x * w + dxs, f.y * w + dys)) { estampa(f.tipo, Offset(f.x * w + dxs, f.y * w + dys), f.r * w, sombra, espejo = f.espejo) }
            }
        }
        drawPath(perfilJarron(vx + dxs, apoyo + dys * 0.2f, alto), sombra)
        // Los tallos, de la boca a cada flor.
        val tallos = Path()
        RAMO.forEach { f ->
            val px = f.x * w; val py = f.y * w + f.r * w
            tallos.moveTo(boca.x + (px - boca.x) * 0.08f, boca.y + w * 0.01f)
            tallos.quadraticTo(boca.x + (px - boca.x) * 0.3f, py + (boca.y - py) * 0.4f, px, py)
        }
        val kr = cerca(Offset(boca.x, boca.y - w * 0.1f))
        drawPath(tallos, a.luz(P.HOJA_OSC, kr), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // Hojas largas de atrás, en abanico.
        hojasLargas(boca, w, a, kr, atras = true)
        with(FloresNouveau) {
            RAMO.forEach { f ->
                val c = Offset(f.x * w, f.y * w)
                val k = cerca(c)
                val luz = if (!a.oscuro) a.claro(0.28f + 0.2f * sol(a, f.x, f.y)) else a.brillo(0.3f, k)
                deColor(f.tipo, c, f.r * w, a.luz(f.color, k), a.luz(P.HOJA, k), a.luz(if (f.tipo == 1) P.TINTA else P.AMARILLO, k),
                    a.luz(P.HOJA_OSC, k), f.giro, f.espejo, luz)
            }
        }
        // El jarrón.
        val kj = cerca(Offset(vx, apoyo - alto * 0.5f))
        val jar = perfilJarron(vx, apoyo, alto)
        contactoNouveau(Offset(vx + s * w * 0.01f, apoyo - 0.5f * u), alto * 0.55f, w * 0.01f, a)
        drawPath(jar, Brush.verticalGradient(listOf(a.luz(mezcla(P.VIOLETA, P.IRIS_OSC, 0.3f), kj), a.luz(mezcla(P.VIOLETA, P.AMBAR, 0.45f), kj), a.luz(P.AMBAR, kj), a.luz(mezcla(P.AMBAR, Color(0xFFEAD49A), 0.4f), kj)), apoyo - alto, apoyo))
        clipPath(jar) {
            // El relieve grabado: una flor de campanillas en violeta hondo sobre el ámbar, con su canto de luz.
            with(FloresNouveau) {
                val gc = a.luz(P.IRIS_OSC, kj).copy(alpha = 0.85f)
                estampa(0, Offset(vx + alto * 0.03f, apoyo - alto * 0.3f), alto * 0.32f, a.claro(0.3f), espejo = true)
                estampa(0, Offset(vx + alto * 0.02f, apoyo - alto * 0.31f), alto * 0.32f, gc, espejo = true)
            }
            // La luz y la sombra del cuerpo redondo, por franjas.
            val izq = s > 0f
            val l = vx - alto * 0.32f; val an = alto * 0.64f
            drawRect(a.sombra(0.3f), Offset(if (izq) l + an * 0.62f else l, apoyo - alto), Size(an * 0.38f, alto))
            drawRect(a.sombra(0.16f), Offset(if (izq) l + an * 0.48f else l + an * 0.38f, apoyo - alto), Size(an * 0.14f, alto))
            drawRect(a.brillo(0.35f + 0.2f * ks, kj), Offset(if (izq) l + an * 0.2f else l + an * 0.68f, apoyo - alto), Size(an * 0.08f, alto))
            if (a.oscuro) drawRect(a.filo(0.5f * kj), Offset(if (izq) l else l + an * 0.92f, apoyo - alto), Size(an * 0.08f, alto))
        }
        drawPath(jar, a.luz(P.IRIS_OSC, kj).copy(alpha = 0.6f), style = Stroke(0.7f * u))
        // El labio de la boca.
        drawOval(a.luz(mezcla(P.VIOLETA, P.IRIS_OSC, 0.4f), kj), Offset(vx - alto * 0.13f, boca.y - alto * 0.025f), Size(alto * 0.26f, alto * 0.06f))
        drawOval(a.luz(P.IRIS_OSC, kj), Offset(vx - alto * 0.09f, boca.y - alto * 0.012f), Size(alto * 0.18f, alto * 0.035f))
        destelloNouveau(Offset(vx - alto * (if (s > 0) 0.18f else -0.18f), apoyo - alto * 0.32f), 1.6f * u, a, kj)
        // Las hojas que caen por delante, sobre la boca y la cornisa.
        hojasLargas(boca, w, a, kr, atras = false)
    }

    /** El perfil del jarrón camafeo, de pie en ([x], [pie]) y de [alto]: pie corto, panza baja, cuello largo y boca abierta. */
    private fun perfilJarron(x: Float, pie: Float, alto: Float): Path = Path().apply {
        val r = alto
        moveTo(x - r * 0.14f, pie)
        cubicTo(x - r * 0.34f, pie - r * 0.06f, x - r * 0.36f, pie - r * 0.36f, x - r * 0.16f, pie - r * 0.52f)
        cubicTo(x - r * 0.08f, pie - r * 0.6f, x - r * 0.07f, pie - r * 0.84f, x - r * 0.13f, pie - r)
        lineTo(x + r * 0.13f, pie - r)
        cubicTo(x + r * 0.07f, pie - r * 0.84f, x + r * 0.08f, pie - r * 0.6f, x + r * 0.16f, pie - r * 0.52f)
        cubicTo(x + r * 0.36f, pie - r * 0.36f, x + r * 0.34f, pie - r * 0.06f, x + r * 0.14f, pie)
        close()
    }

    /** Las hojas largas del ramo: detrás, en abanico hacia arriba; delante, las que caen por la boca y cruzan la cornisa. */
    private fun DrawScope.hojasLargas(boca: Offset, w: Float, a: AireNouveau, k: Float, atras: Boolean) {
        val u = density
        val hojas = if (atras) listOf(
            Triple(-0.17f, -0.16f, 0.016f), Triple(0.15f, -0.18f, 0.014f), Triple(-0.06f, -0.19f, 0.012f), Triple(0.22f, -0.1f, 0.014f),
        ) else listOf(
            Triple(-0.13f, 0.085f, 0.016f), Triple(0.11f, 0.12f, 0.014f), Triple(0.17f, 0.05f, 0.012f),
        )
        hojas.forEachIndexed { i, (dx, dy, an) ->
            val punta = Offset(boca.x + dx * w, boca.y + dy * w)
            val base = Offset(boca.x + dx * w * 0.08f, boca.y + w * 0.006f)
            val ctrl = if (atras) Offset(base.x + dx * w * 0.6f, base.y + dy * w * 0.9f) else Offset(base.x + dx * w * 0.8f, base.y - w * 0.05f)
            val p = Path().apply {
                moveTo(base.x - an * w * 0.3f, base.y)
                quadraticTo(ctrl.x - an * w, ctrl.y, punta.x, punta.y)
                quadraticTo(ctrl.x + an * w * 0.4f, ctrl.y + an * w, base.x + an * w * 0.3f, base.y)
                close()
            }
            val c = if (i % 2 == 0) P.HOJA else mezcla(P.HOJA, P.HOJA_OSC, 0.4f)
            if (!atras) translate(1.6f * u, 2f * u) { drawPath(p, a.sombra(0.3f)) }
            drawPath(p, a.luz(c, k))
            drawPath(p, a.luz(P.HOJA_OSC, k).copy(alpha = 0.6f), style = Stroke(0.6f * u))
            val nervio = Path().apply { moveTo(base.x, base.y); quadraticTo(ctrl.x - an * w * 0.3f, ctrl.y + an * w * 0.3f, punta.x, punta.y) }
            drawPath(nervio, a.dia(P.TALLO, 0.1f, k).copy(alpha = 0.5f), style = Stroke(0.5f * u))
        }
    }

    /** Pétalos caídos en la cornisa, junto al jarrón. */
    private fun DrawScope.petalos(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val cols = listOf(P.MALVA, Color(0xFFD27A5C), P.LIRIO, P.MALVA)
        cols.forEachIndexed { i, c ->
            val x = w * (0.49f + 0.035f * i + 0.01f * azar(961, i))
            val k = cerca(Offset(x, apoyo))
            val y = apoyo - 0.8f * u
            rotate(-30f + 50f * azar(962, i), Offset(x, y)) {
                drawOval(a.sombra(0.3f), Offset(x - 2.2f * u, y - 0.4f * u), Size(4.8f * u, 2f * u))
                drawOval(a.luz(c, k), Offset(x - 2.6f * u, y - 1.6f * u), Size(5f * u, 2.2f * u))
            }
        }
    }
}
