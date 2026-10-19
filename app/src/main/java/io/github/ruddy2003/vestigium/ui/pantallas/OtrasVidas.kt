@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.drawText
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.TusNumeros
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.dibujarEmblema
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sin

// =====================================================================
//  Medallas: cada vida con su objeto
// =====================================================================

/** El objeto de cada vida, dibujado: mano de cueva, espiga, pirámide, ánfora, torre, mazorca, abanico, fábrica, maguey, televisión, globo, teléfono. */
internal fun DrawScope.objetoVida(dibujo: String, c: Offset, r: Float, tinta: Color, acento: Color, fondo: Color) {
    val g = r * 0.07f
    when (dibujo) {
        "mano" -> {
            // Mano en negativo, como en las cuevas: ocre soplado alrededor y la mano en el color del fondo.
            drawCircle(Brush.radialGradient(listOf(acento.copy(alpha = 0.85f), acento.copy(alpha = 0.0f)), c, r * 0.95f), r * 0.95f, c)
            val palma = Rect(c.x - r * 0.3f, c.y - r * 0.05f, c.x + r * 0.3f, c.y + r * 0.55f)
            drawRoundRect(fondo, palma.topLeft, palma.size, CornerRadius(r * 0.18f))
            listOf(-0.22f to 0.62f, -0.07f to 0.75f, 0.08f to 0.72f, 0.22f to 0.58f).forEach { (dx, l) ->
                drawRoundRect(fondo, Offset(c.x + r * dx - r * 0.07f, c.y - r * l), Size(r * 0.14f, r * (l + 0.1f)), CornerRadius(r * 0.07f))
            }
            rotate(-40f, Offset(c.x - r * 0.3f, c.y + r * 0.2f)) { drawRoundRect(fondo, Offset(c.x - r * 0.37f, c.y - r * 0.2f), Size(r * 0.14f, r * 0.42f), CornerRadius(r * 0.07f)) }
        }
        "espiga" -> {
            drawLine(tinta, Offset(c.x, c.y + r * 0.8f), Offset(c.x, c.y - r * 0.75f), g, StrokeCap.Round)
            for (k in 0 until 6) {
                val y = c.y - r * 0.6f + k * r * 0.22f
                listOf(-1f, 1f).forEach { lado ->
                    rotate(lado * 30f, Offset(c.x, y)) { drawOval(acento, Offset(c.x + (if (lado < 0) -r * 0.3f else 0f), y - r * 0.08f), Size(r * 0.3f, r * 0.16f)) }
                    drawLine(tinta.copy(alpha = 0.6f), Offset(c.x + lado * r * 0.26f, y - r * 0.1f), Offset(c.x + lado * r * 0.45f, y - r * 0.32f), g * 0.4f)
                }
            }
        }
        "piramide" -> {
            drawCircle(acento, r * 0.22f, Offset(c.x + r * 0.42f, c.y - r * 0.5f))
            drawPath(Path().apply { moveTo(c.x - r * 0.8f, c.y + r * 0.55f); lineTo(c.x - r * 0.05f, c.y - r * 0.45f); lineTo(c.x + r * 0.7f, c.y + r * 0.55f); close() }, tinta)
            drawPath(Path().apply { moveTo(c.x - r * 0.05f, c.y - r * 0.45f); lineTo(c.x + r * 0.7f, c.y + r * 0.55f); lineTo(c.x + r * 0.15f, c.y + r * 0.55f); close() }, lerp(tinta, fondo, 0.35f))
            drawLine(acento, Offset(c.x - r * 0.95f, c.y + r * 0.62f), Offset(c.x + r * 0.95f, c.y + r * 0.62f), g)
        }
        "anfora" -> {
            val p = Path().apply {
                moveTo(c.x - r * 0.12f, c.y - r * 0.8f); lineTo(c.x + r * 0.12f, c.y - r * 0.8f); lineTo(c.x + r * 0.12f, c.y - r * 0.5f)
                cubicTo(c.x + r * 0.55f, c.y - r * 0.4f, c.x + r * 0.45f, c.y + r * 0.3f, c.x + r * 0.05f, c.y + r * 0.7f)
                lineTo(c.x, c.y + r * 0.9f); lineTo(c.x - r * 0.05f, c.y + r * 0.7f)
                cubicTo(c.x - r * 0.45f, c.y + r * 0.3f, c.x - r * 0.55f, c.y - r * 0.4f, c.x - r * 0.12f, c.y - r * 0.5f); close()
            }
            drawPath(p, acento); drawPath(p, tinta, style = Stroke(g * 0.7f))
            listOf(-1f, 1f).forEach { l -> drawPath(Path().apply { moveTo(c.x + l * r * 0.12f, c.y - r * 0.7f); quadraticTo(c.x + l * r * 0.45f, c.y - r * 0.72f, c.x + l * r * 0.3f, c.y - r * 0.38f) }, tinta, style = Stroke(g)) }
            drawLine(tinta.copy(alpha = 0.5f), Offset(c.x - r * 0.38f, c.y - r * 0.1f), Offset(c.x + r * 0.38f, c.y - r * 0.1f), g * 0.5f)
        }
        "castillo" -> {
            val l = c.x - r * 0.42f; val d = c.x + r * 0.42f; val arr = c.y - r * 0.45f; val ab = c.y + r * 0.75f
            drawRect(tinta, Offset(l, arr), Size(d - l, ab - arr))
            for (k in 0 until 4) drawRect(tinta, Offset(l + k * (d - l) / 3.5f, arr - r * 0.2f), Size((d - l) / 7f, r * 0.2f))
            drawPath(Path().apply { moveTo(c.x - r * 0.15f, ab); lineTo(c.x - r * 0.15f, c.y + r * 0.35f); quadraticTo(c.x, c.y + r * 0.15f, c.x + r * 0.15f, c.y + r * 0.35f); lineTo(c.x + r * 0.15f, ab); close() }, fondo)
            drawRect(acento, Offset(c.x - r * 0.06f, arr + r * 0.15f), Size(r * 0.12f, r * 0.2f))
        }
        "maiz" -> {
            listOf(-1f, 1f).forEach { l -> drawPath(Path().apply { moveTo(c.x, c.y + r * 0.85f); quadraticTo(c.x + l * r * 0.7f, c.y + r * 0.2f, c.x + l * r * 0.25f, c.y - r * 0.6f); quadraticTo(c.x + l * r * 0.35f, c.y + r * 0.2f, c.x, c.y + r * 0.85f); close() }, tinta) }
            drawOval(acento, Offset(c.x - r * 0.24f, c.y - r * 0.8f), Size(r * 0.48f, r * 1.4f))
            for (fy in 0 until 7) for (fx in -1..1) drawCircle(lerp(acento, tinta, 0.35f), r * 0.045f, Offset(c.x + fx * r * 0.12f, c.y - r * 0.6f + fy * r * 0.18f))
        }
        "abanico" -> {
            val piv = Offset(c.x, c.y + r * 0.6f)
            for (k in 0..8) {
                val a = (-PI * 0.85 + k * PI * 0.7 / 8).toFloat()
                val p = Offset(piv.x + r * 1.15f * cos(a), piv.y + r * 1.15f * sin(a))
                drawLine(tinta, piv, p, g * 0.6f)
            }
            drawArc(acento.copy(alpha = 0.9f), -153f, 126f, true, Offset(piv.x - r * 1.15f, piv.y - r * 1.15f), Size(r * 2.3f, r * 2.3f))
            drawArc(fondo, -153f, 126f, true, Offset(piv.x - r * 0.45f, piv.y - r * 0.45f), Size(r * 0.9f, r * 0.9f))
            for (k in 0..8) { val a = (-PI * 0.85 + k * PI * 0.7 / 8).toFloat(); drawLine(tinta.copy(alpha = 0.55f), Offset(piv.x + r * 0.45f * cos(a), piv.y + r * 0.45f * sin(a)), Offset(piv.x + r * 1.15f * cos(a), piv.y + r * 1.15f * sin(a)), g * 0.35f) }
            drawCircle(tinta, r * 0.08f, piv)
        }
        "fabrica" -> {
            listOf(0.0f to 0.22f, 0.32f to 0.16f).forEach { (dx, rr) -> drawCircle(tinta.copy(alpha = 0.25f), r * rr, Offset(c.x - r * 0.4f + dx * r, c.y - r * 0.82f)) }
            drawRect(tinta, Offset(c.x - r * 0.55f, c.y - r * 0.75f), Size(r * 0.14f, r * 0.8f)); drawRect(tinta, Offset(c.x - r * 0.22f, c.y - r * 0.6f), Size(r * 0.12f, r * 0.6f))
            val base = c.y + r * 0.7f
            drawPath(Path().apply { moveTo(c.x - r * 0.8f, base); lineTo(c.x - r * 0.8f, c.y); for (k in 0 until 3) { val x0 = c.x - r * 0.8f + k * r * 0.53f; lineTo(x0 + r * 0.53f, c.y - r * 0.3f); lineTo(x0 + r * 0.53f, c.y) }; lineTo(c.x + r * 0.8f, base); close() }, tinta)
            for (k in 0 until 4) drawRect(acento, Offset(c.x - r * 0.65f + k * r * 0.36f, c.y + r * 0.2f), Size(r * 0.18f, r * 0.2f))
        }
        "maguey" -> {
            for (k in 0 until 9) {
                val a = (-PI / 2 + (k - 4) * 0.33).toFloat(); val l = r * (if (k % 2 == 0) 0.95f else 0.75f)
                val b = Offset(c.x, c.y + r * 0.7f); val p = Offset(b.x + l * cos(a), b.y + l * sin(a))
                val nx = -sin(a) * r * 0.1f; val ny = cos(a) * r * 0.1f
                drawPath(Path().apply { moveTo(b.x + nx, b.y + ny); lineTo(p.x, p.y); lineTo(b.x - nx, b.y - ny); close() }, if (k % 2 == 0) tinta else lerp(tinta, acento, 0.4f))
            }
            drawLine(acento, Offset(c.x - r * 0.7f, c.y + r * 0.72f), Offset(c.x + r * 0.7f, c.y + r * 0.72f), g)
        }
        "television" -> {
            drawLine(tinta, Offset(c.x, c.y - r * 0.4f), Offset(c.x - r * 0.4f, c.y - r * 0.9f), g * 0.6f); drawLine(tinta, Offset(c.x, c.y - r * 0.4f), Offset(c.x + r * 0.35f, c.y - r * 0.9f), g * 0.6f)
            drawRoundRect(tinta, Offset(c.x - r * 0.8f, c.y - r * 0.42f), Size(r * 1.6f, r * 1.1f), CornerRadius(r * 0.18f))
            drawRoundRect(lerp(acento, fondo, 0.3f), Offset(c.x - r * 0.65f, c.y - r * 0.3f), Size(r * 1.05f, r * 0.85f), CornerRadius(r * 0.22f))
            drawCircle(acento, r * 0.07f, Offset(c.x + r * 0.58f, c.y - r * 0.12f)); drawCircle(acento, r * 0.07f, Offset(c.x + r * 0.58f, c.y + r * 0.15f))
            drawLine(tinta, Offset(c.x - r * 0.5f, c.y + r * 0.68f), Offset(c.x - r * 0.6f, c.y + r * 0.85f), g); drawLine(tinta, Offset(c.x + r * 0.5f, c.y + r * 0.68f), Offset(c.x + r * 0.6f, c.y + r * 0.85f), g)
        }
        "globo" -> {
            drawCircle(acento, r * 0.8f, c); drawCircle(tinta, r * 0.8f, c, style = Stroke(g))
            drawOval(tinta, Offset(c.x - r * 0.35f, c.y - r * 0.8f), Size(r * 0.7f, r * 1.6f), style = Stroke(g * 0.6f))
            drawLine(tinta, Offset(c.x, c.y - r * 0.8f), Offset(c.x, c.y + r * 0.8f), g * 0.6f)
            listOf(-0.4f, 0f, 0.4f).forEach { dy -> val w = r * 0.8f * kotlin.math.sqrt(1 - dy * dy); drawLine(tinta, Offset(c.x - w, c.y + r * dy), Offset(c.x + w, c.y + r * dy), g * 0.6f) }
        }
        "telefono" -> {
            drawRoundRect(tinta, Offset(c.x - r * 0.42f, c.y - r * 0.82f), Size(r * 0.84f, r * 1.64f), CornerRadius(r * 0.16f))
            drawRoundRect(lerp(acento, fondo, 0.25f), Offset(c.x - r * 0.34f, c.y - r * 0.68f), Size(r * 0.68f, r * 1.32f), CornerRadius(r * 0.08f))
            for (k in 0 until 3) drawRoundRect(tinta.copy(alpha = 0.5f), Offset(c.x - r * 0.26f, c.y - r * 0.5f + k * r * 0.3f), Size(r * 0.52f, r * 0.16f), CornerRadius(r * 0.05f))
            drawCircle(Color(0xFFE5484D), r * 0.13f, Offset(c.x + r * 0.38f, c.y - r * 0.76f))
        }
    }
}

/** Una medalla redonda con el objeto de la vida (o el emblema del tema, si es la vida del tema). */
@Composable
internal fun MedallaVida(v: OtrasVidas.Vida, tam: Dp, elegida: Boolean = false, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val tema = v.tema?.let { id -> TemaId.entries.firstOrNull { it.id == id } }
    Canvas(modifier.size(tam)) {
        val c = center; val r = size.minDimension / 2
        drawCircle(if (elegida) cs.primaryContainer else cs.surfaceVariant, r, c)
        drawCircle(if (elegida) cs.primary else cs.outline.copy(alpha = 0.6f), r - 1.dp.toPx(), c, style = Stroke((if (elegida) 2.5f else 1.2f) * density))
        if (tema != null) {
            withTransformEmblema(c, r * 0.66f) { dibujarEmblema(tema, 0.3f, cs, oscuro) }
        } else objetoVida(v.dibujo, c, r * 0.62f, cs.onSurface, cs.secondary, if (elegida) cs.primaryContainer else cs.surfaceVariant)
    }
}

/** Dibuja el emblema de un tema centrado en [c] con radio [r] (los emblemas se dibujan en el tamaño entero del lienzo). */
private fun DrawScope.withTransformEmblema(c: Offset, r: Float, f: DrawScope.() -> Unit) {
    val lado = r * 2
    inset(c.x - r, c.y - r, size.width - (c.x - r) - lado, size.height - (c.y - r) - lado) { f() }
}

// =====================================================================
//  El recuadro de Más
// =====================================================================

@Composable
fun RecuadroOtrasVidas(nav: Nav) {
    val tema = Estilo.tema.id
    val vidas = remember(tema) { OtrasVidas.visibles(tema).sortedBy { it.anio } }
    val cs = MaterialTheme.colorScheme
    Recuadro("Otras vidas", "Cómo vivía alguien del Paleolítico, de Roma, de Edo o de 1960, frente a cómo vives tú", alto = 150.dp, onClick = { nav.ir("otras_vidas") }) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(cs.surfaceVariant.copy(alpha = 0.5f), cs.surface)))) {
            Canvas(Modifier.fillMaxSize()) {
                val y = size.height * 0.55f
                drawLine(cs.outline.copy(alpha = 0.6f), Offset(14.dp.toPx(), y), Offset(size.width - 14.dp.toPx(), y), 1.5.dp.toPx())
            }
            val muestra = vidas.filterIndexed { i, v -> v.tema != null || i % 2 == 0 }.take(7)
            Row(Modifier.fillMaxSize().padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                muestra.forEach { v ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        MedallaVida(v, 40.dp, elegida = v.tema != null)
                        Text(anio(v.anio), style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, maxLines = 1, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

private fun anio(a: Int): String = when { a < -9000 -> "${"%,d".format(-a)} a. C."; a < 0 -> "${-a} a. C."; else -> "$a" }

// =====================================================================
//  La pantalla
// =====================================================================

@Composable
fun PantallaOtrasVidas(nav: Nav) {
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    val tema = Estilo.tema.id
    val vidas = remember(tema) { val l = OtrasVidas.visibles(tema); l.filter { it.tema != null } + l.filter { it.tema == null }.sortedBy { it.anio } }
    var elegida by rememberSaveable(tema) { mutableStateOf(vidas.first().id) }
    val tuyos by produceState(if (TusNumeros.sincrono) runCatching { TusNumeros.calcular(ctx) }.getOrDefault(emptyMap()) else null) {
        if (!TusNumeros.sincrono) value = withContext(Dispatchers.Default) { runCatching { TusNumeros.calcular(ctx) }.getOrDefault(emptyMap()) }
    }
    val v = vidas.firstOrNull { it.id == elegida } ?: vidas.first()
    Hoja("Otras vidas", nav.atras, "Personas promedio de otras épocas y lugares: lo que comían, cuánto se movían, cuánto dormían y a cuánta gente veían, junto a ti.") {
        val t = tuyos
        // ---- A quién te pareces.
        val numeros = t?.mapValues { it.value.v }.orEmpty()
        val parecidas = remember(numeros, tema) { vidas.mapNotNull { x -> OtrasVidas.distancia(x, numeros)?.let { x to it } }.sortedBy { it.second.first }.take(3) }
        if (parecidas.isNotEmpty()) Tarjeta(Modifier.padding(top = 12.dp)) {
            Text("Por cómo duermes, comes y te mueves, tu vida se parece más a la de…", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                parecidas.forEach { (x, d) ->
                    Column(Modifier.width(100.dp).clickable { sentir(Sentidos.Evento.ELEGIR); elegida = x.id }, horizontalAlignment = Alignment.CenterHorizontally) {
                        MedallaVida(x, 56.dp, elegida = x.id == v.id)
                        Text(x.nombre, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center, maxLines = 2, modifier = Modifier.padding(top = 4.dp))
                        Text("${(exp(-d.first) * 100).roundToInt()}% parecida", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Ayuda("Compara pasos, comida, alcohol, sueño, hora de dormir, aire libre, pantallas, avisos, música, gente y kilómetros con tus últimos 30 días.", Modifier.padding(top = 8.dp))
        } else if (t != null) Ayuda("Cuando tengas unos días de pasos, sueño y pantalla registrados, aquí verás a quién te pareces.", Modifier.padding(top = 12.dp))

        // ---- La línea del tiempo.
        Row(Modifier.padding(top = 16.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            vidas.forEach { x ->
                Column(Modifier.width(76.dp).clip(RoundedCornerShape(10.dp)).clickable { sentir(Sentidos.Evento.ELEGIR); elegida = x.id }.padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    MedallaVida(x, 54.dp, elegida = x.id == v.id)
                    Text(if (x.tema != null) "tu tema" else anio(x.anio), style = MaterialTheme.typography.labelSmall, color = if (x.tema != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1, modifier = Modifier.padding(top = 3.dp))
                    Text(x.nombre, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                }
            }
        }

        AnimatedContent(v, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "vida") { x -> Column { DetalleVida(x, t) } }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DetalleVida(v: OtrasVidas.Vida, t: Map<String, TusNumeros.Valor>?) {
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    // ---- Retrato.
    Surface(Modifier.fillMaxWidth().padding(top = 14.dp), shape = MaterialTheme.shapes.medium, color = cs.surface, border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.5f))) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            MedallaVida(v, 92.dp, elegida = true)
            Column(Modifier.padding(start = 14.dp).weight(1f)) {
                Text(v.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("${v.rol}, ${v.edad} años", style = MaterialTheme.typography.bodyMedium)
                Text("${v.lugar} · ${v.cuando}", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
                if (v.ficticia) Text("vida de ficción · números inventados", style = MaterialTheme.typography.labelSmall, color = cs.secondary, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
    // ---- Su historia.
    Seccion("Su vida") {
        Tarjeta {
            v.historia.forEachIndexed { i, p ->
                Text(p, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = if (i == 0) 0.dp else 10.dp))
            }
            if (v.inspiracion.isNotBlank()) Text(v.inspiracion, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 10.dp))
        }
    }
    // ---- Un día suyo.
    Seccion("Un día suyo") {
        Tarjeta { RelojDia(v, t?.get("dormir")?.v, t?.get("despertar")?.v) }
    }
    // ---- Tú y ella.
    Seccion("Tú y ${conQuien(v)}",
        "Tus últimos 30 días con datos, junto a lo que sabemos de su vida. Toca un renglón para ver de dónde sale.") {
        Tarjeta {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Leyenda(cs.secondary, v.nombre); Leyenda(cs.primary, "tú")
            }
            OtrasVidas.MEDIDAS.forEach { m ->
                val d = v.datos[m.id] ?: return@forEach
                FilaComparacion(m, d, m.tuyo?.let { t?.get(it)?.v }, if (m.id == "vida" || m.id == "vida_adulto") t?.get("edad")?.v else null)
            }
        }
    }
    // ---- Fuentes.
    Seccion("De dónde salen estos números") {
        Tarjeta {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OtrasVidas.Certeza.entries.filter { c -> v.datos.values.any { it.c == c } }.forEach { c -> ChipCerteza(c) }
            }
            OtrasVidas.Certeza.entries.filter { c -> v.datos.values.any { it.c == c } }.forEach { c -> Ayuda("${c.nombre}: ${c.explica}", Modifier.padding(top = 4.dp)) }
            if (v.fuentes.isEmpty()) Ayuda("Es una vida de ficción: los números son estimaciones razonables para su mundo, no datos.", Modifier.padding(top = 8.dp))
            v.fuentes.forEach { (n, u) ->
                Text(n, style = MaterialTheme.typography.bodyMedium, color = cs.primary, textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(top = 8.dp).clickable { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u))) } })
            }
        }
    }
}

@Composable
private fun Leyenda(c: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(c, CircleShape))
        Text(" $texto", style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ChipCerteza(c: OtrasVidas.Certeza) {
    val cs = MaterialTheme.colorScheme
    val col = when (c) { OtrasVidas.Certeza.MEDIDO -> cs.primary; OtrasVidas.Certeza.ESTIMADO -> cs.tertiary; OtrasVidas.Certeza.RECONSTRUIDO -> cs.onSurfaceVariant; OtrasVidas.Certeza.INVENTADO -> cs.secondary }
    Text(c.nombre, style = MaterialTheme.typography.labelSmall, color = col,
        modifier = Modifier.border(1.dp, col.copy(alpha = 0.6f), RoundedCornerShape(50)).padding(horizontal = 7.dp, vertical = 1.dp))
}

private fun formato(m: OtrasVidas.Medida, x: Double): String = when (m.forma) {
    "hora" -> { val h = ((x % 24) + 24) % 24; "%d:%02d".format(h.toInt(), ((h - h.toInt()) * 60).roundToInt().coerceAtMost(59)) }
    "decimal" -> if (x >= 100) "%,d".format(x.roundToInt()) else "%.1f".format(x)
    else -> when { x >= 1e6 -> "%.1f millones".format(x / 1e6); else -> "%,d".format(x.roundToInt()) }
} + if (m.unidad.isNotEmpty() && m.forma != "hora") " ${m.unidad}" else ""

/** Escala logarítmica para lo que va de unos cuantos a millones. */
private val LOG = setOf("km_anio", "notificaciones", "personas_dia", "conocidas", "poblacion", "lejos")

@Composable
private fun FilaComparacion(m: OtrasVidas.Medida, d: OtrasVidas.Dato, tuyo: Double?, edad: Double?) {
    val cs = MaterialTheme.colorScheme
    var abierta by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().clickable { abierta = !abierta }.padding(top = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(m.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            ChipCerteza(d.c)
        }
        if (m.forma == "hora") RelojitoHora(d.v, tuyo)
        else {
            val log = m.id in LOG
            fun esc(x: Double) = if (log) ln(1 + x) else x
            val max = maxOf(esc(d.v), esc(tuyo ?: 0.0), 1e-9)
            BarraValor(cs.secondary, (esc(d.v) / max).toFloat(), formato(m, d.v))
            if (tuyo != null) BarraValor(cs.primary, (esc(tuyo) / max).toFloat(), formato(m, tuyo))
        }
        if (edad != null) Ayuda("Tú tienes ${edad.roundToInt()} años.")
        if (abierta) {
            if (d.nota.isNotBlank()) Ayuda(d.nota, Modifier.padding(top = 4.dp))
            if (m.explica.isNotBlank()) Ayuda(m.explica, Modifier.padding(top = 2.dp))
            if (tuyo == null && m.tuyo != null) Ayuda("Todavía no hay datos tuyos de esto.", Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun BarraValor(color: Color, f: Float, texto: String) {
    Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(10.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                drawRoundRect(color.copy(alpha = 0.15f), Offset.Zero, size, CornerRadius(5f))
                if (f > 0f) drawRoundRect(color, Offset.Zero, Size(size.width * f.coerceIn(0.015f, 1f), size.height), CornerRadius(5f))
            }
        }
        Text(texto, style = MaterialTheme.typography.labelMedium, color = color, maxLines = 1, modifier = Modifier.width(118.dp).padding(start = 8.dp), textAlign = TextAlign.End)
    }
}

/** La hora de dormir: una línea de las 18 a las 6 con su marca y la tuya. */
@Composable
private fun RelojitoHora(suya: Double, tuya: Double?) {
    val cs = MaterialTheme.colorScheme
    val medidor = androidx.compose.ui.text.rememberTextMeasurer()
    val estilo = androidx.compose.ui.text.TextStyle(fontSize = 9.sp, color = cs.onSurfaceVariant)
    fun norm(h: Double) = (if (h < 12) h + 24 else h).coerceIn(18.0, 32.0)
    Canvas(Modifier.fillMaxWidth().padding(top = 6.dp).height(34.dp)) {
        val y = size.height * 0.35f
        fun x(h: Double) = ((norm(h) - 18) / 14 * size.width).toFloat()
        drawLine(cs.outline.copy(alpha = 0.5f), Offset(0f, y), Offset(size.width, y), 2f)
        for (h in listOf(18, 21, 24, 27, 30)) { drawLine(cs.outline, Offset(x(h.toDouble()), y - 4f), Offset(x(h.toDouble()), y + 4f), 1.5f); drawText(medidor, "${h % 24}:00", Offset(x(h.toDouble()) - 10f, y + 6f), estilo) }
        drawCircle(cs.secondary, 6.dp.toPx(), Offset(x(suya), y))
        tuya?.let { drawCircle(cs.primary, 6.dp.toPx(), Offset(x(it), y)); drawCircle(cs.surface, 2.5.dp.toPx(), Offset(x(it), y)) }
    }
}

/** El día en un reloj de 24 horas: afuera su día, por tramos; adentro, cuándo duermes tú. */
@Composable
private fun RelojDia(v: OtrasVidas.Vida, tuDormir: Double?, tuDespertar: Double?) {
    val cs = MaterialTheme.colorScheme
    val colores = mapOf(
        OtrasVidas.Hacer.DORMIR to lerp(cs.primary, Color(0xFF1B2448), 0.6f), OtrasVidas.Hacer.TRABAJO to cs.secondary, OtrasVidas.Hacer.COMER to cs.tertiary,
        OtrasVidas.Hacer.CAMINO to lerp(cs.secondary, cs.tertiary, 0.5f), OtrasVidas.Hacer.LIBRE to lerp(cs.primary, cs.surface, 0.35f), OtrasVidas.Hacer.GENTE to lerp(cs.tertiary, cs.primary, 0.5f),
        OtrasVidas.Hacer.CUIDAR to lerp(cs.secondary, cs.surface, 0.4f), OtrasVidas.Hacer.REZAR to lerp(cs.tertiary, cs.surface, 0.3f),
    )
    val medidor = androidx.compose.ui.text.rememberTextMeasurer()
    val estilo = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, color = cs.onSurfaceVariant)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(170.dp)) {
            val c = center; val r = size.minDimension / 2 - 14.dp.toPx(); val ancho = 18.dp.toPx()
            fun ang(h: Double) = (h / 24 * 360 - 90).toFloat()
            v.dia.forEach { tr ->
                val dur = ((tr.hasta - tr.desde) + 24) % 24
                drawArc(colores[tr.que] ?: cs.outline, ang(tr.desde), (dur / 24 * 360).toFloat() - 0.8f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(ancho, cap = StrokeCap.Butt))
            }
            // Tu sueño, en un anillo de adentro.
            if (tuDormir != null && tuDespertar != null) {
                val ri = r - ancho - 6.dp.toPx()
                val ini = tuDormir % 24; val dur = ((tuDespertar % 24 - ini) + 24) % 24
                drawCircle(cs.outline.copy(alpha = 0.15f), ri, c, style = Stroke(6.dp.toPx()))
                drawArc(cs.primary, ang(ini), (dur / 24 * 360).toFloat(), false, Offset(c.x - ri, c.y - ri), Size(ri * 2, ri * 2), style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
            }
            for (h in listOf(0, 6, 12, 18)) {
                val a = Math.toRadians(ang(h.toDouble()).toDouble())
                val p = Offset(c.x + (r + ancho / 2 + 8.dp.toPx()) * cos(a).toFloat(), c.y + (r + ancho / 2 + 8.dp.toPx()) * sin(a).toFloat())
                val txt = medidor.measure("$h", estilo)
                drawText(txt, topLeft = Offset(p.x - txt.size.width / 2, p.y - txt.size.height / 2))
            }
        }
        Column(Modifier.padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            v.dia.map { it.que }.distinct().forEach { q ->
                val horas = v.dia.filter { it.que == q }.sumOf { ((it.hasta - it.desde) + 24) % 24 }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).background(colores[q] ?: cs.outline, CircleShape))
                    Text(" ${q.nombre} · ${"%.1f".format(horas)} h", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (tuDormir != null && tuDespertar != null) Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Box(Modifier.size(10.dp).background(cs.primary, CircleShape))
                Text(" tu sueño", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    val notas = v.dia.filter { it.texto.isNotBlank() }
    if (notas.isNotEmpty()) Ayuda(notas.joinToString(" · ") { "${horaCorta(it.desde)} ${it.texto}" }, Modifier.padding(top = 8.dp))
}

private fun horaCorta(h: Double): String { val x = ((h % 24) + 24) % 24; return "%d:%02d".format(x.toInt(), ((x - x.toInt()) * 60).roundToInt().coerceAtMost(59)) }

/** «Tú y Naa», pero «tú y una astronauta», «tú y alguien promedio». */
private fun conQuien(v: OtrasVidas.Vida): String =
    if (listOf("Un ", "Una ", "La ", "Alguien").any { v.nombre.startsWith(it) }) v.nombre.replaceFirstChar { it.lowercase() } else v.nombre
