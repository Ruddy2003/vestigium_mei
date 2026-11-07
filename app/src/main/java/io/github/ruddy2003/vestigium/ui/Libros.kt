@file:OptIn(ExperimentalLayoutApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)

package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.porNumero

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
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
import androidx.compose.ui.unit.Constraints
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
import kotlin.math.sqrt

// =====================================================================
//  El diario como objeto: rollos, libros, cartas, expedientes y terminales
// =====================================================================

/** En qué se escribe el diario en cada tema (lo usan las cartas para su papelería; el dibujo de cada objeto va por tema). */
enum class Soporte { ROLLO, LIBRO, SOBRE, CARPETA, PANTALLA }

fun soporte(t: TemaId): Soporte = when (t) {
    TemaId.ROMANO -> Soporte.ROLLO
    TemaId.URBE -> Soporte.LIBRO
    TemaId.HELENO -> Soporte.ROLLO
    TemaId.EGIPTO -> Soporte.ROLLO
    TemaId.SEDA -> Soporte.ROLLO
    TemaId.EDO -> Soporte.LIBRO
    TemaId.MONGOL -> Soporte.ROLLO
    TemaId.MEDIEVAL -> Soporte.LIBRO
    TemaId.TEMPLARIO -> Soporte.LIBRO
    TemaId.COLONOS -> Soporte.LIBRO
    TemaId.ORDEN -> Soporte.LIBRO
    TemaId.VAPOR -> Soporte.LIBRO
    TemaId.BOTANICO -> Soporte.LIBRO
    TemaId.RUINAS -> Soporte.LIBRO
    TemaId.URSS -> Soporte.LIBRO
    TemaId.LABORATORIO -> Soporte.LIBRO
    TemaId.GOTICO -> Soporte.LIBRO
    TemaId.ARCANO -> Soporte.LIBRO
    TemaId.ANDALUS -> Soporte.LIBRO
    TemaId.MESO -> Soporte.LIBRO
    TemaId.RENACIMIENTO -> Soporte.LIBRO
    TemaId.PIRATA -> Soporte.LIBRO
    TemaId.REVOLUCION -> Soporte.LIBRO
    TemaId.ANTIGUAS -> Soporte.LIBRO
    TemaId.CUEVA -> Soporte.LIBRO      // una laja que se da vuelta, como una tapa
    TemaId.NAPOLEON -> Soporte.SOBRE
    TemaId.DECO -> Soporte.SOBRE
    TemaId.NAVIDAD -> Soporte.SOBRE
    TemaId.DISTOPIA -> Soporte.CARPETA
    TemaId.CIBER -> Soporte.PANTALLA
    TemaId.NEON -> Soporte.PANTALLA
    TemaId.REFUGIO -> Soporte.PANTALLA
    TemaId.ESPACIO -> Soporte.PANTALLA
    TemaId.ABISMO -> Soporte.LIBRO                // la bitácora impermeable del batiscafo
}

/** Colores del objeto físico (cuero, papiro, lacre…); no cambian con el modo claro u oscuro. */
class Materia(val cuerpo: Color, val sombra: Color, val brillo: Color, val acento: Color, val detalle: Color, val hoja: Color, val tinta: Color,
              val extra: Color = acento, val textoCuerpo: Color = tinta)

private fun c(x: Long) = Color(x)

fun materia(t: TemaId): Materia = when (t) {
    TemaId.ROMANO -> Materia(c(0xFFE3CC94), c(0xFFB08C52), c(0xFFF3E4BC), c(0xFF6B4423), c(0xFFC9A54A), c(0xFFEEDCAE), c(0xFF2A1E14), extra = c(0xFF8E2B1F))
    // El cuaderno escolar de tapa ocre, con el elástico rojo pompeyano, la etiqueta blanca y los boletos verde pálido entre las hojas.
    TemaId.URBE -> Materia(c(0xFFC98A3A), c(0xFF8E5A22), c(0xFFE2AE66), c(0xFFA8432A), c(0xFF3D5E45), c(0xFFF5EEDC), c(0xFF2B2420), extra = c(0xFFDCE8D2))
    TemaId.HELENO -> Materia(c(0xFFE6D3A6), c(0xFFB5935E), c(0xFFF4E6C4), c(0xFF2B1D14), c(0xFFC2652F), c(0xFFEFDFB8), c(0xFF1F1712), extra = c(0xFF1A1410))
    // Papiro del Nilo, signos en lapislázuli, rúbricas en cornalina, hilo de lino (el brillo) y sello de barro.
    TemaId.EGIPTO -> Materia(c(0xFFE6D29A), c(0xFFA88C4E), c(0xFFF4E7BF), c(0xFF1D2F7A), c(0xFFB8471E), c(0xFFEEDDA8), c(0xFF1E160C), extra = c(0xFF6E4A2E))
    TemaId.SEDA -> Materia(c(0xFF2B4A9E), c(0xFF15265F), c(0xFF5A78C8), c(0xFF3E9E83), c(0xFFD9B25B), c(0xFF233E8C), c(0xFFF2EAD3), extra = c(0xFFC0392B))
    // Tapa de índigo (aizome), hilo blanco de la costura, bermellón del sello y washi.
    TemaId.EDO -> Materia(c(0xFF1E3A5F), c(0xFF10223A), c(0xFF3C5F8C), c(0xFFEDE6D6), c(0xFFC9442E), c(0xFFF3EDDD), c(0xFF1B1A1E), extra = c(0xFFB8322A))
    TemaId.MEDIEVAL -> Materia(c(0xFF6B2E1A), c(0xFF3E190D), c(0xFF8A4429), c(0xFFD4A93C), c(0xFFB08D57), c(0xFFEFE3C2), c(0xFF2B1D12), extra = c(0xFFB3261E))
    // La regla de la casa: cuero blanco curtido al alumbre, la cruz roja, herrajes de hierro y correas de cuero pardo.
    TemaId.TEMPLARIO -> Materia(c(0xFFE6DDC6), c(0xFFA39880), c(0xFFFBF7EC), c(0xFFB0191F), c(0xFF4A4E54), c(0xFFF4EFE2), c(0xFF201E1B), extra = c(0xFF5A3A22))
    TemaId.COLONOS -> Materia(c(0xFF4A3423), c(0xFF2B1D12), c(0xFF6A4D35), c(0xFFB8893A), c(0xFF2E2016), c(0xFFF1E6CC), c(0xFF1E1A16), extra = c(0xFF9E2B25))
    TemaId.ORDEN -> Materia(c(0xFF4A525C), c(0xFF2A2F35), c(0xFF6E7883), c(0xFFC9A54A), c(0xFFA3172E), c(0xFFD6D9DC), c(0xFF1C2127))
    // El registro de patentes: tela verde de libro mayor, cantoneras de latón, lomo de ladrillo y el papel crema de los planos.
    TemaId.VAPOR -> Materia(c(0xFF34473C), c(0xFF1D2A22), c(0xFF52685A), c(0xFFC9A04A), c(0xFF9C3D24), c(0xFFEFE4CB), c(0xFF231B14), extra = c(0xFFE2BE6A))
    TemaId.BOTANICO -> Materia(c(0xFF6B4A2E), c(0xFF432C19), c(0xFF8C6844), c(0xFFC9A54A), c(0xFF2E5E5A), c(0xFFF2EAD3), c(0xFF2A2318), extra = c(0xFF8C4A22))
    // Lona verde gastada, cinta plateada, óxido, papel reciclado y una hoja de hiedra.
    TemaId.RUINAS -> Materia(c(0xFF5B6B45), c(0xFF333D27), c(0xFF7A8A60), c(0xFFB8BCB8), c(0xFFA34A1E), c(0xFFE6E3D6), c(0xFF1F2420), extra = c(0xFF7FA35A))
    TemaId.GOTICO -> Materia(c(0xFF1F2F7A), c(0xFF0E1640), c(0xFF3B4FA8), c(0xFFE2B34A), c(0xFFB3122E), c(0xFFF3EBD6), c(0xFF1A1630), extra = c(0xFFB3122E))
    // Tela verde gastada, latón oxidado, lacre oscuro, papel de libreta barata y tinta verde-negra; el bramante de extra.
    TemaId.ARCANO -> Materia(c(0xFF2E4A3E), c(0xFF17271F), c(0xFF4A6B5C), c(0xFFB59A5E), c(0xFF5A2A22), c(0xFFE8DDC0), c(0xFF1E3329), extra = c(0xFF8A7A58))
    TemaId.ANDALUS -> Materia(c(0xFF7A3B1E), c(0xFF45200F), c(0xFF9C5530), c(0xFFD9B25B), c(0xFF1E6B5C), c(0xFFF3E7CC), c(0xFF2A1A12), extra = c(0xFF1E6B5C))
    // Tapas de madera forradas de piel de jaguar, cinabrio, jade, el estuco de las páginas y el azul maya de la fecha.
    TemaId.MESO -> Materia(c(0xFFB9803A), c(0xFF6E4520), c(0xFFD9A65C), c(0xFFA3301E), c(0xFF2F7D57), c(0xFFF2E8D2), c(0xFF2A1810), extra = c(0xFF2C7F9C))
    TemaId.RENACIMIENTO -> Materia(c(0xFF9A7A50), c(0xFF5E462B), c(0xFFB89466), c(0xFF8C3B1E), c(0xFF3A2614), c(0xFFF0E4C6), c(0xFF3A2614), extra = c(0xFF8C3B1E))
    // Cuero ennegrecido de brea, latón, cabo de cáñamo y pergamino húmedo; la fecha en el rojo de la bandera.
    TemaId.PIRATA -> Materia(c(0xFF4A3424), c(0xFF2A1C12), c(0xFF6B4E36), c(0xFFC9A04A), c(0xFFB59A62), c(0xFFE6DCBA), c(0xFF2A1C0F), extra = c(0xFF9A2A1C))
    TemaId.MONGOL -> Materia(c(0xFF1F5FA8), c(0xFF0F3566), c(0xFF4A86CC), c(0xFFC48A1E), c(0xFFC2412D), c(0xFFF4EEDC), c(0xFF18202E), extra = c(0xFFC2412D))
    TemaId.ESPACIO -> Materia(c(0xFF0A1022), c(0xFF040814), c(0xFF17213D), c(0xFF8AB4FF), c(0xFFFFB86B), c(0xFF040814), c(0xFFE8ECF8), extra = c(0xFFC59BFF))
    // Lona engomada azul petróleo, latón de escafandra, papel impermeable y la luz azul del agua.
    TemaId.ABISMO -> Materia(c(0xFF1F4A59), c(0xFF0F2A34), c(0xFF3D7385), c(0xFFB8913E), c(0xFF5CC8E0), c(0xFFEDF2EE), c(0xFF0C2430), extra = c(0xFFC0563F))
    TemaId.URSS -> Materia(c(0xFFB71C1C), c(0xFF6E0F0F), c(0xFFD84343), c(0xFFE8B923), c(0xFF161414), c(0xFFEFE6D2), c(0xFF161414))
    TemaId.LABORATORIO -> Materia(c(0xFF20323F), c(0xFF101B23), c(0xFF3A5363), c(0xFF3CD2E5), c(0xFFFFFFFF), c(0xFFF7FBFC), c(0xFF1B2733))
    TemaId.NAPOLEON -> Materia(c(0xFFF1E6CC), c(0xFFC9B48A), c(0xFFFBF5E6), c(0xFFA3172E), c(0xFF1E3A8A), c(0xFFF6F0E0), c(0xFF14213D), extra = c(0xFFD9B25B))
    // El pasaje de primera clase: cartulina marfil, talón de laca, oro de imprenta, jade y el lacre coral.
    TemaId.DECO -> Materia(c(0xFFF3EAD4), c(0xFFC9B48A), c(0xFFFBF6EA), c(0xFFB8952E), c(0xFF1F6B57), c(0xFFF6F0E1), c(0xFF16130E), extra = c(0xFFB5523B))
    TemaId.NAVIDAD -> Materia(c(0xFFB71C1C), c(0xFF7A1010), c(0xFFD84343), c(0xFFC9A227), c(0xFF1B5E20), c(0xFFFBF6EC), c(0xFF1E2A22), extra = c(0xFFFF5252), textoCuerpo = c(0xFFFBF6EC))
    TemaId.DISTOPIA -> Materia(c(0xFFD8C08A), c(0xFFA88F58), c(0xFFE8D4A4), c(0xFFC8102E), c(0xFF141414), c(0xFFF2EFE6), c(0xFF141414))
    TemaId.CIBER -> Materia(c(0xFF0A1416), c(0xFF030708), c(0xFF132529), c(0xFF00E5FF), c(0xFF2C8C99), c(0xFF030708), c(0xFFBDEFF5), extra = c(0xFFC8FBFF))
    TemaId.NEON -> Materia(c(0xFF1C1324), c(0xFF0C0811), c(0xFF33223E), c(0xFFFF3D9A), c(0xFFFFB238), c(0xFF0C0811), c(0xFFF4E7F0), extra = c(0xFF8CFF5A))
    TemaId.REFUGIO -> Materia(c(0xFFC9C2A6), c(0xFF8E886F), c(0xFFE2DCC4), c(0xFF3DFF7F), c(0xFF2F8F4E), c(0xFF031A0B), c(0xFF7CF098))
    // Cuaderno de quejas: tapa de cartón azul, cinta tricolor y escarapela.
    TemaId.REVOLUCION -> Materia(c(0xFF2F4A7A), c(0xFF1A2A4A), c(0xFF4A6699), c(0xFFB22234), c(0xFFF0EADB), c(0xFFF2ECDD), c(0xFF1C1A17), extra = c(0xFFB22234))
    // Libro de crónicas: cuero gastado, esquinas de hierro, oro viejo y una gema verde.
    TemaId.ANTIGUAS -> Materia(c(0xFF5A3A22), c(0xFF33210F), c(0xFF7A5232), c(0xFFC9A04A), c(0xFF4A4E54), c(0xFFEDE2C6), c(0xFF1E1D1A), extra = c(0xFF2E7A5A))
    // La laja de caliza: la piedra, su sombra y su brillo, el ocre rojo, el carbón, la cara lisa donde se pinta y el ocre amarillo de la fecha.
    TemaId.CUEVA -> Materia(c(0xFFD9C7A6), c(0xFF9C8664), c(0xFFEFE3CC), c(0xFFA2401F), c(0xFF2E2620), c(0xFFEFE4D0), c(0xFF2A211B), extra = c(0xFFB07418))
}

// =====================================================================
//  De noche: los sueños con el mismo objeto del tema, bajo el cielo y la luz de su noche
// =====================================================================

/** Qué se escribe en la página: el diario del día, un sueño, una carta o una idea (que va en una ficha del tema, no en un libro). */
enum class Escrito { DIARIO, SUENO, CARTA, IDEA }

internal val NOCHE = Color(0xFF111833)
internal val NOCHE_ALTA = Color(0xFF261C4C)
internal val LUNA = Color(0xFFEDE6C8)

/**
 * La noche de cada tema: el color de su cielo, la luz que alumbra el objeto (luna, lámpara, fuego, fósforo…), cuánto se
 * hunde el objeto en ese cielo ([hunde]: la lámpara de aceite deja ver más el cuero que la luz fría de las estrellas),
 * cuánto se oscurece el papel ([papel]) y la tinta con que se lee de noche ([tinta]), que es la de su luz.
 */
private class NocheTema(val cielo: Color, val luz: Color, val hunde: Float, val papel: Float, val tinta: Color) {
    /** La luna (o la luz principal) de ese cielo: el blanco de luna teñido de la luz del tema. */
    val luna: Color get() = lerp(LUNA, luz, 0.35f)
}

private fun nocheDe(t: TemaId): NocheTema = when (t) {
    // La lucerna: luz de aceite sobre el pórfido de noche.
    TemaId.ROMANO -> NocheTema(c(0xFF1A1426), c(0xFFE9B860), 0.62f, 0.8f, c(0xFFF2DDB0))
    // El cielo de la ciudad nunca es negro: violeta pardo, encendido por las farolas de sodio.
    TemaId.URBE -> NocheTema(c(0xFF231A22), c(0xFFF0A050), 0.55f, 0.78f, c(0xFFF6DDB8))
    // El monitor en reposo: el fósforo cian baja de brillo.
    TemaId.CIBER -> NocheTema(c(0xFF010304), c(0xFF00E5FF), 0.85f, 0.92f, c(0xFF9FE8F0))
    // La calle a las tres de la mañana: solo quedan encendidos los letreros magenta.
    TemaId.NEON -> NocheTema(c(0xFF0A0610), c(0xFFFF3D9A), 0.8f, 0.9f, c(0xFFF7DCEB))
    // Selene en plata sobre el Egeo.
    TemaId.HELENO -> NocheTema(c(0xFF0E1A33), c(0xFFDCE4F2), 0.7f, 0.86f, c(0xFFE4EAF6))
    // El cuerpo de Nut, azul de noche, y la lámpara de aceite del escriba.
    TemaId.EGIPTO -> NocheTema(c(0xFF0C1230), c(0xFFF2D27A), 0.66f, 0.82f, c(0xFFF4E6BE))
    // La vela del scriptorium.
    TemaId.MEDIEVAL -> NocheTema(c(0xFF1A130C), c(0xFFF2C46B), 0.6f, 0.78f, c(0xFFF0D9A8))
    TemaId.TEMPLARIO -> NocheTema(c(0xFF0F1117), c(0xFFF0A040), 0.62f, 0.8f, c(0xFFEEE7D8))
    // Apagón bajo el reflector.
    TemaId.DISTOPIA -> NocheTema(c(0xFF15171A), c(0xFFD8DEE4), 0.74f, 0.88f, c(0xFFDDE2E6))
    TemaId.REFUGIO -> NocheTema(c(0xFF1A1003), c(0xFFFFB23F), 0.7f, 0.85f, c(0xFFFFC870))
    // Brasas de la forja.
    TemaId.ORDEN -> NocheTema(c(0xFF14181D), c(0xFFE8743A), 0.66f, 0.82f, c(0xFFF3C9A8))
    // La sala de máquinas de noche: hollín azulado y la luz amarilla del gas de alumbrado.
    TemaId.VAPOR -> NocheTema(c(0xFF16161B), c(0xFFF2C46A), 0.62f, 0.8f, c(0xFFF4E2B8))
    // La lámpara ultravioleta del laboratorio.
    TemaId.LABORATORIO -> NocheTema(c(0xFF0F0B22), c(0xFFB59CFF), 0.72f, 0.87f, c(0xFFE3DAFF))
    // El farol del pueblo.
    TemaId.COLONOS -> NocheTema(c(0xFF0F1626), c(0xFFF0A860), 0.64f, 0.8f, c(0xFFF3DDBF))
    // El fuego del vivac.
    TemaId.NAPOLEON -> NocheTema(c(0xFF0C1433), c(0xFFE8C35A), 0.66f, 0.83f, c(0xFFF1E3B8))
    // La noche en alta mar desde la cubierta de paseo, con el oro de las luces del salón.
    TemaId.DECO -> NocheTema(c(0xFF0A0C14), c(0xFFF2D27A), 0.62f, 0.82f, c(0xFFF0E6CC))
    // Luciérnagas en el bosque.
    TemaId.BOTANICO -> NocheTema(c(0xFF0B1A14), c(0xFFC6F06A), 0.7f, 0.85f, c(0xFFE6F2C8))
    // La luna fría sobre las azoteas, sin una sola luz eléctrica.
    TemaId.RUINAS -> NocheTema(c(0xFF0D1512), c(0xFF9FD0C8), 0.68f, 0.82f, c(0xFFDDE8E0))
    // La chimenea.
    TemaId.NAVIDAD -> NocheTema(c(0xFF1A0E0E), c(0xFFFF9E57), 0.6f, 0.79f, c(0xFFFBE0C4))
    // El farol de papel rojo de la caravana.
    TemaId.SEDA -> NocheTema(c(0xFF0D1433), c(0xFFF26B4E), 0.67f, 0.84f, c(0xFFF8DCCF))
    // La noche de la posta, índigo, a la luz ámbar de los farolillos.
    TemaId.EDO -> NocheTema(c(0xFF0E1420), c(0xFFF2B65A), 0.7f, 0.82f, c(0xFFF5E6CC))
    // La luna a través del vitral de zafiro.
    TemaId.GOTICO -> NocheTema(c(0xFF0B0F2E), c(0xFF9FB4FF), 0.75f, 0.88f, c(0xFFDCE3FF))
    // La niebla de la costa y la luz pálida del faro.
    TemaId.ARCANO -> NocheTema(c(0xFF08110E), c(0xFFE6D58C), 0.7f, 0.86f, c(0xFFE3E6CF))
    TemaId.ESPACIO -> NocheTema(c(0xFF07041A), c(0xFFC59BFF), 0.78f, 0.9f, c(0xFFECE6FA))
    // La zona abisal: negro de fondo y la única luz, la de los animales.
    TemaId.ABISMO -> NocheTema(c(0xFF020A12), c(0xFF5CF2C6), 0.8f, 0.9f, c(0xFFD6F0F2))
    // Las lámparas de la Alhambra en el patio.
    TemaId.ANDALUS -> NocheTema(c(0xFF0A1A1E), c(0xFFF2D27A), 0.63f, 0.81f, c(0xFFF6E8C2))
    // La noche de obsidiana en el templo, con la brasa del copal.
    TemaId.MESO -> NocheTema(c(0xFF0C1418), c(0xFFF0B062), 0.66f, 0.8f, c(0xFFF2E6CC))
    // La luz fría de las estrellas sobre la estepa.
    TemaId.MONGOL -> NocheTema(c(0xFF061433), c(0xFFBFD9FF), 0.76f, 0.89f, c(0xFFDCE8FA))
    // La vela del taller, en tono sanguina.
    TemaId.RENACIMIENTO -> NocheTema(c(0xFF1A140E), c(0xFFE3A06B), 0.61f, 0.79f, c(0xFFF2D6C0))
    // El farol de popa sobre el mar de noche.
    TemaId.PIRATA -> NocheTema(c(0xFF0E1416), c(0xFFE8B45A), 0.64f, 0.76f, c(0xFFEADFC4))
    // La estrella roja en la noche polar.
    TemaId.URSS -> NocheTema(c(0xFF141018), c(0xFFFF5A4E), 0.72f, 0.86f, c(0xFFF2D8D6))
    // Las antorchas de la barricada.
    TemaId.REVOLUCION -> NocheTema(c(0xFF101828), c(0xFFFF8A4A), 0.65f, 0.82f, c(0xFFF8D9C2))
    // La luz de estrellas de los bosques antiguos.
    TemaId.ANTIGUAS -> NocheTema(c(0xFF0E1612), c(0xFFB8E0C8), 0.73f, 0.87f, c(0xFFDCEFE2))
    // Lo hondo de la cueva: la roca casi negra y la luz amarilla de la lámpara de grasa.
    TemaId.CUEVA -> NocheTema(c(0xFF120C08), c(0xFFF2B36B), 0.7f, 0.85f, c(0xFFF6D9AE))
}

/** El objeto bajo su noche: el cuerpo se hunde en el cielo del tema y el oro toma la luz que lo alumbra. */
private fun materiaTenida(m: Materia, n: NocheTema): Materia {
    fun o(x: Color, k: Float) = lerp(x, n.cielo, k.coerceIn(0f, 0.95f))
    return Materia(cuerpo = o(m.cuerpo, n.hunde), sombra = o(m.sombra, n.hunde + 0.08f), brillo = o(m.brillo, n.hunde - 0.08f),
        acento = lerp(m.acento, n.luz, 0.3f), detalle = lerp(m.detalle, n.luz, 0.45f),
        hoja = o(m.hoja, n.hunde + 0.06f), tinta = n.tinta, extra = lerp(m.extra, n.luz, 0.35f), textoCuerpo = n.tinta)
}

/** La materia del tema de noche: el cuero, el papiro o la pantalla bajo el cielo y la luz de su noche. */
fun materiaNoche(t: TemaId): Materia {
    val m = materia(t); val n = nocheDe(t)
    return when (t) {
        // El fósforo verde se vuelve ámbar, como una pantalla en reposo.
        TemaId.REFUGIO -> Materia(c(0xFF6E6A58), c(0xFF3E3B30), c(0xFF8E886F), c(0xFFFFB23F), c(0xFFB3741E), c(0xFF1A1003), c(0xFFFFC870))
        // El monitor en reposo: el mismo cian, más bajo.
        TemaId.CIBER -> Materia(c(0xFF061012), c(0xFF020607), c(0xFF0B1C20), c(0xFF00B8CC), c(0xFF2C8C99), c(0xFF020607), c(0xFF9FE8F0), extra = c(0xFFC8FBFF))
        // Apagaron el ámbar de los puestos: queda el magenta y el verde de la farmacia de guardia.
        TemaId.NEON -> Materia(c(0xFF1A0E22), c(0xFF0A050F), c(0xFF2C1838), c(0xFFFF3D9A), c(0xFF8CFF5A), c(0xFF0A050F), c(0xFFF7DCEB), extra = c(0xFFFFB238))
        TemaId.ESPACIO -> Materia(c(0xFF120A2A), c(0xFF06040F), c(0xFF241645), c(0xFFC59BFF), c(0xFF8AB4FF), c(0xFF06040F), c(0xFFECE6FA), extra = c(0xFFFF8AD8))
        TemaId.ABISMO -> Materia(c(0xFF07161F), c(0xFF020A10), c(0xFF0F2A36), c(0xFF5CF2C6), c(0xFFE29BFF), c(0xFF041018), c(0xFFD6F0F2), extra = c(0xFF3FA9FF))
        TemaId.ROMANO -> materiaTenida(m, n)
        TemaId.URBE -> materiaTenida(m, n)
        TemaId.HELENO -> materiaTenida(m, n)
        TemaId.EGIPTO -> materiaTenida(m, n)
        TemaId.MEDIEVAL -> materiaTenida(m, n)
        TemaId.TEMPLARIO -> materiaTenida(m, n)
        TemaId.DISTOPIA -> materiaTenida(m, n)
        TemaId.ORDEN -> materiaTenida(m, n)
        TemaId.VAPOR -> materiaTenida(m, n)
        TemaId.LABORATORIO -> materiaTenida(m, n)
        TemaId.COLONOS -> materiaTenida(m, n)
        TemaId.NAPOLEON -> materiaTenida(m, n)
        TemaId.DECO -> materiaTenida(m, n)
        TemaId.BOTANICO -> materiaTenida(m, n)
        TemaId.RUINAS -> materiaTenida(m, n)
        TemaId.NAVIDAD -> materiaTenida(m, n)
        TemaId.SEDA -> materiaTenida(m, n)
        TemaId.EDO -> materiaTenida(m, n)
        TemaId.GOTICO -> materiaTenida(m, n)
        TemaId.ARCANO -> materiaTenida(m, n)
        TemaId.ANDALUS -> materiaTenida(m, n)
        TemaId.MESO -> materiaTenida(m, n)
        TemaId.MONGOL -> materiaTenida(m, n)
        TemaId.RENACIMIENTO -> materiaTenida(m, n)
        TemaId.PIRATA -> materiaTenida(m, n)
        TemaId.URSS -> materiaTenida(m, n)
        TemaId.REVOLUCION -> materiaTenida(m, n)
        TemaId.ANTIGUAS -> materiaTenida(m, n)
        // La laja a la luz del fuego: la piedra se oscurece, el ocre se enciende y la cara lisa queda en sombra.
        TemaId.CUEVA -> Materia(c(0xFF5A4634), c(0xFF2E2219), c(0xFF8A6A4A), c(0xFFE8804F), c(0xFF1A130E), c(0xFF3A2C20), c(0xFFF2DDB8), extra = c(0xFFF4C76E))
    }
}

/** El papel bajo la noche del tema: se oscurece hacia su cielo y la tinta toma su luz. */
private fun paginaTenida(p: Pagina, n: NocheTema) = Pagina(p.fondo, lerp(p.color, n.cielo, n.papel), n.tinta, lerp(n.tinta, n.cielo, 0.42f),
    lerp(p.acento, n.luz, 0.45f), lerp(p.detalle, n.luz, 0.5f), p.texto, p.tam, p.interlinea, true, p.rayas, p.capitular)

/** La página de un sueño: el papel del tema bajo el cielo de su noche. */
fun paginaNoche(t: TemaId): Pagina {
    val p = pagina(t); val n = nocheDe(t)
    return when (t) {
        TemaId.REFUGIO -> Pagina(null, c(0xFF1A1003), c(0xFFFFC870), c(0xFFB3741E), c(0xFFFFB23F), c(0xFF7CF098), p.texto, p.tam, p.interlinea, true, p.rayas)
        TemaId.CIBER -> Pagina(null, c(0xFF010405), c(0xFF9FE8F0), c(0xFF3E7880), c(0xFF00E5FF), c(0xFF5FC4D2), p.texto, p.tam, p.interlinea, true, p.rayas)
        TemaId.NEON -> Pagina(null, c(0xFF0A0610), c(0xFFF7DCEB), c(0xFF8A6A84), c(0xFFFF3D9A), c(0xFF8CFF5A), p.texto, p.tam, p.interlinea, true, p.rayas)
        TemaId.ESPACIO -> Pagina(null, c(0xFF07041A), c(0xFFECE6FA), c(0xFF8A7BB0), c(0xFFC59BFF), c(0xFFFF8AD8), p.texto, p.tam, p.interlinea, true, p.rayas)
        TemaId.ABISMO -> Pagina(null, c(0xFF020A12), c(0xFFD6F0F2), c(0xFF5E8A96), c(0xFF5CF2C6), c(0xFFE29BFF), p.texto, p.tam, p.interlinea, true, p.rayas)
        TemaId.ROMANO -> paginaTenida(p, n)
        TemaId.URBE -> paginaTenida(p, n)
        TemaId.HELENO -> paginaTenida(p, n)
        TemaId.EGIPTO -> paginaTenida(p, n)
        TemaId.MEDIEVAL -> paginaTenida(p, n)
        TemaId.TEMPLARIO -> paginaTenida(p, n)
        TemaId.DISTOPIA -> paginaTenida(p, n)
        TemaId.ORDEN -> paginaTenida(p, n)
        TemaId.VAPOR -> paginaTenida(p, n)
        TemaId.LABORATORIO -> paginaTenida(p, n)
        TemaId.COLONOS -> paginaTenida(p, n)
        TemaId.NAPOLEON -> paginaTenida(p, n)
        TemaId.DECO -> paginaTenida(p, n)
        TemaId.BOTANICO -> paginaTenida(p, n)
        TemaId.RUINAS -> paginaTenida(p, n)
        TemaId.NAVIDAD -> paginaTenida(p, n)
        TemaId.SEDA -> paginaTenida(p, n)
        TemaId.EDO -> paginaTenida(p, n)
        TemaId.GOTICO -> paginaTenida(p, n)
        TemaId.ARCANO -> paginaTenida(p, n)
        TemaId.ANDALUS -> paginaTenida(p, n)
        TemaId.MESO -> paginaTenida(p, n)
        TemaId.MONGOL -> paginaTenida(p, n)
        TemaId.RENACIMIENTO -> paginaTenida(p, n)
        TemaId.PIRATA -> paginaTenida(p, n)
        TemaId.URSS -> paginaTenida(p, n)
        TemaId.REVOLUCION -> paginaTenida(p, n)
        TemaId.ANTIGUAS -> paginaTenida(p, n)
        TemaId.CUEVA -> Pagina(null, c(0xFF1C140E), c(0xFFEEDFC8), c(0xFF9A8670), c(0xFFE8804F), c(0xFFF4C76E), p.texto, p.tam, p.interlinea, true, p.rayas, p.capitular)
    }
}

/** El rótulo de la página de un sueño en cada tema. */
fun rotuloSueno(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "SOMNIVM"
    TemaId.URBE -> "STANOTTE HO SOGNATO"
    TemaId.CIBER -> "~/suenos/"
    TemaId.NEON -> "夢 · LO QUE SOÑÉ"
    TemaId.HELENO -> "ΟΝΕΙΡΟΣ · sueño"
    TemaId.EGIPTO -> "Resut · el sueño"
    TemaId.MEDIEVAL -> "Visión de la noche"
    TemaId.TEMPLARIO -> "Lo soñado en la vigilia"
    TemaId.DISTOPIA -> "INFORME DE ACTIVIDAD ONÍRICA"
    TemaId.REFUGIO -> "REGISTRO ONÍRICO · TERMINAL 03"
    TemaId.ORDEN -> "PARTE NOCTURNO"
    TemaId.VAPOR -> "Sueños del turno de noche"
    TemaId.LABORATORIO -> "REGISTRO DE SUEÑO"
    TemaId.COLONOS -> "Lo soñado"
    TemaId.NAPOLEON -> "SONGE · sueño"
    TemaId.DECO -> "SUEÑOS DE CAMAROTE"
    TemaId.BOTANICO -> "Flora nocturna"
    TemaId.RUINAS -> "Sueños en la azotea"
    TemaId.NAVIDAD -> "Sueños junto al fuego"
    TemaId.SEDA -> "梦 · sueño"
    TemaId.EDO -> "夢 · sueño en la posada"
    TemaId.URSS -> "СОН · SUEÑO"
    TemaId.GOTICO -> "Visio nocturna"
    TemaId.ARCANO -> "Lo que soñé"
    TemaId.ESPACIO -> "TRANSMISIÓN NOCTURNA"
    TemaId.ABISMO -> "SUEÑO DE LA LITERA"
    TemaId.ANDALUS -> "حلم · sueño"
    TemaId.MESO -> "Lo que pintó la noche"
    TemaId.MONGOL -> "ЗҮҮД · sueño"
    TemaId.RENACIMIENTO -> "Sogno"
    TemaId.PIRATA -> "Lo soñado en el coy"
    TemaId.REVOLUCION -> "RÊVE · SUEÑO"
    TemaId.ANTIGUAS -> "VISIONES DE LA NOCHE"
    TemaId.CUEVA -> "Visiones junto al fuego"
}

/** Lo que cierra la página de un sueño: el despertar, en cada tema. */
fun colofonSueno(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "· EVIGILAVI ·"
    TemaId.URBE -> "— e poi mi sono svegliato —"
    TemaId.CIBER -> "-- wake --"
    TemaId.NEON -> "— abre la persiana —"
    TemaId.HELENO -> "ἐξηγέρθην · desperté"
    TemaId.EGIPTO -> "· rs: desperté ·"
    TemaId.MEDIEVAL -> "Y en esto desperté."
    TemaId.TEMPLARIO -> "Y tocaron a maitines."
    TemaId.DISTOPIA -> "FIN DEL INFORME NOCTURNO"
    TemaId.REFUGIO -> "FIN DEL REGISTRO"
    TemaId.ORDEN -> "TOQUE DE DIANA"
    TemaId.VAPOR -> "— silba el tren del alba —"
    TemaId.LABORATORIO -> "fin del registro onírico"
    TemaId.COLONOS -> "Y amaneció."
    TemaId.NAPOLEON -> "Réveil."
    TemaId.DECO -> "Y sonó la campana del desayuno."
    TemaId.BOTANICO -> "Se cerraron las flores de la noche."
    TemaId.RUINAS -> "Amaneció sobre las azoteas."
    TemaId.NAVIDAD -> "Y llegó la mañana."
    TemaId.SEDA -> "Despertar en la caravana."
    TemaId.EDO -> "Se dobla el futón; sigue el camino."
    TemaId.URSS -> "ПОДЪЁМ · DE PIE"
    TemaId.GOTICO -> "Et evigilavi."
    TemaId.ARCANO -> "Y entonces desperté."
    TemaId.ESPACIO -> "FIN DE LA TRANSMISIÓN NOCTURNA"
    TemaId.ABISMO -> "Y SUBÍ A LA SUPERFICIE."
    TemaId.ANDALUS -> "واستيقظت · y desperté"
    TemaId.MESO -> "Y salió K'in por el oriente."
    TemaId.MONGOL -> "Сэрлээ · desperté"
    TemaId.RENACIMIENTO -> "E mi destai."
    TemaId.PIRATA -> "Y sonó la campana del alba."
    TemaId.REVOLUCION -> "DEBOUT · EN PIE"
    TemaId.ANTIGUAS -> "Y la compañía despertó."
    TemaId.CUEVA -> "— y el fuego aún tenía brasas —"
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

private fun polarLibro(r: Float, a: Double) = Offset(r * cos(a).toFloat(), r * sin(a).toFloat())

/** Estrella de [puntas] puntas; [interior] es el radio de los valles respecto del de las puntas. */
private fun DrawScope.estrellaPuntas(ce: Offset, r: Float, color: Color, puntas: Int = 5, interior: Float = 0.42f) {
    val p = Path()
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * interior
        val q = ce + polarLibro(rr, -PI / 2 + PI * i / puntas)
        if (i == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
    }
    p.close(); drawPath(p, color)
}

/** Asterisco de trazos (como las estrellitas de los márgenes). */
private fun DrawScope.asterisco(ce: Offset, l: Float, color: Color, brazos: Int, grosor: Float) {
    for (i in 0 until brazos / 2) { val a = 2 * PI * i / brazos; drawLine(color, ce - polarLibro(l, a), ce + polarLibro(l, a), grosor) }
}

/** Copo de nieve: seis brazos con sus ramitas. */
private fun DrawScope.copoNieve(ce: Offset, l: Float, color: Color) {
    for (i in 0 until 6) {
        val a = PI * i / 3
        val punta = ce + polarLibro(l, a); val medio = ce + polarLibro(l * 0.6f, a)
        drawLine(color, ce, punta, 0.8f)
        drawLine(color, medio, medio + polarLibro(l * 0.3f, a + 0.7), 0.7f); drawLine(color, medio, medio + polarLibro(l * 0.3f, a - 0.7), 0.7f)
    }
}

/** Las estrellas de cada tema: puntos, píxeles, cruces, asteriscos, copos, rombos… La misma semilla da siempre el mismo cielo. */
private fun DrawScope.estrellasDelTema(t: TemaId, semilla: Int, n: Int, color: Color, area: Rect = size.toRect(), max: Float = 1.6f) {
    val az = java.util.Random(semilla.toLong())
    repeat(n) { i ->
        val o = Offset(area.left + az.nextFloat() * area.width, area.top + az.nextFloat() * area.height)
        val r = 0.45f + az.nextFloat() * max
        val col = color.copy(alpha = 0.25f + az.nextFloat() * 0.6f)
        when (t) {
            TemaId.ROMANO -> drawCircle(col, r, o)
            // Con tanta luz de ciudad se ven pocas estrellas, y entre ellas pasan las luces de los aviones que van a Fiumicino.
            TemaId.URBE -> if (i % 7 == 0) { drawCircle(Color(0xFFFF6A50).copy(alpha = col.alpha), r * 0.7f, o); drawCircle(col, r * 0.5f, o + Offset(r * 2.4f, r * 0.3f)) }
                else drawCircle(col.copy(alpha = col.alpha * 0.55f), r * 0.75f, o)
            TemaId.CIBER -> drawRect(col, o, Size(r * 1.6f, r * 1.6f))
            // Luces de dron y de grúa: rayitas que cruzan el cielo turbio.
            TemaId.NEON -> drawLine(col, o, o + Offset(r * 3.2f, 0f), r * 0.7f, cap = StrokeCap.Round)
            TemaId.HELENO -> {
                drawCircle(col, r, o)
                if (r > max * 0.92f) { drawLine(col, o - Offset(r * 2.8f, 0f), o + Offset(r * 2.8f, 0f), 0.8f); drawLine(col, o - Offset(0f, r * 2.8f), o + Offset(0f, r * 2.8f), 0.8f) }
            }
            // Las estrellas de cinco puntas de los techos de las tumbas; las pequeñas, puntos.
            TemaId.EGIPTO -> {
                if (r > max * 0.5f) {
                    val p = Path()
                    for (k in 0 until 10) {
                        val q = o + polarLibro(if (k % 2 == 0) r * 2.2f else r * 0.9f, -PI / 2 + PI * k / 5)
                        if (k == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
                    }
                    p.close(); drawPath(p, col)
                } else drawCircle(col, r * 0.8f, o)
            }
            TemaId.MEDIEVAL -> asterisco(o, r * 1.7f, col, 6, 0.9f)
            TemaId.TEMPLARIO -> drawPath(cruzPatada(o, r * 1.8f), col)
            TemaId.DISTOPIA -> drawCircle(col.copy(alpha = col.alpha * 0.45f), r * 0.8f, o)
            TemaId.REFUGIO -> { val p = r * 0.8f; drawRect(col, Offset(o.x - p / 2, o.y - p * 1.5f), Size(p, p * 3)); drawRect(col, Offset(o.x - p * 1.5f, o.y - p / 2), Size(p * 3, p)) }
            TemaId.ORDEN -> { drawLine(col, o - Offset(r * 2.4f, 0f), o + Offset(r * 2.4f, 0f), 1f); drawLine(col, o - Offset(0f, r * 2.4f), o + Offset(0f, r * 2.4f), 1f); drawCircle(col, r * 0.7f, o) }
            // Con tanto hollín apenas se ven estrellas: son chispas de la chimenea que suben, con su halo.
            TemaId.VAPOR -> { drawCircle(col.copy(alpha = col.alpha * 0.25f), r * 2f, o); drawCircle(col, r * 0.6f, o); if (i % 5 == 0) drawLine(col.copy(alpha = col.alpha * 0.5f), o, o + Offset(-r * 1.5f, r * 3f), 0.8f) }
            TemaId.LABORATORIO -> { drawLine(col, o - Offset(r * 1.5f, 0f), o + Offset(r * 1.5f, 0f), 0.7f); drawLine(col, o - Offset(0f, r * 1.5f), o + Offset(0f, r * 1.5f), 0.7f) }
            TemaId.COLONOS -> { drawCircle(col, r * 0.8f, o); drawCircle(col.copy(alpha = col.alpha * 0.3f), r * 2f, o, style = Stroke(0.6f)) }
            TemaId.NAPOLEON -> estrellaPuntas(o, r * 1.8f, col, 5, 0.42f)
            // Destellos finos de cuatro puntas, como las bombillas de una marquesina vistas de lejos.
            TemaId.DECO -> estrellaPuntas(o, r * 1.7f, col, 4, 0.2f)
            TemaId.BOTANICO -> { drawCircle(col.copy(alpha = col.alpha * 0.25f), r * 3f, o); drawCircle(col, r * 0.8f, o) }
            // Sin luz eléctrica, el cielo de la ciudad vuelve a llenarse de estrellas: crucecitas nítidas.
            TemaId.RUINAS -> { drawLine(col, o - Offset(r * 1.4f, 0f), o + Offset(r * 1.4f, 0f), r * 0.4f); drawLine(col, o - Offset(0f, r * 1.4f), o + Offset(0f, r * 1.4f), r * 0.4f); drawCircle(col, r * 0.5f, o) }
            TemaId.NAVIDAD -> copoNieve(o, r * 2.2f, col)
            TemaId.SEDA -> drawPath(Path().apply { moveTo(o.x, o.y - r * 1.6f); lineTo(o.x + r, o.y); lineTo(o.x, o.y + r * 1.6f); lineTo(o.x - r, o.y); close() }, col)
            // Copos de pan de oro (kirihaku), como los que se esparcen sobre los biombos.
            TemaId.EDO -> rotate(((o.x * 7f) % 40f) - 20f, o) { drawRect(col, Offset(o.x - r * 0.8f, o.y - r * 0.8f), Size(r * 1.6f, r * 1.6f)) }
            TemaId.GOTICO -> estrellaPuntas(o, r * 2f, col, 8, 0.5f)
            // Estrellas veladas por la niebla: un halo y un punto.
            TemaId.ARCANO -> { drawCircle(col.copy(alpha = col.alpha * 0.25f), r * 2.4f, o); drawCircle(col, r * 0.6f, o) }
            TemaId.ESPACIO -> drawCircle(listOf(col, Color(0xFF8AB4FF), Color(0xFFFFB86B))[i % 3].copy(alpha = col.alpha), r, o)
            // Plancton con luz propia: un punto y su halo, verde agua, violeta o azul.
            TemaId.ABISMO -> { val luz = listOf(col, Color(0xFF5CF2C6), Color(0xFFE29BFF))[i % 3]; drawCircle(luz.copy(alpha = col.alpha * 0.25f), r * 2.4f, o); drawCircle(luz.copy(alpha = col.alpha), r * 0.8f, o) }
            TemaId.ANDALUS -> estrellaPuntas(o, r * 1.8f, col, 6, 0.55f)
            // Lamat, el signo de Venus: la estrella de cuatro puntas con un punto en cada hueco.
            TemaId.MESO -> { estrellaPuntas(o, r * 1.7f, col, 4, 0.3f); for (k in 0 until 4) { val a = Math.toRadians(45.0 + 90.0 * k); drawCircle(col, r * 0.28f, Offset(o.x + r * 1.1f * cos(a).toFloat(), o.y + r * 1.1f * sin(a).toFloat())) } }
            TemaId.MONGOL -> drawCircle(col, r * 1.2f, o, style = Stroke(0.9f))
            TemaId.RENACIMIENTO -> { val l = r * 1.3f; drawLine(col, o + Offset(-l, -l), o + Offset(l, l), 0.9f); drawLine(col, o + Offset(-l, l), o + Offset(l, -l), 0.9f) }
            // Las estrellas de marear de las cartas: cuatro puntas, la vertical más larga.
            TemaId.PIRATA -> { val l = r * 1.7f; drawLine(col, o + Offset(0f, -l), o + Offset(0f, l), 0.8f); drawLine(col, o + Offset(-l * 0.6f, 0f), o + Offset(l * 0.6f, 0f), 0.8f); drawCircle(col, r * 0.45f, o) }
            TemaId.URSS -> if (i % 9 == 0) estrellaPuntas(o, r * 2.2f, Color(0xFFE53935).copy(alpha = col.alpha), 5, 0.42f) else drawCircle(col, r * 0.8f, o)
            TemaId.REVOLUCION -> drawCircle(listOf(Color(0xFF8FA6E8), col, Color(0xFFE57373))[i % 3].copy(alpha = col.alpha), r, o)
            TemaId.ANTIGUAS -> estrellaPuntas(o, r * 2.2f, col, 4, 0.3f)
            // Puntos de dos en dos, como las parejas de puntos pintados en la roca.
            TemaId.CUEVA -> { drawCircle(col, r * 0.8f, o); drawCircle(col.copy(alpha = col.alpha * 0.5f), r * 0.5f, o + Offset(r * 2.2f, r * 0.6f)) }
        }
    }
}

/**
 * La luna (o lo que alumbra la noche) de cada tema. Con [sola], solo la figura principal, sin lo que la acompaña
 * (para los lomos, que son angostos).
 */
private fun DrawScope.lunaDelTema(t: TemaId, ce: Offset, r: Float, n: NocheTema, sola: Boolean = false) {
    val luna = n.luna
    when (t) {
        // Luna llena con su halo, como sobre el Foro.
        TemaId.ROMANO -> { drawCircle(luna.copy(alpha = 0.18f), r * 1.45f, ce); drawCircle(luna, r * 0.9f, ce); drawCircle(n.cielo.copy(alpha = 0.18f), r * 0.25f, ce + Offset(-r * 0.25f, -r * 0.1f)) }
        // La luna que sale detrás de una cúpula, con su linterna recortada.
        TemaId.URBE -> {
            drawCircle(luna.copy(alpha = 0.16f), r * 1.5f, ce); drawCircle(luna, r * 0.9f, ce)
            if (!sola) {
                val base = ce.y + r * 0.95f
                drawArc(n.cielo, 180f, 180f, true, Offset(ce.x - r * 1.1f, base - r * 0.9f), Size(r * 2.2f, r * 1.8f))
                drawRect(n.cielo, Offset(ce.x - r * 0.12f, base - r * 1.25f), Size(r * 0.24f, r * 0.4f))
            }
        }
        // Una media luna hecha de cuadritos, como en una consola.
        TemaId.CIBER -> {
            val paso = r / 3.5f; var y = -r
            while (y <= r) {
                var x = -r
                while (x <= r) {
                    val dentro = x * x + y * y <= r * r
                    val fuera = (x - r * 0.48f) * (x - r * 0.48f) + (y + r * 0.22f) * (y + r * 0.22f) > r * 0.88f * r * 0.88f
                    if (dentro && fuera) drawRect(luna, Offset(ce.x + x - paso * 0.45f, ce.y + y - paso * 0.45f), Size(paso * 0.9f, paso * 0.9f))
                    x += paso
                }
                y += paso
            }
        }
        // Una luna de neón: el tubo curvo con su halo y, al lado, el letrero vertical de un bar (月, «luna»).
        TemaId.NEON -> {
            val tubo = Path().apply { addArc(Rect(ce.x - r, ce.y - r, ce.x + r, ce.y + r), 110f, 260f) }
            drawPath(tubo, n.luz.copy(alpha = 0.25f), style = Stroke(r * 0.45f, cap = StrokeCap.Round))
            drawPath(tubo, n.luz, style = Stroke(r * 0.14f, cap = StrokeCap.Round))
            if (!sola) {
                val o = Offset(ce.x + r * 1.9f, ce.y - r * 0.9f)
                drawRect(n.luz.copy(alpha = 0.2f), o, Size(r * 0.8f, r * 2.2f), style = Stroke(r * 0.08f))
                repeat(3) { k -> drawLine(n.luz.copy(alpha = 0.8f), o + Offset(r * 0.2f, r * (0.45f + k * 0.6f)), o + Offset(r * 0.6f, r * (0.45f + k * 0.6f)), r * 0.09f) }
            }
        }
        // Selene: la media luna tumbada como una barca, y las Pléyades al lado.
        TemaId.HELENO -> {
            rotate(-90f, ce) { mediaLuna(ce, r, luna) }
            if (!sola) listOf(0f to 0f, 0.8f to -0.3f, 1.4f to 0.2f, 0.6f to 0.6f, -0.5f to 0.5f, 1.9f to -0.4f, 0.2f to -0.9f).forEach { (x, y) ->
                drawCircle(luna, r * 0.1f, ce + Offset(-r * 3f + x * r * 0.5f, r * 0.3f + y * r * 0.5f))
            }
        }
        // La luna de Jonsu: el creciente como una cuna con el disco encima; al lado, el cinturón de Sah (Orión) y Sopdet (Sirio).
        TemaId.EGIPTO -> {
            rotate(-90f, ce) { mediaLuna(ce, r, luna) }
            drawCircle(luna.copy(alpha = luna.alpha * 0.85f), r * 0.42f, ce - Offset(0f, r * 0.62f))
            if (!sola) {
                listOf(-2.6f to 0.95f, -2.2f to 0.8f, -1.8f to 0.65f).forEach { (x, y) -> drawCircle(luna, r * 0.09f, ce + Offset(x * r, y * r)) }
                drawCircle(luna, r * 0.16f, ce + Offset(-3.4f * r, 1.6f * r))
            }
        }
        // Media luna con una estrellita de seis puntas, como en los márgenes iluminados.
        TemaId.MEDIEVAL -> { mediaLuna(ce, r, luna); if (!sola) asterisco(ce + Offset(-r * 1.9f, r * 0.6f), r * 0.45f, luna, 6, 1.4f) }
        // Media luna sobre la torre de vigía, con su antorcha.
        TemaId.TEMPLARIO -> { mediaLuna(ce, r, luna)
            if (!sola) { val b = ce + Offset(-r * 2.2f, r * 0.4f); drawRect(luna.copy(alpha = 0.7f), Offset(b.x - r * 0.3f, b.y), Size(r * 0.6f, r * 1.4f))
                for (k in 0 until 2) drawRect(luna.copy(alpha = 0.7f), Offset(b.x - r * 0.3f + k * r * 0.4f, b.y - r * 0.22f), Size(r * 0.2f, r * 0.22f))
                drawCircle(Color(0xFFF0A040).copy(alpha = 0.8f), r * 0.12f, Offset(b.x + r * 0.42f, b.y + r * 0.2f)) } }
        // Una luna gris cortada por el haz de un reflector.
        TemaId.DISTOPIA -> {
            drawCircle(luna.copy(alpha = 0.75f), r, ce)
            if (!sola) drawPath(Path().apply { moveTo(ce.x - r * 2.2f, ce.y + r * 2.6f); lineTo(ce.x - r * 0.2f, ce.y - r * 1.4f); lineTo(ce.x + r * 0.8f, ce.y - r * 1.2f); close() }, luna.copy(alpha = 0.16f))
        }
        // Luna de fósforo: un círculo hecho de líneas de barrido.
        TemaId.REFUGIO -> {
            drawCircle(n.luz, r, ce, style = Stroke(1f))
            var y = -r + r / 5f
            while (y < r) { val m = sqrt(r * r - y * y); drawLine(n.luz.copy(alpha = 0.8f), Offset(ce.x - m, ce.y + y), Offset(ce.x + m, ce.y + y), 1.2f); y += r / 5f }
        }
        // Media luna de acero: un semicírculo recto, con su anillo.
        TemaId.ORDEN -> { drawArc(luna, 90f, 180f, true, Offset(ce.x - r, ce.y - r), Size(2 * r, 2 * r)); drawCircle(luna.copy(alpha = 0.4f), r * 1.25f, ce, style = Stroke(1.2f)) }
        // En lugar de luna, el reloj de la estación, con la hora del ferrocarril: esfera, doce marcas y las dos agujas.
        TemaId.VAPOR -> {
            drawCircle(luna.copy(alpha = 0.16f), r * 1.5f, ce); drawCircle(luna, r * 0.95f, ce)
            drawCircle(n.cielo.copy(alpha = 0.55f), r * 0.95f, ce, style = Stroke(r * 0.08f))
            for (k in 0 until 12) { val a = k * PI / 6; drawLine(n.cielo.copy(alpha = 0.6f), ce + Offset((r * 0.78f * cos(a)).toFloat(), (r * 0.78f * sin(a)).toFloat()), ce + Offset((r * 0.66f * cos(a)).toFloat(), (r * 0.66f * sin(a)).toFloat()), r * 0.06f) }
            drawLine(n.cielo.copy(alpha = 0.75f), ce, ce + Offset(-r * 0.3f, -r * 0.38f), r * 0.08f)
            drawLine(n.cielo.copy(alpha = 0.75f), ce, ce + Offset(r * 0.55f, -r * 0.1f), r * 0.05f)
            if (!sola) drawRect(n.cielo, Offset(ce.x - r * 0.08f, ce.y - r * 1.5f), Size(r * 0.16f, r * 0.55f))
        }
        // Diagrama de fase: la mitad iluminada y las marcas de los grados.
        TemaId.LABORATORIO -> {
            drawCircle(luna, r, ce, style = Stroke(1.2f))
            drawArc(luna, -90f, 180f, true, Offset(ce.x - r, ce.y - r), Size(2 * r, 2 * r))
            if (!sola) for (i in 0 until 12) { val a = PI * i / 6; drawLine(luna.copy(alpha = 0.6f), ce + polarLibro(r * 1.15f, a), ce + polarLibro(r * 1.35f, a), 1f) }
        }
        // Media luna y el Carro (la Osa Mayor), con que se orientaban los colonos.
        TemaId.COLONOS -> {
            mediaLuna(ce, r, luna)
            if (!sola) {
                val s = r * 0.55f; val o = ce + Offset(-r * 2.8f, r * 0.4f)
                val puntos = listOf(-1.6f to -0.5f, -1.0f to -0.3f, -0.45f to -0.15f, 0f to 0f, 0.1f to 0.6f, 0.9f to 0.65f, 0.95f to 0.05f).map { (x, y) -> o + Offset(x * s, y * s) }
                puntos.zipWithNext().forEach { (a, b) -> drawLine(luna.copy(alpha = 0.35f), a, b, 0.8f) }
                drawLine(luna.copy(alpha = 0.35f), puntos[6], puntos[3], 0.8f)
                puntos.forEach { drawCircle(luna, s * 0.14f, it) }
            }
        }
        // La estrella del Emperador junto a la luna.
        TemaId.NAPOLEON -> { mediaLuna(ce, r * 0.85f, luna); if (!sola) estrellaPuntas(ce + Offset(-r * 2f, r * 0.4f), r * 0.7f, n.luz, 5, 0.42f) }
        // La luna creciente junto a la aguja de un rascacielos escalonado.
        TemaId.DECO -> {
            mediaLuna(ce, r * 0.85f, luna)
            if (!sola) {
                val x = ce.x - r * 2.2f; val b = ce.y + r * 2.4f; val torre = n.luz.copy(alpha = 0.3f)
                drawRect(torre, Offset(x - r * 0.6f, b - r * 1.6f), Size(r * 1.2f, r * 1.6f))
                drawRect(torre, Offset(x - r * 0.4f, b - r * 2.2f), Size(r * 0.8f, r * 0.6f))
                drawRect(torre, Offset(x - r * 0.2f, b - r * 2.7f), Size(r * 0.4f, r * 0.5f))
                drawLine(torre, Offset(x, b - r * 2.7f), Offset(x, b - r * 3.4f), r * 0.08f)
            }
        }
        // Una polilla nocturna cruza la luna llena.
        TemaId.BOTANICO -> {
            drawCircle(luna, r, ce)
            val o = ce + Offset(r * 0.15f, r * 0.15f); val s = r * 0.5f
            drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x - s, o.y - s * 0.7f); lineTo(o.x - s * 0.8f, o.y + s * 0.5f); close() }, n.cielo)
            drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + s, o.y - s * 0.7f); lineTo(o.x + s * 0.8f, o.y + s * 0.5f); close() }, n.cielo)
            drawLine(n.cielo, o + Offset(0f, -s * 0.3f), o + Offset(0f, s * 0.5f), s * 0.18f)
            drawLine(n.cielo, o + Offset(0f, -s * 0.3f), o + Offset(-s * 0.35f, -s * 0.8f), 1f); drawLine(n.cielo, o + Offset(0f, -s * 0.3f), o + Offset(s * 0.35f, -s * 0.8f), 1f)
        }
        TemaId.RUINAS -> {
            // La luna llena detrás de la punta de una torre rota, con su antena doblada.
            drawCircle(luna, r, ce)
            val b = ce.y + r * 1.1f
            drawPath(Path().apply { moveTo(ce.x - r * 0.9f, b); lineTo(ce.x - r * 0.9f, ce.y + r * 0.1f); lineTo(ce.x - r * 0.6f, ce.y - r * 0.15f); lineTo(ce.x - r * 0.45f, ce.y + r * 0.2f); lineTo(ce.x - r * 0.2f, ce.y + r * 0.05f); lineTo(ce.x - r * 0.1f, b); close() }, n.cielo)
            drawLine(n.cielo, Offset(ce.x - r * 0.6f, ce.y - r * 0.15f), Offset(ce.x - r * 0.55f, ce.y - r * 0.7f), 1.5f)
            drawLine(n.cielo, Offset(ce.x - r * 0.55f, ce.y - r * 0.7f), Offset(ce.x - r * 0.3f, ce.y - r * 0.85f), 1.5f)
        }
        // La estrella de Belén, con su cola larga, y una luna pequeña.
        TemaId.NAVIDAD -> {
            if (!sola) mediaLuna(ce + Offset(r * 0.6f, -r * 0.4f), r * 0.55f, luna)
            val o = if (sola) ce else ce + Offset(-r * 1.4f, r * 0.1f)
            drawCircle(n.luz.copy(alpha = 0.18f), r * 0.7f, o)
            val k = r * 0.14f
            drawPath(Path().apply {
                moveTo(o.x, o.y - r * 0.8f); lineTo(o.x + k, o.y - k); lineTo(o.x + r * 0.6f, o.y); lineTo(o.x + k, o.y + k)
                lineTo(o.x, o.y + r * 1.9f); lineTo(o.x - k, o.y + k); lineTo(o.x - r * 0.6f, o.y); lineTo(o.x - k, o.y - k); close()
            }, n.luz)
        }
        // Luna llena de otoño tras dos nubes alargadas.
        TemaId.SEDA -> {
            drawCircle(luna, r, ce)
            listOf(-0.35f, 0.3f).forEachIndexed { i, f -> drawRoundRect(n.cielo.copy(alpha = 0.85f), Offset(ce.x - r * (1.3f - i * 0.5f), ce.y + r * f), Size(r * 1.8f, r * 0.32f), CornerRadius(r * 0.16f)) }
        }
        // La luna llena con tres gansos salvajes (karigane) que la cruzan, un motivo de otoño de los grabados.
        TemaId.EDO -> {
            drawCircle(luna, r, ce)
            for (i in 0 until 3) {
                val g = Offset(ce.x - r * 0.55f + i * r * 0.5f, ce.y - r * 0.15f + i * r * 0.2f); val a = r * (0.2f - i * 0.03f)
                drawPath(Path().apply { moveTo(g.x - a, g.y - a * 0.35f); quadraticTo(g.x - a * 0.4f, g.y - a * 0.5f, g.x, g.y); quadraticTo(g.x + a * 0.4f, g.y - a * 0.5f, g.x + a, g.y - a * 0.35f) },
                    n.cielo, style = Stroke(r * 0.07f))
            }
        }
        // Estrella de oro de ocho puntas, como en la bóveda de la capilla.
        TemaId.GOTICO -> { estrellaPuntas(ce, r, n.luz, 8, 0.5f); drawCircle(n.cielo, r * 0.18f, ce) }
        // La luna en la fase de esta noche (calculada), con dos bandas de niebla que la cruzan.
        TemaId.ARCANO -> {
            val k = (Temas.edadLuna(java.time.LocalDate.now()) / 29.530588853 * 2).toFloat()
            val dx = if (k <= 1f) -2f * r * k else 2f * r * (2f - k)
            drawCircle(luna.copy(alpha = 0.14f), r * 1.5f, ce)
            clipPath(Path().apply { addOval(Rect(ce, r * 0.9f)) }) { drawCircle(luna, r * 0.9f, ce); drawCircle(n.cielo, r * 0.92f, ce + Offset(dx, 0f)) }
            listOf(0.15f, 0.45f).forEach { f -> drawRect(n.cielo.copy(alpha = 0.55f), Offset(ce.x - r * 1.6f, ce.y + r * f), Size(r * 3.2f, r * 0.14f)) }
        }
        // Un planeta con su anillo.
        TemaId.ESPACIO -> { drawCircle(luna, r * 0.75f, ce); rotate(-20f, ce) { drawOval(n.luz, Offset(ce.x - r * 1.5f, ce.y - r * 0.38f), Size(r * 3f, r * 0.76f), style = Stroke(1.6f)) } }
        // La linterna del rape: la luz al final de su varilla curva, con su halo.
        TemaId.ABISMO -> {
            drawCircle(n.luz.copy(alpha = 0.2f), r * 1.5f, ce)
            drawPath(Path().apply { moveTo(ce.x - r * 1.6f, ce.y + r * 1.8f); quadraticTo(ce.x - r * 1.4f, ce.y - r * 0.6f, ce.x - r * 0.4f, ce.y - r * 0.2f) }, n.luz.copy(alpha = 0.7f), style = Stroke(r * 0.12f))
            drawCircle(luna, r * 0.55f, ce); drawCircle(Color.White.copy(alpha = 0.7f), r * 0.18f, ce - Offset(r * 0.15f, r * 0.15f))
        }
        // Media luna y la estrella de ocho puntas (dos cuadrados girados).
        TemaId.ANDALUS -> {
            mediaLuna(ce, r, luna)
            if (!sola) {
                val e = ce + Offset(-r * 1.8f, r * 0.5f); val l = r * 0.32f
                drawRect(n.luz, Offset(e.x - l, e.y - l), Size(2 * l, 2 * l))
                rotate(45f, e) { drawRect(n.luz, Offset(e.x - l, e.y - l), Size(2 * l, 2 * l)) }
            }
        }
        // La luna y su conejo, el que lleva en brazos la diosa lunar en las vasijas pintadas.
        TemaId.MESO -> {
            mediaLuna(ce, r, luna)
            if (!sola) {
                val e = ce + Offset(-r * 1.9f, r * 0.55f); val l = r * 0.3f
                drawOval(n.luz, Offset(e.x - l, e.y - l * 0.6f), Size(2 * l, l * 1.2f)); drawCircle(n.luz, l * 0.45f, e + Offset(l * 0.95f, -l * 0.5f))
                drawOval(n.luz, Offset(e.x + l * 0.8f, e.y - l * 1.8f), Size(l * 0.28f, l * 1.1f)); drawOval(n.luz, Offset(e.x + l * 1.15f, e.y - l * 1.7f), Size(l * 0.28f, l * 1f))
            }
        }
        // El sol y la luna del soyombo: la media luna boca arriba con el disco encima.
        TemaId.MONGOL -> {
            rotate(-90f, ce + Offset(0f, r * 0.3f)) { mediaLuna(ce + Offset(0f, r * 0.3f), r * 0.8f, luna) }
            drawCircle(luna, r * 0.38f, ce + Offset(0f, -r * 0.8f))
            if (!sola) listOf(-0.3f, 0f, 0.3f).forEach { dx -> drawCircle(n.luz, r * 0.1f, ce + Offset(dx * r, -r * (1.45f + if (dx == 0f) 0.15f else 0f))) }
        }
        // La luz cenicienta que dibujó Leonardo: el creciente iluminado y el resto rayado a pluma.
        TemaId.RENACIMIENTO -> {
            drawCircle(luna.copy(alpha = 0.5f), r, ce, style = Stroke(1f))
            mediaLuna(ce, r, luna)
            var x = r * 0.05f
            while (x < r) { val m = sqrt(r * r - x * x) * 0.92f; drawLine(luna.copy(alpha = 0.35f), Offset(ce.x + x, ce.y - m), Offset(ce.x + x - r * 0.15f, ce.y + m), 0.8f); x += r / 5f }
        }
        // La luna sobre el mar, con su reflejo roto en las olas.
        TemaId.PIRATA -> {
            mediaLuna(ce, r, luna)
            for (k in 1..6) {
                val y = ce.y + r * (1.5f + k * 0.32f); val a = r * (0.7f - k * 0.08f)
                drawLine(luna.copy(alpha = 0.35f * (1f - k / 7f)), Offset(ce.x - a, y), Offset(ce.x + a * 0.8f, y), 1.2f)
            }
        }
        // El Sputnik cruza junto a la luna.
        TemaId.URSS -> {
            mediaLuna(ce + Offset(r * 0.4f, 0f), r * 0.8f, luna)
            if (!sola) {
                val o = ce + Offset(-r * 1.8f, r * 0.6f); val s = r * 0.28f
                drawCircle(n.luz, s, o)
                listOf(20.0, 34.0, -20.0, -34.0).forEach { a -> drawLine(n.luz, o, o + polarLibro(s * 4f, Math.toRadians(a)), 0.9f) }
            }
        }
        // Media luna y la escarapela.
        TemaId.REVOLUCION -> {
            mediaLuna(ce, r, luna)
            if (!sola) { val e = ce + Offset(-r * 1.8f, r * 0.5f); drawCircle(Color(0xFFB22234), r * 0.42f, e); drawCircle(Color(0xFFF4F1E8), r * 0.28f, e); drawCircle(Color(0xFF1F3A6E), r * 0.14f, e) }
        }
        // La luna con su anillo de runas inventadas.
        TemaId.ANTIGUAS -> {
            drawCircle(luna, r * 0.8f, ce)
            if (!sola) for (i in 0 until 16) { val a = 2 * PI * i / 16; val p0 = ce + polarLibro(r * 1.1f, a); drawLine(luna.copy(alpha = 0.7f), p0, p0 + polarLibro(r * 0.25f, a + if (i % 3 == 0) 0.6 else -0.4), 1.1f) }
        }
        // La luna llena y, alrededor, una hilera de muescas en zigzag: las noches de una luna contadas en un hueso.
        TemaId.CUEVA -> {
            drawCircle(luna.copy(alpha = 0.16f), r * 1.3f, ce); drawCircle(luna, r * 0.8f, ce)
            if (!sola) for (i in 0 until 15) { val a = -PI / 2 + PI * 1.6 * i / 14 - PI * 0.3; val p0 = ce + polarLibro(r * (if (i % 2 == 0) 1.2f else 1.38f), a); drawCircle(luna.copy(alpha = 0.75f), 1.2f, p0) }
        }
    }
}

/** Un cielo de noche propio de cada tema (su color, su luna y sus estrellas), para los estantes de los sueños. */
@Composable
fun CieloNoche(modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, content: @Composable () -> Unit) {
    val m = materiaNoche(tema); val n = nocheDe(tema)
    val alto = lerp(n.cielo, n.luz, 0.16f)
    val forma = RoundedCornerShape(10.dp)
    val e = MaterialTheme.colorScheme.copy(onSurfaceVariant = Color(0xFFB8BEDA), onSurface = Color(0xFFE8E6F2), onBackground = Color(0xFFE8E6F2), primary = m.acento)
    Box(modifier.fillMaxWidth().clip(forma).background(Brush.verticalGradient(listOf(n.cielo, alto))).border(1.dp, n.luz.copy(alpha = 0.35f), forma)) {
        Canvas(Modifier.matchParentSize()) {
            estrellasDelTema(tema, tema.ordinal * 31 + 5, (size.width * size.height / 9000f).toInt().coerceIn(20, 140), n.luna)
            lunaDelTema(tema, Offset(size.width - 24.dp.toPx(), 22.dp.toPx()), 10.dp.toPx(), n)
        }
        MaterialTheme(colorScheme = e, typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
            Box(Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) { content() }
        }
    }
}

/** Lo que dice el botón de guardar en cada tema. */
fun textoGuardar(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "SIGNARE"
    TemaId.URBE -> "SEGNA"
    TemaId.CIBER -> "git commit"
    TemaId.NEON -> "cerrar el ticket"
    TemaId.HELENO -> "γράφε · anotar"
    TemaId.EGIPTO -> "sellar el papiro"
    TemaId.MEDIEVAL -> "rubricar"
    TemaId.TEMPLARIO -> "inscribir en el rol"
    TemaId.DISTOPIA -> "ARCHIVAR"
    TemaId.REFUGIO -> "GUARDAR"
    TemaId.ORDEN -> "REGISTRAR"
    TemaId.VAPOR -> "Asentar en el registro"
    TemaId.LABORATORIO -> "guardar registro"
    TemaId.COLONOS -> "asentar"
    TemaId.NAPOLEON -> "SELLAR"
    TemaId.DECO -> "guardar en el carnet"
    TemaId.BOTANICO -> "prensar la hoja"
    TemaId.RUINAS -> "apuntarlo en el cuaderno"
    TemaId.NAVIDAD -> "guardar carta"
    TemaId.SEDA -> "sellar el rollo"
    TemaId.EDO -> "poner el sello"
    TemaId.URSS -> "ENTREGAR"
    TemaId.GOTICO -> "sellar el folio"
    TemaId.ARCANO -> "archivar el legajo"
    TemaId.ESPACIO -> "TRANSMITIR"
    TemaId.ABISMO -> "ANOTAR EN LA BITÁCORA"
    TemaId.ANDALUS -> "guardar · حفظ"
    TemaId.MESO -> "PINTAR EN EL CÓDICE"
    TemaId.MONGOL -> "ТАМГА · sellar"
    TemaId.RENACIMIENTO -> "annotare"
    TemaId.PIRATA -> "ASENTAR LA SINGLADURA"
    TemaId.REVOLUCION -> "FIRMAR"
    TemaId.ANTIGUAS -> "ASENTAR EN LA CRÓNICA"
    TemaId.CUEVA -> "Pintar en la pared"
}

/** Lo que cierra cada página al leerla. */
fun colofon(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "· FINIS ·"
    TemaId.URBE -> "— FINE —"
    TemaId.CIBER -> "-- EOF --"
    TemaId.NEON -> "— se apaga el letrero —"
    TemaId.HELENO -> "ΤΕΛΟΣ"
    TemaId.EGIPTO -> "llegó de principio a fin, como estaba escrito"
    TemaId.MEDIEVAL -> "Aquí se acaba el folio."
    TemaId.TEMPLARIO -> "Non nobis, Domine: aquí acaba la jornada."
    TemaId.DISTOPIA -> "FIN DE LA DECLARACIÓN"
    TemaId.REFUGIO -> "FIN DE LA ENTRADA"
    TemaId.ORDEN -> "FIN DEL PARTE"
    TemaId.VAPOR -> "— fin de la memoria descriptiva —"
    TemaId.LABORATORIO -> "fin del registro"
    TemaId.COLONOS -> "Asentado."
    TemaId.NAPOLEON -> "Dado en el cuartel general."
    TemaId.DECO -> "Fin de la función."
    TemaId.BOTANICO -> "Ex herbario."
    TemaId.RUINAS -> "Escrito tras el silencio."
    TemaId.NAVIDAD -> "Con cariño, junto al fuego."
    TemaId.SEDA -> "Fin del rollo."
    TemaId.EDO -> "Aquí se cose el cuaderno."
    TemaId.URSS -> "INFORME ENTREGADO"
    TemaId.GOTICO -> "Explicit. Deo gratias."
    TemaId.ARCANO -> "Fin del legajo. Que nadie más lo lea."
    TemaId.ESPACIO -> "FIN DE TRANSMISIÓN"
    TemaId.ABISMO -> "FIN DE LA INMERSIÓN"
    TemaId.ANDALUS -> "تمّ · Se acabó."
    TemaId.MESO -> "Así quedó pintado."
    TemaId.MONGOL -> "Ингэж бичив · Así quedó escrito."
    TemaId.RENACIMIENTO -> "Finito."
    TemaId.PIRATA -> "Fin de la singladura."
    TemaId.REVOLUCION -> "Lu et approuvé."
    TemaId.ANTIGUAS -> "Así quedó escrito en la crónica."
    TemaId.CUEVA -> "Aquí termina la pintura; sigue la roca."
}

/** Lo que invita a abrir el objeto cerrado cuando aún no tiene nada escrito. */
private fun pistaDia(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "toca para desenrollar el volumen"
    TemaId.URBE -> "toca para abrir el cuaderno (quita el elástico)"
    TemaId.CIBER -> "> toca para abrir el editor_"
    TemaId.NEON -> "toca y cuenta cómo estuvo la calle"
    TemaId.HELENO -> "toca para desplegar el rollo"
    TemaId.EGIPTO -> "toca para desenrollar el papiro"
    TemaId.MEDIEVAL -> "toca para abrir el códice"
    TemaId.TEMPLARIO -> "toca para abrir la regla"
    TemaId.DISTOPIA -> "TOCA PARA ABRIR EL EXPEDIENTE"
    TemaId.REFUGIO -> "> TOCA PARA ABRIR LA BITÁCORA_"
    TemaId.ORDEN -> "toca para abrir el libro de partes"
    TemaId.VAPOR -> "toca para abrir el registro de patentes"
    TemaId.LABORATORIO -> "toca para abrir el cuaderno de laboratorio"
    TemaId.COLONOS -> "toca para abrir el libro de registro"
    TemaId.NAPOLEON -> "toca para desplegar el boletín"
    TemaId.DECO -> "toca para abrir el pasaje del día"
    TemaId.BOTANICO -> "toca para abrir la libreta de campo"
    TemaId.RUINAS -> "toca para abrir el cuaderno del superviviente"
    TemaId.NAVIDAD -> "toca para abrir la carta"
    TemaId.SEDA -> "toca para desenrollar la seda"
    TemaId.EDO -> "toca para abrir el cuaderno cosido"
    TemaId.GOTICO -> "toca para abrir el libro de horas"
    TemaId.ARCANO -> "toca para soltar el cordel y abrir el diario"
    TemaId.ESPACIO -> "toca para abrir la bitácora de vuelo"
    TemaId.ABISMO -> "toca para abrir la bitácora del batiscafo"
    TemaId.ANDALUS -> "toca para soltar la solapa y abrir el cuaderno"
    TemaId.MESO -> "toca para desatar el cordel y desplegar el códice"
    TemaId.MONGOL -> "toca para desenrollar el rollo de la estepa"
    TemaId.RENACIMIENTO -> "toca para desatar el cuaderno de notas"
    TemaId.PIRATA -> "toca para desatar el cabo de la bitácora"
    TemaId.URSS -> "toca para abrir la libreta de informes"
    TemaId.REVOLUCION -> "toca para abrir el cuaderno de quejas"
    TemaId.ANTIGUAS -> "toca para abrir la crónica"
    TemaId.CUEVA -> "toca para dar vuelta la laja"
}

/** Lo mismo, de noche: invita a contar el sueño. */
private fun pistaNoche(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "toca para contar el somnium"
    TemaId.URBE -> "toca para contar lo que soñaste"
    TemaId.CIBER -> "> toca para registrar el sueño_"
    TemaId.NEON -> "toca y cuenta lo que soñaste entre el ruido"
    TemaId.HELENO -> "toca para contar lo que trajo Hipnos"
    TemaId.EGIPTO -> "toca para contar lo que trajo la noche"
    TemaId.MEDIEVAL -> "toca para asentar la visión de la noche"
    TemaId.TEMPLARIO -> "toca para inscribir lo soñado en la vigilia"
    TemaId.DISTOPIA -> "TOCA PARA DECLARAR LO SOÑADO"
    TemaId.REFUGIO -> "> TOCA PARA REGISTRAR EL SUEÑO_"
    TemaId.ORDEN -> "toca para dar el parte nocturno"
    TemaId.VAPOR -> "toca para anotar los sueños del turno de noche"
    TemaId.LABORATORIO -> "toca para registrar la observación nocturna"
    TemaId.COLONOS -> "toca para anotar lo soñado"
    TemaId.NAPOLEON -> "toca para dictar el sueño"
    TemaId.DECO -> "toca para contar lo que soñaste en el camarote"
    TemaId.BOTANICO -> "toca para anotar la flora de la noche"
    TemaId.RUINAS -> "toca para apuntar lo que soñaste en la azotea"
    TemaId.NAVIDAD -> "toca para contar lo que soñaste junto al fuego"
    TemaId.SEDA -> "toca para escribir el sueño de la caravana"
    TemaId.EDO -> "toca para escribir lo que soñaste en la posada"
    TemaId.GOTICO -> "toca para escribir la visión nocturna"
    TemaId.ARCANO -> "toca para anotar lo que soñaste antes de que se borre"
    TemaId.ESPACIO -> "toca para transmitir el sueño"
    TemaId.ABISMO -> "toca para anotar el sueño de la litera"
    TemaId.ANDALUS -> "toca para escribir el sueño del patio"
    TemaId.MESO -> "toca para pintar el sueño del petate"
    TemaId.MONGOL -> "toca para contar el sueño de la yurta"
    TemaId.RENACIMIENTO -> "toca para dibujar el sogno con palabras"
    TemaId.PIRATA -> "toca para contar lo que soñaste en el coy"
    TemaId.URSS -> "toca para entregar el informe del sueño"
    TemaId.REVOLUCION -> "toca para levantar acta del sueño"
    TemaId.ANTIGUAS -> "toca para contar las visiones de la noche"
    TemaId.CUEVA -> "toca para contar lo que viste con los ojos cerrados"
}

/** Lo que dice la página de un día sin nada escrito. */
private fun textoVacio(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "Nihil scriptum: este día quedó en blanco."
    TemaId.URBE -> "Niente: la página de ese día quedó en blanco."
    TemaId.CIBER -> "// archivo vacío: nada escrito este día"
    TemaId.NEON -> "Letrero apagado: este día no se escribió nada."
    TemaId.HELENO -> "Ἄγραφος: nada escrito este día."
    TemaId.EGIPTO -> "Papiro en blanco: nada escrito este día."
    TemaId.MEDIEVAL -> "Folio en blanco: nada se asentó este día."
    TemaId.TEMPLARIO -> "Guardia sin parte: nada se inscribió esta jornada."
    TemaId.DISTOPIA -> "SIN DECLARACIÓN REGISTRADA PARA ESTA JORNADA."
    TemaId.REFUGIO -> "SIN ENTRADAS EN LA BITÁCORA PARA ESTE DÍA."
    TemaId.ORDEN -> "Sin parte para esta jornada."
    TemaId.VAPOR -> "Ninguna patente registrada en esta jornada."
    TemaId.LABORATORIO -> "Sin datos registrados en esta fecha."
    TemaId.COLONOS -> "Nada asentado en el registro este día."
    TemaId.NAPOLEON -> "Aucun bulletin: no hubo despacho este día."
    TemaId.DECO -> "Butaca vacía: no hubo función este día."
    TemaId.BOTANICO -> "Lámina vacía: no se anotó nada este día."
    TemaId.RUINAS -> "Hoja en blanco: nadie apuntó nada este día."
    TemaId.NAVIDAD -> "Nadie escribió junto al fuego este día."
    TemaId.SEDA -> "El rollo de este día quedó sin tinta."
    TemaId.EDO -> "La hoja de washi de este día quedó en blanco."
    TemaId.GOTICO -> "Folium vacuum: nada escrito este día."
    TemaId.ARCANO -> "Folio en blanco: ese día no quedó nada anotado."
    TemaId.ESPACIO -> "SIN TRANSMISIONES EN ESTA FECHA."
    TemaId.ABISMO -> "SIN INMERSIÓN ESTE DÍA: EL SONAR NO DEVUELVE ECO."
    TemaId.ANDALUS -> "Página en blanco: nada escrito este día."
    TemaId.MESO -> "Página sin pintar: nada en este día de la cuenta."
    TemaId.MONGOL -> "Хоосон · nada escrito este día."
    TemaId.RENACIMIENTO -> "Carta bianca: nada escrito este día."
    TemaId.PIRATA -> "Mar en calma: nada asentado en esta singladura."
    TemaId.URSS -> "ЗАПИСЕЙ НЕТ · sin informe este día."
    TemaId.REVOLUCION -> "Rien à signaler: nada escrito este día."
    TemaId.ANTIGUAS -> "Las crónicas callan sobre este día."
    TemaId.CUEVA -> "Pared lisa: este sol no dejó pintura."
}

/** Lo que dice la página de una noche sin sueño anotado. */
private fun textoSinSueno(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "Nullum somnium: no anotaste ningún sueño esa noche."
    TemaId.URBE -> "Nessun sogno: esa noche no anotaste nada."
    TemaId.CIBER -> "// sin sueños registrados esa noche"
    TemaId.NEON -> "Esa noche no quedó ningún sueño en el ticket."
    TemaId.HELENO -> "Hipnos no dejó nada escrito esa noche."
    TemaId.EGIPTO -> "La noche no dejó ningún signo en el papiro."
    TemaId.MEDIEVAL -> "Ninguna visión se asentó esa noche."
    TemaId.TEMPLARIO -> "Ningún sueño se inscribió en esa vigilia."
    TemaId.DISTOPIA -> "SIN ACTIVIDAD ONÍRICA DECLARADA."
    TemaId.REFUGIO -> "SIN REGISTRO ONÍRICO ESA NOCHE."
    TemaId.ORDEN -> "Sin parte nocturno."
    TemaId.VAPOR -> "Esa noche la caldera durmió sin sueños."
    TemaId.LABORATORIO -> "Sin registro de sueño esa noche."
    TemaId.COLONOS -> "No quedó nada soñado esa noche."
    TemaId.NAPOLEON -> "Pas de songe: no anotaste ningún sueño."
    TemaId.DECO -> "Esa noche el camarote no guardó ningún sueño."
    TemaId.BOTANICO -> "La flora de esa noche quedó sin describir."
    TemaId.RUINAS -> "Esa noche no quedó ningún sueño apuntado."
    TemaId.NAVIDAD -> "No anotaste ningún sueño junto al fuego."
    TemaId.SEDA -> "La caravana durmió sin sueños anotados."
    TemaId.EDO -> "En la posada no se anotó ningún sueño."
    TemaId.GOTICO -> "Nulla visio esa noche."
    TemaId.ARCANO -> "Esa noche no se anotó ningún sueño. Quizá fue mejor así."
    TemaId.ESPACIO -> "SIN TRANSMISIÓN NOCTURNA."
    TemaId.ABISMO -> "NINGÚN SUEÑO SUBIÓ A LA SUPERFICIE ESA NOCHE."
    TemaId.ANDALUS -> "Ningún sueño quedó escrito esa noche."
    TemaId.MESO -> "Ningún sueño quedó pintado esa noche."
    TemaId.MONGOL -> "Зүүдгүй · no anotaste ningún sueño esa noche."
    TemaId.RENACIMIENTO -> "Nessun sogno: no anotaste nada esa noche."
    TemaId.PIRATA -> "Noche sin sueños en el coy: no anotaste nada."
    TemaId.URSS -> "СНОВ НЕТ · sin informe de sueño."
    TemaId.REVOLUCION -> "Nuit sans rêve: no anotaste ningún sueño."
    TemaId.ANTIGUAS -> "La compañía no recordó visiones esa noche."
    TemaId.CUEVA -> "Esa noche el fuego no trajo visiones."
}

/** Cómo se anuncian «lo mejor» y «lo peor» del día al pie de la página. */
private fun mejorPeor(t: TemaId): Pair<String, String> = when (t) {
    TemaId.ROMANO -> "Optimum — " to "Pessimum — "
    TemaId.URBE -> "Il bello — " to "Il brutto — "
    TemaId.CIBER -> "// mejor: " to "// peor: "
    TemaId.NEON -> "▲ lo mejor: " to "▼ lo peor: "
    TemaId.HELENO -> "Τὸ ἄριστον — " to "Τὸ χείριστον — "
    TemaId.EGIPTO -> "Lo justo — " to "Lo torcido — "
    TemaId.MEDIEVAL -> "Lo bueno del día — " to "Lo amargo del día — "
    TemaId.TEMPLARIO -> "Lo ganado en la jornada — " to "Lo perdido en la jornada — "
    TemaId.DISTOPIA -> "MÉRITO — " to "FALTA — "
    TemaId.REFUGIO -> "LO MEJOR > " to "LO PEOR > "
    TemaId.ORDEN -> "Acierto — " to "Baja — "
    TemaId.VAPOR -> "A todo vapor — " to "Avería — "
    TemaId.LABORATORIO -> "Resultado positivo — " to "Resultado negativo — "
    TemaId.COLONOS -> "Lo que salió bien — " to "Lo que salió mal — "
    TemaId.NAPOLEON -> "Victoire — " to "Revers — "
    TemaId.DECO -> "Ovación — " to "Abucheo — "
    TemaId.BOTANICO -> "Hallazgo — " to "Plaga — "
    TemaId.RUINAS -> "Lo que encontré — " to "Lo que perdí — "
    TemaId.NAVIDAD -> "Lo más bonito — " to "Lo más frío — "
    TemaId.SEDA -> "Lo más dulce — " to "Lo más amargo — "
    TemaId.EDO -> "El cerezo — " to "El tifón — "
    TemaId.GOTICO -> "Gaudium — " to "Dolor — "
    TemaId.ARCANO -> "Lo claro — " to "Lo turbio — "
    TemaId.ESPACIO -> "ÉXITO — " to "FALLO — "
    TemaId.ABISMO -> "AGUAS CLARAS — " to "MAR DE FONDO — "
    TemaId.ANDALUS -> "Lo mejor · خير — " to "Lo peor · شرّ — "
    TemaId.MESO -> "Día propicio — " to "Día nefasto — "
    TemaId.MONGOL -> "Сайн · lo bueno — " to "Муу · lo malo — "
    TemaId.RENACIMIENTO -> "Il meglio — " to "Il peggio — "
    TemaId.PIRATA -> "Viento a favor — " to "Mar gruesa — "
    TemaId.URSS -> "ДОСТИЖЕНИЕ · logro — " to "НЕДОСТАТОК · falla — "
    TemaId.REVOLUCION -> "Bonheur — " to "Doléance — "
    TemaId.ANTIGUAS -> "La hazaña — " to "El tropiezo — "
    TemaId.CUEVA -> "La mejor presa — " to "La que se escapó — "
}

/** La invitación a escribir en la página en blanco del día. */
private fun pistaEscribir(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "Quid hodie? ¿Cómo fue el día?"
    TemaId.URBE -> "Com'è andata oggi? ¿Cómo fue el día?"
    TemaId.CIBER -> "// ¿cómo fue el día?"
    TemaId.NEON -> "¿Qué pasó hoy en la calle?"
    TemaId.HELENO -> "Τί ἐγένετο; ¿Qué pasó hoy?"
    TemaId.EGIPTO -> "Moja el cálamo: ¿qué pasó hoy?"
    TemaId.MEDIEVAL -> "Asiéntese aquí lo acontecido en este día…"
    TemaId.TEMPLARIO -> "Inscribe, hermano, lo que pasó en esta guardia…"
    TemaId.DISTOPIA -> "DECLARE LAS ACTIVIDADES DE LA JORNADA."
    TemaId.REFUGIO -> "> REGISTRE LA ENTRADA DEL DÍA_"
    TemaId.ORDEN -> "Redacte el parte de la jornada."
    TemaId.VAPOR -> "Memoria descriptiva de la jornada:"
    TemaId.LABORATORIO -> "Observaciones del día:"
    TemaId.COLONOS -> "¿Qué pasó hoy en el pueblo?"
    TemaId.NAPOLEON -> "Dictez: ¿qué noticias trae el día?"
    TemaId.DECO -> "¿Qué pasó hoy a bordo?"
    TemaId.BOTANICO -> "Observaciones de campo: ¿qué viste hoy?"
    TemaId.RUINAS -> "¿Qué encontraste hoy entre las ruinas?"
    TemaId.NAVIDAD -> "Querido diario: hoy junto al fuego…"
    TemaId.SEDA -> "¿Por dónde pasó hoy la caravana?"
    TemaId.EDO -> "¿Por qué posta pasaste hoy?"
    TemaId.GOTICO -> "Scribe: ¿qué trajeron las horas de hoy?"
    TemaId.ARCANO -> "Anota lo que viste hoy, aunque no tenga explicación…"
    TemaId.ESPACIO -> "BITÁCORA: ¿cómo fue la jornada a bordo?"
    TemaId.ABISMO -> "BITÁCORA: ¿qué pasó hoy por el ojo de buey?"
    TemaId.ANDALUS -> "Escribe lo que trajo el día…"
    TemaId.MESO -> "Pinta lo que trajo este k'in…"
    TemaId.MONGOL -> "Өнөөдөр юу болсон бэ? ¿Qué pasó hoy en la estepa?"
    TemaId.RENACIMIENTO -> "Annota: ¿qué viste y pensaste hoy?"
    TemaId.PIRATA -> "Asienta la singladura: rumbo, viento y lo que pasó a bordo."
    TemaId.URSS -> "ОТЧЁТ: ¿qué se cumplió hoy?"
    TemaId.REVOLUCION -> "Escribe tus quejas y tus esperanzas del día."
    TemaId.ANTIGUAS -> "¿Qué caminos recorrió hoy la compañía?"
    TemaId.CUEVA -> "¿Qué pasó este sol? Píntalo con palabras."
}

/** La invitación a escribir el sueño. */
private fun pistaSonar(t: TemaId): String = when (t) {
    TemaId.ROMANO -> "Quid somniavisti? ¿Qué soñaste?"
    TemaId.URBE -> "Cos'hai sognato? ¿Qué soñaste?"
    TemaId.CIBER -> "// ¿qué soñaste?"
    TemaId.NEON -> "¿Qué soñaste con la ciudad zumbando afuera?"
    TemaId.HELENO -> "¿Qué te mostró Hipnos esta noche?"
    TemaId.EGIPTO -> "¿Qué te mostró la noche? Anótalo como en un libro de sueños."
    TemaId.MEDIEVAL -> "Asiéntese la visión de la noche…"
    TemaId.TEMPLARIO -> "Inscribe lo que soñaste entre vigilia y vigilia…"
    TemaId.DISTOPIA -> "DECLARE LA ACTIVIDAD ONÍRICA."
    TemaId.REFUGIO -> "> REGISTRE EL SUEÑO_"
    TemaId.ORDEN -> "Parte nocturno: ¿qué soñó?"
    TemaId.VAPOR -> "Lo que soñaste junto a la caldera…"
    TemaId.LABORATORIO -> "Registro onírico: fragmentos, imágenes, emociones."
    TemaId.COLONOS -> "¿Qué soñaste anoche?"
    TemaId.NAPOLEON -> "Racontez le songe: ¿qué soñaste?"
    TemaId.DECO -> "¿Qué soñaste en el camarote?"
    TemaId.BOTANICO -> "¿Qué flora creció en el sueño?"
    TemaId.RUINAS -> "¿Qué soñaste mientras crecía la hiedra?"
    TemaId.NAVIDAD -> "¿Qué soñaste junto al fuego?"
    TemaId.SEDA -> "¿Qué soñaste bajo la luna de la caravana?"
    TemaId.EDO -> "¿Qué soñaste bajo el farolillo de la posada?"
    TemaId.GOTICO -> "Visio nocturna: ¿qué viste en sueños?"
    TemaId.ARCANO -> "¿Qué viste en el sueño? Anótalo antes de que se borre."
    TemaId.ESPACIO -> "TRANSMISIÓN NOCTURNA: ¿qué soñaste?"
    TemaId.ABISMO -> "LITERA: ¿qué soñaste allá abajo?"
    TemaId.ANDALUS -> "¿Qué soñaste? Aunque sean fragmentos."
    TemaId.MESO -> "¿Qué te mostró la noche? Aunque sean pedazos."
    TemaId.MONGOL -> "Ямар зүүд зүүдлэв? ¿Qué soñaste?"
    TemaId.RENACIMIENTO -> "Il sogno: ¿qué viste dormido?"
    TemaId.PIRATA -> "¿Qué soñaste mecido en el coy?"
    TemaId.URSS -> "СОН: ¿qué soñaste? Informe breve."
    TemaId.REVOLUCION -> "Le rêve: ¿qué soñaste?"
    TemaId.ANTIGUAS -> "¿Qué visiones tuvo la compañía esta noche?"
    TemaId.CUEVA -> "¿Qué viste con los ojos cerrados, junto al fuego?"
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

/**
 * El diario de un día como objeto del tema, cerrado: la tapa de su biblioteca ([io.github.ruddy2003.vestigium.ui.biblioteca.DisenoBiblioteca.tapa]),
 * con la fecha y la cuenta de palabras dibujadas dentro y el extracto de lo escrito en su caja. Al tocarlo se abre (la
 * tapa gira, el rollo se desenrolla…) y después pasa a pantalla completa.
 */
@Composable
fun ObjetoDiario(fecha: String, texto: String, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, noche: Boolean = false, onClick: () -> Unit) {
    val sentir = rememberSentir()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val abre = remember { androidx.compose.animation.core.Animatable(0f) }
    val animar = Repo.config.animaciones
    TapaDiario(fecha, texto, modifier.clickable {
        sentir(Sentidos.Evento.ABRIR)
        if (animar) scope.launch { abre.animateTo(1f, androidx.compose.animation.core.tween(300)); onClick(); abre.snapTo(0f) } else onClick()
    }, tema, noche) { abre.value }
}

/** La tapa sola, sin el toque: [abierto] se lee al dibujar (de 0, cerrada, a 1, abierta). Las capturas la usan a medio abrir. */
@Composable
internal fun TapaDiario(fecha: String, texto: String, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, noche: Boolean = false, abierto: () -> Float = { 0f }) {
    val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(tema)
    val oscuro = esOscuro()
    val medidor = androidx.compose.ui.text.rememberTextMeasurer()
    val palabras = Texto.palabras(texto)
    val vista = texto.trim().replace(Regex("\\s+"), " ").take(160)
    val fechaTema = Estilo.fecha(fecha)
    val etiqueta = (if (noche) "El sueño de " else "El diario de ") + Fechas.corto(fecha) + if (palabras > 0) ", " + porNumero(palabras, "1 palabra", "$palabras palabras") else ", en blanco"
    BoxWithConstraints(modifier.fillMaxWidth().height(d.altoTapa).semantics { contentDescription = etiqueta }) {
        Canvas(Modifier.fillMaxSize().graphicsLayer()) {
            with(d) { tapa(io.github.ruddy2003.vestigium.ui.biblioteca.EstadoTapa(fechaTema, palabras, vista.isBlank(), noche, abierto(), fecha.hashCode()), medidor, oscuro) }
        }
        val k = d.cajaTapa
        val w = maxWidth; val h = maxHeight
        Text(if (vista.isBlank()) (if (noche) pistaNoche(tema) else pistaDia(tema)) else vista,
            color = d.tintaTapa(oscuro, noche).copy(alpha = if (vista.isBlank()) 0.62f else 0.92f),
            fontFamily = d.letraTapa, fontSize = d.tamTapa.sp, lineHeight = (d.tamTapa * 1.28f).sp, maxLines = d.lineasTapa,
            overflow = TextOverflow.Ellipsis, textAlign = if (d.centradoTapa) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.offset(w * k.left, h * k.top).size(w * (k.right - k.left), h * (k.bottom - k.top))
                .wrapContentHeight(Alignment.CenterVertically).graphicsLayer { alpha = 1f - abierto() })
    }
}

/** La ola corrida de los vasos griegos: espirales que ruedan una tras otra sobre una línea de base. */
private fun DrawScope.olaCorrida(o: Offset, largo: Float, alto: Float, color: Color) {
    val r = alto / 2; val paso = alto * 1.4f
    val p = Path(); var x = 0f
    while (x + paso <= largo) {
        val x0 = o.x + x
        p.moveTo(x0, o.y + r)
        p.cubicTo(x0 + paso * 0.45f, o.y + r, x0 + paso * 0.25f, o.y - r, x0 + paso * 0.65f, o.y - r)
        p.cubicTo(x0 + paso * 0.98f, o.y - r, x0 + paso * 0.98f, o.y + r * 0.15f, x0 + paso * 0.7f, o.y + r * 0.15f)
        p.cubicTo(x0 + paso * 0.5f, o.y + r * 0.15f, x0 + paso * 0.52f, o.y - r * 0.45f, x0 + paso * 0.7f, o.y - r * 0.4f)
        x += paso
    }
    drawPath(p, color, style = Stroke(1.5f))
    drawLine(color, Offset(o.x, o.y + r), Offset(o.x + x, o.y + r), 1.2f)
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

// =====================================================================
//  La biblioteca: lomos en estantes, rollos, boletines, cartas, expedientes, logs
// =====================================================================

/**
 * Las entradas del diario como objetos del tema: el mueble de su biblioteca ([io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca])
 * con un volumen por entrada. De noche ([noche]) guarda los sueños. [abrir] recibe la fecha.
 */
@Composable
fun EstanteDiario(entradas: List<Dia>, tema: TemaId = Estilo.tema, noche: Boolean = false, abrir: (String) -> Unit) {
    if (entradas.isEmpty()) return
    io.github.ruddy2003.vestigium.ui.biblioteca.EstanteTema(io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(tema),
        listOf(io.github.ruddy2003.vestigium.ui.biblioteca.GrupoEstante(null, entradas)), noche, abrir)
}

// =====================================================================
//  La página abierta: para leer y para escribir, a pantalla completa
// =====================================================================

enum class Rayas { NADA, RENGLONES, CUADRICULA, NUMEROS }

/** Cómo se ve una página abierta en cada tema. */
class Pagina(val fondo: Int?, val color: Color, val tinta: Color, val tenue: Color, val acento: Color, val detalle: Color,
             val texto: FontFamily, val tam: Float, val interlinea: Float, val oscura: Boolean, val rayas: Rayas = Rayas.NADA,
             val capitular: FontFamily? = null)

/**
 * El papel y la letra de cada tema. Cada tema escribe con una letra propia: inscripción romana, Garamond con griego,
 * gótica de copista, textura de vitral, kúfica, Didot, letra de molde soviética, estarcido, máquina de escribir, cursiva de carta…
 */
fun pagina(t: TemaId): Pagina = when (t) {
    TemaId.ROMANO -> Pagina(R.drawable.papel_romano, c(0xFFE8D4A2), c(0xFF2A1E14), c(0xFF6B5536), c(0xFF8E2B1F), c(0xFF6B4423), Letra.inscripcion, 16.5f, 27f, false, capitular = Letra.inscripcion)
    // La página de cuaderno escolar, a renglones, en letra de periódico; la primera letra en Titillium, como un rótulo.
    TemaId.URBE -> Pagina(R.drawable.papel_urbe, c(0xFFF5EEDC), c(0xFF2B2420), c(0xFF6A5D51), c(0xFFA8432A), c(0xFF3D5E45), Letra.cronaca, 17f, 27f, false, Rayas.RENGLONES, Letra.placa)
    // Pergamino de Pérgamo: más claro y liso que el papiro romano.
    TemaId.HELENO -> Pagina(R.drawable.papel_heleno, c(0xFFF3E9D2), c(0xFF1F1712), c(0xFF6B5536), c(0xFFA8471F), c(0xFF1A1410), Letra.garamond, 18.5f, 27f, false)
    // Papiro del Nilo, tinta de carbón, rúbricas en cornalina y signos en lapislázuli; Escriba (Gentium) para leer de corrido.
    TemaId.EGIPTO -> Pagina(R.drawable.papel_egipto, c(0xFFEFE0B2), c(0xFF1E160C), c(0xFF6E5A36), c(0xFFB8471E), c(0xFF1D2F7A), Letra.escriba, 17.5f, 27.5f, false, capitular = Letra.messiri)
    TemaId.SEDA -> Pagina(R.drawable.papel_seda, c(0xFF1F3578), c(0xFFF2EAD3), c(0xFFB9B29F), c(0xFFE0B04F), c(0xFFE26A50), LetraExtra.alegreya, 17.5f, 27f, true, capitular = LetraExtra.almendra)
    // Washi claro con tinta sumi; sin capitular, como en los libros japoneses.
    TemaId.EDO -> Pagina(R.drawable.papel_edo, c(0xFFF3EDDD), c(0xFF1B1A1E), c(0xFF6B655B), c(0xFFC9442E), c(0xFF1E3A5F), LetraExtra.zenMincho, 17f, 29f, false)
    TemaId.MEDIEVAL -> Pagina(R.drawable.papel_medieval, c(0xFFEDDDB8), c(0xFF2B1D12), c(0xFF6E5639), c(0xFFB3261E), c(0xFF1F3A93), LetraExtra.gotica, 19.5f, 28f, false, capitular = LetraExtra.gotica)
    // El pergamino blanqueado de la regla, en la letra de la regla, con capitulares de estandarte.
    TemaId.TEMPLARIO -> Pagina(R.drawable.papel_templario, c(0xFFF1EADA), c(0xFF201E1B), c(0xFF5B574E), c(0xFFB0191F), c(0xFF4A5560), LetraExtra.regla, 17.5f, 27f, false, capitular = LetraExtra.cruzada)
    TemaId.COLONOS -> Pagina(R.drawable.papel_colonos, c(0xFFF3EBD7), c(0xFF1E1A16), c(0xFF6B6255), c(0xFF9E2B25), c(0xFF3E4B60), LetraExtra.gaceta, 17.5f, 28f, false, Rayas.RENGLONES, LetraExtra.fell)
    TemaId.ORDEN -> Pagina(R.drawable.papel_orden, c(0xFF1C2127), c(0xFFDDE2E7), c(0xFF9DA6B0), c(0xFFD6B25A), c(0xFFD0333B), LetraExtra.estarcido, 16f, 25f, true, Rayas.NADA)
    TemaId.VAPOR -> Pagina(R.drawable.papel_vapor, c(0xFFEDE3CB), c(0xFF231B14), c(0xFF6E5E46), c(0xFF8A5A14), c(0xFF9C3D24), LetraExtra.bitter, 16.5f, 25.5f, false, Rayas.CUADRICULA, LetraExtra.caldera)
    TemaId.BOTANICO -> Pagina(R.drawable.papel_botanico, c(0xFFF1E9D2), c(0xFF2A2318), c(0xFF6B5E45), c(0xFF3F5A2C), c(0xFF8C4A22), LetraExtra.fraunces, 17.5f, 27f, false, Rayas.NADA, LetraExtra.fraunces)
    TemaId.RUINAS -> Pagina(R.drawable.papel_ruinas, c(0xFFE6E3D6), c(0xFF1F2420), c(0xFF5A5F55), c(0xFF3E6B2A), c(0xFFA34A1E), LetraExtra.overpass, 16.5f, 26f, false, Rayas.RENGLONES, LetraExtra.letrero)
    TemaId.GOTICO -> Pagina(R.drawable.papel_gotico, c(0xFFF1E8D2), c(0xFF1A1630), c(0xFF5E5870), c(0xFFB3122E), c(0xFF1F3A9E), LetraExtra.textura, 19.5f, 28f, false, capitular = LetraExtra.textura)
    TemaId.ARCANO -> Pagina(R.drawable.papel_arcano, c(0xFFE8DDC0), c(0xFF1E3329), c(0xFF5E665A), c(0xFF7A2C24), c(0xFF2D5446), Letra.castoro, 18f, 27f, false, Rayas.RENGLONES, capitular = Letra.spectralCursiva)
    TemaId.ESPACIO -> Pagina(R.drawable.papel_espacio, c(0xFF050A18), c(0xFFE8ECF8), c(0xFF6E7BA0), c(0xFF8AB4FF), c(0xFFFFB86B), LetraExtra.grotesk, 16f, 25f, true, Rayas.RENGLONES)
    // El papel impermeable de la bitácora, verdoso, con la letra hecha para leerse con poca luz y la capitular estrecha.
    TemaId.ABISMO -> Pagina(R.drawable.papel_abismo, c(0xFFE9F1F0), c(0xFF0C2430), c(0xFF4B6B78), c(0xFF0A5C8C), c(0xFFB8913E), LetraExtra.atkinson, 16.5f, 26f, false, Rayas.RENGLONES, LetraExtra.batiscafo)
    TemaId.ANDALUS -> Pagina(R.drawable.papel_andalus, c(0xFFF2E6C8), c(0xFF2A1A12), c(0xFF6A5640), c(0xFF1E6B5C), c(0xFFB5432A), LetraExtra.amiri, 18.5f, 28f, false, capitular = LetraExtra.kufi)
    // La página de estuco del códice, con renglones rojos; la letra de leer y la capitular en versalitas.
    TemaId.MESO -> Pagina(R.drawable.papel_meso, c(0xFFF2E8D2), c(0xFF2A1810), c(0xFF6B5238), c(0xFFA3301E), c(0xFF2F7D57), LetraExtra.amate, 17.5f, 27f, false, Rayas.RENGLONES, capitular = LetraExtra.codice)
    TemaId.MONGOL -> Pagina(R.drawable.papel_mongol, c(0xFFF1EBDC), c(0xFF18202E), c(0xFF5C6577), c(0xFF1F5FA8), c(0xFFC2412D), LetraExtra.filosofo, 17.5f, 27f, false)
    TemaId.RENACIMIENTO -> Pagina(R.drawable.papel_renacimiento, c(0xFFEEDFBC), c(0xFF3A2614), c(0xFF7A6040), c(0xFF8C3B1E), c(0xFF2F4F6F), LetraExtra.cormorant, 19.5f, 28f, false, capitular = LetraExtra.italianno)
    TemaId.PIRATA -> Pagina(R.drawable.papel_pirata, c(0xFFE6DCBA), c(0xFF2A1C0F), c(0xFF6A5A42), c(0xFF1E5157), c(0xFF9A2A1C), LetraExtra.caslon, 17f, 27f, false, Rayas.RENGLONES, capitular = LetraExtra.corsario)
    TemaId.URSS -> Pagina(R.drawable.papel_urss, c(0xFFEFE6D2), c(0xFF161414), c(0xFF6A625A), c(0xFFC1121F), c(0xFF161414), LetraExtra.russo, 15.5f, 26f, false, Rayas.RENGLONES)
    TemaId.LABORATORIO -> Pagina(R.drawable.papel_laboratorio, c(0xFFF7FBFC), c(0xFF1B2733), c(0xFF5D6F80), c(0xFF0B8FA3), c(0xFFE0557A), LetraExtra.jost, 16.5f, 26f, false, Rayas.CUADRICULA)
    TemaId.NAPOLEON -> Pagina(R.drawable.papel_napoleon, c(0xFFF6F0E0), c(0xFF14213D), c(0xFF5A6278), c(0xFF1E3A8A), c(0xFFA3172E), LetraExtra.bodoni, 18f, 27f, false, Rayas.NADA, LetraExtra.bodoniTitulo)
    // El papel de cartas del salón de escribir del barco: marfil, tinta de laca, oro y jade; capitulares de marquesina.
    TemaId.DECO -> Pagina(R.drawable.papel_deco, c(0xFFF6F0E1), c(0xFF16130E), c(0xFF6A6052), c(0xFF8A6A12), c(0xFF1F6B57), LetraExtra.pasaje, 17f, 27f, false, Rayas.NADA, LetraExtra.poiret)
    // La carta de Navidad va en cursiva de pluma, grande para que se lea bien.
    TemaId.NAVIDAD -> Pagina(R.drawable.papel_navidad, c(0xFFFBF6EC), c(0xFF1E2A22), c(0xFF5E6B62), c(0xFFB71C1C), c(0xFF1B5E20), LetraExtra.italianno, 27f, 32f, false, Rayas.NADA, LetraExtra.cinzelDeco)
    // La declaración, tecleada.
    TemaId.DISTOPIA -> Pagina(R.drawable.papel_distopia, c(0xFFECE8DC), c(0xFF141414), c(0xFF55585C), c(0xFFC8102E), c(0xFF141414), Letra.terminal, 15.5f, 24f, false, Rayas.RENGLONES)
    TemaId.CIBER -> Pagina(R.drawable.papel_ciber, c(0xFF030708), c(0xFFBDEFF5), c(0xFF3E7880), c(0xFF00E5FF), c(0xFF5FC4D2), Letra.codigo, 15f, 23f, true, Rayas.NUMEROS)
    // El reverso de un ticket térmico bajo la luz del letrero: oscuro, con la tinta clara y el corte en ámbar.
    TemaId.NEON -> Pagina(R.drawable.papel_neon, c(0xFF120C17), c(0xFFF4E7F0), c(0xFF8E7488), c(0xFFFF3D9A), c(0xFFFFB238), Letra.letrero, 16f, 24f, true, Rayas.RENGLONES)
    TemaId.REFUGIO -> Pagina(R.drawable.papel_refugio, c(0xFF031A0B), c(0xFF7CF098), c(0xFF2F8F4E), c(0xFF3DFF7F), c(0xFFFFB23F), LetraExtra.vt323, 22f, 26f, true, Rayas.NADA)
    TemaId.REVOLUCION -> Pagina(R.drawable.papel_revolucion, c(0xFFF2ECDD), c(0xFF1C1A17), c(0xFF5E584E), c(0xFFB22234), c(0xFF1F3A6E), LetraExtra.ciudadana, 17.5f, 28f, false, Rayas.NADA, LetraExtra.convencion)
    TemaId.ANTIGUAS -> Pagina(R.drawable.papel_antiguas, c(0xFFEDE2C6), c(0xFF1E1D1A), c(0xFF5E5646), c(0xFF8E1B24), c(0xFF2E5A3E), LetraExtra.vollkorn, 18f, 27f, false, Rayas.NADA, LetraExtra.camino)
    TemaId.CUEVA -> Pagina(R.drawable.papel_cueva, c(0xFFEADCC2), c(0xFF2A211B), c(0xFF6A5A48), c(0xFFA2401F), c(0xFFB07418), LetraExtra.abrigo, 17f, 27f, false, Rayas.NADA, LetraExtra.trazo)
}

/** Fondo de la página: papel, papiro, seda, pantalla… y el adorno de cada tema. En modo oscuro, el papel claro se atenúa un poco para no deslumbrar. */
@Composable
internal fun FondoPagina(t: TemaId, p: Pagina, modifier: Modifier = Modifier, noche: Boolean = false) {
    val oscuroApp = esOscuro()
    val n = nocheDe(t)
    Box(modifier.fillMaxSize().background(p.color)) {
        p.fondo?.let { Image(painterResource(it), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
        // De noche el papel queda debajo del cielo del tema: se adivina la textura, pero manda su noche.
        if (noche) Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(lerp(p.color, n.cielo, 0.4f).copy(alpha = if (p.fondo != null) 0.9f else 0.7f),
            lerp(lerp(n.cielo, n.luz, 0.1f), p.acento, 0.12f).copy(alpha = if (p.fondo != null) 0.88f else 0.6f)))))
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            when (t) {
                TemaId.ROMANO -> {
                    // Las juntas de las hojas pegadas del volumen (kolleseis) y la doble regla del margen.
                    var x = w * 0.29f; while (x < w) { drawRect(p.detalle.copy(alpha = 0.08f), Offset(x, 0f), Size(6.dp.toPx(), h)); x += w * 0.37f }
                    listOf(18.dp.toPx(), 22.dp.toPx()).forEach { x0 -> drawLine(p.acento.copy(alpha = 0.3f), Offset(x0, 0f), Offset(x0, h), 1f) }
                }
                TemaId.URBE -> {
                    // El margen a lápiz del cuaderno y, arriba a la derecha, el boleto pegado con dos tiras de cinta.
                    drawLine(p.tenue.copy(alpha = 0.35f), Offset(30.dp.toPx(), 0f), Offset(30.dp.toPx(), h), 1f)
                    val o = Offset(w - 86.dp.toPx(), 12.dp.toPx())
                    rotate(5f, o + Offset(34.dp.toPx(), 14.dp.toPx())) {
                        drawRect(Color(0xFFDCE8D2).copy(alpha = 0.85f), o, Size(68.dp.toPx(), 28.dp.toPx()))
                        drawRect(p.detalle.copy(alpha = 0.45f), o + Offset(0f, 4.dp.toPx()), Size(68.dp.toPx(), 4.dp.toPx()))
                        drawRect(Color(0xFF7A3A6A).copy(alpha = 0.3f), o + Offset(50.dp.toPx(), 11.dp.toPx()), Size(11.dp.toPx(), 13.dp.toPx()))
                        listOf(Offset(-5f, -4f), Offset(58f, 22f)).forEach { d -> drawRect(Color(0xFFF2EAC6).copy(alpha = 0.55f), o + Offset(d.x.dp.toPx(), d.y.dp.toPx()), Size(16.dp.toPx(), 7.dp.toPx())) }
                    }
                }
                TemaId.HELENO -> {
                    // Pergamino de Pérgamo (las manchas y los poros vienen en papel_heleno): la ola corrida arriba y abajo.
                    drawRect(Brush.radialGradient(listOf(Color.Transparent, p.tenue.copy(alpha = 0.14f)), center = Offset(w / 2, h / 2), radius = maxOf(w, h) * 0.75f))
                    listOf(14.dp.toPx(), h - 22.dp.toPx()).forEach { y -> olaCorrida(Offset(16.dp.toPx(), y), w - 32.dp.toPx(), 9.dp.toPx(), p.detalle.copy(alpha = 0.55f)) }
                }
                TemaId.EGIPTO -> {
                    // Papiro (las fibras y las uniones vienen en papel_egipto): arriba, el friso jeker de juncos atados;
                    // abajo, la cenefa de rectángulos de colores; y el punto rojo de verso en el margen.
                    val u = 1.dp.toPx(); val paso = 8 * u
                    var x = 14 * u; var i = 0
                    while (x < w - 14 * u) {
                        val col = (if (i % 2 == 0) p.detalle else p.acento).copy(alpha = 0.5f)
                        drawRect(col, Offset(x - 1.3f * u, 14 * u), Size(2.6f * u, 9 * u)); drawCircle(col, 2.1f * u, Offset(x, 12 * u))
                        x += maxOf(paso, 1f); i++
                    }
                    val colores = listOf(p.detalle, p.acento, p.tenue)
                    var xc = 14 * u; var k = 0
                    while (xc < w - 14 * u) { drawRect(colores[k % 3].copy(alpha = 0.45f), Offset(xc, h - 18 * u), Size(5 * u, 4 * u)); xc += 7 * u; k++ }
                    drawLine(p.acento.copy(alpha = 0.3f), Offset(20 * u, 30 * u), Offset(20 * u, h - 26 * u), 0.8f * u)
                }
                TemaId.MEDIEVAL -> listOf(26.dp.toPx(), 31.dp.toPx(), w - 26.dp.toPx()).forEach { x -> drawLine(p.acento.copy(alpha = 0.22f), Offset(x, 0f), Offset(x, h), 1.2f) }
                TemaId.TEMPLARIO -> {
                    // La banda roja de la sobreveste en el margen y, arriba, una fila de almenas tenue.
                    drawRect(p.acento.copy(alpha = 0.18f), Offset(18.dp.toPx(), 0f), Size(4.dp.toPx(), h))
                    drawLine(p.detalle.copy(alpha = 0.25f), Offset(25.dp.toPx(), 0f), Offset(25.dp.toPx(), h), 1f)
                    val m = 12.dp.toPx(); var x = 0f; while (x < w) { drawRect(p.detalle.copy(alpha = 0.12f), Offset(x, 0f), Size(m, 8.dp.toPx())); x += maxOf(m * 2, 1f) }
                }
                TemaId.COLONOS -> {
                    // Libro de registro: doble margen rojo, la cabecera azul y la columna de las cuentas.
                    listOf(36.dp.toPx(), 40.dp.toPx()).forEach { x -> drawLine(p.acento.copy(alpha = 0.45f), Offset(x, 0f), Offset(x, h), 1.2f) }
                    listOf(18.dp.toPx(), 21.dp.toPx()).forEach { y -> drawLine(p.detalle.copy(alpha = 0.45f), Offset(0f, y), Offset(w, y), 1.2f) }
                    drawLine(p.detalle.copy(alpha = 0.3f), Offset(w - 64.dp.toPx(), 0f), Offset(w - 64.dp.toPx(), h), 1f)
                }
                TemaId.URSS -> {
                    // La libreta escolar: margen rojo a la derecha y una cuña roja y negra abajo, como en los carteles.
                    drawLine(p.acento.copy(alpha = 0.5f), Offset(w - 22.dp.toPx(), 0f), Offset(w - 22.dp.toPx(), h), 1.5f)
                    drawPath(Path().apply { moveTo(0f, h); lineTo(46.dp.toPx(), h); lineTo(0f, h - 46.dp.toPx()); close() }, p.acento.copy(alpha = 0.6f))
                    drawPath(Path().apply { moveTo(0f, h - 46.dp.toPx()); lineTo(46.dp.toPx(), h); lineTo(58.dp.toPx(), h); lineTo(0f, h - 58.dp.toPx()); close() }, p.tinta.copy(alpha = 0.7f))
                }
                TemaId.NAPOLEON -> { var x = 0f; while (x < w) { drawLine(p.tenue.copy(alpha = 0.07f), Offset(x, 0f), Offset(x, h), 1f); x += 22.dp.toPx() } }
                // Un sol de rayos muy tenue que baja desde lo alto y, al pie, tres filetes escalonados.
                TemaId.DECO -> {
                    val o = Offset(w / 2, -h * 0.04f)
                    for (i in 0..18) { val a = PI * (0.12 + 0.76 * i / 18.0); drawLine(p.acento.copy(alpha = 0.05f), o, o + polarLibro(h * 0.6f, a), 1.2f) }
                    listOf(0.7f, 0.5f, 0.3f).forEachIndexed { k, f -> drawLine(p.acento.copy(alpha = 0.22f), Offset(w * (1 - f) / 2, h - (14 + k * 5).dp.toPx()), Offset(w * (1 + f) / 2, h - (14 + k * 5).dp.toPx()), 1f) }
                }
                TemaId.NAVIDAD -> {
                    drawRect(p.acento, Offset(8.dp.toPx(), 8.dp.toPx()), Size(w - 16.dp.toPx(), h - 16.dp.toPx()), style = Stroke(3f))
                    drawRect(p.detalle, Offset(13.dp.toPx(), 13.dp.toPx()), Size(w - 26.dp.toPx(), h - 26.dp.toPx()), style = Stroke(1.5f))
                }
                TemaId.ORDEN -> {
                    drawRect(p.acento.copy(alpha = 0.7f), Offset(6.dp.toPx(), 6.dp.toPx()), Size(w - 12.dp.toPx(), h - 12.dp.toPx()), style = Stroke(2f))
                    listOf(Offset(16f, 16f), Offset(w - 16f, 16f), Offset(16f, h - 16f), Offset(w - 16f, h - 16f)).forEach { drawCircle(p.acento, 9f, it) }
                }
                TemaId.VAPOR -> {
                    // La lámina de patente: doble marco de dibujante y, abajo a la derecha, la cartela con sus casillas
                    // (número, fecha y firma), como en los planos que se entregaban en la oficina.
                    val m0 = 8.dp.toPx()
                    drawRect(p.tinta.copy(alpha = 0.5f), Offset(m0, m0), Size(w - 2 * m0, h - 2 * m0), style = Stroke(1.6f))
                    drawRect(p.tinta.copy(alpha = 0.25f), Offset(m0 + 4.dp.toPx(), m0 + 4.dp.toPx()), Size(w - 2 * m0 - 8.dp.toPx(), h - 2 * m0 - 8.dp.toPx()), style = Stroke(0.8f))
                    val cw = 120.dp.toPx(); val ch = 42.dp.toPx(); val o = Offset(w - m0 - 4.dp.toPx() - cw, h - m0 - 4.dp.toPx() - ch)
                    drawRect(p.tinta.copy(alpha = 0.3f), o, Size(cw, ch), style = Stroke(0.9f))
                    drawLine(p.tinta.copy(alpha = 0.25f), o + Offset(0f, ch / 2), o + Offset(cw, ch / 2), 0.8f)
                    drawLine(p.tinta.copy(alpha = 0.25f), o + Offset(cw * 0.55f, 0f), o + Offset(cw * 0.55f, ch), 0.8f)
                    drawPath(Vapor.engrane(o + Offset(cw * 0.27f, ch * 0.25f), ch * 0.18f, 8, 0.76f), p.acento.copy(alpha = 0.45f))
                }
                TemaId.REFUGIO -> {
                    drawRect(Brush.radialGradient(listOf(p.acento.copy(alpha = 0.1f), Color.Transparent), center = Offset(w / 2, h / 2), radius = h * 0.7f))
                    var y = 0f; while (y < h) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(0f, y), Offset(w, y), 1.5f); y += 4.dp.toPx() }
                }
                TemaId.CIBER -> { var y = 0f; while (y < h) { drawLine(p.acento.copy(alpha = 0.03f), Offset(0f, y), Offset(w, y), 1f); y += 3.dp.toPx() } }
                TemaId.NEON -> {
                    // El reflejo del letrero en la hoja mojada: una mancha magenta arriba y otra verde abajo, y el borde dentado del ticket.
                    drawRect(Brush.radialGradient(listOf(p.acento.copy(alpha = 0.12f), Color.Transparent), center = Offset(w * 0.8f, 0f), radius = w * 0.7f))
                    drawRect(Brush.radialGradient(listOf(Color(0xFF8CFF5A).copy(alpha = 0.06f), Color.Transparent), center = Offset(0f, h), radius = w * 0.6f))
                    val d = 7.dp.toPx(); var x = 0f
                    while (x < w) { drawPath(Path().apply { moveTo(x, h); lineTo(x + d / 2, h - d * 0.6f); lineTo(x + d, h); close() }, p.detalle.copy(alpha = 0.5f)); x += maxOf(d, 1f) }
                }
                TemaId.BOTANICO -> {
                    // Marco de lámina y el número de la tabla.
                    drawRect(p.tinta.copy(alpha = 0.35f), Offset(12.dp.toPx(), 12.dp.toPx()), Size(w - 24.dp.toPx(), h - 24.dp.toPx()), style = Stroke(1.2f))
                    drawRect(p.tinta.copy(alpha = 0.2f), Offset(16.dp.toPx(), 16.dp.toPx()), Size(w - 32.dp.toPx(), h - 32.dp.toPx()), style = Stroke(0.8f))
                }
                TemaId.RUINAS -> {
                    // Una tira de cinta plateada que sujeta la hoja arriba, ladeada, y una hoja de hiedra prensada abajo a la derecha.
                    rotate(-4f, Offset(w / 2, 10.dp.toPx())) { drawRect(Color(0xFFB8BCB8).copy(alpha = 0.55f), Offset(w / 2 - 40.dp.toPx(), 2.dp.toPx()), Size(80.dp.toPx(), 16.dp.toPx())) }
                    val c = Offset(w - 46.dp.toPx(), h - 60.dp.toPx()); val s = 40.dp.toPx()
                    drawPath(Path().apply { moveTo(c.x, c.y + s * 0.5f); lineTo(c.x - s * 0.55f, c.y + s * 0.35f); lineTo(c.x - s * 0.8f, c.y - s * 0.05f); lineTo(c.x - s * 0.32f, c.y - s * 0.05f); lineTo(c.x, c.y - s * 0.5f); lineTo(c.x + s * 0.32f, c.y - s * 0.05f); lineTo(c.x + s * 0.8f, c.y - s * 0.05f); lineTo(c.x + s * 0.55f, c.y + s * 0.35f); close() }, p.acento.copy(alpha = 0.16f))
                    drawLine(p.acento.copy(alpha = 0.22f), Offset(c.x, c.y + s * 0.5f), Offset(c.x - 6.dp.toPx(), c.y + s * 1.1f), 1.4f)
                }
                TemaId.ANTIGUAS -> {
                    // Marco de mapa: doble filete y una rosa de los vientos tenue en la esquina.
                    drawRect(p.tinta.copy(alpha = 0.3f), Offset(10.dp.toPx(), 10.dp.toPx()), Size(w - 20.dp.toPx(), h - 20.dp.toPx()), style = Stroke(1.4f))
                    drawRect(p.detalle.copy(alpha = 0.3f), Offset(14.dp.toPx(), 14.dp.toPx()), Size(w - 28.dp.toPx(), h - 28.dp.toPx()), style = Stroke(0.8f))
                    val o = Offset(w - 46.dp.toPx(), h - 56.dp.toPx()); val r0 = 22.dp.toPx()
                    for (i in 0 until 8) { val a = Math.toRadians(45.0 * i); val rr = if (i % 2 == 0) r0 else r0 * 0.5f; drawLine(p.tinta.copy(alpha = 0.16f), o, Offset(o.x + rr * cos(a).toFloat(), o.y + rr * sin(a).toFloat()), 1.2f) }
                    drawCircle(p.tinta.copy(alpha = 0.12f), r0 * 0.7f, o, style = Stroke(1f))
                }
                TemaId.CUEVA -> {
                    // El hollín de la lámpara arriba, una hilera de puntos de ocre y una mano en negativo, apenas, abajo a la derecha.
                    drawRect(Brush.verticalGradient(listOf(p.tinta.copy(alpha = 0.08f), Color.Transparent), 0f, h * 0.18f), Offset.Zero, Size(w, h * 0.18f))
                    for (k in 0 until 6) drawCircle(p.acento.copy(alpha = 0.3f), 2.4f.dp.toPx(), Offset(w * 0.5f + (k - 2.5f) * 9.dp.toPx(), 18.dp.toPx()))
                    val mano = Offset(w - 46.dp.toPx(), h - 60.dp.toPx())
                    drawCircle(Brush.radialGradient(listOf(p.acento.copy(alpha = 0.16f), Color.Transparent), mano, 40.dp.toPx()), 40.dp.toPx(), mano)
                    piezasMano(mano, 24.dp.toPx()).forEach { drawPath(it, p.color) }
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
                TemaId.ARCANO -> {
                    // El margen rojo desvaído de la libreta, la esquina doblada y unas gotas de tinta que se corrieron.
                    drawLine(p.acento.copy(alpha = 0.35f), Offset(24.dp.toPx(), 0f), Offset(24.dp.toPx(), h), 1.4f)
                    val d = 28.dp.toPx()
                    drawPath(Path().apply { moveTo(w - d, 0f); lineTo(w, d); lineTo(w - d, d); close() }, p.tinta.copy(alpha = 0.06f))
                    drawLine(p.tinta.copy(alpha = 0.2f), Offset(w - d, 0f), Offset(w - d, d), 1f); drawLine(p.tinta.copy(alpha = 0.2f), Offset(w - d, d), Offset(w, d), 1f)
                    val az = java.util.Random(19)
                    repeat(5) { val o = Offset(az.nextFloat() * w, az.nextFloat() * h); drawCircle(p.tinta.copy(alpha = 0.07f), (2f + az.nextFloat() * 4f).dp.toPx(), o) }
                }
                TemaId.ESPACIO -> {
                    val az = java.util.Random(77)
                    repeat(160) { val x = az.nextFloat() * w; val y = az.nextFloat() * h; drawCircle(Color.White.copy(alpha = 0.15f + az.nextFloat() * 0.35f), 0.6f + az.nextFloat() * 1.2f, Offset(x, y)) }
                    val k = 18.dp.toPx(); val m0 = 10.dp.toPx()
                    listOf(Offset(m0, m0) to Offset(1f, 1f), Offset(w - m0, m0) to Offset(-1f, 1f), Offset(m0, h - m0) to Offset(1f, -1f), Offset(w - m0, h - m0) to Offset(-1f, -1f)).forEach { (o, d) ->
                        drawLine(p.acento.copy(alpha = 0.55f), o, o + Offset(d.x * k, 0f), 1.5f); drawLine(p.acento.copy(alpha = 0.55f), o, o + Offset(0f, d.y * k), 1.5f)
                    }
                }
                TemaId.ABISMO -> {
                    // La regla del profundímetro en el margen (más apretada cuanto más hondo) y, de noche, plancton que brilla.
                    val x0 = 14.dp.toPx(); var y = 40.dp.toPx(); var paso = 26.dp.toPx(); var k = 0
                    drawLine(p.acento.copy(alpha = 0.35f), Offset(x0, y), Offset(x0, h - 20.dp.toPx()), 1f)
                    while (y < h - 20.dp.toPx()) {
                        drawLine(p.acento.copy(alpha = 0.4f), Offset(x0, y), Offset(x0 + (if (k % 5 == 0) 9 else 4).dp.toPx(), y), 1f)
                        y += maxOf(paso, 1f); paso = maxOf(paso * 0.96f, 9.dp.toPx()); k++
                    }
                    if (noche || p.oscura) {
                        val az = java.util.Random(53)
                        repeat(90) { val x = az.nextFloat() * w; val yy = az.nextFloat() * h; drawCircle(n.luz.copy(alpha = 0.1f + az.nextFloat() * 0.3f), 0.6f + az.nextFloat() * 1.4f, Offset(x, yy)) }
                    }
                }
                TemaId.ANDALUS -> {
                    drawRect(p.acento.copy(alpha = 0.45f), Offset(10.dp.toPx(), 10.dp.toPx()), Size(w - 20.dp.toPx(), h - 20.dp.toPx()), style = Stroke(1.5f))
                    drawRect(p.detalle.copy(alpha = 0.35f), Offset(14.dp.toPx(), 14.dp.toPx()), Size(w - 28.dp.toPx(), h - 28.dp.toPx()), style = Stroke(1f))
                }
                TemaId.MESO -> {
                    // El marco rojo de la página del códice y, abajo, una greca escalonada.
                    drawRect(p.acento.copy(alpha = 0.5f), Offset(10.dp.toPx(), 10.dp.toPx()), Size(w - 20.dp.toPx(), h - 20.dp.toPx()), style = Stroke(1.6f))
                    grecaEscalonada(16.dp.toPx(), w - 16.dp.toPx(), h - 26.dp.toPx(), h - 16.dp.toPx(), p.acento.copy(alpha = 0.4f), 1.3f)
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
                TemaId.PIRATA -> {
                    // Las líneas de rumbo de una rosa en el rincón de arriba, como en las cartas de marear, y la «X» abajo.
                    val o = Offset(w * 0.86f, h * 0.1f); val largo = w * 0.7f
                    for (k in 0 until 16) { val a = Math.PI / 2 + k * Math.PI / 16; drawLine(p.tinta.copy(alpha = if (k % 4 == 0) 0.09f else 0.05f), o, Offset(o.x + (largo * kotlin.math.cos(a)).toFloat(), o.y + (largo * kotlin.math.sin(a)).toFloat()), 1f) }
                    drawCircle(p.tinta.copy(alpha = 0.1f), w * 0.05f, o, style = Stroke(1f))
                    val x = Offset(w * 0.18f, h * 0.9f); val s = w * 0.025f
                    drawLine(p.detalle.copy(alpha = 0.25f), x - Offset(s, s), x + Offset(s, s), 2f); drawLine(p.detalle.copy(alpha = 0.25f), x + Offset(-s, s), x + Offset(s, -s), 2f)
                }
                TemaId.SEDA -> {
                    // Las columnas de hilo rojo de los rollos chinos y el sello cuadrado de cinabrio.
                    var x = 26.dp.toPx(); while (x < w) { drawLine(p.detalle.copy(alpha = 0.16f), Offset(x, 10.dp.toPx()), Offset(x, h - 10.dp.toPx()), 1f); x += 38.dp.toPx() }
                    val l = 34.dp.toPx(); val o = Offset(w - l - 18.dp.toPx(), h - l - 24.dp.toPx())
                    drawRect(p.detalle.copy(alpha = 0.7f), o, Size(l, l))
                    drawRect(p.color, o + Offset(4f, 4f), Size(l - 8f, l - 8f), style = Stroke(2f))
                    drawLine(p.color, o + Offset(l / 2, 6f), o + Offset(l / 2, l - 6f), 2f); drawLine(p.color, o + Offset(6f, l / 2), o + Offset(l - 6f, l / 2), 2f)
                }
                TemaId.EDO -> {
                    // El marco de las páginas impresas en madera (kyōkaku), doble, con la «cola de pez» (gyobi) arriba al centro.
                    val m = 10.dp.toPx()
                    drawRect(p.tinta.copy(alpha = 0.22f), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(2.4f))
                    drawRect(p.tinta.copy(alpha = 0.14f), Offset(m + 4.dp.toPx(), m + 4.dp.toPx()), Size(w - 2 * m - 8.dp.toPx(), h - 2 * m - 8.dp.toPx()), style = Stroke(1f))
                    val y0 = m + 4.dp.toPx(); val a = 9.dp.toPx()
                    drawPath(Path().apply { moveTo(w / 2 - a, y0); lineTo(w / 2 + a, y0); lineTo(w / 2 + a, y0 + a * 0.9f); lineTo(w / 2, y0 + a * 0.45f); lineTo(w / 2 - a, y0 + a * 0.9f); close() }, p.tinta.copy(alpha = 0.3f))
                }
                TemaId.LABORATORIO -> {
                    // El cuaderno de laboratorio: margen doble, la casilla del número de página y las líneas de firma y de testigo al pie.
                    listOf(24.dp.toPx(), 27.dp.toPx()).forEach { x -> drawLine(p.detalle.copy(alpha = 0.4f), Offset(x, 0f), Offset(x, h), 1f) }
                    drawRect(p.acento.copy(alpha = 0.5f), Offset(w - 74.dp.toPx(), 12.dp.toPx()), Size(58.dp.toPx(), 24.dp.toPx()), style = Stroke(1.2f))
                    val y = h - 30.dp.toPx()
                    drawLine(p.tenue.copy(alpha = 0.5f), Offset(34.dp.toPx(), y), Offset(w * 0.46f, y), 1f)
                    drawLine(p.tenue.copy(alpha = 0.5f), Offset(w * 0.54f, y), Offset(w - 26.dp.toPx(), y), 1f)
                }
                TemaId.DISTOPIA -> {
                    // Los agujeros del archivador, el ojo de agua en el papel y la franja roja de clasificación.
                    listOf(0.28f, 0.72f).forEach { f ->
                        drawCircle(p.tenue.copy(alpha = 0.3f), 6.dp.toPx(), Offset(14.dp.toPx(), h * f))
                        drawCircle(p.tenue.copy(alpha = 0.5f), 6.dp.toPx(), Offset(14.dp.toPx(), h * f), style = Stroke(1.2f))
                    }
                    val o = Offset(w / 2, h * 0.55f); val a = w * 0.32f
                    val ojo = Path().apply { moveTo(o.x - a, o.y); quadraticTo(o.x, o.y - a * 0.75f, o.x + a, o.y); quadraticTo(o.x, o.y + a * 0.75f, o.x - a, o.y); close() }
                    drawPath(ojo, p.tinta.copy(alpha = 0.035f)); drawPath(ojo, p.tinta.copy(alpha = 0.06f), style = Stroke(2f))
                    drawCircle(p.tinta.copy(alpha = 0.06f), a * 0.28f, o)
                    drawPath(Path().apply { moveTo(w - 70.dp.toPx(), h); lineTo(w - 40.dp.toPx(), h); lineTo(w, h - 40.dp.toPx()); lineTo(w, h - 70.dp.toPx()); close() }, p.acento.copy(alpha = 0.6f))
                }
            }
            if (noche) estrellasDelTema(t, t.ordinal * 17 + 3, (w * h / 5200f).toInt().coerceIn(60, 260), n.luna, max = 1.5f)
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

/** Un rótulo centrado del encabezado. */
@Composable
private fun Rubrica(texto: String, letra: FontFamily, tam: Float, color: Color, espacio: Float = 1f) =
    Text(texto, fontFamily = letra, fontSize = tam.sp, letterSpacing = espacio.sp, color = color, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))

/** Encabezado de la página: lo que cada tema pone sobre la fecha (cabecera de gaceta, rúbrica, expediente, ruta…), la fecha del tema y una banda. */
@Composable
private fun EncabezadoPagina(t: TemaId, p: Pagina, fecha: String, modo: Escrito = Escrito.DIARIO) {
    val ty = MaterialTheme.typography
    if (modo == Escrito.SUENO) { EncabezadoSueno(t, p, fecha); return }
    val dia = runCatching { Fechas.parse(fecha) }.getOrNull()
    val nDia = dia?.dayOfYear ?: 1
    val emblema: @Composable () -> Unit = { Emblema(t, 40.dp, MaterialTheme.colorScheme, animado = false) }
    Column(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        when (t) {
            TemaId.ROMANO -> { emblema(); Rubrica("ACTA · DIVRNA", Letra.inscripcion, 12f, p.acento, 3f) }
            // La cabecera de la crónica de la ciudad, entre una raya gruesa y una fina, con el rione del día.
            TemaId.URBE -> Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().height(2.dp).background(p.tinta))
                Text("CRONACA DI ROMA · " + Urbe.rione(nDia), fontFamily = Letra.placa, fontSize = 12.sp, letterSpacing = 1.2.sp, color = p.acento, modifier = Modifier.padding(vertical = 3.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(p.tinta))
            }
            TemaId.CIBER -> Text("~/vestigium/diario/$fecha.log", fontFamily = Letra.codigo, fontSize = 12.sp, color = p.tenue, modifier = Modifier.fillMaxWidth())
            TemaId.NEON -> { emblema(); Rubrica("屋台 · TICKET DEL DÍA · Nº $nDia", Letra.letrero, 13f, p.acento, 2f) }
            TemaId.HELENO -> { emblema(); Rubrica("ΕΦΗΜΕΡΙΔΕΣ · diario", Letra.garamond, 13f, p.acento, 2f) }
            TemaId.EGIPTO -> { emblema(); Rubrica("El papiro del día · hru", Letra.messiri, 13f, p.acento, 1f) }
            TemaId.MEDIEVAL -> Row(verticalAlignment = Alignment.CenterVertically) {
                Emblema(t, 34.dp, MaterialTheme.colorScheme, animado = false)
                Text("Aquí comiença el folio ${Temas.copista(nDia)}", fontFamily = LetraExtra.gotica, fontSize = 15.sp, color = p.acento, modifier = Modifier.padding(start = 8.dp))
            }
            TemaId.TEMPLARIO -> Row(verticalAlignment = Alignment.CenterVertically) {
                Emblema(t, 34.dp, MaterialTheme.colorScheme, animado = false)
                Text("Parte de la jornada ${Temas.puntos(nDia)}", fontFamily = LetraExtra.cruzada, fontSize = 15.sp, color = p.acento, modifier = Modifier.padding(start = 8.dp))
            }
            TemaId.DISTOPIA -> Row(Modifier.fillMaxWidth().border(1.5.dp, p.tinta)) {
                Text("EXPEDIENTE Nº %04d".format((fecha.hashCode() and 0x7fffffff) % 10000), fontFamily = LetraExtra.oswald, fontSize = 13.sp, color = p.tinta, modifier = Modifier.weight(1f).padding(6.dp))
                Text("DECLARACIÓN DIARIA", fontFamily = LetraExtra.oswald, fontSize = 13.sp, color = p.acento, modifier = Modifier.padding(6.dp))
            }
            TemaId.REFUGIO -> Text("BITÁCORA PERSONAL · TERMINAL 03", fontFamily = LetraExtra.vt323, fontSize = 18.sp, color = p.acento)
            TemaId.ORDEN -> Row(Modifier.border(1.5.dp, p.acento).padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Emblema(t, 28.dp, MaterialTheme.colorScheme, animado = false)
                Text("PARTE DE LA ORDEN · Nº %03d".format(nDia), fontFamily = LetraExtra.estarcido, fontSize = 13.sp, color = p.tinta, modifier = Modifier.padding(start = 8.dp))
            }
            // La cabecera de la memoria de patente: el emblema y el número de la patente, entre dos filetes de latón.
            TemaId.VAPOR -> {
                emblema()
                Box(Modifier.fillMaxWidth().height(1.5.dp).background(p.acento))
                Text("Patente n.º %d · memoria descriptiva".format(1000 + nDia), fontFamily = LetraExtra.caldera, fontSize = 14.sp, color = p.tinta, modifier = Modifier.padding(vertical = 3.dp))
                Box(Modifier.fillMaxWidth().height(1.5.dp).background(p.acento))
            }
            TemaId.LABORATORIO -> Text("PROTOCOLO VM-%03d · CUADERNO DE LABORATORIO".format((fecha.hashCode() and 0x7fffffff) % 900 + 100), fontFamily = LetraExtra.jost, fontSize = 11.sp, color = p.acento)
            // La cabecera de la gaceta del pueblo, entre filetes.
            TemaId.COLONOS -> Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().height(2.dp).background(p.tinta))
                Text("LA GACETA DEL PUEBLO", fontFamily = LetraExtra.fell, fontSize = 20.sp, color = p.tinta, letterSpacing = 1.5.sp, modifier = Modifier.padding(vertical = 2.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(p.tinta))
                Text("Año ${dia?.year ?: ""} · Número $nDia", fontFamily = LetraExtra.fell, fontSize = 11.sp, color = p.tenue, modifier = Modifier.padding(top = 2.dp))
            }
            TemaId.NAPOLEON -> { emblema(); Rubrica("BULLETIN DE LA GRANDE ARMÉE · Nº $nDia", LetraExtra.bodoniTitulo, 12f, p.acento, 1.5f) }
            TemaId.DECO -> { emblema(); Rubrica("PASAJE Nº $nDia · LÍNEA TRANSATLÁNTICA", LetraExtra.poiret, 12f, p.acento, 2.4f) }
            TemaId.BOTANICO -> Row(verticalAlignment = Alignment.CenterVertically) {
                Emblema(t, 36.dp, MaterialTheme.colorScheme, animado = false)
                Column(Modifier.padding(start = 10.dp)) {
                    Text("Lámina $nDia", fontFamily = LetraExtra.fraunces, fontSize = 16.sp, color = p.acento)
                    Text("observaciones de campo", fontFamily = Letra.garamond, fontSize = 12.sp, color = p.tenue, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
            TemaId.RUINAS -> Row(verticalAlignment = Alignment.CenterVertically) {
                Emblema(t, 36.dp, MaterialTheme.colorScheme, animado = false)
                Column(Modifier.padding(start = 10.dp)) {
                    Text("Hoja del cuaderno", fontFamily = LetraExtra.letrero, fontSize = 17.sp, color = p.acento)
                    Text(Estilo.fecha(fecha), fontFamily = LetraExtra.overpass, fontSize = 12.sp, color = p.tenue)
                }
            }
            TemaId.NAVIDAD -> { emblema(); Rubrica("— Junto al fuego —", LetraExtra.cinzelDeco, 14f, p.acento) }
            // El sello rojo con «diario» en chino y el nombre de la caravana.
            TemaId.SEDA -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("日記", fontSize = 18.sp, color = p.detalle, modifier = Modifier.border(2.dp, p.detalle).padding(horizontal = 4.dp, vertical = 2.dp))
                Text("diario de la caravana", fontFamily = LetraExtra.almendra, fontSize = 15.sp, color = p.acento, modifier = Modifier.padding(start = 10.dp))
            }
            // El sello rojo con 足 («pie», la huella) y el título del cuaderno de viaje con el número del día.
            TemaId.EDO -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("足", fontFamily = LetraExtra.shippori, fontSize = 17.sp, color = p.color, modifier = Modifier.background(p.acento).padding(horizontal = 5.dp, vertical = 1.dp))
                Text("道中日記 · jornada ${Estilo.numeral(nDia)}", fontFamily = LetraExtra.shippori, fontSize = 15.sp, color = p.tinta, modifier = Modifier.padding(start = 10.dp))
            }
            TemaId.GOTICO -> { emblema(); Rubrica("Liber horarum · Horae diei", LetraExtra.textura, 16f, p.acento) }
            TemaId.ARCANO -> { emblema(); Rubrica("Archivo del caso · legajo " + Temas.tachado(nDia), Letra.spectralCursiva, 15f, p.acento) }
            TemaId.ESPACIO -> {
                emblema()
                Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(p.detalle, androidx.compose.foundation.shape.CircleShape))
                    Text("REGISTRO DE MISIÓN / CANAL 1", fontFamily = LetraExtra.orbita, fontSize = 11.sp, color = p.acento, letterSpacing = 1.sp, modifier = Modifier.padding(start = 6.dp))
                }
            }
            TemaId.ABISMO -> {
                emblema()
                Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(9.dp).border(1.5.dp, p.detalle, androidx.compose.foundation.shape.CircleShape))
                    Text("HOJA DE INMERSIÓN · COTA ${nDia * 10} m", fontFamily = LetraExtra.batiscafo, fontSize = 13.sp, color = p.acento, letterSpacing = 0.8.sp, modifier = Modifier.padding(start = 6.dp))
                }
            }
            TemaId.ANDALUS -> { emblema(); Rubrica("يوميات · Diario", LetraExtra.kufi, 15f, p.acento) }
            TemaId.MESO -> { emblema(); Rubrica("Códice del día · " + (dia?.let { Maya.tzolkin(it) } ?: "k'in"), LetraExtra.codice, 15f, p.acento, 1.2f) }
            TemaId.MONGOL -> { emblema(); Rubrica("Өдрийн тэмдэглэл · diario", LetraExtra.filosofo, 13f, p.acento, 1.5f) }
            TemaId.RENACIMIENTO -> Row(verticalAlignment = Alignment.CenterVertically) {
                Emblema(t, 34.dp, MaterialTheme.colorScheme, animado = false)
                Text("Quaderno di note · c. $nDia", fontFamily = LetraExtra.italianno, fontSize = 26.sp, color = p.acento, modifier = Modifier.padding(start = 8.dp))
            }
            TemaId.PIRATA -> Row(verticalAlignment = Alignment.CenterVertically) {
                Emblema(t, 32.dp, MaterialTheme.colorScheme, animado = false)
                Text("Bitácora · singladura $nDia", fontFamily = LetraExtra.corsario, fontSize = 21.sp, color = p.acento, modifier = Modifier.padding(start = 8.dp))
            }
            TemaId.URSS -> Text("ОТЧЁТ · INFORME DE JORNADA", fontFamily = LetraExtra.russo, fontSize = 12.sp, color = p.acento, modifier = Modifier.border(2.dp, p.acento).padding(horizontal = 8.dp, vertical = 2.dp))
            TemaId.REVOLUCION -> { emblema(); Rubrica("CAHIER DE DOLÉANCES", LetraExtra.convencion, 15f, p.acento, 2f); Rubrica("de la commune · Nº $nDia", LetraExtra.ciudadana, 12f, p.tenue) }
            TemaId.ANTIGUAS -> { emblema(); Rubrica("CRÓNICA DE LA COMPAÑÍA", LetraExtra.camino, 13f, p.acento, 1.5f) }
            TemaId.CUEVA -> { emblema(); Rubrica("La pared de este sol", LetraExtra.trazo, 19f, p.acento, 1f) }
        }
        Text(Estilo.fecha(fecha), style = ty.headlineSmall, color = if (p.oscura) p.acento else p.tinta, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        Text(Fechas.titulo(fecha).lowercase() + " · " + Fechas.relativo(fecha), style = ty.labelMedium, color = p.tenue, textAlign = TextAlign.Center)
        Meandro(Modifier.padding(top = 10.dp, bottom = 12.dp))
    }
}

/** La letra y el tamaño del rótulo de la página de un sueño. */
private fun letraSueno(t: TemaId): Pair<FontFamily, Float> = when (t) {
    TemaId.ROMANO -> Letra.inscripcion to 12f
    TemaId.URBE -> Letra.placa to 12.5f
    TemaId.CIBER -> Letra.codigo to 12f
    TemaId.NEON -> Letra.letrero to 13f
    TemaId.HELENO -> Letra.garamond to 13f
    TemaId.EGIPTO -> Letra.messiri to 13.5f
    TemaId.MEDIEVAL -> LetraExtra.gotica to 15f
    TemaId.TEMPLARIO -> LetraExtra.cruzada to 14f
    TemaId.DISTOPIA -> LetraExtra.oswald to 12f
    TemaId.REFUGIO -> LetraExtra.vt323 to 18f
    TemaId.ORDEN -> LetraExtra.estarcido to 12f
    TemaId.VAPOR -> LetraExtra.caldera to 12.5f
    TemaId.LABORATORIO -> LetraExtra.jost to 12f
    TemaId.COLONOS -> LetraExtra.fell to 14f
    TemaId.NAPOLEON -> LetraExtra.bodoniTitulo to 12f
    TemaId.DECO -> LetraExtra.poiret to 13f
    TemaId.BOTANICO -> LetraExtra.fraunces to 13f
    TemaId.RUINAS -> LetraExtra.letrero to 14f
    TemaId.NAVIDAD -> LetraExtra.cinzelDeco to 13f
    TemaId.SEDA -> LetraExtra.almendra to 14f
    TemaId.EDO -> LetraExtra.shippori to 14.5f
    TemaId.GOTICO -> LetraExtra.textura to 16f
    TemaId.ARCANO -> Letra.spectralCursiva to 15f
    TemaId.ESPACIO -> LetraExtra.orbita to 11f
    TemaId.ABISMO -> LetraExtra.batiscafo to 14f
    TemaId.ANDALUS -> LetraExtra.kufi to 14f
    TemaId.MESO -> LetraExtra.codice to 15f
    TemaId.MONGOL -> LetraExtra.filosofo to 13f
    TemaId.RENACIMIENTO -> LetraExtra.italianno to 26f
    TemaId.PIRATA -> LetraExtra.corsario to 19f
    TemaId.URSS -> LetraExtra.russo to 12f
    TemaId.REVOLUCION -> LetraExtra.cormorant to 14f
    TemaId.ANTIGUAS -> LetraExtra.almendra to 13f
    TemaId.CUEVA -> LetraExtra.abrigo to 13.5f
}

/** Encabezado de la página de un sueño: la luna y las estrellas del tema, su rótulo y la noche en que se soñó. */
@Composable
private fun EncabezadoSueno(t: TemaId, p: Pagina, fecha: String) {
    val ty = MaterialTheme.typography
    val n = nocheDe(t)
    val (letra, tam) = letraSueno(t)
    val antes = runCatching { Fechas.parse(fecha).minusDays(1) }.getOrNull()
    Column(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(width = 120.dp, height = 46.dp)) {
            estrellasDelTema(t, fecha.hashCode(), 14, n.luna, max = 1.4f)
            lunaDelTema(t, Offset(size.width * 0.62f, size.height / 2), size.height * 0.33f, n)
        }
        Text(rotuloSueno(t), fontFamily = letra, fontSize = tam.sp, letterSpacing = 1.5.sp, color = p.acento, textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp))
        Text(Estilo.fecha(fecha), style = ty.headlineSmall, color = p.tinta, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
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
                modifier = Modifier.padding(top = 4.dp).clickable {
                    sentirEp(Sentidos.Evento.CAMBIO)
                    val antes = Repo.config.frases_guardadas.filter { it.texto == f.texto && it.autor == f.autor }
                    Frases.quitar(f); armada = null
                    Deshacer.ofrecer("Frase quitada") { Repo.cambiarConfig { c -> c.copy(frases_guardadas = c.frases_guardadas + antes.filter { a -> c.frases_guardadas.none { it.texto == a.texto && it.autor == a.autor } }) } }
                }.padding(horizontal = 8.dp, vertical = 6.dp))
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
                BarraPagina(p, volver, if (d != null) "${porNumero(Texto.palabras(cuerpo), "1 palabra", "${Texto.palabras(cuerpo)} palabras")}" else "") {
                    Text("‹", fontSize = 24.sp, color = if (anterior != null) p.acento else p.tenue.copy(alpha = 0.3f),
                        modifier = Modifier.clickable(enabled = anterior != null) { sentirPagina(Sentidos.Evento.PASAR); anterior?.invoke() }.padding(horizontal = 10.dp))
                    Text("›", fontSize = 24.sp, color = if (siguiente != null) p.acento else p.tenue.copy(alpha = 0.3f),
                        modifier = Modifier.clickable(enabled = siguiente != null) { sentirPagina(Sentidos.Evento.PASAR); siguiente?.invoke() }.padding(horizontal = 10.dp))
                    Box {
                        Text("⋯", fontSize = 22.sp, color = p.acento, modifier = Modifier.clickable { menu = true }.padding(horizontal = 10.dp))
                        Menu(expanded = menu, onDismissRequest = { menu = false }, containerColor = p.color) {
                            acciones.forEach { (t, f) -> OpcionMenu(text = { Text(t, color = p.tinta) }, onClick = { menu = false; f() }) }
                        }
                    }
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = if (p.rayas == Rayas.NUMEROS) 52.dp else 30.dp, end = 26.dp, bottom = 40.dp)) {
                    EncabezadoPagina(tema, p, fecha, modo)
                    if (!sueno) Epigrafe(fecha, p)
                    if (d == null || cuerpo.isBlank()) {
                        Text(if (sueno) textoSinSueno(tema) else textoVacio(tema), color = p.tenue, fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp)
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
                        val (mejor, peor) = mejorPeor(tema)
                        if (d.mejor_momento.isNotBlank() || d.peor_momento.isNotBlank()) Column(Modifier.padding(top = 22.dp)) {
                            if (d.mejor_momento.isNotBlank()) Text(mejor + d.mejor_momento, color = p.tinta, fontFamily = p.texto, fontSize = (p.tam - 2).sp, lineHeight = (p.interlinea - 3).sp)
                            if (d.peor_momento.isNotBlank()) Text(peor + d.peor_momento, color = p.tinta, fontFamily = p.texto, fontSize = (p.tam - 2).sp, lineHeight = (p.interlinea - 3).sp, modifier = Modifier.padding(top = 6.dp))
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
                 modo: Escrito = Escrito.DIARIO, encabezado: (@Composable (Pagina) -> Unit)? = null, pista: String? = null, textoBoton: String? = null,
                 pie: (@Composable (Pagina) -> Unit)? = null) {
    val p = when (modo) { Escrito.SUENO -> paginaNoche(tema); Escrito.IDEA -> paginaFicha(tema); else -> pagina(tema) }
    val ficha = modo == Escrito.IDEA
    val diseno = io.github.ruddy2003.vestigium.ui.ideario.disenoIdeas(tema)
    // Antes de cambiar al esquema del papel: si la app está de noche.
    val oscuroApp = esOscuro()
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
            if (ficha) io.github.ruddy2003.vestigium.ui.ideario.InteriorIdeas(oscuroApp) else FondoPagina(tema, p, noche = modo == Escrito.SUENO)
            // Lo de adentro de la página: la barra, el texto y la fila de abajo. En una idea va dentro de una ficha grande del tema.
            val interior: @Composable ColumnScope.() -> Unit = {
                if (ficha && diseno.barra) Spacer(Modifier.height(diseno.altoBarra(true)))
                else if (ficha) Spacer(Modifier.height(diseno.margenes.arribaGrande / 2))
                BarraPagina(p, guardar, Texto.palabras(campo.text).let { n -> Estilo.texto("palabras").format(n).let { if (n == 1) it.replace(" palabras", " palabra") else it } }) {
                    Text(textoBoton ?: textoGuardar(tema), style = MaterialTheme.typography.labelLarge, color = if (p.oscura) p.color else Color.White,
                        modifier = Modifier.padding(end = 6.dp).background(p.acento, MaterialTheme.shapes.small).clickable { sentir(Sentidos.Evento.GUARDAR); guardar() }
                            .padding(horizontal = 14.dp, vertical = 8.dp))
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = if (p.rayas == Rayas.NUMEROS) 52.dp else 30.dp, end = 26.dp, bottom = 24.dp)) {
                    if (encabezado != null) encabezado(p) else EncabezadoPagina(tema, p, fecha, modo)
                    if (modo == Escrito.DIARIO) Epigrafe(fecha, p)
                    // La línea de color de la ficha, bajo el encabezado.
                    if (ficha) Box(Modifier.fillMaxWidth().padding(bottom = 6.dp).height(1.6.dp).background(p.acento.copy(alpha = 0.7f)))
                    val estilo = TextStyle(fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, color = p.tinta)
                    BasicTextField(
                        value = campo, onValueChange = { campo = it; if (it.text != valor) onCambio(it.text) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 420.dp).rayado(p, layout),
                        textStyle = estilo, cursorBrush = SolidColor(p.acento), onTextLayout = { layout = it },
                        visualTransformation = rememberMarcas(p.capitular?.let { Capitular(it, p.acento) } ?: VisualTransformation.None),
                        decorationBox = { interior ->
                            Box {
                                if (campo.text.isEmpty()) Text(pista ?: if (modo == Escrito.SUENO) pistaSonar(tema) else pistaEscribir(tema), style = estilo.copy(color = p.tenue.copy(alpha = 0.7f)))
                                interior()
                            }
                        },
                    )
                    if (pie != null) pie(p)
                }
                // Abajo, junto al teclado, también está «guardar»: con el texto largo, el de arriba queda lejos del pulgar (N-27).
                FlowRow(Modifier.fillMaxWidth().background(p.color.copy(alpha = 0.92f)).padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(textoBoton ?: textoGuardar(tema), style = MaterialTheme.typography.labelLarge, color = if (p.oscura) p.color else Color.White,
                        modifier = Modifier.background(p.acento, MaterialTheme.shapes.small).clickable { sentir(Sentidos.Evento.GUARDAR); guardar() }
                            .padding(horizontal = 14.dp, vertical = 8.dp))
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
            // Arriba y a los lados, la barra del sistema; abajo, el teclado del teléfono o la barra de navegación (sin contarlas dos veces).
            if (!ficha) Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)).imePadding()) {
                interior()
                androidx.compose.foundation.layout.Spacer(Modifier.navigationBarsPadding())
            } else Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)).imePadding()) {
                // La ficha grande, con la forma de las del tema, sobre el interior del contenedor.
                val forma = remember(tema) { diseno.forma(fecha.hashCode()) }
                androidx.compose.material3.Surface(shape = forma, color = Color.Transparent, shadowElevation = 6.dp,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp)) {
                    Box {
                        PapelFicha(p)
                        Canvas(Modifier.matchParentSize()) { with(diseno) { adornoFicha(p, true, fecha.hashCode()) } }
                        Column(Modifier.fillMaxSize()) { interior() }
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
    var texto by remember(fecha) { mutableStateOf(Repo.dia(fecha).sueno_texto) }
    var sucio by remember(fecha) { mutableStateOf(false) }
    // Solo se escribe este campo sobre el día tal como está ahora: si algo más del día cambió mientras escribías (la
    // hoja de la noche, un widget), no se pierde. Antes se guardaba el día entero que se cargó al abrir.
    fun guardar() { val hoyDia = Repo.dia(fecha); Repo.guardarDia(hoyDia.copy(sueno_texto = texto, sueno_recordado = hoyDia.sueno_recordado || texto.isNotBlank())); sucio = false }
    val guardarAlCerrar by rememberUpdatedState { if (sucio) guardar() }
    LaunchedEffect(texto, sucio) { if (sucio) { delay(800); guardar() } }
    // También si la pantalla se cierra sin pasar por «cerrar» (atrás del sistema, otra pantalla encima).
    DisposableEffect(fecha) { onDispose { guardarAlCerrar() } }
    DialogoEscritor(fecha, texto, { texto = it; sucio = true }, Escrito.SUENO) { if (sucio) guardar(); cerrar() }
}

/** Escribir el diario de un día desde fuera de la hoja de la noche: carga el día, guarda mientras escribes y al cerrar. */
@Composable
fun EscribirDia(fecha: String, cerrar: () -> Unit) {
    var texto by remember(fecha) { mutableStateOf(Repo.dia(fecha).nota) }
    var sucio by remember(fecha) { mutableStateOf(false) }
    // Solo la nota, sobre el día tal como está ahora (ver EscribirSueno).
    fun guardar() { Repo.guardarDia(Repo.dia(fecha).copy(nota = texto)); sucio = false }
    val guardarAlCerrar by rememberUpdatedState { if (sucio) guardar() }
    LaunchedEffect(texto, sucio) { if (sucio) { delay(800); guardar() } }
    DisposableEffect(fecha) { onDispose { guardarAlCerrar() } }
    DialogoEscritor(fecha, texto, { texto = it; sucio = true }) { if (sucio) guardar(); cerrar() }
}
