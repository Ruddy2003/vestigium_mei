@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * El único selector de hora de la app (el de gastos): «ahora» o «otra fecha u hora…», con atajos opcionales
 * («hace 15 min»…). [valor] null = ahora. Lo de madrugada avisa a qué día cuenta. «Otra fecha u hora…» abre primero
 * el calendario y luego el reloj de 24 h, los dos con el cuerpo de diálogo del tema.
 */
@Composable
fun SelectorHora(valor: LocalDateTime?, atajos: List<Int> = emptyList(), etiquetaAhora: String = "ahora", permitirFuturo: Boolean = false,
                 ayer: Boolean = false, onCambio: (LocalDateTime?) -> Unit) {
    val ahora = remember { LocalDateTime.now().withSecond(0).withNano(0) }
    // 0: nada abierto; 1: eligiendo el día; 2: eligiendo la hora de [dia].
    var paso by remember { mutableStateOf(0) }
    var base by remember { mutableStateOf(ahora) }
    var dia by remember { mutableStateOf(ahora.toLocalDate()) }
    fun elegir() { base = valor ?: LocalDateTime.now(); paso = 1 }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Ficha(etiquetaAhora, valor == null) { onCambio(null) }
        atajos.forEach { m ->
            val t = ahora.minusMinutes(m.toLong())
            Ficha(if (m < 60) "hace $m min" else if (m % 60 == 0) "hace ${m / 60} h" else "hace ${m / 60} h ${m % 60}", valor == t) { onCambio(t) }
        }
        // «ayer»: la misma hora de ayer (para lo que olvidaste anotar).
        val ayerT = ahora.minusDays(1)
        if (ayer) Ficha("ayer", valor == ayerT) { onCambio(ayerT) }
        val propio = valor != null && atajos.none { ahora.minusMinutes(it.toLong()) == valor } && !(ayer && valor == ayerT)
        Ficha(if (!propio) "otra fecha u hora…" else "${Fechas.corto(Fechas.iso(valor!!.toLocalDate()))} · ${"%02d:%02d".format(valor.hour, valor.minute)}", propio) { elegir() }
    }
    valor?.takeIf { it.hour < 5 }?.let {
        Ayuda("De madrugada: cuenta para el ${Fechas.corto(Fechas.iso(it.toLocalDate().minusDays(1)))}.", Modifier.padding(top = 4.dp))
    }
    // Sin futuro, se puede hasta hoy (como el calendario de Android con maxDate = ahora).
    if (paso == 1) DialogoFecha(inicial = base.toLocalDate(), maximo = if (permitirFuturo) null else LocalDate.now(), onCerrar = { paso = 0 }) { d -> dia = d; paso = 2 }
    if (paso == 2) DialogoHora(base.hour, base.minute, onCerrar = { paso = 0 }) { h, min ->
        paso = 0
        onCambio(LocalDateTime.of(dia.year, dia.monthValue, dia.dayOfMonth, h, min))
    }
}

/**
 * Pedidos de día u hora que vienen de fuera de la composición (un toque que llama a [elegirFecha] o [elegirHora]).
 * Los atiende [AnfitrionSelectores], que va dentro de la capa de Deshacer de cada actividad; si no hay ninguno, se usa
 * el selector de Android.
 */
internal object PedidoSelector {
    class Fecha(val inicial: LocalDate, val minimo: LocalDate?, val maximo: LocalDate?, val listo: (LocalDate) -> Unit)
    class Hora(val hora: Int, val minuto: Int, val listo: (Int, Int) -> Unit)

    var fecha by mutableStateOf<Fecha?>(null)
    var hora by mutableStateOf<Hora?>(null)
    /** Cuántos anfitriones hay en pantalla. */
    var anfitriones = 0
}

/** Muestra los diálogos de día y hora que se pidieron con [elegirFecha] y [elegirHora]. */
@Composable
internal fun AnfitrionSelectores() {
    DisposableEffect(Unit) { PedidoSelector.anfitriones++; onDispose { PedidoSelector.anfitriones-- } }
    PedidoSelector.fecha?.let { p ->
        DialogoFecha(inicial = p.inicial, minimo = p.minimo, maximo = p.maximo, onCerrar = { PedidoSelector.fecha = null }) { d ->
            PedidoSelector.fecha = null
            p.listo(d)
        }
    }
    PedidoSelector.hora?.let { p ->
        DialogoHora(p.hora, p.minuto, onCerrar = { PedidoSelector.hora = null }) { h, m ->
            PedidoSelector.hora = null
            p.listo(h, m)
        }
    }
}

/**
 * Abre el calendario del tema para elegir un día (AAAA-MM-DD). Con [desdeManana], solo días futuros; [minimo] y
 * [maximo] limitan más. Si no hay dónde dibujarlo (una actividad sin la capa de Deshacer), abre el de Android.
 */
fun elegirFecha(ctx: android.content.Context, inicial: String?, desdeManana: Boolean = false, minimo: LocalDate? = null, maximo: LocalDate? = null,
                onFecha: (String) -> Unit) {
    val base = inicial?.let { runCatching { Fechas.parse(it) }.getOrNull() } ?: LocalDate.now().plusDays(if (desdeManana) 1 else 0)
    val manana = if (desdeManana) LocalDate.now().plusDays(1) else null
    val desde = if (manana != null && (minimo == null || manana.isAfter(minimo))) manana else minimo
    if (PedidoSelector.anfitriones > 0) {
        PedidoSelector.fecha = PedidoSelector.Fecha(base, desde, maximo) { d -> onFecha(Fechas.iso(d)) }
        return
    }
    DatePickerDialog(ctx, { _, y, m, d -> onFecha(Fechas.iso(LocalDate.of(y, m + 1, d))) }, base.year, base.monthValue - 1, base.dayOfMonth).apply {
        if (desdeManana) datePicker.minDate = System.currentTimeMillis() + 86_400_000L
        else if (minimo != null) datePicker.minDate = Fechas.millisDe(minimo.atStartOfDay())
        if (maximo != null) datePicker.maxDate = Fechas.millisDe(maximo.atTime(23, 59))
    }.show()
}

/** Abre el reloj de 24 h del tema para elegir una hora. Sin anfitrión, abre el de Android. */
fun elegirHora(ctx: android.content.Context, hora: Int, minuto: Int, onHora: (Int, Int) -> Unit) {
    if (PedidoSelector.anfitriones > 0) {
        PedidoSelector.hora = PedidoSelector.Hora(hora, minuto, onHora)
        return
    }
    TimePickerDialog(ctx, { _, h, m -> onHora(h, m) }, hora, minuto, true).show()
}

/** Hora local → instante, con la zona que regía entonces (H-1-7). */
fun LocalDateTime.aMillis(): Long = Fechas.millisDe(this)
fun horaLocal(ms: Long): LocalDateTime = Fechas.local(ms).withSecond(0).withNano(0)

/** El único selector de duración: atajos y «otra» en minutos. */
@Composable
fun SelectorDuracion(minutos: Int?, opciones: List<Int> = listOf(5, 10, 15, 30, 45, 60, 90, 120), onCambio: (Int?) -> Unit) {
    var texto by remember { mutableStateOf(if (minutos != null && minutos !in opciones) "$minutos" else "") }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        opciones.forEach { d ->
            Ficha(duracionTexto(d), minutos == d && texto.isEmpty()) { texto = ""; onCambio(if (minutos == d) null else d) }
        }
    }
    OutlinedTextField(value = texto, onValueChange = { t -> texto = t.filter { it.isDigit() }.take(4); onCambio(texto.toIntOrNull()?.takeIf { it > 0 }) },
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Otra (minutos)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
}

fun duracionTexto(min: Int): String = if (min < 60) "$min min" else if (min % 60 == 0) "${min / 60} h" else "${min / 60} h ${min % 60}"

/**
 * La cuadrícula de afecto (Russell): un solo toque dice qué tan agradable (de izquierda a derecha, 1 a 7)
 * y cuánta energía (de abajo arriba, 1 a 7). Las esquinas llevan palabras de guía. Cada celda tiene la forma del
 * tema (tesela, píxel, frontón, rombo, lanceta, hexágono, estrella de ocho puntas…); los colores no cambian.
 */
@Composable
fun CuadriculaAfecto(valencia: Int?, activacion: Int?, modifier: Modifier = Modifier, onCambio: (Int, Int) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val medidor = rememberTextMeasurer()
    val estilo = MaterialTheme.typography.labelSmall.copy(color = cs.onSurfaceVariant)
    val sentir = rememberSentir()
    val tema = Estilo.tema
    val forma = formaCeldaAfecto(tema)
    val fondo = cs.surface
    Column(modifier) {
        Text("más energía ↑", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        // Para TalkBack (N-27): qué es, qué tiene marcado y cuatro acciones para moverse sin ver la cuadrícula.
        val v0 = valencia ?: 4; val a0 = activacion ?: 4
        Canvas(Modifier.fillMaxWidth().aspectRatio(1.25f).padding(vertical = 4.dp).semantics {
            contentDescription = "Cuadrícula de ánimo: de izquierda a derecha, de desagradable a agradable; de abajo arriba, de menos a más energía"
            stateDescription = if (valencia == null) "sin marcar" else "agrado $valencia de 7, energía ${activacion ?: "sin marcar"} de 7"
            customActions = listOf(
                CustomAccessibilityAction("más agradable") { onCambio((v0 + 1).coerceAtMost(7), a0); true },
                CustomAccessibilityAction("menos agradable") { onCambio((v0 - 1).coerceAtLeast(1), a0); true },
                CustomAccessibilityAction("más energía") { onCambio(v0, (a0 + 1).coerceAtMost(7)); true },
                CustomAccessibilityAction("menos energía") { onCambio(v0, (a0 - 1).coerceAtLeast(1)); true },
            )
        }.pointerInput(Unit) {
            detectTapGestures { o ->
                val v = (o.x / size.width * 7).toInt().coerceIn(0, 6) + 1
                val a = 7 - (o.y / size.height * 7).toInt().coerceIn(0, 6)
                sentir(Sentidos.Evento.ELEGIR); onCambio(v, a)
            }
        }) {
            val w = size.width / 7; val h = size.height / 7
            // Todas las celdas miden lo mismo: la forma del tema se calcula una vez.
            val celda = Size(w - 4, h - 4)
            val contorno = forma.createOutline(celda, layoutDirection, this)
            for (i in 0 until 7) for (j in 0 until 7) {
                val v = i + 1; val a = 7 - j
                val base = Tonos.divergente(i / 6f, oscuro)
                // Más energía, color más intenso.
                val alfa = 0.18f + 0.62f * (a - 1) / 6f
                val sel = valencia == v && activacion == a
                translate(i * w + 2, j * h + 2) {
                    drawOutline(contorno, base.copy(alpha = if (sel) 1f else alfa))
                    detalleCelda(tema, celda, fondo)
                    if (sel) drawOutline(contorno, cs.onSurface, style = Stroke(3.dp.toPx()))
                }
            }
            // Ejes que se cruzan en el centro (neutral).
            drawLine(cs.outline, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), 1.dp.toPx())
            drawLine(cs.outline, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 1.dp.toPx())
            val m = 6.dp.toPx()
            listOf("tenso, estresado" to 0, "emocionado, alegre" to 1, "triste, apagado" to 2, "tranquilo, relajado" to 3).forEach { (t, k) ->
                val r = medidor.measure(t, estilo)
                val x = if (k % 2 == 0) m else size.width - r.size.width - m
                val y = if (k < 2) m else size.height - r.size.height - m
                drawText(r, topLeft = Offset(x, y))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Text("← desagradable", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            Text("menos energía ↓", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("agradable →", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }
        if (valencia != null) Text("agrado $valencia/7" + (activacion?.let { " · energía $it/7" } ?: " · toca otra vez para la energía"),
            style = MaterialTheme.typography.labelMedium, color = cs.primary, modifier = Modifier.padding(top = 4.dp))
    }
}

/** La forma de cada celda de la cuadrícula de afecto. Las medidas van en proporción a la celda. */
private fun formaCeldaAfecto(t: TemaId): Shape = when (t) {
    // Tesela de mosaico, apenas irregular.
    TemaId.ROMANO -> GenericShape { s, _ -> moveTo(s.width * 0.03f, 0f); lineTo(s.width, s.height * 0.04f); lineTo(s.width * 0.97f, s.height); lineTo(0f, s.height * 0.96f); close() }
    // Sampietrino: el adoquín cuadrado de aristas gastadas.
    TemaId.URBE -> RoundedCornerShape(22)
    // Píxel con una esquina cortada.
    TemaId.CIBER -> CutCornerShape(topEndPercent = 28)
    TemaId.NEON -> RoundedCornerShape(percent = 40)
    // Frontón.
    TemaId.HELENO -> GenericShape { s, _ -> moveTo(0f, s.height * 0.24f); lineTo(s.width / 2, 0f); lineTo(s.width, s.height * 0.24f); lineTo(s.width, s.height); lineTo(0f, s.height); close() }
    // Mastaba: muros en talud con la cornisa que sobresale arriba.
    TemaId.EGIPTO -> GenericShape { s, _ -> moveTo(0f, 0f); lineTo(s.width, 0f); lineTo(s.width * 0.94f, s.height * 0.12f); lineTo(s.width, s.height); lineTo(0f, s.height); lineTo(s.width * 0.06f, s.height * 0.12f); close() }
    // Esquinas cóncavas de miniatura.
    TemaId.MEDIEVAL -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val r = minOf(w, h) * 0.22f
        moveTo(r, 0f); lineTo(w - r, 0f); quadraticTo(w - r, r, w, r); lineTo(w, h - r); quadraticTo(w - r, h - r, w - r, h)
        lineTo(r, h); quadraticTo(r, h - r, 0f, h - r); lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }
    // Escudo de lágrima.
    TemaId.TEMPLARIO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(w * 0.06f, 0f); lineTo(w * 0.94f, 0f); quadraticTo(w * 0.98f, h * 0.6f, w * 0.5f, h); quadraticTo(w * 0.02f, h * 0.6f, w * 0.06f, 0f); close()
    }
    // Casilla de formulario.
    TemaId.DISTOPIA -> RectangleShape
    // Píldora de luz de tablero.
    TemaId.REFUGIO -> RoundedCornerShape(50)
    // Placa ochavada.
    TemaId.ORDEN -> CutCornerShape(22)
    // Pocillo de placa de ensayo.
    TemaId.LABORATORIO -> RoundedCornerShape(35)
    // Boleto con una muesca a cada lado.
    TemaId.COLONOS -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val n = minOf(w, h) * 0.18f
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h * 0.36f); quadraticTo(w - n, h / 2, w, h * 0.64f); lineTo(w, h)
        lineTo(0f, h); lineTo(0f, h * 0.64f); quadraticTo(n, h / 2, 0f, h * 0.36f); close()
    }
    // Rombo de condecoración.
    TemaId.NAPOLEON -> GenericShape { s, _ -> moveTo(s.width / 2, 0f); lineTo(s.width, s.height / 2); lineTo(s.width / 2, s.height); lineTo(0f, s.height / 2); close() }
    // Hoja.
    TemaId.BOTANICO -> RoundedCornerShape(topStartPercent = 50, topEndPercent = 6, bottomEndPercent = 50, bottomStartPercent = 6)
    // Un adoquín desportillado.
    TemaId.RUINAS -> GenericShape { s, _ -> moveTo(s.width * 0.08f, 0f); lineTo(s.width * 0.8f, 0f); lineTo(s.width, s.height * 0.22f); lineTo(s.width * 0.94f, s.height); lineTo(s.width * 0.14f, s.height * 0.94f); lineTo(0f, s.height * 0.6f); close() }
    // Caja de regalo (el lazo se dibuja aparte).
    TemaId.NAVIDAD -> RoundedCornerShape(12)
    // Moneda (el agujero se dibuja aparte).
    TemaId.SEDA -> GenericShape { s, _ -> val r = minOf(s.width, s.height) / 2; addOval(Rect(Offset(s.width / 2, s.height / 2), r)) }
    // El sello hanko: cuadrado de esquinas apenas redondeadas.
    TemaId.EDO -> GenericShape { s, _ -> val l = minOf(s.width, s.height) * 0.92f; val o = Offset((s.width - l) / 2, (s.height - l) / 2)
        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(o, Size(l, l)), androidx.compose.ui.geometry.CornerRadius(l * 0.12f))) }
    // Lanceta.
    TemaId.GOTICO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(0f, h); lineTo(0f, h * 0.45f); quadraticTo(0f, h * 0.05f, w / 2, 0f); quadraticTo(w, h * 0.05f, w, h * 0.45f); lineTo(w, h); close()
    }
    // Un recorte de papel: la orilla de arriba rasgada y la esquina de abajo doblada.
    TemaId.ARCANO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height; val d = h * 0.06f
        moveTo(0f, d); for (k in 1..6) lineTo(w * k / 6, if (k % 2 == 0) d else 0f)
        lineTo(w, h - h * 0.18f); lineTo(w - h * 0.18f, h); lineTo(0f, h); close()
    }
    // Hexágono, como los espejos de un telescopio.
    TemaId.ESPACIO -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(w * 0.25f, 0f); lineTo(w * 0.75f, 0f); lineTo(w, h / 2); lineTo(w * 0.75f, h); lineTo(w * 0.25f, h); lineTo(0f, h / 2); close()
    }
    // Una burbuja: el círculo entero.
    TemaId.ABISMO -> GenericShape { s, _ -> addOval(androidx.compose.ui.geometry.Rect(0f, 0f, s.width, s.height)) }
    // Estrella de ocho puntas.
    TemaId.ANDALUS -> GenericShape { s, _ ->
        val c = Offset(s.width / 2, s.height / 2); val r = minOf(s.width, s.height) / 2
        for (k in 0 until 16) {
            val rr = if (k % 2 == 0) r else r * 0.77f
            val ang = Math.toRadians(-90.0 + 22.5 * k)
            val x = c.x + rr * kotlin.math.cos(ang).toFloat(); val y = c.y + rr * kotlin.math.sin(ang).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    // El bloque de un glifo: cuadro de esquinas muy redondeadas, un poco más ancho que alto.
    TemaId.MESO -> GenericShape { s, _ ->
        val r = minOf(s.width, s.height) * 0.3f
        addRoundRect(androidx.compose.ui.geometry.RoundRect(0f, s.height * 0.06f, s.width, s.height * 0.94f, androidx.compose.ui.geometry.CornerRadius(r, r)))
    }
    // Cúpula de ger.
    TemaId.MONGOL -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(0f, h); lineTo(0f, h * 0.45f); cubicTo(0f, -h * 0.1f, w, -h * 0.1f, w, h * 0.45f); lineTo(w, h); close()
    }
    // Hoja con la esquina doblada.
    TemaId.RENACIMIENTO -> CutCornerShape(bottomEndPercent = 30)
    // El doblón acuñado a martillo: un octógono de canto irregular.
    TemaId.PIRATA -> CutCornerShape(topStartPercent = 22, topEndPercent = 30, bottomEndPercent = 24, bottomStartPercent = 28)
    // Paralelogramo de cartel.
    TemaId.URSS -> GenericShape { s, _ -> moveTo(s.width * 0.18f, 0f); lineTo(s.width, 0f); lineTo(s.width * 0.82f, s.height); lineTo(0f, s.height); close() }
    // Banderola con cola de golondrina.
    TemaId.REVOLUCION -> GenericShape { s, _ -> moveTo(0f, 0f); lineTo(s.width, 0f); lineTo(s.width * 0.8f, s.height / 2); lineTo(s.width, s.height); lineTo(0f, s.height); close() }
    // Piedra irregular.
    TemaId.ANTIGUAS -> GenericShape { s, _ ->
        val w = s.width; val h = s.height
        moveTo(w * 0.12f, 0f); lineTo(w * 0.94f, h * 0.08f); lineTo(w, h * 0.74f); lineTo(w * 0.82f, h); lineTo(w * 0.06f, h * 0.92f); lineTo(0f, h * 0.22f); close()
    }
}

/** Lo que va encima de la celda en algunos temas: el lazo del regalo, el agujero de la moneda. [fondo] es el color de detrás. */
private fun DrawScope.detalleCelda(t: TemaId, s: Size, fondo: Color) {
    when (t) {
        TemaId.NAVIDAD -> {
            val g = minOf(s.width, s.height) * 0.09f
            drawLine(fondo.copy(alpha = 0.75f), Offset(s.width / 2, 0f), Offset(s.width / 2, s.height), g)
            drawLine(fondo.copy(alpha = 0.75f), Offset(0f, s.height / 2), Offset(s.width, s.height / 2), g)
        }
        TemaId.SEDA -> {
            val l = minOf(s.width, s.height) * 0.22f
            drawRect(fondo, Offset(s.width / 2 - l / 2, s.height / 2 - l / 2), Size(l, l))
        }
        // El marco en reserva del sello.
        TemaId.EDO -> {
            val l = minOf(s.width, s.height) * 0.92f; val m = l * 0.12f
            drawRect(fondo.copy(alpha = 0.7f), Offset((s.width - l) / 2 + m, (s.height - l) / 2 + m), Size(l - 2 * m, l - 2 * m), style = Stroke(l * 0.05f))
        }
        TemaId.ROMANO -> {}
        // El brillo de la piedra pisada, en una arista.
        TemaId.URBE -> drawLine(fondo.copy(alpha = 0.35f), Offset(s.width * 0.2f, s.height * 0.3f), Offset(s.width * 0.5f, s.height * 0.16f), minOf(s.width, s.height) * 0.06f)
        TemaId.CIBER -> {}
        // Una gota de lluvia que resbala por el letrero.
        TemaId.NEON -> {
            val g = minOf(s.width, s.height) * 0.07f
            drawLine(fondo.copy(alpha = 0.5f), Offset(s.width * 0.72f, s.height * 0.2f), Offset(s.width * 0.7f, s.height * 0.45f), g, StrokeCap.Round)
        }
        TemaId.HELENO -> {}
        // El baquetón bajo la cornisa de la mastaba.
        TemaId.EGIPTO -> drawLine(fondo.copy(alpha = 0.5f), Offset(s.width * 0.06f, s.height * 0.15f), Offset(s.width * 0.94f, s.height * 0.15f), minOf(s.width, s.height) * 0.04f)
        TemaId.MEDIEVAL -> {}
        // La cruz patada calada en el escudo.
        TemaId.TEMPLARIO -> drawPath(cruzPatada(Offset(s.width / 2, s.height * 0.42f), minOf(s.width, s.height) * 0.26f), fondo.copy(alpha = 0.55f))
        TemaId.DISTOPIA -> {}
        TemaId.REFUGIO -> {}
        TemaId.ORDEN -> {}
        TemaId.LABORATORIO -> {}
        TemaId.COLONOS -> {}
        TemaId.NAPOLEON -> {}
        TemaId.BOTANICO -> {}
        TemaId.RUINAS -> drawLine(fondo.copy(alpha = 0.35f), Offset(s.width * 0.7f, 0f), Offset(s.width * 0.62f, s.height * 0.3f), 1.5f)   // una grieta
        TemaId.GOTICO -> {}
        // Una gota de tinta que se corrió en la esquina.
        TemaId.ARCANO -> { val l = minOf(s.width, s.height); drawCircle(fondo.copy(alpha = 0.3f), l * 0.06f, Offset(s.width * 0.2f, s.height * 0.78f)) }
        TemaId.ESPACIO -> {}
        // El brillo de la burbuja, arriba a la izquierda.
        TemaId.ABISMO -> {
            val r = minOf(s.width, s.height) / 2
            drawArc(fondo.copy(alpha = 0.55f), 200f, 60f, false, Offset(s.width / 2 - r * 0.7f, s.height / 2 - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(r * 0.12f))
        }
        TemaId.ANDALUS -> {}
        // Un punto y una barra grabados abajo: el seis maya.
        TemaId.MESO -> {
            val r = minOf(s.width, s.height) / 2
            drawRect(fondo.copy(alpha = 0.45f), Offset(s.width / 2 - r * 0.4f, s.height * 0.74f), Size(r * 0.8f, r * 0.12f))
            drawCircle(fondo.copy(alpha = 0.45f), r * 0.08f, Offset(s.width / 2, s.height * 0.66f))
        }
        TemaId.MONGOL -> {}
        TemaId.RENACIMIENTO -> {}
        // La cruz de cuarteles del doblón, apenas grabada.
        TemaId.PIRATA -> {
            val c = Offset(s.width / 2, s.height / 2); val l = minOf(s.width, s.height) * 0.28f
            drawLine(fondo.copy(alpha = 0.18f), Offset(c.x - l, c.y), Offset(c.x + l, c.y), 1.2f); drawLine(fondo.copy(alpha = 0.18f), Offset(c.x, c.y - l), Offset(c.x, c.y + l), 1.2f)
        }
        TemaId.URSS -> {}
        TemaId.REVOLUCION -> {}
        TemaId.ANTIGUAS -> {}
    }
}
