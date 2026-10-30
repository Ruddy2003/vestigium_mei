package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
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
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ruddy2003.vestigium.data.Carta
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.alFuturo
import io.github.ruddy2003.vestigium.data.lista
import io.github.ruddy2003.vestigium.data.sellada
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Cartas: al futuro (selladas hasta su fecha) y a otras personas
// =====================================================================

/** Cómo está el sobre: cerrado y atado con su sello, con el sello brillando porque ya llegó su fecha, o abierto. */
enum class EstadoSobre { SELLADA, LISTA, ABIERTA }

fun estadoSobre(c: Carta, hoy: String = Fechas.diaLogico()): EstadoSobre = when {
    c.sellada(hoy) -> EstadoSobre.SELLADA
    c.lista(hoy) -> EstadoSobre.LISTA
    else -> EstadoSobre.ABIERTA
}

/** La papelería de cada tema: el sobre, su forro, el sello ([lacre]), el cierre ([cordon]) y la tinta con que se rotula. */
class Papeleria(val sobre: Color, val sombra: Color, val brillo: Color, val forro: Color, val lacre: Color, val cordon: Color, val tinta: Color)

/** La papelería propia de cada tema: de qué es el sobre, de qué color es por dentro, con qué se sella y con qué se ata. */
fun papeleria(t: TemaId): Papeleria = when (t) {
    // Papiro grueso forrado de pórfido, cordón de lino y cera roja para el anillo.
    TemaId.ROMANO -> Papeleria(Color(0xFFE4D0A0), Color(0xFFAE8A52), Color(0xFFF3E4BE), Color(0xFF7C2433), Color(0xFF8E2B1F), Color(0xFFBFA77A), Color(0xFF2A1E14))
    // Cartolina de cartulina crema, forrada del ocre de las fachadas, con el franqueo rojo y el cordel de bramante.
    TemaId.URBE -> Papeleria(Color(0xFFF1E4C8), Color(0xFFC4AC7E), Color(0xFFFBF3E0), Color(0xFFC98A3A), Color(0xFFA8432A), Color(0xFFB59A6A), Color(0xFF2B2420))
    // Un paquete de datos: grafito con la rejilla cian por dentro, el chip en cian y el precinto en cian más tenue.
    TemaId.CIBER -> Papeleria(Color(0xFF0B121C), Color(0xFF05080E), Color(0xFF15202E), Color(0xFF06282F), Color(0xFF00E5FF), Color(0xFF5FC4D2), Color(0xFFBDEFF5))
    // La bolsa de entrega de plástico violeta, forrada de magenta, con la etiqueta adhesiva y la cinta ámbar.
    TemaId.NEON -> Papeleria(Color(0xFF2A1C33), Color(0xFF140D1A), Color(0xFF3D2A48), Color(0xFF3A0D27), Color(0xFFFF3D9A), Color(0xFFFFB238), Color(0xFFF4E7F0))
    // Papiro claro forrado de púrpura macedonio, hilo negro y sello de arcilla.
    TemaId.HELENO -> Papeleria(Color(0xFFE9D7A8), Color(0xFFB5935E), Color(0xFFF5E8C6), Color(0xFF5B2A6E), Color(0xFFB5652F), Color(0xFF2B1D14), Color(0xFF1F1712))
    // Papiro doblado en tiras, forrado de lapislázuli, atado con hilo de lino y sellado con barro del Nilo.
    TemaId.EGIPTO -> Papeleria(Color(0xFFE6D29A), Color(0xFFA88C4E), Color(0xFFF4E7BF), Color(0xFF1D2F7A), Color(0xFF6E4A2E), Color(0xFFCDBB8E), Color(0xFF1E160C))
    // Pergamino doblado con la cinta azul y el sello rojo que cuelga.
    TemaId.MEDIEVAL -> Papeleria(Color(0xFFEADBB4), Color(0xFFB79C6A), Color(0xFFF6EBCB), Color(0xFFC9B07A), Color(0xFFB3261E), Color(0xFF1F3A93), Color(0xFF2B1D12))
    // Lino blanco de la sobreveste forrado de rojo, cera parda del sello de la casa y cordón de cáñamo.
    TemaId.TEMPLARIO -> Papeleria(Color(0xFFF1ECE0), Color(0xFFB9AE97), Color(0xFFFFFCF4), Color(0xFFB0191F), Color(0xFF7A2A16), Color(0xFF8A7650), Color(0xFF201E1B))
    // Sobre de manila de oficina, con su hilo y botones, y la tinta roja del sello de goma.
    TemaId.DISTOPIA -> Papeleria(Color(0xFFD8C08A), Color(0xFFA88F58), Color(0xFFE8D4A4), Color(0xFFB9A26E), Color(0xFFC8102E), Color(0xFF3A3A3A), Color(0xFF141414))
    // Crema del optimismo atómico, forro verde azulado, la estampa amarilla y la banda naranja.
    TemaId.REFUGIO -> Papeleria(Color(0xFFF1E8CF), Color(0xFFB8AC88), Color(0xFFFFF8E6), Color(0xFF1C6E6A), Color(0xFFE8B83A), Color(0xFFD9572B), Color(0xFF1F2A2A))
    // Papel gris acero, forro carmesí, la placa de latón y la cinta carmesí.
    TemaId.ORDEN -> Papeleria(Color(0xFFC9CDD1), Color(0xFF8A9096), Color(0xFFE2E5E8), Color(0xFF9E1B22), Color(0xFFD6B25A), Color(0xFF9E1B22), Color(0xFF1C2127))
    // El sobre de telegrama de papel de estraza, forrado de cardenillo, con lacre de ladrillo y el cordel de latón.
    TemaId.VAPOR -> Papeleria(Color(0xFFD8C08E), Color(0xFFA88E5C), Color(0xFFEBDDB8), Color(0xFF2F6B5E), Color(0xFF9C3D24), Color(0xFFB8862B), Color(0xFF231B14))
    // Blanco clínico con forro cian pálido, la etiqueta cian y el hilo violeta.
    TemaId.LABORATORIO -> Papeleria(Color(0xFFF7FBFC), Color(0xFFB9C6CF), Color(0xFFFFFFFF), Color(0xFFD8F3F7), Color(0xFF0B8FA1), Color(0xFF5E4FD1), Color(0xFF1B2733))
    // Papel de gaceta, forro azul marino, la oblea de ladrillo y el bramante.
    TemaId.COLONOS -> Papeleria(Color(0xFFEDE4CC), Color(0xFFB5A684), Color(0xFFF7F0DC), Color(0xFF3E4B60), Color(0xFF9E2B25), Color(0xFF8A6A44), Color(0xFF1E1A16))
    // Papel de despacho, forro azul imperial, el lacre carmesí del águila y la banda de oro.
    TemaId.NAPOLEON -> Papeleria(Color(0xFFF3EAD2), Color(0xFFC9B48A), Color(0xFFFBF5E6), Color(0xFF1E3A8A), Color(0xFFA3172E), Color(0xFFD9B25B), Color(0xFF14213D))
    // Sobre de laca negra forrado de oro, el lacre de jade y la cinta dorada de los grandes almacenes; la tinta, marfil.
    TemaId.DECO -> Papeleria(Color(0xFF1C1A16), Color(0xFF0E0D0B), Color(0xFF38332B), Color(0xFFD9B44A), Color(0xFF1F6B57), Color(0xFFC9A54A), Color(0xFFF0E6CC))
    // Papel de trapo, forro de musgo, la cera verde en flor y la rafia.
    TemaId.BOTANICO -> Papeleria(Color(0xFFE9E2C6), Color(0xFFAFA27C), Color(0xFFF5F0DC), Color(0xFF6E7A4E), Color(0xFF3F5A2C), Color(0xFFC9B27A), Color(0xFF2A2318))
    // Cartón gris recortado de una caja, forro de hiedra, una chapa oxidada por sello y cordel de nailon viejo.
    TemaId.RUINAS -> Papeleria(Color(0xFFC9C2AE), Color(0xFF8E8775), Color(0xFFDCD6C4), Color(0xFF3E6B2A), Color(0xFFA34A1E), Color(0xFF8A7F68), Color(0xFF1F2420))
    // Rojo de regalo, forro verde pino, el moño y la cinta de oro.
    TemaId.NAVIDAD -> Papeleria(Color(0xFFB71C1C), Color(0xFF7A1010), Color(0xFFD84343), Color(0xFF1B5E20), Color(0xFFD4AF37), Color(0xFFD4AF37), Color(0xFFFBF6EC))
    // Papel de arroz, forro de lapislázuli, el sello de cinabrio y la faja azul.
    TemaId.SEDA -> Papeleria(Color(0xFFF2EAD3), Color(0xFFB9B29F), Color(0xFFFBF7EA), Color(0xFF233E8C), Color(0xFFC0392B), Color(0xFF2B4A9E), Color(0xFF1F1A14))
    // Washi forrado de índigo, el cordón de mizuhiki y el bermellón del sello; tinta sumi.
    TemaId.EDO -> Papeleria(Color(0xFFF1EADB), Color(0xFFC2B79F), Color(0xFFFBF8F0), Color(0xFF1E3A5F), Color(0xFFC9442E), Color(0xFFB8322A), Color(0xFF1B1A1E))
    // Azul de bóveda forrado de rubí, la mandorla de cera roja y la cinta de oro.
    TemaId.GOTICO -> Papeleria(Color(0xFF1F2F7A), Color(0xFF0E1640), Color(0xFF3B4FA8), Color(0xFFB3122E), Color(0xFFB3122E), Color(0xFFE2B34A), Color(0xFFF3EBD6))
    // Sobre de archivo de papel manila forrado de verde, lacre verdinegro y bramante.
    TemaId.ARCANO -> Papeleria(Color(0xFFC9B88A), Color(0xFF8E7E57), Color(0xFFE3D6AC), Color(0xFF2E4A3E), Color(0xFF2C4034), Color(0xFF8A7A58), Color(0xFF1E3329))
    // Estuche de metal claro con interior negro, la cápsula con su luz naranja y la cinta azul.
    TemaId.ESPACIO -> Papeleria(Color(0xFFDDE3EC), Color(0xFF8E99AB), Color(0xFFF6F8FB), Color(0xFF0A1022), Color(0xFFFFB86B), Color(0xFF8AB4FF), Color(0xFF0A1022))
    // Bolsa de hule encerado azul petróleo, forrada de lona, sellada con latón de escafandra y atada con cabo de manila.
    TemaId.ABISMO -> Papeleria(Color(0xFF2C5A6B), Color(0xFF15333F), Color(0xFF4D8597), Color(0xFFE6DFC6), Color(0xFFB8913E), Color(0xFFCDB88C), Color(0xFFE4EEEA))
    // Papel de Xàtiva, forro de cuero, la estrella verde y el cordón de oro.
    TemaId.ANDALUS -> Papeleria(Color(0xFFF3E7CC), Color(0xFFBFA77A), Color(0xFFFBF3DE), Color(0xFF7A3B1E), Color(0xFF1E6B5C), Color(0xFFD9B25B), Color(0xFF2A1A12))
    // Papel amate de corteza, forro de azul maya, el sello de barro con cinabrio y el cordel de henequén.
    TemaId.MESO -> Papeleria(Color(0xFFD9C29A), Color(0xFF9C8058), Color(0xFFEBDBBA), Color(0xFF3E8FA8), Color(0xFFA3301E), Color(0xFFC9B27E), Color(0xFF2A1810))
    // Fieltro crema, forro de cielo azul, la paiza de oro y la bufanda azul.
    TemaId.MONGOL -> Papeleria(Color(0xFFEDE6D3), Color(0xFFB5AC92), Color(0xFFF8F3E6), Color(0xFF1F5FA8), Color(0xFFC48A1E), Color(0xFF2E6DB4), Color(0xFF18202E))
    // Papel del cuaderno de taller, forro de cartón, la cera de sanguina y el cordel sepia.
    TemaId.RENACIMIENTO -> Papeleria(Color(0xFFEEDFBC), Color(0xFFB89466), Color(0xFFF8EDD2), Color(0xFF9A7A50), Color(0xFF8C3B1E), Color(0xFF5A3A1C), Color(0xFF3A2614))
    // Pergamino húmedo de carta náutica, forro del verde del mar de las cartas, lacre de brea y cabo de cáñamo.
    TemaId.PIRATA -> Papeleria(Color(0xFFE2D6B4), Color(0xFFA8956A), Color(0xFFF1E8CF), Color(0xFF2F5D62), Color(0xFF2B2622), Color(0xFFA88A55), Color(0xFF2A1C0F))
    // Papel de estraza, forro rojo, el matasellos rojo y la cinta negra.
    TemaId.URSS -> Papeleria(Color(0xFFE3D9C3), Color(0xFFA89C82), Color(0xFFF0E8D6), Color(0xFFB71C1C), Color(0xFFC1121F), Color(0xFF161414), Color(0xFF161414))
    // Papel de cartel, forro azul, la escarapela y el cordón azul.
    TemaId.REVOLUCION -> Papeleria(Color(0xFFF2ECDD), Color(0xFFB8AE98), Color(0xFFFBF7EE), Color(0xFF1F3A6E), Color(0xFFB22234), Color(0xFF2F4A7A), Color(0xFF1C1A17))
    // Papel de mapa viejo, forro de cuero, la gema verde y la correa de cuero.
    TemaId.ANTIGUAS -> Papeleria(Color(0xFFE3D5B4), Color(0xFFA8946A), Color(0xFFF1E6C8), Color(0xFF5A3A22), Color(0xFF2E7A5A), Color(0xFF5A3A22), Color(0xFF1E1D1A))
    // Piel de ante curtida, el revés más claro, un disco de ocre por sello y tendón por cordón.
    TemaId.CUEVA -> Papeleria(Color(0xFFCDB089), Color(0xFF8C6B48), Color(0xFFE6D2AE), Color(0xFFE8DCC4), Color(0xFFA2401F), Color(0xFFD9C29A), Color(0xFF2A211B))
}

// =====================================================================
//  La forma del sobre: solapa, esquinas, cierre y adorno de cada tema
// =====================================================================

/** El perfil del borde de la solapa, de una esquina a la otra: en pico, redondo, recto, en ondas, en ojiva… */
internal enum class SolapaSobre { PICO, REDONDA, RECTA, ONDAS, LENGUA, BISEL, OJIVA, DIENTES, PARABOLA, ESCALON, CORONA, RASGADA, ALMENA }

/** Con qué se ata el sobre sellado: cordón en cruz, dos vueltas, cinta vertical, faja, hilo y botones o precinto. */
internal enum class CierreSobre { CRUZ, DOBLE, VERTICAL, BANDA, HILO, PRECINTO }

/** La forma del sobre de un tema: el perfil de la solapa, el redondeo de las esquinas (en fracción del alto) y el cierre. */
internal class FormaSobre(val solapa: SolapaSobre, val radio: Float, val cierre: CierreSobre)

internal fun formaSobre(t: TemaId): FormaSobre = when (t) {
    TemaId.ROMANO -> FormaSobre(SolapaSobre.PICO, 0.03f, CierreSobre.CRUZ)
    TemaId.URBE -> FormaSobre(SolapaSobre.RECTA, 0.03f, CierreSobre.VERTICAL)
    TemaId.CIBER -> FormaSobre(SolapaSobre.RECTA, 0f, CierreSobre.PRECINTO)
    TemaId.NEON -> FormaSobre(SolapaSobre.DIENTES, 0.05f, CierreSobre.BANDA)
    TemaId.HELENO -> FormaSobre(SolapaSobre.REDONDA, 0.03f, CierreSobre.DOBLE)
    TemaId.EGIPTO -> FormaSobre(SolapaSobre.RECTA, 0.035f, CierreSobre.VERTICAL)
    TemaId.MEDIEVAL -> FormaSobre(SolapaSobre.LENGUA, 0.015f, CierreSobre.VERTICAL)
    TemaId.TEMPLARIO -> FormaSobre(SolapaSobre.ALMENA, 0.01f, CierreSobre.CRUZ)
    TemaId.DISTOPIA -> FormaSobre(SolapaSobre.RECTA, 0.01f, CierreSobre.HILO)
    TemaId.REFUGIO -> FormaSobre(SolapaSobre.REDONDA, 0.08f, CierreSobre.BANDA)
    TemaId.ORDEN -> FormaSobre(SolapaSobre.BISEL, 0.01f, CierreSobre.CRUZ)
    TemaId.VAPOR -> FormaSobre(SolapaSobre.ESCALON, 0.02f, CierreSobre.PRECINTO)
    TemaId.LABORATORIO -> FormaSobre(SolapaSobre.PARABOLA, 0.05f, CierreSobre.HILO)
    TemaId.COLONOS -> FormaSobre(SolapaSobre.PICO, 0.02f, CierreSobre.DOBLE)
    TemaId.NAPOLEON -> FormaSobre(SolapaSobre.PICO, 0.025f, CierreSobre.BANDA)
    TemaId.DECO -> FormaSobre(SolapaSobre.CORONA, 0.008f, CierreSobre.VERTICAL)
    TemaId.BOTANICO -> FormaSobre(SolapaSobre.ONDAS, 0.04f, CierreSobre.CRUZ)
    TemaId.RUINAS -> FormaSobre(SolapaSobre.RASGADA, 0.005f, CierreSobre.HILO)
    TemaId.NAVIDAD -> FormaSobre(SolapaSobre.DIENTES, 0.04f, CierreSobre.CRUZ)
    TemaId.SEDA -> FormaSobre(SolapaSobre.RECTA, 0.02f, CierreSobre.BANDA)
    TemaId.EDO -> FormaSobre(SolapaSobre.BISEL, 0.005f, CierreSobre.BANDA)
    TemaId.GOTICO -> FormaSobre(SolapaSobre.OJIVA, 0.015f, CierreSobre.VERTICAL)
    TemaId.ARCANO -> FormaSobre(SolapaSobre.PICO, 0.01f, CierreSobre.HILO)
    TemaId.ESPACIO -> FormaSobre(SolapaSobre.REDONDA, 0.08f, CierreSobre.PRECINTO)
    TemaId.ABISMO -> FormaSobre(SolapaSobre.ONDAS, 0.06f, CierreSobre.BANDA)
    TemaId.ANDALUS -> FormaSobre(SolapaSobre.ESCALON, 0.03f, CierreSobre.DOBLE)
    TemaId.MESO -> FormaSobre(SolapaSobre.ESCALON, 0.01f, CierreSobre.CRUZ)      // el perfil de la pirámide; el códice atado en cruz
    TemaId.MONGOL -> FormaSobre(SolapaSobre.PARABOLA, 0.06f, CierreSobre.CRUZ)
    TemaId.RENACIMIENTO -> FormaSobre(SolapaSobre.BISEL, 0.02f, CierreSobre.DOBLE)
    TemaId.PIRATA -> FormaSobre(SolapaSobre.ONDAS, 0.012f, CierreSobre.DOBLE)
    TemaId.URSS -> FormaSobre(SolapaSobre.PICO, 0f, CierreSobre.PRECINTO)
    TemaId.REVOLUCION -> FormaSobre(SolapaSobre.LENGUA, 0.01f, CierreSobre.CRUZ)
    TemaId.ANTIGUAS -> FormaSobre(SolapaSobre.RASGADA, 0.02f, CierreSobre.CRUZ)
    TemaId.CUEVA -> FormaSobre(SolapaSobre.PARABOLA, 0.06f, CierreSobre.DOBLE)      // la piel doblada y atada con dos vueltas de tendón
}

/** Qué tan abajo llega el borde de la solapa en [u] (0 a 1, de una esquina a la otra): 0 en las esquinas y 1 al centro. */
private fun perfilSolapa(s: SolapaSobre, u: Float): Float {
    val p = 1f - abs(2f * u - 1f)
    val borde = (minOf(u, 1f - u) / 0.05f).coerceAtMost(1f)
    return when (s) {
        SolapaSobre.PICO -> p
        SolapaSobre.REDONDA -> sqrt(1f - (1f - p) * (1f - p))
        SolapaSobre.RECTA -> borde
        SolapaSobre.ONDAS -> borde * (0.86f + 0.14f * abs(cos((u - 0.5f) * PI.toFloat() * 5f)))
        SolapaSobre.LENGUA -> p.pow(1.8f)
        SolapaSobre.BISEL -> (p * 2.4f).coerceAtMost(1f)
        SolapaSobre.OJIVA -> p.pow(0.45f)
        SolapaSobre.DIENTES -> { val x = (u - 0.5f) * 8f; borde * (0.82f + 0.18f * (1f - 2f * abs(x - round(x)))) }
        SolapaSobre.PARABOLA -> 1f - (2f * u - 1f) * (2f * u - 1f)
        SolapaSobre.ESCALON -> ceil(p * 4f) / 4f
        // La corona de un rascacielos: tres retranqueos simétricos y, al centro, la aguja que baja hasta la punta.
        SolapaSobre.CORONA -> if (p > 0.92f) 1f else ceil(p * 3f) / 3f * 0.78f
        SolapaSobre.RASGADA -> ((p * 1.6f).coerceAtMost(1f) + 0.05f * sin(u * 37f) * sin(u * PI.toFloat())).coerceIn(0f, 1f)
        // El borde de la solapa en almenas: merlones y troneras alternados.
        SolapaSobre.ALMENA -> borde * (if ((u * 9f).toInt() % 2 == 0) 1f else 0.8f)
    }
}

/** La solapa: de la esquina izquierda a la derecha por el borde de arriba y de vuelta por su perfil, que llega hasta [puntaY]. */
private fun rutaSolapa(s: SolapaSobre, l: Float, r: Float, top: Float, puntaY: Float): Path = Path().apply {
    moveTo(l, top)
    for (i in 1..63) { val u = i / 64f; lineTo(l + (r - l) * u, top + (puntaY - top) * perfilSolapa(s, u)) }
    lineTo(r, top)
    close()
}

/** Una estrella de [puntas] puntas (o un sol dentado, si [r] es casi [R]). */
private fun estrellaRuta(c: Offset, R: Float, r: Float, puntas: Int, giro: Double = -PI / 2): Path = Path().apply {
    for (k in 0 until puntas * 2) {
        val a = giro + k * PI / puntas
        val rr = if (k % 2 == 0) R else r
        val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
        if (k == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Una gota de cera de borde irregular (la [semilla] cambia las ondas), con su sombra y su brillo. */
private fun DrawScope.gotaCera(c: Offset, r: Float, color: Color, semilla: Int) {
    val borde = Path()
    for (i in 0..40) {
        val a = 2 * PI * i / 40
        val rr = r * (1f + 0.07f * sin(a * 5 + semilla).toFloat() + 0.03f * sin(a * 11 + semilla * 2).toFloat())
        val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
        if (i == 0) borde.moveTo(p.x, p.y) else borde.lineTo(p.x, p.y)
    }
    borde.close()
    drawPath(borde, Color.Black.copy(alpha = 0.18f * color.alpha), style = Stroke(r * 0.12f))
    drawPath(borde, color)
    drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.28f * color.alpha), Color.Transparent), center = c - Offset(r * 0.35f, r * 0.35f), radius = r * 0.8f), r, c)
}

/** El emblema del tema en relieve, en un cuadrado de lado 2·[k] centrado en [c]. */
private fun DrawScope.emblemaEn(t: TemaId, c: Offset, k: Float, e: ColorScheme) =
    inset(c.x - k, c.y - k, size.width - (c.x + k), size.height - (c.y + k)) { dibujarEmblema(t, 0.3f, e, true) }

/** Letras de máquina sobre un sello, centradas en [x] con la base en [y]. */
private fun DrawScope.rotuloSello(texto: String, x: Float, y: Float, tam: Float, color: Color, negrita: Boolean = true) {
    val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toArgb(); textSize = tam; textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, if (negrita) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
    }
    drawContext.canvas.nativeCanvas.drawText(texto, x, y, p)
}

/**
 * El sello de cada tema, centrado en [c] y de radio [r]: el anillo romano, el chip cifrado, la arcilla con el sol, el sello
 * pendiente, el sello de goma, la estampa atómica, la placa remachada, la etiqueta de muestra, la oblea, el águila, la flor de
 * cera, el moño, el cinabrio, la mandorla, la cápsula, la estrella de ocho puntas, la paiza, el compás, el matasellos, la
 * escarapela y la gema en su anillo de hierro. La transparencia de [color] se aplica a todo el sello (al caer, aparece).
 * Se usa en el sobre, al pie de la carta abierta y en el PDF de la carta.
 */
internal fun DrawScope.selloCarta(t: TemaId, c: Offset, r: Float, color: Color, e: ColorScheme) {
    val a = color.alpha
    val base = color.copy(alpha = 1f)
    fun k(x: Color) = x.copy(alpha = x.alpha * a)
    val col = k(base)
    val hondo = k(lerp(base, Color.Black, 0.35f))
    val claro = k(lerp(base, Color.White, 0.55f))
    val sombra = Color.Black.copy(alpha = 0.2f * a)
    when (t) {
        TemaId.ROMANO -> {
            // El sello del anillo: un óvalo de cera más alto que ancho, con el laurel tallado en la piedra.
            val o = Rect(c.x - r * 0.84f, c.y - r * 1.04f, c.x + r * 0.84f, c.y + r * 1.04f)
            drawOval(sombra, o.topLeft + Offset(r * 0.06f, r * 0.1f), o.size)
            drawOval(col, o.topLeft, o.size)
            drawOval(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.3f * a), Color.Transparent), center = c - Offset(r * 0.3f, r * 0.4f), radius = r * 0.9f), o.topLeft, o.size)
            drawOval(hondo, Offset(o.left + r * 0.16f, o.top + r * 0.16f), Size(o.width - r * 0.32f, o.height - r * 0.32f), style = Stroke(r * 0.07f))
            emblemaEn(t, c, r * 0.5f, e)
        }
        TemaId.URBE -> {
            // El francobollo: una estampilla dentada con la placa de la calle por figura, y encima las ondas del matasellos.
            val ancho = r * 1.5f; val alto = r * 1.8f
            val o = Offset(c.x - ancho / 2, c.y - alto / 2)
            drawRect(sombra, o + Offset(r * 0.06f, r * 0.08f), Size(ancho, alto))
            drawRect(k(Color(0xFFFBF3E0)), o, Size(ancho, alto))
            // Los dientes: medios círculos mordidos en las cuatro orillas, del color del papel de abajo.
            val diente = r * 0.07f; val hueco = k(e.surface)
            var x = o.x + diente * 1.5f
            while (x < o.x + ancho - diente) { drawCircle(hueco, diente, Offset(x, o.y)); drawCircle(hueco, diente, Offset(x, o.y + alto)); x += maxOf(diente * 3f, 1f) }
            var y = o.y + diente * 1.5f
            while (y < o.y + alto - diente) { drawCircle(hueco, diente, Offset(o.x, y)); drawCircle(hueco, diente, Offset(o.x + ancho, y)); y += maxOf(diente * 3f, 1f) }
            val dentro = Rect(o.x + r * 0.16f, o.y + r * 0.16f, o.x + ancho - r * 0.16f, o.y + alto - r * 0.16f)
            drawRect(col, dentro.topLeft, dentro.size)
            drawRect(claro, Offset(dentro.left + r * 0.06f, dentro.top + r * 0.06f), Size(dentro.width - r * 0.12f, dentro.height - r * 0.12f), style = Stroke(r * 0.03f))
            emblemaEn(t, Offset(c.x, c.y - r * 0.1f), r * 0.42f, e)
            drawRect(claro, Offset(dentro.left + r * 0.12f, dentro.bottom - r * 0.32f), Size(dentro.width - r * 0.24f, r * 0.14f))
            // El matasellos: cinco ondas que cruzan la estampilla de lado a lado.
            for (j in 0 until 5) {
                val yy = c.y - r * 0.5f + j * r * 0.24f
                val onda = Path().apply {
                    moveTo(o.x - r * 0.3f, yy)
                    var xx = o.x - r * 0.3f
                    while (xx < o.x + ancho + r * 0.3f) { quadraticTo(xx + r * 0.12f, yy - r * 0.08f, xx + r * 0.24f, yy); quadraticTo(xx + r * 0.36f, yy + r * 0.08f, xx + r * 0.48f, yy); xx += maxOf(r * 0.48f, 1f) }
                }
                drawPath(onda, k(Color(0xFF2B2420).copy(alpha = 0.45f)), style = Stroke(r * 0.035f))
            }
        }
        TemaId.CIBER -> {
            // El paquete de datos: un chip con sus patas y el candado; se parte al descifrarlo.
            val s = r * 1.5f; val o = Offset(c.x - s / 2, c.y - s / 2)
            for (i in -2..2) {
                val d = i * s * 0.18f
                drawLine(hondo, Offset(c.x + d, o.y - r * 0.22f), Offset(c.x + d, o.y + s + r * 0.22f), r * 0.08f)
                drawLine(hondo, Offset(o.x - r * 0.22f, c.y + d), Offset(o.x + s + r * 0.22f, c.y + d), r * 0.08f)
            }
            val negro = k(Color(0xFF0B121C))
            drawRoundRect(negro, o, Size(s, s), CornerRadius(r * 0.14f))
            drawRoundRect(col, o, Size(s, s), CornerRadius(r * 0.14f), style = Stroke(r * 0.08f))
            drawArc(col, 180f, 180f, false, Offset(c.x - r * 0.24f, c.y - r * 0.44f), Size(r * 0.48f, r * 0.5f), style = Stroke(r * 0.09f))
            drawRoundRect(col, Offset(c.x - r * 0.34f, c.y - r * 0.2f), Size(r * 0.68f, r * 0.5f), CornerRadius(r * 0.06f))
            drawCircle(negro, r * 0.08f, Offset(c.x, c.y + r * 0.02f))
            drawLine(negro, Offset(c.x, c.y + r * 0.04f), Offset(c.x, c.y + r * 0.18f), r * 0.06f)
        }
        TemaId.NEON -> {
            // La calcomanía redonda del puesto: el disco con su orilla clara, el rayo de «entrega rápida» y el halo.
            drawCircle(k(col.copy(alpha = 0.3f)), r * 1.12f, c)
            drawCircle(col, r * 0.95f, c)
            drawCircle(claro, r * 0.95f, c, style = Stroke(r * 0.07f))
            drawCircle(hondo, r * 0.72f, c, style = Stroke(r * 0.04f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(r * 0.12f, r * 0.08f))))
            drawPath(Path().apply {
                moveTo(c.x + r * 0.12f, c.y - r * 0.55f); lineTo(c.x - r * 0.3f, c.y + r * 0.08f); lineTo(c.x - r * 0.02f, c.y + r * 0.08f)
                lineTo(c.x - r * 0.14f, c.y + r * 0.55f); lineTo(c.x + r * 0.3f, c.y - r * 0.1f); lineTo(c.x + r * 0.02f, c.y - r * 0.1f); close()
            }, k(Color(0xFF1C0010)))
            drawCircle(sombra, r * 0.95f, c + Offset(r * 0.06f, r * 0.08f), style = Stroke(r * 0.04f))
        }
        TemaId.HELENO -> {
            // Arcilla aplastada con el dedo, y el sol de Vergina de dieciséis rayos impreso.
            drawOval(sombra, Offset(c.x - r * 1.0f, c.y - r * 0.82f), Size(r * 2.1f, r * 1.8f))
            drawOval(col, Offset(c.x - r * 1.05f, c.y - r * 0.9f), Size(r * 2.1f, r * 1.8f))
            drawOval(hondo.copy(alpha = hondo.alpha * 0.5f), Offset(c.x - r * 0.88f, c.y - r * 0.74f), Size(r * 1.76f, r * 1.48f), style = Stroke(r * 0.05f))
            for (j in 0 until 16) {
                val ang = j * PI / 8; val largo = if (j % 2 == 0) 0.66f else 0.46f
                drawLine(hondo, Offset(c.x + (r * 0.2f * cos(ang)).toFloat(), c.y + (r * 0.2f * sin(ang)).toFloat()),
                    Offset(c.x + (r * largo * cos(ang)).toFloat(), c.y + (r * largo * sin(ang)).toFloat()), r * 0.07f, StrokeCap.Round)
            }
            drawCircle(hondo, r * 0.15f, c)
        }
        TemaId.EGIPTO -> {
            // Un terrón de barro del Nilo sobre el hilo, con el escarabajo del sello hundido y su borde de cuerda.
            drawPath(Path().apply {
                moveTo(c.x - r * 0.95f, c.y - r * 0.2f); quadraticTo(c.x - r * 0.9f, c.y - r * 0.95f, c.x, c.y - r * 0.92f)
                quadraticTo(c.x + r * 1.0f, c.y - r * 0.85f, c.x + r * 0.96f, c.y + r * 0.1f); quadraticTo(c.x + r * 0.85f, c.y + r * 0.95f, c.x - r * 0.05f, c.y + r * 0.9f)
                quadraticTo(c.x - r * 1.02f, c.y + r * 0.8f, c.x - r * 0.95f, c.y - r * 0.2f); close()
            }.also { drawPath(it, sombra, style = Stroke(r * 0.12f)) }, col)
            drawOval(hondo.copy(alpha = hondo.alpha * 0.7f), Offset(c.x - r * 0.55f, c.y - r * 0.68f), Size(r * 1.1f, r * 1.36f), style = Stroke(r * 0.05f))
            for (j in 0 until 14) {
                val ang = j * PI / 7
                drawCircle(hondo.copy(alpha = hondo.alpha * 0.6f), r * 0.04f, Offset(c.x + (r * 0.68f * cos(ang)).toFloat() * 0.82f, c.y + (r * 0.68f * sin(ang)).toFloat()))
            }
            signoEgipcio(3, c, r * 0.95f, hondo, r * 0.07f)
            drawCircle(claro.copy(alpha = claro.alpha * 0.25f), r * 0.16f, c - Offset(r * 0.42f, r * 0.42f))
        }
        TemaId.MEDIEVAL -> {
            // El sello pendiente: la cera roja cuelga de dos cintas azules, con su gráfila de perlas y el rosetón.
            val cinta = k(Color(0xFF1F3A93))
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply {
                    moveTo(c.x + s * r * 0.1f, c.y); lineTo(c.x + s * r * 0.48f, c.y); lineTo(c.x + s * r * 0.66f, c.y + r * 1.3f)
                    lineTo(c.x + s * r * 0.5f, c.y + r * 1.14f); lineTo(c.x + s * r * 0.34f, c.y + r * 1.32f); close()
                }, cinta)
            }
            gotaCera(c, r, col, 1)
            drawCircle(claro.copy(alpha = claro.alpha * 0.5f), r * 0.8f, c, style = Stroke(r * 0.05f))
            for (j in 0 until 24) { val ang = j * PI / 12; drawCircle(hondo, r * 0.04f, Offset(c.x + (r * 0.88f * cos(ang)).toFloat(), c.y + (r * 0.88f * sin(ang)).toFloat())) }
            emblemaEn(t, c, r * 0.52f, e)
        }
        TemaId.TEMPLARIO -> {
            // El sello de la orden en cera parda: la gráfila de perlas y, al centro, dos caballeros sobre un mismo caballo, con su
            // escudo y sus lanzas (la pobreza de los primeros freires, que compartían montura).
            drawCircle(sombra, r * 0.98f, c + Offset(r * 0.05f, r * 0.08f))
            gotaCera(c, r, col, 3)
            drawCircle(hondo, r * 0.78f, c, style = Stroke(r * 0.04f))
            for (j in 0 until 30) { val ang = j * PI / 15; drawCircle(hondo, r * 0.035f, Offset(c.x + (r * 0.88f * cos(ang)).toFloat(), c.y + (r * 0.88f * sin(ang)).toFloat())) }
            val y0 = c.y + r * 0.12f
            drawOval(hondo, Offset(c.x - r * 0.42f, y0 - r * 0.14f), Size(r * 0.78f, r * 0.28f))
            drawPath(Path().apply { moveTo(c.x + r * 0.26f, y0 - r * 0.08f); lineTo(c.x + r * 0.48f, y0 - r * 0.36f); lineTo(c.x + r * 0.62f, y0 - r * 0.3f); lineTo(c.x + r * 0.42f, y0 + r * 0.04f); close() }, hondo)
            listOf(-0.34f, -0.2f, 0.16f, 0.28f).forEachIndexed { i, fx ->
                drawLine(hondo, Offset(c.x + r * fx, y0 + r * 0.08f), Offset(c.x + r * (fx + if (i % 2 == 0) -0.05f else 0.05f), y0 + r * 0.46f), r * 0.06f, StrokeCap.Round)
            }
            drawLine(hondo, Offset(c.x - r * 0.42f, y0 - r * 0.04f), Offset(c.x - r * 0.56f, y0 + r * 0.26f), r * 0.05f, StrokeCap.Round)
            listOf(-0.16f, 0.08f).forEach { fx ->
                val x = c.x + r * fx
                drawLine(hondo, Offset(x, y0 - r * 0.1f), Offset(x, y0 - r * 0.4f), r * 0.1f, StrokeCap.Round)
                drawCircle(hondo, r * 0.08f, Offset(x, y0 - r * 0.5f))
                drawLine(hondo, Offset(x + r * 0.04f, y0 - r * 0.3f), Offset(x + r * 0.52f, y0 - r * 0.64f), r * 0.03f, StrokeCap.Round)
            }
            drawPath(Path().apply {
                moveTo(c.x - r * 0.32f, y0 - r * 0.38f); lineTo(c.x - r * 0.12f, y0 - r * 0.38f)
                quadraticTo(c.x - r * 0.12f, y0 - r * 0.14f, c.x - r * 0.22f, y0 - r * 0.04f); quadraticTo(c.x - r * 0.32f, y0 - r * 0.14f, c.x - r * 0.32f, y0 - r * 0.38f); close()
            }, claro)
        }
        TemaId.DISTOPIA -> rotate(-14f, c) {
            // El sello de goma del Ministerio: tinta en dos marcos y la palabra, un poco torcido.
            drawRect(k(base.copy(alpha = 0.85f)), Offset(c.x - r * 1.25f, c.y - r * 0.6f), Size(r * 2.5f, r * 1.2f), style = Stroke(r * 0.1f))
            drawRect(k(base.copy(alpha = 0.7f)), Offset(c.x - r * 1.06f, c.y - r * 0.42f), Size(r * 2.12f, r * 0.84f), style = Stroke(r * 0.04f))
            rotuloSello("ARCHIVADO", c.x, c.y + r * 0.13f, r * 0.36f, k(base.copy(alpha = 0.85f)))
        }
        TemaId.REFUGIO -> {
            // La estampa del optimismo atómico: un sol dentado amarillo con el átomo y su núcleo naranja.
            drawPath(estrellaRuta(c + Offset(r * 0.06f, r * 0.08f), r * 1.05f, r * 0.84f, 24), sombra)
            drawPath(estrellaRuta(c, r * 1.05f, r * 0.84f, 24), col)
            val tinta = k(Color(0xFF1F2A2A))
            for (j in 0 until 3) rotate(j * 60f, c) { drawOval(tinta, Offset(c.x - r * 0.62f, c.y - r * 0.22f), Size(r * 1.24f, r * 0.44f), style = Stroke(r * 0.06f)) }
            drawCircle(k(Color(0xFFD9572B)), r * 0.13f, c)
        }
        TemaId.ORDEN -> {
            // La placa hexagonal de latón, remachada, con el engrane de la Orden.
            val hex = Path().apply {
                for (j in 0 until 6) { val ang = j * PI / 3; val x = c.x + (r * cos(ang)).toFloat(); val y = c.y + (r * sin(ang)).toFloat(); if (j == 0) moveTo(x, y) else lineTo(x, y) }
                close()
            }
            translate(r * 0.06f, r * 0.08f) { drawPath(hex, sombra) }
            drawPath(hex, col); drawPath(hex, hondo, style = Stroke(r * 0.06f))
            for (j in 0 until 6) { val ang = j * PI / 3 + PI / 6; drawCircle(hondo, r * 0.06f, Offset(c.x + (r * 0.72f * cos(ang)).toFloat(), c.y + (r * 0.72f * sin(ang)).toFloat())) }
            for (j in 0 until 8) { val ang = j * PI / 4; drawLine(hondo, c, Offset(c.x + (r * 0.5f * cos(ang)).toFloat(), c.y + (r * 0.5f * sin(ang)).toFloat()), r * 0.14f) }
            drawCircle(hondo, r * 0.36f, c); drawCircle(col, r * 0.13f, c)
        }
        TemaId.VAPOR -> {
            // El lacre con el borde dentado de un engrane y, estampada, la rueda motriz de una locomotora: llanta, doce
            // rayos, el cubo y el contrapeso en media luna.
            val canto = Vapor.engrane(c, r, 16, 0.9f)
            translate(r * 0.06f, r * 0.08f) { drawPath(canto, sombra) }
            drawPath(canto, col)
            drawCircle(hondo, r * 0.74f, c, style = Stroke(r * 0.05f))
            drawCircle(hondo, r * 0.58f, c, style = Stroke(r * 0.09f))
            for (j in 0 until 12) { val ang = j * PI / 6; drawLine(hondo, c, Offset(c.x + (r * 0.55f * cos(ang)).toFloat(), c.y + (r * 0.55f * sin(ang)).toFloat()), r * 0.045f) }
            drawArc(hondo, 200f, 140f, true, Offset(c.x - r * 0.52f, c.y - r * 0.52f), Size(r * 1.04f, r * 1.04f))
            drawCircle(hondo, r * 0.14f, c); drawCircle(claro, r * 0.06f, Offset(c.x - r * 0.03f, c.y - r * 0.03f))
            drawCircle(claro, r * 0.07f, Offset(c.x + (r * 0.36f * cos(PI / 3)).toFloat(), c.y + (r * 0.36f * sin(PI / 3)).toFloat()))
        }
        TemaId.LABORATORIO -> {
            // La etiqueta de muestra: blanca, con la franja de color, el código de barras y su número.
            val o = Offset(c.x - r * 1.2f, c.y - r * 0.66f); val s = Size(r * 2.4f, r * 1.32f)
            val tinta = k(Color(0xFF1B2733))
            drawRoundRect(sombra, o + Offset(r * 0.05f, r * 0.08f), s, CornerRadius(r * 0.12f))
            drawRoundRect(k(Color.White), o, s, CornerRadius(r * 0.12f))
            drawRoundRect(tinta.copy(alpha = tinta.alpha * 0.35f), o, s, CornerRadius(r * 0.12f), style = Stroke(r * 0.04f))
            drawRect(col, o, Size(r * 0.34f, s.height))
            val az = java.util.Random(11); var x = o.x + r * 0.55f
            while (x < o.x + s.width - r * 0.2f) {
                val g = r * (0.03f + az.nextFloat() * 0.07f)
                drawRect(tinta, Offset(x, o.y + r * 0.18f), Size(g, r * 0.68f)); x += g + r * (0.04f + az.nextFloat() * 0.05f)
            }
            rotuloSello("Nº 0001", o.x + r * 1.38f, o.y + s.height - r * 0.12f, r * 0.24f, tinta, negrita = false)
        }
        TemaId.COLONOS -> {
            // La oblea: un disco plano de pasta, de borde dentado, con la marca punteada del sello de mano.
            drawPath(estrellaRuta(c + Offset(r * 0.05f, r * 0.07f), r * 0.9f, r * 0.84f, 30), sombra)
            drawPath(estrellaRuta(c, r * 0.9f, r * 0.84f, 30), col)
            for (j in 0 until 20) { val ang = j * PI / 10; drawCircle(claro, r * 0.035f, Offset(c.x + (r * 0.62f * cos(ang)).toFloat(), c.y + (r * 0.62f * sin(ang)).toFloat())) }
            drawLine(claro, Offset(c.x - r * 0.3f, c.y), Offset(c.x + r * 0.3f, c.y), r * 0.06f)
            drawLine(claro, Offset(c.x, c.y - r * 0.3f), Offset(c.x, c.y + r * 0.3f), r * 0.06f)
        }
        TemaId.NAPOLEON -> {
            // El lacre del boletín: el águila imperial de alas abiertas, con su corona.
            gotaCera(c, r, col, 3)
            drawCircle(hondo, r * 0.8f, c, style = Stroke(r * 0.05f))
            val oro = k(Color(0xFFE8C76A)); val u = r * 0.55f
            fun p(x: Float, y: Float) = Offset(c.x + x * u, c.y + y * u)
            listOf(-1f, 1f).forEach { s ->
                val ala = Path().apply {
                    val o = p(0f, -0.15f); moveTo(o.x, o.y)
                    listOf(0.4f to -0.5f, 1f to -0.62f, 0.82f to -0.3f, 0.98f to -0.2f, 0.72f to 0f, 0.86f to 0.1f, 0.3f to 0.2f).forEach { (x, y) -> val q = p(s * x, y); lineTo(q.x, q.y) }
                    close()
                }
                drawPath(ala, oro)
            }
            drawOval(oro, p(-0.18f, -0.3f), Size(0.36f * u, 0.95f * u))
            drawCircle(oro, 0.16f * u, p(0.08f, -0.42f))
            val cola = Path().apply { val a1 = p(-0.22f, 0.55f); val a2 = p(0.22f, 0.55f); val a3 = p(0f, 0.9f); moveTo(a1.x, a1.y); lineTo(a2.x, a2.y); lineTo(a3.x, a3.y); close() }
            drawPath(cola, oro)
            listOf(-0.12f, 0f, 0.12f).forEach { x -> drawCircle(oro, 0.05f * u, p(x + 0.04f, -0.66f)) }
        }
        TemaId.DECO -> {
            // Lacre de jade con el abanico solar en relieve: siete rayos que salen de un medio sol, sobre tres escalones.
            gotaCera(c, r, col, 7)
            drawCircle(hondo, r * 0.8f, c, style = Stroke(r * 0.05f))
            val oro = k(Color(0xFFE6C664)); val u = r * 0.6f
            fun p(x: Float, y: Float) = Offset(c.x + x * u, c.y + y * u)
            val pie = p(0f, 0.3f)
            for (i in 0..6) {
                val ang = PI + (i + 0.5) * PI / 7
                drawLine(oro, pie, Offset(pie.x + 0.95f * u * cos(ang).toFloat(), pie.y + 0.95f * u * sin(ang).toFloat()), r * 0.06f, cap = StrokeCap.Round)
            }
            drawArc(oro, 180f, 180f, true, p(-0.32f, -0.02f), Size(0.64f * u, 0.64f * u))
            drawArc(claro, 180f, 180f, false, p(-1.05f, -0.75f), Size(2.1f * u, 2.1f * u), style = Stroke(r * 0.035f))
            listOf(0.42f to 0.9f, 0.56f to 0.62f, 0.7f to 0.34f).forEach { (y, an) -> drawRect(oro, p(-an / 2, y), Size(an * u, 0.08f * u)) }
        }
        TemaId.BOTANICO -> {
            // Cera verde en forma de flor de cinco lóbulos, con una hoja prensada en relieve.
            val flor = Path().apply {
                for (i in 0..60) {
                    val ang = 2 * PI * i / 60 - PI / 2; val rr = r * (0.84f + 0.16f * cos(5 * ang).toFloat())
                    val x = c.x + (rr * cos(ang)).toFloat(); val y = c.y + (rr * sin(ang)).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            translate(r * 0.05f, r * 0.08f) { drawPath(flor, sombra) }
            drawPath(flor, col)
            val hoja = Path().apply { moveTo(c.x, c.y - r * 0.55f); quadraticTo(c.x + r * 0.36f, c.y, c.x, c.y + r * 0.55f); quadraticTo(c.x - r * 0.36f, c.y, c.x, c.y - r * 0.55f); close() }
            drawPath(hoja, claro.copy(alpha = claro.alpha * 0.5f))
            drawLine(claro, Offset(c.x, c.y - r * 0.5f), Offset(c.x, c.y + r * 0.62f), r * 0.04f)
            for (j in -1..1) {
                val y = c.y + j * r * 0.18f
                drawLine(claro, Offset(c.x, y), Offset(c.x - r * 0.2f, y - r * 0.14f), r * 0.03f)
                drawLine(claro, Offset(c.x, y), Offset(c.x + r * 0.2f, y - r * 0.14f), r * 0.03f)
            }
        }
        TemaId.RUINAS -> {
            // Una chapa de botella oxidada, aplastada como sello: 21 dientes, el disco hundido y una hoja de hiedra estampada.
            val chapa = Path().apply {
                for (i in 0 until 42) {
                    val ang = 2 * PI * i / 42; val rr = if (i % 2 == 0) r else r * 0.87f
                    val x = c.x + (rr * cos(ang)).toFloat(); val y = c.y + (rr * sin(ang)).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            translate(r * 0.05f, r * 0.08f) { drawPath(chapa, sombra) }
            drawPath(chapa, col)
            drawCircle(hondo, r * 0.74f, c, style = Stroke(r * 0.05f))
            drawCircle(hondo.copy(alpha = hondo.alpha * 0.5f), r * 0.16f, Offset(c.x + r * 0.42f, c.y - r * 0.38f))
            drawCircle(hondo.copy(alpha = hondo.alpha * 0.4f), r * 0.1f, Offset(c.x - r * 0.48f, c.y + r * 0.3f))
            val hoja = Path().apply {
                moveTo(c.x, c.y + r * 0.42f); lineTo(c.x - r * 0.24f, c.y + r * 0.1f); lineTo(c.x - r * 0.44f, c.y - r * 0.08f); lineTo(c.x - r * 0.16f, c.y - r * 0.1f)
                lineTo(c.x, c.y - r * 0.46f); lineTo(c.x + r * 0.16f, c.y - r * 0.1f); lineTo(c.x + r * 0.44f, c.y - r * 0.08f); lineTo(c.x + r * 0.24f, c.y + r * 0.1f); close()
            }
            drawPath(hoja, claro)
            drawLine(hondo, Offset(c.x, c.y + r * 0.42f), Offset(c.x, c.y - r * 0.3f), r * 0.035f)
        }
        TemaId.NAVIDAD -> {
            // El moño de regalo: las dos lazadas, las puntas que caen, el nudo y una ramita de acebo encima.
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + s * r * 0.5f, c.y + r * 1.1f); lineTo(c.x + s * r * 0.3f, c.y + r * 0.98f); lineTo(c.x + s * r * 0.18f, c.y + r * 1.12f); close() }, hondo)
                rotate(s * 24f, c) {
                    drawOval(col, Offset(c.x + (if (s < 0) -r * 1.1f else r * 0.05f), c.y - r * 0.36f), Size(r * 1.05f, r * 0.72f))
                    drawOval(hondo, Offset(c.x + (if (s < 0) -r * 0.85f else r * 0.3f), c.y - r * 0.14f), Size(r * 0.5f, r * 0.28f))
                }
            }
            drawCircle(hondo, r * 0.24f, c); drawCircle(col, r * 0.18f, c)
            val verde = k(Color(0xFF1B5E20))
            listOf(-1f, 1f).forEach { s ->
                val b = Offset(c.x + s * r * 0.26f, c.y - r * 0.56f)
                rotate(s * 28f, b) { drawOval(verde, Offset(b.x - r * 0.24f, b.y - r * 0.1f), Size(r * 0.48f, r * 0.2f)) }
            }
            listOf(-0.08f, 0.08f, 0f).forEachIndexed { i, x -> drawCircle(k(Color(0xFFC62828)), r * 0.08f, Offset(c.x + x * r, c.y - r * (if (i == 2) 0.66f else 0.52f))) }
        }
        TemaId.SEDA -> {
            // El sello de cinabrio: cuadrado, con su marco en reserva y unos trazos inventados.
            val s = r * 1.6f; val o = Offset(c.x - s / 2, c.y - s / 2)
            translate(r * 0.04f, r * 0.06f) { drawRoundRect(sombra, o, Size(s, s), CornerRadius(r * 0.08f)) }
            drawRoundRect(col, o, Size(s, s), CornerRadius(r * 0.08f))
            val blanco = k(Color(0xFFF7EEDC)); val g = r * 0.08f
            drawRect(blanco, o + Offset(r * 0.12f, r * 0.12f), Size(s - r * 0.24f, s - r * 0.24f), style = Stroke(r * 0.06f))
            drawLine(blanco, Offset(c.x - r * 0.45f, c.y - r * 0.4f), Offset(c.x - r * 0.45f, c.y + r * 0.45f), g)
            drawLine(blanco, Offset(c.x - r * 0.6f, c.y - r * 0.15f), Offset(c.x - r * 0.08f, c.y - r * 0.15f), g)
            drawRect(blanco, Offset(c.x - r * 0.62f, c.y + r * 0.08f), Size(r * 0.4f, r * 0.3f), style = Stroke(g))
            drawLine(blanco, Offset(c.x + r * 0.1f, c.y - r * 0.45f), Offset(c.x + r * 0.58f, c.y - r * 0.45f), g)
            drawLine(blanco, Offset(c.x + r * 0.34f, c.y - r * 0.45f), Offset(c.x + r * 0.34f, c.y + r * 0.5f), g)
            drawLine(blanco, Offset(c.x + r * 0.1f, c.y + r * 0.05f), Offset(c.x + r * 0.58f, c.y + r * 0.2f), g)
            drawLine(blanco, Offset(c.x + r * 0.12f, c.y + r * 0.42f), Offset(c.x + r * 0.56f, c.y + r * 0.42f), g)
        }
        TemaId.EDO -> {
            // En Japón no se sellaba con cera: se cerraba la carta escribiendo 〆 a pincel sobre la solapa, con un sellito rojo al lado.
            val tinta = k(Color(0xFF1B1A1E)); val g = Stroke(r * 0.17f, cap = StrokeCap.Round)
            drawPath(Path().apply { moveTo(c.x - r * 0.62f, c.y - r * 0.55f); quadraticTo(c.x - r * 0.05f, c.y - r * 0.1f, c.x + r * 0.55f, c.y + r * 0.6f) }, tinta, style = g)
            drawPath(Path().apply { moveTo(c.x + r * 0.5f, c.y - r * 0.62f); quadraticTo(c.x - r * 0.05f, c.y - r * 0.05f, c.x - r * 0.5f, c.y + r * 0.3f); lineTo(c.x - r * 0.28f, c.y + r * 0.62f) }, tinta, style = g)
            val o = Offset(c.x + r * 0.7f, c.y + r * 0.62f)
            translate(r * 0.03f, r * 0.04f) { drawCircle(sombra, r * 0.3f, o) }
            drawCircle(col, r * 0.3f, o); drawCircle(claro, r * 0.22f, o, style = Stroke(r * 0.04f))
            drawLine(claro, Offset(o.x, o.y - r * 0.13f), Offset(o.x, o.y + r * 0.13f), r * 0.05f)
        }
        TemaId.GOTICO -> {
            // El sello de capítulo en forma de mandorla (óvalo con punta), con una crucecita arriba y el rosetón.
            val v = Path().apply { moveTo(c.x, c.y - r * 1.25f); quadraticTo(c.x + r * 1.5f, c.y, c.x, c.y + r * 1.25f); quadraticTo(c.x - r * 1.5f, c.y, c.x, c.y - r * 1.25f); close() }
            translate(r * 0.05f, r * 0.08f) { drawPath(v, sombra) }
            drawPath(v, col)
            val vi = Path().apply { moveTo(c.x, c.y - r * 1.02f); quadraticTo(c.x + r * 1.2f, c.y, c.x, c.y + r * 1.02f); quadraticTo(c.x - r * 1.2f, c.y, c.x, c.y - r * 1.02f); close() }
            drawPath(vi, claro.copy(alpha = claro.alpha * 0.6f), style = Stroke(r * 0.05f))
            drawLine(claro, Offset(c.x, c.y - r * 0.9f), Offset(c.x, c.y - r * 0.6f), r * 0.06f)
            drawLine(claro, Offset(c.x - r * 0.12f, c.y - r * 0.78f), Offset(c.x + r * 0.12f, c.y - r * 0.78f), r * 0.06f)
            emblemaEn(t, Offset(c.x, c.y + r * 0.12f), r * 0.4f, e)
        }
        TemaId.ARCANO -> {
            // Una gota de lacre verdinegro derramada sin cuidado, con dos goterones que se corrieron, y el signo hundido.
            val v = Path()
            for (k in 0..15) {
                val ang = -PI / 2 + k * 2 * PI / 15
                val rr = r * (1f + 0.08f * sin(k * 2.3f) + if (k == 6) 0.32f else if (k == 10) 0.2f else 0f)
                val q = Offset(c.x + (rr * cos(ang)).toFloat(), c.y + (rr * sin(ang)).toFloat())
                if (k == 0) v.moveTo(q.x, q.y) else v.lineTo(q.x, q.y)
            }
            v.close()
            translate(r * 0.05f, r * 0.08f) { drawPath(v, sombra) }
            drawPath(v, col)
            drawCircle(hondo, r * 0.72f, c, style = Stroke(r * 0.06f))
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.25f * a), Color.Transparent), center = c - Offset(r * 0.35f, r * 0.35f), radius = r * 0.7f), r * 0.9f, c)
            with(Contenedores) { signoOjo(c, r * 0.5f, claro, r * 0.05f) }
        }
        TemaId.ESPACIO -> {
            // La cápsula: un estuche de metal redondeado con sus juntas, la mirilla y la luz de estado.
            val o = Offset(c.x - r * 1.25f, c.y - r * 0.6f); val s = Size(r * 2.5f, r * 1.2f)
            drawRoundRect(sombra, o + Offset(r * 0.05f, r * 0.08f), s, CornerRadius(r * 0.6f))
            drawRoundRect(Brush.verticalGradient(listOf(k(Color(0xFFF4F7FB)), k(Color(0xFFAAB5C6))), o.y, o.y + s.height), o, s, CornerRadius(r * 0.6f))
            listOf(-0.5f, 0.5f).forEach { f -> drawLine(k(Color(0xFF5E6A80)), Offset(c.x + f * r, o.y + r * 0.06f), Offset(c.x + f * r, o.y + s.height - r * 0.06f), r * 0.04f) }
            drawCircle(k(Color(0xFF0A1022)), r * 0.3f, c); drawCircle(k(Color(0xFF8AB4FF)), r * 0.3f, c, style = Stroke(r * 0.05f))
            drawCircle(k(Color.White.copy(alpha = 0.7f)), r * 0.08f, c - Offset(r * 0.1f, r * 0.1f))
            drawCircle(col, r * 0.1f, Offset(c.x + r * 0.9f, c.y))
        }
        TemaId.ABISMO -> {
            // Un ojo de buey de latón: la brida con ocho pernos, el cristal grueso y, al fondo, una medusa que brilla.
            drawCircle(sombra, r * 1.02f, c + Offset(r * 0.05f, r * 0.09f))
            drawCircle(Brush.radialGradient(listOf(claro, col, hondo), center = c - Offset(r * 0.3f, r * 0.35f), radius = r * 1.3f), r, c)
            drawCircle(hondo, r * 0.98f, c, style = Stroke(r * 0.05f))
            for (i in 0 until 8) {
                val ang = PI / 4 * i + PI / 8
                val p = c + Offset((r * 0.8f * cos(ang)).toFloat(), (r * 0.8f * sin(ang)).toFloat())
                drawCircle(hondo, r * 0.09f, p); drawCircle(claro, r * 0.035f, p - Offset(r * 0.025f, r * 0.025f))
            }
            drawCircle(k(Color(0xFF07202B)), r * 0.6f, c)
            drawCircle(hondo, r * 0.6f, c, style = Stroke(r * 0.07f))
            val luz = k(Color(0xFF5CF2C6))
            drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.35f * a), Color.Transparent), center = c, radius = r * 0.5f), r * 0.5f, c)
            drawArc(luz, 180f, 180f, false, Offset(c.x - r * 0.24f, c.y - r * 0.26f), Size(r * 0.48f, r * 0.4f), style = Stroke(r * 0.05f))
            drawLine(luz, Offset(c.x - r * 0.24f, c.y - r * 0.06f), Offset(c.x + r * 0.24f, c.y - r * 0.06f), r * 0.04f)
            for (j in 0 until 3) {
                val x = c.x - r * 0.14f + r * 0.14f * j
                drawPath(Path().apply { moveTo(x, c.y - r * 0.04f); quadraticTo(x + r * 0.06f, c.y + r * 0.14f, x, c.y + r * 0.3f) }, luz, style = Stroke(r * 0.03f))
            }
            drawArc(k(Color.White.copy(alpha = 0.55f)), 200f, 60f, false, Offset(c.x - r * 0.5f, c.y - r * 0.5f), Size(r, r), style = Stroke(r * 0.05f))
        }
        TemaId.ANDALUS -> {
            // El jatam: estrella de ocho puntas de dos cuadrados cruzados, con su filo de oro y la roseta.
            val oro = k(Color(0xFFD9B25B))
            val estrella = Path().apply {
                for (g in 0 until 2) {
                    for (j in 0 until 4) { val ang = g * PI / 4 + j * PI / 2 + PI / 4; val x = c.x + (r * cos(ang)).toFloat(); val y = c.y + (r * sin(ang)).toFloat(); if (j == 0) moveTo(x, y) else lineTo(x, y) }
                    close()
                }
            }
            translate(r * 0.05f, r * 0.08f) { drawPath(estrella, sombra) }
            drawPath(estrella, col); drawPath(estrella, oro, style = Stroke(r * 0.07f))
            drawCircle(oro, r * 0.42f, c, style = Stroke(r * 0.05f))
            for (j in 0 until 8) { val ang = j * PI / 4; drawCircle(oro, r * 0.06f, Offset(c.x + (r * 0.24f * cos(ang)).toFloat(), c.y + (r * 0.24f * sin(ang)).toFloat())) }
        }
        TemaId.MESO -> {
            // Un sello de barro pintado con cinabrio, de borde escalonado, con el cartucho de un glifo de día en el centro.
            val escalon = Path().apply {
                val n = 12
                for (j in 0 until n * 2) {
                    val rr = if (j % 2 == 0) r else r * 0.9f
                    val a0 = (j * PI / n).toFloat(); val a1 = ((j + 1) * PI / n).toFloat()
                    val p0 = Offset(c.x + rr * cos(a0), c.y + rr * sin(a0)); val p1 = Offset(c.x + rr * cos(a1), c.y + rr * sin(a1))
                    if (j == 0) moveTo(p0.x, p0.y) else lineTo(p0.x, p0.y)
                    lineTo(p1.x, p1.y)
                }
                close()
            }
            translate(r * 0.05f, r * 0.08f) { drawPath(escalon, sombra) }
            drawPath(escalon, col); drawPath(escalon, hondo, style = Stroke(r * 0.05f))
            drawCircle(claro, r * 0.7f, c, style = Stroke(r * 0.05f))
            cartuchoMaya(Offset(c.x, c.y - r * 0.04f), r * 0.42f, claro, hondo, claro, 2)
        }
        TemaId.MONGOL -> {
            // La paiza: la tablilla de oro del salvoconducto, colgada de su cordón, con la escritura en columnas.
            val w = r * 1.1f; val arriba = c.y - r * 1.05f; val abajo = c.y + r * 1.05f
            val placa = Path().apply {
                moveTo(c.x - w / 2, abajo); lineTo(c.x - w / 2, arriba + w / 2)
                arcTo(Rect(c.x - w / 2, arriba, c.x + w / 2, arriba + w), 180f, 180f, false)
                lineTo(c.x + w / 2, abajo); close()
            }
            drawLine(k(Color(0xFF2E6DB4)), Offset(c.x, arriba - r * 0.5f), Offset(c.x, arriba + w * 0.3f), r * 0.07f)
            translate(r * 0.05f, r * 0.08f) { drawPath(placa, sombra) }
            drawPath(placa, col); drawPath(placa, hondo, style = Stroke(r * 0.05f))
            drawCircle(hondo, r * 0.11f, Offset(c.x, arriba + w * 0.32f), style = Stroke(r * 0.04f))
            for (j in 0 until 3) {
                val x = c.x + (j - 1) * w * 0.26f
                drawLine(hondo, Offset(x, arriba + w * 0.66f), Offset(x, abajo - r * 0.15f), r * 0.045f)
                var y = arriba + w * 0.75f; var lado = if (j % 2 == 0) 1f else -1f
                while (y < abajo - r * 0.2f) { drawLine(hondo, Offset(x, y), Offset(x + lado * w * 0.08f, y + r * 0.04f), r * 0.04f); y += r * 0.2f; lado = -lado }
            }
        }
        TemaId.RENACIMIENTO -> {
            // Cera de sanguina con el compás del taller grabado.
            gotaCera(c, r, col, 7)
            drawCircle(hondo, r * 0.78f, c, style = Stroke(r * 0.04f))
            val bisagra = Offset(c.x, c.y - r * 0.5f)
            drawLine(claro, bisagra, Offset(c.x - r * 0.36f, c.y + r * 0.46f), r * 0.07f, StrokeCap.Round)
            drawLine(claro, bisagra, Offset(c.x + r * 0.36f, c.y + r * 0.46f), r * 0.07f, StrokeCap.Round)
            drawCircle(claro, r * 0.08f, bisagra)
            drawArc(claro, 20f, 140f, false, Offset(c.x - r * 0.5f, c.y - r * 0.2f), Size(r, r), style = Stroke(r * 0.04f))
        }
        TemaId.PIRATA -> {
            // Lacre de brea con la calavera y las tibias grabadas, y un cordón de cuentas como de cabo alrededor.
            gotaCera(c, r, col, 11)
            for (i in 0 until 18) {
                val a = 2 * PI * i / 18
                drawCircle(hondo, r * 0.05f, Offset(c.x + (r * 0.8f * cos(a)).toFloat(), c.y + (r * 0.8f * sin(a)).toFloat()))
            }
            listOf(-1f, 1f).forEach { s ->
                val a = Offset(c.x - s * r * 0.5f, c.y + r * 0.42f); val b = Offset(c.x + s * r * 0.5f, c.y - r * 0.08f)
                drawLine(claro, a, b, r * 0.09f, StrokeCap.Round)
                drawCircle(claro, r * 0.07f, a + Offset(-s * r * 0.02f, r * 0.04f)); drawCircle(claro, r * 0.07f, b + Offset(s * r * 0.02f, -r * 0.04f))
            }
            drawCircle(claro, r * 0.3f, Offset(c.x, c.y - r * 0.12f))
            drawRoundRect(claro, Offset(c.x - r * 0.17f, c.y + r * 0.06f), Size(r * 0.34f, r * 0.2f), CornerRadius(r * 0.05f))
            drawCircle(hondo, r * 0.08f, Offset(c.x - r * 0.11f, c.y - r * 0.12f)); drawCircle(hondo, r * 0.08f, Offset(c.x + r * 0.11f, c.y - r * 0.12f))
            drawPath(Path().apply { moveTo(c.x, c.y - r * 0.02f); lineTo(c.x - r * 0.04f, c.y + r * 0.05f); lineTo(c.x + r * 0.04f, c.y + r * 0.05f); close() }, hondo)
            for (j in -1..1) drawLine(hondo, Offset(c.x + j * r * 0.08f, c.y + r * 0.1f), Offset(c.x + j * r * 0.08f, c.y + r * 0.24f), r * 0.025f)
        }
        TemaId.URSS -> rotate(-8f, c) {
            // El matasellos: dos círculos de tinta con la estrella al centro y las ondas que cancelan.
            drawCircle(col, r * 0.92f, c, style = Stroke(r * 0.07f))
            drawCircle(col, r * 0.7f, c, style = Stroke(r * 0.04f))
            drawPath(estrellaRuta(c, r * 0.48f, r * 0.19f, 5), col)
            for (j in 0 until 3) {
                val y = c.y - r * 0.4f + j * r * 0.4f
                val onda = Path().apply {
                    var x = c.x + r * 0.95f; var s = 1f; moveTo(x, y)
                    while (x < c.x + r * 2.2f) { quadraticTo(x + r * 0.15f, y - s * r * 0.15f, x + r * 0.3f, y); x += r * 0.3f; s = -s }
                }
                drawPath(onda, col, style = Stroke(r * 0.05f))
            }
        }
        TemaId.REVOLUCION -> {
            // La escarapela azul, blanca y roja, con sus dos cintas.
            listOf(-1f to Color(0xFF1F3A6E), 1f to Color(0xFFB22234)).forEach { (s, cinta) ->
                drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + s * r * 0.55f, c.y + r * 1.25f); lineTo(c.x + s * r * 0.3f, c.y + r * 1.1f); lineTo(c.x + s * r * 0.1f, c.y + r * 1.22f); close() }, k(cinta))
            }
            with(Contenedores) { escarapela(c, r * 0.92f) }
        }
        TemaId.ANTIGUAS -> {
            // El anillo de hierro con sus remaches y la gema tallada al centro.
            drawCircle(sombra, r * 0.98f, c + Offset(r * 0.05f, r * 0.08f))
            drawCircle(k(Color(0xFF4A4E54)), r * 0.8f, c, style = Stroke(r * 0.32f))
            drawCircle(k(Color(0xFF7A8088)), r * 0.96f, c, style = Stroke(r * 0.04f))
            for (j in 0 until 6) { val ang = j * PI / 3; drawCircle(k(Color(0xFF9EA3A8)), r * 0.06f, Offset(c.x + (r * 0.8f * cos(ang)).toFloat(), c.y + (r * 0.8f * sin(ang)).toFloat())) }
            val vertices = (0 until 6).map { j -> val ang = j * PI / 3 + PI / 6; Offset(c.x + (r * 0.52f * cos(ang)).toFloat(), c.y + (r * 0.52f * sin(ang)).toFloat()) }
            val gema = Path().apply { vertices.forEachIndexed { i, v -> if (i == 0) moveTo(v.x, v.y) else lineTo(v.x, v.y) }; close() }
            drawPath(gema, col)
            vertices.forEach { v -> drawLine(claro.copy(alpha = claro.alpha * 0.5f), c, v, r * 0.025f) }
            drawPath(gema, hondo, style = Stroke(r * 0.05f))
            drawCircle(k(Color.White.copy(alpha = 0.6f)), r * 0.09f, c - Offset(r * 0.16f, r * 0.16f))
        }
        TemaId.CUEVA -> {
            // Un disco de arcilla teñida de ocre, con la mano en negativo de quien lo cerró y una corona de puntos.
            drawCircle(sombra, r * 0.98f, c + Offset(r * 0.05f, r * 0.08f))
            drawCircle(col, r * 0.92f, c)
            drawCircle(hondo, r * 0.92f, c, style = Stroke(r * 0.06f))
            for (j in 0 until 10) { val ang = j * PI / 5; drawCircle(hondo, r * 0.045f, Offset(c.x + (r * 0.78f * cos(ang)).toFloat(), c.y + (r * 0.78f * sin(ang)).toFloat())) }
            drawPath(manoUnida(Offset(c.x, c.y + r * 0.04f), r * 0.55f), claro)
        }
    }
}

/** Un destello de cuatro puntas. */
private fun DrawScope.destelloSobre(c: Offset, r: Float, color: Color) = drawPath(estrellaRuta(c, r, r * 0.25f, 4), color)

/** El adorno de cada tema en el frente del sobre (por los lados, para no tapar lo que va escrito abajo). */
private fun DrawScope.adornoSobre(t: TemaId, pa: Papeleria, cuerpo: Rect) {
    val l = cuerpo.left; val r = cuerpo.right; val top = cuerpo.top; val bot = cuerpo.bottom
    val W = cuerpo.width; val H = cuerpo.height
    when (t) {
        // Los clavi: dos franjas de púrpura, como en la toga.
        TemaId.ROMANO -> listOf(l + W * 0.035f, r - W * 0.053f).forEach { x -> drawRect(Color(0xFF7C2433).copy(alpha = 0.7f), Offset(x, top), Size(W * 0.018f, H)) }
        // La orilla del correo urgente: rayas en diagonal, rojo pompeyano y verde de persiana, por arriba y por abajo.
        TemaId.URBE -> {
            val franja = H * 0.045f; val paso = W * 0.05f
            listOf(top, bot - franja).forEach { y0 ->
                var x = l; var i = 0
                while (x < r) {
                    val col = if (i % 2 == 0) Color(0xFFA8432A) else Color(0xFF3D5E45)
                    drawPath(Path().apply { moveTo(x, y0 + franja); lineTo(x + paso * 0.5f, y0); lineTo(x + paso, y0); lineTo(x + paso * 0.5f, y0 + franja); close() }, col.copy(alpha = 0.6f))
                    x += maxOf(paso, 1f); i++
                }
            }
        }
        TemaId.CIBER -> {
            // Líneas de barrido y la ventana del sobre, por donde asoma la dirección en cian.
            var y = top; while (y < bot) { drawLine(Color(0xFF00E5FF).copy(alpha = 0.035f), Offset(l, y), Offset(r, y), 1f); y += 3f }
            val o = Offset(l + W * 0.05f, top + H * 0.42f); val s = Size(W * 0.25f, H * 0.18f)
            drawRect(Color.Black.copy(alpha = 0.55f), o, s); drawRect(Color(0xFF00E5FF).copy(alpha = 0.6f), o, s, style = Stroke(1.2f))
            for (j in 0 until 3) {
                val yy = o.y + s.height * (0.28f + j * 0.24f)
                drawLine(Color(0xFF00E5FF).copy(alpha = 0.5f), Offset(o.x + s.width * 0.1f, yy), Offset(o.x + s.width * (0.85f - j * 0.2f), yy), 1.4f)
            }
        }
        TemaId.NEON -> {
            // Gotas de lluvia en el plástico y la etiqueta de entrega con su código de barras y el número del puesto.
            val az = java.util.Random(19)
            repeat(18) { val x = l + az.nextFloat() * W; val y = top + az.nextFloat() * H; drawLine(Color.White.copy(alpha = 0.07f), Offset(x, y), Offset(x, y + H * 0.05f), 1.5f, StrokeCap.Round) }
            val o = Offset(l + W * 0.05f, top + H * 0.52f); val s = Size(W * 0.3f, H * 0.28f)
            drawRect(Color(0xFFEFE3EA), o, s)
            drawRect(pa.cordon, o, Size(s.width, s.height * 0.22f))
            var x = o.x + s.width * 0.08f; var k = 0
            while (x < o.x + s.width * 0.92f) { val g = if (k % 3 == 0) 2.2f else 1f; drawLine(Color(0xFF1C0010), Offset(x, o.y + s.height * 0.38f), Offset(x, o.y + s.height * 0.86f), g); x += 3.4f; k++ }
        }
        TemaId.HELENO -> {
            // Un meandro en columna junto al borde derecho, entre dos filetes.
            val u = W * 0.016f; val x0 = r - W * 0.075f; val purpura = Color(0xFF5B2A6E)
            val p = Path(); var y = top + H * 0.12f
            while (y + 3 * u <= top + H * 0.66f) { p.moveTo(x0, y); p.lineTo(x0 + 2 * u, y); p.lineTo(x0 + 2 * u, y + 2 * u); p.lineTo(x0 + u, y + 2 * u); p.lineTo(x0 + u, y + u); y += 3 * u }
            drawPath(p, purpura.copy(alpha = 0.65f), style = Stroke(1.3f))
            listOf(x0 - u * 0.6f, x0 + u * 2.6f).forEach { x -> drawLine(purpura.copy(alpha = 0.5f), Offset(x, top + H * 0.1f), Offset(x, top + H * 0.68f), 1f) }
        }
        TemaId.EGIPTO -> {
            // Un cartucho vertical junto al borde derecho, con tres signos en lapislázuli, y las fibras del papiro en cruz.
            val lapis = Color(0xFF1D2F7A)
            var y = top + H * 0.05f; while (y < bot) { drawLine(Color(0xFF8A6E3A).copy(alpha = 0.08f), Offset(l, y), Offset(r, y), 1f); y += maxOf(H * 0.035f, 1f) }
            var x = l + W * 0.02f; while (x < r) { drawLine(Color(0xFF8A6E3A).copy(alpha = 0.05f), Offset(x, top), Offset(x, bot), 1f); x += maxOf(W * 0.03f, 1f) }
            val ancho = W * 0.075f; val x0 = r - W * 0.12f; val y0 = top + H * 0.1f; val alto = H * 0.56f
            drawRoundRect(lapis.copy(alpha = 0.7f), Offset(x0, y0), Size(ancho, alto), CornerRadius(ancho / 2), style = Stroke(1.6f))
            drawLine(lapis.copy(alpha = 0.7f), Offset(x0 - ancho * 0.15f, y0 + alto + 3f), Offset(x0 + ancho * 1.15f, y0 + alto + 3f), 2.2f)
            for (j in 0 until 3) signoEgipcio(j, Offset(x0 + ancho / 2, y0 + alto * (0.2f + j * 0.3f)), ancho * 0.72f, lapis.copy(alpha = 0.7f), 1.3f)
        }
        // Calderones de copista en rojo y azul a los dos lados.
        TemaId.MEDIEVAL -> listOf(0.3f, 0.44f, 0.58f).forEachIndexed { i, f ->
            val col = if (i % 2 == 0) Color(0xFFB3261E) else Color(0xFF1F3A93)
            listOf(l + W * 0.05f, r - W * 0.07f).forEach { x ->
                drawRect(col.copy(alpha = 0.75f), Offset(x, top + H * f), Size(W * 0.02f, W * 0.02f))
                drawLine(col.copy(alpha = 0.6f), Offset(x + W * 0.01f, top + H * f + W * 0.02f), Offset(x + W * 0.01f, top + H * (f + 0.08f)), 1.2f)
            }
        }
        TemaId.TEMPLARIO -> {
            // El estandarte «baucent», la mitad de arriba negra y la de abajo blanca, en su asta a la izquierda; a la derecha, la cruz patada roja.
            val x = l + W * 0.05f; val y = top + H * 0.24f; val bw = W * 0.06f; val bh = H * 0.34f
            drawLine(pa.tinta.copy(alpha = 0.7f), Offset(x, y - H * 0.05f), Offset(x, y + bh + H * 0.1f), 1.4f)
            drawRect(Color(0xFF1A1816).copy(alpha = 0.85f), Offset(x, y), Size(bw, bh / 2))
            drawRect(Color.White.copy(alpha = 0.95f), Offset(x, y + bh / 2), Size(bw, bh / 2))
            drawRect(pa.tinta.copy(alpha = 0.5f), Offset(x, y), Size(bw, bh), style = Stroke(1f))
            drawPath(cruzPatada(Offset(r - W * 0.08f, top + H * 0.36f), W * 0.035f), pa.forro.copy(alpha = 0.85f))
        }
        TemaId.DISTOPIA -> {
            // Los agujeros del sobre de correo interno y la franja roja de «confidencial» en la esquina.
            var y = top + H * 0.2f; while (y < top + H * 0.7f) { drawCircle(Color.Black.copy(alpha = 0.3f), H * 0.025f, Offset(l + W * 0.04f, y)); y += H * 0.12f }
            drawLine(Color(0xFFC8102E).copy(alpha = 0.8f), Offset(r - W * 0.2f, bot + 4f), Offset(r + 4f, bot - W * 0.2f), W * 0.045f)
        }
        TemaId.REFUGIO -> {
            // La franja verde azulada del pie y dos destellos naranjas.
            drawRect(Color(0xFF1C6E6A).copy(alpha = 0.8f), Offset(l, bot - H * 0.035f), Size(W, H * 0.035f))
            destelloSobre(Offset(r - W * 0.12f, top + H * 0.36f), H * 0.1f, Color(0xFFD9572B))
            destelloSobre(Offset(r - W * 0.2f, top + H * 0.5f), H * 0.063f, Color(0xFFD9572B))
        }
        // Remaches a los dos lados.
        TemaId.ORDEN -> listOf(l + W * 0.03f, r - W * 0.03f).forEach { x ->
            var y = top + H * 0.14f
            while (y < bot - H * 0.05f) { drawCircle(Color(0xFF6E757D), H * 0.018f, Offset(x, y)); drawCircle(Color.White.copy(alpha = 0.5f), H * 0.007f, Offset(x - H * 0.005f, y - H * 0.005f)); y += H * 0.1f }
        }
        // El sobre de telegrama: la franja impresa de la compañía, con su locomotora de grabado, y la línea perforada de abajo.
        TemaId.VAPOR -> {
            val grabado = pa.tinta.copy(alpha = 0.5f)
            drawRect(pa.lacre.copy(alpha = 0.7f), Offset(l, top + H * 0.06f), Size(W, H * 0.03f))
            val x0 = l + W * 0.06f; val y0 = top + H * 0.17f; val s = H * 0.08f
            drawRect(grabado, Offset(x0, y0), Size(s * 2.2f, s * 0.9f))
            drawRect(grabado, Offset(x0 + s * 2.2f, y0 - s * 0.5f), Size(s * 1.1f, s * 1.4f))
            drawRect(grabado, Offset(x0 + s * 0.3f, y0 - s * 0.6f), Size(s * 0.35f, s * 0.6f))
            drawCircle(grabado, s * 0.22f, Offset(x0 + s * 1.2f, y0 - s * 0.05f))
            for (k in 0..2) drawCircle(grabado, s * 0.32f, Offset(x0 + s * (0.5f + k * 0.95f), y0 + s * 1.15f))
            drawLine(grabado, Offset(x0 - s * 0.3f, y0 + s * 1.5f), Offset(x0 + s * 3.6f, y0 + s * 1.5f), s * 0.08f)
            var x = l + W * 0.04f
            while (x < r - W * 0.04f) { drawCircle(pa.sombra.copy(alpha = 0.7f), H * 0.009f, Offset(x, bot - H * 0.08f)); x += maxOf(W * 0.025f, 1f) }
        }
        TemaId.LABORATORIO -> {
            // El código de barras de la muestra, de canto junto al borde derecho.
            val az = java.util.Random(4); var y = top + H * 0.16f; val x = r - W * 0.1f
            while (y < top + H * 0.62f) { val g = H * (0.006f + az.nextFloat() * 0.014f); drawRect(Color(0xFF1B2733).copy(alpha = 0.75f), Offset(x, y), Size(W * 0.07f, g)); y += g + H * (0.008f + az.nextFloat() * 0.01f) }
            drawRect(Color(0xFF0B8FA1), Offset(x, top + H * 0.64f), Size(W * 0.07f, H * 0.02f))
        }
        TemaId.COLONOS -> {
            // Las ondas del matasellos del correo del pueblo.
            for (j in 0 until 4) {
                val y = top + H * (0.34f + j * 0.055f); val p = Path(); var x = r - W * 0.26f; p.moveTo(x, y); var s = 1f
                while (x < r - W * 0.03f) { p.quadraticTo(x + W * 0.015f, y - s * H * 0.025f, x + W * 0.03f, y); x += W * 0.03f; s = -s }
                drawPath(p, Color(0xFF1E1A16).copy(alpha = 0.45f), style = Stroke(1.1f))
            }
        }
        TemaId.NAPOLEON -> {
            // El doble filete de oro de los despachos.
            val oro = Color(0xFFD9B25B)
            drawRect(oro.copy(alpha = 0.8f), Offset(l + W * 0.03f, top + W * 0.03f), Size(W * 0.94f, H - W * 0.06f), style = Stroke(1.6f))
            drawRect(oro.copy(alpha = 0.5f), Offset(l + W * 0.045f, top + W * 0.045f), Size(W * 0.91f, H - W * 0.09f), style = Stroke(0.8f))
        }
        TemaId.DECO -> {
            // Doble filete de oro y, en cada esquina, tres escalones que se meten hacia dentro, como un marco de ascensor.
            val oro = Color(0xFFD9B44A)
            val m = W * 0.035f
            drawRect(oro.copy(alpha = 0.85f), Offset(l + m, top + m), Size(W - 2 * m, H - 2 * m), style = Stroke(1.6f))
            drawRect(oro.copy(alpha = 0.45f), Offset(l + m * 1.5f, top + m * 1.5f), Size(W - 3 * m, H - 3 * m), style = Stroke(0.8f))
            val paso = W * 0.022f
            listOf(Offset(l + m, top + m) to Offset(1f, 1f), Offset(r - m, top + m) to Offset(-1f, 1f), Offset(l + m, bot - m) to Offset(1f, -1f), Offset(r - m, bot - m) to Offset(-1f, -1f)).forEach { (o, d) ->
                val esc = Path().apply {
                    moveTo(o.x, o.y + d.y * 4 * paso)
                    for (i in 1..3) { lineTo(o.x + d.x * i * paso, o.y + d.y * (4 - i + 1) * paso); lineTo(o.x + d.x * i * paso, o.y + d.y * (4 - i) * paso) }
                    lineTo(o.x + d.x * 4 * paso, o.y + d.y * paso); lineTo(o.x + d.x * 4 * paso, o.y)
                }
                drawPath(esc, oro.copy(alpha = 0.8f), style = Stroke(1.2f))
            }
        }
        TemaId.BOTANICO -> {
            // Un helecho a tinta junto al borde izquierdo.
            val musgo = Color(0xFF5A6A3A).copy(alpha = 0.6f)
            val a = Offset(l + W * 0.06f, bot - H * 0.32f); val b = Offset(l + W * 0.16f, top + H * 0.3f)
            drawLine(musgo, a, b, 1.3f)
            for (j in 1..7) {
                val f = j / 8f; val p = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f); val largo = W * 0.05f * (1f - f * 0.6f)
                listOf(-1f, 1f).forEach { s -> drawLine(musgo, p, Offset(p.x + s * largo, p.y - largo * 0.5f), 2.2f, StrokeCap.Round) }
            }
        }
        TemaId.RUINAS -> {
            // El sobre se cortó de un plano viejo de la ciudad: calles en cuadrícula y una avenida en diagonal. En una esquina,
            // una hoja de hiedra prensada, sujeta con un trozo de cinta.
            val calle = pa.tinta.copy(alpha = 0.10f)
            for (i in 1..5) { val x = l + W * i / 6f; drawLine(calle, Offset(x, top), Offset(x, bot), 1f) }
            for (j in 1..3) { val y = top + H * j / 4f; drawLine(calle, Offset(l, y), Offset(r, y), 1f) }
            drawLine(pa.tinta.copy(alpha = 0.14f), Offset(l, bot - H * 0.1f), Offset(r, top + H * 0.2f), 3f)
            val p = Offset(r - W * 0.14f, bot - H * 0.3f); val s = H * 0.16f
            val hoja = Path().apply {
                moveTo(p.x, p.y + s * 0.5f); lineTo(p.x - s * 0.3f, p.y + s * 0.1f); lineTo(p.x - s * 0.55f, p.y - s * 0.1f); lineTo(p.x - s * 0.2f, p.y - s * 0.12f)
                lineTo(p.x, p.y - s * 0.55f); lineTo(p.x + s * 0.2f, p.y - s * 0.12f); lineTo(p.x + s * 0.55f, p.y - s * 0.1f); lineTo(p.x + s * 0.3f, p.y + s * 0.1f); close()
            }
            drawPath(hoja, pa.forro.copy(alpha = 0.75f))
            drawLine(pa.forro, Offset(p.x, p.y + s * 0.45f), Offset(p.x, p.y + s * 0.95f), 1.4f)
            rotate(-20f, p) { drawRect(Color(0xFFE8DFC4).copy(alpha = 0.6f), Offset(p.x - s * 0.5f, p.y + s * 0.3f), Size(s, s * 0.28f)) }
        }
        TemaId.NAVIDAD -> {
            // El borde de bastón de caramelo, blanco y verde, y unos copos.
            val m = W * 0.025f; val o = Offset(l + m, top + m); val s = Size(W - 2 * m, H - 2 * m); val paso = H * 0.05f
            drawRect(Color.White.copy(alpha = 0.85f), o, s, style = Stroke(H * 0.022f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(paso, paso), 0f)))
            drawRect(Color(0xFF1B5E20), o, s, style = Stroke(H * 0.022f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(paso, paso), paso)))
            val az = java.util.Random(25); repeat(16) { drawCircle(Color.White.copy(alpha = 0.45f), 1f + az.nextFloat() * 1.8f, Offset(l + az.nextFloat() * W, top + az.nextFloat() * H)) }
        }
        // Tres monedas de agujero cuadrado.
        TemaId.SEDA -> listOf(Offset(0.1f, 0.42f), Offset(0.17f, 0.5f), Offset(0.085f, 0.57f)).forEach { f ->
            val c = Offset(r - W * f.x, top + H * f.y); val rr = H * 0.055f
            drawCircle(Color(0xFFD9B25B), rr, c); drawCircle(Color(0xFF8E6A1F), rr, c, style = Stroke(1f))
            drawRect(pa.sobre, Offset(c.x - rr * 0.32f, c.y - rr * 0.32f), Size(rr * 0.64f, rr * 0.64f))
        }
        // El noshi: el adorno de papel plegado de los regalos, con su tira amarilla, arriba a la derecha.
        TemaId.EDO -> {
            val c = Offset(r - W * 0.1f, top + H * 0.2f); val a = H * 0.05f; val b = H * 0.13f
            rotate(-18f, c) {
                drawPath(Path().apply { moveTo(c.x, c.y - b); lineTo(c.x + a, c.y - b * 0.6f); lineTo(c.x + a, c.y + b * 0.6f); lineTo(c.x, c.y + b); lineTo(c.x - a, c.y + b * 0.6f); lineTo(c.x - a, c.y - b * 0.6f); close() }, pa.lacre)
                drawPath(Path().apply { moveTo(c.x, c.y - b * 0.8f); lineTo(c.x + a * 0.55f, c.y - b * 0.45f); lineTo(c.x + a * 0.55f, c.y + b * 0.45f); lineTo(c.x, c.y + b * 0.8f); lineTo(c.x - a * 0.55f, c.y + b * 0.45f); lineTo(c.x - a * 0.55f, c.y - b * 0.45f); close() }, Color.White)
                drawRect(Color(0xFFE8C04A), Offset(c.x - a * 0.18f, c.y - b * 0.6f), Size(a * 0.36f, b * 1.2f))
            }
        }
        TemaId.GOTICO -> {
            // Las estrellas de oro de la bóveda.
            val az = java.util.Random(9); val oro = Color(0xFFE2B34A)
            repeat(26) {
                val p = Offset(l + az.nextFloat() * W, top + az.nextFloat() * H); val s = H * (0.012f + az.nextFloat() * 0.016f)
                drawLine(oro.copy(alpha = 0.7f), Offset(p.x - s, p.y), Offset(p.x + s, p.y), 1f); drawLine(oro.copy(alpha = 0.7f), Offset(p.x, p.y - s), Offset(p.x, p.y + s), 1f)
                drawCircle(oro.copy(alpha = 0.8f), s * 0.35f, p)
            }
        }
        TemaId.ARCANO -> {
            // El sobre del archivo: la etiqueta con sus renglones, el matasellos redondo que no se lee y el cerco de una taza.
            val tinta = pa.tinta
            drawRect(Color(0xFFEDE4C8), Offset(l + W * 0.08f, top + H * 0.12f), Size(W * 0.34f, H * 0.2f))
            for (k in 0 until 3) drawLine(tinta.copy(alpha = 0.35f), Offset(l + W * 0.1f, top + H * (0.17f + k * 0.05f)), Offset(l + W * (0.38f - k * 0.06f), top + H * (0.17f + k * 0.05f)), 1.2f)
            val m = Offset(r - W * 0.16f, top + H * 0.26f)
            drawCircle(tinta.copy(alpha = 0.3f), H * 0.13f, m, style = Stroke(1.6f)); drawCircle(tinta.copy(alpha = 0.22f), H * 0.09f, m, style = Stroke(1f))
            for (k in 0 until 4) drawLine(tinta.copy(alpha = 0.2f), Offset(m.x - H * 0.36f, m.y - H * 0.06f + k * H * 0.04f), Offset(m.x - H * 0.15f, m.y - H * 0.06f + k * H * 0.04f), 1.4f)
            drawCircle(Color(0xFF6A4B26).copy(alpha = 0.16f), H * 0.16f, Offset(l + W * 0.7f, bot - H * 0.24f), style = Stroke(H * 0.02f))
        }
        TemaId.ESPACIO -> {
            // Las juntas remachadas del estuche y las luces de estado.
            val junta = Color(0xFF5E6A80).copy(alpha = 0.5f)
            listOf(l + W * 0.12f, r - W * 0.12f).forEach { x ->
                drawLine(junta, Offset(x, top), Offset(x, bot), 1f)
                var y = top + H * 0.16f; while (y < bot) { drawCircle(junta, H * 0.01f, Offset(x + W * 0.02f, y)); y += H * 0.12f }
            }
            listOf(Color(0xFF3DDC84), Color(0xFFFFB86B), Color(0xFFFF5470)).forEachIndexed { i, col -> drawCircle(col, H * 0.022f, Offset(r - W * 0.06f, top + H * (0.3f + i * 0.08f))) }
        }
        TemaId.ABISMO -> {
            // La costura del hule, pespunteada a todo el borde, gotas que resbalan y la chapa de latón con la cota.
            val hilo = Color(0xFFCDB88C).copy(alpha = 0.55f)
            val m = W * 0.035f
            val costura = Stroke(1.2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f)))
            drawRoundRect(hilo, Offset(l + m, top + m), Size(W - 2 * m, H - 2 * m), CornerRadius(H * 0.05f), style = costura)
            listOf(0.18f to 0.55f, 0.27f to 0.72f, 0.74f to 0.62f, 0.86f to 0.4f).forEach { (fx, fy) ->
                val g = Offset(l + W * fx, top + H * fy); val s = H * 0.025f
                drawPath(Path().apply { moveTo(g.x, g.y - s * 1.6f); quadraticTo(g.x + s, g.y, g.x, g.y + s); quadraticTo(g.x - s, g.y, g.x, g.y - s * 1.6f); close() }, Color.White.copy(alpha = 0.22f))
            }
            val chapa = Offset(l + W * 0.07f, bot - H * 0.2f)
            drawRoundRect(Color(0xFFB8913E).copy(alpha = 0.85f), chapa, Size(W * 0.16f, H * 0.1f), CornerRadius(H * 0.02f))
            for (j in 0 until 3) drawLine(Color(0xFF15333F).copy(alpha = 0.6f), Offset(chapa.x + W * 0.02f, chapa.y + H * (0.03f + 0.025f * j)), Offset(chapa.x + W * (0.14f - 0.03f * j), chapa.y + H * (0.03f + 0.025f * j)), 1f)
        }
        TemaId.ANDALUS -> {
            // Una columna de estrellas de ocho puntas, de oro con el centro verde.
            var y = top + H * 0.2f
            while (y < top + H * 0.66f) { val c = Offset(l + W * 0.05f, y); val rr = H * 0.04f; drawPath(estrellaRuta(c, rr, rr * 0.62f, 8), Color(0xFFD9B25B).copy(alpha = 0.8f)); drawCircle(Color(0xFF1E6B5C), rr * 0.3f, c); y += H * 0.11f }
        }
        TemaId.MESO -> {
            // La columna de glifos de un códice: bloques de cartucho con sus numerales, y una greca escalonada abajo.
            val tinta = Color(0xFF2A1810).copy(alpha = 0.55f); val cinabrio = Color(0xFFA3301E).copy(alpha = 0.7f)
            var y = top + H * 0.18f; var k = 0
            while (y < top + H * 0.62f) {
                val c = Offset(l + W * 0.07f, y); val rr = H * 0.045f
                drawRoundRect(cinabrio, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), CornerRadius(rr * 0.4f), style = Stroke(1.4f))
                numeralMaya(3 + k * 4, c, rr * 1.2f, tinta)
                y += maxOf(H * 0.12f, 1f); k++
            }
            grecaEscalonada(l + W * 0.04f, r - W * 0.04f, bot - H * 0.12f, bot - H * 0.05f, cinabrio, 1.6f)
        }
        TemaId.MONGOL -> {
            // La costura del fieltro y una nube en espiral.
            val m = W * 0.035f; val azul = Color(0xFF2E6DB4).copy(alpha = 0.6f)
            drawRoundRect(azul, Offset(l + m, top + m), Size(W - 2 * m, H - 2 * m), CornerRadius(H * 0.05f), style = Stroke(1.4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f))))
            val c = Offset(l + W * 0.1f, top + H * 0.5f); val rr = H * 0.05f
            listOf(-1f, 1f).forEach { s -> drawArc(azul, if (s < 0) 90f else 270f, 270f, false, Offset(c.x + s * rr - rr, c.y - rr), Size(2 * rr, 2 * rr), style = Stroke(1.4f)) }
        }
        TemaId.RENACIMIENTO -> {
            // Trazos de construcción: un círculo con su cuadrado inscrito y una diagonal punteada.
            val sepia = Color(0xFF5A3A1C).copy(alpha = 0.3f); val c = Offset(r - W * 0.16f, top + H * 0.45f); val rr = W * 0.1f
            drawCircle(sepia, rr, c, style = Stroke(0.9f))
            drawRect(sepia, Offset(c.x - rr * 0.707f, c.y - rr * 0.707f), Size(rr * 1.414f, rr * 1.414f), style = Stroke(0.9f))
            drawLine(sepia, Offset(c.x - rr * 1.4f, c.y + rr * 1.2f), Offset(c.x + rr * 1.3f, c.y - rr * 1.3f), 0.8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f)))
        }
        TemaId.PIRATA -> {
            // Las líneas de rumbo de los portulanos, que salen de una rosa pequeña, y la «X» roja del tesoro donde cruzan dos.
            val sepia = Color(0xFF2A1C0F); val c = Offset(r - W * 0.2f, top + H * 0.62f); val largo = W * 0.34f
            for (i in 0 until 16) {
                val a = 2 * PI * i / 16
                val fin = Offset(c.x + (largo * cos(a)).toFloat(), c.y + (largo * sin(a)).toFloat())
                drawLine(sepia.copy(alpha = if (i % 4 == 0) 0.22f else 0.11f), c, fin, if (i % 4 == 0) 0.9f else 0.6f)
            }
            drawCircle(sepia.copy(alpha = 0.3f), W * 0.025f, c, style = Stroke(0.9f))
            val x = Offset(c.x - largo * 0.55f, c.y - largo * 0.32f); val s = W * 0.018f
            drawLine(Color(0xFF9A2A1C).copy(alpha = 0.6f), x - Offset(s, s), x + Offset(s, s), 1.6f, StrokeCap.Round)
            drawLine(Color(0xFF9A2A1C).copy(alpha = 0.6f), x + Offset(-s, s), x + Offset(s, -s), 1.6f, StrokeCap.Round)
            val ruta = Path().apply { moveTo(l + W * 0.06f, bot - H * 0.12f); quadraticTo(l + W * 0.3f, bot - H * 0.45f, x.x - s * 1.8f, x.y + s * 1.8f) }
            drawPath(ruta, sepia.copy(alpha = 0.25f), style = Stroke(0.9f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))))
        }
        TemaId.URSS -> {
            // La cuña roja y la barra negra del cartel constructivista.
            drawPath(Path().apply { moveTo(l, top + H * 0.3f); lineTo(l + W * 0.3f, top + H * 0.5f); lineTo(l, top + H * 0.62f); close() }, Color(0xFFC1121F).copy(alpha = 0.85f))
            drawLine(Color(0xFF161414).copy(alpha = 0.8f), Offset(l + W * 0.04f, top + H * 0.66f), Offset(l + W * 0.36f, top + H * 0.36f), H * 0.03f)
        }
        // Las tres franjas de la bandera junto al borde izquierdo.
        TemaId.REVOLUCION -> listOf(Color(0xFF1F3A6E), Color(0xFFFBF7EE), Color(0xFFB22234)).forEachIndexed { i, col -> drawRect(col, Offset(l + i * W * 0.022f, top), Size(W * 0.022f, H)) }
        TemaId.ANTIGUAS -> {
            // Un camino punteado sobre el mapa, con su cruz roja y dos montañas.
            val tinta = Color(0xFF5A3A22).copy(alpha = 0.55f)
            val camino = Path().apply { moveTo(r - W * 0.34f, top + H * 0.62f); cubicTo(r - W * 0.2f, top + H * 0.6f, r - W * 0.24f, top + H * 0.4f, r - W * 0.08f, top + H * 0.32f) }
            drawPath(camino, tinta, style = Stroke(1.3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 3f))))
            val x = Offset(r - W * 0.08f, top + H * 0.32f); val s = H * 0.03f
            drawLine(Color(0xFF8E1B24), x - Offset(s, s), x + Offset(s, s), 1.6f); drawLine(Color(0xFF8E1B24), x + Offset(-s, s), x + Offset(s, -s), 1.6f)
            listOf(0.26f, 0.2f).forEachIndexed { i, dx ->
                val b = Offset(r - W * dx, top + H * (0.48f - i * 0.02f))
                drawPath(Path().apply { moveTo(b.x - H * 0.05f, b.y); lineTo(b.x, b.y - H * 0.07f); lineTo(b.x + H * 0.05f, b.y) }, tinta, style = Stroke(1.2f))
            }
        }
        TemaId.CUEVA -> {
            // Una hilera de puntos de ocre y un bisonte de carbón pintados en la piel del sobre.
            for (k in 0 until 5) drawCircle(Color(0xFFA2401F).copy(alpha = 0.6f), H * 0.022f, Offset(l + W * (0.08f + k * 0.04f), top + H * 0.8f))
            drawPath(bisonte(Offset(r - W * 0.16f, top + H * 0.72f), W * 0.1f), Color(0xFF2A211B).copy(alpha = 0.45f))
        }
    }
}

/** El cierre del sobre sellado; [avance] (0 a 1) lo va atando. */
private fun DrawScope.cierreSobre(c: CierreSobre, pa: Papeleria, cuerpo: Rect, punta: Float, avance: Float) {
    val l = cuerpo.left; val top = cuerpo.top; val bot = cuerpo.bottom
    val W = cuerpo.width; val H = cuerpo.height; val cx = cuerpo.center.x
    val g = H * 0.032f
    val sombra = Color.Black.copy(alpha = 0.2f)
    val hasta = l + W * avance
    fun horizontal(y: Float, grosor: Float) {
        drawLine(sombra, Offset(l, y + grosor * 0.7f), Offset(hasta, y + grosor * 0.7f), grosor)
        drawLine(pa.cordon, Offset(l, y), Offset(hasta, y), grosor)
        var x = l + grosor; while (x < hasta - grosor) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(x, y - grosor / 2), Offset(x + grosor * 0.8f, y + grosor / 2), 1f); x += grosor * 1.3f }
    }
    when (c) {
        CierreSobre.CRUZ -> {
            // El cordón cruzado, con su trenzado.
            horizontal(punta, g)
            val bajo = top + (punta - top) * avance
            drawLine(sombra, Offset(cx + g * 0.7f, top), Offset(cx + g * 0.7f, bajo), g)
            drawLine(pa.cordon, Offset(cx, top), Offset(cx, bajo), g)
            var y = top + g; while (y < bajo - g) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(cx - g / 2, y), Offset(cx + g / 2, y + g * 0.8f), 1f); y += g * 1.3f }
        }
        // Dos vueltas de hilo, sin cruzar.
        CierreSobre.DOBLE -> { horizontal(punta - H * 0.09f, g * 0.8f); horizontal(punta + H * 0.09f, g * 0.8f) }
        CierreSobre.VERTICAL -> {
            // Una cinta plana que baja por el centro, con su brillo.
            val y1 = top + (bot - top) * avance; val ancho = H * 0.075f
            drawRect(sombra, Offset(cx - ancho / 2 + 2f, top), Size(ancho, y1 - top))
            drawRect(pa.cordon, Offset(cx - ancho / 2, top), Size(ancho, y1 - top))
            drawLine(Color.White.copy(alpha = 0.25f), Offset(cx - ancho * 0.22f, top), Offset(cx - ancho * 0.22f, y1), ancho * 0.16f)
        }
        CierreSobre.BANDA -> {
            // Una faja ancha de papel que da la vuelta al sobre, con dos filetes.
            val alto = H * 0.17f
            drawRect(sombra, Offset(l, punta - alto / 2 + 3f), Size(hasta - l, alto))
            drawRect(pa.cordon, Offset(l, punta - alto / 2), Size(hasta - l, alto))
            listOf(-0.36f, 0.36f).forEach { f -> drawLine(Color.White.copy(alpha = 0.35f), Offset(l, punta + alto * f), Offset(hasta, punta + alto * f), 1.2f) }
        }
        CierreSobre.HILO -> {
            // Los dos botones de cartón y el hilo que da vueltas en ocho entre ellos.
            val a = Offset(cx, punta - H * 0.16f); val b = Offset(cx, punta + H * 0.2f); val rb = H * 0.06f
            listOf(a, b).forEach { p -> drawCircle(sombra, rb, p + Offset(1.5f, 2f)); drawCircle(lerp(pa.sobre, Color.Black, 0.25f), rb, p); drawCircle(Color.Black.copy(alpha = 0.3f), rb * 0.25f, p) }
            val puntos = listOf(a, b + Offset(rb, 0f), a + Offset(-rb, 0f), b + Offset(-rb, 0f), a + Offset(rb, 0f), b + Offset(0f, rb))
            val tramos = (puntos.size - 1) * avance
            for (i in 0 until puntos.size - 1) {
                val f = (tramos - i).coerceIn(0f, 1f)
                if (f > 0f) drawLine(pa.cordon, puntos[i], puntos[i] + (puntos[i + 1] - puntos[i]) * f, g * 0.55f, StrokeCap.Round)
            }
        }
        CierreSobre.PRECINTO -> {
            // Una cinta de precinto con su rayado impreso.
            val alto = H * 0.11f
            drawRect(pa.cordon.copy(alpha = 0.85f), Offset(l, punta - alto / 2), Size(hasta - l, alto))
            var x = l; while (x < hasta) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, punta + alto / 2), Offset(minOf(x + alto * 0.6f, hasta), punta - alto / 2), alto * 0.18f); x += alto * 0.9f }
        }
    }
}

/**
 * Un sobre visto por detrás, del lado de la solapa, dentro de [cuerpo], con la forma, el adorno, el cierre y el sello del tema.
 * [abrir] (0 a 1) levanta la solapa, [romper] parte el sello, [subir] saca la carta, [cordon] (0 a 1) lo ata con el cierre del
 * tema y [brillo] hace brillar el sello. Con [abrir] la solapa sale por arriba de [cuerpo]: hay que dejarle sitio.
 */
internal fun DrawScope.dibujarSobre(t: TemaId, pa: Papeleria, e: ColorScheme, cuerpo: Rect, abrir: Float, romper: Float, subir: Float,
                                    cordon: Float, brillo: Float, hoja: Color, renglones: Color, escalaSello: Float = 1f, alfaSello: Float = 1f) {
    val forma = formaSobre(t)
    val l = cuerpo.left; val r = cuerpo.right; val top = cuerpo.top; val bot = cuerpo.bottom
    val W = cuerpo.width; val H = cuerpo.height
    val cx = cuerpo.center.x
    val punta = top + H * 0.56f
    val q = H * forma.radio
    val rad = CornerRadius(q)
    drawRoundRect(Color.Black.copy(alpha = 0.16f), Offset(l + 3f, top + 5f), cuerpo.size, rad)
    drawRoundRect(pa.sobre, cuerpo.topLeft, cuerpo.size, rad)
    // Fibras del papel.
    val az = java.util.Random(t.ordinal.toLong() * 7 + 1)
    repeat(18) { val y = top + az.nextFloat() * H; drawLine(pa.sombra.copy(alpha = 0.07f), Offset(l, y), Offset(r, y + (az.nextFloat() - 0.5f) * 4f), 1f) }

    val k = 1f - 2f * abrir
    val tipY = top + (punta - top) * k
    val solapa = rutaSolapa(forma.solapa, l, r, top, tipY)
    val silueta = Path().apply { addRoundRect(RoundRect(cuerpo, rad)) }
    val rS = H * 0.13f
    val cS = Offset(cx, tipY)
    fun sello() {
        if (alfaSello <= 0f) return
        if (brillo > 0f) drawCircle(Brush.radialGradient(listOf(pa.lacre.copy(alpha = 0.6f * brillo), Color.Transparent), center = cS, radius = rS * 2.6f), rS * 2.6f, cS)
        if (romper <= 0f) scale(escalaSello, cS) { selloCarta(t, cS, rS, pa.lacre.copy(alpha = alfaSello), e) }
        else {
            // El sello partido en dos mitades que se separan.
            val d = rS * 0.55f * romper
            listOf(-1f, 1f).forEach { lado ->
                rotate(14f * romper * lado, cS) {
                    clipRect(if (lado < 0) cS.x - rS * 2.6f - d else cS.x + d, cS.y - rS * 2f, if (lado < 0) cS.x - d else cS.x + rS * 2.6f + d, cS.y + rS * 2f) {
                        translate(d * lado, 0f) { selloCarta(t, cS, rS, pa.lacre.copy(alpha = alfaSello * (1f - 0.25f * romper)), e) }
                    }
                }
            }
        }
    }
    if (abrir > 0f) {
        // El forro de adentro, con rayas finas.
        val forro = Path().apply { moveTo(l, top); lineTo(r, top); lineTo(cx, punta); close() }
        drawPath(forro, pa.forro)
        clipPath(forro) { var x = l - H; while (x < r) { drawLine(pa.brillo.copy(alpha = 0.16f), Offset(x, top), Offset(x + H, punta + H * 0.1f), 2f); x += 9f } }
    }
    if (k < 0f) {
        // La solapa abierta queda detrás de la carta.
        drawPath(solapa, pa.forro); drawPath(solapa, pa.sombra.copy(alpha = 0.6f), style = Stroke(1.4f))
        sello()
    }
    if (abrir > 0.35f || subir > 0f) {
        // La carta, asomando del sobre.
        val sube = H * 0.62f * subir
        val pl = l + W * 0.07f; val pr = r - W * 0.07f
        val pt = top + H * 0.05f - sube
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(pl + 2f, pt + 2f), Size(pr - pl, H * 0.9f))
        drawRect(hoja, Offset(pl, pt), Size(pr - pl, H * 0.9f))
        var yy = pt + H * 0.13f; var i = 0
        while (yy < top + H * 0.5f && i < 9) {
            drawLine(renglones, Offset(pl + W * 0.06f, yy), Offset(pr - W * (if (i % 3 == 2) 0.3f else 0.06f), yy), 1.6f, StrokeCap.Round)
            yy += H * 0.08f; i++
        }
    }
    // El frente: los dos lados y el fondo, con su escote en V y las esquinas del tema.
    val frente = Path().apply {
        moveTo(l, top + H * 0.03f); lineTo(cx, top + H * 0.6f); lineTo(r, top + H * 0.03f); lineTo(r, bot - q)
        quadraticTo(r, bot, r - q, bot); lineTo(l + q, bot); quadraticTo(l, bot, l, bot - q); close()
    }
    drawPath(frente, pa.sobre)
    clipPath(frente) { adornoSobre(t, pa, cuerpo) }
    drawPath(Path().apply { moveTo(l, top + H * 0.03f); lineTo(cx, top + H * 0.6f); lineTo(r, top + H * 0.03f) }, pa.sombra.copy(alpha = 0.55f), style = Stroke(1.3f))
    drawLine(pa.sombra.copy(alpha = 0.4f), Offset(l + 2f, bot - 2f), Offset(cx - W * 0.07f, top + H * 0.62f), 1.2f)
    drawLine(pa.sombra.copy(alpha = 0.4f), Offset(r - 2f, bot - 2f), Offset(cx + W * 0.07f, top + H * 0.62f), 1.2f)
    if (k >= 0f) clipPath(silueta) {
        drawPath(solapa, pa.brillo); drawPath(solapa, pa.sombra.copy(alpha = 0.7f), style = Stroke(1.4f))
    }
    if (cordon > 0f) cierreSobre(forma.cierre, pa, cuerpo, punta, cordon)
    if (k >= 0f) sello()
}

/** El emblema sobre el sello: claro, para que se lea en relieve. */
@Composable
private fun esquemaLacre(pa: Papeleria): ColorScheme = MaterialTheme.colorScheme.copy(
    primary = Color.White.copy(alpha = 0.9f), secondary = Color.White.copy(alpha = 0.7f), tertiary = Color.White.copy(alpha = 0.8f),
    background = pa.lacre, onBackground = Color.White.copy(alpha = 0.9f), surface = pa.lacre, onSurface = Color.White,
)

private fun faltan(c: Carta, hoy: String): Long = java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(hoy), Fechas.parse(c.abrir_en))

private fun cuanto(dias: Long): String = when {
    dias <= 1 -> "mañana"
    dias < 60 -> "faltan $dias días"
    dias < 730 -> "faltan ${dias / 30} meses"
    else -> "faltan ${dias / 365} años"
}

// =====================================================================
//  La voz de las cartas en cada tema
// =====================================================================

/**
 * Lo que dicen las cartas en cada tema. [lista]: el sobre que ya se puede abrir. [sellada]: cómo quedó cerrada («… hasta el
 * 3 de mayo»). [paraYo] y [yoCorto]: a quién va, con `%s` = la fecha larga o «may 2027». [yoFuturo]: a quién va, sin fecha.
 * [saludo] y [pistaOtro]: lo que se lee en la hoja vacía (al futuro o a otra persona). [guardadaOtro]: al guardar una carta
 * para otra persona (`%s` = «@nombre»). [noAntes]: el aviso de que no se abrirá antes. [toca]: la hoja en miniatura vacía.
 */
internal class VozCarta(val lista: String, val sellada: String, val paraYo: String, val yoCorto: String, val yoFuturo: String,
                        val saludo: String, val pistaOtro: String, val guardadaOtro: String, val noAntes: String, val toca: String)

internal fun vozCarta(t: TemaId): VozCarta = when (t) {
    TemaId.ROMANO -> VozCarta("Ya puedes romper el sello", "Sellada con el anillo", "A mi yo del %s", "A mi yo de %s", "A mi yo del porvenir",
        "Salve, yo del futuro…", "Escribe tu epístola…", "Epístola para %s sellada", "Ni el César podrá abrirla antes.", "Toca para tomar el cálamo…")
    TemaId.URBE -> VozCarta("È arrivata la posta: ábrela", "Con su francobollo", "Per me, il %s", "Per me · %s", "Per me, più avanti",
        "Caro me del futuro…", "Escribe tu cartolina…", "Cartolina para %s echada al buzón", "Ni con correo urgente llega antes.", "Toca para escribir la cartolina…")
    TemaId.CIBER -> VozCarta("> clave liberada: abrir", "Cifrada", "> para mi yo del %s", "> yo@%s", "> para mi yo del futuro",
        "// hola, yo del futuro…", "// escribe tu mensaje…", "> mensaje para %s cifrado", "Sin la clave del tiempo no se descifra antes.", "> toca para escribir_")
    TemaId.NEON -> VozCarta("Ya llegó tu pedido: ábrelo", "En camino", "Para mí, el de %s", "yo, de %s", "Para mí, el de más adelante",
        "Oye, yo de más adelante:", "Cuéntale lo que pasa en la calle…", "Pedido para %s, en camino", "El repartidor no la entrega antes de la fecha.", "toca para escribir el recado")
    TemaId.HELENO -> VozCarta("Ya puedes desatarla", "Sellada en arcilla", "Para mí, el del %s", "Para mí, el de %s", "Para mí, el que vendrá",
        "Χαῖρε, yo del futuro…", "Escribe tu carta…", "Carta para %s sellada en arcilla", "Ni el oráculo la abrirá antes.", "Toca para tomar el estilo…")
    TemaId.EGIPTO -> VozCarta("Ya puedes romper el barro del sello", "Sellada con el escarabajo", "Para mi ka del %s", "Mi ka de %s", "Para el que seré en otra crecida",
        "Que vivas, prosperes y tengas salud, tú que me lees…", "Escribe tu papiro…", "Papiro para %s sellado con barro", "Ni el sacerdote lo abrirá antes.", "Toca para mojar el cálamo…")
    TemaId.MEDIEVAL -> VozCarta("Ya puede abrirse", "Lacrada y atada", "Al que seré el %s", "Al que seré en %s", "Al que seré algún día",
        "A vos, que seréis yo…", "Escribid vuestra misiva…", "Misiva para %s lacrada", "Ningún copista la abrirá antes.", "Tocad para mojar la pluma…")
    TemaId.TEMPLARIO -> VozCarta("Ya puede romperse el sello", "Sellada con el sello de la casa", "Al freire que seré el %s", "Al freire de %s", "Al freire que vendrá",
        "Hermano que serás yo:", "Escribe tu misiva, hermano…", "Misiva para %s, sellada por la casa", "Ningún hermano la abrirá antes de tiempo.", "Toca para tomar la pluma…")
    TemaId.DISTOPIA -> VozCarta("DESCLASIFICADA: PUEDE ABRIRSE", "Clasificada", "DESTINATARIO: CIUDADANO DEL %s", "DEST.: CIUDADANO DE %s", "DESTINATARIO: CIUDADANO FUTURO",
        "Ciudadano del futuro:…", "Redacte su comunicado…", "Comunicado para %s archivado", "El Ministerio prohíbe abrirla antes.", "TOQUE PARA REDACTAR")
    TemaId.REFUGIO -> VozCarta("¡Lista para abrir!", "Sellada en la bóveda", "Para el residente del %s", "Residente de %s", "Para el residente del mañana",
        "¡Hola, yo del mañana!…", "Escribe tu mensaje al vecino…", "Mensaje para %s en la bóveda", "La bóveda no se abre antes de tiempo.", "Toca para escribir con optimismo…")
    TemaId.ORDEN -> VozCarta("SELLO LEVANTADO: ABRIR", "Remachada", "AL CUSTODIO DEL %s", "CUSTODIO DE %s", "AL CUSTODIO QUE VENDRÁ",
        "Custodio del futuro:…", "Redacta el despacho…", "Despacho para %s remachado", "La Orden no permite abrirla antes.", "TOCA PARA REDACTAR EL PARTE")
    TemaId.VAPOR -> VozCarta("Llegó el telegrama: ábrelo", "Precintado en la estación", "Para mi yo del %s", "Para mi yo · %s", "Para mi yo de la próxima estación",
        "Estimado yo del futuro: STOP…", "Redacta tu telegrama…", "Telegrama para %s entregado al jefe de estación", "Ni el expreso del Norte lo lleva antes.", "Toca para dictar el telegrama…")
    TemaId.LABORATORIO -> VozCarta("Muestra lista para abrir", "Etiquetada", "Para el sujeto del %s", "Sujeto · %s", "Para el sujeto del futuro",
        "Observación para mi yo futuro:…", "Anota tu mensaje…", "Mensaje para %s etiquetado", "Protocolo: no abrir antes de la fecha.", "Toca para anotar…")
    TemaId.COLONOS -> VozCarta("Llegó el correo: ábrela", "Cerrada con oblea", "Para el vecino que seré el %s", "Para mí, %s", "Para el vecino que seré",
        "Querido yo de más adelante…", "Escribe tu carta…", "Carta para %s al correo", "El cartero no la entrega antes.", "Toca para mojar la pluma…")
    TemaId.NAPOLEON -> VozCarta("Boletín recibido: ábrelo", "Lacrada con el águila", "A mí mismo, el %s", "A mí mismo · %s", "A mí mismo, en el porvenir",
        "Mon cher moi de l’avenir…", "Redacta tu despacho…", "Despacho para %s lacrado", "Ni el Emperador la abriría antes.", "Toca para dictar el despacho…")
    TemaId.DECO -> VozCarta("Llegó el telegrama: ábrelo", "Lacrado con el abanico", "Al pasajero que seré el %s", "Al pasajero de %s", "Al pasajero que seré",
        "Querido yo de la próxima temporada…", "Dicta tu telegrama…", "Telegrama para %s en la valija", "La radio de a bordo no lo entrega antes.", "Toca para dictar al radiotelegrafista…")
    TemaId.BOTANICO -> VozCarta("Ya floreció: ábrela", "Prensada entre hojas", "Para el naturalista del %s", "Naturalista de %s", "Para el naturalista que seré",
        "Querido yo de otra estación…", "Escribe tu nota de campo…", "Carta para %s prensada", "Hay que esperar a que florezca.", "Toca para escribir a tinta sepia…")
    TemaId.RUINAS -> VozCarta("Ya puedes destapiarla", "Tapiada con una chapa", "Para quien siga en pie el %s", "Superviviente de %s", "Para quien siga en pie",
        "Si lees esto, seguimos aquí…", "Escribe tu mensaje en la pared…", "Mensaje para %s tapiado", "No se destapia antes de tiempo.", "Toca para escribir con el carboncillo…")
    TemaId.NAVIDAD -> VozCarta("¡Ya puedes desenvolverla!", "Envuelta con moño", "Para mi yo del %s, junto al fuego", "Para mi yo de %s", "Para mi yo de otra Navidad",
        "Querido yo de otra Navidad…", "Escribe tu carta junto al fuego…", "Carta para %s envuelta", "Como un regalo: no se abre antes.", "Toca para escribir junto al fuego…")
    TemaId.SEDA -> VozCarta("La caravana llegó: ábrela", "Sellada con cinabrio", "Para el viajero del %s", "Viajero de %s", "Para el viajero que seré",
        "Al viajero que seré…", "Escribe tu carta para la caravana…", "Carta para %s en la caravana", "La caravana no llega antes.", "Toca para mojar el pincel…")
    TemaId.EDO -> VozCarta("El correo a pie llegó: ábrela", "Cerrada con 〆 y el sello rojo", "Para el caminante del %s", "Caminante de %s", "Para el caminante que seré",
        "Al caminante que seré…", "Escribe tu carta para el correo a pie…", "Carta para %s, por el correo del camino real", "El correo no llega antes de su estación.", "Toca para mojar el pincel en la piedra de tinta…")
    TemaId.GOTICO -> VozCarta("Ya suenan las campanas: ábrela", "Sellada en la capilla", "Para mi alma del %s", "Para mi alma de %s", "Para mi alma de mañana",
        "Al que velará en la capilla…", "Escribe a la luz del vitral…", "Epístola para %s sellada", "No se abrirá antes de su hora.", "Toca para escribir en el folio…")
    TemaId.ARCANO -> VozCarta("Llegó su noche: rompe el lacre", "Lacrada y archivada", "Para quien yo sea en %s", "Para mí, %s", "Para quien siga investigando",
        "A quien encuentre este sobre…", "Escribe lo que sabes, antes de que se te olvide…", "Carta para %s lacrada y archivada", "El lacre no se rompe antes de su noche.", "Toca para escribir con la pluma de acero…")
    TemaId.ESPACIO -> VozCarta("CÁPSULA RECUPERADA: ABRIR", "Puesta en órbita", "DESTINO: TRIPULANTE DEL %s", "TRIPULANTE · %s", "DESTINO: TRIPULANTE FUTURO",
        "Transmisión para mi yo futuro…", "Redacta la transmisión…", "Transmisión para %s en órbita", "La cápsula no reentra antes.", "TOCA PARA TRANSMITIR")
    TemaId.ABISMO -> VozCarta("La boya salió a flote: ábrela", "Lastrada", "Para el buzo del %s", "Buzo · %s", "Para el buzo que subirá",
        "Querido yo de la superficie…", "Escribe antes de soltar el lastre…", "Boya para %s lastrada en el fondo", "No sube antes de su fecha.", "Toca para escribir en la bitácora…")
    TemaId.ANDALUS -> VozCarta("Ya puedes abrirla, con la venia", "Sellada con la estrella", "Para mi yo del %s", "A mi yo · %s", "Para mi yo de otra luna",
        "Querido yo de otra luna…", "Escribe tu carta en el patio…", "Carta para %s sellada con la estrella", "No se abre antes de su luna.", "Toca para tomar el cálamo de caña…")
    TemaId.MESO -> VozCarta("Llegó su día en la cuenta: desátalo", "Atado con henequén", "Para mi yo del %s", "Al que seré · %s", "Para el que seré en otro katún",
        "A ti, que vives en otro día de la cuenta…", "Pinta tu mensaje en el amate…", "Códice para %s atado con su cordel", "No se desata antes de su día.", "Toca para mojar el pincel en el cinabrio…")
    TemaId.MONGOL -> VozCarta("Llegó el jinete: ábrela", "Sellada con la paiza", "Para el jinete del %s", "Jinete de %s", "Para el jinete que seré",
        "Bajo el Cielo Azul, querido yo…", "Dicta tu mensaje al jinete…", "Mensaje para %s con el jinete", "Ningún jinete la entrega antes.", "Toca para dictar al escriba…")
    TemaId.RENACIMIENTO -> VozCarta("Ya puedes abrirla, maestro", "Sellada con sanguina", "Al maestro que seré el %s", "Al maestro de %s", "Al maestro que seré",
        "Caro me stesso del futuro…", "Escribe tu lettera…", "Lettera para %s sellada", "Paciencia: no se abre antes.", "Toca para afilar la pluma…")
    TemaId.PIRATA -> VozCarta("La marea trajo la botella: ya puedes descorcharla", "Lacrada con brea", "Al capitán que seré el %s", "Al capitán de %s", "Al capitán que seré",
        "Capitán del porvenir:…", "Escribe el mensaje de la botella…", "Botella para %s, echada al mar", "Aún no la trae la marea.", "Toca para mojar la pluma…")
    TemaId.URSS -> VozCarta("ПИСЬМО · YA PUEDE ABRIRSE", "Matasellada", "AL CAMARADA DEL %s", "CAMARADA DE %s", "AL CAMARADA DEL FUTURO",
        "Camarada del futuro:…", "Escribe tu carta, camarada…", "Carta para %s matasellada", "No antes de cumplir el plan.", "TOCA PARA ESCRIBIR")
    TemaId.REVOLUCION -> VozCarta("Ya puede abrirse, ciudadano", "Sellada con la escarapela", "Al ciudadano que seré el %s", "Ciudadano de %s", "Al ciudadano del porvenir",
        "Citoyen de l’avenir…", "Redacta tu queja o tu carta…", "Carta para %s firmada", "La ley no permite abrirla antes.", "Toca para tomar la pluma…")
    TemaId.ANTIGUAS -> VozCarta("La compañía llegó: ábrela", "Sellada con la gema", "Para el que seré tras muchas lunas · %s", "Tras muchas lunas · %s", "Para el que seré tras muchas lunas",
        "Al que seré cuando vuelva la compañía…", "Escribe tu mensaje para el camino…", "Mensaje para %s sellado", "Ni la magia la abre antes.", "Toca para escribir en la crónica…")
    TemaId.CUEVA -> VozCarta("Llegó su luna: desentiérrala", "Enterrada bajo la laja", "Para el que pintará dentro de muchas lunas · %s", "Dentro de muchas lunas · %s",
        "Para el que pintará dentro de muchas lunas", "Al que seré cuando vuelvan las manadas…", "Pinta tu mensaje para otro clan…", "Piedra para %s, enterrada",
        "Nadie la desentierra antes de su luna.", "Toca para pintar en la piedra…")
}

/** La letra con que se rotula el sobre en cada tema. */
private fun letraSobre(t: TemaId): FontFamily = when (t) {
    TemaId.ROMANO -> Letra.codigo
    TemaId.URBE -> Letra.placa
    TemaId.CIBER -> Letra.terminal
    TemaId.NEON -> Letra.letrero
    TemaId.HELENO -> Letra.garamond
    TemaId.EGIPTO -> Letra.messiri
    TemaId.MEDIEVAL -> LetraExtra.gotica
    TemaId.TEMPLARIO -> LetraExtra.cruzada
    TemaId.DISTOPIA -> LetraExtra.oswald
    TemaId.REFUGIO -> LetraExtra.vt323
    TemaId.ORDEN -> LetraExtra.estarcido
    TemaId.VAPOR -> LetraExtra.caldera
    TemaId.LABORATORIO -> LetraExtra.jost
    TemaId.COLONOS -> LetraExtra.fell
    TemaId.NAPOLEON -> LetraExtra.bodoniTitulo
    TemaId.DECO -> LetraExtra.poiret
    TemaId.BOTANICO -> LetraExtra.fraunces
    TemaId.RUINAS -> LetraExtra.letrero
    TemaId.NAVIDAD -> LetraExtra.cinzelDeco
    TemaId.SEDA -> LetraExtra.almendra
    TemaId.EDO -> LetraExtra.shippori
    TemaId.GOTICO -> LetraExtra.textura
    TemaId.ARCANO -> Letra.spectralCursiva
    TemaId.ESPACIO -> LetraExtra.orbita
    TemaId.ABISMO -> LetraExtra.batiscafo
    TemaId.ANDALUS -> LetraExtra.amiri
    TemaId.MESO -> LetraExtra.codice
    TemaId.MONGOL -> LetraExtra.filosofo
    TemaId.RENACIMIENTO -> LetraExtra.cormorant
    TemaId.PIRATA -> LetraExtra.corsario
    TemaId.URSS -> LetraExtra.russo
    TemaId.REVOLUCION -> LetraExtra.convencion
    TemaId.ANTIGUAS -> LetraExtra.camino
    TemaId.CUEVA -> LetraExtra.trazo
}

/** A quién va, en pocas palabras, en la voz del tema. */
fun destinatario(c: Carta, tema: TemaId = Estilo.tema): String =
    if (c.alFuturo) vozCarta(tema).yoCorto.format(Fechas.parse(c.abrir_en).let { "${MESES_CORTOS[it.monthValue - 1]} ${it.year}" }) else "Para @${c.para}"

private val MESES_CORTOS = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")

/** A quién va la carta abierta o la que se escribe: a otra persona, o al yo del futuro (con su fecha si la hay). */
private fun paraQuien(v: VozCarta, para: String, abrirEn: String?): String =
    if (para.isNotBlank()) "Para @$para" else abrirEn?.let { v.paraYo.format(Fechas.larga(it)) } ?: v.yoFuturo

/**
 * Una carta en su sobre, con el diseño del tema. Sellada: atada y con su sello, y no se abre (tiembla y dice cuánto falta).
 * Lista: el sello brilla. Abierta: la solapa levantada y la carta asomando con sus primeras palabras.
 */
@Composable
fun SobreCarta(c: Carta, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, onClick: () -> Unit) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    val estado = estadoSobre(c, hoy)
    val pa = papeleria(tema); val pg = pagina(tema)
    val v = vozCarta(tema)
    val e = esquemaLacre(pa)
    val animar = Repo.config.animaciones
    val brillo = if (estado == EstadoSobre.LISTA && animar) {
        val inf = rememberInfiniteTransition(label = "lacre")
        inf.animateFloat(0.35f, 1f, infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "brillo").value
    } else if (estado == EstadoSobre.LISTA) 0.8f else 0f
    val sacudir = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val sentir = rememberSentir()
    BoxWithConstraints(modifier.graphicsLayer { translationX = sacudir.value }.clickable {
        if (estado == EstadoSobre.SELLADA) {
            sentir(Sentidos.Evento.TOQUE)
            if (animar) scope.launch { sacudir.animateTo(0f, keyframes { durationMillis = 380; -14f at 50; 12f at 110; -9f at 170; 6f at 240; -3f at 300; 0f at 380 }) }
            android.widget.Toast.makeText(ctx, "${v.sellada} hasta el ${Fechas.larga(c.abrir_en)}: ${cuanto(faltan(c, hoy))}.", android.widget.Toast.LENGTH_SHORT).show()
        } else onClick()
    }) {
        val abierta = estado == EstadoSobre.ABIERTA
        Canvas(Modifier.fillMaxSize()) {
            val cuerpo = if (abierta) Rect(0f, size.height * 0.34f, size.width, size.height) else Rect(0f, 0f, size.width, size.height)
            dibujarSobre(tema, pa, e, cuerpo, abrir = if (abierta) 1f else 0f, romper = if (abierta) 1f else 0f, subir = if (abierta) 0.5f else 0f,
                cordon = if (estado == EstadoSobre.SELLADA) 1f else 0f, brillo = brillo, hoja = pg.color, renglones = pg.tinta.copy(alpha = 0.2f))
        }
        val fuente = letraSobre(tema)
        if (abierta) {
            // Las primeras palabras, escritas en la carta que asoma.
            Text(c.texto.trim().replace(Regex("\\s+"), " "), fontFamily = pg.texto, fontSize = 9.sp, lineHeight = 11.sp, color = pg.tinta.copy(alpha = 0.8f),
                maxLines = 2, overflow = TextOverflow.Ellipsis, fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(start = maxWidth * 0.13f, end = maxWidth * 0.13f, top = maxHeight * 0.2f))
        }
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 7.dp)) {
            val linea1 = when (estado) {
                EstadoSobre.SELLADA -> destinatario(c, tema)
                EstadoSobre.LISTA -> v.lista
                EstadoSobre.ABIERTA -> if (c.alFuturo) "De ${Fechas.corto(c.escrita)} · ${c.escrita.take(4)}" else "Para @${c.para}"
            }
            val linea2 = when (estado) {
                EstadoSobre.SELLADA -> "se abre ${Fechas.corto(c.abrir_en)} ${c.abrir_en.take(4)} · ${cuanto(faltan(c, hoy))}"
                EstadoSobre.LISTA -> "escrita el ${Fechas.corto(c.escrita)} ${c.escrita.take(4)}"
                EstadoSobre.ABIERTA -> if (c.alFuturo) "abierta ${c.abierta_el?.let { Fechas.corto(it) } ?: ""}" else "${Fechas.corto(c.escrita)} ${c.escrita.take(4)}${if (c.hora.isNotBlank()) " · ${c.hora}" else ""}"
            }
            Text(linea1, fontFamily = fuente, fontSize = 12.sp, lineHeight = 13.sp, color = if (estado == EstadoSobre.LISTA) pa.lacre.let { if (abs(it.luminance() - pa.sobre.luminance()) > 0.25f) it else pa.tinta } else pa.tinta,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(linea2, fontFamily = fuente, fontSize = 9.5.sp, lineHeight = 11.sp, color = pa.tinta.copy(alpha = 0.72f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Abrir una carta que ya llegó a su fecha: el sello brilla, se parte, se levanta la solapa y sale la carta. Luego [terminar]. */
@Composable
fun AperturaCarta(c: Carta, terminar: () -> Unit) {
    val animar = Repo.config.animaciones
    val tema = Estilo.tema
    val t = remember { Animatable(if (animar) 0f else 1f) }
    val sentir = rememberSentir()
    val pa = papeleria(tema); val pg = pagina(tema)
    val e = esquemaLacre(pa)
    LaunchedEffect(Unit) {
        sentir(Sentidos.Evento.ABRIR)
        if (animar) t.animateTo(1f, tween(2000, easing = LinearEasing))
        delay(300); terminar()
    }
    Dialog(onDismissRequest = terminar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.62f)).clickable { terminar() }, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(Modifier.fillMaxWidth(0.86f).aspectRatio(1.05f)) {
                    val v = t.value
                    val brillo = (1f - abs(v - 0.12f) / 0.12f).coerceIn(0f, 1f)
                    val romper = ((v - 0.14f) / 0.2f).coerceIn(0f, 1f)
                    val abrir = ((v - 0.3f) / 0.32f).coerceIn(0f, 1f)
                    val subir = ((v - 0.58f) / 0.42f).coerceIn(0f, 1f)
                    dibujarSobre(tema, pa, e, Rect(0f, size.height * 0.42f, size.width, size.height), abrir, romper, subir, 0f, brillo, pg.color, pg.tinta.copy(alpha = 0.22f))
                }
                Text("Escrita el ${Fechas.larga(c.escrita)}", style = MaterialTheme.typography.titleMedium, color = Color.White, modifier = Modifier.padding(top = 18.dp))
                Text("${java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(c.escrita), Fechas.parse(Fechas.diaLogico()))} días después", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

/** Sellar una carta: la hoja entra al sobre, se cierra la solapa, cae el sello y se ata con el cierre del tema. Luego [terminar]. */
@Composable
fun SelladoCarta(c: Carta, terminar: () -> Unit) {
    val animar = Repo.config.animaciones
    val tema = Estilo.tema
    val t = remember { Animatable(if (animar) 0f else 1f) }
    val sentir = rememberSentir()
    val pa = papeleria(tema); val pg = pagina(tema)
    val voz = vozCarta(tema)
    val e = esquemaLacre(pa)
    LaunchedEffect(Unit) {
        if (animar) {
            t.animateTo(0.62f, tween(1150, easing = LinearEasing))
            sentir(Sentidos.Evento.GUARDAR)
            t.animateTo(1f, tween(800, easing = LinearEasing))
        } else sentir(Sentidos.Evento.GUARDAR)
        delay(900); terminar()
    }
    Dialog(onDismissRequest = terminar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.62f)).clickable { terminar() }, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(Modifier.fillMaxWidth(0.86f).aspectRatio(1.05f)) {
                    val v = t.value
                    val subir = (1f - v / 0.3f).coerceIn(0f, 1f)
                    val abrir = (1f - (v - 0.28f) / 0.3f).coerceIn(0f, 1f)
                    val caer = ((v - 0.56f) / 0.12f).coerceIn(0f, 1f)
                    val cordon = if (c.alFuturo) ((v - 0.72f) / 0.28f).coerceIn(0f, 1f) else 0f
                    dibujarSobre(tema, pa, e, Rect(0f, size.height * 0.42f, size.width, size.height), abrir, 0f, subir, cordon, 0f, pg.color, pg.tinta.copy(alpha = 0.22f),
                        escalaSello = 2.2f - 1.2f * caer, alfaSello = caer)
                }
                Text(if (c.alFuturo) "${voz.sellada} hasta el ${Fechas.larga(c.abrir_en)}" else voz.guardadaOtro.format("@${c.para}"), style = MaterialTheme.typography.titleMedium,
                    color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 18.dp, start = 24.dp, end = 24.dp))
                if (c.alFuturo) Text("${voz.noAntes} ${cuanto(faltan(c, Fechas.diaLogico())).replaceFirstChar { it.uppercase() }}.", style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
            }
        }
    }
}

// =====================================================================
//  La carta abierta y la carta que se escribe
// =====================================================================

/** Encabezado de una carta: emblema, lugar, fecha y hora a la derecha, y a quién va. */
@Composable
fun EncabezadoCarta(t: TemaId, p: Pagina, escrita: String, hora: String, lugar: String, para: String, abrirEn: String?) {
    val ty = MaterialTheme.typography
    val v = vozCarta(t)
    Column(Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            Emblema(t, 38.dp, MaterialTheme.colorScheme, animado = false)
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(listOfNotNull(lugar.ifBlank { null }, Fechas.larga(escrita)).joinToString(", "), fontFamily = p.texto, fontSize = (p.tam - 4).sp,
                    fontStyle = FontStyle.Italic, color = p.tinta, textAlign = TextAlign.End)
                Text(Estilo.fecha(escrita) + if (hora.isNotBlank()) " · $hora" else "", style = ty.labelMedium, color = p.tenue, textAlign = TextAlign.End)
            }
        }
        Text(paraQuien(v, para, abrirEn), style = ty.titleLarge, color = p.acento, modifier = Modifier.padding(top = 22.dp))
        if (para.isBlank() && abrirEn != null) Text("${v.sellada.replaceFirstChar { it.lowercase() }} hasta ese día", style = ty.labelMedium, color = p.tenue)
        Meandro(Modifier.padding(top = 10.dp, bottom = 16.dp), color = p.acento.copy(alpha = 0.5f))
    }
}

/** Una carta abierta a pantalla completa, sobre el papel del tema, con firma y el sello del tema al final. */
@Composable
fun LecturaCarta(c: Carta, volver: () -> Unit, acciones: List<Pair<String, () -> Unit>>, tema: TemaId = Estilo.tema) {
    val p = pagina(tema)
    var menu by remember { mutableStateOf(false) }
    val pa = papeleria(tema)
    val v = vozCarta(tema)
    val e = esquemaLacre(pa)
    MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
        Box(Modifier.fillMaxSize()) {
            FondoPagina(tema, p)
            Column(Modifier.fillMaxSize()) {
                BarraPagina(p, volver, "${Texto.palabras(c.texto)} palabras") {
                    Box {
                        Text("⋯", fontSize = 22.sp, color = p.acento, modifier = Modifier.clickable { menu = true }.padding(horizontal = 10.dp))
                        Menu(expanded = menu, onDismissRequest = { menu = false }, containerColor = p.color) {
                            acciones.forEach { (t, f) -> OpcionMenu(text = { Text(t, color = p.tinta) }, onClick = { menu = false; f() }) }
                        }
                    }
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = if (p.rayas == Rayas.NUMEROS) 52.dp else 30.dp, end = 26.dp, bottom = 40.dp)) {
                    EncabezadoCarta(tema, p, c.escrita, c.hora, c.lugar, c.para, if (c.alFuturo) c.abrir_en else null)
                    var layout by remember(c.texto) { mutableStateOf<TextLayoutResult?>(null) }
                    val csPag = MaterialTheme.colorScheme
                    val texto = AnnotatedString(c.texto.trim()).let { if (p.capitular != null) conCapitular(it, p.capitular, p.acento) else it }.let { Marcas.anotar(it, tema, csPag, conFondo = false) }
                    SelectionContainer {
                        Text(texto, color = p.tinta, fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, onTextLayout = { layout = it },
                            modifier = Modifier.fillMaxWidth().rayado(p, layout).drawBehind { layout?.let { l -> with(Marcas) { dibujar(l, texto.text, tema, csPag) } } })
                    }
                    Text("— " + Repo.config.perfil.nombre.ifBlank { "yo" }, fontFamily = p.capitular ?: p.texto, fontSize = (p.tam + 3).sp, color = p.tinta, textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth().padding(top = 22.dp))
                    Canvas(Modifier.fillMaxWidth().padding(top = 18.dp).size(64.dp)) { selloCarta(tema, Offset(size.width / 2, size.height / 2), size.height * 0.38f, pa.lacre, e) }
                    Text(listOfNotNull(if (c.alFuturo) "${v.sellada.replaceFirstChar { it.lowercase() }} el ${Fechas.larga(c.escrita)}" else "escrita el ${Fechas.larga(c.escrita)}",
                        c.abierta_el?.takeIf { c.alFuturo }?.let { "abierta el ${Fechas.larga(it)}" }).joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium, color = p.tenue, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                    Text(colofon(tema), color = p.acento, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 18.dp))
                }
            }
        }
    }
}

/** La carta que se está escribiendo: sobrevive si sales y vuelves (mientras la app siga abierta). */
object BorradorCarta {
    var texto by mutableStateOf("")
    var para by mutableStateOf("")
    var alFuturo by mutableStateOf(true)
    var plazo by mutableStateOf(365L)
    var fecha by mutableStateOf<String?>(null)
    fun limpiar() { texto = ""; para = ""; alFuturo = true; plazo = 365L; fecha = null }
    fun abrirEn(hoy: String = Fechas.diaLogico()): String = fecha ?: Fechas.sumar(hoy, plazo)
}

/** Escribir la carta a pantalla completa, sobre el papel del tema, con el encabezado de carta. El botón dice lo de cada tema. */
@Composable
fun DialogoCarta(lugar: String, hora: String, cerrar: () -> Unit) {
    val hoy = Fechas.diaLogico()
    val tema = Estilo.tema
    val v = vozCarta(tema)
    Dialog(onDismissRequest = cerrar, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        EscritorTema(hoy, BorradorCarta.texto, { BorradorCarta.texto = it }, cerrar, modo = Escrito.CARTA,
            encabezado = { p -> EncabezadoCarta(tema, p, hoy, hora, lugar, if (BorradorCarta.alFuturo) "" else BorradorCarta.para, if (BorradorCarta.alFuturo) BorradorCarta.abrirEn(hoy) else null) },
            pista = if (BorradorCarta.alFuturo) v.saludo else v.pistaOtro)
    }
}

/** Una hoja de carta en miniatura: así se ve lo que llevas escrito. Al tocarla se abre a pantalla completa. */
@Composable
fun HojaCarta(texto: String, para: String, abrirEn: String?, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, onClick: () -> Unit) {
    val p = pagina(tema)
    val v = vozCarta(tema)
    Box(modifier.fillMaxWidth().aspectRatio(1.2f).clickable(onClick = onClick)) {
        FondoPagina(tema, p)
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(Fechas.larga(Fechas.diaLogico()), fontFamily = p.texto, fontSize = 11.sp, fontStyle = FontStyle.Italic, color = p.tenue, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            Text(paraQuien(v, para, abrirEn), style = MaterialTheme.typography.titleMedium, color = p.acento, modifier = Modifier.padding(top = 10.dp, bottom = 8.dp))
            Text(texto.ifBlank { v.toca }, fontFamily = p.texto, fontSize = (p.tam - 3).sp, lineHeight = (p.interlinea - 4).sp,
                color = if (texto.isBlank()) p.tenue else p.tinta, maxLines = 7, overflow = TextOverflow.Ellipsis)
        }
        Text("${Texto.palabras(texto)} palabras", style = MaterialTheme.typography.labelSmall, color = p.tenue, modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp))
    }
}
