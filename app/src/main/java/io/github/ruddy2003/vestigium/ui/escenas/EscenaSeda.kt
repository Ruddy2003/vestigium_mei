package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.vidas.Clima

/**
 * La paleta de la Ruta de la Seda: quince colores con nombre para los dos cuadros (trece de materia y dos de luz), y
 * nueve mezclas de ellos. Los dos cuadros comparten el pelo de los camellos, el fieltro rojo, el añil y el bronce, que
 * es lo que los hace del mismo viaje; Chang'an añade solo el gris de las tejas.
 */
internal object ColorSeda {
    // ---- Trece colores de materia. Manda el ocre de la tierra y lo acompaña el rojo vino del fieltro.
    val ADOBE = Color(0xFFC6A072)
    val ARENA = Color(0xFFE2C08A)
    val ROCA = Color(0xFF8C7880)
    val PELO = Color(0xFFB88E5C)
    val FIELTRO = Color(0xFF8E3A32)
    val ANIL = Color(0xFF3E6682)
    val VERDE = Color(0xFF75935A)
    val MADERA = Color(0xFF9C764A)
    val MADERA_OSC = Color(0xFF5C4232)
    val BRONCE = Color(0xFFC89A46)
    val CAL = Color(0xFFEFE6D2)
    val HOLLIN = Color(0xFF2C2624)
    val TEJA = Color(0xFF7E848C)

    // ---- Las dos luces: la lumbre y los faroles, y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFB45E)
    val SOMBRA = Color(0xFF7E76AE)

    // ---- Lo demás no son colores nuevos: son mezclas de los de arriba, con nombre para no repetir la cuenta.
    val LOESS = lerp(ARENA, BRONCE, 0.36f)
    val NIEVE = lerp(CAL, Color.White, 0.45f)
    val PELO_OSC = lerp(PELO, MADERA_OSC, 0.62f)
    val VERDE_OSC = lerp(VERDE, HOLLIN, 0.42f)
    val LADRILLO = lerp(ADOBE, PELO, 0.3f)
    val TAPIA = lerp(ADOBE, CAL, 0.56f)
    val LACA = lerp(FIELTRO, BRONCE, 0.16f)
    val LUNA = lerp(SOMBRA, Color.White, 0.62f)                             // lo que alumbra la luna
    val NOCHE = lerp(ANIL, HOLLIN, 0.5f)                                    // el azul de las sombras de noche
}

/**
 * El color de una cosa por su lado de luz, por su lado de sombra y de frente, con el sol [r] de este momento.
 * [calida], de 0 a 1: de noche, cuánto la alumbra además la lumbre o un farol, que es cálido.
 */
internal class SolSeda(val l: Luces, val r: Rayo, val calida: Float = 0f) {
    fun con(calida: Float) = SolSeda(l, r, calida.coerceIn(0f, 1f))
    private fun deLumbre(c: Color) = Color(c.red, c.green * 0.74f, c.blue * 0.44f, c.alpha)
    private fun mas(base: Color, c: Color, k: Float) = if (calida <= 0f) base else lerp(base, deLumbre(c), (calida * k).coerceIn(0f, 1f))
    fun luz(c: Color, lejos: Float = 0f) = mas(l.tono(lerp(c, Color(0xFFFFF2D0), 0.34f * r.contraste), lejos), c, 0.85f)
    fun medio(c: Color, lejos: Float = 0f) = mas(l.tono(c, lejos), c, 0.5f)
    fun sombra(c: Color, lejos: Float = 0f) = mas(l.tono(mult(c, r.color, 0.85f * r.contraste), lejos), c, 0.14f)
    /** La cara que mira a quien mira. */
    fun frente(c: Color, lejos: Float = 0f) = mas(if (r.lat < 0f) l.tono(lerp(c, Color(0xFFFFF2D0), 0.14f * r.contraste), lejos) else l.tono(mult(c, r.color, 0.4f * r.contraste), lejos), c, 0.5f)
    /** La cara que mira a la derecha; [alReves], la que mira a la izquierda. */
    fun lado(c: Color, lejos: Float = 0f, alReves: Boolean = false) = if ((r.lat < 0f) != alReves) sombra(c, lejos) else luz(c, lejos)
    /** El sol está a la derecha del cuadro. */
    val solALaDerecha: Boolean get() = r.lat > 0f
}

/**
 * La Ruta de la Seda: el mundo de Nanai-vandak, mercader sogdiano de caravana, del 750 al 754. Son **dos cuadros** en
 * una misma escena, como Pirata y Roma; cuál toca lo decide [lugar], que lee el estado del calendario.
 *
 * 1. **El camino** ([CampamentoSeda], [PaisajesSeda]): casi todo el año. Se mira desde debajo del toldo de fieltro de
 *    la caravana, junto a la puerta de un caravasar: delante, la alfombra con el objeto del día, los fardos, la lumbre
 *    y los camellos; detrás, el paisaje que diga el arco `ruta` ([paisaje]): los valles y las montañas nevadas cerca de
 *    Samarcanda, el desierto de dunas con su oasis en medio, y el corredor del oeste y las colinas de loess cerca de
 *    Chang'an. «Todos los caravasares son iguales», dice el cuento: por eso el primer plano es el mismo y lo que
 *    cambia es lo de detrás. Es **el primer cuadro de desierto** (`docs/diseno/ESCENAS.md`, sección 5, «Sin agua»).
 * 2. **Chang'an** ([ChanganSeda], estado `changan`, de abril a septiembre de las temporadas 1, 3 y 5): el mercado del
 *    oeste, visto desde el soportal de la tienda de Shi Ping.
 *
 * **Samarcanda** (estado `samarcanda`, temporadas 2 y 4) tendrá su cuadro propio más adelante (el plan lo deja para
 * después). Mientras llega, esos días se pinta el cuadro del camino a las puertas de la ciudad: su muralla de adobe,
 * su ciudadela y sus viñas al fondo, y los camellos descargados. **El cuadro propio entra en [quieta], [viva],
 * [anclas] y [estorbos], en la rama `Lugar.SAMARCANDA`.**
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG3.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **Dónde está** ([lugar]) y **qué paisaje cruza** ([ruta], [paisaje], [pesos]).
 * - **La marcha** ([enMarcha]): a las horas de camino los camellos están de pie y cargados y la fila de la caravana
 *   pasa por la pista; en los altos, echados y con la carga en el suelo. **La lumbre** ([lumbre]) arde en los altos.
 * - **Farn** viaja con él desde que salen de Samarcanda en la temporada 2 hasta que se queda en Chang'an en la 3
 *   ([conFarn]): un segundo petate. **El camello blanco**, desde que lo compra en Kashgar en la 4 ([camelloBlanco]).
 * - **Los días especiales:** la fiesta (banderines y faroles), las linternas, la tormenta de arena negra
 *   ([tormentaNegra]), la nieve ([nevado]), el ataque de los bandidos (lanzas y un escudo junto a la puerta, [guerra]),
 *   la lámpara de las cuevas de Dunhuang ([mogao]) y el camello que se muere ([camelloMuerto]).
 * - **En Chang'an:** el mercado abre a mediodía y cierra antes de anochecer ([mercadoAbierto]); la crecida del Wei
 *   ([crecida]), el barco dragón, Qixi y la luna de otoño.
 */
internal object EscenaSeda : EscenaVida {
    override val vidas = listOf("mercader_sogdiano")

    private val C = ColorSeda

    // ---------------------------------------------------------------- Dónde está

    /** Dónde está Nanai-vandak: en el camino o pasando el verano en una de las dos ciudades. */
    internal enum class Lugar { CAMINO, CHANGAN, SAMARCANDA }

    /** Qué cuadro toca hoy: lo vigila `EscenaSedaPrueba`. */
    internal fun lugar(m: Momento): Lugar = when (m.estado) {
        "changan" -> Lugar.CHANGAN
        "samarcanda" -> Lugar.SAMARCANDA
        else -> Lugar.CAMINO
    }

    /** Los tres paisajes del camino. */
    internal enum class Paisaje { MONTANA, DESIERTO, LOESS }

    /** Dónde está en la ruta: 0 = Samarcanda, 1 = Chang'an. */
    internal fun ruta(m: Momento): Float = m.arco("ruta").coerceIn(0f, 1f)

    /** El paisaje de un punto de la ruta, con los mismos cortes que usa el cuento (`PiezasNanai.ENTORNOS`). */
    internal fun paisajeDe(ruta: Float): Paisaje = when {
        ruta < 0.35f -> Paisaje.MONTANA
        ruta < 0.72f -> Paisaje.DESIERTO
        else -> Paisaje.LOESS
    }

    internal fun paisaje(m: Momento): Paisaje = paisajeDe(ruta(m))

    private fun suave(x: Float): Float { val t = x.coerceIn(0f, 1f); return t * t * (3f - 2f * t) }

    /**
     * Cuánto hay de cada paisaje en un punto de la ruta: suman 1. Entre uno y otro hay un tramo en que se mezclan (las
     * dunas asoman cuando las lomas todavía no se han ido), para que el cuadro no cambie de golpe de un día a otro.
     * [alto]: en la montaña, cuánto es paso de altura (1) y cuánto valle con huertos (0). [capital]: en el loess,
     * cuánto son ya las colinas de cerca de la capital (1) y cuánto el corredor de los fuertes (0).
     */
    internal class Pesos(val montana: Float, val desierto: Float, val loess: Float, val alto: Float, val capital: Float)

    internal fun pesos(ruta: Float): Pesos {
        val wm = suave((0.42f - ruta) / 0.14f); val wl = suave((ruta - 0.65f) / 0.14f)
        return Pesos(wm, (1f - wm - wl).coerceIn(0f, 1f), wl, suave((ruta - 0.08f) / 0.1f), suave((ruta - 0.86f) / 0.08f))
    }

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Farn va con la caravana: sale con él de Samarcanda en la temporada 2 y se queda en Chang'an en la 3. */
    internal fun conFarn(m: Momento): Boolean = paso(m, 2, "09-28") && !paso(m, 3, "09-28")

    /** El camello blanco de dos jorobas, que compra en Kashgar. */
    internal fun camelloBlanco(m: Momento): Boolean = paso(m, 4, "03-05")

    /**
     * Hacia dónde va la caravana. Los años que pasa el verano en Chang'an (temporadas 1, 3 y 5) va hacia el este la
     * primera mitad del año y vuelve hacia el oeste la segunda; los de Samarcanda (2 y 4), al revés. Hacia el este
     * lleva lo del oeste (vidrio, plata, almizcle, vino, tapetes); hacia el oeste, la seda.
     */
    internal fun haciaElEste(m: Momento): Boolean = (m.ficha.dia.substring(5, 7).toInt() <= 6) == (m.ficha.temporada % 2 == 1)

    internal fun nevado(m: Momento): Boolean = m.con("nieve") || m.ficha.clima == Clima.NIEVE
    internal fun tormentaNegra(m: Momento): Boolean = m.con("polvo")
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")
    internal fun linternas(m: Momento): Boolean = m.con("linternas")
    /**
     * Hay peligro en el campamento: la clave de escena «guerra» o el día que los bandidos atacan la caravana. Las
     * noticias de guerras lejanas (Talas, el Tíbet) llegan de palabra y no se ven.
     */
    internal fun guerra(m: Momento): Boolean = m.con("guerra") || hoyEs(m, "bandidos")
    internal fun luto(m: Momento): Boolean = m.con("luto")
    internal fun mogao(m: Momento): Boolean = hoyEs(m, "mogao")
    internal fun camelloMuerto(m: Momento): Boolean = hoyEs(m, "camello")

    /**
     * La caravana está en marcha: en el camino, a las horas de andar. Con nieve en el paso o con la tormenta de arena
     * se espera en el caravasar, y en las ciudades los camellos descansan.
     */
    internal fun enMarcha(m: Momento): Boolean = lugar(m) == Lugar.CAMINO && m.hacer == Hacer.CAMINO && !m.con("nieve") && !tormentaNegra(m)

    /**
     * La lumbre del alto, de 0 (apagada: se va de camino) a 1 (viva). De madrugada queda el rescoldo. Con la tormenta
     * de arena no hay quien la tenga encendida.
     */
    internal fun lumbre(m: Momento): Float = when {
        lugar(m) == Lugar.CHANGAN -> 0f
        enMarcha(m) || tormentaNegra(m) -> 0f
        m.hacer == Hacer.DORMIR -> if (m.hora >= 21.0 && m.hora < 23.5) 0.85f else 0.35f
        else -> 1f
    }

    /** El farol del palo y la lámpara del nicho, encendidos de noche. */
    internal fun farol(m: Momento): Boolean = m.luces().noche > 0.35f

    // ---- Chang'an

    /**
     * El mercado del oeste abre a mediodía con los tambores y cierra con los gongs una hora y tres cuartos antes de
     * anochecer (Wikipedia, «Chang'an»): con el sol poniéndose a las 20:24, a las 18:40. El día de la crecida no abre.
     */
    internal fun mercadoAbierto(m: Momento): Boolean = lugar(m) == Lugar.CHANGAN && m.hora >= 12.0 && m.hora < 18.6 && !crecida(m)

    /** El día que el río Wei entra en el mercado. */
    internal fun crecida(m: Momento): Boolean = hoyEs(m, "crecida_wei")
    internal fun duanwu(m: Momento): Boolean = hoyEs(m, "duanwu")
    internal fun qixi(m: Momento): Boolean = hoyEs(m, "qixi")
    internal fun lunaDeOtono(m: Momento): Boolean = hoyEs(m, "zhongqiu")

    /** Los faroles del soportal, encendidos al caer la tarde mientras hay alguien despierto. */
    internal fun farolesDeLaTienda(m: Momento): Boolean = m.luces().noche > 0.3f && (m.hacer != Hacer.DORMIR || m.hora in 21.0..23.0)

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del lugar. En el camino, el cielo de `HITOS` con el polvo del desierto: el horizonte más cálido y más
     * calima cuanto más desierto; de noche el aire es limpio y la luz de fondo no llega al negro, para que se lean
     * las dunas y los camellos. En Chang'an, el aire pesado del verano y, de noche, el resplandor de la ciudad.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        if (lugar(m) == Lugar.CHANGAN) return Luces(
            alto = l.alto, medio = lerp(l.medio, Color(0xFFEFE2C6), 0.14f * dia), bajo = lerp(l.bajo, Color(0xFFF2DDB0), 0.26f * dia),
            ambiente = lerp(l.ambiente, Color(0xFF9692C4), 0.54f * n), bruma = lerp(lerp(l.bruma, Color(0xFFE8D8B8), 0.36f * dia), Color(0xFF4C5488), 0.55f * n), brumaFuerza = (l.brumaFuerza * 1.05f).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
        val p = pesos(ruta(m)); val seco = (p.desierto + 0.6f * p.loess).coerceIn(0f, 1f)
        return Luces(
            alto = l.alto, medio = lerp(l.medio, Color(0xFFF0DDB6), 0.18f * dia * seco), bajo = lerp(l.bajo, Color(0xFFF4D9A2), 0.42f * dia * seco),
            ambiente = lerp(l.ambiente, Color(0xFF9494C8), 0.56f * n), bruma = lerp(lerp(l.bruma, Color(0xFFECD4A4), 0.5f * dia * seco), Color(0xFF5A5A92), 0.55f * n), brumaFuerza = l.brumaFuerza,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    // ---------------------------------------------------------------- Los cuadros

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.CAMINO -> with(CampamentoSeda) { cuadro(m) }
            Lugar.SAMARCANDA -> with(CampamentoSeda) { cuadro(m) }              // hasta que llegue su cuadro propio
            Lugar.CHANGAN -> with(ChanganSeda) { cuadro(m) }
        }
    }

    override fun seMueve(m: Momento): Boolean = when (lugar(m)) {
        Lugar.CAMINO -> CampamentoSeda.seMueve(m)
        Lugar.SAMARCANDA -> CampamentoSeda.seMueve(m)
        Lugar.CHANGAN -> ChanganSeda.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (lugar(m)) {
            Lugar.CAMINO -> with(CampamentoSeda) { vivo(m, reloj) }
            Lugar.SAMARCANDA -> with(CampamentoSeda) { vivo(m, reloj) }
            Lugar.CHANGAN -> with(ChanganSeda) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    /** De día lo alumbra el sol. De noche, la lumbre del campamento o los faroles de la tienda, que lo tiñen de cálido por su lado. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val r = Rayo(l)
        val calida = when (lugar(m)) {
            Lugar.CAMINO -> CampamentoSeda.calor(m, en)
            Lugar.SAMARCANDA -> CampamentoSeda.calor(m, en)
            Lugar.CHANGAN -> ChanganSeda.calor(m, en)
        }
        val s = SolSeda(l, r, calida)
        return LuzGato(r, l.noche, lerp(Color(0xFFFFF0D0), C.LUMBRE, calida)) { s.medio(it) }
    }

    override fun estorbos(m: Momento): List<Float> = when (lugar(m)) {
        Lugar.CAMINO -> CampamentoSeda.estorbos(m)
        Lugar.SAMARCANDA -> CampamentoSeda.estorbos(m)
        Lugar.CHANGAN -> ChanganSeda.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (lugar(m)) {
        Lugar.CAMINO -> CampamentoSeda.anclas(m)
        Lugar.SAMARCANDA -> CampamentoSeda.anclas(m)
        Lugar.CHANGAN -> ChanganSeda.anclas(m)
    }
}
