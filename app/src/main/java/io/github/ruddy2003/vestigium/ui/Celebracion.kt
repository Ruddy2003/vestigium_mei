package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Logros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Los logros recién conseguidos que esperan su celebración a pantalla completa. */
object Celebracion {
    var cola by mutableStateOf<List<Logros.Logro>>(emptyList()); private set
    fun mostrar(l: List<Logros.Logro>) { if (l.isNotEmpty()) cola = cola + l.filter { n -> cola.none { it.id == n.id } } }
    fun siguiente() { cola = cola.drop(1) }
    fun cerrar() { cola = emptyList() }
}

/**
 * Vigila los datos: cuando algo que guardaste te da un logro nuevo, lo celebra en ese momento (no hay que abrir Logros).
 * La primera vez solo los cuenta, para no festejar todo tu historial de golpe; los que llegan de golpe de una importación
 * vieja (más de una semana) tampoco se festejan, solo quedan vistos.
 */
@Composable
fun VigiaLogros() {
    val ctx = LocalContext.current
    LaunchedEffect(Repo.dias, Repo.momentos, Repo.salud, Repo.entrenos, Repo.obras, Repo.cartas, Repo.reacciones, Repo.respuestas, Repo.gastos,
        Repo.ascensos, Repo.config.logros_propios, Repo.config.campanas, Repo.config.politica, Repo.tramos) {
        delay(1800)
        val l = withContext(Dispatchers.Default) { runCatching { Logros.todos(ctx) }.getOrNull() } ?: return@LaunchedEffect
        val vistos = Repo.config.logros_vistos
        val nuevos = l.filter { it.hecho && it.id !in vistos }
        if (nuevos.isEmpty()) return@LaunchedEffect
        Logros.marcarVistos(nuevos)
        if (vistos.isEmpty()) return@LaunchedEffect
        val hoy = Fechas.diaLogico()
        val recientes = nuevos.filter { it.conseguido?.let { f -> f >= Fechas.sumar(hoy, -7) } == true }
            .sortedWith(compareByDescending<Logros.Logro> { it.nivel }.thenByDescending { it.conseguido })
        Celebracion.mostrar(recientes.take(5))
    }
}

/** La celebración: el logro dibujado en grande, rayos del tema girando detrás, su nombre y la lluvia de figuras del tema. */
@Composable
fun CapaLogro() {
    val l = Celebracion.cola.firstOrNull()
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    val tema = Estilo.tema
    val cs = MaterialTheme.colorScheme
    val animar = Repo.config.animaciones
    LaunchedEffect(l?.id) {
        if (l == null) return@LaunchedEffect
        sentir(Sentidos.Evento.LOGRO)
        if (animar) Fiesta.lanzar()
    }
    AnimatedVisibility(l != null, enter = fadeIn(tween(250)), exit = fadeOut(tween(250))) {
        val g = l ?: return@AnimatedVisibility
        val escala = remember(g.id) { Animatable(if (animar) 0.3f else 1f) }
        LaunchedEffect(g.id) { if (animar) escala.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow)) }
        val giro = rememberInfiniteTransition(label = "rayos")
        val angulo by giro.animateFloat(0f, 360f, infiniteRepeatable(tween(24000, easing = LinearEasing), RepeatMode.Restart), label = "angulo")
        Box(Modifier.fillMaxSize().background(cs.background.copy(alpha = 0.96f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { Celebracion.siguiente() },
            contentAlignment = Alignment.Center) {
            // Rayos del color del tema.
            Canvas(Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2, size.height * 0.42f)
                val r = size.maxDimension
                drawCircle(Brush.radialGradient(listOf(cs.primary.copy(alpha = 0.28f), cs.background.copy(alpha = 0f)), c, size.minDimension * 0.6f), size.minDimension * 0.6f, c)
                rotate(if (animar) angulo else 0f, c) {
                    for (i in 0 until 16) {
                        val a1 = i * 2 * PI / 16; val a2 = a1 + PI / 32
                        val p = androidx.compose.ui.graphics.Path().apply {
                            moveTo(c.x, c.y); lineTo(c.x + (r * cos(a1)).toFloat(), c.y + (r * sin(a1)).toFloat()); lineTo(c.x + (r * cos(a2)).toFloat(), c.y + (r * sin(a2)).toFloat()); close()
                        }
                        drawPath(p, cs.primary.copy(alpha = 0.06f))
                    }
                }
            }
            Column(Modifier.statusBarsPadding().navigationBarsPadding().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(Estilo.seccion("Logro conseguido", null).first, style = MaterialTheme.typography.titleLarge, color = cs.primary, textAlign = TextAlign.Center)
                Box(Modifier.size(230.dp).graphicsLayer { scaleX = escala.value; scaleY = escala.value }, contentAlignment = Alignment.Center) {
                    io.github.ruddy2003.vestigium.ui.pantallas.PiezaLogro(g, 210.dp, tema = tema)
                }
                Text(g.nombre, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Text(g.descripcion, style = MaterialTheme.typography.bodyLarge, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
                Text(listOf("bronce", "plata", "oro", "legendario")[(g.nivel - 1).coerceIn(0, 3)] + " · " + g.categoria.lowercase() + (g.conseguido?.let { " · " + Fechas.titulo(it).lowercase() } ?: ""),
                    style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    val enHall = g.id in Repo.config.hall_logros
                    TextButton(onClick = {
                        if (!enHall && Repo.config.hall_logros.size < 12) Repo.cambiarConfig { c -> c.copy(hall_logros = c.hall_logros + g.id) }
                    }, enabled = !enHall) { Text(if (enHall) "ya está en tu hall" else "ponerlo en el hall", style = MaterialTheme.typography.labelLarge) }
                    BotonPrincipal(if (Celebracion.cola.size > 1) "siguiente (${Celebracion.cola.size - 1})" else "seguir") { Celebracion.siguiente() }
                }
            }
        }
    }
}
