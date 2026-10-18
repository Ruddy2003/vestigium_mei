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
import io.github.ruddy2003.vestigium.data.Ingreso
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
    val sentir = io.github.ruddy2003.vestigium.ui.rememberSentir()
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
    // Gastos e ingresos comparten el formulario: «entra» cambia categorías, el interruptor y el color.
    var entra by remember { mutableStateOf(false) }
    var fijo by remember { mutableStateOf(false) }
    var editandoIngreso by remember { mutableStateOf<Ingreso?>(null) }

    fun limpiar() { monto = ""; nota = ""; imprevisto = false; fijo = false; cuando = null; editando = null; editandoIngreso = null; error = null }
    fun elegirCuando() {
        val base = cuando ?: LocalDateTime.now()
        DatePickerDialog(ctx, { _, y, m, d ->
            TimePickerDialog(ctx, { _, h, min -> cuando = LocalDateTime.of(y, m + 1, d, h, min) }, base.hour, base.minute, true).show()
        }, base.year, base.monthValue - 1, base.dayOfMonth).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
    }

    Hoja("Gastos", subtitulo = "Registra en el momento de pagar o de cobrar. Lo que se cuadra después ya no dice en qué se fue el dinero.", buscar = nav?.let { n -> { n.ir("buscar") } }) {
        val corrigiendo = editando != null || editandoIngreso != null
        Seccion(if (corrigiendo) (if (entra) "Corregir ingreso" else "Corregir gasto") else "Registrar", latin = if (corrigiendo) "CORRIGERE" else io.github.ruddy2003.vestigium.ui.Romano.latin("Registrar")) {
            Tarjeta {
                // Gasto o ingreso: dos fichas grandes, como una balanza.
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(false to "− Gasto", true to "+ Ingreso").forEach { (v, t) ->
                        val sel = entra == v
                        val color = if (v) io.github.ruddy2003.vestigium.ui.Tonos.ingreso(io.github.ruddy2003.vestigium.ui.esOscuro()) else io.github.ruddy2003.vestigium.ui.Tonos.gasto(io.github.ruddy2003.vestigium.ui.esOscuro())
                        androidx.compose.material3.Surface(
                            onClick = { if (!corrigiendo && entra != v) { entra = v; cat = null; error = null; sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.ELEGIR) } },
                            shape = MaterialTheme.shapes.small, modifier = Modifier.weight(1f),
                            color = if (sel) color.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(if (sel) 2.dp else 1.dp, if (sel) color else MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            Text(t, style = MaterialTheme.typography.titleMedium, color = if (sel) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 10.dp))
                        }
                    }
                }
                OutlinedTextField(value = monto, onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' }; error = null }, modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("$ ") }, placeholder = { Text("0") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), textStyle = MaterialTheme.typography.headlineSmall)
                Etiqueta("Categoría")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (if (entra) Repo.config.categorias_ingreso else Repo.config.categorias).forEach { c -> Ficha(c, cat == c) { cat = if (cat == c) null else c; error = null } }
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
                if (entra) Interruptor("¿Se repite? (sueldo, beca, renta)", fijo) { fijo = it }
                else Interruptor("¿Fue imprevisto?", imprevisto) { imprevisto = it }
                OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, singleLine = true)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
                BotonPrincipal(if (corrigiendo) "Guardar cambios" else if (entra) "Registrar ingreso" else "Registrar", Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    val v = monto.toDoubleOrNull()
                    when {
                        v == null || v <= 0 -> error = "Escribe un monto mayor a cero."
                        cat == null -> error = "Elige una categoría."
                        entra -> {
                            val c = cuando
                            val fecha = c?.let { Fechas.iso(it.toLocalDate()) } ?: Fechas.hoyCalendario()
                            val hora = c?.let { "%02d:%02d".format(it.hour, it.minute) } ?: Fechas.horaAhora()
                            val i = editandoIngreso?.copy(fecha = fecha, hora = hora, monto = Math.round(v * 100) / 100.0, categoria = cat!!, fijo = fijo, nota = nota.trim())
                                ?: Ingreso(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, nota.trim(), fijo)
                            Repo.guardarIngreso(i)
                            sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.GUARDAR)
                            limpiar(); mes = Fechas.mesDe(i.fecha)
                        }
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
                if (corrigiendo) TextButton(onClick = { limpiar(); cat = null }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) }
            }
        }

        val items = Repo.gastos.filter { it.dia.startsWith(mes) }.sortedByDescending { it.dia + (if (it.dia != it.fecha) "2" else "1") + it.hora }
        val entradas = Repo.ingresos.filter { it.dia.startsWith(mes) }
        val total = items.sumOf { it.monto }
        val ingresado = entradas.sumOf { it.monto }
        val imp = items.filter { it.imprevisto }.sumOf { it.monto }
        Row(Modifier.fillMaxWidth().padding(top = 22.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedIconButton(onClick = { mes = Fechas.mesDe(Fechas.iso(Fechas.parse("$mes-01").minusMonths(1))); armado = null }) { Text("‹", fontSize = 20.sp) }
            Text(Fechas.mesTitulo(mes), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            OutlinedIconButton(onClick = { mes = Fechas.mesDe(Fechas.iso(Fechas.parse("$mes-01").plusMonths(1))); armado = null }) { Text("›", fontSize = 20.sp) }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra(dinero(total), "gastado", Modifier.weight(1f)); Cifra(dinero(ingresado), "ingresos", Modifier.weight(1f)); Cifra(dinero(imp), "imprevistos", Modifier.weight(1f))
        }
        if (ingresado > 0 || total > 0) Box(Modifier.padding(top = 12.dp)) { Tarjeta { Balanza(ingresado, total) } }
        if (entradas.isNotEmpty()) Box(Modifier.padding(top = 12.dp)) {
            Tarjeta {
                Etiqueta("ingresos del mes")
                entradas.sortedByDescending { it.dia + it.hora }.forEach { i ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f).clickable {
                            entra = true; editandoIngreso = i; editando = null
                            monto = if (i.monto % 1.0 == 0.0) i.monto.toLong().toString() else i.monto.toString(); cat = i.categoria; fijo = i.fijo; nota = i.nota; error = null
                            cuando = runCatching { LocalDateTime.of(Fechas.parse(i.fecha), java.time.LocalTime.parse(i.hora.take(5))) }.getOrNull()
                        }) {
                            Text(i.categoria + if (i.fijo) " · se repite" else "", style = MaterialTheme.typography.bodyLarge)
                            Text("${Fechas.corto(i.dia)} · ${i.hora}" + if (i.nota.isNotBlank()) " · ${i.nota}" else "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("+" + dinero(i.monto), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, color = io.github.ruddy2003.vestigium.ui.Tonos.ingreso(io.github.ruddy2003.vestigium.ui.esOscuro()))
                        TextButton(onClick = { if (armado == i.id) { Repo.borrarIngreso(i.id); armado = null } else armado = i.id }) {
                            Text(if (armado == i.id) "¿Borrar?" else "Quitar", color = if (armado == i.id) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
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
                        entra = false; editandoIngreso = null; fijo = false
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

/** Lo que entró contra lo que salió en el mes: dos barras que crecen y el saldo. */
@Composable
private fun Balanza(ingresos: Double, gastos: Double) {
    val animar = Repo.config.animaciones
    var listo by remember { mutableStateOf(!animar) }
    androidx.compose.runtime.LaunchedEffect(Unit) { listo = true }
    val p by androidx.compose.animation.core.animateFloatAsState(if (listo) 1f else 0f, androidx.compose.animation.core.tween(if (animar) 700 else 0), label = "balanza")
    val max = maxOf(ingresos, gastos).takeIf { it > 0 } ?: 1.0
    val verde = io.github.ruddy2003.vestigium.ui.Tonos.ingreso(io.github.ruddy2003.vestigium.ui.esOscuro())
    Etiqueta("balance del mes")
    listOf(Triple("entró", ingresos, verde), Triple("salió", gastos, io.github.ruddy2003.vestigium.ui.Tonos.gasto(io.github.ruddy2003.vestigium.ui.esOscuro()))).forEach { (t, v, c) ->
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(t, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(56.dp))
            Box(Modifier.weight(1f)) {
                Box(Modifier.fillMaxWidth(((v / max).toFloat() * p).coerceIn(0.02f, 1f)).height(12.dp).clip(RoundedCornerShape(2.dp)).background(c))
            }
            Text(dinero(v), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 12.sp, modifier = Modifier.width(90.dp), textAlign = TextAlign.End)
        }
    }
    val saldo = ingresos - gastos
    Text((if (saldo >= 0) "Saldo a favor: " else "Gastaste más de lo que entró: ") + dinero(kotlin.math.abs(saldo)) +
        (if (ingresos > 0) " · ahorro de %.0f%%".format((saldo / ingresos * 100).coerceAtLeast(-999.0)).takeIf { saldo >= 0 }.orEmpty() else ""),
        style = MaterialTheme.typography.titleSmall, color = if (saldo >= 0) verde else MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
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
