package io.github.ruddy2003.vestigium.ui

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.delay

/**
 * La música de cada tema: la presentación al elegirlo (una pantalla con su emblema, su nombre y una frase, como al completar el día),
 * y los tonos de llamada y de notificación para todo el teléfono. Los avisos de la app suenan siempre con su propio sonido,
 * distinto de estos, para que sepas que es Vestigium Mei.
 */
object Musica {
    fun presentacion(t: TemaId): Int = when (t) {
        TemaId.ROMANO -> R.raw.s_romano_presentacion
        TemaId.URBE -> R.raw.s_urbe_presentacion
        TemaId.CIBER -> R.raw.s_ciber_presentacion
        TemaId.NEON -> R.raw.s_neon_presentacion
        TemaId.HELENO -> R.raw.s_heleno_presentacion
        TemaId.EGIPTO -> R.raw.s_egipto_presentacion
        TemaId.MEDIEVAL -> R.raw.s_medieval_presentacion
        TemaId.TEMPLARIO -> R.raw.s_templario_presentacion
        TemaId.DISTOPIA -> R.raw.s_distopia_presentacion
        TemaId.REFUGIO -> R.raw.s_refugio_presentacion
        TemaId.ORDEN -> R.raw.s_orden_presentacion
        TemaId.LABORATORIO -> R.raw.s_laboratorio_presentacion
        TemaId.COLONOS -> R.raw.s_colonos_presentacion
        TemaId.NAPOLEON -> R.raw.s_napoleon_presentacion
        TemaId.BOTANICO -> R.raw.s_botanico_presentacion
        TemaId.RUINAS -> R.raw.s_ruinas_presentacion
        TemaId.NAVIDAD -> R.raw.s_navidad_presentacion
        TemaId.SEDA -> R.raw.s_seda_presentacion
        TemaId.URSS -> R.raw.s_urss_presentacion
        TemaId.GOTICO -> R.raw.s_gotico_presentacion
        TemaId.ARCANO -> R.raw.s_arcano_presentacion
        TemaId.ESPACIO -> R.raw.s_espacio_presentacion
        TemaId.ABISMO -> R.raw.s_abismo_presentacion
        TemaId.ANDALUS -> R.raw.s_andalus_presentacion
        TemaId.MESO -> R.raw.s_meso_presentacion
        TemaId.MONGOL -> R.raw.s_mongol_presentacion
        TemaId.RENACIMIENTO -> R.raw.s_renacimiento_presentacion
        TemaId.PIRATA -> R.raw.s_pirata_presentacion
        TemaId.REVOLUCION -> R.raw.s_revolucion_presentacion
        TemaId.ANTIGUAS -> R.raw.s_antiguas_presentacion
    }
    /** La música larga de «Tu año»: la frase del tema que va creciendo (~1 min y medio). */
    fun anual(t: TemaId): Int = when (t) {
        TemaId.ROMANO -> R.raw.s_romano_anual
        TemaId.URBE -> R.raw.s_urbe_anual
        TemaId.CIBER -> R.raw.s_ciber_anual
        TemaId.NEON -> R.raw.s_neon_anual
        TemaId.HELENO -> R.raw.s_heleno_anual
        TemaId.EGIPTO -> R.raw.s_egipto_anual
        TemaId.MEDIEVAL -> R.raw.s_medieval_anual
        TemaId.TEMPLARIO -> R.raw.s_templario_anual
        TemaId.DISTOPIA -> R.raw.s_distopia_anual
        TemaId.REFUGIO -> R.raw.s_refugio_anual
        TemaId.ORDEN -> R.raw.s_orden_anual
        TemaId.LABORATORIO -> R.raw.s_laboratorio_anual
        TemaId.COLONOS -> R.raw.s_colonos_anual
        TemaId.NAPOLEON -> R.raw.s_napoleon_anual
        TemaId.BOTANICO -> R.raw.s_botanico_anual
        TemaId.RUINAS -> R.raw.s_ruinas_anual
        TemaId.NAVIDAD -> R.raw.s_navidad_anual
        TemaId.SEDA -> R.raw.s_seda_anual
        TemaId.URSS -> R.raw.s_urss_anual
        TemaId.GOTICO -> R.raw.s_gotico_anual
        TemaId.ARCANO -> R.raw.s_arcano_anual
        TemaId.ESPACIO -> R.raw.s_espacio_anual
        TemaId.ABISMO -> R.raw.s_abismo_anual
        TemaId.ANDALUS -> R.raw.s_andalus_anual
        TemaId.MESO -> R.raw.s_meso_anual
        TemaId.MONGOL -> R.raw.s_mongol_anual
        TemaId.RENACIMIENTO -> R.raw.s_renacimiento_anual
        TemaId.PIRATA -> R.raw.s_pirata_anual
        TemaId.REVOLUCION -> R.raw.s_revolucion_anual
        TemaId.ANTIGUAS -> R.raw.s_antiguas_anual
    }
    fun llamada(t: TemaId): Int = when (t) {
        TemaId.ROMANO -> R.raw.t_romano_llamada
        TemaId.URBE -> R.raw.t_urbe_llamada
        TemaId.CIBER -> R.raw.t_ciber_llamada
        TemaId.NEON -> R.raw.t_neon_llamada
        TemaId.HELENO -> R.raw.t_heleno_llamada
        TemaId.EGIPTO -> R.raw.t_egipto_llamada
        TemaId.MEDIEVAL -> R.raw.t_medieval_llamada
        TemaId.TEMPLARIO -> R.raw.t_templario_llamada
        TemaId.DISTOPIA -> R.raw.t_distopia_llamada
        TemaId.REFUGIO -> R.raw.t_refugio_llamada
        TemaId.ORDEN -> R.raw.t_orden_llamada
        TemaId.LABORATORIO -> R.raw.t_laboratorio_llamada
        TemaId.COLONOS -> R.raw.t_colonos_llamada
        TemaId.NAPOLEON -> R.raw.t_napoleon_llamada
        TemaId.BOTANICO -> R.raw.t_botanico_llamada
        TemaId.RUINAS -> R.raw.t_ruinas_llamada
        TemaId.NAVIDAD -> R.raw.t_navidad_llamada
        TemaId.SEDA -> R.raw.t_seda_llamada
        TemaId.URSS -> R.raw.t_urss_llamada
        TemaId.GOTICO -> R.raw.t_gotico_llamada
        TemaId.ARCANO -> R.raw.t_arcano_llamada
        TemaId.ESPACIO -> R.raw.t_espacio_llamada
        TemaId.ABISMO -> R.raw.t_abismo_llamada
        TemaId.ANDALUS -> R.raw.t_andalus_llamada
        TemaId.MESO -> R.raw.t_meso_llamada
        TemaId.MONGOL -> R.raw.t_mongol_llamada
        TemaId.RENACIMIENTO -> R.raw.t_renacimiento_llamada
        TemaId.PIRATA -> R.raw.t_pirata_llamada
        TemaId.REVOLUCION -> R.raw.t_revolucion_llamada
        TemaId.ANTIGUAS -> R.raw.t_antiguas_llamada
    }
    fun notificacion(t: TemaId): Int = when (t) {
        TemaId.ROMANO -> R.raw.t_romano_notificacion
        TemaId.URBE -> R.raw.t_urbe_notificacion
        TemaId.CIBER -> R.raw.t_ciber_notificacion
        TemaId.NEON -> R.raw.t_neon_notificacion
        TemaId.HELENO -> R.raw.t_heleno_notificacion
        TemaId.EGIPTO -> R.raw.t_egipto_notificacion
        TemaId.MEDIEVAL -> R.raw.t_medieval_notificacion
        TemaId.TEMPLARIO -> R.raw.t_templario_notificacion
        TemaId.DISTOPIA -> R.raw.t_distopia_notificacion
        TemaId.REFUGIO -> R.raw.t_refugio_notificacion
        TemaId.ORDEN -> R.raw.t_orden_notificacion
        TemaId.LABORATORIO -> R.raw.t_laboratorio_notificacion
        TemaId.COLONOS -> R.raw.t_colonos_notificacion
        TemaId.NAPOLEON -> R.raw.t_napoleon_notificacion
        TemaId.BOTANICO -> R.raw.t_botanico_notificacion
        TemaId.RUINAS -> R.raw.t_ruinas_notificacion
        TemaId.NAVIDAD -> R.raw.t_navidad_notificacion
        TemaId.SEDA -> R.raw.t_seda_notificacion
        TemaId.URSS -> R.raw.t_urss_notificacion
        TemaId.GOTICO -> R.raw.t_gotico_notificacion
        TemaId.ARCANO -> R.raw.t_arcano_notificacion
        TemaId.ESPACIO -> R.raw.t_espacio_notificacion
        TemaId.ABISMO -> R.raw.t_abismo_notificacion
        TemaId.ANDALUS -> R.raw.t_andalus_notificacion
        TemaId.MESO -> R.raw.t_meso_notificacion
        TemaId.MONGOL -> R.raw.t_mongol_notificacion
        TemaId.RENACIMIENTO -> R.raw.t_renacimiento_notificacion
        TemaId.PIRATA -> R.raw.t_pirata_notificacion
        TemaId.REVOLUCION -> R.raw.t_revolucion_notificacion
        TemaId.ANTIGUAS -> R.raw.t_antiguas_notificacion
    }

    private var reproductor: MediaPlayer? = null

    /** Toca un recurso de principio a fin (respeta el modo silencio). Si ya sonaba otro, lo corta. */
    fun tocar(ctx: Context, res: Int, forzar: Boolean = false) {
        detener()
        if (!forzar) {
            if (!Repo.config.sonidos) return
            val audio = ctx.getSystemService(AudioManager::class.java)
            if (audio != null && audio.ringerMode != AudioManager.RINGER_MODE_NORMAL) return
        }
        runCatching {
            val mp = MediaPlayer()
            mp.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            ctx.resources.openRawResourceFd(res).use { fd -> mp.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length) }
            mp.setOnCompletionListener { it.release(); if (reproductor === it) reproductor = null }
            mp.prepare(); mp.start()
            reproductor = mp
        }
    }

    /** Para «Tu año»: la música larga, que se puede bajar poco a poco y se repite si las historias duran más. */
    fun anualTocar(ctx: Context, t: TemaId): MediaPlayer? {
        detener()
        if (!Repo.config.sonidos) return null
        val audio = ctx.getSystemService(AudioManager::class.java)
        if (audio != null && audio.ringerMode != AudioManager.RINGER_MODE_NORMAL) return null
        return runCatching {
            val mp = MediaPlayer()
            mp.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            ctx.resources.openRawResourceFd(anual(t)).use { fd -> mp.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length) }
            mp.isLooping = true
            mp.prepare(); mp.start()
            reproductor = mp
            mp
        }.getOrNull()
    }

    fun detener() {
        reproductor?.let { runCatching { it.stop() }; runCatching { it.release() } }
        reproductor = null
    }
}

/** La presentación del tema recién elegido. */
object PresentacionTema {
    var tema by mutableStateOf<TemaId?>(null)
    var frase by mutableStateOf<Frases.Frase?>(null)
    var vuelta by mutableIntStateOf(0)

    fun mostrar(ctx: Context, t: TemaId) {
        Sentidos.vibrarEvento(ctx, Sentidos.Evento.TEMA)
        Musica.tocar(ctx, Musica.presentacion(t))
        if (!Repo.config.animaciones) return
        frase = Frases.de(t).random()
        tema = t
        vuelta++
        Fiesta.lanzar()
    }

    fun cerrar() { tema = null }
}

/**
 * Cómo se acomoda la presentación: qué va primero. Cada tema combina un orden con su marco, su alineación, la forma de su
 * título, sus comillas y su «toca para seguir» ([disenoPresentacion]).
 */
private enum class OrdenPresentacion {
    /** Emblema, título, banda y frase. */
    EMBLEMA_ARRIBA,
    /** Título y banda; debajo el emblema y la frase. */
    TITULO_ARRIBA,
    /** Emblema pequeño a un lado del título, como una ficha; la frase debajo. */
    EMBLEMA_AL_LADO,
    /** Título, banda y frase; el emblema al pie, como un sello. */
    EMBLEMA_ABAJO,
    /** La frase primero, como epígrafe; luego el emblema y el título. */
    FRASE_ARRIBA,
}

/**
 * La presentación de un tema: [titulo] es cómo se escribe su nombre; [comillas], cómo abre y cierra la frase;
 * [capitular] agranda la primera letra de la frase (y no lleva comillas); [arriba] y [abajo] dejan aire para el marco;
 * [marco] se dibuja detrás de todo el bloque.
 */
private class DisenoPresentacion(
    val orden: OrdenPresentacion,
    val izquierda: Boolean,
    val titulo: String,
    val tocar: String,
    val comillas: Pair<String, String> = "«" to "»",
    val capitular: Boolean = false,
    val emblema: Dp = 132.dp,
    val arriba: Dp = 34.dp,
    val abajo: Dp = 34.dp,
    val marco: DrawScope.(ColorScheme) -> Unit,
)

/** Estrella de [puntas] puntas para los marcos de la presentación; [dentro] es el radio de los valles respecto al de las puntas. */
private fun DrawScope.estrellaPresentacion(c: Offset, r: Float, color: Color, puntas: Int, dentro: Float) {
    val p = Path()
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * dentro
        val a = Math.toRadians(-90.0 + 180.0 / puntas * i)
        val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    drawPath(p, color)
}

/** La composición de la presentación de cada tema. */
private fun disenoPresentacion(t: TemaId): DisenoPresentacion = when (t) {
    // Lápida: el nombre grabado arriba y todo dentro de una tabula ansata, con sus dos asas en cola de milano.
    TemaId.ROMANO -> DisenoPresentacion(OrdenPresentacion.TITULO_ARRIBA, false, "· " + t.nombre.uppercase() + " ·", "TANGE · ET · PERGE  —  toca y sigue") { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.primary.copy(alpha = 0.6f)
        val x0 = u * 24; val x1 = w - u * 24
        drawRect(c, Offset(x0, 0f), Size(x1 - x0, h), style = Stroke(u * 1.4f))
        drawRect(c.copy(alpha = 0.3f), Offset(x0 + u * 6, u * 6), Size(x1 - x0 - u * 12, h - u * 12), style = Stroke(u * 0.8f))
        listOf(x0 to -1f, x1 to 1f).forEach { (x, s) ->
            drawPath(Path().apply {
                moveTo(x, h / 2 - u * 16); lineTo(x + s * u * 20, h / 2 - u * 28); lineTo(x + s * u * 20, h / 2 + u * 28); lineTo(x, h / 2 + u * 16)
            }, c, style = Stroke(u * 1.4f))
        }
    }
    // La primera plana de la ciudad: la cabecera entre raya gruesa y fina, y abajo el perfil de los tejados al atardecer
    // (cúpulas, campanarios y los pinos piñoneros).
    TemaId.URBE -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, true, t.nombre.uppercase() + " · OGGI", "tocca e vai  —  toca y sigue",
        comillas = "“" to "”", emblema = 112.dp, arriba = 30.dp, abajo = 78.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val tinta = e.onBackground.copy(alpha = 0.7f); val perfil = e.primary.copy(alpha = 0.22f)
        drawLine(tinta, Offset(u * 18, u * 4), Offset(w - u * 18, u * 4), u * 2.6f)
        drawLine(tinta, Offset(u * 18, u * 9), Offset(w - u * 18, u * 9), u * 0.8f)
        val base = h
        drawRect(perfil, Offset(0f, base - u * 14), Size(w, u * 14))
        listOf(0.22f to 26f, 0.62f to 40f).forEach { (fx, r) ->
            val cx = w * fx; val rr = r * u
            drawRect(perfil, Offset(cx - rr, base - u * 14 - rr * 0.5f), Size(2 * rr, rr * 0.5f))
            drawArc(perfil, 180f, 180f, true, Offset(cx - rr, base - u * 14 - rr * 1.5f), Size(2 * rr, 2 * rr))
            drawRect(perfil, Offset(cx - rr * 0.12f, base - u * 14 - rr * 1.85f), Size(rr * 0.24f, rr * 0.4f))
        }
        listOf(0.4f, 0.84f).forEach { fx -> drawRect(perfil, Offset(w * fx, base - u * 70), Size(u * 12, u * 56)); drawPath(Path().apply { moveTo(w * fx - u * 2, base - u * 70); lineTo(w * fx + u * 6, base - u * 82); lineTo(w * fx + u * 14, base - u * 70); close() }, perfil) }
        listOf(0.06f, 0.94f).forEach { fx ->
            drawLine(perfil, Offset(w * fx, base - u * 14), Offset(w * fx + u * 3, base - u * 44), u * 3)
            drawOval(perfil, Offset(w * fx - u * 18, base - u * 58), Size(u * 42, u * 18))
        }
    }
    // Consola: una ventana de terminal con su barra, el emblema como ícono del proceso y la frase como salida.
    TemaId.CIBER -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_AL_LADO, true, "> " + t.nombre.lowercase() + "_", "[ pulsa para continuar ]_",
        comillas = "> " to "", emblema = 84.dp, arriba = 44.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.primary
        drawRect(c.copy(alpha = 0.7f), Offset.Zero, size, style = Stroke(u * 1.2f))
        drawRect(c.copy(alpha = 0.16f), Offset.Zero, Size(w, u * 20))
        drawLine(c.copy(alpha = 0.7f), Offset(0f, u * 20), Offset(w, u * 20), u)
        for (i in 0 until 3) drawRect(if (i == 0) e.secondary else c.copy(alpha = 0.6f), Offset(w - u * (16 + i * 14f), u * 6), Size(u * 8, u * 8))
        drawRect(c.copy(alpha = 0.6f), Offset(u * 8, u * 7), Size(u * 40, u * 6))
        var y = u * 24
        while (y < h) { drawLine(c.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), u * 0.8f); y += u * 4 }
    }
    // Letrero: la frase va primero, como el cartel de la calle; luego el emblema colgado y el nombre en tubo de neón,
    // todo dentro de un marco de tubo magenta con su halo y la llovizna cayendo delante.
    TemaId.NEON -> DisenoPresentacion(OrdenPresentacion.FRASE_ARRIBA, false, t.nombre.uppercase(), "toca para entrar a la calle  ▸",
        comillas = "「" to "」", emblema = 120.dp, arriba = 40.dp, abajo = 40.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.primary
        drawRoundRect(c.copy(alpha = 0.2f), Offset(u * 4, u * 4), Size(w - u * 8, h - u * 8), androidx.compose.ui.geometry.CornerRadius(u * 18), style = Stroke(u * 7))
        drawRoundRect(c, Offset(u * 4, u * 4), Size(w - u * 8, h - u * 8), androidx.compose.ui.geometry.CornerRadius(u * 18), style = Stroke(u * 1.6f))
        drawLine(e.secondary.copy(alpha = 0.6f), Offset(w * 0.3f, h - u * 14), Offset(w * 0.7f, h - u * 14), u * 2)
        val az = java.util.Random(8)
        repeat(60) { val x = az.nextFloat() * w; val y = az.nextFloat() * h; drawLine(Color(0xFF9DB4D8).copy(alpha = 0.09f), Offset(x, y), Offset(x - u * 3, y + u * 12), u * 0.8f) }
    }
    // Templo: frontón con su arquitrabe sobre el emblema, dos columnas estriadas a los lados y las gradas al pie.
    TemaId.HELENO -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, false, t.nombre.uppercase(), "πρόσω  ·  toca para seguir",
        comillas = "“" to "”", arriba = 78.dp, abajo = 40.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.tertiary.copy(alpha = 0.75f); val l = u * 1.4f
        val base = u * 56
        drawPath(Path().apply { moveTo(u * 8, base); lineTo(w / 2, u * 8); lineTo(w - u * 8, base); close() }, c, style = Stroke(l))
        drawPath(Path().apply { moveTo(u * 34, base - u * 5); lineTo(w / 2, u * 20); lineTo(w - u * 34, base - u * 5); close() }, c.copy(alpha = 0.35f), style = Stroke(u * 0.8f))
        drawRect(c, Offset(u * 8, base), Size(w - u * 16, u * 8), style = Stroke(l))
        listOf(u * 12, w - u * 26).forEach { x ->
            drawRect(c, Offset(x, base + u * 8), Size(u * 14, h - base - u * 30), style = Stroke(l))
            for (k in 1..2) drawLine(c.copy(alpha = 0.5f), Offset(x + k * u * 14 / 3, base + u * 12), Offset(x + k * u * 14 / 3, h - u * 26), u * 0.8f)
        }
        listOf(h - u * 22 to u * 6, h - u * 14 to u * 3, h - u * 6 to 0f).forEach { (y, m) -> drawLine(c, Offset(m, y), Offset(w - m, y), l) }
    }
    // El pilono del templo: dos torres en talud con su gola, los mástiles con banderolas y la puerta con el disco alado.
    TemaId.EGIPTO -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, false, t.nombre.uppercase(), "☥  ·  toca para entrar al templo",
        comillas = "«" to "»", arriba = 84.dp, abajo = 40.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.tertiary.copy(alpha = 0.75f); val l = u * 1.4f
        val top = u * 30; val pie = h - u * 8
        val torre = w * 0.36f
        listOf(0f to 1f, w to -1f).forEach { (x0, s) ->
            val xe = x0 + s * u * 6; val xi = x0 + s * torre
            drawPath(Path().apply { moveTo(xe, pie); lineTo(xe + s * u * 14, top); lineTo(xi, top); lineTo(xi, pie); close() }, c, style = Stroke(l))
            drawLine(c, Offset(xe + s * u * 13, top + u * 6), Offset(xi, top + u * 6), u * 0.8f)
            var k = 0f
            while (k < torre - u * 22) { drawLine(c.copy(alpha = 0.4f), Offset(xe + s * (u * 16 + k), top), Offset(xe + s * (u * 16 + k), top + u * 6), u * 0.7f); k += u * 4 }
            listOf(0.35f, 0.7f).forEach { f ->
                val xm = xe + s * (u * 14 + (torre - u * 20) * f)
                drawLine(c.copy(alpha = 0.6f), Offset(xm, pie - u * 10), Offset(xm, top - u * 22), u)
                drawPath(Path().apply { moveTo(xm, top - u * 22); lineTo(xm - s * u * 9, top - u * 18); lineTo(xm, top - u * 14); close() }, e.secondary.copy(alpha = 0.6f))
            }
        }
        val gx = w / 2; val gw = w * 0.26f
        drawRect(c, Offset(gx - gw / 2, top + u * 30), Size(gw, pie - top - u * 30), style = Stroke(l))
        drawCircle(e.secondary.copy(alpha = 0.7f), u * 5, Offset(gx, top + u * 20))
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply {
                moveTo(gx + s * u * 6, top + u * 18); quadraticTo(gx + s * u * 20, top + u * 12, gx + s * gw * 0.5f, top + u * 17)
                lineTo(gx + s * gw * 0.46f, top + u * 21); quadraticTo(gx + s * u * 18, top + u * 21, gx + s * u * 6, top + u * 23); close()
            }, c)
        }
        drawLine(c, Offset(0f, pie), Offset(w, pie), l)
    }
    // Folio de códice: pauta roja de márgenes, anillos de pan de oro donde se cruzan y la frase con capitular.
    TemaId.MEDIEVAL -> DisenoPresentacion(OrdenPresentacion.TITULO_ARRIBA, true, t.nombre, "¶ toca la foja e prosigue", capitular = true, emblema = 112.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val rojo = e.primary.copy(alpha = 0.45f); val oro = e.tertiary
        listOf(u * 18, u * 23).forEach { x ->
            drawLine(rojo, Offset(x, 0f), Offset(x, h), u * 0.8f)
            drawLine(rojo, Offset(w - x, 0f), Offset(w - x, h), u * 0.8f)
        }
        listOf(u * 12, h - u * 12).forEach { y -> drawLine(rojo, Offset(0f, y), Offset(w, y), u * 0.8f) }
        listOf(Offset(u * 20.5f, u * 12), Offset(w - u * 20.5f, u * 12), Offset(u * 20.5f, h - u * 12), Offset(w - u * 20.5f, h - u * 12)).forEach {
            drawCircle(oro, u * 6, it, style = Stroke(u * 1.6f)); drawCircle(oro, u * 2, it)
        }
    }
    // La puerta de la encomienda: el muro almenado arriba, dos estandartes «baucent» a los lados y el lema de la orden al pie.
    TemaId.TEMPLARIO -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, false, t.nombre, "☩ toca para entrar en la casa", comillas = "“" to "”", emblema = 120.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val piedra = e.secondary.copy(alpha = 0.35f)
        var x = 0f; var k = 0
        while (x < w) { drawRect(piedra, Offset(x, 0f), Size(u * 18, if (k % 2 == 0) u * 26 else u * 16)); x += u * 18; k++ }
        drawRect(piedra, Offset(0f, h - u * 10), Size(w, u * 10))
        listOf(u * 26, w - u * 26).forEach { cx ->
            drawLine(e.onBackground.copy(alpha = 0.4f), Offset(cx, u * 40), Offset(cx, u * 150), u * 1.4f)
            drawRect(Color(0xFF1A1816).copy(alpha = 0.85f), Offset(cx, u * 44), Size(u * 18, u * 24))
            drawRect(Color.White.copy(alpha = 0.9f), Offset(cx, u * 68), Size(u * 18, u * 24))
            drawRect(e.onBackground.copy(alpha = 0.3f), Offset(cx, u * 44), Size(u * 18, u * 48), style = Stroke(u * 0.8f))
        }
        drawPath(cruzPatada(Offset(w / 2, h - u * 34), u * 12), e.primary.copy(alpha = 0.7f))
    }
    // Expediente: carpeta con su pestaña, código de archivo y un sello rojo torcido al pie.
    TemaId.DISTOPIA -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_AL_LADO, true, "EXP. " + t.nombre.uppercase(), "TOQUE PARA ACUSAR RECIBO",
        comillas = "“" to "”", emblema = 88.dp, arriba = 52.dp, abajo = 70.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.onSurface.copy(alpha = 0.55f)
        drawPath(Path().apply { moveTo(0f, u * 18); lineTo(u * 10, 0f); lineTo(u * 110, 0f); lineTo(u * 120, u * 18) }, c, style = Stroke(u * 1.3f))
        drawRect(c, Offset(0f, u * 18), Size(w, h - u * 18), style = Stroke(u * 1.3f))
        for (i in 0 until 14) drawRect(c, Offset(w - u * 84 + i * u * 5, u * 26), Size(u * (if (i % 3 == 0) 2.4f else 1.2f), u * 14))
        val centro = Offset(w - u * 62, h - u * 36)
        rotate(-12f, centro) {
            drawRect(e.primary.copy(alpha = 0.7f), Offset(centro.x - u * 46, centro.y - u * 17), Size(u * 92, u * 34), style = Stroke(u * 2.2f))
            drawRect(e.primary.copy(alpha = 0.5f), Offset(centro.x - u * 41, centro.y - u * 12), Size(u * 82, u * 24), style = Stroke(u))
            drawLine(e.primary.copy(alpha = 0.6f), Offset(centro.x - u * 30, centro.y), Offset(centro.x + u * 30, centro.y), u * 3)
        }
    }
    // Compuerta: mampara redondeada con remaches y una franja de peligro al pie.
    TemaId.REFUGIO -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, false, "— " + t.nombre.uppercase() + " —", "PULSE PARA ABRIR LA COMPUERTA",
        arriba = 40.dp, abajo = 50.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.secondary.copy(alpha = 0.8f)
        drawRoundRect(c, Offset(u * 6, u * 6), Size(w - u * 12, h - u * 12), CornerRadius(u * 22), style = Stroke(u * 3))
        drawRoundRect(c.copy(alpha = 0.4f), Offset(u * 12, u * 12), Size(w - u * 24, h - u * 24), CornerRadius(u * 16), style = Stroke(u))
        var x = u * 40
        while (x < w - u * 36) {
            listOf(u * 6, h - u * 6).forEach { y -> drawCircle(c, u * 3, Offset(x, y)); drawCircle(e.background, u * 1.1f, Offset(x - u * 0.8f, y - u * 0.8f)) }
            x += u * 28
        }
        clipRect(u * 40, h - u * 32, w - u * 40, h - u * 22) {
            var s = u * 20
            while (s < w) {
                drawPath(Path().apply { moveTo(s, h - u * 22); lineTo(s + u * 8, h - u * 32); lineTo(s + u * 16, h - u * 32); lineTo(s + u * 8, h - u * 22); close() }, e.tertiary.copy(alpha = 0.8f))
                s += u * 16
            }
        }
    }
    // Placa de acero: chaflanes con pernos de latón y galones carmesí arriba.
    TemaId.ORDEN -> DisenoPresentacion(OrdenPresentacion.TITULO_ARRIBA, false, t.nombre.uppercase(), "« TOCA PARA PROSEGUIR LA MARCHA »",
        arriba = 42.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.onSurfaceVariant.copy(alpha = 0.7f); val k = u * 18
        drawPath(Path().apply { moveTo(k, 0f); lineTo(w - k, 0f); lineTo(w, k); lineTo(w, h - k); lineTo(w - k, h); lineTo(k, h); lineTo(0f, h - k); lineTo(0f, k); close() }, c, style = Stroke(u * 2))
        listOf(Offset(k, k), Offset(w - k, k), Offset(k, h - k), Offset(w - k, h - k)).forEach {
            drawCircle(e.secondary, u * 4, it); drawLine(e.surface, it - Offset(u * 2.5f, 0f), it + Offset(u * 2.5f, 0f), u)
        }
        for (i in 0 until 3) {
            val y = u * (6 + i * 6f)
            drawPath(Path().apply { moveTo(w / 2 - u * 14, y); lineTo(w / 2, y + u * 6); lineTo(w / 2 + u * 14, y) }, e.primary, style = Stroke(u * 2))
        }
    }
    // Hoja de laboratorio: papel milimetrado, etiqueta de muestra y una graduación por el borde.
    TemaId.LABORATORIO -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_AL_LADO, true, "○ " + t.nombre, "toca para cerrar la observación",
        comillas = "“" to "”", emblema = 92.dp, arriba = 46.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val g = e.primary.copy(alpha = 0.08f)
        var x = 0f
        while (x <= w) { drawLine(g, Offset(x, 0f), Offset(x, h), u * 0.7f); x += u * 12 }
        var y = 0f
        while (y <= h) { drawLine(g, Offset(0f, y), Offset(w, y), u * 0.7f); y += u * 12 }
        drawRoundRect(e.primary.copy(alpha = 0.5f), Offset.Zero, size, CornerRadius(u * 14), style = Stroke(u * 1.2f))
        drawRoundRect(e.secondary.copy(alpha = 0.7f), Offset(w - u * 92, u * 10), Size(u * 80, u * 22), CornerRadius(u * 4), style = Stroke(u * 1.2f))
        drawLine(e.secondary.copy(alpha = 0.5f), Offset(w - u * 84, u * 21), Offset(w - u * 32, u * 21), u)
        drawCircle(e.secondary.copy(alpha = 0.7f), u * 4, Offset(w - u * 22, u * 21), style = Stroke(u))
        for (n in 0 until 40) {
            val yy = u * 40 + n * u * 10
            if (yy < h - u * 20) drawLine(e.secondary.copy(alpha = 0.5f), Offset(0f, yy), Offset(if (n % 5 == 0) u * 14 else u * 8, yy), u)
        }
    }
    // Primera plana: cabecera de gaceta entre filetes gruesos y finos, y columnas a los lados.
    TemaId.COLONOS -> DisenoPresentacion(OrdenPresentacion.TITULO_ARRIBA, false, "Gaceta · " + t.nombre, "☞ Pase usted a la página siguiente",
        comillas = "“" to "”", emblema = 116.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.primary.copy(alpha = 0.7f)
        drawLine(c, Offset(0f, u * 4), Offset(w, u * 4), u * 3); drawLine(c, Offset(0f, u * 10), Offset(w, u * 10), u * 0.8f)
        drawLine(c, Offset(0f, h - u * 10), Offset(w, h - u * 10), u * 0.8f); drawLine(c, Offset(0f, h - u * 4), Offset(w, h - u * 4), u * 3)
        listOf(u * 16, w - u * 16).forEach { x -> drawLine(c.copy(alpha = 0.4f), Offset(x, u * 18), Offset(x, h - u * 18), u * 0.8f) }
        listOf(u * 16, w - u * 16).forEach { x ->
            val m = Offset(x, h / 2)
            drawPath(Path().apply { moveTo(m.x, m.y - u * 6); lineTo(m.x + u * 4, m.y); lineTo(m.x, m.y + u * 6); lineTo(m.x - u * 4, m.y); close() }, c)
        }
    }
    // Boletín del Imperio: corona de laurel alrededor del emblema y una fila de estrellas al pie.
    TemaId.NAPOLEON -> {
        val arriba = 34f; val tam = 132f
        DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, false, t.nombre.uppercase(), "En avant !  ·  toca para seguir", emblema = tam.dp, arriba = arriba.dp, abajo = 46.dp) { e ->
            val u = 1.dp.toPx(); val w = size.width; val h = size.height
            val oro = e.tertiary
            val centro = Offset(w / 2, u * (arriba + tam / 2)); val r = u * (tam / 2 + 18)
            listOf(1f, -1f).forEach { s ->
                // Cada rama sube desde abajo por su lado y se queda abierta arriba: a la derecha de 30° a −65°, a la izquierda de 150° a 245°.
                val desde = if (s > 0f) 30f else 150f
                drawArc(oro.copy(alpha = 0.7f), desde, if (s > 0f) -95f else 95f, false, centro - Offset(r, r), Size(r * 2, r * 2), style = Stroke(u * 1.4f))
                for (k in 0 until 6) {
                    val a = Math.toRadians((if (s > 0f) 30.0 - k * 18 else 150.0 + k * 18))
                    val p = Offset(centro.x + r * cos(a).toFloat(), centro.y + r * sin(a).toFloat())
                    rotate(Math.toDegrees(a).toFloat() + 90f + s * 30f, p) { drawOval(oro.copy(alpha = 0.85f), Offset(p.x - u * 3, p.y - u * 8), Size(u * 6, u * 12)) }
                }
            }
            drawRect(oro.copy(alpha = 0.4f), Offset(0f, 0f), size, style = Stroke(u))
            for (i in 0 until 5) estrellaPresentacion(Offset(w / 2 + (i - 2) * u * 22, h - u * 18), u * 6, oro, 5, 0.45f)
        }
    }
    // Lámina de herbario: esquineros, un helecho por el margen y la etiqueta del ejemplar al pie.
    TemaId.BOTANICO -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ABAJO, true, "Lám. I — " + t.nombre, "toca para volver la hoja del herbario",
        emblema = 104.dp, abajo = 62.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.onSurfaceVariant.copy(alpha = 0.5f); val verde = e.secondary.copy(alpha = 0.7f)
        drawRect(c, Offset.Zero, size, style = Stroke(u))
        listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, s) ->
            drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * u * 22, o.y); lineTo(o.x, o.y + s.y * u * 22); close() }, c)
        }
        // Helecho: un raquis curvo con folíolos alternos.
        val x0 = u * 20
        drawPath(Path().apply { moveTo(x0, h - u * 70); quadraticTo(x0 - u * 8, h / 2, x0 + u * 4, u * 40) }, verde, style = Stroke(u * 1.2f, cap = StrokeCap.Round))
        var y = u * 50; var lado = 1f
        while (y < h - u * 80) {
            drawLine(verde, Offset(x0 - u * 2, y), Offset(x0 - u * 2 + lado * u * 9, y - u * 5), u * 1.6f, cap = StrokeCap.Round)
            y += u * 9; lado = -lado
        }
        val et = Offset(w - u * 120, h - u * 50)
        drawRect(c, et, Size(u * 108, u * 38), style = Stroke(u))
        for (i in 0 until 3) drawLine(c, Offset(et.x + u * 8, et.y + u * (10 + i * 9f)), Offset(et.x + u * (if (i == 0) 96f else 70f), et.y + u * (10 + i * 9f)), u * 0.8f)
    }
    // Ciudad verde: el borde de una losa rota, hiedra que cuelga desde arriba y la silueta de los edificios abajo.
    TemaId.RUINAS -> DisenoPresentacion(OrdenPresentacion.TITULO_ARRIBA, true, "Día 1 tras el silencio · " + t.nombre, "toca para seguir caminando",
        comillas = "“" to "”", emblema = 112.dp, arriba = 46.dp, abajo = 70.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.onSurfaceVariant.copy(alpha = 0.45f); val verde = e.primary.copy(alpha = 0.7f)
        drawPath(Path().apply { moveTo(0f, 0f); lineTo(w * 0.7f, 0f); lineTo(w * 0.76f, u * 8); lineTo(w * 0.84f, u * 3); lineTo(w, u * 14); lineTo(w, h); lineTo(0f, h); close() }, c, style = Stroke(u))
        // Hiedra que cuelga del borde de arriba.
        listOf(0.08f to 0.32f, 0.2f to 0.2f, 0.9f to 0.26f).forEach { (fx, largo) ->
            val x0 = w * fx; val y1 = h * largo
            drawLine(verde, Offset(x0, 0f), Offset(x0 + u * 4, y1), u * 1.1f)
            var y = u * 12; var lado = 1f
            while (y < y1) {
                val q = Offset(x0 + u * 4 * (y / y1), y); val r = u * 6
                drawPath(Path().apply { moveTo(q.x, q.y); lineTo(q.x + lado * r, q.y - r * 0.2f); lineTo(q.x + lado * r * 0.7f, q.y + r * 0.6f); close() }, verde)
                y += u * 14; lado = -lado
            }
        }
        // Edificios rotos al pie, con ventanas vacías.
        val pie = Path().apply {
            moveTo(0f, h); lineTo(0f, h - u * 40); lineTo(u * 30, h - u * 46); lineTo(u * 36, h - u * 30); lineTo(u * 60, h - u * 30); lineTo(u * 60, h - u * 58)
            lineTo(u * 78, h - u * 64); lineTo(u * 86, h - u * 50); lineTo(u * 100, h - u * 54); lineTo(u * 100, h - u * 22)
            lineTo(w - u * 90, h - u * 22); lineTo(w - u * 90, h - u * 48); lineTo(w - u * 60, h - u * 48); lineTo(w - u * 52, h - u * 36); lineTo(w - u * 40, h - u * 52); lineTo(w, h - u * 44); lineTo(w, h); close()
        }
        drawPath(pie, c.copy(alpha = 0.18f))
        for (k in 0 until 3) drawRect(e.surface.copy(alpha = 0.7f), Offset(u * (66 + k * 10f), h - u * 46), Size(u * 5, u * 7))
        drawLine(verde, Offset(u * 70, h - u * 22), Offset(u * 70, h - u * 70), u * 1.6f)
        drawCircle(verde.copy(alpha = 0.5f), u * 14, Offset(u * 70, h - u * 76))
    }
    // Ventana de Adviento: la frase colgada bajo una guirnalda con bolas, entre copos de nieve.
    TemaId.NAVIDAD -> DisenoPresentacion(OrdenPresentacion.FRASE_ARRIBA, false, t.nombre, "✦ toca para abrir la siguiente ventanita", arriba = 50.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val verde = e.secondary; val rojo = e.primary; val oro = e.tertiary
        drawRoundRect(oro.copy(alpha = 0.4f), Offset.Zero, size, CornerRadius(u * 12), style = Stroke(u))
        val n = 4; val paso = w / n
        for (i in 0 until n) drawArc(verde, 0f, 180f, false, Offset(i * paso, -u * 14), Size(paso, u * 36), style = Stroke(u * 4, cap = StrokeCap.Round))
        for (i in 1 until n) {
            drawLine(oro, Offset(i * paso, u * 4), Offset(i * paso, u * 16), u)
            drawCircle(if (i % 2 == 0) oro else rojo, u * 5, Offset(i * paso, u * 21))
        }
        listOf(Offset(u * 18, h * 0.4f), Offset(w - u * 20, h * 0.6f), Offset(u * 22, h - u * 28), Offset(w - u * 26, h - u * 22)).forEach { c ->
            for (k in 0 until 3) rotate(k * 60f, c) { drawLine(e.onSurfaceVariant.copy(alpha = 0.5f), c - Offset(0f, u * 6), c + Offset(0f, u * 6), u) }
        }
    }
    // Rollo colgante: dos rodillos de madera, el borde de seda y el emblema al pie como sello de cinabrio.
    TemaId.SEDA -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ABAJO, false, t.nombre, "◇ 前行  ·  toca para que siga la caravana",
        comillas = "「" to "」", emblema = 100.dp, arriba = 44.dp, abajo = 44.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val madera = e.tertiary
        drawRect(e.surface.copy(alpha = 0.55f), Offset(u * 10, u * 12), Size(w - u * 20, h - u * 24))
        drawRect(e.primary.copy(alpha = 0.5f), Offset(u * 18, u * 20), Size(w - u * 36, h - u * 40), style = Stroke(u))
        listOf(0f, h - u * 12).forEach { y ->
            drawRoundRect(madera, Offset(u * 4, y), Size(w - u * 8, u * 12), CornerRadius(u * 6))
            drawCircle(madera, u * 8, Offset(u * 4, y + u * 6)); drawCircle(madera, u * 8, Offset(w - u * 4, y + u * 6))
        }
        drawPath(Path().apply { moveTo(w / 2 - u * 30, 0f); lineTo(w / 2, -u * 18); lineTo(w / 2 + u * 30, 0f) }, madera, style = Stroke(u))
    }
    // Vitral: un arco ojival con su rosetón en la clave.
    TemaId.GOTICO -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, false, t.nombre, "✠ toca, y que se abra la puerta del claustro",
        arriba = 100.dp, abajo = 30.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.tertiary.copy(alpha = 0.75f)
        listOf(0f, u * 7).forEach { m ->
            val s = u * 120 + m; val top = u * 4 + m
            drawPath(Path().apply {
                moveTo(m, h); lineTo(m, s)
                cubicTo(m, s - (s - top) * 0.55f, w / 2 - (w / 2 - m) * 0.35f, top + (s - top) * 0.1f, w / 2, top)
                cubicTo(w / 2 + (w / 2 - m) * 0.35f, top + (s - top) * 0.1f, w - m, s - (s - top) * 0.55f, w - m, s)
                lineTo(w - m, h)
            }, if (m == 0f) c else c.copy(alpha = 0.35f), style = Stroke(if (m == 0f) u * 1.6f else u))
        }
        val r = Offset(w / 2, u * 52)
        drawCircle(c, u * 16, r, style = Stroke(u * 1.2f))
        for (k in 0 until 6) {
            val a = Math.toRadians(k * 60.0)
            drawCircle(c, u * 5.5f, Offset(r.x + u * 9 * cos(a).toFloat(), r.y + u * 9 * sin(a).toFloat()), style = Stroke(u))
        }
        drawLine(c, Offset(0f, h), Offset(w, h), u * 2)
    }
    // El recorte pegado en la libreta: la frase primero, como hallada en un periódico viejo; el borde rasgado, dos tiras de
    // cinta y, al pie, el faro con su haz.
    TemaId.ARCANO -> DisenoPresentacion(OrdenPresentacion.FRASE_ARRIBA, true, "Horror cósmico", "† toca, si te atreves a abrir el archivo",
        comillas = "—" to "", emblema = 118.dp, arriba = 26.dp, abajo = 70.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.onBackground.copy(alpha = 0.45f)
        val borde = Path().apply {
            moveTo(0f, 6 * u); var x = 0f; var k = 0
            while (x < w) { x = minOf(x + 9 * u, w); lineTo(x, if (k % 2 == 0) 2 * u else 7 * u); k++ }
            lineTo(w, h - 52 * u); lineTo(0f, h - 52 * u); close()
        }
        drawPath(borde, e.secondary.copy(alpha = 0.06f))
        drawPath(borde, c, style = Stroke(u))
        listOf(Offset(14 * u, 6 * u) to -32f, Offset(w - 14 * u, 6 * u) to 32f).forEach { (o, g) ->
            rotate(g, o) { drawRect(Color(0xFFE6D9A6).copy(alpha = 0.5f), Offset(o.x - 22 * u, o.y - 7 * u), Size(44 * u, 14 * u)) }
        }
        val f = Offset(w / 2, h - 10 * u)
        drawPath(Path().apply { moveTo(f.x - 6 * u, f.y); lineTo(f.x - 4 * u, f.y - 30 * u); lineTo(f.x + 4 * u, f.y - 30 * u); lineTo(f.x + 6 * u, f.y); close() }, c)
        drawCircle(e.primary.copy(alpha = 0.8f), 3 * u, Offset(f.x, f.y - 34 * u))
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply { moveTo(f.x, f.y - 34 * u); lineTo(f.x + s * w * 0.45f, f.y - 44 * u); lineTo(f.x + s * w * 0.45f, f.y - 26 * u); close() }, e.primary.copy(alpha = 0.08f))
        }
        drawLine(c, Offset(0f, h), Offset(w, h), u)
    }
    // Visor de la sala de control: escuadras, marcas de mira y una órbita que rodea el emblema.
    TemaId.ESPACIO -> {
        val arriba = 40f; val tam = 132f
        DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, false, "◦ " + t.nombre.uppercase() + " ◦", "◦ T–0 · toca para iniciar la secuencia",
            emblema = tam.dp, arriba = arriba.dp) { e ->
            val u = 1.dp.toPx(); val w = size.width; val h = size.height
            val c = e.primary; val l = u * 22
            listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, s) ->
                drawLine(c, o, Offset(o.x + s.x * l, o.y), u * 1.6f); drawLine(c, o, Offset(o.x, o.y + s.y * l), u * 1.6f)
            }
            listOf(Offset(w / 2, 0f) to Offset(0f, 1f), Offset(w / 2, h) to Offset(0f, -1f), Offset(0f, h / 2) to Offset(1f, 0f), Offset(w, h / 2) to Offset(-1f, 0f)).forEach { (o, s) ->
                drawLine(c.copy(alpha = 0.6f), o, Offset(o.x + s.x * u * 10, o.y + s.y * u * 10), u)
            }
            val centro = Offset(w / 2, u * (arriba + tam / 2))
            drawOval(c.copy(alpha = 0.35f), Offset(centro.x - u * 116, centro.y - u * 30), Size(u * 232, u * 60), style = Stroke(u))
            drawCircle(e.secondary, u * 3.5f, Offset(centro.x + u * 100, centro.y - u * 15))
        }
    }
    // La bajada: la medusa arriba, la superficie ondulada, las capas del mar en franjas cada vez más oscuras y la regla de cotas.
    TemaId.ABISMO -> {
        val arriba = 46f; val tam = 128f
        DisenoPresentacion(OrdenPresentacion.EMBLEMA_ARRIBA, false, "▾ " + t.nombre.uppercase() + " ▾", "≈ toca para soltar el lastre y bajar",
            emblema = tam.dp, arriba = arriba.dp) { e ->
            val u = 1.dp.toPx(); val w = size.width; val h = size.height
            val sup = u * 24
            val ola = Path(); var x = 0f
            ola.moveTo(0f, sup)
            while (x <= w) { ola.lineTo(x, sup + u * 4 * kotlin.math.sin(x / (u * 26))); x += u * 3 }
            drawPath(ola, e.primary.copy(alpha = 0.6f), style = Stroke(u * 1.4f))
            for (k in 1..4) drawRect(e.primary.copy(alpha = 0.035f * k), Offset(0f, h * (0.2f + 0.2f * k)), Size(w, h * 0.2f))
            var y = sup + u * 30; var i = 0
            while (y < h - u * 20) {
                drawLine(e.primary.copy(alpha = 0.45f), Offset(u * 12, y), Offset(u * (if (i % 5 == 0) 26 else 18), y), u)
                y += u * 14; i++
            }
            listOf(0.82f to 0.55f, 0.88f to 0.64f, 0.79f to 0.72f, 0.86f to 0.8f).forEachIndexed { j, (fx, fy) ->
                drawCircle(e.secondary.copy(alpha = 0.6f), u * (2.5f + j % 2 * 1.5f), Offset(w * fx, h * fy), style = Stroke(u))
            }
        }
    }
    // Arco de herradura con su alfiz y estrellas de ocho puntas en las enjutas.
    TemaId.ANDALUS -> DisenoPresentacion(OrdenPresentacion.TITULO_ARRIBA, false, t.nombre, "◈ تقدّم  ·  toca para cruzar el arco", arriba = 58.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.tertiary.copy(alpha = 0.75f)
        val r = w / 2 - u * 4; val centro = Offset(w / 2, r + u * 4)
        drawArc(c, 150f, 240f, false, centro - Offset(r, r), Size(r * 2, r * 2), style = Stroke(u * 1.6f))
        val r2 = r - u * 7
        drawArc(c.copy(alpha = 0.4f), 150f, 240f, false, centro - Offset(r2, r2), Size(r2 * 2, r2 * 2), style = Stroke(u))
        // Dovelas: rayas que salen del centro por todo el arco.
        for (k in 0..20) {
            val a = Math.toRadians(150.0 + k * 12)
            val dx = cos(a).toFloat(); val dy = sin(a).toFloat()
            drawLine(c.copy(alpha = 0.4f), Offset(centro.x + dx * r2, centro.y + dy * r2), Offset(centro.x + dx * r, centro.y + dy * r), u * 0.8f)
        }
        val pie = centro.y + r * 0.5f
        val jamba = r * 0.866f
        drawLine(c, Offset(centro.x - jamba, pie), Offset(centro.x - jamba, h), u * 1.6f)
        drawLine(c, Offset(centro.x + jamba, pie), Offset(centro.x + jamba, h), u * 1.6f)
        estrellaPresentacion(Offset(u * 16, u * 16), u * 9, e.primary.copy(alpha = 0.8f), 8, 0.6f)
        estrellaPresentacion(Offset(w - u * 16, u * 16), u * 9, e.primary.copy(alpha = 0.8f), 8, 0.6f)
        // Alfiz: el recuadro que enmarca el arco, abierto abajo para no cruzar la frase.
        drawLine(c.copy(alpha = 0.5f), Offset(0f, 0f), Offset(w, 0f), u)
        drawLine(c.copy(alpha = 0.5f), Offset(0f, 0f), Offset(0f, pie), u)
        drawLine(c.copy(alpha = 0.5f), Offset(w, 0f), Offset(w, pie), u)
    }
    // Una página del códice: el marco rojo con las esquinas escalonadas, el número del día en puntos y barras arriba, la
    // frase con capitular y, al pie, la greca escalonada y el glifo del día como sello.
    TemaId.MESO -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ABAJO, false, t.nombre, "⁘  ·  toca para desplegar el códice",
        capitular = true, emblema = 110.dp, arriba = 52.dp, abajo = 44.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val rojo = e.primary.copy(alpha = 0.7f); val g = 9 * u
        drawPath(Path().apply {
            moveTo(2 * g, 0f); lineTo(w - 2 * g, 0f); lineTo(w - 2 * g, g); lineTo(w - g, g); lineTo(w - g, 2 * g); lineTo(w, 2 * g); lineTo(w, h)
            lineTo(0f, h); lineTo(0f, 2 * g); lineTo(g, 2 * g); lineTo(g, g); lineTo(2 * g, g); close()
        }, rojo, style = Stroke(1.6f * u))
        drawLine(rojo.copy(alpha = 0.45f), Offset(3 * g, 6 * u), Offset(w - 3 * g, 6 * u), u)
        val (n, _) = Maya.tzolkinPartes(java.time.LocalDate.now())
        numeralMaya(n, Offset(2.4f * g, 3.4f * g), 14 * u, e.tertiary)
        numeralMaya(n, Offset(w - 2.4f * g, 3.4f * g), 14 * u, e.tertiary)
        grecaEscalonada(10 * u, w - 10 * u, h - 22 * u, h - 10 * u, rojo, 1.3f * u)
    }
    // Ger: la cúpula con su corona (toono) arriba, la frase bajo ella y la celosía (khana) al pie.
    TemaId.MONGOL -> DisenoPresentacion(OrdenPresentacion.FRASE_ARRIBA, false, t.nombre.uppercase(), "▹ УРАГШАА  ·  toca para levantar el campamento",
        arriba = 72.dp, abajo = 56.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.primary.copy(alpha = 0.75f)
        drawPath(Path().apply { moveTo(0f, u * 64); quadraticTo(w / 2, -u * 40, w, u * 64) }, c, style = Stroke(u * 1.6f))
        val toono = Offset(w / 2, u * 13)
        drawCircle(e.background, u * 10, toono); drawCircle(c, u * 10, toono, style = Stroke(u * 1.4f))
        for (k in 0 until 8) {
            val a = Math.toRadians(k * 45.0)
            drawLine(c.copy(alpha = 0.6f), toono, Offset(toono.x + u * 10 * cos(a).toFloat(), toono.y + u * 10 * sin(a).toFloat()), u)
        }
        drawLine(c, Offset(0f, u * 64), Offset(0f, h), u * 1.6f); drawLine(c, Offset(w, u * 64), Offset(w, h), u * 1.6f)
        drawLine(c, Offset(0f, h), Offset(w, h), u * 1.6f)
        clipRect(u * 4, h - u * 40, w - u * 4, h - u * 8) {
            var x = -u * 32
            while (x < w + u * 32) {
                drawLine(c.copy(alpha = 0.4f), Offset(x, h - u * 40), Offset(x + u * 32, h - u * 8), u)
                drawLine(c.copy(alpha = 0.4f), Offset(x + u * 32, h - u * 40), Offset(x, h - u * 8), u)
                x += u * 16
            }
        }
    }
    // Página de cuaderno: líneas de construcción que se pasan de las esquinas y el círculo de compás alrededor del emblema.
    TemaId.RENACIMIENTO -> {
        val arriba = 40f; val tam = 96f
        DisenoPresentacion(OrdenPresentacion.EMBLEMA_AL_LADO, true, t.nombre, "✎ tocca e prosegui  ·  toca para seguir", emblema = tam.dp, arriba = arriba.dp) { e ->
            val u = 1.dp.toPx(); val w = size.width; val h = size.height
            val c = e.primary.copy(alpha = 0.4f)
            listOf(u * 10, h - u * 10).forEach { y -> drawLine(c, Offset(-u * 6, y), Offset(w + u * 6, y), u * 0.8f) }
            listOf(u * 10, w - u * 10).forEach { x -> drawLine(c, Offset(x, -u * 6), Offset(x, h + u * 6), u * 0.8f) }
            drawLine(c.copy(alpha = 0.25f), Offset(u * 10, u * 10), Offset(w - u * 10, h - u * 10), u * 0.6f)
            drawLine(c.copy(alpha = 0.25f), Offset(w - u * 10, u * 10), Offset(u * 10, h - u * 10), u * 0.6f)
            val centro = Offset(u * (40 + tam / 2), u * (arriba + tam / 2))
            drawCircle(c, u * (tam / 2 + 14), centro, style = Stroke(u * 0.9f))
            drawLine(c, Offset(centro.x - u * (tam / 2 + 22), centro.y), Offset(centro.x + u * (tam / 2 + 22), centro.y), u * 0.6f)
            drawLine(c, Offset(centro.x, centro.y - u * (tam / 2 + 22)), Offset(centro.x, centro.y + u * (tam / 2 + 22)), u * 0.6f)
            drawCircle(c, u * 2, centro)
            // Un arco de la espiral áurea en la esquina de abajo.
            drawArc(e.secondary.copy(alpha = 0.45f), 0f, 90f, false, Offset(w - u * 130, h - u * 140), Size(u * 120, u * 120), style = Stroke(u))
        }
    }
    // La carta del tesoro: las líneas de rumbo que salen del emblema como de una rosa, el mar abajo con sus olas, y la
    // derrota punteada que cruza la hoja hasta la «X».
    TemaId.PIRATA -> {
        val arriba = 44f; val tam = 100f
        DisenoPresentacion(OrdenPresentacion.EMBLEMA_AL_LADO, false, t.nombre, "✷ leven anclas  ·  toca para zarpar", emblema = tam.dp, arriba = arriba.dp) { e ->
            val u = 1.dp.toPx(); val w = size.width; val h = size.height
            val c = e.onBackground.copy(alpha = 0.18f)
            val centro = Offset(u * (40 + tam / 2), u * (arriba + tam / 2))
            for (k in 0 until 16) { val a = k * Math.PI / 8; drawLine(c, centro, Offset(centro.x + (w * 1.5f * kotlin.math.cos(a)).toFloat(), centro.y + (w * 1.5f * kotlin.math.sin(a)).toFloat()), u * 0.6f) }
            drawCircle(e.primary.copy(alpha = 0.35f), u * (tam / 2 + 12), centro, style = Stroke(u))
            for (k in 0 until 3) {
                val y0 = h - u * (18 + k * 16); val p = Path(); var x = -u * 10; p.moveTo(x, y0)
                while (x < w + u * 10) { p.quadraticTo(x + u * 12, y0 - u * 5, x + u * 24, y0); p.quadraticTo(x + u * 36, y0 + u * 5, x + u * 48, y0); x += u * 48 }
                drawPath(p, e.secondary.copy(alpha = 0.3f - k * 0.07f), style = Stroke(u))
            }
            val x = Offset(w - u * 46, h - u * 92); val s = u * 8
            drawPath(Path().apply { moveTo(u * 30, h - u * 70); cubicTo(w * 0.3f, h - u * 150, w * 0.6f, h - u * 40, x.x - s * 1.5f, x.y + s) }, e.tertiary.copy(alpha = 0.55f),
                style = Stroke(u * 1.6f, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(u * 0.1f, u * 6))))
            drawLine(e.tertiary.copy(alpha = 0.8f), x - Offset(s, s), x + Offset(s, s), u * 2.6f, StrokeCap.Round)
            drawLine(e.tertiary.copy(alpha = 0.8f), x + Offset(-s, s), x + Offset(s, -s), u * 2.6f, StrokeCap.Round)
        }
    }
    // Cartel constructivista: cuña roja arriba, barra negra a un lado y una diagonal con su círculo al pie.
    TemaId.URSS -> DisenoPresentacion(OrdenPresentacion.TITULO_ARRIBA, true, t.nombre.uppercase(), "ВПЕРЁД!  ·  toca para seguir", emblema = 120.dp, abajo = 50.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        drawPath(Path().apply { moveTo(w, 0f); lineTo(w, u * 120); lineTo(w - u * 90, 0f); close() }, e.primary.copy(alpha = 0.9f))
        drawRect(e.onSurface, Offset(0f, u * 20), Size(u * 8, h * 0.6f))
        drawLine(e.onSurface.copy(alpha = 0.8f), Offset(0f, h - u * 6), Offset(w, h - u * 30), u * 3)
        drawCircle(e.primary, u * 12, Offset(w - u * 26, h - u * 16))
    }
    // Cartel de la plaza: banda tricolor arriba y abajo, filete de imprenta y la escarapela en el centro de la banda.
    TemaId.REVOLUCION -> DisenoPresentacion(OrdenPresentacion.FRASE_ARRIBA, false, t.nombre.uppercase(), "Marchons, citoyen  ·  toca para seguir",
        arriba = 46.dp, abajo = 40.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val colores = listOf(e.secondary, Color.White, e.primary)
        colores.forEachIndexed { i, col -> drawRect(col, Offset(i * w / 3, 0f), Size(w / 3, u * 8)) }
        colores.reversed().forEachIndexed { i, col -> drawRect(col, Offset(i * w / 3, h - u * 8), Size(w / 3, u * 8)) }
        drawRect(e.onSurface.copy(alpha = 0.6f), Offset(0f, u * 8), Size(w, h - u * 16), style = Stroke(u * 1.4f))
        drawRect(e.onSurface.copy(alpha = 0.3f), Offset(u * 6, u * 14), Size(w - u * 12, h - u * 28), style = Stroke(u * 0.7f))
        val c = Offset(w / 2, u * 8)
        drawCircle(e.secondary, u * 14, c); drawCircle(Color.White, u * 9, c); drawCircle(e.primary, u * 4.5f, c)
    }
    // Losa de las tierras antiguas: esquinas talladas, muescas rúnicas por el borde y una gema engastada arriba.
    TemaId.ANTIGUAS -> DisenoPresentacion(OrdenPresentacion.EMBLEMA_ABAJO, false, t.nombre, "◆ toca para seguir el camino", emblema = 112.dp, arriba = 46.dp) { e ->
        val u = 1.dp.toPx(); val w = size.width; val h = size.height
        val c = e.secondary.copy(alpha = 0.7f); val k = u * 12
        drawPath(Path().apply { moveTo(k, 0f); lineTo(w - k, 0f); lineTo(w, k); lineTo(w, h - k); lineTo(w - k, h); lineTo(k, h); lineTo(0f, h - k); lineTo(0f, k); close() }, c, style = Stroke(u * 1.6f))
        // Muescas: cada una es una runa inventada de dos trazos.
        var x = u * 30; var n = 0
        while (x < w - u * 30) {
            listOf(u * 8, h - u * 20).forEach { y ->
                drawLine(c, Offset(x, y), Offset(x, y + u * 12), u * 1.2f)
                when (n % 3) {
                    0 -> drawLine(c, Offset(x, y + u * 2), Offset(x + u * 5, y + u * 6), u * 1.2f)
                    1 -> drawLine(c, Offset(x, y + u * 6), Offset(x - u * 5, y + u * 10), u * 1.2f)
                    else -> { drawLine(c, Offset(x, y + u * 3), Offset(x + u * 4, y), u * 1.2f); drawLine(c, Offset(x, y + u * 3), Offset(x - u * 4, y), u * 1.2f) }
                }
            }
            x += u * 22; n++
        }
        val g = Offset(w / 2, 0f)
        drawPath(Path().apply { moveTo(g.x, g.y - u * 11); lineTo(g.x + u * 8, g.y); lineTo(g.x, g.y + u * 11); lineTo(g.x - u * 8, g.y); close() }, e.tertiary)
        drawPath(Path().apply { moveTo(g.x, g.y - u * 11); lineTo(g.x + u * 8, g.y); lineTo(g.x, g.y + u * 11); lineTo(g.x - u * 8, g.y); close() }, c, style = Stroke(u))
        drawLine(Color.White.copy(alpha = 0.6f), Offset(g.x - u * 3, g.y - u * 4), Offset(g.x, g.y - u * 8), u)
    }
}

@Composable
fun CapaPresentacionTema() {
    val t = PresentacionTema.tema ?: return
    val vuelta = PresentacionTema.vuelta
    val f = PresentacionTema.frase
    val p = remember(vuelta) { Animatable(if (Repo.config.animaciones) 0f else 1f) }
    val e = MaterialTheme.colorScheme
    LaunchedEffect(vuelta) {
        if (!Repo.config.animaciones) return@LaunchedEffect
        delay(250)
        p.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        delay(7000)
        p.animateTo(0f, tween(600))
        if (PresentacionTema.vuelta == vuelta) PresentacionTema.cerrar()
    }
    val d = disenoPresentacion(t)
    val alinear = if (d.izquierda) Alignment.Start else Alignment.CenterHorizontally
    val alineaTexto = if (d.izquierda) TextAlign.Start else TextAlign.Center
    val emblema: @Composable (Modifier) -> Unit = { m ->
        Box(m.graphicsLayer { val s = 0.6f + 0.4f * p.value; scaleX = s; scaleY = s }) { Emblema(t, d.emblema, e, animado = Repo.config.animaciones) }
    }
    val titulo: @Composable (Modifier) -> Unit = { m ->
        Text(d.titulo, style = MaterialTheme.typography.headlineMedium, color = e.primary, textAlign = alineaTexto, modifier = m)
    }
    val banda: @Composable () -> Unit = { Meandro(Modifier.width(200.dp).padding(top = 8.dp, bottom = 18.dp)) }
    val frase: @Composable (Modifier) -> Unit = { m ->
        f?.let { fr ->
            Column(m, horizontalAlignment = alinear) {
                fr.original?.let { Text(it, style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic), color = e.onSurfaceVariant, textAlign = alineaTexto, modifier = Modifier.padding(bottom = 8.dp)) }
                val cuerpo = if (d.capitular && fr.texto.isNotEmpty()) buildAnnotatedString {
                    // La capitular del copista: la primera letra grande y en rojo.
                    withStyle(SpanStyle(color = e.primary, fontSize = 34.sp, fontWeight = FontWeight.SemiBold)) { append(fr.texto.take(1)) }
                    append(fr.texto.drop(1))
                } else AnnotatedString(d.comillas.first + fr.texto + d.comillas.second)
                Text(cuerpo, style = MaterialTheme.typography.titleMedium, color = e.onBackground, textAlign = alineaTexto)
                Text("— " + fr.autor, style = MaterialTheme.typography.labelMedium, color = e.onSurfaceVariant, textAlign = alineaTexto, modifier = Modifier.padding(top = 10.dp))
            }
        }
    }
    Box(Modifier.fillMaxSize().graphicsLayer { alpha = p.value }.background(e.background.copy(alpha = 0.95f))
        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { PresentacionTema.cerrar() }) {
        CapaFiesta()
        Column(
            Modifier.align(Alignment.Center).fillMaxWidth().padding(horizontal = 22.dp).drawBehind { d.marco.invoke(this, e) }
                .padding(start = 40.dp, end = 40.dp, top = d.arriba, bottom = d.abajo),
            horizontalAlignment = alinear,
        ) {
            when (d.orden) {
                OrdenPresentacion.EMBLEMA_ARRIBA -> {
                    emblema(Modifier)
                    titulo(Modifier.padding(top = 22.dp))
                    banda()
                    frase(Modifier)
                }
                OrdenPresentacion.TITULO_ARRIBA -> {
                    titulo(Modifier)
                    banda()
                    emblema(Modifier.padding(bottom = 18.dp))
                    frase(Modifier)
                }
                OrdenPresentacion.EMBLEMA_AL_LADO -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        emblema(Modifier)
                        titulo(Modifier.weight(1f).padding(start = 16.dp))
                    }
                    banda()
                    frase(Modifier)
                }
                OrdenPresentacion.EMBLEMA_ABAJO -> {
                    titulo(Modifier)
                    banda()
                    frase(Modifier)
                    emblema(Modifier.padding(top = 22.dp))
                }
                OrdenPresentacion.FRASE_ARRIBA -> {
                    frase(Modifier)
                    banda()
                    emblema(Modifier)
                    titulo(Modifier.padding(top = 18.dp))
                }
            }
            Text(t.descripcion, style = MaterialTheme.typography.bodySmall, color = e.onSurfaceVariant, textAlign = alineaTexto, modifier = Modifier.padding(top = 26.dp))
            Text(d.tocar, style = MaterialTheme.typography.labelSmall, color = e.outline, textAlign = alineaTexto, modifier = Modifier.padding(top = 14.dp))
        }
    }
}

/** Tonos de llamada y de notificación del tema para todo el teléfono. */
object TonosTelefono {
    /** Copia el tono a la carpeta de tonos del teléfono (Ringtones/Vestigium o Notifications/Vestigium) y devuelve su dirección. */
    fun instalar(ctx: Context, t: TemaId, llamada: Boolean): Uri? = runCatching {
        val archivo = "vestigium_${t.id}_${if (llamada) "llamada" else "notificacion"}.ogg"
        val col = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        ctx.contentResolver.query(col, arrayOf(MediaStore.Audio.Media._ID), "${MediaStore.Audio.Media.DISPLAY_NAME} = ?", arrayOf(archivo), null)?.use { c ->
            if (c.moveToFirst()) return@runCatching ContentUris.withAppendedId(col, c.getLong(0))
        }
        val v = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, archivo)
            put(MediaStore.Audio.Media.TITLE, "Vestigium · ${t.nombre}")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/ogg")
            put(MediaStore.Audio.Media.RELATIVE_PATH, (if (llamada) Environment.DIRECTORY_RINGTONES else Environment.DIRECTORY_NOTIFICATIONS) + "/Vestigium")
            put(MediaStore.Audio.Media.IS_RINGTONE, llamada); put(MediaStore.Audio.Media.IS_NOTIFICATION, !llamada)
            put(MediaStore.Audio.Media.IS_ALARM, false); put(MediaStore.Audio.Media.IS_MUSIC, false)
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
        val uri = ctx.contentResolver.insert(col, v) ?: return@runCatching null
        ctx.contentResolver.openOutputStream(uri)?.use { out -> ctx.resources.openRawResource(if (llamada) Musica.llamada(t) else Musica.notificacion(t)).use { it.copyTo(out) } }
        ctx.contentResolver.update(uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }, null, null)
        uri
    }.getOrNull()

    fun puedeCambiar(ctx: Context): Boolean = Settings.System.canWrite(ctx)

    /** Android pide un permiso aparte («modificar ajustes del sistema») para cambiar los tonos predeterminados. */
    fun pedirPermiso(ctx: Context) {
        runCatching { ctx.startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + ctx.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    /** Pone el tono del tema como predeterminado del teléfono. Los avisos de la app siguen con su propio sonido. */
    fun poner(ctx: Context, t: TemaId, llamada: Boolean): String {
        if (!puedeCambiar(ctx)) { pedirPermiso(ctx); return "Activa «Modificar ajustes del sistema» para Vestigium Mei y vuelve a tocar el botón." }
        val uri = instalar(ctx, t, llamada) ?: return "No pude guardar el tono en el teléfono."
        return runCatching {
            RingtoneManager.setActualDefaultRingtoneUri(ctx, if (llamada) RingtoneManager.TYPE_RINGTONE else RingtoneManager.TYPE_NOTIFICATION, uri)
            // Para que los avisos de la app nunca suenen igual que el resto del teléfono.
            if (!llamada && !Repo.config.sonido_avisos) Repo.cambiarConfig { it.copy(sonido_avisos = true) }
            if (llamada) "Listo: tu tono de llamada ahora es el de ${t.nombre}." else "Listo: las notificaciones del teléfono suenan con ${t.nombre}; las de Vestigium Mei, con su propio sonido."
        }.getOrElse { "El teléfono no dejó cambiar el tono." }
    }
}
