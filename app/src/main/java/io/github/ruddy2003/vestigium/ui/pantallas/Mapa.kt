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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.ruddy2003.vestigium.data.Db
import io.github.ruddy2003.vestigium.data.Fechas
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
import kotlin.math.max

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
                "calor" -> Repo.db().ubicaciones(Fechas.inicioDia(desde))
                "ruta" -> Repo.db().ubicaciones(Fechas.inicioDia(dia) + 5 * 3_600_000L, Fechas.finDia(dia) + 5 * 3_600_000L)
                else -> emptyList()
            }
        }
    }
    val mapa = remember { mutableStateOf<MapView?>(null) }
    val capaPuntos = remember { Puntos() }
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
        "calor" -> {
            // Rejilla de ~100 m: más tiempo en un sitio, círculo más grande e intenso.
            val celdas = puntos.groupBy { (it.lat * 1000).toInt() to (it.lon * 1000).toInt() }
            val maxN = max(1, celdas.values.maxOfOrNull { it.size } ?: 1)
            celdas.map { (_, l) ->
                val t = l.size.toFloat() / maxN
                Puntos.P(l.map { it.lat }.average(), l.map { it.lon }.average(), Tonos.secuencial(0.35f + 0.65f * t, oscuro).copy(alpha = 0.25f + 0.5f * t).toArgb(), 10f + 22f * kotlin.math.sqrt(t))
            }
        }
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
                        overlays.add(capaPuntos)
                        overlays.add(CopyrightOverlay(c))
                        overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false
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
                    // Al cambiar al recorrido de un día, centra el mapa en él.
                    val clave = "$capa|$dia|${ruta.size}"
                    if (ruta.size > 1 && enfoque[0] != clave) {
                        enfoque[0] = clave
                        runCatching { m.zoomToBoundingBox(org.osmdroid.util.BoundingBox.fromGeoPointsSafe(ruta).increaseByScale(1.3f), false) }
                    }
                    m.invalidate()
                },
            )
            if (capa == "ruta" && puntos.isEmpty()) Ayuda("Sin ubicaciones ese día.", Modifier.align(Alignment.TopCenter).padding(10.dp).background(MaterialTheme.colorScheme.surface).padding(8.dp))
        }
        Box(Modifier.fillMaxWidth().zIndex(2f).background(MaterialTheme.colorScheme.background)) { Leyenda(capa, oscuro, datos.size, puntos.size) }
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
private fun Leyenda(capa: String, oscuro: Boolean, n: Int, ubic: Int) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        when (capa) {
            "animo" -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("difícil ", style = MaterialTheme.typography.labelSmall)
                Row(Modifier.weight(1f).height(8.dp)) { for (k in 0..10) Box(Modifier.weight(1f).height(8.dp).background(Tonos.divergente(k / 10f, oscuro))) }
                Text(" bueno", style = MaterialTheme.typography.labelSmall)
            }
            "calor" -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("poco ", style = MaterialTheme.typography.labelSmall)
                Row(Modifier.weight(1f).height(8.dp)) { for (k in 0..10) Box(Modifier.weight(1f).height(8.dp).background(Tonos.secuencial(k / 10f, oscuro))) }
                Text(" mucho", style = MaterialTheme.typography.labelSmall)
            }
            else -> {}
        }
        Text(when (capa) {
            "animo" -> "$n momentos con lugar, coloreados por cómo te sentías"
            "calor" -> "$ubic ubicaciones agrupadas en celdas de ~100 m"
            "ruta" -> "$ubic puntos del día"
            else -> "${Repo.lugares.size} lugares marcados"
        }, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
    }
}

