package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor

// =====================================================================
//  Las dos acciones del gato de Tierras Antiguas (ver `PelajeAntiguas.kt`)
// =====================================================================
//
// - «consulta el mapa» (sentado), la del de pixel, con el mapa del cuento: «a mano, con tinta de agallas, en piel de
//   cabra raspada; no se pinta el camino, que cambia, sino las piedras marcadas». Lo abre entre sus dos rollos, va de
//   piedra en piedra, alza la cabeza hacia el paso para comparar y vuelve al mapa. Sin lagos, sin dragones, sin la X y sin
//   rosa de los vientos (lo que trae el fondo del de pixel): solo la cresta con su muesca, las piedras una tras otra y la
//   posada abajo.
// - «alza la linterna de mina» (sentado) **[Diseño propio]**: el de pixel «alza una gema que brilla», pero la gema no sale
//   en el cuento y es justo lo que el calendario pide no pintar. Se queda el gesto (levantar algo en alto) con la linterna
//   de Brenna, «de hierro y de cuerno raspado tan fino que deja pasar la luz», encendida, con su luz amarilla. Otra
//   opción para el autor: el cuerno de guía (alzarlo y soplarlo).

private val VITELA = Color(0xFFE9DFC4)
private val VITELA_OSC = Color(0xFFC9B892)
private val ROLLO = Color(0xFF8A6A3E)
private val ROLLO_LUZ = Color(0xFFC2A270)
private val ROLLO_OSC = Color(0xFF54401F)
private val AGALLAS = Color(0xFF3A2A20)
private val HIERRO = Color(0xFF332E33)
private val HIERRO_LUZ = Color(0xFF8A8890)
private val CUERNO_LUZ = Color(0xFFFFE2A0)
private val LUMBRE = Color(0xFFFFAE58)
private val ASCUA = Color(0xFFFFF0C8)

// ---- Consulta el mapa (sentado).

/** Qué tan abierto tiene el mapa en cada cuadro (el ancho entre los dos rollos) y adónde mira. */
private val ANCHO_MAPA = listOf(7.6f, 11f, 11f, 11f, 11f, 9f)
private val MORRO_MAPA = listOf(0.35f, 0.5f, 0.56f, -0.36f, 0.5f, 0.3f)
private val OJOS_MAPA = listOf(0.85f, 0.6f, 0.5f, 1f, 0.15f, 0.8f)
private val CENTRO_MAPA = Offset(13.2f, -14.4f)
private const val ALTO_MAPA = 7.4f

/** Dónde lo agarra: la mano de este lado, del rollo de dentro; la del otro, del de fuera. */
private fun manosEnElMapa(q: Int): Pair<Offset, Offset> {
    val a = ANCHO_MAPA[q]; val c = CENTRO_MAPA
    return Offset(c.x - a / 2f + 0.7f, c.y + ALTO_MAPA / 2f + 0.3f) to Offset(c.x + a / 2f - 0.6f, c.y + ALTO_MAPA / 2f + 0.1f)
}

internal fun poseMapa(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val (mano, otra) = manosEnElMapa(q)
    val sigue = listOf(0f, 0f, 0.6f, 0f, 0.3f, 0f)[q]
    return Gatuno.SENTADO.copy(
        cabeza = if (q == 3) Offset(5f, -26.4f) else Offset(5.4f + sigue, -25.2f), morro = MORRO_MAPA[q], ojos = OJOS_MAPA[q],
        orejas = if (q == 3) 1f else 0.9f, manoC = mano, manoL = otra, cola2 = listOf(2.3f, 2.4f, 2.6f, 2.7f, 2.5f, 2.3f)[q],
    )
}

/** El mapa abierto entre sus dos rollos, con su sombra en el pecho, y las dos manos que lo sostienen. */
internal val mapaDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val a = ANCHO_MAPA[q]
    val piel = piel(a)
    drawPath(Path().apply { addPath(piel, Offset(-0.8f, 0.9f)) }, Color(0xFF241C3A).copy(alpha = 0.22f))
    val t = CENTRO_MAPA.y - ALTO_MAPA / 2f
    drawPath(piel, Brush.verticalGradient(listOf(luz.tono(VITELA), luz.tono(VITELA_OSC)), t, t + ALTO_MAPA))
    clipPath(piel) { raspado(luz, a); tinta(luz, a) }
    drawPath(piel, luz.tono(ROLLO).copy(alpha = 0.5f), style = Stroke(0.25f))
    rollo(luz, CENTRO_MAPA.x - a / 2f)
    rollo(luz, CENTRO_MAPA.x + a / 2f)
    val (mano, otra) = manosEnElMapa(q)
    dedosAg(luz, otra + Offset(0f, -0.4f), 1.1f)
    dedosAg(luz, mano + Offset(0f, -0.5f))
}

/** La piel de cabra entre los rollos: la orilla de arriba y la de abajo, desiguales, como las de una piel. */
private fun piel(a: Float): Path {
    val l = CENTRO_MAPA.x - a / 2f; val r = CENTRO_MAPA.x + a / 2f
    val t = CENTRO_MAPA.y - ALTO_MAPA / 2f; val b = CENTRO_MAPA.y + ALTO_MAPA / 2f
    return Path().apply {
        moveTo(l, t + 0.2f)
        cubicTo(Offset(l + a * 0.2f, t - 0.4f), Offset(l + a * 0.4f, t + 0.3f), Offset(l + a * 0.55f, t + 0.05f))
        cubicTo(Offset(l + a * 0.7f, t - 0.25f), Offset(l + a * 0.85f, t - 0.3f), Offset(r, t + 0.15f))
        lineTo(r, b)
        cubicTo(Offset(l + a * 0.8f, b + 0.45f), Offset(l + a * 0.6f, b - 0.1f), Offset(l + a * 0.45f, b + 0.05f))
        cubicTo(Offset(l + a * 0.3f, b + 0.25f), Offset(l + a * 0.15f, b + 0.3f), Offset(l, b - 0.1f))
        close()
    }
}

/** Lo raspado de la piel: manchas más claras y la orilla más oscura. */
private fun DrawScope.raspado(luz: LuzGato, a: Float) {
    val c = CENTRO_MAPA
    for ((d, w) in listOf(Offset(-2.6f, -1.2f) to 2.6f, Offset(1.8f, 1.6f) to 2f, Offset(3.4f, -2f) to 1.6f)) {
        if (kotlin.math.abs(d.x) + w / 2f < a / 2f) drawOval(Color.White.copy(alpha = 0.18f * (1f - 0.5f * luz.noche)), Offset(c.x + d.x - w / 2f, c.y + d.y - w * 0.3f), Size(w, w * 0.6f))
    }
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, luz.tono(ROLLO).copy(alpha = 0.25f)), c.y + 2f, c.y + ALTO_MAPA / 2f + 0.5f), Offset(c.x - 8f, c.y + 2f), Size(16f, 2.4f))
}

/**
 * Lo dibujado con tinta de agallas: la cresta con la muesca del collado y su sombreado, la posada abajo y, entre las dos,
 * las piedras marcadas una tras otra, cada una con su rayita (sin el camino, que cambia). Con el mapa a medio abrir solo
 * se ve lo del medio.
 */
private fun DrawScope.tinta(luz: LuzGato, a: Float) {
    val tinta = luz.tono(AGALLAS).copy(alpha = 0.82f)
    val c = CENTRO_MAPA
    val cresta = listOf(Offset(-4.6f, -1.3f), Offset(-2.6f, -3.1f), Offset(-0.6f, -1.8f), Offset(1f, -2.6f), Offset(3f, -1.2f), Offset(4.6f, -1.6f)).map { c + it }
    drawPath(Path().apply { moveTo(cresta.first()); for (p in cresta.drop(1)) lineTo(p) }, tinta, style = Stroke(0.3f, cap = StrokeCap.Round))
    for (i in 0..3) { val x = c.x - 3.9f + i * 0.55f; drawLine(tinta.copy(alpha = 0.5f), Offset(x, c.y - 1.9f + i * 0.1f), Offset(x - 0.25f, c.y - 1.1f), 0.18f) }
    val piedras = listOf(Offset(0.6f, 2.7f), Offset(2.4f, 1.9f), Offset(-0.5f, 1.2f), Offset(1.8f, 0.4f), Offset(-0.7f, -0.4f), Offset(0.6f, -1.2f))
    for (d in piedras) {
        val o = c + d
        drawCircle(tinta, 0.34f, o)
        drawLine(tinta, o + Offset(0.55f, -0.5f), o + Offset(0.75f, 0.15f), 0.18f, StrokeCap.Round)
    }
    // La posada, abajo a la izquierda: un tejadito y su puerta.
    val casa = c + Offset(-3f, 2.4f)
    drawPath(Path().apply { moveTo(casa + Offset(-0.9f, 0f)); lineTo(casa + Offset(0f, -0.8f)); lineTo(casa + Offset(1.1f, 0f)) }, tinta, style = Stroke(0.22f, cap = StrokeCap.Round))
    drawRect(tinta, casa + Offset(-0.7f, 0f), Size(1.6f, 0.9f), style = Stroke(0.2f))
}

/** Un rollo de la piel, de pie en [x]: el cilindro con su luz y la espiral de la piel arriba. */
private fun DrawScope.rollo(luz: LuzGato, x: Float) {
    val t = CENTRO_MAPA.y - ALTO_MAPA / 2f - 0.5f
    drawRoundRect(Brush.horizontalGradient(listOf(luz.tono(ROLLO_LUZ), luz.tono(ROLLO), luz.tono(ROLLO_OSC)), x - 0.85f, x + 0.85f), Offset(x - 0.85f, t), Size(1.7f, ALTO_MAPA + 1f), CornerRadius(0.6f))
    drawOval(luz.tono(VITELA_OSC), Offset(x - 0.85f, t - 0.35f), Size(1.7f, 0.8f))
    drawOval(luz.tono(ROLLO_OSC), Offset(x - 0.42f, t - 0.15f), Size(0.84f, 0.4f), style = Stroke(0.16f))
}

// ---- Alza la linterna de mina (sentado).

/** Dónde tiene la mano en cada cuadro (la linterna cuelga de ella por el asa) y cuánto se mece la linterna (grados). */
private val MANO_LINTERNA = listOf(Offset(11.6f, -13.4f), Offset(14.6f, -21.6f), Offset(17f, -29f), Offset(17.4f, -29.6f), Offset(17.2f, -29.2f), Offset(14.8f, -22.8f))
private val MECE = listOf(0f, 6f, -8f, 10f, -5f, 4f)

internal fun poseLinterna(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val alta = q in 2..4
    return Gatuno.SENTADO.copy(
        hombro = if (alta) Offset(2.8f, -17f) else Gatuno.SENTADO.hombro,
        cabeza = listOf(Offset(5.2f, -25.4f), Offset(5f, -25.8f), Offset(4.6f, -26.4f), Offset(4.6f, -26.6f), Offset(4.6f, -26.4f), Offset(5f, -25.8f))[q],
        morro = listOf(0.35f, 0f, -0.3f, -0.36f, -0.28f, 0.1f)[q], ojos = listOf(0.9f, 1f, 1f, 0.55f, 1f, 0.9f)[q], orejas = if (alta) 1f else 0.9f,
        manoC = MANO_LINTERNA[q], manoL = Offset(6.2f, 0f), cola2 = listOf(2.3f, 2.5f, 2.7f, 2.8f, 2.6f, 2.4f)[q],
    )
}

/** La linterna encendida colgada de la mano, que se mece; su luz alrededor; y la mano en el asa. */
internal val linternaDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val asa = MANO_LINTERNA[q] + Offset(0.3f, -0.2f)
    rotate(MECE[q], asa) { linterna(luz, asa) }
    dedosAg(luz, MANO_LINTERNA[q] + Offset(0.2f, -0.3f), 1.15f)
}

/**
 * La linterna de mina, colgada de [asa]: el aro, la tapa en cono, el cuerpo de cuerno raspado encendido de ámbar con las
 * varillas de hierro, la base, y su resplandor (más fuerte de noche). La luz es suya: no la apaga la de la escena.
 */
private fun DrawScope.linterna(luz: LuzGato, asa: Offset) {
    val arriba = asa + Offset(0f, 0.7f)
    val w = 2.6f; val h = 4.4f; val y0 = arriba.y + 1.1f
    val centro = Offset(asa.x, y0 + h * 0.5f)
    resplandor(centro, 10f, 0.25f + 0.5f * luz.noche, LUMBRE)
    val cuerpo = Path().apply { moveTo(asa.x - w * 0.42f, y0); lineTo(asa.x + w * 0.42f, y0); lineTo(asa.x + w * 0.5f, y0 + h); lineTo(asa.x - w * 0.5f, y0 + h); close() }
    drawPath(cuerpo, Brush.verticalGradient(listOf(CUERNO_LUZ, LUMBRE), y0, y0 + h))
    clipPath(cuerpo) {
        // La veta del cuerno raspado, que deja pasar la luz desigual.
        for (dy in listOf(0.9f, 2.1f, 3.3f)) drawLine(Color(0xFFC07A30).copy(alpha = 0.3f), Offset(asa.x - w, y0 + dy), Offset(asa.x + w, y0 + dy + 0.3f), 0.25f)
        drawCircle(ASCUA.copy(alpha = 0.75f), 0.95f, centro + Offset(0f, 0.4f), blendMode = BlendMode.Plus)
    }
    val hierro = luz.tono(HIERRO); val filo = luz.tono(HIERRO_LUZ)
    for (dx in listOf(-0.46f, 0f, 0.46f)) drawLine(hierro.copy(alpha = if (dx == 0f) 0.55f else 0.95f), Offset(asa.x + w * dx * 0.9f, y0), Offset(asa.x + w * dx, y0 + h), 0.3f)
    drawPath(Path().apply { moveTo(asa.x - w * 0.55f, y0 + 0.1f); lineTo(asa.x + w * 0.55f, y0 + 0.1f); lineTo(asa.x + w * 0.2f, arriba.y); lineTo(asa.x - w * 0.2f, arriba.y); close() }, hierro)
    drawLine(filo.copy(alpha = 0.7f), Offset(asa.x - w * 0.45f, y0 - 0.05f), Offset(asa.x - w * 0.15f, arriba.y + 0.15f), 0.2f)
    drawRect(hierro, Offset(asa.x - w * 0.58f, y0 + h), Size(w * 1.16f, 0.75f))
    drawLine(filo.copy(alpha = 0.6f), Offset(asa.x - w * 0.5f, y0 + h + 0.15f), Offset(asa.x + w * 0.3f, y0 + h + 0.15f), 0.18f)
    drawCircle(hierro, 0.6f, asa, style = Stroke(0.28f))
}

/** Las dos acciones del gato de Tierras Antiguas, las del de pixel (la gema, cambiada por la linterna). Van al final: usan
 * lo de arriba, que tiene que estar listo antes. */
internal val ACCIONES_ANTIGUAS = listOf(
    EspecialGato("consulta el mapa", true, 6, ::poseMapa, mapaDelGato),
    EspecialGato("alza la linterna de mina", true, 6, ::poseLinterna, linternaDelGato),
)
