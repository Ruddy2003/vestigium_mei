@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.Separador
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import androidx.compose.foundation.Canvas
import io.github.ruddy2003.vestigium.ui.aMillis
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.Deshacer
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.Dialogo

fun horasMin(min: Int): String = if (min >= 60) "${min / 60} h ${"%02d".format(min % 60)}" else "$min min"

/** Enfoque y siestas: lo medido con el cronómetro, por tipo y por día, con lo que está corriendo ahora. */
@Composable
fun PantallaEnfoque(nav: Nav) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    val corriendo = Enfoque.enCurso()
    var abierto by remember { mutableStateOf<io.github.ruddy2003.vestigium.data.Tramo?>(null) }
    val ahora by produceState(System.currentTimeMillis(), corriendo) { while (corriendo != null) { kotlinx.coroutines.delay(30_000); value = System.currentTimeMillis() } }
    fun abrir() = ctx.startActivity(android.content.Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_ENFOQUE).putExtra(QuickActivity.ORIGEN, "app"))
    Hoja("Enfoque y siestas", nav.atras, "Lo que mides con el cronómetro: estudio, lectura, idiomas, tesis, trabajo, algo creativo y siestas. También desde el widget o el botón del panel rápido.") {
        if (corriendo != null) Tarjeta(Modifier.padding(top = 12.dp)) {
            @Suppress("UNUSED_VARIABLE") val tic = ahora
            val p = Enfoque.plan()
            // Pasada la meta sigue contando: lo de más («+12 min sobre la meta»).
            val deMas = p?.takeIf { Enfoque.pasadoDeMeta(it) }?.let { (Enfoque.extra(it) / 60_000L).toInt() }
            Text("Corriendo: ${Catalogo.nombreTramo(corriendo.first).lowercase()} · ${horasMin(Enfoque.minutosCorriendo())} de enfoque" +
                (p?.let { if (Enfoque.bloques(it) > 1) " · bloque ${Enfoque.numeroBloque(it)} de ${Enfoque.bloques(it)}" else "" } ?: "") +
                (deMas?.let { " · +${horasMin(it)} " + if (p != null && Enfoque.enDescanso(p)) "de más en el descanso" else "sobre la meta" } ?: ""),
                style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                BotonPrincipal("Ver el reloj") { abrir() }
                BotonTexto(onClick = { Enfoque.terminar(ctx) }) { Text("terminar") }
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
                val eg = estiloGrafica()
                if (porTipo.isEmpty()) Ayuda("Nada en los últimos 30 días.")
                porTipo.forEach { (t, m) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(Catalogo.nombreTramo(t), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.3f))
                        Canvas(Modifier.weight(0.5f).aspectRatio(12f)) {
                            barraGrafica(eg, Offset.Zero, size, cs.surfaceVariant, horizontal = true, pista = true)
                            barraGrafica(eg, Offset.Zero, Size(size.width * m / max, size.height), if (t == "siesta") cs.secondary else cs.primary, horizontal = true)
                        }
                        Text(horasMin(m), style = estiloCifras(MaterialTheme.typography.labelMedium), modifier = Modifier.weight(0.2f).padding(start = 8.dp))
                    }
                }
            }
        }
        Seccion("Registro", "Lo más reciente primero. Puedes borrar lo que fue un error.") {
            Tarjeta {
                tr.sortedByDescending { it.inicio }.take(40).forEachIndexed { i, t ->
                    if (i > 0) Separador()
                    // Tocar un tramo lo abre para corregir el tipo, la hora, la duración o la nota, o para borrarlo.
                    Row(Modifier.fillMaxWidth().clickable { abierto = t }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${Catalogo.nombreTramo(t)} · ${horasMin(t.min)}", style = MaterialTheme.typography.bodyLarge)
                            Text("${Fechas.corto(t.fecha)} · ${Fechas.horaDe(t.inicio)}" + if (t.origen == "manual") " · anotado después" else "", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (t.nota.isNotBlank()) TextoMarcado(t.nota, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("corregir ›", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Ayuda("En Entender puedes cruzar los minutos de enfoque y las siestas con tu sueño, ánimo y pantalla.", Modifier.padding(top = 8.dp))
    }
    abierto?.let { t -> DialogoTramo(t) { abierto = null } }
}

/**
 * Corregir un tramo de enfoque o de siesta: tipo, cuándo empezó, cuánto duró y la nota. El día se recalcula con la hora
 * nueva (día lógico). Borrar pide confirmación y se puede deshacer.
 */
@Composable
fun DialogoTramo(t: io.github.ruddy2003.vestigium.data.Tramo, cerrar: () -> Unit) {
    var tipo by remember(t.id) { mutableStateOf(t.tipo) }
    var cuando by remember(t.id) { mutableStateOf<java.time.LocalDateTime?>(io.github.ruddy2003.vestigium.ui.horaLocal(Fechas.millis(t.inicio))) }
    var min by remember(t.id) { mutableStateOf<Int?>(t.min) }
    var nota by remember(t.id) { mutableStateOf(t.nota) }
    var otro by remember(t.id) { mutableStateOf(t.tipo_otro) }
    var confirmar by remember(t.id) { mutableStateOf(false) }
    Dialogo(
        onDismissRequest = cerrar,
        title = { Text("${Catalogo.nombreTramo(t)} · ${Fechas.corto(t.fecha)} ${Fechas.horaDe(t.inicio)}") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                io.github.ruddy2003.vestigium.ui.Etiqueta("Qué fue")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (Catalogo.ENFOQUE + Catalogo.SIESTA).forEach { o -> io.github.ruddy2003.vestigium.ui.Ficha(o.nombre, tipo == o.id) { tipo = o.id } }
                }
                if (tipo == "otro") io.github.ruddy2003.vestigium.ui.comun.CampoTexto(otro, { otro = it.take(40) }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true,
                    label = { Text("¿En qué?") })
                io.github.ruddy2003.vestigium.ui.Etiqueta("Empezó")
                io.github.ruddy2003.vestigium.ui.SelectorHora(cuando) { cuando = it }
                io.github.ruddy2003.vestigium.ui.Etiqueta("Duró")
                io.github.ruddy2003.vestigium.ui.SelectorDuracion(min, listOf(5, 10, 15, 30, 45, 60, 90, 120, 180, 240)) { min = it }
                io.github.ruddy2003.vestigium.ui.comun.CampoTexto(nota, { nota = it }, Modifier.fillMaxWidth().padding(top = 8.dp), placeholder = { Text("Qué hiciste (opcional; @persona y #tema)") },
                    visualTransformation = io.github.ruddy2003.vestigium.ui.rememberMarcas())
                BotonTexto(onClick = {
                    if (confirmar) { Repo.borrarTramo(t.id); cerrar(); Deshacer.ofrecer("Tramo borrado") { Repo.guardarTramo(t) } } else confirmar = true
                }, modifier = Modifier.padding(top = 6.dp)) { Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            BotonTexto(enabled = (min ?: 0) > 0, onClick = {
                val ini = (cuando ?: io.github.ruddy2003.vestigium.ui.horaLocal(Fechas.millis(t.inicio))).aMillis()
                val m = min ?: t.min
                val n = t.copy(tipo = tipo, inicio = Fechas.isoDeMillis(ini), fin = Fechas.isoDeMillis(ini + m * 60_000L), min = m, fecha = Fechas.diaLogicoDeMillis(ini), nota = nota.trim(),
                    tipo_otro = if (tipo == "otro") otro.trim() else "")
                if (n != t) Repo.guardarTramo(n)
                cerrar()
            }) { Text("Guardar") }
        },
        dismissButton = { BotonTexto(onClick = cerrar) { Text("cancelar") } },
    )
}
