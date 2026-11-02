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
import androidx.compose.material3.ColorScheme
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
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
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
import io.github.ruddy2003.vestigium.ui.cartuchoMaya
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.dibujarEmblema
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.figEngrane
import io.github.ruddy2003.vestigium.ui.figEstrella
import io.github.ruddy2003.vestigium.ui.figPoligono
import io.github.ruddy2003.vestigium.ui.figuraTema
import io.github.ruddy2003.vestigium.ui.polarFig
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
            // Mazorca amarilla entre sus hojas.
            val grano = lerp(Color(0xFFE2B84A), acento, 0.2f)
            listOf(-1f, 1f).forEach { l -> drawPath(Path().apply { moveTo(c.x, c.y + r * 0.9f); quadraticTo(c.x + l * r * 0.75f, c.y + r * 0.35f, c.x + l * r * 0.55f, c.y - r * 0.35f); quadraticTo(c.x + l * r * 0.3f, c.y + r * 0.35f, c.x, c.y + r * 0.9f); close() }, tinta.copy(alpha = 0.85f)) }
            drawOval(grano, Offset(c.x - r * 0.3f, c.y - r * 0.85f), Size(r * 0.6f, r * 1.5f))
            for (fy in 0 until 8) for (fx in -1..1) drawCircle(lerp(grano, tinta, 0.3f), r * 0.05f, Offset(c.x + fx * r * 0.14f, c.y - r * 0.66f + fy * r * 0.16f))
            drawOval(tinta, Offset(c.x - r * 0.3f, c.y - r * 0.85f), Size(r * 0.6f, r * 1.5f), style = Stroke(g * 0.6f))
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
                        MedallaVida(v, 46.dp, elegida = v.tema != null)
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
            Ayuda("Compara pasos, comida, alcohol, sueño, hora de dormir, aire libre, pantallas, interrupciones, música, gente y kilómetros con tus últimos 30 días.", Modifier.padding(top = 8.dp))
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
                FilaComparacion(v, m, d, m.tuyo?.let { t?.get(it)?.v }, if (m.id == "vida" || m.id == "vida_adulto") t?.get("edad")?.v else null)
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


@Composable
private fun FilaComparacion(v: OtrasVidas.Vida, m: OtrasVidas.Medida, d: OtrasVidas.Dato, tuyo: Double?, edad: Double?) {
    val cs = MaterialTheme.colorScheme
    var abierta by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().clickable { abierta = !abierta }.padding(top = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(m.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            ChipCerteza(d.c)
        }
        if (m.forma == "hora") RelojitoHora(d.v, tuyo)
        else {
            // Las dos barras en la misma escala, desde cero: el largo es proporcional al número.
            val max = maxOf(d.v, tuyo ?: 0.0, 1e-9)
            BarraValor(cs.secondary, (d.v / max).toFloat(), formato(m, d.v))
            if (tuyo != null) BarraValor(cs.primary, (tuyo / max).toFloat(), formato(m, tuyo))
            // Cuando la diferencia es muy grande, una barra casi no se ve: se dice cuántas veces más.
            if (tuyo != null) {
                val mayor = maxOf(d.v, tuyo); val menor = minOf(d.v, tuyo)
                val veces = if (menor > 0) mayor / menor else null
                val texto = when {
                    mayor <= 0 -> null
                    menor <= 0 -> if (tuyo > d.v) "Para ${v.nombre}, cero." else "Para ti, cero."
                    veces != null && veces >= 1.5 -> (if (tuyo > d.v) "Tú: " else "${v.nombre}: ") + (if (veces < 10) "%.1f".format(veces) else "%,d".format(veces.roundToInt())) + " veces más."
                    else -> null
                }
                texto?.let { Ayuda(it, Modifier.padding(top = 2.dp)) }
            }
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
    val tema = Estilo.tema
    Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(10.dp)) {
            Canvas(Modifier.fillMaxSize()) { barraTema(tema, color, f) }
        }
        Text(texto, style = MaterialTheme.typography.labelMedium, color = color, maxLines = 1, modifier = Modifier.width(118.dp).padding(start = 8.dp), textAlign = TextAlign.End)
    }
}

/**
 * Una barra de comparación con la forma del tema. El largo lleno es siempre [f] del ancho (desde cero, como antes);
 * el tema solo cambia la pista, la forma y lo que lleva dentro: columna acanalada, celdas de consola, tinta con su rúbrica,
 * sello rayado, medidor, barra remachada, regla graduada, cinta pespunteada, galón, tallo con hojas, bastón de caramelo,
 * seda, vidriera, haz de luz, alicatado, fieltro, sombreado a pluma, cuña, banderola y piedra con muescas.
 */
private fun DrawScope.barraTema(tema: TemaId, color: Color, f: Float) {
    val w = size.width; val h = size.height; val d = density
    val fw = if (f > 0f) w * f.coerceIn(0.015f, 1f) else 0f
    val pista = color.copy(alpha = 0.15f)
    val claro = Color.White.copy(alpha = 0.35f); val oscuro = Color.Black.copy(alpha = 0.25f)
    fun guion(a: Float, b: Float) = PathEffect.dashPathEffect(floatArrayOf(a * d, b * d))
    when (tema) {
        // Fuste acanalado, tumbado, con su ábaco arriba y abajo.
        TemaId.ROMANO -> {
            drawRect(pista, Offset.Zero, size)
            if (fw > 0f) {
                drawRect(color, Offset.Zero, Size(fw, h))
                clipRect(right = fw) { var x = 3f * d; while (x < fw) { drawLine(claro, Offset(x, 1.6f * d), Offset(x, h - 1.6f * d), 0.8f * d); x += 3.5f * d } }
                drawRect(oscuro, Offset.Zero, Size(fw, 1.2f * d)); drawRect(oscuro, Offset(0f, h - 1.2f * d), Size(fw, 1.2f * d))
            }
        }
        // El tranvía que avanza por su vía: la caja llena con sus ventanas claras, sobre el riel.
        TemaId.URBE -> {
            drawLine(pista, Offset(0f, h - 0.6f * d), Offset(w, h - 0.6f * d), 1.2f * d)
            drawRect(pista, Offset(0f, 0f), Size(w, h - 2 * d))
            if (fw > 0f) {
                drawRect(color, Offset.Zero, Size(fw, h - 2 * d))
                clipRect(right = fw - 1.5f * d) { var x = 2.5f * d; while (x < fw) { drawRect(claro, Offset(x, 1.6f * d), Size(3.2f * d, h - 5.5f * d)); x += 5.5f * d } }
                drawLine(oscuro, Offset(0f, h - 0.6f * d), Offset(fw, h - 0.6f * d), 1.2f * d)
            }
        }
        // Celdas de consola: ▮▮▮▯▯.
        TemaId.CIBER -> {
            val paso = 5f * d; val celda = 3.6f * d
            var x = 0f
            while (x < w) { drawRect(pista, Offset(x, 0f), Size(celda, h)); x += paso }
            clipRect(right = fw) { x = 0f; while (x < fw) { drawRect(color, Offset(x, 0f), Size(celda, h)); x += paso } }
        }
        // Un tubo de neón: apagado de punta a punta y encendido, con su halo, hasta donde llega.
        TemaId.NEON -> {
            drawLine(pista, Offset(h / 2, h / 2), Offset(w - h / 2, h / 2), h * 0.5f, StrokeCap.Round)
            if (fw > 0f) {
                drawLine(color.copy(alpha = 0.3f), Offset(h / 2, h / 2), Offset(maxOf(fw - h / 2, h / 2), h / 2), h, StrokeCap.Round)
                drawLine(color, Offset(h / 2, h / 2), Offset(maxOf(fw - h / 2, h / 2), h / 2), h * 0.45f, StrokeCap.Round)
            }
        }
        // Friso: la barra entre dos filetes.
        TemaId.HELENO -> {
            drawLine(color.copy(alpha = 0.3f), Offset(0f, h / 2), Offset(w, h / 2), d)
            if (fw > 0f) {
                drawRect(color, Offset(0f, h * 0.27f), Size(fw, h * 0.46f))
                drawLine(color, Offset(0f, 0.5f * d), Offset(fw, 0.5f * d), d); drawLine(color, Offset(0f, h - 0.5f * d), Offset(fw, h - 0.5f * d), d)
            }
        }
        // La cenefa de rectángulos: la barra se llena bloque a bloque, uno fuerte y otro claro.
        TemaId.EGIPTO -> {
            drawLine(color.copy(alpha = 0.3f), Offset(0f, h / 2), Offset(w, h / 2), d)
            var x = 0f; var k = 0
            while (x < fw) { drawRect(color.copy(alpha = if (k % 2 == 0) 1f else 0.6f), Offset(x, h * 0.22f), Size(minOf(5f * d, fw - x), h * 0.56f)); x += 6f * d; k++ }
        }
        // Trazo de tinta sobre el renglón, rematado con un rombo de rúbrica.
        TemaId.MEDIEVAL -> {
            drawLine(color.copy(alpha = 0.3f), Offset(0f, h / 2), Offset(w, h / 2), d, pathEffect = guion(1f, 2.5f))
            if (fw > 0f) {
                drawRoundRect(color, Offset(0f, h * 0.18f), Size(fw, h * 0.64f), CornerRadius(h * 0.32f))
                drawPath(Path().apply { moveTo(fw, 0f); lineTo(fw + h * 0.4f, h / 2); lineTo(fw, h); lineTo(fw - h * 0.4f, h / 2); close() }, Color(0xFFA3312F))
            }
        }
        // La lanza: el asta de lo que falta, la de acero de lo recorrido y el gallardete rojo en la punta.
        TemaId.TEMPLARIO -> {
            drawLine(color.copy(alpha = 0.25f), Offset(0f, h / 2), Offset(w, h / 2), 1.2f * d)
            if (fw > 0f) {
                drawRect(color, Offset(0f, h * 0.36f), Size(fw, h * 0.28f))
                drawPath(Path().apply { moveTo(fw, h * 0.5f); lineTo(fw - h * 0.9f, 0f); lineTo(fw - h * 0.55f, h * 0.5f); lineTo(fw - h * 0.9f, h); close() }, Color(0xFFB0191F))
            }
        }
        // Sello rayado del expediente.
        TemaId.DISTOPIA -> {
            drawRect(color.copy(alpha = 0.4f), Offset(0.5f * d, 0.5f * d), Size(w - d, h - d), style = Stroke(d))
            if (fw > 0f) {
                drawRect(color.copy(alpha = 0.35f), Offset.Zero, Size(fw, h))
                clipRect(right = fw) { var x = -h; while (x < fw) { drawLine(color, Offset(x, h), Offset(x + h, 0f), 1.6f * d); x += 3.2f * d } }
                drawRect(color, Offset.Zero, Size(fw, h), style = Stroke(1.4f * d))
            }
        }
        // Medidor de esfera alargada, con sus décimos y el brillo del vidrio.
        TemaId.REFUGIO -> {
            drawRoundRect(pista, Offset.Zero, size, CornerRadius(h / 2))
            for (k in 1..9) drawLine(color.copy(alpha = 0.3f), Offset(w * k / 10f, h * 0.2f), Offset(w * k / 10f, h * 0.8f), d)
            if (fw > 0f) {
                drawRoundRect(color, Offset.Zero, Size(fw, h), CornerRadius(h / 2))
                drawRoundRect(claro, Offset(h * 0.3f, h * 0.18f), Size((fw - h * 0.6f).coerceAtLeast(0f), h * 0.22f), CornerRadius(h * 0.11f))
            }
        }
        // Barra de acero con bisel y dos remaches.
        TemaId.ORDEN -> {
            drawRect(pista, Offset.Zero, size)
            if (fw > 0f) {
                drawRect(color, Offset.Zero, Size(fw, h)); drawRect(claro, Offset.Zero, Size(fw, 1.5f * d))
                if (fw > h * 3) { drawCircle(oscuro, h * 0.17f, Offset(h * 0.5f, h / 2)); drawCircle(oscuro, h * 0.17f, Offset(fw - h * 0.5f, h / 2)) }
            }
        }
        // El tubo de nivel de una caldera: la pista de vidrio con sus marcas y el agua (el color) hasta donde llega.
        TemaId.VAPOR -> {
            drawRoundRect(pista, Offset.Zero, size, CornerRadius(h / 2))
            if (fw > 0f) { drawRoundRect(color, Offset.Zero, Size(fw, h), CornerRadius(h / 2)); drawLine(claro, Offset(h * 0.4f, h * 0.3f), Offset(maxOf(h * 0.4f, fw - h * 0.4f), h * 0.3f), 1.2f * d) }
            for (k in 1 until 10) drawLine(oscuro.copy(alpha = 0.18f), Offset(w * k / 10f, h * (if (k == 5) 0.1f else 0.55f)), Offset(w * k / 10f, h * 0.9f), 0.8f * d)
            listOf(0f, w).forEach { x -> drawRect(oscuro, Offset(x - 1.5f * d, -1 * d), Size(3 * d, h + 2 * d)) }
        }
        // Regla graduada: la lectura arriba y la marca donde termina.
        TemaId.LABORATORIO -> {
            var x = 0f; var i = 0
            while (x <= w) { drawLine(color.copy(alpha = 0.35f), Offset(x, h), Offset(x, if (i % 5 == 0) h * 0.35f else h * 0.65f), 0.8f * d); x += 3f * d; i++ }
            if (fw > 0f) { drawRect(color, Offset(0f, h * 0.08f), Size(fw, h * 0.45f)); drawLine(color, Offset(fw, 0f), Offset(fw, h), 1.5f * d) }
        }
        // Cinta de tela pespunteada.
        TemaId.COLONOS -> {
            drawRoundRect(pista, Offset.Zero, size, CornerRadius(2f * d))
            if (fw > 0f) {
                drawRoundRect(color, Offset.Zero, Size(fw, h), CornerRadius(2f * d))
                if (fw > 4f * d) drawLine(Color.White.copy(alpha = 0.6f), Offset(2f * d, h / 2), Offset(fw - 2f * d, h / 2), d, pathEffect = guion(2.5f, 2f))
            }
        }
        // Galón con los bordes de oro.
        TemaId.NAPOLEON -> {
            drawRect(pista, Offset.Zero, size)
            if (fw > 0f) {
                val oro = Color(0xFFD4AF37)
                drawRect(color, Offset.Zero, Size(fw, h)); drawRect(oro, Offset.Zero, Size(fw, 1.6f * d)); drawRect(oro, Offset(0f, h - 1.6f * d), Size(fw, 1.6f * d))
            }
        }
        // Un tallo con hojas alternas y un botón en la punta.
        TemaId.BOTANICO -> {
            drawLine(color.copy(alpha = 0.25f), Offset(0f, h / 2), Offset(w, h / 2), d)
            if (fw > 0f) {
                drawLine(color, Offset(0f, h / 2), Offset(fw, h / 2), 2f * d, StrokeCap.Round)
                clipRect(right = fw) {
                    var x = 4f * d; var k = 0
                    while (x < fw) {
                        val arriba = k % 2 == 0
                        rotate(if (arriba) -35f else 35f, Offset(x, h / 2)) { drawOval(color, Offset(x, h / 2 - h * 0.18f), Size(h * 0.55f, h * 0.36f)) }
                        x += 6f * d; k++
                    }
                }
                drawCircle(color, h * 0.3f, Offset(fw, h / 2))
            }
        }
        TemaId.RUINAS -> {
            // Una calle: el asfalto, la línea discontinua de lo recorrido y un brote donde llega el avance.
            drawLine(color.copy(alpha = 0.2f), Offset(0f, h / 2), Offset(w, h / 2), h * 0.5f)
            if (fw > 0f) {
                var x = 2f * d
                while (x < fw) { drawLine(color, Offset(x, h / 2), Offset(minOf(x + 5f * d, fw), h / 2), 1.6f * d); x += 8f * d }
                drawLine(color, Offset(fw, h / 2), Offset(fw, h * 0.1f), 1.2f * d)
                rotate(-40f, Offset(fw, h * 0.25f)) { drawOval(color, Offset(fw, h * 0.1f), Size(h * 0.4f, h * 0.22f)) }
            }
        }
        // Bastón de caramelo.
        TemaId.NAVIDAD -> {
            drawRoundRect(pista, Offset.Zero, size, CornerRadius(h / 2))
            if (fw > 0f) {
                val p = Path().apply { addRoundRect(RoundRect(0f, 0f, fw, h, CornerRadius(h / 2))) }
                drawPath(p, color)
                clipPath(p) { var x = -h; while (x < fw) { drawLine(Color.White.copy(alpha = 0.55f), Offset(x, h), Offset(x + h, 0f), 2f * d); x += 5f * d } }
            }
        }
        // Seda con rombos tejidos.
        TemaId.SEDA -> {
            drawRect(pista, Offset.Zero, size)
            if (fw > 0f) {
                drawRect(color, Offset.Zero, Size(fw, h))
                clipRect(right = fw) { var x = h / 2; while (x < fw + h) { drawPath(figPoligono(Offset(x, h / 2), h * 0.34f, 4, -90f), claro, style = Stroke(0.8f * d)); x += h } }
            }
        }
        // Olas de seigaiha que se van llenando.
        TemaId.EDO -> {
            drawRect(pista, Offset.Zero, size)
            if (fw > 0f) {
                drawRect(color, Offset.Zero, Size(fw, h))
                clipRect(right = fw) { var x = h / 2; while (x < fw + h) { for (k in 0..1) { val q = h * (0.42f - k * 0.18f); drawArc(claro, 180f, 180f, false, Offset(x - q, h - q), Size(2 * q, 2 * q), style = Stroke(0.8f * d)) }; x += maxOf(h, 1f) } }
            }
        }
        // Vidriera: vidrios entre plomos.
        TemaId.GOTICO -> {
            val plomo = Color(0xFF1C1A24).copy(alpha = 0.75f)
            drawRect(plomo.copy(alpha = 0.25f), Offset.Zero, size, style = Stroke(d))
            if (fw > 0f) {
                drawRect(color, Offset.Zero, Size(fw, h))
                clipRect(right = fw) { var x = 7f * d; while (x < fw) { drawLine(plomo, Offset(x, 0f), Offset(x, h), 1.5f * d); x += 7f * d } }
                drawRect(plomo, Offset.Zero, Size(fw, h), style = Stroke(1.5f * d))
            }
        }
        // Una raya de tinta sobre el renglón; donde acaba, la tinta se corrió en una gota.
        TemaId.ARCANO -> {
            drawLine(pista, Offset(0f, h * 0.7f), Offset(w, h * 0.7f), 0.8f * d, pathEffect = guion(1f, 2f))
            if (fw > 0f) {
                val p = Path(); var x = 0f; p.moveTo(0f, h * 0.5f)
                while (x <= fw) { p.lineTo(x, h * 0.5f + h * 0.12f * kotlin.math.sin(x / (5f * d))); x += 2f }
                drawPath(p, color, style = Stroke(h * 0.45f, cap = StrokeCap.Round))
                drawCircle(color, h * 0.32f, Offset(fw, h * 0.6f)); drawCircle(color.copy(alpha = 0.5f), h * 0.14f, Offset(fw + h * 0.5f, h * 0.85f))
            }
        }
        // Haz de luz de la consola con su punta encendida.
        TemaId.ESPACIO -> {
            drawLine(color.copy(alpha = 0.25f), Offset(0f, h / 2), Offset(w, h / 2), d)
            if (fw > 0f) {
                drawRoundRect(color.copy(alpha = 0.25f), Offset.Zero, Size(fw, h), CornerRadius(h / 2))
                drawRoundRect(color, Offset(0f, h * 0.32f), Size(fw, h * 0.36f), CornerRadius(h * 0.18f))
                drawCircle(Color.White.copy(alpha = 0.85f), h * 0.2f, Offset((fw - h * 0.2f).coerceAtLeast(h * 0.2f), h / 2))
            }
        }
        // El cabo de sondeo: la raya con sus marcas y, lo hecho, una columna de agua con burbujas que acaba en la plomada.
        TemaId.ABISMO -> {
            drawLine(pista, Offset(0f, h / 2), Offset(w, h / 2), d)
            var x = 0f; var k = 0; while (x <= w) { drawLine(pista, Offset(x, h / 2 - (if (k % 5 == 0) 3f else 1.5f) * d), Offset(x, h / 2 + (if (k % 5 == 0) 3f else 1.5f) * d), d); x += 6 * d; k++ }
            if (fw > 0f) {
                drawRoundRect(color.copy(alpha = 0.35f), Offset(0f, h * 0.15f), Size(fw, h * 0.7f), CornerRadius(h * 0.2f))
                var b = h * 0.5f; var j = 0
                while (b < fw - h * 0.6f) { drawCircle(claro, h * (if (j % 2 == 0) 0.12f else 0.18f), Offset(b, h * (if (j % 2 == 0) 0.4f else 0.6f)), style = Stroke(0.8f * d)); b += maxOf(h * 0.9f, 1f); j++ }
                drawPath(Path().apply { moveTo(fw - h * 0.25f, h * 0.15f); lineTo(fw + h * 0.25f, h * 0.15f); lineTo(fw, h * 0.95f); close() }, color)
            }
        }
        // Alicatado: estrellas de ocho puntas en fila.
        TemaId.ANDALUS -> {
            drawRect(color.copy(alpha = 0.35f), Offset.Zero, size, style = Stroke(0.8f * d))
            if (fw > 0f) {
                drawRect(color, Offset.Zero, Size(fw, h))
                clipRect(right = fw) {
                    var x = h / 2
                    while (x < fw + h) { val o = Offset(x, h / 2); drawPath(figPoligono(o, h * 0.32f, 4, -90f), claro); drawPath(figPoligono(o, h * 0.32f, 4, -45f), claro); x += h * 1.1f }
                }
            }
        }
        // Lo lleno se cuenta en puntos y barras: cuatro puntos y una barra, una y otra vez, sobre el cinabrio.
        TemaId.MESO -> {
            drawRoundRect(pista, Offset.Zero, size, CornerRadius(h * 0.3f))
            if (fw > 0f) {
                drawRoundRect(color, Offset.Zero, Size(fw, h), CornerRadius(h * 0.3f))
                clipRect(right = fw) {
                    var x = h * 0.5f; var k = 0
                    while (x < fw + h) {
                        if (k % 5 == 4) { drawRect(claro, Offset(x - h * 0.1f, h * 0.36f), Size(h * 1.1f, h * 0.28f)); x += maxOf(h * 1.5f, 1f) }
                        else { drawCircle(claro, h * 0.16f, Offset(x, h / 2)); x += maxOf(h * 0.55f, 1f) }
                        k++
                    }
                }
            }
        }
        // Fieltro con su zigzag cosido.
        TemaId.MONGOL -> {
            drawRoundRect(pista, Offset.Zero, size, CornerRadius(3f * d))
            if (fw > 0f) {
                drawRoundRect(color, Offset.Zero, Size(fw, h), CornerRadius(3f * d))
                clipRect(right = fw) {
                    val z = Path(); var x = 0f; var arriba = true
                    z.moveTo(0f, h * 0.7f)
                    while (x < fw) { x += h * 0.4f; z.lineTo(x, if (arriba) h * 0.3f else h * 0.7f); arriba = !arriba }
                    drawPath(z, Color.White.copy(alpha = 0.5f), style = Stroke(d))
                }
            }
        }
        // Sombreado a pluma, cruzado, sobre la línea de construcción.
        TemaId.RENACIMIENTO -> {
            drawLine(color.copy(alpha = 0.3f), Offset(0f, h / 2), Offset(w, h / 2), 0.6f * d)
            drawLine(color.copy(alpha = 0.3f), Offset(0f, 0f), Offset(0f, h), 0.6f * d); drawLine(color.copy(alpha = 0.3f), Offset(w, 0f), Offset(w, h), 0.6f * d)
            if (fw > 0f) {
                drawRect(color.copy(alpha = 0.3f), Offset.Zero, Size(fw, h))
                clipRect(right = fw) {
                    var x = -h
                    while (x < fw) { drawLine(color, Offset(x, h), Offset(x + h, 0f), 0.9f * d); drawLine(color.copy(alpha = 0.5f), Offset(x, 0f), Offset(x + h, h), 0.6f * d); x += 2.5f * d }
                }
                drawRect(color, Offset.Zero, Size(fw, h), style = Stroke(0.8f * d))
            }
        }
        // Un cabo: lo hecho, trenzado y grueso; lo que falta, el cordón fino.
        TemaId.PIRATA -> {
            drawLine(pista, Offset(0f, h / 2), Offset(w, h / 2), h * 0.3f, StrokeCap.Round)
            if (fw > 0f) {
                drawLine(color, Offset(h / 2, h / 2), Offset(fw - h / 2, h / 2), h * 0.75f, StrokeCap.Round)
                var x = h * 0.4f
                while (x < fw - h * 0.4f) { drawLine(oscuro, Offset(x, h * 0.85f), Offset(x + h * 0.45f, h * 0.15f), 0.9f * d); x += maxOf(h * 0.5f, 1f) }
                drawCircle(color, h * 0.5f, Offset(fw - h / 2, h / 2)); drawCircle(claro, h * 0.2f, Offset(fw - h / 2, h / 2))
            }
        }
        // Cuña constructivista que crece hacia la punta.
        TemaId.URSS -> {
            drawLine(color.copy(alpha = 0.35f), Offset(0f, h / 2), Offset(w, h / 2), 1.2f * d)
            if (fw > 0f) drawPath(Path().apply { moveTo(0f, h * 0.38f); lineTo(fw, 0f); lineTo(fw, h); lineTo(0f, h * 0.62f); close() }, color)
        }
        // Banderola de cola de golondrina.
        TemaId.REVOLUCION -> {
            drawRect(pista, Offset.Zero, size)
            if (fw > 0f) {
                val n = minOf(h * 0.4f, fw * 0.5f)
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(fw, 0f); lineTo(fw - n, h / 2); lineTo(fw, h); lineTo(0f, h); close() }, color)
            }
        }
        // Piedra en chaflán con muescas de runa.
        TemaId.ANTIGUAS -> {
            fun losa(x1: Float): Path { val k = minOf(2f * d, x1 / 2); return Path().apply { moveTo(k, 0f); lineTo(x1 - k, 0f); lineTo(x1, k); lineTo(x1, h - k); lineTo(x1 - k, h); lineTo(k, h); lineTo(0f, h - k); lineTo(0f, k); close() } }
            drawPath(losa(w), pista)
            if (fw > 0f) {
                drawPath(losa(fw), color)
                clipRect(right = fw) { var x = 4f * d; var k = 0; while (x < fw) { drawLine(oscuro, Offset(x, if (k % 3 != 1) 0f else h), Offset(x + 1.5f * d, h / 2), d); x += 5f * d; k++ } }
            }
        }
        TemaId.CUEVA -> {
            // Un hueso largo: lo recorrido, teñido de ocre, con muescas de cinco en cinco.
            drawRoundRect(pista, Offset.Zero, Size(w, h), CornerRadius(h / 2))
            if (fw > 0f) {
                drawRoundRect(color, Offset.Zero, Size(fw, h), CornerRadius(h / 2))
                var x = 3f * d; var k = 0
                while (x < fw - 1f * d) { drawLine(oscuro, Offset(x, h * 0.2f), Offset(x, h * 0.8f), 0.7f * d); x += maxOf(if (k % 5 == 4) 5f * d else 2.5f * d, 1f); k++ }
            }
        }
    }
}

/** La hora de dormir: una línea de las 18 a las 6 con su marca y la tuya, con la figura del tema. */
@Composable
private fun RelojitoHora(suya: Double, tuya: Double?) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val medidor = androidx.compose.ui.text.rememberTextMeasurer()
    val estilo = androidx.compose.ui.text.TextStyle(fontSize = 9.sp, color = cs.onSurfaceVariant)
    fun norm(h: Double) = (if (h < 12) h + 24 else h).coerceIn(18.0, 32.0)
    Canvas(Modifier.fillMaxWidth().padding(top = 6.dp).height(34.dp)) {
        val y = size.height * 0.35f
        fun x(h: Double) = ((norm(h) - 18) / 14 * size.width).toFloat()
        drawLine(cs.outline.copy(alpha = 0.5f), Offset(0f, y), Offset(size.width, y), 2f)
        for (h in listOf(18, 21, 24, 27, 30)) { drawLine(cs.outline, Offset(x(h.toDouble()), y - 4f), Offset(x(h.toDouble()), y + 4f), 1.5f); drawText(medidor, "${h % 24}:00", Offset(x(h.toDouble()) - 10f, y + 6f), estilo) }
        figuraTema(tema, cs.secondary, Offset(x(suya), y), 6.dp.toPx(), cs.surface)
        tuya?.let { figuraTema(tema, cs.primary, Offset(x(it), y), 6.dp.toPx(), cs.surface); drawCircle(cs.surface, 2.5.dp.toPx(), Offset(x(it), y)) }
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
    val tema = Estilo.tema
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(170.dp)) {
            val c = center; val r = size.minDimension / 2 - 14.dp.toPx(); val ancho = 18.dp.toPx()
            fun ang(h: Double) = (h / 24 * 360 - 90).toFloat()
            // La esfera del tema, debajo de los tramos: solo adorno; las horas siguen donde estaban.
            esferaDia(tema, c, r, ancho, cs)
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

/**
 * La esfera del reloj de 24 horas en cada tema: reloj de sol, pantalla de consola, sol de Vergina, horologio iluminado,
 * ojo vigilante, medidor, engrane, papel polar, reloj de pared, laurel, reloj de flores, corona de pino, las doce horas
 * dobles, rosetón, radar, astrolabio, tono de yurta, trazo de construcción, estrella roja, escarapela y fases de la luna.
 * Va en el centro (dentro del anillo de tu sueño) y en el borde de afuera, sin tapar las horas 0, 6, 12 y 18.
 */
private fun DrawScope.esferaDia(tema: TemaId, c: Offset, r: Float, ancho: Float, cs: ColorScheme) {
    val d = density
    val ro = r + ancho / 2
    val rc = r - ancho - 12f * d
    val tinta = cs.onSurface; val linea = cs.outline
    fun marcas(n: Int, largo: Float, grosor: Float, color: Color) {
        for (k in 0 until n) { val a = k * 360f / n - 90f; drawLine(color, polarFig(c, ro + 1.5f * d, a), polarFig(c, ro + 1.5f * d + largo, a), grosor) }
    }
    when (tema) {
        // Reloj de sol: las líneas horarias y el gnomon.
        TemaId.ROMANO -> {
            for (k in 0 until 24) drawLine(linea.copy(alpha = 0.35f), polarFig(c, rc * 0.3f, k * 15f - 90f), polarFig(c, rc, k * 15f - 90f), 0.7f * d)
            drawPath(Path().apply { moveTo(c.x - rc * 0.12f, c.y + rc * 0.12f); lineTo(c.x + rc * 0.12f, c.y + rc * 0.12f); lineTo(c.x, c.y - rc * 0.55f); close() }, tinta.copy(alpha = 0.45f))
            drawCircle(tinta.copy(alpha = 0.4f), rc * 0.15f, c)
            marcas(24, 2.5f * d, d, linea)
        }
        // La plaza redonda con su obelisco: el anillo de sampietrini, la aguja al centro y las cuatro horas mayores marcadas.
        TemaId.URBE -> {
            for (k in 0 until 36) { val p = polarFig(c, rc, k * 10f); drawRect(linea.copy(alpha = 0.35f), p - Offset(1.2f * d, 1.2f * d), Size(2.4f * d, 2.4f * d)) }
            drawPath(Path().apply { moveTo(c.x - rc * 0.07f, c.y + rc * 0.25f); lineTo(c.x - rc * 0.04f, c.y - rc * 0.45f); lineTo(c.x, c.y - rc * 0.55f); lineTo(c.x + rc * 0.04f, c.y - rc * 0.45f); lineTo(c.x + rc * 0.07f, c.y + rc * 0.25f); close() }, tinta.copy(alpha = 0.4f))
            drawRect(tinta.copy(alpha = 0.3f), Offset(c.x - rc * 0.14f, c.y + rc * 0.25f), Size(rc * 0.28f, rc * 0.08f))
            for (k in 0 until 4) drawLine(tinta.copy(alpha = 0.6f), polarFig(c, ro + 1.5f * d, k * 90f - 90f), polarFig(c, ro + 6.5f * d, k * 90f - 90f), 1.8f * d)
            marcas(24, 2f * d, 0.7f * d, linea)
        }
        // Pantalla de consola: píxeles por hora y el cursor.
        TemaId.CIBER -> {
            for (k in 0 until 24) drawRect(cs.primary.copy(alpha = if (k % 6 == 0) 0.9f else 0.35f), polarFig(c, ro + 3f * d, k * 15f - 90f) - Offset(1.2f * d, 1.2f * d), Size(2.4f * d, 2.4f * d))
            drawCircle(cs.primary.copy(alpha = 0.3f), rc, c, style = Stroke(d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * d, 2f * d))))
            drawRect(cs.primary.copy(alpha = 0.6f), Offset(c.x - 3f * d, c.y - 5f * d), Size(6f * d, 10f * d))
        }
        // El reloj de un local de 24 horas: un aro de tubo con su halo y las cuatro horas marcadas en ámbar.
        TemaId.NEON -> {
            drawCircle(cs.primary.copy(alpha = 0.2f), rc, c, style = Stroke(4f * d))
            drawCircle(cs.primary.copy(alpha = 0.8f), rc, c, style = Stroke(1.2f * d))
            for (k in 0 until 4) drawLine(cs.secondary, polarFig(c, rc * 0.7f, k * 90f - 90f), polarFig(c, rc * 0.9f, k * 90f - 90f), 1.6f * d, StrokeCap.Round)
            marcas(24, 2f * d, 0.8f * d, cs.primary.copy(alpha = 0.4f))
        }
        // El sol de Vergina en el centro.
        TemaId.HELENO -> {
            drawPath(figEstrella(c, rc * 0.75f, rc * 0.32f, 16), cs.tertiary.copy(alpha = 0.35f))
            drawCircle(cs.tertiary.copy(alpha = 0.5f), rc * 0.24f, c)
            marcas(24, 2f * d, d, linea)
        }
        // El disco solar alado en el centro del reloj de las veinticuatro horas.
        TemaId.EGIPTO -> {
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply {
                    moveTo(c.x + s * rc * 0.22f, c.y - rc * 0.06f); quadraticTo(c.x + s * rc * 0.5f, c.y - rc * 0.24f, c.x + s * rc * 0.8f, c.y - rc * 0.1f)
                    lineTo(c.x + s * rc * 0.72f, c.y + rc * 0.06f); quadraticTo(c.x + s * rc * 0.45f, c.y + rc * 0.04f, c.x + s * rc * 0.22f, c.y + rc * 0.12f); close()
                }, cs.tertiary.copy(alpha = 0.4f))
            }
            drawCircle(cs.secondary.copy(alpha = 0.55f), rc * 0.2f, c)
            marcas(24, 2f * d, d, linea)
        }
        // Horologio iluminado: cuadrifolio rojo y puntos de rúbrica.
        TemaId.MEDIEVAL -> {
            val rubrica = Color(0xFFA3312F)
            for (k in 0 until 4) drawCircle(rubrica.copy(alpha = 0.6f), rc * 0.22f, polarFig(c, rc * 0.22f, k * 90f), style = Stroke(1.2f * d))
            for (k in 0 until 24) drawCircle(if (k % 3 == 0) rubrica else tinta.copy(alpha = 0.4f), 1.3f * d, polarFig(c, ro + 3f * d, k * 15f - 90f))
        }
        // El reloj de la guardia: la cruz patada al centro y las horas en torno; cada tres horas, una vigilia en rojo.
        TemaId.TEMPLARIO -> {
            val rojo = Color(0xFFB0191F)
            drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(c, rc * 0.42f), rojo.copy(alpha = 0.45f))
            for (k in 0 until 24) {
                val p = polarFig(c, ro + 3f * d, k * 15f - 90f)
                if (k % 3 == 0) drawRect(rojo, p - Offset(1.4f * d, 1.4f * d), Size(2.8f * d, 2.8f * d)) else drawCircle(tinta.copy(alpha = 0.4f), 1f * d, p)
            }
        }
        // El ojo que vigila y las marcas gruesas.
        TemaId.DISTOPIA -> {
            val l = rc * 0.7f
            drawPath(Path().apply { moveTo(c.x - l, c.y); quadraticTo(c.x, c.y - l * 0.9f, c.x + l, c.y); quadraticTo(c.x, c.y + l * 0.9f, c.x - l, c.y); close() }, tinta.copy(alpha = 0.5f), style = Stroke(1.5f * d))
            drawCircle(tinta.copy(alpha = 0.5f), rc * 0.25f, c, style = Stroke(1.5f * d)); drawCircle(cs.primary, rc * 0.1f, c)
            marcas(24, 4f * d, 2.2f * d, tinta.copy(alpha = 0.6f))
        }
        // Medidor: marcas largas cada tres horas y el átomo al centro.
        TemaId.REFUGIO -> {
            for (k in 0 until 24) { val a = k * 15f - 90f; drawLine(linea, polarFig(c, ro + 1.5f * d, a), polarFig(c, ro + (if (k % 3 == 0) 6f else 3f) * d, a), d) }
            figuraTema(tema, cs.primary.copy(alpha = 0.35f), c, rc * 0.5f, cs.surface)
        }
        // Engrane alrededor y cubo con sus pernos.
        TemaId.ORDEN -> {
            drawPath(figEngrane(c, ro + 3.5f * d, 24, ro / (ro + 3.5f * d) - 0.01f), linea.copy(alpha = 0.6f), style = Stroke(1.2f * d))
            drawCircle(linea.copy(alpha = 0.5f), rc * 0.32f, c, style = Stroke(2f * d))
            for (k in 0 until 6) drawCircle(linea.copy(alpha = 0.5f), 1.5f * d, polarFig(c, rc * 0.2f, k * 60f))
        }
        // El reloj de la estación: las doce horas en números de horario por fuera, los minutos marcados y el bisel de latón.
        TemaId.VAPOR -> {
            drawCircle(cs.primary.copy(alpha = 0.55f), ro + 4 * d, c, style = Stroke(2.4f * d))
            marcas(60, 2.5f * d, 0.7f * d, linea.copy(alpha = 0.5f))
            marcas(12, 5 * d, 1.6f * d, tinta.copy(alpha = 0.7f))
            drawCircle(linea.copy(alpha = 0.4f), rc * 0.3f, c, style = Stroke(1.2f * d))
            drawCircle(cs.primary.copy(alpha = 0.6f), 2.4f * d, c)
        }
        // Papel polar: círculos y radios.
        TemaId.LABORATORIO -> {
            listOf(1f, 0.66f, 0.33f).forEach { k -> drawCircle(linea.copy(alpha = 0.25f), rc * k, c, style = Stroke(0.6f * d)) }
            for (k in 0 until 24) drawLine(linea.copy(alpha = 0.2f), c, polarFig(c, rc, k * 15f), 0.5f * d)
            marcas(96, 1.5f * d, 0.5f * d, linea.copy(alpha = 0.5f))
        }
        // Reloj de pared: aro de madera, doce puntos y el eje.
        TemaId.COLONOS -> {
            drawCircle(cs.secondary.copy(alpha = 0.45f), ro + 3f * d, c, style = Stroke(3f * d))
            for (k in 0 until 12) drawCircle(tinta.copy(alpha = 0.35f), 1.4f * d, polarFig(c, rc * 0.85f, k * 30f))
            drawCircle(tinta.copy(alpha = 0.6f), 2.5f * d, c)
        }
        // Laurel por fuera y la estrella de la Legión al centro.
        TemaId.NAPOLEON -> {
            listOf(-60f, -45f, -30f, 30f, 45f, 60f, 120f, 135f, 150f, 210f, 225f, 240f).forEach { a ->
                val p = polarFig(c, ro + 4f * d, a)
                rotate(a, p) { drawOval(cs.tertiary.copy(alpha = 0.7f), Offset(p.x - 1.3f * d, p.y - 3f * d), Size(2.6f * d, 6f * d)) }
            }
            figuraTema(tema, cs.tertiary.copy(alpha = 0.35f), c, rc * 0.45f, cs.surface)
        }
        // El reloj de flores: doce pétalos.
        TemaId.BOTANICO -> {
            for (k in 0 until 12) rotate(k * 30f, c) { drawOval(cs.tertiary.copy(alpha = 0.3f), Offset(c.x - rc * 0.1f, c.y - rc * 0.7f), Size(rc * 0.2f, rc * 0.5f)) }
            drawCircle(cs.secondary.copy(alpha = 0.45f), rc * 0.18f, c)
            marcas(24, 2f * d, d, linea)
        }
        TemaId.RUINAS -> {
            // Doce hojas de hiedra alrededor, como las horas, y el semáforo diminuto al centro.
            for (k in 0 until 12) rotate(k * 30f, c) { val q = Offset(c.x, c.y - rc * 0.68f); val s = rc * 0.22f; drawPath(Path().apply { moveTo(q.x, q.y + s * 0.5f); lineTo(q.x - s * 0.55f, q.y + s * 0.35f); lineTo(q.x - s * 0.8f, q.y - s * 0.05f); lineTo(q.x - s * 0.32f, q.y - s * 0.05f); lineTo(q.x, q.y - s * 0.5f); lineTo(q.x + s * 0.32f, q.y - s * 0.05f); lineTo(q.x + s * 0.8f, q.y - s * 0.05f); lineTo(q.x + s * 0.55f, q.y + s * 0.35f); close() }, cs.primary.copy(alpha = 0.3f)) }
            drawRoundRect(cs.onSurface.copy(alpha = 0.35f), Offset(c.x - rc * 0.08f, c.y - rc * 0.18f), Size(rc * 0.16f, rc * 0.36f), CornerRadius(rc * 0.04f))
            drawCircle(cs.primary.copy(alpha = 0.8f), rc * 0.04f, Offset(c.x, c.y + rc * 0.1f))
            marcas(24, 2f * d, d, linea)
        }
        // Corona de pino con bayas y un copo al centro.
        TemaId.NAVIDAD -> {
            val pino = Color(0xFF2E6B3A).copy(alpha = 0.7f)
            for (k in 0 until 90) { val a = k * 4f; drawLine(pino, polarFig(c, ro + 0.5f * d, a), polarFig(c, ro + 3f * d, a + 6f), d) }
            listOf(45f, 135f, 225f, 315f).forEach { a -> drawCircle(Color(0xFFC0392B), 2f * d, polarFig(c, ro + 3f * d, a)) }
            figuraTema(tema, cs.primary.copy(alpha = 0.3f), c, rc * 0.5f, cs.surface)
        }
        // Las doce horas dobles y la moneda al centro.
        TemaId.SEDA -> {
            for (k in 0 until 12) { val a = (2 * k + 1) * 15f - 90f; drawLine(cs.tertiary.copy(alpha = 0.7f), polarFig(c, ro + d, a), polarFig(c, ro + 5f * d, a), 2.2f * d) }
            figuraTema(tema, cs.tertiary.copy(alpha = 0.3f), c, rc * 0.45f, cs.surface)
        }
        // Las doce horas del templo, con un sellito en cada una, y el mon de cerezo en medio.
        TemaId.EDO -> {
            for (k in 0 until 12) { val q = polarFig(c, ro + 3f * d, k * 30f - 90f); drawRect(cs.secondary.copy(alpha = 0.7f), Offset(q.x - 1.8f * d, q.y - 1.8f * d), Size(3.6f * d, 3.6f * d)) }
            figuraTema(tema, cs.secondary.copy(alpha = 0.3f), c, rc * 0.45f, cs.surface)
        }
        // Rosetón: doce lóbulos, radios y estrellas de oro.
        TemaId.GOTICO -> {
            drawCircle(cs.tertiary.copy(alpha = 0.5f), rc * 0.25f, c, style = Stroke(1.2f * d))
            for (k in 0 until 12) {
                drawCircle(cs.tertiary.copy(alpha = 0.4f), rc * 0.14f, polarFig(c, rc * 0.45f, k * 30f), style = Stroke(d))
                drawLine(cs.tertiary.copy(alpha = 0.3f), polarFig(c, rc * 0.6f, k * 30f + 15f), polarFig(c, rc, k * 30f + 15f), 0.8f * d)
            }
            for (k in 0 until 24) if (k % 6 != 0) drawCircle(cs.tertiary.copy(alpha = 0.7f), 1.2f * d, polarFig(c, ro + 3f * d, k * 15f - 90f))
        }
        // El almanaque: las ocho fases de la luna alrededor y, dentro, el heptágono del signo con el ojo.
        TemaId.ARCANO -> {
            for (k in 0 until 8) {
                val q = polarFig(c, rc * 0.72f, k * 45f - 90f); val rl = rc * 0.1f
                drawCircle(tinta.copy(alpha = 0.35f), rl, q, style = Stroke(0.8f * d))
                if (k != 0) clipPath(Path().apply { addOval(androidx.compose.ui.geometry.Rect(q, rl)) }) {
                    val f = k / 8f; val dx = if (f <= 0.5f) -2f * rl * (f * 2f) else 2f * rl * (2f - f * 2f)
                    drawCircle(tinta.copy(alpha = 0.35f), rl, q); drawCircle(cs.surface, rl * 1.02f, Offset(q.x + dx, q.y))
                }
            }
            val hp = Path()
            for (k in 0..6) { val q = polarFig(c, rc * 0.36f, -90f + k * 360f / 7); if (k == 0) hp.moveTo(q.x, q.y) else hp.lineTo(q.x, q.y) }
            drawPath(hp, cs.tertiary.copy(alpha = 0.55f), style = Stroke(1.2f * d))
            drawPath(Path().apply { moveTo(c.x - rc * 0.2f, c.y); quadraticTo(c.x, c.y - rc * 0.2f, c.x + rc * 0.2f, c.y); quadraticTo(c.x, c.y + rc * 0.2f, c.x - rc * 0.2f, c.y); close() }, cs.tertiary.copy(alpha = 0.55f), style = Stroke(d))
            marcas(24, 3f * d, 0.8f * d, linea.copy(alpha = 0.6f))
        }
        // Radar: anillos, cruz y el barrido.
        TemaId.ESPACIO -> {
            listOf(1f, 0.66f, 0.33f).forEach { k -> drawCircle(cs.secondary.copy(alpha = 0.3f), rc * k, c, style = Stroke(0.8f * d)) }
            drawLine(cs.secondary.copy(alpha = 0.25f), Offset(c.x - rc, c.y), Offset(c.x + rc, c.y), 0.6f * d)
            drawLine(cs.secondary.copy(alpha = 0.25f), Offset(c.x, c.y - rc), Offset(c.x, c.y + rc), 0.6f * d)
            drawArc(cs.secondary.copy(alpha = 0.12f), -90f, 50f, true, Offset(c.x - rc, c.y - rc), Size(rc * 2, rc * 2))
            marcas(24, 2f * d, 0.8f * d, cs.secondary.copy(alpha = 0.5f))
        }
        // El profundímetro: isóbatas onduladas dentro de la esfera, una más quieta que otra, y la escala de 24 marcas.
        TemaId.ABISMO -> {
            listOf(0.92f to 6, 0.62f to 8, 0.32f to 5).forEach { (k, n) ->
                val p = Path()
                for (i in 0..72) {
                    val a = 2 * PI * i / 72; val rr = rc * k * (1f + 0.04f * sin(a * n).toFloat())
                    val q = Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat())
                    if (i == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
                }
                drawPath(p, cs.tertiary.copy(alpha = 0.3f), style = Stroke(0.8f * d))
            }
            marcas(24, 2f * d, 0.8f * d, cs.tertiary.copy(alpha = 0.55f))
            marcas(4, 5f * d, 1.2f * d, cs.tertiary.copy(alpha = 0.7f))
        }
        // Astrolabio: la red con la estrella de ocho y el limbo graduado.
        TemaId.ANDALUS -> {
            drawPath(figPoligono(c, rc * 0.5f, 4, -90f), cs.tertiary.copy(alpha = 0.6f), style = Stroke(d))
            drawPath(figPoligono(c, rc * 0.5f, 4, -45f), cs.tertiary.copy(alpha = 0.6f), style = Stroke(d))
            drawCircle(cs.tertiary.copy(alpha = 0.4f), rc, c, style = Stroke(0.6f * d))
            marcas(72, 2f * d, 0.6f * d, cs.tertiary.copy(alpha = 0.55f)); marcas(24, 4f * d, d, cs.tertiary.copy(alpha = 0.7f))
        }
        // El día como la rueda del tzolk'in: veinte marcas de signo afuera, trece puntos adentro y el glifo de hoy al centro.
        TemaId.MESO -> {
            drawCircle(cs.primary.copy(alpha = 0.4f), rc, c, style = Stroke(0.8f * d))
            for (k in 0 until 13) drawCircle(cs.tertiary.copy(alpha = 0.6f), 1.6f * d, polarFig(c, rc * 0.82f, k * 360f / 13 - 90f))
            cartuchoMaya(c, rc * 0.36f, cs.primary.copy(alpha = 0.7f), cs.surface, tinta.copy(alpha = 0.7f), io.github.ruddy2003.vestigium.ui.Maya.tzolkinPartes(java.time.LocalDate.now()).second)
            marcas(20, 4f * d, 1.2f * d, cs.primary.copy(alpha = 0.6f)); marcas(260, 1.5f * d, 0.4f * d, cs.tertiary.copy(alpha = 0.35f))
        }
        // El tono de la yurta: la rueda, sus dos barras cruzadas y las varas del techo.
        TemaId.MONGOL -> {
            for (k in 0 until 32) { val a = k * 360f / 32; drawLine(linea.copy(alpha = 0.3f), polarFig(c, rc * 0.34f, a), polarFig(c, rc, a), 0.8f * d) }
            drawCircle(cs.primary.copy(alpha = 0.45f), rc * 0.32f, c, style = Stroke(2.5f * d))
            for (s in listOf(-1f, 1f)) {
                drawLine(cs.primary.copy(alpha = 0.4f), Offset(c.x + s * rc * 0.08f, c.y - rc * 0.3f), Offset(c.x + s * rc * 0.08f, c.y + rc * 0.3f), 1.2f * d)
                drawLine(cs.primary.copy(alpha = 0.4f), Offset(c.x - rc * 0.3f, c.y + s * rc * 0.08f), Offset(c.x + rc * 0.3f, c.y + s * rc * 0.08f), 1.2f * d)
            }
        }
        // Trazo de construcción: círculo, cuadrado inscrito, ejes y la punta del compás.
        TemaId.RENACIMIENTO -> {
            val sepia = tinta.copy(alpha = 0.3f); val l = rc * 0.707f
            drawCircle(sepia, rc, c, style = Stroke(0.6f * d))
            drawRect(sepia, Offset(c.x - l, c.y - l), Size(l * 2, l * 2), style = Stroke(0.6f * d))
            drawLine(sepia, Offset(c.x - l, c.y - l), Offset(c.x + l, c.y + l), 0.5f * d); drawLine(sepia, Offset(c.x + l, c.y - l), Offset(c.x - l, c.y + l), 0.5f * d)
            drawLine(sepia, Offset(c.x - rc, c.y), Offset(c.x + rc, c.y), 0.5f * d); drawLine(sepia, Offset(c.x, c.y - rc), Offset(c.x, c.y + rc), 0.5f * d)
            drawCircle(sepia, 2f * d, c, style = Stroke(0.8f * d))
            for (k in 0 until 4) drawArc(sepia, k * 90f + 30f, 30f, false, Offset(c.x - rc * 1.06f, c.y - rc * 1.06f), Size(rc * 2.12f, rc * 2.12f), style = Stroke(0.6f * d))
        }
        // La rosa de los vientos en el centro de la esfera, con sus ocho rumbos, y las horas como campanadas.
        TemaId.PIRATA -> {
            val sepia = tinta.copy(alpha = 0.35f)
            for (k in 0 until 8) {
                val a = k * 45f - 90f; val largo = if (k % 2 == 0) rc * 0.9f else rc * 0.55f
                val punta = polarFig(c, largo, a); val i1 = polarFig(c, rc * 0.1f, a - 90f); val i2 = polarFig(c, rc * 0.1f, a + 90f)
                drawPath(Path().apply { moveTo(i1.x, i1.y); lineTo(punta.x, punta.y); lineTo(i2.x, i2.y); close() }, if (k == 0) cs.tertiary.copy(alpha = 0.5f) else sepia)
            }
            drawCircle(sepia, rc * 0.95f, c, style = Stroke(0.6f * d))
            for (k in 0 until 48) drawCircle(linea.copy(alpha = if (k % 2 == 0) 0.6f else 0.3f), (if (k % 2 == 0) 1.3f else 0.8f) * d, polarFig(c, ro + 3 * d, k * 7.5f - 90f))
        }
        // Estrella roja sobre la barra diagonal.
        TemaId.URSS -> {
            rotate(-30f, c) { drawRect(tinta.copy(alpha = 0.5f), Offset(c.x - rc, c.y - rc * 0.09f), Size(rc * 2, rc * 0.18f)) }
            drawPath(figEstrella(c, rc * 0.5f, rc * 0.2f, 5), Color(0xFFC62828).copy(alpha = 0.8f))
            marcas(24, 3f * d, 1.6f * d, tinta.copy(alpha = 0.55f))
        }
        // La escarapela al centro.
        TemaId.REVOLUCION -> {
            drawCircle(Color(0xFFC8102E).copy(alpha = 0.5f), rc * 0.55f, c)
            drawCircle(Color.White.copy(alpha = 0.8f), rc * 0.4f, c)
            drawCircle(Color(0xFF1F3A93).copy(alpha = 0.55f), rc * 0.25f, c)
            marcas(24, 2f * d, d, linea)
        }
        // Las lunas por fuera (llena, creciente, nueva, menguante) y una runa inventada al centro.
        TemaId.ANTIGUAS -> {
            val luna = tinta.copy(alpha = 0.45f); val rl = 3f * d
            listOf(-45f, 45f, 135f, 225f).forEachIndexed { i, a ->
                val p = polarFig(c, ro + 5f * d, a); val caja = Offset(p.x - rl, p.y - rl); val lado = Size(rl * 2, rl * 2)
                when (i) {
                    0 -> drawCircle(luna, rl, p)
                    1 -> { drawArc(luna, -90f, 180f, true, caja, lado); drawCircle(luna, rl, p, style = Stroke(0.6f * d)) }
                    2 -> drawCircle(luna, rl, p, style = Stroke(0.8f * d))
                    else -> { drawArc(luna, 90f, 180f, true, caja, lado); drawCircle(luna, rl, p, style = Stroke(0.6f * d)) }
                }
            }
            val q = rc * 0.4f
            drawPath(figPoligono(c + Offset(0f, -q * 0.3f), q * 0.5f, 4, -90f), luna, style = Stroke(1.5f * d))
            drawLine(luna, Offset(c.x, c.y + q * 0.2f), Offset(c.x - q * 0.5f, c.y + q), 1.5f * d)
            drawLine(luna, Offset(c.x, c.y + q * 0.2f), Offset(c.x + q * 0.5f, c.y + q), 1.5f * d)
            drawLine(luna, Offset(c.x, c.y - q * 1.1f), Offset(c.x, c.y + q * 0.2f), 1.5f * d)
        }
        TemaId.CUEVA -> {
            // Un sol grabado en el centro (anillo, punto y rayos) y una hilera de puntos por fuera, uno por hora.
            val t2 = tinta.copy(alpha = 0.45f)
            for (k in 0 until 24) drawCircle(t2, (if (k % 6 == 0) 1.6f else 0.9f) * d, polarFig(c, ro + 5f * d, k * 15f - 90f))
            val q = rc * 0.32f
            drawCircle(t2, q, c, style = Stroke(1.4f * d)); drawCircle(t2, q * 0.3f, c)
            for (k in 0 until 8) drawLine(t2, polarFig(c, q * 1.3f, k * 45f), polarFig(c, q * 1.8f, k * 45f), 1.2f * d)
        }
    }
}

private fun horaCorta(h: Double): String { val x = ((h % 24) + 24) % 24; return "%d:%02d".format(x.toInt(), ((x - x.toInt()) * 60).roundToInt().coerceAtMost(59)) }

/** «Tú y Naa», pero «tú y una astronauta», «tú y alguien promedio». */
private fun conQuien(v: OtrasVidas.Vida): String =
    if (listOf("Un ", "Una ", "La ", "Alguien").any { v.nombre.startsWith(it) }) v.nombre.replaceFirstChar { it.lowercase() } else v.nombre
