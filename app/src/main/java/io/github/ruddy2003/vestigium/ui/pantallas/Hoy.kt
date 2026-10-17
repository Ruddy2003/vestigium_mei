@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.text.withStyle
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Escalas
import io.github.ruddy2003.vestigium.ciencia.Movilidad
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Spotify
import io.github.ruddy2003.vestigium.sensores.Galeria
import io.github.ruddy2003.vestigium.sensores.Oyente
import io.github.ruddy2003.vestigium.sensores.Pantalla
import io.github.ruddy2003.vestigium.sensores.Salud
import io.github.ruddy2003.vestigium.sensores.Sincronizar
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Frases
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs

// =====================================================================
//  Lo que falta registrar hoy
// =====================================================================

/** Algo que te toca hacer hoy. [accion] dice a dónde lleva el enlace. */
data class Pendiente(val clave: String, val texto: String, val accion: String)

object Pendientes {
    /** Lo que falta a esta hora. Solo para el día de hoy (y el diario de ayer si quedó vacío). */
    fun deHoy(d: Dia, hoy: String): List<Pendiente> {
        val ahora = LocalTime.now()
        val l = ArrayList<Pendiente>()
        val c = Repo.config
        val ignorado = { k: String -> c.avisos_ignorados[k] == hoy }
        // Diario de ayer: solo en la mañana, si no tiene nada.
        val ayer = Fechas.sumar(hoy, -1)
        val dAyer = Repo.dias[ayer]
        if (ahora.hour < 14 && (dAyer == null || (dAyer.valencia == null && dAyer.nota.isBlank())) && !ignorado("p:ayer"))
            l += Pendiente("p:ayer", "Diario de ayer", "ayer")
        if (d.sueno_calidad == null && ahora.hour >= 5) l += Pendiente("p:sueno", "Calidad del sueño", "sueno")
        // Avisos de ánimo que ya sonaron y no contestaste.
        val ms = System.currentTimeMillis()
        val plan = c.plan_muestreo[Fechas.hoyCalendario()].orEmpty().count { it <= ms }
        val hechos = Repo.momentos.count { it.fecha == hoy && it.tipo == "muestreo" }
        val faltan = (plan - hechos).coerceAtLeast(0)
        if (faltan > 0) l += Pendiente("p:aviso", if (faltan == 1) "1 aviso de ánimo sin contestar" else "$faltan avisos de ánimo sin contestar", "aviso")
        Escalas.pendientes().firstOrNull()?.let { (ins, atraso) ->
            l += Pendiente("p:escalas", if (atraso > 0) "${ins.nombre}: ${atraso} días de atraso" else "${ins.nombre} toca hoy", "escalas")
        }
        // Lo de la noche aparece desde 4 horas antes de tu hora de diario.
        val tarde = ahora.hour >= (c.hora_diario - 4).coerceAtLeast(14)
        if (tarde && (d.valencia == null || d.nota.isBlank())) l += Pendiente("p:diario", if (d.valencia == null) "Diario de hoy" else "Escribir la nota del día", "diario")
        if (tarde && d.prediccion_manana == null && !ignorado("p:prediccion")) l += Pendiente("p:prediccion", "Predicción para mañana", "prediccion")
        if (ahora.hour >= 20 && Repo.gastos.none { it.dia == hoy } && !ignorado("p:gasto")) l += Pendiente("p:gasto", "¿Gastaste algo hoy?", "gasto")
        return l
    }
}

@Composable
fun TeFaltaHoy(d: Dia, hoy: String, accion: (String) -> Unit) {
    val l = Pendientes.deHoy(d, hoy)
    val tarde = LocalTime.now().hour >= 21
    // Cuando se completa lo pendiente, una pequeña celebración con el sonido y la vibración del tema.
    val sentir = io.github.ruddy2003.vestigium.ui.rememberSentir()
    var antes by remember(hoy) { mutableStateOf<Boolean?>(null) }
    androidx.compose.runtime.LaunchedEffect(l.isEmpty()) {
        if (antes == false && l.isEmpty()) { io.github.ruddy2003.vestigium.ui.Fiesta.lanzar(); sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.LOGRO) }
        antes = l.isEmpty()
    }
    if (l.isEmpty()) {
        if (tarde && d.valencia != null) Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            Text("✓ Hoy completo. Buenas noches.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        return
    }
    Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(Estilo.seccion("Te falta hoy", null).first, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                l.forEach { p -> Ficha("${p.texto} →", false) { accion(p.accion) } }
            }
            // Lo opcional se puede descartar por hoy.
            val opcionales = l.filter { it.clave in setOf("p:gasto", "p:prediccion", "p:ayer") }
            if (opcionales.isNotEmpty()) TextButton(onClick = {
                Repo.cambiarConfig { c -> c.copy(avisos_ignorados = c.avisos_ignorados + opcionales.associate { it.clave to hoy }) }
            }) { Text("no hace falta: " + opcionales.joinToString(", ") { it.texto.lowercase().removePrefix("¿").removeSuffix("?") }, style = MaterialTheme.typography.labelMedium) }
        }
    }
}

// =====================================================================
//  Datos automáticos que deberían estar y no están
// =====================================================================

data class Falta(val clave: String, val texto: String, val arreglo: String, val hacer: suspend (Context) -> String?)

object Faltas {
    suspend fun revisar(ctx: Context): List<Falta> = withContext(Dispatchers.IO) {
        val hoy = Fechas.diaLogico(); val ayer = Fechas.sumar(hoy, -1)
        val ahora = LocalDateTime.now()
        val c = Repo.config
        val l = ArrayList<Falta>()
        val sincronizar: suspend (Context) -> String? = { x -> Sincronizar.todo(x) }
        // Sueño de anoche del reloj: a partir de las 10, si Health Connect tiene permiso de sueño.
        val salud = runCatching { if (Salud.disponible(ctx)) Salud.concedidos(ctx) else emptySet() }.getOrDefault(emptySet())
        if (ahora.hour >= 10 && salud.any { it.contains("SLEEP", true) } && Repo.salud[hoy]?.sueno_min == null)
            l += Falta("sueno", "No llegó el sueño de anoche desde el reloj. ¿Lo usaste para dormir?", "sincronizar", sincronizar)
        // Ubicación: más de 3 horas sin muestra con el permiso de fondo activo.
        if (c.muestreo_ubicacion && Ubicacion.tieneSegundoPlano(ctx)) {
            val u = runCatching { Repo.db().ultimaUbicacion() }.getOrNull()
            if (u != null && System.currentTimeMillis() - u > 3 * 3_600_000L)
                l += Falta("ubicacion", "Sin ubicación desde las ${Fechas.horaDe(Fechas.isoDeMillis(u))}. Android pudo haber pausado la app; quítale la optimización de batería.", "permisos") { null }
        }
        if (c.clima && ahora.hour >= 9 && Repo.entorno[ayer] == null) l += Falta("clima", "Falta el clima de ayer.", "sincronizar", sincronizar)
        if (ahora.hour >= 9 && Pantalla.tienePermiso(ctx) && Repo.pantalla[ayer] == null) l += Falta("pantalla", "Falta el uso del teléfono de ayer.", "sincronizar", sincronizar)
        if (Spotify.conectado(ctx) && System.currentTimeMillis() - Spotify.ultimo(ctx) > 2 * 3_600_000L) {
            val u = Spotify.ultimo(ctx)
            l += Falta("spotify", "Spotify sin leer desde " + (if (u > 0) Fechas.horaDe(Fechas.isoDeMillis(u)) else "que lo conectaste") + (Spotify.error(ctx)?.let { ". $it" } ?: "."), "leer ahora") { x ->
                "Spotify: ${Spotify.sincronizar(x)} canciones nuevas."
            }
        }
        val respaldo = listOfNotNull(c.respaldo_ultimo?.takeIf { c.carpeta_respaldo != null }, c.drive_ultimo?.takeIf { c.drive }).maxOrNull()
        if ((c.carpeta_respaldo != null || c.drive) && (respaldo == null || System.currentTimeMillis() - Fechas.millis(respaldo) > 36 * 3_600_000L))
            l += Falta("respaldo", "Sin respaldo " + (respaldo?.let { "desde el ${it.take(10)}" } ?: "todavía") + ".", "datos") { null }
        // Lector de notificaciones: concedido pero sin contar nada en todo el día suele ser que Android lo desconectó.
        if (Oyente.activo(ctx) && ahora.hour >= 13 && (Repo.notifs[hoy]?.total ?: 0) == 0 && Repo.notifs.keys.any { it >= Fechas.sumar(hoy, -7) })
            l += Falta("notificaciones", "El lector de notificaciones no ha contado nada hoy; puede estar desconectado.", "reconectar") { x ->
                runCatching { NotificationListenerService.requestRebind(ComponentName(x, Oyente::class.java)) }; "Pedí reconectar el lector de notificaciones."
            }
        Repo.nutricion.keys.maxOrNull()?.let { u ->
            val dias = java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(u), Fechas.parse(hoy))
            if (dias > 10) l += Falta("macrofactor", "Hace $dias días que no importas MacroFactor.", "datos") { null }
        }
        l.filter { f -> val i = c.avisos_ignorados[f.clave]; i != "siempre" && i != hoy }
    }
}

@Composable
fun DatosQueFaltan(nav: io.github.ruddy2003.vestigium.ui.Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var version by remember { mutableStateOf(0) }
    val faltas by produceState(emptyList<Falta>(), version, Repo.config.avisos_ignorados) { value = runCatching { Faltas.revisar(ctx) }.getOrDefault(emptyList()) }
    var msg by remember { mutableStateOf<String?>(null) }
    if (faltas.isEmpty() && msg == null) return
    val hoy = Fechas.diaLogico()
    Tarjeta(Modifier.padding(top = 10.dp)) {
        Text(Estilo.seccion("Datos que faltan", null).first, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
        faltas.forEachIndexed { i, f ->
            if (i > 0) HorizontalDivider(Modifier.padding(vertical = 6.dp))
            Text(f.texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                TextButton(onClick = {
                    when (f.arreglo) {
                        "permisos" -> nav.ir("permisos")
                        "datos" -> nav.ir("datos")
                        else -> scope.launch { msg = runCatching { withContext(Dispatchers.IO) { f.hacer(ctx) } }.getOrElse { "No se pudo: ${it.message}" }; version++ }
                    }
                }) { Text(f.arreglo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
                TextButton(onClick = { Repo.cambiarConfig { c -> c.copy(avisos_ignorados = c.avisos_ignorados + (f.clave to hoy)) } }) {
                    Text("ignorar hoy", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = { Repo.cambiarConfig { c -> c.copy(avisos_ignorados = c.avisos_ignorados + (f.clave to "siempre")) } }) {
                    Text("no volver a avisar", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        msg?.let { Ayuda(it, Modifier.padding(top = 4.dp)) }
        if (Repo.config.avisos_ignorados.values.any { it == "siempre" }) TextButton(onClick = {
            Repo.cambiarConfig { c -> c.copy(avisos_ignorados = c.avisos_ignorados.filterValues { it != "siempre" }) }
        }) { Text("volver a avisar de todo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

// =====================================================================
//  Frase del día (cada tema la suya)
// =====================================================================

@Composable
fun FraseDelDia(fecha: String) {
    val tema = Estilo.tema
    val f = Frases.del(fecha, tema)
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    if (tema == TemaId.DISTOPIA) {
        // Cartel de propaganda: fondo negro, letras claras y una franja roja.
        Surface(color = cs.onBackground, shape = androidx.compose.ui.graphics.RectangleShape, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Row(Modifier.height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
                Box(Modifier.width(8.dp).fillMaxHeight().background(cs.primary))
                Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Text(Frases.rotulo(tema), style = ty.labelMedium, color = cs.primary)
                    f.original?.let { Text(it, style = ty.bodySmall, fontStyle = FontStyle.Italic, color = cs.background.copy(alpha = 0.72f)) }
                    Text(f.texto.uppercase(), style = ty.titleLarge, color = cs.background, modifier = Modifier.padding(vertical = 2.dp))
                    Text("— ${f.autor}", style = ty.labelSmall, color = cs.background.copy(alpha = 0.7f))
                }
            }
        }
        return
    }
    val marco = tema == TemaId.REFUGIO || tema == TemaId.ORDEN || tema == TemaId.LABORATORIO || tema == TemaId.CIBER
    val contenido: @Composable () -> Unit = {
        Column(Modifier.padding(if (marco) 12.dp else 0.dp)) {
            Text(Frases.rotulo(tema), style = ty.labelMedium, color = cs.primary)
            // Los originales cortos (griego, latín) se lucen grandes; los largos van chicos y la traducción manda.
            f.original?.let { Text(it, style = if (it.length > 60) ty.bodySmall else ty.bodyLarge, color = cs.primary, fontStyle = FontStyle.Italic, modifier = Modifier.padding(top = 2.dp)) }
            if (tema == TemaId.MEDIEVAL) {
                // Capitular iluminada: la primera letra grande, en gótica y en rojo.
                Text(androidx.compose.ui.text.buildAnnotatedString {
                    withStyle(androidx.compose.ui.text.SpanStyle(fontFamily = io.github.ruddy2003.vestigium.ui.LetraExtra.gotica, color = cs.primary, fontSize = ty.bodyMedium.fontSize * 2.2f)) { append(f.texto.take(1)) }
                    append(f.texto.drop(1))
                }, style = ty.bodyMedium)
            } else Text(if (tema == TemaId.CIBER) f.texto else "«${f.texto}»", style = ty.bodyMedium)
            Text("— ${f.autor}", style = ty.labelMedium, color = cs.onSurfaceVariant)
        }
    }
    if (marco) Surface(shape = MaterialTheme.shapes.small, color = cs.surface, border = androidx.compose.foundation.BorderStroke(1.dp, cs.primary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { contenido() }
    else Row(Modifier.fillMaxWidth().padding(top = 12.dp).height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
        Box(Modifier.width(2.dp).fillMaxHeight().background(cs.primary.copy(alpha = 0.6f)))
        Box(Modifier.padding(start = 10.dp)) { contenido() }
    }
}

// =====================================================================
//  Racha de diario
// =====================================================================

/** Días seguidos con diario (valencia o nota) que terminan en [fecha], o en el día anterior si [fecha] aún está vacío. */
fun racha(fecha: String): Int {
    fun lleno(f: String) = Repo.dias[f]?.let { it.valencia != null || it.nota.isNotBlank() } == true
    var f = if (lleno(fecha)) fecha else Fechas.sumar(fecha, -1)
    var n = 0
    while (lleno(f) && n < 5000) { n++; f = Fechas.sumar(f, -1) }
    return n
}

// =====================================================================
//  Tablero: cifras del día contra tu normal
// =====================================================================

class Metrica(val id: String, val nombre: String, val formato: (Double) -> String, val valor: (String, Map<String, Map<String, Double>>) -> Double?)

private fun horas(min: Double) = if (min >= 60) "%d h %02d".format((min / 60).toInt(), (min % 60).toInt()) else "%.0f min".format(min)

val METRICAS = listOf(
    Metrica("sueno", "Sueño", { "%.1f h".format(it) }) { f, _ -> Repo.salud[f]?.sueno_min?.let { it / 60.0 } },
    Metrica("animo", "Ánimo", { "%.1f / 7".format(it) }) { f, _ ->
        Repo.dias[f]?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == f && it.tipo == "muestreo" }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average()
    },
    Metrica("pasos", "Pasos", { "%,.0f".format(it) }) { f, _ -> Repo.salud[f]?.pasos?.toDouble() },
    Metrica("km", "Kilómetros", { "%.1f km".format(it) }) { f, x -> x["km"]?.get(f) },
    Metrica("pantalla", "Pantalla", ::horas) { f, _ -> Repo.pantalla[f]?.total_min?.toDouble() },
    Metrica("desbloqueos", "Desbloqueos", { "%.0f".format(it) }) { f, _ -> Repo.pantalla[f]?.desbloqueos?.toDouble() },
    Metrica("gasto", "Gasto", { "$%,.0f".format(it) }) { f, _ -> Repo.gastos.filter { it.dia == f }.sumOf { it.monto }.takeIf { it > 0 || Repo.dias.containsKey(f) } },
    Metrica("kcal", "Calorías", { "%,.0f kcal".format(it) }) { f, _ -> Repo.nutricion[f]?.valores?.get("kcal") },
    Metrica("proteina", "Proteína", { "%.0f g".format(it) }) { f, _ -> Repo.nutricion[f]?.valores?.get("proteina_g") },
    Metrica("ejercicio", "Ejercicio", ::horas) { f, _ -> Repo.sesiones.filter { it.fecha == f }.sumOf { it.min }.toDouble().takeIf { it > 0 } },
    Metrica("canciones", "Canciones", { "%.0f".format(it) }) { f, x -> x["canciones"]?.get(f) },
    Metrica("notificaciones", "Notificaciones", { "%.0f".format(it) }) { f, _ -> Repo.notifs[f]?.total?.toDouble() },
    Metrica("fotos", "Fotos", { "%.0f".format(it) }) { f, _ -> Repo.fotos[f]?.fotos?.toDouble() },
    Metrica("peso", "Peso", { "%.1f kg".format(it) }) { f, _ -> Repo.salud[f]?.peso_kg ?: Repo.nutricion[f]?.valores?.get("peso_tendencia_kg") },
    Metrica("hrv", "Variabilidad cardiaca", { "%.0f ms".format(it) }) { f, _ -> Repo.salud[f]?.hrv_rmssd },
    Metrica("palabras", "Palabras del diario", { "%.0f".format(it) }) { f, _ -> Repo.dias[f]?.nota?.let { io.github.ruddy2003.vestigium.data.Texto.palabras(it).toDouble() }?.takeIf { it > 0 } },
)
val TABLERO_INICIAL = listOf("sueno", "animo", "pasos", "pantalla", "gasto", "kcal")

@Composable
fun Tablero(fecha: String) {
    var editando by remember { mutableStateOf(false) }
    val elegidas = Repo.config.tablero.ifEmpty { TABLERO_INICIAL }
    // Lo que sale de la base (kilómetros, canciones) se lee una vez para las cinco semanas alrededor.
    val extra by produceState(emptyMap<String, Map<String, Double>>(), fecha) {
        value = withContext(Dispatchers.IO) {
            val desde = Fechas.sumar(fecha, -35)
            mapOf(
                "km" to runCatching { Movilidad.kmEntre(desde, fecha) }.getOrDefault(emptyMap()),
                "canciones" to runCatching { Repo.escuchas(desde, fecha).filter { (it.ms ?: 30_000) >= 30_000 }.groupingBy { it.fecha }.eachCount().mapValues { it.value.toDouble() } }.getOrDefault(emptyMap()),
            )
        }
    }
    val lista = elegidas.mapNotNull { id -> METRICAS.firstOrNull { it.id == id } }
    lista.chunked(2).forEach { par ->
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            par.forEach { m -> TarjetaMetrica(m, fecha, extra, Modifier.weight(1f)) }
            if (par.size == 1) Spacer(Modifier.weight(1f))
        }
    }
    TextButton(onClick = { editando = !editando }) {
        Text(if (editando) "listo" else "editar tablero", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    if (editando) Tarjeta {
        Ayuda("Elige qué cifras ver y en qué orden. Se comparan con el promedio de tus 4 semanas anteriores.")
        val orden = elegidas + METRICAS.map { it.id }.filter { it !in elegidas }
        orden.forEach { id ->
            val m = METRICAS.first { it.id == id }
            val activa = id in elegidas
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(activa, onCheckedChange = { v -> Repo.cambiarConfig { c -> c.copy(tablero = if (v) elegidas + id else elegidas - id) } })
                Text(m.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                if (activa) {
                    TextButton(onClick = { mover(elegidas, id, -1) }) { Text("↑") }
                    TextButton(onClick = { mover(elegidas, id, 1) }) { Text("↓") }
                }
            }
        }
    }
}

private fun mover(l: List<String>, id: String, d: Int) {
    val i = l.indexOf(id); val j = i + d
    if (i < 0 || j !in l.indices) return
    val m = l.toMutableList(); m[i] = l[j]; m[j] = id
    Repo.cambiarConfig { c -> c.copy(tablero = m) }
}

@Composable
private fun TarjetaMetrica(m: Metrica, fecha: String, extra: Map<String, Map<String, Double>>, modifier: Modifier) {
    val semana = (6 downTo 0).map { k -> Fechas.sumar(fecha, -k.toLong()).let { f -> m.valor(f, extra) } }
    val v = semana.last()
    val previos = (1..28).mapNotNull { k -> m.valor(Fechas.sumar(fecha, -k.toLong()), extra) }
    val normal = if (previos.size >= 5) previos.average() else null
    val color = MaterialTheme.colorScheme.primary
    val tenue = MaterialTheme.colorScheme.outline
    // Las barras crecen al abrir el día.
    val crecer = remember(fecha, m.id) { androidx.compose.animation.core.Animatable(if (Repo.config.animaciones) 0f else 1f) }
    androidx.compose.runtime.LaunchedEffect(fecha, m.id) {
        crecer.animateTo(1f, androidx.compose.animation.core.tween(700, delayMillis = 60, easing = androidx.compose.animation.core.FastOutSlowInEasing))
    }
    Surface(modifier, shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
        Column(Modifier.padding(10.dp)) {
            Text(m.nombre.lowercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Text(v?.let(m.formato) ?: "—", style = estiloCifra(), maxLines = 1)
            val dif = if (v != null && normal != null && normal != 0.0) (v - normal) / abs(normal) else null
            Text(
                when {
                    v == null -> "sin dato"
                    dif == null -> "aún sin tu normal"
                    abs(dif) < 0.05 -> "= tu normal"
                    else -> "${if (dif > 0) "↑" else "↓"} ${"%.0f".format(abs(dif) * 100)}% vs tu normal"
                },
                style = MaterialTheme.typography.labelSmall, maxLines = 1,
                color = if (dif != null && abs(dif) >= 0.2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            // Últimos 7 días: la barra de este día resaltada; los días sin dato, un punto.
            val max = semana.filterNotNull().maxOrNull()?.takeIf { it > 0 } ?: 1.0
            Canvas(Modifier.fillMaxWidth().height(26.dp).padding(top = 4.dp)) {
                val w = size.width / 7
                semana.forEachIndexed { i, x ->
                    if (x == null) drawCircle(tenue, 2f, Offset(i * w + w / 2, size.height - 2f))
                    else {
                        val h = (size.height * (x / max) * crecer.value).toFloat().coerceAtLeast(2f)
                        drawRoundRect(if (i == 6) color else color.copy(alpha = 0.35f), Offset(i * w + w * 0.2f, size.height - h), Size(w * 0.6f, h), CornerRadius(2f, 2f))
                    }
                }
                normal?.let { n -> val y = size.height - (size.height * (n / max)).toFloat(); if (y in 0f..size.height) drawLine(tenue, Offset(0f, y), Offset(size.width, y), 1f) }
            }
        }
    }
}

/** Letra de las cifras grandes: la capitular romana no tiene minúsculas, así que ahí se usa la monoespaciada. */
@Composable
fun estiloCifra(): androidx.compose.ui.text.TextStyle = when (Estilo.tema) {
    TemaId.ROMANO -> MaterialTheme.typography.titleLarge.copy(fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = androidx.compose.ui.unit.TextUnit(21f, androidx.compose.ui.unit.TextUnitType.Sp), letterSpacing = androidx.compose.ui.unit.TextUnit(0f, androidx.compose.ui.unit.TextUnitType.Sp))
    // La gótica es para títulos; las cifras se leen mejor en Garamond.
    TemaId.MEDIEVAL -> MaterialTheme.typography.titleLarge.copy(fontFamily = io.github.ruddy2003.vestigium.ui.Letra.garamond, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontSize = androidx.compose.ui.unit.TextUnit(23f, androidx.compose.ui.unit.TextUnitType.Sp), letterSpacing = androidx.compose.ui.unit.TextUnit(0f, androidx.compose.ui.unit.TextUnitType.Sp), fontFeatureSettings = "lnum, tnum")
    else -> MaterialTheme.typography.headlineSmall
}

// =====================================================================
//  Tu día en 24 horas
// =====================================================================

private class Pista(val nombre: String, val bloques: List<Triple<Long, Long, Color>> = emptyList(), val marcas: List<Pair<Long, Color>> = emptyList(),
                    val calor: List<Double>? = null, val etiquetas: List<Pair<Long, String>> = emptyList())

@Composable
fun LineaDelTiempo(fecha: String) {
    val ctx = LocalContext.current
    val oscuro = esOscuro()
    val prim = MaterialTheme.colorScheme.primary; val sec = MaterialTheme.colorScheme.secondary; val ter = MaterialTheme.colorScheme.tertiary
    val ini = Fechas.inicioDia(fecha)
    val fin = ini + 29 * 3_600_000L
    val musica by produceState(emptyList<Long>(), fecha) {
        value = withContext(Dispatchers.IO) { runCatching { Repo.escuchas(fecha, Fechas.sumar(fecha, 1)).map { Fechas.millis(it.ts) } }.getOrDefault(emptyList()) }
    }
    val fotos by produceState(emptyList<Long>(), fecha) {
        value = withContext(Dispatchers.IO) { runCatching { if (Galeria.tienePermiso(ctx)) Galeria.delDia(ctx, fecha).map { it.ms } else emptyList() }.getOrDefault(emptyList()) }
    }
    val pistas = ArrayList<Pista>()
    // Sueño: la noche que terminó esta mañana y la que empieza esta noche.
    val sueno = listOf(fecha, Fechas.sumar(fecha, 1)).mapNotNull { f -> Repo.salud[f]?.let { s -> if (s.sueno_inicio != null && s.sueno_fin != null) Triple(Fechas.millis(s.sueno_inicio), Fechas.millis(s.sueno_fin), sec) else null } }
    if (sueno.isNotEmpty()) pistas += Pista("sueño", sueno)
    val colores = listOf(prim, sec, ter)
    val visitas = Repo.visitas.filter { Fechas.millis(it.entrada) < fin && (it.salida?.let { s -> Fechas.millis(s) } ?: System.currentTimeMillis()) > ini }
    if (visitas.isNotEmpty()) {
        val ids = visitas.map { it.lugar }.distinct()
        val b = visitas.map { v -> Triple(Fechas.millis(v.entrada), v.salida?.let { Fechas.millis(it) } ?: System.currentTimeMillis(), colores[ids.indexOf(v.lugar) % 3]) }
        pistas += Pista("lugares", b, etiquetas = visitas.map { v -> Fechas.millis(v.entrada) to (Repo.lugares.firstOrNull { it.id == v.lugar }?.nombre ?: "") })
    }
    val ses = Repo.sesiones.filter { it.fecha == fecha }
    if (ses.isNotEmpty()) pistas += Pista("ejercicio", ses.map { Triple(Fechas.millis(it.inicio), Fechas.millis(it.fin), ter) })
    val mom = Repo.momentos.filter { it.fecha == fecha }
    if (mom.isNotEmpty()) pistas += Pista("momentos", marcas = mom.map { m -> Fechas.millis(m.ts) to (Tonos.valencia(m.valencia?.toDouble(), oscuro) ?: sec) })
    Repo.notifs[fecha]?.por_hora?.takeIf { it.sum() > 0 }?.let { h -> pistas += Pista("teléfono", calor = h.map { it.toDouble() }) }
    Repo.conexiones[fecha]?.let { cx -> if (cx.luz_n.sum() > 0) pistas += Pista("luz", calor = cx.luz_suma.indices.map { i -> if (cx.luz_n[i] > 0) kotlin.math.ln(1 + cx.luz_suma[i] / cx.luz_n[i]) else 0.0 }) }
    if (musica.isNotEmpty()) pistas += Pista("música", marcas = musica.map { it to prim })
    if (fotos.isNotEmpty()) pistas += Pista("fotos", marcas = fotos.map { it to ter })
    val rojo = MaterialTheme.colorScheme.error
    val gastos = Repo.gastos.filter { it.dia == fecha }
    if (gastos.isNotEmpty()) pistas += Pista("gastos", marcas = gastos.mapNotNull { g ->
        runCatching { java.time.LocalDateTime.of(Fechas.parse(g.fecha), LocalTime.parse(g.hora.take(5))).atZone(Fechas.zona()).toInstant().toEpochMilli() to rojo }.getOrNull()
    })
    if (pistas.isEmpty()) { Ayuda("Todavía no hay nada con hora en este día."); return }

    val e = Repo.entorno[fecha]
    fun hm(s: String?) = s?.let { runCatching { LocalTime.parse(it.takeLast(5)) }.getOrNull() }
    val amanecer = hm(e?.amanecer); val atardecer = hm(e?.atardecer)
    val noche = MaterialTheme.colorScheme.surfaceVariant
    val linea = MaterialTheme.colorScheme.outline
    val texto = MaterialTheme.colorScheme.onSurfaceVariant
    val alto = 20.dp
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.width(66.dp)) {
            pistas.forEach { p -> Box(Modifier.height(alto), contentAlignment = Alignment.CenterStart) { Text(p.nombre, style = MaterialTheme.typography.labelSmall, color = texto, maxLines = 1) } }
            Box(Modifier.height(alto))
        }
        val medidor = androidx.compose.ui.text.rememberTextMeasurer()
        val estiloEtq = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimary)
        val estiloEje = MaterialTheme.typography.labelSmall.copy(color = texto)
        Canvas(Modifier.weight(1f).height(alto * (pistas.size + 1))) {
            val h = alto.toPx()
            fun x(ms: Long) = ((ms - ini).toFloat() / (fin - ini)) * size.width
            // Noche sombreada según el amanecer y el atardecer del día.
            if (amanecer != null && atardecer != null) {
                val a = x(ini + amanecer.toSecondOfDay() * 1000L); val t = x(ini + atardecer.toSecondOfDay() * 1000L)
                drawRect(noche, Offset(0f, 0f), Size(a, h * pistas.size))
                drawRect(noche, Offset(t, 0f), Size(size.width - t, h * pistas.size))
            }
            pistas.forEachIndexed { i, p ->
                val y = i * h
                drawLine(linea, Offset(0f, y + h), Offset(size.width, y + h), 0.6f)
                p.bloques.forEach { (a, b, c) ->
                    val xa = x(a).coerceIn(0f, size.width); val xb = x(b).coerceIn(0f, size.width)
                    if (xb > xa) drawRoundRect(c.copy(alpha = 0.75f), Offset(xa, y + h * 0.2f), Size(xb - xa, h * 0.6f), CornerRadius(3f, 3f))
                }
                p.etiquetas.forEach { (a, t) ->
                    val xa = x(a)
                    if (t.isNotBlank() && xa in 0f..size.width - 30f) {
                        val r = medidor.measure(t.take(12), estiloEtq)
                        drawText(r, topLeft = Offset(xa + 3f, y + (h - r.size.height) / 2))
                    }
                }
                p.marcas.forEach { (a, c) -> val xa = x(a); if (xa in 0f..size.width) drawLine(c, Offset(xa, y + h * 0.18f), Offset(xa, y + h * 0.82f), 3f) }
                p.calor?.let { vals ->
                    val m = vals.maxOrNull()?.takeIf { it > 0 } ?: 1.0
                    vals.forEachIndexed { hora, v ->
                        if (v > 0) {
                            val xa = x(ini + hora * 3_600_000L); val xb = x(ini + (hora + 1) * 3_600_000L)
                            drawRect(prim.copy(alpha = (0.12f + 0.8f * (v / m)).toFloat()), Offset(xa, y + h * 0.25f), Size(xb - xa - 1f, h * 0.5f))
                        }
                    }
                }
            }
            // Eje de horas y la hora actual.
            listOf(0, 6, 12, 18, 24).forEach { hh ->
                val xa = x(ini + hh * 3_600_000L)
                drawLine(linea, Offset(xa, 0f), Offset(xa, h * pistas.size), 0.6f)
                val r = medidor.measure(if (hh == 24) "0" else "$hh", estiloEje)
                drawText(r, topLeft = Offset((xa - r.size.width / 2).coerceIn(0f, size.width - r.size.width), h * pistas.size + 2f))
            }
            val ahora = System.currentTimeMillis()
            if (ahora in ini..fin) { val xa = x(ahora); drawLine(sec, Offset(xa, 0f), Offset(xa, h * pistas.size), 2f) }
        }
    }
    Ayuda("De 0:00 a las 5:00 del día siguiente. Lo sombreado es de noche.", Modifier.padding(top = 4.dp))
}

/** Abre el registro rápido de un aviso de ánimo. */
fun abrirAviso(ctx: Context) {
    ctx.startActivity(Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_ANIMO).putExtra(QuickActivity.ORIGEN, "app")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

@Composable
fun Portada(uri: String?) {
    if (uri == null) return
    Box(Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(6.dp))) {
        Miniatura(android.net.Uri.parse(uri), 900, Modifier.fillMaxWidth().aspectRatio(1.9f))
    }
}
