package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Laboratorio: las dos acciones propias del gato (las del de pixel, `gato17.py`), las dos sentado
// =====================================================================
//
// - «Observa un tubo con grama» (la del de pixel es «observa un matraz»; el cuento no tiene matraces, tiene tubos): saca
//   un tubo de vidrio con tierra gris de arriba y un brote de grama, lo sube a la altura de los ojos, lo gira a un lado y
//   al otro, lo mira de cerca y lo baja. Tapón de corcho, cinta cian sin letras, las raíces blancas a través del vidrio.
// - «Pone gotas con la pipeta» (`t_pipeta`): la pipeta del cuento, «un tubo de vidrio fino con una pera de goma» (la
//   goma, la misma de las pipetas de la mesa del cuadro), sobre una bandeja de tierra gris con un brote: baja la mano,
//   aprieta la pera, cae la gota, la tierra se moja y el brote brilla.
// Lo de vidrio deja ver al gato detrás; la luz le viene de arriba, como la de las lámparas.

private val A_MANTO = Color(0xFF9AA5AE)
private val A_SOMBRA = Color(0xFF748089)
private val A_VIDRIO = Color(0xFFCFE6E2)
private val A_CANTO = Color(0xFFEAF6F4)
private val A_AGUA = Color(0xFF8CC9C6)
private val A_GOMA = Color(0xFFCA5844)
private val A_GOMA_LUZ = Color(0xFFE58A72)
private val A_GOMA_OSC = Color(0xFF8E3A2E)
private val A_TIERRA = Color(0xFF8F8C85)
private val A_TIERRA_OSC = Color(0xFF64615C)
private val A_GRAMA = Color(0xFF6FA04A)
private val A_GRAMA_OSC = Color(0xFF3F6A2E)
private val A_RAIZ = Color(0xFFEDEBE3)
private val A_BANDEJA = Color(0xFF5E666E)
private val A_BANDEJA_LUZ = Color(0xFF8C969E)
private val A_CIAN = Color(0xFF0B8FA1)
private val A_CIAN_LUZ = Color(0xFF52C1CE)
private val A_CORCHO = Color(0xFFB08A5A)
private val A_CORCHO_OSC = Color(0xFF7A5A36)

/** El canto del vidrio: devuelve la luz que le da (de noche también), para que no se pierda en lo oscuro. */
private fun LuzGato.canto(): Color = lerpL(tono(A_CANTO), brillo, 0.35f)

private fun poligonoA(puntos: List<Offset>): Path = Path().apply { moveTo(puntos[0]); for (i in 1 until puntos.size) lineTo(puntos[i]); close() }

/** Una puntita de la mano encima de lo que sostiene, para que se vea que lo agarra. */
private fun DrawScope.zarpaA(luz: LuzGato, en: Offset, r: Float = 1.3f) {
    drawOval(luz.tono(A_MANTO), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f))
    drawOval(luz.tono(A_SOMBRA), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f), style = Stroke(0.25f))
    for (dx in listOf(-0.4f, 0.4f)) drawLine(luz.tono(A_SOMBRA).copy(alpha = 0.8f), en + Offset(dx, -r * 0.55f), en + Offset(dx + 0.1f, r * 0.45f), 0.2f)
}

/** Un brote de grama: dos hojas finas que salen de [pie], la de atrás más oscura; con [brillo], una gota que reluce. */
private fun DrawScope.brote(luz: LuzGato, pie: Offset, hacia: Offset, otra: Offset, brillo: Boolean) {
    drawPath(Path().apply { moveTo(pie); cubicTo(pie + (otra - pie) * 0.3f + Offset(0.3f, 0f), otra - (otra - pie) * 0.3f, otra) }, luz.tono(A_GRAMA_OSC), style = Stroke(0.34f, cap = StrokeCap.Round))
    drawPath(Path().apply { moveTo(pie); cubicTo(pie + (hacia - pie) * 0.35f + Offset(-0.3f, 0f), hacia - (hacia - pie) * 0.25f, hacia) }, luz.tono(A_GRAMA), style = Stroke(0.42f, cap = StrokeCap.Round))
    if (brillo) drawCircle(Color.White.copy(alpha = 0.75f - 0.3f * luz.noche), 0.28f, pie + (hacia - pie) * 0.55f)
}

// ---- Observa un tubo con grama (sentado).

/** Cuánto sube la mano y cuánto se inclina el tubo (en radianes) en cada cuadro. */
private val ALZA = listOf(0.1f, 0.6f, 1f, 1f, 1f, 0.55f)
private val VUELTA = listOf(0.06f, -0.08f, -0.26f, 0.2f, 0f, 0.1f)

private fun poseTubo(c: Int): Pose {
    val q = Math.floorMod(c, 6); val a = ALZA[q]
    // Lo saca, lo sube a los ojos, lo gira a un lado y al otro, se acerca a mirarlo entornando los ojos y lo baja.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.8f + (if (q == 4) 0.7f else 0f), -25.6f - 0.5f * a), morro = 0.32f - 0.36f * a, orejas = 1f,
        ojos = listOf(1f, 1f, 1f, 1f, 0.55f, 0.15f)[q],
        manoC = Offset(11f + 2.2f * a, -16.4f - 8f * a),
        cola2 = listOf(2.3f, 2.5f, 2.7f, 2.6f, 2.8f, 2.4f)[q],
    )
}

/** El tubo: el vidrio con su fondo redondo, la tierra gris, las raíces, el brote, la cinta cian, el tapón y el brillo. */
private val tuboConGrama: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6); val v = VUELTA[q]
    val u = Offset(sin(v), -cos(v)); val n = Offset(cos(v), sin(v))
    val abajo = p.manoC + Offset(1.1f, -0.2f) - u * 3.2f
    val largo = 11.6f; val r = 1.35f
    fun en(a: Float, b: Float) = abajo + u * a + n * b
    val vidrio = Path().apply {
        moveTo(en(largo, -r)); lineTo(en(r, -r))
        cubicTo(en(r * 0.45f, -r), en(0f, -r * 0.55f), en(0f, 0f)); cubicTo(en(0f, r * 0.55f), en(r * 0.45f, r), en(r, r))
        lineTo(en(largo, r)); close()
    }
    drawPath(vidrio, luz.tono(A_VIDRIO).copy(alpha = 0.38f))
    clipPath(vidrio) { muestra(luz, ::en) }
    brote(luz, en(3.9f, 0.1f), en(8.6f, -0.5f), en(7.1f, 0.85f), q == 3)
    // La cinta cian que le da la vuelta (sin letras) y los cantos del vidrio, con su brillo.
    drawPath(poligonoA(listOf(en(9f, -r - 0.05f), en(10.1f, -r - 0.05f), en(10.1f, r + 0.05f), en(9f, r + 0.05f))), luz.tono(A_CIAN))
    drawLine(luz.tono(A_CIAN_LUZ), en(10.05f, -r), en(10.05f, r), 0.25f)
    drawPath(vidrio, luz.canto().copy(alpha = 0.85f), style = Stroke(0.28f))
    drawLine(Color.White.copy(alpha = 0.55f - 0.25f * luz.noche), en(1.6f, -r * 0.5f), en(largo - 1.8f, -r * 0.5f), 0.32f, StrokeCap.Round)
    drawLine(luz.canto(), en(largo, -r - 0.2f), en(largo, r + 0.2f), 0.5f, StrokeCap.Round)
    // El tapón de corcho, un poco más ancho arriba, con sus poros.
    drawPath(poligonoA(listOf(en(largo - 0.4f, -r + 0.1f), en(largo + 1.5f, -r - 0.15f), en(largo + 1.5f, r + 0.15f), en(largo - 0.4f, r - 0.1f))), luz.tono(A_CORCHO))
    drawLine(luz.tono(lerpL(A_CORCHO, Color.White, 0.3f)), en(largo + 1.45f, -r - 0.1f), en(largo + 1.45f, r + 0.1f), 0.3f)
    for ((a, b) in listOf(0.4f to -0.4f, 0.9f to 0.5f)) drawCircle(luz.tono(A_CORCHO_OSC), 0.18f, en(largo + a, b))
    zarpaA(luz, p.manoC + Offset(0.2f, 0.1f), 1.1f)
}

/** Lo de dentro del tubo, recortado al vidrio: la tierra gris con sus grumos, las raíces blancas y el vaho. */
private fun DrawScope.muestra(luz: LuzGato, en: (Float, Float) -> Offset) {
    drawPath(poligonoA(listOf(en(-0.5f, -2f), en(4f, -2f), en(4f, 2f), en(-0.5f, 2f))), luz.tono(A_TIERRA))
    drawLine(luz.tono(A_TIERRA_OSC), en(3.95f, -2f), en(3.95f, 2f), 0.3f)
    for ((a, b) in listOf(2.6f to -0.6f, 1.3f to 0.5f, 3.3f to 0.7f)) drawCircle(luz.tono(A_TIERRA_OSC), 0.22f, en(a, b))
    for ((a, b) in listOf(1f to -0.5f, 1.4f to 0.6f, 2.2f to -0.85f)) drawLine(luz.tono(A_RAIZ).copy(alpha = 0.85f), en(3.8f, 0.1f), en(a, b), 0.16f, StrokeCap.Round)
    for ((a, b) in listOf(7.6f to -0.7f, 6.4f to 0.8f, 10.7f to -0.4f, 5.6f to -0.6f)) drawCircle(luz.canto().copy(alpha = 0.7f), 0.16f, en(a, b))
}

private fun lerpL(a: Color, b: Color, t: Float) = androidx.compose.ui.graphics.lerp(a, b, t)

// ---- Pone gotas con la pipeta (sentado).

/** Cuánto baja la mano hacia la bandeja, cuánto aprieta la pera y cuánto se ha mojado la tierra, en cada cuadro. */
private val BAJA = listOf(0f, 0.6f, 1f, 1f, 0.9f, 0.3f)
private val APRIETA = listOf(0f, 0f, 1f, 0.7f, 0f, 0f)
private val MOJADA = listOf(0f, 0f, 0f, 0f, 0.7f, 1f)

/** Donde cae la gota, en la tierra de la bandeja (bajo la punta de la pipeta con la mano abajo). */
private val GOTA = Offset(15.5f, -3.1f)

private fun posePipeta(c: Int): Pose {
    val q = Math.floorMod(c, 6); val b = BAJA[q]
    // Mira la bandeja con la cabeza baja; baja la mano, aprieta, parpadea cuando cae la gota y la vuelve a subir.
    return Gatuno.SENTADO.copy(
        hombro = Gatuno.SENTADO.hombro + Offset(0.5f * b, 0.3f * b),
        cabeza = Offset(5.2f + 1.1f * b, -25.8f + b), morro = 0.32f + 0.3f * b, orejas = 0.95f,
        ojos = if (q == 4) 0.3f else 1f,
        manoC = Offset(14.2f + 0.8f * b, -15.6f + 1.8f * b),
        cola2 = listOf(2.3f, 2.4f, 2.6f, 2.7f, 2.5f, 2.3f)[q],
    )
}

/** La bandeja de tierra gris en el suelo, el brote, la pipeta en la mano y la gota que cae. */
private val pipetaYBandeja: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    bandeja(luz, MOJADA[q])
    brote(luz, Offset(18.8f, -3.3f), Offset(19.9f, -8.6f), Offset(17.4f, -6.8f), q >= 4)
    val cuello = p.manoC + Offset(0.15f, 0.2f)
    val punta = cuello + Offset(0.34f, 8.6f)
    // El tubo de vidrio fino: el canto, el vidrio, el agua que queda dentro y su brillo; la punta, más fina.
    val nivel = cuello + (punta - cuello) * (1f - listOf(0.62f, 0.62f, 0.55f, 0.5f, 0.5f, 0.5f)[q])
    val afina = cuello + (punta - cuello) * 0.86f
    drawLine(luz.canto().copy(alpha = 0.9f), cuello, afina, 0.85f, StrokeCap.Round)
    drawLine(luz.canto().copy(alpha = 0.9f), afina, punta, 0.42f, StrokeCap.Round)
    drawLine(luz.tono(A_VIDRIO), cuello, afina, 0.55f)
    drawLine(luz.tono(A_AGUA), nivel, afina, 0.45f)
    drawLine(luz.tono(A_AGUA), afina, punta, 0.22f)
    drawLine(Color.White.copy(alpha = 0.6f - 0.25f * luz.noche), cuello + Offset(-0.18f, 0.6f), afina + Offset(-0.18f, -0.4f), 0.14f)
    pera(luz, cuello, APRIETA[q])
    gota(luz, punta, q)
    zarpaA(luz, cuello + Offset(0f, 0.2f))
}

/** La pera de goma, arriba de la pipeta: se estrecha y se alarga cuando la aprieta. */
private fun DrawScope.pera(luz: LuzGato, cuello: Offset, aprieta: Float) {
    val w = 2.1f * (1f - 0.32f * aprieta); val h = 3f * (1f + 0.1f * aprieta)
    val o = cuello + Offset(-w / 2f, -h - 0.3f)
    drawOval(luz.tono(A_GOMA_OSC), o, Size(w, h))
    drawOval(luz.tono(A_GOMA), o, Size(w * 0.86f, h * 0.9f))
    drawOval(luz.tono(A_GOMA_LUZ), o + Offset(w * 0.2f, h * 0.12f), Size(w * 0.3f, h * 0.32f))
    drawLine(luz.tono(A_GOMA_OSC), cuello + Offset(-0.5f, -0.35f), cuello + Offset(0.5f, -0.35f), 0.5f, StrokeCap.Round)
}

/** La gota: se forma en la punta, cae, y al llegar salpica la tierra. */
private fun DrawScope.gota(luz: LuzGato, punta: Offset, q: Int) {
    val agua = luz.tono(A_AGUA); val brillo = Color.White.copy(alpha = 0.7f - 0.3f * luz.noche)
    when (q) {
        2 -> { drawCircle(agua, 0.42f, punta + Offset(0f, 0.45f)); drawCircle(brillo, 0.13f, punta + Offset(-0.12f, 0.3f)) }
        3 -> { val o = Offset(punta.x, punta.y + (GOTA.y - punta.y) * 0.6f); drawOval(agua, o - Offset(0.33f, 0.5f), Size(0.66f, 1f)); drawCircle(brillo, 0.12f, o + Offset(-0.1f, -0.2f)) }
        4 -> for ((dx, dy) in listOf(-1.1f to -0.6f, 1f to -0.8f, 0.2f to -1.2f)) drawCircle(agua, 0.2f, GOTA + Offset(dx, dy))
    }
}

/** La bandeja en el suelo: la boca vista desde arriba, la tierra gris con sus grumos, el frente en sombra con su cinta. */
private fun DrawScope.bandeja(luz: LuzGato, mojada: Float) {
    val x0 = 9.2f; val x1 = 22.4f; val frente = -2f; val fondo = -4.4f
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(x0 - 0.8f, -1.4f), Size(x1 - x0 + 2.2f, 1.8f))
    drawPath(poligonoA(listOf(Offset(x0, frente), Offset(x1, frente), Offset(x1 + 0.6f, fondo), Offset(x0 + 0.6f, fondo))), luz.tono(A_BANDEJA_LUZ))
    drawPath(poligonoA(listOf(Offset(x0 + 0.5f, frente - 0.35f), Offset(x1 - 0.5f, frente - 0.35f), Offset(x1 + 0.1f, fondo + 0.35f), Offset(x0 + 1.1f, fondo + 0.35f))), luz.tono(A_TIERRA))
    for ((gx, gy) in listOf(11.4f to -2.9f, 13.6f to -3.7f, 17f to -2.8f, 20.6f to -3.5f, 12.6f to -2.5f, 16.4f to -3.9f)) drawCircle(luz.tono(A_TIERRA_OSC), 0.3f, Offset(gx, gy))
    if (mojada > 0f) drawOval(luz.tono(A_TIERRA_OSC).copy(alpha = 0.8f), Offset(GOTA.x - 1.4f * mojada, GOTA.y - 0.45f * mojada), Size(2.8f * mojada, 0.9f * mojada))
    // El costado de la derecha y el frente, más oscuros (la luz viene de arriba), con la cinta cian del frente.
    drawPath(poligonoA(listOf(Offset(x1, frente), Offset(x1 + 0.6f, fondo), Offset(x1 + 0.6f, fondo + 2f), Offset(x1, -0.05f))), luz.tono(lerpL(A_BANDEJA, Color.Black, 0.2f)))
    drawRect(luz.tono(A_BANDEJA), Offset(x0, frente), Size(x1 - x0, -0.05f - frente))
    drawLine(luz.tono(A_BANDEJA_LUZ), Offset(x0, frente + 0.12f), Offset(x1, frente + 0.12f), 0.25f)
    drawLine(luz.tono(A_CIAN), Offset(x0 + 1.2f, -1f), Offset(x0 + 4.4f, -1f), 0.75f)
}

/** Las dos acciones del gato de Laboratorio, en el orden del de pixel. */
internal val ACCIONES_LABORATORIO = listOf(
    EspecialGato("observa un tubo con grama", true, 6, ::poseTubo, tuboConGrama),
    EspecialGato("pone gotas con la pipeta", true, 6, ::posePipeta, pipetaYBandeja),
)
