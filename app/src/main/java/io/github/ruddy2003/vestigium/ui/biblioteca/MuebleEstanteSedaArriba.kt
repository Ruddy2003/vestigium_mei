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
import io.github.ruddy2003.vestigium.ui.ideario.mancha
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
 * La lámpara de barro de un pico, sin vidriar, sobre una ménsula de palo clavada en el adobe: dos estacas de álamo con su
 * tablilla encima, la mancha de aceite en la tablilla, la lámpara con el pico hacia la izquierda (donde arde la mecha, en
 * [llama]) y su asita atrás. De día, apagada; la llama la pone [llamaSeda].
 */
internal fun DrawScope.lamparaBarroSeda(llama: Offset, w: Float, a: AireSeda) {
    val u = density
    val k = if (a.lumbre) 0.95f else 0.08f
    val f = if (a.oscuro) frioSeda(llama.x / w - MuebleEstanteSeda.REJA_X, llama.y / w - MuebleEstanteSeda.REJA_Y) else 0f
    val tabla = Rect(llama.x - w * 0.018f, llama.y + w * 0.036f, llama.x + w * 0.11f, llama.y + w * 0.05f)
    val corr = if (a.oscuro) Offset(0f, w * 0.006f) else Offset(w * 0.014f, w * 0.012f)
    // Las dos estacas, metidas en el muro, que asoman por debajo de la tablilla; y la sombra de todo en la cal.
    val estacas = listOf(tabla.left + tabla.width * 0.2f, tabla.left + tabla.width * 0.78f)
    estacas.forEach { x ->
        drawRect(a.sombra(0.3f), Offset(x - 1.6f * u + corr.x, tabla.bottom + corr.y), Size(4f * u, 5f * u))
    }
    drawRect(a.sombra(if (a.oscuro) 0.3f else 0.22f), tabla.topLeft + corr, tabla.size)
    estacas.forEach { x ->
        drawRoundRect(a.luz(SedaTinta.ALAMO_OSC, k, f), Offset(x - 2f * u, tabla.bottom - 1f * u), Size(4f * u, 5f * u), CornerRadius(1.6f * u))
        drawCircle(a.luz(mezcla(SedaTinta.ALAMO, SedaTinta.ALAMO_OSC, 0.4f), k, f), 1.8f * u, Offset(x, tabla.bottom + 3.6f * u))
    }
    drawRect(a.luz(mezcla(SedaTinta.ALAMO, LUZ_MUEBLE, 0.2f), k, f), tabla.topLeft, Size(tabla.width, tabla.height * 0.35f))
    drawRect(a.luz(SedaTinta.ALAMO, k, f), Offset(tabla.left, tabla.top + tabla.height * 0.35f), Size(tabla.width, tabla.height * 0.65f))
    drawLine(a.sombra(0.4f), Offset(tabla.left, tabla.bottom), Offset(tabla.right, tabla.bottom), 0.8f * u)
    // El aceite que goteó por el canto de la tablilla.
    drawRoundRect(a.luz(mezcla(SedaTinta.ALAMO_OSC, SedaTinta.HOLLIN, 0.4f), k, f).copy(alpha = 0.6f), Offset(llama.x + w * 0.004f, tabla.top + tabla.height * 0.35f), Size(2.4f * u, tabla.height * 0.65f + 3f * u), CornerRadius(1.2f * u))
    // La lámpara: el cuenco bajo, el pico pellizcado hacia la izquierda y la asita atrás.
    val c = Offset(llama.x + w * 0.056f, tabla.top - w * 0.015f)
    val an = w * 0.084f; val al = w * 0.03f
    val barro = a.luz(SedaTinta.ARCILLA, k, f)
    val pico = Path().apply {
        moveTo(c.x - an * 0.3f, c.y - al * 0.4f)
        cubicTo(c.x - an * 0.45f, c.y - al * 0.45f, llama.x + an * 0.12f, llama.y + al * 0.7f, llama.x - an * 0.02f, llama.y + al * 0.3f)
        lineTo(llama.x + an * 0.05f, llama.y + al * 0.12f)
        cubicTo(llama.x + an * 0.2f, llama.y + al * 1.2f, c.x - an * 0.38f, c.y + al * 0.35f, c.x - an * 0.26f, c.y + al * 0.4f)
        close()
    }
    drawPath(corridoSeda(pico, corr.x * 0.6f, corr.y * 0.6f), a.sombra(0.2f))
    drawPath(pico, barro)
    drawPath(bordeSeda(pico, 0f, -al * 0.2f), a.sombra(0.3f))
    val cuerpo = Path().apply { addOval(Rect(c.x - an / 2, c.y - al / 2, c.x + an / 2, c.y + al / 2)) }
    drawPath(corridoSeda(cuerpo, corr.x * 0.6f, corr.y * 0.6f), a.sombra(0.25f))
    drawPath(cuerpo, barro)
    redondoSeda(cuerpo, a, k, -1f)
    // La boca del cuenco, con el aceite oscuro, y el labio a la luz.
    drawOval(a.luz(mezcla(SedaTinta.ARCILLA, SedaTinta.HOLLIN, 0.55f), k, f), Offset(c.x - an * 0.36f, c.y - al * 0.5f), Size(an * 0.72f, al * 0.42f))
    drawArc(a.claro(0.5f), 180f, 110f, false, Offset(c.x - an * 0.4f, c.y - al * 0.55f), Size(an * 0.8f, al * 0.5f), style = Stroke(0.8f * u))
    // La asita, atrás.
    drawArc(barro, -80f, 180f, false, Offset(c.x + an * 0.38f, c.y - al * 0.45f), Size(an * 0.22f, al * 0.8f), style = Stroke(1.6f * u))
    // El hollín del pico y la mecha.
    drawOval(a.luz(SedaTinta.HOLLIN, k, f).copy(alpha = 0.6f), Offset(llama.x - an * 0.04f, llama.y - al * 0.1f), Size(an * 0.18f, al * 0.5f))
    drawLine(a.luz(SedaTinta.TINTA, k, f), llama + Offset(0f, al * 0.2f), llama - Offset(an * 0.01f, al * 0.3f), 1.3f * u, StrokeCap.Round)
    if (a.oscuro) drawArc(FILO_MUEBLE.copy(alpha = 0.7f), 180f, 140f, false, Offset(c.x - an * 0.4f, c.y - al * 0.55f), Size(an * 0.8f, al * 0.5f), style = Stroke(1f * u))
}

/** El hollín de tantas noches: sube de la mecha hasta el techo, abriéndose, en la cal. */
internal fun DrawScope.hollinSeda(llama: Offset, w: Float, a: AireSeda) {
    val techo = w * 0.055f
    val alto = llama.y - techo
    if (alto <= 0f) return
    val col = if (a.oscuro) mezcla(SedaTinta.HOLLIN, a.hondo, 0.3f) else SedaTinta.HOLLIN
    // El tizne: un cerco blando que sube de la mecha y se abre, más cargado contra el techo, donde el humo se detiene (tres
    // manchas, sin bordes).
    val f = if (a.oscuro) 1.2f else 1f
    mancha(Offset(llama.x + w * 0.004f, llama.y - alto * 0.22f), alto * 0.24f, col.copy(alpha = 0.3f * f))
    mancha(Offset(llama.x + w * 0.012f, llama.y - alto * 0.55f), alto * 0.32f, col.copy(alpha = 0.26f * f))
    mancha(Offset(llama.x + w * 0.02f, techo + alto * 0.08f), alto * 0.42f, col.copy(alpha = 0.32f * f))
}

/** La llama de la lámpara, con su color propio. */
internal fun DrawScope.llamaSeda(p: Offset, w: Float, a: AireSeda) {
    val s = w * 0.027f
    val q = p - Offset(0f, w * 0.004f)
    drawCircle(SedaTinta.LLAMA.copy(alpha = 0.16f), s * 1.1f, q - Offset(0f, s * 0.5f))
    drawCircle(SedaTinta.LLAMA.copy(alpha = 0.22f), s * 0.66f, q - Offset(0f, s * 0.5f))
    fun lengua(k: Float, ladeo: Float) = Path().apply {
        moveTo(q.x - s * 0.17f * k, q.y)
        cubicTo(q.x - s * 0.3f * k, q.y - s * 0.4f * k, q.x + ladeo * s - s * 0.05f * k, q.y - s * 0.7f * k, q.x + ladeo * s, q.y - s * 1.15f * k)
        cubicTo(q.x + ladeo * s + s * 0.12f * k, q.y - s * 0.6f * k, q.x + s * 0.3f * k, q.y - s * 0.35f * k, q.x + s * 0.17f * k, q.y)
        close()
    }
    drawPath(lengua(1f, -0.06f), Color(0xFFFF9A2E))
    drawPath(lengua(0.72f, -0.04f), SedaTinta.LLAMA)
    drawPath(lengua(0.4f, 0f), Color(0xFFFFF6DC))
}

/**
 * Una tela echada sobre la vara, de [xa] a [xb]: cae en pocos pliegues anchos y desiguales (las crestas en [crestas], en
 * fracciones del ancho), con una punta más larga que la otra ([largoIzq], [largoDer]); con [doblez], el canto de la derecha
 * se dobla hacia delante en un tramo y enseña el revés. El brillo de la seda sigue cada pliegue.
 */
private fun DrawScope.telaSeda(xa: Float, xb: Float, yVara: (Float) -> Float, largoIzq: Float, largoDer: Float, crestas: FloatArray, color: Color,
                               orla: Color, doblez: Boolean, semilla: Int, a: AireSeda, cerca: Float, frio: Float, corr: Offset, brillo: Float) {
    val u = density; val w = size.width
    val an = xb - xa
    val abreI = an * 0.12f; val abreD = an * 0.2f
    val ya = yVara(xa); val yb = yVara(xb)
    val puntaI = Offset(xa - abreI, ya + largoIzq); val puntaD = Offset(xb + abreD, yb + largoDer)
    // El dobladillo: de la punta derecha a la izquierda, más bajo en cada cresta y más alto en cada valle.
    val bajo = mutableListOf(puntaD)
    for (j in crestas.indices.reversed()) {
        val f = crestas[j]
        val x = puntaI.x + (puntaD.x - puntaI.x) * f
        val y = puntaI.y + (puntaD.y - puntaI.y) * f
        bajo.add(Offset(x, y + an * (0.07f + 0.04f * azar(semilla, j))))
        if (j > 0) {
            val fv = (crestas[j] + crestas[j - 1]) / 2
            bajo.add(Offset(puntaI.x + (puntaD.x - puntaI.x) * fv, puntaI.y + (puntaD.y - puntaI.y) * fv - an * 0.02f))
        }
    }
    bajo.add(puntaI)
    val forma = Path().apply {
        moveTo(xa, ya - 1.2f * u)
        lineTo(xb, yb - 1.2f * u)
        cubicTo(xb + abreD * 0.2f, yb + largoDer * 0.35f, xb + abreD * 0.8f, yb + largoDer * 0.7f, puntaD.x, puntaD.y)
        suaveSeda(this, bajo)
        cubicTo(xa - abreI * 0.9f, ya + largoIzq * 0.7f, xa - abreI * 0.2f, ya + largoIzq * 0.35f, xa, ya - 1.2f * u)
        close()
    }
    drawPath(corridoSeda(forma, corr.x, corr.y), a.sombra(if (a.oscuro) 0.3f else 0.16f))
    val seda = a.luz(color, cerca, frio)
    val honda = mezcla(seda, a.hondo, 0.3f)
    drawPath(forma, seda)
    clipPath(forma) {
        // La luz de la izquierda y la sombra de la derecha.
        drawRect(a.claro(0.18f), Offset(xa - abreI, ya), Size(an * 0.3f, largoIzq + an))
        drawRect(a.sombra(0.14f), Offset(xb - an * 0.15f, yb), Size(an * 0.5f, largoDer + an))
        // Los valles entre cresta y cresta: estrechos arriba, donde la tela se junta en la vara, y anchos abajo.
        for (j in 0 until crestas.size - 1) {
            val fv = (crestas[j] + crestas[j + 1]) / 2
            val xt = xa + an * (0.12f + 0.76f * fv)
            val xh = puntaI.x + (puntaD.x - puntaI.x) * fv
            val yh = puntaI.y + (puntaD.y - puntaI.y) * fv
            val g = an * (crestas[j + 1] - crestas[j]) * 0.42f
            drawPath(poligonoSeda(xt - 0.6f * u, yVara(xt) + 2f * u, xt + 0.6f * u, yVara(xt) + 2f * u, xh + g * 0.6f, yh + an * 0.1f, xh - g, yh + an * 0.1f), a.sombra(0.26f))
            drawPath(poligonoSeda(xt - 0.3f * u, yVara(xt) + 2f * u, xt + 0.3f * u, yVara(xt) + 2f * u, xh + g * 0.2f, yh + an * 0.1f, xh - g * 0.4f, yh + an * 0.1f), a.sombra(0.18f))
        }
        // El brillo de la seda, que sigue cada cresta (una línea fina y una más ancha y tenue, combadas como el pliegue).
        crestas.forEachIndexed { j, f ->
            val xt = xa + an * (0.1f + 0.8f * f)
            val xh = puntaI.x + (puntaD.x - puntaI.x) * f
            val yh = puntaI.y + (puntaD.y - puntaI.y) * f + an * 0.05f
            val curva = Path().apply {
                moveTo(xt, yVara(xt) + 3f * u)
                quadraticTo((xt + xh) / 2 + an * (0.05f + 0.04f * azar(semilla, 30 + j)), (yVara(xt) + yh) / 2, xh, yh)
            }
            drawPath(curva, Color.White.copy(alpha = brillo * 0.4f), style = Stroke(4f * u))
            drawPath(curva, Color.White.copy(alpha = brillo), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
        // La orla tejida, que sigue el dobladillo un poco más arriba.
        val dobladillo = Path().apply { moveTo(puntaD.x, puntaD.y); suaveSeda(this, bajo) }
        drawPath(corridoSeda(dobladillo, 0f, -w * 0.02f), a.luz(orla, cerca, frio).copy(alpha = 0.85f), style = Stroke(w * 0.01f))
        // La sombra bajo la vara.
        drawRect(a.sombra(0.3f), Offset(xa - abreI, minOf(ya, yb) - 2f * u), Size(an + abreI + abreD, 4f * u))
    }
    // El canto doblado hacia delante en un tramo: se ve el revés, más mate, con su arista a la luz.
    if (doblez) {
        val y0 = yb + largoDer * 0.3f; val y1 = yb + largoDer * 0.72f
        val x0 = xb + abreD * 0.3f; val x1 = xb + abreD * 0.75f
        val pico = Offset(xb - an * 0.18f, (y0 + y1) / 2 + largoDer * 0.08f)
        val vuelta = poligonoSeda(x0, y0, pico.x, pico.y, x1, y1)
        drawPath(corridoSeda(vuelta, -1.4f * u, 1.6f * u), a.sombra(0.3f))
        drawPath(vuelta, mezcla(seda, if (a.oscuro) a.hondo else Color.White, 0.16f))
        drawLine(Color.White.copy(alpha = brillo), Offset(x0, y0), pico, 1f * u)
        drawLine(honda, pico, Offset(x1, y1), 0.8f * u)
    }
    // La tela que da la vuelta sobre la vara.
    drawRoundRect(mezcla(seda, if (a.oscuro) a.hondo else Color.White, 0.12f), Offset(xa, minOf(ya, yb) - 3.4f * u), Size(an, 4.2f * u), CornerRadius(2f * u))
    // Los flecos del dobladillo.
    val flecos = Path()
    for (i in 0 until bajo.size - 1) {
        val p0 = bajo[i]; val p1 = bajo[i + 1]
        val tramos = ((p0 - p1).getDistance() / (1.8f * u)).toInt().coerceIn(1, 40)
        for (k in 0 until tramos) {
            val q = p0 + (p1 - p0) * (k / tramos.toFloat())
            flecos.moveTo(q.x, q.y); flecos.lineTo(q.x + 0.4f * u, q.y + 3.4f * u)
        }
    }
    drawPath(flecos, honda, style = Stroke(0.6f * u))
    if (a.oscuro && cerca > 0.05f) drawPath(corridoSeda(forma, 0.8f * u, 0f), FILO_MUEBLE.copy(alpha = 0.3f * cerca), style = Stroke(0.8f * u))
}

/**
 * Las sedas que compra, echadas sobre una vara de álamo colgada del muro por dos estacas: un largo de seda azafrán, que cae
 * en tres pliegues anchos y desiguales con la punta de la izquierda más larga, y delante uno bermellón, más corto, con la
 * punta de la derecha caída y el canto doblado hacia delante. Las dos con su orla, sus flecos y su sombra en la cal.
 */
internal fun DrawScope.sedaColgadaSeda(p0: Offset, p1: Offset, w: Float, a: AireSeda, cerca: Float, frio: Float) {
    val u = density
    fun yVara(x: Float) = p0.y + (p1.y - p0.y) * ((x - p0.x) / (p1.x - p0.x))
    val corr = if (a.oscuro) Offset(-w * 0.014f, w * 0.01f) else Offset(w * 0.016f, w * 0.014f)
    val brillo = if (a.oscuro) 0.06f + 0.16f * cerca else if (a.noche) 0.2f else 0.3f
    drawLine(a.sombra(0.25f), p0 + corr, p1 + corr, 3f * u)
    // Las estacas y la vara.
    listOf(p0 + Offset(w * 0.012f, 0f), p1 - Offset(w * 0.012f, 0f)).forEach { e ->
        drawCircle(a.sombra(0.35f), 2.8f * u, e + Offset(1.4f * u, 1.6f * u))
        drawCircle(a.luz(SedaTinta.ALAMO_OSC, cerca, frio), 2.6f * u, e + Offset(0f, 1.4f * u))
    }
    drawLine(a.luz(mezcla(SedaTinta.ALAMO, SedaTinta.ALAMO_OSC, 0.25f), cerca, frio), p0, p1, 3.2f * u, StrokeCap.Round)
    drawLine(a.claro(0.4f), p0 - Offset(0f, 1f * u), p1 - Offset(0f, 1f * u), 0.7f * u)
    telaSeda(p0.x + w * 0.02f, p0.x + w * 0.135f, { x -> yVara(x) }, w * 0.25f, w * 0.18f, floatArrayOf(0.1f, 0.42f, 0.86f),
        SedaTinta.AZAFRAN, SedaTinta.ANIL, false, 1201, a, cerca, frio, corr, brillo)
    telaSeda(p0.x + w * 0.125f, p0.x + w * 0.215f, { x -> yVara(x) }, w * 0.12f, w * 0.2f, floatArrayOf(0.22f, 0.8f),
        SedaTinta.BERMELLON, SedaTinta.AZAFRAN, true, 1202, a, cerca, frio, corr, brillo)
}

/**
 * Las piezas de seda dobladas, apiladas en el vasar: añil, verde, grana y azafrán, cada una con su canto doblado redondo, el
 * brillo de la seda a lo largo y las capas del doblez en la punta; la de arriba deja caer una esquina por delante.
 */
internal fun DrawScope.sedasDobladasSeda(pie: Offset, w: Float, a: AireSeda, cerca: Float, frio: Float, haciaX: Float) {
    val u = density
    val g = w * 0.023f
    val an = w * 0.2f
    val colores = listOf(SedaTinta.ANIL, SedaTinta.VERDE, mezcla(SedaTinta.BERMELLON, SedaTinta.FIELTRO, 0.4f), SedaTinta.AZAFRAN)
    val brillo = if (a.oscuro) 0.06f + 0.18f * cerca else if (a.noche) 0.2f else 0.32f
    val corr = if (a.oscuro) Offset(-w * 0.03f, w * 0.004f) else Offset(w * 0.03f, w * 0.004f)
    val alto = g * colores.size
    drawRect(a.sombra(if (a.oscuro) 0.3f else 0.18f), Offset(pie.x - an / 2 + corr.x, pie.y - alto + corr.y), Size(an, alto))
    contactoSeda(Offset(pie.x + 3f * u, pie.y), an * 1.1f, w * 0.012f, a, 0.34f)
    colores.forEachIndexed { j, col ->
        val dx = (azar(1210, j) - 0.5f) * w * 0.014f
        val r = Rect(pie.x - an / 2 + dx, pie.y - g * (j + 1), pie.x + an / 2 + dx - w * 0.006f * azar(1211, j), pie.y - g * j)
        val seda = a.luz(col, cerca, frio)
        drawRoundRect(seda, r.topLeft, r.size, CornerRadius(g * 0.45f))
        // El brillo a lo largo del canto doblado y la sombra entre capa y capa.
        drawRect(Color.White.copy(alpha = brillo), Offset(r.left + g * 0.5f, r.top + g * 0.28f), Size(r.width - g, g * 0.14f))
        drawRect(a.sombra(0.28f), Offset(r.left + g * 0.3f, r.bottom - g * 0.2f), Size(r.width - g * 0.6f, g * 0.2f))
        // Las capas del doblez en la punta de la izquierda, y el lado de la sombra.
        for (i in 1..3) drawArc(mezcla(seda, a.hondo, 0.35f), 90f, 180f, false, Offset(r.left + i * 1.6f * u, r.top + g * 0.12f), Size(g * 0.8f, g * 0.76f), style = Stroke(0.6f * u))
        drawRect(a.sombra(0.16f), Offset(r.right - r.width * 0.16f, r.top + 1f * u), Size(r.width * 0.16f - g * 0.3f, g - 2f * u))
        if (a.oscuro && cerca > 0.05f) drawRect(FILO_MUEBLE.copy(alpha = 0.4f * cerca), Offset(r.right - g * 0.5f, r.top + g * 0.2f), Size(1.2f * u, g * 0.6f))
    }
    // La cara de arriba de la pila, vista apenas, y la esquina de la de arriba que cae por delante en un pliegue blando.
    val arriba = pie.y - alto
    val col = a.luz(colores.last(), cerca, frio)
    drawRoundRect(mezcla(col, Color.White, if (a.oscuro) 0.04f else 0.14f), Offset(pie.x - an / 2 + w * 0.004f, arriba - g * 0.3f), Size(an - w * 0.01f, g * 0.42f), CornerRadius(g * 0.2f))
    val caida = Path().apply {
        val x0 = pie.x - an * 0.36f
        moveTo(x0, arriba)
        lineTo(x0 + an * 0.24f, arriba)
        cubicTo(x0 + an * 0.25f, arriba + g * 1.2f, x0 + an * 0.2f, arriba + g * 2.2f, x0 + an * 0.14f, arriba + g * 2.6f)
        lineTo(x0 + an * 0.02f, arriba + g * 2.2f)
        cubicTo(x0 + an * 0.04f, arriba + g * 1.4f, x0 - an * 0.01f, arriba + g * 0.6f, x0, arriba)
        close()
    }
    drawPath(corridoSeda(caida, 1.4f * u, 1.6f * u), a.sombra(0.3f))
    drawPath(caida, col)
    clipPath(caida) {
        val x0 = pie.x - an * 0.36f
        drawPath(poligonoSeda(x0 + an * 0.1f, arriba, x0 + an * 0.14f, arriba, x0 + an * 0.11f, arriba + g * 2.6f, x0 + an * 0.07f, arriba + g * 2.6f), Color.White.copy(alpha = brillo))
        drawPath(poligonoSeda(x0 + an * 0.17f, arriba, x0 + an * 0.25f, arriba, x0 + an * 0.2f, arriba + g * 2.6f, x0 + an * 0.13f, arriba + g * 2.6f), a.sombra(0.22f))
    }
    // La orla de añil en el borde de la esquina.
    drawLine(a.luz(SedaTinta.ANIL, cerca, frio), Offset(pie.x - an * 0.36f + an * 0.02f, arriba + g * 2.2f), Offset(pie.x - an * 0.36f + an * 0.14f, arriba + g * 2.6f), 1.6f * u, StrokeCap.Round)
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
