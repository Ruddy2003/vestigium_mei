@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.avisos.Programador
import io.github.ruddy2003.vestigium.data.Exportar
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Importar
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.reportes.Reportes
import io.github.ruddy2003.vestigium.sensores.Geocercas
import io.github.ruddy2003.vestigium.sensores.Pantalla
import io.github.ruddy2003.vestigium.sensores.Salud
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Reanudar
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.widget.Widgets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ---------------- Permisos ----------------
@Composable
fun PantallaPermisos(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val r = Reanudar.n
    var hc by remember { mutableStateOf<Set<String>>(emptySet()) }
    var refrescar by remember { mutableStateOf(0) }
    LaunchedEffect(r, refrescar) { hc = Salud.concedidos(ctx) }

    val notif = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refrescar++ }
    val ubic = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { refrescar++ }
    val fondo = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refrescar++; Geocercas.registrar(ctx) }
    val salud = rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { refrescar++; scope.launch { Salud.sincronizar(ctx, 30) } }
    @Suppress("UNUSED_VARIABLE") val k = refrescar

    val primera = !Repo.config.bienvenida_vista
    Hoja(if (primera) "Bienvenido" else "Permisos y sensores", if (primera) null else nav.atras,
        if (primera) "Vestigium Mei junta en un solo lugar tu diario, tus momentos, tu cuerpo y tus lugares. Todo vive cifrado por el sistema en tu teléfono; nada pasa por servidores ajenos. Activa lo que quieras medir; puedes cambiarlo después."
        else "Cada permiso habilita una fuente de datos. Ninguno es obligatorio.") {

        Seccion("Avisos") {
            Tarjeta {
                Estado("Notificaciones", Avisos.permitido(ctx), "Ánimo durante el día, sueños por la mañana y diario por la noche.") { notif.launch(Manifest.permission.POST_NOTIFICATIONS) }
                HorizontalDivider()
                val pm = ctx.getSystemService(PowerManager::class.java)
                Estado("Sin restricción de batería", pm.isIgnoringBatteryOptimizations(ctx.packageName), "Hace que los avisos y la ubicación de fondo lleguen a tiempo.") {
                    runCatching { ctx.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + ctx.packageName))) }
                }
            }
        }
        Seccion("Ubicación") {
            Tarjeta {
                Estado("Mientras usas la app", Ubicacion.tienePermiso(ctx), "Para guardar dónde estabas en cada momento y gasto.") {
                    ubic.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                }
                HorizontalDivider()
                Estado("Todo el tiempo", Ubicacion.tieneSegundoPlano(ctx), "Para el mapa de calor y para registrar llegadas a tus lugares. Android te llevará a ajustes: elige «Permitir todo el tiempo». Gasta entre 1 y 3% de batería al día.",
                    habilitado = Ubicacion.tienePermiso(ctx)) { fondo.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION) }
            }
        }
        Seccion("Cuerpo") {
            Tarjeta {
                val disp = Salud.disponible(ctx)
                Estado("Health Connect", hc.isNotEmpty(), if (disp) "Sueño por etapas, frecuencia cardiaca, HRV, pasos y oxígeno de tu reloj. En Samsung Health activa también: Ajustes → Health Connect → permitir escribir." else "Health Connect no está disponible en este teléfono.", habilitado = disp) {
                    salud.launch(Salud.PERMISOS)
                }
                if (hc.isNotEmpty()) TextButton(onClick = { scope.launch { val n = Salud.sincronizar(ctx, 30); android.widget.Toast.makeText(ctx, "Sincronicé $n días", android.widget.Toast.LENGTH_SHORT).show() } }) { Text("Sincronizar los últimos 30 días ahora") }
            }
        }
        Seccion("Teléfono") {
            Tarjeta {
                Estado("Tiempo en pantalla", Pantalla.tienePermiso(ctx), "Minutos por app y desbloqueos por día. Android te llevará a «Acceso a datos de uso»: busca Vestigium Mei y actívalo.") {
                    ctx.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
                if (Pantalla.tienePermiso(ctx)) TextButton(onClick = { scope.launch(Dispatchers.IO) { Pantalla.sincronizar(ctx, 7) } }) { Text("Calcular los últimos 7 días ahora") }
            }
        }
        if (primera) {
            BotonPrincipal("Empezar", Modifier.fillMaxWidth().padding(top = 20.dp)) {
                Repo.cambiarConfig { it.copy(bienvenida_vista = true) }
                Programador.ahora(ctx)
                nav.pestana("dia")
            }
            Ayuda("Después agrega los widgets: mantén presionada tu pantalla de inicio → Widgets → Vestigium Mei. Y el botón rápido: baja el panel de ajustes rápidos → lápiz → arrastra «Momento».", Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun Estado(titulo: String, ok: Boolean, detalle: String, habilitado: Boolean = true, accion: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            Ayuda(detalle)
        }
        if (ok) Text("Activo", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp))
        else OutlinedButton(onClick = accion, enabled = habilitado, modifier = Modifier.padding(start = 8.dp)) { Text("Activar") }
    }
}

// ---------------- Ajustes ----------------
@Composable
fun PantallaAjustes(nav: Nav) {
    val ctx = LocalContext.current
    val c = Repo.config
    fun cambiar(f: (io.github.ruddy2003.vestigium.data.Config) -> io.github.ruddy2003.vestigium.data.Config) {
        Repo.cambiarConfig(f); runCatching { Widgets.actualizar(ctx) }
    }
    Hoja("Ajustes", nav.atras) {
        Seccion("Avisos de ánimo durante el día", "Horas aleatorias dentro de la ventana, separadas entre sí. Lo aleatorio evita que solo registres los momentos predecibles.") {
            Tarjeta {
                Interruptor("Avisos activos", c.avisos) { v -> cambiar { it.copy(avisos = v) } }
                Etiqueta("Avisos por día: ${c.muestreos_por_dia}")
                Slider(value = c.muestreos_por_dia.toFloat(), onValueChange = { v -> cambiar { it.copy(muestreos_por_dia = v.toInt(), plan_muestreo = emptyMap()) } }, valueRange = 0f..6f, steps = 5)
                Etiqueta("Ventana: de ${c.ventana_inicio}:00 a ${c.ventana_fin}:00")
                Slider(value = c.ventana_inicio.toFloat(), onValueChange = { v -> cambiar { it.copy(ventana_inicio = v.toInt().coerceAtMost(it.ventana_fin - 2), plan_muestreo = emptyMap()) } }, valueRange = 6f..20f, steps = 13)
                Slider(value = c.ventana_fin.toFloat(), onValueChange = { v -> cambiar { it.copy(ventana_fin = v.toInt().coerceAtLeast(it.ventana_inicio + 2), plan_muestreo = emptyMap()) } }, valueRange = 9f..23f, steps = 13)
                Ayuda("Los cambios aplican desde mañana, o desde hoy si todavía no te llegó ningún aviso.")
            }
        }
        Seccion("Recordatorios") {
            Tarjeta {
                Etiqueta("Sueños por la mañana: ${c.hora_sueno}:00")
                Slider(value = c.hora_sueno.toFloat(), onValueChange = { v -> cambiar { it.copy(hora_sueno = v.toInt()) } }, valueRange = 5f..12f, steps = 6)
                Etiqueta("Diario por la noche: ${c.hora_diario}:00")
                Slider(value = c.hora_diario.toFloat(), onValueChange = { v -> cambiar { it.copy(hora_diario = v.toInt()) } }, valueRange = 18f..23f, steps = 4)
            }
        }
        Seccion("Privacidad") {
            Tarjeta {
                Interruptor("Bloquear con huella", c.bloqueo, "Pide tu huella o PIN al abrir la app. Los widgets y botones rápidos siguen funcionando sin desbloquear.") { v -> cambiar { it.copy(bloqueo = v) } }
                Interruptor("Modo discreto", c.discreto, "Los widgets y reportes muestran letras en vez de nombres de emociones.") { v -> cambiar { it.copy(discreto = v) } }
                Interruptor("Seguimiento privado", c.seguimiento_privado, "Agrega al día la intensidad del impulso, recaídas y detonantes.") { v -> cambiar { it.copy(seguimiento_privado = v) } }
                Interruptor("Ubicación de fondo", c.muestreo_ubicacion, "Una muestra cada 15 minutos para el mapa de calor.") { v -> cambiar { it.copy(muestreo_ubicacion = v) } }
            }
        }
    }
}

// ---------------- Reportes ----------------
@Composable
fun PantallaReportes(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    fun abrir(uri: Uri?, mime: String) {
        if (uri == null) { msg = "No pude guardar el reporte."; return }
        runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)) }
            .onFailure { msg = "Guardado en tu galería (Pictures/VestigiumMei) o en Descargas." }
    }
    Hoja("Reportes", nav.atras, "Cada lunes llega el de la semana anterior y cada día 1 el del mes anterior. Se guardan en la galería y en Descargas, sin mapas ni direcciones.") {
        Seccion("Generar ahora") {
            Tarjeta {
                val hoy = Fechas.diaLogico()
                OutlinedButton(onClick = { scope.launch { abrir(withContext(Dispatchers.IO) { Reportes.semanal(ctx, Fechas.lunesDe(hoy), false) }, "image/png") } }, modifier = Modifier.fillMaxWidth()) { Text("Esta semana (imagen)") }
                OutlinedButton(onClick = { scope.launch { abrir(withContext(Dispatchers.IO) { Reportes.semanal(ctx, Fechas.sumar(Fechas.lunesDe(hoy), -7), false) }, "image/png") } }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) { Text("Semana pasada (imagen)") }
                OutlinedButton(onClick = { scope.launch { abrir(withContext(Dispatchers.IO) { Reportes.mensual(ctx, Fechas.mesDe(hoy), false) }, "application/pdf") } }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) { Text("Este mes (PDF)") }
                OutlinedButton(onClick = { scope.launch { abrir(withContext(Dispatchers.IO) { Reportes.mensual(ctx, Fechas.mesDe(Fechas.iso(Fechas.parse(hoy).minusMonths(1))), false) }, "application/pdf") } }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) { Text("Mes pasado (PDF)") }
                msg?.let { Ayuda(it, Modifier.padding(top = 8.dp)) }
            }
        }
    }
}

// ---------------- Datos ----------------
@Composable
fun PantallaDatos(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }

    fun tarea(b: suspend () -> String) { scope.launch { ocupado = true; msg = try { withContext(Dispatchers.IO) { b() } } catch (e: Exception) { "Algo falló: ${e.message}" }; ocupado = false } }

    val carpeta = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            ctx.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            Repo.cambiarConfig { it.copy(carpeta_respaldo = uri.toString()) }
            tarea { if (Exportar.respaldoAutomatico(ctx)) "Carpeta lista. Cada mañana se actualizan ahí tus archivos." else "No pude escribir en esa carpeta." }
        }
    }
    val respaldo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) tarea { Importar.respaldo(ctx, uri).mensaje } }
    val obsidian = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris -> if (uris.isNotEmpty()) tarea { Importar.obsidian(ctx, uris).mensaje } }
    val maps = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) tarea { Importar.cronologia(ctx, uri).mensaje } }

    Hoja("Datos", nav.atras, "Todo lo que guarda la app, listo para R.") {
        msg?.let { Aviso(it) }
        if (ocupado) Ayuda("Trabajando…", Modifier.padding(top = 8.dp))
        Seccion("Exportar", "Un .zip con un CSV por tabla, un respaldo completo en JSON y un archivo LEEME con las convenciones. Desde el menú de compartir puedes guardarlo directo en tu Drive.") {
            BotonPrincipal("Exportar y compartir", Modifier.fillMaxWidth()) { scope.launch { ocupado = true; withContext(Dispatchers.IO) { Exportar.zip(ctx) }; Exportar.compartir(ctx); ocupado = false } }
        }
        Seccion("Respaldo automático", "Elige una carpeta del teléfono; cada mañana la app deja ahí todos los CSV actualizados. Si eliges una carpeta que sincronizas con la computadora, R siempre tendrá los datos frescos.") {
            Tarjeta {
                Ayuda(Repo.config.carpeta_respaldo?.let { "Carpeta configurada." } ?: "Sin carpeta.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    OutlinedButton(onClick = { carpeta.launch(null) }) { Text("Elegir carpeta") }
                    if (Repo.config.carpeta_respaldo != null) OutlinedButton(onClick = { tarea { if (Exportar.respaldoAutomatico(ctx)) "Respaldo escrito." else "No pude escribir en la carpeta." } }) { Text("Respaldar ahora") }
                }
            }
        }
        Seccion("Importar") {
            Tarjeta {
                OutlinedButton(onClick = { obsidian.launch(arrayOf("text/markdown", "text/plain", "application/octet-stream", "*/*")) }, modifier = Modifier.fillMaxWidth()) { Text("Notas diarias de Obsidian (.md)") }
                Ayuda("Elige varias a la vez. Los días que ya existan aquí no se tocan.")
                OutlinedButton(onClick = { respaldo.launch(arrayOf("application/json", "*/*")) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Respaldo JSON (de esta app o de la versión web)") }
                OutlinedButton(onClick = { maps.launch(arrayOf("application/json", "*/*")) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Cronología de Google Maps (.json)") }
                Ayuda("En Google Maps: tu perfil → Tu cronología → ⋮ → Configuración → Exportar datos de Cronología.")
            }
        }
        Seccion("Etiquetas equivalentes", "Unifica variantes de una misma persona o tema (tianita → tia_anita). Se aplica en Explorar, reportes y exportación; tus textos no cambian.") {
            Equivalencias()
        }
        Seccion("Categorías de gasto") { Categorias() }
        Seccion("Resumen") {
            Tarjeta {
                Text("${Repo.dias.size} días · ${Repo.momentos.size} momentos · ${Repo.gastos.size} gastos · ${Repo.respuestas.size} cuestionarios · ${Repo.reacciones.size} pruebas · ${Repo.salud.size} días de salud · ${Repo.pantalla.size} días de pantalla",
                    style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                Repo.config.ultimo_sync?.let { Ayuda("Última sincronización: ${Fechas.horaDe(it)} del ${it.take(10)}") }
            }
        }
    }
}

@Composable
private fun Equivalencias() {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    val eq = Repo.config.equivalencias
    val sugeridos = remember(Repo.dias.size, eq.size) {
        val p = HashMap<String, Int>(); val t = HashMap<String, Int>()
        Repo.dias.values.forEach { d -> Texto.personas(d.nota).forEach { p[it] = (p[it] ?: 0) + 1 }; Texto.temas(d.nota).forEach { t[it] = (t[it] ?: 0) + 1 } }
        val out = ArrayList<Triple<Char, String, String>>()
        for ((m, s) in listOf(p to '@', t to '#')) {
            val ks = m.keys.filter { it !in eq }
            for (i in ks.indices) for (j in i + 1 until ks.size) if (Texto.parecidos(ks[i], ks[j]) && Texto.canon(ks[i], eq) != Texto.canon(ks[j], eq)) out.add(Triple(s, ks[i], ks[j]))
        }
        out.take(15)
    }
    Tarjeta {
        if (sugeridos.isNotEmpty()) {
            Etiqueta("Posibles duplicados · toca la forma que quieres conservar")
            sugeridos.forEach { (s, x, y) ->
                FlowRow(Modifier.padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Ficha("$s$x", false) { Repo.cambiarConfig { it.copy(equivalencias = it.equivalencias + (y to x)) } }
                    Ficha("$s$y", false) { Repo.cambiarConfig { it.copy(equivalencias = it.equivalencias + (x to y)) } }
                }
            }
        }
        Etiqueta("Reglas activas")
        if (eq.isEmpty()) Ayuda("Ninguna.")
        eq.forEach { (k, v) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$k → $v", fontFamily = FontFamily.Monospace, fontSize = 13.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = { Repo.cambiarConfig { it.copy(equivalencias = it.equivalencias - k) } }) { Text("Quitar") }
            }
        }
        Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = a, onValueChange = { a = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("variante") })
            OutlinedTextField(value = b, onValueChange = { b = it }, modifier = Modifier.weight(1f).padding(start = 6.dp), singleLine = true, placeholder = { Text("canónica") })
            TextButton(onClick = {
                val x = Texto.etiqueta(a); val y = Texto.etiqueta(b)
                if (x.isNotEmpty() && y.isNotEmpty() && x != y) { Repo.cambiarConfig { it.copy(equivalencias = it.equivalencias + (x to y)) }; a = ""; b = "" }
            }) { Text("Añadir") }
        }
    }
}

@Composable
private fun Categorias() {
    var nueva by remember { mutableStateOf("") }
    Tarjeta {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Repo.config.categorias.forEach { c -> Ficha("$c ×", false) { Repo.cambiarConfig { it.copy(categorias = it.categorias - c) } } }
        }
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = nueva, onValueChange = { nueva = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Nueva categoría") })
            TextButton(onClick = { val n = nueva.trim(); if (n.isNotEmpty() && n !in Repo.config.categorias) { Repo.cambiarConfig { it.copy(categorias = it.categorias + n) }; nueva = "" } }) { Text("Añadir") }
        }
        Ayuda("Tocar una categoría la quita de la lista; los gastos ya registrados la conservan.", Modifier.padding(top = 6.dp))
    }
}
