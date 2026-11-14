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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lucesDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import java.time.LocalDate
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor

/**
 * La paleta de Horror cósmico: manda el verde gris del mar y de la pizarra, y lo acompaña el sepia del papel viejo.
 * Son 18 colores: quince de materia, la luz de aceite de la noche, y la luz y la sombra del día.
 */
internal object ColorArcano {
    val PIZARRA = Color(0xFF5C6A70)
    val PIZARRA_OSC = Color(0xFF3E4A52)
    val PIEDRA = Color(0xFFA9AB9C)
    val PIEDRA_OSC = Color(0xFF777C72)
    val PIEDRA_NEGRA = Color(0xFF42494A)
    val CAL = Color(0xFFE6E4D4)
    val MADERA = Color(0xFF8A6E4E)
    val MADERA_OSC = Color(0xFF54443A)
    val CARTON = Color(0xFFC9B88A)
    val PAPEL = Color(0xFFE8DEBC)
    val LIQUEN = Color(0xFFC2A650)
    val ALMAGRE = Color(0xFF8E4034)
    val MONTE = Color(0xFF4A6250)
    val TINTA = Color(0xFF2E4A3E)

    /** El mar de esta costa: verde gris, hondo y frío. */
    val MAR = Color(0xFF4C7E78)

    /** La luz de la noche: el aceite de las lámparas. */
    val ACEITE = Color(0xFFFFC36A)

    /** La luz del día, verdosa y pálida, y su contraria para las sombras. */
    val LUZ = Color(0xFFF0F3DE)
    val SOMBRA = Color(0xFF7E6C9A)

    // Variaciones de los anteriores, con nombre para no repetir la mezcla: no son colores nuevos de la paleta.
    /** El mar hondo (el del mar, hacia la tinta) y el de la orilla (hacia el papel). */
    val MAR_HONDO = lerp(lerp(MAR, TINTA, 0.62f), PIZARRA_OSC, 0.3f)
    val MAR_ORILLA = lerp(MAR, PAPEL, 0.52f)
    /** La arena (cartón y piedra), el cáñamo de las redes y los cabos (las dos maderas) y el hierro (la piedra negra, más oscura). */
    val ARENA = lerp(CARTON, PIEDRA, 0.45f)
    val RED = lerp(MADERA, MADERA_OSC, 0.55f)
    val HIERRO = lerp(PIEDRA_NEGRA, Color.Black, 0.14f)
    /** La luz del faro: la del aceite, casi blanca tras los prismas. */
    val FARO = lerp(ACEITE, Color.White, 0.72f)
}

/**
 * Horror cósmico: la galería del archivo, en el piso alto del ayuntamiento de un pueblo de pescadores, y la bahía que se
 * ve desde ella (el archivista de un pueblo costero, de 1926 a 1930). **[Diseño propio]**: el pueblo, su faro y su
 * signo son inventados; nada copia un lugar, una criatura ni un símbolo de ninguna obra. No hay letras.
 *
 * - **Al fondo:** el cielo de esta costa, pálido y verdoso; unos cabos que se pierden en la calima; el mar, verde gris,
 *   hasta un horizonte que no tiene nada; y, al otro lado de la bahía, la punta del acantilado con **el faro**, blanco
 *   y grueso, y la casa del torrero pegada a él.
 * - **En medio:** a la derecha, el pueblo, que sube por su ladera con tejados de pizarra, la posada de tres pisos, la
 *   lonja y la iglesia con su campanario torcido y el cementerio de las rocas; detrás, el monte. Del pueblo sale **el
 *   muelle** de piedra negra, con sus argollas, su escalera que se hunde en el agua y su farol; a su abrigo, las barcas.
 *   En la playa de este lado, los palos con las redes tendidas.
 * - **Delante:** la galería de madera del archivo. A la izquierda, el muro con la puerta del archivo y su lámpara de
 *   aceite; contra él, **las cajas por abrir**; la mesa grande con el objeto del día; la silla; el fichero del
 *   catálogo; la baranda, y el poste y el alero que enmarcan el cuadro, con la cuerda donde se secan los legajos.
 *
 * Cómo está pintado: es «ficción con colores propios» y el primer cuadro de «mal tiempo frecuente» (guía, sección 5).
 * Manda el verde gris del mar y de la pizarra y lo acompaña el sepia del papel; la luz es pálida y verdosa ([luces]) y
 * las sombras, de su contrario, un violeta. **La niebla no es un velo encima:** va por capas entre los planos
 * ([Niebla], `BahiaArcano.banco`), así que lo lejano se pierde por grados y la galería queda nítida; con niebla se
 * encienden antes el faro, el farol del muelle y la lámpara de la puerta.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG7.kt`):
 * - **Las cajas por abrir** ([cajasPorAbrir], arco `archivo`): de catorce a ninguna; y los cajones llenos del fichero
 *   ([cajonesLlenos]).
 * - **El signo** ([signos], arco `signo`): desde que lo ve (temporada 1) aparece en más sitios cada año.
 * - **El faro** ([faroEncendido], [Torrero]): se enciende al anochecer y con niebla; una noche no se enciende
 *   (temporada 2); muerto el torrero lo encienden tarde, y con el nuevo gira más despacio.
 * - **El muelle** ([muelleRoto]): un temporal le arranca la mitad en la temporada 3.
 * - **Las barcas** ([barcasVaradas], [barcasFuera]): varadas en la arena de octubre a mayo; faenando por la mañana.
 * - **La marea** ([marea]): sube y baja dos veces al día y deja ver más o menos escalones.
 * - **Los días señalados:** las velas de la Candelaria, la bendición de las barcas, la procesión del muelle, la Virgen
 *   en barca, la noche de la marea más baja, el día de difuntos, la barca contra las rocas, la ballena, el mar rojo, la
 *   mañana sin pájaros.
 * - **La fiebre y el faro** (estados `fiebre` y `faro`): no llevan cuadro nuevo (decisión 21). Son variantes: el
 *   archivo cerrado, con su candado; y luz en su ventana de la posada o en la casa del torrero ([Lugar]).
 * - **El tramo del día:** la puerta abierta y los papeles en la mesa cuando trabaja; la taza cuando come; la lámpara
 *   de la mesa cuando lee de noche; todo recogido cuando pasea por el muelle o duerme.
 * - **El objeto del día:** sobre la mesa (el abrigo, en el respaldo de la silla).
 */
internal object EscenaArcano : EscenaVida {
    override val vidas = listOf("archivista")

    /** El horizonte: la altura de los ojos de quien mira desde la galería. */
    const val H = 120f

    private val C = ColorArcano

    // ---------------------------------------------------------------- Lo que el cuento mueve

    /** Dónde pasa el día el archivista. */
    internal enum class Lugar { ARCHIVO, POSADA, FARO }

    /** Dónde está hoy: lo vigila `EscenaArcanoPrueba`. La posada y el faro son variantes del mismo cuadro. */
    internal fun lugar(m: Momento): Lugar = when (m.estado) { "fiebre" -> Lugar.POSADA; "faro" -> Lugar.FARO; else -> Lugar.ARCHIVO }

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Hoy es el día especial [id] del calendario. */
    internal fun hoy(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Ya pasó (o es hoy) el día [md] de la temporada [t]. */
    private fun desde(m: Momento, t: Int, md: String): Boolean = m.ficha.vuelta > 0 || m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= md)

    /** Estamos entre los días [md0] y [md1] de la temporada [t]. */
    private fun entre(m: Momento, t: Int, md0: String, md1: String): Boolean = m.ficha.vuelta == 0 && m.ficha.temporada == t && mesDia(m) >= md0 && mesDia(m) <= md1

    /** Está en el archivo con la puerta abierta: cuando trabaja, come o lee, y no está en cama ni en el faro. */
    internal fun archivoAbierto(m: Momento): Boolean = lugar(m) == Lugar.ARCHIVO && (m.hacer == Hacer.TRABAJO || m.hacer == Hacer.COMER || m.hacer == Hacer.LIBRE)

    internal fun trabajando(m: Momento) = lugar(m) == Lugar.ARCHIVO && m.hacer == Hacer.TRABAJO
    internal fun comiendo(m: Momento) = lugar(m) == Lugar.ARCHIVO && m.hacer == Hacer.COMER
    internal fun leyendo(m: Momento) = lugar(m) == Lugar.ARCHIVO && m.hacer == Hacer.LIBRE

    /**
     * Las cajas que quedan por abrir, de 14 (temporada 1) a ninguna (cuando termina el catálogo, en la 5). Sale del
     * arco `archivo`, que es lo catalogado. La última caja sigue ahí hasta que casi acaba.
     */
    internal fun cajasPorAbrir(m: Momento): Int = ceil(14f * (1f - m.arco("archivo")) - 0.2f).toInt().coerceIn(0, 14)

    /** Los cajones del fichero que ya tienen sus fichas, de 12. */
    internal fun cajonesLlenos(m: Momento): Int = floor(12f * m.arco("archivo") + 0.5f).toInt().coerceIn(0, 12)

    /**
     * En cuántos sitios de la galería aparece el signo, de 0 a 7. Ninguno hasta el día en que lo ve por primera vez (el
     * 14 de septiembre de la temporada 1); desde entonces, uno más cada tanto, según el arco `signo`.
     */
    internal fun signos(m: Momento): Int = m.arco("signo").let { if (it <= 0f) 0 else (1 + floor(it * 7f).toInt()).coerceAtMost(7) }

    /** Quién cuida el faro. */
    internal enum class Torrero { BALTASAR, NADIE, NUEVO }

    /** Baltasar hasta que muere (22 de octubre de la temporada 4); nadie hasta que llega el nuevo (5 de marzo de la 5). */
    internal fun torrero(m: Momento): Torrero = if (desde(m, 5, "03-05")) Torrero.NUEVO else if (desde(m, 4, "10-22")) Torrero.NADIE else Torrero.BALTASAR

    /** La noche en que el faro no se enciende (12 de diciembre de la temporada 2). */
    internal fun faroApagado(m: Momento): Boolean = hoy(m, "faro_apagado")

    /**
     * El faro da luz: de noche y, de día, con niebla. Sin torrero lo encienden los del pueblo, por turnos y tarde (bien
     * entrada la noche). La noche del 12 de diciembre de la temporada 2 no se enciende.
     */
    internal fun faroEncendido(m: Momento): Boolean {
        if (faroApagado(m)) return false
        val noche = luces(m).noche
        if (torrero(m) == Torrero.NADIE) return noche > 0.95f && (m.hora >= 22.0 || m.hora < 5.0)
        return noche > 0.35f || niebla(m).ordinal >= Niebla.NIEBLA.ordinal
    }

    /** Cuánto tarda la luz del faro en dar una vuelta, en segundos: con el torrero nuevo «gira más despacio». */
    internal fun vueltaDelFaro(m: Momento): Float = if (torrero(m) == Torrero.NUEVO) 13f else 8f

    /** Al muelle le falta la mitad de fuera desde el temporal del 30 de septiembre de la temporada 3. */
    internal fun muelleRoto(m: Momento): Boolean = desde(m, 3, "09-30")

    /** Las barcas están varadas en la arena: del fin de la sardina (15 de octubre) a la bendición (1 de mayo). **[Diseño propio]**: el calendario dice cuándo se varan; que vuelvan al agua con la bendición es de la escena. */
    internal fun barcasVaradas(m: Momento): Boolean = mesDia(m) >= "10-15" || mesDia(m) < "05-01"

    /** Las barcas han salido a faenar: de madrugada y por la mañana, en la temporada, si el tiempo deja. */
    internal fun barcasFuera(m: Momento): Boolean = !barcasVaradas(m) && (m.hora >= 4.0 && m.hora < 11.0) && m.ficha.clima != Clima.TORMENTA && m.ficha.clima != Clima.VIENTO && niebla(m) != Niebla.CERRADA && !fiesta(m) && !hoy(m, "bendicion")

    /**
     * La marea, de 0 (bajamar) a 1 (pleamar). **[Aproximación]**: sube y baja cada 12 horas y 25 minutos, igual todos
     * los días, sin mareas vivas ni muertas. La noche de la marea más baja (23 de agosto) el agua se va del todo.
     */
    internal fun marea(m: Momento): Float {
        if (mareaBajisima(m)) return 0f
        val horas = LocalDate.parse(m.ficha.dia).toEpochDay() * 24.0 + m.hora
        return (0.5 + 0.5 * cos(2.0 * PI * horas / 12.42)).toFloat().let { 0.12f + 0.88f * it }
    }

    /** La noche de la marea más baja: se ven todos los escalones del muelle, «muchos más». */
    internal fun mareaBajisima(m: Momento): Boolean = hoy(m, "marea_baja") && (m.hora >= 19.0 || m.hora < 6.0)

    /** Cuánta niebla hay. */
    internal enum class Niebla { NINGUNA, BRUMA, NIEBLA, CERRADA }

    /**
     * La niebla de hoy: cerrada los días de clave `niebla` (la que no deja ver ni el muelle); niebla cuando es el tiempo
     * que hace; y una bruma baja sobre el agua algunas madrugadas en calma. Con viento o temporal no hay ninguna.
     */
    internal fun niebla(m: Momento): Niebla = when {
        m.ficha.clima == Clima.TORMENTA || m.ficha.clima == Clima.VIENTO -> Niebla.NINGUNA
        m.con("niebla") -> Niebla.CERRADA
        m.ficha.clima == Clima.NIEBLA -> Niebla.NIEBLA
        (m.hora < 8.4 || m.hora >= 22.0) && (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES) && m.azar("bruma").entero(5) < 2 -> Niebla.BRUMA
        else -> Niebla.NINGUNA
    }

    /** El tiempo tal como se ve: con la clave `niebla` hay niebla aunque la ficha diga otra cosa (salvo que llueva o nieve). */
    internal fun climaVisto(m: Momento): Clima = if (niebla(m) == Niebla.CERRADA && (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES || m.ficha.clima == Clima.POLVO)) Clima.NIEBLA else m.ficha.clima

    internal fun fiesta(m: Momento) = m.con("fiesta")
    internal fun luto(m: Momento) = m.con("luto")
    internal fun candelaria(m: Momento) = hoy(m, "candelaria")
    internal fun bendicion(m: Momento) = hoy(m, "bendicion")
    internal fun procesion(m: Momento) = hoy(m, "san_pedro")
    internal fun virgenEnBarca(m: Momento) = hoy(m, "carmen") && m.hora >= 10.0 && m.hora < 20.0
    internal fun difuntos(m: Momento) = hoy(m, "difuntos")
    internal fun temporal(m: Momento) = m.con("tormenta") || m.con("viento") || m.ficha.clima == Clima.TORMENTA

    /** La barca que el temporal echó contra las rocas (12 de marzo de la temporada 1) sigue ahí tres semanas. */
    internal fun naufragio(m: Momento): Boolean = entre(m, 1, "03-12", "04-02")

    /** La barca que el mar devolvió, vacía y seca, diez años después (5 de abril de la temporada 2): diez días en la arena, apartada. */
    internal fun barcaSeca(m: Momento): Boolean = entre(m, 2, "04-05", "04-14")

    /** La ballena muerta en la playa (20 de mayo de la temporada 3): una semana. */
    internal fun ballena(m: Momento): Boolean = entre(m, 3, "05-20", "05-26")

    /** El mar amanece rojo (20 de junio de la temporada 5): de 1, al alba, a 0, pasado el mediodía. */
    internal fun marRojo(m: Momento): Float = if (!hoy(m, "mar_rojo")) 0f else if (m.hora < 4.0) 0f else ((14.0 - m.hora) / 6.0).toFloat().coerceIn(0f, 1f)

    /** La mañana sin un solo pájaro (8 de agosto de la temporada 4). */
    internal fun sinPajaros(m: Momento): Boolean = hoy(m, "pajaros")

    /**
     * Donde el mapa del archivo pone una isla, algunos días de niebla la bruma se espesa con esa forma. Solo desde que
     * encuentra el mapa (28 de octubre de la temporada 2), uno de cada tres días de niebla. **[Diseño propio]**: el
     * cuento dice que no hay ninguna isla; el cuadro no la pinta, pinta un banco de niebla que podría serlo.
     */
    internal fun islaQueNoExiste(m: Momento): Boolean = desde(m, 2, "10-28") && niebla(m) == Niebla.NIEBLA && m.azar("isla").entero(3) == 0

    /** Hoy hay legajos tendidos a secar en la cuerda: los días secos que siguen a la humedad, cuando trabaja (dos de cada tres). */
    internal fun legajosTendidos(m: Momento): Boolean = lugar(m) == Lugar.ARCHIVO && m.hacer != Hacer.DORMIR && !mojado(m) && niebla(m) != Niebla.CERRADA && m.ficha.clima != Clima.VIENTO && m.azar("cuerda").entero(3) < 2

    internal fun mojado(m: Momento) = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
    internal fun nevado(m: Momento) = m.ficha.clima == Clima.NIEVE

    /** Su ventana de la posada tiene luz: las tres semanas de la fiebre, de día y de noche. */
    internal fun ventanaDeLaPosada(m: Momento): Boolean = lugar(m) == Lugar.POSADA

    /** Hay luz en la casa del torrero: de noche, mientras hay torrero; y las tres semanas en que él cuida el faro. */
    internal fun casaDelTorrero(m: Momento): Boolean = (lugar(m) == Lugar.FARO && (luces(m).noche > 0.2f || niebla(m) != Niebla.NINGUNA)) || (torrero(m) != Torrero.NADIE && luces(m).noche > 0.4f && !faroApagado(m))

    /** Cuántas ventanas del pueblo tienen luz, de 0 a 1. */
    internal fun ventanas(m: Momento): Float {
        val noche = luces(m).noche
        val conNiebla = niebla(m).ordinal >= Niebla.NIEBLA.ordinal
        return when {
            hoy(m, "anio_nuevo") && noche > 0.3f -> 0.75f                       // «nadie sale a la calle»
            noche < 0.25f -> if (conNiebla) 0.14f else 0f
            m.hora >= 23.5 || m.hora < 5.0 -> 0.08f
            else -> 0.5f
        }
    }

    // ---------------------------------------------------------------- La luz

    /**
     * La luz del momento con el matiz de esta costa: el cielo de todos no se retoca, se le pone encima su velo (guía,
     * sección 5). El azul se vuelve gris verdoso, el horizonte toma el sepia del papel y la luz es pálida. Los días de
     * clave `niebla` la luz es la de la niebla aunque el tiempo de la ficha sea otro.
     */
    internal fun luces(m: Momento): Luces {
        val l = if (climaVisto(m) != m.ficha.clima) lucesDe(m.luz, Clima.NIEBLA) else m.luces()
        val dia = 1f - l.noche
        fun velo(c: Color, propio: Color, k: Float) = lerp(c, Color(propio.red * l.ambiente.red, propio.green * l.ambiente.green, propio.blue * l.ambiente.blue), k)
        return Luces(
            alto = velo(l.alto, Color(0xFF8FA6A0), 0.34f), medio = velo(l.medio, Color(0xFFC2CCB8), 0.36f), bajo = velo(l.bajo, Color(0xFFE6DCBA), 0.36f),
            ambiente = lerp(l.ambiente, Color(l.ambiente.red * C.LUZ.red, l.ambiente.green * C.LUZ.green, l.ambiente.blue * C.LUZ.blue), 0.75f * dia),
            bruma = velo(l.bruma, Color(0xFFCDD4C4), 0.5f), brumaFuerza = (l.brumaFuerza + 0.05f).coerceAtMost(1f),
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza * 0.9f, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas * 0.85f, noche = l.noche, nublado = l.nublado, eclipse = l.eclipse,
        )
    }

    /** El rayo del sol con la sombra de este lugar: violeta, lo contrario de su luz verdosa. */
    internal fun rayo(l: Luces): Rayo { val r = Rayo(l); return Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza, lerp(r.color, C.SOMBRA, 0.5f), r.contraste) }

    /**
     * Cuánto se pierde algo que está a [lejos] (de 0 a 1) con la niebla de hoy: sin niebla, lo que diga la distancia;
     * con ella, lo lejano se va del todo y lo de en medio queda en silueta.
     */
    internal fun perdido(m: Momento, lejos: Float): Float = when (niebla(m)) {
        Niebla.NINGUNA -> lejos
        Niebla.BRUMA -> (lejos * 1.12f + 0.03f).coerceAtMost(0.97f)
        Niebla.NIEBLA -> (lejos * 1.45f + 0.16f).coerceAtMost(0.97f)
        Niebla.CERRADA -> (lejos * 1.9f + 0.42f).coerceAtMost(0.985f)
    }

    /** La lámpara de aceite de la puerta del archivo: dónde está su llama. */
    internal val LAMPARA = Offset(78f, 152f)

    /** La linterna del faro. */
    internal val LINTERNA = Offset(214.6f, 66.4f)

    /** El farol de la punta del muelle. */
    internal val FAROL = Offset(153f, 139.6f)

    /** La lámpara de la puerta está encendida: al anochecer y con niebla, mientras él anda por el archivo; y, en la fiesta, toda la noche. */
    internal fun lamparaDeLaPuerta(m: Momento): Boolean {
        val l = luces(m)
        if (lugar(m) != Lugar.ARCHIVO) return false
        if (fiesta(m) && l.noche > 0.3f) return true
        return m.hacer != Hacer.DORMIR && (l.noche > 0.25f || niebla(m).ordinal >= Niebla.NIEBLA.ordinal || mojado(m))
    }

    /** La lámpara de la mesa: cuando lee de noche («a la luz de una lámpara de aceite»). */
    internal fun lamparaDeLaMesa(m: Momento): Boolean = leyendo(m) && luces(m).noche > 0.3f

    /** Las velas de la baranda: la noche de la Candelaria («una vela en cada ventana que da al mar») y la de difuntos. */
    internal fun velasEnLaBaranda(m: Momento): Boolean = (candelaria(m) || difuntos(m)) && luces(m).noche > 0.25f

    /** Las luces que alumbran la galería en este momento. */
    internal fun focos(m: Momento): List<Foco> {
        val l = luces(m)
        val k = maxOf(((l.noche - 0.15f) / 0.6f).coerceIn(0f, 1f), if (niebla(m).ordinal >= Niebla.NIEBLA.ordinal || mojado(m)) 0.3f else 0f)
        if (k <= 0f) return emptyList()
        return buildList {
            if (lamparaDeLaPuerta(m)) add(Foco(LAMPARA, C.ACEITE, 0.85f * k, 70f, suelo = 272f))
            if (lamparaDeLaMesa(m)) add(Foco(GaleriaArcano.LLAMA_MESA, C.ACEITE, 0.7f * k, 52f, suelo = 272f))
            if (m.ficha.objeto == "lampara" && !lamparaDeLaMesa(m) && l.noche > 0.3f && lugar(m) == Lugar.ARCHIVO && m.hacer != Hacer.DORMIR) add(Foco(GaleriaArcano.SOBRE + Offset(0f, -14f), C.ACEITE, 0.6f * k, 46f, suelo = 272f))
            if (m.ficha.objeto == "vela" && l.noche > 0.3f && lugar(m) == Lugar.ARCHIVO && m.hacer != Hacer.DORMIR) add(Foco(GaleriaArcano.SOBRE + Offset(0f, -16f), C.ACEITE, 0.45f * k, 38f, suelo = 272f))
            if (velasEnLaBaranda(m)) { add(Foco(Offset(300f, 212f), C.ACEITE, 0.3f * k, 40f, suelo = 272f)); add(Foco(Offset(176f, 212f), C.ACEITE, 0.22f * k, 34f, suelo = 272f)) }
        }
    }

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val r = rayo(l)
        val semilla = m.ficha.semilla
        val a = Alumbrado(l, focos(m))
        val clima = climaVisto(m)

        // 1. El cielo.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        with(BahiaArcano) { solDeNiebla(m, l) }
        nubes(l, H, clima, semilla)

        // 2. La bahía: los cabos, el mar, la punta del faro, el muelle y sus barcas, con sus capas de niebla.
        with(BahiaArcano) { bahia(m, l, r) }

        // 3. El pueblo, a la derecha, y los tejados de debajo de la galería.
        with(PuebloArcano) { pueblo(m, l, r) }
        with(BahiaArcano) { nieblaCercana(m, l) }

        // 4. El primer plano: la galería del archivo.
        with(GaleriaArcano) { galeria(m, l, r, a) }

        // 5. El velo de la lluvia o de la nieve (la niebla ya va por capas) y el grano del papel.
        if (clima != Clima.NIEBLA) tiempoQuieto(l, clima, H, semilla)
        grano(Azar(Semilla.de("arcano", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el agua siempre se mueve

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        with(BahiaArcano) { bahiaViva(m, l, reloj) }
        with(PuebloArcano) { puebloVivo(m, l, reloj) }
        with(GaleriaArcano) { galeriaViva(m, l, reloj) }
        tiempoVivo(l, climaVisto(m), reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    /** De día, la luz pálida de esta costa; de noche, la de la lámpara que tenga más cerca. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val l = luces(m); val f = focos(m)
        if (f.isEmpty() || l.noche < 0.5f) return LuzGato(rayo(l), l.noche) { l.tono(it) }
        val a = Alumbrado(l, f)
        val manda = f.maxByOrNull { it.fuerza / (1f + ((it.p.x - en.x) * (it.p.x - en.x)) / (it.alcance * it.alcance)) }!!
        val centro = en + Offset(0f, -12f)
        // El gato es negro: de noche, la lámpara le pone el filo que lo despega del fondo.
        return LuzGato(rayoDeFoco(manda, en, 0.6f, C.SOMBRA), l.noche, manda.color) { lerp(l.tono(it), a.en(centro, lerp(it, Color(0xFF4A4A4C), 0.1f)), 0.5f) }
    }

    override fun estorbos(m: Momento): List<Float> = GaleriaArcano.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = GaleriaArcano.anclas(m)

    /** La estación, para lo que cambia con ella en el pueblo y en el monte. */
    internal fun invierno(m: Momento) = m.ficha.estacion == Estacion.INVIERNO
}

/**
 * Una sombra pequeña y suelta (la de una casa lejana, un noray, una barca varada): oscurece lo que tiene debajo sin
 * abrir una capa. Solo vale para sombras que no se enciman con otras; las del primer plano van con `capaSombra`.
 */
internal fun DrawScope.sombraSuelta(r: Rayo, forma: androidx.compose.ui.graphics.Path, cuanta: Float = 0.85f) {
    if (r.fuerza < 0.05f) return
    drawPath(forma, r.color, alpha = (r.fuerza * cuanta).coerceIn(0f, 1f), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply)
}

/** Lo mismo, con un óvalo tendido en el suelo. */
internal fun DrawScope.sombraOvalo(r: Rayo, esquina: Offset, medida: androidx.compose.ui.geometry.Size, cuanta: Float = 0.85f) {
    if (r.fuerza < 0.05f) return
    drawOval(r.color, esquina, medida, alpha = (r.fuerza * cuanta).coerceIn(0f, 1f), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply)
}
