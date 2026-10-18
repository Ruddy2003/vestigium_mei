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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
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
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Cartas: al futuro (selladas hasta su fecha) y a otras personas
// =====================================================================

/** Cómo está el sobre: cerrado con cordón y lacre, con el lacre brillando porque ya llegó su fecha, o abierto. */
enum class EstadoSobre { SELLADA, LISTA, ABIERTA }

fun estadoSobre(c: Carta, hoy: String = Fechas.diaLogico()): EstadoSobre = when {
    c.sellada(hoy) -> EstadoSobre.SELLADA
    c.lista(hoy) -> EstadoSobre.LISTA
    else -> EstadoSobre.ABIERTA
}

/** La papelería de cada tema: el sobre, su forro, el lacre, el cordón y la tinta con que se rotula. */
class Papeleria(val sobre: Color, val sombra: Color, val brillo: Color, val forro: Color, val lacre: Color, val cordon: Color, val tinta: Color)

fun papeleria(t: TemaId): Papeleria {
    val m = materia(t)
    val lacre = when {
        t == TemaId.URSS -> m.cuerpo
        m.extra != m.acento -> m.extra
        m.detalle.luminance() < 0.6f -> m.detalle
        else -> m.acento
    }
    return when {
        soporte(t) == Soporte.SOBRE -> Papeleria(m.cuerpo, m.sombra, m.brillo, m.detalle, m.acento, m.extra, m.textoCuerpo)
        t == TemaId.DISTOPIA -> Papeleria(m.cuerpo, m.sombra, m.brillo, m.sombra, m.acento, m.detalle, m.tinta)
        t == TemaId.REFUGIO -> Papeleria(m.hoja, Color(0xFF010C05), lerp(m.hoja, m.acento, 0.14f), m.cuerpo, m.acento, m.detalle, m.tinta)
        soporte(t) == Soporte.PANTALLA -> Papeleria(m.cuerpo, m.sombra, m.brillo, m.hoja, m.acento, m.detalle, m.tinta)
        else -> Papeleria(lerp(m.hoja, m.sombra, 0.06f), lerp(m.hoja, m.sombra, 0.5f), lerp(m.hoja, Color.White, 0.3f), m.cuerpo, lacre, m.acento, m.tinta)
    }
}

/** El lacre: una gota de cera de borde irregular con el emblema del tema en relieve. */
private fun DrawScope.lacre(t: TemaId, c: Offset, r: Float, color: Color, e: ColorScheme) {
    val borde = Path()
    for (i in 0..40) {
        val a = 2 * PI * i / 40
        val rr = r * (1f + 0.07f * sin(a * 5 + 1).toFloat() + 0.03f * sin(a * 11).toFloat())
        val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
        if (i == 0) borde.moveTo(p.x, p.y) else borde.lineTo(p.x, p.y)
    }
    borde.close()
    drawPath(borde, Color.Black.copy(alpha = 0.18f), style = Stroke(r * 0.12f))
    drawPath(borde, color)
    drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.28f), Color.Transparent), center = c - Offset(r * 0.35f, r * 0.35f), radius = r * 0.8f), r, c)
    drawCircle(Color.Black.copy(alpha = 0.2f), r * 0.76f, c, style = Stroke(r * 0.07f))
    val k = r * 0.58f
    inset(c.x - k, c.y - k, size.width - (c.x + k), size.height - (c.y + k)) { dibujarEmblema(t, 0.3f, e, true) }
}

/**
 * Un sobre visto por detrás, del lado de la solapa, dentro de [cuerpo]. [abrir] (0 a 1) levanta la solapa, [romper] parte el lacre,
 * [subir] saca la carta, [cordon] (0 a 1) lo cruza con un cordón atado y [brillo] hace brillar el lacre. Con [abrir] la solapa
 * sale por arriba de [cuerpo]: hay que dejarle sitio.
 */
internal fun DrawScope.dibujarSobre(t: TemaId, pa: Papeleria, e: ColorScheme, cuerpo: Rect, abrir: Float, romper: Float, subir: Float,
                                    cordon: Float, brillo: Float, hoja: Color, renglones: Color, escalaSello: Float = 1f, alfaSello: Float = 1f) {
    val l = cuerpo.left; val r = cuerpo.right; val top = cuerpo.top; val bot = cuerpo.bottom
    val W = cuerpo.width; val H = cuerpo.height
    val cx = cuerpo.center.x
    val punta = top + H * 0.56f
    val rad = CornerRadius(H * 0.035f)
    drawRoundRect(Color.Black.copy(alpha = 0.16f), Offset(l + 3f, top + 5f), cuerpo.size, rad)
    drawRoundRect(pa.sobre, cuerpo.topLeft, cuerpo.size, rad)
    // Fibras del papel.
    val az = java.util.Random(t.ordinal.toLong() * 7 + 1)
    repeat(18) { val y = top + az.nextFloat() * H; drawLine(pa.sombra.copy(alpha = 0.07f), Offset(l, y), Offset(r, y + (az.nextFloat() - 0.5f) * 4f), 1f) }

    val k = 1f - 2f * abrir
    val tipY = top + (punta - top) * k
    val solapa = Path().apply { moveTo(l, top); lineTo(cx, tipY); lineTo(r, top); close() }
    val rS = H * 0.13f
    val cS = Offset(cx, tipY)
    fun sello() {
        if (alfaSello <= 0f) return
        if (brillo > 0f) drawCircle(Brush.radialGradient(listOf(pa.lacre.copy(alpha = 0.6f * brillo), Color.Transparent), center = cS, radius = rS * 2.6f), rS * 2.6f, cS)
        if (romper <= 0f) scale(escalaSello, cS) { lacre(t, cS, rS, pa.lacre.copy(alpha = alfaSello), e) }
        else {
            // El lacre partido en dos mitades que se separan.
            val d = rS * 0.55f * romper
            listOf(-1f, 1f).forEach { lado ->
                rotate(14f * romper * lado, cS) {
                    clipRect(if (lado < 0) cS.x - rS * 1.4f - d else cS.x + d, cS.y - rS * 1.5f, if (lado < 0) cS.x - d else cS.x + rS * 1.4f + d, cS.y + rS * 1.5f) {
                        translate(d * lado, 0f) { lacre(t, cS, rS, pa.lacre.copy(alpha = alfaSello * (1f - 0.25f * romper)), e) }
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
    // El frente: los dos lados y el fondo, con su escote en V.
    val frente = Path().apply { moveTo(l, top + H * 0.03f); lineTo(cx, top + H * 0.6f); lineTo(r, top + H * 0.03f); lineTo(r, bot - 3f); quadraticTo(r, bot, r - 3f, bot); lineTo(l + 3f, bot); quadraticTo(l, bot, l, bot - 3f); close() }
    drawPath(frente, pa.sobre)
    drawPath(Path().apply { moveTo(l, top + H * 0.03f); lineTo(cx, top + H * 0.6f); lineTo(r, top + H * 0.03f) }, pa.sombra.copy(alpha = 0.55f), style = Stroke(1.3f))
    drawLine(pa.sombra.copy(alpha = 0.4f), Offset(l + 2f, bot - 2f), Offset(cx - W * 0.07f, top + H * 0.62f), 1.2f)
    drawLine(pa.sombra.copy(alpha = 0.4f), Offset(r - 2f, bot - 2f), Offset(cx + W * 0.07f, top + H * 0.62f), 1.2f)
    if (k >= 0f) {
        drawPath(solapa, pa.brillo); drawPath(solapa, pa.sombra.copy(alpha = 0.7f), style = Stroke(1.4f))
    }
    if (cordon > 0f) {
        // El cordón cruzado, con su trenzado.
        val g = H * 0.032f
        val hasta = l + W * cordon
        drawLine(Color.Black.copy(alpha = 0.2f), Offset(l, punta + g * 0.7f), Offset(hasta, punta + g * 0.7f), g)
        drawLine(pa.cordon, Offset(l, punta), Offset(hasta, punta), g)
        var x = l + g; while (x < hasta - g) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(x, punta - g / 2), Offset(x + g * 0.8f, punta + g / 2), 1f); x += g * 1.3f }
        val bajo = top + (punta - top) * cordon
        drawLine(Color.Black.copy(alpha = 0.2f), Offset(cx + g * 0.7f, top), Offset(cx + g * 0.7f, bajo), g)
        drawLine(pa.cordon, Offset(cx, top), Offset(cx, bajo), g)
        var y = top + g; while (y < bajo - g) { drawLine(Color.Black.copy(alpha = 0.22f), Offset(cx - g / 2, y), Offset(cx + g / 2, y + g * 0.8f), 1f); y += g * 1.3f }
    }
    if (k >= 0f) sello()
}

/** El emblema sobre la cera: claro, para que se lea en relieve. */
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

/** A quién va, en pocas palabras. */
fun destinatario(c: Carta): String = if (c.alFuturo) "Para mi yo de ${Fechas.parse(c.abrir_en).let { "${MESES_CORTOS[it.monthValue - 1]} ${it.year}" }}" else "Para @${c.para}"

private val MESES_CORTOS = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")

/**
 * Una carta en su sobre, con el diseño del tema. Sellada: cordón y lacre, y no se abre (tiembla y dice cuánto falta).
 * Lista: el lacre brilla. Abierta: la solapa levantada y la carta asomando con sus primeras palabras.
 */
@Composable
fun SobreCarta(c: Carta, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, onClick: () -> Unit) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    val estado = estadoSobre(c, hoy)
    val pa = papeleria(tema); val pg = pagina(tema)
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
            android.widget.Toast.makeText(ctx, "Sellada hasta el ${Fechas.larga(c.abrir_en)}: ${cuanto(faltan(c, hoy))}.", android.widget.Toast.LENGTH_SHORT).show()
        } else onClick()
    }) {
        val abierta = estado == EstadoSobre.ABIERTA
        Canvas(Modifier.fillMaxSize()) {
            val cuerpo = if (abierta) Rect(0f, size.height * 0.34f, size.width, size.height) else Rect(0f, 0f, size.width, size.height)
            dibujarSobre(tema, pa, e, cuerpo, abrir = if (abierta) 1f else 0f, romper = if (abierta) 1f else 0f, subir = if (abierta) 0.5f else 0f,
                cordon = if (estado == EstadoSobre.SELLADA) 1f else 0f, brillo = brillo, hoja = pg.color, renglones = pg.tinta.copy(alpha = 0.2f))
        }
        val fuente = when (soporte(tema)) { Soporte.PANTALLA -> if (tema == TemaId.REFUGIO) LetraExtra.vt323 else Letra.codigo; Soporte.CARPETA -> Letra.codigo; else -> Letra.garamond }
        if (abierta) {
            // Las primeras palabras, escritas en la carta que asoma.
            Text(c.texto.trim().replace(Regex("\\s+"), " "), fontFamily = pg.texto, fontSize = 9.sp, lineHeight = 11.sp, color = pg.tinta.copy(alpha = 0.8f),
                maxLines = 2, overflow = TextOverflow.Ellipsis, fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(start = maxWidth * 0.13f, end = maxWidth * 0.13f, top = maxHeight * 0.2f))
        }
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 7.dp)) {
            val linea1 = when (estado) {
                EstadoSobre.SELLADA -> destinatario(c)
                EstadoSobre.LISTA -> "Ya puedes abrirla"
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

/** Abrir una carta que ya llegó a su fecha: el lacre brilla, se parte, se levanta la solapa y sale la carta. Luego [terminar]. */
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

/** Sellar una carta: la hoja entra al sobre, se cierra la solapa, cae el lacre y se ata el cordón. Luego [terminar]. */
@Composable
fun SelladoCarta(c: Carta, terminar: () -> Unit) {
    val animar = Repo.config.animaciones
    val tema = Estilo.tema
    val t = remember { Animatable(if (animar) 0f else 1f) }
    val sentir = rememberSentir()
    val pa = papeleria(tema); val pg = pagina(tema)
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
                Text(if (c.alFuturo) "Sellada hasta el ${Fechas.larga(c.abrir_en)}" else "Carta para @${c.para} guardada", style = MaterialTheme.typography.titleMedium,
                    color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 18.dp, start = 24.dp, end = 24.dp))
                if (c.alFuturo) Text("No se podrá abrir antes. ${cuanto(faltan(c, Fechas.diaLogico())).replaceFirstChar { it.uppercase() }}.", style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f))
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
        Text(if (para.isBlank()) "Para mi yo del ${abrirEn?.let { Fechas.larga(it) } ?: "futuro"}" else "Para @$para", style = ty.titleLarge, color = p.acento,
            modifier = Modifier.padding(top = 22.dp))
        if (para.isBlank() && abrirEn != null) Text("sellada hasta ese día", style = ty.labelMedium, color = p.tenue)
        Meandro(Modifier.padding(top = 10.dp, bottom = 16.dp), color = p.acento.copy(alpha = 0.5f))
    }
}

/** Una carta abierta a pantalla completa, sobre el papel del tema, con firma y lacre al final. */
@Composable
fun LecturaCarta(c: Carta, volver: () -> Unit, acciones: List<Pair<String, () -> Unit>>, tema: TemaId = Estilo.tema) {
    val p = pagina(tema)
    var menu by remember { mutableStateOf(false) }
    val pa = papeleria(tema)
    val e = esquemaLacre(pa)
    MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
        Box(Modifier.fillMaxSize()) {
            FondoPagina(tema, p)
            Column(Modifier.fillMaxSize()) {
                BarraPagina(p, volver, "${Texto.palabras(c.texto)} palabras") {
                    Box {
                        Text("⋯", fontSize = 22.sp, color = p.acento, modifier = Modifier.clickable { menu = true }.padding(horizontal = 10.dp))
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }, containerColor = p.color) {
                            acciones.forEach { (t, f) -> DropdownMenuItem(text = { Text(t, color = p.tinta) }, onClick = { menu = false; f() }) }
                        }
                    }
                }
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(start = if (p.rayas == Rayas.NUMEROS) 52.dp else 30.dp, end = 26.dp, bottom = 40.dp)) {
                    EncabezadoCarta(tema, p, c.escrita, c.hora, c.lugar, c.para, if (c.alFuturo) c.abrir_en else null)
                    var layout by remember(c.texto) { mutableStateOf<TextLayoutResult?>(null) }
                    val texto = AnnotatedString(c.texto.trim()).let { if (p.capitular != null) conCapitular(it, p.capitular, p.acento) else it }
                    SelectionContainer {
                        Text(texto, color = p.tinta, fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, onTextLayout = { layout = it },
                            modifier = Modifier.fillMaxWidth().rayado(p, layout))
                    }
                    Text("— " + Repo.config.perfil.nombre.ifBlank { "yo" }, fontFamily = p.capitular ?: p.texto, fontSize = (p.tam + 3).sp, color = p.tinta, textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth().padding(top = 22.dp))
                    Canvas(Modifier.fillMaxWidth().padding(top = 18.dp).size(64.dp)) { lacre(tema, Offset(size.width / 2, size.height / 2), size.height * 0.42f, pa.lacre, e) }
                    Text(listOfNotNull(if (c.alFuturo) "sellada el ${Fechas.larga(c.escrita)}" else "escrita el ${Fechas.larga(c.escrita)}",
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

/** Escribir la carta a pantalla completa, sobre el papel del tema, con el encabezado de carta. */
@Composable
fun DialogoCarta(lugar: String, hora: String, cerrar: () -> Unit) {
    val hoy = Fechas.diaLogico()
    val tema = Estilo.tema
    Dialog(onDismissRequest = cerrar, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        EscritorTema(hoy, BorradorCarta.texto, { BorradorCarta.texto = it }, cerrar, modo = Escrito.CARTA,
            encabezado = { p -> EncabezadoCarta(tema, p, hoy, hora, lugar, if (BorradorCarta.alFuturo) "" else BorradorCarta.para, if (BorradorCarta.alFuturo) BorradorCarta.abrirEn(hoy) else null) },
            pista = if (BorradorCarta.alFuturo) "Querido yo del futuro…" else "Escribe tu carta…", textoBoton = "listo")
    }
}

/** Una hoja de carta en miniatura: así se ve lo que llevas escrito. Al tocarla se abre a pantalla completa. */
@Composable
fun HojaCarta(texto: String, para: String, abrirEn: String?, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, onClick: () -> Unit) {
    val p = pagina(tema)
    Box(modifier.fillMaxWidth().aspectRatio(1.2f).clickable(onClick = onClick)) {
        FondoPagina(tema, p)
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(Fechas.larga(Fechas.diaLogico()), fontFamily = p.texto, fontSize = 11.sp, fontStyle = FontStyle.Italic, color = p.tenue, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            Text(if (para.isBlank()) "Para mi yo del ${abrirEn?.let { Fechas.larga(it) } ?: "futuro"}" else "Para @$para", style = MaterialTheme.typography.titleMedium, color = p.acento,
                modifier = Modifier.padding(top = 10.dp, bottom = 8.dp))
            Text(texto.ifBlank { "Toca para escribir…" }, fontFamily = p.texto, fontSize = (p.tam - 3).sp, lineHeight = (p.interlinea - 4).sp,
                color = if (texto.isBlank()) p.tenue else p.tinta, maxLines = 7, overflow = TextOverflow.Ellipsis)
        }
        Text("${Texto.palabras(texto)} palabras", style = MaterialTheme.typography.labelSmall, color = p.tenue, modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp))
    }
}
