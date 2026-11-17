package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
//  Orden: gris acero atigrado, de vientre claro y ojos de latón, con su tabardo carmesí de filete de latón
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/orden_c_*`, `herramientas/gato/gato8.py`): gris acero (#A3A8AE, la mezcla
// del acero claro #D3D7DC y el tenue #4B525A del tema), atigrado con rayas gris azulado (en el de pixel, #7A8690, la mezcla
// con el acero azulado #2F4858), vientre claro (#DADDE0), ojos de latón (#B89A4A), collar de latón y un tabardo carmesí
// (#9E1B22, el bajo #601A1F) sobre el lomo, con un filete de latón (#A7893B). El de pixel lleva el filete a media altura
// del costado, con un escalón; aquí va como galón junto al bajo, porque con el gato sentado, a media altura, parecía la
// correa de una mochila. Sin cruces, engranes ni letras.
//
// Sus dos acciones propias (`PelajeOrdenAcciones.kt` y `PelajeOrdenPlaca.kt`) **no son las del de pixel** («alza la espada» y «se cubre con el
// escudo» del engrane): ninguna sale en el cuento (la escriba «no pelea en primera línea»; la orden no usa espadas ni
// escudos) y las dos rozan la iconografía de la obra en que se inspira la vida, que el calendario prohíbe. Se cambian
// guardando la forma del gesto (levantar algo, sostener algo plano) **[Diseño propio]**, para que el autor decida:
// - «alza el candil»: el candil de latón, el signo de la orden, con su llama, en alto como en la ronda de noche;
// - «se cubre con una placa de caparazón»: una placa de la armadura, abollada y remendada, pintada a mano con un candil,
//   como los caparazones de la Fundación.
//
// Lo que no se puede llevar al cuadro con el `Pelaje` de hoy: las patas más oscuras hacia los pies del de pixel (el motor
// no tiene un dibujo «de calcetines»); el pecho claro de perfil (el atigrado aclara el vientre y, de frente, el pecho; de
// perfil no); y el gato de noche del de pixel, más carmesí y más latón, porque el pelaje es el mismo de día y de noche (lo
// que hace la noche es la luz de la escena). El collar del de pixel es latón oscuro (#7E6219) con su luz; aquí va el latón
// (#A7893B) para que se lea como metal a ese tamaño.

private val OR_MANTO = Color(0xFFA3A8AE)
private val OR_CLARO = Color(0xFFDADDE0)
private val OR_RAYA = Color(0xFF5C6872)
private val OR_OJOS = Color(0xFFB89A4A)
private val OR_NARIZ = Color(0xFFE296A0)
private val OR_LATON = Color(0xFFA7893B)
private val OR_LATON_LUZ = Color(0xFFF2D27A)
private val OR_LATON_OSC = Color(0xFF7E6219)
private val OR_CARMESI = Color(0xFF9E1B22)
private val OR_CARMESI_LUZ = Color(0xFFC0303A)
private val OR_CARMESI_OSC = Color(0xFF601A1F)

private fun unidadOr(v: Offset, siNo: Offset = Offset(1f, 0f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** Una forma cerrada y sin esquinas que pasa cerca de los [puntos] (como `redonda` en `Pelajes.kt`). */
private fun redondaOr(puntos: List<Offset>): Path = Path().apply {
    val n = puntos.size
    fun medio(i: Int) = (puntos[Math.floorMod(i, n)] + puntos[Math.floorMod(i + 1, n)]) / 2f
    moveTo(medio(-1))
    for (i in 0 until n) { val a = medio(i - 1); val c = puntos[i]; val b = medio(i); cubicTo(a + (c - a) * 0.67f, b + (c - b) * 0.67f, b) }
    close()
}

/** Una línea suave por los [puntos], para un bajo o un pliegue (como `trazo` en `Pelajes.kt`). */
private fun trazoOr(puntos: List<Offset>): Path = Path().apply {
    moveTo(puntos.first())
    for (i in 1 until puntos.size - 1) { val a = (puntos[i - 1] + puntos[i]) / 2f; val b = (puntos[i] + puntos[i + 1]) / 2f; if (i == 1) lineTo(a); cubicTo(a + (puntos[i] - a) * 0.67f, b + (puntos[i] - b) * 0.67f, b) }
    lineTo(puntos.last())
}

/** Una línea quebrada por los [puntos], para un bajo cortado a tijera. */
private fun quebradaOr(puntos: List<Offset>): Path = Path().apply { moveTo(puntos.first()); for (q in puntos.drop(1)) lineTo(q) }

/**
 * El tronco del gato en una postura (como `Tronco` en `Pelajes.kt`): [en] da el punto a [t] del camino de la cadera (0)
 * al hombro (1) y a [h] del lomo (1 el lomo, 0 el medio, −1 el vientre); [s] y [co], desde dónde se le ve.
 */
private class TroncoOr(val p: PuntosGato) {
    private val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val deEspaldas = p.giro < -0.75f; val deFrente = p.giro > 0.75f
    private val medio = p.lomo + Offset(0f, 3.9f)
    val dir = unidadOr(p.hombro - p.cadera)
    val arriba = Offset(dir.y, -dir.x)
    val nuca = unidadOr(p.cabeza - p.hombro, Offset(0f, -1f)).let { Offset(it.y, -it.x) }
    fun centro(t: Float): Offset = if (t < 0.5f) p.cadera + (medio - p.cadera) * (t * 2f) else medio + (p.hombro - medio) * ((t - 0.5f) * 2f)
    fun radio(t: Float) = if (t < 0.5f) Gatuno.R_GRUPA + (3.9f - Gatuno.R_GRUPA) * t * 2f else 3.9f + (Gatuno.R_PECHO - 3.9f) * (t - 0.5f) * 2f
    fun en(t: Float, h: Float): Offset = centro(t) + arriba * (h * radio(t))
}

/** Pinta [bloque] sin tapar la cabeza (salvo de espaldas) ni, de espaldas, la cola en alto (como en `Pelajes.kt`). */
private inline fun DrawScope.sinTaparCabezaOr(p: PuntosGato, crossinline bloque: DrawScope.() -> Unit) {
    if (p.giro >= -0.25f) {
        clipPath(Path().apply { addOval(Rect(p.cabeza, Gatuno.R_CABEZA + 0.2f)) }, ClipOp.Difference) { bloque() }
        return
    }
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val raiz = p.cadera + Offset(-4f * sin(angulo).coerceIn(0f, 1f), -1.4f - 2.2f * abs(cos(angulo)))
    val v = p.puntaCola - raiz
    if (sqrt(v.x * v.x + v.y * v.y) < 2f) { bloque(); return }
    val n = unidadOr(v).let { Offset(-it.y, it.x) } * 2.6f
    clipPath(Path().apply { moveTo(raiz + n); lineTo(p.puntaCola + n); lineTo(p.puntaCola - n); lineTo(raiz - n); close() }, ClipOp.Difference) { bloque() }
}

/** El tabardo y la hebilla del collar. Echado, el tabardo llega al suelo: se apoya en él y no baja de los pies. */
private val tabardoOrden: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val k = TroncoOr(p)
    clipRect(-60f, -80f, 60f, 0.2f) {
        sinTaparCabezaOr(p) {
            when {
                k.deEspaldas -> tabardoDeEspaldas(p, luz)
                k.deFrente -> tabardoDeFrente(p, luz)
                else -> tabardoDeLado(k, luz)
            }
            hebilla(p, k, luz)
        }
    }
}

/** El latón del filete y de la hebilla: un poco más fiel a su color que la lana, porque es metal que devuelve la luz. */
private fun latonOr(luz: LuzGato) = lerp(luz.tono(OR_LATON), OR_LATON, 0.22f)

/** El carmesí: lo mismo, un poco, para que de noche no se vuelva pardo. */
private fun carmesiOr(luz: LuzGato, c: Color) = lerp(luz.tono(c), c, 0.12f)

/**
 * De perfil o de tres cuartos: el tabardo cubre los hombros y la parte de delante del lomo y baja por el costado hasta
 * media altura, con el bajo más oscuro; por delante baja por el hombro hasta el brazo. Es lana que cuelga: la luz le da
 * arriba y se apaga hacia el bajo, y sus pliegues caen a plomo sea cual sea la postura (sentado, el lomo va empinado y,
 * si siguieran al lomo, el paño parecía una mochila). El filete de latón va como galón junto al bajo: a media altura del
 * costado, como en el de pixel, con el gato sentado parecía la correa de una mochila.
 */
private fun DrawScope.tabardoDeLado(k: TroncoOr, luz: LuzGato) {
    val tela = carmesiOr(luz, OR_CARMESI); val clara = carmesiOr(luz, OR_CARMESI_LUZ); val honda = carmesiOr(luz, OR_CARMESI_OSC)
    val arriba = listOf(k.en(0.44f, 1.16f), k.en(0.7f, 1.22f), k.en(0.98f, 1.2f), k.en(1.12f, 0.6f) + k.dir * 1.8f, k.en(1.1f, 0f) + k.dir * 2.6f)
    val bajo = listOf(k.en(1.06f, -0.5f) + k.dir * 2.2f, k.en(0.9f, -0.58f), k.en(0.72f, -0.5f), k.en(0.54f, -0.44f), k.en(0.44f, 0.28f))
    // Redondo por arriba, sobre el lomo y el hombro; el bajo, cortado a tijera, con sus esquinas (redondo parecía un cojín).
    val forma = trazoOr(listOf(bajo.last()) + arriba + listOf(bajo.first())).apply { for (q in bajo.drop(1)) lineTo(q); close() }
    val y0 = (arriba + bajo).fold(99f) { a, q -> minOf(a, q.y) }; val y1 = (arriba + bajo).fold(-99f) { a, q -> maxOf(a, q.y) }
    drawPath(forma, Brush.verticalGradient(listOf(clara, tela, tela, honda), y0, y1 + 0.5f))
    clipPath(forma) {
        // Los pliegues, a plomo desde el lomo: una franja de sombra y, a su lado, la luz del pliegue.
        for ((t, x) in listOf(0.56f to 0.3f, 0.74f to -0.2f, 0.92f to 0.25f)) {
            val a = k.en(t, 1.25f)
            drawLine(honda.copy(alpha = 0.55f), a, Offset(a.x + x, y1 + 2f), 0.55f)
            drawLine(clara.copy(alpha = 0.4f), a + Offset(-0.75f, 0f), Offset(a.x - 0.75f + x, y1 + 2f), 0.4f)
        }
        // El bajo, más oscuro, con el galón de latón por encima.
        drawPath(quebradaOr(bajo), honda, style = Stroke(1.5f))
        val galon = quebradaOr(bajo.dropLast(1).map { it + k.arriba * 1.15f } + (bajo.last() + k.dir * 0.8f))
        drawPath(galon, luz.tono(OR_LATON_OSC), style = Stroke(0.95f))
        drawPath(galon, latonOr(luz), style = Stroke(0.62f))
    }
}

/** De espaldas: el tabardo sobre la espalda, con la costura del medio y el filete de latón a los dos lados. */
private fun DrawScope.tabardoDeEspaldas(p: PuntosGato, luz: LuzGato) {
    val tela = carmesiOr(luz, OR_CARMESI); val clara = carmesiOr(luz, OR_CARMESI_LUZ); val honda = carmesiOr(luz, OR_CARMESI_OSC)
    val x = (p.hombro.x + p.cadera.x) / 2f
    val y0 = p.hombro.y - Gatuno.R_PECHO - 0.4f; val y1 = p.hombro.y + maxOf(1.4f, (p.cadera.y - p.hombro.y) * 0.55f)
    val forma = redondaOr(listOf(Offset(x - 4.8f, y0), Offset(x + 4.8f, y0), Offset(x + 5.7f, y1), Offset(x + 1.8f, y1 + 0.6f), Offset(x - 1.8f, y1 + 0.6f), Offset(x - 5.7f, y1)))
    drawPath(forma, tela)
    clipPath(forma) {
        drawRect(clara, Offset(x - 6f, y0 - 1f), Size(12f, (y1 - y0) * 0.38f + 1f))
        drawLine(honda, Offset(x - 6f, y1 + 0.1f), Offset(x + 6f, y1 + 0.3f), 1.6f)
        drawLine(honda.copy(alpha = 0.55f), Offset(x, y0), Offset(x + 0.3f, y1 + 1f), 0.4f)
        for (l in listOf(-1f, 1f)) drawLine(latonOr(luz), Offset(x + l * 3.9f, y0 + 1.2f), Offset(x + l * 4.6f, y1 - 0.4f), 0.7f)
    }
}

/** De frente: las dos hojas del tabardo asoman sobre los hombros, a los lados del pecho claro, con su filete. */
private fun DrawScope.tabardoDeFrente(p: PuntosGato, luz: LuzGato) {
    val luzDe = if (luz.rayo.lat >= 0f) 1f else -1f
    for (lado in listOf(-1f, 1f)) {
        val x = p.hombro.x + 3.9f * lado
        val forma = redondaOr(listOf(Offset(x - 1.4f * lado, p.hombro.y - 3.6f), Offset(x + 2.1f * lado, p.hombro.y - 2.6f), Offset(x + 2.3f * lado, p.hombro.y + 4f), Offset(x - 0.6f * lado, p.hombro.y + 4.4f)))
        drawPath(forma, carmesiOr(luz, if (lado == luzDe) OR_CARMESI else OR_CARMESI_OSC))
        clipPath(forma) { drawLine(latonOr(luz), Offset(x - 0.9f * lado, p.hombro.y - 3f), Offset(x - 0.2f * lado, p.hombro.y + 4.4f), 0.7f) }
    }
}

/** La hebilla de latón del collar (el collar lo pinta el motor), con su brillo. De espaldas no se ve. */
private fun DrawScope.hebilla(p: PuntosGato, k: TroncoOr, luz: LuzGato) {
    if (p.giro < -0.25f) return
    val sitio = p.cuello - k.nuca * (3f * k.s) + Offset(0f, 0.9f + 2f * k.co.coerceAtLeast(0f))
    drawRect(luz.tono(OR_LATON_OSC), Offset(sitio.x - 0.85f, sitio.y - 0.7f), Size(1.7f, 1.5f))
    drawRect(latonOr(luz), Offset(sitio.x - 0.6f, sitio.y - 0.5f), Size(1.2f, 1.1f), style = Stroke(0.4f))
    drawCircle(OR_LATON_LUZ.copy(alpha = 0.75f * (1f - 0.5f * luz.noche)), 0.25f, sitio + Offset(-0.35f, -0.35f))
}

/** Una puntita de la mano (del color del manto) encima de lo que agarra. */
internal fun DrawScope.dedosOr(luz: LuzGato, en: Offset, r: Float = 1.25f) {
    drawOval(luz.tono(OR_MANTO), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f))
    drawOval(luz.tono(OR_RAYA).copy(alpha = 0.6f), Offset(en.x - r, en.y - r * 0.85f), Size(r * 2f, r * 1.7f), style = Stroke(0.25f))
    drawLine(luz.tono(OR_RAYA).copy(alpha = 0.7f), en + Offset(-0.1f, -r * 0.6f), en + Offset(0.1f, r * 0.5f), 0.2f)
}

/** El gato de Orden. Sus dos acciones están en `PelajeOrdenAcciones.kt` (el candil) y `PelajeOrdenPlaca.kt` (el peto). */
internal val PELAJE_ORDEN = Pelaje(
    manto = OR_MANTO, claro = OR_CLARO, marca = OR_RAYA, dibujo = DibujoPelaje.ATIGRADO, ojos = OR_OJOS,
    nariz = OR_NARIZ, collar = OR_LATON, accesorio = tabardoOrden,
    especiales = listOf(
        EspecialGato("alza el candil", true, 6, ::poseCandilOrden, candilDeLaOrden),
        EspecialGato("se cubre con una placa de caparazón", true, 6, ::posePlaca, placaDeCaparazon),
    ),
)
