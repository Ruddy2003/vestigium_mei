@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Luna
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Actividad
import io.github.ruddy2003.vestigium.ui.EstiloGrafica
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.IconoPista
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaMeta
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * El reporte del día (Día → «Lo que registró el teléfono»), en fichas: cada una con su ícono, una cifra grande, una nota
 * que la compara con tu normal (el promedio de las 4 semanas anteriores) y una gráfica pequeña con el estilo del tema.
 * Lo que no cabe en una ficha (obras, citas, ciudades, cartas…) sigue en líneas «clave = valor» debajo.
 */
internal class FichaDia(
    /** Ícono de [IconoPista]. */
    val icono: String,
    /** Clave latina de la ficha: su rótulo sale de `Estilo.clave`, en la jerga del tema. */
    val clave: String,
    /** Lo que mide, en español: va junto al rótulo del tema cuando este dice otra cosa («somnus · sueño»). */
    val nombre: String,
    val cifra: String,
    val nota: String?,
    val dibujo: DibujoFicha,
    /** Ocupa todo el ancho (las que tienen línea de tiempo o reparto con leyenda). */
    val ancha: Boolean = false,
)

/** La gráfica pequeña de una ficha. */
internal sealed class DibujoFicha {
    object Nada : DibujoFicha()
    /** Los últimos 7 días (hoy al final, resaltado) con la raya de tu normal. */
    class Semana(val valores: List<Double?>, val normal: Double?) : DibujoFicha()
    /** La noche entre las 18:00 y las 14:00 del día siguiente: de [ini] a [fin] (en horas desde las 18:00), y sus fases. */
    class Noche(val ini: Float?, val fin: Float?, val fases: List<Pair<String, Int>>) : DibujoFicha()
    /** Un anillo que se llena con [fraccion] (1 = tu normal; puede pasarse hasta 1.5). */
    class Anillo(val fraccion: Float) : DibujoFicha()
    /** Un rango de [min] a [max] sobre una escala de [desde] a [hasta], con [punto] marcado (la media) y [marca] (el reposo). */
    class Rango(val min: Double?, val max: Double?, val punto: Double?, val marca: Double?, val desde: Double, val hasta: Double) : DibujoFicha()
    /** Un reparto en una barra, con su leyenda. */
    class Partes(val partes: List<Pair<String, Double>>, val unidad: (Double) -> String) : DibujoFicha()
    /** La luna con su fase (0 nueva, 0.5 llena). */
    class Fase(val fase: Double) : DibujoFicha()
    /** El cielo del día según el código del tiempo: sol, nubes, lluvia, nieve o tormenta. */
    class Cielo(val codigo: Int?) : DibujoFicha()
}

private fun hm(min: Int): String = if (min >= 60) "${min / 60} h ${"%02d".format(min % 60)} min" else "$min min"

/** Promedio de los 28 días anteriores, si hay al menos 5 con dato. */
private fun normal(fecha: String, valor: (String) -> Double?): Double? =
    (1..28).mapNotNull { k -> valor(Fechas.sumar(fecha, -k.toLong())) }.takeIf { it.size >= 5 }?.average()

/** «↑ 12% vs tu normal», «= tu normal» o nada si aún no hay normal. */
private fun comparar(v: Double?, n: Double?): String? {
    if (v == null || n == null || n == 0.0) return null
    val d = (v - n) / abs(n)
    return if (abs(d) < 0.05) "= tu normal" else "${if (d > 0) "↑" else "↓"} ${"%.0f".format(abs(d) * 100)}% vs tu normal"
}

private fun semana(fecha: String, valor: (String) -> Double?) = (6 downTo 0).map { k -> valor(Fechas.sumar(fecha, -k.toLong())) }

/** Horas desde las 18:00 del día anterior hasta [iso] (la noche que termina en [fecha]). */
private fun horasNoche(fecha: String, iso: String?): Float? = runCatching {
    val t = Fechas.localDe(iso ?: return null)
    val base = Fechas.parse(fecha).minusDays(1).atTime(18, 0)
    java.time.Duration.between(base, t).toMinutes() / 60f
}.getOrNull()?.takeIf { it in 0f..20f }

/** Las fichas del día, en el orden en que se muestran. Las que no tienen dato no salen. */
internal fun fichasDelDia(fecha: String, km: Double?, canciones: Int, artista: String?): List<FichaDia> {
    val l = mutableListOf<FichaDia>()
    val s = Repo.salud[fecha]
    // Sueño: la noche dibujada de la hora en que te dormiste a la hora en que despertaste, con sus fases.
    if (s != null) s.sueno_min?.let { m ->
        val fases = listOfNotNull(s.sueno_profundo_min?.let { "profundo" to it }, s.sueno_rem_min?.let { "REM" to it },
            s.sueno_ligero_min?.let { "ligero" to it }, s.sueno_despierto_min?.takeIf { it > 0 }?.let { "despierto" to it })
        val horario = if (s.sueno_inicio != null) "${Fechas.horaDe(s.sueno_inicio)} → ${s.sueno_fin?.let { Fechas.horaDe(it) } ?: "?"}" else null
        val nota = listOfNotNull(horario, comparar(m.toDouble(), normal(fecha) { f -> Repo.salud[f]?.sueno_min?.toDouble() }),
            if (s.sueno_manual) "corregido a mano" else null, s.siesta_reloj_min?.let { "siesta ${hm(it)}" }).joinToString(" · ")
        l += FichaDia("sueno", "somnus", "sueño", hm(m), nota.ifBlank { null },
            DibujoFicha.Noche(horasNoche(fecha, s.sueno_inicio), horasNoche(fecha, s.sueno_fin), fases), ancha = true)
    }
    // Pasos: los 7 días con la raya de tu normal.
    s?.pasos?.let { p ->
        val n = normal(fecha) { f -> Repo.salud[f]?.pasos?.toDouble() }
        val nota = listOfNotNull(km?.takeIf { it >= 0.2 }?.let { "%.1f km".format(it) }, comparar(p.toDouble(), n)).joinToString(" · ")
        l += FichaDia("pasos", "passus", "pasos", "%,d".format(p), nota.ifBlank { null }, DibujoFicha.Semana(semana(fecha) { f -> Repo.salud[f]?.pasos?.toDouble() }, n))
    }
    // Corazón: el rango del día con la media y el reposo.
    if (s != null && (s.fc_media != null || s.fc_reposo != null)) {
        val principal = s.fc_reposo ?: s.fc_media!!
        val nota = listOfNotNull(if (s.fc_reposo != null) "en reposo" else "media del día", s.hrv_rmssd?.let { "variabilidad %.0f ms".format(it) },
            s.spo2?.let { "oxígeno %.0f%%".format(it) }).joinToString(" · ")
        l += FichaDia("corazon", "cor", "corazón", "%.0f lpm".format(principal), nota, DibujoFicha.Rango(s.fc_min, s.fc_max, s.fc_media, s.fc_reposo, 40.0, 180.0))
    }
    // Pantalla: el reparto entre tus apps más usadas.
    Repo.pantalla[fecha]?.let { p ->
        val top = p.por_app_min.entries.sortedByDescending { it.value }.take(3)
        val otros = p.total_min - top.sumOf { it.value }
        val partes = top.map { it.key to it.value.toDouble() } + if (otros > 0) listOf("otras" to otros.toDouble()) else emptyList()
        val nota = listOfNotNull("${p.desbloqueos} desbloqueos", comparar(p.total_min.toDouble(), normal(fecha) { f -> Repo.pantalla[f]?.total_min?.toDouble() })).joinToString(" · ")
        l += FichaDia("pantalla", "telephonum", "pantalla", hm(p.total_min), nota,
            if (partes.size >= 2) DibujoFicha.Partes(partes) { hm(it.toInt()) } else DibujoFicha.Semana(semana(fecha) { f -> Repo.pantalla[f]?.total_min?.toDouble() }, null))
    }
    // Movimiento: cómo repartiste el tiempo activo.
    Repo.actividad[fecha]?.minutos?.filterKeys { it != "quieto" }?.filterValues { it > 0 }?.takeIf { it.isNotEmpty() }?.let { m ->
        val total = m.values.sum()
        l += FichaDia("ejercicio", "motus", "movimiento", hm(total), "en movimiento",
            DibujoFicha.Partes(m.entries.sortedByDescending { it.value }.map { (Actividad.NOMBRES[it.key] ?: it.key) to it.value.toDouble() }) { hm(it.toInt()) }, ancha = true)
    }
    // Lugares: dónde pasaste el día.
    val visitas = Repo.visitas.filter { it.entrada.take(10) == fecha }
    if (visitas.isNotEmpty()) {
        val porLugar = visitas.groupBy { it.lugar }.mapValues { (_, v) -> v.sumOf { x -> ((Fechas.millis(x.salida ?: Fechas.ahoraIso()) - Fechas.millis(x.entrada)) / 60000).toInt() } }
            .entries.sortedByDescending { it.value }
        val partes = porLugar.take(4).map { (id, m) -> (Repo.lugares.firstOrNull { it.id == id }?.nombre ?: "?") to m.toDouble() }
        l += FichaDia("lugares", "loca", "lugares", "${porLugar.size} ${if (porLugar.size == 1) "lugar" else "lugares"}", null, DibujoFicha.Partes(partes) { hm(it.toInt()) }, ancha = true)
    }
    // El cielo del día.
    Repo.entorno[fecha]?.let { e ->
        val temp = if (e.t_max != null) "%.0f° / %.0f°".format(e.t_max, e.t_min ?: e.t_max) else null
        val nota = listOfNotNull(e.codigo?.let { io.github.ruddy2003.vestigium.data.Clima.descripcion(it) }, e.precip_mm?.takeIf { it > 0.05 }?.let { "lluvia %.1f mm".format(it) },
            e.luz_min?.let { "${hm(it)} de luz" }).joinToString(" · ")
        if (temp != null || e.codigo != null) l += FichaDia("clima", "caelum", "cielo", temp ?: "—", nota.ifBlank { null }, DibujoFicha.Cielo(e.codigo))
    }
    // Lo que comiste: calorías y el reparto entre proteína, carbohidratos y grasa.
    Repo.nutricion[fecha]?.valores?.takeIf { it["kcal"] != null }?.let { v ->
        val p = v["proteina_g"] ?: 0.0; val c = v["carbohidratos_g"] ?: 0.0; val g = v["grasa_g"] ?: 0.0
        val partes = listOf("proteína" to p * 4, "carbohidratos" to c * 4, "grasa" to g * 9).filter { it.second > 0 }
        l += FichaDia("comidas", "nutritio", "comida", "%,.0f kcal".format(v["kcal"]), "P %.0f · C %.0f · G %.0f g".format(p, c, g),
            if (partes.size >= 2) DibujoFicha.Partes(partes) { "%.0f kcal".format(it) } else DibujoFicha.Nada)
    }
    // Ejercicio del reloj y gimnasio.
    val ses = Repo.sesiones.filter { it.fecha == fecha }
    if (ses.isNotEmpty()) {
        val min = ses.sumOf { it.min }
        l += FichaDia("ejercicio", "exercitatio", "ejercicio", hm(min), ses.joinToString(" · ") { it.tipo_nombre }.take(60),
            DibujoFicha.Semana(semana(fecha) { f -> Repo.sesiones.filter { it.fecha == f }.sumOf { it.min }.toDouble().takeIf { it > 0 } }, null))
    }
    Repo.entrenos[fecha]?.takeIf { it.series > 0 }?.let { e ->
        l += FichaDia("gimnasio", "robur", "gimnasio", "${e.series_efectivas} series", "%,.0f kg en total".format(e.volumen_kg),
            DibujoFicha.Partes(e.series_musculo.entries.sortedByDescending { it.value }.take(4).map { it.key.replace('_', ' ') to it.value.toDouble() }) { "%.0f".format(it) })
    }
    // Llamadas y mensajes.
    Repo.comunicacion[fecha]?.let { co ->
        val ll = co.entrantes + co.salientes
        if (ll + co.perdidas > 0) l += FichaDia("voces", "voces", "llamadas", "$ll ${if (ll == 1) "llamada" else "llamadas"}",
            listOfNotNull(hm(co.min_hablado.toInt()) + " hablando", co.perdidas.takeIf { it > 0 }?.let { "$it perdidas" }).joinToString(" · "),
            DibujoFicha.Partes(listOf("entrantes" to co.entrantes.toDouble(), "salientes" to co.salientes.toDouble(), "perdidas" to co.perdidas.toDouble()).filter { it.second > 0 }) { "%.0f".format(it) })
    }
    // Música, fotos y avisos: cifras con su reparto.
    if (canciones > 0) l += FichaDia("musica", "musica", "música", "$canciones ${if (canciones == 1) "canción" else "canciones"}", artista?.let { "más: $it" }, DibujoFicha.Nada)
    Repo.fotos[fecha]?.takeIf { it.fotos + it.capturas + it.videos > 0 }?.let { f ->
        l += FichaDia("fotos", "imagines", "fotos", "${f.fotos} fotos", "${f.capturas} capturas · ${f.videos} videos",
            DibujoFicha.Partes(listOf("fotos" to f.fotos.toDouble(), "capturas" to f.capturas.toDouble(), "videos" to f.videos.toDouble()).filter { it.second > 0 }) { "%.0f".format(it) })
    }
    Repo.notifs[fecha]?.takeIf { it.total > 0 }?.let { nt ->
        val top = nt.por_app.entries.sortedByDescending { it.value }.take(3)
        l += FichaDia("avisos", "monita", "avisos", "${nt.total}", "notificaciones", DibujoFicha.Partes(top.map { it.key to it.value.toDouble() }) { "%.0f".format(it) })
    }
    // La luna, siempre.
    val fase = Luna.fase(fecha)
    l += FichaDia("luna", "luna", "luna", "${(Luna.iluminacion(fase) * 100).toInt()}%", Luna.nombre(fase), DibujoFicha.Fase(fase))
    return l
}

/** Las fichas en una rejilla de dos columnas; las anchas ocupan su propia fila. */
@Composable
internal fun RejillaFichas(fichas: List<FichaDia>) {
    val filas = remember(fichas) {
        // Las angostas van de dos en dos; si una queda sola cuando llega una ancha, la ancha espera a que llegue su pareja.
        val r = mutableListOf<List<FichaDia>>(); val par = mutableListOf<FichaDia>(); val anchas = mutableListOf<FichaDia>()
        fichas.forEach { f ->
            if (f.ancha) { if (par.isEmpty()) r += listOf(f) else anchas += f }
            else {
                par += f
                if (par.size == 2) { r += par.toList(); par.clear(); anchas.forEach { r += listOf(it) }; anchas.clear() }
            }
        }
        if (par.isNotEmpty()) r += par.toList()
        anchas.forEach { r += listOf(it) }
        r
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        filas.forEach { fila ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { FichaVista(it, Modifier.weight(1f)) }
                if (fila.size == 1 && !fila[0].ancha) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FichaVista(f: FichaDia, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    Surface(modifier, shape = MaterialTheme.shapes.small, color = cs.surface, border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline.copy(alpha = 0.7f))) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoPista(f.icono, cs.primary, tam = 22.dp, tema = tema)
                Text(rotulo(f), style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, maxLines = 1,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 6.dp).weight(1f))
            }
            Text(f.cifra, style = estiloCifra(), maxLines = 1, modifier = Modifier.padding(top = 4.dp))
            f.nota?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, maxLines = if (f.ancha) 2 else 3) }
            DibujoVista(f.dibujo, Modifier.padding(top = 6.dp))
        }
    }
}

/** El rótulo del tema y, si dice otra cosa, lo que mide en español: «somnus · sueño», «MARCHA · pasos». */
private fun rotulo(f: FichaDia): String {
    val k = Estilo.clave(f.clave)
    fun llano(x: String) = java.text.Normalizer.normalize(x.lowercase(), java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "").replace('_', ' ')
    return if (llano(k) == llano(f.nombre)) k else "$k · ${f.nombre}"
}

/** Los colores de los repartos: los del tema, del más fuerte al más tenue. */
@Composable
private fun coloresPartes(): List<Color> {
    val cs = MaterialTheme.colorScheme
    return listOf(cs.primary, cs.secondary, cs.tertiary, cs.primary.copy(alpha = 0.45f), cs.outline)
}

@Composable
private fun DibujoVista(d: DibujoFicha, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    val eg = estiloGrafica()
    when (d) {
        DibujoFicha.Nada -> Unit
        is DibujoFicha.Semana -> Canvas(modifier.fillMaxWidth().height(34.dp)) { semanaFicha(eg, d, cs.primary, cs.outline) }
        is DibujoFicha.Noche -> {
            Canvas(modifier.fillMaxWidth().height(30.dp)) { nocheFicha(eg, d, cs.primary, cs.outline, cs.onSurfaceVariant) }
            // Las horas de la pista: empieza a las 18:00 y cada marca son 2 horas (6 h hasta medianoche, 6 hasta las 6:00…).
            Row(Modifier.fillMaxWidth()) {
                listOf("18 h" to 6f, "0 h" to 6f, "6 h" to 6f, "12 h" to 2f).forEach { (h, peso) ->
                    Text(h, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, maxLines = 1, modifier = Modifier.weight(peso))
                }
            }
            if (d.fases.isNotEmpty()) Partes(d.fases.map { it.first to it.second.toDouble() }, { hm(it.toInt()) }, Modifier.padding(top = 6.dp))
        }
        is DibujoFicha.Anillo -> Canvas(modifier.size(46.dp)) { anillo(d.fraccion, cs.primary, cs.outline) }
        is DibujoFicha.Rango -> Canvas(modifier.fillMaxWidth().height(26.dp)) { rangoFicha(eg, d, cs.primary, cs.secondary, cs.outline) }
        is DibujoFicha.Partes -> Partes(d.partes, d.unidad, modifier)
        is DibujoFicha.Fase -> Canvas(modifier.size(40.dp)) { lunaFicha(d.fase, cs.primary, cs.surfaceVariant) }
        is DibujoFicha.Cielo -> Canvas(modifier.size(40.dp)) { cieloFicha(d.codigo, cs.primary, cs.secondary, cs.outline) }
    }
}

/** Un reparto: la barra partida y debajo la leyenda con lo que vale cada parte. */
@Composable
private fun Partes(partes: List<Pair<String, Double>>, unidad: (Double) -> String, modifier: Modifier = Modifier) {
    val total = partes.sumOf { it.second }.takeIf { it > 0 } ?: return
    val colores = coloresPartes()
    val eg = estiloGrafica()
    val fondo = MaterialTheme.colorScheme.surfaceVariant
    Column(modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().height(14.dp).clip(MaterialTheme.shapes.extraSmall)) {
            drawRect(fondo)
            var x = 0f
            partes.forEachIndexed { i, (_, v) ->
                val w = (size.width * (v / total)).toFloat()
                if (w > 0.5f) barraGrafica(eg, Offset(x, 0f), Size(w, size.height), colores[i % colores.size], horizontal = true)
                x += w
            }
        }
        FlowRow(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            partes.forEachIndexed { i, (nombre, v) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(colores[i % colores.size]))
                    Text(" $nombre ${unidad(v)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
            }
        }
    }
}

/** Los 7 días: hoy al final y resaltado, los días sin dato como un punto, y la raya de tu normal. */
private fun DrawScope.semanaFicha(eg: EstiloGrafica, d: DibujoFicha.Semana, color: Color, tenue: Color) {
    val max = (d.valores.filterNotNull() + listOfNotNull(d.normal)).maxOrNull()?.takeIf { it > 0 } ?: return
    val w = size.width / 7
    d.valores.forEachIndexed { i, x ->
        if (x == null) puntoGrafica(eg, Offset(i * w + w / 2, size.height - 2f), 2f, tenue)
        else {
            val h = (size.height * (x / max)).toFloat().coerceAtLeast(2f)
            barraGrafica(eg, Offset(i * w + w * 0.2f, size.height - h), Size(w * 0.6f, h), if (i == 6) color else color.copy(alpha = 0.35f))
        }
    }
    d.normal?.let { n -> val y = size.height - (size.height * (n / max)).toFloat(); lineaMeta(eg, Offset(0f, y), Offset(size.width, y), tenue) }
}

/**
 * La noche de las 18:00 a las 14:00: una pista tenue, la barra de las horas dormidas y marcas cada 2 horas, con la
 * medianoche más larga.
 */
private fun DrawScope.nocheFicha(eg: EstiloGrafica, d: DibujoFicha.Noche, color: Color, tenue: Color, tinta: Color) {
    val horas = 20f
    val y = size.height * 0.25f; val alto = size.height * 0.45f
    barraGrafica(eg, Offset(0f, y), Size(size.width, alto), tenue.copy(alpha = 0.25f), horizontal = true, pista = true)
    val ini = d.ini; val fin = d.fin
    if (ini != null && fin != null && fin > ini) {
        val x0 = size.width * ini / horas; val x1 = size.width * fin / horas
        barraGrafica(eg, Offset(x0, y), Size(x1 - x0, alto), color, horizontal = true)
    }
    for (h in 0..20 step 2) {
        val x = size.width * h / horas
        val largo = if (h == 6) alto * 1.6f else alto * 0.5f
        drawLine(tinta.copy(alpha = if (h == 6) 0.8f else 0.4f), Offset(x, size.height), Offset(x, size.height - largo * 0.6f), 1.dp.toPx())
    }
}

/** El rango del corazón: la escala, la barra de mínima a máxima, la media como punto y el reposo como marca. */
private fun DrawScope.rangoFicha(eg: EstiloGrafica, d: DibujoFicha.Rango, color: Color, acento: Color, tenue: Color) {
    fun x(v: Double) = (size.width * ((v - d.desde) / (d.hasta - d.desde))).toFloat().coerceIn(0f, size.width)
    val y = size.height / 2
    lineaMeta(eg, Offset(0f, y), Offset(size.width, y), tenue)
    if (d.min != null && d.max != null && d.max > d.min) barraGrafica(eg, Offset(x(d.min), y - 4.dp.toPx()), Size(x(d.max) - x(d.min), 8.dp.toPx()), color.copy(alpha = 0.45f), horizontal = true)
    d.marca?.let { drawLine(acento, Offset(x(it), y - 9.dp.toPx()), Offset(x(it), y + 9.dp.toPx()), 2.dp.toPx(), StrokeCap.Round) }
    d.punto?.let { puntoGrafica(eg, Offset(x(it), y), 4.dp.toPx(), color) }
}

private fun DrawScope.anillo(f: Float, color: Color, tenue: Color) {
    val grueso = size.minDimension * 0.14f
    val r = Size(size.minDimension - grueso, size.minDimension - grueso)
    val o = Offset(grueso / 2, grueso / 2)
    drawArc(tenue.copy(alpha = 0.3f), -90f, 360f, false, o, r, style = Stroke(grueso))
    drawArc(color, -90f, 360f * f.coerceIn(0f, 1.5f).coerceAtMost(1f), false, o, r, style = Stroke(grueso, cap = StrokeCap.Round))
}

/** La luna: el disco oscuro y la parte iluminada según la fase (creciente a la derecha, como se ve desde el norte). */
private fun DrawScope.lunaFicha(fase: Double, luz: Color, sombra: Color) {
    val r = size.minDimension / 2 * 0.9f
    val c = Offset(size.width / 2, size.height / 2)
    drawCircle(sombra, r, c)
    val k = cos(2 * PI * fase).toFloat()  // 1 nueva, -1 llena
    val creciente = fase < 0.5
    val camino = Path().apply {
        // El borde iluminado (medio círculo) y el terminador (una elipse de ancho |k|·r).
        val pasos = 32
        for (i in 0..pasos) {
            val a = -PI / 2 + PI * i / pasos
            val px = c.x + (if (creciente) 1 else -1) * r * cos(a).toFloat(); val py = c.y + r * sin(a).toFloat()
            if (i == 0) moveTo(px, py) else lineTo(px, py)
        }
        for (i in pasos downTo 0) {
            val a = -PI / 2 + PI * i / pasos
            val px = c.x + (if (creciente) 1 else -1) * k * r * cos(a).toFloat(); val py = c.y + r * sin(a).toFloat()
            lineTo(px, py)
        }
        close()
    }
    drawPath(camino, luz)
    drawCircle(luz.copy(alpha = 0.5f), r, c, style = Stroke(1.dp.toPx()))
}

/** El cielo: sol (0-1), sol con nube (2), nubes (3, niebla), lluvia (51-67, 80-82), nieve (71-77, 85-86) o tormenta (95-99). */
private fun DrawScope.cieloFicha(codigo: Int?, sol: Color, nube: Color, tenue: Color) {
    val w = size.width; val h = size.height
    val t = Stroke(w * 0.07f, cap = StrokeCap.Round)
    fun solo(c: Offset, r: Float) {
        drawCircle(sol, r, c)
        for (k in 0 until 8) {
            val a = Math.toRadians(k * 45.0); val x = cos(a).toFloat(); val y = sin(a).toFloat()
            drawLine(sol, Offset(c.x + x * r * 1.35f, c.y + y * r * 1.35f), Offset(c.x + x * r * 1.75f, c.y + y * r * 1.75f), t.width * 0.7f, StrokeCap.Round)
        }
    }
    fun nubeEn(c: Offset, s: Float, color: Color) {
        drawCircle(color, s * 0.26f, Offset(c.x - s * 0.22f, c.y))
        drawCircle(color, s * 0.34f, Offset(c.x + s * 0.08f, c.y - s * 0.1f))
        drawCircle(color, s * 0.24f, Offset(c.x + s * 0.36f, c.y + s * 0.04f))
        drawRect(color, Offset(c.x - s * 0.22f, c.y), Size(s * 0.58f, s * 0.24f))
    }
    val c = codigo ?: 0
    when {
        c <= 1 -> solo(Offset(w / 2, h / 2), w * 0.2f)
        c == 2 -> { solo(Offset(w * 0.36f, h * 0.36f), w * 0.15f); nubeEn(Offset(w * 0.5f, h * 0.6f), w * 0.7f, nube) }
        c in 71..77 || c in 85..86 -> {
            nubeEn(Offset(w * 0.46f, h * 0.36f), w * 0.8f, nube)
            listOf(0.3f, 0.5f, 0.7f).forEach { x -> drawCircle(tenue, w * 0.05f, Offset(w * x, h * 0.86f)) }
        }
        c in 51..67 || c in 80..82 -> {
            nubeEn(Offset(w * 0.46f, h * 0.36f), w * 0.8f, nube)
            listOf(0.3f, 0.5f, 0.7f).forEach { x -> drawLine(sol, Offset(w * x, h * 0.7f), Offset(w * x - w * 0.06f, h * 0.92f), t.width * 0.8f, StrokeCap.Round) }
        }
        c >= 95 -> {
            nubeEn(Offset(w * 0.46f, h * 0.34f), w * 0.8f, nube)
            drawPath(Path().apply { moveTo(w * 0.52f, h * 0.6f); lineTo(w * 0.4f, h * 0.8f); lineTo(w * 0.52f, h * 0.8f); lineTo(w * 0.44f, h * 0.98f) }, sol, style = t)
        }
        else -> nubeEn(Offset(w * 0.46f, h * 0.5f), w * 0.85f, nube)
    }
}
