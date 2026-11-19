package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sqrt

// El muro del almacén de la Ruta de la Seda (MuebleEstanteSeda): la cal, el techo y la luz de la reja.

// ---------------------------------------------------------------- el muro

/**
 * El muro de adobe encalado: la cal pasada a brocha (los brochazos se ven), el barro que se transparenta donde la cal es
 * delgada, algún desconchón chico con su paja, grietas finas, el zócalo de barro abajo y, arriba, las vigas de álamo del
 * techo con su estera de caña. Donde no da el sol, la cal es fría: una penumbra lila.
 */
internal fun DrawScope.muroSeda(a: AireSeda, ch: Float, fondo: Float, suelo: Float, x0: Float, x1: Float) {
    val u = density; val w = size.width
    val lejos = 0.1f
    val cal = a.luz(SedaTinta.CAL, lejos, 0.1f)
    // De día la cal es fría donde no da el sol: el color de fondo ya lleva esa penumbra lila.
    drawRect(if (a.oscuro) cal else mezcla(cal, SOMBRA_MUEBLE, if (a.noche) 0.14f else 0.11f), Offset.Zero, Size(w, suelo))
    // La textura solo donde el muro se ve: arriba, las dos jambas y el pie (lo de en medio lo tapa la alacena).
    val zonas = listOf(Rect(0f, 0f, w, ch), Rect(0f, ch, x0, fondo), Rect(x1, ch, w, fondo), Rect(0f, fondo, w, suelo))
    zonas.forEachIndexed { i, z ->
        if (z.width < 1f || z.height < 1f) return@forEachIndexed
        val veces = (z.width * z.height / (w * w * 0.5f)).coerceIn(0.15f, 12f)
        fun n(base: Int) = (base * veces).toInt().coerceAtLeast(2)
        // Donde la cal es delgada se transparenta el barro: manchas tibias, sin borde.
        pinceladas(z, a.luz(SedaTinta.ADOBE, lejos), 703 + 11 * i, n(24), u, 0.09f)
        pinceladas(z, a.luz(mezcla(SedaTinta.CAL, SedaTinta.ADOBE, 0.5f), lejos), 705 + 11 * i, n(18), u, 0.12f)
        // Los brochazos de la cal: arcos largos, unos más claros y otros más fríos.
        brochazosSeda(z, a.luz(mezcla(SedaTinta.CAL, LUZ_MUEBLE, 0.7f), lejos), 702 + 11 * i, n(26), 0.3f)
        brochazosSeda(z, a.luz(mezcla(SedaTinta.CAL, SedaTinta.ADOBE, 0.45f), lejos), 701 + 11 * i, n(20), 0.16f)
    }
    // Hacia la derecha la cal se va quedando en penumbra (solo donde se ve: arriba, la jamba derecha y el pie).
    if (!a.oscuro) listOf(Rect(w * 0.55f, 0f, w, ch), Rect(x1, ch, w, fondo), Rect(w * 0.55f, fondo, w, suelo)).forEach { z ->
        if (z.width > 0f && z.height > 0f) {
            drawRect(sombraMueble(0.05f), z.topLeft, z.size)
            drawRect(sombraMueble(0.05f), Offset(maxOf(z.left, w * 0.8f), z.top), Size(z.right - maxOf(z.left, w * 0.8f), z.height))
        }
    }
    // El zócalo: la mano de barro que protege el pie del muro, con su orilla irregular.
    val zoc = suelo - w * 0.1f
    val orilla = Path().apply {
        moveTo(0f, suelo)
        lineTo(0f, zoc)
        var x = 0f; var j = 0
        while (x < w) { x += w * 0.04f; j++; lineTo(minOf(x, w), zoc + (azar(706, j) - 0.5f) * 3.2f * u) }
        lineTo(w, suelo); close()
    }
    drawPath(orilla, a.luz(mezcla(SedaTinta.BARRO, SedaTinta.ADOBE, 0.45f), lejos))
    pinceladas(Rect(0f, zoc, w, suelo), a.luz(mezcla(SedaTinta.BARRO, SOMBRA_MUEBLE, 0.3f), lejos), 707, 26, u, 0.14f)
    pinceladas(Rect(0f, zoc, w, suelo), a.luz(SedaTinta.PAJA, lejos), 708, 16, u, 0.1f)
    drawPath(bordeSeda(orilla, 0f, 1.2f * u), a.luz(mezcla(SedaTinta.CAL, SedaTinta.ADOBE, 0.3f), lejos).copy(alpha = 0.6f))
    // Las salpicaduras de cal que cayeron sobre el barro.
    for (k in 0 until 14) {
        val o = Offset(w * azar(709, k), zoc + (suelo - zoc) * (0.15f + 0.6f * azar(710, k)))
        drawCircle(cal.copy(alpha = 0.45f), (0.6f + 1.1f * azar(711, k)) * u, o)
    }
    // Los desconchones, chicos: la cal saltó y se ve el adobe con su paja.
    val sitios = listOf(Offset(w * 0.37f, w * 0.105f) to 0.022f, Offset(w * 0.04f, ch + w * 0.45f) to 0.016f,
        Offset(w * 0.96f, ch + w * 1.1f) to 0.015f, Offset(w * 0.3f, zoc - w * 0.02f) to 0.022f)
    sitios.forEachIndexed { i, (o, t) ->
        val seVe = o.y < ch || o.x < x0 || o.x > x1 || o.y > fondo
        if (seVe && o.y < zoc - w * 0.01f) desconchonSeda(o, w * t, 720 + i, a, lejos)
    }
    // Grietas finas que bajan del techo y de las esquinas del vano.
    grietaSeda(Offset(w * 0.66f, w * 0.055f), w * 0.12f, 731, a)
    grietaSeda(Offset(x1 + w * 0.025f, ch + 2f * u), w * 0.16f, 732, a)
    grietaSeda(Offset(w * 0.018f, w * 0.48f), w * 0.07f, 733, a)
    // El techo: la estera de caña en sombra y las vigas de álamo que la sostienen.
    techoSeda(a)
}

/** Los brochazos de la cal: arcos largos y tenues del [color], como los deja la brocha al pasar. */
internal fun DrawScope.brochazosSeda(r: Rect, color: Color, semilla: Int, cuantos: Int, alfa: Float) {
    val u = density
    clipRect(r.left, r.top, r.right, r.bottom) {
        for (k in 0 until cuantos) {
            val rx = (18f + 34f * azar(semilla, 4 * k)) * u
            val ry = rx * (0.3f + 0.4f * azar(semilla, 4 * k + 1))
            val c = Offset(r.left + r.width * azar(semilla, 4 * k + 2), r.top + r.height * azar(semilla + 9, k))
            val desde = 190f + 120f * azar(semilla, 4 * k + 3)
            drawArc(color.copy(alpha = alfa * (0.4f + 0.6f * azar(semilla + 5, k))), desde, 40f + 50f * azar(semilla + 6, k), false,
                Offset(c.x - rx, c.y - ry), Size(2 * rx, 2 * ry), style = Stroke((1.2f + 2.6f * azar(semilla + 7, k)) * u, cap = StrokeCap.Round))
        }
    }
}

/**
 * Un desconchón de la cal: la cal se va adelgazando hacia el borde (un cerco donde se transparenta el barro), y donde saltó
 * asoma el adobe con su paja y sus hoyitos; el canto de la cal deja una sombra fina dentro, del lado de la luz, y alrededor
 * quedan migas: cal suelta sobre el barro y barro sobre la cal.
 */
internal fun DrawScope.desconchonSeda(o: Offset, s: Float, semilla: Int, a: AireSeda, lejos: Float) {
    val u = density
    fun contorno(escala: Float, sem: Int): Path {
        val q = List(10) { k ->
            val ang = 6.283f * k / 10
            val rr = s * escala * (0.66f + 0.46f * azar(sem, k))
            Offset(o.x + rr * kotlin.math.cos(ang) * 1.8f, o.y + rr * kotlin.math.sin(ang) * 0.72f)
        }
        return Path().apply { moveTo(q[0].x, q[0].y); suaveSeda(this, q + q[0]); close() }
    }
    // La cal adelgazada alrededor: dos cercos, cada vez más del color del barro.
    drawPath(contorno(1.7f, semilla + 1), a.luz(mezcla(SedaTinta.CAL, SedaTinta.ADOBE, 0.25f), lejos).copy(alpha = 0.55f))
    drawPath(contorno(1.3f, semilla + 2), a.luz(mezcla(SedaTinta.CAL, SedaTinta.ADOBE, 0.5f), lejos).copy(alpha = 0.6f))
    val p = contorno(1f, semilla)
    drawPath(p, a.luz(SedaTinta.ADOBE, lejos + 0.05f))
    clipPath(p) {
        for (k in 0 until 3) {
            val q = Offset(o.x + s * 2.2f * (azar(semilla + 7, k) - 0.5f), o.y + s * 1.1f * (azar(semilla + 8, k) - 0.5f))
            drawOval(a.luz(mezcla(SedaTinta.ADOBE, SedaTinta.BARRO, 0.7f), lejos), Offset(q.x - s * 0.12f, q.y - s * 0.08f), Size(s * 0.24f, s * 0.16f))
        }
        for (k in 0 until 7) {
            val q = Offset(o.x + s * 2.4f * (azar(semilla + 3, k) - 0.5f), o.y + s * 1.4f * (azar(semilla + 4, k) - 0.5f))
            drawLine(a.luz(SedaTinta.PAJA, lejos + 0.1f), q, q + Offset(s * (0.4f + 0.4f * azar(semilla + 5, k)), s * (azar(semilla + 6, k) - 0.5f) * 0.5f), 0.6f * u)
        }
    }
    // El canto de la cal: sombra fina dentro arriba y a la izquierda; abajo, la arista que toma la luz.
    drawPath(bordeSeda(p, 0.7f * u, 0.8f * u), a.sombra(0.32f))
    drawPath(bordeSeda(p, -0.5f * u, -0.6f * u), a.luz(mezcla(SedaTinta.CAL, LUZ_MUEBLE, 0.5f), lejos + 0.2f).copy(alpha = 0.7f))
    // Las migas: cal suelta y barro alrededor.
    for (k in 0 until 6) {
        val ang = 6.283f * azar(semilla + 11, k)
        val d = s * (1.1f + 0.9f * azar(semilla + 12, k))
        val q = Offset(o.x + d * kotlin.math.cos(ang) * 1.8f, o.y + d * kotlin.math.sin(ang) * 0.8f)
        val r = (0.5f + 0.6f * azar(semilla + 13, k)) * u
        drawCircle(a.luz(if (k % 2 == 0) SedaTinta.ADOBE else mezcla(SedaTinta.CAL, LUZ_MUEBLE, 0.4f), lejos), r, q)
    }
}

/** Una grieta fina que baja zigzagueando, con su labio de luz. */
internal fun DrawScope.grietaSeda(desde: Offset, largo: Float, semilla: Int, a: AireSeda) {
    val u = density
    val g = Path().apply { moveTo(desde.x, desde.y) }
    var p = desde
    for (k in 0 until 7) {
        p += Offset((azar(semilla, k) - 0.45f) * largo * 0.35f, largo / 7f)
        g.lineTo(p.x, p.y)
    }
    drawPath(g, a.sombra(0.28f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
    drawPath(corridoSeda(g, -0.8f * u, 0f), a.claro(0.3f), style = Stroke(0.5f * u))
}

/**
 * El techo, visto desde abajo: la estera de caña (las cañas corren a lo largo del muro) y las vigas de álamo, rollizos que
 * vienen hacia el que mira desde el muro donde apoyan; arriba, más cerca, se ven más anchas.
 */
internal fun DrawScope.techoSeda(a: AireSeda) {
    val u = density; val w = size.width
    val t = w * 0.052f
    val k = 0.05f
    drawRect(a.luz(mezcla(SedaTinta.PAJA, SedaTinta.BARRO, 0.45f), k), Offset.Zero, Size(w, t))
    var y = 1.2f * u; var j = 0
    while (y < t) {
        drawLine(a.sombra(0.2f + 0.12f * azar(740, j)), Offset(0f, y), Offset(w, y + (azar(741, j) - 0.5f) * 1f * u), 0.8f * u)
        drawLine(a.claro(0.2f), Offset(0f, y - 0.9f * u), Offset(w, y - 0.9f * u), 0.5f * u)
        y += 2.1f * u; j++
    }
    // Lo de arriba queda más lejos de la ventana: más hondo.
    drawRect(a.sombra(if (a.oscuro) 0.4f else 0.24f), Offset.Zero, Size(w, t))
    drawRect(a.sombra(0.14f), Offset.Zero, Size(w, t * 0.45f))
    val cx = w * 0.5f
    val paso = w * 0.2f
    var x = w * 0.06f; var i = 0
    while (x < w + paso * 0.5f) {
        val bw = w * 0.042f * (0.9f + 0.22f * azar(742, i))
        val xt = cx + (x - cx) * 1.2f; val bt = bw * 1.35f
        val viga = poligonoSeda(x - bw / 2, t, x + bw / 2, t, xt + bt / 2, 0f, xt - bt / 2, 0f)
        drawPath(corridoSeda(viga, 2.5f * u, 0f), a.sombra(0.3f))
        drawPath(viga, a.luz(mezcla(SedaTinta.ALAMO, SedaTinta.ALAMO_OSC, 0.3f), k))
        drawPath(poligonoSeda(x - bw / 2, t, x - bw * 0.15f, t, xt - bt * 0.15f, 0f, xt - bt / 2, 0f), a.claro(0.22f))
        drawPath(poligonoSeda(x + bw * 0.22f, t, x + bw / 2, t, xt + bt / 2, 0f, xt + bt * 0.22f, 0f), a.sombra(0.28f))
        drawLine(a.sombra(0.3f), Offset(x - bw * 0.05f, t), Offset(xt + bt * 0.02f, 0f), 0.6f * u)
        // El barro con que se asentó en el muro.
        drawOval(a.luz(mezcla(SedaTinta.ADOBE, SedaTinta.CAL, 0.25f), k + 0.05f), Offset(x - bw * 0.75f, t - 2.2f * u), Size(bw * 1.5f, 4.4f * u))
        x += paso; i++
    }
    // La línea del muro y la sombra del techo en la cal.
    drawRect(a.sombra(0.3f), Offset(0f, t + 1.4f * u), Size(w, 1.8f * u))
    drawRect(a.sombra(0.12f), Offset(0f, t + 3.2f * u), Size(w, 8f * u))
}

// ---------------------------------------------------------------- la luz de la reja

/**
 * La mancha que deja en la cal la reja de la ventana que queda a la izquierda, fuera del cuadro: un paralelogramo sesgado (los
 * cantos verticales son los de la ventana; los de arriba y abajo, inclinados) con el borde blando, cruzado por la sombra de
 * cinco palos de álamo desiguales, torcidos y a distancias distintas, y de un travesaño atado a ellos con cuerda. De día es el
 * sol, tibio y fuerte; en los Sueños en claro, el sol bajo del atardecer, más largo y anaranjado; de noche, la luna, fría; en
 * los Sueños de noche, la luna manda y baja hasta el canto del vasar.
 */
internal fun DrawScope.luzRejaSeda(a: AireSeda, apoyo: Float) {
    val w = size.width
    val incl = if (a.oscuro) 0.3f else if (a.noche) 0.14f else 0.38f
    val xa = w * 0.02f; val xb = w * (if (a.noche && !a.oscuro) 0.6f else 0.53f)
    val ya = w * (if (a.oscuro) (if (a.noche) 0.15f else 0.1f) else if (a.noche) 0.21f else 0.095f)
    val alto = w * (if (a.oscuro) (if (a.noche) 0.4f else 0.3f) else if (a.noche) 0.24f else 0.31f)
    val color = when {
        a.oscuro -> SedaTinta.LUNA.copy(alpha = if (a.noche) 0.3f else 0.18f)
        a.noche -> SedaTinta.OCASO.copy(alpha = 0.5f)
        else -> SedaTinta.SOL.copy(alpha = 0.78f)
    }
    fun yArriba(x: Float) = ya + (x - xa) * incl
    val mancha = Path().apply {
        moveTo(xa, yArriba(xa)); lineTo(xb, yArriba(xb)); lineTo(xb, yArriba(xb) + alto); lineTo(xa, yArriba(xa) + alto); close()
    }
    // Los palos: desiguales, a distancias distintas y torcidos (abajo no caen en la misma x que arriba).
    val ancho = xb - xa
    val n = 4
    // A distancias claramente distintas: dos palos juntos, un claro ancho, otro palo y el último claro.
    val donde = floatArrayOf(0.13f, 0.27f, 0.6f, 0.76f)
    val xs = FloatArray(n) { j -> xa + ancho * (donde[j] + (azar(1301, j) - 0.5f) * 0.03f) }
    val ladeo = FloatArray(n) { j -> (azar(1302, j) - 0.5f) * ancho * 0.09f }
    val grueso = FloatArray(n) { j -> ancho * (0.03f + 0.03f * azar(1303, j)) }
    // Donde cada palo cruza el travesaño se dobla un poco (son palos, no listones).
    val codo = FloatArray(n) { j -> (azar(1304, j) - 0.5f) * ancho * 0.05f }
    // El travesaño, atado a los palos y algo caído hacia la derecha.
    val tc = 0.4f; val caida = alto * 0.14f; val gt = ancho * 0.034f
    fun yCruce(x: Float) = yArriba(x) + alto * tc + caida * (x - xa) / ancho
    // Lo que se alumbra: entre palo y palo, arriba y abajo del travesaño (cuadriláteros, sin operaciones entre formas). Cada
    // columna en su trazo, para que la mancha se deshaga hacia la derecha.
    val columnas = List(n + 1) { Path() }
    for (j in 0..n) {
        val luz = columnas[j]
        val lTop = if (j == 0) xa else xs[j - 1] + grueso[j - 1] / 2
        val lBot = if (j == 0) xa else xs[j - 1] + ladeo[j - 1] + grueso[j - 1] / 2
        val rTop = if (j == n) xb else xs[j] - grueso[j] / 2
        val rBot = if (j == n) xb else xs[j] + ladeo[j] - grueso[j] / 2
        val lC = lTop + (lBot - lTop) * tc + (if (j == 0) 0f else codo[j - 1]); val rC = rTop + (rBot - rTop) * tc + (if (j == n) 0f else codo[j])
        luz.moveTo(lTop, yArriba(lTop)); luz.lineTo(rTop, yArriba(rTop)); luz.lineTo(rC, yCruce(rC) - gt / 2); luz.lineTo(lC, yCruce(lC) - gt / 2); luz.close()
        luz.moveTo(lC, yCruce(lC) + gt / 2); luz.lineTo(rC, yCruce(rC) + gt / 2); luz.lineTo(rBot, yArriba(rBot) + alto); luz.lineTo(lBot, yArriba(lBot) + alto); luz.close()
    }
    clipRect(0f, w * 0.055f, w, apoyo + w * 0.07f) {
        // El borde blando: la misma mancha corrida en varias direcciones, cada vez más tenue.
        drawPath(corridoSeda(mancha, -w * 0.014f, -w * 0.01f), color.copy(alpha = color.alpha * 0.1f))
        drawPath(corridoSeda(mancha, w * 0.014f, w * 0.016f), color.copy(alpha = color.alpha * 0.1f))
        columnas.forEachIndexed { j, luz ->
            val f = 1f - 0.62f * (j / n.toFloat())
            val blando = w * (0.004f + 0.008f * j / n)
            drawPath(corridoSeda(luz, -blando, -blando * 0.8f), color.copy(alpha = color.alpha * 0.28f * f))
            drawPath(corridoSeda(luz, blando, blando), color.copy(alpha = color.alpha * 0.28f * f))
            drawPath(luz, color.copy(alpha = color.alpha * 0.75f * f))
            // De día, el corazón de la mancha aún más claro.
            if (!a.oscuro && !a.noche) drawPath(luz, LUZ_MUEBLE.copy(alpha = 0.22f * f))
        }
    }
}
