package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import io.github.ruddy2003.vestigium.ui.Gato
import kotlinx.coroutines.delay

// =====================================================================
//  El gato del cuadro: cómo se porta (plan de historia y escenas, sección 3.4; tanda P5a, 12 nov 2026)
// =====================================================================
//
// El gato vive dentro del cuadro de Hoy: camina por el suelo del primer plano de la escena de su tema. Está dibujado
// con código (`GatoEsqueleto.kt` y `GatoDibujo.kt`) y recibe la luz de la escena.
//
// Se porta igual que el gato de pixel (`ui/Gato.kt`): la misma máquina de estados, con los mismos tiempos y las mismas
// probabilidades, y el mismo ánimo (`Gato.humor`, que sale de cómo dormiste y de cómo calificaste el día). Lo único
// que cambia es dónde camina (el tramo de suelo que le da la escena, en vez de una banda) y la escala de sus pasos.
//
// Nada de esto se guarda. Con las animaciones apagadas, con ahorro de energía o en las capturas, se queda sentado.

/**
 * Un cuadro del gato: qué animación y cuál de sus cuadros, dónde pisa ([x], en las unidades del lienzo) y hacia dónde
 * mira ([dir]: 1 a la derecha, −1 a la izquierda). [dy]: cuánto está en el aire. [marcha]: lo larga que es su
 * zancada (1 es la normal). [especial]: cuál de las acciones propias del tema, si [anim] es [AnimGato.ESPECIAL].
 */
class PasoGato(val anim: AnimGato, val i: Int, val x: Float, val dir: Int, val dy: Float = 0f, val marcha: Float = 1f, val especial: Int = -1)

/** Lo que está haciendo el gato. Son los estados del gato de pixel. */
enum class EstadoGato { CAMINAR, QUIETO, SALTAR, JUGAR, ERIZARSE, SENTARSE, LEVANTARSE, ECHARSE, DESPERTAR, SENTADO, ASEARSE, DORMIR, ESPECIAL }

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
 * - Con la misma [semilla] hace siempre lo mismo.
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

    /** Lo que hace ahora mismo. */
    var estado: EstadoGato = if (humor.noche) EstadoGato.DORMIR else if (puedeCaminar) EstadoGato.CAMINAR else EstadoGato.QUIETO
        private set
    private var resto = durar(estado)

    private val tCaminar: List<Pair<EstadoGato, Double>>
    private val tQuieto: List<Pair<EstadoGato, Double>>
    private val tSentado: List<Pair<EstadoGato, Double>>

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

    private fun durar(e: EstadoGato): Int {
        fun u(a: Double, b: Double) = a + rnd.nextDouble() * (b - a)
        val seg = when (e) {
            EstadoGato.CAMINAR -> u(1.6, 3.2)
            EstadoGato.QUIETO -> u(1.0, 2.0)
            EstadoGato.SENTADO -> u(1.0, 2.0) * (if (triste) 1.6 else 1.0)
            EstadoGato.DORMIR -> u(3.0, 4.5) * (if (humor.cansado) 1.5 else 1.0) * (if (humor.noche) 3.0 else 1.0)
            EstadoGato.ERIZARSE -> u(1.5, 2.4)
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

    private fun pon(anim: AnimGato, k: Int, dy: Float = 0f, esp: Int = -1) = cola.addLast(PasoGato(anim, k, x, dir, dy, marcha, esp))
    private fun cabe(xx: Float, d: Int) = if (d > 0) xx + margen <= hasta else xx - margen >= desde

    /** El siguiente cuadro. */
    fun siguiente(): PasoGato {
        while (cola.isEmpty()) avanzar()
        return cola.removeFirst()
    }

    private fun avanzar() {
        val k0 = ESCALA * talla
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
                if (resto <= 0) cambiar(elegir(tCaminar))
            }
            EstadoGato.QUIETO -> { pon(qui, (i / 3) % 4); i++; resto--; if (resto <= 0) cambiar(elegir(tQuieto)) }
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
            }
            EstadoGato.SENTADO -> { pon(AnimGato.SENTADO, (i / 4) % 2); i++; resto--; if (resto <= 0) cambiar(elegir(tSentado)) }
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

        private val SALTO_DX = floatArrayOf(0f, 3f, 4.5f, 4.5f, 2f, 0f)      // por cuadro, en cada fase del salto
        private val SALTO_DY = floatArrayOf(0f, -2f, -9f, -5f, 0f, 0f)
        private val JUEGO = listOf(Triple(0, 3, 0f), Triple(1, 3, 0f), Triple(0, 3, 0f), Triple(1, 3, 0f), Triple(2, 2, 1.5f), Triple(3, 4, 0.3f), Triple(4, 3, 0f), Triple(5, 4, 0f))
    }
}

/** La postura de un cuadro del gato: la de sus 13 animaciones o la de una acción propia de su pelaje. */
fun poseDelPaso(p: PasoGato, pelaje: Pelaje): Pose =
    if (p.anim == AnimGato.ESPECIAL) pelaje.especiales.getOrNull(p.especial)?.pose?.invoke(p.i) ?: Gatuno.SENTADO else Gatuno.poseDe(p.anim, p.i, p.marcha)

/** El gato sentado en medio de su tramo de suelo: lo que se ve cuando no se anima. */
fun gatoQuieto(suelo: Ancla): PasoGato = PasoGato(AnimGato.SENTADO, 0, (suelo.x + suelo.hasta) / 2f, 1)

/**
 * La capa del gato dentro del cuadro: va encima de la parte quieta y de la viva de la escena (la lluvia cae por
 * detrás de él). Camina por el tramo de suelo que le da la escena ([TipoAncla.SUELO]); si la escena no le da ninguno,
 * no hay gato. Solo se mueve con [animar]; si no, queda sentado. [fijo] dibuja un cuadro concreto (las capturas).
 */
@Composable
internal fun CapaGato(
    escena: EscenaVida, momento: Momento, humor: Gato.Humor, pelaje: Pelaje, animar: Boolean, fijo: PasoGato?,
    modifier: Modifier = Modifier, alFallar: (Throwable) -> Unit = {},
) {
    val suelo = remember(escena, momento.sello) { escena.anclas(momento).firstOrNull { it.tipo == TipoAncla.SUELO } } ?: return
    val talla = escena.tallaDelGato
    val semilla = remember { System.nanoTime() }
    val sim = remember(escena, humor, pelaje, suelo.x, suelo.hasta) { SimulacionGato(humor, semilla, suelo.x, suelo.hasta, pelaje.especiales, talla) }
    var paso by remember { mutableStateOf<PasoGato?>(null) }
    val seMueve = animar && fijo == null
    LaunchedEffect(sim, seMueve) {
        paso = null
        if (!seMueve) return@LaunchedEffect
        while (true) {
            withFrameMillis { }          // se detiene mientras la app no está en pantalla
            paso = sim.siguiente()
            delay(SimulacionGato.DT_MS)
        }
    }
    // En su propia capa: al moverse el gato no hay que volver a trazar la parte viva de la escena, ni al revés.
    Spacer(modifier.graphicsLayer().drawBehind {
        val p = fijo ?: paso ?: gatoQuieto(suelo)
        aSalvo(alFallar) {
            lienzo {
                val pie = Offset(p.x, suelo.y)
                gato(poseDelPaso(p, pelaje), pelaje, escena.luzDelGato(momento, pie), pie, p.dir, talla, p.dy, pelaje.especiales.getOrNull(p.especial), p.i)
            }
        }
    })
}
