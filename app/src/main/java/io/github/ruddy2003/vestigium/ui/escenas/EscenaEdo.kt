package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.vidas.Clima
import java.time.LocalDate

/**
 * La paleta de Japón Edo: dieciséis colores de materia y dos de luz. Manda la madera de cedro y el papel; los
 * acompañan el verde de los arrozales y de los pinos y el azul violeta del Fuji. El índigo de las cortinas vuelve en el
 * pañuelo y en los faroles de la posta; el bermellón del fieltro del banco, en el santuario y en el collar del gato.
 */
internal object ColorEdo {
    val WASHI = Color(0xFFF0E8D6)
    val YESO = Color(0xFFE6DCC6)
    val SUMI = Color(0xFF2C2A30)
    val MADERA = Color(0xFF8C6C4C)
    val MADERA_OSC = Color(0xFF4C3829)
    val TEJA = Color(0xFF5E6470)
    val PAJA = Color(0xFFC9A866)
    val TIERRA = Color(0xFFBC9E78)
    val INDIGO = Color(0xFF2E4A6E)
    val BERMELLON = Color(0xFFC4452E)
    val PINO = Color(0xFF3E5E46)
    val ARROZ = Color(0xFF7AA04C)
    val AGUA = Color(0xFF5A8C94)
    val FUJI = Color(0xFF6A6E96)
    val ORO = Color(0xFFD6A846)
    val CEREZO = Color(0xFFF0C4CA)

    // ---- Las dos luces: la de los faroles de papel y el fogón, y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFB866)
    val SOMBRA = Color(0xFF8078B0)

    // ---- Mezclas de los de arriba.
    val NIEVE = lerp(WASHI, Color.White, 0.6f)
    val AGUA_HONDA = lerp(AGUA, SUMI, 0.45f)
    val AGUA_ORILLA = lerp(AGUA, YESO, 0.4f)
    val MONTE = lerp(PINO, FUJI, 0.5f)
    val BARRO = lerp(TIERRA, MADERA_OSC, 0.45f)
    val FIELTRO = lerp(BERMELLON, MADERA_OSC, 0.12f)
}

/**
 * Japón Edo: el mundo de Ochiyo, sirvienta de la casa de té de la señora Tsuru, a la entrada de una posta del Tōkaidō
 * desde la que se ve el Fuji, de 1830 a 1834. Se mira desde **debajo del alero de la casa de té**, de pie junto al
 * fogón, hacia el camino real: enfrente, las primeras casas de la posta; a la izquierda, los arrozales, el río del vado,
 * los montes y el Fuji.
 *
 * Lo que se ve ([LejosEdo], [PostaEdo], [CasaDeTeEdo]):
 * - **Lejos:** el Fuji, grande, con más o menos nieve según la estación; los montes de delante con sus bancos de
 *   bruma; el llano con los arrozales, el río y su vado, los pueblos y la fila de pinos del camino.
 * - **En medio:** el camino real, de tierra apisonada, y al otro lado las casas de la posta, de dos pisos, con sus
 *   celosías, sus cortinas y sus faroles; la casa grande de los señores (el honjin) y la torre de vigía de los fuegos.
 *   A la izquierda, un cerezo junto al jizō del camino.
 * - **Delante:** el alero de la casa de té con su farol y las ristras de sandalias, los dos postes, el fogón con la
 *   tetera grande de hierro, la tinaja del agua, los sacos de arroz, y el banco largo con su fieltro rojo, la bandeja del
 *   té y el objeto del día.
 *
 * **[Aproximación]:** la posta no tiene nombre; es una composición con lo que el cuento pone en ella. Muchas postas del
 * Tōkaidō tenían el Fuji a la vista (Hara, Yoshiwara, Numazu); qué se ve exactamente desde la puerta es mío.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG5.kt`):
 * - **Los peregrinos de Ise** (estado `okage`, de mayo a agosto de la temporada 1): sin personas, se ven sus huellas:
 *   montones de sandalias de paja rotas en la cuneta, la mesa que los ricos de la posta ponen con arroz, té y
 *   sandalias, polvo en el aire y, de noche, la fila de faroles que va hacia el oeste ([okage]).
 * - **La procesión de un daimio** (clave `procesion`): el camino barrido, los conos de arena y los cubos de agua delante
 *   de cada puerta, las cortinas con el blasón en la casa de los señores y los postigos de arriba cerrados
 *   ([procesion]).
 * - **El arroz** (arco `arroz`, [sacos]): cuatro sacos a la puerta cuando está a su precio, dos cuando sube y, con el
 *   hambre de Tenpō, uno y un saco de mijo.
 * - **Los días especiales:** Bon ([bon], los faroles de los muertos), Tanabata ([tanabata], el bambú con los papeles
 *   de colores), el año nuevo ([anioNuevo], los pinos de la puerta y la cuerda sagrada), el día de los niños ([tango],
 *   las carpas de tela), la luna de otoño ([tsukimi]), setsubun ([setsubun]), la gran limpieza ([susuharai]), las
 *   lluvias que cierran el río ([rioCerrado]), la nieve temprana en el Fuji ([nieveDelFuji]) y la boda ([boda]).
 * - **Su vida:** la estampa del Fuji con la ola que deja un viajero, clavada en el poste desde ese día ([estampaDeLaOla]),
 *   y la de su posta, al lado, desde que llegan las estampas del camino ([estampaDeLaPosta]).
 * - **La hora:** de madrugada riega y barre el camino (el suelo mojado y la escoba); el fogón y la tetera echan humo
 *   mientras hay trabajo; el farol se enciende al caer la tarde y se apaga cuando se cierra.
 */
internal object EscenaEdo : EscenaVida {
    override val vidas = listOf("posadera")
    /** La franja del fondo del teléfono ([franjaFondo]): el banco del fieltro rojo, el camino real y el Fuji. */
    override val franjaFondo: Float get() = 0.45f

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)
    internal fun mes(m: Momento): Int = m.ficha.dia.substring(5, 7).toInt()
    internal fun delAnio(m: Momento): Int = LocalDate.parse(m.ficha.dia).dayOfYear

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    // ---- El camino

    /** Los meses de los peregrinos de Ise (estado `okage`). */
    internal fun okage(m: Momento): Boolean = m.estado == "okage"

    /** Pasa la procesión de un daimio, o la comitiva del shogun: el camino preparado (clave `procesion`). */
    internal fun procesion(m: Momento): Boolean = m.con("procesion")

    /** Las lluvias que cierran el río tres días: el río crecido y pardo, y el vado cerrado. */
    internal fun rioCerrado(m: Momento): Boolean = hoyEs(m, "rio_cerrado")

    /** A esta hora el camino está recién regado: de madrugada, cuando ella barre y riega, y hasta media mañana. */
    internal fun regado(m: Momento): Boolean = m.hora >= 5.0 && m.hora < 9.5 && !nevado(m)

    /** La escoba de bambú, apoyada en el poste: mientras barre, de madrugada. */
    internal fun barriendo(m: Momento): Boolean = m.hacer == Hacer.CUIDAR

    // ---- La casa de té

    /** La casa de té está abierta: el banco con su fieltro y su bandeja, de las 7:00 a las 22:00. */
    internal fun abierta(m: Momento): Boolean = m.hacer != Hacer.DORMIR && m.hacer != Hacer.CUIDAR

    /** El fuego del fogón, de 0 a 1: vivo mientras hay trabajo; de madrugada, cuando lo enciende, a medias; de noche, el rescoldo tapado. */
    internal fun fogon(m: Momento): Float = when (m.hacer) {
        Hacer.CUIDAR -> 0.7f
        Hacer.TRABAJO, Hacer.COMER -> 1f
        Hacer.LIBRE -> 0.4f
        else -> 0.15f
    }

    /** El farol de la puerta: se enciende al caer la tarde y se apaga cuando cierra (a las 22:00). */
    internal fun farol(m: Momento): Boolean = luces(m).noche > 0.3f && m.hacer != Hacer.DORMIR && m.hacer != Hacer.CUIDAR

    /** Cuántos sacos de arroz hay a la puerta: cuatro a su precio, dos cuando sube y uno con el hambre (y, al lado, el de mijo). */
    internal fun sacos(m: Momento): Int { val a = m.arco("arroz"); return when { a < 0.4f -> 4; a < 0.7f -> 2; else -> 1 } }

    /** Con el hambre de Tenpō se sirve el arroz mezclado con mijo: el saco de mijo a la puerta. */
    internal fun mijo(m: Momento): Boolean = m.arco("arroz") >= 0.7f

    // ---- Su vida

    /** La estampa del Fuji con la ola enorme que deja un viajero (temporada 2, 28 de abril): clavada en el poste desde ese día. */
    internal fun estampaDeLaOla(m: Momento): Boolean = paso(m, 2, "04-28")

    /** Las estampas de las cincuenta y tres postas (temporada 4, 5 de diciembre): la de su posta, clavada al lado. */
    internal fun estampaDeLaPosta(m: Momento): Boolean = paso(m, 4, "12-05")

    /** El día de su boda, en la casa de té: los barriles de sake y la cortina roja y blanca. */
    internal fun boda(m: Momento): Boolean = hoyEs(m, "boda")

    // ---- Las fiestas

    internal fun bon(m: Momento): Boolean = m.con("linternas")
    internal fun tanabata(m: Momento): Boolean = hoyEs(m, "tanabata")
    internal fun anioNuevo(m: Momento): Boolean = hoyEs(m, "shogatsu")
    internal fun tango(m: Momento): Boolean = hoyEs(m, "tango")
    internal fun tsukimi(m: Momento): Boolean = hoyEs(m, "tsukimi")
    internal fun setsubun(m: Momento): Boolean = hoyEs(m, "setsubun")
    internal fun susuharai(m: Momento): Boolean = hoyEs(m, "susuharai")

    /** Algún adorno de fiesta en la casa de té: para el ancla del gato. */
    internal fun adornada(m: Momento): Boolean = bon(m) || tanabata(m) || anioNuevo(m) || boda(m) || tsukimi(m)

    // ---- El tiempo y la estación

    internal fun nevado(m: Momento): Boolean = m.ficha.clima == Clima.NIEVE
    internal fun llueve(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA || rioCerrado(m)

    /** La nieve temprana en el Fuji (temporada 3, 10 de octubre): la cumbre blanca hasta muy abajo. */
    internal fun nieveDelFuji(m: Momento): Boolean = m.con("nieve")

    /**
     * Cuánta nieve tiene el Fuji, de 0.12 (agosto: unas vetas en la cumbre) a 1 (febrero, hasta media ladera). El día de
     * la nieve temprana, como en pleno invierno.
     */
    internal fun nieveEnElFuji(m: Momento): Float {
        if (nieveDelFuji(m)) return 0.95f
        val t = floatArrayOf(0.95f, 1f, 0.92f, 0.75f, 0.5f, 0.3f, 0.15f, 0.1f, 0.12f, 0.3f, 0.6f, 0.85f)
        val mm = mes(m) - 1; val k = (m.ficha.dia.substring(8, 10).toInt() - 1) / 30f
        return t[mm] + (t[(mm + 1) % 12] - t[mm]) * k
    }

    /** En qué va el arrozal. */
    internal enum class Arrozal { SECO, INUNDADO, PLANTADO, VERDE, DORADO, SEGADO }

    /**
     * El arrozal según el día del año. **[Aproximación]** para el Tōkaidō de esa época: seco en invierno; se inunda en
     * abril; se planta en mayo y junio (el agua espejo con las matas en fila); verde en julio y agosto; dorado en
     * septiembre y octubre; segado, con los haces en sus varales, en noviembre.
     */
    internal fun arrozal(m: Momento): Arrozal {
        val d = delAnio(m)
        return when {
            d < 91 -> Arrozal.SECO
            d < 125 -> Arrozal.INUNDADO
            d < 180 -> Arrozal.PLANTADO
            d < 244 -> Arrozal.VERDE
            d < 300 -> Arrozal.DORADO
            d < 335 -> Arrozal.SEGADO
            else -> Arrozal.SECO
        }
    }

    /** El cerezo: 0 pelado, florido del 1 al 15 de abril, verde, y rojizo en noviembre. */
    internal fun cerezoEnFlor(m: Momento): Boolean { val d = delAnio(m); return d in 91..106 }

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del lugar: el cielo de `HITOS` con el aire húmedo del Tōkaidō, más neblinoso que el del Mediterráneo (la
     * bruma algo más fuerte y más azul). De noche la luz de fondo no llega al negro, para que se lea el Fuji a la luna.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        return Luces(
            alto = l.alto, medio = lerp(l.medio, Color(0xFFDCE4E0), 0.12f * dia), bajo = lerp(l.bajo, Color(0xFFF0E6D2), 0.2f * dia),
            ambiente = lerp(l.ambiente, Color(0xFF9A9ACC), 0.4f * n),
            bruma = lerp(lerp(l.bruma, Color(0xFFD8DEDC), 0.3f * dia), Color(0xFF5E6094), 0.5f * n), brumaFuerza = (l.brumaFuerza + 0.05f).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) { with(CasaDeTeEdo) { cuadro(m) } }

    override fun seMueve(m: Momento): Boolean = true                          // la tetera humea y el agua brilla

    override fun DrawScope.viva(m: Momento, reloj: Float) { with(CasaDeTeEdo) { vivo(m, reloj) } }

    // ---------------------------------------------------------------- El gato

    /** De día, la sombra del alero; de noche, el farol y el fogón, que lo tiñen de cálido por su lado. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val r = Rayo(l); val calida = CasaDeTeEdo.calor(m, en)
        val p = Pincel(l, r, calida, ColorEdo.LUMBRE)
        return LuzGato(r, l.noche, lerp(Color(0xFFFFF0D0), ColorEdo.LUMBRE, calida)) { p.medio(it) }
    }

    override fun estorbos(m: Momento): List<Float> = CasaDeTeEdo.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = CasaDeTeEdo.anclas(m)
}
