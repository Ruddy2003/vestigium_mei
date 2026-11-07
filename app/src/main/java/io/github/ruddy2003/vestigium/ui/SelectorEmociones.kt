@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Opcion
import io.github.ruddy2003.vestigium.data.Repo

/** Lo que se elige en «¿qué sientes?»: las emociones, el nombre de «otro» y cuánto de cada una. */
data class EleccionEmociones(val emociones: List<String>, val otra: String, val intensidades: Map<String, Int>)

/**
 * «¿Qué sientes?», igual en el registro rápido y al corregir un momento: las emociones por tono (agradables, desagradables y
 * mixtas), dentro de cada tono primero las que más usas; «otro…» con su nombre (y los que ya escribiste antes, a un toque);
 * «no sé», que quita las demás; y, para cada emoción marcada, cuánto: un poco, bastante o mucho.
 */
@Composable
fun SelectorEmociones(actual: EleccionEmociones, discreto: Boolean, onCambio: (EleccionEmociones) -> Unit) {
    val usos = remember(Repo.momentos.size) { Repo.momentos.flatMap { Catalogo.emocionesDe(it) }.groupingBy { it }.eachCount() }
    val otrasAntes = remember(Repo.momentos.size) {
        Repo.momentos.mapNotNull { it.emocion_otra?.trim()?.takeIf { t -> t.isNotEmpty() } }.groupingBy { it.lowercase() }.eachCount()
            .entries.sortedByDescending { it.value }.map { it.key }.take(6)
    }
    val sel = actual.emociones
    fun alternar(id: String) {
        val e = when {
            id in sel -> sel - id
            id == Catalogo.NO_SE -> listOf(Catalogo.NO_SE)
            else -> sel - Catalogo.NO_SE + id
        }
        onCambio(actual.copy(emociones = e, intensidades = actual.intensidades.filterKeys { it in e },
            otra = if (Catalogo.OTRO in e) actual.otra else ""))
    }
    val tonos = listOf(1 to (if (discreto) "+" else "agradables"), -1 to (if (discreto) "−" else "desagradables"), 0 to (if (discreto) "±" else "mixtas"))
    Column {
        tonos.forEach { (tono, rotulo) ->
            Text(rotulo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp, bottom = 2.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val orden = Catalogo.SENTIMIENTOS.withIndex().filter { it.value.tono == tono }
                    .sortedWith(compareByDescending<IndexedValue<Catalogo.Sentimiento>> { usos[it.value.id] ?: 0 }.thenBy { it.index }).map { it.value }
                orden.forEach { e -> Ficha(if (discreto) e.discreto else e.nombre, e.id in sel) { alternar(e.id) } }
                if (tono == 0) {
                    Ficha("otro…", Catalogo.OTRO in sel) { alternar(Catalogo.OTRO) }
                    Ficha("no sé", Catalogo.NO_SE in sel) { alternar(Catalogo.NO_SE) }
                }
            }
        }
        if (Catalogo.OTRO in sel) {
            CampoTexto(value = actual.otra, onValueChange = { t -> onCambio(actual.copy(otra = t)) },
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("¿Cuál?") })
            val sugeridas = otrasAntes.filter { !it.equals(actual.otra.trim(), ignoreCase = true) }
            if (!discreto && sugeridas.isNotEmpty()) FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                sugeridas.forEach { t -> Ficha(t, false) { onCambio(actual.copy(otra = t)) } }
            }
        }
        // Cuánto de cada una: opcional; tocar el mismo nivel otra vez lo quita.
        val conIntensidad = sel.filter { it != Catalogo.NO_SE }
        if (conIntensidad.isNotEmpty()) {
            Ayuda("¿Cuánto? (opcional)", Modifier.padding(top = 8.dp))
            conIntensidad.forEach { id ->
                val nombre = if (id == Catalogo.OTRO && actual.otra.isNotBlank() && !discreto) actual.otra.trim() else Catalogo.nombreSentimiento(id, discreto)
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(nombre, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(110.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Catalogo.INTENSIDADES.forEach { (n, txt) ->
                            val activa = actual.intensidades[id] == n
                            Ficha(if (discreto) "$n" else txt, activa) {
                                onCambio(actual.copy(intensidades = if (activa) actual.intensidades - id else actual.intensidades + (id to n)))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * «¿En qué piensas?», varios a la vez (esquema 17). Las que se parecen llevan su diferencia en una línea (preocupación: miedo a lo
 * que viene; dándole vueltas: repetir lo que ya pasó; autocrítica: juzgarte a ti). En modo discreto, letras en lugar de nombres.
 */
@Composable
fun SelectorPensamientos(sel: List<String>, discreto: Boolean, onCambio: (List<String>) -> Unit) {
    Column {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.PENSAMIENTOS.forEachIndexed { i, o ->
                Ficha(if (discreto) ('a' + i).toString() else o.nombre, o.id in sel) { onCambio(if (o.id in sel) sel - o.id else sel + o.id) }
            }
        }
        if (!discreto) Ayuda(Catalogo.PENSAMIENTOS.filter { it.detalle.isNotEmpty() }.joinToString(" · ") { "${it.nombre.lowercase()}: ${it.detalle}" }, Modifier.padding(top = 4.dp))
    }
}

/** «¿Con quién estás?»: «solo» excluye a quien está contigo en persona; «por mensaje o llamada» puede ir con «solo». */
@Composable
fun SelectorConQuien(sel: List<String>, opciones: List<Opcion> = Catalogo.CON_QUIEN, onCambio: (List<String>) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        opciones.forEach { o -> Ficha(o.nombre, o.id in sel) { onCambio(Catalogo.alternarConQuien(sel, o.id)) } }
    }
}
