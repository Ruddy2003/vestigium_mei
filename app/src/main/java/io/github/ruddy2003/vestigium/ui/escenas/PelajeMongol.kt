package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
//  Mongol: gris pardo con motas, con su collar azul de colgante de oro y su gorro rojo de punta
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/mongol_*`, `herramientas/gato/gato8.py`): gris pardo, del color del
// fieltro sucio (#B0A698), de pecho claro y con motas oscuras sueltas (el dibujo «mechones»), ojos ámbar, un collar azul
// con un colgante de oro y un gorro rojo de punta, con el ala parda vuelta hacia arriba y un botón de oro en lo alto.
// Sus dos acciones propias, las mismas que el de pixel: seguir con la vista a un águila que vuela en círculos (cuerpo
// pardo, cabeza blanca y pico de oro) y beber de su cuenco de madera, de filete azul, con leche que humea.
//
// Las motas no son un dibujo del esqueleto común (`DibujoPelaje`): el gato es liso y las motas se le pintan encima, en
// el lomo y el costado, como lo puesto.

private val G_MANTO = Color(0xFFB0A698)
private val G_MOTA = Color(0xFF887E72)
private val G_AZUL = Color(0xFF1F5FA8)
private val G_ORO = Color(0xFFC48A1E)
private val G_ORO_LUZ = Color(0xFFEAC266)
private val G_ROJO = Color(0xFFC2412D)
private val G_ROJO_LUZ = Color(0xFFDC6A50)
private val G_ROJO_OSC = Color(0xFF822A20)
private val G_ALA = Color(0xFF8A6A44)
private val G_ALA_OSC = Color(0xFF5E4630)
private val G_AGUILA = Color(0xFF6A4A2A)
private val G_BLANCO = Color(0xFFF1EDE3)
private val G_MADERA = Color(0xFF8A5A3A)
private val G_LECHE = Color(0xFFF6F2E8)

private fun unidadM(v: Offset, siNo: Offset = Offset(1f, 0f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** Las motas del lomo y del costado, recortadas al tronco; el collar con su colgante; el gorro. */
private val puestoMongol: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo)
    // ---- Las motas: unas manchitas oscuras, sueltas, de borde blando, más en el lomo que en el vientre.
    if (s > 0.3f) {
        val medio = (p.cadera + p.hombro) / 2f + Offset(0f, -0.2f)
        val tronco = Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(p.cadera, Gatuno.R_GRUPA - 0.3f)); addOval(androidx.compose.ui.geometry.Rect(p.hombro, Gatuno.R_PECHO - 0.3f))
            addOval(androidx.compose.ui.geometry.Rect(medio, 3.6f))
            val d = unidadM(p.hombro - p.cadera); val n = Offset(-d.y, d.x)
            moveTo(p.cadera.x + n.x * 4.3f, p.cadera.y + n.y * 4.3f); lineTo(p.hombro.x + n.x * 4.1f, p.hombro.y + n.y * 4.1f); lineTo(p.hombro.x - n.x * 4.1f, p.hombro.y - n.y * 4.1f); lineTo(p.cadera.x - n.x * 4.3f, p.cadera.y - n.y * 4.3f); close()
        }
        clipPath(tronco) {
            val mota = luz.tono(G_MOTA)
            val d = p.hombro - p.cadera
            val arriba = unidadM(Offset(d.y, -d.x)).let { if (it.y > 0f) it * -1f else it }
            // Lugares fijos en el tronco (a lo largo, y de lomo a vientre): siempre las mismas motas.
            val sitios = listOf(0.08f to 0.75f, 0.22f to 0.35f, 0.3f to 0.85f, 0.44f to 0.5f, 0.55f to 0.1f, 0.62f to 0.8f, 0.74f to 0.42f, 0.86f to 0.7f, 0.38f to -0.1f, 0.92f to 0.15f)
            for ((k, par) in sitios.withIndex()) {
                val (t, h) = par
                val c = p.cadera + d * t + arriba * (h * 3.4f)
                val r = if (k % 3 == 0) 1.15f else 0.85f
                drawOval(mota.copy(alpha = 0.75f), Offset(c.x - r * 1.3f, c.y - r * 0.8f), Size(r * 2.6f, r * 1.6f))
            }
            // Un poco de pelo más claro en el lomo, donde le da la luz.
            drawLine(luz.tono(lerp(G_MANTO, Color.White, 0.3f)).copy(alpha = 0.35f), p.cadera + arriba * 3.6f, p.hombro + arriba * 3.4f, 1.1f, StrokeCap.Round)
        }
    }
    // ---- El colgante de oro, bajo el collar, por delante.
    if (p.giro >= -0.25f) {
        val cuello = unidadM(p.cabeza - p.hombro)
        val sitio = p.cuello - cuello * 2.8f + Offset(1.4f * s, 1.8f)
        drawLine(luz.tono(G_ORO), sitio + Offset(0f, -1.4f), sitio, 0.35f)
        drawPath(Path().apply { moveTo(sitio.x, sitio.y - 0.4f); lineTo(sitio.x + 1.1f, sitio.y + 0.9f); lineTo(sitio.x, sitio.y + 2.3f); lineTo(sitio.x - 1.1f, sitio.y + 0.9f); close() }, luz.tono(G_ORO))
        drawCircle(luz.tono(G_ORO_LUZ), 0.4f, sitio + Offset(-0.3f, 0.6f))
    }
    // ---- El gorro: un cono rojo, el ala parda vuelta hacia arriba alrededor y un botón de oro en la punta.
    val r = Gatuno.R_CABEZA
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    val c = p.cabeza + Offset(-0.4f * s, -3.4f)
    val inclina = if (co < -0.3f) -1f else 1f                                    // la punta se va un poco hacia la nuca
    val punta = c + Offset(-inclina * 1.4f * s, -8.6f)
    val cono = Path().apply {
        moveTo(c.x - r * 0.78f, c.y)
        cubicTo(c.x - r * 0.7f, c.y - 3f, punta.x - 1.6f, punta.y + 3f, punta.x, punta.y)
        cubicTo(punta.x + 1.6f, punta.y + 3f, c.x + r * 0.7f, c.y - 3f, c.x + r * 0.78f, c.y)
        close()
    }
    drawPath(cono, Brush.horizontalGradient(if (lado > 0f) listOf(luz.tono(G_ROJO_OSC), luz.tono(G_ROJO), luz.tono(G_ROJO_LUZ)) else listOf(luz.tono(G_ROJO_LUZ), luz.tono(G_ROJO), luz.tono(G_ROJO_OSC)), c.x - r * 0.8f, c.x + r * 0.8f))
    // Las costuras del gorro: de la punta al ala.
    for (k in -1..1) drawLine(luz.tono(G_ROJO_OSC).copy(alpha = 0.55f), punta + Offset(0f, 1.2f), c + Offset(k * r * 0.42f, -0.9f), 0.25f)
    // El ala, vuelta hacia arriba: una banda parda con su canto claro.
    val ala = Path().apply {
        moveTo(c.x - r * 1.02f, c.y + 0.9f); cubicTo(c.x - r * 0.95f, c.y - 1.2f, c.x - r * 0.5f, c.y - 1.9f, c.x, c.y - 1.9f)
        cubicTo(c.x + r * 0.5f, c.y - 1.9f, c.x + r * 0.95f, c.y - 1.2f, c.x + r * 1.02f, c.y + 0.9f)
        cubicTo(c.x + r * 0.5f, c.y + 0.3f, c.x - r * 0.5f, c.y + 0.3f, c.x - r * 1.02f, c.y + 0.9f); close()
    }
    drawPath(ala, Brush.horizontalGradient(if (lado > 0f) listOf(luz.tono(G_ALA_OSC), luz.tono(G_ALA), luz.tono(lerp(G_ALA, G_BLANCO, 0.3f))) else listOf(luz.tono(lerp(G_ALA, G_BLANCO, 0.3f)), luz.tono(G_ALA), luz.tono(G_ALA_OSC)), c.x - r, c.x + r))
    drawPath(Path().apply { moveTo(c.x - r * 0.95f, c.y - 0.6f); cubicTo(c.x - r * 0.5f, c.y - 1.7f, c.x + r * 0.5f, c.y - 1.7f, c.x + r * 0.95f, c.y - 0.6f) }, luz.tono(lerp(G_ALA, G_BLANCO, 0.45f)).copy(alpha = 0.7f), style = Stroke(0.35f, cap = StrokeCap.Round))
    // El botón de oro y la borla roja que cae de él.
    drawLine(luz.tono(G_ROJO_OSC), punta, punta + Offset(inclina * 1.6f, 2.6f), 0.5f, StrokeCap.Round)
    drawCircle(luz.tono(G_ORO), 1f, punta); drawCircle(luz.tono(G_ORO_LUZ), 0.38f, punta + Offset(-0.3f * lado, -0.3f))
}

// ---- Sigue al águila.

private fun poseAguila(c: Int): Pose {
    val q = Math.floorMod(c, 8)
    // Sentado, con el morro en alto: la cabeza sigue al águila que da vueltas, de un lado a otro.
    val gira = listOf(0f, 0.25f, 0.5f, 0.7f, 0.8f, 0.6f, 0.3f, 0.1f)[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.2f + 1.4f * gira, -26.6f - 0.6f * gira), morro = -0.55f - 0.2f * gira, orejas = 1f,
        ojos = if (q == 5) 0.3f else 1f, cara = 0.2f + 0.5f * gira, cola2 = listOf(2.2f, 2.4f, 2.6f, 2.7f, 2.6f, 2.4f, 2.3f, 2.2f)[q],
    )
}

/** El águila que da vueltas en lo alto, delante y por encima del gato: aletea despacio y planea. */
private val aguila: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 8)
    val a = (2 * PI * q / 8).toFloat()
    val o = Offset(22f + 9f * cos(a), -52f + 3.6f * sin(a))
    val vuelta = if (sin(a) > 0f) 1f else -1f                                   // hacia dónde vuela en cada tramo del círculo
    val ala = listOf(0f, -0.6f, -1f, -0.4f, 0.3f, 0.7f, 0.3f, -0.2f)[q]          // arriba (−1) o abajo (1)
    val cuerpo = luz.tono(G_AGUILA); val clara = luz.tono(lerp(G_AGUILA, G_BLANCO, 0.25f)); val osc = luz.tono(lerp(G_AGUILA, Color(0xFF1E1A22), 0.4f))
    // Las dos alas, anchas, con los dedos de las plumas abiertos en la punta.
    for (lado in listOf(-1f, 1f)) {
        val hombro = o + Offset(lado * 1.2f, 0f)
        val codo = hombro + Offset(lado * 4.6f, ala * 2.4f - 0.6f)
        val punta = codo + Offset(lado * 4.4f, ala * 2.2f + 0.4f)
        val forma = Path().apply {
            moveTo(hombro.x, hombro.y - 0.9f); cubicTo(codo.x - lado * 1.6f, codo.y - 1.4f, codo.x, codo.y - 1.2f, punta.x, punta.y)
            for (k in 0..3) { val t = k / 3f; lineTo(punta.x - lado * (0.9f + 1.4f * t), punta.y + 1.2f + 0.6f * t); if (k < 3) lineTo(punta.x - lado * (1.5f + 1.4f * t), punta.y + 0.8f + 0.5f * t) }
            cubicTo(codo.x - lado * 1f, codo.y + 2.2f, hombro.x + lado * 1.8f, hombro.y + 1.6f, hombro.x, hombro.y + 1f); close()
        }
        drawPath(forma, if (lado == vuelta) cuerpo else osc)
        drawLine(clara.copy(alpha = 0.6f), hombro + Offset(0f, -0.6f), codo + Offset(0f, -0.8f), 0.45f, StrokeCap.Round)
    }
    // El cuerpo, la cola en abanico, la cabeza blanca y el pico de oro.
    drawOval(cuerpo, Offset(o.x - 1.4f, o.y - 1.2f), Size(2.8f, 4.4f))
    drawPath(Path().apply { moveTo(o.x - 0.9f, o.y + 2.6f); lineTo(o.x - 1.7f, o.y + 5f); lineTo(o.x + 1.7f, o.y + 5f); lineTo(o.x + 0.9f, o.y + 2.6f); close() }, osc)
    val cabeza = o + Offset(vuelta * 0.4f, -1.9f)
    drawCircle(luz.tono(G_BLANCO), 1.15f, cabeza)
    drawPath(Path().apply { moveTo(cabeza.x + vuelta * 0.8f, cabeza.y - 0.4f); lineTo(cabeza.x + vuelta * 2.1f, cabeza.y + 0.1f); lineTo(cabeza.x + vuelta * 0.9f, cabeza.y + 0.6f); close() }, luz.tono(G_ORO))
}

// ---- Bebe de su cuenco.

private fun poseCuenco(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, se inclina hasta el cuenco, lame la leche y levanta la cabeza para relamerse.
    return Gatuno.SENTADO.copy(
        cabeza = listOf(Offset(6f, -23.6f), Offset(9f, -17.6f), Offset(11.4f, -12.4f), Offset(11.6f, -12f), Offset(8.6f, -18.4f), Offset(5.8f, -24.4f))[q],
        hombro = listOf(Offset(2.6f, -16.4f), Offset(3.6f, -14.6f), Offset(4.6f, -12.6f), Offset(4.6f, -12.4f), Offset(3.4f, -14.8f), Offset(2.6f, -16.4f))[q],
        morro = listOf(0.3f, 0.75f, 1f, 1f, 0.6f, 0.15f)[q], orejas = listOf(1f, 0.9f, 0.8f, 0.8f, 0.9f, 1f)[q],
        ojos = listOf(1f, 0.6f, 0.2f, 0.2f, 0.5f, 0.4f)[q], lengua = q == 2 || q == 3 || q == 5, cola2 = listOf(2.1f, 2.3f, 2.5f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El cuenco de madera, de filete azul, con la leche que humea; el gato lame y salpica una gota. */
private val cuencoDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val x = 16.4f; val ancho = 5.2f
    val madera = luz.tono(G_MADERA); val oscura = luz.tono(lerp(G_MADERA, Color(0xFF2A1E1A), 0.45f)); val clara = luz.tono(lerp(G_MADERA, Color(0xFFF2E2C2), 0.35f))
    drawOval(Color.Black.copy(alpha = 0.2f), Offset(x - ancho - 0.6f, -0.9f), Size(ancho * 2f + 1.2f, 1.8f))
    val cuerpo = Path().apply { moveTo(x - ancho, -4f); cubicTo(x - ancho, -1.6f, x - ancho * 0.55f, 0f, x, 0f); cubicTo(x + ancho * 0.55f, 0f, x + ancho, -1.6f, x + ancho, -4f); close() }
    drawPath(cuerpo, Brush.horizontalGradient(if (luz.rayo.lat >= 0f) listOf(oscura, madera, clara) else listOf(clara, madera, oscura), x - ancho, x + ancho))
    // La veta del torno y el filete azul del borde.
    for (k in 1..2) drawLine(oscura.copy(alpha = 0.5f), Offset(x - ancho * (0.95f - 0.12f * k), -4f + 1.3f * k), Offset(x + ancho * (0.95f - 0.12f * k), -4f + 1.3f * k), 0.2f)
    drawOval(luz.tono(G_AZUL), Offset(x - ancho, -5f), Size(ancho * 2f, 2f))
    drawOval(luz.tono(G_LECHE), Offset(x - ancho * 0.84f, -4.75f), Size(ancho * 1.68f, 1.5f))
    drawOval(Color.White.copy(alpha = 0.5f), Offset(x - ancho * 0.5f, -4.6f), Size(ancho * 0.6f, 0.5f))
    // El vaho de la leche caliente, que sube y se tuerce.
    for (i in 0..1) {
        val f = (q + i * 3) % 6; val y = -5.6f - f * 0.9f
        drawPath(Path().apply { moveTo(x - 1f + i * 2.2f, y); cubicTo(x - 2.4f + i * 2.2f, y - 1.6f, x + 0.6f + i * 2.2f, y - 2.4f, x - 0.8f + i * 2.2f, y - 4.2f) }, Color.White.copy(alpha = (0.5f - 0.07f * f) * (1f - 0.4f * luz.noche)), style = Stroke(0.55f, cap = StrokeCap.Round))
    }
    if (q == 3) drawCircle(luz.tono(G_LECHE), 0.45f, Offset(x - ancho - 0.6f, -5.6f))
}

/** El gato de Mongol. */
internal val PELAJE_MONGOL = Pelaje(
    manto = G_MANTO, claro = Color(0xFFDBD7D1), marca = G_MOTA, dibujo = DibujoPelaje.LISO, ojos = G_ORO,
    nariz = Color(0xFFE296A0), collar = G_AZUL, accesorio = puestoMongol,
    especiales = listOf(EspecialGato("sigue al águila", true, 8, ::poseAguila, aguila), EspecialGato("bebe de su cuenco", true, 6, ::poseCuenco, cuencoDelGato)),
)
