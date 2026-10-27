package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.privado
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.esAnimo
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion

/** Una tarjeta con una cifra viva que abre una pantalla. */
class Tarjetita(val titulo: String, val cifra: String, val detalle: String, val ruta: String)

@Composable
fun RejillaTarjetas(l: List<Tarjetita>, ir: (String) -> Unit) {
    l.chunked(2).forEach { fila ->
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            fila.forEach { t ->
                Surface(onClick = { ir(t.ruta) }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(Estilo.seccion(t.titulo, Romano.latin(t.titulo)).first, style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(t.cifra, style = estiloCifra(), maxLines = 1)
                        Text(t.detalle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    }
                }
            }
            if (fila.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

private fun animoEn(dias: List<String>): Double? = dias.mapNotNull { f ->
    Repo.dias[f]?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == f && it.esAnimo }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average()
}.takeIf { it.isNotEmpty() }?.average()

/**
 * Vida: tu vida vista de lejos. La semana, el mes y el año; la cinta del día; tu gente; los mapas y lo que leíste;
 * los logros y las campañas; el dinero y las sustancias. Lo que se analiza (relaciones, herramientas) está en Análisis.
 */
@Composable
fun PantallaVida(nav: Nav) {
    val hoy = Fechas.diaLogico()
    val lunes = Fechas.lunesDe(hoy)
    val transcurridos = (java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(lunes), Fechas.parse(hoy)) + 1).toInt()
    val semana = (0 until transcurridos).map { Fechas.sumar(lunes, it.toLong()) }
    val a = animoEn(semana); val b = animoEn(semana.map { Fechas.sumar(it, -7) })
    val flecha = if (a != null && b != null) (if (a - b > 0.15) " ↑" else if (b - a > 0.15) " ↓" else " =") else ""
    val mes = hoy.take(7)
    val diasMes = Repo.dias.keys.count { it.startsWith(mes) && Repo.dias[it]?.let { d -> d.valencia != null || d.nota.isNotBlank() } == true }
    val anio = hoy.take(4)
    val ir: (String) -> Unit = { if (it == "cinta") Seleccion.cinta = null; nav.ir(it) }
    Hoja("Vida", subtitulo = "Tu vida vista de lejos: la semana, el año, tu gente, tus lugares y tus logros.", buscar = { nav.ir("buscar") }, ajustes = { nav.ir("mas") }) {
        RejillaTarjetas(listOf(
            Tarjetita("Resumen semanal", a?.let { "%.1f".format(it) + flecha } ?: "—", "ánimo de esta semana", "semana"),
            Tarjetita("La cinta", "${Repo.momentos.count { it.fecha == hoy && !it.privado } + Repo.tramos.count { it.fecha == hoy } + Repo.consumos.count { it.fecha == hoy && Sustancias.visible(it) }}",
                "cosas con hora hoy", "cinta"),
            Tarjetita("Calendario", "$diasMes / ${hoy.takeLast(2).toInt()}", "días con diario este mes", "calendario"),
            Tarjetita("Tu año", "${Repo.dias.keys.count { it.startsWith(anio) }}", "días registrados en $anio", "anno"),
            Tarjetita("Tu gente", "${io.github.ruddy2003.vestigium.data.Personas.menciones().size}",
                io.github.ruddy2003.vestigium.data.Personas.pendientes().size.let { if (it > 0) "personas · $it por clasificar" else "personas que mencionas" }, "personas"),
            Tarjetita("Constelación", "${Repo.dias.values.filter { it.fecha >= Fechas.sumar(hoy, -365) }.flatMap { Texto.personas(it.nota) }.toSet().size}", "personas en tu cielo este año", "constelacion"),
            Tarjetita("Totales", "${Repo.dias.size}", "días de datos" + (Repo.dias.keys.minOrNull()?.let { " desde ${Fechas.corto(it)}" } ?: ""), "totales"),
            Tarjetita("Mapa", "${Repo.lugares.size}", "lugares marcados", "mapa"),
        ), ir)
        Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RecuadroTuAnio(nav)
            RecuadroLogros(nav)
            RecuadroCampanas(nav)
            RecuadroMapas(nav)
            RecuadroObras(nav)
            RecuadroOtrasVidas(nav)
        }
        Seccion("Dinero y sustancias") {
            val gasto = Repo.gastos.filter { it.dia.startsWith(mes) }.sumOf { it.monto }
            val ingreso = Repo.ingresos.filter { it.dia.startsWith(mes) }.sumOf { it.monto }
            val semanaSus = Repo.consumos.filter { it.fecha >= Fechas.sumar(hoy, -6) && Sustancias.visible(it) }
            RejillaTarjetas(listOfNotNull(
                Tarjetita("Dinero", dinero(gasto), "gastado este mes" + if (ingreso > 0) " · entró ${dinero(ingreso)}" else "", "gastos"),
                Tarjetita("Sustancias", "${semanaSus.map { it.fecha }.distinct().size}", "días con algo en 7 días", "sustancias"),
                Tarjetita("Enfoque y siestas", horasMin(io.github.ruddy2003.vestigium.data.Enfoque.delDia(hoy).filterKeys { it != "siesta" }.values.sum()), "de enfoque hoy", "enfoque"),
            ), ir)
        }
    }
}
