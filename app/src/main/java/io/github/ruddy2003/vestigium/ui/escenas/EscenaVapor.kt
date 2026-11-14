package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.floor

/** La paleta de Vapor victoriano: manda el ladrillo con hollín del norte y lo acompaña el verde de la máquina. */
internal object ColorVapor {
    // Doce colores de materia.
    val LADRILLO = Color(0xFF9A4E3A)
    val PIEDRA = Color(0xFFCDB98E)          // la piedra rubia del viaducto y de los dinteles
    val PIZARRA = Color(0xFF58606E)
    val HOLLIN = Color(0xFF2E2C30)
    val HIERRO = Color(0xFF4A4C54)
    val VERDE = Color(0xFF2F6A46)           // la chapa de la máquina
    val LATON = Color(0xFFD2A44A)
    val ROJO = Color(0xFFB03A2C)            // los topes, el brazo de la señal
    val BALASTO = Color(0xFF8E857A)
    val MADERA = Color(0xFF8A6A48)
    val HUESO = Color(0xFFE9E0C8)           // la cal, la pintura blanca de las señales
    val PRADO = Color(0xFF76965E)

    // Mezclas de los anteriores, con nombre porque se usan mucho: no son colores nuevos.
    val LADRILLO_OSC = lerp(LADRILLO, HOLLIN, 0.4f)
    val COBRE = lerp(LATON, ROJO, 0.45f)
    val CARBONILLA = lerp(HOLLIN, BALASTO, 0.35f)
    val BREZO = lerp(PIZARRA, LADRILLO, 0.35f)   // el páramo
    val NIEVE = lerp(HUESO, Color.White, 0.7f)
    val AZUL_PANA = lerp(PIZARRA, HOLLIN, 0.3f)  // la chaqueta de Albert

    /** Las luces de la noche: el gas de las farolas, el fuego del hogar y los dos cristales de las señales. */
    val GAS = Color(0xFFF6D488)
    val FUEGO = Color(0xFFFF8E3E)
    val VERDE_SENAL = Color(0xFF58E0A0)
    val ROJO_SENAL = Color(0xFFFF4636)

    /** La luz del día, algo dorada por el humo del carbón, y su contraria para las sombras. */
    val LUZ = Color(0xFFFFF0CC)
    val SOMBRA = Color(0xFF7E78AE)
}

/** Lo que pinta una cosa del depósito: de día, el sol le da su lado claro y su lado en sombra; de noche la tiñen las luces que tenga cerca. */
internal class PincelVapor(val l: Luces, val r: Rayo, val a: Alumbrado) {
    private val C = ColorVapor

    /** De noche y con alguna luz encendida. */
    val deNoche: Boolean get() = a.focos.isNotEmpty() && l.noche > 0.45f

    /** El color [propio] de una cara que está en [p] y mira a la izquierda (−1), de frente (0) o a la derecha (1). */
    fun cara(p: Offset, propio: Color, mira: Float = 0f, lejos: Float = 0f): Color {
        val hacia = if (mira == 0f) 0.3f * r.dia else (if (mira * r.lat > 0f) 1f else -1f) * abs(mira).coerceAtMost(1f)
        val c = if (hacia >= 0f) lerp(propio, C.LUZ, 0.3f * hacia * r.contraste) else mult(propio, r.color, 0.85f * -hacia * r.contraste)
        return a.en(p, c, mira, lejos)
    }

    /** Una cara de arriba: recibe toda la luz. */
    fun arriba(p: Offset, propio: Color, lejos: Float = 0f): Color = a.en(p, lerp(propio, C.LUZ, 0.36f * r.dia * r.contraste), 0f, lejos)

    /** Una cara en sombra propia (debajo de algo, o de espaldas a todo). */
    fun umbra(p: Offset, propio: Color, lejos: Float = 0f): Color = a.en(p, mult(propio, r.color, 0.85f * (0.4f + 0.6f * r.contraste)), 0f, lejos)
}

/**
 * Vapor victoriano: el patio del depósito de locomotoras, visto por alguien de pie entre las vías, mirando valle abajo
 * hacia la costa (Albert, fogonero de una línea del noreste de Inglaterra, de 1880 a 1884).
 * **[Diseño propio]**: la compañía, el depósito y el viaducto son inventados; la máquina no lleva la librea de ninguna
 * compañía real, y no hay letras ni números (el número de bronce es una placa lisa).
 *
 * - **Al fondo** (`ValleVapor`, por planos): el cielo del norte; el mar al final del valle y los hornos de la costa; los
 *   páramos en tres filas; las laderas con sus hileras de casas de ladrillo, la iglesia y la mina; y, cruzando el valle
 *   entero por detrás del depósito, **el viaducto nuevo** de piedra rubia, alto, que se levanta pilar a pilar y arco a
 *   arco (arco `viaducto`). La línea pasa por debajo. Más cerca, la fábrica con su chimenea grande y la taberna.
 * - **En medio:** la cochera de ladrillo con su tejado de cristal negro de hollín, la torre del agua con su manga de
 *   cuero, las vías que huyen, la caseta del guardagujas, los postes del telégrafo, la garita y un vagón de carbón.
 * - **Delante:** **la máquina**, de costado y grande: una seis ruedas acopladas, verde con filetes negros, con su ténder;
 *   la señal de brazos, que cruza el cuadro de abajo arriba; la farola de gas; el arca de las herramientas con el objeto
 *   del día; el brasero, el montón de carbón con su carretilla, el cambio de agujas y las traviesas apiladas.
 * - **El humo** (`HumoVapor`): el gris de la máquina, a borbotones; el vapor blanco de las válvulas y los purgadores; y
 *   el pardo de las fábricas, tendido por el viento, que sopla siempre hacia la izquierda ([soplo]).
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG6.kt`):
 * - **La máquina** ([maquina]): hasta el 2 de febrero de la temporada 1, la vieja, negra; desde ese día, la nueva, verde.
 * - **El fuego** ([fuego]): dormido de noche, encendiéndose antes del turno y a toda presión de 5:30 a 17:30.
 * - **El viaducto** ([pilar], [arcosCerrados], [viaductoTerminado]): con los mismos cortes que usa el cuento.
 * - **La baja** (estado `baja`, 30 días de la temporada 3): no lleva cuadro nuevo (decisión 21). Es una variante: el
 *   arca con candado, sin su chaqueta ni su lata, y un tubo de cobre nuevo donde reventó el grifo.
 * - **Maquinista** (estado `maquinista`, desde el 10 de septiembre de la temporada 5): su gorra cuelga del otro lado de
 *   la cabina, junto al regulador, la aceitera de pico largo está en el arca y la chaqueta del arca es la de otro.
 * - **Los días especiales:** la excursión (banderines, banderas en la máquina y, en la gala, los estandartes de los
 *   mineros), la fiesta, Guy Fawkes (hogueras y cohetes), la Navidad, el luto, el vendaval (el humo tumbado, los árboles
 *   vencidos, la lona del vagón suelta, hojas y papeles por el aire), la nevada que deja el tren
 *   atascado (la vía, vacía), la helada (carámbanos en la torre del agua), el vagón descarrilado, el primer tren por el
 *   viaducto y los atardeceres rojos de Krakatoa.
 * - **El tramo del día:** la aguada al empezar el turno y a mediodía, la limpieza al acabarlo, la taberna con luz por
 *   la noche.
 * - **El objeto del día:** sobre el arca de las herramientas.
 */
internal object EscenaVapor : EscenaVida {
    override val vidas = listOf("fogonero")

    /** El horizonte: la altura de los ojos de quien mira. */
    const val H = 120f

    /** Hacia dónde huyen las vías. */
    val FUGA = Offset(236f, H)

    /** La cabeza del carril de la vía de la máquina. */
    const val RIEL = 232f

    /** Donde pisa el gato. */
    const val PISO = 277f

    private val C = ColorVapor

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Cuál es su máquina. */
    internal enum class Maquina { VIEJA, VERDE }

    /** Hasta el 2 de febrero de la temporada 1 lleva la vieja; ese día les dan «una recién salida del taller, verde y brillante». */
    internal fun maquina(m: Momento): Maquina = if (paso(m, 1, "02-02")) Maquina.VERDE else Maquina.VIEJA

    /** La máquina está en el depósito. Falta la noche de la nevada que la deja atascada en una trinchera de la línea. */
    internal fun enElDeposito(m: Momento): Boolean = !(hoyEs(m, "nieve") && (m.hora >= 15.0 || m.hora < 5.0))

    /** El turno de doce horas de la máquina, esté Albert o no. */
    internal fun turno(m: Momento): Boolean = m.hora >= 5.5 && m.hora < 17.5

    /** Cómo está el fuego del hogar. */
    internal enum class Fuego { DORMIDO, ENCENDIENDO, A_PRESION }

    internal fun fuego(m: Momento): Fuego = when {
        turno(m) -> Fuego.A_PRESION
        m.hora >= 3.5 && m.hora < 5.5 -> Fuego.ENCENDIENDO
        else -> Fuego.DORMIDO
    }

    /** Cargan agua: al empezar el turno y a mediodía, con la manga de cuero metida en el ténder. */
    internal fun aguada(m: Momento): Boolean = enElDeposito(m) && ((m.hora >= 5.5 && m.hora < 6.67) || (m.hora >= 12.0 && m.hora < 12.67))

    /** La limpian al acabar el turno, con estopa y aceite: la escalera apoyada y el cubo. */
    internal fun limpieza(m: Momento): Boolean = enElDeposito(m) && m.hora >= 16.3 && m.hora < 17.5

    internal fun deBaja(m: Momento): Boolean = m.estado == "baja"
    internal fun maquinista(m: Momento): Boolean = m.estado == "maquinista"

    /** Sus cosas están en el arca (la chaqueta, la lata del té): en su turno, si no está de baja. */
    internal fun susCosas(m: Momento): Boolean = turno(m) && !deBaja(m)

    /** Desde que vuelve de la baja, la máquina lleva un tubo de cobre nuevo donde reventó el grifo. */
    internal fun tuboNuevo(m: Momento): Boolean = paso(m, 3, "07-20")

    /** El día de la quemadura el grifo roto echa vapor por el costado de la cabina. */
    internal fun grifoRoto(m: Momento): Boolean = hoyEs(m, "quemadura") && turno(m)

    // ---- El viaducto: nueve apoyos (dos estribos y siete pilas) y ocho arcos.
    internal const val APOYOS = 9

    internal fun viaducto(m: Momento): Float = m.arco("viaducto").coerceIn(0f, 1f)

    /** Hay obra en el valle (desde el 3 de mayo de la temporada 1): el mismo corte que el cuento, 0.02. */
    internal fun hayObra(m: Momento): Boolean = viaducto(m) >= 0.02f

    /** Cuánto ha subido el apoyo [i] (0 el de la izquierda), de 0 a 1: crecen de izquierda a derecha y están todos al cerrarse el primer arco (0.45). */
    internal fun pilar(m: Momento, i: Int): Float { val a = viaducto(m); return if (a < 0.02f) 0f else ((a - 0.02f) / 0.43f * 1.5f - 0.5f * i / (APOYOS - 1)).coerceIn(0f, 1f) }

    /** Cuántos arcos están cerrados, de 0 a 8: el primero, el 20 de agosto de la temporada 2 (0.45); el último, el 31 de mayo de la 4. */
    internal fun arcosCerrados(m: Momento): Int { val a = viaducto(m); return if (a < 0.4499f) 0 else (1 + floor((a - 0.45f) / 0.55f * 7.0001f).toInt()).coerceIn(1, APOYOS - 1) }

    /** El viaducto está terminado, con su vía (el corte del cuento, 0.99). */
    internal fun viaductoTerminado(m: Momento): Boolean = viaducto(m) >= 0.99f

    /** Pasa un tren por el viaducto: el día del primero, a su hora; después, unas horas sí y otras no. */
    internal fun trenEnElViaducto(m: Momento): Boolean = viaductoTerminado(m) && (hoyEs(m, "primer_tren") && turno(m) || m.azar("tren", floor(m.hora).toInt()).entero(3) == 0)

    internal fun excursion(m: Momento): Boolean = m.con("excursion")
    internal fun gala(m: Momento): Boolean = hoyEs(m, "gala")
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")
    internal fun hogueras(m: Momento): Boolean = hoyEs(m, "guy_fawkes")
    internal fun navidad(m: Momento): Boolean = hoyEs(m, "navidad") || hoyEs(m, "boxing")
    internal fun luto(m: Momento): Boolean = m.con("luto")
    internal fun vendaval(m: Momento): Boolean = m.con("viento")
    internal fun helada(m: Momento): Boolean = hoyEs(m, "helada")
    internal fun descarrilado(m: Momento): Boolean = hoyEs(m, "descarrila")
    internal fun elecciones(m: Momento): Boolean = hoyEs(m, "eleccion")

    /** Desde que se mudan (14 de abril de la temporada 4), su casa de la calle de los ferroviarios tiene luz al anochecer. */
    internal fun suCasa(m: Momento): Boolean = paso(m, 4, "04-14")

    /** Los atardeceres rojos de Krakatoa: el día que el calendario los nombra y los treinta siguientes. **[Aproximación]**: el calendario da un solo día. */
    internal fun krakatoa(m: Momento): Boolean = m.ficha.temporada == 4 && mesDia(m) >= "11-28" && mesDia(m) <= "12-28"

    /** La taberna de la esquina tiene luz: cuando está con los del depósito, y todas las tardes hasta la hora de dormir. */
    internal fun taberna(m: Momento): Boolean = m.hora >= 17.5 && m.hora < 22.5

    /** El brasero del patio arde: las noches y madrugadas frías, con la helada y con nieve. Nunca con tormenta. */
    internal fun brasero(m: Momento): Boolean = m.ficha.clima != Clima.TORMENTA &&
        (helada(m) || m.ficha.clima == Clima.NIEVE || ((m.ficha.estacion == Estacion.INVIERNO || m.ficha.estacion == Estacion.OTONO) && (m.hora < 8.0 || m.hora >= 16.0)) || (m.hora >= 3.5 && m.hora < 6.5))

    internal fun mojado(m: Momento) = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
    internal fun nevado(m: Momento) = m.ficha.clima == Clima.NIEVE

    /** Cuánto sopla el viento, de 0.22 (calma) a 1: lo que traiga el tiempo del día y, el día del vendaval, todo. */
    internal fun viento(m: Momento): Float = 0.22f + 0.78f * (if (vendaval(m)) 1f else m.viento)

    /** El viento con su signo, para el humo: sopla del mar, valle arriba, hacia la izquierda (como las ráfagas de `tiempoVivo`). */
    internal fun soplo(m: Momento): Float = -viento(m)

    /** Sopla tanto que tumba el humo, vence los árboles y se lleva hojas, papeles y lonas: el vendaval, el clima de viento y la tormenta. */
    internal fun ventarron(m: Momento): Boolean = viento(m) > 0.8f

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del momento con el velo de este lugar: el cielo de todos no se retoca (guía, sección 5); se le pone encima
     * el humo del carbón, que dora el horizonte y acerca la bruma. Con los atardeceres de Krakatoa, el poniente se
     * enrojece.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces()
        val dia = 1f - l.noche
        fun humo(c: Color, propio: Color, k: Float) = lerp(c, Color(propio.red * l.ambiente.red, propio.green * l.ambiente.green, propio.blue * l.ambiente.blue), k)
        val ocaso = if (krakatoa(m)) ((0.62f - abs(l.solAltura - 0.1f)) / 0.62f).coerceIn(0f, 1f) * (1f - 0.8f * l.nublado) * (if (m.hora > 12.0) 1f else 0.5f) * (1f - 0.4f * l.noche) else 0f
        val rojo = Color(0xFFE2543A)
        return Luces(
            alto = lerp(humo(l.alto, Color(0xFFA6AEB4), 0.2f), Color(0xFF8A4A5A), 0.55f * ocaso), medio = lerp(humo(l.medio, Color(0xFFCFC8B2), 0.26f), rojo, 0.75f * ocaso),
            bajo = lerp(humo(l.bajo, Color(0xFFE8D8AE), 0.3f), Color(0xFFFF7A3A), 0.8f * ocaso),
            ambiente = lerp(lerp(l.ambiente, Color(l.ambiente.red * C.LUZ.red, l.ambiente.green * C.LUZ.green, l.ambiente.blue * C.LUZ.blue), 0.6f * dia), Color(0xFFF2B090), 0.3f * ocaso),
            bruma = lerp(humo(l.bruma, Color(0xFFCDC4B0), 0.4f), rojo, 0.4f * ocaso), brumaFuerza = (l.brumaFuerza + 0.05f).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = lerp(l.solColor, rojo, 0.5f * ocaso), solFuerza = l.solFuerza * 0.9f, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas * 0.9f, noche = l.noche, nublado = l.nublado, eclipse = l.eclipse,
        )
    }

    /** El rayo del sol con la sombra de este lugar: azul violeta. */
    internal fun rayo(l: Luces): Rayo { val r = Rayo(l); return Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza, lerp(r.color, C.SOMBRA, 0.5f), r.contraste) }

    /** La farola de gas de la puerta de la cochera. */
    internal val FAROLA = Offset(92f, 150f)

    /** La farola del poste, junto a la señal. */
    internal val FAROL_POSTE = Offset(342f, 160f)

    /** La puerta del hogar, dentro de la cabina. */
    internal val HOGAR = MaquinaVapor.ext(14f, -36f)

    /** El brasero del patio. */
    internal val BRASERO = Offset(170f, 274f)

    /** Cuánta luz da el hogar, de 0 a 1. */
    internal fun lumbre(m: Momento): Float = if (!enElDeposito(m)) 0f else when (fuego(m)) { Fuego.DORMIDO -> 0.5f; Fuego.ENCENDIENDO -> 0.9f; Fuego.A_PRESION -> 0.7f }

    /** Las luces que alumbran el patio en este momento. */
    internal fun focos(m: Momento): List<Foco> {
        val l = luces(m)
        val k = ((l.noche - 0.2f) / 0.6f).coerceIn(0f, 1f)
        if (k <= 0f) return emptyList()
        return buildList {
            add(Foco(Offset(FAROLA.x, 178f), C.GAS, 0.66f * k, 82f, suelo = 244f))
            add(Foco(Offset(FAROL_POSTE.x, 190f), C.GAS, 0.6f * k, 78f, suelo = 250f))
            add(Foco(Offset(197f, 146f), lerp(C.GAS, Color.White, 0.2f), 0.2f * k, 54f, suelo = 190f))                 // las ventanas de la caseta
            add(Foco(Offset(56f, 184f), C.GAS, 0.36f * k, 44f, suelo = 214f))                                          // la luz que sale por la puerta de la cochera
            if (lumbre(m) > 0f) add(Foco(Offset(HOGAR.x, HOGAR.y + 8f), C.FUEGO, 1.05f * lumbre(m) * k, 58f, suelo = RIEL + 12f))
            if (brasero(m)) add(Foco(BRASERO + Offset(0f, -30f), C.FUEGO, 0.75f * k, 50f, suelo = BRASERO.y))
        }
    }

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val r = rayo(l)
        val semilla = m.ficha.semilla
        val p = PincelVapor(l, r, Alumbrado(l, focos(m)))

        // 1. El cielo.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)

        // 2. La lejanía: los páramos, el mar, el pueblo, el viaducto.
        with(ValleVapor) { valle(m, l, r) }

        // La niebla envuelve el valle, no el patio de delante: su velo va antes del depósito.
        val niebla = m.ficha.clima == Clima.NIEBLA
        if (niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)

        // 3. El depósito y, delante, la máquina y las cosas del patio.
        with(DepositoVapor) { deposito(m, p) }

        // 4. El velo del tiempo que hace y el grano del papel.
        if (!niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("vapor", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el humo y el vapor no paran

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m); val r = rayo(l)
        with(ValleVapor) { valleVivo(m, l, r, reloj) }
        with(DepositoVapor) { depositoVivo(m, l, r, reloj) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    /** De día, la luz del sol; de noche, la de la farola, el hogar o el brasero que tenga más cerca. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val f = focos(m)
        if (f.isEmpty() || l.noche < 0.5f) return LuzGato(rayo(l), l.noche) { l.tono(it) }
        val a = Alumbrado(l, f)
        val manda = f.maxByOrNull { it.fuerza / (1f + ((it.p.x - en.x) * (it.p.x - en.x)) / (it.alcance * it.alcance)) }!!
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayoDeFoco(manda, en, 0.7f, C.SOMBRA), l.noche, manda.color) { lerp(l.tono(it), a.en(centro, it), 0.6f) }
    }

    override fun estorbos(m: Momento): List<Float> = DepositoVapor.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = DepositoVapor.anclas(m)
}
