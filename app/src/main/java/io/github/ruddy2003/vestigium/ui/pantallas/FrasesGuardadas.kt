package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Frases
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/** El corazón de la frase del día: late al tocarlo y suelta una chispa de puntos cuando se guarda. */
@Composable
fun CorazonFrase(f: Frases.Frase, color: Color, modifier: Modifier = Modifier, fecha: String = io.github.ruddy2003.vestigium.data.Fechas.diaLogico()) {
    val guardada = Frases.guardada(f)
    val sentir = rememberSentir()
    val scope = rememberCoroutineScope()
    val escala = remember { Animatable(1f) }
    val brote = remember { Animatable(1f) }
    val animar = Repo.config.animaciones
    Box(
        modifier.size(40.dp).clip(CircleShape).clickable {
            val ahora = Frases.alternar(f, fecha = fecha)
            sentir(if (ahora) Sentidos.Evento.ELEGIR else Sentidos.Evento.TOQUE)
            if (animar) scope.launch { escala.snapTo(0.55f); escala.animateTo(1f, spring(dampingRatio = 0.32f, stiffness = 420f)) }
            if (animar && ahora) scope.launch { brote.snapTo(0f); brote.animateTo(1f, tween(520)) }
        }.semantics { contentDescription = if (guardada) "Quitar de frases guardadas" else "Guardar esta frase" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(24.dp).graphicsLayer { scaleX = escala.value; scaleY = escala.value }) {
            val u = size.minDimension / 24f
            val p = Path().apply {
                moveTo(12f * u, 21f * u)
                cubicTo(5f * u, 15.5f * u, 2f * u, 12f * u, 2f * u, 8.5f * u)
                cubicTo(2f * u, 5.5f * u, 4.5f * u, 3f * u, 7.5f * u, 3f * u)
                cubicTo(9.5f * u, 3f * u, 11f * u, 4f * u, 12f * u, 5.8f * u)
                cubicTo(13f * u, 4f * u, 14.5f * u, 3f * u, 16.5f * u, 3f * u)
                cubicTo(19.5f * u, 3f * u, 22f * u, 5.5f * u, 22f * u, 8.5f * u)
                cubicTo(22f * u, 12f * u, 19f * u, 15.5f * u, 12f * u, 21f * u)
                close()
            }
            if (guardada) drawPath(p, color) else drawPath(p, color.copy(alpha = 0.7f), style = Stroke(1.8f * u))
            val b = brote.value
            if (b < 1f) for (i in 0 until 8) {
                val a = Math.toRadians(45.0 * i - 90).toFloat()
                val r = (8f + 10f * b) * u
                drawCircle(color.copy(alpha = 1f - b), (1.6f - b) * u * 1.2f, Offset(center.x + r * cos(a), center.y + r * sin(a)))
            }
        }
    }
}

/** Una frase en la lista: del día, de Kindle o copiada de una obra. [quitar] la saca de la lista. */
private class ItemFrase(val clave: String, val original: String?, val texto: String, val autor: String, val origen: String, val fecha: String, val obra: String?, val quitar: () -> Unit)

/** Las frases que guardaste con el corazón: las del día (de todos los temas), los subrayados de Kindle y las citas de tus obras. */
@Composable
fun PantallaFrasesGuardadas(nav: Nav) {
    val delDia = Repo.config.frases_guardadas.map { g ->
        ItemFrase("f" + g.texto + g.autor, g.original, g.texto, g.autor,
            if (g.tema == "kindle") "Subrayado de Kindle" else "Frase del día · " + (TemaId.entries.firstOrNull { it.id == g.tema }?.nombre ?: g.tema), g.fecha, null) {
            Repo.cambiarConfig { c -> c.copy(frases_guardadas = c.frases_guardadas.filterNot { it.texto == g.texto && it.autor == g.autor }) }
        }
    }
    val deObras = Repo.obras.flatMap { o -> o.citas.filter { it.favorita }.map { c ->
        ItemFrase("c" + c.id, null, c.texto, listOfNotNull(o.autor.ifBlank { null }, o.titulo).joinToString(", ") + (c.pagina.ifBlank { null }?.let { if (it.all { ch -> ch.isDigit() }) ", p. $it" else ", $it" } ?: ""),
            "Cita de " + (TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre?.lowercase() ?: "obra"), c.fecha.ifBlank { o.agregado }, o.id) {
            Repo.obras.firstOrNull { it.id == o.id }?.let { x -> Repo.guardarObra(x.copy(citas = x.citas.map { if (it.id == c.id) it.copy(favorita = false) else it })) }
        }
    } }
    val todas = (delDia + deObras).sortedByDescending { it.fecha }
    var filtro by remember { mutableStateOf<String?>(null) }
    val lista = todas.filter { filtro == null || it.origen.startsWith(filtro!!) }
    var armado by remember { mutableStateOf<String?>(null) }
    val sentir = rememberSentir()
    Hoja("Frases guardadas", nav.atras, "Las frases a las que les diste ♥: las del día, tus subrayados de Kindle y las citas que copias de tus libros, películas y artículos.") {
        if (todas.isNotEmpty()) Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todas", filtro == null) { filtro = null }
            if (delDia.any { it.origen.startsWith("Frase") }) Ficha("del día", filtro == "Frase") { filtro = "Frase" }
            if (deObras.isNotEmpty()) Ficha("de mis obras", filtro == "Cita") { filtro = "Cita" }
            if (delDia.any { it.origen.startsWith("Subrayado") }) Ficha("Kindle", filtro == "Subrayado") { filtro = "Subrayado" }
        }
        if (todas.isEmpty()) Box(Modifier.padding(top = 16.dp)) { Vacio("Todavía no guardas ninguna. Toca el corazón junto a la frase del día, o guarda citas dentro de un libro en Obras.") }
        lista.forEach { g ->
            Box(Modifier.padding(top = 12.dp)) {
                Tarjeta {
                    g.original?.let { Text(it, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.primary) }
                    Text("«${g.texto}»", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
                    Text("— ${g.autor}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp).then(if (g.obra != null) Modifier.clickable { EdicionObra.id = g.obra; nav.ir("obra") } else Modifier))
                    Row(Modifier.fillMaxWidth().padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${g.origen} · ${Fechas.relativo(g.fecha)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            if (armado == g.clave) { sentir(Sentidos.Evento.TOQUE); g.quitar(); armado = null } else armado = g.clave
                        }) {
                            Text(if (armado == g.clave) "¿Quitar?" else "Quitar", color = if (armado == g.clave) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
