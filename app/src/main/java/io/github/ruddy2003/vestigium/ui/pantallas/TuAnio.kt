@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ruddy2003.vestigium.ciencia.TuAnio
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.PrediccionAnio
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BorradorCarta
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.CapaFiesta
import io.github.ruddy2003.vestigium.ui.Emblema
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Fiesta
import io.github.ruddy2003.vestigium.ui.Griego
import io.github.ruddy2003.vestigium.ui.FondoVivo
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Musica
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Temas
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.fondoTema
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import io.github.ruddy2003.vestigium.ui.Menu
import io.github.ruddy2003.vestigium.ui.OpcionMenu

// =====================================================================
//  «Tu año»: historias a pantalla completa con la música, la voz y los colores del tema
// =====================================================================

private const val DURACION = 8500

/**
 * Cómo se presenta el año en cada tema: cada uno con su palabra (ninguna se repite entre temas) y su manera de contar.
 */
private fun voz(t: TemaId, anio: Int): String = when (t) {
    // «Res gestae»: las cosas hechas, como el balance que Augusto mandó grabar en bronce.
    TemaId.ROMANO -> "RES GESTAE · ANNO ${Romano.de(anio)}"
    TemaId.DISTOPIA -> "INFORME ANUAL DEL CIUDADANO · $anio"
    TemaId.CIBER -> "> cat year_$anio.log"
    TemaId.NEON -> "> cat year_$anio.log"
    TemaId.BOTANICO -> "Catálogo de especímenes · $anio"
    TemaId.RUINAS -> "Lo que quedó escrito tras el silencio · $anio"
    // «Apologismós»: la rendición de cuentas.
    TemaId.HELENO -> "ΑΠΟΛΟΓΙΣΜΟΣ · ΕΤΟΣ ${Griego.numeral(anio)}"
    // «Fechos»: los hechos, en la grafía de los cronistas castellanos.
    TemaId.MEDIEVAL -> "Libro de los fechos del año de ${Temas.copista(anio)}"
    TemaId.REFUGIO -> "REGISTRO DEL REFUGIO · AÑO $anio BAJO TIERRA"
    TemaId.ORDEN -> "PARTE DE CUSTODIA DE LA ORDEN · $anio"
    TemaId.LABORATORIO -> "Protocolo $anio · resultados del año"
    TemaId.COLONOS -> "Almanaque del año $anio"
    TemaId.NAPOLEON -> "BOLETÍN DE LA GRANDE ARMÉE · $anio"
    TemaId.NAVIDAD -> "Lo que trajo $anio"
    TemaId.SEDA -> "Bitácora de la caravana · ${Temas.anioChino(anio)}"
    TemaId.URSS -> "BALANCE DEL PLAN · $anio"
    TemaId.GOTICO -> "Annales · anno ${Romano.de(anio).lowercase()}"
    // Como en los observatorios: el año empieza en un día juliano.
    TemaId.ESPACIO -> "CICLO $anio · DESDE JD " + String.format(java.util.Locale.US, "%,d", Temas.diaJuliano(java.time.LocalDate.of(anio, 1, 1))).replace(',', ' ')
    // El año del sol cae sobre dos años de la hégira: el del 1 de enero y el del 31 de diciembre.
    TemaId.ANDALUS -> {
        val h1 = Temas.hegira(java.time.LocalDate.of(anio, 1, 1))?.third
        val h2 = Temas.hegira(java.time.LocalDate.of(anio, 12, 31))?.third
        "Diwán del año $anio" + (if (h1 != null && h2 != null) " · " + (if (h1 == h2) "$h1 H" else "$h1–$h2 H") else "")
    }
    TemaId.MONGOL -> Temas.anioMongol(java.time.LocalDate.of(anio, 6, 1)) + " · $anio"
    // «Taccuino»: la libreta de bolsillo en que Leonardo apuntaba todo.
    TemaId.RENACIMIENTO -> "Taccuino dell'anno $anio"
    TemaId.REVOLUCION -> "CAHIER DE L'ANNÉE $anio · ${Temas.anRepublicano(anio, 6).uppercase()}"
    TemaId.ANTIGUAS -> "LAS DOCE LUNAS DEL CAMINO · $anio"
}

/** Lo que una tarjeta necesita saber para dibujarse. */
private class Vista(val d: TuAnio.Datos, val exportar: Boolean, val animar: Boolean, val revelado: Boolean, val revelar: () -> Unit,
                    val nombre: (String) -> String, val abrirCarta: () -> Unit)

private class CartaAnio(val id: String, val titulo: String, val latin: String, val interactiva: Boolean = false, val dibujo: @Composable (Vista) -> Unit)

/** Nombre de cada tarjeta, para elegir cuáles salen al exportar. */
val TARJETAS_ANIO = listOf(
    "portada" to "Portada", "libros" to "Libros del diario", "cosas" to "El año en cosas", "movimiento" to "Movimiento y lugares", "sueno" to "Sueño", "animo" to "Ánimo", "gente" to "Gente", "cultura" to "Cultura",
    "musica" to "Música y YouTube", "cuerpo" to "Comida y gimnasio", "telefono" to "Teléfono", "dinero" to "Dinero", "comun" to "Lo más común",
    "curioso" to "Único y curioso", "comparar" to "Comparaciones", "medallas" to "Medallas", "predicciones" to "Predicciones", "arquetipo" to "Tu arquetipo",
    "palabra" to "La palabra del año", "cartel" to "Cartel final", "carta" to "La carta para el año que viene",
)

private fun n0(x: Double) = "%,.0f".format(x)
private fun n1(x: Double) = "%.1f".format(x)
/** El nombre del mes en la voz del tema (Ianuarius, março, ЯНВАРЬ…), el mismo que usa el resto de la app. */
private fun mesLargo(m: Int) = Estilo.mes(m)
private val DIAS = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábados", "domingos")

private fun iniciales(s: String) = s.removePrefix("@").split('_', ' ', '-').filter { it.isNotBlank() }.joinToString("") { it.take(1).uppercase() + "." }

// ---------------------------------------------------------------- piezas

@Composable
private fun Contador(valor: Double, v: Vista, estilo: TextStyle, color: Color = MaterialTheme.colorScheme.primary, formato: (Double) -> String = { n0(it) }) {
    val a = remember(valor) { Animatable(if (v.animar) 0f else 1f) }
    LaunchedEffect(valor, v.animar) { if (v.animar) { a.snapTo(0f); a.animateTo(1f, tween(1700, easing = FastOutSlowInEasing)) } else a.snapTo(1f) }
    Text(formato(valor * a.value), style = estilo, color = color, maxLines = 1)
}

@Composable
private fun Grande(valor: Double, etiqueta: String, v: Vista, formato: (Double) -> String = { n0(it) }) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Contador(valor, v, MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.SemiBold), formato = formato)
        Text(etiqueta, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun Renglon(k: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(k, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(110.dp))
        Text(valor, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}

/** Adivinar antes de ver: tres opciones; al tocar una se revela (y vibra distinto si acertaste). */
@Composable
private fun Adivina(v: Vista, pregunta: String, opciones: List<String>, correcta: String, revelado: @Composable () -> Unit) {
    val sentir = rememberSentir()
    var elegida by remember(pregunta) { mutableStateOf<String?>(null) }
    if (v.revelado || v.exportar) { revelado(); return }
    Column(Modifier.fillMaxWidth().padding(top = 18.dp)) {
        Text(pregunta, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Ayuda("Adivina antes de verlo.", Modifier.padding(top = 4.dp, bottom = 14.dp))
        opciones.forEach { o ->
            val esta = elegida == o
            Box(Modifier.fillMaxWidth().padding(vertical = 5.dp).border(1.5.dp, if (esta) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .background(if (esta) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent, MaterialTheme.shapes.medium)
                .clickable(enabled = elegida == null) {
                    elegida = o
                    sentir(if (o == correcta) Sentidos.Evento.LOGRO else Sentidos.Evento.ERROR)
                }.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(o, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        elegida?.let { e ->
            Text(if (e == correcta) "¡Exacto!" else "Casi: fue $correcta", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp))
            LaunchedEffect(e) { delay(1100); v.revelar() }
        }
    }
}

/**
 * Rascar para descubrir: se borra la capa con el dedo; al rascar lo suficiente se descubre todo. La capa es la misma
 * lámina del mapa para rascar (Mundo): el metal del tema y su motivo grabado.
 */
@Composable
private fun Rascar(v: Vista, contenido: @Composable () -> Unit) {
    val sentir = rememberSentir()
    val trazos = remember { mutableStateListOf<Offset>() }
    val tema = Estilo.tema
    val lam = laminaRascar(tema)
    val dens = LocalDensity.current.density
    val tesela = remember(tema, dens) { teselaLamina(tema, lam, (26 * dens).toInt()) }
    // El «rasca aquí» va grabado en el tono que contrasta con el cuerpo del metal.
    val letra = if (Color(lam.media).luminance() > 0.4f) Color(lam.oscura) else Color(lam.clara)
    Box(Modifier.fillMaxWidth().height(170.dp), contentAlignment = Alignment.Center) {
        contenido()
        if (!v.revelado && !v.exportar) Canvas(Modifier.fillMaxSize().graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen).pointerInput(Unit) {
            detectDragGestures { cambio, _ ->
                cambio.consume()
                trazos.add(cambio.position)
                if (trazos.size % 12 == 0) sentir(Sentidos.Evento.TOQUE)
                if (trazos.size > 70) v.revelar()
            }
        }) {
            pintarLaminaRascar(lam, tesela, 18.dp.toPx())
            trazos.forEach { drawCircle(Color.Transparent, 30.dp.toPx(), it, blendMode = BlendMode.Clear) }
        }
        if (!v.revelado && !v.exportar && trazos.isEmpty()) Text("rasca aquí", style = MaterialTheme.typography.titleMedium, color = letra)
    }
}

/** Barras de los doce meses, del color de tu ánimo. */
@Composable
private fun MesesAnimo(d: TuAnio.Datos) {
    val cs = MaterialTheme.colorScheme
    val etiquetas = etiquetasMesesAnio()
    Row(Modifier.fillMaxWidth().height(110.dp).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
        for (m in 1..12) {
            val val0 = d.p.valPorMes[m]
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                val h = ((val0 ?: 1.0) - 1) / 6
                Box(Modifier.fillMaxWidth().fillMaxHeight((h.toFloat() * 0.85f).coerceAtLeast(0.03f))
                    .background(if (val0 == null) cs.surfaceVariant else if (m == d.mesMejor) cs.primary else cs.primary.copy(alpha = 0.35f + 0.5f * h.toFloat()), MaterialTheme.shapes.extraSmall))
                Text(etiquetas[m - 1], style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, maxLines = 1, softWrap = false)
            }
        }
    }
}

// ---------------------------------------------------------------- las tarjetas

private fun cartas(d: TuAnio.Datos): List<CartaAnio> {
    val p = d.p
    val l = ArrayList<CartaAnio>()
    l += CartaAnio("portada", "Tu año", "ANNVS") { v ->
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Emblema(tam = 120.dp, animado = v.animar)
            Text("${d.anio}", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 10.dp))
        }
        Grande(p.diasDiario.toDouble(), "días escritos de ${p.n}", v)
        if (p.palabras > 0) {
            Grande(p.palabras.toDouble(), "palabras en el diario", v)
            Text("Un libro de ${n0(d.paginas.toDouble())} páginas. Leerlo en voz alta te tomaría ${n1(d.horasLeerlo)} horas.", style = MaterialTheme.typography.bodyLarge)
        }
    }
    // El año visual: el diario como pilas de libros y las cifras como objetos que se cuentan de un vistazo.
    if (p.palabras > 0) l += CartaAnio("libros", "Tu año en libros", "LIBRI") { _ ->
        Text("Lo que escribiste, mes por mes", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        LibrosDelAnio(d.anio)
    }
    if (d.suenoDias >= 1 || p.pasosTotal > 0 || p.ciudades.isNotEmpty() || p.entrenos > 0) l += CartaAnio("cosas", "Tu año en cosas", "RES") { _ ->
        Text("Tu año, contado en cosas", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        CosasDelAnio(d)
    }
    if (p.pasosTotal > 0 || p.ciudades.isNotEmpty()) l += CartaAnio("movimiento", "Te moviste", "MOTVS") { v ->
        if (p.pasosTotal > 0) {
            Grande(p.pasosTotal.toDouble(), "pasos · unos ${n0(p.km)} km", v)
            d.distancia.take(2).forEach { Text("= $it", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 2.dp)) }
        }
        if (p.ciudades.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Renglon("ciudades", "${p.ciudades.size}" + if (p.ciudadesNuevas.isNotEmpty()) " · ${p.ciudadesNuevas.size} nuevas" else "")
            if (p.paisesNuevos.isNotEmpty()) Renglon("países nuevos", p.paisesNuevos.joinToString(" ") { Mundo.bandera(it) })
            if (p.sitios.isNotEmpty()) Renglon("patrimonio", "${p.sitios.size}: " + p.sitios.take(2).joinToString(" · "))
            MapaRascar(d.paisesAnio, Modifier.fillMaxWidth().height(150.dp).padding(top = 8.dp))
        }
    }
    if (p.suenoTotalH > 0 || p.suenosRecordados > 0) l += CartaAnio("sueno", "Dormiste", "SOMNVS") { v ->
        if (p.suenoTotalH > 0) {
            Grande(d.suenoDias, "días enteros dormido", v) { n1(it) }
            Ayuda("${n0(p.suenoTotalH)} horas · ${p.suenoH?.let { n1(it) } ?: "—"} por noche")
            p.nocheLarga?.let { Renglon("la más larga", "${it.second / 60} h ${it.second % 60} min · ${Fechas.corto(it.first)}") }
            p.nocheCorta?.let { Renglon("la más corta", "${it.second / 60} h ${it.second % 60} min · ${Fechas.corto(it.first)}") }
        }
        if (p.suenosRecordados > 0) {
            Spacer(Modifier.height(8.dp))
            Grande(p.suenosRecordados.toDouble(), "sueños recordados", v)
            p.suenoVivido?.let { f -> Repo.dias[f]?.let { s ->
                Renglon("el más vívido", Fechas.titulo(f).lowercase() + (s.sueno_temas.takeIf { it.isNotEmpty() }?.let { " · " + it.take(3).joinToString(", ") { t -> t.replace('_', ' ') } } ?: ""))
            } }
        }
    }
    if (p.valMedia != null) l += CartaAnio("animo", "Tu ánimo", "ANIMVS", interactiva = d.mesesOrden.size >= 3) { v ->
        val opciones = if (d.mesesOrden.size >= 3) listOf(d.mesesOrden.first(), d.mesesOrden[d.mesesOrden.size / 2], d.mesesOrden.last()).distinct().map { mesLargo(it) }
            .sortedBy { it.hashCode() } else emptyList()
        val mejor = d.mesMejor?.let { mesLargo(it) } ?: "—"
        if (opciones.size >= 3) Adivina(v, "¿Cuál fue tu mejor mes?", opciones, mejor) { Animo(d, v) } else Animo(d, v)
    }
    if (p.personas.isNotEmpty()) l += CartaAnio("gente", "Tu gente", "HOMINES", interactiva = p.personas.size >= 3) { v ->
        val top = p.personas.take(3).map { v.nombre(it.first) }
        if (top.size >= 3) Adivina(v, "¿A quién mencionaste más?", top.sortedBy { it.hashCode() }, top.first()) { Gente(d, v) } else Gente(d, v)
    }
    if (p.obras.isNotEmpty() || p.frase != null) l += CartaAnio("cultura", "Lo que leíste y viste", "CVLTVRA") { v ->
        if (p.obras.isNotEmpty()) {
            Grande(p.obras.size.toDouble(), "obras terminadas", v)
            val tipos = p.obras.groupingBy { it.tipo }.eachCount()
            Ayuda(tipos.entries.joinToString(" · ") { (t, k) -> "$k " + (TIPOS_OBRA.firstOrNull { it.id == t }?.nombre?.lowercase() ?: t) + if (k > 1) "s" else "" })
            p.mejorObra?.let { Renglon("la mejor", "${it.titulo} · ${it.calificacion}/10") }
            p.generoTop?.let { Renglon("tu género", it.replace('_', ' ')) }
        }
        p.frase?.let { Text("«${it.texto}»", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal), modifier = Modifier.padding(top = 14.dp), maxLines = 5, overflow = TextOverflow.Ellipsis)
            Ayuda("— ${it.autor}") }
    }
    if (p.musicaH > 0.5 || p.ytVideos > 0) l += CartaAnio("musica", "Lo que escuchaste", "MVSICA", interactiva = p.artistas.size >= 3) { v ->
        val top = p.artistas.take(3).map { it.first }
        if (top.size >= 3) Adivina(v, "¿Quién fue tu artista del año?", top.sortedBy { it.hashCode() }, top.first()) { MusicaAnio(d, v) } else MusicaAnio(d, v)
    }
    if (p.tazasCafe >= 1 || p.entrenos > 0 || p.comidaTop.isNotEmpty()) l += CartaAnio("cuerpo", "Comida y fuerza", "CORPVS") { v ->
        if (p.tazasCafe >= 1) Grande(p.tazasCafe, "tazas de café (en cafeína)", v)
        p.comidaTop.firstOrNull()?.let { Renglon("lo que más comiste", "${it.first.lowercase()} · ${it.second} veces") }
        if (p.entrenos > 0) {
            Grande(d.elefantes, "elefantes africanos levantados", v) { n1(it) }
            Ayuda("${n1(p.volumenKg / 1000)} toneladas en ${p.entrenos} entrenos · un elefante pesa unas 6 t")
            p.pr?.let { Renglon("tu mejor marca", "${it.first} · ${n0(it.second)} kg (1RM estimado)") }
        }
    }
    if (p.pantallaTotalMin > 0) l += CartaAnio("telefono", "Tu pantalla", "TELEPHONVM") { v ->
        Grande(d.pantallaDias, "días enteros frente a la pantalla", v) { n1(it) }
        Ayuda("${n0(p.pantallaTotalMin / 60.0)} horas · ${p.pantallaMin?.let { "${(it / 60).toInt()} h ${(it % 60).toInt()} min" } ?: "—"} al día")
        if (p.desbloqueosTotal > 0) Renglon("desbloqueos", n0(p.desbloqueosTotal.toDouble()))
        if (p.notifs > 0) Renglon("notificaciones", n0(p.notifs.toDouble()))
        p.apps.firstOrNull()?.let { Renglon("tu app", "${it.first} · ${n0(it.second / 60.0)} h") }
    }
    if (p.gasto > 0) l += CartaAnio("dinero", "Tu dinero", "PECVNIA") { v ->
        Grande(p.gasto, "gastados", v) { "$" + n0(it) }
        Ayuda("$" + n0(p.gasto / p.n) + " al día")
        p.gastoCategorias.take(3).forEach { (c, m) -> Renglon(c.lowercase(), "$" + n0(m)) }
        if (p.ingresos > 0) Renglon("ingresos", "$" + n0(p.ingresos))
    }
    if (d.comun.size >= 3) l += CartaAnio("comun", "Lo más común", "VSITATVM") { v ->
        Text("Si tu año fuera un solo día:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 10.dp))
        d.comun.forEach { (k, x) -> Renglon(k, if (x.startsWith("@")) v.nombre(x) else x) }
    }
    if (d.unaVez.isNotEmpty() || d.raro != null || d.records.isNotEmpty()) l += CartaAnio("curioso", "Único y curioso", "MIRABILIA") { v ->
        if (d.unaVez.isNotEmpty()) {
            Text("Una sola vez:", style = MaterialTheme.typography.titleMedium)
            d.unaVez.take(5).forEach { Text("· " + it.replace(Regex("@(\\S+)")) { m -> v.nombre(m.groupValues[1]) }, style = MaterialTheme.typography.bodyLarge) }
        }
        d.raro?.let { (f, razones) -> Spacer(Modifier.height(10.dp)); Renglon("tu día más raro", Fechas.titulo(f).lowercase() + ": " + razones.joinToString("; ").lowercase()) }
        d.records.take(4).forEach { (k, x) -> Renglon(k, x) }
        d.relacion?.let { Renglon("descubriste", it) }
    }
    if (d.previo != null || d.vida != null || d.brujula.isNotEmpty()) l += CartaAnio("comparar", "Frente a otros años y otras vidas", "COMPARATIO") { v ->
        d.previo?.let { a ->
            Text("Frente a ${d.anio - 1}:", style = MaterialTheme.typography.titleMedium)
            fun cmp(k: String, x: Double?, y: Double?, u: String, dec: Boolean = true): Pair<String, String>? {
                if (x == null || y == null) return null
                val dif = x - y
                return k to (if (dif >= 0) "+" else "−") + (if (dec) n1(kotlin.math.abs(dif)) else n0(kotlin.math.abs(dif))) + " $u"
            }
            listOfNotNull(cmp("ánimo", p.valMedia, a.valMedia, "de 7"), cmp("sueño", p.suenoH, a.suenoH, "h"), cmp("pasos", p.pasos, a.pasos, "al día", false),
                cmp("pantalla", p.pantallaMin?.div(60), a.pantallaMin?.div(60), "h al día"), cmp("palabras", p.palabras.toDouble(), a.palabras.toDouble(), "", false))
                .forEach { (k, x) -> Renglon(k, x) }
        }
        d.vida?.let { vida ->
            Row(Modifier.padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                MedallaVida(vida, 64.dp, elegida = true)
                Column(Modifier.padding(start = 12.dp)) {
                    Text("Tu año se pareció al de", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(vida.nombre, style = MaterialTheme.typography.titleLarge)
                    Ayuda("${vida.lugar}, ${vida.cuando}")
                }
            }
        }
        if (d.brujula.isNotEmpty()) { Text("Tu brújula se movió:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp)); d.brujula.forEach { Ayuda(it) } }
    }
    if (d.medallas.isNotEmpty()) l += CartaAnio("medallas", "Tus medallas", "PALMAE") { v ->
        Grande(d.medallas.size.toDouble(), "logros este año", v)
        FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            d.medallas.take(12).forEach { m -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(78.dp)) {
                TrofeoLogro(m, 64.dp); Text(m.nombre, style = MaterialTheme.typography.labelSmall, maxLines = 2, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis)
            } }
        }
    }
    if (d.predicciones.isNotEmpty()) l += CartaAnio("predicciones", "Lo que predijiste en enero", "PRAEDICTIO") { v ->
        d.predicciones.forEach { (q, dijo, fue) ->
            Column(Modifier.padding(vertical = 6.dp)) {
                Text(q, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("dijiste ${if (dijo.startsWith("@")) v.nombre(dijo) else dijo} · fue ${if (fue.startsWith("@")) v.nombre(fue) else fue}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
    l += CartaAnio("arquetipo", "Tu arquetipo", "TYPVS") { v ->
        Column(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Emblema(tam = 140.dp, animado = v.animar)
            Text(d.arquetipo, style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 18.dp))
            Text("porque ${d.arquetipoPor}", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        }
    }
    if (d.palabra != null) l += CartaAnio("palabra", "La palabra de tu año", "VERBVM", interactiva = true) { v ->
        Text("La que más usaste este año y casi nunca antes:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
        Rascar(v) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(d.palabra, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                Ayuda("${d.palabraVeces} veces")
            }
        }
        if (d.palabrasCasi.isNotEmpty() && (v.revelado || v.exportar)) Ayuda("También: " + d.palabrasCasi.joinToString(", "), Modifier.padding(top = 12.dp))
    }
    l += CartaAnio("cartel", "Tu ${d.anio}", "SVMMA") { v ->
        val seis = listOfNotNull(
            "${p.diasDiario}" to "días escritos", p.pasos?.let { n0(it) to "pasos al día" }, (p.ciudades.size.takeIf { it > 0 })?.let { "$it" to "ciudades" },
            (p.obras.size.takeIf { it > 0 })?.let { "$it" to "obras" }, (p.musicaH.takeIf { it > 1 })?.let { n0(it) to "horas de música" },
            p.suenoH?.let { n1(it) + " h" to "de sueño por noche" }, p.valMedia?.let { n1(it) to "de ánimo medio" }, (p.fotos.takeIf { it > 0 })?.let { n0(it.toDouble()) to "fotos" },
        ).take(6)
        FlowRow(Modifier.fillMaxWidth().padding(top = 8.dp), maxItemsInEachRow = 2, horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            seis.forEach { (x, k) ->
                Column(Modifier.weight(1f).border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), MaterialTheme.shapes.medium).padding(14.dp)) {
                    Text(x, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                    Text(k, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        Text(d.arquetipo, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 18.dp))
        d.palabra?.let { Ayuda("la palabra del año: $it", Modifier.fillMaxWidth().padding(top = 4.dp)) }
    }
    l += CartaAnio("carta", "Una carta al ${d.anio + 1}", "EPISTVLA") { v ->
        Text("Escríbele a quien serás el 31 de diciembre de ${d.anio + 1}. Se sella hasta ese día y la abrirás al ver tu próximo año.", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(18.dp))
        if (TuAnio.cartaLista(d.anio)) Text("✓ Ya está sellada", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        else if (!v.exportar) BotonPrincipal("Escribir la carta", Modifier.fillMaxWidth()) { v.abrirCarta() }
        Spacer(Modifier.height(18.dp))
        Text("Gracias por otro año de páginas.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    return l
}

@Composable
private fun Animo(d: TuAnio.Datos, v: Vista) {
    val p = d.p
    Grande(p.valMedia ?: 0.0, "de 7, tu ánimo medio", v) { n1(it) }
    MesesAnimo(d)
    d.mesMejor?.let { Renglon("tu mejor mes", mesLargo(it) + (p.valPorMes[it]?.let { x -> " · ${n1(x)}" } ?: "")) }
    p.valPorSemana.maxByOrNull { it.value }?.let { Renglon("tu mejor día", "los " + DIAS[it.key - 1]) }
    p.mejorDia?.let { f -> Renglon("el día más feliz", Fechas.titulo(f).lowercase()) }
    p.mejorMomento?.let { Text("«${it.take(140)}»", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp)) }
}

@Composable
private fun Gente(d: TuAnio.Datos, v: Vista) {
    val p = d.p
    p.personas.take(5).forEachIndexed { i, (n, k) ->
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${i + 1}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(34.dp))
            Text(v.nombre(n), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("$k días", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    p.personaNueva?.let { Renglon("alguien nuevo", v.nombre(it)) }
    if (p.cartas > 0) Renglon("cartas", "${p.cartas} escritas" + if (d.dirigidas > 0) " · ${d.dirigidas} a otras personas" else "")
    if (p.waMios > 0) Renglon("WhatsApp", "${n0(p.waMios.toDouble())} mensajes tuyos")
}

@Composable
private fun MusicaAnio(d: TuAnio.Datos, v: Vista) {
    val p = d.p
    if (p.musicaH > 0.5) {
        Grande(p.musicaH, "horas de música", v)
        p.artistas.firstOrNull()?.let { Renglon("tu artista", "${it.first} · ${n0(it.second / 60)} h") }
        p.cancionTop?.let { Renglon("tu canción", "${it.first} (${it.second} veces)") }
        p.artistaNuevo?.let { Renglon("descubriste", it) }
    }
    if (p.ytVideos > 0) {
        Spacer(Modifier.height(10.dp))
        Renglon("YouTube", "${n0(p.ytVideos.toDouble())} videos" + if (p.ytMegusta > 0) " · ${p.ytMegusta} «me gusta»" else "")
        p.ytCanales.firstOrNull()?.let { Renglon("tu canal", it.first) }
        if (p.ytMadrugada > 0) Renglon("de madrugada", "${p.ytMadrugada} videos entre las 0 y las 5")
    }
}

/** El contenido de una tarjeta: título del tema, lo de la tarjeta y el pie (con la marca de año incompleto). */
@Composable
private fun CuerpoTarjeta(d: TuAnio.Datos, actual: CartaAnio, vista: Vista) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 22.dp)) {
        val (t, com) = Estilo.seccion(actual.titulo, actual.latin)
        Text(t, style = MaterialTheme.typography.headlineMedium, color = cs.onBackground)
        com?.let { Text(it, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant) }
        Spacer(Modifier.height(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) { androidx.compose.runtime.key(actual.id) { actual.dibujo(vista) } }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Vestigium Mei · ${d.anio}", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            if (!d.completo) Text("año incompleto · faltan ${d.faltan} días", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        }
    }
}

/** Una tarjeta suelta, quieta y ya revelada (para las capturas de diseño). */
@Composable
internal fun MuestraTarjetaAnio(d: TuAnio.Datos, id: String, revelada: Boolean = true) {
    val c = cartas(d).firstOrNull { it.id == id } ?: return
    val cs = MaterialTheme.colorScheme
    Box(Modifier.fillMaxWidth().aspectRatio(9f / 16f).background(cs.background).fondoTema(Estilo.tema, cs)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(cs.primary.copy(alpha = 0.16f), Color.Transparent, cs.primary.copy(alpha = 0.10f)))))
        CuerpoTarjeta(d, c, Vista(d, exportar = false, animar = false, revelado = revelada, revelar = {}, nombre = { "@" + it.removePrefix("@") }, abrirCarta = {}))
    }
}

/** Los ids de las tarjetas que tiene este año (para las capturas). */
internal fun idsTarjetasAnio(d: TuAnio.Datos): List<String> = cartas(d).map { it.id }

// ---------------------------------------------------------------- guardar imágenes

private fun guardarPng(ctx: Context, nombre: String, bmp: Bitmap): Uri? {
    val v = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/VestigiumMei/TuAnio")
    }
    val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v) ?: return null
    ctx.contentResolver.openOutputStream(uri)?.use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    return uri
}

/** Las historias a pantalla completa. */
@Composable
private fun HistoriasAnio(d: TuAnio.Datos, nav: Nav, cerrar: () -> Unit) {
    val ctx = LocalContext.current
    val tema = Estilo.tema
    val sentir = rememberSentir()
    val scope = rememberCoroutineScope()
    val todas = remember(d) { cartas(d) }
    val n = todas.size
    var idx by remember { mutableIntStateOf(0) }
    var pausado by remember { mutableStateOf(false) }
    // Pausa con botón (N-24): se queda hasta que la quites, no solo mientras mantienes el dedo.
    var pausaFija by remember { mutableStateOf(false) }
    var exportando by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<String?>(null) }
    var menu by remember { mutableStateOf(false) }
    val revelados = remember { mutableStateMapOf<String, Boolean>() }
    val actual = todas[idx.coerceIn(0, n - 1)]
    val esperando = actual.interactiva && revelados[actual.id] != true
    val capa = rememberGraphicsLayer()
    val completos = Repo.config.anio_nombres_completos

    // La música del tema, más fuerte conforme avanzan las historias.
    val reproductor = remember { Musica.anualTocar(ctx, tema) }
    DisposableEffect(Unit) { onDispose { Musica.detener() } }
    LaunchedEffect(idx) {
        val vol = 0.55f + 0.45f * idx / maxOf(1, n - 1)
        runCatching { reproductor?.setVolume(vol, vol) }
        if (idx > 0) sentir(Sentidos.Evento.PASAR)
        if (idx == n - 1) { Fiesta.lanzar(); Sentidos.vibrarEvento(ctx, Sentidos.Evento.LOGRO) }
    }
    val avance = remember(idx) { Animatable(0f) }
    LaunchedEffect(idx, pausado, pausaFija, esperando, exportando) {
        if (pausado || pausaFija || esperando || exportando) return@LaunchedEffect
        avance.animateTo(1f, tween(((1f - avance.value) * DURACION).toInt().coerceAtLeast(1), easing = LinearEasing))
        if (idx < n - 1) idx++
    }
    fun ir(i: Int) { idx = i.coerceIn(0, n - 1) }

    suspend fun capturar(c: CartaAnio, i: Int): Uri? {
        delay(450)
        val bmp = runCatching { capa.toImageBitmap().asAndroidBitmap() }.getOrNull() ?: return null
        val final = Bitmap.createScaledBitmap(bmp.copy(Bitmap.Config.ARGB_8888, false), 1080, 1920, true)
        return withContext(Dispatchers.IO) { guardarPng(ctx, "tu_anio_${d.anio}_${"%02d".format(i + 1)}_${c.id}.png", final) }
    }
    fun exportar(todasLas: Boolean) {
        scope.launch {
            exportando = true
            val ocultas = Repo.config.anio_ocultas.toSet()
            val antes = idx
            if (todasLas) {
                var k = 0
                todas.forEachIndexed { i, c -> if (c.id !in ocultas) { idx = i; if (capturar(c, i) != null) k++ } }
                idx = antes
                aviso = "Guardé $k imágenes en Imágenes › VestigiumMei › TuAnio."
            } else {
                val uri = capturar(actual, idx)
                if (uri != null) {
                    aviso = "Guardada en Imágenes › VestigiumMei › TuAnio."
                    runCatching {
                        ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("image/png").putExtra(Intent.EXTRA_STREAM, uri)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), "Compartir").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                }
            }
            exportando = false
        }
    }

    Dialog(onDismissRequest = cerrar, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        val cs = MaterialTheme.colorScheme
        Box(Modifier.fillMaxSize().background(cs.background)) {
            Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
                // Barras de avance arriba, una por historia.
                Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    for (i in 0 until n) {
                        val f = when { i < idx -> 1f; i == idx -> avance.value; else -> 0f }
                        Box(Modifier.weight(1f).height(3.dp).background(cs.onBackground.copy(alpha = 0.25f), CircleShape)) {
                            Box(Modifier.fillMaxWidth(f).fillMaxHeight().background(cs.primary, CircleShape))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(voz(tema, d.anio), style = MaterialTheme.typography.labelLarge, color = cs.primary, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (actual.id in Repo.config.anio_ocultas) Text("oculta al exportar  ", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    Box {
                        Text("⤓", fontSize = 22.sp, color = cs.primary, modifier = Modifier.clickable(enabled = !exportando) { menu = true }.padding(horizontal = 10.dp, vertical = 4.dp))
                        Menu(expanded = menu, onDismissRequest = { menu = false }) {
                            OpcionMenu(text = { Text("Guardar y compartir esta") }, onClick = { menu = false; exportar(false) })
                            OpcionMenu(text = { Text("Guardar todas (PNG)") }, onClick = { menu = false; exportar(true) })
                        }
                    }
                    Text("✕", fontSize = 20.sp, color = cs.onBackground, modifier = Modifier.clickable { cerrar() }.padding(horizontal = 10.dp, vertical = 4.dp)
                        .semantics { contentDescription = "Cerrar" })
                }
                // Flechas y pausa visibles, además de tocar los lados y mantener presionado (N-24).
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.TextButton(onClick = { ir(idx - 1) }, enabled = idx > 0) { Text("‹ anterior") }
                    androidx.compose.material3.TextButton(onClick = { pausaFija = !pausaFija }) { Text(if (pausaFija) "▶ seguir" else "❚❚ pausa") }
                    androidx.compose.material3.TextButton(onClick = { if (idx < n - 1) ir(idx + 1) else cerrar() }, enabled = !esperando) { Text("siguiente ›") }
                }
                if (!d.completo) Text("Año incompleto: faltan ${d.faltan} días", style = MaterialTheme.typography.labelMedium, color = cs.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).background(cs.secondaryContainer, MaterialTheme.shapes.small).padding(horizontal = 10.dp, vertical = 4.dp))
                // La historia, en 9:16 (lo mismo que se guarda en la imagen).
                BoxWithConstraints(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Box(Modifier.aspectRatio(9f / 16f, matchHeightConstraintsFirst = maxWidth / maxHeight > 9f / 16f)
                        .drawWithContent { capa.record { this@drawWithContent.drawContent() }; drawLayer(capa) }
                        .background(cs.background).fondoTema(tema, cs)
                        .pointerInput(idx, esperando) {
                            detectTapGestures(
                                onTap = { o -> if (o.x < size.width * 0.3f) ir(idx - 1) else if (!esperando) { if (idx < n - 1) ir(idx + 1) else cerrar() } },
                                onLongPress = { pausado = true },
                                onPress = { tryAwaitRelease(); pausado = false },
                            )
                        }) {
                        if (!exportando) FondoVivo(tema)
                        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(cs.primary.copy(alpha = 0.16f), Color.Transparent, cs.primary.copy(alpha = 0.10f)))))
                        val vista = Vista(d, exportar = exportando, animar = !exportando && Repo.config.animaciones, revelado = revelados[actual.id] == true || exportando,
                            revelar = { revelados[actual.id] = true },
                            nombre = { s -> val x = s.removePrefix("@"); if (exportando && !completos) iniciales(x) else "@$x" },
                            abrirCarta = {
                                BorradorCarta.limpiar(); BorradorCarta.alFuturo = true; BorradorCarta.fecha = TuAnio.fechaCarta(d.anio)
                                cerrar(); nav.ir("carta_nueva")
                            })
                        CuerpoTarjeta(d, actual, vista)
                        if (pausado || pausaFija) Text("❚❚", fontSize = 28.sp, color = cs.onBackground.copy(alpha = 0.6f), modifier = Modifier.align(Alignment.Center))
                    }
                }
                aviso?.let { a ->
                    Text(a, style = MaterialTheme.typography.labelMedium, color = cs.onSecondaryContainer, modifier = Modifier.fillMaxWidth().background(cs.secondaryContainer).padding(10.dp))
                    LaunchedEffect(a) { delay(3500); aviso = null }
                }
                if (exportando) Text("Guardando imágenes…", style = MaterialTheme.typography.labelMedium, modifier = Modifier.fillMaxWidth().padding(8.dp), textAlign = TextAlign.Center)
            }
            CapaFiesta()
        }
    }
}

// ---------------------------------------------------------------- la pantalla

@Composable
fun PantallaTuAnio(nav: Nav) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    val actual = hoy.take(4).toInt()
    val anios = remember { (Repo.dias.keys.map { it.take(4).toInt() } + actual).distinct().sortedDescending() }
    var anio by remember { mutableIntStateOf(actual) }
    var viendo by remember { mutableStateOf(false) }
    val datos by produceState<TuAnio.Datos?>(null, anio) {
        value = null
        value = withContext(Dispatchers.Default) { runCatching { TuAnio.calcular(ctx, anio) }.getOrNull() }
    }
    val c = Repo.config
    Hoja("Tu año en historias", nav.atras, "Tu año como historias, con la música y los colores del tema: lo que más hiciste, lo único, lo curioso. Llega solo el 31 de diciembre por la mañana.") {
        if (anios.size > 1) Row(Modifier.padding(top = 10.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            anios.forEach { a -> Ficha("$a", anio == a) { anio = a } }
        }
        val d = datos
        Tarjeta(Modifier.padding(top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Emblema(tam = 64.dp)
                Column(Modifier.padding(start = 14.dp).weight(1f)) {
                    Text(voz(Estilo.tema, anio), style = MaterialTheme.typography.titleLarge)
                    Ayuda(when {
                        d == null -> "Contando tu año…"
                        d.completo -> "${d.p.diasDiario} días escritos · ${n0(d.p.palabras.toDouble())} palabras"
                        else -> "Año incompleto: faltan ${d.faltan} días. Puedes verlo ya; las imágenes llevarán la marca."
                    })
                }
            }
            BotonPrincipal("Ver mi año", Modifier.fillMaxWidth().padding(top = 12.dp), habilitado = d != null) { viendo = true }
        }
        Seccion("Antes de exportar", "Lo que no sale en las imágenes. Nunca salen el seguimiento privado ni lo que dicen tus cartas selladas.") {
            Tarjeta {
                Interruptor("Nombres completos en las imágenes", c.anio_nombres_completos, "Si lo apagas, las personas salen con iniciales (A. M.) al exportar.") { v ->
                    Repo.cambiarConfig { it.copy(anio_nombres_completos = v) }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Ayuda("Tarjetas que se exportan:")
                FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    TARJETAS_ANIO.forEach { (id, nombre) ->
                        val sale = id !in c.anio_ocultas
                        Ficha(nombre, sale) { Repo.cambiarConfig { it.copy(anio_ocultas = if (sale) it.anio_ocultas + id else it.anio_ocultas - id) } }
                    }
                }
            }
        }
        // En enero se escriben las predicciones del año; en diciembre, las del siguiente.
        val mes = hoy.substring(5, 7).toInt()
        val paraAnio = if (mes == 12) actual + 1 else actual
        if (mes == 1 || mes == 12 || c.predicciones_anio["$paraAnio"] != null) Seccion("Predicciones para $paraAnio", "Escríbelas ahora; «Tu año» las comparará con lo que de verdad pase.") {
            Predicciones(paraAnio, editable = mes == 1 || mes == 12)
        }
    }
    val d = datos
    if (viendo && d != null) HistoriasAnio(d, nav) { viendo = false }
}

@Composable
private fun Predicciones(anio: Int, editable: Boolean) {
    val previas = Repo.config.predicciones_anio["$anio"]?.respuestas.orEmpty()
    val r = remember(anio) { mutableStateMapOf<String, String>().apply { putAll(previas) } }
    Tarjeta {
        TuAnio.PREGUNTAS.forEach { (k, q) ->
            OutlinedTextField(r[k].orEmpty(), { r[k] = it }, Modifier.fillMaxWidth().padding(vertical = 4.dp), enabled = editable, singleLine = true, label = { Text(q) },
                keyboardOptions = KeyboardOptions(keyboardType = if (k == "persona") KeyboardType.Text else KeyboardType.Decimal))
        }
        if (editable) BotonPrincipal("Guardar predicciones", Modifier.fillMaxWidth().padding(top = 8.dp)) {
            val l = r.filterValues { it.isNotBlank() }
            Repo.cambiarConfig { c -> c.copy(predicciones_anio = c.predicciones_anio + ("$anio" to PrediccionAnio(Fechas.diaLogico(), l))) }
        }
    }
}

/** En Más: tu año en curso, con lo que lleva y cuánto falta. */
@Composable
fun RecuadroTuAnio(nav: Nav) {
    val hoy = Fechas.diaLogico()
    val anio = hoy.take(4).toInt()
    val faltan = Fechas.diasEntre(hoy, "$anio-12-31").toInt()
    val cs = MaterialTheme.colorScheme
    Recuadro("Tu año en historias", if (faltan <= 0) "Tu $anio está listo: ábrelo" else "Tu $anio en historias · faltan $faltan días (puedes verlo ya)", alto = 110.dp, onClick = { nav.ir("tu_anio") }) {
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(cs.primary.copy(alpha = 0.22f), cs.surface, cs.tertiary.copy(alpha = 0.18f))))) {
            Row(Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Emblema(tam = 64.dp)
                Column(Modifier.padding(start = 16.dp)) {
                    Text(voz(Estilo.tema, anio), style = MaterialTheme.typography.titleLarge, color = cs.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    val f = 1f - (faltan.coerceAtLeast(0) / 365f)
                    Box(Modifier.padding(top = 8.dp).width(160.dp).height(6.dp).background(cs.surfaceVariant, CircleShape)) {
                        Box(Modifier.fillMaxWidth(f.coerceIn(0.02f, 1f)).fillMaxHeight().background(cs.primary, CircleShape))
                    }
                }
            }
        }
    }
}

@Suppress("unused") private fun redondo(x: Double) = x.roundToInt()
@Suppress("unused") private val TAM_MINIMO = Modifier.wrapContentSize()
