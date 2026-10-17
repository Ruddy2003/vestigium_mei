package io.github.ruddy2003.vestigium.ui.pantallas

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Correlacion
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.ciencia.Par
import io.github.ruddy2003.vestigium.ciencia.Relaciones
import io.github.ruddy2003.vestigium.ciencia.Variable
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun PantallaExplorar(nav: Nav) {
    val version = Repo.dias.size + Repo.momentos.size + Repo.salud.size + Repo.pantalla.size + Repo.gastos.size
    var panel by remember { mutableStateOf<Map<String, Map<String, Double?>>>(emptyMap()) }
    var vars by remember { mutableStateOf<List<Variable>>(emptyList()) }
    var mejores by remember { mutableStateOf<Map<Boolean, Relaciones>?>(null) }
    LaunchedEffect(version) {
        withContext(Dispatchers.Default) {
            val p = Estadistica.panel(); val v = Estadistica.variables()
            val m = mapOf(true to Estadistica.relaciones(p, v, true), false to Estadistica.relaciones(p, v, false))
            withContext(Dispatchers.Main) { panel = p; vars = v; mejores = m }
        }
    }
    var foco by remember { mutableStateOf("todas") }
    var sinTendencia by remember { mutableStateOf(true) }
    var verObvias by remember { mutableStateOf(false) }
    var x by remember { mutableStateOf("valencia") }
    var y by remember { mutableStateOf("sueno_horas") }
    var desfase by remember { mutableStateOf(false) }
    var eligiendo by remember { mutableStateOf<Char?>(null) }

    Hoja("Entender", subtitulo = "Tus datos vistos de lejos: la semana, el año, el mapa y las relaciones. Para modelos completos, exporta y abre vestigium.R.",
        buscar = { nav.ir("buscar") }) {
        CentroEntender(nav)
        val nDias = Repo.dias.size
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("$nDias", "días con diario", Modifier.weight(1f))
            Cifra("${Repo.momentos.count { it.tipo != "muestreo" }}", "momentos", Modifier.weight(1f))
            Cifra("${Repo.momentos.count { it.tipo == "muestreo" }}", "avisos respondidos", Modifier.weight(1f))
        }
        Ayuda(if (nDias >= 90) "Con 90 días o más ya se pueden detectar relaciones moderadas." else "Faltan ${90 - nDias} días para tener potencia suficiente; antes de eso, todo es pista, no evidencia.", Modifier.padding(top = 8.dp))

        Seccion("Herramientas", "Análisis más finos de tus datos.", latin = "INSTRVMENTA") {
            Tarjeta {
                HERRAMIENTAS.forEachIndexed { i, (id, n, d) ->
                    if (i > 0) androidx.compose.material3.HorizontalDivider()
                    io.github.ruddy2003.vestigium.ui.FilaMenu("${io.github.ruddy2003.vestigium.ui.Estilo.numeral(i + 1)} · $n", d) { Herramienta.actual = id; nav.ir("analisis") }
                }
            }
        }

        Seccion("Cobertura", "Últimas 16 semanas. Más intenso = mejor valencia. Hueco = sin registro.") { Tarjeta { Cobertura() } }

        Seccion("Cruzar dos variables") {
            Tarjeta {
                val nx = vars.firstOrNull { it.id == x }?.nombre ?: x
                val ny = vars.firstOrNull { it.id == y }?.nombre ?: y
                OutlinedButton(onClick = { eligiendo = 'x' }, modifier = Modifier.fillMaxWidth()) { Text("Eje X: $nx") }
                OutlinedButton(onClick = { eligiendo = 'y' }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) { Text("Eje Y: $ny") }
                Interruptor("Medir Y al día siguiente", desfase, "Ejemplo: ¿lo que dormí hoy predice mi ánimo de mañana?") { desfase = it }
                val ps = Estadistica.pares(panel, x, y, desfase)
                if (x == y && !desfase) Ayuda("Elige dos variables distintas.")
                else if (ps.size < 3) Vacio("Hay ${ps.size} días con ambos datos. Hacen falta al menos 3 para dibujar algo, y unos 30 para empezar a creerle.")
                else {
                    val r = Estadistica.pearson(ps.map { it.x }, ps.map { it.y })
                    val rho = Estadistica.spearman(ps.map { it.x }, ps.map { it.y })
                    val ic = Estadistica.ic95(r, ps.size)
                    Dispersion(ps, nx, ny)
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Cifra(r?.let { "%.2f".format(it) } ?: "—", "Pearson r", Modifier.weight(1f))
                        Cifra(rho?.let { "%.2f".format(it) } ?: "—", "Spearman ρ", Modifier.weight(1f))
                        Cifra("${ps.size}", "días", Modifier.weight(1f))
                    }
                    val txt = if (r == null) "Una de las dos variables no cambia en estos días, así que no se puede calcular relación." else buildString {
                        append("Relación ${Estadistica.fuerza(r)} y ${if (r >= 0) "positiva" else "negativa"}: cuando sube «$nx», «$ny» tiende a ${if (r >= 0) "subir" else "bajar"}${if (desfase) " al día siguiente" else ""}. ")
                        if (ic != null) append(if (ic.first < 0 && ic.second > 0) "El intervalo de 95% va de ${"%.2f".format(ic.first)} a ${"%.2f".format(ic.second)} e incluye el cero: podría ser azar. "
                            else "El intervalo de 95% va de ${"%.2f".format(ic.first)} a ${"%.2f".format(ic.second)} y no incluye el cero. ")
                        if (ps.size < 30) append("Solo son ${ps.size} días: tómalo como pista.")
                        append(" Recuerda que dos cosas que se mueven juntas no prueban que una cause la otra; para eso están los experimentos.")
                    }
                    Text(txt, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }

        Seccion("Las relaciones más marcadas") {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FOCOS.forEach { (id, n) -> Ficha(n, foco == id) { foco = id } }
            }
            Interruptor("Quitar tendencias", sinTendencia, "Compara cada día con sus ~3 semanas alrededor. Así, dos cosas que solo cambiaron con el tiempo (los días se acortan mientras bajas de peso) dejan de parecer relacionadas.") { sinTendencia = it }
            Interruptor("Mostrar también las obvias", verObvias, "Pares de la misma fuente o calculados uno del otro: calorías y proteína, eventos y minutos de agenda, pasos y minutos a pie.") { verObvias = it }
            val m = mejores?.get(sinTendencia)
            if (m == null) Ayuda("Calculando…")
            else if (m.lista.isEmpty()) Vacio("Aparecerán cuando tengas al menos 14 días con datos en común.")
            else {
                val lista = remember(m, verObvias, foco) { m.lista.filter { (verObvias || !it.obvia) && enFoco(foco, it) } }
                val q = { c: Correlacion -> if (verObvias) c.qConObvias else c.q }
                val firmes = m.lista.count { (verObvias || !it.obvia) && q(it) < 0.10 }
                Ayuda(buildString {
                    append("Revisé ${if (verObvias) m.lista.size else m.probadas} combinaciones")
                    if (!verObvias && m.obvias > 0) append(" (dejé fuera ${m.obvias} obvias)")
                    append(". Al revisar tantas, algunas salen por puro azar; ya corregido eso, $firmes se sostienen. ")
                    if (m.lentas.isNotEmpty()) append("Sin tendencia no entran ${m.lentas.joinToString(", ") { "«" + it.nombre.lowercase() + "»" }}: casi solo cambian con las semanas. ")
                    if (firmes == 0) append("Con pocos días es normal que nada se sostenga; las de abajo son pistas, no hallazgos.")
                })
                val grupos = remember(lista) { agrupar(lista, 15) }
                if (lista.isEmpty()) Vacio("Nada en este filtro todavía.")
                else Tarjeta {
                    grupos.forEach { (c, parecidas) ->
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Text("${c.a.nombre}  ↔  ${c.b.nombre}", style = MaterialTheme.typography.bodyMedium)
                            Ayuda(buildString {
                                append("r = ${"%+.2f".format(c.r)} · ${c.n} días")
                                if (c.nEf < c.n * 0.8) append(" (≈ ${c.nEf.toInt()} independientes)")
                                append(" · ${Estadistica.fuerza(c.r)}")
                                if (sinTendencia && abs(c.bruta - c.r) >= 0.2) append(" · con tendencia: ${"%+.2f".format(c.bruta)}")
                                if (c.obvia) append(" · obvia")
                                if (q(c) >= 0.10) append(" · podría ser azar")
                            })
                            if (parecidas.isNotEmpty()) Ayuda("También con " + parecidas.take(3).joinToString(", ") { p ->
                                val otra = if (p.a.id == c.a.id || p.a.id == c.b.id) p.b else p.a
                                "«${otra.nombre.lowercase()}» (${"%+.2f".format(p.r)})"
                            } + if (parecidas.size > 3) " y ${parecidas.size - 3} más" else "")
                        }
                    }
                }
            }
        }

        Seccion("¿Qué tan bien te conoces?", "Tu predicción de cada noche contra lo que pasó al día siguiente.") {
            val pares = Repo.dias.values.mapNotNull { d -> val p = d.prediccion_manana ?: return@mapNotNull null; val real = Repo.dias[Fechas.sumar(d.fecha, 1)]?.valencia ?: return@mapNotNull null; p to real }
            if (pares.size < 5) Vacio("Llevas ${pares.size} predicciones con resultado. Con 5 o más aparece tu calibración.")
            else Tarjeta {
                val error = pares.map { abs(it.first - it.second) }.average()
                val r = Estadistica.pearson(pares.map { it.first.toDouble() }, pares.map { it.second.toDouble() })
                val sesgo = pares.map { it.first - it.second }.average()
                Text("Error promedio: ${"%.1f".format(error)} puntos en la escala de 1 a 7.", style = MaterialTheme.typography.bodyLarge)
                Ayuda("Correlación entre lo que predices y lo que pasa: ${r?.let { "%.2f".format(it) } ?: "—"}. " +
                    if (sesgo > 0.3) "Tiendes a predecir días mejores de lo que resultan." else if (sesgo < -0.3) "Tiendes a predecir días peores de lo que resultan." else "No muestras un sesgo claro hacia el optimismo o el pesimismo.")
            }
        }

        Seccion("A qué hora aparecen tus emociones", "Momentos registrados por hora del día.") {
            val tipos = listOf("preocupacion", "ansiedad", "tristeza", "alegria", "impulso")
            val ms = Repo.momentos.filter { it.tipo in tipos }
            if (ms.size < 5) Vacio("Registra al menos 5 momentos para ver el patrón por hora.")
            else Tarjeta {
                tipos.forEach { t ->
                    val porHora = IntArray(24)
                    ms.filter { it.tipo == t }.forEach { m -> Fechas.horaDe(m.ts).take(2).toIntOrNull()?.let { porHora[it]++ } }
                    if (porHora.sum() > 0) {
                        Text(Catalogo.nombreTipo(t) + " · ${porHora.sum()}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
                        Horas(porHora)
                    }
                }
                Ayuda("De izquierda a derecha: de 0:00 a 23:00.", Modifier.padding(top = 6.dp))
            }
        }

        Seccion("Personas, temas e hitos") {
            val eq = Repo.equivalencias()
            val p = Repo.dias.values.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct() }.groupingBy { it }.eachCount()
            val t = Repo.dias.values.flatMap { d -> Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct() }.groupingBy { it }.eachCount()
            val h = Repo.dias.values.flatMap { it.hitos }.groupingBy { it }.eachCount()
            Lista("Personas", p, "@"); Lista("Temas", t, "#"); Lista("Hitos", h, "")
            TextButton(onClick = { nav.ir("totales") }) { Text("Ver menciones con fechas y por mes en Totales ›") }
        }

        Seccion("Lugares y ánimo") {
            TextButton(onClick = { nav.ir("lugares") }) { Text("Ver el mapa emocional de tus lugares ›") }
        }
    }

    eligiendo?.let { eje ->
        AlertDialog(
            onDismissRequest = { eligiendo = null },
            title = { Text(if (eje == 'x') "Eje X" else "Eje Y") },
            text = {
                LazyColumn(Modifier.heightIn(max = 460.dp)) {
                    items(vars) { v ->
                        TextButton(onClick = { if (eje == 'x') x = v.id else y = v.id; eligiendo = null }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth()) {
                                Text(v.nombre, style = MaterialTheme.typography.bodyMedium)
                                Text(v.grupo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { eligiendo = null }) { Text("Cerrar") } },
        )
    }
}

private val FOCOS = listOf("todas" to "todas", "animo" to "con tu ánimo", "sueno" to "con tu sueño", "cuerpo" to "cuerpo y comida", "telefono" to "teléfono y social")

/**
 * Junta los pares casi repetidos: la misma variable con dos de la misma familia
 * (ánimo ↔ horas de sueño y ánimo ↔ sueño REM). Se queda con el más sólido y anota los demás.
 */
private fun agrupar(l: List<Correlacion>, cuantos: Int): List<Pair<Correlacion, List<Correlacion>>> {
    fun fam(x: String, y: String) = x == y || Estadistica.FAMILIAS.any { x in it && y in it }
    fun parecida(k: Correlacion, c: Correlacion) =
        (k.a.id == c.a.id && fam(k.b.id, c.b.id)) || (k.a.id == c.b.id && fam(k.b.id, c.a.id)) ||
            (k.b.id == c.a.id && fam(k.a.id, c.b.id)) || (k.b.id == c.b.id && fam(k.a.id, c.a.id))
    val out = ArrayList<Pair<Correlacion, MutableList<Correlacion>>>()
    for ((i, c) in l.withIndex()) {
        if (out.size >= cuantos && i > 400) break
        val rep = out.firstOrNull { parecida(it.first, c) }
        if (rep != null) rep.second.add(c) else if (out.size < cuantos) out.add(c to mutableListOf())
    }
    return out
}

private fun enFoco(foco: String, c: Correlacion): Boolean {
    fun es(v: io.github.ruddy2003.vestigium.ciencia.Variable) = when (foco) {
        "animo" -> v.grupo in setOf("Ánimo", "Momentos", "Privado", "Lectura del día") || v.id == "dolor"
        "sueno" -> v.grupo == "Sueño"
        "cuerpo" -> v.grupo in setOf("Cuerpo", "Nutrición")
        "telefono" -> v.grupo in setOf("Teléfono", "Social")
        else -> true
    }
    return es(c.a) || es(c.b)
}

@Composable
private fun Lista(titulo: String, conteo: Map<String, Int>, prefijo: String) {
    Tarjeta(Modifier.padding(bottom = 8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        val top = conteo.entries.sortedByDescending { it.value }.take(12)
        if (top.isEmpty()) Ayuda("Nada aún.")
        top.forEach { (k, v) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                Text(prefijo + k, fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("$v ${if (v == 1) "día" else "días"}", fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun Horas(v: IntArray) {
    val color = MaterialTheme.colorScheme.primary
    val fondo = MaterialTheme.colorScheme.surfaceVariant
    Canvas(Modifier.fillMaxWidth().aspectRatio(10f)) {
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
private fun Cobertura() {
    val acento = MaterialTheme.colorScheme.primary
    val vacio = MaterialTheme.colorScheme.surfaceVariant
    val texto = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val hoy = Fechas.parse(Fechas.diaLogico())
    val inicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(15)
    var registrados = 0
    for (i in 0..((hoy.toEpochDay() - inicio.toEpochDay()).toInt())) if (Repo.dias.containsKey(Fechas.iso(inicio.plusDays(i.toLong())))) registrados++
    Canvas(Modifier.fillMaxWidth().aspectRatio(16f / 7.6f)) {
        val izq = 34f
        val celda = (size.width - izq) / 16
        val lado = celda * 0.84f
        val pincel = Paint().apply { color = texto; textSize = 26f; isAntiAlias = true }
        listOf(0 to "L", 2 to "M", 4 to "V", 6 to "D").forEach { (i, l) -> drawContext.canvas.nativeCanvas.drawText(l, 0f, i * celda + lado * 0.75f, pincel) }
        for (w in 0 until 16) for (dd in 0 until 7) {
            val f: LocalDate = inicio.plusDays((w * 7 + dd).toLong())
            if (f.isAfter(hoy)) continue
            val dia = Repo.dias[Fechas.iso(f)]
            val col = if (dia == null) vacio else acento.copy(alpha = dia.valencia?.let { 0.25f + (it - 1) / 6f * 0.75f } ?: 0.35f)
            drawRoundRect(col, Offset(izq + w * celda, dd * celda), Size(lado, lado), CornerRadius(5f, 5f))
        }
    }
    Ayuda("$registrados días registrados en este periodo.", Modifier.padding(top = 6.dp))
}

@Composable
private fun Dispersion(ps: List<Par>, nx: String, ny: String) {
    val acento = MaterialTheme.colorScheme.primary
    val linea = MaterialTheme.colorScheme.outline
    val texto = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    Ayuda("↑ $ny", Modifier.padding(top = 12.dp, bottom = 4.dp))
    Canvas(Modifier.fillMaxWidth().aspectRatio(1.45f)) {
        val l = 70f; val r = 14f; val t = 14f; val b = 56f
        val xs = ps.map { it.x }; val ys = ps.map { it.y }
        var x0 = xs.min(); var x1 = xs.max(); var y0 = ys.min(); var y1 = ys.max()
        if (x0 == x1) { x0 -= 1; x1 += 1 }; if (y0 == y1) { y0 -= 1; y1 += 1 }
        val discX = xs.all { it % 1.0 == 0.0 } && x1 - x0 <= 10; val discY = ys.all { it % 1.0 == 0.0 } && y1 - y0 <= 10
        fun px(v: Double) = (l + (v - x0) / (x1 - x0) * (size.width - l - r)).toFloat()
        fun py(v: Double) = (size.height - b - (v - y0) / (y1 - y0) * (size.height - t - b)).toFloat()
        val pincel = Paint().apply { color = texto; textSize = 26f; isAntiAlias = true }
        fun fmt(v: Double) = if (abs(v) >= 1000) "%,.0f".format(v) else if (v % 1.0 == 0.0) v.toInt().toString() else "%.1f".format(v)
        listOf(y0, (y0 + y1) / 2, y1).forEach { v ->
            drawLine(linea, Offset(l, py(v)), Offset(size.width - r, py(v)), 1.5f)
            drawContext.canvas.nativeCanvas.drawText(fmt(v), 0f, py(v) + 9f, pincel)
        }
        listOf(x0, (x0 + x1) / 2, x1).forEach { v ->
            val s = fmt(v); drawContext.canvas.nativeCanvas.drawText(s, px(v) - pincel.measureText(s) / 2, size.height - b + 34f, pincel)
        }
        // Recta de tendencia
        val mx = xs.average(); val my = ys.average()
        val sxx = xs.sumOf { (it - mx) * (it - mx) }
        if (sxx > 0) {
            val m = xs.indices.sumOf { (xs[it] - mx) * (ys[it] - my) } / sxx; val c = my - m * mx
            fun cl(v: Double) = v.coerceIn(y0, y1)
            drawLine(acento, Offset(px(x0), py(cl(m * x0 + c))), Offset(px(x1), py(cl(m * x1 + c))), 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f)))
        }
        ps.forEachIndexed { i, p ->
            val jx = if (discX) ((sin(i * 12.9898 + 1) * 43758.5453) % 1) * 0.3 else 0.0
            val jy = if (discY) ((sin(i * 78.233 + 7) * 12543.231) % 1) * 0.3 else 0.0
            val c = Offset(px(p.x + jx), py(p.y + jy))
            drawCircle(acento.copy(alpha = 0.45f), 9f, c)
            drawCircle(acento, 9f, c, style = Stroke(2f))
        }
        val etq = (if (nx.length > 40) nx.take(39) + "…" else nx) + " →"
        drawContext.canvas.nativeCanvas.drawText(etq, (size.width - pincel.measureText(etq)) / 2 + l / 2, size.height - 6f, pincel)
    }
}



/** Promedio de la valencia en un rango de días (diario o, si no hay, avisos de ánimo). */
private fun animoEntre(dias: List<String>): Double? = dias.mapNotNull { f ->
    Repo.dias[f]?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == f && it.tipo == "muestreo" }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average()
}.takeIf { it.isNotEmpty() }?.average()

/** Las vistas de Entender como tarjetas con una cifra viva cada una. */
@Composable
private fun CentroEntender(nav: Nav) {
    val hoy = Fechas.diaLogico()
    val lunes = Fechas.lunesDe(hoy)
    val transcurridos = (java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(lunes), Fechas.parse(hoy)) + 1).toInt()
    val semana = (0 until transcurridos).map { Fechas.sumar(lunes, it.toLong()) }
    val antes = semana.map { Fechas.sumar(it, -7) }
    val a = animoEntre(semana); val b = animoEntre(antes)
    val flecha = if (a != null && b != null) (if (a - b > 0.15) " ↑" else if (b - a > 0.15) " ↓" else " =") else ""
    val mes = hoy.take(7)
    val diasMes = Repo.dias.keys.count { it.startsWith(mes) && Repo.dias[it]?.let { d -> d.valencia != null || d.nota.isNotBlank() } == true }
    val anio = hoy.take(4)
    val tarjetas = listOf(
        Triple("Resumen semanal", a?.let { "%.1f".format(it) + flecha } ?: "—", "ánimo de esta semana") to "semana",
        Triple("Calendario", "$diasMes / ${hoy.takeLast(2).toInt()}", "días con diario este mes") to "calendario",
        Triple("Tu año", "${Repo.dias.keys.count { it.startsWith(anio) }}", "días registrados en $anio") to "anno",
        Triple("Totales", "${Repo.dias.size}", "días de datos" + (Repo.dias.keys.minOrNull()?.let { " desde ${Fechas.corto(it)}" } ?: "")) to "totales",
        Triple("Mapa", "${Repo.lugares.size}", "lugares marcados") to "mapa",
        Triple("Experimentos personales", "${Repo.experimentos.count { it.activo }}", "en curso") to "experimentos",
    )
    tarjetas.chunked(2).forEach { fila ->
        androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            fila.forEach { (t, ruta) ->
                androidx.compose.material3.Surface(onClick = { nav.ir(ruta) }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface, border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(io.github.ruddy2003.vestigium.ui.Estilo.seccion(t.first, io.github.ruddy2003.vestigium.ui.Romano.latin(t.first)).first, style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        Text(t.second, style = estiloCifra(), maxLines = 1)
                        Text(t.third, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    }
                }
            }
            if (fila.size == 1) androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
        }
    }
}
