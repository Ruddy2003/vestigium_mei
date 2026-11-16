package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Laboratorio: gris azulado, de ojos violeta, con la bata blanca de canto cian y el collar violeta
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/laboratorio_*`, `herramientas/gato/gato8.py`, rama «laboratorio»): liso,
// sin rayas, gris azulado medio (#9AA5AE, la mitad entre el blanco clínico #E6EDF2 y el gris #4D5D6B del tema), de pecho
// claro (#D2D6DB), ojos violeta (#7669D8) y nariz rosa (#E296A0). Lleva la bata blanca como un chaleco sobre el lomo y
// los hombros, con el canto cian (#0B8FA1) y una franja cian junto al canto, y el collar violeta (#5E4FD1). La bata
// tiene además el bolsillo dado de sí de la de la doctora («los bolsillos dados de sí por las semillas»).
//
// Sus dos acciones propias salen de las del de pixel (`gato17.py`, `temas.laboratorio_c.especiales`), las dos sentado, y
// están en `PelajeLaboratorioAcciones.kt`:
// - «observa un tubo con grama» (`o_matraz`, «observa un matraz»): **[Diseño propio]**, el cuento no tiene matraces;
//   tiene tubos («cuarenta tubos con su etiqueta»), así que mira a la luz un tubo con tierra gris y un brote de grama,
//   con su tapón de corcho y su cinta cian, sin letras;
// - «pone gotas con la pipeta» (`t_pipeta`): la pipeta del cuento («un tubo de vidrio fino con una pera de goma»), sobre
//   una bandeja de tierra gris de arriba con un brote de grama.
// La luz de la bata viene de arriba (las lámparas del laboratorio), no de un lado: el accesorio no sabe hacia dónde mira
// el gato, y así el brillo no queda del lado contrario cuando el motor lo voltea.

private val L_MANTO = Color(0xFF9AA5AE)
private val L_PECHO = Color(0xFFD2D6DB)
private val L_SOMBRA = Color(0xFF748089)
private val L_OJOS = Color(0xFF7669D8)
private val L_NARIZ = Color(0xFFE296A0)
private val L_VIOLETA = Color(0xFF5E4FD1)
private val L_BATA = Color(0xFFF1F4F5)
private val L_BATA_LUZ = Color(0xFFFCFDFD)
private val L_BATA_SOMBRA = Color(0xFFBFC9D4)
private val L_BATA_PLIEGUE = Color(0xFF9AA6B4)
private val L_CIAN = Color(0xFF0B8FA1)
private val L_CIAN_LUZ = Color(0xFF52C1CE)
private val L_SOMBRA_TELA = Color(0xFF261E44)

private fun largoL(v: Offset) = sqrt(v.x * v.x + v.y * v.y)
private fun unidadL(v: Offset, siNo: Offset = Offset(1f, 0f)) = largoL(v).let { if (it < 0.001f) siNo else v / it }

/** Una forma cerrada y sin esquinas que pasa cerca de los [puntos]. */
private fun redondaL(puntos: List<Offset>): Path = Path().apply {
    val n = puntos.size
    fun medio(i: Int) = (puntos[Math.floorMod(i, n)] + puntos[Math.floorMod(i + 1, n)]) / 2f
    moveTo(medio(-1))
    for (i in 0 until n) { val a = medio(i - 1); val c = puntos[i]; val b = medio(i); cubicTo(a + (c - a) * 0.67f, b + (c - b) * 0.67f, b) }
    close()
}

/** Una línea suave por los [puntos], para un canto o un pliegue. */
private fun trazoL(puntos: List<Offset>): Path = Path().apply {
    moveTo(puntos.first())
    for (i in 1 until puntos.size - 1) { val a = (puntos[i - 1] + puntos[i]) / 2f; val b = (puntos[i] + puntos[i + 1]) / 2f; if (i == 1) lineTo(a); cubicTo(a + (puntos[i] - a) * 0.67f, b + (puntos[i] - b) * 0.67f, b) }
    lineTo(puntos.last())
}

/** Una forma cerrada que une los [puntos] con rectas. */
private fun poligonoL(puntos: List<Offset>): Path = Path().apply { moveTo(puntos[0]); for (i in 1 until puntos.size) lineTo(puntos[i]); close() }

/**
 * El tronco del gato en una postura, para ponerle la bata: el punto a [t] del camino de la cadera (0) al hombro (1) y a
 * [h] del lomo (1 el lomo, 0 el medio, −1 el vientre); y el cuello, de donde sale la bata por arriba.
 */
private class TroncoL(val p: PuntosGato) {
    val deEspaldas = p.giro < -0.75f; val deFrente = p.giro > 0.75f
    private val s = sin((PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()).coerceIn(0f, 1f)
    private val medio = p.lomo + Offset(0f, 3.9f)
    val dir = unidadL(p.hombro - p.cadera)
    val arriba = Offset(dir.y, -dir.x)
    val nuca: Offset = unidadL(p.cabeza - p.hombro, Offset(0f, -1f)).let { Offset(it.y, -it.x) }
    /** La base del cuello, un poco por debajo del collar. */
    val cuello: Offset = (p.hombro + Offset(0.8f * s, -1f)).let { it + (p.cuello - it) * 0.55f }
    private fun centro(t: Float): Offset = if (t < 0.5f) p.cadera + (medio - p.cadera) * (t * 2f) else medio + (p.hombro - medio) * ((t - 0.5f) * 2f)
    private fun radio(t: Float) = if (t < 0.5f) Gatuno.R_GRUPA + (3.9f - Gatuno.R_GRUPA) * t * 2f else 3.9f + (Gatuno.R_PECHO - 3.9f) * (t - 0.5f) * 2f
    fun en(t: Float, h: Float): Offset = centro(t) + arriba * (h * radio(t))
    /** De dónde nace la cola, para no pintarle nada encima cuando pasa por delante. */
    val raizCola = p.cadera + Offset(-4f * s, -1.4f - 2.2f * abs(cos((PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat())))
}

/** Pinta [bloque] sin tapar lo que queda por delante de la bata: la cabeza (salvo de espaldas) y, de espaldas, la cola en alto. */
private inline fun DrawScope.debajoDeLaCabezaL(k: TroncoL, crossinline bloque: DrawScope.() -> Unit) {
    val p = k.p
    if (p.giro >= -0.25f) { clipPath(Path().apply { addOval(Rect(p.cabeza, Gatuno.R_CABEZA + 0.2f)) }, ClipOp.Difference) { bloque() }; return }
    val v = p.puntaCola - k.raizCola
    if (largoL(v) < 2f) { bloque(); return }
    val n = unidadL(v).let { Offset(-it.y, it.x) } * 2.6f
    clipPath(poligonoL(listOf(k.raizCola + n, p.puntaCola + n, p.puntaCola - n, k.raizCola - n)), ClipOp.Difference) { bloque() }
}

/**
 * Los colores de la bata con la luz del sitio: la tela con luz, la tela, su sombra, el pliegue y el canto cian. La tela
 * blanca recibe el brillo de la luz que le da como lo recibe el pelo del gato en el motor (`luz.brillo`, más cuanto más
 * contraste tenga esa luz): así es siempre lo más claro del gato y no se queda gris a su lado. De noche el canto tira al
 * cian claro, para que no se pierda.
 */
private class TelaL(luz: LuzGato) {
    private val k = (0.45f * luz.rayo.contraste + 0.2f).coerceIn(0f, 0.75f)
    val clara = lerp(luz.tono(L_BATA_LUZ), luz.brillo, k); val tela = lerp(luz.tono(L_BATA), luz.brillo, k * 0.6f)
    val sombra = lerp(luz.tono(L_BATA_SOMBRA), luz.brillo, k * 0.2f)
    val pliegue = luz.tono(L_BATA_PLIEGUE); val cian = luz.tono(lerp(L_CIAN, L_CIAN_LUZ, 0.15f + 0.55f * luz.noche))
}

/**
 * La bata del gato: blanca, como un chaleco sobre los hombros y el lomo, cerrada en la base del cuello por debajo del
 * collar, con el canto cian por delante, abajo y atrás, la franja cian junto al canto, un pliegue que baja del hombro y el
 * bolsillo dado de sí. De espaldas se le ve la espalda de la bata con su costura; de frente, las dos solapas.
 */
private val bataDelGato: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val k = TroncoL(p)
    val c = TelaL(luz)
    debajoDeLaCabezaL(k) {
        if (k.deEspaldas) bataDeEspaldas(k, c) else if (k.deFrente) bataDeFrente(k, c) else bataDePerfil(k, c)
    }
}

private fun DrawScope.bataDePerfil(k: TroncoL, c: TelaL) {
    val p = k.p
    val atras = k.cuello + k.nuca * 3.3f; val delante = k.cuello - k.nuca * 3.3f
    val pecho = listOf(p.hombro + k.dir * 3f - k.arriba * 2.9f, p.hombro + k.dir * 0.9f - k.arriba * 4.3f)
    val bajo = listOf(k.en(0.74f, -0.72f), k.en(0.54f, -0.5f), k.en(0.38f, -0.16f))
    val lomo = listOf(k.en(0.36f, 0.62f), k.en(0.4f, 1.1f), k.en(0.66f, 1.14f), k.en(0.94f, 1.1f))
    val forma = redondaL(lomo + listOf(atras, delante) + pecho + bajo)
    // La sombra que la bata deja en el pelo, bajo el canto: es tela puesta encima, no una placa.
    drawPath(trazoL(pecho + bajo), L_SOMBRA_TELA.copy(alpha = 0.16f), style = Stroke(1.4f, cap = StrokeCap.Round))
    // La tela: con luz arriba (el lomo, bajo las lámparas) y en sombra hacia el vientre.
    drawPath(forma, Brush.linearGradient(listOf(c.clara, c.tela, c.sombra), k.en(0.66f, 1.2f), k.en(0.66f, -0.8f)))
    clipPath(forma) {
        // Un pliegue blando que baja del hombro hacia el canto.
        drawPath(trazoL(listOf(k.en(0.92f, 1f), k.en(0.85f, 0.2f), k.en(0.8f, -0.55f))), c.pliegue.copy(alpha = 0.4f), style = Stroke(0.55f, cap = StrokeCap.Round))
        drawPath(trazoL(listOf(atras + k.dir * 0.2f, k.cuello - k.dir * 0.5f, delante + k.dir * 0.1f)), c.sombra, style = Stroke(0.5f))
        bolsillo(k, c)
    }
    // El canto cian, de la base del cuello por delante, abajo y atrás; y la franja junto a él.
    drawPath(trazoL(listOf(delante) + pecho + bajo + listOf(k.en(0.36f, 0.62f))), c.cian, style = Stroke(0.7f, cap = StrokeCap.Round))
    drawPath(trazoL(listOf(pecho[1] + k.arriba * 1.2f) + bajo.map { it + k.arriba * 1.15f }), c.cian.copy(alpha = 0.85f), style = Stroke(0.4f, cap = StrokeCap.Round))
    drawPath(forma, c.pliegue.copy(alpha = 0.45f), style = Stroke(0.25f))
}

/** El bolsillo de la bata, en el costado: un parche con su boca y la tela que cede, dado de sí por las semillas. */
private fun DrawScope.bolsillo(k: TroncoL, c: TelaL) {
    val o = k.en(0.56f, 0.2f); val d = k.dir; val a = k.arriba
    val boca = listOf(o + d * 1.4f + a * 1f, o - d * 1.4f + a * 1f)
    val parche = redondaL(listOf(boca[0], o + d * 1.5f - a * 1.1f, o - a * 1.45f, o - d * 1.5f - a * 1.1f, boca[1]))
    drawPath(parche, c.sombra.copy(alpha = 0.25f))
    drawPath(parche, c.pliegue.copy(alpha = 0.45f), style = Stroke(0.22f))
    drawLine(c.pliegue.copy(alpha = 0.75f), boca[0], boca[1], 0.35f, StrokeCap.Round)
}

private fun DrawScope.bataDeEspaldas(k: TroncoL, c: TelaL) {
    val p = k.p
    val x = (p.hombro.x + p.cadera.x) / 2f
    val y0 = maxOf(p.hombro.y - Gatuno.R_PECHO - 0.4f, p.cuello.y + 1f); val y1 = p.hombro.y + maxOf(1.6f, (p.cadera.y - p.hombro.y) * 0.62f)
    val borde = listOf(Offset(x - 4.6f, y0 + 0.8f), Offset(x - 5.4f, y1), Offset(x - 2f, y1 + 0.7f), Offset(x + 2f, y1 + 0.7f), Offset(x + 5.4f, y1), Offset(x + 4.6f, y0 + 0.8f))
    val forma = redondaL(listOf(Offset(x - 3.8f, y0), Offset(x + 3.8f, y0)) + borde.reversed())
    drawPath(forma, Brush.verticalGradient(listOf(c.clara, c.tela, c.sombra), y0, y1 + 0.7f))
    // La costura de la espalda, y el canto cian por los dos lados y abajo, con su franja.
    drawLine(c.pliegue.copy(alpha = 0.7f), Offset(x, y0 + 1.2f), Offset(x + 0.1f, y1 + 0.5f), 0.3f)
    drawPath(trazoL(borde), c.cian, style = Stroke(0.7f, cap = StrokeCap.Round))
    drawPath(trazoL(listOf(Offset(x - 4.9f, y1 - 1.1f), Offset(x, y1 - 0.5f), Offset(x + 4.9f, y1 - 1.1f))), c.cian.copy(alpha = 0.85f), style = Stroke(0.4f))
    drawPath(forma, c.pliegue.copy(alpha = 0.45f), style = Stroke(0.25f))
}

private fun DrawScope.bataDeFrente(k: TroncoL, c: TelaL) {
    val p = k.p
    for (lado in listOf(-1f, 1f)) {
        val x = p.hombro.x + 3.6f * lado
        val canto = listOf(Offset(x - 1.4f * lado, p.cuello.y + 1.2f), Offset(x - 0.9f * lado, p.hombro.y + 5.2f))
        val forma = redondaL(listOf(canto[0], Offset(x + 1.9f * lado, p.hombro.y - 2.8f), Offset(x + 2.2f * lado, p.hombro.y + 4.6f), canto[1]))
        drawPath(forma, if (lado < 0f) c.tela else c.sombra)
        drawLine(c.cian, canto[0], canto[1], 0.6f, StrokeCap.Round)
    }
}

/** El gato de Laboratorio. Sus dos acciones están en `PelajeLaboratorioAcciones.kt`. */
internal val PELAJE_LABORATORIO = Pelaje(
    manto = L_MANTO, claro = L_PECHO, marca = L_SOMBRA, dibujo = DibujoPelaje.LISO, ojos = L_OJOS,
    nariz = L_NARIZ, collar = L_VIOLETA, accesorio = bataDelGato, especiales = ACCIONES_LABORATORIO,
)
