package io.github.ruddy2003.vestigium.ui.pantallas

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Escalas
import io.github.ruddy2003.vestigium.ciencia.Instrumento
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Db
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Geo
import io.github.ruddy2003.vestigium.data.Lugar
import io.github.ruddy2003.vestigium.data.Reaccion
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Respuesta
import io.github.ruddy2003.vestigium.sensores.Geocercas
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.FilaMenu
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

// ---------------- Cuestionarios ----------------
@Composable
fun PantallaEscalas(nav: Nav) {
    var abierto by remember { mutableStateOf<Instrumento?>(null) }
    val inst = abierto
    if (inst != null) { Cuestionario(inst) { abierto = null }; return }
    Hoja("Cuestionarios", nav.atras, "Instrumentos publicados y validados. Te permiten compararte contigo mismo a lo largo de los años.") {
        Seccion("Disponibles") {
            Tarjeta {
                Escalas.TODAS.forEach { i ->
                    val u = Repo.ultimaRespuesta(i.id)
                    val dias = u?.let { java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(it.fecha), java.time.LocalDate.now()) }
                    val estado = when { u == null -> "Nunca respondido"; dias!! >= i.frecuenciaDias -> "Toca responder · último hace $dias días"; else -> "Al día · último el ${u.fecha}" }
                    FilaMenu(i.nombre, "${i.mide} · $estado") { abierto = i }
                }
            }
        }
        Escalas.TODAS.forEach { i ->
            val hist = Repo.respuestas.filter { it.instrumento == i.id }
            if (hist.size >= 2) Seccion("Historial · ${i.nombre}") {
                Tarjeta {
                    val clave = hist.last().puntajes.keys.firstOrNull { it == "porcentaje" || it == "total" } ?: hist.last().puntajes.keys.first()
                    Linea(hist.mapNotNull { it.puntajes[clave] })
                    Ayuda(hist.takeLast(6).joinToString(" · ") { "${it.fecha.substring(5)}: ${"%.0f".format(it.puntajes[clave] ?: 0.0)}" })
                }
            }
        }
    }
}

@Composable
private fun Cuestionario(i: Instrumento, cerrar: () -> Unit) {
    val resp = remember { mutableStateOf(mapOf<String, Double>()) }
    var listo by remember { mutableStateOf<Respuesta?>(null) }
    Hoja(i.nombre, cerrar, i.instruccion) {
        val hecho = listo
        if (hecho != null) {
            Seccion("Resultado") { Tarjeta { Text(i.lectura(hecho.puntajes), style = MaterialTheme.typography.bodyLarge) } }
            Ayuda(i.referencia, Modifier.padding(top = 12.dp))
            BotonPrincipal("Listo", Modifier.fillMaxWidth().padding(top = 16.dp)) { cerrar() }
            return@Hoja
        }
        i.items.forEachIndexed { n, it ->
            Seccion("${n + 1}. ${it.texto.replaceFirstChar { c -> c.uppercase() }}") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    i.opciones.forEach { (v, etq) -> Ficha(etq, resp.value[it.clave] == v) { resp.value = resp.value + (it.clave to v) } }
                }
            }
        }
        val completo = i.items.all { resp.value.containsKey(it.clave) }
        if (!completo) Ayuda("Faltan ${i.items.count { !resp.value.containsKey(it.clave) }} respuestas.", Modifier.padding(top = 12.dp))
        BotonPrincipal("Guardar", Modifier.fillMaxWidth().padding(top = 16.dp), habilitado = completo) {
            val r = Respuesta(Repo.nuevoId(), Fechas.diaLogico(), Fechas.ahoraIso(), i.id, resp.value, i.interpretar(resp.value))
            Repo.guardarRespuesta(r); listo = r
        }
        Ayuda(i.adaptacion, Modifier.padding(top = 10.dp))
    }
}

@Composable
private fun Linea(v: List<Double>) {
    val c = MaterialTheme.colorScheme.primary
    val l = MaterialTheme.colorScheme.outline
    Canvas(Modifier.fillMaxWidth().height(90.dp)) {
        if (v.size < 2) return@Canvas
        val min = v.min(); val max = v.max().let { if (it == min) min + 1 else it }
        drawLine(l, Offset(0f, size.height - 1), Offset(size.width, size.height - 1), 1.5f)
        var prev: Offset? = null
        v.forEachIndexed { k, x ->
            val p = Offset(k * size.width / (v.size - 1), (size.height - 8 - (x - min) / (max - min) * (size.height - 16)).toFloat())
            prev?.let { drawLine(c, it, p, 5f) }
            drawCircle(c, 7f, p); prev = p
        }
    }
}

// ---------------- Prueba de reacción ----------------
/** Versión breve de la prueba de vigilancia psicomotora: 10 estímulos con espera aleatoria. */
@Composable
fun PantallaReaccion(nav: Nav) {
    var fase by remember { mutableStateOf("inicio") } // inicio, espera, estimulo, fin
    var ensayo by remember { mutableIntStateOf(0) }
    var inicioEstimulo by remember { mutableLongStateOf(0L) }
    val tiempos = remember { mutableStateOf(listOf<Int>()) }
    var anticipaciones by remember { mutableIntStateOf(0) }
    var mensaje by remember { mutableStateOf("") }
    val total = 10
    val scope = rememberCoroutineScope()

    LaunchedEffect(fase, ensayo) {
        if (fase == "espera") {
            delay(1500L + Random.nextLong(2500L))
            if (fase == "espera") { fase = "estimulo"; inicioEstimulo = SystemClock.uptimeMillis() }
        }
    }

    fun toque() {
        when (fase) {
            "espera" -> { anticipaciones++; mensaje = "Muy pronto. Espera a que aparezca el círculo."; fase = "pausa"; scope.launch { delay(900); fase = "espera" } }
            "estimulo" -> {
                val rt = (SystemClock.uptimeMillis() - inicioEstimulo).toInt()
                tiempos.value = tiempos.value + rt
                mensaje = "$rt ms"
                if (tiempos.value.size >= total) {
                    fase = "fin"
                    val t = tiempos.value
                    val ord = t.sorted()
                    val med = if (ord.size % 2 == 1) ord[ord.size / 2].toDouble() else (ord[ord.size / 2 - 1] + ord[ord.size / 2]) / 2.0
                    Repo.guardarReaccion(Reaccion(Repo.nuevoId(), Fechas.ahoraIso(), Fechas.diaLogico(), t, t.average(), med, t.count { it >= 500 }, anticipaciones))
                } else { fase = "pausa"; scope.launch { delay(600); ensayo++; fase = "espera" } }
            }
        }
    }

    Hoja("Prueba de reacción", nav.atras, "Toca la pantalla en cuanto aparezca el círculo. Son $total intentos, unos 30 segundos. Mide tu atención: el sueño, el alcohol o el cansancio la cambian.") {
        val fondo = MaterialTheme.colorScheme.surfaceVariant
        val acento = MaterialTheme.colorScheme.primary
        Box(
            Modifier.fillMaxWidth().aspectRatio(1f).padding(top = 16.dp).clip(RoundedCornerShape(16.dp)).background(fondo)
                .pointerInput(Unit) { detectTapGestures(onPress = { toque() }) },
            contentAlignment = Alignment.Center,
        ) {
            when (fase) {
                "inicio" -> Text("Toca «Empezar»", style = MaterialTheme.typography.titleMedium)
                "estimulo" -> Box(Modifier.size(140.dp).clip(CircleShape).background(acento))
                "fin" -> Text("Terminado", style = MaterialTheme.typography.titleMedium)
                else -> Text(mensaje.ifEmpty { "Espera…" }, style = MaterialTheme.typography.titleMedium)
            }
        }
        Ayuda("Intento ${minOf(tiempos.value.size + 1, total)} de $total" + if (anticipaciones > 0) " · $anticipaciones anticipaciones" else "", Modifier.padding(top = 8.dp))
        if (fase == "inicio" || fase == "fin") BotonPrincipal(if (fase == "fin") "Repetir" else "Empezar", Modifier.fillMaxWidth().padding(top = 12.dp)) {
            tiempos.value = emptyList(); anticipaciones = 0; ensayo = 0; mensaje = ""; fase = "espera"
        }
        if (fase == "fin") {
            val t = tiempos.value
            Seccion("Resultado") {
                Tarjeta {
                    Text("Promedio ${t.average().toInt()} ms · el más rápido ${t.min()} ms", style = MaterialTheme.typography.bodyLarge)
                    Ayuda("${t.count { it >= 500 }} lapsos (500 ms o más). La precisión depende de la pantalla del teléfono, así que compara solo contigo mismo.")
                }
            }
        }
        val hist = Repo.reacciones
        if (hist.size >= 2) Seccion("Historial") { Tarjeta { Linea(hist.mapNotNull { it.media_ms }); Ayuda("Más bajo es mejor.") } }
    }
}

// ---------------- Lugares ----------------
@Composable
fun PantallaLugares(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf<String?>("otro") }
    var radio by remember { mutableStateOf(120f) }
    var msg by remember { mutableStateOf<String?>(null) }
    var puntos by remember { mutableStateOf<List<Db.Punto>>(emptyList()) }
    LaunchedEffect(Repo.lugares.size) {
        puntos = withContext(Dispatchers.IO) { Repo.db().ubicaciones(System.currentTimeMillis() - 60L * 86_400_000) }
    }
    val muestreos = Repo.momentos.filter { it.tipo == "muestreo" && it.valencia != null }
    val general = muestreos.mapNotNull { it.valencia?.toDouble() }.takeIf { it.isNotEmpty() }?.average()

    Hoja("Lugares", nav.atras, "Marca tus lugares frecuentes. La app registra cuándo llegas y te vas, y cruza tu ánimo con cada lugar.") {
        if (!Ubicacion.tieneSegundoPlano(ctx)) Aviso("Para registrar llegadas y salidas automáticamente necesitas permitir la ubicación «todo el tiempo» en Más → Permisos y sensores.")
        Seccion("Mapa emocional", "Últimos 60 días. Cada punto gris es una muestra de ubicación; los círculos son tus lugares, en verde si ahí sueles sentirte mejor que tu promedio y en rojo si peor.") {
            Tarjeta { Mapa(puntos, Repo.lugares, muestreos) }
        }
        Seccion("Marcar dónde estoy ahora") {
            Tarjeta {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre (ej. SEDECO, casa de mis abuelos)") }, singleLine = true)
                Etiqueta("Tipo"); FilaFichas(Catalogo.TIPOS_LUGAR, tipo) { tipo = it }
                Etiqueta("Radio: ${radio.toInt()} m")
                Slider(value = radio, onValueChange = { radio = it }, valueRange = 80f..400f)
                msg?.let { Ayuda(it) }
                BotonPrincipal("Guardar este lugar", Modifier.fillMaxWidth().padding(top = 8.dp), habilitado = nombre.isNotBlank()) {
                    scope.launch {
                        msg = "Buscando tu ubicación…"
                        val loc = Ubicacion.actual(ctx, 12000)
                        if (loc == null) { msg = "No pude obtener tu ubicación. Revisa el permiso de ubicación." ; return@launch }
                        Repo.guardarLugar(Lugar(Repo.nuevoId(), nombre.trim(), loc.latitude, loc.longitude, radio.toInt(), tipo ?: "otro"))
                        Geocercas.registrar(ctx)
                        msg = "Guardado (precisión ${loc.accuracy.toInt()} m)."; nombre = ""
                    }
                }
            }
        }
        Seccion("Tus lugares") {
            if (Repo.lugares.isEmpty()) Vacio("Aún no marcas lugares.")
            Repo.lugares.forEach { l ->
                var borrar by remember(l.id) { mutableStateOf(false) }
                val vis = Repo.visitas.filter { it.lugar == l.id && it.entrada >= Fechas.sumar(Fechas.hoyCalendario(), -30) }
                val horas = vis.sumOf { v -> v.salida?.let { (Fechas.millis(it) - Fechas.millis(v.entrada)) / 3_600_000.0 } ?: 0.0 }
                val aqui = muestreos.filter { m -> m.lugar == l.id || (m.lat != null && m.lon != null && Geo.distanciaM(m.lat, m.lon, l.lat, l.lon) <= l.radio_m) }
                val media = aqui.mapNotNull { it.valencia?.toDouble() }.takeIf { it.isNotEmpty() }?.average()
                val act = aqui.mapNotNull { it.activacion?.toDouble() }.takeIf { it.isNotEmpty() }?.average()
                Tarjeta(Modifier.padding(bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(l.nombre, style = MaterialTheme.typography.titleMedium)
                            Ayuda("${vis.size} visitas en 30 días · ${"%.1f".format(horas)} h")
                            Ayuda(if (aqui.size < 5) "Pocos datos de ánimo aquí (${aqui.size}); se necesitan 5." else
                                "Valencia aquí ${"%.1f".format(media)}" + (general?.let { g -> " (tu promedio ${"%.1f".format(g)})" } ?: "") + (act?.let { " · energía ${"%.1f".format(it)}" } ?: ""))
                        }
                        TextButton(onClick = { if (borrar) { Repo.borrarLugar(l.id); Geocercas.registrar(ctx) } else borrar = true }) {
                            Text(if (borrar) "¿Borrar?" else "Quitar", color = if (borrar) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        Ayuda("${Repo.db().contarUbicaciones()} puntos de ubicación guardados en total.", Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun Mapa(puntos: List<Db.Punto>, lugares: List<Lugar>, muestreos: List<io.github.ruddy2003.vestigium.data.Momento>) {
    val tenue = MaterialTheme.colorScheme.onSurfaceVariant
    val neutro = MaterialTheme.colorScheme.outline
    val general = muestreos.mapNotNull { it.valencia?.toDouble() }.takeIf { it.isNotEmpty() }?.average()
    if (puntos.isEmpty() && lugares.isEmpty()) { Ayuda("Aparecerá cuando haya ubicaciones guardadas."); return }
    val lats = puntos.map { it.lat } + lugares.map { it.lat }
    val lons = puntos.map { it.lon } + lugares.map { it.lon }
    // Recorte al 2-98% para que un viaje no aplaste la ciudad
    fun recorte(v: List<Double>): Pair<Double, Double> { val s = v.sorted(); return s[(s.size * 0.02).toInt()] to s[((s.size - 1) * 0.98).toInt()] }
    val (la0, la1) = recorte(lats); val (lo0, lo1) = recorte(lons)
    Canvas(Modifier.fillMaxWidth().aspectRatio(1f)) {
        val dLa = (la1 - la0).coerceAtLeast(0.002); val dLo = (lo1 - lo0).coerceAtLeast(0.002)
        val esc = minOf(size.width / (dLo * Math.cos(Math.toRadians((la0 + la1) / 2))), size.height / dLa) * 0.9
        val cx = size.width / 2; val cy = size.height / 2
        val mLa = (la0 + la1) / 2; val mLo = (lo0 + lo1) / 2
        fun p(la: Double, lo: Double) = Offset((cx + (lo - mLo) * Math.cos(Math.toRadians(mLa)) * esc).toFloat(), (cy - (la - mLa) * esc).toFloat())
        puntos.forEach { drawCircle(tenue.copy(alpha = 0.18f), 4f, p(it.lat, it.lon)) }
        lugares.forEach { l ->
            val aqui = muestreos.filter { it.lugar == l.id }.mapNotNull { it.valencia?.toDouble() }
            val color = if (aqui.size >= 5 && general != null) {
                val d = (aqui.average() - general).coerceIn(-1.5, 1.5) / 1.5
                if (d >= 0) Color(0xFF2E8B57).copy(alpha = 0.35f + d.toFloat() * 0.6f) else Color(0xFFC0392B).copy(alpha = 0.35f + (-d).toFloat() * 0.6f)
            } else neutro
            val r = (l.radio_m * esc / 111_000).toFloat().coerceIn(14f, 80f)
            drawCircle(color, r, p(l.lat, l.lon))
            drawCircle(tenue, r, p(l.lat, l.lon), style = Stroke(2f))
        }
    }
    Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        lugares.take(6).forEach { Text(it.nombre, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = tenue) }
    }
}
