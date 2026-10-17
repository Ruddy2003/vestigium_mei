@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.app.DatePickerDialog
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun PantallaTotales(nav: Nav) {
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
    var busqueda by remember { mutableStateOf("") }
    var indice by remember { mutableStateOf<String?>(null) }
    var todos by remember { mutableStateOf(false) }
    var abierta by remember { mutableStateOf<Pair<Totales.Indice, Totales.Entrada>?>(null) }

    Hoja("Totales", nav.atras, "Todo lo acumulado: cuánto, cuántas veces y desde cuándo. Cada fuente empezó en una fecha distinta, así que cada cifra dice desde cuándo cuenta.", latin = "SVMMA OMNIVM") {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todo", periodo == "todo") { periodo = "todo" }
            Ficha("este año", periodo == "anio") { periodo = "anio" }
            Ficha("este mes", periodo == "mes") { periodo = "mes" }
            Ficha("30 días", periodo == "30") { periodo = "30" }
            Ficha(if (periodo == "desde" && propio != null) "desde ${Totales.fecha(propio!!)}" else "desde…", periodo == "desde") {
                val d = Fechas.parse(propio ?: Fechas.sumar(hoy, -90))
                DatePickerDialog(ctx, { _, y, m, dd -> propio = Fechas.iso(LocalDate.of(y, m + 1, dd)); periodo = "desde" }, d.year, d.monthValue - 1, d.dayOfMonth).show()
            }
        }
        Ayuda(if (periodo == "todo") "Desde el primer dato que tengas hasta hoy." else "Del ${Totales.fecha(desde)} a hoy.", Modifier.padding(top = 4.dp))
        OutlinedTextField(
            busqueda, { busqueda = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true,
            placeholder = { Text("Buscar: plátano, @persona, artista, lugar, ejercicio…") },
            trailingIcon = if (busqueda.isNotEmpty()) ({ TextButton(onClick = { busqueda = "" }) { Text("×") } }) else null,
        )
        val r = res
        when {
            r == null -> Ayuda("Sumando…", Modifier.padding(top = 12.dp))
            busqueda.isNotBlank() -> Resultados(r, busqueda) { i, e -> abierta = i to e }
            r.grupos.isEmpty() && r.indices.isEmpty() -> Vacio("Nada registrado en este periodo.")
            else -> {
                r.grupos.forEach { g ->
                    Seccion(g.titulo, latin = g.latin) {
                        Tarjeta { g.cifras.forEachIndexed { k, c -> if (k > 0) HorizontalDivider(); FilaCifra(c) } }
                    }
                }
                if (r.indices.isNotEmpty()) Seccion("Índices", "Elige una lista y toca cualquier renglón para ver su historia mes a mes.", latin = "INDICES") {
                    val sel = r.indices.firstOrNull { it.id == indice } ?: r.indices.first()
                    FlowRow(Modifier.padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        r.indices.forEach { i -> Ficha("${i.titulo} · ${i.entradas.size}", i.id == sel.id) { indice = i.id; todos = false } }
                    }
                    Tarjeta {
                        val l = if (todos) sel.entradas.take(150) else sel.entradas.take(12)
                        l.forEachIndexed { k, e -> if (k > 0) HorizontalDivider(); FilaEntrada(sel, e, k + 1) { abierta = sel to e } }
                        if (sel.entradas.size > 12) TextButton(onClick = { todos = !todos }) {
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
        Tarjeta { cifras.forEachIndexed { k, c -> if (k > 0) HorizontalDivider(); FilaCifra(c) } }
    }
    hallados.forEach { (i, l) ->
        Etiqueta(i.titulo.lowercase())
        Tarjeta {
            l.take(10).forEachIndexed { k, e -> if (k > 0) HorizontalDivider(); FilaEntrada(i, e) { abrir(i, e) } }
            if (l.size > 10) Ayuda("y ${l.size - 10} más; escribe algo más específico.")
            // Si hay varias coincidencias (p. ej. tipos de plátano), también la suma de todas.
            if (l.size > 1 && l.map { it.unidad }.distinct().size == 1) {
                val s = Totales.Entrada("Todo lo que coincide con «$q»", l.first().unidad)
                l.forEach { e -> e.porMes.forEach { (m, v) -> s.porMes.merge(m, v, Double::plus) }; s.total += e.total; s.veces += e.veces; s.dias.addAll(e.dias); s.extra += e.extra
                    if (s.primera.isEmpty() || e.primera < s.primera) s.primera = e.primera; if (e.ultima > s.ultima) s.ultima = e.ultima }
                HorizontalDivider()
                FilaEntrada(i, s) { abrir(i, s) }
            }
        }
    }
}

@Composable
private fun Detalle(i: Totales.Indice, e: Totales.Entrada, cerrar: () -> Unit) {
    AlertDialog(
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
                    Clave("ritmo", "${e.dias.size} de $lapso días (${Totales.n(100.0 * e.dias.size / lapso)}%)", ancho = 9)
                }
                if (e.porMes.size >= 2) Meses(e.porMes, e.unidad)
            }
        },
        confirmButton = { TextButton(onClick = cerrar) { Text("cerrar", style = MaterialTheme.typography.labelLarge) } },
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
    Etiqueta("por mes")
    Canvas(Modifier.fillMaxWidth().aspectRatio(2.6f).padding(top = 4.dp)) {
        val abajo = 30f
        val w = size.width / meses.size
        val pincel = Paint().apply { color = texto; textSize = 22f; isAntiAlias = true; textAlign = Paint.Align.CENTER }
        meses.forEachIndexed { k, (m, v) ->
            val h = (size.height - abajo) * (v / max).toFloat()
            if (v > 0) drawRoundRect(barra, Offset(k * w + w * 0.15f, size.height - abajo - h), Size(w * 0.7f, h), CornerRadius(3f, 3f))
            if (meses.size <= 12 || k % 2 == 0) drawContext.canvas.nativeCanvas.drawText("EFMAMJJASOND"[m.takeLast(2).toInt() - 1].toString(), k * w + w / 2, size.height - 6f, pincel)
        }
    }
    Ayuda("${Fechas.mesTitulo(meses.first().first)} a ${Fechas.mesTitulo(meses.last().first)} · más alto: ${Totales.formato(pico.second, unidad)} en ${Fechas.mesTitulo(pico.first).lowercase()}")
}
