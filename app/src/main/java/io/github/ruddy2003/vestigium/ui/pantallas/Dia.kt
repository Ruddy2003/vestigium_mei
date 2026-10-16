@file:OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Movilidad
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Clima
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Luna
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.sensores.Actividad
import io.github.ruddy2003.vestigium.sensores.Galeria
import io.github.ruddy2003.vestigium.sensores.Sincronizar
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.BotonDictado
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Editor
import io.github.ruddy2003.vestigium.ui.Escala
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Opciones
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.tipoNombre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaDia(nav: Nav) {
    var fecha by remember { mutableStateOf(Seleccion.pedida ?: Fechas.diaLogico()) }
    val guardado = Repo.dias[fecha]
    var d by remember(fecha) { mutableStateOf(guardado ?: Dia(fecha = fecha)) }
    var sucio by remember(fecha) { mutableStateOf(false) }
    val dActual by rememberUpdatedState(d)
    val sucioActual by rememberUpdatedState(sucio)

    val pedida = Seleccion.pedida
    LaunchedEffect(pedida) {
        if (pedida != null) {
            if (pedida != fecha) { if (sucio) Repo.guardarDia(d); fecha = pedida }
            Seleccion.pedida = null
        }
    }
    LaunchedEffect(guardado) { if (!sucio && guardado != null) d = guardado }
    LaunchedEffect(d, sucio) { if (sucio) { delay(700); Repo.guardarDia(d); sucio = false } }
    DisposableEffect(fecha) { onDispose { if (sucioActual) Repo.guardarDia(dActual) } }
    fun cambiar(n: Dia) { d = n; sucio = true }
    fun irA(f: String) { if (sucio) Repo.guardarDia(d); fecha = f; Seleccion.ultimoMes = Fechas.mesDe(f) }

    val c = Repo.config
    val hoy = Fechas.diaLogico()

    Hoja("Día", encabezado = {
        Row(Modifier.padding(top = 14.dp), verticalAlignment = Alignment.Bottom) {
            Text("DIES", style = MaterialTheme.typography.headlineMedium)
            Text("  ${Romano.fecha(fecha)}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        }
        val fase = Luna.fase(fecha)
        Text("// ${Fechas.titulo(fecha).lowercase()} · ${Fechas.relativo(fecha)} · ${Luna.glifo(fase)}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }) {
        // El contador vive dentro del bloque para que la numeración se reinicie en cada recomposición.
        var k = 0
        fun n() = ++k
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            BotonSecundario("‹") { irA(Fechas.sumar(fecha, -1)) }
            BotonSecundario("▦ calendarium", Modifier.weight(1f)) { Seleccion.ultimoMes = Fechas.mesDe(fecha); if (sucio) Repo.guardarDia(d); nav.ir("calendario") }
            if (fecha != hoy) BotonSecundario("hoy") { irA(hoy) }
            BotonSecundario("›") { irA(Fechas.sumar(fecha, 1)) }
        }
        Row(Modifier.padding(top = 6.dp)) {
            Text("estado = ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(when { sucio -> "guardando…"; guardado != null -> "guardado"; else -> "vacío" } + (if (guardado?.retroactivo == true) " · retroactivo" else ""),
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
        if (fecha == hoy && java.time.LocalTime.now().hour < 5) Aviso("Pasa de medianoche: sigues llenando el día $fecha. Los días empiezan a las 5:00.")
        if (fecha > hoy) Aviso("Este día todavía no llega. Puedes dejar notas, pero se marcará como escrito por adelantado.")

        // Hace un año
        Repo.dias[Fechas.iso(Fechas.parse(fecha).minusYears(1))]?.let { viejo ->
            if (viejo.nota.isNotBlank()) Seccion("Este día hace un año") {
                Tarjeta { Text(viejo.nota.take(280) + if (viejo.nota.length > 280) "…" else "", style = MaterialTheme.typography.bodyMedium) }
            }
        }
        Repo.experimentos.filter { it.activo }.forEach { e ->
            val semana = java.time.temporal.ChronoUnit.WEEKS.between(Fechas.parse(e.inicio), Fechas.parse(fecha)).toInt()
            if (semana in e.plan.indices) Aviso("Experimento «${e.nombre}», semana ${semana + 1} de ${e.semanas}: " + if (e.plan[semana]) "esta semana SÍ: ${e.accion}." else "esta semana NO hagas: ${e.accion}.")
        }

        Seccion("Sueño", "Llénalo apenas despiertes. El resto, en la noche.", numero = n()) {
            Tarjeta {
                Etiqueta("Calidad del sueño, como la sentiste")
                Escala(d.sueno_calidad, 1, 5, Catalogo.ANCLAS["sueno_calidad"]) { cambiar(d.copy(sueno_calidad = it)) }
                Interruptor("¿Recordaste un sueño?", d.sueno_recordado) { cambiar(d.copy(sueno_recordado = it)) }
                AnimatedVisibility(d.sueno_recordado) {
                    Column {
                        Etiqueta("Qué tan agradable fue")
                        Escala(d.sueno_valencia, 1, 7, Catalogo.ANCLAS["sueno_valencia"]) { cambiar(d.copy(sueno_valencia = it)) }
                        Etiqueta("Lucidez: ¿sabías que soñabas?"); FilaFichas(Catalogo.SUENO_LUCIDEZ, d.sueno_lucidez) { cambiar(d.copy(sueno_lucidez = it)) }
                        Etiqueta("Vividez del recuerdo"); FilaFichas(Catalogo.SUENO_VIVIDEZ, d.sueno_vividez) { cambiar(d.copy(sueno_vividez = it)) }
                        Etiqueta("Tipo"); FilaFichas(Catalogo.SUENO_TIPO, d.sueno_tipo) { cambiar(d.copy(sueno_tipo = it)) }
                        Etiqueta("Personas y temas del sueño")
                        ListaEtiquetas(d.sueno_temas, "@persona o #tema", normalizar = { Texto.etiqueta(it) }) { cambiar(d.copy(sueno_temas = it)) }
                        Etiqueta("Narración")
                        Editor(d.sueno_texto, "somnium.txt", "Lo que recuerdes, aunque sean fragmentos.", minimo = 110, pie = "${Texto.palabras(d.sueno_texto)} verba") { cambiar(d.copy(sueno_texto = it)) }
                        Row(Modifier.padding(top = 6.dp)) { BotonDictado { t -> cambiar(d.copy(sueno_texto = (d.sueno_texto + " " + t).trim())) } }
                    }
                }
                Repo.salud[fecha]?.sueno_min?.let { m -> Clave("horologium", "${m / 60} h ${m % 60} min dormido según tu reloj") }
            }
        }

        Seccion("Contexto", "Activa todo lo que aplique. Pueden ser varios o ninguno.", numero = n()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Catalogo.CONTEXTOS.forEach { o ->
                    val activo = d.ctx[o.id] == true
                    Ficha(o.nombre, activo) { cambiar(d.copy(ctx = if (activo) d.ctx - o.id else d.ctx + (o.id to true))) }
                }
            }
        }

        Seccion("Hitos", "Lo que no pasa todos los días: un vuelo, un beso, una decisión importante. Una etiqueta corta por cosa.", numero = n()) {
            ListaEtiquetas(d.hitos, "vuelo, beso, decision_importante", normalizar = { Texto.slug(it) }) { cambiar(d.copy(hitos = it)) }
        }

        Seccion("Ánimo y cuerpo", numero = n()) {
            Tarjeta {
                Etiqueta("Valencia · qué tan agradable fue el día")
                Escala(d.valencia, 1, 7, Catalogo.ANCLAS["valencia"]) { cambiar(d.copy(valencia = it)) }
                Etiqueta("Activación · tu nivel de energía")
                Escala(d.activacion, 1, 7, Catalogo.ANCLAS["activacion"]) { cambiar(d.copy(activacion = it)) }
                Etiqueta("Dolor o molestia física")
                Escala(d.dolor, 0, 5, Catalogo.ANCLAS["dolor"]) { cambiar(d.copy(dolor = it)) }
                val mu = Repo.momentos.filter { it.fecha == fecha && it.tipo == "muestreo" && it.valencia != null }
                if (mu.isNotEmpty()) Clave("avisos", mu.joinToString("  ") { "${Fechas.horaDe(it.ts)}→${it.valencia}" })
            }
        }

        Seccion("Lectura del día", "Tres preguntas de marcos científicos. Toca «cómo elegir» si dudas.", numero = n()) {
            Tarjeta {
                Catalogo.CIENTIFICAS.forEachIndexed { i, p ->
                    var abierta by remember { mutableStateOf(false) }
                    if (i > 0) Spacer(Modifier.padding(top = 14.dp))
                    Text(p.pregunta, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { abierta = !abierta }, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                        Text(if (abierta) "− ocultar guía" else "+ cómo elegir · ${p.autor}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    if (abierta) Ayuda(p.guia, Modifier.padding(bottom = 8.dp))
                    val valor = when (p.clave) { "flow" -> d.flow; "sdt_necesidad" -> d.sdt_necesidad; else -> d.apreciacion }
                    Opciones(p.opciones, valor) { v ->
                        cambiar(when (p.clave) { "flow" -> d.copy(flow = v); "sdt_necesidad" -> d.copy(sdt_necesidad = v); else -> d.copy(apreciacion = v) })
                    }
                }
            }
        }

        Seccion("Lo mejor y lo peor", "Una línea cada uno.", numero = n()) {
            OutlinedTextField(value = d.mejor_momento, onValueChange = { cambiar(d.copy(mejor_momento = it)) }, modifier = Modifier.fillMaxWidth(),
                label = { Text("optimum", style = MaterialTheme.typography.labelMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.padding(top = 8.dp))
            OutlinedTextField(value = d.peor_momento, onValueChange = { cambiar(d.copy(peor_momento = it)) }, modifier = Modifier.fillMaxWidth(),
                label = { Text("pessimum", style = MaterialTheme.typography.labelMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
        }

        Seccion("Hábitos", numero = n()) {
            val activos = c.habitos.filter { it.activo }
            if (activos.isEmpty()) {
                Tarjeta {
                    Ayuda("Aún no tienes hábitos.")
                    TextButton(onClick = { nav.ir("habitos") }) { Text("+ agregar hábitos", style = MaterialTheme.typography.labelLarge) }
                }
            } else Tarjeta {
                activos.forEach { h ->
                    val hecho = d.habitos[h.id] == true
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = hecho, onCheckedChange = { cambiar(d.copy(habitos = if (it) d.habitos + (h.id to true) else d.habitos - h.id)) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary))
                        Text(h.nombre, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Text(if (hecho) "= 1" else "= 0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Seccion("Medicamentos", "Lo que tomaste hoy, si aplica.", numero = n()) {
            ListaEtiquetas(d.medicamentos, "nombre del medicamento", normalizar = { Texto.slug(it) }) { cambiar(d.copy(medicamentos = it)) }
        }

        if (c.seguimiento_privado) Seccion("Seguimiento privado", "Solo para ti. Sirve para ver qué días y contextos lo hacen más difícil.", numero = n()) {
            Tarjeta {
                Etiqueta("Intensidad del impulso")
                Escala(d.impulso, 0, 5, Catalogo.ANCLAS["impulso"]) { cambiar(d.copy(impulso = it)) }
                Etiqueta("¿Hubo recaída?")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Ficha("No", d.recaida == false) { cambiar(d.copy(recaida = if (d.recaida == false) null else false)) }
                    Ficha("Sí", d.recaida == true) { cambiar(d.copy(recaida = if (d.recaida == true) null else true)) }
                }
                Etiqueta("Qué lo detonó")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.DETONANTES.forEach { t ->
                        val activo = t in d.detonantes
                        Ficha(t.replace("_", " "), activo) { cambiar(d.copy(detonantes = if (activo) d.detonantes - t else d.detonantes + t)) }
                    }
                }
            }
        }

        Seccion("Diario", "Escribe libre. Usa @persona y #tema; abajo ves lo que se detecta y si alguna etiqueta parece error de dedo.", numero = n()) {
            Editor(d.nota, "diarium_$fecha.txt", "¿Cómo fue el día?", pie = "${Texto.palabras(d.nota)} verba") { cambiar(d.copy(nota = it)) }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                BotonDictado { t -> cambiar(d.copy(nota = (d.nota + " " + t).trim())) }
            }
            Detectados(d.nota, fecha) { nuevo -> cambiar(d.copy(nota = nuevo)) }
        }

        Seccion("Predicción", "¿Cómo crees que estará tu ánimo mañana? La app medirá qué tan bien te conoces.", numero = n()) {
            Escala(d.prediccion_manana, 1, 7, Catalogo.ANCLAS["prediccion_manana"]) { cambiar(d.copy(prediccion_manana = it)) }
        }

        Seccion("Lo que registró el teléfono", "Se llena solo con los sensores que activaste. Nada de esto tienes que escribirlo.", numero = n()) {
            Vestigios(fecha, nav)
        }

        Seccion("Fotos del día", "Toca una para verla; mantén presionada para elegirla como la imagen del día.", numero = n()) {
            Imagenes(fecha, d.foto, nav) { uri -> cambiar(d.copy(foto = if (d.foto == uri) null else uri)) }
        }

        val delDia = Repo.momentos.filter { it.fecha == fecha }
        if (delDia.isNotEmpty()) Seccion("Momentos de este día", numero = n()) {
            Tarjeta {
                delDia.forEach { m ->
                    Row(Modifier.padding(vertical = 3.dp)) {
                        Text(Fechas.horaDe(m.ts) + "  ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(tipoNombre(m.tipo, c.discreto) + (m.valencia?.let { " · $it" } ?: "") + (if (m.texto.isNotBlank()) " · ${m.texto}" else ""),
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

private fun hm(min: Int): String = if (min >= 60) "${min / 60} h ${"%02d".format(min % 60)} min" else "$min min"

/** Todo lo automático del día, en formato clave = valor. */
@Composable
private fun Vestigios(fecha: String, nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val s = Repo.salud[fecha]; val p = Repo.pantalla[fecha]; val e = Repo.entorno[fecha]; val ft = Repo.fotos[fecha]
    val ag = Repo.agenda[fecha]; val nt = Repo.notifs[fecha]; val ac = Repo.actividad[fecha]
    val ses = Repo.sesiones.filter { it.fecha == fecha }
    val visitas = Repo.visitas.filter { it.entrada.take(10) == fecha }
    val km by produceState<Double?>(null, fecha) { value = withContext(Dispatchers.IO) { runCatching { Movilidad.kmDe(fecha) }.getOrNull() } }
    val musica by produceState(emptyList<io.github.ruddy2003.vestigium.data.Escucha>(), fecha, Repo.notifs[fecha]) {
        value = withContext(Dispatchers.IO) { runCatching { Repo.escuchas(fecha, fecha) }.getOrDefault(emptyList()) }
    }
    val fase = Luna.fase(fecha)

    Tarjeta {
        var algo = false
        if (s != null) {
            val m = s.sueno_min
            if (m != null) {
                algo = true
                val ini = s.sueno_inicio; val fin = s.sueno_fin
                Clave("somnus", hm(m) + if (ini != null) " · ${Fechas.horaDe(ini)}→${if (fin != null) Fechas.horaDe(fin) else "?"}" else "")
                val prof = s.sueno_profundo_min
                if (prof != null) Clave("  stadia", "profundo ${hm(prof)} · rem ${hm(s.sueno_rem_min ?: 0)} · ligero ${hm(s.sueno_ligero_min ?: 0)}")
            }
            if (s.fc_reposo != null || s.hrv_rmssd != null) {
                algo = true
                Clave("cor", listOfNotNull(s.fc_reposo?.let { "reposo %.0f lpm".format(it) }, s.hrv_rmssd?.let { "hrv %.0f ms".format(it) }, s.spo2?.let { "spo₂ %.0f%%".format(it) }).joinToString(" · "))
            }
            s.pasos?.let { algo = true; Clave("passus", "%,d pasos".format(it)) }
        }
        ac?.minutos?.filterKeys { it != "quieto" }?.filterValues { it > 0 }?.takeIf { it.isNotEmpty() }?.let { m ->
            algo = true; Clave("motus", m.entries.sortedByDescending { it.value }.joinToString(" · ") { "${Actividad.NOMBRES[it.key] ?: it.key} ${hm(it.value)}" })
        }
        km?.takeIf { it >= 0.2 }?.let { algo = true; Clave("iter", "%.1f km recorridos".format(it)) }
        if (ses.isNotEmpty()) { algo = true; Clave("exercitatio", ses.joinToString(" · ") { x -> "${x.tipo_nombre} ${hm(x.min)}" + (x.fc_media?.let { " (%.0f lpm)".format(it) } ?: "") }) }
        if (visitas.isNotEmpty()) {
            algo = true
            val porLugar = visitas.groupBy { it.lugar }.mapValues { (_, l) -> l.sumOf { v -> ((Fechas.millis(v.salida ?: Fechas.ahoraIso()) - Fechas.millis(v.entrada)) / 60000).toInt() } }
            Clave("loca", porLugar.entries.sortedByDescending { it.value }.joinToString(" · ") { (id, m) -> (Repo.lugares.firstOrNull { it.id == id }?.nombre ?: "?") + " " + hm(m) })
        }
        p?.let {
            algo = true
            Clave("telephonum", "${hm(it.total_min)} · ${it.desbloqueos} desbloqueos")
            if (it.primer_uso != null) Clave("  usus", "primer uso ${Fechas.horaDe(it.primer_uso)} · último ${it.ultimo_uso?.let { u -> Fechas.horaDe(u) } ?: "?"}")
            it.por_app_min.entries.take(3).takeIf { l -> l.isNotEmpty() }?.let { l -> Clave("  apps", l.joinToString(" · ") { a -> "${a.key} ${hm(a.value)}" }) }
        }
        nt?.let { algo = true; Clave("monita", "${it.total} notificaciones · " + it.por_app.entries.sortedByDescending { x -> x.value }.take(3).joinToString(" · ") { x -> "${x.key} ${x.value}" }) }
        if (musica.isNotEmpty()) {
            algo = true
            val top = musica.groupBy { it.artista.ifBlank { "?" } }.maxByOrNull { it.value.size }?.key
            Clave("musica", "${musica.size} canciones" + (top?.let { " · más: $it" } ?: ""))
        }
        ft?.let { if (it.fotos + it.capturas + it.videos > 0) { algo = true; Clave("imagines", "${it.fotos} fotos · ${it.capturas} capturas · ${it.videos} videos") } }
        ag?.let { if (it.eventos + it.todo_el_dia > 0) { algo = true; Clave("fasti", "${it.eventos} eventos · ${hm(it.min_ocupado)} ocupado" + if (it.titulos.isNotEmpty()) " · " + it.titulos.take(3).joinToString(", ") else "") } }
        e?.let {
            algo = true
            Clave("caelum", listOfNotNull(it.codigo?.let { c -> Clima.descripcion(c) }, if (it.t_max != null) "%.0f° / %.0f°".format(it.t_max, it.t_min ?: it.t_max) else null,
                it.precip_mm?.takeIf { x -> x > 0.05 }?.let { x -> "lluvia %.1f mm".format(x) }, it.nubes_pct?.let { x -> "nubes %.0f%%".format(x) }).joinToString(" · "))
            if (it.luz_min != null) Clave("lux", "${it.amanecer ?: "?"} → ${it.atardecer ?: "?"} · ${hm(it.luz_min)} de luz" + (it.sol_min?.let { x -> " · ${hm(x)} de sol" } ?: ""))
            if (it.aqi_us != null) Clave("aer", "calidad del aire ${it.aqi_us} (${aqiTexto(it.aqi_us)})" + (it.pm25?.let { x -> " · pm2.5 %.0f".format(x) } ?: ""))
        }
        Clave("luna", "${Luna.nombre(fase)} · ${(Luna.iluminacion(fase) * 100).toInt()}% iluminada")
        if (!algo) Ayuda("Aún no hay datos automáticos de este día. Activa fuentes en Más → Permisos y sensores.", Modifier.padding(top = 8.dp))
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            BotonSecundario(if (ocupado) "sincronizando…" else "↻ sincronizar", habilitado = !ocupado) {
                scope.launch { ocupado = true; msg = Sincronizar.todo(ctx); ocupado = false }
            }
            BotonSecundario("sensores →") { nav.ir("permisos") }
        }
        msg?.let { Ayuda(it, Modifier.padding(top = 6.dp)) }
    }
}

private fun aqiTexto(a: Int) = when { a <= 50 -> "buena"; a <= 100 -> "aceptable"; a <= 150 -> "mala para sensibles"; a <= 200 -> "mala"; else -> "muy mala" }

@Composable
private fun Imagenes(fecha: String, elegida: String?, nav: Nav, onElegir: (String) -> Unit) {
    val ctx = LocalContext.current
    val permiso = Galeria.tienePermiso(ctx) || Galeria.parcial(ctx)
    if (!permiso) {
        Vacio("Da acceso a tus fotos en Permisos y sensores para ver aquí las del día.")
        BotonSecundario("dar acceso →", Modifier.padding(top = 8.dp)) { nav.ir("permisos") }
        return
    }
    val fotos by produceState(emptyList<Galeria.Foto>(), fecha) { value = withContext(Dispatchers.IO) { Galeria.delDia(ctx, fecha) } }
    if (fotos.isEmpty()) { Vacio("No hay fotos de este día en tu galería."); return }
    val orden = fotos.sortedWith(compareBy({ it.uri.toString() != elegida }, { it.tipo != "camara" }, { it.ms }))
    elegida?.let { u ->
        Box(Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(4.dp)).border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))) {
            Miniatura(Uri.parse(u), 720, Modifier.fillMaxWidth().aspectRatio(1.6f))
            Text(" IMAGO DIEI ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(MaterialTheme.colorScheme.primary))
        }
        Spacer(Modifier.height(8.dp))
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(orden, key = { it.id }) { f ->
            val sel = f.uri.toString() == elegida
            Box(Modifier.size(96.dp).clip(RoundedCornerShape(3.dp))
                .border(if (sel) 2.dp else 1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(3.dp))
                .combinedClickable(
                    onClick = { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(f.uri, "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)) } },
                    onLongClick = { onElegir(f.uri.toString()) },
                )) {
                Miniatura(f.uri, 240, Modifier.size(96.dp))
                Text(Fechas.horaDe(Fechas.isoDeMillis(f.ms)), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomEnd).background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)).padding(horizontal = 3.dp))
                if (f.tipo == "captura") Text("cap", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.TopStart).background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)).padding(horizontal = 3.dp))
                if (sel) Text("★", color = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.TopEnd).padding(3.dp))
            }
        }
    }
    val camara = fotos.count { it.tipo == "camara" }
    Ayuda("$camara de cámara · ${fotos.count { it.tipo == "captura" }} capturas · ${fotos.count { it.tipo == "otra" }} de otras apps", Modifier.padding(top = 6.dp))
}

@Composable
fun Miniatura(uri: Uri, px: Int, modifier: Modifier) {
    val ctx = LocalContext.current
    val bmp by produceState<Bitmap?>(null, uri, px) { value = withContext(Dispatchers.IO) { Galeria.miniatura(ctx, uri, px) } }
    val b = bmp
    if (b != null) Image(b.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
    else Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
}

/** Muestra las etiquetas detectadas y marca las nuevas; sugiere corrección si se parece a una conocida. */
@Composable
fun Detectados(texto: String, fecha: String, onCorregir: (String) -> Unit) {
    val eq = Repo.equivalencias()
    val conocidas = remember(Repo.dias.size, fecha) {
        val p = HashSet<String>(); val t = HashSet<String>()
        Repo.dias.forEach { (f, dd) -> if (f != fecha) { Texto.personas(dd.nota).forEach { p.add(Texto.canon(it, eq)) }; Texto.temas(dd.nota).forEach { t.add(Texto.canon(it, eq)) } } }
        Repo.momentos.forEach { m -> Texto.personas(m.texto).forEach { p.add(Texto.canon(it, eq)) }; Texto.temas(m.texto).forEach { t.add(Texto.canon(it, eq)) } }
        p to t
    }
    val P = Texto.personas(texto).distinct(); val T = Texto.temas(texto).distinct()
    if (P.isEmpty() && T.isEmpty()) return
    val hayHistoria = conocidas.first.size + conocidas.second.size > 0
    FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        P.forEach { Tok("@$it", hayHistoria && Texto.canon(it, eq) !in conocidas.first) }
        T.forEach { Tok("#$it", hayHistoria && Texto.canon(it, eq) !in conocidas.second) }
    }
    val sugerencias = P.filter { Texto.canon(it, eq) !in conocidas.first }.mapNotNull { x -> conocidas.first.firstOrNull { Texto.parecidos(x, it) }?.let { Triple('@', x, it) } } +
        T.filter { Texto.canon(it, eq) !in conocidas.second }.mapNotNull { x -> conocidas.second.firstOrNull { Texto.parecidos(x, it) }?.let { Triple('#', x, it) } }
    sugerencias.forEach { (s, a, b) ->
        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Row(Modifier.padding(start = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("$s$a es nueva. ¿Quisiste decir $s$b?", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                TextButton(onClick = { onCorregir(Texto.reemplazar(texto, s, a, b)) }) { Text("corregir", style = MaterialTheme.typography.labelLarge) }
            }
        }
    }
    if (hayHistoria && (P.any { Texto.canon(it, eq) !in conocidas.first } || T.any { Texto.canon(it, eq) !in conocidas.second }))
        Ayuda("Resaltadas: etiquetas que nunca habías usado. Si es una persona o tema nuevo, está bien.", Modifier.padding(top = 6.dp))
}

@Composable
private fun Tok(t: String, nueva: Boolean) {
    Surface(shape = RoundedCornerShape(3.dp), color = if (nueva) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant) {
        Text(t, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = if (nueva) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
    }
}
