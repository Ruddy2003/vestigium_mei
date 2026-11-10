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
    val dc = io.github.ruddy2003.vestigium.ui.comun.disenoComun(Estilo.tema)
    val osc = io.github.ruddy2003.vestigium.ui.esOscuro()
    val tipo = io.github.ruddy2003.vestigium.ui.comun.TipoTarjeta.VIDA
    l.chunked(2).forEach { fila ->
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            fila.forEach { t ->
                io.github.ruddy2003.vestigium.ui.comun.TarjetaTema(tipo, Modifier.weight(1f), onClick = { ir(t.ruta) }) {
                    Text(Estilo.seccion(t.titulo, Romano.latin(t.titulo)).first, style = MaterialTheme.typography.labelLarge,
                        color = dc.acentoTarjeta(tipo, osc), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(t.cifra, style = estiloCifra(), maxLines = 1)
                    Text(t.detalle, style = MaterialTheme.typography.labelSmall, color = dc.rotuloTarjeta(tipo, osc), maxLines = 2)
                }
            }
            if (fila.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

/** El ánimo medio de esos días, con la definición única de las vistas ([io.github.ruddy2003.vestigium.ciencia.Periodo.animoDia]). */
private fun animoEn(dias: List<String>): Double? = io.github.ruddy2003.vestigium.ciencia.Periodo.animoMedio(dias)

/**
 * Lo de «Tu vida vista desde lejos» (9 nov 2026, antes en Vida; ahora arriba de Análisis): la semana, la cinta, el mes,
 * el año, lo acumulado, y el gasto y el ingreso del mes.
 */
@Composable
fun VidaDesdeLejos(nav: Nav) {
    val hoy = Fechas.diaLogico()
    val lunes = Fechas.lunesDe(hoy)
    val transcurridos = (java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(lunes), Fechas.parse(hoy)) + 1).toInt()
    val semana = (0 until transcurridos).map { Fechas.sumar(lunes, it.toLong()) }
    val conAvisos = io.github.ruddy2003.vestigium.ciencia.Periodo.animoDias(semana).any { it?.deAvisos == true }
    val a = animoEn(semana); val b = animoEn(semana.map { Fechas.sumar(it, -7) })
    val flecha = if (a != null && b != null) (if (a - b > 0.15) " ↑" else if (b - a > 0.15) " ↓" else " =") else ""
    val mes = hoy.take(7)
    val diasMes = Repo.dias.keys.count { it.startsWith(mes) && io.github.ruddy2003.vestigium.ciencia.Periodo.conDiario(it) }
    val anio = hoy.take(4)
    val gasto = Repo.gastos.filter { it.dia.startsWith(mes) }.sumOf { it.monto }
    val ingreso = Repo.ingresos.filter { it.dia.startsWith(mes) }.sumOf { it.monto }
    val ir: (String) -> Unit = { if (it == "cinta") Seleccion.cinta = null; nav.ir(it) }
    RejillaTarjetas(listOf(
        Tarjetita("Resumen semanal", a?.let { "%.1f".format(it) + flecha } ?: "—", "ánimo de esta semana" + (if (conAvisos) " (con avisos)" else ""), "semana"),
        Tarjetita("La cinta", "${Repo.momentos.count { it.fecha == hoy && !it.privado } + Repo.tramos.count { it.fecha == hoy } + Repo.consumos.count { it.fecha == hoy && Sustancias.visible(it) }}",
            "cosas con hora hoy", "cinta"),
        Tarjetita("Calendario", "$diasMes / ${hoy.takeLast(2).toInt()}", "días con diario este mes", "calendario"),
        Tarjetita("Tu año", "${Repo.dias.keys.count { it.startsWith(anio) }}", "días registrados en $anio", "anno"),
        Tarjetita("Gasto", dinero(gasto), "gastado este mes", "gastos"),
        Tarjetita("Ingreso", dinero(ingreso), "entró este mes", "gastos"),
        Tarjetita("Totales", "${Repo.dias.size}", "días de datos" + (Repo.dias.keys.minOrNull()?.let { " desde ${Fechas.corto(it)}" } ?: ""), "totales"),
    ), ir)
}

/**
 * Vida (9 nov 2026): las secciones especiales, cada una con su dibujo: tu gente (Social), tu año en historias, los
 * campañas, los logros, los mapas, las obras y las otras vidas. Lo que se mira «desde lejos» (la semana, el mes, el año,
 * el dinero) pasó a Análisis; Sustancias y Enfoque siguen en Más.
 */
@Composable
fun PantallaVida(nav: Nav) {
    Hoja("Vida", subtitulo = "Tu gente, tu año, tus logros, tus mapas, tus obras y tus otras vidas.", buscar = { nav.ir("buscar") }, ajustes = { nav.ir("mas") }) {
        Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RecuadroSocial(nav)
            // Lo de esta semana, arriba (N-20).
            RecuadroCampanas(nav)
            RecuadroTuAnio(nav)
            RecuadroLogros(nav)
            RecuadroMapas(nav)
            RecuadroObras(nav)
            RecuadroOtrasVidas(nav)
        }
    }
}
