@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.LibroDiario
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** Minúsculas y sin acentos, para buscar sin importar cómo se escribió. */
private fun normal(s: String) = java.text.Normalizer.normalize(s.lowercase(), java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

/** Exporta en segundo plano y abre el menú para guardar o enviar el archivo. */
@Composable
private fun rememberExportar(): (String, () -> File, String) -> Unit {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember(ctx) {
        { _: String, hacer: () -> File, mime: String ->
            scope.launch {
                val f = runCatching { withContext(Dispatchers.IO) { hacer() } }.getOrNull()
                if (f != null) LibroDiario.compartir(ctx, f, mime)
                else android.widget.Toast.makeText(ctx, "No se pudo generar el archivo.", android.widget.Toast.LENGTH_LONG).show()
            }
            Unit
        }
    }
}

/** Biblioteca del diario: todas las entradas para leer, buscar y sacar de la app. */
@Composable
fun PantallaDiarios(nav: Nav) {
    val ctx = LocalContext.current
    val todas = remember(Repo.dias.size) { LibroDiario.entradas() }
    var busca by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf<String?>(null) }
    var conFoto by remember { mutableStateOf(false) }
    var largas by remember { mutableStateOf(false) }
    var mostrar by remember { mutableIntStateOf(40) }
    val exportar = rememberExportar()
    val q = normal(busca.trim())
    val lista = todas.filter { d ->
        (anio == null || d.fecha.startsWith(anio!!)) && (!conFoto || d.foto != null) && (!largas || Texto.palabras(d.nota) >= 200) &&
            (q.isBlank() || normal(d.nota + " " + d.mejor_momento + " " + d.peor_momento).contains(q))
    }
    val palabras = remember(todas) { todas.sumOf { Texto.palabras(it.nota) } }

    Hoja("Biblioteca del diario", nav.atras, "Todas tus entradas, de la más reciente a la más antigua. Toca una para leerla completa.") {
        if (todas.isEmpty()) { Vacio("Todavía no hay entradas con texto. Escribe en la hoja de la noche y aparecerán aquí."); return@Hoja }
        Text("${todas.size} entradas · %,d palabras · desde ${Fechas.corto(todas.last().fecha)}".format(palabras),
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 10.dp))
        OutlinedTextField(busca, { busca = it; mostrar = 40 }, Modifier.fillMaxWidth().padding(top = 10.dp), singleLine = true,
            placeholder = { Text("buscar palabras, @personas o #temas", style = MaterialTheme.typography.bodyMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
        FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todos", anio == null) { anio = null }
            todas.map { it.fecha.take(4) }.distinct().forEach { a -> Ficha(a, anio == a) { anio = if (anio == a) null else a } }
            Ficha("con foto", conFoto) { conFoto = !conFoto }
            Ficha("largas", largas) { largas = !largas }
        }

        Seccion("Exportar", "Lo que estás viendo (${lista.size} entradas). El PDF y el HTML llevan el diseño del tema; el de Obsidian es una nota por día con sus datos.") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BotonSecundario("PDF") { exportar("pdf", { LibroDiario.pdf(ctx, lista) }, "application/pdf") }
                BotonSecundario("HTML") { exportar("html", { LibroDiario.html(ctx, lista) }, "text/html") }
                BotonSecundario("Markdown") { exportar("md", { LibroDiario.markdown(ctx, lista) }, "text/markdown") }
                BotonSecundario("Obsidian (.zip)") { exportar("zip", { LibroDiario.obsidian(ctx, lista) }, "application/zip") }
                BotonSecundario("Texto") { exportar("txt", { LibroDiario.texto(ctx, lista) }, "text/plain") }
            }
        }

        Seccion("Entradas") {
            if (lista.isEmpty()) Vacio("Nada coincide con la búsqueda.")
            var mesAnterior = ""
            lista.take(mostrar).forEach { d ->
                val mes = d.fecha.take(7)
                if (mes != mesAnterior) {
                    mesAnterior = mes
                    val rotulo = Estilo.mes(mes.drop(5).toInt(), mes.take(4).toInt()).let { if (it.contains(mes.take(4))) it else it + " · " + mes.take(4) }
                    Text(rotulo,
                        style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 14.dp, bottom = 6.dp))
                }
                FichaEntrada(d) { Seleccion.entrada = d.fecha; nav.ir("entrada") }
            }
            if (lista.size > mostrar) BotonSecundario("ver ${minOf(40, lista.size - mostrar)} más", Modifier.padding(top = 8.dp)) { mostrar += 40 }
        }
    }
}

@Composable
private fun FichaEntrada(d: Dia, abrir: () -> Unit) {
    val color = Tonos.valencia(d.valencia?.toDouble(), esOscuro()) ?: MaterialTheme.colorScheme.outline
    Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).clickable(onClick = abrir)) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(5.dp).fillMaxHeight().background(color))
            Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(Fechas.titulo(d.fecha), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                    Text("${Texto.palabras(d.nota)} palabras" + (d.valencia?.let { " · $it/7" } ?: ""), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(d.nota.trim(), style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
            }
        }
    }
}

/** Una entrada para leer completa, con flechas a la anterior y la siguiente, y su PDF. */
@Composable
fun PantallaEntrada(nav: Nav) {
    val ctx = LocalContext.current
    val todas = remember(Repo.dias.size) { LibroDiario.entradas() }
    var fecha by remember { mutableStateOf(Seleccion.entrada ?: todas.firstOrNull()?.fecha ?: Fechas.diaLogico()) }
    val d = Repo.dias[fecha]
    val exportar = rememberExportar()
    val i = todas.indexOfFirst { it.fecha == fecha }
    Hoja("Entrada", nav.atras, encabezado = {
        Text(Estilo.fecha(fecha), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 10.dp))
        Text(Estilo.comentario + Fechas.titulo(fecha).lowercase() + " · " + Fechas.relativo(fecha), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }) {
        if (d == null) { Vacio("No encontré esa entrada."); return@Hoja }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // La lista va de la más reciente a la más antigua: «anterior» en el tiempo es el índice siguiente.
            BotonSecundario("‹ anterior", habilitado = i in 0 until todas.size - 1) { fecha = todas[i + 1].fecha }
            BotonSecundario("siguiente ›", habilitado = i > 0, modifier = Modifier.weight(1f)) { fecha = todas[i - 1].fecha }
        }
        Portada(d.foto)
        LibroDiario.meta(d).takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp)) }
        Tarjeta(Modifier.padding(top = 10.dp)) {
            androidx.compose.foundation.text.selection.SelectionContainer {
                Text(d.nota.trim(), style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (d.mejor_momento.isNotBlank() || d.peor_momento.isNotBlank()) Tarjeta(Modifier.padding(top = 8.dp)) {
            if (d.mejor_momento.isNotBlank()) Text("Lo mejor: " + d.mejor_momento, style = MaterialTheme.typography.bodyMedium)
            if (d.peor_momento.isNotBlank()) Text("Lo peor: " + d.peor_momento, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        }
        if (d.sueno_texto.isNotBlank()) Seccion("Sueño") { Tarjeta { Text(d.sueno_texto.trim(), style = MaterialTheme.typography.bodyMedium) } }
        FraseDelDia(fecha)
        FlowRow(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonSecundario("PDF de esta entrada") { exportar("pdf", { LibroDiario.pdf(ctx, listOf(d)) }, "application/pdf") }
            BotonSecundario("compartir texto") {
                val t = Fechas.titulo(fecha) + "\n\n" + d.nota.trim()
                ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, t), "Compartir").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            BotonSecundario("abrir el día") { Seleccion.abrir(nav, fecha) }
        }
        Ayuda("El PDF lleva los colores, letras y el emblema del tema que tengas puesto.", Modifier.padding(top = 6.dp))
    }
}
