package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

/** Los colores propios del mar en calma, de día: en general, donde es hondo y donde hay poco fondo. Un turquesa apagado, no un azul puro. */
val MAR = Color(0xFF2E8091)
val MAR_HONDO = Color(0xFF1F5D75)
val MAR_ORILLA = Color(0xFF8CCBB8)

/**
 * Agua quieta dentro de [zona] (el mar, un río, un estanque), recortada a [contorno] si se da. Lejos (el borde de
 * arriba de [zona]) es honda y oscura, con una línea clara en el horizonte; hacia quien mira se aclara y verdea. Lleva
 * rachas donde el viento la riza, calmas que reflejan el cielo y rizos, más largos y curvos cuanto más cerca.
 * [viento], de 0 a 1: con viento, más rizos y crestas blancas. La estela del sol va aparte ([estela]).
 */
fun DrawScope.agua(l: Luces, zona: Rect, azar: Azar, propio: Color = MAR, viento: Float = 0f, contorno: Path? = null, hondo: Color = MAR_HONDO, orilla: Color = MAR_ORILLA) {
    val lejos = lerp(l.tono(hondo, 0.25f), l.bajo, 0.3f)
    val medio = lerp(l.tono(propio), l.medio, 0.12f)
    val cerca = lerp(l.tono(lerp(propio, orilla, 0.4f)), l.alto, 0.06f)
    val dibujo: DrawScope.() -> Unit = {
        drawRect(Brush.verticalGradient(0f to lerp(lejos, l.bajo, 0.5f), 0.035f to lejos, 0.24f to lerp(lejos, medio, 0.6f), 0.62f to medio, 1f to cerca, startY = zona.top, endY = zona.bottom), zona.topLeft, zona.size)
        val alto = zona.height - 12f
        repeat(8) { val p = azar.real(); mancha(Offset(azar.entre(zona.left, zona.right), zona.top + 5f + alto * p), azar.entre(40f, 110f) * (0.5f + p), 1.4f + 5f * p, l.tono(hondo), 0.26f) }
        repeat(6) { val p = azar.real(); mancha(Offset(azar.entre(zona.left, zona.right), zona.top + 7f + alto * p), azar.entre(40f, 120f) * (0.5f + p), 1.1f + 4f * p, lerp(l.bajo, Color.White, 0.25f), 0.2f) }
        val claro = lerp(l.bajo, Color.White, 0.3f); val oscuro = lerp(l.tono(hondo), Color(0xFF0A1A2C), 0.3f)
        repeat(((zona.width * zona.height) / 150f * (1f + viento)).toInt().coerceAtMost(560)) {
            val k = azar.real(); val prof = k * k                              // más rizos lejos (arriba)
            val y = zona.top + 1.5f + (zona.height - 2f) * prof
            val largo = (2f + 15f * prof) * azar.entre(0.5f, 1.3f); val x = azar.entre(zona.left - 6f, zona.right)
            val c = (if (azar.real() > 0.5f) claro else oscuro).copy(alpha = ((0.10f + 0.2f * azar.real()) * (1f + 0.5f * viento)).coerceAtMost(0.5f))
            if (prof > 0.3f) drawPath(Path().apply { moveTo(x, y); cubicTo(x + largo * 0.3f, y - 0.8f - prof, x + largo * 0.7f, y - 0.8f - prof, x + largo, y + 0.3f) }, c, style = Stroke(0.5f + 0.6f * prof, cap = StrokeCap.Round))
            else drawLine(c, Offset(x, y), Offset(x + largo, y), 0.45f + 0.5f * prof, StrokeCap.Round)
        }
        if (viento > 0.4f) repeat((zona.width / 16f * viento).toInt()) {
            val prof = azar.real(); val y = zona.top + 3f + (zona.height - 5f) * prof; val x = azar.entre(zona.left, zona.right)
            val w = 2f + 4.5f * prof
            drawPath(Path().apply { moveTo(x, y); cubicTo(x + w * 0.3f, y - 1.6f, x + w * 0.7f, y - 1.6f, x + w, y) }, l.tono(Color.White).copy(alpha = 0.6f), style = Stroke(0.7f, cap = StrokeCap.Round))
        }
    }
    if (contorno != null) clipPath(contorno) { dibujo() } else dibujo()
}

/**
 * La estela de luz del sol o de la luna sobre el agua de [zona]. Con el astro bajo es una calle estrecha y viva que
 * viene del horizonte; con el sol alto, un campo ancho de chispas. Va encima del agua y de los reflejos, y debajo de
 * lo que flota.
 */
fun DrawScope.estela(l: Luces, zona: Rect, azar: Azar) {
    val conSol = l.solAltura > -0.06f && l.solFuerza > 0.3f && l.noche < 0.6f
    val conLuna = !conSol && l.estrellas > 0.35f
    if (!conSol && !conLuna) return
    val x0 = (if (conSol) l.solX else l.lunaX) * ANCHO
    val bajo = (1f - (if (conSol) l.solAltura else l.lunaAltura) / 0.7f).coerceIn(0f, 1f)
    val color = if (conSol) lerp(l.solColor, Color.White, 0.5f) else Color(0xFFE2EAFF)
    val fuerza = if (conSol) l.solFuerza * (1f - l.eclipse) * (0.42f + 0.58f * bajo) else 0.75f * l.estrellas
    repeat(260) {
        val prof = azar.real(); val y = zona.top + 1f + (zona.height - 2f) * prof
        val ancho = (170f + 60f * prof) + ((5f + 62f * prof) - (170f + 60f * prof)) * bajo
        val u = (azar.real() + azar.real() + azar.real()) / 1.5f - 1f                // juntas hacia el centro
        val x = x0 + u * ancho * 0.5f
        val largo = (1.4f + 8f * prof) * azar.entre(0.5f, 1.2f)
        val a = 0.8f * fuerza * azar.entre(0.35f, 1f) * (1f - 0.6f * abs(u))
        drawLine(color.copy(alpha = a.coerceIn(0f, 1f)), Offset(x - largo / 2f, y), Offset(x + largo / 2f, y), 0.5f + 0.9f * prof, StrokeCap.Round, blendMode = BlendMode.Screen)
    }
    if (bajo > 0.3f) mancha(Offset(x0, zona.top + 3f), 36f, 4f, color, (0.55f * fuerza * bajo).coerceIn(0f, 1f))
}

/**
 * El reflejo roto de algo que está sobre el agua: de [x0] a [x1], desde la línea del agua [y] y [alto] hacia abajo.
 * Son trazos tendidos que se corren de lado y se abren más cuanto más hondo.
 */
fun DrawScope.reflejoRoto(x0: Float, x1: Float, y: Float, alto: Float, color: Color, alfa: Float, azar: Azar, paso: Float = 1f) {
    var yy = y + 0.35f
    while (yy < y + alto) {
        val k = (yy - y) / alto
        val corre = (azar.real() - 0.5f) * (0.8f + 5f * k)
        val merma = (x1 - x0) * 0.16f * k
        val a = alfa * (1f - k).pow(1.25f)
        if (azar.real() > 0.5f * k) drawLine(color.copy(alpha = a.coerceIn(0f, 1f)), Offset(x0 + merma + corre, yy), Offset(x1 - merma + corre, yy), paso * 0.62f, StrokeCap.Round)
        yy += paso * (1f + 0.7f * k)
    }
}

/** Junto a una [orilla] hay poco fondo: el agua se aclara y verdea. De noche casi no se nota. */
fun DrawScope.bajio(l: Luces, orilla: Path, ancho: Float = 38f, propio: Color = MAR_ORILLA) {
    val c = l.tono(propio); val dia = 1f - 0.75f * l.noche
    for ((w, a) in listOf(1f to 0.20f, 0.66f to 0.26f, 0.34f to 0.36f)) drawPath(orilla, c.copy(alpha = a * dia), style = Stroke(ancho * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/**
 * Los destellos que se mueven sobre el agua: unos pocos rizos que se encienden y se apagan con el [reloj]. Es lo único
 * del agua que se traza en cada cuadro.
 */
fun DrawScope.destellos(l: Luces, zona: Rect, reloj: Float, semilla: Long, cuantos: Int = 26) {
    val azar = Azar(semilla)
    val claro = lerp(l.bajo, Color.White, 0.5f)
    val x0 = (if (l.estrellas > 0.6f) l.lunaX else l.solX) * ANCHO
    repeat(cuantos) { i ->
        val prof = azar.real(); val y = zona.top + 2f + (zona.height - 3f) * prof
        // La mitad, repartidos; la otra mitad, en la estela.
        val x = if (i % 2 == 0) azar.entre(zona.left, zona.right) else x0 + (azar.real() - 0.5f) * (10f + 40f * prof)
        val largo = 2f + 8f * prof
        val fase = azar.entre(0f, 6.28f); val ritmo = azar.entre(0.9f, 2.2f)
        val a = (0.5f + 0.5f * sin(reloj * ritmo + fase)).let { it * it } * (0.35f - 0.2f * l.nublado)
        if (x > zona.left && x < zona.right) drawLine(claro.copy(alpha = a.coerceIn(0f, 1f)), Offset(x - largo / 2f, y), Offset(x + largo / 2f, y), 0.6f + 0.6f * prof, StrokeCap.Round)
    }
}

/** El reflejo de una luz (una antorcha, una ventana) en el agua: una columna de trazos que tiembla con el [reloj]. */
fun DrawScope.reflejo(pie: Offset, alto: Float, color: Color, fuerza: Float, reloj: Float, semilla: Int = 0) {
    for (i in 0 until 6) {
        val k = i / 6f
        val y = pie.y + 1f + alto * k
        val w = 1.5f + 2.5f * k + 0.8f * sin(reloj * 3.1f + i * 1.7f + semilla)
        drawLine(color.copy(alpha = (fuerza * (0.55f - 0.45f * k)).coerceIn(0f, 1f)), Offset(pie.x - w, y), Offset(pie.x + w, y), 0.8f, StrokeCap.Round, blendMode = BlendMode.Screen)
    }
}
