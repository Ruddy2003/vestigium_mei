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
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El pie del estante de Frente: el zócalo de consola oscura con sus rejillas y su parachoques de goma (el del Ideario), el piso de
 * hormigón en losas con sus juntas, sus manchas y la franja de peligro pintada al pie, y lo que hay en él: la estufa eléctrica de
 * resistencias (de noche, encendida: la luz de abajo), la caja de equipo con sus cierres y el rollo del cable grueso.
 */
internal object PieEstanteFrente {
    private val P = PaletaFrente
    private val M = MuebleEstanteFrente

    /** El color de [c] en [p] con la luz que le llega (de día, tal cual, con [f] más claro o más hondo). */
    private fun DrawScope.en(c: Color, f: Float, p: Offset, a: AireFrente, cerca: (Offset) -> Float): Color = a.dia(c, f, cerca(p))

    /**
     * Lo de abajo en [r] (desde la última tabla hasta el fondo del lienzo): [zocalo] es donde empieza la base (arriba del
     * zócalo) y [suelo], donde acaba el zócalo y empieza el piso.
     */
    fun DrawScope.alPie(r: Rect, zocalo: Float, suelo: Float, a: AireFrente, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val h = r.bottom
        val lado = w * M.LADO
        // Las patas: los costados bajan hasta el suelo, con su canto redondo y su taco de goma.
        listOf(Rect(0f, r.top, lado, suelo), Rect(w - lado, r.top, w, suelo)).forEachIndexed { i, m ->
            val k = cerca(m.center)
            val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(m, CornerRadius.Zero, CornerRadius.Zero, CornerRadius(5f * u), CornerRadius(5f * u))) }
            clipPath(forma) {
                drawRect(Brush.horizontalGradient(listOf(en(P.HUESO_LUZ, 0.1f, m.center, a, cerca), en(P.HUESO, 0f, m.center, a, cerca), en(P.HUESO_OSC, -0.12f, m.center, a, cerca)), m.left, m.right), m.topLeft, m.size)
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.sombra(0.28f)), m.top, m.bottom), m.topLeft, m.size)
            }
            drawLine(a.sombra(0.5f), Offset(m.right - 0.5f * u, m.top), Offset(m.right - 0.5f * u, m.bottom - 4f * u), 1f * u)
            val taco = Rect(m.left + 2f * u, suelo - 4f * u, m.right - 2f * u, suelo + 1f * u)
            drawRoundRect(en(P.BAQUELITA, 0f, taco.center, a, cerca), taco.topLeft, taco.size, CornerRadius(1.5f * u))
            tornilloFrente(Offset(m.center.x, m.top + (suelo - m.top) * 0.42f), 1.5f * u, a, k, 50f + 40f * i)
        }
        // El zócalo de consola oscura, metido entre las patas: el parachoques de goma arriba, la cara con sus dos rejillas.
        // Un píxel por encima de cada pata, para que la junta (que puede caer a medio píxel) no deje asomar el fondo.
        val z = Rect(lado - 1f, zocalo, w - lado + 1f, suelo)
        val paradas = Array(5) { i -> val x = z.left + z.width * i / 4f; (i / 4f) to en(P.CONSOLA, 0f, Offset(x, z.center.y), a, cerca) }
        drawRect(Brush.horizontalGradient(*paradas, startX = z.left, endX = z.right), z.topLeft, z.size)
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.5f), Color.Transparent), z.top, z.top + z.height * 0.4f), z.topLeft, Size(z.width, z.height * 0.4f))
        val goma = Rect(z.left - 1f * u, z.top, z.right + 1f * u, z.top + 5f * u)
        drawRoundRect(en(P.BAQUELITA_LUZ, 0f, goma.center, a, cerca), goma.topLeft, goma.size, CornerRadius(2.5f * u))
        drawLine(a.brillo(0.25f, cerca(goma.center)), Offset(goma.left + 3f * u, goma.top + 1f * u), Offset(goma.right - 3f * u, goma.top + 1f * u), 0.8f * u)
        listOf(0.08f, 0.66f).forEach { f ->
            for (fila in 0 until 2) {
                val y = z.top + z.height * (0.45f + 0.26f * fila)
                var x = z.left + z.width * f
                while (x < z.left + z.width * (f + 0.22f)) {
                    val k = cerca(Offset(x, y))
                    drawRoundRect(Color.Black.copy(alpha = 0.75f), Offset(x, y - 1f * u), Size(5f * u, 2f * u), CornerRadius(1f * u))
                    drawLine(a.brillo(0.14f, k), Offset(x + 0.5f * u, y + 1.4f * u), Offset(x + 4.5f * u, y + 1.4f * u), 0.6f * u)
                    x += 7f * u
                }
            }
        }
        listOf(z.left + 7f * u, z.right - 7f * u).forEachIndexed { i, x -> tornilloFrente(Offset(x, z.center.y + 2f * u), 1.6f * u, a, cerca(Offset(x, z.center.y)), 40f + 60f * i) }
        // El piso de hormigón en losas: más hondo junto al mueble, con sus juntas, sus manchas y la franja de peligro.
        val piso = Rect(0f, suelo, w, h)
        drawRect(en(P.HORMIGON, 0f, Offset(w * 0.6f, piso.center.y), a, cerca), Offset(0f, piso.top - 1f), Size(w, piso.height + 2f))
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.4f), a.sombra(0.12f), Color.Transparent), piso.top, piso.top + piso.height * 0.6f), piso.topLeft, Size(w, piso.height * 0.6f))
        clipRect(piso.left, piso.top, piso.right, piso.bottom) {
            // Las juntas de las losas: una a lo largo y las de través, que se abren hacia delante.
            val junta = a.dia(P.HORMIGON_OSC, -0.1f, cerca(Offset(w / 2, piso.center.y)))
            val yj = piso.top + piso.height * 0.52f
            drawLine(junta, Offset(0f, yj), Offset(w, yj), 1.2f * u)
            drawLine(a.brillo(0.18f, cerca(Offset(w / 2, yj))), Offset(0f, yj + 1.2f * u), Offset(w, yj + 1.2f * u), 0.6f * u)
            listOf(0.08f, 0.42f, 0.78f).forEach { f ->
                val x0 = w * f; val x1 = w * (0.5f + (f - 0.5f) * 1.35f)
                drawLine(junta, Offset(x0, piso.top + 8f * u), Offset(x1, h), 1.1f * u)
            }
            // Las manchas de aceite y de humedad, y las grietas.
            for (j in 0 until 9) {
                val c = Offset(w * azar(701, j), piso.top + piso.height * (0.3f + 0.65f * azar(703, j)))
                drawOval(a.dia(Color(0xFF4A5244), 0f, cerca(c)).copy(alpha = 0.12f + 0.12f * azar(707, j)), Offset(c.x - 8f * u, c.y - 2f * u), Size((10f + 22f * azar(709, j)) * u, (2.5f + 3f * azar(711, j)) * u))
            }
            val grieta = Path().apply { moveTo(w * 0.36f, piso.top + piso.height * 0.55f); lineTo(w * 0.4f, piso.top + piso.height * 0.68f); lineTo(w * 0.38f, piso.top + piso.height * 0.8f); lineTo(w * 0.43f, h) }
            drawPath(grieta, a.sombra(0.45f), style = Stroke(0.9f * u))
            // La franja de peligro pintada al pie del zócalo, gastada.
            with(TrazosFrente) {
                franjaPeligro(Rect(0f, piso.top + 3f * u, w, piso.top + 8f * u), a.dia(P.PELIGRO, 0f, cerca(Offset(w / 2, piso.top))), a.dia(P.BAQUELITA, 0f, cerca(Offset(w / 2, piso.top))), 7f * u)
            }
            for (j in 0 until 26) drawCircle(a.dia(P.HORMIGON, 0f, cerca(Offset(w / 2, piso.top))).copy(alpha = 0.7f), (0.5f + 1.2f * azar(713, j)) * u,
                Offset(w * azar(717, j), piso.top + 3f * u + 5f * u * azar(719, j)))
            // El charco tibio de la estufa en el suelo (de noche y en los Sueños).
            if (a.lumbre) {
                val c = Offset(w * M.ESTUFA_X, h - w * 0.02f)
                val rr = w * (if (a.oscuro && !a.noche) 0.5f else 0.34f)
                drawOval(Brush.radialGradient(0f to P.ESTUFA.copy(alpha = if (a.oscuro && !a.noche) 0.34f else 0.18f), 0.5f to P.ESTUFA.copy(alpha = 0.08f), 1f to P.ESTUFA.copy(alpha = 0f),
                    center = c, radius = rr), Offset(c.x - rr, c.y - rr * 0.55f), Size(rr * 2, rr * 1.1f))
            }
        }
        // El rollo del cable grueso, en medio, con su punta que se mete bajo el zócalo.
        // Las cajas de equipo apiladas, a la derecha, cortadas por el borde: la de chapa hueso detrás y arriba, la verde delante.
        caja(Rect(w * 0.76f, h - w * 0.27f, w * 1.06f, h - w * 0.13f), a, cerca, P.HUESO_OSC)
        caja(Rect(w * 0.66f, h - w * 0.168f, w * 1.04f, h - w * 0.01f), a, cerca, Color(0xFF3B4F45))
        cable(Rect(w * 0.4f, h - w * 0.082f, w * 0.7f, h - w * 0.012f), z, a, cerca)
        // La estufa eléctrica, a la izquierda.
        estufa(Rect(w * 0.035f, h - w * 0.215f, w * 0.305f, h - w * 0.008f), a, cerca)
    }

    /** El rollo del cable grueso en [r] (vueltas tendidas en el suelo), y su punta que va hasta el zócalo [z]. */
    private fun DrawScope.cable(r: Rect, z: Rect, a: AireFrente, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(r.center)
        val g = 4.2f * u
        val color = a.dia(P.BAQUELITA, 0f, k)
        val brillo = if (a.oscuro) 0.2f + 0.6f * k else 1f
        contactoFrente(Offset(r.center.x, r.bottom - 1f * u), r.width * 1.05f, r.height * 0.5f, a, 0.35f)
        for (j in 0 until 3) {
            val e = Rect(r.left + j * 4f * u, r.top + j * 2.4f * u, r.right - j * 3f * u, r.bottom - j * 1.6f * u)
            val p = Path().apply { addOval(e) }
            with(TrazosFrente) { cableGrueso(p, g, color, brillo, if (j == 0) 0.35f else 0f) }
        }
        val punta = Path().apply { moveTo(r.right - 6f * u, r.center.y); cubicTo(r.right + 18f * u, r.top - 4f * u, z.right - z.width * 0.3f, z.bottom + 6f * u, z.right - z.width * 0.32f, z.bottom - 2f * u) }
        with(TrazosFrente) { cableGrueso(punta, g, color, brillo, 0.3f) }
    }

    /** La caja de equipo en [r]: chapa de consola oscura de cantos redondos, sus nervios, sus dos cierres de cromo y el asa. */
    private fun DrawScope.caja(r: Rect, a: AireFrente, cerca: (Offset) -> Float, verde: Color) {
        val u = density
        val k = cerca(r.center)
        contactoFrente(Offset(r.center.x, r.bottom), r.width * 1.1f, 7f * u, a, 0.45f, -1f)
        val tapa = Rect(r.left, r.top, r.right, r.top + r.height * 0.3f)
        val cuerpo = Rect(r.left, tapa.bottom, r.right, r.bottom)
        drawRoundRect(Brush.verticalGradient(listOf(a.dia(mezcla(verde, P.HUESO_LUZ, 0.12f), 0f, k), a.dia(verde, 0f, k), a.dia(mezcla(verde, P.SOMBRA, 0.4f), 0f, k)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(6f * u))
        // La junta de la tapa y los nervios del cuerpo.
        drawLine(a.sombra(0.6f), Offset(r.left, tapa.bottom), Offset(r.right, tapa.bottom), 1.4f * u)
        drawLine(a.brillo(0.25f, k), Offset(r.left + 3f * u, tapa.bottom + 1.4f * u), Offset(r.right, tapa.bottom + 1.4f * u), 0.7f * u)
        listOf(0.42f, 0.72f).forEach { f ->
            val y = cuerpo.top + cuerpo.height * f
            drawRect(a.sombra(0.3f), Offset(r.left + 4f * u, y + 1.2f * u), Size(r.width, 2.4f * u))
            drawRect(a.dia(mezcla(verde, P.HUESO_LUZ, 0.18f), 0f, k), Offset(r.left + 4f * u, y), Size(r.width, 2.2f * u))
        }
        drawLine(a.brillo(0.35f, k), Offset(r.left + 6f * u, r.top + 1.2f * u), Offset(r.right, r.top + 1.2f * u), 1f * u)
        // Los dos cierres de palanca, de cromo.
        listOf(r.left + r.width * 0.18f, r.left + r.width * 0.62f).forEach { x ->
            val c = Rect(x - 5f * u, tapa.bottom - 6f * u, x + 5f * u, tapa.bottom + 8f * u)
            drawRect(a.sombra(0.4f), Offset(c.left + 1f * u, c.top + 1.4f * u), c.size)
            drawRoundRect(Brush.horizontalGradient(listOf(a.dia(P.CROMO_LUZ, 0f, k), a.dia(P.CROMO, 0f, k), a.dia(P.CROMO_OSC, 0f, k)), c.left, c.right), c.topLeft, c.size, CornerRadius(1.5f * u))
            drawLine(a.sombra(0.5f), Offset(c.left + 1f * u, c.center.y), Offset(c.right - 1f * u, c.center.y), 0.8f * u)
        }
        // El asa de arriba, abatida.
        val asa = Rect(r.left + r.width * 0.3f, r.top - 3f * u, r.left + r.width * 0.55f, r.top + 3f * u)
        drawRoundRect(a.dia(P.BAQUELITA, 0f, k), asa.topLeft, asa.size, CornerRadius(3f * u))
        // Los golpes y la pintura saltada en los cantos.
        for (j in 0 until 5) {
            val x = r.left + r.width * azar(731, j); val y = r.top + r.height * azar(733, j)
            drawOval(a.dia(P.ACERO, 0f, k).copy(alpha = 0.6f), Offset(x, y), Size((2f + 3f * azar(737, j)) * u, (1.2f + 1.4f * azar(739, j)) * u))
        }
    }

    /**
     * La estufa eléctrica en [r]: el marco de chapa esmaltada color hueso sobre sus dos patas, el reflector de cromo curvo, las dos
     * resistencias (de noche, al rojo naranja, con su resplandor) y la rejilla de alambre delante.
     */
    private fun DrawScope.estufa(r: Rect, a: AireFrente, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(r.center)
        val arde = a.lumbre
        val fuerza = if (a.oscuro && !a.noche) 1f else if (a.oscuro) 0.4f else if (a.noche) 0.6f else 0f
        contactoFrente(Offset(r.center.x, r.bottom), r.width * 1.15f, 8f * u, a, 0.45f)
        // Las dos patas curvas.
        listOf(r.left + r.width * 0.16f, r.right - r.width * 0.16f).forEach { x ->
            drawLine(a.dia(P.CROMO_OSC, 0f, k), Offset(x, r.bottom - r.height * 0.14f), Offset(x + (if (x < r.center.x) -6f else 6f) * u, r.bottom), 2.4f * u, StrokeCap.Round)
        }
        val marco = Rect(r.left, r.top, r.right, r.bottom - r.height * 0.12f)
        if (arde) drawCircle(Brush.radialGradient(listOf(P.ESTUFA.copy(alpha = 0.3f * fuerza), P.ESTUFA.copy(alpha = 0.1f * fuerza), P.ESTUFA.copy(alpha = 0f)), marco.center, r.width * 0.78f), r.width * 0.78f, marco.center)
        drawRoundRect(Brush.horizontalGradient(listOf(a.dia(P.HUESO_LUZ, 0f, k), a.dia(P.HUESO, 0f, k), a.dia(P.HUESO_OSC, -0.1f, k)), marco.left, marco.right), marco.topLeft, marco.size, CornerRadius(9f * u))
        drawRoundRect(a.sombra(0.4f), marco.topLeft, marco.size, CornerRadius(9f * u), style = Stroke(0.8f * u))
        // El reflector de cromo curvo, más hondo en medio; de noche, encendido por las resistencias.
        val refl = Rect(marco.left + marco.width * 0.08f, marco.top + marco.height * 0.14f, marco.right - marco.width * 0.08f, marco.bottom - marco.height * 0.12f)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(refl, CornerRadius(6f * u))) }
        if (arde) drawPath(forma, Brush.verticalGradient(listOf(mezcla(P.CROMO_OSC, P.ESTUFA, 0.4f * fuerza), mezcla(P.CROMO_LUZ, P.FUEGO, 0.6f * fuerza), mezcla(P.CROMO_OSC, P.BRASA, 0.45f * fuerza)), refl.top, refl.bottom))
        else drawPath(forma, Brush.verticalGradient(listOf(a.dia(P.CROMO_OSC, 0f, k), a.dia(P.CROMO_LUZ, 0f, k), a.dia(P.CROMO, 0f, k), a.dia(P.CROMO_OSC, 0f, k)), refl.top, refl.bottom))
        // Las dos resistencias.
        listOf(0.38f, 0.66f).forEach { f ->
            val y = refl.top + refl.height * f
            if (arde) {
                drawLine(P.ESTUFA.copy(alpha = 0.4f * fuerza), Offset(refl.left + 3f * u, y), Offset(refl.right - 3f * u, y), 8f * u, StrokeCap.Round)
                drawLine(mezcla(P.BRASA, P.ESTUFA, fuerza), Offset(refl.left + 3f * u, y), Offset(refl.right - 3f * u, y), 3.6f * u, StrokeCap.Round)
                drawLine(mezcla(P.ESTUFA, P.FUEGO, 0.6f * fuerza), Offset(refl.left + 5f * u, y - 0.6f * u), Offset(refl.right - 5f * u, y - 0.6f * u), 1.4f * u, StrokeCap.Round)
            } else {
                drawLine(a.dia(Color(0xFF6E4A36), 0f, k), Offset(refl.left + 3f * u, y), Offset(refl.right - 3f * u, y), 3.6f * u, StrokeCap.Round)
                drawLine(a.claro(0.3f), Offset(refl.left + 5f * u, y - 0.8f * u), Offset(refl.right - 5f * u, y - 0.8f * u), 0.8f * u)
            }
        }
        // La rejilla de alambre delante.
        clipPath(forma) {
            var x = refl.left + 4f * u
            val alambre = if (arde) mezcla(P.CROMO_OSC, P.ESTUFA, 0.3f * fuerza) else a.dia(P.CROMO, 0f, k)
            while (x < refl.right) { drawLine(alambre.copy(alpha = 0.85f), Offset(x, refl.top), Offset(x, refl.bottom), 0.8f * u); x += 5.6f * u }
        }
        drawLine(a.brillo(0.5f, k), Offset(marco.left + 6f * u, marco.top + 1.4f * u), Offset(marco.right - 8f * u, marco.top + 1.4f * u), 1f * u)
        // La perilla del costado.
        val pc = Offset(marco.right - marco.width * 0.04f, marco.top + marco.height * 0.08f)
        drawCircle(a.dia(P.BAQUELITA, 0f, k), 3f * u, pc)
        drawCircle(a.brillo(0.3f, k), 1f * u, Offset(pc.x - 0.8f * u, pc.y - 0.8f * u))
    }
}
