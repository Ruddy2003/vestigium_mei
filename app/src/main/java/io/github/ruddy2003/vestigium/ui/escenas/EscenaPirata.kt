package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.sin

/** La paleta de Pirata: madera lavada, lona, brea, el rojo de la amurada, latón, y el mar de las Bahamas: hondo en los canales y turquesa en los bajos. */
internal object ColorPirata {
    val CUBIERTA = Color(0xFFCDB68E)
    val MADERA = Color(0xFFB08A58)
    val MADERA_OSC = Color(0xFF5E4430)
    val BREA = Color(0xFF2E2622)
    val ROJO = Color(0xFFA6402E)
    val OCRE = Color(0xFFCF9E48)
    val LONA = Color(0xFFE9DEC2)
    val LONA_OSC = Color(0xFFB9A886)
    val CABO = Color(0xFFCDB47C)
    val HIERRO = Color(0xFF4C4E58)
    val BRONCE = Color(0xFFB8873A)
    val LATON = Color(0xFFDAB556)
    val MAR = Color(0xFF2A88A2)
    val MAR_HONDO = Color(0xFF1C5C80)
    val BAJIO = Color(0xFF7ED4C2)
    val ARENA = Color(0xFFF1E5C6)
    val PALMA = Color(0xFF4F7C44)
    val ALGA = Color(0xFF5F7C3C)
    val NEGRO = Color(0xFF211C20)
}

/**
 * Pirata: el balandro «Fortuna», donde Tomás es grumete (las Bahamas y el Caribe, de 1718 a 1722). Es el primer
 * cuadro **a bordo** (`docs/diseno/ESCENAS.md`, sección 5) y lleva **dos cuadros**:
 *
 * - **A bordo, en el mar** (casi todo el año). Se mira desde la cubierta, de una banda a la otra: delante, la cubierta
 *   con sus cosas (la chalupa volcada, el fogón en su caja de arena, un cañón trincado, la bomba, el palo, la escotilla
 *   con el objeto del día, el tonel del agua); en medio, la amurada pintada de rojo y, por encima de ella, el mar, que
 *   lo rodea todo: hondo en los canales y turquesa en los bajos, con cayos y velas lejanas. Enmarcan la vela mayor y
 *   su botavara por arriba, y el palo con sus obenques a la derecha.
 * - **El cayo de la carena** (estado `cayo`, catorce días cada marzo). Se mira desde la playa: el balandro tumbado de
 *   costado en la orilla, con el fondo al aire, a medio rascar; el andamio, el caldero de la brea, los cañones bajados
 *   a tierra, el toldo de vela vieja entre las palmeras y, detrás, la laguna, el arrecife y el mar abierto.
 *
 * **Nassau** (estado `nassau`, 26 días de la temporada 1) es una estancia corta: no lleva cuadro nuevo (decisión 21).
 * Es el cuadro de a bordo con el balandro fondeado: la vela aferrada, y detrás de la amurada, en vez del mar abierto,
 * el puerto con otros barcos, la playa con sus tiendas y sus casas y el fuerte medio caído.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG5.kt`):
 * - **Dónde está el balandro** ([lugar]): en el mar, en el cayo o en Nassau.
 * - **Lo que se ve a lo lejos** ([horizonte], [velasALaVista]): el balandro navega, y el horizonte cambia con el día:
 *   mar abierto, islas lejanas, un cayo, una fila de cayos, una costa alta o un arrecife, y ninguna, una o dos velas.
 * - **El casco** (arco `casco`, [casco] y [bomba]): limpio después de la carena, la estela es larga y blanca y la bomba
 *   descansa; según se ensucia, la estela se acorta y arrastra algas, y la bomba trabaja; muy sucio, no para nunca. En
 *   el cayo ([carenado]): cuánto del fondo está ya rascado y untado.
 * - **La vela** ([vela]): larga; caída los días de `calma`; con rizos en la `tormenta`; aferrada en Nassau.
 * - **Las claves:** `abordaje` (la presa abarloada, garfios en la borda y humo de pólvora), `calma` (el mar como un
 *   plato), `tormenta` (mar gruesa, andariveles y la escotilla trincada), `fiesta` (empavesado y el barril de ron),
 *   `luto` (un paño negro en la borda), `cosecha` (las tortugas, guardadas vivas en cubierta).
 * - **El objeto del día**, sobre la escotilla (en el cayo, sobre un cofre), y **el tramo del día:** el fogón encendido a
 *   la hora de comer, el cubo y el lampazo en la guardia del alba, el farol y los dados de noche.
 */
internal object EscenaPirata : EscenaVida {
    override val vidas = listOf("grumete")

    /** El horizonte del mar. */
    const val H = 118f

    private val C = ColorPirata

    // ---------------------------------------------------------------- Lo que el cuento mueve

    /** Dónde está el balandro. */
    internal enum class Lugar { MAR, CAYO, NASSAU }

    /** Dónde está el balandro hoy: lo vigila `EscenaPirataPrueba`. El cayo es otro cuadro; Nassau, una variante del de a bordo. */
    internal fun lugar(m: Momento): Lugar = when (m.estado) { "cayo" -> Lugar.CAYO; "nassau" -> Lugar.NASSAU; else -> Lugar.MAR }

    /** Cómo va el casco, con los mismos cortes que usa el cuento (0.3 y 0.7 del arco `casco`). */
    internal enum class Casco { LIMPIO, ENSUCIANDOSE, SUCIO }

    internal fun casco(m: Momento): Casco = m.arco("casco").let { if (it < 0.3f) Casco.LIMPIO else if (it < 0.7f) Casco.ENSUCIANDOSE else Casco.SUCIO }

    /** La bomba de achique: descansa con el casco limpio, trabaja a sus horas cuando se ensucia y no para nunca cuando va sucio. */
    internal enum class Bomba { DESCANSA, TRABAJA, NO_PARA }

    internal fun bomba(m: Momento): Bomba = when (casco(m)) { Casco.LIMPIO -> Bomba.DESCANSA; Casco.ENSUCIANDOSE -> Bomba.TRABAJA; Casco.SUCIO -> Bomba.NO_PARA }

    /** Si en este momento sale agua por la bomba. */
    internal fun achicando(m: Momento): Boolean = lugar(m) != Lugar.CAYO && when (bomba(m)) { Bomba.DESCANSA -> false; Bomba.TRABAJA -> m.hacer == Hacer.TRABAJO; Bomba.NO_PARA -> true }

    /** En el cayo: cuánto del fondo está ya rascado y untado, de 0 (recién tumbado) a 1 (listo para volver al agua). */
    internal fun carenado(m: Momento): Float = (1f - m.arco("casco") / 0.95f).coerceIn(0f, 1f)

    /** Cómo va la vela mayor. */
    internal enum class Vela { LARGA, CAIDA, RIZADA, AFERRADA }

    internal fun vela(m: Momento): Vela = when {
        lugar(m) == Lugar.NASSAU -> Vela.AFERRADA
        tormenta(m) -> Vela.RIZADA
        calma(m) -> Vela.CAIDA
        else -> Vela.LARGA
    }

    internal fun tormenta(m: Momento) = m.con("tormenta") || m.ficha.clima == Clima.TORMENTA
    internal fun calma(m: Momento) = m.con("calma")
    internal fun abordaje(m: Momento) = m.con("abordaje") && lugar(m) == Lugar.MAR
    internal fun fiesta(m: Momento) = m.con("fiesta")
    internal fun luto(m: Momento) = m.con("luto")
    internal fun tortugas(m: Momento) = m.con("cosecha")
    internal fun comiendo(m: Momento) = m.hacer == Hacer.COMER
    internal fun conGente(m: Momento) = m.hacer == Hacer.GENTE

    /** La guardia del alba: se baldea la cubierta. */
    internal fun baldeando(m: Momento) = m.hacer == Hacer.TRABAJO && m.hora < 8.0 && lugar(m) != Lugar.CAYO && !tormenta(m)

    /** El farol está encendido: de noche, mientras hay alguien despierto. */
    internal fun farol(m: Momento) = m.luces().noche > 0.3f && m.hacer != Hacer.DORMIR

    /** Cuánto se mueve el mar, de 0 (la calma) a 1 (la tormenta). */
    internal fun marejada(m: Momento): Float = when { calma(m) -> 0f; tormenta(m) -> 1f; lugar(m) == Lugar.NASSAU -> 0.12f; else -> (0.3f + 0.5f * m.viento).coerceIn(0f, 1f) }

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        if (lugar(m) == Lugar.CAYO) { with(CayoPirata) { cayo(m) }; return }
        val l = m.luces(); val r = Rayo(l)
        val semilla = m.ficha.semilla

        // 1. Cielo.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        cieloBajo(m, l)

        // 2. El mar y lo que hay en él: los cayos y las velas lejanas, o el puerto de Nassau.
        if (lugar(m) == Lugar.NASSAU) with(NassauPirata) { puerto(m, l, r) } else marAbierto(m, l, r)

        // La niebla envuelve el mar, no la cubierta: su velo va antes del primer plano, para que no lo cruce en barras.
        val niebla = m.ficha.clima == Clima.NIEBLA
        if (niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)

        // 3. Primer plano: la cubierta, con la amurada, el palo, la vela y las cosas de a bordo.
        with(CubiertaPirata) { cubierta(m, l, r) }

        // 4. El velo del tiempo que hace y el grano del papel.
        if (!niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("pirata", "grano")), 0.6f + 0.4f * r.dia)
    }

    /**
     * La vela tapa lo alto del cielo, así que lo que cambia de un día a otro se ve por debajo de la botavara: la fila de
     * cúmulos del alisio, pegada al horizonte, distinta cada día, y dos o tres rabihorcados planeando.
     */
    private fun DrawScope.cieloBajo(m: Momento, l: Luces) {
        val az = m.azar("cielo bajo")
        if (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES || m.ficha.clima == Clima.VIENTO) {
            val cuantas = 3 + az.entero(3)
            repeat(cuantas) { nube(l, az.entre(-20f, 400f), H - az.entre(3f, 12f), az.entre(26f, 60f), az, carga = if (m.ficha.clima == Clima.NUBES) 0.3f else 0f, lejos = az.entre(0.55f, 0.8f), alza = az.entre(0.8f, 1.3f)) }
        }
        if (l.noche < 0.5f && m.ficha.clima != Clima.TORMENTA && m.ficha.clima != Clima.LLUVIA) {
            val ave = l.tono(Color(0xFF2E2A30), 0.2f)
            repeat(2 + az.entero(2)) {
                val c = Offset(az.entre(270f, 370f), az.entre(40f, 96f)); val w = az.entre(4f, 7f); val giro = az.entre(-0.3f, 0.3f)
                drawPath(Path().apply { moveTo(c.x - w, c.y + giro * w); cubicTo(c.x - w * 0.5f, c.y - w * 0.5f, c.x - w * 0.2f, c.y - w * 0.1f, c.x, c.y); cubicTo(c.x + w * 0.2f, c.y - w * 0.1f, c.x + w * 0.5f, c.y - w * 0.5f, c.x + w, c.y - giro * w) }, ave, style = Stroke(0.9f, cap = StrokeCap.Round))
                drawLine(ave, c, c + Offset(-0.6f, w * 0.34f), 0.7f, StrokeCap.Round)
            }
        }
    }

    // ---------------------------------------------------------------- El mar abierto

    /**
     * Lo que se ve a lo lejos desde el balandro. Navega, así que no ve lo mismo cada día: unos días no hay más que
     * agua; otros, islas azules en el horizonte, un cayo, una fila de cayos sobre su banco, una costa larga y alta
     * como la de La Española, o un arrecife donde rompe el mar.
     */
    internal enum class Horizonte { MAR_ABIERTO, ISLAS_LEJANAS, UN_CAYO, CAYOS_EN_FILA, COSTA_ALTA, ARRECIFE }

    private fun diaDeViaje(m: Momento): Long = LocalDate.parse(m.ficha.dia).toEpochDay()

    /** El tramo de la derrota: tres días seguidos se navega por las mismas aguas. */
    internal fun tramo(m: Momento): Long = Math.floorDiv(diaDeViaje(m), 3L)

    /**
     * Qué hay hoy en el horizonte. Sale del día (no de la hora ni del tiempo que hace), así que el mismo día da siempre
     * la misma vista; los tres días de un [tramo] comparten aguas, y de un tramo al siguiente el paisaje cambia. Lo
     * vigila `EscenaPirataPrueba`. No vale en el cayo ni en Nassau, que tienen su propia vista.
     */
    internal fun horizonte(m: Momento): Horizonte = Horizonte.entries[Azar(Semilla.de("pirata", "derrota", tramo(m))).entero(Horizonte.entries.size)]

    /** Cuántas velas lejanas hay hoy a la vista: ninguna, una o dos. Cambia cada día. */
    internal fun velasALaVista(m: Momento): Int = Azar(Semilla.de("pirata", "velas", diaDeViaje(m))).entero(10).let { if (it < 2) 0 else if (it < 7) 1 else 2 }

    /** El mar de las islas: hondo y azul en los canales, turquesa sobre los bancos de arena; y a lo lejos, lo que toque hoy. */
    private fun DrawScope.marAbierto(m: Momento, l: Luces, r: Rayo) {
        val vista = horizonte(m)
        val marea = marejada(m)
        val enCalma = calma(m)
        val zona = Rect(0f, H, ANCHO, 206f)
        fun aguas(que: String) = Azar(Semilla.de("pirata", "aguas", tramo(m), que))    // lo propio de estas aguas: igual los tres días del tramo

        // La tierra que asoma por detrás de la raya del horizonte va antes que el agua.
        if (vista == Horizonte.ISLAS_LEJANAS) islasLejanas(l, aguas("islas"), reflejos = false)
        if (vista == Horizonte.COSTA_ALTA) costaAlta(l, r, aguas("costa"), enCalma, soloMontes = true)

        // En mar abierto el agua es más honda y más oscura; cerca de tierra, la de siempre.
        val abierto = vista == Horizonte.MAR_ABIERTO
        agua(l, zona, m.azar("mar"), propio = if (abierto) lerp(C.MAR, C.MAR_HONDO, 0.4f) else C.MAR, viento = if (enCalma) 0f else maxOf(m.viento, 0.2f),
            hondo = if (abierto) lerp(C.MAR_HONDO, Color(0xFF143E62), 0.5f) else C.MAR_HONDO, orilla = if (abierto) lerp(C.BAJIO, C.MAR, 0.5f) else C.BAJIO)
        bancos(l, aguas("bancos"), when (vista) { Horizonte.MAR_ABIERTO -> 0; Horizonte.ISLAS_LEJANAS -> 2; Horizonte.COSTA_ALTA -> 2; Horizonte.UN_CAYO -> 4; Horizonte.CAYOS_EN_FILA -> 5; Horizonte.ARRECIFE -> 5 })
        if (enCalma) {
            // La calma: el agua se alisa, pierde los rizos y devuelve entero lo que tiene encima.
            drawRect(Brush.verticalGradient(listOf(lerp(l.bajo, l.tono(C.MAR_HONDO), 0.5f).copy(alpha = 0.62f), lerp(l.medio, l.tono(C.MAR), 0.6f).copy(alpha = 0.6f), lerp(l.alto, l.tono(C.BAJIO), 0.6f).copy(alpha = 0.5f)), zona.top, zona.bottom), zona.topLeft, zona.size)
            val nubesEnElAgua = m.azar("cielo bajo")
            repeat(4) { mancha(Offset(nubesEnElAgua.entre(0f, ANCHO), zona.top + nubesEnElAgua.entre(10f, 50f)), nubesEnElAgua.entre(30f, 70f), nubesEnElAgua.entre(3f, 8f), Color.White, 0.2f * (1f - 0.7f * l.noche)) }
        }
        when (vista) {
            Horizonte.MAR_ABIERTO -> Unit
            Horizonte.ISLAS_LEJANAS -> islasLejanas(l, aguas("islas"), reflejos = true)
            Horizonte.COSTA_ALTA -> costaAlta(l, r, aguas("costa"), enCalma, soloMontes = false)
            Horizonte.UN_CAYO -> {
                val az = aguas("cayo")
                // A veces el cayo queda lejos y chico; otras, cerca y con su rompiente delante. Casi siempre a babor del palo; alguna vez, por la amura.
                val cerca = az.real(); val cx = listOf(92f, 132f, 176f, 150f, 336f)[az.entero(5)]
                cayo(l, r, az, cx, 30f + 34f * cerca, 129f + 9f * cerca, 4 + az.entero(4), 0.44f - 0.14f * cerca, enCalma, rompiente = az.real() > 0.4f)
            }
            Horizonte.CAYOS_EN_FILA -> {
                val az = aguas("fila")
                // Una hilera de cayos que se aleja, cada uno más chico, sobre el mismo banco.
                val haciaLaDerecha = az.real() > 0.5f; val cuantos = 2 + az.entero(2)
                var cx = if (haciaLaDerecha) az.entre(40f, 80f) else az.entre(170f, 210f); var ancho = az.entre(34f, 44f); var base = az.entre(135f, 138f)
                val sitios = ArrayList<Triple<Float, Float, Float>>()
                repeat(cuantos) { sitios += Triple(cx, ancho, base); cx += (if (haciaLaDerecha) 1f else -1f) * ancho * az.entre(1.5f, 1.9f); ancho *= az.entre(0.6f, 0.72f); base -= az.entre(3.4f, 4.6f) }
                val medio = sitios[sitios.size / 2]
                mancha(Offset(medio.first, medio.third + 2f), 130f, 6f, l.tono(C.BAJIO), 0.4f * (1f - 0.7f * l.noche))
                for ((i, c) in sitios.withIndex().reversed()) cayo(l, r, az, c.first, c.second, c.third, if (i == sitios.size - 1 && az.real() > 0.5f) 0 else 2 + az.entero(4), 0.5f - 0.06f * (sitios.size - i), enCalma, rompiente = i == 0)
            }
            Horizonte.ARRECIFE -> arrecife(l, aguas("arrecife"))
        }
        // La mar de fondo: lomos largos que suben y bajan, más altos con viento.
        oleaje(m, l, r, zona, marea)
        if (tormenta(m)) olasGrandes(m, l)
        estela(l, zona, m.azar("estela"))

        // Las velas lejanas: hoy ninguna, una o dos, cada día en otro sitio y con otro aparejo. El día del abordaje, la presa.
        if (abordaje(m)) with(VelasPirata) { presa(m, l, r) } else velasLejanas(m, l, r, marea)
        // La estela del «Fortuna», que pasa pegada a la borda: larga y blanca con el casco limpio, corta y con algas cuando va sucio.
        estelaPropia(m, l)
    }

    /** Los bancos: manchas largas de agua clara donde hay poco fondo, y los canales oscuros entre ellas. Dónde caen depende de las aguas del tramo. */
    private fun DrawScope.bancos(l: Luces, az: Azar, cuantos: Int) {
        val dia = 1f - 0.7f * l.noche
        repeat(cuantos) {
            val prof = az.real(); val c = Offset(az.entre(10f, ANCHO - 10f), H + 10f + 62f * prof); val w = az.entre(50f, 110f) * (0.6f + 0.6f * prof); val h = 2.2f + 5.4f * prof
            mancha(c, w, h, l.tono(C.BAJIO), 0.34f * dia); mancha(c + Offset(w * 0.1f, 0.4f), w * 0.55f, h * 0.55f, l.tono(lerp(C.BAJIO, C.ARENA, 0.4f)), 0.3f * dia)
        }
        repeat(3) { val prof = az.real(); mancha(Offset(az.entre(0f, ANCHO), H + 18f + 60f * prof), az.entre(90f, 130f), 2.4f + 4f * prof, l.tono(C.MAR_HONDO), 0.3f) }
    }

    /** Islas azules en el horizonte, dos o tres, cada tramo en otro sitio: tierra que se ve pero queda a un día de vela. */
    private fun DrawScope.islasLejanas(l: Luces, az: Azar, reflejos: Boolean) {
        val cuantas = 2 + az.entero(2)
        repeat(cuantas) { i ->
            val ancho = az.entre(90f, 210f); val x0 = az.entre(-70f, ANCHO - 60f); val alto = az.entre(5f, 13f); val lejos = az.entre(0.72f, 0.86f)
            val propio = if (i % 2 == 0) Color(0xFF6C7C96) else Color(0xFF6E8474)
            val forma = Azar(Semilla.de("pirata", "isla", x0.toInt(), ancho.toInt()))
            if (!reflejos) monte(l, perfil(forma, x0, x0 + ancho, H + 0.6f, alto, 0.4f, 20, isla = true), H + 0.8f, propio, lejos)
            else reflejoRoto(x0 + ancho * 0.08f, x0 + ancho * 0.92f, H + 0.9f, 2.6f + alto * 0.14f, l.tono(lerp(propio, Color(0xFF3E4E66), 0.5f), 0.6f), 0.4f, az, 0.8f)
        }
    }

    /**
     * Una costa larga y alta, como la de La Española o la de Cuba: los montes azules del interior, con su nube
     * agarrada a la cumbre; delante, las lomas verdes; y abajo, la franja del llano con su playa. Unas veces llena el
     * horizonte y otras acaba en un cabo, con el mar abierto más allá.
     */
    private fun DrawScope.costaAlta(l: Luces, r: Rayo, az: Azar, enCalma: Boolean, soloMontes: Boolean) {
        val cabo = az.entero(3)                                                 // 0: toda la costa; 1: acaba en un cabo a la derecha; 2: empieza en un cabo a la izquierda
        val x0 = if (cabo == 2) az.entre(40f, 110f) else -50f; val x1 = if (cabo == 1) az.entre(250f, 330f) else ANCHO + 50f
        val altoSierra = az.entre(30f, 44f); val altoLomas = az.entre(13f, 20f)
        val semilla = az.entero(100000)
        if (soloMontes) {
            monte(l, perfil(Azar(Semilla.de("pirata", "sierra", semilla)), x0 + 10f, x1 - 10f, H + 1f, altoSierra, 0.55f, 34, isla = true), H + 1.6f, Color(0xFF6A7698), 0.86f)
            monte(l, perfil(Azar(Semilla.de("pirata", "lomas", semilla)), x0, x1, H + 1f, altoLomas, 0.4f, 28, isla = true), H + 1.6f, Color(0xFF5E7C70), 0.7f)
            monte(l, perfil(Azar(Semilla.de("pirata", "llano", semilla)), x0 - 6f, x1 + 6f, H + 0.8f, 4.6f, 0.3f, 22, isla = true), H + 1.4f, lerp(C.PALMA, Color(0xFF7E9A78), 0.4f), 0.56f)
            // La nube que se agarra a la sierra, a media ladera.
            val nubes = Azar(Semilla.de("pirata", "nubes de la sierra", semilla))
            repeat(2) { nube(l, nubes.entre(x0 + 40f, x1 - 80f), H - altoSierra * nubes.entre(0.45f, 0.7f), nubes.entre(40f, 70f), nubes, carga = 0.1f, lejos = 0.7f, alza = 0.8f) }
            return
        }
        val a = x0.coerceAtLeast(-6f); val b = x1.coerceAtMost(ANCHO + 6f)
        // La playa, una raya clara al pie del llano, con la espuma que la bordea; y el reflejo oscuro de la tierra.
        val fijo = Azar(Semilla.de("pirata", "playa de la costa", semilla))
        var x = a + 4f
        while (x < b - 6f) { val w = fijo.entre(8f, 26f); if (fijo.real() > 0.3f) drawLine(l.tono(C.ARENA, 0.5f), Offset(x, H + 1.1f + fijo.entre(-0.2f, 0.3f)), Offset(minOf(x + w, b - 4f), H + 1.2f), 0.9f, StrokeCap.Round); x += w + fijo.entre(2f, 9f) }
        repeat(18) { val px = fijo.entre(a, b - 8f); drawLine(l.tono(Color.White, 0.4f).copy(alpha = fijo.entre(0.3f, 0.7f) * (1f - 0.5f * l.noche)), Offset(px, H + 2.2f), Offset(px + fijo.entre(3f, 10f), H + 2.3f), 0.5f, StrokeCap.Round) }
        reflejoRoto(a + 6f, b - 6f, H + 2.6f, if (enCalma) 22f else 7f, l.tono(Color(0xFF44605E), 0.6f), 0.42f, fijo, 0.8f)
        // Unas palmeras y un humo en la playa: tierra con gente, a la que no conviene acercarse.
        repeat(5) { with(VelasPirata) { palmera(l, r, Offset(fijo.entre(a + 10f, b - 10f), H + 1f), fijo.entre(3.4f, 5.4f), fijo.entre(-1f, 1f), 0.56f, fijo) } }
    }

    /**
     * Un cayo: una lengua de arena con su mata y sus palmeras, cada una a su aire. [cx] y [medio]: dónde está y cuánto
     * mide de medio ancho; [base]: la línea del agua (más abajo es más cerca). El perfil sale del azar de las aguas,
     * así que no hay dos cayos iguales.
     */
    private fun DrawScope.cayo(l: Luces, r: Rayo, az: Azar, cx: Float, medio: Float, base: Float, palmeras: Int, lejos: Float, enCalma: Boolean, rompiente: Boolean) {
        val pasos = 12; val sube = 1.4f + medio * 0.05f
        val joroba = az.entre(0.3f, 0.7f)                                       // dónde queda lo más alto de la arena
        val puntos = (0..pasos).map { i ->
            val t = i / pasos.toFloat(); val forma = if (t < joroba) t / joroba else (1f - t) / (1f - joroba)
            Offset(cx - medio + 2f * medio * t, base - sube * forma.coerceIn(0f, 1f).let { it * (2f - it) } + (if (i == 0 || i == pasos) 0f else az.entre(-0.4f, 0.4f)))
        }
        val orilla = curva(puntos)
        bajio(l, curva(listOf(Offset(cx - medio - 3f, base + 0.6f), Offset(cx + medio + 3f, base + 0.6f))), 5f + medio * 0.2f, C.BAJIO)
        if (rompiente) {
            // La rompiente del arrecife, por fuera.
            val blanco = l.tono(Color.White, 0.3f)
            repeat(10 + (medio / 3f).toInt()) { val x = az.entre(cx - medio * 1.5f, cx + medio * 1.5f); val y = base + 4.6f + medio * 0.03f + 1.4f * sin(x * 0.07f) + az.entre(-0.5f, 0.5f); drawLine(blanco.copy(alpha = az.entre(0.35f, 0.8f) * (1f - 0.5f * l.noche)), Offset(x, y), Offset(x + az.entre(2f, 8f), y + az.entre(-0.3f, 0.3f)), az.entre(0.5f, 1.1f), StrokeCap.Round) }
        }
        drawPath(curva(puntos, base + 0.9f), Brush.verticalGradient(listOf(l.tono(C.ARENA, lejos), l.tono(lerp(C.ARENA, Color(0xFFD2BE94), 0.5f), lejos - 0.06f)), base - sube - 1f, base + 0.9f))
        if (palmeras > 0) {
            // La mata: monte bajo y uva de playa, y por encima las palmeras.
            val m0 = az.entre(0.14f, 0.26f); val m1 = az.entre(0.72f, 0.88f); val alto = (1.6f + medio * 0.07f) * az.entre(0.8f, 1.3f)
            fun enLaArena(t: Float): Offset { val k = t * pasos; val i = k.toInt().coerceIn(0, pasos - 1); return puntos[i] + (puntos[i + 1] - puntos[i]) * (k - i) }
            val mata = curva((0..6).map { i -> val t = m0 + (m1 - m0) * i / 6f; enLaArena(t) + Offset(0f, -0.4f - alto * sin(3.14f * i / 6f).coerceAtLeast(0f) * az.entre(0.75f, 1.1f)) }, base - 0.2f)
            drawPath(mata, l.tono(lerp(C.PALMA, Color(0xFF7E9068), 0.35f), lejos + 0.02f))
            clipPath(mata) { repeat(6 + (medio / 4f).toInt()) { val p = Offset(az.entre(cx - medio, cx + medio), base - az.entre(0.4f, alto + sube)); mancha(p + Offset(r.lat * 1.4f, -0.6f), az.entre(2f, 5f), az.entre(0.8f, 1.6f), l.tono(lerp(C.PALMA, Color(0xFFE2E6A0), 0.5f), lejos), 0.5f); mancha(p + Offset(-r.lat * 1.4f, 0.8f), az.entre(2f, 5f), az.entre(0.8f, 1.6f), l.tono(mult(C.PALMA, r.color, 0.8f), lejos), 0.5f) } }
            val sitios = (0 until palmeras).map { az.entre(m0 + 0.04f, m1 - 0.04f) }.sorted()
            for (t in sitios) with(VelasPirata) { palmera(l, r, enLaArena(t) + Offset(0f, -alto * 0.5f), (5.4f + medio * 0.2f) * az.entre(0.7f, 1.25f), az.entre(-3f, 3f) * (medio / 44f), lejos, az) }
        }
        // La espuma de la orilla y el reflejo de la arena y de la mata.
        drawPath(orilla, l.tono(Color.White, 0.3f).copy(alpha = 0.7f * (1f - 0.5f * l.noche)), style = Stroke(0.5f + medio * 0.01f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val largo = if (enCalma) 5f else 1f
        reflejoRoto(cx - medio * 0.9f, cx + medio * 0.9f, base + 0.6f, (3f + medio * 0.05f) * largo, l.tono(C.ARENA, lejos), 0.42f, az, 0.8f)
        if (palmeras > 0) reflejoRoto(cx - medio * 0.6f, cx + medio * 0.6f, base + 1.4f, (4f + medio * 0.05f) * largo, l.tono(C.PALMA, lejos + 0.06f), 0.3f, az, 0.9f)
    }

    /** Un arrecife a flor de agua: dos o tres hileras de rompiente con sus claros, las cabezas de coral oscuras y, a veces, un bajo de arena que asoma. */
    private fun DrawScope.arrecife(l: Luces, az: Azar) {
        val dia = 1f - 0.7f * l.noche
        val x0 = az.entre(-20f, 60f); val x1 = az.entre(250f, 400f)
        mancha(Offset((x0 + x1) / 2f, 140f), (x1 - x0) * 0.6f, 9f, l.tono(C.BAJIO), 0.5f * dia)
        mancha(Offset((x0 + x1) / 2f + 20f, 137f), (x1 - x0) * 0.4f, 4f, l.tono(lerp(C.BAJIO, C.ARENA, 0.5f)), 0.4f * dia)
        // Las cabezas de coral, que oscurecen el agua clara.
        repeat(26) { val p = Offset(az.entre(x0, x1), az.entre(133f, 150f)); mancha(p, az.entre(3f, 9f) * (0.6f + (p.y - 130f) / 30f), az.entre(0.7f, 1.8f), l.tono(Color(0xFF2E5E62)), 0.4f) }
        val blanco = l.tono(Color.White, 0.25f)
        for ((y, cuantas, grueso) in listOf(Triple(az.entre(131f, 133f), 34, 0.8f), Triple(az.entre(137f, 139.6f), 40, 1.1f), Triple(az.entre(145f, 148f), 26, 1.4f))) {
            val hueco = az.entre(x0 + 40f, x1 - 60f)                             // el paso entre los bajos, por donde se cuela un barco chico
            repeat(cuantas) {
                val x = az.entre(x0, x1); if (x > hueco && x < hueco + 26f) return@repeat
                val yy = y + 1.8f * sin(x * 0.05f + y) + az.entre(-0.7f, 0.7f)
                drawLine(blanco.copy(alpha = az.entre(0.4f, 0.9f) * (1f - 0.5f * l.noche)), Offset(x, yy), Offset(x + az.entre(3f, 12f), yy + az.entre(-0.3f, 0.3f)), grueso * az.entre(0.7f, 1.3f), StrokeCap.Round)
                if (it % 5 == 0) drawOval(l.tono(Color(0xFF4E463C), 0.3f), Offset(x, yy - 0.9f), Size(az.entre(2f, 4.6f), az.entre(0.8f, 1.4f)))
            }
        }
        if (az.real() > 0.5f) {
            // Un bajo de arena pelado, sin una mata.
            val cx = az.entre(x0 + 60f, x1 - 60f); val w = az.entre(16f, 30f)
            drawPath(curva(listOf(Offset(cx - w, 135.4f), Offset(cx - w * 0.4f, 134.2f), Offset(cx + w * 0.3f, 134f), Offset(cx + w, 135.4f)), 135.8f), l.tono(C.ARENA, 0.44f))
            drawLine(l.tono(Color.White, 0.3f).copy(alpha = 0.7f * (1f - 0.5f * l.noche)), Offset(cx - w - 1f, 135.8f), Offset(cx + w + 1f, 135.8f), 0.7f, StrokeCap.Round)
        }
    }

    /** Las velas que se ven a lo lejos: cuántas, dónde y con qué aparejo cambia cada día. Ninguna se parece a la otra. */
    private fun DrawScope.velasLejanas(m: Momento, l: Luces, r: Rayo, marea: Float) {
        val cuantas = velasALaVista(m)
        val az = Azar(Semilla.de("pirata", "velas", diaDeViaje(m), "cuales"))
        val aparejos = VelasPirata.Aparejo.entries.toMutableList().also { for (i in it.size - 1 downTo 1) { val j = az.entero(i + 1); val t = it[i]; it[i] = it[j]; it[j] = t } }
        val pinturas = listOf(Color(0xFF3E5A6A), C.BREA, Color(0xFF5E4430), Color(0xFF4A3A30))
        var antes = -1000f
        repeat(cuantas) { i ->
            val cerca = az.real() > 0.5f
            // A babor del palo o, más allá de los obenques, por la amura; nunca detrás del palo.
            var x = if (az.real() > 0.28f) az.entre(24f, 214f) else az.entre(326f, 366f)
            if (abs(x - antes) < 60f) x = if (x < 120f) x + 90f else x - 90f
            antes = x
            val aparejo = aparejos[i]
            val largo = if (cerca) az.entre(22f, 32f) else az.entre(9f, 15f); val agua = Offset(x, if (cerca) az.entre(142f, 156f) else az.entre(121.6f, 127f))
            with(VelasPirata) { velero(l, r, agua, if (aparejo == VelasPirata.Aparejo.PIRAGUA) largo * 0.6f else largo, aparejo, haciaLaDerecha = az.real() > 0.4f, lejos = if (cerca) 0.34f else 0.68f, viento = marea, semilla = 30 + i, pintura = pinturas[az.entero(pinturas.size)]) }
        }
    }

    /** La mar de fondo: lomos tendidos, con su cara de sombra, su cresta de luz y, si sopla, sus borreguillos. */
    private fun DrawScope.oleaje(m: Momento, l: Luces, r: Rayo, zona: Rect, marea: Float) {
        if (marea <= 0.02f) {
            // La calma: el mar como un plato, que devuelve el cielo en bandas lisas.
            for (i in 0..5) { val y = zona.top + 6f + i * i * 2.6f + i * 4f; drawRect(lerp(l.medio, Color.White, 0.25f).copy(alpha = 0.16f), Offset(0f, y), Size(ANCHO, 1.2f + i * 0.9f)) }
            return
        }
        val az = m.azar("oleaje")
        val oscuro = lerp(l.tono(C.MAR_HONDO), Color(0xFF0C2238), 0.25f); val claro = lerp(l.tono(C.BAJIO), Color.White, 0.25f)
        val cuantos = (9 + 9 * marea).toInt()
        repeat(cuantos) {
            val k = az.real(); val prof = k * k
            val y = zona.top + 10f + (zona.height - 12f) * prof
            val w = (26f + 80f * prof) * az.entre(0.7f, 1.3f); val alto = (0.8f + 4.2f * prof) * (0.5f + 1.3f * marea)
            val x = az.entre(-30f, ANCHO)
            val lomo = Path().apply { moveTo(x, y); cubicTo(x + w * 0.3f, y - alto, x + w * 0.62f, y - alto * 1.1f, x + w, y); cubicTo(x + w * 0.7f, y + alto * 0.5f, x + w * 0.3f, y + alto * 0.5f, x, y); close() }
            drawPath(lomo, oscuro.copy(alpha = 0.16f + 0.14f * marea))
            drawPath(Path().apply { moveTo(x + w * 0.08f, y - alto * 0.24f); cubicTo(x + w * 0.32f, y - alto * 0.98f, x + w * 0.6f, y - alto * 1.06f, x + w * 0.92f, y - alto * 0.2f) }, claro.copy(alpha = (0.2f + 0.2f * marea) * (1f - 0.5f * l.noche)), style = Stroke(0.5f + 0.8f * prof, cap = StrokeCap.Round))
            if (marea > 0.55f && az.real() > 0.2f) {
                val cx = x + w * az.entre(0.3f, 0.6f)
                drawPath(Path().apply { moveTo(cx - w * 0.14f, y - alto * 0.86f); cubicTo(cx - w * 0.04f, y - alto * 1.5f, cx + w * 0.06f, y - alto * 1.4f, cx + w * 0.16f, y - alto * 0.8f); cubicTo(cx + w * 0.06f, y - alto * 0.9f, cx - w * 0.04f, y - alto * 0.96f, cx - w * 0.14f, y - alto * 0.86f); close() }, l.tono(Color.White).copy(alpha = 0.75f))
            }
        }
    }

    /** Con tormenta, la mar gruesa: tres o cuatro olas grandes que se levantan cerca, con la cresta rota en espuma. */
    private fun DrawScope.olasGrandes(m: Momento, l: Luces) {
        val az = m.azar("olas grandes")
        val oscuro = lerp(l.tono(C.MAR_HONDO), Color(0xFF0C2238), 0.4f); val verde = l.tono(lerp(C.MAR, C.BAJIO, 0.3f)); val blanco = l.tono(Color.White)
        for ((x, y, w, alto) in listOf(listOf(-30f, 176f, 170f, 15f), listOf(120f, 190f, 200f, 19f), listOf(250f, 170f, 160f, 13f), listOf(300f, 198f, 150f, 16f))) {
            val ola = Path().apply { moveTo(x, y); cubicTo(x + w * 0.24f, y - alto * 0.6f, x + w * 0.4f, y - alto * 1.1f, x + w * 0.56f, y - alto); cubicTo(x + w * 0.68f, y - alto * 0.86f, x + w * 0.84f, y - alto * 0.3f, x + w, y); cubicTo(x + w * 0.7f, y + alto * 0.5f, x + w * 0.3f, y + alto * 0.5f, x, y); close() }
            drawPath(ola, Brush.verticalGradient(listOf(verde, oscuro.copy(alpha = 0.85f), oscuro.copy(alpha = 0f)), y - alto, y + alto * 0.5f))
            // La cresta, que rompe hacia sotavento, y los regueros de espuma que bajan por el lomo.
            drawPath(Path().apply { moveTo(x + w * 0.3f, y - alto * 0.8f); cubicTo(x + w * 0.42f, y - alto * 1.34f, x + w * 0.54f, y - alto * 1.24f, x + w * 0.7f, y - alto * 0.74f); cubicTo(x + w * 0.58f, y - alto * 0.86f, x + w * 0.44f, y - alto * 0.9f, x + w * 0.3f, y - alto * 0.8f); close() }, blanco.copy(alpha = 0.85f))
            repeat(7) { val k = az.entre(0.2f, 0.8f); val px = x + w * k; val py = y - alto * (1f - abs(k - 0.52f) * 1.9f).coerceAtLeast(0.1f) * 0.8f; drawPath(Path().apply { moveTo(px, py); cubicTo(px - 4f, py + 3f, px - 9f, py + 5f, px - az.entre(10f, 22f), py + az.entre(5f, 9f)) }, blanco.copy(alpha = 0.4f), style = Stroke(0.7f, cap = StrokeCap.Round)) }
        }
    }

    /** La estela del balandro junto a la borda: encajes de espuma que se quedan atrás. Con el casco sucio se acorta, se apaga y arrastra hebras de alga. */
    private fun DrawScope.estelaPropia(m: Momento, l: Luces) {
        if (calma(m)) return
        val az = Azar(Semilla.de("pirata", "estela propia"))
        val viva = when (casco(m)) { Casco.LIMPIO -> 1f; Casco.ENSUCIANDOSE -> 0.6f; Casco.SUCIO -> 0.3f }
        val blanco = l.tono(Color.White)
        val cuantos = (40 * viva).toInt() + 6
        repeat(cuantos) {
            val k = az.real(); val y = 186f + 16f * k
            val x = az.entre(-10f, ANCHO * (0.35f + 0.65f * viva)) + (1f - viva) * 60f
            val w = az.entre(6f, 22f) * (0.6f + k)
            drawPath(Path().apply { moveTo(x, y); cubicTo(x + w * 0.3f, y - 1.6f * k - 0.4f, x + w * 0.7f, y + 1.2f * k, x + w, y - 0.3f) }, blanco.copy(alpha = az.entre(0.3f, 0.75f) * (0.5f + 0.5f * viva) * (1f - 0.45f * l.noche)), style = Stroke(az.entre(0.6f, 1.5f), cap = StrokeCap.Round))
        }
        mancha(Offset(ANCHO * 0.5f, 201f), ANCHO * 0.6f, 5f, l.tono(C.BAJIO), 0.3f * viva)
        if (casco(m) != Casco.LIMPIO) {
            // Las algas que cuelgan del casco se ven a ras de agua, verdes, tendidas hacia popa.
            val alga = l.tono(C.ALGA); val cuantas = if (casco(m) == Casco.SUCIO) 16 else 7
            repeat(cuantas) {
                val x = az.entre(20f, ANCHO - 10f); val y = az.entre(185f, 198f); val w = az.entre(12f, 30f)
                drawPath(Path().apply { moveTo(x, y); cubicTo(x - w * 0.3f, y - 1.6f, x - w * 0.6f, y + 1.4f, x - w, y - 0.4f) }, alga.copy(alpha = 0.75f), style = Stroke(az.entre(0.7f, 1.4f), cap = StrokeCap.Round))
            }
        }
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el agua siempre brilla

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = m.luces()
        if (lugar(m) == Lugar.CAYO) { with(CayoPirata) { cayoVivo(m, l, reloj) }; tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla); return }
        // El balandro anda hacia la derecha: el humo y los gallardetes se tienden hacia popa, a la izquierda.
        val viento = -(if (calma(m)) 0.02f else maxOf(0.3f, m.viento))
        destellos(l, Rect(0f, H + 1f, ANCHO, 198f), reloj, m.semilla("destellos"), if (calma(m)) 14 else 26)
        if (lugar(m) == Lugar.NASSAU) with(NassauPirata) { puertoVivo(m, l, reloj) }
        if (abordaje(m)) with(VelasPirata) { humoDeLaPresa(l, reloj, viento) }
        with(CubiertaPirata) { cubiertaViva(m, l, reloj, viento) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    override fun estorbos(m: Momento): List<Float> = if (lugar(m) == Lugar.CAYO) CayoPirata.estorbos(m) else CubiertaPirata.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = if (lugar(m) == Lugar.CAYO) CayoPirata.anclas(m) else CubiertaPirata.anclas(m)
}
