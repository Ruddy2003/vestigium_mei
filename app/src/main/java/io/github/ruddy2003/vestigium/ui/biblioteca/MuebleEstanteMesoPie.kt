package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.BRASA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CENIZA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.LLAMA_VIVA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.MAIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.OCOTE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TOTOMOXTLE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

// El pie del cuarto de guardar (MuebleEstanteMeso) y sus dos lumbres: el fogón de tres piedras (a la izquierda, en primer
// plano) con su leña, el metate con su mano y la masa (a la derecha), las mazorcas secas, y las llamas del fogón y del nicho.

/** Lo del piso, en primer plano: el fogón, cortado por el borde de la izquierda, y el metate, por el de la derecha. */
internal fun DrawScope.pieMeso(m: MesaMeso) {
    val w = m.w
    petateMeso(m)
    mazorcasSecasMeso(m, Offset(w * 0.56f, m.base + w * 0.29f))
    metateMeso(m, Offset(w * 0.82f, m.base + w * 0.25f))
    fogonMeso(m, Offset(w * 0.15f, m.base + w * 0.255f))
}

/** Un canto rodado dentro de [r]: un contorno blando de diez puntos, cada uno un poco más o menos lejos del centro. */
private fun cantoMeso(r: Rect, semilla: Int, irregular: Float = 0.1f): Path {
    val n = 10
    val pts = List(n) { i ->
        val ang = i / n.toFloat() * 6.2832f
        val rr = 1f - irregular + irregular * 2f * azar(semilla, i)
        Offset(r.center.x + r.width / 2 * rr * kotlin.math.cos(ang), r.center.y + r.height / 2 * rr * kotlin.math.sin(ang))
    }
    fun medio(a: Offset, b: Offset) = Offset((a.x + b.x) / 2, (a.y + b.y) / 2)
    return Path().apply {
        val m0 = medio(pts[0], pts[1])
        moveTo(m0.x, m0.y)
        for (i in 1..n) { val p = pts[i % n]; val q = medio(p, pts[(i + 1) % n]); quadraticTo(p.x, p.y, q.x, q.y) }
        close()
    }
}

/**
 * Una piedra del fogón en [r]: un canto rodado de caliza, con su plano de luz arriba, la media luna de sombra abajo, el hollín
 * que sube del lado del fuego (blando, no de regla) y su sombra en el piso; de noche, el filo de la lumbre del lado del fuego.
 */
private fun DrawScope.piedraFogonMeso(m: MesaMeso, r: Rect, semilla: Int, fuegoDer: Boolean) {
    val u = density
    val a = m.a
    val k = m.k(r.center); val f = m.f(r.center)
    val p = cantoMeso(r, semilla)
    contactoMeso(Offset(r.center.x + r.width * 0.06f, r.bottom - r.height * 0.05f), r.width * 1.15f, r.height * 0.24f, a, 0.45f)
    drawPath(p, a.c(mezcla(CALIZA, CALIZA_OSC, 0.3f), k, f))
    clipPath(p) {
        drawOval(a.b(0.3f, k), Offset(r.left + r.width * 0.08f, r.top), Size(r.width * 0.6f, r.height * 0.42f))
        // El hollín del lado del fuego, en dos manchas que suben.
        val sx = if (fuegoDer) r.right else r.left
        drawOval(TINTA.copy(alpha = 0.32f), Offset(sx - r.width * 0.5f, r.top - r.height * 0.1f), Size(r.width, r.height * 1.2f))
        drawOval(TINTA.copy(alpha = 0.45f), Offset(sx - r.width * 0.3f, r.top + r.height * 0.15f), Size(r.width * 0.6f, r.height * 0.95f))
        drawPath(bordeMeso(p, -r.width * 0.14f, -r.height * 0.14f), a.s(0.32f))
        val pic = Path()
        for (j in 0 until 7) { val q = Offset(r.left + r.width * azar(semilla, j), r.top + r.height * azar(semilla, j + 9)); pic.moveTo(q.x, q.y); pic.lineTo(q.x + 1.6f * u, q.y + 0.8f * u) }
        drawPath(pic, a.s(0.25f), style = Stroke(0.8f * u))
        if (a.oscuro) drawPath(bordeMeso(p, if (fuegoDer) -r.width * 0.06f else r.width * 0.06f, r.height * 0.04f), MesoTinta.LLAMA.copy(alpha = (0.7f * k).coerceAtMost(0.65f)))
    }
    drawPath(p, a.s(0.3f), style = Stroke(0.6f * u))
}

/**
 * Un leño de [p0] (su testa, cortada) a [p1] (la punta, quemada) y de grueso [gr]: la corteza con sus surcos, la luz arriba y la
 * sombra abajo, la testa con sus anillos y la punta carbonizada con sus grietas encendidas.
 */
private fun DrawScope.lenoMeso(m: MesaMeso, p0: Offset, p1: Offset, gr: Float, semilla: Int) {
    val u = density
    val a = m.a
    val k = m.k(p1); val f = m.f(p0)
    val d = p1 - p0
    val largo = d.getDistance()
    if (largo < 1f) return
    val giro = Math.toDegrees(kotlin.math.atan2(d.y.toDouble(), d.x.toDouble())).toFloat()
    rotate(giro, p0) {
        val r = Rect(p0.x, p0.y - gr / 2, p0.x + largo, p0.y + gr / 2)
        drawRoundRect(a.s(0.35f), Offset(r.left + gr * 0.2f, r.top + gr * 0.45f), r.size, androidx.compose.ui.geometry.CornerRadius(gr * 0.3f))
        drawRoundRect(a.c(mezcla(MesoTinta.ZAPOTE_OSC, OCOTE, 0.35f), k, f), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(gr * 0.3f))
        clipRect(r.left, r.top, r.right, r.bottom) {
            val surcos = Path()
            for (j in 0 until 4) { val y = r.top + gr * (0.2f + 0.2f * j); surcos.moveTo(r.left, y); surcos.cubicTo(r.left + largo * 0.3f, y + gr * 0.08f, r.left + largo * 0.6f, y - gr * 0.08f, r.right, y) }
            drawPath(surcos, a.s(0.3f), style = Stroke(0.8f * u))
            drawRect(a.b(0.25f, k), r.topLeft, Size(r.width, gr * 0.28f))
            drawRect(a.s(0.3f), Offset(r.left, r.bottom - gr * 0.32f), Size(r.width, gr * 0.32f))
            // La punta carbonizada.
            val q = Rect(r.right - largo * 0.3f, r.top, r.right, r.bottom)
            drawRect(a.c(TINTA, k * 0.5f), q.topLeft, q.size)
            val grietas = Path()
            for (j in 0 until 4) { val x = q.left + q.width * (0.15f + 0.22f * j + 0.05f * azar(semilla, j)); grietas.moveTo(x, q.top + gr * 0.1f); grietas.lineTo(x + gr * 0.18f, q.bottom - gr * 0.1f) }
            drawPath(grietas, (if (a.lumbre) MesoTinta.LLAMA else MesoTinta.BRASA).copy(alpha = if (a.oscuro) 0.95f else 0.5f), style = Stroke(0.9f * u))
        }
        // La testa con sus anillos.
        drawOval(a.c(mezcla(OCOTE, ESTUCO, 0.3f), k, f), Offset(r.left - gr * 0.22f, r.top), Size(gr * 0.44f, gr))
        drawOval(a.s(0.28f), Offset(r.left - gr * 0.12f, r.top + gr * 0.25f), Size(gr * 0.24f, gr * 0.5f), style = Stroke(0.6f * u))
        drawArc(a.b(0.4f, k), 120f, 120f, false, Offset(r.left - gr * 0.22f, r.top), Size(gr * 0.44f, gr), style = Stroke(0.7f * u))
    }
}

/**
 * El fogón de tres piedras en [c] (el centro de las cenizas), en primer plano: la tierra quemada, la cama de ceniza, la piedra
 * de atrás, los tres leños que entran al centro (con su testa cortada y la punta quemada), los carbones con sus grietas, las
 * dos piedras de delante y, de día, el hilo de humo. La llama va aparte ([lumbreFogonMeso]).
 */
private fun DrawScope.fogonMeso(m: MesaMeso, c: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(c); val f = m.f(c)
    // La tierra quemada y la ceniza.
    drawPath(cantoMeso(Rect(c.x - w * 0.24f, c.y - w * 0.05f, c.x + w * 0.24f, c.y + w * 0.055f), 909, 0.08f), a.c(mezcla(CALIZA_OSC, TINTA, 0.4f), k, f).copy(alpha = 0.55f))
    val ceniza = cantoMeso(Rect(c.x - w * 0.18f, c.y - w * 0.034f, c.x + w * 0.18f, c.y + w * 0.038f), 901, 0.1f)
    drawPath(ceniza, a.c(CENIZA, k, f))
    clipPath(ceniza) {
        drawOval(a.b(0.25f, k), Offset(c.x - w * 0.12f, c.y - w * 0.03f), Size(w * 0.2f, w * 0.03f))
        pinceladas(Rect(c.x - w * 0.18f, c.y - w * 0.035f, c.x + w * 0.18f, c.y + w * 0.04f), CALIZA_OSC, 902, 10, u, 0.3f)
    }
    piedraFogonMeso(m, Rect(c.x - w * 0.045f, c.y - w * 0.1f, c.x + w * 0.07f, c.y - w * 0.02f), 903, false)
    // Los leños.
    lenoMeso(m, Offset(c.x - w * 0.24f, c.y + w * 0.012f), Offset(c.x - w * 0.02f, c.y - w * 0.008f), w * 0.034f, 905)
    lenoMeso(m, Offset(c.x + w * 0.25f, c.y + w * 0.02f), Offset(c.x + w * 0.03f, c.y - w * 0.006f), w * 0.032f, 906)
    lenoMeso(m, Offset(c.x + w * 0.1f, c.y - w * 0.06f), Offset(c.x + w * 0.01f, c.y - w * 0.015f), w * 0.026f, 907)
    // Los carbones: un montón de trozos negros, en un solo camino, con chispas de brasa sueltas entre ellos.
    val carbones = Path()
    for (j in 0 until 9) {
        val q = Offset(c.x + w * (azar(j, 904) - 0.5f) * 0.15f, c.y - w * 0.006f + w * 0.022f * azar(j, 908))
        val r = w * (0.01f + 0.008f * azar(j, 910))
        carbones.addPath(poligonoMeso(q.x - r, q.y, q.x - r * 0.3f, q.y - r * 0.6f, q.x + r * 0.7f, q.y - r * 0.5f, q.x + r, q.y + r * 0.3f, q.x - r * 0.2f, q.y + r * 0.55f))
    }
    drawPath(carbones, a.c(Color(0xFF2B211E), k * 0.4f))
    for (j in 0 until 10) {
        val q = Offset(c.x + w * (azar(j, 914) - 0.5f) * 0.14f, c.y - w * 0.004f + w * 0.02f * azar(j, 915))
        val r = w * (0.003f + 0.004f * azar(j, 916))
        drawRect((if (a.oscuro) MesoTinta.BRASA else mezcla(MesoTinta.BRASA, TINTA, 0.3f)).copy(alpha = if (a.oscuro) 0.95f else 0.75f), Offset(q.x - r, q.y - r * 0.4f), Size(r * 2f, r * 0.8f))
    }
    if (!a.oscuro) drawPath(carbones, CENIZA.copy(alpha = 0.35f), style = Stroke(0.8f * u))
    piedraFogonMeso(m, Rect(c.x - w * 0.28f, c.y - w * 0.065f, c.x - w * 0.12f, c.y + w * 0.035f), 912, true)
    piedraFogonMeso(m, Rect(c.x + w * 0.11f, c.y - w * 0.06f, c.x + w * 0.27f, c.y + w * 0.04f), 913, false)
    // De día, el hilo de humo que sube de las brasas.
    if (!a.oscuro) {
        val humo = Path().apply {
            moveTo(c.x, c.y - w * 0.02f)
            cubicTo(c.x - w * 0.05f, c.y - w * 0.08f, c.x + w * 0.06f, c.y - w * 0.12f, c.x + w * 0.01f, c.y - w * 0.19f)
        }
        drawPath(humo, ESTUCO.copy(alpha = 0.26f), style = Stroke(w * 0.016f, cap = StrokeCap.Round))
        drawPath(humo, ESTUCO.copy(alpha = 0.2f), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
    }
}

/**
 * El petate tendido en el piso donde se muele, visto en perspectiva: el tejido en espiga (hileras de tiras cortas que se
 * inclinan a un lado y al otro, más altas hacia quien mira), dos tiras teñidas que corren a lo largo, la orilla de atrás
 * rematada y su sombra en las lajas; de noche, la luz del fogón lo alumbra de un lado.
 */
private fun DrawScope.petateMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a; val h = m.alto
    val atras = m.base + w * 0.14f
    val forma = poligonoMeso(w * 0.47f, atras, w * 1.05f, atras, w * 1.12f, h + 2f, w * 0.36f, h + 2f)
    val c = Offset(w * 0.75f, (atras + h) / 2)
    val k = m.k(c); val f = m.f(c)
    drawPath(corridoMeso(forma, w * 0.006f, w * 0.006f), a.s(0.3f))
    val palma = mezcla(MesoTinta.BEJUCO, ESTUCO, 0.35f)
    clipPath(forma) {
        panoMeso(m, listOf(Rect(w * 0.3f, atras, w * 1.2f, h + 2f)), palma)
        // El tejido en espiga.
        val luz = Path(); val sombra = Path()
        var y = atras; var fila = 0
        while (y < h && fila < 20) {
            val t = ((y - atras) / (h - atras).coerceAtLeast(1f)).coerceIn(0f, 1f)
            val alto = w * (0.016f + 0.02f * t)
            val paso = w * (0.014f + 0.012f * t)
            val dir = if (fila % 2 == 0) 1f else -1f
            var x = w * 0.3f; var n = 0
            while (x < w * 1.15f && n < 80) {
                val p = if (dir > 0) luz else sombra
                p.moveTo(x, y); p.lineTo(x + dir * paso * 0.8f, y + alto)
                x += paso; n++
            }
            y += alto; fila++
        }
        drawPath(luz, a.c(mezcla(palma, ESTUCO, 0.4f), k, f).copy(alpha = 0.55f), style = Stroke(w * 0.006f))
        drawPath(sombra, a.c(MesoTinta.BEJUCO_OSC, k, f).copy(alpha = 0.45f), style = Stroke(w * 0.005f))
        // Las dos tiras teñidas a lo largo y la orilla de atrás.
        listOf(0.3f to MesoTinta.CINABRIO, 0.4f to TINTA).forEach { (t, col) ->
            val yy = atras + (h - atras) * t
            drawRect(a.c(col, k, f).copy(alpha = 0.6f), Offset(0f, yy), Size(w * 1.2f, (h - atras) * 0.04f))
        }
        drawRect(a.c(mezcla(palma, MesoTinta.BEJUCO_OSC, 0.5f), k, f), Offset(0f, atras), Size(w * 1.2f, w * 0.01f))
        drawRect(a.s(0.15f), Offset(0f, atras), Size(w * 1.2f, (h - atras) * 0.3f))
    }
    drawLine(a.b(0.3f, k), Offset(w * 0.47f, atras + 0.6f * u), Offset(w * 1.05f, atras + 0.6f * u), 1f * u)
}

/**
 * El metate en [c] (el centro de su cara de moler): la piedra de basalto, picada, en batea y un poco alzada por detrás sobre una
 * laja; la cara de moler lisa y más clara; la mano de moler encima, de través; la masa amontonada en la punta baja y unos granos
 * caídos. Lo corta el borde de la derecha.
 */
private fun DrawScope.metateMeso(m: MesaMeso, c: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(c); val f = m.f(c)
    val basalto = Color(0xFF6E6862)
    val cuerpo = poligonoMeso(c.x - w * 0.16f, c.y - w * 0.035f, c.x + w * 0.22f, c.y - w * 0.06f, c.x + w * 0.22f, c.y + w * 0.02f, c.x - w * 0.15f, c.y + w * 0.035f)
    contactoMeso(Offset(c.x + w * 0.02f, c.y + w * 0.032f), w * 0.4f, w * 0.022f, a, 0.5f)
    drawPath(cuerpo, a.c(basalto, k, f))
    // El frente picado y la cara de moler en batea, lisa.
    clipPath(cuerpo) {
        val picado = Path()
        for (j in 0 until 40) { val q = Offset(c.x - w * 0.16f + w * 0.38f * azar(j, 911), c.y - w * 0.01f + w * 0.045f * azar(j, 912)); picado.addOval(Rect(q.x, q.y, q.x + 1.4f * u, q.y + 1f * u)) }
        drawPath(picado, a.s(0.35f))
        drawRect(a.s(0.25f), Offset(c.x - w * 0.2f, c.y + w * 0.012f), Size(w * 0.5f, w * 0.03f))
    }
    val cara = poligonoMeso(c.x - w * 0.15f, c.y - w * 0.036f, c.x + w * 0.22f, c.y - w * 0.061f, c.x + w * 0.22f, c.y - w * 0.035f, c.x - w * 0.14f, c.y - w * 0.008f)
    drawPath(cara, a.c(mezcla(basalto, ESTUCO, 0.28f), k, f))
    clipPath(cara) {
        drawOval(a.s(0.25f), Offset(c.x - w * 0.06f, c.y - w * 0.05f), Size(w * 0.24f, w * 0.03f))
        drawRect(a.b(0.3f, k), Offset(c.x - w * 0.15f, c.y - w * 0.04f), Size(w * 0.38f, w * 0.006f))
    }
    drawPath(cara, a.s(0.3f), style = Stroke(0.8f * u))
    // La masa en la punta baja.
    val masa = contornoMeso(Rect(c.x - w * 0.15f, c.y - w * 0.045f, c.x - w * 0.07f, c.y - w * 0.01f), 913, 0.18f)
    drawPath(masa, a.c(mezcla(MAIZ, ESTUCO, 0.6f), k, f))
    drawPath(bordeMeso(masa, -w * 0.006f, -w * 0.004f), a.s(0.25f))
    drawPath(bordeMeso(masa, w * 0.004f, w * 0.004f), a.b(0.4f, k))
    // La mano de moler, de través, con su luz arriba y su sombra en la cara.
    val mano = Rect(c.x - w * 0.04f, c.y - w * 0.075f, c.x + w * 0.2f, c.y - w * 0.045f)
    rotate(-6f, mano.center) {
        drawRoundRect(a.s(0.4f), Offset(mano.left + w * 0.006f, mano.top + w * 0.01f), mano.size, androidx.compose.ui.geometry.CornerRadius(mano.height / 2))
        drawRoundRect(a.c(mezcla(basalto, CALIZA, 0.3f), k, f), mano.topLeft, mano.size, androidx.compose.ui.geometry.CornerRadius(mano.height / 2))
        drawRect(a.b(0.35f, k), Offset(mano.left + mano.height * 0.4f, mano.top + mano.height * 0.12f), Size(mano.width - mano.height * 0.8f, mano.height * 0.22f))
        drawRect(a.s(0.3f), Offset(mano.left + mano.height * 0.3f, mano.bottom - mano.height * 0.35f), Size(mano.width - mano.height * 0.6f, mano.height * 0.3f))
    }
    // Unos granos caídos.
    for (j in 0 until 6) {
        val q = Offset(c.x - w * 0.22f + w * 0.08f * azar(j, 914), c.y + w * 0.04f + w * 0.02f * azar(j, 915))
        drawOval(a.c(MAIZ, k, f), Offset(q.x, q.y), Size(w * 0.008f, w * 0.006f))
        drawOval(a.s(0.3f), Offset(q.x + w * 0.002f, q.y + w * 0.004f), Size(w * 0.008f, w * 0.003f))
    }
}

/** Dos mazorcas secas, sin hojas, en el piso junto al metate, y una hoja suelta. */
private fun DrawScope.mazorcasSecasMeso(m: MesaMeso, c: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.k(c); val f = m.f(c)
    val hoja = poligonoMeso(c.x - w * 0.09f, c.y + w * 0.01f, c.x - w * 0.02f, c.y - w * 0.012f, c.x + w * 0.05f, c.y + w * 0.004f, c.x - w * 0.01f, c.y + w * 0.02f)
    drawPath(hoja, a.c(TOTOMOXTLE, k, f))
    drawPath(bordeMeso(hoja, 0f, -1.2f * u), a.s(0.3f))
    listOf(Triple(-0.03f, -0.004f, -14f), Triple(0.03f, 0.012f, 8f)).forEachIndexed { i, (dx, dy, g) ->
        val cc = Offset(c.x + w * dx, c.y + w * dy)
        rotate(g, cc) {
            val r = Rect(cc.x - w * 0.05f, cc.y - w * 0.014f, cc.x + w * 0.05f, cc.y + w * 0.014f)
            contactoMeso(Offset(cc.x, r.bottom), r.width, w * 0.01f, a, 0.4f)
            val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, androidx.compose.ui.geometry.CornerRadius(r.height / 2))) }
            drawPath(forma, a.c(if (i == 0) MAIZ else mezcla(MAIZ, MesoTinta.MAIZ_ROJO, 0.6f), k, f))
            clipPath(forma) {
                val granos = Path()
                for (fi in 0 until 3) { var x = r.left + (if (fi % 2 == 0) 0f else w * 0.004f); val y = r.top + r.height * (0.08f + 0.3f * fi); while (x < r.right) { granos.addOval(Rect(x, y, x + w * 0.007f, y + r.height * 0.26f)); x += w * 0.008f } }
                drawPath(granos, a.b(0.25f, k))
                drawPath(granos, a.s(0.25f), style = Stroke(0.5f * u))
                drawRect(a.b(0.2f, k), r.topLeft, Size(r.width, r.height * 0.3f))
                drawRect(a.s(0.3f), Offset(r.left, r.bottom - r.height * 0.35f), Size(r.width, r.height * 0.35f))
            }
        }
    }
}

// ---------------------------------------------------------------- las lumbres

/** Una lengua de llama en [c] (su base), de [alto] y [ancho], inclinada [giro] grados. */
private fun lenguaMeso(c: Offset, alto: Float, ancho: Float, giro: Float): Path = Path().apply {
    val dx = alto * giro / 60f
    moveTo(c.x - ancho * 0.5f, c.y)
    cubicTo(c.x - ancho * 0.66f, c.y - alto * 0.32f, c.x - ancho * 0.08f + dx * 0.3f, c.y - alto * 0.55f, c.x + dx * 0.7f, c.y - alto * 0.8f)
    quadraticTo(c.x + dx * 0.95f - ancho * 0.08f, c.y - alto * 0.9f, c.x + dx, c.y - alto)
    cubicTo(c.x + dx * 0.5f + ancho * 0.3f, c.y - alto * 0.64f, c.x + ancho * 0.66f, c.y - alto * 0.3f, c.x + ancho * 0.5f, c.y)
    close()
}

/** Las lenguas de una lumbre en [c]: cuatro capas (rojo de brasa, naranja, oro y el corazón claro), de [escala] de ancho y [alta]. */
private fun DrawScope.llamasMeso(c: Offset, escala: Float, alta: Float, semilla: Int) {
    val lenguas = listOf(Triple(-0.34f, 0.62f, -16f), Triple(0.3f, 0.72f, 18f), Triple(-0.06f, 1f, 6f), Triple(0.1f, 0.5f, -8f))
    listOf(Color(0xFFD9542A) to 1f, BRASA to 0.78f, LLAMA to 0.55f, LLAMA_VIVA to 0.32f).forEach { (col, s) ->
        lenguas.forEachIndexed { i, (dx, h, g) ->
            val base = Offset(c.x + dx * escala * 0.5f, c.y)
            drawPath(lenguaMeso(base, alta * h * (0.35f + 0.65f * s) * (0.9f + 0.2f * azar(semilla, i)), escala * 0.36f * s, g), col)
        }
    }
}

/**
 * La lumbre del fogón: de noche, el halo que alumbra el piso y lo de abajo del anaquel, y las llamas que suben de los leños con
 * sus chispas; en los Sueños de noche, baja (brasas y una llama chica); en los Sueños claros, solo las brasas encendidas.
 */
internal fun DrawScope.lumbreFogonMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val c = Offset(m.fogon.x, m.base + w * 0.248f)
    when {
        a.ocaso -> mancha(c, w * 0.16f, BRASA.copy(alpha = 0.35f))
        a.lunar -> {
            mancha(c, w * 0.32f, BRASA.copy(alpha = 0.3f))
            llamasMeso(c, w * 0.08f, w * 0.06f, 921)
        }
        else -> {
            mancha(Offset(c.x, c.y - w * 0.04f), w * 0.45f, BRASA.copy(alpha = 0.2f))
            mancha(Offset(c.x, c.y - w * 0.05f), w * 0.16f, LLAMA.copy(alpha = 0.45f))
            llamasMeso(c, w * 0.12f, w * 0.13f, 922)
            for (j in 0 until 6) {
                val q = Offset(c.x + w * (azar(j, 923) - 0.5f) * 0.12f, c.y - w * (0.14f + 0.1f * azar(j, 924)))
                drawCircle(LLAMA_VIVA.copy(alpha = 0.85f), (0.8f + 0.7f * azar(j, 925)) * u, q)
            }
        }
    }
}

/**
 * La lumbre de ocote en el nicho: de noche, el hueco encendido por dentro (el fondo y los costados de naranja) y las llamas
 * que suben de las rajas, con su hilo de humo negro hacia el hollín; en los Sueños de noche, baja; en los Sueños claros, recién
 * prendida, chica.
 */
internal fun DrawScope.lumbreNichoMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val r = nichoRectMeso(w)
    val c = Offset(r.center.x, r.bottom - r.height * 0.2f)
    val fuerza = if (a.ocaso) 0.35f else if (a.lunar) 0.6f else 1f
    // El hueco encendido por dentro.
    drawRect(BRASA.copy(alpha = 0.32f * fuerza), Offset(r.left + r.width * 0.16f, r.top + r.height * 0.1f), Size(r.width * 0.72f, r.height * 0.8f))
    mancha(Offset(c.x, c.y - r.height * 0.2f), r.width * 0.7f, LLAMA.copy(alpha = 0.55f * fuerza))
    // El humo negro del ocote, hacia arriba.
    if (!a.ocaso) {
        var p = Offset(c.x, c.y - r.height * 0.5f * fuerza)
        for (j in 0 until 4) {
            val sube = r.height * (0.18f + 0.04f * j)
            val lado = (if (j % 2 == 0) 1f else -1f) * r.width * (0.08f + 0.04f * j)
            val q = p + Offset(lado * 0.3f, -sube)
            val seg = Path().apply { moveTo(p.x, p.y); cubicTo(p.x + lado, p.y - sube * 0.35f, q.x + lado, q.y + sube * 0.35f, q.x, q.y) }
            drawPath(seg, Color(0xFF1A1210).copy(alpha = 0.45f - 0.08f * j), style = Stroke((1.4f + 1.1f * j) * u, cap = StrokeCap.Round))
            p = q
        }
    }
    llamasMeso(c, r.width * 0.42f * fuerza, r.height * 0.5f * fuerza, 931)
    drawCircle(Color(0xFFFFF4D8).copy(alpha = 0.9f * fuerza), 1.4f * u, Offset(c.x, c.y - 2f * u))
    if (!a.ocaso && !a.lunar) for (j in 0 until 3) {
        drawCircle(LLAMA_VIVA.copy(alpha = 0.8f), (0.7f + 0.5f * azar(j, 932)) * u, Offset(c.x + r.width * (azar(j, 933) - 0.5f) * 0.6f, r.top - r.height * (0.05f + 0.2f * azar(j, 934))))
    }
}
