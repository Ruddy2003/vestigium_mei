@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Mascota
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Opcion
import io.github.ruddy2003.vestigium.data.Perfil
import io.github.ruddy2003.vestigium.data.Perfiles
import io.github.ruddy2003.vestigium.data.PersonaCercana
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Emblema
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Etiquetas
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
private fun Varias(opciones: List<Opcion>, elegidas: List<String>, onCambio: (List<String>) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        opciones.forEach { o -> Ficha(o.nombre, o.id in elegidas) { onCambio(if (o.id in elegidas) elegidas - o.id else elegidas + o.id) } }
    }
}

/**
 * «Conocerte»: lo grande de tu vida (con quién vives, a quién ves, a qué te dedicas). Aparece una vez al instalar,
 * se puede omitir y se cambia cuando cambie algo; cada cambio se guarda con su fecha.
 */
@Composable
fun PantallaConocer(nav: Nav) {
    val ctx = LocalContext.current
    val primera = !Repo.config.conocer_visto
    var p by remember { mutableStateOf(Repo.config.perfil) }
    fun cambiar(n: Perfil) { p = n }
    fun guardar() {
        val antes = Repo.config.perfil
        val cambios = if (Perfiles.vacio(antes)) emptyList() else Perfiles.cambios(antes, p)
        Repo.cambiarConfig { c -> c.copy(perfil = p, conocer_visto = true, perfil_historial = c.perfil_historial + cambios) }
        nav.atras()
    }
    Hoja("Conocerte", if (primera) null else nav.atras, encabezado = if (!primera) null else ({
        Column(Modifier.fillMaxWidth().padding(top = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Emblema(tam = 64.dp)
            Text("Antes de empezar", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 14.dp))
            Text("Unas preguntas sobre lo grande de tu vida", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    })) {
        Ayuda("Con quién vives, a quién ves, a qué te dedicas. Sirve para que los análisis entiendan tus días: si tu ánimo cambia cuando ves a tu familia, " +
            "cuando sales de tu ciudad o cuando cambias de trabajo. Todo se queda en este teléfono. Puedes dejar en blanco lo que quieras y cambiarlo después; " +
            "cada cambio se guarda con su fecha.", Modifier.padding(top = 8.dp))

        Seccion("Tú", numero = 1) {
            Tarjeta {
                OutlinedTextField(p.nombre, { cambiar(p.copy(nombre = it)) }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("¿Cómo te llamas? (firma tus cartas)") })
                Etiqueta("Cuándo naciste")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BotonSecundario(if (p.nacimiento.isBlank()) "elegir fecha" else Fechas.larga(p.nacimiento)) {
                        val d = runCatching { Fechas.parse(p.nacimiento) }.getOrDefault(LocalDate.now().minusYears(25))
                        DatePickerDialog(ctx, { _, y, m, dd -> cambiar(p.copy(nacimiento = Fechas.iso(LocalDate.of(y, m + 1, dd)))) }, d.year, d.monthValue - 1, d.dayOfMonth)
                            .apply { datePicker.maxDate = System.currentTimeMillis() }.show()
                    }
                    Perfiles.edad(p = p)?.let { Text("  $it años", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
                }
                Etiqueta("Dónde vives")
                CiudadDeCasa(p) { cambiar(it) }
            }
        }

        Seccion("Con quién vives", numero = 2) {
            Tarjeta {
                Varias(Perfiles.VIVE_CON, p.vive_con) { cambiar(p.copy(vive_con = it)) }
                if (p.vive_con.any { it != "solo" }) {
                    Etiqueta("Sus nombres, como los escribes en el diario")
                    ListaEtiquetas(p.convivientes, "@nombre", prefijo = "@", normalizar = { Texto.etiqueta(it) }) { cambiar(p.copy(convivientes = it.distinct())) }
                }
                Etiqueta("Mascotas")
                p.mascotas.forEachIndexed { i, m ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(m.nombre + (Perfiles.ESPECIES.firstOrNull { it.id == m.especie }?.let { " · ${it.nombre.lowercase()}" } ?: ""), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        TextButton(onClick = { cambiar(p.copy(mascotas = p.mascotas.filterIndexed { j, _ -> j != i })) }) { Text("quitar", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
                var mascota by remember { mutableStateOf("") }
                var especie by remember { mutableStateOf<String?>("perro") }
                OutlinedTextField(mascota, { mascota = it }, Modifier.fillMaxWidth().padding(top = 4.dp), singleLine = true, placeholder = { Text("nombre de tu mascota") })
                if (mascota.isNotBlank()) {
                    Row(Modifier.padding(top = 8.dp)) { FilaFichas(Perfiles.ESPECIES, especie) { especie = it } }
                    BotonSecundario("+ añadir mascota", Modifier.padding(top = 8.dp)) {
                        cambiar(p.copy(mascotas = p.mascotas + Mascota(mascota.trim(), especie.orEmpty()))); mascota = ""
                    }
                }
            }
        }

        Seccion("A quién ves más", "Las personas que más ves: tu pareja, tu familia, tus amistades, gente del trabajo. Usa el mismo nombre que en tus @ del diario.", numero = 3) {
            Tarjeta {
                p.cercanas.forEachIndexed { i, c ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("@${c.nombre}" + (Perfiles.RELACIONES.firstOrNull { it.id == c.relacion }?.let { " · ${it.nombre.lowercase()}" } ?: ""),
                            style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        TextButton(onClick = { cambiar(p.copy(cercanas = p.cercanas.filterIndexed { j, _ -> j != i })) }) { Text("quitar", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
                var quien by remember { mutableStateOf("") }
                var relacion by remember { mutableStateOf<String?>("familia") }
                val sugeridas = remember { Etiquetas.frecuentes().first }
                OutlinedTextField(quien, { quien = Texto.etiqueta(it) }, Modifier.fillMaxWidth().padding(top = 4.dp), singleLine = true, prefix = { Text("@") },
                    placeholder = { Text("nombre") })
                val sug = sugeridas.filter { s -> p.cercanas.none { it.nombre == s } && s != quien && (quien.isBlank() || s.startsWith(quien)) }.take(8)
                if (sug.isNotEmpty()) FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sug.forEach { s -> Ficha("@$s", false) { quien = s } }
                }
                if (quien.isNotBlank()) {
                    Row(Modifier.padding(top = 8.dp)) { FilaFichas(Perfiles.RELACIONES, relacion) { relacion = it } }
                    BotonSecundario("+ añadir a @$quien", Modifier.padding(top = 8.dp)) {
                        cambiar(p.copy(cercanas = p.cercanas.filter { it.nombre != quien } + PersonaCercana(quien, relacion.orEmpty()))); quien = ""
                    }
                }
            }
        }

        Seccion("Pareja", numero = 4) { Tarjeta { FilaFichas(Perfiles.PAREJA, p.pareja.ifBlank { null }) { cambiar(p.copy(pareja = it.orEmpty())) } } }

        Seccion("A qué te dedicas", numero = 5) {
            Tarjeta {
                Varias(Perfiles.OCUPACION, p.ocupacion) { cambiar(p.copy(ocupacion = it)) }
                OutlinedTextField(p.ocupacion_detalle, { cambiar(p.copy(ocupacion_detalle = it)) }, Modifier.fillMaxWidth().padding(top = 10.dp), singleLine = true,
                    placeholder = { Text("qué estudias o en qué trabajas") })
                Etiqueta("Tu horario"); FilaFichas(Perfiles.HORARIO, p.horario.ifBlank { null }) { cambiar(p.copy(horario = it.orEmpty())) }
                Etiqueta("¿Eres de mañana o de noche?"); FilaFichas(Perfiles.CRONOTIPO, p.cronotipo.ifBlank { null }) { cambiar(p.copy(cronotipo = it.orEmpty())) }
            }
        }

        Seccion("Tus costumbres", "Cómo sueles ser en general; lo de cada día se mide aparte.", numero = 6) {
            Tarjeta {
                Etiqueta("Ejercicio"); FilaFichas(Perfiles.EJERCICIO, p.ejercicio.ifBlank { null }) { cambiar(p.copy(ejercicio = it.orEmpty())) }
                Etiqueta("Alcohol"); FilaFichas(Perfiles.ALCOHOL, p.alcohol.ifBlank { null }) { cambiar(p.copy(alcohol = it.orEmpty())) }
                Etiqueta("Tabaco o vapeo"); FilaFichas(Perfiles.TABACO, p.tabaco.ifBlank { null }) { cambiar(p.copy(tabaco = it.orEmpty())) }
                Etiqueta("Café"); FilaFichas(Perfiles.CAFE, p.cafe.ifBlank { null }) { cambiar(p.copy(cafe = it.orEmpty())) }
            }
        }

        Seccion("Lo que está pasando en tu vida", "Algo grande de esta temporada: una mudanza, un trabajo nuevo, una pérdida, una relación que empieza o termina.", numero = 7) {
            OutlinedTextField(p.momento, { cambiar(p.copy(momento = it)) }, Modifier.fillMaxWidth(), minLines = 3)
        }

        BotonPrincipal(if (primera) "Listo" else "Guardar cambios", Modifier.fillMaxWidth().padding(top = 22.dp), habilitado = primera || p != Repo.config.perfil) { guardar() }
        if (primera) TextButton(onClick = { Repo.cambiarConfig { it.copy(conocer_visto = true) }; nav.atras() }, modifier = Modifier.fillMaxWidth()) {
            Text("Omitir por ahora (está en Más → Conocerte)", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        val historial = Repo.config.perfil_historial
        if (!primera && historial.isNotEmpty()) Seccion("Cómo ha cambiado", "Cada vez que cambiaste algo, con su fecha.", plegable = true) {
            Tarjeta {
                historial.sortedByDescending { it.fecha }.take(40).forEachIndexed { i, c ->
                    if (i > 0) HorizontalDivider()
                    Column(Modifier.padding(vertical = 7.dp)) {
                        Text("${Fechas.corto(c.fecha)} ${c.fecha.take(4)} · ${c.campo}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text("${c.antes.ifBlank { "(vacío)" }}  →  ${c.despues.ifBlank { "(vacío)" }}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/** Buscar tu ciudad en la lista de ciudades del mundo (sin internet): queda con su país y su punto en el mapa. */
@Composable
private fun CiudadDeCasa(p: Perfil, cambiar: (Perfil) -> Unit) {
    val ctx = LocalContext.current
    var q by remember { mutableStateOf("") }
    val resultados by produceState(emptyList<Triple<String, String, Pair<Double, Double>>>(), q) {
        value = if (q.trim().length < 2) emptyList() else withContext(Dispatchers.IO) {
            runCatching {
                val c = Mundo.ciudades(ctx)
                Mundo.buscarCiudad(ctx, q, 8).map { i -> Triple(c.nombre(i), c.pais(i), c.lat[i] / 1e5 to c.lon[i] / 1e5) }
            }.getOrDefault(emptyList())
        }
    }
    if (p.ciudad.isNotBlank()) Row(verticalAlignment = Alignment.CenterVertically) {
        Text("${Mundo.bandera(p.pais)}  ${p.ciudad}" + (Mundo.pais(ctx, p.pais)?.nombre?.let { ", $it" } ?: ""), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        TextButton(onClick = { cambiar(p.copy(ciudad = "", pais = "", ciudad_lat = null, ciudad_lon = null)) }) { Text("cambiar", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    } else {
        OutlinedTextField(q, { q = it }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("escribe tu ciudad") })
        if (resultados.isNotEmpty()) FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            resultados.forEach { (n, pa, ll) ->
                Ficha("${Mundo.bandera(pa)} $n" + (Mundo.pais(ctx, pa)?.nombre?.let { ", $it" } ?: ""), false) {
                    cambiar(p.copy(ciudad = n, pais = pa, ciudad_lat = ll.first, ciudad_lon = ll.second)); q = ""
                }
            }
        }
    }
}
