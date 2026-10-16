@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.background
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
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.launch

fun dinero(v: Double): String = "$" + String.format(java.util.Locale.US, "%,.2f", v).removeSuffix(".00")

@Composable
fun PantallaGastos() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var monto by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf<String?>(null) }
    var imprevisto by remember { mutableStateOf(false) }
    var nota by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var mes by remember { mutableStateOf(Fechas.mesDe(Fechas.hoyCalendario())) }
    var armado by remember { mutableStateOf<String?>(null) }

    Hoja("Gastos", subtitulo = "Registra en el momento de pagar. Lo que se cuadra después ya no dice en qué gastaste.") {
        Seccion("Registrar") {
            Tarjeta {
                OutlinedTextField(value = monto, onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' }; error = null }, modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("$ ") }, placeholder = { Text("0") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), textStyle = MaterialTheme.typography.headlineSmall)
                Etiqueta("Categoría")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Repo.config.categorias.forEach { c -> Ficha(c, cat == c) { cat = if (cat == c) null else c; error = null } }
                }
                Interruptor("¿Fue imprevisto?", imprevisto) { imprevisto = it }
                OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, singleLine = true)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
                BotonPrincipal("Registrar", Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    val v = monto.toDoubleOrNull()
                    when {
                        v == null || v <= 0 -> error = "Escribe un monto mayor a cero."
                        cat == null -> error = "Elige una categoría."
                        else -> {
                            val g = Gasto(Repo.nuevoId(), Fechas.hoyCalendario(), Fechas.horaAhora(), Math.round(v * 100) / 100.0, cat!!, imprevisto, nota.trim())
                            Repo.guardarGasto(g)
                            scope.launch { Ubicacion.ultima(ctx)?.let { Repo.guardarGasto(g.copy(lat = it.latitude, lon = it.longitude)) } }
                            monto = ""; nota = ""; imprevisto = false; mes = Fechas.mesDe(g.fecha)
                        }
                    }
                }
            }
        }

        val items = Repo.gastos.filter { it.fecha.startsWith(mes) }.sortedByDescending { it.fecha + it.hora }
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
                if (g.fecha != ultimo) {
                    ultimo = g.fecha
                    Text(Fechas.corto(g.fecha).uppercase(), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp, bottom = 2.dp))
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(g.categoria + if (g.imprevisto) " · imprevisto" else "", style = MaterialTheme.typography.bodyLarge)
                        Text(g.hora + if (g.nota.isNotBlank()) " · ${g.nota}" else "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
