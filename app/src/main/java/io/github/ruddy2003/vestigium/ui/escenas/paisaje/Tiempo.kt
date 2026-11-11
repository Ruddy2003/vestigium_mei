package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.floor
import kotlin.math.sin

/** Cuánto sopla el viento con cada tiempo, de 0 a 1: tumba el humo, riza el agua e inclina la lluvia. */
fun vientoDe(clima: Clima): Float = when (clima) {
    Clima.DESPEJADO -> 0.12f
    Clima.NUBES -> 0.2f
    Clima.LLUVIA -> 0.3f
    Clima.TORMENTA -> 0.8f
    Clima.NIEVE -> 0.15f
    Clima.NIEBLA -> 0f
    Clima.VIENTO -> 1f
    Clima.POLVO -> 0.7f
    Clima.SIN_CLIMA -> 0f
}

/** Si el tiempo trae algo que se mueve (gotas, copos, ráfagas, polvo): entonces la escena pide cuadros. */
fun seMueve(clima: Clima): Boolean = when (clima) {
    Clima.DESPEJADO -> false
    Clima.NUBES -> false
    Clima.LLUVIA -> true
    Clima.TORMENTA -> true
    Clima.NIEVE -> true
    Clima.NIEBLA -> false
    Clima.VIENTO -> true
    Clima.POLVO -> true
    Clima.SIN_CLIMA -> false
}

/**
 * Lo quieto del tiempo, encima del paisaje ya pintado: los bancos de niebla, el velo gris de la lluvia que borra lo
 * lejano, el velo ocre del polvo y la nieve posada no (esa la pone cada escena donde cuaja). Va al final de la parte
 * quieta de la escena.
 */
fun DrawScope.tiempoQuieto(l: Luces, clima: Clima, horizonte: Float, semilla: Long) {
    val azar = Azar(Semilla.de(semilla, "tiempo"))
    when (clima) {
        Clima.NIEBLA -> {
            drawRect(Brush.verticalGradient(0f to l.bruma.copy(alpha = 0.25f), 0.5f to l.bruma.copy(alpha = 0.72f), 1f to l.bruma.copy(alpha = 0.3f), startY = horizonte - 70f, endY = ALTO),
                Offset(0f, horizonte - 70f), Size(ANCHO, ALTO - horizonte + 70f))
            repeat(9) {
                mancha(Offset(azar.entre(-20f, ANCHO + 20f), azar.entre(horizonte - 26f, ALTO - 50f)), azar.entre(80f, 170f), azar.entre(9f, 20f), lerp(l.bruma, Color.White, 0.3f), 0.55f)
            }
        }
        Clima.LLUVIA, Clima.TORMENTA -> {
            // Lo lejano se pierde tras la cortina de agua.
            val velo = lerp(l.bruma, l.medio, 0.4f)
            drawRect(Brush.verticalGradient(0f to velo.copy(alpha = 0.10f), 0.45f to velo.copy(alpha = 0.42f), 1f to Color.Transparent, startY = 0f, endY = horizonte + 60f), Offset.Zero, Size(ANCHO, horizonte + 60f))
        }
        Clima.POLVO -> drawRect(Brush.verticalGradient(0f to Color(0xFFC9A66A).copy(alpha = 0.12f), 0.6f to Color(0xFFC9A66A).copy(alpha = 0.40f), 1f to Color(0xFFC9A66A).copy(alpha = 0.22f)), Offset.Zero, Size(ANCHO, ALTO))
        Clima.NIEVE -> drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFFE8EDF2)).copy(alpha = 0.28f)), 0f, horizonte + 20f), Offset.Zero, Size(ANCHO, horizonte + 20f))
        Clima.DESPEJADO -> Unit
        Clima.NUBES -> Unit
        Clima.VIENTO -> Unit
        Clima.SIN_CLIMA -> Unit
    }
}

/** La lluvia: gotas en dos planos que caen con el [reloj]. [fuerza], de 0 a 1; [viento] las inclina. */
fun DrawScope.lluvia(l: Luces, reloj: Float, semilla: Long, fuerza: Float = 0.6f, viento: Float = 0.3f) {
    val azar = Azar(Semilla.de(semilla, "lluvia"))
    val color = lerp(l.tono(Color(0xFFE2ECF6)), Color.White, 0.2f)
    val cuantas = (60 + 110 * fuerza).toInt()
    val sesgo = -0.18f - 0.45f * viento                                         // cae hacia la izquierda
    repeat(cuantas) { i ->
        val cerca = i % 3 == 0
        val rapidez = (if (cerca) 330f else 230f) * azar.entre(0.85f, 1.15f)
        val largo = (if (cerca) 13f else 7f) * (0.8f + 0.5f * fuerza)
        val y = ((azar.entre(0f, ALTO + 40f) + reloj * rapidez) % (ALTO + 40f)) - 20f
        val x = ((azar.entre(0f, ANCHO + 120f) + (reloj * rapidez) * sesgo) % (ANCHO + 120f) + (ANCHO + 120f)) % (ANCHO + 120f) - 20f
        drawLine(color.copy(alpha = if (cerca) 0.5f else 0.28f), Offset(x, y), Offset(x + largo * sesgo, y + largo), if (cerca) 0.9f else 0.6f, StrokeCap.Round)
    }
}

/** La nieve: copos que bajan despacio y se mecen. */
fun DrawScope.nieve(l: Luces, reloj: Float, semilla: Long, fuerza: Float = 0.6f) {
    val azar = Azar(Semilla.de(semilla, "nieve"))
    val color = l.tono(Color(0xFFFFFFFF)).let { lerp(it, Color.White, 0.5f) }
    repeat((50 + 80 * fuerza).toInt()) { i ->
        val cerca = i % 3 == 0
        val rapidez = (if (cerca) 26f else 15f) * azar.entre(0.7f, 1.3f)
        val fase = azar.entre(0f, 6.28f)
        val y = ((azar.entre(0f, ALTO + 20f) + reloj * rapidez) % (ALTO + 20f)) - 10f
        val x = azar.entre(-10f, ANCHO + 10f) + 7f * sin(reloj * 0.9f + fase)
        drawCircle(color.copy(alpha = if (cerca) 0.95f else 0.6f), if (cerca) 1.5f else 0.9f, Offset(x, y))
    }
}

/**
 * El relámpago: cada pocos segundos el cielo se enciende un instante y cae un rayo en un sitio distinto. Entre uno y
 * otro no dibuja nada.
 */
fun DrawScope.relampago(reloj: Float, horizonte: Float, semilla: Long) {
    val cada = 6.5f
    val turno = floor(reloj / cada); val dentro = reloj / cada - turno
    val enciende = dentro < 0.018f || (dentro > 0.035f && dentro < 0.05f)
    if (!enciende) return
    val azar = Azar(Semilla.de(semilla, "rayo", turno.toInt()))
    drawRect(Color.White.copy(alpha = 0.22f), Offset.Zero, Size(ANCHO, ALTO))
    var p = Offset(azar.entre(40f, ANCHO - 40f), azar.entre(10f, 30f))
    val rayo = Path().apply { moveTo(p.x, p.y) }
    while (p.y < horizonte - 6f) { p = Offset(p.x + azar.entre(-9f, 9f), p.y + azar.entre(7f, 15f)); rayo.lineTo(p.x, minOf(p.y, horizonte - 2f)) }
    drawPath(rayo, Color.White.copy(alpha = 0.35f), style = Stroke(3.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(rayo, Color(0xFFFFFBEA), style = Stroke(1.1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** Las ráfagas de viento: unos pocos trazos largos y tenues que cruzan el cuadro. */
fun DrawScope.rafagas(l: Luces, reloj: Float, semilla: Long) {
    val azar = Azar(Semilla.de(semilla, "viento"))
    val color = lerp(l.tono(Color.White), l.bajo, 0.3f)
    repeat(9) {
        val rapidez = azar.entre(120f, 210f); val largo = azar.entre(28f, 64f)
        val y = azar.entre(20f, ALTO - 30f)
        val x = ANCHO + largo - ((azar.entre(0f, ANCHO * 2.2f) + reloj * rapidez) % (ANCHO * 2.2f))
        val onda = 3f * sin(reloj * 2f + y)
        drawPath(Path().apply { moveTo(x, y + onda); cubicTo(x - largo * 0.3f, y - 2.5f + onda, x - largo * 0.7f, y + 2.5f + onda, x - largo, y + onda) },
            color.copy(alpha = 0.22f), style = Stroke(0.8f, cap = StrokeCap.Round))
    }
}

/** El polvo en el aire: motas que pasan de lado. */
fun DrawScope.polvo(reloj: Float, semilla: Long) {
    val azar = Azar(Semilla.de(semilla, "polvo"))
    repeat(60) {
        val rapidez = azar.entre(40f, 90f)
        val y = azar.entre(0f, ALTO) + 4f * sin(reloj * 1.3f + it)
        val x = ANCHO + 10f - ((azar.entre(0f, ANCHO + 20f) + reloj * rapidez) % (ANCHO + 20f))
        drawCircle(Color(0xFFE2C48A).copy(alpha = azar.entre(0.2f, 0.5f)), azar.entre(0.5f, 1.3f), Offset(x, y))
    }
}

/** Lo que se mueve del tiempo que hace, en el instante [reloj]: va encima de todo lo demás, salvo el gato. */
fun DrawScope.tiempoVivo(l: Luces, clima: Clima, reloj: Float, horizonte: Float, semilla: Long) {
    when (clima) {
        Clima.LLUVIA -> lluvia(l, reloj, semilla, 0.55f, vientoDe(clima))
        Clima.TORMENTA -> { relampago(reloj, horizonte, semilla); lluvia(l, reloj, semilla, 1f, vientoDe(clima)) }
        Clima.NIEVE -> nieve(l, reloj, semilla)
        Clima.VIENTO -> rafagas(l, reloj, semilla)
        Clima.POLVO -> polvo(reloj, semilla)
        Clima.DESPEJADO -> Unit
        Clima.NUBES -> Unit
        Clima.NIEBLA -> Unit
        Clima.SIN_CLIMA -> Unit
    }
}
