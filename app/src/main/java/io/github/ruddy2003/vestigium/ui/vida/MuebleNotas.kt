package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Nota
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.esOscuro

/**
 * El mueble de Notas en la pestaña Diario (9 nov 2026): el tablero del tema con hasta seis notas encima, primero las
 * fijadas y luego las más recientes. Tocar una nota (o el tablero) abre Notas.
 */
@Composable
fun TableroDeNotas(notas: List<Nota>, modifier: Modifier = Modifier, alto: androidx.compose.ui.unit.Dp = 200.dp, abrir: () -> Unit) {
    val d = disenoVida(Estilo.tema)
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val muestra = notas.filter { !(it.tipo == "recordatorio" && it.hecha) }.sortedWith(compareByDescending<Nota> { it.fijada }.thenByDescending { it.editada ?: it.creada }).take(6)
    val e = EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 3)
    BoxWithConstraints(modifier.fillMaxWidth().height(alto).clickable(onClick = abrir).semantics { contentDescription = "Notas: ${muestra.size} a la vista" }) {
        Canvas(Modifier.fillMaxSize()) { with(d) { tableroNotas(e, oscuro) } }
        val dens = LocalDensity.current
        val w = constraints.maxWidth.toFloat(); val h = constraints.maxHeight.toFloat()
        val mx = w * 0.06f; val my = h * 0.08f; val hueco = w * 0.025f
        val cw = (w - 2 * mx - 2 * hueco) / 3f; val ch = (h - 2 * my - hueco) / 2f
        muestra.forEachIndexed { i, n ->
            val x = mx + (i % 3) * (cw + hueco); val y = my + (i / 3) * (ch + hueco)
            with(dens) {
                Box(Modifier.offset(x.toDp(), y.toDp()).size(cw.toDp(), ch.toDp())) {
                    Canvas(Modifier.fillMaxSize()) {
                        with(d) { notaTablero(EstadoNota(Rect(0f, 0f, size.width, size.height), n.tipo, n.fijada, i, cs.primary, cs.surface), oscuro) }
                    }
                    Text(n.texto.trim().lines().firstOrNull().orEmpty().ifBlank { "(sin texto)" }, style = MaterialTheme.typography.labelSmall, color = d.tintaNota(oscuro),
                        maxLines = 4, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = (cw * 0.12f).toDp(), vertical = (ch * 0.16f).toDp()))
                }
            }
        }
    }
}
