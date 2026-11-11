package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.sin

/** El ancho y el alto del lienzo de una escena, en sus unidades (4:3). */
const val ANCHO = 380f
const val ALTO = 285f

/** El cielo, del borde de arriba al [horizonte] (y un poco más abajo, por si algo deja ver detrás). */
fun DrawScope.cielo(l: Luces, horizonte: Float) {
    drawRect(Brush.verticalGradient(0f to l.alto, 0.58f to l.medio, 1f to l.bajo, startY = 0f, endY = horizonte), Offset.Zero, Size(ANCHO, horizonte))
    drawRect(l.bajo, Offset(0f, horizonte - 0.5f), Size(ANCHO, ALTO - horizonte + 0.5f))
}

/** Dónde está el sol en el lienzo, con el horizonte en [horizonte]. */
fun Luces.sol(horizonte: Float) = Offset(solX * ANCHO, horizonte * (1f - solAltura))

/** Dónde está la luna. */
fun Luces.luna(horizonte: Float) = Offset(lunaX * ANCHO, horizonte * (1f - lunaAltura))

/**
 * Las estrellas, la luna y el sol con su resplandor. Con [Luces.eclipse], el sol es una media luna. Va después de
 * [cielo] y antes de las nubes y de todo lo que tape el horizonte.
 */
fun DrawScope.astros(l: Luces, horizonte: Float, semilla: Long) {
    if (l.estrellas > 0.02f) {
        val azar = Azar(Semilla.de(semilla, "estrellas"))
        repeat(90) {
            val x = azar.entre(0f, ANCHO); val y = azar.entre(0f, horizonte - 6f)
            val brillo = azar.real(); val r = 0.35f + 0.75f * brillo * brillo
            // Cerca del horizonte la bruma las apaga.
            val a = l.estrellas * (0.35f + 0.65f * brillo) * (1f - (y / horizonte)).coerceIn(0f, 1f).let { 0.25f + 0.75f * it }
            drawCircle(Color(0xFFF6F2E6).copy(alpha = a.coerceIn(0f, 1f)), r, Offset(x, y))
            if (brillo > 0.93f) {
                val c = Color(0xFFFFFFFF).copy(alpha = (a * 0.5f).coerceIn(0f, 1f))
                drawLine(c, Offset(x - 2.4f, y), Offset(x + 2.4f, y), 0.35f); drawLine(c, Offset(x, y - 2.4f), Offset(x, y + 2.4f), 0.35f)
            }
        }
    }
    // La luna: solo de noche y con el cielo abierto.
    val verLuna = (l.estrellas - 0.3f).coerceAtLeast(0f) / 0.7f * (1f - l.eclipse)
    if (verLuna > 0.02f) {
        val p = l.luna(horizonte); val r = 7.5f
        drawCircle(Brush.radialGradient(listOf(Color(0xFFDDE6FF).copy(alpha = 0.30f * verLuna), Color.Transparent), p, r * 5f), r * 5f, p)
        val disco = Path().apply { addOval(Rect(p, r)) }
        drawCircle(Color(0xFFF5EFDD).copy(alpha = verLuna), r, p)
        clipPath(disco) {
            // La parte en sombra y dos mares.
            drawCircle(lerp(l.alto, Color(0xFF2A3350), 0.4f).copy(alpha = 0.92f * verLuna), r * 1.05f, p + Offset(-5.2f, -2.2f))
            drawCircle(Color(0xFFCFC8B4).copy(alpha = 0.55f * verLuna), 1.7f, p + Offset(2.6f, 1.2f))
            drawCircle(Color(0xFFCFC8B4).copy(alpha = 0.45f * verLuna), 1.1f, p + Offset(4.2f, -2.4f))
        }
    }
    // El sol.
    if (l.solAltura > -0.2f && l.solFuerza > 0.02f) {
        val p = l.sol(horizonte)
        val bajo = (1f - l.solAltura / 0.45f).coerceIn(0f, 1f)             // cerca del horizonte
        val f = l.solFuerza * (1f - 0.75f * l.eclipse)
        // El resplandor: ancho y tendido cuando el sol está bajo.
        drawRect(Brush.radialGradient(listOf(l.solColor.copy(alpha = 0.60f * f), l.solColor.copy(alpha = 0.18f * f), Color.Transparent), p, 70f + 60f * bajo),
            Offset(0f, 0f), Size(ANCHO, horizonte), blendMode = BlendMode.Screen)
        if (bajo > 0.05f) drawOval(Brush.radialGradient(listOf(l.solColor.copy(alpha = 0.45f * bajo * f), Color.Transparent), Offset(p.x, horizonte), 150f),
            Offset(p.x - 190f, horizonte - 26f), Size(380f, 44f), blendMode = BlendMode.Screen)
        if (l.solAltura > -0.06f && l.solAltura < 1.05f) {
            val r = 7.5f + 3f * bajo
            val centro = lerp(l.solColor, Color.White, 0.75f - 0.45f * bajo)
            if (l.eclipse > 0f) {
                // La corona, muy tenue, y la media luna que queda.
                drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.5f * l.eclipse), Color.Transparent), p, r * 3f), r * 3f, p)
                val disco = Path().apply { addOval(Rect(p, r)) }
                clipPath(disco) {
                    drawCircle(centro, r, p)
                    drawCircle(lerp(l.alto, Color(0xFF10172E), 0.55f), r * 0.98f, p + Offset(r * 0.42f * (1.15f - l.eclipse * 0.4f), -r * 0.22f))
                }
            } else {
                drawCircle(Brush.radialGradient(listOf(centro, lerp(centro, l.solColor, 0.6f)), p, r), r, p)
            }
        }
    }
}

/**
 * Una mancha de borde blando (una elipse que se desvanece hacia fuera): con muchas se hacen las nubes bajas, la
 * niebla y el humo, sin que se note el contorno de cada una.
 */
fun DrawScope.mancha(centro: Offset, rx: Float, ry: Float, color: Color, alfa: Float) {
    if (alfa <= 0.004f || rx <= 0f || ry <= 0f) return
    val a = alfa.coerceIn(0f, 1f)
    scale(rx / ry, 1f, centro) {
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = a), color.copy(alpha = a * 0.62f), color.copy(alpha = 0f)), centro, ry), ry, centro)
    }
}

/**
 * Una nube de algodón (cúmulo) apoyada en la línea [y]: de [x] a [x] + [ancho]. La parte de arriba toma la luz del
 * sol y la base queda en sombra. [carga], de 0 a 1: qué tan gris y pesada es.
 */
fun DrawScope.nube(l: Luces, x: Float, y: Float, ancho: Float, azar: Azar, carga: Float = 0f, lejos: Float = 0.3f) {
    val alto = ancho * (0.30f + 0.10f * azar.real())
    val forma = Path()
    // Los borbotones de arriba, más altos hacia el centro.
    val n = 6 + azar.entero(3)
    for (i in 0 until n) {
        val k = (i + azar.entre(0.3f, 0.7f)) / n
        val campana = sin(PI.toFloat() * k)
        val r = ancho * azar.entre(0.09f, 0.16f) * (0.55f + 0.6f * campana)
        forma.addOval(Rect(Offset(x + ancho * (0.08f + 0.84f * k), y - r * 0.5f - alto * 0.62f * campana * azar.entre(0.5f, 1f)), r))
    }
    // El cuerpo y la base plana.
    for (i in 0 until 4) {
        val k = (i + 0.5f) / 4f
        val r = ancho * 0.15f * (0.8f + 0.45f * sin(PI.toFloat() * k))
        forma.addOval(Rect(Offset(x + ancho * (0.14f + 0.72f * k), y - r * 0.66f), r))
    }
    forma.addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(x + ancho * 0.05f, y - alto * 0.28f, x + ancho * 0.95f, y), CornerRadius(alto * 0.14f)))
    val blanco = lerp(Color(0xFFFFFFFF), Color(0xFF8E97A6), 0.75f * carga)
    val luz = lerp(l.tono(blanco, 0f), l.solColor, if (l.solAltura in -0.1f..0.35f && l.nublado < 0.5f) 0.45f else 0.08f)
    val sombra = lerp(l.tono(lerp(Color(0xFFB9C3D4), Color(0xFF59616F), carga), 0f), l.medio, 0.35f)
    val alfa = 0.96f - 0.25f * lejos
    clipPath(forma) {
        drawRect(Brush.verticalGradient(0f to luz.copy(alpha = alfa), 0.5f to lerp(luz, sombra, 0.4f).copy(alpha = alfa), 1f to sombra.copy(alpha = alfa),
            startY = y - alto * 1.25f, endY = y), Offset(x - ancho * 0.2f, y - alto * 2.2f), Size(ancho * 1.4f, alto * 2.3f))
    }
}

/** Una capa de nube baja (estrato) a la altura [y], de lado a lado, para los días cubiertos: manchas blandas, sin borde. */
fun DrawScope.estrato(l: Luces, y: Float, alto: Float, azar: Azar, carga: Float) {
    val base = lerp(l.tono(lerp(Color(0xFFDDE2EA), Color(0xFF5E6674), carga)), l.medio, 0.3f)
    val luz = lerp(base, l.tono(Color.White), 0.22f)
    var x = -30f
    while (x < ANCHO + 30f) {
        val w = azar.entre(60f, 130f); val h = alto * azar.entre(0.7f, 1.3f)
        mancha(Offset(x, y + azar.entre(-alto * 0.25f, alto * 0.25f) + h * 0.25f), w, h, base, 0.62f + 0.3f * carga)
        mancha(Offset(x + azar.entre(-20f, 20f), y - h * 0.15f), w * 0.7f, h * 0.6f, luz, 0.4f)
        x += w * azar.entre(0.45f, 0.75f)
    }
}

/** Hilos de nube alta (cirros), para los días de viento: plumas largas y tenues, tendidas. */
fun DrawScope.cirros(l: Luces, horizonte: Float, azar: Azar) {
    val c = lerp(l.tono(Color.White), l.medio, 0.2f)
    repeat(6) {
        val x = azar.entre(-10f, ANCHO - 60f); val y = azar.entre(12f, horizonte * 0.62f); val w = azar.entre(60f, 130f); val sube = azar.entre(4f, 12f)
        for (i in 0..8) {
            val k = i / 8f
            mancha(Offset(x + w * k, y - sube * k * k + azar.entre(-1f, 1f)), w * 0.16f, 1.4f + 2.2f * (1f - k), c, 0.42f * (1f - 0.5f * k))
        }
    }
}

/**
 * Las nubes del tiempo que hace: ninguna o un par, lejanas, con el cielo despejado; cirros con viento; cúmulos con
 * «nublado»; el cielo cubierto con lluvia, tormenta o nieve. Con niebla y con polvo no hay nubes: el velo lo pone
 * `tiempoQuieto`.
 */
fun DrawScope.nubes(l: Luces, horizonte: Float, clima: Clima, semilla: Long) {
    val azar = Azar(Semilla.de(semilla, "nubes"))
    when (clima) {
        Clima.DESPEJADO -> repeat(azar.entero(3)) { nube(l, azar.entre(10f, ANCHO - 90f), azar.entre(horizonte * 0.45f, horizonte * 0.86f), azar.entre(34f, 62f), azar, 0f, 0.7f) }
        Clima.VIENTO -> { cirros(l, horizonte, azar); nube(l, azar.entre(40f, ANCHO - 120f), horizonte * 0.82f, 50f, azar, 0f, 0.7f) }
        Clima.NUBES -> {
            repeat(3) { nube(l, azar.entre(-20f, ANCHO - 60f), azar.entre(horizonte * 0.55f, horizonte * 0.9f), azar.entre(46f, 80f), azar, 0.15f, 0.7f) }
            repeat(4) { nube(l, azar.entre(-30f, ANCHO - 90f), azar.entre(horizonte * 0.16f, horizonte * 0.55f), azar.entre(90f, 150f), azar, 0.28f, 0.2f) }
        }
        Clima.LLUVIA -> {
            estrato(l, horizonte * 0.04f, 30f, azar, 0.75f); estrato(l, horizonte * 0.3f, 24f, azar, 0.55f); estrato(l, horizonte * 0.56f, 16f, azar, 0.4f)
        }
        Clima.TORMENTA -> {
            estrato(l, 0f, 40f, azar, 1f); estrato(l, horizonte * 0.26f, 32f, azar, 0.9f); estrato(l, horizonte * 0.52f, 20f, azar, 0.65f)
        }
        Clima.NIEVE -> { estrato(l, 0f, 30f, azar, 0.25f); estrato(l, horizonte * 0.3f, 24f, azar, 0.2f); estrato(l, horizonte * 0.58f, 16f, azar, 0.15f) }
        Clima.NIEBLA -> Unit
        Clima.POLVO -> Unit
        Clima.SIN_CLIMA -> Unit
    }
}
