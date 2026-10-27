package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Borrado
import io.github.ruddy2003.vestigium.data.Papelera
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** La papelera (N-14): lo borrado en los últimos 7 días, para recuperarlo. Lo privado no sale aquí, sino en Privado. */
@Composable
fun PantallaPapelera(nav: Nav) {
    Hoja("Papelera", nav.atras, "Lo que borraste en los últimos 7 días. Toca «recuperar» para devolverlo a su lugar; después de 7 días se vacía sola. No viaja en los respaldos ni en la exportación.") {
        ListaPapelera(privado = false)
    }
}

/** La lista de la papelera: la general (sin nada privado) o la de Privado (solo lo privado). */
@Composable
fun ListaPapelera(privado: Boolean) {
    var version by remember { mutableStateOf(0) }
    val discreto = Repo.config.discreto
    val lista by produceState(emptyList<Borrado>(), version) {
        value = withContext(Dispatchers.IO) { runCatching { Repo.papelera() }.getOrDefault(emptyList()) }.filter { Papelera.esPrivado(it) == privado }
    }
    Seccion(if (privado) "Papelera de lo privado" else "En la papelera", if (privado) "Lo privado que borraste en los últimos 7 días. Solo se ve aquí." else null,
        plegable = privado, plegadaPorDefecto = privado) {
        if (lista.isEmpty()) Vacio("Nada borrado en los últimos 7 días.")
        else Tarjeta {
            lista.forEachIndexed { i, b ->
                if (i > 0) HorizontalDivider()
                var msg by remember(b.col, b.id) { mutableStateOf<String?>(null) }
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                        Text(Papelera.describir(b, discreto), style = MaterialTheme.typography.bodyMedium)
                        Ayuda(msg ?: Papelera.diasQuedan(b).let { if (it <= 1) "se vacía mañana" else "quedan $it días" })
                    }
                    TextButton(onClick = { if (Repo.restaurar(b)) version++ else msg = "No se pudo recuperar." }) { Text("recuperar") }
                }
            }
        }
    }
}
