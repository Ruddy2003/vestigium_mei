@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.porNumero

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Analisis
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.ciencia.Variable
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.lineaMeta
import io.github.ruddy2003.vestigium.ui.pincelDeCifras
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import io.github.ruddy2003.vestigium.ui.rejillaGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import io.github.ruddy2003.vestigium.ui.Dialogo

/** Herramienta que se abre (la elige Explorar). */
object Herramienta {
    var actual by mutableStateOf("tendencia")
    /** Con qué abrir «Todo lo que se mueve con algo» (una persona, un lugar…), si se llega desde otra pantalla. */
    var objetivo by mutableStateOf<String?>(null)
    /** Si la lista de relaciones de donde se llegó quitaba tendencias: «Todo lo que se mueve» empieza igual. */
    var sinTendencia by mutableStateOf(true)
}

val HERRAMIENTAS = listOf(
    Triple("tendencia", "Tendencia", "Cómo cambia algo con el tiempo, con medias móviles"),
    Triple("semana", "Día de la semana", "Si una variable cambia según el día"),
    Triple("factores", "Todo lo que se mueve con algo", "Elige cualquier cosa (tu ánimo, una persona, un lugar, una app, un nutriente, una etapa de tu vida) y ve qué la acompaña"),
    Triple("modelo", "Modelo múltiple", "Varias causas posibles a la vez, controlando el día anterior"),
    Triple("dinamica", "Dinámica emocional", "Variabilidad, inestabilidad e inercia de tu ánimo"),
    Triple("ritmo", "Ritmo de sueño", "Horarios, regularidad y jet lag social"),
    Triple("palabras", "Palabras del diario", "Qué palabras aparecen en tus días buenos y difíciles"),
    Triple("atipicos", "Días atípicos", "Días que se salieron de lo normal"),
)

private val LATIN_H = mapOf("tendencia" to "TENDENTIA", "semana" to "HEBDOMAS", "factores" to "CAVSAE", "modelo" to "MODELVS",
    "dinamica" to "MOTVS ANIMI", "ritmo" to "RHYTHMVS", "palabras" to "VERBA", "atipicos" to "PRODIGIA")

private fun fmt(v: Double): String = if (abs(v) >= 1000) "%,.0f".format(v) else if (abs(v) >= 10) "%.1f".format(v) else "%.2f".format(v)
private fun hora(h: Double): String { val x = ((h % 24) + 24) % 24; return "%d:%02d".format(x.toInt(), ((x - x.toInt()) * 60).toInt()) }

@Composable
fun PantallaAnalisis(nav: Nav) {
    val version = Repo.dias.size + Repo.momentos.size + Repo.salud.size + Repo.pantalla.size + Repo.gastos.size + Repo.entorno.size
    var panel by remember { mutableStateOf<Map<String, Map<String, Double?>>?>(null) }
    var vars by remember { mutableStateOf<List<Variable>>(emptyList()) }
    LaunchedEffect(version) {
        val (p, v) = withContext(Dispatchers.Default) { Estadistica.panel() to Estadistica.variables() }
        panel = p; vars = v
    }
    var eligiendo by remember { mutableStateOf<((String) -> Unit)?>(null) }
    val h = Herramienta.actual
    val info = HERRAMIENTAS.first { it.first == h }

    // Cada herramienta con su nombre (N-23).
    Hoja(info.second, nav.atras, subtitulo = "Pistas para entenderte. Ninguna prueba causas: para eso están los experimentos personales y los modelos en R.", latin = LATIN_H[h] ?: "ANALYSIS") {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HERRAMIENTAS.forEach { (id, n, _) -> Ficha(n, id == h) { Herramienta.actual = id } }
        }
        val p = panel
        if (p == null) { Ayuda("Calculando…", Modifier.padding(top = 12.dp)); return@Hoja }
        Seccion(info.second, info.third, latin = LATIN_H[h]) {
            when (h) {
                "tendencia" -> Tendencia(p, vars) { eligiendo = it }
                "semana" -> Semana(p, vars) { eligiendo = it }
                "factores" -> Factores(p, vars) { eligiendo = it }
                "modelo" -> ModeloUI(p, vars) { eligiendo = it }
                "dinamica" -> Dinamica()
                "ritmo" -> Ritmo()
                "palabras" -> Palabras()
                else -> Atipicos(p, vars, nav)
            }
        }
    }

    eligiendo?.let { cb -> ElegirVariable(vars, { eligiendo = null }) { cb(it); eligiendo = null } }
}

/** Elegir una variable entre cientos: buscador y grupos. */
@Composable
internal fun ElegirVariable(vars: List<Variable>, cerrar: () -> Unit, elegir: (String) -> Unit) {
    var q by remember { mutableStateOf("") }
    var grupo by remember { mutableStateOf<String?>(null) }
    fun normal(x: String) = java.text.Normalizer.normalize(x.lowercase(), java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
    val grupos = remember(vars) { vars.map { it.grupo }.distinct() }
    val lista = vars.filter { (grupo == null || it.grupo == grupo) && (q.isBlank() || normal(it.nombre + " " + it.grupo + " " + it.id).contains(normal(q.trim()))) }
    Dialogo(
        onDismissRequest = cerrar,
        title = { Text("Variable · ${vars.size}", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                io.github.ruddy2003.vestigium.ui.comun.CampoTexto(q, { q = it }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("buscar: @ana, sueño, instagram, hierro…") })
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("todas", grupo == null) { grupo = null }
                    grupos.forEach { g -> Ficha(g.lowercase(), grupo == g) { grupo = if (grupo == g) null else g } }
                }
                LazyColumn(Modifier.heightIn(max = 400.dp)) {
                    items(lista) { v ->
                        Column(Modifier.fillMaxWidth().clickable { elegir(v.id) }.padding(vertical = 8.dp)) {
                            Text(v.nombre, style = MaterialTheme.typography.bodyMedium)
                            Text(v.grupo.lowercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = cerrar) { Text("cerrar", style = MaterialTheme.typography.labelLarge) } },
    )
}

@Composable
private fun Selector(etiqueta: String, id: String, vars: List<Variable>, elegir: (((String) -> Unit)) -> Unit, poner: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$etiqueta = ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        BotonSecundario((vars.firstOrNull { it.id == id }?.nombre ?: id) + " ▾") { elegir(poner) }
    }
}

// ---------------- Tendencia ----------------
@Composable
private fun Tendencia(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, elegir: (((String) -> Unit)) -> Unit) {
    var v by remember { mutableStateOf("valencia") }
    Selector("variable", v, vars, elegir) { v = it }
    val s = Analisis.serie(panel, v)
    if (s.size < 5) { Vacio("Hay ${porNumero(s.size, "1 día", "${s.size} días")} con este dato. Con 5 o más aparece la gráfica."); return }
    val m7 = Analisis.movil(s, 7); val m30 = Analisis.movil(s, 30)
    val c1 = MaterialTheme.colorScheme.outline; val c2 = MaterialTheme.colorScheme.secondary; val c3 = MaterialTheme.colorScheme.primary
    val eg = estiloGrafica()
    val pin = pincelDeCifras(MaterialTheme.colorScheme.onSurfaceVariant, 24f)
    val ultimos = s.takeLast(365)
    Canvas(Modifier.fillMaxWidth().height(200.dp).padding(top = 10.dp)) {
        val ini = Fechas.parse(ultimos.first().first).toEpochDay(); val fin = Fechas.parse(ultimos.last().first).toEpochDay().coerceAtLeast(ini + 1)
        val ys = ultimos.map { it.second }; val lo = ys.min(); val hi = ys.max().let { if (it == lo) lo + 1 else it }
        val izq = 60f
        fun px(f: String) = izq + (Fechas.parse(f).toEpochDay() - ini).toFloat() / (fin - ini) * (size.width - izq)
        fun py(y: Double) = (size.height - 20f - (y - lo) / (hi - lo) * (size.height - 30f)).toFloat()
        val guias = listOf(lo, (lo + hi) / 2, hi)
        rejillaGrafica(eg, guias.map { py(it) }, c1, area = Rect(izq, py(hi), size.width, py(lo)))
        ejeGrafica(eg, Offset(izq, py(lo)), Offset(size.width, py(lo)), c1)
        guias.forEach { y -> drawContext.canvas.nativeCanvas.drawText(fmt(y), 0f, py(y) + 8f, pin) }
        ultimos.forEach { (f, y) -> puntoGrafica(eg, Offset(px(f), py(y)), 4f, c1) }
        // Solo se unen días seguidos: un día sin dato corta la línea (null).
        fun linea(l: List<Pair<String, Double>>, color: androidx.compose.ui.graphics.Color, w: Float) {
            val r = l.filter { Fechas.parse(it.first).toEpochDay() >= ini }
            val puntos = ArrayList<Offset?>()
            for (i in r.indices) {
                if (i > 0 && Fechas.parse(r[i].first).toEpochDay() - Fechas.parse(r[i - 1].first).toEpochDay() != 1L) puntos.add(null)
                puntos.add(Offset(px(r[i].first), py(r[i].second)))
            }
            lineaGrafica(eg, puntos, color, w)
        }
        linea(m7, c2, 3f); linea(m30, c3, 6f)
    }
    Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("· día", style = MaterialTheme.typography.labelSmall, color = c1); Text("— 7 días", style = MaterialTheme.typography.labelSmall, color = c2)
        Text("━ 30 días", style = MaterialTheme.typography.labelSmall, color = c3)
    }
    Analisis.cambio(s)?.let { c ->
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra(fmt(c.reciente), "últimos 30 días", Modifier.weight(1f)); Cifra(fmt(c.anterior), "30 anteriores", Modifier.weight(1f))
            Cifra((if (c.dif >= 0) "+" else "") + fmt(c.dif), "cambio", Modifier.weight(1f))
        }
        Ayuda(if (c.lo > 0 || c.hi < 0) "El cambio es claro: el intervalo de 95% (${fmt(c.lo)} a ${fmt(c.hi)}) no incluye el cero."
        else "El intervalo de 95% (${fmt(c.lo)} a ${fmt(c.hi)}) incluye el cero: podría ser variación normal. (${c.nA} y ${porNumero(c.nB, "1 día", "${c.nB} días")})", Modifier.padding(top = 6.dp))
    }
}

// ---------------- Día de la semana ----------------
@Composable
private fun Semana(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, elegir: (((String) -> Unit)) -> Unit) {
    var v by remember { mutableStateOf("valencia") }
    Selector("variable", v, vars, elegir) { v = it }
    val s = Analisis.serie(panel, v)
    val g = Analisis.porDiaSemana(s)
    if (g.count { it != null } < 3) { Vacio("Aún no hay suficientes semanas con este dato."); return }
    val col = MaterialTheme.colorScheme.primary; val err = MaterialTheme.colorScheme.onSurface; val fondo = MaterialTheme.colorScheme.surfaceVariant
    val lo = g.filterNotNull().minOf { it.lo }; val hi = g.filterNotNull().maxOf { it.hi }.let { if (it == lo) lo + 1 else it }
    val base = if (lo > 0 && lo / hi > 0.5) lo - (hi - lo) * 0.3 else minOf(0.0, lo)
    val eg = estiloGrafica()
    Canvas(Modifier.fillMaxWidth().height(170.dp).padding(top = 10.dp)) {
        val w = size.width / 7
        fun py(y: Double) = (size.height - (y - base) / (hi - base) * size.height).toFloat()
        g.forEachIndexed { i, x ->
            barraGrafica(eg, Offset(i * w + 6, 0f), Size(w - 12, size.height), fondo, pista = true)
            if (x != null) {
                barraGrafica(eg, Offset(i * w + 6, py(x.media)), Size(w - 12, size.height - py(x.media)), col)
                lineaGrafica(eg, listOf(Offset(i * w + w / 2, py(x.lo)), Offset(i * w + w / 2, py(x.hi))), err, 3f)
            }
        }
    }
    Row(Modifier.fillMaxWidth()) { io.github.ruddy2003.vestigium.ui.Estilo.semana.forEach { Text(it, Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center) } }
    val mejor = g.withIndex().filter { it.value != null }.maxByOrNull { it.value!!.media }; val peor = g.withIndex().filter { it.value != null }.minByOrNull { it.value!!.media }
    if (mejor != null && peor != null) Ayuda("Más alto: ${io.github.ruddy2003.vestigium.ui.Estilo.semana[mejor.index]} (${fmt(mejor.value!!.media)}); más bajo: ${io.github.ruddy2003.vestigium.ui.Estilo.semana[peor.index]} (${fmt(peor.value!!.media)}). " +
        if (mejor.value!!.lo > peor.value!!.hi) "Los intervalos no se enciman: la diferencia parece real." else "Los intervalos se enciman: podría ser azar.", Modifier.padding(top = 8.dp))
}

// ---------------- Factores ----------------
@Composable
private fun Factores(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, elegir: (((String) -> Unit)) -> Unit) {
    var objetivo by remember { mutableStateOf(Herramienta.objetivo?.takeIf { o -> vars.any { it.id == o } } ?: "valencia") }
    var desfase by remember { mutableStateOf(false) }
    var sinTendencia by remember { mutableStateOf(Herramienta.sinTendencia) }
    var lista by remember { mutableStateOf<List<Analisis.Factor>?>(null) }
    LaunchedEffect(objetivo, desfase, sinTendencia, panel) { lista = withContext(Dispatchers.Default) { Analisis.factores(panel, vars, objetivo, desfase, sinTendencia) } }
    Selector("objetivo", objetivo, vars, elegir) { objetivo = it }
    Interruptor("Medir el objetivo al día siguiente", desfase, "¿Lo de hoy se asocia con cómo estás mañana? Así se evita que el ánimo cause lo otro.") { desfase = it }
    Interruptor("Quitar tendencias", sinTendencia, "Como en «Las relaciones más marcadas»: cada día se compara con sus ~3 semanas alrededor.") { sinTendencia = it; Herramienta.sinTendencia = it }
    val l = lista
    if (l == null) { Ayuda("Calculando…"); return }
    if (l.isEmpty()) { Vacio("Hacen falta al menos 14 días con datos en común."); return }
    val firmes = l.count { it.q < 0.1 }
    Aviso("Revisé ${l.size} variables. $firmes resisten la corrección por comparaciones múltiples (Benjamini-Hochberg, q < 0.1): esas son las pistas más confiables.")
    val c = MaterialTheme.colorScheme.primary; val c2 = MaterialTheme.colorScheme.outline
    val eg = estiloGrafica()
    Tarjeta(Modifier.padding(top = 8.dp)) {
        l.take(20).forEachIndexed { i, f ->
            if (i > 0) HorizontalDivider()
            Column(Modifier.padding(vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(f.v.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    if (f.q < 0.1) Text("✓", style = MaterialTheme.typography.labelLarge, color = c)
                }
                val texto = if (f.binaria) "%s%.2f puntos los días con (%d) frente a sin (%d)".format(if (f.efecto >= 0) "+" else "", f.efecto, f.nCon, f.nSin)
                else "r = %+.2f · %s · %d días".format(f.efecto, Estadistica.fuerza(f.efecto), f.n) + if (f.nEf < f.n * 0.8) " (≈ ${f.nEf.toInt()} independientes)" else ""
                Text(texto, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                // Intervalo como barra: el centro es cero.
                val escala = if (f.binaria) maxOf(1.0, l.filter { it.binaria }.maxOfOrNull { maxOf(abs(it.lo), abs(it.hi)) } ?: 1.0) else 1.0
                Canvas(Modifier.fillMaxWidth().height(12.dp).padding(top = 4.dp)) {
                    val mid = size.width / 2
                    fun px(x: Double) = (mid + x / escala * mid).toFloat().coerceIn(0f, size.width)
                    lineaMeta(eg, Offset(mid, 0f), Offset(mid, size.height), c2, 1f)
                    lineaGrafica(eg, listOf(Offset(px(f.lo), size.height / 2), Offset(px(f.hi), size.height / 2)), if (f.q < 0.1) c else c2, 4f)
                    puntoGrafica(eg, Offset(px(f.efecto), size.height / 2), 5f, if (f.q < 0.1) c else c2)
                }
            }
        }
    }
    Ayuda("La barra es el intervalo de 95%; si cruza la línea del centro, podría ser azar. Cuenta que los días seguidos se parecen: no cada día es una prueba nueva. Que dos cosas vayan juntas no prueba que una cause la otra.", Modifier.padding(top = 6.dp))
}

// ---------------- Modelo ----------------
@Composable
private fun ModeloUI(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, elegir: (((String) -> Unit)) -> Unit) {
    var objetivo by remember { mutableStateOf("valencia") }
    var pred by remember { mutableStateOf(listOf("sueno_horas", "pasos", "pantalla_min")) }
    var ayer by remember { mutableStateOf(true) }
    Selector("objetivo", objetivo, vars, elegir) { objetivo = it }
    Etiqueta("Predictores (hasta 6)")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        pred.forEach { p -> Ficha((vars.firstOrNull { it.id == p }?.nombre ?: p) + "  ×", true) { pred = pred - p } }
        if (pred.size < 6) Ficha("+ añadir", false) { elegir { id -> if (id !in pred && id != objetivo) pred = pred + id } }
    }
    Interruptor("Controlar por el valor del día anterior", ayer, "Los días se parecen a sus vecinos; incluir el día anterior evita confundir esa inercia con un efecto.") { ayer = it }
    val m = remember(panel, objetivo, pred, ayer) { Analisis.modelo(panel, objetivo, pred, ayer) }
    if (m == null) { Vacio("No hay suficientes días con todas estas variables a la vez (hacen falta unos ${pred.size + 11}). Prueba con menos predictores."); return }
    Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Cifra("${m.n}", "días completos", Modifier.weight(1f)); Cifra("%.0f%%".format(m.r2Ajustado.coerceAtLeast(0.0) * 100), "varianza explicada (ajustada)", Modifier.weight(1f))
    }
    val c = MaterialTheme.colorScheme.primary; val c2 = MaterialTheme.colorScheme.outline
    val eg = estiloGrafica()
    val cifras = estiloCifras(MaterialTheme.typography.labelMedium)
    Tarjeta(Modifier.padding(top = 8.dp)) {
        m.nombres.forEachIndexed { i, n ->
            val nombre = if (n == "ayer") "Valor del día anterior" else vars.firstOrNull { it.id == n }?.nombre ?: n
            val firme = m.lo[i] > 0 || m.hi[i] < 0
            Text(nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = if (i == 0) 0.dp else 8.dp))
            Text("β = %+.2f  [%.2f, %.2f]".format(m.beta[i], m.lo[i], m.hi[i]) + if (firme) "  ✓" else "", style = cifras, color = if (firme) c else MaterialTheme.colorScheme.onSurfaceVariant)
            Canvas(Modifier.fillMaxWidth().height(12.dp).padding(top = 4.dp)) {
                val mid = size.width / 2
                fun px(x: Double) = (mid + x * mid).toFloat().coerceIn(0f, size.width)
                lineaMeta(eg, Offset(mid, 0f), Offset(mid, size.height), c2, 1f)
                lineaGrafica(eg, listOf(Offset(px(m.lo[i]), size.height / 2), Offset(px(m.hi[i]), size.height / 2)), if (firme) c else c2, 4f)
                puntoGrafica(eg, Offset(px(m.beta[i]), size.height / 2), 5f, if (firme) c else c2)
            }
        }
    }
    Ayuda("β estandarizado: cuántas desviaciones estándar cambia el objetivo por cada desviación estándar del predictor, manteniendo los demás fijos. ±0.1 es pequeño, ±0.3 moderado, ±0.5 grande.", Modifier.padding(top = 6.dp))
}

// ---------------- Dinámica ----------------
@Composable
private fun Dinamica() {
    val d = remember(Repo.dias.size) { Analisis.dinamica() }
    if (d == null) { Vacio("Hacen falta al menos 7 días con valencia."); return }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Cifra(fmt(d.media), "valencia media", Modifier.weight(1f)); Cifra(d.sd?.let { fmt(it) } ?: "—", "variabilidad (DE)", Modifier.weight(1f))
    }
    Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Cifra(d.mssd?.let { fmt(it) } ?: "—", "inestabilidad (MSSD)", Modifier.weight(1f)); Cifra(d.inercia?.let { "%.2f".format(it) } ?: "—", "inercia", Modifier.weight(1f))
    }
    Tarjeta(Modifier.padding(top = 10.dp)) {
        // «Muy difíciles» (1 o 2) no es lo mismo que la racha dura (3 o menos): por eso se nombran distinto (H-5-12).
        Clave("muy difíciles", "%.0f%% de tus días con valencia 1 o 2".format(d.dificiles))
        Clave("racha buena", "${porNumero(d.rachaBuena, "1 día seguido", "${d.rachaBuena} días seguidos")} con 5 o más")
        Clave("racha dura", "${porNumero(d.rachaDificil, "1 día seguido", "${d.rachaDificil} días seguidos")} con 3 o menos")
        d.dentroDelDia?.let { Clave("intradía", "DE de %.2f entre avisos de un mismo día".format(it)) }
    }
    Ayuda("La DE mide qué tanto cambia tu ánimo en general; el MSSD, qué tan bruscos son los saltos de un día al siguiente; la inercia (de −1 a 1), qué tanto el ánimo de hoy arrastra al de mañana. " +
        "Una inercia alta se ha asociado con más dificultad para salir de los días malos (Kuppens et al., 2010).", Modifier.padding(top = 6.dp))
    if (d.meses.size >= 2) {
        Etiqueta("Por mes")
        Tarjeta {
            d.meses.takeLast(12).forEach { m ->
                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text(Fechas.mesTitulo(m.mes).lowercase(), style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                    Text("μ ${fmt(m.media)} · de ${m.sd?.let { fmt(it) } ?: "—"} · mssd ${m.mssd?.let { fmt(it) } ?: "—"}", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// ---------------- Ritmo ----------------
@Composable
private fun Ritmo() {
    val mexico = Repo.config.perfil.pais.equals("MX", ignoreCase = true)
    val r = remember(Repo.salud, Repo.pantalla, mexico) { Analisis.ritmo(mexico = mexico) }
    if (r == null) { Vacio("Hacen falta al menos 5 noches de las últimas 8 semanas registradas por el reloj (o por el uso del teléfono)."); return }
    when (r.fuente) {
        "teléfono" -> Aviso("Sin datos del reloj en las últimas 8 semanas: las noches se aproximan con el último uso del teléfono y el primero del día siguiente.")
        "mezcla" -> Aviso("Solo ${r.nochesReloj} ${if (r.nochesReloj == 1) "noche" else "noches"} del reloj en las últimas 8 semanas: las demás se aproximan con el último uso del teléfono y el primero del día siguiente.")
    }
    Ayuda("Con las noches de las últimas 8 semanas.", Modifier.padding(top = 4.dp))
    Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Cifra(hora(r.dormirMedio), "te duermes", Modifier.weight(1f)); Cifra(hora(r.despertarMedio), "despiertas", Modifier.weight(1f))
        Cifra(r.jetLag?.let { "%.1f h".format(it) } ?: "—", "jet lag social", Modifier.weight(1f))
    }
    Tarjeta(Modifier.padding(top = 10.dp)) {
        r.duracionLaboral?.let { Clave("antes de laborar", "%.1f h de sueño".format(it)) }
        r.duracionLibre?.let { Clave("antes de un día libre", "%.1f h de sueño".format(it)) }
        r.sdDormir?.let { Clave("regularidad", "la hora de dormir varía ±%.0f min".format(it * 60)) }
    }
    // Cada noche es una barra de las 18:00 a las 14:00.
    val c = MaterialTheme.colorScheme.primary; val c2 = MaterialTheme.colorScheme.secondary; val guia = MaterialTheme.colorScheme.outline
    val eg = estiloGrafica()
    val pin = pincelDeCifras(MaterialTheme.colorScheme.onSurfaceVariant, 22f)
    Canvas(Modifier.fillMaxWidth().height((r.noches.size * 9 + 24).dp).padding(top = 10.dp)) {
        val izq = 0f; val w = size.width
        fun px(h: Double) = (izq + (h - 18) / 20.0 * (w - izq)).toFloat()
        val alto = (size.height - 24f) / r.noches.size
        val horas = listOf(18.0, 22.0, 26.0, 30.0, 34.0, 38.0)
        rejillaGrafica(eg, emptyList(), guia, xs = horas.map { px(it) }, area = Rect(0f, 0f, size.width, size.height - 24f))
        horas.forEach { h -> drawContext.canvas.nativeCanvas.drawText(hora(h), px(h) + 2, size.height - 4f, pin) }
        r.noches.forEachIndexed { i, n ->
            val libre = Fechas.esDiaLibre(n.fecha, mexico)
            barraGrafica(eg, Offset(px(n.inicio), i * alto + 1), Size((px(n.fin) - px(n.inicio)).coerceAtLeast(2f), alto - 2), if (libre) c2 else c, horizontal = true)
        }
        ejeGrafica(eg, Offset(0f, size.height - 24f), Offset(size.width, size.height - 24f), guia)
    }
    Ayuda("Barras de color fuerte: noches antes de un día laboral; de otro color: antes de un día libre (sábado, domingo${if (mexico) " o festivo" else ""}). El jet lag social es la diferencia del punto medio del sueño entre ambos (Wittmann et al., 2006); más de 1 hora se ha asociado con peor ánimo y salud.", Modifier.padding(top = 6.dp))
}

// ---------------- Palabras ----------------
@Composable
private fun Palabras() {
    val (buenas, malas, frecuentes) = remember(Repo.dias.size) { Analisis.palabras() }
    if (frecuentes.isEmpty()) { Vacio("Escribe en el diario para ver tus palabras."); return }
    if (buenas.isEmpty()) Ayuda("Para asociar palabras con tu ánimo hacen falta palabras que aparezcan en al menos 5 días con valencia.")
    else {
        Etiqueta("En tus mejores días")
        Tarjeta { buenas.take(10).forEach { Clave(it.palabra, "%+.2f · %d días".format(it.dif, it.dias), ancho = 14) } }
        Etiqueta("En tus días difíciles")
        Tarjeta { malas.take(10).forEach { Clave(it.palabra, "%+.2f · %d días".format(it.dif, it.dias), ancho = 14) } }
    }
    Etiqueta("Las que más usas")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val max = frecuentes.first().second.toFloat()
        frecuentes.forEach { (w, n) ->
            Text(w, style = MaterialTheme.typography.labelLarge.copy(fontSize = (12f + 8f * n / max).sp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f + 0.5f * n / max))
        }
    }
    Ayuda("La cifra es cuánto más alta (o baja) es tu valencia en los días que usas esa palabra, frente a tu promedio.", Modifier.padding(top = 6.dp))
}

// ---------------- Atípicos ----------------
@Composable
private fun Atipicos(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, nav: Nav) {
    val l = remember(panel) { Analisis.atipicos(panel, vars) }
    if (l.isEmpty()) { Vacio("Ningún día del último año se sale claramente de lo normal (o aún hay pocos datos)."); return }
    Tarjeta {
        l.take(40).forEachIndexed { i, a ->
            if (i > 0) HorizontalDivider()
            Column(Modifier.fillMaxWidth().clickable { Seleccion.abrir(nav, a.fecha) }.padding(vertical = 8.dp)) {
                Text(Fechas.titulo(a.fecha), style = MaterialTheme.typography.bodyMedium)
                a.razones.take(3).forEach { Text("› $it", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
    Ayuda("Se compara cada día con tu mediana; se marcan los que están a más de 3 desviaciones robustas. Toca uno para abrirlo y ver qué pasó.", Modifier.padding(top = 6.dp))
}
