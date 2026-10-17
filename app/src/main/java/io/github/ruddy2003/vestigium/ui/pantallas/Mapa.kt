package io.github.ruddy2003.vestigium.ui.pantallas

import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Point
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.ruddy2003.vestigium.data.Db
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Geo
import io.github.ruddy2003.vestigium.data.Lugar
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Meandro
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Overlay

/**
 * Mosaicos estándar de OpenStreetMap (no piden clave). Para que combinen con la app se tiñen en el teléfono:
 * de día, mármol desaturado; de noche, invertidos hacia basalto.
 */
private fun filtroMapa(oscuro: Boolean): ColorMatrixColorFilter {
    val m = ColorMatrix().apply { setSaturation(if (oscuro) 0.2f else 0.45f) }
    if (oscuro) {
        m.postConcat(ColorMatrix(floatArrayOf(-1f, 0f, 0f, 0f, 255f, 0f, -1f, 0f, 0f, 255f, 0f, 0f, -1f, 0f, 255f, 0f, 0f, 0f, 1f, 0f)))
        m.postConcat(ColorMatrix(floatArrayOf(0.80f, 0f, 0f, 0f, 8f, 0f, 0.82f, 0f, 0f, 10f, 0f, 0f, 0.85f, 0f, 12f, 0f, 0f, 0f, 1f, 0f)))
    } else {
        m.postConcat(ColorMatrix(floatArrayOf(0.97f, 0f, 0f, 0f, 6f, 0f, 0.97f, 0f, 0f, 6f, 0f, 0f, 0.95f, 0f, 4f, 0f, 0f, 0f, 1f, 0f)))
    }
    return ColorMatrixColorFilter(m)
}

/** Dibuja círculos en coordenadas del mapa. Mucho más rápido que un marcador por punto. */
private class Puntos : Overlay() {
    data class P(val lat: Double, val lon: Double, val color: Int, val radio: Float, val borde: Boolean = false)
    var datos: List<P> = emptyList()
    var ruta: List<GeoPoint> = emptyList()
    var colorRuta: Int = 0xFFD2AE5A.toInt()
    private val relleno = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linea = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 3f }
    private val trazo = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 6f; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }
    private val pt = Point(); private val pt2 = Point()

    override fun draw(c: Canvas, mapView: MapView, shadow: Boolean) {
        if (shadow) return
        val pr = mapView.projection
        if (ruta.size > 1) {
            trazo.color = colorRuta
            for (i in 1 until ruta.size) {
                pr.toPixels(ruta[i - 1], pt); pr.toPixels(ruta[i], pt2)
                c.drawLine(pt.x.toFloat(), pt.y.toFloat(), pt2.x.toFloat(), pt2.y.toFloat(), trazo)
            }
        }
        val escala = (mapView.zoomLevelDouble / 14.0).toFloat().coerceIn(0.5f, 1.6f)
        for (p in datos) {
            pr.toPixels(GeoPoint(p.lat, p.lon), pt)
            relleno.color = p.color
            c.drawCircle(pt.x.toFloat(), pt.y.toFloat(), p.radio * escala, relleno)
            if (p.borde) { linea.color = p.color or (0xFF shl 24); c.drawCircle(pt.x.toFloat(), pt.y.toFloat(), p.radio * escala, linea) }
        }
    }
}

/** Un cuadro de ~100 m con las horas que pasaste ahí (centro = promedio ponderado por tiempo). */
private class Celda(val lat: Double, val lon: Double, val horas: Double)

/**
 * Convierte las ubicaciones en horas por cuadro. Cada muestra vale el tiempo hasta la siguiente:
 * si la siguiente está cerca, seguías ahí (hasta 8 h, p. ej. una noche en casa); si está lejos, ya te movías (máximo 30 min).
 * Así cuenta el tiempo y no el número de puntos, que depende de qué tan seguido se tomaron.
 */
private fun celdasDeTiempo(desde: Long, hasta: Long = Long.MAX_VALUE): Pair<List<Celda>, Int> {
    val acc = HashMap<Long, DoubleArray>()
    var n = 0
    var pMs = -1L; var pLat = 0.0; var pLon = 0.0
    fun sumar(lat: Double, lon: Double, h: Double) {
        if (h <= 0) return
        val k = (kotlin.math.floor(lat * 1000).toLong() + 100_000L) * 1_000_000L + (kotlin.math.floor(lon * 1000).toLong() + 200_000L)
        val a = acc.getOrPut(k) { DoubleArray(3) }
        a[0] += lat * h; a[1] += lon * h; a[2] += h
    }
    Repo.db().recorrerUbicaciones(desde, hasta) { ms, lat, lon, prec ->
        if (prec > 500) return@recorrerUbicaciones
        n++
        if (pMs >= 0) {
            val dt = (ms - pMs) / 3_600_000.0
            val cerca = io.github.ruddy2003.vestigium.data.Geo.distanciaM(pLat, pLon, lat, lon) < 400
            sumar(pLat, pLon, dt.coerceIn(0.0, if (cerca) 8.0 else 0.5))
        }
        pMs = ms; pLat = lat; pLon = lon
    }
    if (pMs >= 0) sumar(pLat, pLon, 0.25)
    return acc.values.map { Celda(it[0] / it[2], it[1] / it[2], it[2]) } to n
}

/** Escala de calor: verde azulado (poco) → oro → vino (mucho). */
private fun colorCalor(t: Float, oscuro: Boolean): Int {
    val x = t.coerceIn(0f, 1f)
    val (a, b, c) = if (oscuro) Triple(Color(0xFF3FA393), Color(0xFFD2AE5A), Color(0xFFE0604F)) else Triple(Color(0xFF2D7A6F), Color(0xFFB8892B), Color(0xFF9E3445))
    val col = if (x < 0.5f) lerp(a, b, x * 2) else lerp(b, c, (x - 0.5f) * 2)
    return col.copy(alpha = 0.38f + 0.47f * x).toArgb()
}

/**
 * Mapa de calor con escala logarítmica: el color sube lo mismo de 1 a 10 horas que de 10 a 100,
 * así los lugares donde pasas poco tiempo no desaparecen junto a la casa.
 * Al alejar el mapa los cuadros se juntan (de 100 m a 200, 400, 800 m…) para que no se encimen.
 */
private class Calor(oscuroInicial: Boolean) : Overlay() {
    private var paleta = IntArray(64) { colorCalor(it / 63f, oscuroInicial) }
    var oscuro: Boolean = oscuroInicial
        set(v) { if (v != field) { field = v; paleta = IntArray(64) { colorCalor(it / 63f, v) } } }
    var base: List<Celda> = emptyList()
        set(v) { field = v; niveles.clear(); nivelActual = -1 }
    /** Avisa las horas del cuadro más visitado al cambiar de nivel, para la leyenda. */
    var alCambiar: ((Double) -> Unit)? = null
    private val niveles = HashMap<Int, Pair<List<Celda>, Double>>()
    private var nivelActual = -1
    private val pincel = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pt = Point()

    companion object { const val H0 = 0.25; const val BASE = 0.001 }

    fun intensidad(h: Double, max: Double) = (kotlin.math.ln(1 + h / H0) / kotlin.math.ln(1 + max / H0)).toFloat()

    private fun nivel(k: Int): Pair<List<Celda>, Double> = niveles.getOrPut(k) {
        val l = if (k == 0) base else {
            val tam = BASE * (1 shl k)
            val acc = HashMap<Long, DoubleArray>()
            for (c in base) {
                val key = (kotlin.math.floor(c.lat / tam).toLong() + 1_000_000L) * 10_000_000L + (kotlin.math.floor(c.lon / tam).toLong() + 2_000_000L)
                val a = acc.getOrPut(key) { DoubleArray(3) }
                a[0] += c.lat * c.horas; a[1] += c.lon * c.horas; a[2] += c.horas
            }
            acc.values.map { Celda(it[0] / it[2], it[1] / it[2], it[2]) }
        }
        l.sortedBy { it.horas } to (l.maxOfOrNull { it.horas } ?: 1.0)
    }

    /** Grados de longitud por píxel en la vista actual. */
    private fun gradosPorPx(mv: MapView): Double = mv.boundingBox.let { b -> ((b.lonEast - b.lonWest).let { if (it <= 0) it + 360 else it }) / mv.width.coerceAtLeast(1) }

    private fun nivelPara(mv: MapView): Int {
        val objetivo = gradosPorPx(mv) * 16 * mv.resources.displayMetrics.density
        return if (objetivo <= BASE) 0 else kotlin.math.ceil(kotlin.math.ln(objetivo / BASE) / kotlin.math.ln(2.0)).toInt().coerceIn(0, 12)
    }

    fun cercana(g: GeoPoint, mv: MapView): Celda? {
        if (base.isEmpty()) return null
        val (l, _) = nivel(nivelPara(mv))
        val tam = BASE * (1 shl nivelPara(mv))
        return l.minByOrNull { Geo.distanciaM(it.lat, it.lon, g.latitude, g.longitude) }
            ?.takeIf { Geo.distanciaM(it.lat, it.lon, g.latitude, g.longitude) < tam * 111_000 * 1.2 }
    }

    override fun draw(c: Canvas, mv: MapView, shadow: Boolean) {
        if (shadow || base.isEmpty()) return
        val k = nivelPara(mv)
        val (l, max) = nivel(k)
        if (k != nivelActual) { nivelActual = k; mv.post { alCambiar?.invoke(max) } }
        val pr = mv.projection
        val celdaPx = (BASE * (1 shl k) / gradosPorPx(mv)).toFloat()
        val minimo = 4f * mv.resources.displayMetrics.density; val maximo = 40f * mv.resources.displayMetrics.density
        val bb = mv.boundingBox
        val margen = BASE * (1 shl k) * 2
        for (cel in l) {
            if (cel.lat < bb.latSouth - margen || cel.lat > bb.latNorth + margen) continue
            if (bb.lonWest < bb.lonEast && (cel.lon < bb.lonWest - margen || cel.lon > bb.lonEast + margen)) continue
            val t = intensidad(cel.horas, max)
            pr.toPixels(GeoPoint(cel.lat, cel.lon), pt)
            pincel.color = paleta[(t * 63).toInt().coerceIn(0, 63)]
            c.drawCircle(pt.x.toFloat(), pt.y.toFloat(), (celdaPx * (0.55f + 0.35f * t)).coerceIn(minimo, maximo), pincel)
        }
    }
}

/** Horas legibles: minutos, horas o días. */
private fun duracion(h: Double): String = when {
    h < 1 -> "${(h * 60).toInt().coerceAtLeast(1)} min"
    h < 72 -> "${"%.0f".format(h)} h"
    else -> "${"%,.0f".format(h / 24)} días"
}

private data class CapaMapa(val id: String, val nombre: String)
private val CAPAS = listOf(CapaMapa("animo", "ánimo"), CapaMapa("calor", "dónde paso el tiempo"), CapaMapa("ruta", "ruta del día"), CapaMapa("lugares", "lugares"))
private val PERIODOS = listOf(30 to "30 días", 90 to "90 días", 365 to "un año", 100_000 to "todo")

@Composable
fun PantallaMapa(nav: Nav) {
    val ctx = LocalContext.current
    val oscuro = esOscuro()
    var capa by remember { mutableStateOf("animo") }
    var periodo by remember { mutableStateOf(90) }
    var dia by remember { mutableStateOf(Fechas.diaLogico()) }
    var nuevoLugar by remember { mutableStateOf<GeoPoint?>(null) }
    val desde = Fechas.sumar(Fechas.diaLogico(), -periodo.toLong())

    // Ubicaciones del periodo o del día, leídas fuera del hilo principal.
    val puntos by produceState(emptyList<Db.Punto>(), capa, periodo, dia) {
        value = withContext(Dispatchers.IO) {
            when (capa) {
                "ruta" -> Repo.db().ubicaciones(Fechas.inicioDia(dia) + 5 * 3_600_000L, Fechas.finDia(dia) + 5 * 3_600_000L)
                else -> emptyList()
            }
        }
    }
    // Horas por cuadro para la capa de calor.
    val calor by produceState<Pair<List<Celda>, Int>?>(null, capa, periodo) {
        value = if (capa != "calor") null else withContext(Dispatchers.IO) { celdasDeTiempo(Fechas.inicioDia(desde)) }
    }
    var maxVisible by remember { mutableStateOf<Double?>(null) }
    var tocada by remember { mutableStateOf<Celda?>(null) }
    LaunchedEffect(calor) { tocada = null }
    val mapa = remember { mutableStateOf<MapView?>(null) }
    val capaPuntos = remember { Puntos() }
    val capaCalor = remember { Calor(oscuro) }
    capaCalor.oscuro = oscuro
    capaCalor.alCambiar = { maxVisible = it }
    val enfoque = remember { arrayOfNulls<String>(1) }
    val ciclo = LocalLifecycleOwner.current
    DisposableEffect(ciclo) {
        val obs = LifecycleEventObserver { _, e ->
            when (e) { Lifecycle.Event.ON_RESUME -> mapa.value?.onResume(); Lifecycle.Event.ON_PAUSE -> mapa.value?.onPause(); else -> {} }
        }
        ciclo.lifecycle.addObserver(obs)
        onDispose { ciclo.lifecycle.removeObserver(obs); mapa.value?.onDetach() }
    }

    val momentos = Repo.momentos.filter { it.lat != null && it.lon != null && it.fecha >= desde }
    val datos: List<Puntos.P> = when (capa) {
        "animo" -> momentos.mapNotNull { m ->
            val v = m.valencia?.toDouble() ?: Repo.dias[m.fecha]?.valencia?.toDouble() ?: return@mapNotNull null
            Puntos.P(m.lat!!, m.lon!!, Tonos.divergente(((v - 1) / 6).toFloat(), oscuro).copy(alpha = 0.75f).toArgb(), 14f, borde = true)
        }
        "calor" -> emptyList()
        "lugares" -> Repo.lugares.map { l -> Puntos.P(l.lat, l.lon, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f).toArgb(), 18f, borde = true) }
        else -> momentos.filter { it.fecha == dia }.map { m -> Puntos.P(m.lat!!, m.lon!!, MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f).toArgb(), 12f, borde = true) }
    }
    val ruta = if (capa == "ruta") puntos.filter { (it.precision ?: 0.0) < 150 }.map { GeoPoint(it.lat, it.lon) } else emptyList()
    val colorRuta = MaterialTheme.colorScheme.primary.toArgb()

    Column(Modifier.fillMaxSize()) {
      // Controles arriba, siempre por encima del mapa y con fondo propio para que se puedan tocar.
      Column(Modifier.fillMaxWidth().zIndex(2f).background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp, top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = nav.atras) { Text("← cd ..", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
            Column(Modifier.weight(1f)) {
                Text("MAPPA", style = MaterialTheme.typography.headlineSmall)
                Text("// mapa · mantén presionado para marcar un lugar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Meandro(Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CAPAS.forEach { c -> Ficha(c.nombre, capa == c.id) { capa = c.id } }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (capa == "ruta") {
                Ficha("‹", false) { dia = Fechas.sumar(dia, -1) }
                Ficha(Fechas.corto(dia), true) { dia = Fechas.diaLogico() }
                Ficha("›", false) { dia = Fechas.sumar(dia, 1) }
            } else if (capa != "lugares") PERIODOS.forEach { (n, t) -> Ficha(t, periodo == n) { periodo = n } }
        }
      }
        // El mapa queda recortado a su recuadro: sin esto osmdroid dibuja encima de los menús al arrastrarlo.
        Box(Modifier.weight(1f).fillMaxWidth().clipToBounds().zIndex(0f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize().clipToBounds(),
                factory = { c ->
                    MapView(c).apply {
                        addOnAttachStateChangeListener(object : android.view.View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: android.view.View) {
                                (v.parent as? android.view.ViewGroup)?.apply { clipChildren = true; clipToPadding = true }
                            }
                            override fun onViewDetachedFromWindow(v: android.view.View) {}
                        })
                        setTileSource(TileSourceFactory.MAPNIK)
                        overlayManager.tilesOverlay.setColorFilter(filtroMapa(oscuro))
                        setMultiTouchControls(true)
                        isTilesScaledToDpi = true
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        minZoomLevel = 3.0
                        overlays.add(capaCalor)
                        overlays.add(capaPuntos)
                        overlays.add(CopyrightOverlay(c))
                        overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                if (p == null || capaCalor.base.isEmpty()) return false
                                tocada = capaCalor.cercana(p, this@apply); return tocada != null
                            }
                            override fun longPressHelper(p: GeoPoint?): Boolean { nuevoLugar = p; return p != null }
                        }))
                        val centro = Repo.momentos.lastOrNull { it.lat != null }?.let { GeoPoint(it.lat!!, it.lon!!) }
                            ?: Repo.lugares.firstOrNull()?.let { GeoPoint(it.lat, it.lon) } ?: GeoPoint(19.4326, -99.1332)
                        controller.setZoom(13.5); controller.setCenter(centro)
                        mapa.value = this
                    }
                },
                update = { m ->
                    capaPuntos.datos = datos; capaPuntos.ruta = ruta; capaPuntos.colorRuta = colorRuta
                    val celdas = calor?.first ?: emptyList()
                    if (capaCalor.base !== celdas) capaCalor.base = celdas
                    // Al cambiar al recorrido de un día, centra el mapa en él.
                    val clave = "$capa|$dia|${ruta.size}"
                    if (ruta.size > 1 && enfoque[0] != clave) {
                        enfoque[0] = clave
                        runCatching { m.zoomToBoundingBox(org.osmdroid.util.BoundingBox.fromGeoPointsSafe(ruta).increaseByScale(1.3f), false) }
                    }
                    m.invalidate()
                },
            )
            if (capa == "calor" && calor?.first?.isEmpty() == true) Ayuda("Sin ubicaciones en este periodo.", Modifier.align(Alignment.TopCenter).padding(10.dp).background(MaterialTheme.colorScheme.surface).padding(8.dp))
            if (capa == "ruta" && puntos.isEmpty()) Ayuda("Sin ubicaciones ese día.", Modifier.align(Alignment.TopCenter).padding(10.dp).background(MaterialTheme.colorScheme.surface).padding(8.dp))
        }
        Box(Modifier.fillMaxWidth().zIndex(2f).background(MaterialTheme.colorScheme.background)) {
            Leyenda(capa, oscuro, datos.size, puntos.size, calor, maxVisible, tocada)
        }
    }

    nuevoLugar?.let { p ->
        var nombre by remember(p) { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { nuevoLugar = null },
            title = { Text("Marcar este lugar", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Ayuda("%.5f, %.5f · la app registrará cuándo llegas y te vas.".format(p.latitude, p.longitude))
                    OutlinedTextField(nombre, { nombre = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("casa, gimnasio, oficina…") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (nombre.isNotBlank()) {
                        Repo.guardarLugar(Lugar(Repo.nuevoId(), nombre.trim(), p.latitude, p.longitude))
                        runCatching { io.github.ruddy2003.vestigium.sensores.Geocercas.registrar(ctx) }
                    }
                    nuevoLugar = null
                }) { Text("guardar", style = MaterialTheme.typography.labelLarge) }
            },
            dismissButton = { TextButton(onClick = { nuevoLugar = null }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) } },
        )
    }
}

@Composable
private fun Leyenda(capa: String, oscuro: Boolean, n: Int, ubic: Int, calor: Pair<List<Celda>, Int>?, maxVisible: Double?, tocada: Celda?) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        when (capa) {
            "animo" -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("difícil ", style = MaterialTheme.typography.labelSmall)
                Row(Modifier.weight(1f).height(8.dp)) { for (k in 0..10) Box(Modifier.weight(1f).height(8.dp).background(Tonos.divergente(k / 10f, oscuro))) }
                Text(" bueno", style = MaterialTheme.typography.labelSmall)
            }
            "calor" -> {
                val max = maxVisible ?: calor?.first?.maxOfOrNull { it.horas } ?: 1.0
                // Valor de las horas en un punto t de la escala logarítmica.
                fun horasEn(t: Double) = Calor.H0 * (Math.pow(1 + max / Calor.H0, t) - 1)
                Row(Modifier.fillMaxWidth().height(10.dp)) { for (k in 0..20) Box(Modifier.weight(1f).height(10.dp).background(Color(colorCalor(k / 20f, oscuro)))) }
                Row(Modifier.fillMaxWidth()) {
                    Text(duracion(horasEn(0.08)), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                    Text(duracion(horasEn(0.5)), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(duracion(max), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
            }
            else -> {}
        }
        Text(when (capa) {
            "animo" -> "$n momentos con lugar, coloreados por cómo te sentías"
            "calor" -> tocada?.let { "Aquí: ≈ ${duracion(it.horas)} en el periodo. Toca otro punto para comparar." }
                ?: calor?.let { (c, np) -> "≈ ${duracion(c.sumOf { it.horas })} estimadas a partir de $np ubicaciones · escala logarítmica: el color sube igual de 1 a 10 h que de 10 a 100 h · toca un punto para ver sus horas" }
                ?: "Calculando…"
            "ruta" -> "$ubic puntos del día"
            else -> "${Repo.lugares.size} lugares marcados"
        }, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
    }
}

