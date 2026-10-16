@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Obra
import io.github.ruddy2003.vestigium.data.Opcion
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Editor
import io.github.ruddy2003.vestigium.ui.Escala
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import java.time.LocalDate

object EdicionObra { var id by mutableStateOf<String?>(null) }

val TIPOS_OBRA = listOf(Opcion("libro", "Libro"), Opcion("pelicula", "Película"), Opcion("serie", "Serie"), Opcion("juego", "Juego"))
private val ESTADOS = listOf(Opcion("quiero", "Pendiente"), Opcion("en_curso", "En curso"), Opcion("terminado", "Terminado"), Opcion("abandonado", "Abandonado"))

private fun autorDe(tipo: String) = when (tipo) { "libro" -> "Autor"; "pelicula" -> "Dirección"; "serie" -> "Creador o canal"; else -> "Estudio" }
private fun unidad(tipo: String) = when (tipo) { "libro" -> "páginas"; "pelicula" -> "minutos"; "serie" -> "capítulos"; else -> "horas" }
private fun verbo(tipo: String) = when (tipo) { "libro" -> "Leyendo ahora"; "juego" -> "Jugando ahora"; else -> "Viendo ahora" }

@Composable
fun PantallaBiblioteca(nav: Nav) {
    var filtro by remember { mutableStateOf<String?>(null) }
    val todas = Repo.obras.filter { filtro == null || it.tipo == filtro }
    val anio = Fechas.diaLogico().take(4)
    // Lo que se abrió para añadir y se dejó sin título no se queda.
    androidx.compose.runtime.LaunchedEffect(Unit) { Repo.obras.filter { it.titulo.isBlank() }.forEach { Repo.borrarObra(it.id) } }

    fun nueva() {
        val o = Obra(id = Repo.nuevoId(), tipo = filtro ?: "libro", titulo = "", agregado = Fechas.diaLogico())
        Repo.guardarObra(o); EdicionObra.id = o.id; nav.ir("obra")
    }

    Hoja("Biblioteca", nav.atras, "Libros, películas, series y juegos: qué empezaste, qué terminaste y qué te dejó.") {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todo", filtro == null) { filtro = null }
            TIPOS_OBRA.forEach { t -> Ficha(t.nombre.lowercase(), filtro == t.id) { filtro = if (filtro == t.id) null else t.id } }
        }
        BotonPrincipal("Añadir", Modifier.fillMaxWidth().padding(top = 12.dp)) { nueva() }

        val terminadasAnio = Repo.obras.filter { it.estado == "terminado" && it.fin?.startsWith(anio) == true }
        Row(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TIPOS_OBRA.forEach { t -> Cifra("${terminadasAnio.count { it.tipo == t.id }}", "${t.nombre.lowercase()}s $anio", Modifier.weight(1f)) }
        }

        val enCurso = todas.filter { it.estado == "en_curso" }.sortedByDescending { it.inicio ?: it.agregado }
        if (enCurso.isNotEmpty()) Seccion(filtro?.let { verbo(it) } ?: "En curso", latin = "IN MANIBVS") { Lista(enCurso, nav) }
        val terminadas = todas.filter { it.estado == "terminado" }.sortedByDescending { it.fin ?: it.agregado }
        Seccion("Terminados") { if (terminadas.isEmpty()) Vacio("Nada terminado todavía.") else Lista(terminadas, nav, porAnio = true) }
        val pendientes = todas.filter { it.estado == "quiero" }.sortedByDescending { it.agregado }
        if (pendientes.isNotEmpty()) Seccion("Pendientes") { Lista(pendientes, nav) }
        val abandonadas = todas.filter { it.estado == "abandonado" }
        if (abandonadas.isNotEmpty()) Seccion("Archivados", latin = "RELICTA") { Lista(abandonadas, nav) }
        if ((filtro == null || filtro == "libro") && Repo.subrayados.isNotEmpty()) Seccion("Subrayados de Kindle", "Toca un libro para ver lo que marcaste.", latin = "EXCERPTA") {
            var abierto by remember { mutableStateOf<String?>(null) }
            Tarjeta {
                Repo.subrayados.groupBy { it.libro }.entries.sortedByDescending { e -> e.value.maxOfOrNull { it.ts ?: "" } }.forEachIndexed { i, (libro, l) ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().clickable { abierto = if (abierto == libro) null else libro }.padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(libro, style = MaterialTheme.typography.bodyLarge)
                            Text(listOfNotNull(l.first().autor.ifBlank { null }, "${l.size} marcas", l.mapNotNull { it.fecha }.maxOrNull()?.let { Fechas.corto(it) }).joinToString(" · "),
                                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(if (abierto == libro) "−" else "+", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    if (abierto == libro) Subrayados(l)
                }
            }
        }
    }
}

@Composable
private fun Lista(obras: List<Obra>, nav: Nav, porAnio: Boolean = false) {
    Tarjeta {
        var anioPrevio = ""
        obras.forEachIndexed { i, o ->
            val anio = (o.fin ?: o.agregado).take(4)
            if (porAnio && anio != anioPrevio) {
                anioPrevio = anio
                Text(io.github.ruddy2003.vestigium.ui.Romano.de(anio.toIntOrNull() ?: 0) + " · $anio", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = if (i == 0) 0.dp else 12.dp, bottom = 4.dp))
            } else if (i > 0) HorizontalDivider()
            Row(Modifier.fillMaxWidth().clickable { EdicionObra.id = o.id; nav.ir("obra") }.padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(o.titulo.ifBlank { "(sin título)" }, style = MaterialTheme.typography.bodyLarge)
                    val detalle = listOfNotNull(
                        TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre?.lowercase(), o.autor.ifBlank { null }, o.anio?.toString(),
                        if (o.estado == "en_curso" && o.progreso != null && o.total != null && o.total > 0) "${o.progreso * 100 / o.total}%" else null,
                        o.fin?.let { Fechas.corto(it) },
                    ).joinToString(" · ")
                    Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                o.calificacion?.let { Text("$it/10", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}

@Composable
fun PantallaObra(nav: Nav) {
    val ctx = LocalContext.current
    val o = Repo.obras.firstOrNull { it.id == EdicionObra.id }
    if (o == null) { Hoja("Biblioteca", nav.atras) { Vacio("No encontré esa obra.") }; return }
    fun g(n: Obra) = Repo.guardarObra(n)
    var confirmar by remember(o.id) { mutableStateOf(false) }
    val hoy = Fechas.diaLogico()
    fun fecha(actual: String?, poner: (String?) -> Unit) {
        val p = Fechas.parse(actual ?: hoy)
        DatePickerDialog(ctx, { _, y, m, d -> poner(Fechas.iso(LocalDate.of(y, m + 1, d))) }, p.year, p.monthValue - 1, p.dayOfMonth).show()
    }

    Hoja(TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre ?: "Obra", nav.atras, latin = "OPVS", subtitulo = "Se guarda sola mientras escribes.") {
        Seccion("Qué es", numero = 1, latin = "QVID") {
            Tarjeta {
                FilaFichas(TIPOS_OBRA, o.tipo) { g(o.copy(tipo = it ?: o.tipo)) }
                Etiqueta("Título")
                OutlinedTextField(o.titulo, { g(o.copy(titulo = it)) }, Modifier.fillMaxWidth(), singleLine = true)
                Etiqueta(autorDe(o.tipo))
                OutlinedTextField(o.autor, { g(o.copy(autor = it)) }, Modifier.fillMaxWidth(), singleLine = true)
                Etiqueta("Año de publicación o estreno")
                OutlinedTextField(o.anio?.toString() ?: "", { t -> g(o.copy(anio = t.filter { it.isDigit() }.take(4).toIntOrNull())) }, Modifier.width(140.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }
        Seccion("Estado", numero = 2, latin = "STATVS") {
            Tarjeta {
                FilaFichas(ESTADOS, o.estado) { e ->
                    val est = e ?: "quiero"
                    g(o.copy(estado = est,
                        inicio = if ((est == "en_curso" || est == "terminado") && o.inicio == null) hoy else o.inicio,
                        fin = if (est == "terminado" && o.fin == null) hoy else if (est != "terminado" && est != "abandonado") null else o.fin))
                }
                Spacer(Modifier.height(8.dp))
                Clave("initium", o.inicio?.let { Fechas.titulo(it).lowercase() } ?: "—", ancho = 8)
                Clave("finis", o.fin?.let { Fechas.titulo(it).lowercase() } ?: "—", ancho = 8)
                Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BotonSecundario("inicio…") { fecha(o.inicio) { g(o.copy(inicio = it)) } }
                    BotonSecundario("fin…") { fecha(o.fin) { g(o.copy(fin = it)) } }
                    if (o.inicio != null || o.fin != null) BotonSecundario("borrar fechas") { g(o.copy(inicio = null, fin = null)) }
                }
                Etiqueta("Progreso (${unidad(o.tipo)})")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(o.progreso?.toString() ?: "", { t -> g(o.copy(progreso = t.filter { it.isDigit() }.take(6).toIntOrNull())) }, Modifier.width(110.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Text("  de  ", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(o.total?.toString() ?: "", { t -> g(o.copy(total = t.filter { it.isDigit() }.take(6).toIntOrNull())) }, Modifier.width(110.dp), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("veces = ", style = MaterialTheme.typography.labelLarge)
                    BotonSecundario("−") { g(o.copy(veces = (o.veces - 1).coerceAtLeast(1))) }
                    Text("${o.veces}", style = MaterialTheme.typography.titleLarge)
                    BotonSecundario("+") { g(o.copy(veces = o.veces + 1)) }
                }
            }
        }
        Seccion("Qué te dejó", numero = 3, latin = "IVDICIVM") {
            Tarjeta {
                Etiqueta("Calificación")
                Escala(o.calificacion, 1, 10, mapOf(1 to "pésimo", 4 to "flojo", 6 to "bueno", 8 to "excelente", 10 to "de mis favoritos")) { g(o.copy(calificacion = it)) }
                Etiqueta("Etiquetas")
                ListaEtiquetas(o.etiquetas, "ciencia_ficcion, ensayo, recomendado_por_ana", normalizar = { Texto.slug(it) }) { g(o.copy(etiquetas = it)) }
                Etiqueta("Notas")
                Editor(o.nota, "notae.txt", "Ideas, citas, qué sentiste…", minimo = 120) { g(o.copy(nota = it)) }
            }
        }
        val marcas = Repo.subrayados.filter { o.titulo.isNotBlank() && (it.libro.equals(o.titulo, true) || it.libro.contains(o.titulo, true) || o.titulo.contains(it.libro, true)) }
        if (marcas.isNotEmpty()) Seccion("Subrayados de Kindle", numero = 4, latin = "EXCERPTA") { Tarjeta { Subrayados(marcas) } }
        TextButton(onClick = { if (confirmar) { Repo.borrarObra(o.id); nav.atras() } else confirmar = true }, modifier = Modifier.padding(top = 16.dp)) {
            Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun Subrayados(l: List<io.github.ruddy2003.vestigium.data.Subrayado>) {
    Column(Modifier.padding(bottom = 8.dp)) {
        l.sortedBy { it.ubicacion.substringBefore('-').toIntOrNull() ?: it.pagina ?: 0 }.take(80).forEach { s ->
            Row(Modifier.padding(vertical = 5.dp)) {
                androidx.compose.foundation.layout.Box(Modifier.padding(top = 3.dp).width(3.dp).height(16.dp).background(if (s.tipo == "nota") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary))
                Column(Modifier.padding(start = 8.dp)) {
                    Text(s.texto, style = MaterialTheme.typography.bodyMedium)
                    Text(listOfNotNull(s.tipo, s.pagina?.let { "p. $it" }, s.ubicacion.ifBlank { null }?.let { "pos. $it" }, s.fecha?.let { Fechas.corto(it) }).joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (l.size > 80) io.github.ruddy2003.vestigium.ui.Ayuda("…y ${l.size - 80} más en vm_subrayados.csv.")
    }
}
