package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * La paleta de Mesoamérica: manda el verde de la selva y lo acompañan el blanco crema del estuco y el rojo de
 * hematita con que se pintaban los edificios. La luz es cálida y la sombra, azul violeta, como en todos.
 */
internal object ColorMeso {
    val ESTUCO = Color(0xFFEDE3CC)
    val ROJO = Color(0xFFB0503C)
    val PIEDRA = Color(0xFFB9B09A)
    val PIEDRA_OSC = Color(0xFF7E7868)
    val TIERRA = Color(0xFF8A6244)
    val PALMA = Color(0xFFB89A5E)
    val MADERA = Color(0xFF8C6844)
    val CORTEZA = Color(0xFFA3A590)
    val SELVA = Color(0xFF3F6E44)
    val SELVA_OSC = Color(0xFF24492F)
    val HOJA_LUZ = Color(0xFF8DAE54)
    val MAIZ = Color(0xFFCDB85C)
    val JADE = Color(0xFF4F9A82)
    val AMATE = Color(0xFFE2CFA0)
    val BARRO = Color(0xFFB5683E)
    val AGUADA = Color(0xFF4C8C7C)
    val CENIZA = Color(0xFF5E564E)
    val TINTA = Color(0xFF2E2622)

    /** La luz del fogón, del ocote y de los fuegos de los templos: no hay otra. */
    val LUMBRE = Color(0xFFFF9A40)
}

/**
 * Mesoamérica: el patio de la casa de K'an, escriba y pintor de códices, sobre su plataforma de piedra en una de las
 * colinas que rodean la plaza de una ciudad maya de las tierras bajas (años 750 a 754). **[Diseño propio]**: la ciudad
 * no tiene nombre en el cuento y no copia ninguna real; los glifos son manchas que no se leen.
 *
 * - **Al fondo:** la selva hasta donde alcanza la vista, en varias filas con bruma entre ellas, y otras pirámides que
 *   salen de ella «como islas».
 * - **En medio:** la plaza estucada, grande y blanca. A un lado, el templo mayor, una pirámide de nueve cuerpos pintada
 *   de rojo, con su templo y su crestería; al otro, el palacio del señor, y delante el juego de pelota. En medio, las
 *   estelas de los señores de antes y **la estela nueva**, la del fin del k'atun. A la derecha, la aguada y, en la
 *   ladera, **la milpa** de la familia.
 * - **Delante:** el patio. El banco con el códice de amate desplegado, las conchas de tinta y el objeto del día, sobre
 *   el petate; la tinaja del agua; la piedra de moler; el fogón de tres piedras; el poyo de la casa, con el horcón, el
 *   ocote y su morral de viaje; y, enmarcando, la ceiba a la izquierda (con el telar de cintura atado a su tronco) y el
 *   alero de palma a la derecha.
 *
 * Cómo está pintado: es un exterior con sol y se aplica la guía tal cual. De noche alumbran el fogón, el ocote del
 * horcón, el fuego de lo alto del templo y los braseros del palacio.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG3.kt`):
 * - **La estela** ([estela], arco `estela`): el sitio marcado hasta que el señor la encarga; la piedra recién traída,
 *   tumbada sobre rodillos; en pie dentro de su andamio mientras se talla, de arriba abajo ([tallado]); y levantada,
 *   pintada y con su altar desde el fin del k'atun, para siempre.
 * - **La milpa** ([milpa], arco `milpa`): arde en abril y su humo enturbia el cielo ([luces]); ceniza hasta las
 *   lluvias; el maíz bajo, alto y doblado; la cosecha; el rastrojo. El huracán de la temporada 2 la tumba ([tumbada]).
 * - **La embajada** (estado `embajada`, 33 días de la temporada 3): no lleva cuadro nuevo (decisión 21). Es una
 *   variante: el mismo patio sin él, con el banco vacío, el petate enrollado y sin su morral en el horcón.
 * - **Los días especiales:** el copal del fin del tun y del k'atun; las fiestas (el año nuevo, el juego de pelota, los
 *   primeros elotes); los cinco días sin nombre, con la plaza vacía y los fuegos apagados; el luto; la guerra, que se
 *   ve como humo lejos y estandartes en el palacio; las tormentas; y Venus, el lucero de la mañana.
 * - **El tramo del día:** el comal al amanecer y a la hora de comer; el fogón encendido para la cena; el rescoldo de
 *   noche; el códice tapado mientras duerme.
 * - **El objeto del día:** sobre el banco, junto al códice (el petate y la manta, a su lado).
 */
internal object EscenaMeso : EscenaVida {
    override val vidas = listOf("escriba_maya")

    /** El horizonte: la altura de los ojos de quien mira, de pie en el patio. */
    const val H = 122f

    private val C = ColorMeso

    // ---------------------------------------------------------------- Lo que el cuento mueve

    internal fun mesDia(m: Momento): String = m.ficha.dia.substring(5)
    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Está en Yaxchilan con los regalos de su señor: su sitio del patio queda recogido. */
    internal fun deEmbajada(m: Momento): Boolean = m.estado == "embajada"

    /** Cómo está la estela del fin del k'atun. */
    internal enum class Estela { SIN, PIEDRA, EN_OBRA, LEVANTADA }

    /** Lo que lleva hecha la estela, de 0 a 1: el arco `estela`. */
    internal fun obraDeLaEstela(m: Momento): Float = m.arco("estela").coerceIn(0f, 1f)

    /**
     * La estela: nada hasta que el señor la encarga (20 de febrero de la temporada 1); la piedra recién traída de la
     * cantera, tumbada, el primer tramo de la obra; en pie dentro del andamio mientras se talla; y levantada desde el
     * fin del k'atun (3 de mayo de la temporada 2), para siempre.
     */
    internal fun estela(m: Momento): Estela {
        val e = obraDeLaEstela(m)
        return when {
            e >= 0.999f -> Estela.LEVANTADA
            e > 0.15f -> Estela.EN_OBRA
            m.ficha.temporada == 1 && mesDia(m) < "02-20" -> Estela.SIN
            else -> Estela.PIEDRA
        }
    }

    /** Cuánto de la piedra está ya tallado, de 0 a 1, de arriba abajo. */
    internal fun tallado(m: Momento): Float = ((obraDeLaEstela(m) - 0.15f) / 0.85f).coerceIn(0f, 1f)

    /** Cómo está la milpa de la familia. */
    internal enum class Milpa { RASTROJO, ARDE, CENIZA, BAJA, ALTA, DOBLADA }

    /** Lo que ha crecido el maíz, de 0 a 1: el arco `milpa`. */
    internal fun etapaDeLaMilpa(m: Momento): Float = m.arco("milpa").coerceIn(0f, 1f)

    /**
     * La milpa: rastrojo desde que se acaba la cosecha (5 de noviembre) hasta la quema; arde los días de la quema;
     * ceniza hasta la siembra (20 de mayo); después el maíz bajo, alto y, desde que está a punto, doblado.
     */
    internal fun milpa(m: Momento): Milpa {
        val md = mesDia(m); val e = etapaDeLaMilpa(m)
        return when {
            quema(m) -> Milpa.ARDE
            md >= "11-05" || md < "04-05" -> Milpa.RASTROJO
            md < "05-20" -> Milpa.CENIZA
            md >= "10-15" || e >= 0.95f -> Milpa.DOBLADA
            e < 0.5f -> Milpa.BAJA
            else -> Milpa.ALTA
        }
    }

    /** Cuánto de la milpa se ha cosechado ya, de 0 a 1 (del 15 de octubre al 5 de noviembre). */
    internal fun cosechado(m: Momento): Float = mesDia(m).let { if (it >= "10-15" && it < "11-05") (1f - etapaDeLaMilpa(m)).coerceIn(0f, 1f) else 0f }

    /** El huracán de la temporada 2 tumba la milpa: queda acostada hasta la cosecha. */
    internal fun tumbada(m: Momento): Boolean = m.ficha.temporada == 2 && mesDia(m).let { it >= "09-05" && it < "10-15" }

    /** La sequía de la temporada 1: del 10 de julio a fin de mes la milpa amarillea y se dobla. */
    internal fun mustia(m: Momento): Boolean = m.ficha.temporada == 1 && mesDia(m).let { it >= "07-10" && it <= "07-31" }

    internal fun quema(m: Momento) = m.con("quema")
    internal fun copal(m: Momento) = m.con("copal")
    internal fun luto(m: Momento) = m.con("luto")
    internal fun guerra(m: Momento) = m.con("guerra")
    internal fun huracan(m: Momento) = hoyEs(m, "huracan")
    internal fun wayeb(m: Momento) = hoyEs(m, "wayeb")
    internal fun juegoDePelota(m: Momento) = hoyEs(m, "juego_pelota")
    internal fun elotes(m: Momento) = hoyEs(m, "elotes")
    internal fun comerciantes(m: Momento) = hoyEs(m, "comerciantes")
    internal fun finDelKatun(m: Momento) = hoyEs(m, "fin_katun")
    internal fun murcielagos(m: Momento) = hoyEs(m, "murcielagos") && m.hora in 17.0..20.4

    /** Día de fiesta en la ciudad: estandartes en la plaza y guirnaldas en el patio. En el viaje no cuenta: la fiesta es de allá. */
    internal fun fiesta(m: Momento) = !deEmbajada(m) && (m.con("fiesta") || hoyEs(m, "pop") || juegoDePelota(m) || elotes(m) || copal(m))

    /** Venus sale hoy como lucero de la mañana: se ve antes de que claree, bajo, al este. */
    internal fun lucero(m: Momento) = (hoyEs(m, "venus") || hoyEs(m, "venus_752") || hoyEs(m, "venus_754")) && m.hora >= 4.0 && m.hora < 6.4

    internal fun mojado(m: Momento) = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
    internal fun lluvias(m: Momento) = m.ficha.estacion == Estacion.LLUVIAS

    /** La bruma de la mañana sobre la selva, de 0 a 1: desde antes de amanecer hasta que el sol la levanta. Más en las lluvias. */
    internal fun brumaDeLaManana(m: Momento): Float {
        val h = m.hora.toFloat()
        val k = if (h < 4f || h > 9f) 0f else if (h <= 7f) 1f else (9f - h) / 2f
        return k * (if (lluvias(m)) 1f else 0.65f) * (if (m.ficha.clima == Clima.VIENTO) 0.3f else 1f)
    }

    /** Cómo está el fogón de tres piedras del patio. */
    internal enum class Fogon { APAGADO, COMAL, ENCENDIDO, RESCOLDO }

    /**
     * El fogón: con el comal al amanecer (se muele y se echan las tortillas) y a la hora de comer; encendido para la
     * cena, que es en el patio; rescoldo después. Con tormenta no se enciende, y en los días sin nombre se come frío.
     */
    internal fun fogon(m: Momento): Fogon {
        val h = m.hora
        return when {
            wayeb(m) || m.ficha.clima == Clima.TORMENTA -> Fogon.APAGADO
            m.hacer == Hacer.COMER || (h >= 5.34 && h < 7.34) -> Fogon.COMAL
            m.hacer == Hacer.GENTE || (h >= 18.66 && h < 21.0) -> Fogon.ENCENDIDO
            h >= 21.0 || h < 5.34 -> Fogon.RESCOLDO
            else -> Fogon.APAGADO
        }
    }

    /** Cuánta lumbre da el fogón: alumbra lo que tiene cerca. */
    internal fun lumbre(m: Momento): Float = when (fogon(m)) {
        Fogon.APAGADO -> 0f
        Fogon.COMAL -> 0.5f
        Fogon.RESCOLDO -> 0.55f
        Fogon.ENCENDIDO -> 1f
    }

    /** El ocote del horcón: encendido al anochecer, hasta que la casa se acuesta (a las diez), y antes de amanecer, cuando se muele. */
    internal fun ocote(m: Momento): Boolean = luces(m).noche > 0.3f && (m.hora >= 17.0 && m.hora < 22.0 || m.hora >= 4.66 && m.hora < 8.0) && !wayeb(m) && m.ficha.clima != Clima.TORMENTA

    /** El fuego de lo alto del templo mayor: arde toda la noche, menos en los días sin nombre y con tormenta. */
    internal fun fuegoDelTemplo(m: Momento): Boolean = luces(m).noche > 0.2f && !wayeb(m) && m.ficha.clima != Clima.TORMENTA

    /** Sube humo de copal de lo alto del templo: en las fiestas del tun y a la hora en que él está en el templo. */
    internal fun copalEnElTemplo(m: Momento): Boolean = !wayeb(m) && (copal(m) || (m.hacer == Hacer.REZAR && !deEmbajada(m)))

    /** El códice está desplegado sobre el banco: mientras él está en casa y despierto. Si no, plegado y tapado; de viaje, guardado. */
    internal fun codiceAbierto(m: Momento): Boolean = !deEmbajada(m) && m.hacer != Hacer.DORMIR && !mojado(m) && !luto(m) && !wayeb(m)

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del momento. El cielo de todos no se retoca: los días de la quema se le pone encima el velo del humo (el
     * sol es una bola roja y todo amarillea), y de noche, con el cielo limpio, la luna aclara un poco la plaza blanca.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces()
        val velo = if (quema(m)) 0.7f else if (guerra(m)) 0.2f else 0f
        if (velo <= 0f) {
            if (l.noche < 0.3f || l.estrellas < 0.2f) return l
            val k = 0.3f * ((l.noche - 0.3f) / 0.7f) * l.estrellas
            return Luces(l.alto, l.medio, l.bajo, lerp(l.ambiente, Color(0xFF8C96C2), k), lerp(l.bruma, Color(0xFF4A5686), k * 0.6f), l.brumaFuerza, l.solX, l.solAltura, l.solColor, l.solFuerza, l.lunaX, l.lunaAltura, l.estrellas, l.noche, l.nublado, l.eclipse)
        }
        fun humo(c: Color, propio: Color, k: Float) = lerp(c, Color(propio.red * l.ambiente.red, propio.green * l.ambiente.green, propio.blue * l.ambiente.blue), k * velo)
        return Luces(
            alto = humo(l.alto, Color(0xFFA89A84), 0.6f), medio = humo(l.medio, Color(0xFFD0B088), 0.7f), bajo = humo(l.bajo, Color(0xFFE8B878), 0.7f),
            ambiente = lerp(l.ambiente, Color(l.ambiente.red, l.ambiente.green * 0.88f, l.ambiente.blue * 0.72f), 0.8f * velo),
            bruma = humo(l.bruma, Color(0xFFD8B890), 0.7f), brumaFuerza = (l.brumaFuerza + 0.08f * velo).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = lerp(l.solColor, Color(0xFFFF7A44), 0.8f * velo), solFuerza = l.solFuerza * (1f - 0.5f * velo),
            lunaX = l.lunaX, lunaAltura = l.lunaAltura, estrellas = l.estrellas * (1f - 0.8f * velo), noche = l.noche, nublado = l.nublado, eclipse = l.eclipse,
        )
    }

    /** El rayo del sol; de noche, el de la luna, que sobre el estuco blanco dibuja bien las formas. */
    internal fun rayo(l: Luces): Rayo { val r = Rayo(l); return Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza, r.color, maxOf(r.contraste, 0.3f + 0.5f * l.noche)) }

    /** Dónde arde el fogón, dónde está el ocote del horcón y dónde el fuego del templo. */
    internal val FOGON = Offset(300f, 266f)
    internal val OCOTE = Offset(359f, 168f)
    internal val FUEGO_TEMPLO = Offset(262f, 83.6f)

    /** Las luces del patio en este momento: el fogón y el ocote. */
    internal fun focos(m: Momento): List<Foco> {
        val l = luces(m)
        val k = ((l.noche - 0.1f) / 0.6f).coerceIn(0f, 1f)
        return buildList {
            val f = lumbre(m)
            if (f > 0f) add(Foco(FOGON + Offset(0f, -12f), C.LUMBRE, (0.22f + 0.78f * k) * f * 0.95f, 54f + 20f * f, suelo = FOGON.y))
            if (ocote(m)) add(Foco(OCOTE, Color(0xFFFFB45A), 0.75f * k, 62f, suelo = 262f))
        }
    }

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val r = rayo(l)
        val semilla = m.ficha.semilla
        val a = Alumbrado(l, focos(m))

        // 1. El cielo.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)

        // 2. La selva, de lo más lejano a los árboles que rodean la plaza, y la milpa en su ladera.
        with(SelvaMeso) { lejania(m, l, r) }

        // 3. La ciudad: la plaza, el palacio, el templo mayor, las estelas y la aguada.
        with(CiudadMeso) { ciudad(m, l, r) }
        with(SelvaMeso) { brumaDeLaPlaza(m, l); orlaDeLaPlaza(m, l, r) }

        // La niebla envuelve la ciudad, no el patio: su velo va antes del primer plano.
        val niebla = m.ficha.clima == Clima.NIEBLA
        if (niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)

        // 4. El primer plano: el patio y sus cosas, la ceiba y el alero.
        with(PatioMeso) { patio(m, l, r, a) }

        // 5. El velo del tiempo que hace y el grano del papel.
        if (!niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("meso", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // siempre humea algo, o hay luciérnagas

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        with(CiudadMeso) { ciudadViva(m, l, reloj) }
        with(PatioMeso) { patioVivo(m, l, reloj) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    /** De día, la luz del sol; de noche, junto al fogón o bajo el ocote, la de la lumbre. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val f = focos(m)
        if (f.isEmpty() || l.noche < 0.4f) return LuzGato(rayo(l), l.noche) { l.tono(it) }
        val a = Alumbrado(l, f)
        val manda = f.maxByOrNull { it.fuerza / (1f + ((it.p.x - en.x) * (it.p.x - en.x)) / (it.alcance * it.alcance)) }!!
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayoDeFoco(manda, en, 0.6f, Color(0xFF7A70A8)), l.noche, manda.color) { lerp(l.tono(it), a.en(centro, it), 0.7f) }
    }

    override fun estorbos(m: Momento): List<Float> = PatioMeso.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = PatioMeso.anclas(m)
}
