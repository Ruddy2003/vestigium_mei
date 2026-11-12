package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  El esqueleto del gato del cuadro (plan de historia y escenas, sección 3.4; tanda P5a, 12 nov 2026)
// =====================================================================
//
// El gato del cuadro no es una tira de imágenes, como el de pixel: es un esqueleto que se dibuja con código. Una
// postura ([Pose]) dice dónde están la cadera, el hombro, la cabeza, los cuatro pies y la cola; el dibujo
// (`GatoDibujo.kt`) le pone el cuerpo encima. Las 13 animaciones del gato de pixel están aquí escritas una sola vez,
// como posturas por cuadro ([poseDe]): sirven para todos los temas, que solo cambian el pelaje, el accesorio y sus dos
// acciones propias.
//
// Las medidas van en las unidades del lienzo de la escena (380 × 285) para un gato de talla 1: de pie mide unas 33 de
// largo sin la cola y 38 de alto, sentado, hasta la punta de las orejas. El gato mira hacia la derecha (+x) y pisa en y = 0;
// hacia arriba, y es negativa. Quien dibuja lo voltea si mira a la izquierda.

/** Las animaciones comunes: las mismas 13 del gato de pixel, con los mismos cuadros cada una. */
enum class AnimGato(val cuadros: Int) {
    CAMINAR(8), CAMINAR_TRISTE(8), QUIETO(4), QUIETO_TRISTE(4), GIRAR(2), SALTAR(6), JUGAR(6), ENOJADO(3),
    SENTARSE(4), SENTADO(2), ECHARSE(3), ASEARSE(6), DORMIR(3),
    /** Una de las dos acciones propias del tema: sus posturas las da el pelaje. */
    ESPECIAL(0),
}

/**
 * Una postura del gato.
 *
 * - [cadera] y [hombro]: los dos extremos del lomo. [lomo]: cuánto se arquea hacia arriba (negativo: se hunde).
 * - [cabeza]: su centro. [morro]: cuánto baja el hocico, en radianes (negativo: lo levanta).
 * - [manoC], [manoL], [pieC] y [pieL]: dónde pisan las patas de delante y las de atrás; la C es la del lado que se
 *   ve, la L la del otro lado. Las rodillas y los codos salen solos.
 * - [cola0], [cola1] y [cola2]: hacia dónde apunta cada tercio de la cola, en radianes: 0 es hacia atrás, π/2 hacia
 *   arriba y π hacia delante. [colaDelante]: la cola pasa por delante del cuerpo (cuando duerme enroscado).
 * - [orejas]: de −1 (gachas, hacia atrás) a 1 (tiesas). [ojos]: de 0 (cerrados) a 1. [boca]: de 0 a 1 (bufa o bosteza).
 * - [erizo]: de 0 a 1, el pelo de punta. [lengua]: se está lamiendo la mano.
 * - [giro]: hacia dónde está vuelto el cuerpo: 0 de perfil, 1 de frente (mirando a quien lo ve) y −1 de espaldas;
 *   0.5 y −0.5 son de tres cuartos. No estrecha el dibujo: dice desde dónde se le ve, y con eso se pintan las cuatro patas, el pecho y
 *   la cola en su sitio. Las posturas de giro traen ya la cadera, el hombro y los pies donde se ven desde ahí.
 * - [cara]: lo mismo para la cabeza sola, que puede volverse más que el cuerpo. Con [SIGUE] va como el cuerpo.
 */
data class Pose(
    val cadera: Offset = Offset(-7.2f, -14.4f), val hombro: Offset = Offset(6.6f, -15f), val lomo: Float = 0f,
    val cabeza: Offset = Offset(13f, -22.8f), val morro: Float = 0f,
    val manoC: Offset = Offset(7.6f, 0f), val manoL: Offset = Offset(9.3f, 0f),
    val pieC: Offset = Offset(-9.3f, 0f), val pieL: Offset = Offset(-7.4f, 0f),
    val cola0: Float = 0.45f, val cola1: Float = 0.1f, val cola2: Float = 0.75f, val colaDelante: Boolean = false,
    val orejas: Float = 1f, val ojos: Float = 1f, val boca: Float = 0f, val erizo: Float = 0f,
    val giro: Float = 0f, val cara: Float = SIGUE, val lengua: Boolean = false,
) {
    /** Hacia dónde está vuelta la cabeza: 0 de perfil, 1 de frente, −1 de espaldas. */
    val caraVista: Float get() = if (cara < -1.5f) giro else cara
}

/** El valor de `Pose.cara` que deja la cabeza vuelta como el cuerpo. */
const val SIGUE = -2f

private fun mz(a: Float, b: Float, t: Float) = a + (b - a) * t
private fun mz(a: Offset, b: Offset, t: Float) = Offset(mz(a.x, b.x, t), mz(a.y, b.y, t))

/** La postura que queda a [t] (de 0 a 1) del camino entre [a] y [b]. Lo que no se puede mezclar se toma de la más cercana. */
fun mezcla(a: Pose, b: Pose, t: Float): Pose {
    val c = if (t < 0.5f) a else b
    return Pose(
        mz(a.cadera, b.cadera, t), mz(a.hombro, b.hombro, t), mz(a.lomo, b.lomo, t), mz(a.cabeza, b.cabeza, t), mz(a.morro, b.morro, t),
        mz(a.manoC, b.manoC, t), mz(a.manoL, b.manoL, t), mz(a.pieC, b.pieC, t), mz(a.pieL, b.pieL, t),
        mz(a.cola0, b.cola0, t), mz(a.cola1, b.cola1, t), mz(a.cola2, b.cola2, t), c.colaDelante,
        mz(a.orejas, b.orejas, t), mz(a.ojos, b.ojos, t), mz(a.boca, b.boca, t), mz(a.erizo, b.erizo, t), mz(a.giro, b.giro, t), mz(a.caraVista, b.caraVista, t), c.lengua,
    )
}

/** Las medidas del gato de talla 1 y las posturas de base. */
object Gatuno {
    const val R_GRUPA = 4.7f
    const val R_PECHO = 4.5f
    const val R_CABEZA = 6.4f
    const val BRAZO = 5.2f
    const val ANTEBRAZO = 10f
    const val MUSLO = 6.4f
    const val PIERNA = 9.4f
    const val COLA = 23f

    /** Cuánto avanza en un ciclo de ocho pasos a la marcha normal: lo que recorre la simulación en ese tiempo. */
    const val ZANCADA = 13.3f

    /** Lo que ocupa a lo ancho, desde su centro, y a lo alto: para saber si cabe y para tocarlo. */
    const val MEDIO_LARGO = 19f
    const val ALTO = 38f

    val DE_PIE = Pose()

    /** Sentado, erguido, con las manos juntas y la cola enroscada hacia arriba a su lado. */
    val SENTADO = Pose(
        cadera = Offset(-4.6f, -5f), hombro = Offset(2.6f, -16.4f), cabeza = Offset(4.6f, -26f),
        manoC = Offset(5f, 0f), manoL = Offset(6.5f, 0f), pieC = Offset(0.8f, 0f), pieL = Offset(2.2f, 0f),
        cola0 = 0f, cola1 = 0.75f, cola2 = 2.3f,
    )

    /** Enroscado, con la cola por delante y la cabeza sobre las manos. */
    val ECHADO = Pose(
        cadera = Offset(-7f, -4.9f), hombro = Offset(5f, -4.8f), lomo = 1.4f, cabeza = Offset(11.2f, -5.6f), morro = 0.3f,
        manoC = Offset(11f, 0f), manoL = Offset(11.2f, -0.6f), pieC = Offset(-3.2f, 0f), pieL = Offset(-3.4f, -0.6f),
        cola0 = -1.3f, cola1 = 3.05f, cola2 = 3.2f, colaDelante = true, orejas = 0.5f, ojos = 0f,
    )

    private val AGAZAPADO = Pose(
        cadera = Offset(-7.2f, -13.2f), hombro = Offset(6.4f, -7.4f), cabeza = Offset(13f, -10.6f), morro = 0.1f,
        manoC = Offset(10.6f, 0f), manoL = Offset(12.2f, 0f), pieC = Offset(-9f, 0f), pieL = Offset(-7.4f, 0f),
        cola0 = 1.1f, cola1 = 1.5f, cola2 = 1.2f,
    )

    private val ERIZADO = Pose(
        cadera = Offset(-5.8f, -15.4f), hombro = Offset(5.4f, -15.2f), lomo = 4.4f, cabeza = Offset(11.4f, -17.2f), morro = 0.25f,
        manoC = Offset(5.8f, 0f), manoL = Offset(7.4f, 0f), pieC = Offset(-6.6f, 0f), pieL = Offset(-5f, 0f),
        cola0 = 1.45f, cola1 = 1.6f, cola2 = 1.9f, orejas = -1f, boca = 1f, erizo = 1f,
    )

    private fun fr(x: Float) = x - kotlin.math.floor(x)

    /** Dónde va un pie en la fase [p] del paso (de 0 a 1), alrededor de [neutro]: apoya y empuja, o va por el aire. */
    private fun pisada(neutro: Offset, p: Float, zancada: Float, alza: Float): Offset {
        val medio = zancada * 0.3f                                     // apoya el 60 % del ciclo: retrocede 0.6 zancadas
        return if (p < 0.6f) Offset(neutro.x + medio - 2f * medio * (p / 0.6f), 0f)
        else { val u = (p - 0.6f) / 0.4f; Offset(neutro.x - medio + 2f * medio * u, -alza * sin(PI.toFloat() * u)) }
    }

    /** Un paso del caminar: las patas van una tras otra (atrás, delante del mismo lado, atrás, delante), sin patinar. */
    private fun caminar(k: Int, marcha: Float, triste: Boolean): Pose {
        val f = k / 8f
        val z = ZANCADA * marcha
        val vaiven = sin(4f * PI.toFloat() * f)
        val b = DE_PIE
        val p = b.copy(
            cadera = b.cadera + Offset(0f, 0.35f * vaiven), hombro = b.hombro + Offset(0f, -0.3f * vaiven),
            cabeza = b.cabeza + Offset(0.2f * vaiven, 0.3f * vaiven),
            pieC = pisada(b.pieC, fr(f), z, 2.2f), manoC = pisada(b.manoC, fr(f + 0.75f), z, 2f),
            pieL = pisada(b.pieL, fr(f + 0.5f), z, 2.2f), manoL = pisada(b.manoL, fr(f + 0.25f), z, 2f),
            cola0 = 0.9f + 0.08f * vaiven, cola1 = 1.3f, cola2 = 1.75f + 0.15f * sin(2f * PI.toFloat() * f),
        )
        return if (!triste) p else p.copy(
            cabeza = Offset(13.8f, -16f + 0.3f * vaiven), morro = 0.35f, orejas = 0.1f, ojos = 0.6f,
            cola0 = -0.55f, cola1 = -0.25f, cola2 = 0.1f + 0.06f * vaiven,
        )
    }

    /**
     * La postura del cuadro [k] de la animación [anim]. [marcha]: la zancada del caminar, de 0.4 a 1.2 veces la normal
     * (triste o cansado camina más corto), para que los pies no patinen.
     */
    fun poseDe(anim: AnimGato, k: Int, marcha: Float = 1f): Pose = when (anim) {
        AnimGato.CAMINAR -> caminar(Math.floorMod(k, 8), marcha, false)
        AnimGato.CAMINAR_TRISTE -> caminar(Math.floorMod(k, 8), marcha, true)
        // Respira, y mueve la punta de la cola.
        AnimGato.QUIETO -> { val r = sin(PI.toFloat() * k / 2f); DE_PIE.copy(hombro = DE_PIE.hombro + Offset(0f, -0.25f * r), cabeza = DE_PIE.cabeza + Offset(0f, -0.3f * r), cola2 = 0.75f + 0.25f * r, ojos = if (k == 3) 0.15f else 1f) }
        AnimGato.QUIETO_TRISTE -> { val r = sin(PI.toFloat() * k / 2f); DE_PIE.copy(cabeza = Offset(13.6f, -16.4f - 0.2f * r), morro = 0.35f, orejas = 0.05f, ojos = 0.55f, cola0 = -0.6f, cola1 = -0.3f, cola2 = 0.05f + 0.05f * r) }
        // Da media vuelta dando la espalda: primero de tres cuartos, con la cabeza ya vuelta; luego de espaldas, con la cola en alto.
        AnimGato.GIRAR -> if (Math.floorMod(k, 2) == 0) Pose(
            cadera = Offset(-4.6f, -14.3f), hombro = Offset(4.8f, -15.2f), cabeza = Offset(8.4f, -23.2f), giro = -0.5f, cara = -0.68f,
            manoC = Offset(6.9f, -0.7f), manoL = Offset(3.5f, -1.2f), pieC = Offset(-4.2f, 0f), pieL = Offset(-7.8f, -0.5f),
            cola0 = 0.95f, cola1 = 1.3f, cola2 = 1.7f,
        ) else Pose(
            cadera = Offset(0f, -14f), hombro = Offset(0.3f, -15.6f), cabeza = Offset(0.4f, -23.4f), giro = -1f,
            manoC = Offset(2.4f, -1.2f), manoL = Offset(-2.2f, -1.2f), pieC = Offset(3f, 0f), pieL = Offset(-3f, 0f),
            cola0 = 1.05f, cola1 = 1.45f, cola2 = 2.6f,
        )
        AnimGato.SALTAR -> when (Math.floorMod(k, 6)) {
            0 -> AGAZAPADO.copy(cadera = Offset(-6.8f, -9f), cola0 = 0.4f, cola1 = 0.3f, cola2 = 0.2f)
            1 -> DE_PIE.copy(cadera = Offset(-7f, -13f), hombro = Offset(7.6f, -18.4f), cabeza = Offset(14.6f, -25f), manoC = Offset(12.4f, -8f), manoL = Offset(13.8f, -9f), pieC = Offset(-11.8f, 0f), pieL = Offset(-10f, 0f), cola0 = 0.1f, cola1 = 0f, cola2 = -0.1f)
            2 -> DE_PIE.copy(cadera = Offset(-8.2f, -14.6f), hombro = Offset(7.6f, -16.2f), cabeza = Offset(15f, -21.4f), manoC = Offset(18f, -10f), manoL = Offset(19f, -11f), pieC = Offset(-18.5f, -9f), pieL = Offset(-17.4f, -10f), cola0 = 0.15f, cola1 = 0.1f, cola2 = 0.3f, orejas = 0.4f)
            3 -> DE_PIE.copy(cadera = Offset(-7.4f, -16.6f), hombro = Offset(7f, -13.4f), cabeza = Offset(13.8f, -17.6f), morro = 0.2f, manoC = Offset(13.6f, -2.5f), manoL = Offset(15f, -3.5f), pieC = Offset(-14.6f, -8f), pieL = Offset(-13f, -9f), cola0 = 0.8f, cola1 = 0.9f, cola2 = 1f)
            4 -> AGAZAPADO.copy(cadera = Offset(-7.2f, -14.4f), hombro = Offset(6.4f, -9f), cabeza = Offset(13f, -12.6f), manoC = Offset(9.8f, 0f), manoL = Offset(11.4f, 0f), pieC = Offset(-8.2f, 0f), pieL = Offset(-6.6f, 0f))
            else -> mezcla(AGAZAPADO, DE_PIE, 0.7f)
        }
        AnimGato.JUGAR -> when (Math.floorMod(k, 6)) {
            0 -> AGAZAPADO.copy(cola2 = 0.9f)
            1 -> AGAZAPADO.copy(cadera = Offset(-6.5f, -13.6f), cola0 = 1.3f, cola2 = 1.7f)                   // menea la grupa
            2 -> DE_PIE.copy(cadera = Offset(-8f, -13.4f), hombro = Offset(7.6f, -15f), cabeza = Offset(14.8f, -19f), morro = 0.2f, manoC = Offset(17f, -6.5f), manoL = Offset(18.2f, -7.5f), pieC = Offset(-14.4f, -3f), pieL = Offset(-13f, -4f), cola0 = 0.5f, cola1 = 0.7f, cola2 = 1f)
            3 -> AGAZAPADO.copy(cabeza = Offset(13.2f, -9.4f), morro = 0.35f, manoC = Offset(14f, 0f), manoL = Offset(15.2f, 0f))                    // lo tiene bajo las manos
            4 -> AGAZAPADO.copy(cadera = Offset(-7.2f, -10.4f), hombro = Offset(6f, -11.6f), cabeza = Offset(12.2f, -16f), manoC = Offset(15f, -11f), cola0 = 0.9f, cola1 = 1.6f, cola2 = 2.2f)   // un manotazo
            else -> AGAZAPADO.copy(cadera = Offset(-7.2f, -10.4f), hombro = Offset(6f, -11.6f), cabeza = Offset(12.4f, -15.2f), morro = 0.2f, manoC = Offset(13.6f, -2.5f), cola0 = 0.7f, cola1 = 1.2f, cola2 = 1.5f)
        }
        // Lomo arqueado, pelo de punta, y un temblor.
        AnimGato.ENOJADO -> { val t = Math.floorMod(k, 3) - 1; ERIZADO.copy(cabeza = ERIZADO.cabeza + Offset(0.5f * t, 0f), lomo = 4.4f + 0.5f * t, cola2 = 1.9f + 0.2f * t, boca = if (t == 0) 1f else 0.6f) }
        AnimGato.SENTARSE -> mezcla(DE_PIE, SENTADO, (Math.floorMod(k, 4) + 1) / 4f)
        // Sentado: mueve la punta de la cola y parpadea.
        AnimGato.SENTADO -> if (Math.floorMod(k, 2) == 0) SENTADO else SENTADO.copy(cola2 = 2.75f, cabeza = SENTADO.cabeza + Offset(0f, -0.2f))
        AnimGato.ECHARSE -> { val t = (Math.floorMod(k, 3) + 1) / 3f; mezcla(SENTADO, ECHADO, t).copy(ojos = if (t < 1f) 0.6f else 0f) }
        // Se lame una mano y se la pasa por la cara.
        AnimGato.ASEARSE -> {
            val q = Math.floorMod(k, 6)
            val sube = listOf(0.3f, 0.8f, 1f, 0.75f, 1f, 0.5f)[q]
            SENTADO.copy(
                cabeza = SENTADO.cabeza + Offset(0.2f, 1.6f * sube), morro = 0.55f * sube, ojos = if (q >= 2) 0.12f else 0.7f, orejas = 0.7f,
                manoC = Offset(mz(5f, 9.4f, sube), mz(0f, -20.4f - (if (q == 3) 2.2f else 0f), sube)), lengua = q == 2 || q == 4,
            )
        }
        // Dormido: respira despacio.
        AnimGato.DORMIR -> { val r = listOf(0f, 0.5f, 1f)[Math.floorMod(k, 3)]; ECHADO.copy(lomo = 1.4f + 0.7f * r, cadera = ECHADO.cadera + Offset(0f, -0.15f * r)) }
        AnimGato.ESPECIAL -> SENTADO
    }

    /**
     * Dónde queda el codo o la rodilla de una pata que nace en [raiz] y pisa en [pie], con huesos de largo [a] y [b].
     * [haciaDelante]: la rodilla de atrás dobla hacia delante; el codo, hacia atrás. Si el pie queda fuera de alcance,
     * la pata se estira hacia él todo lo que da. [doblez]: cuánto del doblez se ve; de frente, casi nada (dobla hacia el fondo).
     */
    fun articulacion(raiz: Offset, pie: Offset, a: Float, b: Float, haciaDelante: Boolean, doblez: Float = 1f): Offset {
        val v = pie - raiz
        val d = sqrt(v.x * v.x + v.y * v.y).coerceIn(kotlin.math.abs(a - b) + 0.05f, a + b - 0.05f)
        val u = if (v.x == 0f && v.y == 0f) Offset(0f, 1f) else v / sqrt(v.x * v.x + v.y * v.y)
        val largo = (d * d + a * a - b * b) / (2f * d)
        val alto = sqrt((a * a - largo * largo).coerceAtLeast(0f))
        val n = if (haciaDelante) Offset(u.y, -u.x) else Offset(-u.y, u.x)
        return raiz + u * largo + n * (alto * doblez)
    }

    /**
     * El punto [t] (de 0 a 1) de la cola de [p], que nace en [base] y mide [largo]. [perfil]: cuánto de lo que la cola
     * va hacia atrás se ve desde donde se mira (1 de perfil; de frente, casi nada: se le ve subir).
     */
    fun cola(p: Pose, base: Offset, largo: Float, t: Float, perfil: Float = 1f): Offset {
        fun dir(a: Float) = Offset(-cos(a) * perfil, -sin(a))
        val tercio = largo / 3f
        val p1 = base + dir(p.cola0) * tercio; val p2 = p1 + dir(p.cola1) * tercio; val p3 = p2 + dir(p.cola2) * tercio
        val q = 1f - t
        return base * (q * q * q) + p1 * (3f * q * q * t) + p2 * (3f * q * t * t) + p3 * (t * t * t)
    }
}
