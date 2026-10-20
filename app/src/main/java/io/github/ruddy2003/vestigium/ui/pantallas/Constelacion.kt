package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Perfiles
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Una persona en el cielo: cuántos días la mencionaste, la última vez y qué es para ti. */
class Estrella(val nombre: String, val dias: Int, val ultima: String, val relacion: String?, var x: Float = 0f, var y: Float = 0f, var r: Float = 0f)

object Constelacion {
    /** Las personas mencionadas (@) en el diario y los momentos dentro del periodo, y con quién aparece cada una el mismo día. */
    fun calcular(desde: String, hoy: String = Fechas.diaLogico()): Pair<List<Estrella>, Map<Pair<String, String>, Int>> {
        val eq = Repo.equivalencias()
        val porDia = HashMap<String, MutableSet<String>>()
        fun sumar(f: String, t: String) { if (f >= desde) Texto.personas(t).forEach { porDia.getOrPut(f) { HashSet() }.add(Texto.canon(it, eq)) } }
        Repo.dias.values.forEach { d -> sumar(d.fecha, d.nota) }
        Repo.momentos.filter { it.tipo != "recaida" }.forEach { m -> sumar(m.fecha, m.texto) }
        val dias = HashMap<String, Int>(); val ultima = HashMap<String, String>()
        val juntos = HashMap<Pair<String, String>, Int>()
        porDia.forEach { (f, gente) ->
            gente.forEach { p -> dias[p] = (dias[p] ?: 0) + 1; if ((ultima[p] ?: "") < f) ultima[p] = f }
            val l = gente.sorted()
            for (i in l.indices) for (j in i + 1 until l.size) { val k = l[i] to l[j]; juntos[k] = (juntos[k] ?: 0) + 1 }
        }
        val estrellas = dias.entries.sortedByDescending { it.value }.take(80).map { (p, n) -> Estrella(p, n, ultima[p] ?: hoy, Perfiles.relacion(p)) }
        return estrellas to juntos.filterValues { it >= 2 }
    }
}

/**
 * Tu gente como un cielo: cada persona es una estrella. Más grande si la mencionas más días; más cerca del centro (tú)
 * si fue hace poco. Las que aparecen juntas el mismo día quedan unidas por líneas, como una constelación.
 * Solo cuenta menciones: nada del texto sale de aquí.
 */
@Composable
fun PantallaConstelacion(nav: Nav) {
    val hoy = Fechas.diaLogico()
    var periodo by remember { mutableStateOf(365) }
    val desde = if (periodo == 0) "0000-01-01" else Fechas.sumar(hoy, -periodo.toLong())
    val (estrellas, juntos) = remember(Repo.dias, Repo.momentos, periodo) { Constelacion.calcular(desde, hoy) }
    var elegida by remember { mutableStateOf<Estrella?>(null) }
    val cs = MaterialTheme.colorScheme
    val tonos = mapOf("familia" to cs.tertiary, "pareja" to cs.error, "amistad" to cs.secondary, "casa" to cs.primary)
    val trans = rememberInfiniteTransition(label = "cielo")
    val fase = trans.animateFloat(0f, 1f, infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "titila")
    val animar = io.github.ruddy2003.vestigium.data.Repo.config.animaciones
    val medidor = rememberTextMeasurer()
    val estiloNombre = MaterialTheme.typography.labelSmall.copy(color = cs.onSurface)

    Hoja("Constelación", nav.atras, "Tu gente como un cielo: más grande, más días mencionada; más cerca del centro, más reciente. Las líneas unen a quienes aparecen el mismo día.") {
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(90 to "3 meses", 365 to "un año", 0 to "siempre").forEach { (d, n) -> Ficha(n, periodo == d) { periodo = d; elegida = null } }
        }
        if (estrellas.isEmpty()) { Vacio("Menciona a alguien con @ en tu diario o en un momento y aparecerá aquí."); return@Hoja }
        val maxDias = estrellas.maxOf { it.dias }.toFloat()
        val diasPeriodo = if (periodo == 0) (Repo.dias.keys.minOrNull()?.let { Fechas.diasEntre(it, hoy).toFloat() } ?: 365f).coerceAtLeast(30f) else periodo.toFloat()
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f).padding(top = 10.dp).pointerInput(estrellas) {
            detectTapGestures { o -> elegida = estrellas.minByOrNull { (it.x - o.x).pow(2) + (it.y - o.y).pow(2) }?.takeIf { (it.x - o.x).pow(2) + (it.y - o.y).pow(2) < (it.r + 28f).pow(2) } }
        }) {
            val c = Offset(size.width / 2, size.height / 2)
            val radio = size.minDimension / 2
            drawCircle(Brush.radialGradient(listOf(cs.primary.copy(alpha = 0.10f), cs.surface.copy(alpha = 0f)), c, radio), radio, c)
            // Polvo de estrellas de fondo, siempre en el mismo sitio.
            val azar = java.util.Random(11)
            repeat(90) { val p = Offset(azar.nextFloat() * size.width, azar.nextFloat() * size.height); drawCircle(cs.onSurface.copy(alpha = 0.10f + azar.nextFloat() * 0.12f), 1.2f, p) }
            listOf(0.33f, 0.66f, 1f).forEach { k -> drawCircle(cs.outline.copy(alpha = 0.25f), radio * 0.94f * k, c, style = Stroke(1f)) }
            // Cada estrella: ángulo fijo por su nombre; distancia según hace cuánto apareció.
            estrellas.forEach { e ->
                val h = (e.nombre.hashCode() and 0x7fffffff)
                val ang = (h % 3600) / 3600.0 * 2 * Math.PI
                val dias = Fechas.diasEntre(e.ultima, hoy).toFloat().coerceAtLeast(0f)
                val d = (0.30f + 0.64f * (dias / diasPeriodo).coerceIn(0f, 1f).pow(0.6f)) * radio * 0.94f
                e.x = c.x + (d * cos(ang)).toFloat(); e.y = c.y + (d * sin(ang)).toFloat()
                e.r = (2.5f + 7f * sqrt(e.dias / maxDias)) * density
            }
            val pos = estrellas.associateBy { it.nombre }
            juntos.forEach { (k, n) ->
                val a = pos[k.first] ?: return@forEach; val b = pos[k.second] ?: return@forEach
                drawLine(cs.primary.copy(alpha = (0.12f + 0.06f * n).coerceAtMost(0.5f)), Offset(a.x, a.y), Offset(b.x, b.y), (0.6f + n * 0.15f).coerceAtMost(2f) * density)
            }
            // Tú, en el centro.
            drawCircle(cs.primary, 4.5f * density, c); drawCircle(cs.primary.copy(alpha = 0.3f), 9f * density, c, style = Stroke(1f * density))
            estrellas.forEachIndexed { i, e ->
                val col = tonos[e.relacion] ?: cs.onSurface
                val brillo = if (animar) 0.65f + 0.35f * sin((fase.value + i * 0.137f) * 2 * Math.PI).toFloat().let { (it + 1) / 2 } else 1f
                drawCircle(col.copy(alpha = 0.18f * brillo), e.r * 2.2f, Offset(e.x, e.y))
                drawCircle(col.copy(alpha = brillo), e.r, Offset(e.x, e.y))
                if (e === elegida) drawCircle(cs.primary, e.r + 6f, Offset(e.x, e.y), style = Stroke(2f))
                if (i < 14 || e === elegida) {
                    val t = medidor.measure("@" + e.nombre, estiloNombre)
                    drawText(t, topLeft = Offset((e.x - t.size.width / 2).coerceIn(0f, size.width - t.size.width), (e.y + e.r + 2f).coerceAtMost(size.height - t.size.height)))
                }
            }
        }
        Ayuda("Colores: " + listOf("casa" to "quien vive contigo", "familia" to "familia", "pareja" to "pareja", "amistad" to "amistades").joinToString(", ") { it.second } +
            ". Las demás, del color del texto. Las relaciones vienen de «Conocerte».", Modifier.padding(top = 6.dp))
        Tarjeta(Modifier.padding(top = 10.dp)) {
            val e = elegida
            if (e == null) Ayuda("Toca una estrella para ver a esa persona.")
            else {
                io.github.ruddy2003.vestigium.ui.TextoMarcado("@" + e.nombre, style = MaterialTheme.typography.titleMedium)
                Text("${e.dias} ${if (e.dias == 1) "día" else "días"} · la última vez ${Fechas.relativo(e.ultima)}" + (e.relacion?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                val con = juntos.filterKeys { it.first == e.nombre || it.second == e.nombre }.entries.sortedByDescending { it.value }.take(4)
                    .map { (k, n) -> (if (k.first == e.nombre) k.second else k.first) to n }
                if (con.isNotEmpty()) io.github.ruddy2003.vestigium.ui.TextoMarcado("Suele aparecer con " + con.joinToString(", ") { "@${it.first} (${it.second})" },
                    style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            }
        }
        Tarjeta(Modifier.padding(top = 10.dp)) {
            Text("Las más presentes", style = MaterialTheme.typography.titleMedium)
            estrellas.take(10).forEach { e ->
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                    io.github.ruddy2003.vestigium.ui.TextoMarcado("@" + e.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text("${e.dias} d · ${Fechas.corto(e.ultima)}", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
                }
            }
        }
    }
}
