@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import io.github.ruddy2003.vestigium.ui.aMillis
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.data.ColdTurkey
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Experimentos
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Personas
import io.github.ruddy2003.vestigium.data.RedPersonas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Suplemento
import io.github.ruddy2003.vestigium.data.Suplementos
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private fun cifra(x: Double) = if (x == Math.floor(x)) "%.0f".format(x) else "%.1f".format(x)

// ============================================================================ suplementos

/** Suplementos: tomar en un toque, ver el nivel estimado y ajustar el modelo de cada uno. */
@Composable
fun PantallaSuplementos(nav: Nav) {
    val c = Repo.config
    val hoy = Fechas.diaLogico()
    var editando by remember { mutableStateOf<Suplemento?>(null) }
    var toma by remember { mutableStateOf<io.github.ruddy2003.vestigium.data.TomaSuplemento?>(null) }
    // Cuándo la tomaste: null = ahora. «ayer» o «otra fecha u hora…» para lo que olvidaste anotar (N-07).
    var cuando by remember { mutableStateOf<java.time.LocalDateTime?>(null) }
    val ctx = androidx.compose.ui.platform.LocalContext.current
    Hoja("Suplementos", nav.atras, "Lo que tomas, en un toque. La app estima cuánto queda en tu cuerpo con un modelo simple; no es una medición ni un consejo médico.") {
        val activos = Suplementos.activos(c)
        if (activos.isEmpty()) Vacio("No tienes suplementos activos. Agrega uno abajo.")
        val dia = cuando?.let { Fechas.diaLogicoDeMillis(it.aMillis()) } ?: hoy
        if (activos.isNotEmpty()) {
            Etiqueta("¿Cuándo la tomaste?")
            io.github.ruddy2003.vestigium.ui.SelectorHora(cuando, ayer = true) { cuando = it }
        }
        fun tomar(s: Suplemento, cantidad: Double) {
            val t = Suplementos.registrar(s, cantidad, cuando?.let { Fechas.isoDeMillis(it.aMillis()) } ?: Fechas.ahoraIso())
            if (cuando != null) android.widget.Toast.makeText(ctx, "Anotado para el ${Fechas.titulo(t.fecha).lowercase()} a las ${Fechas.horaDe(t.ts)}", android.widget.Toast.LENGTH_SHORT).show()
            cuando = null
        }
        activos.forEach { s ->
            Tarjeta(Modifier.padding(top = 10.dp)) {
                val tomas = Repo.tomas.filter { it.suplemento == s.id && it.fecha == dia }
                val ese = if (dia == hoy) "Hoy" else Fechas.titulo(dia)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(s.nombre, style = MaterialTheme.typography.titleMedium)
                        Ayuda(if (tomas.isEmpty()) "$ese: todavía no" else "$ese: " + tomas.joinToString(" + ") { "${cifra(it.cantidad)} ${s.unidad} a las ${Fechas.horaDe(it.ts)}" })
                    }
                    TextButton(onClick = { editando = s }) { Text("ajustar") }
                }
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonPrincipal("Tomé ${cifra(s.dosis)} ${s.unidad}" + if (cuando != null) " · ${Fechas.corto(dia)}" else "", Modifier.weight(1f)) { tomar(s, s.dosis) }
                    if (s.modelo == "carga") BotonSecundario("Carga ×4") { tomar(s, s.dosis * 4) }
                    if (tomas.isNotEmpty()) BotonSecundario("corregir") { toma = tomas.last() }
                }
                NivelSuplemento(s, hoy)
            }
        }
        Seccion("Recordatorio", "Un aviso al día si falta alguno, con «Ya los tomé» (anota la dosis habitual) y «En una hora».") {
            Tarjeta {
                Interruptor("Recordarme cada día", c.suplementos_hora != null) { v -> Repo.cambiarConfig { it.copy(suplementos_hora = if (v) 9 else null) } }
                c.suplementos_hora?.let { h ->
                    Etiqueta("Hora")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(7, 8, 9, 10, 12, 14, 18, 21, 22).forEach { x -> Ficha("%02d:00".format(x), h == x) { Repo.cambiarConfig { it.copy(suplementos_hora = x) } } }
                    }
                }
            }
        }
        Seccion("Agregar", "Del catálogo; después puedes cambiar dosis, unidad y parámetros.") {
            Tarjeta {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Suplementos.CATALOGO.forEach { s ->
                        val ya = c.suplementos.firstOrNull { it.id == s.id }
                        Ficha(s.nombre, ya?.activo == true) {
                            Repo.cambiarConfig { cc ->
                                val previo = cc.suplementos.firstOrNull { it.id == s.id }
                                cc.copy(suplementos = if (previo == null) cc.suplementos + s else cc.suplementos.map { if (it.id == s.id) it.copy(activo = !it.activo) else it })
                            }
                        }
                    }
                }
                if (c.suplementos.any { it.id == "probioticos" && it.activo }) {
                    Ayuda("Probióticos: también se cuentan como porciones estimadas los alimentos de tu bitácora de MacroFactor que contienen: " + c.probioticos_palabras.joinToString(", ") + ".",
                        Modifier.padding(top = 10.dp))
                    var palabras by remember { mutableStateOf(c.probioticos_palabras.joinToString(", ")) }
                    OutlinedTextField(palabras, { palabras = it }, Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Palabras (separadas por coma)") })
                    TextButton(onClick = { Repo.cambiarConfig { it.copy(probioticos_palabras = palabras.split(',').map { p -> p.trim() }.filter { p -> p.isNotEmpty() }) } }) { Text("guardar palabras") }
                }
            }
        }
        val pasadas = Repo.tomas.sortedBy { it.ts }.takeLast(14).reversed()
        if (pasadas.isNotEmpty()) Seccion("Últimas tomas", "Toca una para corregir la cantidad o la hora, o para quitarla.") {
            Tarjeta {
                pasadas.forEachIndexed { i, t ->
                    if (i > 0) HorizontalDivider()
                    val s = Suplementos.de(t.suplemento)
                    Row(Modifier.fillMaxWidth().clickable { toma = t }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${s?.nombre ?: t.suplemento} · ${cifra(t.cantidad)} ${s?.unidad ?: ""} · ${Fechas.corto(t.fecha)} ${Fechas.horaDe(t.ts)}", style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f))
                        Text("›", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
    editando?.let { s -> EditorSuplemento(s) { editando = null } }
    toma?.let { t -> DialogoToma(t) { toma = null } }
}

/** El nivel de los últimos 60 días: reserva en % (carga), cantidad en el cuerpo (vida media) o porciones. */
@Composable
private fun NivelSuplemento(s: Suplemento, hoy: String) {
    val cs = MaterialTheme.colorScheme
    val dias = (59 downTo 0).map { Fechas.sumar(hoy, -it.toLong()) }
    val valores: List<Double?> = remember(Repo.tomas, s) {
        when (s.modelo) {
            "carga" -> { val m = Suplementos.carga(s, hoy); dias.map { m[it] } }
            "vida_media" -> dias.map { f -> if (Repo.tomas.any { it.suplemento == s.id && it.fecha <= f }) Suplementos.cantidadEn(s, Fechas.finDia(f) + 5 * 3_600_000L - 1) else null }
            else -> { val d = Suplementos.dosisPorDia(s.id); val e = if (s.id == "probioticos") Suplementos.probioticosEstimados() else emptyMap(); dias.map { f -> ((d[f] ?: 0.0) + (e[f] ?: 0.0)).takeIf { it > 0 } } }
        }
    }
    if (valores.all { it == null }) { Ayuda("Cuando registres tomas, aquí verás tu nivel estimado.", Modifier.padding(top = 8.dp)); return }
    val ultimo = valores.lastOrNull { it != null }
    Ayuda(when (s.modelo) {
        "carga" -> "Reserva estimada hoy: ${ultimo?.roundToInt() ?: 0} %. Se llena en unos ${s.carga_dias} días de dosis normal y baja en unos ${s.lavado_dias} sin tomar."
        "vida_media" -> "En el cuerpo al final del día: ${cifra(ultimo ?: 0.0)} ${s.unidad} (vida media de ${cifra(s.vida_media_h)} h)."
        else -> "Porciones por día en los últimos 60 días."
    }, Modifier.padding(top = 8.dp))
    val max = if (s.modelo == "carga") 100.0 else (valores.filterNotNull().maxOrNull() ?: 1.0).coerceAtLeast(1e-6)
    Canvas(Modifier.fillMaxWidth().height(70.dp).padding(top = 6.dp)) {
        val w = size.width; val h = size.height; val n = valores.size
        if (s.modelo == "carga") listOf(0.5f, 1f).forEach { k -> drawLine(cs.outlineVariant, Offset(0f, h * (1 - k)), Offset(w, h * (1 - k)), 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))) }
        if (s.modelo == "exposicion") valores.forEachIndexed { i, v ->
            if (v != null) { val bh = (v / max * h).toFloat(); drawRect(cs.primary, Offset(i * w / n + 1f, h - bh), androidx.compose.ui.geometry.Size(w / n - 2f, bh)) }
        } else {
            val p = Path(); var abierto = false
            valores.forEachIndexed { i, v ->
                if (v == null) { abierto = false; return@forEachIndexed }
                val x = i * w / (n - 1); val y = h - (v / max * h).toFloat()
                if (!abierto) { p.moveTo(x, y); abierto = true } else p.lineTo(x, y)
            }
            drawPath(p, cs.primary, style = Stroke(2.5f * density))
        }
    }
}

@Composable
private fun EditorSuplemento(s0: Suplemento, cerrar: () -> Unit) {
    var nombre by remember { mutableStateOf(s0.nombre) }
    var unidad by remember { mutableStateOf(s0.unidad) }
    var dosis by remember { mutableStateOf(cifra(s0.dosis)) }
    var modelo by remember { mutableStateOf(s0.modelo) }
    var vida by remember { mutableStateOf(cifra(s0.vida_media_h)) }
    var carga by remember { mutableStateOf(s0.carga_dias.toString()) }
    var lavado by remember { mutableStateOf(s0.lavado_dias.toString()) }
    AlertDialog(onDismissRequest = cerrar, title = { Text(s0.nombre) },
        confirmButton = {
            TextButton(onClick = {
                val n = s0.copy(nombre = nombre.trim().ifBlank { s0.nombre }, unidad = unidad.trim().ifBlank { s0.unidad }, dosis = dosis.replace(',', '.').toDoubleOrNull() ?: s0.dosis,
                    modelo = modelo, vida_media_h = vida.replace(',', '.').toDoubleOrNull() ?: s0.vida_media_h, carga_dias = carga.toIntOrNull() ?: s0.carga_dias, lavado_dias = lavado.toIntOrNull() ?: s0.lavado_dias)
                Repo.cambiarConfig { c -> c.copy(suplementos = c.suplementos.map { if (it.id == s0.id) n else it }) }
                cerrar()
            }) { Text("Guardar") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { Repo.cambiarConfig { c -> c.copy(suplementos = c.suplementos.map { if (it.id == s0.id) it.copy(activo = false) else it }) }; cerrar() }) { Text("dejar de usar") }
                TextButton(onClick = cerrar) { Text("cancelar") }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(nombre, { nombre = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Nombre") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    OutlinedTextField(dosis, { dosis = it }, Modifier.weight(1f), singleLine = true, label = { Text("Dosis habitual") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(unidad, { unidad = it }, Modifier.weight(1f), singleLine = true, label = { Text("Unidad") })
                }
                Etiqueta("Modelo")
                FilaFichas(Suplementos.MODELOS, modelo) { modelo = it ?: modelo }
                when (modelo) {
                    "carga" -> {
                        OutlinedTextField(carga, { carga = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Días para llenar la reserva") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(lavado, { lavado = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Días para vaciarla sin tomar") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        Ayuda("Creatina: con 3–5 g diarios la reserva del músculo se llena en unas 4 semanas (en una con dosis de carga) y vuelve a lo normal 4 a 6 semanas después de dejarla.", Modifier.padding(top = 6.dp))
                    }
                    "vida_media" -> OutlinedTextField(vida, { vida = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Vida media (horas)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    else -> Ayuda("Solo se cuentan porciones por día.", Modifier.padding(top = 6.dp))
                }
                Ayuda("Los valores iniciales son aproximados. Cámbialos si sabes algo mejor.", Modifier.padding(top = 8.dp))
            }
        })
}

// ============================================================================ laptop (Cold Turkey)

/** La laptop: minutos por día, apps y sitios con su categoría, y comparación con tu enfoque del cronómetro. */
@Composable
fun PantallaLaptop(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    val abrir = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) scope.launch { msg = withContext(Dispatchers.IO) { runCatching { ColdTurkey.importar(ctx, uris) }.getOrElse { "Algo falló: ${it.message}" } } }
    }
    val cs = MaterialTheme.colorScheme
    Hoja("Laptop", nav.atras, "Lo que mide Cold Turkey en tu computadora: minutos por app y por sitio. Importa los dos CSV (apps y sitios); reimportar un día lo reemplaza.") {
        msg?.let { Aviso(it) }
        BotonPrincipal("Importar de Cold Turkey (.csv)", Modifier.fillMaxWidth().padding(top = 10.dp)) { abrir.launch(arrayOf("text/csv", "text/comma-separated-values", "text/plain", "*/*")) }
        Ayuda("En Cold Turkey: Statistics → elige el periodo → Export. Sale un CSV de apps y otro de sitios; puedes elegir los dos a la vez.", Modifier.padding(top = 6.dp))
        val hoy = Fechas.diaLogico()
        val dias = (13 downTo 0).map { Fechas.sumar(hoy, -it.toLong()) }
        val res = remember(Repo.laptop, Repo.config.laptop_categorias) { Repo.laptop.mapValues { ColdTurkey.resumen(it.value) } }
        if (Repo.laptop.isEmpty()) { Vacio("Todavía no hay datos de la laptop."); return@Hoja }
        Seccion("Últimas dos semanas", "Cada barra es un día, partida por categoría.") {
            Tarjeta {
                val cats = ColdTurkey.CATEGORIAS.map { it.id }
                val colores = listOf(cs.primary, cs.tertiary, cs.secondary, cs.outline)
                val max = dias.maxOf { f -> cats.sumOf { k -> res[f]?.get("laptop_${k}_min") ?: 0.0 } }.coerceAtLeast(30.0)
                Canvas(Modifier.fillMaxWidth().height(120.dp)) {
                    val bw = size.width / dias.size
                    dias.forEachIndexed { i, f ->
                        var y = size.height
                        cats.forEachIndexed { k, cat -> val m = res[f]?.get("laptop_${cat}_min") ?: 0.0; val h = (m / max * size.height).toFloat()
                            drawRect(colores[k], Offset(i * bw + bw * 0.15f, y - h), androidx.compose.ui.geometry.Size(bw * 0.7f, h)); y -= h }
                    }
                }
                Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ColdTurkey.CATEGORIAS.forEachIndexed { k, o -> Text("■ ${o.nombre}", style = MaterialTheme.typography.labelSmall, color = colores[k]) }
                }
                val ult = dias.mapNotNull { res[it]?.get("laptop_min") }
                if (ult.isNotEmpty()) Ayuda("Promedio: ${(ult.average()).roundToInt()} min al día en los días con datos.", Modifier.padding(top = 6.dp))
            }
        }
        // Comparación con el cronómetro: lo productivo de la laptop frente a lo que marcaste como enfoque.
        val enfoque = remember(Repo.tramos) { Repo.tramos.filter { it.tipo != "siesta" }.groupBy { it.fecha }.mapValues { (_, l) -> l.sumOf { it.min }.toDouble() } }
        val comunes = res.keys.filter { it >= Fechas.sumar(hoy, -30) && enfoque.containsKey(it) }
        Seccion("Contra tu enfoque", "Días de los últimos 30 con laptop y cronómetro.") {
            Tarjeta {
                if (comunes.size < 3) Ayuda("Hace falta que coincidan al menos 3 días con datos de la laptop y bloques de enfoque.")
                else {
                    val prod = comunes.map { res[it]?.get("laptop_productivo_min") ?: 0.0 }.average(); val enf = comunes.map { enfoque[it] ?: 0.0 }.average()
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Cifra("${prod.roundToInt()} min", "productivo en la laptop", Modifier.weight(1f)); Cifra("${enf.roundToInt()} min", "enfoque con el cronómetro", Modifier.weight(1f))
                    }
                    Ayuda(if (prod > enf * 1.3) "La laptop marca bastante más tiempo productivo que tus bloques de enfoque: quizá trabajas sin cronómetro, o la categoría incluye cosas que no son trabajo."
                        else if (enf > prod * 1.3) "Tu enfoque supera lo productivo de la laptop: parte de tu trabajo ocurre fuera de ella (papel, lectura, teléfono)." else "Coinciden bastante.", Modifier.padding(top = 6.dp))
                }
            }
        }
        Seccion("Apps y sitios", "Toca la categoría para cambiarla. El navegador no cuenta en las categorías: sus minutos ya están en los sitios.") {
            Tarjeta {
                ColdTurkey.nombres().take(25).forEachIndexed { i, (n, m) ->
                    if (i > 0) HorizontalDivider()
                    val cat = ColdTurkey.categoria(n)
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(n, style = MaterialTheme.typography.bodyMedium)
                            Text("${(m / 60).let { "%.1f".format(it) }} h en total" + if (n in ColdTurkey.NAVEGADORES) " · navegador" else "", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                        }
                        val sig = ColdTurkey.CATEGORIAS[(ColdTurkey.CATEGORIAS.indexOfFirst { it.id == cat } + 1) % ColdTurkey.CATEGORIAS.size].id
                        Ficha(ColdTurkey.CATEGORIAS.first { it.id == cat }.nombre, cat != "otro") { Repo.cambiarConfig { c -> c.copy(laptop_categorias = c.laptop_categorias + (n to sig)) } }
                    }
                }
            }
        }
    }
}

// ============================================================================ red de personas

/** En la ficha de una persona: «¿A quién conoce?». Cada toque cambia el tipo de vínculo. */
@Composable
fun AQuienConoce(nombre: String) {
    val menc = remember(Repo.dias, Repo.momentos) { Personas.menciones() }
    val otras = (Repo.config.personas.map { it.nombre } + menc.entries.sortedByDescending { it.value.first }.take(20).map { it.key })
        .distinct().filter { it != nombre }.take(24)
    if (otras.isEmpty()) return
    Etiqueta("¿A quién conoce?")
    Ayuda("Toca para cambiar: se conocen → familia entre sí → pareja → colegas → no se conocen → sin decir.")
    FlowRow(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        otras.forEach { o ->
            val t = RedPersonas.tipo(nombre, o)
            Ficha("@$o" + (t?.let { " · " + RedPersonas.nombreTipo(it).lowercase() } ?: ""), t != null && t != "no_se_conocen") { RedPersonas.poner(nombre, o, RedPersonas.siguiente(t)) }
        }
    }
}

/** La red de tu gente: un gráfico de fuerzas con lo que dijiste (líneas firmes) y lo que se deriva de aparecer juntas (líneas finas). */
@Composable
fun PantallaRed(nav: Nav) {
    val cs = MaterialTheme.colorScheme
    var soloDichas by remember { mutableStateOf(false) }
    val datos = remember(Repo.dias, Repo.momentos, Repo.config.vinculos, Repo.config.personas, soloDichas) {
        val (n, a) = RedPersonas.calcular(minJuntos = 2)
        val aa = if (soloDichas) a.filter { it.tipo != null } else a
        // El grado y los grupos, con las líneas que se ven (P-25).
        RedPersonas.grados(n, aa)
        RedPersonas.acomodar(n, aa); Triple(n, aa, RedPersonas.componentes(n, aa))
    }
    val (nodos, aristas, comp) = datos
    var elegido by remember { mutableStateOf<String?>(null) }
    val medidor = rememberTextMeasurer()
    val estilo = MaterialTheme.typography.labelSmall.copy(color = cs.onSurface)
    val tonos = mapOf("familia" to cs.tertiary, "pareja" to cs.error, "amistad" to cs.secondary, "casa" to cs.primary, "trabajo" to cs.outline, "expareja" to cs.error.copy(alpha = 0.45f))
    Hoja("Red", nav.atras, "Quién conoce a quién. Las líneas firmes son lo que dijiste en Tu gente; las finas, personas que aparecen juntas en tu diario al menos dos días.") {
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todo", !soloDichas) { soloDichas = false }; Ficha("solo lo que dije", soloDichas) { soloDichas = true }
        }
        if (nodos.isEmpty()) { Vacio("Menciona a personas con @ en tu diario, o di quién conoce a quién en Tu gente."); return@Hoja }
        val lazos = aristas.filter { it.tipo != "no_se_conocen" }
        val n = nodos.size
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("$n", "personas", Modifier.weight(1f))
            Cifra(if (n > 1) "%.0f%%".format(100.0 * lazos.size / (n * (n - 1) / 2.0)) else "—", "densidad", Modifier.weight(1f))
            Cifra("$comp", if (comp == 1) "grupo" else "grupos", Modifier.weight(1f))
        }
        val maxDias = nodos.maxOf { it.dias }.coerceAtLeast(1)
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f).padding(top = 10.dp).pointerInput(nodos) {
            detectTapGestures { o -> elegido = nodos.minByOrNull { val dx = it.x * size.width - o.x; val dy = it.y * size.height - o.y; dx * dx + dy * dy }?.nombre }
        }) {
            val w = size.width; val h = size.height
            lazos.forEach { a ->
                val p = nodos.firstOrNull { it.nombre == a.a } ?: return@forEach; val q = nodos.firstOrNull { it.nombre == a.b } ?: return@forEach
                val resaltado = elegido != null && (a.a == elegido || a.b == elegido)
                val col = if (a.tipo != null) cs.primary else cs.onSurfaceVariant
                drawLine(col.copy(alpha = if (resaltado) 0.9f else if (a.tipo != null) 0.55f else 0.22f), Offset(p.x * w, p.y * h), Offset(q.x * w, q.y * h),
                    (if (a.tipo != null) 2f else 1f) * density, pathEffect = if (a.tipo == "colegas") PathEffect.dashPathEffect(floatArrayOf(6f * density, 4f * density)) else null)
            }
            nodos.forEach { nd ->
                val r = (4f + 8f * kotlin.math.sqrt(nd.dias.toFloat() / maxDias)) * density
                // Color por grupo de relación, como la Constelación: una amistad cercana es «amistad», un colega es «trabajo» (P-25).
                val col = tonos[nd.grupo] ?: cs.onSurfaceVariant
                drawCircle(col, r, Offset(nd.x * w, nd.y * h))
                if (nd.nombre == elegido) drawCircle(cs.onSurface, r + 3f * density, Offset(nd.x * w, nd.y * h), style = Stroke(1.5f * density))
                if (nd.dias >= maxDias / 4 || nd.nombre == elegido || n <= 15) {
                    val t = medidor.measure("@" + nd.nombre, estilo)
                    drawText(t, topLeft = Offset((nd.x * w - t.size.width / 2).coerceIn(0f, w - t.size.width), (nd.y * h + r + 2f).coerceAtMost(h - t.size.height)))
                }
            }
        }
        elegido?.let { e ->
            val nd = nodos.first { it.nombre == e }
            Tarjeta(Modifier.padding(top = 8.dp)) {
                Text("@$e", style = MaterialTheme.typography.titleMedium)
                Ayuda(listOfNotNull(nd.relacion?.let { Personas.nombreRelacion(it) }, "${nd.dias} días mencionada", "unida a ${nd.grado}").joinToString(" · "))
                val suyos = lazos.filter { it.a == e || it.b == e }.sortedByDescending { it.juntos }
                if (suyos.isNotEmpty()) Ayuda(suyos.take(8).joinToString(" · ") { a -> "@" + (if (a.a == e) a.b else a.a) + (a.tipo?.let { " (${RedPersonas.nombreTipo(it).lowercase()})" } ?: " (${a.juntos} días juntas)") },
                    Modifier.padding(top = 4.dp))
            }
        }
        Ayuda("Colores: familia, pareja, amistad, casa y trabajo como en la Constelación. Para la restricción de Burt y la composición de tu red, usa vm_red() en R.", Modifier.padding(top = 10.dp))
        BotonSecundario("Decir quién conoce a quién (Tu gente)", Modifier.fillMaxWidth().padding(top = 8.dp)) { nav.ir("personas") }
    }
}

// ============================================================================ experimentos guiados

/**
 * Experimentos: pregunta e hipótesis escritas y selladas con la hora antes de empezar, diseño de semanas alternas sorteadas o de
 * antes y después, días de lavado y, al final, la diferencia con su intervalo, el tamaño del efecto y un aviso si hay tendencia.
 */
@Composable
fun PantallaExperimentos(nav: Nav) {
    val vars = remember { Estadistica.variables(dinamicas = false) }
    var plantilla by remember { mutableStateOf<String?>(null) }
    var nombre by remember { mutableStateOf("") }
    var pregunta by remember { mutableStateOf("") }
    var hipotesis by remember { mutableStateOf("") }
    var accion by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("valencia") }
    var semanas by remember { mutableStateOf(6) }
    var diseno by remember { mutableStateOf("alterno") }
    var lavado by remember { mutableStateOf(0) }
    Hoja("Experimentos personales", nav.atras,
        "Escribes la pregunta y lo que crees que pasará antes de empezar (queda sellado con la hora). La app decide qué semanas haces la acción y, al final, compara.") {
        // El panel se arma en segundo plano, como en Análisis: así las variables que dependen de MacroFactor valen lo mismo aquí
        // que en las demás pantallas (P-21). La clave son los propios datos, no solo cuántos días hay.
        // En las capturas (sincrono) se calcula de una vez, como en Logros y Cobertura.
        val sinc = io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono
        val panel by androidx.compose.runtime.produceState(if (sinc && Repo.experimentos.isNotEmpty()) Estadistica.panel(dinamicas = false) else emptyMap(),
            Repo.dias, Repo.experimentos, Repo.salud, Repo.nutricion, Repo.momentos) {
            if (!sinc) value = if (Repo.experimentos.isEmpty()) emptyMap() else kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) { Estadistica.panel(dinamicas = false) }
        }
        // Un experimento que mide algo privado no se muestra aquí: su resultado se analiza en R.
        Repo.experimentos.filter { !io.github.ruddy2003.vestigium.data.Privado.esVariable(it.resultado) }.sortedByDescending { it.inicio }
            .forEach { e -> TarjetaExperimento(e, panel, vars.firstOrNull { it.id == e.resultado }?.nombre ?: e.resultado) }
        Seccion("Nuevo experimento") {
            Tarjeta {
                Etiqueta("Empieza de una plantilla (opcional)")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Experimentos.PLANTILLAS.forEach { p ->
                        Ficha(p.nombre, plantilla == p.id) {
                            plantilla = p.id; nombre = p.nombre; pregunta = p.pregunta; accion = p.accion; resultado = p.resultado; diseno = p.diseno; semanas = p.semanas; lavado = p.lavado
                        }
                    }
                }
                Experimentos.PLANTILLAS.firstOrNull { it.id == plantilla }?.let { Ayuda(it.nota, Modifier.padding(top = 4.dp)) }
                OutlinedTextField(nombre, { nombre = it }, Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(pregunta, { pregunta = it }, Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Pregunta") })
                OutlinedTextField(accion, { accion = it }, Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Qué vas a cambiar (la acción)") })
                OutlinedTextField(hipotesis, { hipotesis = it }, Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Qué crees que pasará (hipótesis)") })
                Etiqueta("Qué vas a medir")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (listOf("valencia", "activacion", "sueno_calidad", "sueno_horas", "muestreo_valencia", "pantalla_min", "rt_media", "volumen_kg", "enfoque_min") +
                        listOf(resultado)).distinct().forEach { id -> vars.firstOrNull { it.id == id }?.let { v -> Ficha(v.nombre, resultado == id) { resultado = id } } }
                }
                Etiqueta("Diseño")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("Semanas alternas sorteadas", diseno == "alterno") { diseno = "alterno" }
                    Ficha("Antes y después", diseno == "antes_despues") { diseno = "antes_despues" }
                }
                Ayuda(if (diseno == "alterno") "Sortear evita que lo hagas justo en las semanas en que ya ibas a estar bien. Se sortea por pares de semanas: en cada par, una con la acción y otra sin ella." else "Primero la mitad de las semanas sin la acción y luego con ella. Úsalo cuando el efecto tarda en irse (creatina).",
                    Modifier.padding(top = 4.dp))
                Etiqueta("Duración")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf(4, 6, 8, 12).forEach { s -> Ficha("$s semanas", semanas == s) { semanas = s } } }
                Etiqueta("Días de lavado tras cada cambio")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf(0, 1, 2, 7, 28).forEach { d -> Ficha(if (d == 0) "ninguno" else "$d", lavado == d) { lavado = d } } }
                // Con este lavado, ¿quedan días suficientes en cada condición, sea cual sea el sorteo? (P-18)
                val minimo = remember(diseno, semanas, lavado) { Experimentos.minimoPosible(diseno, semanas, lavado) }
                if (minimo < Experimentos.MIN_DIAS) Aviso("Con $lavado días de lavado tras cada cambio, en $semanas semanas podrían quedar solo $minimo días que cuentan en una condición. " +
                    "Hacen falta al menos ${Experimentos.MIN_DIAS}: alarga el experimento o reduce el lavado.")
                BotonPrincipal("Sellar y empezar el lunes", Modifier.fillMaxWidth().padding(top = 12.dp),
                    habilitado = nombre.isNotBlank() && accion.isNotBlank() && hipotesis.isNotBlank() && minimo >= Experimentos.MIN_DIAS) {
                    val plan = Experimentos.plan(diseno, semanas)
                    val lunes = Fechas.sumar(Fechas.lunesDe(Fechas.diaLogico()), 7)
                    Repo.guardarExperimento(Experimento(Repo.nuevoId(), nombre.trim(), accion.trim(), resultado, lunes, semanas, plan, pregunta = pregunta.trim(),
                        hipotesis = hipotesis.trim(), sellada = Fechas.ahoraIso(), diseno = diseno, lavado_dias = lavado, plantilla = plantilla))
                    nombre = ""; accion = ""; pregunta = ""; hipotesis = ""; plantilla = null
                }
                if (hipotesis.isBlank()) Ayuda("Escribe tu hipótesis: sin ella no se puede sellar.", Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun TarjetaExperimento(e: Experimento, panel: Map<String, Map<String, Double?>>, nombreResultado: String) {
    Seccion(e.nombre) {
        Tarjeta {
            if (e.pregunta.isNotBlank()) Text(e.pregunta, style = MaterialTheme.typography.titleSmall)
            Text("Acción: ${e.accion} · mides: $nombreResultado", style = MaterialTheme.typography.bodyMedium)
            if (e.hipotesis.isNotBlank()) Ayuda("Hipótesis: «${e.hipotesis}»" + (e.sellada?.let { " · sellada el ${Fechas.corto(it.take(10))} a las ${Fechas.horaDe(it)}" } ?: ""), Modifier.padding(top = 4.dp))
            val hoy = Fechas.diaLogico()
            val sem = (Fechas.diasEntre(e.inicio, hoy) / 7).toInt()
            Ayuda(when {
                e.fin != null -> "Terminado el ${Fechas.corto(e.fin)}" + (if (e.fin < Experimentos.ultimoDia(e.copy(fin = null))) ", antes de acabar el plan: los días siguientes no cuentan." else ".")
                Fechas.diasEntre(e.inicio, hoy) < 0 -> "Empieza el ${Fechas.corto(e.inicio)}."
                sem >= e.semanas -> "Terminado."
                else -> "Semana ${sem + 1} de ${e.semanas}: " + (if (e.plan[sem]) "SÍ toca." else "NO toca.") + if (Experimentos.enLavado(e, hoy)) " Hoy es día de lavado (no cuenta)." else ""
            }, Modifier.padding(top = 4.dp))
            Ayuda((if (e.diseno == "antes_despues") "Antes y después · " else "Semanas sorteadas · ") + "plan: " + e.plan.joinToString(" ") { if (it) "SÍ" else "no" } +
                if (e.lavado_dias > 0) " · ${e.lavado_dias} días de lavado tras cada cambio" else "")
            val r = remember(e, panel) { Experimentos.resultado(e, panel) }
            if (r.dif == null) Ayuda(when {
                // Tres casos distintos (P-18): el plan nunca alcanzará, ya terminó, o falta esperar.
                r.posibleSi < 3 || r.posibleNo < 3 -> "Este plan nunca dará resultado: con el lavado solo cuentan ${r.posibleSi} días con la acción y ${r.posibleNo} sin ella (hacen falta 3 de cada una)."
                !r.provisional -> "El experimento terminó sin suficientes días medidos: ${r.con.size} con la acción y ${r.sin.size} sin ella (hacían falta 3 de cada una)."
                else -> "Aún no hay suficientes días medidos en ambas condiciones (${r.con.size} con, ${r.sin.size} sin; hacen falta 3 de cada una)."
            }, Modifier.padding(top = 6.dp))
            else {
                val (d, lo, hi) = r.dif
                if (r.provisional) Aviso("Provisional: el plan sigue en curso. Mirar el resultado a medio camino y parar en la primera «señal» engaña; espera a que termine.")
                Text("Semanas con la acción según el plan: ${"%.2f".format(r.con.average())} · sin ella: ${"%.2f".format(r.sin.average())}. Diferencia ${"%+.2f".format(d)} (intervalo de 95 %: ${"%.2f".format(lo)} a ${"%.2f".format(hi)}).",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
                if (!r.provisional) Ayuda(if (lo > 0 || hi < 0) "El intervalo no toca el cero: es una señal para ti." else "El intervalo incluye el cero: todavía no se distingue del azar.")
                r.d?.let { Ayuda("Tamaño del efecto (d de Cohen): ${"%.2f".format(it)}, ${Experimentos.tamano(it)}.") }
                Ayuda("Comparación según el plan: semanas que tocaba la acción contra semanas que no, la hayas hecho o no. Es la comparación justa; si no siempre cumpliste, el efecto real puede ser mayor. " +
                    "El intervalo cuenta que los días seguidos se parecen.", Modifier.padding(top = 2.dp))
                // El aviso fuerte depende del plan real: un sorteo que salió en bloque también es un antes y después (P-18).
                val bloque = Experimentos.enBloque(e)
                if (r.tendenciaClara) Ayuda("Ojo: dentro de cada condición, tu medida venía cambiando sola (${"%+.3f".format(r.tendencia ?: 0.0)} por día). Parte de la diferencia puede ser esa tendencia" +
                    (if (bloque) ", y como el plan quedó en bloque (primero una condición y luego la otra), pesa más." else "."), Modifier.padding(top = 2.dp))
                else if (bloque && e.diseno != "antes_despues") Ayuda("El sorteo quedó en bloque: primero una condición y luego la otra. Se lee como un antes y después, y cualquier cambio con el tiempo se confunde con el efecto.", Modifier.padding(top = 2.dp))
            }
            // Cuánto cumpliste, con lo que respondiste en «¿Lo hiciste hoy?» (en el Día).
            r.cumplimiento?.let { c ->
                fun pct(p: Double?) = p?.let { "%.0f %%".format(it * 100) } ?: "sin respuestas"
                Text("Lo hiciste: ${pct(c.pSi)} de los días que tocaba (${c.respSi} de ${c.diasSi} respondidos) y ${pct(c.pNo)} de los que no tocaba (${c.respNo} de ${c.diasNo}).",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
                if (c.sinRespuesta > 0) Ayuda("${c.sinRespuesta} días sin respuesta: no cuentan como «no».")
                r.cociente?.let { Ayuda("Si el efecto viene de hacerlo, hacerlo de verdad cambiaría tu medida en unos ${"%+.2f".format(it)} (la diferencia según el plan entre la diferencia de cumplimiento). " +
                    "Es una aproximación: supone que saber qué semana tocaba no cambia nada por sí solo.") }
            }
            if (Experimentos.enCurso(e, hoy)) TextButton(onClick = { Repo.guardarExperimento(e.copy(activo = false, fin = hoy)) }) { Text("Terminar experimento") }
        }
    }
}

// ============================================================================ guía de uso

/** La guía: todas las secciones, o solo la que pidió el «?» de una pantalla. */
@Composable
fun PantallaGuiaUso(nav: Nav, seccion: String = "") {
    val sola = io.github.ruddy2003.vestigium.data.GuiaUso.de(seccion)
    Hoja("Cómo usar la app", nav.atras, if (sola != null) null else "Cómo usar la app para que tus datos digan algo. Los ejemplos marcan las @personas y los #temas como el tema de hoy.") {
        val lista = if (sola != null) listOf(sola) else io.github.ruddy2003.vestigium.data.GuiaUso.SECCIONES
        lista.forEach { s ->
            Seccion(s.titulo, plegable = sola == null, plegadaPorDefecto = sola == null && s.id != "principios") {
                Tarjeta {
                    s.parrafos.forEachIndexed { i, p -> Text(p, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = if (i == 0) 0.dp else 8.dp)) }
                    if (s.ejemplos.isNotEmpty()) {
                        Etiqueta("Por ejemplo")
                        s.ejemplos.forEach { io.github.ruddy2003.vestigium.ui.TextoMarcado(it, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            }
        }
        if (sola != null) BotonSecundario("Ver toda la guía", Modifier.fillMaxWidth().padding(top = 12.dp)) { nav.reemplazar("guia_uso") }
        else BotonSecundario("Cómo leer tus datos: por qué se mide lo que se mide", Modifier.fillMaxWidth().padding(top = 12.dp)) { nav.ir("guia") }
    }
}

/** Corregir una toma: cantidad y cuándo (el día lógico sale de la hora), o quitarla con confirmación y Deshacer. */
@Composable
fun DialogoToma(t: io.github.ruddy2003.vestigium.data.TomaSuplemento, cerrar: () -> Unit) {
    val s = Suplementos.de(t.suplemento)
    var cantidad by remember(t.id) { mutableStateOf(cifra(t.cantidad)) }
    var cuando by remember(t.id) { mutableStateOf<java.time.LocalDateTime?>(io.github.ruddy2003.vestigium.ui.horaLocal(Fechas.millis(t.ts))) }
    var confirmar by remember(t.id) { mutableStateOf(false) }
    val v = cantidad.replace(',', '.').toDoubleOrNull()
    androidx.compose.material3.AlertDialog(
        onDismissRequest = cerrar,
        title = { Text("${s?.nombre ?: t.suplemento} · ${Fechas.corto(t.fecha)} ${Fechas.horaDe(t.ts)}") },
        text = {
            Column {
                OutlinedTextField(cantidad, { cantidad = it }, Modifier.fillMaxWidth(), singleLine = true, suffix = { Text(s?.unidad ?: "") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal))
                Etiqueta("Cuándo")
                io.github.ruddy2003.vestigium.ui.SelectorHora(cuando, ayer = true) { cuando = it }
            }
        },
        confirmButton = {
            TextButton(enabled = v != null && v > 0, onClick = {
                val ms = (cuando ?: java.time.LocalDateTime.now()).aMillis()
                val n = t.copy(cantidad = v ?: t.cantidad, ts = Fechas.isoDeMillis(ms), fecha = Fechas.diaLogicoDeMillis(ms))
                if (n != t) Repo.guardarToma(n)
                cerrar()
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = {
                if (!confirmar) { confirmar = true; return@TextButton }
                Repo.borrarToma(t.id); cerrar(); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Toma quitada") { Repo.guardarToma(t) }
            }) { Text(if (confirmar) "¿quitar de verdad?" else "Quitar", color = MaterialTheme.colorScheme.error) }
        },
    )
}
