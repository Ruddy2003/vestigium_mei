package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lucesDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.vientoDe
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import io.github.ruddy2003.vestigium.vidas.Luz
import io.github.ruddy2003.vestigium.vidas.Semilla
import java.time.LocalDate
import kotlin.math.floor

// =====================================================================
//  El motor de escena (plan de historia y escenas, sección 3.3; tanda P4a, 11 nov 2026)
// =====================================================================
//
// El cuadro de Hoy pinta el entorno del personaje de «Otras vidas» de cada tema: un paisaje sin personas, a la altura
// de la vista, que cambia con la hora, la estación, el tiempo y lo que pasa ese día en su vida. Todo lo que dibuja
// sale de la ficha del día (`FichaDelDia`), que es lo mismo que lee el cuento: por eso los dos cuentan lo mismo
// (decisión 18 del plan). Nada de esto se guarda y nada lee un dato tuyo.
//
// Las capas, de abajo hacia arriba:
//   1. cielo y lejanía     2. plano medio     3. primer plano, con las anclas del gato y el objeto del día
//   4. la luz de la hora   5. estación y tiempo, con sus partículas   6. arcos   7. adornos del día especial
//   8. el gato (tanda P5)
// Lo que no se mueve ([EscenaVida.quieta]) se traza una vez y se guarda como imagen; lo que se mueve
// ([EscenaVida.viva]: gotas, humo, llamas, destellos) se traza en cada cuadro, encima.
//
// La luz no es un velo encima del dibujo: cada cosa pide su color a `Luces.tono`, que lo tiñe según la hora y el
// tiempo y lo desvanece con la distancia (ver `paisaje/Luces.kt`).

/**
 * El momento que pinta una escena: la ficha del día de su personaje y la hora. Es lo único que una escena puede leer.
 *
 * La [hora] se redondea hacia abajo a tercios de hora: dentro de cada veinte minutos la parte quieta del cuadro es la
 * misma, y solo se vuelve a trazar cuando cambia el [sello].
 */
class Momento(val ficha: FichaDelDia, horaExacta: Double) {
    /** La hora del cuadro, de 0 a 24, en pasos de veinte minutos. */
    val hora: Double = floor((((horaExacta % 24) + 24) % 24) * 3) / 3

    /** La franja de luz y cuánto ha avanzado. */
    val luz: Luz = ficha.luz(hora)

    /** Qué hace el personaje a esta hora, según «Un día suyo»: se nota en su entorno (el fuego a la hora de comer). */
    val hacer: OtrasVidas.Hacer? = ficha.tramo(horaExacta)?.que

    /** Las claves de escena de todos los días especiales de hoy («fiesta», «humo», «partida»…). */
    val claves: Set<String> = ficha.especiales.map { it.dia.escena }.filter { it.isNotEmpty() }.toSet()

    /** El estado del personaje («mar»), o vacío. */
    val estado: String get() = ficha.estado ?: ""

    /** Cuánto sopla el viento, de 0 a 1, con el tiempo de hoy. */
    val viento: Float get() = vientoDe(ficha.clima)

    /** Lo que distingue un cuadro quieto de otro: si no cambia, no hay que volver a trazarlo. */
    val sello: String = "${ficha.vida}|${ficha.dia}|${(hora * 3).toInt()}|${hacer?.name}"

    /** Si hoy la escena lleva la clave [clave]. */
    fun con(clave: String): Boolean = clave in claves

    /** El valor de hoy del arco [id], de 0 a 1; 0 si el calendario no lo tiene. */
    fun arco(id: String): Float = ficha.arcos[id]?.toFloat() ?: 0f

    /** La luz de este momento. [eclipse], de 0 a 1, lo decide cada escena (a qué horas se ve el suyo). */
    fun luces(eclipse: Float = 0f): Luces = lucesDe(luz, ficha.clima, eclipse)

    /** Un azar propio de este día y de las [partes] dadas: el mismo día dibuja siempre lo mismo. */
    fun azar(vararg partes: Any): Azar = Azar(semilla(*partes))

    /** La semilla de ese azar. */
    fun semilla(vararg partes: Any): Long = Semilla.de(ficha.semilla, *partes)
}

/** A qué puede reaccionar el gato del cuadro (tanda P5) en un sitio de la escena. */
enum class TipoAncla {
    /** Un tramo de suelo por donde camina: va de `x` a `hasta`. */
    SUELO,
    /** Un sitio alto donde se sienta a mirar (un muro, una ventana). */
    MIRADOR,
    /** Un fuego encendido: se echa cerca. */
    FUEGO,
    /** El objeto del día: lo huele, juega con él. */
    OBJETO,
    /** Un sitio a cubierto, para cuando llueve o nieva. */
    ABRIGO,
    /** Un adorno del día especial: lo mira o le da con la pata. */
    ADORNO,
}

/** Un sitio de la escena para el gato, en las unidades del lienzo (380 × 285). [hasta] solo cuenta en un [TipoAncla.SUELO]. */
class Ancla(val tipo: TipoAncla, val x: Float, val y: Float, val hasta: Float = x)

/**
 * La escena de un tema: el entorno de su personaje de «Otras vidas», dibujado con código. Un `object` por tema
 * (`Escena<Tema>.kt`), como los muebles de Vida. Se dibuja siempre dentro de [lienzo]: 380 × 285 unidades.
 *
 * Reglas para todas las escenas:
 * - **Sin personas.** Se ve su mundo y lo que dejan: el fuego, las herramientas, la nave.
 * - **Solo se lee el [Momento]** (la ficha del día y la hora). Lo que el cuento dice y se puede ver, se ve aquí ese
 *   mismo día: el **estado** del personaje, los **arcos**, la **clave de escena** y el **objeto del día**.
 * - **Igual en claro y en oscuro:** la luz es la de la hora, no la del modo del teléfono.
 * - Con piezas de la biblioteca (`paisaje/`) y lo propio del tema encima; cada color pasa por `Luces.tono`.
 * - Determinista: el mismo día y la misma hora dan el mismo cuadro.
 * - **Con el aspecto de la guía de arte** (`docs/diseno/ESCENAS.md`, decisión 20 del plan): estilo gouache y diez
 *   reglas (luz con dirección y sombras, cielo con vida, paleta corta, agua con profundidad, primer plano grande y
 *   con uso, algo que cruce y enmarque, grano, la noche al nivel del día, bordes naturales y lejanía con
 *   profundidad). La vara es `EscenaHeleno`: una escena nueva se dibuja al lado de esa y no puede verse más pobre.
 */
interface EscenaVida {
    /** Los personajes cuyo entorno pinta (su id en `OtrasVidas`). Casi siempre uno; en Ciudad neón, dos que se alternan. */
    val vidas: List<String>

    /** A quién le toca el día lógico [dia] («2027-03-21»): con dos personajes, un día cada uno. */
    fun vida(dia: String): String = vidas[Math.floorMod(LocalDate.parse(dia).toEpochDay(), vidas.size.toLong()).toInt()]

    /**
     * Lo que no se mueve: cielo y lejanía, plano medio, primer plano con el objeto del día, los arcos, los adornos del
     * día especial y la luz de la hora en todo ello. Se guarda como imagen mientras no cambie [Momento.sello].
     */
    fun DrawScope.quieta(m: Momento)

    /**
     * Lo que se mueve, en el instante [reloj] (segundos; con las animaciones apagadas llega siempre el mismo): las
     * partículas del tiempo, el humo, las llamas y los destellos. Se traza en cada cuadro, encima de [quieta].
     */
    fun DrawScope.viva(m: Momento, reloj: Float)

    /** Si en este momento hay algo que se mueva. Si no, el cuadro no pide cuadros de animación y no gasta batería. */
    fun seMueve(m: Momento): Boolean

    /** Los sitios de la escena para el gato en este momento (tanda P5). */
    fun anclas(m: Momento): List<Ancla>

    /**
     * La luz que le llega al gato cuando pisa en [en]: de qué lado viene, qué sombra arroja y cómo tiñe su pelo. Por
     * omisión, la del sol o la luna de este momento. La cambia la escena que tenga su propia luz (un eclipse, o los
     * focos de una escena donde siempre es de noche).
     */
    fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = m.luces(); return LuzGato(Rayo(l), l.noche) { l.tono(it) } }

    /**
     * El tamaño del gato en esta escena. Con 1 mide unas 33 unidades de largo sin la cola, que es su tamaño real junto a
     * las cosas del primer plano; por omisión va algo más grande, para que se le vea bien en un cuadro tan pequeño.
     */
    val tallaDelGato: Float get() = 1.3f
}

/**
 * Pone el lienzo de una escena: desde aquí se dibuja en 380 × 285 unidades, sea cual sea el tamaño real del cuadro
 * (una unidad es un dp en un teléfono de 412 dp de ancho; en uno más angosto todo se encoge a la vez).
 */
inline fun DrawScope.lienzo(dibujo: DrawScope.() -> Unit) {
    val k = size.width / ANCHO
    scale(k, k, Offset.Zero) { dibujo() }
}

/**
 * La escena de cada tema, o null si el tema no tiene cuadro (sigue con el gato de pixel).
 *
 * Navidad, Terminal y Básico no lo tendrán (decisión 1 del plan). Los demás van llegando por tandas: el piloto
 * (Helénico en P4a; Gótico y Ciudad neón en P4b) y después las tandas S.
 */
fun escenaDe(tema: TemaId): EscenaVida? = when (tema) {
    TemaId.ROMANO -> null           // S1
    TemaId.URBE -> null             // S5
    TemaId.CIBER -> null            // Terminal: sin cuadro
    TemaId.NEON -> EscenaNeon
    TemaId.HELENO -> EscenaHeleno
    TemaId.EGIPTO -> null           // S1
    TemaId.MEDIEVAL -> null         // S2
    TemaId.TEMPLARIO -> null        // S2
    TemaId.DISTOPIA -> null         // S6
    TemaId.REFUGIO -> null          // S6
    TemaId.ORDEN -> null            // S7
    TemaId.VAPOR -> null            // S4
    TemaId.LABORATORIO -> null      // S7
    TemaId.COLONOS -> null          // S7
    TemaId.NAPOLEON -> null         // S4
    TemaId.DECO -> null             // S5
    TemaId.BOTANICO -> null         // S4
    TemaId.RUINAS -> null           // S6
    TemaId.NAVIDAD -> null          // Navidad: sin cuadro
    TemaId.SEDA -> null             // S2
    TemaId.EDO -> null              // S3
    TemaId.GOTICO -> EscenaGotico
    TemaId.ARCANO -> null           // S7
    TemaId.ESPACIO -> null          // S6
    TemaId.ABISMO -> null           // S5
    TemaId.ANDALUS -> null          // S2
    TemaId.MESO -> null             // S1
    TemaId.MONGOL -> null           // S3
    TemaId.RENACIMIENTO -> null     // S3
    TemaId.PIRATA -> null           // S3
    TemaId.URSS -> null             // S5
    TemaId.REVOLUCION -> null       // S4
    TemaId.ANTIGUAS -> null         // S7
    TemaId.CUEVA -> null            // S1
    TemaId.BASICO -> null           // Básico: sin cuadro
}

/** Los temas que no tendrán cuadro nunca (decisión 1 del plan): los demás que hoy dan null están pendientes. */
fun sinCuadro(tema: TemaId): Boolean = when (tema) {
    TemaId.ROMANO -> false
    TemaId.URBE -> false
    TemaId.CIBER -> true
    TemaId.NEON -> false
    TemaId.HELENO -> false
    TemaId.EGIPTO -> false
    TemaId.MEDIEVAL -> false
    TemaId.TEMPLARIO -> false
    TemaId.DISTOPIA -> false
    TemaId.REFUGIO -> false
    TemaId.ORDEN -> false
    TemaId.VAPOR -> false
    TemaId.LABORATORIO -> false
    TemaId.COLONOS -> false
    TemaId.NAPOLEON -> false
    TemaId.DECO -> false
    TemaId.BOTANICO -> false
    TemaId.RUINAS -> false
    TemaId.NAVIDAD -> true
    TemaId.SEDA -> false
    TemaId.EDO -> false
    TemaId.GOTICO -> false
    TemaId.ARCANO -> false
    TemaId.ESPACIO -> false
    TemaId.ABISMO -> false
    TemaId.ANDALUS -> false
    TemaId.MESO -> false
    TemaId.MONGOL -> false
    TemaId.RENACIMIENTO -> false
    TemaId.PIRATA -> false
    TemaId.URSS -> false
    TemaId.REVOLUCION -> false
    TemaId.ANTIGUAS -> false
    TemaId.CUEVA -> false
    TemaId.BASICO -> true
}
