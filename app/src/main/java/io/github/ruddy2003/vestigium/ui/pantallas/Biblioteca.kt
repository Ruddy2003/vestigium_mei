@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Cita
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.FraseGuardada
import io.github.ruddy2003.vestigium.data.Obra
import io.github.ruddy2003.vestigium.data.Opcion
import io.github.ruddy2003.vestigium.data.Portadas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Editor
import io.github.ruddy2003.vestigium.ui.Escala
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.grecaEscalonada
import io.github.ruddy2003.vestigium.ui.numeralMaya
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import io.github.ruddy2003.vestigium.ui.elegirFecha

object EdicionObra { var id by mutableStateOf<String?>(null) }

/** Qué se busca en el buscador de obras y, si viene de una obra ya creada, solo se le cambia la portada. */
object BusquedaObra {
    var tipo by mutableStateOf("libro")
    var texto by mutableStateOf("")
    var portadaDe by mutableStateOf<String?>(null)
}

val TIPOS_OBRA = listOf(Opcion("libro", "Libro"), Opcion("pelicula", "Película"), Opcion("serie", "Serie"), Opcion("juego", "Juego"), Opcion("articulo", "Artículo"),
    Opcion("documental", "Documental"), Opcion("podcast", "Podcast"), Opcion("audiolibro", "Audiolibro"), Opcion("comic", "Cómic"), Opcion("album", "Álbum"))
private val ESTADOS = listOf(Opcion("quiero", "Pendiente"), Opcion("en_curso", "En curso"), Opcion("pausado", "Pausado"), Opcion("terminado", "Terminado"), Opcion("abandonado", "Abandonado"))

private fun autorDe(tipo: String) = when (tipo) { "libro", "audiolibro", "comic" -> "Autor"; "pelicula", "documental" -> "Dirección"; "serie" -> "Creador o canal"; "articulo" -> "Autores"
    "podcast" -> "Conductores"; "album" -> "Artista"; else -> "Estudio" }
private fun unidad(tipo: String) = when (tipo) { "libro", "articulo", "comic" -> "páginas"; "pelicula", "documental" -> "minutos"; "serie" -> "capítulos"; "podcast" -> "episodios"
    "album" -> "canciones"; else -> "horas" }
private fun verbo(tipo: String) = when (tipo) { "libro", "articulo" -> "Leyendo ahora"; "juego" -> "Jugando ahora"; else -> "Viendo ahora" }
private fun plural(tipo: String) = when (tipo) { "libro" -> "libros"; "pelicula" -> "películas"; "serie" -> "series"; "juego" -> "juegos"; "documental" -> "documentales"
    "podcast" -> "podcasts"; "audiolibro" -> "audiolibros"; "comic" -> "cómics"; "album" -> "álbumes"; else -> "artículos" }
private fun hoyHice(tipo: String) = when (tipo) { "libro", "articulo" -> "Hoy leí esto"; "juego" -> "Hoy jugué esto"; else -> "Hoy vi esto" }

// =====================================================================
//  Portadas
// =====================================================================

/** Miniaturas de los resultados de búsqueda en memoria, para no bajarlas dos veces. */
private object Miniaturas { val cache = android.util.LruCache<String, Bitmap>(80) }

@Composable
private fun ImagenRed(url: String?, modifier: Modifier) {
    val bmp by produceState<Bitmap?>(url?.let { Miniaturas.cache.get(it) }, url) {
        if (url != null && value == null) value = withContext(Dispatchers.IO) { Portadas.miniatura(url) }?.also { Miniaturas.cache.put(url, it) }
    }
    Box(modifier.clip(RoundedCornerShape(3.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
        bmp?.let { Image(it.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
    }
}

/**
 * La portada de una obra: la imagen guardada en el teléfono o, si no hay, una tapa dibujada con los colores, la letra y el adorno del tema
 * (los artículos siempre llevan esta, como una ficha de biblioteca). Si la imagen se perdió pero se sabe de dónde vino, se vuelve a bajar.
 */
@Composable
fun PortadaObra(o: Obra, modifier: Modifier = Modifier, lado: Int = 360) {
    val ctx = LocalContext.current
    val archivo = Portadas.archivo(ctx, o.portada)
    val bmp by produceState<Bitmap?>(null, o.portada, archivo?.length()) {
        value = archivo?.let { f -> withContext(Dispatchers.IO) { Portadas.leer(f, lado) } }
    }
    LaunchedEffect(o.id, o.portada) {
        if (archivo == null && o.portada_url != null) {
            val nuevo = withContext(Dispatchers.IO) { Portadas.bajar(ctx, o.portada_url, o.id) }
            if (nuevo != null) Repo.obras.firstOrNull { it.id == o.id }?.let { Repo.guardarObra(it.copy(portada = nuevo)) }
        }
    }
    val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(Estilo.tema)
    val oscuro = io.github.ruddy2003.vestigium.ui.esOscuro()
    val medidor = androidx.compose.ui.text.rememberTextMeasurer(cacheSize = 8)
    Box(modifier.clip(RoundedCornerShape(4.dp))) {
        val b = bmp
        if (b != null) Image(b.asImageBitmap(), o.titulo, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize()) {
            // Sin imagen, la tapa la dibuja el tema; en una miniatura de lista, solo la inicial.
            val e = io.github.ruddy2003.vestigium.ui.biblioteca.EstadoPortada(
                tipo = (TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre ?: "").uppercase(), titulo = o.titulo.ifBlank { "—" },
                autor = o.revista.ifBlank { null } ?: o.autor.substringBefore(",").substringBefore(";").trim(), anio = o.anio?.toString() ?: "",
                mini = maxWidth < 64.dp, semilla = (o.id + o.titulo).hashCode(),
            )
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize().semantics { contentDescription = o.titulo }) { with(d) { portadaObra(e, medidor, oscuro) } }
        }
    }
}

@Composable
fun PantallaBiblioteca(nav: Nav) {
    var filtro by remember { mutableStateOf<String?>(null) }
    val todas = Repo.obras.filter { filtro == null || it.tipo == filtro }
    val anio = Fechas.diaLogico().take(4)
    // Lo que se abrió para añadir y se dejó del todo vacío no se queda. Una obra sin título pero con algo más (autor,
    // calificación, nota, citas, fechas, portada…) se conserva: antes se borraba sin poder deshacerlo.
    LaunchedEffect(Unit) {
        Repo.obras.filter { o ->
            o.titulo.isBlank() && o.autor.isBlank() && o.anio == null && o.inicio == null && o.fin == null && o.calificacion == null &&
                o.progreso == null && o.etiquetas.isEmpty() && o.nota.isBlank() && o.portada == null && o.portada_url == null && o.revista.isBlank() &&
                o.doi.isBlank() && o.dias.isEmpty() && o.citas.isEmpty() && o.recomendo.isEmpty() && o.donde.isBlank()
        }.forEach { Repo.borrarObra(it.id) }
    }

    // «Lista y pendientes», para no confundirla con la galería «Obras» (N-23).
    Hoja("Lista y pendientes", nav.atras, "Libros, películas, series, juegos y artículos: qué tienes pendiente, qué empezaste, qué terminaste, qué te dejó y qué frases te quedaron.", latin = "INDEX OPERVM") {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todo", filtro == null) { filtro = null }
            TIPOS_OBRA.forEach { t -> Ficha(t.nombre.lowercase(), filtro == t.id) { filtro = if (filtro == t.id) null else t.id } }
        }
        BotonPrincipal("Buscar y añadir", Modifier.fillMaxWidth().padding(top = 12.dp)) {
            BusquedaObra.tipo = filtro ?: "libro"; BusquedaObra.texto = ""; BusquedaObra.portadaDe = null; nav.ir("buscar_obra")
        }

        val terminadasAnio = Repo.obras.filter { it.estado == "terminado" && it.fin?.startsWith(anio) == true }
        Row(Modifier.padding(top = 14.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TIPOS_OBRA.forEach { t -> Cifra("${terminadasAnio.count { it.tipo == t.id }}", "${plural(t.id)} $anio", Modifier.width(96.dp)) }
        }

        val enCurso = todas.filter { it.estado == "en_curso" }.sortedByDescending { it.dias.maxOrNull() ?: it.inicio ?: it.agregado }
        if (enCurso.isNotEmpty()) Seccion(filtro?.let { verbo(it) } ?: "En curso", latin = "IN MANIBVS") { Estante(enCurso, nav) }
        // Lo pendiente va antes que lo terminado: lo de «pronto» primero, y la lista entera en «Quiero leer y ver».
        val pendientes = ordenDeseados(todas.filter { it.estado == "quiero" })
        Seccion("Pendientes", if (pendientes.isEmpty()) "Lo que quieres leer, ver o jugar." else "${pendientes.size} en tu lista de deseados") {
            if (pendientes.isEmpty()) Vacio("Nada pendiente. Añade lo que quieres leer o ver, aunque sea solo el título.")
            else Lista(pendientes.take(5), nav)
            BotonSecundario("toda la lista de deseados →", Modifier.fillMaxWidth().padding(top = 8.dp)) { nav.ir("deseados") }
        }
        val pausadas = todas.filter { it.estado == "pausado" }.sortedByDescending { it.dias.maxOrNull() ?: it.inicio ?: it.agregado }
        if (pausadas.isNotEmpty()) Seccion("Pausados") { Lista(pausadas, nav) }
        val terminadas = todas.filter { it.estado == "terminado" }.sortedByDescending { it.fin ?: it.agregado }
        Seccion("Terminados") { if (terminadas.isEmpty()) Vacio("Nada terminado todavía.") else Lista(terminadas, nav, porAnio = true) }
        val abandonadas = todas.filter { it.estado == "abandonado" }
        if (abandonadas.isNotEmpty()) Seccion("Archivados", latin = "RELICTA") { Lista(abandonadas, nav) }
        if ((filtro == null || filtro == "libro") && Repo.subrayados.isNotEmpty()) Seccion("Subrayados de Kindle", "Toca un libro para ver lo que marcaste; con ♥ la frase pasa a tus frases guardadas.", latin = "EXCERPTA") {
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
        Seccion("Buscadores", "Libros, series y artículos se buscan sin nada más. Para películas hace falta una clave gratuita de TMDB y para juegos una de RAWG.",
            latin = "INDICES", plegable = true, plegadaPorDefecto = Portadas.claveTmdb(LocalContext.current).isNotBlank()) { Claves() }
        Ayuda(Portadas.CREDITOS, Modifier.padding(top = 18.dp))
    }
}

/** Las claves de TMDB y RAWG: se guardan solo en este teléfono (fuera de respaldos y exportaciones). */
@Composable
private fun Claves() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var tmdb by remember { mutableStateOf(Portadas.claveTmdb(ctx)) }
    var rawg by remember { mutableStateOf(Portadas.claveRawg(ctx)) }
    var estado by remember { mutableStateOf<String?>(null) }
    Tarjeta {
        Etiqueta("TMDB · películas (y series en español)")
        OutlinedTextField(tmdb, { tmdb = it.trim() }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("API Key") },
            visualTransformation = PasswordVisualTransformation())
        Etiqueta("RAWG · videojuegos")
        OutlinedTextField(rawg, { rawg = it.trim() }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("API key") },
            visualTransformation = PasswordVisualTransformation())
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonPrincipal("Guardar y probar") {
                Portadas.guardarClaves(ctx, tmdb, rawg)
                estado = "Probando…"
                scope.launch {
                    val r = withContext(Dispatchers.IO) {
                        listOfNotNull(if (tmdb.isNotBlank()) "TMDB: " + (Portadas.probar(ctx, "tmdb") ?: "funciona ✓") else null,
                            if (rawg.isNotBlank()) "RAWG: " + (Portadas.probar(ctx, "rawg") ?: "funciona ✓") else null)
                    }
                    estado = r.joinToString("\n").ifBlank { "Guardé las claves vacías: películas y juegos irán con foto." }
                }
            }
        }
        estado?.let { Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp)) }
        Ayuda("TMDB: themoviedb.org → tu perfil → Settings → API → Developer; copia la «API Key». RAWG: rawg.io/apidocs → Get API key. Las dos son gratis. Las claves no salen de este teléfono salvo para buscar en esos servicios.", Modifier.padding(top = 8.dp))
    }
}

/** Lo que estás leyendo o viendo, como portadas de pie. */
@Composable
private fun Estante(obras: List<Obra>, nav: Nav) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        obras.forEach { o ->
            Column(Modifier.width(104.dp).clickable { EdicionObra.id = o.id; nav.ir("obra") }) {
                PortadaObra(o, Modifier.fillMaxWidth().aspectRatio(2f / 3f))
                if (o.progreso != null && o.total != null && o.total > 0) {
                    Box(Modifier.padding(top = 5.dp).fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(Modifier.fillMaxWidth((o.progreso.toFloat() / o.total).coerceIn(0f, 1f)).height(4.dp).background(MaterialTheme.colorScheme.primary))
                    }
                }
                Text(o.titulo, style = MaterialTheme.typography.labelMedium, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                if (o.dias.isNotEmpty()) Text("${o.dias.size} día${if (o.dias.size == 1) "" else "s"} · ${Fechas.corto(o.dias.max())}", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
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
                Text(Estilo.numeral(anio.toIntOrNull() ?: 0).let { if (it == anio) anio else "$it · $anio" }, style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = if (i == 0) 0.dp else 12.dp, bottom = 4.dp))
            } else if (i > 0) HorizontalDivider()
            Row(Modifier.fillMaxWidth().clickable { EdicionObra.id = o.id; nav.ir("obra") }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                PortadaObra(o, Modifier.width(40.dp).aspectRatio(2f / 3f), lado = 160)
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(o.titulo.ifBlank { "(sin título)" }, style = MaterialTheme.typography.bodyLarge)
                    val detalle = listOfNotNull(
                        TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre?.lowercase(), o.autor.ifBlank { null }, o.anio?.toString(),
                        if (o.estado == "en_curso" && o.progreso != null && o.total != null && o.total > 0) "${o.progreso * 100 / o.total}%" else null,
                        o.fin?.let { Fechas.corto(it) }, if (o.citas.isNotEmpty()) "${o.citas.size} cita${if (o.citas.size == 1) "" else "s"}" else null,
                    ).joinToString(" · ")
                    Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                o.calificacion?.let { Calificacion(it, tam = 12.dp) }
            }
        }
    }
}

// =====================================================================
//  Buscar una obra (o solo su portada)
// =====================================================================

@Composable
fun PantallaBuscarObra(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val sentir = rememberSentir()
    val soloPortada = BusquedaObra.portadaDe
    var resultados by remember { mutableStateOf<List<Portadas.Resultado>>(emptyList()) }
    var buscando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var eligiendo by remember { mutableStateOf<String?>(null) }
    val tipo = BusquedaObra.tipo

    fun buscar() {
        val q = BusquedaObra.texto
        if (q.isBlank()) return
        buscando = true; mensaje = null
        scope.launch {
            val r = withContext(Dispatchers.IO) { runCatching { Portadas.buscar(ctx, tipo, q) } }
            buscando = false
            resultados = r.getOrDefault(emptyList())
            mensaje = when {
                r.isFailure -> "No pude buscar: revisa tu conexión." + if (r.exceptionOrNull()?.message?.contains("401") == true) " La clave no es válida." else ""
                resultados.isEmpty() -> "No encontré nada con «$q». Prueba con otras palabras o añádelo a mano."
                else -> null
            }
        }
    }
    LaunchedEffect(Unit) { if (BusquedaObra.texto.isNotBlank()) buscar() }

    fun elegir(r: Portadas.Resultado) {
        eligiendo = r.fuente
        scope.launch {
            val d = withContext(Dispatchers.IO) { Portadas.detalles(ctx, r) }
            val id = soloPortada ?: Repo.nuevoId()
            val archivo = d.portada?.let { url -> withContext(Dispatchers.IO) { Portadas.bajar(ctx, url, id) } }
            val previa = Repo.obras.firstOrNull { it.id == soloPortada }
            if (previa != null) {
                Repo.guardarObra(previa.copy(portada = archivo ?: previa.portada, portada_url = d.portada ?: previa.portada_url, fuente = d.fuente,
                    autor = previa.autor.ifBlank { d.autor }, anio = previa.anio ?: d.anio, total = previa.total ?: d.total,
                    revista = previa.revista.ifBlank { d.revista }, doi = previa.doi.ifBlank { d.doi }))
            } else {
                Repo.guardarObra(Obra(id = id, tipo = tipo, titulo = d.titulo, autor = d.autor, anio = d.anio, total = d.total, agregado = Fechas.diaLogico(),
                    portada = archivo, portada_url = d.portada, fuente = d.fuente, revista = d.revista, doi = d.doi))
            }
            sentir(Sentidos.Evento.GUARDAR)
            eligiendo = null
            EdicionObra.id = id; BusquedaObra.portadaDe = null
            if (previa != null) nav.atras() else { nav.atras(); nav.ir("obra") }
        }
    }

    Hoja(if (soloPortada != null) "Buscar portada" else "Buscar obra", nav.atras, "Escribe el título (o el DOI de un artículo) y elige el correcto: se llenan solos los datos y la portada.", latin = "QVAERERE") {
        if (soloPortada == null) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TIPOS_OBRA.forEach { t -> Ficha(t.nombre.lowercase(), tipo == t.id) { BusquedaObra.tipo = t.id; resultados = emptyList(); mensaje = null } }
        }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(BusquedaObra.texto, { BusquedaObra.texto = it }, Modifier.weight(1f), singleLine = true,
                placeholder = { Text(when (tipo) { "articulo" -> "título o DOI"; "pelicula" -> "título de la película"; "serie" -> "título de la serie"; "juego" -> "título del juego"; else -> "título o autor" }) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { buscar() }))
            Spacer(Modifier.width(8.dp))
            BotonSecundario("buscar") { buscar() }
        }
        if (tipo in setOf("documental", "podcast", "audiolibro", "comic", "album")) Ayuda("Este tipo no tiene buscador: añádelo a mano y ponle una foto.", Modifier.padding(top = 8.dp))
        else if (!Portadas.disponible(ctx, tipo)) Ayuda(if (tipo == "pelicula") "Para buscar películas pon tu clave de TMDB en Obras → Buscadores. Mientras, añádela a mano y ponle una foto."
            else "Para buscar juegos pon tu clave de RAWG en Obras → Buscadores. Mientras, añádelo a mano y ponle una foto.", Modifier.padding(top = 8.dp))
        if (buscando) Text("Buscando…", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp))
        mensaje?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp)) }
        if (resultados.isNotEmpty()) Box(Modifier.padding(top = 12.dp)) {
            Tarjeta {
                resultados.forEachIndexed { i, r ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().clickable(enabled = eligiendo == null) { elegir(r) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (tipo == "articulo") PortadaObra(Obra("", "articulo", r.titulo, r.autor, r.anio, revista = r.revista), Modifier.width(48.dp).aspectRatio(2f / 3f))
                        else ImagenRed(r.portada, Modifier.width(48.dp).aspectRatio(2f / 3f))
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(r.titulo, style = MaterialTheme.typography.bodyLarge, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            Text(listOfNotNull(r.autor.ifBlank { null }, r.revista.ifBlank { null }, r.anio?.toString(), r.detalle.ifBlank { null }).joinToString(" · "),
                                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        if (eligiendo == r.fuente) Text("…", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        if (soloPortada == null) TextButton(onClick = {
            val o = Obra(id = Repo.nuevoId(), tipo = tipo, titulo = BusquedaObra.texto.trim(), agregado = Fechas.diaLogico())
            Repo.guardarObra(o); EdicionObra.id = o.id; nav.atras(); nav.ir("obra")
        }, modifier = Modifier.padding(top = 10.dp)) { Text("No está: añadir a mano", style = MaterialTheme.typography.labelLarge) }
        Ayuda(Portadas.CREDITOS, Modifier.padding(top = 16.dp))
    }
}

// =====================================================================
//  Una obra
// =====================================================================

@Composable
fun PantallaObra(nav: Nav) {
    val ctx = LocalContext.current
    val o = Repo.obras.firstOrNull { it.id == EdicionObra.id }
    if (o == null) { Hoja("Obras", nav.atras) { Vacio("No encontré esa obra.") }; return }
    fun g(n: Obra) = Repo.guardarObra(n)
    var confirmar by remember(o.id) { mutableStateOf(false) }
    val hoy = Fechas.diaLogico()
    val sentir = rememberSentir()
    val foto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) Portadas.desdeFoto(ctx, uri, o.id)?.let { n -> Repo.obras.firstOrNull { it.id == o.id }?.let { g(it.copy(portada = n, portada_url = null)) } }
    }
    fun fecha(actual: String?, poner: (String?) -> Unit) {
        elegirFecha(ctx, actual ?: hoy) { poner(it) }
    }

    Hoja(TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre ?: "Obra", nav.atras, latin = "OPVS", subtitulo = "Se guarda sola mientras escribes.") {
        // Portada y lo esencial, lado a lado.
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.Top) {
            PortadaObra(o, Modifier.width(118.dp).aspectRatio(2f / 3f))
            Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text(o.titulo.ifBlank { "(sin título)" }, style = MaterialTheme.typography.titleLarge)
                Text(listOfNotNull(o.autor.ifBlank { null }, o.revista.ifBlank { null }, o.anio?.toString()).joinToString(" · "), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                val marcadoHoy = hoy in o.dias
                Box(Modifier.padding(top = 10.dp)) {
                    Ficha(hoyHice(o.tipo).lowercase(), marcadoHoy) {
                        g(o.copy(dias = if (marcadoHoy) o.dias - hoy else (o.dias + hoy).sorted(),
                            estado = if (!marcadoHoy && o.estado == "quiero") "en_curso" else o.estado, inicio = o.inicio ?: if (!marcadoHoy) hoy else null))
                    }
                }
                if (o.dias.isNotEmpty()) Text("${o.dias.size} día${if (o.dias.size == 1) "" else "s"} marcados · último ${Fechas.relativo(o.dias.max())}",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (o.tipo != "articulo") TextButton(onClick = { BusquedaObra.tipo = o.tipo; BusquedaObra.texto = o.titulo; BusquedaObra.portadaDe = o.id; nav.ir("buscar_obra") }) {
                        Text("buscar portada", style = MaterialTheme.typography.labelMedium) }
                    TextButton(onClick = { foto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("foto", style = MaterialTheme.typography.labelMedium) }
                    if (o.portada != null) TextButton(onClick = { Portadas.borrar(ctx, o.portada); g(o.copy(portada = null, portada_url = null)) }) {
                        Text("quitar portada", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
        Seccion("Qué es", numero = 1, latin = "QVID") {
            Tarjeta {
                FilaFichas(TIPOS_OBRA, o.tipo) { g(o.copy(tipo = it ?: o.tipo)) }
                Etiqueta("Título")
                OutlinedTextField(o.titulo, { g(o.copy(titulo = it)) }, Modifier.fillMaxWidth(), singleLine = true)
                Etiqueta(autorDe(o.tipo))
                OutlinedTextField(o.autor, { g(o.copy(autor = it)) }, Modifier.fillMaxWidth(), singleLine = true)
                if (o.tipo == "articulo") {
                    Etiqueta("Revista")
                    OutlinedTextField(o.revista, { g(o.copy(revista = it)) }, Modifier.fillMaxWidth(), singleLine = true)
                    Etiqueta("DOI")
                    OutlinedTextField(o.doi, { g(o.copy(doi = it.trim())) }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("10.xxxx/…") })
                }
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
                if (o.estado == "quiero") {
                    Etiqueta("Qué tan pronto la quieres")
                    FilaFichas(io.github.ruddy2003.vestigium.data.Catalogo.PRIORIDADES_DESEO, o.prioridad.ifBlank { null }) { g(o.copy(prioridad = it.orEmpty())) }
                    Etiqueta("Dónde verla o conseguirla")
                    OutlinedTextField(o.donde, { g(o.copy(donde = it.take(80))) }, Modifier.fillMaxWidth(), singleLine = true,
                        placeholder = { Text("plataforma, biblioteca, quien te la presta…") })
                }
                Etiqueta("Te la recomendó")
                ListaEtiquetas(o.recomendo, "@persona de tu gente", prefijo = "@", normalizar = { Texto.etiqueta(it.trim().removePrefix("@")) }) { g(o.copy(recomendo = it)) }
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
                    Text("Veces que la leíste o viste:", style = MaterialTheme.typography.labelLarge)
                    BotonSecundario("−") { g(o.copy(veces = (o.veces - 1).coerceAtLeast(1))) }
                    Text("${o.veces}", style = MaterialTheme.typography.titleLarge)
                    BotonSecundario("+") { g(o.copy(veces = o.veces + 1)) }
                }
            }
        }
        Seccion("Citas", "Las frases que te quedaron. Con ♥ aparecen en tus frases guardadas, junto a las del día.", numero = 3, latin = "SENTENTIAE") {
            Citas(o, sentir) { c -> EdicionIdea.abrir(nav, EdicionIdea.nueva(obra = o, cita = c), contexto = "«${c.texto}»") }
        }
        Seccion("Ideas", "Lo que entendiste o te hizo pensar, con tus palabras. Se guarda en tu Ideario.", numero = 4) {
            Tarjeta { IdeasDeObra(o, nav) }
        }
        Seccion("Qué te dejó", numero = 5, latin = "IVDICIVM") {
            Tarjeta {
                Etiqueta("Calificación")
                Calificacion(o.calificacion, Modifier.padding(bottom = 10.dp), tam = 24.dp)
                Escala(o.calificacion, 1, 10, mapOf(1 to "pésimo", 4 to "flojo", 6 to "bueno", 8 to "excelente", 10 to "de mis favoritos")) { g(o.copy(calificacion = it)) }
                Etiqueta("Etiquetas")
                ListaEtiquetas(o.etiquetas, "ciencia_ficcion, ensayo, recomendado_por_ana", normalizar = { Texto.slug(it) }) { g(o.copy(etiquetas = it)) }
                Etiqueta("Notas")
                Editor(o.nota, "notae.txt", "Ideas, qué sentiste, qué te recordó…", minimo = 120) { g(o.copy(nota = it)) }
            }
        }
        val marcas = Repo.subrayados.filter { o.titulo.isNotBlank() && (it.libro.equals(o.titulo, true) || it.libro.contains(o.titulo, true) || o.titulo.contains(it.libro, true)) }
        if (marcas.isNotEmpty()) Seccion("Subrayados de Kindle", numero = 6, latin = "EXCERPTA") { Tarjeta { Subrayados(marcas) } }
        TextButton(onClick = { if (confirmar) { Repo.borrarObra(o.id); nav.atras(); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Obra borrada", alExpirar = { Portadas.borrar(ctx, o.portada) }) { Repo.guardarObra(o) } } else confirmar = true }, modifier = Modifier.padding(top = 16.dp)) {
            Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Las citas de una obra: escribir una nueva, darle o quitarle ♥, borrarla, o anotar lo que entendiste de ella en el Ideario. */
@Composable
private fun Citas(o: Obra, sentir: (Sentidos.Evento) -> Unit, anotar: (Cita) -> Unit) {
    var texto by remember(o.id) { mutableStateOf("") }
    var pagina by remember(o.id) { mutableStateOf("") }
    var armado by remember { mutableStateOf<String?>(null) }
    var editando by remember { mutableStateOf<Cita?>(null) }
    Tarjeta {
        o.citas.forEachIndexed { i, c ->
            if (i > 0) HorizontalDivider(Modifier.padding(vertical = 6.dp))
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    // Tocar la cita la corrige (texto y página o minuto).
                    Text("«${c.texto}»", style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, modifier = Modifier.clickable { editando = c })
                    Text(listOfNotNull(c.pagina.ifBlank { null }?.let { if (it.all { ch -> ch.isDigit() }) "p. $it" else it }, c.fecha.ifBlank { null }?.let { Fechas.corto(it) }).joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val n = Repo.ideas.count { it.cita_id == c.id }
                    Text(if (n > 0) "✎ $n ${if (n == 1) "idea" else "ideas"} · anotar otra" else "✎ anotar lo que entendí", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { anotar(c) }.padding(top = 4.dp))
                }
                TextButton(onClick = {
                    sentir(Sentidos.Evento.ELEGIR)
                    Repo.guardarObra(o.copy(citas = o.citas.map { if (it.id == c.id) it.copy(favorita = !it.favorita) else it }))
                }) { Text(if (c.favorita) "♥" else "♡", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge) }
                TextButton(onClick = { if (armado == c.id) {
                    Repo.guardarObra(o.copy(citas = o.citas.filterNot { it.id == c.id })); armado = null
                    io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Cita borrada") { Repo.obras.firstOrNull { it.id == o.id }?.let { x -> if (x.citas.none { it.id == c.id }) Repo.guardarObra(x.copy(citas = x.citas + c)) } }
                } else armado = c.id }) {
                    Text(if (armado == c.id) "¿borrar?" else "×", color = if (armado == c.id) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (o.citas.isNotEmpty()) Spacer(Modifier.height(10.dp))
        OutlinedTextField(texto, { texto = it }, Modifier.fillMaxWidth(), placeholder = { Text("Copia aquí la frase") }, minLines = 2)
        Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(pagina, { pagina = it.take(20) }, Modifier.width(130.dp), singleLine = true, placeholder = { Text(if (o.tipo in setOf("pelicula", "serie", "documental", "podcast", "audiolibro", "album")) "minuto" else "página") })
            Spacer(Modifier.width(10.dp))
            BotonPrincipal("Guardar cita", habilitado = texto.isNotBlank()) {
                val c = Cita(Repo.nuevoId(), texto.trim().removePrefix("«").removeSuffix("»").removePrefix("\"").removeSuffix("\"").trim(), pagina.trim(), Fechas.diaLogico())
                Repo.guardarObra(o.copy(citas = o.citas + c)); texto = ""; pagina = ""
            }
        }
    }
    editando?.let { c ->
        var t by remember(c.id) { mutableStateOf(c.texto) }
        var p by remember(c.id) { mutableStateOf(c.pagina) }
        io.github.ruddy2003.vestigium.ui.Dialogo(onDismissRequest = { editando = null }, title = { Text("Corregir la cita") },
            text = {
                Column {
                    OutlinedTextField(t, { t = it }, Modifier.fillMaxWidth(), minLines = 3)
                    OutlinedTextField(p, { p = it.take(20) }, Modifier.width(160.dp).padding(top = 8.dp), singleLine = true, label = { Text("página o minuto") })
                }
            },
            confirmButton = { TextButton(enabled = t.isNotBlank(), onClick = {
                Repo.obras.firstOrNull { it.id == o.id }?.let { x -> Repo.guardarObra(x.copy(citas = x.citas.map { if (it.id == c.id) it.copy(texto = t.trim(), pagina = p.trim()) else it })) }
                editando = null
            }) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { editando = null }) { Text("cancelar") } })
    }
}

@Composable
private fun Subrayados(l: List<io.github.ruddy2003.vestigium.data.Subrayado>) {
    val sentir = rememberSentir()
    Column(Modifier.padding(bottom = 8.dp)) {
        l.sortedBy { it.ubicacion.substringBefore('-').toIntOrNull() ?: it.pagina ?: 0 }.take(80).forEach { s ->
            val autor = listOfNotNull(s.autor.ifBlank { null }, s.libro).joinToString(", ") + (s.pagina?.let { ", p. $it" } ?: "")
            val guardada = Repo.config.frases_guardadas.any { it.texto == s.texto && it.autor == autor }
            Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.padding(top = 3.dp).width(3.dp).height(16.dp).background(if (s.tipo == "nota") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary))
                Column(Modifier.weight(1f).padding(start = 8.dp)) {
                    Text(s.texto, style = MaterialTheme.typography.bodyMedium)
                    Text(listOfNotNull(s.tipo, s.pagina?.let { "p. $it" }, s.ubicacion.ifBlank { null }?.let { "pos. $it" }, s.fecha?.let { Fechas.corto(it) }).joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (s.tipo != "nota" && s.texto.isNotBlank()) TextButton(onClick = {
                    sentir(Sentidos.Evento.ELEGIR)
                    Repo.cambiarConfig { c ->
                        c.copy(frases_guardadas = if (guardada) c.frases_guardadas.filterNot { it.texto == s.texto && it.autor == autor }
                            else c.frases_guardadas + FraseGuardada(s.texto, null, autor, "kindle", Fechas.diaLogico()))
                    }
                }) { Text(if (guardada) "♥" else "♡", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium) }
            }
        }
        if (l.size > 80) Ayuda("…y ${l.size - 80} más en vm_subrayados.csv.")
    }
}

/** Para la hoja de la noche: lo que estás leyendo o viendo, para marcar en un toque que hoy le dedicaste tiempo. */
@Composable
fun ObrasDeHoy(fecha: String, nav: Nav) {
    val candidatas = Repo.obras.filter { it.titulo.isNotBlank() && (it.estado == "en_curso" || fecha in it.dias) }
        .sortedByDescending { (if (fecha in it.dias) "1" else "0") + (it.dias.maxOrNull() ?: it.inicio ?: it.agregado) }.take(12)
    Tarjeta {
        if (candidatas.isEmpty()) Text("No tienes nada en curso. Busca un libro, una película o una serie para marcarlo.", style = MaterialTheme.typography.bodyMedium)
        else Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            candidatas.forEach { o ->
                val marcado = fecha in o.dias
                Column(Modifier.width(78.dp).clickable {
                    Repo.guardarObra(o.copy(dias = if (marcado) o.dias - fecha else (o.dias + fecha).sorted()))
                }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Box {
                        PortadaObra(o, Modifier.width(78.dp).aspectRatio(2f / 3f).then(if (marcado) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)) else Modifier), lado = 220)
                        if (marcado) Text("✓", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp))
                    }
                    Text(o.titulo, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonSecundario("buscar otra…") { BusquedaObra.tipo = "libro"; BusquedaObra.texto = ""; BusquedaObra.portadaDe = null; nav.ir("buscar_obra") }
            BotonSecundario("ver obras") { nav.ir("biblioteca") }
        }
    }
}

// =====================================================================
//  Quiero leer y ver: la lista de deseados (esquema 18)
// =====================================================================

private fun rangoPrioridad(p: String) = when (p) { "pronto" -> 0; "cuando_pueda" -> 1; "algun_dia" -> 2; else -> 3 }

/** El orden de lo pendiente: primero lo de «pronto», luego «cuando pueda», «algún día» y sin decir; dentro de cada uno, lo más reciente. */
internal fun ordenDeseados(l: List<Obra>): List<Obra> = l.sortedWith(compareBy<Obra> { rangoPrioridad(it.prioridad) }.thenByDescending { it.agregado })

/**
 * La lista de deseados: lo que quieres leer, ver, escuchar o jugar (las obras «pendientes»). Se añade con solo el título (o
 * buscando la portada), se agrupa por qué tan pronto lo quieres y un toque en «empezar» lo pasa a «en curso».
 */
@Composable
fun PantallaDeseados(nav: Nav) {
    var filtro by remember { mutableStateOf<String?>(null) }
    var tipoNuevo by remember { mutableStateOf("libro") }
    var titulo by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("") }
    val sentir = rememberSentir()
    val hoy = Fechas.diaLogico()
    val pendientes = Repo.obras.filter { it.estado == "quiero" && it.titulo.isNotBlank() }
    val todas = pendientes.filter { filtro == null || it.tipo == filtro }
    fun anadir() {
        val t = titulo.trim(); if (t.isEmpty()) return
        Repo.guardarObra(Obra(id = Repo.nuevoId(), tipo = tipoNuevo, titulo = t, agregado = hoy, prioridad = prioridad))
        sentir(Sentidos.Evento.GUARDAR); titulo = ""
    }
    Hoja("Quiero leer y ver", nav.atras, "Lo que quieres leer, ver, escuchar o jugar. Basta el título; lo demás, cuando quieras.") {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todo · ${pendientes.size}", filtro == null) { filtro = null }
            TIPOS_OBRA.forEach { t -> val n = pendientes.count { it.tipo == t.id }; if (n > 0) Ficha("${plural(t.id)} · $n", filtro == t.id) { filtro = if (filtro == t.id) null else t.id } }
        }
        Seccion("Añadir", "Elige qué es, escribe el título y listo.") {
            Tarjeta {
                FilaFichas(TIPOS_OBRA, tipoNuevo) { tipoNuevo = it ?: tipoNuevo }
                OutlinedTextField(titulo, { titulo = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("título") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { anadir() }))
                Etiqueta("Qué tan pronto")
                FilaFichas(io.github.ruddy2003.vestigium.data.Catalogo.PRIORIDADES_DESEO, prioridad.ifBlank { null }) { prioridad = it.orEmpty() }
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonPrincipal("Añadir", Modifier.weight(1f), habilitado = titulo.isNotBlank()) { anadir() }
                    BotonSecundario("buscar con portada", Modifier.weight(1f)) {
                        BusquedaObra.tipo = tipoNuevo; BusquedaObra.texto = titulo.trim(); BusquedaObra.portadaDe = null; nav.ir("buscar_obra")
                    }
                }
            }
        }
        if (todas.isEmpty()) Vacio(if (pendientes.isEmpty()) "Tu lista está vacía. Lo que te recomienden o se te antoje, aquí, para no olvidarlo." else "Nada de este tipo.")
        val grupos = ordenDeseados(todas).groupBy { it.prioridad.takeIf { p -> io.github.ruddy2003.vestigium.data.Catalogo.nombrePrioridad(p) != null }.orEmpty() }
        listOf("pronto", "cuando_pueda", "algun_dia", "").forEach { p ->
            val l = grupos[p].orEmpty()
            if (l.isNotEmpty()) Seccion(io.github.ruddy2003.vestigium.data.Catalogo.nombrePrioridad(p) ?: "Sin decir cuándo", "${l.size}") {
                Tarjeta {
                    l.forEachIndexed { i, o ->
                        if (i > 0) HorizontalDivider()
                        FilaDeseo(o, hoy, nav)
                    }
                }
            }
        }
    }
}

/** Una obra deseada: su portada, qué es, quién te la recomendó, dónde verla, hace cuánto la agregaste y «empezar». */
@Composable
private fun FilaDeseo(o: Obra, hoy: String, nav: Nav) {
    Row(Modifier.fillMaxWidth().clickable { EdicionObra.id = o.id; nav.ir("obra") }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        PortadaObra(o, Modifier.width(40.dp).aspectRatio(2f / 3f), lado = 160)
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(o.titulo, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
            val detalle = listOfNotNull(
                TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre?.lowercase(), o.autor.ifBlank { null },
                o.recomendo.takeIf { it.isNotEmpty() }?.let { r -> "te la recomendó " + r.joinToString(", ") { "@$it" } },
                o.donde.ifBlank { null },
                o.agregado.takeIf { it.length >= 10 && it.take(10) != hoy }?.let { Fechas.relativo(it.take(10)) },
            ).joinToString(" · ")
            if (detalle.isNotBlank()) Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        TextButton(onClick = { Repo.guardarObra(o.copy(estado = "en_curso", inicio = o.inicio ?: hoy)) }) { Text("empezar", style = MaterialTheme.typography.labelLarge) }
    }
}
