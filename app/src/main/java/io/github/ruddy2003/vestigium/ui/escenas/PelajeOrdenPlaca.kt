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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  «Se cubre con una placa de caparazón»: la segunda acción del gato de Orden (ver `PelajeOrden.kt`) **[Diseño propio]**
// =====================================================================
//
// En vez del escudo carmesí del engrane del de pixel (`gato17.py`, «sostener»), el peto de un caparazón («la armadura de
// placas con motor» del cuento): hierro pavonado, con su arista en medio, el escote y las sisas de canto enrollado,
// abollado, con un remiendo de chapa remachado y una correa rota con su hebilla en el hombro, pintado a mano con un candil
// carmesí de llama ocre («placas abolladas», «más remiendos que placas»; los caparazones recién pintados de la Fundación).
// Tiene la forma de una pieza de armadura, no de escudo. Lo sube del suelo, se esconde detrás (las orejas gachas), asoma
// por encima y se vuelve a esconder.

private val OP_LATON = Color(0xFFC9A04A)
private val OP_LATON_OSC = Color(0xFF7E6219)
private val OP_ACERO = Color(0xFF4A4E58)
private val OP_ACERO_LUZ = Color(0xFF8C93A0)
private val OP_ACERO_OSC = Color(0xFF24272E)
private val OP_CHAPA = Color(0xFF6C7076)
private val OP_OXIDO = Color(0xFF8E4A24)
private val OP_CARMESI = Color(0xFF9E1B22)
private val OP_OCRE = Color(0xFFD9A23A)
private val OP_CUERO = Color(0xFF6E4A2A)

/** Cuánto lo ha subido (0 en el suelo, 1 tapándole el pecho) y cuánto lo ladea (grados, hacia el gato). */
private val SUBE = listOf(0f, 0.6f, 1f, 1f, 1f, 0.45f)
private val LADEO_PETO = listOf(-6f, -4f, 0f, -3f, 1f, -5f)
/** El peto: su alto, con el medio de abajo en (0, 0); los hombros, a cada lado del escote. */
private const val P_ALTO = 12.4f
private const val P_HOMBRO = 5.2f

/** El medio de abajo del peto en el cuadro [q]. */
private fun piePeto(q: Int): Offset = Offset(13.4f - 0.8f * SUBE[q], -0.9f - 7.3f * SUBE[q])

/** Dónde lo agarran sus dos manos, por los hombros: la de este lado, hacia el gato, y la otra. */
private fun manosEnElPeto(q: Int): Pair<Offset, Offset> {
    val p = piePeto(q); val a = Math.toRadians(LADEO_PETO[q].toDouble()).toFloat()
    fun giro(v: Offset) = Offset(v.x * cos(a) - v.y * sin(a), v.x * sin(a) + v.y * cos(a))
    return (p + giro(Offset(-P_HOMBRO + 0.9f, -P_ALTO + 0.7f))) to (p + giro(Offset(P_HOMBRO - 0.9f, -P_ALTO + 0.7f)))
}

internal fun posePlaca(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val (mano, otra) = manosEnElPeto(q)
    return Gatuno.SENTADO.copy(
        cabeza = listOf(Offset(5.2f, -25.4f), Offset(5.6f, -24.8f), Offset(6.2f, -23.6f), Offset(6.4f, -25.6f), Offset(6f, -23.8f), Offset(5.4f, -25f))[q],
        morro = listOf(0.45f, 0.2f, 0.1f, -0.05f, 0.12f, 0.3f)[q], orejas = listOf(1f, 0.6f, -0.4f, 1f, -0.5f, 0.8f)[q],
        ojos = listOf(0.85f, 0.8f, 0.35f, 1f, 0.12f, 0.9f)[q], manoC = mano + Offset(0f, 0.5f), manoL = otra + Offset(0f, 0.5f),
        cola2 = listOf(2.2f, 2.4f, 2.6f, 2.5f, 2.7f, 2.3f)[q],
    )
}

/** El peto, ladeado según el cuadro, y las manos que lo agarran por los hombros. Apoyado en el suelo, no baja de él. */
internal val placaDeCaparazon: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val p = piePeto(q)
    clipRect(-60f, -80f, 60f, 0.2f) {
        if (SUBE[q] < 0.1f) drawOval(Color.Black.copy(alpha = 0.18f), Offset(p.x - 5f, -0.9f), Size(10f, 0.9f))
        withTransform({ translate(p.x, p.y); rotate(LADEO_PETO[q], Offset.Zero) }) { peto(luz) }
    }
    val (mano, otra) = manosEnElPeto(q)
    dedosOr(luz, otra, 1.1f)
    dedosOr(luz, mano)
}

/** El escote: baja en curva entre los dos hombros. */
private fun Path.escote(): Path = apply {
    val h = P_ALTO
    cubicTo(-1.6f, -h + 0.2f, -0.9f, -h + 1.5f, 0f, -h + 1.5f)
    cubicTo(0.9f, -h + 1.5f, 1.6f, -h + 0.2f, 2.8f, -h)
}

/** La sisa del lado [l] (1 o −1): del hombro hacia dentro y abajo, cóncava. */
private fun Path.sisa(l: Float): Path = apply { cubicTo(l * 4.2f, -P_ALTO + 1.6f, l * 3.7f, -P_ALTO + 3.6f, l * 4f, -P_ALTO + 5f) }

/** El contorno del peto: el escote, los hombros, las sisas, los costados que se estrechan y el borde de abajo en curva. */
private fun formaPeto(): Path = Path().apply {
    val h = P_ALTO
    moveTo(-2.8f, -h); escote()
    lineTo(P_HOMBRO, -h + 0.5f); sisa(1f)
    cubicTo(4.3f, -h + 7.5f, 4.4f, -4.5f, 4.1f, -2.6f)
    cubicTo(3.8f, -1.3f, 2.2f, -0.3f, 0f, 0f)
    cubicTo(-2.2f, -0.3f, -3.8f, -1.3f, -4.1f, -2.6f)
    cubicTo(-4.4f, -4.5f, -4.3f, -h + 7.5f, -4f, -h + 5f)
    cubicTo(-3.7f, -h + 3.6f, -4.2f, -h + 1.6f, -P_HOMBRO, -h + 0.5f)
    close()
}

/** El peto, con el medio de abajo en (0, 0): el hierro con su arista, lo que tiene encima, sus cantos enrollados y la correa. */
private fun DrawScope.peto(luz: LuzGato) {
    val forma = formaPeto()
    val acero = luz.tono(OP_ACERO); val claro = luz.tono(OP_ACERO_LUZ); val hondo = luz.tono(OP_ACERO_OSC)
    // La sombra que echa sobre el pecho del gato, y el grueso del canto, del lado del gato.
    drawPath(Path().apply { addPath(forma, Offset(-1f, 0.6f)) }, Color(0xFF14161C).copy(alpha = 0.3f))
    drawPath(Path().apply { addPath(forma, Offset(-0.55f, 0.15f)) }, hondo)
    // El hierro: la mitad de la luz, clara hacia la arista; la otra, en sombra; y más oscuro hacia abajo.
    drawPath(forma, Brush.horizontalGradient(listOf(hondo, claro, acero), -P_HOMBRO, 0f))
    clipPath(forma) {
        drawRect(Brush.horizontalGradient(listOf(lerp(acero, hondo, 0.35f), acero, hondo), 0f, P_HOMBRO), Offset(0f, -P_ALTO - 1f), Size(P_HOMBRO + 1f, P_ALTO + 2f))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, hondo.copy(alpha = 0.35f)), -5f, 0f), Offset(-7f, -5f), Size(14f, 5f))
        // La arista del medio: un filo de luz y, al otro lado, su sombra.
        drawLine(claro.copy(alpha = 0.75f), Offset(-0.25f, -P_ALTO + 1.6f), Offset(-0.2f, 0f), 0.35f)
        drawLine(hondo.copy(alpha = 0.6f), Offset(0.2f, -P_ALTO + 1.6f), Offset(0.25f, 0f), 0.3f)
        abolladuras(luz)
        remiendo(luz)
        candilPintado(luz)
        for ((x0, y0, x1) in listOf(Triple(-3.4f, -3.4f, -1.4f), Triple(1.6f, -9.6f, 3.4f), Triple(-2.6f, -6.4f, -1.2f)))
            drawLine(claro.copy(alpha = 0.5f), Offset(x0, y0), Offset(x1, y0 - 0.8f), 0.16f)
    }
    // Los cantos enrollados del escote y de las sisas: un tubito con su luz.
    val h = P_ALTO
    val cantos = listOf(Path().apply { moveTo(-2.8f, -h); escote() }, Path().apply { moveTo(P_HOMBRO, -h + 0.5f); sisa(1f) }, Path().apply { moveTo(-P_HOMBRO, -h + 0.5f); sisa(-1f) })
    for (canto in cantos) {
        drawPath(canto, hondo, style = Stroke(1.1f, cap = StrokeCap.Round))
        drawPath(Path().apply { addPath(canto, Offset(0f, -0.22f)) }, claro, style = Stroke(0.36f, cap = StrokeCap.Round))
    }
    // Los remaches de los hombros, del costado y del borde de abajo.
    for ((x, y) in listOf(-4.3f to -h + 1.1f, 4.3f to -h + 1.1f, -3.2f to -1.6f, 3.2f to -1.6f, -3.9f to -6f)) remache(luz, Offset(x, y))
    drawPath(forma, hondo.copy(alpha = 0.8f), style = Stroke(0.28f))
    correa(luz)
}

/** Dos abolladuras: la sombra del lado de arriba y la luz del de abajo, al revés que un bulto. */
private fun DrawScope.abolladuras(luz: LuzGato) {
    for ((c, r) in listOf(Offset(2.2f, -5.6f) to 1.2f, Offset(-2.4f, -9.4f) to 0.8f)) {
        drawArc(luz.tono(OP_ACERO_OSC).copy(alpha = 0.6f), 190f, 150f, false, Offset(c.x - r, c.y - r * 0.8f), Size(r * 2f, r * 1.6f), style = Stroke(0.5f))
        drawArc(luz.tono(OP_ACERO_LUZ).copy(alpha = 0.7f), 20f, 140f, false, Offset(c.x - r, c.y - r * 0.8f), Size(r * 2f, r * 1.6f), style = Stroke(0.35f))
    }
}

/** El remiendo: un trozo de chapa más nueva remachado abajo, del lado de la sombra, con el óxido en sus juntas. */
private fun DrawScope.remiendo(luz: LuzGato) {
    val esq = listOf(Offset(0.9f, -4.4f), Offset(3.6f, -4.7f), Offset(3.7f, -2.3f), Offset(1.1f, -1.9f))
    val chapa = Path().apply { moveTo(esq[0].x, esq[0].y); for (e in esq.drop(1)) lineTo(e.x, e.y); close() }
    drawPath(Path().apply { addPath(chapa, Offset(0.25f, 0.3f)) }, luz.tono(OP_ACERO_OSC).copy(alpha = 0.6f))
    drawPath(chapa, luz.tono(OP_CHAPA))
    drawPath(chapa, luz.tono(OP_OXIDO).copy(alpha = 0.55f), style = Stroke(0.35f))
    drawLine(luz.tono(OP_ACERO_LUZ).copy(alpha = 0.6f), esq[0] + Offset(0.3f, 0.25f), esq[1] + Offset(-0.3f, 0.25f), 0.22f)
    for (e in esq) remache(luz, e + (Offset(2.3f, -3.3f) - e) * 0.22f, 0.32f)
}

/** El candil pintado a mano: el vaso y el pie carmesí, la llama ocre, con el borde algo torcido y la pintura saltada. */
private fun DrawScope.candilPintado(luz: LuzGato) {
    val rojo = lerp(luz.tono(OP_CARMESI), OP_CARMESI, 0.1f); val ocre = lerp(luz.tono(OP_OCRE), OP_OCRE, 0.1f)
    val c = Offset(-0.4f, -7f)
    val vaso = Path().apply {
        moveTo(c.x - 2.3f, c.y - 1.2f); lineTo(c.x + 2.2f, c.y - 1.35f)
        cubicTo(c.x + 2f, c.y + 0.4f, c.x + 1.3f, c.y + 1.1f, c.x + 0.7f, c.y + 1.3f)
        lineTo(c.x + 1.5f, c.y + 2.1f); lineTo(c.x - 1.6f, c.y + 2.2f); lineTo(c.x - 0.8f, c.y + 1.35f)
        cubicTo(c.x - 1.4f, c.y + 1.1f, c.x - 2.1f, c.y + 0.4f, c.x - 2.3f, c.y - 1.2f)
        close()
    }
    drawPath(vaso, rojo)
    val b = c + Offset(0.1f, -1.6f)
    val llama = Path().apply {
        moveTo(b.x, b.y); cubicTo(b.x - 1.5f, b.y - 0.6f, b.x - 0.9f, b.y - 2.6f, b.x + 0.2f, b.y - 4f)
        cubicTo(b.x + 0.5f, b.y - 2.5f, b.x + 1.4f, b.y - 1f, b.x, b.y); close()
    }
    drawPath(llama, ocre)
    // La pintura saltada: pocos puntos del hierro que asoma.
    val hierro = luz.tono(OP_ACERO)
    for (d in listOf(Offset(1.2f, -0.5f), Offset(-1.5f, 0.2f), Offset(0.2f, 1.9f), Offset(0.3f, -3.6f))) drawCircle(hierro, 0.22f, c + d)
}

/** Un remache: la cabeza redonda con su luz y su sombra corta. */
private fun DrawScope.remache(luz: LuzGato, c: Offset, r: Float = 0.42f) {
    drawCircle(luz.tono(OP_ACERO_OSC), r, c + Offset(0.15f, 0.2f))
    drawCircle(luz.tono(lerp(OP_ACERO, OP_ACERO_LUZ, 0.5f)), r, c)
    drawCircle(luz.tono(OP_ACERO_LUZ), r * 0.4f, c + Offset(-r * 0.3f, -r * 0.3f))
}

/** La correa rota que cuelga del hombro del lado del gato, de cuero, con su hebilla de latón y el cabo deshilachado. */
private fun DrawScope.correa(luz: LuzGato) {
    val cuero = luz.tono(OP_CUERO)
    val x = -P_HOMBRO - 0.2f; val y0 = -P_ALTO + 0.4f
    drawRoundRect(luz.tono(lerp(OP_CUERO, OP_ACERO_OSC, 0.4f)), Offset(x - 0.5f, y0 + 0.2f), Size(1.6f, 6f), CornerRadius(0.3f))
    drawRoundRect(cuero, Offset(x - 0.7f, y0), Size(1.5f, 5.9f), CornerRadius(0.3f))
    for (i in 0..2) drawLine(cuero, Offset(x - 0.5f + i * 0.5f, y0 + 5.7f), Offset(x - 0.6f + i * 0.55f, y0 + 6.6f - (i % 2) * 0.3f), 0.3f, StrokeCap.Round)
    drawRect(lerp(luz.tono(OP_LATON), OP_LATON, 0.2f), Offset(x - 1f, y0 + 1.8f), Size(2.1f, 1.7f), style = Stroke(0.42f))
    drawLine(luz.tono(OP_LATON_OSC), Offset(x, y0 + 1.8f), Offset(x, y0 + 3.5f), 0.3f)
    remache(luz, Offset(x + 0.05f, y0 + 0.8f), 0.3f)
}
