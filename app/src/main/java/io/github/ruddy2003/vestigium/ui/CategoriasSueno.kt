@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo

/**
 * Categorías del sueño: arriba las más usadas, y abajo los seis ejes (quiénes, dónde, qué pasa entre personas,
 * temas típicos, cómo es el sueño y la emoción). Se puede marcar varias en cada eje.
 */
@Composable
fun CategoriasSueno(sel: List<String>, onCambio: (List<String>) -> Unit) {
    var abierto by remember { mutableStateOf<String?>(null) }
    fun alternar(id: String) = onCambio(if (id in sel) sel - id else sel + id)
    Column {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.SUENO_RAPIDAS.forEach { id -> Catalogo.opcionSueno(id)?.let { o -> Ficha(o.nombre, id in sel) { alternar(id) } } }
        }
        FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Catalogo.SUENO_EJES.forEach { e ->
                val n = e.opciones.count { it.id in sel }
                TextButton(onClick = { abierto = if (abierto == e.id) null else e.id }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text((if (abierto == e.id) "− " else "+ ") + e.ayuda.lowercase() + if (n > 0) " ($n)" else "", style = MaterialTheme.typography.labelLarge,
                        color = if (abierto == e.id || n > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Catalogo.SUENO_EJES.firstOrNull { it.id == abierto }?.let { e ->
            Ayuda(e.nombre, Modifier.padding(bottom = 4.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                e.opciones.forEach { o -> Ficha(o.nombre, o.id in sel) { alternar(o.id) } }
            }
        }
    }
}
