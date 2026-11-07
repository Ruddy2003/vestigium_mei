@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.data.porNumero

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Opcion
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto

/**
 * Categorías del sueño: arriba las que más usas (calculadas, ver [Catalogo.suenoRapidas]), y abajo los ejes (quiénes, dónde,
 * qué pasa entre personas, temas, cómo me fue, sensaciones, estructura, emociones y cómo desperté). Se pueden marcar varias en cada eje.
 */
@Composable
fun CategoriasSueno(sel: List<String>, onCambio: (List<String>) -> Unit) {
    var abierto by remember { mutableStateOf<String?>(null) }
    val rapidas = remember(Repo.dias.size) { Catalogo.suenoRapidas(Repo.dias.values) }
    fun alternar(id: String) = onCambio(if (id in sel) sel - id else sel + id)
    Column {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            rapidas.forEach { id -> Catalogo.opcionSueno(id)?.let { o -> Ficha(o.nombre, id in sel) { alternar(id) } } }
        }
        FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Catalogo.SUENO_EJES.forEach { e ->
                val n = e.opciones.count { it.id in sel }
                BotonTexto(onClick = { abierto = if (abierto == e.id) null else e.id }, contentPadding = PaddingValues(horizontal = 8.dp)) {
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

private val SI_NO = listOf(Opcion("si", "Sí"), Opcion("no", "No"))

/**
 * Un día con el «tipo» de sueño de antes pasado a los campos del esquema 17 (rareza, pesadilla y «se repite» en la estructura),
 * para que al corregir algo no quede la mitad en el campo viejo y la mitad en los nuevos.
 */
private fun migrado(d: Dia): Dia = if (d.sueno_tipo == null) d else d.copy(
    sueno_rareza = Catalogo.rarezaDe(d), sueno_pesadilla = Catalogo.pesadillaDe(d), sueno_categorias = Catalogo.categoriasSuenoDe(d), sueno_tipo = null)

/**
 * Todo lo del sueño de una noche, igual en Día y en la pantalla rápida «Tu sueño». La narración se escribe en el libro de noche
 * del tema, a pantalla completa ([DialogoEscritor] en modo sueño), como el diario. Con [narracionPrimero] (la pantalla rápida),
 * el libro va arriba: es lo que se olvida primero.
 */
@Composable
fun FormularioSueno(fecha: String, d: Dia, narracionPrimero: Boolean = false, guardar: (Dia) -> Unit) {
    val actual by rememberUpdatedState(d)
    var escribiendo by remember(fecha) { mutableStateOf(false) }
    /** Cada cambio parte del día ya migrado al esquema 17. */
    fun cambiar(f: (Dia) -> Dia) = guardar(f(migrado(actual)))
    fun ponerTexto(t: String) = cambiar { it.copy(sueno_texto = t, sueno_recordado = it.sueno_recordado || t.isNotBlank()) }

    val libro: @Composable () -> Unit = {
        Etiqueta("Qué soñaste")
        ObjetoDiario(fecha, d.sueno_texto, noche = true) { escribiendo = true }
        Ayuda(if (d.sueno_texto.isBlank()) "Toca el libro para escribirlo a pantalla completa: dónde estabas, quién aparecía, qué pasaba, qué sentías, cómo terminó. Aunque sean fragmentos."
            else "${porNumero(Texto.palabras(d.sueno_texto), "1 palabra", "${Texto.palabras(d.sueno_texto)} palabras")}. Toca el libro para seguir escribiendo.", Modifier.padding(top = 6.dp))
        Row(Modifier.padding(top = 6.dp)) { BotonDictado { t -> ponerTexto((actual.sueno_texto + " " + t).trim()) } }
    }

    Column {
        if (narracionPrimero) libro()
        Etiqueta("Calidad del sueño, como la sentiste")
        Escala(d.sueno_calidad, 1, 5, Catalogo.ANCLAS["sueno_calidad"]) { v -> cambiar { it.copy(sueno_calidad = v) } }
        Interruptor("¿Recordaste un sueño?", d.sueno_recordado) { v -> cambiar { it.copy(sueno_recordado = v) } }
        AnimatedVisibility(d.sueno_recordado || narracionPrimero && d.sueno_texto.isNotBlank()) {
            Column {
                if (!narracionPrimero) libro()
                Etiqueta("Qué tan agradable fue")
                Escala(d.sueno_valencia, 1, 7, Catalogo.ANCLAS["sueno_valencia"]) { v -> cambiar { it.copy(sueno_valencia = v) } }
                Etiqueta("¿Fue pesadilla? (angustia fuerte o te despertó)")
                FilaFichas(SI_NO, Catalogo.pesadillaDe(d)?.let { if (it) "si" else "no" }) { v -> cambiar { it.copy(sueno_pesadilla = v?.let { x -> x == "si" }) } }
                Etiqueta("¿Qué tan raro fue?")
                FilaFichas(Catalogo.SUENO_RAREZA, Catalogo.rarezaDe(d)) { v -> cambiar { it.copy(sueno_rareza = v) } }
                Etiqueta("Qué tan claro lo recuerdas")
                FilaFichas(Catalogo.SUENO_VIVIDEZ, d.sueno_vividez) { v -> cambiar { it.copy(sueno_vividez = v) } }
                Etiqueta("Lucidez: ¿sabías que soñabas?")
                FilaFichas(Catalogo.SUENO_LUCIDEZ, d.sueno_lucidez) { v -> cambiar { it.copy(sueno_lucidez = v) } }
                Etiqueta("De qué fue")
                CategoriasSueno(Catalogo.categoriasSuenoDe(d)) { l -> cambiar { it.copy(sueno_categorias = l) } }
                Etiqueta("Nombres y temas propios (@ana, #mudanza)")
                ListaEtiquetas(d.sueno_temas, "@persona o #tema", normalizar = { Texto.etiquetaConSimbolo(it) }) { l -> cambiar { it.copy(sueno_temas = l) } }
                Ayuda("Las @ y # que escribas en la narración también cuentan.", Modifier.padding(top = 4.dp))
            }
        }
    }
    if (escribiendo) DialogoEscritor(fecha, d.sueno_texto, { t -> ponerTexto(t) }, Escrito.SUENO) { escribiendo = false }
}
