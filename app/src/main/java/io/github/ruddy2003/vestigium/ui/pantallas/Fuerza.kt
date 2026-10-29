package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Macrofactor
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Serie
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Gimnasio: lo que llega de MacroFactor Workouts. */
@Composable
fun PantallaFuerza(nav: Nav) {
    val series by produceState<List<Serie>?>(null, Repo.entrenos.size) { value = withContext(Dispatchers.IO) { Repo.series("0000-01-01", "9999-12-31") } }
    Hoja("Gimnasio", nav.atras, "Lo que registras en MacroFactor Workouts. Se actualiza cada vez que importas su exportación en Más → Datos.", latin = "ROBVR") {
        val l = series
        if (l == null) { Ayuda("Cargando…", Modifier.padding(top = 12.dp)); return@Hoja }
        if (l.isEmpty()) {
            Vacio("Aún no hay entrenamientos. En MacroFactor: Más → Data Management → Data Export, incluye Workouts, y elige el .xlsx en Más → Datos → Otros servicios.")
            BotonSecundario("ir a Datos →", Modifier.padding(top = 8.dp)) { nav.ir("datos") }
            return@Hoja
        }
        val hoy = Fechas.diaLogico()
        val e = Repo.entrenos.values.filter { it.series > 0 }
        val d30 = e.filter { it.fecha > Fechas.sumar(hoy, -30) }
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("${d30.size}", "entrenos en 30 días", Modifier.weight(1f))
            Cifra("%.0f".format(d30.sumOf { it.series_efectivas } / 30.0 * 7), "series por semana", Modifier.weight(1f))
            Cifra("%,.0f".format(d30.sumOf { it.volumen_kg } / 30.0 * 7), "kg por semana", Modifier.weight(1f))
        }

        Seccion("Series por grupo muscular", "Promedio semanal de las últimas 4 semanas. Para crecer suele recomendarse de 10 a 20 series semanales por grupo.", latin = "MVSCVLI") {
            val d28 = e.filter { it.fecha > Fechas.sumar(hoy, -28) }
            val porGrupo = d28.flatMap { it.series_musculo.entries }.groupBy({ it.key }, { it.value }).mapValues { it.value.sum() / 4.0 }.entries.sortedByDescending { it.value }
            if (porGrupo.isEmpty()) Vacio("La exportación no trajo series por grupo muscular.")
            else Tarjeta {
                val max = maxOf(20.0, porGrupo.maxOf { it.value })
                val c = MaterialTheme.colorScheme.primary; val banda = MaterialTheme.colorScheme.secondaryContainer
                val eg = estiloGrafica()
                porGrupo.forEach { (g, v) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(g.replace('_', ' '), style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(120.dp))
                        Canvas(Modifier.weight(1f).height(12.dp)) {
                            drawRect(banda, Offset((10 / max * size.width).toFloat(), 0f), Size((10 / max * size.width).toFloat(), size.height))
                            barraGrafica(eg, Offset(0f, 2f), Size((v / max * size.width).toFloat(), size.height - 4), c, horizontal = true)
                        }
                        Text("%.1f".format(v), style = estiloCifras(MaterialTheme.typography.labelMedium), modifier = Modifier.width(40.dp).padding(start = 6.dp))
                    }
                }
                Ayuda("La franja de fondo marca de 10 a 20 series.", Modifier.padding(top = 6.dp))
            }
        }

        Seccion("Ejercicios", "1RM estimado (fórmula de Epley) con tus series efectivas de hasta 12 repeticiones. Toca uno para ver su progreso.", latin = "EXERCITIA") {
            var abierto by remember { mutableStateOf<String?>(null) }
            val porEj = l.filter { Macrofactor.e1rm(it) != null }.groupBy { it.ejercicio }.entries.sortedByDescending { en -> en.value.map { it.fecha }.distinct().size }
            Tarjeta {
                porEj.take(30).forEachIndexed { i, (ej, ss) ->
                    if (i > 0) HorizontalDivider()
                    val porSesion = ss.groupBy { it.fecha }.mapValues { en -> en.value.mapNotNull { Macrofactor.e1rm(it) }.max() }.toSortedMap()
                    val ultimo = porSesion.values.last(); val mejor = porSesion.values.max()
                    Column(Modifier.fillMaxWidth().clickable { abierto = if (abierto == ej) null else ej }.padding(vertical = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(ej, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text("%.1f kg".format(ultimo), style = MaterialTheme.typography.labelLarge, color = if (ultimo >= mejor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        }
                        Text("${porSesion.size} sesiones · mejor %.1f kg · desde ${Fechas.corto(porSesion.firstKey())}".format(mejor), style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (abierto == ej && porSesion.size >= 2) {
                            val c = MaterialTheme.colorScheme.primary; val rej = MaterialTheme.colorScheme.outline
                            val eg = estiloGrafica()
                            val vals = porSesion.values.toList(); val lo = vals.min(); val hi = vals.max().let { if (it == lo) lo + 1 else it }
                            Canvas(Modifier.fillMaxWidth().height(90.dp).padding(top = 8.dp)) {
                                ejeGrafica(eg, Offset(0f, size.height), Offset(size.width, size.height), rej)
                                val pts = vals.mapIndexed { k, v -> Offset(size.width * k / (vals.size - 1), (size.height * (1 - (v - lo) / (hi - lo))).toFloat()) }
                                lineaGrafica(eg, pts, c, 4f)
                                pts.forEach { puntoGrafica(eg, it, 4f, c) }
                            }
                            Text("%.1f → %.1f kg".format(vals.first(), vals.last()), style = estiloCifras())
                        }
                    }
                }
            }
        }

        Seccion("Entrenamientos recientes", latin = "ANNALES") {
            Tarjeta {
                e.sortedByDescending { it.fecha }.take(15).forEachIndexed { i, x ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().clickable { Seleccion.abrir(nav, x.fecha) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(x.entrenos.joinToString(", ").ifBlank { "Entrenamiento" }, style = MaterialTheme.typography.bodyMedium)
                            Text(Fechas.corto(x.fecha) + (x.minutos?.let { " · %.0f min".format(it) } ?: "") + " · ${x.series_efectivas} series", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("%,.0f kg".format(x.volumen_kg), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

