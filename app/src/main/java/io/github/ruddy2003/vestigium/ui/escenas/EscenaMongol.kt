package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.vidas.Clima
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * La paleta de Mongol: dieciséis colores de materia y dos de luz, para los dos cuadros. Manda el crema del fieltro de
 * las tiendas; lo acompañan el ocre de la hierba seca (en el valle) o el verde apagado (en el verano) y el azul de las
 * montañas y del hielo. Los acentos se repiten: el rojo de la puerta vuelve en los cofres de las carretas; el azul de
 * las cintas del ovoo, en las costuras de la puerta y en el río; el cuero del odre, en los arreos y en los caballos
 * bayos; el hollín del aro, en el estiércol del corral.
 *
 * El rojo, el azul y el oro salen del tema y del gato de pixel de Mongol (gorro rojo, collar azul con colgante de oro);
 * el cuento solo dice «fieltro blanco» **[Diseño propio]**.
 */
internal object ColorMongol {
    // ---- Dieciséis colores de materia.
    val FIELTRO = Color(0xFFEDE3CB)
    val HOLLIN = Color(0xFF2E2826)
    val MADERA = Color(0xFF9C6C44)
    val ROJO = Color(0xFFA8432E)
    val CUERO = Color(0xFF7C4E2E)
    val LANA = Color(0xFFDDD3BE)
    val ESTEPA = Color(0xFFB9A170)
    val VERDE = Color(0xFF7E9858)
    val TIERRA = Color(0xFF8A7058)
    val PIEDRA = Color(0xFF9C948A)
    val AZUL = Color(0xFF3B6AA4)
    val ORO = Color(0xFFC8963A)
    val NIEVE = Color(0xFFF2F2EE)
    val HIELO = Color(0xFFBCD4DA)
    val MONTE = Color(0xFF6F7FA2)
    val RIO = Color(0xFF5B8D96)

    // ---- Las dos luces: la del fuego del centro de la tienda (y de las lumbres de fuera), y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFA650)
    val SOMBRA = Color(0xFF7C74B0)

    // ---- Lo demás no son colores nuevos: son mezclas de los de arriba, con nombre para no repetir la cuenta.
    val FIELTRO_VIEJO = lerp(FIELTRO, TIERRA, 0.28f)
    val CRIN = lerp(HOLLIN, CUERO, 0.42f)
    val ESTIERCOL = lerp(TIERRA, HOLLIN, 0.45f)
    val MADERA_OSC = lerp(MADERA, HOLLIN, 0.5f)
    val NIEVE_SOMBRA = lerp(NIEVE, SOMBRA, 0.42f)
    val RIO_HONDO = lerp(RIO, HOLLIN, 0.42f)
    val LUNA = lerp(SOMBRA, Color.White, 0.62f)
    val CABEZA = lerp(HOLLIN, CUERO, 0.25f)                                     // la cabeza oscura de las ovejas
}

/**
 * Mongol: el mundo de Bat, pastor nómada a orillas del Kerulen, en la estepa mongola, de 1220 a 1224. Son **dos cuadros**
 * en una misma escena, como Prehistoria y la Ruta de la Seda; cuál toca lo decide [lugar], que lee el estado del
 * calendario.
 *
 * 1. **El valle de invierno** ([ValleMongol]), del 25 de septiembre al 24 de mayo: un sitio cerrado y al abrigo. Se mira
 *    de pie junto a la tienda de Bat, valle adentro: la tienda enmarca por la izquierda y por arriba, el cerro con el
 *    ovoo cierra por detrás, el río (helado la mitad del año) abre por la derecha; en medio, las otras tiendas al pie
 *    del cerro, los corrales de piedra y estiércol con las ovejas y los caballos.
 * 2. **El campamento de verano** ([VeranoMongol], estado `verano`, del 25 de mayo al 24 de septiembre): un sitio abierto,
 *    de cielo grande. Se mira de pie entre una carreta y la tienda de Bat, por el pasillo que queda entre las dos, hacia
 *    el río ancho del llano; la cuerda larga de las yeguas lleva el ojo al fondo.
 *
 * Las piezas que usan los dos (la tienda, la carreta, las ovejas, los caballos, el ovoo, el odre, el corral) están en
 * [PiezasMongol].
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG4.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **Dónde está** ([lugar]) y **las tiendas del campamento** ([tiendas]): la de la madre hasta el día en que muere
 *   ([tiendaDeLaMadre]); su sitio vacío y quemado desde entonces ([sitioDeLaMadre]); la de Temür desde que vuelve
 *   ([tiendaDeTemur]).
 * - **El rebaño** ([rebano], [ovejas], [caballos]): cuántos animales tiene; baja en el invierno malo de la temporada 4.
 * - **La hierba** ([pasto], [flores]), **la nieve** ([nieve], [helada]) y **el río** ([hielo], [rioHelado], [deshielo]).
 * - **Los días señalados:** la fiesta ([fiesta]), el luto ([luto]), las noticias de la guerra ([guerra],
 *   [caballoAjeno]), la mudanza ([partida], [armado], [carretasCargadas]), las faenas del año ([faena]), el ovoo
 *   ([diaDelOvoo], [cintasDelOvoo]), la primera leche ([primeraLeche]), la noche más larga ([nocheLarga],
 *   [dondeSeJuntan]) y los sucesos que dejan algo a la vista ([potroBlanco], [cuna], [pielDeLobo], [liebre],
 *   [carrera], [monjeDePaso], [fuegoDeMonkh], [madreEnferma]).
 * - **El horario de Bat** («Un día suyo»): el fuego del centro de la tienda ([fuegoDentro]) y su humo, la puerta
 *   ([puerta]), la lumbre de fuera ([fuegoFuera]), dónde están las ovejas ([ovejasCerca]), el Negro atado junto a la
 *   puerta ([negroAtado]), las yeguas en la cuerda larga ([yeguasEnLaCuerda]) y lo que cuelga de la cuerda del verano
 *   ([colgado]).
 */
internal object EscenaMongol : EscenaVida {
    override val vidas = listOf("pastor_mongol")
    /** La franja del fondo del teléfono ([franjaFondo]): en el valle, su tienda y el cerro del ovoo; en el verano, la tienda, la carreta y el río. */
    override val franjaFondo: Float get() = 0.45f

    private val C = ColorMongol

    // ---------------------------------------------------------------- Dónde está

    /** Dónde vive el campamento: en el valle de invierno, al abrigo del cerro, o en el llano del verano, junto al río. */
    internal enum class Lugar { VALLE, VERANO }

    /** Qué cuadro toca hoy: lo vigila `EscenaMongolPrueba`. */
    internal fun lugar(m: Momento): Lugar = when (m.estado) {
        "verano" -> Lugar.VERANO
        else -> Lugar.VALLE
    }

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** El día del año de [md] («MM-DD») en un año sin bisiesto, de 1 a 365. */
    private fun diaDelAnio(md: String): Int = LocalDate.parse("2027-$md").dayOfYear

    /** El valor en el día [md] de una curva dada por puntos («MM-DD» → valor), en línea recta entre uno y otro, que da la vuelta al año. */
    private fun curvaDelAnio(md: String, puntos: List<Pair<String, Float>>): Float {
        val d = diaDelAnio(if (md == "02-29") "02-28" else md)
        val p = puntos.map { diaDelAnio(it.first) to it.second }
        val i = p.indexOfLast { it.first <= d }
        val a = if (i < 0) p.last().let { (it.first - 365) to it.second } else p[i]
        val b = if (i < 0) p.first() else p.getOrNull(i + 1) ?: p.first().let { (it.first + 365) to it.second }
        if (b.first == a.first) return a.second
        return a.second + (b.second - a.second) * ((d - a.first).toFloat() / (b.first - a.first))
    }

    // ---------------------------------------------------------------- Las tiendas del campamento

    /**
     * Las tiendas del campamento: «seis tiendas» en invierno y en verano (`PiezasBat.kt`, el valle y el llano). Son la de
     * Bat, la de su madre, la de Bolad, la de la madre de Bolad («la tienda más grande»), la del chamán («una tienda
     * pequeña, con tambores y figuras de fieltro colgadas») y la de otra familia **[Diseño propio]**; desde que vuelve,
     * la de Temür, «junto a la de» Bat.
     */
    internal enum class Tienda { BAT, MADRE, BOLAD, MADRE_BOLAD, CHAMAN, VECINO, TEMUR }

    /** La madre «vive en su propia tienda, junto a la de él» hasta que muere (temporada 4, 8 de marzo): «se quemó con sus cosas». */
    internal fun tiendaDeLaMadre(m: Momento): Boolean = !paso(m, 4, "03-08")

    /** Desde que muere la madre, «su lugar en el campamento está vacío»: en el valle queda la tierra quemada donde estuvo su tienda. */
    internal fun sitioDeLaMadre(m: Momento): Boolean = !tiendaDeLaMadre(m) && lugar(m) == Lugar.VALLE

    /** Temür vuelve de la guerra (temporada 5, 28 de julio) y «vive en una tienda junto a la de» Bat. */
    internal fun tiendaDeTemur(m: Momento): Boolean = paso(m, 5, "07-28")

    /** Las tiendas que hay hoy en el campamento, en el orden de [Tienda]. */
    internal fun tiendas(m: Momento): List<Tienda> = Tienda.entries.filter {
        when (it) {
            Tienda.BAT, Tienda.BOLAD, Tienda.MADRE_BOLAD, Tienda.CHAMAN, Tienda.VECINO -> true
            Tienda.MADRE -> tiendaDeLaMadre(m)
            Tienda.TEMUR -> tiendaDeTemur(m)
        }
    }

    /** La madre, enferma de la tos del invierno, «ya no sale de su tienda» (de `madre_tos` a `muere_madre`): su puerta no se abre. */
    internal fun madreEnferma(m: Momento): Boolean = paso(m, 3, "01-30") && tiendaDeLaMadre(m)

    // ---------------------------------------------------------------- El rebaño y la hierba

    /** Cuántos animales tiene, de 0 a 1 (el arco `rebano`): baja en el invierno malo de la temporada 4 y vuelve a crecer. */
    internal fun rebano(m: Momento): Float = m.arco("rebano").coerceIn(0f, 1f)

    /** Cuántas ovejas se pintan en el rebaño: proporcional al arco (46 en lo más alto, 27 en el fondo del invierno malo). */
    internal fun ovejas(m: Momento): Int = (rebano(m) * 54f).roundToInt()

    /** Cuántos caballos del campamento se ven, sin contar el Negro: también según el arco. */
    internal fun caballos(m: Momento): Int = (rebano(m) * 10f).roundToInt()

    /** La hierba de la estepa, de 0 a 1 (el arco `pasto`): 0 del 1 de noviembre al 15 de abril; alta y verde en agosto. */
    internal fun pasto(m: Momento): Float = m.arco("pasto").coerceIn(0f, 1f)

    /** Cuántas flores hay en la hierba, de 0 a 1: el cuento las nombra con la hierba alta (el arco por encima de 0.5). */
    internal fun flores(m: Momento): Float = ((pasto(m) - 0.45f) / 0.35f).coerceIn(0f, 1f)

    // ---------------------------------------------------------------- La nieve y el río

    /**
     * La curva de la nieve en el valle a lo largo del año. **[Aproximación]**: el calendario no tiene arco de nieve; la
     * regla se ajusta a lo que el cuento ata a fechas: el pasto en 0 del 1 de noviembre al 15 de abril («parda y seca,
     * o blanca de nieve»), «descalzo en la nieve dura» de noviembre a marzo, «ya hiela de día» en la matanza (28 de
     * noviembre), «corderos en la nieve» en la parición (del 25 de marzo al 13 de abril) y las primeras heladas, sin
     * nieve, el día que bajan al valle (25 de septiembre).
     */
    private val NIEVE_DEL_ANIO = listOf(
        "01-01" to 0.9f, "02-28" to 0.88f, "03-20" to 0.7f, "04-05" to 0.38f, "04-20" to 0.1f, "05-01" to 0f,
        "10-15" to 0f, "11-01" to 0.25f, "11-20" to 0.65f, "12-10" to 0.9f,
    )

    /**
     * Cuánta nieve cubre el valle, de 0 a 1 (en el llano del verano, nunca). La de la curva del año; un día que nieva,
     * al menos 0.85; el día de la costra de hielo (clave `nieve`), toda; un día de lluvia de primavera se come la mitad.
     */
    internal fun nieve(m: Momento): Float {
        if (lugar(m) == Lugar.VERANO) return 0f
        if (m.con("nieve")) return 1f
        val base = curvaDelAnio(mesDia(m), NIEVE_DEL_ANIO)
        return when (m.ficha.clima) {
            Clima.NIEVE -> maxOf(base, 0.85f)
            Clima.LLUVIA -> base * 0.5f
            else -> base
        }.coerceIn(0f, 1f)
    }

    /** La escarcha de las mañanas frías, cuando no hay nieve: «Las primeras heladas blanquean la hierba». De septiembre a mayo, hasta las diez. */
    internal fun helada(m: Momento): Boolean {
        if (lugar(m) == Lugar.VERANO) return mesDia(m) >= "09-20" && m.hora < 9.0
        val md = mesDia(m)
        return (md >= "09-25" || md < "05-15") && nieve(m) < 0.3f && m.hora < 10.0
    }

    /** Cuánto del río está helado, de 0 a 1: «helado la mitad del año», de noviembre a abril, con unos días de paso a cada lado. */
    internal fun hielo(m: Momento): Float =
        if (lugar(m) == Lugar.VERANO) 0f else curvaDelAnio(mesDia(m), listOf("04-05" to 1f, "04-25" to 0f, "11-01" to 0f, "11-20" to 1f))

    internal fun rioHelado(m: Momento): Boolean = hielo(m) >= 0.5f

    /** El deshielo: el río suelta las placas de hielo, del 5 al 25 de abril. */
    internal fun deshielo(m: Momento): Boolean = lugar(m) == Lugar.VALLE && mesDia(m) >= "04-05" && mesDia(m) < "04-25"

    // ---------------------------------------------------------------- Los días señalados

    /** El mes blanco, la boda de la hija de Bolad y la vuelta de Temür: ropa nueva, visitas, cintas azules. */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    /** La muerte de la madre y las de los generales del kan: un luto discreto **[Diseño propio]**: la puerta cerrada, nada colgado. */
    internal fun luto(m: Momento): Boolean = m.con("luto")

    /** Las noticias de la guerra del oeste, que trae un jinete del correo «con caballo de refresco»: se bebe por la victoria. */
    internal fun guerra(m: Momento): Boolean = m.con("guerra")

    /** Un caballo ajeno, ensillado, atado junto a la tienda: el del correo los días de noticias y el día que pasan los del correo del kan. */
    internal fun caballoAjeno(m: Momento): Boolean = guerra(m) || hoyEs(m, "correo")

    /** Los dos días de mudanza (clave `partida`): el 25 de mayo al llano y el 25 de septiembre al valle. */
    internal fun partida(m: Momento): Boolean = m.con("partida")

    /**
     * Cuánto están armadas las tiendas, de 0 (solo el armazón: la celosía y las varas) a 1 (con todo el fieltro). El día
     * de la mudanza el cuadro es el del sitio al que llegan (lo dice la ficha) y las tiendas se van armando a lo largo
     * de la tarde: «al caer la tarde las tiendas ya están armadas» **[Diseño propio]**: el cuento cuenta la mañana en el
     * sitio de donde se van; el cuadro la pinta en el sitio adonde llegan.
     */
    internal fun armado(m: Momento): Float = if (!partida(m)) 1f else ((m.hora.toFloat() - 12f) / 6f).coerceIn(0f, 1f)

    /** Las carretas, cargadas: el día de la mudanza; en la esquila, con los fardos de lana; después de la matanza, con la carne tapada con fieltro. */
    internal fun carretasCargadas(m: Momento): Boolean = partida(m) || faena(m) == Faena.ESQUILA || faena(m) == Faena.MATANZA

    /** Las faenas del año (clave `cosecha`): se distinguen por su día, no por la clave, que es la misma para las cinco. */
    internal enum class Faena { NINGUNA, PARICION, ESQUILA, FIELTRO, CAZA, MATANZA }

    internal fun faena(m: Momento): Faena = when {
        hoyEs(m, "paricion") -> Faena.PARICION
        hoyEs(m, "esquila") -> Faena.ESQUILA
        hoyEs(m, "fieltro") -> Faena.FIELTRO
        hoyEs(m, "caza") -> Faena.CAZA
        hoyEs(m, "matanza") -> Faena.MATANZA
        else -> Faena.NINGUNA
    }

    /** El día del ovoo (12 de julio): cada uno lleva una piedra y una cinta azul al montón del cerro. */
    internal fun diaDelOvoo(m: Momento): Boolean = hoyEs(m, "ovoo")

    /**
     * Cuántas cintas hay atadas a los palos del ovoo: unas cuantas de siempre y una más por cada 12 de julio que ya pasó
     * desde la temporada 1 **[Diseño propio]**: el cuento dice que cada uno ata la suya, no cuántas quedan.
     */
    internal fun cintasDelOvoo(m: Momento): Int = 5 + (1..5).count { paso(m, it, "07-12") } * 2

    /** La primera leche de las yeguas (16 de junio): el chamán la echa al cielo; las yeguas, atadas con sus potros. */
    internal fun primeraLeche(m: Momento): Boolean = hoyEs(m, "primera_leche")

    /** La noche más larga (22 de diciembre): todo el campamento se junta en la tienda de la madre de Bolad. */
    internal fun nocheLarga(m: Momento): Boolean = hoyEs(m, "noche_larga")

    /** En qué tienda arde el fuego grande de la reunión a la hora de la gente; null si cada uno está en la suya. */
    internal fun dondeSeJuntan(m: Momento): Tienda? = if (nocheLarga(m) && (m.hacer == Hacer.GENTE || m.hacer == Hacer.DORMIR && m.hora < 24.0 && m.hora >= 22.0)) Tienda.MADRE_BOLAD else null

    /** El potro blanco que pare la yegua baya (temporada 1, 2 de mayo): está en el rebaño desde ese día. */
    internal fun potroBlanco(m: Momento): Boolean = paso(m, 1, "05-02")

    /** La cuna de madera de Saran, atada en la carreta, desde que nace (temporada 2, 12 de marzo) hasta que monta sola (temporada 5, 5 de mayo). */
    internal fun cuna(m: Momento): Boolean = paso(m, 2, "03-12") && !paso(m, 5, "05-05")

    /** El día que mata al lobo que atacaba las ovejas: la piel, tendida en la carreta. */
    internal fun pielDeLobo(m: Momento): Boolean = hoyEs(m, "lobo")

    /** La primera liebre de Mönkh, colgada junto a la puerta. */
    internal fun liebre(m: Momento): Boolean = hoyEs(m, "primera_liebre")

    /** Las carreras: la del potro blanco (temporada 2) y la de los niños (temporada 5). Cintas en las crines. */
    internal fun carrera(m: Momento): Boolean = hoyEs(m, "carrera") || hoyEs(m, "carrera_monkh")

    /** El monje chino que cruza el Kerulen (temporada 2, 25 de abril): su carro, lejos, en el vado. */
    internal fun monjeDePaso(m: Momento): Boolean = hoyEs(m, "changchun")

    /** La noche que Mönkh cuida solo los caballos (temporada 4, 25 de julio): una lumbre pequeña, lejos, junto a la manada. */
    internal fun fuegoDeMonkh(m: Momento): Boolean = hoyEs(m, "monkh_noche") && (m.hora >= 19.0 || m.hora < 5.0)

    // ---------------------------------------------------------------- El horario de Bat

    /**
     * El fuego del centro de la tienda de Bat, de 0 (brasas) a 1: lo que dice «Un día suyo». Se aviva al alba, para el
     * té y la leche; en el valle, para la carne de mediodía, y al caer la tarde, cuando se juntan; se deja en brasas de
     * noche («El fuego se apaga despacio»). En verano se come fuera y el de dentro casi no se enciende.
     */
    internal fun fuegoDentro(m: Momento): Float {
        val valle = lugar(m) == Lugar.VALLE
        val base = when (m.hacer) {
            Hacer.CUIDAR -> 0.8f
            Hacer.TRABAJO -> if (valle) 0.4f else 0.15f
            Hacer.COMER -> if (valle) 0.9f else 0.3f
            Hacer.GENTE -> if (valle) 1f else 0.55f
            Hacer.DORMIR -> if (m.hora >= 22.0) 0.45f else 0.2f
            Hacer.REZAR, Hacer.LIBRE, Hacer.CAMINO -> 0.4f
            null -> 0.3f
        }
        // La noche más larga se junta todo el campamento en otra tienda: la de Bat queda en brasas.
        return if (dondeSeJuntan(m) != null) minOf(base, 0.2f) else if (armado(m) < 1f) 0f else base
    }

    /** Cómo está la puerta de fieltro de la tienda de Bat. */
    internal enum class Puerta { CERRADA, ENTREABIERTA, ABIERTA }

    /**
     * En el valle, cerrada por el frío; entreabierta a la hora de la gente, cuando se entra y se sale, y los días de
     * fiesta, de visitas. En el verano, abierta «para que entre el fresco», también de noche; cerrada si llueve. El día
     * de la mudanza, abierta mientras se descarga. Los días de luto, cerrada.
     */
    internal fun puerta(m: Momento): Puerta = when {
        armado(m) < 1f -> Puerta.ABIERTA
        luto(m) -> Puerta.CERRADA
        lugar(m) == Lugar.VERANO -> if (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA) Puerta.ENTREABIERTA else Puerta.ABIERTA
        m.hacer == Hacer.GENTE -> Puerta.ENTREABIERTA
        fiesta(m) && m.hacer != Hacer.DORMIR -> Puerta.ENTREABIERTA
        else -> Puerta.CERRADA
    }

    /**
     * La lumbre de fuera, de 0 (ceniza fría) a 1. En el valle **[Diseño propio]**: el hogar de piedras con la olla grande,
     * junto a la puerta, se enciende para las faenas (la carne de la caza y de la matanza) y en las fiestas a la hora de
     * la gente. En el verano, el fuego del campamento: para comer «en la hierba fuera de la tienda» y, al anochecer,
     * «alguien toca el violín de cabeza de caballo junto a un fuego».
     */
    internal fun fuegoFuera(m: Momento): Float = when (lugar(m)) {
        Lugar.VALLE -> {
            val faena = faena(m) == Faena.MATANZA || faena(m) == Faena.CAZA
            when {
                faena && (m.hacer == Hacer.TRABAJO || m.hacer == Hacer.COMER) -> 0.8f
                fiesta(m) && m.hacer == Hacer.GENTE -> 1f
                partida(m) && m.hora >= 17.0 && m.hora < 22.0 -> 0.7f
                else -> 0f
            }
        }
        Lugar.VERANO -> when (m.hacer) {
            Hacer.COMER -> 0.7f
            Hacer.GENTE -> 1f
            Hacer.CUIDAR -> 0.5f
            Hacer.DORMIR -> if (m.hora >= 22.0) 0.35f else 0f
            else -> 0.12f
        }
    }

    /**
     * Qué parte de las ovejas está cerca de las tiendas (en el corral, en el valle), de 0 a 1. De día el rebaño pace
     * fuera, «por las laderas del valle» o en el llano junto al río; de noche «están apretadas en el corral». En la
     * parición quedan en el corral las madres con sus corderos; el día de la mudanza, todas, recién llegadas.
     */
    internal fun ovejasCerca(m: Momento): Float {
        val fuera = m.hacer == Hacer.TRABAJO || m.hacer == Hacer.COMER
        return when {
            !fuera -> 1f
            partida(m) -> 1f
            faena(m) == Faena.PARICION || faena(m) == Faena.ESQUILA -> 0.5f
            else -> 0.15f
        }
    }

    /** El Negro, el caballo de Bat: de día va montado en él; al anochecer «ata el Negro junto a la puerta», y ahí pasa la noche. */
    internal fun negroAtado(m: Momento): Boolean = m.hacer != Hacer.TRABAJO && m.hacer != Hacer.COMER

    /**
     * Las yeguas, en la cuerda larga con sus potros al lado, «esperando el ordeño»: del alba al anochecer, que se
     * ordeñan «muchas veces al día». De noche quedan atados solo los potros **[Aproximación]**.
     */
    internal fun yeguasEnLaCuerda(m: Momento): Boolean = lugar(m) == Lugar.VERANO && m.hora >= 5.0 && m.hora < 21.0

    /** Lo que cuelga de la cuerda de crin del verano. */
    internal enum class Colgado { CUAJADA, LANA, FIELTRO, CINTAS, NADA }

    /**
     * En la fiesta, cintas azules; en la esquila, vellones de lana; en la semana del fieltro, tiras de fieltro nuevo; los
     * días de luto, nada; los demás, las bolsas de cuajada y la leche seca que se guarda para el invierno **[Diseño
     * propio]**: el cuento dice qué se hace con la leche, no dónde se seca.
     */
    internal fun colgado(m: Momento): Colgado = when {
        lugar(m) == Lugar.VALLE -> Colgado.NADA
        luto(m) -> Colgado.NADA
        fiesta(m) || diaDelOvoo(m) || carrera(m) -> Colgado.CINTAS
        faena(m) == Faena.ESQUILA -> Colgado.LANA
        faena(m) == Faena.FIELTRO -> Colgado.FIELTRO
        partida(m) -> Colgado.NADA
        else -> Colgado.CUAJADA
    }

    /** El odre grande de cuero, para el airag, «cuelga junto a la puerta de la tienda en verano». */
    internal fun odre(m: Momento): Boolean = lugar(m) == Lugar.VERANO && armado(m) >= 1f

    // ---------------------------------------------------------------- La luz

    private fun Luces.con(ambiente: Color, bruma: Color, medio: Color = this.medio, bajo: Color = this.bajo) = Luces(
        alto = alto, medio = medio, bajo = bajo, ambiente = ambiente, bruma = bruma, brumaFuerza = brumaFuerza,
        solX = solX, solAltura = solAltura, solColor = solColor, solFuerza = solFuerza, lunaX = lunaX, lunaAltura = lunaAltura,
        estrellas = estrellas, noche = noche, nublado = nublado, eclipse = 0f,
    )

    /**
     * La luz de fuera. En el valle, el aire frío y seco del invierno: el horizonte más pálido y limpio y, de noche, la
     * claridad de la nieve, que deja ver el cerro, las tiendas y el hielo. En el llano del verano, el aire dorado y las
     * noches claras.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        return when (lugar(m)) {
            Lugar.VALLE -> {
                val k = nieve(m)
                l.con(ambiente = lerp(l.ambiente, Color(0xFF9EA6DA), (0.2f + 0.22f * k) * n), bruma = lerp(lerp(l.bruma, Color(0xFFE0E6EC), 0.3f * dia), Color(0xFF5C6699), 0.5f * n),
                    medio = lerp(l.medio, Color(0xFFD6E2EA), 0.14f * dia), bajo = lerp(l.bajo, Color(0xFFEFE9DC), 0.25f * dia))
            }
            Lugar.VERANO -> l.con(ambiente = lerp(l.ambiente, Color(0xFF9C98CC), 0.5f * n), bruma = lerp(lerp(l.bruma, Color(0xFFE6DCBA), 0.28f * dia), Color(0xFF5E6498), 0.5f * n),
                bajo = lerp(l.bajo, Color(0xFFF4E4B8), 0.28f * dia))
        }
    }

    // ---------------------------------------------------------------- Los cuadros

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.VALLE -> with(ValleMongol) { cuadro(m) }
            Lugar.VERANO -> with(VeranoMongol) { cuadro(m) }
        }
    }

    override fun seMueve(m: Momento): Boolean = when (lugar(m)) {
        Lugar.VALLE -> ValleMongol.seMueve(m)
        Lugar.VERANO -> VeranoMongol.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (lugar(m)) {
            Lugar.VALLE -> with(ValleMongol) { vivo(m, reloj) }
            Lugar.VERANO -> with(VeranoMongol) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = when (lugar(m)) {
        Lugar.VALLE -> ValleMongol.luzDelGato(m, en)
        Lugar.VERANO -> VeranoMongol.luzDelGato(m, en)
    }

    override fun estorbos(m: Momento): List<Float> = when (lugar(m)) {
        Lugar.VALLE -> ValleMongol.estorbos(m)
        Lugar.VERANO -> VeranoMongol.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (lugar(m)) {
        Lugar.VALLE -> ValleMongol.anclas(m)
        Lugar.VERANO -> VeranoMongol.anclas(m)
    }
}
