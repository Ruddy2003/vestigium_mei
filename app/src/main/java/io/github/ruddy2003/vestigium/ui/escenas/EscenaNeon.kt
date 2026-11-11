package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.anuncio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cable
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lluvia
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pasarela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoV
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoMojado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.torre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tuberia
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tuboNeon
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

/**
 * Ciudad neón: el callejón del nivel −3 de la megaciudad, donde Amaru tiene su puesto de fideos, «El Tazón», y por
 * donde pasan las rutas de Kei, repartidora en bici (2077 a 2081). Todo es ficción. Los dos comparten el cuadro: un
 * día es de él y otro de ella, y el callejón es el mismo.
 *
 * - **A la izquierda:** el edificio del puesto, de frente. Arriba, el letrero del tazón magenta; debajo, el toldo, la
 *   barra con sus seis bancos rojos y, dentro, la olla grande.
 * - **En medio:** el callejón, que se aleja hasta su boca. Por ella se ve la avenida y, encima, los niveles de la
 *   ciudad: torres, pasarelas, anuncios de luz y, arriba del todo, una rendija por donde se sabe qué hora es fuera.
 * - **A la derecha:** la farmacia de Celso, con su cruz de guardia, y su azotea. De día despacha con la puerta
 *   abierta; de ocho de la tarde a ocho de la mañana, por la ventanilla de la reja.
 * - **Delante:** el poste con su farol, la bombona, las cajas, el cajón con el objeto del día, la toma de agua, el
 *   carril de las bicis pintado en el suelo y, los días de Kei, su bici azul. De noche, las bolsas de la basura.
 * - **Arriba:** los cables, que cruzan de lado a lado, y el alero de chapa de este lado del callejón.
 *
 * Cómo está pintado (`docs/diseno/ESCENAS.md`, sección 5: es la primera escena donde siempre es de noche) **[Diseño
 * propio]**: no hay sol, así que la luz con dirección la dan los focos del callejón ([Alumbrado]). El letrero tiñe de
 * magenta lo que tiene cerca, la cruz de la farmacia de verde agua y el farol de ámbar; cada cosa tiene un filo de
 * cada luz. A cada cosa le tumba la sombra el foco que más la alumbra (el puesto, el farol o la cruz), lejos de él: por
 * eso las sombras cambian cuando el puesto abre o cierra. El suelo mojado devuelve cada luz. La paleta es esa: tres
 * luces sobre azul violeta.
 *
 * **[Aproximación]**: abajo siempre es de noche, pero por la rendija de arriba se ve el día de fuera: rosa al amanecer
 * y gris claro a mediodía, cuando baja un rayo de claridad. Con eso, con el puesto (abre de seis de la tarde a cuatro
 * de la mañana) y con la farmacia (abre de día), el cuadro de las 7:00, el de las 13:00 y el de las 21:00 son
 * distintos: a las 7:00 duermen los dos; a las 13:00 manda la farmacia; a las 21:00, el letrero.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de sus calendarios en `AnioPiloto.kt`):
 * - **El puesto** ([puesto]): abierto de 18:00 a 4:00, con la olla echando vapor; de sobremesa hasta las 5:00;
 *   recogido, con la cortina bajada y los bancos apilados, hasta las 10:00; cerrado mientras él duerme; y a las
 *   17:00, a medio abrir, con la compra. Con toque de queda cierra a las 21:00. Desde que Sami la pinta (15 de junio
 *   de la temporada 4), la cortina lleva su mural.
 * - **El letrero** ([letrero], arco `letrero`): encendido mientras el puesto está abierto; roto, del 12 al 28 de
 *   noviembre de la temporada 2; remendado, con un tubo de otro color, hasta que Sami lo deja como nuevo.
 * - **El apagón** (clave `apagon`): ni letreros ni anuncios; el puesto cocina con gas y velas.
 * - **La bici de Kei** ([bici], estado `sin_bici` y arco `bici`): solo los días de ella. A sus horas de trabajo espera
 *   junto a la toma de agua, con el faro encendido, entre un pedido y otro; a la hora de cenar está enchufada al cable
 *   que le tiende el cocinero; **mientras está en el taller, no está**; después se le ve la soldadura del cuadro,
 *   hasta que la vuelve a pintar de azul.
 * - **El agua** (arco `agua`): el suelo se anega, y con la marea hay que pasar por tablones.
 * - **Las pintadas** (arco `pintas`) y **las máquinas** (arco `obra`, clave `maquinas`) en la boca del callejón; el
 *   día de la cadena humana (clave `cadena`), la cuerda de trapos anudados delante de ellas.
 * - **Las luces de fin de año** (arco `luces`), **los faroles** de papel (clave `faroles`), **las velas** y los
 *   retratos de luz de la Noche de los Ausentes (clave `velas`), los banderines de las fiestas, las pancartas de la
 *   huelga (clave `huelga`), la cinta y la luz roja del toque de queda (clave `toque`) y las cajas de verdura de la
 *   cosecha de las torres (clave `cosecha`).
 * - **El objeto del día** de cada uno, sobre el cajón de delante.
 */
internal object EscenaNeon : EscenaVida {
    override val vidas = listOf("repartidora", "fideos")

    // ---------------------------------------------------------------- El lugar
    /** Donde la fachada del puesto toca el suelo, y la esquina de su edificio. */
    private const val BASE = 214f
    private const val ESQ = 160f
    /** La boca del callejón: su suelo y sus dos jambas. */
    private const val FONDO = 190f
    private const val BOCA_I = 194f
    private const val BOCA_D = 282f
    /** El edificio de la farmacia: su esquina, dónde toca el suelo y su cornisa (cerca y en la boca). */
    private const val FARM = 334f
    private const val FARM_BASE = 230f
    private const val FARM_ALTO = 40f
    private const val FARM_FONDO = 105.6f
    /** A dónde se van las líneas del callejón. */
    private val FUGA = Offset(240f, 158f)

    private val LETRERO = Offset(84f, 70f)
    private val CRUZ = Offset(303f, 117f)
    private val FAROL = Offset(200f, 91f)
    private val POSTE = 175f
    private val BICI = Offset(298f, 278f)

    // ---------------------------------------------------------------- La paleta
    internal val MAGENTA = Color(0xFFFF3E9A)
    internal val CIAN = Color(0xFF3CE8CF)
    internal val AMBAR = Color(0xFFFFB25A)
    private val VELA = Color(0xFFFF9A48)
    private val BLANCO_FRIO = Color(0xFFCFF4F0)
    private val MURO = Color(0xFF7A7098)
    private val MURO_FARM = Color(0xFF6C7A9C)
    private val HORMIGON = Color(0xFF8A86A4)
    private val ASFALTO = Color(0xFF4C4868)
    internal val ACERO = Color(0xFFA6ABC0)
    private val ESMALTE = Color(0xFF4E8A94)
    internal val ROJO = Color(0xFFD8463E)
    private val TOLDO = Color(0xFFB8484C)
    internal val CREMA = Color(0xFFE2D8C4)
    internal val AZUL = Color(0xFF3C7CE4)
    internal val AMARILLO = Color(0xFFE6C63A)
    private val OXIDO = Color(0xFF9A5A3E)
    internal val NEGRO = Color(0xFF2A2638)
    internal val VERDE = Color(0xFF5CA862)
    private val SOMBRA = Color(0xFF2E3068)

    // ---------------------------------------------------------------- Lo que el cuento mueve

    /** Cómo está el puesto de Amaru. */
    internal enum class Puesto { ABIERTO, SOBREMESA, RECOGIDO, CERRADO, PREPARANDO }

    /** Cómo está el letrero del tazón. */
    internal enum class Letrero { ENCENDIDO, REMENDADO, ROTO, APAGADO }

    /** Dónde está la bici de Kei. */
    internal enum class Bici { NO_ES_SU_DIA, EN_EL_TALLER, FUERA, ESPERANDO, ENCHUFADA }

    private fun esDeKei(m: Momento) = m.ficha.vida == "repartidora"

    /** Sin letreros ni anuncios: la Hora Muda y los apagones del nivel −3. */
    internal fun aOscuras(m: Momento) = m.con("apagon")

    /**
     * El puesto, por la hora: es el horario de Amaru en «Un día suyo» (una prueba lo compara), y vale igual los días
     * de Kei, porque el callejón es el mismo. Con toque de queda cierra a las nueve.
     */
    internal fun puesto(m: Momento): Puesto {
        val h = m.hora
        return when {
            m.con("toque") && (h >= 21.0 || h < 5.0) -> Puesto.CERRADO
            h >= 18.0 || h < 4.0 -> Puesto.ABIERTO
            h < 5.0 -> Puesto.SOBREMESA
            h < 10.0 -> Puesto.RECOGIDO
            h < 17.0 -> Puesto.CERRADO
            else -> Puesto.PREPARANDO
        }
    }

    /** El letrero: solo luce con el puesto abierto y con corriente; roto o remendado según el arco `letrero`. */
    internal fun letrero(m: Momento): Letrero = when {
        aOscuras(m) || puesto(m) != Puesto.ABIERTO -> Letrero.APAGADO
        m.arco("letrero") >= 0.5f -> Letrero.ROTO
        m.arco("letrero") > 0.05f -> Letrero.REMENDADO
        else -> Letrero.ENCENDIDO
    }

    /** La bici de Kei. **[Diseño propio]**: a sus horas de trabajo espera en el callejón entre un pedido y otro. */
    internal fun bici(m: Momento): Bici = when {
        !esDeKei(m) -> Bici.NO_ES_SU_DIA
        m.estado == "sin_bici" -> Bici.EN_EL_TALLER
        m.hacer == Hacer.TRABAJO -> Bici.ESPERANDO
        m.hacer == Hacer.COMER && puesto(m) == Puesto.ABIERTO -> Bici.ENCHUFADA
        else -> Bici.FUERA
    }

    /** Si a la bici se le ve la soldadura del cuadro: desde que vuelve del taller hasta que la pinta otra vez. */
    internal fun soldada(m: Momento) = esDeKei(m) && m.estado != "sin_bici" && m.arco("bici") > 0.05f && m.arco("bici") < 0.95f

    /** Si la cortina del puesto lleva el mural de Sami: desde el 15 de junio de la temporada 4. */
    internal fun mural(m: Momento) = m.ficha.temporada > 4 || (m.ficha.temporada == 4 && m.ficha.dia.substring(5) >= "06-15")

    /** Si las máquinas de demolición están en la boca del callejón. */
    internal fun maquinas(m: Momento) = m.arco("obra") >= 0.5f || m.con("maquinas")

    /** La farmacia de Celso: de día despacha con la puerta abierta; de ocho de la tarde a ocho de la mañana está de guardia, con la reja echada y la ventanilla. */
    internal fun farmaciaAbierta(m: Momento) = !aOscuras(m) && m.hora >= 8.0 && m.hora < 20.0

    /** Las bolsas de basura: se sacan de noche y se las llevan a media mañana. */
    private fun hayBolsas(m: Momento) = m.hora >= 20.0 || m.hora < 10.0

    private fun abierto(m: Momento) = puesto(m) == Puesto.ABIERTO || puesto(m) == Puesto.SOBREMESA
    private fun fiesta(m: Momento) = m.con("fiesta") || m.con("faroles")
    private fun llueve(m: Momento) = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA

    /** Cuánto brilla el suelo: siempre algo (abajo nunca se seca), más con lluvia y con el agua subida. */
    private fun mojado(m: Momento) = ((if (llueve(m)) 0.95f else if (m.ficha.clima == Clima.NIEBLA) 0.7f else 0.55f) + 0.4f * m.arco("agua")).coerceAtMost(1.2f)

    // ---------------------------------------------------------------- La luz

    /** Cuánto día hay fuera, de 0 a 1: solo se nota en la rendija de arriba. */
    private fun claridad(h: Double): Float = if (h < 6.0 || h > 19.5) 0f else sin(PI * (h - 6.0) / 13.5).toFloat().coerceIn(0f, 1f)
    private fun arrebol(h: Double): Float = (exp(-((h - 6.9) / 1.1) * ((h - 6.9) / 1.1)) + exp(-((h - 19.2) / 1.0) * ((h - 19.2) / 1.0))).toFloat().coerceIn(0f, 1f)

    /** La noche del callejón: violeta, con la bruma de la ciudad. De día sube un poco la claridad que baja de arriba. */
    private fun luces(m: Momento): Luces {
        val b = m.luces(); val dia = claridad(m.hora); val rosa = arrebol(m.hora)
        val gris = b.nublado
        val alto = lerp(lerp(lerp(Color(0xFF16122E), Color(0xFFB4B6D0), dia), Color(0xFFD69A96), 0.75f * rosa), Color(0xFF6E6C86), 0.5f * gris * dia)
        val medio = lerp(lerp(Color(0xFF2A1E4C), Color(0xFF7C7AA4), 0.8f * dia), Color(0xFF9A6A8C), 0.5f * rosa)
        val niebla = m.ficha.clima == Clima.NIEBLA
        return Luces(
            alto = alto, medio = medio, bajo = lerp(Color(0xFF40285E), Color(0xFF58527E), dia),
            ambiente = lerp(lerp(Color(0xFF2E2B58), Color(0xFF5A6088), 0.8f * dia), Color(0xFF1E1E3C), if (aOscuras(m)) 0.5f else 0f),
            bruma = lerp(lerp(Color(0xFF3A2C66), Color(0xFF666690), 0.8f * dia), Color(0xFF5E5486), if (niebla) 0.6f else 0f), brumaFuerza = if (niebla) 1f else 0.86f,
            solX = 0.5f, solAltura = -1f, solColor = Color.White, solFuerza = 0f, lunaX = b.lunaX, lunaAltura = b.lunaAltura,
            estrellas = 0f, noche = 1f, nublado = b.nublado, eclipse = 0f,
        )
    }

    /** Cuánto lucen los anuncios de la ciudad: de noche, del todo; con el día de fuera, menos; en el apagón, nada. */
    private fun anuncios(m: Momento) = if (aOscuras(m)) 0f else 1f - 0.6f * claridad(m.hora)

    /** Qué parte de las ventanas tiene luz, por la hora. */
    private fun ventanas(m: Momento): Float {
        val h = m.hora
        val k = when { h >= 18.0 -> 0.5f; h < 1.0 -> 0.42f; h < 5.0 -> 0.2f; h < 8.0 -> 0.3f; h < 17.0 -> 0.1f; else -> 0.34f }
        return if (m.con("toque")) k * 0.45f else k
    }

    private val F_LETRERO = 0; private val F_PUESTO = 1; private val F_FAROL = 2; private val F_CRUZ = 3; private val F_ESCAPARATE = 4

    /** Los focos de ahora. Los cinco primeros son siempre el letrero, el puesto, el farol, la cruz y el escaparate (con fuerza 0 si no lucen). */
    private fun focos(m: Momento): List<Foco> = buildList {
        val oscuras = aOscuras(m); val p = puesto(m); val le = letrero(m)
        add(Foco(LETRERO, MAGENTA, when (le) { Letrero.ENCENDIDO -> 1.3f; Letrero.REMENDADO -> 1.05f; else -> 0f }, 66f, BASE))
        add(when {
            abierto(m) && oscuras -> Foco(Offset(84f, 156f), VELA, 0.7f, 50f, BASE)
            abierto(m) -> Foco(Offset(84f, 152f), AMBAR, 1f, 58f, BASE)
            p == Puesto.PREPARANDO -> Foco(Offset(84f, 162f), AMBAR, 0.7f, 48f, BASE)
            p == Puesto.RECOGIDO && !oscuras -> Foco(Offset(142f, 132f), AMBAR, 0.75f, 40f, BASE)
            else -> Foco(Offset(84f, 150f), AMBAR, 0f, 60f, BASE)
        })
        add(Foco(FAROL, AMBAR, if (oscuras) 0f else 0.8f, 64f, 250f))
        add(Foco(CRUZ, CIAN, if (oscuras) 0f else 1f, 48f, 214f))
        add(Foco(Offset(300f, 176f), BLANCO_FRIO, if (oscuras) 0f else if (farmaciaAbierta(m)) 0.85f else 0.3f, 42f, 216f))
        if (!oscuras) add(Foco(Offset(238f, 172f), lerp(CIAN, MAGENTA, 0.35f), 0.55f * anuncios(m), 58f, FONDO))
        else add(Foco(Offset(238f, 150f), Color(0xFF8E90C8), 0.4f, 96f, FONDO))
        // El charco del farol, que es el que alumbra el cajón del objeto del día; en el apagón, una vela sobre el cajón.
        if (!oscuras) add(Foco(Offset(206f, 232f), AMBAR, 0.7f, 46f, 250f)) else add(Foco(Offset(222f, 240f), VELA, 0.75f, 36f, 250f))
        // La claridad que baja de arriba cuando fuera es de día.
        if (claridad(m.hora) > 0.05f) add(Foco(Offset(250f, 40f), Color(0xFFC8CCE8), 0.5f * claridad(m.hora), 130f, FONDO))
        if (bici(m) == Bici.ESPERANDO) add(Foco(BICI + Offset(-66f, -34f), Color(0xFFEFF4FF), 0.8f, 30f, BICI.y))
        if (m.con("faroles")) add(Foco(Offset(236f, 74f), Color(0xFFFF8A4A), 0.7f, 80f, 240f))
        if (m.con("velas")) add(Foco(Offset(120f, 224f), VELA, 0.6f, 44f, 226f))
        if (maquinas(m) && !oscuras) add(Foco(Offset(240f, 152f), AMBAR, 0.5f, 40f, FONDO))
    }

    private fun Alumbrado.sin(vararg cuales: Int) = Alumbrado(l, focos.filterIndexed { i, _ -> i !in cuales })

    /**
     * Los focos que tumban sombras: el puesto (el letrero y su lámpara dan desde el mismo lado, y se toma la luz baja de
     * la barra, que es la que alarga las sombras), el farol y la cruz. A cada cosa la manda el que más la alumbra.
     */
    private fun tumban(f: List<Foco>): List<Foco> = buildList {
        val delPuesto = maxOf(f[F_LETRERO].fuerza, f[F_PUESTO].fuerza)
        if (delPuesto > 0.3f) add(Foco(Offset(if (f[F_PUESTO].p.x > 120f) 142f else 84f, 168f), f[F_PUESTO].color, delPuesto, 90f, BASE))
        if (f[F_FAROL].fuerza > 0.3f) add(Foco(FAROL, AMBAR, f[F_FAROL].fuerza, 70f, 246f))
        if (f[F_CRUZ].fuerza > 0.3f) add(Foco(Offset(CRUZ.x, 150f), CIAN, f[F_CRUZ].fuerza * 0.8f, 60f, 216f))
    }

    private fun elQueManda(tumban: List<Foco>, pie: Offset): Foco? = tumban.maxByOrNull { val d = it.p - Offset(pie.x, pie.y - 30f); it.fuerza / (1f + (d.x * d.x + d.y * d.y) / (it.alcance * it.alcance)) }

    // ---------------------------------------------------------------- Las dos paredes que se alejan

    /** Un punto de la pared de la farmacia: [u] de 0 (la esquina de cerca) a 1 (la boca), [v] de 0 (la cornisa) a 1 (el suelo). */
    private fun pf(u: Float, v: Float): Offset { val top = FARM_ALTO + (FARM_FONDO - FARM_ALTO) * u; val bot = FARM_BASE - (FARM_BASE - FONDO) * u; return Offset(FARM - (FARM - BOCA_D) * u, top + (bot - top) * v) }

    /** Un punto del costado del edificio del puesto: [u] de 0 (la esquina) a 1 (la boca), [v] de 0 (arriba del cuadro) a 1 (el suelo). */
    private fun pc(u: Float, v: Float): Offset { val bot = BASE - (BASE - FONDO) * u; return Offset(ESQ + (BOCA_I - ESQ) * u, -40f + (bot + 40f) * v) }

    private fun cuadro(f: (Float, Float) -> Offset, u0: Float, v0: Float, u1: Float, v1: Float) = poligono(f(u0, v0), f(u1, v0), f(u1, v1), f(u0, v1))

    private fun sueloDelCallejon() = poligono(listOf(Offset(-2f, BASE), Offset(ESQ, BASE), Offset(BOCA_I, FONDO), Offset(BOCA_D, FONDO), Offset(FARM, FARM_BASE), Offset(ANCHO + 2f, FARM_BASE), Offset(ANCHO + 2f, ALTO + 2f), Offset(-2f, ALTO + 2f)))

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val f = focos(m); val a = Alumbrado(l, f)
        ciudad(m, l)
        boca(m, l, a)
        farmacia(m, l, a)
        costado(m, l, a)
        suelo(m, l, a, f)
        fachada(m, l, a, f)
        poste(m, l, a)
        primerPlano(m, l, a, f)
        marco(m, l, a)
        brumas(m, l, f)
        if (m.ficha.clima == Clima.NIEBLA) niebla(m, l) else tiempoQuieto(l, m.ficha.clima, 120f, m.ficha.semilla)
        grano(Azar(Semilla.de("neon", "grano")), 0.5f)
    }

    // ---------------------------------------------------------------- La ciudad, arriba y al fondo

    /** Los niveles de la ciudad: la rendija de arriba, tres filas de torres con sus ventanas, los anuncios y las pasarelas. */
    private fun DrawScope.ciudad(m: Momento, l: Luces) {
        val fijo = Azar(Semilla.de("neon", "ciudad")); val hoy = m.azar("ventanas", (m.hora * 3).toInt() / 9)
        val av = anuncios(m); val vent = ventanas(m)
        val resplandor = lerp(l.bruma, lerp(MAGENTA, CIAN, 0.45f), 0.3f * av)
        drawRect(Brush.verticalGradient(0f to l.alto, 0.3f to l.medio, 0.7f to l.bajo, 1f to lerp(l.bajo, resplandor, 0.7f), startY = -4f, endY = FONDO), Offset(ESQ - 2f, -4f), Size(ANCHO - ESQ + 4f, FONDO + 4f))
        // La rendija: unas barras de nube o de humo, que cambian cada día.
        val nube = m.azar("rendija")
        repeat(5) { mancha(Offset(nube.entre(ESQ, ANCHO), nube.entre(2f, 46f)), nube.entre(40f, 90f), nube.entre(3f, 8f), lerp(l.alto, Color.White, 0.25f), 0.3f + 0.3f * claridad(m.hora)) }
        val calidas = listOf(Color(0xFFFFD08A), Color(0xFFFFE6B8), Color(0xFFBFEFFF), Color(0xFFFFA8C8))
        // Las más lejanas: altas y flacas, casi del color de la bruma.
        var x = ESQ - 6f
        while (x < ANCHO + 10f) { val w = fijo.entre(13f, 26f); torre(l, Rect(x, fijo.entre(14f, 78f), x + w, FONDO), Color(0xFF6A6694), 0.93f, fijo, vent * 0.8f, calidas, fijo.entero(4), resplandor); x += w + fijo.entre(1f, 9f) }
        pasarela(l, 62f, ESQ, ANCHO + 4f, 3f, Color(0xFF5A5684), 0.86f, if (aOscuras(m)) 0.2f else 0.8f)
        // El tren colgante, bajo la pasarela alta: pasa cada tanto.
        val tren = m.azar("tren", (m.hora * 3).toInt())
        if (tren.real() < 0.6f) {
            val tx = tren.entre(ESQ + 20f, ANCHO - 60f)
            drawRoundRect(l.tono(Color(0xFF8A88B0), 0.8f), Offset(tx, 66.5f), Size(44f, 6.5f), CornerRadius(2f))
            for (i in 0..8) drawRect(Color(0xFFFFE9C0).copy(alpha = 0.8f), Offset(tx + 3f + i * 4.4f, 68f), Size(2.8f, 2.4f))
            drawLine(l.tono(Color(0xFF5A5684), 0.8f), Offset(tx + 8f, 65f), Offset(tx + 8f, 66.5f), 0.8f); drawLine(l.tono(Color(0xFF5A5684), 0.8f), Offset(tx + 36f, 65f), Offset(tx + 36f, 66.5f), 0.8f)
        }
        // Las de en medio, con sus anuncios.
        val medias = listOf(Rect(186f, 40f, 212f, FONDO) to 1, Rect(226f, 4f, 250f, FONDO) to 0, Rect(264f, 30f, 294f, FONDO) to 2, Rect(306f, 52f, 338f, 120f) to 3, Rect(350f, 22f, 386f, 60f) to 0)
        medias.forEach { (r, remate) -> torre(l, r, Color(0xFF5E5A8C), 0.74f, fijo, vent, calidas, remate, resplandor) }
        val hoyAnuncio = m.azar("anuncios")
        anuncio(l, Rect(229f, 12f, 249f, 44f), MAGENTA, av, hoyAnuncio, 0.55f)
        anuncio(l, Rect(266f, 44f, 292f, 60f), CIAN, av, hoyAnuncio, 0.55f)
        anuncio(l, Rect(350f, 6f, 378f, 26f), AMBAR, av * 0.9f, hoyAnuncio, 0.6f)
        anuncio(l, Rect(189f, 52f, 200f, 92f), CIAN, av * (if (hoy.real() < 0.8f) 1f else 0.2f), hoyAnuncio, 0.6f)
        pasarela(l, 104f, ESQ, 300f, 4.4f, Color(0xFF4E4A7A), 0.6f, if (aOscuras(m)) 0.15f else 0.9f, Color(0xFFBFF4FF))
        // Las de la avenida, a los lados de la boca.
        torre(l, Rect(BOCA_I - 6f, 84f, 218f, FONDO), Color(0xFF524E7E), 0.5f, fijo, vent * 1.1f, calidas, 1, resplandor)
        torre(l, Rect(258f, 96f, BOCA_D + 6f, FONDO), Color(0xFF524E7E), 0.5f, fijo, vent * 1.1f, calidas, 0, resplandor)
        anuncio(l, Rect(262f, 122f, 280f, 146f), MAGENTA, av, hoyAnuncio, 0.35f)
        anuncio(l, Rect(198f, 112f, 214f, 124f), AMBAR, av, hoyAnuncio, 0.35f)
        // La ropa tendida entre las torres: solo cuando fuera es de día y no llueve.
        if (claridad(m.hora) > 0.35f && !llueve(m)) {
            val ropa = m.azar("ropa"); val pts = cuerda(Offset(ESQ + 10f, 82f), Offset(310f, 86f), 7f, 16)
            cable(Offset(ESQ + 10f, 82f), Offset(310f, 86f), 7f, l.tono(Color(0xFF9A98B8), 0.4f), 0.4f)
            for (i in 2..14 step 2) { val p = pts[i]; val w = ropa.entre(4f, 7f); val h = ropa.entre(5f, 9f); drawRect(l.tono(listOf(CREMA, Color(0xFF8AA6C8), TOLDO, Color(0xFFB8B0D0))[ropa.entero(4)], 0.35f), p + Offset(-w / 2f, 0.4f), Size(w, h)) }
        }
        // Cuando fuera es de día, un rayo de claridad baja por la rendija y se pierde antes de llegar al suelo.
        val dia = claridad(m.hora)
        if (dia > 0.2f && m.ficha.clima != Clima.TORMENTA) drawPath(poligono(Offset(236f, -3f), Offset(300f, -3f), Offset(286f, 176f), Offset(206f, 176f)), Brush.verticalGradient(listOf(Color(0xFFDCE0F4).copy(alpha = 0.3f * dia), Color(0xFFDCE0F4).copy(alpha = 0.08f * dia), Color.Transparent), -3f, 176f), blendMode = BlendMode.Plus)
        // El resplandor de la avenida, que sube por la bruma.
        haloDeBruma(Offset(238f, 176f), 70f, 46f, lerp(resplandor, Color.White, 0.2f), 0.5f * av + 0.12f)
    }

    /** La boca del callejón: la avenida que cruza, y lo que haya en ella (las máquinas, la cuerda de la cadena, la cinta del toque). */
    private fun DrawScope.boca(m: Momento, l: Luces, a: Alumbrado) {
        val av = anuncios(m)
        // La avenida: una franja de luz con las estelas de lo que pasa.
        drawRect(Brush.verticalGradient(listOf(lerp(l.bajo, lerp(CIAN, Color.White, 0.5f), 0.32f * av + 0.06f), a.en(Offset(238f, 186f), ASFALTO, 0f, 0.3f)), 170f, FONDO), Offset(BOCA_I, 170f), Size(BOCA_D - BOCA_I, FONDO - 170f))
        val paso = m.azar("avenida", (m.hora * 3).toInt())
        if (!m.con("toque")) repeat(5) { val y = paso.entre(173f, 183f); val x0 = paso.entre(BOCA_I, BOCA_D - 20f); drawLine((if (it % 2 == 0) Color(0xFFFFE0B0) else Color(0xFFFF6A7A)).copy(alpha = 0.5f * (0.4f + 0.6f * av)), Offset(x0, y), Offset(x0 + paso.entre(8f, 22f), y), 0.7f, StrokeCap.Round) }
        // La barandilla del otro lado de la avenida.
        drawLine(l.tono(Color(0xFF4A4674), 0.5f), Offset(BOCA_I, 171f), Offset(BOCA_D, 171f), 1f)
        if (maquinas(m)) maquina(m, l, a)
        else if (m.arco("obra") > 0.05f) valla(a, 198f, 187f, 20f, -6f)                  // se fueron, y dejaron una valla
        if (m.con("toque")) {
            // La cinta del toque de queda, de jamba a jamba, y un foco rojo de guardia.
            for (y in listOf(176f, 182f)) { val p = cuerda(Offset(BOCA_I, y), Offset(BOCA_D, y + 1f), 2.4f, 22); for (i in 0 until p.size - 1) drawLine(if (i % 2 == 0) Color(0xFFE8E0D0) else Color(0xFFD83A3A), p[i], p[i + 1], 1.3f) }
            resplandor(Offset(238f, 150f), 46f, 0.5f, Color(0xFFFF3A3A))
        }
        if (m.con("cadena")) {
            // La cadena humana, sin la gente: la cuerda de trapos anudados y las luces que dejaron en el suelo.
            val p = cuerda(Offset(BOCA_I - 2f, 181f), Offset(BOCA_D + 4f, 183f), 5f, 20); val az = m.azar("cadena")
            for (i in 0 until p.size - 1) drawLine(a.en(p[i], CREMA, 0f, 0.2f), p[i], p[i + 1], 0.9f)
            for (i in 1 until p.size step 2) drawLine(a.en(p[i], listOf(ROJO, AMARILLO, CIAN, CREMA, AZUL)[az.entero(5)], 0f, 0.15f), p[i], p[i] + Offset(az.entre(-1.4f, 1.4f), az.entre(3f, 6f)), 1.5f, StrokeCap.Round)
            for (i in 0..9) { val q = Offset(BOCA_I + 6f + i * 8.6f, 193f + (i % 3)); drawCircle(Color(0xFFFFE2A8), 0.9f, q); resplandor(q, 6f, 0.5f, VELA) }
        }
    }

    /** La máquina de demolición, quieta en la boca, con sus vallas. */
    private fun DrawScope.maquina(m: Momento, l: Luces, a: Alumbrado) {
        val c = Offset(232f, 172f); val amar = Color(0xFFC8A040)
        fun tin(p: Long, mira: Float = 0f) = a.en(c, Color(p), mira, 0.3f)
        // Las orugas, el cuerpo, la cabina y el brazo con su pinza.
        drawRoundRect(tin(0xFF2E2A3C), Offset(206f, 179f), Size(58f, 10f), CornerRadius(5f))
        for (i in 0..6) drawCircle(tin(0xFF55506A), 2.2f, Offset(211f + i * 8f, 184f))
        drawRect(a.en(c, amar, 0f, 0.3f), Offset(210f, 162f), Size(46f, 18f)); drawRect(a.en(c, lerp(amar, Color.Black, 0.35f), 0f, 0.3f), Offset(210f, 174f), Size(46f, 6f))
        drawRect(a.en(c, lerp(amar, Color.White, 0.15f), 0f, 0.3f), Offset(214f, 146f), Size(20f, 17f)); drawRect(tin(0xFF1E2A44), Offset(216.5f, 148.5f), Size(15f, 9f))
        val brazo = Path().apply { moveTo(248f, 164f); lineTo(268f, 112f); lineTo(280f, 136f) }
        drawPath(brazo, a.en(Offset(264f, 130f), lerp(amar, Color.Black, 0.2f), 0f, 0.3f), style = Stroke(5.4f, join = StrokeJoin.Round, cap = StrokeCap.Round))
        drawPath(brazo, a.en(Offset(264f, 130f), lerp(amar, Color.White, 0.25f), -1f, 0.3f), style = Stroke(1.2f, join = StrokeJoin.Round, cap = StrokeCap.Round))
        drawLine(tin(0xFF8E8AA8), Offset(250f, 150f), Offset(264f, 124f), 1.4f)
        drawPath(poligono(Offset(276f, 134f), Offset(286f, 138f), Offset(284f, 150f), Offset(277f, 144f)), tin(0xFF4A4660)); drawPath(poligono(Offset(276f, 134f), Offset(270f, 142f), Offset(273f, 151f), Offset(278f, 144f)), tin(0xFF3A3650))
        // Las franjas de aviso y la baliza.
        for (i in 0..7) drawPath(poligono(Offset(211f + i * 6f, 174f), Offset(214f + i * 6f, 174f), Offset(211f + i * 6f, 180f), Offset(208f + i * 6f, 180f)), tin(0xFF2A2638).copy(alpha = 0.7f))
        if (!aOscuras(m)) { drawCircle(Color(0xFFFFC04A), 1.8f, Offset(224f, 144.6f)); resplandor(Offset(224f, 144f), 18f, 0.55f, AMBAR) }
        valla(a, 197f, 188f, 26f, 0f); valla(a, 252f, 189f, 28f, 0f)
    }

    /** Una valla de obra con franjas. */
    private fun DrawScope.valla(a: Alumbrado, x: Float, y: Float, w: Float, ladeo: Float) {
        val p = Offset(x + w / 2f, y)
        drawLine(a.en(p, 0xFF55506A, 0f, 0.2f), Offset(x + 2f, y - 9f), Offset(x + 1f, y + 2f), 1f); drawLine(a.en(p, 0xFF55506A, 0f, 0.2f), Offset(x + w - 2f, y - 9f + ladeo), Offset(x + w - 1f, y + 2f), 1f)
        for (k in 0..1) {
            val yy = y - 9f + k * 4.6f
            drawPath(poligono(Offset(x, yy), Offset(x + w, yy + ladeo), Offset(x + w, yy + ladeo + 3f), Offset(x, yy + 3f)), a.en(p, CREMA, 0f, 0.2f))
            var xx = x + 2f
            while (xx < x + w - 3f) { val dy = ladeo * (xx - x) / w; drawPath(poligono(Offset(xx, yy + dy), Offset(xx + 2.6f, yy + dy), Offset(xx + 1.2f, yy + dy + 3f), Offset(xx - 1.4f, yy + dy + 3f)), a.en(p, 0xFFD8643A, 0f, 0.2f)); xx += 5.6f }
        }
    }

    // ---------------------------------------------------------------- La farmacia

    private fun DrawScope.farmacia(m: Momento, l: Luces, a: Alumbrado) {
        val az = Azar(Semilla.de("neon", "farmacia")); val oscuras = aOscuras(m)
        // Lo de la azotea, contra la bruma: el depósito, la antena y el tubo del que sale vapor.
        val dep = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(346f, 12f, 372f, 40f, CornerRadius(4f))) }
        cuerpo(a, dep, 346f, 372f, 26f, Color(0xFF7E86A6))
        for (y in listOf(18f, 26f, 34f)) drawLine(a.en(Offset(359f, y), 0xFF4E5474), Offset(346f, y), Offset(372f, y), 0.6f)
        for (x in listOf(349f, 369f)) drawLine(a.en(Offset(x, 40f), 0xFF3E4260), Offset(x, 38f), Offset(x, 42f), 1.2f)
        drawLine(a.en(Offset(340f, 20f), 0xFF8A88A8), Offset(340f, 40f), Offset(340f, 2f), 0.8f); for (y in listOf(8f, 13f)) drawLine(a.en(Offset(340f, y), 0xFF8A88A8), Offset(335f, y), Offset(345f, y), 0.6f)
        val ty = FARM_ALTO + (FARM_FONDO - FARM_ALTO) * 0.42f
        tuberia(a, Offset(312f, ty + 1f), Offset(312f, ty - 15f), 3.4f, Color(0xFF8088A6), 0.1f)
        drawRect(a.en(Offset(312f, ty - 16f), 0xFF8088A6, 0f, 0.1f), Offset(309.4f, ty - 17.6f), Size(5.2f, 2.4f))
        // La pared que da al callejón.
        val pared = poligono(pf(0f, 0f), pf(1f, 0f), pf(1f, 1f), pf(0f, 1f))
        planoH(a.sin(F_CRUZ, F_ESCAPARATE), pared, BOCA_D, FARM, 150f, MURO_FARM, -1f, 0.1f, 6)
        clipPath(pared) {
            drawRect(Brush.horizontalGradient(listOf(l.bruma.copy(alpha = 0.5f), Color.Transparent), BOCA_D, FARM), Offset(BOCA_D, 0f), Size(FARM - BOCA_D, ALTO))
            // Los paneles del muro, las chorreras y el zócalo.
            for (u in listOf(0.2f, 0.45f, 0.7f)) drawLine(Color.Black.copy(alpha = 0.16f), pf(u, 0f), pf(u, 1f), 0.6f)
            for (v in listOf(0.36f, 0.44f)) drawLine(Color.Black.copy(alpha = 0.18f), pf(0f, v), pf(1f, v), 0.7f)
            repeat(9) { val u = az.real(); val p = pf(u, az.entre(0.02f, 0.3f)); drawLine(Color.Black.copy(alpha = 0.16f), p, p + Offset(0f, az.entre(10f, 34f)), az.entre(0.6f, 1.6f)) }
            repeat(10) { val p = pf(az.real(), az.real()); mancha(p, az.entre(4f, 10f), az.entre(3f, 9f), if (it % 3 == 0) Color(0xFFB8C0D8) else Color.Black, 0.09f) }
            drawPath(cuadro(::pf, 0f, 0.9f, 1f, 1f), Color.Black.copy(alpha = 0.26f))
            charcoDeLuz(a.focos[F_CRUZ], CRUZ + Offset(8f, 0f), 30f, 36f, MURO_FARM, 0.8f)
            charcoDeLuz(a.focos[F_ESCAPARATE], pf(0.4f, 0.9f), 34f, 20f, MURO_FARM, 0.6f)
        }
        // La cornisa.
        drawPath(poligono(pf(0f, 0f) + Offset(0f, -2.4f), pf(1f, 0f) + Offset(0f, -1.6f), pf(1f, 0f) + Offset(0f, 1f), pf(0f, 0f) + Offset(0f, 1.6f)), a.en(pf(0.4f, 0f), lerp(MURO_FARM, Color.White, 0.25f), -1f, 0.15f))
        // La ventana del piso de arriba y su aparato de aire.
        val arriba = cuadro(::pf, 0.3f, 0.1f, 0.56f, 0.27f)
        val luzArriba = m.azar("ventana farmacia").real() < ventanas(m) * 1.6f
        drawPath(arriba, if (luzArriba) Color(0xFFFFD79A) else a.en(pf(0.4f, 0.2f), 0xFF2A2C4A, -1f))
        if (luzArriba) { drawPath(cuadro(::pf, 0.3f, 0.1f, 0.42f, 0.27f), Color(0xFFB8744A).copy(alpha = 0.6f)); resplandor(pf(0.43f, 0.18f), 16f, 0.3f, AMBAR) }
        drawPath(arriba, a.en(pf(0.4f, 0.2f), 0xFF8A90B0, -1f), style = Stroke(0.9f)); drawLine(a.en(pf(0.4f, 0.2f), 0xFF8A90B0, -1f), pf(0.43f, 0.1f), pf(0.43f, 0.27f), 0.6f)
        val aire = cuadro(::pf, 0.66f, 0.2f, 0.86f, 0.3f)
        drawPath(aire, a.en(pf(0.76f, 0.25f), 0xFFA0A6BE, -1f, 0.2f)); for (k in 1..3) drawLine(Color.Black.copy(alpha = 0.3f), pf(0.68f, 0.2f + 0.025f * k), pf(0.84f, 0.2f + 0.025f * k), 0.4f)
        drawLine(Color.Black.copy(alpha = 0.22f), pf(0.76f, 0.3f), pf(0.76f, 0.5f), 0.8f)
        // El escaparate y la puerta. De día, abiertos y con su luz fría; de guardia, la reja echada y la ventanilla.
        val esc = cuadro(::pf, 0.14f, 0.52f, 0.6f, 0.8f); val puerta = cuadro(::pf, 0.68f, 0.5f, 0.88f, 1f); val abierta = farmaciaAbierta(m)
        val tinta = if (oscuras) Color(0xFF1A1C34) else Color(0xFF2E6A6A)
        if (abierta) {
            drawPath(esc, Brush.horizontalGradient(listOf(Color(0xFFE2FFF6), Color(0xFFA6E8DC)), BOCA_D, FARM)); drawPath(puerta, Brush.verticalGradient(listOf(Color(0xFFD2FBF0), Color(0xFF8AD8CC)), 140f, 200f))
            // Los estantes, con sus frascos, a contraluz, y el mostrador.
            for (v in listOf(0.595f, 0.665f, 0.735f)) {
                drawLine(tinta.copy(alpha = 0.7f), pf(0.14f, v), pf(0.6f, v), 0.6f)
                var u = 0.165f
                while (u < 0.585f) {
                    val h = az.entre(0.016f, 0.04f); val w = az.entre(0.012f, 0.022f)
                    val c = listOf(tinta, tinta, Color(0xFF3E8A8A), Color(0xFFB86A8A), Color(0xFFC89A5A))[az.entero(5)]
                    drawPath(cuadro(::pf, u, v - h, u + w, v), c.copy(alpha = az.entre(0.55f, 0.9f))); u += w + az.entre(0.008f, 0.03f)
                }
            }
            drawPath(cuadro(::pf, 0.14f, 0.765f, 0.6f, 0.8f), tinta.copy(alpha = 0.55f))
        } else {
            drawPath(esc, Brush.horizontalGradient(listOf(a.en(pf(0.6f, 0.66f), 0xFF2A3A5A, -1f), a.en(pf(0.14f, 0.66f), 0xFF34486A, -1f)), BOCA_D, FARM)); drawPath(puerta, a.en(pf(0.8f, 0.7f), 0xFF2C3454, -1f))
            // La reja de la guardia y, en ella, la ventanilla encendida.
            var v = 0.535f; while (v < 0.8f) { drawLine(Color.Black.copy(alpha = 0.3f), pf(0.14f, v), pf(0.6f, v), 0.5f); v += 0.022f }
            var u = 0.18f; while (u < 0.6f) { drawLine(Color.Black.copy(alpha = 0.22f), pf(u, 0.52f), pf(u, 0.8f), 0.4f); u += 0.05f }
            if (!oscuras) { drawPath(cuadro(::pf, 0.26f, 0.61f, 0.46f, 0.73f), Brush.horizontalGradient(listOf(Color(0xFFE2FFF6), Color(0xFFA6E8DC)), BOCA_D, FARM)); drawPath(cuadro(::pf, 0.26f, 0.705f, 0.46f, 0.73f), tinta.copy(alpha = 0.6f)); resplandor(pf(0.36f, 0.67f), 14f, 0.4f, BLANCO_FRIO) }
            else { drawCircle(Color(0xFFFFD08A), 0.9f, pf(0.36f, 0.7f)); resplandor(pf(0.36f, 0.69f), 12f, 0.5f, VELA) }
        }
        // El toldillo de la farmacia: una tira de lona verde y crema sobre el escaparate.
        for (i in 0..8) drawPath(cuadro(::pf, 0.1f + i * 0.09f, 0.455f, 0.1f + (i + 1) * 0.09f, 0.505f), a.en(pf(0.14f + i * 0.09f, 0.48f), if (i % 2 == 0) Color(0xFF3E9A84) else CREMA, -1f, 0.1f))
        drawLine(Color.Black.copy(alpha = 0.3f), pf(0.1f, 0.507f), pf(0.91f, 0.507f), 0.6f)
        if (abierta) {
            // El caballete de la acera, con su cruz pintada.
            val c = pf(0.42f, 1f) + Offset(-12f, 8f)
            drawPath(poligono(c + Offset(-6f, 0f), c + Offset(-4f, -15f), c + Offset(4f, -15f), c + Offset(6f, 0f)), a.en(c, CREMA)); drawLine(a.en(c, 0xFF3E9A84), c + Offset(0f, -11.4f), c + Offset(0f, -4.6f), 1.8f); drawLine(a.en(c, 0xFF3E9A84), c + Offset(-3.4f, -8f), c + Offset(3.4f, -8f), 1.8f)
            drawOval(Color.Black.copy(alpha = 0.3f), c + Offset(-7f, -1.4f), Size(14f, 3f))
        }
        for (p in listOf(esc, puerta)) drawPath(p, a.en(pf(0.4f, 0.6f), 0xFF9AA2BE, -1f), style = Stroke(1.1f))
        drawLine(a.en(pf(0.37f, 0.6f), 0xFF9AA2BE, -1f), pf(0.37f, 0.52f), pf(0.37f, 0.8f), 0.8f)
        drawLine(Color(0xFF3A6A70), pf(0.72f, 0.74f), pf(0.72f, 0.82f), 1f)
        // La cruz de guardia, en su brazo.
        drawLine(a.en(CRUZ, 0xFF6A7090), pf(0.3f, 0.36f), CRUZ + Offset(-9f, -6f), 1.4f); drawLine(a.en(CRUZ, 0xFF6A7090), pf(0.3f, 0.44f), CRUZ + Offset(-9f, 6f), 1.4f)
        drawRoundRect(a.en(CRUZ, 0xFF2A2E4A), CRUZ + Offset(-11f, -11f), Size(22f, 22f), CornerRadius(2.4f))
        drawRoundRect(a.en(CRUZ, 0xFF8A90B0), CRUZ + Offset(-11f, -11f), Size(22f, 22f), CornerRadius(2.4f), style = Stroke(0.8f))
        val cruz = Path().apply { moveTo(CRUZ.x - 2.8f, CRUZ.y - 7.6f); lineTo(CRUZ.x + 2.8f, CRUZ.y - 7.6f); lineTo(CRUZ.x + 2.8f, CRUZ.y - 2.8f); lineTo(CRUZ.x + 7.6f, CRUZ.y - 2.8f); lineTo(CRUZ.x + 7.6f, CRUZ.y + 2.8f); lineTo(CRUZ.x + 2.8f, CRUZ.y + 2.8f); lineTo(CRUZ.x + 2.8f, CRUZ.y + 7.6f); lineTo(CRUZ.x - 2.8f, CRUZ.y + 7.6f); lineTo(CRUZ.x - 2.8f, CRUZ.y + 2.8f); lineTo(CRUZ.x - 7.6f, CRUZ.y + 2.8f); lineTo(CRUZ.x - 7.6f, CRUZ.y - 2.8f); lineTo(CRUZ.x - 2.8f, CRUZ.y - 2.8f); close() }
        tuboNeon(cruz, CIAN, if (oscuras) 0f else 1f, 1.5f)
        // Las pintadas y los carteles, según el arco.
        pintas(m, a, ::pf, 0.02f, 0.5f, 0.12f, 0.88f, "farmacia", -1f)

        // La cara de cerca: una pilastra con su bajante, el contador y más carteles.
        val cara = Path().apply { addRect(Rect(FARM, FARM_ALTO, ANCHO + 2f, FARM_BASE)) }
        planoV(a.sin(F_CRUZ, F_ESCAPARATE), cara, 372f, FARM_ALTO, FARM_BASE, lerp(MURO_FARM, Color(0xFF4A4868), 0.5f), 0f, pasos = 7)
        clipPath(cara) {
            drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.07f), Color.Transparent), FARM, FARM + 8f), Offset(FARM, FARM_ALTO), Size(8f, FARM_BASE - FARM_ALTO))
            for (y in listOf(86f, 132f, 178f)) drawLine(Color.Black.copy(alpha = 0.2f), Offset(FARM, y), Offset(ANCHO, y), 0.7f)
            repeat(8) { val p = Offset(az.entre(FARM, ANCHO), az.entre(FARM_ALTO, 90f)); drawLine(Color.Black.copy(alpha = 0.15f), p, p + Offset(0f, az.entre(14f, 44f)), az.entre(0.6f, 1.8f)) }
            repeat(9) { mancha(Offset(az.entre(FARM, ANCHO), az.entre(60f, FARM_BASE)), az.entre(5f, 12f), az.entre(4f, 10f), if (it % 2 == 0) Color(0xFFC0C6DC) else Color.Black, 0.1f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.34f)), 206f, FARM_BASE), Offset(FARM, 206f), Size(ANCHO - FARM, FARM_BASE - 206f))
        }
        drawRect(a.en(Offset(356f, FARM_ALTO), lerp(MURO_FARM, Color.White, 0.3f)), Offset(FARM - 1.5f, FARM_ALTO - 2.6f), Size(ANCHO - FARM + 4f, 3.6f))
        tuberia(a, Offset(345f, FARM_ALTO + 2f), Offset(345f, FARM_BASE - 2f), 4.2f, Color(0xFF7A82A2))
        drawRoundRect(a.en(Offset(366f, 132f), 0xFF5E6486), Offset(355f, 118f), Size(21f, 27f), CornerRadius(1.6f))
        drawRect(a.en(Offset(366f, 126f), 0xFF2A2E48), Offset(358f, 121.5f), Size(15f, 8f)); drawCircle(Color(0xFFFF5A4A).copy(alpha = if (oscuras) 0.2f else 1f), 1f, Offset(360.5f, 138.6f)); drawCircle(Color(0xFF7CFFB8).copy(alpha = if (oscuras) 0.2f else 0.9f), 1f, Offset(364.5f, 138.6f))
        tuberia(a, Offset(366f, 145f), Offset(366f, FARM_BASE - 2f), 1.8f, Color(0xFF6A7090)); tuberia(a, Offset(370f, FARM_ALTO + 2f), Offset(370f, 118f), 1.8f, Color(0xFF6A7090))
        for ((q, w, h) in listOf(Triple(Offset(351f, 158f), 12f, 15f), Triple(Offset(364f, 166f), 13f, 17f), Triple(Offset(352f, 180f), 11f, 12f))) { drawRect(a.en(q, lerp(CREMA, MURO_FARM, 0.5f)).copy(alpha = 0.6f), q, Size(w, h)); drawRect(a.en(q, NEGRO).copy(alpha = 0.26f), q + Offset(w * 0.15f, h * 0.2f), Size(w * 0.7f, h * 0.25f)) }
        pintasPlanas(m, a, Rect(350f, 152f, 380f, 208f), "pilastra")
    }

    // ---------------------------------------------------------------- El costado del edificio del puesto

    private fun DrawScope.costado(m: Momento, l: Luces, a: Alumbrado) {
        val az = Azar(Semilla.de("neon", "costado"))
        val pared = poligono(pc(0f, 0f), pc(1f, 0f), pc(1f, 1f), pc(0f, 1f))
        planoH(a, pared, ESQ, BOCA_I, 150f, lerp(MURO, Color(0xFF5A5480), 0.35f), 1f, 0.1f, 5)
        clipPath(pared) {
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, l.bruma.copy(alpha = 0.55f)), ESQ, BOCA_I), Offset(ESQ, 0f), Size(BOCA_I - ESQ, ALTO))
            for (v in listOf(0.34f, 0.52f, 0.7f)) drawLine(Color.Black.copy(alpha = 0.2f), pc(0f, v), pc(1f, v), 0.7f)
            repeat(8) { val p = pc(az.real(), az.entre(0.2f, 0.7f)); drawLine(Color.Black.copy(alpha = 0.16f), p, p + Offset(0f, az.entre(10f, 30f)), az.entre(0.6f, 1.4f)) }
            drawPath(cuadro(::pc, 0f, 0.93f, 1f, 1f), Color.Black.copy(alpha = 0.3f))
        }
        // Una ventana alta, el aparato de aire y la puerta de servicio.
        val vent = cuadro(::pc, 0.3f, 0.36f, 0.7f, 0.47f); val luz = m.azar("ventana costado").real() < ventanas(m) * 1.3f
        drawPath(vent, if (luz) Color(0xFFBFF0FF) else a.en(pc(0.5f, 0.4f), 0xFF26284A, 1f, 0.2f)); drawPath(vent, a.en(pc(0.5f, 0.4f), 0xFF8A88AC, 1f, 0.2f), style = Stroke(0.8f))
        if (luz) resplandor(pc(0.5f, 0.42f), 14f, 0.25f, Color(0xFFBFF0FF))
        drawPath(cuadro(::pc, 0.2f, 0.58f, 0.62f, 0.66f), a.en(pc(0.4f, 0.62f), 0xFF9AA0B8, 1f, 0.15f)); drawLine(Color.Black.copy(alpha = 0.25f), pc(0.4f, 0.66f), pc(0.4f, 0.8f), 0.7f)
        val puerta = cuadro(::pc, 0.62f, 0.78f, 0.9f, 1f)
        drawPath(puerta, a.en(pc(0.76f, 0.88f), 0xFF3E4668, 1f, 0.2f)); drawPath(puerta, a.en(pc(0.76f, 0.88f), 0xFF8A88AC, 1f, 0.2f), style = Stroke(0.6f))
        // La máquina de bebidas: una caja de luz arrimada a la pared, con sus latas en fila.
        val maq = cuadro(::pc, 0.1f, 0.745f, 0.52f, 0.985f); val conLuz = !aOscuras(m)
        drawPath(maq, a.en(pc(0.3f, 0.86f), 0xFF3A4670, 1f)); drawPath(poligono(pc(0.1f, 0.745f), pc(0.52f, 0.745f), pc(0.52f, 0.745f) + Offset(-3f, -3.4f), pc(0.1f, 0.745f) + Offset(-3f, -3.4f)), a.en(pc(0.3f, 0.74f), 0xFF55628E))
        val luna = cuadro(::pc, 0.14f, 0.762f, 0.48f, 0.9f)
        drawPath(luna, if (conLuz) Brush.verticalGradient(listOf(Color(0xFFFFF4D8), Color(0xFFFFD99A)), 150f, 190f) else Brush.verticalGradient(listOf(a.en(pc(0.3f, 0.8f), 0xFF2A2E4E, 1f), a.en(pc(0.3f, 0.9f), 0xFF22264A, 1f)), 150f, 190f))
        for (k in 0..3) { val v = 0.786f + k * 0.03f; drawLine(Color(0xFF7A5A3A).copy(alpha = 0.5f), pc(0.14f, v + 0.012f), pc(0.48f, v + 0.012f), 0.4f)
            for (i in 0..4) drawPath(cuadro(::pc, 0.165f + i * 0.062f, v - 0.012f, 0.2f + i * 0.062f, v + 0.01f), listOf(ROJO, CIAN, AMARILLO, VERDE, MAGENTA)[(i + k) % 5].copy(alpha = if (conLuz) 0.85f else 0.3f)) }
        drawPath(cuadro(::pc, 0.2f, 0.925f, 0.42f, 0.965f), Color.Black.copy(alpha = 0.5f))
        if (conLuz) { resplandor(pc(0.3f, 0.84f), 22f, 0.3f, AMBAR); drawCircle(Color(0xFF7CFFB8), 0.7f, pc(0.5f, 0.91f)) }
        tuberia(a, pc(0.04f, 0.3f), pc(0.04f, 0.99f), 2.2f, Color(0xFF7A7C9E), 0.1f)
        pintas(m, a, ::pc, 0.76f, 0.6f, 0.98f, 0.92f, "costado", 1f)
    }

    // ---------------------------------------------------------------- Pintadas y carteles (arco `pintas`)

    /** Carteles pegados y pintadas en una pared que se aleja. Sin letra que se pueda leer: manchas, franjas y un aro. */
    private fun DrawScope.pintas(m: Momento, a: Alumbrado, f: (Float, Float) -> Offset, u0: Float, v0: Float, u1: Float, v1: Float, donde: String, mira: Float) {
        val cuanto = m.arco("pintas"); if (cuanto < 0.05f) return
        val az = Azar(Semilla.de("neon", "pintas", donde)); val n = (2 + cuanto * 5).toInt()
        repeat(n) {
            val u = az.entre(u0, u1 - (u1 - u0) * 0.5f); val v = az.entre(v0, v1 - (v1 - v0) * 0.4f); val w = (u1 - u0) * az.entre(0.3f, 0.5f); val h = (v1 - v0) * az.entre(0.25f, 0.4f)
            val c = f(u + w / 2f, v + h / 2f); val papel = listOf(CREMA, Color(0xFFE8D25A), Color(0xFFD86A5A), Color(0xFF8AC8D8))[az.entero(4)]
            drawPath(cuadro(f, u, v, u + w, v + h), a.en(c, papel, mira, 0.15f))
            drawPath(cuadro(f, u + w * 0.15f, v + h * 0.15f, u + w * 0.85f, v + h * 0.45f), a.en(c, NEGRO, mira, 0.15f).copy(alpha = 0.75f))
            drawLine(a.en(c, NEGRO, mira, 0.15f).copy(alpha = 0.6f), f(u + w * 0.15f, v + h * 0.62f), f(u + w * 0.8f, v + h * 0.62f), 0.6f); drawLine(a.en(c, NEGRO, mira, 0.15f).copy(alpha = 0.6f), f(u + w * 0.15f, v + h * 0.78f), f(u + w * 0.6f, v + h * 0.78f), 0.6f)
        }
    }

    /** Lo mismo en una pared de frente: carteles, un brochazo y el aro de una rueda pintado con plantilla. */
    private fun DrawScope.pintasPlanas(m: Momento, a: Alumbrado, zona: Rect, donde: String) {
        val cuanto = m.arco("pintas"); if (cuanto < 0.05f) return
        val az = Azar(Semilla.de("neon", "pintas", donde)); val n = (2 + cuanto * 5).toInt()
        clipRect(zona.left, zona.top, zona.right, zona.bottom) {
            repeat(n) {
                val w = az.entre(9f, 15f); val h = az.entre(12f, 19f); val p = Offset(az.entre(zona.left, zona.right - w * 0.7f), az.entre(zona.top, zona.bottom - h * 0.7f))
                val papel = listOf(CREMA, Color(0xFFE8D25A), Color(0xFFD86A5A), Color(0xFF8AC8D8))[az.entero(4)]
                drawRect(a.en(p, papel), p, Size(w, h))
                drawRect(a.en(p, NEGRO).copy(alpha = 0.75f), p + Offset(w * 0.14f, h * 0.12f), Size(w * 0.72f, h * 0.34f))
                for (k in 0..2) drawLine(a.en(p, NEGRO).copy(alpha = 0.55f), p + Offset(w * 0.14f, h * (0.58f + 0.12f * k)), p + Offset(w * (0.8f - 0.18f * k), h * (0.58f + 0.12f * k)), 0.6f)
                if (az.real() < 0.4f) drawPath(poligono(p + Offset(w, 0f), p + Offset(w - 3.4f, 0f), p + Offset(w, 3.8f)), a.en(p, lerp(MURO_FARM, HORMIGON, 0.4f)))
            }
            if (cuanto > 0.4f) {
                val c = Offset(zona.center.x + 3f, zona.bottom - 14f); val tinta = a.en(c, 0xFFE0463E).copy(alpha = 0.8f)
                drawCircle(tinta, 6.4f, c, style = Stroke(1.5f)); for (k in 0..5) { val an = (PI * k / 3).toFloat(); drawLine(tinta, c, c + Offset(kotlin.math.cos(an), sin(an)) * 6.2f, 0.6f) }
                drawLine(tinta, c + Offset(-10f, 9f), c + Offset(11f, 8f), 1.8f, StrokeCap.Round)
            }
        }
    }

    // ---------------------------------------------------------------- El suelo

    private fun charco1() = Path().apply { moveTo(64f, 252f); cubicTo(86f, 242f, 132f, 246f, 160f, 250f); cubicTo(184f, 253f, 176f, 266f, 150f, 268f); cubicTo(120f, 271f, 76f, 272f, 62f, 264f); cubicTo(54f, 259f, 56f, 255f, 64f, 252f); close() }
    private fun charco2() = Path().apply { moveTo(228f, 200f); cubicTo(244f, 196f, 268f, 198f, 278f, 203f); cubicTo(284f, 207f, 270f, 211f, 250f, 211f); cubicTo(232f, 211f, 218f, 205f, 228f, 200f); close() }
    private fun charco3() = Path().apply { moveTo(286f, 244f); cubicTo(304f, 240f, 336f, 242f, 344f, 248f); cubicTo(348f, 253f, 326f, 256f, 304f, 255f); cubicTo(288f, 254f, 278f, 248f, 286f, 244f); close() }

    private fun DrawScope.suelo(m: Momento, l: Luces, a: Alumbrado, f: List<Foco>) {
        val az = Azar(Semilla.de("neon", "suelo")); val suelo = sueloDelCallejon(); val moj = mojado(m); val agua = m.arco("agua")
        planoV(a, suelo, 200f, FONDO, ALTO, ASFALTO, 0f, 0.3f, 0f, 7)
        clipPath(suelo) {
            // Las losas: las juntas que se van a la fuga y las que cruzan, cada vez más apretadas.
            for (i in -6..14) { val x = -160f + i * 44f; drawLine(Color.Black.copy(alpha = 0.2f), Offset(x, ALTO + 2f), FUGA + (Offset(x, ALTO + 2f) - FUGA) * 0.25f, 0.7f) }
            var y = FONDO + 3f; var paso = 3f
            while (y < ALTO) { drawLine(Color.Black.copy(alpha = 0.17f), Offset(0f, y), Offset(ANCHO, y), 0.6f); paso *= 1.3f; y += paso }
            // Remiendos de asfalto, manchas de aceite y la rejilla del desagüe.
            repeat(16) { val p = Offset(az.entre(0f, ANCHO), az.entre(FONDO + 6f, ALTO)); val k = (p.y - FONDO) / 95f; mancha(p, az.entre(8f, 26f) * (0.4f + k), az.entre(2f, 6f) * (0.4f + k), if (it % 3 == 0) Color(0xFF9A98B8) else Color.Black, 0.12f) }
            drawPath(poligono(Offset(100f, 226f), Offset(150f, 224f), Offset(162f, 238f), Offset(96f, 240f)), Color.Black.copy(alpha = 0.13f))
            // El carril de las bicis: dos rayas amarillas, gastadas, que se van a la boca, y el aro de una rueda pintado.
            for (x in listOf(214f, 262f)) { val p0 = Offset(x + (x - FUGA.x) * 2.2f, ALTO + 2f); var t = 0.1f
                while (t < 0.9f) { val q0 = p0 + (FUGA - p0) * t; val q1 = p0 + (FUGA - p0) * (t + 0.07f); if (az.real() > 0.25f) drawLine(a.en(q0, 0xFFE8C850).copy(alpha = 0.75f * (1f - t) + 0.2f), q0, q1, 2.6f * (1f - t) + 0.5f); t += 0.085f } }
            drawOval(a.en(Offset(246f, 262f), 0xFFE8C850).copy(alpha = 0.6f), Offset(232f, 257f), Size(28f, 9f), style = Stroke(1.8f)); drawOval(a.en(Offset(246f, 262f), 0xFFE8C850).copy(alpha = 0.55f), Offset(243.6f, 260.6f), Size(5f, 1.8f)); for (k in 0..5) { val an = (PI * k / 6).toFloat(); drawLine(a.en(Offset(246f, 262f), 0xFFE8C850).copy(alpha = 0.4f), Offset(246f - 14f * kotlin.math.cos(an), 261.5f - 4.5f * sin(an)), Offset(246f + 14f * kotlin.math.cos(an), 261.5f + 4.5f * sin(an)), 0.5f) }
            // La acera del puesto: baldosas chicas, en damero gastado.
            val acera = poligono(Offset(-2f, BASE), Offset(ESQ, BASE), Offset(ESQ + 14f, 240f), Offset(-2f, 244f))
            clipPath(acera) {
                for (i in -1..12) for (j in 0..3) if ((i + j) % 2 == 0) { val y0 = BASE + j * 7.4f; val x0 = i * 14.4f + j * 1.2f; drawPath(poligono(Offset(x0, y0), Offset(x0 + 14.4f, y0), Offset(x0 + 15.6f, y0 + 7.4f), Offset(x0 + 1.2f, y0 + 7.4f)), a.en(Offset(x0 + 7f, y0 + 3f), 0xFF8A86A8).copy(alpha = 0.22f)) }
                for (j in 1..3) drawLine(Color.Black.copy(alpha = 0.2f), Offset(0f, BASE + j * 7.4f), Offset(ESQ + 14f, BASE + j * 7.4f), 0.5f)
            }
            drawLine(a.en(Offset(84f, 243f), lerp(HORMIGON, Color.White, 0.15f)).copy(alpha = 0.7f), Offset(-2f, 244f), Offset(ESQ + 14f, 240f), 1.2f); drawLine(Color.Black.copy(alpha = 0.34f), Offset(-2f, 245.4f), Offset(ESQ + 14f, 241.4f), 1.4f)
            // Grietas, y otra tapa de registro.
            for ((p0, d) in listOf(Offset(230f, 222f) to Offset(26f, 9f), Offset(300f, 262f) to Offset(-30f, 12f), Offset(120f, 262f) to Offset(34f, 8f))) drawPath(Path().apply { moveTo(p0.x, p0.y); lineTo(p0.x + d.x * 0.3f, p0.y + d.y * 0.5f); lineTo(p0.x + d.x * 0.55f, p0.y + d.y * 0.4f); lineTo(p0.x + d.x, p0.y + d.y) }, Color.Black.copy(alpha = 0.3f), style = Stroke(0.6f))
            drawOval(a.en(Offset(236f, 214f), 0xFF34304E), Offset(226f, 211f), Size(20f, 5f)); drawOval(a.en(Offset(236f, 214f), 0xFF8A86A8).copy(alpha = 0.6f), Offset(226f, 211f), Size(20f, 5f), style = Stroke(0.5f))
            // El bordillo de la acera del puesto y la canaleta de la farmacia.
            drawRect(a.en(Offset(80f, 217f), lerp(HORMIGON, Color.White, 0.1f)), Offset(-2f, BASE), Size(ESQ + 2f, 3.2f)); drawRect(Color.Black.copy(alpha = 0.3f), Offset(-2f, BASE + 3.2f), Size(ESQ + 2f, 1.3f))
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(BOCA_D - 3f, FONDO + 1f), Offset(FARM - 6f, FARM_BASE + 3f), 2f); drawLine(Color.Black.copy(alpha = 0.28f), Offset(FARM - 6f, FARM_BASE + 3f), Offset(ANCHO, FARM_BASE + 3f), 2f)
            // La luz que cae en el suelo: cada foco deja su charco.
            charcoDeLuz(f[F_LETRERO], Offset(90f, 236f), 104f, 26f, ASFALTO, 0.55f)
            if (f[F_PUESTO].fuerza > 0f) drawPath(poligono(Offset(18f, BASE + 4f), Offset(150f, BASE + 4f), Offset(190f, 262f), Offset(-10f, 262f)), Brush.verticalGradient(listOf(f[F_PUESTO].color.copy(alpha = 0.3f * f[F_PUESTO].fuerza), Color.Transparent), BASE + 4f, 262f), blendMode = BlendMode.Plus)
            charcoDeLuz(f[F_FAROL], Offset(206f, 250f), 70f, 24f, ASFALTO, 1f)
            if (!aOscuras(m)) {
                charcoDeLuz(f[F_CRUZ], Offset(298f, 216f), 42f, 13f, ASFALTO, 0.7f)
                // Lo que sale por la puerta y el escaparate de la farmacia.
                drawPath(poligono(pf(0.88f, 1f), pf(0.14f, 1f), Offset(264f, 246f), Offset(226f, 206f)), Brush.horizontalGradient(listOf(Color.Transparent, BLANCO_FRIO.copy(alpha = 0.34f * f[F_ESCAPARATE].fuerza)), 226f, FARM), blendMode = BlendMode.Plus)
            }
            charcoDeLuz(Foco(Offset(238f, 180f), lerp(CIAN, Color.White, 0.4f), 0.6f * anuncios(m) + 0.14f, 60f), Offset(238f, 200f), 52f, 18f, ASFALTO)
            // El brillo del suelo mojado hacia la boca: devuelve el resplandor de la avenida.
            drawRect(Brush.verticalGradient(listOf(lerp(l.bajo, Color.White, 0.12f).copy(alpha = 0.34f * moj), Color.Transparent), FONDO, FONDO + 44f), Offset(BOCA_I - 40f, FONDO), Size(BOCA_D - BOCA_I + 100f, 44f))
            val br = Azar(Semilla.de("neon", "brillos"))
            repeat(46) { val p = Offset(br.entre(0f, ANCHO), br.entre(FONDO + 3f, ALTO)); val k = (p.y - FONDO) / 95f; drawLine(lerp(l.bajo, Color.White, 0.3f).copy(alpha = (0.1f + 0.1f * br.real()) * moj), p, p + Offset(br.entre(5f, 16f) * (0.4f + k), 0f), 0.4f + 0.5f * k, StrokeCap.Round) }
            // Los charcos: espejos. Devuelven la bruma de arriba y, cada uno, la luz que tiene encima.
            val espejo = lerp(Color(0xFF0E0C22), l.bajo, 0.6f)
            for (c in listOf(charco1(), charco2(), charco3())) { drawPath(c, Color(0xFF080618).copy(alpha = 0.5f)); drawPath(c, Brush.verticalGradient(listOf(espejo.copy(alpha = 0.9f), espejo.copy(alpha = 0.35f)), FONDO, ALTO)); drawPath(c, Color(0xFFC8C4F0).copy(alpha = 0.22f), style = Stroke(0.6f)) }
            // El agua subida: lo cubre todo, más cuanto más sube. Es un espejo que devuelve la bruma, con sus ondas.
            if (agua > 0.05f) {
                val lamina = lerp(Color(0xFF141A40), l.bajo, 0.55f)
                drawRect(Brush.verticalGradient(listOf(lerp(lamina, Color.White, 0.14f).copy(alpha = 0.3f + 0.5f * agua), lamina.copy(alpha = 0.3f + 0.55f * agua)), FONDO, ALTO), Offset(0f, FONDO), Size(ANCHO, ALTO - FONDO))
                val on = m.azar("ondas")
                repeat((16 + 46 * agua).toInt()) { val p = Offset(on.entre(0f, ANCHO), on.entre(FONDO + 4f, ALTO)); val k = (p.y - FONDO) / 95f; drawLine(Color(0xFFC8D2FA).copy(alpha = 0.16f + 0.16f * agua), p, p + Offset(on.entre(6f, 24f) * (0.4f + k), 0f), 0.5f + 0.5f * k, StrokeCap.Round) }
                if (agua > 0.4f) {
                    // La línea del agua al pie de las paredes, y lo que flota.
                    drawLine(Color(0xFFD0D8FF).copy(alpha = 0.4f), Offset(0f, BASE + 4.6f), Offset(ESQ, BASE + 4.6f), 0.7f); drawLine(Color(0xFFD0D8FF).copy(alpha = 0.3f), Offset(BOCA_D, FONDO + 1f), Offset(FARM, FARM_BASE + 1f), 0.7f)
                    drawPath(poligono(Offset(244f, 222f), Offset(262f, 219f), Offset(264f, 226f), Offset(246f, 229f)), a.en(Offset(254f, 224f), 0xFFCFA83C)); drawLine(Color(0xFFD0D8FF).copy(alpha = 0.4f), Offset(242f, 229.6f), Offset(266f, 226.6f), 0.5f)
                    drawOval(a.en(Offset(150f, 252f), 0xFF8AC8D8), Offset(146f, 250f), Size(9f, 2.6f)); drawOval(a.en(Offset(318f, 236f), CREMA), Offset(314f, 235f), Size(6f, 2f))
                }
            }
            // Los reflejos: cada luz baja por el suelo mojado.
            val r = m.azar("reflejos")
            if (f[F_LETRERO].fuerza > 0f) {
                reflejoMojado(Offset(84f, BASE + 5f), 66f, 52f, MAGENTA, (0.62f * moj * f[F_LETRERO].fuerza).coerceAtMost(1f), r)
                // El tazón, del revés, en el charco grande.
                clipPath(charco1()) { scale(1.15f, -0.4f, Offset(84f, 226f)) { letreroTazon(letrero(m), (0.7f * moj).coerceAtMost(0.8f)) } }
            }
            if (f[F_PUESTO].fuerza > 0f) reflejoMojado(Offset(if (f[F_PUESTO].p.x > 120f) 142f else 86f, BASE + 5f), 44f, if (f[F_PUESTO].p.x > 120f) 16f else 100f, f[F_PUESTO].color, 0.5f * moj * f[F_PUESTO].fuerza, r, 1.8f)
            if (f[F_FAROL].fuerza > 0f) reflejoMojado(Offset(FAROL.x, 244f), 40f, 14f, AMBAR, 0.7f * moj, r)
            if (!aOscuras(m)) reflejoMojado(Offset(176f, 207f), 26f, 12f, Color(0xFFFFE2A8), 0.55f * moj, r)
            if (!aOscuras(m)) {
                reflejoMojado(Offset(CRUZ.x, 214f), 60f, 20f, CIAN, 0.85f * moj, r); clipPath(charco3()) { reflejoMojado(Offset(CRUZ.x + 8f, 240f), 16f, 26f, CIAN, 0.8f, r, 1f) }
                reflejoMojado(Offset(286f, 203f), 40f, 30f, BLANCO_FRIO, 0.75f * moj * f[F_ESCAPARATE].fuerza, r)
                reflejoMojado(Offset(238f, FONDO + 1f), 94f, 40f, lerp(CIAN, Color.White, 0.45f), (0.7f * moj * (0.3f + 0.7f * anuncios(m))).coerceAtMost(1f), r, 1.3f); reflejoMojado(Offset(222f, FONDO + 1f), 40f, 14f, AMBAR, 0.5f * moj * anuncios(m), r); clipPath(charco2()) { reflejoMojado(Offset(246f, 198f), 14f, 40f, lerp(MAGENTA, Color.White, 0.3f), 0.6f * anuncios(m), r, 1f) }
                reflejoMojado(Offset(270f, FONDO + 1f), 22f, 12f, MAGENTA, 0.4f * moj * anuncios(m), r)
            } else reflejoMojado(Offset(238f, FONDO + 1f), 60f, 34f, Color(0xFF9A9CD0), 0.3f * moj, r)
            if (m.con("faroles")) for (i in 0..4) reflejoMojado(Offset(196f + i * 22f, 204f + i * 8f), 22f, 8f, Color(0xFFFF8A4A), 0.4f * moj, r)
            if (maquinas(m) && !aOscuras(m)) reflejoMojado(Offset(224f, FONDO + 1f), 26f, 10f, AMBAR, 0.5f * moj, r)
        }
        // La rejilla, por donde sube vapor.
        drawOval(a.en(Offset(286f, 232f), 0xFF2A2840), Offset(272f, 228.6f), Size(27f, 7.4f)); drawOval(a.en(Offset(286f, 232f), 0xFF7A7898), Offset(272f, 228.6f), Size(27f, 7.4f), style = Stroke(0.7f))
        for (i in 1..5) drawLine(a.en(Offset(286f, 232f), 0xFF7A7898).copy(alpha = 0.7f), Offset(272f + i * 4.5f, 229.4f + abs(3 - i) * 0.5f), Offset(272f + i * 4.5f, 235.2f - abs(3 - i) * 0.5f), 0.5f)
        // Con la marea, los tablones para pasar.
        if (agua > 0.55f) for ((p, q) in listOf(Offset(120f, 244f) to Offset(196f, 240f), Offset(186f, 238f) to Offset(262f, 228f))) {
            val tabla = poligono(p, q, q + Offset(2f, 5f), p + Offset(2f, 5.4f))
            drawPath(tabla, a.en((p + q) / 2f, 0xFFB09068)); drawLine(a.en((p + q) / 2f, 0xFFE0C8A0), p, q, 0.6f); drawPath(poligono(p + Offset(2f, 5.4f), q + Offset(2f, 5f), q + Offset(2f, 7f), p + Offset(2f, 7.4f)), Color.Black.copy(alpha = 0.4f))
            for (t in listOf(0.12f, 0.88f)) drawRect(a.en(p, OXIDO), p + (q - p) * t + Offset(-3f, 5f), Size(7f, 3f))
        }
    }

    // ---------------------------------------------------------------- La fachada del puesto

    /** El letrero del tazón: el tazón con su borde y su pie, los palillos y el vaho. [alfa] lo atenúa (para su reflejo). */
    private fun DrawScope.letreroTazon(estado: Letrero, alfa: Float = 1f, vaho: Int = 5) {
        val k = when (estado) { Letrero.ENCENDIDO -> 1f; Letrero.REMENDADO -> 0.92f; else -> 0f } * alfa
        val panza = Path().apply { moveTo(59f, 75f); cubicTo(61f, 95f, 107f, 95f, 109f, 75f) }
        val borde = Path().apply { moveTo(55f, 73.5f); lineTo(113f, 73.5f) }
        val pie = Path().apply { moveTo(75f, 93.6f); lineTo(93f, 93.6f) }
        if (alfa < 1f && k <= 0f) return
        tuboNeon(panza, MAGENTA, k, 2f)
        tuboNeon(borde, if (estado == Letrero.REMENDADO) AMBAR else MAGENTA, k, 2f)
        tuboNeon(pie, MAGENTA, k, 1.8f)
        tuboNeon(Path().apply { moveTo(97f, 71f); lineTo(114f, 51f) }, CREMA, k * 0.9f, 1.3f); tuboNeon(Path().apply { moveTo(101.5f, 72f); lineTo(117.5f, 54f) }, CREMA, k * 0.9f, 1.3f)
        for ((i, x) in listOf(70f, 80f, 90f).withIndex()) if (vaho and (1 shl i) != 0) tuboNeon(Path().apply { moveTo(x, 68.6f); cubicTo(x - 5f, 63f, x + 5f, 58.6f, x, 52.6f) }, Color(0xFFFF8AC0), k, 1.2f)
    }

    private fun DrawScope.fachada(m: Momento, l: Luces, a: Alumbrado, f: List<Foco>) {
        val az = Azar(Semilla.de("neon", "fachada")); val p = puesto(m); val le = letrero(m); val oscuras = aOscuras(m)
        val muro = Path().apply { addRect(Rect(-2f, -2f, ESQ, BASE)) }
        planoH(a.sin(F_LETRERO, F_PUESTO), muro, 0f, ESQ, 60f, MURO, 0f, pasos = 6)
        clipPath(muro) {
            // Los paños del muro, los desconchones (asoma el ladrillo), las chorreras y los remiendos.
            for (y in listOf(36f, 100f)) drawLine(Color.Black.copy(alpha = 0.2f), Offset(0f, y), Offset(ESQ, y), 0.7f)
            repeat(7) { val c = Offset(az.entre(4f, ESQ - 6f), az.entre(4f, 100f)); val r = az.entre(4f, 9f); val par = Path().apply { moveTo(c.x - r, c.y); cubicTo(c.x - r * 0.6f, c.y - r * 0.9f, c.x + r * 0.7f, c.y - r * 0.7f, c.x + r, c.y - r * 0.1f); cubicTo(c.x + r * 0.8f, c.y + r * 0.8f, c.x - r * 0.5f, c.y + r * 0.9f, c.x - r, c.y); close() }
                drawPath(par, a.en(c, 0xFF9A6A5C)); clipPath(par) { for (k in -3..3) drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x - r, c.y + k * 2.4f), Offset(c.x + r, c.y + k * 2.4f), 0.4f) }; drawPath(par, Color.Black.copy(alpha = 0.3f), style = Stroke(0.5f)) }
            repeat(12) { val q = Offset(az.entre(0f, ESQ), 39f + az.entre(0f, 4f)); drawLine(Color.Black.copy(alpha = 0.17f), q, q + Offset(az.entre(-1f, 1f), az.entre(12f, 46f)), az.entre(0.6f, 1.8f)) }
            repeat(12) { mancha(Offset(az.entre(0f, ESQ), az.entre(0f, 120f)), az.entre(6f, 16f), az.entre(4f, 12f), if (it % 2 == 0) Color(0xFFB8B0D8) else Color.Black, 0.09f) }
            // La luz del letrero en el muro.
            charcoDeLuz(f[F_LETRERO], LETRERO, 74f, 54f, MURO, 0.85f)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.22f)), 130f, BASE), Offset(0f, 130f), Size(ESQ, BASE - 130f))
            if (p == Puesto.RECOGIDO && !oscuras) charcoDeLuz(f[F_PUESTO], Offset(142f, 132f), 30f, 30f, MURO)
            // El canto de la esquina, que recibe la luz de la farmacia.
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.en(Offset(ESQ, 120f), lerp(MURO, Color.White, 0.3f), 1f).copy(alpha = 0.5f)), ESQ - 5f, ESQ), Offset(ESQ - 5f, 0f), Size(5f, BASE))
        }
        // Las dos ventanas del piso de arriba, con su repisa.
        for ((i, x) in listOf(62f, 116f).withIndex()) {
            val luz = m.azar("ventana puesto", i, (m.hora * 3).toInt() / 6).real() < ventanas(m) * 1.5f && !oscuras
            drawRect(if (luz) (if (i == 0) Color(0xFFFFD79A) else Color(0xFFBFF0FF)) else a.en(Offset(x + 12f, 18f), 0xFF24264A), Offset(x, 6f), Size(25f, 24f))
            if (luz) { drawRect(Color(0xFF7A4A3A).copy(alpha = 0.55f), Offset(x, 6f), Size(9f, 24f)); resplandor(Offset(x + 12f, 18f), 22f, 0.28f, if (i == 0) AMBAR else Color(0xFFBFF0FF)) }
            else drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.Transparent), 6f, 30f), Offset(x, 6f), Size(25f, 24f))
            drawRect(a.en(Offset(x + 12f, 18f), 0xFF9A96B8), Offset(x, 6f), Size(25f, 24f), style = Stroke(1.2f)); drawLine(a.en(Offset(x + 12f, 18f), 0xFF9A96B8), Offset(x + 12.5f, 6f), Offset(x + 12.5f, 30f), 0.8f)
            drawRect(a.en(Offset(x + 12f, 31f), lerp(HORMIGON, Color.White, 0.15f)), Offset(x - 2f, 30f), Size(29f, 2.4f))
            if (i == 1) { drawRoundRect(a.en(Offset(x + 30f, 22f), 0xFFA4AAC0), Offset(x + 27.5f, 14f), Size(13f, 11f), CornerRadius(1f)); for (k in 1..3) drawLine(Color.Black.copy(alpha = 0.28f), Offset(x + 29f, 14f + k * 2.6f), Offset(x + 39f, 14f + k * 2.6f), 0.4f) }
        }
        // La tubería que cruza la fachada y la que baja por la esquina.
        for (i in 0..4) tuberia(a, Offset(-2f + i * 31.6f, 38f), Offset(-2f + (i + 1) * 31.6f, 38f), 3.6f, Color(0xFF6E6890))
        tuberia(a, Offset(ESQ - 6f, 38f), Offset(ESQ - 6f, BASE), 3f, Color(0xFF8A84A8))
        // El tubo del extractor de la cocina: sale sobre el toldo, sube y dobla; la grasa le chorrea.
        val ducto = Path().apply { addRect(Rect(131f, 50f, 143f, 104f)) }
        drawPath(ducto, Brush.horizontalGradient(listOf(a.en(Offset(131f, 76f), 0xFFA6ABC0, -1f), a.en(Offset(137f, 76f), 0xFF5E627C), a.en(Offset(143f, 76f), 0xFF8E94AE, 1f)), 131f, 143f))
        for (y in listOf(62f, 76f, 90f)) drawLine(Color.Black.copy(alpha = 0.3f), Offset(131f, y), Offset(143f, y), 0.6f)
        drawPath(poligono(Offset(129f, 50f), Offset(145f, 50f), Offset(147f, 44f), Offset(127f, 44f)), a.en(Offset(137f, 46f), 0xFF7A7E98)); drawRect(a.en(Offset(137f, 42f), 0xFF34304E), Offset(128f, 41f), Size(18f, 3.4f))
        clipPath(ducto) { for (x in listOf(133f, 138f, 141f)) drawLine(Color(0xFF3A2A22).copy(alpha = 0.4f), Offset(x, 50f), Offset(x + 0.6f, 50f + 20f + (x % 5f) * 6f), 1f) }
        // Restos de carteles viejos, de los que no se despegan nunca.
        for ((q, w, h) in listOf(Triple(Offset(6f, 60f), 12f, 16f), Triple(Offset(20f, 64f), 11f, 14f), Triple(Offset(8f, 80f), 14f, 11f))) { drawRect(a.en(q, lerp(CREMA, MURO, 0.45f)).copy(alpha = 0.7f), q, Size(w, h)); drawRect(a.en(q, NEGRO).copy(alpha = 0.3f), q + Offset(w * 0.15f, h * 0.2f), Size(w * 0.7f, h * 0.25f)); drawPath(poligono(q + Offset(w, h), q + Offset(w - 4f, h), q + Offset(w, h - 5f)), a.en(q, MURO)) }
        // El letrero: la chapa, sus dos tirantes, el cable y los tubos.
        for (x in listOf(60f, 108f)) drawLine(a.en(Offset(x, 42f), 0xFF6A6688), Offset(x, 39f), Offset(x, 46f), 1.4f)
        drawRoundRect(a.en(LETRERO, 0xFF343054), Offset(47f, 44f), Size(74f, 56f), CornerRadius(3f))
        drawRoundRect(a.en(LETRERO, OXIDO), Offset(47f, 44f), Size(74f, 56f), CornerRadius(3f), style = Stroke(1.3f))
        for (q in listOf(Offset(50.5f, 47.5f), Offset(117.5f, 47.5f), Offset(50.5f, 96.5f), Offset(117.5f, 96.5f))) drawCircle(a.en(q, 0xFF9A96B8), 0.8f, q)
        if (f[F_LETRERO].fuerza > 0f) drawRoundRect(Brush.radialGradient(listOf(MAGENTA.copy(alpha = 0.22f), Color.Transparent), Offset(84f, 74f), 40f), Offset(47f, 44f), Size(74f, 56f), CornerRadius(3f))
        cable(Offset(121f, 60f), Offset(ESQ - 6f, 46f), 6f, a.en(Offset(140f, 56f), 0xFF2A2640), 0.8f)
        letreroTazon(le, vaho = 5)
        if (le == Letrero.ROTO) { drawLine(a.en(LETRERO, 0xFF8A86A8), Offset(109f, 75f), Offset(112f, 90f), 1.6f, StrokeCap.Round); cable(Offset(75f, 94f), Offset(70f, 108f), -2f, a.en(LETRERO, 0xFF2A2640), 0.7f) }
        puestoPorDentro(m, a, p)
        // El toldo: listas rojas y crema, descoloridas, con su cenefa.
        val toldo = poligono(Offset(14f, 103f), Offset(152f, 103f), Offset(159f, 124f), Offset(6f, 124f))
        clipPath(toldo) {
            for (i in 0..11) { val x0 = 14f + i * 11.5f; val x1 = 6f + i * 12.75f; val lista = poligono(Offset(x0, 103f), Offset(x0 + 11.5f, 103f), Offset(x1 + 12.75f, 124f), Offset(x1, 124f)); val c = if (i % 2 == 0) TOLDO else CREMA
                drawPath(lista, Brush.verticalGradient(listOf(a.en(Offset(x0, 100f), c), a.en(Offset(x1, 126f), lerp(c, NEGRO, 0.25f))), 103f, 124f)) }
            repeat(7) { mancha(Offset(az.entre(10f, 155f), az.entre(104f, 123f)), az.entre(5f, 12f), az.entre(2f, 5f), Color.Black, 0.14f) }
            if (abierto(m) || p == Puesto.PREPARANDO) drawRect(Brush.verticalGradient(listOf(Color.Transparent, f[F_PUESTO].color.copy(alpha = 0.25f)), 112f, 124f), Offset(0f, 112f), Size(ESQ, 12f), blendMode = BlendMode.Plus)
        }
        drawLine(a.en(Offset(84f, 103f), lerp(HORMIGON, Color.White, 0.2f)), Offset(13f, 103f), Offset(153f, 103f), 1.4f)
        for (i in 0..11) { val x = 6f + i * 12.75f; drawPath(Path().apply { moveTo(x, 124f); cubicTo(x + 1f, 130f, x + 11.75f, 130f, x + 12.75f, 124f); close() }, a.en(Offset(x, 127f), lerp(if (i % 2 == 0) TOLDO else CREMA, NEGRO, 0.3f))) }
        // Los bancos: en fila con el puesto abierto; apilados y con cadena cuando cierra.
        // (Van con las cosas del primer plano, que dan sombra.)
    }

    /** El hueco del puesto: abierto (la cocina), a medio abrir o con la cortina bajada. Y la barra. */
    private fun DrawScope.puestoPorDentro(m: Momento, a: Alumbrado, p: Puesto) {
        val oscuras = aOscuras(m); val az = Azar(Semilla.de("neon", "puesto"))
        val hueco = Rect(22f, 125f, 146f, 173f)
        val cortina = when (p) { Puesto.ABIERTO, Puesto.SOBREMESA -> 0f; Puesto.PREPARANDO -> 0.5f; else -> 1f }
        if (cortina < 1f) {
            // La cocina: la pared del fondo con su luz, la repisa, los tazones, la radio, los cucharones y la olla.
            val luz = if (oscuras) VELA else AMBAR; val fuerte = if (oscuras) 0.55f else 1f
            drawRect(Brush.radialGradient(listOf(lerp(Color(0xFF3A2430), lerp(luz, Color.White, 0.3f), 0.85f * fuerte), lerp(Color(0xFF3A2430), luz, 0.5f * fuerte), Color(0xFF2E1E30)), Offset(92f, 140f), 78f), hueco.topLeft, hueco.size)
            fun dentro(c: Long, k: Float = 1f) = lerp(Color(c), luz, 0.25f * fuerte).let { Color((it.red * (0.45f + 0.55f * fuerte * k)).coerceIn(0f, 1f), (it.green * (0.42f + 0.5f * fuerte * k)).coerceIn(0f, 1f), (it.blue * (0.5f + 0.4f * fuerte * k)).coerceIn(0f, 1f)) }
            // Azulejos del fondo.
            for (x in 0..15) drawLine(Color.Black.copy(alpha = 0.1f), Offset(22f + x * 8f, 125f), Offset(22f + x * 8f, 173f), 0.4f)
            for (y in 0..5) drawLine(Color.Black.copy(alpha = 0.1f), Offset(22f, 125f + y * 8f), Offset(146f, 125f + y * 8f), 0.4f)
            // La repisa: tazones apilados, frascos y la radio.
            drawRect(dentro(0xFF6A4A3A), Offset(60f, 144f), Size(84f, 2f))
            for (i in 0..3) { val x = 66f + i * 9f; for (k in 0..(2 + i % 2)) drawArc(dentro(0xFFEDE8E0), 0f, 180f, true, Offset(x, 138.6f - k * 1.9f), Size(7.6f, 6f)) }
            for ((i, x) in listOf(103f, 108.5f, 114f).withIndex()) { drawRoundRect(dentro(listOf(0xFF8A3A2A, 0xFF3A2A22, 0xFFC8A040)[i]), Offset(x, 135.6f), Size(4f, 8.4f), CornerRadius(1f)); drawRect(dentro(0xFF2A2020), Offset(x + 1f, 133.8f), Size(2f, 2f)) }
            drawRoundRect(dentro(0xFF7A4A3E), Offset(122f, 135f), Size(18f, 9f), CornerRadius(1.4f)); drawCircle(dentro(0xFF2A2020), 2.6f, Offset(127f, 139.5f)); drawRect(dentro(0xFFE2D8C4), Offset(131.5f, 137f), Size(6.4f, 2f)); drawLine(dentro(0xFFA6ABC0), Offset(138f, 135f), Offset(143f, 127.5f), 0.6f)
            // La barra de los cucharones y los cestillos de colar.
            drawLine(dentro(0xFFA6ABC0), Offset(58f, 130f), Offset(100f, 130f), 0.8f)
            for (x in listOf(62f, 68f)) { drawLine(dentro(0xFFC8A040), Offset(x, 130f), Offset(x, 141f), 0.9f); drawOval(dentro(0xFFC8A040), Offset(x - 2.2f, 140f), Size(4.4f, 3.4f)) }
            for (x in listOf(78f, 86f, 94f)) { drawLine(dentro(0xFFA6ABC0), Offset(x, 130f), Offset(x, 135f), 0.6f); drawArc(dentro(0xFFA6ABC0), 0f, 180f, false, Offset(x - 2.6f, 132.6f), Size(5.2f, 6f), style = Stroke(0.7f)) }
            // La bombilla (o, sin corriente, nada: alumbran las velas de la barra).
            if (!oscuras) { drawLine(dentro(0xFF2A2020), Offset(110f, 125f), Offset(110f, 130f), 0.5f); drawCircle(Color(0xFFFFF2CC), 1.8f, Offset(110f, 131.4f)); resplandor(Offset(110f, 132f), 20f, 0.55f, AMBAR) }
            // La olla grande, en su fogón.
            val olla = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(27f, 142f, 57f, 172f, CornerRadius(3f))) }
            drawPath(olla, Brush.horizontalGradient(listOf(dentro(0xFF5A5E78), dentro(0xFFD8DCEA, 1.2f), dentro(0xFF8A8EA6), dentro(0xFF4A4E66)), 27f, 57f))
            drawRoundRect(dentro(0xFFC8CCDC, 1.2f), Offset(25.6f, 140.4f), Size(32.8f, 3.2f), CornerRadius(1.6f)); for (x in listOf(24.4f, 56.4f)) drawRoundRect(dentro(0xFF7A7E96), Offset(x, 147f), Size(3.2f, 5f), CornerRadius(1f))
            for (y in listOf(152f, 160f)) drawLine(Color.Black.copy(alpha = 0.14f), Offset(27f, y), Offset(57f, y), 0.5f)
            if (p == Puesto.ABIERTO) { drawLine(Color(0xFF6AB0FF).copy(alpha = 0.9f), Offset(30f, 172.4f), Offset(54f, 172.4f), 1.1f); mancha(Offset(42f, 136f), 14f, 7f, Color(0xFFFFF0E0), 0.4f) }
            // Con la compra recién traída: cajas en la barra.
            if (p == Puesto.PREPARANDO) for (x in listOf(74f, 100f)) { drawRect(dentro(0xFF9A7A4A), Offset(x, 161f), Size(22f, 12f)); drawRect(dentro(0xFF5CA862), Offset(x + 2f, 158.4f), Size(18f, 3f)) }
        }
        if (cortina > 0f) {
            // La cortina de chapa, con sus lamas; arriba, su caja.
            val abajo = hueco.top + hueco.height * cortina
            val chapa = Path().apply { addRect(Rect(hueco.left, hueco.top, hueco.right, abajo)) }
            planoH(a, chapa, hueco.left, hueco.right, 150f, Color(0xFF7C86A6), 0f, pasos = 5)
            clipPath(chapa) {
                if (mural(m)) {
                    // El mural de Sami: un tazón grande con su vaho sobre olas, a brochazos planos.
                    fun pint(c: Long) = a.en(Offset(84f, 150f), c).copy(alpha = 0.9f)
                    drawRect(pint(0xFF2E5C8A), hueco.topLeft, hueco.size)
                    for (k in 0..3) { val y = 160f + k * 4.4f; val ola = Path().apply { moveTo(22f, y); var x = 22f; while (x < 146f) { cubicTo(x + 5f, y - 4f, x + 11f, y - 4f, x + 16f, y); x += 16f }; lineTo(146f, 176f); lineTo(22f, 176f); close() }; drawPath(ola, pint(if (k % 2 == 0) 0xFF3E8AA8 else 0xFF58B0B8)) }
                    drawPath(Path().apply { moveTo(56f, 142f); cubicTo(58f, 166f, 110f, 166f, 112f, 142f); close() }, pint(0xFFE8E0D0)); drawRect(pint(0xFFE04A8A), Offset(54f, 139.4f), Size(60f, 3.4f))
                    for (x in listOf(70f, 84f, 98f)) drawPath(Path().apply { moveTo(x, 137f); cubicTo(x - 5f, 133f, x + 5f, 130f, x, 126.4f) }, pint(0xFFF2E6C8), style = Stroke(1.8f, cap = StrokeCap.Round))
                    drawCircle(pint(0xFFF2B84A), 7f, Offset(132f, 136f))
                }
                var y = hueco.top + 2f
                while (y < abajo) { drawLine(Color.Black.copy(alpha = 0.26f), Offset(hueco.left, y), Offset(hueco.right, y), 0.6f); drawLine(Color.White.copy(alpha = 0.09f), Offset(hueco.left, y + 1f), Offset(hueco.right, y + 1f), 0.5f); y += 3.2f }
                repeat(6) { mancha(Offset(az.entre(24f, 144f), az.entre(126f, abajo)), az.entre(5f, 14f), az.entre(3f, 8f), if (it % 2 == 0) OXIDO else Color.Black, 0.13f) }
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.34f), Color.Transparent), hueco.top, hueco.top + 9f), hueco.topLeft, Size(hueco.width, 9f))
            }
            drawRect(a.en(Offset(84f, abajo), 0xFF5A6284), Offset(hueco.left, abajo - 2.4f), Size(hueco.width, 2.4f))
            if (cortina >= 1f) { drawRoundRect(a.en(Offset(84f, 168f), 0xFFC8A040), Offset(81f, abajo - 7.4f), Size(6f, 5f), CornerRadius(1f)); drawArc(a.en(Offset(84f, 164f), 0xFFA6ABC0), 180f, 180f, false, Offset(82f, abajo - 10.4f), Size(4f, 5f), style = Stroke(0.8f)) }
        }
        // El marco del hueco y la caja de la cortina.
        drawRect(a.en(Offset(84f, 125f), 0xFF4A4668), Offset(18f, 123.6f), Size(132f, 3.4f))
        for (x in listOf(18f, 146f)) drawRect(a.en(Offset(x, 150f), lerp(HORMIGON, MURO, 0.5f)), Offset(x, 125f), Size(4f, 49f))
        // La barra: la encimera de acero y el frente de chapa esmaltada, con sus golpes.
        val frente = Path().apply { addRect(Rect(16f, 177f, 150f, BASE)) }
        planoH(a, frente, 16f, 150f, 198f, ESMALTE, 0f, pasos = 6)
        clipPath(frente) {
            for (x in listOf(60.6f, 105.2f)) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, 177f), Offset(x, BASE), 0.8f); drawLine(Color.White.copy(alpha = 0.1f), Offset(x + 1f, 177f), Offset(x + 1f, BASE), 0.5f) }
            repeat(10) { mancha(Offset(az.entre(18f, 148f), az.entre(180f, BASE)), az.entre(3f, 9f), az.entre(2f, 6f), if (it % 3 == 0) OXIDO else Color.Black, 0.16f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)), 200f, BASE), Offset(16f, 200f), Size(134f, 14f))
            // La carta, sin letra: tres tazones pintados.
            for (i in 0..2) { val c = Offset(72f + i * 11f, 190f); drawArc(a.en(c, CREMA).copy(alpha = 0.75f), 0f, 180f, true, c + Offset(-4f, -4f), Size(8f, 7f)); drawLine(a.en(c, CREMA).copy(alpha = 0.6f), c + Offset(-4.6f, -0.6f), c + Offset(4.6f, -0.6f), 0.6f) }
        }
        val enc = Path().apply { addRect(Rect(12f, 172.6f, 154f, 177.4f)) }
        planoH(a, enc, 12f, 154f, 174f, lerp(ACERO, Color.White, 0.2f), 0f, pasos = 6)
        drawLine(Color.White.copy(alpha = 0.3f), Offset(12f, 173f), Offset(154f, 173f), 0.6f); drawRect(Color.Black.copy(alpha = 0.3f), Offset(14f, 177.4f), Size(138f, 1.6f))
        if (abierto(m)) {
            // Sobre la barra: el bote de los palillos, la salsa, una pila de tazones y, en el apagón, velas.
            val s = Offset(84f, 170f)
            drawRoundRect(a.en(s, 0xFFB09068), Offset(72f, 164.4f), Size(6f, 8.4f), CornerRadius(1f)); for (k in 0..3) drawLine(a.en(s, CREMA), Offset(73f + k * 1.3f, 165f), Offset(72.4f + k * 1.6f, 158.6f), 0.5f)
            drawRoundRect(a.en(s, 0xFF4A2A22), Offset(82f, 165.4f), Size(4.4f, 7.4f), CornerRadius(1f)); drawRect(a.en(s, ROJO), Offset(83f, 163.4f), Size(2.4f, 2.2f))
            for (k in 0..3) drawArc(a.en(Offset(128f, 168f), 0xFFEDE8E0), 0f, 180f, true, Offset(122f, 166.4f - k * 2.2f), Size(12f, 8f))
            if (p == Puesto.SOBREMESA) for (x in listOf(40f, 58f, 100f)) { drawArc(a.en(Offset(x, 170f), 0xFFEDE8E0), 0f, 180f, true, Offset(x - 5.5f, 167f), Size(11f, 8f)); drawLine(a.en(Offset(x, 168f), CREMA), Offset(x - 3f, 170.6f), Offset(x + 6f, 166.6f), 0.5f) }
            if (oscuras) for (x in listOf(48f, 96f, 140f)) { drawRect(a.en(Offset(x, 168f), CREMA), Offset(x - 1.1f, 167f), Size(2.2f, 5.6f)); resplandor(Offset(x, 164.6f), 12f, 0.6f, VELA) }
        }
        // El trapo, colgado del canto de la barra.
        drawPath(poligono(Offset(132f, 177.4f), Offset(143f, 177.4f), Offset(142f, 191f), Offset(137f, 188.6f), Offset(133f, 192f)), a.en(Offset(138f, 184f), 0xFFB89A6A))
    }

    // ---------------------------------------------------------------- El poste

    private fun DrawScope.poste(m: Momento, l: Luces, a: Alumbrado) {
        val oscuras = aOscuras(m)
        // La base de hormigón y el fuste.
        drawPath(poligono(Offset(POSTE - 7f, 241f), Offset(POSTE + 7f, 241f), Offset(POSTE + 5f, 232f), Offset(POSTE - 5f, 232f)), a.en(Offset(POSTE, 238f), HORMIGON))
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(POSTE - 9f, 239f), Size(18f, 4.4f))
        val fuste = Path().apply { addRect(Rect(POSTE - 3.8f, -4f, POSTE + 3.8f, 234f)) }
        drawPath(fuste, Brush.horizontalGradient(listOf(a.en(Offset(POSTE - 3f, 120f), 0xFF8A86A6, -1f), a.en(Offset(POSTE, 120f), 0xFF4A4668), a.en(Offset(POSTE + 3f, 120f), 0xFF8A86A6, 1f)), POSTE - 3.8f, POSTE + 3.8f))
        clipPath(fuste) { drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.en(Offset(POSTE, 220f), 0xFFB0A8C8).copy(alpha = 0.25f)), 120f, 234f), Offset(POSTE - 4f, 120f), Size(8f, 114f)) }
        for (y in listOf(60f, 110f, 196f)) drawRect(a.en(Offset(POSTE, y), 0xFF3A3658), Offset(POSTE - 4.6f, y), Size(9.2f, 2.4f))
        // Los peldaños, el transformador y las cajas de los contadores.
        for (i in 0..5) drawLine(a.en(Offset(POSTE, 128f + i * 9f), 0xFF8A86A6), Offset(POSTE - 7f, 128f + i * 9f), Offset(POSTE + 7f, 128f + i * 9f), 0.9f)
        val trafo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(POSTE - 15f, 24f, POSTE - 3f, 50f, CornerRadius(2.4f))) }
        cuerpo(a, trafo, POSTE - 15f, POSTE - 3f, 37f, Color(0xFF7A7E9C)); for (y in listOf(30f, 37f, 44f)) drawLine(Color.Black.copy(alpha = 0.25f), Offset(POSTE - 15f, y), Offset(POSTE - 3f, y), 0.5f)
        drawLine(a.en(Offset(POSTE - 9f, 22f), 0xFFE2D8C4), Offset(POSTE - 12f, 24f), Offset(POSTE - 12f, 19f), 1.2f); drawLine(a.en(Offset(POSTE - 9f, 22f), 0xFFE2D8C4), Offset(POSTE - 6f, 24f), Offset(POSTE - 6f, 19f), 1.2f)
        for ((y, c) in listOf(148f to 0xFF8A90AA, 163f to 0xFF7A8098)) { drawRoundRect(a.en(Offset(POSTE + 8f, y + 6f), c, 1f), Offset(POSTE + 3.4f, y), Size(11f, 12f), CornerRadius(1f)); drawRect(a.en(Offset(POSTE + 8f, y + 6f), 0xFF2A2C48), Offset(POSTE + 5.4f, y + 2f), Size(7f, 4f)); drawCircle(Color(0xFF7CFFB8).copy(alpha = if (oscuras) 0.15f else 0.9f), 0.7f, Offset(POSTE + 12f, y + 9f)) }
        // El brazo del farol y su pantalla.
        drawPath(Path().apply { moveTo(POSTE + 2f, 100f); cubicTo(POSTE + 8f, 84f, FAROL.x - 10f, 82f, FAROL.x, 85.6f) }, a.en(Offset(POSTE + 12f, 88f), 0xFF6A6688), style = Stroke(1.6f, cap = StrokeCap.Round))
        drawPath(poligono(FAROL + Offset(-7.5f, -1f), FAROL + Offset(7.5f, -1f), FAROL + Offset(4.6f, -6.4f), FAROL + Offset(-4.6f, -6.4f)), a.en(FAROL + Offset(0f, -8f), 0xFF5A5878))
        if (!oscuras) { drawOval(Color(0xFFFFF0C8), FAROL + Offset(-5f, -1.6f), Size(10f, 3.6f)); resplandor(FAROL + Offset(0f, 2f), 26f, 0.7f, AMBAR) }
        else drawOval(a.en(FAROL, 0xFF8A86A6), FAROL + Offset(-5f, -1.6f), Size(10f, 3.6f))
        // Las pegatinas del poste y, en la huelga, las cintas rojas.
        for ((i, y) in listOf(182f, 204f, 214f).withIndex()) drawRect(a.en(Offset(POSTE, y), listOf(0xFFE8D25A, 0xFF8AC8D8, 0xFFE2D8C4)[i]).copy(alpha = if (m.arco("pintas") > 0.05f || i == 2) 0.85f else 0f), Offset(POSTE - 2.6f, y), Size(5.2f, 6f))
        if (m.con("huelga")) for (y in listOf(120f, 174f)) { drawRect(a.en(Offset(POSTE, y), ROJO), Offset(POSTE - 3.2f, y), Size(6.4f, 2.6f)); drawLine(a.en(Offset(POSTE, y), ROJO), Offset(POSTE + 3f, y + 1f), Offset(POSTE + 9f, y + 9f), 1.4f, StrokeCap.Round); drawLine(a.en(Offset(POSTE, y), ROJO), Offset(POSTE + 3f, y + 2f), Offset(POSTE + 5f, y + 12f), 1.4f, StrokeCap.Round) }
    }

    // ---------------------------------------------------------------- El primer plano

    private fun DrawScope.primerPlano(m: Momento, l: Luces, a: Alumbrado, f: List<Foco>) {
        val cosas = cosas(m, a).sortedBy { it.pie.y }
        val tumban = tumban(f)
        if (tumban.isNotEmpty()) {
            // Las sombras: cada cosa la tumba el foco que más la alumbra, lejos de él.
            capaSombra(rayoDeFoco(tumban[0], Offset(200f, 270f), 0.86f, SOMBRA)) { c ->
                clipPath(sueloDelCallejon()) { cosas.forEach { k -> elQueManda(tumban, k.pie)?.let { foco -> sombraArrojada(rayoDeFoco(foco, k.pie, 1f, SOMBRA), k.forma, k.pie, c, FONDO, FONDO) } } }
            }
        }
        // El asiento de cada cosa: la sombra pegada al suelo, que está siempre, o el cerco del agua.
        val agua = m.arco("agua")
        cosas.forEach { k -> val b = k.forma.getBounds(); drawOval(Color.Black.copy(alpha = 0.3f), Offset(b.left - 1f, k.pie.y - 2.2f), Size(b.width + 2f, 4.6f)) }
        cosas.forEach { it.pintar(this) }
        if (agua > 0.3f) cosas.forEach { k -> val b = k.forma.getBounds(); drawOval(Color(0xFF10163A).copy(alpha = 0.5f), Offset(b.left - 2f, k.pie.y - 2.6f * agua - 1f), Size(b.width + 4f, 5f * agua + 2f)); drawArc(Color(0xFFC0CCF4).copy(alpha = 0.4f), 0f, 180f, false, Offset(b.left - 3f, k.pie.y - 2f), Size(b.width + 6f, 3.4f), style = Stroke(0.5f)) }
        // Lo que no da sombra: las velas y los retratos de luz de la Noche de los Ausentes, las pancartas de la huelga.
        if (m.con("velas")) ausentes(a)
        if (m.con("huelga")) pancartas(a)
        if (aOscuras(m)) { val v = Offset(222f, 245.4f); drawRect(a.en(v, CREMA), v + Offset(-1.4f, -7f), Size(2.8f, 7f)); drawOval(a.en(v, 0xFF8A86A6), v + Offset(-3.4f, -1f), Size(6.8f, 2.4f)); resplandor(v + Offset(0f, -10f), 16f, 0.7f, VELA) }
        if (bici(m) == Bici.ENCHUFADA) {
            // El cable que le tiende el cocinero: sale de debajo de la barra y cruza el suelo hasta la bici.
            val cab = Path().apply { moveTo(150f, 212f); cubicTo(170f, 232f, 150f, 250f, 196f, 262f); cubicTo(240f, 280f, 262f, 262f, BICI.x + 9f, BICI.y - 35f) }
            drawPath(cab, Color.Black.copy(alpha = 0.35f), style = Stroke(2f)); drawPath(cab, a.en(Offset(200f, 256f), 0xFFE8843A), style = Stroke(1.1f, cap = StrokeCap.Round))
            drawCircle(Color(0xFF7CFFB8), 1.2f, BICI + Offset(9f, -36f)); resplandor(BICI + Offset(9f, -36f), 7f, 0.5f, CIAN)
        }
    }

    private fun cosas(m: Momento, a: Alumbrado): List<Cosa> = buildList {
        val dondeBici = bici(m); val p = puesto(m)
        with(CosasNeon) {
        add(grande(bombona(a), 1.24f)); add(grande(cajas(m, a), 1.25f))
        if (p == Puesto.RECOGIDO || p == Puesto.PREPARANDO) add(grande(cubo(a), 1.3f))
        if (abierto(m)) {
            // Seis bancos rojos delante de la barra; el de la punta cojea. Cada día quedan un poco distintos.
            val az = m.azar("bancos")
            for (i in 0..5) add(banco(a, Offset(30f + i * 22f + az.entre(-3f, 3f), 232f + az.entre(-3f, 4f) + (if (i == 5) 3f else 0f)), 1.22f, if (i == 5) 5f else az.entre(-2f, 2f)))
        } else { add(pilaDeBancos(a, Offset(108f, 231f), 1.22f)); add(pilaDeBancos(a, Offset(131f, 235f), 1.22f)) }
        if (m.con("cosecha")) add(grande(verdura(a), 1.3f))
        add(grande(cajon(m, a), 1.32f))
        if (m.ficha.objeto == "banco") add(banco(a, Offset(252f, 281f), 1.5f, 7f))
        add(grande(bolsas(a, hayBolsas(m)), 1.3f)); add(grande(tomaDeAgua(a), 1.4f))
        if (dondeBici == Bici.ESPERANDO || dondeBici == Bici.ENCHUFADA) add(grande(bici(a, BICI, soldada(m), dondeBici == Bici.ESPERANDO), 1.18f))
        }
    }

    private fun DrawScope.ausentes(a: Alumbrado) {
        // Retratos de luz, sin cara: marcos que alumbran, apoyados en la barra y en el suelo, cada uno con su vela.
        for ((i, q) in listOf(Offset(40f, 208f), Offset(66f, 210f), Offset(96f, 208f), Offset(126f, 211f), Offset(164f, 236f), Offset(232f, 268f)).withIndex()) {
            val w = 9f + (i % 2) * 2f; val h = 12f + (i % 3)
            drawRect(a.en(q, 0xFF2A2640), q + Offset(-w / 2f - 1f, -h - 1f), Size(w + 2f, h + 2f)); drawRect(Brush.verticalGradient(listOf(Color(0xFFE8F6FF), Color(0xFFA8C8F0)), q.y - h, q.y), q + Offset(-w / 2f, -h), Size(w, h))
            drawOval(Color(0xFF7A98C8).copy(alpha = 0.6f), q + Offset(-w * 0.2f, -h * 0.78f), Size(w * 0.4f, h * 0.34f)); drawArc(Color(0xFF7A98C8).copy(alpha = 0.6f), 180f, 180f, true, q + Offset(-w * 0.36f, -h * 0.44f), Size(w * 0.72f, h * 0.7f))
            resplandor(q + Offset(0f, -h / 2f), 13f, 0.4f, Color(0xFFBFE0FF))
            val v = q + Offset(w / 2f + 4f, 3f); drawRect(a.en(v, CREMA), v + Offset(-1.1f, -5f), Size(2.2f, 5f)); resplandor(v + Offset(0f, -7f), 10f, 0.6f, VELA)
        }
    }

    private fun DrawScope.pancartas(a: Alumbrado) {
        // Las pancartas de la huelga, apoyadas: un cartón con un aro de rueda pintado, sin letra.
        for ((q, giro) in listOf(Offset(158f, 240f) to -9f, Offset(338f, 262f) to 7f)) {
            val arriba = q + Offset(giro, -52f)
            drawLine(a.en(q, 0xFFB09068), q, arriba, 1.6f, StrokeCap.Round)
            val c = arriba + Offset(giro * 0.2f, 10f)
            drawPath(poligono(c + Offset(-15f, -12f), c + Offset(15f, -13f), c + Offset(16f, 11f), c + Offset(-14f, 12f)), a.en(c, CREMA))
            drawCircle(a.en(c, ROJO), 7f, c, style = Stroke(1.8f)); for (k in 0..3) { val an = (PI * k / 4).toFloat(); drawLine(a.en(c, ROJO), c - Offset(kotlin.math.cos(an), sin(an)) * 6.6f, c + Offset(kotlin.math.cos(an), sin(an)) * 6.6f, 0.7f) }
        }
    }

    // ---------------------------------------------------------------- Lo que enmarca: cables, alero, luces y faroles

    private fun DrawScope.marco(m: Momento, l: Luces, a: Alumbrado) {
        val tinta = l.tono(Color(0xFF1E1A34), 0.1f)
        // Los cables: del poste a la azotea, a la fachada y fuera del cuadro.
        val tendidos = listOf(Triple(Offset(POSTE, 12f), Offset(340f, 6f), 20f), Triple(Offset(POSTE, 18f), Offset(ANCHO + 6f, 34f), 26f), Triple(Offset(-4f, 50f), Offset(POSTE, 24f), 9f), Triple(Offset(POSTE, 30f), Offset(ANCHO + 6f, 70f), 34f), Triple(Offset(96f, -4f), Offset(POSTE, 10f), 5f))
        tendidos.forEachIndexed { i, (p, q, c) -> cable(p, q, c, tinta, if (i == 3) 1f else 0.7f) }
        // Una caja de empalmes y un par de zapatillas colgadas, como en todo callejón.
        val pts = cuerda(Offset(POSTE, 30f), Offset(ANCHO + 6f, 70f), 34f, 16)
        drawRect(tinta, pts[6] + Offset(-3f, -1f), Size(6f, 4.4f))
        drawLine(tinta, pts[10], pts[10] + Offset(0f, 9f), 0.5f); for (dx in listOf(-2.4f, 2f)) drawRoundRect(a.en(pts[10] + Offset(0f, 10f), 0xFFD8D0E0), pts[10] + Offset(dx - 1.6f, 8.6f), Size(3.4f, 5.4f), CornerRadius(1.4f))
        // Las ristras de luces de fin de año: más cuantas más diga el arco.
        val cuantas = m.arco("luces"); val colores = listOf(AMBAR, CIAN, MAGENTA, Color(0xFFFFF2CC))
        val ristras = (1 + cuantas * 4.4f).toInt().coerceAtMost(tendidos.size)
        if (cuantas > 0.05f && !aOscuras(m)) for (i in 0 until ristras) { val (p, q, c) = tendidos[i]; val pp = cuerda(p, q, c, 26); for (k in 1 until pp.size) { val col = colores[(k + i) % 4]; drawCircle(col, 1.1f, pp[k] + Offset(0f, 1.6f)); resplandor(pp[k] + Offset(0f, 1.6f), 5.5f, 0.42f, col) } }
        // Los faroles de papel de la Noche de los Faroles.
        if (m.con("faroles")) {
            val pp = cuerda(Offset(POSTE, 44f), Offset(FARM + 4f, 56f), 18f, 12); cable(Offset(POSTE, 44f), Offset(FARM + 4f, 56f), 18f, tinta, 0.6f)
            for ((i, k) in listOf(1, 3, 5, 7, 9, 11).withIndex()) { val c = pp[k] + Offset(0f, 9f); val rad = 6.4f + (i % 2) * 1.6f; val col = if (i % 2 == 0) Color(0xFFFF7A4A) else Color(0xFFFFC46A)
                drawLine(tinta, pp[k], c + Offset(0f, -rad), 0.5f); drawOval(Brush.radialGradient(listOf(lerp(col, Color.White, 0.6f), col, lerp(col, Color(0xFF7A2A2A), 0.4f)), c, rad), c + Offset(-rad * 0.82f, -rad), Size(rad * 1.64f, rad * 2f))
                for (j in -1..1) drawArc(Color(0xFF7A2A2A).copy(alpha = 0.35f), 90f, 180f, false, c + Offset(j * rad * 0.42f - rad * 0.3f, -rad), Size(rad * 0.6f, rad * 2f), style = Stroke(0.4f))
                drawRect(Color(0xFF5A2A2A), c + Offset(-rad * 0.3f, -rad - 1f), Size(rad * 0.6f, 1.6f)); drawRect(Color(0xFF5A2A2A), c + Offset(-rad * 0.3f, rad - 0.6f), Size(rad * 0.6f, 1.6f)); drawLine(Color(0xFFFFC46A), c + Offset(0f, rad + 1f), c + Offset(0f, rad + 5f), 0.6f)
                resplandor(c, rad * 3f, 0.5f, col) }
            for (i in 0..4) { val c = Offset(24f + i * 29f, 136f); val col = if (i % 2 == 0) Color(0xFFFFC46A) else Color(0xFFFF7A4A); drawLine(tinta, c + Offset(0f, -10f), c + Offset(0f, -5f), 0.4f); drawOval(Brush.radialGradient(listOf(lerp(col, Color.White, 0.6f), col), c, 5f), c + Offset(-4f, -5f), Size(8f, 10f)); drawRect(Color(0xFF5A2A2A), c + Offset(-1.6f, -6f), Size(3.2f, 1.2f)); drawLine(Color(0xFFFFC46A), c + Offset(0f, 5f), c + Offset(0f, 8.4f), 0.5f); resplandor(c, 13f, 0.45f, col) }
        }
        if (m.con("fiesta")) {
            banderines(l, Offset(POSTE, 58f), Offset(FARM, 64f), 14f, listOf(Color(0xFFFF6AA8), Color(0xFF6AE8D8), Color(0xFFFFD06A), Color(0xFFE8E0F0)).map { lerp(it, Color.White, 0.2f) }, 8f)
            banderines(l, Offset(10f, 100f), Offset(ESQ, 96f), 6f, listOf(Color(0xFFFF6AA8), Color(0xFF6AE8D8), Color(0xFFFFD06A)).map { lerp(it, Color.White, 0.2f) }, 6f)
        }
        // El alero de este lado del callejón, arriba a la derecha: chapa ondulada, con su canalón, sus goteras y lo que
        // cuelga de él. Es lo que cierra el cuadro por arriba, como una rama.
        val alero = poligono(listOf(Offset(258f, -3f), Offset(ANCHO + 3f, -3f), Offset(ANCHO + 3f, 50f), Offset(344f, 35f), Offset(296f, 14f)))
        drawPath(alero, Brush.verticalGradient(listOf(l.tono(Color(0xFF1E1A36)), a.en(Offset(350f, 44f), 0xFF4A4468)), -3f, 50f))
        clipPath(alero) {
            for (i in 0..15) drawLine(Color.Black.copy(alpha = 0.3f), Offset(256f + i * 8.6f, -3f), Offset(262f + i * 9.4f, 52f), 0.9f)
            for (i in 0..15) drawLine(Color.White.copy(alpha = 0.05f), Offset(259f + i * 8.6f, -3f), Offset(265f + i * 9.4f, 52f), 0.7f)
            val az = Azar(Semilla.de("neon", "alero")); repeat(6) { mancha(Offset(az.entre(280f, ANCHO), az.entre(4f, 40f)), az.entre(6f, 14f), az.entre(3f, 7f), Color(0xFF9A5A3E), 0.16f) }
        }
        val canto = Path().apply { moveTo(258f, -3f); lineTo(296f, 14f); lineTo(344f, 35f); lineTo(ANCHO + 3f, 50f) }
        drawPath(canto, l.tono(Color(0xFF14122A)), style = Stroke(3.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(canto, a.en(Offset(330f, 34f), 0xFFB0A8C8).copy(alpha = 0.7f), style = Stroke(0.7f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Un cable enrollado, una bombilla vieja que ya no luce y las goteras.
        drawLine(tinta, Offset(318f, 25f), Offset(318f, 41f), 0.5f); drawOval(a.en(Offset(318f, 45f), 0xFF8A86A6), Offset(315.6f, 41f), Size(4.8f, 6.4f)); drawRect(tinta, Offset(316.6f, 39.4f), Size(2.8f, 2.4f))
        drawOval(tinta, Offset(352f, 41f), Size(9f, 12f), style = Stroke(1.1f)); drawOval(tinta, Offset(353.4f, 43f), Size(6.6f, 9f), style = Stroke(0.7f))
        for ((x, y, h) in listOf(Triple(304f, 19f, 5f), Triple(336f, 33f, 7f), Triple(370f, 45f, 5f))) drawLine(Color(0xFFC8D0F8).copy(alpha = 0.35f), Offset(x, y), Offset(x, y + h), 0.5f, StrokeCap.Round)
    }

    /** La niebla en un callejón: borra la ciudad y la boca, vela lo de en medio y deja jirones a ras de suelo. */
    private fun DrawScope.niebla(m: Momento, l: Luces) {
        val c = lerp(l.bruma, Color(0xFF9088B8), 0.45f); val az = m.azar("niebla")
        drawRect(Brush.verticalGradient(0f to c.copy(alpha = 0.5f), 0.7f to c.copy(alpha = 0.66f), 1f to c.copy(alpha = 0.3f), startY = 0f, endY = FONDO + 30f), Offset(ESQ, 0f), Size(ANCHO - ESQ, FONDO + 30f))
        drawRect(Brush.verticalGradient(listOf(c.copy(alpha = 0.2f), c.copy(alpha = 0.1f)), 0f, ALTO), Offset.Zero, Size(ANCHO, ALTO))
        repeat(7) { mancha(Offset(az.entre(-20f, ANCHO + 20f), az.entre(212f, 262f)), az.entre(50f, 110f), az.entre(5f, 11f), lerp(c, Color.White, 0.25f), 0.34f) }
    }

    /** La bruma alrededor de cada luz; con niebla, más. */
    private fun DrawScope.brumas(m: Momento, l: Luces, f: List<Foco>) {
        val k = when (m.ficha.clima) { Clima.NIEBLA -> 2.2f; Clima.LLUVIA, Clima.TORMENTA -> 1.4f; Clima.POLVO -> 1.6f; else -> 1f }
        haloDeBruma(LETRERO, 60f, 44f, MAGENTA, 0.2f * k * f[F_LETRERO].fuerza)
        if (!aOscuras(m)) { haloDeBruma(CRUZ, 30f, 28f, CIAN, 0.3f * k); haloDeBruma(FAROL + Offset(0f, 16f), 30f, 46f, AMBAR, 0.2f * k) }
        if (f[F_PUESTO].fuerza > 0.5f) haloDeBruma(Offset(84f, 150f), 80f, 40f, f[F_PUESTO].color, 0.1f * k)
        // El fondo del callejón se pierde: una veladura de bruma entre el primer plano y la boca.
        drawRect(Brush.verticalGradient(listOf(l.bruma.copy(alpha = 0.26f * k.coerceAtMost(1.6f)), Color.Transparent), FONDO - 60f, FONDO + 26f), Offset(BOCA_I - 4f, FONDO - 60f), Size(BOCA_D - BOCA_I + 8f, 86f))
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el vapor de la rejilla y los reflejos no paran

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m); val le = letrero(m); val oscuras = aOscuras(m); val moj = mojado(m)
        // El letrero parpadea «bien»: el vaho del tazón se enciende por turnos. Remendado, el tubo nuevo tiembla.
        if (le == Letrero.ENCENDIDO || le == Letrero.REMENDADO) {
            val turno = ((reloj * 1.6f).toInt() % 3 + 3) % 3
            val x = listOf(70f, 80f, 90f)[turno]
            if (turno == 1) tuboNeon(Path().apply { moveTo(x, 68.6f); cubicTo(x - 5f, 63f, x + 5f, 58.6f, x, 52.6f) }, Color(0xFFFF8AC0), 1f, 1.2f)
            if (le == Letrero.REMENDADO && sin(reloj * 23f) * sin(reloj * 7.3f) > 0.35f) drawLine(Color(0xFF2A2440).copy(alpha = 0.7f), Offset(55f, 73.5f), Offset(113f, 73.5f), 2.4f, StrokeCap.Round)
            reflejo(Offset(84f, BASE + 8f), 40f, MAGENTA, 0.3f * moj, reloj, 1)
        }
        if (!oscuras) {
            // La cruz late despacio, y su reflejo tiembla.
            resplandor(CRUZ, 20f, 0.2f + 0.16f * sin(reloj * 1.4f), CIAN)
            reflejo(Offset(CRUZ.x, 220f), 30f, CIAN, 0.34f * moj, reloj, 2)
            reflejo(Offset(FAROL.x, 248f), 22f, AMBAR, 0.26f * moj, reloj, 3)
        }
        // El vapor: el de la olla con el puesto abierto, el del tubo de la azotea y el de la rejilla.
        if (puesto(m) == Puesto.ABIERTO) humo(l, Offset(42f, 140f), 40f, 16f, reloj, 0.15f, 0.02f, 0f, 1, 9, 0.75f)
        val ty = FARM_ALTO + (FARM_FONDO - FARM_ALTO) * 0.42f
        humo(l, Offset(312f, ty - 18f), 34f, 12f, reloj, -0.25f, 0.05f, 0.25f, 2, 8, 0.6f)
        humo(l, Offset(286f, 231f), 30f, 14f, reloj, 0.1f, 0.04f, 0f, 3, 8, 0.5f)
        // Las llamas: las velas del apagón y de los Ausentes.
        if (oscuras && abierto(m)) for ((i, x) in listOf(48f, 96f, 140f).withIndex()) llama(Offset(x, 167f), 3.6f, reloj, i)
        if (oscuras) llama(Offset(222f, 238.4f), 4.4f, reloj, 7)
        if (m.con("velas")) for ((i, q) in listOf(Offset(48.5f, 206f), Offset(75.5f, 208f), Offset(104.5f, 206f), Offset(135.5f, 209f), Offset(173.5f, 234f), Offset(240.5f, 266f)).withIndex()) llama(q, 3.4f, reloj, i + 3)
        // La baliza de la máquina da vueltas; el foco rojo del toque barre la boca.
        if (maquinas(m) && !oscuras) resplandor(Offset(224f, 144f), 26f, 0.3f + 0.3f * sin(reloj * 4f), AMBAR)
        if (m.con("toque")) resplandor(Offset(238f + 30f * sin(reloj * 0.7f), 176f), 30f, 0.34f, Color(0xFFFF3A3A))
        // El faro de la bici que espera.
        if (bici(m) == Bici.ESPERANDO) resplandor(BICI + Offset(-32f, -51f), 15f, 0.5f + 0.06f * sin(reloj * 9f), Color(0xFFEFF4FF))
        // La lluvia: las gotas, los aros en los charcos y lo que escurre del toldo. Con tormenta, el fogonazo.
        if (llueve(m)) {
            val fuerte = m.ficha.clima == Clima.TORMENTA
            val az = Azar(Semilla.de(m.ficha.semilla, "aros"))
            repeat(if (fuerte) 16 else 9) { val c = Offset(az.entre(10f, ANCHO - 10f), az.entre(FONDO + 12f, ALTO - 4f)); val fase = ((reloj * az.entre(0.7f, 1.3f) + az.real()) % 1f + 1f) % 1f; val k = (c.y - FONDO) / 95f
                drawOval(Color(0xFFC8D0F8).copy(alpha = 0.42f * (1f - fase)), c + Offset(-7f * fase * (0.5f + k), -2f * fase * (0.5f + k)), Size(14f * fase * (0.5f + k), 4f * fase * (0.5f + k)), style = Stroke(0.5f)) }
            for (i in 0..11) { val x = 6f + i * 12.75f + 6f; val fase = ((reloj * 1.7f + i * 0.37f) % 1f + 1f) % 1f; drawLine(Color(0xFFC8D0F8).copy(alpha = 0.5f), Offset(x, 129f + 80f * fase * fase), Offset(x, 133f + 80f * fase * fase), 0.6f) }
            if (fuerte) { val cada = 6.5f; val dentro = reloj / cada - kotlin.math.floor(reloj / cada); if (dentro < 0.018f || (dentro > 0.035f && dentro < 0.05f)) { drawRect(Color.White.copy(alpha = 0.18f), Offset.Zero, Size(ANCHO, ALTO)); drawRect(Color(0xFFE8ECFF).copy(alpha = 0.5f), Offset(ESQ, 0f), Size(ANCHO - ESQ, 60f)) } }
            lluvia(l, reloj, m.ficha.semilla, if (fuerte) 1f else 0.55f, m.viento)
        } else tiempoVivo(l, m.ficha.clima, reloj, 60f, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    override fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 20f, 280f, hasta = 330f))
        add(Ancla(TipoAncla.MIRADOR, 64f, 236f))                              // encima de las cajas
        add(Ancla(TipoAncla.OBJETO, 236f, 281f))
        add(Ancla(TipoAncla.ABRIGO, 100f, 224f))                              // bajo el toldo
        if (puesto(m) == Puesto.ABIERTO) add(Ancla(TipoAncla.FUEGO, 44f, 226f))   // al calor de la olla
        if (fiesta(m) || m.con("velas")) add(Ancla(TipoAncla.ADORNO, 152f, 244f))
    }
}
