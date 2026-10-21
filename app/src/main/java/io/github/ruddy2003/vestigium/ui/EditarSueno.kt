package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.SaludDia
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Corregir o anotar a mano el sueño de una noche (la que terminó en [fecha]): a qué hora te dormiste, a qué hora
 * despertaste y cuánto estuviste despierto en medio. Lo corregido ya no lo pisa la sincronización; se puede volver a lo del reloj.
 * Abajo se ve lo que llegó de Health Connect esa noche, para entender de dónde salía el número.
 */
@Composable
fun EditarSueno(fecha: String, cerrar: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val s0 = Repo.salud[fecha] ?: SaludDia(fecha = fecha)
    val dia = Fechas.parse(fecha)
    var dormi by remember { mutableStateOf<LocalDateTime?>(s0.sueno_inicio?.let { horaLocal(Fechas.millis(it)) } ?: dia.minusDays(1).atTime(23, 30)) }
    var desperte by remember { mutableStateOf<LocalDateTime?>(s0.sueno_fin?.let { horaLocal(Fechas.millis(it)) } ?: dia.atTime(7, 30)) }
    var despierto by remember { mutableStateOf(s0.sueno_despierto_min?.takeIf { s0.sueno_manual }) }
    val a = (dormi ?: LocalDateTime.now()).aMillis(); val b = (desperte ?: LocalDateTime.now()).aMillis()
    val total = ((b - a) / 60_000L).toInt() - (despierto ?: 0)
    val valido = b > a && total in 30..(20 * 60)
    AlertDialog(
        onDismissRequest = cerrar,
        title = { Text("Sueño de la noche al ${Fechas.corto(fecha)}") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Etiqueta("Me dormí")
                SelectorHora(dormi) { dormi = it }
                Etiqueta("Desperté")
                SelectorHora(desperte) { desperte = it }
                Etiqueta("Despierto en medio (opcional)")
                SelectorDuracion(despierto, listOf(0, 10, 20, 30, 45, 60)) { despierto = it }
                Text(if (valido) "= ${total / 60} h ${total % 60} min dormido" else "Revisa las horas: despertar debe ir después de dormir (entre 30 min y 20 h).",
                    style = MaterialTheme.typography.labelLarge, color = if (valido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 10.dp))
                if (s0.sueno_fuentes.isNotEmpty()) {
                    Etiqueta("Lo que llegó del reloj esa noche")
                    s0.sueno_fuentes.forEach { Text("· $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Ayuda("Se suma una sola fuente por noche (la que trae etapas; Samsung Health primero). Lo que termina después del mediodía se cuenta como siesta.", Modifier.padding(top = 4.dp))
                }
                if (s0.sueno_manual) TextButton(onClick = {
                    Repo.guardarSalud(s0.copy(sueno_manual = false))
                    scope.launch { runCatching { io.github.ruddy2003.vestigium.sensores.Salud.resincronizarDia(ctx, fecha) } }
                    cerrar()
                }, modifier = Modifier.fillMaxWidth()) { Text("quitar la corrección y volver a lo del reloj") }
            }
        },
        confirmButton = {
            TextButton(enabled = valido, onClick = {
                // Al corregir a mano, las etapas del reloj dejan de cuadrar con el total: se quitan.
                val cambioHoras = s0.sueno_inicio == null || Fechas.millis(s0.sueno_inicio) != a || s0.sueno_fin == null || Fechas.millis(s0.sueno_fin) != b
                Repo.guardarSalud(s0.copy(sueno_min = total, sueno_inicio = Fechas.isoDeMillis(a), sueno_fin = Fechas.isoDeMillis(b), sueno_despierto_min = despierto,
                    sueno_profundo_min = if (cambioHoras) null else s0.sueno_profundo_min, sueno_rem_min = if (cambioHoras) null else s0.sueno_rem_min,
                    sueno_ligero_min = if (cambioHoras) null else s0.sueno_ligero_min, sueno_manual = true, ts = Fechas.ahoraIso()))
                cerrar()
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = cerrar) { Text("cancelar") } },
    )
}
