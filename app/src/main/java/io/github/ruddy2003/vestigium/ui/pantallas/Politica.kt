@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Politica
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.TestPolitico
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import io.github.ruddy2003.vestigium.ui.rejillaGrafica
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * La brújula política anual: el test (una afirmación a la vez), dónde quedaste en cada eje, cómo te has movido
 * de un año a otro y, junto a cada año, lo que leíste, viste, escuchaste y viviste.
 */
@Composable
fun PantallaPolitica(nav: Nav) {
    val tests = Repo.config.politica.sortedBy { it.fecha }
    var haciendo by remember { mutableStateOf(false) }
    val hoy = Fechas.diaLogico()
    if (haciendo) { TestEnCurso(cerrar = { haciendo = false }); return }
    Hoja("Brújula política", nav.atras,
        "Una vez al año, 25 afirmaciones. Con los años ves cómo se mueven tus posiciones, y a un lado lo que leíste, viste y viviste ese año. " +
            "No hay respuestas correctas y nada sale del teléfono.") {
        val esteAnio = tests.lastOrNull()?.fecha?.take(4) == hoy.take(4)
        BotonPrincipal(if (tests.isEmpty()) "Hacer el test" else if (esteAnio) "Volver a hacer el de este año" else "Hacer el test de este año",
            Modifier.fillMaxWidth().padding(top = 14.dp)) { haciendo = true }
        if (tests.isEmpty()) {
            Vacio("Todavía no lo has hecho. Toma unos cinco minutos.")
            Ayuda("Las afirmaciones son de la app, no de un test publicado. En cada eje la mitad apunta a un lado y la mitad al otro, para que estar de acuerdo " +
                "con todo no te empuje hacia ninguno. El resultado describe dónde quedaste, no qué es mejor.", Modifier.padding(top = 10.dp))
            return@Hoja
        }
        val ultimo = tests.last()
        Seccion("Tu brújula", "Economía de izquierda a derecha y autoridad de abajo arriba. Cada punto es un año; la línea va del primero al último.") {
            Tarjeta { Brujula(tests) }
        }
        Seccion("Los cinco ejes", "Cada marca es un año; la grande es el más reciente (${Fechas.corto(ultimo.fecha)} ${ultimo.fecha.take(4)}).") {
            Tarjeta {
                Politica.EJES.forEachIndexed { i, e ->
                    if (i > 0) HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    EjeAnual(e, tests)
                }
            }
        }
        if (tests.size >= 2) {
            val antes = tests[tests.size - 2]
            Seccion("Qué cambió", "Del ${Fechas.larga(antes.fecha)} al ${Fechas.larga(ultimo.fecha)}.") {
                Tarjeta {
                    Politica.EJES.forEach { e ->
                        val a = antes.ejes[e.id]; val b = ultimo.ejes[e.id]
                        if (a != null && b != null) {
                            val d = b - a
                            Clave(e.nombre.lowercase(), when {
                                kotlin.math.abs(d) < 8 -> "casi igual (%+.0f)".format(d)
                                d > 0 -> "%.0f puntos hacia %s".format(d, e.mas)
                                else -> "%.0f puntos hacia %s".format(-d, e.menos)
                            }, ancho = 10)
                        }
                    }
                }
            }
        }
        Seccion("Lo que viviste cada año", "Lo que consumiste y cómo vivías en el año antes de cada test. Ver dos cosas juntas no dice que una cause la otra, pero ayuda a recordar qué pasaba.") {
            tests.reversed().forEachIndexed { i, t ->
                val previo = tests.getOrNull(tests.size - 2 - i)
                Contexto(t, previo)
            }
        }
        Ayuda("Las afirmaciones son de la app, no de un test publicado; en cada eje la mitad apunta a un lado y la mitad al otro. El resultado describe dónde quedaste, no qué es mejor.",
            Modifier.padding(top = 16.dp))
    }
}

/** El test: una afirmación a la vez, con cinco respuestas. Al terminar se guarda (uno por año). */
@Composable
private fun TestEnCurso(cerrar: () -> Unit) {
    val hoy = Fechas.diaLogico()
    val orden = remember { Politica.orden(hoy.take(4).toInt()) }
    var i by remember { mutableIntStateOf(0) }
    var resp by remember { mutableStateOf(mapOf<String, Int>()) }
    val sentir = rememberSentir()
    val fin = i >= orden.size
    Hoja("Brújula política", cerrar, if (fin) "Listo. Revisa o guarda." else "Afirmación ${i + 1} de ${orden.size}. Responde lo primero que pienses.") {
        // Barra de avance.
        Box(Modifier.fillMaxWidth().padding(top = 12.dp).height(6.dp).background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)) {
            Box(Modifier.fillMaxWidth((i.toFloat() / orden.size).coerceIn(0f, 1f)).height(6.dp).background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small))
        }
        if (!fin) {
            val a = orden[i]
            Text(a.texto, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 22.dp).heightIn(min = 110.dp))
            Politica.RESPUESTAS.forEach { (v, nombre) ->
                val sel = resp[a.id] == v
                Surface(onClick = { sentir(Sentidos.Evento.ELEGIR); resp = resp + (a.id to v); i++ }, shape = MaterialTheme.shapes.small,
                    color = if (sel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(if (sel) 2.dp else 1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text(nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { if (i > 0) i-- }, enabled = i > 0) { Text("← anterior") }
                TextButton(onClick = { resp = resp - a.id; i++ }) { Text("prefiero no responder →") }
            }
        } else {
            val ejes = Politica.ejes(resp)
            Tarjeta(Modifier.padding(top = 20.dp)) {
                Politica.EJES.forEach { e -> ejes[e.id]?.let { v -> Clave(e.nombre.lowercase(), Politica.lado(e, v), ancho = 10) } }
                Ayuda("${resp.size} de ${orden.size} respondidas.", Modifier.padding(top = 8.dp))
            }
            BotonPrincipal("Guardar el test de ${hoy.take(4)}", Modifier.fillMaxWidth().padding(top = 16.dp), habilitado = resp.isNotEmpty()) {
                val t = TestPolitico(hoy, resp, ejes)
                Repo.cambiarConfig { c -> c.copy(politica = c.politica.filter { it.fecha.take(4) != hoy.take(4) } + t) }
                sentir(Sentidos.Evento.LOGRO)
                cerrar()
            }
            TextButton(onClick = { i = orden.size - 1 }) { Text("← revisar la última") }
        }
    }
}

/** El plano economía × autoridad con un punto por año y la línea del camino. */
@Composable
private fun Brujula(tests: List<TestPolitico>) {
    val cs = MaterialTheme.colorScheme
    val medidor = rememberTextMeasurer()
    val eco = Politica.EJES.first { it.id == "economia" }; val aut = Politica.EJES.first { it.id == "autoridad" }
    val estilo = TextStyle(fontSize = 10.sp, color = cs.onSurfaceVariant)
    val estiloAnio = estiloCifras(TextStyle(fontSize = 10.sp, color = cs.primary))
    val eg = estiloGrafica()
    Canvas(Modifier.fillMaxWidth().aspectRatio(1f).padding(6.dp)) {
        val w = size.width; val h = size.height
        val m = 26.dp.toPx()
        val l = m; val r = w - m; val t = m; val b = h - m
        fun p(x: Double, y: Double) = Offset((l + (x + 100) / 200 * (r - l)).toFloat(), (b - (y + 100) / 200 * (b - t)).toFloat())
        // Cuadrantes muy tenues y la cruz.
        listOf(cs.primary.copy(alpha = 0.05f), cs.secondary.copy(alpha = 0.05f), cs.tertiary.copy(alpha = 0.05f), cs.outline.copy(alpha = 0.06f)).forEachIndexed { i, c ->
            val x0 = if (i % 2 == 0) l else (l + r) / 2; val y0 = if (i < 2) t else (t + b) / 2
            drawRect(c, Offset(x0, y0), androidx.compose.ui.geometry.Size((r - l) / 2, (b - t) / 2))
        }
        drawRect(cs.outline, Offset(l, t), androidx.compose.ui.geometry.Size(r - l, b - t), style = Stroke(1f))
        // Las guías de ±50 en los dos ejes y la cruz del centro (cada eje apunta hacia su polo «más»).
        val guias = listOf(-50.0, 50.0)
        rejillaGrafica(eg, guias.map { p(-100.0, it).y }, cs.outline.copy(alpha = 0.5f), xs = guias.map { p(it, -100.0).x },
            area = androidx.compose.ui.geometry.Rect(l, t, r, b))
        ejeGrafica(eg, Offset((l + r) / 2, b), Offset((l + r) / 2, t), cs.outline)
        ejeGrafica(eg, Offset(l, (t + b) / 2), Offset(r, (t + b) / 2), cs.outline)
        // Rótulos de los polos.
        val izq = medidor.measure(eco.menos, estilo); val der = medidor.measure(eco.mas, estilo)
        drawText(izq, topLeft = Offset(l, b + 4.dp.toPx()))
        drawText(der, topLeft = Offset(r - der.size.width, b + 4.dp.toPx()))
        val arr = medidor.measure(aut.mas, estilo); val aba = medidor.measure(aut.menos, estilo)
        drawText(arr, topLeft = Offset((w - arr.size.width) / 2, t - arr.size.height - 3.dp.toPx()))
        drawText(aba, topLeft = Offset((w - aba.size.width) / 2, b - aba.size.height - 3.dp.toPx()))
        // El camino.
        val puntos = tests.mapNotNull { x -> val e = x.ejes["economia"]; val a = x.ejes["autoridad"]; if (e != null && a != null) x.fecha to p(e, a) else null }
        if (puntos.size >= 2) {
            val camino = Path().apply { moveTo(puntos[0].second.x, puntos[0].second.y); puntos.drop(1).forEach { lineTo(it.second.x, it.second.y) } }
            lineaGrafica(eg, camino, cs.primary.copy(alpha = 0.55f), 2.dp.toPx())
        }
        puntos.forEachIndexed { i, (f, o) ->
            val ultimo = i == puntos.lastIndex
            val alfa = if (ultimo) 1f else 0.35f + 0.5f * i / puntos.size.coerceAtLeast(1)
            puntoGrafica(eg, o, if (ultimo) 8.dp.toPx() else 5.dp.toPx(), cs.primary.copy(alpha = alfa))
            if (ultimo) puntoGrafica(eg, o, 3.dp.toPx(), cs.surface)
            val et = medidor.measure(f.take(4), estiloAnio)
            drawText(et, topLeft = Offset(o.x + 8.dp.toPx(), o.y - et.size.height - 2.dp.toPx()), alpha = alfa)
        }
    }
}

/** Un eje como una regla de −100 a 100, con una marca por año. */
@Composable
private fun EjeAnual(e: Politica.Eje, tests: List<TestPolitico>) {
    val cs = MaterialTheme.colorScheme
    val medidor = rememberTextMeasurer()
    val ultimo = tests.lastOrNull()?.ejes?.get(e.id)
    val eg = estiloGrafica()
    val estiloAnio = estiloCifras(TextStyle(fontSize = 8.sp, color = cs.onSurfaceVariant))
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(e.nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            ultimo?.let { Text(Politica.lado(e, it), style = MaterialTheme.typography.labelMedium, color = cs.primary) }
        }
        Canvas(Modifier.fillMaxWidth().height(40.dp).padding(top = 6.dp)) {
            val w = size.width; val y = size.height * 0.6f
            ejeGrafica(eg, Offset(0f, y), Offset(w, y), cs.outline, marcas = listOf(0f, 0.25f, 0.5f, 0.75f, 1f).map { w * it })
            // El centro (0) conserva su raya larga que cruza el eje.
            drawLine(cs.outline, Offset(w * 0.5f, y - 8f), Offset(w * 0.5f, y + 8f), 1.5f)
            tests.forEachIndexed { i, t ->
                val v = t.ejes[e.id] ?: return@forEachIndexed
                val x = ((v + 100) / 200 * w).toFloat()
                val es = i == tests.lastIndex
                puntoGrafica(eg, Offset(x, y), if (es) 7.dp.toPx() else 4.5.dp.toPx(), cs.primary.copy(alpha = if (es) 1f else 0.3f + 0.4f * i / tests.size))
                if (!es && tests.size <= 6) {
                    val et = medidor.measure(t.fecha.take(4), estiloAnio)
                    drawText(et, topLeft = Offset((x - et.size.width / 2).coerceIn(0f, w - et.size.width), 0f))
                }
            }
        }
        Row {
            Text(e.menos, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            Text(e.mas, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
        }
    }
}

/** Lo que consumiste y cómo vivías en el año antes de un test. */
@Composable
private fun Contexto(t: TestPolitico, previo: TestPolitico?) {
    val ctx = LocalContext.current
    val desde = previo?.fecha?.let { Fechas.sumar(it, 1) } ?: Fechas.sumar(t.fecha, -364)
    val lineas by produceState<List<Pair<String, String>>?>(null, t.fecha, desde) {
        value = withContext(Dispatchers.IO) { runCatching { contextoDe(ctx, desde, t.fecha) }.getOrDefault(emptyList()) }
    }
    Tarjeta(Modifier.padding(bottom = 10.dp)) {
        Text("Test del ${Fechas.larga(t.fecha)}", style = MaterialTheme.typography.titleMedium)
        Ayuda("Del ${Fechas.larga(desde)} a ese día")
        val l = lineas
        when {
            l == null -> Ayuda("Juntando…", Modifier.padding(top = 6.dp))
            l.isEmpty() -> Ayuda("No hay datos de ese periodo.", Modifier.padding(top = 6.dp))
            else -> Column(Modifier.padding(top = 6.dp)) { l.forEach { (k, v) -> Clave(k, v, ancho = 9) } }
        }
    }
}

private fun contextoDe(ctx: Context, desde: String, hasta: String): List<Pair<String, String>> {
    val out = ArrayList<Pair<String, String>>()
    fun en(f: String?) = f != null && f in desde..hasta
    val obras = Repo.obras.filter { it.estado == "terminado" && en(it.fin) }
    if (obras.isNotEmpty()) {
        val porTipo = obras.groupingBy { it.tipo }.eachCount()
        out += "obras" to listOf("libro" to "libros", "pelicula" to "películas", "serie" to "series", "juego" to "juegos", "articulo" to "artículos")
            .mapNotNull { (id, n) -> porTipo[id]?.let { "$it $n" } }.joinToString(" · ")
        obras.filter { it.calificacion != null }.sortedByDescending { it.calificacion }.take(3).takeIf { it.isNotEmpty() }?.let { l ->
            out += "lo mejor" to l.joinToString(" · ") { "${it.titulo} (${it.calificacion}/10)" }
        }
        obras.flatMap { it.etiquetas }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(6).takeIf { it.isNotEmpty() }?.let { l ->
            out += "géneros" to l.joinToString(", ") { it.key }
        }
    }
    val hu = Repo.huellas.filterKeys { it in desde..hasta }.values
    if (hu.isNotEmpty()) {
        val canales = HashMap<String, Int>(); hu.forEach { h -> h.canales.forEach { (k, v) -> canales.merge(k, v, Int::plus) } }
        out += "youtube" to "%,d videos".format(hu.sumOf { it.yt_vistos }) + (canales.entries.sortedByDescending { it.value }.take(4).takeIf { it.isNotEmpty() }?.let { l -> " · " + l.joinToString(", ") { it.key } } ?: "")
        out += "búsquedas" to "%,d en Google".format(hu.sumOf { it.busquedas })
    }
    val apps = HashMap<String, Int>(); Repo.pantalla.filterKeys { it in desde..hasta }.values.forEach { p -> p.por_app_min.forEach { (k, v) -> apps.merge(k, v, Int::plus) } }
    apps.entries.sortedByDescending { it.value }.take(4).takeIf { it.isNotEmpty() }?.let { l -> out += "apps" to l.joinToString(" · ") { "${it.key} ${it.value / 60} h" } }
    runCatching {
        val esc = Repo.escuchas(desde, hasta)
        esc.groupingBy { it.artista.ifBlank { "?" } }.eachCount().entries.filter { it.key != "?" }.sortedByDescending { it.value }.take(4).takeIf { it.isNotEmpty() }?.let { l ->
            out += "música" to l.joinToString(", ") { it.key }
        }
    }
    val dias = Repo.dias.filterKeys { it in desde..hasta }.values
    dias.mapNotNull { it.valencia }.takeIf { it.size >= 5 }?.let { out += "ánimo" to "%.1f de 7 en promedio (%d días)".format(it.average(), it.size) }
    Repo.salud.filterKeys { it in desde..hasta }.values.mapNotNull { it.sueno_min }.takeIf { it.size >= 5 }?.let { out += "sueño" to "%.1f h por noche".format(it.average() / 60) }
    val ejercicio = Repo.sesiones.filter { it.fecha in desde..hasta }.map { it.fecha }.toSet().size + Repo.ascensos.filter { it.fecha in desde..hasta }.map { it.fecha }.toSet().size
    if (ejercicio > 0) out += "ejercicio" to "$ejercicio días"
    val eq = Repo.equivalencias()
    dias.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct() }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(5)
        .takeIf { it.isNotEmpty() }?.let { l -> out += "personas" to l.joinToString(", ") { "@${it.key}" } }
    runCatching {
        val d0 = Fechas.parse(desde).toEpochDay().toInt(); val d1 = Fechas.parse(hasta).toEpochDay().toInt()
        val r = Mundo.resumen(ctx, d0)
        val paises = r.paises.filter { e -> e.dias.any { it in d0..d1 } }
        if (paises.isNotEmpty()) out += "países" to paises.take(8).joinToString(" ") { Mundo.bandera(it.clave) }
    }
    return out
}
