@file:OptIn(ExperimentalLayoutApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
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
    TemaId.ROMANO, TemaId.HELENO, TemaId.SEDA, TemaId.MONGOL -> Soporte.ROLLO
    TemaId.MEDIEVAL, TemaId.COLONOS, TemaId.ORDEN, TemaId.BOTANICO, TemaId.URSS, TemaId.LABORATORIO, TemaId.GOTICO, TemaId.ANDALUS, TemaId.RENACIMIENTO, TemaId.REVOLUCION, TemaId.ANTIGUAS -> Soporte.LIBRO
    TemaId.NAPOLEON, TemaId.NAVIDAD -> Soporte.SOBRE
    TemaId.DISTOPIA -> Soporte.CARPETA
    TemaId.CIBER, TemaId.REFUGIO, TemaId.ESPACIO -> Soporte.PANTALLA
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
    TemaId.BOTANICO -> Materia(c(0xFF6B4A2E), c(0xFF432C19), c(0xFF8C6844), c(0xFFC9A54A), c(0xFF2E5E5A), c(0xFFF2EAD3), c(0xFF2A2318), extra = c(0xFF8C4A22))
    TemaId.GOTICO -> Materia(c(0xFF1F2F7A), c(0xFF0E1640), c(0xFF3B4FA8), c(0xFFE2B34A), c(0xFFB3122E), c(0xFFF3EBD6), c(0xFF1A1630), extra = c(0xFFB3122E))
    TemaId.ANDALUS -> Materia(c(0xFF7A3B1E), c(0xFF45200F), c(0xFF9C5530), c(0xFFD9B25B), c(0xFF1E6B5C), c(0xFFF3E7CC), c(0xFF2A1A12), extra = c(0xFF1E6B5C))
    TemaId.RENACIMIENTO -> Materia(c(0xFF9A7A50), c(0xFF5E462B), c(0xFFB89466), c(0xFF8C3B1E), c(0xFF3A2614), c(0xFFF0E4C6), c(0xFF3A2614), extra = c(0xFF8C3B1E))
    TemaId.MONGOL -> Materia(c(0xFF1F5FA8), c(0xFF0F3566), c(0xFF4A86CC), c(0xFFC48A1E), c(0xFFC2412D), c(0xFFF4EEDC), c(0xFF18202E), extra = c(0xFFC2412D))
    TemaId.ESPACIO -> Materia(c(0xFF0A1022), c(0xFF040814), c(0xFF17213D), c(0xFF8AB4FF), c(0xFFFFB86B), c(0xFF040814), c(0xFFE8ECF8), extra = c(0xFFC59BFF))
    TemaId.URSS -> Materia(c(0xFFB71C1C), c(0xFF6E0F0F), c(0xFFD84343), c(0xFFE8B923), c(0xFF161414), c(0xFFEFE6D2), c(0xFF161414))
    TemaId.LABORATORIO -> Materia(c(0xFF20323F), c(0xFF101B23), c(0xFF3A5363), c(0xFF3CD2E5), c(0xFFFFFFFF), c(0xFFF7FBFC), c(0xFF1B2733))
    TemaId.NAPOLEON -> Materia(c(0xFFF1E6CC), c(0xFFC9B48A), c(0xFFFBF5E6), c(0xFFA3172E), c(0xFF1E3A8A), c(0xFFF6F0E0), c(0xFF14213D), extra = c(0xFFD9B25B))
    TemaId.NAVIDAD -> Materia(c(0xFFB71C1C), c(0xFF7A1010), c(0xFFD84343), c(0xFFC9A227), c(0xFF1B5E20), c(0xFFFBF6EC), c(0xFF1E2A22), extra = c(0xFFFF5252), textoCuerpo = c(0xFFFBF6EC))
    TemaId.DISTOPIA -> Materia(c(0xFFD8C08A), c(0xFFA88F58), c(0xFFE8D4A4), c(0xFFC8102E), c(0xFF141414), c(0xFFF2EFE6), c(0xFF141414))
    TemaId.CIBER -> Materia(c(0xFF0B121C), c(0xFF05080E), c(0xFF15202E), c(0xFF00E5FF), c(0xFFFF2E88), c(0xFF05080E), c(0xFFD6F3F7), extra = c(0xFFF2C94C))
    TemaId.REFUGIO -> Materia(c(0xFFC9C2A6), c(0xFF8E886F), c(0xFFE2DCC4), c(0xFF3DFF7F), c(0xFF2F8F4E), c(0xFF031A0B), c(0xFF7CF098))
    // Cuaderno de quejas: tapa de cartón azul, cinta tricolor y escarapela.
    TemaId.REVOLUCION -> Materia(c(0xFF2F4A7A), c(0xFF1A2A4A), c(0xFF4A6699), c(0xFFB22234), c(0xFFF0EADB), c(0xFFF2ECDD), c(0xFF1C1A17), extra = c(0xFFB22234))
    // Libro de crónicas: cuero gastado, esquinas de hierro, oro viejo y una gema verde.
    TemaId.ANTIGUAS -> Materia(c(0xFF5A3A22), c(0xFF33210F), c(0xFF7A5232), c(0xFFC9A04A), c(0xFF4A4E54), c(0xFFEDE2C6), c(0xFF1E1D1A), extra = c(0xFF2E7A5A))
}

// =====================================================================
//  De noche: los sueños con el mismo objeto del tema, teñido de medianoche
// =====================================================================

/** Qué se escribe en la página: el diario del día, un sueño o una carta. */
enum class Escrito { DIARIO, SUENO, CARTA }

internal val NOCHE = Color(0xFF111833)
internal val NOCHE_ALTA = Color(0xFF261C4C)
internal val LUNA = Color(0xFFEDE6C8)

/** La materia del tema de noche: el cuero, el papiro o la pantalla en azul de medianoche, y el oro vuelto plata de luna. */
fun materiaNoche(t: TemaId): Materia {
    val m = materia(t)
    return when (t) {
        // El fósforo verde se vuelve ámbar, como una pantalla en reposo.
        TemaId.REFUGIO -> Materia(c(0xFF6E6A58), c(0xFF3E3B30), c(0xFF8E886F), c(0xFFFFB23F), c(0xFFB3741E), c(0xFF1A1003), c(0xFFFFC870))
        TemaId.CIBER -> Materia(c(0xFF120B1F), c(0xFF07040D), c(0xFF22163A), c(0xFFFF2E88), c(0xFF9B5CFF), c(0xFF07040D), c(0xFFF0DAF7), extra = c(0xFF00E5FF))
        TemaId.ESPACIO -> Materia(c(0xFF120A2A), c(0xFF06040F), c(0xFF241645), c(0xFFC59BFF), c(0xFF8AB4FF), c(0xFF06040F), c(0xFFECE6FA), extra = c(0xFFFF8AD8))
        else -> {
            fun n(x: Color, k: Float) = lerp(x, NOCHE, k)
            Materia(cuerpo = n(m.cuerpo, 0.68f), sombra = n(m.sombra, 0.76f), brillo = n(m.brillo, 0.6f),
                acento = lerp(m.acento, LUNA, 0.3f), detalle = lerp(m.detalle, Color(0xFFAFC0EE), 0.45f),
                hoja = n(m.hoja, 0.74f), tinta = Color(0xFFE8E6F2), extra = lerp(m.extra, Color(0xFF8FA6F0), 0.35f), textoCuerpo = Color(0xFFE8E6F2))
        }
    }
}

/** La página de un sueño: el papel del tema bajo un cielo de noche. */
fun paginaNoche(t: TemaId): Pagina {
    val p = pagina(t)
    return when (t) {
        TemaId.REFUGIO -> Pagina(null, c(0xFF1A1003), c(0xFFFFC870), c(0xFFB3741E), c(0xFFFFB23F), c(0xFF7CF098), p.texto, p.tam, p.interlinea, true, p.rayas)
        TemaId.CIBER -> Pagina(null, c(0xFF0A0614), c(0xFFF0DAF7), c(0xFF7A5B8C), c(0xFFFF2E88), c(0xFF00E5FF), p.texto, p.tam, p.interlinea, true, p.rayas)
        TemaId.ESPACIO -> Pagina(null, c(0xFF07041A), c(0xFFECE6FA), c(0xFF8A7BB0), c(0xFFC59BFF), c(0xFFFF8AD8), p.texto, p.tam, p.interlinea, true, p.rayas)
        else -> Pagina(p.fondo, lerp(p.color, NOCHE, 0.84f), c(0xFFE6E4F0), c(0xFFA2AACB), lerp(p.acento, LUNA, 0.45f), lerp(p.detalle, Color(0xFFAFC0EE), 0.5f),
            p.texto, p.tam, p.interlinea, true, p.rayas, p.capitular)
    }
}

/** El rótulo de la página de un sueño en cada tema. */
fun rotuloSueno(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "SOMNIVM"; TemaId.CIBER -> "~/suenos/"; TemaId.HELENO -> "ΟΝΕΙΡΟΣ · sueño"; TemaId.MEDIEVAL -> "Visión de la noche"
    TemaId.DISTOPIA -> "INFORME DE ACTIVIDAD ONÍRICA"; TemaId.REFUGIO -> "REGISTRO ONÍRICO · TERMINAL 03"; TemaId.ORDEN -> "PARTE NOCTURNO"
    TemaId.LABORATORIO -> "REGISTRO DE SUEÑO"; TemaId.COLONOS -> "Lo soñado"; TemaId.NAPOLEON -> "SONGE · sueño"; TemaId.BOTANICO -> "Flora nocturna"
    TemaId.NAVIDAD -> "Sueños junto al fuego"; TemaId.SEDA -> "梦 · sueño"; TemaId.URSS -> "СОН · SUEÑO"; TemaId.GOTICO -> "Visio nocturna"
    TemaId.ESPACIO -> "TRANSMISIÓN NOCTURNA"; TemaId.ANDALUS -> "حلم · sueño"; TemaId.MONGOL -> "ЗҮҮД · sueño"; TemaId.RENACIMIENTO -> "Sogno"
    TemaId.REVOLUCION -> "RÊVE · SUEÑO"
    TemaId.ANTIGUAS -> "VISIONES DE LA NOCHE"
}

/** Lo que cierra la página de un sueño: el despertar, en cada tema. */
fun colofonSueno(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "· EVIGILAVI ·"; TemaId.CIBER -> "-- wake --"; TemaId.HELENO -> "ἐξηγέρθην · desperté"; TemaId.MEDIEVAL -> "Y en esto desperté."
    TemaId.DISTOPIA -> "FIN DEL INFORME NOCTURNO"; TemaId.REFUGIO -> "FIN DEL REGISTRO"; TemaId.ORDEN -> "TOQUE DE DIANA"; TemaId.LABORATORIO -> "fin del registro onírico"
    TemaId.COLONOS -> "Y amaneció."; TemaId.NAPOLEON -> "Réveil."; TemaId.BOTANICO -> "Se cerraron las flores de la noche."; TemaId.NAVIDAD -> "Y llegó la mañana."
    TemaId.SEDA -> "Despertar en la caravana."; TemaId.URSS -> "ПОДЪЁМ · DE PIE"; TemaId.GOTICO -> "Et evigilavi."; TemaId.ESPACIO -> "FIN DE LA TRANSMISIÓN NOCTURNA"
    TemaId.ANDALUS -> "واستيقظت · y desperté"; TemaId.MONGOL -> "Сэрлээ · desperté"; TemaId.RENACIMIENTO -> "E mi destai."
    TemaId.REVOLUCION -> "DEBOUT · EN PIE"
    TemaId.ANTIGUAS -> "Y la compañía despertó."
}

/** Media luna: un círculo menos otro corrido hacia un lado. */
internal fun DrawScope.mediaLuna(c: Offset, r: Float, color: Color) {
    val a = Path().apply { addOval(Rect(c, r)) }
    val b = Path().apply { addOval(Rect(Offset(c.x + r * 0.48f, c.y - r * 0.22f), r * 0.88f)) }
    drawPath(Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, a, b), color)
}

/** Estrellas fijas: la misma semilla da siempre el mismo cielo. Las más grandes llevan destello en cruz. */
internal fun DrawScope.estrellas(semilla: Int, n: Int, color: Color, area: Rect = size.toRect(), max: Float = 1.6f) {
    val az = java.util.Random(semilla.toLong())
    repeat(n) {
        val x = area.left + az.nextFloat() * area.width; val y = area.top + az.nextFloat() * area.height
        val r = 0.45f + az.nextFloat() * max
        drawCircle(color.copy(alpha = 0.25f + az.nextFloat() * 0.6f), r, Offset(x, y))
        if (r > max * 0.92f) {
            drawLine(color.copy(alpha = 0.45f), Offset(x - r * 2.8f, y), Offset(x + r * 2.8f, y), 0.8f)
            drawLine(color.copy(alpha = 0.45f), Offset(x, y - r * 2.8f), Offset(x, y + r * 2.8f), 0.8f)
        }
    }
}

/** Un cielo de noche con el tono del tema, para los estantes de los sueños. */
@Composable
fun CieloNoche(modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, content: @Composable () -> Unit) {
    val m = materiaNoche(tema)
    val alto = lerp(NOCHE_ALTA, m.acento, 0.14f)
    val forma = RoundedCornerShape(10.dp)
    val e = MaterialTheme.colorScheme.copy(onSurfaceVariant = Color(0xFFB8BEDA), onSurface = Color(0xFFE8E6F2), onBackground = Color(0xFFE8E6F2), primary = m.acento)
    Box(modifier.fillMaxWidth().clip(forma).background(Brush.verticalGradient(listOf(NOCHE, alto))).border(1.dp, m.acento.copy(alpha = 0.35f), forma)) {
        Canvas(Modifier.matchParentSize()) {
            estrellas(tema.ordinal * 31 + 5, (size.width * size.height / 9000f).toInt().coerceIn(20, 140), LUNA)
            mediaLuna(Offset(size.width - 24.dp.toPx(), 22.dp.toPx()), 10.dp.toPx(), LUNA.copy(alpha = 0.9f))
        }
        MaterialTheme(colorScheme = e, typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
            Box(Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) { content() }
        }
    }
}

/** Lo que dice el botón de guardar en cada tema. */
fun textoGuardar(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "SIGNARE"; TemaId.CIBER -> "git commit"; TemaId.HELENO -> "guardar"; TemaId.MEDIEVAL -> "asentar"
    TemaId.DISTOPIA -> "ARCHIVAR"; TemaId.REFUGIO -> "GUARDAR"; TemaId.ORDEN -> "REGISTRAR"; TemaId.LABORATORIO -> "guardar registro"
    TemaId.COLONOS -> "asentar"; TemaId.NAPOLEON -> "SELLAR"; TemaId.BOTANICO -> "guardar"; TemaId.NAVIDAD -> "guardar carta"
    TemaId.SEDA -> "sellar el rollo"; TemaId.URSS -> "ENTREGAR"
    TemaId.GOTICO -> "sellar el folio"; TemaId.ESPACIO -> "TRANSMITIR"; TemaId.ANDALUS -> "guardar · حفظ"; TemaId.MONGOL -> "ТАМГА · sellar"
    TemaId.RENACIMIENTO -> "annotare"; TemaId.REVOLUCION -> "FIRMAR"; TemaId.ANTIGUAS -> "ASENTAR EN LA CRÓNICA"
}

/** Lo que cierra cada página al leerla. */
fun colofon(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "· FINIS ·"; TemaId.CIBER -> "-- EOF --"; TemaId.HELENO -> "ΤΕΛΟΣ"; TemaId.MEDIEVAL -> "Aquí se acaba el folio."
    TemaId.DISTOPIA -> "FIN DE LA DECLARACIÓN"; TemaId.REFUGIO -> "FIN DE LA ENTRADA"; TemaId.ORDEN -> "FIN DEL PARTE"; TemaId.LABORATORIO -> "fin del registro"
    TemaId.COLONOS -> "Asentado."; TemaId.NAPOLEON -> "Dado en el cuartel general."; TemaId.BOTANICO -> "Ex herbario."
    TemaId.NAVIDAD -> "Con cariño, junto al fuego."; TemaId.SEDA -> "Fin del rollo."; TemaId.URSS -> "INFORME ENTREGADO"
    TemaId.GOTICO -> "Explicit. Deo gratias."; TemaId.ESPACIO -> "FIN DE TRANSMISIÓN"; TemaId.ANDALUS -> "تمّ · Se acabó."
    TemaId.MONGOL -> "Ингэж бичив · Así quedó escrito."; TemaId.RENACIMIENTO -> "Finito."
    TemaId.REVOLUCION -> "Lu et approuvé."
    TemaId.ANTIGUAS -> "Así quedó escrito en la crónica."
}

private fun pistaSueno(s: Soporte) = when (s) {
    Soporte.CARPETA -> "TOCA PARA DECLARAR LO SOÑADO"
    Soporte.PANTALLA -> "> toca para registrar el sueño_"
    else -> "toca para contar lo que soñaste"
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
internal fun esquemaPagina(p: Pagina): ColorScheme {
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
fun ObjetoDiario(fecha: String, texto: String, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, noche: Boolean = false, onClick: () -> Unit) {
    val m = if (noche) materiaNoche(tema) else materia(tema); val s = soporte(tema)
    val sentir = rememberSentir()
    val esquemaEmblema = MaterialTheme.colorScheme.copy(primary = m.brillo, secondary = m.detalle, tertiary = m.brillo, background = m.acento, onBackground = m.brillo)
    val palabras = Texto.palabras(texto)
    val vista = texto.trim().replace(Regex("\\s+"), " ").take(140)
    val alto = when (s) { Soporte.LIBRO -> 176.dp; Soporte.SOBRE -> 176.dp; else -> 168.dp }
    // Al tocarlo, el objeto se abre un instante (la tapa gira, el rollo se acerca) antes de pasar a pantalla completa.
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val abre = remember { androidx.compose.animation.core.Animatable(0f) }
    val animar = Repo.config.animaciones
    BoxWithConstraints(modifier.fillMaxWidth().height(alto).graphicsLayer {
        val k = abre.value
        scaleX = 1f + 0.05f * k; scaleY = 1f + 0.05f * k
        if (s == Soporte.LIBRO || s == Soporte.SOBRE) { rotationY = -22f * k; transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f); cameraDistance = 14f * density }
        alpha = 1f - 0.25f * k
    }.clickable {
        sentir(Sentidos.Evento.ABRIR)
        if (animar) scope.launch { abre.animateTo(1f, androidx.compose.animation.core.tween(230)); onClick(); abre.snapTo(0f) } else onClick()
    }) {
        Canvas(Modifier.fillMaxSize()) {
            when (s) {
                Soporte.ROLLO -> rolloAbierto(tema, m)
                Soporte.LIBRO -> libroCerrado(tema, m)
                Soporte.SOBRE -> sobre(tema, m, esquemaEmblema, luna = noche)
                Soporte.CARPETA -> carpeta(m)
                Soporte.PANTALLA -> pantalla(tema, m)
            }
            if (noche) {
                // Un poco de cielo sobre el objeto: estrellas y la luna en una esquina.
                estrellas(fecha.hashCode(), 26, LUNA, Rect(0f, 0f, size.width, size.height * 0.5f), 1.3f)
                mediaLuna(Offset(size.width * 0.93f, size.height * 0.16f), size.height * 0.07f, LUNA)
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
        // De noche, la fecha va en luz de luna (salvo en las pantallas, que ya brillan).
        val acento = if (noche && s != Soporte.PANTALLA) LUNA else when (s) { Soporte.PANTALLA -> m.acento; Soporte.SOBRE -> if (tema == TemaId.NAVIDAD) m.acento else m.detalle; Soporte.CARPETA -> m.acento; else -> m.extra }
        val ty = MaterialTheme.typography
        val mono = s == Soporte.PANTALLA || s == Soporte.CARPETA
        val fuente = when { tema == TemaId.REFUGIO -> LetraExtra.vt323; mono -> Letra.codigo; else -> null }
        Column(Modifier.fillMaxSize().padding(start = izq, top = arr, end = der, bottom = aba), verticalArrangement = Arrangement.Center,
            horizontalAlignment = if (s == Soporte.LIBRO) Alignment.CenterHorizontally else Alignment.Start) {
            if (tema == TemaId.CIBER) Text(if (noche) "$ nano ~/suenos/$fecha.dream" else "$ nano ~/diario/$fecha.log", fontFamily = Letra.codigo, fontSize = 11.sp, color = m.acento, maxLines = 1)
            Text(Estilo.fecha(fecha), style = ty.titleSmall, color = acento, maxLines = 1, overflow = TextOverflow.Ellipsis,
                fontFamily = fuente ?: ty.titleSmall.fontFamily, textAlign = if (s == Soporte.LIBRO) TextAlign.Center else TextAlign.Start)
            Text(if (vista.isBlank()) (if (noche) pistaSueno(s) else pista(s)) else vista, color = tinta.copy(alpha = if (vista.isBlank()) 0.65f else 0.9f),
                fontFamily = fuente ?: if (s == Soporte.LIBRO || s == Soporte.ROLLO || s == Soporte.SOBRE) Letra.garamond else Letra.texto,
                fontSize = if (tema == TemaId.REFUGIO) 17.sp else 13.5.sp, lineHeight = if (tema == TemaId.REFUGIO) 18.sp else 17.sp,
                maxLines = if (s == Soporte.SOBRE) 2 else 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp),
                textAlign = if (s == Soporte.LIBRO) TextAlign.Center else TextAlign.Start)
            if (palabras > 0) Text(if (s == Soporte.PANTALLA) "[$palabras palabras]" else "$palabras palabras",
                fontSize = 10.5.sp, color = tinta.copy(alpha = 0.6f), fontFamily = fuente ?: Letra.texto, modifier = Modifier.padding(top = 3.dp))
        }
        if (s == Soporte.CARPETA) Text(if (noche) "SUEÑO" else "ARCHIVO", color = m.acento.copy(alpha = 0.75f), fontFamily = LetraExtra.oswald, fontSize = 15.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = h * 0.36f, end = w * 0.06f).rotate(-10f)
                .border(2.dp, m.acento.copy(alpha = 0.7f)).padding(horizontal = 6.dp, vertical = 1.dp))
        if (s == Soporte.CARPETA) Text("EXPEDIENTE", color = if (noche) LUNA else m.detalle, fontFamily = LetraExtra.oswald, fontSize = 11.sp,
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
        // Franjas azules como un jadag, con greca de martillos.
        TemaId.MONGOL -> listOf(arr + h * 0.07f, aba - h * 0.07f).forEach { y ->
            drawRect(m.cuerpo.copy(alpha = 0.85f), Offset(x0, y - h * 0.035f), Size(x1 - x0, h * 0.07f))
            grecaPequena(Offset(x0 + r, y), x1 - x0 - 2 * r, h * 0.045f, m.acento)
        }
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
        TemaId.BOTANICO -> {
            // Libreta de campo de cuero, con su broche de latón y una fila de hormigas estampada en el borde.
            val x = ini.x + tam.width * 0.86f
            drawRect(m.sombra, Offset(x - w * 0.02f, ini.y), Size(w * 0.04f, tam.height))
            drawRoundRect(m.acento, Offset(x - w * 0.034f, ini.y + tam.height * 0.46f), Size(w * 0.068f, tam.height * 0.08f), CornerRadius(3f))
            var xx = ini.x + lomo + 12f
            while (xx < x - 18f) {
                listOf(ini.y + 8f, ini.y + tam.height - 8f).forEach { y ->
                    drawCircle(m.acento.copy(alpha = 0.8f), 2.6f, Offset(xx - 4f, y)); drawCircle(m.acento.copy(alpha = 0.8f), 1.6f, Offset(xx, y)); drawCircle(m.acento.copy(alpha = 0.8f), 2f, Offset(xx + 3.6f, y))
                }
                xx += 22f
            }
        }
        TemaId.GOTICO -> {
            // Terciopelo azul sembrado de flores de lis y broches de oro.
            var y = ini.y + 10f; var fila = 0
            while (y < ini.y + tam.height - 6f) {
                var x = ini.x + lomo + (if (fila % 2 == 0) 10f else 22f)
                while (x < ini.x + tam.width - 6f) { drawCircle(m.acento.copy(alpha = 0.55f), 2.4f, Offset(x, y)); drawCircle(m.acento.copy(alpha = 0.55f), 1.4f, Offset(x - 3f, y + 3f)); drawCircle(m.acento.copy(alpha = 0.55f), 1.4f, Offset(x + 3f, y + 3f)); x += 24f }
                y += 14f; fila++
            }
            esquinas.forEach { drawCircle(m.acento, 7f, it); drawCircle(m.extra, 3f, it) }
            listOf(0.3f, 0.7f).forEach { f -> drawRoundRect(m.acento, Offset(ini.x + tam.width - w * 0.05f, ini.y + tam.height * f - h * 0.045f), Size(w * 0.075f, h * 0.09f), CornerRadius(3f)) }
        }
        TemaId.ANDALUS -> {
            // Encuadernación andalusí: la solapa (lisán) que cierra por delante y una estrella de lacería al centro.
            val solapa = Path().apply { moveTo(ini.x + tam.width, ini.y + tam.height * 0.18f); lineTo(ini.x + tam.width + w * 0.035f, ini.y + tam.height * 0.5f); lineTo(ini.x + tam.width, ini.y + tam.height * 0.82f); close() }
            drawPath(solapa, m.sombra)
            esquinas.forEach { o -> for (i in 0 until 8) { val a = Math.toRadians(45.0 * i); drawLine(m.acento.copy(alpha = 0.8f), o, Offset(o.x + 9f * cos(a).toFloat(), o.y + 9f * sin(a).toFloat()), 1.5f) } }
        }
        TemaId.RENACIMIENTO -> {
            // Cuaderno de vitela flexible atado con una correa; un círculo de sanguina en la tapa.
            val x = ini.x + tam.width * 0.93f
            drawRect(m.detalle.copy(alpha = 0.8f), Offset(ini.x + lomo, ini.y + tam.height * 0.47f), Size(tam.width - lomo + w * 0.03f, h * 0.035f))
            drawCircle(m.detalle, 5f, Offset(x, ini.y + tam.height * 0.485f))
            drawCircle(m.acento.copy(alpha = 0.4f), h * 0.16f, Offset(ini.x + tam.width * 0.8f, ini.y + tam.height * 0.24f), style = Stroke(1.5f))
        }
        TemaId.URSS -> {
            val c = Offset(ini.x + lomo + w * 0.08f, ini.y + tam.height * 0.5f)
            val est = Path(); for (i in 0 until 10) { val rr = if (i % 2 == 0) h * 0.13f else h * 0.05f; val a = Math.toRadians(-90.0 + 36.0 * i); val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat()); if (i == 0) est.moveTo(p.x, p.y) else est.lineTo(p.x, p.y) }
            est.close(); drawPath(est, m.acento)
            drawRect(m.detalle, Offset(ini.x + lomo, ini.y + tam.height * 0.86f), Size(tam.width - lomo, tam.height * 0.06f))
        }
        TemaId.REVOLUCION -> {
            // Cinta tricolor que baja por la tapa y la escarapela clavada encima.
            val x = ini.x + lomo + w * 0.06f; val an = w * 0.018f
            listOf(c(0xFF1F3A6E), c(0xFFF4F1E8), c(0xFFB22234)).forEachIndexed { i, col -> drawRect(col, Offset(x + i * an, ini.y), Size(an, tam.height)) }
            val o = Offset(x + an * 1.5f, ini.y + tam.height * 0.5f)
            drawCircle(c(0xFFB22234), h * 0.1f, o); drawCircle(c(0xFFF4F1E8), h * 0.066f, o); drawCircle(c(0xFF1F3A6E), h * 0.034f, o)
        }
        TemaId.ANTIGUAS -> {
            // Esquinas de hierro y el broche con su gema.
            val e = h * 0.12f
            listOf(Offset(ini.x + lomo, ini.y), Offset(ini.x + tam.width, ini.y), Offset(ini.x + lomo, ini.y + tam.height), Offset(ini.x + tam.width, ini.y + tam.height)).forEachIndexed { i, o ->
                val sx = if (i % 2 == 0) 1f else -1f; val sy = if (i < 2) 1f else -1f
                drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + sx * e, o.y); lineTo(o.x, o.y + sy * e); close() }, m.detalle)
            }
            drawRect(m.detalle, Offset(ini.x + tam.width - w * 0.02f, ini.y + tam.height * 0.44f), Size(w * 0.04f, tam.height * 0.12f))
            drawCircle(m.extra, h * 0.035f, Offset(ini.x + tam.width, ini.y + tam.height * 0.5f))
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
internal fun DrawScope.sobre(t: TemaId, m: Materia, e: ColorScheme, luna: Boolean = false) {
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
    if (luna) mediaLuna(c, r * 0.5f, m.brillo.copy(alpha = 0.95f))
    else inset(c.x - k, c.y - k, w - (c.x + k), h - (c.y + k)) { dibujarEmblema(t, 0.3f, e, true) }
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
fun EstanteDiario(entradas: List<Dia>, tema: TemaId = Estilo.tema, noche: Boolean = false, abrir: (String) -> Unit) {
    if (entradas.isEmpty()) return
    val m = if (noche) materiaNoche(tema) else materia(tema)
    val sentir = rememberSentir()
    val conSonido: (String) -> Unit = { f -> sentir(Sentidos.Evento.ABRIR); abrir(f) }
    // De noche el objeto guarda el sueño; de día, la entrada.
    val texto: (Dia) -> String = if (noche) { d -> d.sueno_texto } else { d -> d.nota }
    when (soporte(tema)) {
        Soporte.LIBRO -> Lomos(entradas, tema, m, noche, texto, conSonido)
        Soporte.ROLLO -> Rollos(entradas, tema, m, noche, texto, conSonido)
        Soporte.SOBRE -> Cartas(entradas, tema, m, noche, texto, conSonido)
        Soporte.CARPETA -> Expedientes(entradas, m, noche, texto, conSonido)
        Soporte.PANTALLA -> Logs(entradas, tema, m, noche, texto, conSonido)
    }
}

private fun madera(t: TemaId): Pair<Color, Color> = when (t) {
    TemaId.ORDEN -> Color(0xFF6E7883) to Color(0xFF2A2F35)
    TemaId.LABORATORIO -> Color(0xFFE3ECF3) to Color(0xFF97A8B9)
    TemaId.SEDA -> Color(0xFF8E2B1F) to Color(0xFF4A120C)
    TemaId.BOTANICO -> Color(0xFF8A6A3E) to Color(0xFF4A3820)
    TemaId.GOTICO -> Color(0xFFBDB4A0) to Color(0xFF7E7563)
    TemaId.RENACIMIENTO -> Color(0xFF5A3A22) to Color(0xFF2E1D10)
    TemaId.MONGOL -> Color(0xFFC2412D) to Color(0xFF7A2418)
    else -> Color(0xFF7A5230) to Color(0xFF3E2716)
}

@Composable
private fun Tabla(tema: TemaId, noche: Boolean = false) {
    val (a, b) = madera(tema).let { (x, y) -> if (noche) lerp(x, NOCHE, 0.5f) to lerp(y, NOCHE, 0.55f) else x to y }
    Box(Modifier.fillMaxWidth().height(11.dp).background(Brush.verticalGradient(listOf(a, b))))
    Box(Modifier.fillMaxWidth().height(6.dp).background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent))))
}

private val MESES3 = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")

/** Libros de pie en estantes: el grosor depende de cuánto escribiste y el color del ánimo del día. */
@Composable
private fun Lomos(entradas: List<Dia>, tema: TemaId, m: Materia, noche: Boolean, texto: (Dia) -> String, abrir: (String) -> Unit) {
    val oscuro = esOscuro()
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val ancho = maxWidth
        val lomos = entradas.map { d -> d to (24 + (Texto.palabras(texto(d)) / 40).coerceAtMost(22)).dp }
        val filas = mutableListOf<MutableList<Pair<Dia, androidx.compose.ui.unit.Dp>>>(mutableListOf())
        var usado = 0.dp
        lomos.forEach { l -> if (usado + l.second + 3.dp > ancho && filas.last().isNotEmpty()) { filas.add(mutableListOf()); usado = 0.dp }; filas.last().add(l); usado += l.second + 3.dp }
        Column {
            filas.forEach { fila ->
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Bottom) {
                    fila.forEach { (d, w) -> Lomo(d, w, tema, m, oscuro, noche) { abrir(d.fecha) } }
                }
                Tabla(tema, noche)
            }
        }
    }
}

@Composable
private fun Lomo(d: Dia, ancho: androidx.compose.ui.unit.Dp, tema: TemaId, m: Materia, oscuro: Boolean, noche: Boolean = false, abrir: () -> Unit) {
    val h = (118 + (d.fecha.hashCode() and 0x7fffffff) % 34).dp
    // De noche, el color del lomo es el del sueño (qué tan agradable fue); de día, el ánimo del día.
    val tono = Tonos.valencia((if (noche) d.sueno_valencia else d.valencia)?.toDouble(), oscuro)
    val cuerpo = if (tono != null) lerp(m.cuerpo, tono, 0.28f) else m.cuerpo
    val dia = d.fecha.takeLast(2).trimStart('0'); val mes = MESES3[(d.fecha.substring(5, 7).toIntOrNull() ?: 1) - 1]
    Box(Modifier.width(ancho).height(h).clickable(onClick = abrir), contentAlignment = Alignment.TopCenter) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val hh = size.height
            drawRoundRect(Brush.horizontalGradient(listOf(lerp(cuerpo, Color.Black, 0.35f), lerp(cuerpo, Color.White, 0.18f), cuerpo, lerp(cuerpo, Color.Black, 0.4f))), Offset.Zero, size, CornerRadius(3f))
            listOf(0.07f, 0.11f, 0.89f, 0.93f).forEach { f -> drawLine(m.acento.copy(alpha = 0.85f), Offset(2f, hh * f), Offset(w - 2f, hh * f), 2.2f) }
            drawRoundRect(m.hoja.copy(alpha = 0.92f), Offset(w * 0.14f, hh * 0.2f), Size(w * 0.72f, hh * 0.27f), CornerRadius(2f))
            if (noche) {
                mediaLuna(Offset(w / 2, hh * 0.64f), w * 0.2f, LUNA.copy(alpha = 0.9f))
                estrellas(d.fecha.hashCode(), 4, LUNA, Rect(w * 0.15f, hh * 0.72f, w * 0.85f, hh * 0.86f), 1.1f)
            } else if (tema == TemaId.URSS) { drawCircle(m.acento, w * 0.16f, Offset(w / 2, hh * 0.66f)) }
            else if (tema == TemaId.REVOLUCION) {
                val o = Offset(w / 2, hh * 0.66f)
                drawCircle(Color(0xFFB22234), w * 0.2f, o); drawCircle(Color(0xFFF4F1E8), w * 0.13f, o); drawCircle(Color(0xFF1F3A6E), w * 0.065f, o)
            }
            else if (tema == TemaId.ANTIGUAS) {
                // Una gema tallada en el lomo.
                val o = Offset(w / 2, hh * 0.66f); val g = w * 0.18f
                drawPath(Path().apply { moveTo(o.x, o.y - g); lineTo(o.x + g * 0.75f, o.y - g * 0.2f); lineTo(o.x, o.y + g); lineTo(o.x - g * 0.75f, o.y - g * 0.2f); close() }, m.extra)
                drawLine(m.acento, Offset(o.x - g * 0.75f, o.y - g * 0.2f), Offset(o.x + g * 0.75f, o.y - g * 0.2f), 1.2f)
            }
            if (!noche && tema == TemaId.BOTANICO) {
                // Un escarabajo estampado en oro sobre el cuero.
                val cx = w / 2; val y0 = hh * 0.64f; val l = w * 0.2f
                drawOval(m.acento, Offset(cx - l * 0.55f, y0 - l * 0.45f), Size(l * 1.1f, l * 1.5f)); drawCircle(m.acento, l * 0.3f, Offset(cx, y0 - l * 0.6f))
                drawLine(lerp(cuerpo, Color.Black, 0.3f), Offset(cx, y0 - l * 0.4f), Offset(cx, y0 + l), 1.2f)
                listOf(-1f, 1f).forEach { s -> for (k in 0 until 3) drawLine(m.acento, Offset(cx + s * l * 0.4f, y0 - l * 0.1f + k * l * 0.4f), Offset(cx + s * l * 0.85f, y0 - l * 0.3f + k * l * 0.55f), 1.4f) }
            }
            if (!noche && tema == TemaId.GOTICO) { drawCircle(m.acento, w * 0.12f, Offset(w / 2, hh * 0.64f)); drawCircle(m.extra, w * 0.06f, Offset(w / 2, hh * 0.64f)) }
            if (!noche && tema == TemaId.ANDALUS) for (i in 0 until 8) { val a = Math.toRadians(45.0 * i); drawLine(m.acento, Offset(w / 2, hh * 0.66f), Offset(w / 2 + w * 0.22f * cos(a).toFloat(), hh * 0.66f + w * 0.22f * sin(a).toFloat()), 1.5f) }
            if (!noche && tema == TemaId.RENACIMIENTO) drawRect(m.detalle.copy(alpha = 0.7f), Offset(0f, hh * 0.62f), Size(w, hh * 0.03f))
        }
        Column(Modifier.padding(top = h * 0.205f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dia, color = m.tinta, fontSize = 13.sp, fontFamily = Letra.garamond, lineHeight = 13.sp, maxLines = 1)
            Text(mes, color = m.tinta.copy(alpha = 0.75f), fontSize = 8.5.sp, fontFamily = Letra.texto, lineHeight = 9.sp, maxLines = 1)
        }
    }
}

/** Rollos tumbados en un estante, con su etiqueta colgando. */
@Composable
private fun Rollos(entradas: List<Dia>, tema: TemaId, m: Materia, noche: Boolean, texto: (Dia) -> String, abrir: (String) -> Unit) {
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
                            if (tema == TemaId.MONGOL) { drawRect(m.acento, Offset(x0 + largo * 0.2f, h * 0.2f), Size(largo * 0.04f, h * 0.6f)); drawRect(m.extra, Offset(x0 + largo * 0.55f, h * 0.2f), Size(largo * 0.05f, h * 0.75f)) }
                            drawLine(m.acento, Offset(w / 2, h * 0.8f), Offset(w / 2, h), 1.5f)
                            if (noche) estrellas(d.fecha.hashCode(), 7, LUNA, Rect(x0 + largo * 0.1f, h * 0.28f, x0 + largo * 0.85f, h * 0.72f), 1.2f)
                        }
                        val dia = d.fecha.takeLast(2).trimStart('0'); val mes = MESES3[(d.fecha.substring(5, 7).toIntOrNull() ?: 1) - 1]
                        Text((if (noche) "☾ " else "") + "$dia $mes", color = if (noche) LUNA else m.hoja, fontSize = 11.sp, fontFamily = Letra.garamond, maxLines = 1,
                            modifier = Modifier.background(if (noche) lerp(m.extra, NOCHE, 0.45f) else m.extra, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 1.dp))
                        Text("${Texto.palabras(texto(d))} pal.", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 1.dp))
                    }
                }
                repeat(3 - fila.size) { Spacer(Modifier.weight(1f)) }
            }
            Tabla(tema, noche)
        }
    }
}

/** Cartas selladas, de dos en dos. */
@Composable
private fun Cartas(entradas: List<Dia>, tema: TemaId, m: Materia, noche: Boolean, texto: (Dia) -> String, abrir: (String) -> Unit) {
    val e = MaterialTheme.colorScheme.copy(primary = m.brillo, secondary = m.detalle, tertiary = m.brillo, background = m.acento, onBackground = m.brillo)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 6.dp)) {
        entradas.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { d ->
                    Box(Modifier.weight(1f).height(104.dp).clickable { abrir(d.fecha) }) {
                        Canvas(Modifier.fillMaxSize()) { sobre(tema, m, e, luna = noche); if (noche) estrellas(d.fecha.hashCode(), 12, LUNA, Rect(0f, 0f, size.width, size.height * 0.4f), 1.2f) }
                        Column(Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 8.dp, end = 10.dp)) {
                            Text(Fechas.corto(d.fecha), fontSize = 12.sp, color = m.textoCuerpo, fontFamily = Letra.garamond, maxLines = 1)
                            Text("${Texto.palabras(texto(d))} palabras", fontSize = 9.5.sp, color = m.textoCuerpo.copy(alpha = 0.7f), maxLines = 1)
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
private fun Expedientes(entradas: List<Dia>, m: Materia, noche: Boolean, texto: (Dia) -> String, abrir: (String) -> Unit) {
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
                Text((if (noche) "SUEÑO " else "EXP. ") + d.fecha, fontSize = 9.sp, fontFamily = LetraExtra.oswald, color = if (noche) LUNA else m.detalle,
                    modifier = Modifier.padding(start = pestana + 8.dp, top = 3.dp))
                Row(Modifier.padding(start = 10.dp, top = 22.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(Fechas.corto(d.fecha).uppercase(), fontSize = 11.sp, fontFamily = Letra.codigo, color = if (noche) LUNA else m.detalle, modifier = Modifier.width(96.dp), maxLines = 1)
                    Text(texto(d).trim().replace(Regex("\\s+"), " "), fontSize = 11.sp, fontFamily = Letra.codigo, color = (if (noche) LUNA else m.detalle).copy(alpha = 0.8f),
                        maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/** El directorio de logs (cyberpunk) o la lista de cintas de bitácora (refugio). */
@Composable
private fun Logs(entradas: List<Dia>, tema: TemaId, m: Materia, noche: Boolean, texto: (Dia) -> String, abrir: (String) -> Unit) {
    val refugio = tema == TemaId.REFUGIO
    if (tema == TemaId.ESPACIO) { RegistroDeVuelo(entradas, m, noche, texto, abrir); return }
    val fuente = if (refugio) LetraExtra.vt323 else Letra.codigo
    val tam = if (refugio) 17.sp else 11.5.sp
    val fondo = if (refugio) m.hoja else m.cuerpo
    Column(Modifier.fillMaxWidth().background(fondo, RoundedCornerShape(if (refugio) 14.dp else 6.dp))
        .border(1.5.dp, m.acento.copy(alpha = 0.5f), RoundedCornerShape(if (refugio) 14.dp else 6.dp)).padding(12.dp)) {
        Text(if (refugio) (if (noche) "REGISTRO ONÍRICO — ${entradas.size} SUEÑOS" else "BITÁCORA PERSONAL — ${entradas.size} ENTRADAS")
            else "$ ls -lt ~/${if (noche) "suenos" else "diario"}/  # ${entradas.size} archivos", fontFamily = fuente, fontSize = tam,
            color = if (refugio) m.acento else m.detalle, modifier = Modifier.padding(bottom = 6.dp))
        entradas.forEach { d ->
            val p = Texto.palabras(texto(d))
            Row(Modifier.fillMaxWidth().clickable { abrir(d.fecha) }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                if (refugio) {
                    Text("▸ ${Estilo.fecha(d.fecha).substringBefore(" ·")}", fontFamily = fuente, fontSize = tam, color = m.acento, modifier = Modifier.width(140.dp), maxLines = 1)
                    Text(texto(d).trim().replace(Regex("\\s+"), " ").uppercase(), fontFamily = fuente, fontSize = tam, color = m.tinta.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                } else {
                    Text("-rw-", fontFamily = fuente, fontSize = tam, color = m.tinta.copy(alpha = 0.45f))
                    Text("%5dw".format(p), fontFamily = fuente, fontSize = tam, color = m.extra, modifier = Modifier.padding(horizontal = 6.dp))
                    Text("${d.fecha}.${if (noche) "dream" else "log"}", fontFamily = fuente, fontSize = tam, color = m.acento)
                    Text("  " + texto(d).trim().replace(Regex("\\s+"), " "), fontFamily = fuente, fontSize = tam, color = m.tinta.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/** Bitácora de vuelo: cada día es un registro con su día juliano, como en la consola de una misión. */
@Composable
private fun RegistroDeVuelo(entradas: List<Dia>, m: Materia, noche: Boolean, texto: (Dia) -> String, abrir: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().background(m.cuerpo, RoundedCornerShape(14.dp)).border(1.dp, m.acento.copy(alpha = 0.45f), RoundedCornerShape(14.dp)).padding(12.dp)) {
        Text(if (noche) "SUEÑOS EN ÓRBITA · ${entradas.size} TRANSMISIONES NOCTURNAS" else "REGISTRO DE VUELO · ${entradas.size} TRANSMISIONES", fontFamily = LetraExtra.orbita, fontSize = 11.sp, color = m.detalle, modifier = Modifier.padding(bottom = 8.dp))
        entradas.forEach { d ->
            val jd = runCatching { Temas.diaJuliano(Fechas.parse(d.fecha)) }.getOrNull()
            Row(Modifier.fillMaxWidth().clickable { abrir(d.fecha) }.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).background(m.acento, androidx.compose.foundation.shape.CircleShape))
                Text("JD ${jd ?: "—"}", fontFamily = Letra.codigo, fontSize = 11.sp, color = m.acento, modifier = Modifier.padding(start = 8.dp).width(92.dp), maxLines = 1)
                Text(texto(d).trim().replace(Regex("\\s+"), " "), fontFamily = LetraExtra.grotesk, fontSize = 12.sp, color = m.tinta.copy(alpha = 0.8f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text("${Texto.palabras(texto(d))}w", fontFamily = Letra.codigo, fontSize = 10.sp, color = m.extra, modifier = Modifier.padding(start = 6.dp))
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
    TemaId.BOTANICO -> Pagina(R.drawable.papel_lamina, c(0xFFF1E9D2), c(0xFF2A2318), c(0xFF6B5E45), c(0xFF3F5A2C), c(0xFF8C4A22), Letra.garamond, 18.5f, 27f, false, Rayas.NADA, LetraExtra.fraunces)
    TemaId.GOTICO -> Pagina(R.drawable.papel_vitela, c(0xFFF1E8D2), c(0xFF1A1630), c(0xFF5E5870), c(0xFFB3122E), c(0xFF1F3A9E), Letra.garamond, 18.5f, 27f, false, capitular = LetraExtra.textura)
    TemaId.ESPACIO -> Pagina(null, c(0xFF050A18), c(0xFFE8ECF8), c(0xFF6E7BA0), c(0xFF8AB4FF), c(0xFFFFB86B), LetraExtra.grotesk, 16f, 25f, true, Rayas.RENGLONES)
    TemaId.ANDALUS -> Pagina(R.drawable.papel_xativa, c(0xFFF2E6C8), c(0xFF2A1A12), c(0xFF6A5640), c(0xFF1E6B5C), c(0xFFB5432A), LetraExtra.amiri, 18.5f, 28f, false, capitular = LetraExtra.kufi)
    TemaId.MONGOL -> Pagina(R.drawable.papel_estepa, c(0xFFF1EBDC), c(0xFF18202E), c(0xFF5C6577), c(0xFF1F5FA8), c(0xFFC2412D), LetraExtra.filosofo, 17.5f, 27f, false)
    TemaId.RENACIMIENTO -> Pagina(R.drawable.papel_leonardo, c(0xFFEEDFBC), c(0xFF3A2614), c(0xFF7A6040), c(0xFF8C3B1E), c(0xFF2F4F6F), LetraExtra.cormorant, 19.5f, 28f, false, capitular = LetraExtra.italianno)
    TemaId.URSS -> Pagina(null, c(0xFFEFE6D2), c(0xFF161414), c(0xFF6A625A), c(0xFFC1121F), c(0xFF161414), Letra.codigo, 15f, 26f, false, Rayas.RENGLONES)
    TemaId.LABORATORIO -> Pagina(null, c(0xFFF7FBFC), c(0xFF1B2733), c(0xFF5D6F80), c(0xFF0B8FA3), c(0xFFE0557A), LetraExtra.jost, 16.5f, 26f, false, Rayas.CUADRICULA)
    TemaId.NAPOLEON -> Pagina(null, c(0xFFF6F0E0), c(0xFF14213D), c(0xFF5A6278), c(0xFF1E3A8A), c(0xFFA3172E), LetraExtra.bodoni, 18f, 27f, false, Rayas.NADA, LetraExtra.bodoniTitulo)
    TemaId.NAVIDAD -> Pagina(null, c(0xFFFBF6EC), c(0xFF1E2A22), c(0xFF5E6B62), c(0xFFB71C1C), c(0xFF1B5E20), Letra.garamond, 18.5f, 27f, false, Rayas.NADA, LetraExtra.cinzelDeco)
    TemaId.DISTOPIA -> Pagina(null, c(0xFFECE8DC), c(0xFF141414), c(0xFF55585C), c(0xFFC8102E), c(0xFF141414), Letra.codigo, 15f, 24f, false, Rayas.RENGLONES)
    TemaId.CIBER -> Pagina(null, c(0xFF05080E), c(0xFFD6F3F7), c(0xFF4F6B78), c(0xFF00E5FF), c(0xFFFF2E88), Letra.codigo, 15f, 23f, true, Rayas.NUMEROS)
    TemaId.REFUGIO -> Pagina(null, c(0xFF031A0B), c(0xFF7CF098), c(0xFF2F8F4E), c(0xFF3DFF7F), c(0xFFFFB23F), LetraExtra.vt323, 22f, 26f, true, Rayas.NADA)
    TemaId.REVOLUCION -> Pagina(R.drawable.papel_revolucion, c(0xFFF2ECDD), c(0xFF1C1A17), c(0xFF5E584E), c(0xFFB22234), c(0xFF1F3A6E), LetraExtra.cormorant, 19f, 28f, false, Rayas.NADA, LetraExtra.fell)
    TemaId.ANTIGUAS -> Pagina(R.drawable.papel_antiguas, c(0xFFEDE2C6), c(0xFF1E1D1A), c(0xFF5E5646), c(0xFF8E1B24), c(0xFF2E5A3E), Letra.garamond, 18.5f, 27f, false, Rayas.NADA, Letra.inscripcion)
}

/** Fondo de la página: papel, papiro, seda, pantalla… En modo oscuro, el papel claro se atenúa un poco para no deslumbrar. */
@Composable
internal fun FondoPagina(t: TemaId, p: Pagina, modifier: Modifier = Modifier, noche: Boolean = false) {
    val oscuroApp = esOscuro()
    Box(modifier.fillMaxSize().background(p.color)) {
        p.fondo?.let { Image(painterResource(it), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
        // De noche el papel queda debajo de un cielo: se adivina la textura, pero manda el azul de medianoche.
        if (noche) Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(lerp(p.color, NOCHE, 0.4f).copy(alpha = if (p.fondo != null) 0.9f else 0.7f),
            lerp(NOCHE_ALTA, p.acento, 0.12f).copy(alpha = if (p.fondo != null) 0.88f else 0.6f)))))
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
                TemaId.BOTANICO -> {
                    // Marco de lámina y el número de la tabla.
                    drawRect(p.tinta.copy(alpha = 0.35f), Offset(12.dp.toPx(), 12.dp.toPx()), Size(w - 24.dp.toPx(), h - 24.dp.toPx()), style = Stroke(1.2f))
                    drawRect(p.tinta.copy(alpha = 0.2f), Offset(16.dp.toPx(), 16.dp.toPx()), Size(w - 32.dp.toPx(), h - 32.dp.toPx()), style = Stroke(0.8f))
                }
                TemaId.ANTIGUAS -> {
                    // Marco de mapa: doble filete y una rosa de los vientos tenue en la esquina.
                    drawRect(p.tinta.copy(alpha = 0.3f), Offset(10.dp.toPx(), 10.dp.toPx()), Size(w - 20.dp.toPx(), h - 20.dp.toPx()), style = Stroke(1.4f))
                    drawRect(p.detalle.copy(alpha = 0.3f), Offset(14.dp.toPx(), 14.dp.toPx()), Size(w - 28.dp.toPx(), h - 28.dp.toPx()), style = Stroke(0.8f))
                    val o = Offset(w - 46.dp.toPx(), h - 56.dp.toPx()); val r0 = 22.dp.toPx()
                    for (i in 0 until 8) { val a = Math.toRadians(45.0 * i); val rr = if (i % 2 == 0) r0 else r0 * 0.5f; drawLine(p.tinta.copy(alpha = 0.16f), o, Offset(o.x + rr * cos(a).toFloat(), o.y + rr * sin(a).toFloat()), 1.2f) }
                    drawCircle(p.tinta.copy(alpha = 0.12f), r0 * 0.7f, o, style = Stroke(1f))
                }
                TemaId.REVOLUCION -> {
                    // El margen del cuaderno: un filete azul y otro rojo.
                    listOf(34.dp.toPx() to p.detalle, 38.dp.toPx() to p.acento).forEach { (x, col) -> drawLine(col.copy(alpha = 0.45f), Offset(x, 0f), Offset(x, h), 1.5f) }
                }
                TemaId.GOTICO -> {
                    // Margen de libro de horas: filete azul y rojo con bolitas de oro.
                    listOf(18.dp.toPx() to p.detalle, 22.dp.toPx() to p.acento).forEach { (x, col) -> drawLine(col.copy(alpha = 0.5f), Offset(x, 0f), Offset(x, h), 2f) }
                    var y = 30.dp.toPx(); while (y < h) { drawCircle(Color(0xFFC9A227).copy(alpha = 0.7f), 2.2.dp.toPx(), Offset(20.dp.toPx(), y)); y += 46.dp.toPx() }
                }
                TemaId.ESPACIO -> {
                    val az = java.util.Random(77)
                    repeat(160) { val x = az.nextFloat() * w; val y = az.nextFloat() * h; drawCircle(Color.White.copy(alpha = 0.15f + az.nextFloat() * 0.35f), 0.6f + az.nextFloat() * 1.2f, Offset(x, y)) }
                    val k = 18.dp.toPx(); val m0 = 10.dp.toPx()
                    listOf(Offset(m0, m0) to Offset(1f, 1f), Offset(w - m0, m0) to Offset(-1f, 1f), Offset(m0, h - m0) to Offset(1f, -1f), Offset(w - m0, h - m0) to Offset(-1f, -1f)).forEach { (o, d) ->
                        drawLine(p.acento.copy(alpha = 0.55f), o, o + Offset(d.x * k, 0f), 1.5f); drawLine(p.acento.copy(alpha = 0.55f), o, o + Offset(0f, d.y * k), 1.5f)
                    }
                }
                TemaId.ANDALUS -> {
                    drawRect(p.acento.copy(alpha = 0.45f), Offset(10.dp.toPx(), 10.dp.toPx()), Size(w - 20.dp.toPx(), h - 20.dp.toPx()), style = Stroke(1.5f))
                    drawRect(p.detalle.copy(alpha = 0.35f), Offset(14.dp.toPx(), 14.dp.toPx()), Size(w - 28.dp.toPx(), h - 28.dp.toPx()), style = Stroke(1f))
                }
                TemaId.MONGOL -> listOf(0f, h - 10.dp.toPx()).forEach { y ->
                    drawRect(p.acento.copy(alpha = 0.8f), Offset(0f, y), Size(w, 10.dp.toPx()))
                    grecaPequena(Offset(6.dp.toPx(), y + 5.dp.toPx()), w - 12.dp.toPx(), 6.dp.toPx(), Color(0xFFF2C14E))
                }
                TemaId.RENACIMIENTO -> {
                    // Trazos de construcción en un rincón, como en los folios de Leonardo.
                    val o = Offset(w * 0.82f, h * 0.84f)
                    drawCircle(p.tinta.copy(alpha = 0.08f), w * 0.22f, o, style = Stroke(1f))
                    drawRect(p.tinta.copy(alpha = 0.08f), Offset(o.x - w * 0.2f, o.y - w * 0.2f), Size(w * 0.4f, w * 0.4f), style = Stroke(1f))
                    drawLine(p.tinta.copy(alpha = 0.08f), Offset(o.x - w * 0.24f, o.y), Offset(o.x + w * 0.24f, o.y), 1f)
                }
                else -> {}
            }
            if (noche) {
                estrellas(t.ordinal * 17 + 3, (w * h / 5200f).toInt().coerceIn(60, 260), LUNA, max = 1.5f)
            }
        }
        if (oscuroApp && !p.oscura) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.22f)))
    }
}

/** La capitular: primera letra grande y de color, como en un libro iluminado. */
private class Capitular(val familia: FontFamily, val color: Color) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = TransformedText(conCapitular(text, familia, color), OffsetMapping.Identity)
}

internal fun conCapitular(text: AnnotatedString, familia: FontFamily, color: Color): AnnotatedString = buildAnnotatedString {
    append(text)
    val i = text.text.indexOfFirst { it.isLetterOrDigit() }
    if (i >= 0) addStyle(SpanStyle(fontFamily = familia, color = color, fontSize = 2.1.em), i, i + 1)
}

/** Dibuja renglones, cuadrícula o números de línea detrás del texto, alineados con sus líneas. */
@Composable
internal fun Modifier.rayado(p: Pagina, layout: TextLayoutResult?): Modifier {
    if (p.rayas == Rayas.NADA) return this
    val medidor = rememberTextMeasurer(cacheSize = 64)
    val estiloNum = TextStyle(fontFamily = Letra.codigo, fontSize = 11.sp, color = p.tenue)
    return this.drawBehind {
        val paso = (layout?.takeIf { it.lineCount > 1 }?.let { (it.getLineBottom(it.lineCount - 1) - it.getLineTop(0)) / it.lineCount } ?: p.interlinea.sp.toPx())
            .coerceAtLeast(8.dp.toPx())
        val lineas = layout?.lineCount ?: 0
        fun fondoDe(i: Int) = if (layout != null && i < lineas) layout.getLineBottom(i) else (if (lineas > 0) layout!!.getLineBottom(lineas - 1) else 0f) + paso * (i - lineas + 1)
        val total = ((size.height / paso).toInt() + 1).coerceIn(0, 2000)
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
                // Se mide aparte: así no depende del espacio que queda abajo (con drawText(medidor, …) fallaba en el último renglón).
                drawText(medidor.measure("%3d".format(i + 1), estiloNum), topLeft = Offset(-40.dp.toPx(), top + paso * 0.18f))
            }
            else -> {}
        }
    }
}

/** Encabezado de la página: emblema, fecha del tema y una banda. */
@Composable
private fun EncabezadoPagina(t: TemaId, p: Pagina, fecha: String, modo: Escrito = Escrito.DIARIO) {
    val ty = MaterialTheme.typography
    if (modo == Escrito.SUENO) { EncabezadoSueno(t, p, fecha); return }
    Column(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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

/** Encabezado de la página de un sueño: la luna, el rótulo del tema y la noche en que se soñó. */
@Composable
private fun EncabezadoSueno(t: TemaId, p: Pagina, fecha: String) {
    val ty = MaterialTheme.typography
    val antes = runCatching { Fechas.parse(fecha).minusDays(1) }.getOrNull()
    Column(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(width = 120.dp, height = 46.dp)) {
            estrellas(fecha.hashCode(), 14, LUNA, max = 1.4f)
            mediaLuna(Offset(size.width / 2, size.height / 2), size.height * 0.36f, p.acento)
        }
        Text(rotuloSueno(t), fontFamily = if (t == TemaId.REFUGIO) LetraExtra.vt323 else if (t == TemaId.CIBER) Letra.codigo else ty.labelLarge.fontFamily,
            fontSize = if (t == TemaId.REFUGIO) 18.sp else 12.sp, letterSpacing = 1.5.sp, color = p.acento, textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp))
        Text(Estilo.fecha(fecha), style = ty.headlineSmall, color = p.tinta, textAlign = TextAlign.Center,
            fontFamily = if (t == TemaId.REFUGIO) LetraExtra.vt323 else ty.headlineSmall.fontFamily, modifier = Modifier.padding(top = 6.dp))
        val dia = runCatching { Fechas.parse(fecha) }.getOrNull()
        fun sem(d: java.time.LocalDate) = d.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Fechas.ES)
        Text((if (antes != null && dia != null) "la noche del ${sem(antes)} ${antes.dayOfMonth} al ${sem(dia)} ${dia.dayOfMonth} de ${dia.month.getDisplayName(java.time.format.TextStyle.FULL, Fechas.ES)}"
            else "la noche antes del " + Fechas.titulo(fecha).lowercase()) + " · " + Fechas.relativo(fecha),
            style = ty.labelMedium, color = p.tenue, textAlign = TextAlign.Center)
        Meandro(Modifier.padding(top = 10.dp, bottom = 12.dp), color = p.acento.copy(alpha = 0.5f))
    }
}

/** Las frases con ♥ del día, como epígrafe arriba de la hoja: se ven ahí, pero no se guardan en el diario. */
@Composable
private fun Epigrafe(fecha: String, p: Pagina) {
    val frases = remember(fecha, Repo.config.frases_guardadas) { Frases.deLaHoja(fecha) }
    var armada by remember(fecha) { mutableStateOf<String?>(null) }
    val sentirEp = rememberSentir()
    frases.forEach { f ->
        // Tocar la frase deja quitarle el ♥ (y con eso desaparece de la hoja).
        Column(Modifier.fillMaxWidth().padding(start = 44.dp, bottom = 16.dp).clickable { sentirEp(Sentidos.Evento.TOQUE); armada = if (armada == f.texto) null else f.texto },
            horizontalAlignment = Alignment.End) {
            f.original?.let { Text(it, fontFamily = p.texto, fontSize = (p.tam - 5).sp, lineHeight = (p.tam - 2).sp, color = p.tenue,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, textAlign = TextAlign.End) }
            Text("«${f.texto}»", fontFamily = p.texto, fontSize = (p.tam - 3).sp, lineHeight = (p.tam + 1).sp, color = p.tinta.copy(alpha = 0.82f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, textAlign = TextAlign.End)
            Text("— ${f.autor}  ♥", style = MaterialTheme.typography.labelSmall, color = p.acento, textAlign = TextAlign.End, modifier = Modifier.padding(top = 2.dp))
            if (armada == f.texto) Text("quitar ♥", style = MaterialTheme.typography.labelLarge, color = p.acento,
                modifier = Modifier.padding(top = 4.dp).clickable { sentirEp(Sentidos.Evento.CAMBIO); Frases.quitar(f); armada = null }.padding(horizontal = 8.dp, vertical = 6.dp))
        }
    }
}

/** Barra superior de lectura o escritura. */
@Composable
internal fun BarraPagina(p: Pagina, volver: () -> Unit, centro: String, derecha: @Composable () -> Unit) {
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
                tema: TemaId = Estilo.tema, modo: Escrito = Escrito.DIARIO, capitular: Boolean = true) {
    val sueno = modo == Escrito.SUENO
    val p = if (sueno) paginaNoche(tema) else pagina(tema)
    val cuerpo = if (sueno) d?.sueno_texto.orEmpty() else d?.nota.orEmpty()
    val sentirPagina = rememberSentir()
    var menu by remember { mutableStateOf(false) }
    // Deslizar a los lados pasa de día: a la derecha, el anterior; a la izquierda, el siguiente.
    val umbralPaso = with(androidx.compose.ui.platform.LocalDensity.current) { 110.dp.toPx() }
    val ant by androidx.compose.runtime.rememberUpdatedState(anterior)
    val sig by androidx.compose.runtime.rememberUpdatedState(siguiente)
    val deslizar = Modifier.pointerInput(fecha) {
        var total = 0f
        detectHorizontalDragGestures(onDragStart = { total = 0f }, onDragEnd = {
            if (total > umbralPaso) ant?.let { sentirPagina(Sentidos.Evento.PASAR); it() }
            else if (total < -umbralPaso) sig?.let { sentirPagina(Sentidos.Evento.PASAR); it() }
        }) { _, dx -> total += dx }
    }
    MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
        Box(Modifier.fillMaxSize().then(deslizar)) {
            FondoPagina(tema, p, noche = sueno)
            Column(Modifier.fillMaxSize()) {
                BarraPagina(p, volver, if (d != null) "${Texto.palabras(cuerpo)} palabras" else "") {
                    Text("‹", fontSize = 24.sp, color = if (anterior != null) p.acento else p.tenue.copy(alpha = 0.3f),
                        modifier = Modifier.clickable(enabled = anterior != null) { sentirPagina(Sentidos.Evento.PASAR); anterior?.invoke() }.padding(horizontal = 10.dp))
                    Text("›", fontSize = 24.sp, color = if (siguiente != null) p.acento else p.tenue.copy(alpha = 0.3f),
                        modifier = Modifier.clickable(enabled = siguiente != null) { sentirPagina(Sentidos.Evento.PASAR); siguiente?.invoke() }.padding(horizontal = 10.dp))
                    Box {
                        Text("⋯", fontSize = 22.sp, color = p.acento, modifier = Modifier.clickable { menu = true }.padding(horizontal = 10.dp))
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }, containerColor = p.color) {
                            acciones.forEach { (t, f) -> DropdownMenuItem(text = { Text(t, color = p.tinta) }, onClick = { menu = false; f() }) }
                        }
                    }
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = if (p.rayas == Rayas.NUMEROS) 52.dp else 30.dp, end = 26.dp, bottom = 40.dp)) {
                    EncabezadoPagina(tema, p, fecha, modo)
                    if (!sueno) Epigrafe(fecha, p)
                    if (d == null || cuerpo.isBlank()) {
                        Text(if (sueno) "No anotaste ningún sueño esa noche." else "No hay nada escrito este día.", color = p.tenue, fontFamily = p.texto, fontSize = p.tam.sp)
                    } else if (sueno) {
                        var layout by remember(cuerpo) { mutableStateOf<TextLayoutResult?>(null) }
                        val csPag = MaterialTheme.colorScheme
                        val texto = AnnotatedString(cuerpo.trim()).let { if (capitular && p.capitular != null) conCapitular(it, p.capitular, p.acento) else it }.let { Marcas.anotar(it, tema, csPag, conFondo = false) }
                        SelectionContainer {
                            Text(texto, color = p.tinta, fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, onTextLayout = { layout = it },
                                modifier = Modifier.fillMaxWidth().rayado(p, layout).drawBehind { layout?.let { l -> with(Marcas) { dibujar(l, texto.text, tema, csPag) } } })
                        }
                        LibroDiario.metaSueno(d).takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.labelMedium, color = p.tenue, modifier = Modifier.padding(top = 22.dp)) }
                    } else {
                        var layout by remember(d.nota) { mutableStateOf<TextLayoutResult?>(null) }
                        val csPag = MaterialTheme.colorScheme
                        val texto = AnnotatedString(d.nota.trim()).let { if (capitular && p.capitular != null) conCapitular(it, p.capitular, p.acento) else it }.let { Marcas.anotar(it, tema, csPag, conFondo = false) }
                        SelectionContainer {
                            Text(texto, color = p.tinta, fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, onTextLayout = { layout = it },
                                modifier = Modifier.fillMaxWidth().rayado(p, layout).drawBehind { layout?.let { l -> with(Marcas) { dibujar(l, texto.text, tema, csPag) } } })
                        }
                        if (d.mejor_momento.isNotBlank() || d.peor_momento.isNotBlank()) Column(Modifier.padding(top = 22.dp)) {
                            if (d.mejor_momento.isNotBlank()) Text("Lo mejor — " + d.mejor_momento, color = p.tinta, fontFamily = p.texto, fontSize = (p.tam - 2).sp, lineHeight = (p.interlinea - 3).sp)
                            if (d.peor_momento.isNotBlank()) Text("Lo peor — " + d.peor_momento, color = p.tinta, fontFamily = p.texto, fontSize = (p.tam - 2).sp, lineHeight = (p.interlinea - 3).sp, modifier = Modifier.padding(top = 6.dp))
                        }
                        if (d.sueno_texto.isNotBlank()) Text("☾ " + d.sueno_texto.trim(), color = p.tenue, fontFamily = p.texto, fontSize = (p.tam - 2).sp, lineHeight = (p.interlinea - 3).sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, modifier = Modifier.padding(top = 18.dp))
                        LibroDiario.meta(d).takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.labelMedium, color = p.tenue, modifier = Modifier.padding(top = 22.dp)) }
                    }
                    Text(if (sueno) colofonSueno(tema) else colofon(tema), color = p.acento, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 26.dp))
                }
            }
        }
    }
}

/** Escribir el diario a pantalla completa, sobre el papel del tema. Se guarda solo; el botón guarda y vuelve. */
@Composable
fun EscritorTema(fecha: String, valor: String, onCambio: (String) -> Unit, guardar: () -> Unit, tema: TemaId = Estilo.tema, dictado: Boolean = true,
                 modo: Escrito = Escrito.DIARIO, encabezado: (@Composable (Pagina) -> Unit)? = null, pista: String? = null, textoBoton: String? = null) {
    val p = if (modo == Escrito.SUENO) paginaNoche(tema) else pagina(tema)
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
            FondoPagina(tema, p, noche = modo == Escrito.SUENO)
            // Arriba y a los lados, la barra del sistema; abajo, el teclado del teléfono o la barra de navegación (sin contarlas dos veces).
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)).imePadding()) {
                BarraPagina(p, guardar, "${Texto.palabras(campo.text)} palabras") {
                    Text(textoBoton ?: textoGuardar(tema), style = MaterialTheme.typography.labelLarge, color = if (p.oscura) p.color else Color.White,
                        modifier = Modifier.padding(end = 6.dp).background(p.acento, MaterialTheme.shapes.small).clickable { sentir(Sentidos.Evento.GUARDAR); guardar() }
                            .padding(horizontal = 14.dp, vertical = 8.dp))
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = if (p.rayas == Rayas.NUMEROS) 52.dp else 30.dp, end = 26.dp, bottom = 24.dp)) {
                    if (encabezado != null) encabezado(p) else EncabezadoPagina(tema, p, fecha, modo)
                    if (modo == Escrito.DIARIO) Epigrafe(fecha, p)
                    val estilo = TextStyle(fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, color = p.tinta)
                    BasicTextField(
                        value = campo, onValueChange = { campo = it; if (it.text != valor) onCambio(it.text) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 420.dp).rayado(p, layout),
                        textStyle = estilo, cursorBrush = SolidColor(p.acento), onTextLayout = { layout = it },
                        visualTransformation = rememberMarcas(p.capitular?.let { Capitular(it, p.acento) } ?: VisualTransformation.None),
                        decorationBox = { interior ->
                            Box {
                                if (campo.text.isEmpty()) Text(pista ?: when {
                                    modo == Escrito.SUENO -> if (tema == TemaId.CIBER) "// ¿qué soñaste?" else "¿Qué soñaste? Aunque sean fragmentos."
                                    tema == TemaId.CIBER -> "// ¿cómo fue el día?"
                                    else -> "¿Cómo fue el día?"
                                }, style = estilo.copy(color = p.tenue.copy(alpha = 0.7f)))
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
                androidx.compose.foundation.layout.Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

/** El escritor en una ventana que tapa toda la pantalla (también la barra de abajo). */
@Composable
fun DialogoEscritor(fecha: String, valor: String, onCambio: (String) -> Unit, modo: Escrito = Escrito.DIARIO, cerrar: () -> Unit) {
    Dialog(onDismissRequest = cerrar, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        EscritorTema(fecha, valor, onCambio, cerrar, modo = modo)
    }
}

/** Escribir el sueño de una noche sobre la página nocturna del tema. Se guarda mientras escribes y al cerrar. */
@Composable
fun EscribirSueno(fecha: String, cerrar: () -> Unit) {
    var d by remember(fecha) { mutableStateOf(Repo.dia(fecha)) }
    var sucio by remember(fecha) { mutableStateOf(false) }
    LaunchedEffect(d, sucio) { if (sucio) { delay(800); Repo.guardarDia(d); sucio = false } }
    DialogoEscritor(fecha, d.sueno_texto, { d = d.copy(sueno_texto = it, sueno_recordado = d.sueno_recordado || it.isNotBlank()); sucio = true }, Escrito.SUENO) {
        if (sucio) { Repo.guardarDia(d); sucio = false }; cerrar()
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
