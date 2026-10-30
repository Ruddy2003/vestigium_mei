package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.privado
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Size
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
import io.github.ruddy2003.vestigium.ui.numeralMaya
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
import io.github.ruddy2003.vestigium.ui.Dialogo

/**
 * Mosaicos estándar de OpenStreetMap (no piden clave). Para que combinen con la app se tiñen en el teléfono:
 * de día, mármol desaturado; de noche, invertidos hacia basalto.
 */
private fun filtroMapa(oscuro: Boolean): ColorMatrixColorFilter {
    val tema = io.github.ruddy2003.vestigium.ui.Estilo.tema
    val invertir = ColorMatrix(floatArrayOf(-1f, 0f, 0f, 0f, 255f, 0f, -1f, 0f, 0f, 255f, 0f, 0f, -1f, 0f, 255f, 0f, 0f, 0f, 1f, 0f))
    fun tinte(r: Float, g: Float, b: Float, or: Float, og: Float, ob: Float) = ColorMatrix(floatArrayOf(r, 0f, 0f, 0f, or, 0f, g, 0f, 0f, og, 0f, 0f, b, 0f, ob, 0f, 0f, 0f, 1f, 0f))
    val m = ColorMatrix()
    when (tema) {
        // Romano: mármol desaturado de día, basalto de noche.
        TemaId.ROMANO -> if (oscuro) { m.setSaturation(0.2f); m.postConcat(invertir); m.postConcat(tinte(0.80f, 0.82f, 0.85f, 8f, 10f, 12f)) }
            else { m.setSaturation(0.45f); m.postConcat(tinte(0.97f, 0.97f, 0.95f, 6f, 6f, 4f)) }
        // Roma de hoy: el plano cálido, tirando a ocre, de día; de noche, las calles a la luz de sodio.
        TemaId.URBE -> if (oscuro) { m.setSaturation(0.25f); m.postConcat(invertir); m.postConcat(tinte(0.92f, 0.78f, 0.6f, 10f, 6f, 0f)) }
            else { m.setSaturation(0.55f); m.postConcat(tinte(1.0f, 0.94f, 0.84f, 10f, 4f, -4f)) }
        // Terminal: plano nocturno en azul eléctrico (solo existe en oscuro, así que es igual con o sin «oscuro»).
        TemaId.CIBER -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.30f, 0.62f, 0.80f, 0f, 4f, 10f)) }
        // Ciudad neón: el plano de noche teñido de magenta y violeta, como bajo los letreros.
        TemaId.NEON -> { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.78f, 0.34f, 0.62f, 14f, 2f, 16f)) }
        // Helénico: pergamino de día, noche púrpura de noche.
        TemaId.HELENO -> if (oscuro) { m.setSaturation(0.15f); m.postConcat(invertir); m.postConcat(tinte(0.78f, 0.70f, 0.92f, 6f, 4f, 14f)) }
            else { m.setSaturation(0.25f); m.postConcat(tinte(1.0f, 0.95f, 0.84f, 4f, 2f, 0f)) }
        // Papiro de día (amarillo, poco color) y, de noche, el oro de la lámpara sobre el hollín.
        TemaId.EGIPTO -> if (oscuro) { m.setSaturation(0.2f); m.postConcat(invertir); m.postConcat(tinte(0.92f, 0.78f, 0.55f, 10f, 6f, 0f)) }
            else { m.setSaturation(0.3f); m.postConcat(tinte(1.0f, 0.92f, 0.72f, 6f, 4f, -6f)) }
        // Scriptorium: mapa en sepia sobre pergamino; de noche, tinta parda.
        TemaId.MEDIEVAL -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.86f, 0.72f, 0.55f, 6f, 3f, 0f)) }
            else { m.setSaturation(0f); m.postConcat(tinte(1.0f, 0.92f, 0.76f, 0f, 0f, 0f)) }
        // Templario: la tierra de arena al sol; de noche, el azul acerado de la vigilia.
        TemaId.TEMPLARIO -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.72f, 0.75f, 0.82f, 0f, 2f, 8f)) }
            else { m.setSaturation(0.2f); m.postConcat(tinte(1.02f, 0.94f, 0.80f, 8f, 4f, 0f)) }
        // Distopía: gris de expediente, sin color.
        TemaId.DISTOPIA -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.82f, 0.83f, 0.84f, 4f, 4f, 4f)) }
            else { m.setSaturation(0f); m.postConcat(tinte(0.9f, 0.89f, 0.86f, 0f, 0f, 0f)) }
        // Refugio: fósforo verde de noche; papel de folleto de día.
        TemaId.REFUGIO -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.18f, 0.9f, 0.38f, 0f, 6f, 0f)) }
            else { m.setSaturation(0.3f); m.postConcat(tinte(0.97f, 0.96f, 0.88f, 4f, 4f, 0f)) }
        // Orden: acero.
        TemaId.ORDEN -> if (oscuro) { m.setSaturation(0.05f); m.postConcat(invertir); m.postConcat(tinte(0.76f, 0.8f, 0.86f, 6f, 8f, 10f)) }
            else { m.setSaturation(0.1f); m.postConcat(tinte(0.9f, 0.92f, 0.95f, 0f, 0f, 2f)) }
        // Laboratorio: blanco clínico con un toque cian; de noche, azul de monitor.
        TemaId.LABORATORIO -> if (oscuro) { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.58f, 0.8f, 0.95f, 2f, 6f, 12f)) }
            else { m.setSaturation(0.2f); m.postConcat(tinte(0.94f, 0.98f, 1.0f, 2f, 4f, 6f)) }
        // Colonos: mapa viejo de gaceta; de noche, azul marino.
        TemaId.COLONOS -> if (oscuro) { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.62f, 0.7f, 0.86f, 4f, 6f, 14f)) }
            else { m.setSaturation(0.35f); m.postConcat(tinte(1.0f, 0.95f, 0.86f, 2f, 0f, 0f)) }
        // Napoleónico: carta de estado mayor sepia; de noche, azul imperial.
        TemaId.NAPOLEON -> if (oscuro) { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.6f, 0.68f, 0.92f, 2f, 4f, 16f)) }
            else { m.setSaturation(0.25f); m.postConcat(tinte(1.0f, 0.94f, 0.82f, 2f, 0f, 0f)) }
        // Botánico: lámina de atlas en crema y verde; de noche, el bosque.
        TemaId.BOTANICO -> if (oscuro) { m.setSaturation(0.2f); m.postConcat(invertir); m.postConcat(tinte(0.66f, 0.86f, 0.66f, 2f, 8f, 2f)) }
            else { m.setSaturation(0.45f); m.postConcat(tinte(0.97f, 0.97f, 0.86f, 2f, 4f, 0f)) }
        // Ciudad verde: el plano gris de hormigón con un velo verde; de noche, asfalto oscuro y calles de musgo.
        TemaId.RUINAS -> if (oscuro) { m.setSaturation(0.15f); m.postConcat(invertir); m.postConcat(tinte(0.6f, 0.8f, 0.66f, 0f, 10f, 4f)) }
            else { m.setSaturation(0.3f); m.postConcat(tinte(0.92f, 0.95f, 0.88f, 0f, 6f, 0f)) }
        // Navidad: nieve; de noche, bosque.
        TemaId.NAVIDAD -> if (oscuro) { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.62f, 0.86f, 0.7f, 2f, 8f, 4f)) }
            else { m.setSaturation(0.35f); m.postConcat(tinte(0.98f, 0.97f, 0.94f, 4f, 4f, 2f)) }
        // Ruta de la Seda: arena; de noche, desierto bajo las estrellas.
        TemaId.SEDA -> if (oscuro) { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.66f, 0.7f, 0.92f, 4f, 6f, 16f)) }
            else { m.setSaturation(0.3f); m.postConcat(tinte(1.0f, 0.93f, 0.78f, 4f, 2f, 0f)) }
        // Japón Edo: washi con un velo de índigo; de noche, la posta a la luz de los farolillos.
        TemaId.EDO -> if (oscuro) { m.setSaturation(0.08f); m.postConcat(invertir); m.postConcat(tinte(0.62f, 0.66f, 0.86f, 6f, 4f, 12f)) }
            else { m.setSaturation(0.22f); m.postConcat(tinte(0.97f, 0.95f, 0.89f, 0f, 2f, 4f)) }
        // Gótico: piedra caliza; de noche, el azul de la bóveda.
        TemaId.GOTICO -> if (oscuro) { m.setSaturation(0.05f); m.postConcat(invertir); m.postConcat(tinte(0.56f, 0.6f, 0.95f, 2f, 4f, 22f)) }
            else { m.setSaturation(0.2f); m.postConcat(tinte(0.98f, 0.95f, 0.88f, 2f, 2f, 0f)) }
        // Horror cósmico: carta de sondeos amarillenta; de noche, la costa en la niebla verdinegra.
        TemaId.ARCANO -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.5f, 0.64f, 0.58f, 0f, 6f, 3f)) }
            else { m.setSaturation(0.12f); m.postConcat(tinte(0.93f, 0.92f, 0.82f, 0f, 3f, 0f)) }
        // Espacio: carta de navegación; de noche, como vista desde la órbita.
        TemaId.ESPACIO -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.42f, 0.55f, 0.85f, 0f, 2f, 8f)) }
            else { m.setSaturation(0.15f); m.postConcat(tinte(0.93f, 0.96f, 1.0f, 0f, 2f, 6f)) }
        // Abismo: la carta bajo el agua azul de la zona iluminada; de noche, la zona abisal en verde azulado de luz viva.
        TemaId.ABISMO -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.28f, 0.72f, 0.68f, 0f, 6f, 10f)) }
            else { m.setSaturation(0.2f); m.postConcat(tinte(0.82f, 0.95f, 1.0f, 0f, 6f, 12f)) }
        // Al-Ándalus: arena y yeso; de noche, el jardín en verde azulado.
        TemaId.ANDALUS -> if (oscuro) { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.6f, 0.82f, 0.78f, 2f, 8f, 6f)) }
            else { m.setSaturation(0.35f); m.postConcat(tinte(1.0f, 0.94f, 0.82f, 4f, 2f, 0f)) }
        // Mesoamérica: el papel amate con el verde de la selva; de noche, la obsidiana con el azul maya.
        TemaId.MESO -> if (oscuro) { m.setSaturation(0.12f); m.postConcat(invertir); m.postConcat(tinte(0.52f, 0.76f, 0.84f, 0f, 6f, 10f)) }
            else { m.setSaturation(0.42f); m.postConcat(tinte(0.97f, 0.95f, 0.8f, 6f, 6f, -2f)) }
        // Mongol: la estepa bajo el cielo azul; de noche, el azul profundo.
        TemaId.MONGOL -> if (oscuro) { m.setSaturation(0.1f); m.postConcat(invertir); m.postConcat(tinte(0.55f, 0.68f, 0.95f, 2f, 6f, 18f)) }
            else { m.setSaturation(0.4f); m.postConcat(tinte(0.97f, 0.97f, 0.92f, 2f, 2f, 4f)) }
        // Renacimiento: mapa dibujado a sanguina; de noche, nogal.
        TemaId.RENACIMIENTO -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.85f, 0.68f, 0.5f, 6f, 2f, 0f)) }
            else { m.setSaturation(0f); m.postConcat(tinte(1.0f, 0.88f, 0.7f, 0f, 0f, 0f)) }
        // Pirata: carta náutica en sepia sobre pergamino húmedo; de noche, el mar verde oscuro a la luz del farol.
        TemaId.PIRATA -> if (oscuro) { m.setSaturation(0.05f); m.postConcat(invertir); m.postConcat(tinte(0.62f, 0.74f, 0.7f, 2f, 6f, 4f)) }
            else { m.setSaturation(0.2f); m.postConcat(tinte(0.98f, 0.92f, 0.76f, 6f, 4f, 0f)) }
        // Soviético: papel de cartel en blanco y negro con un toque rojo.
        TemaId.URSS -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.95f, 0.78f, 0.74f, 6f, 0f, 0f)) }
            else { m.setSaturation(0f); m.postConcat(tinte(1.0f, 0.93f, 0.84f, 4f, 0f, 0f)) }
        // Revolución: papel de cartel con tinta de imprenta; de noche, la plaza con antorchas.
        TemaId.REVOLUCION -> if (oscuro) { m.setSaturation(0f); m.postConcat(invertir); m.postConcat(tinte(0.72f, 0.74f, 0.82f, 6f, 4f, 8f)) }
            else { m.setSaturation(0.12f); m.postConcat(tinte(1.0f, 0.96f, 0.88f, 4f, 2f, 0f)) }
        // Tierras Antiguas: pergamino verdoso de día; de noche, bosque con luz de luna.
        TemaId.ANTIGUAS -> if (oscuro) { m.setSaturation(0.15f); m.postConcat(invertir); m.postConcat(tinte(0.70f, 0.80f, 0.74f, 4f, 10f, 6f)) }
            else { m.setSaturation(0.3f); m.postConcat(tinte(0.97f, 1.0f, 0.90f, 4f, 6f, 0f)) }
        // Prehistoria: el plano en tonos de ocre y caliza de día; de noche, la roca a la luz del fuego.
        TemaId.CUEVA -> if (oscuro) { m.setSaturation(0.15f); m.postConcat(invertir); m.postConcat(tinte(0.85f, 0.72f, 0.6f, 8f, 4f, 0f)) }
            else { m.setSaturation(0.35f); m.postConcat(tinte(1.0f, 0.92f, 0.8f, 8f, 2f, -6f)) }
    }
    return ColorMatrixColorFilter(m)
}

/**
 * La forma con que cada tema marca un lugar en el mapa, una distinta por tema: tabla con asas (Romano), cursor de bloque
 * (Terminal), sol de dieciséis rayos (Helénico), sello de cera (Scriptorium), ojo (Distopía), señal de refugio (Refugio),
 * escudo (Orden), anillo de benceno (Laboratorio), casita (Colonos), bicornio (Napoleónico), hoja (Botánico), abeto (Navidad),
 * moneda con agujero (Seda), arco ojival (Gótico), destello (Espacio), estrella de dos cuadrados (Al-Ándalus), corona de yurta
 * (Mongol), cuadrado con el círculo de Vitruvio (Renacimiento), estrella (Soviético), escarapela (Revolución) y gema tallada
 * (Tierras Antiguas). Con borde, cada una lleva además su detalle. Se dibujan directo en el lienzo del mapa (rápido aunque sean miles).
 */
internal object Formas {
    private val p = android.graphics.Path()
    private val horario = android.graphics.Path.Direction.CW
    private val antihorario = android.graphics.Path.Direction.CCW
    private fun poligono(x: Float, y: Float, r: Float, n: Int, giro: Double = -90.0, interior: Float? = null) {
        p.reset()
        sumarPoligono(x, y, r, n, giro, interior)
    }

    /** Agrega un polígono (o estrella, con `interior`) al trazo sin borrar lo anterior: para figuras de varias piezas. */
    private fun sumarPoligono(x: Float, y: Float, r: Float, n: Int, giro: Double, interior: Float? = null) {
        val pasos = if (interior != null) n * 2 else n
        for (i in 0 until pasos) {
            val rr = if (interior != null && i % 2 == 1) r * interior else r
            val a = Math.toRadians(giro + 360.0 * i / pasos)
            val px = x + rr * kotlin.math.cos(a).toFloat(); val py = y + rr * kotlin.math.sin(a).toFloat()
            if (i == 0) p.moveTo(px, py) else p.lineTo(px, py)
        }
        p.close()
    }

    /** Una figura cerrada a partir de pares (kx, ky): cada vértice está en (x + kx·r, y + ky·r). */
    private fun figura(x: Float, y: Float, r: Float, vararg k: Float) {
        p.reset()
        var i = 0
        while (i + 1 < k.size) {
            val px = x + k[i] * r; val py = y + k[i + 1] * r
            if (i == 0) p.moveTo(px, py) else p.lineTo(px, py)
            i += 2
        }
        p.close()
    }

    /** Una línea de (x + a·r, y + b·r) a (x + e·r, y + f·r). */
    private fun raya(c: Canvas, x: Float, y: Float, r: Float, a: Float, b: Float, e: Float, f: Float, pincel: Paint) =
        c.drawLine(x + a * r, y + b * r, x + e * r, y + f * r, pincel)

    fun dibujar(c: Canvas, tema: TemaId, x: Float, y: Float, r: Float, relleno: Paint, borde: Paint?) {
        when (tema) {
            // Tabula ansata: la placa de las inscripciones, con sus dos asas en cola de milano.
            TemaId.ROMANO -> figura(x, y, r, -0.8f, -0.7f, 0.8f, -0.7f, 0.8f, -0.12f, 1.3f, -0.5f, 1.3f, 0.5f, 0.8f, 0.12f,
                0.8f, 0.7f, -0.8f, 0.7f, -0.8f, 0.12f, -1.3f, 0.5f, -1.3f, -0.5f, -0.8f, -0.12f)
            // Una cúpula con su tambor y su linterna.
            TemaId.URBE -> {
                p.reset(); p.moveTo(x - r, y + r * 0.7f); p.lineTo(x - r, y)
                p.arcTo(android.graphics.RectF(x - r, y - r, x + r, y + r), 180f, 180f, false)
                p.lineTo(x + r, y + r * 0.7f); p.close()
                p.addRect(x - r * 0.18f, y - r * 1.4f, x + r * 0.18f, y - r * 0.96f, horario)
            }
            // El cursor de bloque de la consola.
            TemaId.CIBER -> { p.reset(); p.addRect(x - r * 0.55f, y - r * 0.9f, x + r * 0.55f, y + r * 0.9f, horario) }
            TemaId.NEON -> { p.reset(); p.addRoundRect(x - r * 1.15f, y - r * 0.62f, x + r * 1.15f, y + r * 0.62f, r * 0.62f, r * 0.62f, horario) }   // la placa del letrero
            // El sol de Vergina: dieciséis rayos.
            TemaId.HELENO -> poligono(x, y, r * 1.15f, 16, -90.0, 0.5f)
            // Una pirámide.
            TemaId.EGIPTO -> figura(x, y, r, 0f, -1.1f, 1.15f, 0.8f, -1.15f, 0.8f)
            TemaId.MEDIEVAL -> {   // sello de cera de borde ondulado
                p.reset()
                for (i in 0..36) { val a = 2 * Math.PI * i / 36; val rr = r * (1f + 0.09f * kotlin.math.sin(a * 7).toFloat())
                    val px = x + rr * kotlin.math.cos(a).toFloat(); val py = y + rr * kotlin.math.sin(a).toFloat(); if (i == 0) p.moveTo(px, py) else p.lineTo(px, py) }
                p.close()
            }
            // El escudo de lágrima.
            TemaId.TEMPLARIO -> figura(x, y, r, -0.82f, -0.9f, 0.82f, -0.9f, 0.8f, -0.1f, 0.58f, 0.5f, 0f, 1.05f, -0.58f, 0.5f, -0.8f, -0.1f)
            // El ojo que todo lo ve.
            TemaId.DISTOPIA -> { p.reset(); p.moveTo(x - r * 1.2f, y); p.quadTo(x, y - r * 1.2f, x + r * 1.2f, y); p.quadTo(x, y + r * 1.2f, x - r * 1.2f, y); p.close() }
            // La señal de refugio: un triángulo con la punta hacia abajo.
            TemaId.REFUGIO -> poligono(x, y, r * 1.2f, 3, 90.0)
            // El escudo de la orden.
            TemaId.ORDEN -> { p.reset(); p.moveTo(x - r, y - r); p.lineTo(x + r, y - r); p.lineTo(x + r, y - r * 0.1f); p.quadTo(x + r, y + r * 0.7f, x, y + r * 1.2f)
                p.quadTo(x - r, y + r * 0.7f, x - r, y - r * 0.1f); p.close() }
            // El anillo de benceno: un hexágono (con borde lleva su círculo dentro).
            TemaId.LABORATORIO -> poligono(x, y, r, 6, -90.0)
            // Una casita del pueblo.
            TemaId.COLONOS -> { p.reset(); p.moveTo(x, y - r * 1.1f); p.lineTo(x + r, y - r * 0.2f); p.lineTo(x + r, y + r); p.lineTo(x - r, y + r); p.lineTo(x - r, y - r * 0.2f); p.close() }
            // El bicornio, visto de frente.
            TemaId.NAPOLEON -> { p.reset(); p.moveTo(x - r * 1.4f, y + r * 0.3f); p.quadTo(x - r * 0.9f, y - r * 1.1f, x, y - r * 0.9f)
                p.quadTo(x + r * 0.9f, y - r * 1.1f, x + r * 1.4f, y + r * 0.3f); p.quadTo(x, y - r * 0.2f, x - r * 1.4f, y + r * 0.3f); p.close() }
            // Una hoja.
            TemaId.BOTANICO -> { p.reset(); p.moveTo(x, y + r * 1.2f); p.quadTo(x + r * 1.3f, y, x, y - r * 1.2f); p.quadTo(x - r * 1.3f, y, x, y + r * 1.2f); p.close() }
            // Una hoja de hiedra de cinco puntas.
            TemaId.RUINAS -> figura(x, y, r, 0f, 0.9f, -0.55f, 0.62f, -0.95f, -0.05f, -0.38f, -0.08f, 0f, -0.95f, 0.38f, -0.08f, 0.95f, -0.05f, 0.55f, 0.62f)
            // Un abeto de tres pisos con su tronco.
            TemaId.NAVIDAD -> figura(x, y, r, 0f, -1.35f, 0.55f, -0.55f, 0.3f, -0.55f, 0.85f, 0.2f, 0.5f, 0.2f, 1.05f, 0.85f, 0.2f, 0.85f, 0.2f, 1.25f,
                -0.2f, 1.25f, -0.2f, 0.85f, -1.05f, 0.85f, -0.5f, 0.2f, -0.85f, 0.2f, -0.3f, -0.55f, -0.55f, -0.55f)
            // La moneda de la ruta, con su agujero cuadrado.
            TemaId.SEDA -> { p.reset(); p.addCircle(x, y, r, horario); p.addRect(x - r * 0.32f, y - r * 0.32f, x + r * 0.32f, y + r * 0.32f, antihorario) }
            // El chōchin: el farol de papel de la posada.
            TemaId.EDO -> { p.reset(); p.addRoundRect(x - r * 0.75f, y - r, x + r * 0.75f, y + r, r * 0.55f, r * 0.55f, horario) }
            // Un arco ojival.
            TemaId.GOTICO -> { p.reset(); p.moveTo(x - r * 0.8f, y + r); p.lineTo(x - r * 0.8f, y - r * 0.1f); p.quadTo(x - r * 0.8f, y - r * 0.9f, x, y - r * 1.3f)
                p.quadTo(x + r * 0.8f, y - r * 0.9f, x + r * 0.8f, y - r * 0.1f); p.lineTo(x + r * 0.8f, y + r); p.close() }
            // El heptágono del signo.
            TemaId.ARCANO -> poligono(x, y, r * 1.15f, 7, -90.0)
            // Un destello de cuatro puntas.
            TemaId.ESPACIO -> { p.reset(); val k = r * 1.3f; p.moveTo(x, y - k); p.quadTo(x, y, x + k, y); p.quadTo(x, y, x, y + k); p.quadTo(x, y, x - k, y); p.quadTo(x, y, x, y - k); p.close() }
            // Una gota de agua.
            TemaId.ABISMO -> { p.reset(); p.moveTo(x, y - r * 1.35f); p.cubicTo(x + r * 0.4f, y - r * 0.7f, x + r * 0.95f, y - r * 0.2f, x + r * 0.95f, y + r * 0.3f)
                p.cubicTo(x + r * 0.95f, y + r * 0.85f, x + r * 0.5f, y + r * 1.1f, x, y + r * 1.1f); p.cubicTo(x - r * 0.5f, y + r * 1.1f, x - r * 0.95f, y + r * 0.85f, x - r * 0.95f, y + r * 0.3f)
                p.cubicTo(x - r * 0.95f, y - r * 0.2f, x - r * 0.4f, y - r * 0.7f, x, y - r * 1.35f); p.close() }
            // La estrella de ocho puntas hecha de dos cuadrados (los dos giran igual, así que se suman).
            TemaId.ANDALUS -> { p.reset(); sumarPoligono(x, y, r * 1.15f, 4, -90.0); sumarPoligono(x, y, r * 1.15f, 4, -45.0) }
            // Una pirámide escalonada de tres cuerpos con el templo arriba.
            TemaId.MESO -> figura(x, y, r, -1.15f, 0.95f, -1.15f, 0.5f, -0.8f, 0.5f, -0.8f, 0.05f, -0.45f, 0.05f, -0.45f, -0.4f, -0.22f, -0.4f, -0.22f, -1.05f,
                0.22f, -1.05f, 0.22f, -0.4f, 0.45f, -0.4f, 0.45f, 0.05f, 0.8f, 0.05f, 0.8f, 0.5f, 1.15f, 0.5f, 1.15f, 0.95f)
            // La corona de la yurta (toono): un anillo con sus dos varas cruzadas.
            TemaId.MONGOL -> {
                p.reset(); p.addCircle(x, y, r, horario); p.addCircle(x, y, r * 0.5f, antihorario)
                p.addRect(x - r * 0.5f, y - r * 0.09f, x + r * 0.5f, y + r * 0.09f, horario); p.addRect(x - r * 0.09f, y - r * 0.5f, x + r * 0.09f, y + r * 0.5f, horario)
            }
            // El cuadrado de Vitruvio con el círculo calado.
            TemaId.RENACIMIENTO -> { p.reset(); p.addRect(x - r, y - r, x + r, y + r, horario); p.addCircle(x, y, r * 0.62f, antihorario) }
            // La «X» del tesoro: dos barras cruzadas, en el mismo sentido para que se sumen.
            TemaId.PIRATA -> {
                p.reset(); val a = r * 1.05f; val e = r * 0.42f
                p.moveTo(x - a, y - a + e); p.lineTo(x - a + e, y - a); p.lineTo(x + a, y + a - e); p.lineTo(x + a - e, y + a); p.close()
                p.moveTo(x - a + e, y + a); p.lineTo(x - a, y + a - e); p.lineTo(x + a - e, y - a); p.lineTo(x + a, y - a + e); p.close()
            }
            // La estrella de cinco puntas.
            TemaId.URSS -> poligono(x, y, r * 1.25f, 5, -90.0, 0.42f)
            TemaId.REVOLUCION -> poligono(x, y, r * 1.12f, 14, -90.0, 0.84f)   // escarapela plisada
            TemaId.ANTIGUAS -> poligono(x, y, r * 1.2f, 6, -90.0, 0.5f)   // gema tallada de seis puntas
            TemaId.CUEVA -> { p.reset(); p.addOval(android.graphics.RectF(x - r * 0.62f, y - r * 0.85f, x + r * 0.62f, y + r * 0.95f), horario) }   // la huella de un pie
        }
        c.drawPath(p, relleno)
        borde?.let { b -> c.drawPath(p, b) }
        // Detalles que van encima de la forma: uno propio por tema.
        val d = borde ?: return
        when (tema) {
            TemaId.ROMANO -> c.drawRect(x - r * 0.6f, y - r * 0.48f, x + r * 0.6f, y + r * 0.48f, d)   // el campo de la inscripción
            TemaId.URBE -> { raya(c, x, y, r, -1f, 0.25f, 1f, 0.25f, d); raya(c, x, y, r, 0f, -0.95f, 0f, 0.25f, d) }   // la cornisa del tambor y el nervio del casquete
            TemaId.CIBER -> { raya(c, x, y, r, -1.55f, -0.45f, -0.95f, 0f, d); raya(c, x, y, r, -0.95f, 0f, -1.55f, 0.45f, d) }   // el «>» del indicador
            TemaId.NEON -> { raya(c, x, y, r, -0.6f, -0.15f, 0.6f, -0.15f, d); raya(c, x, y, r, -0.35f, 0.2f, 0.35f, 0.2f, d) }   // los renglones del letrero
            TemaId.HELENO -> c.drawCircle(x, y, r * 0.32f, d)
            TemaId.EGIPTO -> { raya(c, x, y, r, -0.36f, -0.48f, 0.36f, -0.48f, d); raya(c, x, y, r, 0f, -1.1f, 0.3f, 0.8f, d) }   // el piramidión y la arista en sombra
            TemaId.MEDIEVAL -> { c.drawCircle(x, y, r * 0.62f, d); raya(c, x, y, r, -0.3f, 0f, 0.3f, 0f, d); raya(c, x, y, r, 0f, -0.3f, 0f, 0.3f, d) }   // la cruz del sello
            TemaId.TEMPLARIO -> { raya(c, x, y, r, 0f, -0.65f, 0f, 0.55f, d); raya(c, x, y, r, -0.52f, -0.15f, 0.52f, -0.15f, d)
                raya(c, x, y, r, -0.62f, -0.75f, 0.62f, -0.75f, d) }   // la cruz del escudo y su jefe
            TemaId.DISTOPIA -> { c.drawCircle(x, y, r * 0.38f, d); c.drawCircle(x, y, r * 0.12f, d) }   // la pupila
            TemaId.REFUGIO -> { poligono(x, y, r * 0.55f, 3, 90.0); c.drawPath(p, d) }
            TemaId.ORDEN -> { raya(c, x, y, r, 0f, -0.8f, 0f, 0.95f, d); raya(c, x, y, r, -0.72f, -0.25f, 0.72f, -0.25f, d) }   // la cruz del escudo
            TemaId.LABORATORIO -> c.drawCircle(x, y, r * 0.55f, d)
            TemaId.COLONOS -> c.drawRect(x - r * 0.25f, y + r * 0.25f, x + r * 0.25f, y + r, d)   // la puerta
            TemaId.NAPOLEON -> c.drawCircle(x + r * 0.5f, y - r * 0.38f, r * 0.22f, d)   // la escarapela del sombrero
            TemaId.BOTANICO -> { raya(c, x, y, r, 0f, 1.2f, 0f, -0.8f, d); raya(c, x, y, r, 0f, 0.2f, 0.45f, -0.25f, d); raya(c, x, y, r, 0f, 0.2f, -0.45f, -0.25f, d) }
            TemaId.RUINAS -> { raya(c, x, y, r, 0f, 0.8f, 0f, -0.6f, d); raya(c, x, y, r, 0f, 0.2f, -0.5f, -0.05f, d); raya(c, x, y, r, 0f, 0.2f, 0.5f, -0.05f, d) }   // los nervios
            TemaId.NAVIDAD -> { c.drawCircle(x, y - r * 1.35f, r * 0.18f, d); raya(c, x, y, r, -0.6f, 0.55f, 0.45f, 0.1f, d) }   // la estrella y la guirnalda
            TemaId.SEDA -> c.drawCircle(x, y, r * 0.72f, d)
            TemaId.EDO -> { raya(c, x, y, r, -0.7f, -0.45f, 0.7f, -0.45f, d); raya(c, x, y, r, -0.75f, 0f, 0.75f, 0f, d); raya(c, x, y, r, -0.7f, 0.45f, 0.7f, 0.45f, d) }   // las costillas de bambú
            TemaId.GOTICO -> { raya(c, x, y, r, 0f, -0.3f, 0f, 1f, d); c.drawCircle(x, y - r * 0.6f, r * 0.2f, d) }   // el parteluz y el rosetón
            TemaId.ARCANO -> { raya(c, x, y, r, 0f, -0.45f, 0f, 0.45f, d); c.drawCircle(x, y, r * 0.45f, d) }   // el ojo, con su pupila
            TemaId.ESPACIO -> c.drawOval(x - r * 1.45f, y - r * 0.4f, x + r * 1.45f, y + r * 0.4f, d)   // una órbita
            TemaId.ABISMO -> c.drawCircle(x - r * 0.3f, y + r * 0.15f, r * 0.24f, d)   // el brillo de la gota
            TemaId.ANDALUS -> c.drawCircle(x, y, r * 0.42f, d)
            TemaId.MESO -> raya(c, x, y, r, 0f, -0.4f, 0f, 0.95f, d)   // la escalinata
            TemaId.MONGOL -> for (i in 0 until 8) {   // las varas del techo, que salen de la corona
                val a = Math.PI / 4 * i; val cx = kotlin.math.cos(a).toFloat(); val sy = kotlin.math.sin(a).toFloat()
                c.drawLine(x + cx * r * 1.08f, y + sy * r * 1.08f, x + cx * r * 1.42f, y + sy * r * 1.42f, d)
            }
            TemaId.RENACIMIENTO -> {   // el círculo que pasa por las esquinas y la retícula de dibujo
                c.drawCircle(x, y, r * 1.41f, d); raya(c, x, y, r, -1.6f, 0f, 1.6f, 0f, d); raya(c, x, y, r, 0f, -1.6f, 0f, 1.6f, d)
            }
            TemaId.PIRATA -> c.drawCircle(x, y, r * 0.24f, d)   // el punto donde está enterrado
            TemaId.URSS -> for (i in 0 until 5) {   // las aristas de la estrella facetada
                val a = Math.toRadians(-90.0 + 72.0 * i)
                c.drawLine(x, y, x + r * 1.25f * kotlin.math.cos(a).toFloat(), y + r * 1.25f * kotlin.math.sin(a).toFloat(), d)
            }
            TemaId.REVOLUCION -> c.drawCircle(x, y, r * 0.42f, d)
            TemaId.ANTIGUAS -> { raya(c, x, y, r, 0f, -0.5f, 0f, 0.5f, d); raya(c, x, y, r, -0.3f, -0.15f, 0.3f, 0.15f, d) }   // una runa
            TemaId.CUEVA -> listOf(-0.42f to 1.12f, 0f to 1.24f, 0.4f to 1.14f).forEach { (dx, dy) -> c.drawCircle(x + dx * r, y - dy * r, r * 0.17f, d) }   // los dedos de la huella
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

    val momentos = Repo.momentos.filter { it.lat != null && it.lon != null && it.fecha >= desde && !it.privado }
    val datos: List<Puntos.P> = when (capa) {
        "animo" -> momentos.mapNotNull { m ->
            val v = m.valencia?.toDouble() ?: Repo.dias[m.fecha]?.valencia?.toDouble() ?: return@mapNotNull null
            Puntos.P(m.lat!!, m.lon!!, Tonos.divergente(((v - 1) / 6).toFloat(), oscuro).copy(alpha = 0.75f).toArgb(), 14f, borde = true)
        }
        "calor" -> emptyList()
        "lugares" -> Repo.lugares.map { l -> Puntos.P(l.lat, l.lon, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f).toArgb(), 18f, borde = true, nombre = l.nombre) }
        else -> momentos.filter { it.fecha == dia }.map { m -> Puntos.P(m.lat!!, m.lon!!, MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f).toArgb(), 12f, borde = true) }
    }
    val ruta = if (capa == "ruta") puntos.filter { (it.precision ?: io.github.ruddy2003.vestigium.data.Db.PRECISION_SIN_DATO) < 150 }.map { GeoPoint(it.lat, it.lon) } else emptyList()
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
                Text("${io.github.ruddy2003.vestigium.ui.Estilo.comentario}mapa · «＋ lugar aquí» marca el centro (o mantén presionado un punto)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // La alternativa visible al toque largo (N-24): marca el centro del mapa.
            TextButton(onClick = { mapa.value?.mapCenter?.let { c -> nuevoLugar = GeoPoint(c.latitude, c.longitude) } }) {
                Text("＋ lugar aquí", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
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
                        val centro = Repo.momentos.lastOrNull { it.lat != null && !it.privado }?.let { GeoPoint(it.lat!!, it.lon!!) }
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
        var tipoNuevo by remember(p) { mutableStateOf("otro") }
        Dialogo(
            onDismissRequest = { nuevoLugar = null },
            title = { Text("Marcar este lugar", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Ayuda("%.5f, %.5f · la app registrará cuándo llegas y te vas.".format(p.latitude, p.longitude))
                    OutlinedTextField(nombre, { nombre = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("casa, gimnasio, oficina…") })
                    io.github.ruddy2003.vestigium.ui.FilaFichas(io.github.ruddy2003.vestigium.data.Catalogo.TIPOS_LUGAR, tipoNuevo) { t -> if (t != null) tipoNuevo = t }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (nombre.isNotBlank()) {
                        val l = Lugar(Repo.nuevoId(), nombre.trim(), p.latitude, p.longitude, tipo = tipoNuevo)
                        Repo.guardarLugar(l)
                        runCatching { io.github.ruddy2003.vestigium.data.Lugares.reconstruir(l) }
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
internal fun MarcoMapa(tema: TemaId, modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    androidx.compose.foundation.Canvas(modifier) {
        val u = 1.dp.toPx()
        val tinta = if (oscuro) e.primary else e.onBackground
        marcoDelMapa(tema, u, tinta, e.primary, e.surface)
        // Rosa de los vientos arriba a la derecha.
        rosaDelMapa(tema, Offset(size.width - 40 * u, 44 * u), 24 * u, tinta, e.primary, e.secondary, e.surface)
    }
}

/** La estrella de ocho puntas hecha con dos cuadrados, como en las yeserías (los dos giran igual, así que se suman). */
private fun estrellaDeCuadros(c: Offset, r: Float): Path = Path().apply {
    listOf(-90.0, -45.0).forEach { g0 ->
        for (i in 0 until 4) {
            val a = Math.toRadians(g0 + 90.0 * i)
            val x = c.x + r * kotlin.math.cos(a).toFloat(); val y = c.y + r * kotlin.math.sin(a).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
}

/**
 * El marco de un mapa con el adorno del tema. Va solo en los bordes, para no tapar lo que hay en medio.
 * `u` es lo que mide un dp en el dibujo: en el mapa, un dp; en las tarjetas pequeñas, menos.
 */
internal fun DrawScope.marcoDelMapa(tema: TemaId, u: Float, tinta: Color, acento: Color, fondo: Color) {
    val w = size.width; val h = size.height
    fun filete(m: Float, color: Color, grosor: Float) = drawRect(color, Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(grosor))
    // Repite un dibujo en las cuatro esquinas: le pasa la esquina (a `m` del borde) y hacia dónde queda el centro (±1, ±1).
    fun esquinas(m: Float, dibujo: (Offset, Float, Float) -> Unit) {
        dibujo(Offset(m, m), 1f, 1f); dibujo(Offset(w - m, m), -1f, 1f); dibujo(Offset(m, h - m), 1f, -1f); dibujo(Offset(w - m, h - m), -1f, -1f)
    }
    when (tema) {
        // Doble filete con dentículos de cornisa arriba y abajo, y un cuadro de pórfido en cada esquina.
        TemaId.ROMANO -> {
            filete(4 * u, tinta.copy(alpha = 0.55f), 2 * u); filete(8 * u, tinta.copy(alpha = 0.35f), 1 * u)
            var x = 14 * u
            while (x < w - 18 * u) {
                drawRect(tinta.copy(alpha = 0.35f), Offset(x, 11 * u), Size(4 * u, 3.5f * u))
                drawRect(tinta.copy(alpha = 0.35f), Offset(x, h - 14.5f * u), Size(4 * u, 3.5f * u))
                x += 8 * u
            }
            esquinas(4 * u) { o, _, _ -> drawRect(acento, Offset(o.x - 3 * u, o.y - 3 * u), Size(6 * u, 6 * u)) }
        }
        // Las vías del tranvía arriba y abajo, con sus durmientes, y un tornillo de placa en cada esquina.
        TemaId.URBE -> {
            filete(4 * u, tinta.copy(alpha = 0.5f), 1.4f * u)
            listOf(9 * u, h - 9 * u).forEach { y ->
                drawLine(tinta.copy(alpha = 0.4f), Offset(14 * u, y - 2 * u), Offset(w - 14 * u, y - 2 * u), 0.8f * u)
                drawLine(tinta.copy(alpha = 0.4f), Offset(14 * u, y + 2 * u), Offset(w - 14 * u, y + 2 * u), 0.8f * u)
                var x = 16 * u; while (x < w - 14 * u) { drawLine(tinta.copy(alpha = 0.3f), Offset(x, y - 3.5f * u), Offset(x, y + 3.5f * u), 1.4f * u); x += 7 * u }
            }
            esquinas(9 * u) { o, _, _ -> drawCircle(acento, 2.4f * u, o); drawLine(fondo, o - Offset(1.6f * u, 0f), o + Offset(1.6f * u, 0f), 0.7f * u) }
        }
        // Líneas de barrido de la pantalla de fósforo, esquinas de mira y el cursor.
        TemaId.CIBER -> {
            var y = 0f
            while (y < h) { drawLine(acento.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), 0.6f * u); y += 3 * u }
            val k = 22 * u
            esquinas(8 * u) { o, dx, dy -> drawLine(acento, o, o + Offset(dx * k, 0f), 2 * u); drawLine(acento, o, o + Offset(0f, dy * k), 2 * u) }
            drawRect(acento, Offset(14 * u, h - 24 * u), Size(7 * u, 11 * u))
        }
        // Un marco de tubo de neón con su halo, roto en las esquinas, y un letrerito vertical colgado a la derecha.
        TemaId.NEON -> {
            val k = 10 * u
            drawRoundRect(acento.copy(alpha = 0.2f), Offset(6 * u, 6 * u), Size(w - 12 * u, h - 12 * u), androidx.compose.ui.geometry.CornerRadius(10 * u), style = Stroke(5 * u))
            drawRoundRect(acento, Offset(6 * u, 6 * u), Size(w - 12 * u, h - 12 * u), androidx.compose.ui.geometry.CornerRadius(10 * u), style = Stroke(1.4f * u,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf((w - 12 * u - 2 * k) / 2, k))))
            drawRect(fondo.copy(alpha = 0.85f), Offset(w - 22 * u, 22 * u), Size(10 * u, 34 * u))
            drawRect(tinta, Offset(w - 22 * u, 22 * u), Size(10 * u, 34 * u), style = Stroke(1.2f * u))
            for (j in 0 until 3) drawLine(tinta, Offset(w - 20 * u, (28 + j * 10) * u), Offset(w - 14 * u, (28 + j * 10) * u), 1.2f * u)
        }
        // Una greca arriba y otra abajo.
        TemaId.HELENO -> {
            filete(4 * u, tinta.copy(alpha = 0.5f), 1.2f * u)
            val a = 5 * u
            listOf(8 * u, h - 8 * u - a).forEach { y0 ->
                val greca = Path().apply {
                    moveTo(8 * u, y0 + a)
                    var x = 8 * u
                    while (x + 2 * a <= w - 8 * u) { lineTo(x, y0); lineTo(x + a, y0); lineTo(x + a, y0 + a); lineTo(x + 2 * a, y0 + a); x += 2 * a }
                }
                drawPath(greca, tinta.copy(alpha = 0.45f), style = Stroke(1.2f * u))
            }
        }
        // La gola pintada arriba (hojas de palma en franjas) y la cenefa de rectángulos abajo.
        TemaId.EGIPTO -> {
            filete(4 * u, tinta.copy(alpha = 0.5f), 1.2f * u)
            var x = 8 * u; var k = 0
            while (x < w - 8 * u) {
                drawRect(tinta.copy(alpha = if (k % 2 == 0) 0.45f else 0.2f), Offset(x, 8 * u), Size(3 * u, 8 * u))
                drawRect(tinta.copy(alpha = if (k % 2 == 0) 0.4f else 0.18f), Offset(x, h - 13 * u), Size(3 * u, 4 * u))
                x += 4.5f * u; k++
            }
            drawLine(tinta.copy(alpha = 0.5f), Offset(8 * u, 17 * u), Offset(w - 8 * u, 17 * u), 1f * u)
        }
        // El pautado del copista: doble raya roja en el margen izquierdo y un cuadrifolio en cada esquina.
        TemaId.MEDIEVAL -> {
            filete(5 * u, tinta.copy(alpha = 0.45f), 1 * u)
            drawLine(acento.copy(alpha = 0.7f), Offset(11 * u, 5 * u), Offset(11 * u, h - 5 * u), 1.4f * u)
            drawLine(acento.copy(alpha = 0.7f), Offset(14 * u, 5 * u), Offset(14 * u, h - 5 * u), 0.8f * u)
            esquinas(5 * u) { o, _, _ ->
                listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { d -> drawCircle(acento, 2.6f * u, o + d * (2.6f * u)) }
                drawCircle(fondo, 1.4f * u, o)
            }
        }
        // El muro: filete de acero, almenas arriba y abajo y una cruz patada en cada esquina.
        TemaId.TEMPLARIO -> {
            filete(5 * u, tinta.copy(alpha = 0.45f), 1.2f * u)
            val m = 9 * u
            listOf(5 * u, h - 5 * u - 4 * u).forEach { y0 -> var x = 14 * u; while (x + m < w - 14 * u) { drawRect(tinta.copy(alpha = 0.35f), Offset(x, y0), Size(m, 4 * u)); x += 2 * m } }
            esquinas(5 * u) { o, _, _ -> drawCircle(fondo, 5 * u, o); drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(o, 4.6f * u), acento) }
        }
        // La cámara que graba: el punto rojo, la tira del expediente y la regla de tiempo abajo.
        TemaId.DISTOPIA -> {
            filete(5 * u, tinta.copy(alpha = 0.45f), 1 * u)
            drawCircle(acento, 3.5f * u, Offset(16 * u, 16 * u))
            drawRect(tinta.copy(alpha = 0.6f), Offset(23 * u, 14 * u), Size(18 * u, 4 * u))
            var x = 10 * u; var i = 0
            while (x < w - 10 * u) {
                val l = if (i % 5 == 0) 6 * u else 3 * u
                drawLine(tinta.copy(alpha = 0.5f), Offset(x, h - 5 * u), Offset(x, h - 5 * u - l), 1 * u)
                x += 6 * u; i++
            }
        }
        // Una placa de acero con remaches.
        TemaId.REFUGIO -> {
            filete(3 * u, tinta.copy(alpha = 0.6f), 3 * u)
            val paso = 36 * u; val m = 9 * u; val remache = tinta.copy(alpha = 0.5f)
            var x = m
            while (x <= w - m) { drawCircle(remache, 2.2f * u, Offset(x, m)); drawCircle(remache, 2.2f * u, Offset(x, h - m)); x += paso }
            var y = m + paso
            while (y <= h - m - paso / 2) { drawCircle(remache, 2.2f * u, Offset(m, y)); drawCircle(remache, 2.2f * u, Offset(w - m, y)); y += paso }
        }
        // Filete de latón y tres galones en cada esquina.
        TemaId.ORDEN -> {
            filete(4 * u, acento.copy(alpha = 0.8f), 2.5f * u); filete(9 * u, tinta.copy(alpha = 0.3f), 1 * u)
            esquinas(4 * u) { o, dx, dy ->
                for (k in 0..2) { val a = (10 + 6 * k) * u; drawLine(acento, o + Offset(dx * a, dy * 3 * u), o + Offset(dx * 3 * u, dy * a), 2 * u) }
            }
        }
        // Reglas graduadas arriba y a la izquierda, como en una hoja milimétrica.
        TemaId.LABORATORIO -> {
            filete(3 * u, tinta.copy(alpha = 0.4f), 1 * u)
            val marca = acento.copy(alpha = 0.7f)
            var i = 0; var x = 3 * u
            while (x < w - 3 * u) { val l = if (i % 5 == 0) 8 * u else 4 * u; drawLine(marca, Offset(x, 3 * u), Offset(x, 3 * u + l), 0.8f * u); x += 6 * u; i++ }
            i = 0; var y = 3 * u
            while (y < h - 3 * u) { val l = if (i % 5 == 0) 8 * u else 4 * u; drawLine(marca, Offset(3 * u, y), Offset(3 * u + l, y), 0.8f * u); y += 6 * u; i++ }
        }
        // La raya gruesa y fina de la gaceta, con un rombo de imprenta arriba.
        TemaId.COLONOS -> {
            filete(4 * u, tinta.copy(alpha = 0.7f), 3 * u); filete(9 * u, tinta.copy(alpha = 0.5f), 0.8f * u)
            drawPath(Path().apply { moveTo(w / 2, 5.5f * u); lineTo(w / 2 + 5 * u, 9 * u); lineTo(w / 2, 12.5f * u); lineTo(w / 2 - 5 * u, 9 * u); close() }, tinta.copy(alpha = 0.7f))
        }
        // El borde de las cartas de estado mayor: tramos claros y oscuros alternados.
        TemaId.NAPOLEON -> {
            filete(4 * u, tinta.copy(alpha = 0.65f), 1 * u); filete(9 * u, tinta.copy(alpha = 0.65f), 1 * u)
            val tramo = 18 * u; val banda = tinta.copy(alpha = 0.55f)
            var x = 4 * u; var i = 0
            while (x < w - 4 * u) {
                if (i % 2 == 0) { val l = minOf(tramo, w - 4 * u - x); drawRect(banda, Offset(x, 4 * u), Size(l, 5 * u)); drawRect(banda, Offset(x, h - 9 * u), Size(l, 5 * u)) }
                x += tramo; i++
            }
            var y = 4 * u; i = 0
            while (y < h - 4 * u) {
                if (i % 2 == 1) { val l = minOf(tramo, h - 4 * u - y); drawRect(banda, Offset(4 * u, y), Size(5 * u, l)); drawRect(banda, Offset(w - 9 * u, y), Size(5 * u, l)) }
                y += tramo; i++
            }
        }
        // Tiras de papel engomado en las esquinas, como en un pliego de herbario.
        TemaId.BOTANICO -> {
            filete(6 * u, tinta.copy(alpha = 0.4f), 1 * u)
            esquinas(10 * u) { o, dx, dy ->
                val l = Offset(dx, -dy) * (9 * u); val a = Offset(dx, dy) * (3 * u)
                val p1 = o + l + a; val p2 = o + l - a; val p3 = o - l - a; val p4 = o - l + a
                drawPath(Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close() }, acento.copy(alpha = 0.25f))
            }
        }
        TemaId.RUINAS -> {
            // El filete doble de un plano de calles, con hiedra que entra por las esquinas.
            filete(6 * u, tinta.copy(alpha = 0.4f), 1 * u)
            filete(10 * u, tinta.copy(alpha = 0.18f), 0.6f * u)
            esquinas(10 * u) { o, dx, dy ->
                listOf(Offset(0f, 0f) to 7f, Offset(dx * 9 * u, dy * 3 * u) to 5f, Offset(dx * 3 * u, dy * 10 * u) to 5f).forEach { (d, t) ->
                    val q = o + d; val s = t * u
                    drawPath(Path().apply { moveTo(q.x, q.y + s * 0.5f); lineTo(q.x - s * 0.55f, q.y + s * 0.35f); lineTo(q.x - s * 0.8f, q.y - s * 0.05f); lineTo(q.x - s * 0.32f, q.y - s * 0.05f); lineTo(q.x, q.y - s * 0.5f); lineTo(q.x + s * 0.32f, q.y - s * 0.05f); lineTo(q.x + s * 0.8f, q.y - s * 0.05f); lineTo(q.x + s * 0.55f, q.y + s * 0.35f); close() }, acento.copy(alpha = 0.4f))
                }
            }
        }
        // Nieve que cuelga del borde de arriba y acebo en las esquinas de abajo.
        TemaId.NAVIDAD -> {
            filete(5 * u, acento.copy(alpha = 0.6f), 1.5f * u)
            var x = 0f; var i = 0
            while (x < w + 8 * u) {
                val rr = (7 + (i % 3) * 2) * u
                drawCircle(tinta.copy(alpha = 0.15f), rr, Offset(x, 1.2f * u)); drawCircle(Color.White.copy(alpha = 0.92f), rr, Offset(x, 0f))
                x += 12 * u; i++
            }
            val hoja = Color(0xFF2E6B3A)
            listOf(Offset(16 * u, h - 14 * u), Offset(w - 16 * u, h - 14 * u)).forEach { o ->
                drawOval(hoja, Offset(o.x - 9 * u, o.y - 2.5f * u), Size(9 * u, 5 * u)); drawOval(hoja, Offset(o.x, o.y - 2.5f * u), Size(9 * u, 5 * u))
                listOf(Offset(-2f, -3f), Offset(2f, -3f), Offset(0f, -6f)).forEach { k -> drawCircle(acento, 1.8f * u, o + k * u) }
            }
        }
        // Una greca de llave china en cada esquina.
        TemaId.SEDA -> {
            filete(5 * u, tinta.copy(alpha = 0.5f), 1 * u)
            val llave = listOf(0f to 0f, 16f to 0f, 16f to 16f, 4f to 16f, 4f to 6f, 11f to 6f, 11f to 11f)
            esquinas(8 * u) { o, dx, dy ->
                drawPath(Path().apply { llave.forEachIndexed { i, (a, b) -> val px = o.x + dx * a * u; val py = o.y + dy * b * u; if (i == 0) moveTo(px, py) else lineTo(px, py) } },
                    acento, style = Stroke(1.6f * u))
            }
        }
        // El marco de un rollo colgante: las bandas de brocado arriba y abajo (ichimonji) y un sellito en cada esquina.
        TemaId.EDO -> {
            filete(5 * u, tinta.copy(alpha = 0.5f), 1 * u); filete(8 * u, tinta.copy(alpha = 0.25f), 0.6f * u)
            drawRect(acento.copy(alpha = 0.25f), Offset(5 * u, 12 * u), Size(w - 10 * u, 4 * u)); drawRect(acento.copy(alpha = 0.25f), Offset(5 * u, h - 16 * u), Size(w - 10 * u, 4 * u))
            esquinas(8 * u) { o, dx, dy -> val s = 6 * u; drawRect(acento, Offset(if (dx > 0) o.x else o.x - s, if (dy > 0) o.y else o.y - s), Size(s, s)) }
        }
        // Una arquería de arcos apuntados arriba y un trébol en las esquinas de abajo.
        TemaId.GOTICO -> {
            filete(4 * u, tinta.copy(alpha = 0.5f), 1.2f * u)
            val ancho = 24 * u; var x = 4 * u
            val arcos = Path()
            while (x + ancho <= w - 4 * u + 0.5f) {
                arcos.moveTo(x, 20 * u); arcos.quadraticTo(x, 8 * u, x + ancho / 2, 5 * u); arcos.quadraticTo(x + ancho, 8 * u, x + ancho, 20 * u)
                x += ancho
            }
            drawPath(arcos, tinta.copy(alpha = 0.45f), style = Stroke(1.2f * u))
            listOf(Offset(14 * u, h - 14 * u), Offset(w - 14 * u, h - 14 * u)).forEach { o ->
                listOf(-90.0, 30.0, 150.0).forEach { g -> val a = Math.toRadians(g); drawCircle(acento, 2.6f * u, o + Offset(kotlin.math.cos(a).toFloat(), kotlin.math.sin(a).toFloat()) * (2.8f * u)) }
            }
        }
        // La carta de sondeos pegada con cinta: filete, tiras de cinta en las esquinas de arriba y la escala de millas abajo.
        TemaId.ARCANO -> {
            filete(4 * u, tinta.copy(alpha = 0.5f), 1.2f * u)
            listOf(Offset(4 * u, 4 * u) to 1f, Offset(w - 4 * u, 4 * u) to -1f).forEach { (o, s) ->
                drawPath(Path().apply { moveTo(o.x - s * 6 * u, o.y + 8 * u); lineTo(o.x + s * 8 * u, o.y - 6 * u); lineTo(o.x + s * 16 * u, o.y + 2 * u); lineTo(o.x + s * 2 * u, o.y + 16 * u); close() }, acento.copy(alpha = 0.22f))
            }
            val y0 = h - 14 * u; val seg = 12 * u
            for (k in 0 until 5) {
                val x0 = 14 * u + k * seg
                if (k % 2 == 0) drawRect(tinta.copy(alpha = 0.55f), Offset(x0, y0), Size(seg, 3 * u)) else drawRect(tinta.copy(alpha = 0.55f), Offset(x0, y0), Size(seg, 3 * u), style = Stroke(0.8f * u))
            }
        }
        // El visor de la sala de control: arcos en las esquinas y marcas de nivel a los lados.
        TemaId.ESPACIO -> {
            val rr = 18 * u; val m = 6 * u; val visor = Stroke(1.8f * u); val lado = Size(2 * rr, 2 * rr)
            drawArc(acento, 180f, 90f, false, Offset(m, m), lado, style = visor)
            drawArc(acento, 270f, 90f, false, Offset(w - m - 2 * rr, m), lado, style = visor)
            drawArc(acento, 0f, 90f, false, Offset(w - m - 2 * rr, h - m - 2 * rr), lado, style = visor)
            drawArc(acento, 90f, 90f, false, Offset(m, h - m - 2 * rr), lado, style = visor)
            for (k in -2..2) {
                val y = h / 2 + k * 6 * u; val l = if (k == 0) 10 * u else 5 * u
                drawLine(acento.copy(alpha = 0.7f), Offset(m, y), Offset(m + l, y), 1 * u); drawLine(acento.copy(alpha = 0.7f), Offset(w - m, y), Offset(w - m - l, y), 1 * u)
            }
        }
        // La superficie ondulada arriba, la regla de sondeo bajando por la izquierda y un remache en cada esquina.
        TemaId.ABISMO -> {
            val m = 6 * u
            val ola = Path(); var x = m
            ola.moveTo(m, m + 2 * u)
            while (x <= w - m) { ola.lineTo(x, m + 2 * u + 2 * u * kotlin.math.sin((x - m) / (8 * u))); x += 2 * u }
            drawPath(ola, acento, style = Stroke(1.4f * u))
            drawLine(tinta.copy(alpha = 0.5f), Offset(m, h - m), Offset(w - m, h - m), 1 * u)
            var y = m + 16 * u; var k = 0
            while (y < h - m - 8 * u) { drawLine(acento.copy(alpha = 0.7f), Offset(m, y), Offset(m + (if (k % 5 == 0) 10 else 4) * u, y), 1 * u); y += 7 * u; k++ }
            esquinas(m + 3 * u) { o, _, _ -> drawCircle(acento, 2 * u, o); drawCircle(fondo.copy(alpha = 0.6f), 0.8f * u, o) }
        }
        // Doble filete y una estrella de ocho puntas en cada esquina.
        TemaId.ANDALUS -> {
            filete(4 * u, tinta.copy(alpha = 0.5f), 1.2f * u); filete(8 * u, acento.copy(alpha = 0.45f), 0.8f * u)
            esquinas(15 * u) { o, _, _ -> drawPath(estrellaDeCuadros(o, 7 * u), acento); drawCircle(fondo, 2.2f * u, o) }
        }
        // El marco rojo del lienzo y, en cada esquina, un bloque de glifo con un seis maya.
        TemaId.MESO -> {
            filete(4 * u, acento.copy(alpha = 0.7f), 1.5f * u); filete(8 * u, tinta.copy(alpha = 0.35f), 0.7f * u)
            esquinas(15 * u) { o, _, _ ->
                drawRoundRect(fondo, Offset(o.x - 6 * u, o.y - 6 * u), Size(12 * u, 12 * u), androidx.compose.ui.geometry.CornerRadius(3 * u))
                drawRoundRect(acento, Offset(o.x - 6 * u, o.y - 6 * u), Size(12 * u, 12 * u), androidx.compose.ui.geometry.CornerRadius(3 * u), style = Stroke(1 * u))
                numeralMaya(6, o, 7 * u, tinta.copy(alpha = 0.7f))
            }
        }
        // Cuernos de carnero enroscados en las esquinas.
        TemaId.MONGOL -> {
            filete(4 * u, tinta.copy(alpha = 0.55f), 2 * u)
            esquinas(14 * u) { o, dx, dy ->
                val c1 = o + Offset(dx * 1.5f * u, dy * 1.5f * u)
                drawArc(acento, 0f, 270f, false, Offset(c1.x - 6 * u, c1.y - 6 * u), Size(12 * u, 12 * u), style = Stroke(1.6f * u))
                drawArc(acento, 90f, 270f, false, Offset(c1.x - 3 * u, c1.y - 3 * u), Size(6 * u, 6 * u), style = Stroke(1.6f * u))
            }
        }
        // Trazos de construcción: arcos de compás desde cada esquina y su diagonal.
        TemaId.RENACIMIENTO -> {
            filete(6 * u, tinta.copy(alpha = 0.45f), 0.8f * u)
            val m = 6 * u; val rr = 34 * u; val trazo = Stroke(0.8f * u); val sanguina = acento.copy(alpha = 0.6f); val lado = Size(2 * rr, 2 * rr)
            drawArc(sanguina, 0f, 90f, false, Offset(m - rr, m - rr), lado, style = trazo)
            drawArc(sanguina, 90f, 90f, false, Offset(w - m - rr, m - rr), lado, style = trazo)
            drawArc(sanguina, 180f, 90f, false, Offset(w - m - rr, h - m - rr), lado, style = trazo)
            drawArc(sanguina, 270f, 90f, false, Offset(m - rr, h - m - rr), lado, style = trazo)
            esquinas(m) { o, dx, dy -> drawLine(acento.copy(alpha = 0.5f), o, o + Offset(dx, dy) * (rr * 1.15f), 0.8f * u) }
        }
        // El borde de las cartas de marear: la escala de grados en franjas claras y oscuras entre dos filetes, un cabo que
        // corre por dentro y un nudo en cada esquina.
        TemaId.PIRATA -> {
            filete(4 * u, tinta.copy(alpha = 0.55f), 1 * u); filete(9 * u, tinta.copy(alpha = 0.45f), 0.8f * u)
            var x = 4 * u; var k = 0
            while (x < w - 4 * u) { if (k % 2 == 0) { drawRect(tinta.copy(alpha = 0.4f), Offset(x, 4 * u), Size(minOf(10 * u, w - 4 * u - x), 5 * u)); drawRect(tinta.copy(alpha = 0.4f), Offset(x, h - 9 * u), Size(minOf(10 * u, w - 4 * u - x), 5 * u)) }; x += 10 * u; k++ }
            var y = 4 * u; k = 0
            while (y < h - 4 * u) { if (k % 2 == 0) { drawRect(tinta.copy(alpha = 0.4f), Offset(4 * u, y), Size(5 * u, minOf(10 * u, h - 4 * u - y))); drawRect(tinta.copy(alpha = 0.4f), Offset(w - 9 * u, y), Size(5 * u, minOf(10 * u, h - 4 * u - y))) }; y += 10 * u; k++ }
            val cabo = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.2f * u))
            filete(12 * u, acento.copy(alpha = 0.55f), 1.6f * u)
            drawRect(fondo.copy(alpha = 0.6f), Offset(12 * u, 12 * u), Size(w - 24 * u, h - 24 * u), style = Stroke(0.6f * u, pathEffect = cabo))
            esquinas(12 * u) { o, _, _ -> drawCircle(acento.copy(alpha = 0.8f), 3 * u, o); drawCircle(tinta.copy(alpha = 0.6f), 3 * u, o, style = Stroke(0.7f * u)) }
        }
        // Constructivismo: una barra roja a la izquierda, una negra abajo y una cuña roja en la esquina.
        TemaId.URSS -> {
            drawRect(acento, Offset(0f, 0f), Size(7 * u, h))
            drawRect(tinta.copy(alpha = 0.85f), Offset(0f, h - 5 * u), Size(w, 5 * u))
            drawPath(Path().apply { moveTo(w, h); lineTo(w - 54 * u, h); lineTo(w, h - 54 * u); close() }, acento)
            drawLine(tinta.copy(alpha = 0.8f), Offset(7 * u, 12 * u), Offset(w * 0.4f, 12 * u), 2 * u)
        }
        // Franjas tricolores arriba y abajo, y dos escarapelas.
        TemaId.REVOLUCION -> {
            val azul = Color(0xFF1F3A93); val rojo = Color(0xFFC8102E); val tercio = w / 3
            listOf(0f to azul, tercio to Color.White, 2 * tercio to rojo).forEach { (x, col) ->
                drawRect(col, Offset(x, 0f), Size(tercio, 5 * u)); drawRect(col, Offset(x, h - 5 * u), Size(tercio, 5 * u))
            }
            filete(5 * u, tinta.copy(alpha = 0.4f), 1 * u)
            listOf(Offset(16 * u, h - 17 * u), Offset(w - 16 * u, h - 17 * u)).forEach { o -> drawCircle(azul, 6 * u, o); drawCircle(Color.White, 4 * u, o); drawCircle(rojo, 2 * u, o) }
        }
        // Una hilera de runas inventadas abajo y una gema en cada esquina.
        TemaId.ANTIGUAS -> {
            filete(5 * u, tinta.copy(alpha = 0.5f), 1.2f * u)
            val y0 = h - 16 * u; val alto = 8 * u; val runa = tinta.copy(alpha = 0.5f)
            var x = 16 * u; var i = 0
            while (x < w - 16 * u) {
                drawLine(runa, Offset(x, y0), Offset(x, y0 + alto), 1 * u)
                when (i % 4) {
                    0 -> drawLine(runa, Offset(x, y0), Offset(x + 4 * u, y0 + 3 * u), 1 * u)
                    1 -> { drawLine(runa, Offset(x, y0 + 2 * u), Offset(x + 4 * u, y0), 1 * u); drawLine(runa, Offset(x, y0 + 5 * u), Offset(x + 4 * u, y0 + 3 * u), 1 * u) }
                    2 -> drawLine(runa, Offset(x - 3 * u, y0 + 2 * u), Offset(x + 3 * u, y0 + 6 * u), 1 * u)
                    else -> { drawLine(runa, Offset(x, y0 + 4 * u), Offset(x + 4 * u, y0), 1 * u); drawLine(runa, Offset(x + 4 * u, y0), Offset(x + 4 * u, y0 + alto), 1 * u) }
                }
                x += 12 * u; i++
            }
            esquinas(5 * u) { o, _, _ -> drawPath(Path().apply { moveTo(o.x, o.y - 4 * u); lineTo(o.x + 3 * u, o.y); lineTo(o.x, o.y + 4 * u); lineTo(o.x - 3 * u, o.y); close() }, acento) }
        }
        TemaId.CUEVA -> {
            // El borde de la roca, una hilera de puntos de ocre abajo y una mano pequeña en cada esquina.
            filete(5 * u, tinta.copy(alpha = 0.4f), 1.4f * u)
            var x = 18 * u; var k = 0
            while (x < w - 18 * u) { drawCircle(acento.copy(alpha = 0.6f), (1.4f + (k % 3) * 0.4f) * u, Offset(x, h - 12 * u)); x += 10 * u; k++ }
            esquinas(14 * u) { o, _, _ -> drawPath(io.github.ruddy2003.vestigium.ui.manoUnida(o, 5 * u), acento.copy(alpha = 0.7f)) }
        }
    }
}

/**
 * La rosa de los vientos del tema, sobre su disco. `c` es el centro y `r` el radio. El norte va en el color de acento
 * (en la Ruta de la Seda, el sur: la brújula china señala al sur).
 */
internal fun DrawScope.rosaDelMapa(tema: TemaId, c: Offset, r: Float, tinta: Color, acento: Color, secundario: Color, fondo: Color) {
    val u = r / 24f
    drawCircle(fondo.copy(alpha = 0.82f), r * 1.05f, c)
    drawCircle(tinta.copy(alpha = 0.6f), r * 1.05f, c, style = Stroke(1.2f * u))
    fun rumbo(g: Double) = Offset(kotlin.math.cos(Math.toRadians(g)).toFloat(), kotlin.math.sin(Math.toRadians(g)).toFloat())
    // Una punta: dos medios triángulos, uno lleno y otro más tenue, como en las rosas de los portulanos.
    fun punta(g: Double, largo: Float, ancho: Float, color: Color) {
        val q = c + rumbo(g) * largo; val b1 = c + rumbo(g + 90.0) * ancho; val b2 = c + rumbo(g - 90.0) * ancho
        drawPath(Path().apply { moveTo(q.x, q.y); lineTo(b1.x, b1.y); lineTo(c.x, c.y); close() }, color)
        drawPath(Path().apply { moveTo(q.x, q.y); lineTo(b2.x, b2.y); lineTo(c.x, c.y); close() }, color.copy(alpha = color.alpha * 0.55f))
    }
    fun anillo(rr: Float, color: Color, grosor: Float = 1f * u) = drawCircle(color, rr, c, style = Stroke(grosor))
    val n = -90.0   // el norte
    val tenue = tinta.copy(alpha = 0.8f)
    when (tema) {
        // Los doce vientos de los romanos.
        TemaId.ROMANO -> {
            for (i in 0 until 12) punta(n + 30.0 * i, if (i % 3 == 0) r * 0.95f else r * 0.6f, r * 0.11f, if (i == 0) acento else tenue)
            drawCircle(secundario, r * 0.1f, c)
        }
        // La rosa de los vientos de mármol de la plaza de San Pedro: dieciséis vientos alrededor del obelisco.
        TemaId.URBE -> {
            anillo(r * 0.78f, tenue.copy(alpha = 0.4f))
            for (i in 0 until 16) punta(n + 22.5 * i, when { i % 4 == 0 -> r * 0.96f; i % 2 == 0 -> r * 0.7f; else -> r * 0.5f }, r * 0.08f, if (i == 0) acento else tenue)
            drawRect(secundario, Offset(c.x - r * 0.05f, c.y - r * 0.05f), Size(r * 0.1f, r * 0.1f))
        }
        // Una mira de consola con su flecha.
        TemaId.CIBER -> {
            drawRect(acento, Offset(c.x - r * 0.62f, c.y - r * 0.62f), Size(r * 1.24f, r * 1.24f), style = Stroke(1.2f * u))
            drawLine(acento.copy(alpha = 0.6f), c + Offset(-r * 0.9f, 0f), c + Offset(r * 0.9f, 0f), 1f * u)
            drawLine(acento, c + Offset(0f, r * 0.9f), c + Offset(0f, -r * 0.92f), 1.6f * u)
            drawPath(Path().apply { moveTo(c.x - r * 0.3f, c.y - r * 0.6f); lineTo(c.x, c.y - r * 0.92f); lineTo(c.x + r * 0.3f, c.y - r * 0.6f) }, acento, style = Stroke(2f * u))
            drawRect(acento, Offset(c.x - r * 0.1f, c.y - r * 0.1f), Size(r * 0.2f, r * 0.2f))
        }
        // Rosa de tubo: un aro de neón con su halo, la flecha del norte en magenta y los otros rumbos en ámbar.
        TemaId.NEON -> {
            anillo(r * 0.78f, acento.copy(alpha = 0.25f), 5f * u); anillo(r * 0.78f, acento, 1.4f * u)
            drawLine(acento, c + Offset(0f, r * 0.2f), c + Offset(0f, -r * 0.95f), 2f * u)
            drawPath(Path().apply { moveTo(c.x - r * 0.22f, c.y - r * 0.7f); lineTo(c.x, c.y - r * 0.95f); lineTo(c.x + r * 0.22f, c.y - r * 0.7f) }, acento, style = Stroke(2f * u))
            listOf(0.0, 90.0, 180.0).forEach { g -> drawLine(secundario, c + rumbo(g) * (r * 0.3f), c + rumbo(g) * (r * 0.62f), 1.4f * u) }
        }
        // El sol de Vergina: dieciséis rayos.
        TemaId.HELENO -> {
            for (i in 0 until 16) punta(n + 22.5 * i, if (i % 2 == 0) r * 0.95f else r * 0.62f, r * 0.07f, if (i == 0) acento else tinta.copy(alpha = 0.75f))
            drawCircle(acento, r * 0.22f, c); drawCircle(fondo, r * 0.1f, c)
        }
        // Ocho puntas y la del sur en color: los egipcios se orientaban mirando al sur, de donde baja el Nilo. Al centro, el sol.
        TemaId.EGIPTO -> {
            for (i in 0 until 8) punta(n + 45.0 * i, if (i % 2 == 0) r * 0.95f else r * 0.5f, r * 0.1f, if (i == 4) acento else tinta.copy(alpha = 0.75f))
            drawCircle(acento, r * 0.2f, c); drawCircle(fondo, r * 0.12f, c); drawCircle(acento, r * 0.05f, c)
        }
        // Ocho puntas y una cruz al oriente, como en las rosas de los manuscritos.
        TemaId.MEDIEVAL -> {
            for (i in 0 until 8) punta(n + 45.0 * i, when { i == 2 -> r * 0.5f; i % 2 == 0 -> r * 0.95f; else -> r * 0.55f }, r * 0.13f, if (i == 0) acento else tenue)
            drawLine(acento, c + Offset(r * 0.58f, 0f), c + Offset(r * 0.98f, 0f), 2 * u)
            drawLine(acento, c + Offset(r * 0.8f, -r * 0.2f), c + Offset(r * 0.8f, r * 0.2f), 2 * u)
            drawCircle(secundario, r * 0.1f, c)
        }
        // Cuatro puntas anchas como los brazos de la cruz patada, cuatro finas entre ellas y la cruz roja al centro.
        TemaId.TEMPLARIO -> {
            for (i in 0 until 4) punta(n + 45.0 + 90.0 * i, r * 0.5f, r * 0.07f, tenue)
            for (i in 0 until 4) punta(n + 90.0 * i, r * 0.95f, r * 0.22f, if (i == 0) acento else tinta.copy(alpha = 0.7f))
            drawCircle(fondo, r * 0.26f, c)
            drawPath(io.github.ruddy2003.vestigium.ui.cruzPatada(c, r * 0.22f), acento)
        }
        // El ojo, con la aguja del norte detrás.
        TemaId.DISTOPIA -> {
            punta(n, r * 0.98f, r * 0.1f, acento)
            val ojo = Path().apply { moveTo(c.x - r * 0.8f, c.y); quadraticTo(c.x, c.y - r * 0.75f, c.x + r * 0.8f, c.y); quadraticTo(c.x, c.y + r * 0.75f, c.x - r * 0.8f, c.y); close() }
            drawPath(ojo, fondo); drawPath(ojo, tenue, style = Stroke(1.4f * u))
            drawCircle(tinta.copy(alpha = 0.85f), r * 0.24f, c); drawCircle(acento, r * 0.1f, c)
        }
        // Un manómetro: medio círculo graduado y la aguja al norte.
        TemaId.REFUGIO -> {
            drawArc(tenue, 180f, 180f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(1.6f * u))
            for (k in 0..6) { val g = 180.0 + 30.0 * k; drawLine(tinta.copy(alpha = 0.7f), c + rumbo(g) * (r * 0.62f), c + rumbo(g) * (r * 0.8f), 1.2f * u) }
            drawRect(tinta.copy(alpha = 0.6f), Offset(c.x - r * 0.45f, c.y + r * 0.3f), Size(r * 0.9f, r * 0.22f))
            drawLine(acento, c, c + rumbo(n) * (r * 0.92f), 2.2f * u)
            drawCircle(acento, r * 0.14f, c)
        }
        // Una rueda dentada con la aguja.
        TemaId.ORDEN -> {
            for (i in 0 until 10) { val g = n + 36.0 * i; drawLine(tenue, c + rumbo(g) * (r * 0.62f), c + rumbo(g) * (r * 0.88f), r * 0.17f) }
            anillo(r * 0.62f, tenue, r * 0.16f)
            punta(n, r * 0.5f, r * 0.13f, acento); punta(90.0, r * 0.4f, r * 0.13f, tinta.copy(alpha = 0.6f))
        }
        // Una brújula de laboratorio: 36 marcas y la aguja imantada.
        TemaId.LABORATORIO -> {
            for (i in 0 until 36) { val g = n + 10.0 * i; val l = if (i % 9 == 0) r * 0.22f else r * 0.1f; drawLine(tinta.copy(alpha = 0.7f), c + rumbo(g) * (r * 0.95f - l), c + rumbo(g) * (r * 0.95f), 0.8f * u) }
            punta(n, r * 0.7f, r * 0.14f, acento); punta(90.0, r * 0.7f, r * 0.14f, tinta.copy(alpha = 0.6f))
            drawCircle(fondo, r * 0.07f, c)
        }
        // Una flecha con plumas que apunta al norte.
        TemaId.COLONOS -> {
            anillo(r * 0.72f, tinta.copy(alpha = 0.4f))
            drawLine(tinta.copy(alpha = 0.6f), c + Offset(-r * 0.85f, 0f), c + Offset(r * 0.85f, 0f), 1f * u)
            drawLine(tinta.copy(alpha = 0.85f), c + Offset(0f, r * 0.85f), c + Offset(0f, -r * 0.6f), 1.6f * u)
            drawPath(Path().apply { moveTo(c.x, c.y - r * 0.98f); lineTo(c.x - r * 0.22f, c.y - r * 0.55f); lineTo(c.x + r * 0.22f, c.y - r * 0.55f); close() }, acento)
            listOf(0.55f, 0.72f).forEach { k ->
                drawLine(tinta.copy(alpha = 0.7f), c + Offset(0f, r * k), c + Offset(-r * 0.2f, r * (k + 0.18f)), 1.2f * u)
                drawLine(tinta.copy(alpha = 0.7f), c + Offset(0f, r * k), c + Offset(r * 0.2f, r * (k + 0.18f)), 1.2f * u)
            }
        }
        // Ocho puntas con una corona de laurel abajo.
        TemaId.NAPOLEON -> {
            for (i in 0 until 8) punta(n + 45.0 * i, if (i % 2 == 0) r * 0.78f else r * 0.45f, r * 0.12f, if (i == 0) acento else tenue)
            for (k in 0..10) { val g = 30.0 + 12.0 * k; if (k != 5) drawCircle(secundario.copy(alpha = 0.85f), r * 0.07f, c + rumbo(g) * (r * 0.88f)) }
        }
        // Una flor de ocho pétalos.
        TemaId.BOTANICO -> {
            for (i in 0 until 8) {
                val g = n + 45.0 * i; val largo = if (i % 2 == 0) r * 0.95f else r * 0.6f
                val q = c + rumbo(g) * largo; val a = c + rumbo(g - 22.0) * (largo * 0.6f); val b = c + rumbo(g + 22.0) * (largo * 0.6f)
                drawPath(Path().apply { moveTo(c.x, c.y); quadraticTo(a.x, a.y, q.x, q.y); quadraticTo(b.x, b.y, c.x, c.y); close() }, if (i == 0) acento else tinta.copy(alpha = 0.55f))
            }
            drawCircle(secundario, r * 0.14f, c)
        }
        TemaId.RUINAS -> {
            // Una brújula oxidada: el aro con sus marcas, cuatro puntas finas (la del norte en el color del tema) y una mancha de óxido.
            drawCircle(tinta.copy(alpha = 0.45f), r * 0.8f, c, style = Stroke(r * 0.04f))
            for (i in 0 until 16) { val g = n + 22.5 * i; drawLine(tinta.copy(alpha = 0.5f), c + rumbo(g) * (r * 0.72f), c + rumbo(g) * (r * (if (i % 4 == 0) 0.6f else 0.68f)), r * 0.03f) }
            for (i in 0 until 4) {
                val g = n + 90.0 * i; val q = c + rumbo(g) * (r * 0.98f); val a = c + rumbo(g - 90.0) * (r * 0.1f); val b = c + rumbo(g + 90.0) * (r * 0.1f)
                drawPath(Path().apply { moveTo(a.x, a.y); lineTo(q.x, q.y); lineTo(b.x, b.y); close() }, if (i == 0) acento else tinta.copy(alpha = 0.6f))
            }
            drawCircle(secundario.copy(alpha = 0.6f), r * 0.16f, c + Offset(r * 0.35f, r * 0.42f))
            drawCircle(tinta, r * 0.08f, c)
        }
        // Un copo de nieve de seis brazos.
        TemaId.NAVIDAD -> {
            for (i in 0 until 6) {
                val g = n + 60.0 * i; val col = if (i == 0) acento else tenue; val grosor = if (i == 0) 2f * u else 1.4f * u
                drawLine(col, c, c + rumbo(g) * (r * 0.92f), grosor)
                val m = c + rumbo(g) * (r * 0.55f)
                drawLine(col, m, m + rumbo(g - 40.0) * (r * 0.25f), grosor); drawLine(col, m, m + rumbo(g + 40.0) * (r * 0.25f), grosor)
            }
            drawCircle(secundario, r * 0.1f, c)
        }
        // La brújula de geomante: anillos, 24 rumbos y la aguja que señala al sur.
        TemaId.SEDA -> {
            anillo(r * 0.85f, tinta.copy(alpha = 0.6f)); anillo(r * 0.6f, tinta.copy(alpha = 0.6f)); anillo(r * 0.35f, tinta.copy(alpha = 0.5f))
            for (i in 0 until 24) { val g = n + 15.0 * i; drawLine(tinta.copy(alpha = 0.6f), c + rumbo(g) * (r * 0.62f), c + rumbo(g) * (r * 0.83f), 0.8f * u) }
            punta(90.0, r * 0.58f, r * 0.12f, acento); punta(n, r * 0.4f, r * 0.12f, tinta.copy(alpha = 0.55f))
        }
        // La brújula japonesa de las doce direcciones (las del zodiaco: la rata al norte), con el norte sellado en bermellón.
        TemaId.EDO -> {
            anillo(r * 0.82f, tinta.copy(alpha = 0.6f)); anillo(r * 0.5f, tinta.copy(alpha = 0.4f))
            for (i in 0 until 12) { val g = n + 30.0 * i; drawCircle(tinta.copy(alpha = 0.6f), 1.3f * u, c + rumbo(g) * (r * 0.82f)); drawLine(tinta.copy(alpha = 0.4f), c + rumbo(g) * (r * 0.5f), c + rumbo(g) * (r * 0.68f), 0.7f * u) }
            punta(n, r * 0.72f, r * 0.13f, acento); punta(n + 180.0, r * 0.45f, r * 0.1f, tinta.copy(alpha = 0.5f))
            val q = c + rumbo(n) * (r * 0.96f); drawRect(acento, Offset(q.x - 2.6f * u, q.y - 2.6f * u), Size(5.2f * u, 5.2f * u))
        }
        // Un rosetón de ocho lóbulos con la marca del norte arriba.
        TemaId.GOTICO -> {
            for (i in 0 until 8) drawCircle(tinta.copy(alpha = 0.7f), r * 0.28f, c + rumbo(n + 45.0 * i) * (r * 0.58f), style = Stroke(1.2f * u))
            drawCircle(acento.copy(alpha = 0.75f), r * 0.26f, c); drawCircle(fondo, r * 0.1f, c)
            drawPath(Path().apply { moveTo(c.x, c.y - r * 1.0f); lineTo(c.x - r * 0.12f, c.y - r * 0.8f); lineTo(c.x + r * 0.12f, c.y - r * 0.8f); close() }, acento)
        }
        // La rosa de una carta de sondeos: dieciséis rumbos a tinta, el norte largo y, en el centro, el ojo.
        TemaId.ARCANO -> {
            for (i in 0 until 16) drawLine(tinta.copy(alpha = if (i % 4 == 0) 0.7f else 0.4f), c, c + rumbo(n + 22.5 * i) * (if (i % 4 == 0) r * 0.9f else r * 0.55f), (if (i % 4 == 0) 1.2f else 0.7f) * u)
            punta(n, r, r * 0.14f, acento)
            drawCircle(tinta.copy(alpha = 0.6f), r * 0.3f, c, style = Stroke(1.2f * u))
            drawPath(Path().apply { moveTo(c.x - r * 0.24f, c.y); quadraticTo(c.x, c.y - r * 0.2f, c.x + r * 0.24f, c.y); quadraticTo(c.x, c.y + r * 0.2f, c.x - r * 0.24f, c.y); close() }, fondo)
            drawOval(acento, Offset(c.x - r * 0.04f, c.y - r * 0.1f), Size(r * 0.08f, r * 0.2f))
        }
        // El indicador de rumbo de una cabina.
        TemaId.ESPACIO -> {
            anillo(r * 0.8f, acento.copy(alpha = 0.7f))
            for (i in 0 until 12) { val g = n + 30.0 * i; val l = if (i % 3 == 0) r * 0.18f else r * 0.08f; drawLine(acento.copy(alpha = 0.8f), c + rumbo(g) * (r * 0.8f), c + rumbo(g) * (r * 0.8f - l), 1f * u) }
            listOf(0.0, 90.0, 180.0, 270.0).forEach { g -> drawLine(tinta.copy(alpha = 0.7f), c + rumbo(g) * (r * 0.15f), c + rumbo(g) * (r * 0.45f), 1f * u) }
            drawPath(Path().apply { moveTo(c.x, c.y - r * 0.82f); lineTo(c.x - r * 0.16f, c.y - r * 1.02f); lineTo(c.x + r * 0.16f, c.y - r * 1.02f); close() }, acento)
            drawCircle(acento, r * 0.05f, c)
        }
        // La pantalla del sonar hecha rosa: tres anillos de distancia, el haz que barre hacia el noreste con su estela y
        // la punta del norte.
        TemaId.ABISMO -> {
            listOf(0.85f, 0.6f, 0.35f).forEach { k -> anillo(r * k, acento.copy(alpha = 0.5f)) }
            for (i in 0 until 10) drawLine(acento.copy(alpha = 0.5f * (1f - i / 10f)), c, c + rumbo(n + 45.0 - i * 4.0) * (r * 0.85f), 1.6f * u)
            listOf(0.0, 90.0, 180.0, 270.0).forEach { g -> drawLine(tinta.copy(alpha = 0.6f), c + rumbo(n + g) * (r * 0.88f), c + rumbo(n + g) * (r * 1.0f), 1.4f * u) }
            listOf(0.7f to 30.0, 0.45f to 140.0).forEach { (k, g) -> drawCircle(secundario, r * 0.06f, c + rumbo(n + g) * (r * k)) }
            punta(n, r * 1.0f, r * 0.12f, acento)
            drawCircle(acento, r * 0.06f, c)
        }
        // Ocho puntas sobre la estrella de dos cuadrados.
        TemaId.ANDALUS -> {
            drawPath(estrellaDeCuadros(c, r * 0.8f), tinta.copy(alpha = 0.75f))
            drawCircle(fondo, r * 0.42f, c)
            for (i in 0 until 8) punta(n + 45.0 * i, if (i % 2 == 0) r * 0.95f else r * 0.6f, r * 0.08f, if (i == 0) acento else tinta.copy(alpha = 0.55f))
            drawCircle(secundario, r * 0.1f, c)
        }
        // Los cuatro rumbos con sus colores: blanco al norte, rojo al oriente, amarillo al sur y negro al poniente; la ceiba
        // verde en el centro. Entre ellos, los rumbos intermedios, cortos.
        TemaId.MESO -> {
            for (i in 0 until 4) punta(n + 45.0 + 90.0 * i, r * 0.5f, r * 0.08f, tinta.copy(alpha = 0.45f))
            listOf(Color(0xFFF2E8D2), Color(0xFFA3301E), Color(0xFFD9A63A), Color(0xFF2A1810)).forEachIndexed { i, col -> punta(n + 90.0 * i, r * 0.95f, r * 0.16f, col) }
            punta(n, r * 0.95f, r * 0.16f, acento.copy(alpha = 0.35f))
            drawCircle(Color(0xFF2F7D57), r * 0.15f, c)
        }
        // Cuatro puntas con la corona de la yurta en medio.
        TemaId.MONGOL -> {
            for (i in 0 until 8) punta(n + 45.0 * i, if (i % 2 == 0) r * 0.95f else r * 0.5f, r * 0.13f, if (i == 0) acento else tenue)
            drawCircle(fondo, r * 0.4f, c); anillo(r * 0.4f, tenue, 1.6f * u)
            for (i in 0 until 8) { val g = n + 22.5 + 45.0 * i; drawLine(tenue, c + rumbo(g) * (r * 0.12f), c + rumbo(g) * (r * 0.4f), 1f * u) }
            drawCircle(secundario, r * 0.12f, c)
        }
        // La rosa de un portulano: treinta y dos rumbos y ocho puntas.
        TemaId.RENACIMIENTO -> {
            for (i in 0 until 32) drawLine(tinta.copy(alpha = 0.3f), c, c + rumbo(n + 11.25 * i) * r, 0.6f * u)
            for (i in 0 until 8) punta(n + 45.0 * i, if (i % 2 == 0) r * 0.9f else r * 0.55f, r * 0.1f, if (i == 0) acento else tinta.copy(alpha = 0.75f))
            anillo(r * 0.3f, tinta.copy(alpha = 0.6f), 0.8f * u)
        }
        // La rosa de las cartas de marear: dieciséis puntas (las cardinales largas), el anillo graduado en treinta y dos
        // cuartas, la flor de lis roja al norte y la cruz al este.
        TemaId.PIRATA -> {
            anillo(r * 0.98f, tinta.copy(alpha = 0.5f), 0.8f * u)
            for (i in 0 until 32) drawLine(tinta.copy(alpha = 0.55f), c + rumbo(n + 11.25 * i) * (r * 0.9f), c + rumbo(n + 11.25 * i) * (r * if (i % 4 == 0) 0.98f else 0.94f), 0.7f * u)
            for (i in 0 until 8) punta(n + 22.5 + 45.0 * i, r * 0.42f, r * 0.07f, secundario.copy(alpha = 0.8f))
            for (i in 0 until 4) punta(n + 45.0 + 90.0 * i, r * 0.6f, r * 0.1f, tenue)
            for (i in 0 until 4) punta(n + 90.0 * i, r * 0.86f, r * 0.13f, if (i == 0) acento else tenue)
            val lis = c + rumbo(n) * (r * 0.9f)
            drawCircle(acento, r * 0.07f, lis); drawCircle(acento, r * 0.05f, lis + Offset(-r * 0.08f, r * 0.04f)); drawCircle(acento, r * 0.05f, lis + Offset(r * 0.08f, r * 0.04f))
            val este = c + rumbo(0.0) * (r * 0.92f)
            drawLine(tinta, este - Offset(0f, r * 0.07f), este + Offset(0f, r * 0.07f), 1 * u); drawLine(tinta, este - Offset(r * 0.05f, 0f), este + Offset(r * 0.05f, 0f), 1 * u)
            drawCircle(fondo, r * 0.08f, c); drawCircle(tinta, r * 0.08f, c, style = Stroke(0.8f * u))
        }
        // Una cuña al norte y la estrella roja en medio.
        TemaId.URSS -> {
            punta(n, r * 0.98f, r * 0.16f, acento); punta(90.0, r * 0.6f, r * 0.12f, tinta.copy(alpha = 0.5f))
            drawPath(Path().apply {
                for (i in 0 until 10) { val q = c + rumbo(n + 36.0 * i) * (if (i % 2 == 0) r * 0.45f else r * 0.18f); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
                close()
            }, acento)
        }
        // Cuatro puntas sobre la escarapela.
        TemaId.REVOLUCION -> {
            for (i in 0 until 4) punta(n + 90.0 * i, r * 0.95f, r * 0.1f, if (i == 0) acento else tinta.copy(alpha = 0.75f))
            drawCircle(Color(0xFF1F3A93), r * 0.5f, c); drawCircle(Color.White, r * 0.34f, c); drawCircle(Color(0xFFC8102E), r * 0.18f, c)
        }
        // Cuatro puntas, una gema en medio y otras cuatro entre los rumbos.
        TemaId.ANTIGUAS -> {
            for (i in 0 until 4) punta(n + 90.0 * i, r * 0.95f, r * 0.13f, if (i == 0) acento else tenue)
            for (i in 0 until 4) {
                val q = c + rumbo(-45.0 + 90.0 * i) * (r * 0.68f)
                drawPath(Path().apply { moveTo(q.x, q.y - r * 0.12f); lineTo(q.x + r * 0.08f, q.y); lineTo(q.x, q.y + r * 0.12f); lineTo(q.x - r * 0.08f, q.y); close() }, secundario)
            }
            drawPath(Path().apply { moveTo(c.x, c.y - r * 0.2f); lineTo(c.x + r * 0.14f, c.y); lineTo(c.x, c.y + r * 0.2f); lineTo(c.x - r * 0.14f, c.y); close() }, secundario)
        }
        TemaId.CUEVA -> {
            // Una rosa grabada en la roca: el anillo con su punto, rayos cortos y largos, y el norte con una punta de azagaya.
            anillo(r * 0.42f, tenue, 1.4f * u); drawCircle(acento, r * 0.12f, c)
            for (i in 0 until 12) {
                val q0 = c + rumbo(n + 30.0 * i) * (r * 0.55f); val q1 = c + rumbo(n + 30.0 * i) * (r * (if (i % 3 == 0) 0.95f else 0.72f))
                drawLine(if (i == 0) acento else tenue, q0, q1, (if (i % 3 == 0) 2f else 1.2f) * u, androidx.compose.ui.graphics.StrokeCap.Round)
            }
            punta(n, r * 1.0f, r * 0.12f, acento)
        }
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

