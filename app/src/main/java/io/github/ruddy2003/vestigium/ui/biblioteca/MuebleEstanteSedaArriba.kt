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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ADOBE
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ALAMO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ALAMO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ANIL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ARCILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.BERMELLON
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.CAL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.CUERO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.FIELTRO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.PAJA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.SOL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.TIERRA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.VIDRIO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

// Lo de arriba del almacén de la Ruta de la Seda (MuebleEstanteSeda): lo que hay en el vasar y lo que cuelga del muro.

// ---------------------------------------------------------------- arriba

/**
 * La lámpara de bronce colgada de una estaca del muro por tres cadenas: el cuenco en forma de barca, el pico largo hacia la
 * izquierda (donde arde la mecha, en [llama]) y la anilla de atrás. De día, apagada; de noche, la llama la pone [llamaSeda].
 */
internal fun DrawScope.lamparaSeda(estaca: Offset, llama: Offset, w: Float, a: AireSeda) {
    val u = density
    val k = if (a.lumbre) 0.95f else 0f
    val an = w * 0.1f
    val c = Offset(llama.x + an * 0.62f, llama.y + w * 0.014f)
    val alto = an * 0.3f
    // La sombra de la lámpara y de las cadenas en la cal: de día a la derecha y abajo; de noche no hay (la luz es ella).
    val anillo = Offset(estaca.x, estaca.y + w * 0.018f)
    val puntas = listOf(Offset(c.x - an * 0.36f, c.y - alto * 0.4f), Offset(c.x + an * 0.06f, c.y - alto * 0.5f), Offset(c.x + an * 0.4f, c.y - alto * 0.36f))
    if (!a.oscuro) translate(w * 0.02f, w * 0.014f) {
        val sc = a.sombra(0.16f)
        puntas.forEach { drawLine(sc, anillo, it, 1f * u) }
        drawOval(sc, Offset(c.x - an * 0.4f, c.y - alto * 0.4f), Size(an * 0.8f, alto))
    }
    // La estaca de álamo clavada en el muro, con su sombra.
    drawOval(a.sombra(0.35f), Offset(estaca.x - 3f * u, estaca.y - 1f * u), Size(9f * u, 6f * u))
    drawRoundRect(a.luz(ALAMO, k * 0.6f), Offset(estaca.x - 3.4f * u, estaca.y - 3.4f * u), Size(6.8f * u, 6.8f * u), CornerRadius(3.4f * u))
    drawCircle(a.luz(ALAMO_OSC, k * 0.6f), 1.6f * u, estaca)
    // La anilla y las tres cadenas, eslabón por eslabón.
    val bronce = a.luz(BRONCE, k)
    val bronceOsc = a.luz(mezcla(BRONCE, ALAMO_OSC, 0.55f), k)
    drawCircle(bronceOsc, 2.4f * u, anillo, style = Stroke(1.1f * u))
    drawLine(bronceOsc, estaca, anillo, 1f * u)
    puntas.forEach { p ->
        val v = p - anillo; val largo = v.getDistance()
        val pasos = (largo / (3.2f * u)).toInt().coerceIn(2, 60)
        for (j in 0 until pasos) {
            val q = anillo + v * ((j + 0.5f) / pasos)
            if (j % 2 == 0) drawOval(bronceOsc, Offset(q.x - 0.9f * u, q.y - 1.6f * u), Size(1.8f * u, 3.2f * u), style = Stroke(0.6f * u))
            else drawLine(bronceOsc, q - Offset(0f, 1.2f * u), q + Offset(0f, 1.2f * u), 0.7f * u)
        }
    }
    // El cuenco: una barca de bronce, ancha y baja, con el borde vuelto; el pico, un caño que se afina y se alza en la punta.
    val pico = Path().apply {
        moveTo(c.x - an * 0.3f, c.y - alto * 0.38f)
        cubicTo(c.x - an * 0.45f, c.y - alto * 0.3f, llama.x + an * 0.06f, llama.y + alto * 0.36f, llama.x - an * 0.02f, llama.y + alto * 0.02f)
        lineTo(llama.x + an * 0.05f, llama.y - alto * 0.04f)
        cubicTo(llama.x + an * 0.12f, llama.y + alto * 0.5f, c.x - an * 0.4f, c.y + alto * 0.1f, c.x - an * 0.28f, c.y + alto * 0.08f)
        close()
    }
    drawPath(pico, bronce)
    drawPath(bordeSeda(pico, 0f, -alto * 0.12f), a.sombra(0.35f))
    val cuenco = Path().apply {
        moveTo(c.x - an * 0.4f, c.y - alto * 0.42f)
        lineTo(c.x + an * 0.42f, c.y - alto * 0.42f)
        cubicTo(c.x + an * 0.44f, c.y + alto * 0.2f, c.x + an * 0.2f, c.y + alto * 0.6f, c.x, c.y + alto * 0.6f)
        cubicTo(c.x - an * 0.24f, c.y + alto * 0.6f, c.x - an * 0.42f, c.y + alto * 0.2f, c.x - an * 0.4f, c.y - alto * 0.42f)
        close()
    }
    drawPath(cuenco, bronce)
    redondoSeda(cuenco, a, k, -1f, fuerza = 1.2f)
    // El borde vuelto, con su luz, y la boca del aceite.
    drawOval(a.luz(mezcla(BRONCE, LUZ_MUEBLE, 0.35f), k), Offset(c.x - an * 0.42f, c.y - alto * 0.56f), Size(an * 0.86f, alto * 0.3f))
    drawOval(a.luz(mezcla(BRONCE, ALAMO_OSC, 0.75f), k), Offset(c.x - an * 0.34f, c.y - alto * 0.5f), Size(an * 0.7f, alto * 0.18f))
    // La anilla de atrás, para colgarla o tomarla.
    drawCircle(bronce, an * 0.07f, Offset(c.x + an * 0.47f, c.y - alto * 0.2f), style = Stroke(1.4f * u))
    // El cardenillo en el pie del cuenco y la mecha en la punta del pico.
    drawOval(a.luz(Color(0xFF5E8A74), k).copy(alpha = 0.5f), Offset(c.x - an * 0.12f, c.y + alto * 0.3f), Size(an * 0.2f, alto * 0.2f))
    drawLine(a.luz(TINTA, k), Offset(llama.x + an * 0.01f, llama.y + alto * 0.02f), Offset(llama.x - an * 0.005f, llama.y - alto * 0.22f), 1.4f * u, StrokeCap.Round)
    if (a.oscuro) drawArc(FILO_MUEBLE.copy(alpha = 0.7f), 200f, 120f, false, Offset(c.x - an * 0.42f, c.y - alto * 0.56f), Size(an * 0.86f, alto * 0.3f), style = Stroke(1f * u))
}

/** La llama de la lámpara, con su color propio; en los Sueños de noche, más baja. */
internal fun DrawScope.llamaSeda(p: Offset, w: Float, a: AireSeda) {
    val s = w * (if (a.oscuro && a.noche) 0.024f else 0.03f)
    val q = p - Offset(0f, w * 0.004f)
    drawCircle(LLAMA.copy(alpha = 0.16f), s * 1.1f, q - Offset(0f, s * 0.5f))
    drawCircle(LLAMA.copy(alpha = 0.22f), s * 0.66f, q - Offset(0f, s * 0.5f))
    fun lengua(k: Float, ladeo: Float) = Path().apply {
        moveTo(q.x - s * 0.17f * k, q.y)
        cubicTo(q.x - s * 0.3f * k, q.y - s * 0.4f * k, q.x + ladeo * s - s * 0.05f * k, q.y - s * 0.7f * k, q.x + ladeo * s, q.y - s * 1.15f * k)
        cubicTo(q.x + ladeo * s + s * 0.12f * k, q.y - s * 0.6f * k, q.x + s * 0.3f * k, q.y - s * 0.35f * k, q.x + s * 0.17f * k, q.y)
        close()
    }
    drawPath(lengua(1f, -0.06f), Color(0xFFFF9A2E))
    drawPath(lengua(0.72f, -0.04f), LLAMA)
    drawPath(lengua(0.4f, 0f), Color(0xFFFFF6DC))
}

/**
 * Lo que manda arriba: los vidrios que vende Nanai-vandak, recién sacados de su paja, al sol de la reja. La botella de vidrio
 * verde pálido, de cuello largo y panza tallada a facetas, y delante el cuenco tallado de alveolos; debajo, el paño de añil
 * en que venían envueltos y la paja. Por donde el vidrio junta el sol, deja en la cal y en el vasar unos ojos de luz.
 */
internal fun DrawScope.vidriosSeda(pie: Offset, w: Float, a: AireSeda, cerca: Float, frio: Float, haciaX: Float) {
    val u = density
    val alto = w * 0.28f
    val r = w * 0.066f
    val cuerpoC = Offset(pie.x, pie.y - alto * 0.33f)
    // El paño de añil, doblado, y la paja del embalaje.
    val pano = Path().apply {
        moveTo(pie.x - w * 0.095f, pie.y + 0.5f * u)
        cubicTo(pie.x - w * 0.09f, pie.y - w * 0.018f, pie.x - w * 0.05f, pie.y - w * 0.012f, pie.x - w * 0.02f, pie.y - w * 0.02f)
        cubicTo(pie.x + w * 0.04f, pie.y - w * 0.026f, pie.x + w * 0.09f, pie.y - w * 0.012f, pie.x + w * 0.13f, pie.y - w * 0.008f)
        lineTo(pie.x + w * 0.15f, pie.y + 0.5f * u); close()
    }
    drawPath(corridoSeda(pano, 3f * u, 2f * u), a.sombra(0.3f))
    drawPath(pano, a.luz(ANIL, cerca, frio))
    clipPath(pano) {
        for (j in 0 until 5) {
            val x = pie.x - w * 0.08f + w * 0.05f * j
            drawLine(a.sombra(0.3f), Offset(x, pie.y - w * 0.03f), Offset(x + w * 0.02f, pie.y), 1f * u)
            drawLine(a.claro(0.25f), Offset(x - 1.4f * u, pie.y - w * 0.03f), Offset(x + w * 0.02f - 1.4f * u, pie.y), 0.7f * u)
        }
    }
    val paja = a.luz(PAJA, cerca, frio)
    for (j in 0 until 16) {
        val o = Offset(pie.x - w * 0.11f + w * 0.27f * azar(901, j), pie.y - w * 0.004f * azar(902, j) * 3f)
        val ang = -0.6f + 1.2f * azar(903, j)
        val l = w * (0.02f + 0.03f * azar(904, j))
        drawLine(paja, o, o + Offset(l * cos(ang), l * sin(ang) * 0.5f), 0.8f * u, StrokeCap.Round)
    }
    // La sombra de la botella en la cal y en el vasar: de día a la derecha, larga.
    val botella = Path().apply {
        addPath(perfilSeda(pie.x, pie.y - alto, alto, floatArrayOf(
            0.05f, 0f, 0.046f, 0.05f, 0.038f, 0.17f, 0.038f, 0.31f, 0.066f, 0.42f, 0.175f, 0.52f, 0.24f, 0.67f, 0.23f, 0.83f, 0.17f, 0.95f, 0.11f, 1f,
        )))
    }
    val sombraLargo = if (a.oscuro) Offset(-w * 0.035f, w * 0.012f) else Offset(w * 0.055f, w * 0.018f)
    // La sombra del vidrio es de color: verde y transparente, con el ojo de luz que junta la panza en el medio.
    translate(sombraLargo.x, sombraLargo.y) {
        if (a.oscuro) drawPath(botella, a.sombra(0.22f))
        else {
            drawPath(botella, Color(0xFF4E8A6E).copy(alpha = if (a.noche) 0.12f else 0.14f))
        }
    }
    contactoSeda(Offset(pie.x + 3f * u, pie.y), w * 0.1f, w * 0.012f, a, 0.3f)
    // El vidrio: deja ver la cal de atrás, teñida de verde, más hondo en los bordes y en el fondo grueso.
    val vidrio = a.luz(VIDRIO, cerca, frio)
    drawPath(botella, vidrio.copy(alpha = 0.55f))
    clipPath(botella) {
        drawRect(a.luz(mezcla(VIDRIO, Color(0xFF3E6E5E), 0.6f), cerca, frio).copy(alpha = 0.55f), Offset(pie.x - w * 0.07f, pie.y - w * 0.018f), Size(w * 0.14f, w * 0.02f))
        // Las facetas talladas de la panza: alveolos en tres hileras, cada uno con su borde de luz y su fondo verde.
        for (fila in 0 until 3) {
            val yy = cuerpoC.y - r * 0.55f + fila * r * 0.5f
            val cuantos = 6
            for (j in 0 until cuantos) {
                val t = (j + 0.5f * (fila % 2)) / cuantos - 0.5f
                val xx = pie.x + t * r * 2.1f
                val ancho = r * 0.32f * cos(t * 2.6f).coerceAtLeast(0.15f)
                drawOval(a.luz(mezcla(VIDRIO, Color(0xFF3E6E5E), 0.45f), cerca, frio).copy(alpha = 0.5f), Offset(xx - ancho / 2, yy - r * 0.2f), Size(ancho, r * 0.4f))
                drawArc(a.luz(mezcla(VIDRIO, Color.White, 0.6f), cerca + 0.2f, frio).copy(alpha = 0.7f), 150f, 120f, false, Offset(xx - ancho / 2, yy - r * 0.2f), Size(ancho, r * 0.4f), style = Stroke(0.6f * u))
            }
        }
        // El borde, más hondo: el vidrio de lado se ve verde.
        drawPath(bordeSeda(botella, w * 0.008f, 0f), a.luz(mezcla(VIDRIO, Color(0xFF2E5A4C), 0.6f), cerca, frio).copy(alpha = 0.7f))
        drawPath(bordeSeda(botella, -w * 0.008f, 0f), a.luz(mezcla(VIDRIO, Color(0xFF2E5A4C), 0.6f), cerca, frio).copy(alpha = 0.5f))
    }
    // Los brillos: el de la ventana a la izquierda, largo, y uno chico a la derecha; de noche, el de la lámpara y el de la luna.
    val brillo = if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.3f + 0.6f * cerca) else Color.White.copy(alpha = if (a.noche) 0.7f else 0.9f)
    val xb = if (a.oscuro && haciaX > 0) 1f else -1f
    drawRoundRect(brillo, Offset(pie.x + xb * r * 0.6f - 1.2f * u, cuerpoC.y - r * 0.5f), Size(2.4f * u, r * 0.9f), CornerRadius(1.2f * u))
    drawRoundRect(brillo, Offset(pie.x + xb * w * 0.004f - 0.8f * u, pie.y - alto * 0.9f), Size(1.6f * u, alto * 0.28f), CornerRadius(0.8f * u))
    if (a.oscuro) drawRoundRect(a.helado(0.55f * frio + 0.1f), Offset(pie.x - xb * r * 0.7f - 0.8f * u, cuerpoC.y - r * 0.3f), Size(1.6f * u, r * 0.7f), CornerRadius(0.8f * u))
    // El labio del cuello.
    drawOval(a.luz(mezcla(VIDRIO, Color(0xFF3E6E5E), 0.4f), cerca, frio), Offset(pie.x - w * 0.016f, pie.y - alto - 1.4f * u), Size(w * 0.032f, 3.4f * u))
    drawOval(brillo, Offset(pie.x - w * 0.012f, pie.y - alto - 1f * u), Size(w * 0.012f, 1.4f * u))

    // El cuenco tallado, delante y a la derecha.
    val cb = Offset(pie.x + w * 0.072f, pie.y + 0.5f * u)
    val rc = w * 0.046f; val hc = w * 0.05f
    val cuenco = Path().apply {
        moveTo(cb.x - rc, cb.y - hc)
        cubicTo(cb.x - rc * 1.02f, cb.y - hc * 0.3f, cb.x - rc * 0.6f, cb.y, cb.x - rc * 0.3f, cb.y)
        lineTo(cb.x + rc * 0.3f, cb.y)
        cubicTo(cb.x + rc * 0.6f, cb.y, cb.x + rc * 1.02f, cb.y - hc * 0.3f, cb.x + rc, cb.y - hc)
        close()
    }
    translate(sombraLargo.x * 0.6f, sombraLargo.y * 0.3f) { drawPath(cuenco, a.sombra(if (a.oscuro) 0.22f else 0.14f)) }
    contactoSeda(Offset(cb.x + 2f * u, cb.y), rc * 1.6f, w * 0.01f, a, 0.3f)
    val vidrioC = a.luz(mezcla(VIDRIO, Color(0xFFE6E8D8), 0.5f), cerca, frio)
    drawPath(cuenco, vidrioC.copy(alpha = 0.6f))
    clipPath(cuenco) {
        for (fila in 0 until 2) {
            val yy = cb.y - hc * (0.62f - 0.36f * fila)
            for (j in 0 until 5) {
                val t = (j + 0.5f * fila) / 5f - 0.45f
                val xx = cb.x + t * rc * 2f
                val ancho = rc * 0.4f * cos(t * 2.4f).coerceAtLeast(0.2f)
                drawOval(a.luz(mezcla(VIDRIO, Color(0xFF3E6E5E), 0.35f), cerca, frio).copy(alpha = 0.45f), Offset(xx - ancho / 2, yy - hc * 0.16f), Size(ancho, hc * 0.32f))
                drawArc(brillo.copy(alpha = brillo.alpha * 0.7f), 160f, 100f, false, Offset(xx - ancho / 2, yy - hc * 0.16f), Size(ancho, hc * 0.32f), style = Stroke(0.6f * u))
            }
        }
        drawPath(bordeSeda(cuenco, rc * 0.12f, 0f), a.luz(Color(0xFF6A8A7C), cerca, frio).copy(alpha = 0.5f))
    }
    drawOval(vidrioC, Offset(cb.x - rc, cb.y - hc - rc * 0.12f), Size(rc * 2f, rc * 0.24f), style = Stroke(1.1f * u))
    drawArc(brillo, 180f, 70f, false, Offset(cb.x - rc, cb.y - hc - rc * 0.12f), Size(rc * 2f, rc * 0.24f), style = Stroke(1.2f * u))
    // Los ojos de luz que el vidrio junta y tira en el vasar y en la cal (solo con sol).
    if (!a.oscuro) {
        val ojo = (if (a.noche) SedaTinta.OCASO else SOL)
        listOf(Offset(w * 0.03f, w * 0.004f), Offset(w * 0.052f, -w * 0.02f), Offset(w * 0.12f, w * 0.002f)).forEachIndexed { i, d ->
            val o = Offset(cb.x + d.x, pie.y + d.y - 1f * u)
            drawOval(ojo.copy(alpha = 0.55f), Offset(o.x - w * 0.012f, o.y - w * 0.003f), Size(w * (0.024f - 0.004f * i), w * 0.006f))
            drawOval(Color.White.copy(alpha = 0.6f), Offset(o.x - w * 0.005f, o.y - w * 0.0015f), Size(w * 0.01f, w * 0.003f))
        }
        drawOval(Color(0xFFDDF2C8).copy(alpha = 0.5f), Offset(pie.x + w * 0.035f, pie.y - w * 0.07f), Size(w * 0.02f, w * 0.03f))
    }
}

/**
 * La carta de casa a medio cerrar: un fajito de cartas dobladas apoyado en la cal, atado con su cordón y su sello de arcilla,
 * el tintero de barro vidriado con su tapa y el cálamo cruzado delante.
 */
internal fun DrawScope.cartaSeda(pie: Offset, w: Float, a: AireSeda, cerca: Float, frio: Float, medidor: TextMeasurer) {
    val u = density
    // Dos cartas apoyadas en el muro, una delante de otra, ladeadas.
    listOf(-1f, 1f).forEachIndexed { i, l ->
        val an = w * 0.068f; val al = w * 0.108f
        val base = Offset(pie.x + l * w * 0.015f, pie.y)
        rotate(l * 5f + 3f, base) {
            val r = Rect(base.x - an / 2, base.y - al, base.x + an / 2, base.y)
            drawRect(a.sombra(if (a.oscuro) 0.3f else 0.18f), r.topLeft + Offset(w * 0.012f, w * 0.006f), r.size)
            val papel = a.luz(mezcla(PAPEL, ADOBE, 0.12f * i), cerca, frio)
            drawRect(papel, r.topLeft, r.size)
            // Los dobleces: una cara más clara y otra en sombra, y la pestaña de cierre.
            drawRect(a.claro(0.3f), r.topLeft, Size(an * 0.3f, al))
            drawRect(a.sombra(0.12f), Offset(r.right - an * 0.25f, r.top), Size(an * 0.25f, al))
            drawLine(a.sombra(0.25f), Offset(r.left, r.top + al * 0.33f), Offset(r.right, r.top + al * 0.33f), 0.6f * u)
            drawLine(a.sombra(0.22f), Offset(r.left, r.top + al * 0.66f), Offset(r.right, r.top + al * 0.66f), 0.6f * u)
            // El cordón, cruzado, y el sello de arcilla en el nudo.
            val cord = a.luz(if (i == 0) FIELTRO else CUERO, cerca, frio)
            drawLine(cord, Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 1f * u)
            drawLine(cord, Offset(r.left, r.top + al * 0.5f), Offset(r.right, r.top + al * 0.5f), 1f * u)
            if (i == 1) {
                val s = Offset(r.center.x, r.top + al * 0.5f)
                drawOval(a.luz(ARCILLA, cerca, frio), Offset(s.x - 3.6f * u, s.y - 3f * u), Size(7.2f * u, 6.2f * u))
                drawCircle(a.luz(mezcla(ARCILLA, ALAMO_OSC, 0.4f), cerca, frio), 1.8f * u, s, style = Stroke(0.6f * u))
                drawArc(a.claro(0.4f), 180f, 90f, false, Offset(s.x - 3.2f * u, s.y - 2.6f * u), Size(6.4f * u, 5.4f * u), style = Stroke(0.6f * u))
            }
        }
    }
    // El tintero: un botecito de barro vidriado de pardo, con su tapa y el borde manchado.
    val t = Offset(pie.x + w * 0.075f, pie.y)
    val bote = perfilSeda(t.x, t.y - w * 0.04f, w, floatArrayOf(0.01f, 0f, 0.012f, 0.006f, 0.02f, 0.016f, 0.021f, 0.03f, 0.016f, 0.04f))
    contactoSeda(Offset(t.x + 2f * u, t.y), w * 0.05f, w * 0.008f, a, 0.3f)
    drawPath(bote, a.luz(Color(0xFF5A3A26), cerca, frio))
    redondoSeda(bote, a, cerca, 1f)
    drawOval(a.luz(Color(0xFF3A2418), cerca, frio), Offset(t.x - w * 0.011f, t.y - w * 0.043f), Size(w * 0.022f, w * 0.007f))
    drawOval(a.claro(0.5f), Offset(t.x - w * 0.012f, t.y - w * 0.032f), Size(w * 0.006f, w * 0.012f))
    // El cálamo, cruzado delante, con la punta tiznada.
    val c0 = Offset(pie.x - w * 0.06f, pie.y - 1f * u); val c1 = Offset(pie.x + w * 0.12f, pie.y - w * 0.022f)
    drawLine(a.sombra(0.3f), c0 + Offset(2f * u, 2f * u), c1 + Offset(2f * u, 2f * u), 1.8f * u, StrokeCap.Round)
    drawLine(a.luz(mezcla(PAJA, ALAMO, 0.4f), cerca, frio), c0, c1, 1.8f * u, StrokeCap.Round)
    drawLine(a.claro(0.4f), c0 - Offset(0f, 0.6f * u), c1 - Offset(0f, 0.6f * u), 0.5f * u)
    drawLine(a.luz(TINTA, cerca, frio), c1 - (c1 - c0) * 0.05f, c1, 1.6f * u, StrokeCap.Round)
}

/** Un plato de barro con los melocotones dorados de Samarcanda y una hoja. */
internal fun DrawScope.melocotonesSeda(pie: Offset, w: Float, a: AireSeda, cerca: Float, frio: Float) {
    val u = density
    val rp = w * 0.07f
    contactoSeda(Offset(pie.x + 3f * u, pie.y), rp * 2.1f, w * 0.012f, a, 0.32f)
    val plato = Path().apply {
        moveTo(pie.x - rp, pie.y - w * 0.016f)
        lineTo(pie.x + rp, pie.y - w * 0.016f)
        cubicTo(pie.x + rp * 0.8f, pie.y - w * 0.004f, pie.x + rp * 0.5f, pie.y, pie.x + rp * 0.3f, pie.y)
        lineTo(pie.x - rp * 0.3f, pie.y)
        cubicTo(pie.x - rp * 0.5f, pie.y, pie.x - rp * 0.8f, pie.y - w * 0.004f, pie.x - rp, pie.y - w * 0.016f)
        close()
    }
    val frutas = listOf(Offset(-0.035f, -0.034f), Offset(0.03f, -0.036f), Offset(-0.002f, -0.052f))
    frutas.forEachIndexed { i, d ->
        val c = Offset(pie.x + w * d.x, pie.y + w * d.y)
        val rr = w * (0.024f - 0.002f * i)
        val f = Path().apply { addOval(Rect(c.x - rr, c.y - rr * 0.95f, c.x + rr, c.y + rr)) }
        drawPath(f, a.luz(Color(0xFFE9A848), cerca, frio))
        clipPath(f) {
            // El rubor del lado del sol y la hendidura.
            drawOval(a.luz(Color(0xFFC8573A), cerca, frio).copy(alpha = 0.7f), Offset(c.x - rr * 1.1f, c.y - rr), Size(rr * 1.3f, rr * 1.4f))
            drawOval(a.luz(Color(0xFFD9763E), cerca, frio).copy(alpha = 0.5f), Offset(c.x - rr * 0.5f, c.y - rr * 0.6f), Size(rr * 1.1f, rr * 1.1f))
        }
        drawArc(a.sombra(0.3f), 250f, 60f, false, Offset(c.x - rr * 0.3f, c.y - rr * 0.95f), Size(rr * 0.6f, rr * 1.4f), style = Stroke(0.7f * u))
        redondoSeda(f, a, cerca, 1f)
        drawCircle(a.claro(0.5f), rr * 0.18f, c - Offset(rr * 0.4f, rr * 0.4f))
    }
    // La hoja, de un verde gris, caída sobre el borde.
    val hoja = Path().apply {
        val o = Offset(pie.x + w * 0.05f, pie.y - w * 0.02f)
        moveTo(o.x, o.y); cubicTo(o.x + w * 0.02f, o.y - w * 0.018f, o.x + w * 0.045f, o.y - w * 0.006f, o.x + w * 0.05f, o.y + w * 0.004f)
        cubicTo(o.x + w * 0.03f, o.y + w * 0.008f, o.x + w * 0.012f, o.y + w * 0.006f, o.x, o.y); close()
    }
    drawPath(hoja, a.luz(Color(0xFF6E8A4C), cerca, frio))
    drawPath(plato, a.luz(ARCILLA, cerca, frio))
    clipPath(plato) { drawRect(a.luz(Color(0xFF9C8A4A), cerca, frio), Offset(pie.x - rp, pie.y - w * 0.018f), Size(rp * 2f, w * 0.004f)) }
    drawPath(bordeSeda(plato, 0f, -1.4f * u), a.sombra(0.3f))
    drawLine(a.claro(0.4f), Offset(pie.x - rp, pie.y - w * 0.016f), Offset(pie.x + rp * 0.4f, pie.y - w * 0.016f), 0.8f * u)
}

/**
 * El cayado del camellero, de álamo, apoyado en el muro con su gancho arriba y la correa de cuero: cruza el sol de la reja y
 * deja en la cal su sombra larga.
 */
internal fun DrawScope.cayadoSeda(pie: Offset, tope: Offset, w: Float, a: AireSeda, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
    val u = density
    val g = 3.2f * u
    val medio = (pie + tope) * 0.5f
    val k = cerca(medio); val f = frio(medio)
    val v = tope - pie
    val gancho = Path().apply {
        moveTo(tope.x, tope.y)
        cubicTo(tope.x + v.x * 0.06f, tope.y - w * 0.035f, tope.x + w * 0.055f, tope.y - w * 0.04f, tope.x + w * 0.06f, tope.y - w * 0.006f)
        cubicTo(tope.x + w * 0.062f, tope.y + w * 0.012f, tope.x + w * 0.05f, tope.y + w * 0.02f, tope.x + w * 0.044f, tope.y + w * 0.016f)
    }
    val palo = Path().apply { moveTo(pie.x, pie.y); lineTo(tope.x, tope.y); addPath(gancho) }
    // La sombra: de día, larga, a la derecha; de noche, la que le da la lámpara, hacia la izquierda.
    val corr = if (a.oscuro) Offset(-w * 0.02f, w * 0.006f) else Offset(w * 0.045f, w * 0.012f)
    drawPath(corridoSeda(palo, corr.x, corr.y), a.sombra(if (a.oscuro) 0.3f else 0.22f), style = Stroke(g, cap = StrokeCap.Round))
    val alamo = a.luz(mezcla(ALAMO, ALAMO_OSC, 0.2f), k, f)
    drawPath(palo, alamo, style = Stroke(g, cap = StrokeCap.Round))
    drawPath(corridoSeda(palo, -0.7f * u, -0.3f * u), a.claro(0.4f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
    if (a.oscuro) drawPath(corridoSeda(palo, 0.8f * u, 0f), FILO_MUEBLE.copy(alpha = 0.5f * k), style = Stroke(0.6f * u))
    // Los nudos del palo.
    for (j in 1..3) {
        val q = pie + v * (0.2f + 0.25f * j)
        drawOval(a.luz(ALAMO_OSC, k, f), Offset(q.x - 1.8f * u, q.y - 1f * u), Size(3.6f * u, 2f * u))
    }
    // La correa de cuero, dada vuelta bajo el gancho y colgando.
    val c = pie + v * 0.86f
    val cuero = a.luz(CUERO, k, f)
    drawLine(cuero, c - Offset(2.8f * u, 0.6f * u), c + Offset(2.8f * u, -0.6f * u), 3f * u)
    drawLine(a.claro(0.3f), c - Offset(2.8f * u, 1.6f * u), c + Offset(2.8f * u, -1.6f * u), 0.6f * u)
    val lazo = Path().apply { moveTo(c.x - 1.5f * u, c.y + 1f * u); cubicTo(c.x - w * 0.02f, c.y + w * 0.05f, c.x + w * 0.03f, c.y + w * 0.055f, c.x + 2f * u, c.y + 1f * u) }
    drawPath(corridoSeda(lazo, corr.x * 0.5f, corr.y * 0.5f), a.sombra(0.2f), style = Stroke(1.6f * u))
    drawPath(lazo, cuero, style = Stroke(1.8f * u, cap = StrokeCap.Round))
}

/**
 * La sarta de orejones de albaricoque, colgada en guirnalda entre dos estacas del muro para que acaben de secarse: el cordel
 * combado y los orejones ensartados, aplastados, de naranja a pardo; su sombra en la cal.
 */
internal fun DrawScope.sartaSeda(p0: Offset, p1: Offset, comba: Float, w: Float, a: AireSeda, cerca: Float, frio: Float) {
    val u = density
    val c = Offset((p0.x + p1.x) / 2, maxOf(p0.y, p1.y) + comba)
    fun punto(t: Float): Offset {
        val m = 1f - t
        return p0 * (m * m) + c * (2 * m * t) + p1 * (t * t)
    }
    val cuerda = Path().apply { moveTo(p0.x, p0.y); quadraticTo(c.x, c.y, p1.x, p1.y) }
    val corr = if (a.oscuro) Offset(-w * 0.012f, w * 0.008f) else Offset(w * 0.016f, w * 0.012f)
    drawPath(corridoSeda(cuerda, corr.x, corr.y), a.sombra(0.2f), style = Stroke(1f * u))
    drawPath(cuerda, a.luz(mezcla(PAJA, CUERO, 0.4f), cerca, frio), style = Stroke(0.9f * u))
    val n = 12
    for (j in 1 until n) {
        val t = j / n.toFloat()
        val q = punto(t)
        val rr = w * (0.014f + 0.004f * azar(980, j))
        drawOval(a.sombra(0.22f), Offset(q.x - rr + corr.x * 0.6f, q.y - rr * 0.7f + corr.y * 0.6f), Size(rr * 2f, rr * 1.4f))
        val col = mezcla(Color(0xFFE08A3A), Color(0xFF8A4A22), 0.2f + 0.6f * azar(981, j))
        drawOval(a.luz(col, cerca, frio), Offset(q.x - rr, q.y - rr * 0.7f), Size(rr * 2f, rr * 1.4f))
        drawOval(a.luz(mezcla(col, Color(0xFF5A2A14), 0.5f), cerca, frio), Offset(q.x - rr * 0.35f, q.y - rr * 0.2f), Size(rr * 0.7f, rr * 0.45f))
        drawArc(a.claro(0.5f), 200f, 90f, false, Offset(q.x - rr * 0.8f, q.y - rr * 0.6f), Size(rr * 1.6f, rr * 1.1f), style = Stroke(0.6f * u))
    }
    listOf(p0, p1).forEach { p ->
        drawOval(a.sombra(0.35f), Offset(p.x - 2f * u, p.y - 0.6f * u), Size(6f * u, 4.4f * u))
        drawCircle(a.luz(ALAMO, cerca, frio), 2.6f * u, p)
        drawCircle(a.luz(ALAMO_OSC, cerca, frio), 1.2f * u, p)
    }
}

/** Las cuentas del camino rayadas con carbón en la cal: palotes en grupos, uno por jornada, y algunos tachados. */
internal fun DrawScope.cuentasSeda(o: Offset, alto: Float, a: AireSeda) {
    val u = density
    val carbon = (if (a.oscuro) mezcla(SedaTinta.TINTA, a.hondo, 0.3f) else SedaTinta.TINTA).copy(alpha = 0.3f)
    var x = o.x
    for (fila in 0 until 2) {
        x = o.x
        val y = o.y + fila * alto * 1.5f
        val grupos = if (fila == 0) 4 else 2
        for (g in 0 until grupos) {
            val palos = if (fila == 1 && g == grupos - 1) 3 else 5
            for (j in 0 until palos) {
                val dx = (azar(990 + fila, g * 7 + j) - 0.5f) * 0.8f * u
                drawLine(carbon, Offset(x + dx, y), Offset(x + dx + 0.6f * u, y + alto * (0.9f + 0.2f * azar(991, g * 7 + j))), 0.7f * u, StrokeCap.Round)
                x += alto * 0.32f
            }
            x += alto * 0.5f
        }
    }
}
