@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package io.github.ruddy2003.vestigium.ui

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

/** Esquinas según el tema (rectas en cyberpunk, suaves en helénico). */
private val R4 @Composable get() = MaterialTheme.shapes.small

/**
 * Banda ornamental bajo cada título, una por tema: ovas y dardos (romano), greca enmarcada (helénico), pista de
 * circuito (terminal), expediente con su ojo (distopía), regla con anillo de benceno (laboratorio), filete de gaceta
 * (colonos), sarta de monedas con nudo y borla (seda), meandro de martillos entre costuras de fieltro (mongol)…
 */
@Composable
fun Meandro(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)) {
    when (Estilo.tema) {
        TemaId.CIBER -> Circuito(modifier)
        TemaId.NEON -> Circuito(modifier)
        TemaId.HELENO -> Greca(modifier, color)
        TemaId.MEDIEVAL -> Zarcillo(modifier)
        TemaId.DISTOPIA -> BandaExpediente(modifier)
        TemaId.REFUGIO -> Destellos(modifier)
        TemaId.ORDEN -> Galones(modifier)
        TemaId.LABORATORIO -> BandaProbeta(modifier)
        TemaId.COLONOS -> BandaGaceta(modifier)
        TemaId.NAPOLEON -> Laureles(modifier)
        TemaId.BOTANICO -> Enredadera(modifier)
        TemaId.GOTICO -> Arqueria(modifier)
        TemaId.ARCANO -> BandaMarea(modifier)
        TemaId.ESPACIO -> Orbita(modifier)
        TemaId.ANDALUS -> Lazo(modifier)
        TemaId.MONGOL -> BandaEstepa(modifier)
        TemaId.RENACIMIENTO -> Trazo(modifier)
        TemaId.NAVIDAD -> Guirnalda(modifier)
        TemaId.SEDA -> BandaCaravana(modifier)
        TemaId.URSS -> Barra(modifier)
        TemaId.REVOLUCION -> Tricolor(modifier)
        TemaId.ANTIGUAS -> Runica(modifier)
        TemaId.ROMANO -> BandaOvas(modifier, color)
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

/** Pista de circuito: línea fina con nodos y un tramo encendido en magenta. */
@Composable
private fun Circuito(modifier: Modifier) {
    val cian = MaterialTheme.colorScheme.primary
    val magenta = MaterialTheme.colorScheme.secondary
    Canvas(modifier.fillMaxWidth().height(10.dp)) {
        val y = size.height / 2
        drawLine(cian.copy(alpha = 0.35f), Offset(0f, y), Offset(size.width, y), 1.5f)
        drawLine(magenta, Offset(0f, y), Offset(size.width * 0.16f, y), 2.5f)
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
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
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
                    Text("$i", style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal),
                        color = if (sel) Tonos.sobre(c) else MaterialTheme.colorScheme.onSurface)
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
        val nuevos = texto.split(",").map(normalizar).filter { it.isNotEmpty() }
        if (nuevos.isNotEmpty()) { onCambio(lista + nuevos); texto = "" }
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
                        TextButton(onClick = { onCambio(lista.filterIndexed { j, _ -> j != i }) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(36.dp)) { Text(piel.quitar, fontSize = 16.sp) }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
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
            Text(contando(valor), style = MaterialTheme.typography.labelLarge.copy(fontSize = 19.sp, letterSpacing = 0.sp), maxLines = 1)
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
    TemaId.CIBER -> PielComponentes(sep = " ", cerrada = "[+]", abierta = "[-]", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "!", igual = ":= ", nulo = "null", elegido = "▣", riel = RolColor.SECUNDARIO, quitar = "[x]", listaVacia = "[ ]", flecha = ">>",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0f, colorPrefijo = RolColor.SECUNDARIO, renglones = false, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(1.5f, 1.5f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1f)
    TemaId.NEON -> PielComponentes(sep = " ", cerrada = "[+]", abierta = "[-]", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "!", igual = ":= ", nulo = "null", elegido = "▣", riel = RolColor.SECUNDARIO, quitar = "[x]", listaVacia = "[ ]", flecha = ">>",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0f, colorPrefijo = RolColor.SECUNDARIO, renglones = false, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(1.5f, 1.5f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1f)
    TemaId.HELENO -> PielComponentes(sep = "  —  ", cerrada = "◇", abierta = "◆", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "☉", igual = "· ", nulo = "οὐδέν", elegido = "✓", riel = RolColor.SECUNDARIO, quitar = "✕", listaVacia = "( κενόν )", flecha = "⟶",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.5f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(5f, 2f), tamBoton = 14, letraBoton = null, borde = RolColor.TERCIARIO, grosor = 1f)
    TemaId.MEDIEVAL -> PielComponentes(sep = "  ⁖  ", cerrada = "❦", abierta = "☙", numeral = RolColor.PRIMARIO, centrar = true, rellena = true,
        aviso = "✠", igual = "— ", nulo = "sine nota", elegido = "✚", riel = RolColor.TERCIARIO, quitar = "✗", listaVacia = "( folio en blanco )", flecha = "❧",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.2f, colorPrefijo = RolColor.PRIMARIO, renglones = true, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(0.8f, 2f), tamBoton = 17, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1f)
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
    TemaId.BOTANICO -> PielComponentes(sep = "  ⁂  ", cerrada = "⚘", abierta = "❀", numeral = RolColor.SECUNDARIO, centrar = false, rellena = true,
        aviso = "✾", igual = "~ ", nulo = "sin espécimen", elegido = "✿", riel = RolColor.SECUNDARIO, quitar = "⊘", listaVacia = "( frasco vacío )", flecha = "⤳",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.1f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.PUNTOS,
        guiones = floatArrayOf(0.5f, 1.8f), tamBoton = 14, letraBoton = null, borde = RolColor.CONTORNO, grosor = 1f)
    TemaId.NAVIDAD -> PielComponentes(sep = "  ✶  ", cerrada = "❄", abierta = "❅", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "✉", igual = "✶ ", nulo = "sin abrir", elegido = "❆", riel = RolColor.SECUNDARIO, quitar = "⊖", listaVacia = "( ni un regalo )", flecha = "➝",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.4f, colorPrefijo = RolColor.SECUNDARIO, renglones = true, margen = MargenEditor.NINGUNO,
        guiones = floatArrayOf(2f, 2f), tamBoton = 14, letraBoton = null, borde = RolColor.SECUNDARIO, grosor = 1.5f)
    TemaId.SEDA -> PielComponentes(sep = "  ◦  ", cerrada = "◎", abierta = "◉", numeral = RolColor.PRIMARIO, centrar = true, rellena = true,
        aviso = "◈", igual = "〉", nulo = "空", elegido = "⊡", riel = RolColor.PRIMARIO, quitar = "(×)", listaVacia = "( alforja vacía )", flecha = "⟿",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.6f, colorPrefijo = RolColor.PRIMARIO, renglones = false, margen = MargenEditor.ROJO,
        guiones = floatArrayOf(2f, 0.8f, 0.4f, 0.8f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1f)
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
    TemaId.ANDALUS -> PielComponentes(sep = "  ❖  ", cerrada = "۞", abierta = "✺", numeral = RolColor.TERCIARIO, centrar = false, rellena = true,
        aviso = "❁", igual = "∷ ", nulo = "لا شيء", elegido = "✹", riel = RolColor.TERCIARIO, quitar = "‹×›", listaVacia = "( sin azulejos )", flecha = "➛",
        sufijo = "", caja = CajaEtiqueta.CAPITAL, espaciado = 0.5f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.DOBLE,
        guiones = floatArrayOf(1.2f, 1.2f, 3f, 1.2f), tamBoton = 14, letraBoton = null, borde = RolColor.TERCIARIO, grosor = 1f)
    TemaId.MONGOL -> PielComponentes(sep = "  ◆  ", cerrada = "◑", abierta = "◐", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "☾", igual = "⇢ ", nulo = "хоосон", elegido = "◐", riel = RolColor.PRIMARIO, quitar = "x", listaVacia = "( ger vacía )", flecha = "➜",
        sufijo = "", caja = CajaEtiqueta.MAYUSCULAS, espaciado = 1.4f, colorPrefijo = RolColor.TERCIARIO, renglones = false, margen = MargenEditor.BARRA,
        guiones = floatArrayOf(3.5f, 2.5f), tamBoton = 14, letraBoton = null, borde = RolColor.PRIMARIO, grosor = 1.5f)
    TemaId.RENACIMIENTO -> PielComponentes(sep = "  ⁘  ", cerrada = "∴", abierta = "∵", numeral = RolColor.PRIMARIO, centrar = false, rellena = true,
        aviso = "✎", igual = "≡ ", nulo = "nulla", elegido = "✐", riel = RolColor.PRIMARIO, quitar = "✂", listaVacia = "( foglio bianco )", flecha = "↝",
        sufijo = "", caja = CajaEtiqueta.MINUSCULAS, espaciado = 0.2f, colorPrefijo = RolColor.PRIMARIO, renglones = true, margen = MargenEditor.NINGUNO,
        guiones = floatArrayOf(7f, 1f, 1f, 1f), tamBoton = 17, letraBoton = LetraExtra.cormorant, borde = RolColor.CONTORNO, grosor = 1f)
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
}

/** El título de una sección, con el trazo del tema (capitular, rótulo en bloque, subrayado doble, sello, tramo tricolor…). */
@Composable
private fun TituloSeccion(nombre: String, t: TemaId, peso: Modifier) {
    val e = MaterialTheme.colorScheme
    val estilo = MaterialTheme.typography.titleLarge
    when (t) {
        // Letras espaciadas, como en una inscripción.
        TemaId.ROMANO -> Text(nombre, style = estilo.copy(letterSpacing = 2.sp), modifier = peso)
        // Indicador de consola con el cursor detrás.
        TemaId.CIBER -> Text(buildAnnotatedString { append(nombre); withStyle(SpanStyle(color = e.secondary)) { append(" ▌") } }, style = estilo, color = e.primary, modifier = peso)
        TemaId.NEON -> Text(buildAnnotatedString { append(nombre); withStyle(SpanStyle(color = e.secondary)) { append(" ▌") } }, style = estilo, color = e.primary, modifier = peso)
        // Doble filete de oro, como el borde de un vaso.
        TemaId.HELENO -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 1.dp.toPx()
            drawLine(e.tertiary, Offset(0f, y - 2.5.dp.toPx()), Offset(size.width, y - 2.5.dp.toPx()), 0.8.dp.toPx())
            drawLine(e.tertiary, Offset(0f, y), Offset(size.width, y), 0.8.dp.toPx())
        }.padding(bottom = 5.dp))
        // Capitular en rojo, como las rúbricas de los manuscritos.
        TemaId.MEDIEVAL -> Text(buildAnnotatedString {
            withStyle(SpanStyle(color = e.primary, fontSize = estilo.fontSize * 1.55f)) { append(nombre.take(1)) }
            append(nombre.drop(1))
        }, style = estilo, modifier = peso)
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
        // Pestaña de protocolo: una barra violeta al lado.
        TemaId.LABORATORIO -> Text(nombre, style = estilo, modifier = peso.drawBehind { drawRect(e.secondary, Offset.Zero, Size(3.dp.toPx(), size.height)) }.padding(start = 10.dp))
        // Raya inglesa de gaceta: gruesa y fina.
        TemaId.COLONOS -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            drawRect(e.onBackground, Offset(0f, size.height - 4.dp.toPx()), Size(size.width, 1.6.dp.toPx()))
            drawRect(e.onBackground, Offset(0f, size.height - 1.dp.toPx()), Size(size.width, 0.6.dp.toPx()))
        }.padding(bottom = 6.dp))
        // Azul imperial con una estrella de oro.
        TemaId.NAPOLEON -> Text(buildAnnotatedString { append(nombre); withStyle(SpanStyle(color = e.tertiary)) { append("  ✦") } }, style = estilo.copy(letterSpacing = 1.2.sp), color = e.primary, modifier = peso)
        // Nombre de especie: cursiva sobre una línea punteada a lápiz.
        TemaId.BOTANICO -> Text(nombre, style = estilo.copy(fontStyle = FontStyle.Italic), modifier = peso.drawBehind {
            drawLine(e.onBackground.copy(alpha = 0.5f), Offset(0f, size.height - 1.dp.toPx()), Offset(size.width, size.height - 1.dp.toPx()), 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 3.dp.toPx())))
        }.padding(bottom = 4.dp))
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
        // Remate con el signo de la rub el-hizb, en oro.
        TemaId.ANDALUS -> Text(buildAnnotatedString { append(nombre); withStyle(SpanStyle(color = e.tertiary)) { append("  ۞") } }, style = estilo, modifier = peso)
        // Una franja de fieltro azul encima.
        TemaId.MONGOL -> Text(nombre, style = estilo, modifier = peso.drawBehind { drawRect(e.primary, Offset.Zero, Size(minOf(size.width, 72.dp.toPx()), 2.5.dp.toPx())) }.padding(top = 6.dp))
        // Dos trazos de sanguina, a mano alzada.
        TemaId.RENACIMIENTO -> Text(nombre, style = estilo, modifier = peso.drawBehind {
            val y = size.height - 2.dp.toPx()
            drawLine(e.primary.copy(alpha = 0.7f), Offset(0f, y), Offset(size.width * 0.9f, y - 1.dp.toPx()), 0.8.dp.toPx())
            drawLine(e.primary.copy(alpha = 0.45f), Offset(size.width * 0.04f, y + 1.5.dp.toPx()), Offset(size.width * 0.7f, y + 1.dp.toPx()), 0.7.dp.toPx())
        }.padding(bottom = 5.dp))
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
    }
}

/** La forma de los botones, las fichas de la lista y las pastillas. Las medidas van en proporción a la altura, para que sirvan en todos los tamaños. */
private fun formaBoton(t: TemaId): Shape = when (t) {
    TemaId.ROMANO -> RoundedCornerShape(2.dp)
    TemaId.CIBER -> CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp)
    TemaId.NEON -> CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp)
    // Frontón de templo, muy bajo.
    TemaId.HELENO -> GenericShape { s, _ -> moveTo(0f, s.height * 0.18f); lineTo(s.width / 2, 0f); lineTo(s.width, s.height * 0.18f); lineTo(s.width, s.height); lineTo(0f, s.height); close() }
    // Esquinas cóncavas, como un marco de miniatura.
    TemaId.MEDIEVAL -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val r = h * 0.14f
        moveTo(r, 0f); lineTo(w - r, 0f); quadraticTo(w - r, r, w, r); lineTo(w, h - r); quadraticTo(w - r, h - r, w - r, h)
        lineTo(r, h); quadraticTo(r, h - r, 0f, h - r); lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }
    TemaId.DISTOPIA -> RectangleShape
    TemaId.REFUGIO -> RoundedCornerShape(50)
    TemaId.ORDEN -> CutCornerShape(6.dp)
    TemaId.LABORATORIO -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 4.dp, bottomStart = 4.dp)
    // Boleto de feria: una muesca a cada lado.
    TemaId.COLONOS -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val n = h * 0.26f
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h * 0.34f); quadraticTo(w - n, h / 2, w, h * 0.66f); lineTo(w, h)
        lineTo(0f, h); lineTo(0f, h * 0.66f); quadraticTo(n, h / 2, 0f, h * 0.34f); close()
    }
    // Placa de puntas, como una chapa de chacó.
    TemaId.NAPOLEON -> CutCornerShape(50)
    // Hoja: dos esquinas redondas y dos en punta.
    TemaId.BOTANICO -> RoundedCornerShape(topStart = 16.dp, topEnd = 2.dp, bottomEnd = 16.dp, bottomStart = 2.dp)
    TemaId.NAVIDAD -> RoundedCornerShape(10.dp)
    // Tablilla con alero, como el rótulo de una puerta.
    TemaId.SEDA -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val a = h * 0.12f
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - a, h * 0.22f); lineTo(w - a, h); lineTo(a, h); lineTo(a, h * 0.22f); close()
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
    // Arco de medio punto.
    TemaId.ANDALUS -> RoundedCornerShape(topStartPercent = 50, topEndPercent = 50, bottomEndPercent = 0, bottomStartPercent = 0)
    TemaId.MONGOL -> RoundedCornerShape(8.dp)
    // Hoja con la esquina doblada.
    TemaId.RENACIMIENTO -> CutCornerShape(bottomEnd = 12.dp)
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
}

/** El rótulo de la barra del editor: dónde se escribe en cada tema. Los nombres de archivo en latín se traducen fuera del romano. */
private fun rotuloArchivo(archivo: String, t: TemaId): String {
    val base = archivo.substringBeforeLast('.')
    val raiz = base.substringBefore('_')
    val resto = base.substringAfter('_', "")
    val es = (mapOf("somnium" to "sueño", "notae" to "notas", "ascensus" to "escalada")[raiz] ?: raiz) + (if (resto.isEmpty()) "" else " $resto")
    return when (t) {
        TemaId.ROMANO -> "tabula · " + base.uppercase().replace('U', 'V')
        TemaId.CIBER -> "~/vm/$archivo"
        TemaId.NEON -> "~/vm/$archivo"
        TemaId.HELENO -> "πάπυρος · $es"
        TemaId.MEDIEVAL -> "pluma y tintero · $es"
        TemaId.DISTOPIA -> "FORMULARIO 7-B · ${es.uppercase()}"
        TemaId.REFUGIO -> "BITÁCORA DEL REFUGIO > $es"
        TemaId.ORDEN -> "CRÓNICA DE LA ORDEN · ${es.uppercase()}"
        TemaId.LABORATORIO -> "cuaderno de laboratorio · $es"
        TemaId.COLONOS -> "a máquina · $es"
        TemaId.NAPOLEON -> "dépêche · $es"
        TemaId.BOTANICO -> "cuaderno de campo · $es"
        TemaId.NAVIDAD -> "carta junto al fuego · $es"
        TemaId.SEDA -> "pincel y tinta · $es"
        TemaId.GOTICO -> "breviario · $es"
        TemaId.ARCANO -> "diario del investigador · $es"
        TemaId.ESPACIO -> "BITÁCORA DE MISIÓN · ${es.uppercase()}"
        TemaId.ANDALUS -> "cálamo · $es"
        TemaId.MONGOL -> "тэмдэглэл · $es"
        TemaId.RENACIMIENTO -> "taccuino · $es"
        TemaId.URSS -> "ТЕЛЕТАЙП · ${es.uppercase()}"
        TemaId.REVOLUCION -> "cahier · $es"
        TemaId.ANTIGUAS -> "crónica de la compañía · $es"
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
        TemaId.CIBER -> {
            // Un led encendido arriba a la derecha y un tramo de pista abajo a la izquierda.
            drawRect(e.secondary, Offset(w - i - u * 0.7f, i), Size(u * 0.7f, u * 0.35f))
            drawLine(e.primary.copy(alpha = 0.7f), Offset(i, h - i), Offset(i + u * 3f, h - i), fino * 1.5f)
            drawLine(e.primary.copy(alpha = 0.7f), Offset(i + u * 3f, h - i), Offset(i + u * 3.6f, h - i - u * 0.6f), fino * 1.5f)
        }
        TemaId.NEON -> {
            // Un led encendido arriba a la derecha y un tramo de pista abajo a la izquierda.
            drawRect(e.secondary, Offset(w - i - u * 0.7f, i), Size(u * 0.7f, u * 0.35f))
            drawLine(e.primary.copy(alpha = 0.7f), Offset(i, h - i), Offset(i + u * 3f, h - i), fino * 1.5f)
            drawLine(e.primary.copy(alpha = 0.7f), Offset(i + u * 3f, h - i), Offset(i + u * 3.6f, h - i - u * 0.6f), fino * 1.5f)
        }
        TemaId.HELENO -> enLasCuatro {
            // Una vuelta de greca en cada esquina.
            val p = Path().apply { moveTo(i, i + u * 1.2f); lineTo(i, i); lineTo(i + u * 1.2f, i); lineTo(i + u * 1.2f, i + u * 0.8f); lineTo(i + u * 0.45f, i + u * 0.8f); lineTo(i + u * 0.45f, i + u * 0.4f) }
            drawPath(p, e.tertiary.copy(alpha = 0.8f), style = Stroke(fino * 1.2f, cap = StrokeCap.Square))
        }
        TemaId.MEDIEVAL -> enLasCuatro {
            // Esquina de pan de oro: un cuarto de anillo con su punto, y un punto rojo de rúbrica.
            drawArc(e.tertiary, 180f, 90f, false, Offset(i, i), Size(u * 2f, u * 2f), style = Stroke(fino * 1.4f))
            drawCircle(e.tertiary, u * 0.3f, Offset(i + u * 0.35f, i + u * 0.35f))
            drawCircle(e.primary, u * 0.17f, Offset(i + u * 1.5f, i + u * 0.2f))
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
        TemaId.BOTANICO -> {
            // El alfiler del entomólogo, clavado arriba al centro, con su sombra.
            val c = Offset(w / 2, i + u * 0.2f)
            drawLine(e.onSurface.copy(alpha = 0.25f), c, Offset(c.x + u * 1.2f, c.y + u * 0.9f), fino * 1.2f)
            drawCircle(e.secondary, u * 0.38f, c)
            drawCircle(e.surface.copy(alpha = 0.6f), u * 0.13f, Offset(c.x - u * 0.12f, c.y - u * 0.12f))
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
        TemaId.ANDALUS -> enLasCuatro { estrellaComp(Offset(i + u * 0.7f, i + u * 0.7f), u * 0.7f, e.tertiary, puntas = 8, dentro = 0.6f) }
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
        TemaId.CIBER -> {
            // Consola sin salida: el indicador y el cursor.
            drawRect(l, Offset(f(0.06f), f(0.18f)), Size(f(0.88f), f(0.64f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.2f), f(0.36f)); lineTo(f(0.34f), f(0.5f)); lineTo(f(0.2f), f(0.64f)) }, a, style = tr)
            drawLine(e.secondary, Offset(f(0.42f), f(0.64f)), Offset(f(0.62f), f(0.64f)), tr.width)
        }
        TemaId.NEON -> {
            // Consola sin salida: el indicador y el cursor.
            drawRect(l, Offset(f(0.06f), f(0.18f)), Size(f(0.88f), f(0.64f)), style = tr)
            drawPath(Path().apply { moveTo(f(0.2f), f(0.36f)); lineTo(f(0.34f), f(0.5f)); lineTo(f(0.2f), f(0.64f)) }, a, style = tr)
            drawLine(e.secondary, Offset(f(0.42f), f(0.64f)), Offset(f(0.62f), f(0.64f)), tr.width)
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
        TemaId.MEDIEVAL -> {
            // Tintero seco y la pluma apoyada.
            drawRoundRect(l, Offset(f(0.14f), f(0.58f)), Size(f(0.4f), f(0.32f)), CornerRadius(f(0.06f)), style = tr)
            drawLine(l, Offset(f(0.24f), f(0.58f)), Offset(f(0.24f), f(0.5f)), tr.width)
            drawLine(l, Offset(f(0.44f), f(0.58f)), Offset(f(0.44f), f(0.5f)), tr.width)
            drawPath(Path().apply { moveTo(f(0.36f), f(0.62f)); quadraticTo(f(0.6f), f(0.2f), f(0.92f), f(0.06f)); quadraticTo(f(0.78f), f(0.36f), f(0.36f), f(0.62f)) }, a, style = tr)
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
        TemaId.BOTANICO -> {
            // Frasco de especímenes vacío, con su corcho y la etiqueta.
            drawRoundRect(l, Offset(f(0.22f), f(0.26f)), Size(f(0.56f), f(0.66f)), CornerRadius(f(0.1f)), style = tr)
            drawRect(a, Offset(f(0.3f), f(0.1f)), Size(f(0.4f), f(0.14f)), style = tr)
            drawRect(l, Offset(f(0.32f), f(0.5f)), Size(f(0.36f), f(0.18f)), style = Stroke(tr.width * 0.6f))
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
        TemaId.ANDALUS -> {
            // Arco de herradura sin nadie bajo él.
            drawArc(l, 160f, 220f, false, Offset(f(0.2f), f(0.1f)), Size(f(0.6f), f(0.6f)), style = tr)
            drawLine(l, Offset(f(0.22f), f(0.5f)), Offset(f(0.22f), f(0.92f)), tr.width)
            drawLine(l, Offset(f(0.78f), f(0.5f)), Offset(f(0.78f), f(0.92f)), tr.width)
            drawLine(a, Offset(f(0.12f), f(0.92f)), Offset(f(0.88f), f(0.92f)), tr.width)
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
    }
}

/** La marca del color de cada valor en una casilla de la escala ([fondo] es el de la casilla). */
private fun DrawScope.marcaEscala(t: TemaId, c: Color, fondo: Color) {
    val w = size.width; val h = size.height; val d = 4.dp.toPx()
    when (t) {
        TemaId.ROMANO -> drawRect(c, Offset(0f, h - d), Size(w, d))
        TemaId.CIBER -> drawRect(c, Offset.Zero, Size(d * 0.75f, h))
        TemaId.NEON -> drawRect(c, Offset.Zero, Size(d * 0.75f, h))
        TemaId.HELENO -> drawPath(Path().apply { moveTo(w / 2 - d * 1.6f, h); lineTo(w / 2, h - d * 1.4f); lineTo(w / 2 + d * 1.6f, h); close() }, c)
        TemaId.MEDIEVAL -> drawCircle(c, d * 0.8f, Offset(w / 2, d * 1.4f))
        TemaId.DISTOPIA -> drawRect(c, Offset.Zero, Size(w, d))
        TemaId.REFUGIO -> drawCircle(c, d * 0.9f, Offset(w / 2, h - d * 1.6f), style = Stroke(d * 0.5f))
        TemaId.ORDEN -> drawPath(Path().apply { moveTo(w / 2 - d * 1.5f, h - d * 0.6f); lineTo(w / 2, h - d * 1.8f); lineTo(w / 2 + d * 1.5f, h - d * 0.6f) }, c, style = Stroke(d * 0.55f))
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
        TemaId.BOTANICO -> rotate(-35f, Offset(w - d * 2.2f, h - d * 1.5f)) { drawOval(c, Offset(w - d * 3.4f, h - d * 2f), Size(d * 2.4f, d)) }
        TemaId.NAVIDAD -> {
            drawLine(c, Offset(w / 2, 0f), Offset(w / 2, d * 1.2f), d * 0.3f)
            drawCircle(c, d * 0.75f, Offset(w / 2, d * 1.9f))
        }
        TemaId.SEDA -> {
            drawCircle(c, d * 0.95f, Offset(w / 2, h - d * 1.4f))
            drawRect(fondo, Offset(w / 2 - d * 0.3f, h - d * 1.7f), Size(d * 0.6f, d * 0.6f))
        }
        TemaId.GOTICO -> drawPath(Path().apply {
            moveTo(w / 2 - d, h); lineTo(w / 2 - d, h - d * 1.2f); quadraticTo(w / 2 - d, h - d * 2.2f, w / 2, h - d * 2.6f)
            quadraticTo(w / 2 + d, h - d * 2.2f, w / 2 + d, h - d * 1.2f); lineTo(w / 2 + d, h); close()
        }, c)
        // Una raya de tachón sobre la marca, como una cifra corregida.
        TemaId.ARCANO -> { drawCircle(c, d * 0.8f, Offset(w / 2, h - d * 1.4f)); drawLine(c, Offset(w / 2 - d * 1.6f, h - d * 0.6f), Offset(w / 2 + d * 1.6f, h - d * 2.2f), d * 0.35f) }
        TemaId.ESPACIO -> estrellaComp(Offset(w / 2, d * 1.5f), d * 0.95f, c, puntas = 4, dentro = 0.35f)
        TemaId.ANDALUS -> estrellaComp(Offset(w / 2, h - d * 1.4f), d, c, puntas = 8, dentro = 0.6f)
        TemaId.MONGOL -> drawArc(c, 180f, 180f, true, Offset(w / 2 - d * 1.3f, h - d * 1.3f), Size(d * 2.6f, d * 2.6f))
        TemaId.RENACIMIENTO -> {
            for (n in 0 until 3) {
                val o = d * 0.6f + n * d * 0.8f
                drawLine(c, Offset(o, h), Offset(o + d * 1.6f, h - d * 1.6f), d * 0.3f)
            }
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
    }
}

/** El adorno del botón principal, en [c] (el color del texto, tenue). Queda dentro de la forma del tema. */
private fun DrawScope.adornoBoton(t: TemaId, c: Color, e: ColorScheme) {
    val w = size.width; val h = size.height; val m = 4.dp.toPx(); val fino = 1.dp.toPx()
    when (t) {
        // Filete interior, como el marco de una inscripción.
        TemaId.ROMANO -> drawRect(c, Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(fino * 0.8f))
        // Un led a la derecha y la pista abajo.
        TemaId.CIBER -> {
            drawRect(c, Offset(w - m * 2.6f, h / 2 - m * 0.8f), Size(m * 0.9f, m * 1.6f))
            drawLine(c, Offset(m * 2.5f, h - m), Offset(w * 0.4f, h - m), fino)
        }
        TemaId.NEON -> {
            drawRect(c, Offset(w - m * 2.6f, h / 2 - m * 0.8f), Size(m * 0.9f, m * 1.6f))
            drawLine(c, Offset(m * 2.5f, h - m), Offset(w * 0.4f, h - m), fino)
        }
        // El arquitrabe bajo el frontón.
        TemaId.HELENO -> drawLine(c, Offset(w * 0.06f, h * 0.24f), Offset(w * 0.94f, h * 0.24f), fino)
        // Cuatro puntos de oro junto a las esquinas cóncavas.
        TemaId.MEDIEVAL -> listOf(Offset(m * 2f, m * 2f), Offset(w - m * 2f, m * 2f), Offset(m * 2f, h - m * 2f), Offset(w - m * 2f, h - m * 2f)).forEach { drawCircle(e.tertiary, fino * 1.6f, it) }
        // Doble recuadro de sello de goma.
        TemaId.DISTOPIA -> {
            drawRect(c, Offset(m * 0.7f, m * 0.7f), Size(w - m * 1.4f, h - m * 1.4f), style = Stroke(fino))
            drawRect(c, Offset(m * 1.4f, m * 1.4f), Size(w - m * 2.8f, h - m * 2.8f), style = Stroke(fino * 0.6f))
        }
        // Un destello atómico a la izquierda.
        TemaId.REFUGIO -> estrellaComp(Offset(m * 3.2f, h / 2), m * 1.1f, c, puntas = 4, dentro = 0.3f)
        // Remaches a los lados.
        TemaId.ORDEN -> listOf(m * 2f, w - m * 2f).forEach { x -> drawCircle(c, fino * 2f, Offset(x, h / 2)) }
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
        // Nervaduras en las dos puntas de la hoja.
        TemaId.BOTANICO -> {
            drawLine(c, Offset(m * 1.5f, h - m * 1.5f), Offset(m * 4.5f, h - m * 3.2f), fino)
            drawLine(c, Offset(w - m * 1.5f, m * 1.5f), Offset(w - m * 4.5f, m * 3.2f), fino)
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
        // Costura de fieltro.
        TemaId.MONGOL -> drawRoundRect(c, Offset(m, m), Size(w - 2 * m, h - 2 * m), CornerRadius(m * 1.2f), style = Stroke(fino, pathEffect = PathEffect.dashPathEffect(floatArrayOf(m * 1.5f, m))))
        // La esquina doblada de la hoja.
        TemaId.RENACIMIENTO -> {
            val s = 12.dp.toPx()
            drawPath(Path().apply { moveTo(w - s, h); lineTo(w - s, h - s); lineTo(w, h - s); close() }, c)
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
        TemaId.CIBER -> {
            // Indicador de consola.
            drawPath(Path().apply { moveTo(f(0.08f), f(0.2f)); lineTo(f(0.4f), f(0.5f)); lineTo(f(0.08f), f(0.8f)) }, a, style = tr)
            drawLine(e.secondary, Offset(f(0.5f), f(0.82f)), Offset(f(0.92f), f(0.82f)), tr.width)
        }
        TemaId.NEON -> {
            // Indicador de consola.
            drawPath(Path().apply { moveTo(f(0.08f), f(0.2f)); lineTo(f(0.4f), f(0.5f)); lineTo(f(0.08f), f(0.8f)) }, a, style = tr)
            drawLine(e.secondary, Offset(f(0.5f), f(0.82f)), Offset(f(0.92f), f(0.82f)), tr.width)
        }
        TemaId.HELENO -> {
            // Papiro entre sus dos rodillos.
            drawRect(l, Offset(f(0.2f), f(0.22f)), Size(f(0.6f), f(0.56f)), style = tr)
            drawRoundRect(a, Offset(f(0.04f), f(0.1f)), Size(f(0.16f), f(0.8f)), CornerRadius(f(0.08f)), style = tr)
            drawRoundRect(a, Offset(f(0.8f), f(0.1f)), Size(f(0.16f), f(0.8f)), CornerRadius(f(0.08f)), style = tr)
        }
        TemaId.MEDIEVAL -> {
            // Pluma y tintero.
            drawRect(l, Offset(f(0.08f), f(0.6f)), Size(f(0.36f), f(0.32f)))
            drawPath(Path().apply { moveTo(f(0.3f), f(0.62f)); quadraticTo(f(0.56f), f(0.2f), f(0.96f), f(0.04f)); quadraticTo(f(0.8f), f(0.4f), f(0.3f), f(0.62f)) }, a)
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
        TemaId.BOTANICO -> {
            // Cuaderno de campo con una hoja prensada.
            drawRoundRect(l, Offset(f(0.14f), f(0.04f)), Size(f(0.72f), f(0.92f)), CornerRadius(f(0.06f)), style = tr)
            rotate(-30f, Offset(f(0.5f), f(0.5f))) { drawOval(a, Offset(f(0.3f), f(0.38f)), Size(f(0.4f), f(0.22f))) }
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
        TemaId.ANDALUS -> {
            // Cálamo de caña y tintero.
            drawLine(a, Offset(f(0.16f), f(0.94f)), Offset(f(0.84f), f(0.1f)), tr.width * 1.3f)
            drawPath(Path().apply { moveTo(f(0.84f), f(0.1f)); lineTo(f(0.98f), f(0.02f)); lineTo(f(0.92f), f(0.2f)); close() }, a)
            drawCircle(l, f(0.16f), Offset(f(0.24f), f(0.3f)), style = tr)
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
        TemaId.CIBER -> {
            var y = 0f
            while (y < h) { drawRect(c, Offset(0f, y), Size(2 * d, 4 * d)); y += 7 * d }
        }
        TemaId.NEON -> {
            var y = 0f
            while (y < h) { drawRect(c, Offset(0f, y), Size(2 * d, 4 * d)); y += 7 * d }
        }
        TemaId.HELENO -> {
            drawRect(c, Offset.Zero, Size(1.4f * d, h))
            drawRect(c, Offset(3.2f * d, 0f), Size(1.4f * d, h))
        }
        TemaId.MEDIEVAL -> {
            drawRect(e.tertiary, Offset(d, 0f), Size(1.2f * d, h))
            var y = 5 * d
            while (y < h) { drawCircle(e.primary, 1.5f * d, Offset(4.2f * d, y)); y += 9 * d }
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
        TemaId.BOTANICO -> {
            // Un tallo que ondula.
            val p = Path(); var y = 0f
            p.moveTo(3 * d, 0f)
            while (y <= h) { p.lineTo(3 * d + 1.8f * d * sin(y / (5 * d)), y); y += 2f }
            drawPath(p, c, style = Stroke(1.3f * d))
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
        TemaId.ANDALUS -> {
            // Cinta de lazo en zigzag.
            val p = Path(); var y = 0f; var izq = true
            p.moveTo(0.5f * d, 0f)
            while (y < h) { y += 4 * d; izq = !izq; p.lineTo(if (izq) 0.5f * d else w - 0.5f * d, y) }
            drawPath(p, e.tertiary, style = Stroke(1.4f * d))
        }
        TemaId.MONGOL -> {
            drawRect(c, Offset.Zero, Size(4.5f * d, h))
            drawLine(e.secondaryContainer, Offset(2.2f * d, 0f), Offset(2.2f * d, h), 0.8f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 2 * d)))
        }
        TemaId.RENACIMIENTO -> {
            drawLine(c, Offset(1.5f * d, 0f), Offset(2 * d, h), 0.9f * d)
            drawLine(c.copy(alpha = 0.6f), Offset(3.5f * d, 2 * d), Offset(3 * d, h - 3 * d), 0.7f * d)
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
    }
}
