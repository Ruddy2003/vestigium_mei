@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Personas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaMenu
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio

/** Lo que dice cada nivel de la escala de cercanía (círculos de Aron: qué tanto se enciman tú y esa persona). */
private val CERCANIA = mapOf(1 to "Casi nada en común", 2 to "Poco", 3 to "Algo", 4 to "Bastante", 5 to "Mucho", 6 to "Muy cercana", 7 to "Casi parte de mí")

/** Los siete pares de círculos: el tuyo y el de la otra persona, cada vez más encimados. */
@Composable
fun CirculosCercania(valor: Int?, onCambio: (Int?) -> Unit) {
    val cs = MaterialTheme.colorScheme
    // Siempre en una fila: cada par de círculos toma la séptima parte del ancho.
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..7) {
            val sel = valor == i
            Surface(onClick = { onCambio(if (sel) null else i) }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small, color = if (sel) cs.primary.copy(alpha = 0.14f) else cs.surface,
                border = BorderStroke(if (sel) 2.dp else 1.dp, if (sel) cs.primary else cs.outline)) {
                Column(Modifier.padding(horizontal = 2.dp, vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Canvas(Modifier.fillMaxWidth().height(20.dp)) {
                        val r = minOf(size.height * 0.42f, size.width / 4.3f)
                        // Separación de los centros: de casi 2 radios (no se tocan) a casi 0 (uno dentro del otro).
                        val d = r * (2.1f - (i - 1) * 0.31f)
                        val cx = size.width / 2; val cy = size.height / 2
                        val t = Stroke(1.6.dp.toPx())
                        drawCircle(if (sel) cs.primary else cs.onSurfaceVariant, r, Offset(cx - d / 2, cy), style = t)
                        drawCircle(if (sel) cs.primary else cs.onSurfaceVariant, r, Offset(cx + d / 2, cy), style = t)
                    }
                    Text("$i", style = MaterialTheme.typography.labelSmall, color = if (sel) cs.primary else cs.onSurfaceVariant)
                }
            }
        }
    }
    Text(valor?.let { "= $it · ${CERCANIA[it]?.lowercase()}" } ?: "qué tan cerca la sientes (opcional)", style = MaterialTheme.typography.labelMedium,
        color = if (valor != null) cs.primary else cs.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
}

/** Relación, cercanía y una nota corta. [alGuardar] recibe lo elegido. */
@Composable
private fun FormularioPersona(nombre: String, alGuardar: () -> Unit, extra: @Composable () -> Unit = {}) {
    val previo = Personas.de(nombre)
    var rel by remember(nombre) { mutableStateOf(previo?.relacion) }
    var cerca by remember(nombre) { mutableStateOf(previo?.cercania) }
    var detalle by remember(nombre) { mutableStateOf(previo?.detalle.orEmpty()) }
    Etiqueta("¿Qué es para ti?")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Personas.RELACIONES.forEach { o -> Ficha(o.nombre, rel == o.id) { rel = if (rel == o.id) null else o.id } }
    }
    Etiqueta("¿Qué tan cerca?")
    CirculosCercania(cerca) { cerca = it }
    OutlinedTextField(detalle, { detalle = it.take(80) }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true,
        placeholder = { Text("detalle opcional: «mi prima», «de la prepa»…") })
    if (previo != null && rel != null && rel != previo.relacion)
        Ayuda("Lo de antes (${Personas.nombreRelacion(previo.relacion).lowercase()}) queda guardado hasta hoy: los días anteriores se siguen analizando así.", Modifier.padding(top = 6.dp))
    FlowRow(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        extra()
        BotonPrincipal("Guardar", habilitado = rel != null) { rel?.let { Personas.guardar(nombre, it, cerca, detalle.trim()); alGuardar() } }
    }
}

/** La tarjeta de «¿Quién es @x?»: una persona a la vez, la más mencionada primero. */
@Composable
fun TarjetaQuienEs(modifier: Modifier = Modifier, compacta: Boolean = false, abrirTodo: (() -> Unit)? = null) {
    val c = Repo.config
    val pend = remember(Repo.dias, Repo.momentos, Repo.tramos, c.personas, c.personas_omitidas, c.equivalencias) { Personas.pendientes() }
    val (nombre, n) = pend.firstOrNull() ?: return
    var abierta by remember(nombre) { mutableStateOf(!compacta) }
    Tarjeta(modifier) {
        Text("¿Quién es @$nombre?", style = MaterialTheme.typography.titleMedium)
        Ayuda("La mencionaste en $n días." + if (pend.size > 1) " Faltan ${pend.size - 1} más." else "")
        if (!abierta) Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { Personas.posponer() }) { Text("después") }
            BotonSecundario("responder") { abierta = true }
        } else FormularioPersona(nombre, alGuardar = {}) {
            TextButton(onClick = { Personas.omitir(nombre) }) { Text("no preguntar") }
            if (compacta) TextButton(onClick = { Personas.posponer() }) { Text("después") }
            if (abrirTodo != null && pend.size > 1) TextButton(onClick = abrirTodo) { Text("todas") }
        }
    }
}

/**
 * Tu gente: quién es cada @persona, qué tan cerca y desde cuándo. Con eso los análisis separan familia, pareja,
 * expareja, amistades y colegas, y cada día usan la relación que había entonces.
 */
@Composable
fun PantallaPersonas(nav: Nav) {
    val c = Repo.config
    val menc = remember(Repo.dias, Repo.momentos, Repo.tramos, Repo.cartas, c.equivalencias) { Personas.menciones() }
    var editando by remember { mutableStateOf<String?>(null) }
    var verTodas by remember { mutableStateOf(false) }
    Hoja("Tu gente", nav.atras, "Quién es cada @persona para ti. Si una relación cambia (pareja → expareja), lo de antes se guarda y los análisis usan lo que había cada día.") {
        TarjetaQuienEs(Modifier.padding(top = 14.dp))
        val porRevisar = remember(Repo.dias) { io.github.ruddy2003.vestigium.data.Presencias.porRevisar().size }
        if (porRevisar > 0) Tarjeta(Modifier.padding(top = 10.dp)) {
            FilaMenu("¿Estuviste o solo pensaste?", "$porRevisar días con personas por revisar: ver a alguien no es lo mismo que pensar en esa persona") { nav.ir("presencias") }
        }
        val clasif = c.personas.sortedByDescending { menc[it.nombre]?.first ?: 0 }
        Seccion("Clasificadas", "${clasif.size} personas. Toca una para cambiarla.") {
            if (clasif.isEmpty()) Vacio("Todavía nadie. Responde arriba o toca a alguien de la lista de abajo.")
            Personas.RELACIONES.forEach { r ->
                val l = clasif.filter { it.relacion == r.id }
                if (l.isNotEmpty()) {
                    Text(r.nombre, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 10.dp))
                    l.forEach { p ->
                        val m = menc[p.nombre]
                        FilaMenu("@${p.nombre}" + if (p.detalle.isNotBlank()) " · ${p.detalle}" else "",
                            listOfNotNull(p.cercania?.let { "cercanía $it/7" }, m?.let { "${it.first} días" }, m?.second?.takeIf { it.isNotBlank() }?.let { "última vez ${Fechas.corto(it)}" },
                                p.historial.lastOrNull()?.let { "antes ${Personas.nombreRelacion(it.relacion).lowercase()}" }).joinToString(" · ")) { editando = p.nombre }
                    }
                }
            }
            // Las de «Conocerte» (cercanas y con quién vives) ya cuentan; aparecen aquí para poder detallarlas.
            val conocer = (c.perfil.cercanas.map { it.nombre } + c.perfil.convivientes).map { it.lowercase().removePrefix("@") }.distinct()
                .filter { n -> Personas.de(n) == null }
            if (conocer.isNotEmpty()) {
                Text("De «Conocerte»", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 10.dp))
                conocer.forEach { n -> FilaMenu("@$n", "ya cuenta en los análisis · toca para detallar") { editando = n } }
            }
        }
        val dups = remember(menc, c.personas_distintas) { Personas.duplicados() }
        if (dups.isNotEmpty()) Seccion("¿Son la misma persona?", "Nombres que se parecen. Si son la misma, se cuentan juntos en todo (conservas lo que escribiste).") {
            dups.take(6).forEach { (a, b) ->
                Tarjeta(Modifier.padding(bottom = 8.dp)) {
                    Text("@$a  y  @$b", style = MaterialTheme.typography.titleSmall)
                    FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Ficha("misma: @$a", false) { Personas.unir(b, a) }
                        Ficha("misma: @$b", false) { Personas.unir(a, b) }
                        Ficha("son distintas", false) { Personas.distintas(a, b) }
                    }
                }
            }
        }
        val ya = c.personas.map { it.nombre }.toSet()
        val resto = menc.entries.filter { it.key !in ya }.sortedByDescending { it.value.first }
        if (resto.isNotEmpty()) Seccion("Sin clasificar", "Todas las que has mencionado, de la más a la menos frecuente.") {
            (if (verTodas) resto else resto.take(15)).forEach { (n, v) ->
                FilaMenu("@$n", "${v.first} días · última vez ${Fechas.corto(v.second)}" + if (n in c.personas_omitidas) " · no preguntar" else "") { editando = n }
            }
            if (!verTodas && resto.size > 15) BotonSecundario("ver las ${resto.size}", Modifier.padding(top = 8.dp)) { verTodas = true }
        }
    }
    editando?.let { n ->
        AlertDialog(onDismissRequest = { editando = null }, confirmButton = {}, dismissButton = { TextButton(onClick = { editando = null }) { Text("cerrar") } },
            title = { Text("@$n") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Personas.de(n)?.historial?.takeIf { it.isNotEmpty() }?.let { h ->
                        Ayuda("Antes: " + h.sortedBy { it.hasta }.joinToString(" · ") { "${Personas.nombreRelacion(it.relacion).lowercase()} hasta ${Fechas.corto(it.hasta)}" })
                    }
                    FormularioPersona(n, alGuardar = { editando = null })
                }
            })
    }
}
