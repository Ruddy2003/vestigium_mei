package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Tierras Antiguas: gris piedra atigrado, de pecho claro y ojos de oro viejo, con su capa corta verde bosque
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/antiguas_*`, `herramientas/gato/gato8.py`): gris piedra (#A09A8D, la
// mezcla del gris claro #DCD5C3 y el tenue #57534A del tema), atigrado, de pecho y vientre más claros (#D4D2CC), ojos de
// oro viejo (#9A7B2E) y una capa corta verde bosque (#2E5A3E, el bajo #1F3A28) sobre los hombros, cerrada al cuello con
// algo rojo (#8E1B24). Las rayas van con el tono hondo del de pixel (#666259) y no con su gris de rayas (#807B70): el
// motor las pinta a medias, y con ese gris no se verían.
//
// **[Diseño propio]**: el de pixel cierra la capa con un broche rubí, como una gema; el cuento no tiene gemas (hierro,
// sal, lana, sebo), así que aquí la cierra un nudo de cordón rojo, mate, con sus dos cabos: la cinta roja de la feria. La
// capa es de lana de oveja, como la del cuento («de lana de oveja sin lavar»), con su bajo más oscuro y una vuelta de
// cordón en el canto.
//
// Sus dos acciones están en `PelajeAntiguasAcciones.kt`: «consulta el mapa» y «alza la linterna de mina» (la del de pixel
// es «alza una gema que brilla», y la gema no sale en el cuento).
//
// Lo que no se puede con el `Pelaje` de hoy: el pecho claro de perfil (el dibujo atigrado solo aclara el vientre y el
// hocico) se pinta aquí, con lo puesto, como una mancha clara en el pecho; las rayas del de pixel, que le cruzan todo el
// cuerpo y las patas, el motor las pone solo en el lomo, la cola y la frente.

internal val AG_MANTO = Color(0xFFA09A8D)
private val AG_CLARO = Color(0xFFD4D2CC)
internal val AG_RAYA = Color(0xFF666259)
private val AG_OJOS = Color(0xFF9A7B2E)
private val AG_NARIZ = Color(0xFFCB9799)
private val AG_VERDE = Color(0xFF2E5A3E)
private val AG_VERDE_LUZ = Color(0xFF4A7A58)
private val AG_VERDE_OSC = Color(0xFF1F3A28)
private val AG_ROJO = Color(0xFF8E1B24)
private val AG_ROJO_LUZ = Color(0xFFB8464E)
private val AG_ROJO_OSC = Color(0xFF5A1018)

private fun unidadAg(v: Offset, siNo: Offset = Offset(1f, 0f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** Una forma cerrada y sin esquinas que pasa cerca de los [puntos] (como `redonda` en `Pelajes.kt`). */
private fun redondaAg(puntos: List<Offset>): Path = Path().apply {
    val n = puntos.size
    fun medio(i: Int) = (puntos[Math.floorMod(i, n)] + puntos[Math.floorMod(i + 1, n)]) / 2f
    moveTo(medio(-1))
    for (i in 0 until n) { val a = medio(i - 1); val c = puntos[i]; val b = medio(i); cubicTo(a + (c - a) * 0.67f, b + (c - b) * 0.67f, b) }
    close()
}

/** Una línea suave por los [puntos], para un bajo o un pliegue (como `trazo` en `Pelajes.kt`). */
private fun trazoAg(puntos: List<Offset>): Path = Path().apply {
    moveTo(puntos.first())
    for (i in 1 until puntos.size - 1) { val a = (puntos[i - 1] + puntos[i]) / 2f; val b = (puntos[i] + puntos[i + 1]) / 2f; if (i == 1) lineTo(a); cubicTo(a + (puntos[i] - a) * 0.67f, b + (puntos[i] - b) * 0.67f, b) }
    lineTo(puntos.last())
}

/**
 * El tronco del gato en una postura (como `Tronco` en `Pelajes.kt`): [en] da el punto a [t] del camino de la cadera (0)
 * al hombro (1) y a [h] del lomo (1 el lomo, 0 el medio, −1 el vientre); [s] y [co], desde dónde se le ve.
 */
private class TroncoAg(val p: PuntosGato) {
    private val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val deEspaldas = p.giro < -0.75f; val deFrente = p.giro > 0.75f
    private val medio = p.lomo + Offset(0f, 3.9f)
    val dir = unidadAg(p.hombro - p.cadera)
    val arriba = Offset(dir.y, -dir.x)
    val nuca = unidadAg(p.cabeza - p.hombro, Offset(0f, -1f)).let { Offset(it.y, -it.x) }
    fun centro(t: Float): Offset = if (t < 0.5f) p.cadera + (medio - p.cadera) * (t * 2f) else medio + (p.hombro - medio) * ((t - 0.5f) * 2f)
    fun radio(t: Float) = if (t < 0.5f) Gatuno.R_GRUPA + (3.9f - Gatuno.R_GRUPA) * t * 2f else 3.9f + (Gatuno.R_PECHO - 3.9f) * (t - 0.5f) * 2f
    fun en(t: Float, h: Float): Offset = centro(t) + arriba * (h * radio(t))
}

/** Pinta [bloque] sin tapar la cabeza (salvo de espaldas) ni, de espaldas, la cola en alto (como en `Pelajes.kt`). */
private inline fun DrawScope.sinTaparCabezaAg(p: PuntosGato, crossinline bloque: DrawScope.() -> Unit) {
    if (p.giro >= -0.25f) {
        clipPath(Path().apply { addOval(Rect(p.cabeza, Gatuno.R_CABEZA + 0.2f)) }, ClipOp.Difference) { bloque() }
        return
    }
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val raiz = p.cadera + Offset(-4f * sin(angulo).coerceIn(0f, 1f), -1.4f - 2.2f * abs(cos(angulo)))
    val v = p.puntaCola - raiz
    if (sqrt(v.x * v.x + v.y * v.y) < 2f) { bloque(); return }
    val n = unidadAg(v).let { Offset(-it.y, it.x) } * 2.6f
    clipPath(Path().apply { moveTo(raiz + n); lineTo(p.puntaCola + n); lineTo(p.puntaCola - n); lineTo(raiz - n); close() }, ClipOp.Difference) { bloque() }
}

/** Lo puesto: el pecho claro (de perfil), la capa corta de lana verde bosque y el nudo rojo que la cierra. */
private val puestoAntiguas: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val k = TroncoAg(p)
    // Echado, la capa llega al suelo: se apoya en él y no baja de los pies.
    clipRect(-60f, -80f, 60f, 0.2f) {
        sinTaparCabezaAg(p) {
            when {
                k.deEspaldas -> capaDeEspaldas(p, luz)
                k.deFrente -> capaDeFrente(p, luz)
                else -> { if (k.s > 0.45f) pecho(k, luz); capaDeLado(k, luz) }
            }
            nudo(p, k, luz)
        }
    }
}

/** El pecho claro del de pixel, de perfil: una mancha del claro del pelaje por delante del pecho, dentro de él. */
private fun DrawScope.pecho(k: TroncoAg, luz: LuzGato) {
    val c = k.en(1f, -0.5f) + k.dir * 1.9f
    clipPath(Path().apply { addOval(Rect(k.p.hombro, Gatuno.R_PECHO - 0.2f)) }) {
        drawOval(luz.tono(AG_CLARO).copy(alpha = 0.62f), Offset(c.x - 2.2f, c.y - 3.2f), Size(4.4f, 6.4f))
    }
}

/** La lana de la capa con la luz de la escena: la tela, su lado de luz y su bajo. */
private class LanaAg(luz: LuzGato) {
    val tela = luz.tono(AG_VERDE); val clara = luz.tono(AG_VERDE_LUZ); val honda = luz.tono(AG_VERDE_OSC)
}

/** De perfil o de tres cuartos: sobre los hombros y la cruz, por delante hasta el pecho y por el costado hasta el codo. */
private fun DrawScope.capaDeLado(k: TroncoAg, luz: LuzGato) {
    val c = LanaAg(luz)
    val p = k.p
    val nuca = p.cuello + k.nuca * 3.4f; val gorja = p.cuello - k.nuca * 3.1f
    // Cae del cuello: por delante tapa el pecho hasta su pico, por el costado baja en dos ondas y por detrás no pasa de la cruz.
    val bajo = listOf(k.en(1.1f, -0.25f) + k.dir * 3.4f, k.en(1.02f, -1f) + k.dir * 1.2f, k.en(0.88f, -0.72f), k.en(0.76f, -0.9f), k.en(0.64f, -0.3f), k.en(0.6f, 0.5f))
    val forma = redondaAg(listOf(k.en(0.6f, 1.1f), k.en(0.8f, 1.2f), k.en(1f, 1.16f), nuca, gorja) + bajo)
    drawPath(forma, c.tela)
    clipPath(forma) {
        // La luz en los hombros, los pliegues que caen del nudo y el bajo más hondo.
        drawPath(redondaAg(listOf(k.en(0.56f, 1.35f), k.en(0.8f, 1.45f), k.en(1.02f, 1.35f), nuca + k.nuca * 1.2f, k.en(1f, 0.5f), k.en(0.78f, 0.4f), k.en(0.6f, 0.6f))), c.clara)
        drawPath(trazoAg(listOf(gorja + k.dir * 0.4f, k.en(1.06f, -0.2f) + k.dir * 1.6f, k.en(1.02f, -0.85f) + k.dir * 0.8f)), c.honda.copy(alpha = 0.7f), style = Stroke(0.5f, cap = StrokeCap.Round))
        drawPath(trazoAg(listOf(k.en(0.94f, 0.9f), k.en(0.9f, 0.1f), k.en(0.88f, -0.62f))), c.honda.copy(alpha = 0.6f), style = Stroke(0.5f, cap = StrokeCap.Round))
        drawPath(trazoAg(listOf(k.en(0.76f, 0.9f), k.en(0.74f, 0.2f), k.en(0.76f, -0.75f))), c.honda.copy(alpha = 0.45f), style = Stroke(0.4f, cap = StrokeCap.Round))
        drawPath(trazoAg(bajo), c.honda, style = Stroke(1.4f, cap = StrokeCap.Round))
    }
    // El cordón del canto, más claro, que coge la luz.
    drawPath(trazoAg(bajo), c.clara.copy(alpha = 0.6f), style = Stroke(0.3f, cap = StrokeCap.Round))
}

/** De espaldas: la capa sobre la espalda, de hombro a hombro, con su bajo que cae en dos ondas. */
private fun DrawScope.capaDeEspaldas(p: PuntosGato, luz: LuzGato) {
    val c = LanaAg(luz)
    val x = (p.hombro.x + p.cadera.x) / 2f
    val y0 = p.hombro.y - Gatuno.R_PECHO - 0.5f; val y1 = p.hombro.y + maxOf(1.2f, (p.cadera.y - p.hombro.y) * 0.5f)
    val forma = redondaAg(listOf(Offset(x - 4.6f, y0), Offset(x + 4.6f, y0), Offset(x + 5.8f, y1), Offset(x + 2.4f, y1 + 1.1f), Offset(x, y1 + 0.4f), Offset(x - 2.4f, y1 + 1.1f), Offset(x - 5.8f, y1)))
    drawPath(forma, c.tela)
    clipPath(forma) {
        drawRect(c.clara, Offset(x - 6f, y0 - 1f), Size(12f, (y1 - y0) * 0.38f + 1f))
        drawLine(c.honda.copy(alpha = 0.6f), Offset(x + 1.6f, y0 + 2f), Offset(x + 2.2f, y1 + 1f), 0.5f)
        drawLine(c.honda.copy(alpha = 0.6f), Offset(x - 1.8f, y0 + 2f), Offset(x - 2.4f, y1 + 1f), 0.5f)
    }
    drawPath(trazoAg(listOf(Offset(x - 5.5f, y1 - 0.2f), Offset(x - 2.4f, y1 + 0.9f), Offset(x, y1 + 0.3f), Offset(x + 2.4f, y1 + 0.9f), Offset(x + 5.5f, y1 - 0.2f))), c.honda, style = Stroke(1.1f, cap = StrokeCap.Round))
}

/** De frente: la capa asoma a los dos lados del pecho; el lado de la luz, más claro. */
private fun DrawScope.capaDeFrente(p: PuntosGato, luz: LuzGato) {
    val c = LanaAg(luz)
    val luzDe = if (luz.rayo.lat >= 0f) 1f else -1f
    for (lado in listOf(-1f, 1f)) {
        val x = p.hombro.x + 3.9f * lado
        val forma = redondaAg(listOf(Offset(x - 1.7f * lado, p.hombro.y - 3.8f), Offset(x + 2.1f * lado, p.hombro.y - 2.6f), Offset(x + 2.4f * lado, p.hombro.y + 4f), Offset(x - 0.7f * lado, p.hombro.y + 4.6f)))
        drawPath(forma, if (lado == luzDe) c.tela else c.honda)
        drawLine(c.honda, Offset(x + 2.3f * lado, p.hombro.y + 3.9f), Offset(x - 0.6f * lado, p.hombro.y + 4.5f), 0.8f, StrokeCap.Round)
    }
}

/** El nudo de cordón rojo que cierra la capa al cuello, con sus dos cabos; mate (no es una gema). De espaldas no se ve. */
private fun DrawScope.nudo(p: PuntosGato, k: TroncoAg, luz: LuzGato) {
    if (p.giro < -0.25f) return
    val sitio = p.cuello - k.nuca * (3.1f * k.s) + Offset(0f, 1.2f + 2.4f * k.co.coerceAtLeast(0f))
    val rojo = luz.tono(AG_ROJO); val hondo = luz.tono(AG_ROJO_OSC)
    for (dx in listOf(-0.45f, 0.4f)) drawLine(hondo, sitio + Offset(dx * 0.4f, 0.4f), sitio + Offset(dx, 2.3f), 0.42f, StrokeCap.Round)
    drawCircle(hondo, 1.05f, sitio + Offset(0.12f, 0.16f))
    drawCircle(rojo, 0.92f, sitio)
    drawLine(hondo.copy(alpha = 0.7f), sitio + Offset(-0.6f, -0.35f), sitio + Offset(0.55f, 0.4f), 0.22f)
    drawCircle(luz.tono(AG_ROJO_LUZ).copy(alpha = 0.7f), 0.3f, sitio + Offset(-0.3f, -0.32f))
}

/** Una puntita de la mano (del color del manto) encima de lo que agarra, con su raya. */
internal fun DrawScope.dedosAg(luz: LuzGato, en: Offset, r: Float = 1.25f) {
    drawOval(luz.tono(AG_MANTO), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f))
    drawOval(luz.tono(AG_RAYA).copy(alpha = 0.6f), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f), style = Stroke(0.25f))
    drawLine(luz.tono(AG_RAYA).copy(alpha = 0.7f), en + Offset(-0.1f, -r * 0.6f), en + Offset(0.1f, r * 0.5f), 0.2f)
}

/** El gato de Tierras Antiguas. Sus dos acciones están en `PelajeAntiguasAcciones.kt`. */
internal val PELAJE_ANTIGUAS = Pelaje(
    manto = AG_MANTO, claro = AG_CLARO, marca = AG_RAYA, dibujo = DibujoPelaje.ATIGRADO, ojos = AG_OJOS,
    nariz = AG_NARIZ, accesorio = puestoAntiguas, especiales = ACCIONES_ANTIGUAS,
)
