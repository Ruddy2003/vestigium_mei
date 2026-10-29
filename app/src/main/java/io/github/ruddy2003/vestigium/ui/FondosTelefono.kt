package io.github.ruddy2003.vestigium.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Fondos del teléfono hechos con el tema: el mismo papel, el emblema y los adornos de la app.
 * Fijo (se pone con un toque en inicio, bloqueo o ambas) o vivo (estrellas que titilan, luciérnagas, nieve… y cambia solo con el tema).
 */
object FondosTelefono {
    private const val VERSION = 3

    /** [variante]: «bloqueo» (con emblema, nombre y marco) o «inicio» (limpio, para que no compita con las apps). */
    fun archivo(ctx: Context, tema: TemaId, oscuro: Boolean, variante: String = "inicio"): File =
        File(File(ctx.filesDir, "fondos").apply { mkdirs() }, "fondo_v${VERSION}_${tema.id}_${if (oscuro) "o" else "c"}_$variante.png")

    fun guardar(ctx: Context, bmp: Bitmap, tema: TemaId, oscuro: Boolean, variante: String) {
        val f = archivo(ctx, tema, oscuro, variante)
        val tmp = File(f.parentFile, f.name + ".tmp")
        tmp.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        tmp.renameTo(f)
    }

    /** Pone los fondos: el limpio en inicio y el completo en bloqueo. [donde]: «inicio», «bloqueo» o «ambas». */
    fun poner(ctx: Context, inicio: Bitmap?, bloqueo: Bitmap?, donde: String): Boolean = runCatching {
        val wm = WallpaperManager.getInstance(ctx)
        if (donde != "bloqueo" && inicio != null) ponerUno(ctx, wm, inicio, WallpaperManager.FLAG_SYSTEM)
        if (donde != "inicio" && bloqueo != null) ponerUno(ctx, wm, bloqueo, WallpaperManager.FLAG_LOCK)
        true
    }.getOrDefault(false)

    /** Tamaño real de la pantalla (con las barras del sistema), en píxeles. */
    private fun pantalla(ctx: Context): android.graphics.Point {
        val wmg = ctx.getSystemService(android.view.WindowManager::class.java)
        val r = wmg.maximumWindowMetrics.bounds
        return android.graphics.Point(r.width(), r.height())
    }

    /**
     * La imagen, exactamente del tamaño de la pantalla, y diciéndole al teléfono que se vea entera. Sin esto, algunos
     * teléfonos (los Pixel con Android 15 o más) recortan a su manera para dejar espacio al desplazamiento de la pantalla
     * de inicio, y el fondo de bloqueo queda corrido hacia un lado.
     */
    private fun ponerUno(ctx: Context, wm: WallpaperManager, b: Bitmap, cual: Int) {
        val p = pantalla(ctx)
        val img = ajustar(b, p.x, p.y)
        val todo = android.graphics.Rect(0, 0, img.width, img.height)
        // Android 15+: un recorte explícito por tamaño de pantalla (si el sistema lo ofrece); si no, la pista de recorte de siempre.
        val hecho = android.os.Build.VERSION.SDK_INT >= 35 && runCatching {
            val m = WallpaperManager::class.java.getMethod("setBitmapWithCrops", Bitmap::class.java, Map::class.java, Boolean::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            m.invoke(wm, img, mapOf(android.graphics.Point(img.width, img.height) to todo), true, cual)
            true
        }.getOrDefault(false)
        if (!hecho) wm.setBitmap(img, todo, true, cual)
    }

    /** Escala y recorta al centro para llenar justo [w]×[h] (como «cubrir»). */
    private fun ajustar(b: Bitmap, w: Int, h: Int): Bitmap {
        if (w <= 0 || h <= 0 || (b.width == w && b.height == h)) return b
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(out)
        val s = maxOf(w.toFloat() / b.width, h.toFloat() / b.height)
        val dw = b.width * s; val dh = b.height * s
        c.drawBitmap(b, null, android.graphics.RectF((w - dw) / 2, (h - dh) / 2, (w + dw) / 2, (h + dh) / 2), Paint(Paint.FILTER_BITMAP_FLAG))
        return out
    }

    fun vivoActivo(ctx: Context): Boolean = runCatching { WallpaperManager.getInstance(ctx).wallpaperInfo?.packageName == ctx.packageName }.getOrDefault(false)

    /** Abre la pantalla del sistema para elegir el fondo vivo de Vestigium. */
    fun elegirVivo(ctx: Context) {
        val i = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(ctx, FondoVivoServicio::class.java))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { ctx.startActivity(i) }.onFailure {
            runCatching { ctx.startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }
    }

    /** Qué tema se aplicó la última vez como fondo fijo (para cambiarlo solo al cambiar de tema). */
    private fun prefs(ctx: Context) = ctx.getSharedPreferences("fondos", Context.MODE_PRIVATE)
    fun ultimo(ctx: Context): String? = prefs(ctx).getString("ultimo", null)
    fun marcarUltimo(ctx: Context, clave: String) { prefs(ctx).edit().putString("ultimo", clave).apply() }

    /** Lo que lleva escrito el fondo bajo el emblema: el nombre de la app en la voz de cada tema. */
    fun marca(t: TemaId): String = when (t) {
        TemaId.ROMANO -> "VESTIGIVM · MEI"
        TemaId.CIBER -> "vestigium.mei"
        TemaId.HELENO -> "ΙΧΝΟΣ ΕΜΟΝ"
        TemaId.MEDIEVAL -> "Liber vestigii mei"
        TemaId.DISTOPIA -> "EXPEDIENTE PERSONAL"
        TemaId.REFUGIO -> "VESTIGIUM-MEI OS"
        TemaId.ORDEN -> "ORDO · VESTIGII"
        TemaId.LABORATORIO -> "CUADERNO DE LABORATORIO VM-01"
        TemaId.COLONOS -> "LA GACETA DEL VESTIGIO"
        TemaId.NAPOLEON -> "Bulletin du Vestige"
        TemaId.BOTANICO -> "Herbarium vestigii"
        TemaId.NAVIDAD -> "Cartas de invierno"
        TemaId.SEDA -> "絲路 · VESTIGIUM"
        TemaId.GOTICO -> "Horae vestigii"
        TemaId.ESPACIO -> "BITÁCORA DE A BORDO · VM-1"
        TemaId.ANDALUS -> "أثري"
        TemaId.MONGOL -> "МИНИЙ МӨР"
        TemaId.RENACIMIENTO -> "Il mio vestigio"
        TemaId.URSS -> "МОЙ СЛЕД"
        TemaId.REVOLUCION -> "VESTIGIUM · MEI · AN I"
        TemaId.ANTIGUAS -> "VESTIGIUM · MEI · CRÓNICA"
    }
}

/**
 * El fondo tal como queda en el teléfono. «bloqueo»: papel o pantalla del tema, emblema grande, nombre, adorno y marco.
 * «inicio»: solo el papel, lo que se mueve y el emblema muy tenue en grande, para que las apps y los widgets se lean bien encima.
 */
@Composable
fun FondoTelefonoVista(tema: TemaId, oscuro: Boolean, modifier: Modifier = Modifier, variante: String = "bloqueo") {
    val o = oscuro || tema.soloOscuro
    val e = esquemaDe(tema, o)
    val limpio = variante == "inicio"
    MaterialTheme(colorScheme = e, typography = tipoDe(tema, o)) {
        Box(modifier.background(e.background).fondoTema(tema, e)) {
            // Lo que se mueve en la app, aquí quieto y un poco más visible (se dibuja dos veces en el de bloqueo).
            FondoVivo(tema, fijo = 0.37f)
            if (!limpio) { FondoVivo(tema, fijo = 0.37f); MarcoFondo(tema, e) }
            // Una luz suave al centro y las orillas un poco más oscuras.
            Canvas(Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2, size.height * 0.42f)
                if (!limpio) drawRect(Brush.radialGradient(listOf(e.primary.copy(alpha = if (o) 0.10f else 0.06f), Color.Transparent), c, size.width * 0.7f))
                drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (o) 0.35f else 0.10f)), Offset(size.width / 2, size.height / 2), size.height * 0.75f))
            }
            if (limpio) {
                // El emblema como marca de agua: grande, apenas visible, abajo del centro donde suelen quedar menos íconos.
                Box(Modifier.align(BiasAlignment(0f, 0.28f)).graphicsLayer { alpha = if (o) 0.10f else 0.08f }) { Emblema(tema, 300.dp, e, animado = false) }
            } else Column(Modifier.align(BiasAlignment(0f, -0.18f)), horizontalAlignment = Alignment.CenterHorizontally) {
                Emblema(tema, 176.dp, e, animado = false)
                Text(FondosTelefono.marca(tema), style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 3.sp), color = e.primary, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 22.dp))
                Meandro(Modifier.width(210.dp).padding(top = 10.dp))
            }
        }
    }
}

/** Una estrella pequeña de [puntas] puntas, para adornar los marcos. */
private fun DrawScope.estrellaMarco(c: Offset, r: Float, puntas: Int, color: Color) {
    drawPath(Path().apply {
        for (i in 0 until puntas * 2) {
            val rr = if (i % 2 == 0) r else r * 0.42f
            val a = -PI / 2 + PI * i / puntas
            val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }, color)
}

/** El marco del fondo de bloqueo, propio de cada tema: del doble filete con rombos del romano a la consola, el arco o la gaceta. */
@Composable
private fun MarcoFondo(tema: TemaId, e: androidx.compose.material3.ColorScheme) {
    Canvas(Modifier.fillMaxSize()) {
        val u = 1.dp.toPx(); val m = 22 * u
        val izq = m; val der = size.width - m; val arr = m + 26 * u; val aba = size.height - m - 10 * u
        val cx = (izq + der) / 2; val cy = (arr + aba) / 2
        val col = e.primary.copy(alpha = 0.42f)
        val tenue = e.primary.copy(alpha = 0.24f)
        val claro = e.background.luminance() > 0.5f
        val esquinas = listOf(Offset(izq, arr), Offset(der, arr), Offset(izq, aba), Offset(der, aba))
        fun caja(dentro: Float, c: Color, grosor: Float) =
            drawRect(c, Offset(izq + dentro, arr + dentro), Size(der - izq - 2 * dentro, aba - arr - 2 * dentro), style = Stroke(grosor))
        fun escuadras(l: Float, g: Float, c: Color) =
            listOf(Offset(izq, arr) to Offset(1f, 1f), Offset(der, arr) to Offset(-1f, 1f), Offset(izq, aba) to Offset(1f, -1f), Offset(der, aba) to Offset(-1f, -1f)).forEach { (o, d) ->
                drawLine(c, o, o + Offset(d.x * l, 0f), g); drawLine(c, o, o + Offset(0f, d.y * l), g)
            }
        fun haciaDentro(c: Offset, d: Float) = Offset(if (c.x < size.width / 2) c.x + d else c.x - d, if (c.y < size.height / 2) c.y + d else c.y - d)
        when (tema) {
            TemaId.ROMANO -> {
                // Doble filete con rombos en las esquinas, como una lápida.
                caja(0f, col, 1.2f * u); caja(6 * u, tenue, 0.7f * u)
                esquinas.forEach { c ->
                    val k = 7 * u
                    drawPath(Path().apply { moveTo(c.x - k, c.y); lineTo(c.x, c.y - k); lineTo(c.x + k, c.y); lineTo(c.x, c.y + k); close() }, e.primary.copy(alpha = 0.6f))
                }
            }
            TemaId.CIBER -> {
                // Consola: borde punteado, escuadras de luz y un indicador con su cursor abajo.
                drawRect(e.primary.copy(alpha = 0.22f), Offset(izq, arr), Size(der - izq, aba - arr), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * u, 6 * u))))
                escuadras(34 * u, 2 * u, e.primary.copy(alpha = 0.6f))
                val py = aba - 16 * u
                drawLine(col, Offset(izq + 12 * u, py - 5 * u), Offset(izq + 18 * u, py), 1.6f * u); drawLine(col, Offset(izq + 18 * u, py), Offset(izq + 12 * u, py + 5 * u), 1.6f * u)
                drawRect(col, Offset(izq + 24 * u, py - 6 * u), Size(7 * u, 12 * u))
            }
            TemaId.HELENO -> {
                // Un filete con una greca arriba y otra abajo.
                caja(0f, col, 1.2f * u)
                fun greca(y: Float) {
                    val q = 6 * u; var x = izq + 12 * u
                    val ruta = Path(); ruta.moveTo(x, y + 2 * q)
                    while (x + 3 * q <= der - 12 * u) {
                        ruta.lineTo(x, y); ruta.lineTo(x + 2 * q, y); ruta.lineTo(x + 2 * q, y + q); ruta.lineTo(x + q, y + q); ruta.lineTo(x + q, y + 2 * q); ruta.lineTo(x + 3 * q, y + 2 * q)
                        x += 3 * q
                    }
                    drawPath(ruta, tenue, style = Stroke(1.1f * u))
                }
                greca(arr + 8 * u); greca(aba - 20 * u)
            }
            TemaId.MEDIEVAL -> {
                // Filete doble de minio y oro, con cuadros iluminados en las esquinas y una flor de cuatro granos.
                caja(0f, col, 1.4f * u); caja(5 * u, e.secondary.copy(alpha = 0.35f), 0.8f * u)
                esquinas.forEach { c ->
                    val q = 18 * u
                    drawRect(e.primary.copy(alpha = 0.18f), Offset(c.x - q / 2, c.y - q / 2), Size(q, q))
                    drawRect(col, Offset(c.x - q / 2, c.y - q / 2), Size(q, q), style = Stroke(1f * u))
                    for (i in 0 until 4) {
                        val a = PI / 2 * i
                        drawCircle(e.tertiary.copy(alpha = 0.55f), 2.6f * u, c + Offset((3.2f * u * cos(a)).toFloat(), (3.2f * u * sin(a)).toFloat()))
                    }
                }
            }
            TemaId.DISTOPIA -> {
                // Carpeta de expediente: línea gruesa, dos perforaciones, una franja de «clasificado» y marcas de corte.
                caja(0f, col, 2f * u)
                listOf(cy - 40 * u, cy + 40 * u).forEach { y -> drawCircle(tenue, 5 * u, Offset(izq + 16 * u, y), style = Stroke(1.2f * u)) }
                clipRect(izq, arr, der, aba) { drawLine(e.primary.copy(alpha = 0.3f), Offset(der - 46 * u, arr), Offset(der, arr + 46 * u), 8 * u) }
                esquinas.forEach { c ->
                    val sx = if (c.x < size.width / 2) -1f else 1f; val sy = if (c.y < size.height / 2) -1f else 1f
                    drawLine(tenue, c + Offset(sx * 4 * u, 0f), c + Offset(sx * 14 * u, 0f), 1f * u)
                    drawLine(tenue, c + Offset(0f, sy * 4 * u), c + Offset(0f, sy * 14 * u), 1f * u)
                }
            }
            TemaId.REFUGIO -> {
                // Panel de compuerta: marco grueso, placas en las esquinas y remaches a lo largo.
                caja(0f, col, 2.2f * u)
                esquinas.forEach { c -> val q = 16 * u; drawRect(tenue, Offset(c.x - q / 2, c.y - q / 2), Size(q, q)); drawCircle(col, 1.8f * u, c) }
                var x = izq + 30 * u
                while (x < der - 20 * u) { drawCircle(tenue, 1.6f * u, Offset(x, arr + 7 * u)); drawCircle(tenue, 1.6f * u, Offset(x, aba - 7 * u)); x += 28 * u }
                var y = arr + 30 * u
                while (y < aba - 20 * u) { drawCircle(tenue, 1.6f * u, Offset(izq + 7 * u, y)); drawCircle(tenue, 1.6f * u, Offset(der - 7 * u, y)); y += 28 * u }
            }
            TemaId.ORDEN -> {
                // Placa de acero de esquinas achaflanadas, doble, con medallones de latón al centro de cada lado.
                val k = 22 * u
                fun chaflan(d: Float, c: Color, g: Float) = drawPath(Path().apply {
                    moveTo(izq + d + k, arr + d); lineTo(der - d - k, arr + d); lineTo(der - d, arr + d + k); lineTo(der - d, aba - d - k)
                    lineTo(der - d - k, aba - d); lineTo(izq + d + k, aba - d); lineTo(izq + d, aba - d - k); lineTo(izq + d, arr + d + k); close()
                }, c, style = Stroke(g))
                chaflan(0f, col, 2.4f * u); chaflan(7 * u, tenue, 1f * u)
                listOf(Offset(cx, arr), Offset(cx, aba), Offset(izq, cy), Offset(der, cy)).forEach { c -> drawCircle(e.secondary.copy(alpha = 0.5f), 4 * u, c) }
            }
            TemaId.LABORATORIO -> {
                // Esquinas redondeadas y una escala graduada a la izquierda, como una probeta.
                drawRoundRect(col, Offset(izq, arr), Size(der - izq, aba - arr), CornerRadius(18 * u), style = Stroke(1.2f * u))
                var y = aba - 24 * u; var i = 0
                while (y > arr + (aba - arr) * 0.45f) {
                    val l = if (i % 5 == 0) 14 * u else 7 * u
                    drawLine(tenue, Offset(izq + 8 * u, y), Offset(izq + 8 * u + l, y), 1f * u)
                    y -= 10 * u; i++
                }
            }
            TemaId.COLONOS -> {
                // Filete de gaceta (grueso y fino) con la regla doble bajo la cabecera.
                caja(0f, col, 3f * u); caja(6 * u, tenue, 0.8f * u)
                val y = arr + 34 * u
                drawLine(col, Offset(izq + 6 * u, y), Offset(der - 6 * u, y), 2f * u)
                drawLine(tenue, Offset(izq + 6 * u, y + 4 * u), Offset(der - 6 * u, y + 4 * u), 0.8f * u)
            }
            TemaId.NAPOLEON -> {
                // Esquinas cóncavas, filete de oro por dentro y una estrella arriba y abajo.
                val k = 16 * u
                fun concavo(d: Float, c: Color, g: Float) = drawPath(Path().apply {
                    moveTo(izq + d + k, arr + d); lineTo(der - d - k, arr + d); quadraticTo(der - d - k, arr + d + k, der - d, arr + d + k)
                    lineTo(der - d, aba - d - k); quadraticTo(der - d - k, aba - d - k, der - d - k, aba - d)
                    lineTo(izq + d + k, aba - d); quadraticTo(izq + d + k, aba - d - k, izq + d, aba - d - k)
                    lineTo(izq + d, arr + d + k); quadraticTo(izq + d + k, arr + d + k, izq + d + k, arr + d); close()
                }, c, style = Stroke(g))
                concavo(0f, col, 1.4f * u); concavo(6 * u, e.tertiary.copy(alpha = 0.35f), 0.8f * u)
                listOf(Offset(cx, arr), Offset(cx, aba)).forEach { c -> estrellaMarco(c, 6 * u, 5, e.tertiary.copy(alpha = 0.6f)) }
            }
            TemaId.BOTANICO -> {
                // Pliego de herbario: filete fino, alfileres arriba y la etiqueta del ejemplar abajo a la derecha.
                caja(0f, tenue, 0.9f * u)
                val an = 96 * u; val al = 50 * u; val o = Offset(der - an - 12 * u, aba - al - 12 * u)
                drawRect(col, o, Size(an, al), style = Stroke(1f * u))
                for (i in 1..3) drawLine(tenue, Offset(o.x + 8 * u, o.y + al * i / 4f), Offset(o.x + an - 8 * u, o.y + al * i / 4f), 0.7f * u)
                esquinas.take(2).forEach { c -> val q = haciaDentro(c, 18 * u); drawCircle(col, 2.4f * u, q); drawLine(tenue, q, q + Offset(5 * u, 7 * u), 0.8f * u) }
            }
            TemaId.NAVIDAD -> {
                // Una guirnalda de luces de colores a lo largo del borde.
                caja(0f, tenue, 0.8f * u)
                val luces = listOf(Color(0xFFD94A4A), Color(0xFF3E9A5B), Color(0xFFE8C25A))
                var i = 0
                fun luz(p: Offset) {
                    val c = luces[i % 3]
                    drawCircle(Brush.radialGradient(listOf(c.copy(alpha = 0.35f), Color.Transparent), p, 7 * u), 7 * u, p)
                    drawCircle(c.copy(alpha = 0.8f), 2.2f * u, p); i++
                }
                var x = izq
                while (x <= der) { luz(Offset(x, arr)); luz(Offset(x, aba)); x += 26 * u }
                var y = arr + 26 * u
                while (y < aba) { luz(Offset(izq, y)); luz(Offset(der, y)); y += 26 * u }
            }
            TemaId.SEDA -> {
                // Esquinas de celosía con el meandro en escuadra y una moneda de agujero cuadrado arriba y abajo.
                caja(0f, col, 1.2f * u)
                esquinas.forEach { c ->
                    val sx = if (c.x < size.width / 2) 1f else -1f; val sy = if (c.y < size.height / 2) 1f else -1f; val q = 6 * u
                    drawPath(Path().apply {
                        moveTo(c.x + sx * 5 * q, c.y + sy * q); lineTo(c.x + sx * q, c.y + sy * q); lineTo(c.x + sx * q, c.y + sy * 5 * q)
                        lineTo(c.x + sx * 3 * q, c.y + sy * 5 * q); lineTo(c.x + sx * 3 * q, c.y + sy * 3 * q); lineTo(c.x + sx * 2 * q, c.y + sy * 3 * q)
                    }, col, style = Stroke(1.6f * u))
                }
                listOf(Offset(cx, arr), Offset(cx, aba)).forEach { c -> drawCircle(e.tertiary.copy(alpha = 0.6f), 7 * u, c); drawRect(e.background, Offset(c.x - 2.4f * u, c.y - 2.4f * u), Size(4.8f * u, 4.8f * u)) }
            }
            TemaId.GOTICO -> {
                // Un arco apuntado, doble, como el marco de una vidriera.
                fun ojiva(d: Float, c: Color, g: Float) {
                    val ah = (der - izq) * 0.6f; val an = der - izq
                    drawPath(Path().apply {
                        moveTo(izq + d, aba - d); lineTo(izq + d, arr + ah)
                        cubicTo(izq + d, arr + ah * 0.45f + d, cx - an * 0.18f, arr + d * 1.5f, cx, arr + d)
                        cubicTo(cx + an * 0.18f, arr + d * 1.5f, der - d, arr + ah * 0.45f + d, der - d, arr + ah)
                        lineTo(der - d, aba - d); close()
                    }, c, style = Stroke(g))
                }
                ojiva(0f, col, 1.4f * u); ojiva(7 * u, tenue, 0.8f * u)
            }
            TemaId.ESPACIO -> {
                // Pantalla de la sala de control: escuadras, reglas graduadas a los lados y retículas en las esquinas.
                escuadras(26 * u, 1.4f * u, col)
                var y = arr + 40 * u; var i = 0
                while (y < aba - 40 * u) {
                    val l = if (i % 4 == 0) 10 * u else 5 * u
                    drawLine(tenue, Offset(izq, y), Offset(izq + l, y), 1f * u); drawLine(tenue, Offset(der, y), Offset(der - l, y), 1f * u)
                    y += 16 * u; i++
                }
                esquinas.forEach { c ->
                    val q = haciaDentro(c, 16 * u)
                    drawCircle(tenue, 4 * u, q, style = Stroke(1f * u))
                    drawLine(tenue, q - Offset(7 * u, 0f), q + Offset(7 * u, 0f), 0.8f * u); drawLine(tenue, q - Offset(0f, 7 * u), q + Offset(0f, 7 * u), 0.8f * u)
                }
            }
            TemaId.ANDALUS -> {
                // Remate lobulado arriba, como un arco de yesería, doble.
                fun lobulado(d: Float, c: Color, g: Float) {
                    val n = 9; val paso = (der - izq - 2 * d) / n
                    drawPath(Path().apply {
                        moveTo(izq + d, aba - d); lineTo(izq + d, arr + d + paso / 2)
                        for (i in 0 until n) { val x0 = izq + d + i * paso; arcTo(Rect(x0, arr + d, x0 + paso, arr + d + paso), 180f, 180f, false) }
                        lineTo(der - d, aba - d); close()
                    }, c, style = Stroke(g))
                }
                lobulado(0f, col, 1.3f * u); lobulado(7 * u, tenue, 0.8f * u)
            }
            TemaId.MONGOL -> {
                // Fieltro: esquinas redondeadas, una costura punteada por dentro y botones a la mitad de cada lado.
                drawRoundRect(col, Offset(izq, arr), Size(der - izq, aba - arr), CornerRadius(26 * u), style = Stroke(2f * u))
                drawRoundRect(tenue, Offset(izq + 7 * u, arr + 7 * u), Size(der - izq - 14 * u, aba - arr - 14 * u), CornerRadius(20 * u),
                    style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * u, 4 * u))))
                listOf(Offset(cx, arr), Offset(cx, aba), Offset(izq, cy), Offset(der, cy)).forEach { c -> drawCircle(col, 5 * u, c, style = Stroke(1.4f * u)); drawCircle(col, 1.6f * u, c) }
            }
            TemaId.RENACIMIENTO -> {
                // Trazos de dibujante: líneas que se pasan de las esquinas, círculos de compás y diagonales de construcción.
                val sobra = 14 * u
                drawLine(col, Offset(izq - sobra, arr), Offset(der + sobra, arr), 0.9f * u); drawLine(col, Offset(izq - sobra, aba), Offset(der + sobra, aba), 0.9f * u)
                drawLine(col, Offset(izq, arr - sobra), Offset(izq, aba + sobra), 0.9f * u); drawLine(col, Offset(der, arr - sobra), Offset(der, aba + sobra), 0.9f * u)
                esquinas.forEach { c -> drawCircle(tenue, 9 * u, c, style = Stroke(0.7f * u)) }
                val punteado = PathEffect.dashPathEffect(floatArrayOf(3 * u, 5 * u))
                drawLine(e.secondary.copy(alpha = 0.22f), Offset(izq, arr), Offset(der, aba), 0.6f * u, pathEffect = punteado)
                drawLine(e.secondary.copy(alpha = 0.22f), Offset(der, arr), Offset(izq, aba), 0.6f * u, pathEffect = punteado)
            }
            TemaId.URSS -> {
                // Constructivista: barras gruesas a la izquierda y abajo, filetes finos y una cuña roja en la esquina.
                drawRect(e.primary.copy(alpha = 0.55f), Offset(izq, arr), Size(7 * u, aba - arr))
                drawRect(e.onBackground.copy(alpha = 0.45f), Offset(izq, aba - 7 * u), Size(der - izq, 7 * u))
                drawLine(tenue, Offset(izq, arr), Offset(der, arr), 1f * u); drawLine(tenue, Offset(der, arr), Offset(der, aba), 1f * u)
                drawPath(Path().apply { moveTo(izq, arr); lineTo(izq + 70 * u, arr); lineTo(izq, arr + 70 * u); close() }, e.primary.copy(alpha = 0.45f))
            }
            TemaId.REVOLUCION -> {
                // Borde tricolor (azul a la izquierda, blanco arriba y abajo, rojo a la derecha) con una escarapela arriba.
                val azul = Color(0xFF2B4BA8).copy(alpha = 0.6f); val rojo = Color(0xFFC8352E).copy(alpha = 0.6f)
                val blanco = if (claro) e.onBackground.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.5f)
                val g = 4 * u
                drawLine(blanco, Offset(izq, arr), Offset(der, arr), g); drawLine(blanco, Offset(izq, aba), Offset(der, aba), g)
                drawLine(azul, Offset(izq, arr), Offset(izq, aba), g); drawLine(rojo, Offset(der, arr), Offset(der, aba), g)
                val c = Offset(cx, arr)
                drawCircle(rojo, 9 * u, c); drawCircle(if (claro) e.background else Color.White.copy(alpha = 0.8f), 6 * u, c); drawCircle(azul, 3 * u, c)
            }
            TemaId.ANTIGUAS -> {
                // Tabla de piedra de esquinas con muesca, doble, con tres runas arriba y tres abajo.
                val k = 12 * u
                fun muesca(d: Float, c: Color, g: Float) = drawPath(Path().apply {
                    moveTo(izq + d + k, arr + d); lineTo(der - d - k, arr + d); lineTo(der - d - k, arr + d + k); lineTo(der - d, arr + d + k)
                    lineTo(der - d, aba - d - k); lineTo(der - d - k, aba - d - k); lineTo(der - d - k, aba - d); lineTo(izq + d + k, aba - d)
                    lineTo(izq + d + k, aba - d - k); lineTo(izq + d, aba - d - k); lineTo(izq + d, arr + d + k); lineTo(izq + d + k, arr + d + k); close()
                }, c, style = Stroke(g))
                muesca(0f, col, 2f * u); muesca(6 * u, tenue, 0.9f * u)
                for (j in -1..1) {
                    runaVivo(RUNAS_VIVO[j + 1], Offset(cx + j * 16 * u, arr + 20 * u), 12 * u, col, 1.3f * u)
                    runaVivo(RUNAS_VIVO[j + 5], Offset(cx + j * 16 * u, aba - 20 * u), 12 * u, col, 1.3f * u)
                }
            }
        }
    }
}

/** Las dos variantes dibujadas a tamaño de pantalla: se ve una, la otra se graba sin mostrarse. */
@Composable
private fun DosFondos(tema: TemaId, oscuro: Boolean, visible: String?, inicio: androidx.compose.ui.graphics.layer.GraphicsLayer, bloqueo: androidx.compose.ui.graphics.layer.GraphicsLayer) {
    listOf("inicio" to inicio, "bloqueo" to bloqueo).forEach { (v, capa) ->
        FondoTelefonoVista(tema, oscuro, Modifier.fillMaxSize().drawWithContent { capa.record { this@drawWithContent.drawContent() }; if (v == visible) drawLayer(capa) }, v)
    }
}

private suspend fun foto(capa: androidx.compose.ui.graphics.layer.GraphicsLayer): Bitmap? =
    runCatching { capa.toImageBitmap().asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false) }.getOrNull()

/** Pantalla para ver los fondos a tamaño real y ponerlos. */
@Composable
fun PantallaFondoTelefono(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val tema = Estilo.tema
    val oscuroInicial = modoOscuro(tema)
    var oscuro by remember { mutableStateOf(oscuroInicial) }
    var ver by remember { mutableStateOf("bloqueo") }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val capaInicio = rememberGraphicsLayer()
    val capaBloqueo = rememberGraphicsLayer()
    val sentir = rememberSentir()

    Box(Modifier.fillMaxSize()) {
        DosFondos(tema, oscuro, ver, capaInicio, capaBloqueo)
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)) {
                TextButton(onClick = nav.atras) { Text(Estilo.atras, color = MaterialTheme.colorScheme.primary) }
            }
        }
        Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(12.dp), shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Fondo del teléfono", style = MaterialTheme.typography.titleMedium)
                Ayuda(mensaje ?: "Dos fondos del tema ${tema.nombre}: el de bloqueo con el emblema y el marco, y uno más limpio para la pantalla de inicio, para que no se sature con las apps.")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("bloqueo", ver == "bloqueo") { ver = "bloqueo" }; Ficha("inicio", ver == "inicio") { ver = "inicio" }
                    if (!tema.soloOscuro) { Ficha("claro", !oscuro) { oscuro = false }; Ficha("oscuro", oscuro) { oscuro = true } }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("inicio" to "poner en inicio", "bloqueo" to "en bloqueo", "ambas" to "en los dos").forEach { (id, n) ->
                        BotonSecundario(n, Modifier.weight(1f), habilitado = !ocupado) {
                            ocupado = true
                            scope.launch {
                                val bi = foto(capaInicio); val bb = foto(capaBloqueo)
                                val ok = (bi != null || bb != null) && withContext(Dispatchers.IO) {
                                    bi?.let { FondosTelefono.guardar(ctx, it, tema, oscuro, "inicio") }; bb?.let { FondosTelefono.guardar(ctx, it, tema, oscuro, "bloqueo") }
                                    FondosTelefono.poner(ctx, bi, bb, id)
                                }
                                if (ok) { FondosTelefono.marcarUltimo(ctx, "${tema.id}/$oscuro"); Repo.cambiarConfig { it.copy(fondo_telefono_donde = id) }; sentir(Sentidos.Evento.GUARDAR) }
                                mensaje = if (ok) "Listo: " + when (id) { "inicio" -> "el limpio ya es tu fondo de inicio."; "bloqueo" -> "ya es tu fondo de bloqueo."; else -> "el limpio en inicio y el completo en bloqueo." } else "El teléfono no dejó cambiar el fondo."
                                ocupado = false
                            }
                        }
                    }
                }
                BotonPrincipal("Fondo vivo", Modifier.fillMaxWidth(), habilitado = !ocupado) {
                    ocupado = true
                    scope.launch {
                        // El fondo vivo dibuja encima de estas mismas imágenes; se guardan en claro y en oscuro de una vez.
                        suspend fun guardarAmbas() {
                            foto(capaInicio)?.let { b -> withContext(Dispatchers.IO) { FondosTelefono.guardar(ctx, b, tema, oscuro, "inicio") } }
                            foto(capaBloqueo)?.let { b -> withContext(Dispatchers.IO) { FondosTelefono.guardar(ctx, b, tema, oscuro, "bloqueo") } }
                        }
                        guardarAmbas()
                        if (!tema.soloOscuro) { oscuro = !oscuro; delay(400); guardarAmbas(); oscuro = !oscuro }
                        ocupado = false
                        FondosTelefono.elegirVivo(ctx)
                    }
                }
                Interruptor("Cambiar el fondo fijo al cambiar de tema", Repo.config.fondo_telefono_auto, "Cuando elijas otro tema en la app, tus fondos de inicio y bloqueo cambian también.") { v -> Repo.cambiarConfig { it.copy(fondo_telefono_auto = v) } }
            }
        }
    }
}

/**
 * Detrás de todo, sin verse: cuando cambias de tema prepara las imágenes del fondo nuevo para el fondo vivo
 * y, si lo pediste, cambia también los fondos fijos.
 */
@Composable
fun CapturaFondoTelefono() {
    val ctx = LocalContext.current
    val tema = Estilo.tema
    val oscuro = modoOscuro(tema)
    val auto = Repo.config.fondo_telefono_auto
    val vivo = remember(tema, oscuro) { FondosTelefono.vivoActivo(ctx) }
    val clave = "${tema.id}/$oscuro"
    val falta = remember(tema, oscuro, auto, vivo) {
        (vivo && !FondosTelefono.archivo(ctx, tema, oscuro, "inicio").exists()) || (auto && FondosTelefono.ultimo(ctx) != null && FondosTelefono.ultimo(ctx) != clave)
    }
    if (!falta) return
    val capaInicio = rememberGraphicsLayer()
    val capaBloqueo = rememberGraphicsLayer()
    var hecho by remember(clave) { mutableStateOf(false) }
    if (hecho) return
    Box(Modifier.fillMaxSize()) { DosFondos(tema, oscuro, null, capaInicio, capaBloqueo) }
    LaunchedEffect(clave) {
        delay(600)
        val bi = foto(capaInicio); val bb = foto(capaBloqueo)
        withContext(Dispatchers.IO) {
            runCatching { bi?.let { FondosTelefono.guardar(ctx, it, tema, oscuro, "inicio") }; bb?.let { FondosTelefono.guardar(ctx, it, tema, oscuro, "bloqueo") } }
            if (auto && FondosTelefono.poner(ctx, bi, bb, Repo.config.fondo_telefono_donde)) FondosTelefono.marcarUltimo(ctx, clave)
        }
        hecho = true
    }
}

// =====================================================================
//  El fondo vivo
// =====================================================================

/** Fondo vivo: la imagen del tema y, encima, lo que se mueve en ese tema. Cambia solo cuando cambias de tema en la app. */
class FondoVivoServicio : WallpaperService() {
    override fun onCreateEngine(): Engine = Motor()

    inner class Motor : Engine() {
        private val mano = Handler(Looper.getMainLooper())
        private var visible = false
        private var base: Bitmap? = null
        private var claveBase = ""
        private var t0 = System.currentTimeMillis()
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private var motas: List<MotaVivo> = emptyList()
        private var motasDe: TemaId? = null
        private val dibujar = Runnable { cuadro() }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            Repo.init(applicationContext)
        }
        override fun onVisibilityChanged(v: Boolean) {
            visible = v
            mano.removeCallbacks(dibujar)
            if (v) cuadro()
        }
        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) { super.onSurfaceChanged(holder, format, width, height); claveBase = ""; cuadro() }
        override fun onSurfaceDestroyed(holder: SurfaceHolder) { visible = false; mano.removeCallbacks(dibujar); super.onSurfaceDestroyed(holder) }
        override fun onDestroy() { mano.removeCallbacks(dibujar); super.onDestroy() }

        private fun tema(): TemaId = Estilo.tema
        private fun oscuro(t: TemaId): Boolean = t.soloOscuro || when (Repo.config.modo) {
            "claro" -> false; "oscuro" -> true
            else -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }

        /** En la pantalla de bloqueo (Android 14+ lo dice) va el completo; en inicio, el limpio. */
        private fun variante(): String = runCatching {
            if (android.os.Build.VERSION.SDK_INT >= 34 && (wallpaperFlags and WallpaperManager.FLAG_SYSTEM) == 0 && (wallpaperFlags and WallpaperManager.FLAG_LOCK) != 0) "bloqueo" else "inicio"
        }.getOrDefault("inicio")

        private fun cargar(t: TemaId, o: Boolean, w: Int, h: Int) {
            val f = FondosTelefono.archivo(applicationContext, t, o, variante())
            val clave = "${t.id}/$o/${f.lastModified()}/$w/$h"
            if (clave == claveBase) return
            claveBase = clave
            base = runCatching { BitmapFactory.decodeFile(f.absolutePath) }.getOrNull() ?: runCatching { BitmapFactory.decodeResource(resources, logo(t)) }.getOrNull()
        }

        private fun cuadro() {
            val h = surfaceHolder
            val c = runCatching { h.lockCanvas() }.getOrNull() ?: return
            try {
                val t = tema(); val o = oscuro(t)
                val e = esquemaDe(t, o)
                val w = c.width; val alto = c.height
                cargar(t, o, w, alto)
                c.drawColor(e.background.toArgb())
                base?.let { b ->
                    if (b.width >= w / 2) {
                        // La imagen del tema, recortada para cubrir la pantalla.
                        val s = maxOf(w.toFloat() / b.width, alto.toFloat() / b.height)
                        val dw = b.width * s; val dh = b.height * s
                        c.drawBitmap(b, null, android.graphics.RectF((w - dw) / 2, (alto - dh) / 2, (w + dw) / 2, (alto + dh) / 2), p)
                    } else {
                        val lado = w * 0.42f
                        c.drawBitmap(b, null, android.graphics.RectF((w - lado) / 2, alto * 0.42f - lado / 2, (w + lado) / 2, alto * 0.42f + lado / 2), p)
                    }
                }
                particulas(c, t, o, e, w.toFloat(), alto.toFloat())
            } finally {
                runCatching { h.unlockCanvasAndPost(c) }
            }
            mano.removeCallbacks(dibujar)
            // Lo que se mueve muy despacio (nubes, hormigas) no necesita tantos cuadros.
            if (visible) mano.postDelayed(dibujar, if (vivoTema(tema(), oscuro(tema())).periodo >= 60000) 100L else 50L)
        }

        /**
         * Lo mismo que el fondo vivo de la app (el mismo [vivoTema] y el mismo dibujo, [pintarVivoTema]), un poco más
         * visible porque aquí no hay nada encima.
         */
        private fun particulas(c: android.graphics.Canvas, t: TemaId, o: Boolean, e: androidx.compose.material3.ColorScheme, w: Float, h: Float) {
            val tipo = vivoTema(t, o)
            if (motasDe != t) { motas = motasVivo(t); motasDe = t }
            val lista = motas
            val tt = ((System.currentTimeMillis() - t0) % tipo.periodo) / tipo.periodo.toFloat()
            CanvasDrawScope().draw(Density(resources.displayMetrics.density), LayoutDirection.Ltr, androidx.compose.ui.graphics.Canvas(c), Size(w, h)) {
                pintarVivoTema(tipo, tt, o, e, lista, fuerza = 1.6f)
            }
        }
    }
}

private fun logo(t: TemaId): Int = when (t) {
    TemaId.ROMANO -> R.drawable.logo_romano; TemaId.CIBER -> R.drawable.logo_ciber; TemaId.HELENO -> R.drawable.logo_heleno; TemaId.MEDIEVAL -> R.drawable.logo_medieval
    TemaId.DISTOPIA -> R.drawable.logo_distopia; TemaId.REFUGIO -> R.drawable.logo_refugio; TemaId.ORDEN -> R.drawable.logo_orden; TemaId.LABORATORIO -> R.drawable.logo_laboratorio
    TemaId.COLONOS -> R.drawable.logo_colonos; TemaId.NAPOLEON -> R.drawable.logo_napoleon; TemaId.BOTANICO -> R.drawable.logo_botanico; TemaId.NAVIDAD -> R.drawable.logo_navidad
    TemaId.SEDA -> R.drawable.logo_seda; TemaId.URSS -> R.drawable.logo_urss; TemaId.GOTICO -> R.drawable.logo_gotico; TemaId.ESPACIO -> R.drawable.logo_espacio
    TemaId.ANDALUS -> R.drawable.logo_andalus; TemaId.MONGOL -> R.drawable.logo_mongol; TemaId.RENACIMIENTO -> R.drawable.logo_renacimiento
    TemaId.REVOLUCION -> R.drawable.logo_revolucion
    TemaId.ANTIGUAS -> R.drawable.logo_antiguas
}
