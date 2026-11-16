package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
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
//  Refugio: gris crema moteado, de vientre claro y ojos naranjas, con su traje verde azulado de filete ocre
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/refugio_c_*`, `herramientas/gato/gato8.py`): gris crema (#C6C6BA, la
// mezcla del crema #F8F1DE y el acero #8A928F del tema), con motas sueltas más oscuras (#A1A59B; en el de pixel,
// «mechones»), vientre y hocico claros, ojos y collar naranjas (#D9572B) con su chapita de oro (#B98A15), y el traje del
// de pixel: un chaleco verde azulado (#1C6E6A, el bajo #1D5350) sobre el lomo, con un filete ocre (#B98A15) a lo largo
// del costado (el de pixel hace un escalón; con el gato sentado parecía un rayo, y va derecho). **Sin números ni letras**
// (nada de monos con cifras de otras obras); lleva un remiendo, como el mono del cuento.
//
// Sus dos acciones propias, las del de pixel (`gato17.py`), llevadas al cuento de la residente:
// - «bebe un sorbo de agua» (sentado): el de pixel «toma su café», pero en el cuento no hay café; la residente bebe de
//   la botella de agua de su mesilla («Bebe un sorbo. Se obliga a parar»). Es la misma botella de vidrio de tapón rojo
//   que hay en la bancada del cuadro. Destapa la botella (el tapón queda en el suelo), bebe y la vuelve a tapar; el agua
//   se queda siempre a nivel aunque la incline. **[Diseño propio]**.
// - «repara con la llave» (sentado): con la llave de válvulas del cuento («de hierro, larga como un brazo, con el mango
//   forrado de cinta»), enganchada a la rueda roja de una llave de paso que gotea por la junta, empuja, la rueda gira, la
//   vuelve a enganchar, empuja otra vez y la gotera para.
//
// Lo que no se puede llevar al cuadro con el `Pelaje` de hoy: el gato de noche del de pixel (todo en fósforo verde
// sobre negro, como una pantalla de terminal): el cuadro es el mismo en claro y en oscuro y el pelaje no tiene un dibujo
// que cambie de noche (y la noche del cuento es azul, no verde). El motor no tiene un dibujo moteado: las motas se pintan
// aquí, con lo puesto, solo en el tronco (no en la cabeza, las patas ni la cola) y solo de perfil o de tres cuartos. El
// pecho claro del de pixel lo deja el motor en el vientre y el hocico (el dibujo liso no aclara el pecho de perfil).

private val RF_MANTO = Color(0xFFC6C6BA)
private val RF_CLARO = Color(0xFFEFEADB)
private val RF_MOTA = Color(0xFFA1A59B)
/** Lo que las motas dejan del pelo: más o menos el gris de la mota entre el del manto (#A1A59B / #C6C6BA). */
private val RF_MOTA_MULT = Color(0xFFD0D5CE)
private val RF_OJOS = Color(0xFFD9572B)
private val RF_NARIZ = Color(0xFFD9958E)
private val RF_NARANJA = Color(0xFFD9572B)
private val RF_VERDE = Color(0xFF1C6E6A)
private val RF_VERDE_LUZ = Color(0xFF34908A)
private val RF_VERDE_OSC = Color(0xFF1D5350)
private val RF_OCRE = Color(0xFFB98A15)
private val RF_OCRE_LUZ = Color(0xFFE8C45A)
private val RF_OCRE_OSC = Color(0xFF7A5A0E)
private val RF_PUNTADA = Color(0xFFE6DAB8)

private fun unidadRf(v: Offset, siNo: Offset = Offset(1f, 0f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** Una forma cerrada y sin esquinas que pasa cerca de los [puntos] (como `redonda` en `Pelajes.kt`). */
private fun redondaRf(puntos: List<Offset>): Path = Path().apply {
    val n = puntos.size
    fun medio(i: Int) = (puntos[Math.floorMod(i, n)] + puntos[Math.floorMod(i + 1, n)]) / 2f
    moveTo(medio(-1))
    for (i in 0 until n) { val a = medio(i - 1); val c = puntos[i]; val b = medio(i); cubicTo(a + (c - a) * 0.67f, b + (c - b) * 0.67f, b) }
    close()
}

/** Una línea suave por los [puntos], para un bajo o una costura (como `trazo` en `Pelajes.kt`). */
private fun trazoRf(puntos: List<Offset>): Path = Path().apply {
    moveTo(puntos.first())
    for (i in 1 until puntos.size - 1) { val a = (puntos[i - 1] + puntos[i]) / 2f; val b = (puntos[i] + puntos[i + 1]) / 2f; if (i == 1) lineTo(a); cubicTo(a + (puntos[i] - a) * 0.67f, b + (puntos[i] - b) * 0.67f, b) }
    lineTo(puntos.last())
}

/**
 * El tronco del gato en una postura (como `Tronco` en `Pelajes.kt`): [en] da el punto a [t] del camino de la cadera (0)
 * al hombro (1) y a [h] del lomo (1 el lomo, 0 el medio, −1 el vientre); [s] y [co], desde dónde se le ve.
 */
private class TroncoRf(val p: PuntosGato) {
    private val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val deEspaldas = p.giro < -0.75f; val deFrente = p.giro > 0.75f
    private val medio = p.lomo + Offset(0f, 3.9f)
    val dir = unidadRf(p.hombro - p.cadera)
    val arriba = Offset(dir.y, -dir.x)
    val nuca = unidadRf(p.cabeza - p.hombro, Offset(0f, -1f)).let { Offset(it.y, -it.x) }
    fun centro(t: Float): Offset = if (t < 0.5f) p.cadera + (medio - p.cadera) * (t * 2f) else medio + (p.hombro - medio) * ((t - 0.5f) * 2f)
    fun radio(t: Float) = if (t < 0.5f) Gatuno.R_GRUPA + (3.9f - Gatuno.R_GRUPA) * t * 2f else 3.9f + (Gatuno.R_PECHO - 3.9f) * (t - 0.5f) * 2f
    fun en(t: Float, h: Float): Offset = centro(t) + arriba * (h * radio(t))
}

/** Pinta [bloque] sin tapar la cabeza (salvo de espaldas) ni, de espaldas, la cola en alto (como en `Pelajes.kt`). */
private inline fun DrawScope.sinTaparCabezaRf(p: PuntosGato, crossinline bloque: DrawScope.() -> Unit) {
    if (p.giro >= -0.25f) {
        clipPath(Path().apply { addOval(Rect(p.cabeza, Gatuno.R_CABEZA + 0.2f)) }, ClipOp.Difference) { bloque() }
        return
    }
    val s = sin((PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()).coerceIn(0f, 1f)
    val raiz = p.cadera + Offset(-4f * s, -1.4f - 2.2f * abs(cos((PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat())))
    val v = p.puntaCola - raiz
    if (sqrt(v.x * v.x + v.y * v.y) < 2f) { bloque(); return }
    val n = unidadRf(v).let { Offset(-it.y, it.x) } * 2.6f
    clipPath(Path().apply { moveTo(raiz + n); lineTo(p.puntaCola + n); lineTo(p.puntaCola - n); lineTo(raiz - n); close() }, ClipOp.Difference) { bloque() }
}

/** Las motas del pelaje, en el tronco: (dónde, de la cadera al hombro; a qué altura, del vientre al lomo). */
private val MOTAS = listOf(
    0.08f to 0.3f, 0.14f to -0.42f, 0.2f to 0.68f, 0.26f to 0.02f, 0.34f to -0.5f, 0.4f to 0.36f,
    0.48f to -0.18f, 0.58f to -0.62f, 0.68f to -0.3f, 0.8f to -0.66f, 0.9f to -0.42f, 0.18f to -0.08f,
)

/** El traje: las motas del tronco, el chaleco verde azulado con su filete ocre, y la chapita del collar. */
private val trajeRefugio: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val k = TroncoRf(p)
    // Echado, el chaleco llega al suelo: se apoya en él y no baja de los pies.
    clipRect(-60f, -80f, 60f, 0.2f) {
        sinTaparCabezaRf(p) {
            when {
                k.deEspaldas -> chalecoDeEspaldas(p, luz)
                k.deFrente -> chalecoDeFrente(p, luz)
                else -> { if (k.s > 0.4f) motas(k); chalecoDeLado(k, luz) }
            }
            chapita(p, k, luz)
        }
    }
}

/**
 * Las motas oscurecen el pelo que tienen debajo (multiplicando), y no lo tapan con un gris fijo: así siguen siendo más
 * oscuras que el pelo también en el lado de la sombra, que el motor ya oscureció.
 */
private fun DrawScope.motas(k: TroncoRf) {
    for ((t, h) in MOTAS) drawCircle(RF_MOTA_MULT, 0.55f, k.en(t, h), alpha = 0.9f, blendMode = BlendMode.Multiply)
}

/** El ocre del filete: un poco más fiel a su color que la tela, para que se lea también de noche. */
private fun ocre(luz: LuzGato) = lerp(luz.tono(RF_OCRE), RF_OCRE, 0.2f)

/** De perfil o de tres cuartos: del medio del lomo a los hombros, hasta dos tercios del costado, con el bajo oscuro. */
private fun DrawScope.chalecoDeLado(k: TroncoRf, luz: LuzGato) {
    val tela = luz.tono(RF_VERDE); val clara = luz.tono(RF_VERDE_LUZ); val honda = luz.tono(RF_VERDE_OSC)
    // Por delante baja por el hombro hasta el brazo, como una prenda, y no se queda en el lomo como una mochila.
    val bajo = listOf(k.en(1.06f, -0.62f) + k.dir * 2.2f, k.en(0.9f, -0.74f), k.en(0.62f, -0.66f), k.en(0.36f, -0.56f), k.en(0.22f, 0.2f))
    val forma = redondaRf(listOf(k.en(0.26f, 1.14f), k.en(0.64f, 1.2f), k.en(0.98f, 1.18f), k.en(1.12f, 0.62f) + k.dir * 1.8f, k.en(1.1f, 0f) + k.dir * 2.6f) + bajo)
    drawPath(forma, tela)
    clipPath(forma) {
        // La luz en el lomo, el bajo más oscuro y la costura del hombro.
        drawPath(redondaRf(listOf(k.en(0.2f, 1.4f), k.en(0.64f, 1.5f), k.en(1.04f, 1.4f), k.en(1.04f, 0.62f), k.en(0.64f, 0.55f), k.en(0.24f, 0.66f))), clara)
        drawPath(trazoRf(bajo), honda, style = Stroke(1.7f, cap = StrokeCap.Round))
        drawPath(trazoRf(listOf(k.en(0.86f, 1.2f), k.en(0.84f, 0.3f), k.en(0.86f, -0.7f))), honda.copy(alpha = 0.6f), style = Stroke(0.4f))
        remiendo(k, luz)
        // El filete ocre del costado, a lo largo, derecho (con el escalón del de pixel, sentado parecía un rayo).
        val filete = Path().apply { moveTo(k.en(0.26f, -0.12f)); lineTo(k.en(1.0f, -0.12f) + k.dir * 2f) }
        drawPath(filete, luz.tono(RF_OCRE_OSC), style = Stroke(1.05f))
        drawPath(filete, ocre(luz), style = Stroke(0.75f))
    }
}

/** Un remiendo en la grupa: un cuadro de tela más gastada con sus puntadas. */
private fun DrawScope.remiendo(k: TroncoRf, luz: LuzGato) {
    val esquinas = listOf(k.en(0.4f, 0.86f), k.en(0.52f, 0.9f), k.en(0.53f, 0.42f), k.en(0.41f, 0.38f))
    val parche = Path().apply { moveTo(esquinas[0]); for (q in esquinas.drop(1)) lineTo(q); close() }
    drawPath(parche, luz.tono(lerp(RF_VERDE, RF_PUNTADA, 0.22f)))
    val puntada = luz.tono(RF_PUNTADA).copy(alpha = 0.75f)
    for (i in 0..3) {
        val a = esquinas[i]; val b = esquinas[(i + 1) % 4]
        for (j in 0..2) { val m = a + (b - a) * ((j + 0.35f) / 3f); drawLine(puntada, m, m + (b - a) * 0.12f, 0.22f) }
    }
}

/** De espaldas: el chaleco sobre la espalda, con la costura del medio y el filete asomando a los dos lados. */
private fun DrawScope.chalecoDeEspaldas(p: PuntosGato, luz: LuzGato) {
    val tela = luz.tono(RF_VERDE); val clara = luz.tono(RF_VERDE_LUZ); val honda = luz.tono(RF_VERDE_OSC)
    val x = (p.hombro.x + p.cadera.x) / 2f
    val y0 = p.hombro.y - Gatuno.R_PECHO - 0.4f; val y1 = p.hombro.y + maxOf(1.6f, (p.cadera.y - p.hombro.y) * 0.62f)
    val forma = redondaRf(listOf(Offset(x - 4.7f, y0), Offset(x + 4.7f, y0), Offset(x + 5.7f, y1), Offset(x + 2f, y1 + 0.8f), Offset(x - 2f, y1 + 0.8f), Offset(x - 5.7f, y1)))
    drawPath(forma, tela)
    clipPath(forma) {
        drawRect(clara, Offset(x - 6f, y0 - 1f), Size(12f, (y1 - y0) * 0.4f + 1f))
        drawLine(honda, Offset(x - 6f, y1 + 0.1f), Offset(x + 6f, y1 + 0.3f), 1.6f)
        drawLine(honda.copy(alpha = 0.55f), Offset(x, y0), Offset(x + 0.3f, y1 + 1f), 0.4f)
        val yf = y0 + (y1 - y0) * 0.55f
        for (l in listOf(-1f, 1f)) drawLine(ocre(luz), Offset(x + l * 3.6f, yf), Offset(x + l * 6f, yf), 0.75f)
    }
}

/** De frente: las dos hojas del chaleco asoman a los lados del pecho, con el filete. */
private fun DrawScope.chalecoDeFrente(p: PuntosGato, luz: LuzGato) {
    val luzDe = if (luz.rayo.lat >= 0f) 1f else -1f
    for (lado in listOf(-1f, 1f)) {
        val x = p.hombro.x + 3.9f * lado
        val forma = redondaRf(listOf(Offset(x - 1.4f * lado, p.hombro.y - 3.6f), Offset(x + 2f * lado, p.hombro.y - 2.6f), Offset(x + 2.3f * lado, p.hombro.y + 4.2f), Offset(x - 0.6f * lado, p.hombro.y + 4.6f)))
        drawPath(forma, luz.tono(if (lado == luzDe) RF_VERDE else RF_VERDE_OSC))
        clipPath(forma) { drawLine(ocre(luz), Offset(x - 2f, p.hombro.y + 1.2f), Offset(x + 3f, p.hombro.y + 1.2f), 0.75f) }
    }
}

/** La chapita de oro que cuelga del collar naranja (el collar lo pinta el motor), con su brillo. De espaldas no se ve. */
private fun DrawScope.chapita(p: PuntosGato, k: TroncoRf, luz: LuzGato) {
    if (p.giro < -0.25f) return
    val sitio = p.cuello - k.nuca * (3.2f * k.s) + Offset(0f, 1.3f + 2.2f * k.co.coerceAtLeast(0f))
    drawCircle(luz.tono(RF_OCRE_OSC), 1.05f, sitio + Offset(0.12f, 0.18f))
    drawCircle(luz.tono(RF_OCRE), 0.92f, sitio)
    drawCircle(RF_OCRE_LUZ.copy(alpha = 0.8f * (1f - 0.5f * luz.noche)), 0.3f, sitio + Offset(-0.3f, -0.3f))
}

/** Una puntita de la mano (del color del manto) encima de lo que agarra. */
internal fun DrawScope.dedosRf(luz: LuzGato, en: Offset, r: Float = 1.25f) {
    drawOval(luz.tono(RF_MANTO), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f))
    drawOval(luz.tono(RF_MOTA).copy(alpha = 0.7f), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f), style = Stroke(0.25f))
    drawLine(luz.tono(RF_MOTA).copy(alpha = 0.8f), en + Offset(-0.1f, -r * 0.6f), en + Offset(0.1f, r * 0.5f), 0.2f)
}

/** El gato de Refugio. Sus dos acciones están en `PelajeRefugioAcciones.kt`. */
internal val PELAJE_REFUGIO = Pelaje(
    manto = RF_MANTO, claro = RF_CLARO, marca = RF_MOTA, dibujo = DibujoPelaje.LISO, ojos = RF_OJOS,
    nariz = RF_NARIZ, collar = RF_NARANJA, accesorio = trajeRefugio,
    especiales = listOf(
        EspecialGato("bebe un sorbo de agua", true, 6, ::poseBotella, botellaDelGato),
        EspecialGato("repara con la llave", true, 6, ::poseLlave, llaveYRueda),
    ),
)
