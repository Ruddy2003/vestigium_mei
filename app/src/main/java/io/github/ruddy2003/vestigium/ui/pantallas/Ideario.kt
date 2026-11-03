@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Totales
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Cita
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Idea
import io.github.ruddy2003.vestigium.data.Ideario
import io.github.ruddy2003.vestigium.data.Obra
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.RevisionIdea
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Deshacer
import io.github.ruddy2003.vestigium.ui.Editor
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.elegirFecha

/** La idea que se abre en la pantalla «idea»: nueva (todavía sin guardar) o una ya guardada. */
object EdicionIdea {
    var idea by mutableStateOf<Idea?>(null)
    /** Lo que se muestra arriba en solo lectura mientras escribes: la cita de la que nace, o un recordatorio. */
    var contexto by mutableStateOf<String?>(null)

    fun abrir(nav: Nav, i: Idea, contexto: String? = null) { idea = i; this.contexto = contexto; nav.ir("idea") }

    /** Una idea vacía para hoy (o para [fecha]), ligada a una obra y a una de sus citas si se dan. */
    fun nueva(fecha: String = Fechas.diaLogico(), obra: Obra? = null, cita: Cita? = null): Idea = Idea(
        id = Repo.nuevoId(), creada = Fechas.ahoraIso(), fecha = fecha, texto = "", obra_id = obra?.id, cita_id = cita?.id,
        donde = cita?.pagina.orEmpty(), quien = listOfNotNull(obra?.autor?.trim()?.ifBlank { null }),
        origen = when (obra?.tipo) { "libro", "articulo", "comic" -> "leido"; "pelicula", "serie", "juego", "documental", "podcast", "audiolibro", "album" -> "visto"; else -> "" })
}

private fun nombreOrigen(id: String) = Catalogo.ORIGENES_IDEA.firstOrNull { it.id == id }?.nombre
private fun nombrePostura(id: String) = Catalogo.POSTURAS_IDEA.firstOrNull { it.id == id }?.nombre

@Composable
private fun colorPostura(id: String): Color? {
    val cs = MaterialTheme.colorScheme
    return when (id) { "convence" -> cs.primary; "piensa" -> cs.tertiary; "duda" -> cs.secondary; "desacuerdo" -> cs.error; else -> null }
}

/** De dónde salió, en una línea: quién lo dice, la obra o la fuente, y el minuto o la página. */
private fun lineaFuente(i: Idea): String {
    val obra = i.obra_id?.let { o -> Repo.obras.firstOrNull { it.id == o }?.titulo }
    return listOfNotNull(i.quien.joinToString(", ").ifBlank { null }, obra ?: i.fuente.ifBlank { null }, i.donde.ifBlank { null }).joinToString(" · ")
}

// =====================================================================
//  Escribir o corregir una idea
// =====================================================================

/**
 * Una idea a pantalla completa. Solo el texto es obligatorio; se guarda sola mientras escribes (en cuanto tiene texto).
 * Si «se me ocurrió», no se piden quién ni fuente.
 */
@Composable
fun PantallaIdea(nav: Nav) {
    val ctx = LocalContext.current
    val inicial = remember { EdicionIdea.idea ?: EdicionIdea.nueva() }
    val contexto = remember { EdicionIdea.contexto }
    val yaGuardada = remember { Repo.ideas.any { it.id == inicial.id } }
    var x by remember { mutableStateOf(Repo.ideas.firstOrNull { it.id == inicial.id } ?: inicial) }
    var tocada by remember { mutableStateOf(false) }
    fun cambiar(n: Idea) {
        x = n; tocada = true
        if (n.texto.isNotBlank()) Repo.guardarIdea(n.copy(editada = if (yaGuardada) Fechas.ahoraIso() else n.editada))
    }
    var confirmar by remember { mutableStateOf(false) }
    Hoja(if (yaGuardada) "Una idea" else "Nueva idea", nav.atras, "Lo que entendiste, con tus palabras. Se guarda sola mientras escribes; solo el texto es obligatorio.") {
        contexto?.let { c ->
            Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text(c, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, modifier = Modifier.padding(12.dp))
            }
        }
        Etiqueta("La idea")
        Editor(x.texto, "idea.txt", "¿Qué entendiste? Con tus palabras; @persona si te lo contó alguien de tu gente.", minimo = 160,
            pie = "${Texto.palabras(x.texto)} verba") { cambiar(x.copy(texto = it)) }
        if (x.texto.isBlank() && tocada) Ayuda("Sin texto todavía no se guarda.", Modifier.padding(top = 6.dp))
        // Los temas: cada uno es un cajón del fichero. Los #temas escritos en el texto también cuentan (se ven marcados).
        Etiqueta("Temas")
        val enTexto = Texto.temas(x.texto).toSet()
        ListaEtiquetas(x.temas, "un tema: estoicismo, dinero, salud…", normalizar = { Texto.etiqueta(it) }) { l -> cambiar(x.copy(temas = l.distinct())) }
        val temas = remember(Repo.ideas) { Ideario.temas(Repo.ideas).take(18) }
        if (temas.isNotEmpty()) FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            temas.forEach { (t, _) ->
                val puesto = t in x.temas || t in enTexto
                Ficha(t.replace('_', ' '), puesto) { if (t in x.temas) cambiar(x.copy(temas = x.temas - t)) else if (t !in enTexto) cambiar(x.copy(temas = x.temas + t)) }
            }
        }
        if (enTexto.any { it !in x.temas }) Ayuda("También cuentan los #temas del texto: " + enTexto.filter { it !in x.temas }.joinToString(", ") { "#$it" } + ".", Modifier.padding(top = 4.dp))
        Etiqueta("De dónde viene")
        FilaFichas(Catalogo.ORIGENES_IDEA, x.origen.ifBlank { null }) { cambiar(x.copy(origen = it.orEmpty())) }
        if (x.origen != "propia") {
            Etiqueta("Quién lo dice")
            ListaEtiquetas(x.quien, "autor, invitado, quien te lo contó…", normalizar = { it.trim() }) { cambiar(x.copy(quien = it.distinct())) }
            val voces = remember(Repo.ideas, Repo.obras) { Ideario.voces(Repo.ideas, Repo.obras) }
            val faltan = voces.filter { v -> x.quien.none { it.equals(v, ignoreCase = true) } }.take(5)
            if (faltan.isNotEmpty()) FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                faltan.forEach { v -> Ficha("+ $v", false) { cambiar(x.copy(quien = x.quien + v)) } }
            }
            Seccion("Fuente", "Una de tus obras, o lo que sea: un video, una plática, un sitio.", plegable = true,
                plegadaPorDefecto = x.obra_id == null && x.fuente.isBlank() && x.enlace.isBlank()) {
                FuenteIdea(x) { cambiar(it) }
            }
        }
        Seccion("Cómo la tengo", "No tiene que ser verdad para guardarla: también caben las que dudas o rechazas.", plegable = true, plegadaPorDefecto = x.postura.isBlank()) {
            FilaFichas(Catalogo.POSTURAS_IDEA, x.postura.ifBlank { null }) { cambiar(x.copy(postura = it.orEmpty())) }
        }
        Seccion("La idea en una línea", "Opcional: para leer la lista sin abrir cada una.", plegable = true, plegadaPorDefecto = x.titulo.isBlank()) {
            OutlinedTextField(x.titulo, { cambiar(x.copy(titulo = it.take(140))) }, Modifier.fillMaxWidth(), singleLine = true)
        }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Ayuda("Del ${Fechas.titulo(x.fecha).lowercase()} · anotada a las ${Fechas.horaDe(x.creada)}", Modifier.weight(1f))
            Text("cambiar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { elegirFecha(ctx, x.fecha) { cambiar(x.copy(fecha = it)) } }.padding(6.dp))
            Ficha(if (x.fijada) "◆ fijada" else "fijar", x.fijada) { cambiar(x.copy(fijada = !x.fijada)) }
        }
        if (x.revisiones.isNotEmpty()) Ayuda("La volviste a ver ${x.revisiones.size} ${if (x.revisiones.size == 1) "vez" else "veces"}: " +
            x.revisiones.takeLast(3).joinToString(" · ") { r -> Fechas.corto(r.fecha) + " " + (Catalogo.RESPUESTAS_IDEA.firstOrNull { it.id == r.respuesta }?.nombre?.lowercase() ?: r.respuesta) },
            Modifier.padding(top = 6.dp))
        BotonPrincipal("Listo", Modifier.fillMaxWidth().padding(top = 14.dp)) { nav.atras() }
        if (Repo.ideas.any { it.id == x.id }) TextButton(onClick = {
            if (!confirmar) { confirmar = true; return@TextButton }
            val antes = Repo.ideas.firstOrNull { it.id == x.id } ?: x
            Repo.borrarIdea(x.id); nav.atras(); Deshacer.ofrecer("Idea borrada") { Repo.guardarIdea(antes) }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** La fuente: elegir una de tus obras (se liga y su autor se sugiere) o escribirla, con enlace y minuto o página. */
@Composable
private fun FuenteIdea(x: Idea, cambiar: (Idea) -> Unit) {
    val obra = x.obra_id?.let { o -> Repo.obras.firstOrNull { it.id == o } }
    var buscando by remember { mutableStateOf(false) }
    var q by remember { mutableStateOf("") }
    Tarjeta {
        if (obra != null) Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${TIPOS_OBRA.firstOrNull { it.id == obra.tipo }?.nombre ?: "Obra"} · ${obra.titulo}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = { cambiar(x.copy(obra_id = null, cita_id = null)) }) { Text("quitar", style = MaterialTheme.typography.labelMedium) }
        } else {
            Ficha(if (buscando) "elegir de tus obras ▾" else "elegir de tus obras…", buscando) { buscando = !buscando }
            if (buscando) {
                OutlinedTextField(q, { q = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, placeholder = { Text("título o autor") })
                val nq = Totales.normal(q.trim())
                val l = Repo.obras.filter { nq.isBlank() || Totales.normal(it.titulo + " " + it.autor).contains(nq) }
                    .sortedByDescending { it.dias.maxOrNull() ?: it.fin ?: it.inicio ?: it.agregado }.take(8)
                if (Repo.obras.isEmpty()) Ayuda("Todavía no tienes obras. Escribe la fuente abajo.", Modifier.padding(top = 6.dp))
                l.forEach { o ->
                    Text(o.titulo + (o.autor.ifBlank { null }?.let { " · $it" } ?: ""), style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth().clickable {
                            val quien = if (o.autor.isNotBlank() && x.quien.none { it.equals(o.autor.trim(), true) }) x.quien + o.autor.trim() else x.quien
                            cambiar(x.copy(obra_id = o.id, quien = quien)); buscando = false; q = ""
                        }.padding(vertical = 8.dp))
                }
            }
            Etiqueta("O escríbela")
            OutlinedTextField(x.fuente, { cambiar(x.copy(fuente = it.take(200))) }, Modifier.fillMaxWidth(), singleLine = true,
                placeholder = { Text("video, podcast, plática, sitio…") })
            Etiqueta("Enlace")
            OutlinedTextField(x.enlace, { cambiar(x.copy(enlace = it.trim())) }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("https://…") })
        }
        Etiqueta("Minuto o página")
        OutlinedTextField(x.donde, { cambiar(x.copy(donde = it.take(20))) }, Modifier.width(160.dp), singleLine = true)
    }
}

// =====================================================================
//  Cómo se ve una idea
// =====================================================================

/** Una idea en la lista: origen y fecha, la idea en una línea, el texto recortado y de dónde salió. */
@Composable
fun TarjetaIdea(i: Idea, modifier: Modifier = Modifier, lineas: Int = 4, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(shape = RoundedCornerShape(6.dp), color = if (i.fijada) cs.primaryContainer else cs.surface, border = BorderStroke(1.dp, cs.outline),
        modifier = modifier.fillMaxWidth().padding(top = 8.dp).clickable { onClick() }) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text((if (i.fijada) "◆ " else "") + listOfNotNull(nombreOrigen(i.origen), Fechas.corto(i.fecha)).joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall, color = cs.primary, modifier = Modifier.weight(1f))
                colorPostura(i.postura)?.let { c -> Text("● " + (nombrePostura(i.postura)?.lowercase() ?: ""), style = MaterialTheme.typography.labelSmall, color = c) }
            }
            if (i.titulo.isNotBlank()) Text(i.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
            TextoMarcado(i.texto, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodyMedium, maxLines = lineas, overflow = TextOverflow.Ellipsis)
            Ideario.temasDe(i).takeIf { it.isNotEmpty() }?.let { t -> Text(t.joinToString("  ") { "#" + it.replace('_', ' ') }, style = MaterialTheme.typography.labelSmall, color = cs.primary, modifier = Modifier.padding(top = 4.dp), maxLines = 1, overflow = TextOverflow.Ellipsis) }
            lineaFuente(i).ifBlank { null }?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp), maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }
    }
}

// =====================================================================
//  La libreta
// =====================================================================

/**
 * El Ideario dentro de las bibliotecas: arriba el fichero (un cajón por tema, [FicheroIdeas]) y debajo todas las ideas por mes,
 * con buscador y filtros. [vistaInicial] se conserva por compatibilidad: con «temas», la lista de abajo se oculta y queda solo el fichero.
 */
@Composable
fun BibliotecaIdeas(nav: Nav, rotulo: (String) -> String, vistaInicial: String = "mes") {
    var busca by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf<String?>(null) }
    var origen by remember { mutableStateOf<String?>(null) }
    var meses by remember { mutableIntStateOf(6) }
    val todas = Repo.ideas
    BotonPrincipal("＋ Anotar una idea", Modifier.fillMaxWidth().padding(top = 12.dp)) { EdicionIdea.abrir(nav, EdicionIdea.nueva()) }
    if (todas.isEmpty()) {
        Vacio("Todavía no hay ideas. Anota lo que aprendiste de un libro, un video o una plática, o algo que se te ocurrió, y ponle un tema: cada tema es un cajón del fichero. También puedes compartir un video o una página a Vestigium.")
        return
    }
    Text("El fichero", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 16.dp, bottom = 6.dp))
    Ayuda("Un cajón por tema. Toca uno para sacar sus fichas.", Modifier.padding(bottom = 8.dp))
    FicheroIdeas(nav, todas)
    if (vistaInicial == "temas") return
    Text("Todas, por mes", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 20.dp))
    OutlinedTextField(busca, { busca = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true,
        placeholder = { Text("buscar: idea, tema, autor, fuente…", style = MaterialTheme.typography.bodyMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
    FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Catalogo.ORIGENES_IDEA.filter { o -> todas.any { it.origen == o.id } }.forEach { o -> Ficha(o.nombre.lowercase(), origen == o.id) { origen = if (origen == o.id) null else o.id } }
        todas.map { it.fecha.take(4) }.distinct().sortedDescending().takeIf { it.size > 1 }?.forEach { a -> Ficha(a, anio == a) { anio = if (anio == a) null else a } }
    }
    val q = Totales.normal(busca.trim())
    val visibles = todas.filter { i -> (anio == null || i.fecha.startsWith(anio!!)) && (origen == null || i.origen == origen) &&
        (q.isBlank() || Totales.normal(Ideario.textoBuscable(i, Repo.obras)).contains(q)) }
    fun abrir(i: Idea) = EdicionIdea.abrir(nav, i)
    if (visibles.isEmpty()) { Vacio("Nada coincide."); return }
    val fijadas = visibles.filter { it.fijada }
    if (fijadas.isNotEmpty()) {
        Text("Fijadas", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 16.dp))
        fijadas.forEach { i -> TarjetaIdea(i) { abrir(i) } }
    }
    val porMes = visibles.filter { !it.fijada }.groupBy { it.fecha.take(7) }.toSortedMap(compareByDescending { it })
    porMes.entries.take(meses).forEach { (mes, l) ->
        Text(rotulo(mes), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 20.dp, bottom = 2.dp))
        l.sortedByDescending { it.creada }.forEach { i -> TarjetaIdea(i) { abrir(i) } }
    }
    if (porMes.size > meses) BotonSecundario("ver más meses", Modifier.padding(top = 14.dp)) { meses += 6 }
    Row(Modifier.fillMaxWidth().padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Ayuda("«Una idea de antes» en la pantalla del día", Modifier.weight(1f))
        Ficha(if (Repo.config.ideas_resurgir) "encendido" else "apagado", Repo.config.ideas_resurgir) { Repo.cambiarConfig { it.copy(ideas_resurgir = !it.ideas_resurgir) } }
    }
}

// =====================================================================
//  En otras pantallas
// =====================================================================

/** En una obra: las ideas que salieron de ella y el botón para anotar otra. */
@Composable
fun IdeasDeObra(o: Obra, nav: Nav) {
    val l = Repo.ideas.filter { it.obra_id == o.id }.sortedByDescending { it.creada }
    l.forEach { i -> TarjetaIdea(i, lineas = 3) { EdicionIdea.abrir(nav, i) } }
    if (l.isEmpty()) Ayuda("Lo que entendiste o te hizo pensar, con tus palabras. Sale también en tu Ideario.")
    BotonSecundario("＋ anotar una idea de esta obra", Modifier.fillMaxWidth().padding(top = 8.dp)) { EdicionIdea.abrir(nav, EdicionIdea.nueva(obra = o)) }
}

/** En la hoja de la noche: las ideas de ese día y el botón para anotar una. No guarda nada si no se usa. */
@Composable
fun IdeasDelDia(fecha: String, nav: Nav, antes: () -> Unit) {
    Repo.ideas.filter { it.fecha == fecha }.forEach { i -> TarjetaIdea(i, lineas = 2) { antes(); EdicionIdea.abrir(nav, i) } }
    BotonSecundario("＋ anotar lo que aprendí o pensé", Modifier.fillMaxWidth().padding(top = 8.dp)) { antes(); EdicionIdea.abrir(nav, EdicionIdea.nueva(fecha)) }
}

/**
 * «Una idea de antes» (solo hoy): una idea de hace un mes, seis meses o un año (ver [Ideario.resurgir]) para volver a leerla.
 * La respuesta queda en la idea; «ya no» ofrece cambiar cómo la tienes. Una por día.
 */
@Composable
fun UnaIdeaDeAntes(hoy: String, nav: Nav) {
    if (!Repo.config.ideas_resurgir) return
    val respondida = Ideario.respondidaHoy(Repo.ideas, hoy)
    if (respondida != null) {
        if (respondida.revisiones.lastOrNull { it.fecha == hoy }?.respuesta != "cambio") return
        Seccion("Una idea de antes", "Cambiaste de opinión. ¿Cómo la tienes ahora?") {
            Tarjeta {
                TarjetaIdea(respondida, lineas = 3) { EdicionIdea.abrir(nav, respondida) }
                FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.POSTURAS_IDEA.forEach { p -> Ficha(p.nombre, respondida.postura == p.id) { Repo.guardarIdea(respondida.copy(postura = p.id)) } }
                }
            }
        }
        return
    }
    val i = remember(hoy, Repo.ideas) { Ideario.resurgir(Repo.ideas, hoy) } ?: return
    Seccion("Una idea de antes", "La anotaste ${Ideario.haceCuanto(i.fecha, hoy)}, el ${Fechas.corto(i.fecha)}. ¿Sigues pensando igual?") {
        Tarjeta {
            TarjetaIdea(i, lineas = 5) { EdicionIdea.abrir(nav, i) }
            FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Catalogo.RESPUESTAS_IDEA.forEach { r -> Ficha(r.nombre, false) { Repo.guardarIdea(i.copy(revisiones = i.revisiones + RevisionIdea(hoy, r.id))) } }
            }
            Text("no mostrar esto", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { Repo.cambiarConfig { it.copy(ideas_resurgir = false) } }.padding(top = 8.dp))
        }
    }
}
