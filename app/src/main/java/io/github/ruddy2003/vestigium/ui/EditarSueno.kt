package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val s0 = Repo.salud[fecha] ?: SaludDia(fecha = fecha)
    val dia = Fechas.parse(fecha)
    var dormi by remember { mutableStateOf<LocalDateTime?>(s0.sueno_inicio?.let { horaLocal(Fechas.millis(it)) } ?: dia.minusDays(1).atTime(23, 30)) }
    var desperte by remember { mutableStateOf<LocalDateTime?>(s0.sueno_fin?.let { horaLocal(Fechas.millis(it)) } ?: dia.atTime(7, 30)) }
    var despierto by remember { mutableStateOf(s0.sueno_despierto_min?.takeIf { s0.sueno_manual }) }
    var volver by remember { mutableStateOf(false) }
    val a = (dormi ?: LocalDateTime.now()).aMillis(); val b = (desperte ?: LocalDateTime.now()).aMillis()
    val total = ((b - a) / 60_000L).toInt() - (despierto ?: 0)
    val valido = b > a && total in 30..(20 * 60)
    Dialogo(
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
                    // Cada sesión se puede marcar «no cuenta» (la siesta del sofá, una noche que no fue tuya); la sincronización lo respeta (N-04).
                    s0.sueno_fuentes.forEach { f ->
                        val k = io.github.ruddy2003.vestigium.data.claveFuenteSueno(f)
                        val fuera = k in s0.sueno_descartadas
                        androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text("· $f", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                val app = ctx.applicationContext
                                Repo.guardarSalud(s0.copy(sueno_descartadas = if (fuera) s0.sueno_descartadas - k else s0.sueno_descartadas + k))
                                // Se vuelve a leer el reloj de esa noche para rehacer la cuenta sin (o con) esa sesión.
                                if (!s0.sueno_manual) kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    runCatching { io.github.ruddy2003.vestigium.sensores.Salud.resincronizarDia(app, fecha) }
                                }
                            }) { Text(if (fuera) "sí cuenta" else "no cuenta", style = MaterialTheme.typography.labelMedium) }
                        }
                    }
                    Ayuda("Se suma una sola fuente por noche (la que trae etapas; Samsung Health primero). Lo que termina después del mediodía se cuenta como siesta. " +
                        "Lo que marques «no cuenta» no entra ni en la noche ni en las siestas.", Modifier.padding(top = 4.dp))
                }
                if (s0.sueno_manual) {
                    if (volver) {
                        // Si el reloj no trajo nada esa noche, volver a él deja la noche vacía: se avisa antes.
                        Ayuda(if (s0.sueno_fuentes.isEmpty()) "El reloj no tiene esta noche: si vuelves a él, el sueño de esa fecha quedará vacío."
                            else "Se usará lo que midió el reloj y se pierde tu corrección.", Modifier.padding(top = 10.dp))
                        TextButton(onClick = {
                            val app = ctx.applicationContext
                            Repo.guardarSalud(s0.copy(sueno_manual = false))
                            cerrar()
                            // La relectura del reloj espera a que pase el «deshacer», para que no pise lo que se recupere.
                            Deshacer.ofrecer("Volviste a lo del reloj", alExpirar = {
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    runCatching { io.github.ruddy2003.vestigium.sensores.Salud.resincronizarDia(app, fecha) }
                                }
                            }) { Repo.guardarSalud(s0) }
                        }, modifier = Modifier.fillMaxWidth()) { Text("sí, volver a lo del reloj", color = MaterialTheme.colorScheme.error) }
                    } else TextButton(onClick = { volver = true }, modifier = Modifier.fillMaxWidth()) { Text("quitar la corrección y volver a lo del reloj") }
                }
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
