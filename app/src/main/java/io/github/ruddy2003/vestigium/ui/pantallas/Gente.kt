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
import io.github.ruddy2003.vestigium.data.Especiales
import io.github.ruddy2003.vestigium.data.FechaPropia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Persona
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
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.Dialogo

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
                    Text("$i", style = estiloCifras(), color = if (sel) cs.primary else cs.onSurfaceVariant)
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
    var nac by remember(nombre) { mutableStateOf(Especiales.paraEditar(previo?.nacimiento.orEmpty())) }
    var fall by remember(nombre) { mutableStateOf(Especiales.paraEditar(previo?.fallecimiento.orEmpty())) }
    val nacOk = Especiales.leer(nac); val fallOk = Especiales.leer(fall)
    // Cuándo cambió de verdad la relación (H-6-3): por omisión hoy; no antes del cambio anterior ni después de hoy.
    val hoy = Fechas.diaLogico()
    val minimo = previo?.historial?.maxOfOrNull { it.hasta }
    var desde by remember(nombre) { mutableStateOf(hoy) }
    val ctx = androidx.compose.ui.platform.LocalContext.current
    Etiqueta("¿Qué es para ti?")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Personas.RELACIONES.forEach { o -> Ficha(o.nombre, rel == o.id) { rel = if (rel == o.id) null else o.id } }
    }
    Etiqueta("¿Qué tan cerca?")
    CirculosCercania(cerca) { cerca = it }
    OutlinedTextField(detalle, { detalle = it.take(80) }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true,
        placeholder = { Text("detalle opcional: «mi prima», «de la prepa»…") })
    // Fechas (opcionales): con el año de nacimiento se ve su edad; los dos días cuentan como días especiales.
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(nac, { nac = it.take(10) }, Modifier.weight(1f), singleLine = true, isError = nacOk == null,
            label = { Text("nacimiento") }, placeholder = { Text("15/03/1962 o 15/03") })
        OutlinedTextField(fall, { fall = it.take(10) }, Modifier.weight(1f), singleLine = true, isError = fallOk == null,
            label = { Text("fallecimiento") }, placeholder = { Text("si aplica") })
    }
    if (nacOk == null || fallOk == null) Ayuda("Escribe día/mes o día/mes/año, por ejemplo 15/03 o 15/03/1962.", Modifier.padding(top = 4.dp))
    else nacOk.takeIf { it.isNotBlank() }?.let { n ->
        val e = Especiales.edadDe(Persona(nombre, "", nacimiento = n, fallecimiento = fallOk))
        Ayuda("Cumple el ${Especiales.texto(n, conAnio = false)}" + (e?.let { if (fallOk.isBlank()) " · tiene $it años" else " · tenía $it años" } ?: ""), Modifier.padding(top = 4.dp))
    }
    if (previo != null && rel != null && (rel != previo.relacion || cerca != previo.cercania)) {
        val cuando = if (desde == hoy) "hoy" else "el ${Fechas.corto(desde)} ${desde.take(4)}"
        Ayuda("Lo de antes (${Personas.nombreRelacion(previo.relacion).lowercase()}${previo.cercania?.let { ", cercanía $it" } ?: ""}) queda guardado hasta $cuando: los días anteriores se siguen analizando así.", Modifier.padding(top = 6.dp))
        TextButton(onClick = {
            io.github.ruddy2003.vestigium.ui.elegirFecha(ctx, desde) { f -> desde = f.coerceAtMost(hoy).let { d -> if (minimo != null && d <= minimo) Fechas.sumar(minimo, 1).coerceAtMost(hoy) else d } }
        }) { Text(if (desde == hoy) "cambió antes de hoy…" else "cambió el ${Fechas.corto(desde)} ${desde.take(4)} · cambiar", style = MaterialTheme.typography.labelLarge) }
    }
    FlowRow(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        extra()
        BotonPrincipal("Guardar", habilitado = rel != null && nacOk != null && fallOk != null) {
            rel?.let { Personas.guardar(nombre, it, cerca, detalle.trim(), hoy = if (previo != null) desde else hoy, nacimiento = nacOk, fallecimiento = fallOk); alGuardar() }
        }
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
        val porRevisar = remember(Repo.dias) { io.github.ruddy2003.vestigium.data.Presencias.porRevisar().size + io.github.ruddy2003.vestigium.data.Presencias.paraReabrir().size }
        if (porRevisar > 0) Tarjeta(Modifier.padding(top = 10.dp)) {
            FilaMenu("¿Estuviste o solo pensaste?", "$porRevisar días con personas por revisar: ver a alguien no es lo mismo que pensar en esa persona") { nav.ir("presencias") }
        }
        val clasif = c.personas.sortedByDescending { menc[it.nombre]?.first ?: 0 }
        // Alguien que aún no mencionas (N-25): nombre, y después relación y nacimiento en su ficha.
        var nueva by remember { mutableStateOf<String?>(null) }
        TextButton(onClick = { nueva = "" }, modifier = Modifier.padding(top = 8.dp)) { Text("+ persona", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
        nueva?.let { t ->
            Dialogo(onDismissRequest = { nueva = null }, title = { Text("Nueva persona") },
                text = {
                    Column {
                        androidx.compose.material3.OutlinedTextField(value = t, onValueChange = { nueva = it }, singleLine = true, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nombre, como la escribirías con @") })
                        Ayuda("Después eliges su relación, su cercanía y su nacimiento. No tendrá días hasta que la menciones.", Modifier.padding(top = 6.dp))
                    }
                },
                confirmButton = { TextButton(enabled = io.github.ruddy2003.vestigium.data.Texto.etiqueta(t).isNotEmpty(), onClick = {
                    editando = io.github.ruddy2003.vestigium.data.Texto.etiqueta(t); nueva = null
                }) { Text("Seguir") } },
                dismissButton = { TextButton(onClick = { nueva = null }) { Text("Cancelar") } })
        }
        Seccion("Clasificadas", "${clasif.size} personas. Toca una para cambiarla.") {
            if (clasif.isEmpty()) Vacio("Todavía nadie. Responde arriba o toca a alguien de la lista de abajo.")
            Personas.RELACIONES.forEach { r ->
                val l = clasif.filter { it.relacion == r.id }
                if (l.isNotEmpty()) {
                    Text(r.nombre, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 10.dp))
                    l.forEach { p ->
                        val m = menc[p.nombre]
                        FilaMenu("@${p.nombre}" + if (p.detalle.isNotBlank()) " · ${p.detalle}" else "",
                            listOfNotNull(Especiales.edadDe(p)?.let { if (p.fallecimiento.isBlank()) "$it años" else "†, a los $it" } ?: "†".takeIf { p.fallecimiento.isNotBlank() },
                                p.cercania?.let { "cercanía $it/7" }, m?.let { "${it.first} días" }, m?.second?.takeIf { it.isNotBlank() }?.let { "última vez ${Fechas.corto(it)}" },
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
        DiasEspeciales()
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
        Dialogo(onDismissRequest = { editando = null }, confirmButton = {}, dismissButton = { TextButton(onClick = { editando = null }) { Text("cerrar") } },
            title = { Text("@$n") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Personas.de(n)?.historial?.takeIf { it.isNotEmpty() }?.let { h ->
                        Ayuda("Antes: " + h.sortedBy { it.hasta }.joinToString(" · ") { "${Personas.nombreRelacion(it.relacion).lowercase()} hasta ${Fechas.corto(it.hasta)}" })
                    }
                    FormularioPersona(n, alGuardar = { editando = null })
                    AQuienConoce(n)
                }
            })
    }
}

/**
 * Días especiales (I-6): los cumpleaños y aniversarios de Tu gente, el tuyo (de Conocerte) y tus fechas propias,
 * en el orden en que vienen. Las fechas propias se agregan y se editan aquí.
 */
@Composable
private fun DiasEspeciales() {
    val c = Repo.config
    val hoy = Fechas.diaLogico()
    var editando by remember { mutableStateOf<FechaPropia?>(null) }
    // Los próximos 12 meses, día por día.
    val proximos = remember(c.personas, c.fechas_propias, c.perfil.nacimiento, hoy) {
        (0L until 366L).flatMap { i -> val f = Fechas.sumar(hoy, i); Especiales.de(f, c).map { f to it } }
    }
    Seccion("Días especiales", "Cumpleaños, aniversarios y tus propias fechas. Cuentan como «día especial» en la exportación para R.") {
        Tarjeta {
            if (proximos.isEmpty()) Ayuda("Todavía no hay fechas. Pon la de nacimiento de alguien al tocarlo, o agrega una fecha propia.")
            proximos.take(8).forEach { (f, e) ->
                Text((if (f == hoy) "Hoy" else Fechas.corto(f)) + " · " + Especiales.frase(e), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 2.dp))
            }
            if (c.fechas_propias.isNotEmpty()) {
                Etiqueta("Tus fechas propias")
                c.fechas_propias.sortedBy { Especiales.mesDia(it.fecha) }.forEach { fp ->
                    FilaMenu(fp.nombre, Especiales.texto(fp.fecha) + " · " + (Especiales.TIPOS_PROPIOS.firstOrNull { it.id == fp.tipo }?.nombre ?: fp.tipo).lowercase()) { editando = fp }
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                BotonSecundario("+ fecha propia") { editando = FechaPropia(Repo.nuevoId(), "", "") }
                Ficha(if (c.avisos_especiales) "aviso en la mañana: sí" else "aviso en la mañana: no", c.avisos_especiales) {
                    Repo.cambiarConfig { it.copy(avisos_especiales = !it.avisos_especiales) }
                }
            }
        }
    }
    editando?.let { fp0 ->
        var nombre by remember(fp0.id) { mutableStateOf(fp0.nombre) }
        var fecha by remember(fp0.id) { mutableStateOf(Especiales.paraEditar(fp0.fecha)) }
        var tipo by remember(fp0.id) { mutableStateOf(fp0.tipo) }
        val ok = Especiales.leer(fecha)?.takeIf { it.isNotBlank() }
        val existe = c.fechas_propias.any { it.id == fp0.id }
        Dialogo(onDismissRequest = { editando = null },
            confirmButton = {
                TextButton(enabled = ok != null && nombre.isNotBlank(), onClick = {
                    val n = fp0.copy(nombre = nombre.trim(), fecha = ok!!, tipo = tipo)
                    Repo.cambiarConfig { cc -> cc.copy(fechas_propias = cc.fechas_propias.filter { it.id != n.id } + n) }
                    editando = null
                }) { Text("guardar") }
            },
            dismissButton = {
                Row {
                    var confirmar by remember(fp0.id) { mutableStateOf(false) }
                    if (existe) TextButton(onClick = {
                        if (!confirmar) { confirmar = true; return@TextButton }
                        Repo.cambiarConfig { cc -> cc.copy(fechas_propias = cc.fechas_propias.filter { it.id != fp0.id }) }; editando = null
                        io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Fecha borrada") { Repo.cambiarConfig { cc -> if (cc.fechas_propias.any { it.id == fp0.id }) cc else cc.copy(fechas_propias = cc.fechas_propias + fp0) } }
                    }) { Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error) }
                    TextButton(onClick = { editando = null }) { Text("cancelar") }
                }
            },
            title = { Text(if (existe) "Fecha propia" else "Nueva fecha propia") },
            text = {
                Column {
                    OutlinedTextField(nombre, { nombre = it.take(60) }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("qué es") },
                        placeholder = { Text("«boda de mis papás», «llegué a la ciudad»…") })
                    OutlinedTextField(fecha, { fecha = it.take(10) }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, isError = fecha.isNotBlank() && ok == null,
                        label = { Text("fecha") }, placeholder = { Text("15/03/1990 o 15/03") })
                    FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Especiales.TIPOS_PROPIOS.forEach { o -> Ficha(o.nombre, tipo == o.id) { tipo = o.id } }
                    }
                }
            })
    }
}
