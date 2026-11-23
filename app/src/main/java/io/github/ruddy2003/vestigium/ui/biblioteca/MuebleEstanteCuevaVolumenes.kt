package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Temas
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sin

/**
 * Las entradas del estante de Prehistoria, tres objetos de la cueva (la clase sale de la semilla, nunca del sitio):
 * - **el hueso de cuenta**: un hueso largo de pie, con sus cabezas, untado del pigmento del ánimo y con su fila de muescas, más
 *   cuantas más palabras; alto y delgado;
 * - **la bolsa de piel**: un zurrón blando y panzudo, curtido con el pigmento del ánimo, fruncido con su correa, con el pelo que
 *   asoma en la boca y su costura de tendón; alguna recostada;
 * - **la piel enrollada**: una piel curtida con el pigmento del ánimo, enrollada con el pelo hacia dentro (la espiral se ve
 *   arriba) y atada con dos tientos; más gruesa cuantas más palabras; un cilindro recto.
 * Cada una lleva su etiqueta de cuero crudo atada con un tiento, con el día y el mes corto pintados con carbón; en los Sueños, una
 * media luna pintada con caolín en el objeto.
 */

/** Los meses cortos en español, para la etiqueta (el nombre largo del tema ya lo dice el rótulo de la repisa). */
private val MESES_CORTOS_CUEVA = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

/** El mes corto de una entrada: se ubica el nombre del tema entre las lunas de la cueva; si no está, queda tal cual. */
private fun mesCortoCueva(mes: String): String {
    val i = Temas.MESES_CUEVA.indexOf(mes)
    return if (i in MESES_CORTOS_CUEVA.indices) MESES_CORTOS_CUEVA[i] else mes
}

internal fun DrawScope.volumenCueva(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val m = MuebleEstanteCueva
    val a = AireCueva(oscuro, v.noche)
    val fila = m.FILA / m.ANCHO_NOMINAL
    val x = m.LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * m.LADO)
    val y = m.CORONA + v.repisa * fila + (m.ALTO_REPISA.value / m.ANCHO_NOMINAL) * 0.55f
    val total = m.CORONA + v.repisas * fila + m.BASE
    val l = m.luz(a, x, y, total, v.repisas, 0.12f)
    // De dónde le llega la luz: de día y en los Sueños, de la boca (izquierda); de noche, del fuego más cercano.
    val hacia = if (a.oscuro && !a.noche) (if (x > 0.62f) 1f else -1f) else -1f
    val tono = tonoDeDiaCueva(v.tono, oscuro)
    when (m.clase(v.semilla)) {
        0 -> huesoCueva(v, tono, medidor, l, hacia)
        1 -> bolsaCueva(v, tono, medidor, l, hacia)
        else -> rolloCueva(v, tono, medidor, l, hacia)
    }
}

/** El pigmento del ánimo, o null si no hay ánimo (el material queda desnudo). */
private fun pigmento(t: Color?): Color? = t?.let { pigmentoDelAnimoCueva(it) }

/** La sombra que la entrada echa detrás, en la roca y en la vecina (a la derecha de día; de noche, lejos del fuego). */
private fun DrawScope.sombraDetras(forma: Path, l: LuzCueva, hacia: Float) {
    val u = density
    drawPath(corridoCueva(forma, -hacia * 6f * u, if (l.a.oscuro) -2f * u else 2.4f * u), l.s(if (l.a.oscuro) 0.5f else 0.4f))
}

/** La sombra de contacto en la cara del zarzo, a lo ancho de [x0] a [x1]. */
private fun DrawScope.contactoVol(x0: Float, x1: Float, l: LuzCueva, hacia: Float) {
    val u = density; val h = size.height
    drawOval(l.s(0.5f), Offset(x0 - hacia * 2f * u, h - 3f * u), Size(x1 - x0 + 3f * u, 5f * u))
}

// ---------------------------------------------------------------- el hueso de cuenta

private fun DrawScope.huesoCueva(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzCueva, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val bw = (w * (0.56f + 0.12f * azar(s, 3))).coerceIn(16f * u, 27f * u)
    val cx = w * 0.5f + (w - bw) * 0.3f * (azar(s, 4) - 0.5f)
    val giro = if (w > bw * 1.5f) (azar(s, 5) - 0.5f) * 8f else 0f
    val top = h * 0.01f
    val cab = bw
    val cana = bw * (0.4f + 0.06f * azar(s, 7))
    val curva = bw * 0.14f * (azar(s, 8) - 0.5f)
    val espejo = if (azar(s, 2) < 0.5f) 1f else -1f
    val pig = pigmento(tono)
    val etiquetaIzq = azar(s, 59) < 0.5f
    rotate(giro, Offset(cx, h)) {
        // Un fémur de pie sobre sus dos cóndilos: arriba la cabeza redonda en su cuello y, del otro lado, el trocánter; la caña
        // que se estrecha y se curva apenas; abajo los dos cóndilos con la garganta entre ellos.
        fun q(x: Float, y: Float) = Offset(cx + espejo * x, y)
        val f = cerradaCueva(listOf(
            q(-cab * 0.5f, top + cab * 0.3f), q(-cab * 0.44f, top + cab * 0.07f), q(-cab * 0.22f, top), q(-cab * 0.02f, top + cab * 0.1f),
            q(cab * 0.1f, top + cab * 0.26f), q(cab * 0.28f, top + cab * 0.05f), q(cab * 0.48f, top + cab * 0.14f), q(cab * 0.44f, top + cab * 0.5f),
            q(cana * 0.5f + curva, top + cab * 1.0f), q(cana * 0.46f + curva, h * 0.55f), q(cana * 0.56f, h - cab * 0.92f),
            q(cab * 0.5f, h - cab * 0.4f), q(cab * 0.42f, h - cab * 0.02f), q(cab * 0.14f, h - cab * 0.04f), q(0f, h - cab * 0.2f),
            q(-cab * 0.14f, h - cab * 0.04f), q(-cab * 0.42f, h - cab * 0.02f), q(-cab * 0.5f, h - cab * 0.4f),
            q(-cana * 0.56f, h - cab * 0.92f), q(-cana * 0.46f + curva, h * 0.55f), q(-cana * 0.5f + curva, top + cab * 0.95f), q(-cab * 0.26f, top + cab * 0.56f)))
        sombraDetras(f, l, hacia)
        contactoVol(cx - cab * 0.5f, cx + cab * 0.5f, l, hacia)
        // El hueso y el pigmento untado en la caña (lo que dice el ánimo; las cabezas quedan en hueso), en un solo relleno.
        val y0 = top + cab * (0.66f + 0.12f * azar(s, 9)); val y1 = h - cab * (0.78f + 0.14f * azar(s, 10))
        val hueso = l.c(P.HUESO)
        val color = if (pig != null) l.animo(pig) else l.c(mezcla(P.HUESO, P.HUESO_OSC, 0.4f))
        val t0 = ((y0 - top) / (h - top)).coerceIn(0f, 1f); val t1 = ((y1 - top) / (h - top)).coerceIn(t0, 1f)
        drawPath(f, Brush.verticalGradient(colorStops = arrayOf(0f to hueso, t0 to hueso, t0 to color, t1 to color, t1 to hueso, 1f to hueso), startY = top, endY = h))
        // El borde comido del untado: unas manchas del pigmento que se pasan, dentro de la caña.
        for (k in 0 until 3) {
            val dx = cana * (-0.3f + 0.3f * k)
            drawOval(color, Offset(cx + dx + curva * 0.3f - cana * 0.12f, y0 - cana * (0.1f + 0.22f * azar(s, 11 + k))), Size(cana * 0.24f, cana * 0.4f))
            drawOval(color, Offset(cx + dx - cana * 0.12f, y1 - cana * (0.3f - 0.22f * azar(s, 14 + k))), Size(cana * 0.24f, cana * 0.4f))
        }
        sombreadoCueva(f, l, hacia, 1.2f, 0.45f)
        // La garganta entre los cóndilos y el brillo de la cabeza; los poros del hueso.
        drawLine(l.s(0.5f), Offset(cx, h - cab * 0.2f), Offset(cx + curva * 0.2f, h - cab * 0.6f), 1.2f * u)
        drawCircle(l.b(0.5f), cab * 0.1f, q(-cab * 0.28f, top + cab * 0.16f))
        for (k in 0 until 4) drawCircle(l.c(P.HUESO_OSC).copy(alpha = 0.55f), (0.5f + 0.6f * azar(s, 20 + k)) * u, Offset(cx + cab * (azar(s, 30 + k) - 0.5f) * 0.6f, h - cab * (0.2f + 0.3f * azar(s, 40 + k))))
        // Las muescas: una fila en el canto de la caña (del lado contrario a la etiqueta), más cuantas más palabras.
        val y0m = top + cab * 1.0f; val y1m = h - cab * 1.0f
        if (y1m > y0m + 4f * u) {
            val n = (3 + (v.lleno * 15f).toInt()).coerceAtMost(18)
            val paso = (y1m - y0m) / n
            val cortes = Path(); val labios = Path()
            for (k in 0 until n) {
                val yy = y0m + paso * (k + 0.5f)
                val largo = cana * (0.38f + 0.16f * azar(s, 60 + k))
                val xl = if (!etiquetaIzq) cx - cana * 0.5f + curva * 0.5f else cx + cana * 0.5f + curva * 0.5f - largo
                cortes.moveTo(xl, yy); cortes.lineTo(xl + largo, yy + 0.7f * u)
                labios.moveTo(xl + 0.3f * u, yy + 1.1f * u); labios.lineTo(xl + largo * 0.9f, yy + 1.7f * u)
            }
            drawPath(cortes, l.c(P.CARBON).copy(alpha = 0.85f), style = Stroke(1.5f * u, cap = StrokeCap.Round))
            drawPath(labios, l.b(0.6f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        }
        if (v.noche) mediaLuna(Offset(cx - cana * 0.04f, h - cab * 1.5f), minOf(cana * 0.4f, 4.6f * u), l)
    }
    // La etiqueta, atada al cuello del hueso con un tiento y caída hacia un lado: el hueso se sigue leyendo.
    val lw = (w * 0.8f).coerceIn(minOf(w, 20f * u), 24f * u)
    val yt = top + cab * 0.78f
    val xc = if (etiquetaIzq) cx - bw * 0.34f else cx + bw * 0.34f
    val x0 = (xc - lw * 0.5f).coerceIn(-w * 0.15f, maxOf(-w * 0.15f, w - lw))
    etiquetaCueva(v, medidor, l, Rect(x0, yt + 3f * u, x0 + lw, yt + 30f * u), Offset(cx, yt), hacia)
}

// ---------------------------------------------------------------- la bolsa de piel

private fun DrawScope.bolsaCueva(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzCueva, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val giro = if (azar(s, 11) < 0.3f) (azar(s, 12) - 0.5f) * 14f else 0f
    val a = w * (0.53f + 0.04f * azar(s, 13))
    val cx = w * 0.5f
    val cuello = h * (0.22f + 0.05f * azar(s, 14))
    val pig = pigmento(tono)
    val nudoDer = azar(s, 15) < 0.5f
    rotate(giro, Offset(cx, h)) {
        // El zurrón: el fondo que se aplasta y se derrama sobre el zarzo, la panza que se hincha a un tercio del alto y el cuello
        // que se aprieta bajo el frunce.
        val panza = h - cuello
        val f = cerradaCueva(listOf(
            Offset(cx - a * 0.34f, cuello), Offset(cx - a * 0.62f, cuello + panza * 0.22f), Offset(cx - a * 0.96f, cuello + panza * 0.6f),
            Offset(cx - a * 1.02f, h - panza * 0.1f), Offset(cx - a * 0.7f, h), Offset(cx + a * 0.66f, h), Offset(cx + a * 1.03f, h - panza * 0.12f),
            Offset(cx + a * 0.94f, cuello + panza * 0.58f), Offset(cx + a * 0.6f, cuello + panza * 0.2f), Offset(cx + a * 0.32f, cuello)))
        sombraDetras(f, l, hacia)
        contactoVol(cx - a, cx + a, l, hacia)
        val base = if (pig != null) l.animo(mezcla(pig, P.PIEL, 0.1f)) else l.c(P.PIEL)
        bultoRapidoCueva(f, base, l, hacia, 1.2f, 0.38f)
        run {
            // Los pliegues que bajan del frunce, la costura de tendón a un lado, el roce más claro y la mancha de la grasa.
            val pliegues = Path()
            for (k in 0 until 5) {
                val dx = (k - 2) * a * 0.2f
                pliegues.moveTo(cx + dx * 0.3f, cuello + h * 0.01f)
                pliegues.quadraticTo(cx + dx * 1.2f, cuello + panza * 0.2f, cx + dx * 1.6f, cuello + panza * (0.32f + 0.12f * azar(s, 15 + k)))
            }
            drawPath(pliegues, l.s(0.34f), style = Stroke(1.5f * u, cap = StrokeCap.Round))
            drawPath(corridoCueva(pliegues, -1.2f * u, 0f), l.b(0.32f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            // El fondo aplastado: un pliegue horizontal donde la panza descansa.
            drawPath(abiertaCueva(listOf(Offset(cx - a * 0.9f, h - panza * 0.16f), Offset(cx, h - panza * 0.1f), Offset(cx + a * 0.9f, h - panza * 0.18f))), l.s(0.3f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            val lado = if (nudoDer) -1f else 1f
            val costura = abiertaCueva(listOf(Offset(cx + lado * a * 0.5f, cuello + panza * 0.12f), Offset(cx + lado * a * 0.8f, cuello + panza * 0.55f), Offset(cx + lado * a * 0.7f, h - 2f * u)))
            drawPath(costura, l.s(0.42f), style = Stroke(1.2f * u))
            drawPath(costura, l.c(P.CUERO), style = Stroke(1.5f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 2.4f * u))))
            drawOval(l.b(0.3f), Offset(cx - a * (if (hacia < 0f) 0.8f else 0.05f), cuello + panza * 0.3f), Size(a * 0.75f, panza * 0.36f))
            drawOval(l.s(0.16f), Offset(cx - a * 0.25f, h - panza * 0.36f), Size(a * 0.7f, panza * 0.2f))
        }
        // La boca fruncida, con el pelo de la piel que asoma hacia fuera, y la correa del frunce con su nudo y sus dos cabos.
        val boca = Path()
        for (k in 0 until 9) {
            val x = cx - a * 0.44f + a * 0.88f * k / 8f
            boca.moveTo(x, cuello + 1.4f * u)
            boca.lineTo(x + a * 0.08f * sin(k * 2.3f), cuello - h * (0.06f + 0.05f * azar(s, 30 + k)))
        }
        drawPath(boca, l.c(mezcla(base, P.PIEL_OSC, 0.3f)), style = Stroke(a * 0.15f, cap = StrokeCap.Round))
        val pelo = Path()
        for (k in 0 until 14) {
            val x = cx - a * 0.5f + a * 1.0f * k / 13f
            pelo.moveTo(x, cuello - h * 0.05f); pelo.lineTo(x + a * 0.12f * (k / 13f - 0.5f) + a * 0.04f * sin(k * 1.9f), cuello - h * (0.11f + 0.05f * azar(s, 50 + k)))
        }
        drawPath(pelo, l.c(P.PELO), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(pelo, -0.6f * u, 0f), l.b(0.32f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
        drawLine(l.s(0.45f), Offset(cx - a * 0.4f, cuello + 2.4f * u), Offset(cx + a * 0.42f, cuello + 3f * u), 2.8f * u)
        drawLine(l.c(P.CUERO_OSC), Offset(cx - a * 0.42f, cuello + 0.6f * u), Offset(cx + a * 0.42f, cuello + 1.2f * u), 2.8f * u, StrokeCap.Round)
        drawLine(l.b(0.45f), Offset(cx - a * 0.38f, cuello - 0.3f * u), Offset(cx, cuello + 0.2f * u), 0.8f * u)
        if (v.noche) mediaLuna(Offset(cx + a * 0.4f * hacia, cuello + panza * 0.62f), minOf(a * 0.22f, 5.2f * u), l)
    }
    // El nudo a un lado del cuello: de él cuelgan los dos cabos y la etiqueta.
    val nudo = Offset(cx + (if (nudoDer) a * 0.4f else -a * 0.4f), cuello + 1f * u)
    drawCircle(l.c(mezcla(P.CUERO_OSC, P.CUERO, 0.3f)), 2f * u, nudo)
    drawLine(l.c(P.CUERO_OSC), nudo, nudo + Offset(if (nudoDer) 2f * u else -2f * u, h * 0.16f), 1.4f * u, StrokeCap.Round)
    val lw = (w * 0.76f).coerceIn(minOf(w, 20f * u), 24f * u)
    val xc = cx + (if (nudoDer) a * 0.22f else -a * 0.22f)
    val x0 = (xc - lw * 0.5f).coerceIn(0f, maxOf(0f, w - lw))
    val yt = cuello + (h - cuello) * 0.22f
    etiquetaCueva(v, medidor, l, Rect(x0, yt, x0 + lw, yt + 27f * u), nudo, hacia)
}

// ---------------------------------------------------------------- la piel enrollada

private fun DrawScope.rolloCueva(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzCueva, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val cx = w * 0.5f
    // Más gruesa cuanto más escribiste: la piel enrollada da más vueltas.
    val a = w * (0.4f + 0.06f * v.lleno + 0.03f * azar(s, 250))
    val ry = minOf(a * 0.42f, 6.5f * u)
    val top = ry + 1.5f * u
    val pig = pigmento(tono)
    val giro = if (azar(s, 251) < 0.25f) (azar(s, 252) - 0.5f) * 8f else 0f
    rotate(giro, Offset(cx, h)) {
        // El cuerpo: un cilindro de piel, un poco más gordo en medio, con la base redonda.
        val panza = a * (0.04f + 0.06f * azar(s, 254))
        val f = Path().apply {
            moveTo(cx - a, top)
            cubicTo(cx - a - panza, top + (h - top) * 0.3f, cx - a + panza * 0.4f, top + (h - top) * 0.7f, cx - a - panza * 0.5f, h - ry)
            quadraticTo(cx, h + ry * 0.95f, cx + a + panza * 0.4f, h - ry)
            cubicTo(cx + a + panza * 0.2f, top + (h - top) * 0.7f, cx + a + panza, top + (h - top) * 0.3f, cx + a, top)
            close()
        }
        sombraDetras(f, l, hacia)
        contactoVol(cx - a, cx + a, l, hacia)
        val cuero = if (pig != null) l.animo(mezcla(pig, P.PIEL, 0.14f)) else l.c(mezcla(P.PIEL, P.CUERO, 0.3f))
        bultoRapidoCueva(f, cuero, l, hacia, 1.15f, 0.4f)
        run {
            // La carne raspada: pasadas del raspador, oblicuas.
            val raspa = Path()
            for (k in 0 until 9) {
                val x = cx - a * 0.65f + a * 1.1f * azar(s, 260 + k); val y = top + (h - top) * (0.05f + 0.85f * azar(s, 270 + k))
                raspa.moveTo(x, y); raspa.lineTo(x + a * 0.25f, y + a * 0.12f)
            }
            drawPath(raspa, l.c(P.PIEL_OSC).copy(alpha = 0.3f), style = Stroke(1f * u, cap = StrokeCap.Round))
            // El canto de la última vuelta, que baja por el frente ondulando, con su sombra y su filo.
            val xb = cx + a * (0.15f + 0.4f * (azar(s, 280) - 0.5f))
            val canto = abiertaCueva(List(6) { k -> Offset(xb + a * 0.12f * sin(k * 1.6f + s), top + (h - top) * k / 5f) })
            drawPath(corridoCueva(canto, a * 0.06f, 0f), l.s(0.45f), style = Stroke(2f * u))
            drawPath(canto, l.b(0.5f), style = Stroke(1f * u, cap = StrokeCap.Round))
        }
        // La boca de arriba: la espiral de la piel enrollada, con el pelo pardo hacia dentro, y unos pelos que asoman.
        val boca = Rect(cx - a, top - ry, cx + a, top + ry)
        drawOval(l.c(P.PELO), boca.topLeft, boca.size)
        drawOval(cuero, boca.topLeft, boca.size, style = Stroke(1.8f * u))
        drawArc(l.c(mezcla(P.PIEL, P.CUERO, 0.2f)), 180f, 300f, false, Offset(boca.left + boca.width * 0.17f, boca.top + boca.height * 0.17f), Size(boca.width * 0.66f, boca.height * 0.66f), style = Stroke(1.5f * u))
        drawArc(l.c(mezcla(P.PIEL, P.CUERO, 0.2f)), 0f, 270f, false, Offset(boca.left + boca.width * 0.34f, boca.top + boca.height * 0.34f), Size(boca.width * 0.32f, boca.height * 0.32f), style = Stroke(1.2f * u))
        val pelo = Path()
        for (k in 0 until 9) {
            val t = 3.4f + 2.6f * k / 8f
            val p = Offset(cx + a * 0.92f * kotlin.math.cos(t), top + ry * 0.92f * kotlin.math.sin(t))
            pelo.moveTo(p.x, p.y); pelo.lineTo(p.x + 1f * u * sin(k * 2f), p.y - 2.6f * u)
        }
        drawPath(pelo, l.c(P.PELO), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        // La atadura: un tiento de dos vueltas juntas, con su nudo y su cabo que cuelga.
        val yA = top + (h - top) * 0.34f
        listOf(0f, 3.2f * u).forEach { dy ->
            val atadura = Path().apply { moveTo(cx - a * 1.04f, yA + dy); quadraticTo(cx, yA + dy + ry * 0.9f, cx + a * 1.04f, yA + dy) }
            drawPath(corridoCueva(atadura, 0f, 1.4f * u), l.s(0.42f), style = Stroke(2.4f * u))
            drawPath(atadura, l.c(P.CUERO_OSC), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            drawPath(corridoCueva(atadura, 0f, -0.6f * u), l.b(0.45f), style = Stroke(0.6f * u))
        }
        if (v.noche) mediaLuna(Offset(cx - a * 0.35f * hacia, top + (h - top) * 0.86f), minOf(a * 0.3f, 5f * u), l)
    }
    // La etiqueta cuelga del nudo de la atadura.
    val yA = top + (h - top) * 0.34f
    val nudo = Offset(cx + a * 0.45f * (if (azar(s, 253) < 0.5f) -1f else 1f), yA + ry * 0.7f)
    drawCircle(l.c(mezcla(P.CUERO_OSC, P.CUERO, 0.3f)), 2f * u, nudo)
    val lw = (w * 0.74f).coerceIn(minOf(w, 20f * u), 24f * u)
    val x0 = (nudo.x - lw * 0.5f).coerceIn(0f, maxOf(0f, w - lw))
    etiquetaCueva(v, medidor, l, Rect(x0, nudo.y + 4f * u, x0 + lw, nudo.y + 31f * u), nudo, hacia)
}

// ---------------------------------------------------------------- la etiqueta y la luna

/**
 * La etiqueta de cuero crudo: un trozo cortado con sílex, de valor medio, con su agujero y el tiento que la ata en [nudo]; el día
 * (unos 16 sp) y el mes corto pintados con carbón. Se tiñe con la luz que le toca y no pierde la lectura.
 */
private fun DrawScope.etiquetaCueva(v: Volumen, medidor: TextMeasurer, l: LuzCueva, r: Rect, nudo: Offset, hacia: Float) {
    val u = density
    val giro = 0f
    val ag = Offset(r.center.x, r.top + 2.4f * u)
    drawLine(l.s(0.35f), nudo + Offset(-hacia * 0.8f * u, 0.8f * u), ag + Offset(-hacia * 0.8f * u, 0.8f * u), 1.2f * u)
    drawLine(l.c(P.CUERO_OSC), nudo, ag, 1.2f * u)
    rotate(giro, ag) {
        val forma = cerradaCueva(listOf(Offset(r.left + 1f * u, r.top + 1.5f * u), Offset(r.center.x, r.top), Offset(r.right - 0.6f * u, r.top + 1.2f * u),
            Offset(r.right, r.center.y), Offset(r.right - 1.2f * u, r.bottom - 0.5f * u), Offset(r.center.x + r.width * 0.1f, r.bottom),
            Offset(r.left + 0.5f * u, r.bottom - 1.4f * u), Offset(r.left, r.center.y + r.height * 0.1f)))
        drawPath(corridoCueva(forma, -hacia * 1.8f * u, (if (l.a.oscuro) -0.8f else 2f) * u), l.s(0.55f))
        val cuero = l.etiqueta()
        drawPath(forma, cuero)
        // El cuero raspado: un velo desigual, el canto más oscuro y la luz del lado de la luz.
        drawOval(mezcla(cuero, P.CUERO_OSC, 0.5f).copy(alpha = 0.3f), Offset(r.left + r.width * (0.1f + 0.3f * azar(v.semilla, 81)), r.bottom - r.height * 0.28f), Size(r.width * 0.6f, r.height * 0.22f))
        drawPath(forma, mezcla(cuero, P.CUERO_OSC, 0.65f), style = Stroke(1f * u))
        drawCircle(l.c(P.HOLLIN), 1.1f * u, ag)
        val tinta = if (l.a.oscuro) Color(0xFF0C0706) else P.CARBON
        val zonaDia = Rect(r.left - 0.4f * u, r.top + 3.6f * u, r.right + 0.4f * u, r.top + 19.4f * u)
        textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 16f, peso = FontWeight.Bold, minimo = 9f)
        val zonaMes = Rect(r.left + 0.4f * u, zonaDia.bottom - 1f * u, r.right - 0.4f * u, zonaDia.bottom + 8.2f * u)
        textoEn(medidor, mesCortoCueva(v.mes), zonaMes, P.LETRA, tinta, 8.5f, peso = FontWeight.Bold, minimo = 6f)
    }
}

/**
 * La media luna de los Sueños, pintada con caolín en el objeto: un creciente recortado (no un círculo entero, que se leería como
 * un ojo), con su borde frío.
 */
private fun DrawScope.mediaLuna(c: Offset, r: Float, l: LuzCueva) {
    val luna = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val mordida = Path().apply { addOval(Rect(c.x - r * 0.4f, c.y - r * 1.2f, c.x + r * 1.6f, c.y + r * 0.8f)) }
    val creciente = Path().apply { op(luna, mordida, androidx.compose.ui.graphics.PathOperation.Difference) }
    drawPath(corridoCueva(creciente, 0.6f * density, 0.6f * density), P.CARBON.copy(alpha = 0.35f))
    drawPath(creciente, l.c(P.CAOLIN))
    drawPath(creciente, l.frio(0.4f))
}
