@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.elegirFecha
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ruddy2003.vestigium.ui.ArchivoIdeas
import io.github.ruddy2003.vestigium.ui.EscritorTema
import io.github.ruddy2003.vestigium.ui.Escrito
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.FormaFicha
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.archivoIdeas
import io.github.ruddy2003.vestigium.ui.margenSuperior
import io.github.ruddy2003.vestigium.ui.paginaFicha

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

// =====================================================================
//  Escribir o corregir una idea
// =====================================================================

/**
 * Una idea a pantalla completa, como el diario: una ficha grande del tema ([Escrito.IDEA]) sobre el interior de su contenedor.
 * Arriba, la pestaña del tema y la fecha y «la idea en una línea»; luego el texto, que es lo único obligatorio y se guarda solo
 * mientras escribes (en cuanto tiene texto). Abajo, en «el reverso de la ficha», los temas, de dónde viene, quién lo dice, la
 * fuente y cómo la tienes. Si «se me ocurrió», no se piden quién ni fuente.
 */
@Composable
fun PantallaIdea(nav: Nav) {
    Dialog(onDismissRequest = nav.atras, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) { EscritorIdea(nav) }
}

/** Lo de adentro de [PantallaIdea]: la ficha grande para escribir (aparte, para poder verla en las capturas de diseño). */
@Composable
fun EscritorIdea(nav: Nav) {
    val inicial = remember { EdicionIdea.idea ?: EdicionIdea.nueva() }
    val contexto = remember { EdicionIdea.contexto }
    val yaGuardada = remember { Repo.ideas.any { it.id == inicial.id } }
    var x by remember { mutableStateOf(Repo.ideas.firstOrNull { it.id == inicial.id } ?: inicial) }
    var tocada by remember { mutableStateOf(false) }
    fun cambiar(n: Idea) {
        x = n; tocada = true
        if (n.texto.isNotBlank()) Repo.guardarIdea(n.copy(editada = if (yaGuardada) Fechas.ahoraIso() else n.editada))
    }
    val a = archivoIdeas(Estilo.tema)
    EscritorTema(x.fecha, x.texto, { cambiar(x.copy(texto = it)) }, nav.atras, modo = Escrito.IDEA, textoBoton = "listo",
        pista = "¿Qué entendiste? Con tus palabras; @persona si te lo contó alguien de tu gente.",
        encabezado = { p -> EncabezadoIdea(x, contexto, a, p, yaGuardada) { cambiar(it) } },
        pie = { _ -> ReversoIdea(x, tocada, nav) { cambiar(it) } })
}

/** Arriba de la ficha: el tema (o qué es: una tablilla nueva, un tanzaku…), la fecha que se cambia al tocarla, la idea en una línea y de dónde nace. */
@Composable
private fun EncabezadoIdea(x: Idea, contexto: String?, a: ArchivoIdeas, p: Pagina, yaGuardada: Boolean, cambiar: (Idea) -> Unit) {
    val ctx = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val tema = Ideario.temasDe(x).firstOrNull()
            Text((if (x.fijada) "◆ " else "") + (tema?.let { "#" + it.replace('_', ' ') } ?: if (yaGuardada) a.ficha else "${a.ficha} nueva"),
                style = MaterialTheme.typography.labelLarge, color = p.acento, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(Fechas.corto(x.fecha) + " · " + Fechas.horaDe(x.creada), style = MaterialTheme.typography.labelMedium, color = p.tenue,
                modifier = Modifier.clickable { elegirFecha(ctx, x.fecha) { cambiar(x.copy(fecha = it)) } }.padding(vertical = 4.dp))
        }
        val estilo = TextStyle(fontFamily = p.texto, fontSize = (p.tam + 1).sp, lineHeight = p.interlinea.sp, color = p.tinta, fontWeight = FontWeight.SemiBold)
        BasicTextField(x.titulo, { cambiar(x.copy(titulo = it.replace('\n', ' ').take(140))) }, singleLine = true, textStyle = estilo, cursorBrush = SolidColor(p.acento),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
            decorationBox = { campo -> Box { if (x.titulo.isEmpty()) Text("La idea en una línea (opcional)", style = estilo.copy(color = p.tenue.copy(alpha = 0.6f), fontWeight = FontWeight.Normal)); campo() } })
        contexto?.let { c -> Text(c, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, color = p.tenue, modifier = Modifier.padding(bottom = 6.dp)) }
    }
}

/** El reverso de la ficha: temas, de dónde viene, quién lo dice, la fuente, cómo la tienes, fijarla y borrarla. */
@Composable
private fun ReversoIdea(x: Idea, tocada: Boolean, nav: Nav, cambiar: (Idea) -> Unit) {
    var confirmar by remember { mutableStateOf(false) }
    if (x.texto.isBlank() && tocada) Ayuda("Sin texto todavía no se guarda.", Modifier.padding(top = 6.dp))
    Text("El reverso de la ficha", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 22.dp))
    // Los temas: cada uno es un contenedor del archivo. Los #temas escritos en el texto también cuentan (se ven marcados).
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
    Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Ayuda("Del ${Fechas.titulo(x.fecha).lowercase()} · anotada a las ${Fechas.horaDe(x.creada)}", Modifier.weight(1f))
        Ficha(if (x.fijada) "◆ fijada" else "fijar", x.fijada) { cambiar(x.copy(fijada = !x.fijada)) }
    }
    if (x.revisiones.isNotEmpty()) Ayuda("La volviste a ver ${x.revisiones.size} ${if (x.revisiones.size == 1) "vez" else "veces"}: " +
        x.revisiones.takeLast(3).joinToString(" · ") { r -> Fechas.corto(r.fecha) + " " + (Catalogo.RESPUESTAS_IDEA.firstOrNull { it.id == r.respuesta }?.nombre?.lowercase() ?: r.respuesta) },
        Modifier.padding(top = 6.dp))
    if (Repo.ideas.any { it.id == x.id }) TextButton(onClick = {
        if (!confirmar) { confirmar = true; return@TextButton }
        val antes = Repo.ideas.firstOrNull { it.id == x.id } ?: x
        Repo.borrarIdea(x.id); nav.atras(); Deshacer.ofrecer("Idea borrada") { Repo.guardarIdea(antes) }
    }, modifier = Modifier.padding(top = 8.dp)) {
        Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
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

/**
 * Una idea sola, a lo ancho: la misma ficha del tema que en el archivo ([FichaMini]), con alto para [lineas] renglones.
 * Se usa donde sale una idea suelta (en una obra, en la hoja de la noche, en «Una idea de antes»).
 */
@Composable
fun TarjetaIdea(i: Idea, modifier: Modifier = Modifier, lineas: Int = 4, onClick: () -> Unit) {
    val t = Estilo.tema
    val a = archivoIdeas(t); val p = paginaFicha(t)
    val renglon = with(LocalDensity.current) { (p.interlinea * 0.66f).sp.toDp() }
    // 25 dp: el encabezado (16), el aire bajo la línea de color (7) y un respiro.
    val alto = margenSuperior(a.formaFicha) + 25.dp + renglon * (lineas + if (i.titulo.isNotBlank()) 1 else 0) + 12.dp
    FichaMini(i, modifier.fillMaxWidth().padding(top = 8.dp).height(alto), lineas = lineas + if (i.titulo.isNotBlank()) 1 else 0, onClick = onClick)
}

/** Varias ideas en fichas chicas, de dos en dos (de tres en tres si las fichas del tema son tiras). Para usar dentro de una hoja que ya se desplaza. */
@Composable
fun FichasEnFilas(ideas: List<Idea>, abrir: (Idea) -> Unit) {
    val tira = archivoIdeas(Estilo.tema).formaFicha == FormaFicha.TIRA
    val cols = if (tira) 3 else 2
    Column(Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ideas.chunked(cols).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                fila.forEach { i -> FichaMini(i, Modifier.weight(1f).aspectRatio(if (tira) 0.5f else 1.2f), lineas = if (tira) 9 else 4) { abrir(i) } }
                repeat(cols - fila.size) { Spacer(Modifier.weight(1f)) }
            }
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
    archivoIdeas(Estilo.tema).let { a -> Ayuda("Un${if (a.fem) "a" else ""} ${a.contenedor} por tema. Toca uno para sacar sus ${a.fichas}.", Modifier.padding(bottom = 8.dp)) }
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
        FichasEnFilas(fijadas) { abrir(it) }
    }
    val porMes = visibles.filter { !it.fijada }.groupBy { it.fecha.take(7) }.toSortedMap(compareByDescending { it })
    porMes.entries.take(meses).forEach { (mes, l) ->
        Text(rotulo(mes), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 20.dp, bottom = 2.dp))
        FichasEnFilas(l.sortedByDescending { it.creada }) { abrir(it) }
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
