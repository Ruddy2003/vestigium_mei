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
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El pie del estante de Frente: el zócalo de consola oscura con sus rejillas y su parachoques de goma (el del Ideario), el piso de
 * hormigón en losas con sus juntas, su remiendo, sus manchas, el desgaste junto a las patas y la franja de peligro pintada al
 * pie, y lo que hay en él: el cargador de baterías de válvulas (de noche, las válvulas al naranja tras su rejilla: la luz de
 * abajo), las cajas de equipo apiladas con sus cierres y el rollo del cable grueso.
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
        // Detrás del canto redondo de cada pata, por dentro, la sombra del zócalo (que no asome el fondo en esa esquina).
        val rincon = mezcla(en(P.CONSOLA, 0f, Offset(lado, suelo), a, cerca), P.SOMBRA, 0.5f)
        drawRect(rincon, Offset(lado - 7f * u, suelo - 7f * u), Size(7f * u + 1f, 7f * u + 1f))
        drawRect(rincon, Offset(w - lado - 1f, suelo - 7f * u), Size(7f * u + 1f, 7f * u + 1f))
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
        // El piso de hormigón en losas: más hondo junto al mueble, con sus juntas, una losa remendada, las manchas de aceite y de
        // humedad, el desgaste junto a las patas, las grietas, las esquirlas de piedra de las ruinas y la franja de peligro gastada.
        val piso = Rect(0f, suelo, w, h)
        val hormigon = en(P.HORMIGON, 0f, Offset(w * 0.6f, piso.center.y), a, cerca)
        drawRect(hormigon, Offset(0f, piso.top - 1f), Size(w, piso.height + 2f))
        clipRect(piso.left, piso.top, piso.right, piso.bottom) {
            val kp = cerca(Offset(w / 2, piso.center.y))
            // La losa remendada, de otro hormigón, con su borde.
            val remiendo = Rect(w * 0.47f, piso.top + piso.height * 0.55f, w * 0.71f, h + 2f)
            drawRect(a.dia(mezcla(P.HORMIGON, P.HORMIGON_OSC, 0.28f), 0f, kp), remiendo.topLeft, remiendo.size)
            drawRect(a.sombra(0.3f), remiendo.topLeft, Size(remiendo.width, 1.2f * u))
            // El desgaste junto a las patas: el hormigón pulido y más claro donde se pisa y se arrastra.
            listOf(w * M.LADO * 0.5f, w - w * M.LADO * 0.5f).forEach { x ->
                drawOval(a.dia(mezcla(P.HORMIGON, P.HUESO_LUZ, 0.32f), 0f, cerca(Offset(x, piso.top))), Offset(x - w * 0.09f, piso.top + 2f * u), Size(w * 0.18f, piso.height * 0.42f))
                for (j in 0 until 3) drawArc(a.claro(0.4f), 200f, 110f, false, Offset(x - w * (0.04f + 0.02f * j), piso.top + piso.height * (0.12f + 0.08f * j)),
                    Size(w * (0.08f + 0.04f * j), piso.height * 0.14f), style = Stroke(0.7f * u))
            }
            // La sombra del mueble en el suelo, junto al zócalo.
            drawRect(Brush.verticalGradient(listOf(a.sombra(0.42f), a.sombra(0.1f), Color.Transparent), piso.top, piso.top + piso.height * 0.45f), piso.topLeft, Size(w, piso.height * 0.45f))
            // Las juntas de las losas: una a lo largo y las de través, que se abren hacia delante, con su canto de luz.
            val junta = a.dia(P.HORMIGON_OSC, -0.25f, kp)
            val yj = piso.top + piso.height * 0.55f
            drawLine(junta, Offset(0f, yj), Offset(w, yj), 1.6f * u)
            drawLine(a.brillo(0.3f, kp), Offset(0f, yj + 1.5f * u), Offset(w, yj + 1.5f * u), 0.7f * u)
            listOf(0.08f, 0.47f, 0.84f).forEach { f ->
                val x0 = w * f; val x1 = w * (0.5f + (f - 0.5f) * 1.4f)
                drawLine(junta, Offset(x0, piso.top + 8f * u), Offset(x1, h), 1.5f * u)
                drawLine(a.brillo(0.22f, kp), Offset(x0 + 1.4f * u, piso.top + 8f * u), Offset(x1 + 1.4f * u, h), 0.6f * u)
            }
            // La mancha de aceite junto al cargador y la de humedad bajo las cajas, de borde irregular.
            drawPath(contornoIrregular(Rect(w * 0.3f, piso.top + piso.height * 0.6f, w * 0.46f, piso.top + piso.height * 0.86f), 741, 14, 0.18f), a.dia(Color(0xFF2E3330), 0f, kp).copy(alpha = 0.4f))
            drawPath(contornoIrregular(Rect(w * 0.33f, piso.top + piso.height * 0.66f, w * 0.41f, piso.top + piso.height * 0.8f), 743, 10, 0.2f), a.dia(Color(0xFF232826), 0f, kp).copy(alpha = 0.35f))
            drawPath(contornoIrregular(Rect(w * 0.6f, piso.top + piso.height * 0.25f, w * 0.97f, piso.top + piso.height * 0.62f), 747, 16, 0.15f), a.dia(Color(0xFF4A5244), 0f, kp).copy(alpha = 0.3f))
            for (j in 0 until 7) {
                val c = Offset(w * azar(701, j), piso.top + piso.height * (0.3f + 0.65f * azar(703, j)))
                drawOval(a.dia(Color(0xFF4A5244), 0f, cerca(c)).copy(alpha = 0.12f + 0.12f * azar(707, j)), Offset(c.x - 8f * u, c.y - 2f * u), Size((10f + 22f * azar(709, j)) * u, (2.5f + 3f * azar(711, j)) * u))
            }
            // Las grietas.
            val grieta = Path().apply {
                moveTo(w * 0.36f, piso.top + piso.height * 0.55f); lineTo(w * 0.4f, piso.top + piso.height * 0.68f); lineTo(w * 0.38f, piso.top + piso.height * 0.8f); lineTo(w * 0.43f, h)
                moveTo(w * 0.86f, piso.top + piso.height * 0.3f); lineTo(w * 0.9f, piso.top + piso.height * 0.42f); lineTo(w * 0.89f, piso.top + piso.height * 0.55f)
            }
            drawPath(grieta, a.sombra(0.55f), style = Stroke(1f * u))
            // La franja de peligro pintada al pie del zócalo, gastada junto a las patas.
            with(TrazosFrente) {
                franjaPeligro(Rect(0f, piso.top + 3f * u, w, piso.top + 8f * u), a.dia(P.PELIGRO, 0f, cerca(Offset(w / 2, piso.top))), a.dia(P.BAQUELITA, 0f, cerca(Offset(w / 2, piso.top))), 7f * u)
            }
            listOf(w * 0.05f, w * 0.31f, w * 0.9f).forEachIndexed { j, x ->
                drawPath(contornoIrregular(Rect(x, piso.top + 2.5f * u, x + w * (0.05f + 0.03f * j), piso.top + 8.5f * u), 751 + j, 10, 0.3f), hormigon)
            }
            // Las esquirlas de piedra caídas de las ruinas, con su sombra.
            for (j in 0 until 6) {
                val c = Offset(w * (0.62f + 0.3f * azar(761, j)), piso.top + piso.height * (0.3f + 0.6f * azar(763, j)))
                val s = (1.6f + 2.6f * azar(767, j)) * u
                drawOval(a.sombra(0.4f), Offset(c.x - s * 0.6f, c.y + s * 0.2f), Size(s * 2.2f, s * 0.8f))
                drawPath(poligonoFrente(c.x - s, c.y + s * 0.4f, c.x - s * 0.3f, c.y - s * 0.7f, c.x + s * 0.8f, c.y - s * 0.3f, c.x + s, c.y + s * 0.4f), a.dia(P.PIEDRA, 0f, cerca(c)))
                drawLine(a.claro(0.4f), Offset(c.x - s * 0.6f, c.y - s * 0.1f), Offset(c.x - s * 0.2f, c.y - s * 0.6f), 0.6f * u)
            }
            // El charco tibio de las válvulas del cargador en el suelo (de noche y en los Sueños).
            if (a.lumbre) {
                val c = Offset(w * M.CARGADOR_X + w * 0.02f, h - w * 0.012f)
                val fuerte = a.oscuro && !a.noche
                val rr = w * (if (fuerte) 0.42f else 0.3f)
                drawOval(Brush.radialGradient(0f to P.VALVULA.copy(alpha = if (fuerte) 0.38f else 0.2f), 0.45f to P.VALVULA.copy(alpha = if (fuerte) 0.12f else 0.06f), 1f to P.VALVULA.copy(alpha = 0f),
                    center = c, radius = rr), Offset(c.x - rr, c.y - rr * 0.5f), Size(rr * 2, rr))
            }
        }
        // Las cajas de equipo apiladas, a la derecha, cortadas por el borde: la de chapa hueso detrás y arriba, la verde delante.
        caja(Rect(w * 0.76f, h - w * 0.27f, w * 1.06f, h - w * 0.13f), a, cerca, P.HUESO_OSC)
        caja(Rect(w * 0.66f, h - w * 0.168f, w * 1.04f, h - w * 0.01f), a, cerca, Color(0xFF3B4F45))
        // El rollo del cable grueso, en medio, con su punta que se mete bajo el zócalo.
        cable(Rect(w * 0.4f, h - w * 0.082f, w * 0.7f, h - w * 0.012f), z, a, cerca)
        // El cargador de válvulas, a la izquierda, delante de la pata.
        cargador(Rect(w * 0.025f, h - w * 0.205f, w * 0.29f, h - w * 0.008f), a, cerca)
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
     * El cargador de baterías de válvulas en [r], en el suelo: la caja de consola oscura de cantos redondos sobre sus tacos, el asa
     * de cromo arriba y el frente de chapa hueso con sus tornillos; a la izquierda, la ventana con las tres válvulas tras la rejilla
     * de barrotes (de noche, sus filamentos al naranja y su resplandor, que sale entre los barrotes); a la derecha, el amperímetro
     * sin cifras, la perilla, el piloto y la palanca.
     */
    private fun DrawScope.cargador(r: Rect, a: AireFrente, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(r.center)
        val arde = a.lumbre
        val fuerza = if (a.oscuro && !a.noche) 1f else if (a.oscuro) 0.45f else if (a.noche) 0.6f else 0f
        contactoFrente(Offset(r.center.x, r.bottom), r.width * 1.12f, 8f * u, a, 0.5f)
        // Los tacos de goma.
        listOf(r.left + r.width * 0.12f, r.right - r.width * 0.12f).forEach { x ->
            drawRoundRect(a.dia(P.BAQUELITA, 0f, k), Offset(x - 5f * u, r.bottom - 3.5f * u), Size(10f * u, 3.5f * u), CornerRadius(1.5f * u))
        }
        val caja = Rect(r.left, r.top + r.height * 0.1f, r.right, r.bottom - 3f * u)
        // El asa de cromo, abatida hacia atrás, con sus dos soportes.
        val asa = Rect(caja.left + caja.width * 0.25f, r.top, caja.right - caja.width * 0.25f, caja.top + 3f * u)
        drawRoundRect(a.dia(P.CROMO_OSC, 0f, k), asa.topLeft, asa.size, CornerRadius(asa.height * 0.6f), style = Stroke(2.2f * u))
        drawLine(a.brillo(0.5f, k), Offset(asa.left + 4f * u, asa.top + 0.6f * u), Offset(asa.right - 4f * u, asa.top + 0.6f * u), 0.7f * u)
        // La caja: consola oscura, más clara arriba, con su canto de luz.
        drawRoundRect(Brush.verticalGradient(listOf(a.dia(P.CONSOLA_LUZ, 0.05f, k), a.dia(P.CONSOLA, 0f, k), a.dia(P.CONSOLA_OSC, 0f, k)), caja.top, caja.bottom), caja.topLeft, caja.size, CornerRadius(6f * u))
        drawRoundRect(a.sombra(0.5f), caja.topLeft, caja.size, CornerRadius(6f * u), style = Stroke(0.8f * u))
        drawLine(a.brillo(0.35f, k), Offset(caja.left + 6f * u, caja.top + 1.2f * u), Offset(caja.right - 6f * u, caja.top + 1.2f * u), 1f * u)
        // El frente de chapa hueso.
        val frente = Rect(caja.left + 4f * u, caja.top + 5f * u, caja.right - 4f * u, caja.bottom - 4f * u)
        drawRoundRect(a.dia(P.HUESO, 0f, k), frente.topLeft, frente.size, CornerRadius(3f * u))
        drawRect(a.claro(0.3f), Offset(frente.left + 3f * u, frente.top + 1f * u), Size(frente.width - 6f * u, 1.4f * u))
        drawRect(a.sombra(0.18f), Offset(frente.left, frente.bottom - frente.height * 0.12f), Size(frente.width, frente.height * 0.12f))
        listOf(Offset(frente.left + 3f * u, frente.top + 3f * u), Offset(frente.right - 3f * u, frente.top + 3f * u),
            Offset(frente.left + 3f * u, frente.bottom - 3f * u), Offset(frente.right - 3f * u, frente.bottom - 3f * u)).forEachIndexed { i, o -> tornilloFrente(o, 1.3f * u, a, k, 20f + 50f * i) }
        // La ventana de las válvulas: el hondo, las tres válvulas y, delante, la rejilla de barrotes.
        val ven = Rect(frente.left + frente.width * 0.07f, frente.top + frente.height * 0.14f, frente.left + frente.width * 0.6f, frente.bottom - frente.height * 0.14f)
        drawRect(a.sombra(0.55f), Offset(ven.left - 1f * u, ven.top - 1f * u), Size(ven.width + 2f * u, ven.height + 2f * u))
        drawRect(if (arde) mezcla(Color(0xFF1A0E08), P.VALVULA, 0.25f * fuerza) else a.dia(Color(0xFF141816), 0f, k), ven.topLeft, ven.size)
        clipRect(ven.left, ven.top, ven.right, ven.bottom) {
            if (arde) drawCircle(Brush.radialGradient(listOf(P.VALVULA.copy(alpha = 0.6f * fuerza), P.VALVULA.copy(alpha = 0f)), Offset(ven.center.x, ven.bottom - ven.height * 0.35f), ven.width * 0.6f),
                ven.width * 0.6f, Offset(ven.center.x, ven.bottom - ven.height * 0.35f))
            for (j in 0 until 3) {
                val cx = ven.left + ven.width * (0.2f + 0.3f * j)
                val vw = ven.width * 0.2f
                val tope = ven.top + ven.height * (if (j == 1) 0.08f else 0.18f)
                val v = Rect(cx - vw / 2, tope, cx + vw / 2, ven.bottom - ven.height * 0.1f)
                // La base de baquelita, la ampolla de vidrio y las placas; de noche, el filamento al naranja.
                drawRect(a.dia(P.BAQUELITA, 0f, k), Offset(v.left - vw * 0.1f, v.bottom), Size(vw * 1.2f, ven.height * 0.1f))
                drawRoundRect(if (arde) mezcla(Color(0xFF3A2414), P.VALVULA, 0.35f * fuerza) else a.dia(mezcla(P.VIDRIO_LUZ, P.HUESO, 0.5f), -0.3f, k), v.topLeft, v.size, CornerRadius(vw * 0.5f, vw * 0.5f))
                drawRect(a.dia(P.CROMO_OSC, if (arde) -0.2f else 0f, k), Offset(v.left + vw * 0.2f, v.top + v.height * 0.32f), Size(vw * 0.6f, v.height * 0.46f))
                if (arde) {
                    drawRect(mezcla(P.VALVULA, P.FUEGO, 0.5f * fuerza), Offset(v.left + vw * 0.38f, v.top + v.height * 0.36f), Size(vw * 0.24f, v.height * 0.36f))
                    drawCircle(P.VALVULA.copy(alpha = 0.35f * fuerza), vw * 0.7f, Offset(v.center.x, v.top + v.height * 0.55f))
                }
                drawRect(a.dia(mezcla(P.CROMO, P.ACERO, 0.4f), 0f, k), Offset(v.left + vw * 0.1f, v.top + vw * 0.15f), Size(vw * 0.8f, v.height * 0.1f))
                drawLine(Color.White.copy(alpha = if (arde) 0.25f else 0.5f), Offset(v.left + vw * 0.22f, v.top + vw * 0.5f), Offset(v.left + vw * 0.22f, v.bottom - vw * 0.3f), 1f * u)
            }
        }
        // La rejilla: barrotes de chapa delante de las válvulas.
        val barrotes = 7
        for (j in 0..barrotes) {
            val x = ven.left + ven.width * j / barrotes
            drawLine(a.dia(P.CONSOLA_OSC, 0f, k), Offset(x, ven.top), Offset(x, ven.bottom), 1.8f * u)
            drawLine(if (arde) P.VALVULA.copy(alpha = 0.5f * fuerza) else a.brillo(0.25f, k), Offset(x + 1.1f * u, ven.top), Offset(x + 1.1f * u, ven.bottom), 0.6f * u)
        }
        drawRect(a.dia(P.CROMO_OSC, 0f, k), ven.topLeft, ven.size, style = Stroke(1.2f * u))
        // El amperímetro sin cifras: la cara crema, su arco de marcas y la aguja.
        val am = Rect(frente.left + frente.width * 0.66f, frente.top + frente.height * 0.14f, frente.right - frente.width * 0.08f, frente.top + frente.height * 0.5f)
        drawRect(a.dia(P.CROMO_OSC, 0f, k), am.inflate(1.4f * u).topLeft, am.inflate(1.4f * u).size)
        drawRect(if (arde) mezcla(P.CINTA, P.VALVULA, 0.3f * fuerza) else a.dia(P.CINTA, 0.1f, k), am.topLeft, am.size)
        val ac = Offset(am.center.x, am.bottom + am.height * 0.45f)
        val ar = am.height * 1.05f
        clipRect(am.left, am.top, am.right, am.bottom) {
            drawArc(a.tinta(1f).copy(alpha = 0.7f), 240f, 60f, false, Offset(ac.x - ar, ac.y - ar), Size(ar * 2, ar * 2), style = Stroke(0.7f * u))
            for (j in 0..6) {
                val g = Math.toRadians(240.0 + j * 10.0)
                drawLine(a.tinta(1f).copy(alpha = 0.7f), Offset(ac.x + ar * kotlin.math.cos(g).toFloat(), ac.y + ar * kotlin.math.sin(g).toFloat()),
                    Offset(ac.x + ar * 1.12f * kotlin.math.cos(g).toFloat(), ac.y + ar * 1.12f * kotlin.math.sin(g).toFloat()), 0.7f * u)
            }
            val g = Math.toRadians(if (arde) 276.0 else 246.0)
            drawLine(Color(0xFF1A1A18), ac, Offset(ac.x + ar * 1.15f * kotlin.math.cos(g).toFloat(), ac.y + ar * 1.15f * kotlin.math.sin(g).toFloat()), 0.9f * u)
        }
        // La perilla, el piloto y la palanca.
        val pc = Offset(am.left + am.width * 0.3f, frente.top + frente.height * 0.74f)
        drawCircle(a.sombra(0.45f), 4.6f * u, Offset(pc.x + 0.8f * u, pc.y + 1.2f * u))
        drawCircle(a.dia(P.BAQUELITA, 0f, k), 4.4f * u, pc)
        drawCircle(a.dia(P.BAQUELITA_LUZ, 0f, k), 3f * u, Offset(pc.x - 0.5f * u, pc.y - 0.6f * u))
        drawLine(a.dia(P.HUESO_LUZ, 0f, k), pc, Offset(pc.x + 2.6f * u, pc.y - 2.6f * u), 1f * u, StrokeCap.Round)
        pilotoFrente(Offset(am.right - am.width * 0.12f, frente.top + frente.height * 0.7f), 2f * u, P.AMBAR, arde || a.noche, a, k)
        val pal = Offset(am.right - am.width * 0.12f, frente.top + frente.height * 0.86f)
        drawCircle(a.dia(P.CROMO, 0f, k), 1.8f * u, pal)
        drawLine(a.dia(P.CROMO_LUZ, 0f, k), pal, Offset(pal.x + 0.5f * u, pal.y - 4f * u), 1.2f * u, StrokeCap.Round)
        // De noche, el filo naranja de las válvulas en el canto de la caja y en el frente, debajo de la ventana.
        if (arde) {
            drawRect(P.VALVULA.copy(alpha = 0.3f * fuerza), Offset(ven.left, ven.bottom + 1.2f * u), Size(ven.width, 2.4f * u))
            drawCircle(Brush.radialGradient(listOf(P.VALVULA.copy(alpha = 0.22f * fuerza), P.VALVULA.copy(alpha = 0f)), ven.center, r.width * 0.75f), r.width * 0.75f, ven.center)
        }
    }
}
