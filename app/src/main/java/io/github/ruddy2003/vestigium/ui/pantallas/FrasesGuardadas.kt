package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
fun CorazonFrase(f: Frases.Frase, color: Color, modifier: Modifier = Modifier) {
    val guardada = Frases.guardada(f)
    val sentir = rememberSentir()
    val scope = rememberCoroutineScope()
    val escala = remember { Animatable(1f) }
    val brote = remember { Animatable(1f) }
    val animar = Repo.config.animaciones
    Box(
        modifier.size(40.dp).clip(CircleShape).clickable {
            val ahora = Frases.alternar(f)
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

/** Las frases del día que guardaste con el corazón, de todos los temas. */
@Composable
fun PantallaFrasesGuardadas(nav: Nav) {
    val lista = Repo.config.frases_guardadas.reversed()
    var armado by remember { mutableStateOf<String?>(null) }
    val sentir = rememberSentir()
    Hoja("Frases guardadas", nav.atras, "Las frases del día a las que les diste ♥. Todas son citas reales, con su autor y su fuente.") {
        if (lista.isEmpty()) Box(Modifier.padding(top = 16.dp)) { Vacio("Todavía no guardas ninguna. Toca el corazón junto a la frase del día, en la pestaña Día.") }
        lista.forEach { g ->
            val clave = g.texto + g.autor
            Box(Modifier.padding(top = 12.dp)) {
                Tarjeta {
                    g.original?.let { Text(it, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.primary) }
                    Text("«${g.texto}»", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
                    Text("— ${g.autor}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                    Row(Modifier.fillMaxWidth().padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        val tema = TemaId.entries.firstOrNull { it.id == g.tema }?.nombre ?: g.tema
                        Text("$tema · guardada ${Fechas.relativo(g.fecha)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            if (armado == clave) {
                                sentir(Sentidos.Evento.TOQUE)
                                Repo.cambiarConfig { c -> c.copy(frases_guardadas = c.frases_guardadas.filterNot { it.texto == g.texto && it.autor == g.autor }) }
                                armado = null
                            } else armado = clave
                        }) {
                            Text(if (armado == clave) "¿Quitar?" else "Quitar", color = if (armado == clave) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
