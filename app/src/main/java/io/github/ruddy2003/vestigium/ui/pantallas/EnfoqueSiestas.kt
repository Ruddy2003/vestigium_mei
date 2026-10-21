package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Enfoque
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Deshacer
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.Vacio

fun horasMin(min: Int): String = if (min >= 60) "${min / 60} h ${"%02d".format(min % 60)}" else "$min min"

/** Enfoque y siestas: lo medido con el cronómetro, por tipo y por día, con lo que está corriendo ahora. */
@Composable
fun PantallaEnfoque(nav: Nav) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    val corriendo = Enfoque.enCurso()
    val ahora by produceState(System.currentTimeMillis(), corriendo) { while (corriendo != null) { kotlinx.coroutines.delay(30_000); value = System.currentTimeMillis() } }
    fun abrir() = ctx.startActivity(android.content.Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_ENFOQUE).putExtra(QuickActivity.ORIGEN, "app"))
    Hoja("Enfoque y siestas", nav.atras, "Lo que mides con el cronómetro: estudio, lectura, idiomas, tesis, trabajo, algo creativo y siestas. También desde el widget o el botón del panel rápido.") {
        if (corriendo != null) Tarjeta(Modifier.padding(top = 12.dp)) {
            @Suppress("UNUSED_VARIABLE") val tic = ahora
            val p = Enfoque.plan()
            Text("Corriendo: ${Catalogo.nombreTramo(corriendo.first).lowercase()} · ${horasMin(Enfoque.minutosCorriendo())} de enfoque" +
                (p?.let { if (Enfoque.bloques(it) > 1) " · bloque ${Enfoque.numeroBloque(it)} de ${Enfoque.bloques(it)}" else "" } ?: ""),
                style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                BotonPrincipal("Ver el reloj") { abrir() }
                TextButton(onClick = { Enfoque.terminar(ctx) }) { Text("terminar") }
            }
        } else BotonPrincipal("Empezar o anotar", Modifier.fillMaxWidth().padding(top = 12.dp)) { abrir() }
        val semana = (0 until 7).map { Fechas.sumar(hoy, -it.toLong()) }.toSet()
        val mes = (0 until 30).map { Fechas.sumar(hoy, -it.toLong()) }.toSet()
        val tr = Repo.tramos
        fun suma(dias: Set<String>, siesta: Boolean) = tr.filter { it.fecha in dias && (it.tipo == "siesta") == siesta }.sumOf { it.min }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra(horasMin(suma(setOf(hoy), false)), "enfoque hoy", Modifier.weight(1f))
            Cifra(horasMin(suma(semana, false)), "en 7 días", Modifier.weight(1f))
            Cifra(horasMin(suma(semana, true)), "de siesta en 7 días", Modifier.weight(1f))
        }
        if (tr.isEmpty()) { Vacio("Aún no mides nada. Toca «Empezar» antes de estudiar, leer o dormir una siesta."); return@Hoja }
        Seccion("Últimos 30 días, por tipo") {
            Tarjeta {
                val porTipo = tr.filter { it.fecha in mes }.groupBy { it.tipo }.mapValues { (_, l) -> l.sumOf { it.min } }.entries.sortedByDescending { it.value }
                val max = (porTipo.maxOfOrNull { it.value } ?: 1).coerceAtLeast(1)
                val cs = MaterialTheme.colorScheme
                if (porTipo.isEmpty()) Ayuda("Nada en los últimos 30 días.")
                porTipo.forEach { (t, m) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(Catalogo.nombreTramo(t), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.3f))
                        Canvas(Modifier.weight(0.5f).aspectRatio(12f)) {
                            drawRoundRect(cs.surfaceVariant, Offset.Zero, size, CornerRadius(4f, 4f))
                            drawRoundRect(if (t == "siesta") cs.secondary else cs.primary, Offset.Zero, Size(size.width * m / max, size.height), CornerRadius(4f, 4f))
                        }
                        Text(horasMin(m), style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.2f).padding(start = 8.dp))
                    }
                }
            }
        }
        Seccion("Registro", "Lo más reciente primero. Puedes borrar lo que fue un error.") {
            Tarjeta {
                tr.sortedByDescending { it.inicio }.take(40).forEachIndexed { i, t ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${Catalogo.nombreTramo(t.tipo)} · ${horasMin(t.min)}", style = MaterialTheme.typography.bodyLarge)
                            Text("${Fechas.corto(t.fecha)} · ${Fechas.horaDe(t.inicio)}" + if (t.origen == "manual") " · anotado después" else "", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (t.nota.isNotBlank()) TextoMarcado(t.nota, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { Repo.borrarTramo(t.id); Deshacer.ofrecer("Tramo borrado") { Repo.guardarTramo(t) } }) {
                            Text("quitar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        Ayuda("En Entender puedes cruzar los minutos de enfoque y las siestas con tu sueño, ánimo y pantalla.", Modifier.padding(top = 8.dp))
    }
}
