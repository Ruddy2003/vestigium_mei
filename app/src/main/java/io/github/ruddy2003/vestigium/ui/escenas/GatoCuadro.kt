package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.vidas.Clima
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

// =====================================================================
//  El gato del cuadro: cómo se porta (plan de historia y escenas, sección 3.4; tandas P5a y P5b, 12 nov 2026)
// =====================================================================
//
// El gato vive dentro del cuadro de Hoy: camina por el suelo del primer plano de la escena de su tema. Está dibujado
// con código (`GatoEsqueleto.kt` y `GatoDibujo.kt`) y recibe la luz de la escena.
//
// Se porta igual que el gato de pixel (`ui/Gato.kt`): la misma máquina de estados, con los mismos tiempos y las mismas
// probabilidades, y el mismo ánimo (`Gato.humor`, que sale de cómo dormiste y de cómo calificaste el día). Lo único
// que cambia es dónde camina (el tramo de suelo que le da la escena, en vez de una banda) y la escala de sus pasos.
//
// Además reacciona a la escena (P5b), por las anclas que ella le da: se echa junto al fuego cuando está encendido, va
// a oler el objeto del día, se sube al mirador a mirar, se guarece cuando llueve o nieva y mira los adornos de las
// fiestas. Y al dedo: con un toque sobre él se deja acariciar; con un toque en otro sitio, mira o va hacia ahí.
//
// Nada de esto se guarda. Con las animaciones apagadas, con ahorro de energía o en las capturas, se queda sentado.

/**
 * Un cuadro del gato: qué animación y cuál de sus cuadros, dónde pisa ([x], en las unidades del lienzo) y hacia dónde
 * mira ([dir]: 1 a la derecha, −1 a la izquierda). [dy]: cuánto está en el aire. [marcha]: lo larga que es su
 * zancada (1 es la normal). [especial]: cuál de las acciones propias del tema, si [anim] es [AnimGato.ESPECIAL].
 * [alza]: cuánto más arriba que el suelo pisa (negativo: está subido al mirador). [escala]: su tamaño respecto al de
 * abajo (subido queda un poco más lejos, y más chico).
 */
class PasoGato(
    val anim: AnimGato, val i: Int, val x: Float, val dir: Int, val dy: Float = 0f, val marcha: Float = 1f, val especial: Int = -1,
    val alza: Float = 0f, val escala: Float = 1f,
)

/** Lo que está haciendo el gato. Los trece primeros son los estados del gato de pixel; los demás, sus reacciones a la escena y al dedo. */
enum class EstadoGato {
    CAMINAR, QUIETO, SALTAR, JUGAR, ERIZARSE, SENTARSE, LEVANTARSE, ECHARSE, DESPERTAR, SENTADO, ASEARSE, DORMIR, ESPECIAL,
    /** Va a un sitio (un ancla, o donde tocaste). */
    IR,
    /** Huele el objeto del día. */
    OLER,
    /** Sentado, mira hacia arriba el adorno de la fiesta y le da con la mano. */
    ADORNO,
    /** Salta al mirador, se queda ahí mirando, de espaldas, y baja. */
    SUBIR, VIGIA, BAJAR,
    /** Se deja acariciar. */
    MIMO,
    /** Mira hacia donde tocaste. */
    ATENTO,
}

/**
 * La máquina de estados del gato, cuadro a cuadro (12,5 por segundo, como el de pixel).
 *
 * Camina, se detiene, se sienta, se asea, se echa a dormir, salta, juega y hace las acciones propias de su tema.
 * Alegre: juega y salta más. Triste: camina lento con la cola baja y pasa más rato sentado. Enojado: a ratos se
 * eriza. Cansado: camina al 70 %, salta menos y se echa más, con siestas más largas. De noche, casi solo duerme.
 *
 * - [desde] y [hasta]: el tramo de suelo por el que camina, en las unidades del lienzo.
 * - [especiales]: las acciones propias de su tema (las del pelaje); puede no haber ninguna.
 * - [talla]: su tamaño; con él cambian el largo de sus pasos y de sus saltos.
 * - [alCambiar]: avisa de cada cambio de estado (para las pruebas).
 * - Con la misma [semilla] (y los mismos toques en los mismos cuadros) hace siempre lo mismo.
 *
 * Mientras no se le dé un [entorno], se porta exactamente como el gato de pixel. Con él, a ratos deja lo que hacía
 * para ir a una de las anclas de la escena.
 */
class SimulacionGato(
    private val humor: Gato.Humor, semilla: Long, private val desde: Float, private val hasta: Float,
    private val especiales: List<EspecialGato> = emptyList(), private val talla: Float = 1f,
    private val alCambiar: ((EstadoGato) -> Unit)? = null,
) {
    private val rnd = java.util.Random(semilla)
    private val cola = ArrayDeque<PasoGato>()
    private val triste = humor.animo == Gato.Animo.TRISTE
    private val cam = if (triste) AnimGato.CAMINAR_TRISTE else AnimGato.CAMINAR
    private val qui = if (triste) AnimGato.QUIETO_TRISTE else AnimGato.QUIETO

    /** Cuánto de su paso normal: el ánimo y el cansancio lo acortan o lo alargan. */
    val marcha: Float = (when (humor.animo) { Gato.Animo.TRISTE -> 0.6f; Gato.Animo.ALEGRE -> 1.15f; else -> 1f }) * (if (humor.cansado) 0.7f else 1f)
    private val vel = VELOCIDAD * talla * marcha
    private val margen = Gatuno.MEDIO_LARGO * talla
    /** Si su tramo de suelo da para caminar. Si no, se queda donde está: no camina, ni salta, ni da vueltas en el sitio. */
    private val puedeCaminar = hasta - desde >= 2 * margen + 12f * talla
    private var x = (desde + hasta) / 2
    private var dir = 1
    private var i = 0
    private var especial = 0

    // Por dónde puede andar ahora: su tramo de suelo, o un trecho junto al abrigo mientras llueve.
    private var izquierda = desde
    private var derecha = hasta

    /** Lo que hace ahora mismo. */
    var estado: EstadoGato = if (humor.noche) EstadoGato.DORMIR else if (puedeCaminar) EstadoGato.CAMINAR else EstadoGato.QUIETO
        private set
    private var resto = durar(estado)

    private val tCaminar: List<Pair<EstadoGato, Double>>
    private val tQuieto: List<Pair<EstadoGato, Double>>
    private val tSentado: List<Pair<EstadoGato, Double>>

    // ---- Las reacciones (P5b).
    private var anclas: List<Ancla> = emptyList()
    private var sueloY = 0f
    private var mojado = false
    private var guarecido = false
    private var destino = 0f
    private var motivo: Ancla? = null                  // a qué ancla va (null: a donde tocaste)
    private var ultimoMotivo: TipoAncla? = null
    private var enfriar = 0                            // cuadros que faltan para que pueda reaccionar otra vez
    private var calor = false                          // está junto al fuego: se queda más y acaba echándose
    private var alza = 0f                              // subido al mirador
    private var escala = 1f
    private var base = 0f                              // desde dónde saltó al mirador, para bajar al mismo sitio
    private var caricia = false                        // toques que esperan su momento
    private var toque: Float? = null
    private var ultimo = PasoGato(if (humor.noche) AnimGato.DORMIR else AnimGato.SENTADO, 0, x, dir)

    init {
        var c = when (humor.animo) {
            Gato.Animo.ALEGRE -> listOf(EstadoGato.JUGAR to 0.4, EstadoGato.SALTAR to 0.3, EstadoGato.QUIETO to 0.1, EstadoGato.SENTARSE to 0.1, EstadoGato.CAMINAR to 0.1)
            Gato.Animo.TRISTE -> listOf(EstadoGato.QUIETO to 0.35, EstadoGato.SENTARSE to 0.45, EstadoGato.CAMINAR to 0.2)
            Gato.Animo.ENOJADO -> listOf(EstadoGato.ERIZARSE to 0.45, EstadoGato.QUIETO to 0.25, EstadoGato.SENTARSE to 0.15, EstadoGato.CAMINAR to 0.15)
            Gato.Animo.NORMAL -> listOf(EstadoGato.QUIETO to 0.3, EstadoGato.SENTARSE to 0.3, EstadoGato.SALTAR to 0.2, EstadoGato.JUGAR to 0.1, EstadoGato.CAMINAR to 0.1)
        }
        val q = when (humor.animo) {
            Gato.Animo.ALEGRE -> listOf(EstadoGato.JUGAR to 0.4, EstadoGato.SALTAR to 0.2, EstadoGato.CAMINAR to 0.3, EstadoGato.ESPECIAL to 0.1)
            Gato.Animo.TRISTE -> listOf(EstadoGato.CAMINAR to 0.4, EstadoGato.SENTARSE to 0.6)
            Gato.Animo.ENOJADO -> listOf(EstadoGato.ERIZARSE to 0.4, EstadoGato.CAMINAR to 0.6)
            Gato.Animo.NORMAL -> listOf(EstadoGato.CAMINAR to 0.45, EstadoGato.SENTARSE to 0.25, EstadoGato.SALTAR to 0.1, EstadoGato.JUGAR to 0.1, EstadoGato.ESPECIAL to 0.1)
        }
        var s = when (humor.animo) {
            Gato.Animo.ALEGRE -> listOf(EstadoGato.LEVANTARSE to 0.5, EstadoGato.ASEARSE to 0.25, EstadoGato.ESPECIAL to 0.25)
            Gato.Animo.TRISTE -> listOf(EstadoGato.ECHARSE to 0.5, EstadoGato.ASEARSE to 0.1, EstadoGato.LEVANTARSE to 0.3, EstadoGato.ESPECIAL to 0.1)
            Gato.Animo.ENOJADO -> listOf(EstadoGato.LEVANTARSE to 0.6, EstadoGato.ASEARSE to 0.4)
            Gato.Animo.NORMAL -> listOf(EstadoGato.ASEARSE to 0.35, EstadoGato.ECHARSE to 0.25, EstadoGato.ESPECIAL to 0.25, EstadoGato.LEVANTARSE to 0.15)
        }
        if (humor.cansado) {
            // Salta y juega la mitad; lo que sobra se va a sentarse. Se echa más seguido.
            val menos = c.filter { it.first == EstadoGato.SALTAR || it.first == EstadoGato.JUGAR }.sumOf { it.second } / 2
            c = c.map { (n, p) -> n to if (n == EstadoGato.SALTAR || n == EstadoGato.JUGAR) p / 2 else p } + (EstadoGato.SENTARSE to menos)
            s = s.map { (n, p) -> n to if (n == EstadoGato.ECHARSE) p + 0.2 else p }
        }
        if (humor.noche) s = s.map { (n, p) -> n to if (n == EstadoGato.ECHARSE) p + 0.6 else p }
        tCaminar = c; tQuieto = q; tSentado = s
    }

    private fun u(a: Double, b: Double) = a + rnd.nextDouble() * (b - a)

    private fun durar(e: EstadoGato): Int {
        val seg = when (e) {
            EstadoGato.CAMINAR -> u(1.6, 3.2)
            EstadoGato.QUIETO -> u(1.0, 2.0)
            EstadoGato.SENTADO -> u(1.0, 2.0) * (if (triste) 1.6 else 1.0)
            EstadoGato.DORMIR -> u(3.0, 4.5) * (if (humor.cansado) 1.5 else 1.0) * (if (humor.noche) 3.0 else 1.0)
            EstadoGato.ERIZARSE -> u(1.5, 2.4)
            EstadoGato.OLER -> u(2.0, 3.4)
            EstadoGato.ADORNO -> u(3.0, 4.6)
            EstadoGato.VIGIA -> u(5.0, 9.0)
            EstadoGato.ATENTO -> u(1.4, 2.0)
            else -> 0.0
        }
        return (seg / DT).toInt()
    }

    private fun elegir(op: List<Pair<EstadoGato, Double>>): EstadoGato {
        var u = rnd.nextDouble() * op.sumOf { it.second }
        for ((n, p) in op) { u -= p; if (u <= 0) return n }
        return op.last().first
    }

    private fun cambiar(nuevo: EstadoGato) {
        var n = nuevo
        if (!puedeCaminar && (n == EstadoGato.CAMINAR || n == EstadoGato.SALTAR || n == EstadoGato.JUGAR)) n = EstadoGato.QUIETO
        if (n == EstadoGato.ESPECIAL) {
            // Solo las acciones del tema que parten de esta postura (sentado o de pie).
            val sentado = estado == EstadoGato.SENTADO
            val ok = especiales.indices.filter { especiales[it].sentado == sentado && especiales[it].cuadros > 0 }
            if (ok.isEmpty()) { n = if (sentado) EstadoGato.ASEARSE else if (puedeCaminar) EstadoGato.CAMINAR else EstadoGato.SENTARSE } else { especial = ok[rnd.nextInt(ok.size)] }
        }
        estado = n; i = 0; resto = durar(n)
        alCambiar?.invoke(n)
    }

    private fun pon(anim: AnimGato, k: Int, dy: Float = 0f, esp: Int = -1) = cola.addLast(PasoGato(anim, k, x, dir, dy, marcha, esp, alza, escala))
    private fun cabe(xx: Float, d: Int) = if (d > 0) xx + margen <= derecha else xx - margen >= izquierda

    /** El siguiente cuadro. */
    fun siguiente(): PasoGato {
        atenderAlDedo()
        if (mojado && !guarecido && !(estado == EstadoGato.IR && motivo?.tipo == TipoAncla.ABRIGO)) buscarAbrigo()
        while (cola.isEmpty()) avanzar()
        return cola.removeFirst().also { ultimo = it }
    }

    // ---------------------------------------------------------------- La escena y el dedo

    /**
     * Lo que la escena le ofrece ahora: sus [anclas] (el fuego solo está si está encendido; el adorno, si hay
     * fiesta), a qué altura está su suelo y si llueve o nieva ([mojado]). Se le dice cada vez que cambia el momento.
     */
    fun entorno(anclas: List<Ancla>, sueloY: Float, mojado: Boolean) {
        this.anclas = anclas.filter { it.tipo != TipoAncla.SUELO }; this.sueloY = sueloY
        if (mojado != this.mojado) { this.mojado = mojado; if (!mojado) { guarecido = false; izquierda = desde; derecha = hasta } }
    }

    /** Un toque sobre el gato: se deja acariciar en cuanto puede (si va por el aire, al caer). */
    fun acariciar() { caricia = true }

    /** Un toque en otro sitio del cuadro, a la altura [x] del lienzo: si queda cerca, mira hacia ahí; si queda lejos, va. */
    fun tocar(x: Float) { toque = x }

    /** Dónde está ahora: para saber si un toque cayó sobre él. */
    val donde: PasoGato get() = ultimo

    /** Si puede dejar lo que hace ahora mismo: está pisando el suelo (no va por el aire ni está subido). */
    private fun libre() = ultimo.dy == 0f && ultimo.alza == 0f

    /** Deja lo que tenía pensado hacer y se queda donde está el último cuadro que se vio. */
    private fun cortar() { cola.clear(); x = ultimo.x; dir = ultimo.dir; alza = 0f; escala = 1f }

    private fun echado() = ultimo.anim == AnimGato.DORMIR || ultimo.anim == AnimGato.ECHARSE || ultimo.anim == AnimGato.MIMO_ECHADO
    private fun sentado() = ultimo.anim == AnimGato.SENTADO || ultimo.anim == AnimGato.ASEARSE || ultimo.anim == AnimGato.MIMO || ultimo.anim == AnimGato.ARRIBA ||
        ultimo.anim == AnimGato.MIRAR_SENTADO || (ultimo.anim == AnimGato.ESPECIAL && especiales.getOrNull(ultimo.especial)?.sentado != false)

    private fun atenderAlDedo() {
        if (!caricia && toque == null) return
        // En el mirador (ya sentado arriba, no a medio brinco): mueve las orejas y sigue mirando.
        if (caricia && estado == EstadoGato.VIGIA && ultimo.anim == AnimGato.VIGIA && cola.all { it.anim == AnimGato.VIGIA }) {
            caricia = false; toque = null; cola.clear(); repeat(14) { pon(AnimGato.VIGIA, 1 - (it / 4) % 2) }; return
        }
        if (!libre()) return
        cortar()
        if (caricia) {
            caricia = false; toque = null
            if (echado()) { repeat(22) { pon(AnimGato.MIMO_ECHADO, (it / 4) % 2) }; estado = EstadoGato.DORMIR; if (resto < 20) resto = 20; return }
            if (!sentado()) for (k in 0 until AnimGato.SENTARSE.cuadros) pon(AnimGato.SENTARSE, k)
            repeat(24) { pon(AnimGato.MIMO, (it / 3) % 4) }
            estado = EstadoGato.SENTADO; cambiar(EstadoGato.SENTADO)
            return
        }
        val t = toque ?: return
        toque = null
        if (echado()) return                                                    // dormido no se entera
        val lejos = abs(t - x) > 60f * talla && puedeCaminar && !guarecido
        if (sentado() && lejos) for (k in AnimGato.SENTARSE.cuadros - 1 downTo 0) pon(AnimGato.SENTARSE, k)
        if (lejos) { irA(t.coerceIn(izquierda + margen, derecha - margen), null) } else {
            if (!sentado()) mirarHacia(t)
            cambiar(EstadoGato.ATENTO)
        }
    }

    /** Empezó a llover y tiene dónde guarecerse: deja lo que hace (se despierta, se levanta) y va. */
    private fun buscarAbrigo() {
        val a = anclas.firstOrNull { it.tipo == TipoAncla.ABRIGO } ?: return
        if (!puedeCaminar || !libre()) return
        cortar()
        if (echado()) for (k in AnimGato.ECHARSE.cuadros - 1 downTo 0) repeat(2) { pon(AnimGato.ECHARSE, k) }
        if (echado() || sentado()) for (k in AnimGato.SENTARSE.cuadros - 1 downTo 0) pon(AnimGato.SENTARSE, k)
        calor = false
        irA(sitioDe(a), a)
    }

    /** Se vuelve hacia [xx] si le queda a la espalda. */
    private fun mirarHacia(xx: Float) {
        val d = if (xx >= x) 1 else -1
        if (d == dir || abs(xx - x) < 6f) return
        repeat(3) { pon(AnimGato.GIRAR, 0) }; repeat(4) { pon(AnimGato.GIRAR, 1) }
        dir = d
        repeat(3) { pon(AnimGato.GIRAR, 0) }
    }

    private fun irA(xx: Float, ancla: Ancla?) { destino = xx; motivo = ancla; cambiar(EstadoGato.IR) }

    /** Dónde se pone para un ancla: en su sitio junto al fuego, al lado del objeto (no encima), debajo del adorno, al pie del mirador. */
    private fun sitioDe(a: Ancla): Float {
        val tope0 = margen + 2f; val tope1 = ANCHO_LIENZO - margen - 2f
        return when (a.tipo) {
            // El ancla del fuego ya es el sitio donde se echa (su `hasta` dice dónde está la lumbre).
            TipoAncla.FUEGO -> a.x.coerceIn(desde + margen, hasta - margen)
            TipoAncla.OBJETO, TipoAncla.MIRADOR -> {
                val aparte = if (a.tipo == TipoAncla.MIRADOR) 20f * talla else margen + 8f * talla
                val izq = a.x - aparte; val der = a.x + aparte
                val cabeIzq = izq - margen >= desde; val cabeDer = der + margen <= hasta
                when { cabeIzq && cabeDer -> if (abs(izq - x) <= abs(der - x)) izq else der; cabeIzq -> izq; cabeDer -> der; else -> a.x.coerceIn(desde + margen, hasta - margen) }
            }
            // El abrigo puede quedar fuera de su tramo de siempre (bajo un alero, al borde del cuadro).
            TipoAncla.ABRIGO -> a.x.coerceIn(tope0, tope1)
            else -> a.x.coerceIn(desde + margen, hasta - margen)
        }
    }

    /**
     * A ratos, al acabar de caminar o de estar quieto, va a una de las anclas en vez de seguir a lo suyo. Si llueve y
     * tiene dónde, se guarece y ya no sale de ahí. Devuelve si se fue.
     */
    private fun reaccionar(): Boolean {
        if (anclas.isEmpty() || !puedeCaminar) return false
        if (mojado && !guarecido) anclas.firstOrNull { it.tipo == TipoAncla.ABRIGO }?.let { irA(sitioDe(it), it); return true }
        if (guarecido || enfriar > 0) return false
        val ganas = (when (humor.animo) { Gato.Animo.ALEGRE -> 0.34; Gato.Animo.NORMAL -> 0.26; Gato.Animo.TRISTE -> 0.12; Gato.Animo.ENOJADO -> 0.06 }) * (if (humor.cansado) 0.6 else 1.0)
        if (rnd.nextDouble() >= ganas) return false
        val posibles = anclas.filter { it.tipo != TipoAncla.ABRIGO && it.tipo != ultimoMotivo }.map { a ->
            a to when (a.tipo) {
                TipoAncla.FUEGO -> if (humor.cansado || triste || humor.noche) 5.0 else 3.0
                TipoAncla.ADORNO -> 3.0
                TipoAncla.OBJETO -> 2.0
                TipoAncla.MIRADOR -> if (humor.cansado || triste || humor.noche) 0.3 else 2.0
                else -> 0.0
            }
        }.filter { it.second > 0.0 }
        if (posibles.isEmpty()) { ultimoMotivo = null; return false }
        var v = rnd.nextDouble() * posibles.sumOf { it.second }
        val a = posibles.firstOrNull { v -= it.second; v <= 0 }?.first ?: posibles.last().first
        irA(sitioDe(a), a)
        return true
    }

    /** Llegó a donde iba. */
    private fun llegar() {
        val a = motivo
        if (a == null) { cambiar(EstadoGato.ATENTO); return }
        ultimoMotivo = a.tipo; enfriar = (u(7.0, 14.0) / DT).toInt()
        when (a.tipo) {
            TipoAncla.FUEGO -> { mirarHacia(a.hasta); calor = true; cambiar(EstadoGato.SENTARSE) }
            TipoAncla.OBJETO -> { mirarHacia(a.x); cambiar(EstadoGato.OLER) }
            TipoAncla.ADORNO -> { for (k in 0 until AnimGato.SENTARSE.cuadros) repeat(2) { pon(AnimGato.SENTARSE, k) }; cambiar(EstadoGato.ADORNO) }
            TipoAncla.MIRADOR -> { mirarHacia(a.x); cambiar(EstadoGato.SUBIR) }
            TipoAncla.ABRIGO -> {
                // Ya a cubierto, no sale de ahí mientras llueva: se sienta, se asea, duerme.
                guarecido = true; izquierda = (x - margen - 14f * talla).coerceAtLeast(0f); derecha = (x + margen + 14f * talla).coerceAtMost(ANCHO_LIENZO)
                cambiar(EstadoGato.SENTARSE)
            }
            TipoAncla.SUELO -> cambiar(EstadoGato.QUIETO)
        }
    }

    /** El brinco al mirador (o de vuelta): doce cuadros en los que sube, se aleja un poco y se achica. */
    private fun brinco(x0: Float, x1: Float, alza0: Float, alza1: Float, e0: Float, e1: Float) {
        val alto = abs(alza1 - alza0)
        for (k in 0 until 6) repeat(2) { j ->
            val t = (k * 2 + j + 1) / 12f
            val suave = t * t * (3f - 2f * t)
            x = x0 + (x1 - x0) * suave; alza = alza0 + (alza1 - alza0) * suave; escala = e0 + (e1 - e0) * suave
            pon(AnimGato.SALTAR, k, -0.28f * alto * sin(PI.toFloat() * t))
        }
        x = x1; alza = alza1; escala = e1
    }

    private fun avanzar() {
        val k0 = ESCALA * talla
        if (enfriar > 0) enfriar--
        when (estado) {
            EstadoGato.CAMINAR -> {
                x += dir * vel * DT
                if (!cabe(x, dir)) {
                    x -= dir * vel * DT
                    repeat(3) { pon(AnimGato.GIRAR, 0) }; repeat(4) { pon(AnimGato.GIRAR, 1) }
                    dir = -dir
                    repeat(3) { pon(AnimGato.GIRAR, 0) }
                }
                pon(cam, i % 8); i++; resto--
                if (resto <= 0 && !reaccionar()) cambiar(elegir(tCaminar))
            }
            EstadoGato.QUIETO -> { pon(qui, (i / 3) % 4); i++; resto--; if (resto <= 0 && !reaccionar()) cambiar(elegir(tQuieto)) }
            EstadoGato.SALTAR -> {
                if (!cabe(x + dir * 34 * k0, dir)) { cambiar(EstadoGato.QUIETO); return }
                for (k in 0 until 6) repeat(2) { x += dir * SALTO_DX[k] * k0; pon(AnimGato.SALTAR, k, SALTO_DY[k] * k0) }
                cambiar(elegir(listOf(EstadoGato.CAMINAR to 0.6, EstadoGato.QUIETO to 0.4)))
            }
            EstadoGato.JUGAR -> {
                if (!cabe(x + dir * 10 * k0, dir)) { cambiar(EstadoGato.QUIETO); return }
                for ((k, n, dx) in JUEGO) repeat(n) { x += dir * dx * k0; pon(AnimGato.JUGAR, k, if (k == 2) -3f * k0 else 0f) }
                cambiar(elegir(listOf(EstadoGato.CAMINAR to 0.5, EstadoGato.QUIETO to 0.2, EstadoGato.JUGAR to if (humor.animo == Gato.Animo.ALEGRE) 0.3 else 0.0)))
            }
            EstadoGato.ERIZARSE -> { pon(AnimGato.ENOJADO, (i / 2) % 3); i++; resto--; if (resto <= 0) cambiar(EstadoGato.QUIETO) }
            EstadoGato.SENTARSE, EstadoGato.LEVANTARSE, EstadoGato.ECHARSE, EstadoGato.DESPERTAR -> {
                val base = if (estado == EstadoGato.SENTARSE || estado == EstadoGato.LEVANTARSE) AnimGato.SENTARSE else AnimGato.ECHARSE
                val n = base.cuadros
                val orden = if (estado == EstadoGato.SENTARSE || estado == EstadoGato.ECHARSE) (0 until n) else (n - 1 downTo 0)
                for (k in orden) repeat(2) { pon(base, k) }
                cambiar(when (estado) { EstadoGato.SENTARSE -> EstadoGato.SENTADO; EstadoGato.ECHARSE -> EstadoGato.DORMIR; EstadoGato.DESPERTAR -> EstadoGato.SENTADO; else -> EstadoGato.CAMINAR })
                // Junto al fuego se queda sentado un buen rato antes de echarse, y luego duerme más.
                if (calor && estado == EstadoGato.SENTADO) resto *= 3
                if (calor && estado == EstadoGato.DORMIR) { resto *= 2; calor = false }
            }
            EstadoGato.SENTADO -> {
                pon(AnimGato.SENTADO, (i / 4) % 2); i++; resto--
                if (resto <= 0) cambiar(if (calor) EstadoGato.ECHARSE else elegir(tSentado))
            }
            EstadoGato.ASEARSE -> {
                for (k in 0 until 6) repeat(2) { pon(AnimGato.ASEARSE, k) }
                for (k in 1 until 6) repeat(2) { pon(AnimGato.ASEARSE, k) }
                pon(AnimGato.SENTADO, 0)
                estado = EstadoGato.SENTADO
                cambiar(elegir(listOf(EstadoGato.ECHARSE to if (humor.cansado) 0.7 else 0.5, EstadoGato.LEVANTARSE to 0.5)))
            }
            EstadoGato.DORMIR -> { pon(AnimGato.DORMIR, (i / 5) % 3); i++; resto--; if (resto <= 0) cambiar(EstadoGato.DESPERTAR) }
            EstadoGato.ESPECIAL -> {
                val accion = especiales.getOrNull(especial)
                val n = accion?.cuadros ?: 0
                repeat(if (n == 4) 3 else 2) { for (k in 0 until n) repeat(3) { pon(AnimGato.ESPECIAL, k, esp = especial) } }
                val sentado = accion?.sentado ?: true
                repeat(3) { if (sentado) pon(AnimGato.SENTADO, 0) else pon(qui, 0) }
                estado = if (sentado) EstadoGato.SENTADO else EstadoGato.QUIETO
                cambiar(estado)
            }

            // ---- Las reacciones.
            EstadoGato.IR -> {
                val falta = destino - x
                if (abs(falta) <= vel * DT) { x = destino; llegar(); return }
                val d = if (falta > 0) 1 else -1
                if (d != dir) { repeat(3) { pon(AnimGato.GIRAR, 0) }; repeat(4) { pon(AnimGato.GIRAR, 1) }; dir = d; repeat(3) { pon(AnimGato.GIRAR, 0) } }
                x += dir * vel * DT
                pon(cam, i % 8); i++
            }
            EstadoGato.OLER -> {
                pon(AnimGato.OLER, (i / 3) % 4); i++; resto--
                if (resto <= 0) {
                    // A veces, además, le da con la mano.
                    if (!triste && rnd.nextDouble() < 0.45) for ((k, n) in listOf(3 to 4, 4 to 3, 5 to 4, 4 to 3, 5 to 3)) repeat(n) { pon(AnimGato.JUGAR, k) }
                    cambiar(EstadoGato.QUIETO)
                }
            }
            EstadoGato.ADORNO -> {
                // Lo mira y, cada tanto, le tira un manotazo.
                val golpe = i % 16 >= 12
                pon(AnimGato.ARRIBA, if (golpe) 2 else (i / 5) % 2); i++; resto--
                if (resto <= 0) { estado = EstadoGato.SENTADO; cambiar(EstadoGato.SENTADO) }
            }
            EstadoGato.SUBIR -> {
                val a = motivo
                if (a == null) { cambiar(EstadoGato.QUIETO); return }
                base = x
                brinco(x, a.x, 0f, a.y - sueloY, 1f, ESCALA_MIRADOR)
                cambiar(EstadoGato.VIGIA)
            }
            EstadoGato.VIGIA -> {
                pon(AnimGato.VIGIA, (i / 9) % 2); i++; resto--
                if (resto <= 0 || (mojado && !guarecido)) cambiar(EstadoGato.BAJAR)
            }
            EstadoGato.BAJAR -> {
                dir = if (base >= x) 1 else -1
                brinco(x, base, alza, 0f, escala, 1f)
                cambiar(if (puedeCaminar) EstadoGato.CAMINAR else EstadoGato.QUIETO)
            }
            EstadoGato.MIMO -> cambiar(EstadoGato.SENTADO)
            EstadoGato.ATENTO -> {
                pon(if (sentado()) AnimGato.MIRAR_SENTADO else AnimGato.MIRAR, (i / 6) % 2); i++; resto--
                if (resto <= 0) cambiar(if (sentado()) EstadoGato.SENTADO else EstadoGato.QUIETO)
            }
        }
    }

    companion object {
        /** Cada cuántos milisegundos da un cuadro: 12,5 por segundo, como el gato de pixel. */
        const val DT_MS = 80L
        private const val DT = DT_MS / 1000f

        /** Las medidas del gato de pixel están en dp de su banda; las de este, en unidades del lienzo: es algo más chico. */
        private const val ESCALA = 0.8f

        /** Lo que camina por segundo un gato de talla 1 con ánimo normal: a ocho cuadros por ciclo da justo una [Gatuno.ZANCADA]. */
        const val VELOCIDAD = 26f * ESCALA

        /** Subido al mirador queda un poco más lejos: se le pinta más chico. */
        const val ESCALA_MIRADOR = 0.82f

        private const val ANCHO_LIENZO = 380f

        private val SALTO_DX = floatArrayOf(0f, 3f, 4.5f, 4.5f, 2f, 0f)      // por cuadro, en cada fase del salto
        private val SALTO_DY = floatArrayOf(0f, -2f, -9f, -5f, 0f, 0f)
        private val JUEGO = listOf(Triple(0, 3, 0f), Triple(1, 3, 0f), Triple(0, 3, 0f), Triple(1, 3, 0f), Triple(2, 2, 1.5f), Triple(3, 4, 0.3f), Triple(4, 3, 0f), Triple(5, 4, 0f))
    }
}

/** La postura de un cuadro del gato: la de sus animaciones o la de una acción propia de su pelaje. */
fun poseDelPaso(p: PasoGato, pelaje: Pelaje): Pose =
    if (p.anim == AnimGato.ESPECIAL) pelaje.especiales.getOrNull(p.especial)?.pose?.invoke(p.i) ?: Gatuno.SENTADO else Gatuno.poseDe(p.anim, p.i, p.marcha)

/**
 * El gato sentado en su tramo de suelo: lo que se ve cuando no se anima. Se pone en medio, salvo que ahí estorbe: con
 * [anclas], busca el sitio más despejado, lejos del fuego y del objeto del día (para no taparlos ni quedar encima), y
 * lejos de lo que la escena diga que hay plantado en el suelo ([plantado], de `EscenaVida.estorbos`).
 */
fun gatoQuieto(suelo: Ancla, anclas: List<Ancla> = emptyList(), talla: Float = 1f, plantado: List<Float> = emptyList()): PasoGato {
    val medio = (suelo.x + suelo.hasta) / 2f
    val estorbos = anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.flatMap { listOf(it.x, it.hasta) } + plantado
    val margen = Gatuno.MEDIO_LARGO * talla
    if (estorbos.isEmpty() || suelo.hasta - suelo.x < 2 * margen + 8f) return PasoGato(AnimGato.SENTADO, 0, medio, 1)
    val despejado = 2.4f * margen
    var mejor = medio; var nota = -1f
    var x = suelo.x + margen
    while (x <= suelo.hasta - margen) {
        // Cuánto le queda libre a los lados (hasta lo que basta para no tapar nada); a igualdad, lo más cerca del medio.
        val n = minOf(estorbos.minOf { abs(it - x) }, despejado) - 0.02f * abs(x - medio)
        if (n > nota) { nota = n; mejor = x }
        x += 2f
    }
    return PasoGato(AnimGato.SENTADO, 0, mejor, if (estorbos.minByOrNull { abs(it - mejor) }!! >= mejor) 1 else -1)
}

/** Si llueve o nieva: el gato busca dónde guarecerse. */
fun mojaAlGato(clima: Clima): Boolean = when (clima) {
    Clima.LLUVIA -> true
    Clima.TORMENTA -> true
    Clima.NIEVE -> true
    Clima.DESPEJADO -> false
    Clima.NUBES -> false
    Clima.NIEBLA -> false
    Clima.VIENTO -> false
    Clima.POLVO -> false
    Clima.SIN_CLIMA -> false
}

/** Si el punto [p] del lienzo cae sobre el gato que está en [paso], pisando a la altura [sueloY] y de tamaño [talla]. Con algo de holgura: es chico para un dedo. */
fun tocaAlGato(p: Offset, paso: PasoGato, sueloY: Float, talla: Float): Boolean {
    val k = talla * paso.escala
    val pie = sueloY + paso.alza + paso.dy
    return abs(p.x - paso.x) <= Gatuno.MEDIO_LARGO * k * 1.15f + 6f && p.y <= pie + 8f && p.y >= pie - Gatuno.ALTO * k - 8f
}

/**
 * Lo que se ve al acariciarlo, en el instante [t] (de 0 a 1): un resplandor suave a su alrededor y un corazón que
 * sube desde su cabeza y se desvanece. [centro]: la mitad del gato; [radio]: lo que ocupa.
 */
fun DrawScope.mimoDelGato(t: Float, centro: Offset, radio: Float) {
    if (t < 0f || t > 1f) return
    val fuerza = (sin(PI.toFloat() * t)).coerceIn(0f, 1f)
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE3B0).copy(alpha = 0.42f * fuerza), Color(0xFFFFC9A0).copy(alpha = 0.16f * fuerza), Color.Transparent), centro, radio * 1.5f), radio * 1.5f, centro, blendMode = BlendMode.Screen)
    val c = Offset(centro.x + 2.5f * sin(7f * t), centro.y - radio * 0.9f - 20f * t)
    val r = 4.6f * (0.6f + 0.6f * minOf(1f, t * 4f))
    val alfa = (1f - ((t - 0.55f) / 0.45f).coerceIn(0f, 1f))
    fun corazon(r: Float) = Path().apply {
        moveTo(c.x, c.y + r)
        cubicTo(c.x - r * 1.9f, c.y - r * 0.2f, c.x - r * 0.9f, c.y - r * 1.5f, c.x, c.y - r * 0.5f)
        cubicTo(c.x + r * 0.9f, c.y - r * 1.5f, c.x + r * 1.9f, c.y - r * 0.2f, c.x, c.y + r)
        close()
    }
    drawPath(corazon(r * 1.22f), Color(0xFF7A2038).copy(alpha = 0.55f * alfa))
    drawPath(corazon(r), Color(0xFFEF6A82).copy(alpha = alfa))
    drawCircle(Color.White.copy(alpha = 0.6f * alfa), r * 0.2f, Offset(c.x - r * 0.42f, c.y - r * 0.42f))
}

/**
 * La capa del gato dentro del cuadro: va encima de la parte quieta y de la viva de la escena (la lluvia cae por
 * detrás de él). Camina por el tramo de suelo que le da la escena ([TipoAncla.SUELO]); si la escena no le da ninguno,
 * no hay gato. Solo se mueve con [animar]; si no, queda sentado. [fijo] dibuja un cuadro concreto (las capturas).
 *
 * Con [dedo], responde al dedo: un toque corto sobre él es una caricia (avisa por [alAcariciar], para la
 * vibración, y le sale un corazón); en otro sitio, mira o va hacia ahí. Los toques no los detecta esta capa: se los
 * pasa el cuadro ([VistaEscena]), que tiene el único detector. [mimoFijo] pinta el corazón en un instante dado (las
 * capturas). Las caricias no se guardan en ningún sitio.
 *
 * Con [pausa] (el cuadro no se ve) el gato se queda en el cuadro en que iba y sigue desde ahí al volver.
 */
@Composable
internal fun CapaGato(
    escena: EscenaVida, momento: Momento, humor: Gato.Humor, pelaje: Pelaje, animar: Boolean, fijo: PasoGato?,
    modifier: Modifier = Modifier, alFallar: (Throwable) -> Unit = {}, dedo: DedoDelCuadro? = null, alAcariciar: () -> Unit = {}, mimoFijo: Float = -1f,
    pausa: Boolean = false,
) {
    val anclas = remember(escena, momento.sello) { escena.anclas(momento) }
    val suelo = anclas.firstOrNull { it.tipo == TipoAncla.SUELO } ?: return
    val talla = escena.tallaDelGato
    val semilla = remember { System.nanoTime() }
    val sim = remember(escena, humor, pelaje, suelo.x, suelo.hasta) { SimulacionGato(humor, semilla, suelo.x, suelo.hasta, pelaje.especiales, talla) }
    remember(sim, momento.sello) { sim.entorno(anclas, suelo.y, mojaAlGato(momento.ficha.clima)); 0 }
    var paso by remember(sim) { mutableStateOf<PasoGato?>(null) }
    val seMueve = animar && fijo == null
    val quieto = remember(escena, momento.sello, talla) { gatoQuieto(suelo, anclas, talla, escena.estorbos(momento)) }
    LaunchedEffect(sim, seMueve, pausa) {
        if (!seMueve) { paso = null; return@LaunchedEffect }
        if (pausa) return@LaunchedEffect
        while (true) {
            withFrameMillis { }          // se detiene mientras la app no está en pantalla
            paso = sim.siguiente()
            delay(SimulacionGato.DT_MS)
        }
    }
    // La caricia: un corazón que dura algo más de un segundo. Sin animaciones, sale quieto y se va.
    var caricias by remember { mutableIntStateOf(0) }
    var mimo by remember { mutableStateOf(-1f) }
    LaunchedEffect(caricias) {
        if (caricias == 0) return@LaunchedEffect
        if (!animar) { mimo = 0.3f; delay(1100); mimo = -1f; return@LaunchedEffect }
        val t0 = withFrameMillis { it }
        while (true) { val t = (withFrameMillis { it } - t0) / 1500f; if (t >= 1f) break; mimo = t }
        mimo = -1f
    }
    if (dedo != null) {
        val mimar = { sim.acariciar(); caricias++; alAcariciar() }
        SideEffect {
            dedo.alTocar = { p -> if (tocaAlGato(p, fijo ?: paso ?: quieto, suelo.y, talla)) mimar() else sim.tocar(p.x) }
            dedo.alMimar = mimar
        }
        DisposableEffect(dedo) { onDispose { dedo.alTocar = null; dedo.alMimar = null } }
    }
    // En su propia capa: al moverse el gato no hay que volver a trazar la parte viva de la escena, ni al revés.
    Spacer(modifier.graphicsLayer().drawBehind {
        val p = fijo ?: paso ?: quieto
        aSalvo(alFallar) {
            lienzo {
                val k = talla * p.escala
                val pie = Offset(p.x, suelo.y + p.alza)
                gato(poseDelPaso(p, pelaje), pelaje, escena.luzDelGato(momento, pie), pie, p.dir, k, p.dy, pelaje.especiales.getOrNull(p.especial), p.i)
                val t = if (mimoFijo >= 0f) mimoFijo else mimo
                if (t >= 0f) mimoDelGato(t, Offset(p.x, pie.y + p.dy - Gatuno.ALTO * k * 0.5f), Gatuno.ALTO * k * 0.6f)
            }
        }
    })
}
