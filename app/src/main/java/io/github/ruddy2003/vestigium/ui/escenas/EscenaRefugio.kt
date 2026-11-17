package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lucesDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Franja
import io.github.ruddy2003.vestigium.vidas.RegimenLuz
import io.github.ruddy2003.vestigium.vidas.luzDe
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.tan

/**
 * La paleta de Refugio: quince colores de materia y siete de luz, para la sala de bombas de la residente, el pasillo grande
 * y la Puerta. Manda el verde de las máquinas pintadas ([VERDE]: «máquinas viejas pintadas de verde») y lo acompañan el
 * crema de los bloques ([CREMA]) y el gris del hormigón y de la roca ([HORMIGON], [ROCA]). Los acentos se repiten de una
 * cosa a otra: el rojo de las ruedas de las válvulas ([ROJO]) vuelve en el extintor, en la luz de la compuerta y en la
 * marca de la columna; el amarillo de la línea del suelo ([AMARILLO]), en la cinta de aviso del portón, en los carteles y
 * en la gradilla; el latón de los manómetros ([LATON]), en los grifos, en las bridas y en la jaula de la lámpara; el azul
 * del mono ([AZUL]), en los cubos esmaltados y en el cielo de los carteles.
 *
 * Del tema salen el verde azulado de la chapa (`#3F6B70` en Vida, aquí [CHAPA]), el crema de los bloques (`#D9CFAF`), el
 * acero (`#8A9294`), el cobre (`#B0703F`), la cinta amarilla (`#E5B436`) y el naranja del cartel (`#D9572B`). Lo demás es
 * **[Diseño propio]**.
 */
internal object ColorRefugio {
    // ---- Quince colores de materia.
    val VERDE = Color(0xFF52806A)                                              // las máquinas pintadas de verde
    val CHAPA = Color(0xFF3F6B70)                                              // la chapa verde azulada: zócalo, cuadro de mando
    val CREMA = Color(0xFFD9CDA9)                                              // los bloques pintados de crema
    val HORMIGON = Color(0xFF8E887A)                                           // el suelo de la sala y el portón
    val ROCA = Color(0xFF746A5B)                                               // la roca húmeda del pasillo
    val ACERO = Color(0xFF8A9294)                                              // tubos, costillas, la Puerta
    val COBRE = Color(0xFFB0703F)                                              // los tubos de cobre
    val LATON = Color(0xFFC59B4C)                                              // los manómetros, los grifos, las bridas
    val ROJO = Color(0xFFB53A2D)                                               // las ruedas de las válvulas, el extintor
    val AMARILLO = Color(0xFFE2B23C)                                           // la línea del suelo, la cinta de aviso
    val NARANJA = Color(0xFFD2622F)                                            // los carteles, la pintura de la jaula
    val TINTA = Color(0xFF222B2A)                                              // el hierro negro, la goma, las juntas
    val AZUL = Color(0xFF4F6E93)                                               // el mono, los cubos esmaltados
    val PAPEL = Color(0xFFE6D8AF)                                              // los carteles amarillos, el cuaderno
    val ALGA = Color(0xFF6E9E48)                                               // las algas y lo que crece

    // ---- Siete luces: la lámpara de trabajo, los tubos del pasillo, el azul de la noche, el violeta de los tanques, la
    //      lucecita de guardia, la alarma y la sombra.
    val LAMPARA = Color(0xFFFFB766)
    val TUBO = Color(0xFFE6F2EC)
    val PISCINA = Color(0xFF5AA6D6)
    val VIOLETA = Color(0xFFB07EEA)
    val GUARDIA = Color(0xFFFFA548)
    val ALARMA = Color(0xFFFF4A38)
    val SOMBRA = Color(0xFF45507E)

    // ---- Mezclas de las de arriba, con nombre para no repetir la cuenta.
    val PENUMBRA = lerp(CHAPA, SOMBRA, 0.55f)                                  // la penumbra verde azulada honda de la sala
    val HIERRO = lerp(TINTA, CHAPA, 0.3f)                                      // el hierro pintado de negro
    val OXIDO = lerp(COBRE, TINTA, 0.35f)                                      // el óxido de las bridas y los pernos
    val CAL = lerp(CREMA, Color.White, 0.35f)                                  // la cal de las goteras
    val BARRO = lerp(TINTA, ROCA, 0.35f)                                       // el barro negro del filtro
    val VERDE_CLARO = lerp(VERDE, PAPEL, 0.35f)                                // el verde gastado de los cantos
    val FUEGO = lerp(GUARDIA, NARANJA, 0.35f)                                  // el fuego de fuera
}

/**
 * Refugio: el mundo de la residente, técnica de purificación de agua en un refugio bajo tierra, de 2105 a 2109. Todo es
 * ficción. Es un **interior con dos luces** (`docs/diseno/ESCENAS.md`, sección 5, como Espacio): no hay cielo ni sol, salvo
 * el que se ve por la Puerta cuando se abre.
 *
 * **El cuadro** (`composicion_refugio.md` de la tanda S17): de pie dentro de **la sala de bombas**, mirando en diagonal
 * hacia su portón y, por él, a lo largo del **pasillo grande**, hasta **la Puerta** del fondo. Que desde la sala de bombas
 * se vea la Puerta es **[Diseño propio]**: el cuento pone las bombas «abajo del todo» y la Puerta «al final» del pasillo
 * grande, sin decir cómo se juntan. Los planos, cada uno en su archivo ([SalaRefugio], `EscenaRefugioSala.kt`;
 * [PasilloRefugio], `EscenaRefugioPasillo.kt`; [LuzRefugio], `EscenaRefugioLuz.kt`; las piezas, [PiezasRefugio]).
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG7.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **El estado** ([estado]): el refugio cerrado de siempre; el racionamiento (la cisterna rota), la cuarentena (el sector B
 *   cerrado) y el «abierto» (la Puerta abierta). Los tres cambian el mismo cuadro.
 * - **Los arcos:** la columna de nivel de la cisterna lee [reserva] (sus tramos, [tramoReserva]); los tanques del ventanal
 *   leen [cultivo] ([tramoCultivo]).
 * - **El horario** («Un día suyo»): dónde cuelga la lámpara de trabajo ([lampara], [lamparas]), el cuaderno, la fiambrera
 *   y el mono ([cuadernoAbierto], [fiambreraAbierta], [monoColgado]).
 * - **La luz** ([deDia], [tubos], [nocheAzul], [tanques], [guardia], [alarma], [solFalso]): las luces del refugio suben a
 *   las 5:00 y bajan a las 21:00 (la franja de la ficha manda: el cuento dice a las 22:00, **[Aproximación]**).
 * - **La compuerta** del sector ([compuerta]) y **la Puerta** ([puerta]); fuera, cuando está abierta, el cielo de la hora
 *   ([cieloDeFuera]), el sol que entra y camina por el pasillo ([solPorLaPuerta], [alcanceDelSol]) y el fuego de la noche
 *   ([fuego]).
 * - **Los días del cuento** ([hoyEs]): el simulacro, la bomba tres, los filtros, la cosecha, el sellado, las fiestas, el
 *   luto, la grieta, la señal, el tocadiscos, la salida y la vuelta de la expedición.
 */
internal object EscenaRefugio : EscenaVida {
    override val vidas = listOf("residente")
    /** La franja del fondo del teléfono ([franjaFondo]): la bancada y, por el portón, el pasillo con la Puerta al fondo. */
    override val franjaFondo: Float get() = 0.65f

    // ---------------------------------------------------------------- El estado

    /** El estado del refugio: [clave] es la del calendario. El cerrado de siempre es lo que queda sin estado. */
    internal enum class Estado(val clave: String) { CERRADO(""), RACIONAMIENTO("racionamiento"), CUARENTENA("cuarentena"), ABIERTO("abierto") }

    /** El estado de hoy, por el del calendario: lo vigila `EscenaRefugioPrueba`. Un estado que no se conoce, el cerrado. */
    internal fun estado(m: Momento): Estado = Estado.entries.firstOrNull { it.clave == m.estado } ?: Estado.CERRADO

    internal fun racionamiento(m: Momento): Boolean = estado(m) == Estado.RACIONAMIENTO
    internal fun cuarentena(m: Momento): Boolean = estado(m) == Estado.CUARENTENA
    internal fun abierto(m: Momento): Boolean = estado(m) == Estado.ABIERTO

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [temporada]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, temporada: Int, fecha: String): Boolean = m.ficha.temporada > temporada || (m.ficha.temporada == temporada && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    // ---------------------------------------------------------------- Los arcos

    /** El agua de la cisterna grande, de 0 a 1 (el arco `reserva`): la lee la columna de nivel de la pared de manómetros. */
    internal fun reserva(m: Momento): Float = m.arco("reserva").coerceIn(0f, 1f)

    /** Cómo está la cisterna: baja (la grieta y el racionamiento), a medias (lo que tarda en volver a llenarse) o llena. */
    internal enum class TramoReserva { BAJA, MEDIA, LLENA }

    /** El tramo de la cisterna, con los cortes del cuento (`{entorno}`: «a medias» por debajo de 0.6) y la marca roja, en 0.35. */
    internal fun tramoReserva(m: Momento): TramoReserva { val r = reserva(m); return when { r < MARCA_ROJA -> TramoReserva.BAJA; r < 0.6f -> TramoReserva.MEDIA; else -> TramoReserva.LLENA } }

    /** La marca roja de la columna de nivel: por debajo, la cisterna está en peligro **[Diseño propio]**. */
    const val MARCA_ROJA = 0.35f

    /** Lo que ha crecido en los tanques, de 0 a 1 (el arco `cultivo`): lo leen las algas del ventanal. */
    internal fun cultivo(m: Momento): Float = m.arco("cultivo").coerceIn(0f, 1f)

    /** Los tramos del cuento: recién sembrados (hay más cristal que verde), verdes y espesos, y llenos y oscuros de algas. */
    internal enum class TramoCultivo { SEMBRADO, VERDE, LLENO }

    internal fun tramoCultivo(m: Momento): TramoCultivo { val c = cultivo(m); return when { c < 0.35f -> TramoCultivo.SEMBRADO; c < 0.75f -> TramoCultivo.VERDE; else -> TramoCultivo.LLENO } }

    // ---------------------------------------------------------------- El horario de la residente

    private fun trabajando(m: Momento) = m.hacer == Hacer.TRABAJO
    private fun comiendoAqui(m: Momento) = m.hacer == Hacer.COMER && m.hora >= 12.0 && m.hora < 13.0
    private fun deTarde(m: Momento) = m.hacer == Hacer.LIBRE || m.hacer == Hacer.GENTE

    /**
     * Dónde cuelga la lámpara de trabajo, que ella mueve a donde trabaja **[Diseño propio]**: a primera hora, sobre la pared
     * de manómetros («leyendo uno a uno los números del agua»); desde las 9:20, sobre la bomba grande («tumbada debajo de una
     * bomba vieja… apretando una junta que gotea»); a la hora de comer, sobre la bancada («de una fiambrera, sin dejar de
     * mirar los manómetros»); por la tarde, sobre el filtro grande y la bancada.
     */
    internal enum class Obra { MANOMETROS, BOMBA, BANCADA, FILTRO }

    /**
     * Las lámparas encendidas en la sala, de 0 a 2 (la primera es la que manda). Fuera de su turno, ninguna: «Apaga la
     * lámpara de la sala de bombas». Los días de obra, la lámpara no se mueve de ella y se queda hasta tarde (la bomba tres,
     * hasta las 19:00; los filtros, «tres días de trabajo doble», hasta las 21:00). En el racionamiento y en la cuarentena,
     * el turno de más, de 17:00 a 22:00, en la bancada; en la cuarentena, además, «mantiene las bombas del sector A sola»:
     * dos lámparas a la vez en su turno.
     */
    internal fun lamparas(m: Momento): List<Obra> {
        val h = m.hora
        if (bombaTres(m) && (trabajando(m) || comiendoAqui(m) || m.hacer == Hacer.LIBRE)) return listOf(Obra.BOMBA)
        if (filtros(m) && (trabajando(m) || comiendoAqui(m) || m.hacer == Hacer.LIBRE || (m.hacer == Hacer.GENTE && h < 21.0))) return listOf(Obra.FILTRO)
        val una = when {
            trabajando(m) -> when { h < 9.3 -> Obra.MANOMETROS; h < 12.0 -> Obra.BOMBA; else -> Obra.FILTRO }
            comiendoAqui(m) -> Obra.BANCADA
            (racionamiento(m) || cuarentena(m)) && deTarde(m) -> Obra.BANCADA
            else -> null
        } ?: return emptyList()
        if (cuarentena(m) && trabajando(m)) return listOf(una, if (una == Obra.BOMBA) Obra.FILTRO else Obra.BOMBA)
        return listOf(una)
    }

    /** La lámpara que manda, o null si está apagada. */
    internal fun lampara(m: Momento): Obra? = lamparas(m).firstOrNull()

    /** El cuaderno del turno, abierto en la bancada mientras ella trabaja (y en el turno de más); cerrado, si no. */
    internal fun cuadernoAbierto(m: Momento): Boolean = lampara(m) != null

    /** La fiambrera abierta en la bancada: de la hora de comer a media tarde (a las 13:00 todavía está ahí). */
    internal fun fiambreraAbierta(m: Momento): Boolean = m.hora >= 12.0 && m.hora < 13.7 && !estaFuera(m)

    /** El mono azul en su gancho: de día lo lleva puesto; desde que deja el turno hasta que vuelve, cuelga. */
    internal fun monoColgado(m: Momento): Boolean = lampara(m) == null && (m.hora < 6.0 || m.hora >= 17.0)

    /** Si ella está fuera de la sala (en el comedor, en la terminal o durmiendo): la sala queda sola. */
    internal fun estaFuera(m: Momento): Boolean = lampara(m) == null

    // ---------------------------------------------------------------- La luz del refugio

    /** Si las luces del refugio están altas: de 5:00 a 21:00, la franja «encendido» de la ficha. */
    internal fun deDia(m: Momento): Boolean = m.luz.franja == Franja.ENCENDIDO

    /** Los tubos del techo del pasillo, de 0 (apagados) a 1: «suben de golpe en todo el refugio a la misma hora». */
    internal fun tubos(m: Momento): Float = if (deDia(m)) 1f else 0f

    /** El azul de la noche, de 0 a 1: «la luz tenue de la noche, azul, como el fondo de una piscina». */
    internal fun nocheAzul(m: Momento): Float = if (deDia(m)) 0f else 1f

    /**
     * La luz violeta de los tanques de cultivo, de 0 a 1: de día apenas se nota; de noche es «la última luz del día…, que se
     * queda encendida más que las demás»; de la 1:00 a las 5:00 queda baja **[Diseño propio]**.
     */
    internal fun tanques(m: Momento): Float = when {
        deDia(m) -> 0.5f
        m.hora >= 21.0 || m.hora < 1.0 -> 1f
        else -> 0.4f
    }

    /** La lucecita de guardia de la sala, ámbar, sobre el cuadro de mando: siempre encendida; de noche es lo único. */
    internal fun guardia(m: Momento): Float = if (deDia(m)) 0.3f else 1f

    /**
     * Las luces rojas de alarma: los días con la clave `alarma` (la bomba tres, la grieta, la fiebre, la salida de la
     * expedición) todo el día; el del simulacro, mientras dura (de 7:00 a 17:00, el turno: **[Diseño propio]**).
     */
    internal fun alarma(m: Momento): Boolean = m.con("alarma") && (!simulacro(m) || (m.hora >= 7.0 && m.hora < 17.0))

    /** La fiesta del sol falso: los tubos del pasillo, al máximo y calientes, «desde que se encienden hasta que se apagan». */
    internal fun solFalso(m: Momento): Boolean = hoyEs(m, "sol_falso") && deDia(m)

    /**
     * La luz de dentro del refugio: la penumbra de la sala, que no es gris (verde azulada honda de día, azul de noche). La
     * usan las piezas que piden unas `Luces` (los objetos, el gato) y el grano; la luz de cada cosa la da [VistaRefugio].
     */
    internal fun luces(m: Momento): Luces {
        val n = nocheAzul(m)
        val amb = lerp(Color(0xFFB4BEB8), Color(0xFF55618A), n)
        val bruma = lerp(Color(0xFF6E7C80), Color(0xFF263452), n)
        return Luces(
            alto = bruma, medio = bruma, bajo = bruma, ambiente = amb, bruma = bruma, brumaFuerza = 0.7f,
            solX = 0.5f, solAltura = -1f, solColor = Color.White, solFuerza = 0f, lunaX = 0.5f, lunaAltura = -1f,
            estrellas = 0f, noche = n, nublado = 0f, eclipse = 0f,
        )
    }

    // ---------------------------------------------------------------- La compuerta y la Puerta

    /** La compuerta del sector, en la pared del pasillo: abierta (la hoja recogida arriba) o bajada. */
    internal enum class Compuerta { ABIERTA, BAJADA }

    /** Bajada toda la cuarentena («compuertas cerradas y luces de aviso») y mientras dura el simulacro. */
    internal fun compuerta(m: Momento): Compuerta = if (cuarentena(m) || (simulacro(m) && alarma(m))) Compuerta.BAJADA else Compuerta.ABIERTA

    /** La Puerta del fondo: cerrada (la de siempre), entornada (el día que salen los cinco y el día que vuelven) o abierta. */
    internal enum class Puerta { CERRADA, ENTORNADA, ABIERTA }

    /** La Puerta, abierta todo el estado «abierto»; entornada, con un filo de luz, los días `salen` y `vuelven` **[Diseño propio]**. */
    internal fun puerta(m: Momento): Puerta = when {
        abierto(m) -> Puerta.ABIERTA
        hoyEs(m, "salen") || hoyEs(m, "vuelven") -> Puerta.ENTORNADA
        else -> Puerta.CERRADA
    }

    // ---------------------------------------------------------------- Fuera, por la Puerta abierta

    /**
     * El tiempo que hace fuera **[Diseño propio]**: la ficha no tiene clima (bajo tierra no llueve), así que fuera se sortea
     * con el día (despejado, nubes, viento; el cuento da la luz «gris, débil» y el viento «que no para»), salvo el día
     * `llueve`, con su lluvia.
     */
    internal fun climaDeFuera(m: Momento): Clima {
        if (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA) return Clima.LLUVIA
        val r = m.azar("fuera", "cielo").real()
        return when { r < 0.4f -> Clima.DESPEJADO; r < 0.8f -> Clima.NUBES; else -> Clima.VIENTO }
    }

    /**
     * El cielo de fuera a esta hora, el «rectángulo de cielo, que cambia de color con las horas, azul, gris, naranja,
     * negro» **[Diseño propio]**: la ficha no tiene cielo (sus luces son las de dentro), así que la escena hace el suyo con la
     * hora, con las seis franjas de las demás escenas.
     */
    internal fun cieloDeFuera(m: Momento): Luces = lucesDe(luzDe(m.hora, RegimenLuz.SEIS_FRANJAS), climaDeFuera(m))

    /**
     * El sol que entra por la Puerta abierta: su [elevacion] y su [azimut] (en grados; 0, de frente a lo largo del pasillo;
     * positivo, hacia la pared de la derecha), su [fuerza] y su [color].
     */
    internal class SolDePuerta(val elevacion: Float, val azimut: Float, val fuerza: Float, val color: Color)

    /**
     * El sol que entra a esta hora por la Puerta abierta, o null si no entra **[Aproximación]**: la Puerta mira al sureste,
     * al fondo de un valle hondo, así que el sol entra desde que asoma por la loma (6:00) hasta que se va detrás del monte
     * (17:00): de lado por la mañana, de frente hacia las 11:30, de lado otra vez por la tarde; y como entra por encima de
     * la loma de enfrente, siempre bajo (entre 3° y 11°), así que su mancha se tiende largo por el suelo del pasillo. Por la
     * tarde solo entra la luz del cielo («gris, débil»), que se pone naranja. No es el recorrido real del sol para una
     * Puerta con una orientación dada (la misma aproximación que la capilla de Roma): la altura va comprimida.
     */
    internal fun solPorLaPuerta(m: Momento): SolDePuerta? {
        if (puerta(m) != Puerta.ABIERTA) return null
        val h = m.hora.toFloat(); if (h < 6.0f || h > 17.0f) return null
        val l = cieloDeFuera(m); if (l.solAltura <= 0.02f || l.solFuerza < 0.1f) return null
        val az = 84f * ((h - 11.5f) / 5.5f)
        if (abs(az) > 84f) return null
        val elev = 3f + 8f * (l.solAltura / 1.35f).coerceIn(0f, 1f)
        val fuerza = (kotlin.math.sqrt(l.solFuerza) * (l.solAltura / 0.1f).coerceIn(0f, 1f) * (1f - 0.4f * l.nublado)).coerceIn(0f, 1f)
        if (fuerza < 0.05f) return null
        return SolDePuerta(elev, az, fuerza, lerp(l.solColor, Color.White, 0.12f))
    }

    /** Hasta dónde llega el sol por el suelo del pasillo, en metros desde la Puerta (0 si no entra): lo que camina con la hora. */
    internal fun alcanceDelSol(m: Momento): Float = solPorLaPuerta(m)?.let { s ->
        val alto = PasilloRefugio.PUERTA_C.y + PasilloRefugio.PUERTA_R
        (alto / tan(Math.toRadians(s.elevacion.toDouble())).toFloat() * cos(Math.toRadians(s.azimut.toDouble())).toFloat()).coerceIn(0f, PasilloRefugio.LARGO)
    } ?: 0f

    /**
     * El fuego de fuera, delante de la Puerta, de 0 a 1: en el «abierto», de noche («alrededor de un fuego de verdad»), desde
     * que oscurece hasta medianoche; de madrugada, el rescoldo **[Diseño propio]**.
     */
    internal fun fuego(m: Momento): Float {
        if (!abierto(m)) return 0f
        val h = m.hora
        return when { h >= 20.3 -> 1f; h < 2.0 -> 0.45f; else -> 0f }
    }

    // ---------------------------------------------------------------- Los días del cuento

    /** El simulacro de cierre (11 de noviembre): «todas las compuertas bajan a la vez… Las luces se ponen rojas». */
    internal fun simulacro(m: Momento): Boolean = hoyEs(m, "simulacro")

    /** La bomba tres rota (12 de febrero de la temporada 1): desmontada «pieza por pieza», y el agua marrón en un cubo. */
    internal fun bombaTres(m: Momento): Boolean = hoyEs(m, "bomba_tres")

    /** La revisión de los filtros grandes (tres días desde el 10 de abril): el filtro abierto, el barro negro, el suelo mojado. */
    internal fun filtros(m: Momento): Boolean = hoyEs(m, "filtros")

    /** La cosecha grande (tres días desde el 20 de mayo): las cestas de algas por el pasillo y los tanques vaciándose. */
    internal fun cosecha(m: Momento): Boolean = m.con("cosecha")

    /** El aniversario del sellado (15 de enero): las flores de papel delante de la Puerta, en la línea amarilla. */
    internal fun sellado(m: Momento): Boolean = hoyEs(m, "sellado")

    /** Un día de fiesta (año nuevo, fundadores, el sol falso, la Nochebuena, el hijo de Lina, el fin de año). */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    /** El luto por la vieja Dorotea: un paño oscuro en el gancho y una flor de papel. */
    internal fun luto(m: Momento): Boolean = m.con("luto")

    /** El día que se agrieta la cisterna (14 de marzo de la temporada 2): la columna ya cae y salta la alarma; todavía no se raciona. */
    internal fun grieta(m: Momento): Boolean = hoyEs(m, "grieta")

    /** La radio de la entrada capta una señal de fuera (30 de agosto de la temporada 3): la radio de la sala, encendida **[Diseño propio]**. */
    internal fun senal(m: Momento): Boolean = hoyEs(m, "senal")

    /** Se rompe el tocadiscos del comedor (20 de septiembre de la temporada 2): lo traen a la bancada para abrirlo **[Diseño propio]**. */
    internal fun tocadiscosRoto(m: Momento): Boolean = hoyEs(m, "tocadiscos")

    /** El árbol de Nochebuena, de cable verde y trozos de tubo, en el pasillo, del 18 al 31 de diciembre **[Diseño propio]**. */
    internal fun arbolDeCable(m: Momento): Boolean = mesDia(m) >= "12-18"

    /** Sus luces «solo se encienden esta noche»: la Nochebuena, desde las 19:00. */
    internal fun lucesDelArbol(m: Momento): Boolean = hoyEs(m, "nochebuena") && (m.hora >= 19.0 || !deDia(m))

    /** Las gafas de cartón y celofán del sol falso, en la bancada. */
    internal fun gafasDeCarton(m: Momento): Boolean = hoyEs(m, "sol_falso")

    /** La guirnalda de flores de papel en la bancada, los días de fiesta. */
    internal fun guirnalda(m: Momento): Boolean = fiesta(m)

    /** El barro de las botas en la línea amarilla, el día que vuelven los tres. */
    internal fun barroDeBotas(m: Momento): Boolean = hoyEs(m, "vuelven")

    /** La flor de verdad que trajo Noa, en un frasco de la bancada, desde que vuelven (28 de julio de la temporada 4). */
    internal fun florDeVerdad(m: Momento): Boolean = paso(m, 4, "07-28")

    /** La flor de verdad ya seca, un mes después **[Diseño propio]**. */
    internal fun florSeca(m: Momento): Boolean = florDeVerdad(m) && paso(m, 4, "08-28")

    /** Las latas de antes de la guerra abiertas en la bancada: el postre del año nuevo y la tarta de lata de los fundadores. */
    internal fun latas(m: Momento): Boolean = hoyEs(m, "anio_nuevo") || hoyEs(m, "fundadores")

    /** Las etiquetas rojas en las ruedas de las válvulas de emergencia, que ella cierra «una por una» el día del simulacro. */
    internal fun etiquetasRojas(m: Momento): Boolean = simulacro(m)

    /** El primer día del «abierto» (`abre`): los papeles de colores y las huellas de todos hacia la Puerta, la guirnalda del portón. */
    internal fun primerDia(m: Momento): Boolean = hoyEs(m, "abre")

    /** Una puerta del pasillo con luz detrás, de noche: alguien despierto en su cuarto (cada noche una, o ninguna) **[Diseño propio]**. */
    internal fun rendija(m: Momento): Int {
        if (deDia(m)) return -1
        val r = m.azar("rendija").real()
        return if (r < 0.75f) (r * 4f).toInt().coerceIn(0, 2) else -1
    }

    // ---------------------------------------------------------------- Lo que dejan los estados

    /** El racionamiento: el grifo medidor junto al portón, con las botellas en fila en el suelo del pasillo. */
    internal fun botellasEnFila(m: Momento): Boolean = racionamiento(m)

    /** El candado en la llave de paso de las duchas, y los filtros saturados apilados: el racionamiento. */
    internal fun candado(m: Momento): Boolean = racionamiento(m)

    /** La cuarentena: las mascarillas de tela colgadas, el jabón amarillo, la ventanilla de la compuerta con botellas y un paquete. */
    internal fun mascarillas(m: Momento): Boolean = cuarentena(m)

    /** La cuarentena: ella lleva las bombas sola y las herramientas quedan por todas partes. */
    internal fun herramientasPorTodas(m: Momento): Boolean = cuarentena(m)

    /** El «abierto»: hojas secas y tierra que entran por el pasillo, y los bidones para el arroyo junto al portón. */
    internal fun hojasSecas(m: Momento): Boolean = abierto(m)

    /** Un pájaro posado en el tubo gordo, de día, con la Puerta abierta (en la parte viva). */
    internal fun pajaro(m: Momento): Boolean = abierto(m) && deDia(m) && m.ficha.clima != Clima.LLUVIA

    // ---------------------------------------------------------------- Los objetos del día que ya están en el cuadro

    /**
     * Los objetos del día que también son piezas de la sala: el tocadiscos pequeño y el cuaderno del turno (en la bancada),
     * el mono (en su gancho), la llave de válvulas (apoyada en la bomba grande) y la linterna (en su percha). El día que uno
     * es el objeto, la sala no pinta el suyo si `ObjetosRefugio` ya lo dibuja ([ObjetosRefugio.DIBUJADOS]), para que no
     * salga dos veces.
     */
    internal val DE_LA_SALA = setOf("tocadiscos", "cuaderno", "mono", "llave", "linterna")

    /** Si la sala pinta hoy su [pieza] (no la pinta si es el objeto del día y el contrato de objetos ya la dibuja). */
    internal fun laSalaPinta(m: Momento, pieza: String): Boolean = !(m.ficha.objeto == pieza && pieza in ObjetosRefugio.DIBUJADOS)

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) { with(SalaRefugio) { cuadro(VistaRefugio(m)) } }

    override fun seMueve(m: Momento): Boolean = true                          // las burbujas de los tanques y la gota no paran

    override fun DrawScope.viva(m: Momento, reloj: Float) { with(LuzRefugio) { vivo(VistaRefugio(m), reloj) } }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = SalaRefugio.luzDelGato(VistaRefugio(m), en)

    override val tallaDelGato: Float get() = 1.25f

    override fun estorbos(m: Momento): List<Float> = SalaRefugio.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = SalaRefugio.anclas(m)
}
