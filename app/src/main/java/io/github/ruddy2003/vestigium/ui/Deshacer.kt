package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo

/**
 * «Deshacer» después de borrar: durante 5 segundos aparece una barra abajo para devolver lo borrado.
 * [alExpirar] corre si no se deshizo (por ejemplo, borrar de verdad la portada de una obra).
 */
object Deshacer {
    class Oferta(val texto: String, val restaurar: () -> Unit, val alExpirar: () -> Unit, val n: Long)

    var actual by mutableStateOf<Oferta?>(null); private set
    private var cuenta = 0L

    fun ofrecer(texto: String, alExpirar: () -> Unit = {}, restaurar: () -> Unit) {
        actual?.alExpirar?.invoke()
        actual = Oferta(texto, restaurar, alExpirar, ++cuenta)
    }

    fun deshacer() { val o = actual ?: return; actual = null; o.restaurar() }

    fun expirar(n: Long) { val o = actual ?: return; if (o.n != n) return; actual = null; o.alExpirar() }
}

@Composable
fun BoxScope.CapaDeshacer() {
    val o = Deshacer.actual
    // Lo que queda de los 5 segundos, para la cuenta atrás de la barra (sin animaciones, la barra se queda llena).
    val resta = remember(o?.n) { Animatable(1f) }
    LaunchedEffect(o?.n) { val n = o?.n ?: return@LaunchedEffect; kotlinx.coroutines.delay(5000); Deshacer.expirar(n) }
    LaunchedEffect(o?.n) { if (o != null && Repo.config.animaciones) resta.animateTo(0f, tween(5000, easing = LinearEasing)) }
    AnimatedVisibility(o != null, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 80.dp, start = 16.dp, end = 16.dp),
        enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut()) {
        BarraDeshacer(o?.texto ?: "", { Deshacer.deshacer() }, restante = { resta.value })
    }
    // Los selectores de día y hora que se piden desde un toque (elegirFecha, elegirHora) se dibujan aquí.
    AnfitrionSelectores()
}

/**
 * La barra de «deshacer»: la forma de los botones del tema, su perilla como marca al empezar y, abajo, el riel del
 * tema que se vacía en los 5 segundos. Colores inversos a la pantalla, como un aviso que flota.
 */
@Composable
fun BarraDeshacer(texto: String, onDeshacer: () -> Unit, modifier: Modifier = Modifier, restante: () -> Float = { 1f }) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    Surface(color = cs.inverseSurface, shape = formaBotonTema(tema), border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.5f)), shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()) {
        Column {
            Row(Modifier.padding(start = 14.dp, end = 10.dp, top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Canvas(Modifier.size(16.dp)) { perillaTema(tema, cs, center, size.minDimension / 2f, cs.inversePrimary, cs.inverseSurface) }
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) { Text(texto, style = MaterialTheme.typography.bodyMedium, color = cs.inverseOnSurface) }
                TextButton(onClick = onDeshacer) { Text("deshacer", style = MaterialTheme.typography.labelLarge, color = cs.inversePrimary) }
            }
            Canvas(Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 6.dp).height(3.dp)) {
                rielTema(tema, cs, restante(), 0, cs.inversePrimary, cs.inverseOnSurface.copy(alpha = 0.25f))
            }
        }
    }
}
