@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.esAnimo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
        // Los días sin llenar de la última semana (N-09): a cualquier hora, hasta que se llenen o se descarten hoy.
        val dias = faltantes(hoy)
        if (dias.isNotEmpty() && !ignorado("p:ayer"))
            l += Pendiente("p:ayer", if (dias == listOf(Fechas.sumar(hoy, -1))) "Diario de ayer" else "Te faltan " + nombresDias(dias), "ponerse")
        if (d.sueno_calidad == null && ahora.hour >= 5) l += Pendiente("p:sueno", "Calidad del sueño", "sueno")
        // Avisos de ánimo que ya sonaron y no contestaste.
        // Se cuentan los avisos sin respuesta (por su hora), no la resta de planeados menos registros.
        val faltan = io.github.ruddy2003.vestigium.data.AvisosAnimo.sinContestar().size
        if (faltan > 0) l += Pendiente("p:aviso", if (faltan == 1) "1 aviso de ánimo sin contestar" else "$faltan avisos de ánimo sin contestar", "aviso")
        if (!ignorado("p:escalas")) Escalas.pendientes().firstOrNull()?.let { (ins, atraso) ->
            l += Pendiente("p:escalas", if (atraso > 0) "${ins.nombre}: ${atraso} días de atraso" else "${ins.nombre} toca hoy", "escalas")
        }
        // La prueba de reacción, una vez al día (desde las 8, para no medir el cerebro recién despierto).
        if (ahora.hour >= 8 && Repo.reacciones.none { it.fecha == hoy } && !ignorado("p:reaccion")) l += Pendiente("p:reaccion", "Prueba de reacción", "reaccion")
        // Lo de la noche aparece desde 4 horas antes de tu hora de diario.
        val tarde = ahora.hour >= (c.hora_diario - 4).coerceAtLeast(14)
        if (tarde && (d.valencia == null || d.nota.isBlank())) l += Pendiente("p:diario", if (d.valencia == null) "Diario de hoy" else "Escribir la nota del día", "diario")
        if (tarde && d.prediccion_manana == null && !ignorado("p:prediccion")) l += Pendiente("p:prediccion", "Predicción para mañana", "prediccion")
        if (ahora.hour >= 20 && Repo.gastos.none { it.dia == hoy } && !ignorado("p:gasto")) l += Pendiente("p:gasto", "¿Gastaste algo hoy?", "gasto")
        // Una vez al año, la brújula política (se puede dejar para otro día).
        if (io.github.ruddy2003.vestigium.data.Politica.toca(hoy) && c.avisos_ignorados["p:politica"]?.let { it >= Fechas.sumar(hoy, -6) } != true)
            l += Pendiente("p:politica", "Brújula política del año", "politica")
        return l
    }

    /**
     * Días de los 7 anteriores sin valencia ni diario, desde que empezaste a usar la app (el primer día guardado).
     * Del más viejo al más nuevo.
     */
    fun faltantes(hoy: String): List<String> {
        val primero = Repo.dias.keys.minOrNull() ?: return emptyList()
        return (7 downTo 1).map { Fechas.sumar(hoy, -it.toLong()) }.filter { f ->
            f >= primero && Repo.dias[f].let { d -> d == null || (d.valencia == null && d.nota.isBlank()) }
        }
    }

    /** «jueves y viernes», «lunes, martes y miércoles». */
    fun nombresDias(l: List<String>): String {
        val n = l.map { Fechas.parse(it).dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("es")) }
        return if (n.size == 1) n[0] else n.dropLast(1).joinToString(", ") + " y " + n.last()
    }

    /** Si lo que pedía [clave] ya está hecho (y no solo dejó de pedirse por la hora o porque lo descartaste). */
    fun resuelto(clave: String, d: Dia, hoy: String): Boolean = when (clave) {
        "p:ayer" -> faltantes(hoy).isEmpty()
        "p:sueno" -> d.sueno_calidad != null
        "p:aviso" -> io.github.ruddy2003.vestigium.data.AvisosAnimo.sinContestar().isEmpty()
        "p:escalas" -> Escalas.pendientes().isEmpty()
        "p:reaccion" -> Repo.reacciones.any { it.fecha == hoy }
        "p:diario" -> d.valencia != null && d.nota.isNotBlank()
        "p:prediccion" -> d.prediccion_manana != null
        "p:gasto" -> Repo.gastos.any { it.dia == hoy }
        "p:politica" -> !io.github.ruddy2003.vestigium.data.Politica.toca(hoy)
        else -> false
    }
}

/** Lo único que «Te falta hoy» pide: el diario de ayer, el sueño, los avisos de ánimo y el diario. Lo demás es una invitación. */
val ESENCIALES = setOf("p:ayer", "p:sueno", "p:aviso", "p:diario")

@Composable
fun TeFaltaHoy(d: Dia, hoy: String, ocultar: Set<String> = emptySet(), accion: (String) -> Unit) {
    val todo = Pendientes.deHoy(d, hoy).filter { it.clave !in ocultar }
    val l = todo.filter { it.clave in ESENCIALES }
    val extra = todo.filter { it.clave !in ESENCIALES }
    val tarde = LocalTime.now().hour >= 21
    // Cuando completas lo esencial, una pequeña celebración con el sonido y la vibración del tema. Solo si lo que faltaba
    // de verdad se hizo (no si se venció su hora, como «diario de ayer» a las 14:00) y no justo al volver a abrir la app.
    val sentir = io.github.ruddy2003.vestigium.ui.rememberSentir()
    var antes by remember(hoy) { mutableStateOf<Set<String>?>(null) }
    val claves = l.map { it.clave }.toSet()
    androidx.compose.runtime.LaunchedEffect(claves) {
        val previo = antes
        antes = claves
        if (previo != null && previo.isNotEmpty() && claves.isEmpty() && !io.github.ruddy2003.vestigium.ui.Reanudar.recien() &&
            previo.all { Pendientes.resuelto(it, d, hoy) }) {
            io.github.ruddy2003.vestigium.ui.Fiesta.lanzar(); sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.LOGRO)
        }
    }
    // Las invitaciones: una línea discreta que se puede descartar por hoy.
    val invitaciones: @Composable (Color) -> Unit = { tinta ->
        if (extra.isNotEmpty()) {
            Text("si tienes tiempo:", style = MaterialTheme.typography.labelMedium, color = tinta.copy(alpha = 0.8f), modifier = Modifier.padding(top = 8.dp))
            FlowRow(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                extra.forEach { p ->
                    Text(p.texto.removePrefix("¿").removeSuffix("?").replaceFirstChar { it.lowercase() } + " →", style = MaterialTheme.typography.labelLarge, color = tinta,
                        modifier = Modifier.clickable { accion(p.accion) }.padding(vertical = 4.dp))
                }
                Text("hoy no", style = MaterialTheme.typography.labelLarge, color = tinta.copy(alpha = 0.6f),
                    modifier = Modifier.clickable { Repo.cambiarConfig { c -> c.copy(avisos_ignorados = c.avisos_ignorados + extra.associate { it.clave to hoy },
                        hoy_no = (c.hoy_no.filter { it >= Fechas.sumar(hoy, -10) } + hoy).distinct()) } }.padding(vertical = 4.dp))
            }
        }
    }
    if (l.isEmpty()) {
        if (tarde && d.valencia != null) Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            Text("✓ Hoy completo. Buenas noches.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
        } else if (extra.isNotEmpty()) Column(Modifier.fillMaxWidth().padding(top = 4.dp)) { invitaciones(MaterialTheme.colorScheme.primary) }
        return
    }
    Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(Estilo.seccion("Te falta hoy", null).first, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                l.forEach { p -> Ficha("${p.texto} →", false) { accion(p.accion) } }
            }
            // El diario de ayer se puede dejar pasar.
            if (l.any { it.clave == "p:ayer" }) TextButton(onClick = {
                Repo.cambiarConfig { c -> c.copy(avisos_ignorados = c.avisos_ignorados + ("p:ayer" to hoy)) }
            }) { Text("hoy no: " + (l.first { it.clave == "p:ayer" }.texto.removePrefix("Te faltan ").lowercase()), style = MaterialTheme.typography.labelMedium) }
            invitaciones(MaterialTheme.colorScheme.onPrimaryContainer)
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
        // Lo que no se pudo leer al abrir la app.
        Repo.configIlegible?.let { ts ->
            l += Falta("config_ilegible", "Tus ajustes no se pudieron leer al abrir la app y quedaron como nuevos. Guardé el original aparte (${Fechas.corto(ts.take(10))}); para recuperar hábitos, personas y perfil, restaura un respaldo.", "datos") { null }
        }
        Repo.ilegibles.values.sum().takeIf { it > 0 }?.let { n ->
            l += Falta("ilegibles", "$n registros guardados no se pudieron leer. Mira de qué tipo en Estado de los datos.", "estado") { null }
        }
        val respaldo = listOfNotNull(c.respaldo_ultimo?.takeIf { c.carpeta_respaldo != null }, c.drive_ultimo?.takeIf { c.drive }).maxOrNull()
        if ((c.carpeta_respaldo != null || c.drive) && io.github.ruddy2003.vestigium.data.Respaldos.bloqueado(ctx))
            l += Falta("respaldo", io.github.ruddy2003.vestigium.data.Respaldos.PAUSA, "datos") { null }
        else if ((c.carpeta_respaldo != null || c.drive) && (respaldo == null || System.currentTimeMillis() - Fechas.millis(respaldo) > 36 * 3_600_000L))
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
fun DatosQueFaltan(nav: io.github.ruddy2003.vestigium.ui.Nav, compacto: Boolean = false) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var version by remember { mutableStateOf(0) }
    val faltas by produceState(emptyList<Falta>(), version, Repo.config.avisos_ignorados) { value = runCatching { Faltas.revisar(ctx) }.getOrDefault(emptyList()) }
    var msg by remember { mutableStateOf<String?>(null) }
    if (faltas.isEmpty() && msg == null) return
    val hoy = Fechas.diaLogico()
    // En Hoy, solo un contador que se despliega (N-16).
    var abierto by remember { mutableStateOf(!compacto) }
    if (!abierto) {
        TextButton(onClick = { abierto = true }) {
            Text(if (faltas.size == 1) "1 dato que falta →" else "${faltas.size} datos que faltan →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
        }
        return
    }
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
                        "estado" -> nav.ir("estado_datos")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("— ${f.autor}", style = ty.labelSmall, color = cs.background.copy(alpha = 0.7f), modifier = Modifier.weight(1f))
                        CorazonFrase(f, cs.primary, fecha = fecha)
                    }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("— ${f.autor}", style = ty.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
                CorazonFrase(f, cs.primary, fecha = fecha)
            }
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
        Repo.dias[f]?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == f && it.esAnimo }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average()
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
fun Tablero(fecha: String, max: Int? = null, tocar: (String) -> Unit = {}) {
    var editando by remember { mutableStateOf(false) }
    // Con [max], solo las primeras cifras y «ver todo» (N-16).
    var todo by remember { mutableStateOf(max == null) }
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
    val completa = elegidas.mapNotNull { id -> METRICAS.firstOrNull { it.id == id } }
    val lista = if (todo || max == null) completa else completa.take(max)
    lista.chunked(2).forEach { par ->
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            par.forEach { m -> TarjetaMetrica(m, fecha, extra, Modifier.weight(1f)) { tocar(m.id) } }
            if (par.size == 1) Spacer(Modifier.weight(1f))
        }
    }
    if (!todo && completa.size > lista.size) {
        TextButton(onClick = { todo = true }) { Text("ver todo (${completa.size}) ▾", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary) }
        return
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
private fun TarjetaMetrica(m: Metrica, fecha: String, extra: Map<String, Map<String, Double>>, modifier: Modifier, tocar: () -> Unit) {
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
    // Tocar la cifra lleva a donde se corrige o se ve de dónde salió (P1).
    Surface(onClick = tocar, modifier = modifier, shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(m.nombre.lowercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, modifier = Modifier.weight(1f))
                Text("›", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            }
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
    // La gótica y las capitulares navideñas son para títulos; las cifras se leen mejor en Garamond.
    TemaId.RENACIMIENTO -> MaterialTheme.typography.titleLarge.copy(fontFamily = io.github.ruddy2003.vestigium.ui.LetraExtra.cormorant, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = androidx.compose.ui.unit.TextUnit(25f, androidx.compose.ui.unit.TextUnitType.Sp), letterSpacing = androidx.compose.ui.unit.TextUnit(0f, androidx.compose.ui.unit.TextUnitType.Sp), fontFeatureSettings = "lnum")
    TemaId.MEDIEVAL, TemaId.NAVIDAD, TemaId.GOTICO -> MaterialTheme.typography.titleLarge.copy(fontFamily = io.github.ruddy2003.vestigium.ui.Letra.garamond, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
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
    val tramos = Repo.tramos.filter { it.fecha == fecha }
    if (tramos.isNotEmpty()) pistas += Pista("enfoque", tramos.map { t -> val a = Fechas.millis(t.inicio); Triple(a, a + t.min * 60_000L, if (t.tipo == "siesta") sec else prim) },
        etiquetas = tramos.map { t -> Fechas.millis(t.inicio) to io.github.ruddy2003.vestigium.data.Catalogo.nombreTramo(t.tipo).lowercase() })
    val mom = Repo.momentos.filter { it.fecha == fecha && !it.privado }
    if (mom.isNotEmpty()) pistas += Pista("momentos", marcas = mom.map { m -> Fechas.millis(m.ts) to (Tonos.valencia(m.valencia?.toDouble(), oscuro) ?: sec) })
    Repo.notifs[fecha]?.por_hora?.takeIf { it.sum() > 0 }?.let { h -> pistas += Pista("teléfono", calor = h.map { it.toDouble() }) }
    Repo.conexiones[fecha]?.let { cx -> if (cx.luz_n.sum() > 0) pistas += Pista("luz", calor = cx.luz_suma.indices.map { i -> if (cx.luz_n[i] > 0) kotlin.math.ln(1 + cx.luz_suma[i] / cx.luz_n[i]) else 0.0 }) }
    if (musica.isNotEmpty()) pistas += Pista("música", marcas = musica.map { it to prim })
    if (fotos.isNotEmpty()) pistas += Pista("fotos", marcas = fotos.map { it to ter })
    val rojo = MaterialTheme.colorScheme.error
    val gastos = Repo.gastos.filter { it.dia == fecha }
    if (gastos.isNotEmpty()) pistas += Pista("gastos", marcas = gastos.mapNotNull { g ->
        runCatching { Fechas.millisDe(java.time.LocalDateTime.of(Fechas.parse(g.fecha), LocalTime.parse(g.hora.take(5)))) to rojo }.getOrNull()
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

/**
 * Abre el registro rápido del aviso de ánimo más antiguo sin contestar: la respuesta se guarda como respuesta a ese aviso
 * («muestreo», con su hora), no como un registro propio, y la tarjeta se apaga al contestarlos.
 */
fun abrirAviso(ctx: Context) {
    val i = Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, QuickActivity.M_ANIMO).putExtra(QuickActivity.ORIGEN, "app")
    io.github.ruddy2003.vestigium.data.AvisosAnimo.sinContestar().firstOrNull()?.let { i.putExtra(QuickActivity.AVISO_MS, it) }
    ctx.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

// =====================================================================
//  Tus últimos días (N-28)
// =====================================================================

/**
 * «Tus últimos días»: los 13 días anteriores y hoy como hilos verticales, en lugar del personaje.
 * - Color: la valencia del día, con la paleta divergente del calendario (`Tonos.valencia`).
 * - Alto: las horas de sueño de esa noche frente a tu normal (promedio de los 28 días anteriores; 7 h si hay menos de 5).
 * - Remate: un sello si el día tiene valencia y diario; un punto si solo tiene diario. Un día sin nada queda hueco.
 * - Hoy se va llenando (sueño → ánimo → diario) y no muestra su color hasta tener valencia y diario, para no influir en
 *   cómo lo calificas (capítulo 3, sección 3.14). Por lo mismo, la franja no sale en la hoja de la noche.
 * Solo usa la valencia, el sueño y si hay diario: nada privado. Cada tema la dibuja a su manera.
 */
@Composable
fun FranjaDias(hoy: String, abrir: (String) -> Unit, ponerse: () -> Unit) {
    if (!Repo.config.personaje) return
    val oscuro = esOscuro()
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val dias = (13 downTo 0).map { Fechas.sumar(hoy, -it.toLong()) }
    val normal = (1..28).mapNotNull { Repo.salud[Fechas.sumar(hoy, -it.toLong())]?.sueno_min }.takeIf { it.size >= 5 }?.average() ?: 420.0
    val oro = Tonos.secuencial(1f, oscuro)
    val letras = listOf("L", "M", "M", "J", "V", "S", "D")
    Column(Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Etiqueta("Tus últimos días")
        Row(Modifier.fillMaxWidth().height(118.dp).padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            dias.forEach { f ->
                val d = Repo.dias[f]
                val esHoy = f == hoy
                val conDiario = d?.nota?.isNotBlank() == true
                val completo = d?.valencia != null && conDiario
                val sueno = Repo.salud[f]?.sueno_min
                val alto = ((sueno?.div(normal))?.toFloat() ?: 0.8f).coerceIn(0.45f, 1.25f) / 1.25f
                val color = if (esHoy && !completo) null else Tonos.valencia(d?.valencia?.toDouble(), oscuro)
                val avance = if (!esHoy) 0f else listOf(d?.sueno_calidad != null || sueno != null, d?.valencia != null, conDiario).count { it } / 3f
                val marca = if (completo) 2 else if (conDiario) 1 else 0
                Canvas(Modifier.weight(1f).fillMaxHeight().clickable { abrir(f) }) {
                    val w = size.width; val h = size.height
                    val remate = 14.dp.toPx()
                    val barH = (h - remate) * alto
                    val top = h - barH
                    val borde = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx())
                    val linea = if (esHoy) cs.primary else cs.outline
                    when (tema) {
                        // Columnas: fuste, capitel y basa.
                        TemaId.ROMANO -> {
                            val fx = w * 0.18f; val fw = w * 0.64f; val cap = 3.dp.toPx()
                            if (color != null) drawRect(color, Offset(fx, top + cap), Size(fw, barH - 2 * cap)) else drawRect(linea, Offset(fx, top + cap), Size(fw, barH - 2 * cap), style = borde)
                            if (color != null) for (k in 1..2) drawLine(Color.Black.copy(alpha = 0.18f), Offset(fx + fw * k / 3, top + cap), Offset(fx + fw * k / 3, h - cap), 1.dp.toPx())
                            drawRect(color ?: linea, Offset(0f, top), Size(w, cap), style = if (color == null) borde else androidx.compose.ui.graphics.drawscope.Fill)
                            drawRect(color ?: linea, Offset(0f, h - cap), Size(w, cap), style = if (color == null) borde else androidx.compose.ui.graphics.drawscope.Fill)
                        }
                        // Barras con líneas de escaneo.
                        TemaId.CIBER -> {
                            if (color != null) {
                                drawRect(color, Offset(0f, top), Size(w, barH))
                                var y = top + 2.dp.toPx()
                                while (y < h) { drawLine(Color.Black.copy(alpha = 0.28f), Offset(0f, y), Offset(w, y), 1f); y += 3.dp.toPx() }
                            } else drawRect(linea, Offset(0f, top), Size(w, barH), style = borde)
                        }
                        // Franjas iluminadas: arco arriba y filete dorado.
                        TemaId.MEDIEVAL, TemaId.GOTICO -> {
                            val r = CornerRadius(w / 2, w / 2)
                            if (color != null) {
                                drawRoundRect(color, Offset(0f, top), Size(w, barH), r)
                                drawRoundRect(oro, Offset(0f, top), Size(w, barH), r, style = androidx.compose.ui.graphics.drawscope.Stroke(1.5.dp.toPx()))
                            } else drawRoundRect(linea, Offset(0f, top), Size(w, barH), r, style = borde)
                        }
                        else -> {
                            val r = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                            if (color != null) drawRoundRect(color, Offset(0f, top), Size(w, barH), r) else drawRoundRect(linea, Offset(0f, top), Size(w, barH), r, style = borde)
                        }
                    }
                    // Hoy: se llena desde abajo con lo que ya anotaste, sin el color del ánimo.
                    if (esHoy && color == null && avance > 0f) drawRect(cs.primary.copy(alpha = 0.35f), Offset(0f, h - barH * avance), Size(w, barH * avance))
                    val cx = w / 2; val cy = remate / 2
                    when (marca) {
                        2 -> { drawCircle(if (tema == TemaId.MEDIEVAL || tema == TemaId.GOTICO) oro else cs.primary, w.coerceAtMost(remate) * 0.42f, Offset(cx, cy))
                               drawCircle(cs.background, w.coerceAtMost(remate) * 0.16f, Offset(cx, cy)) }
                        1 -> drawCircle(cs.onSurfaceVariant, 2.dp.toPx(), Offset(cx, cy))
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            dias.forEach { f ->
                Text(letras[Fechas.parse(f).dayOfWeek.value - 1], style = MaterialTheme.typography.labelSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = if (f == hoy) cs.primary else cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            }
        }
        // La semana en una línea, contra la anterior. Hoy cuenta solo si ya tiene valencia y diario.
        fun valencias(desde: Int, hasta: Int) = (desde..hasta).mapNotNull { k ->
            val f = Fechas.sumar(hoy, -k.toLong()); val d = Repo.dias[f]
            if (k == 0 && (d?.valencia == null || d.nota.isBlank())) null else d?.valencia
        }
        val esta = valencias(0, 6).takeIf { it.isNotEmpty() }?.average()
        val antes = valencias(7, 13).takeIf { it.isNotEmpty() }?.average()
        val sueno = (0..6).mapNotNull { Repo.salud[Fechas.sumar(hoy, -it.toLong())]?.sueno_min }.takeIf { it.isNotEmpty() }?.average()?.toInt()
        val faltan = Pendientes.faltantes(hoy).size
        val partes = listOfNotNull(
            esta?.let { e -> "ánimo ${"%.1f".format(e)}" + (antes?.let { a -> val x = e - a; if (abs(x) >= 0.05) " (${if (x > 0) "↑" else "↓"}${"%.1f".format(abs(x))})" else "" } ?: "") },
            sueno?.let { "sueño ${it / 60} h ${"%02d".format(it % 60)}" },
        )
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (partes.isEmpty()) "Esta semana todavía no hay datos." else "esta semana: " + partes.joinToString(" · "),
                style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            if (faltan > 0) TextButton(onClick = ponerse) {
                Text(if (faltan == 1) "1 día sin llenar →" else "$faltan días sin llenar →", style = MaterialTheme.typography.labelMedium, color = cs.primary)
            }
        }
    }
}
