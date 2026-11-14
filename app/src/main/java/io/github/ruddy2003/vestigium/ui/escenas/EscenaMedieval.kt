package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.SolQueEntra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cae
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.solQueEntra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.TipoDia
import kotlin.math.atan
import kotlin.math.roundToInt

/**
 * La paleta del scriptorium. Manda la cal cálida de los muros y la acompaña la arenisca rojiza del Rin (arcos,
 * columnillas, la iglesia de fuera); la madera y el pergamino llenan el primer plano, y el rojo de las rúbricas vuelve
 * en la sillería pintada, en las iniciales y en el cuerno de la tinta roja.
 */
internal object ColorMedieval {
    val CAL = Color(0xFFE2D8C0)
    val ARENISCA = Color(0xFFC49A7C)
    val LOSA = Color(0xFFB08268)
    val MADERA = Color(0xFF9C7040)
    val MADERA_OSC = Color(0xFF523A26)
    val PERGAMINO = Color(0xFFEFE3C2)
    val TINTA = Color(0xFF2C2622)
    val ROJO = Color(0xFFB8432E)
    val AZUL = Color(0xFF3A628C)
    val ORO = Color(0xFFD6A840)
    val VERDE = Color(0xFF5F8A56)
    val HIERRO = Color(0xFF3E3C44)
    val LANA = Color(0xFF34303A)
    val PIZARRA = Color(0xFF636C78)
    val RIN = Color(0xFF6A9494)

    // ---- Las luces: la lámpara de sebo del dormitorio, la luna que entra por la ventana y el violeta de las sombras.
    val LAMPARA = Color(0xFFFFB25C)
    val LUNA = Color(0xFFAEC2FF)
    val SOMBRA = Color(0xFF7C74AC)
}

/**
 * Scriptorium: el mundo del hermano Odo, monje copista de una abadía benedictina sobre el Rin, de 1150 a 1154.
 *
 * Se mira el scriptorium desde dentro, de pie, hacia el testero: una sala larga del piso alto, encima de la sala del
 * capítulo. A la izquierda, el muro del sur con su ventana de dos luces, por donde entra el sol y se ven el Rin y los
 * montes de la otra orilla. Al fondo, la ventana de tres arcos que da al claustro (el huerto, el pozo, la iglesia con
 * su campanario), el armario de los libros y la puerta. Delante, el pupitre de Odo con el libro que copia, su banco
 * alto, el facistol del libro de donde copia, el arca y el bastidor de las pieles. Es un **interior con ventana**
 * (`docs/diseno/ESCENAS.md`, sección 5), como la capilla de Roma: la luz del día es la mancha de sol que entra por el
 * hueco y se mueve con la hora, con las piezas de `paisaje/Estancia.kt`.
 *
 * **[Diseño propio]**: la abadía es inventada. El cuento da la sala («una sala larga con ventanas al sur», «el armario
 * de los libros junto a la puerta», la vista «sobre los tejados del claustro»); la ventana del testero que da al
 * claustro es del cuadro, para que se vean el huerto y la nieve que el calendario pide. Es románico (hacia 1150):
 * arcos de medio punto, muros gruesos, capiteles de cubo.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG4.kt`):
 * - **El libro** (arco `libro`, [cuadernos], [tomos]): la Biblia grande en dos tomos. Los cuadernos escritos se van
 *   apilando en el estante del pupitre; el primer tomo se encuaderna el 8 de septiembre de la temporada 3; el 29 de
 *   junio de la 4 los dos se van al coro ([bibliaEnElCoro]), y el 28 de agosto de la 5 empieza un libro nuevo.
 * - **La página del día** ([renglones]): se llena con las horas de trabajo; fuera de ellas, un paño la cubre
 *   ([cubierto]), como dice el cuento. La gran inicial pintada, desde el 15 de octubre de la temporada 1 ([inicial]).
 * - **Su gente y sus cosas:** las pieles en el bastidor ([pieles]); el banquillo y la tablilla de Heinrich
 *   ([heinrich]); la llave del armario, que le deja Gerhard ([llave]); la piedra de leer ([piedraDeLeer]).
 * - **El estado `lorsch`** ([enLorsch], temporada 2, 33 días): variante del mismo sitio (decisión 21): su pupitre
 *   vacío, sin cuernos ni pluma, y su zurrón y su bordón, que no están en la percha.
 * - **Fuera:** el huerto del claustro según el arco `huerto`; la nieve; la vendimia en las viñas; el Rin helado; el
 *   tejado del campanario, quemado por el rayo ([tejadoQuemado]); las campanas; la procesión de las velas.
 * - **La hora:** de día, la mancha de sol; de noche no se copia y no hay vela en el scriptorium («la luz de vela
 *   engaña la letra»): quedan la luna que entra, la lámpara del dormitorio, que se cuela por la puerta entornada, y las
 *   ventanas de la iglesia, encendidas en los oficios ([enElCoro]).
 */
internal object EscenaMedieval : EscenaVida {
    override val vidas = listOf("copista")

    private val C = ColorMedieval

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Está en la abadía de Lorsch: su sitio queda vacío. */
    internal fun enLorsch(m: Momento): Boolean = m.estado == "lorsch"

    /** Cuánto de la Biblia grande está copiado, de 0 a 1 (en la temporada 5, del libro nuevo). */
    internal fun libro(m: Momento): Float = m.arco("libro").coerceIn(0f, 1f)

    /** Antes del encargo (12 de enero de la temporada 1) no hay libro grande en su pupitre. */
    internal fun encargada(m: Momento): Boolean = paso(m, 1, "01-12")

    /** Las pieles de ternero, que llegan el 10 de marzo de la temporada 1: una, tendida en el bastidor. */
    internal fun pieles(m: Momento): Boolean = paso(m, 1, "03-10")

    /** La primera gran inicial, la del Génesis, terminada el 15 de octubre de la temporada 1. */
    internal fun inicial(m: Momento): Boolean = paso(m, 1, "10-15")

    /** Heinrich, el niño, desde el 20 de enero de la temporada 2: su banquillo y su tablilla. */
    internal fun heinrich(m: Momento): Boolean = paso(m, 2, "01-20")

    /** Desde el 24 de junio de la temporada 5 Heinrich copia en pergamino. */
    internal fun heinrichCopia(m: Momento): Boolean = paso(m, 5, "06-24")

    /** La llave de los libros, que el viejo Gerhard le deja el 25 de noviembre de la temporada 2. */
    internal fun llave(m: Momento): Boolean = paso(m, 2, "11-25")

    /** El primer tomo, terminado y encuadernado el 8 de septiembre de la temporada 3. */
    internal fun primerTomo(m: Momento): Boolean = paso(m, 3, "09-08")

    /** El 29 de junio de la temporada 4 la Biblia está terminada y el abad la lleva al coro. */
    internal fun bibliaTerminada(m: Momento): Boolean = paso(m, 4, "06-29")

    /** Desde el día siguiente la Biblia ya no está en el scriptorium. */
    internal fun bibliaEnElCoro(m: Momento): Boolean = paso(m, 4, "06-30")

    /** El libro nuevo, los sermones, desde el 28 de agosto de la temporada 5. */
    internal fun libroNuevo(m: Momento): Boolean = paso(m, 5, "08-28")

    /** La piedra de leer, desde el 20 de octubre de la temporada 4. */
    internal fun piedraDeLeer(m: Momento): Boolean = paso(m, 4, "10-20")

    /**
     * El tejado del campanario, quemado por el rayo del 28 de julio de la temporada 3. **[Diseño propio]**: el cuento
     * no dice cuándo se rehízo; aquí queda en vigas hasta la primavera siguiente.
     */
    internal fun tejadoQuemado(m: Momento): Boolean = paso(m, 3, "07-28") && !paso(m, 4, "04-01")

    /** Cuántos tomos encuadernados hay en el estante del pupitre: 0, 1 (el primero) o 2 (solo el día que se termina). */
    internal fun tomos(m: Momento): Int = when {
        bibliaEnElCoro(m) -> 0
        bibliaTerminada(m) -> 2
        primerTomo(m) -> 1
        else -> 0
    }

    /** Cuántos cuadernos escritos hay apilados en el estante, de 0 a 8: los del tomo que está copiando. */
    internal fun cuadernos(m: Momento): Int {
        if (!encargada(m) || bibliaTerminada(m) && !libroNuevo(m)) return 0
        val l = libro(m)
        val delTomo = if (libroNuevo(m)) l / 0.25f else if (primerTomo(m)) (l - 0.5f) / 0.5f else l / 0.5f
        return (delTomo.coerceIn(0f, 1f) * 8f).roundToInt()
    }

    /** Si hoy hay un libro en su pupitre: la Biblia mientras se copia, o el libro nuevo; entre los dos, hojas sueltas. */
    internal fun copiando(m: Momento): Boolean = encargada(m) && !enLorsch(m) && (!bibliaTerminada(m) || libroNuevo(m))

    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")
    internal fun campanas(m: Momento): Boolean = m.con("campanas")
    internal fun luto(m: Momento): Boolean = m.con("luto")
    internal fun vendimia(m: Momento): Boolean = m.con("vendimia")
    internal fun helada(m: Momento): Boolean = hoyEs(m, "helada")
    internal fun candelaria(m: Momento): Boolean = hoyEs(m, "candelaria")

    /** Hay nieve posada fuera: nieva, o es el día de la helada, o lo pide la clave de escena. */
    internal fun nevado(m: Momento): Boolean = m.ficha.clima == Clima.NIEVE || m.con("nieve")

    /**
     * El scriptorium está cerrado: «en las fiestas grandes los monjes no trabajan con las manos», y los tres días de la
     * sangría tampoco. Los pupitres quedan tapados todo el día.
     */
    internal fun cerrado(m: Momento): Boolean = m.ficha.especiales.any { it.dia.tipo == TipoDia.FIESTA || it.dia.id == "sangria" || it.dia.id == "fin_biblia" }

    /** Odo copia a esta hora. */
    internal fun trabajando(m: Momento): Boolean = m.hacer == Hacer.TRABAJO && !enLorsch(m) && !cerrado(m)

    /** Los otros copistas siguen en sus pupitres aunque Odo esté en Lorsch. */
    internal fun salaEnMarcha(m: Momento): Boolean = m.hacer == Hacer.TRABAJO && !cerrado(m)

    /** El paño que cubre la página fuera de las horas de trabajo («cubre la página con un paño y baja a la iglesia»). */
    internal fun cubierto(m: Momento): Boolean = !trabajando(m) && !enLorsch(m)

    /** Cuántos renglones lleva escritos hoy en la página nueva, de 0 a 22: crecen con las horas de trabajo. */
    internal fun renglones(m: Momento): Int {
        if (!copiando(m) || cerrado(m)) return 0
        val h = m.hora
        val horas = when { h < 7.0 -> 0.0; h < 11.0 -> h - 7.0; h < 13.0 -> 4.0; h < 16.5 -> 4.0 + (h - 13.0); else -> 7.5 }
        return (2 + horas / 7.5 * 20).toInt().coerceIn(0, 22)
    }

    /** Los monjes están en el coro: las ventanas de la iglesia se encienden si es de noche. */
    internal fun enElCoro(m: Momento): Boolean = m.hacer == Hacer.REZAR

    /** La procesión de las velas da la vuelta al claustro la mañana de la Candelaria. */
    internal fun procesion(m: Momento): Boolean = candelaria(m) && m.hora >= 6.0 && m.hora < 9.0

    /** Las campanas voltean en los oficios de los días de campanas y cuando doblan a muerto. */
    internal fun volteo(m: Momento): Boolean = (campanas(m) || luto(m)) && (enElCoro(m) || m.hora in 9.0..10.0)

    // ---------------------------------------------------------------- La luz de la sala

    /** El sol (o la luna) que entra por la ventana del sur, o null. Es la pieza de la biblioteca, con la luna del tema. */
    internal fun sol(m: Momento): SolQueEntra? = solQueEntra(m.luces(), C.LUNA)

    /** La luz de fondo de la sala: la del día de fuera, rebajada. De noche, casi nada. */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val k = 0.5f + 0.22f * l.noche
        val amb = Color((l.ambiente.red * k).coerceAtMost(1f), (l.ambiente.green * k).coerceAtMost(1f), (l.ambiente.blue * (k + 0.05f)).coerceAtMost(1f))
        return Luces(
            alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = amb, bruma = lerp(Color(0xFF3A3440), amb, 0.6f), brumaFuerza = 0.5f,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    /** La lámpara del dormitorio, que arde toda la noche y se cuela por la puerta entornada. */
    internal fun lampara(m: Momento): Float = ((m.luces().noche - 0.25f) / 0.5f).coerceIn(0f, 1f)

    /** Las luces de la sala en este momento. Las dos primeras son siempre el cielo que entra por las dos ventanas. */
    internal fun focos(m: Momento): List<Foco> = buildList {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val S = SalaMedieval
        val cielo = lerp(lerp(l.medio, Color.White, 0.55f), C.LUNA, l.noche)
        add(Foco(S.LUZ_SUR, cielo, 0.52f * dia * (1f - 0.4f * l.nublado) + 0.42f * l.noche * (1f - 0.5f * l.nublado), 130f, suelo = S.E.suelo))
        add(Foco(S.LUZ_CLAUSTRO, cielo, 0.36f * dia * (1f - 0.4f * l.nublado) + 0.22f * l.noche * (1f - 0.5f * l.nublado), 124f, suelo = S.E.suelo))
        sol(m)?.let { s -> add(Foco(S.E.cae(49f, 124f, s), s.color, 0.46f * s.fuerza, 96f, suelo = S.E.suelo)) }
        val k = lampara(m)
        if (k > 0.02f) add(Foco(S.LUZ_PUERTA, C.LAMPARA, 0.95f * k, 78f, suelo = S.E.suelo))
        if (SalaMedieval.iglesiaEncendida(m) > 0.3f) add(Foco(S.LUZ_CLAUSTRO + Offset(0f, 10f), C.LAMPARA, 0.2f * l.noche, 60f, suelo = S.E.suelo))
    }

    /**
     * El rayo con que se tumban las sombras de las cosas: de día, con sol, el del sol que entra por la izquierda; sin
     * sol, el de la claridad de la ventana; de noche, el de la lámpara de la puerta, que queda a la derecha.
     */
    internal fun rayoEn(m: Momento, f: List<Foco>, pie: Offset): Rayo {
        val s = sol(m); val l = m.luces()
        if (s != null && !s.deLuna) return rayoFijo(-0.8f, Math.toDegrees(atan(s.e).toDouble()).toFloat().coerceIn(14f, 62f), (0.35f + 0.5f * s.fuerza).coerceAtMost(0.85f), C.SOMBRA)
        val puerta = f.firstOrNull { it.p == SalaMedieval.LUZ_PUERTA }
        if (puerta != null && puerta.fuerza > 0.3f) return rayoDeFoco(puerta, pie, 0.85f * puerta.fuerza.coerceAtMost(1f), C.SOMBRA)
        return rayoDeFoco(f[0], pie, (0.4f + 0.2f * (1f - l.nublado)) * (1f - 0.5f * l.noche), C.SOMBRA)
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) { with(SalaMedieval) { cuadro(m) } }

    override fun seMueve(m: Momento): Boolean = lampara(m) > 0.1f || volteo(m) || procesion(m) || SalaMedieval.iglesiaEncendida(m) > 0.3f ||
        io.github.ruddy2003.vestigium.ui.escenas.paisaje.seMueve(m.ficha.clima) || (m.arco("huerto") > 0.85f && m.luces().noche < 0.3f)

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val ext = m.luces()
        // El tiempo que hace, solo por los huecos: dentro no llueve.
        with(SalaMedieval) {
            if (io.github.ruddy2003.vestigium.ui.escenas.paisaje.seMueve(m.ficha.clima)) for (hueco in huecos()) clipPath(hueco) { tiempoVivo(ext, m.ficha.clima, reloj, 128f, m.semilla("tiempo")) }
            vivo(m, reloj)
        }
    }

    // ---------------------------------------------------------------- El gato

    /** Al gato lo alumbran las ventanas y, de noche, la lámpara de la puerta. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val f = focos(m); val l = luces(m); val a = Alumbrado(l, f)
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayoEn(m, f, en), l.noche, lerp(Color(0xFFFFF0D2), C.LAMPARA, l.noche * 0.6f)) { lerp(l.tono(it), a.en(centro, it), 0.85f) }
    }

    override val tallaDelGato: Float get() = 1.25f

    override fun estorbos(m: Momento): List<Float> = PupitresMedieval.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = PupitresMedieval.anclas(m)

    /** Si la estación pinta de verde lo de fuera, y cuánto: para la prueba y para el huerto. */
    internal fun verdor(e: Estacion): Float = when (e) {
        Estacion.PRIMAVERA -> 0.8f
        Estacion.VERANO -> 1f
        Estacion.OTONO -> 0.5f
        Estacion.INVIERNO -> 0.1f
        Estacion.SECAS -> 0.4f
        Estacion.LLUVIAS -> 1f
        Estacion.INUNDACION -> 0.6f
        Estacion.SIEMBRA -> 0.7f
        Estacion.COSECHA -> 0.6f
        Estacion.NINGUNA -> 0.6f
    }
}
