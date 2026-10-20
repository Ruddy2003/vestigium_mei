@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Campanas
import io.github.ruddy2003.vestigium.data.Campana
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio

/**
 * El mapa de una campaña: siete etapas unidas por un camino. Cada día cumplido enciende su etapa; la meta lleva el
 * emblema del tema. El camino es una vía en los templos, un sendero punteado en los castillos, un circuito en los
 * laboratorios y un riel en los talleres.
 */
@Composable
fun MapaCampana(e: Campanas.Estado, modifier: Modifier = Modifier, conNombres: Boolean = true, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    val medidor = rememberTextMeasurer()
    val estilo = MaterialTheme.typography.labelSmall.copy(color = cs.onSurfaceVariant)
    val etapas = Campanas.etapas(e.c.tema.ifBlank { tema.id })
    val hoy = Fechas.diaLogico()
    val trans = rememberInfiniteTransition(label = "campana")
    val pulso by trans.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Reverse), label = "pulso")
    val animar = Repo.config.animaciones
    Canvas(modifier.fillMaxWidth().aspectRatio(2.4f)) {
        val w = size.width; val h = size.height
        // Siete puntos en zigzag, de izquierda a derecha.
        val pts = (0 until 7).map { i -> Offset(w * (0.07f + 0.86f * i / 6f), h * (if (i % 2 == 0) 0.32f else 0.62f)) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until 7) { val a = pts[i - 1]; val b = pts[i]; cubicTo((a.x + b.x) / 2, a.y, (a.x + b.x) / 2, b.y, b.x, b.y) } }
        val salon = salonDe(tema)
        when (salon) {
            Salon.TEMPLO -> { drawPath(camino, cs.outline.copy(alpha = 0.5f), style = Stroke(9f)); drawPath(camino, cs.surface, style = Stroke(5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))) }
            Salon.CASTILLO -> drawPath(camino, cs.onSurfaceVariant.copy(alpha = 0.6f), style = Stroke(3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 8f))))
            Salon.LABORATORIO -> { drawPath(camino, cs.primary.copy(alpha = 0.25f), style = Stroke(8f)); drawPath(camino, cs.primary, style = Stroke(1.5f)) }
            Salon.TALLER -> { drawPath(camino, Color(0xFF8A6A44).copy(alpha = 0.6f), style = Stroke(7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 6f)))); drawPath(camino, cs.onSurfaceVariant.copy(alpha = 0.5f), style = Stroke(1.5f)) }
        }
        e.dias.forEachIndexed { i, (f, ok) ->
            val p = pts[i]
            val r = if (i == 6) 13f else 9f
            val col = when { ok == true -> cs.primary; f == hoy -> cs.tertiary; f < hoy -> cs.error.copy(alpha = 0.55f); else -> cs.surfaceVariant }
            if (f == hoy && ok != true && animar) drawCircle(cs.tertiary.copy(alpha = 0.25f * (1 - pulso)), r + 10f * pulso, p)
            when (salon) {
                Salon.CASTILLO -> drawRect(col, Offset(p.x - r, p.y - r), androidx.compose.ui.geometry.Size(r * 2, r * 2))
                Salon.LABORATORIO -> drawPath(Path().apply { for (k in 0 until 6) { val a = k * Math.PI / 3; val x = p.x + (r * kotlin.math.cos(a)).toFloat(); val y = p.y + (r * kotlin.math.sin(a)).toFloat(); if (k == 0) moveTo(x, y) else lineTo(x, y) }; close() }, col)
                else -> drawCircle(col, r, p)
            }
            drawCircle(cs.outline, r, p, style = Stroke(1.2f))
            if (ok == true) drawPath(Path().apply { moveTo(p.x - r * 0.45f, p.y); lineTo(p.x - r * 0.1f, p.y + r * 0.4f); lineTo(p.x + r * 0.5f, p.y - r * 0.4f) }, cs.onPrimary, style = Stroke(2.2f))
            if (conNombres) {
                val t = medidor.measure(etapas.getOrElse(i) { "" }, estilo)
                val y = if (i % 2 == 0) p.y - r - t.size.height - 2f else p.y + r + 2f
                drawText(t, topLeft = Offset((p.x - t.size.width / 2).coerceIn(0f, w - t.size.width), y.coerceIn(0f, h - t.size.height)))
            }
        }
        // La bandera de la meta si se ganó.
        if (e.ganada) {
            val m = pts[6]
            drawLine(cs.onSurface, m, m + Offset(0f, -32f), 2f)
            drawPath(Path().apply { moveTo(m.x, m.y - 32f); lineTo(m.x + 20f, m.y - 26f); lineTo(m.x, m.y - 20f); close() }, cs.primary)
        }
    }
}

@Composable
fun PantallaCampanas(nav: Nav) {
    val hoy = Fechas.diaLogico()
    val lunes = Fechas.lunesDe(hoy)
    val tema = Estilo.tema
    val actuales = Repo.config.campanas.filter { it.lunes == lunes }
    var eligiendo by remember { mutableStateOf(false) }
    Hoja("Campañas", nav.atras, "Retos opcionales de lunes a domingo. Cada día que cumples avanzas una etapa; al llegar a la meta, ganas la campaña.") {
        if (actuales.isEmpty() && !eligiendo) Vacio("Esta semana no tienes campañas. Elige hasta tres retos: dormir, moverte, escribir, enfocarte, leer…")
        actuales.forEach { c ->
            val e = remember(c, Repo.dias, Repo.salud, Repo.tramos, Repo.pantalla) { Campanas.estado(c, hoy) }
            Tarjeta(Modifier.padding(top = 10.dp)) {
                Text(Campanas.titulo(c.tema.ifBlank { tema.id }, c.lunes, c.reto), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text("${e.reto?.nombre ?: c.reto} · ${c.meta} de 7 días", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                MapaCampana(e, Modifier.padding(top = 8.dp))
                Text(when {
                    e.ganada -> "¡Campaña ganada" + (e.ganadaEl?.let { " el ${Fechas.titulo(it).lowercase()}" } ?: "") + "!"
                    e.perdida -> "Esta ya no se alcanza. La próxima semana se vuelve a intentar."
                    else -> "${e.hechos} de ${c.meta}. " + (e.reto?.descripcion ?: "")
                }, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                TextButton(onClick = { Repo.cambiarConfig { cc -> cc.copy(campanas = cc.campanas - c) } }) {
                    Text("abandonar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (actuales.size < 3) {
            if (!eligiendo) BotonPrincipal("+ Elegir una campaña", Modifier.fillMaxWidth().padding(top = 12.dp)) { eligiendo = true }
            else Seccion("Elegir campaña", "La meta es cuántos días de los siete. Puedes cambiarla antes de aceptar.") {
                var meta by remember { mutableIntStateOf(0) }
                var elegido by remember { mutableStateOf<String?>(null) }
                Tarjeta {
                    Campanas.retos().filter { r -> actuales.none { it.reto == r.id } }.forEachIndexed { i, r ->
                        if (i > 0) HorizontalDivider()
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(r.nombre, style = MaterialTheme.typography.bodyLarge)
                                Text(r.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Ficha(if (elegido == r.id) "✓" else "elegir", elegido == r.id) { elegido = r.id; meta = r.meta }
                        }
                    }
                }
                elegido?.let { id ->
                    Text("Meta: $meta de 7 días", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { (3..7).forEach { m -> Ficha("$m", meta == m) { meta = m } } }
                    Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BotonPrincipal("Aceptar la campaña") {
                            val c = Campana(Repo.nuevoId(), lunes, id, meta, tema.id)
                            Repo.cambiarConfig { cc -> cc.copy(campanas = cc.campanas + c) }
                            eligiendo = false; elegido = null
                        }
                        TextButton(onClick = { eligiendo = false; elegido = null }) { Text("cancelar") }
                    }
                }
            }
        }
        val pasadas = Repo.config.campanas.filter { it.lunes < lunes }.sortedByDescending { it.lunes }
        if (pasadas.isNotEmpty()) Seccion("Campañas anteriores", "${Campanas.victorias(hoy).size} ganadas de ${Repo.config.campanas.count { it.lunes < lunes }}.") {
            Tarjeta {
                pasadas.take(20).forEachIndexed { i, c ->
                    if (i > 0) HorizontalDivider()
                    val e = Campanas.estado(c, hoy)
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(Campanas.titulo(c.tema.ifBlank { tema.id }, c.lunes, c.reto), style = MaterialTheme.typography.bodyMedium)
                            Text("semana del ${Fechas.corto(c.lunes)} · ${e.reto?.nombre ?: c.reto}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(if (e.ganada) "✓ ${e.hechos}/${c.meta}" else "${e.hechos}/${c.meta}", style = MaterialTheme.typography.labelLarge,
                            color = if (e.ganada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Ayuda("Las campañas ganadas cuentan para los logros de constancia.", Modifier.padding(top = 10.dp))
    }
}

/** En Más: la campaña de esta semana con su mapa. */
@Composable
fun RecuadroCampanas(nav: Nav) {
    val hoy = Fechas.diaLogico()
    val c = Campanas.deLaSemana(hoy).firstOrNull()
    val e = c?.let { remember(it, Repo.dias, Repo.salud, Repo.tramos) { Campanas.estado(it, hoy) } }
    Recuadro("Campañas", if (e == null) "Retos de la semana con su mapa" else "${Campanas.titulo(c.tema.ifBlank { Estilo.tema.id }, c.lunes, c.reto)} · ${e.hechos}/${c.meta}",
        alto = 112.dp, onClick = { nav.ir("campanas") }) {
        if (e != null) MapaCampana(e, Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 6.dp), conNombres = false)
        else Text("elige un reto para esta semana", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.Center))
    }
}
