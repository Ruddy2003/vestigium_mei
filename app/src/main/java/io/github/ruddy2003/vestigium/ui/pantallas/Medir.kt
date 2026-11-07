@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import io.github.ruddy2003.vestigium.data.porNumero

import io.github.ruddy2003.vestigium.data.esAnimo
import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import io.github.ruddy2003.vestigium.ui.aMillis
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
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random
import io.github.ruddy2003.vestigium.ui.Deslizador
import io.github.ruddy2003.vestigium.ui.Dialogo

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
                    val atraso = Escalas.atraso(i)
                    val cada = when (i.frecuenciaDias) { 7 -> "cada semana"; 30 -> "cada mes"; 365 -> "cada año"; else -> "cada ${porNumero(i.frecuenciaDias, "1 día", "${i.frecuenciaDias} días")}" }
                    val estado = when {
                        u == null -> "nunca respondido · toca hoy"
                        atraso > 0 -> "atrasado $atraso día${if (atraso > 1) "s" else ""}"
                        atraso == 0L -> "toca hoy"
                        else -> "próximo: ${Fechas.corto(Escalas.proxima(i))}"
                    }
                    FilaMenu(i.nombre, "${i.mide} · $cada · $estado") { abierto = i }
                }
            }
        }
        Escalas.TODAS.forEach { i ->
            val hist = Repo.respuestas.filter { it.instrumento == i.id }
            // Una respuesta sin puntajes (de un respaldo raro) ya no cierra la pantalla: antes, `first()` sobre una lista vacía.
            val clave = hist.lastOrNull()?.puntajes?.keys?.let { k -> k.firstOrNull { it == "porcentaje" || it == "total" } ?: k.firstOrNull() }
            if (hist.size >= 2 && clave != null) Seccion("Historial · ${i.nombre}") {
                Tarjeta {
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
    val eg = estiloGrafica()
    Canvas(Modifier.fillMaxWidth().height(90.dp)) {
        if (v.size < 2) return@Canvas
        val min = v.min(); val max = v.max().let { if (it == min) min + 1 else it }
        ejeGrafica(eg, Offset(0f, size.height - 1), Offset(size.width, size.height - 1), l)
        val pts = v.mapIndexed { k, x -> Offset(k * size.width / (v.size - 1), (size.height - 8 - (x - min) / (max - min) * (size.height - 16)).toFloat()) }
        lineaGrafica(eg, pts, c, 5f)
        pts.forEach { puntoGrafica(eg, it, 7f, c) }
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
                // Menos de 100 ms no es una reacción a lo que viste, sino un toque anticipado: no cuenta y se repite (P-26).
                if (rt < 100) { anticipaciones++; mensaje = "Muy pronto. Espera a que aparezca el círculo."; fase = "pausa"; scope.launch { delay(900); fase = "espera" }; return }
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
            Modifier.fillMaxWidth().aspectRatio(1f).padding(top = 16.dp).clip(RoundedCornerShape(6.dp)).background(fondo)
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
    val muestreos = Repo.momentos.filter { it.esAnimo && it.valencia != null }
    val general = muestreos.mapNotNull { it.valencia?.toDouble() }.takeIf { it.isNotEmpty() }?.average()

    Hoja("Lugares", nav.atras, "Marca tus lugares frecuentes. La app registra cuándo llegas y te vas, y cruza tu ánimo con cada lugar.") {
        if (!Ubicacion.tieneSegundoPlano(ctx)) Aviso("Para registrar llegadas y salidas automáticamente necesitas permitir la ubicación «todo el tiempo» en Más → Permisos y sensores.")
        Seccion("Mapa emocional", "Cada punto gris es una muestra de ubicación de los últimos 60 días; los círculos son tus lugares, en verde si ahí sueles sentirte mejor que tu promedio y en rojo si peor (con todo tu historial de ánimo).") {
            Tarjeta { Mapa(puntos, Repo.lugares, muestreos) }
            io.github.ruddy2003.vestigium.ui.BotonSecundario("ver con calles →", Modifier.padding(top = 8.dp)) { nav.ir("mapa") }
        }
        TiposDeLugar(Modifier.padding(top = 10.dp))
        val pendLug = io.github.ruddy2003.vestigium.data.Lugares.pendientes().size
        TextButton(onClick = { nav.ir("lugar_nuevo") }) {
            Text(if (pendLug > 0) "$pendLug sitio(s) nuevos esperando que digas si son especiales ›" else "lugares especiales: cuándo pregunta y sitios ignorados ›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Seccion("Marcar dónde estoy ahora") {
            Tarjeta {
                CamposLugar(nombre, tipo, radio, { nombre = it }, { tipo = it }, { radio = it })
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
                var cambiarTipo by remember(l.id) { mutableStateOf(false) }
                val vis = Repo.visitas.filter { it.lugar == l.id && it.entrada >= Fechas.sumar(Fechas.hoyCalendario(), -30) }
                val horas = vis.sumOf { v -> v.salida?.let { (Fechas.millis(it) - Fechas.millis(v.entrada)) / 3_600_000.0 } ?: 0.0 }
                val aqui = muestreos.filter { m -> enLugar(m, l) }
                val media = aqui.mapNotNull { it.valencia?.toDouble() }.takeIf { it.isNotEmpty() }?.average()
                val act = aqui.mapNotNull { it.activacion?.toDouble() }.takeIf { it.isNotEmpty() }?.average()
                Tarjeta(Modifier.padding(bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(l.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(Catalogo.nombreTipoLugar(l.tipo).lowercase() + " · cambiar tipo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { cambiarTipo = !cambiarTipo })
                            if (cambiarTipo) FilaFichas(Catalogo.TIPOS_LUGAR, l.tipo) { t -> if (t != null) Repo.guardarLugar(l.copy(tipo = t)); cambiarTipo = false }
                            Ayuda("${porNumero(vis.size, "1 visita", "${vis.size} visitas")} en 30 días · ${"%.1f".format(horas)} h")
                            Ayuda(if (aqui.size < 5) "Pocos datos de ánimo aquí (${aqui.size}); se necesitan 5." else
                                "Valencia aquí ${"%.1f".format(media)}" + (general?.let { g -> " (tu promedio ${"%.1f".format(g)})" } ?: "") + (act?.let { " · energía ${"%.1f".format(it)}" } ?: ""))
                        }
                    }
                    // Corregir un lugar ya guardado (N-05): nombre, radio, ignorarlo para siempre o quitarlo.
                    var renombrar by remember(l.id) { mutableStateOf<String?>(null) }
                    var radioAbierto by remember(l.id) { mutableStateOf(false) }
                    Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        TextButton(onClick = { renombrar = l.nombre }) { Text("renombrar", style = MaterialTheme.typography.labelMedium) }
                        TextButton(onClick = { radioAbierto = !radioAbierto }) { Text("radio ${l.radio_m} m", style = MaterialTheme.typography.labelMedium) }
                        TextButton(onClick = { borrar = true }) { Text("quitar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error) }
                    }
                    if (radioAbierto) {
                        androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (listOf(50, 80, 120, 200, 300, 400) + listOf(l.radio_m)).distinct().sorted().forEach { r ->
                                io.github.ruddy2003.vestigium.ui.Ficha("$r m", l.radio_m == r) {
                                    val n = l.copy(radio_m = r); Repo.guardarLugar(n); Geocercas.registrar(ctx); radioAbierto = false
                                    // Con un radio mayor, se buscan las visitas de los últimos 30 días que antes quedaban fuera (no se tocan las que ya hay).
                                    if (r > l.radio_m) scope.launch(Dispatchers.IO) { runCatching { io.github.ruddy2003.vestigium.data.Lugares.reconstruir(n) } }
                                }
                            }
                        }
                        Ayuda("Agrandar el radio busca las visitas de los últimos 30 días que antes quedaban fuera; achicarlo no quita las que ya hay.")
                    }
                    renombrar?.let { t ->
                        Dialogo(onDismissRequest = { renombrar = null }, title = { Text("Renombrar lugar") },
                            text = { CampoTexto(value = t, onValueChange = { renombrar = it }, singleLine = true, modifier = Modifier.fillMaxWidth()) },
                            confirmButton = { TextButton(enabled = t.isNotBlank(), onClick = { Repo.guardarLugar(l.copy(nombre = t.trim())); renombrar = null }) { Text("Guardar") } },
                            dismissButton = { TextButton(onClick = { renombrar = null }) { Text("cancelar") } })
                    }
                    if (borrar) {
                        val suyas = Repo.visitas.filter { it.lugar == l.id }
                        val otros = Repo.lugares.filter { it.id != l.id }
                        fun quitar(pasarA: String?, ignorar: Boolean) {
                            if (ignorar) io.github.ruddy2003.vestigium.data.Lugares.ignorarLugar(l)
                            Repo.borrarLugar(l.id, pasarA); Geocercas.registrar(ctx); borrar = false
                            io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Lugar quitado") {
                                Repo.guardarLugar(l); suyas.forEach { Repo.guardarVisita(it) }; Geocercas.registrar(ctx)
                                if (ignorar) Repo.cambiarConfig { cc -> cc.copy(lugares_ignorados = cc.lugares_ignorados.dropLast(1)) }
                            }
                        }
                        Dialogo(onDismissRequest = { borrar = false }, title = { Text("Quitar «${l.nombre}»") },
                            text = {
                                Column {
                                    if (suyas.isEmpty()) Ayuda("No tiene visitas guardadas.")
                                    else {
                                        Text("¿Qué hago con " + porNumero(suyas.size, "su visita", "sus ${suyas.size} visitas") + "?", style = MaterialTheme.typography.bodyMedium)
                                        if (otros.isNotEmpty()) {
                                            Etiqueta("Pasarlas a…")
                                            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                otros.forEach { o -> io.github.ruddy2003.vestigium.ui.Ficha(o.nombre, false) { quitar(o.id, false) } }
                                            }
                                        }
                                    }
                                    TextButton(onClick = { quitar(null, true) }, modifier = Modifier.padding(top = 8.dp)) {
                                        Text("quitarlo e ignorar este sitio para siempre", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            },
                            confirmButton = { TextButton(onClick = { quitar(null, false) }) { Text(if (suyas.isEmpty()) "Quitar" else "Quitar con sus visitas", color = MaterialTheme.colorScheme.error) } },
                            dismissButton = { TextButton(onClick = { borrar = false }) { Text("cancelar") } })
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
    val eg = estiloGrafica()
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
        // Pueden ser miles de muestras: van como círculos simples, sin la forma del tema (que arma un Path por punto).
        puntos.forEach { drawCircle(tenue.copy(alpha = 0.18f), 4f, p(it.lat, it.lon)) }
        lugares.forEach { l ->
            val aqui = muestreos.filter { enLugar(it, l) }.mapNotNull { it.valencia?.toDouble() }
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
        lugares.take(6).forEach { Text(it.nombre, fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 11.sp, color = tenue) }
    }
}

/**
 * Lo mismo en las tres formas de marcar un lugar (dónde estoy, un sitio nuevo detectado y un punto del mapa): nombre, tipo y
 * radio de 50 a 400 m en pasos de 25.
 */
@Composable
fun CamposLugar(nombre: String, tipo: String?, radio: Float, onNombre: (String) -> Unit, onTipo: (String?) -> Unit, onRadio: (Float) -> Unit) {
    CampoTexto(value = nombre, onValueChange = onNombre, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = nombre.isBlank(),
        label = { Text("Nombre") }, placeholder = { Text("casa de mis abuelos, la biblioteca, el muro de escalada…") })
    Etiqueta("Tipo"); FilaFichas(Catalogo.TIPOS_LUGAR, tipo) { onTipo(it) }
    Etiqueta("Radio: ${radio.toInt()} m")
    Deslizador(value = radio, onValueChange = { onRadio(Math.round(it / 25f) * 25f) }, valueRange = 50f..400f, steps = 13)
}

/** Un registro de ánimo cuenta para un lugar si lo marcaste ahí o si su ubicación cae dentro del radio: el mismo criterio en la lista y en el mapa (P-26). */
private fun enLugar(m: io.github.ruddy2003.vestigium.data.Momento, l: io.github.ruddy2003.vestigium.data.Lugar): Boolean =
    m.lugar == l.id || (m.lat != null && m.lon != null && Geo.distanciaM(m.lat, m.lon, l.lat, l.lon) <= l.radio_m)

/**
 * Una visita de un día (N-05): corregir la llegada y la salida, «fue en…» otro de tus lugares, o «no estuve aquí» (con
 * confirmación). Todo con Deshacer. Las horas corregidas no las pisa la detección: solo cierra visitas abiertas y agrega las que no se enciman.
 */
@Composable
fun DialogoVisita(v: io.github.ruddy2003.vestigium.data.Visita, cerrar: () -> Unit) {
    var lugar by remember(v.id) { mutableStateOf(v.lugar) }
    var entrada by remember(v.id) { mutableStateOf<java.time.LocalDateTime?>(io.github.ruddy2003.vestigium.ui.horaLocal(Fechas.millis(v.entrada))) }
    var salida by remember(v.id) { mutableStateOf<java.time.LocalDateTime?>(v.salida?.let { io.github.ruddy2003.vestigium.ui.horaLocal(Fechas.millis(it)) }) }
    var confirmar by remember(v.id) { mutableStateOf(false) }
    val actual = Repo.lugares.firstOrNull { it.id == v.lugar }
    val ini = entrada?.aMillis() ?: Fechas.millis(v.entrada)
    val fin = salida?.aMillis()
    val valida = fin == null || fin > ini
    Dialogo(onDismissRequest = cerrar,
        title = { Text("${actual?.nombre ?: "Lugar quitado"} · ${Fechas.horaDe(v.entrada)}" + (v.salida?.let { "–${Fechas.horaDe(it)}" } ?: "")) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Etiqueta("Llegué")
                io.github.ruddy2003.vestigium.ui.SelectorHora(entrada, ayer = true) { entrada = it }
                Etiqueta("Me fui")
                io.github.ruddy2003.vestigium.ui.SelectorHora(salida, etiquetaAhora = "sigo aquí", ayer = true) { salida = it }
                if (!valida) Text("La salida debe ser después de la llegada.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Etiqueta("Fue en…")
                androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Repo.lugares.forEach { l -> io.github.ruddy2003.vestigium.ui.Ficha(l.nombre, l.id == lugar) { lugar = l.id } }
                }
                Ayuda("Si no fue en ninguno de tus lugares, «no estuve aquí» la quita.", Modifier.padding(top = 6.dp))
                TextButton(onClick = {
                    if (!confirmar) { confirmar = true; return@TextButton }
                    Repo.borrarVisita(v.id); cerrar()
                    io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Visita quitada") { Repo.guardarVisita(v) }
                }) { Text(if (confirmar) "¿quitarla de verdad?" else "no estuve aquí", color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(enabled = valida, onClick = {
            val nuevaSalida = fin?.let { Fechas.isoDeMillis(it) }
            // Si cambiaste la salida, la visita dice que la pusiste tú (esquema 19); «sigo aquí» la deja abierta.
            val cierre = when { nuevaSalida == null -> null; fin != v.salida?.let { Fechas.millis(it) } -> io.github.ruddy2003.vestigium.data.Lugares.CIERRE_MANUAL; else -> v.cierre }
            val n = v.copy(lugar = lugar, entrada = Fechas.isoDeMillis(ini), salida = nuevaSalida, cierre = cierre)
            if (n != v) {
                Repo.guardarVisita(n)
                io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Visita corregida") { Repo.guardarVisita(v) }
            }
            cerrar()
        }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = cerrar) { Text("cancelar") } })
}

