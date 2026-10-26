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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import java.time.Instant
import java.time.LocalDateTime

/**
 * El único selector de hora de la app (el de gastos): «ahora» o «otra fecha u hora…», con atajos opcionales
 * («hace 15 min»…). [valor] null = ahora. Lo de madrugada avisa a qué día cuenta.
 */
@Composable
fun SelectorHora(valor: LocalDateTime?, atajos: List<Int> = emptyList(), etiquetaAhora: String = "ahora", permitirFuturo: Boolean = false,
                 onCambio: (LocalDateTime?) -> Unit) {
    val ctx = LocalContext.current
    val ahora = remember { LocalDateTime.now().withSecond(0).withNano(0) }
    fun elegir() {
        val base = valor ?: LocalDateTime.now()
        DatePickerDialog(ctx, { _, y, m, d ->
            TimePickerDialog(ctx, { _, h, min -> onCambio(LocalDateTime.of(y, m + 1, d, h, min)) }, base.hour, base.minute, true).show()
        }, base.year, base.monthValue - 1, base.dayOfMonth).apply { if (!permitirFuturo) datePicker.maxDate = System.currentTimeMillis() }.show()
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Ficha(etiquetaAhora, valor == null) { onCambio(null) }
        atajos.forEach { m ->
            val t = ahora.minusMinutes(m.toLong())
            Ficha(if (m < 60) "hace $m min" else if (m % 60 == 0) "hace ${m / 60} h" else "hace ${m / 60} h ${m % 60}", valor == t) { onCambio(t) }
        }
        val propio = valor != null && atajos.none { ahora.minusMinutes(it.toLong()) == valor }
        Ficha(if (!propio) "otra fecha u hora…" else "${Fechas.corto(Fechas.iso(valor!!.toLocalDate()))} · ${"%02d:%02d".format(valor.hour, valor.minute)}", propio) { elegir() }
    }
    valor?.takeIf { it.hour < 5 }?.let {
        Ayuda("De madrugada: cuenta para el ${Fechas.corto(Fechas.iso(it.toLocalDate().minusDays(1)))}.", Modifier.padding(top = 4.dp))
    }
}

/** Abre el calendario de Android para elegir un día (AAAA-MM-DD). Con [desdeManana], solo días futuros. */
fun elegirFecha(ctx: android.content.Context, inicial: String?, desdeManana: Boolean = false, onFecha: (String) -> Unit) {
    val base = inicial?.let { runCatching { Fechas.parse(it) }.getOrNull() } ?: java.time.LocalDate.now().plusDays(if (desdeManana) 1 else 0)
    DatePickerDialog(ctx, { _, y, m, d -> onFecha(Fechas.iso(java.time.LocalDate.of(y, m + 1, d))) }, base.year, base.monthValue - 1, base.dayOfMonth)
        .apply { if (desdeManana) datePicker.minDate = System.currentTimeMillis() + 86_400_000L }.show()
}

fun LocalDateTime.aMillis(): Long = atZone(Fechas.zona()).toInstant().toEpochMilli()
fun horaLocal(ms: Long): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), Fechas.zona()).withSecond(0).withNano(0)

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
 * y cuánta energía (de abajo arriba, 1 a 7). Las esquinas llevan palabras de guía.
 */
@Composable
fun CuadriculaAfecto(valencia: Int?, activacion: Int?, modifier: Modifier = Modifier, onCambio: (Int, Int) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val medidor = rememberTextMeasurer()
    val estilo = MaterialTheme.typography.labelSmall.copy(color = cs.onSurfaceVariant)
    val sentir = rememberSentir()
    Column(modifier) {
        Text("más energía ↑", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Canvas(Modifier.fillMaxWidth().aspectRatio(1.25f).padding(vertical = 4.dp).pointerInput(Unit) {
            detectTapGestures { o ->
                val v = (o.x / size.width * 7).toInt().coerceIn(0, 6) + 1
                val a = 7 - (o.y / size.height * 7).toInt().coerceIn(0, 6)
                sentir(Sentidos.Evento.ELEGIR); onCambio(v, a)
            }
        }) {
            val w = size.width / 7; val h = size.height / 7
            for (i in 0 until 7) for (j in 0 until 7) {
                val v = i + 1; val a = 7 - j
                val base = Tonos.divergente(i / 6f, oscuro)
                // Más energía, color más intenso.
                val alfa = 0.18f + 0.62f * (a - 1) / 6f
                val sel = valencia == v && activacion == a
                drawRoundRect(base.copy(alpha = if (sel) 1f else alfa), Offset(i * w + 2, j * h + 2), Size(w - 4, h - 4), CornerRadius(6f, 6f))
                if (sel) drawRoundRect(cs.onSurface, Offset(i * w + 2, j * h + 2), Size(w - 4, h - 4), CornerRadius(6f, 6f), style = Stroke(3.dp.toPx()))
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
