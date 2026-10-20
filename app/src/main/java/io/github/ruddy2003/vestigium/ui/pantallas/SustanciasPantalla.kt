@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Consumo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.FilaMenu
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio

/** Abre la hoja rápida de sustancias: con [id], ese registro (completar o editar); con [sustancia], esa sustancia. */
fun abrirConsumo(ctx: Context, id: String? = null, sustancia: String? = null) {
    val i = Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_CONSUMO).putExtra(QuickActivity.ORIGEN, "app")
    if (id != null) i.putExtra(QuickActivity.CONSUMO, id)
    if (sustancia != null) i.putExtra(QuickActivity.SUSTANCIA, sustancia)
    ctx.startActivity(i)
}

private fun cifra(x: Double) = if (x % 1.0 == 0.0) "${x.toInt()}" else "%.1f".format(x)

/** Una línea de resumen de un episodio: nivel, cantidad, duración y cruda. */
fun resumenConsumo(c: Consumo): String = listOfNotNull(
    Sustancias.maximo(c)?.let { "nivel $it/10" },
    c.cantidad?.let { "${cifra(it)} ${Catalogo.sustancia(c.sustancia)?.unidad ?: ""}".trim() },
    Sustancias.horas(c)?.let { "%.1f h".format(it) } ?: if (c.fin == null && Catalogo.sustancia(c.sustancia)?.conNivel == true) "sin fin" else null,
    c.cruda?.let { "cruda $it/5" },
    if (!c.completo) "por completar" else null,
).joinToString(" · ")

/**
 * Sustancias: alcohol, cannabis, tabaco y cafeína como un dato más. Entran en la cinta, en la tabla por hora y en los análisis;
 * lo que ocultes en Privacidad deja de verse en las pantallas pero sigue en los análisis.
 */
@Composable
fun PantallaSustancias(nav: Nav) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    val ocultas = Repo.config.sustancias_ocultas
    val todos = remember(Repo.consumos, ocultas) { Repo.consumos.filter { Sustancias.visible(it) }.sortedByDescending { it.inicio } }
    var mostrar by remember { mutableIntStateOf(30) }
    Hoja("Sustancias", nav.atras, "Lo básico en el momento (qué, desde cuándo, cómo vas); la duración, el máximo y la cruda, a la mañana siguiente.") {
        FlowRow(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.SUSTANCIAS.filter { Sustancias.visible(it.id) }.forEach { s -> BotonSecundario("+ ${s.nombre.lowercase()}") { abrirConsumo(ctx, sustancia = s.id) } }
        }
        val abiertos = Sustancias.abiertos().filter { Sustancias.visible(it) }
        abiertos.forEach { c ->
            Tarjeta(Modifier.padding(top = 10.dp)) {
                Text("${Catalogo.nombreSustancia(c)} desde las ${Fechas.horaDe(c.inicio)}", style = MaterialTheme.typography.titleSmall)
                Ayuda(listOfNotNull(c.niveles.lastOrNull()?.let { "último nivel ${it.nivel}/10 a las ${Fechas.horaDe(it.ts)}" }, c.cantidad?.let { "${cifra(it)} ${Catalogo.sustancia(c.sustancia)?.unidad}" }).joinToString(" · ").ifBlank { "en curso" })
                BotonSecundario("¿cómo vas? · ya se me pasó", Modifier.padding(top = 6.dp)) { abrirConsumo(ctx, sustancia = c.sustancia) }
            }
        }
        val pendientes = Sustancias.porCompletar(hoy).filter { Sustancias.visible(it) }
        if (pendientes.isNotEmpty()) Seccion("Falta completar", "Hasta qué hora duró, el máximo y la cruda. Unos toques.") {
            pendientes.forEach { c -> FilaMenu("${Fechas.corto(c.fecha)} · ${Catalogo.nombreSustancia(c)}", "desde las ${Fechas.horaDe(c.inicio)}") { abrirConsumo(ctx, id = c.id) } }
        }

        if (Sustancias.visible("cafeina")) {
            val ahora = System.currentTimeMillis()
            val (caf, _) = remember(Repo.consumos, hoy) { Sustancias.dosis(Fechas.sumar(hoy, -1), hoy) }
            val pasos = (0..96).map { ahora + it * 15 * 60_000L }
            val curva = remember(caf, ahora / 60_000) { Sustancias.cafeinaEn(caf, pasos) }
            val ahoraMg = curva.firstOrNull() ?: 0.0
            Seccion("Cafeína en el cuerpo", "Estimada con una vida media de ${cifra(Repo.config.cafeina_vida_media_h)} h (cámbiala en Privacidad). Sale de MacroFactor, con la hora de cada alimento, o de lo que anotes aquí.") {
                if (caf.isEmpty()) Vacio("Nada de cafeína en las últimas horas.")
                else {
                    Clave("ahora", "${ahoraMg.toInt()} mg")
                    Clave("última dosis", Fechas.horaDe(Fechas.isoDeMillis(caf.last().ms)) + " · ${caf.last().cantidad.toInt()} mg")
                    val bajo = pasos.zip(curva).firstOrNull { it.second < 50 }?.first
                    Clave("baja de 50 mg", if (ahoraMg < 50) "ya" else bajo?.let { Fechas.horaDe(Fechas.isoDeMillis(it)) } ?: "en más de un día")
                    Clave("hoy en total", "${caf.filter { Fechas.diaLogicoDeMillis(it.ms) == hoy }.sumOf { it.cantidad }.toInt()} mg")
                }
            }
        }

        val mes = todos.filter { it.fecha >= Fechas.sumar(hoy, -29) }
        Seccion("Últimos 30 días") {
            if (mes.isEmpty()) Vacio("Nada registrado en los últimos 30 días.")
            mes.groupBy { it.sustancia }.forEach { (id, l) ->
                val info = Catalogo.sustancia(id)
                val partes = listOfNotNull(
                    l.map { it.fecha }.distinct().size.let { n -> if (n == 1) "1 día" else "$n días" },
                    l.mapNotNull { Sustancias.maximo(it) }.takeIf { it.isNotEmpty() }?.let { "nivel máx. medio %.1f".format(it.average()) },
                    l.mapNotNull { it.cantidad }.takeIf { it.isNotEmpty() }?.let { "${cifra(it.sum())} ${info?.unidad ?: ""}".trim() },
                    l.mapNotNull { Sustancias.horas(it) }.takeIf { it.isNotEmpty() }?.let { "%.1f h de media".format(it.average()) },
                    l.mapNotNull { it.cruda }.takeIf { it.isNotEmpty() }?.let { "cruda media %.1f".format(it.average()) },
                )
                Clave(info?.nombre ?: id, partes.joinToString(" · "))
            }
        }

        Seccion("Historial", "Toca uno para completarlo, corregirlo o borrarlo.") {
            if (todos.isEmpty()) Vacio("Todavía no hay registros. Usa los botones de arriba o «¿Cómo estás ahora?» en Diario.")
            todos.take(mostrar).forEach { c ->
                val bac = if (c.sustancia == "alcohol") remember(c) { Sustancias.alcoholMaximo(c) } else null
                FilaMenu("${Fechas.corto(c.fecha)} · ${Fechas.horaDe(c.inicio)} — ${Catalogo.nombreSustancia(c)}",
                    resumenConsumo(c) + (bac?.let { " · ≈%.2f g/L estimado".format(it) } ?: "")) { abrirConsumo(ctx, id = c.id) }
            }
            if (todos.size > mostrar) BotonSecundario("ver ${minOf(30, todos.size - mostrar)} más", Modifier.fillMaxWidth().padding(top = 8.dp)) { mostrar += 30 }
        }
        if (ocultas.isNotEmpty()) Ayuda("Ocultas de las pantallas: " + ocultas.joinToString(", ") { Catalogo.sustancia(it)?.nombre?.lowercase() ?: it } +
            ". Siguen en los análisis y en la exportación. Se cambia en Privacidad.", Modifier.padding(top = 16.dp))
    }
}
