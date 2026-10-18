@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  El diario como objeto: rollos, libros, cartas, expedientes y terminales
// =====================================================================

/** En qué se escribe el diario en cada tema. */
enum class Soporte { ROLLO, LIBRO, SOBRE, CARPETA, PANTALLA }

fun soporte(t: TemaId): Soporte = when (t) {
    TemaId.ROMANO, TemaId.HELENO, TemaId.SEDA -> Soporte.ROLLO
    TemaId.MEDIEVAL, TemaId.COLONOS, TemaId.ORDEN, TemaId.MUERTOS, TemaId.URSS, TemaId.LABORATORIO -> Soporte.LIBRO
    TemaId.NAPOLEON, TemaId.NAVIDAD -> Soporte.SOBRE
    TemaId.DISTOPIA -> Soporte.CARPETA
    TemaId.CIBER, TemaId.REFUGIO -> Soporte.PANTALLA
}

/** Colores del objeto físico (cuero, papiro, lacre…); no cambian con el modo claro u oscuro. */
class Materia(val cuerpo: Color, val sombra: Color, val brillo: Color, val acento: Color, val detalle: Color, val hoja: Color, val tinta: Color,
              val extra: Color = acento, val textoCuerpo: Color = tinta)

private fun c(x: Long) = Color(x)

fun materia(t: TemaId): Materia = when (t) {
    TemaId.ROMANO -> Materia(c(0xFFE3CC94), c(0xFFB08C52), c(0xFFF3E4BC), c(0xFF6B4423), c(0xFFC9A54A), c(0xFFEEDCAE), c(0xFF2A1E14), extra = c(0xFF8E2B1F))
    TemaId.HELENO -> Materia(c(0xFFE6D3A6), c(0xFFB5935E), c(0xFFF4E6C4), c(0xFF2B1D14), c(0xFFC2652F), c(0xFFEFDFB8), c(0xFF1F1712), extra = c(0xFF1A1410))
    TemaId.SEDA -> Materia(c(0xFF2B4A9E), c(0xFF15265F), c(0xFF5A78C8), c(0xFF3E9E83), c(0xFFD9B25B), c(0xFF233E8C), c(0xFFF2EAD3), extra = c(0xFFC0392B))
    TemaId.MEDIEVAL -> Materia(c(0xFF6B2E1A), c(0xFF3E190D), c(0xFF8A4429), c(0xFFD4A93C), c(0xFFB08D57), c(0xFFEFE3C2), c(0xFF2B1D12), extra = c(0xFFB3261E))
    TemaId.COLONOS -> Materia(c(0xFF4A3423), c(0xFF2B1D12), c(0xFF6A4D35), c(0xFFB8893A), c(0xFF2E2016), c(0xFFF1E6CC), c(0xFF1E1A16), extra = c(0xFF9E2B25))
    TemaId.ORDEN -> Materia(c(0xFF4A525C), c(0xFF2A2F35), c(0xFF6E7883), c(0xFFC9A54A), c(0xFFA3172E), c(0xFFD6D9DC), c(0xFF1C2127))
    TemaId.MUERTOS -> Materia(c(0xFFC2185B), c(0xFF7B0F3A), c(0xFFE24A86), c(0xFFFFA726), c(0xFF6A1B9A), c(0xFFFFF4E2), c(0xFF2A1033), extra = c(0xFF26C6DA))
    TemaId.URSS -> Materia(c(0xFFB71C1C), c(0xFF6E0F0F), c(0xFFD84343), c(0xFFE8B923), c(0xFF161414), c(0xFFEFE6D2), c(0xFF161414))
    TemaId.LABORATORIO -> Materia(c(0xFF20323F), c(0xFF101B23), c(0xFF3A5363), c(0xFF3CD2E5), c(0xFFFFFFFF), c(0xFFF7FBFC), c(0xFF1B2733))
    TemaId.NAPOLEON -> Materia(c(0xFFF1E6CC), c(0xFFC9B48A), c(0xFFFBF5E6), c(0xFFA3172E), c(0xFF1E3A8A), c(0xFFF6F0E0), c(0xFF14213D), extra = c(0xFFD9B25B))
    TemaId.NAVIDAD -> Materia(c(0xFFB71C1C), c(0xFF7A1010), c(0xFFD84343), c(0xFFC9A227), c(0xFF1B5E20), c(0xFFFBF6EC), c(0xFF1E2A22), extra = c(0xFFFF5252), textoCuerpo = c(0xFFFBF6EC))
    TemaId.DISTOPIA -> Materia(c(0xFFD8C08A), c(0xFFA88F58), c(0xFFE8D4A4), c(0xFFC8102E), c(0xFF141414), c(0xFFF2EFE6), c(0xFF141414))
    TemaId.CIBER -> Materia(c(0xFF0B121C), c(0xFF05080E), c(0xFF15202E), c(0xFF00E5FF), c(0xFFFF2E88), c(0xFF05080E), c(0xFFD6F3F7), extra = c(0xFFF2C94C))
    TemaId.REFUGIO -> Materia(c(0xFFC9C2A6), c(0xFF8E886F), c(0xFFE2DCC4), c(0xFF3DFF7F), c(0xFF2F8F4E), c(0xFF031A0B), c(0xFF7CF098))
}

/** Lo que dice el botón de guardar en cada tema. */
fun textoGuardar(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "SIGNARE"; TemaId.CIBER -> "git commit"; TemaId.HELENO -> "guardar"; TemaId.MEDIEVAL -> "asentar"
    TemaId.DISTOPIA -> "ARCHIVAR"; TemaId.REFUGIO -> "GUARDAR"; TemaId.ORDEN -> "REGISTRAR"; TemaId.LABORATORIO -> "guardar registro"
    TemaId.COLONOS -> "asentar"; TemaId.NAPOLEON -> "SELLAR"; TemaId.MUERTOS -> "guardar"; TemaId.NAVIDAD -> "guardar carta"
    TemaId.SEDA -> "sellar el rollo"; TemaId.URSS -> "ENTREGAR"
}

/** Lo que cierra cada página al leerla. */
fun colofon(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "· FINIS ·"; TemaId.CIBER -> "-- EOF --"; TemaId.HELENO -> "ΤΕΛΟΣ"; TemaId.MEDIEVAL -> "Aquí se acaba el folio."
    TemaId.DISTOPIA -> "FIN DE LA DECLARACIÓN"; TemaId.REFUGIO -> "FIN DE LA ENTRADA"; TemaId.ORDEN -> "FIN DEL PARTE"; TemaId.LABORATORIO -> "fin del registro"
    TemaId.COLONOS -> "Asentado."; TemaId.NAPOLEON -> "Dado en el cuartel general."; TemaId.MUERTOS -> "✿ que no se olvide ✿"
    TemaId.NAVIDAD -> "Con cariño, junto al fuego."; TemaId.SEDA -> "Fin del rollo."; TemaId.URSS -> "INFORME ENTREGADO"
}

private fun pista(s: Soporte) = when (s) {
    Soporte.ROLLO -> "toca para desenrollar y escribir"
    Soporte.LIBRO -> "toca para abrir el libro"
    Soporte.SOBRE -> "toca para abrir la carta"
    Soporte.CARPETA -> "TOCA PARA ABRIR EL EXPEDIENTE"
    Soporte.PANTALLA -> "> toca para abrir el editor_"
}

/** Un esquema de color con los colores del objeto, para que bandas, botones y fichas combinen con la página. */
@Composable
private fun esquemaPagina(p: Pagina): ColorScheme {
    val b = MaterialTheme.colorScheme
    return b.copy(
        primary = p.acento, onPrimary = if (p.oscura) Color(0xFF05080E) else Color.White, secondary = p.detalle, tertiary = p.detalle,
        background = p.color, onBackground = p.tinta, surface = p.color, onSurface = p.tinta, surfaceVariant = p.color.copy(alpha = 0.6f),
        onSurfaceVariant = p.tenue, outline = p.tenue.copy(alpha = 0.6f), outlineVariant = p.tenue.copy(alpha = 0.4f),
        primaryContainer = p.acento.copy(alpha = 0.18f), onPrimaryContainer = p.tinta, secondaryContainer = p.detalle.copy(alpha = 0.18f),
    )
}

// =====================================================================
//  El objeto cerrado: «Hoy» en el Diario y en la hoja de la noche
// =====================================================================

/** El diario de un día como objeto del tema (rollo, libro, carta, expediente o terminal). Al tocarlo se abre a pantalla completa. */
@Composable
fun ObjetoDiario(fecha: String, texto: String, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, onClick: () -> Unit) {
    val m = materia(tema); val s = soporte(tema)
    val sentir = rememberSentir()
    val esquemaEmblema = MaterialTheme.colorScheme.copy(primary = m.brillo, secondary = m.detalle, tertiary = m.brillo, background = m.acento, onBackground = m.brillo)
    val palabras = Texto.palabras(texto)
    val vista = texto.trim().replace(Regex("\\s+"), " ").take(140)
    val alto = when (s) { Soporte.LIBRO -> 176.dp; Soporte.SOBRE -> 176.dp; else -> 168.dp }
    BoxWithConstraints(modifier.fillMaxWidth().height(alto).clickable { sentir(Sentidos.Evento.PASAR); onClick() }) {
        Canvas(Modifier.fillMaxSize()) {
            when (s) {
                Soporte.ROLLO -> rolloAbierto(tema, m)
                Soporte.LIBRO -> libroCerrado(tema, m)
                Soporte.SOBRE -> sobre(tema, m, esquemaEmblema)
                Soporte.CARPETA -> carpeta(m)
                Soporte.PANTALLA -> pantalla(tema, m)
            }
        }
        val w = maxWidth; val h = maxHeight
        // Dónde va el texto según el objeto.
        val (izq, arr, der, aba) = when (s) {
            Soporte.ROLLO -> listOf(w * 0.15f, h * 0.14f, w * 0.15f, h * 0.12f)
            Soporte.LIBRO -> listOf(w * 0.25f, h * 0.24f, w * 0.17f, h * 0.24f)
            Soporte.SOBRE -> listOf(w * 0.07f, h * 0.62f, w * 0.07f, h * 0.07f)
            Soporte.CARPETA -> listOf(w * 0.06f, h * 0.34f, w * 0.06f, h * 0.06f)
            Soporte.PANTALLA -> if (tema == TemaId.REFUGIO) listOf(w * 0.1f, h * 0.13f, w * 0.1f, h * 0.2f) else listOf(w * 0.05f, h * 0.22f, w * 0.05f, h * 0.06f)
        }
        val tinta = when (s) { Soporte.SOBRE, Soporte.CARPETA -> m.textoCuerpo; Soporte.PANTALLA -> m.tinta; else -> m.tinta }
        val acento = when (s) { Soporte.PANTALLA -> m.acento; Soporte.SOBRE -> if (tema == TemaId.NAVIDAD) m.acento else m.detalle; Soporte.CARPETA -> m.acento; else -> m.extra }
        val ty = MaterialTheme.typography
        val mono = s == Soporte.PANTALLA || s == Soporte.CARPETA
        val fuente = when { tema == TemaId.REFUGIO -> LetraExtra.vt323; mono -> Letra.codigo; else -> null }
        Column(Modifier.fillMaxSize().padding(start = izq, top = arr, end = der, bottom = aba), verticalArrangement = Arrangement.Center,
            horizontalAlignment = if (s == Soporte.LIBRO) Alignment.CenterHorizontally else Alignment.Start) {
            if (tema == TemaId.CIBER) Text("$ nano ~/diario/$fecha.log", fontFamily = Letra.codigo, fontSize = 11.sp, color = m.acento, maxLines = 1)
            Text(Estilo.fecha(fecha), style = ty.titleSmall, color = acento, maxLines = 1, overflow = TextOverflow.Ellipsis,
                fontFamily = fuente ?: ty.titleSmall.fontFamily, textAlign = if (s == Soporte.LIBRO) TextAlign.Center else TextAlign.Start)
            Text(if (vista.isBlank()) pista(s) else vista, color = tinta.copy(alpha = if (vista.isBlank()) 0.65f else 0.9f),
                fontFamily = fuente ?: if (s == Soporte.LIBRO || s == Soporte.ROLLO || s == Soporte.SOBRE) Letra.garamond else Letra.texto,
                fontSize = if (tema == TemaId.REFUGIO) 17.sp else 13.5.sp, lineHeight = if (tema == TemaId.REFUGIO) 18.sp else 17.sp,
                maxLines = if (s == Soporte.SOBRE) 2 else 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp),
                textAlign = if (s == Soporte.LIBRO) TextAlign.Center else TextAlign.Start)
            if (palabras > 0) Text(if (s == Soporte.PANTALLA) "[$palabras palabras]" else "$palabras palabras",
                fontSize = 10.5.sp, color = tinta.copy(alpha = 0.6f), fontFamily = fuente ?: Letra.texto, modifier = Modifier.padding(top = 3.dp))
        }
        if (s == Soporte.CARPETA) Text("ARCHIVO", color = m.acento.copy(alpha = 0.75f), fontFamily = LetraExtra.oswald, fontSize = 15.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = h * 0.36f, end = w * 0.06f).rotate(-10f)
                .border(2.dp, m.acento.copy(alpha = 0.7f)).padding(horizontal = 6.dp, vertical = 1.dp))
        if (s == Soporte.CARPETA) Text("EXPEDIENTE", color = m.detalle, fontFamily = LetraExtra.oswald, fontSize = 11.sp,
            modifier = Modifier.padding(start = w * 0.07f, top = h * 0.035f))
    }
}

/** Rollo abierto: la hoja entre dos varillas. */
private fun DrawScope.rolloAbierto(t: TemaId, m: Materia) {
    val w = size.width; val h = size.height
    val r = h * 0.075f
    val arr = h * 0.09f; val aba = h * 0.91f
    val x0 = w * 0.075f; val x1 = w * 0.925f
    // Hoja con fibras.
    drawRect(m.hoja, Offset(x0, arr + h * 0.03f), Size(x1 - x0, aba - arr - h * 0.06f))
    val fibras = java.util.Random(7)
    repeat(26) {
        val y = arr + h * 0.05f + fibras.nextFloat() * (aba - arr - h * 0.1f)
        drawLine(m.sombra.copy(alpha = 0.12f), Offset(x0, y), Offset(x1, y + (fibras.nextFloat() - 0.5f) * 3f), 1.2f)
    }
    when (t) {
        TemaId.SEDA -> listOf(arr + h * 0.06f, aba - h * 0.06f).forEach { y -> drawLine(m.detalle, Offset(x0, y), Offset(x1, y), 3f) }
        TemaId.HELENO -> listOf(arr + h * 0.075f, aba - h * 0.075f).forEach { y -> grecaPequena(Offset(x0 + r, y), x1 - x0 - 2 * r, h * 0.05f, m.extra) }
        else -> {}
    }
    // Varillas: cilindros con sombra y pomos.
    listOf(x0, x1).forEach { x ->
        val cil = Brush.horizontalGradient(listOf(m.sombra, m.brillo, m.cuerpo, m.sombra), startX = x - r, endX = x + r)
        drawRoundRect(cil, Offset(x - r, arr), Size(2 * r, aba - arr), CornerRadius(r * 0.6f))
        drawLine(m.acento, Offset(x, arr - h * 0.06f), Offset(x, aba + h * 0.06f), r * 0.45f, StrokeCap.Round)
        listOf(arr - h * 0.06f, aba + h * 0.06f).forEach { y -> drawCircle(m.detalle, r * 0.55f, Offset(x, y)); drawCircle(m.brillo.copy(alpha = 0.6f), r * 0.2f, Offset(x - r * 0.15f, y - r * 0.15f)) }
    }
    if (t == TemaId.SEDA) {
        // Cordón rojo con borla.
        val a = Offset(x1 + r * 0.6f, h * 0.45f)
        val p = Path().apply { moveTo(a.x, a.y); quadraticTo(w * 0.99f, h * 0.55f, w * 0.975f, h * 0.7f) }
        drawPath(p, m.extra, style = Stroke(3f))
        drawRoundRect(m.extra, Offset(w * 0.965f, h * 0.7f), Size(w * 0.02f, h * 0.12f), CornerRadius(4f))
    }
    if (t == TemaId.ROMANO) {
        // Etiqueta (titulus) colgando de la varilla izquierda.
        drawLine(m.acento, Offset(x0, aba - h * 0.02f), Offset(x0 - r * 0.2f, aba + h * 0.03f), 1.5f)
        drawRect(m.extra, Offset(x0 - r * 1.4f, aba - h * 0.015f), Size(r * 1.6f, h * 0.06f))
    }
}

private fun DrawScope.grecaPequena(o: Offset, largo: Float, alto: Float, color: Color) {
    val u = alto / 3f; val paso = u * 4; var x = 0f
    val p = Path()
    while (x + paso <= largo) {
        p.moveTo(o.x + x, o.y + alto / 2); p.lineTo(o.x + x, o.y - alto / 2); p.lineTo(o.x + x + u * 3, o.y - alto / 2); p.lineTo(o.x + x + u * 3, o.y + u / 2)
        p.lineTo(o.x + x + u, o.y + u / 2); p.lineTo(o.x + x + u, o.y - u / 2); p.lineTo(o.x + x + u * 2, o.y - u / 2)
        p.moveTo(o.x + x, o.y + alto / 2); p.lineTo(o.x + x + paso, o.y + alto / 2)
        x += paso
    }
    drawPath(p, color, style = Stroke(1.6f))
}

/** Libro cerrado visto desde arriba: cubierta, lomo, cantos y adornos de cada tema. */
private fun DrawScope.libroCerrado(t: TemaId, m: Materia) {
    val w = size.width; val h = size.height
    val rad = CornerRadius(8f)
    // Cantos de las hojas.
    drawRoundRect(m.hoja, Offset(w * 0.045f, h * 0.085f), Size(w * 0.93f, h * 0.88f), rad)
    for (i in 1..4) drawLine(m.sombra.copy(alpha = 0.25f), Offset(w * 0.06f, h * (0.94f + i * 0.006f) - 2f), Offset(w * 0.97f, h * (0.94f + i * 0.006f) - 2f), 1f)
    // Cubierta.
    val ini = Offset(w * 0.02f, h * 0.04f); val tam = Size(w * 0.925f, h * 0.885f)
    drawRoundRect(Brush.verticalGradient(listOf(m.brillo, m.cuerpo, m.sombra), startY = ini.y, endY = ini.y + tam.height), ini, tam, rad)
    if (t == TemaId.LABORATORIO) {
        // Cuaderno de composición: jaspeado blanco.
        val az = java.util.Random(5)
        repeat(70) {
            val x = ini.x + az.nextFloat() * tam.width; val y = ini.y + az.nextFloat() * tam.height
            drawOval(m.detalle.copy(alpha = 0.18f), Offset(x, y), Size(6f + az.nextFloat() * 22f, 3f + az.nextFloat() * 10f))
        }
    }
    // Lomo.
    val lomo = w * 0.075f
    drawRect(Brush.horizontalGradient(listOf(m.sombra, m.cuerpo, m.sombra), startX = ini.x, endX = ini.x + lomo), ini, Size(lomo, tam.height))
    listOf(0.18f, 0.38f, 0.62f, 0.82f).forEach { f -> drawLine(m.acento.copy(alpha = 0.8f), Offset(ini.x + 3f, ini.y + tam.height * f), Offset(ini.x + lomo - 3f, ini.y + tam.height * f), 3f) }
    // Filete dorado.
    val m0 = 11.dp.toPx()
    drawRect(m.acento.copy(alpha = 0.85f), Offset(ini.x + lomo + m0, ini.y + m0), Size(tam.width - lomo - 2 * m0, tam.height - 2 * m0), style = Stroke(2f))
    drawRect(m.acento.copy(alpha = 0.5f), Offset(ini.x + lomo + m0 + 5f, ini.y + m0 + 5f), Size(tam.width - lomo - 2 * m0 - 10f, tam.height - 2 * m0 - 10f), style = Stroke(1f))
    val esquinas = listOf(Offset(ini.x + lomo + m0, ini.y + m0), Offset(ini.x + tam.width - m0, ini.y + m0), Offset(ini.x + lomo + m0, ini.y + tam.height - m0), Offset(ini.x + tam.width - m0, ini.y + tam.height - m0))
    when (t) {
        TemaId.MEDIEVAL -> {
            esquinas.forEach { drawCircle(m.acento, 7f, it); drawCircle(m.sombra, 3f, it) }
            // Broches de latón.
            listOf(0.3f, 0.7f).forEach { f ->
                drawRoundRect(m.detalle, Offset(ini.x + tam.width - w * 0.05f, ini.y + tam.height * f - h * 0.045f), Size(w * 0.075f, h * 0.09f), CornerRadius(3f))
                drawCircle(m.sombra, 3f, Offset(ini.x + tam.width + w * 0.01f, ini.y + tam.height * f))
            }
        }
        TemaId.COLONOS -> {
            val x = ini.x + tam.width * 0.9f
            drawRect(m.detalle, Offset(x - w * 0.025f, ini.y), Size(w * 0.05f, tam.height + h * 0.04f))
            drawRect(m.acento, Offset(x - w * 0.033f, ini.y + tam.height * 0.44f), Size(w * 0.066f, tam.height * 0.12f), style = Stroke(3f))
        }
        TemaId.ORDEN -> {
            val k = 26f
            listOf(Triple(ini.x + lomo, ini.y, 1f to 1f), Triple(ini.x + tam.width, ini.y, -1f to 1f), Triple(ini.x + lomo, ini.y + tam.height, 1f to -1f), Triple(ini.x + tam.width, ini.y + tam.height, -1f to -1f))
                .forEach { (x, y, s) -> drawPath(Path().apply { moveTo(x, y); lineTo(x + s.first * k, y); lineTo(x, y + s.second * k); close() }, m.acento) }
            drawRect(m.detalle, Offset(ini.x + tam.width * 0.72f, ini.y + tam.height - 4f), Size(w * 0.03f, h * 0.1f))
        }
        TemaId.MUERTOS -> {
            val colores = listOf(m.acento, m.extra, Color(0xFFFFEB3B), m.detalle, Color(0xFF66BB6A))
            var x = ini.x + lomo + 4f; var k = 0
            while (x < ini.x + tam.width - 16f) {
                drawPath(Path().apply { moveTo(x, ini.y + 3f); lineTo(x + 16f, ini.y + 3f); lineTo(x + 8f, ini.y + 15f); close() }, colores[k % colores.size])
                drawPath(Path().apply { moveTo(x, ini.y + tam.height - 3f); lineTo(x + 16f, ini.y + tam.height - 3f); lineTo(x + 8f, ini.y + tam.height - 15f); close() }, colores[(k + 2) % colores.size])
                x += 18f; k++
            }
        }
        TemaId.URSS -> {
            val c = Offset(ini.x + lomo + w * 0.08f, ini.y + tam.height * 0.5f)
            val est = Path(); for (i in 0 until 10) { val rr = if (i % 2 == 0) h * 0.13f else h * 0.05f; val a = Math.toRadians(-90.0 + 36.0 * i); val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat()); if (i == 0) est.moveTo(p.x, p.y) else est.lineTo(p.x, p.y) }
            est.close(); drawPath(est, m.acento)
            drawRect(m.detalle, Offset(ini.x + lomo, ini.y + tam.height * 0.86f), Size(tam.width - lomo, tam.height * 0.06f))
        }
        else -> {}
    }
    // Placa para el título.
    val placa = Offset(w * 0.22f, h * 0.2f); val tp = Size(w * 0.63f, h * 0.6f)
    drawRoundRect(m.hoja, placa, tp, CornerRadius(4f))
    drawRoundRect(m.acento.copy(alpha = 0.9f), placa, tp, CornerRadius(4f), style = Stroke(2f))
    if (t == TemaId.LABORATORIO) for (i in 1..3) drawLine(m.acento.copy(alpha = 0.35f), Offset(placa.x + 8f, placa.y + tp.height * (0.25f * i + 0.12f)), Offset(placa.x + tp.width - 8f, placa.y + tp.height * (0.25f * i + 0.12f)), 1f)
}

/** Carta con solapa, lacre y un adorno (cinta, acebo). */
private fun DrawScope.sobre(t: TemaId, m: Materia, e: ColorScheme) {
    val w = size.width; val h = size.height
    val ini = Offset(w * 0.02f, h * 0.05f); val tam = Size(w * 0.96f, h * 0.9f)
    drawRoundRect(Color.Black.copy(alpha = 0.12f), ini + Offset(4f, 6f), tam, CornerRadius(6f))
    drawRoundRect(m.cuerpo, ini, tam, CornerRadius(6f))
    // Pliegues de abajo.
    drawLine(m.sombra.copy(alpha = 0.6f), Offset(ini.x, ini.y + tam.height), Offset(w * 0.42f, h * 0.5f), 1.5f)
    drawLine(m.sombra.copy(alpha = 0.6f), Offset(ini.x + tam.width, ini.y + tam.height), Offset(w * 0.58f, h * 0.5f), 1.5f)
    // Solapa.
    val solapa = Path().apply { moveTo(ini.x, ini.y); lineTo(w / 2, h * 0.47f); lineTo(ini.x + tam.width, ini.y); close() }
    drawPath(solapa, m.brillo); drawPath(solapa, m.sombra, style = Stroke(1.5f))
    // Timbre.
    val st = Offset(ini.x + tam.width - w * 0.13f, ini.y + h * 0.07f); val ts = Size(w * 0.09f, h * 0.2f)
    drawRect(m.hoja, st, ts); drawRect(if (t == TemaId.NAVIDAD) m.acento else m.detalle, st + Offset(4f, 4f), Size(ts.width - 8f, ts.height - 8f), style = Stroke(2f))
    if (t == TemaId.NAVIDAD) {
        // Acebo.
        val o = Offset(ini.x + w * 0.09f, ini.y + h * 0.12f)
        listOf(-35f, 35f, 200f).forEach { g -> rotate(g, o) { drawOval(m.detalle, o + Offset(2f, -h * 0.035f), Size(w * 0.075f, h * 0.07f)) } }
        listOf(Offset(0f, 0f), Offset(9f, 5f), Offset(-6f, 8f)).forEach { drawCircle(m.extra, 5.5f, o + it) }
    } else {
        // Cinta azul cruzando la esquina.
        drawPath(Path().apply { moveTo(ini.x, ini.y + h * 0.38f); lineTo(ini.x + w * 0.16f, ini.y); lineTo(ini.x + w * 0.22f, ini.y); lineTo(ini.x, ini.y + h * 0.52f); close() }, m.detalle)
    }
    // Lacre con el emblema.
    val c = Offset(w / 2, h * 0.47f); val r = h * 0.13f
    val borde = Path(); for (i in 0..36) { val a = 2 * PI * i / 36; val rr = r * (1f + 0.07f * sin(a * 5 + 1).toFloat()); val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat()); if (i == 0) borde.moveTo(p.x, p.y) else borde.lineTo(p.x, p.y) }
    borde.close()
    drawPath(borde, m.acento)
    drawCircle(Color.Black.copy(alpha = 0.18f), r * 0.78f, c, style = Stroke(r * 0.08f))
    val k = r * 0.62f
    inset(c.x - k, c.y - k, w - (c.x + k), h - (c.y + k)) { dibujarEmblema(t, 0.3f, e, true) }
}

/** Carpeta de archivo color manila con su pestaña y una hoja asomando. */
private fun DrawScope.carpeta(m: Materia) {
    val w = size.width; val h = size.height
    drawRoundRect(m.sombra, Offset(0f, h * 0.12f), Size(w, h * 0.86f), CornerRadius(5f))
    drawRoundRect(m.sombra, Offset(w * 0.04f, h * 0.02f), Size(w * 0.32f, h * 0.16f), CornerRadius(8f))
    drawRect(m.hoja, Offset(w * 0.06f, h * 0.08f), Size(w * 0.88f, h * 0.3f))
    for (i in 0..3) drawLine(m.tinta.copy(alpha = 0.25f), Offset(w * 0.1f, h * (0.13f + i * 0.04f)), Offset(w * (0.5f + i * 0.08f), h * (0.13f + i * 0.04f)), 2f)
    drawRoundRect(Brush.verticalGradient(listOf(m.brillo, m.cuerpo), startY = h * 0.24f, endY = h), Offset(0f, h * 0.24f), Size(w, h * 0.76f), CornerRadius(5f))
    drawLine(m.sombra, Offset(0f, h * 0.24f), Offset(w, h * 0.24f), 2f)
    // Cordel.
    drawCircle(m.sombra, 6f, Offset(w * 0.5f, h * 0.9f)); drawLine(m.detalle.copy(alpha = 0.6f), Offset(w * 0.5f, h * 0.9f), Offset(w * 0.58f, h * 0.99f), 1.5f)
}

/** Ventana de terminal (cyberpunk) o monitor de fósforo verde (refugio). */
private fun DrawScope.pantalla(t: TemaId, m: Materia) {
    val w = size.width; val h = size.height
    if (t == TemaId.REFUGIO) {
        drawRoundRect(Brush.verticalGradient(listOf(m.brillo, m.cuerpo, m.sombra)), Offset.Zero, size, CornerRadius(28f))
        val pi = Offset(w * 0.06f, h * 0.07f); val ps = Size(w * 0.88f, h * 0.76f)
        drawRoundRect(m.hoja, pi, ps, CornerRadius(30f))
        drawRoundRect(Brush.radialGradient(listOf(m.acento.copy(alpha = 0.16f), Color.Transparent), center = pi + Offset(ps.width / 2, ps.height / 2), radius = ps.width * 0.6f), pi, ps, CornerRadius(30f))
        var y = pi.y; while (y < pi.y + ps.height) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(pi.x + 8f, y), Offset(pi.x + ps.width - 8f, y), 1.5f); y += 5f }
        listOf(0.8f, 0.87f).forEach { f -> drawCircle(m.sombra, h * 0.035f, Offset(w * f, h * 0.915f)) }
        for (i in 0 until 6) drawLine(m.sombra, Offset(w * (0.1f + i * 0.03f), h * 0.89f), Offset(w * (0.1f + i * 0.03f), h * 0.94f), 2f)
    } else {
        drawRoundRect(m.cuerpo, Offset.Zero, size, CornerRadius(10f))
        drawRoundRect(m.brillo, Offset.Zero, Size(w, h * 0.15f), CornerRadius(10f))
        drawRect(m.brillo, Offset(0f, h * 0.08f), Size(w, h * 0.07f))
        listOf(m.detalle, m.extra, m.acento).forEachIndexed { i, col -> drawCircle(col, h * 0.03f, Offset(w * 0.03f + i * h * 0.09f, h * 0.075f)) }
        var y = h * 0.15f; while (y < h) { drawLine(m.acento.copy(alpha = 0.04f), Offset(0f, y), Offset(w, y), 1f); y += 4f }
        drawRoundRect(m.acento.copy(alpha = 0.7f), Offset.Zero, size, CornerRadius(10f), style = Stroke(2f))
        drawLine(m.detalle.copy(alpha = 0.5f), Offset(3f, h * 0.15f), Offset(w * 0.4f, h * 0.15f), 2f)
    }
}

// =====================================================================
//  La biblioteca: lomos en estantes, rollos, cartas, expedientes, logs
// =====================================================================

/** Las entradas del diario como objetos del tema. [abrir] recibe la fecha. */
@Composable
fun EstanteDiario(entradas: List<Dia>, tema: TemaId = Estilo.tema, abrir: (String) -> Unit) {
    if (entradas.isEmpty()) return
    val m = materia(tema)
    when (soporte(tema)) {
        Soporte.LIBRO -> Lomos(entradas, tema, m, abrir)
        Soporte.ROLLO -> Rollos(entradas, tema, m, abrir)
        Soporte.SOBRE -> Cartas(entradas, tema, m, abrir)
        Soporte.CARPETA -> Expedientes(entradas, m, abrir)
        Soporte.PANTALLA -> Logs(entradas, tema, m, abrir)
    }
}

private fun madera(t: TemaId): Pair<Color, Color> = when (t) {
    TemaId.ORDEN -> Color(0xFF6E7883) to Color(0xFF2A2F35)
    TemaId.LABORATORIO -> Color(0xFFE3ECF3) to Color(0xFF97A8B9)
    TemaId.SEDA -> Color(0xFF8E2B1F) to Color(0xFF4A120C)
    TemaId.MUERTOS -> Color(0xFF6A1B9A) to Color(0xFF3A0F55)
    else -> Color(0xFF7A5230) to Color(0xFF3E2716)
}

@Composable
private fun Tabla(tema: TemaId) {
    val (a, b) = madera(tema)
    Box(Modifier.fillMaxWidth().height(11.dp).background(Brush.verticalGradient(listOf(a, b))))
    Box(Modifier.fillMaxWidth().height(6.dp).background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent))))
}

private val MESES3 = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")

/** Libros de pie en estantes: el grosor depende de cuánto escribiste y el color del ánimo del día. */
@Composable
private fun Lomos(entradas: List<Dia>, tema: TemaId, m: Materia, abrir: (String) -> Unit) {
    val oscuro = esOscuro()
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val ancho = maxWidth
        val lomos = entradas.map { d -> d to (24 + (Texto.palabras(d.nota) / 40).coerceAtMost(22)).dp }
        val filas = mutableListOf<MutableList<Pair<Dia, androidx.compose.ui.unit.Dp>>>(mutableListOf())
        var usado = 0.dp
        lomos.forEach { l -> if (usado + l.second + 3.dp > ancho && filas.last().isNotEmpty()) { filas.add(mutableListOf()); usado = 0.dp }; filas.last().add(l); usado += l.second + 3.dp }
        Column {
            filas.forEach { fila ->
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Bottom) {
                    fila.forEach { (d, w) -> Lomo(d, w, tema, m, oscuro) { abrir(d.fecha) } }
                }
                Tabla(tema)
            }
        }
    }
}

@Composable
private fun Lomo(d: Dia, ancho: androidx.compose.ui.unit.Dp, tema: TemaId, m: Materia, oscuro: Boolean, abrir: () -> Unit) {
    val h = (118 + (d.fecha.hashCode() and 0x7fffffff) % 34).dp
    val tono = Tonos.valencia(d.valencia?.toDouble(), oscuro)
    val cuerpo = if (tono != null) lerp(m.cuerpo, tono, 0.28f) else m.cuerpo
    val dia = d.fecha.takeLast(2).trimStart('0'); val mes = MESES3[(d.fecha.substring(5, 7).toIntOrNull() ?: 1) - 1]
    Box(Modifier.width(ancho).height(h).clickable(onClick = abrir), contentAlignment = Alignment.TopCenter) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val hh = size.height
            drawRoundRect(Brush.horizontalGradient(listOf(lerp(cuerpo, Color.Black, 0.35f), lerp(cuerpo, Color.White, 0.18f), cuerpo, lerp(cuerpo, Color.Black, 0.4f))), Offset.Zero, size, CornerRadius(3f))
            listOf(0.07f, 0.11f, 0.89f, 0.93f).forEach { f -> drawLine(m.acento.copy(alpha = 0.85f), Offset(2f, hh * f), Offset(w - 2f, hh * f), 2.2f) }
            drawRoundRect(m.hoja.copy(alpha = 0.92f), Offset(w * 0.14f, hh * 0.2f), Size(w * 0.72f, hh * 0.27f), CornerRadius(2f))
            if (tema == TemaId.URSS) { drawCircle(m.acento, w * 0.16f, Offset(w / 2, hh * 0.66f)) }
            if (tema == TemaId.MUERTOS) for (i in 0..2) drawCircle(listOf(m.acento, m.extra, Color(0xFFFFEB3B))[i], w * 0.08f, Offset(w / 2, hh * (0.6f + i * 0.08f)))
        }
        Column(Modifier.padding(top = h * 0.205f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dia, color = m.tinta, fontSize = 13.sp, fontFamily = Letra.garamond, lineHeight = 13.sp, maxLines = 1)
            Text(mes, color = m.tinta.copy(alpha = 0.75f), fontSize = 8.5.sp, fontFamily = Letra.texto, lineHeight = 9.sp, maxLines = 1)
        }
    }
}

/** Rollos tumbados en un estante, con su etiqueta colgando. */
@Composable
private fun Rollos(entradas: List<Dia>, tema: TemaId, m: Materia, abrir: (String) -> Unit) {
    Column {
        entradas.chunked(3).forEach { fila ->
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                fila.forEach { d ->
                    Column(Modifier.weight(1f).clickable { abrir(d.fecha) }, horizontalAlignment = Alignment.CenterHorizontally) {
                        Canvas(Modifier.fillMaxWidth().height(40.dp)) {
                            val w = size.width; val h = size.height
                            val largo = w * (0.72f + ((d.fecha.hashCode() and 0xff) / 255f) * 0.2f)
                            val x0 = (w - largo) / 2
                            drawRoundRect(Brush.verticalGradient(listOf(m.brillo, m.cuerpo, m.sombra)), Offset(x0, h * 0.2f), Size(largo, h * 0.6f), CornerRadius(h * 0.3f))
                            drawOval(m.sombra, Offset(x0 + largo - h * 0.18f, h * 0.2f), Size(h * 0.3f, h * 0.6f))
                            drawOval(m.hoja, Offset(x0 + largo - h * 0.14f, h * 0.27f), Size(h * 0.2f, h * 0.46f))
                            listOf(x0 - h * 0.1f, x0 + largo + h * 0.08f).forEach { x -> drawCircle(m.detalle, h * 0.13f, Offset(x, h * 0.5f)) }
                            if (tema == TemaId.SEDA) { drawRect(m.detalle, Offset(x0 + largo * 0.12f, h * 0.2f), Size(largo * 0.05f, h * 0.6f)); drawRect(m.extra, Offset(x0 + largo * 0.5f, h * 0.2f), Size(largo * 0.04f, h * 0.75f)) }
                            if (tema == TemaId.HELENO) drawRect(m.extra, Offset(x0 + largo * 0.3f, h * 0.2f), Size(largo * 0.4f, h * 0.6f), style = Stroke(1.5f))
                            drawLine(m.acento, Offset(w / 2, h * 0.8f), Offset(w / 2, h), 1.5f)
                        }
                        val dia = d.fecha.takeLast(2).trimStart('0'); val mes = MESES3[(d.fecha.substring(5, 7).toIntOrNull() ?: 1) - 1]
                        Text("$dia $mes", color = m.hoja, fontSize = 11.sp, fontFamily = Letra.garamond, maxLines = 1,
                            modifier = Modifier.background(m.extra, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 1.dp))
                        Text("${Texto.palabras(d.nota)} pal.", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 1.dp))
                    }
                }
                repeat(3 - fila.size) { Spacer(Modifier.weight(1f)) }
            }
            Tabla(tema)
        }
    }
}

/** Cartas selladas, de dos en dos. */
@Composable
private fun Cartas(entradas: List<Dia>, tema: TemaId, m: Materia, abrir: (String) -> Unit) {
    val e = MaterialTheme.colorScheme.copy(primary = m.brillo, secondary = m.detalle, tertiary = m.brillo, background = m.acento, onBackground = m.brillo)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 6.dp)) {
        entradas.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { d ->
                    Box(Modifier.weight(1f).height(104.dp).clickable { abrir(d.fecha) }) {
                        Canvas(Modifier.fillMaxSize()) { sobre(tema, m, e) }
                        Column(Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 8.dp, end = 10.dp)) {
                            Text(Fechas.corto(d.fecha), fontSize = 12.sp, color = m.textoCuerpo, fontFamily = Letra.garamond, maxLines = 1)
                            Text("${Texto.palabras(d.nota)} palabras", fontSize = 9.5.sp, color = m.textoCuerpo.copy(alpha = 0.7f), maxLines = 1)
                        }
                    }
                }
                if (fila.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

/** Pestañas de expedientes en un cajón de archivo. */
@Composable
private fun Expedientes(entradas: List<Dia>, m: Materia, abrir: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color(0xFF3A3D40), RoundedCornerShape(4.dp)).padding(8.dp)) {
        entradas.forEachIndexed { i, d ->
            BoxWithConstraints(Modifier.fillMaxWidth().padding(top = if (i == 0) 0.dp else 3.dp).clickable { abrir(d.fecha) }) {
                val pestana = maxWidth * (0.04f + (i % 4) * 0.2f)
                Canvas(Modifier.fillMaxWidth().height(52.dp)) {
                    val w = size.width; val h = size.height
                    val tx = w * (0.04f + (i % 4) * 0.2f)
                    drawRoundRect(m.sombra, Offset(tx, 0f), Size(w * 0.24f, h * 0.36f), CornerRadius(6f))
                    drawRoundRect(Brush.verticalGradient(listOf(m.brillo, m.cuerpo)), Offset(0f, h * 0.28f), Size(w, h * 0.72f), CornerRadius(3f))
                }
                Text("EXP. ${d.fecha}", fontSize = 9.sp, fontFamily = LetraExtra.oswald, color = m.detalle,
                    modifier = Modifier.padding(start = pestana + 8.dp, top = 3.dp))
                Row(Modifier.padding(start = 10.dp, top = 22.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(Fechas.corto(d.fecha).uppercase(), fontSize = 11.sp, fontFamily = Letra.codigo, color = m.detalle, modifier = Modifier.width(96.dp), maxLines = 1)
                    Text(d.nota.trim().replace(Regex("\\s+"), " "), fontSize = 11.sp, fontFamily = Letra.codigo, color = m.detalle.copy(alpha = 0.8f),
                        maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/** El directorio de logs (cyberpunk) o la lista de cintas de bitácora (refugio). */
@Composable
private fun Logs(entradas: List<Dia>, tema: TemaId, m: Materia, abrir: (String) -> Unit) {
    val refugio = tema == TemaId.REFUGIO
    val fuente = if (refugio) LetraExtra.vt323 else Letra.codigo
    val tam = if (refugio) 17.sp else 11.5.sp
    val fondo = if (refugio) m.hoja else m.cuerpo
    Column(Modifier.fillMaxWidth().background(fondo, RoundedCornerShape(if (refugio) 14.dp else 6.dp))
        .border(1.5.dp, m.acento.copy(alpha = 0.5f), RoundedCornerShape(if (refugio) 14.dp else 6.dp)).padding(12.dp)) {
        Text(if (refugio) "BITÁCORA PERSONAL — ${entradas.size} ENTRADAS" else "$ ls -lt ~/diario/  # ${entradas.size} archivos", fontFamily = fuente, fontSize = tam,
            color = if (refugio) m.acento else m.detalle, modifier = Modifier.padding(bottom = 6.dp))
        entradas.forEach { d ->
            val p = Texto.palabras(d.nota)
            Row(Modifier.fillMaxWidth().clickable { abrir(d.fecha) }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                if (refugio) {
                    Text("▸ ${Estilo.fecha(d.fecha).substringBefore(" ·")}", fontFamily = fuente, fontSize = tam, color = m.acento, modifier = Modifier.width(140.dp), maxLines = 1)
                    Text(d.nota.trim().replace(Regex("\\s+"), " ").uppercase(), fontFamily = fuente, fontSize = tam, color = m.tinta.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                } else {
                    Text("-rw-", fontFamily = fuente, fontSize = tam, color = m.tinta.copy(alpha = 0.45f))
                    Text("%5dw".format(p), fontFamily = fuente, fontSize = tam, color = m.extra, modifier = Modifier.padding(horizontal = 6.dp))
                    Text("${d.fecha}.log", fontFamily = fuente, fontSize = tam, color = m.acento)
                    Text("  " + d.nota.trim().replace(Regex("\\s+"), " "), fontFamily = fuente, fontSize = tam, color = m.tinta.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// =====================================================================
//  La página abierta: para leer y para escribir, a pantalla completa
// =====================================================================

enum class Rayas { NADA, RENGLONES, CUADRICULA, NUMEROS }

/** Cómo se ve una página abierta en cada tema. */
class Pagina(val fondo: Int?, val color: Color, val tinta: Color, val tenue: Color, val acento: Color, val detalle: Color,
             val texto: FontFamily, val tam: Float, val interlinea: Float, val oscura: Boolean, val rayas: Rayas = Rayas.NADA,
             val capitular: FontFamily? = null)

fun pagina(t: TemaId): Pagina = when (t) {
    TemaId.ROMANO -> Pagina(R.drawable.papel_papiro, c(0xFFE8D4A2), c(0xFF2A1E14), c(0xFF6B5536), c(0xFF8E2B1F), c(0xFF6B4423), Letra.garamond, 18.5f, 27f, false, capitular = Letra.inscripcion)
    TemaId.HELENO -> Pagina(R.drawable.papel_papiro, c(0xFFE8D4A2), c(0xFF1F1712), c(0xFF6B5536), c(0xFFA8471F), c(0xFF1A1410), Letra.garamond, 18.5f, 27f, false)
    TemaId.SEDA -> Pagina(R.drawable.papel_seda, c(0xFF1F3578), c(0xFFF2EAD3), c(0xFFB9B29F), c(0xFFE0B04F), c(0xFFE26A50), Letra.garamond, 18.5f, 27f, true, capitular = LetraExtra.almendra)
    TemaId.MEDIEVAL -> Pagina(R.drawable.papel_pergamino, c(0xFFEDDDB8), c(0xFF2B1D12), c(0xFF6E5639), c(0xFFB3261E), c(0xFF1F3A93), Letra.garamond, 18.5f, 27f, false, capitular = LetraExtra.gotica)
    TemaId.COLONOS -> Pagina(null, c(0xFFF3EBD7), c(0xFF1E1A16), c(0xFF6B6255), c(0xFF9E2B25), c(0xFF3E4B60), Letra.garamond, 18f, 28f, false, Rayas.RENGLONES, LetraExtra.fell)
    TemaId.ORDEN -> Pagina(null, c(0xFF1C2127), c(0xFFDDE2E7), c(0xFF9DA6B0), c(0xFFD6B25A), c(0xFFD0333B), Letra.texto, 16.5f, 25f, true, Rayas.NADA)
    TemaId.MUERTOS -> Pagina(null, c(0xFFFFF1E0), c(0xFF2A1033), c(0xFF6B4A6E), c(0xFFC2185B), c(0xFFE67E00), Letra.texto, 16.5f, 25f, false, Rayas.NADA, LetraExtra.rye)
    TemaId.URSS -> Pagina(null, c(0xFFEFE6D2), c(0xFF161414), c(0xFF6A625A), c(0xFFC1121F), c(0xFF161414), Letra.codigo, 15f, 26f, false, Rayas.RENGLONES)
    TemaId.LABORATORIO -> Pagina(null, c(0xFFF7FBFC), c(0xFF1B2733), c(0xFF5D6F80), c(0xFF0B8FA3), c(0xFFE0557A), LetraExtra.jost, 16.5f, 26f, false, Rayas.CUADRICULA)
    TemaId.NAPOLEON -> Pagina(null, c(0xFFF6F0E0), c(0xFF14213D), c(0xFF5A6278), c(0xFF1E3A8A), c(0xFFA3172E), LetraExtra.bodoni, 18f, 27f, false, Rayas.NADA, LetraExtra.bodoniTitulo)
    TemaId.NAVIDAD -> Pagina(null, c(0xFFFBF6EC), c(0xFF1E2A22), c(0xFF5E6B62), c(0xFFB71C1C), c(0xFF1B5E20), Letra.garamond, 18.5f, 27f, false, Rayas.NADA, LetraExtra.cinzelDeco)
    TemaId.DISTOPIA -> Pagina(null, c(0xFFECE8DC), c(0xFF141414), c(0xFF55585C), c(0xFFC8102E), c(0xFF141414), Letra.codigo, 15f, 24f, false, Rayas.RENGLONES)
    TemaId.CIBER -> Pagina(null, c(0xFF05080E), c(0xFFD6F3F7), c(0xFF4F6B78), c(0xFF00E5FF), c(0xFFFF2E88), Letra.codigo, 15f, 23f, true, Rayas.NUMEROS)
    TemaId.REFUGIO -> Pagina(null, c(0xFF031A0B), c(0xFF7CF098), c(0xFF2F8F4E), c(0xFF3DFF7F), c(0xFFFFB23F), LetraExtra.vt323, 22f, 26f, true, Rayas.NADA)
}

/** Fondo de la página: papel, papiro, seda, pantalla… En modo oscuro, el papel claro se atenúa un poco para no deslumbrar. */
@Composable
private fun FondoPagina(t: TemaId, p: Pagina, modifier: Modifier = Modifier) {
    val oscuroApp = esOscuro()
    Box(modifier.fillMaxSize().background(p.color)) {
        p.fondo?.let { Image(painterResource(it), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            when (t) {
                TemaId.MEDIEVAL -> listOf(26.dp.toPx(), 31.dp.toPx(), w - 26.dp.toPx()).forEach { x -> drawLine(p.acento.copy(alpha = 0.22f), Offset(x, 0f), Offset(x, h), 1.2f) }
                TemaId.COLONOS, TemaId.URSS -> drawLine(p.acento.copy(alpha = 0.45f), Offset(40.dp.toPx(), 0f), Offset(40.dp.toPx(), h), 1.5f)
                TemaId.NAPOLEON -> { var x = 0f; while (x < w) { drawLine(p.tenue.copy(alpha = 0.07f), Offset(x, 0f), Offset(x, h), 1f); x += 22.dp.toPx() } }
                TemaId.NAVIDAD -> {
                    drawRect(p.acento, Offset(8.dp.toPx(), 8.dp.toPx()), Size(w - 16.dp.toPx(), h - 16.dp.toPx()), style = Stroke(3f))
                    drawRect(p.detalle, Offset(13.dp.toPx(), 13.dp.toPx()), Size(w - 26.dp.toPx(), h - 26.dp.toPx()), style = Stroke(1.5f))
                }
                TemaId.ORDEN -> {
                    drawRect(p.acento.copy(alpha = 0.7f), Offset(6.dp.toPx(), 6.dp.toPx()), Size(w - 12.dp.toPx(), h - 12.dp.toPx()), style = Stroke(2f))
                    listOf(Offset(16f, 16f), Offset(w - 16f, 16f), Offset(16f, h - 16f), Offset(w - 16f, h - 16f)).forEach { drawCircle(p.acento, 9f, it) }
                }
                TemaId.REFUGIO -> {
                    drawRect(Brush.radialGradient(listOf(p.acento.copy(alpha = 0.1f), Color.Transparent), center = Offset(w / 2, h / 2), radius = h * 0.7f))
                    var y = 0f; while (y < h) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(0f, y), Offset(w, y), 1.5f); y += 4.dp.toPx() }
                }
                TemaId.CIBER -> { var y = 0f; while (y < h) { drawLine(p.acento.copy(alpha = 0.03f), Offset(0f, y), Offset(w, y), 1f); y += 3.dp.toPx() } }
                TemaId.MUERTOS -> {
                    val colores = listOf(p.acento, p.detalle, Color(0xFF26C6DA), Color(0xFF6A1B9A), Color(0xFFFDD835))
                    val ancho = 36.dp.toPx(); var x = 0f; var k = 0
                    while (x < w) {
                        val q = Path().apply { moveTo(x + 2f, 0f); lineTo(x + ancho - 2f, 0f); lineTo(x + ancho - 2f, 34.dp.toPx()); for (i in 4 downTo 0) lineTo(x + 2f + (ancho - 4f) * i / 4, if (i % 2 == 0) 34.dp.toPx() else 40.dp.toPx()); close() }
                        drawPath(q, colores[k % colores.size].copy(alpha = 0.85f)); x += ancho; k++
                    }
                }
                else -> {}
            }
        }
        if (oscuroApp && !p.oscura) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.22f)))
    }
}

/** La capitular: primera letra grande y de color, como en un libro iluminado. */
private class Capitular(val familia: FontFamily, val color: Color) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = TransformedText(conCapitular(text, familia, color), OffsetMapping.Identity)
}

private fun conCapitular(text: AnnotatedString, familia: FontFamily, color: Color): AnnotatedString = buildAnnotatedString {
    append(text)
    val i = text.text.indexOfFirst { it.isLetterOrDigit() }
    if (i >= 0) addStyle(SpanStyle(fontFamily = familia, color = color, fontSize = 2.1.em), i, i + 1)
}

/** Dibuja renglones, cuadrícula o números de línea detrás del texto, alineados con sus líneas. */
@Composable
private fun Modifier.rayado(p: Pagina, layout: TextLayoutResult?): Modifier {
    if (p.rayas == Rayas.NADA) return this
    val medidor = rememberTextMeasurer()
    val estiloNum = TextStyle(fontFamily = Letra.codigo, fontSize = 11.sp, color = p.tenue)
    return this.drawBehind {
        val paso = layout?.takeIf { it.lineCount > 1 }?.let { (it.getLineBottom(it.lineCount - 1) - it.getLineTop(0)) / it.lineCount } ?: (p.interlinea.sp.toPx())
        val lineas = layout?.lineCount ?: 0
        fun fondoDe(i: Int) = if (layout != null && i < lineas) layout.getLineBottom(i) else (if (lineas > 0) layout!!.getLineBottom(lineas - 1) else 0f) + paso * (i - lineas + 1)
        val total = (size.height / paso).toInt() + 1
        when (p.rayas) {
            Rayas.RENGLONES -> for (i in 0 until total) { val y = fondoDe(i) - paso * 0.12f; if (y < size.height) drawLine(p.tenue.copy(alpha = 0.3f), Offset(-12.dp.toPx(), y), Offset(size.width + 12.dp.toPx(), y), 1f) }
            Rayas.CUADRICULA -> {
                val g = paso / 2; var x = -g * 2
                while (x < size.width + g * 2) { drawLine(p.acento.copy(alpha = 0.12f), Offset(x, -g), Offset(x, size.height), 1f); x += g }
                var y = 0f; while (y < size.height) { drawLine(p.acento.copy(alpha = 0.12f), Offset(-g * 2, y), Offset(size.width + g * 2, y), 1f); y += g }
            }
            Rayas.NUMEROS -> for (i in 0 until minOf(total, 999)) {
                val top = if (layout != null && i < lineas) layout.getLineTop(i) else fondoDe(i) - paso
                if (top > size.height) break
                drawText(medidor, "%3d".format(i + 1), Offset(-40.dp.toPx(), top + paso * 0.18f), estiloNum)
            }
            else -> {}
        }
    }
}

/** Encabezado de la página: emblema, fecha del tema y una banda. */
@Composable
private fun EncabezadoPagina(t: TemaId, p: Pagina, fecha: String) {
    val ty = MaterialTheme.typography
    Column(Modifier.fillMaxWidth().padding(top = if (t == TemaId.MUERTOS) 34.dp else 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        when (t) {
            TemaId.CIBER -> Text("~/vestigium/diario/$fecha.log", fontFamily = Letra.codigo, fontSize = 12.sp, color = p.tenue, modifier = Modifier.fillMaxWidth())
            TemaId.DISTOPIA -> Row(Modifier.fillMaxWidth().border(1.5.dp, p.tinta)) {
                Text("EXPEDIENTE Nº %04d".format((fecha.hashCode() and 0x7fffffff) % 10000), fontFamily = LetraExtra.oswald, fontSize = 13.sp, color = p.tinta, modifier = Modifier.weight(1f).padding(6.dp))
                Text("DECLARACIÓN DIARIA", fontFamily = LetraExtra.oswald, fontSize = 13.sp, color = p.acento, modifier = Modifier.padding(6.dp))
            }
            TemaId.LABORATORIO -> Text("PROTOCOLO VM-%03d · CUADERNO DE LABORATORIO".format((fecha.hashCode() and 0x7fffffff) % 900 + 100), fontFamily = LetraExtra.jost, fontSize = 11.sp, color = p.acento)
            TemaId.URSS -> Text("ОТЧЁТ · INFORME DE JORNADA", fontFamily = LetraExtra.russo, fontSize = 12.sp, color = p.acento, modifier = Modifier.border(2.dp, p.acento).padding(horizontal = 8.dp, vertical = 2.dp))
            TemaId.REFUGIO -> Text("BITÁCORA PERSONAL · TERMINAL 03", fontFamily = LetraExtra.vt323, fontSize = 18.sp, color = p.acento)
            else -> Emblema(t, 44.dp, MaterialTheme.colorScheme, animado = false)
        }
        Text(Estilo.fecha(fecha), style = ty.headlineSmall, color = if (p.oscura) p.acento else p.tinta, textAlign = TextAlign.Center,
            fontFamily = if (t == TemaId.REFUGIO) LetraExtra.vt323 else ty.headlineSmall.fontFamily, modifier = Modifier.padding(top = 8.dp))
        Text(Fechas.titulo(fecha).lowercase() + " · " + Fechas.relativo(fecha), style = ty.labelMedium, color = p.tenue, textAlign = TextAlign.Center)
        Meandro(Modifier.padding(top = 10.dp, bottom = 12.dp))
    }
}

/** Barra superior de lectura o escritura. */
@Composable
private fun BarraPagina(p: Pagina, volver: () -> Unit, centro: String, derecha: @Composable () -> Unit) {
    val sentir = rememberSentir()
    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(Estilo.atras, style = MaterialTheme.typography.labelLarge, color = p.acento,
            modifier = Modifier.clickable { sentir(Sentidos.Evento.TOQUE); volver() }.padding(horizontal = 8.dp, vertical = 10.dp))
        Text(centro, style = MaterialTheme.typography.labelMedium, color = p.tenue, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1)
        derecha()
    }
}

/** Una entrada abierta a pantalla completa con el diseño del tema. */
@Composable
fun LecturaTema(fecha: String, d: Dia?, volver: () -> Unit, anterior: (() -> Unit)?, siguiente: (() -> Unit)?, acciones: List<Pair<String, () -> Unit>>,
                tema: TemaId = Estilo.tema) {
    val p = pagina(tema)
    var menu by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
        Box(Modifier.fillMaxSize()) {
            FondoPagina(tema, p)
            Column(Modifier.fillMaxSize()) {
                BarraPagina(p, volver, d?.let { "${Texto.palabras(it.nota)} palabras" } ?: "") {
                    Text("‹", fontSize = 24.sp, color = if (anterior != null) p.acento else p.tenue.copy(alpha = 0.3f),
                        modifier = Modifier.clickable(enabled = anterior != null) { anterior?.invoke() }.padding(horizontal = 10.dp))
                    Text("›", fontSize = 24.sp, color = if (siguiente != null) p.acento else p.tenue.copy(alpha = 0.3f),
                        modifier = Modifier.clickable(enabled = siguiente != null) { siguiente?.invoke() }.padding(horizontal = 10.dp))
                    Box {
                        Text("⋯", fontSize = 22.sp, color = p.acento, modifier = Modifier.clickable { menu = true }.padding(horizontal = 10.dp))
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }, containerColor = p.color) {
                            acciones.forEach { (t, f) -> DropdownMenuItem(text = { Text(t, color = p.tinta) }, onClick = { menu = false; f() }) }
                        }
                    }
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = if (p.rayas == Rayas.NUMEROS) 52.dp else 30.dp, end = 26.dp, bottom = 40.dp)) {
                    EncabezadoPagina(tema, p, fecha)
                    if (d == null || d.nota.isBlank()) {
                        Text("No hay nada escrito este día.", color = p.tenue, fontFamily = p.texto, fontSize = p.tam.sp)
                    } else {
                        var layout by remember(d.nota) { mutableStateOf<TextLayoutResult?>(null) }
                        val texto = AnnotatedString(d.nota.trim()).let { if (p.capitular != null) conCapitular(it, p.capitular, p.acento) else it }
                        SelectionContainer {
                            Text(texto, color = p.tinta, fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, onTextLayout = { layout = it },
                                modifier = Modifier.fillMaxWidth().rayado(p, layout))
                        }
                        if (d.mejor_momento.isNotBlank() || d.peor_momento.isNotBlank()) Column(Modifier.padding(top = 22.dp)) {
                            if (d.mejor_momento.isNotBlank()) Text("Lo mejor — " + d.mejor_momento, color = p.tinta, fontFamily = p.texto, fontSize = (p.tam - 2).sp, lineHeight = (p.interlinea - 3).sp)
                            if (d.peor_momento.isNotBlank()) Text("Lo peor — " + d.peor_momento, color = p.tinta, fontFamily = p.texto, fontSize = (p.tam - 2).sp, lineHeight = (p.interlinea - 3).sp, modifier = Modifier.padding(top = 6.dp))
                        }
                        if (d.sueno_texto.isNotBlank()) Text("☾ " + d.sueno_texto.trim(), color = p.tenue, fontFamily = p.texto, fontSize = (p.tam - 2).sp, lineHeight = (p.interlinea - 3).sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, modifier = Modifier.padding(top = 18.dp))
                        LibroDiario.meta(d).takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.labelMedium, color = p.tenue, modifier = Modifier.padding(top = 22.dp)) }
                    }
                    Text(colofon(tema), color = p.acento, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 26.dp))
                }
            }
        }
    }
}

/** Escribir el diario a pantalla completa, sobre el papel del tema. Se guarda solo; el botón guarda y vuelve. */
@Composable
fun EscritorTema(fecha: String, valor: String, onCambio: (String) -> Unit, guardar: () -> Unit, tema: TemaId = Estilo.tema, dictado: Boolean = true) {
    val p = pagina(tema)
    val sentir = rememberSentir()
    var campo by remember { mutableStateOf(TextFieldValue(valor, TextRange(valor.length))) }
    if (campo.text != valor) campo = TextFieldValue(valor, TextRange(valor.length))
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val etiquetas = remember { Etiquetas.frecuentes() }
    val antes = campo.text.substring(0, campo.selection.start.coerceIn(0, campo.text.length))
    val token = Regex("[@#][\\p{L}\\d_]*$").find(antes)?.value
    val sugerencias = if (token == null) emptyList() else {
        val pre = token.first(); val q = token.drop(1).lowercase()
        (if (pre == '@') etiquetas.first else etiquetas.second).filter { it.startsWith(q) && it != q }.take(6).map { "$pre$it" }
    }
    MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
        Box(Modifier.fillMaxSize()) {
            FondoPagina(tema, p)
            Column(Modifier.fillMaxSize().systemBarsPadding().imePadding()) {
                BarraPagina(p, guardar, "${Texto.palabras(campo.text)} palabras") {
                    Text(textoGuardar(tema), style = MaterialTheme.typography.labelLarge, color = if (p.oscura) p.color else Color.White,
                        modifier = Modifier.padding(end = 6.dp).background(p.acento, MaterialTheme.shapes.small).clickable { sentir(Sentidos.Evento.GUARDAR); guardar() }
                            .padding(horizontal = 14.dp, vertical = 8.dp))
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = if (p.rayas == Rayas.NUMEROS) 52.dp else 30.dp, end = 26.dp, bottom = 24.dp)) {
                    EncabezadoPagina(tema, p, fecha)
                    val estilo = TextStyle(fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, color = p.tinta)
                    BasicTextField(
                        value = campo, onValueChange = { campo = it; if (it.text != valor) onCambio(it.text) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 420.dp).rayado(p, layout),
                        textStyle = estilo, cursorBrush = SolidColor(p.acento), onTextLayout = { layout = it },
                        visualTransformation = p.capitular?.let { Capitular(it, p.acento) } ?: VisualTransformation.None,
                        decorationBox = { interior ->
                            Box {
                                if (campo.text.isEmpty()) Text(if (tema == TemaId.CIBER) "// ¿cómo fue el día?" else "¿Cómo fue el día?", style = estilo.copy(color = p.tenue.copy(alpha = 0.7f)))
                                interior()
                            }
                        },
                    )
                }
                if (sugerencias.isNotEmpty() || dictado) FlowRow(Modifier.fillMaxWidth().background(p.color.copy(alpha = 0.92f)).padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (dictado) BotonDictado { t -> val nuevo = (campo.text + " " + t).trim(); campo = TextFieldValue(nuevo, TextRange(nuevo.length)); onCambio(nuevo) }
                    sugerencias.forEach { s ->
                        Ficha(s, false) {
                            val ini = antes.length - token!!.length
                            val nuevo = campo.text.substring(0, ini) + s + " " + campo.text.substring(antes.length)
                            campo = TextFieldValue(nuevo, TextRange(ini + s.length + 1)); onCambio(nuevo)
                        }
                    }
                }
            }
        }
    }
}

/** El escritor en una ventana que tapa toda la pantalla (también la barra de abajo). */
@Composable
fun DialogoEscritor(fecha: String, valor: String, onCambio: (String) -> Unit, cerrar: () -> Unit) {
    Dialog(onDismissRequest = cerrar, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        EscritorTema(fecha, valor, onCambio, cerrar)
    }
}

/** Escribir el diario de un día desde fuera de la hoja de la noche: carga el día, guarda mientras escribes y al cerrar. */
@Composable
fun EscribirDia(fecha: String, cerrar: () -> Unit) {
    var d by remember(fecha) { mutableStateOf(Repo.dia(fecha)) }
    var sucio by remember(fecha) { mutableStateOf(false) }
    LaunchedEffect(d, sucio) { if (sucio) { delay(800); Repo.guardarDia(d); sucio = false } }
    DialogoEscritor(fecha, d.nota, { d = d.copy(nota = it); sucio = true }) { if (sucio) { Repo.guardarDia(d); sucio = false }; cerrar() }
}
