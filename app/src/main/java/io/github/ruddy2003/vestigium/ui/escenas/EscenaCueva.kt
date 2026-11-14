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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion

/**
 * La paleta de Prehistoria: dieciocho colores con nombre para los dos cuadros (dieciséis de materia y dos de luz).
 * Manda la caliza clara del valle y la acompaña el ocre rojo con que pinta Ama, que vuelve en las pieles, en la
 * lumbre y en el suelo. La luz del fuego es ámbar y su sombra, el violeta de siempre.
 */
internal object ColorCueva {
    // ---- Dieciséis colores de materia.
    val CALIZA = Color(0xFFDCCFB6)
    val ROCA = Color(0xFF9A8E80)
    val HOLLIN = Color(0xFF2B2523)
    val OCRE_ROJO = Color(0xFFA4421F)
    val OCRE_AMARILLO = Color(0xFFD29A3E)
    val PIEL = Color(0xFFB98B5C)
    val PIEL_OSC = Color(0xFF6C4A34)
    val HUESO = Color(0xFFE9DEC6)
    val MADERA = Color(0xFF7C5C3E)
    val TIERRA = Color(0xFF8E7458)
    val ESTEPA = Color(0xFFB9A86C)
    val VERDE = Color(0xFF718E54)
    val PINO = Color(0xFF3C5844)
    val RIO = Color(0xFF5C8C94)
    val HIELO = Color(0xFFCDE0E4)
    val NIEVE = Color(0xFFF3F3EF)

    // ---- Las dos luces: la del fuego y las lámparas de grasa, y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFA64C)
    val SOMBRA = Color(0xFF7A72AC)

    // ---- Lo demás no son colores nuevos: son mezclas de los de arriba, con nombre para no repetir la cuenta.
    val ROCA_OSC = lerp(ROCA, HOLLIN, 0.5f)
    val CENIZA = lerp(CALIZA, ROCA, 0.55f)
    val LUNA = lerp(SOMBRA, Color.White, 0.6f)
    val RIO_HONDO = lerp(RIO, HOLLIN, 0.4f)
    val PELO = lerp(PIEL, PIEL_OSC, 0.5f)
}

/**
 * El color de una cosa de fuera (el valle, la meseta) por su lado de luz, por su lado de sombra y de frente, con el sol
 * [r] de este momento. [calida], de 0 a 1: de noche, cuánto la alumbra además una hoguera.
 */
internal class SolCueva(val l: Luces, val r: Rayo, val calida: Float = 0f) {
    fun con(calida: Float) = SolCueva(l, r, calida.coerceIn(0f, 1f))
    private fun deLumbre(c: Color) = Color(c.red, c.green * 0.72f, c.blue * 0.42f, c.alpha)
    private fun mas(base: Color, c: Color, k: Float) = if (calida <= 0f) base else lerp(base, deLumbre(c), (calida * k).coerceIn(0f, 1f))
    fun luz(c: Color, lejos: Float = 0f) = mas(l.tono(lerp(c, Color(0xFFFFF2D0), 0.34f * r.contraste), lejos), c, 0.9f)
    fun medio(c: Color, lejos: Float = 0f) = mas(l.tono(c, lejos), c, 0.5f)
    fun sombra(c: Color, lejos: Float = 0f) = mas(l.tono(mult(c, r.color, 0.85f * r.contraste), lejos), c, 0.12f)
    /** La cara que mira a la derecha; [alReves], la que mira a la izquierda. */
    fun lado(c: Color, lejos: Float = 0f, alReves: Boolean = false) = if ((r.lat < 0f) != alReves) sombra(c, lejos) else luz(c, lejos)
    val solALaDerecha: Boolean get() = r.lat > 0f
}

/**
 * La luz de dentro del abrigo: la de fondo ([dentro]: la del día, rebajada porque se está a la sombra de la roca, o la
 * de la noche) y sus focos. El que manda de noche es la hoguera ([fuego]); de día, la claridad que entra por la boca.
 * Cada cosa de dentro le pide su color a [en], como en las escenas de focos (`paisaje/Focos.kt`).
 *
 * Es la pieza que hará falta en los demás cuadros con luz de fuego.
 */
internal class LuzCueva(val fuera: Luces, val dentro: Luces, val sol: Rayo, val focos: List<Foco>, val fuego: Foco, val lumbre: Float) {
    val a = Alumbrado(dentro, focos)
    val noche: Float get() = fuera.noche

    /** El color [c] de algo que está en [p]; [mira]: hacia dónde da su cara (−1, a la izquierda; 1, a la derecha). */
    fun en(p: Offset, c: Color, mira: Float = 0f): Color = a.en(p, c, mira)

    /** Cuánto manda el fuego sobre el sol en este momento, de 0 a 1: decide de quién son las sombras. */
    val mandaElFuego: Float get() = (lumbre * (0.25f + 0.75f * noche) * 1.6f - sol.fuerza).coerceIn(0f, 1f)

    /** El rayo con que el fuego tumba la sombra de lo que pisa en [pie]: lejos de la lumbre. */
    fun rayoDelFuego(pie: Offset): Rayo = rayoDeFoco(fuego, pie, (0.62f * mandaElFuego).coerceIn(0f, 1f), ColorCueva.SOMBRA)
}

/**
 * Prehistoria: el mundo de Ama, pintora de cuevas en un valle de caliza del suroeste de Europa, al final de la última
 * glaciación. Son **dos cuadros** en una misma escena, como Pirata, Roma y la Ruta de la Seda; cuál toca lo decide
 * [lugar], que lee el estado del calendario.
 *
 * 1. **El abrigo** ([AbrigoCueva], [ValleCueva], [FrisoCueva]): de otoño a primavera. Se mira desde dentro del abrigo
 *    de roca, que mira al sur: el techo, la pared del friso y el pilar enmarcan, y por la boca se ve el valle con su
 *    río, sus farallones de caliza y la estepa. Dentro, el hogar. Es **el primer cuadro con luz de fuego**
 *    (`docs/diseno/ESCENAS.md`, sección 5): de día manda el sol que entra por la boca; al caer la tarde y de noche,
 *    la hoguera, que alumbra por su lado y tumba las sombras lejos de sí.
 * 2. **La meseta** ([MesetaCueva], estado `verano`, del 1 de junio al 19 de septiembre): el campamento de tiendas de
 *    piel, al aire libre, en el llano alto.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG3.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **Dónde está** ([lugar]).
 * - **El friso** ([friso]): las figuras que Ama pinta en la pared, una tras otra, de la temporada 1 a la 5; su mano
 *   soplada desde que su madre le enseña ([manoDeAma]) y la de Lin, al lado, en la temporada 5 ([manoDeLin]).
 * - **La nieve** ([nieve]): cuánta cubre el valle; **el río** helado, en deshielo o abierto ([hielo], [deshielo]).
 * - **El fuego** ([lumbre]) según la hora y el día, y **la lámpara** de grasa junto al friso ([lampara]).
 * - **Las manadas** que pasan ([manada]) y los días especiales: la caza de otoño ([caza]), el luto ([luto]), la noche
 *   más larga y la fiesta del friso ([fuegoGrande]), la aurora ([aurora]) y la reunión de los grupos ([reunion]).
 */
internal object EscenaCueva : EscenaVida {
    override val vidas = listOf("pintora_cueva")

    private val C = ColorCueva

    // ---------------------------------------------------------------- Dónde está

    /** Dónde vive el grupo: bajo el abrigo de roca o en el campamento de verano. */
    internal enum class Lugar { ABRIGO, MESETA }

    /** Qué cuadro toca hoy: lo vigila `EscenaCuevaPrueba`. */
    internal fun lugar(m: Momento): Lugar = when (m.estado) {
        "verano" -> Lugar.MESETA
        else -> Lugar.ABRIGO
    }

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Cuánto del friso está pintado, de 0 a 1 (el arco `friso`). */
    internal fun friso(m: Momento): Float = m.arco("friso").coerceIn(0f, 1f)

    /** La mano soplada de Ama, desde el día que su madre le enseña. */
    internal fun manoDeAma(m: Momento): Boolean = paso(m, 1, "02-20")

    /** La mano de niña de Lin, junto a la de su madre. */
    internal fun manoDeLin(m: Momento): Boolean = paso(m, 5, "03-10")

    /** La lámpara nueva de piedra, con mango, que Ama talla en la temporada 2: desde entonces hay dos junto al friso. */
    internal fun lamparaNueva(m: Momento): Boolean = paso(m, 2, "08-10")

    /** Cuánta nieve cubre el valle, de 0 a 1: el arco `nieve`, y casi toda si hoy nieva o el día lo dice. */
    internal fun nieve(m: Momento): Float {
        val arco = m.arco("nieve").coerceIn(0f, 1f)
        return if (lugar(m) == Lugar.MESETA) 0f else if (m.con("nieve") || m.ficha.clima == Clima.NIEVE) maxOf(arco, 0.85f) else arco
    }

    /** Cuánto del río está helado, de 0 a 1: se hiela con el frío hondo y aguanta hasta el deshielo. */
    internal fun hielo(m: Momento): Float { val n = m.arco("nieve").coerceIn(0f, 1f); return if (lugar(m) == Lugar.MESETA) 0f else ((n - 0.3f) / 0.4f).coerceIn(0f, 1f) }

    /** El río baja crecido y gris de barro, con placas sueltas: desde el día del deshielo, unas tres semanas. */
    internal fun deshielo(m: Momento): Boolean = mesDia(m) >= "04-10" && mesDia(m) <= "04-30"

    internal fun caza(m: Momento): Boolean = m.con("caza")
    internal fun luto(m: Momento): Boolean = m.con("luto")
    internal fun aurora(m: Momento): Boolean = m.con("aurora")
    internal fun reunion(m: Momento): Boolean = m.con("reunion")
    /** La noche más larga y el día en que el grupo baja a ver el friso terminado: fuego grande. */
    internal fun fuegoGrande(m: Momento): Boolean = m.con("fuego")
    /** El día en que el grupo baja a ver el friso: una hilera de lámparas a su pie. */
    internal fun fiestaDelFriso(m: Momento): Boolean = hoyEs(m, "friso_fin")
    /** El león ronda el abrigo: nadie sale de noche y el fuego no se deja bajar. */
    internal fun leon(m: Momento): Boolean = hoyEs(m, "leon")
    /** Suben los salmones (en la meseta): pescado en los secaderos. */
    internal fun salmones(m: Momento): Boolean = hoyEs(m, "salmones")

    /** Las manadas que pueden verse en el valle o en la meseta. */
    internal enum class Manada { NINGUNA, CABALLOS, RENOS, BISONTES, SAIGAS, MAMUTS }

    /**
     * La manada que pasa hoy. Los días que el calendario la nombra, esa (los mamuts, los saigas, los renos de la caza
     * de otoño); los demás, la que traiga el día: algo menos de la mitad no se ve ninguna («Busca manadas. Hoy no
     * hay»). **[Diseño propio]**: qué manada toca un día cualquiera sale del azar del día, no de la ficha.
     */
    internal fun manada(m: Momento): Manada = when {
        hoyEs(m, "mamuts") -> Manada.MAMUTS
        hoyEs(m, "saigas") -> Manada.SAIGAS
        caza(m) || hoyEs(m, "tiro_tor") || hoyEs(m, "duro_reno") -> Manada.RENOS
        hoyEs(m, "frio_hondo") || hoyEs(m, "leon") -> Manada.NINGUNA
        else -> {
            val az = m.azar("manada"); val suerte = az.entero(100)
            when (lugar(m)) {
                Lugar.MESETA -> if (suerte < 30) Manada.NINGUNA else if (suerte < 72) Manada.CABALLOS else if (suerte < 92) Manada.BISONTES else Manada.SAIGAS
                Lugar.ABRIGO -> if (suerte < 46) Manada.NINGUNA else if (suerte < 72) Manada.CABALLOS else if (suerte < 90) Manada.RENOS else Manada.BISONTES
            }
        }
    }

    /**
     * El fuego del hogar, de 0 (cenizas) a algo más de 1 (el fuego grande de las fiestas). Sigue «Un día suyo»: se aviva
     * para comer y al caer la tarde, cuando el grupo se junta; de día queda en rescoldo con una llama pequeña; de noche
     * se deja en brasas. Con el frío hondo, el león o la nieve cayendo no se deja bajar.
     */
    internal fun lumbre(m: Momento): Float {
        if (fuegoGrande(m) && (m.hora >= 16.0 || m.hora < 2.0)) return 1.3f
        val base = when (m.hacer) {
            Hacer.COMER -> 0.8f
            Hacer.GENTE -> 1f
            Hacer.DORMIR -> if (m.hora >= 20.0 && m.hora < 23.0) 0.95f else if (m.hora >= 23.0 || m.hora < 2.0) 0.6f else 0.4f
            Hacer.REZAR -> 0.5f
            Hacer.LIBRE -> 0.5f
            Hacer.TRABAJO -> 0.34f
            Hacer.CAMINO -> 0.3f
            Hacer.CUIDAR -> 0.5f
            null -> 0.4f
        }
        val frio = lugar(m) == Lugar.ABRIGO && (leon(m) || hoyEs(m, "frio_hondo") || m.ficha.clima == Clima.NIEVE)
        // En la meseta la hoguera del corro arde todo el día: se cocina, se ahúma la carne y se seca el pescado.
        return if (frio) maxOf(base, 0.8f) else if (lugar(m) == Lugar.MESETA) maxOf(base, 0.6f) else base
    }

    /**
     * La lámpara de grasa que alumbra el friso: encendida mientras se pinta (de 16:00 a 18:00) y hasta que el grupo se
     * duerme; después queda una sola, de vela, hasta el alba ([velando]). El día de la fiesta del friso y el del luto,
     * todo el día. **[Diseño propio]**: la lámpara de vela no sale del cuento.
     */
    internal fun lampara(m: Momento): Boolean = lugar(m) == Lugar.ABRIGO && (m.hacer == Hacer.REZAR || m.hora >= 16.0 || m.hora < 6.0 || fiestaDelFriso(m) || luto(m))

    /** De las once de la noche al alba solo queda una lámpara, la de velar: las demás se apagan cuando el grupo se duerme. */
    internal fun velando(m: Momento): Boolean = (m.hora >= 23.0 || m.hora < 6.0) && !fiestaDelFriso(m)

    // ---------------------------------------------------------------- La luz

    private fun Luces.con(ambiente: Color, bruma: Color, medio: Color = this.medio, bajo: Color = this.bajo, brumaFuerza: Float = this.brumaFuerza) = Luces(
        alto = alto, medio = medio, bajo = bajo, ambiente = ambiente, bruma = bruma, brumaFuerza = brumaFuerza,
        solX = solX, solAltura = solAltura, solColor = solColor, solFuerza = solFuerza, lunaX = lunaX, lunaAltura = lunaAltura,
        estrellas = estrellas, noche = noche, nublado = nublado, eclipse = 0f,
    )

    /**
     * La luz de fuera. En el valle, el cielo de `HITOS` con el aire frío y limpio de la glaciación: el horizonte más
     * pálido y, de noche, la claridad de la nieve, para que se lean los farallones y el río. En la meseta, el verano:
     * el aire dorado y las noches cortas y claras.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        return when (lugar(m)) {
            Lugar.ABRIGO -> {
                val k = nieve(m)
                l.con(ambiente = lerp(l.ambiente, Color(0xFF8E92CC), (0.2f + 0.12f * k) * n), bruma = lerp(lerp(l.bruma, Color(0xFFDDE6EA), 0.34f * dia), Color(0xFF5A6498), 0.5f * n),
                    medio = lerp(l.medio, Color(0xFFDCE6EA), 0.16f * dia), bajo = lerp(l.bajo, Color(0xFFEFEADC), 0.3f * dia))
            }
            Lugar.MESETA -> l.con(ambiente = lerp(l.ambiente, Color(0xFF9A96CC), 0.58f * n), bruma = lerp(lerp(l.bruma, Color(0xFFE6DCB8), 0.3f * dia), Color(0xFF5E6498), 0.5f * n),
                bajo = lerp(l.bajo, Color(0xFFF2E2B4), 0.3f * dia))
        }
    }

    /** La luz de fondo de dentro del abrigo: la de fuera, a la sombra de la roca; de noche, la del rescoldo en las paredes. */
    private fun dentro(m: Momento, fuera: Luces): Luces {
        val n = fuera.noche; val a = fuera.ambiente
        val sombra = Color(a.red * 0.68f, a.green * 0.66f, (a.blue * 0.76f).coerceAtMost(1f))
        return fuera.con(ambiente = lerp(sombra, Color(0xFF2C2024), 0.56f * n), bruma = lerp(fuera.bruma, Color(0xFF3A3448), 0.6f), brumaFuerza = 0.5f)
    }

    /** La luz de dentro del abrigo en este momento, con sus focos: la boca, la mancha de sol, el fuego, las lámparas y la luna. */
    internal fun luzDeDentro(m: Momento): LuzCueva {
        val fuera = luces(m); val r = Rayo(fuera); val dia = (1f - fuera.noche).coerceIn(0f, 1f); val f = lumbre(m)
        val fuego = Foco(AbrigoCueva.HOGAR + Offset(0f, -12f), C.LUMBRE, f * (0.3f + 1.05f * fuera.noche), 112f * (0.8f + 0.2f * f), suelo = AbrigoCueva.HOGAR.y)
        val focos = buildList {
            // La claridad del cielo que entra por la boca: alumbra lo que la mira y lo que está cerca del labio.
            add(Foco(AbrigoCueva.BOCA, lerp(fuera.medio, Color.White, 0.5f), 0.5f * dia * (1f - 0.35f * fuera.nublado), 150f, suelo = AbrigoCueva.LABIO))
            // La mancha de sol en el suelo, que rebota.
            if (r.fuerza > 0.05f) add(Foco(AbrigoCueva.centroDelSol(r), lerp(fuera.solColor, Color.White, 0.2f), 0.4f * r.fuerza, 80f, suelo = AbrigoCueva.LABIO))
            add(fuego)
            if (lampara(m)) {
                val k = 0.3f + 0.5f * fuera.noche
                for (p in AbrigoCueva.lamparas(m)) add(Foco(p + Offset(0f, -3f), C.LUMBRE, k, 40f, suelo = p.y))
            }
            // La luna, por la boca.
            if (fuera.noche > 0.5f && fuera.nublado < 0.6f) add(Foco(AbrigoCueva.BOCA, C.LUNA, 0.2f * fuera.noche * (1f - fuera.nublado), 120f, suelo = AbrigoCueva.LABIO))
        }
        return LuzCueva(fuera, dentro(m, fuera), r, focos, fuego, f)
    }

    // ---------------------------------------------------------------- Los cuadros

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.ABRIGO -> with(AbrigoCueva) { cuadro(m) }
            Lugar.MESETA -> with(MesetaCueva) { cuadro(m) }
        }
    }

    override fun seMueve(m: Momento): Boolean = when (lugar(m)) {
        Lugar.ABRIGO -> AbrigoCueva.seMueve(m)
        Lugar.MESETA -> MesetaCueva.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (lugar(m)) {
            Lugar.ABRIGO -> with(AbrigoCueva) { vivo(m, reloj) }
            Lugar.MESETA -> with(MesetaCueva) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    /**
     * En el abrigo lo alumbra lo que alumbre el sitio donde pisa: la claridad de la boca de día y el fuego de noche,
     * que le da por su lado y le tumba la sombra lejos de la lumbre. En la meseta, el sol; de noche, la hoguera.
     */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato = when (lugar(m)) {
        Lugar.ABRIGO -> {
            val z = luzDeDentro(m); val k = z.mandaElFuego
            val rayo = if (k > 0.4f) z.rayoDelFuego(en) else AbrigoCueva.rayoQueEntra(z.sol)
            LuzGato(rayo, z.noche, lerp(Color(0xFFFFF0D0), C.LUMBRE, k)) { z.en(en + Offset(0f, -10f), it) }
        }
        Lugar.MESETA -> {
            val l = luces(m); val r = Rayo(l); val calida = MesetaCueva.calor(m, en); val s = SolCueva(l, r, calida)
            LuzGato(if (calida > 0.35f) MesetaCueva.rayoDelFuego(m, en) else r, l.noche, lerp(Color(0xFFFFF0D0), C.LUMBRE, calida)) { s.medio(it) }
        }
    }

    override fun estorbos(m: Momento): List<Float> = when (lugar(m)) {
        Lugar.ABRIGO -> AbrigoCueva.estorbos(m)
        Lugar.MESETA -> MesetaCueva.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (lugar(m)) {
        Lugar.ABRIGO -> AbrigoCueva.anclas(m)
        Lugar.MESETA -> MesetaCueva.anclas(m)
    }

    /** La estación, para los árboles y la hierba. */
    internal fun estacion(m: Momento): Estacion = m.ficha.estacion
}
