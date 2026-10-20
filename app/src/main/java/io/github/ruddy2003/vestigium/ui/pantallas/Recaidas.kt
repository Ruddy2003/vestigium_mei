@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Recaidas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio

/**
 * Impulsos y recaídas: a qué hora y dónde aparecen, qué los detona, cuánto pasa entre las ganas y la recaída,
 * y cuántos impulsos pasaron sin recaer. Solo existe con el seguimiento privado; nada de aquí sale de la app.
 */
@Composable
fun PantallaRecaidas(nav: Nav) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    Hoja("Impulsos y recaídas", nav.atras, "Solo para ti. Sirve para ver en qué horas, lugares y situaciones es más difícil, no para juzgarte.") {
        if (!Repo.config.seguimiento_privado) {
            Vacio("Activa el seguimiento privado en Más → Privacidad para usar esta pantalla.")
            return@Hoja
        }
        val r = remember(Repo.momentos, Repo.dias) { Recaidas.resumen() }
        BotonPrincipal("Registrar una recaída ahora", Modifier.fillMaxWidth().padding(top = 12.dp)) {
            ctx.startActivity(android.content.Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_MOMENTO)
                .putExtra(QuickActivity.TIPO, "recaida").putExtra(QuickActivity.ORIGEN, "app"))
        }
        Ayuda("También con el botón «Recaída» del registro rápido. Las ganas que pasaron sin recaer se anotan como «Impulso».", Modifier.padding(top = 6.dp))
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra(r.diasDesdeUltima?.toString() ?: "—", "días desde la última", Modifier.weight(1f))
            Cifra("${r.rachaActual}", "días seguidos sin", Modifier.weight(1f))
            Cifra("${r.rachaMaxima}", "racha más larga", Modifier.weight(1f))
        }
        if (r.impulsos > 0) Tarjeta(Modifier.padding(top = 10.dp)) {
            val pct = r.impulsosSuperados * 100 / r.impulsos
            Text("$pct% de tus impulsos pasaron sin recaída", style = MaterialTheme.typography.titleMedium)
            Ayuda("${r.impulsosSuperados} de ${r.impulsos} impulsos registrados no terminaron en recaída en las 3 horas siguientes. Cada uno cuenta.")
        }
        if (r.minutosImpulsoARecaida.isNotEmpty()) Tarjeta(Modifier.padding(top = 10.dp)) {
            val m = r.minutosImpulsoARecaida.sorted()
            val mediana = m[m.size / 2]
            Text("Entre las ganas y la recaída: ${duracion(mediana)} (mediana)", style = MaterialTheme.typography.titleMedium)
            Ayuda("De ${m.size} recaídas con un impulso registrado antes. Ese rato es la ventana para cambiar de lugar, salir o hablar con alguien.")
        }
        Seccion("A qué hora", "Cada barra es una hora del día, de 0:00 a 23:00.") {
            Tarjeta {
                if (r.recaidasHora.sum() + r.impulsosHora.sum() == 0) Ayuda("Aún no hay impulsos ni recaídas registrados con hora.")
                else {
                    Text("Impulsos · ${r.impulsosHora.sum()}", style = MaterialTheme.typography.bodySmall)
                    Barras24(r.impulsosHora, MaterialTheme.colorScheme.secondary)
                    Text("Recaídas · ${r.recaidasHora.sum()}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    Barras24(r.recaidasHora, MaterialTheme.colorScheme.error)
                    val pico = r.recaidasHora.indices.maxByOrNull { r.recaidasHora[it] }?.takeIf { r.recaidasHora[it] > 0 }
                    if (pico != null) Ayuda("La hora más difícil: de las $pico:00 a las ${(pico + 1) % 24}:00.", Modifier.padding(top = 6.dp))
                }
            }
        }
        Seccion("Qué día de la semana") {
            Tarjeta {
                val nombres = listOf("lun", "mar", "mié", "jue", "vie", "sáb", "dom")
                val max = (r.recaidasSemana.maxOrNull() ?: 0).coerceAtLeast(1)
                nombres.forEachIndexed { i, n ->
                    Fila(n, r.recaidasSemana[i], max)
                }
            }
        }
        Seccion("Dónde, qué lo detona y qué hacías") {
            Lista("Lugares", r.lugares)
            Lista("Detonantes", r.detonantes.map { it.first.replace("_", " ") to it.second })
            Lista("Justo antes", r.antes.map { (k, v) -> (Catalogo.ANTES.firstOrNull { it.id == k }?.nombre ?: k) to v })
            Lista("Aparato", r.aparatos.map { (k, v) -> (Catalogo.DISPOSITIVOS.firstOrNull { it.id == k }?.nombre ?: k) to v })
        }
        Ayuda("${r.totalDias} días con recaída de ${r.diasConRespuesta} días con seguimiento. Para cruzarlo con sueño, pantalla o ánimo, usa Entender → Cruzar dos variables (están en el grupo «Privado»).",
            Modifier.padding(top = 12.dp))
    }
}

private fun duracion(min: Double): String = if (min < 60) "${min.toInt()} min" else "%.1f h".format(min / 60)

@Composable
private fun Barras24(v: IntArray, color: androidx.compose.ui.graphics.Color) {
    val fondo = MaterialTheme.colorScheme.surfaceVariant
    Canvas(Modifier.fillMaxWidth().aspectRatio(9f).padding(top = 4.dp)) {
        val max = (v.maxOrNull() ?: 1).coerceAtLeast(1)
        val w = size.width / 24
        for (i in 0 until 24) {
            drawRoundRect(fondo, Offset(i * w + 1, 0f), Size(w - 2, size.height), CornerRadius(3f, 3f))
            val h = size.height * v[i] / max
            if (v[i] > 0) drawRoundRect(color, Offset(i * w + 1, size.height - h), Size(w - 2, h), CornerRadius(3f, 3f))
        }
    }
}

@Composable
private fun Fila(nombre: String, n: Int, max: Int) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(nombre, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.18f))
        Canvas(Modifier.weight(0.72f).padding(vertical = 4.dp).aspectRatio(18f)) {
            drawRoundRect(cs.surfaceVariant, Offset.Zero, size, CornerRadius(4f, 4f))
            if (n > 0) drawRoundRect(cs.error.copy(alpha = 0.8f), Offset.Zero, Size(size.width * n / max, size.height), CornerRadius(4f, 4f))
        }
        Text("$n", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.1f).padding(start = 6.dp))
    }
}

@Composable
private fun Lista(titulo: String, l: List<Pair<String, Int>>) {
    Tarjeta(Modifier.padding(bottom = 8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        if (l.isEmpty()) Ayuda("Nada registrado todavía.")
        Column {
            val max = (l.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
            l.take(8).forEach { (k, v) -> Fila(k, v, max) }
        }
    }
}
