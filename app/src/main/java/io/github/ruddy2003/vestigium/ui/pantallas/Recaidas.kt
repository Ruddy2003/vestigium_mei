@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Privado
import io.github.ruddy2003.vestigium.data.Recaidas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Escala
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta

/**
 * Privado: lo único que muestra el seguimiento privado. Cada día, los medicamentos y (con el seguimiento privado) la intensidad
 * del impulso, la recaída, la libido, los detonantes y el hábito ligado; luego a qué hora, qué día y dónde aparecen, cómo va
 * por mes y el registro para corregir. Nada de esto sale en otra pantalla, reporte, widget, logro ni análisis (ver [Privado]);
 * sí va en la exportación para R.
 */
@Composable
fun PantallaRecaidas(nav: Nav) {
    val ctx = LocalContext.current
    val c = Repo.config
    val hoy = Fechas.diaLogico()
    var fecha by remember { mutableStateOf(Seleccion.privado ?: hoy) }
    DisposableEffect(Unit) { onDispose { Seleccion.privado = null } }
    var editando by remember { mutableStateOf<Momento?>(null) }
    editando?.let { m -> DialogoMomento(m) { editando = null } }
    fun registrar(tipo: String) {
        ctx.startActivity(android.content.Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_MOMENTO)
            .putExtra(QuickActivity.TIPO, tipo).putExtra(QuickActivity.ORIGEN, "app"))
    }
    Hoja("Privado", nav.atras, "Solo aquí. No aparece en ninguna otra pantalla, reporte, widget, logro ni análisis de la app; sí va en tu exportación para R. Sirve para ver en qué horas, lugares y situaciones es más difícil, no para juzgarte.") {
        // ---- El día: se guarda campo por campo sobre lo que ya hay, para no pisar lo que se anotó en Día.
        val d = Repo.dias[fecha] ?: Dia(fecha = fecha)
        fun guardar(f: (Dia) -> Dia) = Repo.guardarDia(f(Repo.dia(fecha)))
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { fecha = Fechas.sumar(fecha, -1) }) { Text("‹", style = MaterialTheme.typography.titleLarge) }
            Text(if (fecha == hoy) "Hoy" else Fechas.corto(fecha), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            TextButton(onClick = { fecha = Fechas.sumar(fecha, 1) }, enabled = fecha < hoy) { Text("›", style = MaterialTheme.typography.titleLarge) }
        }
        Tarjeta {
            Etiqueta("Medicamentos")
            ListaEtiquetas(d.medicamentos, "nombre del medicamento", normalizar = { Texto.slug(it) }) { l -> guardar { it.copy(medicamentos = l) } }
            val siempre = remember(Repo.dias, fecha) { Repo.dias.values.filter { it.fecha < fecha && it.medicamentos.isNotEmpty() }.maxByOrNull { it.fecha }?.medicamentos }
            if (d.medicamentos.isEmpty() && siempre != null) TextButton(onClick = { guardar { it.copy(medicamentos = siempre) } }) {
                Text("lo de siempre: " + siempre.joinToString(", ") { it.replace('_', ' ') }, style = MaterialTheme.typography.labelLarge)
            }
            if (c.seguimiento_privado) {
                Etiqueta("Intensidad del impulso")
                Escala(d.impulso, 0, 5, Catalogo.ANCLAS["impulso"]) { v -> guardar { it.copy(impulso = v) } }
                Etiqueta("¿Hubo recaída?")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Ficha("No", d.recaida == false) { guardar { x -> x.copy(recaida = if (x.recaida == false) null else false) } }
                    Ficha("Sí", d.recaida == true) { guardar { x -> x.copy(recaida = if (x.recaida == true) null else true) } }
                }
                Etiqueta("Qué lo detonó")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.DETONANTES.forEach { t ->
                        val activo = t in d.detonantes
                        Ficha(t.replace("_", " "), activo) { guardar { x -> x.copy(detonantes = if (t in x.detonantes) x.detonantes - t else x.detonantes + t) } }
                    }
                }
                Privado.habito(c)?.takeIf { it.activo }?.let { h ->
                    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = d.habitos[h.id] == true, onCheckedChange = { v -> guardar { x -> x.copy(habitos = if (v) x.habitos + (h.id to true) else x.habitos - h.id) } })
                        Text(h.nombre, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Ayuda("una recaída lo deja en 0")
                    }
                }
            }
            val delDia = Repo.momentos.filter { it.fecha == fecha && it.privado }.sortedBy { it.ts }
            if (delDia.isNotEmpty()) Clave("con hora", delDia.joinToString("  ") { m ->
                Fechas.horaDe(m.ts) + " " + Catalogo.nombreTipo(m.tipo).lowercase() + (m.intensidad?.let { " $it" } ?: "") + (m.duracion_min?.let { " · ${duracion(it.toDouble())}" } ?: "")
            })
        }
        BotonPrincipal("Registrar un impulso ahora", Modifier.fillMaxWidth().padding(top = 12.dp)) { registrar("impulso") }
        if (c.seguimiento_privado) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonSecundario("Una recaída", Modifier.weight(1f)) { registrar("recaida") }
                BotonSecundario("La libido", Modifier.weight(1f)) { registrar("libido") }
            }
            Ayuda("También con los botones del registro rápido. Las ganas que pasaron sin recaer se anotan como «Impulso».", Modifier.padding(top = 6.dp))
        } else Ayuda("Para anotar también recaídas, libido y detonantes, activa el seguimiento privado en Más → Privacidad.", Modifier.padding(top = 6.dp))

        val r = remember(Repo.momentos, Repo.dias) { Recaidas.resumen() }
        if (c.seguimiento_privado) {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Cifra(r.diasDesdeUltima?.toString() ?: "—", "días desde la última", Modifier.weight(1f))
                Cifra("${r.rachaActual}", "días seguidos sin", Modifier.weight(1f))
                Cifra("${r.rachaMaxima}", "racha más larga", Modifier.weight(1f))
            }
            if (r.impulsos > 0) Tarjeta(Modifier.padding(top = 10.dp)) {
                val pct = r.impulsosSuperados * 100 / r.impulsos
                Text("$pct% de tus impulsos pasaron sin recaída", style = MaterialTheme.typography.titleMedium)
                Ayuda("${r.impulsosSuperados} de ${r.impulsos} ${if (r.impulsos == 1) "impulso registrado no terminó" else "impulsos registrados no terminaron"} en recaída en las 3 horas siguientes. Cada uno cuenta.")
            }
            if (r.minutosImpulsoARecaida.isNotEmpty()) Tarjeta(Modifier.padding(top = 10.dp)) {
                val m = r.minutosImpulsoARecaida.sorted()
                val mediana = m[m.size / 2]
                Text("Entre las ganas y la recaída: ${duracion(mediana)} (mediana)", style = MaterialTheme.typography.titleMedium)
                Ayuda("De ${m.size} ${if (m.size == 1) "recaída" else "recaídas"} con un impulso registrado antes. Ese rato es la ventana para cambiar de lugar, salir o hablar con alguien.")
            }
            if (r.duraciones.isNotEmpty()) Tarjeta(Modifier.padding(top = 10.dp)) {
                val dd = r.duraciones.sorted()
                Text("Duración típica: ${duracion(dd[dd.size / 2].toDouble())} (mediana)", style = MaterialTheme.typography.titleMedium)
                Ayuda("De ${dd.size} ${if (dd.size == 1) "recaída" else "recaídas"} con duración anotada · la más corta ${duracion(dd.first().toDouble())}, la más larga ${duracion(dd.last().toDouble())} · ${duracion(dd.sum().toDouble())} en total.")
            }
        }
        if (r.impulsosHora.sum() + r.recaidasHora.sum() > 0) Seccion("A qué hora", "Cada barra es una hora del día, de 0:00 a 23:00 (según la hora en que empezó).") {
            Tarjeta {
                Text("Impulsos · ${r.impulsosHora.sum()}", style = MaterialTheme.typography.bodySmall)
                Barras24(r.impulsosHora, MaterialTheme.colorScheme.secondary)
                if (c.seguimiento_privado) {
                    Text("Recaídas · ${r.recaidasHora.sum()}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    Barras24(r.recaidasHora, MaterialTheme.colorScheme.error)
                    val pico = r.recaidasHora.indices.maxByOrNull { r.recaidasHora[it] }?.takeIf { r.recaidasHora[it] > 0 }
                    if (pico != null) Ayuda("La hora más difícil: de las $pico:00 a las ${(pico + 1) % 24}:00.", Modifier.padding(top = 6.dp))
                }
            }
        }
        if (c.seguimiento_privado) {
            Seccion("Qué día de la semana") {
                Tarjeta {
                    val nombres = listOf("lun", "mar", "mié", "jue", "vie", "sáb", "dom")
                    val max = (r.recaidasSemana.maxOrNull() ?: 0).coerceAtLeast(1)
                    nombres.forEachIndexed { i, n -> Fila(n, r.recaidasSemana[i], max) }
                }
            }
            Seccion("Dónde, qué lo detona y qué hacías") {
                Lista("Lugares", r.lugares)
                Lista("Detonantes", r.detonantes.map { it.first.replace("_", " ") to it.second })
                Lista("Justo antes", r.antes.map { (k, v) -> (Catalogo.ANTES.firstOrNull { it.id == k }?.nombre ?: k) to v })
                Lista("Aparato", r.aparatos.map { (k, v) -> (Catalogo.DISPOSITIVOS.firstOrNull { it.id == k }?.nombre ?: k) to v })
            }
        }
        // ---- Cómo va: los últimos 12 meses, mes por mes.
        val meses = remember(Repo.momentos, Repo.dias, hoy) { porMes(hoy) }
        if (meses.any { it.recaidas + it.impulsos + it.medicamentos > 0 }) Seccion("Cómo va, mes por mes", "Los últimos 12 meses. Un mes con menos registros puede ser un mes con menos días anotados: compáralo con cuántos días llenaste.") {
            Tarjeta {
                val maxR = meses.maxOf { it.recaidas }.coerceAtLeast(1); val maxI = meses.maxOf { it.impulsos }.coerceAtLeast(1)
                if (c.seguimiento_privado) {
                    Text("Días con recaída", style = MaterialTheme.typography.bodySmall)
                    meses.forEach { m -> Fila(m.nombre, m.recaidas, maxR) }
                }
                Text("Impulsos registrados", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                meses.forEach { m -> Fila(m.nombre, m.impulsos, maxI) }
                if (meses.any { it.medicamentos > 0 }) {
                    val maxM = meses.maxOf { it.medicamentos }.coerceAtLeast(1)
                    Text("Días con medicamentos", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    meses.forEach { m -> Fila(m.nombre, m.medicamentos, maxM) }
                }
            }
        }
        val meds = remember(Repo.dias) { Repo.dias.values.flatMap { it.medicamentos }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.map { it.key.replace('_', ' ') to it.value } }
        if (meds.isNotEmpty()) Seccion("Medicamentos") { Lista("Días que tomaste cada uno", meds) }
        // Libido: distinta del impulso. Por hora, nivel medio y en qué estaba el foco.
        val libido = Repo.momentos.filter { it.tipo == "libido" }
        if (c.seguimiento_privado && libido.isNotEmpty()) Seccion("Libido", "Escala de 0 (nada) a 5 (dominante). No es lo mismo que un impulso; sirve para ver cuándo sube.") {
            Tarjeta {
                val horas = IntArray(24).also { a -> libido.forEach { m -> runCatching { a[java.time.OffsetDateTime.parse(m.ts).atZoneSameInstant(Fechas.zona()).hour]++ } } }
                Text("Registros por hora · ${libido.size}", style = MaterialTheme.typography.bodySmall)
                Barras24(horas, MaterialTheme.colorScheme.tertiary)
                val niv = libido.mapNotNull { it.intensidad }
                if (niv.isNotEmpty()) Ayuda("Nivel medio %.1f de 5 · ".format(niv.average()) + "${libido.count { (it.intensidad ?: 0) >= 4 }} veces fuerte o dominante.", Modifier.padding(top = 6.dp))
                Lista("Foco", libido.mapNotNull { it.foco }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
                    .map { (k, v) -> (Catalogo.FOCOS.firstOrNull { it.id == k }?.nombre ?: k) to v })
            }
        }
        val lista = Repo.momentos.filter { it.privado }.sortedByDescending { it.ts }
        if (lista.isNotEmpty()) Seccion("Registro", "Los últimos 30. Toca uno para corregir la hora, la duración o la nota.") {
            Tarjeta {
                lista.take(30).forEachIndexed { i, m ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().clickable { editando = m }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${Fechas.corto(m.fecha)} · ${Fechas.horaDe(m.ts)} · ${Catalogo.nombreTipo(m.tipo)}" + (m.intensidad?.let { " $it" } ?: "") +
                                (m.duracion_min?.let { " · ${duracion(it.toDouble())}" } ?: ""), style = MaterialTheme.typography.bodyLarge)
                            val extra = listOfNotNull(Repo.lugares.firstOrNull { it.id == m.lugar }?.nombre, m.detonantes.joinToString(", ") { it.replace("_", " ") }.ifBlank { null },
                                m.antes?.let { a -> Catalogo.ANTES.firstOrNull { it.id == a }?.nombre?.lowercase() }, m.texto.ifBlank { null }).joinToString(" · ")
                            if (extra.isNotEmpty()) Text(extra, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("✎", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        Ayuda((if (c.seguimiento_privado) "${r.totalDias} días con recaída de ${r.diasConRespuesta} días con seguimiento. " else "") +
            "Para cruzar esto con sueño, pantalla o ánimo, usa tu exportación en R (vm_panel_extendido.csv: impulso, recaida, n_recaida, n_impulso, libido_max, medicamentos_n, med_…, det_…).",
            Modifier.padding(top = 12.dp))
        // Lo privado borrado se recupera aquí, nunca en la papelera general (N-14).
        ListaPapelera(privado = true)
    }
}

private class Mes(val nombre: String, val recaidas: Int, val impulsos: Int, val medicamentos: Int)

/** Los últimos 12 meses (el actual al final): días con recaída, impulsos registrados y días con medicamentos. */
private fun porMes(hoy: String): List<Mes> {
    val nombres = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
    val actual = java.time.YearMonth.parse(hoy.take(7))
    val diasRec = Recaidas.dias()
    return (11 downTo 0).map { k ->
        val ym = actual.minusMonths(k.toLong()); val pre = ym.toString()
        Mes("${nombres[ym.monthValue - 1]} ${ym.year % 100}", diasRec.count { it.startsWith(pre) },
            Repo.momentos.count { it.tipo == "impulso" && it.fecha.startsWith(pre) },
            Repo.dias.values.count { it.fecha.startsWith(pre) && it.medicamentos.isNotEmpty() })
    }
}

private fun duracion(min: Double): String = if (min < 60) "${min.toInt()} min" else "%.1f h".format(min / 60)

@Composable
private fun Barras24(v: IntArray, color: androidx.compose.ui.graphics.Color) {
    val fondo = MaterialTheme.colorScheme.surfaceVariant
    Canvas(Modifier.fillMaxWidth().aspectRatio(9f).padding(top = 4.dp)) {
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
private fun Fila(nombre: String, n: Int, max: Int) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(nombre, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.18f))
        Canvas(Modifier.weight(0.72f).padding(vertical = 4.dp).aspectRatio(18f)) {
            drawRoundRect(cs.surfaceVariant, Offset.Zero, size, CornerRadius(4f, 4f))
            if (n > 0) drawRoundRect(cs.error.copy(alpha = 0.8f), Offset.Zero, Size(size.width * n / max, size.height), CornerRadius(4f, 4f))
        }
        Text("$n", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.1f).padding(start = 6.dp))
    }
}

@Composable
private fun Lista(titulo: String, l: List<Pair<String, Int>>) {
    Tarjeta(Modifier.padding(bottom = 8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        if (l.isEmpty()) Ayuda("Nada registrado todavía.")
        Column {
            val max = (l.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
            l.take(8).forEach { (k, v) -> Fila(k, v, max) }
        }
    }
}
