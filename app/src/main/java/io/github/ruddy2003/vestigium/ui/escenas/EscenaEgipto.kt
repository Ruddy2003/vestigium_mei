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
 * La paleta de Egipto: quince colores de materia y dos de luz. Manda el ocre rosado de la caliza y del adobe; lo
 * acompaña el verde del valle, que es poco y por eso se nota. El rojo de las puertas vuelve en las vasijas, y el
 * turquesa del río, en la loza de los amuletos.
 */
internal object ColorEgipto {
    val CALIZA = Color(0xFFEBE0C6)
    val ARENA = Color(0xFFDFBD88)
    val ROCA = Color(0xFFC9A078)
    val ROCA_OSC = Color(0xFF946E5A)
    val ADOBE = Color(0xFFB78C62)
    val BARRO = Color(0xFF7A5844)
    val TERRACOTA = Color(0xFFB65E3C)
    val OCRE = Color(0xFFD8A23E)
    val VERDE = Color(0xFF74A056)
    val PALMA = Color(0xFF3E6A46)
    val NILO = Color(0xFF4A8E90)
    val ESTERA = Color(0xFFCBAA64)
    val MADERA = Color(0xFF8C6A46)
    val MADERA_OSC = Color(0xFF4E3A2C)
    val HOLLIN = Color(0xFF2A2422)

    // ---- Las dos luces: la de las lámparas de aceite y la lumbre, y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFB45E)
    val SOMBRA = Color(0xFF8078B0)

    // ---- Mezclas de los de arriba, con nombre para no repetir la cuenta.
    val LINO = lerp(CALIZA, Color.White, 0.5f)
    val LUNA = lerp(SOMBRA, Color.White, 0.62f)
    val LIMO = lerp(BARRO, HOLLIN, 0.35f)                                   // la tierra negra que deja el río
    val TRIGO = lerp(OCRE, ARENA, 0.45f)
    val LOZA = lerp(NILO, Color(0xFF58C8BC), 0.45f)                         // la loza azul de los amuletos y las cuentas
    val ENLUCIDO = lerp(ADOBE, CALIZA, 0.55f)
}

/**
 * El color de una cosa por su lado de luz, por su lado de sombra y de frente, con el sol [r] de este momento.
 * [calida], de 0 a 1: de noche, cuánto la alumbra además una lámpara o la lumbre, que es cálida.
 */
internal class SolEgipto(val l: Luces, val r: Rayo, val calida: Float = 0f) {
    fun con(calida: Float) = SolEgipto(l, r, calida.coerceIn(0f, 1f))
    private fun deLumbre(c: Color) = Color(c.red, c.green * 0.74f, c.blue * 0.44f, c.alpha)
    private fun mas(base: Color, c: Color, k: Float) = if (calida <= 0f) base else lerp(base, deLumbre(c), (calida * k).coerceIn(0f, 1f))
    fun luz(c: Color, lejos: Float = 0f) = mas(l.tono(lerp(c, Color(0xFFFFF2D0), 0.36f * r.contraste), lejos), c, 0.85f)
    fun medio(c: Color, lejos: Float = 0f) = mas(l.tono(c, lejos), c, 0.5f)
    fun sombra(c: Color, lejos: Float = 0f) = mas(l.tono(mult(c, r.color, 0.9f * r.contraste), lejos), c, 0.14f)
    /** La cara que mira a quien mira. */
    fun frente(c: Color, lejos: Float = 0f) = mas(if (r.lat < 0f) l.tono(lerp(c, Color(0xFFFFF2D0), 0.14f * r.contraste), lejos) else l.tono(mult(c, r.color, 0.42f * r.contraste), lejos), c, 0.5f)
    /** La cara que mira a la derecha; [alReves], la que mira a la izquierda. */
    fun lado(c: Color, lejos: Float = 0f, alReves: Boolean = false) = if ((r.lat < 0f) != alReves) sombra(c, lejos) else luz(c, lejos)
    /** Lo que mira al cielo: una azotea, el suelo. */
    fun arriba(c: Color, lejos: Float = 0f) = mas(l.tono(lerp(c, Color(0xFFFFF2D0), 0.22f * r.contraste * r.dia), lejos), c, 0.6f)
    /** El sol está a la derecha del cuadro. */
    val solALaDerecha: Boolean get() = r.lat > 0f
}

/**
 * Egipto: el mundo de Pay, escriba de la cuadrilla de la Tumba, en la aldea de los obreros (Deir el-Medina), del 1200
 * al 1196 a. C. Se mira desde **la azotea de su casa**, donde la familia duerme en verano y donde él tiene su sitio de
 * escriba a la sombra de un toldo de estera. Es un exterior sin agua cerca: el río se ve lejos.
 *
 * Lo que se ve ([LejosEgipto], [AzoteaEgipto]):
 * - **Lejos, a la derecha:** el valle del Nilo por la boca de la hondonada: los campos, el río, los templos de los
 *   reyes al borde del desierto y, en la otra orilla, Tebas y los cerros del este, por donde sale el sol.
 * - **A la izquierda:** la montaña de Tebas, con su cima en forma de pirámide, el paso con las chozas de la cuadrilla y
 *   el sendero que sube; en su falda, un barranco blanco con la boca de la tumba del rey.
 * - **En medio:** la ladera de las tumbas de los obreros, con sus pirámides pequeñas, y los tejados de la aldea, con
 *   su calle estrecha y la puerta del muro al fondo.
 * - **Delante:** la azotea, con el toldo, la tinaja del agua, la estera con las lascas, el granero, el banco con el
 *   objeto del día, el hornillo, la boca de la escalera y la palmera del patio.
 *
 * **[Aproximación]: el Valle está detrás de la montaña y desde la aldea no se ve** (lo dice el cuento: «al otro lado
 * de los cerros está el Valle»). El cuadro lo acerca y lo pone en la falda del cerro, porque su calendario pide que
 * se vean la boca de la tumba, su andamio y sus montones de lascas. Es la misma licencia del faro de Horror cósmico.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG3.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **El río** ([rio], [verdor], [maduro]): el valle seco de antes de la crecida, el lago de la inundación con los
 *   pueblos como islas, el limo negro cuando el agua se va, el verde de la siembra y el oro de la cosecha.
 * - **La tumba** ([tumba], [tumbaNueva], [sellada], [andamio], [montones]): cuánto se ha picado, cuántos montones de
 *   lascas hay delante y si está el andamio. En la temporada 4 se sella la de Seti II y se empieza otra al lado.
 * - **El paso** ([enElPaso], estado `valle`, setenta días de la temporada 4): la cuadrilla duerme en las chozas del
 *   paso. En la azotea falta lo suyo, arriba humea la lumbre de las chozas y de noche hay antorchas en la obra y una
 *   lámpara en el pretil de su casa. Es una **variante** del mismo sitio: el calendario pide «la aldea sin los hombres».
 * - **Los días especiales:** las procesiones ([procesion]), las fiestas ([fiesta]), el luto ([luto]), el viento del
 *   desierto ([polvo]), la lluvia que baja por el barranco ([torrente]), las luces de Opet en la otra orilla
 *   ([lucesDeTebas]), las lámparas de Joiak y de la Fiesta del Valle en las capillas ([lamparasDeLasTumbas]).
 * - **Su casa:** la tumba de su madre en la ladera ([tumbaDeTiy]), la lasca de Huy ([huyEscribe]), la cuna de Meryt
 *   ([cuna]), el telar de Tamit ([telar]), el arca de escriba ([arca]), los sacos del grano ([sacos]) y las esteras
 *   tendidas las noches de calor ([duermenArriba]).
 */
internal object EscenaEgipto : EscenaVida {
    override val vidas = listOf("escriba_tumba")

    private val C = ColorEgipto

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)
    private fun mes(m: Momento): Int = m.ficha.dia.substring(5, 7).toInt()

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    private fun suave(x: Float): Float { val t = x.coerceIn(0f, 1f); return t * t * (3f - 2f * t) }

    // ---- El río

    /** Cuánta agua de la crecida cubre los campos: de 0 (el río en su cauce) a 1 (el valle hecho un lago). */
    internal fun rio(m: Momento): Float = m.arco("rio").coerceIn(0f, 1f)

    /** El día del año, de 0 a 1. */
    private fun delAnio(m: Momento): Float = (java.time.LocalDate.parse(m.ficha.dia).dayOfYear - 1) / 365f

    /**
     * Cuánto verdean los campos, de 0 a 1. **[Aproximación]**: se siembra en el limo cuando el agua se va (noviembre),
     * el campo está verde de diciembre a febrero, madura en marzo y abril ([maduro]) y de mayo a la crecida queda el
     * rastrojo seco. Son las tres estaciones del calendario egipcio que usa el cuento, sin más detalle.
     */
    internal fun verdor(m: Momento): Float {
        val d = delAnio(m)
        val brota = suave((d - 0.83f) / 0.12f)                              // de noviembre a mediados de diciembre
        val sigue = 1f - suave((d - 0.14f) / 0.1f)                          // hasta fines de febrero; se va con la espiga
        return if (d > 0.5f) brota else sigue
    }

    /** Cuánto limo negro se ve: cuando el agua se retira (octubre y noviembre) y todavía no ha brotado la siembra. */
    internal fun limo(m: Momento): Float { val d = delAnio(m); return suave((d - 0.74f) / 0.06f) * (1f - verdor(m)) * (1f - 0.8f * rio(m)) }

    /** Cuánto del campo está en espiga dorada: marzo y abril. */
    internal fun maduro(m: Momento): Float { val d = delAnio(m); return suave((d - 0.14f) / 0.08f) * (1f - suave((d - 0.3f) / 0.07f)) }

    // ---- La tumba del rey

    /** Cómo va la tumba que la cuadrilla hace ahora: 1 = terminada. */
    internal fun tumba(m: Momento): Float = m.arco("tumba").coerceIn(0f, 1f)

    /** Ya se pica la tumba del rey nuevo, Siptah (desde el 25 de junio de la temporada 4): la obra pasa a otra boca. */
    internal fun tumbaNueva(m: Momento): Boolean = paso(m, 4, "06-25")

    /** La tumba de Seti II está sellada: lo enterraron el 11 de mayo de la temporada 4. */
    internal fun sellada(m: Momento): Boolean = paso(m, 4, "05-12")

    /** Hay andamio en la boca de la tumba en obra: desde que la boca tiene altura para necesitarlo. El día del entierro ya lo han quitado. */
    internal fun andamio(m: Momento): Boolean = if (tumbaNueva(m)) tumba(m) >= 0.1f else !sellada(m) && !entierro(m)

    /** Cuántos montones de lascas hay delante de la tumba en obra: de 1 a 5. */
    internal fun montones(m: Momento): Int = 1 + (tumba(m) * 4.4f).toInt().coerceIn(0, 4)

    /** Se trabaja en la tumba a esta hora: de día en los turnos, y de noche también mientras hay que acabarla deprisa. */
    internal fun enObra(m: Momento): Boolean = !descanso(m) && !polvo(m) && !entierro(m) && (m.hacer == Hacer.TRABAJO || (enElPaso(m) && m.hacer != Hacer.DORMIR)) && !(sellada(m) && !tumbaNueva(m))

    // ---- El paso

    /** La cuadrilla duerme en las chozas del paso para acabar la tumba del rey muerto (estado `valle`). */
    internal fun enElPaso(m: Momento): Boolean = m.estado == "valle"

    /** Pay está en casa a esta hora: ni en la obra, ni de camino, ni en el paso. El día del escorpión no sube. */
    internal fun enCasa(m: Momento): Boolean = !enElPaso(m) && (picado(m) || descanso(m) || (m.hacer != Hacer.TRABAJO && m.hacer != Hacer.CAMINO))

    /** Los días que no sube al Valle porque le picó un escorpión. */
    internal fun picado(m: Momento): Boolean = paso(m, 1, "05-25") && !paso(m, 1, "06-01")

    // ---- Los días especiales

    internal fun procesion(m: Momento): Boolean = m.con("procesion")
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta") || procesion(m)

    /** Hoy no se sube al Valle: las fiestas grandes de la aldea y los días fuera del año. Opet y Joiak duran muchos días y se trabaja. */
    internal fun descanso(m: Momento): Boolean = m.ficha.especiales.any { it.dia.id in DESCANSOS }

    /** La azotea está engalanada: las fiestas en que la aldea para. En Joiak se llora a Osiris y no hay guirnaldas. */
    internal fun engalanada(m: Momento): Boolean = fiesta(m) && !hoyEs(m, "joiak") && !hoyEs(m, "valle_obra") && !entierro(m)
    internal fun luto(m: Momento): Boolean = m.con("luto")
    internal fun polvo(m: Momento): Boolean = m.con("polvo") || m.ficha.clima == Clima.POLVO
    internal fun llueve(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA

    /** El agua baja por el barranco: el día que llueve en el desierto. */
    internal fun torrente(m: Momento): Boolean = m.con("lluvia") || m.ficha.clima == Clima.TORMENTA

    private val DESCANSOS = setOf("amenhotep", "valle", "min", "epagomenos", "anio_nuevo")

    /** La barca de Amón cruza el río: la Fiesta del Valle, vista desde la aldea. */
    internal fun barcaDeAmon(m: Momento): Boolean = hoyEs(m, "valle")

    /** La estatua de Amenhotep sale en andas de su capilla. */
    internal fun amenhotep(m: Momento): Boolean = hoyEs(m, "amenhotep")

    /** El entierro del rey: el trineo con su capilla, delante de la tumba. */
    internal fun entierro(m: Momento): Boolean = hoyEs(m, "entierro_rey")

    /** Viene alguien grande a ver la obra: el visir o el canciller. Se le pone un pabellón delante de la tumba. */
    internal fun visita(m: Momento): Boolean = hoyEs(m, "visir") || hoyEs(m, "canciller")

    /** Las noches de Opet se ven las luces de los templos de la otra orilla. */
    internal fun lucesDeTebas(m: Momento): Boolean = hoyEs(m, "opet")

    /** Hay lámparas en las capillas de las tumbas: las noches de Joiak y de la Fiesta del Valle, y el día del luto. */
    internal fun lamparasDeLasTumbas(m: Momento): Boolean = hoyEs(m, "joiak") || barcaDeAmon(m) || hoyEs(m, "valle_obra") || hoyEs(m, "muere_tiy")

    // ---- Su casa

    /** La tumba de Tiy, su madre, en la ladera: una pirámide nueva desde el 5 de febrero de la temporada 3. */
    internal fun tumbaDeTiy(m: Momento): Boolean = paso(m, 3, "02-05")

    /** Huy ya copia signos: hay una lasca suya junto a las de su padre. */
    internal fun huyEscribe(m: Momento): Boolean = paso(m, 1, "04-15")

    /** La cuna de Meryt, desde que nace hasta que camina. */
    internal fun cuna(m: Momento): Boolean = paso(m, 2, "05-05") && !paso(m, 3, "04-25")

    /** El juguete de Meryt, desde que camina. */
    internal fun juguete(m: Momento): Boolean = paso(m, 3, "04-25")

    /** El telar que Hay le hizo a Tamit. */
    internal fun telar(m: Momento): Boolean = paso(m, 4, "10-25")

    /** El arca de los papiros, desde que lo nombran escriba del lado derecho. */
    internal fun arca(m: Momento): Boolean = paso(m, 5, "08-25")

    /**
     * Cuántos sacos de grano hay en la azotea. El día que llega la ración (28 de noviembre) y dos semanas después,
     * cuatro; el día que el grano no llega, ninguno; los demás, uno o dos según el día.
     */
    internal fun sacos(m: Momento): Int = when {
        hoyEs(m, "grano_tarde") -> 0
        mesDia(m) >= "11-28" && mesDia(m) <= "12-11" -> 4
        else -> 1 + m.azar("sacos").entero(2)
    }

    /** Las noches de calor (de abril a octubre) la familia duerme en la azotea: las esteras están tendidas. */
    internal fun duermenArriba(m: Momento): Boolean = m.hacer == Hacer.DORMIR && mes(m) in 4..10 && !polvo(m) && !llueve(m)

    /**
     * El hornillo de la azotea, de 0 (apagado) a 1 (vivo): a las horas de comer y cuando hay gente; recién acostados
     * queda el rescoldo. Con el viento del desierto o con lluvia no hay quien lo tenga encendido.
     */
    internal fun lumbre(m: Momento): Float = when {
        polvo(m) || llueve(m) -> 0f
        m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE -> 1f
        m.hacer == Hacer.CUIDAR -> 0.6f
        m.hacer == Hacer.DORMIR && m.hora >= 20.0 && m.hora < 22.0 -> 0.35f
        else -> 0f
    }

    /** Las lámparas de aceite de la azotea y la luz que sube por la boca de la escalera: mientras hay alguien despierto. */
    internal fun lamparas(m: Momento): Boolean = m.luces().noche > 0.3f && (m.hacer != Hacer.DORMIR || (m.hora >= 20.0 && m.hora < 22.7))

    /** La lámpara que Tamit deja en el pretil mientras él está en el paso: desde arriba, él la busca cada noche. */
    internal fun lamparaDelPretil(m: Momento): Boolean = enElPaso(m) && m.luces().noche > 0.3f

    /** La lumbre de las chozas del paso: mientras la cuadrilla duerme allí, encendida de noche y humeando de día. */
    internal fun lumbreDelPaso(m: Momento): Boolean = enElPaso(m)

    /** Hay antorchas en la boca de la tumba: de noche, mientras hay que acabarla deprisa. */
    internal fun antorchasDeLaObra(m: Momento): Boolean = enElPaso(m) && !sellada(m) && m.luces().noche > 0.3f

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del lugar: el cielo de `HITOS` con el polvo del desierto (el horizonte más cálido y la calima más ocre).
     * De noche el aire es limpio y la luz de fondo no llega al negro, para que se lean la caliza y los tejados a la
     * luz de la luna.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        return Luces(
            alto = l.alto, medio = lerp(l.medio, Color(0xFFF0DDB6), 0.16f * dia), bajo = lerp(l.bajo, Color(0xFFF4D9A2), 0.4f * dia),
            ambiente = lerp(l.ambiente, Color(0xFF9A98CC), 0.58f * n), bruma = lerp(lerp(l.bruma, Color(0xFFEAD2A6), 0.46f * dia), Color(0xFF5C5C94), 0.55f * n), brumaFuerza = l.brumaFuerza,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) { with(AzoteaEgipto) { cuadro(m) } }

    override fun seMueve(m: Momento): Boolean = AzoteaEgipto.seMueve(m)

    override fun DrawScope.viva(m: Momento, reloj: Float) { with(AzoteaEgipto) { vivo(m, reloj) } }

    // ---------------------------------------------------------------- El gato

    /** De día lo alumbra el sol. De noche, el hornillo, las lámparas o la luz de la escalera, que lo tiñen de cálido por su lado. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val r = Rayo(l); val calida = AzoteaEgipto.calor(m, en)
        val s = SolEgipto(l, r, calida)
        return LuzGato(r, l.noche, lerp(Color(0xFFFFF0D0), C.LUMBRE, calida)) { s.medio(it) }
    }

    override fun estorbos(m: Momento): List<Float> = AzoteaEgipto.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = AzoteaEgipto.anclas(m)
}
