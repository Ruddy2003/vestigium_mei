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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.AI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.BAMBU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KAMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KINARI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SHU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WARA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WASHI
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// Lo que se deja en los huecos libres del estante de la casa de té (MuebleEstanteEdo).

/**
 * Lo que se deja en el hueco libre de una repisa, de tres clases según la repisa: la botella de sake con su tacita; el abanico
 * redondo recostado en el fondo, con la toalla doblada; los cuencos de laca negra con sus palillos. [kc] es la luz cálida
 * que le llega y [kf] la de la luna.
 */
internal fun DrawScope.huecoLibreEdo(k: Int, r: Rect, oc: Float, a: AireEdo, kc: Float, kf: Float) {
    val u = density
    val libre = r.right - oc
    if (libre < 40f * u) return
    val s = minOf(r.height * 0.95f, libre * 1.4f)
    val x = oc + libre * (if (libre > s * 1.6f) 0.6f else 0.5f)
    val pie = Offset(x, r.bottom)
    when (k % 3) {
        0 -> sakeHuecoEdo(pie, s, a, kc, kf)
        1 -> abanicoEdo(pie, s, a, kc, kf, k)
        else -> cuencosEdo(pie, s, a, kc, kf)
    }
}

/** La sombra que algo deja en el fondo del hueco, corrida hacia la derecha (de día) o lejos del farol (de noche). */
private fun DrawScope.sombraFondoEdo(a: AireEdo, alfa: Float, silueta: DrawScope.(Color) -> Unit) {
    val u = density
    val dx = if (a.oscuro) -7f * u else 8f * u
    val s = a.sombra(alfa)
    val desplazado = Offset(dx, 3f * u)
    translate(desplazado.x, desplazado.y) { silueta(s) }
}

/** La botella de sake de barro, con su tacita de loza con una raya añil, en el hueco. */
private fun DrawScope.sakeHuecoEdo(pie: Offset, s: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val alto = s * 0.66f; val bw = alto * 0.5f
    val c = Offset(pie.x - bw * 0.3f, pie.y)
    val cuerpo = Path().apply {
        moveTo(c.x - bw * 0.13f, c.y - alto)
        lineTo(c.x - bw * 0.11f, c.y - alto * 0.78f)
        cubicTo(c.x - bw * 0.64f, c.y - alto * 0.6f, c.x - bw * 0.58f, c.y, c.x - bw * 0.3f, c.y)
        lineTo(c.x + bw * 0.3f, c.y)
        cubicTo(c.x + bw * 0.58f, c.y, c.x + bw * 0.64f, c.y - alto * 0.6f, c.x + bw * 0.11f, c.y - alto * 0.78f)
        lineTo(c.x + bw * 0.13f, c.y - alto)
        close()
    }
    sombraFondoEdo(a, 0.3f) { col -> drawPath(cuerpo, col) }
    drawOval(a.sombra(0.45f), Offset(c.x - bw * 0.55f, pie.y - 2.4f * u), Size(bw * 1.2f, 4.8f * u))
    drawPath(cuerpo, a.luz(mezcla(KAMI, HINOKI, 0.3f), k, f))
    clipPath(cuerpo) {
        val vidrio = Path().apply {
            moveTo(c.x - bw, c.y - alto * 1.1f); lineTo(c.x + bw, c.y - alto * 1.1f); lineTo(c.x + bw, c.y - alto * 0.5f)
            quadraticTo(c.x + bw * 0.2f, c.y - alto * 0.42f, c.x, c.y - alto * 0.52f)
            quadraticTo(c.x - bw * 0.3f, c.y - alto * 0.36f, c.x - bw, c.y - alto * 0.5f); close()
        }
        drawPath(vidrio, a.luz(Color(0xFF3E3A52), k, f))
        drawRect(a.brillo(0.3f, k), Offset(c.x - bw * 0.5f, c.y - alto), Size(bw * 0.25f, alto))
        drawRect(a.sombra(0.32f), Offset(c.x + bw * 0.12f, c.y - alto), Size(bw * 0.5f, alto))
        drawRect(a.brillo(0.65f, k), Offset(c.x - bw * 0.34f, c.y - alto * 0.7f), Size(bw * 0.07f, alto * 0.2f))
    }
    filoFarolEdo(Offset(c.x + bw * 0.46f, c.y - alto * 0.45f), Offset(c.x + bw * 0.4f, c.y - alto * 0.1f), k, a)
    // La tacita, delante y a la derecha.
    val tc = Offset(c.x + bw * 0.85f, pie.y)
    val t = poligonoEdo(tc.x - bw * 0.26f, pie.y - alto * 0.18f, tc.x + bw * 0.26f, pie.y - alto * 0.18f, tc.x + bw * 0.15f, pie.y, tc.x - bw * 0.15f, pie.y)
    drawPath(corridoEdo(t, 2f * u, 1f * u), a.sombra(0.35f))
    drawPath(t, a.luz(mezcla(WASHI, KAMI, 0.15f), k, f))
    drawOval(a.luz(mezcla(KAMI, SUMI, 0.15f), k, f), Offset(tc.x - bw * 0.26f, pie.y - alto * 0.18f - 1.4f * u), Size(bw * 0.52f, 2.8f * u))
    drawLine(a.luz(AI, k, f), Offset(tc.x - bw * 0.23f, pie.y - alto * 0.13f), Offset(tc.x + bw * 0.23f, pie.y - alto * 0.13f), 1f * u)
    drawLine(a.brillo(0.5f, k), Offset(tc.x - bw * 0.2f, pie.y - alto * 0.16f), Offset(tc.x - bw * 0.13f, pie.y - 1f * u), 0.7f * u)
}

/** El abanico redondo de papel y varillas de bambú, recostado contra el fondo, y la toalla de algodón doblada delante. */
private fun DrawScope.abanicoEdo(pie: Offset, s: Float, a: AireEdo, k: Float, f: Float, semilla: Int) {
    val u = density
    val rr = s * 0.25f
    val tw = s * 0.46f; val th = s * 0.13f
    val tr = Rect(pie.x - tw * 0.6f, pie.y - th, pie.x + tw * 0.4f, pie.y)
    // El mango baja por detrás de la toalla: solo se ve la hoja, recostada en el fondo.
    val base = Offset(tr.left + tw * 0.35f, tr.top + th * 0.6f)
    val largo = s * 0.12f
    val papel = if (semilla % 2 == 0) AI else mezcla(WASHI, KAMI, 0.2f)
    rotate(30f, base) {
        val c = Offset(base.x, base.y - largo - rr * 0.85f)
        drawCircle(a.sombra(0.3f), rr, c + Offset(6f * u, 3f * u))
        drawLine(a.luz(mezcla(BAMBU, KURI_OSC, 0.2f), k, f), base, Offset(c.x, c.y + rr * 0.8f), 2.6f * u, StrokeCap.Round)
        drawCircle(a.luz(papel, k, f), rr, c)
        // Las varillas que se abren desde el mango, el papel teñido en reserva con una ola de puntos claros, y la luz.
        for (j in 0 until 13) {
            val ang = Math.toRadians((195.0 + 150.0 * j / 12.0)).toFloat()
            drawLine(a.sombra(0.2f), Offset(c.x, c.y + rr * 0.72f), Offset(c.x + rr * 0.96f * kotlin.math.cos(ang), c.y + rr * 0.96f * kotlin.math.sin(ang)), 0.6f * u)
        }
        for (j in 0 until 7) {
            val ang = Math.toRadians((210.0 + 120.0 * j / 6.0)).toFloat()
            drawCircle(a.luz(if (semilla % 2 == 0) WASHI else AI, k, f).copy(alpha = 0.7f), rr * 0.07f, Offset(c.x + rr * 0.6f * kotlin.math.cos(ang), c.y + rr * 0.6f * kotlin.math.sin(ang)))
        }
        drawArc(a.brillo(0.4f, k), 190f, 80f, false, Offset(c.x - rr * 0.9f, c.y - rr * 0.9f), Size(rr * 1.8f, rr * 1.8f), style = Stroke(1.2f * u))
        drawCircle(a.luz(KURI_OSC, k, f), rr, c, style = Stroke(1.6f * u))
    }
    // La toalla doblada: tres dobleces de algodón añil con rayas claras, con su bulto y su sombra.
    drawRect(a.sombra(0.4f), tr.topLeft + Offset(2.4f * u, 1.6f * u), tr.size)
    drawRoundRect(a.luz(mezcla(AI, KINARI, 0.12f), k, f), tr.topLeft, tr.size, CornerRadius(2.4f * u))
    var x = tr.left + 3f * u
    while (x < tr.right) { drawLine(a.luz(WASHI, k, f).copy(alpha = 0.45f), Offset(x, tr.top + 1f * u), Offset(x, tr.bottom - 1f * u), 1f * u); x += 5f * u }
    for (j in 1..2) drawLine(a.sombra(0.4f), Offset(tr.left + 1f * u, tr.top + th * j / 3f), Offset(tr.right - 1f * u, tr.top + th * j / 3f), 0.8f * u)
    drawRect(a.brillo(0.4f, k), tr.topLeft, Size(tw, 1.2f * u))
    drawRect(a.sombra(0.3f), Offset(tr.right - tw * 0.15f, tr.top), Size(tw * 0.15f, th))
}

/** Dos cuencos de laca negra con su tapa, uno sobre otro, con su brillo, y los palillos de madera atravesados encima. */
private fun DrawScope.cuencosEdo(pie: Offset, s: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val cw = s * 0.42f; val ch = s * 0.19f
    sombraFondoEdo(a, 0.3f) { col -> drawRect(col, Offset(pie.x - cw / 2, pie.y - ch * 2f), Size(cw, ch * 2f)) }
    drawOval(a.sombra(0.45f), Offset(pie.x - cw * 0.6f, pie.y - 2.4f * u), Size(cw * 1.3f, 4.8f * u))
    for (i in 0 until 2) {
        val y1 = pie.y - ch * i * 0.95f
        val cuenco = Path().apply {
            moveTo(pie.x - cw / 2, y1 - ch)
            lineTo(pie.x + cw / 2, y1 - ch)
            cubicTo(pie.x + cw / 2, y1 - ch * 0.3f, pie.x + cw * 0.3f, y1, pie.x + cw * 0.18f, y1)
            lineTo(pie.x - cw * 0.18f, y1)
            cubicTo(pie.x - cw * 0.3f, y1, pie.x - cw / 2, y1 - ch * 0.3f, pie.x - cw / 2, y1 - ch)
            close()
        }
        drawPath(cuenco, a.luz(mezcla(SUMI, KURI_OSC, 0.25f), k, f))
        clipPath(cuenco) {
            drawRect(a.brillo(0.5f, k), Offset(pie.x - cw * 0.36f, y1 - ch), Size(cw * 0.08f, ch))
            drawRect(a.brillo(0.2f, k), Offset(pie.x - cw * 0.5f, y1 - ch), Size(cw * 0.3f, ch))
        }
        drawRect(a.luz(mezcla(KURI_OSC, SHU, 0.25f), k, f), Offset(pie.x - cw / 2, y1 - ch - 1.2f * u), Size(cw, 2f * u))
        drawRect(a.brillo(0.6f, k), Offset(pie.x - cw / 2, y1 - ch - 1.2f * u), Size(cw * 0.5f, 0.7f * u))
        filoFarolEdo(Offset(pie.x + cw * 0.46f, y1 - ch * 0.8f), Offset(pie.x + cw * 0.2f, y1 - ch * 0.1f), k, a)
    }
    val yP = pie.y - ch * 1.95f - 2f * u
    drawLine(a.sombra(0.35f), Offset(pie.x - cw * 0.7f, yP + 3f * u), Offset(pie.x + cw * 0.65f, yP + 0.6f * u), 1.6f * u, StrokeCap.Round)
    drawLine(a.luz(HINOKI, k + 0.1f, f), Offset(pie.x - cw * 0.7f, yP + 1.6f * u), Offset(pie.x + cw * 0.65f, yP - 1.2f * u), 1.5f * u, StrokeCap.Round)
    drawLine(a.luz(mezcla(HINOKI, WARA, 0.4f), k + 0.1f, f), Offset(pie.x - cw * 0.66f, yP + 3.4f * u), Offset(pie.x + cw * 0.7f, yP + 0.8f * u), 1.5f * u, StrokeCap.Round)
}
