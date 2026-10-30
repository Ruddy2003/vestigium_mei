@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.ciencia.Logros
import io.github.ruddy2003.vestigium.ciencia.TusNumeros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.LogroPropio
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Fiesta
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.figEngrane
import io.github.ruddy2003.vestigium.ui.figEstrella
import io.github.ruddy2003.vestigium.ui.figLegion
import io.github.ruddy2003.vestigium.ui.figOndulado
import io.github.ruddy2003.vestigium.ui.figPoligono
import io.github.ruddy2003.vestigium.ui.polarFig
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.ruddy2003.vestigium.ui.Progreso

private val METALES = listOf(Color(0xFFB27A45), Color(0xFFBFC6CF), Color(0xFFE3B748), Color(0xFF9AD8E6))

/**
 * Una medalla: el metal según el nivel, el signo del logro al centro y, si falta, cuánto llevas en un arco. La forma es la del
 * tema (ver [medallaTema]); sin conseguir, la misma silueta en gris.
 */
@Composable
fun MedallaLogro(l: Logros.Logro, tam: Dp, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    val metal = METALES[(l.nivel - 1).coerceIn(0, 3)]
    val secreto = l.secreto && !l.hecho
    Box(modifier.size(tam), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            medallaTema(tema, l.hecho, metal, cs.outline, cs.surfaceVariant.copy(alpha = 0.6f))
            if (l.hecho) {
                if (l.nivel >= 4) for (i in 0 until 12) {
                    val a = Math.toRadians(i * 30.0)
                    drawLine(metal, Offset(center.x + (r * 1.0f) * kotlin.math.cos(a).toFloat(), center.y + r * kotlin.math.sin(a).toFloat()),
                        Offset(center.x + r * 0.9f * kotlin.math.cos(a).toFloat(), center.y + r * 0.9f * kotlin.math.sin(a).toFloat()), r * 0.05f)
                }
            } else {
                if (!secreto && l.fraccion > 0f) drawArc(cs.primary, -90f, 360f * l.fraccion, false, Offset(center.x - r * 0.94f, center.y - r * 0.94f),
                    Size(r * 1.88f, r * 1.88f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
            }
        }
        Text(if (secreto) "?" else l.icono, fontSize = (tam.value * 0.42f).sp, textAlign = TextAlign.Center,
            color = if (l.hecho) Color(0xFF2A1E0E) else cs.onSurfaceVariant.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
    }
}

/**
 * La medalla de cada tema: clípeo con laurel, chip de consola, sol de Vergina, sello pendiente, insignia numerada, botón
 * atómico, engrane, placa hexagonal, roseta de feria, Legión de Honor, camafeo de herbario, esfera de árbol, medallón
 * polilobulado, rosetón, parche de misión, estrella de lazo, paiza, tondo de construcción, orden de la estrella roja,
 * escarapela y gema tallada. Conseguida, en el metal de su nivel ([metal]); si no, la silueta en [apagado] con trazo [borde].
 */
private fun DrawScope.medallaTema(tema: TemaId, hecho: Boolean, metal: Color, borde: Color, apagado: Color) {
    val c = center; val r = size.minDimension / 2
    val relleno: Brush = if (hecho) Brush.radialGradient(listOf(metal, metal.copy(alpha = 0.72f), metal.copy(alpha = 0.45f)), c, r) else SolidColor(apagado)
    val canto = if (hecho) metal else borde.copy(alpha = 0.5f)
    val luz = if (hecho) Color.White.copy(alpha = 0.38f) else borde.copy(alpha = 0.28f)
    val sombra = if (hecho) lerp(metal, Color.Black, 0.38f) else borde.copy(alpha = 0.4f)
    val g = r * 0.07f
    fun forma(p: Path) { drawPath(p, relleno); drawPath(p, canto, style = Stroke(g)) }
    fun disco(centro: Offset, radio: Float) = Path().apply { addOval(Rect(centro, radio)) }
    when (tema) {
        // Clípeo: escudo redondo con una corona de laurel en el canto.
        TemaId.ROMANO -> {
            forma(disco(c, r * 0.96f))
            drawCircle(luz, r * 0.76f, c, style = Stroke(r * 0.05f))
            for (k in 0 until 18) rotate(k * 20f, c) { drawOval(sombra, Offset(c.x - r * 0.045f, c.y - r * 0.93f), Size(r * 0.09f, r * 0.15f)) }
        }
        // Chip de consola: cuadro de esquinas cortadas con sus patas.
        TemaId.CIBER -> {
            val k = r * 0.8f; val q = r * 0.26f
            for (i in -1..1) {
                val d = i * r * 0.36f
                drawLine(canto, Offset(c.x + d, c.y - k), Offset(c.x + d, c.y - r), g); drawLine(canto, Offset(c.x + d, c.y + k), Offset(c.x + d, c.y + r), g)
                drawLine(canto, Offset(c.x - k, c.y + d), Offset(c.x - r, c.y + d), g); drawLine(canto, Offset(c.x + k, c.y + d), Offset(c.x + r, c.y + d), g)
            }
            forma(Path().apply {
                moveTo(c.x - k + q, c.y - k); lineTo(c.x + k - q, c.y - k); lineTo(c.x + k, c.y - k + q); lineTo(c.x + k, c.y + k - q)
                lineTo(c.x + k - q, c.y + k); lineTo(c.x - k + q, c.y + k); lineTo(c.x - k, c.y + k - q); lineTo(c.x - k, c.y - k + q); close()
            })
            drawRect(luz, Offset(c.x - r * 0.6f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.2f), style = Stroke(r * 0.04f))
        }
        TemaId.NEON -> {
            val k = r * 0.8f; val q = r * 0.26f
            for (i in -1..1) {
                val d = i * r * 0.36f
                drawLine(canto, Offset(c.x + d, c.y - k), Offset(c.x + d, c.y - r), g); drawLine(canto, Offset(c.x + d, c.y + k), Offset(c.x + d, c.y + r), g)
                drawLine(canto, Offset(c.x - k, c.y + d), Offset(c.x - r, c.y + d), g); drawLine(canto, Offset(c.x + k, c.y + d), Offset(c.x + r, c.y + d), g)
            }
            forma(Path().apply {
                moveTo(c.x - k + q, c.y - k); lineTo(c.x + k - q, c.y - k); lineTo(c.x + k, c.y - k + q); lineTo(c.x + k, c.y + k - q)
                lineTo(c.x + k - q, c.y + k); lineTo(c.x - k + q, c.y + k); lineTo(c.x - k, c.y + k - q); lineTo(c.x - k, c.y - k + q); close()
            })
            drawRect(luz, Offset(c.x - r * 0.6f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.2f), style = Stroke(r * 0.04f))
        }
        // El sol de Vergina: disco con dieciséis rayos cortos.
        TemaId.HELENO -> {
            forma(figEstrella(c, r, r * 0.66f, 16))
            drawCircle(sombra, r * 0.62f, c, style = Stroke(g))
            drawCircle(luz, r * 0.52f, c, style = Stroke(r * 0.04f))
        }
        // Sello de lacre que cuelga de su cordón.
        TemaId.MEDIEVAL -> {
            val cordon = if (hecho) Color(0xFF8E2A22) else borde.copy(alpha = 0.4f)
            drawLine(cordon, Offset(c.x - r * 0.3f, c.y - r), Offset(c.x - r * 0.08f, c.y - r * 0.55f), r * 0.07f)
            drawLine(cordon, Offset(c.x + r * 0.3f, c.y - r), Offset(c.x + r * 0.08f, c.y - r * 0.55f), r * 0.07f)
            val cs0 = c + Offset(0f, r * 0.12f)
            forma(figOndulado(cs0, r * 0.84f, 7, 0.1f))
            drawCircle(luz, r * 0.6f, cs0, style = Stroke(r * 0.05f))
            drawCircle(sombra, r * 0.68f, cs0, style = Stroke(r * 0.03f))
        }
        // Insignia numerada: hexágono con banda roja y remaches.
        TemaId.DISTOPIA -> {
            val hex = figPoligono(c, r * 0.97f, 6)
            forma(hex)
            clipPath(hex) { drawRect(if (hecho) Color(0xFFB3261E).copy(alpha = 0.85f) else borde.copy(alpha = 0.25f), Offset(0f, c.y + r * 0.5f), Size(size.width, r * 0.2f)) }
            drawCircle(sombra, r * 0.06f, c + Offset(0f, -r * 0.8f)); drawCircle(sombra, r * 0.06f, c + Offset(0f, r * 0.8f))
            drawPath(figPoligono(c, r * 0.78f, 6), luz, style = Stroke(r * 0.04f))
        }
        // Botón atómico: el disco entre tres órbitas.
        TemaId.REFUGIO -> {
            for (a in listOf(0f, 60f, 120f)) rotate(a, c) { drawOval(sombra, Offset(c.x - r * 0.98f, c.y - r * 0.3f), Size(r * 1.96f, r * 0.6f), style = Stroke(r * 0.05f)) }
            forma(disco(c, r * 0.74f))
            drawCircle(luz, r * 0.6f, c, style = Stroke(r * 0.04f))
        }
        // Engrane de diez dientes.
        TemaId.ORDEN -> {
            forma(figEngrane(c, r * 0.98f, 10, 0.82f))
            drawCircle(sombra, r * 0.62f, c, style = Stroke(g))
            drawCircle(luz, r * 0.52f, c, style = Stroke(r * 0.04f))
        }
        // Placa hexagonal con el anillo de benceno.
        TemaId.LABORATORIO -> {
            forma(figPoligono(c, r * 0.97f, 6, 0f))
            drawCircle(luz, r * 0.56f, c, style = Stroke(r * 0.05f))
            for (k in 0 until 6) { val a = k * 60f + 30f; drawLine(sombra, polarFig(c, r * 0.8f, a), polarFig(c, r * 0.66f, a), r * 0.04f) }
        }
        // Roseta de feria del condado con sus dos cintas.
        TemaId.COLONOS -> {
            val cinta = if (hecho) Color(0xFF2F4A7A) else borde.copy(alpha = 0.35f)
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply {
                    moveTo(c.x + s * r * 0.05f, c.y + r * 0.2f); lineTo(c.x + s * r * 0.4f, c.y + r * 0.25f); lineTo(c.x + s * r * 0.6f, c.y + r)
                    lineTo(c.x + s * r * 0.42f, c.y + r * 0.86f); lineTo(c.x + s * r * 0.3f, c.y + r); close()
                }, cinta)
            }
            val cr = c + Offset(0f, -r * 0.1f)
            forma(figOndulado(cr, r * 0.88f, 20, 0.12f))
            drawCircle(sombra, r * 0.58f, cr, style = Stroke(g * 0.7f))
            drawCircle(luz, r * 0.5f, cr, style = Stroke(r * 0.04f))
        }
        // La Legión de Honor: cinco brazos de dos puntas y el medallón esmaltado.
        TemaId.NAPOLEON -> {
            forma(figLegion(c, r * 0.98f))
            drawCircle(if (hecho) Color(0xFFF4EFE3) else apagado, r * 0.44f, c)
            drawCircle(if (hecho) Color(0xFF1F3A93) else borde.copy(alpha = 0.5f), r * 0.44f, c, style = Stroke(r * 0.08f))
        }
        // Camafeo de herbario: óvalo con una ramita al pie.
        TemaId.BOTANICO -> {
            forma(Path().apply { addOval(Rect(c.x - r * 0.74f, c.y - r * 0.97f, c.x + r * 0.74f, c.y + r * 0.97f)) })
            drawOval(luz, Offset(c.x - r * 0.6f, c.y - r * 0.83f), Size(r * 1.2f, r * 1.66f), style = Stroke(r * 0.04f))
            val tallo = if (hecho) lerp(metal, Color(0xFF3F5A2A), 0.6f) else borde.copy(alpha = 0.45f)
            drawLine(tallo, Offset(c.x - r * 0.32f, c.y + r * 0.78f), Offset(c.x + r * 0.32f, c.y + r * 0.62f), r * 0.04f)
            for (k in 0..2) { val x = c.x - r * 0.2f + k * r * 0.2f; drawOval(tallo, Offset(x - r * 0.06f, c.y + r * (0.5f - k * 0.05f)), Size(r * 0.12f, r * 0.22f)) }
        }
        // Esfera de árbol con su tapa, su gancho y una ola de escarcha.
        TemaId.NAVIDAD -> {
            val cb = c + Offset(0f, r * 0.1f)
            drawArc(sombra, 180f, 180f, false, Offset(c.x - r * 0.12f, c.y - r), Size(r * 0.24f, r * 0.2f), style = Stroke(r * 0.05f))
            drawRect(sombra, Offset(c.x - r * 0.2f, c.y - r * 0.88f), Size(r * 0.4f, r * 0.16f))
            val bola = disco(cb, r * 0.86f)
            forma(bola)
            clipPath(bola) {
                drawPath(Path().apply {
                    moveTo(cb.x - r, cb.y + r * 0.5f)
                    for (k in 0 until 4) { val x0 = cb.x - r + k * r * 0.5f; quadraticTo(x0 + r * 0.125f, cb.y + r * 0.36f, x0 + r * 0.25f, cb.y + r * 0.5f); quadraticTo(x0 + r * 0.375f, cb.y + r * 0.64f, x0 + r * 0.5f, cb.y + r * 0.5f) }
                }, luz, style = Stroke(r * 0.06f))
            }
        }
        // Medallón de ocho lóbulos con su marco cuadrado.
        TemaId.SEDA -> {
            forma(figOndulado(c, r * 0.98f, 8, 0.2f))
            drawCircle(sombra, r * 0.62f, c, style = Stroke(g * 0.7f))
            drawRect(luz, Offset(c.x - r * 0.44f, c.y - r * 0.44f), Size(r * 0.88f, r * 0.88f), style = Stroke(r * 0.04f))
        }
        // Rosetón: la rueda con su tracería de doce pétalos.
        TemaId.GOTICO -> {
            forma(disco(c, r * 0.97f))
            for (k in 0 until 12) {
                val a = k * 30f
                drawLine(sombra, polarFig(c, r * 0.58f, a), polarFig(c, r * 0.97f, a), r * 0.04f)
                drawCircle(luz, r * 0.13f, polarFig(c, r * 0.78f, a + 15f), style = Stroke(r * 0.035f))
            }
            drawCircle(sombra, r * 0.58f, c, style = Stroke(r * 0.06f))
        }
        // Parche de misión: óvalo bordado y la órbita.
        TemaId.ESPACIO -> {
            forma(Path().apply { addOval(Rect(c.x - r * 0.97f, c.y - r * 0.8f, c.x + r * 0.97f, c.y + r * 0.8f)) })
            drawOval(luz, Offset(c.x - r * 0.86f, c.y - r * 0.69f), Size(r * 1.72f, r * 1.38f),
                style = Stroke(r * 0.04f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(r * 0.1f, r * 0.07f))))
            rotate(-18f, c) { drawArc(sombra, 200f, 140f, false, Offset(c.x - r * 0.9f, c.y - r * 0.32f), Size(r * 1.8f, r * 0.64f), style = Stroke(r * 0.05f)) }
            drawCircle(sombra, r * 0.07f, polarFig(c, r * 0.62f, -40f))
        }
        // Estrella de lazo de ocho puntas: dos cuadrados.
        TemaId.ANDALUS -> {
            val a1 = figPoligono(c, r * 0.97f, 4, -90f); val a2 = figPoligono(c, r * 0.97f, 4, -45f)
            drawPath(a1, relleno); drawPath(a2, relleno)
            drawPath(a1, canto, style = Stroke(g * 0.7f)); drawPath(a2, canto, style = Stroke(g * 0.7f))
            drawCircle(luz, r * 0.55f, c, style = Stroke(r * 0.05f))
        }
        // Paiza: la tablilla de paso con su agujero para colgarla.
        TemaId.MONGOL -> {
            forma(Path().apply { addRoundRect(RoundRect(c.x - r * 0.62f, c.y - r * 0.97f, c.x + r * 0.62f, c.y + r * 0.97f, CornerRadius(r * 0.5f))) })
            drawCircle(if (hecho) Color(0xFF2A1E0E).copy(alpha = 0.55f) else borde.copy(alpha = 0.4f), r * 0.1f, c + Offset(0f, -r * 0.74f))
            drawCircle(luz, r * 0.17f, c + Offset(0f, -r * 0.74f), style = Stroke(r * 0.04f))
            drawRoundRect(luz, Offset(c.x - r * 0.5f, c.y - r * 0.5f), Size(r, r * 1.35f), CornerRadius(r * 0.3f), style = Stroke(r * 0.04f))
        }
        // Tondo con el trazo de construcción: cuadrado inscrito, diagonales y las puntas del compás.
        TemaId.RENACIMIENTO -> {
            forma(disco(c, r * 0.97f))
            val l = r * 0.68f
            drawRect(sombra, Offset(c.x - l, c.y - l), Size(l * 2, l * 2), style = Stroke(r * 0.03f))
            drawLine(sombra.copy(alpha = sombra.alpha * 0.6f), Offset(c.x - l, c.y - l), Offset(c.x + l, c.y + l), r * 0.02f)
            drawLine(sombra.copy(alpha = sombra.alpha * 0.6f), Offset(c.x + l, c.y - l), Offset(c.x - l, c.y + l), r * 0.02f)
            for (k in 0 until 4) drawCircle(sombra, r * 0.045f, polarFig(c, l * 1.414f, k * 90f + 45f))
        }
        // Orden de la estrella roja: estrella esmaltada con el centro de metal.
        TemaId.URSS -> {
            val st = figEstrella(c, r * 0.99f, r * 0.45f, 5)
            drawPath(st, if (hecho) Color(0xFFC62828) else apagado)
            drawPath(st, canto, style = Stroke(g))
            drawCircle(relleno, r * 0.46f, c)
            drawCircle(canto, r * 0.46f, c, style = Stroke(g * 0.6f))
        }
        // Escarapela plisada: rojo, blanco y azul, con el metal al centro.
        TemaId.REVOLUCION -> {
            val pl = figOndulado(c, r * 0.98f, 22, 0.08f)
            drawPath(pl, if (hecho) Color(0xFFC8102E) else apagado)
            drawCircle(if (hecho) Color(0xFFF4F1EA) else apagado, r * 0.76f, c)
            drawCircle(if (hecho) Color(0xFF1F3A93) else borde.copy(alpha = 0.3f), r * 0.6f, c)
            drawCircle(relleno, r * 0.46f, c)
            drawCircle(canto, r * 0.46f, c, style = Stroke(g * 0.6f)); drawPath(pl, canto, style = Stroke(g * 0.5f))
        }
        // Gema tallada: octágono con su mesa y sus facetas.
        TemaId.ANTIGUAS -> {
            forma(figPoligono(c, r * 0.98f, 8, -67.5f))
            drawPath(figPoligono(c, r * 0.55f, 8, -67.5f), luz, style = Stroke(r * 0.04f))
            for (k in 0 until 8) { val a = -67.5f + k * 45f; drawLine(luz, polarFig(c, r * 0.55f, a), polarFig(c, r * 0.98f, a), r * 0.03f) }
        }
    }
}

@Composable
private fun rememberLogros(): List<Logros.Logro>? {
    val ctx = LocalContext.current
    val v by produceState(if (TusNumeros.sincrono) Logros.todos(ctx) else null, Repo.config, Repo.dias, Repo.salud) {
        if (!TusNumeros.sincrono) value = withContext(Dispatchers.Default) { runCatching { Logros.todos(ctx) }.getOrDefault(emptyList()) }
    }
    return v
}

/** En Más: los últimos logros como medallas, cuántos llevas y si hay nuevos. */
@Composable
fun RecuadroLogros(nav: Nav) {
    val l = rememberLogros()
    val hechos = l?.filter { it.hecho }?.sortedByDescending { it.conseguido }.orEmpty()
    val nuevos = l?.count { it.hecho && it.id !in Repo.config.logros_vistos } ?: 0
    val cs = MaterialTheme.colorScheme
    val detalle = when {
        l == null -> "Contando…"
        nuevos > 0 && Repo.config.logros_vistos.isNotEmpty() -> "¡$nuevos nuevo${if (nuevos == 1) "" else "s"}! · ${hechos.size} de ${l.size} conseguidos"
        else -> "${hechos.size} de ${l.size} conseguidos" + (l.filter { !it.hecho && !it.secreto }.maxByOrNull { it.fraccion }?.let { " · cerca: ${it.nombre}" } ?: "")
    }
    Recuadro("Logros", detalle, alto = 118.dp, onClick = { nav.ir("logros") }) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(cs.primary.copy(alpha = 0.10f), cs.surface)))) {
            Row(Modifier.fillMaxSize().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                val muestra = (hechos.take(5) + l.orEmpty().filter { !it.hecho && !it.secreto }.sortedByDescending { it.fraccion }.take(5)).take(6)
                if (muestra.isEmpty()) Text("Tus medallas aparecerán aquí", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
                muestra.forEach { TrofeoLogro(it, 76.dp) }
            }
        }
    }
}

@Composable
fun PantallaLogros(nav: Nav) {
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    val todos = rememberLogros()
    var filtro by remember { mutableStateOf("todos") }
    var nuevos by remember { mutableStateOf<List<Logros.Logro>>(emptyList()) }
    var crear by remember { mutableStateOf(false) }
    var abierto by remember { mutableStateOf<Logros.Logro?>(null) }
    // Al abrir: se festejan los nuevos (la primera vez solo se cuentan, para no festejar todo tu historial de golpe).
    LaunchedEffect(todos != null) {
        val l = todos ?: return@LaunchedEffect
        val vistos = Repo.config.logros_vistos
        val n = l.filter { it.hecho && it.id !in vistos }
        if (n.isNotEmpty()) {
            if (vistos.isNotEmpty()) { nuevos = n; Fiesta.lanzar(); sentir(Sentidos.Evento.LOGRO) }
            Logros.marcarVistos(n)
        }
    }
    Hoja("Logros", nav.atras, "Tu sala de trofeos: escribir, moverte, viajar, leer, ser constante. Toca uno para ver lo que lograste o cuánto te falta. Los de constancia piden la mayoría de los días, no una racha perfecta.") {
        val l = todos
        if (l == null) { Vacio("Contando…"); return@Hoja }
        val hechos = l.filter { it.hecho }
        Tarjeta(Modifier.padding(top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("${hechos.size} de ${l.size}", style = MaterialTheme.typography.headlineMedium)
                    Ayuda("logros conseguidos · ${(hechos.size * 100 / maxOf(1, l.size))}%")
                }
                (1..4).forEach { nv ->
                    val k = hechos.count { it.nivel == nv }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(start = 10.dp)) {
                        Box(Modifier.size(22.dp).background(METALES[nv - 1], androidx.compose.foundation.shape.CircleShape))
                        Text("$k", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Progreso(progress = { hechos.size.toFloat() / maxOf(1, l.size) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
        }
        Seccion("Hall de logros", "Tus favoritos, en exhibición. Toca «elegir piezas» para cambiarlos.") { CuerpoHall(l) }
        if (nuevos.isNotEmpty()) Tarjeta(Modifier.padding(top = 10.dp)) {
            Text("¡Nuevo" + if (nuevos.size > 1) "s logros!" else " logro!", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Row(Modifier.padding(top = 8.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                nuevos.take(12).forEach { n -> Box(Modifier.clickable { abierto = n }) { TrofeoConNombre(n, 84.dp) } }
            }
        }
        Row(Modifier.padding(top = 12.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (listOf("todos" to "Todos", "hechos" to "Conseguidos", "cerca" to "Cerca") + Logros.CATEGORIAS.filter { c -> l.any { it.categoria == c } || c == "Tuyos" }.map { it to it })
                .forEach { (id, n) -> Ficha(n, filtro == id) { filtro = id } }
        }
        val lista = when (filtro) {
            "todos" -> l.sortedWith(compareByDescending<Logros.Logro> { it.hecho }.thenByDescending { it.conseguido ?: "" }.thenByDescending { it.fraccion })
            "hechos" -> hechos.sortedByDescending { it.conseguido }
            "cerca" -> l.filter { !it.hecho && !it.secreto && it.fraccion > 0f }.sortedByDescending { it.fraccion }.take(25)
            else -> l.filter { it.categoria == filtro }.sortedWith(compareByDescending<Logros.Logro> { it.hecho }.thenByDescending { it.fraccion })
        }
        if (filtro == "Tuyos") {
            BotonPrincipal("+ Inventar un logro", Modifier.fillMaxWidth().padding(top = 10.dp)) { crear = true }
            Ayuda("Por ejemplo: «Leer 20 de 30 días», «10 veces con @abuela», «100,000 pasos en una semana» o «Gimnasio 3 veces por semana».", Modifier.padding(top = 6.dp))
        }
        if (crear) EditorLogro(cerrar = { crear = false })
        if (lista.isEmpty()) Vacio(if (filtro == "Tuyos") "Todavía no inventas ninguno." else "Nada por aquí todavía.")
        // La sala de trofeos: en «Todos», una vitrina por categoría; en los filtros, una sola.
        if (filtro == "todos") Logros.CATEGORIAS.forEach { c ->
            val deC = l.filter { it.categoria == c }.sortedWith(compareByDescending<Logros.Logro> { it.hecho }.thenByDescending { if (it.hecho) it.nivel.toFloat() else it.fraccion })
            SalaTrofeos(deC, titulo = c + " · " + deC.count { it.hecho } + " de " + deC.size) { abierto = it }
        } else SalaTrofeos(lista) { abierto = it }
        abierto?.let { a -> DetalleTrofeo(a, l) { abierto = null } }
        if (filtro != "Tuyos") TextButton(onClick = { filtro = "Tuyos"; crear = true }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
            Text("+ inventar un logro propio", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun FilaLogro(g: Logros.Logro) {
    val secreto = g.secreto && !g.hecho
    var abierto by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().clickable { abierto = !abierto }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        MedallaLogro(g, 52.dp)
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(if (secreto) "Logro secreto" else g.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (secreto) "Sigue viviendo y escribiendo: aparece cuando lo logras." else g.descripcion, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = if (abierto) 4 else 2, overflow = TextOverflow.Ellipsis)
            when {
                g.hecho -> Text("✓ " + Fechas.titulo(g.conseguido!!).lowercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                !secreto -> {
                    Progreso(progress = { g.fraccion }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(5.dp))
                    Text(cuanto(g.progreso) + " / " + cuanto(g.meta) + if (g.unidad.isNotEmpty()) " ${g.unidad}" else "", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            var confirmar by remember(g.id) { mutableStateOf(false) }
            if (abierto && g.propio) TextButton(onClick = {
                if (!confirmar) { confirmar = true; return@TextButton }
                val previos = Repo.config.logros_propios.filter { "p_" + it.id == g.id }
                Repo.cambiarConfig { c -> c.copy(logros_propios = c.logros_propios.filterNot { "p_" + it.id == g.id }) }
                io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Logro borrado") { Repo.cambiarConfig { c -> c.copy(logros_propios = c.logros_propios + previos.filter { p -> c.logros_propios.none { it.id == p.id } }) } }
            }) { Text(if (confirmar) "¿borrar de verdad?" else "borrar este logro", style = MaterialTheme.typography.labelMedium) }
        }
    }
}

private fun cuanto(x: Double): String = when {
    x >= 1e6 -> "%.1f M".format(x / 1e6)
    x >= 10_000 -> "%,d".format(x.toLong())
    x == Math.floor(x) -> "%,d".format(x.toLong())
    else -> "%.1f".format(x)
}

private val ICONOS = listOf("★", "✎", "❖", "☾", "▣", "❦", "♥", "⚑", "✓", "☀", "⚡", "♫", "✈", "☕", "✿", "◆")

/** Inventar un logro: de qué (una variable de tu día, un hábito o una @persona/#tema), cómo se cuenta y la meta. */
@Composable
private fun EditorLogro(cerrar: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var fuente by remember { mutableStateOf("habito") }
    var clave by remember { mutableStateOf("") }
    var modo by remember { mutableStateOf("dias") }
    var meta by remember { mutableStateOf("20") }
    var ventana by remember { mutableStateOf("30") }
    var umbral by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("★") }
    var buscar by remember { mutableStateOf("") }
    val variables = remember { runCatching { Estadistica.variables(dinamicas = false) }.getOrDefault(emptyList()) }
    var version by remember { mutableIntStateOf(0) }
    Tarjeta(Modifier.padding(top = 10.dp)) {
        Text("Nuevo logro", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(nombre, { nombre = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("Nombre") })
        Etiqueta("Qué cuenta")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("habito" to "Un hábito", "variable" to "Algo de tu día", "etiqueta" to "@persona o #tema").forEach { (id, n) -> Ficha(n, fuente == id) { fuente = id; clave = "" } }
        }
        when (fuente) {
            "habito" -> FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val hs = io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().filter { it.activo }
                if (hs.isEmpty()) Ayuda("Primero crea un hábito en Más → Hábitos.")
                hs.forEach { h -> Ficha(h.nombre, clave == h.id) { clave = h.id; if (nombre.isBlank()) nombre = h.nombre } }
            }
            "etiqueta" -> OutlinedTextField(clave, { clave = it.trim() }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("@abuela o #guitarra") })
            else -> {
                OutlinedTextField(buscar, { buscar = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("buscar: pasos, sueño, palabras…") })
                val q = buscar.trim().lowercase()
                FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    variables.filter { q.isEmpty() || it.nombre.lowercase().contains(q) || it.id.contains(q) }.take(14).forEach { v ->
                        Ficha(v.nombre, clave == v.id) { clave = v.id; if (nombre.isBlank()) nombre = v.nombre }
                    }
                }
                OutlinedTextField(umbral, { umbral = it.filter { ch -> ch.isDigit() || ch == '.' } }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true,
                    label = { Text("Un día cuenta si pasa de… (opcional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }
        }
        Etiqueta("Cómo se cuenta")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("dias" to "Días", "racha" to "Seguidos", "total" to "Suma total").forEach { (id, n) -> Ficha(n, modo == id) { modo = id } }
        }
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(meta, { meta = it.filter { ch -> ch.isDigit() || ch == '.' } }, Modifier.weight(1f), singleLine = true,
                label = { Text(if (modo == "total") "Meta (suma)" else "Días") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            if (modo == "dias") OutlinedTextField(ventana, { ventana = it.filter { ch -> ch.isDigit() } }, Modifier.weight(1f), singleLine = true,
                label = { Text("de cada (0 = desde siempre)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        Ayuda(when (modo) {
            "dias" -> if ((ventana.toIntOrNull() ?: 0) > 0) "Se logra cuando en cualquier tramo de ${ventana.ifBlank { "?" }} días lo hiciste ${meta.ifBlank { "?" }} veces: no necesitas racha." else "Se logra al juntar ${meta.ifBlank { "?" }} días con eso."
            "racha" -> "Se logra con ${meta.ifBlank { "?" }} días seguidos."
            else -> "Se logra cuando lo acumulado llega a ${meta.ifBlank { "?" }}."
        }, Modifier.padding(top = 4.dp))
        Etiqueta("Signo")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { ICONOS.forEach { i -> Ficha(i, icono == i) { icono = i } } }
        val listo = nombre.isNotBlank() && clave.isNotBlank() && (meta.toDoubleOrNull() ?: 0.0) > 0
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonPrincipal("Guardar", habilitado = listo) {
                val p = LogroPropio(Repo.nuevoId(), nombre.trim(), fuente, clave.trim(), modo, meta.toDoubleOrNull() ?: 1.0,
                    if (modo == "dias") ventana.toIntOrNull() ?: 0 else 0, umbral.toDoubleOrNull() ?: 0.0, Fechas.diaLogico(), icono)
                Repo.cambiarConfig { c -> c.copy(logros_propios = c.logros_propios + p) }
                version++; cerrar()
            }
            TextButton(onClick = cerrar) { Text("cancelar", style = MaterialTheme.typography.labelLarge) }
        }
    }
}
