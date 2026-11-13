package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Franja
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** La paleta de Espacio, de 18 colores: el casco crema y gris azulado, el naranja de lo que se agarra, el verde de lo que crece, cinco luces y la sombra. */
internal object ColorEspacio {
    val CASCO = Color(0xFFCFCBC0)
    val CASCO_OSC = Color(0xFF7C8296)
    val ACERO = Color(0xFF9AA3B4)
    val GOMA = Color(0xFF2E3246)
    val NARANJA = Color(0xFFE8843C)
    val TELA = Color(0xFFE2D8C4)
    val TELA_AZUL = Color(0xFF4E6E9C)
    val ROJO = Color(0xFFD24A3C)
    val VERDE = Color(0xFF5E9A52)
    val VERDE_OSC = Color(0xFF2E5A3A)
    val ALGA = Color(0xFF7CC850)
    val TRIGO = Color(0xFFD8B45A)
    val CIAN = Color(0xFF58D8D0)
    val AMBAR = Color(0xFFFFB25E)
    val LUZ_BLANCA = Color(0xFFF4F6FF)
    val LUZ_CULTIVO = Color(0xFFFFA8D8)
    val GUARDIA = Color(0xFF6A8CE8)
    val SOMBRA = Color(0xFF2A2E62)
}

/**
 * Espacio: el puesto de la ingeniera de vuelo en el anillo de la nave que va a Alfa Centauri (años 15 a 19 del viaje).
 * Todo es ficción. Es el primer **interior con dos luces** (`docs/diseno/ESCENAS.md`, sección 5): no hay cielo ni sol.
 *
 * - **Al fondo:** el mamparo, con la compuerta abierta. Por ella se ve la galería de la granja, que sigue la curva del
 *   anillo y por eso sube hasta perderse por arriba. A su derecha, la pared de consolas y el armario del traje de salida.
 * - **A la derecha:** el costado de proa, acolchado, con la ventana pequeña: por ella se ve una galaxia, que gira
 *   despacio porque el anillo gira. Debajo, la red de carga y el banco.
 * - **A la izquierda:** las bandejas del cultivo, con sus lámparas rosadas.
 * - **Delante:** la torre de cultivo, el cajón de carga, la mesa con el objeto del día, el taburete y la columna de algas.
 * - **Arriba:** el techo, con sus tres lámparas, el conducto del aire y los cables; y la barra que va del suelo al techo.
 *
 * **La luz [Diseño propio]**, que sigue lo que dice el cuento («las luces suben de azul a blanco» y bajan «de blanco a
 * naranja y de naranja a azul»): las tres lámparas del techo hacen de sol. Por la mañana manda la de la izquierda,
 * ámbar, y la mitad derecha del módulo queda en penumbra azul; a mediodía, la del centro, blanca y pareja; por la tarde,
 * la de la derecha, que acaba naranja. Así las sombras cambian de lado y de largo con la hora, aunque no haya sol. A las 21:00 queda el último naranja y ya están las luces azules de
 * guardia; a las 22:00, solo las de guardia, las pantallas, las lámparas del cultivo y lo que brilla ([programa]).
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario del calendario en `AnioG7.kt`):
 * - **El cultivo** ([cultivo], arco `granja`): las plantas de las bandejas, de la torre y de la galería crecen todo el
 *   año; el trigo amarillea; con la cosecha (del 20 al 23 de agosto) quedan los rastrojos y hay sacos y gavillas.
 * - **La parada del reactor** ([parada], estado `parada`, diez días de octubre): media luz naranja todo el día, las
 *   pantallas apagadas, las lámparas del cultivo al mínimo y un calefactor encendido.
 * - **Sin peso** ([sinPeso], estado `ingravidez`, tres semanas de la temporada 3): el anillo no gira, las cosas flotan
 *   sujetas a sus cuerdas, los cables no cuelgan y la galaxia de la ventana se queda quieta.
 * - **La alarma** (clave `alarma`): las balizas rojas y las pantallas en rojo. **Fuera** (clave `fuera`): el armario
 *   del traje está vacío y en la pantalla grande se ve la cámara del casco. **Fiesta, luto y cosecha**, con lo suyo.
 * - **El viaje** (arco `viaje`): la pantalla de la ruta, con la nave un poco más cerca de la estrella de delante.
 * - **Un día suyo:** a sus horas de trabajo, las pantallas vivas, la caja de herramientas abierta y un registro
 *   destapado; a las de comer, la bandeja; con gente, las tazas y el tablero; en su rato de la ventana, el cojín y la
 *   lámpara; cuando duerme, todo recogido.
 * - **El objeto del día**, sobre la mesa.
 */
internal object EscenaEspacio : EscenaVida {
    override val vidas = listOf("astronauta")

    private val C = ColorEspacio

    /** Lo de fuera de la nave: no es un color más de la paleta, es su SOMBRA llevada casi a negro. */
    internal val FUERA = lerp(C.SOMBRA, Color(0xFF000008), 0.8f)

    // ---------------------------------------------------------------- El lugar
    /** A dónde se van las líneas del módulo. */
    val FUGA = Offset(170f, 126f)
    /** El mamparo del fondo. */
    const val FONDO_I = 60f
    const val FONDO_D = 288f
    const val TECHO = 50f
    const val SUELO = 206f
    /** Cuánto se acerca lo que está en el borde de abajo del cuadro (el mamparo es 1). */
    const val CERCA = 1.9875f

    /** Un punto del módulo: [wx] y [wy], como si estuviera en el mamparo del fondo; [s], cuánto más cerca (1 = en el mamparo). */
    fun p(wx: Float, wy: Float, s: Float) = Offset(FUGA.x + (wx - FUGA.x) * s, FUGA.y + (wy - FUGA.y) * s)

    /** El paso [i] de [n] en profundidad, de [s0] a [s1], con el espaciado de la perspectiva. */
    fun fondo(i: Int, n: Int, s0: Float, s1: Float): Float { val k = i / n.toFloat(); return 1f / (1f / s0 + (1f / s1 - 1f / s0) * k) }

    val LAMPARA_IZQ = Offset(66f, 25f)
    val LAMPARA_CEN = Offset(170f, 18f)
    val LAMPARA_DER = Offset(318f, 22f)
    val VENTANA = Offset(337.6f, 83.4f)
    const val VENTANA_RX = 12.5f
    const val VENTANA_RY = 19f
    val BALIZA = Offset(120f, 84f)
    val MESA = Offset(228f, 267f)
    /** Cuánto se agranda la mesa, con todo lo que tiene encima. */
    const val K_MESA = 1.25f
    val FLEXO = Offset(MESA.x + 36f, MESA.y - 64f)
    val CALEFACTOR = Offset(316f, 276f)
    /** Dónde cae la luz de las lámparas del techo: el suelo que tienen debajo. Con eso las sombras se tumban hacia quien mira. */
    private const val BAJO_LAS_LAMPARAS = 232f
    /** Dónde pisa el gato. */
    const val PISO_GATO = 281f

    // ---------------------------------------------------------------- Lo que el cuento mueve

    /** La parada del reactor: diez días de octubre, con media luz. */
    internal fun parada(m: Momento) = m.estado == "parada"

    /** Sin peso: las tres semanas de la temporada 3 con el anillo parado. */
    internal fun sinPeso(m: Momento) = m.estado == "ingravidez"

    /** Si el anillo gira: entonces lo que se ve por la ventana da vueltas. */
    internal fun giraElAnillo(m: Momento) = !sinPeso(m)

    internal fun alarma(m: Momento) = m.con("alarma")
    internal fun fuera(m: Momento) = m.con("fuera")
    internal fun fiesta(m: Momento) = m.con("fiesta")
    internal fun luto(m: Momento) = m.con("luto")
    internal fun cosecha(m: Momento) = m.con("cosecha")

    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    /** La fiesta de las luces (21 de diciembre): una lámpara por cada uno que se quedó en la Tierra. */
    internal fun lamparitas(m: Momento) = hoyEs(m, "luces")

    /** El aniversario de la partida: al bajar la luz se apaga todo y se busca el Sol por las ventanas. */
    internal fun aOscuras(m: Momento) = hoyEs(m, "partida") && m.luz.franja == Franja.TENUE

    /** Cómo va el cultivo, de 0 a 1 (el arco `granja`). */
    internal fun cultivo(m: Momento): Float = m.arco("granja").coerceIn(0f, 1f)

    /** Si el trigo ya amarillea: las últimas semanas antes de la cosecha. */
    internal fun trigoMaduro(m: Momento) = cultivo(m) >= 0.8f

    /** Qué parte del camino lleva hecha la nave (el arco `viaje`). */
    internal fun viaje(m: Momento): Float = m.arco("viaje").coerceIn(0f, 1f)

    /** El traje de salida está en su armario, salvo los días en que se sale al casco. */
    internal fun trajeEnSuSitio(m: Momento) = !fuera(m)

    internal fun trabajando(m: Momento) = m.hacer == Hacer.TRABAJO
    internal fun comiendo(m: Momento) = m.hacer == Hacer.COMER
    internal fun conGente(m: Momento) = m.hacer == Hacer.GENTE
    internal fun durmiendo(m: Momento) = m.hacer == Hacer.DORMIR
    /** Su rato de la ventana, al bajar la luz. */
    internal fun enLaVentana(m: Momento) = m.hacer == Hacer.LIBRE && m.hora >= 20.0
    /** Su rato de ejercicio, después de comer. */
    internal fun ejercicio(m: Momento) = m.hacer == Hacer.LIBRE && m.hora < 20.0

    /** El calefactor: solo en la parada, que es cuando hace frío; apagado mientras duerme. */
    internal fun calefactor(m: Momento) = parada(m) && !durmiendo(m)

    // ---------------------------------------------------------------- La luz

    /**
     * Cuánto da cada lámpara a una hora: la de la izquierda, la del centro y la de la derecha (de 0 a 1), cuánto de
     * naranja tiene la de la derecha y cuánto lucen las luces azules de guardia.
     */
    internal class Programa(val izq: Float, val centro: Float, val der: Float, val naranja: Float, val guardia: Float) {
        /** Cuánta luz hay en total, de 0 (la de guardia) a 1 (el día entero). */
        val nivel: Float get() = ((izq * 0.5f + centro * 0.6f + der * 0.5f) / 0.75f).coerceIn(0f, 1f)
    }

    private val HORAS = listOf(
        5f to Programa(0f, 0f, 0f, 0f, 1f), 6f to Programa(0.8f, 0.16f, 0f, 0f, 0f), 8.5f to Programa(0.95f, 0.22f, 0.04f, 0f, 0f), 10.5f to Programa(0.86f, 0.34f, 0.08f, 0f, 0f),
        13f to Programa(0.36f, 0.95f, 0.36f, 0f, 0f), 17.5f to Programa(0.06f, 0.3f, 0.92f, 0.35f, 0f), 20f to Programa(0.06f, 0.28f, 0.82f, 0.6f, 0f),
        21f to Programa(0f, 0f, 0.6f, 1f, 0.7f), 22f to Programa(0f, 0f, 0f, 1f, 1f), 29f to Programa(0f, 0f, 0f, 0f, 1f),
    )

    /** El programa de luces de a bordo a la hora del momento. En la parada no hay más que la luz de emergencia; la noche de la partida, nada. */
    internal fun programa(m: Momento): Programa {
        if (parada(m)) return Programa(0f, 0f, 0f, 1f, if (m.luz.franja == Franja.TENUE) 0.6f else 0.4f)
        if (aOscuras(m)) return Programa(0f, 0f, 0f, 0f, 0.2f)
        val h = (if (m.hora < 5.0) m.hora + 24.0 else m.hora).toFloat()
        val i = HORAS.indexOfLast { it.first <= h }.coerceIn(0, HORAS.size - 2)
        val (h0, a) = HORAS[i]; val (h1, b) = HORAS[i + 1]; val k = ((h - h0) / (h1 - h0)).coerceIn(0f, 1f)
        fun f(x: Float, y: Float) = x + (y - x) * k
        return Programa(f(a.izq, b.izq), f(a.centro, b.centro), f(a.der, b.der), f(a.naranja, b.naranja), f(a.guardia, b.guardia))
    }

    /** La lámpara que manda ahora: la que da la dirección de la luz y de las sombras. */
    internal enum class Manda { IZQUIERDA, CENTRO, DERECHA, GUARDIA }

    internal fun manda(m: Momento): Manda { val g = programa(m); return when { parada(m) || g.nivel < 0.12f -> Manda.GUARDIA; g.izq >= g.centro && g.izq >= g.der -> Manda.IZQUIERDA; g.der > g.centro -> Manda.DERECHA; else -> Manda.CENTRO } }

    /** La luz de emergencia de la parada: ámbar, baja, todo el día. */
    internal fun emergencia(m: Momento): Float = if (parada(m)) (if (m.luz.franja == Franja.TENUE) 0.3f else 0.5f) else 0f

    /** Cuánto lucen las pantallas de las consolas, de 0 a 1. */
    internal fun pantallas(m: Momento): Float = when {
        aOscuras(m) -> 0f
        parada(m) -> 0.1f
        trabajando(m) || alarma(m) -> 1f
        durmiendo(m) -> 0.3f
        m.luz.franja == Franja.TENUE -> 0.6f
        else -> 0.8f
    }

    /** Cuánto lucen las lámparas del cultivo: de día, del todo; de noche descansan; en la parada, al mínimo. */
    internal fun luzDeCultivo(m: Momento): Float = when {
        aOscuras(m) -> 0f
        parada(m) -> 0.2f
        else -> 0.3f + 0.7f * programa(m).nivel
    }

    /** La lámpara de la mesa: encendida en su rato de la ventana y cuando hay gente ya con la luz baja. */
    internal fun flexo(m: Momento) = !aOscuras(m) && !sinPeso(m) && (enLaVentana(m) || (conGente(m) && programa(m).nivel < 0.6f) || (parada(m) && trabajando(m)))

    /**
     * De qué lado viene la luz de las lámparas del techo: de −1 (toda de la izquierda, por la mañana) a 1 (toda de la
     * derecha, por la tarde y con el último naranja). A mediodía y de noche, 0.
     */
    internal fun deLado(m: Momento): Float { val g = programa(m); val t = g.izq + g.centro + g.der; return if (t < 0.05f) 0f else ((g.der - g.izq) / t).coerceIn(-1f, 1f) }

    /** Cuánto día hay, de 0 a 1. Los matices de la mañana, del mediodía y de la tarde solo valen con las lámparas altas: desde el último naranja de las 21:00, nada. */
    private fun pleno(g: Programa) = ((g.nivel - 0.45f) / 0.25f).coerceIn(0f, 1f)

    /** La lámpara de la izquierda: azul al alba, ámbar toda la mañana y blanca desde el mediodía. */
    private fun colorIzq(m: Momento): Color {
        val h = if (m.hora < 5.0) 30f else m.hora.toFloat()
        val alba = lerp(Color(0xFFB4C8FF), Color(0xFFFFBE7E), ((h - 5f) / 1.6f).coerceIn(0f, 1f))
        return lerp(alba, C.LUZ_BLANCA, ((h - 10.2f) / 2.4f).coerceIn(0f, 1f))
    }
    /** La de la derecha: blanca a mediodía, crema por la tarde y naranja al final. */
    private fun colorDer(g: Programa) = lerp(lerp(C.LUZ_BLANCA, Color(0xFFFFE6C2), (g.naranja * 5f).coerceIn(0f, 1f)), Color(0xFFFF9648), g.naranja)

    /** La luz de fondo del módulo: la que rebota en las paredes. De noche, azul; en la parada, parda. */
    internal fun luces(m: Momento, ambienteFijo: Color? = null): Luces {
        val g = programa(m); val n = g.nivel
        val calido = if (parada(m)) 0.7f else g.naranja * (1f - g.guardia) * (if (n < 0.6f) 1f else 0.4f)
        // De día: a mediodía, un blanco frío y parejo; con la luz de lado (mañana y tarde), un fondo más bajo y más azul, para que lo que no alcanza la lámpara quede en penumbra.
        val lado = abs(deLado(m)); val pl = pleno(g)
        val deDia = lerp(lerp(Color(0xFF4E5264), Color(0xFF5C6678), pl * (1f - lado)), Color(0xFF3E476C), 0.7f * pl * lado)
        val ambiente = lerp(lerp(Color(0xFF262B48), deDia, n), Color(0xFF4A3830), 0.5f * calido * (1f - n))
        val bruma = lerp(lerp(Color(0xFF1E2440), Color(0xFF8A90A0), n), Color(0xFF503A30), 0.4f * calido * (1f - n))
        return Luces(
            alto = bruma, medio = bruma, bajo = bruma, ambiente = ambienteFijo ?: if (aOscuras(m)) Color(0xFF1C2240) else ambiente, bruma = bruma, brumaFuerza = 0.8f,
            solX = 0.5f, solAltura = -1f, solColor = Color.White, solFuerza = 0f, lunaX = 0.5f, lunaAltura = -1f,
            estrellas = 0f, noche = (1f - n * 1.4f).coerceIn(0f, 1f), nublado = 0f, eclipse = 0f,
        )
    }

    /** La luz que hay en el punto [en], para los adornos de la biblioteca, que piden unas `Luces` y no un [Alumbrado]. */
    internal fun luzDeAdorno(m: Momento, a: Alumbrado, en: Offset): Luces = luces(m, a.en(en, Color(0xFFE6E6E6)))

    /** Los focos de ahora. Los tres primeros son siempre las lámparas del techo (con fuerza 0 si no lucen). */
    internal fun focos(m: Momento): List<Foco> = buildList {
        val g = programa(m); val dia = g.nivel; val noche = 1f - dia
        val lado = deLado(m); val pl = pleno(g)
        add(Foco(LAMPARA_IZQ, colorIzq(m), 0.9f * g.izq, 128f - 22f * pl * abs(lado), BAJO_LAS_LAMPARAS))
        add(Foco(LAMPARA_CEN, C.LUZ_BLANCA, 0.86f * g.centro, 128f + 24f * pl * (1f - abs(lado)), BAJO_LAS_LAMPARAS))
        add(Foco(LAMPARA_DER, colorDer(g), 0.9f * g.der, 128f - 22f * pl * abs(lado), BAJO_LAS_LAMPARAS))
        // Donde da de lleno la lámpara que manda de lado: el costado de su lado, la esquina del mamparo y el suelo cercano. (El cuarto y el quinto foco son siempre estos dos.)
        add(Foco(Offset(52f, 176f), colorIzq(m), 0.72f * pl * (-lado).coerceAtLeast(0f) * g.izq, 112f, BAJO_LAS_LAMPARAS))
        add(Foco(Offset(340f, 186f), colorDer(g), 0.5f * pl * lado.coerceAtLeast(0f) * g.der, 112f, BAJO_LAS_LAMPARAS))
        val e = emergencia(m)
        if (e > 0f) { add(Foco(Offset(70f, 72f), C.AMBAR, 0.8f * e, 120f, 214f)); add(Foco(Offset(300f, 60f), C.AMBAR, 0.8f * e, 120f, 226f)) }
        // Las luces de guardia, a ras de suelo.
        if (g.guardia > 0.02f) for (q in listOf(Offset(14f, 238f), Offset(176f, 202f), Offset(352f, 246f))) add(Foco(q, C.GUARDIA, 0.42f * g.guardia, 78f, q.y + 30f))
        // Las pantallas, las lámparas del cultivo, la galería de la granja, las algas y la ventana: de día casi no se notan.
        val pan = pantallas(m)
        if (pan > 0.05f) add(Foco(Offset(204f, 100f), if (alarma(m)) lerp(C.CIAN, C.ROJO, 0.7f) else C.CIAN, pan * (0.16f + 0.5f * noche), 64f, SUELO))
        val cul = luzDeCultivo(m)
        add(Foco(Offset(26f, 150f), C.LUZ_CULTIVO, cul * (0.14f + 0.36f * noche), 62f, 236f))
        add(Foco(Offset(120f, 168f), lerp(C.LUZ_CULTIVO, C.LUZ_BLANCA, 0.4f), cul * (0.1f + 0.34f * noche), 60f, SUELO))
        if (!aOscuras(m)) add(Foco(Offset(356f, 214f), C.ALGA, 0.1f + 0.3f * noche, 52f, 281f))
        add(Foco(VENTANA, Color(0xFFBFD0FF), if (aOscuras(m)) 0.62f else 0.12f * noche, if (aOscuras(m)) 150f else 60f, 250f))
        if (alarma(m)) add(Foco(BALIZA + Offset(0f, 10f), C.ROJO, 0.5f, 100f, SUELO))
        if (flexo(m)) add(Foco(FLEXO, Color(0xFFFFD9A0), 0.6f, 56f, MESA.y - 30f))
        if (calefactor(m)) add(Foco(CALEFACTOR + Offset(0f, -16f), Color(0xFFFF8A4A), 0.55f, 56f, CALEFACTOR.y - 8f))
        if (lamparitas(m)) add(Foco(Offset(190f, 232f), Color(0xFFFFCF8A), 0.42f, 130f, 276f))
        else if (fiesta(m)) add(Foco(Offset(200f, 60f), Color(0xFFFFD9A0), 0.22f, 120f, 276f))
    }

    /** Los focos que tumban sombras: las tres lámparas del techo, las de emergencia, la pantalla grande, la lámpara de la mesa y el calefactor. */
    private fun tumban(f: List<Foco>): List<Foco> = f.filterIndexed { i, it -> i !in 3..4 && it.fuerza > 0.16f && it.color != C.GUARDIA && it.color != C.ALGA && (it.p != VENTANA || it.fuerza > 0.5f) && it.p.y < 240f }

    private fun esDelTecho(f: Foco) = f.p == LAMPARA_IZQ || f.p == LAMPARA_CEN || f.p == LAMPARA_DER

    /**
     * El rayo con que el foco [f] tumba la sombra de lo que pisa en [pie]. Para las lámparas del techo vale la misma
     * **[Aproximación]** que `Rayo(luces)` hace con el sol: la luz entra de lado y un poco desde delante, así que la
     * sombra cae hacia el lado contrario a la lámpara y hacia el fondo, y sube por el mamparo si lo alcanza. Con la
     * lámpara del centro son cortas; con las de los lados, largas. Las demás luces (una pantalla, la lámpara de la
     * mesa, el calefactor) tumban la suya con `rayoDeFoco`.
     */
    private fun rayoDe(f: Foco, pie: Offset, fuerza: Float): Rayo {
        if (!esDelTecho(f)) return rayoDeFoco(f, pie, fuerza, C.SOMBRA)
        val centro = f.p == LAMPARA_CEN
        val ax = ((pie.x - f.p.x) / (if (centro) 300f else 150f)).coerceIn(-0.95f, 0.95f)
        return Rayo(dia = 1f, lat = if (ax >= 0f) -1f else 1f, cot = abs(ax), tan = 1f / abs(ax).coerceAtLeast(0.05f), ax = ax, ay = if (centro) -0.2f else -0.34f, fuerza = fuerza.coerceIn(0f, 1f), color = C.SOMBRA, contraste = 0.8f)
    }

    private fun elQueManda(tumban: List<Foco>, pie: Offset): Foco? = tumban.maxByOrNull { val d = it.p - Offset(pie.x, pie.y - 30f); it.fuerza / (1f + (d.x * d.x + d.y * d.y) / (it.alcance * it.alcance)) }

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val f = focos(m); val a = Alumbrado(l, f)
        with(ModuloEspacio) {
            techo(m, a)
            mamparo(m, a)
            costadoIzquierdo(m, a)
            costadoDerecho(m, a)
            suelo(m, a)
        }
        // Lejos de la lámpara que manda, el módulo queda en penumbra: así se ve de qué lado viene la luz.
        val peso = f.take(3).sumOf { it.fuerza.toDouble() }.toFloat()
        if (peso > 0.1f) {
            val xc = f.take(3).sumOf { (it.p.x * it.fuerza).toDouble() }.toFloat() / peso
            val lado = deLado(m); val pl = pleno(programa(m))
            // A mediodía la luz es pareja y la penumbra de los rincones, menor.
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.42f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.34f * programa(m).nivel * (1f - 0.55f * pl * (1f - abs(lado)))), center = Offset(xc + (xc - FUGA.x) * 0.5f, 60f), radius = 330f), Offset.Zero, Size(ANCHO, ALTO))
            // Con la luz de lado, la mitad contraria del módulo queda en una penumbra azul.
            val cuanta = 0.4f * pl * abs(lado)
            if (cuanta > 0.01f) {
                if (lado < 0f) drawRect(Brush.horizontalGradient(0f to Color.Transparent, 0.55f to C.SOMBRA.copy(alpha = cuanta * 0.7f), 1f to C.SOMBRA.copy(alpha = cuanta), startX = 172f, endX = ANCHO), Offset(172f, 0f), Size(ANCHO - 172f, ALTO))
                else drawRect(Brush.horizontalGradient(0f to C.SOMBRA.copy(alpha = cuanta), 0.45f to C.SOMBRA.copy(alpha = cuanta * 0.7f), 1f to Color.Transparent, startX = 0f, endX = 208f), Offset.Zero, Size(208f, ALTO))
            }
        }
        // La claridad que baja de cada lámpara encendida: un cono de aire alumbrado.
        for (q in f.take(3)) if (q.fuerza > 0.2f) { mancha(Offset(q.p.x + (q.p.x - FUGA.x) * 0.1f, 110f), 54f, 100f, q.color, 0.07f * q.fuerza); mancha(Offset(q.p.x, 44f), 40f, 26f, q.color, 0.1f * q.fuerza) }
        primerPlano(m, a, f)
        with(ModuloEspacio) { marco(m, a) }
        if (sinPeso(m)) with(CosasEspacio) { flotando(m, a) }
        // El aire del módulo: un velo muy leve hacia el fondo y, con poca luz, el halo de cada cosa encendida.
        val g = programa(m)
        if (g.guardia > 0.3f || parada(m)) for (q in f.drop(3)) if (q.fuerza > 0.2f) mancha(q.p, q.alcance * 0.5f, q.alcance * 0.36f, q.color, 0.05f)
        grano(Azar(Semilla.de("espacio", "grano")), 0.55f + 0.3f * g.nivel)
    }

    private fun DrawScope.primerPlano(m: Momento, a: Alumbrado, f: List<Foco>) {
        val cosas = (with(CosasEspacio) { cosas(m, a) } + ModuloEspacio.barraCosa(m, a)).sortedBy { it.pie.y }
        val tumban = tumban(f)
        if (tumban.isNotEmpty()) {
            // Las sombras: a cada cosa se la tumba la lámpara que más la alumbra, lejos de ella. Sin peso, las cosas siguen sujetas y dan sombra igual.
            capaSombra(rayoDeFoco(tumban[0], Offset(190f, 270f), 0.74f, C.SOMBRA)) { c ->
                val rayos = cosas.map { k -> elQueManda(tumban, k.pie)?.let { foco -> rayoDe(foco, k.pie, 1f) } }
                // En el suelo.
                clipPath(ModuloEspacio.formaDelSuelo()) { cosas.forEachIndexed { i, k -> rayos[i]?.let { r -> sombraArrojada(r, k.forma, k.pie, c, SUELO, SUELO) } } }
                // Subidas al mamparo, más tenues: lo que de la sombra pasa del pie de la pared cuando manda una lámpara de lado (de lo que hay en el cuadro, solo la barra llega).
                clipRect(FONDO_I, TECHO, FONDO_D, SUELO) {
                    cosas.forEachIndexed { i, k -> rayos[i]?.let { r -> val separa = k.pie.y - SUELO; if (r.ay < -0.3f && separa > 0f) translate(r.ax * separa / -r.ay, separa / -r.ay - separa) { sombra(k.forma, c.copy(alpha = 0.62f)) } } }
                }
                // Los cables colgantes dejan la suya en el mamparo y en los costados, hacia el lado contrario a la lámpara y más abajo.
                val lado = deLado(m); val delTecho = tumban.filter { esDelTecho(it) }.maxOfOrNull { it.fuerza } ?: 0f
                if (delTecho > 0.16f) clipPath(ModuloEspacio.formaDeLasParedes()) {
                    translate(-lado * 15f, 9f + 4f * abs(lado)) {
                        for (pts in ModuloEspacio.cables(m)) drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y) }, c.copy(alpha = 0.5f + 0.4f * abs(lado)), style = Stroke(2.2f, cap = StrokeCap.Round))
                    }
                }
            }
        }
        // El asiento de cada cosa: la sombra pegada al suelo, que está siempre.
        cosas.forEach { k -> val b = k.forma.getBounds(); drawOval(C.SOMBRA.copy(alpha = 0.36f), Offset(b.left - 1f, k.pie.y - 2.6f), Size(b.width + 2f, 5.4f)) }
        cosas.forEach { it.pintar(this) }
        with(CosasEspacio) { sueltas(m, a) }
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // la ventana, las pantallas y las algas no paran

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        cieloDeLaVentana(m, reloj)
        val pan = pantallas(m)
        // La pantalla grande: el trazo que barre; las lucecitas de la consola, que laten.
        if (pan > 0.2f && !fuera(m)) {
            val x = 173f + ((reloj * 9f) % 38f + 38f) % 38f
            drawLine((if (alarma(m)) C.ROJO else C.CIAN).copy(alpha = 0.5f * pan), Offset(x, 79f), Offset(x, 109f), 0.6f)
        }
        if (!aOscuras(m)) for (i in 0..5) { val late = 0.5f + 0.5f * sin(reloj * (1.1f + i * 0.37f) + i * 1.9f); drawCircle(listOf(C.CIAN, C.AMBAR, C.ALGA)[i % 3].copy(alpha = (0.25f + 0.6f * late) * (0.3f + 0.7f * pan)), 0.8f, Offset(173f + i * 5.2f, 65f)) }
        // Las balizas de la alarma dan vueltas.
        if (alarma(m)) {
            val late = 0.5f + 0.5f * sin(reloj * 5f)
            resplandor(BALIZA, 22f + 8f * late, 0.35f + 0.4f * late, C.ROJO)
            resplandor(Offset(352f, 40f), 16f, 0.3f + 0.3f * (1f - late), C.ROJO)
        }
        // Las burbujas de la columna de algas.
        if (!aOscuras(m)) {
            val az = Azar(Semilla.de("espacio", "burbujas"))
            repeat(9) {
                val x = 347f + az.entre(0f, 18f); val fase = ((reloj * az.entre(0.12f, 0.24f) + az.real()) % 1f + 1f) % 1f
                val y = if (sinPeso(m)) 176f + 90f * az.real() + 2f * sin(reloj * 0.6f + it) else 270f - 100f * fase
                drawCircle(Color.White.copy(alpha = 0.34f), az.entre(0.6f, 1.5f), Offset(x + 1.4f * sin(reloj * 1.3f + it), y), style = Stroke(0.35f))
            }
        }
        // El vaho de la comida y el calor del calefactor.
        if (comiendo(m) && !sinPeso(m)) humo(l, MESA + Offset(-58f, -62f), 16f, 6f, reloj, 0.05f, 0f, 0f, 3, 6, 0.5f)
        if (calefactor(m)) resplandor(CALEFACTOR + Offset(0f, -17f), 16f + 1.5f * sin(reloj * 2.3f), 0.42f, Color(0xFFFF7A3A))
        // Las lamparitas de la fiesta de las luces titilan; sin peso, unas gotas de agua van a la deriva.
        if (lamparitas(m)) with(CosasEspacio) { lamparitasVivas(reloj) }
        if (sinPeso(m)) {
            val az = Azar(Semilla.de(m.ficha.semilla, "gotas"))
            repeat(7) {
                val c = Offset(az.entre(70f, 320f) + 6f * sin(reloj * 0.21f + it * 1.7f), az.entre(120f, 236f) + 4f * cos(reloj * 0.17f + it))
                val rad = az.entre(1f, 2.4f)
                drawCircle(Color(0xFFBFE0F0).copy(alpha = 0.4f), rad, c); drawCircle(Color.White.copy(alpha = 0.7f), rad * 0.3f, c + Offset(-rad * 0.3f, -rad * 0.3f))
            }
        }
    }

    /** Lo que se ve por la ventana: el vacío, las estrellas, la banda de la Vía Láctea y una galaxia. Gira despacio con el anillo; sin peso, se queda quieto. */
    private fun DrawScope.cieloDeLaVentana(m: Momento, reloj: Float) {
        val vidrio = Path().apply { addOval(Rect(VENTANA.x - VENTANA_RX + 2.2f, VENTANA.y - VENTANA_RY + 2.6f, VENTANA.x + VENTANA_RX - 2.2f, VENTANA.y + VENTANA_RY - 2.6f)) }
        val giro = if (giraElAnillo(m)) reloj * 2.4f + (m.hora * 15.0).toFloat() else 24f
        clipPath(vidrio) {
            drawRect(FUERA, VENTANA - Offset(VENTANA_RX, VENTANA_RY), Size(VENTANA_RX * 2f, VENTANA_RY * 2f))
            rotate(giro, VENTANA) {
                // La Vía Láctea: una banda de polvo blanco de lado a lado.
                rotate(-28f, VENTANA) { mancha(VENTANA + Offset(0f, 9f), 34f, 5.4f, Color(0xFFAEB8E0), 0.3f); mancha(VENTANA + Offset(-6f, 10f), 16f, 2.6f, Color(0xFFF0E6D8), 0.26f) }
                val az = Azar(Semilla.de("espacio", "estrellas"))
                repeat(46) {
                    val q = VENTANA + Offset(az.entre(-24f, 24f), az.entre(-24f, 24f)); val k = az.real()
                    drawCircle(listOf(Color.White, Color(0xFFFFE2B0), Color(0xFFB8D0FF), Color(0xFFFFC0A8))[it % 4].copy(alpha = 0.5f + 0.5f * k), 0.28f + 0.5f * k * k, q)
                }
                // La galaxia: el halo, el disco ladeado con sus dos brazos y el núcleo.
                val c = VENTANA + Offset(-1.5f, -5f)
                rotate(-34f, c) {
                    mancha(c, 11.5f, 5f, Color(0xFF8A9AE8), 0.5f)
                    mancha(c, 8f, 3f, Color(0xFFD8C8F0), 0.6f)
                    for (lado in listOf(1f, -1f)) drawPath(Path().apply { moveTo(c.x + lado * 1.6f, c.y - lado * 0.6f); cubicTo(c.x + lado * 5f, c.y - lado * 3f, c.x + lado * 9.6f, c.y - lado * 1.6f, c.x + lado * 10.4f, c.y + lado * 1.4f) }, Color(0xFFDCE4FF).copy(alpha = 0.55f), style = Stroke(0.9f, cap = StrokeCap.Round))
                    mancha(c, 3f, 1.8f, Color(0xFFFFF0D0), 0.95f); drawCircle(Color.White, 0.8f, c)
                }
                // La estrella de delante: cada año un poco más gorda.
                val brillo = ((viaje(m) - 0.34f) / 0.11f).coerceIn(0f, 1f)
                val e = VENTANA + Offset(5.5f, 9.5f)
                resplandor(e, 3.4f + 2.4f * brillo, 0.7f, Color(0xFFFFE2A8)); drawCircle(Color(0xFFFFF4D8), 0.7f + 0.5f * brillo, e)
            }
            // El reflejo del vidrio grueso.
            drawPath(Path().apply { moveTo(VENTANA.x - 7f, VENTANA.y - 12f); cubicTo(VENTANA.x - 9.4f, VENTANA.y - 5f, VENTANA.x - 9.4f, VENTANA.y + 3f, VENTANA.x - 7.6f, VENTANA.y + 9f) }, Color.White.copy(alpha = 0.16f), style = Stroke(1.3f, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- El gato

    /**
     * Al gato lo alumbran las lámparas del módulo: toma el color de las que tiene cerca y su sombra la tumba la que
     * más lo alumbra. **[Diseño propio]**: sin peso, el gato sigue en la alfombra, que es de las que agarran: es el
     * único sitio donde sus uñas lo sujetan.
     */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val f = focos(m); val l = luces(m); val a = Alumbrado(l, f)
        val manda = elQueManda(tumban(f), en)
        val rayo = if (manda != null) rayoDe(manda, en, 0.75f) else rayoFijo(0.4f, 60f, 0f, C.SOMBRA, 0.3f)
        val centro = en + Offset(0f, -12f)
        // El brillo del lomo: el color de la lámpara del techo que manda; el de una pantalla o del calefactor, muy rebajado, para que no le tiña la cabeza.
        val brillo = when { manda == null -> Color(0xFFC8D0F0); esDelTecho(manda) -> manda.color; else -> lerp(manda.color, Color(0xFFC8D0F0), 0.75f) }
        return LuzGato(rayo, l.noche, brillo) { lerp(l.tono(it), a.en(centro, it), 0.8f) }
    }

    override fun estorbos(m: Momento): List<Float> = listOf(30f, 104f, 150f, MESA.x, CALEFACTOR.x)

    override fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 24f, PISO_GATO, hasta = 336f))
        add(Ancla(TipoAncla.MIRADOR, 104f, 220.6f))                             // encima del cajón de carga
        add(Ancla(TipoAncla.OBJETO, MESA.x - 8f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 64f, PISO_GATO))                          // al pie de la torre de cultivo
        if (calefactor(m)) add(Ancla(TipoAncla.FUEGO, CALEFACTOR.x - 36f, PISO_GATO, hasta = CALEFACTOR.x))
        if (fiesta(m)) add(Ancla(TipoAncla.ADORNO, 164f, 246f))
    }
}
