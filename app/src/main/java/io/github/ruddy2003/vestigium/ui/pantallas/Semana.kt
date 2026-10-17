@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlin.math.abs

/** Cifras de una semana (lunes a domingo, días lógicos). */
private class ResumenSemana(val dias: List<String>) {
    private fun prom(l: List<Double>) = if (l.isEmpty()) null else l.average()
    val diario = dias.count { f -> Repo.dias[f]?.let { it.valencia != null || it.nota.isNotBlank() } == true }
    val valencias = dias.map { f -> Repo.dias[f]?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == f && it.tipo == "muestreo" }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average() }
    val animo = prom(valencias.filterNotNull())
    val sueno = prom(dias.mapNotNull { Repo.salud[it]?.sueno_min?.let { m -> m / 60.0 } })
    val pasos = dias.mapNotNull { Repo.salud[it]?.pasos }.takeIf { it.isNotEmpty() }?.sum()
    val pantalla = prom(dias.mapNotNull { Repo.pantalla[it]?.total_min?.toDouble() })
    val gasto = Repo.gastos.filter { it.dia in dias }.sumOf { it.monto }
    val kcal = prom(dias.mapNotNull { Repo.nutricion[it]?.valores?.get("kcal") })
    val entrenos = dias.count { (Repo.entrenos[it]?.series ?: 0) > 0 } + dias.count { f -> Repo.ascensos.any { it.fecha == f } }
    val momentos = Repo.momentos.filter { it.fecha in dias && it.tipo != "muestreo" }
}

@Composable
fun PantallaSemana(nav: Nav) {
    val hoy = Fechas.diaLogico()
    var lunes by remember { mutableStateOf(Fechas.lunesDe(hoy)) }
    val dias = (0..6).map { Fechas.sumar(lunes, it.toLong()) }
    val r = remember(lunes, Repo.dias.size, Repo.momentos.size, Repo.gastos.size) { ResumenSemana(dias) }
    val antes = remember(lunes, Repo.dias.size) { ResumenSemana(dias.map { Fechas.sumar(it, -7) }) }
    val oscuro = esOscuro()

    Hoja("Resumen semanal", nav.atras, "Tu semana de un vistazo, comparada con la anterior. Toca un día para abrirlo.") {
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonSecundario("‹") { lunes = Fechas.sumar(lunes, -7) }
            Text("${Fechas.corto(dias.first())} — ${Fechas.corto(dias.last())}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            BotonSecundario("›", habilitado = lunes < Fechas.lunesDe(hoy)) { lunes = Fechas.sumar(lunes, 7) }
        }
        // Los siete días coloreados por ánimo, con su número para no depender solo del color.
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            dias.forEachIndexed { i, f ->
                val v = r.valencias[i]
                val fondo = Tonos.valencia(v, oscuro) ?: MaterialTheme.colorScheme.surfaceVariant
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Estilo.semana[i].take(3), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box(Modifier.fillMaxWidth().aspectRatio(0.8f).clip(MaterialTheme.shapes.small).background(fondo).clickable(enabled = f <= hoy) { Seleccion.abrir(nav, f) },
                        contentAlignment = Alignment.Center) {
                        Text(v?.let { "%.0f".format(it) } ?: if (f > hoy) "" else "·", style = MaterialTheme.typography.titleMedium, color = Tonos.sobre(fondo))
                    }
                    Text(f.takeLast(2).trimStart('0'), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Seccion("Cifras") {
            Tarjeta {
                fun fila(nombre: String, v: Double?, a: Double?, f: (Double) -> String) {
                    if (v == null) return
                    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(f(v), style = MaterialTheme.typography.titleMedium)
                        val d = if (a != null && a != 0.0) (v - a) / abs(a) else null
                        Text(if (d == null) "" else if (abs(d) < 0.05) "  =" else "  ${if (d > 0) "↑" else "↓"}${"%.0f".format(abs(d) * 100)}%",
                            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                    }
                }
                fila("Días con diario", r.diario.toDouble(), antes.diario.toDouble()) { "${it.toInt()} de 7" }
                fila("Ánimo promedio", r.animo, antes.animo) { "%.1f / 7".format(it) }
                fila("Sueño por noche", r.sueno, antes.sueno) { "%.1f h".format(it) }
                fila("Pasos en la semana", r.pasos?.toDouble(), antes.pasos?.toDouble()) { "%,.0f".format(it) }
                fila("Pantalla al día", r.pantalla, antes.pantalla) { "%d h %02d".format((it / 60).toInt(), (it % 60).toInt()) }
                fila("Gasto", r.gasto.takeIf { it > 0 }, antes.gasto) { "$%,.0f".format(it) }
                fila("Calorías al día", r.kcal, antes.kcal) { "%,.0f".format(it) }
                fila("Entrenamientos", r.entrenos.toDouble().takeIf { it > 0 }, antes.entrenos.toDouble()) { "%.0f".format(it) }
                Ayuda("Las flechas comparan con la semana anterior.", Modifier.padding(top = 4.dp))
            }
        }

        val conValencia = dias.zip(r.valencias).filter { it.second != null }
        if (conValencia.size >= 2) Seccion("Lo mejor y lo peor") {
            val mejor = conValencia.maxBy { it.second!! }; val peor = conValencia.minBy { it.second!! }
            Tarjeta {
                listOf("Mejor día" to mejor, "Día más difícil" to peor).forEachIndexed { k, (t, p) ->
                    if (k > 0) HorizontalDivider(Modifier.padding(vertical = 6.dp))
                    Column(Modifier.fillMaxWidth().clickable { Seleccion.abrir(nav, p.first) }) {
                        Text("$t · ${Fechas.titulo(p.first).lowercase()} · ${"%.0f".format(p.second)}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Repo.dias[p.first]?.nota?.takeIf { it.isNotBlank() }?.let { Text(it.take(220) + if (it.length > 220) "…" else "", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }

        val eq = Repo.equivalencias()
        val notas = dias.mapNotNull { Repo.dias[it]?.nota } + r.momentos.map { it.texto }
        val personas = notas.flatMap { Texto.personas(it).map { p -> Texto.canon(p, eq) }.distinct() }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
        val temas = notas.flatMap { Texto.temas(it).map { p -> Texto.canon(p, eq) }.distinct() }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
        val hitos = dias.flatMap { f -> Repo.dias[f]?.hitos.orEmpty() }
        if (personas.isNotEmpty() || temas.isNotEmpty() || hitos.isNotEmpty()) Seccion("Personas, temas e hitos") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                personas.take(8).forEach { (p, n) -> Ficha("@$p · $n", false) { nav.ir("buscar") } }
                temas.take(8).forEach { (t, n) -> Ficha("#$t · $n", false) { nav.ir("buscar") } }
                hitos.distinct().forEach { h -> Ficha("★ ${h.replace('_', ' ')}", true) {} }
            }
        }

        if (r.momentos.isNotEmpty()) Seccion("Momentos de la semana") {
            Tarjeta {
                r.momentos.groupingBy { it.tipo }.eachCount().entries.sortedByDescending { it.value }.forEach { (t, n) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                        Text(Catalogo.nombreTipo(t), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text("$n", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        if (r.diario == 0 && r.animo == null) Vacio("Esta semana todavía no tiene registros.")
    }
}
