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
    private const val VERSION = 1

    fun archivo(ctx: Context, tema: TemaId, oscuro: Boolean): File =
        File(File(ctx.filesDir, "fondos").apply { mkdirs() }, "fondo_v${VERSION}_${tema.id}_${if (oscuro) "o" else "c"}.png")

    fun guardar(ctx: Context, bmp: Bitmap, tema: TemaId, oscuro: Boolean) {
        val f = archivo(ctx, tema, oscuro)
        val tmp = File(f.parentFile, f.name + ".tmp")
        tmp.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        tmp.renameTo(f)
    }

    /** Pone una imagen como fondo. [donde]: «inicio», «bloqueo» o «ambas». */
    fun poner(ctx: Context, bmp: Bitmap, donde: String): Boolean = runCatching {
        val wm = WallpaperManager.getInstance(ctx)
        val banderas = when (donde) { "inicio" -> WallpaperManager.FLAG_SYSTEM; "bloqueo" -> WallpaperManager.FLAG_LOCK; else -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK }
        wm.setBitmap(bmp, null, true, banderas)
        true
    }.getOrDefault(false)

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
        else -> "Vestigium Mei"
    }
}

/** El fondo tal como queda en el teléfono: papel o pantalla del tema, su emblema grande y la banda de adorno. */
@Composable
fun FondoTelefonoVista(tema: TemaId, oscuro: Boolean, modifier: Modifier = Modifier) {
    val o = oscuro || tema.soloOscuro
    val e = esquemaDe(tema, o)
    MaterialTheme(colorScheme = e, typography = tipoDe(tema, o)) {
        Box(modifier.background(e.background).fondoTema(tema, e)) {
            FondoVivo(tema, fijo = 0.37f)
            // Una luz suave detrás del emblema y las orillas un poco más oscuras.
            Canvas(Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2, size.height * 0.42f)
                drawRect(Brush.radialGradient(listOf(e.primary.copy(alpha = if (o) 0.10f else 0.06f), Color.Transparent), c, size.width * 0.7f))
                drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (o) 0.35f else 0.10f)), Offset(size.width / 2, size.height / 2), size.height * 0.75f))
            }
            Column(Modifier.align(BiasAlignment(0f, -0.18f)), horizontalAlignment = Alignment.CenterHorizontally) {
                Emblema(tema, 150.dp, e, animado = false)
                Text(FondosTelefono.marca(tema), style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 3.sp), color = e.primary, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 22.dp))
                Meandro(Modifier.width(210.dp).padding(top = 10.dp))
            }
        }
    }
}

/** Pantalla para ver el fondo a tamaño real y ponerlo. */
@Composable
fun PantallaFondoTelefono(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val tema = Estilo.tema
    var oscuro by remember { mutableStateOf(modoOscuro(tema)) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val capa = rememberGraphicsLayer()
    val sentir = rememberSentir()

    suspend fun foto(): Bitmap? = runCatching { capa.toImageBitmap().asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false) }.getOrNull()

    Box(Modifier.fillMaxSize()) {
        FondoTelefonoVista(tema, oscuro, Modifier.fillMaxSize().drawWithContent { capa.record { this@drawWithContent.drawContent() }; drawLayer(capa) })
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)) {
                TextButton(onClick = nav.atras) { Text(Estilo.atras, color = MaterialTheme.colorScheme.primary) }
            }
        }
        Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(12.dp), shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Fondo del teléfono", style = MaterialTheme.typography.titleMedium)
                Ayuda(mensaje ?: "Así se ve con el tema ${tema.nombre}. El fijo se queda así; el vivo se mueve despacio y cambia solo cuando cambias de tema en la app.")
                if (!tema.soloOscuro) Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("claro", !oscuro) { oscuro = false }; Ficha("oscuro", oscuro) { oscuro = true }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("inicio" to "inicio", "bloqueo" to "bloqueo", "ambas" to "las dos").forEach { (id, n) ->
                        BotonSecundario(n, Modifier.weight(1f), habilitado = !ocupado) {
                            ocupado = true
                            scope.launch {
                                val b = foto()
                                val ok = b != null && withContext(Dispatchers.IO) { FondosTelefono.guardar(ctx, b, tema, oscuro); FondosTelefono.poner(ctx, b, id) }
                                if (ok) { FondosTelefono.marcarUltimo(ctx, "${tema.id}/$oscuro"); Repo.cambiarConfig { it.copy(fondo_telefono_donde = id) }; sentir(Sentidos.Evento.GUARDAR) }
                                mensaje = if (ok) "Listo: ya es tu fondo de ${if (id == "ambas") "inicio y bloqueo" else id}." else "El teléfono no dejó cambiar el fondo."
                                ocupado = false
                            }
                        }
                    }
                }
                BotonPrincipal("Fondo vivo", Modifier.fillMaxWidth(), habilitado = !ocupado) {
                    ocupado = true
                    scope.launch {
                        // El fondo vivo dibuja encima de esta misma imagen; se guarda la del tema en claro y en oscuro de una vez.
                        foto()?.let { b -> withContext(Dispatchers.IO) { FondosTelefono.guardar(ctx, b, tema, oscuro) } }
                        if (!tema.soloOscuro) { oscuro = !oscuro; delay(350); foto()?.let { b -> withContext(Dispatchers.IO) { FondosTelefono.guardar(ctx, b, tema, oscuro) } }; oscuro = !oscuro }
                        ocupado = false
                        FondosTelefono.elegirVivo(ctx)
                    }
                }
                Interruptor("Cambiar el fondo fijo al cambiar de tema", Repo.config.fondo_telefono_auto, "Cuando elijas otro tema en la app, tu fondo fijo cambia también.") { v -> Repo.cambiarConfig { it.copy(fondo_telefono_auto = v) } }
            }
        }
    }
}

/**
 * Detrás de todo, sin verse: cuando cambias de tema prepara la imagen del fondo nuevo para el fondo vivo
 * y, si lo pediste, cambia también el fondo fijo.
 */
@Composable
fun CapturaFondoTelefono() {
    val ctx = LocalContext.current
    val tema = Estilo.tema
    val oscuro = modoOscuro(tema)
    val auto = Repo.config.fondo_telefono_auto
    val vivo = remember(tema, oscuro) { FondosTelefono.vivoActivo(ctx) }
    val clave = "${tema.id}/$oscuro"
    val falta = remember(tema, oscuro, auto, vivo) { (vivo && !FondosTelefono.archivo(ctx, tema, oscuro).exists()) || (auto && FondosTelefono.ultimo(ctx) != null && FondosTelefono.ultimo(ctx) != clave) }
    if (!falta) return
    val capa = rememberGraphicsLayer()
    var hecho by remember(clave) { mutableStateOf(false) }
    if (hecho) return
    Box(Modifier.fillMaxSize().drawWithContent { capa.record { this@drawWithContent.drawContent() } }) {
        FondoTelefonoVista(tema, oscuro, Modifier.fillMaxSize())
    }
    LaunchedEffect(clave) {
        delay(600)
        val b = runCatching { capa.toImageBitmap().asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false) }.getOrNull()
        if (b != null) withContext(Dispatchers.IO) {
            runCatching { FondosTelefono.guardar(ctx, b, tema, oscuro) }
            if (auto && FondosTelefono.poner(ctx, b, Repo.config.fondo_telefono_donde)) FondosTelefono.marcarUltimo(ctx, clave)
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

        private fun cargar(t: TemaId, o: Boolean, w: Int, h: Int) {
            val f = FondosTelefono.archivo(applicationContext, t, o)
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
            TemaId.ESPACIO -> "estrellas"; TemaId.GOTICO -> "vitral"; TemaId.BOTANICO -> if (o) "luciernagas" else "polen"; TemaId.NAVIDAD -> "nieve"
            TemaId.ANDALUS -> "agua"; TemaId.MONGOL -> "nubes"; TemaId.CIBER -> "lluvia"; TemaId.RENACIMIENTO -> "polvo"; TemaId.SEDA -> "arena"
            TemaId.HELENO -> if (o) "estrellas" else "polvo"; TemaId.REFUGIO -> if (o) "barrido" else null; TemaId.MEDIEVAL, TemaId.ROMANO, TemaId.COLONOS -> "polvo"
            TemaId.LABORATORIO -> "polen"; TemaId.NAPOLEON -> "polvo"; TemaId.ORDEN, TemaId.DISTOPIA, TemaId.URSS -> "barrido"
        }

        private fun alfa(c: Color, a: Float) = c.copy(alpha = a.coerceIn(0f, 1f)).toArgb()

        /** Lo mismo que el fondo vivo de la app, un poco más visible porque aquí no hay nada encima. */
        private fun particulas(c: android.graphics.Canvas, t: TemaId, o: Boolean, e: androidx.compose.material3.ColorScheme, w: Float, h: Float) {
            val tp = tipo(t, o) ?: return
            val u = resources.displayMetrics.density
            val periodo = when (tp) { "nubes" -> 90000; "agua" -> 14000; "estrellas", "luciernagas" -> 9000; "lluvia" -> 4000; "barrido" -> 7000; else -> 24000 }
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
}
