package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Las dos acciones del gato de Refugio (ver `PelajeRefugio.kt`)
// =====================================================================
//
// - «bebe un sorbo de agua»: la botella de vidrio de tapón rojo de la residente (la misma de la bancada del cuadro).
// - «repara con la llave»: la llave de válvulas del cuento contra la rueda roja de una llave de paso que gotea.
// Los colores son los de la sala de bombas del cuadro (`ColorRefugio`): el verde de las máquinas, el rojo de las ruedas,
// el hierro, la cinta del mango y el agua.

private val RA_VIDRIO = Color(0xFFCFE6EC)
private val RA_AGUA = Color(0xFF83BCE0)
private val RA_AGUA_HONDA = Color(0xFF5690BB)
private val RA_ROJO = Color(0xFFB53A2D)
private val RA_ROJO_LUZ = Color(0xFFDE6A52)
private val RA_ROJO_OSC = Color(0xFF7A2219)
private val RA_VERDE = Color(0xFF52806A)
private val RA_VERDE_LUZ = Color(0xFF86A88A)
private val RA_VERDE_OSC = Color(0xFF2F4D44)
private val RA_HIERRO = Color(0xFF3A4446)
private val RA_ACERO = Color(0xFF8A9294)
private val RA_CINTA = Color(0xFF2F3F49)
private val RA_MOJADO = Color(0xFF2A3A44)

private fun rad(grados: Float) = grados * PI.toFloat() / 180f

// ---- Bebe un sorbo de agua (sentado).

/** Cuánto inclina la botella en cada cuadro (grados: 0 de pie; negativo, el gollete hacia la boca) y cuánto baja el hocico. */
private val GIRO_BOTELLA = listOf(0f, -40f, -100f, -112f, -38f, 0f)
private val MORRO_BOTELLA = listOf(0.32f, 0.05f, -0.25f, -0.3f, 0.25f, 0.34f)
private val CABEZA_BEBE = Offset(5.4f, -25.8f)

/** Del agarre (el medio de la botella) al culo, a donde se estrecha y a la boca; y su radio. */
private const val B_CULO = 3.4f
private const val B_HOMBRO = -2.6f
private const val B_BOCA = 5f
private const val B_RADIO = 1.75f

/** La boca del gato de perfil con el hocico bajado [morro] (el punto que pinta el motor: 0,8 radios hacia el hocico y 0,56 hacia abajo). */
private fun bocaDelGato(morro: Float): Offset {
    val px = 0.8f * Gatuno.R_CABEZA; val py = 0.56f * Gatuno.R_CABEZA
    return CABEZA_BEBE + Offset(px * cos(morro) - py * sin(morro), px * sin(morro) + py * cos(morro))
}

/** Hacia dónde apunta la botella, del culo a la boca, inclinada [grados]. */
private fun ejeBotella(grados: Float) = Offset(sin(rad(grados)), -cos(rad(grados)))

/** Dónde la agarra en el cuadro [q]: en el pecho, a medio subir, o con la boca de la botella en la suya. */
private fun agarre(q: Int): Offset = when (q) {
    1 -> Offset(13.8f, -17.6f)
    2, 3 -> bocaDelGato(MORRO_BOTELLA[q]) - ejeBotella(GIRO_BOTELLA[q]) * B_BOCA
    4 -> Offset(13.6f, -16.4f)
    else -> Offset(11.6f, -11.8f)
}

/** Dónde van sus dos manos en la botella: la de este lado y la del otro (las dos por el lado del gato, que es abajo al beber). */
private fun manosEnLaBotella(q: Int): Pair<Offset, Offset> {
    val g = agarre(q); val eje = ejeBotella(GIRO_BOTELLA[q]); val n = Offset(-eje.y, eje.x)
    return (g - n * 1.6f + eje * 0.6f) to (g - n * 1.4f - eje * 2.1f)
}

internal fun poseBotella(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val (mano, otra) = manosEnLaBotella(q)
    val bebe = q == 2 || q == 3
    return Gatuno.SENTADO.copy(
        cabeza = if (q == 0 || q == 5) Offset(5f, -25.4f) else CABEZA_BEBE, morro = MORRO_BOTELLA[q], orejas = if (bebe) 0.85f else 1f,
        ojos = if (bebe) 0.12f else if (q == 4) 1f else 0.8f, manoC = mano, manoL = otra,
        cola2 = listOf(2.3f, 2.4f, 2.6f, 2.7f, 2.5f, 2.3f)[q],
    )
}

/** La botella, el tapón (puesto, o en el suelo mientras bebe), el agua que se le escapa y sus manos. */
internal val botellaDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val tapada = q == 0 || q == 5
    if (!tapada) {
        drawOval(Color.Black.copy(alpha = 0.18f), Offset(18.6f, -0.5f), Size(2.2f, 0.8f))
        tapon(luz, Offset(19.6f, -0.5f))
    }
    val g = agarre(q); val giro = GIRO_BOTELLA[q]
    withTransform({ translate(g.x, g.y); rotate(giro, Offset.Zero) }) { botella(luz, giro, tapada) }
    if (q == 2 || q == 3) sorbo(luz, bocaDelGato(MORRO_BOTELLA[q]), q)
    val (mano, otra) = manosEnLaBotella(q)
    dedosRf(luz, otra, 1.1f)
    dedosRf(luz, mano)
}

/** El cuerpo de la botella, de pie, con el agarre en (0, 0): el culo redondeado, los hombros y el gollete. */
private fun cuerpoBotella(): Path = Path().apply {
    moveTo(-B_RADIO + 0.3f, B_CULO); lineTo(B_RADIO - 0.3f, B_CULO)
    cubicTo(B_RADIO, B_CULO, B_RADIO, B_CULO - 0.3f, B_RADIO, B_CULO - 0.6f)
    lineTo(B_RADIO, B_HOMBRO)
    cubicTo(B_RADIO, B_HOMBRO - 1f, 0.65f, -3.6f, 0.62f, -4.2f)
    lineTo(0.62f, -B_BOCA); lineTo(-0.62f, -B_BOCA); lineTo(-0.62f, -4.2f)
    cubicTo(-0.65f, -3.6f, -B_RADIO, B_HOMBRO - 1f, -B_RADIO, B_HOMBRO)
    lineTo(-B_RADIO, B_CULO - 0.6f)
    cubicTo(-B_RADIO, B_CULO - 0.3f, -B_RADIO, B_CULO, -B_RADIO + 0.3f, B_CULO)
    close()
}

/** La botella inclinada [giro] grados: el vidrio, el agua a nivel (gira al revés que la botella), su reflejo y el tapón. */
private fun DrawScope.botella(luz: LuzGato, giro: Float, tapada: Boolean) {
    val cuerpo = cuerpoBotella()
    val vidrio = luz.tono(RA_VIDRIO)
    drawPath(cuerpo, vidrio.copy(alpha = 0.32f))
    clipPath(cuerpo) {
        val nivel = Offset(0f, 0.3f)
        rotate(-giro, nivel) {
            drawRect(Brush.verticalGradient(listOf(luz.tono(RA_AGUA), luz.tono(RA_AGUA_HONDA)), nivel.y, nivel.y + 6f), Offset(-12f, nivel.y), Size(24f, 14f))
            drawLine(Color.White.copy(alpha = 0.6f * (1f - 0.4f * luz.noche)), Offset(-12f, nivel.y), Offset(12f, nivel.y), 0.3f)
        }
        drawLine(Color.White.copy(alpha = 0.55f * (1f - 0.5f * luz.noche)), Offset(-B_RADIO + 0.55f, B_CULO - 0.8f), Offset(-B_RADIO + 0.55f, B_HOMBRO), 0.45f, StrokeCap.Round)
        drawLine(luz.tono(RA_AGUA_HONDA).copy(alpha = 0.35f), Offset(B_RADIO - 0.4f, B_CULO - 0.6f), Offset(B_RADIO - 0.4f, B_HOMBRO), 0.5f)
    }
    drawPath(cuerpo, vidrio.copy(alpha = 0.9f), style = Stroke(0.32f))
    drawLine(vidrio, Offset(-0.78f, -B_BOCA + 0.35f), Offset(0.78f, -B_BOCA + 0.35f), 0.45f)
    if (tapada) tapon(luz, Offset(0f, -B_BOCA - 0.42f))
}

/** El tapón rojo, con [c] en su medio. */
private fun DrawScope.tapon(luz: LuzGato, c: Offset) {
    drawRoundRect(luz.tono(RA_ROJO), Offset(c.x - 0.8f, c.y - 0.48f), Size(1.6f, 0.96f), CornerRadius(0.25f))
    drawLine(luz.tono(RA_ROJO_LUZ), Offset(c.x - 0.55f, c.y - 0.25f), Offset(c.x + 0.4f, c.y - 0.25f), 0.2f)
}

/** Lo que se le escapa por la comisura: una gota que cae (y otra, en el primer sorbo). */
private fun DrawScope.sorbo(luz: LuzGato, boca: Offset, q: Int) {
    val agua = lerp(luz.tono(RA_AGUA), RA_AGUA, 0.25f)
    drawCircle(agua, 0.32f, boca + Offset(0.3f, 1.2f))
    if (q == 2) drawCircle(agua.copy(alpha = 0.8f), 0.22f, boca + Offset(0.7f, 2.6f))
}

// ---- Repara con la llave (sentado).

/** La rueda de la llave de paso: su medio y su radio; el largo de la llave de válvulas, de la rueda al mango. */
private val RUEDA_PASO = Offset(21.4f, -9.8f)
private const val R_PASO = 4.7f
private const val LARGO_LLAVE = 7.2f

/** Hacia dónde sale la llave de la rueda en cada cuadro (grados) y cuánto ha girado ya la rueda: empuja, vuelve a enganchar, empuja. */
private val PALANCA = listOf(200f, 186f, 168f, 200f, 178f, 162f)
private val VUELTA = listOf(0f, -14f, -32f, -32f, -54f, -70f)

private fun haciaLaLlave(q: Int) = Offset(cos(rad(PALANCA[q])), sin(rad(PALANCA[q])))

internal fun poseLlave(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val u = haciaLaLlave(q)
    val empuja = q == 1 || q == 2 || q == 4
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.6f + (if (empuja) 0.6f else 0f), -25.2f + (if (empuja) 0.6f else 0f)), morro = if (q == 5) 0.32f else 0.55f,
        orejas = if (empuja) 0.7f else 1f, ojos = if (empuja) 0.5f else 1f,
        manoC = RUEDA_PASO + u * (R_PASO + LARGO_LLAVE - 0.4f) + Offset(0f, 0.5f), manoL = RUEDA_PASO + u * (R_PASO + 3.8f) + Offset(0f, 0.5f),
        cola2 = listOf(2.3f, 2.5f, 2.7f, 2.4f, 2.6f, 2.2f)[q],
    )
}

/** La llave de paso que gotea (el charco, el poste con su junta, la gota y la rueda) y la llave de válvulas en las manos. */
internal val llaveYRueda: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    charco(luz)
    poste(luz)
    gotera(luz, q)
    ruedaDePaso(luz, VUELTA[q])
    llaveDeValvulas(luz, q)
}

/** El charco de la gotera, en el suelo, con el brillo de la luz. */
private fun DrawScope.charco(luz: LuzGato) {
    drawOval(luz.tono(RA_MOJADO).copy(alpha = 0.4f), Offset(17.4f, -0.85f), Size(8.8f, 1.05f))
    drawLine(luz.brillo.copy(alpha = 0.35f), Offset(19.2f, -0.5f), Offset(22.6f, -0.45f), 0.25f, StrokeCap.Round)
}

/** El poste de la llave de paso: la brida del suelo, el tubo pintado de verde, la junta con sus pernos y el cuerpo de la válvula. */
private fun DrawScope.poste(luz: LuzGato) {
    val x = RUEDA_PASO.x; val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    val verde = luz.tono(RA_VERDE); val claro = luz.tono(RA_VERDE_LUZ); val hondo = luz.tono(RA_VERDE_OSC); val hierro = luz.tono(RA_HIERRO)
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(x - 3.6f + 1.4f * lado, -0.9f), Size(7.2f, 1.3f))
    drawRoundRect(hierro, Offset(x - 2.8f, -1.3f), Size(5.6f, 1.3f), CornerRadius(0.3f))
    for (dx in listOf(-2f, 2f)) drawCircle(luz.tono(RA_ACERO), 0.3f, Offset(x + dx, -0.85f))
    val tubo = Brush.horizontalGradient(if (lado > 0f) listOf(hondo, verde, claro) else listOf(claro, verde, hondo), x - 1.2f, x + 1.2f)
    drawRect(tubo, Offset(x - 1.2f, -9.6f), Size(2.4f, 8.3f))
    // Un desconchón del verde con su óxido, y la junta que gotea, con dos pernos.
    drawOval(luz.tono(lerp(RA_ROJO_OSC, RA_HIERRO, 0.4f)), Offset(x - 0.9f, -2.9f), Size(0.9f, 0.6f))
    drawRoundRect(Brush.horizontalGradient(if (lado > 0f) listOf(hondo, verde, claro) else listOf(claro, verde, hondo), x - 2f, x + 2f), Offset(x - 2f, -4.9f), Size(4f, 1f), CornerRadius(0.3f))
    for (dx in listOf(-1.3f, 1.3f)) drawCircle(hierro, 0.32f, Offset(x + dx, -4.4f))
    drawLine(luz.tono(RA_MOJADO).copy(alpha = 0.5f), Offset(x + 1f, -3.9f), Offset(x + 1.1f, -1.4f), 0.4f, StrokeCap.Round)
    // El cuerpo de la válvula, detrás de la rueda.
    drawCircle(Brush.radialGradient(listOf(claro, verde, hondo), RUEDA_PASO + Offset(-1.2f * lado, -1.2f), 4.4f), 2.9f, RUEDA_PASO)
}

/** La gota que se forma en la junta, cae y salpica; a cada vuelta, menos; en el último cuadro ya no gotea. */
private fun DrawScope.gotera(luz: LuzGato, q: Int) {
    val agua = lerp(luz.tono(RA_AGUA), RA_AGUA, 0.25f)
    val x = RUEDA_PASO.x + 1.5f
    when (q) {
        0 -> drawCircle(agua, 0.5f, Offset(x, -3.5f))
        1 -> drawCircle(agua, 0.42f, Offset(x + 0.1f, -2f))
        2 -> { drawOval(agua.copy(alpha = 0.8f), Offset(x - 0.8f, -0.75f), Size(1.6f, 0.5f), style = Stroke(0.16f)); drawCircle(agua, 0.16f, Offset(x - 0.6f, -1.2f)); drawCircle(agua, 0.14f, Offset(x + 0.7f, -1.1f)) }
        3 -> drawCircle(agua, 0.36f, Offset(x, -3.6f))
        4 -> drawCircle(agua, 0.22f, Offset(x, -3.75f))
        else -> Unit
    }
}

/** La rueda roja de la llave de paso, de frente, girada [vuelta] grados: cinco radios, el aro con su luz y una mella blanca que deja ver que gira. */
private fun DrawScope.ruedaDePaso(luz: LuzGato, vuelta: Float) {
    val c = RUEDA_PASO; val r = R_PASO
    val rojo = luz.tono(RA_ROJO); val claro = luz.tono(RA_ROJO_LUZ); val hondo = luz.tono(RA_ROJO_OSC)
    drawCircle(Color.Black.copy(alpha = 0.22f), r, c + Offset(0.4f, 0.5f), style = Stroke(1.2f))
    for (i in 0 until 5) {
        val a = rad(vuelta + 72f * i - 90f); val u = Offset(cos(a), sin(a))
        drawLine(hondo, c + u * 0.9f + Offset(0.15f, 0.2f), c + u * (r - 0.2f) + Offset(0.15f, 0.2f), 0.8f, StrokeCap.Round)
        drawLine(rojo, c + u * 0.9f, c + u * (r - 0.2f), 0.7f, StrokeCap.Round)
    }
    drawCircle(hondo, r, c + Offset(0.12f, 0.16f), style = Stroke(1.15f))
    drawCircle(rojo, r, c, style = Stroke(1f))
    drawArc(claro, 195f, 80f, false, Offset(c.x - r + 0.25f, c.y - r + 0.25f), Size((r - 0.25f) * 2f, (r - 0.25f) * 2f), style = Stroke(0.35f, cap = StrokeCap.Round))
    val m = rad(vuelta + 30f)
    drawCircle(lerp(luz.tono(Color(0xFFEDE6D2)), Color(0xFFEDE6D2), 0.2f), 0.32f, c + Offset(cos(m), sin(m)) * r)
    // El cubo, con la tuerca del vástago.
    drawCircle(rojo, 1.1f, c)
    drawCircle(luz.tono(RA_HIERRO), 0.55f, c)
    drawCircle(luz.tono(RA_ACERO), 0.2f, c + Offset(-0.15f, -0.15f))
}

/** La llave de válvulas: la barra de hierro con su brillo, el mango forrado de cinta con sus vueltas y la boca de dos dientes enganchada al aro. */
private fun DrawScope.llaveDeValvulas(luz: LuzGato, q: Int) {
    val u = haciaLaLlave(q); val n = Offset(-u.y, u.x)
    val boca = RUEDA_PASO + u * R_PASO; val mango = RUEDA_PASO + u * (R_PASO + LARGO_LLAVE)
    val hierro = luz.tono(lerp(RA_HIERRO, RA_ACERO, 0.4f)); val cinta = luz.tono(RA_CINTA)
    drawLine(Color.Black.copy(alpha = 0.2f), boca + Offset(0.3f, 0.4f), mango + Offset(0.3f, 0.4f), 0.9f, StrokeCap.Round)
    drawLine(hierro, boca, mango, 0.95f, StrokeCap.Round)
    drawLine(luz.tono(lerp(RA_ACERO, Color.White, 0.35f)).copy(alpha = 0.7f), boca - n * 0.22f + u * 0.6f, mango - n * 0.22f, 0.22f)
    // Los dos dientes, uno a cada lado del aro.
    for (l in listOf(-1f, 1f)) drawLine(hierro, boca + n * (0.55f * l), boca + n * (0.55f * l) - u * 1.3f, 0.42f, StrokeCap.Round)
    // El mango de cinta, con sus vueltas.
    val desde = mango - u * 3f
    drawLine(cinta, desde, mango + u * 0.2f, 1.25f, StrokeCap.Round)
    for (k in 1..5) { val m = desde + (mango - desde) * (k / 6f); drawLine(luz.tono(lerp(RA_CINTA, RA_ACERO, 0.45f)).copy(alpha = 0.6f), m - n * 0.6f - u * 0.15f, m + n * 0.6f + u * 0.15f, 0.16f) }
    dedosRf(luz, RUEDA_PASO + u * (R_PASO + 3.8f), 1.1f)
    dedosRf(luz, mango - u * 0.5f)
}
