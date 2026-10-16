@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Opcion
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.BotonDictado
import io.github.ruddy2003.vestigium.ui.Escala
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Opciones
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.tipoNombre
import kotlinx.coroutines.delay

@Composable
fun PantallaDia(nav: Nav) {
    var fecha by remember { mutableStateOf(Fechas.diaLogico()) }
    val guardado = Repo.dias[fecha]
    var d by remember(fecha) { mutableStateOf(guardado ?: Dia(fecha = fecha)) }
    var sucio by remember(fecha) { mutableStateOf(false) }
    val dActual by rememberUpdatedState(d)
    val sucioActual by rememberUpdatedState(sucio)

    LaunchedEffect(guardado) { if (!sucio && guardado != null) d = guardado }
    LaunchedEffect(d, sucio) { if (sucio) { delay(700); Repo.guardarDia(d); sucio = false } }
    DisposableEffect(fecha) { onDispose { if (sucioActual) Repo.guardarDia(dActual) } }
    fun cambiar(n: Dia) { d = n; sucio = true }

    val ctx = LocalContext.current
    val c = Repo.config

    Hoja(Fechas.titulo(fecha)) {
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedIconButton(onClick = { if (sucio) Repo.guardarDia(d); fecha = Fechas.sumar(fecha, -1) }) { Text("‹", fontSize = 20.sp) }
            TextButton(onClick = {
                val p = Fechas.parse(fecha)
                DatePickerDialog(ctx, { _, y, m, dd -> if (sucio) Repo.guardarDia(d); fecha = Fechas.iso(java.time.LocalDate.of(y, m + 1, dd)) }, p.year, p.monthValue - 1, p.dayOfMonth).show()
            }, modifier = Modifier.weight(1f)) {
                Text("$fecha · ${Fechas.relativo(fecha)}" + if (guardado != null) " · con registro" else "", fontFamily = FontFamily.Monospace, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
            OutlinedIconButton(onClick = { if (sucio) Repo.guardarDia(d); fecha = Fechas.sumar(fecha, 1) }) { Text("›", fontSize = 20.sp) }
        }
        if (fecha == Fechas.diaLogico() && java.time.LocalTime.now().hour < 5) Aviso("Pasa de medianoche: sigues llenando el día $fecha. Los días empiezan a las 5:00.")
        if (sucio) Ayuda("Guardando…", Modifier.padding(top = 4.dp)) else if (guardado != null) Ayuda("Guardado", Modifier.padding(top = 4.dp))

        // Hace un año
        Repo.dias[Fechas.iso(Fechas.parse(fecha).minusYears(1))]?.let { viejo ->
            if (viejo.nota.isNotBlank()) Seccion("Este día hace un año") {
                Tarjeta { Text(viejo.nota.take(280) + if (viejo.nota.length > 280) "…" else "", style = MaterialTheme.typography.bodyMedium) }
            }
        }
        // Experimento activo
        Repo.experimentos.filter { it.activo }.forEach { e ->
            val semana = java.time.temporal.ChronoUnit.WEEKS.between(Fechas.parse(e.inicio), Fechas.parse(fecha)).toInt()
            if (semana in e.plan.indices) Aviso("Experimento «${e.nombre}», semana ${semana + 1} de ${e.semanas}: " + if (e.plan[semana]) "esta semana SÍ: ${e.accion}." else "esta semana NO hagas: ${e.accion}.")
        }

        Seccion("Sueño", "Llénalo apenas despiertes. El resto, en la noche.") {
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
                        OutlinedTextField(value = d.sueno_texto, onValueChange = { cambiar(d.copy(sueno_texto = it)) }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                        Row(Modifier.padding(top = 6.dp)) { BotonDictado { t -> cambiar(d.copy(sueno_texto = (d.sueno_texto + " " + t).trim())) } }
                    }
                }
                Repo.salud[fecha]?.sueno_min?.let { m -> Ayuda("Según tu reloj dormiste ${m / 60} h ${m % 60} min.", Modifier.padding(top = 10.dp)) }
            }
        }

        Seccion("Contexto", "Activa todo lo que aplique. Pueden ser varios o ninguno.") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Catalogo.CONTEXTOS.forEach { o ->
                    val activo = d.ctx[o.id] == true
                    Ficha(o.nombre, activo) { cambiar(d.copy(ctx = if (activo) d.ctx - o.id else d.ctx + (o.id to true))) }
                }
            }
        }

        Seccion("Hitos", "Momentos que no pasan todos los días: un vuelo, un beso, una decisión importante. Una etiqueta corta por cosa.") {
            ListaEtiquetas(d.hitos, "vuelo, beso, decision_importante", normalizar = { Texto.slug(it) }) { cambiar(d.copy(hitos = it)) }
        }

        Seccion("Ánimo y cuerpo") {
            Tarjeta {
                Etiqueta("Valencia · qué tan agradable fue el día")
                Escala(d.valencia, 1, 7, Catalogo.ANCLAS["valencia"]) { cambiar(d.copy(valencia = it)) }
                Etiqueta("Activación · tu nivel de energía")
                Escala(d.activacion, 1, 7, Catalogo.ANCLAS["activacion"]) { cambiar(d.copy(activacion = it)) }
                Etiqueta("Dolor o molestia física")
                Escala(d.dolor, 0, 5, Catalogo.ANCLAS["dolor"]) { cambiar(d.copy(dolor = it)) }
                val mu = Repo.momentos.filter { it.fecha == fecha && it.tipo == "muestreo" && it.valencia != null }
                if (mu.isNotEmpty()) Ayuda("Durante el día registraste: " + mu.joinToString(" · ") { "${Fechas.horaDe(it.ts)} → ${it.valencia}" }, Modifier.padding(top = 10.dp))
            }
        }

        Seccion("Lectura del día", "Tres preguntas de marcos científicos. Toca «Cómo elegir» si dudas.") {
            Tarjeta {
                Catalogo.CIENTIFICAS.forEachIndexed { i, p ->
                    var abierta by remember { mutableStateOf(false) }
                    if (i > 0) Spacer(Modifier.padding(top = 14.dp))
                    Text(p.pregunta, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { abierta = !abierta }, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                        Text(if (abierta) "Ocultar guía" else "Cómo elegir · ${p.autor}", style = MaterialTheme.typography.bodySmall)
                    }
                    if (abierta) Ayuda(p.guia, Modifier.padding(bottom = 8.dp))
                    val valor = when (p.clave) { "flow" -> d.flow; "sdt_necesidad" -> d.sdt_necesidad; else -> d.apreciacion }
                    Opciones(p.opciones, valor) { v ->
                        cambiar(when (p.clave) { "flow" -> d.copy(flow = v); "sdt_necesidad" -> d.copy(sdt_necesidad = v); else -> d.copy(apreciacion = v) })
                    }
                }
            }
        }

        Seccion("Lo mejor y lo peor", "Una línea cada uno.") {
            OutlinedTextField(value = d.mejor_momento, onValueChange = { cambiar(d.copy(mejor_momento = it)) }, modifier = Modifier.fillMaxWidth(), label = { Text("Mejor momento") })
            Spacer(Modifier.padding(top = 8.dp))
            OutlinedTextField(value = d.peor_momento, onValueChange = { cambiar(d.copy(peor_momento = it)) }, modifier = Modifier.fillMaxWidth(), label = { Text("Peor momento") })
        }

        Seccion("Hábitos") {
            val activos = c.habitos.filter { it.activo }
            if (activos.isEmpty()) {
                Tarjeta {
                    Ayuda("Aún no tienes hábitos.")
                    TextButton(onClick = { nav.ir("habitos") }) { Text("Agregar hábitos") }
                }
            } else Tarjeta {
                activos.forEach { h ->
                    val hecho = d.habitos[h.id] == true
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = hecho, onCheckedChange = { cambiar(d.copy(habitos = if (it) d.habitos + (h.id to true) else d.habitos - h.id)) })
                        Text(h.nombre, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Seccion("Medicamentos", "Lo que tomaste hoy, si aplica.") {
            ListaEtiquetas(d.medicamentos, "nombre del medicamento", normalizar = { Texto.slug(it) }) { cambiar(d.copy(medicamentos = it)) }
        }

        if (c.seguimiento_privado) Seccion("Seguimiento privado", "Solo para ti. Sirve para ver qué días y contextos lo hacen más difícil.") {
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

        Seccion("Diario", "Escribe libre. Usa @persona y #tema; abajo ves lo que se detecta y si alguna etiqueta parece error de dedo.") {
            OutlinedTextField(value = d.nota, onValueChange = { cambiar(d.copy(nota = it)) }, modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp), placeholder = { Text("¿Cómo fue el día?") })
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                BotonDictado { t -> cambiar(d.copy(nota = (d.nota + " " + t).trim())) }
                Spacer(Modifier.weight(1f))
                Text("${Texto.palabras(d.nota)} palabras", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Detectados(d.nota, fecha) { nuevo -> cambiar(d.copy(nota = nuevo)) }
        }

        Seccion("Predicción", "¿Cómo crees que estará tu ánimo mañana? La app medirá qué tan bien te conoces.") {
            Escala(d.prediccion_manana, 1, 7, Catalogo.ANCLAS["prediccion_manana"]) { cambiar(d.copy(prediccion_manana = it)) }
        }

        val delDia = Repo.momentos.filter { it.fecha == fecha }
        if (delDia.isNotEmpty()) Seccion("Momentos de este día") {
            Tarjeta {
                delDia.forEach { m ->
                    Text("${Fechas.horaDe(m.ts)} · ${tipoNombre(m.tipo, c.discreto)}" + (m.valencia?.let { " · $it" } ?: "") + (if (m.texto.isNotBlank()) " · ${m.texto}" else ""),
                        style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 3.dp))
                }
            }
        }
    }
}

/** Muestra las etiquetas detectadas y marca en ámbar las nuevas; sugiere corrección si se parece a una conocida. */
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
        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Row(Modifier.padding(start = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("$s$a es nueva. ¿Quisiste decir $s$b?", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                TextButton(onClick = { onCorregir(Texto.reemplazar(texto, s, a, b)) }) { Text("Corregir") }
            }
        }
    }
    if (hayHistoria && (P.any { Texto.canon(it, eq) !in conocidas.first } || T.any { Texto.canon(it, eq) !in conocidas.second }))
        Ayuda("En ámbar: etiquetas que nunca habías usado. Si es una persona o tema nuevo, está bien.", Modifier.padding(top = 6.dp))
}

@Composable
private fun Tok(t: String, nueva: Boolean) {
    Surface(shape = RoundedCornerShape(6.dp), color = if (nueva) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant) {
        Text(t, fontFamily = FontFamily.Monospace, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = if (nueva) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

