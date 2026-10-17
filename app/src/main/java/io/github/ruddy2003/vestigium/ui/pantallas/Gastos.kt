@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import io.github.ruddy2003.vestigium.ui.Ayuda
import java.time.LocalDateTime
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.launch

fun dinero(v: Double): String = "$" + String.format(java.util.Locale.US, "%,.2f", v).removeSuffix(".00")

@Composable
fun PantallaGastos(nav: Nav? = null) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var monto by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf<String?>(null) }
    var imprevisto by remember { mutableStateOf(false) }
    var nota by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var mes by remember { mutableStateOf(Fechas.mesDe(Fechas.hoyCalendario())) }
    var armado by remember { mutableStateOf<String?>(null) }
    // null = ahora. Si se elige otra fecha u hora, se guarda tal cual y sin ubicación (la de ahora no sería la del pago).
    var cuando by remember { mutableStateOf<LocalDateTime?>(null) }
    var editando by remember { mutableStateOf<Gasto?>(null) }

    fun limpiar() { monto = ""; nota = ""; imprevisto = false; cuando = null; editando = null; error = null }
    fun elegirCuando() {
        val base = cuando ?: LocalDateTime.now()
        DatePickerDialog(ctx, { _, y, m, d ->
            TimePickerDialog(ctx, { _, h, min -> cuando = LocalDateTime.of(y, m + 1, d, h, min) }, base.hour, base.minute, true).show()
        }, base.year, base.monthValue - 1, base.dayOfMonth).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
    }

    Hoja("Gastos", subtitulo = "Registra en el momento de pagar. Lo que se cuadra después ya no dice en qué gastaste.", buscar = nav?.let { n -> { n.ir("buscar") } }) {
        Seccion(if (editando != null) "Corregir gasto" else "Registrar", latin = if (editando != null) "CORRIGERE" else io.github.ruddy2003.vestigium.ui.Romano.latin("Registrar")) {
            Tarjeta {
                OutlinedTextField(value = monto, onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' }; error = null }, modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("$ ") }, placeholder = { Text("0") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), textStyle = MaterialTheme.typography.headlineSmall)
                Etiqueta("Categoría")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Repo.config.categorias.forEach { c -> Ficha(c, cat == c) { cat = if (cat == c) null else c; error = null } }
                }
                Etiqueta("Cuándo")
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("ahora", cuando == null) { cuando = null }
                    val c = cuando
                    Ficha(if (c == null) "otra fecha u hora…" else "${Fechas.corto(Fechas.iso(c.toLocalDate()))} · ${"%02d:%02d".format(c.hour, c.minute)}", c != null) { elegirCuando() }
                }
                cuando?.takeIf { it.hour < 5 }?.let {
                    Ayuda("De madrugada: en tu diario y en R cuenta para el ${Fechas.corto(Fechas.iso(it.toLocalDate().minusDays(1)))}, como los momentos.", Modifier.padding(top = 4.dp))
                }
                Interruptor("¿Fue imprevisto?", imprevisto) { imprevisto = it }
                OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, singleLine = true)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
                BotonPrincipal(if (editando != null) "Guardar cambios" else "Registrar", Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    val v = monto.toDoubleOrNull()
                    when {
                        v == null || v <= 0 -> error = "Escribe un monto mayor a cero."
                        cat == null -> error = "Elige una categoría."
                        else -> {
                            val c = cuando
                            val fecha = c?.let { Fechas.iso(it.toLocalDate()) } ?: Fechas.hoyCalendario()
                            val hora = c?.let { "%02d:%02d".format(it.hour, it.minute) } ?: Fechas.horaAhora()
                            val previo = editando
                            val g = previo?.copy(fecha = fecha, hora = hora, monto = Math.round(v * 100) / 100.0, categoria = cat!!, imprevisto = imprevisto, nota = nota.trim())
                                ?: Gasto(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, imprevisto, nota.trim())
                            Repo.guardarGasto(g)
                            if (previo == null && c == null) scope.launch { Ubicacion.ultima(ctx)?.let { Repo.guardarGasto(g.copy(lat = it.latitude, lon = it.longitude)) } }
                            limpiar(); mes = Fechas.mesDe(g.fecha)
                        }
                    }
                }
                if (editando != null) TextButton(onClick = { limpiar(); cat = null }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) }
            }
        }

        val items = Repo.gastos.filter { it.dia.startsWith(mes) }.sortedByDescending { it.dia + (if (it.dia != it.fecha) "2" else "1") + it.hora }
        val total = items.sumOf { it.monto }
        val imp = items.filter { it.imprevisto }.sumOf { it.monto }
        Row(Modifier.fillMaxWidth().padding(top = 22.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedIconButton(onClick = { mes = Fechas.mesDe(Fechas.iso(Fechas.parse("$mes-01").minusMonths(1))); armado = null }) { Text("‹", fontSize = 20.sp) }
            Text(Fechas.mesTitulo(mes), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            OutlinedIconButton(onClick = { mes = Fechas.mesDe(Fechas.iso(Fechas.parse("$mes-01").plusMonths(1))); armado = null }) { Text("›", fontSize = 20.sp) }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra(dinero(total), "total", Modifier.weight(1f)); Cifra("${items.size}", "movimientos", Modifier.weight(1f)); Cifra(dinero(imp), "imprevistos", Modifier.weight(1f))
        }
        if (items.isEmpty()) { Box(Modifier.padding(top = 12.dp)) { Vacio("Sin gastos registrados este mes.") } }
        else {
            Box(Modifier.padding(top = 12.dp)) { Tarjeta { GastoPorDia(mes, items) } }
            val porCat = items.groupBy { it.categoria }.mapValues { e -> e.value.sumOf { it.monto } }.entries.sortedByDescending { it.value }
            val max = porCat.first().value
            Box(Modifier.padding(top = 12.dp)) {
                Tarjeta {
                    porCat.forEach { (c, v) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(c, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(100.dp))
                            Box(Modifier.weight(1f)) {
                                Box(Modifier.fillMaxWidth((v / max).toFloat().coerceAtLeast(0.03f)).height(9.dp).clip(RoundedCornerShape(1.dp)).background(MaterialTheme.colorScheme.primary))
                            }
                            Text(dinero(v), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 12.sp, modifier = Modifier.width(80.dp), textAlign = TextAlign.End)
                        }
                    }
                }
            }
            var ultimo = ""
            items.forEach { g ->
                if (g.dia != ultimo) {
                    ultimo = g.dia
                    Text(Fechas.corto(g.dia).uppercase(), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp, bottom = 2.dp))
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Tocar un gasto lo carga arriba para corregir monto, categoría, fecha u hora.
                    Column(Modifier.weight(1f).clickable {
                        editando = g; monto = if (g.monto % 1.0 == 0.0) g.monto.toLong().toString() else g.monto.toString(); cat = g.categoria
                        imprevisto = g.imprevisto; nota = g.nota; error = null
                        cuando = runCatching { LocalDateTime.of(Fechas.parse(g.fecha), java.time.LocalTime.parse(g.hora.take(5))) }.getOrNull()
                    }) {
                        Text(g.categoria + if (g.imprevisto) " · imprevisto" else "", style = MaterialTheme.typography.bodyLarge)
                        Text((if (g.dia != g.fecha) "${g.hora} de la madrugada" else g.hora) + if (g.nota.isNotBlank()) " · ${g.nota}" else "",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(dinero(g.monto), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo)
                    TextButton(onClick = { if (armado == g.id) { Repo.borrarGasto(g.id); armado = null } else armado = g.id }) {
                        Text(if (armado == g.id) "¿Borrar?" else "Quitar", color = if (armado == g.id) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

/** Barras del gasto de cada día del mes; los imprevistos en otro tono encima. */
@Composable
private fun GastoPorDia(mes: String, items: List<Gasto>) {
    val dias = Fechas.parse("$mes-01").lengthOfMonth()
    val porDia = DoubleArray(dias); val imp = DoubleArray(dias)
    items.forEach { g -> val d = g.dia.takeLast(2).toInt() - 1; if (d in 0 until dias) { porDia[d] += g.monto; if (g.imprevisto) imp[d] += g.monto } }
    val max = porDia.maxOrNull()?.takeIf { it > 0 } ?: return
    val diasConGasto = porDia.count { it > 0 }
    val barra = MaterialTheme.colorScheme.primary
    val alerta = MaterialTheme.colorScheme.error
    val texto = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    Etiqueta("por día")
    Canvas(Modifier.fillMaxWidth().aspectRatio(2.8f)) {
        val abajo = 26f
        val w = size.width / dias
        val pincel = android.graphics.Paint().apply { color = texto; textSize = 22f; isAntiAlias = true; textAlign = android.graphics.Paint.Align.CENTER }
        for (k in 0 until dias) {
            val h = (size.height - abajo) * (porDia[k] / max).toFloat()
            if (h > 0) drawRoundRect(barra, Offset(k * w + w * 0.15f, size.height - abajo - h), Size(w * 0.7f, h), CornerRadius(2f, 2f))
            val hi = (size.height - abajo) * (imp[k] / max).toFloat()
            if (hi > 0) drawRoundRect(alerta, Offset(k * w + w * 0.15f, size.height - abajo - hi), Size(w * 0.7f, hi), CornerRadius(2f, 2f))
            if (k == 0 || (k + 1) % 5 == 0) drawContext.canvas.nativeCanvas.drawText("${k + 1}", k * w + w / 2, size.height - 4f, pincel)
        }
    }
    val top = porDia.indices.maxBy { porDia[it] }
    Ayuda("Día más alto: el ${top + 1} con ${dinero(porDia[top])} · gastaste en $diasConGasto de $dias días · en rojo, imprevistos.")
}
