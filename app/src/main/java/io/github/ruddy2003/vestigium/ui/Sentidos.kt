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
import kotlin.math.pow

/**
 * Vibraciones y sonidos de cada tema. Cada evento tiene su propio patrón: el golpe de cincel romano,
 * el sello de cera del scriptorium, el sello de goma del Ministerio, el relé del refugio…
 * Los sonidos están sintetizados para esta app y solo suenan con el teléfono en modo normal (no en silencio ni vibración).
 */
object Sentidos {
    enum class Evento { TOQUE, ELEGIR, GUARDAR, LOGRO, PASAR, CAMBIO, ERROR, ABRIR, TEMA }

    /** Un golpe de vibración: primitiva, intensidad (0–1) y espera en ms antes de él. */
    internal data class Golpe(val prim: Int, val fuerza: Float, val espera: Int = 0)

    @SuppressLint("InlinedApi") private const val CLICK = VibrationEffect.Composition.PRIMITIVE_CLICK
    @SuppressLint("InlinedApi") private const val THUD = VibrationEffect.Composition.PRIMITIVE_THUD
    @SuppressLint("InlinedApi") private const val SPIN = VibrationEffect.Composition.PRIMITIVE_SPIN
    @SuppressLint("InlinedApi") private const val SUBE = VibrationEffect.Composition.PRIMITIVE_QUICK_RISE
    @SuppressLint("InlinedApi") private const val SUBE_LENTO = VibrationEffect.Composition.PRIMITIVE_SLOW_RISE
    @SuppressLint("InlinedApi") private const val BAJA = VibrationEffect.Composition.PRIMITIVE_QUICK_FALL
    @SuppressLint("InlinedApi") private const val TICK = VibrationEffect.Composition.PRIMITIVE_TICK
    @SuppressLint("InlinedApi") private const val TICK_GRAVE = VibrationEffect.Composition.PRIMITIVE_LOW_TICK

    private fun g(p: Int, f: Float, e: Int = 0) = Golpe(p, f, e)

    /**
     * El patrón de cada tema y evento, todo propio: elegir, abrir, cambiar (encender o apagar algo) y el error también son
     * del tema (antes eran comunes a varios). Logro y tema comparten patrón: los dos celebran.
     */
    internal fun patron(tema: TemaId, e: Evento): List<Golpe> = when (tema) {
        // Cincel sobre mármol.
        TemaId.ROMANO -> when (e) {
            Evento.TOQUE -> listOf(g(CLICK, 0.5f)); Evento.PASAR -> listOf(g(TICK, 0.6f))
            Evento.GUARDAR -> listOf(g(THUD, 0.8f), g(CLICK, 0.6f, 60))
            Evento.ELEGIR -> listOf(g(CLICK, 0.55f), g(TICK, 0.3f, 40)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.45f), g(CLICK, 0.7f, 40))
            Evento.CAMBIO -> listOf(g(CLICK, 0.5f), g(CLICK, 0.8f, 60)); Evento.ERROR -> listOf(g(THUD, 0.7f), g(BAJA, 0.5f, 50))
            Evento.LOGRO, Evento.TEMA -> listOf(g(CLICK, 0.4f), g(CLICK, 0.6f, 70), g(CLICK, 0.9f, 70))
        }
        // La motoneta sobre los sampietrini: tumbos graves y cortos; guardar es la máquina que marca el boleto y el logro,
        // las campanas de la tarde.
        TemaId.URBE -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.62f), g(TICK, 0.3f, 18)); Evento.PASAR -> listOf(g(TICK, 0.45f), g(TICK, 0.45f, 55), g(TICK, 0.45f, 55))
            Evento.GUARDAR -> listOf(g(THUD, 0.66f), g(TICK_GRAVE, 0.5f, 45), g(CLICK, 0.42f, 90))
            Evento.ELEGIR -> listOf(g(CLICK, 0.47f), g(TICK_GRAVE, 0.36f, 33)); Evento.ABRIR -> listOf(g(SUBE, 0.38f), g(THUD, 0.44f, 75))
            Evento.CAMBIO -> listOf(g(TICK_GRAVE, 0.7f), g(CLICK, 0.52f, 48)); Evento.ERROR -> listOf(g(BAJA, 0.62f), g(TICK_GRAVE, 0.55f, 65), g(TICK_GRAVE, 0.55f, 65))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE_LENTO, 0.4f), g(CLICK, 0.55f, 85), g(CLICK, 0.75f, 65), g(THUD, 0.6f, 90))
        }
        // Ráfagas digitales.
        TemaId.CIBER -> when (e) {
            Evento.TOQUE -> listOf(g(TICK, 1f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.8f), g(TICK_GRAVE, 0.8f, 25))
            Evento.GUARDAR -> listOf(g(TICK, 1f), g(TICK, 1f, 20), g(CLICK, 0.8f, 20))
            Evento.ELEGIR -> listOf(g(TICK, 0.8f)); Evento.ABRIR -> listOf(g(SUBE, 0.5f), g(TICK, 0.7f, 30))
            Evento.CAMBIO -> listOf(g(TICK, 1f), g(TICK, 1f, 15)); Evento.ERROR -> listOf(g(TICK_GRAVE, 1f), g(TICK_GRAVE, 1f, 25), g(TICK_GRAVE, 1f, 25), g(BAJA, 0.6f, 20))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE, 0.8f), g(CLICK, 1f, 10), g(TICK, 1f, 40), g(TICK, 1f, 30))
        }
        // El zumbido del transformador de un letrero: golpes graves muy juntos.
        TemaId.NEON -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.9f), g(TICK, 0.35f, 12)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.6f), g(TICK_GRAVE, 0.6f, 12), g(TICK_GRAVE, 0.6f, 12))
            Evento.GUARDAR -> listOf(g(CLICK, 0.9f), g(TICK_GRAVE, 0.7f, 14), g(TICK_GRAVE, 0.7f, 14), g(TICK, 0.9f, 40))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.7f), g(TICK_GRAVE, 0.4f, 10)); Evento.ABRIR -> listOf(g(TICK_GRAVE, 0.5f), g(TICK_GRAVE, 0.7f, 18), g(SUBE, 0.6f, 18))
            Evento.CAMBIO -> listOf(g(TICK_GRAVE, 0.9f), g(TICK_GRAVE, 0.3f, 60), g(TICK_GRAVE, 0.9f, 25)); Evento.ERROR -> listOf(g(CLICK, 1f), g(TICK_GRAVE, 1f, 10), g(TICK_GRAVE, 1f, 10), g(TICK_GRAVE, 1f, 10), g(BAJA, 0.5f, 30))
            Evento.LOGRO, Evento.TEMA -> listOf(g(TICK_GRAVE, 0.6f), g(TICK_GRAVE, 0.2f, 40), g(TICK_GRAVE, 0.8f, 30), g(SUBE, 0.9f, 20), g(CLICK, 1f, 20))
        }
        // Cuerda de lira y el sol que gira.
        TemaId.HELENO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK, 0.7f)); Evento.PASAR -> listOf(g(BAJA, 0.5f))
            Evento.GUARDAR -> listOf(g(SUBE_LENTO, 0.6f), g(CLICK, 0.5f, 10))
            Evento.ELEGIR -> listOf(g(TICK, 0.6f), g(TICK, 0.3f, 50)); Evento.ABRIR -> listOf(g(SPIN, 0.4f), g(TICK, 0.5f, 40))
            Evento.CAMBIO -> listOf(g(SPIN, 0.6f)); Evento.ERROR -> listOf(g(BAJA, 0.6f), g(TICK_GRAVE, 0.5f, 80))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SPIN, 0.7f), g(CLICK, 0.6f, 60))
        }
        // El sistro que sacude sus varillas, el sello que se hunde en el barro y la puerta de piedra que se abre.
        TemaId.EGIPTO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.65f)); Evento.PASAR -> listOf(g(SUBE_LENTO, 0.35f))
            Evento.GUARDAR -> listOf(g(THUD, 0.55f), g(TICK, 0.45f, 35))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.45f), g(TICK, 0.35f, 45), g(TICK, 0.25f, 35)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.5f), g(THUD, 0.4f, 55))
            Evento.CAMBIO -> listOf(g(TICK, 0.55f), g(TICK_GRAVE, 0.55f, 45)); Evento.ERROR -> listOf(g(THUD, 0.65f), g(TICK_GRAVE, 0.4f, 90), g(TICK_GRAVE, 0.3f, 60))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE_LENTO, 0.6f), g(TICK, 0.5f, 30), g(TICK, 0.5f, 30), g(THUD, 0.7f, 60))
        }
        // Pluma, página y sello de cera.
        TemaId.MEDIEVAL -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.8f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.6f), g(TICK, 0.3f, 40))
            Evento.GUARDAR -> listOf(g(SUBE, 0.3f), g(THUD, 1f, 20))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.7f)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.4f), g(THUD, 0.6f, 30))
            Evento.CAMBIO -> listOf(g(TICK_GRAVE, 0.6f), g(CLICK, 0.5f, 90)); Evento.ERROR -> listOf(g(THUD, 0.8f), g(THUD, 0.4f, 120))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 0.8f), g(THUD, 0.45f, 300))
        }
        // Cota de malla, espada, el sello de la casa, el rastrillo y los tambores con el olifante.
        TemaId.TEMPLARIO -> when (e) {
            Evento.TOQUE -> listOf(g(CLICK, 0.75f), g(TICK_GRAVE, 0.4f, 25)); Evento.PASAR -> listOf(g(BAJA, 0.55f), g(TICK_GRAVE, 0.5f, 35))
            Evento.GUARDAR -> listOf(g(THUD, 0.9f), g(CLICK, 0.7f, 45), g(TICK_GRAVE, 0.4f, 40))
            Evento.ELEGIR -> listOf(g(CLICK, 0.65f), g(CLICK, 0.35f, 55)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.55f), g(THUD, 0.75f, 20), g(TICK, 0.3f, 60))
            Evento.CAMBIO -> listOf(g(THUD, 0.5f), g(CLICK, 0.9f, 35)); Evento.ERROR -> listOf(g(THUD, 1f), g(BAJA, 0.6f, 40), g(THUD, 0.5f, 90))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 0.9f), g(CLICK, 0.8f, 120), g(CLICK, 0.8f, 120), g(SUBE, 0.6f, 60))
        }
        // Máquina de escribir y sello de goma.
        TemaId.DISTOPIA -> when (e) {
            Evento.TOQUE -> listOf(g(CLICK, 1f)); Evento.PASAR -> listOf(g(CLICK, 0.6f))
            Evento.GUARDAR -> listOf(g(THUD, 1f), g(CLICK, 1f, 70))
            Evento.ELEGIR -> listOf(g(CLICK, 0.45f)); Evento.ABRIR -> listOf(g(CLICK, 0.8f), g(THUD, 0.6f, 50))
            Evento.CAMBIO -> listOf(g(CLICK, 0.9f), g(CLICK, 0.5f, 30)); Evento.ERROR -> listOf(g(CLICK, 0.9f), g(CLICK, 0.9f, 140))
            Evento.LOGRO, Evento.TEMA -> listOf(g(CLICK, 0.8f), g(CLICK, 0.8f, 80), g(CLICK, 0.8f, 80), g(CLICK, 0.8f, 80), g(THUD, 0.9f, 120))
        }
        // Relé y contador Geiger.
        TemaId.REFUGIO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK, 0.9f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.7f))
            Evento.GUARDAR -> listOf(g(CLICK, 0.7f), g(CLICK, 0.7f, 35), g(SUBE, 0.5f, 40))
            Evento.ELEGIR -> listOf(g(TICK, 0.9f), g(TICK, 0.5f, 20)); Evento.ABRIR -> listOf(g(CLICK, 0.6f), g(SUBE, 0.5f, 30))
            Evento.CAMBIO -> listOf(g(CLICK, 0.7f), g(SUBE_LENTO, 0.5f, 30)); Evento.ERROR -> listOf(g(TICK, 0.9f), g(TICK, 0.9f, 20), g(TICK, 0.9f, 50), g(TICK, 0.9f, 10), g(TICK, 0.9f, 40), g(BAJA, 0.5f, 40))
            Evento.LOGRO, Evento.TEMA -> listOf(g(TICK, 0.9f), g(TICK, 0.9f, 30), g(TICK, 0.9f, 80), g(TICK, 0.9f, 20), g(TICK, 0.9f, 120), g(TICK, 0.9f, 40), g(SUBE, 0.6f, 80))
        }
        // Servo y pisada de armadura.
        TemaId.ORDEN -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 1f)); Evento.PASAR -> listOf(g(SUBE, 0.4f))
            Evento.GUARDAR -> listOf(g(SUBE_LENTO, 0.8f), g(THUD, 1f, 10))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.8f)); Evento.ABRIR -> listOf(g(SUBE, 0.5f), g(THUD, 0.9f, 30))
            Evento.CAMBIO -> listOf(g(SUBE, 0.4f), g(CLICK, 0.8f, 40)); Evento.ERROR -> listOf(g(BAJA, 0.7f), g(THUD, 1f, 40))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 1f), g(THUD, 1f, 180), g(THUD, 1f, 180))
        }
        // Limpio y suave.
        TemaId.LABORATORIO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK, 0.4f)); Evento.PASAR -> listOf(g(TICK, 0.3f))
            Evento.GUARDAR -> listOf(g(CLICK, 0.4f), g(SUBE, 0.3f, 20))
            Evento.ELEGIR -> listOf(g(TICK, 0.35f)); Evento.ABRIR -> listOf(g(SUBE, 0.4f), g(TICK, 0.5f, 40))
            Evento.CAMBIO -> listOf(g(TICK, 0.4f), g(SUBE, 0.3f, 30)); Evento.ERROR -> listOf(g(BAJA, 0.45f), g(BAJA, 0.35f, 90))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE, 0.5f), g(BAJA, 0.5f, 40))
        }
        // Nudillos sobre madera.
        TemaId.COLONOS -> when (e) {
            Evento.TOQUE -> listOf(g(CLICK, 0.6f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.7f))
            Evento.GUARDAR -> listOf(g(THUD, 0.9f), g(THUD, 0.9f, 110))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.6f), g(TICK, 0.3f, 30)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.4f), g(CLICK, 0.6f, 60))
            Evento.CAMBIO -> listOf(g(CLICK, 0.6f), g(TICK_GRAVE, 0.6f, 50)); Evento.ERROR -> listOf(g(THUD, 0.6f), g(THUD, 0.5f, 140))
            Evento.LOGRO, Evento.TEMA -> listOf(g(CLICK, 0.5f), g(CLICK, 0.5f, 90), g(CLICK, 0.5f, 90), g(THUD, 0.9f, 120))
        }
        // Tambor de marcha y cañonazo lejano.
        TemaId.NAPOLEON -> when (e) {
            Evento.TOQUE -> listOf(g(CLICK, 0.7f)); Evento.PASAR -> listOf(g(TICK, 0.6f), g(TICK, 0.6f, 50))
            Evento.GUARDAR -> listOf(g(CLICK, 0.6f), g(CLICK, 0.6f, 60), g(THUD, 1f, 60))
            Evento.ELEGIR -> listOf(g(CLICK, 0.6f), g(CLICK, 0.4f, 40)); Evento.ABRIR -> listOf(g(SUBE, 0.6f), g(THUD, 0.7f, 40))
            Evento.CAMBIO -> listOf(g(CLICK, 0.5f), g(CLICK, 0.7f, 60), g(THUD, 0.5f, 60)); Evento.ERROR -> listOf(g(THUD, 0.8f), g(BAJA, 0.6f, 60))
            Evento.LOGRO, Evento.TEMA -> listOf(g(CLICK, 0.5f), g(CLICK, 0.5f, 50), g(CLICK, 0.5f, 50), g(CLICK, 0.5f, 50), g(THUD, 1f, 160), g(THUD, 0.6f, 240))
        }
        // Hojas que se rozan y un pájaro que canta.
        TemaId.BOTANICO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.5f)); Evento.PASAR -> listOf(g(SUBE, 0.3f), g(BAJA, 0.3f, 20))
            Evento.GUARDAR -> listOf(g(SUBE_LENTO, 0.5f), g(CLICK, 0.4f, 30))
            Evento.ELEGIR -> listOf(g(TICK, 0.5f), g(TICK, 0.7f, 60)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.4f), g(TICK, 0.4f, 50))
            Evento.CAMBIO -> listOf(g(SUBE, 0.3f), g(TICK, 0.5f, 40)); Evento.ERROR -> listOf(g(BAJA, 0.4f), g(TICK_GRAVE, 0.4f, 70))
            Evento.LOGRO, Evento.TEMA -> listOf(g(TICK, 0.6f), g(TICK, 0.8f, 70), g(TICK, 0.6f, 50), g(SUBE, 0.5f, 60))
        }
        // Hormigón que cae y cascajo que rueda.
        TemaId.RUINAS -> when (e) {
            Evento.TOQUE -> listOf(g(THUD, 0.35f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.32f), g(TICK, 0.22f, 35))
            Evento.GUARDAR -> listOf(g(THUD, 0.55f), g(TICK_GRAVE, 0.3f, 45), g(TICK, 0.2f, 35))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.42f), g(TICK, 0.26f, 35)); Evento.ABRIR -> listOf(g(BAJA, 0.33f), g(THUD, 0.62f, 55))
            Evento.CAMBIO -> listOf(g(CLICK, 0.42f), g(TICK_GRAVE, 0.36f, 85)); Evento.ERROR -> listOf(g(THUD, 0.66f), g(TICK_GRAVE, 0.33f, 45), g(TICK_GRAVE, 0.22f, 45))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE_LENTO, 0.45f), g(TICK, 0.35f, 55), g(TICK, 0.5f, 45), g(THUD, 0.66f, 65))
        }
        // Órgano y campanas de la capilla.
        TemaId.GOTICO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.6f)); Evento.PASAR -> listOf(g(SUBE_LENTO, 0.35f))
            Evento.GUARDAR -> listOf(g(THUD, 0.8f), g(SUBE_LENTO, 0.5f, 40))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.5f), g(TICK_GRAVE, 0.3f, 100)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.5f), g(THUD, 0.7f, 60))
            Evento.CAMBIO -> listOf(g(THUD, 0.5f), g(TICK_GRAVE, 0.5f, 120)); Evento.ERROR -> listOf(g(THUD, 0.7f), g(THUD, 0.7f, 40))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 0.9f), g(THUD, 0.6f, 260), g(THUD, 0.4f, 260))
        }
        // Un latido que se acelera, la ola que llega y se va, la puerta del archivo que cruje.
        TemaId.ARCANO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.42f), g(TICK, 0.18f, 70)); Evento.PASAR -> listOf(g(SUBE_LENTO, 0.27f), g(BAJA, 0.22f, 90))
            Evento.GUARDAR -> listOf(g(THUD, 0.66f), g(THUD, 0.38f, 140))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.47f), g(TICK_GRAVE, 0.21f, 170)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.62f), g(SPIN, 0.24f, 80))
            Evento.CAMBIO -> listOf(g(BAJA, 0.44f), g(TICK_GRAVE, 0.36f, 130)); Evento.ERROR -> listOf(g(THUD, 0.58f), g(BAJA, 0.41f, 110), g(TICK_GRAVE, 0.2f, 160))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE_LENTO, 0.4f), g(THUD, 0.72f, 210), g(THUD, 0.44f, 140), g(SPIN, 0.3f, 300))
        }
        // Pitidos de la sala de control.
        TemaId.ESPACIO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK, 0.8f)); Evento.PASAR -> listOf(g(SUBE, 0.5f))
            Evento.GUARDAR -> listOf(g(TICK, 0.9f), g(TICK, 0.9f, 60), g(SUBE_LENTO, 0.6f, 40))
            Evento.ELEGIR -> listOf(g(TICK, 0.7f), g(TICK, 0.7f, 45)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.6f), g(TICK, 0.8f, 30))
            Evento.CAMBIO -> listOf(g(CLICK, 0.6f), g(TICK, 0.8f, 30)); Evento.ERROR -> listOf(g(TICK, 1f), g(TICK, 1f, 60), g(TICK, 1f, 60))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE_LENTO, 0.9f), g(CLICK, 1f, 10), g(SPIN, 0.5f, 60))
        }
        // Bajo el agua todo llega amortiguado: golpes sordos que se hunden y, al subir, una burbuja lenta.
        TemaId.ABISMO -> when (e) {
            Evento.TOQUE -> listOf(g(BAJA, 0.42f)); Evento.PASAR -> listOf(g(THUD, 0.35f), g(BAJA, 0.3f, 40))
            Evento.GUARDAR -> listOf(g(THUD, 0.7f), g(BAJA, 0.5f, 70), g(TICK_GRAVE, 0.4f, 90))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.55f), g(BAJA, 0.35f, 55)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.45f), g(THUD, 0.5f, 85))
            Evento.CAMBIO -> listOf(g(BAJA, 0.6f), g(TICK_GRAVE, 0.45f, 75)); Evento.ERROR -> listOf(g(THUD, 0.9f), g(THUD, 0.6f, 110))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE_LENTO, 0.7f), g(BAJA, 0.8f, 30), g(THUD, 0.6f, 120), g(TICK, 0.35f, 90))
        }
        // Laúd y darbuka: dum, tek, tek.
        TemaId.ANDALUS -> when (e) {
            Evento.TOQUE -> listOf(g(CLICK, 0.5f)); Evento.PASAR -> listOf(g(TICK, 0.5f), g(TICK_GRAVE, 0.5f, 60))
            Evento.GUARDAR -> listOf(g(THUD, 0.8f), g(TICK, 0.6f, 90), g(TICK, 0.6f, 90))
            Evento.ELEGIR -> listOf(g(TICK, 0.5f), g(TICK_GRAVE, 0.4f, 40)); Evento.ABRIR -> listOf(g(THUD, 0.6f), g(TICK, 0.5f, 90), g(TICK, 0.5f, 50))
            Evento.CAMBIO -> listOf(g(TICK, 0.6f), g(TICK, 0.6f, 50), g(THUD, 0.5f, 60)); Evento.ERROR -> listOf(g(THUD, 0.8f), g(BAJA, 0.5f, 60))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 0.9f), g(TICK, 0.7f, 120), g(TICK, 0.7f, 60), g(THUD, 0.8f, 120), g(SPIN, 0.4f, 80))
        }
        // Las dos lengüetas del tunkul (el tambor de tronco hendido), la sonaja de semillas y las páginas del biombo al desplegarse.
        TemaId.MESO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.45f), g(TICK, 0.35f, 37)); Evento.PASAR -> listOf(g(TICK, 0.42f), g(TICK, 0.42f, 33), g(TICK, 0.42f, 33))
            Evento.GUARDAR -> listOf(g(THUD, 0.75f), g(TICK_GRAVE, 0.55f, 110), g(TICK, 0.45f, 70))
            Evento.ELEGIR -> listOf(g(TICK_GRAVE, 0.5f), g(TICK, 0.45f, 53)); Evento.ABRIR -> listOf(g(TICK, 0.4f), g(TICK, 0.45f, 41), g(TICK, 0.5f, 41), g(THUD, 0.6f, 67))
            Evento.CAMBIO -> listOf(g(TICK_GRAVE, 0.55f), g(TICK_GRAVE, 0.55f, 73), g(TICK, 0.5f, 37)); Evento.ERROR -> listOf(g(BAJA, 0.7f), g(THUD, 0.6f, 83))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 0.85f), g(TICK_GRAVE, 0.7f, 130), g(THUD, 0.8f, 130), g(TICK, 0.6f, 65), g(TICK, 0.6f, 65), g(SPIN, 0.45f, 90))
        }
        // Cascos al galope y el tambor de la horda.
        TemaId.MONGOL -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.8f)); Evento.PASAR -> listOf(g(CLICK, 0.6f), g(CLICK, 0.6f, 50), g(CLICK, 0.8f, 50))
            Evento.GUARDAR -> listOf(g(THUD, 0.9f), g(THUD, 0.5f, 70))
            Evento.ELEGIR -> listOf(g(CLICK, 0.5f), g(CLICK, 0.6f, 50)); Evento.ABRIR -> listOf(g(CLICK, 0.5f), g(CLICK, 0.6f, 50), g(THUD, 0.8f, 60))
            Evento.CAMBIO -> listOf(g(CLICK, 0.6f), g(CLICK, 0.6f, 70)); Evento.ERROR -> listOf(g(CLICK, 0.7f), g(BAJA, 0.6f, 60))
            Evento.LOGRO, Evento.TEMA -> listOf(g(CLICK, 0.6f), g(CLICK, 0.6f, 50), g(CLICK, 0.8f, 50), g(CLICK, 0.6f, 140), g(CLICK, 0.6f, 50), g(CLICK, 0.8f, 50), g(THUD, 1f, 140))
        }
        // Pluma que raspa el papel y un laúd.
        TemaId.RENACIMIENTO -> when (e) {
            Evento.TOQUE -> listOf(g(TICK, 0.5f)); Evento.PASAR -> listOf(g(BAJA, 0.4f))
            Evento.GUARDAR -> listOf(g(SUBE, 0.3f), g(CLICK, 0.6f, 30))
            Evento.ELEGIR -> listOf(g(TICK, 0.45f), g(BAJA, 0.2f, 20)); Evento.ABRIR -> listOf(g(SUBE_LENTO, 0.4f), g(TICK, 0.6f, 60), g(TICK, 0.6f, 40))
            Evento.CAMBIO -> listOf(g(TICK, 0.5f), g(TICK, 0.7f, 50)); Evento.ERROR -> listOf(g(BAJA, 0.5f), g(TICK_GRAVE, 0.5f, 60))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE_LENTO, 0.5f), g(TICK, 0.7f, 40), g(TICK, 0.7f, 80), g(TICK, 0.9f, 80))
        }
        // La campana de a bordo, el oleaje contra el casco y un cañonazo.
        TemaId.PIRATA -> when (e) {
            Evento.TOQUE -> listOf(g(THUD, 0.45f)); Evento.PASAR -> listOf(g(SUBE_LENTO, 0.3f), g(BAJA, 0.3f, 40))
            Evento.GUARDAR -> listOf(g(THUD, 0.9f), g(SPIN, 0.3f, 50))
            Evento.ELEGIR -> listOf(g(CLICK, 0.45f), g(THUD, 0.3f, 50)); Evento.ABRIR -> listOf(g(SPIN, 0.3f), g(THUD, 0.5f, 70))
            Evento.CAMBIO -> listOf(g(THUD, 0.4f), g(CLICK, 0.6f, 80)); Evento.ERROR -> listOf(g(THUD, 1f), g(TICK_GRAVE, 0.6f, 90), g(BAJA, 0.5f, 60))
            // Dos y dos campanadas, como al cambiar la guardia, y el cañonazo.
            Evento.LOGRO, Evento.TEMA -> listOf(g(CLICK, 0.8f), g(CLICK, 0.8f, 60), g(CLICK, 0.8f, 200), g(CLICK, 0.8f, 60), g(THUD, 1f, 200))
        }
        // Cascabeles.
        TemaId.NAVIDAD -> when (e) {
            Evento.TOQUE -> listOf(g(TICK, 0.7f)); Evento.PASAR -> listOf(g(TICK, 0.5f), g(TICK, 0.4f, 30))
            Evento.GUARDAR -> listOf(g(TICK, 0.8f), g(TICK, 0.7f, 35), g(TICK, 0.8f, 35), g(CLICK, 0.6f, 50))
            Evento.ELEGIR -> listOf(g(TICK, 0.6f), g(TICK, 0.4f, 25)); Evento.ABRIR -> listOf(g(SUBE, 0.5f), g(TICK, 0.7f, 30), g(TICK, 0.6f, 30))
            Evento.CAMBIO -> listOf(g(TICK, 0.7f), g(TICK, 0.7f, 30), g(TICK, 0.7f, 30)); Evento.ERROR -> listOf(g(BAJA, 0.5f), g(TICK, 0.4f, 60), g(TICK, 0.3f, 40))
            Evento.LOGRO, Evento.TEMA -> listOf(g(SUBE, 0.6f), g(TICK, 0.9f, 30), g(TICK, 0.8f, 40), g(TICK, 0.9f, 40), g(TICK, 0.8f, 40), g(CLICK, 0.7f, 60))
        }
        // Gong y pasos de camello.
        TemaId.SEDA -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.7f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.5f), g(TICK_GRAVE, 0.4f, 160))
            Evento.GUARDAR -> listOf(g(THUD, 0.8f), g(SPIN, 0.35f, 30))
            Evento.ELEGIR -> listOf(g(TICK, 0.5f), g(SPIN, 0.25f, 30)); Evento.ABRIR -> listOf(g(THUD, 0.6f), g(SPIN, 0.4f, 40))
            Evento.CAMBIO -> listOf(g(SPIN, 0.35f), g(TICK, 0.6f, 60)); Evento.ERROR -> listOf(g(THUD, 0.6f), g(BAJA, 0.5f, 100))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 1f), g(SPIN, 0.5f, 40), g(SUBE_LENTO, 0.4f, 200))
        }
        // Prensa de fábrica y sirena.
        TemaId.URSS -> when (e) {
            Evento.TOQUE -> listOf(g(CLICK, 0.9f)); Evento.PASAR -> listOf(g(TICK_GRAVE, 0.9f))
            Evento.GUARDAR -> listOf(g(THUD, 1f), g(THUD, 1f, 90))
            Evento.ELEGIR -> listOf(g(CLICK, 0.8f)); Evento.ABRIR -> listOf(g(CLICK, 0.8f), g(THUD, 0.9f, 60))
            Evento.CAMBIO -> listOf(g(CLICK, 0.9f), g(THUD, 0.6f, 40)); Evento.ERROR -> listOf(g(SUBE_LENTO, 0.7f), g(BAJA, 0.9f, 60))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 1f), g(THUD, 1f, 120), g(THUD, 1f, 120), g(SUBE_LENTO, 0.8f, 120))
        }
        // Piedra que asienta y cuerda de arpa.
        TemaId.ANTIGUAS -> when (e) {
            Evento.TOQUE -> listOf(g(THUD, 0.6f)); Evento.PASAR -> listOf(g(BAJA, 0.35f))
            Evento.GUARDAR -> listOf(g(THUD, 0.7f), g(TICK, 0.6f, 60), g(TICK, 0.7f, 120))
            Evento.ELEGIR -> listOf(g(THUD, 0.4f), g(TICK, 0.4f, 40)); Evento.ABRIR -> listOf(g(THUD, 0.6f), g(SUBE_LENTO, 0.4f, 60))
            Evento.CAMBIO -> listOf(g(TICK, 0.6f), g(TICK, 0.8f, 70)); Evento.ERROR -> listOf(g(THUD, 0.8f), g(BAJA, 0.4f, 80))
            Evento.LOGRO, Evento.TEMA -> listOf(g(THUD, 0.9f), g(SUBE_LENTO, 0.5f, 80), g(TICK, 0.7f, 90), g(TICK, 0.8f, 90), g(TICK, 1f, 90))
        }
        // Tambor de las milicias: redoble y golpe.
        TemaId.REVOLUCION -> when (e) {
            Evento.TOQUE -> listOf(g(TICK_GRAVE, 0.7f)); Evento.PASAR -> listOf(g(TICK, 0.5f), g(TICK, 0.5f, 40))
            Evento.GUARDAR -> listOf(g(TICK, 0.6f), g(TICK, 0.6f, 30), g(TICK, 0.7f, 30), g(THUD, 0.9f, 50))
            Evento.ELEGIR -> listOf(g(TICK, 0.6f), g(TICK, 0.4f, 30)); Evento.ABRIR -> listOf(g(TICK, 0.5f), g(TICK, 0.5f, 30), g(TICK, 0.5f, 30), g(THUD, 0.7f, 40))
            Evento.CAMBIO -> listOf(g(TICK, 0.6f), g(THUD, 0.6f, 40)); Evento.ERROR -> listOf(g(TICK, 0.5f), g(TICK, 0.5f, 30), g(BAJA, 0.7f, 40))
            Evento.LOGRO, Evento.TEMA -> listOf(g(TICK, 0.6f), g(TICK, 0.6f, 30), g(TICK, 0.6f, 30), g(TICK, 0.7f, 30), g(TICK, 0.8f, 30), g(THUD, 1f, 60), g(SUBE, 0.6f, 120))
        }
    }

    /** Vibra y, si toca, suena. Nunca falla: si el teléfono no puede, simplemente no pasa nada. */
    fun sentir(ctx: Context, e: Evento) {
        val c = Repo.config
        val tema = Estilo.tema
        if (c.vibracion) runCatching { vibrar(ctx, patron(tema, e)) }
        if (c.sonidos) runCatching { sonar(ctx, tema, e) }
    }

    /** Solo la vibración del evento (para cuando el sonido lo pone otra cosa, como la música al cambiar de tema). */
    fun vibrarEvento(ctx: Context, e: Evento) {
        if (Repo.config.vibracion) runCatching { vibrar(ctx, patron(Estilo.tema, e)) }
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

    /**
     * Por tema, en este orden: toque, guardar, logro, pasar (cambiar de pantalla), elegir, abrir, el sonido del propio tema
     * (al elegirlo), error, cambio (encender o apagar algo), la tecla del diario y el rascado de la lámina del mapa.
     * Todos salen de `herramientas/afinados.py`.
     */
    private fun sonidos(tema: TemaId): IntArray = when (tema) {
        TemaId.ROMANO -> intArrayOf(R.raw.s_romano_toque, R.raw.s_romano_guardar, R.raw.s_romano_logro, R.raw.s_romano_pasar, R.raw.s_romano_elegir, R.raw.s_romano_abrir, R.raw.s_romano_tema, R.raw.s_romano_error, R.raw.s_romano_cambio, R.raw.s_tecla_romano, R.raw.s_rascar_romano)
        TemaId.URBE -> intArrayOf(R.raw.s_urbe_toque, R.raw.s_urbe_guardar, R.raw.s_urbe_logro, R.raw.s_urbe_pasar, R.raw.s_urbe_elegir, R.raw.s_urbe_abrir, R.raw.s_urbe_tema, R.raw.s_urbe_error, R.raw.s_urbe_cambio, R.raw.s_tecla_urbe, R.raw.s_rascar_urbe)
        TemaId.CIBER -> intArrayOf(R.raw.s_ciber_toque, R.raw.s_ciber_guardar, R.raw.s_ciber_logro, R.raw.s_ciber_pasar, R.raw.s_ciber_elegir, R.raw.s_ciber_abrir, R.raw.s_ciber_tema, R.raw.s_ciber_error, R.raw.s_ciber_cambio, R.raw.s_tecla_ciber, R.raw.s_rascar_ciber)
        TemaId.NEON -> intArrayOf(R.raw.s_neon_toque, R.raw.s_neon_guardar, R.raw.s_neon_logro, R.raw.s_neon_pasar, R.raw.s_neon_elegir, R.raw.s_neon_abrir, R.raw.s_neon_tema, R.raw.s_neon_error, R.raw.s_neon_cambio, R.raw.s_tecla_neon, R.raw.s_rascar_neon)
        TemaId.HELENO -> intArrayOf(R.raw.s_heleno_toque, R.raw.s_heleno_guardar, R.raw.s_heleno_logro, R.raw.s_heleno_pasar, R.raw.s_heleno_elegir, R.raw.s_heleno_abrir, R.raw.s_heleno_tema, R.raw.s_heleno_error, R.raw.s_heleno_cambio, R.raw.s_tecla_heleno, R.raw.s_rascar_heleno)
        TemaId.EGIPTO -> intArrayOf(R.raw.s_egipto_toque, R.raw.s_egipto_guardar, R.raw.s_egipto_logro, R.raw.s_egipto_pasar, R.raw.s_egipto_elegir, R.raw.s_egipto_abrir, R.raw.s_egipto_tema, R.raw.s_egipto_error, R.raw.s_egipto_cambio, R.raw.s_tecla_egipto, R.raw.s_rascar_egipto)
        TemaId.MEDIEVAL -> intArrayOf(R.raw.s_medieval_toque, R.raw.s_medieval_guardar, R.raw.s_medieval_logro, R.raw.s_medieval_pasar, R.raw.s_medieval_elegir, R.raw.s_medieval_abrir, R.raw.s_medieval_tema, R.raw.s_medieval_error, R.raw.s_medieval_cambio, R.raw.s_tecla_medieval, R.raw.s_rascar_medieval)
        TemaId.TEMPLARIO -> intArrayOf(R.raw.s_templario_toque, R.raw.s_templario_guardar, R.raw.s_templario_logro, R.raw.s_templario_pasar, R.raw.s_templario_elegir, R.raw.s_templario_abrir, R.raw.s_templario_tema, R.raw.s_templario_error, R.raw.s_templario_cambio, R.raw.s_tecla_templario, R.raw.s_rascar_templario)
        TemaId.DISTOPIA -> intArrayOf(R.raw.s_distopia_toque, R.raw.s_distopia_guardar, R.raw.s_distopia_logro, R.raw.s_distopia_pasar, R.raw.s_distopia_elegir, R.raw.s_distopia_abrir, R.raw.s_distopia_tema, R.raw.s_distopia_error, R.raw.s_distopia_cambio, R.raw.s_tecla_distopia, R.raw.s_rascar_distopia)
        TemaId.REFUGIO -> intArrayOf(R.raw.s_refugio_toque, R.raw.s_refugio_guardar, R.raw.s_refugio_logro, R.raw.s_refugio_pasar, R.raw.s_refugio_elegir, R.raw.s_refugio_abrir, R.raw.s_refugio_tema, R.raw.s_refugio_error, R.raw.s_refugio_cambio, R.raw.s_tecla_refugio, R.raw.s_rascar_refugio)
        TemaId.ORDEN -> intArrayOf(R.raw.s_orden_toque, R.raw.s_orden_guardar, R.raw.s_orden_logro, R.raw.s_orden_pasar, R.raw.s_orden_elegir, R.raw.s_orden_abrir, R.raw.s_orden_tema, R.raw.s_orden_error, R.raw.s_orden_cambio, R.raw.s_tecla_orden, R.raw.s_rascar_orden)
        TemaId.LABORATORIO -> intArrayOf(R.raw.s_laboratorio_toque, R.raw.s_laboratorio_guardar, R.raw.s_laboratorio_logro, R.raw.s_laboratorio_pasar, R.raw.s_laboratorio_elegir, R.raw.s_laboratorio_abrir, R.raw.s_laboratorio_tema, R.raw.s_laboratorio_error, R.raw.s_laboratorio_cambio, R.raw.s_tecla_laboratorio, R.raw.s_rascar_laboratorio)
        TemaId.COLONOS -> intArrayOf(R.raw.s_colonos_toque, R.raw.s_colonos_guardar, R.raw.s_colonos_logro, R.raw.s_colonos_pasar, R.raw.s_colonos_elegir, R.raw.s_colonos_abrir, R.raw.s_colonos_tema, R.raw.s_colonos_error, R.raw.s_colonos_cambio, R.raw.s_tecla_colonos, R.raw.s_rascar_colonos)
        TemaId.NAPOLEON -> intArrayOf(R.raw.s_napoleon_toque, R.raw.s_napoleon_guardar, R.raw.s_napoleon_logro, R.raw.s_napoleon_pasar, R.raw.s_napoleon_elegir, R.raw.s_napoleon_abrir, R.raw.s_napoleon_tema, R.raw.s_napoleon_error, R.raw.s_napoleon_cambio, R.raw.s_tecla_napoleon, R.raw.s_rascar_napoleon)
        TemaId.BOTANICO -> intArrayOf(R.raw.s_botanico_toque, R.raw.s_botanico_guardar, R.raw.s_botanico_logro, R.raw.s_botanico_pasar, R.raw.s_botanico_elegir, R.raw.s_botanico_abrir, R.raw.s_botanico_tema, R.raw.s_botanico_error, R.raw.s_botanico_cambio, R.raw.s_tecla_botanico, R.raw.s_rascar_botanico)
        TemaId.RUINAS -> intArrayOf(R.raw.s_ruinas_toque, R.raw.s_ruinas_guardar, R.raw.s_ruinas_logro, R.raw.s_ruinas_pasar, R.raw.s_ruinas_elegir, R.raw.s_ruinas_abrir, R.raw.s_ruinas_tema, R.raw.s_ruinas_error, R.raw.s_ruinas_cambio, R.raw.s_tecla_ruinas, R.raw.s_rascar_ruinas)
        TemaId.GOTICO -> intArrayOf(R.raw.s_gotico_toque, R.raw.s_gotico_guardar, R.raw.s_gotico_logro, R.raw.s_gotico_pasar, R.raw.s_gotico_elegir, R.raw.s_gotico_abrir, R.raw.s_gotico_tema, R.raw.s_gotico_error, R.raw.s_gotico_cambio, R.raw.s_tecla_gotico, R.raw.s_rascar_gotico)
        TemaId.ARCANO -> intArrayOf(R.raw.s_arcano_toque, R.raw.s_arcano_guardar, R.raw.s_arcano_logro, R.raw.s_arcano_pasar, R.raw.s_arcano_elegir, R.raw.s_arcano_abrir, R.raw.s_arcano_tema, R.raw.s_arcano_error, R.raw.s_arcano_cambio, R.raw.s_tecla_arcano, R.raw.s_rascar_arcano)
        TemaId.ESPACIO -> intArrayOf(R.raw.s_espacio_toque, R.raw.s_espacio_guardar, R.raw.s_espacio_logro, R.raw.s_espacio_pasar, R.raw.s_espacio_elegir, R.raw.s_espacio_abrir, R.raw.s_espacio_tema, R.raw.s_espacio_error, R.raw.s_espacio_cambio, R.raw.s_tecla_espacio, R.raw.s_rascar_espacio)
        TemaId.ABISMO -> intArrayOf(R.raw.s_abismo_toque, R.raw.s_abismo_guardar, R.raw.s_abismo_logro, R.raw.s_abismo_pasar, R.raw.s_abismo_elegir, R.raw.s_abismo_abrir, R.raw.s_abismo_tema, R.raw.s_abismo_error, R.raw.s_abismo_cambio, R.raw.s_tecla_abismo, R.raw.s_rascar_abismo)
        TemaId.ANDALUS -> intArrayOf(R.raw.s_andalus_toque, R.raw.s_andalus_guardar, R.raw.s_andalus_logro, R.raw.s_andalus_pasar, R.raw.s_andalus_elegir, R.raw.s_andalus_abrir, R.raw.s_andalus_tema, R.raw.s_andalus_error, R.raw.s_andalus_cambio, R.raw.s_tecla_andalus, R.raw.s_rascar_andalus)
        TemaId.MESO -> intArrayOf(R.raw.s_meso_toque, R.raw.s_meso_guardar, R.raw.s_meso_logro, R.raw.s_meso_pasar, R.raw.s_meso_elegir, R.raw.s_meso_abrir, R.raw.s_meso_tema, R.raw.s_meso_error, R.raw.s_meso_cambio, R.raw.s_tecla_meso, R.raw.s_rascar_meso)
        TemaId.MONGOL -> intArrayOf(R.raw.s_mongol_toque, R.raw.s_mongol_guardar, R.raw.s_mongol_logro, R.raw.s_mongol_pasar, R.raw.s_mongol_elegir, R.raw.s_mongol_abrir, R.raw.s_mongol_tema, R.raw.s_mongol_error, R.raw.s_mongol_cambio, R.raw.s_tecla_mongol, R.raw.s_rascar_mongol)
        TemaId.RENACIMIENTO -> intArrayOf(R.raw.s_renacimiento_toque, R.raw.s_renacimiento_guardar, R.raw.s_renacimiento_logro, R.raw.s_renacimiento_pasar, R.raw.s_renacimiento_elegir, R.raw.s_renacimiento_abrir, R.raw.s_renacimiento_tema, R.raw.s_renacimiento_error, R.raw.s_renacimiento_cambio, R.raw.s_tecla_renacimiento, R.raw.s_rascar_renacimiento)
        TemaId.PIRATA -> intArrayOf(R.raw.s_pirata_toque, R.raw.s_pirata_guardar, R.raw.s_pirata_logro, R.raw.s_pirata_pasar, R.raw.s_pirata_elegir, R.raw.s_pirata_abrir, R.raw.s_pirata_tema, R.raw.s_pirata_error, R.raw.s_pirata_cambio, R.raw.s_tecla_pirata, R.raw.s_rascar_pirata)
        TemaId.NAVIDAD -> intArrayOf(R.raw.s_navidad_toque, R.raw.s_navidad_guardar, R.raw.s_navidad_logro, R.raw.s_navidad_pasar, R.raw.s_navidad_elegir, R.raw.s_navidad_abrir, R.raw.s_navidad_tema, R.raw.s_navidad_error, R.raw.s_navidad_cambio, R.raw.s_tecla_navidad, R.raw.s_rascar_navidad)
        TemaId.SEDA -> intArrayOf(R.raw.s_seda_toque, R.raw.s_seda_guardar, R.raw.s_seda_logro, R.raw.s_seda_pasar, R.raw.s_seda_elegir, R.raw.s_seda_abrir, R.raw.s_seda_tema, R.raw.s_seda_error, R.raw.s_seda_cambio, R.raw.s_tecla_seda, R.raw.s_rascar_seda)
        TemaId.URSS -> intArrayOf(R.raw.s_urss_toque, R.raw.s_urss_guardar, R.raw.s_urss_logro, R.raw.s_urss_pasar, R.raw.s_urss_elegir, R.raw.s_urss_abrir, R.raw.s_urss_tema, R.raw.s_urss_error, R.raw.s_urss_cambio, R.raw.s_tecla_urss, R.raw.s_rascar_urss)
        TemaId.ANTIGUAS -> intArrayOf(R.raw.s_antiguas_toque, R.raw.s_antiguas_guardar, R.raw.s_antiguas_logro, R.raw.s_antiguas_pasar, R.raw.s_antiguas_elegir, R.raw.s_antiguas_abrir, R.raw.s_antiguas_tema, R.raw.s_antiguas_error, R.raw.s_antiguas_cambio, R.raw.s_tecla_antiguas, R.raw.s_rascar_antiguas)
        TemaId.REVOLUCION -> intArrayOf(R.raw.s_revolucion_toque, R.raw.s_revolucion_guardar, R.raw.s_revolucion_logro, R.raw.s_revolucion_pasar, R.raw.s_revolucion_elegir, R.raw.s_revolucion_abrir, R.raw.s_revolucion_tema, R.raw.s_revolucion_error, R.raw.s_revolucion_cambio, R.raw.s_tecla_revolucion, R.raw.s_rascar_revolucion)
    }
    internal val SONIDOS: Map<String, IntArray> = TemaId.entries.associate { it.id to sonidos(it) }
    private const val TECLA = 9
    private const val RASCAR = 10

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
        runCatching { SONIDOS[Estilo.tema.id]?.take(TECLA)?.forEach { r -> if (r !in cargados) cargados[r] = pool().load(ctx.applicationContext, r, 1) } }
    }

    /**
     * Afinación: los sonidos con nota (laúd, lira, campanas, pitidos…) nunca se desafinan. El toque va recorriendo las
     * notas de la escala de su tema; pasar de pantalla y elegir, si tienen nota, se transponen a los grados del tema
     * ([Afinacion.giros]), así cada pestaña suena distinta pero todo queda en el mismo tono.
     * Nunca se repite la misma nota dos veces seguidas, y se mueve por pasos, como una melodía.
     * Los que son puro ruido (papel, pluma, madera, teclas) varían un poco de altura al azar.
     */
    /**
     * La afinación de cada tema. [toque]: las notas que va recorriendo el toque, en semitonos desde su nota (si el toque
     * es ruido, como la pluma o la máquina de escribir, son pasos cortos de su modo: cada tecla suena un poco distinta
     * sin salirse del tono). [giros]: los grados a los que se transponen pasar de pantalla y elegir cuando tienen nota
     * (null si ninguno la tiene). [pasarRuido] y [elegirRuido]: esos sonidos son ruido y solo varían un poco al azar.
     */
    internal class Afinacion(val toque: FloatArray, val giros: FloatArray?, val pasarRuido: Boolean, val elegirRuido: Boolean)
    private fun st(vararg semitonos: Int) = FloatArray(semitonos.size) { 2.0.pow(semitonos[it] / 12.0).toFloat() }
    internal fun afinacion(tema: TemaId): Afinacion = when (tema) {
        // Re mayor: el acorde de la tónica; mármol y papel.
        TemaId.ROMANO -> Afinacion(st(0, 4, 7, 12), st(-5, 0, 5, 7), pasarRuido = true, elegirRuido = false)
        // Re mayor de mandolina, con la sexta de los stornelli; los pasos sobre la piedra son ruido.
        TemaId.URBE -> Afinacion(st(0, 4, 9, 7, 2), st(-3, 0, 4, 7), pasarRuido = true, elegirRuido = false)
        // Mi menor, en blips.
        TemaId.CIBER -> Afinacion(st(-5, 0, 3, 7), st(-5, 0, 3, 7), pasarRuido = false, elegirRuido = false)
        // Fa sostenido menor con séptima, el acorde del synth-pop de los bares.
        TemaId.NEON -> Afinacion(st(-3, 0, 3, 7, 10), st(-7, 0, 3, 10), pasarRuido = false, elegirRuido = true)
        // La pentatónico de la lira.
        TemaId.HELENO -> Afinacion(st(0, 2, 4, 7, 9), st(-5, 0, 2, 7), pasarRuido = false, elegirRuido = false)
        // Mi con la segunda menor, para el arpa del templo (escala propia: no se sabe cómo afinaban); el sistro de pasar es ruido.
        TemaId.EGIPTO -> Afinacion(st(0, 1, 5, 7, 8), st(-5, 0, 1, 7), pasarRuido = true, elegirRuido = false)
        // Re dórico; la pluma solo da pasos cortos del modo.
        TemaId.MEDIEVAL -> Afinacion(st(-2, 0, 2, 3), null, pasarRuido = true, elegirRuido = true)
        // Sol mixolidio, el modo de los cantos de cruzada; el toque es el tintineo de la malla y elegir, el golpe de la espada en el escudo.
        TemaId.TEMPLARIO -> Afinacion(st(-5, -2, 0, 5), st(-7, 0, 5, 7), pasarRuido = true, elegirRuido = false)
        // Do menor; la máquina de escribir, un semitono abajo y un tono arriba.
        TemaId.DISTOPIA -> Afinacion(st(-1, 0, 2), null, pasarRuido = true, elegirRuido = true)
        // Fa pentatónico; el relé sube por tonos enteros.
        TemaId.REFUGIO -> Afinacion(st(0, 2, 4), null, pasarRuido = true, elegirRuido = true)
        // Sol menor; el servo baja a la cuarta.
        TemaId.ORDEN -> Afinacion(st(-5, -2, 0), st(-5, 0, 3, 5), pasarRuido = false, elegirRuido = false)
        // Re menor con la novena: misterio.
        TemaId.LABORATORIO -> Afinacion(st(-5, 0, 2, 3, 7), st(-5, 0, 1, 6), pasarRuido = false, elegirRuido = false)
        // Sol pentatónico con la quinta grave de la marimba.
        TemaId.COLONOS -> Afinacion(st(-5, 0, 2, 4, 7), st(-7, 0, 5, 7), pasarRuido = false, elegirRuido = false)
        // Si bemol: el tambor afinado en las notas de la corneta.
        TemaId.NAPOLEON -> Afinacion(st(-5, 0, 4, 7), null, pasarRuido = true, elegirRuido = true)
        // Sol dórico.
        TemaId.BOTANICO -> Afinacion(st(-5, 0, 3, 7, 9), st(-5, 0, 2, 5), pasarRuido = true, elegirRuido = false)
        // Re mixolidio con quintas al aire, como una flauta en una plaza vacía; el viento entre los edificios al pasar y al elegir.
        TemaId.RUINAS -> Afinacion(st(-7, 0, 2, 7, 10), st(-7, -2, 0, 7), pasarRuido = true, elegirRuido = true)
        // La menor con la sexta menor del órgano.
        TemaId.GOTICO -> Afinacion(st(-5, 0, 3, 7, 8), st(-5, 0, 3, 8), pasarRuido = false, elegirRuido = false)
        // Si locrio: la quinta disminuida que nunca termina de resolver; el papel del legajo pasa sin nota.
        TemaId.ARCANO -> Afinacion(st(-6, 0, 1, 5, 6), st(-6, -1, 0, 6), pasarRuido = true, elegirRuido = false)
        // Mi lidio: la cuarta aumentada de la sala de control.
        TemaId.ESPACIO -> Afinacion(st(0, 4, 6, 7, 11), st(-5, 0, 6, 7), pasarRuido = true, elegirRuido = false)
        // La escala de tonos enteros, sin centro: suena a agua y a sonar; el giro baja.
        TemaId.ABISMO -> Afinacion(st(0, 2, 4, 6, 8, 10), st(-10, -6, -2, 0), pasarRuido = true, elegirRuido = true)
        // Re hiyaz.
        TemaId.ANDALUS -> Afinacion(st(0, 1, 4, 5, 7), st(-5, 0, 1, 5), pasarRuido = false, elegirRuido = true)
        // Una pentatónica de flauta de barro (sin semitonos); la sonaja de pasar y el golpe de elegir son ruido.
        TemaId.MESO -> Afinacion(st(-5, -3, 0, 2, 5), st(-7, -3, 0, 5), pasarRuido = true, elegirRuido = true)
        // Mi pentatónico; los cascos, en pasos del modo.
        TemaId.MONGOL -> Afinacion(st(-5, -3, 0, 2), st(-5, 0, 2, 4), pasarRuido = true, elegirRuido = false)
        // La mayor; la pluma da pasos de la escala.
        TemaId.RENACIMIENTO -> Afinacion(st(-1, 0, 2, 4), st(-5, 0, 4, 5), pasarRuido = true, elegirRuido = false)
        // Re mixolidio, el de las salomas; el oleaje es ruido y el cabrestante da pasos del modo.
        TemaId.PIRATA -> Afinacion(st(-5, -2, 0, 4, 7), st(-7, -2, 0, 4), pasarRuido = true, elegirRuido = false)
        // La mayor: cascabeles en el acorde.
        TemaId.NAVIDAD -> Afinacion(st(-3, 0, 4, 7), st(0, 5, 7, 12), pasarRuido = true, elegirRuido = false)
        // La menor pentatónico; la seda da pasos del modo.
        TemaId.SEDA -> Afinacion(st(-2, 0, 3, 5), st(-7, -5, 0, 3), pasarRuido = true, elegirRuido = false)
        // Do menor; la prensa baja a la quinta.
        TemaId.URSS -> Afinacion(st(-5, -3, 0), st(-9, -5, 0, 3), pasarRuido = true, elegirRuido = false)
        // Re dórico; la piedra, en pasos graves del modo.
        TemaId.ANTIGUAS -> Afinacion(st(-5, -2, 0, 3), st(-5, -2, 0, 5), pasarRuido = true, elegirRuido = false)
        // Sol mayor; el tambor, en la quinta y el tono.
        TemaId.REVOLUCION -> Afinacion(st(-5, 0, 2), st(-7, 0, 4, 7), pasarRuido = true, elegirRuido = false)
    }
    internal fun acordeToque(tema: TemaId): FloatArray = afinacion(tema).toque
    /** ¿Ese sonido de ese tema es ruido sin nota? El toque siempre tiene escala. */
    private fun ruidoso(tema: TemaId, i: Int): Boolean = when (i) {
        3 -> afinacion(tema).pasarRuido
        4 -> afinacion(tema).elegirRuido
        else -> false
    }
    /** Qué notas puede tomar cada evento con nota. */
    private fun notas(tema: TemaId, i: Int): FloatArray? = when {
        i == 0 -> acordeToque(tema)
        ruidoso(tema, i) -> null
        i == 3 || i == 4 -> afinacion(tema).giros
        else -> null
    }

    /** La última nota de cada evento, para moverse por pasos y no repetir. */
    private val ultima = IntArray(8) { -1 }
    private var temaUltimo: TemaId? = null
    private fun siguiente(tema: TemaId, i: Int, n: Int): Int {
        if (temaUltimo != tema) { ultima.fill(-1); temaUltimo = tema }
        val u = ultima[i]
        val k = when {
            u < 0 -> n / 2
            n <= 1 -> 0
            else -> {
                val salto = if (azar.nextInt(4) == 0) 2 else 1
                val dir = if (azar.nextBoolean()) 1 else -1
                var k = u + dir * salto
                if (k !in 0 until n) k = u - dir * salto
                k.coerceIn(0, n - 1).let { if (it == u) (u + 1) % n else it }
            }
        }
        ultima[i] = k
        return k
    }

    private fun sonar(ctx: Context, tema: TemaId, e: Evento) {
        val i = when (e) { Evento.TOQUE -> 0; Evento.GUARDAR -> 1; Evento.LOGRO -> 2; Evento.PASAR -> 3; Evento.ELEGIR -> 4; Evento.ABRIR -> 5; Evento.TEMA -> 6; Evento.ERROR -> 7; Evento.CAMBIO -> 8 }
        val audio = ctx.getSystemService(AudioManager::class.java)
        if (audio != null && audio.ringerMode != AudioManager.RINGER_MODE_NORMAL) return
        val r = SONIDOS[tema.id]?.get(i) ?: return
        val vol = when (i) { 0, 4 -> 0.45f; 3 -> 0.38f; 5, 7 -> 0.6f; 8 -> 0.5f; 6 -> 0.75f; else -> 0.8f }
        val escala = notas(tema, i)
        val tono = when {
            escala != null -> escala[siguiente(tema, i, escala.size)]
            (i == 0 || i == 3 || i == 4) && ruidoso(tema, i) -> 0.94f + azar.nextFloat() * 0.12f
            else -> 1f
        }
        tocar(ctx, r, vol, tono)
    }

    private fun tocar(ctx: Context, r: Int, vol: Float, tono: Float = 1f) {
        val p = pool()
        val id = cargados.getOrPut(r) { p.load(ctx.applicationContext, r, 1) }
        if (id in listos) p.play(id, vol, vol, 1, 0, tono) else pendientes[id] = vol
    }

    private fun puedeSonar(ctx: Context): Boolean {
        if (!Repo.config.sonidos) return false
        val audio = ctx.getSystemService(AudioManager::class.java)
        return audio == null || audio.ringerMode == AudioManager.RINGER_MODE_NORMAL
    }

    /** Una tecla del diario, propia de cada tema: estilo sobre cera, cálamo, pluma, lápiz, pincel, máquina de escribir, consola… */
    fun tecla(ctx: Context, tema: TemaId) {
        if (!puedeSonar(ctx)) return
        val r = SONIDOS[tema.id]?.get(TECLA) ?: return
        runCatching { tocar(ctx, r, 0.32f, 0.95f + azar.nextFloat() * 0.1f) }
    }

    /** Tres pasadas de la moneda: [golpes] golpes de [prim] cada [paso] ms, con un respiro entre pasada y pasada. */
    private fun pasadas(prim: Int, fuerza: Float, paso: Int, golpes: Int): List<Golpe> =
        (0 until 3).flatMap { p -> (0 until golpes).map { k -> g(prim, fuerza * (if (k == golpes - 1) 1.15f else 1f), if (k == 0) (if (p == 0) 0 else 120) else paso) } }

    /** Cómo se siente rascar la lámina de cada tema: el metal blando apenas raspa y el acero golpetea. */
    internal fun patronRascar(tema: TemaId): List<Golpe> = when (tema) {
        TemaId.ROMANO -> pasadas(TICK, 0.5f, 60, 3)   // oro imperial, blando
        TemaId.URBE -> pasadas(TICK_GRAVE, 0.58f, 47, 4)   // la moneda de bronce de la fuente
        TemaId.CIBER -> pasadas(TICK, 0.9f, 30, 5)   // la placa de consola
        TemaId.NEON -> pasadas(TICK_GRAVE, 0.9f, 22, 6)   // la cortina metálica del puesto
        TemaId.HELENO -> pasadas(TICK, 0.55f, 55, 3)   // electro
        TemaId.EGIPTO -> pasadas(TICK_GRAVE, 0.4f, 70, 4)   // oro de Nubia, muy blando
        TemaId.MEDIEVAL -> pasadas(TICK_GRAVE, 0.5f, 70, 3)   // pan de oro
        TemaId.TEMPLARIO -> pasadas(CLICK, 0.65f, 50, 4)   // hierro de cota de malla
        TemaId.DISTOPIA -> pasadas(CLICK, 0.5f, 60, 3)   // chapa de ministerio
        TemaId.REFUGIO -> pasadas(TICK, 0.8f, 35, 4)   // chapa galvanizada
        TemaId.ORDEN -> pasadas(CLICK, 0.8f, 45, 3)   // acero pavonado
        TemaId.LABORATORIO -> pasadas(TICK, 0.4f, 30, 5)   // aluminio
        TemaId.COLONOS -> pasadas(TICK_GRAVE, 0.7f, 55, 3)   // cobre
        TemaId.NAPOLEON -> pasadas(CLICK, 0.6f, 50, 3)   // bronce dorado
        TemaId.BOTANICO -> pasadas(TICK_GRAVE, 0.45f, 65, 3)   // bronce con verdín
        TemaId.RUINAS -> pasadas(THUD, 0.38f, 58, 4)   // hormigón áspero
        TemaId.GOTICO -> pasadas(TICK_GRAVE, 0.6f, 80, 3)   // peltre
        TemaId.ARCANO -> pasadas(TICK_GRAVE, 0.52f, 95, 3)   // bronce comido por el salitre
        TemaId.ESPACIO -> pasadas(TICK, 0.85f, 28, 5)   // titanio
        TemaId.ABISMO -> pasadas(THUD, 0.5f, 95, 2)   // cobre con verdín de pecio, blando
        TemaId.ANDALUS -> pasadas(TICK, 0.6f, 45, 4)   // latón
        TemaId.MESO -> pasadas(CLICK, 0.7f, 33, 5)   // una lámina de jade pulido
        TemaId.MONGOL -> pasadas(CLICK, 0.55f, 55, 3)   // plata sobredorada
        TemaId.RENACIMIENTO -> pasadas(TICK, 0.5f, 65, 3)   // bronce de taller
        TemaId.PIRATA -> pasadas(TICK, 0.65f, 40, 4)   // un doblón de oro
        TemaId.NAVIDAD -> pasadas(TICK, 0.7f, 25, 5)   // plata escarchada
        TemaId.SEDA -> pasadas(TICK, 0.6f, 50, 3)   // bronce de moneda
        TemaId.URSS -> pasadas(CLICK, 0.9f, 40, 3)   // acero ennegrecido
        TemaId.ANTIGUAS -> pasadas(TICK_GRAVE, 0.8f, 60, 3)   // hierro viejo
        TemaId.REVOLUCION -> pasadas(CLICK, 0.45f, 50, 4)   // estaño
    }

    /** Una moneda que raspa la lámina del mapa: el sonido y la vibración de su metal. */
    fun rascar(ctx: Context) {
        val tema = Estilo.tema
        if (Repo.config.vibracion) runCatching { vibrar(ctx, patronRascar(tema)) }
        if (puedeSonar(ctx)) runCatching { SONIDOS[tema.id]?.get(RASCAR)?.let { tocar(ctx, it, 0.7f) } }
    }
}

/** Para usar desde la interfaz: `val sentir = rememberSentir(); sentir(Sentidos.Evento.GUARDAR)`. */
@Composable
fun rememberSentir(): (Sentidos.Evento) -> Unit {
    val ctx = LocalContext.current
    return remember(ctx) { { e: Sentidos.Evento -> Sentidos.sentir(ctx, e) } }
}
