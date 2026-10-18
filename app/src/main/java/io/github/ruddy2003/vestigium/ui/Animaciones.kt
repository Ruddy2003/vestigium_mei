package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Movimiento: secciones que entran escalonadas, cifras que cuentan hasta su valor, botones que se hunden
 * al tocarlos y un fondo vivo para cada tema (estrellas que titilan, polvo de vitral, hojas que caen…).
 * Todo respeta el interruptor de animaciones: si está apagado, las cosas aparecen ya quietas en su lugar.
 */

/** Aparece subiendo un poco y aclarándose. [orden] retrasa unos milisegundos, para que las secciones entren una tras otra. */
fun Modifier.entrada(orden: Int = 0): Modifier = composed {
    val animar = Repo.config.animaciones
    val p = remember { Animatable(if (animar) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (p.value < 1f) { delay((orden.coerceIn(0, 8) * 55L)); p.animateTo(1f, tween(420, easing = FastOutSlowInEasing)) }
    }
    graphicsLayer { alpha = p.value; translationY = (1f - p.value) * 18.dp.toPx() }
}

/** Se hunde un poco mientras el dedo lo aprieta. */
fun Modifier.presion(interaccion: MutableInteractionSource, cuanto: Float = 0.95f): Modifier = composed {
    val apretado by interaccion.collectIsPressedAsState()
    val animar = Repo.config.animaciones
    val s by animateFloatAsState(if (apretado && animar) cuanto else 1f, spring(dampingRatio = 0.5f, stiffness = 900f), label = "presion")
    graphicsLayer { scaleX = s; scaleY = s }
}

private val NUMERO = Regex("^(\\D*?)(\\d[\\d,]*(?:\\.\\d+)?)(.*)$", RegexOption.DOT_MATCHES_ALL)

/** El texto de una cifra contando desde cero hasta su valor («$1,250» pasa por «$312», «$874»…). Sin animaciones, el valor tal cual. */
@Composable
fun contando(valor: String): String {
    val animar = Repo.config.animaciones
    val m = NUMERO.find(valor)
    val objetivo = m?.groupValues?.get(2)?.replace(",", "")?.toDoubleOrNull()
    if (!animar || m == null || objetivo == null || objetivo == 0.0) return valor
    val p = remember(valor) { Animatable(0f) }
    LaunchedEffect(valor) { p.animateTo(1f, tween(750, easing = FastOutSlowInEasing)) }
    if (p.value >= 1f) return valor
    val (antes, num, despues) = Triple(m.groupValues[1], m.groupValues[2], m.groupValues[3])
    val decimales = num.substringAfter('.', "").length
    val miles = num.contains(',')
    val v = objetivo * p.value
    val texto = if (miles) String.format(java.util.Locale.US, "%,.${decimales}f", v) else String.format(java.util.Locale.US, "%.${decimales}f", v)
    return antes + texto + despues
}

// =====================================================================
//  Fondo vivo: una capa muy tenue que se mueve detrás de todo
// =====================================================================

private class Mota(val x: Float, val y: Float, val v: Float, val fase: Float, val tam: Float, val tono: Int)

/** Lo que flota en el fondo de cada tema. Si el tema no tiene nada que mover, no dibuja nada (ni gasta batería). */
@Composable
fun FondoVivo(tema: TemaId = Estilo.tema, fijo: Float? = null) {
    val c = Repo.config
    // [fijo]: un instante quieto (para el fondo del teléfono), aunque las animaciones estén apagadas.
    if (fijo == null && (!c.animaciones || !c.fondo_vivo)) return
    val e = MaterialTheme.colorScheme
    val oscuro = e.background.luminance() < 0.5f
    val tipo = when (tema) {
        TemaId.ESPACIO -> "estrellas"
        TemaId.GOTICO -> "vitral"
        TemaId.BOTANICO -> if (oscuro) "luciernagas" else "polen"
        TemaId.NAVIDAD -> "nieve"
        TemaId.ANDALUS -> "agua"
        TemaId.MONGOL -> "nubes"
        TemaId.CIBER -> "lluvia"
        TemaId.RENACIMIENTO -> "polvo"
        TemaId.SEDA -> "arena"
        TemaId.HELENO -> if (oscuro) "estrellas" else null
        TemaId.REFUGIO -> if (oscuro) "barrido" else null
        else -> null
    } ?: return
    val motas = remember(tema) {
        val r = java.util.Random(tema.ordinal.toLong() + 5)
        List(if (tipo == "lluvia") 34 else 26) { Mota(r.nextFloat(), r.nextFloat(), 0.4f + r.nextFloat() * 0.6f, r.nextFloat(), 0.5f + r.nextFloat(), r.nextInt(3)) }
    }
    val periodo = when (tipo) { "nubes" -> 90000; "agua" -> 14000; "estrellas", "luciernagas" -> 9000; "lluvia" -> 4000; "barrido" -> 7000; else -> 24000 }
    val t = if (fijo != null) fijo else {
        val v by rememberInfiniteTransition(label = "fondo").animateFloat(0f, 1f, infiniteRepeatable(tween(periodo, easing = LinearEasing)), label = "t")
        v
    }
    val tonos = listOf(e.primary, e.secondary, e.tertiary)
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height; val u = 1.dp.toPx()
        when (tipo) {
            "estrellas" -> motas.forEach { m ->
                val a = 0.15f + 0.55f * (0.5f + 0.5f * sin(2 * PI * (t * 3 * m.v + m.fase)).toFloat())
                val p = Offset(m.x * w, m.y * h); val s = 2.2f * u * m.tam
                val col = (if (oscuro) Color.White else e.primary).copy(alpha = a * (if (oscuro) 0.6f else 0.25f))
                drawLine(col, p - Offset(s, 0f), p + Offset(s, 0f), 0.9f * u); drawLine(col, p - Offset(0f, s), p + Offset(0f, s), 0.9f * u)
                drawCircle(col, 0.9f * u, p)
            }
            "vitral" -> motas.take(14).forEach { m ->
                // Manchas de luz de colores que cruzan despacio, como el sol a través de las vidrieras.
                val x = ((m.x + t * 0.25f * m.v) % 1.2f - 0.1f) * w; val y = (m.y + 0.03f * sin(2 * PI * (t + m.fase)).toFloat()) * h
                val col = listOf(e.secondary, if (oscuro) e.tertiary else e.primary, Color(0xFF2E8B57), Color(0xFFE2B34A))[m.tono % 4]
                drawCircle(Brush.radialGradient(listOf(col.copy(alpha = if (oscuro) 0.12f else 0.09f), Color.Transparent), center = Offset(x, y), radius = 46f * u * m.tam), 46f * u * m.tam, Offset(x, y))
            }
            "polen", "polvo", "arena" -> motas.forEach { m ->
                val y = ((m.y + t * m.v * 0.6f) % 1f) * h
                val x = (m.x + 0.02f * sin(2 * PI * (t * 4 + m.fase)).toFloat()) * w
                val col = when (tipo) { "polen" -> Color(0xFFE2B84A); "arena" -> tonos[2]; else -> e.onBackground }.copy(alpha = if (tipo == "polvo") 0.08f else 0.18f)
                if (tipo == "polen" && m.tono == 0) rotate(360f * (t * 3 + m.fase), Offset(x, y)) {
                    // Algunas son hojitas que caen girando.
                    val l = 5f * u * m.tam
                    drawPath(Path().apply { moveTo(x - l, y); quadraticTo(x, y - l * 0.7f, x + l, y); quadraticTo(x, y + l * 0.7f, x - l, y); close() }, e.primary.copy(alpha = 0.16f))
                } else drawCircle(col, 1.3f * u * m.tam, Offset(x, y))
            }
            "luciernagas" -> motas.forEach { m ->
                val x = (m.x + 0.04f * sin(2 * PI * (t * m.v + m.fase)).toFloat()) * w
                val y = (m.y + 0.03f * cos(2 * PI * (t * m.v * 1.3f + m.fase)).toFloat()) * h
                val a = (sin(2 * PI * (t * 2 * m.v + m.fase)).toFloat()).coerceAtLeast(0f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFD8F07A).copy(alpha = 0.45f * a), Color.Transparent), center = Offset(x, y), radius = 9f * u), 9f * u, Offset(x, y))
                drawCircle(Color(0xFFF2FFB0).copy(alpha = 0.8f * a), 1.4f * u, Offset(x, y))
            }
            "nieve" -> motas.forEach { m ->
                val y = ((m.y + t * m.v) % 1f) * h
                val x = (m.x + 0.015f * sin(2 * PI * (t * 6 + m.fase)).toFloat()) * w
                drawCircle((if (oscuro) Color.White else e.secondary).copy(alpha = if (oscuro) 0.35f else 0.14f), 1.8f * u * m.tam, Offset(x, y))
            }
            "agua" -> {
                // Reflejos de una alberca: líneas onduladas que se mecen.
                for (k in 0 until 6) {
                    val y0 = h * (0.15f + k * 0.15f)
                    val p = Path(); var x = 0f
                    p.moveTo(0f, y0)
                    while (x <= w) { p.lineTo(x, y0 + 5f * u * sin(2 * PI * (x / (90f * u) + t + k * 0.3f)).toFloat()); x += 6f * u }
                    drawPath(p, e.secondary.copy(alpha = if (oscuro) 0.06f else 0.05f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.2f * u))
                }
            }
            "nubes" -> motas.take(6).forEach { m ->
                val x = ((m.x + t * m.v * 0.5f) % 1.4f - 0.2f) * w; val y = m.y * h * 0.6f
                val col = (if (oscuro) Color.White else Color.White).copy(alpha = if (oscuro) 0.025f else 0.35f)
                listOf(Offset(0f, 0f) to 26f, Offset(22f, -8f) to 20f, Offset(-22f, -4f) to 18f, Offset(40f, 4f) to 14f).forEach { (o, r) ->
                    drawCircle(col, r * u * m.tam, Offset(x + o.x * u * m.tam, y + o.y * u * m.tam))
                }
            }
            "lluvia" -> motas.forEach { m ->
                val y = ((m.y + t * m.v * 2f) % 1.1f) * h
                val x = m.x * w
                drawLine(tonos[m.tono % 2].copy(alpha = 0.12f), Offset(x, y), Offset(x, y + 14f * u * m.tam), 1f * u)
            }
            "barrido" -> drawRect(e.primary.copy(alpha = 0.035f), Offset(0f, t * h), Size(w, 26f * u))
        }
    }
}

// =====================================================================
//  Al cambiar de tema: el tema nuevo se abre en círculo desde donde tocaste
// =====================================================================

/** La pantalla tal como estaba con el tema anterior, y desde dónde se abre el nuevo. */
object TransicionTema {
    var foto by androidx.compose.runtime.mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null)
    var centro by androidx.compose.runtime.mutableStateOf(Offset.Zero)
    var vuelta by androidx.compose.runtime.mutableIntStateOf(0)

    /** Toma la foto de la pantalla antes de cambiar el tema. Si no se puede (o no hay animaciones), simplemente no hay transición. */
    fun preparar(vista: android.view.View, centroEnVentana: Offset) {
        if (!Repo.config.animaciones) return
        runCatching {
            val raiz = vista.rootView
            if (raiz.width <= 0 || raiz.height <= 0) return
            val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
            raiz.draw(android.graphics.Canvas(bmp))
            foto = bmp.asImageBitmap()
            centro = centroEnVentana
            vuelta++
        }
    }
}

@Composable
fun CapaTransicionTema() {
    val foto = TransicionTema.foto ?: return
    val vuelta = TransicionTema.vuelta
    val p = remember(vuelta) { Animatable(0f) }
    val color = MaterialTheme.colorScheme.primary
    var origen by remember { androidx.compose.runtime.mutableStateOf(Offset.Zero) }
    LaunchedEffect(vuelta) {
        p.animateTo(1f, tween(720, easing = FastOutSlowInEasing))
        if (TransicionTema.vuelta == vuelta) TransicionTema.foto = null
    }
    Canvas(Modifier.fillMaxSize().onGloballyPositioned { origen = it.positionInWindow() }) {
        val c = TransicionTema.centro - origen
        val r = listOf(Offset(0f, 0f), Offset(size.width, 0f), Offset(0f, size.height), Offset(size.width, size.height)).maxOf { (it - c).getDistance() }
        val radio = r * p.value
        val agujero = androidx.compose.ui.graphics.Path().apply { addOval(androidx.compose.ui.geometry.Rect(c, radio)) }
        clipPath(agujero, androidx.compose.ui.graphics.ClipOp.Difference) {
            drawImage(foto, topLeft = -origen)
        }
        // El borde del círculo con el color del tema nuevo.
        drawCircle(color.copy(alpha = 0.9f * (1f - p.value)), radio, c, style = androidx.compose.ui.graphics.drawscope.Stroke(6.dp.toPx() * (1f - p.value * 0.6f)))
    }
}
