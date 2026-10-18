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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import io.github.ruddy2003.vestigium.data.Importaciones
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.reportes.Reportes
import io.github.ruddy2003.vestigium.data.Drive
import io.github.ruddy2003.vestigium.data.Spotify
import io.github.ruddy2003.vestigium.sensores.Actividad
import io.github.ruddy2003.vestigium.sensores.Ambiente
import io.github.ruddy2003.vestigium.sensores.Comunicacion
import io.github.ruddy2003.vestigium.sensores.Percepcion
import io.github.ruddy2003.vestigium.sensores.Agenda
import io.github.ruddy2003.vestigium.sensores.Entorno
import io.github.ruddy2003.vestigium.sensores.Galeria
import io.github.ruddy2003.vestigium.sensores.Oyente
import io.github.ruddy2003.vestigium.sensores.Sincronizar
import io.github.ruddy2003.vestigium.sensores.Geocercas
import io.github.ruddy2003.vestigium.sensores.Pantalla
import io.github.ruddy2003.vestigium.sensores.Salud
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Clave
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
    val fotos = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { refrescar++; scope.launch(Dispatchers.IO) { Galeria.sincronizar(ctx, 7); Galeria.historia(ctx) } }
    val agenda = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refrescar++; scope.launch(Dispatchers.IO) { Agenda.sincronizar(ctx); Agenda.historia(ctx) } }
    val movimiento = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refrescar++; Actividad.registrar(ctx) }
    val comunicacion = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { refrescar++; scope.launch(Dispatchers.IO) { Comunicacion.sincronizar(ctx, 7); Comunicacion.historia(ctx) } }
    val bt = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { refrescar++ }
    val microfono = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok -> refrescar++; if (ok) Repo.cambiarConfig { it.copy(medir_ruido = true) } }
    var msgSync by remember { mutableStateOf<String?>(null) }
    var sincronizando by remember { mutableStateOf(false) }
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
                Estado("Health Connect", hc.isNotEmpty(), if (disp) "Sueño por etapas, frecuencia cardiaca, HRV, pasos, oxígeno, sesiones de ejercicio (incluida escalada), peso, temperatura de la piel, respiración, presión, calorías, distancia, pisos y agua: lo que tu reloj o báscula escriban. En Samsung Health activa también: Ajustes → Health Connect → permitir escribir." else "Health Connect no está disponible en este teléfono.", habilitado = disp) {
                    salud.launch(Salud.PERMISOS)
                }
                if (hc.isNotEmpty() && !hc.containsAll(Salud.PERMISOS.filter { it.startsWith("android.permission.health.READ_") && !it.endsWith("BACKGROUND") && !it.endsWith("HISTORY") && !it.endsWith("SKIN_TEMPERATURE") }))
                    TextButton(onClick = { salud.launch(Salud.PERMISOS) }) { Text("+ conceder los permisos nuevos", style = MaterialTheme.typography.labelLarge) }
                if (hc.isNotEmpty()) TextButton(onClick = { scope.launch { val n = Salud.sincronizar(ctx, 30); android.widget.Toast.makeText(ctx, "Sincronicé $n días", android.widget.Toast.LENGTH_SHORT).show() } }) { Text("↻ sincronizar los últimos 30 días", style = MaterialTheme.typography.labelLarge) }
                HorizontalDivider()
                Estado("Movimiento", Actividad.tienePermiso(ctx), "Minutos a pie, en bici, en vehículo o quieto, detectados por el teléfono. Casi no gasta batería.") {
                    movimiento.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        }
        Seccion("Galería y agenda") {
            Tarjeta {
                Estado("Fotos", Galeria.tienePermiso(ctx), "Cuántas fotos tomaste cada día y verlas dentro del día. Las fotos no se copian ni salen del teléfono." +
                    if (Galeria.parcial(ctx)) " Diste acceso solo a algunas: elige «Permitir todo» para que los conteos sean reales." else "") {
                    fotos.launch(Galeria.permisos())
                }
                HorizontalDivider()
                Estado("Calendario", Agenda.tienePermiso(ctx), "Cuántos eventos tuviste y cuánto tiempo ocupado: sirve para cruzar tu ánimo con la carga del día.") {
                    agenda.launch(Manifest.permission.READ_CALENDAR)
                }
            }
        }
        Seccion("Notificaciones y música") {
            Tarjeta {
                Estado("Acceso a notificaciones", Oyente.activo(ctx), "Cuenta cuántas notificaciones recibes, de qué app y a qué hora (nunca lee su texto), y anota qué canción empieza a sonar.") {
                    runCatching { ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                }
                if (!Oyente.activo(ctx)) {
                    Aviso("Si Android dice «ajuste restringido»: abre la información de la app, toca ⋮ arriba a la derecha → «Permitir ajustes restringidos», y vuelve a intentarlo. Pasa con las apps instaladas fuera de la tienda.")
                    TextButton(onClick = { runCatching { ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + ctx.packageName))) } }) {
                        Text("abrir información de la app →", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        Seccion("Llamadas y mensajes", latin = "VOCES") {
            Tarjeta {
                Estado("Llamadas y SMS", Comunicacion.llamadas(ctx) && Comunicacion.sms(ctx), "Cuántas llamadas y SMS, cuántos minutos y con cuántas personas distintas. Nunca se guardan números, nombres ni textos.") {
                    comunicacion.launch(Comunicacion.permisos())
                }
            }
        }
        Seccion("Alrededor", "Cada 15 minutos el teléfono anota su wifi, si hay audífonos, coche o bocina conectados, si suena algo, la carga y la luz.", latin = "CIRCVMSTANTIA") {
            Tarjeta {
                Estado("Dispositivos Bluetooth", Percepcion.bluetooth(ctx), "Para distinguir el coche de los audífonos o una bocina. Sin esto, todo cuenta como «bluetooth».") {
                    if (android.os.Build.VERSION.SDK_INT >= 31) bt.launch(Manifest.permission.BLUETOOTH_CONNECT)
                }
                Ayuda("El nombre de tu wifi solo se puede leer con la ubicación activada; si no, se anota como «wifi (sin nombre)».", Modifier.padding(top = 4.dp))
                HorizontalDivider(Modifier.padding(vertical = 6.dp))
                Interruptor("Luz del ambiente", Repo.config.medir_luz, "Lee el sensor de luz cada 15 minutos y al registrar un momento: cuánta luz de exterior recibes al día.") { v -> Repo.cambiarConfig { it.copy(medir_luz = v) } }
                Interruptor("Ruido al registrar", Repo.config.medir_ruido && Ambiente.tienePermiso(ctx), "Al guardar un momento escucha 1.5 segundos y anota solo el nivel de ruido; el audio se descarta. Verás el aviso del micrófono un instante.") { v ->
                    if (v && !Ambiente.tienePermiso(ctx)) microfono.launch(Manifest.permission.RECORD_AUDIO) else Repo.cambiarConfig { it.copy(medir_ruido = v) }
                }
            }
        }
        Seccion("Clima") {
            Tarjeta {
                Interruptor("Clima, luz y aire", Repo.config.clima, "Temperatura, lluvia, nubes, horas de luz, presión y calidad del aire de donde estuviste, de Open-Meteo (gratis, sin cuenta). Solo se envía tu zona aproximada (~1 km).") { v ->
                    Repo.cambiarConfig { it.copy(clima = v) }
                }
                if (Repo.config.clima) TextButton(onClick = { scope.launch { val n = withContext(Dispatchers.IO) { runCatching { Entorno.sincronizar(ctx, 20) }.getOrDefault(0) }; msgSync = if (n > 0) "Clima de $n días." else "No pude traer el clima (¿hay internet y alguna ubicación guardada?)." } }) {
                    Text("↻ traer clima de los días con registro", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        BotonPrincipal(if (sincronizando) "Sincronizando…" else "Sincronizar todo ahora", Modifier.fillMaxWidth().padding(top = 18.dp), habilitado = !sincronizando) {
            scope.launch { sincronizando = true; msgSync = Sincronizar.todo(ctx, 7); sincronizando = false }
        }
        msgSync?.let { Ayuda(it, Modifier.padding(top = 6.dp)) }
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
        if (ok) Text("= on", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp))
        else BotonSecundario("activar", Modifier.padding(start = 8.dp), habilitado = habilitado) { accion() }
    }
}

// ---------------- Ajustes ----------------
@Composable
fun PantallaAjustes(nav: Nav, parte: String = "apariencia") {
    val ctx = LocalContext.current
    val c = Repo.config
    fun cambiar(f: (io.github.ruddy2003.vestigium.data.Config) -> io.github.ruddy2003.vestigium.data.Config) {
        Repo.cambiarConfig(f); runCatching { Widgets.actualizar(ctx) }
    }
    val titulo = when (parte) { "avisos" -> "Avisos y recordatorios"; "privacidad" -> "Privacidad"; else -> "Apariencia" }
    Hoja(titulo, nav.atras) {
        if (parte == "apariencia") Seccion("Tema", "Cambia colores, letras, adornos y cómo se nombran las cosas. Tus datos no cambian; los widgets también toman los colores del tema.") {
            val oscuroAhora = io.github.ruddy2003.vestigium.ui.modoOscuro(io.github.ruddy2003.vestigium.ui.TemaId.ROMANO)
            // Los temas en una cuadrícula de tres por fila.
            io.github.ruddy2003.vestigium.ui.TemaId.entries.chunked(3).forEach { fila ->
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    fila.forEach { t ->
                        io.github.ruddy2003.vestigium.ui.VistaPreviaTema(t, oscuroAhora, c.tema == t.id, Modifier.weight(1f)) { cambiar { it.copy(tema = t.id) } }
                    }
                    repeat(3 - fila.size) { androidx.compose.foundation.layout.Spacer(Modifier.weight(1f)) }
                }
            }
            Ayuda(io.github.ruddy2003.vestigium.ui.Estilo.tema.descripcion, Modifier.padding(top = 2.dp))
            Etiqueta("Claro u oscuro")
            if (io.github.ruddy2003.vestigium.ui.Estilo.tema.soloOscuro) Ayuda("El cyberpunk solo existe en oscuro.")
            else Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("sistema" to "Como el teléfono", "claro" to "Claro", "oscuro" to "Oscuro").forEach { (id, nombre) ->
                    Ficha(nombre, c.modo == id) { cambiar { it.copy(modo = id) } }
                }
            }
            Ayuda("También puedes cambiarlo con el sol o la luna junto al título de cada pantalla.", Modifier.padding(top = 6.dp))
            Tarjeta(Modifier.padding(top = 10.dp)) {
                Interruptor("El ícono de la app cambia con el tema", c.icono_tema, "La huella toma la textura del tema. Se aplica al salir de la app; en algunos teléfonos el ícono de la pantalla de inicio desaparece y hay que volver a ponerlo desde la lista de apps.") { v -> cambiar { it.copy(icono_tema = v) } }
                Interruptor("Animaciones", c.animaciones, "Emblemas que se mueven, transiciones entre pantallas, cifras que cuentan, barras que crecen y una pequeña celebración al completar el día.") { v -> cambiar { it.copy(animaciones = v) } }
                if (c.animaciones) Interruptor("Fondo vivo", c.fondo_vivo, "Algo se mueve muy despacio detrás de todo: estrellas que titilan, luz de vitral, luciérnagas, nieve, nubes de la estepa, reflejos de agua. Gasta un poco más de batería.") { v -> cambiar { it.copy(fondo_vivo = v) } }
                Interruptor("Vibraciones", c.vibracion, "Cada tema vibra distinto: cincel, sello de cera, máquina de escribir, relé, armadura, madera…") { v -> cambiar { it.copy(vibracion = v) } }
                Interruptor("Sonidos", c.sonidos, "Sonidos del tema al tocar, elegir, pasar de pantalla, abrir el diario, guardar y completar el día. Cambian un poco cada vez. No suenan con el teléfono en silencio o en vibración.") { v ->
                    cambiar { it.copy(sonidos = v) }
                    if (v) io.github.ruddy2003.vestigium.ui.Sentidos.preparar(ctx)
                }
                androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    io.github.ruddy2003.vestigium.ui.Sentidos.Evento.entries.filter { it.name in setOf("TOQUE", "ELEGIR", "PASAR", "ABRIR", "GUARDAR", "LOGRO") }
                        .map { it.name.lowercase() to it }.forEach { (n, e) ->
                        BotonSecundario(n) {
                            io.github.ruddy2003.vestigium.ui.Sentidos.sentir(ctx, e)
                            if (e == io.github.ruddy2003.vestigium.ui.Sentidos.Evento.LOGRO) io.github.ruddy2003.vestigium.ui.Fiesta.lanzar()
                        }
                    }
                }
            }
            Tarjeta(Modifier.padding(top = 10.dp)) {
                Etiqueta("Tamaño del texto: ${(c.letra * 100).toInt()}%")
                Slider(value = c.letra, onValueChange = { v -> cambiar { it.copy(letra = (Math.round(v * 20) / 20f)) } }, valueRange = 0.8f..1.4f, steps = 11)
                Text("Así se lee un renglón del diario con este tamaño.", style = MaterialTheme.typography.bodyLarge)
                Interruptor("Modo nocturno cálido", c.nocturno, "De 22:00 a 5:00 la pantalla se entibia y baja un poco el brillo, para escribir el diario sin despabilarte.") { v -> cambiar { it.copy(nocturno = v) } }
            }
        }
        if (parte == "avisos") Seccion("Sonido de las notificaciones", "Cada tema suena a su manera, siempre grave y solemne: campanas, órgano, coros, synthwave. " +
            "Los avisos de ánimo usan un sonido corto; el diario, los sueños, las cartas y los cuestionarios, uno más largo.") {
            Tarjeta {
                Interruptor("Usar el sonido del tema", c.sonido_avisos, "Si lo apagas, suenan con el sonido normal del teléfono. Si silencias un tipo de aviso en los ajustes de Android, sigue en silencio aunque cambies de tema.") { v ->
                    cambiar { it.copy(sonido_avisos = v) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    listOf("probar aviso" to io.github.ruddy2003.vestigium.avisos.Avisos.CANAL_ANIMO, "probar solemne" to io.github.ruddy2003.vestigium.avisos.Avisos.CANAL_DIARIO).forEach { (n, canal) ->
                        BotonSecundario(n) {
                            io.github.ruddy2003.vestigium.avisos.Avisos.sonido(canal)?.let { r ->
                                runCatching {
                                    android.media.RingtoneManager.getRingtone(ctx, io.github.ruddy2003.vestigium.avisos.Avisos.uriSonido(ctx, r))?.apply {
                                        audioAttributes = android.media.AudioAttributes.Builder().setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION).build()
                                    }?.play()
                                }
                            }
                        }
                    }
                }
                Ayuda("Suenan al volumen de notificaciones y no suenan con el teléfono en silencio, vibración o No molestar.", Modifier.padding(top = 4.dp))
            }
        }
        if (parte == "avisos") Seccion("Avisos de ánimo durante el día", "Horas aleatorias dentro de la ventana, separadas entre sí. Lo aleatorio evita que solo registres los momentos predecibles.") {
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
        if (parte == "avisos") Seccion("Recordatorios") {
            Tarjeta {
                Etiqueta("Sueños por la mañana: ${c.hora_sueno}:00")
                Slider(value = c.hora_sueno.toFloat(), onValueChange = { v -> cambiar { it.copy(hora_sueno = v.toInt()) } }, valueRange = 5f..12f, steps = 6)
                Etiqueta("Diario por la noche: ${c.hora_diario}:00")
                Slider(value = c.hora_diario.toFloat(), onValueChange = { v -> cambiar { it.copy(hora_diario = v.toInt()) } }, valueRange = 18f..23f, steps = 4)
            }
        }
        if (parte == "avisos") Seccion("Cuestionarios", "WHO-5 y PSS-4 cada semana, UCLA-3 cada mes y TIPI cada año, contando desde tu última respuesta.") {
            Tarjeta {
                Etiqueta("Aviso el día que toca: ${c.escalas_hora}:00")
                Slider(value = c.escalas_hora.toFloat(), onValueChange = { v -> cambiar { it.copy(escalas_hora = v.toInt()) } }, valueRange = 8f..22f, steps = 13)
                Interruptor("Insistir si me atraso", c.escalas_insistir, "Con 1 a 3 días de atraso avisa dos veces al día (12:00 y tu hora); con 4 o más, tres veces (10:00, 15:00 y 20:00) hasta que respondas.") { v ->
                    cambiar { it.copy(escalas_insistir = v) }
                }
            }
        }
        if (parte == "privacidad") Seccion("Protección") {
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
    val spotify = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris -> if (uris.isNotEmpty()) tarea { Importaciones.spotify(ctx, uris) } }
    val kindle = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) tarea { Importaciones.kindle(ctx, uri) } }
    val macrofactor = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) tarea { io.github.ruddy2003.vestigium.data.Macrofactor.importar(ctx, uri) } }
    val google = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris -> if (uris.isNotEmpty()) tarea { Importaciones.google(ctx, uris) } }
    var chatsLeidos by remember { mutableStateOf<List<Importaciones.Chat>?>(null) }
    val whatsapp = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) scope.launch {
            ocupado = true
            val l = withContext(Dispatchers.IO) { runCatching { Importaciones.whatsappLeer(ctx, uris) }.getOrDefault(emptyList()) }
            ocupado = false
            if (l.isEmpty() || l.all { it.mensajes.isEmpty() }) msg = "No reconocí mensajes. En WhatsApp abre el chat → ⋮ → Más → Exportar chat → Sin archivos, y elige el .txt."
            else {
                val yo = Repo.config.whatsapp_yo
                // Si ya sé cuál autor eres y aparece en todos los chats, se guarda directo; si no, se pregunta.
                if (yo.isNotBlank() && l.all { c -> yo in c.autores }) tarea { Importaciones.whatsappGuardar(l, yo) } else chatsLeidos = l
            }
        }
    }

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
        Seccion("Google Drive", "Cada mañana reemplaza tus CSV en la carpeta «Vestigium Mei» de tu Drive y cada semana guarda una copia .zip fechada (se conservan las últimas 12). La app solo puede ver lo que ella misma sube.") {
            DriveTarjeta { tarea(it) }
        }
        Seccion("Spotify en vivo", "Conexión directa con tu cuenta: lo que escuchas en cualquier aparato (compu, bocina, coche), con artista, álbum y si venía de una playlist.", latin = "MVSICA") {
            SpotifyTarjeta { tarea(it) }
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
        Seccion("Otros servicios", "Tu historia en otras apps. Se pueden importar varias veces: lo repetido no se duplica.", latin = "ALIENA") {
            Tarjeta {
                OutlinedButton(onClick = { macrofactor.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "*/*")) }, modifier = Modifier.fillMaxWidth()) { Text("MacroFactor · nutrición y Workouts (.xlsx)") }
                Ayuda("En MacroFactor: Más → Data Management → Data Export. Elige el periodo e incluye nutrición y Workouts. Al reimportar, las fechas del archivo se reemplazan.")
                if (Repo.nutricion.isNotEmpty() || Repo.entrenos.isNotEmpty()) Clave("macrofactor", "${Repo.nutricion.size} días de nutrición · ${Repo.entrenos.values.count { it.series > 0 }} entrenos" +
                    (Repo.nutricion.keys.maxOrNull()?.let { " · hasta $it" } ?: ""))
                OutlinedButton(onClick = { spotify.launch(arrayOf("application/json", "application/zip", "*/*")) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Spotify · historial de reproducción") }
                Ayuda("spotify.com → Cuenta → Privacidad → Descargar tus datos → «Historial de reproducción ampliado». Tarda unos días; llega un .zip: elígelo completo o los Streaming_History…json.")
                OutlinedButton(onClick = { kindle.launch(arrayOf("text/plain", "*/*")) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Kindle · subrayados (My Clippings.txt)") }
                Ayuda("Conecta el Kindle a la computadora: el archivo está en la carpeta documents. Pásalo al teléfono y elígelo.")
                OutlinedButton(onClick = { google.launch(arrayOf("application/json", "application/zip", "*/*")) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Google · YouTube y búsquedas") }
                Ayuda("takeout.google.com → elige «YouTube y YouTube Music» (historial) y «Mi actividad» → formato JSON (no HTML). Elige el .zip o los archivos watch-history.json, search-history.json y MyActivity.json.")
                OutlinedButton(onClick = { whatsapp.launch(arrayOf("text/plain", "application/zip", "*/*")) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("WhatsApp · actividad de chats") }
                Ayuda("En cada chat: ⋮ → Más → Exportar chat → Sin archivos. Solo se guardan conteos (mensajes, palabras, horas y tiempos de respuesta); el texto se descarta al leerlo y nunca se guarda.")
                val nMusica = remember(msg) { runCatching { Repo.db().contar("escuchas") }.getOrDefault(0L) }
                if (nMusica > 0 || Repo.subrayados.isNotEmpty() || Repo.huellas.isNotEmpty() || Repo.chats.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    if (nMusica > 0) Clave("musica", "$nMusica reproducciones")
                    if (Repo.subrayados.isNotEmpty()) Clave("kindle", "${Repo.subrayados.size} subrayados de ${Repo.subrayados.map { it.libro }.distinct().size} libros")
                    if (Repo.huellas.isNotEmpty()) Clave("google", "${Repo.huellas.size} días · ${Repo.huellas.values.sumOf { it.yt_vistos }} videos")
                    if (Repo.chats.isNotEmpty()) Clave("whatsapp", "${Repo.chats.map { it.chat }.distinct().size} chats · ${Repo.chats.size} días")
                }
            }
        }
        Seccion("Etiquetas equivalentes", "Unifica variantes de una misma persona o tema (tianita → tia_anita). Se aplica en Explorar, reportes y exportación; tus textos no cambian.") {
            Equivalencias()
        }
        Seccion("Categorías de gasto") { Categorias() }
        Seccion("Resumen") {
            Tarjeta {
                Clave("dies", "${Repo.dias.size}"); Clave("momenta", "${Repo.momentos.size}"); Clave("svmptvs", "${Repo.gastos.size}")
                Clave("quaestiones", "${Repo.respuestas.size}"); Clave("celeritas", "${Repo.reacciones.size}"); Clave("corpus", "${Repo.salud.size} días")
                Clave("telephonum", "${Repo.pantalla.size} días"); Clave("caelum", "${Repo.entorno.size} días"); Clave("imagines", "${Repo.fotos.size} días")
                Clave("fasti", "${Repo.agenda.size} días"); Clave("monita", "${Repo.notifs.size} días"); Clave("ascensvs", "${Repo.ascensos.size} sesiones")
                Clave("bibliotheca", "${Repo.obras.size} obras"); Clave("loca", "${Repo.lugares.size} · ${Repo.visitas.size} visitas")
                Repo.config.ultimo_sync?.let { Clave("synchronia", "${Fechas.horaDe(it)} del ${it.take(10)}") }
            }
        }
    }
    chatsLeidos?.let { l ->
        val autores = l.flatMap { it.autores }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.map { it.key }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { chatsLeidos = null },
            title = { Text("¿Cuál eres tú?", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Ayuda("Leí ${l.size} chat(s) con ${l.sumOf { it.mensajes.size }} mensajes. Elige tu nombre tal como aparece en el chat para separar tus mensajes de los de los demás.")
                    FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        autores.take(12).forEach { a -> Ficha(a, false) { chatsLeidos = null; tarea { Importaciones.whatsappGuardar(l, a) } } }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { chatsLeidos = null }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
        )
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
                Text("$k → $v", fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 13.sp, modifier = Modifier.weight(1f))
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


/** Huella SHA-1 del certificado con que está firmada esta app (la que pide Google Cloud). */
@Suppress("DEPRECATION")
private fun huellaSha1(ctx: android.content.Context): String = runCatching {
    val info = ctx.packageManager.getPackageInfo(ctx.packageName, android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES)
    val firma = info.signingInfo?.apkContentsSigners?.firstOrNull() ?: return@runCatching "?"
    java.security.MessageDigest.getInstance("SHA-1").digest(firma.toByteArray()).joinToString(":") { "%02X".format(it) }
}.getOrDefault("?")

@Composable
private fun SpotifyTarjeta(tarea: (suspend () -> String) -> Unit) {
    val ctx = LocalContext.current
    val version by Spotify.version
    val msg by Spotify.mensaje
    val esperando by Spotify.esperando
    var clientId by remember { mutableStateOf(Spotify.clientId(ctx)) }
    var pegada by remember { mutableStateOf("") }
    val conectado = remember(version) { Spotify.conectado(ctx) }
    val n by androidx.compose.runtime.produceState(0L, version) { value = withContext(Dispatchers.IO) { Spotify.contar() } }
    fun copiar(texto: String) {
        ctx.getSystemService(android.content.ClipboardManager::class.java)?.setPrimaryClip(android.content.ClipData.newPlainText("Spotify", texto))
        Spotify.mensaje.value = "Copiado."
    }
    Tarjeta {
        if (conectado) {
            Clave("estado", "conectado · $n canciones por esta vía", ancho = 8)
            val u = remember(version) { Spotify.ultimo(ctx) }
            Clave("lectura", if (u > 0) Fechas.isoDeMillis(u).let { "${Fechas.horaDe(it)} del ${it.take(10)}" } else "aún no", ancho = 8)
            remember(version) { Spotify.error(ctx) }?.let { Aviso(it) }
            Ayuda("Se lee sola cada media hora. Spotify solo guarda tus últimas 50 canciones: si el teléfono pasa apagado una tarde entera de música, esas se pierden (el historial importado las recupera).", Modifier.padding(top = 6.dp))
            Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonSecundario("leer ahora") { tarea { "Spotify: ${Spotify.sincronizar(ctx)} canciones nuevas." } }
                BotonSecundario("desconectar") { Spotify.desconectar(ctx) }
            }
        } else {
            Etiqueta("una sola vez, en developer.spotify.com/dashboard")
            Ayuda("1. Entra con tu cuenta de Spotify y toca «Create app».\n" +
                "2. Nombre y descripción: los que quieras (p. ej. Vestigium Mei). Website: déjalo vacío.\n" +
                "3. En «Redirect URIs» escribe exactamente esta dirección y toca «Add»:")
            Clave("redirect", Spotify.REDIRECCION, ancho = 8)
            TextButton(onClick = { copiar(Spotify.REDIRECCION) }) { Text("copiar dirección", style = MaterialTheme.typography.labelLarge) }
            Ayuda("4. En «Which API/SDKs…» marca «Web API», acepta los términos y guarda.\n" +
                "5. En la app recién creada abre «Settings», copia el Client ID y pégalo aquí:")
            OutlinedTextField(clientId, { clientId = it.trim() }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("Client ID") })
            BotonPrincipal(if (esperando) "esperando al navegador…" else "Conectar con Spotify", Modifier.fillMaxWidth().padding(top = 8.dp)) {
                if (clientId.length < 20) Spotify.mensaje.value = "Ese Client ID se ve incompleto: son 32 letras y números."
                else runCatching {
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Spotify.iniciar(ctx, clientId))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }.onFailure { Spotify.mensaje.value = "No pude abrir el navegador." }
            }
            Ayuda("Requisito de Spotify desde 2026: el dueño de la app de desarrollador necesita Premium. Sin Premium, usa la importación del historial en «Otros servicios»: da lo mismo, solo que no al momento.", Modifier.padding(top = 6.dp))
            Etiqueta("si al volver el navegador muestra un error")
            Ayuda("Copia la dirección completa de esa página (empieza con http://127.0.0.1) y pégala aquí:")
            OutlinedTextField(pegada, { pegada = it.trim() }, Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, placeholder = { Text("http://127.0.0.1:43117/callback?code=…") })
            if (pegada.isNotBlank()) BotonSecundario("usar dirección") { val d = pegada; pegada = ""; tarea { Spotify.canjearPegada(ctx, d) } }
        }
        msg?.let { Aviso(it) }
    }
}

@Composable
private fun DriveTarjeta(tarea: (suspend () -> String) -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    var guia by remember { mutableStateOf(false) }
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { r ->
        val token = Drive.tokenDe(ctx, r.data)
        if (token == null) msg = "No se concedió el acceso." else { Repo.cambiarConfig { it.copy(drive = true) }; tarea { Drive.subir(ctx, token) } }
    }
    fun conectar() {
        scope.launch {
            when (val a = Drive.acceso(ctx)) {
                is Drive.Acceso.Listo -> { Repo.cambiarConfig { it.copy(drive = true) }; tarea { Drive.subir(ctx, a.token) } }
                is Drive.Acceso.Permiso -> permiso.launch(androidx.activity.result.IntentSenderRequest.Builder(a.intent.intentSender).build())
                is Drive.Acceso.Error -> { msg = a.mensaje; guia = true }
            }
        }
    }
    Tarjeta {
        Interruptor("Respaldo diario en Drive", Repo.config.drive, Repo.config.drive_ultimo?.let { "Último: ${Fechas.horaDe(it)} del ${it.take(10)}" } ?: "Aún no se ha subido nada.") { v ->
            if (v) conectar() else Repo.cambiarConfig { it.copy(drive = false) }
        }
        Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonSecundario("conectar y subir ahora") { conectar() }
            BotonSecundario(if (guia) "ocultar guía" else "guía de configuración") { guia = !guia }
        }
        msg?.let { Aviso(it) }
        if (guia) {
            val sha = remember { huellaSha1(ctx) }
            Etiqueta("una sola vez, en console.cloud.google.com")
            Ayuda("1. Crea un proyecto (por ejemplo «Vestigium Mei»).\n" +
                "2. APIs y servicios → Biblioteca → busca «Google Drive API» → Habilitar.\n" +
                "3. Pantalla de consentimiento de OAuth → tipo Externo → nombre de la app y tu correo. En «Usuarios de prueba» agrega tu cuenta de Gmail; después toca «Publicar app» para que el permiso no caduque cada 7 días (drive.file no requiere verificación).\n" +
                "4. Credenciales → Crear credenciales → ID de cliente de OAuth → tipo Android, con estos datos:")
            Clave("paquete", ctx.packageName, ancho = 8)
            Clave("sha-1", sha, ancho = 8)
            TextButton(onClick = {
                val cb = ctx.getSystemService(android.content.ClipboardManager::class.java)
                cb?.setPrimaryClip(android.content.ClipData.newPlainText("SHA-1", sha))
                msg = "SHA-1 copiada."
            }) { Text("copiar SHA-1", style = MaterialTheme.typography.labelLarge) }
            Ayuda("5. Vuelve aquí y toca «conectar y subir ahora». Google te pedirá elegir tu cuenta y aceptar el permiso para archivos creados por la app.")
        }
    }
}
