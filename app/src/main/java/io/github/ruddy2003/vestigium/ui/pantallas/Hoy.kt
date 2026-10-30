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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
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
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaMeta
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import io.github.ruddy2003.vestigium.ui.rejillaGrafica
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

/**
 * La frase del día con el marco de cada tema: una tabula ansata en el romano, la consola en la terminal, meandros en el
 * helénico, el cartel en la distopía, el recorte de gaceta en Colonos, el sello de cinabrio en la Ruta de la Seda…
 * Lo de adentro (rótulo, original, texto, autor y corazón) lo arma [CuerpoFrase].
 */
@Composable
fun FraseDelDia(fecha: String) {
    val tema = Estilo.tema
    val f = Frases.del(fecha, tema)
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    val rotulo = Frases.rotulo(tema)
    val arriba = Modifier.fillMaxWidth().padding(top = 12.dp)
    when (tema) {
        // Tabula ansata: placa con doble moldura y colas de milano; el texto centrado, como inscripción.
        TemaId.ROMANO -> Box(arriba.drawBehind {
            val u = 1.dp.toPx(); val w = size.width; val h = size.height; val a = 10 * u
            drawRect(cs.primary.copy(alpha = 0.7f), Offset(a, 0f), Size(w - 2 * a, h), style = Stroke(1.2f * u))
            drawRect(cs.primary.copy(alpha = 0.35f), Offset(a + 3 * u, 3 * u), Size(w - 2 * a - 6 * u, h - 6 * u), style = Stroke(0.6f * u))
            listOf(a to -1f, w - a to 1f).forEach { (x, d) ->
                val p = Path().apply { moveTo(x, h / 2); lineTo(x + d * a, h / 2 - a); lineTo(x + d * a, h / 2 + a); close() }
                drawPath(p, cs.primary.copy(alpha = 0.18f)); drawPath(p, cs.primary.copy(alpha = 0.7f), style = Stroke(1.2f * u))
            }
        }.padding(horizontal = 24.dp, vertical = 14.dp)) {
            CuerpoFrase(f, fecha, rotulo.replace(" ", " · "), "", "", centrado = true)
        }
        // El recuadro de la cita en el periódico: raya gruesa arriba, fina abajo y las comillas grandes al margen.
        TemaId.URBE -> Box(arriba.drawBehind {
            val u = 1.dp.toPx(); val w = size.width; val h = size.height
            drawLine(cs.onBackground.copy(alpha = 0.75f), Offset(0f, 1.5f * u), Offset(w, 1.5f * u), 3 * u)
            drawLine(cs.onBackground.copy(alpha = 0.5f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 1 * u)
            drawRect(cs.primary.copy(alpha = 0.7f), Offset(0f, 9 * u), Size(3 * u, h - 18 * u))
        }.padding(start = 14.dp, end = 8.dp, top = 14.dp, bottom = 12.dp)) {
            CuerpoFrase(f, fecha, rotulo, "“", "”")
        }
        // Consola: fondo de pantalla, el texto como salida de un comando y el cursor al final.
        TemaId.CIBER -> Surface(color = cs.surfaceVariant, shape = RectangleShape, border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.35f)), modifier = arriba) {
            Box(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) { CuerpoFrase(f, fecha, rotulo, "", " ▌", autor = "-- ${f.autor}") }
        }
        // El cartel del puesto: una placa redondeada con el tubo magenta y la frase entre corchetes japoneses.
        TemaId.NEON -> Surface(color = cs.surfaceVariant, shape = RoundedCornerShape(18.dp), border = BorderStroke(1.5.dp, cs.primary.copy(alpha = 0.8f)), modifier = arriba) {
            Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) { CuerpoFrase(f, fecha, rotulo, "「", "」", autor = "◆ ${f.autor}") }
        }
        // Meandros arriba y abajo; el texto centrado.
        TemaId.HELENO -> Column(arriba) {
            Canvas(Modifier.fillMaxWidth().height(8.dp)) { meandroFrase(cs.primary.copy(alpha = 0.7f)) }
            Box(Modifier.padding(horizontal = 6.dp, vertical = 8.dp)) { CuerpoFrase(f, fecha, rotulo, "«", "»", centrado = true) }
            Canvas(Modifier.fillMaxWidth().height(8.dp)) { meandroFrase(cs.primary.copy(alpha = 0.7f)) }
        }
        // El friso de lotos y capullos arriba, la enseñanza con su ankh y la cenefa de colores abajo.
        TemaId.EGIPTO -> Column(arriba) {
            Canvas(Modifier.fillMaxWidth().height(10.dp)) { frisoLotoFrase(cs.primary.copy(alpha = 0.7f), cs.secondary.copy(alpha = 0.7f)) }
            Box(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) { CuerpoFrase(f, fecha, rotulo, "☥ ", "") }
            Canvas(Modifier.fillMaxWidth().height(5.dp)) {
                val u = 1.dp.toPx(); val colores = listOf(cs.primary, cs.tertiary, cs.secondary, cs.tertiary)
                var x = 0f; var k = 0
                while (x < size.width) { drawRect(colores[k % 4].copy(alpha = 0.65f), Offset(x, 0f), Size(minOf(5 * u, size.width - x), size.height)); x += 6 * u; k++ }
            }
        }
        // Doble filete de rúbrica y oro a la izquierda, y la capitular iluminada en gótica.
        TemaId.MEDIEVAL -> Row(arriba.height(IntrinsicSize.Min)) {
            Box(Modifier.width(3.dp).fillMaxHeight().background(cs.primary))
            Box(Modifier.padding(start = 2.dp).width(1.dp).fillMaxHeight().background(cs.tertiary))
            Box(Modifier.weight(1f).padding(start = 10.dp)) { CuerpoFrase(f, fecha, "¶ $rotulo", "", "", capitular = io.github.ruddy2003.vestigium.ui.LetraExtra.gotica) }
        }
        // La lectura del refectorio: el escudo de la casa a la izquierda y la barra de acero de la sobreveste encima.
        TemaId.TEMPLARIO -> Column(arriba) {
            Box(Modifier.fillMaxWidth().height(2.dp).background(cs.secondary.copy(alpha = 0.6f)))
            Row(Modifier.padding(top = 8.dp)) {
                Canvas(Modifier.padding(top = 2.dp, end = 10.dp).size(22.dp, 26.dp)) {
                    val w = size.width; val h = size.height
                    val escudo = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); quadraticTo(w * 1.04f, h * 0.62f, w / 2, h); quadraticTo(-w * 0.04f, h * 0.62f, 0f, 0f); close() }
                    drawPath(escudo, cs.surface); drawPath(escudo, cs.secondary, style = Stroke(1.2.dp.toPx()))
                    drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(w / 2, h * 0.4f), w * 0.36f), cs.primary)
                }
                Box(Modifier.weight(1f)) { CuerpoFrase(f, fecha, "☩ $rotulo", "“", "”", capitular = LetraExtra.cruzada) }
            }
        }
        // Cartel de propaganda: fondo negro, letras claras y una franja roja.
        TemaId.DISTOPIA -> Surface(color = cs.onBackground, shape = RectangleShape, modifier = arriba) {
            Row(Modifier.height(IntrinsicSize.Min)) {
                Box(Modifier.width(8.dp).fillMaxHeight().background(cs.primary))
                Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Text(rotulo, style = ty.labelMedium, color = cs.primary)
                    f.original?.let { Text(it, style = ty.bodySmall, fontStyle = FontStyle.Italic, color = cs.background.copy(alpha = 0.72f)) }
                    Text(f.texto.uppercase(), style = ty.titleLarge, color = cs.background, modifier = Modifier.padding(vertical = 2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("— ${f.autor}", style = ty.labelSmall, color = cs.background.copy(alpha = 0.7f), modifier = Modifier.weight(1f))
                        CorazonFrase(f, cs.primary, fecha = fecha)
                    }
                }
            }
        }
        // Tarjeta redonda de los cincuenta con el rótulo en una banda de color.
        TemaId.REFUGIO -> Surface(shape = RoundedCornerShape(12.dp), color = cs.surface, border = BorderStroke(2.dp, cs.primary), modifier = arriba) {
            Column {
                Box(Modifier.fillMaxWidth().background(cs.primary).padding(horizontal = 12.dp, vertical = 4.dp)) { Text(rotulo, style = ty.labelMedium, color = cs.onPrimary) }
                Box(Modifier.padding(12.dp)) { CuerpoFrase(f, fecha, null, "“", "”") }
            }
        }
        // Placa de acero de esquinas cortadas, con remaches de latón.
        TemaId.ORDEN -> Surface(shape = CutCornerShape(10.dp), color = cs.surface, border = BorderStroke(1.5.dp, cs.primary.copy(alpha = 0.7f)), modifier = arriba) {
            Box(Modifier.drawBehind {
                val u = 1.dp.toPx(); val w = size.width; val h = size.height
                listOf(Offset(16 * u, 5 * u), Offset(w - 16 * u, 5 * u), Offset(16 * u, h - 5 * u), Offset(w - 16 * u, h - 5 * u)).forEach { drawCircle(cs.secondary, 2.2f * u, it) }
            }.padding(horizontal = 18.dp, vertical = 14.dp)) { CuerpoFrase(f, fecha, rotulo, "«", "»") }
        }
        // Etiqueta de laboratorio con una regla graduada arriba.
        TemaId.LABORATORIO -> Surface(shape = RoundedCornerShape(14.dp), color = cs.surface, border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.4f)), modifier = arriba) {
            Column {
                Canvas(Modifier.fillMaxWidth().height(10.dp)) {
                    val u = 1.dp.toPx(); var x = 14 * u; var i = 0
                    while (x < size.width - 14 * u) { drawLine(cs.primary.copy(alpha = 0.6f), Offset(x, 0f), Offset(x, (if (i % 5 == 0) 8f else 4f) * u), 0.8f * u); x += 4 * u; i++ }
                }
                Box(Modifier.padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 12.dp)) { CuerpoFrase(f, fecha, rotulo, "“", "”") }
            }
        }
        // Recorte de gaceta: filete grueso y fino arriba, fino abajo.
        TemaId.COLONOS -> Column(arriba) {
            HorizontalDivider(thickness = 2.dp, color = cs.onSurface)
            HorizontalDivider(Modifier.padding(top = 2.dp), thickness = 0.5.dp, color = cs.onSurface)
            Box(Modifier.padding(vertical = 6.dp)) { CuerpoFrase(f, fecha, rotulo.uppercase(), "“", "”") }
            HorizontalDivider(thickness = 0.5.dp, color = cs.onSurface)
        }
        // Boletín: un filete de Didot (grueso al centro, fino en las puntas) arriba y abajo; el texto centrado.
        TemaId.NAPOLEON -> Column(arriba, horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(Modifier.fillMaxWidth(0.6f).height(6.dp)) { fileteDidot(cs.tertiary) }
            Box(Modifier.padding(vertical = 8.dp)) { CuerpoFrase(f, fecha, rotulo, "« ", " »", centrado = true) }
            Canvas(Modifier.fillMaxWidth(0.6f).height(6.dp)) { fileteDidot(cs.tertiary) }
        }
        // Etiqueta de herbario clavada con un alfiler.
        TemaId.BOTANICO -> Surface(shape = RoundedCornerShape(4.dp), color = cs.surface, border = BorderStroke(1.dp, cs.outline), modifier = arriba) {
            Box(Modifier.drawBehind {
                val u = 1.dp.toPx(); val o = Offset(size.width - 18 * u, 9 * u)
                drawLine(cs.onSurfaceVariant.copy(alpha = 0.7f), o, o + Offset(-6 * u, 7 * u), 0.8f * u)
                drawCircle(cs.secondary, 3 * u, o)
            }.padding(start = 14.dp, end = 30.dp, top = 12.dp, bottom = 10.dp)) { CuerpoFrase(f, fecha, rotulo, "“", "”") }
        }
        // Una nota pegada al muro con un trozo de cinta, en una losa desportillada, con una grieta en la esquina.
        TemaId.RUINAS -> Surface(shape = CutCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomEnd = 0.dp, bottomStart = 4.dp), color = cs.surface, border = BorderStroke(1.dp, cs.outline), modifier = arriba) {
            Box(Modifier.drawBehind {
                val u = 1.dp.toPx(); val m = size.width / 2
                drawPath(Path().apply { moveTo(m - 26 * u, -2 * u); lineTo(m + 26 * u, -6 * u); lineTo(m + 27 * u, 6 * u); lineTo(m - 25 * u, 10 * u); close() }, cs.onSurfaceVariant.copy(alpha = 0.18f))
                drawPath(Path().apply { moveTo(size.width, size.height - 18 * u); lineTo(size.width - 6 * u, size.height - 12 * u); lineTo(size.width - 3 * u, size.height - 6 * u); lineTo(size.width - 9 * u, size.height) },
                    cs.onSurfaceVariant.copy(alpha = 0.4f), style = Stroke(0.8f * u))
            }.padding(start = 14.dp, end = 22.dp, top = 14.dp, bottom = 10.dp)) { CuerpoFrase(f, fecha, rotulo, "“", "”") }
        }
        // Tarjeta de felicitación con una ramita de acebo en la esquina.
        TemaId.NAVIDAD -> Surface(shape = RoundedCornerShape(10.dp), color = cs.primaryContainer.copy(alpha = 0.45f), border = BorderStroke(1.dp, cs.secondary.copy(alpha = 0.6f)), modifier = arriba) {
            Box(Modifier.drawBehind {
                val u = 1.dp.toPx(); val o = Offset(size.width - 16 * u, 12 * u)
                drawPath(hojaFrase(o, o + Offset(-10 * u, -2 * u), 2.6f * u), cs.secondary)
                drawPath(hojaFrase(o, o + Offset(-2 * u, 9 * u), 2.6f * u), cs.secondary)
                listOf(Offset(0f, 0f), Offset(2.4f, -1f), Offset(1f, 2.4f)).forEach { drawCircle(cs.primary, 1.8f * u, o + it * u) }
            }.padding(horizontal = 16.dp, vertical = 14.dp)) { CuerpoFrase(f, fecha, rotulo, "“", "”", centrado = true) }
        }
        // El sello de cinabrio a la derecha, como en los rollos.
        TemaId.SEDA -> Row(arriba, verticalAlignment = Alignment.Top) {
            Box(Modifier.weight(1f)) { CuerpoFrase(f, fecha, rotulo, "「", "」") }
            Canvas(Modifier.padding(start = 10.dp, top = 2.dp).size(28.dp)) {
                val u = 1.dp.toPx(); val s = size.width
                drawRect(cs.secondary, Offset.Zero, Size(s, s))
                drawRect(cs.surface, Offset(2.5f * u, 2.5f * u), Size(s - 5 * u, s - 5 * u), style = Stroke(1.2f * u))
                drawLine(cs.surface, Offset(s / 2, 6 * u), Offset(s / 2, s - 6 * u), 1.4f * u)
                drawLine(cs.surface, Offset(7 * u, s * 0.38f), Offset(s - 7 * u, s * 0.38f), 1.4f * u)
                drawRect(cs.surface, Offset(8 * u, s * 0.55f), Size(s - 16 * u, s * 0.2f), style = Stroke(1.2f * u))
            }
        }
        // Ventana ojival: el marco termina en punta y el texto va centrado.
        TemaId.GOTICO -> Surface(shape = OJIVA_FRASE, color = cs.surface, border = BorderStroke(1.5.dp, cs.primary.copy(alpha = 0.7f)), modifier = arriba) {
            Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp, bottom = 12.dp)) { CuerpoFrase(f, fecha, rotulo, "«", "»", centrado = true) }
        }
        // Un recorte de periódico pegado con dos tiras de cinta: la orilla de arriba rasgada.
        TemaId.ARCANO -> Surface(shape = RECORTE_FRASE, color = cs.surface, border = BorderStroke(1.dp, cs.onSurface.copy(alpha = 0.35f)), modifier = arriba) {
            Box(Modifier.drawBehind {
                val u = 1.dp.toPx()
                listOf(Offset(14 * u, 8 * u) to -30f, Offset(size.width - 14 * u, 8 * u) to 30f).forEach { (o, g) ->
                    rotate(g, o) { drawRect(cs.secondary.copy(alpha = 0.22f), Offset(o.x - 16 * u, o.y - 5 * u), Size(32 * u, 10 * u)) }
                }
            }.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp)) { CuerpoFrase(f, fecha, rotulo, "—", "") }
        }
        // Pantalla de la sala de control: miras en las esquinas.
        TemaId.ESPACIO -> Box(arriba.drawBehind {
            val u = 1.dp.toPx(); val w = size.width; val h = size.height; val k = cs.primary.copy(alpha = 0.8f); val l = 10 * u
            listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f))
                .forEach { (o, d) -> drawLine(k, o, o + Offset(d.x * l, 0f), 1.5f * u); drawLine(k, o, o + Offset(0f, d.y * l), 1.5f * u) }
            listOf(0f to 1f, w to -1f).forEach { (x, d) -> drawLine(k.copy(alpha = 0.5f), Offset(x, h / 2), Offset(x + d * 5 * u, h / 2), u) }
        }.padding(horizontal = 16.dp, vertical = 14.dp)) { CuerpoFrase(f, fecha, rotulo, "“", "”") }
        // La frase entre dos superficies onduladas, con tres burbujas que suben por la izquierda.
        TemaId.ABISMO -> Box(arriba.drawBehind {
            val u = 1.dp.toPx(); val w = size.width; val h = size.height; val k = cs.primary.copy(alpha = 0.7f)
            listOf(0f, h).forEach { y0 -> val p = Path(); var x = 0f; p.moveTo(0f, y0); while (x <= w) { p.lineTo(x, y0 + 2 * u * kotlin.math.sin(x / (9 * u))); x += 3 * u }; drawPath(p, k, style = Stroke(1.2f * u)) }
            listOf(0.75f to 2.4f, 0.5f to 3.2f, 0.25f to 1.8f).forEach { (fy, r) -> drawCircle(cs.tertiary.copy(alpha = 0.7f), r * u, Offset(6 * u, h * fy), style = Stroke(0.9f * u)) }
        }.padding(horizontal = 18.dp, vertical = 16.dp)) { CuerpoFrase(f, fecha, rotulo, "«", "»") }
        // Arco de herradura con la estrella de ocho puntas en la clave.
        TemaId.ANDALUS -> Surface(shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp, bottomStart = 4.dp, bottomEnd = 4.dp), color = cs.surface,
            border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.6f)), modifier = arriba) {
            Box(Modifier.drawBehind {
                val u = 1.dp.toPx()
                drawPath(estrellaFrase(Offset(size.width / 2, 14 * u), 8, 7 * u, 5.4f * u), cs.tertiary.copy(alpha = 0.8f), style = Stroke(1.1f * u))
            }.padding(start = 18.dp, end = 18.dp, top = 28.dp, bottom = 12.dp)) { CuerpoFrase(f, fecha, rotulo, "«", "»", centrado = true) }
        }
        // Fieltro con su costura punteada por dentro.
        TemaId.MONGOL -> Surface(shape = RoundedCornerShape(8.dp), color = cs.surfaceVariant, border = BorderStroke(2.dp, cs.primary.copy(alpha = 0.8f)), modifier = arriba) {
            Box(Modifier.drawBehind {
                val u = 1.dp.toPx()
                drawRoundRect(cs.primary.copy(alpha = 0.55f), Offset(5 * u, 5 * u), Size(size.width - 10 * u, size.height - 10 * u), CornerRadius(4 * u, 4 * u),
                    style = Stroke(u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.5f * u))))
            }.padding(16.dp)) { CuerpoFrase(f, fecha, rotulo, "«", "»") }
        }
        // Nota al margen del cuaderno: a la izquierda, una regla con un círculo de compás.
        TemaId.RENACIMIENTO -> Row(arriba.height(IntrinsicSize.Min)) {
            Canvas(Modifier.width(18.dp).fillMaxHeight()) {
                val u = 1.dp.toPx(); val k = cs.primary.copy(alpha = 0.6f); val x = size.width / 2
                drawCircle(k, 5 * u, Offset(x, 6 * u), style = Stroke(0.8f * u))
                drawLine(k, Offset(x - 7 * u, 6 * u), Offset(x + 7 * u, 6 * u), 0.6f * u)
                drawLine(k, Offset(x, 0f), Offset(x, size.height), 0.8f * u)
                var y = 16 * u; var i = 0
                while (y < size.height) { drawLine(k, Offset(x - (if (i % 3 == 0) 4f else 2f) * u, y), Offset(x, y), 0.6f * u); y += 5 * u; i++ }
            }
            Box(Modifier.weight(1f).padding(start = 8.dp)) { CuerpoFrase(f, fecha, rotulo, "“", "”") }
        }
        // El dicho clavado al palo: un mástil de cabo con su nudo arriba y la frase al lado.
        TemaId.PIRATA -> Row(arriba.height(IntrinsicSize.Min)) {
            Canvas(Modifier.width(18.dp).fillMaxHeight()) {
                val u = 1.dp.toPx(); val k = cs.primary.copy(alpha = 0.6f); val x = size.width / 2
                drawCircle(cs.secondary.copy(alpha = 0.8f), 4 * u, Offset(x, 6 * u))
                var y = 12 * u
                while (y < size.height - 4 * u) { drawLine(k, Offset(x - 2 * u, y + 2 * u), Offset(x + 2 * u, y - 1 * u), 1.2f * u); y += 3 * u }
                drawLine(cs.tertiary.copy(alpha = 0.8f), Offset(x - 3 * u, size.height - 6 * u), Offset(x + 3 * u, size.height), 1.4f * u)
                drawLine(cs.tertiary.copy(alpha = 0.8f), Offset(x + 3 * u, size.height - 6 * u), Offset(x - 3 * u, size.height), 1.4f * u)
            }
            Box(Modifier.weight(1f).padding(start = 8.dp)) { CuerpoFrase(f, fecha, rotulo, "«", "»") }
        }
        // Cuña roja constructivista y el rótulo en mayúsculas.
        TemaId.URSS -> Row(arriba.height(IntrinsicSize.Min)) {
            Canvas(Modifier.width(22.dp).fillMaxHeight()) {
                drawPath(Path().apply { moveTo(0f, 0f); lineTo(size.width, size.height * 0.5f); lineTo(0f, size.height); close() }, cs.primary)
            }
            Box(Modifier.weight(1f).padding(start = 10.dp)) { CuerpoFrase(f, fecha, rotulo.uppercase(), "«", "»") }
        }
        // Cartel con la franja tricolor arriba.
        TemaId.REVOLUCION -> Surface(shape = RoundedCornerShape(2.dp), color = cs.surface, border = BorderStroke(1.dp, cs.outline), modifier = arriba) {
            Column {
                Row(Modifier.fillMaxWidth().height(4.dp)) {
                    Box(Modifier.weight(1f).fillMaxHeight().background(cs.secondary))
                    Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFF4F1E8)))
                    Box(Modifier.weight(1f).fillMaxHeight().background(cs.primary))
                }
                Box(Modifier.padding(12.dp)) { CuerpoFrase(f, fecha, rotulo, "« ", " »") }
            }
        }
        // Lápida de piedra con dos gemas a los lados.
        TemaId.ANTIGUAS -> Surface(shape = CutCornerShape(8.dp), color = cs.surfaceVariant, border = BorderStroke(1.dp, cs.outline), modifier = arriba) {
            Box(Modifier.drawBehind {
                val u = 1.dp.toPx(); val h = size.height
                listOf(9 * u, size.width - 9 * u).forEach { x ->
                    drawPath(Path().apply { moveTo(x, h / 2 - 4 * u); lineTo(x + 2.8f * u, h / 2); lineTo(x, h / 2 + 4 * u); lineTo(x - 2.8f * u, h / 2); close() }, cs.tertiary)
                }
            }.padding(horizontal = 20.dp, vertical = 12.dp)) { CuerpoFrase(f, fecha, rotulo, "«", "»", centrado = true) }
        }
    }
}

/**
 * Lo de adentro de la frase del día: rótulo (si hay), original, texto entre las comillas del tema (o con capitular),
 * y el autor con el corazón para guardarla. Centrado, deja a la izquierda un hueco del ancho del corazón para que el
 * autor quede al centro.
 */
@Composable
private fun CuerpoFrase(f: Frases.Frase, fecha: String, rotulo: String?, abre: String, cierra: String, centrado: Boolean = false,
                        capitular: androidx.compose.ui.text.font.FontFamily? = null, autor: String = "— ${f.autor}") {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    val alin = if (centrado) TextAlign.Center else TextAlign.Start
    Column(Modifier.fillMaxWidth(), horizontalAlignment = if (centrado) Alignment.CenterHorizontally else Alignment.Start) {
        rotulo?.let { Text(it, style = ty.labelMedium, color = cs.primary, textAlign = alin) }
        // Los originales cortos (griego, latín) se lucen grandes; los largos van chicos y la traducción manda.
        f.original?.let { Text(it, style = if (it.length > 60) ty.bodySmall else ty.bodyLarge, color = cs.primary, fontStyle = FontStyle.Italic, textAlign = alin, modifier = Modifier.padding(top = 2.dp)) }
        if (capitular != null) {
            // Capitular iluminada: la primera letra grande y en el color del tema.
            Text(androidx.compose.ui.text.buildAnnotatedString {
                withStyle(androidx.compose.ui.text.SpanStyle(fontFamily = capitular, color = cs.primary, fontSize = ty.bodyMedium.fontSize * 2.2f)) { append(f.texto.take(1)) }
                append(f.texto.drop(1))
            }, style = ty.bodyMedium, textAlign = alin)
        } else Text(abre + f.texto + cierra, style = ty.bodyMedium, textAlign = alin)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (centrado) Spacer(Modifier.width(40.dp))
            Text(autor, style = ty.labelMedium, color = cs.onSurfaceVariant, textAlign = alin, modifier = Modifier.weight(1f))
            CorazonFrase(f, cs.primary, fecha = fecha)
        }
    }
}

/** Arco apuntado poco alto, para el marco gótico de la frase. */
private val OJIVA_FRASE = GenericShape { s, _ ->
    val r = minOf(s.height * 0.3f, s.width * 0.09f)
    moveTo(0f, s.height); lineTo(0f, r)
    quadraticTo(s.width * 0.18f, r * 0.1f, s.width / 2, 0f)
    quadraticTo(s.width * 0.82f, r * 0.1f, s.width, r)
    lineTo(s.width, s.height); close()
}

/** El recorte de periódico de la frase del horror cósmico: la orilla de arriba rasgada en dientes desiguales. */
private val RECORTE_FRASE = GenericShape { s, _ ->
    val d = minOf(s.height * 0.05f, 6f); val n = 16
    moveTo(0f, d)
    for (k in 1..n) lineTo(s.width * k / n, if (k % 2 == 0) d else if (k % 3 == 0) d * 2f else 0f)
    lineTo(s.width, s.height); lineTo(0f, s.height); close()
}

/** Una tira de meandros (ganchos sobre una línea) del alto del lienzo. */
/** Friso de lotos abiertos y capullos, alternados, sobre una línea. */
private fun DrawScope.frisoLotoFrase(flor: Color, capullo: Color) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    drawLine(flor, Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), u)
    var x = 6 * u; var i = 0
    while (x < w - 4 * u) {
        if (i % 2 == 0) drawPath(Path().apply { moveTo(x, h); lineTo(x - 4 * u, 1 * u); lineTo(x - 1.4f * u, 3 * u); lineTo(x, 0f); lineTo(x + 1.4f * u, 3 * u); lineTo(x + 4 * u, 1 * u); close() }, flor)
        else drawPath(Path().apply { moveTo(x, h); quadraticTo(x - 2.4f * u, h * 0.5f, x, 1.5f * u); quadraticTo(x + 2.4f * u, h * 0.5f, x, h); close() }, capullo)
        x += 8 * u; i++
    }
}

private fun DrawScope.meandroFrase(c: Color) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    val p = Path()
    p.moveTo(0f, h - 0.5f * u); p.lineTo(w, h - 0.5f * u)
    var x = 0f
    while (x + 9 * u <= w) {
        p.moveTo(x, h); p.lineTo(x, 0.5f * u); p.lineTo(x + 7 * u, 0.5f * u); p.lineTo(x + 7 * u, h * 0.65f); p.lineTo(x + 3 * u, h * 0.65f); p.lineTo(x + 3 * u, h * 0.3f)
        x += 10 * u
    }
    drawPath(p, c, style = Stroke(u))
}

/** Filete de Didot: una lente, gruesa al centro y fina en las puntas, con un rombo. */
private fun DrawScope.fileteDidot(c: Color) {
    val w = size.width; val y = size.height / 2; val u = 1.dp.toPx()
    drawPath(Path().apply { moveTo(0f, y); quadraticTo(w / 2, y - 2.2f * u, w, y); quadraticTo(w / 2, y + 2.2f * u, 0f, y); close() }, c)
    drawPath(Path().apply { moveTo(w / 2, y - 3 * u); lineTo(w / 2 + 3 * u, y); lineTo(w / 2, y + 3 * u); lineTo(w / 2 - 3 * u, y); close() }, c)
}

/** Una hoja de [a] (la base) a [b] (la punta); [ancho] es lo que se abre cada lado. */
private fun hojaFrase(a: Offset, b: Offset, ancho: Float): Path {
    val d = b - a; val l = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y / l, d.x / l)
    val m = (a + b) / 2f
    return Path().apply {
        moveTo(a.x, a.y)
        quadraticTo(m.x + n.x * ancho * 2, m.y + n.y * ancho * 2, b.x, b.y)
        quadraticTo(m.x - n.x * ancho * 2, m.y - n.y * ancho * 2, a.x, a.y)
        close()
    }
}

/** Una estrella de [puntas] puntas centrada en [c]. */
private fun estrellaFrase(c: Offset, puntas: Int, rExt: Float, rInt: Float): Path = Path().apply {
    for (i in 0 until puntas * 2) {
        val a = -Math.PI / 2 + i * Math.PI / puntas
        val r = if (i % 2 == 0) rExt else rInt
        val x = c.x + r * kotlin.math.cos(a).toFloat(); val y = c.y + r * kotlin.math.sin(a).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
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
    val eg = estiloGrafica()
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
                    if (x == null) puntoGrafica(eg, Offset(i * w + w / 2, size.height - 2f), 2f, tenue)
                    else {
                        val h = (size.height * (x / max) * crecer.value).toFloat().coerceAtLeast(2f)
                        barraGrafica(eg, Offset(i * w + w * 0.2f, size.height - h), Size(w * 0.6f, h), if (i == 6) color else color.copy(alpha = 0.35f))
                    }
                }
                normal?.let { n -> val y = size.height - (size.height * (n / max)).toFloat(); if (y in 0f..size.height) lineaMeta(eg, Offset(0f, y), Offset(size.width, y), tenue) }
            }
        }
    }
}

/**
 * Letra de las cifras grandes del tablero, una por tema. Cuando la letra de títulos no sirve para números (la capitular
 * romana no tiene minúsculas; la gótica del scriptorium y las capitulares navideñas son para títulos), se usa otra
 * familia del proyecto que vaya con el tema.
 */
@Composable
fun estiloCifra(): androidx.compose.ui.text.TextStyle {
    val base = MaterialTheme.typography.titleLarge
    fun c(familia: FontFamily, peso: FontWeight, tam: Float, esp: Float, rasgos: String? = null, cursiva: Boolean = false) =
        base.copy(fontFamily = familia, fontWeight = peso, fontSize = tam.sp, lineHeight = (tam * 1.25f).sp, letterSpacing = esp.sp,
            fontFeatureSettings = rasgos, fontStyle = if (cursiva) FontStyle.Italic else FontStyle.Normal)
    return when (Estilo.tema) {
        TemaId.ROMANO -> base.copy(fontFamily = Letra.codigo, fontSize = 21.sp, letterSpacing = 0.sp)
        TemaId.URBE -> c(Letra.placa, FontWeight.Bold, 23f, 0.4f, "tnum")
        TemaId.CIBER -> c(Letra.terminal, FontWeight.Normal, 24f, 0.8f)
        TemaId.NEON -> c(Letra.letrero, FontWeight.SemiBold, 23f, 1.6f)
        TemaId.HELENO -> c(Letra.inscripcion, FontWeight.SemiBold, 21f, 1.4f, "lnum")
        TemaId.EGIPTO -> c(Letra.messiri, FontWeight.Bold, 22f, 0.6f, "lnum, tnum")
        TemaId.MEDIEVAL -> base.copy(fontFamily = Letra.garamond, fontWeight = FontWeight.SemiBold, fontSize = 23.sp, letterSpacing = 0.sp, fontFeatureSettings = "lnum, tnum")
        TemaId.TEMPLARIO -> c(LetraExtra.regla, FontWeight.Bold, 22f, 0.3f, "lnum, tnum")
        TemaId.DISTOPIA -> c(LetraExtra.oswald, FontWeight.Bold, 24f, 0.6f)
        TemaId.REFUGIO -> c(LetraExtra.vt323, FontWeight.Normal, 30f, 0.4f)
        TemaId.ORDEN -> c(LetraExtra.estarcido, FontWeight.Normal, 23f, 1.2f)
        TemaId.LABORATORIO -> c(LetraExtra.jost, FontWeight.Normal, 23f, 1f, "tnum")
        TemaId.COLONOS -> c(LetraExtra.fell, FontWeight.Normal, 24f, 0.2f, "lnum")
        TemaId.NAPOLEON -> c(LetraExtra.bodoniTitulo, FontWeight.SemiBold, 23f, 0.8f, "lnum")
        TemaId.BOTANICO -> c(LetraExtra.fraunces, FontWeight.Medium, 22f, 0f)
        TemaId.RUINAS -> c(LetraExtra.letrero, FontWeight.Normal, 24f, 0.5f, "tnum")
        TemaId.NAVIDAD -> c(LetraExtra.amiri, FontWeight.Normal, 25f, 0f)
        TemaId.SEDA -> c(LetraExtra.almendra, FontWeight.Normal, 24f, 0.3f)
        TemaId.GOTICO -> c(LetraExtra.gotica, FontWeight.SemiBold, 25f, 0.2f)
        TemaId.ARCANO -> c(Letra.spectral, FontWeight.Medium, 24f, 0.3f)
        TemaId.ESPACIO -> c(LetraExtra.orbita, FontWeight.Medium, 20f, 1.4f)
        TemaId.ABISMO -> c(LetraExtra.batiscafo, FontWeight.SemiBold, 25f, 0.5f, "tnum")
        TemaId.ANDALUS -> c(LetraExtra.kufi, FontWeight.Medium, 22f, 0.4f)
        TemaId.MONGOL -> c(LetraExtra.filosofo, FontWeight.Bold, 23f, 0.6f)
        TemaId.RENACIMIENTO -> base.copy(fontFamily = LetraExtra.cormorant, fontWeight = FontWeight.Medium, fontSize = 25.sp, letterSpacing = 0.sp, fontFeatureSettings = "lnum")
        TemaId.PIRATA -> c(LetraExtra.caslon, FontWeight.SemiBold, 23f, 0.3f, "lnum, tnum")
        TemaId.URSS -> c(LetraExtra.russo, FontWeight.Normal, 22f, 0.5f)
        // Didot en cursiva, como los impresos de 1793; el napoleónico usa la misma casa, derecha y espaciada.
        TemaId.REVOLUCION -> c(LetraExtra.bodoni, FontWeight.Normal, 24f, 0.2f, "lnum", cursiva = true)
        TemaId.ANTIGUAS -> c(LetraExtra.cinzelDeco, FontWeight.Normal, 21f, 1f)
    }
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
        val estiloEje = estiloCifras(MaterialTheme.typography.labelSmall.copy(color = texto))
        val eg = estiloGrafica()
        Canvas(Modifier.weight(1f).height(alto * (pistas.size + 1))) {
            val h = alto.toPx()
            fun x(ms: Long) = ((ms - ini).toFloat() / (fin - ini)) * size.width
            // Noche sombreada según el amanecer y el atardecer del día.
            if (amanecer != null && atardecer != null) {
                val a = x(ini + amanecer.toSecondOfDay() * 1000L); val t = x(ini + atardecer.toSecondOfDay() * 1000L)
                drawRect(noche, Offset(0f, 0f), Size(a, h * pistas.size))
                drawRect(noche, Offset(t, 0f), Size(size.width - t, h * pistas.size))
            }
            // La rejilla del tema: un renglón por pista y una guía cada 6 horas; abajo, el eje de las horas.
            val horas = listOf(0, 6, 12, 18, 24)
            val fondo = h * pistas.size
            rejillaGrafica(eg, (1 until pistas.size).map { it * h }, linea, xs = horas.map { x(ini + it * 3_600_000L) }, area = Rect(0f, 0f, size.width, fondo))
            ejeGrafica(eg, Offset(0f, fondo), Offset(size.width, fondo), linea)
            pistas.forEachIndexed { i, p ->
                val y = i * h
                p.bloques.forEach { (a, b, c) ->
                    val xa = x(a).coerceIn(0f, size.width); val xb = x(b).coerceIn(0f, size.width)
                    if (xb > xa) barraGrafica(eg, Offset(xa, y + h * 0.2f), Size(xb - xa, h * 0.6f), c.copy(alpha = 0.75f), horizontal = true)
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
            horas.forEach { hh ->
                val xa = x(ini + hh * 3_600_000L)
                val r = medidor.measure(if (hh == 24) "0" else "$hh", estiloEje)
                drawText(r, topLeft = Offset((xa - r.size.width / 2).coerceIn(0f, size.width - r.size.width), h * pistas.size + 2f))
            }
            val ahora = System.currentTimeMillis()
            if (ahora in ini..fin) { val xa = x(ahora); lineaMeta(eg, Offset(xa, 0f), Offset(xa, h * pistas.size), sec, 2f) }
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
                    val borde = Stroke(1.dp.toPx())
                    val linea = if (esHoy) cs.primary else cs.outline
                    val u = 1.dp.toPx()
                    // Con color, se rellena; sin él (hoy, todavía), solo el contorno.
                    fun pinta(p: Path) { if (color != null) drawPath(p, color) else drawPath(p, linea, style = borde) }
                    fun caja(x: Float, y: Float, ancho: Float, altoCaja: Float) = Path().apply { addRect(Rect(x, y, x + ancho, y + altoCaja)) }
                    // El color del sello de arriba (día con valencia y diario).
                    var sello = cs.primary
                    when (tema) {
                        // Columnas: fuste, capitel y basa.
                        TemaId.ROMANO -> {
                            val fx = w * 0.18f; val fw = w * 0.64f; val cap = 3.dp.toPx()
                            if (color != null) drawRect(color, Offset(fx, top + cap), Size(fw, barH - 2 * cap)) else drawRect(linea, Offset(fx, top + cap), Size(fw, barH - 2 * cap), style = borde)
                            if (color != null) for (k in 1..2) drawLine(Color.Black.copy(alpha = 0.18f), Offset(fx + fw * k / 3, top + cap), Offset(fx + fw * k / 3, h - cap), 1.dp.toPx())
                            drawRect(color ?: linea, Offset(0f, top), Size(w, cap), style = if (color == null) borde else Fill)
                            drawRect(color ?: linea, Offset(0f, h - cap), Size(w, cap), style = if (color == null) borde else Fill)
                        }
                        // Cada día es un edificio con su cúpula: el cuerpo con las ventanas y el casquete arriba.
                        TemaId.URBE -> {
                            val fx = w * 0.14f; val fw = w * 0.72f
                            val cuerpo = Path().apply { moveTo(fx, h); lineTo(fx, top + fw / 2); arcTo(Rect(fx, top, fx + fw, top + fw), 180f, 180f, false); lineTo(fx + fw, h); close() }
                            pinta(cuerpo)
                            if (color != null) {
                                var y = top + fw * 0.8f
                                while (y < h - 5 * u) { drawRect(Color.Black.copy(alpha = 0.2f), Offset(fx + fw * 0.3f, y), Size(fw * 0.4f, 3 * u)); y += 7 * u }
                            }
                            drawRect(linea, Offset(w / 2 - 1 * u, top - 3 * u), Size(2 * u, 3 * u))
                        }
                        // Barras con líneas de escaneo.
                        TemaId.CIBER -> {
                            if (color != null) {
                                drawRect(color, Offset(0f, top), Size(w, barH))
                                var y = top + 2.dp.toPx()
                                while (y < h) { drawLine(Color.Black.copy(alpha = 0.28f), Offset(0f, y), Offset(w, y), 1f); y += 3.dp.toPx() }
                            } else drawRect(linea, Offset(0f, top), Size(w, barH), style = borde)
                        }
                        // Tubos de neón verticales: el halo y el núcleo; vacío, solo el tubo apagado.
                        TemaId.NEON -> {
                            val cx = w / 2; val g = w * 0.32f
                            if (color != null) {
                                drawLine(color.copy(alpha = 0.3f), Offset(cx, top + g), Offset(cx, h - g), g * 2.6f, StrokeCap.Round)
                                drawLine(color, Offset(cx, top + g), Offset(cx, h - g), g, StrokeCap.Round)
                            } else drawLine(linea, Offset(cx, top + g), Offset(cx, h - g), g * 0.6f, StrokeCap.Round)
                        }
                        // Estelas: un cuerpo de piedra con su frontón triangular.
                        TemaId.HELENO -> {
                            val fr = minOf(w * 0.45f, barH * 0.3f)
                            pinta(caja(w * 0.12f, top + fr, w * 0.76f, barH - fr))
                            pinta(Path().apply { moveTo(0f, top + fr); lineTo(w / 2, top); lineTo(w, top + fr); close() })
                            drawLine(if (color != null) Color.Black.copy(alpha = 0.22f) else linea, Offset(0f, top + fr), Offset(w, top + fr), u)
                        }
                        // Obeliscos: el fuste que se adelgaza y el piramidión en la punta, separado por una línea.
                        TemaId.EGIPTO -> {
                            val pp = minOf(w * 0.5f, barH * 0.25f)
                            pinta(Path().apply { moveTo(w * 0.1f, top + barH); lineTo(w * 0.2f, top + pp); lineTo(w / 2, top); lineTo(w * 0.8f, top + pp); lineTo(w * 0.9f, top + barH); close() })
                            drawLine(if (color != null) Color.Black.copy(alpha = 0.22f) else linea, Offset(w * 0.2f, top + pp), Offset(w * 0.8f, top + pp), u)
                        }
                        // Franjas iluminadas: arco de medio punto y filete dorado.
                        TemaId.MEDIEVAL -> {
                            val r = CornerRadius(w / 2, w / 2)
                            if (color != null) {
                                drawRoundRect(color, Offset(0f, top), Size(w, barH), r)
                                drawRoundRect(oro, Offset(0f, top), Size(w, barH), r, style = Stroke(1.5f * u))
                            } else drawRoundRect(linea, Offset(0f, top), Size(w, barH), r, style = borde)
                            sello = oro
                        }
                        // Torres de la muralla: dos merlones arriba y, en las jornadas registradas, la cruz pintada.
                        TemaId.TEMPLARIO -> {
                            val m = minOf(w * 0.3f, barH * 0.14f)
                            pinta(Path().apply {
                                moveTo(0f, h); lineTo(0f, top); lineTo(w * 0.36f, top); lineTo(w * 0.36f, top + m); lineTo(w * 0.64f, top + m); lineTo(w * 0.64f, top); lineTo(w, top); lineTo(w, h); close()
                            })
                            if (color != null && barH > w * 1.6f) drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(Offset(w / 2, top + m + w * 0.55f), w * 0.3f), Color.White.copy(alpha = 0.75f))
                            sello = cs.secondary
                        }
                        // Fichas perforadas: esquina cortada y una hilera de agujeros.
                        TemaId.DISTOPIA -> {
                            val k = minOf(w * 0.35f, barH * 0.2f)
                            pinta(Path().apply { moveTo(0f, h); lineTo(0f, top); lineTo(w - k, top); lineTo(w, top + k); lineTo(w, h); close() })
                            var y = top + k + 4 * u
                            while (y < h - 4 * u) { drawRect(if (color != null) cs.background.copy(alpha = 0.55f) else linea.copy(alpha = 0.5f), Offset(w * 0.4f, y), Size(w * 0.2f, 2.5f * u)); y += 9 * u }
                        }
                        // Medidor de segmentos, como en un tablero de control.
                        TemaId.REFUGIO -> {
                            val seg = 7 * u; val hueco = 2 * u; val r = CornerRadius(2 * u, 2 * u)
                            var y = h
                            while (y > top + 0.5f) {
                                val alto2 = minOf(seg, y - top)
                                if (color != null) drawRoundRect(color, Offset(0f, y - alto2), Size(w, alto2), r) else drawRoundRect(linea, Offset(0f, y - alto2), Size(w, alto2), r, style = borde)
                                y -= seg + hueco
                            }
                        }
                        // Espadas con la punta arriba, la guarda y el puño abajo.
                        TemaId.ORDEN -> {
                            val punta = minOf(w * 0.6f, barH * 0.3f); val bx = w * 0.22f; val bw = w * 0.56f
                            pinta(Path().apply { moveTo(bx, h - 6 * u); lineTo(bx, top + punta); lineTo(w / 2, top); lineTo(bx + bw, top + punta); lineTo(bx + bw, h - 6 * u); close() })
                            if (color != null) drawLine(Color.Black.copy(alpha = 0.22f), Offset(w / 2, top + punta), Offset(w / 2, h - 7 * u), u)
                            drawRect(color ?: linea, Offset(0f, h - 6 * u), Size(w, 2 * u))
                            drawRect(color ?: linea, Offset(w * 0.4f, h - 4 * u), Size(w * 0.2f, 4 * u))
                            sello = cs.secondary
                        }
                        // Probetas de fondo redondo, con su borde y un brillo de vidrio.
                        TemaId.LABORATORIO -> {
                            val tx = w * 0.15f; val tw = w * 0.7f; val rr = tw / 2
                            val tubo = Path().apply { moveTo(tx, top); lineTo(tx, h - rr); arcTo(Rect(tx, h - 2 * rr, tx + tw, h), 180f, -180f, false); lineTo(tx + tw, top); close() }
                            pinta(tubo)
                            if (color != null) {
                                drawPath(tubo, cs.outline, style = borde)
                                drawLine(Color.White.copy(alpha = 0.35f), Offset(tx + tw * 0.28f, top + 3 * u), Offset(tx + tw * 0.28f, h - rr), u)
                            }
                            drawLine(linea, Offset(tx - 1.5f * u, top), Offset(tx + tw + 1.5f * u, top), 1.5f * u)
                        }
                        // Muros de ladrillo, hilada por hilada.
                        TemaId.COLONOS -> {
                            pinta(caja(0f, top, w, barH))
                            if (color != null) {
                                val junta = cs.background.copy(alpha = 0.6f); val hl = 5 * u
                                var y = h; var fila = 0
                                while (y - hl > top) {
                                    drawLine(junta, Offset(0f, y - hl), Offset(w, y - hl), 0.8f * u)
                                    val juntas = if (fila % 2 == 0) listOf(w * 0.5f) else listOf(w * 0.2f, w * 0.8f)
                                    juntas.forEach { x -> drawLine(junta, Offset(x, y - hl), Offset(x, y), 0.8f * u) }
                                    y -= hl; fila++
                                }
                            }
                        }
                        // Bastones de mariscal: casquillos de oro y una hilera de abejas (puntos).
                        TemaId.NAPOLEON -> {
                            val bx = w * 0.18f; val bw = w * 0.64f; val r = CornerRadius(bw / 2, bw / 2)
                            if (color != null) drawRoundRect(color, Offset(bx, top), Size(bw, barH), r) else drawRoundRect(linea, Offset(bx, top), Size(bw, barH), r, style = borde)
                            drawRect(oro, Offset(bx, top + bw * 0.5f), Size(bw, 2.5f * u))
                            drawRect(oro, Offset(bx, h - bw * 0.5f - 2.5f * u), Size(bw, 2.5f * u))
                            if (color != null) { var y = top + bw * 0.5f + 8 * u; while (y < h - bw * 0.5f - 6 * u) { drawCircle(oro, 1.1f * u, Offset(w / 2, y)); y += 7 * u } }
                            sello = oro
                        }
                        // Plantas: tallo con hojas alternas y un capullo arriba.
                        TemaId.BOTANICO -> {
                            drawLine(color ?: linea, Offset(w / 2, h), Offset(w / 2, top + w * 0.35f), 1.5f * u)
                            var y = h - 8 * u; var lado = 1f
                            while (y > top + w * 0.9f) { pinta(hojaFrase(Offset(w / 2, y), Offset(w / 2 + lado * w * 0.45f, y - 5 * u), 1.4f * u)); y -= 9 * u; lado = -lado }
                            pinta(Path().apply { addOval(Rect(w / 2 - w * 0.3f, top, w / 2 + w * 0.3f, top + w * 0.75f)) })
                        }
                        TemaId.RUINAS -> {
                            // Un edificio roto: la azotea quebrada a la altura del día, ventanas vacías y una mata de hiedra al pie.
                            pinta(Path().apply { moveTo(0f, h); lineTo(0f, top + 3 * u); lineTo(w * 0.35f, top); lineTo(w * 0.55f, top + 4 * u); lineTo(w, top + 1.5f * u); lineTo(w, h); close() })
                            if (color != null) {
                                var y = top + 8 * u
                                while (y < h - 6 * u) { drawRect(cs.background.copy(alpha = 0.5f), Offset(w * 0.25f, y), Size(w * 0.18f, 3 * u)); drawRect(cs.background.copy(alpha = 0.5f), Offset(w * 0.58f, y), Size(w * 0.18f, 3 * u)); y += 7 * u }
                            }
                            drawCircle(Color(0xFF4F7A33).copy(alpha = 0.8f), w * 0.22f, Offset(w * 0.12f, h - w * 0.1f))
                        }
                        // Velas con su llama.
                        TemaId.NAVIDAD -> {
                            val llama = minOf(w * 0.9f, barH * 0.3f); val vx = w * 0.15f; val vw = w * 0.7f
                            pinta(caja(vx, top + llama, vw, barH - llama))
                            drawLine(cs.onSurfaceVariant, Offset(w / 2, top + llama), Offset(w / 2, top + llama - 2.5f * u), u)
                            drawPath(Path().apply { moveTo(w / 2, top); quadraticTo(w / 2 + w * 0.32f, top + llama * 0.6f, w / 2, top + llama - 2.5f * u); quadraticTo(w / 2 - w * 0.32f, top + llama * 0.6f, w / 2, top); close() },
                                if (color != null) oro else linea.copy(alpha = 0.5f))
                            if (color != null) drawCircle(color, vw * 0.14f, Offset(vx + vw * 0.25f, top + llama + 2 * u))
                        }
                        // Cañas de bambú con sus nudos.
                        TemaId.SEDA -> {
                            val bx = w * 0.15f; val bw = w * 0.7f
                            pinta(caja(bx, top, bw, barH))
                            val nudo = if (color != null) Color.Black.copy(alpha = 0.25f) else linea
                            var y = h - 12 * u
                            while (y > top + 4 * u) { drawLine(nudo, Offset(bx - 1.5f * u, y), Offset(bx + bw + 1.5f * u, y), 1.6f * u); y -= 12 * u }
                            pinta(hojaFrase(Offset(bx + bw, top + 8 * u), Offset(w, top + 2 * u), 1.2f * u))
                        }
                        // Lancetas: ventana apuntada con su filete de oro y el parteluz.
                        TemaId.GOTICO -> {
                            val r = minOf(w, barH * 0.5f)
                            val p = Path().apply { moveTo(0f, h); lineTo(0f, top + r); quadraticTo(0f, top + r * 0.3f, w / 2, top); quadraticTo(w, top + r * 0.3f, w, top + r); lineTo(w, h); close() }
                            pinta(p)
                            if (color != null) { drawPath(p, oro, style = Stroke(1.2f * u)); drawLine(oro, Offset(w / 2, top + r * 0.6f), Offset(w / 2, h), 0.8f * u) }
                            sello = oro
                        }
                        // Legajos de pie: la carpeta con su pestaña arriba, el cordel que la cruza y la ficha.
                        TemaId.ARCANO -> {
                            val pest = minOf(5 * u, barH * 0.3f)
                            val p = Path().apply { moveTo(0f, h); lineTo(0f, top + pest); lineTo(w * 0.15f, top + pest); lineTo(w * 0.25f, top); lineTo(w * 0.7f, top); lineTo(w * 0.8f, top + pest); lineTo(w, top + pest); lineTo(w, h); close() }
                            pinta(p)
                            if (color != null) {
                                drawLine(cs.onSurface.copy(alpha = 0.35f), Offset(0f, top + (h - top) * 0.55f), Offset(w, top + (h - top) * 0.55f), 0.9f * u)
                                if (barH > 18 * u) drawRect(cs.surface.copy(alpha = 0.7f), Offset(w * 0.2f, top + pest + 3 * u), Size(w * 0.6f, minOf(6 * u, barH * 0.2f)))
                            }
                            sello = cs.tertiary
                        }
                        // Cohetes: cuerpo, ojiva, aletas y una ventanilla.
                        TemaId.ESPACIO -> {
                            val bx = w * 0.22f; val bw = w * 0.56f; val nariz = minOf(w * 0.8f, barH * 0.3f); val aleta = minOf(9 * u, barH * 0.2f)
                            pinta(Path().apply { moveTo(bx, h - 2 * u); lineTo(bx, top + nariz); quadraticTo(bx, top + nariz * 0.2f, w / 2, top); quadraticTo(bx + bw, top + nariz * 0.2f, bx + bw, top + nariz); lineTo(bx + bw, h - 2 * u); close() })
                            pinta(Path().apply { moveTo(bx, h - aleta - 2 * u); lineTo(0f, h); lineTo(bx, h); close(); moveTo(bx + bw, h - aleta - 2 * u); lineTo(w, h); lineTo(bx + bw, h); close() })
                            drawCircle(if (color != null) cs.background.copy(alpha = 0.8f) else linea, bw * 0.22f, Offset(w / 2, top + nariz + bw * 0.4f), style = if (color != null) Fill else borde)
                        }
                        // Algas gigantes: el tallo que ondula hacia la luz, las vejigas de aire a los lados y la más grande arriba.
                        TemaId.ABISMO -> {
                            val tallo = Path(); var y = h
                            tallo.moveTo(w / 2, h)
                            while (y > top + w * 0.5f) { y -= 2 * u; tallo.lineTo(w / 2 + w * 0.12f * kotlin.math.sin((h - y) / (7 * u)), y) }
                            drawPath(tallo, color ?: linea, style = Stroke(1.6f * u))
                            var yy = h - 9 * u; var lado = 1f
                            while (yy > top + w * 1.1f) { pinta(Path().apply { addOval(Rect(Offset(w / 2 + lado * w * 0.26f, yy), w * 0.13f)) }); yy -= 10 * u; lado = -lado }
                            pinta(Path().apply { addOval(Rect(Offset(w / 2, top + w * 0.3f), w * 0.28f)) })
                        }
                        // Arcos de herradura: el círculo de arriba se cierra más allá de las jambas.
                        TemaId.ANDALUS -> {
                            val r = w / 2; val bx = w * 0.14f; val bw = w * 0.72f
                            val p = Path().apply {
                                moveTo(bx, h); lineTo(bx, top + r + r * 0.695f)
                                arcTo(Rect(0f, top, w, top + 2 * r), 136f, 268f, false)
                                lineTo(bx + bw, h); close()
                            }
                            pinta(p)
                            if (color != null) drawPath(p, oro, style = Stroke(u))
                        }
                        // Estandartes de la estepa: lanza arriba y crines que cuelgan.
                        TemaId.MONGOL -> {
                            val bx = w * 0.2f; val bw = w * 0.6f; val punta = minOf(w * 0.8f, barH * 0.28f)
                            pinta(caja(bx, top + punta, bw, barH - punta))
                            drawPath(Path().apply { moveTo(w / 2, top); lineTo(w / 2 + bw * 0.35f, top + punta * 0.55f); lineTo(w / 2, top + punta * 0.8f); lineTo(w / 2 - bw * 0.35f, top + punta * 0.55f); close() },
                                if (color != null) cs.onSurfaceVariant else linea)
                            for (k in -3..3) drawLine(color ?: linea, Offset(w / 2, top + punta * 0.8f), Offset(w / 2 + k * w * 0.07f, top + punta + 6 * u), 0.8f * u)
                        }
                        // Barras sombreadas a plumilla, con su contorno, como en los cuadernos.
                        TemaId.RENACIMIENTO -> {
                            val x0 = w * 0.08f; val bw = w * 0.84f
                            val r = caja(x0, top, bw, barH)
                            if (color != null) {
                                drawPath(r, color.copy(alpha = 0.3f))
                                clipPath(r) { var k = 0f; while (k < barH + bw) { drawLine(color, Offset(x0, top + k), Offset(x0 + bw, top + k - bw), 0.9f * u); k += 3.5f * u } }
                                drawPath(r, color, style = borde)
                            } else drawPath(r, linea, style = borde)
                        }
                        // La franja como un tablón de cubierta: sus vetas a lo largo y los dos clavos de los extremos.
                        TemaId.PIRATA -> {
                            val x0 = w * 0.08f; val bw = w * 0.84f
                            val r = caja(x0, top, bw, barH)
                            if (color != null) {
                                drawPath(r, color.copy(alpha = 0.32f))
                                clipPath(r) { var k = 0; var y = top + barH * 0.25f; while (y < top + barH) { drawLine(color.copy(alpha = 0.7f), Offset(x0, y), Offset(x0 + bw, y + (if (k % 2 == 0) 1f else -1f) * u), 0.7f * u); y += barH * 0.25f; k++ } }
                                drawPath(r, color, style = borde)
                                listOf(x0 + 3 * u, x0 + bw - 3 * u).forEach { x -> drawCircle(color, 1.2f * u, Offset(x, top + barH / 2)) }
                            } else drawPath(r, linea, style = borde)
                        }
                        // Bloques constructivistas: el corte en diagonal arriba y una base negra.
                        TemaId.URSS -> {
                            val inc = minOf(w * 0.7f, barH * 0.25f)
                            pinta(Path().apply { moveTo(0f, h); lineTo(0f, top); lineTo(w, top + inc); lineTo(w, h); close() })
                            if (color != null) drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, h - 4 * u), Size(w, 4 * u))
                        }
                        // Árboles de la libertad: tronco y copa.
                        TemaId.REVOLUCION -> {
                            val copa = w / 2
                            drawRect(if (color != null) cs.onSurfaceVariant.copy(alpha = 0.7f) else linea, Offset(w / 2 - w * 0.09f, top + copa), Size(w * 0.18f, h - top - copa))
                            drawLine(if (color != null) cs.onSurfaceVariant.copy(alpha = 0.7f) else linea, Offset(w * 0.2f, h), Offset(w * 0.8f, h), 1.5f * u)
                            pinta(Path().apply { addOval(Rect(0f, top, w, top + 2 * copa)) })
                        }
                        // Cristales tallados, con sus facetas.
                        TemaId.ANTIGUAS -> {
                            val pc = minOf(w * 0.7f, barH * 0.25f)
                            pinta(Path().apply { moveTo(w * 0.18f, h); lineTo(w * 0.06f, top + pc); lineTo(w / 2, top); lineTo(w * 0.94f, top + pc); lineTo(w * 0.82f, h); close() })
                            val faceta = if (color != null) Color.White.copy(alpha = 0.3f) else linea.copy(alpha = 0.5f)
                            drawLine(faceta, Offset(w / 2, top), Offset(w / 2, h), 0.7f * u)
                            drawLine(faceta, Offset(w * 0.06f, top + pc), Offset(w / 2, top + pc * 1.6f), 0.7f * u)
                            drawLine(faceta, Offset(w * 0.94f, top + pc), Offset(w / 2, top + pc * 1.6f), 0.7f * u)
                            sello = cs.tertiary
                        }
                    }
                    // Hoy: se llena desde abajo con lo que ya anotaste, sin el color del ánimo.
                    if (esHoy && color == null && avance > 0f) drawRect(cs.primary.copy(alpha = 0.35f), Offset(0f, h - barH * avance), Size(w, barH * avance))
                    val cx = w / 2; val cy = remate / 2
                    when (marca) {
                        2 -> { drawCircle(sello, w.coerceAtMost(remate) * 0.42f, Offset(cx, cy))
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
