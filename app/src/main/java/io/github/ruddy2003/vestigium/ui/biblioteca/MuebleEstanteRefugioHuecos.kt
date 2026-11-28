package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRefugio as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que llena los huecos de las repisas donde no llegan las entradas: grupos de dos o tres cosas del refugio, solapadas, que
 * llenan del 70 al 90 % del alto del hueco y cambian de un hueco a otro. La cafetera cromada con su taza; el tocadiscos de maleta
 * con dos fundas de discos; la radio de baquelita con un plantón en su lata; las latas de ración del Ideario con una lata abierta y
 * su cuchara; el cohete de hojalata con la caja de bombillas de repuesto. En un hueco muy ancho, dos grupos.
 */
internal object HuecosEstanteRefugio {
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, g: MarcoRefugio) {
        val w = g.w
        val x0 = ocupado + w * 0.014f
        val libre = r.right - x0 - w * 0.03f
        if (libre < w * 0.12f) return
        val orden = intArrayOf(0, 3, 1, 4, 2)
        val i = Math.floorMod(2 * k + k / 5, 5)
        if (libre > w * 0.5f) {
            val mitad = libre * 0.5f
            grupo(orden[i], Rect(x0, r.top, x0 + mitad, r.bottom), g, centrado = true)
            grupo(orden[Math.floorMod(i + 3, 5)], Rect(x0 + mitad + w * 0.012f, r.top, r.right - w * 0.03f, r.bottom), g, centrado = true)
        } else grupo(orden[i], Rect(x0, r.top, x0 + libre, r.bottom), g)
    }

    /** Un grupo en su sitio [r]: pegado a las entradas o, si comparte un hueco muy ancho con otro, en medio de su mitad. */
    private fun DrawScope.grupo(cual: Int, r: Rect, g: MarcoRefugio, centrado: Boolean = false) {
        val w = g.w
        val an = minOf(r.width, w * 0.3f)
        val x0 = if (centrado) r.left + (r.width - an) * 0.5f else r.left
        val z = Rect(x0, r.top, x0 + an, r.bottom)
        val l = g.luz(z.center.x, z.top + z.height * 0.55f, 0.05f)
        val hacia = g.hacia(z.center.x, z.center.y)
        when (cual) {
            0 -> cafetera(z, l, hacia, g)
            1 -> tocadiscos(z, l, hacia, g)
            2 -> radio(z, l, hacia, g)
            3 -> despensa(z, l, hacia, g)
            else -> cohete(z, l, hacia, g)
        }
    }

    /** La sombra que una cosa echa en el fondo (lejos de la luz) y la de contacto en la tabla. */
    private fun DrawScope.sombras(f: Path, x0: Float, x1: Float, pie: Float, l: LuzRefugio, hacia: Float) {
        val u = density
        drawPath(corridoRefugio(f, -hacia * 6f * u, if (l.a.oscuro) -1f * u else 3f * u), l.s(if (l.a.oscuro) 0.45f else 0.36f))
        drawOval(l.s(0.55f), Offset(x0 - hacia * 2f * u, pie - 2.6f * u), Size(x1 - x0 + 4f * u, 4.4f * u))
    }

    private fun rr(r: Rect, c: Float) = Path().apply { addRoundRect(RoundRect(r, CornerRadius(c))) }

    // ---------------------------------------------------------------- la cafetera y la taza

    /**
     * La cafetera de émbolo cromada, de las de la época: el cuerpo que se abre hacia abajo, el pomo de vidrio con el café, el pico
     * largo y el asa de baquelita; delante, la taza coral en su plato.
     */
    private fun DrawScope.cafetera(z: Rect, l: LuzRefugio, hacia: Float, g: MarcoRefugio) {
        val u = density
        val pie = z.bottom
        val al = z.height * 0.8f
        val an = minOf(z.width * 0.36f, al * 0.42f)
        val cx = z.left + an * 0.75f
        val top = pie - al
        val cuerpo = Path().apply {
            moveTo(cx - an * 0.36f, top + al * 0.14f)
            lineTo(cx + an * 0.36f, top + al * 0.14f)
            lineTo(cx + an * 0.5f, pie - al * 0.06f)
            quadraticTo(cx + an * 0.5f, pie, cx + an * 0.42f, pie)
            lineTo(cx - an * 0.42f, pie)
            quadraticTo(cx - an * 0.5f, pie, cx - an * 0.5f, pie - al * 0.06f)
            close()
        }
        val tapa = Path().apply { addArc(Rect(cx - an * 0.38f, top + al * 0.06f, cx + an * 0.38f, top + al * 0.22f), 180f, 180f); close() }
        val pico = Path().apply {
            moveTo(cx - an * 0.4f, top + al * 0.5f)
            cubicTo(cx - an * 0.62f, top + al * 0.45f, cx - an * 0.72f, top + al * 0.3f, cx - an * 0.78f, top + al * 0.2f)
            lineTo(cx - an * 0.7f, top + al * 0.19f)
            cubicTo(cx - an * 0.62f, top + al * 0.3f, cx - an * 0.52f, top + al * 0.36f, cx - an * 0.38f, top + al * 0.36f)
            close()
        }
        val forma = Path().apply { addPath(cuerpo); addPath(tapa); addPath(pico) }
        sombras(forma, cx - an * 0.6f, cx + an * 0.6f, pie, l, hacia)
        // El asa de baquelita, del otro lado, con sus dos brazos.
        val asa = Rect(cx + an * 0.48f, top + al * 0.24f, cx + an * 0.72f, top + al * 0.72f)
        drawRoundRect(l.c(P.BAQUELITA), Offset(asa.right - an * 0.1f, asa.top), Size(an * 0.1f, asa.height), CornerRadius(an * 0.05f))
        drawLine(l.c(P.CROMO_OSC), Offset(cx + an * 0.38f, asa.top + 2f * u), Offset(asa.right - an * 0.06f, asa.top + 2f * u), 2f * u)
        drawLine(l.c(P.CROMO_OSC), Offset(cx + an * 0.45f, asa.bottom - 2f * u), Offset(asa.right - an * 0.06f, asa.bottom - 2f * u), 2f * u)
        drawLine(l.b(0.4f), Offset(asa.right - an * 0.08f, asa.top + 2f * u), Offset(asa.right - an * 0.08f, asa.bottom - 2f * u), 0.8f * u)
        // El cromo del cuerpo: el reflejo del cuarto en bandas a lo alto.
        drawPath(pico, Brush.verticalGradient(listOf(l.c(P.CROMO_LUZ), l.c(P.CROMO_OSC)), top + al * 0.2f, top + al * 0.5f))
        drawPath(cuerpo, Brush.horizontalGradient(listOf(l.c(P.CROMO), l.c(P.CROMO_LUZ), l.c(P.CROMO), l.c(P.CROMO_OSC), l.c(mezcla(P.CROMO_OSC, P.CROMO, 0.4f)), l.c(P.CROMO_OSC)), cx - an * 0.5f, cx + an * 0.5f))
        clipPath(cuerpo) {
            // El reflejo de la tabla abajo y del fondo oliva en medio.
            drawRect(l.c(mezcla(P.OLIVA_OSC, P.CROMO_OSC, 0.4f)).copy(alpha = 0.6f), Offset(cx - an, top + al * 0.55f), Size(an * 2, al * 0.08f))
            drawRect(l.c(P.CROMO_LUZ).copy(alpha = 0.6f), Offset(cx - an, pie - al * 0.12f), Size(an * 2, al * 0.03f))
            drawRect(l.b(0.9f), Offset(cx - an * 0.24f, top + al * 0.16f), Size(an * 0.07f, al * 0.8f))
            // Los dos aros prensados del cuerpo.
            listOf(0.3f, 0.82f).forEach { f -> drawRect(l.s(0.4f), Offset(cx - an, top + al * f), Size(an * 2, 1f * u)); drawRect(l.b(0.6f), Offset(cx - an, top + al * f + 1f * u), Size(an * 2, 0.7f * u)) }
        }
        cilindroRefugio(tapa, l.c(P.CROMO), l, hacia, 0.9f)
        // El pomo de vidrio con el café.
        val pc = Offset(cx, top + al * 0.06f)
        val pr = an * 0.15f
        drawCircle(l.c(mezcla(P.BAQUELITA, P.NARANJA, 0.35f)).copy(alpha = 0.85f), pr, pc)
        drawCircle(l.b(0.9f), pr * 0.35f, pc - Offset(pr * 0.35f, pr * 0.4f))
        drawRect(l.c(P.CROMO_OSC), Offset(pc.x - pr * 0.9f, pc.y + pr * 0.7f), Size(pr * 1.8f, 1.6f * u))
        if (l.k > 0.06f) drawPath(bordeRefugio(cuerpo, -hacia * 1.4f * u, 0f), l.filo(0.9f))
        // La taza coral en su plato, delante a la derecha.
        val tx = cx + an * 0.95f
        val ta = an * 0.62f; val th = al * 0.28f
        val plato = Rect(tx - ta * 0.75f, pie - th * 0.14f, tx + ta * 0.75f, pie)
        drawOval(l.s(0.5f), Offset(plato.left - hacia * 2f * u, pie - 2f * u), Size(plato.width + 3f * u, 4f * u))
        drawOval(l.c(P.VINILO), plato.topLeft, plato.size)
        drawOval(l.s(0.25f), Offset(plato.left + plato.width * 0.2f, plato.top), Size(plato.width * 0.6f, plato.height * 0.6f))
        val taza = Path().apply {
            moveTo(tx - ta * 0.5f, pie - th); lineTo(tx + ta * 0.5f, pie - th)
            quadraticTo(tx + ta * 0.48f, pie - th * 0.18f, tx + ta * 0.2f, pie - th * 0.12f)
            lineTo(tx - ta * 0.2f, pie - th * 0.12f)
            quadraticTo(tx - ta * 0.48f, pie - th * 0.18f, tx - ta * 0.5f, pie - th)
            close()
        }
        drawArc(l.c(P.CORAL), -60f, 140f, false, Offset(tx + ta * 0.32f, pie - th * 0.86f), Size(ta * 0.42f, th * 0.5f), style = Stroke(2f * u))
        cilindroRefugio(taza, l.c(P.CORAL), l, hacia, 0.5f)
        drawOval(l.c(mezcla(P.BAQUELITA, P.CORAL, 0.2f)), Offset(tx - ta * 0.48f, pie - th - ta * 0.08f), Size(ta * 0.96f, ta * 0.16f))
        drawOval(l.c(P.VINILO), Offset(tx - ta * 0.5f, pie - th - ta * 0.09f), Size(ta, ta * 0.18f), style = Stroke(1f * u))
    }

    // ---------------------------------------------------------------- el tocadiscos de maleta

    /**
     * El tocadiscos de maleta, cerrado, tumbado: la tapa crema y la caja coral, la costura, las cantoneras cromadas, los dos cierres y
     * el asa delante; encima, dos fundas de discos apoyadas en el fondo.
     */
    private fun DrawScope.tocadiscos(z: Rect, l: LuzRefugio, hacia: Float, g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.92f, w * 0.27f)
        val x0 = z.left
        val al = z.height * 0.34f
        val caja = Rect(x0, pie - al, x0 + an, pie)
        // Las fundas de discos, detrás, apoyadas en el fondo y ladeadas.
        val lado = minOf(z.height * 0.56f, an * 0.62f)
        listOf(Triple(x0 + an * 0.06f, -7f, P.TEAL), Triple(x0 + an * 0.3f, 4f, P.MANTEQUILLA)).forEachIndexed { i, (fx, giro, col) ->
            val r = Rect(fx, caja.top - lado + 2f * u, fx + lado, caja.top + 2f * u)
            rotate(giro, Offset(if (giro < 0f) r.left else r.right, r.bottom)) {
                val f = Path().apply { addRect(r) }
                drawPath(corridoRefugio(f, -hacia * 5f * u, 2f * u), l.s(0.35f))
                planosRefugio(f, l.c(col), l, hacia, 0.1f, 0.22f)
                if (i == 0) drawCircle(l.c(P.CORAL), lado * 0.18f, Offset(r.left + lado * 0.4f, r.top + lado * 0.4f))
                else for (j in 0 until 4) drawRect(l.c(P.TEAL), Offset(r.left, r.top + lado * (0.2f + 0.14f * j)), Size(lado * (0.4f + 0.12f * j), lado * 0.05f))
                drawRect(l.b(0.3f), r.topLeft, Size(lado, 1f * u))
            }
        }
        // La caja, con la tapa y su costura.
        val cajaP = rr(caja, al * 0.18f)
        sombras(cajaP, caja.left, caja.right, pie, l, hacia)
        planosRefugio(cajaP, l.c(P.CORAL), l, hacia, 0.1f, 0.2f)
        clipPath(cajaP) {
            val tapa = Rect(caja.left, caja.top, caja.right, caja.top + al * 0.45f)
            drawRect(l.c(mezcla(P.VINILO, P.MANTEQUILLA, 0.2f)), tapa.topLeft, tapa.size)
            drawRect(l.s(0.2f), Offset(tapa.right - an * 0.18f, tapa.top), Size(an * 0.18f, tapa.height))
            drawRect(l.b(0.5f), tapa.topLeft, Size(tapa.width, 1f * u))
            drawRect(l.s(0.4f), Offset(caja.left, tapa.bottom), Size(an, 1.4f * u))
            // La costura del vinilo: puntadas a lo largo.
            val pes = Path()
            var xp = caja.left + 3f * u; var j = 0
            while (xp < caja.right - 3f * u && j < 60) { pes.addRect(Rect(xp, caja.bottom - 3f * u, xp + 1.4f * u, caja.bottom - 2.4f * u)); pes.addRect(Rect(xp, tapa.top + 2.4f * u, xp + 1.4f * u, tapa.top + 3f * u)); xp += 2.8f * u; j++ }
            drawPath(pes, l.b(0.35f))
        }
        // Las cantoneras cromadas y los dos cierres.
        listOf(caja.left, caja.right - al * 0.3f).forEach { x ->
            drawRoundRect(l.c(P.CROMO), Offset(x, caja.bottom - al * 0.3f), Size(al * 0.3f, al * 0.3f), CornerRadius(al * 0.1f))
            drawRect(l.b(0.7f), Offset(x + 1f * u, caja.bottom - al * 0.3f + 1f * u), Size(al * 0.12f, 0.8f * u))
        }
        listOf(caja.left + an * 0.2f, caja.right - an * 0.26f).forEach { x ->
            val c = Rect(x, caja.top + al * 0.34f, x + an * 0.06f, caja.top + al * 0.62f)
            drawRect(l.s(0.4f), c.topLeft + Offset(1f * u, 1f * u), c.size)
            drawRect(l.c(P.CROMO), c.topLeft, c.size)
            drawRect(l.b(0.8f), c.topLeft, Size(0.9f * u, c.height))
        }
        // El asa, delante, de cuero con sus dos anillas.
        val ay = caja.top + al * 0.58f
        drawRoundRect(l.c(P.BAQUELITA), Offset(caja.left + an * 0.38f, ay), Size(an * 0.24f, al * 0.18f), CornerRadius(al * 0.09f))
        drawLine(l.b(0.35f), Offset(caja.left + an * 0.4f, ay + 0.8f * u), Offset(caja.left + an * 0.6f, ay + 0.8f * u), 0.7f * u)
        if (l.k > 0.06f) drawPath(bordeRefugio(cajaP, -hacia * 1.4f * u, 0f), l.filo(0.8f))
    }

    // ---------------------------------------------------------------- la radio y el plantón

    /**
     * La radio de baquelita de la época: la caja mostaza de esquinas redondas, la rejilla del altavoz con sus barras a la izquierda,
     * la esfera redonda crema sin cifras con la aguja roja a la derecha y los dos mandos; al lado, un plantón de tomatera en su lata.
     */
    private fun DrawScope.radio(z: Rect, l: LuzRefugio, hacia: Float, g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.68f, w * 0.2f)
        val al = minOf(z.height * 0.56f, an * 0.8f)
        val x0 = z.left
        val caja = Rect(x0, pie - al, x0 + an, pie)
        val cajaP = Path().apply { addRoundRect(RoundRect(caja, CornerRadius(an * 0.22f), CornerRadius(an * 0.22f), CornerRadius(an * 0.05f), CornerRadius(an * 0.05f))) }
        sombras(cajaP, caja.left, caja.right, pie, l, hacia)
        planosRefugio(cajaP, l.c(mezcla(P.MANTEQUILLA, P.OCRE, 0.45f)), l, hacia, 0.14f, 0.24f)
        drawPath(bordeRefugio(cajaP, 1.2f * u, 1.4f * u), l.b(0.5f))
        // La rejilla: el paño oscuro y las barras doradas.
        val rej = Rect(caja.left + an * 0.1f, caja.top + al * 0.22f, caja.left + an * 0.5f, caja.bottom - al * 0.16f)
        drawRoundRect(l.c(P.BAQUELITA), rej.topLeft, rej.size, CornerRadius(an * 0.06f))
        for (j in 0 until 6) {
            val y = rej.top + rej.height * (j + 0.6f) / 6.4f
            drawRect(l.c(mezcla(P.OCRE, P.MANTEQUILLA, 0.5f)), Offset(rej.left, y), Size(rej.width, 1.3f * u))
            drawRect(l.b(0.5f), Offset(rej.left, y), Size(rej.width, 0.5f * u))
        }
        // La esfera: el aro, la cara crema con sus marcas (sin cifras) y la aguja roja.
        val ec = Offset(caja.left + an * 0.74f, caja.top + al * 0.42f)
        val er = minOf(an * 0.17f, al * 0.26f)
        drawCircle(l.s(0.35f), er * 1.15f, ec + Offset(0.8f * u, 1f * u))
        drawCircle(l.c(P.CROMO), er * 1.15f, ec)
        drawCircle(l.c(P.VINILO), er, ec)
        for (k in 0 until 9) {
            val a = 3.6f + k * 0.28f
            drawLine(l.c(P.TINTA).copy(alpha = 0.6f), ec + Offset(er * 0.62f * cos(a), er * 0.62f * sin(a)), ec + Offset(er * 0.85f * cos(a), er * 0.85f * sin(a)), 0.7f * u)
        }
        drawLine(l.c(P.TOMATE), ec, ec + Offset(er * 0.8f * cos(4.4f), er * 0.8f * sin(4.4f)), 1f * u)
        drawArc(l.b(0.7f), 190f, 70f, false, Offset(ec.x - er, ec.y - er), Size(er * 2, er * 2), style = Stroke(0.8f * u))
        listOf(caja.left + an * 0.64f, caja.left + an * 0.86f).forEach { x ->
            val p = Offset(x, caja.bottom - al * 0.17f)
            drawCircle(l.c(P.BAQUELITA), al * 0.07f, p)
            drawCircle(l.b(0.5f), al * 0.025f, p - Offset(al * 0.02f, al * 0.02f))
        }
        if (l.k > 0.06f) drawPath(bordeRefugio(cajaP, -hacia * 1.4f * u, 0f), l.filo(0.8f))
        // El plantón de tomatera en su lata, a la derecha.
        val lx = caja.right + an * 0.24f
        val la = an * 0.32f; val lh = z.height * 0.24f
        val lata = Rect(lx - la / 2, pie - lh, lx + la / 2, pie)
        val tallo = pie - lh
        val tope = pie - z.height * 0.8f
        listOf(Offset(lx - la * 0.6f, tope + z.height * 0.12f), Offset(lx + la * 0.1f, tope), Offset(lx + la * 0.7f, tope + z.height * 0.16f)).forEach { q ->
            drawPath(Path().apply { moveTo(lx, tallo); cubicTo(lx, tallo - (tallo - q.y) * 0.5f, q.x, q.y + (tallo - q.y) * 0.4f, q.x, q.y) }, l.c(mezcla(P.HOJA, P.OLIVA_OSC, 0.3f)), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        val hojas = listOf(Triple(-0.6f, 0.15f, -40f), Triple(0.55f, 0.24f, 35f), Triple(-0.2f, 0.04f, -10f), Triple(-0.7f, 0.45f, -60f), Triple(0.65f, 0.52f, 60f), Triple(0.2f, 0.34f, 20f), Triple(-0.3f, 0.6f, -70f))
        hojas.forEachIndexed { j, (fx, fy, gr) ->
            val p = Offset(lx + la * fx, tope + (tallo - tope) * fy)
            val hl = z.height * 0.12f
            rotate(gr, p) {
                drawOval(l.c(mezcla(P.HOJA, P.OLIVA_OSC, 0.3f * (j % 2))), Offset(p.x - hl * 0.22f, p.y - hl * 0.5f), Size(hl * 0.44f, hl))
                drawOval(l.s(0.2f), Offset(p.x, p.y - hl * 0.45f), Size(hl * 0.2f, hl * 0.9f))
                drawLine(l.b(0.35f), Offset(p.x, p.y - hl * 0.4f), Offset(p.x, p.y + hl * 0.4f), 0.6f * u)
            }
        }
        val lataP = Path().apply { addRect(lata) }
        drawOval(l.s(0.5f), Offset(lata.left - hacia * 2f * u, pie - 2.4f * u), Size(la + 3f * u, 4.4f * u))
        cilindroRefugio(lataP, l.c(mezcla(P.ACERO, P.CREMA, 0.3f)), l, hacia, 0.6f)
        drawRect(l.c(P.NARANJA).copy(alpha = 0.4f), Offset(lata.left, pie - lh * 0.18f), Size(la, lh * 0.18f))
        drawOval(l.c(mezcla(P.OLIVA_OSC, P.NARANJA, 0.2f)), Offset(lata.left, lata.top - 1.6f * u), Size(la, 3.2f * u))
    }

    // ---------------------------------------------------------------- las latas de ración y el tarro

    /**
     * Las latas de ración del Ideario, sin marca (oliva con su franja ocre): dos de pie y una encima; al lado, otra abierta, con la tapa
     * levantada y la cuchara dentro.
     */
    private fun DrawScope.despensa(z: Rect, l: LuzRefugio, hacia: Float, g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = z.bottom
        val la = minOf(z.width * 0.3f, w * 0.075f)
        val lh = z.height * 0.36f
        fun lata(x: Float, y: Float, i: Int) {
            val r = Rect(x, y - lh, x + la, y)
            val ry = la * 0.14f
            val cuerpo = Path().apply {
                moveTo(r.left, r.top); lineTo(r.left, r.bottom - ry)
                arcTo(Rect(r.left, r.bottom - 2 * ry, r.right, r.bottom), 180f, -180f, false)
                lineTo(r.right, r.top); close()
            }
            if (i < 2) sombras(cuerpo, r.left, r.right, y, l, hacia)
            cilindroRefugio(cuerpo, l.c(mezcla(P.OLIVA_LUZ, P.OLIVA, 0.3f + 0.2f * i)), l, hacia, 0.5f)
            clipPath(cuerpo) {
                drawRect(l.c(P.OCRE), Offset(r.left, r.top + lh * 0.42f), Size(la, lh * 0.16f))
                drawRect(l.s(0.2f), Offset(if (hacia < 0f) r.left + la * 0.6f else r.left, r.top + lh * 0.42f), Size(la * 0.4f, lh * 0.16f))
                listOf(0.2f, 0.82f).forEach { f -> drawRect(l.s(0.25f), Offset(r.left, r.top + lh * f), Size(la, 0.8f * u)); drawRect(l.b(0.25f), Offset(r.left, r.top + lh * f + 0.8f * u), Size(la, 0.6f * u)) }
                if (i != 2) drawOval(l.c(mezcla(P.NARANJA, P.OLIVA_OSC, 0.5f)).copy(alpha = 0.55f), Offset(r.left + la * (0.2f + 0.4f * i), r.bottom - ry * 2.2f), Size(la * 0.25f, ry * 1.4f))
            }
            drawOval(l.c(mezcla(P.ACERO, P.CREMA, 0.2f)), Offset(r.left, r.top - ry), Size(la, 2 * ry))
            drawOval(l.c(P.ACERO), Offset(r.left + la * 0.09f, r.top - ry * 0.72f), Size(la * 0.82f, ry * 1.44f))
            drawArc(l.b(0.55f), 150f, 110f, false, Offset(r.left + 0.5f * u, r.top - ry + 0.5f * u), Size(la - 1f * u, 2 * ry - 1f * u), style = Stroke(0.9f * u))
            if (l.k > 0.06f) drawPath(bordeRefugio(cuerpo, -hacia * 1.2f * u, 0f), l.filo(0.7f))
        }
        val x0 = z.left
        lata(x0, pie, 0)
        lata(x0 + la * 1.04f, pie + 0.4f * u, 1)
        lata(x0 + la * 0.52f, pie - lh - la * 0.04f, 2)
        // La lata abierta, a la derecha, con la tapa levantada en su bisagra y la cuchara dentro.
        val ta = minOf(z.width * 0.3f, w * 0.08f); val th = z.height * 0.4f
        val tx = x0 + la * 2.25f
        val r = Rect(tx, pie - th, tx + ta, pie)
        val ry = ta * 0.14f
        val cuerpo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.left, r.bottom - ry)
            arcTo(Rect(r.left, r.bottom - 2 * ry, r.right, r.bottom), 180f, -180f, false)
            lineTo(r.right, r.top); close()
        }
        sombras(cuerpo, r.left, r.right, pie, l, hacia)
        // La cuchara, detrás del borde: el mango largo que sale inclinado.
        val c0 = Offset(r.left + ta * 0.55f, r.top + th * 0.2f)
        val c1 = Offset(r.left + ta * 0.95f, pie - z.height * 0.82f)
        drawLine(l.s(0.3f), c0 + Offset(1.4f * u, 1.4f * u), c1 + Offset(1.4f * u, 1.4f * u), 2.4f * u, StrokeCap.Round)
        drawLine(l.c(P.CROMO), c0, c1, 2.4f * u, StrokeCap.Round)
        drawLine(l.b(0.8f), c0 - Offset(0.8f * u, 0f), c1 - Offset(0.8f * u, 0f), 0.7f * u, StrokeCap.Round)
        cilindroRefugio(cuerpo, l.c(mezcla(P.OLIVA_LUZ, P.OLIVA, 0.3f)), l, hacia, 0.5f)
        clipPath(cuerpo) {
            drawRect(l.c(P.OCRE), Offset(r.left, r.top + th * 0.42f), Size(ta, th * 0.16f))
            listOf(0.2f, 0.82f).forEach { fr -> drawRect(l.s(0.25f), Offset(r.left, r.top + th * fr), Size(ta, 0.8f * u)) }
        }
        // La boca abierta (lo oscuro de dentro) y la tapa levantada hacia atrás, de canto.
        drawOval(l.c(mezcla(P.ACERO, P.CREMA, 0.2f)), Offset(r.left, r.top - ry), Size(ta, 2 * ry))
        drawOval(l.c(mezcla(P.OLIVA_OSC, P.NOCHE, 0.4f)), Offset(r.left + ta * 0.08f, r.top - ry * 0.7f), Size(ta * 0.84f, ry * 1.4f))
        rotate(-58f, Offset(r.right - ta * 0.06f, r.top)) {
            drawOval(l.c(mezcla(P.ACERO, P.CROMO, 0.5f)), Offset(r.right - ta * 0.06f, r.top - ry), Size(ta, 2 * ry))
            drawArc(l.b(0.7f), 180f, 160f, false, Offset(r.right - ta * 0.06f, r.top - ry), Size(ta, 2 * ry), style = Stroke(0.8f * u))
        }
        if (l.k > 0.06f) drawPath(bordeRefugio(cuerpo, -hacia * 1.2f * u, 0f), l.filo(0.6f))
    }

    // ---------------------------------------------------------------- el cohete de hojalata y las bombillas

    /**
     * El cohete de juguete de hojalata, de pie sobre sus aletas: el cuerpo de chapa crema con las franjas coral, la punta roja, la
     * ventanilla redonda con su aro y las aletas; al lado, la caja de cartón de las bombillas de repuesto, abierta, y una bombilla
     * suelta, tumbada.
     */
    private fun DrawScope.cohete(z: Rect, l: LuzRefugio, hacia: Float, g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = z.bottom
        val al = z.height * 0.86f
        val an = minOf(z.width * 0.24f, al * 0.26f)
        val cx = z.left + an * 0.95f
        val top = pie - al
        val cuerpo = Path().apply {
            moveTo(cx, top)
            cubicTo(cx + an * 0.45f, top + al * 0.14f, cx + an * 0.5f, top + al * 0.3f, cx + an * 0.5f, top + al * 0.45f)
            lineTo(cx + an * 0.44f, pie - al * 0.14f)
            lineTo(cx - an * 0.44f, pie - al * 0.14f)
            lineTo(cx - an * 0.5f, top + al * 0.45f)
            cubicTo(cx - an * 0.5f, top + al * 0.3f, cx - an * 0.45f, top + al * 0.14f, cx, top)
            close()
        }
        val aletaI = poligonoRefugio(Offset(cx - an * 0.46f, top + al * 0.56f), Offset(cx - an * 0.95f, pie - 0.5f * u), Offset(cx - an * 0.6f, pie - 0.5f * u), Offset(cx - an * 0.44f, pie - al * 0.14f))
        val aletaD = poligonoRefugio(Offset(cx + an * 0.46f, top + al * 0.56f), Offset(cx + an * 0.95f, pie - 0.5f * u), Offset(cx + an * 0.6f, pie - 0.5f * u), Offset(cx + an * 0.44f, pie - al * 0.14f))
        val tobera = Rect(cx - an * 0.3f, pie - al * 0.14f, cx + an * 0.3f, pie - al * 0.06f)
        val forma = Path().apply { addPath(cuerpo); addPath(aletaI); addPath(aletaD); addRect(tobera) }
        sombras(forma, cx - an * 0.95f, cx + an * 0.95f, pie, l, hacia)
        planosRefugio(aletaI, l.c(P.CORAL), l, hacia, 0.2f, 0.3f)
        planosRefugio(aletaD, l.c(mezcla(P.CORAL, P.BAQUELITA, 0.25f)), l, hacia, 0.2f, 0.3f)
        cilindroRefugio(Path().apply { addRect(tobera) }, l.c(P.ACERO), l, hacia, 0.5f)
        cilindroRefugio(cuerpo, l.c(mezcla(P.VINILO, P.CROMO, 0.3f)), l, hacia, 0.7f)
        clipPath(cuerpo) {
            drawRect(l.c(P.TOMATE), Offset(cx - an, top), Size(an * 2, al * 0.16f))
            listOf(0.62f, 0.7f).forEach { f -> drawRect(l.c(P.CORAL), Offset(cx - an, top + al * f), Size(an * 2, al * 0.04f)) }
            drawRect(l.s(0.3f), Offset(if (hacia < 0f) cx + an * 0.2f else cx - an * 0.5f, top), Size(an * 0.3f, al))
            // Los remaches pintados de la chapa, en una fila.
            for (j in 0 until 6) drawCircle(l.s(0.4f), 0.7f * u, Offset(cx - an * 0.2f + an * 0.08f * j, top + al * 0.52f))
        }
        // La ventanilla, con su aro y su vidrio azul verdoso (con un brillo, sin pupila).
        val vc = Offset(cx, top + al * 0.36f)
        val vr = an * 0.2f
        drawCircle(l.c(P.CROMO_OSC), vr * 1.3f, vc)
        drawCircle(l.c(P.CROMO), vr * 1.15f, vc)
        drawCircle(l.c(mezcla(P.AGUA_HONDA, P.TEAL, 0.4f)), vr, vc)
        drawArc(l.b(0.8f), 190f, 80f, false, Offset(vc.x - vr * 0.75f, vc.y - vr * 0.75f), Size(vr * 1.5f, vr * 1.5f), style = Stroke(1f * u))
        if (l.k > 0.06f) drawPath(bordeRefugio(cuerpo, -hacia * 1.2f * u, 0f), l.filo(0.8f))
        // La caja de las bombillas de repuesto, abierta, con las bombillas asomando.
        val ca = minOf(z.width * 0.4f, w * 0.1f); val chh = z.height * 0.34f
        val caja = Rect(cx + an * 1.15f, pie - chh, cx + an * 1.15f + ca, pie)
        val cajaP = Path().apply { addRect(caja) }
        sombras(cajaP, caja.left, caja.right, pie, l, hacia)
        // Las bombillas, de pie en la caja y ladeadas, a distintas alturas: el globo de pera y el casquillo cromado abajo (no dos
        // bolas sueltas, que harían ojos).
        listOf(Triple(0.24f, -14f, 0.2f), Triple(0.52f, 6f, 0.05f), Triple(0.8f, 18f, 0.26f)).forEach { (fx, giro, baja) ->
            val br = ca * 0.14f
            val pie = caja.top + chh * 0.1f
            val bc = Offset(caja.left + ca * fx, pie - br * 2.3f + chh * baja)
            rotate(giro, Offset(bc.x, pie)) {
                val pera = cerradaRefugio(listOf(Offset(bc.x, bc.y - br), Offset(bc.x + br, bc.y), Offset(bc.x + br * 0.4f, bc.y + br * 1.3f),
                    Offset(bc.x - br * 0.4f, bc.y + br * 1.3f), Offset(bc.x - br, bc.y)))
                drawPath(pera, l.c(mezcla(P.ETIQUETA, Color.White, 0.3f)).copy(alpha = 0.92f))
                drawPath(pera, l.s(0.25f), style = Stroke(0.6f * u))
                drawLine(l.b(0.85f), Offset(bc.x - br * 0.45f, bc.y - br * 0.4f), Offset(bc.x - br * 0.5f, bc.y + br * 0.3f), 0.9f * u)
                val casquillo = Rect(bc.x - br * 0.42f, bc.y + br * 1.2f, bc.x + br * 0.42f, bc.y + br * 2.1f)
                cromoRefugio(casquillo, l, vertical = true)
                for (k in 1..2) drawLine(l.s(0.4f), Offset(casquillo.left, casquillo.top + casquillo.height * k / 3f), Offset(casquillo.right, casquillo.top + casquillo.height * k / 3f), 0.6f * u)
            }
        }
        planosRefugio(cajaP, l.c(mezcla(P.OCRE, P.CREMA, 0.45f)), l, hacia, 0.12f, 0.24f)
        drawRect(l.c(P.TEAL), Offset(caja.left, caja.top + chh * 0.35f), Size(ca, chh * 0.2f))
        drawPath(poligonoRefugio(Offset(caja.left, caja.top), Offset(caja.left - ca * 0.16f, caja.top - chh * 0.3f), Offset(caja.left + ca * 0.3f, caja.top - chh * 0.34f), Offset(caja.left + ca * 0.45f, caja.top)), l.c(mezcla(P.OCRE, P.CREMA, 0.3f)))
        // La bombilla suelta, tumbada delante.
        val bx = caja.left + ca * 0.5f; val by = pie - ca * 0.1f
        drawOval(l.s(0.4f), Offset(bx - ca * 0.2f, pie - 2f * u), Size(ca * 0.5f, 3.4f * u))
        drawCircle(l.c(mezcla(P.ETIQUETA, Color.White, 0.3f)), ca * 0.1f, Offset(bx, by))
        drawRect(l.c(P.CROMO), Offset(bx + ca * 0.08f, by - ca * 0.05f), Size(ca * 0.1f, ca * 0.1f))
        drawCircle(l.b(0.8f), ca * 0.03f, Offset(bx - ca * 0.03f, by - ca * 0.04f))
    }
}
