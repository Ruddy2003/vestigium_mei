package io.github.ruddy2003.vestigium.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
    private const val VERSION = 2

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

    /** Lo que lleva escrito el fondo bajo el emblema. */
    fun marca(t: TemaId): String = when (t) {
        TemaId.ROMANO -> "VESTIGIVM · MEI"; TemaId.HELENO -> "ΙΧΝΟΣ ΕΜΟΝ"; TemaId.CIBER -> "vestigium.mei"; TemaId.ANDALUS -> "أثري"
        TemaId.MONGOL -> "МИНИЙ МӨР"; TemaId.RENACIMIENTO -> "Vestigium mei"; TemaId.MEDIEVAL, TemaId.GOTICO -> "Vestigium mei"
        TemaId.DISTOPIA -> "EXPEDIENTE PERSONAL"; TemaId.URSS -> "VESTIGIUM MEI"; TemaId.REFUGIO -> "VESTIGIUM-MEI OS"; TemaId.ESPACIO -> "VESTIGIUM · MEI"
        TemaId.REVOLUCION -> "VESTIGIUM · MEI · AN I"
        else -> "Vestigium Mei"
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

/** Un marco fino a todo el borde: doble filete con rombos en las esquinas (papel) o escuadras de luz (pantallas). */
@Composable
private fun MarcoFondo(tema: TemaId, e: androidx.compose.material3.ColorScheme) {
    val pantalla = tema in setOf(TemaId.CIBER, TemaId.ESPACIO, TemaId.LABORATORIO, TemaId.REFUGIO, TemaId.ORDEN)
    Canvas(Modifier.fillMaxSize()) {
        val u = 1.dp.toPx(); val m = 22 * u
        val col = e.primary.copy(alpha = if (pantalla) 0.55f else 0.38f)
        val izq = m; val der = size.width - m; val arr = m + 26 * u; val aba = size.height - m - 10 * u
        if (pantalla) {
            val l = 34 * u
            listOf(Offset(izq, arr) to Offset(1f, 1f), Offset(der, arr) to Offset(-1f, 1f), Offset(izq, aba) to Offset(1f, -1f), Offset(der, aba) to Offset(-1f, -1f)).forEach { (o, d) ->
                drawLine(col, o, o + Offset(d.x * l, 0f), 2 * u); drawLine(col, o, o + Offset(0f, d.y * l), 2 * u)
            }
        } else {
            val est = androidx.compose.ui.graphics.drawscope.Stroke(1.2f * u)
            drawRect(col, Offset(izq, arr), androidx.compose.ui.geometry.Size(der - izq, aba - arr), style = est)
            drawRect(col.copy(alpha = col.alpha * 0.6f), Offset(izq + 6 * u, arr + 6 * u), androidx.compose.ui.geometry.Size(der - izq - 12 * u, aba - arr - 12 * u), style = androidx.compose.ui.graphics.drawscope.Stroke(0.7f * u))
            listOf(Offset(izq, arr), Offset(der, arr), Offset(izq, aba), Offset(der, aba)).forEach { c ->
                val k = 7 * u
                drawPath(androidx.compose.ui.graphics.Path().apply { moveTo(c.x - k, c.y); lineTo(c.x, c.y - k); lineTo(c.x + k, c.y); lineTo(c.x, c.y + k); close() }, e.primary.copy(alpha = 0.6f))
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

    private class Mota(val x: Float, val y: Float, val v: Float, val fase: Float, val tam: Float, val tono: Int)

    inner class Motor : Engine() {
        private val mano = Handler(Looper.getMainLooper())
        private var visible = false
        private var base: Bitmap? = null
        private var claveBase = ""
        private var t0 = System.currentTimeMillis()
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private val motas = run { val r = java.util.Random(5); List(34) { Mota(r.nextFloat(), r.nextFloat(), 0.4f + r.nextFloat() * 0.6f, r.nextFloat(), 0.5f + r.nextFloat(), r.nextInt(3)) } }
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
            if (visible) mano.postDelayed(dibujar, if (tipo(tema(), oscuro(tema())) == null) 2000L else 50L)
        }

        private fun tipo(t: TemaId, o: Boolean): String? = when (t) {
            TemaId.ESPACIO -> "estrellas"; TemaId.GOTICO -> "vitral"; TemaId.BOTANICO -> if (o) "luciernagas" else "hormigas"; TemaId.NAVIDAD -> "nieve"
            TemaId.ANDALUS -> "agua"; TemaId.MONGOL -> "nubes"; TemaId.CIBER -> "lluvia"; TemaId.RENACIMIENTO -> "polvo"; TemaId.SEDA -> "arena"
            TemaId.HELENO -> if (o) "estrellas" else "polvo"; TemaId.REFUGIO -> if (o) "barrido" else null; TemaId.MEDIEVAL, TemaId.ROMANO, TemaId.COLONOS -> "polvo"
            TemaId.LABORATORIO -> "polen"; TemaId.NAPOLEON, TemaId.REVOLUCION -> "polvo"; TemaId.ORDEN, TemaId.DISTOPIA, TemaId.URSS -> "barrido"
        }

        private fun alfa(c: Color, a: Float) = c.copy(alpha = a.coerceIn(0f, 1f)).toArgb()

        /** Lo mismo que el fondo vivo de la app, un poco más visible porque aquí no hay nada encima. */
        private fun particulas(c: android.graphics.Canvas, t: TemaId, o: Boolean, e: androidx.compose.material3.ColorScheme, w: Float, h: Float) {
            val tp = tipo(t, o) ?: return
            val u = resources.displayMetrics.density
            val periodo = when (tp) { "nubes" -> 90000; "hormigas" -> 70000; "agua" -> 14000; "estrellas", "luciernagas" -> 9000; "lluvia" -> 4000; "barrido" -> 7000; else -> 24000 }
            val tt = ((System.currentTimeMillis() - t0) % periodo) / periodo.toFloat()
            val tonos = listOf(e.primary, e.secondary, e.tertiary)
            p.shader = null; p.style = Paint.Style.FILL
            when (tp) {
                "estrellas" -> motas.forEach { m ->
                    val a = 0.2f + 0.7f * (0.5f + 0.5f * sin(2 * PI * (tt * 3 * m.v + m.fase)).toFloat())
                    val x = m.x * w; val y = m.y * h; val s = 2.6f * u * m.tam
                    p.color = alfa(if (o) Color.White else e.primary, a * (if (o) 0.75f else 0.35f)); p.strokeWidth = 1.1f * u
                    c.drawLine(x - s, y, x + s, y, p); c.drawLine(x, y - s, x, y + s, p); c.drawCircle(x, y, 1.1f * u, p)
                }
                "vitral" -> motas.take(14).forEach { m ->
                    val x = ((m.x + tt * 0.25f * m.v) % 1.2f - 0.1f) * w; val y = (m.y + 0.03f * sin(2 * PI * (tt + m.fase)).toFloat()) * h
                    val col = listOf(e.secondary, if (o) e.tertiary else e.primary, Color(0xFF2E8B57), Color(0xFFE2B34A))[m.tono % 4]
                    val r = 60f * u * m.tam
                    p.shader = RadialGradient(x, y, r, alfa(col, if (o) 0.2f else 0.14f), 0, Shader.TileMode.CLAMP); c.drawCircle(x, y, r, p); p.shader = null
                }
                "polen", "polvo", "arena" -> motas.forEach { m ->
                    val y = ((m.y + tt * m.v * 0.6f) % 1f) * h
                    val x = (m.x + 0.02f * sin(2 * PI * (tt * 4 + m.fase)).toFloat()) * w
                    p.color = alfa(when (tp) { "polen" -> Color(0xFFE2B84A); "arena" -> tonos[2]; else -> e.onBackground }, if (tp == "polvo") 0.16f else 0.3f)
                    c.drawCircle(x, y, 1.6f * u * m.tam, p)
                }
                "hormigas" -> motas.take(7).forEachIndexed { i, m ->
                    val avance = (m.x + tt * (0.5f + m.v * 0.6f)) % 1.3f - 0.15f
                    val dir = if (i % 2 == 0) 1f else -1f
                    val x = (if (dir > 0) avance else 1f - avance) * w
                    val fase = 2 * PI * (avance * 2 + m.fase)
                    val y = m.y * h + 22f * u * sin(fase).toFloat()
                    val ang = kotlin.math.atan2((22f * u * cos(fase) * 4 * PI / w).toFloat() * dir, dir)
                    val l = 7f * u * (0.8f + m.tam * 0.3f); val dx = cos(ang); val dy = sin(ang)
                    p.color = alfa(e.onBackground, 0.22f)
                    c.drawCircle(x - dx * l * 0.35f, y - dy * l * 0.35f, l * 0.2f, p); c.drawCircle(x, y, l * 0.11f, p); c.drawCircle(x + dx * l * 0.28f, y + dy * l * 0.28f, l * 0.12f, p)
                    p.strokeWidth = 0.9f * u
                    for (k in -1..1) { val bx = x + dx * l * 0.05f * k; val by = y + dy * l * 0.05f * k
                        c.drawLine(bx, by, bx - dy * l * 0.35f + dx * l * 0.12f * k, by + dx * l * 0.35f + dy * l * 0.12f * k, p)
                        c.drawLine(bx, by, bx + dy * l * 0.35f + dx * l * 0.12f * k, by - dx * l * 0.35f + dy * l * 0.12f * k, p) }
                }
                "luciernagas" -> motas.forEach { m ->
                    val x = (m.x + 0.04f * sin(2 * PI * (tt * m.v + m.fase)).toFloat()) * w
                    val y = (m.y + 0.03f * cos(2 * PI * (tt * m.v * 1.3f + m.fase)).toFloat()) * h
                    val a = sin(2 * PI * (tt * 2 * m.v + m.fase)).toFloat().coerceAtLeast(0f)
                    if (a > 0.01f) {
                        p.shader = RadialGradient(x, y, 12f * u, alfa(Color(0xFFD8F07A), 0.55f * a), 0, Shader.TileMode.CLAMP); c.drawCircle(x, y, 12f * u, p); p.shader = null
                        p.color = alfa(Color(0xFFF2FFB0), 0.9f * a); c.drawCircle(x, y, 1.6f * u, p)
                    }
                }
                "nieve" -> motas.forEach { m ->
                    val y = ((m.y + tt * m.v) % 1f) * h
                    val x = (m.x + 0.015f * sin(2 * PI * (tt * 6 + m.fase)).toFloat()) * w
                    p.color = alfa(if (o) Color.White else e.secondary, if (o) 0.5f else 0.25f); c.drawCircle(x, y, 2.2f * u * m.tam, p)
                }
                "agua" -> {
                    p.style = Paint.Style.STROKE; p.strokeWidth = 1.4f * u; p.color = alfa(e.secondary, if (o) 0.1f else 0.09f)
                    for (k in 0 until 7) {
                        val y0 = h * (0.12f + k * 0.13f); val path = android.graphics.Path(); var x = 0f
                        path.moveTo(0f, y0)
                        while (x <= w) { path.lineTo(x, y0 + 6f * u * sin(2 * PI * (x / (100f * u) + tt + k * 0.3f)).toFloat()); x += 6f * u }
                        c.drawPath(path, p)
                    }
                }
                "nubes" -> motas.take(6).forEach { m ->
                    val x = ((m.x + tt * m.v * 0.5f) % 1.4f - 0.2f) * w; val y = m.y * h * 0.55f
                    p.color = alfa(Color.White, if (o) 0.04f else 0.4f)
                    listOf(Offset(0f, 0f) to 30f, Offset(26f, -9f) to 23f, Offset(-26f, -5f) to 21f, Offset(46f, 5f) to 16f).forEach { (of, r) ->
                        c.drawCircle(x + of.x * u * m.tam, y + of.y * u * m.tam, r * u * m.tam, p)
                    }
                }
                "lluvia" -> motas.forEach { m ->
                    val y = ((m.y + tt * m.v * 2f) % 1.1f) * h; val x = m.x * w
                    p.color = alfa(tonos[m.tono % 2], 0.22f); p.strokeWidth = 1.2f * u
                    c.drawLine(x, y, x, y + 16f * u * m.tam, p)
                }
                "barrido" -> { p.color = alfa(e.primary, if (o) 0.06f else 0.035f); c.drawRect(0f, tt * h, w, tt * h + 30f * u, p) }
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
}
