@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Escalas
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.ciencia.Fuentes
import io.github.ruddy2003.vestigium.data.Carta
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Habito
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaMenu
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlin.random.Random

@Composable
fun PantallaMas(nav: Nav) {
    val pendientes = Escalas.TODAS.count { i ->
        val u = Repo.ultimaRespuesta(i.id)
        u == null || java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(u.fecha), java.time.LocalDate.now()) >= i.frecuenciaDias
    }
    Hoja("Más") {
        Seccion("Registrar", numero = 1, latin = "TABVLAE") {
            Tarjeta {
                FilaMenu("Calendario", "Elige cualquier día para abrirlo o corregirlo") { nav.ir("calendario") }
                HorizontalDivider()
                val ult = Repo.ascensos.maxByOrNull { it.fecha }
                FilaMenu("Escalada", if (ult == null) "Bitácora de sesiones y bloques" else "${Repo.ascensos.size} sesiones · última ${Fechas.relativo(ult.fecha)}") { nav.ir("escalada") }
                HorizontalDivider()
                FilaMenu("Gimnasio", if (Repo.entrenos.isEmpty()) "Entrenamientos de MacroFactor Workouts" else "${Repo.entrenos.values.count { it.series > 0 }} entrenamientos importados") { nav.ir("fuerza") }
                HorizontalDivider()
                val leyendo = Repo.obras.count { it.estado == "en_curso" }
                FilaMenu("Biblioteca", "Libros, películas, series y juegos" + if (leyendo > 0) " · $leyendo en curso" else "") { nav.ir("biblioteca") }
                HorizontalDivider()
                FilaMenu("Hábitos", "${Repo.config.habitos.count { it.activo }} activos") { nav.ir("habitos") }
            }
        }
        Seccion("Medir", numero = 2) {
            Tarjeta {
                FilaMenu("Cuestionarios", if (pendientes > 0) "$pendientes pendiente(s): bienestar, estrés, soledad, personalidad" else "Al día") { nav.ir("escalas") }
                HorizontalDivider()
                FilaMenu("Prueba de reacción", "30 segundos para medir tu estado mental") { nav.ir("reaccion") }
                HorizontalDivider()
                FilaMenu("Mapa", "Calles con tu ánimo, tus rutas y dónde pasas el tiempo") { nav.ir("mapa") }
                HorizontalDivider()
                FilaMenu("Lugares", "${Repo.lugares.size} lugares marcados") { nav.ir("lugares") }
            }
        }
        Seccion("Entenderte", numero = 3) {
            Tarjeta {
                FilaMenu("Totales", "Cuánto de todo desde la fecha que elijas: comida, kilómetros, personas, música…") { nav.ir("totales") }
                HorizontalDivider()
                FilaMenu("Tu año", "El año en píxeles, mes por mes y lo más frecuente") { nav.ir("anno") }
                HorizontalDivider()
                FilaMenu("Análisis", "Tendencias, factores, modelo, dinámica emocional, sueño, palabras") { nav.ir("analisis") }
                HorizontalDivider()
                FilaMenu("Experimentos personales", "Prueba si algo de verdad te cambia") { nav.ir("experimentos") }
                HorizontalDivider()
                FilaMenu("Cartas al futuro", "${Repo.cartas.count { !it.abierta }} esperando su fecha") { nav.ir("cartas") }
                HorizontalDivider()
                FilaMenu("Reportes", "Semanal en imagen, mensual en PDF") { nav.ir("reportes") }
            }
        }
        Seccion("Sistema", numero = 4) {
            Tarjeta {
                FilaMenu("Datos", "Exportar a R, Google Drive, respaldos, importar") { nav.ir("datos") }
                HorizontalDivider()
                FilaMenu("Permisos y sensores", "Ubicación, reloj, pantalla, fotos, agenda, notificaciones, clima") { nav.ir("permisos") }
                HorizontalDivider()
                FilaMenu("Ajustes", "Horarios de avisos, modo discreto, huella") { nav.ir("ajustes") }
                HorizontalDivider()
                FilaMenu("Guía científica", "Qué mide cada cosa y de dónde viene") { nav.ir("guia") }
            }
        }
        Ayuda("vestigium mei · schema_v = ${io.github.ruddy2003.vestigium.data.SCHEMA_V} · ${Romano.fecha(Fechas.diaLogico())}", Modifier.padding(top = 16.dp))
    }
}

@Composable
fun PantallaHabitos(nav: Nav) {
    var nuevo by remember { mutableStateOf("") }
    val c = Repo.config
    fun agregar(nombre: String) {
        if (nombre.isBlank() || c.habitos.any { it.nombre == nombre }) return
        var id = Texto.slug(nombre).ifEmpty { "h" }
        while (Repo.config.habitos.any { it.id == id }) id += "_"
        Repo.cambiarConfig { it.copy(habitos = it.habitos + Habito(id, nombre, true, Fechas.diaLogico())) }
        nuevo = ""
    }
    Hoja("Hábitos", nav.atras, "Cada hábito aparece como casilla en el día. Archivar lo oculta sin borrar su historia.") {
        Seccion("Activos") {
            Tarjeta {
                val activos = c.habitos.filter { it.activo }
                if (activos.isEmpty()) Ayuda("Sin hábitos activos.")
                activos.forEach { h ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        val hechos = Repo.dias.values.count { it.habitos[h.id] == true }
                        Column(Modifier.weight(1f)) { Text(h.nombre, style = MaterialTheme.typography.bodyLarge); Ayuda("$hechos días cumplido") }
                        TextButton(onClick = { Repo.cambiarConfig { cc -> cc.copy(habitos = cc.habitos.map { if (it.id == h.id) it.copy(activo = false) else it }) } }) { Text("Archivar") }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = nuevo, onValueChange = { nuevo = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Nombre del hábito") })
                    TextButton(onClick = { agregar(nuevo.trim()) }) { Text("Añadir") }
                }
                FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.HABITOS_SUGERIDOS.filter { s -> c.habitos.none { it.nombre == s } }.forEach { s -> Ficha("+ $s", false) { agregar(s) } }
                }
            }
        }
        val archivados = c.habitos.filter { !it.activo }
        if (archivados.isNotEmpty()) Seccion("Archivados") {
            Tarjeta {
                archivados.forEach { h ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(h.nombre, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = { Repo.cambiarConfig { cc -> cc.copy(habitos = cc.habitos.map { if (it.id == h.id) it.copy(activo = true) else it }) } }) { Text("Reactivar") }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaCartas(nav: Nav) {
    var texto by remember { mutableStateOf("") }
    var plazo by remember { mutableStateOf(365L) }
    var leyendo by remember { mutableStateOf<Carta?>(null) }
    val hoy = Fechas.diaLogico()
    Hoja("Cartas al futuro", nav.atras, "Escribe algo para tu yo de dentro de un tiempo. Queda sellado hasta la fecha que elijas.") {
        val listas = Repo.cartas.filter { !it.abierta && it.abrir_en <= hoy }
        if (listas.isNotEmpty()) Seccion("Listas para abrir") {
            listas.forEach { c ->
                Tarjeta(Modifier.padding(bottom = 8.dp)) {
                    Text("Escrita el ${Fechas.titulo(c.escrita)}", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { Repo.guardarCarta(c.copy(abierta = true)); leyendo = c.copy(abierta = true) }) { Text("Abrir") }
                }
            }
        }
        leyendo?.let { c -> Seccion("Carta del ${Fechas.titulo(c.escrita)}") { Tarjeta { Text(c.texto, style = MaterialTheme.typography.bodyLarge) } } }
        Seccion("Escribir una carta") {
            OutlinedTextField(value = texto, onValueChange = { texto = it }, modifier = Modifier.fillMaxWidth(), minLines = 6, placeholder = { Text("Querido yo del futuro…") })
            Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(30L to "1 mes", 182L to "6 meses", 365L to "1 año", 1826L to "5 años").forEach { (d, n) -> Ficha(n, plazo == d) { plazo = d } }
            }
            BotonPrincipal("Sellar carta", Modifier.fillMaxWidth().padding(top = 10.dp), habilitado = texto.isNotBlank()) {
                Repo.guardarCarta(Carta(Repo.nuevoId(), hoy, Fechas.sumar(hoy, plazo), texto.trim())); texto = ""
            }
        }
        val selladas = Repo.cartas.filter { !it.abierta && it.abrir_en > hoy }.sortedBy { it.abrir_en }
        Seccion("Selladas") {
            if (selladas.isEmpty()) Vacio("Ninguna por ahora.")
            selladas.forEach { Ayuda("Escrita el ${it.escrita} · se abre el ${it.abrir_en}", Modifier.padding(vertical = 4.dp)) }
        }
        val abiertas = Repo.cartas.filter { it.abierta }.sortedByDescending { it.escrita }
        if (abiertas.isNotEmpty()) Seccion("Ya abiertas") {
            abiertas.forEach { c -> TextButton(onClick = { leyendo = c }) { Text("Del ${c.escrita}") } }
        }
    }
}

@Composable
fun PantallaExperimentos(nav: Nav) {
    var nombre by remember { mutableStateOf("") }
    var accion by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("valencia") }
    var semanas by remember { mutableStateOf(6) }
    val vars = remember { Estadistica.variables() }
    Hoja("Experimentos personales", nav.atras,
        "Eliges algo que probar y la app sortea qué semanas lo haces y cuáles no. Al final compara tu resultado entre unas y otras. Sortear evita que lo hagas justo en las semanas en que ya ibas a estar bien.") {
        Repo.experimentos.sortedByDescending { it.inicio }.forEach { e ->
            Seccion(e.nombre) {
                Tarjeta {
                    val panel = remember(e.id, Repo.dias.size) { Estadistica.panel() }
                    val si = ArrayList<Double>(); val no = ArrayList<Double>()
                    e.plan.forEachIndexed { s, toca ->
                        for (d in 0 until 7) {
                            val f = Fechas.sumar(e.inicio, s * 7L + d)
                            panel[f]?.get(e.resultado)?.let { if (toca) si.add(it) else no.add(it) }
                        }
                    }
                    val sem = java.time.temporal.ChronoUnit.WEEKS.between(Fechas.parse(e.inicio), Fechas.parse(Fechas.diaLogico())).toInt()
                    Text("Acción: ${e.accion} · resultado: ${vars.firstOrNull { it.id == e.resultado }?.nombre ?: e.resultado}", style = MaterialTheme.typography.bodyMedium)
                    Ayuda(if (sem in e.plan.indices) "Semana ${sem + 1} de ${e.semanas}: " + (if (e.plan[sem]) "SÍ toca." else "NO toca.") else if (sem >= e.semanas) "Terminado." else "Empieza el ${e.inicio}.")
                    Ayuda("Plan: " + e.plan.joinToString(" ") { if (it) "SÍ" else "no" })
                    val dif = Estadistica.diferencia(si, no)
                    if (dif == null) Ayuda("Aún no hay suficientes días medidos en ambas condiciones (${si.size} con, ${no.size} sin).", Modifier.padding(top = 6.dp))
                    else Text(
                        "Con la acción: ${"%.2f".format(si.average())} · sin ella: ${"%.2f".format(no.average())}. Diferencia ${"%+.2f".format(dif.first)} " +
                            "(intervalo de 95%: ${"%.2f".format(dif.second)} a ${"%.2f".format(dif.third)}). " +
                            if (dif.second > 0 || dif.third < 0) "El intervalo no toca el cero: es una señal real para ti." else "El intervalo incluye el cero: todavía no se distingue del azar.",
                        style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
                    if (e.activo) TextButton(onClick = { Repo.guardarExperimento(e.copy(activo = false)) }) { Text("Terminar experimento") }
                }
            }
        }
        Seccion("Nuevo experimento") {
            Tarjeta {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(value = accion, onValueChange = { accion = it }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp), label = { Text("Qué vas a hacer (ej. meditar 10 minutos)") })
                Ayuda("Qué quieres ver si cambia:", Modifier.padding(top = 10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("valencia", "activacion", "sueno_calidad", "sueno_horas", "muestreo_valencia", "pantalla_min", "rt_media", "impulso").forEach { id ->
                        vars.firstOrNull { it.id == id }?.let { v -> Ficha(v.nombre, resultado == id) { resultado = id } }
                    }
                }
                Ayuda("Duración:", Modifier.padding(top = 10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf(4, 6, 8).forEach { s -> Ficha("$s semanas", semanas == s) { semanas = s } } }
                BotonPrincipal("Empezar el lunes", Modifier.fillMaxWidth().padding(top = 12.dp), habilitado = nombre.isNotBlank() && accion.isNotBlank()) {
                    val plan = (List(semanas / 2) { true } + List(semanas - semanas / 2) { false }).shuffled(Random(System.currentTimeMillis()))
                    val lunes = Fechas.sumar(Fechas.lunesDe(Fechas.diaLogico()), 7)
                    Repo.guardarExperimento(Experimento(Repo.nuevoId(), nombre.trim(), accion.trim(), resultado, lunes, semanas, plan))
                    nombre = ""; accion = ""
                }
            }
        }
    }
}

@Composable
fun PantallaGuia(nav: Nav) {
    Hoja("Guía científica", nav.atras, "Por qué la app mide lo que mide.") {
        Seccion("Cómo leer tus datos") {
            Tarjeta {
                Text("Con pocos días todo es pista, no evidencia. Para detectar una relación moderada se necesitan unos 85 días con ambos datos.", style = MaterialTheme.typography.bodyMedium)
                Text("Los días se parecen a sus vecinos: si en R usas regresión simple, la incertidumbre sale más pequeña de lo real. Usa modelos con autocorrelación (por ejemplo nlme::gls con corAR1) o modelos multinivel.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                Text("Que dos cosas se muevan juntas no prueba que una cause la otra. Para causas, usa los experimentos personales.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                Text("Los huecos importan: si dejas de registrar justo los días malos, los datos se ven más felices de lo que fue tu vida. Por eso existe el registro de un toque para días difíciles.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Seccion("Cuestionarios") {
            Escalas.TODAS.forEach { i ->
                Tarjeta(Modifier.padding(bottom = 8.dp)) {
                    Text(i.nombre, style = MaterialTheme.typography.titleMedium)
                    Ayuda(i.mide + " · cada ${i.frecuenciaDias} días")
                    Text(i.referencia, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    Ayuda(i.adaptacion, Modifier.padding(top = 4.dp))
                }
            }
        }
        Seccion("Marcos y referencias") {
            Tarjeta {
                Fuentes.LISTA.forEach { (t, r) ->
                    Text(t, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                    Text(r, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
