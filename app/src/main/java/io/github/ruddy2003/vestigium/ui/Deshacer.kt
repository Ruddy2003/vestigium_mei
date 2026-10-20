package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    LaunchedEffect(o?.n) { val n = o?.n ?: return@LaunchedEffect; kotlinx.coroutines.delay(5000); Deshacer.expirar(n) }
    AnimatedVisibility(o != null, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 80.dp, start = 16.dp, end = 16.dp),
        enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut()) {
        val cs = MaterialTheme.colorScheme
        Surface(color = cs.inverseSurface, shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.5f)), shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(start = 16.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { Text(o?.texto ?: "", style = MaterialTheme.typography.bodyMedium, color = cs.inverseOnSurface) }
                TextButton(onClick = { Deshacer.deshacer() }) { Text("deshacer", style = MaterialTheme.typography.labelLarge, color = cs.inversePrimary) }
            }
        }
    }
}
