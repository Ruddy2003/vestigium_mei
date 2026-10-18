package io.github.ruddy2003.vestigium.ui

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Repo

/**
 * Vibraciones y sonidos de cada tema. Cada evento tiene su propio patrón: el golpe de cincel romano,
 * el sello de cera del scriptorium, el sello de goma del Ministerio, el relé del refugio…
 * Los sonidos están sintetizados para esta app y solo suenan con el teléfono en modo normal (no en silencio ni vibración).
 */
object Sentidos {
    enum class Evento { TOQUE, ELEGIR, GUARDAR, LOGRO, PASAR, CAMBIO, ERROR, ABRIR }

    /** Un golpe de vibración: primitiva, intensidad (0–1) y espera en ms antes de él. */
    private class Golpe(val prim: Int, val fuerza: Float, val espera: Int = 0)

    @SuppressLint("InlinedApi") private const val CLICK = VibrationEffect.Composition.PRIMITIVE_CLICK
    @SuppressLint("InlinedApi") private const val THUD = VibrationEffect.Composition.PRIMITIVE_THUD
    @SuppressLint("InlinedApi") private const val SPIN = VibrationEffect.Composition.PRIMITIVE_SPIN
    @SuppressLint("InlinedApi") private const val SUBE = VibrationEffect.Composition.PRIMITIVE_QUICK_RISE
    @SuppressLint("InlinedApi") private const val SUBE_LENTO = VibrationEffect.Composition.PRIMITIVE_SLOW_RISE
    @SuppressLint("InlinedApi") private const val BAJA = VibrationEffect.Composition.PRIMITIVE_QUICK_FALL
    @SuppressLint("InlinedApi") private const val TICK = VibrationEffect.Composition.PRIMITIVE_TICK
    @SuppressLint("InlinedApi") private const val TICK_GRAVE = VibrationEffect.Composition.PRIMITIVE_LOW_TICK

    private fun g(p: Int, f: Float, e: Int = 0) = Golpe(p, f, e)

    private fun patron(tema: TemaId, e: Evento): List<Golpe> = when (e) {
        Evento.ELEGIR -> when (tema) {
            TemaId.CIBER -> listOf(g(TICK, 0.8f))
            TemaId.DISTOPIA -> listOf(g(CLICK, 0.45f))
            TemaId.ORDEN, TemaId.MEDIEVAL, TemaId.COLONOS -> listOf(g(TICK_GRAVE, 0.7f))
            else -> listOf(g(TICK, 0.5f))
        }
        Evento.ERROR -> listOf(g(BAJA, 0.8f), g(CLICK, 0.6f, 60))
        Evento.ABRIR -> when (tema) {
            TemaId.CIBER, TemaId.ESPACIO, TemaId.LABORATORIO -> listOf(g(SUBE, 0.5f), g(TICK, 0.7f, 30))
            TemaId.DISTOPIA, TemaId.URSS, TemaId.ORDEN -> listOf(g(CLICK, 0.8f), g(THUD, 0.6f, 50))
            else -> listOf(g(SUBE_LENTO, 0.45f), g(TICK_GRAVE, 0.6f, 20))
        }
        Evento.CAMBIO -> when (tema) {
            TemaId.HELENO -> listOf(g(SPIN, 0.6f))
            TemaId.REFUGIO -> listOf(g(CLICK, 0.7f), g(SUBE_LENTO, 0.5f, 30))
            else -> listOf(g(SUBE_LENTO, 0.55f), g(CLICK, 0.7f, 30))
        }
        else -> when (tema) {
            // Cincel sobre mármol.
            TemaId.ROMANO -> when (e) {
                Evento.TOQUE -> listOf(g(CLICK, 0.5f)); Evento.PASAR -> listOf(g(TICK, 0.6f))
                Evento.GUARDAR -> listOf(g(THUD, 0.8f), g(CLICK, 0.6f, 60))
                else -> listOf(g(CLICK, 0.4f), g(CLICK, 0.6f, 70), g(CLICK, 0.9f, 70))
            }
            // Ráfagas digitales.
            TemaId.CIBER -> when (e) {
                Evento.TOQUE -> listOf(g(TICK, 1f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.8f), g(TICK_GRAVE, 0.8f, 25))
                Evento.GUARDAR -> listOf(g(TICK, 1f), g(TICK, 1f, 20), g(CLICK, 0.8f, 20))
                else -> listOf(g(SUBE, 0.8f), g(CLICK, 1f, 10), g(TICK, 1f, 40), g(TICK, 1f, 30))
            }
            // Cuerda de lira y el sol que gira.
            TemaId.HELENO -> when (e) {
                Evento.TOQUE -> listOf(g(TICK, 0.7f)); Evento.PASAR -> listOf(g(BAJA, 0.5f))
                Evento.GUARDAR -> listOf(g(SUBE_LENTO, 0.6f), g(CLICK, 0.5f, 10))
                else -> listOf(g(SPIN, 0.7f), g(CLICK, 0.6f, 60))
            }
            // Pluma, página y sello de cera.
            TemaId.MEDIEVAL -> when (e) {
                Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.8f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.6f), g(TICK, 0.3f, 40))
                Evento.GUARDAR -> listOf(g(SUBE, 0.3f), g(THUD, 1f, 20))
                else -> listOf(g(THUD, 0.8f), g(THUD, 0.45f, 300))
            }
            // Máquina de escribir y sello de goma.
            TemaId.DISTOPIA -> when (e) {
                Evento.TOQUE -> listOf(g(CLICK, 1f)); Evento.PASAR -> listOf(g(CLICK, 0.6f))
                Evento.GUARDAR -> listOf(g(THUD, 1f), g(CLICK, 1f, 70))
                else -> listOf(g(CLICK, 0.8f), g(CLICK, 0.8f, 80), g(CLICK, 0.8f, 80), g(CLICK, 0.8f, 80), g(THUD, 0.9f, 120))
            }
            // Relé y contador Geiger.
            TemaId.REFUGIO -> when (e) {
                Evento.TOQUE -> listOf(g(TICK, 0.9f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.7f))
                Evento.GUARDAR -> listOf(g(CLICK, 0.7f), g(CLICK, 0.7f, 35), g(SUBE, 0.5f, 40))
                else -> listOf(g(TICK, 0.9f), g(TICK, 0.9f, 30), g(TICK, 0.9f, 80), g(TICK, 0.9f, 20), g(TICK, 0.9f, 120), g(TICK, 0.9f, 40), g(SUBE, 0.6f, 80))
            }
            // Servo y pisada de armadura.
            TemaId.ORDEN -> when (e) {
                Evento.TOQUE -> listOf(g(TICK_GRAVE, 1f)); Evento.PASAR -> listOf(g(SUBE, 0.4f))
                Evento.GUARDAR -> listOf(g(SUBE_LENTO, 0.8f), g(THUD, 1f, 10))
                else -> listOf(g(THUD, 1f), g(THUD, 1f, 180), g(THUD, 1f, 180))
            }
            // Limpio y suave.
            TemaId.LABORATORIO -> when (e) {
                Evento.TOQUE -> listOf(g(TICK, 0.4f)); Evento.PASAR -> listOf(g(TICK, 0.3f))
                Evento.GUARDAR -> listOf(g(CLICK, 0.4f), g(SUBE, 0.3f, 20))
                else -> listOf(g(SUBE, 0.5f), g(BAJA, 0.5f, 40))
            }
            // Nudillos sobre madera.
            TemaId.COLONOS -> when (e) {
                Evento.TOQUE -> listOf(g(CLICK, 0.6f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.7f))
                Evento.GUARDAR -> listOf(g(THUD, 0.9f), g(THUD, 0.9f, 110))
                else -> listOf(g(CLICK, 0.5f), g(CLICK, 0.5f, 90), g(CLICK, 0.5f, 90), g(THUD, 0.9f, 120))
            }
            // Tambor de marcha y cañonazo lejano.
            TemaId.NAPOLEON -> when (e) {
                Evento.TOQUE -> listOf(g(CLICK, 0.7f)); Evento.PASAR -> listOf(g(TICK, 0.6f), g(TICK, 0.6f, 50))
                Evento.GUARDAR -> listOf(g(CLICK, 0.6f), g(CLICK, 0.6f, 60), g(THUD, 1f, 60))
                else -> listOf(g(CLICK, 0.5f), g(CLICK, 0.5f, 50), g(CLICK, 0.5f, 50), g(CLICK, 0.5f, 50), g(THUD, 1f, 160), g(THUD, 0.6f, 240))
            }
            // Hojas que se rozan y un pájaro que canta.
            TemaId.BOTANICO -> when (e) {
                Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.5f)); Evento.PASAR -> listOf(g(SUBE, 0.3f), g(BAJA, 0.3f, 20))
                Evento.GUARDAR -> listOf(g(SUBE_LENTO, 0.5f), g(CLICK, 0.4f, 30))
                else -> listOf(g(TICK, 0.6f), g(TICK, 0.8f, 70), g(TICK, 0.6f, 50), g(SUBE, 0.5f, 60))
            }
            // Órgano y campanas de la capilla.
            TemaId.GOTICO -> when (e) {
                Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.6f)); Evento.PASAR -> listOf(g(SUBE_LENTO, 0.35f))
                Evento.GUARDAR -> listOf(g(THUD, 0.8f), g(SUBE_LENTO, 0.5f, 40))
                else -> listOf(g(THUD, 0.9f), g(THUD, 0.6f, 260), g(THUD, 0.4f, 260))
            }
            // Pitidos de la sala de control.
            TemaId.ESPACIO -> when (e) {
                Evento.TOQUE -> listOf(g(TICK, 0.8f)); Evento.PASAR -> listOf(g(SUBE, 0.5f))
                Evento.GUARDAR -> listOf(g(TICK, 0.9f), g(TICK, 0.9f, 60), g(SUBE_LENTO, 0.6f, 40))
                else -> listOf(g(SUBE_LENTO, 0.9f), g(CLICK, 1f, 10), g(SPIN, 0.5f, 60))
            }
            // Laúd y darbuka: dum, tek, tek.
            TemaId.ANDALUS -> when (e) {
                Evento.TOQUE -> listOf(g(CLICK, 0.5f)); Evento.PASAR -> listOf(g(TICK, 0.5f), g(TICK_GRAVE, 0.5f, 60))
                Evento.GUARDAR -> listOf(g(THUD, 0.8f), g(TICK, 0.6f, 90), g(TICK, 0.6f, 90))
                else -> listOf(g(THUD, 0.9f), g(TICK, 0.7f, 120), g(TICK, 0.7f, 60), g(THUD, 0.8f, 120), g(SPIN, 0.4f, 80))
            }
            // Cascos al galope y el tambor de la horda.
            TemaId.MONGOL -> when (e) {
                Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.8f)); Evento.PASAR -> listOf(g(CLICK, 0.6f), g(CLICK, 0.6f, 50), g(CLICK, 0.8f, 50))
                Evento.GUARDAR -> listOf(g(THUD, 0.9f), g(THUD, 0.5f, 70))
                else -> listOf(g(CLICK, 0.6f), g(CLICK, 0.6f, 50), g(CLICK, 0.8f, 50), g(CLICK, 0.6f, 140), g(CLICK, 0.6f, 50), g(CLICK, 0.8f, 50), g(THUD, 1f, 140))
            }
            // Pluma que raspa el papel y un laúd.
            TemaId.RENACIMIENTO -> when (e) {
                Evento.TOQUE -> listOf(g(TICK, 0.5f)); Evento.PASAR -> listOf(g(BAJA, 0.4f))
                Evento.GUARDAR -> listOf(g(SUBE, 0.3f), g(CLICK, 0.6f, 30))
                else -> listOf(g(SUBE_LENTO, 0.5f), g(TICK, 0.7f, 40), g(TICK, 0.7f, 80), g(TICK, 0.9f, 80))
            }
            // Cascabeles.
            TemaId.NAVIDAD -> when (e) {
                Evento.TOQUE -> listOf(g(TICK, 0.7f)); Evento.PASAR -> listOf(g(TICK, 0.5f), g(TICK, 0.4f, 30))
                Evento.GUARDAR -> listOf(g(TICK, 0.8f), g(TICK, 0.7f, 35), g(TICK, 0.8f, 35), g(CLICK, 0.6f, 50))
                else -> listOf(g(SUBE, 0.6f), g(TICK, 0.9f, 30), g(TICK, 0.8f, 40), g(TICK, 0.9f, 40), g(TICK, 0.8f, 40), g(CLICK, 0.7f, 60))
            }
            // Gong y pasos de camello.
            TemaId.SEDA -> when (e) {
                Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.7f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.5f), g(TICK_GRAVE, 0.4f, 160))
                Evento.GUARDAR -> listOf(g(THUD, 0.8f), g(SPIN, 0.35f, 30))
                else -> listOf(g(THUD, 1f), g(SPIN, 0.5f, 40), g(SUBE_LENTO, 0.4f, 200))
            }
            // Prensa de fábrica y sirena.
            TemaId.URSS -> when (e) {
                Evento.TOQUE -> listOf(g(CLICK, 0.9f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.9f))
                Evento.GUARDAR -> listOf(g(THUD, 1f), g(THUD, 1f, 90))
                else -> listOf(g(THUD, 1f), g(THUD, 1f, 120), g(THUD, 1f, 120), g(SUBE_LENTO, 0.8f, 120))
            }
        }
    }

    /** Vibra y, si toca, suena. Nunca falla: si el teléfono no puede, simplemente no pasa nada. */
    fun sentir(ctx: Context, e: Evento) {
        val c = Repo.config
        val tema = Estilo.tema
        if (c.vibracion) runCatching { vibrar(ctx, patron(tema, e)) }
        if (c.sonidos) runCatching { sonar(ctx, tema, e) }
    }

    // ---------------------------------------------------------------- vibración

    private fun vibrador(ctx: Context): Vibrator? =
        if (Build.VERSION.SDK_INT >= 31) ctx.getSystemService(VibratorManager::class.java)?.defaultVibrator
        else ctx.getSystemService(Vibrator::class.java)

    private fun vibrar(ctx: Context, golpes: List<Golpe>) {
        val v = vibrador(ctx) ?: return
        if (!v.hasVibrator() || golpes.isEmpty()) return
        val usados = golpes.map { it.prim }.distinct().toIntArray()
        // THUD, SPIN y LOW_TICK existen desde Android 12; en otros teléfonos se imitan con una onda.
        val primitivas = (Build.VERSION.SDK_INT >= 31 || usados.none { it == THUD || it == SPIN || it == TICK_GRAVE }) && v.areAllPrimitivesSupported(*usados)
        val efecto = if (primitivas) {
            val comp = VibrationEffect.startComposition()
            golpes.forEach { comp.addPrimitive(it.prim, it.fuerza.coerceIn(0f, 1f), it.espera) }
            comp.compose()
        } else onda(golpes)
        if (Build.VERSION.SDK_INT >= 33) v.vibrate(efecto, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_TOUCH))
        else v.vibrate(efecto, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build())
    }

    /** Lo mismo como forma de onda (duración y amplitud), para teléfonos sin primitivas. */
    private fun onda(golpes: List<Golpe>): VibrationEffect {
        val tiempos = ArrayList<Long>(); val amplitudes = ArrayList<Int>()
        tiempos += 0L; amplitudes += 0
        golpes.forEach { gp ->
            if (gp.espera > 0) { tiempos += gp.espera.toLong(); amplitudes += 0 }
            val (dur, base) = when (gp.prim) {
                THUD -> 32L to 255; SPIN -> 90L to 180; SUBE -> 40L to 160; SUBE_LENTO -> 110L to 140; BAJA -> 40L to 160
                TICK -> 6L to 150; TICK_GRAVE -> 12L to 110; else -> 12L to 220
            }
            tiempos += dur; amplitudes += (base * gp.fuerza).toInt().coerceIn(1, 255)
        }
        return VibrationEffect.createWaveform(tiempos.toLongArray(), amplitudes.toIntArray(), -1)
    }

    // ---------------------------------------------------------------- sonido

    /** Por tema: toque, guardar, logro, pasar (cambiar de pantalla), elegir y abrir. */
    private val SONIDOS = mapOf(
        "romano" to intArrayOf(R.raw.s_romano_toque, R.raw.s_romano_guardar, R.raw.s_romano_logro, R.raw.s_romano_pasar, R.raw.s_romano_elegir, R.raw.s_romano_abrir),
        "ciber" to intArrayOf(R.raw.s_ciber_toque, R.raw.s_ciber_guardar, R.raw.s_ciber_logro, R.raw.s_ciber_pasar, R.raw.s_ciber_elegir, R.raw.s_ciber_abrir),
        "heleno" to intArrayOf(R.raw.s_heleno_toque, R.raw.s_heleno_guardar, R.raw.s_heleno_logro, R.raw.s_heleno_pasar, R.raw.s_heleno_elegir, R.raw.s_heleno_abrir),
        "medieval" to intArrayOf(R.raw.s_medieval_toque, R.raw.s_medieval_guardar, R.raw.s_medieval_logro, R.raw.s_medieval_pasar, R.raw.s_medieval_elegir, R.raw.s_medieval_abrir),
        "distopia" to intArrayOf(R.raw.s_distopia_toque, R.raw.s_distopia_guardar, R.raw.s_distopia_logro, R.raw.s_distopia_pasar, R.raw.s_distopia_elegir, R.raw.s_distopia_abrir),
        "refugio" to intArrayOf(R.raw.s_refugio_toque, R.raw.s_refugio_guardar, R.raw.s_refugio_logro, R.raw.s_refugio_pasar, R.raw.s_refugio_elegir, R.raw.s_refugio_abrir),
        "orden" to intArrayOf(R.raw.s_orden_toque, R.raw.s_orden_guardar, R.raw.s_orden_logro, R.raw.s_orden_pasar, R.raw.s_orden_elegir, R.raw.s_orden_abrir),
        "laboratorio" to intArrayOf(R.raw.s_laboratorio_toque, R.raw.s_laboratorio_guardar, R.raw.s_laboratorio_logro, R.raw.s_laboratorio_pasar, R.raw.s_laboratorio_elegir, R.raw.s_laboratorio_abrir),
        "colonos" to intArrayOf(R.raw.s_colonos_toque, R.raw.s_colonos_guardar, R.raw.s_colonos_logro, R.raw.s_colonos_pasar, R.raw.s_colonos_elegir, R.raw.s_colonos_abrir),
        "napoleon" to intArrayOf(R.raw.s_napoleon_toque, R.raw.s_napoleon_guardar, R.raw.s_napoleon_logro, R.raw.s_napoleon_pasar, R.raw.s_napoleon_elegir, R.raw.s_napoleon_abrir),
        "botanico" to intArrayOf(R.raw.s_botanico_toque, R.raw.s_botanico_guardar, R.raw.s_botanico_logro, R.raw.s_botanico_pasar, R.raw.s_botanico_elegir, R.raw.s_botanico_abrir),
        "navidad" to intArrayOf(R.raw.s_navidad_toque, R.raw.s_navidad_guardar, R.raw.s_navidad_logro, R.raw.s_navidad_pasar, R.raw.s_navidad_elegir, R.raw.s_navidad_abrir),
        "seda" to intArrayOf(R.raw.s_seda_toque, R.raw.s_seda_guardar, R.raw.s_seda_logro, R.raw.s_seda_pasar, R.raw.s_seda_elegir, R.raw.s_seda_abrir),
        "urss" to intArrayOf(R.raw.s_urss_toque, R.raw.s_urss_guardar, R.raw.s_urss_logro, R.raw.s_urss_pasar, R.raw.s_urss_elegir, R.raw.s_urss_abrir),
        "gotico" to intArrayOf(R.raw.s_gotico_toque, R.raw.s_gotico_guardar, R.raw.s_gotico_logro, R.raw.s_gotico_pasar, R.raw.s_gotico_elegir, R.raw.s_gotico_abrir),
        "espacio" to intArrayOf(R.raw.s_espacio_toque, R.raw.s_espacio_guardar, R.raw.s_espacio_logro, R.raw.s_espacio_pasar, R.raw.s_espacio_elegir, R.raw.s_espacio_abrir),
        "andalus" to intArrayOf(R.raw.s_andalus_toque, R.raw.s_andalus_guardar, R.raw.s_andalus_logro, R.raw.s_andalus_pasar, R.raw.s_andalus_elegir, R.raw.s_andalus_abrir),
        "mongol" to intArrayOf(R.raw.s_mongol_toque, R.raw.s_mongol_guardar, R.raw.s_mongol_logro, R.raw.s_mongol_pasar, R.raw.s_mongol_elegir, R.raw.s_mongol_abrir),
        "renacimiento" to intArrayOf(R.raw.s_renacimiento_toque, R.raw.s_renacimiento_guardar, R.raw.s_renacimiento_logro, R.raw.s_renacimiento_pasar, R.raw.s_renacimiento_elegir, R.raw.s_renacimiento_abrir),
    )

    private var pool: SoundPool? = null
    private val azar = java.util.Random()
    private val cargados = HashMap<Int, Int>()      // recurso → id en el pool
    private val listos = HashSet<Int>()             // ids ya decodificados
    private val pendientes = HashMap<Int, Float>()  // ids que se pidieron antes de estar listos → volumen

    @Synchronized
    private fun pool(): SoundPool = pool ?: SoundPool.Builder().setMaxStreams(4)
        .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
        .build().also { p ->
            p.setOnLoadCompleteListener { sp, id, estado ->
                if (estado == 0) { listos += id; pendientes.remove(id)?.let { vol -> sp.play(id, vol, vol, 1, 0, 1f) } }
            }
            pool = p
        }

    /** Deja listos los sonidos del tema para que el primero no llegue tarde. */
    fun preparar(ctx: Context) {
        if (!Repo.config.sonidos) return
        runCatching { SONIDOS[Estilo.tema.id]?.forEach { r -> if (r !in cargados) cargados[r] = pool().load(ctx.applicationContext, r, 1) } }
    }

    private fun sonar(ctx: Context, tema: TemaId, e: Evento) {
        val i = when (e) { Evento.TOQUE -> 0; Evento.GUARDAR -> 1; Evento.LOGRO -> 2; Evento.PASAR -> 3; Evento.ELEGIR -> 4; Evento.ABRIR, Evento.CAMBIO -> 5; else -> return }
        val audio = ctx.getSystemService(AudioManager::class.java)
        if (audio != null && audio.ringerMode != AudioManager.RINGER_MODE_NORMAL) return
        val r = SONIDOS[tema.id]?.get(i) ?: return
        val vol = when (i) { 0, 4 -> 0.45f; 3 -> 0.38f; 5 -> 0.6f; else -> 0.8f }
        // Los sonidos cortos cambian un poco de altura cada vez, para que tocar varias veces no suene a máquina.
        val tono = if (i == 0 || i == 3 || i == 4) 0.94f + azar.nextFloat() * 0.12f else 1f
        val p = pool()
        val id = cargados.getOrPut(r) { p.load(ctx.applicationContext, r, 1) }
        if (id in listos) p.play(id, vol, vol, 1, 0, tono) else pendientes[id] = vol
    }
}

/** Para usar desde la interfaz: `val sentir = rememberSentir(); sentir(Sentidos.Evento.GUARDAR)`. */
@Composable
fun rememberSentir(): (Sentidos.Evento) -> Unit {
    val ctx = LocalContext.current
    return remember(ctx) { { e: Sentidos.Evento -> Sentidos.sentir(ctx, e) } }
}
