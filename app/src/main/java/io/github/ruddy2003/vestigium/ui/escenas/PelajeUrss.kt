package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Soviético: color papel, de ojos negros, con el pañuelo rojo de los pioneros
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/urss_*`, `herramientas/gato/gato8.py`): un gato liso del color del
// papel del cartel (#F7F0DE), de patas negras (#4A4440), ojos negros (#161414), nariz roja y un pañuelo rojo
// (#C1121F) anudado al cuello, el de los pioneros («un trozo de la bandera», dice el cuento de Liudmila). Sus dos
// acciones propias, las mismas que el de pixel (`gato17.py`): alzar la bandera roja (asta negra con su remate, el
// paño rojo que ondea y la estrella de papel) y martillar en el yunque (yunque gris acero #8A8780, martillo de mango
// negro y cabeza de acero).
//
// Lo que no se puede llevar al cuadro sin tocar el motor del gato: las patas negras (el motor no tiene un dibujo que
// pinte solo las patas: el «de dos colores» pinta también el hocico y el pecho, y el «de puntas», la cara, las orejas
// y la cola; probados los dos, el gato quedaba con la cara negra), la trama de puntos del de pixel (es un cartel a dos
// tintas) y el rojo de dentro de las orejas. Quedan el papel, el pañuelo y la nariz rojos y los ojos negros. Si algún
// día el motor tiene un dibujo «de calcetines», este gato es el primero que lo pide (#4A4440).

private val R_PAPEL = Color(0xFFF7F0DE)
private val R_SOMBRA = Color(0xFFC9B994)
private val R_TINTA = Color(0xFF161414)
private val R_ROJO = Color(0xFFC1121F)
private val R_ROJO_LUZ = Color(0xFFE5252F)
private val R_ROJO_OSC = Color(0xFF8A0E14)
private val R_ACERO = Color(0xFF8A8780)
private val R_ACERO_LUZ = Color(0xFFC4C1B8)
private val R_ACERO_OSC = Color(0xFF55534E)
private val R_ORO = Color(0xFFD4A017)
private val R_MADERA = Color(0xFF6B4A30)

private fun unidadR(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** Una forma cerrada que une los [puntos] con rectas. */
private fun poligonoR(puntos: List<Offset>): Path = Path().apply {
    moveTo(puntos[0].x, puntos[0].y); for (i in 1 until puntos.size) lineTo(puntos[i].x, puntos[i].y); close()
}

/**
 * El pañuelo de pionero: un triángulo de tela roja anudado bajo la barbilla, con las dos puntas que caen sobre el
 * pecho; de perfil asoma el pico de atrás sobre los hombros, y de espaldas se le ve entero, con el pico en el lomo.
 */
private val panueloPionero: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val sube = unidadR(p.cabeza - p.hombro); val nuca = Offset(sube.y, -sube.x)
    val rojo = luz.tono(R_ROJO); val claro = luz.tono(R_ROJO_LUZ); val hondo = luz.tono(R_ROJO_OSC)
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    if (p.giro < -0.25f) {
        // De espaldas: el triángulo entero sobre los hombros, con el pico hacia el lomo.
        val a = p.cuello + Offset(-4.2f, -0.4f); val b = p.cuello + Offset(4.2f, -0.4f); val pico = p.cuello + Offset(0.4f, 8.2f)
        val tela = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + 2.8f, a.y + 0.9f, b.x - 2.8f, b.y + 0.9f, b.x, b.y); cubicTo(b.x - 1.2f, b.y + 3f, pico.x + 1.2f, pico.y - 2.6f, pico.x, pico.y); cubicTo(pico.x - 1.4f, pico.y - 2.6f, a.x + 1f, a.y + 3f, a.x, a.y); close() }
        drawPath(tela, rojo)
        clipPath(tela) {
            drawPath(poligonoR(listOf(Offset(p.cuello.x - 0.4f * lado, a.y), if (lado > 0f) b else a, pico)), claro.copy(alpha = 0.45f))
            drawLine(hondo.copy(alpha = 0.6f), p.cuello + Offset(-1.2f, 1.6f), pico + Offset(-0.4f, -1.6f), 0.5f, StrokeCap.Round)
        }
        drawLine(hondo, a, b, 0.7f, StrokeCap.Round)
    } else panueloDelante(p, luz, s, co, sube, nuca, lado)
}

/** El pañuelo visto de lado o de frente: el pico de atrás, la vuelta al cuello, el nudo y las dos puntas. */
private fun DrawScope.panueloDelante(p: PuntosGato, luz: LuzGato, s: Float, co: Float, sube: Offset, nuca: Offset, lado: Float) {
    val rojo = luz.tono(R_ROJO); val claro = luz.tono(R_ROJO_LUZ); val hondo = luz.tono(R_ROJO_OSC)
    val cabeza = Path().apply { addOval(Rect(p.cabeza, Gatuno.R_CABEZA + 0.15f)) }
    // El pico de atrás, de perfil o de tres cuartos: asoma por detrás de la cabeza sobre los hombros.
    if (s > 0.35f) clipPath(cabeza, ClipOp.Difference) {
        val n1 = p.cuello + nuca * 3.2f + sube * 0.6f
        val n2 = p.cuello + nuca * 0.4f - sube * 1.6f
        val pico = p.hombro + nuca * 4.1f - sube * 1.4f
        val tela = Path().apply { moveTo(n1.x, n1.y); cubicTo(n1.x - 0.3f * sube.x, n1.y + 2f, pico.x + 0.4f, pico.y - 2.2f, pico.x, pico.y); cubicTo(pico.x + 1.6f, pico.y - 0.6f, n2.x - 1f, n2.y + 0.8f, n2.x, n2.y); close() }
        drawPath(tela, hondo)
        drawLine(rojo.copy(alpha = 0.8f), n1 + (n2 - n1) * 0.3f, pico + (n2 - pico) * 0.25f, 0.6f, StrokeCap.Round)
    }
    // El nudo, bajo la barbilla, y la vuelta que da al cuello (de frente, de lado a lado).
    val nudo = p.cuello - sube * 1.5f + Offset(2.4f * s, 1.1f + 1.4f * co.coerceAtLeast(0f))
    val atras = p.cuello + nuca * (3.3f * s) + Offset(-3.4f * co.coerceAtLeast(0f), 0.6f)
    val otro = p.cuello + Offset(3.4f * co.coerceAtLeast(0f), 0.6f)
    drawPath(Path().apply { moveTo(atras.x, atras.y); cubicTo(atras.x + (nudo.x - atras.x) * 0.4f, atras.y + 1.6f, nudo.x - 1.4f, nudo.y + 0.4f, nudo.x, nudo.y) }, rojo, style = Stroke(2.1f, cap = StrokeCap.Round))
    if (co > 0.3f) drawPath(Path().apply { moveTo(otro.x, otro.y); cubicTo(otro.x - 1f, otro.y + 1.2f, nudo.x + 1.4f, nudo.y + 0.4f, nudo.x, nudo.y) }, hondo, style = Stroke(2.1f, cap = StrokeCap.Round))
    // Las dos puntas que caen sobre el pecho: anchas en el nudo, cortadas en pico abajo; la de atrás, en sombra.
    fun punta(dx: Float, largo: Float, color: Color, brillo: Boolean) {
        val fin = nudo + Offset(dx, largo)
        val ancho = 1.5f
        val forma = Path().apply {
            moveTo(nudo.x - ancho, nudo.y)
            cubicTo(nudo.x - ancho, nudo.y + largo * 0.4f, fin.x - 1.4f, fin.y - largo * 0.3f, fin.x - 1.3f, fin.y)
            lineTo(fin.x + 0.2f, fin.y - 1.3f); lineTo(fin.x + 1.4f, fin.y + 0.2f)
            cubicTo(fin.x + 1.2f, fin.y - largo * 0.35f, nudo.x + ancho, nudo.y + largo * 0.35f, nudo.x + ancho, nudo.y)
            close()
        }
        drawPath(forma, color)
        if (brillo) drawLine(claro.copy(alpha = 0.7f), nudo + Offset(-0.5f * lado, 1f), fin + Offset(-0.5f * lado, -1.6f), 0.45f, StrokeCap.Round)
    }
    punta(-1.4f - 1.2f * co.coerceAtLeast(0f) - 0.6f * s, 5.6f, hondo, false)
    punta(1.6f + 0.6f * co.coerceAtLeast(0f) + 0.4f * s, 6.6f, rojo, true)
    drawOval(rojo, Offset(nudo.x - 1.5f, nudo.y - 1.15f), Size(3f, 2.3f))
    drawOval(claro.copy(alpha = 0.8f), Offset(nudo.x - 0.9f + 0.3f * lado, nudo.y - 0.85f), Size(1.2f, 0.8f))
    drawOval(hondo, Offset(nudo.x - 1.5f, nudo.y - 1.15f), Size(3f, 2.3f), style = Stroke(0.3f))
}

/** Una puntita de la mano (del color del manto) encima del asta o del mango, para que se vea que lo agarra. */
private fun DrawScope.zarpaR(luz: LuzGato, en: Offset, r: Float = 1.3f) {
    drawOval(luz.tono(R_PAPEL), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f))
    drawOval(luz.tono(R_SOMBRA), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f), style = Stroke(0.25f))
    drawLine(luz.tono(R_SOMBRA).copy(alpha = 0.8f), en + Offset(-0.1f, -r * 0.6f), en + Offset(0.1f, r * 0.5f), 0.2f)
}

/** Una estrella de cinco puntas con centro en [c]. */
private fun estrellaR(c: Offset, radio: Float, giro: Float = 0f): Path = Path().apply {
    for (i in 0 until 10) {
        val a = giro - PI.toFloat() / 2f + i * PI.toFloat() / 5f; val r = if (i % 2 == 0) radio else radio * 0.42f
        val x = c.x + r * cos(a); val y = c.y + r * sin(a); if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

// ---- Alza la bandera roja (sentado).

private fun poseBandera(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // La saca, la sube por encima de la cabeza, la mantiene en alto mientras ondea y la baja un poco.
    val alzado = listOf(0.2f, 0.65f, 1f, 1f, 1f, 0.7f)[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.4f + 0.4f * alzado, -26f - 0.4f * alzado), morro = -0.1f - 0.4f * alzado + listOf(0f, 0f, 0f, 0.06f, -0.04f, 0f)[q], orejas = 1f,
        ojos = if (q == 3) 0.3f else 1f,
        manoC = Offset(10f + 1.6f * alzado, -9f - 9.5f * alzado), manoL = Offset(6.5f, 0f),
        cola2 = listOf(2.3f, 2.5f, 2.7f, 2.8f, 2.6f, 2.4f)[q],
    )
}

/** La bandera: el asta negra con su remate, el paño rojo de tres trozos que ondea y la estrella de papel. */
private val banderaDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val mano = p.manoC
    // El asta, casi derecha, un poco hacia delante; la mano la toma por abajo.
    val abajo = mano + Offset(-1.2f, 6.5f); val arriba = mano + Offset(2.4f, -15.5f)
    val asta = unidadR(arriba - abajo); val normal = Offset(-asta.y, asta.x)
    drawLine(luz.tono(R_TINTA), abajo, arriba, 0.9f, StrokeCap.Round)
    drawLine(luz.tono(lerp(R_TINTA, R_PAPEL, 0.35f)).copy(alpha = 0.6f), abajo + normal * 0.25f, arriba + normal * 0.25f, 0.25f)
    // El remate: una punta de lanza de latón.
    val r = arriba + asta * 0.4f
    drawPath(poligonoR(listOf(r + asta * 2.4f, r + normal * 0.85f, r - asta * 0.3f, r - normal * 0.85f)), luz.tono(R_ORO))
    drawCircle(luz.tono(lerp(R_ORO, R_TINTA, 0.35f)), 0.55f, r - asta * 0.2f)
    // El paño, que ondea: sale del asta hacia delante, a la altura del remate, y se mueve con el cuadro.
    val fase = q * PI.toFloat() / 2f
    fun ola(u: Float) = 1.1f * sin(fase + u * 3.2f) * u.coerceAtLeast(0.15f)
    val alto = 6.6f; val largo = 11.5f
    val p0 = arriba + asta * 0.2f
    fun en(u: Float, v: Float) = p0 + Offset(largo * u * (1f - 0.06f * sin(fase + u * 2f)), alto * v + ola(u))
    val pano = Path().apply {
        moveTo(en(0f, 0f).x, en(0f, 0f).y)
        for (i in 1..6) { val u = i / 6f; val a = en(u - 1f / 12f, 0f); val b = en(u, 0f); cubicTo(a.x, a.y - 0.2f, a.x, a.y, b.x, b.y) }
        lineTo(en(1f, 1f).x, en(1f, 1f).y)
        for (i in 5 downTo 0) { val u = i / 6f; val a = en(u + 1f / 12f, 1f); val b = en(u, 1f); cubicTo(a.x, a.y + 0.2f, a.x, a.y, b.x, b.y) }
        close()
    }
    drawPath(Path().apply { addPath(pano, Offset(0.6f, 1.1f)) }, Color(0xFF2A1418).copy(alpha = 0.18f))
    drawPath(pano, rojo(luz))
    clipPath(pano) {
        // Los pliegues: bandas de luz y de sombra que siguen la ola.
        for (k in 0..2) {
            val u = (k + 0.5f) / 3f; val h = sin(fase + u * 3.2f)
            val color = if (h > 0f) luz.tono(R_ROJO_LUZ).copy(alpha = 0.55f * h) else luz.tono(R_ROJO_OSC).copy(alpha = -0.6f * h)
            drawLine(color, en(u, -0.1f), en(u, 1.1f), 2.4f)
        }
        // Los tres trozos de tela cosidos, como el de pixel: dos costuras.
        for (u in listOf(1f / 3f, 2f / 3f)) drawLine(luz.tono(R_ROJO_OSC).copy(alpha = 0.55f), en(u, 0f), en(u, 1f), 0.22f)
    }
    // La estrella de papel, junto al asta, arriba.
    val e = en(0.24f, 0.36f)
    drawPath(estrellaR(e, 1.75f, 0.08f * sin(fase)), luz.tono(R_PAPEL))
    // La mano que la sostiene.
    zarpaR(luz, mano + Offset(0.15f, 0.4f))
}

private fun rojo(luz: LuzGato) = luz.tono(R_ROJO)

// ---- Martilla en el yunque (sentado).

private fun poseYunque(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Levanta el martillo, lo deja caer sobre el yunque (cuadro 2), rebota, lo vuelve a subir y da otro golpe más corto.
    val alto = listOf(0.6f, 1f, 0f, 0.35f, 0.85f, 0.05f)[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f, -25.4f + 0.5f * (1f - alto)), morro = 0.45f + 0.15f * (1f - alto), orejas = if (q == 2) 0.8f else 1f,
        ojos = if (q == 2) 0.4f else 0.9f,
        manoC = Offset(10f + 1.2f * (1f - alto), -8.5f - 7f * alto), manoL = Offset(9.2f, -6.2f),
        cola2 = listOf(2.3f, 2.5f, 2.2f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El yunque de acero, delante, y el martillo en la mano; al dar el golpe, unas chispas. */
private val yunqueDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val alto = listOf(0.6f, 1f, 0f, 0.35f, 0.85f, 0.05f)[q]
    val acero = luz.tono(R_ACERO); val aceroLuz = luz.tono(R_ACERO_LUZ); val aceroOsc = luz.tono(R_ACERO_OSC)
    // El yunque: el pie ancho, la cintura, la mesa de arriba y el cuerno hacia delante.
    val x = 15.5f
    drawOval(Color.Black.copy(alpha = 0.2f), Offset(x - 6.6f, -1.3f), Size(13.6f, 2.4f))
    val cuerpo = poligonoR(listOf(
        Offset(x - 5.6f, 0f), Offset(x + 5f, 0f), Offset(x + 3.6f, -1.8f), Offset(x + 2.2f, -2.6f), Offset(x + 2.6f, -5.6f),
        Offset(x + 4.6f, -6f), Offset(x + 8.6f, -6.3f), Offset(x + 4.6f, -7.6f), Offset(x - 4.8f, -7.8f), Offset(x - 4.8f, -6f),
        Offset(x - 2.6f, -5.4f), Offset(x - 2.2f, -2.6f), Offset(x - 3.8f, -1.8f),
    ))
    drawPath(cuerpo, Brush.horizontalGradient(if (luz.rayo.lat >= 0f) listOf(aceroOsc, acero, aceroLuz) else listOf(aceroLuz, acero, aceroOsc), x - 5.6f, x + 8.6f))
    // La cara de arriba, pulida, y la sombra bajo la mesa.
    drawLine(aceroLuz, Offset(x - 4.6f, -7.6f), Offset(x + 4.4f, -7.5f), 0.55f, StrokeCap.Round)
    drawLine(aceroOsc.copy(alpha = 0.8f), Offset(x - 2.4f, -5.3f), Offset(x + 2.4f, -5.4f), 0.5f)
    drawPath(cuerpo, luz.tono(R_TINTA).copy(alpha = 0.55f), style = Stroke(0.3f))
    // La pieza que forja: una barrita al rojo sobre la mesa (luz propia, se enfría entre golpe y golpe).
    val brasa = if (q == 2 || q == 5) 1f else 0.65f
    drawRoundRect(lerp(Color(0xFFB0301A), Color(0xFFFFB347), brasa * 0.7f), Offset(x - 1.6f, -8.6f), Size(5.4f, 1f), androidx.compose.ui.geometry.CornerRadius(0.4f))
    // El martillo: el mango negro desde la mano, la cabeza de acero al final.
    val mano = p.manoC
    val a = -0.2f - 1.15f * alto                      // el ángulo del mango: casi tumbado al golpear, alzado arriba
    val d = Offset(cos(a), sin(a)); val n = Offset(-d.y, d.x)
    val fin = mano + d * 7.2f
    drawLine(luz.tono(R_MADERA), mano - d * 1.2f, fin, 0.95f, StrokeCap.Round)
    drawLine(luz.tono(R_TINTA).copy(alpha = 0.6f), mano - d * 1.2f + n * 0.3f, fin + n * 0.3f, 0.3f)
    val cabezaM = poligonoR(listOf(fin + n * 2f - d * 1.1f, fin + n * 2f + d * 1.1f, fin - n * 1.9f + d * 1f, fin - n * 1.9f - d * 1f))
    drawPath(cabezaM, aceroOsc)
    drawLine(aceroLuz, fin + n * 1.8f - d * 0.8f, fin - n * 1.6f - d * 0.8f, 0.4f)
    zarpaR(luz, mano)
    // Las chispas del golpe: luz propia, sin pasar por la luz de la escena.
    if (q == 2 || q == 5) {
        val golpe = Offset(x + 0.6f, -8.8f)
        for ((i, ang) in listOf(-2.7f, -2.2f, -1.6f, -1f, -0.5f).withIndex()) {
            val l = if (q == 2) 3.4f + (i % 2) * 1.2f else 2.2f
            val v = Offset(cos(ang), sin(ang))
            drawLine(Color(0xFFFFD27A), golpe + v * 0.8f, golpe + v * l, 0.35f, StrokeCap.Round)
        }
        drawCircle(Color(0xFFFFF1C2).copy(alpha = 0.8f), 0.8f, golpe)
    }
}

/** El gato de Soviético. */
internal val PELAJE_URSS = Pelaje(
    manto = R_PAPEL, claro = Color(0xFFFFFFFF), marca = R_SOMBRA, dibujo = DibujoPelaje.LISO, ojos = R_TINTA,
    nariz = R_ROJO, collar = R_ROJO, accesorio = panueloPionero,
    especiales = listOf(EspecialGato("alza la bandera roja", true, 6, ::poseBandera, banderaDelGato), EspecialGato("martilla en el yunque", true, 6, ::poseYunque, yunqueDelGato)),
)
