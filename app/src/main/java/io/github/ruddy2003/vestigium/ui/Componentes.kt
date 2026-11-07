@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import io.github.ruddy2003.vestigium.data.privado
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Opcion
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Esquinas según el tema (rectas en Terminal, suaves en helénico). */
private val R4 @Composable get() = MaterialTheme.shapes.small

/**
 * Banda ornamental bajo cada título, una por tema: ovas y dardos (romano), greca enmarcada (helénico), pista de
 * circuito (terminal), tubo de neón con sus grapas (ciudad neón), expediente con su ojo (distopía), regla con anillo de benceno (laboratorio), filete de gaceta
 * (colonos), sarta de monedas con nudo y borla (seda), meandro de martillos entre costuras de fieltro (mongol)…
 */
@Composable
fun Meandro(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)) {
    when (Estilo.tema) {
        TemaId.CIBER -> Circuito(modifier)
        TemaId.NEON -> TuboNeon(modifier)
        TemaId.HELENO -> Greca(modifier, color)
        TemaId.EGIPTO -> BandaJeker(modifier)
        TemaId.MEDIEVAL -> Zarcillo(modifier)
        TemaId.TEMPLARIO -> Adarve(modifier)
        TemaId.DISTOPIA -> BandaExpediente(modifier)
        TemaId.REFUGIO -> Destellos(modifier)
        TemaId.ORDEN -> Galones(modifier)
        TemaId.VAPOR -> BandaVia(modifier)
        TemaId.LABORATORIO -> BandaProbeta(modifier)
        TemaId.COLONOS -> BandaGaceta(modifier)
        TemaId.NAPOLEON -> Laureles(modifier)
        TemaId.DECO -> Abanicos(modifier)
        TemaId.BOTANICO -> Enredadera(modifier)
        TemaId.RUINAS -> BandaCalle(modifier)
        TemaId.GOTICO -> Arqueria(modifier)
        TemaId.ARCANO -> BandaMarea(modifier)
        TemaId.ESPACIO -> Orbita(modifier)
        TemaId.ABISMO -> TrazaSonar(modifier)
        TemaId.ANDALUS -> Lazo(modifier)
        TemaId.MESO -> BandaGreca(modifier)
        TemaId.MONGOL -> BandaEstepa(modifier)
        TemaId.RENACIMIENTO -> Trazo(modifier)
        TemaId.PIRATA -> Cabo(modifier)
        TemaId.NAVIDAD -> Guirnalda(modifier)
        TemaId.SEDA -> BandaCaravana(modifier)
        TemaId.EDO -> BandaSeigaiha(modifier)
        TemaId.URSS -> Barra(modifier)
        TemaId.REVOLUCION -> Tricolor(modifier)
        TemaId.ANTIGUAS -> Runica(modifier)
        TemaId.CUEVA -> BandaCueva(modifier)
        TemaId.ROMANO -> BandaOvas(modifier, color)
        TemaId.URBE -> BandaSampietrini(modifier, color)
    }
}

/** Ovas y dardos, la moldura de las cornisas romanas: huevos en su cáscara y una punta de flecha entre cada dos, entre filetes. */
@Composable
private fun BandaOvas(modifier: Modifier, color: Color) {
    Canvas(modifier.fillMaxWidth().height(13.dp)) {
        val h = size.height; val f = 1.dp.toPx()
        drawLine(color, Offset(0f, 0.6f * f), Offset(size.width, 0.6f * f), 1.1f * f)
        drawLine(color, Offset(0f, h - 0.6f * f), Offset(size.width, h - 0.6f * f), 1.1f * f)
        val paso = h * 1.15f
        var x = (size.width % paso) / 2
        while (x + paso <= size.width + 0.5f) {
            val cx = x + paso / 2
            // La cáscara: una U abierta hacia arriba alrededor del huevo.
            val cascara = Path().apply {
                moveTo(cx - paso * 0.42f, h * 0.18f)
                quadraticTo(cx - paso * 0.42f, h * 0.9f, cx, h * 0.88f)
                quadraticTo(cx + paso * 0.42f, h * 0.9f, cx + paso * 0.42f, h * 0.18f)
            }
            drawPath(cascara, color, style = Stroke(f))
            drawOval(color, Offset(cx - paso * 0.23f, h * 0.22f), Size(paso * 0.46f, h * 0.56f))
            // El dardo, entre esta ova y la anterior: asta y punta hacia abajo.
            if (x > f) {
                drawLine(color, Offset(x, h * 0.12f), Offset(x, h * 0.6f), 0.9f * f)
                drawPath(Path().apply { moveTo(x - paso * 0.1f, h * 0.48f); lineTo(x, h * 0.84f); lineTo(x + paso * 0.1f, h * 0.48f); close() }, color)
            }
            x += paso
        }
    }
}

/**
 * Sampietrini: dos hileras de adoquines de basalto puestos a tresbolillo, cada uno un poco distinto (la piedra se gasta
 * desigual), con la junta de arena entre ellos y el bordillo de travertino arriba.
 */
@Composable
private fun BandaSampietrini(modifier: Modifier, color: Color) {
    Canvas(modifier.fillMaxWidth().height(13.dp)) {
        val h = size.height; val f = 1.dp.toPx()
        drawLine(color, Offset(0f, 0.6f * f), Offset(size.width, 0.6f * f), 1.4f * f)
        val lado = (h - 3f * f) / 2f
        val paso = lado + 1.2f * f
        for (fila in 0 until 2) {
            var x = if (fila == 0) 0f else -paso / 2
            var i = 0
            val y = 2.2f * f + fila * (lado + 0.8f * f)
            while (x < size.width) {
                val azar = ((i * 7919 + fila * 104729) % 97) / 97f
                val enc = lado * (0.86f + 0.14f * azar)
                drawRoundRect(color.copy(alpha = 0.45f + 0.4f * azar), Offset(x + (lado - enc) / 2, y + (lado - enc) / 2), Size(enc, enc), CornerRadius(enc * 0.28f))
                x += maxOf(paso, 1f); i++
            }
        }
    }
}

/** Expediente: barra gruesa con un tramo rojo donde mira el ojo, línea fina y el código de barras del folio. */
@Composable
private fun BandaExpediente(modifier: Modifier) {
    val tinta = MaterialTheme.colorScheme.onBackground; val rojo = MaterialTheme.colorScheme.primary; val papel = MaterialTheme.colorScheme.background
    Canvas(modifier.fillMaxWidth().height(13.dp)) {
        val w = size.width; val g = 5.dp.toPx(); val f = 1.dp.toPx()
        drawRect(tinta, Offset.Zero, Size(w, g))
        drawRect(rojo, Offset.Zero, Size(w * 0.22f, g))
        // El ojo que lo mira todo, en blanco sobre el tramo rojo.
        val c = Offset(w * 0.11f, g / 2)
        val ojo = Path().apply { moveTo(c.x - g * 1.2f, c.y); quadraticTo(c.x, c.y - g * 0.95f, c.x + g * 1.2f, c.y); quadraticTo(c.x, c.y + g * 0.95f, c.x - g * 1.2f, c.y); close() }
        drawPath(ojo, papel)
        drawCircle(rojo, g * 0.3f, c)
        drawRect(tinta, Offset(0f, g + 2.5f * f), Size(w * 0.68f, f))
        // Código de barras del folio, al final.
        var x = w * 0.72f; var k = 0
        while (x < w) {
            val a = if (k % 3 == 0) 2f * f else f
            drawRect(tinta, Offset(x, g + 1.5f * f), Size(a, size.height - g - 1.5f * f))
            x += a + (if (k % 2 == 0) 1.5f * f else 2.5f * f)
            k++
        }
    }
}

/** Regla graduada con su cursor de lectura y, al final, el anillo de benceno. */
@Composable
private fun BandaProbeta(modifier: Modifier) {
    val tinta = MaterialTheme.colorScheme.onSurfaceVariant; val cian = MaterialTheme.colorScheme.primary; val violeta = MaterialTheme.colorScheme.secondary
    Canvas(modifier.fillMaxWidth().height(13.dp)) {
        val h = size.height; val b = h - 1f; val fin = size.width - h * 1.9f; val f = 1.dp.toPx()
        drawLine(tinta.copy(alpha = 0.6f), Offset(0f, b), Offset(fin, b), f)
        val paso = 6.dp.toPx(); var i = 0; var x = 0f
        while (x <= fin) {
            val alto = if (i % 10 == 0) h * 0.8f else if (i % 5 == 0) h * 0.5f else h * 0.25f
            drawLine(tinta.copy(alpha = 0.5f), Offset(x, b), Offset(x, b - alto), f)
            x += paso; i++
        }
        // Cursor de lectura: una flecha que baja hasta la marca.
        val cx = size.width * 0.3f
        drawRect(cian, Offset(cx - f, h * 0.3f), Size(2f * f, h * 0.7f))
        drawPath(Path().apply { moveTo(cx - h * 0.28f, 0f); lineTo(cx + h * 0.28f, 0f); lineTo(cx, h * 0.34f); close() }, cian)
        // Anillo de benceno: hexágono con su círculo.
        val c = Offset(size.width - h * 0.9f, h / 2); val r = h * 0.46f
        val hexagono = Path().apply {
            for (k in 0 until 6) {
                val a = Math.toRadians(30.0 + 60.0 * k)
                val px = c.x + r * cos(a).toFloat(); val py = c.y + r * sin(a).toFloat()
                if (k == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        drawPath(hexagono, violeta, style = Stroke(1.2f * f))
        drawCircle(violeta, r * 0.5f, c, style = Stroke(f))
    }
}

/** Filete de gaceta: raya inglesa (gruesa y fina) a cada lado y, al centro, un rombo entre dos hojas de imprenta. */
@Composable
private fun BandaGaceta(modifier: Modifier) {
    val tinta = MaterialTheme.colorScheme.primary; val rojo = MaterialTheme.colorScheme.secondary
    Canvas(modifier.fillMaxWidth().height(14.dp)) {
        val y = size.height / 2; val cx = size.width / 2; val s = size.height * 0.34f; val f = 1.dp.toPx()
        val hueco = s * 5.2f
        listOf(0f to cx - hueco, cx + hueco to size.width).forEach { (a, b) ->
            drawRect(tinta, Offset(a, y - 2.6f * f), Size(b - a, 1.8f * f))
            drawRect(tinta, Offset(a, y + 1.4f * f), Size(b - a, 0.7f * f))
        }
        drawPath(Path().apply { moveTo(cx, y - s); lineTo(cx + s * 0.8f, y); lineTo(cx, y + s); lineTo(cx - s * 0.8f, y); close() }, rojo)
        // Hojas de hiedra que miran hacia afuera.
        listOf(1f, -1f).forEach { d ->
            val px = cx + d * s * 1.3f
            drawPath(Path().apply { moveTo(px, y); quadraticTo(px + d * s * 0.6f, y - s * 1.2f, px + d * s * 1.7f, y); quadraticTo(px + d * s * 0.6f, y + s * 1.2f, px, y); close() }, tinta)
            drawCircle(tinta, 1.4f * f, Offset(cx + d * s * 4f, y))
        }
    }
}

/** Cordón de seda con sartas de monedas y, entre ellas, un nudo chino con su borla. */
@Composable
private fun BandaCaravana(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val oro = if (esOscuro()) e.primary else e.tertiary
    Canvas(modifier.fillMaxWidth().height(17.dp)) {
        val h = size.height; val y = h * 0.36f; val f = 1.dp.toPx()
        drawLine(e.secondary, Offset(0f, y), Offset(size.width, y), 1.6f * f)
        val r = h * 0.26f; var x = 26.dp.toPx(); var k = 0
        while (x < size.width - r * 4) {
            if (k % 2 == 0) {
                for (i in 0 until 3) {
                    val cx = x + i * r * 1.5f
                    drawCircle(oro, r, Offset(cx, y))
                    drawRect(e.background, Offset(cx - r * 0.3f, y - r * 0.3f), Size(r * 0.6f, r * 0.6f))
                }
            } else {
                // Nudo: un rombo de cordón con un lazo a cada lado y la borla colgando.
                val s = r * 1.1f
                drawPath(Path().apply { moveTo(x, y - s); lineTo(x + s, y); lineTo(x, y + s); lineTo(x - s, y); close() }, e.primary, style = Stroke(1.4f * f))
                drawCircle(e.primary, r * 0.42f, Offset(x - s * 1.35f, y), style = Stroke(1.1f * f))
                drawCircle(e.primary, r * 0.42f, Offset(x + s * 1.35f, y), style = Stroke(1.1f * f))
                drawLine(e.primary, Offset(x, y + s), Offset(x, h - r * 1.1f), 1.1f * f)
                drawPath(Path().apply { moveTo(x - r * 0.25f, h - r * 1.1f); lineTo(x + r * 0.25f, h - r * 1.1f); lineTo(x + r * 0.5f, h); lineTo(x - r * 0.5f, h); close() }, e.primary)
            }
            x += 48.dp.toPx(); k++
        }
    }
}

/** Una franja de seigaiha (olas en escamas, dos hileras) cortada al centro por el sello de bermellón. */
@Composable
private fun BandaSeigaiha(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(17.dp)) {
        val f = 1.dp.toPx(); val r = 5.5f * f; val base = size.height - 1.5f * f; val cx = size.width / 2
        val ola = e.primary.copy(alpha = 0.55f)
        for (fila in 1 downTo 0) {
            val yb = base - fila * r * 0.55f; var x = if (fila == 1) r else 0f
            while (x <= size.width + r) {
                if (kotlin.math.abs(x - cx) > r * 2.6f) {
                    drawArc(e.background, 180f, 180f, true, Offset(x - r, yb - r), Size(2 * r, 2 * r))
                    for (k in 0..2) { val q = r * (1f - k * 0.32f); drawArc(ola, 180f, 180f, false, Offset(x - q, yb - q), Size(2 * q, 2 * q), style = Stroke(0.9f * f)) }
                }
                x += maxOf(2 * r, 1f)
            }
        }
        val s = 11 * f; val o = Offset(cx - s / 2, base - s - 1 * f)
        drawRect(e.secondary, o, Size(s, s))
        drawRect(e.background.copy(alpha = 0.85f), o + Offset(1.6f * f, 1.6f * f), Size(s - 3.2f * f, s - 3.2f * f), style = Stroke(0.8f * f))
        drawLine(e.background.copy(alpha = 0.85f), Offset(cx, o.y + 3 * f), Offset(cx, o.y + s - 3 * f), 1.1f * f)
    }
}

/** El meandro de martillos entre dos costuras de fieltro y, al centro, el sol sobre la luna. */
@Composable
private fun BandaEstepa(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val h = size.height; val w = size.width; val f = 1.dp.toPx()
        val costura = PathEffect.dashPathEffect(floatArrayOf(4f * f, 3f * f))
        drawLine(e.secondary, Offset(0f, f), Offset(w, f), f, pathEffect = costura)
        drawLine(e.secondary, Offset(0f, h - f), Offset(w, h - f), f, pathEffect = costura)
        val arriba = h * 0.22f; val abajo = h * 0.78f; val u = (abajo - arriba) / 2; val paso = u * 4
        val cx = w / 2; val hueco = h * 1.1f
        fun martillos(desde: Float, hasta: Float) {
            val p = Path(); var x = desde
            p.moveTo(x, h / 2)
            while (x + paso <= hasta) {
                p.lineTo(x + u, h / 2); p.lineTo(x + u, arriba); p.lineTo(x + u * 3, arriba); p.lineTo(x + u * 3, h / 2)
                p.lineTo(x + u * 2, h / 2); p.lineTo(x + u * 2, abajo); p.lineTo(x + u * 4, abajo); p.lineTo(x + u * 4, h / 2)
                x += paso
            }
            drawPath(p, e.primary, style = Stroke(1.5f * f))
        }
        martillos(0f, cx - hueco)
        martillos(cx + hueco, w)
        drawCircle(e.tertiary, h * 0.15f, Offset(cx, h * 0.3f))
        drawArc(e.tertiary, 20f, 140f, false, Offset(cx - h * 0.3f, h * 0.28f), Size(h * 0.6f, h * 0.5f), style = Stroke(2f * f))
    }
}

/** Greca griega entre dos filetes, como en el borde de un vaso ático. */
@Composable
private fun Greca(modifier: Modifier, color: Color) {
    Canvas(modifier.fillMaxWidth().height(15.dp)) {
        val m = size.height * 0.22f
        val h = size.height - 2 * m
        val u = h / 3f
        val paso = u * 4
        drawLine(color, Offset(0f, 0.6f), Offset(size.width, 0.6f), 1.1f)
        drawLine(color, Offset(0f, size.height - 0.6f), Offset(size.width, size.height - 0.6f), 1.1f)
        val p = Path()
        var x = (size.width % paso) / 2
        val b = m + h
        p.moveTo(0f, b); p.lineTo(x, b)
        while (x + paso <= size.width) {
            p.lineTo(x, m); p.lineTo(x + u * 3, m); p.lineTo(x + u * 3, m + u * 2); p.lineTo(x + u, m + u * 2); p.lineTo(x + u, m + u); p.lineTo(x + u * 2, m + u)
            p.moveTo(x, b); p.lineTo(x + paso, b)
            x += paso
        }
        p.lineTo(size.width, b)
        drawPath(p, color, style = Stroke(width = 1.5f, cap = StrokeCap.Square, join = StrokeJoin.Miter))
    }
}

/**
 * Friso jeker, el remate de los muros de las tumbas egipcias: haces de juncos atados, con su copete redondo, alternando
 * lapislázuli y cornalina, sobre la cenefa de rectángulos de colores que enmarca las pinturas.
 */
@Composable
private fun BandaJeker(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(16.dp)) {
        val f = 1.dp.toPx(); val h = size.height
        val paso = 7f * f; val n = (size.width / paso).toInt(); val x0 = (size.width - n * paso) / 2
        val alto = h * 0.64f
        for (i in 0 until n) {
            val cx = x0 + (i + 0.5f) * paso
            val col = (if (i % 2 == 0) e.primary else e.secondary).copy(alpha = 0.72f)
            drawRect(col, Offset(cx - 1.3f * f, h * 0.3f), Size(2.6f * f, alto - h * 0.3f))
            drawCircle(col, 2.1f * f, Offset(cx, h * 0.2f))
            drawLine(e.tertiary, Offset(cx - 1.9f * f, h * 0.42f), Offset(cx + 1.9f * f, h * 0.42f), 0.9f * f)
        }
        val y = alto + 1.6f * f
        val colores = listOf(e.primary, e.tertiary, e.secondary, e.tertiary)
        var x = 0f; var k = 0
        while (x < size.width) {
            drawRect(colores[k % 4].copy(alpha = 0.7f), Offset(x, y), Size(minOf(5f * f, size.width - x), h - y - 0.5f * f))
            x += 6f * f; k++
        }
    }
}

/** Pista de circuito en una sola tinta: línea fina con nodos y un tramo encendido al máximo brillo. */
@Composable
private fun Circuito(modifier: Modifier) {
    val cian = MaterialTheme.colorScheme.primary
    val vivo = MaterialTheme.colorScheme.tertiary
    Canvas(modifier.fillMaxWidth().height(10.dp)) {
        val y = size.height / 2
        drawLine(cian.copy(alpha = 0.35f), Offset(0f, y), Offset(size.width, y), 1.5f)
        drawLine(vivo, Offset(0f, y), Offset(size.width * 0.16f, y), 2.5f)
        drawLine(cian, Offset(size.width * 0.16f, y), Offset(size.width * 0.22f, y), 2.5f)
        val paso = 46.dp.toPx()
        var x = size.width * 0.28f
        var k = 0
        while (x < size.width) {
            val lado = if (k % 3 == 0) 6f else 3.5f
            drawRect(cian.copy(alpha = if (k % 3 == 0) 0.9f else 0.5f), Offset(x - lado / 2, y - lado / 2), androidx.compose.ui.geometry.Size(lado, lado))
            if (k % 4 == 1) drawLine(cian.copy(alpha = 0.4f), Offset(x, y), Offset(x + 10f, y - size.height / 2), 1.2f)
            x += paso; k++
        }
    }
}

/**
 * Tubo de neón: el tubo magenta con su halo, cortado donde se dobla hacia atrás, sujeto con grapas a la fachada, y un
 * tramo ámbar que parpadea apagado (más tenue), como un letrero viejo.
 */
@Composable
private fun TuboNeon(modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    Canvas(modifier.fillMaxWidth().height(12.dp)) {
        val y = size.height / 2; val w = size.width
        val tramos = listOf(0f to 0.34f, 0.37f to 0.72f, 0.75f to 1f)
        tramos.forEachIndexed { i, (a, b) ->
            val col = if (i == 1) e.secondary.copy(alpha = 0.55f) else e.primary
            drawLine(col.copy(alpha = 0.22f), Offset(w * a, y), Offset(w * b, y), 7.dp.toPx(), cap = StrokeCap.Round)
            drawLine(col, Offset(w * a, y), Offset(w * b, y), 2.dp.toPx(), cap = StrokeCap.Round)
        }
        var x = 30.dp.toPx()
        while (x < w) { drawRect(e.outline, Offset(x - 1.5f, y - 4.dp.toPx()), androidx.compose.ui.geometry.Size(3f, 8.dp.toPx())); x += 58.dp.toPx() }
    }
}

/** El sol de Vergina: la estrella de dieciséis rayos de la casa real macedonia, la de Alejandro. */
@Composable
fun SolVergina(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier.size(30.dp)) { sol(Offset(size.width / 2, size.height / 2), size.minDimension / 2, color) }
}

/** Encabezado de sección: numeral y nombre según el tema, con el nombre en español como comentario. */
@Composable
fun Seccion(titulo: String, ayuda: String? = null, numero: Int? = null, latin: String? = Romano.latin(titulo), modifier: Modifier = Modifier,
            plegable: Boolean = false, plegadaPorDefecto: Boolean = false, autoPlegar: Boolean = false, abrir: Int = 0, resumen: String? = null,
            content: @Composable () -> Unit) {
    val (nombre, comentario) = Estilo.seccion(titulo, latin)
    // Las secciones plegables recuerdan si las cerraste (se guarda en tu configuración). Las que empiezan plegadas
    // guardan «+título» cuando las abres. Las de [autoPlegar] se vuelven a plegar al cerrar la app o al cambiar de día.
    val plegadas = io.github.ruddy2003.vestigium.data.Repo.config.plegadas
    val claveAuto = "${SesionApp.n}|${io.github.ruddy2003.vestigium.data.Fechas.diaLogico()}"
    var abiertaAuto by androidx.compose.runtime.remember(claveAuto) { mutableStateOf(false) }
    val plegable = plegable || autoPlegar
    val plegada = if (autoPlegar) !abiertaAuto else plegable && if (plegadaPorDefecto) "+$titulo" !in plegadas else titulo in plegadas
    // [abrir] sube cuando otra parte de la pantalla quiere mostrar lo de adentro (por ejemplo, al tocar una cifra del Tablero).
    androidx.compose.runtime.LaunchedEffect(abrir) {
        if (abrir == 0 || !plegada) return@LaunchedEffect
        if (autoPlegar) abiertaAuto = true
        else io.github.ruddy2003.vestigium.data.Repo.cambiarConfig { c -> c.copy(plegadas = if (plegadaPorDefecto) c.plegadas + "+$titulo" else c.plegadas - titulo) }
    }
    val tema = Estilo.tema
    val piel = pielDe(tema)
    Column(modifier.fillMaxWidth().padding(top = 26.dp).entrada(numero ?: 0)) {
        Row(
            verticalAlignment = if (piel.centrar) Alignment.CenterVertically else Alignment.Bottom,
            modifier = if (!plegable) Modifier else if (autoPlegar) Modifier.fillMaxWidth().clickable { abiertaAuto = !abiertaAuto } else Modifier.fillMaxWidth().clickable {
                io.github.ruddy2003.vestigium.data.Repo.cambiarConfig { c ->
                    val clave = if (plegadaPorDefecto) "+$titulo" else titulo
                    val quitar = if (plegadaPorDefecto) !plegada else plegada
                    c.copy(plegadas = if (quitar) c.plegadas - clave else c.plegadas + clave)
                }
            },
        ) {
            if (numero != null) {
                Text(Estilo.numeral(numero), style = MaterialTheme.typography.titleLarge, color = piel.numeral.color())
                Text(piel.sep, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.outline)
            }
            // Los rótulos en bloque (expediente, letrero, cartel) no se estiran a todo el ancho.
            val peso = if (plegable) Modifier.weight(1f, fill = piel.rellena) else Modifier
            TituloSeccion(nombre, tema, peso)
            if (plegable && !piel.rellena) Spacer(Modifier.weight(1f))
            if (plegable) Text(if (plegada) piel.cerrada else piel.abierta, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (comentario != null) Text(comentario, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 1.dp))
        // Plegada, una línea dice qué hay adentro (por ejemplo, «5 registros con hora»).
        if (plegada && resumen != null) Text(resumen, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
        androidx.compose.animation.AnimatedVisibility(!plegada) {
            Column {
                if (ayuda != null) Text(ayuda, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
                else Spacer(Modifier.height(10.dp))
                content()
            }
        }
    }
}

/** Recuadro con las esquinas del tema: cola de milano romana, greca, remaches, clip de expediente, sello de cinabrio… */
@Composable
fun Tarjeta(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    Surface(modifier = modifier.fillMaxWidth().drawWithContent { drawContent(); esquinasComp(tema, e, 1f) }, shape = R4, color = e.surface, border = BorderStroke(1.dp, e.outline)) {
        Column(Modifier.padding(14.dp)) { content() }
    }
}

/** Etiqueta de campo: el prefijo del tema y el nombre con la caja, el espaciado y el remate del tema. */
@Composable
fun Etiqueta(texto: String) {
    val piel = pielDe(Estilo.tema)
    val cuerpo = when (piel.caja) {
        CajaEtiqueta.MINUSCULAS -> texto.lowercase()
        CajaEtiqueta.MAYUSCULAS -> texto.uppercase()
        CajaEtiqueta.CAPITAL -> texto.lowercase().replaceFirstChar { it.uppercase() }
    }
    Row(Modifier.padding(top = 14.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(Estilo.prefijo, style = MaterialTheme.typography.titleSmall, color = piel.colorPrefijo.color())
        Text(cuerpo + piel.sufijo, style = MaterialTheme.typography.titleSmall.copy(letterSpacing = piel.espaciado.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


/**
 * Antes de las 5:00 dice, en palabras, a qué día va lo que anotas: el día lógico empieza a las 5:00 (N-10).
 * Fuera de la madrugada no ocupa nada.
 */
@Composable
fun AvisoMadrugada(modifier: Modifier = Modifier) {
    val ahora = java.time.LocalTime.now()
    if (ahora.hour >= 5) return
    val dia = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()
    Ayuda("Son las ${"%02d:%02d".format(ahora.hour, ahora.minute)}: lo que anotes va al ${io.github.ruddy2003.vestigium.data.Fechas.titulo(dia).lowercase()} " +
        "(los días empiezan a las 5:00).", modifier.padding(top = 2.dp))
}

@Composable
fun Ayuda(texto: String, modifier: Modifier = Modifier) {
    Text(texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = modifier)
}

/**
 * Línea `clave = valor` en monoespaciada, para datos automáticos. Con [espanol] (el reporte del día), si la clave del tema
 * va en otro alfabeto lleva debajo su español ([Estilo.claveEspanol]).
 */
@Composable
fun Clave(clave: String, valor: String, ancho: Int = 14, espanol: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        // Ancho fijo medido con la letra del tema: así las columnas se alinean aunque no sea monoespaciada.
        val medidor = rememberTextMeasurer()
        val estilo = MaterialTheme.typography.labelLarge
        val anchoDp = with(LocalDensity.current) { medidor.measure("0".repeat(ancho), estilo).size.width.toDp() }
        val es = if (espanol) Estilo.claveEspanol(clave) else null
        if (es == null) Text(Estilo.clave(clave), style = estilo, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(anchoDp), maxLines = 1)
        else Column(Modifier.width(anchoDp)) {
            Text(Estilo.clave(clave), style = estilo, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Text(clave.takeWhile { it == ' ' } + es, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, maxLines = 1)
        }
        Text(pielDe(Estilo.tema).igual, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(valor, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
    }
}

@Composable
fun Escala(valor: Int?, desde: Int, hasta: Int, anclas: Map<Int, String>? = null, secuencial: Boolean = false, onCambio: (Int?) -> Unit) {
    val sentir = rememberSentir()
    val oscuro = esOscuro()
    val n = hasta - desde + 1
    // Las escalas desde 0 son de intensidad (dolor, impulso), igual que las [secuencial] (la activación: más energía no es
    // «mejor»); las demás van de desagradable a agradable.
    fun tono(i: Int): Color = if (desde == 0 || secuencial) Tonos.secuencial((i - desde).toFloat() / (n - 1), oscuro) else Tonos.divergente((i - desde).toFloat() / (n - 1), oscuro)
    val actual by androidx.compose.runtime.rememberUpdatedState(valor)
    val tema = Estilo.tema
    val piel = pielDe(tema)
    val fondo = MaterialTheme.colorScheme.surface
    Column {
        // Se puede tocar un número o deslizar el dedo sobre la fila.
        Row(
            Modifier.fillMaxWidth().pointerInput(desde, hasta) {
                detectHorizontalDragGestures { change, _ ->
                    val i = (desde + (change.position.x / size.width * n).toInt()).coerceIn(desde, hasta)
                    if (i != actual) { onCambio(i); sentir(Sentidos.Evento.ELEGIR) }
                    change.consume()
                }
            },
            // Con 10 u 11 casillas (0 a 10) van más juntas y con el número más chico, para que el «10» quepa.
            horizontalArrangement = Arrangement.spacedBy(if (n > 9) 3.dp else 5.dp),
        ) {
            // Si aun así no cabe (letra grande del sistema), todos los números se achican igual en vez de partirse.
            var tam by remember(n) { mutableFloatStateOf(if (n > 9) 15f else 17f) }
            for (i in desde..hasta) {
                val sel = valor == i
                val c = tono(i)
                Box(
                    Modifier.weight(1f).heightIn(min = 52.dp).clip(R4)
                        .background(if (sel) c else MaterialTheme.colorScheme.surface)
                        // La marca del color de cada valor, para leer la escala sin depender solo del número; su forma es del tema.
                        .drawBehind { if (!sel) marcaEscala(tema, c, fondo) }
                        .border(if (sel) 2.dp else 1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, R4)
                        .clickable { onCambio(if (sel) null else i); sentir(Sentidos.Evento.ELEGIR) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$i", style = MaterialTheme.typography.labelLarge.copy(fontSize = tam.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal),
                        color = if (sel) Tonos.sobre(c) else MaterialTheme.colorScheme.onSurface, maxLines = 1, softWrap = false,
                        onTextLayout = { r -> if (r.hasVisualOverflow && tam > 9f) tam = maxOf(9f, tam * 0.9f) })
                }
            }
        }
        if (anclas != null) Row(Modifier.padding(top = 7.dp)) {
            Text(piel.igual + (valor?.toString() ?: piel.nulo), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(if (valor == null) "  " + Estilo.texto("escala_vacia") else "  ${anclas[valor] ?: ""}".lowercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Ficha que se elige: la forma de los botones del tema y, elegida, la perilla del tema delante (sello, gota, moneda, gema…). */
@Composable
fun Ficha(texto: String, activa: Boolean, onClick: () -> Unit) {
    val sentir = rememberSentir()
    val toque = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    Surface(
        onClick = { sentir(Sentidos.Evento.ELEGIR); onClick() }, shape = formaBoton(tema), interactionSource = toque, modifier = Modifier.presion(toque, 0.93f),
        color = if (activa) e.primaryContainer else e.surface,
        border = BorderStroke(1.dp, if (activa) e.primary else e.outline),
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            if (activa) {
                Canvas(Modifier.size(11.dp)) { perillaTema(tema, e, center, size.minDimension / 2f, e.primary, e.primaryContainer) }
                Spacer(Modifier.width(7.dp))
            }
            Text(texto, style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal))
        }
    }
}

@Composable
fun FilaFichas(opciones: List<Opcion>, valor: String?, onCambio: (String?) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        opciones.forEach { o -> Ficha(o.nombre, valor == o.id) { onCambio(if (valor == o.id) null else o.id) } }
    }
}

@Composable
fun Opciones(opciones: List<Opcion>, valor: String?, onCambio: (String?) -> Unit) {
    val piel = pielDe(Estilo.tema)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp), maxItemsInEachRow = 2) {
        opciones.forEach { o ->
            val sel = valor == o.id
            Surface(
                onClick = { onCambio(if (sel) null else o.id) }, modifier = Modifier.weight(1f), shape = R4,
                color = if (sel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
            ) {
                Column(Modifier.padding(10.dp)) {
                    Text(if (sel) "${piel.elegido} ${o.nombre}" else o.nombre, style = MaterialTheme.typography.titleMedium, color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    if (o.detalle.isNotEmpty()) Text(o.detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

/**
 * Fila con texto y el conmutador del tema (forma de los botones, perilla del tema y, encendido, el glifo de «elegido»
 * en el riel). Toda la fila se toca; para TalkBack es un interruptor.
 */
@Composable
fun Interruptor(texto: String, valor: Boolean, detalle: String? = null, onCambio: (Boolean) -> Unit) {
    val sentir = rememberSentir()
    Row(
        Modifier.fillMaxWidth().toggleable(value = valor, role = Role.Switch) { sentir(Sentidos.Evento.ELEGIR); onCambio(it) }.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(texto, style = MaterialTheme.typography.bodyLarge)
            if (detalle != null) Ayuda(detalle)
        }
        Conmutador(checked = valor, onCheckedChange = null)
    }
}

@Composable
fun ListaEtiquetas(lista: List<String>, pista: String, prefijo: String = "", normalizar: (String) -> String, onCambio: (List<String>) -> Unit) {
    var texto by remember { mutableStateOf("") }
    fun agregar() {
        // Sin repetir: lo que ya está en la lista (o se escribió dos veces) no se agrega otra vez.
        val nuevos = texto.split(",").map(normalizar).filter { it.isNotEmpty() && it !in lista }.distinct()
        if (nuevos.isNotEmpty()) onCambio(lista + nuevos)
        texto = ""
    }
    val tema = Estilo.tema
    val piel = pielDe(tema)
    val forma = formaBoton(tema)
    Column {
        if (lista.isEmpty()) Text(piel.listaVacia, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            lista.forEachIndexed { i, t ->
                Surface(shape = forma, color = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 12.dp)) {
                        Text(prefijo + t, style = MaterialTheme.typography.labelLarge)
                        BotonTexto(onClick = { onCambio(lista.filterIndexed { j, _ -> j != i }) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(36.dp)) { Text(piel.quitar, fontSize = 16.sp) }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            CampoTexto(
                value = texto, onValueChange = { texto = it }, modifier = Modifier.weight(1f), singleLine = true,
                placeholder = { Text(pista, style = MaterialTheme.typography.labelLarge) }, textStyle = MaterialTheme.typography.labelLarge,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { agregar() }),
            )
            Spacer(Modifier.width(8.dp))
            BotonSecundario(Estilo.texto("lista_anadir")) { agregar() }
        }
    }
}

/**
 * Campo de texto largo con una barra superior que dice dónde se escribe, según el tema: tablilla de cera, consola,
 * papiro, pluma y tintero, formulario, máquina de escribir, cuaderno de campo… Debajo, el margen y, en los temas de
 * cuaderno, renglones.
 */
@Composable
fun Editor(valor: String, archivo: String, pista: String, minimo: Int = 200, pie: String? = null, onCambio: (String) -> Unit) {
    // Se lleva el cursor para saber qué palabra se está escribiendo y sugerir @personas y #temas.
    var campo by remember { mutableStateOf(TextFieldValue(valor, TextRange(valor.length))) }
    if (campo.text != valor) campo = TextFieldValue(valor, TextRange(valor.length))
    val etiquetas = remember(io.github.ruddy2003.vestigium.data.Repo.dias.size, io.github.ruddy2003.vestigium.data.Repo.momentos.size) { Etiquetas.frecuentes() }
    val antes = campo.text.substring(0, campo.selection.start.coerceIn(0, campo.text.length))
    val token = Regex("[@#][\\p{L}\\d_]*$").find(antes)?.value
    val sugerencias = if (token == null) emptyList() else {
        val pre = token.first(); val q = token.drop(1).lowercase()
        (if (pre == '@') etiquetas.first else etiquetas.second).filter { it.startsWith(q) && it != q }.take(6).map { "$pre$it" }
    }
    val tema = Estilo.tema
    val piel = pielDe(tema)
    val e = MaterialTheme.colorScheme
    val renglon = e.outline.copy(alpha = 0.7f)
    Column(Modifier.fillMaxWidth().border(1.dp, e.outline, R4)) {
        Row(Modifier.fillMaxWidth().background(e.surfaceVariant).padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.size(14.dp)) { iconoEditor(tema, e) }
            Text("  " + rotuloArchivo(archivo, tema), style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (pie != null) Text(pie, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(Modifier.height(IntrinsicSize.Min)) {
            Canvas(Modifier.width(5.dp).fillMaxHeight()) { margenEditor(piel.margen, e) }
            androidx.compose.foundation.text.BasicTextField(
                value = campo, onValueChange = { campo = it; if (it.text != valor) onCambio(it.text) },
                modifier = Modifier.weight(1f).heightIn(min = minimo.dp).drawBehind {
                    // Renglones al paso del interlineado (22 sp), bajo cada línea de texto.
                    if (piel.renglones) {
                        val paso = 22.sp.toPx(); val borde = 12.dp.toPx()
                        var y = borde + paso - 3.dp.toPx()
                        while (y < size.height) { drawLine(renglon, Offset(borde, y), Offset(size.width - borde, y), 1.dp.toPx() * 0.7f); y += paso }
                    }
                }.padding(12.dp),
                textStyle = MaterialTheme.typography.labelLarge.copy(fontSize = 14.5.sp, lineHeight = 22.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                visualTransformation = rememberMarcas(),
                decorationBox = { campoTexto ->
                    Box {
                        if (campo.text.isEmpty()) Text(pista, style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        campoTexto()
                    }
                },
            )
        }
        if (sugerencias.isNotEmpty()) FlowRow(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            sugerencias.forEach { s ->
                Ficha(s, false) {
                    val ini = antes.length - token!!.length
                    val nuevo = campo.text.substring(0, ini) + s + " " + campo.text.substring(antes.length)
                    campo = TextFieldValue(nuevo, TextRange(ini + s.length + 1))
                    onCambio(nuevo)
                }
            }
        }
    }
}

/** Personas y temas que ya usaste, de más a menos frecuentes, para sugerirlos al escribir. */
object Etiquetas {
    fun frecuentes(): Pair<List<String>, List<String>> {
        val p = HashMap<String, Int>(); val t = HashMap<String, Int>()
        val textos = io.github.ruddy2003.vestigium.data.Repo.dias.values.map { it.nota } + io.github.ruddy2003.vestigium.data.Repo.momentos.filter { m -> !m.privado }.map { it.texto }
        textos.forEach { x ->
            io.github.ruddy2003.vestigium.data.Texto.personas(x).distinct().forEach { p.merge(it, 1, Int::plus) }
            io.github.ruddy2003.vestigium.data.Texto.temas(x).distinct().forEach { t.merge(it, 1, Int::plus) }
        }
        return p.entries.sortedByDescending { it.value }.map { it.key } to t.entries.sortedByDescending { it.value }.map { it.key }
    }
}

@Composable
fun BotonDictado(onTexto: (String) -> Unit) {
    val ctx = LocalContext.current
    val lanzar = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val t = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!t.isNullOrBlank()) onTexto(t)
    }
    BotonSecundario("● dictar") {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
            .putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicta")
        try { lanzar.launch(i) } catch (e: ActivityNotFoundException) { Toast.makeText(ctx, "Este teléfono no tiene dictado por voz disponible.", Toast.LENGTH_SHORT).show() }
    }
}

/** Botón principal con la forma y el adorno del tema (frontón, arco apuntado, banderola, losa de piedra…). */
@Composable
fun BotonPrincipal(texto: String, modifier: Modifier = Modifier, habilitado: Boolean = true, onClick: () -> Unit) {
    val sentir = rememberSentir()
    val toque = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val tema = Estilo.tema
    val piel = pielDe(tema)
    val e = MaterialTheme.colorScheme
    val tinta = e.onPrimary.copy(alpha = if (habilitado) 0.5f else 0.2f)
    Button(onClick = { sentir(Sentidos.Evento.GUARDAR); onClick() }, enabled = habilitado,
        modifier = modifier.heightIn(min = 48.dp).presion(toque).drawWithContent { drawContent(); adornoBoton(tema, tinta, e) }, shape = formaBoton(tema),
        interactionSource = toque, colors = ButtonDefaults.buttonColors(containerColor = e.primary)) {
        val base = MaterialTheme.typography.titleLarge
        // La letra de Leonardo es caligráfica y muy fina: en botones va la Cormorant, que se lee mejor chica.
        val estilo = piel.letraBoton?.let { base.copy(fontFamily = it, fontWeight = FontWeight.SemiBold, fontSize = piel.tamBoton.sp, letterSpacing = 0.5.sp) }
            ?: base.copy(fontSize = piel.tamBoton.sp, letterSpacing = if (Estilo.mayusculas) 2.sp else 0.5.sp)
        Text(if (Estilo.mayusculas) texto.uppercase() else texto, style = estilo)
    }
}

/** Botón secundario: la misma forma que el principal, en línea, con el grosor y el color de borde del tema. */
@Composable
fun BotonSecundario(texto: String, modifier: Modifier = Modifier, habilitado: Boolean = true, onClick: () -> Unit) {
    val sentir = rememberSentir()
    val toque = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val tema = Estilo.tema
    val piel = pielDe(tema)
    OutlinedButton(onClick = { sentir(Sentidos.Evento.TOQUE); onClick() }, enabled = habilitado, modifier = modifier.presion(toque), shape = formaBoton(tema), interactionSource = toque,
        border = BorderStroke(piel.grosor.dp, piel.borde.color()),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)) {
        Text(texto, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Una cifra grande con su rótulo; lleva las esquinas del tema en chico. */
@Composable
fun Cifra(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    Surface(modifier = modifier.drawWithContent { drawContent(); esquinasComp(tema, e, 0.7f) }, shape = R4, color = e.surfaceVariant) {
        Column(Modifier.padding(10.dp)) {
            // Si la cifra no cabe (cifras largas o letra grande del sistema), se achica en vez de cortarse: antes «-0.12»
            // podía verse como «-0.1», que es otro número.
            var tam by remember(valor) { mutableFloatStateOf(19f) }
            Text(contando(valor), style = MaterialTheme.typography.labelLarge.copy(fontSize = tam.sp, letterSpacing = 0.sp), maxLines = 1, softWrap = false,
                onTextLayout = { r -> if (r.hasVisualOverflow && tam > 10f) tam = maxOf(10f, tam * 0.9f) })
            Text(etiqueta.uppercase(), style = MaterialTheme.typography.labelSmall, color = e.onSurfaceVariant, maxLines = 2)
        }
    }
}

/** Nada que mostrar: un recuadro punteado con el trazo del tema y una ilustración pequeña de algo vacío (un ánfora, una lata, un frasco…). */
@Composable
fun Vacio(texto: String) {
    val tema = Estilo.tema
    val piel = pielDe(tema)
    val e = MaterialTheme.colorScheme
    val linea = e.outline
    Box(
        Modifier.fillMaxWidth().drawBehind {
            val guiones = FloatArray(piel.guiones.size) { piel.guiones[it] * 1.dp.toPx() }
            drawRoundRect(linea, cornerRadius = CornerRadius(minOf(radioDe(tema).radio, 8).dp.toPx()), style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(guiones)))
        }.padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(Modifier.size(26.dp)) { dibujoVacio(tema, e) }
            Text(texto, style = MaterialTheme.typography.bodyMedium, color = e.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

/** Aviso: la barra del tema a la izquierda (doble, punteada, de remaches, en zigzag…) y su glifo al empezar. */
@Composable
fun Aviso(texto: String) {
    val tema = Estilo.tema
    val piel = pielDe(tema)
    val e = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(top = 10.dp).height(IntrinsicSize.Min).background(e.secondaryContainer)) {
        Canvas(Modifier.width(6.dp).fillMaxHeight()) { barraAviso(tema, e) }
        Text(buildAnnotatedString {
            withStyle(SpanStyle(color = e.secondary, fontWeight = FontWeight.SemiBold)) { append(piel.aviso) }
            append("  "); append(texto)
        }, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun FilaMenu(titulo: String, detalle: String? = null, onClick: () -> Unit) {
    val piel = pielDe(Estilo.tema)
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            if (detalle != null) Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(piel.flecha, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
}

/** Pastilla: un rótulo chico con el contorno de la forma del tema. */
@Composable
fun Pastilla(texto: String, color: Color = MaterialTheme.colorScheme.primary) {
    Box(Modifier.border(1.dp, color, formaBoton(Estilo.tema)).padding(horizontal = 6.dp, vertical = 1.dp).widthIn(min = 10.dp)) {
        Text(texto, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

fun tipoNombre(id: String, discreto: Boolean): String = Catalogo.tipo(id)?.let { if (discreto) it.discreto else it.nombre } ?: if (id == "muestreo" || id == "animo") "Ánimo" else id


// =====================================================================
//  La piel de los componentes: lo que cambia de un tema a otro
// =====================================================================

/** Qué color del esquema toma un detalle. */
private enum class RolColor { PRIMARIO, SECUNDARIO, TERCIARIO, TINTA, CONTORNO }

@Composable
private fun RolColor.color(): Color = when (this) {
    RolColor.PRIMARIO -> MaterialTheme.colorScheme.primary
    RolColor.SECUNDARIO -> MaterialTheme.colorScheme.secondary
    RolColor.TERCIARIO -> MaterialTheme.colorScheme.tertiary
    RolColor.TINTA -> MaterialTheme.colorScheme.onSurface
    RolColor.CONTORNO -> MaterialTheme.colorScheme.outline
}

/** Cómo se escribe el nombre de una etiqueta de campo. */
private enum class CajaEtiqueta { MINUSCULAS, MAYUSCULAS, CAPITAL }

/** El margen izquierdo del editor. */
private enum class MargenEditor { BARRA, DOBLE, PUNTOS, ROJO, NINGUNO }

/**
 * Los rasgos de los componentes que no son dibujo: glifos, textos cortos, colores y medidas. Los dibujos (esquinas,
 * marcas de la escala, adornos del botón, ilustración de «vacío», barra del aviso, ícono del editor, título de sección)
 * van en sus propios `when (t)` más abajo, uno por tema.
 */
private class PielComponentes(
    val sep: String,               // entre el numeral y el título de una sección
    val cerrada: String,           // sección plegada
    val abierta: String,           // sección abierta
    val numeral: RolColor,         // color del numeral de la sección
    val centrar: Boolean,          // numeral y título centrados en vertical (capitulares y rótulos en bloque)
    val rellena: Boolean,          // el título plegable ocupa todo el ancho (los rótulos en bloque, no)
    val aviso: String,             // glifo al empezar un aviso
    val igual: String,             // entre la clave y el valor (y en la escala)
    val nulo: String,              // escala sin marcar
    val elegido: String,           // opción elegida y perilla del interruptor encendido
    val riel: RolColor,            // riel del interruptor encendido
    val quitar: String,            // quitar una etiqueta de la lista
    val listaVacia: String,        // lista de etiquetas vacía
    val flecha: String,            // fila de menú
    val sufijo: String,            // tras el nombre de una etiqueta de campo
    val caja: CajaEtiqueta,
    val espaciado: Float,          // espaciado de letras de la etiqueta, en sp
    val colorPrefijo: RolColor,    // color del prefijo de la etiqueta (el prefijo es Estilo.prefijo)
    val renglones: Boolean,        // el editor lleva renglones
    val margen: MargenEditor,
    val guiones: FloatArray,       // trazo del recuadro de «vacío», en dp
    val tamBoton: Int,             // letra del botón principal, en sp
    val letraBoton: FontFamily?,   // letra propia del botón (null: la de los títulos)
    val borde: RolColor,           // borde del botón secundario
    val grosor: Float,             // grosor de ese borde, en dp
)

private val PIELES: Map<TemaId, PielComponentes> by lazy { TemaId.entries.associateWith { crearPiel(it) } }

private fun pielDe(t: TemaId): PielComponentes = PIELES.getValue(t)

// Para los controles de ControlesTema.kt: la piel es privada de este archivo y estos son los rasgos que comparten.
/** La forma de los botones del tema (también la del conmutador, el botón de confirmar de los diálogos y la barra de Deshacer). */
internal fun formaBotonTema(t: TemaId): Shape = formaBoton(t)
/** El glifo de «elegido» del tema. */
internal fun glifoElegido(t: TemaId): String = pielDe(t).elegido
/** La flecha de las filas de menú del tema. */
internal fun glifoFlecha(t: TemaId): String = pielDe(t).flecha
/** El color del riel del interruptor encendido. */
@Composable
internal fun rielInterruptor(t: TemaId): Color = pielDe(t).riel.color()

private fun crearPiel(t: TemaId): PielComponentes = when (t) {
    TemaId.ROMANO -> PielComponentes(sep = "  ·  ", cerrada = "⊕", abierta = "⊖", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "N.B.", igual = "= ", nulo = "nihil", elegido = "◉", riel = RolColor.PRIMARIO, quitar = "×", listaVacia = "[ vacat ]", flecha = "→",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0.6f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(4f, 3f), tamBoton = 14, letraBoton = null, borde = RolColor.CONTORNO, grosor = 1f)
    // Rótulos de calle en mayúsculas espaciadas, el verde de las persianas en el interruptor y un cuaderno escolar con renglones y margen.
    TemaId.URBE -> PielComponentes(sep = " — ", cerrada = "▸", abierta = "▾", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "Attenzione:", igual = ": ", nulo = "niente", elegido = "●", riel = RolColor.SECUNDARIO, quitar = "✕", listaVacia = "( ancora niente )", flecha = "›",
        sufijo = ":", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.2f, colorPrefijo = RolColor.TERCIARIO, renglones = true, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(6f, 2f, 1f, 2f), tamBoton = 15, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.4f)
    TemaId.CIBER -> PielComponentes(sep = " ", cerrada = "[+]", abierta = "[-]", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "!", igual = ":= ", nulo = "null", elegido = "▣", riel = RolColor.SECUNDARIO, quitar = "[x]", listaVacia = "[ ]", flecha = ">>",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0f, colorPrefijo = RolColor.SECUNDARIO, renglones = false, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(1.5f, 1.5f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1f)
    TemaId.NEON -> PielComponentes(sep = "  ·  ", cerrada = "▶", abierta = "▼", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "⚠", igual = " ▸ ", nulo = "apagado", elegido = "●", riel = RolColor.PRIMARIO, quitar = "✖", listaVacia = "( nada en el pedido )", flecha = "▸▸",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.4f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.NINGUNO,
        guiones = floatArrayOf(6f, 2f), tamBoton = 15, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.5f)
    TemaId.HELENO -> PielComponentes(sep = "  —  ", cerrada = "◇", abierta = "◆", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "☉", igual = "· ", nulo = "οὐδέν", elegido = "✓", riel = RolColor.SECUNDARIO, quitar = "✕", listaVacia = "( κενόν )", flecha = "⟶",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.5f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(5f, 2f), tamBoton = 14, letraBoton = null, borde = RolColor.TERCIARIO, grosor = 1f)
    // Rúbricas en rojo, como las de los escribas: el margen del editor y el prefijo van en cornalina.
    TemaId.EGIPTO -> PielComponentes(sep = "  ⁞  ", cerrada = "▷", abierta = "▽", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "☥", igual = "∶ ", nulo = "sin signo", elegido = "☉", riel = RolColor.PRIMARIO, quitar = "⨯", listaVacia = "[ rollo vacío ]", flecha = "⇢",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.8f, colorPrefijo = RolColor.SECUNDARIO, renglones = false, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(6f, 2f, 1f, 2f), tamBoton = 15, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1.5f)
    TemaId.MEDIEVAL -> PielComponentes(sep = "  ⁖  ", cerrada = "❦", abierta = "☙", numeral = RolColor.PRIMARIO, centrar = true, rellena = true,
        aviso = "✠", igual = "— ", nulo = "sine nota", elegido = "✚", riel = RolColor.TERCIARIO, quitar = "✗", listaVacia = "( folio en blanco )", flecha = "❧",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.2f, colorPrefijo = RolColor.PRIMARIO, renglones = true, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(0.8f, 2f), tamBoton = 17, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1f)
    TemaId.TEMPLARIO -> PielComponentes(sep = "  ⚔  ", cerrada = "✚", abierta = "☩", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "⚔", igual = " : ", nulo = "sin parte", elegido = "✚", riel = RolColor.PRIMARIO, quitar = "✕", listaVacia = "( rol vacío )", flecha = "⟶",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.2f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(3f, 2f), tamBoton = 15, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.6f)
    TemaId.DISTOPIA -> PielComponentes(sep = " ▪ ", cerrada = "▶", abierta = "▼", numeral = RolColor.TINTA, centrar = true, rellena = false,
        aviso = "⚠", igual = "| ", nulo = "SIN DATO", elegido = "☒", riel = RolColor.TINTA, quitar = "✖", listaVacia = "[ EXPEDIENTE VACÍO ]", flecha = "►",
        sufijo = ":", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.NINGUNO,
        guiones = floatArrayOf(2.5f, 1f), tamBoton = 14, letraBoton = null, borde = RolColor.TINTA, grosor = 2f)
    TemaId.REFUGIO -> PielComponentes(sep = " : ", cerrada = "▷", abierta = "▽", numeral = RolColor.PRIMARIO, centrar = true, rellena = false,
        aviso = "⚛", igual = ": ", nulo = "---", elegido = "✔", riel = RolColor.PRIMARIO, quitar = "⊗", listaVacia = "< VACÍO >", flecha = "▻",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 0.8f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(3f, 1.5f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1.5f)
    TemaId.ORDEN -> PielComponentes(sep = " | ", cerrada = "▹", abierta = "▿", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "⚙", igual = "» ", nulo = "N/D", elegido = "◆", riel = RolColor.SECUNDARIO, quitar = "⊠", listaVacia = "[ SIN REGISTRO ]", flecha = "⟫",
        sufijo = " ::", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 2f, colorPrefijo = RolColor.SECUNDARIO, renglones = false, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(4.5f, 1f), tamBoton = 14, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.5f)
    // La memoria descriptiva de una patente: «Fig. 3 ·», el manicule de los carteles, la aguja del manómetro en cero y el
    // margen doble de la lámina; el botón principal, en la egipcia de los horarios.
    TemaId.VAPOR -> PielComponentes(sep = " · Fig. ", cerrada = "⊕", abierta = "⊖", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "☛", igual = " ⟶ ", nulo = "aguja en cero", elegido = "⊙", riel = RolColor.PRIMARIO, quitar = "⊗", listaVacia = "( ninguna pieza acoplada )", flecha = "⇢",
        sufijo = ".—", caja = CajaEtiqueta.CAPITAL, espaciado = 0.6f, colorPrefijo = RolColor.TERCIARIO, renglones = true, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(8f, 2f, 2f, 2f), tamBoton = 15, letraBoton = LetraExtra.bitter, borde = RolColor.TERCIARIO, grosor = 1.8f)
    TemaId.LABORATORIO -> PielComponentes(sep = "  /  ", cerrada = "＋", abierta = "－", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "⚗", igual = "≈ ", nulo = "n.d.", elegido = "●", riel = RolColor.PRIMARIO, quitar = "−", listaVacia = "( muestra vacía )", flecha = "↦",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0.4f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(1f, 1f), tamBoton = 14, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1f)
    TemaId.COLONOS -> PielComponentes(sep = "  ~  ", cerrada = "☞", abierta = "☟", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "☛", igual = "… ", nulo = "en blanco", elegido = "☑", riel = RolColor.SECUNDARIO, quitar = "✄", listaVacia = "( sin noticias )", flecha = "⇨",
        sufijo = ":", caja = CajaEtiqueta.CAPITAL, espaciado = 0.3f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(6f, 2f), tamBoton = 14, letraBoton = null, borde = RolColor.TINTA, grosor = 1f)
    TemaId.NAPOLEON -> PielComponentes(sep = "  ✧  ", cerrada = "⊞", abierta = "⊟", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "✪", igual = "→ ", nulo = "néant", elegido = "✦", riel = RolColor.TERCIARIO, quitar = "✘", listaVacia = "( sans ordre )", flecha = "➔",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.2f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(5.5f, 1.5f), tamBoton = 14, letraBoton = null, borde = RolColor.TERCIARIO, grosor = 1.5f)
    // Rótulos de marquesina muy espaciados, el abanico de oro como aviso, doble filete en el editor y jade en el borde.
    TemaId.DECO -> PielComponentes(sep = "  ‖  ", cerrada = "▷", abierta = "▽", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "☼", igual = " ≡ ", nulo = "telón cerrado", elegido = "◆", riel = RolColor.PRIMARIO, quitar = "✕", listaVacia = "( butacas vacías )", flecha = "⇢",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 2.2f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(8f, 2f, 2f, 2f), tamBoton = 15, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.2f)
    TemaId.BOTANICO -> PielComponentes(sep = "  ⁂  ", cerrada = "⚘", abierta = "❀", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "✾", igual = "~ ", nulo = "sin espécimen", elegido = "✿", riel = RolColor.SECUNDARIO, quitar = "⊘", listaVacia = "( frasco vacío )", flecha = "⤳",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.1f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(0.5f, 1.8f), tamBoton = 14, letraBoton = null, borde = RolColor.CONTORNO, grosor = 1f)
    TemaId.RUINAS -> PielComponentes(sep = "  ❦  ", cerrada = "▣", abierta = "▢", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "⚠", igual = "· ", nulo = "sin marcar", elegido = "✔", riel = RolColor.PRIMARIO, quitar = "✕", listaVacia = "( mochila vacía )", flecha = "→",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 0.8f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(6f, 3f), tamBoton = 15, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.5f)
    TemaId.NAVIDAD -> PielComponentes(sep = "  ✶  ", cerrada = "❄", abierta = "❅", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "✉", igual = "✶ ", nulo = "sin abrir", elegido = "❆", riel = RolColor.SECUNDARIO, quitar = "⊖", listaVacia = "( ni un regalo )", flecha = "➝",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.4f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.NINGUNO,
        guiones = floatArrayOf(2f, 2f), tamBoton = 14, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.5f)
    TemaId.SEDA -> PielComponentes(sep = "  ◦  ", cerrada = "◎", abierta = "◉", numeral = RolColor.PRIMARIO, centrar = true, rellena = true,
        aviso = "◈", igual = "〉", nulo = "空", elegido = "⊡", riel = RolColor.PRIMARIO, quitar = "(×)", listaVacia = "( alforja vacía )", flecha = "⟿",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.6f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(2f, 0.8f, 0.4f, 0.8f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1f)
    TemaId.EDO -> PielComponentes(sep = "  ・  ", cerrada = "＋", abierta = "－", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "※", igual = "：", nulo = "無", elegido = "■", riel = RolColor.SECUNDARIO, quitar = "〔×〕", listaVacia = "( hatillo vacío )", flecha = "⇢",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0.3f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(5f, 2.5f), tamBoton = 15, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.2f)
    TemaId.GOTICO -> PielComponentes(sep = "  †  ", cerrada = "†", abierta = "‡", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "✝", igual = "⁝ ", nulo = "vacat", elegido = "✠", riel = RolColor.TERCIARIO, quitar = "☓", listaVacia = "( nave vacía )", flecha = "⇀",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.3f, colorPrefijo = RolColor.TERCIARIO, renglones = true, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(3f, 0.8f, 0.8f, 0.8f), tamBoton = 17, letraBoton = null, borde = RolColor.TERCIARIO, grosor = 1.5f)
    TemaId.ARCANO -> PielComponentes(sep = " — ", cerrada = "▹", abierta = "▿", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "⚠", igual = " … ", nulo = "sin anotar", elegido = "◉", riel = RolColor.PRIMARIO, quitar = "✗", listaVacia = "( folio en blanco )", flecha = "↝",
        sufijo = ":", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0.15f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(5f, 2f, 1f, 2f), tamBoton = 16, letraBoton = Letra.spectralCursiva, borde = RolColor.SECUNDARIO, grosor = 1.1f)
    TemaId.ESPACIO -> PielComponentes(sep = "  ⋆  ", cerrada = "☆", abierta = "★", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "⊛", igual = "⇒ ", nulo = "sin señal", elegido = "★", riel = RolColor.PRIMARIO, quitar = "⊟", listaVacia = "[ SIN TELEMETRÍA ]", flecha = "⇥",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 2.5f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(0.8f, 2.5f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1f)
    TemaId.ABISMO -> PielComponentes(sep = "  ∿  ", cerrada = "◌", abierta = "◍", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "≋", igual = "∿ ", nulo = "sin eco", elegido = "◍", riel = RolColor.TERCIARIO, quitar = "⊘", listaVacia = "( el sonar no devuelve eco )", flecha = "⤓",
        sufijo = " ·", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.4f, colorPrefijo = RolColor.TERCIARIO, renglones = true, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(2.2f, 1f, 0.5f, 1f), tamBoton = 16, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.6f)
    TemaId.ANDALUS -> PielComponentes(sep = "  ❖  ", cerrada = "۞", abierta = "✺", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "❁", igual = "∷ ", nulo = "لا شيء", elegido = "✹", riel = RolColor.TERCIARIO, quitar = "‹×›", listaVacia = "( sin azulejos )", flecha = "➛",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.5f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(1.2f, 1.2f, 3f, 1.2f), tamBoton = 14, letraBoton = null, borde = RolColor.TERCIARIO, grosor = 1f)
    // El códice: numerales en rojo de cinabrio, renglones rojos que dividen la página en registros y bloques de glifo.
    TemaId.MESO -> PielComponentes(sep = "  ⁖  ", cerrada = "⊞", abierta = "⊟", numeral = RolColor.PRIMARIO, centrar = true, rellena = true,
        aviso = "☼", igual = "⋯ ", nulo = "mix ba'al", elegido = "◆", riel = RolColor.SECUNDARIO, quitar = "⨯", listaVacia = "( página sin glifos )", flecha = "↣",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.8f, colorPrefijo = RolColor.PRIMARIO, renglones = true, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(4f, 2f, 1f, 2f), tamBoton = 15, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1.4f)
    TemaId.MONGOL -> PielComponentes(sep = "  ◆  ", cerrada = "◑", abierta = "◐", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "☾", igual = "⇢ ", nulo = "хоосон", elegido = "◐", riel = RolColor.PRIMARIO, quitar = "x", listaVacia = "( ger vacía )", flecha = "➜",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.4f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(3.5f, 2.5f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1.5f)
    TemaId.RENACIMIENTO -> PielComponentes(sep = "  ⁘  ", cerrada = "∴", abierta = "∵", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "✎", igual = "≡ ", nulo = "nulla", elegido = "✐", riel = RolColor.PRIMARIO, quitar = "✂", listaVacia = "( foglio bianco )", flecha = "↝",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0.2f, colorPrefijo = RolColor.PRIMARIO, renglones = true, margen = MargenEditor.NINGUNO,
        guiones = floatArrayOf(7f, 1f, 1f, 1f), tamBoton = 17, letraBoton = LetraExtra.cormorant, borde = RolColor.CONTORNO, grosor = 1f)
    // Banderas arriada e izada para plegar y abrir, la «X» del tesoro para lo elegido, la derrota punteada en lo vacío.
    TemaId.PIRATA -> PielComponentes(sep = "  ∿  ", cerrada = "⚐", abierta = "⚑", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "⚑", igual = "⇢ ", nulo = "sin sondar", elegido = "✕", riel = RolColor.SECUNDARIO, quitar = "†", listaVacia = "( mar en calma )", flecha = "➳",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.6f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(5f, 3f), tamBoton = 18, letraBoton = LetraExtra.corsario, borde = RolColor.SECUNDARIO, grosor = 1.5f)
    TemaId.URSS -> PielComponentes(sep = "  ▸  ", cerrada = "◣", abierta = "◥", numeral = RolColor.TINTA, centrar = true, rellena = false,
        aviso = "★", igual = "▸ ", nulo = "НЕТ", elegido = "▶", riel = RolColor.PRIMARIO, quitar = "—", listaVacia = "[ ПУСТО ]", flecha = "➤",
        sufijo = "!", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.8f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(6f, 1.5f), tamBoton = 14, letraBoton = null, borde = RolColor.TINTA, grosor = 2f)
    TemaId.REVOLUCION -> PielComponentes(sep = "  •  ", cerrada = "☐", abierta = "☒", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "⚑", igual = "⟶ ", nulo = "rien", elegido = "✶", riel = RolColor.SECUNDARIO, quitar = "╳", listaVacia = "( cahier vide )", flecha = "⟹",
        sufijo = " —", caja = CajaEtiqueta.CAPITAL, espaciado = 0.8f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(4f, 1f, 4f, 3f), tamBoton = 14, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.5f)
    TemaId.ANTIGUAS -> PielComponentes(sep = "  ◇  ", cerrada = "ᛃ", abierta = "ᛟ", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "ᛟ", igual = "⁖ ", nulo = "sin runa", elegido = "◈", riel = RolColor.SECUNDARIO, quitar = "(✕)", listaVacia = "( cofre vacío )", flecha = "↠",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 1f, colorPrefijo = RolColor.SECUNDARIO, renglones = false, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(2f, 1f, 0.5f, 1f), tamBoton = 14, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1f)
    TemaId.CUEVA -> PielComponentes(sep = "  ⁘  ", cerrada = "⋮", abierta = "⋯", numeral = RolColor.PRIMARIO, centrar = false, rellena = false,
        aviso = "!·", igual = "≈ ", nulo = "nada pintado", elegido = "●", riel = RolColor.PRIMARIO, quitar = "⨯", listaVacia = "( la bolsa está vacía )", flecha = "⟶",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0.8f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(1f, 2.5f), tamBoton = 15, letraBoton = null, borde = RolColor.CONTORNO, grosor = 1.2f)
}

/** El título de una sección, con el trazo del tema (capitular, rótulo en bloque, subrayado doble, sello, tramo tricolor…). */
@Composable
private fun TituloSeccion(nombre: String, t: TemaId, peso: Modifier) {
    val e = MaterialTheme.colorScheme
    val estilo = MaterialTheme.typography.titleLarge
    when (t) {
        // Letras espaciadas, como en una inscripción.
        TemaId.ROMANO -> Text(nombre, style = estilo.copy(letterSpacing = 2.sp), modifier = peso)
        // La placa de mármol de la esquina: doble filete y los dos tornillos que la sujetan al muro.
        TemaId.URBE -> Text(nombre, style = estilo.copy(letterSpacing = 1.6.sp), modifier = peso.drawBehind {
            val g = 1.dp.toPx(); val w = size.width; val h = size.height
            drawRect(e.onBackground.copy(alpha = 0.55f), Offset(0f, 0f), Size(w, h), style = Stroke(1.4f * g))
            drawRect(e.onBackground.copy(alpha = 0.3f), Offset(3 * g, 3 * g), Size(w - 6 * g, h - 6 * g), style = Stroke(0.7f * g))
            listOf(5.5f * g, w - 5.5f * g).forEach { x -> drawCircle(e.tertiary, 1.4f * g, Offset(x, h / 2)) }
        }.padding(horizontal = 14.dp, vertical = 3.dp))
        // Indicador de consola con el cursor detrás.
        TemaId.CIBER -> Text(buildAnnotatedString { append(nombre); withStyle(SpanStyle(color = e.secondary)) { append(" ▌") } }, style = estilo, color = e.primary, modifier = peso)
        // Letrero encendido: el nombre en magenta con halo, subrayado por un tubo ámbar.
        TemaId.NEON -> Text(nombre, style = estilo.copy(shadow = androidx.compose.ui.graphics.Shadow(e.primary.copy(alpha = 0.8f), blurRadius = 14f)), color = e.primary,
            modifier = peso.drawBehind { drawLine(e.secondary.copy(alpha = 0.7f), Offset(0f, size.height), Offset(size.width * 0.4f, size.height), 1.5.dp.toPx(), cap = StrokeCap.Round) }.padding(bottom = 4.dp))
        // Doble filete de oro, como el borde de un vaso.
        TemaId.HELENO -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 1.dp.toPx()
            drawLine(e.tertiary, Offset(0f, y - 2.5.dp.toPx()), Offset(size.width, y - 2.5.dp.toPx()), 0.8.dp.toPx())
            drawLine(e.tertiary, Offset(0f, y), Offset(size.width, y), 0.8.dp.toPx())
        }.padding(bottom = 5.dp))
        // La cenefa de rectángulos de colores bajo el título, y el punto rojo de rúbrica al final.
        TemaId.EGIPTO -> Text(buildAnnotatedString { append(nombre); withStyle(SpanStyle(color = e.secondary)) { append(" •") } }, style = estilo, modifier = peso.drawBehind {
            val f = 1.dp.toPx(); val y = size.height - 3 * f
            val colores = listOf(e.primary, e.tertiary, e.secondary, e.tertiary)
            var x = 0f; var k = 0
            while (x < size.width) { drawRect(colores[k % 4].copy(alpha = 0.75f), Offset(x, y), Size(minOf(4f * f, size.width - x), 2.5f * f)); x += 5f * f; k++ }
        }.padding(bottom = 7.dp))
        // Capitular en rojo, como las rúbricas de los manuscritos.
        TemaId.MEDIEVAL -> Text(buildAnnotatedString {
            withStyle(SpanStyle(color = e.primary, fontSize = estilo.fontSize * 1.55f)) { append(nombre.take(1)) }
            append(nombre.drop(1))
        }, style = estilo, modifier = peso)
        // La cruz roja delante y, debajo, la barra de acero de la sobreveste.
        TemaId.TEMPLARIO -> Text(buildAnnotatedString { withStyle(SpanStyle(color = e.primary)) { append("✚ ") }; append(nombre) },
            style = estilo.copy(letterSpacing = 1.sp), modifier = peso.drawBehind {
                val y = size.height - 1.dp.toPx()
                drawLine(e.secondary, Offset(0f, y), Offset(size.width * 0.6f, y), 2.dp.toPx())
                drawLine(e.primary, Offset(size.width * 0.6f, y), Offset(size.width, y), 0.8.dp.toPx())
            }.padding(bottom = 5.dp))
        // Rótulo de expediente: blanco sobre negro.
        TemaId.DISTOPIA -> Text(nombre, style = estilo, color = e.background, modifier = peso.background(e.onBackground).padding(horizontal = 6.dp, vertical = 1.dp))
        // Letrero de pasillo del refugio: una cápsula con borde.
        TemaId.REFUGIO -> Text(nombre, style = estilo, color = e.primary, modifier = peso.border(1.5.dp, e.primary, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 1.dp))
        // Placa de latón con su tramo carmesí debajo.
        TemaId.ORDEN -> Text(nombre, style = estilo.copy(letterSpacing = 1.5.sp), modifier = peso.drawBehind {
            val g = 2.dp.toPx()
            drawRect(e.secondary, Offset(0f, size.height - g), Size(minOf(size.width, 56.dp.toPx()), g))
            drawRect(e.primary, Offset(0f, size.height - g), Size(minOf(size.width, 14.dp.toPx()), g))
        }.padding(bottom = 5.dp))
        // La placa de nombre: un filete de latón con el remate en arco por encima y un remache a cada lado.
        TemaId.VAPOR -> Text(nombre, style = estilo.copy(letterSpacing = 0.6.sp), modifier = peso.drawBehind {
            val g = 1.dp.toPx(); val w = size.width; val h = size.height
            drawPath(Path().apply { moveTo(0f, h); lineTo(0f, h * 0.35f); quadraticTo(w / 2, -h * 0.25f, w, h * 0.35f); lineTo(w, h) }, e.primary.copy(alpha = 0.7f), style = Stroke(1.4f * g))
            drawLine(e.primary, Offset(0f, h), Offset(w, h), 2f * g)
            listOf(5f * g, w - 5f * g).forEach { x -> remacheVapor(Offset(x, h * 0.62f), 1.8f * g, e.tertiary) }
        }.padding(horizontal = 16.dp, vertical = 4.dp))
        // Pestaña de protocolo: una barra violeta al lado.
        TemaId.LABORATORIO -> Text(nombre, style = estilo, modifier = peso.drawBehind { drawRect(e.secondary, Offset.Zero, Size(3.dp.toPx(), size.height)) }.padding(start = 10.dp))
        // Raya inglesa de gaceta: gruesa y fina.
        TemaId.COLONOS -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            drawRect(e.onBackground, Offset(0f, size.height - 4.dp.toPx()), Size(size.width, 1.6.dp.toPx()))
            drawRect(e.onBackground, Offset(0f, size.height - 1.dp.toPx()), Size(size.width, 0.6.dp.toPx()))
        }.padding(bottom = 6.dp))
        // Azul imperial con una estrella de oro.
        TemaId.NAPOLEON -> Text(buildAnnotatedString { append(nombre); withStyle(SpanStyle(color = e.tertiary)) { append("  ✦") } }, style = estilo.copy(letterSpacing = 1.2.sp), color = e.primary, modifier = peso)
        // Rótulo de marquesina: letras muy abiertas sobre tres filetes de oro que se acortan en escalones.
        TemaId.DECO -> Text(nombre, style = estilo.copy(letterSpacing = 2.6.sp), modifier = peso.drawBehind {
            val g = 1.dp.toPx(); val w = size.width; val h = size.height
            listOf(1f, 0.62f, 0.3f).forEachIndexed { k, f -> drawRect(e.primary.copy(alpha = 0.75f - k * 0.18f), Offset(0f, h - (1.2f + k * 2.6f) * g), Size(w * f, 1.1f * g)) }
        }.padding(bottom = 9.dp))
        // Nombre de especie: cursiva sobre una línea punteada a lápiz.
        TemaId.BOTANICO -> Text(nombre, style = estilo.copy(fontStyle = FontStyle.Italic), modifier = peso.drawBehind {
            drawLine(e.onBackground.copy(alpha = 0.5f), Offset(0f, size.height - 1.dp.toPx()), Offset(size.width, size.height - 1.dp.toPx()), 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 3.dp.toPx())))
        }.padding(bottom = 4.dp))
        // Subrayado de pintura de calle gastada, partido por una grieta, con una mata de hierba que brota al final.
        TemaId.RUINAS -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 2.dp.toPx(); val g = 2.dp.toPx(); val corte = size.width * 0.38f
            drawLine(e.tertiary.copy(alpha = 0.7f), Offset(0f, y), Offset(corte - 2.dp.toPx(), y), g)
            drawLine(e.tertiary.copy(alpha = 0.7f), Offset(corte + 2.dp.toPx(), y), Offset(size.width * 0.86f, y), g)
            for (j in -1..1) drawLine(e.primary, Offset(size.width * 0.93f + j * 1.5.dp.toPx(), y + g / 2), Offset(size.width * 0.93f + j * 3.dp.toPx(), y - (if (j == 0) 7f else 5f).dp.toPx()), 1.2.dp.toPx())
        }.padding(bottom = 6.dp))
        // Rojo con una guirnalda verde que ondula debajo.
        TemaId.NAVIDAD -> Text(nombre, style = estilo, color = e.primary, modifier = peso.drawBehind {
            val y = size.height - 3.dp.toPx(); val amp = 1.5.dp.toPx(); val lam = 14.dp.toPx()
            val p = Path(); var x = 0f
            p.moveTo(0f, y)
            while (x <= size.width) { p.lineTo(x, y + amp * sin(2 * PI * x / lam).toFloat()); x += 2f }
            drawPath(p, e.secondary, style = Stroke(1.6.dp.toPx()))
        }.padding(bottom = 7.dp))
        // Un sello de cinabrio antes del nombre.
        TemaId.SEDA -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val l = 12.dp.toPx(); val o = Offset(0f, (size.height - l) / 2)
            drawRect(e.primary, o, Size(l, l))
            drawRect(e.surface, Offset(o.x + l * 0.2f, o.y + l * 0.2f), Size(l * 0.6f, l * 0.6f), style = Stroke(1.dp.toPx()))
        }.padding(start = 18.dp))
        // Un tanzaku (la tira de papel de los poemas) colgado de su cordón antes del nombre.
        TemaId.EDO -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val a = 6.dp.toPx(); val alto = size.height * 0.78f; val o = Offset(2.dp.toPx(), (size.height - alto) / 2 + 2.dp.toPx())
            drawLine(e.onBackground.copy(alpha = 0.5f), Offset(o.x + a / 2, 0f), Offset(o.x + a / 2, o.y), 0.8.dp.toPx())
            drawRect(e.secondary, o, Size(a, alto))
            drawLine(e.surface.copy(alpha = 0.7f), Offset(o.x + a / 2, o.y + alto * 0.25f), Offset(o.x + a / 2, o.y + alto * 0.75f), 0.8.dp.toPx())
        }.padding(start = 16.dp))
        // Inicial sobre pan de oro, como en un vitral.
        TemaId.GOTICO -> Text(buildAnnotatedString {
            withStyle(SpanStyle(color = e.surface, background = e.tertiary)) { append(" ${nombre.take(1)} ") }
            append(nombre.drop(1))
        }, style = estilo, modifier = peso)
        // Escrito a mano en cursiva y subrayado dos veces, la segunda raya más corta y torcida.
        TemaId.ARCANO -> Text(nombre, style = estilo.copy(fontFamily = Letra.spectralCursiva), color = e.onBackground, modifier = peso.drawBehind {
            val y = size.height - 3.dp.toPx(); val u = 1.dp.toPx()
            drawLine(e.secondary.copy(alpha = 0.8f), Offset(0f, y), Offset(size.width, y - 1.5f * u), 1.2f * u)
            drawLine(e.secondary.copy(alpha = 0.6f), Offset(4 * u, y + 3 * u), Offset(size.width * 0.6f, y + 2 * u), 0.9f * u)
        }.padding(bottom = 7.dp))
        // Letras abiertas sobre una órbita de puntos.
        TemaId.ESPACIO -> Text(nombre, style = estilo.copy(letterSpacing = 2.5.sp), modifier = peso.drawBehind {
            var x = 1.dp.toPx()
            while (x < size.width) { drawCircle(e.primary.copy(alpha = 0.6f), 1.dp.toPx(), Offset(x, size.height - 1.5.dp.toPx())); x += 6.dp.toPx() }
        }.padding(bottom = 5.dp))
        // La regla de sondeo debajo: una raya con marcas cada tanto, más largas cada cinco, como el cabo del escandallo.
        TemaId.ABISMO -> Text(nombre, style = estilo.copy(letterSpacing = 1.2.sp), modifier = peso.drawBehind {
            val y = size.height - 4.dp.toPx(); val paso = 7.dp.toPx()
            drawLine(e.primary.copy(alpha = 0.7f), Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
            var x = 0f; var k = 0
            while (x < size.width) { drawLine(e.tertiary.copy(alpha = 0.8f), Offset(x, y), Offset(x, y + (if (k % 5 == 0) 4f else 2f).dp.toPx()), 1.dp.toPx()); x += maxOf(paso, 1f); k++ }
        }.padding(bottom = 8.dp))
        // Remate con el signo de la rub el-hizb, en oro.
        TemaId.ANDALUS -> Text(buildAnnotatedString { append(nombre); withStyle(SpanStyle(color = e.tertiary)) { append("  ۞") } }, style = estilo, modifier = peso)
        // El renglón rojo del códice debajo y, al final, tres puntos de jade.
        TemaId.MESO -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 2.dp.toPx()
            drawLine(e.primary.copy(alpha = 0.8f), Offset(0f, y), Offset(size.width, y), 1.4.dp.toPx())
            for (k in 0 until 3) drawCircle(e.tertiary, 1.7.dp.toPx(), Offset(size.width - (4 + k * 6).dp.toPx(), y - 4.5.dp.toPx()))
        }.padding(bottom = 8.dp))
        // Una franja de fieltro azul encima.
        TemaId.MONGOL -> Text(nombre, style = estilo, modifier = peso.drawBehind { drawRect(e.primary, Offset.Zero, Size(minOf(size.width, 72.dp.toPx()), 2.5.dp.toPx())) }.padding(top = 6.dp))
        // Dos trazos de sanguina, a mano alzada.
        TemaId.RENACIMIENTO -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 2.dp.toPx()
            drawLine(e.primary.copy(alpha = 0.7f), Offset(0f, y), Offset(size.width * 0.9f, y - 1.dp.toPx()), 0.8.dp.toPx())
            drawLine(e.primary.copy(alpha = 0.45f), Offset(size.width * 0.04f, y + 1.5.dp.toPx()), Offset(size.width * 0.7f, y + 1.dp.toPx()), 0.7.dp.toPx())
        }.padding(bottom = 5.dp))
        // Una ola de tinta bajo el nombre que termina en la «X» roja del tesoro.
        TemaId.PIRATA -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 3.dp.toPx(); val a = 1.6.dp.toPx(); val l = 9.dp.toPx(); val fin = size.width * 0.82f
            val ola = Path(); var x = 0f; ola.moveTo(0f, y)
            while (x < fin) { ola.quadraticTo(x + l / 4, y - a, x + l / 2, y); ola.quadraticTo(x + l * 3 / 4, y + a, x + l, y); x += maxOf(l, 1f) }
            drawPath(ola, e.primary.copy(alpha = 0.6f), style = Stroke(0.9.dp.toPx()))
            val s = 2.6.dp.toPx(); val c = Offset(fin + 7.dp.toPx(), y)
            drawLine(e.tertiary, c - Offset(s, s), c + Offset(s, s), 1.4.dp.toPx(), StrokeCap.Round)
            drawLine(e.tertiary, c + Offset(-s, s), c + Offset(s, -s), 1.4.dp.toPx(), StrokeCap.Round)
        }.padding(bottom = 7.dp))
        // Cartel: blanco sobre rojo, con su estrella.
        TemaId.URSS -> Text("★ $nombre", style = estilo, color = e.onPrimary, modifier = peso.background(e.primary).padding(horizontal = 6.dp, vertical = 1.dp))
        // Tramo azul, blanco y rojo.
        TemaId.REVOLUCION -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val g = 2.5.dp.toPx(); val tercio = minOf(size.width, 84.dp.toPx()) / 3
            listOf(e.secondary, e.outline, e.primary).forEachIndexed { k, c -> drawRect(c, Offset(k * tercio, size.height - g), Size(tercio, g)) }
        }.padding(bottom = 5.dp))
        // Línea tallada que termina en una gema.
        TemaId.ANTIGUAS -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 3.dp.toPx(); val r = 3.dp.toPx(); val fin = minOf(size.width, 120.dp.toPx())
            drawLine(e.secondary.copy(alpha = 0.7f), Offset(0f, y), Offset(fin - r * 2.5f, y), 1.dp.toPx())
            romboComp(Offset(fin - r, y), r * 0.8f, r, e.primary)
        }.padding(bottom = 7.dp))
        // Un trazo de carbón a mano alzada bajo el título, y tres puntos de ocre al final.
        TemaId.CUEVA -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 3.dp.toPx(); val fin = minOf(size.width, 110.dp.toPx()); val u = 1.dp.toPx()
            drawPath(Path().apply { moveTo(0f, y); quadraticTo(fin * 0.3f, y - 2.5f * u, fin * 0.55f, y); quadraticTo(fin * 0.75f, y + 2f * u, fin - 12 * u, y - u) },
                e.onBackground.copy(alpha = 0.6f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            for (k in 0 until 3) drawCircle(e.primary, 1.6f * u, Offset(fin - 8 * u + k * 4 * u, y - u))
        }.padding(bottom = 7.dp))
    }
}

/** La forma de los botones, las fichas de la lista y las pastillas. Las medidas van en proporción a la altura, para que sirvan en todos los tamaños. */
private fun formaBoton(t: TemaId): Shape = when (t) {
    TemaId.ROMANO -> RoundedCornerShape(2.dp)
    // El portón: arco rebajado arriba, pies rectos abajo.
    TemaId.URBE -> RoundedCornerShape(topStartPercent = 40, topEndPercent = 40, bottomEndPercent = 6, bottomStartPercent = 6)
    TemaId.CIBER -> CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp)
    TemaId.NEON -> CutCornerShape(6.dp)   // la placa del letrero
    // Frontón de templo, muy bajo.
    TemaId.HELENO -> GenericShape { s, _ -> moveTo(0f, s.height * 0.18f); lineTo(s.width / 2, 0f); lineTo(s.width, s.height * 0.18f); lineTo(s.width, s.height); lineTo(0f, s.height); close() }
    // Cartucho: redondo a la izquierda y cerrado a la derecha por la barra del nudo.
    TemaId.EGIPTO -> GenericShape { s, _ ->
        val r = s.height / 2
        moveTo(r, 0f); lineTo(s.width, 0f); lineTo(s.width, s.height); lineTo(r, s.height)
        arcTo(Rect(0f, 0f, 2 * r, s.height), 90f, 180f, false); close()
    }
    // Esquinas cóncavas, como un marco de miniatura.
    TemaId.MEDIEVAL -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val r = h * 0.14f
        moveTo(r, 0f); lineTo(w - r, 0f); quadraticTo(w - r, r, w, r); lineTo(w, h - r); quadraticTo(w - r, h - r, w - r, h)
        lineTo(r, h); quadraticTo(r, h - r, 0f, h - r); lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }
    // El gallardete: recto del lado del asta y con cola de golondrina al otro.
    TemaId.TEMPLARIO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - h * 0.22f, h / 2); lineTo(w, h); lineTo(0f, h); close()
    }
    TemaId.DISTOPIA -> RectangleShape
    TemaId.REFUGIO -> RoundedCornerShape(50)
    TemaId.ORDEN -> CutCornerShape(6.dp)
    TemaId.VAPOR -> RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomEnd = 3.dp, bottomStart = 3.dp)
    TemaId.LABORATORIO -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 4.dp, bottomStart = 4.dp)
    // Boleto de feria: una muesca a cada lado.
    TemaId.COLONOS -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val n = h * 0.26f
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h * 0.34f); quadraticTo(w - n, h / 2, w, h * 0.66f); lineTo(w, h)
        lineTo(0f, h); lineTo(0f, h * 0.66f); quadraticTo(n, h / 2, 0f, h * 0.34f); close()
    }
    // Placa de puntas, como una chapa de chacó.
    TemaId.NAPOLEON -> CutCornerShape(50)
    // Placa de ascensor de los años veinte: cada esquina se mete en dos escalones.
    TemaId.DECO -> GenericShape { s, _ ->
        val a = s.height * 0.16f; val w = s.width; val h = s.height
        moveTo(2 * a, 0f); lineTo(w - 2 * a, 0f); lineTo(w - 2 * a, a); lineTo(w - a, a); lineTo(w - a, 2 * a); lineTo(w, 2 * a)
        lineTo(w, h - 2 * a); lineTo(w - a, h - 2 * a); lineTo(w - a, h - a); lineTo(w - 2 * a, h - a); lineTo(w - 2 * a, h)
        lineTo(2 * a, h); lineTo(2 * a, h - a); lineTo(a, h - a); lineTo(a, h - 2 * a); lineTo(0f, h - 2 * a)
        lineTo(0f, 2 * a); lineTo(a, 2 * a); lineTo(a, a); lineTo(2 * a, a); close()
    }
    // Hoja: dos esquinas redondas y dos en punta.
    TemaId.BOTANICO -> RoundedCornerShape(topStart = 16.dp, topEnd = 2.dp, bottomEnd = 16.dp, bottomStart = 2.dp)
    // Una losa con las esquinas desportilladas, cada una distinta.
    TemaId.RUINAS -> CutCornerShape(topStart = 2.dp, topEnd = 9.dp, bottomEnd = 2.dp, bottomStart = 5.dp)
    TemaId.NAVIDAD -> RoundedCornerShape(10.dp)
    // Tablilla con alero, como el rótulo de una puerta.
    TemaId.SEDA -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val a = h * 0.12f
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - a, h * 0.22f); lineTo(w - a, h); lineTo(a, h); lineTo(a, h * 0.22f); close()
    }
    // Ema: la tablilla votiva de madera, con el tejadillo en chaflán.
    TemaId.EDO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val a = h * 0.24f
        moveTo(0f, a); lineTo(w * 0.1f, 0f); lineTo(w * 0.9f, 0f); lineTo(w, a); lineTo(w, h); lineTo(0f, h); close()
    }
    // Arco apuntado.
    TemaId.GOTICO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(0f, h); lineTo(0f, h * 0.5f); quadraticTo(0f, h * 0.1f, w / 2, 0f); quadraticTo(w, h * 0.1f, w, h * 0.5f); lineTo(w, h); close()
    }
    // Ficha de catálogo con la esquina de arriba a la derecha doblada.
    TemaId.ARCANO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val d = minOf(h * 0.32f, w * 0.12f)
        moveTo(0f, 0f); lineTo(w - d, 0f); lineTo(w, d); lineTo(w, h); lineTo(0f, h); close()
    }
    // Tecla de consola, más angosta arriba.
    TemaId.ESPACIO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(h * 0.2f, 0f); lineTo(w - h * 0.2f, 0f); lineTo(w, h); lineTo(0f, h); close()
    }
    // El flotador del batiscafo: proa redonda a la izquierda, popa recortada en diagonal a la derecha.
    TemaId.ABISMO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val r = h / 2
        moveTo(r, 0f); lineTo(w - h * 0.25f, 0f); lineTo(w, h * 0.35f); lineTo(w, h); lineTo(r, h)
        arcTo(Rect(0f, 0f, h, h), 90f, 180f, false); close()
    }
    // Arco de medio punto.
    TemaId.ANDALUS -> RoundedCornerShape(topStartPercent = 50, topEndPercent = 50, bottomEndPercent = 0, bottomStartPercent = 0)
    // La bóveda maya en saledizo: los lados de arriba suben en diagonal hasta la losa.
    TemaId.MESO -> CutCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
    TemaId.MONGOL -> RoundedCornerShape(8.dp)
    // Hoja con la esquina doblada.
    TemaId.RENACIMIENTO -> CutCornerShape(bottomEnd = 12.dp)
    // La duela de un barril de ron: arriba y abajo, un poco combada.
    TemaId.PIRATA -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(0f, h * 0.1f); quadraticTo(w / 2, -h * 0.1f, w, h * 0.1f); lineTo(w, h * 0.9f); quadraticTo(w / 2, h * 1.1f, 0f, h * 0.9f); close()
    }
    // Paralelogramo de cartel.
    TemaId.URSS -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(h * 0.3f, 0f); lineTo(w, 0f); lineTo(w - h * 0.3f, h); lineTo(0f, h); close()
    }
    // Banderola con cola de golondrina.
    TemaId.REVOLUCION -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - h * 0.25f, h / 2); lineTo(w, h); lineTo(0f, h); close()
    }
    // Losa de piedra irregular.
    TemaId.ANTIGUAS -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(h * 0.15f, 0f); lineTo(w - h * 0.05f, h * 0.06f); lineTo(w, h * 0.78f); lineTo(w - h * 0.2f, h); lineTo(h * 0.05f, h * 0.94f); lineTo(0f, h * 0.2f); close()
    }
    // Laja de caliza: los bordes ondulan un poco, como una piedra partida.
    TemaId.CUEVA -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val a = h * 0.08f
        moveTo(0f, h * 0.22f); quadraticTo(w * 0.04f, 0f, w * 0.3f, a * 0.3f); quadraticTo(w * 0.6f, -a * 0.4f, w * 0.88f, a * 0.5f)
        quadraticTo(w, h * 0.1f, w, h * 0.55f); quadraticTo(w * 0.99f, h, w * 0.7f, h - a * 0.2f); quadraticTo(w * 0.35f, h + a * 0.3f, w * 0.1f, h - a * 0.4f)
        quadraticTo(-a * 0.2f, h * 0.8f, 0f, h * 0.22f); close()
    }
}

/** El rótulo de la barra del editor: dónde se escribe en cada tema. Los nombres de archivo en latín se traducen fuera del romano. */
private fun rotuloArchivo(archivo: String, t: TemaId): String {
    val base = archivo.substringBeforeLast('.')
    val raiz = base.substringBefore('_')
    val resto = base.substringAfter('_', "")
    val es = (mapOf("somnium" to "sueño", "notae" to "notas", "ascensus" to "escalada")[raiz] ?: raiz) + (if (resto.isEmpty()) "" else " $resto")
    return when (t) {
        TemaId.ROMANO -> "tabula · " + base.uppercase().replace('U', 'V')
        TemaId.URBE -> "quaderno a righe · $es"
        TemaId.CIBER -> "~/vm/$archivo"
        TemaId.NEON -> "ticket · $es"
        TemaId.HELENO -> "πάπυρος · $es"
        TemaId.EGIPTO -> "rollo del escriba · $es"
        TemaId.MEDIEVAL -> "pluma y tintero · $es"
        TemaId.TEMPLARIO -> "rol de la casa · $es"
        TemaId.DISTOPIA -> "FORMULARIO 7-B · ${es.uppercase()}"
        TemaId.REFUGIO -> "BITÁCORA DEL REFUGIO > $es"
        TemaId.ORDEN -> "CRÓNICA DE LA ORDEN · ${es.uppercase()}"
        TemaId.VAPOR -> "Plano n.º ${(base.hashCode() and 0xffff) % 900 + 100} · $es"
        TemaId.LABORATORIO -> "cuaderno de laboratorio · $es"
        TemaId.COLONOS -> "a máquina · $es"
        TemaId.NAPOLEON -> "dépêche · $es"
        TemaId.DECO -> "telegrama de a bordo · $es"
        TemaId.BOTANICO -> "cuaderno de campo · $es"
        TemaId.RUINAS -> "a lápiz, en el cuaderno del superviviente · $es"
        TemaId.NAVIDAD -> "carta junto al fuego · $es"
        TemaId.SEDA -> "pincel y tinta · $es"
        TemaId.EDO -> "cuaderno cosido · $es"
        TemaId.GOTICO -> "breviario · $es"
        TemaId.ARCANO -> "diario del investigador · $es"
        TemaId.ESPACIO -> "BITÁCORA DE MISIÓN · ${es.uppercase()}"
        TemaId.ABISMO -> "bitácora de inmersión · $es"
        TemaId.ANDALUS -> "cálamo · $es"
        TemaId.MESO -> "códice · $es"
        TemaId.MONGOL -> "тэмдэглэл · $es"
        TemaId.RENACIMIENTO -> "taccuino · $es"
        TemaId.PIRATA -> "bitácora · $es"
        TemaId.URSS -> "ТЕЛЕТАЙП · ${es.uppercase()}"
        TemaId.REVOLUCION -> "cahier · $es"
        TemaId.ANTIGUAS -> "crónica de la compañía · $es"
        TemaId.CUEVA -> "pared pintada · $es"
    }
}

// =====================================================================
//  Dibujos de los componentes
// =====================================================================

/** Dibuja [bloque] en la esquina de arriba a la izquierda y su reflejo en las otras tres. */
private fun DrawScope.enLasCuatro(bloque: DrawScope.() -> Unit) {
    val c = Offset(size.width / 2, size.height / 2)
    bloque()
    scale(-1f, 1f, c) { bloque() }
    scale(1f, -1f, c) { bloque() }
    scale(-1f, -1f, c) { bloque() }
}

/** Estrella de [puntas] puntas; [dentro] es el radio de los valles respecto al de las puntas. */
private fun DrawScope.estrellaComp(c: Offset, r: Float, color: Color, puntas: Int = 5, dentro: Float = 0.45f, estilo: DrawStyle = Fill) {
    val p = Path()
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * dentro
        val a = Math.toRadians(-90.0 + 180.0 / puntas * i)
        val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    drawPath(p, color, style = estilo)
}

private fun DrawScope.romboComp(c: Offset, rx: Float, ry: Float, color: Color, estilo: DrawStyle = Fill) {
    drawPath(Path().apply { moveTo(c.x, c.y - ry); lineTo(c.x + rx, c.y); lineTo(c.x, c.y + ry); lineTo(c.x - rx, c.y); close() }, color, style = estilo)
}

/** Las esquinas de una tarjeta ([k] = 1) o de una cifra ([k] < 1). Se apartan del borde según el radio del tema. */
private fun DrawScope.esquinasComp(t: TemaId, e: ColorScheme, k: Float) {
    val i = (3f + radioDe(t).radio * 0.3f).dp.toPx() * k
    val u = 6.dp.toPx() * k
    val fino = 1.dp.toPx() * k
    val w = size.width; val h = size.height
    when (t) {
        TemaId.ROMANO -> {
            // Tabula ansata: filete interior y una cola de milano a cada lado, como en las placas con inscripción.
            drawRect(e.primary.copy(alpha = 0.3f), Offset(i, i), Size(w - 2 * i, h - 2 * i), style = Stroke(fino * 0.8f))
            listOf(1f, -1f).forEach { s ->
                val x0 = if (s > 0f) i else w - i
                drawPath(Path().apply { moveTo(x0, h / 2 - u); lineTo(x0 + s * u * 0.9f, h / 2); lineTo(x0, h / 2 + u); close() }, e.primary.copy(alpha = 0.55f))
            }
        }
        TemaId.URBE -> {
            // La placa de la calle: marco grueso y filete fino por dentro, con un tornillo en cada esquina.
            drawRect(e.onSurface.copy(alpha = 0.22f), Offset(i, i), Size(w - 2 * i, h - 2 * i), style = Stroke(fino * 1.6f))
            drawRect(e.onSurface.copy(alpha = 0.14f), Offset(i + u * 0.5f, i + u * 0.5f), Size(w - 2 * i - u, h - 2 * i - u), style = Stroke(fino * 0.6f))
            listOf(Offset(i + u * 0.9f, i + u * 0.9f), Offset(w - i - u * 0.9f, i + u * 0.9f), Offset(i + u * 0.9f, h - i - u * 0.9f), Offset(w - i - u * 0.9f, h - i - u * 0.9f)).forEach { p ->
                drawCircle(e.tertiary.copy(alpha = 0.7f), u * 0.22f, p)
                drawLine(e.surface.copy(alpha = 0.8f), p - Offset(u * 0.14f, 0f), p + Offset(u * 0.14f, 0f), fino * 0.6f)
            }
        }
        TemaId.CIBER -> {
            // Un led encendido arriba a la derecha y un tramo de pista abajo a la izquierda.
            drawRect(e.secondary, Offset(w - i - u * 0.7f, i), Size(u * 0.7f, u * 0.35f))
            drawLine(e.primary.copy(alpha = 0.7f), Offset(i, h - i), Offset(i + u * 3f, h - i), fino * 1.5f)
            drawLine(e.primary.copy(alpha = 0.7f), Offset(i + u * 3f, h - i), Offset(i + u * 3.6f, h - i - u * 0.6f), fino * 1.5f)
        }
        TemaId.NEON -> {
            // Una escuadra de tubo de neón arriba a la izquierda, con su halo, y el foquito verde de «abierto» abajo a la derecha.
            val p = Path().apply { moveTo(i, i + u * 2.4f); lineTo(i, i); lineTo(i + u * 2.4f, i) }
            drawPath(p, e.primary.copy(alpha = 0.25f), style = Stroke(fino * 5f, cap = StrokeCap.Round))
            drawPath(p, e.primary, style = Stroke(fino * 1.6f, cap = StrokeCap.Round))
            drawCircle(e.tertiary.copy(alpha = 0.3f), u * 0.5f, Offset(w - i - u * 0.4f, h - i - u * 0.4f))
            drawCircle(e.tertiary, u * 0.2f, Offset(w - i - u * 0.4f, h - i - u * 0.4f))
        }
        TemaId.HELENO -> enLasCuatro {
            // Una vuelta de greca en cada esquina.
            val p = Path().apply { moveTo(i, i + u * 1.2f); lineTo(i, i); lineTo(i + u * 1.2f, i); lineTo(i + u * 1.2f, i + u * 0.8f); lineTo(i + u * 0.45f, i + u * 0.8f); lineTo(i + u * 0.45f, i + u * 0.4f) }
            drawPath(p, e.tertiary.copy(alpha = 0.8f), style = Stroke(fino * 1.2f, cap = StrokeCap.Square))
        }
        TemaId.EGIPTO -> enLasCuatro {
            // En cada esquina, el cuadrito de cornalina donde se cruzan las dos cenefas del marco pintado.
            drawRect(e.secondary.copy(alpha = 0.75f), Offset(i, i), Size(u * 0.55f, u * 0.55f))
            drawLine(e.primary.copy(alpha = 0.6f), Offset(i + u * 0.75f, i + u * 0.27f), Offset(i + u * 2f, i + u * 0.27f), fino * 1.6f)
            drawLine(e.primary.copy(alpha = 0.6f), Offset(i + u * 0.27f, i + u * 0.75f), Offset(i + u * 0.27f, i + u * 2f), fino * 1.6f)
            drawCircle(e.tertiary.copy(alpha = 0.8f), fino * 0.9f, Offset(i + u * 2.3f, i + u * 0.27f))
        }
        TemaId.MEDIEVAL -> enLasCuatro {
            // Esquina de pan de oro: un cuarto de anillo con su punto, y un punto rojo de rúbrica.
            drawArc(e.tertiary, 180f, 90f, false, Offset(i, i), Size(u * 2f, u * 2f), style = Stroke(fino * 1.4f))
            drawCircle(e.tertiary, u * 0.3f, Offset(i + u * 0.35f, i + u * 0.35f))
            drawCircle(e.primary, u * 0.17f, Offset(i + u * 1.5f, i + u * 0.2f))
        }
        TemaId.TEMPLARIO -> enLasCuatro {
            // Esquinera de hierro remachada y una cruz patada roja pequeña.
            drawLine(e.secondary, Offset(i, i), Offset(i + u * 2.2f, i), fino * 1.6f)
            drawLine(e.secondary, Offset(i, i), Offset(i, i + u * 2.2f), fino * 1.6f)
            drawCircle(e.tertiary, u * 0.16f, Offset(i + u * 1.8f, i + u * 0.3f)); drawCircle(e.tertiary, u * 0.16f, Offset(i + u * 0.3f, i + u * 1.8f))
            drawPath(cruzPatada(Offset(i + u * 0.95f, i + u * 0.95f), u * 0.5f), e.primary.copy(alpha = 0.75f))
        }
        TemaId.DISTOPIA -> {
            // Un clip de expediente que asoma por arriba y un sello rojo abajo a la derecha.
            val x = i + u * 1.2f
            drawRoundRect(e.onSurface.copy(alpha = 0.7f), Offset(x, -u * 0.6f), Size(u * 0.9f, u * 2.8f), CornerRadius(u * 0.45f), style = Stroke(fino * 1.3f))
            drawLine(e.onSurface.copy(alpha = 0.7f), Offset(x + u * 0.45f, -u * 0.2f), Offset(x + u * 0.45f, u * 1.6f), fino * 1.1f)
            drawRect(e.primary.copy(alpha = 0.8f), Offset(w - i - u * 1.1f, h - i - u * 1.1f), Size(u * 1.1f, u * 1.1f), style = Stroke(fino * 1.6f))
        }
        TemaId.REFUGIO -> enLasCuatro {
            // Remaches de escotilla.
            val c = Offset(i + u * 0.45f, i + u * 0.45f)
            drawCircle(e.secondary.copy(alpha = 0.8f), u * 0.32f, c)
            drawCircle(e.surface.copy(alpha = 0.7f), u * 0.12f, Offset(c.x - u * 0.1f, c.y - u * 0.1f))
        }
        TemaId.ORDEN -> enLasCuatro {
            // Chaflán de placa de acero con su perno de latón.
            drawLine(e.secondary, Offset(i, i + u * 1.4f), Offset(i + u * 1.4f, i), fino * 1.6f)
            drawCircle(e.secondary, u * 0.22f, Offset(i + u * 1.25f, i + u * 1.25f))
        }
        TemaId.VAPOR -> enLasCuatro {
            // Cantonera de latón curva, como las de los libros mayores, con su remache.
            drawPath(Path().apply { moveTo(i, i); lineTo(i + u * 1.6f, i); quadraticTo(i + u * 0.7f, i + u * 0.7f, i, i + u * 1.6f); close() }, e.primary.copy(alpha = 0.75f))
            remacheVapor(Offset(i + u * 0.45f, i + u * 0.45f), u * 0.16f, e.tertiary)
        }
        TemaId.LABORATORIO -> {
            // Etiqueta de muestra arriba a la derecha y una graduación por el borde izquierdo.
            val et = Offset(w - i - u * 3.2f, i)
            drawRoundRect(e.primary.copy(alpha = 0.7f), et, Size(u * 3.2f, u * 1.3f), CornerRadius(u * 0.3f), style = Stroke(fino))
            drawLine(e.primary.copy(alpha = 0.5f), Offset(et.x + u * 0.4f, et.y + u * 0.65f), Offset(et.x + u * 2.4f, et.y + u * 0.65f), fino)
            for (n in 0 until 5) {
                val y = h / 2 - u * 2f + n * u
                drawLine(e.secondary.copy(alpha = 0.6f), Offset(i * 0.6f, y), Offset(i * 0.6f + (if (n % 2 == 0) u * 0.9f else u * 0.5f), y), fino)
            }
        }
        TemaId.COLONOS -> {
            // Doble filete de imprenta por dentro.
            drawRect(e.primary.copy(alpha = 0.45f), Offset(i, i), Size(w - 2 * i, h - 2 * i), style = Stroke(fino * 1.4f))
            val j = i + 2.dp.toPx() * k
            drawRect(e.primary.copy(alpha = 0.35f), Offset(j, j), Size(w - 2 * j, h - 2 * j), style = Stroke(fino * 0.6f))
        }
        TemaId.NAPOLEON -> enLasCuatro { estrellaComp(Offset(i + u * 0.6f, i + u * 0.6f), u * 0.6f, e.tertiary) }
        TemaId.DECO -> {
            // Doble filete de oro y, en cada esquina, tres escalones que se meten hacia dentro.
            drawRect(e.primary.copy(alpha = 0.35f), Offset(i, i), Size(w - 2 * i, h - 2 * i), style = Stroke(fino * 0.8f))
            enLasCuatro {
                val esc = Path().apply {
                    moveTo(i, i + u * 1.8f); lineTo(i + u * 0.6f, i + u * 1.8f); lineTo(i + u * 0.6f, i + u * 1.2f); lineTo(i + u * 1.2f, i + u * 1.2f)
                    lineTo(i + u * 1.2f, i + u * 0.6f); lineTo(i + u * 1.8f, i + u * 0.6f); lineTo(i + u * 1.8f, i)
                }
                drawPath(esc, e.primary.copy(alpha = 0.75f), style = Stroke(fino * 1.2f))
                drawRect(e.secondary.copy(alpha = 0.6f), Offset(i + u * 0.15f, i + u * 0.15f), Size(u * 0.32f, u * 0.32f))
            }
        }
        TemaId.BOTANICO -> {
            // El alfiler del entomólogo, clavado arriba al centro, con su sombra.
            val c = Offset(w / 2, i + u * 0.2f)
            drawLine(e.onSurface.copy(alpha = 0.25f), c, Offset(c.x + u * 1.2f, c.y + u * 0.9f), fino * 1.2f)
            drawCircle(e.secondary, u * 0.38f, c)
            drawCircle(e.surface.copy(alpha = 0.6f), u * 0.13f, Offset(c.x - u * 0.12f, c.y - u * 0.12f))
        }
        TemaId.RUINAS -> {
            // Una guía de hiedra que trepa desde la esquina de arriba a la izquierda, con tres hojas de cinco puntas.
            val o = Offset(i, i)
            val tallo = Path().apply { moveTo(o.x, o.y + u * 3.2f); quadraticTo(o.x + u * 0.4f, o.y + u * 0.4f, o.x + u * 3.4f, o.y) }
            drawPath(tallo, e.primary.copy(alpha = 0.55f), style = Stroke(fino))
            listOf(Offset(o.x + u * 0.25f, o.y + u * 2.2f) to 200f, Offset(o.x + u * 1.0f, o.y + u * 0.9f) to 135f, Offset(o.x + u * 2.4f, o.y + u * 0.15f) to 80f).forEach { (p, g) ->
                rotate(g, p) {
                    drawPath(Path().apply {
                        moveTo(p.x, p.y); lineTo(p.x - u * 0.33f, p.y + u * 0.1f); lineTo(p.x - u * 0.45f, p.y + u * 0.35f); lineTo(p.x - u * 0.18f, p.y + u * 0.33f)
                        lineTo(p.x, p.y + u * 0.62f); lineTo(p.x + u * 0.18f, p.y + u * 0.33f); lineTo(p.x + u * 0.45f, p.y + u * 0.35f); lineTo(p.x + u * 0.33f, p.y + u * 0.1f); close()
                    }, e.primary.copy(alpha = 0.75f))
                }
            }
        }
        TemaId.NAVIDAD -> {
            // Una ramita de acebo arriba a la derecha: dos hojas y tres bayas.
            val c = Offset(w - i - u * 1.4f, i + u * 0.7f)
            rotate(-25f, c) { drawOval(e.secondary, Offset(c.x - u * 1.3f, c.y - u * 0.3f), Size(u * 1.3f, u * 0.6f)) }
            rotate(25f, c) { drawOval(e.secondary, Offset(c.x, c.y - u * 0.3f), Size(u * 1.3f, u * 0.6f)) }
            listOf(Offset(-0.2f, 0.25f), Offset(0.2f, 0.25f), Offset(0f, -0.1f)).forEach { o -> drawCircle(e.primary, u * 0.24f, Offset(c.x + o.x * u, c.y + o.y * u)) }
        }
        TemaId.SEDA -> {
            // Sello de cinabrio abajo a la derecha.
            val s = u * 1.5f; val o = Offset(w - i - s, h - i - s)
            drawRect(e.primary.copy(alpha = 0.85f), o, Size(s, s))
            drawRect(e.surface.copy(alpha = 0.8f), Offset(o.x + s * 0.18f, o.y + s * 0.18f), Size(s * 0.64f, s * 0.64f), style = Stroke(fino))
            drawLine(e.surface.copy(alpha = 0.8f), Offset(o.x + s * 0.5f, o.y + s * 0.25f), Offset(o.x + s * 0.5f, o.y + s * 0.75f), fino)
        }
        TemaId.EDO -> {
            // La costura de cuatro agujeros (yotsume-toji) de los cuadernos japoneses, en el borde izquierdo.
            val x = i + u * 0.7f; val hilo = e.secondary.copy(alpha = 0.7f)
            val ys = listOf(0.2f, 0.4f, 0.6f, 0.8f).map { h * it }
            drawLine(hilo, Offset(x, ys.first()), Offset(x, ys.last()), fino * 1.2f)
            ys.forEach { y -> drawLine(hilo, Offset(i * 0.4f, y), Offset(x, y), fino * 1.2f); drawCircle(e.onSurface.copy(alpha = 0.45f), fino * 1.3f, Offset(x, y)) }
        }
        TemaId.GOTICO -> enLasCuatro {
            // Cuadrifolio de tracería.
            val c = Offset(i + u * 0.8f, i + u * 0.8f); val r = u * 0.3f
            listOf(Offset(0f, -r), Offset(r, 0f), Offset(0f, r), Offset(-r, 0f)).forEach { o -> drawCircle(e.tertiary.copy(alpha = 0.8f), r, c + o, style = Stroke(fino)) }
        }
        TemaId.ARCANO -> {
            // Dos tiras de cinta amarillenta que pegan la tarjeta, como un recorte, y una mancha de tinta abajo.
            listOf(Offset(i + u * 0.2f, i + u * 0.4f) to -38f, Offset(w - i - u * 0.2f, i + u * 0.4f) to 38f).forEach { (c, g) ->
                rotate(g, c) { drawRect(Color(0xFFE6D9A6).copy(alpha = 0.55f), Offset(c.x - u * 1.1f, c.y - u * 0.35f), Size(u * 2.2f, u * 0.7f)) }
            }
            val m = Offset(w - i - u * 1.6f, h - i - u * 0.9f)
            drawCircle(e.primary.copy(alpha = 0.18f), u * 0.5f, m); drawCircle(e.primary.copy(alpha = 0.14f), u * 0.22f, m + Offset(u * 0.7f, u * 0.2f))
        }
        TemaId.ESPACIO -> enLasCuatro {
            // Escuadras de visor.
            drawLine(e.primary, Offset(i, i + u * 1.6f), Offset(i, i), fino * 1.5f)
            drawLine(e.primary, Offset(i, i), Offset(i + u * 1.6f, i), fino * 1.5f)
            drawCircle(e.primary, fino * 1.2f, Offset(i + u * 2.1f, i))
        }
        TemaId.ABISMO -> enLasCuatro {
            // Un cuarto de la brida de un ojo de buey, con su perno de latón.
            drawArc(e.primary.copy(alpha = 0.7f), 180f, 90f, false, Offset(i, i), Size(u * 3f, u * 3f), style = Stroke(fino * 1.4f))
            drawCircle(e.secondary, fino * 1.8f, Offset(i + u * 0.55f, i + u * 0.55f))
            drawCircle(e.surface.copy(alpha = 0.7f), fino * 0.6f, Offset(i + u * 0.45f, i + u * 0.45f))
        }
        TemaId.ANDALUS -> enLasCuatro { estrellaComp(Offset(i + u * 0.7f, i + u * 0.7f), u * 0.7f, e.tertiary, puntas = 8, dentro = 0.6f) }
        // Una esquina escalonada de tres gradas en cinabrio, con un cuadro de jade adentro.
        TemaId.MESO -> enLasCuatro {
            val g = u * 0.42f
            for (k in 0 until 3) drawRect(e.primary.copy(alpha = 0.75f), Offset(i + k * g, i + (2 - k) * g), Size(g, g * (k + 1).toFloat()))
            drawRect(e.tertiary, Offset(i + g * 1.15f, i + g * 2.15f), Size(g * 0.7f, g * 0.7f))
        }
        TemaId.MONGOL -> enLasCuatro {
            // Voluta de nube en cada esquina.
            drawArc(e.primary.copy(alpha = 0.8f), 180f, 270f, false, Offset(i, i), Size(u * 1.4f, u * 1.4f), style = Stroke(fino * 1.3f))
            drawArc(e.primary.copy(alpha = 0.8f), 0f, 270f, false, Offset(i + u * 0.35f, i + u * 0.35f), Size(u * 0.7f, u * 0.7f), style = Stroke(fino * 1.1f))
        }
        TemaId.RENACIMIENTO -> {
            // Líneas de construcción que se pasan de la esquina y un círculo de compás.
            enLasCuatro {
                drawLine(e.primary.copy(alpha = 0.45f), Offset(0f, i + u), Offset(i + u * 2.2f, i + u), fino * 0.8f)
                drawLine(e.primary.copy(alpha = 0.45f), Offset(i + u, 0f), Offset(i + u, i + u * 2.2f), fino * 0.8f)
            }
            drawCircle(e.primary.copy(alpha = 0.4f), u * 0.9f, Offset(w - i - u, i + u), style = Stroke(fino * 0.8f))
        }
        TemaId.PIRATA -> {
            // Cantoneras de latón clavadas en las esquinas, como en un cofre, y un cabo que corre por el borde de arriba.
            val laton = e.secondary.copy(alpha = 0.55f)
            enLasCuatro {
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(u * 2.2f, 0f); lineTo(u * 2.2f, i * 0.5f); lineTo(i * 0.5f, i * 0.5f); lineTo(i * 0.5f, u * 2.2f); lineTo(0f, u * 2.2f); close() }, laton)
                drawCircle(e.onSurface.copy(alpha = 0.35f), fino * 1.1f, Offset(u * 0.8f, u * 0.8f) * 0.6f + Offset(i * 0.25f, i * 0.25f))
            }
            var x = u * 3f
            while (x < w - u * 3f) { drawLine(e.primary.copy(alpha = 0.22f), Offset(x, i * 0.3f), Offset(x + u * 0.5f, i * 0.3f + u * 0.5f), fino * 0.9f); x += maxOf(u * 0.6f, 1f) }
        }
        TemaId.URSS -> {
            // Cuña roja de cartel arriba y una barra negra abajo.
            drawPath(Path().apply { moveTo(w - u * 3f, 0f); lineTo(w, 0f); lineTo(w, u * 3f); close() }, e.primary)
            drawRect(e.onSurface, Offset(i, h - i - u * 0.45f), Size(u * 3.2f, u * 0.45f))
        }
        TemaId.REVOLUCION -> {
            // Escarapela arriba a la izquierda.
            val c = Offset(i + u * 0.7f, i + u * 0.7f)
            drawCircle(e.secondary, u * 0.7f, c); drawCircle(Color.White, u * 0.46f, c); drawCircle(e.primary, u * 0.24f, c)
        }
        TemaId.ANTIGUAS -> enLasCuatro {
            // Gema engastada y dos muescas talladas.
            romboComp(Offset(i + u * 0.6f, i + u * 0.6f), u * 0.45f, u * 0.6f, e.primary)
            drawLine(e.secondary.copy(alpha = 0.7f), Offset(i + u * 1.3f, i + u * 0.2f), Offset(i + u * 2f, i + u * 0.2f), fino)
            drawLine(e.secondary.copy(alpha = 0.7f), Offset(i + u * 0.2f, i + u * 1.3f), Offset(i + u * 0.2f, i + u * 2f), fino)
        }
        TemaId.CUEVA -> enLasCuatro {
            // En cada esquina, tres puntos de ocre soplado y un trazo corto de carbón.
            listOf(Offset(i + u * 0.3f, i + u * 0.3f), Offset(i + u * 1.1f, i + u * 0.15f), Offset(i + u * 0.15f, i + u * 1.1f)).forEachIndexed { k, o ->
                drawCircle(e.primary.copy(alpha = 0.75f - k * 0.15f), u * (0.3f - k * 0.05f), o)
            }
            drawLine(e.onBackground.copy(alpha = 0.35f), Offset(i + u * 1.8f, i + u * 0.2f), Offset(i + u * 3f, i + u * 0.35f), fino, StrokeCap.Round)
        }
    }
}

/** Ilustración pequeña de algo vacío, para el recuadro de «nada que mostrar». */
private fun DrawScope.dibujoVacio(t: TemaId, e: ColorScheme) {
    val s = size.minDimension
    fun f(v: Float) = v * s
    val l = e.onSurfaceVariant.copy(alpha = 0.8f); val a = e.primary
    val tr = Stroke(1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (t) {
        TemaId.ROMANO -> {
            // Ánfora vacía.
            drawPath(Path().apply { moveTo(f(0.42f), f(0.1f)); lineTo(f(0.42f), f(0.26f)); quadraticTo(f(0.16f), f(0.42f), f(0.5f), f(0.94f)); quadraticTo(f(0.84f), f(0.42f), f(0.58f), f(0.26f)); lineTo(f(0.58f), f(0.1f)) }, l, style = tr)
            drawLine(a, Offset(f(0.36f), f(0.1f)), Offset(f(0.64f), f(0.1f)), tr.width)
            drawArc(l, 90f, 180f, false, Offset(f(0.3f), f(0.14f)), Size(f(0.14f), f(0.2f)), style = tr)
            drawArc(l, -90f, 180f, false, Offset(f(0.56f), f(0.14f)), Size(f(0.14f), f(0.2f)), style = tr)
        }
        TemaId.URBE -> {
            // La tazzina del café ya bebido, en su platito: sin vapor.
            drawOval(l, Offset(f(0.1f), f(0.72f)), Size(f(0.8f), f(0.16f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.24f), f(0.4f)); lineTo(f(0.3f), f(0.7f)); quadraticTo(f(0.5f), f(0.8f), f(0.7f), f(0.7f)); lineTo(f(0.76f), f(0.4f)) }, l, style = tr)
            drawOval(a, Offset(f(0.24f), f(0.35f)), Size(f(0.52f), f(0.1f)), style = tr)
            drawArc(l, -80f, 190f, false, Offset(f(0.7f), f(0.44f)), Size(f(0.16f), f(0.16f)), style = tr)
        }
        TemaId.CIBER -> {
            // Consola sin salida: el indicador y el cursor.
            drawRect(l, Offset(f(0.06f), f(0.18f)), Size(f(0.88f), f(0.64f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.2f), f(0.36f)); lineTo(f(0.34f), f(0.5f)); lineTo(f(0.2f), f(0.64f)) }, a, style = tr)
            drawLine(e.secondary, Offset(f(0.42f), f(0.64f)), Offset(f(0.62f), f(0.64f)), tr.width)
        }
        TemaId.NEON -> {
            // El puesto cerrado: la cortina metálica bajada y, encima, el letrero apagado.
            drawRect(l.copy(alpha = 0.4f), Offset(f(0.1f), f(0.1f)), Size(f(0.8f), f(0.18f)), style = tr)
            drawLine(a.copy(alpha = 0.35f), Offset(f(0.2f), f(0.19f)), Offset(f(0.8f), f(0.19f)), tr.width)
            drawRect(l, Offset(f(0.14f), f(0.34f)), Size(f(0.72f), f(0.56f)), style = tr)
            var y = f(0.42f); while (y < f(0.88f)) { drawLine(l.copy(alpha = 0.6f), Offset(f(0.14f), y), Offset(f(0.86f), y), tr.width * 0.7f); y += maxOf(f(0.08f), 1f) }
            drawCircle(e.secondary, f(0.03f), Offset(f(0.5f), f(0.86f)))
        }
        TemaId.HELENO -> {
            // Cílica vacía: copa baja con dos asas y pie.
            drawArc(l, 0f, 180f, false, Offset(f(0.12f), f(0.12f)), Size(f(0.76f), f(0.5f)), style = tr)
            drawLine(l, Offset(f(0.12f), f(0.37f)), Offset(f(0.88f), f(0.37f)), tr.width)
            drawLine(l, Offset(f(0.5f), f(0.62f)), Offset(f(0.5f), f(0.82f)), tr.width)
            drawLine(a, Offset(f(0.32f), f(0.86f)), Offset(f(0.68f), f(0.86f)), tr.width)
            drawArc(l, 180f, 180f, false, Offset(0f, f(0.3f)), Size(f(0.16f), f(0.14f)), style = tr)
            drawArc(l, 180f, 180f, false, Offset(f(0.84f), f(0.3f)), Size(f(0.16f), f(0.14f)), style = tr)
        }
        TemaId.EGIPTO -> {
            // Un cartucho vacío: el óvalo de cuerda, sin nombre todavía, con su barra abajo.
            drawRoundRect(l, Offset(f(0.3f), f(0.06f)), Size(f(0.4f), f(0.76f)), CornerRadius(f(0.2f)), style = tr)
            drawLine(l, Offset(f(0.24f), f(0.9f)), Offset(f(0.76f), f(0.9f)), tr.width * 1.6f)
            drawLine(a, Offset(f(0.42f), f(0.82f)), Offset(f(0.42f), f(0.9f)), tr.width)
            drawLine(a, Offset(f(0.58f), f(0.82f)), Offset(f(0.58f), f(0.9f)), tr.width)
        }
        TemaId.MEDIEVAL -> {
            // Tintero seco y la pluma apoyada.
            drawRoundRect(l, Offset(f(0.14f), f(0.58f)), Size(f(0.4f), f(0.32f)), CornerRadius(f(0.06f)), style = tr)
            drawLine(l, Offset(f(0.24f), f(0.58f)), Offset(f(0.24f), f(0.5f)), tr.width)
            drawLine(l, Offset(f(0.44f), f(0.58f)), Offset(f(0.44f), f(0.5f)), tr.width)
            drawPath(Path().apply { moveTo(f(0.36f), f(0.62f)); quadraticTo(f(0.6f), f(0.2f), f(0.92f), f(0.06f)); quadraticTo(f(0.78f), f(0.36f), f(0.36f), f(0.62f)) }, a, style = tr)
        }
        TemaId.TEMPLARIO -> {
            // El yelmo de cubo sin nadie dentro: la ranura de la vista y la cruz de los respiraderos.
            drawPath(Path().apply {
                moveTo(f(0.22f), f(0.88f)); lineTo(f(0.22f), f(0.3f)); quadraticTo(f(0.22f), f(0.1f), f(0.5f), f(0.08f))
                quadraticTo(f(0.78f), f(0.1f), f(0.78f), f(0.3f)); lineTo(f(0.78f), f(0.88f)); close()
            }, l, style = tr)
            drawLine(a, Offset(f(0.3f), f(0.42f)), Offset(f(0.7f), f(0.42f)), tr.width * 1.6f)
            drawLine(l, Offset(f(0.5f), f(0.52f)), Offset(f(0.5f), f(0.8f)), tr.width); drawLine(l, Offset(f(0.38f), f(0.65f)), Offset(f(0.62f), f(0.65f)), tr.width)
        }
        TemaId.DISTOPIA -> {
            // Carpeta de expediente vacía, cruzada por una franja roja.
            drawPath(Path().apply { moveTo(f(0.08f), f(0.86f)); lineTo(f(0.08f), f(0.2f)); lineTo(f(0.38f), f(0.2f)); lineTo(f(0.46f), f(0.3f)); lineTo(f(0.92f), f(0.3f)); lineTo(f(0.92f), f(0.86f)); close() }, l, style = tr)
            drawLine(a, Offset(f(0.2f), f(0.74f)), Offset(f(0.8f), f(0.44f)), tr.width * 1.6f)
        }
        TemaId.REFUGIO -> {
            // Lata de conservas abierta y vacía, con la tapa levantada.
            drawOval(l, Offset(f(0.22f), f(0.24f)), Size(f(0.56f), f(0.16f)), style = tr)
            drawLine(l, Offset(f(0.22f), f(0.32f)), Offset(f(0.22f), f(0.82f)), tr.width)
            drawLine(l, Offset(f(0.78f), f(0.32f)), Offset(f(0.78f), f(0.82f)), tr.width)
            drawArc(l, 0f, 180f, false, Offset(f(0.22f), f(0.74f)), Size(f(0.56f), f(0.16f)), style = tr)
            drawLine(a, Offset(f(0.22f), f(0.56f)), Offset(f(0.78f), f(0.56f)), tr.width)
            drawArc(a, 200f, 120f, false, Offset(f(0.3f), f(0.02f)), Size(f(0.56f), f(0.2f)), style = tr)
        }
        TemaId.ORDEN -> {
            // Yunque sin pieza.
            drawPath(Path().apply {
                moveTo(f(0.06f), f(0.34f)); lineTo(f(0.86f), f(0.34f)); lineTo(f(0.94f), f(0.26f)); lineTo(f(0.94f), f(0.46f)); lineTo(f(0.66f), f(0.5f))
                lineTo(f(0.6f), f(0.7f)); lineTo(f(0.76f), f(0.86f)); lineTo(f(0.24f), f(0.86f)); lineTo(f(0.4f), f(0.7f)); lineTo(f(0.34f), f(0.5f))
                quadraticTo(f(0.14f), f(0.48f), f(0.06f), f(0.34f)); close()
            }, l, style = tr)
            drawLine(a, Offset(f(0.24f), f(0.94f)), Offset(f(0.76f), f(0.94f)), tr.width)
        }
        TemaId.VAPOR -> {
            // Un manómetro con la aguja en cero: la caldera está fría.
            drawCircle(l, f(0.4f), Offset(f(0.5f), f(0.5f)), style = tr)
            for (k in 0..8) { val ang = Math.toRadians(135.0 + 33.75 * k); val c0 = Offset(f(0.5f), f(0.5f)); drawLine(l, Vapor.en(c0, f(0.32f), ang), Vapor.en(c0, f(if (k % 4 == 0) 0.24f else 0.28f), ang), tr.width * 0.8f) }
            drawLine(a, Offset(f(0.5f), f(0.5f)), Vapor.en(Offset(f(0.5f), f(0.5f)), f(0.26f), Math.toRadians(135.0)), tr.width * 1.4f)
            drawCircle(a, f(0.04f), Offset(f(0.5f), f(0.5f)))
            drawLine(l, Offset(f(0.5f), f(0.9f)), Offset(f(0.5f), f(0.98f)), tr.width * 2f)
        }
        TemaId.LABORATORIO -> rotate(18f) {
            // Tubo de ensayo vacío, con su graduación.
            drawLine(l, Offset(f(0.38f), f(0.08f)), Offset(f(0.38f), f(0.72f)), tr.width)
            drawLine(l, Offset(f(0.62f), f(0.08f)), Offset(f(0.62f), f(0.72f)), tr.width)
            drawArc(l, 0f, 180f, false, Offset(f(0.38f), f(0.6f)), Size(f(0.24f), f(0.24f)), style = tr)
            drawLine(a, Offset(f(0.32f), f(0.08f)), Offset(f(0.68f), f(0.08f)), tr.width)
            listOf(0.3f, 0.42f, 0.54f).forEach { y -> drawLine(a.copy(alpha = 0.6f), Offset(f(0.38f), f(y)), Offset(f(0.48f), f(y)), tr.width * 0.6f) }
        }
        TemaId.COLONOS -> {
            // Gaceta sin noticias: la cabecera y columnas en blanco.
            drawRect(l, Offset(f(0.12f), f(0.1f)), Size(f(0.76f), f(0.8f)), style = tr)
            drawLine(a, Offset(f(0.2f), f(0.24f)), Offset(f(0.8f), f(0.24f)), tr.width * 1.8f)
            drawLine(l, Offset(f(0.2f), f(0.34f)), Offset(f(0.8f), f(0.34f)), tr.width * 0.6f)
            drawLine(l, Offset(f(0.5f), f(0.42f)), Offset(f(0.5f), f(0.82f)), tr.width * 0.6f)
        }
        TemaId.NAPOLEON -> {
            // Bicornio sin dueño, con su escarapela.
            drawPath(Path().apply { moveTo(f(0.04f), f(0.66f)); quadraticTo(f(0.5f), 0f, f(0.96f), f(0.66f)); quadraticTo(f(0.5f), f(0.5f), f(0.04f), f(0.66f)); close() }, l, style = tr)
            drawCircle(a, f(0.08f), Offset(f(0.66f), f(0.4f)))
            drawLine(l, Offset(f(0.2f), f(0.78f)), Offset(f(0.8f), f(0.78f)), tr.width)
        }
        TemaId.DECO -> {
            // La copa de champán vacía (la «coupe»), con su tallo y su pie, y una sola burbuja que quedó.
            drawPath(Path().apply { moveTo(f(0.16f), f(0.26f)); quadraticTo(f(0.18f), f(0.52f), f(0.5f), f(0.54f)); quadraticTo(f(0.82f), f(0.52f), f(0.84f), f(0.26f)); close() }, l, style = tr)
            drawLine(l, Offset(f(0.5f), f(0.54f)), Offset(f(0.5f), f(0.86f)), tr.width)
            drawOval(l, Offset(f(0.32f), f(0.84f)), Size(f(0.36f), f(0.08f)), style = tr)
            drawCircle(a, f(0.035f), Offset(f(0.56f), f(0.16f)), style = Stroke(tr.width * 0.8f))
        }
        TemaId.BOTANICO -> {
            // Frasco de especímenes vacío, con su corcho y la etiqueta.
            drawRoundRect(l, Offset(f(0.22f), f(0.26f)), Size(f(0.56f), f(0.66f)), CornerRadius(f(0.1f)), style = tr)
            drawRect(a, Offset(f(0.3f), f(0.1f)), Size(f(0.4f), f(0.14f)), style = tr)
            drawRect(l, Offset(f(0.32f), f(0.5f)), Size(f(0.36f), f(0.18f)), style = Stroke(tr.width * 0.6f))
        }
        TemaId.RUINAS -> {
            // Una lata vacía, abierta, de la que asoma un brote.
            drawRect(l, Offset(f(0.3f), f(0.36f)), Size(f(0.4f), f(0.52f)), style = tr)
            drawOval(l, Offset(f(0.3f), f(0.31f)), Size(f(0.4f), f(0.1f)), style = tr)
            drawLine(l, Offset(f(0.3f), f(0.55f)), Offset(f(0.7f), f(0.55f)), tr.width * 0.5f)
            drawLine(l, Offset(f(0.3f), f(0.7f)), Offset(f(0.7f), f(0.7f)), tr.width * 0.5f)
            drawLine(a, Offset(f(0.5f), f(0.36f)), Offset(f(0.52f), f(0.12f)), tr.width)
            rotate(-35f, Offset(f(0.52f), f(0.2f))) { drawOval(a, Offset(f(0.52f), f(0.16f)), Size(f(0.18f), f(0.08f))) }
            rotate(30f, Offset(f(0.51f), f(0.26f))) { drawOval(a, Offset(f(0.33f), f(0.22f)), Size(f(0.18f), f(0.08f))) }
        }
        TemaId.NAVIDAD -> {
            // Calcetín colgado, sin regalos.
            drawPath(Path().apply {
                moveTo(f(0.34f), f(0.16f)); lineTo(f(0.66f), f(0.16f)); lineTo(f(0.66f), f(0.6f)); quadraticTo(f(0.66f), f(0.9f), f(0.36f), f(0.9f))
                lineTo(f(0.2f), f(0.9f)); quadraticTo(f(0.1f), f(0.78f), f(0.24f), f(0.7f)); lineTo(f(0.34f), f(0.62f)); close()
            }, l, style = tr)
            drawRect(a, Offset(f(0.3f), f(0.06f)), Size(f(0.4f), f(0.12f)))
        }
        TemaId.SEDA -> {
            // Cuenco vacío con sus palillos.
            drawArc(l, 0f, 180f, true, Offset(f(0.12f), f(0.2f)), Size(f(0.76f), f(0.6f)), style = tr)
            drawLine(l, Offset(f(0.36f), f(0.88f)), Offset(f(0.64f), f(0.88f)), tr.width)
            drawLine(a, Offset(f(0.56f), f(0.06f)), Offset(f(0.9f), f(0.44f)), tr.width)
            drawLine(a, Offset(f(0.66f), f(0.04f)), Offset(f(0.96f), f(0.38f)), tr.width)
        }
        TemaId.EDO -> {
            // Un farolillo de papel apagado: el cuerpo con sus costillas, las tapas y el gancho.
            drawOval(l, Offset(f(0.24f), f(0.18f)), Size(f(0.52f), f(0.66f)), style = tr)
            for (k in 1..4) {
                val y = f(0.18f + 0.66f * k / 5f); val dy = (y - f(0.51f)) / f(0.33f)
                val media = f(0.26f) * kotlin.math.sqrt(maxOf(0f, 1f - dy * dy))
                drawLine(l, Offset(f(0.5f) - media, y), Offset(f(0.5f) + media, y), tr.width * 0.6f)
            }
            drawRect(a, Offset(f(0.36f), f(0.12f)), Size(f(0.28f), f(0.07f)))
            drawRect(a, Offset(f(0.36f), f(0.83f)), Size(f(0.28f), f(0.07f)))
            drawLine(a, Offset(f(0.5f), f(0.02f)), Offset(f(0.5f), f(0.12f)), tr.width)
        }
        TemaId.GOTICO -> {
            // Vela apagada en su palmatoria, con un hilo de humo.
            drawRect(l, Offset(f(0.4f), f(0.3f)), Size(f(0.2f), f(0.48f)), style = tr)
            drawLine(l, Offset(f(0.5f), f(0.3f)), Offset(f(0.52f), f(0.2f)), tr.width)
            drawOval(a, Offset(f(0.2f), f(0.76f)), Size(f(0.6f), f(0.14f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.52f), f(0.18f)); quadraticTo(f(0.62f), f(0.1f), f(0.54f), f(0.03f)) }, l.copy(alpha = 0.5f), style = tr)
        }
        TemaId.ARCANO -> {
            // Caja de archivo abierta y vacía, con su ficha caída y una telaraña en la esquina.
            drawPath(Path().apply { moveTo(f(0.1f), f(0.4f)); lineTo(f(0.9f), f(0.4f)); lineTo(f(0.84f), f(0.92f)); lineTo(f(0.16f), f(0.92f)); close() }, l, style = tr)
            drawPath(Path().apply { moveTo(f(0.1f), f(0.4f)); lineTo(f(0.22f), f(0.22f)); lineTo(f(0.98f), f(0.22f)); lineTo(f(0.9f), f(0.4f)) }, l, style = tr)
            rotate(-14f, Offset(f(0.5f), f(0.66f))) { drawRect(a, Offset(f(0.36f), f(0.6f)), Size(f(0.28f), f(0.14f)), style = Stroke(tr.width * 0.7f)) }
            val t = Offset(f(0.84f), f(0.44f))
            listOf(180f, 225f, 270f).forEach { g -> val r = Math.toRadians(g.toDouble()); drawLine(l.copy(alpha = 0.5f), t, Offset(t.x + f(0.2f) * kotlin.math.cos(r).toFloat(), t.y - f(0.2f) * kotlin.math.sin(r).toFloat()), tr.width * 0.4f) }
            drawArc(l.copy(alpha = 0.5f), 90f, 90f, false, Offset(t.x - f(0.12f), t.y), Size(f(0.24f), f(0.24f)), style = Stroke(tr.width * 0.4f))
        }
        TemaId.ESPACIO -> {
            // Órbita sin planeta.
            drawOval(l, Offset(f(0.06f), f(0.3f)), Size(f(0.88f), f(0.4f)), style = Stroke(tr.width, pathEffect = PathEffect.dashPathEffect(floatArrayOf(f(0.06f), f(0.05f)))))
            drawCircle(a, f(0.1f), Offset(f(0.5f), f(0.5f)), style = tr)
            estrellaComp(Offset(f(0.82f), f(0.16f)), f(0.08f), l, puntas = 4, dentro = 0.35f)
        }
        TemaId.ABISMO -> {
            // La pantalla del sonar sin un solo eco: anillos, la cruz y el haz que barre en vano.
            val c = Offset(f(0.5f), f(0.5f))
            drawCircle(l, f(0.44f), c, style = tr)
            drawCircle(l.copy(alpha = 0.5f), f(0.29f), c, style = Stroke(tr.width, pathEffect = PathEffect.dashPathEffect(floatArrayOf(f(0.04f), f(0.04f)))))
            drawCircle(l.copy(alpha = 0.5f), f(0.14f), c, style = Stroke(tr.width, pathEffect = PathEffect.dashPathEffect(floatArrayOf(f(0.03f), f(0.03f)))))
            drawLine(l.copy(alpha = 0.4f), Offset(f(0.06f), f(0.5f)), Offset(f(0.94f), f(0.5f)), tr.width * 0.7f)
            drawLine(l.copy(alpha = 0.4f), Offset(f(0.5f), f(0.06f)), Offset(f(0.5f), f(0.94f)), tr.width * 0.7f)
            drawLine(a, c, Offset(f(0.81f), f(0.19f)), tr.width * 1.4f)
            drawCircle(a, f(0.035f), c)
        }
        TemaId.ANDALUS -> {
            // Arco de herradura sin nadie bajo él.
            drawArc(l, 160f, 220f, false, Offset(f(0.2f), f(0.1f)), Size(f(0.6f), f(0.6f)), style = tr)
            drawLine(l, Offset(f(0.22f), f(0.5f)), Offset(f(0.22f), f(0.92f)), tr.width)
            drawLine(l, Offset(f(0.78f), f(0.5f)), Offset(f(0.78f), f(0.92f)), tr.width)
            drawLine(a, Offset(f(0.12f), f(0.92f)), Offset(f(0.88f), f(0.92f)), tr.width)
        }
        TemaId.MESO -> {
            // El cartucho de un glifo, sobre su pie de tres lóbulos, sin nada pintado adentro.
            drawRoundRect(l, Offset(f(0.18f), f(0.14f)), Size(f(0.64f), f(0.58f)), CornerRadius(f(0.14f)), style = tr)
            for (k in -1..1) drawCircle(a, f(0.07f), Offset(f(0.5f + k * 0.15f), f(0.8f)), style = tr)
            drawLine(l, Offset(f(0.3f), f(0.94f)), Offset(f(0.7f), f(0.94f)), tr.width)
        }
        TemaId.MONGOL -> {
            // Ger sin fieltro: el muro de celosía y la cúpula.
            drawPath(Path().apply { moveTo(f(0.1f), f(0.86f)); lineTo(f(0.1f), f(0.52f)); quadraticTo(f(0.5f), f(0.08f), f(0.9f), f(0.52f)); lineTo(f(0.9f), f(0.86f)); close() }, l, style = tr)
            for (n in 0 until 4) {
                val x0 = f(0.1f + n * 0.2f)
                drawLine(l.copy(alpha = 0.5f), Offset(x0, f(0.86f)), Offset(x0 + f(0.2f), f(0.52f)), tr.width * 0.6f)
            }
            drawCircle(a, f(0.07f), Offset(f(0.5f), f(0.3f)), style = tr)
        }
        TemaId.RENACIMIENTO -> {
            // Hoja en blanco y un compás abierto.
            rotate(-6f) { drawRect(l, Offset(f(0.14f), f(0.12f)), Size(f(0.56f), f(0.76f)), style = tr) }
            drawLine(a, Offset(f(0.72f), f(0.14f)), Offset(f(0.56f), f(0.9f)), tr.width)
            drawLine(a, Offset(f(0.72f), f(0.14f)), Offset(f(0.92f), f(0.88f)), tr.width)
            drawCircle(a, f(0.05f), Offset(f(0.72f), f(0.14f)))
        }
        TemaId.PIRATA -> {
            // Mar en calma: una isla pequeña con su palmera, dos olas y, en la arena, la «X» que aún no se ha marcado.
            drawPath(Path().apply { moveTo(f(0.18f), f(0.72f)); quadraticTo(f(0.5f), f(0.52f), f(0.82f), f(0.72f)) }, l, style = tr)
            drawPath(Path().apply { moveTo(f(0.52f), f(0.64f)); quadraticTo(f(0.5f), f(0.4f), f(0.58f), f(0.24f)) }, l, style = tr)
            listOf(-0.2f, 0.06f, 0.24f).forEach { dx ->
                drawPath(Path().apply { moveTo(f(0.58f), f(0.24f)); quadraticTo(f(0.58f + dx * 0.6f), f(0.14f), f(0.58f + dx), f(0.3f)) }, l, style = tr)
            }
            listOf(0.84f, 0.94f).forEach { y ->
                drawPath(Path().apply { moveTo(f(0.1f), f(y)); quadraticTo(f(0.2f), f(y - 0.05f), f(0.3f), f(y)); quadraticTo(f(0.4f), f(y + 0.05f), f(0.5f), f(y)); quadraticTo(f(0.6f), f(y - 0.05f), f(0.7f), f(y)); quadraticTo(f(0.8f), f(y + 0.05f), f(0.9f), f(y)) }, l.copy(alpha = 0.5f), style = tr)
            }
            val x = Offset(f(0.38f), f(0.64f)); val d = f(0.04f)
            drawLine(a.copy(alpha = 0.5f), x - Offset(d, d), x + Offset(d, d), tr.width, StrokeCap.Round)
            drawLine(a.copy(alpha = 0.5f), x + Offset(-d, d), x + Offset(d, -d), tr.width, StrokeCap.Round)
        }
        TemaId.URSS -> {
            // Estrella de cartel sin pintar, sobre su barra.
            estrellaComp(Offset(f(0.5f), f(0.48f)), f(0.42f), a, estilo = tr)
            drawLine(l, Offset(f(0.1f), f(0.95f)), Offset(f(0.9f), f(0.95f)), tr.width * 1.6f)
        }
        TemaId.REVOLUCION -> {
            // Urna sin papeletas, con la escarapela.
            drawRect(l, Offset(f(0.16f), f(0.34f)), Size(f(0.68f), f(0.56f)), style = tr)
            drawLine(a, Offset(f(0.38f), f(0.34f)), Offset(f(0.62f), f(0.34f)), tr.width * 1.8f)
            val c = Offset(f(0.5f), f(0.62f))
            drawCircle(e.secondary, f(0.1f), c); drawCircle(Color.White, f(0.065f), c); drawCircle(e.primary, f(0.03f), c)
        }
        TemaId.ANTIGUAS -> {
            // Cofre abierto y vacío: la tapa levantada y la gema de la cerradura.
            drawRect(l, Offset(f(0.12f), f(0.5f)), Size(f(0.76f), f(0.38f)), style = tr)
            drawArc(l, 180f, 180f, false, Offset(f(0.12f), f(0.08f)), Size(f(0.76f), f(0.36f)), style = tr)
            drawLine(l, Offset(f(0.12f), f(0.26f)), Offset(f(0.88f), f(0.26f)), tr.width)
            romboComp(Offset(f(0.5f), f(0.62f)), f(0.06f), f(0.08f), a)
        }
        TemaId.CUEVA -> {
            // La bolsa de piel, abierta y vacía, con su cordón suelto y tres puntos de ocre.
            drawPath(Path().apply { moveTo(f(0.24f), f(0.34f)); quadraticTo(f(0.06f), f(0.9f), f(0.5f), f(0.92f)); quadraticTo(f(0.94f), f(0.9f), f(0.76f), f(0.34f)) }, l, style = tr)
            drawOval(l, Offset(f(0.22f), f(0.24f)), Size(f(0.56f), f(0.16f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.7f), f(0.36f)); quadraticTo(f(0.86f), f(0.5f), f(0.8f), f(0.62f)) }, a, style = tr)
            for (k in 0 until 3) drawCircle(a, f(0.025f), Offset(f(0.36f + k * 0.14f), f(0.66f)))
        }
    }
}

/** La marca del color de cada valor en una casilla de la escala ([fondo] es el de la casilla). */
private fun DrawScope.marcaEscala(t: TemaId, c: Color, fondo: Color) {
    val w = size.width; val h = size.height; val d = 4.dp.toPx()
    when (t) {
        TemaId.ROMANO -> drawRect(c, Offset(0f, h - d), Size(w, d))
        // El agujero de la picadora del boleto, arriba a la derecha, y una raya de tinta abajo.
        TemaId.URBE -> { drawCircle(c, d * 0.85f, Offset(w - d * 1.7f, d * 1.7f)); drawRect(c, Offset(0f, h - d * 0.5f), Size(w, d * 0.5f)) }
        TemaId.CIBER -> drawRect(c, Offset.Zero, Size(d * 0.75f, h))
        TemaId.NEON -> { drawRect(c.copy(alpha = c.alpha * 0.3f), Offset(0f, h - d * 2f), Size(w, d * 2f)); drawRect(c, Offset(0f, h - d * 0.9f), Size(w, d * 0.6f)) }   // el tubo encendido con su halo
        TemaId.HELENO -> drawPath(Path().apply { moveTo(w / 2 - d * 1.6f, h); lineTo(w / 2, h - d * 1.4f); lineTo(w / 2 + d * 1.6f, h); close() }, c)
        // Una pirámide pequeña con su piramidión de oro.
        TemaId.EGIPTO -> {
            drawPath(Path().apply { moveTo(w / 2 - d * 1.8f, h); lineTo(w / 2, h - d * 2f); lineTo(w / 2 + d * 1.8f, h); close() }, c)
            drawPath(Path().apply { moveTo(w / 2 - d * 0.45f, h - d * 1.5f); lineTo(w / 2, h - d * 2f); lineTo(w / 2 + d * 0.45f, h - d * 1.5f); close() }, fondo.copy(alpha = 0.5f))
        }
        TemaId.MEDIEVAL -> drawCircle(c, d * 0.8f, Offset(w / 2, d * 1.4f))
        TemaId.TEMPLARIO -> drawPath(cruzPatada(Offset(w / 2, d * 1.6f), d * 1.2f), c)
        TemaId.DISTOPIA -> drawRect(c, Offset.Zero, Size(w, d))
        TemaId.REFUGIO -> drawCircle(c, d * 0.9f, Offset(w / 2, h - d * 1.6f), style = Stroke(d * 0.5f))
        TemaId.ORDEN -> drawPath(Path().apply { moveTo(w / 2 - d * 1.5f, h - d * 0.6f); lineTo(w / 2, h - d * 1.8f); lineTo(w / 2 + d * 1.5f, h - d * 0.6f) }, c, style = Stroke(d * 0.55f))
        // La aguja de un manómetro que sube desde la esquina de abajo.
        TemaId.VAPOR -> { drawLine(c, Offset(d, h - d), Offset(d * 3.2f, h - d * 3.2f), d * 0.5f); drawCircle(c, d * 0.6f, Offset(d, h - d)) }
        TemaId.LABORATORIO -> {
            for (n in -1..1) {
                val x = w / 2 + n * d * 1.4f
                drawLine(c, Offset(x, h), Offset(x, h - d * (if (n == 0) 2.2f else 1.3f)), d * 0.4f)
            }
        }
        TemaId.COLONOS -> {
            drawRect(c, Offset(d, h - d * 1.1f), Size(w - 2 * d, d * 0.35f))
            drawRect(c, Offset(d, h - d * 0.5f), Size(w - 2 * d, d * 0.35f))
        }
        TemaId.NAPOLEON -> romboComp(Offset(w / 2, d * 1.3f), d * 0.8f, d, c)
        // Un abanico chico arriba al centro, con su raya de oro debajo.
        TemaId.DECO -> {
            drawArc(c, 180f, 180f, true, Offset(w / 2 - d * 1.3f, d * 0.2f), Size(d * 2.6f, d * 2.6f))
            drawLine(fondo, Offset(w / 2, d * 1.5f), Offset(w / 2, d * 0.3f), d * 0.22f)
            drawRect(c, Offset(d, h - d * 0.45f), Size(w - 2 * d, d * 0.45f))
        }
        TemaId.BOTANICO -> rotate(-35f, Offset(w - d * 2.2f, h - d * 1.5f)) { drawOval(c, Offset(w - d * 3.4f, h - d * 2f), Size(d * 2.4f, d)) }
        TemaId.RUINAS -> {
            // Un semáforo diminuto arriba al centro: la caja y sus tres luces, la de abajo encendida.
            drawRoundRect(c, Offset(w / 2 - d * 0.9f, 0f), Size(d * 1.8f, d * 4.4f), CornerRadius(d * 0.5f))
            for (k in 0..2) drawCircle(if (k == 2) fondo else fondo.copy(alpha = 0.35f), d * 0.5f, Offset(w / 2, d * (0.8f + 1.4f * k)))
        }
        TemaId.NAVIDAD -> {
            drawLine(c, Offset(w / 2, 0f), Offset(w / 2, d * 1.2f), d * 0.3f)
            drawCircle(c, d * 0.75f, Offset(w / 2, d * 1.9f))
        }
        TemaId.SEDA -> {
            drawCircle(c, d * 0.95f, Offset(w / 2, h - d * 1.4f))
            drawRect(fondo, Offset(w / 2 - d * 0.3f, h - d * 1.7f), Size(d * 0.6f, d * 0.6f))
        }
        // Un sellito cuadrado en la esquina, como el que se estampa al margen.
        TemaId.EDO -> {
            drawRect(c, Offset(w - d * 1.9f, d * 0.5f), Size(d * 1.4f, d * 1.4f))
            drawRect(fondo, Offset(w - d * 1.65f, d * 0.75f), Size(d * 0.9f, d * 0.9f), style = Stroke(d * 0.18f))
        }
        TemaId.GOTICO -> drawPath(Path().apply {
            moveTo(w / 2 - d, h); lineTo(w / 2 - d, h - d * 1.2f); quadraticTo(w / 2 - d, h - d * 2.2f, w / 2, h - d * 2.6f)
            quadraticTo(w / 2 + d, h - d * 2.2f, w / 2 + d, h - d * 1.2f); lineTo(w / 2 + d, h); close()
        }, c)
        // Una raya de tachón sobre la marca, como una cifra corregida.
        TemaId.ARCANO -> { drawCircle(c, d * 0.8f, Offset(w / 2, h - d * 1.4f)); drawLine(c, Offset(w / 2 - d * 1.6f, h - d * 0.6f), Offset(w / 2 + d * 1.6f, h - d * 2.2f), d * 0.35f) }
        TemaId.ESPACIO -> estrellaComp(Offset(w / 2, d * 1.5f), d * 0.95f, c, puntas = 4, dentro = 0.35f)
        // El escandallo: la plomada de sondeo colgada de su cabo.
        TemaId.ABISMO -> {
            drawLine(c, Offset(w / 2, 0f), Offset(w / 2, d * 1.2f), d * 0.3f)
            drawPath(Path().apply { moveTo(w / 2, d * 0.9f); quadraticTo(w / 2 + d * 0.9f, d * 2.2f, w / 2, d * 2.9f); quadraticTo(w / 2 - d * 0.9f, d * 2.2f, w / 2, d * 0.9f); close() }, c)
        }
        TemaId.ANDALUS -> estrellaComp(Offset(w / 2, h - d * 1.4f), d, c, puntas = 8, dentro = 0.6f)
        // Un punto sobre una barra: el seis maya en pequeño.
        TemaId.MESO -> { drawRoundRect(c, Offset(w / 2 - d * 1.5f, h - d * 0.8f), Size(d * 3f, d * 0.8f), CornerRadius(d * 0.3f)); drawCircle(c, d * 0.55f, Offset(w / 2, h - d * 1.9f)) }
        TemaId.MONGOL -> drawArc(c, 180f, 180f, true, Offset(w / 2 - d * 1.3f, h - d * 1.3f), Size(d * 2.6f, d * 2.6f))
        TemaId.RENACIMIENTO -> {
            for (n in 0 until 3) {
                val o = d * 0.6f + n * d * 0.8f
                drawLine(c, Offset(o, h), Offset(o + d * 1.6f, h - d * 1.6f), d * 0.3f)
            }
        }
        // La «X» del tesoro, en la esquina de abajo.
        TemaId.PIRATA -> {
            val x = Offset(w - d * 1.6f, h - d * 1.6f)
            drawLine(c, x - Offset(d, d), x + Offset(d, d), d * 0.45f, StrokeCap.Round)
            drawLine(c, x + Offset(-d, d), x + Offset(d, -d), d * 0.45f, StrokeCap.Round)
        }
        TemaId.URSS -> drawPath(Path().apply { moveTo(w, h - d * 3f); lineTo(w, h); lineTo(w - d * 3f, h); close() }, c)
        TemaId.REVOLUCION -> drawRect(c, Offset(w - d, 0f), Size(d, h))
        TemaId.ANTIGUAS -> {
            // Una runa inventada: asta con dos ramas.
            val x = w / 2
            drawLine(c, Offset(x, h), Offset(x, h - d * 2.2f), d * 0.4f)
            drawLine(c, Offset(x, h - d * 1.3f), Offset(x - d * 0.8f, h - d * 2.1f), d * 0.4f)
            drawLine(c, Offset(x, h - d * 1.3f), Offset(x + d * 0.8f, h - d * 2.1f), d * 0.4f)
        }
        // Una mano pequeña de ocre, en positivo.
        TemaId.CUEVA -> drawPath(manoUnida(Offset(w / 2, h - d * 1.2f), d * 1.1f), c)
    }
}

/** El adorno del botón principal, en [c] (el color del texto, tenue). Queda dentro de la forma del tema. */
private fun DrawScope.adornoBoton(t: TemaId, c: Color, e: ColorScheme) {
    val w = size.width; val h = size.height; val m = 4.dp.toPx(); val fino = 1.dp.toPx()
    when (t) {
        // Filete interior, como el marco de una inscripción.
        TemaId.ROMANO -> drawRect(c, Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(fino * 0.8f))
        // La clave del arco del portón, arriba al centro, y el umbral abajo.
        TemaId.URBE -> {
            drawPath(Path().apply { moveTo(w / 2 - m * 0.9f, 0f); lineTo(w / 2 + m * 0.9f, 0f); lineTo(w / 2 + m * 0.55f, m * 1.5f); lineTo(w / 2 - m * 0.55f, m * 1.5f); close() }, c)
            drawLine(c, Offset(m * 2, h - m * 0.7f), Offset(w - m * 2, h - m * 0.7f), fino)
        }
        // Un led a la derecha y la pista abajo.
        TemaId.CIBER -> {
            drawRect(c, Offset(w - m * 2.6f, h / 2 - m * 0.8f), Size(m * 0.9f, m * 1.6f))
            drawLine(c, Offset(m * 2.5f, h - m), Offset(w * 0.4f, h - m), fino)
        }
        // Dos grapas de letrero a los lados y un brillo de neón bajo el texto.
        TemaId.NEON -> {
            drawRect(c, Offset(m * 0.8f, h / 2 - m), Size(fino * 1.6f, m * 2f))
            drawRect(c, Offset(w - m * 0.8f - fino * 1.6f, h / 2 - m), Size(fino * 1.6f, m * 2f))
            drawLine(c.copy(alpha = 0.35f), Offset(w * 0.25f, h - m * 0.8f), Offset(w * 0.75f, h - m * 0.8f), fino * 2.4f, cap = StrokeCap.Round)
        }
        // El arquitrabe bajo el frontón.
        TemaId.HELENO -> drawLine(c, Offset(w * 0.06f, h * 0.24f), Offset(w * 0.94f, h * 0.24f), fino)
        // La barra del nudo del cartucho, a la derecha, y la cuerda por dentro del borde.
        TemaId.EGIPTO -> {
            drawRect(c, Offset(w - m * 1.1f, m * 0.6f), Size(fino * 2.2f, h - m * 1.2f))
            drawLine(c.copy(alpha = c.alpha * 0.6f), Offset(h / 2, m * 0.8f), Offset(w - m * 1.6f, m * 0.8f), fino * 0.8f)
            drawLine(c.copy(alpha = c.alpha * 0.6f), Offset(h / 2, h - m * 0.8f), Offset(w - m * 1.6f, h - m * 0.8f), fino * 0.8f)
        }
        // Cuatro puntos de oro junto a las esquinas cóncavas.
        TemaId.MEDIEVAL -> listOf(Offset(m * 2f, m * 2f), Offset(w - m * 2f, m * 2f), Offset(m * 2f, h - m * 2f), Offset(w - m * 2f, h - m * 2f)).forEach { drawCircle(e.tertiary, fino * 1.6f, it) }
        // La cruz junto al asta del gallardete y una costura de malla a lo largo.
        TemaId.TEMPLARIO -> { drawPath(cruzPatada(Offset(m * 3f, h / 2), m * 1.2f), c); var x = m * 6f; while (x < w - h * 0.4f) { drawCircle(c, fino * 1.1f, Offset(x, h - m), style = Stroke(fino * 0.6f)); x += maxOf(m * 1.4f, 1f) } }
        // Doble recuadro de sello de goma.
        TemaId.DISTOPIA -> {
            drawRect(c, Offset(m * 0.7f, m * 0.7f), Size(w - m * 1.4f, h - m * 1.4f), style = Stroke(fino))
            drawRect(c, Offset(m * 1.4f, m * 1.4f), Size(w - m * 2.8f, h - m * 2.8f), style = Stroke(fino * 0.6f))
        }
        // Un destello atómico a la izquierda.
        TemaId.REFUGIO -> estrellaComp(Offset(m * 3.2f, h / 2), m * 1.1f, c, puntas = 4, dentro = 0.3f)
        // Remaches a los lados.
        TemaId.ORDEN -> listOf(m * 2f, w - m * 2f).forEach { x -> drawCircle(c, fino * 2f, Offset(x, h / 2)) }
        // Dos filetes arriba y abajo, como en los billetes de tren de cartón, y una muesca de picado a la derecha.
        TemaId.VAPOR -> {
            listOf(m, h - m).forEach { y -> drawLine(c, Offset(m * 2f, y), Offset(w - m * 2f, y), fino * 0.7f) }
            drawCircle(c, fino * 2.2f, Offset(w - m * 1.6f, h / 2), style = Stroke(fino * 0.8f))
        }
        // Graduación por abajo.
        TemaId.LABORATORIO -> {
            var x = m * 2; var n = 0
            while (x < w - m * 2) { drawLine(c, Offset(x, h - m), Offset(x, h - m - (if (n % 5 == 0) m else m * 0.5f)), fino * 0.8f); x += m * 1.5f; n++ }
        }
        // Filetes de imprenta arriba y abajo.
        TemaId.COLONOS -> {
            drawLine(c, Offset(m * 3, m), Offset(w - m * 3, m), fino)
            drawLine(c, Offset(m * 3, h - m), Offset(w - m * 3, h - m), fino)
        }
        // Estrellas de oro en las puntas.
        TemaId.NAPOLEON -> listOf(m * 3.2f, w - m * 3.2f).forEach { x -> estrellaComp(Offset(x, h / 2), m * 0.9f, e.tertiary) }
        // Tres barras de velocidad en cada punta, cada una más corta, como en los rótulos de los trenes de lujo.
        TemaId.DECO -> listOf(1f, -1f).forEach { sg ->
            for (k in 0..2) {
                val x = if (sg > 0) m * (1.6f + k * 0.9f) else w - m * (1.6f + k * 0.9f)
                val alto = h * (0.5f - k * 0.12f)
                drawLine(c, Offset(x, h / 2 - alto / 2), Offset(x, h / 2 + alto / 2), fino * 1.3f)
            }
        }
        // Nervaduras en las dos puntas de la hoja.
        TemaId.BOTANICO -> {
            drawLine(c, Offset(m * 1.5f, h - m * 1.5f), Offset(m * 4.5f, h - m * 3.2f), fino)
            drawLine(c, Offset(w - m * 1.5f, m * 1.5f), Offset(w - m * 4.5f, m * 3.2f), fino)
        }
        // Una grieta que baja desde el borde de arriba y una mancha de musgo abajo a la izquierda.
        TemaId.RUINAS -> {
            drawPath(Path().apply { moveTo(w - m * 3f, 0f); lineTo(w - m * 3.4f, m * 1.2f); lineTo(w - m * 2.7f, m * 1.9f); lineTo(w - m * 3.1f, m * 3f) }, c, style = Stroke(fino))
            drawOval(e.primary.copy(alpha = 0.35f), Offset(m * 0.6f, h - m * 1.6f), Size(m * 3.2f, m * 1.1f))
            drawOval(e.primary.copy(alpha = 0.25f), Offset(m * 2.4f, h - m * 1.2f), Size(m * 1.8f, m * 0.8f))
        }
        // Nieve en el borde de arriba.
        TemaId.NAVIDAD -> {
            var x = m * 2.5f
            while (x < w - m * 2) { drawCircle(Color.White.copy(alpha = 0.75f), m * 0.55f, Offset(x, m * 0.9f)); x += m * 2.6f }
        }
        // Una moneda con agujero cuadrado a cada lado.
        TemaId.SEDA -> listOf(m * 3.4f, w - m * 3.4f).forEach { x ->
            drawCircle(c, m * 1.1f, Offset(x, h / 2 + m * 0.4f))
            drawRect(e.primary, Offset(x - m * 0.35f, h / 2 + m * 0.05f), Size(m * 0.7f, m * 0.7f))
        }
        // Dos nudos de cordón con sus borlas, como los que cierran una caja de madera.
        TemaId.EDO -> listOf(m * 3.4f, w - m * 3.4f).forEach { x ->
            drawCircle(c, m * 0.85f, Offset(x, h / 2 - m * 0.4f), style = Stroke(fino))
            drawLine(c, Offset(x, h / 2 + m * 0.45f), Offset(x - m * 0.45f, h / 2 + m * 1.7f), fino)
            drawLine(c, Offset(x, h / 2 + m * 0.45f), Offset(x + m * 0.45f, h / 2 + m * 1.7f), fino)
        }
        // Trébol de tracería bajo la clave del arco.
        TemaId.GOTICO -> {
            val cc = Offset(w / 2, h * 0.2f); val r = m * 0.45f
            listOf(Offset(0f, -r), Offset(-r * 0.9f, r * 0.6f), Offset(r * 0.9f, r * 0.6f)).forEach { o -> drawCircle(c, r, cc + o, style = Stroke(fino * 0.8f)) }
        }
        // El doblez de la ficha y un ojal con su cordel.
        TemaId.ARCANO -> {
            val dd = minOf(h * 0.32f, w * 0.12f)
            drawPath(Path().apply { moveTo(w - dd, 0f); lineTo(w - dd, dd); lineTo(w, dd) }, c, style = Stroke(fino))
            drawCircle(c, m * 0.6f, Offset(m * 2.6f, h / 2), style = Stroke(fino))
            drawLine(c.copy(alpha = c.alpha * 0.6f), Offset(m * 2.6f, h / 2 + m * 0.6f), Offset(m * 1.4f, h - fino), fino * 0.8f)
        }
        // Dos luces de consola y una línea de estado.
        TemaId.ESPACIO -> {
            listOf(m * 3f, w - m * 3f).forEach { x -> drawCircle(c, fino * 1.6f, Offset(x, h / 2)) }
            drawLine(c, Offset(w * 0.3f, h - m), Offset(w * 0.7f, h - m), fino)
        }
        // Tres burbujas que suben a la derecha y la ola abajo.
        TemaId.ABISMO -> {
            listOf(0.72f to 1.2f, 0.5f to 1.7f, 0.28f to 2.2f).forEach { (fy, r) -> drawCircle(c, fino * r, Offset(w - m * (2.2f + (fy * 2f)), h * fy), style = Stroke(fino * 0.8f)) }
            val ola = Path(); var x = m * 2f; ola.moveTo(x, h - m)
            while (x < w * 0.6f) { ola.lineTo(x, h - m + fino * 1.2f * sin(x / (m * 0.8f))); x += maxOf(fino, 1f) }
            drawPath(ola, c, style = Stroke(fino))
        }
        // El trasdós del arco, por dentro.
        TemaId.ANDALUS -> {
            val r = h / 2 - m
            val p = Path().apply {
                moveTo(m, h - m); lineTo(m, h / 2)
                arcTo(Rect(m, m, m + 2 * r, m + 2 * r), 180f, 90f, false)
                lineTo(w - m - r, m)
                arcTo(Rect(w - m - 2 * r, m, w - m, m + 2 * r), 270f, 90f, false)
                lineTo(w - m, h - m)
            }
            drawPath(p, c, style = Stroke(fino))
        }
        // El filete interior de la bóveda: sube en diagonal por los dos lados y abajo lleva un renglón de puntos.
        TemaId.MESO -> {
            val sube = h * 0.32f
            val p = Path().apply { moveTo(m, h - m); lineTo(m, m + sube); lineTo(m + sube, m); lineTo(w - m - sube, m); lineTo(w - m, m + sube); lineTo(w - m, h - m) }
            drawPath(p, c, style = Stroke(fino))
            var x = m * 3; while (x < w - m * 3) { drawCircle(c, fino * 0.9f, Offset(x, h - m * 1.6f)); x += maxOf(m * 2, 1f) }
        }
        // Costura de fieltro.
        TemaId.MONGOL -> drawRoundRect(c, Offset(m, m), Size(w - 2 * m, h - 2 * m), CornerRadius(m * 1.2f), style = Stroke(fino, pathEffect = PathEffect.dashPathEffect(floatArrayOf(m * 1.5f, m))))
        // La esquina doblada de la hoja.
        TemaId.RENACIMIENTO -> {
            val s = 12.dp.toPx()
            drawPath(Path().apply { moveTo(w - s, h); lineTo(w - s, h - s); lineTo(w, h - s); close() }, c)
        }
        // Los dos aros de latón del barril, con sus clavos.
        TemaId.PIRATA -> listOf(m * 2.2f, w - m * 2.2f).forEach { x ->
            drawLine(c, Offset(x, h * 0.14f), Offset(x, h * 0.86f), fino * 1.6f)
            drawCircle(c, fino * 1.3f, Offset(x, h * 0.3f)); drawCircle(c, fino * 1.3f, Offset(x, h * 0.7f))
        }
        // Rayas de cartel paralelas al corte.
        TemaId.URSS -> listOf(4.dp.toPx(), 7.5.dp.toPx()).forEach { dd -> drawLine(c, Offset(w - 0.3f * m - dd, m), Offset(w - 0.3f * (h - m) - dd, h - m), fino * 1.6f) }
        // Escarapela a la izquierda.
        TemaId.REVOLUCION -> {
            val cc = Offset(m * 3f, h / 2)
            drawCircle(e.secondary, m * 1.3f, cc); drawCircle(Color.White, m * 0.85f, cc); drawCircle(c, m * 0.4f, cc)
        }
        // Gemas engastadas a los lados.
        TemaId.ANTIGUAS -> listOf(m * 2.6f, w - m * 2.6f).forEach { x -> romboComp(Offset(x, h / 2), m * 0.7f, m * 1.1f, c) }
        // Dos puntos de ocre a cada lado, como los que flanquean los animales pintados.
        TemaId.CUEVA -> listOf(m * 2.4f, w - m * 2.4f).forEach { x -> drawCircle(c, m * 0.38f, Offset(x, h / 2 - m * 0.5f)); drawCircle(c, m * 0.38f, Offset(x, h / 2 + m * 0.5f)) }
    }
}

/** El ícono de la barra del editor: con qué se escribe en cada tema. */
private fun DrawScope.iconoEditor(t: TemaId, e: ColorScheme) {
    val s = size.minDimension
    fun f(v: Float) = v * s
    val a = e.primary; val l = e.onSurfaceVariant
    val tr = Stroke(1.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (t) {
        TemaId.ROMANO -> {
            // Tablilla de cera y estilo.
            drawRect(l, Offset(f(0.06f), f(0.18f)), Size(f(0.78f), f(0.64f)), style = tr)
            drawRect(a.copy(alpha = 0.5f), Offset(f(0.18f), f(0.3f)), Size(f(0.54f), f(0.4f)))
            drawLine(a, Offset(f(0.6f), f(0.98f)), Offset(f(0.98f), f(0.2f)), tr.width)
        }
        TemaId.URBE -> {
            // El blocchetto de espiral y el bolígrafo del quiosco.
            drawRect(l, Offset(f(0.08f), f(0.2f)), Size(f(0.6f), f(0.74f)), style = tr)
            for (k in 0 until 4) drawCircle(l, f(0.04f), Offset(f(0.18f + k * 0.13f), f(0.2f)), style = tr)
            for (k in 0 until 3) drawLine(l.copy(alpha = 0.6f), Offset(f(0.16f), f(0.42f + k * 0.14f)), Offset(f(0.58f), f(0.42f + k * 0.14f)), tr.width * 0.7f)
            drawLine(a, Offset(f(0.6f), f(0.96f)), Offset(f(0.94f), f(0.26f)), tr.width * 1.8f)
            drawLine(l, Offset(f(0.94f), f(0.26f)), Offset(f(0.97f), f(0.2f)), tr.width)
        }
        TemaId.CIBER -> {
            // Indicador de consola.
            drawPath(Path().apply { moveTo(f(0.08f), f(0.2f)); lineTo(f(0.4f), f(0.5f)); lineTo(f(0.08f), f(0.8f)) }, a, style = tr)
            drawLine(e.secondary, Offset(f(0.5f), f(0.82f)), Offset(f(0.92f), f(0.82f)), tr.width)
        }
        TemaId.NEON -> {
            // Un bolígrafo sobre el ticket con el borde dentado.
            val tk = Path().apply { moveTo(f(0.12f), f(0.12f)); lineTo(f(0.72f), f(0.12f)); lineTo(f(0.72f), f(0.86f))
                var x = f(0.72f); var arriba = true; while (x > f(0.12f)) { x -= f(0.06f); lineTo(maxOf(x, f(0.12f)), if (arriba) f(0.8f) else f(0.86f)); arriba = !arriba }; close() }
            drawPath(tk, l, style = tr)
            drawLine(l, Offset(f(0.22f), f(0.32f)), Offset(f(0.58f), f(0.32f)), tr.width); drawLine(l, Offset(f(0.22f), f(0.48f)), Offset(f(0.5f), f(0.48f)), tr.width)
            drawLine(a, Offset(f(0.9f), f(0.18f)), Offset(f(0.5f), f(0.7f)), tr.width * 1.8f, cap = StrokeCap.Round)
        }
        TemaId.HELENO -> {
            // Papiro entre sus dos rodillos.
            drawRect(l, Offset(f(0.2f), f(0.22f)), Size(f(0.6f), f(0.56f)), style = tr)
            drawRoundRect(a, Offset(f(0.04f), f(0.1f)), Size(f(0.16f), f(0.8f)), CornerRadius(f(0.08f)), style = tr)
            drawRoundRect(a, Offset(f(0.8f), f(0.1f)), Size(f(0.16f), f(0.8f)), CornerRadius(f(0.08f)), style = tr)
        }
        TemaId.EGIPTO -> {
            // La paleta del escriba: la tablilla larga con sus dos pocillos (negro y rojo) y el cálamo en su ranura.
            drawRoundRect(l, Offset(f(0.32f), f(0.04f)), Size(f(0.36f), f(0.92f)), CornerRadius(f(0.06f)), style = tr)
            drawCircle(a, f(0.06f), Offset(f(0.43f), f(0.16f)), style = tr)
            drawCircle(a, f(0.06f), Offset(f(0.57f), f(0.16f)))
            drawLine(l, Offset(f(0.5f), f(0.32f)), Offset(f(0.5f), f(0.88f)), tr.width)
        }
        TemaId.MEDIEVAL -> {
            // Pluma y tintero.
            drawRect(l, Offset(f(0.08f), f(0.6f)), Size(f(0.36f), f(0.32f)))
            drawPath(Path().apply { moveTo(f(0.3f), f(0.62f)); quadraticTo(f(0.56f), f(0.2f), f(0.96f), f(0.04f)); quadraticTo(f(0.8f), f(0.4f), f(0.3f), f(0.62f)) }, a)
        }
        TemaId.TEMPLARIO -> {
            // La espada en alto: hoja, guarda en cruz y pomo.
            drawLine(a, Offset(f(0.24f), f(0.76f)), Offset(f(0.92f), f(0.08f)), tr.width * 1.6f)
            drawLine(l, Offset(f(0.12f), f(0.62f)), Offset(f(0.38f), f(0.88f)), tr.width * 1.6f)
            drawLine(l, Offset(f(0.25f), f(0.75f)), Offset(f(0.14f), f(0.86f)), tr.width * 1.2f)
            drawCircle(l, f(0.07f), Offset(f(0.1f), f(0.9f)))
        }
        TemaId.DISTOPIA -> {
            // Sello de goma.
            drawCircle(l, f(0.16f), Offset(f(0.5f), f(0.2f)))
            drawRect(l, Offset(f(0.44f), f(0.3f)), Size(f(0.12f), f(0.3f)))
            drawRect(a, Offset(f(0.14f), f(0.6f)), Size(f(0.72f), f(0.28f)))
        }
        TemaId.REFUGIO -> {
            // Tablilla con pinza.
            drawRoundRect(l, Offset(f(0.16f), f(0.14f)), Size(f(0.68f), f(0.82f)), CornerRadius(f(0.08f)), style = tr)
            drawRect(a, Offset(f(0.34f), f(0.04f)), Size(f(0.32f), f(0.18f)))
            drawLine(l, Offset(f(0.3f), f(0.5f)), Offset(f(0.7f), f(0.5f)), tr.width * 0.7f)
            drawLine(l, Offset(f(0.3f), f(0.7f)), Offset(f(0.6f), f(0.7f)), tr.width * 0.7f)
        }
        TemaId.ORDEN -> {
            // Placa de datos remachada.
            drawRect(l, Offset(f(0.06f), f(0.16f)), Size(f(0.88f), f(0.68f)), style = tr)
            listOf(Offset(0.18f, 0.28f), Offset(0.82f, 0.28f), Offset(0.18f, 0.72f), Offset(0.82f, 0.72f)).forEach { drawCircle(e.secondary, f(0.07f), Offset(f(it.x), f(it.y))) }
        }
        TemaId.VAPOR -> {
            // La lámina del plano con su escuadra y el compás abierto.
            drawRect(l, Offset(f(0.08f), f(0.1f)), Size(f(0.7f), f(0.82f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.16f), f(0.82f)); lineTo(f(0.16f), f(0.46f)); lineTo(f(0.52f), f(0.82f)); close() }, l, style = tr)
            drawLine(a, Offset(f(0.8f), f(0.12f)), Offset(f(0.64f), f(0.62f)), tr.width)
            drawLine(a, Offset(f(0.8f), f(0.12f)), Offset(f(0.94f), f(0.6f)), tr.width)
            drawCircle(a, f(0.05f), Offset(f(0.8f), f(0.12f)))
        }
        TemaId.LABORATORIO -> {
            // Cuaderno de laboratorio cuadriculado.
            drawRect(l, Offset(f(0.1f), f(0.06f)), Size(f(0.8f), f(0.88f)), style = tr)
            for (n in 1..3) {
                drawLine(a.copy(alpha = 0.6f), Offset(f(0.1f + n * 0.2f), f(0.06f)), Offset(f(0.1f + n * 0.2f), f(0.94f)), tr.width * 0.5f)
                drawLine(a.copy(alpha = 0.6f), Offset(f(0.1f), f(0.06f + n * 0.22f)), Offset(f(0.9f), f(0.06f + n * 0.22f)), tr.width * 0.5f)
            }
        }
        TemaId.COLONOS -> {
            // Máquina de escribir con su hoja.
            drawRect(l, Offset(f(0.28f), f(0.04f)), Size(f(0.44f), f(0.34f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.12f), f(0.4f)); lineTo(f(0.88f), f(0.4f)); lineTo(f(0.98f), f(0.92f)); lineTo(f(0.02f), f(0.92f)); close() }, a)
            for (n in 0..3) drawCircle(e.surfaceVariant, f(0.05f), Offset(f(0.24f + n * 0.17f), f(0.66f)))
        }
        TemaId.NAPOLEON -> {
            // Despacho lacrado.
            drawRect(l, Offset(f(0.04f), f(0.2f)), Size(f(0.92f), f(0.6f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.04f), f(0.2f)); lineTo(f(0.5f), f(0.56f)); lineTo(f(0.96f), f(0.2f)) }, l, style = tr)
            drawCircle(a, f(0.13f), Offset(f(0.5f), f(0.58f)))
        }
        TemaId.DECO -> {
            // El formulario del telegrama, con las dos tiras de cinta pegadas y el abanico de la compañía arriba.
            drawRect(l, Offset(f(0.06f), f(0.12f)), Size(f(0.88f), f(0.76f)), style = tr)
            drawArc(a, 180f, 180f, true, Offset(f(0.12f), f(0.18f)), Size(f(0.2f), f(0.2f)))
            drawLine(l, Offset(f(0.4f), f(0.26f)), Offset(f(0.86f), f(0.26f)), tr.width)
            listOf(0.46f, 0.64f).forEach { y -> drawRect(a.copy(alpha = 0.55f), Offset(f(0.14f), f(y)), Size(f(0.72f), f(0.1f))) }
        }
        TemaId.BOTANICO -> {
            // Cuaderno de campo con una hoja prensada.
            drawRoundRect(l, Offset(f(0.14f), f(0.04f)), Size(f(0.72f), f(0.92f)), CornerRadius(f(0.06f)), style = tr)
            rotate(-30f, Offset(f(0.5f), f(0.5f))) { drawOval(a, Offset(f(0.3f), f(0.38f)), Size(f(0.4f), f(0.22f))) }
        }
        TemaId.RUINAS -> {
            // Un muro de ladrillo y el trozo de carboncillo con que se escribe en él.
            drawRect(l, Offset(f(0.06f), f(0.12f)), Size(f(0.88f), f(0.76f)), style = tr)
            for (j in 1..3) drawLine(l, Offset(f(0.06f), f(0.12f + 0.19f * j)), Offset(f(0.94f), f(0.12f + 0.19f * j)), tr.width * 0.6f)
            for (j in 0..3) { val x0 = if (j % 2 == 0) 0.35f else 0.6f; drawLine(l, Offset(f(x0), f(0.12f + 0.19f * j)), Offset(f(x0), f(0.31f + 0.19f * j)), tr.width * 0.6f) }
            rotate(-40f, Offset(f(0.62f), f(0.55f))) { drawRoundRect(a, Offset(f(0.42f), f(0.5f)), Size(f(0.42f), f(0.12f)), CornerRadius(f(0.03f))) }
        }
        TemaId.NAVIDAD -> {
            // Carta con una ramita de acebo.
            drawRect(l, Offset(f(0.04f), f(0.22f)), Size(f(0.92f), f(0.58f)), style = tr)
            drawOval(e.secondary, Offset(f(0.5f), f(0.46f)), Size(f(0.22f), f(0.12f)))
            drawCircle(a, f(0.09f), Offset(f(0.72f), f(0.42f)))
            drawCircle(a, f(0.09f), Offset(f(0.84f), f(0.52f)))
        }
        TemaId.SEDA -> {
            // Pincel sobre la piedra de tinta.
            drawRect(l, Offset(f(0.08f), f(0.72f)), Size(f(0.84f), f(0.22f)))
            drawLine(a, Offset(f(0.3f), f(0.06f)), Offset(f(0.62f), f(0.58f)), tr.width * 1.2f)
            drawPath(Path().apply { moveTo(f(0.56f), f(0.5f)); lineTo(f(0.7f), f(0.72f)); lineTo(f(0.66f), f(0.48f)); close() }, a)
        }
        TemaId.EDO -> {
            // El pincel (fude) en vertical, con su punta mojada, y la piedra de tinta (suzuri) con su pocito.
            drawRoundRect(l, Offset(f(0.06f), f(0.62f)), Size(f(0.5f), f(0.32f)), CornerRadius(f(0.06f)), style = tr)
            drawOval(l, Offset(f(0.12f), f(0.67f)), Size(f(0.18f), f(0.1f)))
            drawLine(a, Offset(f(0.76f), f(0.04f)), Offset(f(0.76f), f(0.6f)), tr.width * 1.6f)
            drawPath(Path().apply { moveTo(f(0.7f), f(0.6f)); quadraticTo(f(0.68f), f(0.8f), f(0.76f), f(0.96f)); quadraticTo(f(0.84f), f(0.8f), f(0.82f), f(0.6f)); close() }, a)
        }
        TemaId.GOTICO -> {
            // Breviario abierto.
            drawPath(Path().apply {
                moveTo(f(0.5f), f(0.26f)); quadraticTo(f(0.26f), f(0.12f), f(0.02f), f(0.22f)); lineTo(f(0.02f), f(0.86f)); quadraticTo(f(0.26f), f(0.76f), f(0.5f), f(0.9f))
                quadraticTo(f(0.74f), f(0.76f), f(0.98f), f(0.86f)); lineTo(f(0.98f), f(0.22f)); quadraticTo(f(0.74f), f(0.12f), f(0.5f), f(0.26f)); close()
            }, l, style = tr)
            drawLine(a, Offset(f(0.5f), f(0.26f)), Offset(f(0.5f), f(0.9f)), tr.width)
        }
        TemaId.ARCANO -> {
            // La libreta con un recorte pegado y la pluma de acero encima.
            drawRect(l, Offset(f(0.1f), f(0.08f)), Size(f(0.62f), f(0.84f)), style = tr)
            rotate(-6f, Offset(f(0.4f), f(0.36f))) { drawRect(a, Offset(f(0.2f), f(0.22f)), Size(f(0.4f), f(0.28f)), style = Stroke(tr.width * 0.7f)) }
            listOf(0.62f, 0.72f).forEach { y -> drawLine(l.copy(alpha = 0.6f), Offset(f(0.18f), f(y)), Offset(f(0.6f), f(y)), tr.width * 0.5f) }
            drawLine(a, Offset(f(0.96f), f(0.06f)), Offset(f(0.52f), f(0.78f)), tr.width * 1.2f)
            drawPath(Path().apply { moveTo(f(0.56f), f(0.72f)); lineTo(f(0.5f), f(0.84f)); lineTo(f(0.6f), f(0.76f)); close() }, a)
        }
        TemaId.ESPACIO -> {
            // Bitácora grabando.
            drawCircle(l, f(0.42f), Offset(f(0.5f), f(0.5f)), style = tr)
            drawCircle(e.error, f(0.2f), Offset(f(0.5f), f(0.5f)))
        }
        TemaId.ABISMO -> {
            // La pizarra de buceo con su lápiz atado de un cordón.
            drawRoundRect(l, Offset(f(0.08f), f(0.12f)), Size(f(0.58f), f(0.8f)), CornerRadius(f(0.08f)), style = tr)
            for (k in 0 until 3) drawLine(l.copy(alpha = 0.5f), Offset(f(0.18f), f(0.36f + k * 0.16f)), Offset(f(0.56f), f(0.36f + k * 0.16f)), tr.width * 0.7f)
            drawCircle(l, f(0.04f), Offset(f(0.37f), f(0.2f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.37f), f(0.24f)); quadraticTo(f(0.7f), f(0.1f), f(0.8f), f(0.3f)) }, l.copy(alpha = 0.6f), style = tr)
            drawLine(a, Offset(f(0.8f), f(0.3f)), Offset(f(0.9f), f(0.88f)), tr.width * 1.6f)
            drawLine(l, Offset(f(0.885f), f(0.82f)), Offset(f(0.9f), f(0.96f)), tr.width)
        }
        TemaId.ANDALUS -> {
            // Cálamo de caña y tintero.
            drawLine(a, Offset(f(0.16f), f(0.94f)), Offset(f(0.84f), f(0.1f)), tr.width * 1.3f)
            drawPath(Path().apply { moveTo(f(0.84f), f(0.1f)); lineTo(f(0.98f), f(0.02f)); lineTo(f(0.92f), f(0.2f)); close() }, a)
            drawCircle(l, f(0.16f), Offset(f(0.24f), f(0.3f)), style = tr)
        }
        TemaId.MESO -> {
            // El pincel de pelo y la concha que hace de tintero, como en las vasijas que pintan a los escribas.
            drawLine(l, Offset(f(0.2f), f(0.92f)), Offset(f(0.72f), f(0.24f)), tr.width * 1.2f)
            drawPath(Path().apply { moveTo(f(0.72f), f(0.24f)); quadraticTo(f(0.84f), f(0.04f), f(0.94f), f(0.02f)); quadraticTo(f(0.9f), f(0.16f), f(0.78f), f(0.3f)); close() }, a)
            drawArc(l, 0f, 180f, true, Offset(f(0.04f), f(0.2f)), Size(f(0.4f), f(0.3f)), style = tr)
            drawCircle(a, f(0.05f), Offset(f(0.24f), f(0.4f)))
        }
        TemaId.MONGOL -> {
            // Rollo vertical con la escritura de arriba abajo.
            drawRect(l, Offset(f(0.3f), f(0.02f)), Size(f(0.4f), f(0.96f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.5f), f(0.12f)); quadraticTo(f(0.62f), f(0.3f), f(0.5f), f(0.46f)); quadraticTo(f(0.38f), f(0.62f), f(0.5f), f(0.86f)) }, a, style = tr)
        }
        TemaId.RENACIMIENTO -> {
            // Compás abierto sobre su arco.
            drawLine(a, Offset(f(0.5f), f(0.12f)), Offset(f(0.16f), f(0.96f)), tr.width)
            drawLine(a, Offset(f(0.5f), f(0.12f)), Offset(f(0.84f), f(0.96f)), tr.width)
            drawCircle(l, f(0.11f), Offset(f(0.5f), f(0.12f)))
            drawArc(l, 20f, 140f, false, Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.6f)), style = Stroke(tr.width * 0.6f))
        }
        TemaId.PIRATA -> {
            // Pluma de gaviota sobre el tintero de la bitácora.
            val pluma = Path().apply {
                moveTo(f(0.86f), f(0.06f)); cubicTo(f(0.56f), f(0.12f), f(0.34f), f(0.4f), f(0.3f), f(0.7f))
                cubicTo(f(0.5f), f(0.58f), f(0.76f), f(0.36f), f(0.86f), f(0.06f))
            }
            drawPath(pluma, a.copy(alpha = 0.25f)); drawPath(pluma, a, style = tr)
            drawLine(a, Offset(f(0.3f), f(0.7f)), Offset(f(0.22f), f(0.84f)), tr.width)
            for (k in 1..4) drawLine(a.copy(alpha = 0.6f), Offset(f(0.32f + k * 0.1f), f(0.62f - k * 0.12f)), Offset(f(0.42f + k * 0.1f), f(0.66f - k * 0.12f)), tr.width * 0.6f)
            drawRoundRect(l, Offset(f(0.08f), f(0.78f)), Size(f(0.3f), f(0.18f)), CornerRadius(f(0.04f)), style = tr)
            drawLine(l, Offset(f(0.13f), f(0.78f)), Offset(f(0.33f), f(0.78f)), tr.width * 1.6f)
        }
        TemaId.URSS -> {
            // Cinta perforada de teletipo.
            drawRect(l, Offset(0f, f(0.3f)), Size(s, f(0.4f)), style = tr)
            for (n in 0..3) drawCircle(a, f(0.06f), Offset(f(0.14f + n * 0.24f), f(0.5f)))
        }
        TemaId.REVOLUCION -> {
            // Cuaderno de quejas con la escarapela.
            drawRect(l, Offset(f(0.14f), f(0.04f)), Size(f(0.72f), f(0.92f)), style = tr)
            val c = Offset(f(0.38f), f(0.3f))
            drawCircle(e.secondary, f(0.16f), c); drawCircle(Color.White, f(0.1f), c); drawCircle(a, f(0.05f), c)
        }
        TemaId.ANTIGUAS -> {
            // Piedra con una runa.
            drawRoundRect(l, Offset(f(0.2f), f(0.04f)), Size(f(0.6f), f(0.92f)), CornerRadius(f(0.3f)), style = tr)
            drawLine(a, Offset(f(0.5f), f(0.24f)), Offset(f(0.5f), f(0.76f)), tr.width)
            drawLine(a, Offset(f(0.5f), f(0.4f)), Offset(f(0.34f), f(0.26f)), tr.width)
            drawLine(a, Offset(f(0.5f), f(0.4f)), Offset(f(0.66f), f(0.26f)), tr.width)
        }
        TemaId.CUEVA -> {
            // Un tizón de carbón que traza una línea en la roca.
            drawPath(Path().apply { moveTo(f(0.14f), f(0.86f)); quadraticTo(f(0.4f), f(0.7f), f(0.62f), f(0.8f)) }, l, style = tr)
            rotate(-40f, Offset(f(0.66f), f(0.42f))) { drawRoundRect(a, Offset(f(0.6f), f(0.04f)), Size(f(0.13f), f(0.72f)), CornerRadius(f(0.05f))) }
        }
    }
}

/** El margen izquierdo del editor. */
private fun DrawScope.margenEditor(m: MargenEditor, e: ColorScheme) {
    val h = size.height; val w = size.width
    when (m) {
        MargenEditor.BARRA -> drawRect(e.primary.copy(alpha = 0.35f), Offset.Zero, Size(3.dp.toPx(), h))
        MargenEditor.DOBLE -> {
            drawRect(e.primary.copy(alpha = 0.5f), Offset.Zero, Size(1.dp.toPx(), h))
            drawRect(e.primary.copy(alpha = 0.5f), Offset(2.5.dp.toPx(), 0f), Size(1.dp.toPx(), h))
        }
        MargenEditor.PUNTOS -> {
            var y = 3.dp.toPx()
            while (y < h) { drawCircle(e.primary.copy(alpha = 0.55f), 1.2.dp.toPx(), Offset(2.dp.toPx(), y)); y += 6.dp.toPx() }
        }
        // El margen rojo de los cuadernos.
        MargenEditor.ROJO -> {
            drawRect(e.error.copy(alpha = 0.45f), Offset(w - 2.6.dp.toPx(), 0f), Size(0.8.dp.toPx(), h))
            drawRect(e.error.copy(alpha = 0.45f), Offset(w - 1.dp.toPx(), 0f), Size(0.8.dp.toPx(), h))
        }
        MargenEditor.NINGUNO -> Unit
    }
}

/** La barra izquierda de un aviso (6 dp de ancho). */
private fun DrawScope.barraAviso(t: TemaId, e: ColorScheme) {
    val c = e.secondary; val w = size.width; val h = size.height; val d = 1.dp.toPx()
    when (t) {
        TemaId.ROMANO -> drawRect(c, Offset.Zero, Size(3 * d, h))
        // El poste de la parada: tramos de color y tramos vacíos, como la pintura de los postes.
        TemaId.URBE -> { var y = 0f; var i = 0; while (y < h) { drawRect(if (i % 2 == 0) c else e.primary, Offset(0f, y), Size(3 * d, minOf(5 * d, h - y))); y += 5 * d; i++ } }
        TemaId.CIBER -> {
            var y = 0f
            while (y < h) { drawRect(c, Offset(0f, y), Size(2 * d, 4 * d)); y += 7 * d }
        }
        // La cinta de precaución de obra: franjas ámbar en diagonal.
        TemaId.NEON -> {
            drawRect(c.copy(alpha = 0.25f), Offset.Zero, Size(4 * d, h))
            var y = -4 * d; while (y < h) { drawPath(Path().apply { moveTo(0f, y + 3 * d); lineTo(4 * d, y); lineTo(4 * d, y + 3 * d); lineTo(0f, y + 6 * d); close() }, c); y += 8 * d }
        }
        TemaId.HELENO -> {
            drawRect(c, Offset.Zero, Size(1.4f * d, h))
            drawRect(c, Offset(3.2f * d, 0f), Size(1.4f * d, h))
        }
        // La cenefa vertical de rectángulos de colores: cornalina, oro y lapislázuli.
        TemaId.EGIPTO -> {
            val colores = listOf(c, e.tertiary, e.primary, e.tertiary)
            var y = 0f; var k = 0
            while (y < h) { drawRect(colores[k % 4], Offset(0f, y), Size(3 * d, minOf(5 * d, h - y))); y += 6 * d; k++ }
        }
        TemaId.MEDIEVAL -> {
            drawRect(e.tertiary, Offset(d, 0f), Size(1.2f * d, h))
            var y = 5 * d
            while (y < h) { drawCircle(e.primary, 1.5f * d, Offset(4.2f * d, y)); y += 9 * d }
        }
        TemaId.TEMPLARIO -> {
            drawRect(e.secondary, Offset.Zero, Size(2 * d, h))
            var y = 6 * d
            while (y < h) { drawPath(cruzPatada(Offset(4.4f * d, y), 1.7f * d), e.primary); y += 10 * d }
        }
        TemaId.DISTOPIA -> drawRect(e.onSurface, Offset.Zero, Size(w, h))
        TemaId.REFUGIO -> drawRoundRect(c, Offset(d, d), Size(4 * d, h - 2 * d), CornerRadius(2 * d))
        TemaId.ORDEN -> {
            // Galones apilados.
            val p = Path(); var y = 0f
            p.moveTo(0f, 0f)
            while (y < h) { p.lineTo(w * 0.8f, y + 3 * d); p.lineTo(0f, y + 6 * d); y += 6 * d }
            drawPath(p, c, style = Stroke(1.4f * d))
        }
        // El tubo de vapor vertical con sus bridas.
        TemaId.VAPOR -> {
            drawRect(e.primary.copy(alpha = 0.8f), Offset(0f, 0f), Size(3 * d, h))
            var y = 4 * d; while (y < h) { drawRect(e.primary, Offset(-1 * d, y), Size(5 * d, 1.6f * d)); y += 14 * d }
        }
        TemaId.LABORATORIO -> {
            drawRect(c, Offset.Zero, Size(d, h))
            var y = 0f; var n = 0
            while (y < h) { drawLine(c, Offset(0f, y), Offset(if (n % 5 == 0) w else w * 0.55f, y), d); y += 4 * d; n++ }
        }
        TemaId.COLONOS -> {
            drawRect(c, Offset.Zero, Size(3 * d, h))
            drawRect(c, Offset(4.4f * d, 0f), Size(0.8f * d, h))
        }
        TemaId.NAPOLEON -> {
            drawRect(e.tertiary, Offset(2.5f * d, 0f), Size(d, h))
            var y = 6 * d
            while (y < h) { romboComp(Offset(3 * d, y), 2.4f * d, 3.2f * d, e.tertiary); y += 14 * d }
        }
        // Doble filete de oro y, de trecho en trecho, la corona escalonada de una torre.
        TemaId.DECO -> {
            drawRect(e.primary, Offset(0.5f * d, 0f), Size(d, h)); drawRect(e.primary.copy(alpha = 0.5f), Offset(3f * d, 0f), Size(0.7f * d, h))
            var y = 8 * d
            while (y < h) {
                drawRect(c, Offset(0f, y), Size(5 * d, 1.6f * d)); drawRect(c, Offset(0.8f * d, y - 1.6f * d), Size(3.4f * d, 1.6f * d))
                drawRect(c, Offset(1.6f * d, y - 3.2f * d), Size(1.8f * d, 1.6f * d))
                y += 18 * d
            }
        }
        TemaId.BOTANICO -> {
            // Un tallo que ondula.
            val p = Path(); var y = 0f
            p.moveTo(3 * d, 0f)
            while (y <= h) { p.lineTo(3 * d + 1.8f * d * sin(y / (5 * d)), y); y += 2f }
            drawPath(p, c, style = Stroke(1.3f * d))
        }
        TemaId.RUINAS -> {
            // Una valla de calle cortada: franjas en diagonal, óxido y ámbar.
            var y = 0f; var n = 0
            while (y < h) {
                drawPath(Path().apply { moveTo(0f, y); lineTo(4 * d, y - 3 * d); lineTo(4 * d, y + 2 * d); lineTo(0f, y + 5 * d); close() }, if (n % 2 == 0) c else e.tertiary)
                y += 5 * d; n++
            }
        }
        TemaId.NAVIDAD -> {
            // Bastón de caramelo: tramos rojos y verdes.
            var y = 0f; var n = 0
            while (y < h) { drawRect(if (n % 2 == 0) e.primary else e.secondary, Offset(0f, y), Size(4 * d, 5 * d)); y += 5 * d; n++ }
        }
        TemaId.SEDA -> {
            drawRect(c, Offset(2.6f * d, 0f), Size(0.8f * d, h))
            var y = 6 * d
            while (y < h) { drawCircle(e.tertiary, 2.6f * d, Offset(3 * d, y)); drawRect(e.secondaryContainer, Offset(2.2f * d, y - 0.8f * d), Size(1.6f * d, 1.6f * d)); y += 10 * d }
        }
        // Un tanzaku de bermellón con su tapa de tinta y las tiras de papel que se le pegan.
        TemaId.EDO -> {
            drawRect(c, Offset.Zero, Size(3.5f * d, h))
            drawRect(e.onSurface, Offset.Zero, Size(3.5f * d, 3 * d))
            var y = 9 * d
            while (y < h) { drawLine(e.surface.copy(alpha = 0.7f), Offset(0.7f * d, y), Offset(2.8f * d, y), 0.8f * d); y += 12 * d }
        }
        TemaId.GOTICO -> {
            drawRect(c, Offset(2 * d, 0f), Size(2 * d, h))
            var y = 7 * d
            while (y < h) { drawRect(e.tertiary, Offset(0f, y - 0.6f * d), Size(w, 1.2f * d)); y += 14 * d }
        }
        // Una línea temblorosa a tinta, con una gota que se corrió.
        TemaId.ARCANO -> {
            val p = Path(); var y = 0f; p.moveTo(2.5f * d, 0f)
            while (y <= h) { p.lineTo(2.5f * d + 0.8f * d * sin(y / (2.2f * d)) + 0.5f * d * sin(y / (7f * d)), y); y += 2f }
            drawPath(p, c, style = Stroke(1.6f * d))
            drawCircle(c, 2.2f * d, Offset(2.5f * d, h * 0.3f)); drawLine(c, Offset(2.5f * d, h * 0.3f), Offset(2.5f * d, h * 0.3f + 6 * d), 1.4f * d)
        }
        TemaId.ESPACIO -> {
            var y = 2 * d
            while (y < h) { drawCircle(c, d, Offset(2.5f * d, y)); y += 4 * d }
        }
        TemaId.ABISMO -> {
            // Una corriente: la línea ondula de arriba abajo.
            val p = Path(); var y = 0f
            p.moveTo(2.5f * d, 0f)
            while (y < h) { y += maxOf(d, 1f); p.lineTo(2.5f * d + 1.6f * d * sin(y / (3 * d)), y) }
            drawPath(p, c, style = Stroke(1.6f * d))
        }
        TemaId.ANDALUS -> {
            // Cinta de lazo en zigzag.
            val p = Path(); var y = 0f; var izq = true
            p.moveTo(0.5f * d, 0f)
            while (y < h) { y += 4 * d; izq = !izq; p.lineTo(if (izq) 0.5f * d else w - 0.5f * d, y) }
            drawPath(p, e.tertiary, style = Stroke(1.4f * d))
        }
        TemaId.MESO -> {
            // Una columna de puntos y barras, como un numeral muy largo, en cinabrio y jade.
            var y = d; var k = 0
            while (y < h) {
                if (k % 3 == 2) { drawRect(e.primary, Offset(0f, y), Size(w, 1.6f * d)); y += 4f * d }
                else { drawCircle(e.tertiary, minOf(w / 2, 1.6f * d), Offset(w / 2, y + 1.6f * d)); y += 5f * d }
                k++
            }
        }
        TemaId.MONGOL -> {
            drawRect(c, Offset.Zero, Size(4.5f * d, h))
            drawLine(e.secondaryContainer, Offset(2.2f * d, 0f), Offset(2.2f * d, h), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 2 * d)))
        }
        TemaId.RENACIMIENTO -> {
            drawLine(c, Offset(1.5f * d, 0f), Offset(2 * d, h), 0.9f * d)
            drawLine(c.copy(alpha = 0.6f), Offset(3.5f * d, 2 * d), Offset(3 * d, h - 3 * d), 0.7f * d)
        }
        // Un cabo trenzado: dos cordones que se cruzan en diagonal.
        TemaId.PIRATA -> {
            var y = 0f; var k = 0
            while (y < h) {
                drawLine(if (k % 2 == 0) c else c.copy(alpha = 0.55f), Offset(0.4f * d, y), Offset(3.6f * d, y + 3 * d), 1.4f * d, StrokeCap.Round)
                y += 2.5f * d; k++
            }
        }
        TemaId.URSS -> drawPath(Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(0f, h); close() }, e.primary)
        TemaId.REVOLUCION -> listOf(e.secondary, e.outline, e.primary).forEachIndexed { k, cc -> drawRect(cc, Offset(k * 2 * d, 0f), Size(1.6f * d, h)) }
        TemaId.ANTIGUAS -> {
            drawRect(c, Offset(2.5f * d, 0f), Size(d, h))
            var y = 4 * d; var n = 0
            while (y < h) {
                val lado = if (n % 2 == 0) 1f else -1f
                drawLine(c, Offset(3 * d, y), Offset(3 * d + lado * 2.5f * d, y - 2.5f * d), d)
                y += 6 * d; n++
            }
        }
        TemaId.CUEVA -> {
            // Una hilera de puntos de ocre, de grosor desparejo, como pintados con la yema del dedo.
            var y = 3 * d; var k = 0
            while (y < h) { drawCircle(e.primary, (1.4f + (k * 7 % 5) * 0.25f) * d, Offset(2.5f * d, y)); y += 6 * d; k++ }
        }
    }
}
