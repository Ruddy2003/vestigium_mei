package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * La pared del estante de Revolución ([MuebleEstanteRevolucion]): el yeso de cal teñido de ocre rosado de una casa de vecinos
 * de París, con la cenefa de plantilla bajo el techo (rombos y pastillas en sangre de toro y cardenillo, gastada), el moteado
 * de las manos de cal, el humo de la vela, las huellas de tinta junto a la ventana, las grietas y el desconchón que deja ver el
 * listón; la ventana del cajista arriba a la izquierda (el derrame enlucido, el marco gris, la vidriera de cuarterones con uno
 * de papel aceitado y, detrás, el cielo y la fachada de enfrente), por donde de día entra la mancha de los cuarterones; y el
 * tendedero de la imprenta, el cordel con los pliegos recién tirados colgando a secar.
 */
internal object MuroEstanteRevolucion {
    private val P = PaletaRevolucion

    /** La cenefa: su borde de arriba y de abajo, en anchos (encima, la sombra del techo). */
    const val CENEFA0 = 0.014f
    const val CENEFA1 = 0.05f
    /** La ventana: el vano en la pared (izquierda, arriba, derecha, abajo), en anchos; la izquierda queda fuera del cuadro. */
    const val V_IZQ = -0.02f
    const val V_ARR = 0.075f
    const val V_DER = 0.215f
    const val V_ABA = 0.37f
    /** Cuánto se mete el derrame hasta el marco, en anchos. */
    private const val DERRAME = 0.03f

    /** El yeso a la luz del momento ([lit]: dentro de la mancha de la ventana). */
    private fun yeso(a: AireRevolucion, c: Color, k: Float, lit: Float, m: ManchaRevolucion?): Color = a.en(c, 0f, k, lit, m)

    // ---------------------------------------------------------------- la pared entera (arriba)

    fun DrawScope.pared(a: AireRevolucion, apoyo: Float, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width
        val fondo = Rect(0f, 0f, w, apoyo + w * 0.02f)
        baseYeso(fondo, a, null)
        pintura(fondo, a, cerca, null)
        // La mancha de la ventana: la misma pared, alumbrada, dentro de los cuarterones (y el de papel, a media luz).
        if (m != null) {
            clipRect(w * V_DER, fondo.top, fondo.right, fondo.bottom) {
                val medio = ManchaRevolucion(m.q, m.color, m.fuerza * 0.45f)
                clipPath(m.forma(w, 0f, 0.02f)) { baseYeso(fondo, a, medio); pintura(fondo, a, cerca, medio) }
                clipPath(m.forma(w, 0f, 0f, papel = true)) { baseYeso(fondo, a, medio); pintura(fondo, a, cerca, medio) }
                clipPath(m.forma(w)) { baseYeso(fondo, a, m); pintura(fondo, a, cerca, m) }
            }
        }
        techo(a, cerca)
        ventana(a, cerca, m)
        tendedero(a, cerca, m)
    }

    /** El yeso liso de [r]: de día, a la sombra del cuarto y más vivo junto a la ventana; de noche, el charco de la vela. */
    private fun DrawScope.baseYeso(r: Rect, a: AireRevolucion, m: ManchaRevolucion?) {
        val w = size.width
        if (m != null && !a.oscuro) {
            val c0 = a.en(mezcla(P.YESO, P.YESO_SOMBRA, 0.15f), 0f, 1f, 1f, m); val c1 = a.en(mezcla(P.YESO, P.YESO_SOMBRA, 0.35f), 0f, 1f, 1f, m)
            drawRect(Brush.verticalGradient(listOf(c0, c1), m.en(0f, 0f).y * w, m.en(1f, 1f).y * w), r.topLeft, r.size)
            return
        }
        if (a.lumbre && a.oscuro) {
            // El charco de la vela: el color del yeso a cada distancia de la llama (un solo degradado, en la imagen guardada).
            val c = Offset(w * MuebleEstanteRevolucion.VELA_X, w * MuebleEstanteRevolucion.VELA_Y)
            val radios = floatArrayOf(0f, 0.05f, 0.11f, 0.18f, 0.27f, 0.38f, 0.52f, 0.7f, 0.9f)
            val col = radios.map { rr -> a.luz(mezcla(P.YESO, P.YESO_SOMBRA, 0.2f), MuebleEstanteRevolucion.velaArriba(rr, 0f, a.noche)) }
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 0.9f to col[i] }, center = c, radius = w * 0.9f), r.topLeft, r.size)
            if (m != null) clipPath(m.forma(w)) { drawRect(Color(0xFF7890C8).copy(alpha = (0.5f * m.fuerza).coerceAtMost(1f)), r.topLeft, r.size, blendMode = BlendMode.Screen) }
            return
        }
        // De día (y al ocaso): el yeso a la sombra del cuarto, más vivo junto a la ventana y más hondo a la derecha y arriba.
        val claro = a.dia(mezcla(P.YESO, P.YESO_SOMBRA, 0.3f), 0f)
        val hondo = a.dia(mezcla(P.YESO_SOMBRA, P.YESO_HONDO, 0.18f), 0f)
        drawRect(Brush.horizontalGradient(listOf(claro, mezcla(claro, hondo, 0.45f), hondo), r.left + w * 0.2f, r.right), r.topLeft, r.size)
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.2f), a.sombra(0f)), r.top, r.top + w * 0.18f), r.topLeft, Size(r.width, w * 0.18f))
    }

    /**
     * Lo pintado en el yeso (con mezcla de multiplicar, así sigue la luz que tenga debajo): el moteado de las manos de cal, los
     * brochazos, la cenefa de plantilla, el humo de la vela, las huellas de tinta, la mancha de humedad y las grietas; y el
     * desconchón, donde asoma el listón.
     */
    private fun DrawScope.pintura(r: Rect, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        // El moteado de las manos de cal y los brochazos a lo largo.
        val motas = Path()
        for (j in 0 until 20) {
            val mx = r.left + r.width * azar(7050, j); val my = w * (CENEFA1 + 0.01f) + (r.bottom - w * CENEFA1) * azar(7051, j)
            val mw = w * (0.07f + 0.12f * azar(7052, j)); val mh = mw * (0.3f + 0.25f * azar(7053, j))
            motas.addOval(Rect(mx - mw / 2, my - mh / 2, mx + mw / 2, my + mh / 2))
        }
        drawPath(motas, Color(0xFFF2E2D4).copy(alpha = 0.32f), blendMode = BlendMode.Multiply)
        val brochazos = Path()
        for (j in 0 until 40) {
            val bx = r.left + r.width * azar(7060, j); val by = w * (CENEFA1 + 0.015f) + (r.bottom - w * CENEFA1) * azar(7061, j)
            val bl = w * (0.06f + 0.12f * azar(7062, j))
            brochazos.moveTo(bx, by); brochazos.quadraticTo(bx + bl * 0.5f, by - w * 0.007f, bx + bl, by + w * 0.002f)
        }
        drawPath(brochazos, Color(0xFFEAD6C6).copy(alpha = 0.5f), style = Stroke(w * 0.005f, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        cenefa(r, a, cerca, m)
        // El humo de la vela: una lengua parda que sube de la llama y se abre bajo la cenefa.
        val vx = w * MuebleEstanteRevolucion.VELA_X
        val humo = Path().apply {
            moveTo(vx - w * 0.008f, w * 0.31f)
            cubicTo(vx - w * 0.03f, w * 0.24f, vx - w * 0.07f, w * 0.15f, vx - w * 0.055f, w * 0.06f)
            lineTo(vx + w * 0.065f, w * 0.06f)
            cubicTo(vx + w * 0.075f, w * 0.15f, vx + w * 0.03f, w * 0.24f, vx + w * 0.008f, w * 0.31f); close()
        }
        drawPath(humo, Color(0xFF8A766E).copy(alpha = 0.17f), blendMode = BlendMode.Multiply)
        drawPath(Path().apply { addPath(humo, Offset(0f, -w * 0.025f)) }, Color(0xFF8A766E).copy(alpha = 0.12f), blendMode = BlendMode.Multiply)
        // Las huellas de tinta de los cajistas junto a la ventana: dedos negros donde se apoyan al pasar.
        val huellas = Path()
        for (j in 0 until 7) {
            val hx = w * (0.235f + 0.05f * azar(7100, j)); val hy = w * (0.2f + 0.2f * azar(7101, j))
            val hw = w * (0.007f + 0.004f * azar(7102, j))
            huellas.addOval(Rect(hx, hy, hx + hw, hy + hw * 1.4f))
        }
        drawPath(huellas, Color(0xFF3A3038).copy(alpha = 0.32f), blendMode = BlendMode.Multiply)
        // La mancha de humedad, abajo a la derecha, que sube desde la cornisa: el paño pardo y su cerco.
        val humedad = Path().apply {
            moveTo(w * 0.83f, r.bottom)
            cubicTo(w * 0.82f, w * 0.42f, w * 0.86f, w * 0.37f, w * 0.9f, w * 0.39f)
            cubicTo(w * 0.93f, w * 0.35f, w * 0.98f, w * 0.37f, w * 1.01f, w * 0.4f)
            lineTo(w * 1.01f, r.bottom); close()
        }
        drawPath(humedad, Color(0xFFE2C8B2).copy(alpha = 0.6f), blendMode = BlendMode.Multiply)
        drawPath(humedad, Color(0xFFB08A70).copy(alpha = 0.45f), style = Stroke(1.3f * u), blendMode = BlendMode.Multiply)
        // Las grietas finas.
        val grietas = Path()
        listOf(Triple(0.34f, 0.06f, 7200), Triple(0.97f, 0.12f, 7210), Triple(0.55f, 0.29f, 7220)).forEach { (gx, gy, s) ->
            var p = Offset(w * gx, w * gy)
            grietas.moveTo(p.x, p.y)
            for (q in 0 until 6) {
                p = Offset(p.x + w * (azar(s, q) - 0.4f) * 0.02f, p.y + w * (0.008f + 0.012f * azar(s, q + 10)))
                grietas.lineTo(p.x, p.y)
            }
        }
        drawPath(grietas, Color(0xFF6E5048).copy(alpha = 0.7f), style = Stroke(0.8f * u, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        desconchon(Rect(w * 0.865f, w * 0.29f, w * 0.965f, w * 0.355f), 7300, a, cerca, m)
        desconchon(Rect(w * 0.4f, w * 0.058f, w * 0.448f, w * 0.085f), 7310, a, cerca, m)
    }

    /** La cenefa de plantilla bajo el techo: dos filetes de sangre de toro y, entre ellos, rombos de cardenillo y pastillas. */
    private fun DrawScope.cenefa(r: Rect, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val c0 = w * CENEFA0; val c1 = w * CENEFA1
        val sangre = Color(0xFF9A4A3E); val verde = Color(0xFF6E8E80)
        drawRect(sangre.copy(alpha = 0.8f), Offset(r.left, c0 + 1.5f * u), Size(r.width, 2.4f * u), blendMode = BlendMode.Multiply)
        drawRect(sangre.copy(alpha = 0.8f), Offset(r.left, c1 - 4f * u), Size(r.width, 2.4f * u), blendMode = BlendMode.Multiply)
        val rombos = Path(); val pastillas = Path()
        val paso = w * 0.05f
        val ym = (c0 + c1) / 2; val hr = (c1 - c0) * 0.3f
        var x = r.left + paso * 0.5f
        while (x < r.right + paso) {
            rombos.moveTo(x, ym - hr); rombos.lineTo(x + paso * 0.24f, ym); rombos.lineTo(x, ym + hr); rombos.lineTo(x - paso * 0.24f, ym); rombos.close()
            pastillas.addOval(Rect(x + paso * 0.5f - 1.9f * u, ym - 1.9f * u, x + paso * 0.5f + 1.9f * u, ym + 1.9f * u))
            x += paso
        }
        drawPath(rombos, verde.copy(alpha = 0.85f), blendMode = BlendMode.Multiply)
        drawPath(pastillas, sangre.copy(alpha = 0.85f), blendMode = BlendMode.Multiply)
        // Lo gastado de la plantilla: la cal que la tapa a trozos.
        for (j in 0 until 11) {
            val gx = r.left + r.width * azar(7400, j)
            val lit = if (m != null) 1f else 0f
            drawRect(mezcla(yeso(a, P.YESO_LUZ, cerca(Offset(gx, c0)), lit, m), Color.Transparent, 0.3f),
                Offset(gx, c0 + (c1 - c0) * (0.15f + 0.55f * azar(7401, j))), Size(w * (0.01f + 0.03f * azar(7402, j)), (1.6f + 2f * azar(7403, j)) * u))
        }
    }

    /** Un desconchón: el hueco con el listón y su sombra, la sombra que echa el borde del yeso y el canto alumbrado del otro lado. */
    private fun DrawScope.desconchon(r: Rect, s: Int, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val u = density
        val k = cerca(r.center)
        val lit = if (m != null) 1f else 0f
        val hueco = Path()
        val n = 12
        for (j in 0..n) {
            val t = j / n.toFloat() * 6.2832f
            val rr = 0.76f + 0.24f * azar(s, j % n)
            val p = Offset(r.center.x + kotlin.math.cos(t) * r.width / 2 * rr, r.center.y + kotlin.math.sin(t) * r.height / 2 * rr)
            if (j == 0) hueco.moveTo(p.x, p.y) else hueco.lineTo(p.x, p.y)
        }
        hueco.close()
        // Detrás del yeso, el hondo entre listones y los listones de castaño, a lo largo.
        drawPath(hueco, yeso(a, P.YESO_HONDO, k, lit, m))
        clipPath(hueco) {
            val lh = r.height * 0.26f
            var y = r.top + r.height * 0.06f
            while (y < r.bottom) {
                drawRect(yeso(a, P.LISTON, k, lit, m), Offset(r.left, y), Size(r.width, lh))
                drawRect(a.claro(0.25f), Offset(r.left, y), Size(r.width, 0.8f * u))
                drawRect(a.sombra(0.35f), Offset(r.left, y + lh - 1f * u), Size(r.width, 1f * u))
                y += lh + r.height * 0.1f
            }
            translate(-1.8f * u, -2f * u) { drawPath(hueco, a.sombra(0.55f), style = Stroke(4.4f * u)) }
        }
        clipRect(r.center.x - r.width * 0.1f, r.center.y, r.right + 2f * u, r.bottom + 2f * u) {
            drawPath(hueco, yeso(a, P.YESO_LUZ, k, lit, m).copy(alpha = 0.85f), style = Stroke(1.3f * u))
        }
    }

    /** El techo: la moldura de yeso arriba, con su sombra en la pared. */
    private fun DrawScope.techo(a: AireRevolucion, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val tramos = if (a.oscuro) 12 else 1
        for (t in 0 until tramos) {
            val x0 = w * t / tramos; val x1 = w * (t + 1) / tramos + 1f
            val k = cerca(Offset((x0 + x1) / 2, 0f))
            drawRect(a.dia(P.YESO_SOMBRA, -0.1f, k), Offset(x0, 0f), Size(x1 - x0, w * CENEFA0 * 0.55f))
            drawRect(a.dia(P.YESO, 0.05f, k), Offset(x0, w * CENEFA0 * 0.55f), Size(x1 - x0, w * CENEFA0 * 0.45f))
        }
        drawRect(a.claro(0.3f), Offset(0f, w * CENEFA0 * 0.55f), Size(w, 0.9f * u))
        drawRect(a.sombra(0.4f), Offset(0f, w * CENEFA0), Size(w, 1.6f * u))
        drawRect(a.sombra(0.14f), Offset(0f, w * CENEFA0 + 1.6f * u), Size(w, w * 0.008f))
    }

    // ---------------------------------------------------------------- la ventana del cajista

    /**
     * La ventana: el derrame enlucido (el lado de la derecha y el de arriba, que se abren hacia el cuarto), el marco gris con su
     * vierteaguas, la vidriera de cuarterones chicos con el montante de en medio, el cuarterón de papel aceitado y, detrás, el
     * cielo y la fachada de enfrente (de noche, la calle oscura con una ventana encendida; en los Sueños, la luna).
     */
    private fun DrawScope.ventana(a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val k = cerca(Offset(w * 0.1f, w * 0.22f))
        val vano = Rect(w * V_IZQ, w * V_ARR, w * V_DER, w * V_ABA)
        val marco = Rect(vano.left, vano.top + w * DERRAME * 0.7f, vano.right - w * DERRAME, vano.bottom - w * 0.012f)
        // El derrame: el de arriba (intradós, en sombra) y el de la derecha (de cara a quien mira, a media luz).
        drawRect(a.dia(P.YESO_SOMBRA, -0.05f, k), vano.topLeft, vano.size)
        drawPath(poligonoRevolucion(vano.right, vano.top, vano.right, vano.bottom, marco.right, marco.bottom, marco.right, marco.top), a.dia(P.YESO, 0.08f, k))
        drawPath(poligonoRevolucion(vano.left, vano.top, vano.right, vano.top, marco.right, marco.top, marco.left, marco.top), a.dia(P.YESO_SOMBRA, -0.18f, k))
        drawLine(a.sombra(0.3f), Offset(vano.right, vano.top), Offset(marco.right, marco.top), 1.2f * u)
        // El cielo y la fachada de enfrente, por los vidrios.
        val vidrio = Rect(marco.left, marco.top + w * 0.012f, marco.right - w * 0.012f, marco.bottom - w * 0.016f)
        val afuera = when {
            a.oscuro -> listOf(P.CIELO_NOCHE_ALTO, P.CIELO_NOCHE)
            a.noche -> listOf(Color(0xFF6A4E8A), Color(0xFFE8906A), Color(0xFFF6C27A))
            else -> listOf(P.CIELO_ALTO, mezcla(P.CIELO_ALTO, P.CIELO, 0.6f), P.CIELO)
        }
        drawRect(Brush.verticalGradient(afuera, vidrio.top, vidrio.top + vidrio.height * 0.55f), vidrio.topLeft, vidrio.size)
        clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
            // La fachada de enfrente: su cornisa, el yeso y una ventana con su postigo; de noche, oscura con una ventana encendida.
            val fy = vidrio.top + vidrio.height * 0.5f
            val fach = when {
                a.oscuro -> Color(0xFF1C2038)
                a.noche -> Color(0xFFD99A78)
                else -> P.ENFRENTE
            }
            drawRect(fach, Offset(vidrio.left, fy), Size(vidrio.width, vidrio.bottom - fy))
            drawRect(if (a.oscuro) Color(0xFF12162A) else mezcla(fach, Color(0xFF5A4A44), 0.35f), Offset(vidrio.left, fy - w * 0.006f), Size(vidrio.width, w * 0.008f))
            val vx = vidrio.left + vidrio.width * 0.38f
            val vv = Rect(vx, fy + w * 0.03f, vx + w * 0.05f, vidrio.bottom + w * 0.02f)
            if (a.oscuro) {
                drawRect(Color(0xFFE8B868).copy(alpha = if (a.noche) 0.55f else 0.85f), vv.topLeft, vv.size)
                drawRect(Color(0xFF7A4A2A).copy(alpha = 0.6f), Offset(vv.left + vv.width * 0.48f, vv.top), Size(w * 0.004f, vv.height))
            } else {
                drawRect(mezcla(fach, Color(0xFF2A2A34), 0.55f), vv.topLeft, vv.size)
                drawRect(mezcla(fach, Color(0xFF3E5A4E), 0.5f), Offset(vv.right, vv.top), Size(w * 0.022f, vv.height))
            }
            if (a.oscuro) {
                for (j in 0 until 7) {
                    val o = Offset(vidrio.left + vidrio.width * azar(7500, j), vidrio.top + vidrio.height * 0.45f * azar(7501, j))
                    drawCircle(Color(0xFFE6ECF8).copy(alpha = 0.45f + 0.5f * azar(7502, j)), (0.6f + 0.7f * azar(7503, j)) * u, o)
                }
                if (a.noche) {
                    val c = Offset(vidrio.left + vidrio.width * 0.66f, vidrio.top + vidrio.height * 0.2f)
                    drawCircle(P.LUNA.copy(alpha = 0.22f), w * 0.022f, c)
                    drawCircle(P.LUNA, w * 0.012f, c)
                }
            }
            // Cada vidrio soplado, de su tono (alguno más verdoso, alguno con su burbuja de luz).
            val col = vidrio.width / 3f; val fil = vidrio.height / 4f
            for (c in 0 until 3) for (f in 0 until 4) {
                val z = azar(7520 + c, f)
                val tinte = if (z < 0.35f) Color(0xFF6E8A6A).copy(alpha = 0.16f) else if (z > 0.8f) Color.White.copy(alpha = if (a.oscuro) 0.03f else 0.12f) else Color.Transparent
                drawRect(tinte, Offset(vidrio.left + col * c, vidrio.top + fil * f), Size(col, fil))
                if (!a.oscuro) drawLine(Color.White.copy(alpha = 0.35f), Offset(vidrio.left + col * (c + 0.2f), vidrio.top + fil * (f + 0.25f)), Offset(vidrio.left + col * (c + 0.45f), vidrio.top + fil * (f + 0.12f)), 1f * u)
            }
            // El cuarterón de papel aceitado, abajo a la derecha: ámbar tamizado con su arruga y un remiendo.
            val pp = Rect(vidrio.left + col * 2, vidrio.top + fil * 3, vidrio.right, vidrio.bottom)
            val aceitado = when {
                a.oscuro -> Color(0xFF3A2C24)
                a.noche -> Color(0xFFF0A868)
                else -> Color(0xFFE8C88A)
            }
            drawRect(aceitado, pp.topLeft, pp.size)
            drawRect(Color(0xFF8A6A3A).copy(alpha = 0.3f), Offset(pp.left, pp.top + pp.height * 0.55f), Size(pp.width, pp.height * 0.45f))
            drawLine(Color(0xFF8A6A3A).copy(alpha = 0.45f), Offset(pp.left + pp.width * 0.15f, pp.top + pp.height * 0.2f), Offset(pp.right - pp.width * 0.2f, pp.bottom - pp.height * 0.3f), 1f * u)
            drawRect(mezcla(aceitado, Color.White, 0.15f), Offset(pp.left + pp.width * 0.55f, pp.top + pp.height * 0.1f), Size(pp.width * 0.3f, pp.height * 0.3f))
        }
        // El marco gris y los listones (petits bois) de la vidriera, con el montante grueso de en medio.
        val gris = Color(0xFFD2CABA)
        val listones = Path()
        val col = vidrio.width / 3f; val fil = vidrio.height / 4f
        for (c in 1 until 3) listones.addRect(Rect(vidrio.left + col * c - w * 0.003f, vidrio.top, vidrio.left + col * c + w * 0.003f, vidrio.bottom))
        for (f in 1 until 4) listones.addRect(Rect(vidrio.left, vidrio.top + fil * f - w * 0.003f, vidrio.right, vidrio.top + fil * f + w * 0.003f))
        val montante = Rect(vidrio.left + col - w * 0.008f, vidrio.top, vidrio.left + col + w * 0.008f, vidrio.bottom)
        translate(1.2f * u, 1.4f * u) { drawPath(listones, a.sombra(0.3f)) }
        drawPath(listones, a.dia(gris, 0f, k))
        drawRect(a.sombra(0.3f), Offset(montante.left + 1.4f * u, montante.top), montante.size)
        drawRect(a.dia(gris, 0.05f, k), montante.topLeft, montante.size)
        drawRect(a.claro(0.4f), montante.topLeft, Size(1f * u, montante.height))
        // El marco, de canto, alrededor del vidrio.
        val m0 = Path().apply {
            addRect(Rect(marco.left, marco.top, marco.right, vidrio.top))
            addRect(Rect(vidrio.right, marco.top, marco.right, marco.bottom))
            addRect(Rect(marco.left, vidrio.bottom, marco.right, marco.bottom))
        }
        drawPath(m0, a.dia(gris, -0.06f, k))
        drawRect(a.sombra(0.35f), Offset(vidrio.left, vidrio.top), Size(vidrio.width, 1.6f * u))
        drawRect(a.claro(0.35f), Offset(vidrio.right, marco.top), Size(1f * u, marco.height))
        // El vierteaguas y el alféizar de yeso, con el canto que coge la luz y su sombra debajo.
        val alf = Rect(vano.left, marco.bottom, vano.right + w * 0.008f, vano.bottom + w * 0.008f)
        drawRect(a.dia(P.YESO, 0.1f, k), alf.topLeft, alf.size)
        drawRect(a.claro(0.4f), alf.topLeft, Size(alf.width, 1.2f * u))
        drawRect(a.sombra(0.4f), Offset(alf.left, alf.bottom), Size(alf.width + w * 0.004f, w * 0.006f))
        drawRect(a.sombra(0.15f), Offset(alf.left, alf.bottom + w * 0.006f), Size(alf.width, w * 0.012f))
        // De día, el aire del cuarto alrededor de la ventana: el yeso del derrame derecho recibe la luz rebotada.
        if (!a.oscuro && m != null) drawPath(poligonoRevolucion(vano.right, vano.top, vano.right, vano.bottom, marco.right, marco.bottom, marco.right, marco.top), a.claro(0.18f))
    }

    // ---------------------------------------------------------------- el tendedero

    /** Dónde está el cordel a lo alto en [x] (anchos), con su comba. */
    private fun cordel(x: Float): Float {
        val t = ((x - 0.235f) / 0.8f).coerceIn(0f, 1f)
        return 0.084f + (0.07f - 0.084f) * t + 0.06f * 4f * t * (1f - t)
    }

    /**
     * Los pliegos del tendedero, en el orden en que se pintan (de atrás adelante): centro, ancho y largo en anchos, qué llevan
     * impreso (0, el revés en blanco; 1, dos columnas; 2, con el filete rojo; 3, con el filete azul), el giro y si la mitad de
     * atrás asoma por debajo de la de delante.
     */
    private val PLIEGOS = listOf(
        floatArrayOf(0.565f, 0.1f, 0.105f, 0f, 2f, 0f), floatArrayOf(0.315f, 0.11f, 0.125f, 1f, -2f, 0f),
        floatArrayOf(0.44f, 0.135f, 0.165f, 2f, 1.5f, 1f), floatArrayOf(0.875f, 0.12f, 0.12f, 1f, -1f, 1f),
        floatArrayOf(0.71f, 0.13f, 0.15f, 3f, 2.5f, 0f),
    )

    /**
     * El tendedero de la imprenta: el cordel de cáñamo atado a un clavo junto a la ventana y, a caballo de él, los pliegos
     * recién tirados que se secan, unos sobre otros y de largos distintos: el papel crudo con su doblez, bloques de renglones
     * (formas, no letras), algún filete rojo o azul, y su sombra en la pared. La mancha de la ventana los pinta donde los toca.
     */
    private fun DrawScope.tendedero(a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val cuerda = Path()
        var x = 0.235f
        cuerda.moveTo(w * x, w * cordel(x))
        while (x < 1.02f) { x += 0.02f; cuerda.lineTo(w * x, w * cordel(x)) }
        translate(w * 0.006f, w * 0.01f) { drawPath(cuerda, a.sombra(0.25f), style = Stroke(1.3f * u)) }
        PLIEGOS.forEachIndexed { j, p ->
            sombraPliego(p, a, cerca)
            pliego(j, p, a, cerca, null)
            if (m != null) {
                clipPath(m.forma(w, -0.008f)) { pliego(j, p, a, cerca, m) }
                clipPath(m.forma(w, -0.008f, papel = true)) { pliego(j, p, a, cerca, ManchaRevolucion(m.q, m.color, m.fuerza * 0.5f)) }
            }
        }
        drawPath(cuerda, a.dia(P.BRAMANTE, -0.1f, cerca(Offset(w * 0.6f, w * 0.1f))), style = Stroke(1.5f * u))
        val c = Offset(w * 0.235f, w * cordel(0.235f))
        clavoRevolucion(c, 2.2f * u, a, cerca(c))
        drawCircle(a.dia(P.BRAMANTE, 0f, cerca(c)), 2f * u, c + Offset(2.4f * u, 0.6f * u))
        drawLine(a.dia(P.BRAMANTE, 0f, cerca(c)), c + Offset(2f * u, 1f * u), c + Offset(0.5f * u, 9f * u), 1.1f * u)
    }

    /** La forma de la mitad de delante de un pliego (o, con [atras], la de atrás, más larga y corrida). */
    private fun formaPliego(w: Float, p: FloatArray, atras: Boolean): Path {
        val cx = p[0]; val an = p[1]
        val la = p[2] * (if (atras) 1.35f else 1f)
        val dx = if (atras) w * 0.008f else 0f
        val x0 = w * (cx - an / 2) + dx; val x1 = w * (cx + an / 2) + dx
        val y0 = w * cordel(cx - an / 2); val y1 = w * cordel(cx + an / 2)
        val caida = w * la
        val levanta = (azar(7600 + (cx * 100).toInt(), 1) - 0.5f) * w * 0.014f
        return Path().apply {
            moveTo(x0, y0)
            quadraticTo((x0 + x1) / 2, (y0 + y1) / 2 + w * 0.008f, x1, y1)
            lineTo(x1 + w * 0.004f, y1 + caida + levanta)
            quadraticTo((x0 + x1) / 2, (y0 + y1) / 2 + caida + w * 0.014f, x0 - w * 0.003f, y0 + caida - levanta)
            close()
        }
    }

    /** La sombra de un pliego en la pared: de día, a la derecha y abajo; de noche, hacia fuera de la vela. */
    private fun DrawScope.sombraPliego(p: FloatArray, a: AireRevolucion, cerca: (Offset) -> Float) {
        val w = size.width
        val k = cerca(Offset(w * p[0], w * (cordel(p[0]) + p[2] / 2)))
        val dx = if (a.oscuro) (if (p[0] < MuebleEstanteRevolucion.VELA_X) -1f else 1f) * w * 0.016f * k else w * 0.018f
        val forma = Path().apply { addPath(formaPliego(w, p, p[5] > 0f)); addPath(formaPliego(w, p, false)) }
        rotate(p[4], Offset(w * p[0], w * cordel(p[0]))) {
            translate(dx, w * 0.014f) { drawPath(forma, a.sombra(if (a.oscuro) 0.38f * k else 0.2f)) }
        }
    }

    /** Un pliego colgado ([j] de [PLIEGOS]), con la luz [m] de la ventana si va dentro de su mancha. */
    private fun DrawScope.pliego(j: Int, p: FloatArray, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val s = 7600 + j
        val cx = p[0]; val an = p[1]; val tipo = p[3].toInt()
        val x0 = w * (cx - an / 2); val x1 = w * (cx + an / 2)
        val top = w * minOf(cordel(cx - an / 2), cordel(cx + an / 2))
        val caida = w * p[2]
        val k = cerca(Offset((x0 + x1) / 2, top + caida / 2))
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float = 0f) = a.en(col, f, k, lit, m)
        rotate(p[4], Offset(w * cx, w * cordel(cx))) {
            if (p[5] > 0f) {
                // La mitad de atrás, más larga, asoma por debajo: el revés, algo más a la sombra.
                val atras = formaPliego(w, p, true)
                drawPath(atras, c(mezcla(P.PAPEL, P.TOSTADO, 0.3f), -0.12f))
                drawPath(atras, a.sombra(0.25f), style = Stroke(0.8f * u))
            }
            val forma = formaPliego(w, p, false)
            drawPath(forma, c(mezcla(P.PAPEL, P.TOSTADO, 0.2f * azar(s, 2))))
            clipPath(forma) {
                val col = x1 - x0
                val tinta = c(P.TINTA).copy(alpha = 0.72f)
                if (tipo == 0) {
                    // El revés: el impreso del otro lado, de espejo y muy tenue.
                    var ly = top + caida * 0.3f
                    while (ly < top + caida * 0.9f) { drawLine(tinta.copy(alpha = 0.08f), Offset(x0 + col * 0.18f, ly), Offset(x1 - col * 0.14f, ly), 1.6f * u); ly += caida * 0.07f }
                } else {
                    // La cabecera: un bloque grueso y, debajo, un filete (de color en los tipos 2 y 3).
                    drawRect(tinta, Offset(x0 + col * 0.16f, top + caida * 0.18f), Size(col * 0.68f, caida * 0.07f))
                    val filete = when (tipo) { 2 -> c(P.ROJO); 3 -> c(P.AZUL); else -> tinta }
                    drawRect(filete, Offset(x0 + col * 0.12f, top + caida * 0.3f), Size(col * 0.76f, maxOf(1.4f * u, caida * 0.018f)))
                    // Dos columnas de renglones a trozos (palabras sin letras), con su sangría.
                    val renglones = Path()
                    for (cc in 0 until 2) {
                        val cx0 = x0 + col * (0.12f + 0.4f * cc); val cx1 = cx0 + col * 0.34f
                        var ly = top + caida * 0.38f; var r = 0
                        while (ly < top + caida * 0.9f) {
                            var xx = cx0 + (if (r % 6 == 0) col * 0.04f else 0f)
                            val fin = if (r % 6 == 5) cx0 + col * 0.2f else cx1
                            var q = 0
                            while (xx < fin) {
                                val pal = col * (0.04f + 0.07f * azar(s * 7 + cc, r * 9 + q))
                                renglones.moveTo(xx, ly); renglones.lineTo(minOf(xx + pal, fin), ly)
                                xx += pal + col * 0.02f; q++
                            }
                            ly += caida * 0.055f; r++
                        }
                    }
                    drawPath(renglones, tinta.copy(alpha = 0.5f), style = Stroke(maxOf(1f * u, caida * 0.022f)))
                }
                // La luz: el doblez de arriba claro, el papel que se ensombrece al bajar y la arruga del agua.
                drawRect(a.claro(0.35f), Offset(x0, top - w * 0.01f), Size(col, w * 0.016f))
                drawRect(a.sombra(0.07f), Offset(x0 - w * 0.01f, top + caida * 0.55f), Size(col + w * 0.02f, caida * 0.6f))
                drawRect(a.sombra(0.07f), Offset(x0 - w * 0.01f, top + caida * 0.8f), Size(col + w * 0.02f, caida * 0.4f))
                val arr = Path().apply { moveTo(x0 + col * (0.2f + 0.3f * azar(s, 3)), top + caida * 0.25f); quadraticTo(x0 + col * 0.5f, top + caida * 0.6f, x0 + col * (0.4f + 0.4f * azar(s, 4)), top + caida) }
                drawPath(arr, a.sombra(0.12f), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
                if (a.oscuro && k > 0.25f && m == null) {
                    // De noche, el papel cerca de la vela se transparenta: la luz pasa por él y lo enciende de ámbar.
                    drawRect(P.LLAMA.copy(alpha = (0.32f * (k - 0.25f)).coerceAtMost(0.22f)), Offset(x0 - w * 0.01f, top - w * 0.02f), Size(col + w * 0.02f, caida + w * 0.04f), blendMode = BlendMode.Screen)
                }
            }
            drawPath(forma, a.sombra(0.22f), style = Stroke(0.8f * u))
        }
    }

    /** De noche, el halo cálido de la vela sobre lo que hay delante de la pared (tenue: la pared ya se pintó encendida). */
    fun DrawScope.luzDeNoche(a: AireRevolucion, ch: Float) {
        if (!a.oscuro) return
        val w = size.width
        val c = Offset(w * MuebleEstanteRevolucion.VELA_X, w * MuebleEstanteRevolucion.VELA_Y)
        val f = if (a.noche) 0.6f else 1f
        clipRect(0f, 0f, w, ch) {
            drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.24f * f), P.LLAMA.copy(alpha = 0.06f * f), P.LLAMA.copy(alpha = 0f)), c, w * 0.26f), w * 0.26f, c)
        }
    }
}
