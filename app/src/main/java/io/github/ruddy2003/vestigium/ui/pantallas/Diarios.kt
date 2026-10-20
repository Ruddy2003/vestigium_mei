@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.layout.heightIn
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

/**
 * Pestaña Diario: todo lo que escribiste en una sola línea de tiempo (entradas, momentos y sueños, por día),
 * con registro rápido arriba, buscador, filtros y exportación.
 */
@Composable
fun PantallaDiario(nav: Nav) {
    val ctx = LocalContext.current
    val discreto = Repo.config.discreto
    var editando by remember { mutableStateOf<io.github.ruddy2003.vestigium.data.Momento?>(null) }
    var escribiendo by remember { mutableStateOf<String?>(null) }
    var busca by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("todo") }
    var anio by remember { mutableStateOf<String?>(null) }
    var conFoto by remember { mutableStateOf(false) }
    var largas by remember { mutableStateOf(false) }
    var mostrar by remember { mutableIntStateOf(30) }
    val exportar = rememberExportar()
    fun rapido(modo: String, t: String? = null) {
        val i = Intent(ctx, io.github.ruddy2003.vestigium.ui.QuickActivity::class.java).putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.MODO, modo)
            .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.ORIGEN, "app")
        if (t != null) i.putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.TIPO, t)
        ctx.startActivity(i)
    }
    val privado = Repo.config.seguimiento_privado
    val visiblesM = remember(Repo.momentos, privado) { Repo.momentos.filter { privado || it.tipo != "recaida" } }
    val porDia = remember(visiblesM) { visiblesM.groupBy { it.fecha } }
    val fechas = remember(Repo.dias, visiblesM) {
        (Repo.dias.values.filter { it.nota.isNotBlank() || it.sueno_texto.isNotBlank() }.map { it.fecha } + visiblesM.map { it.fecha }).distinct().sortedDescending()
    }
    val q = normal(busca.trim())
    val visibles = fechas.filter { f ->
        val d = Repo.dias[f]; val ms = porDia[f].orEmpty()
        val tieneEntrada = d?.nota?.isNotBlank() == true; val tieneSueno = d?.sueno_texto?.isNotBlank() == true
        (when (tipo) { "entradas" -> tieneEntrada; "momentos" -> ms.isNotEmpty(); "suenos" -> tieneSueno; else -> true }) &&
            (anio == null || f.startsWith(anio!!)) && (!conFoto || d?.foto != null) && (!largas || Texto.palabras(d?.nota) >= 200) &&
            (q.isBlank() || normal(listOfNotNull(d?.nota, d?.mejor_momento, d?.peor_momento, d?.sueno_texto).joinToString(" ") + " " +
                ms.joinToString(" ") { it.texto + " " + io.github.ruddy2003.vestigium.data.Catalogo.titulo(it, false) + " " + it.pensamiento.orEmpty() }).contains(q))
    }
    val entradas = visibles.mapNotNull { Repo.dias[it]?.takeIf { d -> d.nota.isNotBlank() } }
    val palabras = remember(Repo.dias) { Repo.dias.values.sumOf { Texto.palabras(it.nota) } }

    Hoja("Diario", buscar = { nav.ir("buscar") }, ajustes = { nav.ir("mas") }) {
        // Un solo botón: el ánimo de ahora (sentimientos, pensamiento, con quién) y, desde ahí, una sustancia o un impulso.
        BotonAnimo(Modifier.padding(top = 14.dp)) { rapido(io.github.ruddy2003.vestigium.ui.QuickActivity.M_ANIMO) }
        // El diario de hoy, como objeto del tema: al tocarlo se abre a pantalla completa.
        val hoy = Fechas.diaLogico()
        Seccion("Hoy", "Toca para escribir. Se guarda mientras escribes.") {
            io.github.ruddy2003.vestigium.ui.ObjetoDiario(hoy, Repo.dias[hoy]?.nota.orEmpty()) { escribiendo = hoy }
        }
        // La biblioteca: las últimas entradas en su estante, y la puerta para entrar.
        val ultimas = remember(Repo.dias) { LibroDiario.entradas().take(9) }
        Seccion("Biblioteca del diario", "${Repo.dias.values.count { it.nota.isNotBlank() }} entradas · %,d palabras".format(palabras)) {
            if (ultimas.isEmpty()) Vacio("Aquí se irán guardando tus entradas.")
            else io.github.ruddy2003.vestigium.ui.EstanteDiario(ultimas) { f -> Seleccion.entrada = f; nav.ir("entrada") }
            io.github.ruddy2003.vestigium.ui.BotonPrincipal("entrar a la biblioteca →", Modifier.fillMaxWidth().padding(top = 12.dp)) { nav.ir("biblioteca_diario") }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonSecundario("☾  sueños", Modifier.weight(1f)) { nav.ir("biblioteca_suenos") }
                BotonSecundario("✦  momentos", Modifier.weight(1f)) { nav.ir("biblioteca_momentos") }
                BotonSecundario("✉  cartas", Modifier.weight(1f)) { nav.ir("cartas") }
            }
        }
        // La línea de tiempo y la exportación empiezan plegadas, y se vuelven a plegar al cerrar la app o al cambiar de día.
        Seccion("Línea de tiempo", "Entradas, momentos y sueños, día por día, con buscador y filtros.", autoPlegar = true) { Column {
        Text("${visiblesM.size} registros de ánimo y momentos", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(busca, { busca = it; mostrar = 30 }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true,
            placeholder = { Text("buscar en tu diario, @personas, #temas", style = MaterialTheme.typography.bodyMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
        FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("todo" to "todo", "entradas" to "entradas", "momentos" to "momentos", "suenos" to "sueños").forEach { (id, n) -> Ficha(n, tipo == id) { tipo = id; mostrar = 30 } }
        }
        FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            fechas.map { it.take(4) }.distinct().forEach { a -> Ficha(a, anio == a) { anio = if (anio == a) null else a } }
            Ficha("con foto", conFoto) { conFoto = !conFoto }
            Ficha("largas", largas) { largas = !largas }
        }

        if (fechas.isEmpty()) Vacio("Todavía no hay nada escrito. Registra un momento arriba o escribe el diario de hoy.")
        else if (visibles.isEmpty()) Vacio("Nada coincide con la búsqueda o los filtros.")
        var mesAnterior = ""
        visibles.take(mostrar).forEach { f ->
            val mes = f.take(7)
            if (mes != mesAnterior) {
                mesAnterior = mes
                val rotulo = Estilo.mes(mes.drop(5).toInt(), mes.take(4).toInt()).let { if (it.contains(mes.take(4))) it else it + " · " + mes.take(4) }
                Text(rotulo, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
            }
            TarjetaDiaDiario(f, Repo.dias[f], porDia[f].orEmpty(), tipo, discreto,
                abrirDia = { Seleccion.abrir(nav, f) }, abrirEntrada = { Seleccion.entrada = f; nav.ir("entrada") }, editar = { editando = it })
        }
        if (visibles.size > mostrar) BotonSecundario("ver ${minOf(30, visibles.size - mostrar)} días más", Modifier.padding(top = 8.dp)) { mostrar += 30 }
        } }

        Seccion("Exportar", "Las ${entradas.size} entradas de lo que estás viendo. El PDF y el HTML llevan el diseño del tema; el de Obsidian es una nota por día con sus datos.",
            autoPlegar = true) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BotonSecundario("PDF", habilitado = entradas.isNotEmpty()) { exportar("pdf", { LibroDiario.pdf(ctx, entradas) }, "application/pdf") }
                BotonSecundario("HTML", habilitado = entradas.isNotEmpty()) { exportar("html", { LibroDiario.html(ctx, entradas) }, "text/html") }
                BotonSecundario("Markdown", habilitado = entradas.isNotEmpty()) { exportar("md", { LibroDiario.markdown(ctx, entradas) }, "text/markdown") }
                BotonSecundario("Obsidian (.zip)", habilitado = entradas.isNotEmpty()) { exportar("zip", { LibroDiario.obsidian(ctx, entradas) }, "application/zip") }
                BotonSecundario("Texto", habilitado = entradas.isNotEmpty()) { exportar("txt", { LibroDiario.texto(ctx, entradas) }, "text/plain") }
            }
        }
    }
    editando?.let { m -> DialogoMomento(m) { editando = null } }
    escribiendo?.let { f -> io.github.ruddy2003.vestigium.ui.EscribirDia(f) { escribiendo = null } }
}

/** El botón grande de la pestaña Diario: abre el selector de ánimo. */
@Composable
fun BotonAnimo(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val hoy = Fechas.diaLogico()
    val ultimo = Repo.momentos.filter { it.fecha == hoy && io.github.ruddy2003.vestigium.data.Catalogo.esRegistro(it) }.maxByOrNull { it.ts }
    Surface(onClick = onClick, modifier = modifier.fillMaxWidth().heightIn(min = 76.dp), shape = MaterialTheme.shapes.medium, color = cs.primary) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text("¿Cómo estás ahora?", style = MaterialTheme.typography.titleLarge, color = cs.onPrimary)
            Text(ultimo?.let { "último: ${Fechas.horaDe(it.ts)} · ${io.github.ruddy2003.vestigium.data.Catalogo.titulo(it, Repo.config.discreto)}" }
                ?: "ánimo, sentimientos, pensamiento · y si tomaste o fumaste algo",
                style = MaterialTheme.typography.labelMedium, color = cs.onPrimary.copy(alpha = 0.85f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Un día en la línea de tiempo: la entrada, el sueño y los momentos, cada cosa tocable. */
@Composable
private fun TarjetaDiaDiario(f: String, d: Dia?, ms: List<io.github.ruddy2003.vestigium.data.Momento>, tipo: String, discreto: Boolean,
                             abrirDia: () -> Unit, abrirEntrada: () -> Unit, editar: (io.github.ruddy2003.vestigium.data.Momento) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val color = Tonos.valencia(d?.valencia?.toDouble(), esOscuro()) ?: cs.outline
    Surface(shape = MaterialTheme.shapes.small, color = cs.surface, border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(5.dp).fillMaxHeight().background(color))
            Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Row(Modifier.fillMaxWidth().clickable(onClick = abrirDia), verticalAlignment = Alignment.CenterVertically) {
                    Text(Fechas.titulo(f), style = MaterialTheme.typography.labelLarge, color = cs.primary, modifier = Modifier.weight(1f))
                    Text(listOfNotNull(d?.nota?.takeIf { it.isNotBlank() }?.let { "${Texto.palabras(it)} palabras" }, d?.valencia?.let { "$it/7" }).joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                }
                if (tipo != "momentos" && tipo != "suenos" && d != null && d.nota.isNotBlank())
                    io.github.ruddy2003.vestigium.ui.TextoMarcado(d.nota.trim(), style = MaterialTheme.typography.bodyMedium, maxLines = 4, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp).clickable(onClick = abrirEntrada))
                if ((tipo == "todo" || tipo == "suenos") && d != null && d.sueno_texto.isNotBlank())
                    Text("☾ " + d.sueno_texto.trim(), style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 6.dp))
                if (tipo == "todo" || tipo == "momentos") {
                    ms.sortedBy { it.ts }.take(8).forEach { m ->
                        Row(Modifier.fillMaxWidth().clickable { editar(m) }.padding(top = 5.dp), verticalAlignment = Alignment.Top) {
                            Text(Fechas.horaDe(m.ts), style = MaterialTheme.typography.labelMedium, color = cs.primary, modifier = Modifier.width(46.dp))
                            io.github.ruddy2003.vestigium.ui.TextoMarcado(io.github.ruddy2003.vestigium.data.Catalogo.titulo(m, discreto) + (m.valencia?.let { " · $it/7" } ?: "") + (m.intensidad?.let { " · $it/5" } ?: "") +
                                (if (m.texto.isNotBlank()) " — ${m.texto}" else ""), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    if (ms.size > 8) Text("+${ms.size - 8} momentos más", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                }
            }
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
                io.github.ruddy2003.vestigium.ui.TextoMarcado(d.nota.trim(), style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
            }
        }
    }
}

/** Una entrada abierta a pantalla completa, con el diseño del tema: anterior y siguiente, editar, PDF y compartir. */
@Composable
fun PantallaEntrada(nav: Nav) {
    val ctx = LocalContext.current
    val todas = remember(Repo.dias) { LibroDiario.entradas() }
    var fecha by remember { mutableStateOf(Seleccion.entrada ?: todas.firstOrNull()?.fecha ?: Fechas.diaLogico()) }
    var escribiendo by remember { mutableStateOf(false) }
    val d = Repo.dias[fecha]
    val exportar = rememberExportar()
    val i = todas.indexOfFirst { it.fecha == fecha }
    // La lista va de la más reciente a la más antigua: «anterior» en el tiempo es el índice siguiente.
    io.github.ruddy2003.vestigium.ui.LecturaTema(fecha, d, volver = nav.atras,
        anterior = if (i in 0 until todas.size - 1) ({ fecha = todas[i + 1].fecha; Seleccion.entrada = fecha }) else null,
        siguiente = if (i > 0) ({ fecha = todas[i - 1].fecha; Seleccion.entrada = fecha }) else null,
        acciones = listOf(
            "Editar" to { escribiendo = true },
            "PDF de esta entrada" to { if (d != null) exportar("pdf", { LibroDiario.pdf(ctx, listOf(d)) }, "application/pdf") },
            "Compartir texto" to {
                if (d != null) {
                    val t = Fechas.titulo(fecha) + "\n\n" + d.nota.trim()
                    ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, t), "Compartir").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            },
            "Abrir el día" to { Seleccion.abrir(nav, fecha) },
        ))
    if (escribiendo) io.github.ruddy2003.vestigium.ui.EscribirDia(fecha) { escribiendo = false }
}
