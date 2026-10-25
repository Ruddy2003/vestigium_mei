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
import io.github.ruddy2003.vestigium.ui.TemaId
import androidx.compose.ui.geometry.Offset
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
    val tema = io.github.ruddy2003.vestigium.ui.Estilo.tema
    val invertir = ColorMatrix(floatArrayOf(-1f, 0f, 0f, 0f, 255f, 0f, -1f, 0f, 0f, 255f, 0f, 0f, -1f, 0f, 255f, 0f, 0f, 0f, 1f, 0f))
    fun tinte(r: Float, g: Float, b: Float, or: Float, og: Float, ob: Float) = ColorMatrix(floatArrayOf(r, 0f, 0f, 0f, or, 0f, g, 0f, 0f, og, 0f, 0f, b, 0f, ob, 0f, 0f, 0f, 1f, 0f))
    val m = ColorMatrix()
    when {
        // Cyberpunk: plano nocturno en azul eléctrico.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.CIBER -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.30f, 0.62f, 0.80f, 0f, 4f, 10f)) }
        // Helénico: pergamino de día, noche púrpura de noche.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.HELENO && oscuro -> { m.setSaturation(0.15f); m.postConcat(invertir); m.postConcat(tinte(0.78f, 0.70f, 0.92f, 6f, 4f, 14f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.HELENO -> { m.setSaturation(0.25f); m.postConcat(tinte(1.0f, 0.95f, 0.84f, 4f, 2f, 0f)) }
        // Scriptorium: mapa en sepia sobre pergamino; de noche, tinta parda.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.MEDIEVAL && oscuro -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.86f, 0.72f, 0.55f, 6f, 3f, 0f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.MEDIEVAL -> { m.setSaturation(0f); m.postConcat(tinte(1.0f, 0.92f, 0.76f, 0f, 0f, 0f)) }
        // Distopía: gris de expediente, sin color.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.DISTOPIA && oscuro -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.82f, 0.83f, 0.84f, 4f, 4f, 4f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.DISTOPIA -> { m.setSaturation(0f); m.postConcat(tinte(0.9f, 0.89f, 0.86f, 0f, 0f, 0f)) }
        // Refugio: fósforo verde de noche; papel de folleto de día.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.REFUGIO && oscuro -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.18f, 0.9f, 0.38f, 0f, 6f, 0f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.REFUGIO -> { m.setSaturation(0.3f); m.postConcat(tinte(0.97f, 0.96f, 0.88f, 4f, 4f, 0f)) }
        // Orden: acero.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.ORDEN && oscuro -> { m.setSaturation(0.05f); m.postConcat(invertir); m.postConcat(tinte(0.76f, 0.8f, 0.86f, 6f, 8f, 10f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.ORDEN -> { m.setSaturation(0.1f); m.postConcat(tinte(0.9f, 0.92f, 0.95f, 0f, 0f, 2f)) }
        // Laboratorio: blanco clínico con un toque cian; de noche, azul de monitor.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.LABORATORIO && oscuro -> { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.58f, 0.8f, 0.95f, 2f, 6f, 12f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.LABORATORIO -> { m.setSaturation(0.2f); m.postConcat(tinte(0.94f, 0.98f, 1.0f, 2f, 4f, 6f)) }
        // Colonos: mapa viejo de gaceta; de noche, azul marino.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.COLONOS && oscuro -> { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.62f, 0.7f, 0.86f, 4f, 6f, 14f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.COLONOS -> { m.setSaturation(0.35f); m.postConcat(tinte(1.0f, 0.95f, 0.86f, 2f, 0f, 0f)) }
        // Napoleónico: carta de estado mayor sepia; de noche, azul imperial.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.NAPOLEON && oscuro -> { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.6f, 0.68f, 0.92f, 2f, 4f, 16f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.NAPOLEON -> { m.setSaturation(0.25f); m.postConcat(tinte(1.0f, 0.94f, 0.82f, 2f, 0f, 0f)) }
        // Botánico: lámina de atlas en crema y verde; de noche, el bosque.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.BOTANICO && oscuro -> { m.setSaturation(0.2f); m.postConcat(invertir); m.postConcat(tinte(0.66f, 0.86f, 0.66f, 2f, 8f, 2f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.BOTANICO -> { m.setSaturation(0.45f); m.postConcat(tinte(0.97f, 0.97f, 0.86f, 2f, 4f, 0f)) }
        // Gótico: piedra caliza; de noche, el azul de la bóveda.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.GOTICO && oscuro -> { m.setSaturation(0.05f); m.postConcat(invertir); m.postConcat(tinte(0.56f, 0.6f, 0.95f, 2f, 4f, 22f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.GOTICO -> { m.setSaturation(0.2f); m.postConcat(tinte(0.98f, 0.95f, 0.88f, 2f, 2f, 0f)) }
        // Espacio: carta de navegación; de noche, como vista desde la órbita.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.ESPACIO && oscuro -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.42f, 0.55f, 0.85f, 0f, 2f, 8f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.ESPACIO -> { m.setSaturation(0.15f); m.postConcat(tinte(0.93f, 0.96f, 1.0f, 0f, 2f, 6f)) }
        // Al-Ándalus: arena y yeso; de noche, el jardín en verde azulado.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.ANDALUS && oscuro -> { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.6f, 0.82f, 0.78f, 2f, 8f, 6f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.ANDALUS -> { m.setSaturation(0.35f); m.postConcat(tinte(1.0f, 0.94f, 0.82f, 4f, 2f, 0f)) }
        // Mongol: la estepa bajo el cielo azul; de noche, el azul profundo.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.MONGOL && oscuro -> { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.55f, 0.68f, 0.95f, 2f, 6f, 18f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.MONGOL -> { m.setSaturation(0.4f); m.postConcat(tinte(0.97f, 0.97f, 0.92f, 2f, 2f, 4f)) }
        // Renacimiento: mapa dibujado a sanguina; de noche, nogal.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.RENACIMIENTO && oscuro -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.85f, 0.68f, 0.5f, 6f, 2f, 0f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.RENACIMIENTO -> { m.setSaturation(0f); m.postConcat(tinte(1.0f, 0.88f, 0.7f, 0f, 0f, 0f)) }
        // Navidad: nieve; de noche, bosque.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.NAVIDAD && oscuro -> { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.62f, 0.86f, 0.7f, 2f, 8f, 4f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.NAVIDAD -> { m.setSaturation(0.35f); m.postConcat(tinte(0.98f, 0.97f, 0.94f, 4f, 4f, 2f)) }
        // Ruta de la Seda: arena; de noche, desierto bajo las estrellas.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.SEDA && oscuro -> { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.66f, 0.7f, 0.92f, 4f, 6f, 16f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.SEDA -> { m.setSaturation(0.3f); m.postConcat(tinte(1.0f, 0.93f, 0.78f, 4f, 2f, 0f)) }
        // Soviético: papel de cartel en blanco y negro con un toque rojo.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.URSS && oscuro -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.95f, 0.78f, 0.74f, 6f, 0f, 0f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.URSS -> { m.setSaturation(0f); m.postConcat(tinte(1.0f, 0.93f, 0.84f, 4f, 0f, 0f)) }
        // Revolución: papel de cartel con tinta de imprenta; de noche, la plaza con antorchas.
        tema == io.github.ruddy2003.vestigium.ui.TemaId.REVOLUCION && oscuro -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.72f, 0.74f, 0.82f, 6f, 4f, 8f)) }
        tema == io.github.ruddy2003.vestigium.ui.TemaId.REVOLUCION -> { m.setSaturation(0.12f); m.postConcat(tinte(1.0f, 0.96f, 0.88f, 4f, 2f, 0f)) }
        // Romano: mármol desaturado de día, basalto de noche.
        oscuro -> { m.setSaturation(0.2f); m.postConcat(invertir); m.postConcat(tinte(0.80f, 0.82f, 0.85f, 8f, 10f, 12f)) }
        else -> { m.setSaturation(0.45f); m.postConcat(tinte(0.97f, 0.97f, 0.95f, 6f, 6f, 4f)) }
    }
    return ColorMatrixColorFilter(m)
}

/**
 * La forma de los marcadores en cada tema: sello de cera, hexágono de neón, hoja, vitral, estrella de ocho puntas,
 * destello, corona de yurta, compás… Se dibujan directo en el lienzo del mapa (rápido aunque sean miles).
 */
private object Formas {
    private val p = android.graphics.Path()
    private fun poligono(x: Float, y: Float, r: Float, n: Int, giro: Double = -90.0, interior: Float? = null) {
        p.reset()
        val pasos = if (interior != null) n * 2 else n
        for (i in 0 until pasos) {
            val rr = if (interior != null && i % 2 == 1) r * interior else r
            val a = Math.toRadians(giro + 360.0 * i / pasos)
            val px = x + rr * kotlin.math.cos(a).toFloat(); val py = y + rr * kotlin.math.sin(a).toFloat()
            if (i == 0) p.moveTo(px, py) else p.lineTo(px, py)
        }
        p.close()
    }

    fun dibujar(c: Canvas, tema: TemaId, x: Float, y: Float, r: Float, relleno: Paint, borde: Paint?) {
        when (tema) {
            TemaId.ROMANO -> { p.reset(); p.addRoundRect(x - r, y - r * 0.8f, x + r, y + r * 0.8f, r * 0.2f, r * 0.2f, android.graphics.Path.Direction.CW) }
            TemaId.CIBER, TemaId.LABORATORIO -> poligono(x, y, r, 6, -90.0)
            TemaId.HELENO -> poligono(x, y, r * 1.15f, 8, -90.0, 0.55f)
            TemaId.MEDIEVAL, TemaId.NAPOLEON -> {   // sello de cera de borde ondulado
                p.reset()
                for (i in 0..36) { val a = 2 * Math.PI * i / 36; val rr = r * (1f + 0.09f * kotlin.math.sin(a * 7).toFloat())
                    val px = x + rr * kotlin.math.cos(a).toFloat(); val py = y + rr * kotlin.math.sin(a).toFloat(); if (i == 0) p.moveTo(px, py) else p.lineTo(px, py) }
                p.close()
            }
            TemaId.DISTOPIA -> { p.reset(); p.moveTo(x - r * 1.2f, y); p.quadTo(x, y - r * 1.2f, x + r * 1.2f, y); p.quadTo(x, y + r * 1.2f, x - r * 1.2f, y); p.close() }
            TemaId.REFUGIO -> poligono(x, y, r * 1.1f, 4, -90.0)
            TemaId.ORDEN -> { p.reset(); p.moveTo(x - r, y - r); p.lineTo(x + r, y - r); p.lineTo(x + r, y - r * 0.1f); p.quadTo(x + r, y + r * 0.7f, x, y + r * 1.2f)
                p.quadTo(x - r, y + r * 0.7f, x - r, y - r * 0.1f); p.close() }
            TemaId.COLONOS -> { p.reset(); p.moveTo(x, y - r * 1.1f); p.lineTo(x + r, y - r * 0.2f); p.lineTo(x + r, y + r); p.lineTo(x - r, y + r); p.lineTo(x - r, y - r * 0.2f); p.close() }
            TemaId.BOTANICO -> { p.reset(); p.moveTo(x, y + r * 1.2f); p.quadTo(x + r * 1.3f, y, x, y - r * 1.2f); p.quadTo(x - r * 1.3f, y, x, y + r * 1.2f); p.close() }
            TemaId.NAVIDAD, TemaId.URSS -> poligono(x, y, r * 1.25f, 5, -90.0, 0.42f)
            TemaId.SEDA -> { p.reset(); p.addCircle(x, y, r, android.graphics.Path.Direction.CW); p.addRect(x - r * 0.32f, y - r * 0.32f, x + r * 0.32f, y + r * 0.32f, android.graphics.Path.Direction.CCW) }
            TemaId.GOTICO -> { p.reset(); p.moveTo(x - r * 0.8f, y + r); p.lineTo(x - r * 0.8f, y - r * 0.1f); p.quadTo(x - r * 0.8f, y - r * 0.9f, x, y - r * 1.3f)
                p.quadTo(x + r * 0.8f, y - r * 0.9f, x + r * 0.8f, y - r * 0.1f); p.lineTo(x + r * 0.8f, y + r); p.close() }
            TemaId.ESPACIO -> { p.reset(); val k = r * 1.3f; p.moveTo(x, y - k); p.quadTo(x, y, x + k, y); p.quadTo(x, y, x, y + k); p.quadTo(x, y, x - k, y); p.quadTo(x, y, x, y - k); p.close() }
            TemaId.ANDALUS -> poligono(x, y, r * 1.2f, 8, -90.0, 0.62f)
            TemaId.MONGOL -> { p.reset(); p.addCircle(x, y, r, android.graphics.Path.Direction.CW) }
            TemaId.RENACIMIENTO -> { p.reset(); p.addCircle(x, y, r, android.graphics.Path.Direction.CW) }
            TemaId.REVOLUCION -> poligono(x, y, r * 1.12f, 14, -90.0, 0.84f)   // escarapela plisada
        }
        c.drawPath(p, relleno)
        borde?.let { b -> c.drawPath(p, b) }
        // Detalles que van encima de la forma.
        val d = borde ?: return
        when (tema) {
            TemaId.MONGOL -> { c.drawLine(x - r * 0.7f, y, x + r * 0.7f, y, d); c.drawLine(x, y - r * 0.7f, x, y + r * 0.7f, d) }   // las varas del toono
            TemaId.RENACIMIENTO -> { c.drawLine(x - r * 1.4f, y, x + r * 1.4f, y, d); c.drawLine(x, y - r * 1.4f, x, y + r * 1.4f, d) }  // retícula de dibujo
            TemaId.GOTICO -> c.drawLine(x, y - r * 0.5f, x, y + r, d)
            TemaId.BOTANICO -> c.drawLine(x, y + r * 1.2f, x, y - r * 0.8f, d)
            TemaId.REVOLUCION -> c.drawCircle(x, y, r * 0.42f, d)
            else -> {}
        }
    }
}

/** Dibuja marcadores en coordenadas del mapa, con la forma del tema. Mucho más rápido que un marcador por punto. */
private class Puntos : Overlay() {
    data class P(val lat: Double, val lon: Double, val color: Int, val radio: Float, val borde: Boolean = false, val nombre: String? = null)
    var tema: TemaId = TemaId.ROMANO
    var colorBorde: Int = 0xFF000000.toInt()
    var colorTexto: Int = 0xFF000000.toInt()
    var fondoTexto: Int = 0xFFFFFFFF.toInt()
    var datos: List<P> = emptyList()
    var ruta: List<GeoPoint> = emptyList()
    var colorRuta: Int = 0xFFD2AE5A.toInt()
    private val relleno = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linea = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 3f; strokeJoin = Paint.Join.ROUND }
    private val letra = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER; isFakeBoldText = true }
    private val fondoLetra = Paint(Paint.ANTI_ALIAS_FLAG)
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
        val dens = mapView.resources.displayMetrics.density
        // Inicio y fin del recorrido, con la forma del tema.
        if (ruta.size > 1) listOf(ruta.first(), ruta.last()).forEachIndexed { i, g ->
            pr.toPixels(g, pt)
            relleno.color = if (i == 0) colorBorde else colorRuta; linea.color = fondoTexto; linea.strokeWidth = 2.5f
            Formas.dibujar(c, tema, pt.x.toFloat(), pt.y.toFloat(), 9f * dens, relleno, linea)
        }
        for (p in datos) {
            pr.toPixels(GeoPoint(p.lat, p.lon), pt)
            relleno.color = p.color
            linea.color = p.color or (0xFF shl 24); linea.strokeWidth = 3f
            val x = pt.x.toFloat(); val y = pt.y.toFloat(); val r = p.radio * escala
            Formas.dibujar(c, tema, x, y, r, relleno, if (p.borde) linea else null)
            p.nombre?.let { n ->
                // Etiqueta del lugar, en una cinta con el color del tema.
                letra.textSize = 11.5f * dens; letra.color = colorTexto
                val ancho = letra.measureText(n) / 2 + 6 * dens
                val top = y + r + 5 * dens
                fondoLetra.color = fondoTexto
                c.drawRoundRect(x - ancho, top, x + ancho, top + 17 * dens, 4 * dens, 4 * dens, fondoLetra)
                linea.color = colorBorde; linea.strokeWidth = 1.5f
                c.drawRoundRect(x - ancho, top, x + ancho, top + 17 * dens, 4 * dens, 4 * dens, linea)
                c.drawText(n, x, top + 12.5f * dens, letra)
            }
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
    return Tonos.calor(x, oscuro).copy(alpha = 0.38f + 0.47f * x).toArgb()
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

    val momentos = Repo.momentos.filter { it.lat != null && it.lon != null && it.fecha >= desde && it.tipo != "recaida" }
    val datos: List<Puntos.P> = when (capa) {
        "animo" -> momentos.mapNotNull { m ->
            val v = m.valencia?.toDouble() ?: Repo.dias[m.fecha]?.valencia?.toDouble() ?: return@mapNotNull null
            Puntos.P(m.lat!!, m.lon!!, Tonos.divergente(((v - 1) / 6).toFloat(), oscuro).copy(alpha = 0.75f).toArgb(), 14f, borde = true)
        }
        "calor" -> emptyList()
        "lugares" -> Repo.lugares.map { l -> Puntos.P(l.lat, l.lon, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f).toArgb(), 18f, borde = true, nombre = l.nombre) }
        else -> momentos.filter { it.fecha == dia }.map { m -> Puntos.P(m.lat!!, m.lon!!, MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f).toArgb(), 12f, borde = true) }
    }
    val ruta = if (capa == "ruta") puntos.filter { (it.precision ?: 0.0) < 150 }.map { GeoPoint(it.lat, it.lon) } else emptyList()
    val colorRuta = MaterialTheme.colorScheme.primary.toArgb()
    val tema = io.github.ruddy2003.vestigium.ui.Estilo.tema
    val cs = MaterialTheme.colorScheme

    Column(Modifier.fillMaxSize()) {
      // Controles arriba, siempre por encima del mapa y con fondo propio para que se puedan tocar.
      Column(Modifier.fillMaxWidth().zIndex(2f).background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp, top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = nav.atras) { Text(io.github.ruddy2003.vestigium.ui.Estilo.atras, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
            Column(Modifier.weight(1f)) {
                Text(io.github.ruddy2003.vestigium.ui.Estilo.pantalla("Mapa", "MAPPA").first, style = MaterialTheme.typography.headlineSmall)
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
                    capaPuntos.tema = tema; capaPuntos.colorBorde = cs.secondary.toArgb(); capaPuntos.colorTexto = cs.onSurface.toArgb(); capaPuntos.fondoTexto = cs.surface.toArgb()
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
            // Marco y rosa de los vientos con el estilo del tema (no estorban al tocar el mapa).
            MarcoMapa(tema, Modifier.fillMaxSize())
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

/** Marco del mapa y rosa de los vientos, dibujados con los colores y adornos del tema. */
@Composable
private fun MarcoMapa(tema: TemaId, modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    androidx.compose.foundation.Canvas(modifier) {
        val w = size.width; val h = size.height; val u = 1.dp.toPx()
        val tinta = if (oscuro) e.primary else e.onBackground
        // Marco: doble filete en los temas de papel, esquinas de mira en los de pantalla.
        when (tema) {
            TemaId.CIBER, TemaId.ESPACIO, TemaId.REFUGIO, TemaId.LABORATORIO, TemaId.ORDEN -> {
                val k = 22 * u; val m = 8 * u
                listOf(Offset(m, m) to Offset(1f, 1f), Offset(w - m, m) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f)).forEach { (o, d) ->
                    drawLine(e.primary, o, o + Offset(d.x * k, 0f), 2 * u); drawLine(e.primary, o, o + Offset(0f, d.y * k), 2 * u)
                }
            }
            else -> {
                drawRect(tinta.copy(alpha = 0.55f), Offset(4 * u, 4 * u), androidx.compose.ui.geometry.Size(w - 8 * u, h - 8 * u), style = androidx.compose.ui.graphics.drawscope.Stroke(2 * u))
                drawRect(tinta.copy(alpha = 0.35f), Offset(8 * u, 8 * u), androidx.compose.ui.geometry.Size(w - 16 * u, h - 16 * u), style = androidx.compose.ui.graphics.drawscope.Stroke(1 * u))
            }
        }
        // Rosa de los vientos arriba a la derecha.
        val c = Offset(w - 40 * u, 44 * u); val r = 24 * u
        drawCircle(e.surface.copy(alpha = 0.82f), r * 1.05f, c)
        drawCircle(tinta.copy(alpha = 0.6f), r * 1.05f, c, style = androidx.compose.ui.graphics.drawscope.Stroke(1.2f * u))
        val puntas = if (tema == TemaId.ANDALUS) 8 else 4
        for (i in 0 until puntas * 2) {
            val a = Math.toRadians(-90.0 + 360.0 * i / (puntas * 2)).toFloat()
            val larga = i % 2 == 0
            val rr = if (larga) r * 0.95f else r * 0.55f
            val lado = Math.toRadians(90.0).toFloat()
            val punta = c + Offset(kotlin.math.cos(a) * rr, kotlin.math.sin(a) * rr)
            val b1 = c + Offset(kotlin.math.cos(a + lado) * r * 0.13f, kotlin.math.sin(a + lado) * r * 0.13f)
            val b2 = c + Offset(kotlin.math.cos(a - lado) * r * 0.13f, kotlin.math.sin(a - lado) * r * 0.13f)
            val norte = i == 0
            drawPath(androidx.compose.ui.graphics.Path().apply { moveTo(punta.x, punta.y); lineTo(b1.x, b1.y); lineTo(c.x, c.y); close() }, if (norte) e.primary else tinta.copy(alpha = 0.8f))
            drawPath(androidx.compose.ui.graphics.Path().apply { moveTo(punta.x, punta.y); lineTo(b2.x, b2.y); lineTo(c.x, c.y); close() }, if (norte) e.primary.copy(alpha = 0.6f) else tinta.copy(alpha = 0.4f))
        }
        drawCircle(e.secondary, r * 0.1f, c)
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

