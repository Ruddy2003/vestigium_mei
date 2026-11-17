package io.github.ruddy2003.vestigium.ui.escenas

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.delay

// =====================================================================
//  El ambiente del cuento y el ronroneo del gato (parte 8, encargo B; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================
//
// **El ambiente** es el sonido del lugar del cuadro (el mar y los remos en Helénico, la lluvia y el vapor del depósito en
// Vapor…), un bucle de unos 45 segundos por tema con escena: `res/raw/a_<tema>_ambiente.ogg` y, donde la escena cambia
// mucho de noche, `a_<tema>_ambiente_noche.ogg`. Los sintetiza `herramientas/ambientes.py`. Se busca por nombre: cada
// tanda de escenas nueva solo agrega su archivo.
//
// - **Solo en la pantalla del cuento** (y en su cuadro a pantalla completa, que se abre encima): empieza al abrirla, con un
//   fundido de 2 s, y se calla con uno de 1,5 s al salir, al irse la app a segundo plano o apagarse la pantalla (la
//   actividad se pausa), al entrar una llamada, al ponerse el teléfono en silencio o vibración y al perder el foco del
//   audio. **Nunca queda sonando:** cada reproductor se suelta al terminar su fundido.
// - **Cuándo suena** ([ReglasAmbiente.puedeEmpezar]): con su interruptor (`Config.ambiente_cuadro`) y el teléfono en
//   modo normal. **No depende del ajuste general de sonidos** (decisión del director, 17 nov 2026). No empieza si ya hay
//   música de otra app.
// - **El foco del audio:** se pide «pasajero, los demás pueden agacharse» (`AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK`), y solo
//   cuando no suena nada más, así que no baja nada de lo que estés oyendo; sirve para enterarse de cuando otra app quiere
//   sonar: una llamada o música lo callan; una notificación lo agacha un momento.
//
// **El ronroneo** (`s_gato_ronroneo.ogg`, común a todos los temas) suena al acariciar al gato del cuadro, además de la
// vibración, con las mismas condiciones que los demás efectos (con `Config.sonidos`). Las caricias **no se guardan** (D.190).

/** El ambiente del cuento: un reproductor en bucle a la vez, con sus fundidos. Todo se llama desde el hilo de la pantalla. */
object AmbienteCuadro {
    private val principal = Handler(Looper.getMainLooper())

    /** Un archivo sonando, con su volumen y su fundido en curso. */
    private class Pista(val reproductor: MediaPlayer, val nombre: String) {
        var volumen = 0f
        var fundido: Runnable? = null
        var suelta = false
        /** Ya preparado: antes de eso el reproductor no acepta que se le cambie el volumen. */
        var listo = false
    }

    private var actual: Pista? = null
    private val salientes = mutableListOf<Pista>()
    private var foco: AudioFocusRequest? = null

    private val atributos: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

    /** El id del recurso `res/raw/<nombre>`, o 0 si no existe. */
    internal fun idDe(ctx: Context, nombre: String): Int = ctx.resources.getIdentifier(nombre, "raw", ctx.packageName)

    /** El archivo del ambiente del [tema] (su id), de noche o de día; null si el tema no tiene. */
    fun elegir(ctx: Context, tema: String, noche: Boolean): String? = ReglasAmbiente.elegir(tema, noche) { idDe(ctx, it) != 0 }

    internal fun audio(ctx: Context): AudioManager? = ctx.getSystemService(AudioManager::class.java)

    /** El teléfono en modo normal: ni en silencio ni en vibración. */
    internal fun timbreNormal(a: AudioManager?): Boolean = a == null || a.ringerMode == AudioManager.RINGER_MODE_NORMAL

    /** Suena o está entrando una llamada (o una videollamada, o una llamada en filtro). */
    internal fun enLlamada(a: AudioManager?): Boolean = a != null && a.mode != AudioManager.MODE_NORMAL

    /** Empieza (o sigue) el ambiente [nombre], con su fundido de entrada. Si sonaba otro, ese se va con su fundido de salida. */
    fun empezar(ctx: Context, nombre: String) {
        val app = ctx.applicationContext
        contexto = app
        actual?.let { p ->
            if (p.nombre == nombre) { fundir(p, ReglasAmbiente.VOLUMEN, ReglasAmbiente.ENTRADA_MS); return }
            salir(p)
        }
        actual = null
        val a = audio(app)
        if (!ReglasAmbiente.puedeEmpezar(Repo.config.ambiente_cuadro, timbreNormal(a), enLlamada(a), a?.isMusicActive == true)) { soltarFoco(); return }
        val id = idDe(app, nombre)
        if (id == 0) { soltarFoco(); return }
        if (a != null && !pedirFoco(a)) return
        val mp = MediaPlayer()
        val p = Pista(mp, nombre)
        try {
            mp.setAudioAttributes(atributos)
            app.resources.openRawResourceFd(id).use { afd -> mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length) }
            mp.isLooping = true
            mp.setVolume(0f, 0f)
            mp.setOnPreparedListener {
                if (actual === p && !p.suelta) {
                    p.listo = true; p.volumen = 0f
                    runCatching { it.start() }
                    fundir(p, ReglasAmbiente.VOLUMEN, ReglasAmbiente.ENTRADA_MS)
                } else soltar(p)
            }
            mp.setOnErrorListener { _, _, _ -> soltar(p); if (actual === p) { actual = null; soltarFoco() }; true }
            actual = p
            mp.prepareAsync()
        } catch (e: Exception) {
            soltar(p); if (actual === p) actual = null; soltarFoco()
        }
    }

    /** Se calla con su fundido de salida (y suelta el reproductor al terminar). Si no sonaba nada, no hace nada. */
    fun callar() {
        val p = actual ?: return
        actual = null
        salir(p)
        soltarFoco()
    }

    /**
     * Lo que se revisa cada poco mientras la pantalla del cuento está abierta: si el teléfono pasó a silencio o vibración,
     * si entró una llamada o si se apagó el interruptor, se calla.
     */
    fun vigilar(ctx: Context) {
        if (actual == null) return
        val a = audio(ctx.applicationContext)
        if (!ReglasAmbiente.puedeSeguir(Repo.config.ambiente_cuadro, timbreNormal(a), enLlamada(a))) callar()
    }

    // ---------------------------------------------------------------- fundidos y reproductores

    private fun salir(p: Pista) {
        if (p.suelta) return
        if (p !in salientes) salientes += p
        fundir(p, 0f, ReglasAmbiente.SALIDA_MS) { soltar(p) }
    }

    /** Lleva el volumen de [p] a [meta] en [duracion] ms ([ReglasAmbiente.rampa]); al llegar, [luego]. Cancela el fundido anterior. */
    private fun fundir(p: Pista, meta: Float, duracion: Long, luego: (() -> Unit)? = null) {
        p.fundido?.let { principal.removeCallbacks(it) }
        val desde = p.volumen
        val t0 = SystemClock.uptimeMillis()
        val paso = object : Runnable {
            override fun run() {
                if (p.suelta) return
                val ms = SystemClock.uptimeMillis() - t0
                val v = ReglasAmbiente.rampa(desde, meta, ms, duracion)
                p.volumen = v
                if (p.listo) runCatching { p.reproductor.setVolume(v, v) }
                if (ms >= duracion) { p.fundido = null; luego?.invoke() } else principal.postDelayed(this, ReglasAmbiente.PASO_MS)
            }
        }
        p.fundido = paso
        paso.run()
    }

    private fun soltar(p: Pista) {
        if (p.suelta) return
        p.suelta = true
        p.fundido?.let { principal.removeCallbacks(it) }
        p.fundido = null
        // release() vale en cualquier estado (también mientras se prepara) y lo detiene.
        runCatching { p.reproductor.release() }
        salientes.remove(p)
    }

    // ---------------------------------------------------------------- el foco del audio

    private val alCambiarFoco = AudioManager.OnAudioFocusChangeListener { cambio ->
        when (cambio) {
            // Otra app quiere sonar (música, una llamada, el asistente): el ambiente se va, y no vuelve hasta abrir otra vez el cuento.
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> callar()
            // Un sonido corto (una notificación): se agacha y vuelve.
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> actual?.let { fundir(it, ReglasAmbiente.VOLUMEN_AGACHADO, 300L) }
            AudioManager.AUDIOFOCUS_GAIN -> actual?.let { fundir(it, ReglasAmbiente.VOLUMEN, 600L) }
        }
    }

    private fun pedirFoco(a: AudioManager): Boolean {
        val pedido = foco ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(atributos).setWillPauseWhenDucked(false)
            .setOnAudioFocusChangeListener(alCambiarFoco, principal).build()
        val r = runCatching { a.requestAudioFocus(pedido) }.getOrDefault(AudioManager.AUDIOFOCUS_REQUEST_FAILED)
        if (r != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) return false
        foco = pedido
        return true
    }

    private var contexto: Context? = null

    private fun soltarFoco() {
        val pedido = foco ?: return
        foco = null
        contexto?.let { c -> runCatching { audio(c)?.abandonAudioFocusRequest(pedido) } }
    }
}

/** El ronroneo del gato al acariciarlo: corto (unos 1,6 s) y común a todos los temas. Desde el hilo de la pantalla. */
object Ronroneo {
    private var sonando: MediaPlayer? = null

    private val atributos: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

    /** Ronronea, si los sonidos de la app están encendidos y el teléfono en modo normal. Si ya ronronea, no empieza otro encima. */
    fun sonar(ctx: Context) {
        val app = ctx.applicationContext
        val a = AmbienteCuadro.audio(app)
        if (!ReglasAmbiente.puedeRonronear(Repo.config.sonidos, AmbienteCuadro.timbreNormal(a), AmbienteCuadro.enLlamada(a))) return
        if (sonando != null) return
        val id = AmbienteCuadro.idDe(app, ReglasAmbiente.RONRONEO)
        if (id == 0) return
        val mp = MediaPlayer()
        fun soltar() { runCatching { mp.release() }; if (sonando === mp) sonando = null }
        try {
            mp.setAudioAttributes(atributos)
            app.resources.openRawResourceFd(id).use { afd -> mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length) }
            mp.setVolume(ReglasAmbiente.VOLUMEN_RONRONEO, ReglasAmbiente.VOLUMEN_RONRONEO)
            mp.setOnPreparedListener { runCatching { it.start() }.onFailure { soltar() } }
            mp.setOnCompletionListener { soltar() }
            mp.setOnErrorListener { _, _, _ -> soltar(); true }
            sonando = mp
            mp.prepareAsync()
        } catch (e: Exception) { soltar() }
    }
}

/**
 * Pone el ambiente del cuadro del [tema] (su id; null: sin cuadro, sin ambiente), de noche si [noche], mientras esta
 * pantalla esté en la composición **y** la app en primer plano: empieza al reanudarse la actividad y se calla al pausarse
 * (otra app, pantalla apagada, una llamada que la tapa) y al salir. Cada poco revisa el modo del teléfono y las llamadas.
 * En las capturas no hace nada.
 */
@Composable
fun AmbienteDelCuadro(tema: String?, noche: Boolean) {
    if (LocalInspectionMode.current) return
    val ctx = LocalContext.current
    val ciclo = LocalLifecycleOwner.current
    val encendido = Repo.config.ambiente_cuadro
    DisposableEffect(tema, noche, encendido, ciclo) {
        val nombre = if (tema != null && encendido) AmbienteCuadro.elegir(ctx, tema, noche) else null
        fun poner() { if (nombre != null) AmbienteCuadro.empezar(ctx, nombre) else AmbienteCuadro.callar() }
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> poner()
                Lifecycle.Event.ON_PAUSE -> AmbienteCuadro.callar()
                else -> Unit
            }
        }
        // Si la actividad ya está reanudada, agregar el observador le manda ON_RESUME enseguida: empieza ahí.
        ciclo.lifecycle.addObserver(observador)
        onDispose {
            ciclo.lifecycle.removeObserver(observador)
            AmbienteCuadro.callar()
        }
    }
    LaunchedEffect(tema, noche, encendido) {
        while (true) { delay(700); AmbienteCuadro.vigilar(ctx) }
    }
}
