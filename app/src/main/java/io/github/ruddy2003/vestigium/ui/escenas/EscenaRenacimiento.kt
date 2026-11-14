package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Lado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.SolQueEntra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cae
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.solQueEntra
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import java.time.LocalDate

/**
 * La paleta del taller de Florencia: 15 colores de materia y 3 de luz. Manda el enlucido cálido de las paredes
 * ([MURO]) y lo acompaña el rojo del barro cocido ([COTTO] en el suelo, [TEJA] en los tejados). Los colores del
 * oficio ([ULTRAMAR], [CINABRIO], [OCRE], [VERDE], [ORO]) vuelven en las conchas de la mesa, en las tablas y en la
 * ciudad.
 */
internal object ColorRenacimiento {
    val MURO = Color(0xFFD2BD94)
    val PIEDRA = Color(0xFF9C988E)
    val VIGA = Color(0xFF5E4630)
    val MADERA = Color(0xFFB08A58)
    val COTTO = Color(0xFFB5694A)
    val YESO = Color(0xFFF1EADA)
    val ULTRAMAR = Color(0xFF2F4E9A)
    val CINABRIO = Color(0xFFC0452E)
    val OCRE = Color(0xFFCE9440)
    val VERDE = Color(0xFF64865E)
    val PORFIDO = Color(0xFF7E3A40)
    val ORO = Color(0xFFE0B04A)
    val CARNE = Color(0xFFE8C2A2)
    val CARBON = Color(0xFF2E2824)
    val TEJA = Color(0xFFB8603C)

    // ---- Las luces: la lámpara de aceite y la lumbre, la luna y el violeta de las sombras.
    val LAMPARA = Color(0xFFFFC070)
    val LUNA = Color(0xFFBFD0F0)
    val SOMBRA = Color(0xFF7C74AC)

    /** El papel de los cartones y de los dibujos: el yeso, entibiado. No es un color aparte. */
    val PAPEL = lerp(YESO, OCRE, 0.22f)
}

/**
 * Renacimiento: el mundo de Iacopo, aprendiz de pintor en un taller de Florencia, de 1490 a 1494.
 *
 * Se mira desde dentro del taller, de pie ([TallerRenacimiento]). Es un **interior con ventana**
 * (`docs/diseno/ESCENAS.md`, sección 5), hecho con la `Estancia` de la biblioteca, que sale de la capilla de Roma:
 * - **Al fondo**, la ventana grande, por la que se ve Florencia ([CiudadRenacimiento]): tejados, torres, colinas con
 *   cipreses y, más grande que todo, la cúpula de la catedral con su campanario.
 * - **A la izquierda**, la ventana de la calle, por donde entra el sol: su mancha cruza el suelo por la mañana, cae
 *   junto a la pared a mediodía y se tiende hacia el fondo por la tarde ([solQueEntra], `manchaDeSol`).
 * - **A la derecha**, el caballete grande con la tabla del maestro ([tablaDelMaestro]).
 * - **Delante** ([ObraRenacimiento]), la mesa de moler con su losa de pórfido, las conchas de color y el objeto del día;
 *   el rincón de Iacopo, con su caballete desde que empieza su tabla; el hornillo de la cola; el arcón y las tablas
 *   enyesadas.
 *
 * **[Diseño propio]**: el cuento dice que el taller es una sala con mesas, caballetes, tablas, cartones colgados y la
 * mesa de moler, y que desde su ventana se ve la cúpula. Cómo es la sala, dónde va cada cosa y qué pintan las tablas es
 * mío: las pinturas son inventadas y no copian ninguna obra. Que la ventana grande dé a la catedral y la de la calle
 * reciba el sol tampoco sale de ninguna fuente.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG4.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **La tabla del maestro** ([tablaDelMaestro]): tres tablas en cinco años, cada una del yeso al cuadro acabado. La
 *   primera se termina el 28 de mayo de la temporada 2 (el día `visitacion`); en la segunda aparece el pastor después
 *   de que Iacopo pose (22 de enero de la 3); la tercera queda a medias cuando muere el maestro (11 de enero de la 5)
 *   y pasa tapada y con un crespón hasta que Davide retoma el taller ([maestroDeLuto]).
 * - **La tabla de Iacopo** (arco `tabla`, [conTabla], [tablaDeIacopo]): su caballete aparece en su rincón el 10 de marzo
 *   de la temporada 4, con la madera enyesada, y la tabla avanza hasta que la firma el 25 de mayo de la 5; al día
 *   siguiente ya está en casa del sedero y en el caballete hay otra tabla en blanco.
 * - **Los cartones de la capilla** (arco `capilla`, [cartones]): clavados en la pared mientras se terminan los frescos;
 *   enrollados desde que se descubren (22 de diciembre de la temporada 1). Después hay cartones nuevos.
 * - **El campo** (estado `campo`, [enElCampo]): seis semanas de la temporada 3. Es estancia corta, así que por la
 *   decisión 21 no lleva cuadro nuevo: es el mismo taller sin él. Faltan la losa de moler, que se fue en la mula, su
 *   banqueta está patas arriba sobre la mesa, su jergón enrollado y los cartones no están.
 * - **El azul de ultramar** ([ultramar]), desde el 18 de enero de la temporada 1: la concha del azul y su rastro en la
 *   losa. **La cinta de Caterina** ([cinta]), desde el 29 de septiembre de la 2, atada en su rincón.
 * - **El tramo del día:** la lumbre de la cola mientras trabaja ([lumbre]); el pan y la jarra a la hora de comer; la
 *   lámpara de aceite al caer la tarde ([lampara]); las tablas tapadas con paños ([tapadas]); de noche, el jergón
 *   extendido detrás de la mesa, una hoja de la contraventana entornada ([entornada]) y la luna.
 * - **Los días especiales:** la fiesta (ramas y guirnalda), el primero de mayo (ramas en flor en la ventana), el carro
 *   de fuego de Pascua (humo y chispas junto a la catedral), San Juan (telas en las torres y luminarias de noche), las
 *   campanas (las palomas que levantan el vuelo), el luto (un crespón), la nieve (los tejados y la cúpula blancos, el
 *   brasero; el día sin leña, apagado: [sinLena]), el rayo en la cúpula ([andamioEnLaLinterna]) y la entrada del rey de
 *   Francia (las contraventanas casi cerradas: [guerra]).
 */
internal object EscenaRenacimiento : EscenaVida {
    override val vidas = listOf("aprendiz")

    private val C = ColorRenacimiento
    private val T get() = TallerRenacimiento

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /**
     * Si ya pasó el día [fecha] («MM-DD») de la temporada [t]. Es la misma cuenta que hace el cuento con `desde` y
     * `hasta`: así, cuando la temporada 5 se repite, el cuadro y el cuento vuelven a contar lo mismo.
     */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** El lugar del día en la línea de las cinco temporadas, en días (cada temporada cuenta 366). */
    private fun linea(m: Momento): Float = (m.ficha.temporada - 1) * 366f + LocalDate.parse(m.ficha.dia).dayOfYear

    private fun linea(t: Int, fecha: String): Float = (t - 1) * 366f + LocalDate.parse("2028-$fecha").dayOfYear

    /** De 0 a 1: cuánto ha avanzado el día de hoy entre el [fecha0] de la temporada [t0] y el [fecha1] de la [t1]. */
    private fun entre(m: Momento, t0: Int, fecha0: String, t1: Int, fecha1: String): Float =
        ((linea(m) - linea(t0, fecha0)) / (linea(t1, fecha1) - linea(t0, fecha0))).coerceIn(0f, 1f)

    /** Las seis semanas en el Mugello: el taller, sin él. */
    internal fun enElCampo(m: Momento): Boolean = m.estado == "campo"

    /** Las tres tablas que el maestro tiene en el caballete grande a lo largo de los cinco años. */
    internal enum class Motivo { ENCUENTRO, PASTORES, SANTOS }

    /** Qué tabla está en el caballete grande y cuánto lleva pintado, de 0 (la madera enyesada) a 1 (acabada). */
    internal class TablaGrande(val motivo: Motivo, val avance: Float)

    /**
     * La tabla del maestro en este día. **[Diseño propio]**: el calendario solo dice que el taller termina una tabla el
     * 28 de mayo de la temporada 2, que Iacopo posa de pastor el 22 de enero de la 3 y que el maestro muere el 11 de
     * enero de la 5; lo demás (cuándo empieza cada una y a qué paso va) es mío.
     * - **El encuentro** (dos mujeres que se saludan): ya empezada el primer día; acabada el 28 de mayo de la 2. Se
     *   queda quince días en el caballete, con su marco, y se la llevan.
     * - **Los pastores:** del 12 de junio de la 2 al 20 de febrero de la 4. El pastor del cordero se pinta después de
     *   que Iacopo pose: antes de ese día el avance no pasa de 0.4, que es donde empieza su figura.
     * - **Los santos:** desde el 1 de marzo de la 4. Cuando muere el maestro lleva algo más de la mitad; no avanza
     *   hasta que Davide retoma el taller (24 de febrero de la 5), y al acabar la 5 sigue sin terminar.
     */
    internal fun tablaDelMaestro(m: Momento): TablaGrande = when {
        !paso(m, 2, "06-12") -> TablaGrande(Motivo.ENCUENTRO, 0.34f + 0.66f * entre(m, 1, "01-01", 2, "05-28"))
        !paso(m, 4, "03-01") -> {
            val k = entre(m, 2, "06-12", 4, "02-20")
            TablaGrande(Motivo.PASTORES, if (paso(m, 3, "01-22")) k.coerceAtLeast(0.42f) else k.coerceAtMost(0.4f))
        }
        else -> TablaGrande(Motivo.SANTOS, if (!paso(m, 5, "02-24")) 0.62f * entre(m, 4, "03-01", 5, "01-11") else 0.62f + 0.24f * entre(m, 5, "02-24", 5, "12-31"))
    }

    /** Del día que muere el maestro al día que Davide retoma el taller: su tabla, tapada y con un crespón. */
    internal fun maestroDeLuto(m: Momento): Boolean = paso(m, 5, "01-11") && !paso(m, 5, "02-24")

    /** El caballete de Iacopo está en su rincón desde que empieza su primera tabla. */
    internal fun conCaballete(m: Momento): Boolean = paso(m, 4, "03-10")

    /** Su primera tabla está en el caballete: del día que la empieza al día que la firma y se la llevan al sedero. */
    internal fun conTabla(m: Momento): Boolean = paso(m, 4, "03-10") && !paso(m, 5, "05-26")

    /** Cuánto lleva pintado de su tabla, de 0 (la madera enyesada) a 1 (firmada): el arco `tabla`. */
    internal fun tablaDeIacopo(m: Momento): Float = if (conTabla(m)) m.arco("tabla").coerceIn(0f, 1f) else 0f

    internal fun firmada(m: Momento): Boolean = paso(m, 5, "05-25")

    /**
     * Cuántos cartones de la capilla hay clavados en la pared del fondo: tres mientras se pintan los frescos, dos cuando
     * falta poco y ninguno desde que se descubren (están enrollados en el rincón). Después, un cartón nuevo. En el campo
     * no hay ninguno: se los llevaron.
     */
    internal fun cartones(m: Momento): Int = when {
        enElCampo(m) -> 0
        paso(m, 1, "12-22") -> 1
        m.arco("capilla") < 0.93f -> 3
        else -> 2
    }

    /** Los cartones de la capilla ya están enrollados en el rincón. */
    internal fun cartonesEnrollados(m: Momento): Boolean = paso(m, 1, "12-22")

    /** La losa de moler está en su mesa (en el campo se la llevaron en la mula). */
    internal fun losa(m: Momento): Boolean = !enElCampo(m)

    internal fun ultramar(m: Momento): Boolean = paso(m, 1, "01-18")
    internal fun cinta(m: Momento): Boolean = paso(m, 2, "09-29")

    /** Tras el rayo del 5 de abril de la temporada 3, un andamio en la linterna de la cúpula, hasta el verano. */
    internal fun andamioEnLaLinterna(m: Momento): Boolean = paso(m, 3, "04-05") && !paso(m, 3, "07-01")

    internal fun fiesta(m: Momento): Boolean = m.con("fiesta") || hoyEs(m, "calendimaggio") || hoyEs(m, "san_juan") || hoyEs(m, "carnaval")
    internal fun luto(m: Momento): Boolean = m.con("luto") || maestroDeLuto(m)
    internal fun campanas(m: Momento): Boolean = m.con("campanas")
    internal fun carroDeFuego(m: Momento): Boolean = m.con("fuego")
    internal fun guerra(m: Momento): Boolean = m.con("guerra")
    internal fun mayo(m: Momento): Boolean = hoyEs(m, "calendimaggio")
    internal fun sanJuan(m: Momento): Boolean = hoyEs(m, "san_juan")
    internal fun carnaval(m: Momento): Boolean = hoyEs(m, "carnaval")
    internal fun posa(m: Momento): Boolean = hoyEs(m, "modelo")

    /** Hay nieve posada en los tejados: cuando nieva y en los días de nieve del calendario. */
    internal fun nevado(m: Momento): Boolean = m.ficha.clima == Clima.NIEVE || m.con("nieve")

    /** El día de la nevada en que el taller no tiene leña: ni lumbre ni brasero. */
    internal fun sinLena(m: Momento): Boolean = hoyEs(m, "nieve_dic")

    /** Los días de guardar, en que el taller no trabaja. El martes de carnaval cierra a mediodía. */
    internal fun cerrado(m: Momento): Boolean =
        listOf("anunciacion", "pascua", "san_juan", "asuncion", "san_lucas", "todos_santos", "navidad").any { hoyEs(m, it) } || (carnaval(m) && m.hora >= 12.0)

    /** Se trabaja en el taller a esta hora (aunque Iacopo esté en el campo, están los demás). */
    internal fun trabajando(m: Momento): Boolean = m.hacer == Hacer.TRABAJO && !cerrado(m) && !maestroDeLuto(m)

    /** El frío: el brasero se enciende al caer la tarde en invierno y los días de nieve. */
    private fun frio(m: Momento): Boolean = m.ficha.estacion == Estacion.INVIERNO || nevado(m)

    /** La lumbre del hornillo: la de la cola mientras se trabaja y la del brasero las tardes de frío. */
    internal fun lumbre(m: Momento): Boolean = !sinLena(m) && (trabajando(m) || (frio(m) && m.hora >= 16.0 && m.hora < 22.0))

    /** El rescoldo que queda después, hasta la madrugada. */
    internal fun rescoldo(m: Momento): Boolean = !sinLena(m) && !lumbre(m) && (m.hora >= 18.0 || m.hora < 3.0)

    /** La lámpara de aceite de la mesa: antes de amanecer, mientras desayunan, y de la caída de la tarde a la hora de acostarse. */
    internal fun lampara(m: Momento): Boolean = (m.hora >= 18.0 && m.hora < 21.5) || (m.hora >= 5.0 && m.hora < 6.0) || (hoyEs(m, "navidad") && m.hora >= 16.0 && m.hora < 23.0)

    /** «El maestro tapa las tablas con paños» al caer la tarde; también los días que el taller está cerrado. */
    internal fun tapadas(m: Momento): Boolean = m.hora >= 18.5 || m.hora < 5.0 || cerrado(m) || maestroDeLuto(m)

    /** De noche, mientras duermen, una hoja de la contraventana grande queda entornada. */
    internal fun entornada(m: Momento): Boolean = m.hacer == Hacer.DORMIR

    /** El jergón, extendido detrás de la mesa de moler mientras duermen; de día, enrollado. En el campo, el suyo no se extiende. */
    internal fun jergon(m: Momento): Boolean = m.hacer == Hacer.DORMIR && !enElCampo(m)

    /** La hora de comer en el taller: el pan, la escudilla y la jarra en la mesa. */
    internal fun comiendo(m: Momento): Boolean = m.hacer == Hacer.COMER && !enElCampo(m)

    // ---------------------------------------------------------------- La luz del taller

    /** El sol (o la luna) que entra por la ventana de la calle a esta hora: el de la biblioteca, con la luna de aquí. */
    internal fun solQueEntra(m: Momento): SolQueEntra? = solQueEntra(m.luces(), C.LUNA)

    /** El centro de la mancha de sol: de ahí rebota su luz. */
    internal fun centroDeLaMancha(sol: SolQueEntra): Offset = T.E.cae(56f, 104f, sol, Lado.IZQUIERDA)

    /**
     * La luz de fondo del taller: la del día de fuera, algo rebajada (la sala tiene dos ventanas y es clara). De noche
     * queda más oscura y más azul que la calle: así lo que alumbran la lámpara, el rescoldo y la luna destaca en cálido
     * o en frío sobre la penumbra, en vez de quedar todo de un gris parejo.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val k = 0.8f - 0.1f * l.noche
        val amb = Color((l.ambiente.red * k).coerceAtMost(1f), (l.ambiente.green * k).coerceAtMost(1f), (l.ambiente.blue * (k + 0.04f + 0.1f * l.noche)).coerceAtMost(1f))
        return Luces(
            alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = amb, bruma = lerp(Color(0xFF3A3040), amb, 0.6f), brumaFuerza = 0.45f,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    /** Cuánto abre la contraventana grande: 1, del todo; con el rey de Francia en la ciudad, casi cerrada. */
    internal fun abierta(m: Momento): Float = if (guerra(m)) 0.3f else 1f

    /**
     * Las luces del taller en este momento. La primera es siempre el cielo que entra por la ventana grande; la
     * segunda, el de la ventana de la calle.
     */
    internal fun focos(m: Momento): List<Foco> = buildList {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val abre = 0.5f + 0.5f * abierta(m)
        val cielo = lerp(lerp(l.medio, Color.White, 0.55f), C.LUNA, l.noche)
        add(Foco(T.LUZ_GRANDE, cielo, (0.2f * dia * (1f - 0.4f * l.nublado) + 0.13f * l.noche * (1f - 0.5f * l.nublado)) * abre, 130f, suelo = T.SUELO))
        add(Foco(T.LUZ_CALLE, cielo, 0.16f * dia * (1f - 0.4f * l.nublado) + 0.08f * l.noche * (1f - 0.5f * l.nublado), 80f, suelo = T.SUELO + 30f))
        solQueEntra(m)?.let { s -> add(Foco(centroDeLaMancha(s), s.color, (if (s.deLuna) 0.3f else 0.3f) * s.fuerza, 84f, suelo = T.SUELO)) }
        if (lampara(m)) add(Foco(ObraRenacimiento.LUZ_LAMPARA, C.LAMPARA, 0.4f + 0.75f * l.noche, 90f, suelo = 246f))
        if (lumbre(m)) add(Foco(ObraRenacimiento.LUZ_LUMBRE, Color(0xFFFF9A4A), 0.26f + 0.5f * l.noche, 58f, suelo = 249f))
        else if (rescoldo(m)) add(Foco(ObraRenacimiento.LUZ_LUMBRE, Color(0xFFFF7446), 0.24f + 0.46f * l.noche, 50f, suelo = 249f))
    }

    /**
     * El rayo con que el sol (o la luna) que entra por la ventana de la calle tumba las sombras. Sale de [SolQueEntra]:
     * de lado, lejos de la ventana, largas con el sol bajo y cortas a mediodía ([SolQueEntra.e]); y, según la hora, hacia
     * el fondo de la sala (por la mañana, [SolQueEntra.a] negativo) o hacia quien mira (por la tarde). Así la sombra de
     * la mesa apunta a un sitio distinto a las 7:00, a las 13:00 y a las 18:00.
     *
     * **[Aproximación]**: todas las cosas del taller arrojan esa sombra, también las que no están dentro de la mancha;
     * así la hora se lee en todo el suelo, como en un exterior. Como el sol entra siempre por la misma ventana, las
     * sombras del sol nunca caen hacia la izquierda: eso solo pasa con la lámpara, la lumbre o el cielo de la ventana
     * grande ([rayoEn]).
     */
    internal fun rayoDelSol(s: SolQueEntra): Rayo {
        val ax = (1f / s.e).coerceAtMost(2.3f); val crudo = (1.3f * s.a / s.e).coerceIn(-0.9f, 1.1f)
        // Nunca del todo de lado: una sombra sin fondo sería una raya.
        val ay = if (kotlin.math.abs(crudo) < 0.12f) (if (crudo < 0f) -0.12f else 0.12f) else crudo
        return Rayo(dia = 1f, lat = -1f, cot = ax, tan = s.e, ax = ax, ay = ay, fuerza = ((if (s.deLuna) 0.5f else 0.66f) * s.fuerza).coerceIn(0f, 1f), color = C.SOMBRA, contraste = 0.8f)
    }

    /**
     * El rayo que tumba la sombra de lo que pisa en [pie]: con sol dentro, el del sol; si no, el de la luz que más lo
     * alumbra (la lámpara, la lumbre o la ventana grande). Lo vigila la prueba: a las 7:00 la sombra es más larga que
     * a las 13:00.
     */
    internal fun rayoEn(m: Momento, f: List<Foco>, pie: Offset, fuerza: Float): Rayo? {
        val s = solQueEntra(m)
        // Con una luz encendida cerca (la lámpara, la lumbre), manda ella; si no, el sol o la luna que entran.
        val cerca = f.filter { it.fuerza > 0.5f && (it.p == ObraRenacimiento.LUZ_LAMPARA || it.p == ObraRenacimiento.LUZ_LUMBRE) }.minByOrNull { (it.p - pie).getDistance() / it.alcance }
        if (cerca != null && (cerca.p - pie).getDistance() < cerca.alcance * 1.5f && (s == null || s.deLuna)) return rayoDeFoco(cerca, pie, fuerza * cerca.fuerza.coerceIn(0.3f, 0.9f), C.SOMBRA)
        if (s != null && s.fuerza > 0.2f) return rayoDelSol(s).let { Rayo(it.dia, it.lat, it.cot, it.tan, it.ax, it.ay, it.fuerza * fuerza, it.color, it.contraste) }
        val tumban = f.filter { it.fuerza > 0.1f && (it.p == T.LUZ_GRANDE || it.p == ObraRenacimiento.LUZ_LAMPARA || it.p == ObraRenacimiento.LUZ_LUMBRE) }
        val manda = tumban.maxByOrNull { val d = it.p - Offset(pie.x, pie.y - 24f); it.fuerza / (1f + (d.x * d.x + d.y * d.y) / (it.alcance * it.alcance)) } ?: return null
        return rayoDeFoco(manda, pie, fuerza * manda.fuerza.coerceIn(0.3f, 0.9f), C.SOMBRA)
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) { with(TallerRenacimiento) { cuadro(m) } }

    override fun seMueve(m: Momento): Boolean {
        val c = m.ficha.clima
        return lampara(m) || lumbre(m) || rescoldo(m) || c == Clima.LLUVIA || c == Clima.TORMENTA || c == Clima.NIEVE || carroDeFuego(m) || campanas(m) || (sanJuan(m) && m.luces().noche > 0.5f)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) { with(TallerRenacimiento) { vivo(m, reloj) } }

    // ---------------------------------------------------------------- El gato

    /** Al gato lo alumbran las ventanas, el sol que entra, la lámpara y la lumbre. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val f = focos(m); val l = luces(m); val a = Alumbrado(l, f)
        val rayo = rayoEn(m, f, en, 0.7f) ?: rayoFijo(-0.5f, 55f, 0f, C.SOMBRA, 0.3f)
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayo, l.noche, lerp(Color(0xFFFFF0D2), Color(0xFFC8D0F0), l.noche * 0.5f)) { lerp(l.tono(it), a.en(centro, it), 0.85f) }
    }

    override val tallaDelGato: Float get() = 1.2f

    override fun estorbos(m: Momento): List<Float> = ObraRenacimiento.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = ObraRenacimiento.anclas(m)
}
