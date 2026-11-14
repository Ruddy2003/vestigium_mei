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
import java.time.LocalDate
import kotlin.math.roundToInt

/** La paleta de Distopía: manda el gris verdoso del hormigón y lo acompaña el rojo de la consigna. */
internal object ColorDistopia {
    val HORMIGON = Color(0xFFC0C3B4)
    val HORMIGON_OSC = Color(0xFF8E9388)
    val CAL = Color(0xFFE6E4D4)
    val LOSA = Color(0xFFB6B6A6)
    val ASFALTO = Color(0xFF666A66)
    val ROJO = Color(0xFFB5372B)
    val ROJO_OSC = Color(0xFF7C2820)
    val HUESO = Color(0xFFEDE6D0)
    val HIERRO = Color(0xFF4C5054)
    val OXIDO = Color(0xFF8E5C3C)
    val VERDE_GRIS = Color(0xFF7C8A78)
    val MADERA = Color(0xFF9C7C54)
    val CRISTAL = Color(0xFF64787A)
    val SACO = Color(0xFFC8B48A)
    val HOLLIN = Color(0xFF3C3A38)
    val NIEVE = Color(0xFFEFF1EE)

    /** Las dos luces de la noche: el sodio de las farolas y el frío de las pantallas, que no se apagan. */
    val SODIO = Color(0xFFF4B458)
    val PANTALLA = Color(0xFF9CE0C6)
    val PILOTO = Color(0xFFFF4A3A)

    /** La luz del día, amarillenta por el humo, y su contraria para las sombras. */
    val LUZ = Color(0xFFF2F0C6)
    val SOMBRA = Color(0xFF8A78A8)
}

/**
 * Distopía: la plaza de la capital, al pie de la avenida que lleva al ministerio, vista por alguien de pie en ella (el
 * funcionario, empleado de un ministerio en la capital de un Estado que todo lo vigila, de 1984 a 1988).
 * **[Diseño propio]**: el Estado es inventado; nada de lo que se ve copia un lugar, un símbolo ni un edificio reales.
 * El emblema es un ojo, y no hay letras ni caras.
 *
 * - **Al fondo:** el cielo, que aquí es una franja entre bloques, sucio de humo; las chimeneas y los bloques lejanos en
 *   la bruma; y, al final de la avenida, **el ministerio**: una torre de hormigón blanco con ventanas como rendijas y
 *   una bandera enorme en la azotea. A su derecha sube, año tras año, **la torre nueva**.
 * - **En medio:** la avenida, que se aleja entre dos filas de bloques iguales, cada uno gastado a su manera, con sus
 *   bocacalles; las dos vías del tranvía, que acaban en sus topes al llegar a la plaza, con la catenaria colgada de las
 *   farolas y, unos días sí y otros no, un tranvía parado ([tranvia]); las farolas y los árboles desmochados; la pantalla grande de una fachada; la caseta de la ración con sus vallas.
 * - **Delante:** la plaza de losas. El poste de los altavoces, que cruza el cuadro de abajo arriba; la columna de los
 *   carteles; el banco de hormigón con el objeto del día; el bidón donde se calienta la cola; la jardinera; y, a la
 *   derecha, el cartel del mes, sin texto.
 *
 * Cómo está pintado: es «ficción con colores propios» (`docs/diseno/ESCENAS.md`, sección 5). Manda el gris verdoso del
 * hormigón y lo acompaña el rojo de la consigna; la luz del día es amarillenta, de humo ([luces]), y las sombras, de
 * su contrario, un violeta ciruela. De noche hay dos luces: el sodio de las farolas y el frío de las pantallas.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG7.kt`):
 * - **Las cámaras** ([camaras], arco `camaras`): cada año hay más; desde que están en todas partes ([Vigilancia.TODAS])
 *   llevan un piloto rojo que no se apaga.
 * - **La ración** ([sacos], arco `racion`): los sacos de la caseta, que van siendo menos.
 * - **La campaña** (estado `campana`, [enCampana]): las rendijas del ministerio, encendidas toda la noche, y sus
 *   colgaduras rojas.
 * - **La torre nueva** ([torre], [alturaDeLaTorre]): sube durante cuatro años, con sus grúas, y se inaugura el 15 de
 *   mayo de la temporada 5.
 * - **El vecino de enfrente** ([vecinoDeEnfrente]): su ventana tiene luz y un tiesto hasta que se lo llevan (temporada
 *   2); después queda tapiada.
 *   Ese día, antes de que amanezca, el furgón negro de la patrulla ([furgon]) está en su portal.
 * - **Los días especiales:** el desfile (banderas en las farolas, colgaduras, un retrato sin cara en cada ventana), la
 *   fiesta (ristras de bombillas), el apagón (ni una luz: solo velas, la luna y el bidón), los cohetes (humo y
 *   resplandor en el barrio de los obreros) y el luto (la bandera a media asta).
 * - **La provincia** (estado `provincia`, dos días de la temporada 4): no lleva cuadro nuevo (decisión 21). Es una
 *   variante: la misma plaza, pero la de un pueblo, sin bloques ni cámaras ni farolas y con el cielo entero.
 * - **El cartel del mes** ([cartelDelMes]): cambia cada mes, y asoma por debajo el del mes pasado.
 * - **El tramo del día:** la caseta abierta y humeando a la hora de comer; el bidón encendido y los taburetes en las
 *   actividades obligatorias; después del toque de queda, las ventanas a oscuras con el resplandor frío de la pantalla.
 * - **El objeto del día:** sobre el banco (el abrigo, colgado de él).
 */
internal object EscenaDistopia : EscenaVida {
    override val vidas = listOf("funcionario")

    /** El horizonte: la altura de los ojos de quien mira. */
    const val H = 128f

    /** Hacia dónde huye la avenida. */
    const val VX = 156f

    private val C = ColorDistopia

    // ---------------------------------------------------------------- Lo que el cuento mueve

    /** Dónde está el funcionario. */
    internal enum class Lugar { CAPITAL, PROVINCIA }

    /** Dónde está hoy: lo vigila `EscenaDistopiaPrueba`. La provincia es una variante del mismo cuadro. */
    internal fun lugar(m: Momento): Lugar = if (m.estado == "provincia") Lugar.PROVINCIA else Lugar.CAPITAL

    /** Tres semanas de noventa horas: las luces del ministerio no se apagan. */
    internal fun enCampana(m: Momento): Boolean = m.estado == "campana"

    /** Cuánta vigilancia hay en la calle, con los mismos cortes que usa el cuento (0.55 y 0.75 del arco `camaras`). */
    internal enum class Vigilancia { ALGUNAS, MUCHAS, TODAS }

    internal fun vigilancia(m: Momento): Vigilancia = m.arco("camaras").let { if (it < 0.55f) Vigilancia.ALGUNAS else if (it < 0.75f) Vigilancia.MUCHAS else Vigilancia.TODAS }

    /** Cuántas cámaras se ven en el cuadro: de 5 (temporada 1) a 9 (temporada 5). En el pueblo, ninguna. */
    internal fun camaras(m: Momento): Int = if (lugar(m) == Lugar.PROVINCIA) 0 else (1f + 9f * m.arco("camaras")).roundToInt().coerceIn(0, 9)

    /** El piloto rojo de las cámaras nuevas, «que no se apaga nunca»: solo con la corriente cortada. */
    internal fun pilotos(m: Momento): Boolean = vigilancia(m) == Vigilancia.TODAS && !apagon(m) && lugar(m) == Lugar.CAPITAL

    /** Cuántos sacos hay en la caseta de la ración: de 6 a 4 (el arco `racion` baja de 0.6 a 0.35). */
    internal fun sacos(m: Momento): Int = (m.arco("racion") * 10f).roundToInt().coerceIn(0, 8)

    /** Cómo va la torre nueva del ministerio. */
    internal enum class Torre { CIMIENTOS, ESTRUCTURA, REMATE, TERMINADA }

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** La torre está terminada desde el día en que se inaugura (el 15 de mayo de la temporada 5) y en sus repeticiones. */
    private fun inaugurada(m: Momento): Boolean = m.ficha.temporada >= 5 && (m.ficha.vuelta > 0 || mesDia(m) >= "05-15")

    /**
     * Lo que lleva subido la torre nueva, de 0 (el solar) a 1 (inaugurada). **[Diseño propio]**: el calendario solo dice
     * cuándo se inaugura; se la hace subir a ritmo parejo desde el primer día de la temporada 1.
     */
    internal fun alturaDeLaTorre(m: Momento): Float {
        if (inaugurada(m)) return 1f
        val dia = LocalDate.parse(m.ficha.dia).dayOfYear.coerceAtMost(365)
        return (((m.ficha.temporada - 1) * 365f + dia) / (4f * 365f + 135f)).coerceIn(0f, 0.97f)
    }

    internal fun torre(m: Momento): Torre = alturaDeLaTorre(m).let { if (it >= 1f) Torre.TERMINADA else if (it < 0.12f) Torre.CIMIENTOS else if (it < 0.8f) Torre.ESTRUCTURA else Torre.REMATE }

    /** El vecino de enfrente sigue en su casa: se lo llevan el 25 de junio de la temporada 2. */
    internal fun vecinoDeEnfrente(m: Momento): Boolean = m.ficha.temporada < 2 || (m.ficha.temporada == 2 && mesDia(m) < "06-25")

    /**
     * Hay un tranvía parado en el final de la línea: unos días sí y otros no (dos de cada cinco, por la semilla del
     * día). Nunca sin corriente, ni después del toque de queda, ni con la avenida cortada por un desfile.
     */
    internal fun tranvia(m: Momento): Boolean = lugar(m) == Lugar.CAPITAL && !apagon(m) && !toqueDeQueda(m) && !m.con("desfile") && m.azar("tranvia").entero(5) < 2

    /** Dónde está el furgón negro de la patrulla. */
    internal enum class Furgon { NINGUNO, APARCADO, EN_EL_PORTAL }

    /**
     * El furgón: casi siempre aparcado avenida arriba; el día que se llevan al vecino, «antes de que amanezca», está en
     * su portal con las luces encendidas. Los días de desfile no hay coches en la avenida, y en el pueblo no lo hay.
     */
    internal fun furgon(m: Momento): Furgon = when {
        lugar(m) == Lugar.PROVINCIA || m.con("desfile") -> Furgon.NINGUNO
        m.ficha.especiales.any { it.dia.id == "vecino" } && m.hora < 8.0 -> Furgon.EN_EL_PORTAL
        else -> Furgon.APARCADO
    }

    internal fun desfile(m: Momento) = m.con("desfile") && lugar(m) == Lugar.CAPITAL
    internal fun fiesta(m: Momento) = m.con("fiesta")
    internal fun apagon(m: Momento) = m.con("apagon")
    internal fun cohetes(m: Momento) = m.con("cohetes") && lugar(m) == Lugar.CAPITAL
    internal fun luto(m: Momento) = m.con("luto")
    internal fun comiendo(m: Momento) = m.hacer == Hacer.COMER
    internal fun conGente(m: Momento) = m.hacer == Hacer.GENTE

    /** Después del toque de queda (mientras él duerme) nadie tiene luz en casa. */
    internal fun toqueDeQueda(m: Momento) = m.hacer == Hacer.DORMIR

    /** Hay corriente: las farolas, las pantallas y las ventanas pueden encenderse. En el pueblo no hay farolas ni pantallas. */
    internal fun corriente(m: Momento) = !apagon(m)

    /** Las farolas están encendidas: de noche, en la capital, si hay corriente. */
    internal fun farolas(m: Momento) = lugar(m) == Lugar.CAPITAL && corriente(m) && luces(m).noche > 0.3f

    /** Las rendijas del ministerio, de 0 a 1: en campaña, todas y toda la noche. */
    internal fun rendijas(m: Momento): Float {
        val noche = luces(m).noche
        return when {
            !corriente(m) || noche < 0.2f -> 0f
            enCampana(m) -> 0.96f
            toqueDeQueda(m) -> 0.07f
            else -> 0.42f
        }
    }

    /** Cuántas ventanas de las casas tienen luz, de 0 a 1: al anochecer, muchas; después del toque de queda, ninguna. */
    internal fun ventanas(m: Momento): Float {
        val noche = luces(m).noche
        return when {
            noche < 0.2f || toqueDeQueda(m) -> 0f
            !corriente(m) -> 0.06f                                              // velas
            enCampana(m) -> 0.3f                                                // media ciudad duerme en el ministerio
            else -> 0.55f
        }
    }

    /** El bidón de la plaza arde: cuando hay gente, las noches del apagón y las tardes de invierno. Nunca con lluvia fuerte. */
    internal fun bidon(m: Momento): Boolean = m.ficha.clima != Clima.TORMENTA && m.hacer != Hacer.DORMIR &&
        (conGente(m) || (apagon(m) && luces(m).noche > 0.3f) || (m.ficha.estacion == Estacion.INVIERNO && m.hora >= 16.0))

    /** El suelo está mojado. */
    internal fun mojado(m: Momento) = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
    internal fun nevado(m: Momento) = m.ficha.clima == Clima.NIEVE

    /** Los tres primeros días de cada mes se pega el cartel nuevo: la escalera y el cubo del engrudo siguen al pie del cartel. */
    internal fun pegandoCarteles(m: Momento): Boolean = lugar(m) == Lugar.CAPITAL && m.ficha.dia.substring(8).toInt() <= 3

    /** Cuál de los seis carteles sin texto toca este mes: se cambian cada mes, y nadie se acuerda del anterior. */
    internal fun cartelDelMes(m: Momento): Int { val d = LocalDate.parse(m.ficha.dia); return Math.floorMod(d.year * 12 + d.monthValue, 6) }

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del momento, teñida por el humo de la capital: el cielo de todos no se retoca, se le pone encima el velo de
     * este lugar (guía, sección 5). El horizonte amarillea, el azul se agrisa y lo lejano se pierde antes. En el pueblo,
     * el cielo es el de todos.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces()
        if (lugar(m) == Lugar.PROVINCIA) return l
        val dia = 1f - l.noche
        fun humo(c: Color, propio: Color, k: Float) = lerp(c, Color(propio.red * l.ambiente.red, propio.green * l.ambiente.green, propio.blue * l.ambiente.blue), k)
        return Luces(
            alto = humo(l.alto, Color(0xFF9AA69A), 0.34f), medio = humo(l.medio, Color(0xFFC2C4A6), 0.4f), bajo = humo(l.bajo, Color(0xFFE2D8A8), 0.42f),
            ambiente = lerp(l.ambiente, Color(l.ambiente.red * C.LUZ.red, l.ambiente.green * C.LUZ.green, l.ambiente.blue * C.LUZ.blue), 0.7f * dia),
            bruma = humo(l.bruma, Color(0xFFCCCBAA), 0.5f), brumaFuerza = (l.brumaFuerza + 0.06f).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza * 0.85f, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas * 0.9f, noche = l.noche, nublado = l.nublado, eclipse = l.eclipse,
        )
    }

    /** El rayo del sol con la sombra de este lugar: violeta ciruela, lo contrario de su luz. */
    internal fun rayo(l: Luces): Rayo { val r = Rayo(l); return Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza, lerp(r.color, C.SOMBRA, 0.55f), r.contraste) }

    /** La farola de la plaza, la primera de la avenida: dónde está su cabeza y dónde pisa. */
    internal val FAROLA = Offset(233.6f, 72.2f)
    internal const val FAROLA_PIE = 191f

    /** La lámpara del poste de los altavoces. */
    internal val LAMPARA = Offset(84f, 92f)

    /** La pantalla grande de la fachada. */
    internal val PANTALLA_C = Offset(243f, 112f)

    /** El fuego del bidón. */
    internal val BIDON = Offset(250f, 272f)

    /** Las luces que alumbran la plaza en este momento: las dos farolas, la pantalla, los focos del cartel y el bidón. */
    internal fun focos(m: Momento): List<Foco> {
        val l = luces(m)
        val k = ((l.noche - 0.2f) / 0.6f).coerceIn(0f, 1f)
        if (k <= 0f) return emptyList()
        return buildList {
            if (farolas(m)) {
                add(Foco(Offset(FAROLA.x, 150f), C.SODIO, 0.62f * k, 84f, suelo = 236f))
                add(Foco(Offset(LAMPARA.x + 6f, 170f), C.SODIO, 0.6f * k, 72f, suelo = 262f))
                add(Foco(Offset(338f, 150f), lerp(C.SODIO, Color.White, 0.3f), 0.22f * k, 60f, suelo = 262f))
            }
            if (corriente(m) && lugar(m) == Lugar.CAPITAL) add(Foco(Offset(PANTALLA_C.x, 150f), C.PANTALLA, 0.2f * k, 90f, suelo = 200f))
            if (bidon(m)) add(Foco(BIDON + Offset(0f, -26f), Color(0xFFFF9A40), 0.75f * k, 46f, suelo = BIDON.y))
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

        // 2. Lo de detrás de la plaza: la capital con su avenida o, dos días, el pueblo de la hermana.
        if (lugar(m) == Lugar.PROVINCIA) with(PuebloDistopia) { pueblo(m, l, r) } else with(CiudadDistopia) { capital(m, l, r) }

        // La niebla envuelve la ciudad, no la plaza de delante: su velo va antes del primer plano.
        val niebla = m.ficha.clima == Clima.NIEBLA
        if (niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)

        // 3. El primer plano: la plaza y sus cosas.
        with(PlazaDistopia) { plaza(m, l, r, a) }

        // 4. El velo del tiempo que hace y el grano del papel.
        if (!niebla) tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("distopia", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el humo de las chimeneas no para

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        if (lugar(m) == Lugar.PROVINCIA) with(PuebloDistopia) { puebloVivo(m, l, reloj) } else with(CiudadDistopia) { capitalViva(m, l, reloj) }
        with(PlazaDistopia) { plazaViva(m, l, reloj) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    /** De día, la luz del sol con el humo; de noche, la de la farola o el bidón que tenga más cerca. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val f = focos(m)
        if (f.isEmpty() || l.noche < 0.5f) return LuzGato(rayo(l), l.noche) { l.tono(it) }
        val a = Alumbrado(l, f)
        val manda = f.maxByOrNull { it.fuerza / (1f + ((it.p.x - en.x) * (it.p.x - en.x)) / (it.alcance * it.alcance)) }!!
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayoDeFoco(manda, en, 0.7f, C.SOMBRA), l.noche, manda.color) { lerp(l.tono(it), a.en(centro, it), 0.6f) }
    }

    override fun estorbos(m: Momento): List<Float> = PlazaDistopia.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = PlazaDistopia.anclas(m)
}
