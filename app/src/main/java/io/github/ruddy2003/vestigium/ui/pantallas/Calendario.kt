package io.github.ruddy2003.vestigium.ui.pantallas

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Meandro
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import java.time.LocalDate

/** Fecha que otra pantalla le pide abrir al Día. */
object Seleccion {
    var pedida by mutableStateOf<String?>(null)
    var ultimoMes by mutableStateOf<String?>(null)
    /** Sección de Día a la que hay que llevar al abrirlo (por ejemplo, desde el atajo «Diario de hoy»). */
    var seccion by mutableStateOf<String?>(null)
    /** Día que se está llenando en la hoja de la noche, y día al que vuelve Día al cerrarla. */
    var noche by mutableStateOf<String?>(null)
    var volverA by mutableStateOf<String?>(null)
    /** Entrada abierta en la biblioteca del diario. */
    var entrada by mutableStateOf<String?>(null)
    fun abrir(nav: Nav, fecha: String) { pedida = fecha; ultimoMes = Fechas.mesDe(fecha); nav.pestana("dia") }
}

private data class CapaCal(val id: String, val nombre: String, val divergente: Boolean, val valor: (String) -> Double?, val rango: (List<Double>) -> Pair<Double, Double>, val texto: (Double) -> String)

private fun capas(): List<CapaCal> {
    val mu = Repo.momentos.filter { it.tipo == "muestreo" && it.valencia != null }.groupBy { it.fecha }
    val gastos = Repo.gastos.groupBy { it.dia }.mapValues { e -> e.value.sumOf { it.monto } }
    val fijo = { a: Double, b: Double -> { _: List<Double> -> a to b } }
    val datos = { xs: List<Double> -> (xs.minOrNull() ?: 0.0) to (xs.maxOrNull()?.takeIf { it > 0 } ?: 1.0) }
    return listOf(
        CapaCal("animo", "ánimo", true, { f -> Repo.dias[f]?.valencia?.toDouble() ?: mu[f]?.mapNotNull { it.valencia }?.average() }, fijo(1.0, 7.0)) { "%.1f".format(it) },
        CapaCal("sueno", "sueño", true, { f -> Repo.salud[f]?.sueno_min?.let { it / 60.0 } }, fijo(5.0, 8.5)) { "%.1f h".format(it) },
        CapaCal("pasos", "pasos", false, { f -> Repo.salud[f]?.pasos?.toDouble() }, datos) { "%,.0f".format(it) },
        CapaCal("pantalla", "pantalla", false, { f -> Repo.pantalla[f]?.total_min?.toDouble() }, datos) { "${(it / 60).toInt()} h ${(it % 60).toInt()} m" },
        CapaCal("gasto", "gasto", false, { f -> gastos[f] }, datos) { "$%,.0f".format(it) },
        CapaCal("fotos", "fotos", false, { f -> Repo.fotos[f]?.fotos?.toDouble() }, datos) { "%.0f".format(it) },
        CapaCal("momentos", "momentos", false, { f -> Repo.momentos.count { it.fecha == f && it.tipo != "muestreo" }.toDouble().takeIf { it > 0 } }, datos) { "%.0f".format(it) },
        CapaCal("lluvia", "lluvia", false, { f -> Repo.entorno[f]?.precip_mm }, datos) { "%.1f mm".format(it) },
    )
}

@Composable
fun PantallaCalendario(nav: Nav) {
    val ctx = LocalContext.current
    var mes by remember { mutableStateOf(Seleccion.ultimoMes ?: Fechas.mesDe(Fechas.diaLogico())) }
    var capaId by remember { mutableStateOf("animo") }
    var verFotos by remember { mutableStateOf(false) }
    val oscuro = esOscuro()
    val todas = capas()
    val capa = todas.first { it.id == capaId }
    val primero = Fechas.parse("$mes-01")
    val hoy = Fechas.diaLogico()
    val dias = (0 until primero.lengthOfMonth()).map { Fechas.iso(primero.plusDays(it.toLong())) }
    val valores = dias.associateWith { capa.valor(it) }
    val (lo, hi) = capa.rango(valores.values.filterNotNull())

    fun ir(fecha: String) { Seleccion.abrir(nav, fecha) }

    Hoja("Calendario", nav.atras, subtitulo = "Toca un día para abrirlo en el editor. El color muestra la capa elegida.") {
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { mes = Fechas.mesDe(Fechas.iso(primero.minusMonths(1))) }, shape = RoundedCornerShape(4.dp)) { Text("‹", fontSize = 18.sp) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${io.github.ruddy2003.vestigium.ui.Estilo.mes(primero.monthValue)} · ${io.github.ruddy2003.vestigium.ui.Estilo.numeral(primero.year)}", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                Text("// ${Fechas.mesTitulo(mes).lowercase()}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(onClick = { mes = Fechas.mesDe(Fechas.iso(primero.plusMonths(1))) }, shape = RoundedCornerShape(4.dp)) { Text("›", fontSize = 18.sp) }
        }

        Row(Modifier.fillMaxWidth().padding(top = 14.dp)) {
            io.github.ruddy2003.vestigium.ui.Estilo.semana.forEach { d -> Text(d, Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        val desfase = primero.dayOfWeek.value - 1
        val celdas = List(desfase) { null } + dias
        celdas.chunked(7).forEach { semana ->
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 0 until 7) {
                    val f = semana.getOrNull(i)
                    if (f == null) { Spacer(Modifier.weight(1f)); continue }
                    val v = valores[f]
                    val t = if (v == null) null else if (hi > lo) ((v - lo) / (hi - lo)).toFloat() else 0.5f
                    val fondo = when {
                        t == null -> MaterialTheme.colorScheme.surface
                        capa.divergente -> Tonos.divergente(t, oscuro)
                        else -> Tonos.secuencial(t, oscuro)
                    }
                    Celda(Modifier.weight(1f), f, fondo, f == hoy, f > hoy, verFotos) { ir(f) }
                }
            }
        }

        Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (capa.divergente) capa.texto(lo) else "0", style = MaterialTheme.typography.labelSmall)
            Row(Modifier.weight(1f).padding(horizontal = 8.dp).height(8.dp)) {
                for (k in 0..10) Box(Modifier.weight(1f).height(8.dp).background(if (capa.divergente) Tonos.divergente(k / 10f, oscuro) else Tonos.secuencial(k / 10f, oscuro)))
            }
            Text(capa.texto(hi), style = MaterialTheme.typography.labelSmall)
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("▣ fotos", verFotos) { verFotos = !verFotos }
            todas.forEach { c -> Ficha(c.nombre, c.id == capaId) { capaId = c.id } }
        }
        Ayuda("▪ diario escrito · • momentos · ✦ hito · KAL, NON, ID: calendas, nonas e idus del calendario romano.", Modifier.padding(top = 8.dp))

        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonSecundario("hoy") { ir(hoy) }
            BotonSecundario("ir a una fecha…") {
                val p = Fechas.parse(hoy)
                DatePickerDialog(ctx, { _, y, m, d -> ir(Fechas.iso(LocalDate.of(y, m + 1, d))) }, p.year, p.monthValue - 1, p.dayOfMonth).show()
            }
        }

        Seccion("Este mes") {
            Tarjeta {
                val conDiario = dias.count { Repo.dias[it] != null }
                val transcurridos = dias.count { it <= hoy }
                val vals = dias.mapNotNull { Repo.dias[it]?.valencia }
                Clave("dias", "$conDiario de $transcurridos con registro")
                if (vals.isNotEmpty()) Clave("animus", "%.2f de media · ${vals.size} días".format(vals.average()))
                val mejor = dias.filter { Repo.dias[it]?.valencia != null }.maxByOrNull { Repo.dias[it]!!.valencia!! }
                mejor?.let { Clave("optimus", Fechas.corto(it) + " · " + Repo.dias[it]?.valencia) }
                val mom = Repo.momentos.count { it.fecha.startsWith(mes) && it.tipo != "muestreo" }
                Clave("momenta", "$mom")
                val gasto = Repo.gastos.filter { it.fecha.startsWith(mes) }.sumOf { it.monto }
                if (gasto > 0) Clave("svmptvs", "$%,.0f".format(gasto))
                val hitos = dias.flatMap { Repo.dias[it]?.hitos ?: emptyList() }
                if (hitos.isNotEmpty()) Clave("memorabilia", hitos.distinct().take(8).joinToString(", "))
                val esc = Repo.ascensos.count { it.fecha.startsWith(mes) }
                if (esc > 0) Clave("ascensvs", "$esc sesiones")
            }
        }
    }
}

@Composable
private fun Celda(modifier: Modifier, fecha: String, fondo: Color, hoy: Boolean, futuro: Boolean, verFotos: Boolean, onClick: () -> Unit) {
    val d = Repo.dias[fecha]
    // Con «fotos», los días con foto del día la muestran de fondo (las letras van en blanco sobre una sombra).
    val conFoto = verFotos && d?.foto != null
    val tinta = if (conFoto) Color.White else Tonos.sobre(fondo)
    val nMom = Repo.momentos.count { it.fecha == fecha && it.tipo != "muestreo" }
    val marca = io.github.ruddy2003.vestigium.ui.Estilo.marca(fecha)
    Box(
        modifier.aspectRatio(0.78f).clip(RoundedCornerShape(3.dp)).background(fondo)
            .border(if (hoy) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(3.dp))
            .clickable(onClick = onClick),
    ) {
        if (conFoto) {
            Miniatura(android.net.Uri.parse(d!!.foto), 200, Modifier.matchParentSize())
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.28f)))
        }
        Box(Modifier.matchParentSize().padding(3.dp)) {
        Text(fecha.takeLast(2).trimStart('0'), style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = if (futuro) tinta.copy(alpha = 0.4f) else tinta,
            modifier = Modifier.align(Alignment.TopStart))
        if (marca != null) Text(marca, style = MaterialTheme.typography.titleLarge.copy(fontSize = 7.sp, letterSpacing = 0.5.sp), color = tinta.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.TopEnd))
        Row(Modifier.align(Alignment.BottomStart), verticalAlignment = Alignment.CenterVertically) {
            if (d != null && d.nota.isNotBlank()) Box(Modifier.size(5.dp).background(tinta))
            if (d != null && d.hitos.isNotEmpty()) Text("✦", fontSize = 8.sp, color = tinta, modifier = Modifier.padding(start = 2.dp))
            if (nMom > 0) Text("•".repeat(minOf(nMom, 3)), fontSize = 9.sp, color = tinta, modifier = Modifier.padding(start = 2.dp))
        }
        if (d?.foto != null && !conFoto) Box(Modifier.align(Alignment.BottomEnd).size(4.dp).background(MaterialTheme.colorScheme.primary))
        }
    }
}

