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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.luminance
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
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.materia
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import java.time.LocalDate
import io.github.ruddy2003.vestigium.ui.elegirFecha

object EdicionObra { var id by mutableStateOf<String?>(null) }

/** Qué se busca en el buscador de obras y, si viene de una obra ya creada, solo se le cambia la portada. */
object BusquedaObra {
    var tipo by mutableStateOf("libro")
    var texto by mutableStateOf("")
    var portadaDe by mutableStateOf<String?>(null)
}

val TIPOS_OBRA = listOf(Opcion("libro", "Libro"), Opcion("pelicula", "Película"), Opcion("serie", "Serie"), Opcion("juego", "Juego"), Opcion("articulo", "Artículo"))
private val ESTADOS = listOf(Opcion("quiero", "Pendiente"), Opcion("en_curso", "En curso"), Opcion("terminado", "Terminado"), Opcion("abandonado", "Abandonado"))

private fun autorDe(tipo: String) = when (tipo) { "libro" -> "Autor"; "pelicula" -> "Dirección"; "serie" -> "Creador o canal"; "articulo" -> "Autores"; else -> "Estudio" }
private fun unidad(tipo: String) = when (tipo) { "libro", "articulo" -> "páginas"; "pelicula" -> "minutos"; "serie" -> "capítulos"; else -> "horas" }
private fun verbo(tipo: String) = when (tipo) { "libro", "articulo" -> "Leyendo ahora"; "juego" -> "Jugando ahora"; else -> "Viendo ahora" }
private fun plural(tipo: String) = when (tipo) { "libro" -> "libros"; "pelicula" -> "películas"; "serie" -> "series"; "juego" -> "juegos"; else -> "artículos" }
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
    val tema = Estilo.tema
    val m = materia(tema)
    // La letra de los títulos del tema (la gótica, la de estarcido, la de consola…).
    val letraTema = MaterialTheme.typography.headlineSmall.fontFamily ?: Letra.garamond
    Box(modifier.clip(RoundedCornerShape(4.dp))) {
        val b = bmp
        if (b != null) Image(b.asImageBitmap(), o.titulo, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(m.brillo, m.cuerpo, m.sombra)))) {
            // Letra que se lea sobre la tapa: la tinta del tema si contrasta, si no crema o tinta oscura.
            val ancho = maxWidth
            val fondo = m.cuerpo.luminance()
            val letra = if (abs(m.textoCuerpo.luminance() - fondo) > 0.3f) m.textoCuerpo else if (fondo < 0.4f) Color(0xFFF4ECD8) else Color(0xFF231A12)
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) { adornoPortada(tema, m.acento, letra) }
            if (ancho < 64.dp) {
                // Miniatura de lista: solo la inicial, grande, dentro del filete.
                Box(Modifier.fillMaxSize().padding(3.dp).border(1.dp, m.acento.copy(alpha = 0.8f), RoundedCornerShape(2.dp)), contentAlignment = Alignment.Center) {
                    Text(o.titulo.trim().firstOrNull()?.uppercase() ?: "·", fontFamily = letraTema, fontSize = (ancho.value * 0.45f).sp, color = letra)
                }
            } else Box(Modifier.fillMaxSize().padding(5.dp).border(1.dp, m.acento.copy(alpha = 0.8f), RoundedCornerShape(2.dp)).padding(5.dp)) {
                val escala = (ancho.value / 78f).coerceIn(1f, 1.6f)
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre ?: "").uppercase(), fontSize = (7 * escala).sp, color = m.acento, letterSpacing = 1.sp, maxLines = 1)
                    Text(o.titulo.ifBlank { "—" }, fontFamily = letraTema, fontSize = (11 * escala).sp, lineHeight = (12 * escala).sp, color = letra, textAlign = TextAlign.Center,
                        maxLines = 5, overflow = TextOverflow.Ellipsis)
                    Text(listOfNotNull(o.revista.ifBlank { null } ?: o.autor.substringBefore(",").ifBlank { null }, o.anio?.toString()).joinToString(" · "),
                        fontSize = (7 * escala).sp, color = letra.copy(alpha = 0.8f), maxLines = 2, textAlign = TextAlign.Center, lineHeight = (8 * escala).sp)
                }
            }
        }
    }
}

/**
 * El adorno de la tapa dibujada, uno por tema: laurel, renglones de consola, grecas, cuadrifolios, tachones de censura,
 * remaches y franjas, galones, papel cuadriculado, columnas de gaceta, abejas, helecho, copos, moneda, arco ojival, órbita,
 * estrellas de lazo, cuernos de carnero, círculo y cuadrado de Vitruvio, cuña roja, la franja tricolor o una hilera de runas.
 * Va tenue, en los bordes y abajo, para que el título se siga leyendo. `a` es el color de acento y `l` el de la letra.
 */
private fun DrawScope.adornoPortada(t: TemaId, a: Color, l: Color) {
    val w = size.width; val h = size.height; val u = w / 80f
    val tenue = a.copy(alpha = 0.45f)
    fun linea(x0: Float, y0: Float, x1: Float, y1: Float, color: Color = tenue, grosor: Float = 1f * u) = drawLine(color, Offset(x0, y0), Offset(x1, y1), grosor)
    fun rombo(o: Offset, rx: Float, ry: Float, color: Color) =
        drawPath(Path().apply { moveTo(o.x, o.y - ry); lineTo(o.x + rx, o.y); lineTo(o.x, o.y + ry); lineTo(o.x - rx, o.y); close() }, color)
    when (t) {
        // Dos ramas de laurel que suben desde abajo.
        TemaId.ROMANO -> for (lado in listOf(-1f, 1f)) for (k in 0 until 6) {
            val g = Math.toRadians(20.0 + 12.0 * k); val r = w * 0.3f
            val o = Offset(w / 2 + lado * r * kotlin.math.cos(g).toFloat(), h * 0.74f + r * 0.6f * kotlin.math.sin(g).toFloat())
            drawOval(tenue, Offset(o.x - 2.6f * u, o.y - 1.4f * u), Size(5.2f * u, 2.8f * u))
        }
        // Renglones de pantalla y el indicador con su cursor.
        TemaId.CIBER -> {
            var y = 0f
            while (y < h) { linea(0f, y, w, y, a.copy(alpha = 0.08f), 0.6f * u); y += 3 * u }
            linea(8 * u, h - 14 * u, 12 * u, h - 11 * u, a.copy(alpha = 0.7f)); linea(12 * u, h - 11 * u, 8 * u, h - 8 * u, a.copy(alpha = 0.7f))
            drawRect(a.copy(alpha = 0.7f), Offset(15 * u, h - 14 * u), Size(4 * u, 6 * u))
        }
        TemaId.NEON -> {
            var y = 0f
            while (y < h) { linea(0f, y, w, y, a.copy(alpha = 0.08f), 0.6f * u); y += 3 * u }
            linea(8 * u, h - 14 * u, 12 * u, h - 11 * u, a.copy(alpha = 0.7f)); linea(12 * u, h - 11 * u, 8 * u, h - 8 * u, a.copy(alpha = 0.7f))
            drawRect(a.copy(alpha = 0.7f), Offset(15 * u, h - 14 * u), Size(4 * u, 6 * u))
        }
        // Una greca arriba y otra abajo.
        TemaId.HELENO -> listOf(16 * u, h - 20 * u).forEach { y0 ->
            val p = 3.5f * u
            drawPath(Path().apply {
                moveTo(6 * u, y0 + p); var x = 6 * u
                while (x + 2 * p <= w - 6 * u) { lineTo(x, y0); lineTo(x + p, y0); lineTo(x + p, y0 + p); lineTo(x + 2 * p, y0 + p); x += 2 * p }
            }, tenue, style = Stroke(1f * u))
        }
        // Cuadrifolios en las esquinas y la raya roja del margen.
        TemaId.MEDIEVAL -> {
            linea(10 * u, 8 * u, 10 * u, h - 8 * u, a.copy(alpha = 0.6f), 1.2f * u)
            listOf(Offset(10 * u, 10 * u), Offset(w - 10 * u, 10 * u), Offset(10 * u, h - 10 * u), Offset(w - 10 * u, h - 10 * u)).forEach { o ->
                listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { d -> drawCircle(tenue, 2.2f * u, o + d * (2.2f * u)) }
            }
        }
        // Tachones de censura y el sello de goma.
        TemaId.DISTOPIA -> {
            drawRect(l.copy(alpha = 0.35f), Offset(8 * u, h - 22 * u), Size(w * 0.55f, 4 * u))
            drawRect(l.copy(alpha = 0.35f), Offset(8 * u, h - 15 * u), Size(w * 0.35f, 4 * u))
            drawCircle(a.copy(alpha = 0.55f), 9 * u, Offset(w - 16 * u, 22 * u), style = Stroke(1.4f * u))
            drawCircle(a.copy(alpha = 0.55f), 6.5f * u, Offset(w - 16 * u, 22 * u), style = Stroke(0.8f * u))
        }
        // Tres franjas de folleto abajo y un remache en cada esquina.
        TemaId.REFUGIO -> {
            for (k in 0 until 3) drawRect(a.copy(alpha = 0.22f + 0.1f * k), Offset(0f, h - (8 + 6 * k) * u), Size(w, 3.5f * u))
            listOf(Offset(7 * u, 7 * u), Offset(w - 7 * u, 7 * u), Offset(7 * u, h - 26 * u), Offset(w - 7 * u, h - 26 * u)).forEach { drawCircle(tenue, 1.8f * u, it) }
        }
        // Galones de grado abajo, uno sobre otro.
        TemaId.ORDEN -> for (k in 0 until 3) {
            val y = h - (12 + 5 * k) * u
            drawPath(Path().apply { moveTo(w / 2 - 12 * u, y - 5 * u); lineTo(w / 2, y); lineTo(w / 2 + 12 * u, y - 5 * u) }, tenue, style = Stroke(2f * u))
        }
        // Papel cuadriculado de cuaderno de laboratorio.
        TemaId.LABORATORIO -> {
            val malla = a.copy(alpha = 0.12f); var x = 0f
            while (x < w) { linea(x, 0f, x, h, malla, 0.5f * u); x += 6 * u }
            var y = 0f
            while (y < h) { linea(0f, y, w, y, malla, 0.5f * u); y += 6 * u }
        }
        // Raya gruesa y fina arriba, y columnas de gaceta abajo.
        TemaId.COLONOS -> {
            linea(6 * u, 14 * u, w - 6 * u, 14 * u, tenue, 2f * u); linea(6 * u, 17 * u, w - 6 * u, 17 * u, tenue, 0.6f * u)
            listOf(w / 3, 2 * w / 3).forEach { x -> linea(x, h * 0.72f, x, h - 8 * u, tenue, 0.6f * u) }
        }
        // Abejas sembradas, como en el manto imperial.
        TemaId.NAPOLEON -> for (fila in 0 until 3) for (col in 0 until 3) {
            if ((fila + col) % 2 == 0) drawPath(simboloCalificacion(t, Offset(w * (0.2f + 0.3f * col), h * (0.62f + 0.13f * fila)), 3.2f * u), a.copy(alpha = 0.35f))
        }
        // Un helecho que sube por el borde izquierdo.
        TemaId.BOTANICO -> {
            val x0 = 9 * u
            linea(x0, h - 6 * u, x0 + 3 * u, h * 0.45f, tenue)
            for (k in 0 until 7) {
                val y = h - 10 * u - k * (h * 0.075f); val x = x0 + 3 * u * (k / 7f); val largo = (7 - k) * 0.9f * u + 2 * u
                linea(x, y, x + largo, y - 2.5f * u); linea(x, y, x - largo * 0.5f, y - 2 * u)
            }
        }
        // Copos de nieve.
        TemaId.NAVIDAD -> listOf(Offset(w * 0.2f, h * 0.2f) to 5f, Offset(w * 0.82f, h * 0.36f) to 3.5f, Offset(w * 0.3f, h * 0.84f) to 4f, Offset(w * 0.75f, h * 0.8f) to 3f).forEach { (o, r) ->
            for (k in 0 until 3) { val g = Math.toRadians(90.0 + 60.0 * k); val d = Offset(kotlin.math.cos(g).toFloat(), kotlin.math.sin(g).toFloat()) * (r * u); drawLine(tenue, o - d, o + d, 0.8f * u) }
        }
        // Una moneda con su agujero cuadrado, abajo al centro.
        TemaId.SEDA -> {
            val o = Offset(w / 2, h - 16 * u)
            drawCircle(tenue, 8 * u, o, style = Stroke(1.4f * u)); drawCircle(tenue, 5.5f * u, o, style = Stroke(0.7f * u))
            drawRect(tenue, Offset(o.x - 2.2f * u, o.y - 2.2f * u), Size(4.4f * u, 4.4f * u), style = Stroke(1f * u))
        }
        // Un arco ojival que enmarca el título.
        TemaId.GOTICO -> {
            val x0 = 8 * u; val x1 = w - 8 * u; val base = h - 8 * u; val arranque = h * 0.36f
            drawPath(Path().apply {
                moveTo(x0, base); lineTo(x0, arranque); quadraticTo(x0, arranque - h * 0.2f, w / 2, 10 * u)
                quadraticTo(x1, arranque - h * 0.2f, x1, arranque); lineTo(x1, base)
            }, tenue, style = Stroke(1.2f * u))
        }
        // Estrellas sueltas y un planeta con su órbita.
        TemaId.ESPACIO -> {
            listOf(0.15f to 0.12f, 0.8f to 0.2f, 0.12f to 0.55f, 0.88f to 0.6f, 0.5f to 0.08f).forEach { (x, y) -> drawCircle(a.copy(alpha = 0.6f), 0.9f * u, Offset(w * x, h * y)) }
            drawOval(tenue, Offset(w * 0.2f, h - 20 * u), Size(w * 0.6f, 9 * u), style = Stroke(0.8f * u))
            drawCircle(tenue, 3.5f * u, Offset(w / 2, h - 15.5f * u))
        }
        // Estrellas de lazo de ocho puntas en las esquinas.
        TemaId.ANDALUS -> listOf(Offset(11 * u, 11 * u), Offset(w - 11 * u, 11 * u), Offset(11 * u, h - 11 * u), Offset(w - 11 * u, h - 11 * u)).forEach { o ->
            listOf(-90.0, -45.0).forEach { g0 ->
                drawPath(Path().apply {
                    for (i in 0 until 4) { val g = Math.toRadians(g0 + 90.0 * i); val x = o.x + 5 * u * kotlin.math.cos(g).toFloat(); val y = o.y + 5 * u * kotlin.math.sin(g).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y) }
                    close()
                }, tenue)
            }
        }
        // Cuernos de carnero enroscados en las esquinas de abajo.
        TemaId.MONGOL -> listOf(Offset(13 * u, h - 13 * u), Offset(w - 13 * u, h - 13 * u)).forEach { o ->
            drawArc(tenue, 0f, 270f, false, Offset(o.x - 6 * u, o.y - 6 * u), Size(12 * u, 12 * u), style = Stroke(1.4f * u))
            drawArc(tenue, 90f, 270f, false, Offset(o.x - 3 * u, o.y - 3 * u), Size(6 * u, 6 * u), style = Stroke(1.4f * u))
        }
        // El círculo y el cuadrado de Vitruvio, a lápiz, detrás del título.
        TemaId.RENACIMIENTO -> {
            val c = Offset(w / 2, h * 0.52f); val r = w * 0.36f
            drawCircle(a.copy(alpha = 0.25f), r, c, style = Stroke(0.7f * u))
            drawRect(a.copy(alpha = 0.25f), Offset(c.x - r * 0.88f, c.y - r * 0.6f), Size(r * 1.76f, r * 1.76f), style = Stroke(0.7f * u))
            linea(c.x, c.y - r, c.x, c.y + r, a.copy(alpha = 0.18f), 0.5f * u); linea(c.x - r, c.y, c.x + r, c.y, a.copy(alpha = 0.18f), 0.5f * u)
        }
        // Una cuña roja desde la esquina y una barra en diagonal.
        TemaId.URSS -> {
            drawPath(Path().apply { moveTo(0f, h); lineTo(0f, h * 0.7f); lineTo(w * 0.45f, h); close() }, a.copy(alpha = 0.5f))
            linea(w * 0.55f, h - 6 * u, w - 6 * u, h * 0.62f, l.copy(alpha = 0.35f), 3 * u)
        }
        // La franja tricolor a lo largo del borde izquierdo.
        TemaId.REVOLUCION -> listOf(Color(0xFF1F3A93), Color(0xFFF4F1EA), Color(0xFFC8102E)).forEachIndexed { k, col ->
            drawRect(col.copy(alpha = 0.75f), Offset((3 + 3 * k) * u, 0f), Size(3 * u, h))
        }
        // Una hilera de runas abajo y una gema arriba.
        TemaId.ANTIGUAS -> {
            rombo(Offset(w / 2, 9 * u), 3 * u, 4.5f * u, a.copy(alpha = 0.55f))
            var x = 12 * u; var i = 0; val y0 = h - 14 * u
            while (x < w - 10 * u) {
                linea(x, y0, x, y0 + 7 * u)
                if (i % 2 == 0) linea(x, y0, x + 3.5f * u, y0 + 2.5f * u) else linea(x - 3 * u, y0 + 2 * u, x + 3 * u, y0 + 5 * u)
                x += 9 * u; i++
            }
        }
    }
}

// =====================================================================
//  Biblioteca
// =====================================================================

@Composable
fun PantallaBiblioteca(nav: Nav) {
    var filtro by remember { mutableStateOf<String?>(null) }
    val todas = Repo.obras.filter { filtro == null || it.tipo == filtro }
    val anio = Fechas.diaLogico().take(4)
    // Lo que se abrió para añadir y se dejó sin título no se queda.
    LaunchedEffect(Unit) { Repo.obras.filter { it.titulo.isBlank() }.forEach { Repo.borrarObra(it.id) } }

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
        val terminadas = todas.filter { it.estado == "terminado" }.sortedByDescending { it.fin ?: it.agregado }
        Seccion("Terminados") { if (terminadas.isEmpty()) Vacio("Nada terminado todavía.") else Lista(terminadas, nav, porAnio = true) }
        val pendientes = todas.filter { it.estado == "quiero" }.sortedByDescending { it.agregado }
        if (pendientes.isNotEmpty()) Seccion("Pendientes") { Lista(pendientes, nav) }
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
        if (!Portadas.disponible(ctx, tipo)) Ayuda(if (tipo == "pelicula") "Para buscar películas pon tu clave de TMDB en Obras → Buscadores. Mientras, añádela a mano y ponle una foto."
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
        Seccion("Citas", "Las frases que te quedaron. Con ♥ aparecen en tus frases guardadas, junto a las del día.", numero = 3, latin = "SENTENTIAE") {
            Citas(o, sentir)
        }
        Seccion("Qué te dejó", numero = 4, latin = "IVDICIVM") {
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
        if (marcas.isNotEmpty()) Seccion("Subrayados de Kindle", numero = 5, latin = "EXCERPTA") { Tarjeta { Subrayados(marcas) } }
        TextButton(onClick = { if (confirmar) { Repo.borrarObra(o.id); nav.atras(); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Obra borrada", alExpirar = { Portadas.borrar(ctx, o.portada) }) { Repo.guardarObra(o) } } else confirmar = true }, modifier = Modifier.padding(top = 16.dp)) {
            Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Las citas de una obra: escribir una nueva, darle o quitarle ♥, borrarla. */
@Composable
private fun Citas(o: Obra, sentir: (Sentidos.Evento) -> Unit) {
    var texto by remember(o.id) { mutableStateOf("") }
    var pagina by remember(o.id) { mutableStateOf("") }
    var armado by remember { mutableStateOf<String?>(null) }
    Tarjeta {
        o.citas.forEachIndexed { i, c ->
            if (i > 0) HorizontalDivider(Modifier.padding(vertical = 6.dp))
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("«${c.texto}»", style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic)
                    Text(listOfNotNull(c.pagina.ifBlank { null }?.let { if (it.all { ch -> ch.isDigit() }) "p. $it" else it }, c.fecha.ifBlank { null }?.let { Fechas.corto(it) }).joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            OutlinedTextField(pagina, { pagina = it.take(20) }, Modifier.width(130.dp), singleLine = true, placeholder = { Text(if (o.tipo == "pelicula" || o.tipo == "serie") "minuto" else "página") })
            Spacer(Modifier.width(10.dp))
            BotonPrincipal("Guardar cita", habilitado = texto.isNotBlank()) {
                val c = Cita(Repo.nuevoId(), texto.trim().removePrefix("«").removeSuffix("»").removePrefix("\"").removeSuffix("\"").trim(), pagina.trim(), Fechas.diaLogico())
                Repo.guardarObra(o.copy(citas = o.citas + c)); texto = ""; pagina = ""
            }
        }
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
