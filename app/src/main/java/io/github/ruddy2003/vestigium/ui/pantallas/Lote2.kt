@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.ciencia.Variable
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Enfoque
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Lugares
import io.github.ruddy2003.vestigium.data.Meta
import io.github.ruddy2003.vestigium.data.Metas
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Nota
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.esCierre
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.CuadriculaAfecto
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.elegirFecha
import io.github.ruddy2003.vestigium.ui.rememberMarcas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.ruddy2003.vestigium.ui.Deslizador
import io.github.ruddy2003.vestigium.ui.Dialogo

// =====================================================================
//  Cobertura por variable
// =====================================================================

/** Cuántos días tiene una variable, desde cuándo y qué proporción de los días desde entonces. */
class FilaCobertura(val v: Variable, val n: Int, val total: Int, val primera: String?) {
    val pct: Double get() = if (total <= 0) 0.0 else n.toDouble() / total
    val faltan: Int get() = (30 - n).coerceAtLeast(0)
}

object CoberturaVariables {
    fun calcular(hoy: String = Fechas.diaLogico()): List<FilaCobertura> {
        val panel = Estadistica.panel(dinamicas = false)
        return Estadistica.variables(dinamicas = false).map { v ->
            val dias = panel.filter { it.value[v.id] != null }.keys
            val p0 = dias.minOrNull()
            FilaCobertura(v, dias.size, p0?.let { (Fechas.diasEntre(it, hoy) + 1).toInt() } ?: 0, p0)
        }
    }
}

@Composable
fun PantallaCoberturaVariables(nav: Nav) {
    val sinc = io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono
    val filas by produceState<List<FilaCobertura>?>(if (sinc) runCatching { CoberturaVariables.calcular() }.getOrDefault(emptyList()) else null) {
        if (!sinc) value = withContext(Dispatchers.Default) { runCatching { CoberturaVariables.calcular() }.getOrDefault(emptyList()) }
    }
    var grupo by remember { mutableStateOf("todas") }
    var orden by remember { mutableStateOf("menos") }
    val cs = MaterialTheme.colorScheme
    Hoja("Cobertura de variables", nav.atras, "Cuántos días tiene cada variable desde su primer dato. Con menos de 30 días, una correlación todavía es pista, no evidencia.") {
        val l = filas
        if (l == null) { Ayuda("Contando…", Modifier.padding(top = 12.dp)); return@Hoja }
        val conDatos = l.filter { it.n > 0 }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("${conDatos.count { it.n >= 30 }}", "con 30 días o más", Modifier.weight(1f))
            Cifra("${conDatos.count { it.n < 30 }}", "todavía cortas", Modifier.weight(1f))
            Cifra("${l.count { it.n == 0 }}", "sin datos", Modifier.weight(1f))
        }
        val grupos = listOf("todas") + conDatos.map { it.v.grupo }.distinct().sorted()
        FlowRow(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            grupos.forEach { g -> Ficha(g, grupo == g) { grupo = g } }
        }
        FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("menos días primero", orden == "menos") { orden = "menos" }
            Ficha("más días primero", orden == "mas") { orden = "mas" }
            Ficha("por nombre", orden == "nombre") { orden = "nombre" }
        }
        val visibles = conDatos.filter { grupo == "todas" || it.v.grupo == grupo }.let { x ->
            when (orden) { "mas" -> x.sortedByDescending { it.n }; "nombre" -> x.sortedBy { it.v.nombre }; else -> x.sortedBy { it.n } }
        }
        Tarjeta(Modifier.padding(top = 10.dp)) {
            visibles.forEachIndexed { i, f ->
                if (i > 0) HorizontalDivider(Modifier.padding(vertical = 6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(f.v.nombre, style = MaterialTheme.typography.bodyMedium)
                        Text("${f.n} ${if (f.n == 1) "día" else "días"} · ${Math.round(f.pct * 100)}% desde el ${f.primera?.let { Fechas.corto(it) } ?: "?"}" + if (f.faltan > 0) " · faltan ${f.faltan} para 30" else "",
                            style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    }
                }
                Box(Modifier.fillMaxWidth().padding(top = 4.dp).height(5.dp).clip(RoundedCornerShape(3.dp)).background(cs.surfaceVariant)) {
                    Box(Modifier.fillMaxWidth((f.n / 30f).coerceIn(0f, 1f)).fillMaxHeight().background(if (f.n >= 30) cs.primary else cs.tertiary))
                }
            }
        }
        if (l.any { it.n == 0 }) Seccion("Sin datos todavía", plegable = true, plegadaPorDefecto = true) {
            Ayuda(l.filter { it.n == 0 }.joinToString(" · ") { it.v.nombre })
        }
    }
}

// =====================================================================
//  ¿Es un lugar especial?
// =====================================================================

@Composable
fun PantallaLugarNuevo(nav: Nav) {
    val ctx = LocalContext.current
    val c = Repo.config
    val pend = Lugares.pendientes()
    var msg by remember { mutableStateOf<String?>(null) }
    Hoja("Lugares especiales", nav.atras, "Cuando llevas un rato en un sitio nuevo, la app pregunta si es un lugar especial. Así el tiempo en cada tipo de lugar entra en tus análisis.") {
        msg?.let { io.github.ruddy2003.vestigium.ui.Aviso(it) }
        val cand = pend.firstOrNull()
        if (cand == null) Vacio("No hay sitios esperando respuesta. Cuando pases ${c.lugar_minutos} minutos en un lugar nuevo, te preguntará.")
        else androidx.compose.runtime.key(cand.lat, cand.lon) {
            var nombre by remember { mutableStateOf(cand.sugerencia) }
            var tipo by remember { mutableStateOf(Lugares.tipoProbable(cand)) }
            var radio by remember { mutableStateOf(120f) }
            Tarjeta(Modifier.padding(top = 12.dp)) {
                Text("¿Es un lugar especial?", style = MaterialTheme.typography.titleLarge)
                Ayuda("Estuviste aquí de ${Fechas.horaDe(Fechas.isoDeMillis(cand.desde_ms))} a ${Fechas.horaDe(Fechas.isoDeMillis(cand.hasta_ms))} del ${Fechas.corto(Fechas.diaDeMillis(cand.desde_ms))}" +
                    (if (cand.sugerencia.isNotBlank()) ", cerca de ${cand.sugerencia}." else "."))
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 10.dp))
                CamposLugar(nombre, tipo, radio, { nombre = it }, { t -> if (t != null) tipo = t }, { radio = it })
                BotonPrincipal("Guardar", Modifier.fillMaxWidth().padding(top = 8.dp), habilitado = nombre.isNotBlank()) {
                    val n = Lugares.guardar(ctx, cand, nombre, tipo, radio.toInt())
                    msg = "Guardado «${nombre.trim()}»." + if (n > 0) " Encontré $n visita${if (n > 1) "s" else ""} de los últimos 30 días." else ""
                }
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { Lugares.ignorar(cand); msg = "No volveré a preguntar en 150 m a la redonda." }) { Text("no guardar nunca aquí") }
                    TextButton(onClick = { Lugares.despues(cand); msg = "Te pregunto en tres días." ; nav.atras() }) { Text("ahora no") }
                }
            }
            if (pend.size > 1) Ayuda("Hay ${pend.size - 1} sitio(s) más esperando.", Modifier.padding(top = 6.dp))
        }
        Seccion("Cómo pregunta", "Como mucho dos veces al día, nunca de noche ni mientras te mueves. «Ahora no» vuelve a preguntar a los tres días, dos veces como máximo.") {
            Tarjeta {
                Interruptor("Preguntar por lugares nuevos", c.lugares_detectar) { v -> Repo.cambiarConfig { it.copy(lugares_detectar = v) } }
                Etiqueta("Tiempo en el sitio: ${c.lugar_minutos} min")
                Deslizador(value = c.lugar_minutos.toFloat(), onValueChange = { v -> Repo.cambiarConfig { it.copy(lugar_minutos = v.toInt()) } }, valueRange = 15f..60f, steps = 8)
                Etiqueta("Radio del sitio: ${c.lugar_radio_m} m")
                Deslizador(value = c.lugar_radio_m.toFloat(), onValueChange = { v -> Repo.cambiarConfig { it.copy(lugar_radio_m = v.toInt()) } }, valueRange = 40f..200f, steps = 15)
                Ayuda("El sonido del aviso es el del canal «Lugares especiales»; se cambia desde los ajustes de notificaciones de Android.")
            }
        }
        if (c.lugares_ignorados.isNotEmpty()) Seccion("Sitios ignorados", plegable = true, plegadaPorDefecto = true) {
            Tarjeta {
                c.lugares_ignorados.forEachIndexed { i, x ->
                    if (i > 0) HorizontalDivider()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("%.4f, %.4f · desde %s".format(x.lat, x.lon, x.desde.ifBlank { "?" }), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            Repo.cambiarConfig { cc -> cc.copy(lugares_ignorados = cc.lugares_ignorados - x) }
                            io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Sitio quitado de los ignorados") { Repo.cambiarConfig { cc -> if (x in cc.lugares_ignorados) cc else cc.copy(lugares_ignorados = cc.lugares_ignorados + x) } }
                        }) { Text("quitar") }
                    }
                }
            }
        }
        TextButton(onClick = { nav.ir("lugares") }, modifier = Modifier.padding(top = 8.dp)) { Text("ver el mapa emocional de tus lugares ›") }
    }
}

/** Horas por tipo de lugar en los últimos 30 días y la valencia de tus registros en cada tipo. */
@Composable
fun TiposDeLugar(modifier: Modifier = Modifier) {
    val hoy = Fechas.diaLogico()
    val horas = remember(Repo.visitas.size, Repo.lugares.size) { Lugares.horasPorTipo(Fechas.sumar(hoy, -29), hoy) }
    val val0 = remember(Repo.momentos.size, Repo.lugares.size) { Lugares.valenciaPorTipo() }
    if (horas.isEmpty() && val0.isEmpty()) return
    val cs = MaterialTheme.colorScheme
    Tarjeta(modifier) {
        Text("Por tipo de lugar · 30 días", style = MaterialTheme.typography.titleMedium)
        val maxH = horas.values.maxOrNull() ?: 1.0
        (horas.keys + val0.keys).distinct().forEach { t ->
            val h = horas[t] ?: 0.0
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(Catalogo.nombreTipoLugar(t), style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(92.dp))
                Box(Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(cs.surfaceVariant)) {
                    Box(Modifier.fillMaxWidth((h / maxH).toFloat().coerceIn(0f, 1f)).fillMaxHeight().background(cs.primary))
                }
                Text("  %.0f h".format(h) + (val0[t]?.let { (v, n) -> " · %.1f/7 (%d)".format(v, n) } ?: ""), style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            }
        }
        Ayuda("Barra: horas en ese tipo de lugar. Al lado: la valencia media de tus registros de ánimo ahí (y cuántos).", Modifier.padding(top = 6.dp))
    }
}

// =====================================================================
//  Notas
// =====================================================================

@Composable
fun PantallaNotas(nav: Nav) {
    val ctx = LocalContext.current
    var texto by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("idea") }
    var recordar by remember { mutableStateOf<String?>(null) }
    // null = el día lógico de hoy; se puede mover a otro día (N-11).
    var paraDia by remember { mutableStateOf<String?>(Seleccion.notaPara.also { Seleccion.notaPara = null }) }
    var buscar by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("todas") }
    var editando by remember { mutableStateOf<Nota?>(null) }
    val marcas = rememberMarcas()
    Hoja("Notas", nav.atras, "Ideas, pensamientos especiales, propósitos y recordatorios. Con @persona y #tema, fijar arriba y buscar.") {
        Tarjeta(Modifier.padding(top = 10.dp)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Catalogo.TIPOS_NOTA.forEach { o -> Ficha(o.nombre, tipo == o.id) { tipo = o.id } }
            }
            CampoTexto(value = texto, onValueChange = { texto = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), minLines = 3,
                placeholder = { Text("Escribe… usa @persona y #tema") }, visualTransformation = marcas)
            if (tipo == "recordatorio") Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Ficha(recordar?.let { "el ${Fechas.corto(it)}" } ?: "¿qué día?", recordar != null) { elegirFecha(ctx, recordar, desdeManana = false) { recordar = it } }
                Ayuda("  Te aviso ese día a las ${Repo.config.metas_hora}:00.")
            }
            val dia = paraDia ?: Fechas.diaLogico()
            Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Ayuda("Para el ${Fechas.titulo(dia).lowercase()}", Modifier.weight(1f))
                Text("cambiar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { elegirFecha(ctx, dia) { paraDia = it.takeIf { f -> f != Fechas.diaLogico() } } }.padding(6.dp))
            }
            if (paraDia == null) io.github.ruddy2003.vestigium.ui.AvisoMadrugada()
            BotonPrincipal("Guardar nota", Modifier.fillMaxWidth().padding(top = 10.dp), habilitado = texto.isNotBlank()) {
                Repo.guardarNota(Nota(Repo.nuevoId(), Fechas.ahoraIso(), dia, texto.trim(), tipo, recordar_el = recordar.takeIf { tipo == "recordatorio" }))
                texto = ""; recordar = null; paraDia = null
            }
        }
        CampoTexto(value = buscar, onValueChange = { buscar = it }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp), singleLine = true,
            placeholder = { Text("Buscar en tus notas") })
        FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todas", filtro == "todas") { filtro = "todas" }
            Catalogo.TIPOS_NOTA.forEach { o -> Ficha(o.nombre.lowercase(), filtro == o.id) { filtro = o.id } }
        }
        val q = io.github.ruddy2003.vestigium.ciencia.Totales.normal(buscar)
        val lista = Repo.notas.filter { (filtro == "todas" || it.tipo == filtro) && (q.isBlank() || io.github.ruddy2003.vestigium.ciencia.Totales.normal(it.texto).contains(q)) }
            .sortedWith(compareByDescending<Nota> { it.fijada }.thenByDescending { it.creada })
        if (lista.isEmpty()) Vacio(if (Repo.notas.isEmpty()) "Todavía no hay notas." else "Nada con eso.")
        lista.forEach { n -> FilaNota(n, onEditar = { editando = n }) }
    }
    editando?.let { n -> DialogoNota(n) { editando = null } }
}

/** Corregir una nota: texto, tipo, fecha y recordatorio; borrar pide confirmación y deja deshacer (N-11). */
@Composable
fun DialogoNota(n: Nota, cerrar: () -> Unit) {
    val ctx = LocalContext.current
    val marcas = rememberMarcas()
    var t by remember(n.id) { mutableStateOf(n.texto) }
    var tp by remember(n.id) { mutableStateOf(n.tipo) }
    var f by remember(n.id) { mutableStateOf(n.fecha) }
    var rec by remember(n.id) { mutableStateOf(n.recordar_el) }
    var confirmar by remember(n.id) { mutableStateOf(false) }
    Dialogo(onDismissRequest = { cerrar() }, title = { Text("Editar nota") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.TIPOS_NOTA.forEach { o -> Ficha(o.nombre, tp == o.id) { tp = o.id } }
                }
                CampoTexto(value = t, onValueChange = { t = it }, minLines = 4, visualTransformation = marcas, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Ayuda("Del ${Fechas.titulo(f).lowercase()}", Modifier.weight(1f))
                    Text("cambiar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { elegirFecha(ctx, f) { f = it } }.padding(6.dp))
                }
                if (tp == "recordatorio") Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Ficha(rec?.let { "recordar el ${Fechas.corto(it)}" } ?: "¿qué día te aviso?", rec != null) { elegirFecha(ctx, rec) { rec = it } }
                    if (rec != null) Text("quitar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { rec = null }.padding(6.dp))
                }
                // Un recordatorio sin día no avisa nunca: se dice antes de guardar.
                if (tp == "recordatorio" && rec == null) Text("Sin día, este recordatorio no te avisará.", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
                // Una idea o un pensamiento que vale la pena conservar se puede mover al Ideario (con su fecha y hora de creación).
                if (tp == "idea" || tp == "pensamiento") Text("pasar al Ideario →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp).clickable {
                        val i = io.github.ruddy2003.vestigium.data.Ideario.deNota(n.copy(texto = t.trim().ifBlank { n.texto }, fecha = f), Repo.nuevoId())
                        Repo.guardarIdea(i); Repo.borrarNota(n.id); cerrar()
                        io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Nota pasada al Ideario") { Repo.borrarIdea(i.id); Repo.guardarNota(n) }
                    }.padding(vertical = 6.dp))
                TextButton(onClick = {
                    if (!confirmar) { confirmar = true; return@TextButton }
                    Repo.borrarNota(n.id); cerrar(); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Nota borrada") { Repo.guardarNota(n) }
                }, modifier = Modifier.padding(top = 6.dp)) { Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(onClick = {
            Repo.guardarNota(n.copy(texto = t.trim(), tipo = tp, fecha = f, recordar_el = rec.takeIf { tp == "recordatorio" }, editada = Fechas.ahoraIso()))
            cerrar()
        }, enabled = t.isNotBlank()) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = cerrar) { Text("cancelar") } })
}

@Composable
private fun FilaNota(n: Nota, onEditar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(shape = RoundedCornerShape(6.dp), color = if (n.fijada) cs.primaryContainer else cs.surface, border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline),
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { onEditar() }) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text((if (n.fijada) "◆ " else "") + (Catalogo.TIPOS_NOTA.firstOrNull { it.id == n.tipo }?.nombre ?: n.tipo) + " · " + Fechas.corto(n.fecha) +
                    (n.recordar_el?.let { " · recordar el ${Fechas.corto(it)}" } ?: ""), style = MaterialTheme.typography.labelSmall, color = cs.primary, modifier = Modifier.weight(1f))
                if (n.tipo == "recordatorio") Text(if (n.hecha) "✓ hecha" else "marcar hecha", style = MaterialTheme.typography.labelSmall, color = cs.tertiary,
                    modifier = Modifier.clickable { Repo.guardarNota(n.copy(hecha = !n.hecha)) }.padding(horizontal = 6.dp))
                Text(if (n.fijada) "desfijar" else "fijar", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant,
                    modifier = Modifier.clickable { Repo.guardarNota(n.copy(fijada = !n.fijada)) }.padding(horizontal = 4.dp))
            }
            TextoMarcado(n.texto, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodyMedium, color = if (n.hecha) cs.onSurfaceVariant else cs.onSurface)
        }
    }
}

// =====================================================================
//  Metas
// =====================================================================

@Composable
fun PantallaMetas(nav: Nav) {
    var editando by remember { mutableStateOf<Meta?>(null) }
    var nueva by remember { mutableStateOf(false) }
    var verTodas by remember { mutableStateOf(false) }
    val panel by produceState<Map<String, Map<String, Double?>>?>(null, Repo.metas.size) {
        value = if (Repo.metas.any { it.medida_tipo == "variable" }) withContext(Dispatchers.Default) { runCatching { Estadistica.panel(dinamicas = false) }.getOrNull() } else emptyMap()
    }
    val hoy = Fechas.diaLogico()
    Hoja("Metas", nav.atras, "Corto plazo (hasta 3 meses), mediano (hasta un año) y largo. Si la ligas a un hábito o a una variable, el progreso se calcula solo.") {
        // En diciembre (y los primeros días de enero): los propósitos del año.
        Metas.toPlantilla(hoy)?.let { anio -> PlantillaPropositos(anio) }
        val revisar = Metas.porRevisar(hoy)
        if (revisar.isNotEmpty()) Seccion("Toca revisar") {
            revisar.forEach { m -> TarjetaMeta(m, panel, hoy, revisar = true) { editando = m } }
        }
        BotonPrincipal("+ Nueva meta", Modifier.fillMaxWidth().padding(top = 12.dp)) { nueva = true }
        Catalogo.HORIZONTES.forEach { h ->
            val l = Metas.activas().filter { it.horizonte == h.id && it !in revisar }
            if (l.isNotEmpty()) Seccion("${h.nombre} plazo", h.detalle) { l.forEach { m -> TarjetaMeta(m, panel, hoy) { editando = m } } }
        }
        val otras = Repo.metas.filter { it.estado != "activa" }
        if (otras.isNotEmpty()) Seccion("Pausadas, logradas y abandonadas", plegable = true, plegadaPorDefecto = !verTodas) {
            otras.sortedByDescending { it.creada }.forEach { m -> TarjetaMeta(m, panel, hoy) { editando = m } }
        }
        if (Repo.metas.isEmpty()) Vacio("Todavía no hay metas. Empieza con una sola, pequeña y medible.")
        Ayuda("Las revisiones llegan a las ${Repo.config.metas_hora}:00 junto con tus notas de recordatorio.", Modifier.padding(top = 10.dp))
    }
    if (nueva || editando != null) EditorMeta(editando) { nueva = false; editando = null }
}

@Composable
private fun TarjetaMeta(m: Meta, panel: Map<String, Map<String, Double?>>?, hoy: String, revisar: Boolean = false, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val p = Metas.progreso(m, hoy, panel)
    Surface(shape = RoundedCornerShape(6.dp), color = cs.surface, border = androidx.compose.foundation.BorderStroke(1.dp, if (revisar) cs.tertiary else cs.outline),
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { onClick() }) {
        Column(Modifier.padding(12.dp)) {
            Text(m.nombre, style = MaterialTheme.typography.titleMedium)
            Text(listOfNotNull(Catalogo.ESTADOS_META.firstOrNull { it.id == m.estado }?.nombre?.lowercase(), m.fecha_objetivo?.let { "para el ${Fechas.corto(it)}" },
                m.medida_tipo?.let { t -> medidaTexto(m) + (Metas.valor(m, hoy, panel)?.let { v -> " · lleva ${"%.1f".format(v).removeSuffix(".0")}" } ?: "") },
                Metas.proximaRevision(m)?.let { "revisión: ${Fechas.corto(it)}" }).joinToString(" · "), style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            if (p != null) Box(Modifier.fillMaxWidth().padding(top = 6.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(cs.surfaceVariant)) {
                Box(Modifier.fillMaxWidth(p.toFloat()).fillMaxHeight().background(cs.primary))
            }
            if (revisar) Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonSecundario("✓ revisada") { Metas.revisar(m) }
                BotonSecundario("cambiar algo") { onClick() }
            }
        }
    }
}

private fun medidaTexto(m: Meta): String {
    val nombre = when (m.medida_tipo) {
        "habito" -> Repo.config.habitos.firstOrNull { it.id == m.medida_clave }?.nombre ?: m.medida_clave
        else -> Estadistica.variables(dinamicas = false).firstOrNull { it.id == m.medida_clave }?.nombre ?: m.medida_clave
    }
    val modo = when (m.medida_modo) { "suma" -> "suma de"; "media" -> "media de"; else -> "días con" }
    return "$modo ${nombre?.lowercase()}" + (m.objetivo?.let { " (meta ${"%.0f".format(it)})" } ?: "")
}

@Composable
private fun PlantillaPropositos(anio: Int) {
    var elegidos by remember { mutableStateOf(setOf<String>()) }
    var propio by remember { mutableStateOf("") }
    Tarjeta(Modifier.padding(top = 12.dp)) {
        Text("Propósitos de $anio", style = MaterialTheme.typography.titleLarge)
        Ayuda("Elige o escribe los tuyos. Se revisan el 31 de marzo, el 30 de junio, el 30 de septiembre y al cerrar el año.")
        FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            (Metas.IDEAS_PROPOSITOS + elegidos.filter { it !in Metas.IDEAS_PROPOSITOS }).forEach { p -> Ficha(p, p in elegidos) { elegidos = if (p in elegidos) elegidos - p else elegidos + p } }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
            CampoTexto(value = propio, onValueChange = { propio = it }, singleLine = true, modifier = Modifier.weight(1f), placeholder = { Text("Otro propósito") })
            TextButton(onClick = { if (propio.isNotBlank()) { elegidos = elegidos + propio.trim(); propio = "" } }) { Text("+") }
        }
        BotonPrincipal("Guardar ${elegidos.size} propósito${if (elegidos.size == 1) "" else "s"}", Modifier.fillMaxWidth().padding(top = 8.dp), habilitado = elegidos.isNotEmpty()) {
            elegidos.forEach { Repo.guardarMeta(Metas.proposito(it, anio)) }; elegidos = emptySet()
        }
    }
}

@Composable
private fun EditorMeta(previa: Meta?, cerrar: () -> Unit) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    var m by remember(previa?.id) { mutableStateOf(previa ?: Meta(Repo.nuevoId(), "", creada = Fechas.ahoraIso())) }
    var buscarVar by remember { mutableStateOf("") }
    val vars = remember { Estadistica.variables(dinamicas = false).filter { it.grupo != "Privado" } }
    // Con sus decimales (antes «%.0f» redondeaba 7.5 a 8 al volver a abrir).
    var objetivoTexto by remember(previa?.id) { mutableStateOf(previa?.objetivo?.let { io.github.ruddy2003.vestigium.data.Dinero.texto(it) } ?: "") }
    var umbralTexto by remember(previa?.id) { mutableStateOf(previa?.umbral?.takeIf { it > 0 }?.let { io.github.ruddy2003.vestigium.data.Dinero.texto(it) } ?: "") }
    Dialogo(onDismissRequest = cerrar, title = { Text(if (previa == null) "Nueva meta" else "Meta") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                CampoTexto(value = m.nombre, onValueChange = { m = m.copy(nombre = it) }, label = { Text("Qué quieres lograr") }, modifier = Modifier.fillMaxWidth())
                CampoTexto(value = m.detalle, onValueChange = { m = m.copy(detalle = it) }, label = { Text("Detalle (opcional)") }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                Etiqueta("Fecha objetivo")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Ficha(m.fecha_objetivo?.let { Fechas.corto(it) + " " + it.take(4) } ?: "elegir…", m.fecha_objetivo != null) {
                        elegirFecha(ctx, m.fecha_objetivo, desdeManana = true) { f -> m = m.copy(fecha_objetivo = f, horizonte = Metas.horizonteDe(f, hoy)) }
                    }
                }
                Etiqueta("Horizonte")
                // Con fecha objetivo, el horizonte sale de ella; sin fecha, se elige.
                if (m.fecha_objetivo != null) Text(Catalogo.HORIZONTES.firstOrNull { it.id == m.horizonte }?.let { "${it.nombre} (${it.detalle}), según la fecha" } ?: m.horizonte,
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                else FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { Catalogo.HORIZONTES.forEach { h -> Ficha(h.nombre, m.horizonte == h.id) { m = m.copy(horizonte = h.id) } } }
                Etiqueta("Cómo se mide")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("a mano", m.medida_tipo == null) { m = m.copy(medida_tipo = null, medida_clave = null) }
                    if (io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().isNotEmpty()) Ficha("un hábito", m.medida_tipo == "habito") { m = m.copy(medida_tipo = "habito", medida_clave = null, medida_modo = "dias") }
                    Ficha("una variable", m.medida_tipo == "variable") { m = m.copy(medida_tipo = "variable", medida_clave = null) }
                }
                when (m.medida_tipo) {
                    null -> {
                        Etiqueta("Progreso: ${m.progreso_manual ?: 0}%")
                        Deslizador(value = (m.progreso_manual ?: 0).toFloat(), onValueChange = { m = m.copy(progreso_manual = it.toInt()) }, valueRange = 0f..100f, steps = 19)
                    }
                    "habito" -> FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().filter { it.activo }.forEach { h -> Ficha(h.nombre, m.medida_clave == h.id) { m = m.copy(medida_clave = h.id) } }
                    }
                    else -> {
                        CampoTexto(value = buscarVar, onValueChange = { buscarVar = it }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                            placeholder = { Text("Buscar variable (enfoque, pasos, sueño…)") })
                        val q = io.github.ruddy2003.vestigium.ciencia.Totales.normal(buscarVar)
                        val elegida = vars.firstOrNull { it.id == m.medida_clave }
                        elegida?.let { Text("✓ ${it.nombre}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp)) }
                        if (q.isNotBlank()) vars.filter { io.github.ruddy2003.vestigium.ciencia.Totales.normal(it.nombre).contains(q) }.take(8).forEach { v ->
                            Text(v.nombre, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().clickable { m = m.copy(medida_clave = v.id); buscarVar = "" }.padding(vertical = 6.dp))
                        }
                        FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("dias" to "días que cumplen", "suma" to "suma", "media" to "media").forEach { (k, n) -> Ficha(n, m.medida_modo == k) { m = m.copy(medida_modo = k) } }
                        }
                        if (m.medida_modo == "dias") {
                            FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Ficha("al menos", !m.umbral_max) { m = m.copy(umbral_max = false) }
                                Ficha("como mucho", m.umbral_max) { m = m.copy(umbral_max = true) }
                            }
                            CampoTexto(value = umbralTexto, onValueChange = { t -> umbralTexto = io.github.ruddy2003.vestigium.data.Dinero.limpiarMonto(t); m = m.copy(umbral = umbralTexto.toDoubleOrNull() ?: 0.0) },
                                singleLine = true, label = { Text(if (m.umbral_max) "Un día cuenta si queda en (p. ej. 120 minutos) o menos" else "Un día cuenta desde (p. ej. 60 minutos)") },
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                    }
                }
                if (m.medida_tipo != null) CampoTexto(value = objetivoTexto, onValueChange = { t -> objetivoTexto = io.github.ruddy2003.vestigium.data.Dinero.limpiarMonto(t); m = m.copy(objetivo = objetivoTexto.toDoubleOrNull()) },
                    singleLine = true, label = { Text(if (m.medida_modo == "dias") "Cuántos días" else "Meta numérica") }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Etiqueta("Revisión")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.REVISIONES.forEach { r -> Ficha(r.nombre, m.revision == r.id && m.revisiones.isEmpty()) {
                        if (r.id == "fecha") elegirFecha(ctx, m.revisar_el, desdeManana = true) { f -> m = m.copy(revision = "fecha", revisar_el = f, revisiones = emptyList()) }
                        else m = m.copy(revision = r.id, revisiones = emptyList())
                    } }
                }
                if (previa != null) {
                    Etiqueta("Estado")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Catalogo.ESTADOS_META.forEach { e -> Ficha(e.nombre, m.estado == e.id) { m = m.copy(estado = e.id) } }
                    }
                    if (previa.historial.isNotEmpty()) {
                        Etiqueta("Historial")
                        previa.historial.takeLast(8).reversed().forEach { h ->
                            Text("${Fechas.corto(h.fecha)} · " + when (h.campo) { "revision" -> "revisada" + if (h.despues.isNotBlank()) ": ${h.despues}" else ""; else -> "${h.campo}: ${h.antes.ifBlank { "—" }} → ${h.despues.ifBlank { "—" }}" },
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(enabled = m.nombre.isNotBlank(), onClick = { Metas.cambiar(previa, m.copy(nombre = m.nombre.trim())); cerrar() }) { Text("Guardar") } },
        dismissButton = {
            Row {
                var confirmar by remember { mutableStateOf(false) }
                if (previa != null) TextButton(onClick = {
                    if (!confirmar) { confirmar = true; return@TextButton }
                    Repo.borrarMeta(previa.id); cerrar(); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Meta borrada") { Repo.guardarMeta(previa) }
                }) { Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error) }
                TextButton(onClick = cerrar) { Text("cancelar") }
            }
        })
}

// =====================================================================
//  «Cómo estás ahora» al cerrar el día
// =====================================================================

/**
 * Al final de la hoja de la noche: un registro de ánimo con la hora real. Es la misma medición a la misma hora todos
 * los días, comparable entre días (animo_cierre_valencia y animo_cierre_activacion).
 */
@Composable
fun BloqueCierre(fecha: String) {
    if (!io.github.ruddy2003.vestigium.data.cierrePermitido(fecha)) return
    val previo = Repo.momentos.lastOrNull { it.fecha == fecha && it.esCierre }
    var v by remember(fecha, previo?.id) { mutableStateOf(previo?.valencia) }
    var a by remember(fecha, previo?.id) { mutableStateOf(previo?.activacion) }
    Tarjeta {
        // El título ya lo pone la sección de la hoja de la noche.
        Ayuda(previo?.let { "Anotado a las ${Fechas.horaDe(it.ts)}. Si lo cambias, se guarda con la hora de ahora." } ?: "Un toque: qué tan agradable y cuánta energía, ahora, antes de repasar el día.")
        CuadriculaAfecto(v, a, Modifier.padding(top = 8.dp)) { nv, na ->
            v = nv; a = na
            val base = previo ?: Momento(Repo.nuevoId(), Fechas.ahoraIso(), fecha, "animo", origen = "cierre", ts_creado = Fechas.ahoraIso())
            Repo.guardarMomento(base.copy(ts = Fechas.ahoraIso(), valencia = nv, activacion = na))
        }
    }
}

// =====================================================================
//  Semana corta
// =====================================================================

/** Hoy con lo mínimo: cuatro cifras (valencia, sueño, enfoque, pasos) y tres botones (ánimo ahora, diario, sueño). */
@Composable
fun SemanaCorta(d: Dia, abrirNoche: () -> Unit) {
    val ctx = LocalContext.current
    val f = d.fecha
    val cs = MaterialTheme.colorScheme
    val animo = d.valencia ?: Repo.momentos.filter { it.fecha == f && it.valencia != null && (it.tipo == "muestreo" || it.tipo == "animo") }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average()?.let { Math.round(it).toInt() }
    val sueno = Repo.salud[f]?.sueno_min
    val enfoque = Enfoque.delDia(f).filterKeys { it != "siesta" }.values.sum()
    val pasos = Repo.salud[f]?.pasos
    Column(Modifier.padding(top = 12.dp)) {
        if (f == Fechas.diaLogico()) io.github.ruddy2003.vestigium.ui.AvisoMadrugada(Modifier.padding(bottom = 6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra(animo?.let { "$it/7" } ?: "—", "valencia", Modifier.weight(1f))
            Cifra(sueno?.let { "${it / 60}h${"%02d".format(it % 60)}" } ?: "—", "sueño", Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra(if (enfoque > 0) "${enfoque} min" else "—", "enfoque", Modifier.weight(1f))
            Cifra(pasos?.let { "%,d".format(it) } ?: "—", "pasos", Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        BotonPrincipal("Ánimo ahora", Modifier.fillMaxWidth()) {
            ctx.startActivity(Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_ANIMO).putExtra(QuickActivity.ORIGEN, "app").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonSecundario("Diario", Modifier.weight(1f)) { abrirNoche() }
            BotonSecundario("Sueño", Modifier.weight(1f)) {
                ctx.startActivity(Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_SUENO).putExtra(QuickActivity.ORIGEN, "app").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
        Text("Semana corta: solo lo mínimo. Todo lo demás sigue guardándose solo.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
        TextButton(onClick = { Repo.cambiarConfig { it.copy(semana_corta = false) } }) { Text("volver a Hoy completo", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
    }
}

/** Tras tres «hoy no» seguidos, una invitación a la semana corta (una vez por día). */
@Composable
fun SugerirSemanaCorta(hoy: String) {
    val c = Repo.config
    if (c.semana_corta || c.semana_corta_sugerida == hoy) return
    val tres = (0..2).all { Fechas.sumar(hoy, -it.toLong()) in c.hoy_no }
    if (!tres) return
    Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Tres días seguidos de «hoy no»", style = MaterialTheme.typography.titleMedium)
            Ayuda("¿Probamos la semana corta? Hoy muestra solo cuatro cifras y tres botones; lo automático se sigue guardando.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                BotonPrincipal("Activar") { Repo.cambiarConfig { it.copy(semana_corta = true, semana_corta_sugerida = hoy) } }
                TextButton(onClick = { Repo.cambiarConfig { it.copy(semana_corta_sugerida = hoy) } }) { Text("no, gracias") }
            }
        }
    }
}
