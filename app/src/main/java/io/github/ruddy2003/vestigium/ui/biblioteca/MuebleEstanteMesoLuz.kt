package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.exp
import kotlin.math.sqrt

// La luz del cuarto de guardar (MuebleEstanteMeso), compartida por el mueble y los volúmenes: las tres lumbres de la noche
// (el nicho, el fogón y la tea del poste), la luna (la mancha chica o el plano de plata de los Sueños), la luz de la puerta
// de día, la luz que le toca a cada volumen y el color del ánimo, llevado al mundo del cuarto.

/** Una luz que cae con la distancia [d] (en anchos) y se acaba del todo a su [alcance]. */
private fun caidaMeso(d: Float, a: Float, e: Float, alcance: Float): Float {
    val fin = a * exp(-e * alcance)
    return ((a * exp(-e * d) - fin) / (1f - fin)).coerceIn(0f, 1f)
}

/** Cuánta luz de la lumbre del nicho llega a un punto a [dx] y [dy] anchos de ella. Con [baja] (los Sueños de noche), menos. */
internal fun nichoLuzMeso(dx: Float, dy: Float, baja: Boolean = false): Float {
    val d = sqrt(dx * dx + dy * dy)
    return if (baja) caidaMeso(d, 1.0f, 4.4f, 0.6f) else caidaMeso(d, 1.6f, 2.5f, 1.15f)
}

/** Lo mismo para el fogón, que arde más grande. */
internal fun fogonLuzMeso(dx: Float, dy: Float, baja: Boolean = false): Float {
    val d = sqrt(dx * dx + dy * dy)
    return if (baja) caidaMeso(d, 1.0f, 4.0f, 0.7f) else caidaMeso(d, 1.7f, 2.4f, 1.2f)
}

/** La de la tea del poste, chica: alumbra el hueco de en medio y algo de los de al lado. */
internal fun teaLuzMeso(dx: Float, dy: Float): Float = caidaMeso(sqrt(dx * dx + dy * dy), 1.2f, 3.4f, 0.55f)

/** Cuánta luz de una lumbre de [clase] (0, el nicho; 1, el fogón; 2, la tea) llega a [d] anchos de ella. */
internal fun luzLumbreMeso(clase: Int, d: Float, baja: Boolean): Float = when (clase) {
    0 -> nichoLuzMeso(d, 0f, baja)
    1 -> fogonLuzMeso(d, 0f, baja)
    else -> teaLuzMeso(d, 0f)
}

/** El alcance de cada lumbre, en anchos (donde su luz ya es cero). */
internal fun alcanceLumbreMeso(clase: Int, baja: Boolean): Float = when (clase) {
    0 -> if (baja) 0.6f else 1.15f
    1 -> if (baja) 0.7f else 1.2f
    else -> 0.55f
}

/** Dos luces juntas: lo que no deja pasar ninguna de las dos. */
internal fun juntarLucesMeso(k1: Float, k2: Float): Float = 1f - (1f - k1) * (1f - k2)

/**
 * El plano de luna de los Sueños de noche, en anchos desde la esquina de arriba a la izquierda: entra por la puerta (a la
 * izquierda), da en el testero bajo la viga y baja en diagonal cruzando el mueble hacia la derecha.
 */
private val PUNTOS_LUNA_MESO = floatArrayOf(0f, 0.27f, 0.4f, 0.32f, 1.1f, 1.48f, 0.56f, 1.62f, 0f, 0.98f)

internal fun formaLunaMeso(w: Float): Path {
    val q = PUNTOS_LUNA_MESO
    return poligonoMeso(-2f, q[1] * w, q[2] * w, q[3] * w, q[4] * w, q[5] * w, q[6] * w, q[7] * w, -2f, q[9] * w)
}

/** Si un punto (en anchos) cae en el plano de luna, con su borde un poco blando: de 0 a 1. */
private fun enPlanoLunaMeso(x: Float, y: Float): Float {
    val q = PUNTOS_LUNA_MESO
    val n = q.size / 2
    var dentro = false
    var borde = Float.MAX_VALUE
    for (i in 0 until n) {
        val ax = q[2 * i]; val ay = q[2 * i + 1]
        val bx = q[(2 * i + 2) % q.size]; val by = q[(2 * i + 3) % q.size]
        if ((ay > y) != (by > y) && x < ax + (bx - ax) * (y - ay) / (by - ay)) dentro = !dentro
        val ex = bx - ax; val ey = by - ay
        val largo = ex * ex + ey * ey
        val t = if (largo < 1e-6f) 0f else (((x - ax) * ex + (y - ay) * ey) / largo).coerceIn(0f, 1f)
        val px = ax + ex * t - x; val py = ay + ey * t - y
        borde = minOf(borde, sqrt(px * px + py * py))
    }
    return if (dentro) (borde / 0.03f).coerceIn(0f, 1f) else 0f
}

/**
 * La luna (de noche). En el oscuro de siempre, una mancha chica de borde blando arriba a la izquierda, donde de día da el sol;
 * en los Sueños ([lunar]), el plano de plata que cruza el mueble.
 */
internal fun lunaMeso(x: Float, y: Float, lunar: Boolean): Float {
    if (lunar) return enPlanoLunaMeso(x, y)
    val dx = x - MuebleEstanteMeso.LUNA_X
    val dy = y - MuebleEstanteMeso.LUNA_Y
    return ((1f - sqrt(dx * dx + dy * dy) / 0.42f) * 0.85f).coerceIn(0f, 1f)
}

/**
 * La mancha de la puerta en el testero, en anchos desde la esquina de arriba a la izquierda: la forma de la puerta proyectada,
 * con el borde de arriba (el dintel) y el de la derecha (la jamba) rectos y decididos, que sigue por el anaquel. Con [ocaso]
 * (los Sueños en claro), el sol ya bajo: más larga y más baja.
 */
private fun puntosPuertaMeso(ocaso: Boolean): FloatArray =
    if (ocaso) floatArrayOf(0f, 0.3f, 0.62f, 0.4f, 1.05f, 1.45f, 0f, 1.3f)
    else floatArrayOf(0f, 0.232f, 0.46f, 0.3f, 0.7f, 1.02f, 0f, 1.08f)

internal fun formaPuertaMeso(w: Float, ocaso: Boolean): Path {
    val q = puntosPuertaMeso(ocaso)
    return poligonoMeso(-2f, q[1] * w, q[2] * w, q[3] * w, q[4] * w, q[5] * w, -2f, q[7] * w)
}

/** Cuánto da la luz de la puerta (de 0 a 1) en un punto a [x] e [y] anchos de la esquina: 1 dentro, con el borde un poco suave. */
internal fun luzPuertaMeso(x: Float, y: Float, ocaso: Boolean): Float {
    val q = puntosPuertaMeso(ocaso)
    if (x < 0f) return 0f
    val arriba = q[1] + (q[3] - q[1]) * x / q[2]
    val abajo = q[7] + (q[5] - q[7]) * x / q[4]
    val derecha = q[2] + (q[4] - q[2]) * ((y - q[3]) / (q[5] - q[3])).coerceIn(0f, 1f)
    return minOf((y - arriba) / 0.025f, (abajo - y) / 0.035f, (derecha - x) / 0.03f).coerceIn(0f, 1f)
}

/**
 * La luz de las lumbres, de la puerta y de qué lado le llega la de las lumbres a un volumen; [fogon], si la que más le llega
 * es la del fogón (ámbar rojizo, desde abajo) y no la del nicho.
 */
internal class LuzEnVolumenMeso(val k: Float, val f: Float, val der: Boolean, val fogon: Boolean = false)

internal fun luzVolumenMeso(v: Volumen, oscuro: Boolean): LuzEnVolumenMeso {
    val m = MuebleEstanteMeso
    val fx = m.LADO + v.centro * (1f - 2 * m.LADO)
    val y = m.CORONA + (v.repisa + 0.55f) * m.FILA / m.ANCHO_NOMINAL
    if (!oscuro && !v.noche) return LuzEnVolumenMeso(1f, luzPuertaMeso(fx, y, false), false)
    val baja = oscuro && v.noche
    val base = m.CORONA + v.repisas * m.FILA / m.ANCHO_NOMINAL
    val k1 = nichoLuzMeso(fx - m.NICHO_X, y - m.NICHO_Y, baja)
    val k2 = fogonLuzMeso(fx - m.FOGON_X, y - (base + m.FOGON_BAJO), baja)
    // La tea del poste (solo de noche fuera de los Sueños), a la altura de su hueco.
    val hueco = m.teaHueco(v.repisas)
    val k3 = if (oscuro && !baja && hueco >= 0) teaLuzMeso(fx - m.TEA_X, y - (m.CORONA + (hueco + m.TEA_EN_HUECO) * m.FILA / m.ANCHO_NOMINAL)) else 0f
    val k = juntarLucesMeso(juntarLucesMeso(k1, k2), k3)
    val f = if (oscuro) lunaMeso(fx, y, baja) else luzPuertaMeso(fx, y, true)
    val lx = if (k3 > k1 && k3 > k2) m.TEA_X else if (k1 > k2) m.NICHO_X else m.FOGON_X
    return LuzEnVolumenMeso(k, f, oscuro && fx < lx - 0.02f, oscuro && k2 > k1 && k2 > k3)
}

/** Las escalas del ánimo de Mesoamérica, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_MESO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.MESO, false) to juegoTonos(TemaId.MESO, true) }

private fun cuadradoMeso(x: Float) = x * x

/**
 * Dónde cae [t] en la escala [escala] (de bajo a alto, de 0 a 1, el neutro en 0.5): el tramo y la fracción que más se le
 * acercan; -1 si no está en la escala.
 */
private fun enEscalaMeso(t: Color, escala: List<Color>): Float {
    if (escala.size < 3) return -1f
    var mejor = -1f
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoMeso(t.red - a0.red - dr * f) + cuadradoMeso(t.green - a0.green - dg * f) + cuadradoMeso(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el neutro pasa del amate a un
 * casi negro, pensado para la interfaz) y un volumen claro se volvía negro de noche. Aquí el volumen es un objeto del cuarto:
 * conserva su color de día y la luz lo apaga con la distancia a las lumbres. Se ubica el color en la escala de noche y se toma
 * el mismo punto en la de día; si no está en la escala, queda igual.
 */
internal fun tonoDeDiaMeso(t: Color, oscuro: Boolean): Color {
    if (!oscuro) return t
    val (dia, noche) = TONOS_MESO
    val x = enEscalaMeso(t, noche)
    if (x < 0f || dia.size < 3) return t
    val s = x * 2f
    val i = s.toInt().coerceIn(0, 1)
    return mezcla(dia[i], dia[i + 1], s - i)
}

/**
 * El ánimo con los colores del mundo, con cuerpo: se ubica el color (ya de día) en la escala del tema (de 0 a 1, el neutro en
 * 0.5) y se toma el mismo punto en [MesoTinta.ANIMO]. Lo que se aparta poco del neutro se aparta un poco más (una curva que
 * conserva el orden y el neutro), para que un ánimo templado no quede en un pastel lavado. Si no está en la escala, queda igual.
 */
internal fun animoMundoMeso(t: Color): Color {
    val x0 = enEscalaMeso(t, TONOS_MESO.first)
    if (x0 < 0f) return t
    val d = (x0 - 0.5f) * 2f
    val x = 0.5f + 0.5f * kotlin.math.sign(d) * Math.pow(kotlin.math.abs(d).toDouble(), 0.62).toFloat()
    val e = MesoTinta.ANIMO
    val s = x * (e.size - 1)
    val i = s.toInt().coerceIn(0, e.size - 2)
    return mezcla(e[i], e[i + 1], s - i)
}
