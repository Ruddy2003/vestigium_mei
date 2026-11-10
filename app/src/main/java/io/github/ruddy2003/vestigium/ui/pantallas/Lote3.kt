@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.Separador
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import io.github.ruddy2003.vestigium.data.porNumero

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.drawscope.clipRect
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
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import io.github.ruddy2003.vestigium.ui.rejillaGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import io.github.ruddy2003.vestigium.ui.Dialogo

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
                // La cuenta del día (esquema 21): «Hoy 1 de 2» si va más de una vez al día; «Hoy ×3» si va una y llevas más.
                val n = s.tomas_dia.coerceAtLeast(1)
                val k = tomas.count { !it.estimado }
                val cuenta = when { n > 1 -> " $k de $n"; k > 1 -> " ×$k"; else -> "" }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(s.nombre, style = MaterialTheme.typography.titleMedium)
                        Ayuda(if (tomas.isEmpty()) "$ese$cuenta: todavía no" else "$ese$cuenta: " + tomas.joinToString(" + ") { "${cifra(it.cantidad)} ${s.unidad} a las ${Fechas.horaDe(it.ts)}" })
                    }
                    BotonTexto(onClick = { editando = s }) { Text("ajustar") }
                }
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonPrincipal("Tomé ${cifra(s.dosis)} ${s.unidad}" + if (cuando != null) " · ${Fechas.corto(dia)}" else "", Modifier.weight(1f)) { tomar(s, s.dosis) }
                    if (s.modelo == "carga") BotonSecundario("Carga ×4") { tomar(s, s.dosis * 4) }
                    if (tomas.isNotEmpty()) BotonSecundario("corregir") { toma = tomas.last() }
                }
                // Quita la última toma de ese día (la anotada por error o de más), con Deshacer.
                if (k > 0) BotonTexto(onClick = {
                    Suplementos.quitarUltima(s.id, dia)?.let { t -> io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Toma quitada") { Repo.guardarToma(t) } }
                }) { Text("quitar la última") }
                NivelSuplemento(s, hoy)
            }
        }
        Seccion("Recordatorio", "Un aviso a cada hora que marques si te falta alguno, con «Ya los tomé» (anota la dosis habitual) y «En una hora».") {
            Tarjeta {
                // Varias horas (esquema 21): se escribe la lista nueva y la hora única de antes queda vacía.
                val horas = Suplementos.horas(c)
                Interruptor("Recordarme cada día", horas.isNotEmpty()) { v ->
                    Repo.cambiarConfig { it.copy(suplementos_horas = if (v) listOf(9) else emptyList(), suplementos_hora = null) }
                }
                if (horas.isNotEmpty()) {
                    Etiqueta("Horas")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        (listOf(7, 8, 9, 10, 12, 14, 18, 21, 22) + horas).distinct().sorted().forEach { x ->
                            Ficha("%02d:00".format(x), x in horas) {
                                Repo.cambiarConfig { cc ->
                                    val ya = Suplementos.horas(cc)
                                    cc.copy(suplementos_horas = if (x in ya) ya.filter { it != x } else (ya + x).sorted(), suplementos_hora = null)
                                }
                            }
                        }
                    }
                    Ayuda("Si tomas algo dos veces al día, marca dos horas y pon “2 tomas al día” en el suplemento.", Modifier.padding(top = 6.dp))
                }
            }
        }
        Seccion("Agregar", "Del catálogo o uno tuyo; después puedes cambiar dosis, unidad y parámetros. Tocar uno activo lo deja de usar.") {
            Tarjeta {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Los tuyos (esquema 17) junto a los del catálogo, para volver a activarlos.
                    (Suplementos.CATALOGO + c.suplementos.filter { s -> Suplementos.CATALOGO.none { it.id == s.id } }).forEach { s ->
                        val ya = c.suplementos.firstOrNull { it.id == s.id }
                        Ficha(s.nombre, ya?.activo == true) {
                            Repo.cambiarConfig { cc ->
                                val previo = cc.suplementos.firstOrNull { it.id == s.id }
                                cc.copy(suplementos = if (previo == null) cc.suplementos + s else cc.suplementos.map { if (it.id == s.id) it.copy(activo = !it.activo) else it })
                            }
                        }
                    }
                    // Uno que no está en el catálogo (hierro, B12, zinc, proteína…): se crea con «solo porciones» y se ajusta enseguida.
                    BotonSecundario("＋ otro suplemento") { editando = Suplemento("s_" + Repo.nuevoId().take(8), "", "mg", 1.0, "exposicion", activo = false) }
                }
                if (c.suplementos.any { it.id == "probioticos" && it.activo }) {
                    Ayuda("Probióticos: también se cuentan como porciones estimadas los alimentos de tu bitácora de MacroFactor que contienen: " + c.probioticos_palabras.joinToString(", ") + ".",
                        Modifier.padding(top = 10.dp))
                    var palabras by remember { mutableStateOf(c.probioticos_palabras.joinToString(", ")) }
                    CampoTexto(palabras, { palabras = it }, Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Palabras (separadas por coma)") })
                    BotonTexto(onClick = { Repo.cambiarConfig { it.copy(probioticos_palabras = palabras.split(',').map { p -> p.trim() }.filter { p -> p.isNotEmpty() }) } }) { Text("guardar palabras") }
                }
            }
        }
        val pasadas = Repo.tomas.sortedBy { it.ts }.takeLast(14).reversed()
        if (pasadas.isNotEmpty()) Seccion("Últimas tomas", "Toca una para corregir la cantidad o la hora, o para quitarla.") {
            Tarjeta {
                pasadas.forEachIndexed { i, t ->
                    if (i > 0) Separador()
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
            "vida_media" -> dias.map { f -> if (Repo.tomas.any { it.suplemento == s.id && it.fecha <= f }) Suplementos.cantidadEn(s, Fechas.finDiaLogico(f) - 1) else null }
            else -> { val d = Suplementos.dosisPorDia(s.id); val e = if (s.id == "probioticos") Suplementos.probioticosEstimados() else emptyMap(); dias.map { f -> ((d[f] ?: 0.0) + (e[f] ?: 0.0)).takeIf { it > 0 } } }
        }
    }
    if (valores.all { it == null }) { Ayuda("Cuando registres tomas, aquí verás tu nivel estimado.", Modifier.padding(top = 8.dp)); return }
    val ultimo = valores.lastOrNull { it != null }
    Ayuda(when (s.modelo) {
        "carga" -> "Reserva estimada hoy: ${ultimo?.roundToInt() ?: 0} %. Se llena en unos ${porNumero(s.carga_dias, "1 día", "${s.carga_dias} días")} de dosis normal y baja en unos ${s.lavado_dias} sin tomar."
        "vida_media" -> "En el cuerpo al final del día: ${cifra(ultimo ?: 0.0)} ${s.unidad} (vida media de ${cifra(s.vida_media_h)} h)."
        else -> "Porciones por día en los últimos 60 días."
    }, Modifier.padding(top = 8.dp))
    val max = if (s.modelo == "carga") 100.0 else (valores.filterNotNull().maxOrNull() ?: 1.0).coerceAtLeast(1e-6)
    val eg = estiloGrafica()
    Canvas(Modifier.fillMaxWidth().height(70.dp).padding(top = 6.dp)) {
        val w = size.width; val h = size.height; val n = valores.size
        // Las guías del 50 % y el 100 % de la reserva.
        if (s.modelo == "carga") rejillaGrafica(eg, listOf(0.5f, 1f).map { k -> h * (1 - k) }, cs.outlineVariant)
        if (s.modelo == "exposicion") valores.forEachIndexed { i, v ->
            if (v != null) { val bh = (v / max * h).toFloat(); barraGrafica(eg, Offset(i * w / n + 1f, h - bh), androidx.compose.ui.geometry.Size(w / n - 2f, bh), cs.primary) }
        } else {
            val p = Path(); var abierto = false
            valores.forEachIndexed { i, v ->
                if (v == null) { abierto = false; return@forEachIndexed }
                val x = i * w / (n - 1); val y = h - (v / max * h).toFloat()
                if (!abierto) { p.moveTo(x, y); abierto = true } else p.lineTo(x, y)
            }
            lineaGrafica(eg, p, cs.primary, 2.5f * density)
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
    var tomasDia by remember { mutableStateOf(s0.tomas_dia.coerceIn(1, 6)) }
    // Uno nuevo (todavía no está en tus suplementos): se agrega activo al guardar.
    val nuevo = Repo.config.suplementos.none { it.id == s0.id }
    // Los números se revisan antes de guardar: si uno no se entiende, se avisa en lugar de volver en silencio al anterior.
    val dosisOk = dosis.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }
    val vidaOk = if (modelo == "vida_media") vida.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 } else s0.vida_media_h
    val cargaOk = if (modelo == "carga") carga.toIntOrNull()?.takeIf { it > 0 } else s0.carga_dias
    val lavadoOk = if (modelo == "carga") lavado.toIntOrNull()?.takeIf { it > 0 } else s0.lavado_dias
    val valido = nombre.isNotBlank() && dosisOk != null && vidaOk != null && cargaOk != null && lavadoOk != null
    Dialogo(onDismissRequest = cerrar, title = { Text(if (nuevo) "Nuevo suplemento" else s0.nombre) },
        confirmButton = {
            BotonTexto(enabled = valido, onClick = {
                val n = s0.copy(nombre = nombre.trim(), unidad = unidad.trim().ifBlank { s0.unidad }, dosis = dosisOk ?: s0.dosis,
                    modelo = modelo, vida_media_h = vidaOk ?: s0.vida_media_h, carga_dias = cargaOk ?: s0.carga_dias, lavado_dias = lavadoOk ?: s0.lavado_dias,
                    activo = if (nuevo) true else s0.activo, tomas_dia = tomasDia)
                Repo.cambiarConfig { c -> c.copy(suplementos = if (nuevo) c.suplementos + n else c.suplementos.map { if (it.id == s0.id) n else it }) }
                cerrar()
            }) { Text("Guardar") }
        },
        dismissButton = {
            Row {
                if (!nuevo) BotonTexto(onClick = { Repo.cambiarConfig { c -> c.copy(suplementos = c.suplementos.map { if (it.id == s0.id) it.copy(activo = false) else it }) }; cerrar() }) { Text("dejar de usar") }
                BotonTexto(onClick = cerrar) { Text("cancelar") }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                CampoTexto(nombre, { nombre = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Nombre") }, isError = nombre.isBlank())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    CampoTexto(dosis, { dosis = it }, Modifier.weight(1f), singleLine = true, label = { Text("Dosis habitual") }, isError = dosisOk == null, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    CampoTexto(unidad, { unidad = it }, Modifier.weight(1f), singleLine = true, label = { Text("Unidad") })
                }
                // Cuántas veces al día (esquema 21): mientras lleves menos, el recordatorio, el mosaico y «Ya los tomé» lo cuentan como faltante.
                Etiqueta("Tomas al día")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..6).forEach { x -> Ficha(x.toString(), tomasDia == x) { tomasDia = x } }
                }
                if (tomasDia > 1) Ayuda("Pon una hora de recordatorio por cada toma (en «Recordatorio», abajo de tus suplementos).", Modifier.padding(top = 4.dp))
                Etiqueta("Modelo")
                FilaFichas(Suplementos.MODELOS, modelo) { modelo = it ?: modelo }
                when (modelo) {
                    "carga" -> {
                        CampoTexto(carga, { carga = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Días para llenar la reserva") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        CampoTexto(lavado, { lavado = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Días para vaciarla sin tomar") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        Ayuda("Creatina: con 3–5 g diarios la reserva del músculo se llena en unas 4 semanas (en una con dosis de carga) y vuelve a lo normal 4 a 6 semanas después de dejarla.", Modifier.padding(top = 6.dp))
                    }
                    "vida_media" -> CampoTexto(vida, { vida = it }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Vida media (horas)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
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
                val eg = estiloGrafica()
                Canvas(Modifier.fillMaxWidth().height(120.dp)) {
                    val bw = size.width / dias.size
                    dias.forEachIndexed { i, f ->
                        // Una sola barra con la forma del tema, del alto del total, partida en franjas por categoría (cada
                        // franja se recorta de la barra entera, así la punta del tema queda solo arriba).
                        val alturas = cats.map { cat -> ((res[f]?.get("laptop_${cat}_min") ?: 0.0) / max * size.height).toFloat() }
                        val total = alturas.sum()
                        var y = size.height
                        alturas.forEachIndexed { k, h ->
                            if (h > 0f) clipRect(0f, y - h, size.width, y) {
                                barraGrafica(eg, Offset(i * bw + bw * 0.15f, size.height - total), androidx.compose.ui.geometry.Size(bw * 0.7f, total), colores[k])
                            }
                            y -= h
                        }
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
                    if (i > 0) Separador()
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

/**
 * En la ficha de una persona: «¿A quién conoce?». Cada toque cambia el tipo de vínculo. Salen todas las personas
 * ([Personas.todas]): antes la lista se cortaba a las primeras 24 (las de Tu gente en el orden en que se guardaron y luego las
 * 20 más mencionadas), y con más gente que eso había pares que no se podían unir desde ninguna de las dos fichas.
 */
@Composable
fun AQuienConoce(nombre: String) {
    val c = Repo.config
    val menc = remember(Repo.dias, Repo.momentos, Repo.tramos, Repo.cartas, c.equivalencias) { Personas.menciones() }
    val yo = io.github.ruddy2003.vestigium.data.Texto.canon(nombre.trim().lowercase().removePrefix("@"), c.equivalencias)
    // Primero las que ya tienen un vínculo dicho con esta persona; luego, de la más mencionada a la menos. El orden se fija al
    // abrir la ficha: si dependiera de lo que vas tocando, las fichas saltarían de lugar.
    val otras = remember(nombre, menc, c.personas, c.perfil, c.equivalencias) {
        val dichas = RedPersonas.dichos(c).mapNotNull { v -> if (v.a == yo) v.b else if (v.b == yo) v.a else null }.toSet()
        Personas.todas(menc, c).filter { it != yo }.sortedByDescending { it in dichas }
    }
    if (otras.isEmpty()) return
    var buscar by remember(nombre) { mutableStateOf("") }
    Etiqueta("¿A quién conoce?")
    Ayuda("Toca para cambiar: se conocen → familia entre sí → pareja → colegas → no se conocen → sin decir.")
    if (otras.size > 12) CampoTexto(value = buscar, onValueChange = { buscar = it }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), singleLine = true,
        placeholder = { Text("Buscar entre ${otras.size} personas") })
    val q = buscar.trim().lowercase().removePrefix("@")
    val visibles = if (q.isEmpty()) otras else otras.filter { q in it }
    if (visibles.isEmpty()) Ayuda("Nadie con ese nombre. Las personas salen aquí cuando las mencionas con @ o las agregas a Tu gente.", Modifier.padding(top = 4.dp))
    FlowRow(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        visibles.forEach { o ->
            val t = RedPersonas.tipo(nombre, o)
            Ficha("@$o" + (t?.let { " · " + RedPersonas.nombreTipo(it).lowercase() } ?: ""), t != null && t != "no_se_conocen") { RedPersonas.poner(nombre, o, RedPersonas.siguiente(t)) }
        }
    }
}

// La pantalla de la Red pasó a Social, vista «red» (9 nov 2026): ver Social.kt.

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
    var sentido by remember { mutableStateOf<Int?>(null) }
    var umbral by remember { mutableStateOf("") }
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
                CampoTexto(nombre, { nombre = it }, Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Nombre") }, singleLine = true)
                CampoTexto(pregunta, { pregunta = it }, Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Pregunta") })
                CampoTexto(accion, { accion = it }, Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Qué vas a cambiar (la acción)") })
                CampoTexto(hipotesis, { hipotesis = it }, Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Qué crees que pasará (hipótesis)") })
                Etiqueta("Qué vas a medir")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (listOf("valencia", "activacion", "sueno_calidad", "sueno_horas", "muestreo_valencia", "pantalla_min", "rt_media", "volumen_kg", "fuerza_ref_kg", "enfoque_min") +
                        listOf(resultado)).distinct().forEach { id -> vars.firstOrNull { it.id == id }?.let { v -> Ficha(v.nombre, resultado == id) { resultado = id } } }
                }
                // El criterio de decisión también se sella (H-7-10): decidirlo después, viendo los datos, es acomodarlo.
                Etiqueta("Qué contaría como efecto (se sella)")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("debería subir", sentido == 1) { sentido = if (sentido == 1) null else 1 }
                    Ficha("debería bajar", sentido == -1) { sentido = if (sentido == -1) null else -1 }
                }
                if (sentido != null) CampoTexto(umbral, { umbral = it.replace(',', '.') }, Modifier.fillMaxWidth().padding(top = 6.dp),
                    label = { Text("Diferencia mínima que te importaría (opcional, en las unidades de la medida)") }, singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal))
                Ayuda(if (sentido == null) "Opcional, pero recomendable: di antes de empezar hacia dónde esperas el cambio. Al final la app te dirá si se cumplió."
                    else "Al terminar, contará como efecto si la diferencia va hacia " + (if (sentido == 1) "arriba" else "abajo") +
                        (umbral.toDoubleOrNull()?.let { ", mide al menos ${kotlin.math.abs(it)}" } ?: "") + " y su intervalo de 95 % no toca el cero.", Modifier.padding(top = 4.dp))
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
                if (minimo < Experimentos.MIN_DIAS) Aviso("Con ${porNumero(lavado, "1 día", "$lavado días")} de lavado tras cada cambio, en ${porNumero(semanas, "1 semana", "$semanas semanas")} podrían quedar solo ${porNumero(minimo, "1 día", "$minimo días")} que cuentan en una condición. " +
                    "Hacen falta al menos ${Experimentos.MIN_DIAS}: alarga el experimento o reduce el lavado.")
                BotonPrincipal("Sellar y empezar el lunes", Modifier.fillMaxWidth().padding(top = 12.dp),
                    habilitado = nombre.isNotBlank() && accion.isNotBlank() && hipotesis.isNotBlank() && minimo >= Experimentos.MIN_DIAS) {
                    val plan = Experimentos.plan(diseno, semanas)
                    val lunes = Fechas.sumar(Fechas.lunesDe(Fechas.diaLogico()), 7)
                    Repo.guardarExperimento(Experimentos.sellar(Experimento(Repo.nuevoId(), nombre.trim(), accion.trim(), resultado, lunes, semanas, plan, pregunta = pregunta.trim(),
                        hipotesis = hipotesis.trim(), sellada = Fechas.ahoraIso(), diseno = diseno, lavado_dias = lavado, plantilla = plantilla,
                        sentido = sentido, umbral = if (sentido != null) umbral.toDoubleOrNull()?.let { kotlin.math.abs(it) } else null)))
                    nombre = ""; accion = ""; pregunta = ""; hipotesis = ""; plantilla = null; sentido = null; umbral = ""
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
            e.sentido?.let { s -> Ayuda("Criterio sellado: un cambio hacia " + (if (s > 0) "arriba" else "abajo") + (e.umbral?.takeIf { it > 0 }?.let { " de al menos ${"%.2f".format(java.util.Locale.US, it).removeSuffix("0").removeSuffix(".0")}" } ?: "") +
                ", con un intervalo de 95 % que no toque el cero.") }
            when (Experimentos.intacto(e)) {
                true -> Ayuda("Huella del sello: ${e.huella!!.take(8)} (coincide: nada de lo sellado cambió).")
                false -> Aviso("Lo sellado cambió después del sello (la huella ${e.huella!!.take(8)} ya no coincide): la pregunta, la hipótesis, el criterio o el plan no son los del principio.")
                null -> {}
            }
            val hoy = Fechas.diaLogico()
            val sem = (Fechas.diasEntre(e.inicio, hoy) / 7).toInt()
            Ayuda(when {
                e.fin != null -> "Terminado el ${Fechas.corto(e.fin)}" + (if (e.fin < Experimentos.ultimoDia(e.copy(fin = null))) ", antes de acabar el plan: los días siguientes no cuentan." else ".")
                Fechas.diasEntre(e.inicio, hoy) < 0 -> "Empieza el ${Fechas.corto(e.inicio)}."
                sem >= e.semanas -> "Terminado."
                else -> "Semana ${sem + 1} de ${e.semanas}: " + (if (e.plan[sem]) "SÍ toca." else "NO toca.") + if (Experimentos.enLavado(e, hoy)) " Hoy es día de lavado (no cuenta)." else ""
            }, Modifier.padding(top = 4.dp))
            Ayuda((if (e.diseno == "antes_despues") "Antes y después · " else "Semanas sorteadas · ") + "plan: " + e.plan.joinToString(" ") { if (it) "SÍ" else "no" } +
                if (e.lavado_dias > 0) " · ${porNumero(e.lavado_dias, "1 día", "${e.lavado_dias} días")} de lavado tras cada cambio" else "")
            val r = remember(e, panel) { Experimentos.resultado(e, panel) }
            if (r.dif == null) Ayuda(when {
                // Tres casos distintos (P-18): el plan nunca alcanzará, ya terminó, o falta esperar.
                r.posibleSi < 3 || r.posibleNo < 3 -> "Este plan nunca dará resultado: con el lavado solo cuentan ${porNumero(r.posibleSi, "1 día", "${r.posibleSi} días")} con la acción y ${r.posibleNo} sin ella (hacen falta 3 de cada una)."
                !r.provisional -> "El experimento terminó sin suficientes días medidos: ${r.con.size} con la acción y ${r.sin.size} sin ella (hacían falta 3 de cada una)."
                else -> "Aún no hay suficientes días medidos en ambas condiciones (${r.con.size} con, ${r.sin.size} sin; hacen falta 3 de cada una)."
            }, Modifier.padding(top = 6.dp))
            else {
                val (d, lo, hi) = r.dif
                if (r.provisional) Aviso("Provisional: el plan sigue en curso. Mirar el resultado a medio camino y parar en la primera «señal» engaña; espera a que termine.")
                Text("Semanas con la acción según el plan: ${"%.2f".format(r.con.average())} · sin ella: ${"%.2f".format(r.sin.average())}. Diferencia ${"%+.2f".format(d)} (intervalo de 95 %: ${"%.2f".format(lo)} a ${"%.2f".format(hi)}).",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
                if (!r.provisional) Ayuda(if (lo > 0 || hi < 0) "El intervalo no toca el cero: es una señal para ti." else "El intervalo incluye el cero: todavía no se distingue del azar.")
                Experimentos.criterio(e, r)?.let { ok -> Text(if (ok) "Según el criterio que sellaste: se cumple." else "Según el criterio que sellaste: no se cumple.",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 2.dp)) }
                r.d?.let { Ayuda("Tamaño del efecto (g de Hedges): ${"%.2f".format(it)}, ${Experimentos.tamano(it)} para cómo varían tus días; no se compara con los estudios entre personas.") }
                Ayuda("Comparación según el plan: semanas que tocaba la acción contra semanas que no, la hayas hecho o no. Es la comparación justa; si no siempre cumpliste, el efecto real puede ser mayor. " +
                    "El intervalo cuenta que los días seguidos se parecen.", Modifier.padding(top = 2.dp))
                // El aviso fuerte depende del plan real: un sorteo que salió en bloque también es un antes y después (P-18).
                val bloque = Experimentos.enBloque(e)
                if (r.tendenciaClara) Ayuda("Ojo: dentro de cada condición, tu medida venía cambiando sola (${"%+.3f".format(r.tendencia ?: 0.0)} por día). Parte de la diferencia puede ser esa tendencia" +
                    (if (bloque) ", y como el plan quedó en bloque (primero una condición y luego la otra), pesa más." else "."), Modifier.padding(top = 2.dp))
                else if (bloque && e.diseno != "antes_despues") Ayuda("El sorteo quedó en bloque: primero una condición y luego la otra. Se lee como un antes y después, y cualquier cambio con el tiempo se confunde con el efecto.", Modifier.padding(top = 2.dp))
            }
            // Cuánto cumpliste, con lo que respondiste en «¿Lo hiciste hoy?» (en el Día); en «Creatina», también con tus tomas.
            r.cumplimiento?.let { c ->
                fun pct(p: Double?) = p?.let { "%.0f %%".format(it * 100) } ?: "sin respuestas"
                Text("Lo hiciste: ${pct(c.pSi)} de los días que tocaba (${c.respSi} de ${c.diasSi} respondidos) y ${pct(c.pNo)} de los que no tocaba (${c.respNo} de ${c.diasNo}).",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
                if (e.plantilla == "creatina") Ayuda("Los días sin respuesta se completan con tu registro de suplementos (si anotaste creatina ese día).")
                if (c.sinRespuesta > 0) Ayuda("${porNumero(c.sinRespuesta, "1 día", "${c.sinRespuesta} días")} sin respuesta: no cuentan como «no».")
                r.cociente?.let { Ayuda("Si el efecto viene de hacerlo, hacerlo de verdad cambiaría tu medida en unos ${"%+.2f".format(it)} (la diferencia según el plan entre la diferencia de cumplimiento). " +
                    "Es una aproximación: supone que saber qué semana tocaba no cambia nada por sí solo.") }
            }
            if (Experimentos.enCurso(e, hoy)) BotonTexto(onClick = { Repo.guardarExperimento(e.copy(activo = false, fin = hoy)) }) { Text("Terminar experimento") }
        }
    }
}

// ============================================================================ guía de uso

/** La guía: todas las secciones, o solo la que pidió el «?» de una pantalla. */
@Composable
fun PantallaGuiaUso(nav: Nav, seccion: String = "") {
    val sola = io.github.ruddy2003.vestigium.data.GuiaUso.de(seccion)
    Hoja("Guía", nav.atras, if (sola != null) null else "Cómo usar la app para que tus datos digan algo. Los ejemplos marcan las @personas y los #temas como el tema de hoy.") {
        if (sola == null) PestanasGuia("guia_uso", nav)
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
    Dialogo(
        onDismissRequest = cerrar,
        title = { Text("${s?.nombre ?: t.suplemento} · ${Fechas.corto(t.fecha)} ${Fechas.horaDe(t.ts)}") },
        text = {
            Column {
                CampoTexto(cantidad, { cantidad = it }, Modifier.fillMaxWidth(), singleLine = true, suffix = { Text(s?.unidad ?: "") }, label = { Text("Cantidad") },
                    isError = v == null || v <= 0, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal))
                Etiqueta("Cuándo")
                io.github.ruddy2003.vestigium.ui.SelectorHora(cuando, ayer = true) { cuando = it }
                BotonTexto(onClick = {
                    if (!confirmar) { confirmar = true; return@BotonTexto }
                    Repo.borrarToma(t.id); cerrar(); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Toma borrada") { Repo.guardarToma(t) }
                }, modifier = Modifier.padding(top = 6.dp)) { Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            BotonTexto(enabled = v != null && v > 0, onClick = {
                val ms = (cuando ?: java.time.LocalDateTime.now()).aMillis()
                val n = t.copy(cantidad = v ?: t.cantidad, ts = Fechas.isoDeMillis(ms), fecha = Fechas.diaLogicoDeMillis(ms))
                if (n != t) Repo.guardarToma(n)
                cerrar()
            }) { Text("Guardar") }
        },
        dismissButton = { BotonTexto(onClick = cerrar) { Text("cancelar") } },
    )
}
