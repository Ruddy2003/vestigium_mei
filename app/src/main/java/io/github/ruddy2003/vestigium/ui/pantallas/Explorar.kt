package io.github.ruddy2003.vestigium.ui.pantallas

import android.graphics.Paint
import androidx.compose.foundation.Canvas
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
import io.github.ruddy2003.vestigium.ciencia.Variable
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Cifra
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
    var mejores by remember { mutableStateOf<Pair<List<Correlacion>, Int>?>(null) }
    LaunchedEffect(version) {
        withContext(Dispatchers.Default) {
            val p = Estadistica.panel(); val v = Estadistica.variables()
            val m = Estadistica.mejores(p, v)
            withContext(Dispatchers.Main) { panel = p; vars = v; mejores = m }
        }
    }
    var x by remember { mutableStateOf("valencia") }
    var y by remember { mutableStateOf("sueno_horas") }
    var desfase by remember { mutableStateOf(false) }
    var eligiendo by remember { mutableStateOf<Char?>(null) }

    Hoja("Explorar", subtitulo = "Cruces rápidos para tu curiosidad. El análisis serio, con modelos que respetan que los días se parecen a sus vecinos, sigue en R.") {
        val nDias = Repo.dias.size
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("$nDias", "días con diario", Modifier.weight(1f))
            Cifra("${Repo.momentos.count { it.tipo != "muestreo" }}", "momentos", Modifier.weight(1f))
            Cifra("${Repo.momentos.count { it.tipo == "muestreo" }}", "avisos respondidos", Modifier.weight(1f))
        }
        Ayuda(if (nDias >= 90) "Con 90 días o más ya se pueden detectar relaciones moderadas." else "Faltan ${90 - nDias} días para tener potencia suficiente; antes de eso, todo es pista, no evidencia.", Modifier.padding(top = 8.dp))

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
            val m = mejores
            if (m == null) Ayuda("Calculando…")
            else if (m.first.isEmpty()) Vacio("Aparecerán cuando tengas al menos 14 días con datos en común.")
            else {
                val solidas = m.first.count { c -> c.ic?.let { it.first > 0 || it.second < 0 } == true }
                Ayuda("Revisé ${m.second} combinaciones. Por azar, unas ${(m.second * 0.05).toInt()} saldrían «significativas» sin significar nada; aquí $solidas tienen un intervalo que no toca el cero. Desconfía de las que no puedas explicar.")
                Tarjeta {
                    m.first.take(12).forEach { c ->
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Text("${c.a.nombre}  ↔  ${c.b.nombre}", style = MaterialTheme.typography.bodyMedium)
                            val firme = c.ic?.let { it.first > 0 || it.second < 0 } == true
                            Ayuda("r = ${"%+.2f".format(c.r)} · ${c.n} días · ${Estadistica.fuerza(c.r)}" + if (!firme) " · podría ser azar" else "")
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

@Composable
private fun Lista(titulo: String, conteo: Map<String, Int>, prefijo: String) {
    Tarjeta(Modifier.padding(bottom = 8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        val top = conteo.entries.sortedByDescending { it.value }.take(12)
        if (top.isEmpty()) Ayuda("Nada aún.")
        top.forEach { (k, v) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                Text(prefijo + k, fontFamily = FontFamily.Monospace, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("$v ${if (v == 1) "día" else "días"}", fontFamily = FontFamily.Monospace, fontSize = 13.sp)
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

@Suppress("unused")
private val transparente = Color.Transparent
