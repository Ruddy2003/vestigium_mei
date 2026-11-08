@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.ui.graphics.luminance
import io.github.ruddy2003.vestigium.ui.comun.Separador
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
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
import io.github.ruddy2003.vestigium.ui.MapasCampana
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio

/**
 * El mapa de una campaña: siete etapas unidas por un camino, sobre el mapa propio del tema. Cada día cumplido enciende su
 * etapa; el marcador del tema avanza por la ruta y, al ganar, la meta muestra la escena de la victoria del tema.
 */
@Composable
fun MapaCampana(e: Campanas.Estado, modifier: Modifier = Modifier, conNombres: Boolean = true, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    val medidor = rememberTextMeasurer()
    val estilo = MaterialTheme.typography.labelSmall.copy(color = cs.onSurfaceVariant)
    val etapas = Campanas.etapas(e.c.tema.ifBlank { tema.id })
    // El mapa es el de la campaña (el tema con que se aceptó), para que las etapas y el dibujo coincidan.
    val temaMapa = TemaId.entries.firstOrNull { it.id == e.c.tema } ?: tema
    val hoy = Fechas.diaLogico()
    val trans = rememberInfiniteTransition(label = "campana")
    val pulso by trans.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Reverse), label = "pulso")
    val animar = Repo.config.animaciones
    Canvas(modifier.fillMaxWidth().aspectRatio(2.4f)) {
        val w = size.width; val h = size.height
        // Las siete paradas del tema, donde quedan en su mapa.
        val pts = io.github.ruddy2003.vestigium.ui.MapasMas.puntos(temaMapa).map { Offset(w * it.x, h * it.y) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until 7) { val a = pts[i - 1]; val b = pts[i]; cubicTo((a.x + b.x) / 2, a.y, (a.x + b.x) / 2, b.y, b.x, b.y) } }
        with(MapasCampana) { fondo(temaMapa, cs, e.hechos / 7f); ruta(temaMapa, cs, camino) }
        // Dónde va cada nombre: se prueban abajo, arriba, a la derecha y a la izquierda, y se queda el primero que no choca
        // con otro nombre, con otra parada ni con el marcador.
        val lugaresNombre: List<Pair<androidx.compose.ui.text.TextLayoutResult, Offset>> = if (!conNombres) emptyList() else {
            val d = density
            val mk = pts[e.hechos.coerceIn(0, 6)]
            val evitar = pts.map { androidx.compose.ui.geometry.Rect(it.x - 7 * d, it.y - 7 * d, it.x + 7 * d, it.y + 7 * d) } +
                androidx.compose.ui.geometry.Rect(mk.x - 14 * d, mk.y - 36 * d, mk.x + 14 * d, mk.y + 6 * d)
            val puestos = ArrayList<androidx.compose.ui.geometry.Rect>()
            (0 until 7).map { i ->
                val p = pts[i]; val r = (if (i == 6) 9f else 6.5f) * d
                val t = medidor.measure(etapas.getOrElse(i) { "" }, estilo)
                val tw = t.size.width.toFloat(); val th = t.size.height.toFloat()
                val abajo = Offset(p.x - tw / 2, p.y + r + 2f); val arriba = Offset(p.x - tw / 2, p.y - r - th - 2f)
                val opciones = (if (p.y > h * 0.45f) listOf(abajo, arriba) else listOf(arriba, abajo)) + listOf(
                    Offset(p.x + r + 4f, p.y - th / 2), Offset(p.x - r - 4f - tw, p.y - th / 2),
                    Offset(p.x + r, p.y + r), Offset(p.x - r - tw, p.y + r), Offset(p.x + r, p.y - r - th), Offset(p.x - r - tw, p.y - r - th))
                fun caja(o: Offset): androidx.compose.ui.geometry.Rect {
                    val x = o.x.coerceIn(0f, (w - tw).coerceAtLeast(0f)); val y = o.y.coerceIn(0f, (h - th).coerceAtLeast(0f))
                    return androidx.compose.ui.geometry.Rect(x, y, x + tw, y + th)
                }
                fun choques(c: androidx.compose.ui.geometry.Rect) = puestos.count { it.overlaps(c) } * 3 + evitar.filterIndexed { k, _ -> k != i }.count { it.overlaps(c) }
                val mejor = opciones.map { caja(it) }.withIndex().minByOrNull { (k, c) -> choques(c) * 10 + k }!!.value
                puestos.add(mejor)
                t to mejor.topLeft
            }
        }
        e.dias.forEachIndexed { i, (f, ok) ->
            val p = pts[i]
            val r = (if (i == 6) 9f else 6.5f) * density
            val col = when { ok == true -> cs.primary; f == hoy -> cs.tertiary; f < hoy -> cs.error.copy(alpha = 0.55f); else -> cs.surfaceVariant }
            if (f == hoy && ok != true && animar) drawCircle(cs.tertiary.copy(alpha = 0.25f * (1 - pulso)), r + 5f * density * pulso, p)
            with(MapasCampana) { etapa(temaMapa, cs, p, r, col, i == 6) }
            if (ok == true) drawPath(Path().apply { moveTo(p.x - r * 0.45f, p.y); lineTo(p.x - r * 0.1f, p.y + r * 0.4f); lineTo(p.x + r * 0.5f, p.y - r * 0.4f) }, cs.onPrimary, style = Stroke(1f * density))
        }
        // Los nombres al final, encima de la ruta y las paradas.
        lugaresNombre.forEach { (t, o) -> drawText(t, topLeft = o) }
        // El marcador del tema avanza por la ruta con los días cumplidos (brinca si hoy falta cumplir).
        val hoyFalta = e.dias.any { it.first == hoy && it.second != true } && !e.ganada
        val dl = io.github.ruddy2003.vestigium.ui.logros.disenoLogros(temaMapa)
        val oscuroMapa = cs.background.luminance() < 0.5f
        // Ganada, en la meta va la escena de la victoria en lugar del marcador.
        if (!e.ganada) with(dl) { marcadorCampana(io.github.ruddy2003.vestigium.ui.logros.EstadoMarcador(pts[e.hechos.coerceIn(0, 6)], if (animar && hoyFalta) pulso else 0f, cs.tertiary), oscuroMapa) }
        // La escena de la meta si se ganó.
        if (e.ganada) with(dl) { victoriaCampana(io.github.ruddy2003.vestigium.ui.logros.EstadoVictoria(pts[6], cs.tertiary), oscuroMapa) }
    }
}

@Composable
fun PantallaCampanas(nav: Nav) {
    val hoy = Fechas.diaLogico()
    val lunes = Fechas.lunesDe(hoy)
    val tema = Estilo.tema
    val actuales = Campanas.deLaSemana(hoy)
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
                // Abandonar la cierra hoy, no la borra: queda en la historia (y en el panel, los días que la tuviste).
                BotonTexto(onClick = { Repo.cambiarConfig { cc -> cc.copy(campanas = cc.campanas.map { if (it.id == c.id) it.copy(abandonada = hoy) else it }) } }) {
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
                        if (i > 0) Separador()
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
                            val c = Campana(Repo.nuevoId(), lunes, id, meta, tema.id, aceptada = hoy)
                            Repo.cambiarConfig { cc -> cc.copy(campanas = cc.campanas + c) }
                            eligiendo = false; elegido = null
                        }
                        BotonTexto(onClick = { eligiendo = false; elegido = null }) { Text("cancelar") }
                    }
                }
            }
        }
        val pasadas = Repo.config.campanas.filter { it.lunes < lunes || it.abandonada != null }.sortedByDescending { it.lunes }
        if (pasadas.isNotEmpty()) Seccion("Campañas anteriores", "${Campanas.victorias(hoy).size} ganadas de ${pasadas.size}.") {
            Tarjeta {
                pasadas.take(20).forEachIndexed { i, c ->
                    if (i > 0) Separador()
                    val e = Campanas.estado(c, hoy)
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(Campanas.titulo(c.tema.ifBlank { tema.id }, c.lunes, c.reto), style = MaterialTheme.typography.bodyMedium)
                            Text("semana del ${Fechas.corto(c.lunes)} · ${e.reto?.nombre ?: c.reto}" + (c.abandonada?.let { " · abandonada el ${Fechas.corto(it)}" } ?: ""), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
