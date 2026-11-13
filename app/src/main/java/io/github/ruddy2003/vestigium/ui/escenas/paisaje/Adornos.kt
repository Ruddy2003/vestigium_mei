package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 * Adornos genéricos de los días especiales: lo que se cuelga y se quita. Nunca personas. Cada escena elige los suyos
 * según la clave de escena de la ficha («fiesta», «luto», «antorchas»…) y les da los colores de su tema.
 */

/** Los puntos de una cuerda que cuelga de [a] a [b] y baja [caida] en el centro. */
fun cuerda(a: Offset, b: Offset, caida: Float, pasos: Int = 14): List<Offset> = (0..pasos).map { i ->
    val k = i / pasos.toFloat()
    Offset(a.x + (b.x - a.x) * k, a.y + (b.y - a.y) * k + caida * 4f * k * (1f - k))
}

/**
 * Una guirnalda de hojas colgada de [a] a [b], con [flores] de color entre las hojas si se dan (rosas, cintas,
 * granadas). Es el adorno de fiesta más antiguo y sirve en casi cualquier época.
 */
fun DrawScope.guirnalda(l: Luces, a: Offset, b: Offset, caida: Float, azar: Azar, hoja: Color = Color(0xFF4F7A3A), flores: List<Color> = emptyList(), grueso: Float = 3f, lejos: Float = 0f) {
    val puntos = cuerda(a, b, caida, maxOf(8, ((b - a).getDistance() / (grueso * 1.2f)).toInt()))
    val verde = l.tono(hoja, lejos); val verdeLuz = l.tono(lerp(hoja, Color(0xFFDDE8A0), 0.35f), lejos); val verdeSombra = l.tono(lerp(hoja, Color(0xFF16241A), 0.4f), lejos)
    drawPath(curva(puntos), verdeSombra, style = Stroke(grueso * 0.9f, cap = StrokeCap.Round))
    puntos.forEachIndexed { i, p ->
        val ang = azar.entre(0f, 6.28f)
        val d = Offset(cos(ang), sin(ang)) * (grueso * 0.55f)
        drawOval(if (i % 2 == 0) verde else verdeLuz, p + d - Offset(grueso * 0.55f, grueso * 0.32f), androidx.compose.ui.geometry.Size(grueso * 1.1f, grueso * 0.64f))
    }
    if (flores.isNotEmpty()) for (i in 2 until puntos.size - 1 step 3) {
        val c = flores[(i / 3) % flores.size]
        drawCircle(l.tono(c, lejos), grueso * 0.55f, puntos[i] + Offset(0f, grueso * 0.2f))
        drawCircle(l.tono(lerp(c, Color.White, 0.5f), lejos), grueso * 0.2f, puntos[i] + Offset(-grueso * 0.12f, grueso * 0.05f))
    }
}

/**
 * Una cuerda de [a] a [b] con banderines o cintas de [colores] que cuelgan [largo]. Con [punta] acaban en pico
 * (banderines); sin ella, son tiras (cintas). [reloj] las mece un poco.
 */
fun DrawScope.banderines(l: Luces, a: Offset, b: Offset, caida: Float, colores: List<Color>, largo: Float, punta: Boolean = true, reloj: Float = 0f, lejos: Float = 0f) {
    val n = maxOf(3, ((b - a).getDistance() / (largo * 0.95f)).toInt())
    val puntos = cuerda(a, b, caida, n * 2)
    drawPath(curva(puntos), l.tono(Color(0xFF5A4630), lejos), style = Stroke(maxOf(0.4f, largo * 0.06f)))
    for (i in 0 until n) {
        val p = puntos[i * 2]; val q = puntos[i * 2 + 2]
        val color = l.tono(colores[i % colores.size], lejos)
        val mece = largo * 0.14f * sin(reloj * 2.2f + i * 0.9f)
        val medio = Offset((p.x + q.x) / 2f + mece, maxOf(p.y, q.y) + largo)
        if (punta) drawPath(poligono(p, q, medio), color)
        else drawPath(poligono(p + Offset((q.x - p.x) * 0.2f, 0f), q - Offset((q.x - p.x) * 0.2f, 0f), Offset(q.x - (q.x - p.x) * 0.2f + mece, q.y + largo), Offset(p.x + (q.x - p.x) * 0.2f + mece, p.y + largo)), color)
    }
}

/** Una corona de hojas (laurel, olivo, hiedra) de [radio] con el centro en [centro], abierta por abajo si [abierta]. */
fun DrawScope.corona(l: Luces, centro: Offset, radio: Float, hoja: Color = Color(0xFF5A8440), cinta: Color? = null, abierta: Boolean = false, lejos: Float = 0f) {
    val verde = l.tono(hoja, lejos); val luz = l.tono(lerp(hoja, Color(0xFFE2ECA8), 0.35f), lejos)
    val n = 16
    for (i in 0 until n) {
        val ang = (2 * PI * i / n).toFloat() + 1.57f
        if (abierta && (i == 0 || i == n - 1)) continue
        val p = centro + Offset(cos(ang), sin(ang)) * radio
        val tang = Offset(-sin(ang), cos(ang)) * (radio * 0.34f)
        drawLine(if (i % 2 == 0) verde else luz, p - tang, p + tang + Offset(cos(ang), sin(ang)) * (radio * 0.16f), radio * 0.26f, StrokeCap.Round)
    }
    if (cinta != null) {
        val c = l.tono(cinta, lejos); val pie = centro + Offset(0f, radio)
        drawLine(c, pie, pie + Offset(-radio * 0.5f, radio * 0.8f), radio * 0.18f, StrokeCap.Round)
        drawLine(c, pie, pie + Offset(radio * 0.45f, radio * 0.85f), radio * 0.18f, StrokeCap.Round)
    }
}

/**
 * Un paño colgado de [a] a [b] que cae [alto]: negro o pardo para el luto, de color para una colgadura de fiesta.
 * Hace tres pliegues.
 */
fun DrawScope.pano(l: Luces, a: Offset, b: Offset, alto: Float, propio: Color, lejos: Float = 0f) {
    val c = l.tono(propio, lejos); val pliegue = l.tono(lerp(propio, Color.Black, 0.35f), lejos); val luz = l.tono(lerp(propio, Color.White, 0.18f), lejos)
    val w = b.x - a.x
    val forma = Path().apply {
        moveTo(a.x, a.y); lineTo(b.x, b.y)
        lineTo(b.x - w * 0.04f, b.y + alto * 0.92f)
        cubicTo(b.x - w * 0.25f, b.y + alto * 1.08f, a.x + w * 0.25f, a.y + alto * 0.86f, a.x + w * 0.04f, a.y + alto)
        close()
    }
    drawPath(forma, c)
    for (i in 1..3) {
        val x = a.x + w * i / 4f
        drawLine(if (i % 2 == 0) luz else pliegue, Offset(x, a.y + (b.y - a.y) * i / 4f + alto * 0.04f), Offset(x - w * 0.02f, a.y + alto * 0.9f), maxOf(0.5f, w * 0.035f), StrokeCap.Round)
    }
}
