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

/**
 * El cielo, del borde de arriba al [horizonte] (y un poco más abajo, por si algo deja ver detrás). Con [semilla] lleva
 * además unas pinceladas tendidas, para que no sea un degradado liso, y la calima del horizonte.
 */
fun DrawScope.cielo(l: Luces, horizonte: Float, semilla: Long? = null) {
    drawRect(Brush.verticalGradient(0f to l.alto, 0.58f to l.medio, 1f to l.bajo, startY = 0f, endY = horizonte), Offset.Zero, Size(ANCHO, horizonte))
    drawRect(l.bajo, Offset(0f, horizonte - 0.5f), Size(ANCHO, ALTO - horizonte + 0.5f))
    if (semilla == null) return
    val az = Azar(Semilla.de(semilla, "pinceladas"))
    repeat(52) {
        val y = az.entre(2f, horizonte - 4f); val w = az.entre(50f, 170f); val x = az.entre(-40f, ANCHO)
        mancha(Offset(x + w / 2f, y), w / 2f, az.entre(1.2f, 3.8f), if (az.real() > 0.5f) lerp(l.bajo, Color.White, 0.3f) else l.alto, 0.06f + 0.07f * az.real())
    }
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, lerp(l.bajo, l.solColor, 0.3f * (1f - l.noche)).copy(alpha = 0.6f)), horizonte - 24f, horizonte), Offset(0f, horizonte - 24f), Size(ANCHO, 24f))
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
 * Una nube de algodón (cúmulo) apoyada en la línea [y], de [x] a [x] + [ancho]: un montón de borbotones, más alto por
 * el centro, con la base plana. La luz le da por arriba y del lado del sol; la base y el otro lado quedan en sombra
 * violeta. [carga], de 0 a 1: qué tan gris y pesada es. [lejos] la funde con el cielo; [alza] la hace más alta (las
 * torres que crecen sobre el horizonte).
 */
fun DrawScope.nube(l: Luces, x: Float, y: Float, ancho: Float, azar: Azar, carga: Float = 0f, lejos: Float = 0.3f, alza: Float = 1f) {
    val lado = Rayo(l).lat
    val alto = ancho * azar.entre(0.30f, 0.42f) * alza
    val bolas = ArrayList<Pair<Offset, Float>>(); val macizos = ArrayList<Rect>()
    // Tres pisos: muchos borbotones abajo, pocos arriba, corridos hacia un lado.
    val sesgo = azar.entre(-0.14f, 0.14f)
    for (piso in 0..2) {
        val n = listOf(6, 4, 2)[piso] + azar.entero(2)
        val w = ancho * listOf(0.86f, 0.58f, 0.3f)[piso]
        val cx = x + ancho * (0.5f + sesgo * piso)
        for (i in 0 until n) {
            val k = (i + azar.entre(0.2f, 0.8f)) / n
            val rad = ancho * azar.entre(0.085f, 0.14f) * (1f - 0.12f * piso)
            bolas += Offset(cx - w / 2f + w * k, y - rad * 0.55f - alto * (0.36f * piso) * azar.entre(0.8f, 1.15f)) to rad
        }
        // El cuerpo de cada piso, para que entre los borbotones no queden huecos.
        macizos += Rect(cx - w * 0.4f, y - alto * (0.36f * piso) - ancho * 0.05f, cx + w * 0.4f, y)
    }
    val forma = Path().apply {
        bolas.forEach { addOval(Rect(it.first, it.second)) }
        macizos.forEach { addRoundRect(androidx.compose.ui.geometry.RoundRect(it, CornerRadius(ancho * 0.06f))) }
        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(x + ancho * 0.09f, y - alto * 0.2f, x + ancho * 0.91f, y), CornerRadius(alto * 0.1f)))
    }
    val calido = l.solAltura > -0.1f && l.solAltura < 0.5f && l.nublado < 0.5f
    val blanco = lerp(Color.White, Color(0xFF8E97A6), 0.75f * carga)
    val luz = lerp(lerp(l.tono(blanco), l.solColor, if (calido) 0.4f else 0.08f), l.bajo, 0.5f * lejos)
    val sombra = lerp(lerp(l.tono(lerp(Color(0xFFA9A6CA), Color(0xFF59616F), carga)), l.medio, 0.4f), l.bajo, 0.6f * lejos)
    val arriba = bolas.minOf { it.first.y - it.second }
    clipPath(forma) {
        drawRect(Brush.verticalGradient(0f to luz, 0.45f to luz, 0.82f to lerp(luz, sombra, 0.75f), 1f to sombra, startY = arriba, endY = y), Offset(x - ancho * 0.3f, arriba - 2f), Size(ancho * 1.6f, y - arriba + 4f))
        // La sombra propia de cada borbotón, del lado contrario al sol, y un toque de luz en lo alto.
        bolas.forEach { (c, rad) ->
            mancha(c + Offset(-lado * rad * 0.55f, rad * 0.5f), rad * 0.95f, rad * 0.7f, sombra, 0.6f)
            mancha(c + Offset(lado * rad * 0.35f, -rad * 0.45f), rad * 0.8f, rad * 0.6f, luz, 0.85f)
        }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.bajo.copy(alpha = 0.85f * lejos * (1f - 0.6f * carga))), y - alto * 0.2f, y), Offset(x - ancho * 0.3f, y - alto * 0.2f), Size(ancho * 1.6f, alto * 0.2f))
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

/** Velos altos y un banco de borreguillos: el cielo de los días de calma. */
fun DrawScope.velos(l: Luces, horizonte: Float, azar: Azar) {
    val c = lerp(l.tono(Color.White), l.medio, 0.12f)
    repeat(7) {
        val x = azar.entre(-30f, ANCHO - 80f); val y = azar.entre(10f, horizonte * 0.62f); val w = azar.entre(80f, 190f); val sube = azar.entre(6f, 22f)
        for (i in 0..16) {
            val k = i / 16f
            mancha(Offset(x + w * k, y - sube * k * k + azar.entre(-0.8f, 0.8f)), w * 0.09f, (1f + 2.6f * (1f - k)) * azar.entre(0.6f, 1f), c, 0.4f * (1f - 0.55f * k))
        }
    }
    val cx = azar.entre(70f, 250f); val cy = azar.entre(22f, 52f)
    repeat(46) {
        val u = azar.entre(-1f, 1f); val v = azar.entre(-1f, 1f)
        mancha(Offset(cx + u * 78f + v * 16f, cy + v * 13f), azar.entre(4f, 8.5f), azar.entre(1.5f, 2.8f), c, 0.36f * (1f - u * u))
    }
}

/**
 * Las nubes del tiempo que hace. Con el cielo despejado cada día trae las suyas, según su semilla: borregos en filas
 * que se alejan, torres sobre el horizonte, velos altos o unas barras de calima. Con viento, cirros; con «nublado»,
 * cúmulos; con lluvia, tormenta o nieve, el cielo cubierto. Con niebla y con polvo no hay nubes: el velo lo pone
 * `tiempoQuieto`.
 */
fun DrawScope.nubes(l: Luces, horizonte: Float, clima: Clima, semilla: Long) {
    val azar = Azar(Semilla.de(semilla, "nubes"))
    val h = horizonte
    when (clima) {
        Clima.DESPEJADO -> when (azar.entero(4)) {
            0 -> {
                repeat(7) { nube(l, azar.entre(-20f, ANCHO), h * azar.entre(0.80f, 0.90f), azar.entre(16f, 26f), azar, 0f, 0.9f) }
                repeat(4) { nube(l, azar.entre(-30f, ANCHO - 40f), h * azar.entre(0.54f, 0.68f), azar.entre(34f, 52f), azar, 0f, 0.55f) }
                repeat(2) { nube(l, azar.entre(44f, ANCHO - 150f), h * azar.entre(0.36f, 0.48f), azar.entre(64f, 92f), azar, 0f, 0.25f) }
            }
            1 -> {
                var x = azar.entre(-30f, 30f)
                repeat(5) { val w = azar.entre(48f, 86f); nube(l, x, h + 3f, w, azar, 0f, 0.72f, alza = azar.entre(1.3f, 2.1f)); x += w * azar.entre(0.5f, 0.8f) }
                repeat(2) { nube(l, azar.entre(60f, ANCHO - 160f), h * azar.entre(0.36f, 0.6f), azar.entre(30f, 48f), azar, 0f, 0.45f) }
            }
            2 -> { velos(l, h, azar); nube(l, azar.entre(20f, 160f), h * 0.82f, 30f, azar, 0f, 0.8f) }
            else -> repeat(4) {
                val y = h * azar.entre(0.62f, 0.93f); val w = azar.entre(90f, 200f); val x = azar.entre(-40f, ANCHO - 60f)
                for (i in 0..10) mancha(Offset(x + w * i / 10f, y + azar.entre(-0.6f, 0.6f)), w * 0.09f, azar.entre(1.2f, 2.4f), lerp(l.tono(Color.White), l.bajo, 0.3f), 0.5f)
            }
        }
        Clima.VIENTO -> { cirros(l, h, azar); nube(l, azar.entre(40f, ANCHO - 120f), h * 0.82f, 50f, azar, 0f, 0.7f) }
        Clima.NUBES -> {
            repeat(3) { nube(l, azar.entre(-20f, ANCHO - 60f), azar.entre(h * 0.55f, h * 0.9f), azar.entre(46f, 80f), azar, 0.15f, 0.7f) }
            repeat(4) { nube(l, azar.entre(-30f, ANCHO - 90f), azar.entre(h * 0.2f, h * 0.55f), azar.entre(90f, 150f), azar, 0.28f, 0.2f) }
        }
        Clima.LLUVIA -> {
            estrato(l, h * 0.04f, 30f, azar, 0.75f); estrato(l, h * 0.3f, 24f, azar, 0.55f); estrato(l, h * 0.56f, 16f, azar, 0.4f)
        }
        Clima.TORMENTA -> {
            estrato(l, 0f, 40f, azar, 1f); estrato(l, h * 0.26f, 32f, azar, 0.9f); estrato(l, h * 0.52f, 20f, azar, 0.65f)
        }
        Clima.NIEVE -> { estrato(l, 0f, 30f, azar, 0.25f); estrato(l, h * 0.3f, 24f, azar, 0.2f); estrato(l, h * 0.58f, 16f, azar, 0.15f) }
        Clima.NIEBLA -> Unit
        Clima.POLVO -> Unit
        Clima.SIN_CLIMA -> Unit
    }
}
