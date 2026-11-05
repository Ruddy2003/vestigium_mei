@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.esAnimo
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaMeta
import io.github.ruddy2003.vestigium.ui.pincelDeCifras
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import io.github.ruddy2003.vestigium.ui.rejillaGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs
import kotlin.math.sin

/** «Cruzar dos variables», ya calculado: los pares y sus correlaciones. */
private class Cruce(val ps: List<io.github.ruddy2003.vestigium.ciencia.Par>, val r: Double?, val rho: Double?, val ic: Pair<Double, Double>?, val nEf: Double?, val rSin: Double?)

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
    // Por semana: cada semana es el promedio de sus días; sirve para lo que pasa poco (escalar, viajar, escribir cartas).
    var porSemana by remember { mutableStateOf(false) }
    var semanales by remember { mutableStateOf<Map<Boolean, Relaciones>?>(null) }
    var panelSemanal by remember { mutableStateOf<Map<String, Map<String, Double?>>>(emptyMap()) }
    LaunchedEffect(porSemana, panel) {
        if (porSemana && panel.isNotEmpty()) withContext(Dispatchers.Default) {
            val ps = Estadistica.semanal(panel)
            val m = mapOf(true to Estadistica.relaciones(ps, vars, true, minN = 8, paso = 7, ventana = 35), false to Estadistica.relaciones(ps, vars, false, minN = 8, paso = 7))
            withContext(Dispatchers.Main) { panelSemanal = ps; semanales = m }
        }
    }
    fun abrirFicha(id: String) { Herramienta.actual = "factores"; Herramienta.objetivo = id; Herramienta.sinTendencia = sinTendencia; nav.ir("analisis") }
    // Por semana, la ficha de una variable es por día: la pareja tocada se abre abajo, en «Cruzar dos variables», por semanas (H-9-40).
    var abrirCruce by remember { mutableStateOf(0) }

    Hoja("Análisis", subtitulo = "Qué se mueve con qué: relaciones, herramientas y experimentos. Para modelos completos, exporta y abre vestigium.R.",
        buscar = { nav.ir("buscar") }, ajustes = { nav.ir("mas") }) {
        // Los resultados primero (N-19): tus experimentos y las relaciones más marcadas. Totales vive en Vida (N-20).
        RejillaTarjetas(listOf(
            Tarjetita("Experimentos personales", "${Repo.experimentos.count { io.github.ruddy2003.vestigium.data.Experimentos.enCurso(it) }}", "en curso · prueba algo y mide si sirve", "experimentos"),
        )) { nav.ir(it) }
        val nDias = Repo.dias.size
        Ayuda("$nDias días con diario · ${Repo.momentos.count { it.tipo != "muestreo" && !it.privado }} registros propios · ${Repo.momentos.count { it.tipo == "muestreo" }} avisos respondidos. " +
            // 90 días bastan para una relación moderada en una pareja que ya sospechabas; al revisar todas a la vez, solo para las fuertes (P-28).
            if (nDias >= 90) "Con 90 días o más ya se puede detectar una relación moderada en una pareja concreta; en esta lista, que revisa todas a la vez, solo las fuertes." else "Faltan ${90 - nDias} días para tener potencia suficiente; antes de eso, todo es pista, no evidencia.",
            Modifier.padding(top = 10.dp))
        Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("por día", !porSemana) { porSemana = false }
            Ficha("por semana", porSemana) { porSemana = true }
        }
        Ayuda(if (porSemana) "Cada semana es el promedio de sus días (en lo que es sí/no, la proporción de días). Sirve para lo que pasa pocas veces." else
            "Cada día contra cada día. ${vars.size} variables: tus personas, temas, lugares, apps, categorías de gasto, artistas, obras, ciudades, nutrientes, cuestionarios y etapas de vida, además de lo de siempre.",
            Modifier.padding(top = 4.dp))

        Seccion("Las relaciones más marcadas") {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FOCOS.forEach { (id, n) -> Ficha(n, foco == id) { foco = id } }
            }
            Interruptor("Quitar tendencias", sinTendencia, "Compara cada día con sus ~3 semanas alrededor. Así, dos cosas que solo cambiaron con el tiempo (los días se acortan mientras bajas de peso) dejan de parecer relacionadas. " +
                "No quita el ritmo de la semana: dos cosas que cambian juntas el fin de semana siguen saliendo juntas (en R, vm_relaciones(sin_semana = TRUE) lo quita).") { sinTendencia = it }
            Interruptor("Mostrar también las obvias", verObvias, "Pares de la misma fuente o calculados uno del otro: calorías y proteína, eventos y minutos de agenda, pasos y minutos a pie.") { verObvias = it }
            val m = (if (porSemana) semanales else mejores)?.get(sinTendencia)
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
                    // Las etapas, cuestionarios y la brújula son escalones: casi siempre caen aquí (H-5-14).
                    if (m.lentas.any { it.id.startsWith("vida_") || it.id.startsWith("pol_") || it.id.startsWith("esc_") })
                        append("Las etapas de vida, los cuestionarios y la brújula cambian a saltos: para ellas mira «Antes y después», abajo, o apaga «Quitar tendencias» sabiendo que así las tendencias se cuelan. ")
                    if (firmes == 0) append("Con pocos días es normal que nada se sostenga; las de abajo son pistas, no hallazgos.")
                })
                val grupos = remember(lista) { agrupar(lista, 15) }
                if (lista.isEmpty()) Vacio("Nada en este filtro todavía.")
                else {
                    // Cómo leer los recuadros, una sola vez arriba.
                    Row(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        val oscuro = io.github.ruddy2003.vestigium.ui.esOscuro()
                        Text("● suben juntas", style = MaterialTheme.typography.labelSmall, color = io.github.ruddy2003.vestigium.ui.Tonos.ingreso(oscuro))
                        Text("● una sube, la otra baja", style = MaterialTheme.typography.labelSmall, color = io.github.ruddy2003.vestigium.ui.Tonos.gasto(oscuro))
                    }
                    Ayuda("Más gruesa la unión, más fuerte la relación; punteada, podría ser azar.", Modifier.padding(bottom = 8.dp))
                    var todas by remember { mutableStateOf(false) }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        (if (todas) grupos else grupos.take(5)).forEach { (c, parecidas) ->
                            val detalle = buildString {
                                append("${c.n} ${if (porSemana) "semanas" else "días"}")
                                if (c.nEf < c.n * 0.8) append(" (≈ ${c.nEf.toInt()} independientes)")
                                append(" · ${Estadistica.fuerza(c.r)}")
                                if (sinTendencia && abs(c.bruta - c.r) >= 0.2) append(" · con tendencia: ${"%+.2f".format(c.bruta)}")
                                if (c.obvia) append(" · obvia")
                                if (q(c) >= 0.10) append(" · podría ser azar")
                            }
                            val tambien = if (parecidas.isEmpty()) null else "También con " + parecidas.take(3).joinToString(", ") { p ->
                                val otra = if (p.a.id == c.a.id || p.a.id == c.b.id) p.b else p.a
                                "«${otra.nombre.lowercase()}» (${"%+.2f".format(p.r)})"
                            } + if (parecidas.size > 3) " y ${parecidas.size - 3} más" else ""
                            io.github.ruddy2003.vestigium.ui.TarjetaRelacion(c.a, c.b, c.r, detalle, q(c) >= 0.10, tambien,
                                Modifier.clickable { if (porSemana) { x = c.a.id; y = c.b.id; desfase = false; abrirCruce++ } else abrirFicha(if (c.a.id == "valencia") c.b.id else c.a.id) })
                        }
                    }
                    if (porSemana) Ayuda(if (abrirCruce > 0) "La pareja quedó abajo, en «Cruzar dos variables», por semanas." else "Toca una relación para verla abajo, en «Cruzar dos variables», por semanas.",
                        Modifier.padding(top = 6.dp))
                    if (!todas && grupos.size > 5) TextButton(onClick = { todas = true }) {
                        Text("ver las ${grupos.size - 5} siguientes ▾", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Seccion("Herramientas", "Análisis más finos de tus datos.", latin = "INSTRVMENTA") {
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                HERRAMIENTAS.forEach { (id, n, _) -> Ficha(n, false) { Herramienta.actual = id; nav.ir("analisis") } }
            }
        }

        Seccion("Cruzar dos variables", plegable = true, abrir = abrirCruce) {
            Tarjeta {
                val nx = vars.firstOrNull { it.id == x }?.nombre ?: x
                val ny = vars.firstOrNull { it.id == y }?.nombre ?: y
                OutlinedButton(onClick = { eligiendo = 'x' }, modifier = Modifier.fillMaxWidth()) { Text("Eje X: $nx") }
                OutlinedButton(onClick = { eligiendo = 'y' }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) { Text("Eje Y: $ny") }
                Interruptor(if (porSemana) "Medir Y la semana siguiente" else "Medir Y al día siguiente", desfase, "Ejemplo: ¿lo que dormí hoy predice mi ánimo de mañana?") { desfase = it }
                // Se calcula solo cuando cambian los datos o las variables elegidas (antes, en cada redibujo de la pantalla).
                val cruce = remember(panel, panelSemanal, x, y, desfase, porSemana) {
                    val ps = if (porSemana) Estadistica.pares(panelSemanal, x, y, desfase, 7) else Estadistica.pares(panel, x, y, desfase)
                    if (ps.size < 3) return@remember Cruce(ps, null, null, null, null, null)
                    val r = Estadistica.pearson(ps.map { it.x }, ps.map { it.y })
                    val rho = Estadistica.spearman(ps.map { it.x }, ps.map { it.y })
                    // El intervalo cuenta los días seguidos que se parecen (n efectivo), como la lista (P-17).
                    val base = if (porSemana) panelSemanal else panel
                    val paso = if (porSemana) 7L else 1L
                    val sx = io.github.ruddy2003.vestigium.ciencia.Analisis.serie(base, x); val sy = io.github.ruddy2003.vestigium.ciencia.Analisis.serie(base, y)
                    val ef = Estadistica.correlacionEf(ps.map { it.x }, ps.map { it.y }, Estadistica.ar1(sx, paso) ?: Estadistica.AR1_SIN_DATO, Estadistica.ar1(sy, paso) ?: Estadistica.AR1_SIN_DATO)
                    // La misma relación sin tendencia: cada valor menos el promedio de sus semanas cercanas.
                    val ventanaT = if (porSemana) 35 else Estadistica.VENTANA
                    val tx = Estadistica.sinTendencia(sx, ventanaT); val ty = Estadistica.sinTendencia(sy, ventanaT).toMap()
                    val pt = tx.mapNotNull { (f, a) -> ty[if (desfase) Fechas.sumar(f, paso) else f]?.let { a to it } }
                    Cruce(ps, r, rho, ef?.second, ef?.third?.first, if (pt.size >= 5) Estadistica.pearson(pt.map { it.first }, pt.map { it.second }) else null)
                }
                val ps = cruce.ps
                if (x == y && !desfase) Ayuda("Elige dos variables distintas.")
                else if (ps.size < 3) Vacio("Hay ${ps.size} ${if (porSemana) "semanas" else "días"} con ambos datos. Hacen falta al menos 3 para dibujar algo, y unos 30 para empezar a creerle.")
                else {
                    val r = cruce.r; val rho = cruce.rho; val ic = cruce.ic; val nEf = cruce.nEf; val rSin = cruce.rSin
                    Dispersion(ps, nx, ny)
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Cifra(r?.let { "%.2f".format(it) } ?: "—", "Pearson r", Modifier.weight(1f))
                        Cifra(rSin?.let { "%.2f".format(it) } ?: "—", "r sin tendencia", Modifier.weight(1f))
                        Cifra(rho?.let { "%.2f".format(it) } ?: "—", "Spearman ρ", Modifier.weight(1f))
                        Cifra("${ps.size}", if (porSemana) "semanas" else "días", Modifier.weight(1f))
                    }
                    val txt = if (r == null) "Una de las dos variables no cambia en estos días, así que no se puede calcular relación." else buildString {
                        append("Relación ${Estadistica.fuerza(r)} y ${if (r >= 0) "positiva" else "negativa"}: cuando sube «$nx», «$ny» tiende a ${if (r >= 0) "subir" else "bajar"}${if (desfase) " al día siguiente" else ""}. ")
                        if (ic != null) append(if (ic.first < 0 && ic.second > 0) "El intervalo de 95% va de ${"%.2f".format(ic.first)} a ${"%.2f".format(ic.second)} e incluye el cero: podría ser azar. "
                            else "El intervalo de 95% va de ${"%.2f".format(ic.first)} a ${"%.2f".format(ic.second)} y no incluye el cero. ")
                        if (nEf != null && nEf < ps.size * 0.8) append("Como cada ${if (porSemana) "semana" else "día"} se parece al anterior, cuentan como unos ${nEf.toInt()} independientes. ")
                        if (rSin != null && abs(rSin - r) >= 0.2) append("Sin tendencia la relación es ${"%+.2f".format(rSin)}: parte de lo que se ve es que las dos cambiaron con el tiempo. ")
                        if (ps.size < 30) append("Solo son ${ps.size} ${if (porSemana) "semanas" else "días"}: tómalo como pista.")
                        append(" Recuerda que dos cosas que se mueven juntas no prueban que una cause la otra; para eso están los experimentos.")
                    }
                    Text(txt, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }

        Seccion("Cobertura", "Últimas 16 semanas. Más intenso = mejor valencia. Hueco = sin registro.", plegable = true) {
            Tarjeta { Cobertura() }
            TextButton(onClick = { nav.ir("cobertura") }) { Text("cobertura de cada variable: ¿ya hay 30 días? ›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
        }

        Seccion("¿Qué tan bien te conoces?", "Tu predicción de cada noche contra lo que pasó al día siguiente.", plegable = true) {
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

        Seccion("A qué hora aparecen tus emociones", "Lo que sentías en cada registro de ánimo, por hora del día (las seis más frecuentes).", plegable = true) {
            val regs = Repo.momentos.filter { Catalogo.esRegistro(it) }
            val conteo = regs.flatMap { Catalogo.emocionesDe(it) }.filter { it != Catalogo.OTRO && it != Catalogo.NO_SE }.groupingBy { it }.eachCount()
            val top = conteo.entries.sortedByDescending { it.value }.take(6).map { it.key }
            if (regs.size < 5) Vacio("Registra al menos 5 veces tu ánimo para ver el patrón por hora.")
            else Tarjeta {
                top.forEach { e ->
                    val porHora = IntArray(24)
                    regs.filter { e in Catalogo.emocionesDe(it) }.forEach { m -> Fechas.horaDe(m.ts).take(2).toIntOrNull()?.let { porHora[it]++ } }
                    if (porHora.sum() > 0) {
                        Text(Catalogo.nombreSentimiento(e, Repo.config.discreto) + " · ${porHora.sum()}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
                        Horas(porHora)
                    }
                }
                Ayuda("De izquierda a derecha: de 0:00 a 23:00.", Modifier.padding(top = 6.dp))
            }
        }

        ConQuien()
        Seccion("Personas, temas e hitos", plegable = true) {
            val eq = Repo.equivalencias()
            val p = Repo.dias.values.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct() }.groupingBy { it }.eachCount()
            val t = Repo.dias.values.flatMap { d -> Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct() }.groupingBy { it }.eachCount()
            val h = Repo.dias.values.flatMap { it.hitos }.groupingBy { it }.eachCount()
            val ids = vars.map { it.id }.toSet()
            fun id(pre: String, k: String): String? = (pre + Texto.slug(k).take(40)).takeIf { it in ids }
            Ayuda("Toca una para ver todo lo que se mueve con ella.", Modifier.padding(bottom = 6.dp))
            Lista("Personas", p, "@", { id("per_", it) }, ::abrirFicha); Lista("Temas", t, "#", { id("tema_", it) }, ::abrirFicha); Lista("Hitos", h, "", { id("hito_", it) }, ::abrirFicha)
            TextButton(onClick = { nav.ir("totales") }) { Text("Ver menciones con fechas y por mes en Totales ›") }
        }

        Seccion("Lugares y ánimo", plegable = true) {
            val lugares = vars.filter { it.id.startsWith("lug_") || it.id.startsWith("ciu_") }
            if (lugares.isNotEmpty()) Tarjeta(Modifier.padding(bottom = 8.dp)) {
                lugares.forEach { v ->
                    Row(Modifier.fillMaxWidth().clickable { abrirFicha(v.id) }.padding(vertical = 6.dp)) {
                        Text(v.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text("›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            TextButton(onClick = { nav.ir("lugares") }) { Text("Ver el mapa emocional de tus lugares ›") }
        }

        EtapasDeVida(panel)
    }

    eligiendo?.let { eje ->
        ElegirVariable(vars, { eligiendo = null }) { id -> if (eje == 'x') x = id else y = id; eligiendo = null }
    }
}

private val FOCOS = listOf("todas" to "todas", "animo" to "con tu ánimo", "sueno" to "con tu sueño", "personas" to "personas y vida", "lugares" to "lugares y viajes",
    "cuerpo" to "cuerpo y comida", "telefono" to "teléfono y social", "cultura" to "lo que lees, ves y escuchas", "dinero" to "dinero")

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
        "cuerpo" -> v.grupo in setOf("Cuerpo", "Nutrición", "Salud")
        "telefono" -> v.grupo in setOf("Teléfono", "Social", "Apps", "Notificaciones")
        "personas" -> v.grupo in setOf("Personas", "Social", "Etapas de vida", "Temas", "Hitos")
        "lugares" -> v.grupo in setOf("Lugares", "Mundo", "Movilidad")
        "cultura" -> v.grupo in setOf("Cultura", "Música", "YouTube", "Lectura", "Texto")
        "dinero" -> v.grupo == "Dinero"
        else -> true
    }
    return es(c.a) || es(c.b)
}

@Composable
private fun Lista(titulo: String, conteo: Map<String, Int>, prefijo: String, id: (String) -> String? = { null }, abrir: (String) -> Unit = {}) {
    Tarjeta(Modifier.padding(bottom = 8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        val top = conteo.entries.sortedByDescending { it.value }.take(12)
        if (top.isEmpty()) Ayuda("Nada aún.")
        val base = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
        val cifras = estiloCifras(base)
        top.forEach { (k, v) ->
            val vid = id(k)
            Row(Modifier.fillMaxWidth().then(if (vid != null) Modifier.clickable { abrir(vid) } else Modifier).padding(vertical = 3.dp)) {
                Text(prefijo + k + if (vid != null) "  ›" else "", style = base, modifier = Modifier.weight(1f),
                    color = if (vid != null) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Unspecified)
                Text("$v ${if (v == 1) "día" else "días"}", style = cifras)
            }
        }
    }
}

@Composable
private fun Horas(v: IntArray) {
    val color = MaterialTheme.colorScheme.primary
    val fondo = MaterialTheme.colorScheme.surfaceVariant
    val eg = estiloGrafica()
    Canvas(Modifier.fillMaxWidth().aspectRatio(10f)) {
        val max = (v.maxOrNull() ?: 1).coerceAtLeast(1)
        val w = size.width / 24
        for (i in 0 until 24) {
            barraGrafica(eg, Offset(i * w + 1, 0f), Size(w - 2, size.height), fondo, pista = true)
            val h = size.height * v[i] / max
            if (v[i] > 0) barraGrafica(eg, Offset(i * w + 1, size.height - h), Size(w - 2, h), color)
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
    val eg = estiloGrafica()
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
            // Cada día es una celda con la silueta de barra del tema (sin adornos internos: son muy chicas).
            barraGrafica(eg, Offset(izq + w * celda, dd * celda), Size(lado, lado), col, pista = true)
        }
    }
    Ayuda("$registrados días registrados en este periodo.", Modifier.padding(top = 6.dp))
}

@Composable
private fun Dispersion(ps: List<Par>, nx: String, ny: String) {
    val acento = MaterialTheme.colorScheme.primary
    val linea = MaterialTheme.colorScheme.outline
    val texto = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val eg = estiloGrafica()
    val cifras = pincelDeCifras(MaterialTheme.colorScheme.onSurfaceVariant, 26f)
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
        val marcasY = listOf(y0, (y0 + y1) / 2, y1)
        val marcasX = listOf(x0, (x0 + x1) / 2, x1)
        rejillaGrafica(eg, marcasY.map { py(it) }, linea, area = Rect(l, t, size.width - r, size.height - b))
        ejeGrafica(eg, Offset(l, size.height - b), Offset(size.width - r, size.height - b), linea, marcas = marcasX.map { px(it) })
        marcasY.forEach { v -> drawContext.canvas.nativeCanvas.drawText(fmt(v), 0f, py(v) + 9f, cifras) }
        marcasX.forEach { v ->
            val s = fmt(v); drawContext.canvas.nativeCanvas.drawText(s, px(v) - cifras.measureText(s) / 2, size.height - b + 34f, cifras)
        }
        // Recta de tendencia
        val mx = xs.average(); val my = ys.average()
        val sxx = xs.sumOf { (it - mx) * (it - mx) }
        if (sxx > 0) {
            val m = xs.indices.sumOf { (xs[it] - mx) * (ys[it] - my) } / sxx; val c = my - m * mx
            fun cl(v: Double) = v.coerceIn(y0, y1)
            lineaMeta(eg, Offset(px(x0), py(cl(m * x0 + c))), Offset(px(x1), py(cl(m * x1 + c))), acento, 4f)
        }
        ps.forEachIndexed { i, p ->
            val jx = if (discX) ((sin(i * 12.9898 + 1) * 43758.5453) % 1) * 0.3 else 0.0
            val jy = if (discY) ((sin(i * 78.233 + 7) * 12543.231) % 1) * 0.3 else 0.0
            val c = Offset(px(p.x + jx), py(p.y + jy))
            puntoGrafica(eg, c, 9f, acento.copy(alpha = 0.45f))
            puntoGrafica(eg, c, 9f, acento, hueco = true)
        }
        val etq = (if (nx.length > 40) nx.take(39) + "…" else nx) + " →"
        drawContext.canvas.nativeCanvas.drawText(etq, (size.width - pincel.measureText(etq)) / 2 + l / 2, size.height - 6f, pincel)
    }
}



/** Promedio de la valencia en un rango de días (diario o, si no hay, avisos de ánimo). */
private fun animoEntre(dias: List<String>): Double? = dias.mapNotNull { f ->
    Repo.dias[f]?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == f && it.esAnimo }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average()
}.takeIf { it.isNotEmpty() }?.average()

/**
 * Antes y después de cada cambio grande de «Conocerte» (mudanza, pareja, trabajo): tu ánimo, sueño, pantalla, pasos,
 * gasto y vida social en los 60 días anteriores contra los 60 siguientes.
 */
@Composable
private fun EtapasDeVida(panel: Map<String, Map<String, Double?>>) {
    // Los cambios de un mismo día (un mismo guardado) van en una sola tarjeta: los números serían los mismos (P-18).
    // El primer llenado de «Conocerte» no es un cambio de vida: no sale aquí (P-26).
    val primero = io.github.ruddy2003.vestigium.data.Perfiles.primerGuardado(Repo.config.perfil_historial)
    val cambios = Repo.config.perfil_historial.filter { it.fecha != primero }.groupBy { it.fecha }.entries.sortedByDescending { it.key }.take(8)
    if (cambios.isEmpty() || panel.isEmpty()) return
    val claves = listOf("valencia" to "ánimo", "sueno_horas" to "sueño (h)", "pantalla_min" to "pantalla (min)", "pasos" to "pasos",
        "gasto_total" to "gasto", "personas" to "personas mencionadas", "palabras" to "palabras escritas")
    Seccion("Antes y después", "Cada cambio que guardaste en «Conocerte»: los 60 días anteriores contra los 60 siguientes. Muchas cosas cambian a la vez; tómalo como una pista.", plegable = true) {
        cambios.forEach { (fecha, lista) ->
            Tarjeta(Modifier.padding(bottom = 8.dp)) {
                Text("${Fechas.corto(fecha)} ${fecha.take(4)} · ${lista.joinToString(", ") { it.campo }}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                lista.forEach { c -> Ayuda((if (lista.size > 1) "${c.campo}: " else "") + "${c.antes.ifBlank { "(vacío)" }}  →  ${c.despues.ifBlank { "(vacío)" }}") }
                var alguno = false
                claves.forEach { (k, n) ->
                    val antes = (1..60).mapNotNull { i -> Fechas.sumar(fecha, -i.toLong()).let { f -> panel[f]?.get(k)?.let { f to it } } }
                    val despues = (0 until 60).mapNotNull { i -> Fechas.sumar(fecha, i.toLong()).let { f -> panel[f]?.get(k)?.let { f to it } } }
                    // t de Welch con el error estándar corregido porque los días seguidos se parecen (P-17).
                    val d = Estadistica.diferenciaEnDias(despues, antes) ?: return@forEach
                    alguno = true
                    val claro = d.lo > 0 || d.hi < 0
                    io.github.ruddy2003.vestigium.ui.Clave(n, "%s → %s (%+.1f%s)".format(fmtCorto(antes.map { it.second }.average()), fmtCorto(despues.map { it.second }.average()), d.d, if (claro) "" else ", podría ser azar"), ancho = 12)
                }
                if (!alguno) Ayuda("Todavía no hay suficientes días a ambos lados del cambio.", Modifier.padding(top = 6.dp))
            }
        }
    }
}

private fun fmtCorto(v: Double) = if (kotlin.math.abs(v) >= 100) "%,.0f".format(v) else "%.1f".format(v)


/** Tu ánimo en los avisos según con quién estabas (lo que contestas en «¿con quién estás?»). */
@Composable
private fun ConQuien() {
    val mu = Repo.momentos.filter { it.esAnimo && it.valencia != null && it.con_quien.isNotEmpty() }
    if (mu.isEmpty()) return
    Seccion("Ánimo según con quién estás", "Promedio de la valencia en los avisos de ánimo, según con quién dijiste que estabas.", plegable = true) {
        Tarjeta {
            if (mu.size < 10) Ayuda("Llevas ${mu.size} avisos con compañía. Con 10 o más empieza a verse algo; con 30, ya es una pista.")
            val general = mu.mapNotNull { it.valencia }.average()
            val cs = MaterialTheme.colorScheme
            val oscuro = io.github.ruddy2003.vestigium.ui.esOscuro()
            val eg = estiloGrafica()
            val cifras = estiloCifras(MaterialTheme.typography.labelMedium)
            io.github.ruddy2003.vestigium.data.Catalogo.CON_QUIEN.forEach { o ->
                val l = mu.filter { o.id in it.con_quien }.mapNotNull { it.valencia }
                if (l.isEmpty()) return@forEach
                val m = l.average()
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(o.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.32f))
                    Canvas(Modifier.weight(0.48f).aspectRatio(10f)) {
                        barraGrafica(eg, Offset.Zero, size, cs.surfaceVariant, horizontal = true, pista = true)
                        barraGrafica(eg, Offset.Zero, Size(size.width * ((m - 1) / 6).toFloat(), size.height), io.github.ruddy2003.vestigium.ui.Tonos.valencia(m, oscuro) ?: cs.primary, horizontal = true)
                        val xg = size.width * ((general - 1) / 6).toFloat()
                        lineaMeta(eg, Offset(xg, 0f), Offset(xg, size.height), cs.onSurface, 2f)
                    }
                    Text("%.1f · %d".format(m, l.size), style = cifras, modifier = Modifier.weight(0.2f).padding(start = 8.dp))
                }
            }
            Ayuda("La raya es tu promedio general (%.1f). Cuidado: con quién estás también depende de la hora y el lugar.".format(general), Modifier.padding(top = 6.dp))
        }
    }
}
