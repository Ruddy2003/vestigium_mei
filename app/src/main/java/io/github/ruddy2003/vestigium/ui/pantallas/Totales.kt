@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.Separador
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import io.github.ruddy2003.vestigium.data.porNumero

import io.github.ruddy2003.vestigium.data.privado
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Totales
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import io.github.ruddy2003.vestigium.data.alFuturo
import io.github.ruddy2003.vestigium.data.legible
import io.github.ruddy2003.vestigium.ui.Dialogo
import io.github.ruddy2003.vestigium.ui.elegirFecha

@Composable
fun PantallaTotales(nav: Nav, buscar: Boolean = false) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    var periodo by remember { mutableStateOf("todo") }
    var propio by remember { mutableStateOf<String?>(null) }
    val desde = when (periodo) {
        "anio" -> hoy.take(4) + "-01-01"
        "mes" -> hoy.take(7) + "-01"
        "30" -> Fechas.sumar(hoy, -29)
        "desde" -> propio ?: hoy
        else -> "0000-01-01"
    }
    val version = Repo.dias.size + Repo.momentos.size + Repo.nutricion.size + Repo.salud.size + Repo.gastos.size
    val res by produceState<Totales.Resultado?>(null, desde, hoy, version) {
        value = null
        value = withContext(Dispatchers.Default) { runCatching { Totales.calcular(desde, hoy) }.getOrNull() }
    }
    var busqueda by remember { mutableStateOf(Seleccion.buscar.orEmpty().also { Seleccion.buscar = null }) }
    var indice by remember { mutableStateOf<String?>(null) }
    var todos by remember { mutableStateOf(false) }
    var abierta by remember { mutableStateOf<Pair<Totales.Indice, Totales.Entrada>?>(null) }

    val foco = remember { androidx.compose.ui.focus.FocusRequester() }
    if (buscar) androidx.compose.runtime.LaunchedEffect(Unit) { kotlinx.coroutines.delay(150); runCatching { foco.requestFocus() } }
    Hoja(if (buscar) "Buscar" else "Totales", nav.atras, "Todo lo acumulado: cuánto, cuántas veces y desde cuándo. Cada fuente empezó en una fecha distinta, así que cada cifra dice desde cuándo cuenta.", latin = if (buscar) "QVAERERE" else "SVMMA OMNIVM") {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todo", periodo == "todo") { periodo = "todo" }
            Ficha("este año", periodo == "anio") { periodo = "anio" }
            Ficha("este mes", periodo == "mes") { periodo = "mes" }
            Ficha("30 días", periodo == "30") { periodo = "30" }
            Ficha(if (periodo == "desde" && propio != null) "desde ${Totales.fecha(propio!!)}" else "desde…", periodo == "desde") {
                elegirFecha(ctx, propio ?: Fechas.sumar(hoy, -90)) { propio = it; periodo = "desde" }
            }
        }
        Ayuda(if (periodo == "todo") "Desde el primer dato que tengas hasta hoy." else "Del ${Totales.fecha(desde)} a hoy.", Modifier.padding(top = 4.dp))
        CampoTexto(
            busqueda, { busqueda = it }, Modifier.fillMaxWidth().padding(top = 8.dp).focusRequester(foco), singleLine = true,
            placeholder = { Text("Buscar en todo: diario, plátano, @persona, artista, lugar…") },
            trailingIcon = if (busqueda.isNotEmpty()) ({ BotonTexto(onClick = { busqueda = "" }) { Text("×") } }) else null,
        )
        if (busqueda.isNotBlank()) EnPantallas(busqueda) { ruta -> nav.ir(ruta) }
        val r = res
        when {
            r == null -> Ayuda("Sumando…", Modifier.padding(top = 12.dp))
            busqueda.isNotBlank() -> {
                EnDiario(busqueda, desde, hoy) { f -> Seleccion.abrir(nav, f) }
                EnCartas(busqueda, desde, hoy) { id -> Seleccion.carta = id; nav.ir("carta") }
                EnIdeas(busqueda, desde, hoy) { i -> EdicionIdea.abrir(nav, i) }
                Resultados(r, busqueda) { i, e -> abierta = i to e }
            }
            r.grupos.isEmpty() && r.indices.isEmpty() -> Vacio("Nada registrado en este periodo.")
            else -> {
                r.grupos.forEach { g ->
                    Seccion(g.titulo, latin = g.latin) {
                        Tarjeta { g.cifras.forEachIndexed { k, c -> if (k > 0) Separador(); FilaCifra(c) } }
                    }
                }
                if (r.indices.isNotEmpty()) Seccion("Índices", "Elige una lista y toca cualquier renglón para ver su historia mes a mes.", latin = "INDICES") {
                    val sel = r.indices.firstOrNull { it.id == indice } ?: r.indices.first()
                    FlowRow(Modifier.padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        r.indices.forEach { i -> Ficha("${i.titulo} · ${i.entradas.size}", i.id == sel.id) { indice = i.id; todos = false } }
                    }
                    Tarjeta {
                        val l = if (todos) sel.entradas.take(150) else sel.entradas.take(12)
                        l.forEachIndexed { k, e -> if (k > 0) Separador(); FilaEntrada(sel, e, k + 1) { abierta = sel to e } }
                        if (sel.entradas.size > 12) BotonTexto(onClick = { todos = !todos }) {
                            Text(if (todos) "ver menos" else "ver ${minOf(150, sel.entradas.size)} de ${sel.entradas.size}", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
    abierta?.let { (i, e) -> Detalle(i, e) { abierta = null } }
}

@Composable
private fun FilaCifra(c: Totales.Cifra) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(c.nombre, style = MaterialTheme.typography.bodyMedium)
            val sub = listOfNotNull(c.desde?.let { "desde ${Totales.fecha(it)}" }, c.nota).joinToString(" · ")
            if (sub.isNotEmpty()) Ayuda(sub)
        }
        Text(c.valor, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.End, modifier = Modifier.padding(start = 10.dp))
    }
}

@Composable
private fun FilaEntrada(i: Totales.Indice, e: Totales.Entrada, puesto: Int? = null, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp)) {
        Text((puesto?.let { "$it. " } ?: "") + e.nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        Ayuda(i.linea(e) + " · desde ${Totales.fecha(e.primera)}")
    }
}

@Composable
private fun Resultados(r: Totales.Resultado, q: String, abrir: (Totales.Indice, Totales.Entrada) -> Unit) {
    val cifras = if (q.trim().let { it.startsWith("@") || it.startsWith("#") }) emptyList() else remember(r, q) { r.grupos.flatMap { g -> g.cifras.filter { Totales.coincide(it.nombre, q, false) || Totales.coincide(g.titulo, q, false) } } }
    // «@ana» busca solo personas y «#viaje» solo temas.
    val solo = when { q.trim().startsWith("@") -> "personas"; q.trim().startsWith("#") -> "temas"; else -> null }
    val hallados = remember(r, q) { r.indices.filter { solo == null || it.id == solo }.mapNotNull { i -> i.entradas.filter { Totales.coincide(it.nombre, q, i.buscaSinonimos) }.takeIf { it.isNotEmpty() }?.let { i to it } } }
    if (cifras.isEmpty() && hallados.isEmpty()) {
        Vacio("Nada con «$q» en este periodo. Los alimentos de MacroFactor vienen en inglés: si no aparece, prueba con el nombre en inglés.")
        return
    }
    if (cifras.isNotEmpty()) {
        Etiqueta("cifras")
        Tarjeta { cifras.forEachIndexed { k, c -> if (k > 0) Separador(); FilaCifra(c) } }
    }
    hallados.forEach { (i, l) ->
        Etiqueta(i.titulo.lowercase())
        Tarjeta {
            l.take(10).forEachIndexed { k, e -> if (k > 0) Separador(); FilaEntrada(i, e) { abrir(i, e) } }
            if (l.size > 10) Ayuda("y ${l.size - 10} más; escribe algo más específico.")
            // Si hay varias coincidencias (p. ej. tipos de plátano), también la suma de todas.
            if (l.size > 1 && l.map { it.unidad }.distinct().size == 1) {
                val s = Totales.Entrada("Todo lo que coincide con «$q»", l.first().unidad)
                l.forEach { e -> e.porMes.forEach { (m, v) -> s.porMes.merge(m, v, Double::plus) }; s.total += e.total; s.veces += e.veces; s.dias.addAll(e.dias); s.extra += e.extra
                    if (s.primera.isEmpty() || e.primera < s.primera) s.primera = e.primera; if (e.ultima > s.ultima) s.ultima = e.ultima }
                Separador()
                FilaEntrada(i, s) { abrir(i, s) }
            }
        }
    }
}

@Composable
private fun Detalle(i: Totales.Indice, e: Totales.Entrada, cerrar: () -> Unit) {
    Dialogo(
        onDismissRequest = cerrar,
        title = { Text(e.nombre, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Etiqueta(i.titulo.lowercase())
                Text(Totales.cantidad(e), style = MaterialTheme.typography.headlineSmall)
                Ayuda(i.linea(e))
                Clave("primera", Totales.fecha(e.primera), ancho = 9)
                Clave("última", "${Totales.fecha(e.ultima)} · ${Fechas.relativo(e.ultima)}", ancho = 9)
                Clave("días", "${e.dias.size}", ancho = 9)
                if (e.veces > 1) Clave("por vez", Totales.formato(e.total / e.veces, e.unidad), ancho = 9)
                if (e.dias.size >= 2) {
                    val lapso = java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(e.primera), Fechas.parse(e.ultima)) + 1
                    Clave("ritmo", "${e.dias.size} de ${porNumero(lapso, "1 día", "$lapso días")} (${Totales.n(100.0 * e.dias.size / lapso)}%)", ancho = 9)
                }
                if (e.porMes.size >= 2) Meses(e.porMes, e.unidad)
            }
        },
        confirmButton = { BotonTexto(onClick = cerrar) { Text("cerrar", style = MaterialTheme.typography.labelLarge) } },
    )
}

/** Barras por mes (hasta los últimos 24), con los meses sin registro en cero. */
@Composable
private fun Meses(porMes: Map<String, Double>, unidad: String) {
    val meses = remember(porMes) {
        val ini = LocalDate.parse(porMes.keys.min() + "-01"); val fin = LocalDate.parse(porMes.keys.max() + "-01")
        val l = ArrayList<Pair<String, Double>>()
        var d = ini
        while (!d.isAfter(fin)) { val k = d.toString().take(7); l.add(k to (porMes[k] ?: 0.0)); d = d.plusMonths(1) }
        l.takeLast(24)
    }
    val barra = MaterialTheme.colorScheme.primary
    val texto = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val max = meses.maxOf { it.second }.coerceAtLeast(1e-9)
    val pico = meses.maxBy { it.second }
    val linea = MaterialTheme.colorScheme.outline
    val eg = estiloGrafica()
    Etiqueta("por mes")
    Canvas(Modifier.fillMaxWidth().aspectRatio(2.6f).padding(top = 4.dp)) {
        val abajo = 30f
        val w = size.width / meses.size
        val pincel = Paint().apply { color = texto; textSize = 22f; isAntiAlias = true; textAlign = Paint.Align.CENTER }
        meses.forEachIndexed { k, (m, v) ->
            val h = (size.height - abajo) * (v / max).toFloat()
            if (v > 0) barraGrafica(eg, Offset(k * w + w * 0.15f, size.height - abajo - h), Size(w * 0.7f, h), barra)
            if (meses.size <= 12 || k % 2 == 0) drawContext.canvas.nativeCanvas.drawText("EFMAMJJASOND"[m.takeLast(2).toInt() - 1].toString(), k * w + w / 2, size.height - 6f, pincel)
        }
        ejeGrafica(eg, Offset(0f, size.height - abajo), Offset(size.width, size.height - abajo), linea)
    }
    Ayuda("${Fechas.mesTitulo(meses.first().first)} a ${Fechas.mesTitulo(meses.last().first)} · más alto: ${Totales.formato(pico.second, unidad)} en ${Fechas.mesTitulo(pico.first).lowercase()}")
}

/** Días cuyo diario, sueño, mejor/peor momento o momentos mencionan el texto. Toca uno para abrirlo. */
@Composable
private fun EnDiario(q: String, desde: String, hasta: String, abrir: (String) -> Unit) {
    val nq = Totales.normal(q.trim())
    if (nq.length < 2) return
    val hallados = remember(nq, desde, hasta, Repo.dias.size, Repo.momentos.size) {
        val porDia = LinkedHashMap<String, String>()
        Repo.dias.values.filter { it.fecha in desde..hasta }.sortedByDescending { it.fecha }.forEach { d ->
            listOf(d.nota, d.sueno_texto, d.mejor_momento, d.peor_momento).firstOrNull { Totales.normal(it).contains(nq) }?.let { porDia[d.fecha] = it }
        }
        Repo.momentos.filter { it.fecha in desde..hasta && !it.privado && Totales.normal(it.texto).contains(nq) }.forEach { m -> porDia.putIfAbsent(m.fecha, m.texto) }
        porDia.entries.sortedByDescending { it.key }.map { it.key to it.value }
    }
    if (hallados.isEmpty()) return
    Etiqueta("en tu diario · ${hallados.size} ${if (hallados.size == 1) "día" else "días"}")
    Tarjeta {
        hallados.take(20).forEachIndexed { k, (f, t) ->
            if (k > 0) Separador()
            Column(Modifier.fillMaxWidth().clickable { abrir(f) }.padding(vertical = 8.dp)) {
                Text(Totales.fecha(f), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                // Un fragmento alrededor de lo que buscaste.
                val n = Totales.normal(t); val i = n.indexOf(nq).coerceAtLeast(0)
                val a = (i - 50).coerceAtLeast(0); val b = (i + nq.length + 70).coerceAtMost(t.length)
                io.github.ruddy2003.vestigium.ui.TextoMarcado((if (a > 0) "…" else "") + t.substring(a, b).replace("\n", " ") + (if (b < t.length) "…" else ""), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (hallados.size > 20) Ayuda("y ${porNumero(hallados.size - 20, "1 día", "${hallados.size - 20} días")} más.")
    }
}

/** Las ideas del Ideario que mencionan el texto (en la idea, su título, quién lo dice, la fuente o su obra). */
@Composable
private fun EnIdeas(q: String, desde: String, hasta: String, abrir: (io.github.ruddy2003.vestigium.data.Idea) -> Unit) {
    val nq = Totales.normal(q.trim())
    if (nq.length < 2) return
    val hallados = remember(nq, desde, hasta, Repo.ideas, Repo.obras) {
        Repo.ideas.filter { it.fecha in desde..hasta && Totales.normal(io.github.ruddy2003.vestigium.data.Ideario.textoBuscable(it, Repo.obras)).contains(nq) }
            .sortedByDescending { it.creada }
    }
    if (hallados.isEmpty()) return
    Etiqueta("en tu ideario · ${hallados.size}")
    Tarjeta {
        hallados.take(12).forEachIndexed { k, i ->
            if (k > 0) Separador()
            Column(Modifier.fillMaxWidth().clickable { abrir(i) }.padding(vertical = 8.dp)) {
                Text(Totales.fecha(i.fecha) + (i.quien.joinToString(", ").ifBlank { null }?.let { " · $it" } ?: ""), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                io.github.ruddy2003.vestigium.ui.TextoMarcado(i.titulo.ifBlank { i.texto }.replace("\n", " ").take(160), style = MaterialTheme.typography.bodySmall, maxLines = 3)
            }
        }
        if (hallados.size > 12) Ayuda("y ${porNumero(hallados.size - 12, "1 idea", "${hallados.size - 12} ideas")} más.")
    }
}

/** Las cartas que se pueden leer (a otras personas, o tuyas ya abiertas). Las selladas nunca salen aquí. */
@Composable
private fun EnCartas(q: String, desde: String, hasta: String, abrir: (String) -> Unit) {
    val nq = Totales.normal(q.trim())
    if (nq.length < 2) return
    val hallados = remember(nq, desde, hasta, Repo.cartas) {
        Repo.cartas.filter { it.legible && it.escrita in desde..hasta && (Totales.normal(it.texto).contains(nq) || Totales.normal("@" + it.para).contains(nq)) }
            .sortedByDescending { it.escrita }
    }
    if (hallados.isEmpty()) return
    Etiqueta("en tus cartas · ${hallados.size}")
    Tarjeta {
        hallados.take(12).forEachIndexed { k, c ->
            if (k > 0) Separador()
            Column(Modifier.fillMaxWidth().clickable { abrir(c.id) }.padding(vertical = 8.dp)) {
                Text((if (c.alFuturo) "A mi yo del futuro" else "Para @${c.para}") + " · " + Totales.fecha(c.escrita), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                val t = c.texto; val n = Totales.normal(t); val i = n.indexOf(nq).coerceAtLeast(0)
                val a = (i - 50).coerceAtLeast(0); val b = (i + nq.length + 70).coerceAtMost(t.length)
                io.github.ruddy2003.vestigium.ui.TextoMarcado((if (a > 0) "…" else "") + t.substring(a, b).replace("\n", " ") + (if (b < t.length) "…" else ""), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
