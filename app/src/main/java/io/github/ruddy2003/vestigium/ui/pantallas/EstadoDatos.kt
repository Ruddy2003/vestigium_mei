package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.Separador
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.data.porNumero

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Actividad
import io.github.ruddy2003.vestigium.sensores.Agenda
import io.github.ruddy2003.vestigium.sensores.Comunicacion
import io.github.ruddy2003.vestigium.sensores.Galeria
import io.github.ruddy2003.vestigium.sensores.Oyente
import io.github.ruddy2003.vestigium.sensores.Pantalla
import io.github.ruddy2003.vestigium.sensores.Salud
import io.github.ruddy2003.vestigium.sensores.Sincronizar
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Reanudar
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Cómo está una fuente de datos: bien (llegó hoy o ayer), atrasada, sin permiso o apagada. */
enum class Salud3 { BIEN, ATRASADA, SIN_PERMISO, APAGADA }

class Fuente(val nombre: String, val estado: Salud3, val detalle: String, val arreglo: String? = null)

object EstadoDatos {
    private fun hace(fecha: String?, hoy: String): String = when {
        fecha == null -> "nunca"
        fecha >= hoy -> "hoy"
        fecha == Fechas.sumar(hoy, -1) -> "ayer"
        else -> "hace ${porNumero(Fechas.diasEntre(fecha, hoy), "1 día", "${Fechas.diasEntre(fecha, hoy)} días")}"
    }

    private fun por(fecha: String?, hoy: String, tolerancia: Int = 1): Salud3 =
        if (fecha != null && Fechas.diasEntre(fecha, hoy) <= tolerancia) Salud3.BIEN else Salud3.ATRASADA

    suspend fun revisar(ctx: Context): List<Fuente> = withContext(Dispatchers.IO) {
        val hoy = Fechas.diaLogico()
        val c = Repo.config
        val l = ArrayList<Fuente>()
        // Reloj (Health Connect).
        val hc = runCatching { if (Salud.disponible(ctx)) Salud.concedidos(ctx) else emptySet() }.getOrDefault(emptySet())
        val uSueno = Repo.salud.values.filter { it.sueno_min != null }.maxOfOrNull { it.fecha }
        val uPasos = Repo.salud.values.filter { (it.pasos ?: 0) > 0 }.maxOfOrNull { it.fecha }
        l += if (hc.isEmpty()) Fuente("Reloj · sueño y pasos", Salud3.SIN_PERMISO, "Health Connect sin permisos", "permisos")
        else Fuente("Reloj · sueño y pasos", maxOf(por(uSueno, hoy), por(uPasos, hoy)), "sueño ${hace(uSueno, hoy)} · pasos ${hace(uPasos, hoy)}", "sincronizar")
        // Corazón y oxígeno: sin permiso, o con permiso pero sin datos (Samsung Health no los está escribiendo en Health Connect).
        if (hc.isNotEmpty()) {
            val pFc = androidx.health.connect.client.permission.HealthPermission.getReadPermission(androidx.health.connect.client.records.HeartRateRecord::class)
            val pO2 = androidx.health.connect.client.permission.HealthPermission.getReadPermission(androidx.health.connect.client.records.OxygenSaturationRecord::class)
            val uFc = Repo.salud.values.filter { it.fc_media != null || it.fc_reposo != null }.maxOfOrNull { it.fecha }
            val uO2 = Repo.salud.values.filter { it.spo2 != null }.maxOfOrNull { it.fecha }
            l += when {
                pFc !in hc || pO2 !in hc -> Fuente("Reloj · corazón y oxígeno", Salud3.SIN_PERMISO,
                    "Falta el permiso de " + listOfNotNull("ritmo cardiaco".takeIf { pFc !in hc }, "oxígeno".takeIf { pO2 !in hc }).joinToString(" y ") + " en Health Connect", "permisos")
                uFc == null && uO2 == null -> Fuente("Reloj · corazón y oxígeno", Salud3.ATRASADA,
                    "Health Connect no tiene esos datos. En Samsung Health → Ajustes → Health Connect, deja que escriba ritmo cardiaco y saturación de oxígeno; luego sincroniza.", "sincronizar")
                else -> Fuente("Reloj · corazón y oxígeno", maxOf(por(uFc, hoy), por(uO2, hoy, 3)), "ritmo ${hace(uFc, hoy)} · oxígeno ${hace(uO2, hoy)}", "sincronizar")
            }
            if ("android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND" !in hc)
                l += Fuente("Reloj en segundo plano", Salud3.SIN_PERMISO, "Sin permiso de lectura en segundo plano: el reloj solo se actualiza con la app abierta", "permisos")
        }
        // Pantalla.
        l += if (!Pantalla.tienePermiso(ctx)) Fuente("Uso del teléfono", Salud3.SIN_PERMISO, "Falta el acceso a uso", "permisos")
        else Repo.pantalla.keys.maxOrNull().let { u -> Fuente("Uso del teléfono", por(u, hoy), "último día: ${hace(u, hoy)}", "sincronizar") }
        // Ubicación.
        l += when {
            !Ubicacion.tienePermiso(ctx) -> Fuente("Ubicación", Salud3.SIN_PERMISO, "Sin permiso de ubicación", "permisos")
            !c.muestreo_ubicacion -> Fuente("Ubicación", Salud3.APAGADA, "Muestras de fondo apagadas en Privacidad", "privacidad")
            else -> {
                val u = runCatching { Repo.db().ultimaUbicacion() }.getOrNull()
                val horas = u?.let { (System.currentTimeMillis() - it) / 3_600_000.0 }
                Fuente("Ubicación", if (horas != null && horas < 3) Salud3.BIEN else Salud3.ATRASADA,
                    if (u == null) "sin muestras todavía" else "última muestra a las ${Fechas.horaDe(Fechas.isoDeMillis(u))}" + if (horas != null && horas >= 24) " (hace ${porNumero((horas / 24).toInt(), "1 día", "${(horas / 24).toInt()} días")})" else "",
                    if (!Ubicacion.tieneSegundoPlano(ctx)) "permisos" else null)
            }
        }
        // Notificaciones.
        l += if (!Oyente.activo(ctx)) Fuente("Notificaciones y música", Salud3.SIN_PERMISO, "El lector de notificaciones no está activo", "permisos")
        else Repo.notifs.entries.filter { it.value.total > 0 }.maxOfOrNull { it.key }.let { u -> Fuente("Notificaciones y música", por(u, hoy, 0), "contó algo ${hace(u, hoy)}", "permisos") }
        // Movimiento.
        l += if (!Actividad.tienePermiso(ctx)) Fuente("Movimiento", Salud3.SIN_PERMISO, "Sin permiso de actividad física", "permisos")
        else Repo.actividad.keys.maxOrNull().let { u -> Fuente("Movimiento", por(u, hoy), "último registro: ${hace(u, hoy)}") }
        // Clima.
        l += if (!c.clima) Fuente("Clima", Salud3.APAGADA, "Apagado", "permisos")
        else Repo.entorno.keys.maxOrNull().let { u -> Fuente("Clima", por(u, hoy), "último día: ${hace(u, hoy)}", "sincronizar") }
        // Fotos, agenda, llamadas y percepción.
        l += if (!Galeria.tienePermiso(ctx)) Fuente("Fotos", Salud3.SIN_PERMISO, "Sin permiso de fotos", "permisos")
        else Repo.fotos.keys.maxOrNull().let { u -> Fuente("Fotos", por(u, hoy, 3), "último día con fotos: ${hace(u, hoy)}") }
        l += if (!Agenda.tienePermiso(ctx)) Fuente("Agenda", Salud3.SIN_PERMISO, "Sin permiso de calendario", "permisos")
        else Repo.agenda.keys.maxOrNull().let { u -> Fuente("Agenda", por(u, hoy, 7), "último día con eventos: ${hace(u, hoy)}") }
        l += if (!Comunicacion.tienePermiso(ctx)) Fuente("Llamadas y SMS", Salud3.APAGADA, "Sin permiso (opcional)", "permisos")
        else Repo.comunicacion.keys.maxOrNull().let { u -> Fuente("Llamadas y SMS", por(u, hoy, 3), "último día: ${hace(u, hoy)}") }
        Repo.conexiones.entries.filter { it.value.muestras > 0 }.maxOfOrNull { it.key }.let { u -> l += Fuente("Wifi, audio, carga y luz", por(u, hoy, 0), "muestras ${hace(u, hoy)}") }
        // Importaciones a mano: hace cuánto se importó cada una (y si ya toca).
        val R = io.github.ruddy2003.vestigium.avisos.Recordatorios
        R.FUENTES.filter { it.activa }.forEach { f ->
            val hace = R.hace(f.id, hoy)
            val estado = when { R.apagada(f.id) -> Salud3.APAGADA; hace == null -> Salud3.ATRASADA; hace <= f.cada -> Salud3.BIEN; else -> Salud3.ATRASADA }
            l += Fuente(f.nombre, estado, (hace?.let { if (it == 0L) "importado hoy" else "importado hace ${porNumero(it, "1 día", "$it días")}" } ?: "nunca importado") + " · toca cada ${if (f.cada >= 28) "mes" else "${porNumero(f.cada, "1 día", "${f.cada} días")}"}", "datos")
        }
        // Respaldos.
        val respaldo = io.github.ruddy2003.vestigium.data.Respaldos.ultimaFuera(c)
        l += if (c.carpeta_respaldo == null && !c.drive) Fuente("Respaldo automático", Salud3.APAGADA, "Sin carpeta ni Google Drive", "datos")
        else Fuente("Respaldo automático", if (respaldo != null && System.currentTimeMillis() - Fechas.millis(respaldo) < 36 * 3_600_000L) Salud3.BIEN else Salud3.ATRASADA,
            "último: " + (respaldo?.let { Fechas.corto(it.take(10)) + " " + Fechas.horaDe(it) } ?: "nunca"), "datos")
        // Registros guardados que no se pudieron leer (un documento dañado no debe perderse en silencio).
        val il = Repo.ilegibles
        l += if (il.isEmpty() && Repo.configIlegible == null) Fuente("Registros guardados", Salud3.BIEN, "todos se leen bien")
        else Fuente("Registros guardados", Salud3.ATRASADA, (listOfNotNull("ajustes (el original quedó aparte)".takeIf { Repo.configIlegible != null }) +
            il.entries.sortedBy { it.key }.map { "${it.value} de «${it.key}»" }).joinToString(" · ") + " no se pudieron leer", "datos")
        l += Fuente("Sincronización", if (c.ultimo_sync != null && System.currentTimeMillis() - Fechas.millis(c.ultimo_sync) < 6 * 3_600_000L) Salud3.BIEN else Salud3.ATRASADA,
            "última: " + (c.ultimo_sync?.let { Fechas.corto(it.take(10)) + " " + Fechas.horaDe(it) } ?: "nunca"), "sincronizar")
        l
    }
}

/**
 * Estado de los datos: qué fuente registró hoy, cuál se atrasó, qué permiso se cayó y cuándo fue el último respaldo.
 * Útil después de activar bloqueadores de apps o modos de ahorro que pueden cortar permisos.
 */
@Composable
fun PantallaEstadoDatos(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var version by remember { mutableIntStateOf(0) }
    var msg by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val fuentes by produceState<List<Fuente>?>(null, version, Reanudar.n) { value = runCatching { EstadoDatos.revisar(ctx) }.getOrDefault(emptyList()) }
    val cs = MaterialTheme.colorScheme
    Hoja("Estado de los datos", nav.atras, "Qué está llegando y qué no. Verde: al día. Ámbar: atrasado. Rojo: falta un permiso. Gris: apagado.") {
        val l = fuentes
        if (l == null) { Ayuda("Revisando…", Modifier.padding(top = 12.dp)); return@Hoja }
        val bien = l.count { it.estado == Salud3.BIEN }
        Text("$bien de ${l.size} fuentes al día", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
        if (l.any { it.estado == Salud3.SIN_PERMISO }) Ayuda("Hay permisos caídos. Si usas un bloqueador de apps en modo estricto, desactívalo un momento para volver a darlos.", Modifier.padding(top = 4.dp))
        BotonPrincipal(if (ocupado) "Sincronizando…" else "Sincronizar todo ahora", Modifier.fillMaxWidth().padding(top = 10.dp), habilitado = !ocupado) {
            ocupado = true
            scope.launch { msg = runCatching { withContext(Dispatchers.IO) { Sincronizar.todo(ctx) } }.getOrElse { "No se pudo: ${it.message}" }; ocupado = false; version++ }
        }
        msg?.let { Ayuda(it, Modifier.padding(top = 4.dp)) }
        listOf(Salud3.SIN_PERMISO to "Faltan permisos", Salud3.ATRASADA to "Atrasadas", Salud3.BIEN to "Al día", Salud3.APAGADA to "Apagadas").forEach { (estado, titulo) ->
            val grupo = l.filter { it.estado == estado }
            if (grupo.isNotEmpty()) Seccion(titulo) {
                Tarjeta {
                    grupo.forEachIndexed { i, f ->
                        if (i > 0) Separador()
                        Row(Modifier.fillMaxWidth().then(if (f.arreglo != null && f.arreglo != "sincronizar") Modifier.clickable { nav.ir(f.arreglo) } else Modifier).padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            val color = when (f.estado) { Salud3.BIEN -> cs.primary; Salud3.ATRASADA -> cs.tertiary; Salud3.SIN_PERMISO -> cs.error; Salud3.APAGADA -> cs.outline }
                            Box(Modifier.size(12.dp).background(color, CircleShape))
                            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(f.nombre, style = MaterialTheme.typography.bodyLarge)
                                Text(f.detalle, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                            }
                            if (f.arreglo != null && f.arreglo != "sincronizar") Text("›", style = MaterialTheme.typography.titleMedium, color = cs.primary)
                        }
                    }
                }
            }
        }
        // Lo que devolvió Health Connect en la última sincronización, tipo por tipo: sirve para saber por qué algo no llega.
        val diag = Repo.config.hc_diagnostico
        if (diag.isNotEmpty()) Seccion("Detalle de Health Connect", "Última lectura: " + (Repo.config.hc_diagnostico_ts?.let { Fechas.corto(it.take(10)) + " " + Fechas.horaDe(it) } ?: "?"), plegable = true, plegadaPorDefecto = true) {
            Tarjeta { diag.forEach { (k, v) -> Clave(k, v, 18) } }
        }
        BotonTexto(onClick = { nav.ir("cobertura") }, modifier = Modifier.padding(top = 8.dp)) { Text("cobertura de cada variable ›", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
        BotonTexto(onClick = { nav.ir("permisos") }) { Text("abrir Permisos y sensores ›", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
    }
}
