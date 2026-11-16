package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lucesDe
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Franja
import io.github.ruddy2003.vestigium.vidas.RegimenLuz
import io.github.ruddy2003.vestigium.vidas.luzDe

/**
 * La paleta del laboratorio de la doctora: catorce colores de materia y cuatro de luz. Manda el blanco del hormigón
 * pintado ([HORMIGON]), que nunca es blanco puro: en la sombra se va a azul verdoso y de noche a azul. Lo acompañan el
 * gris de la tierra de arriba ([TIERRA]) y el verde de la grama ([GRAMA]), que es lo único que crece. Los acentos se
 * repiten de una cosa a otra: el barro cocido ([BARRO]) en las macetas, la taza y la caja de tierra; el latón ([LATON])
 * en el microscopio, la balanza y la llave del riego; el cian del tema ([CIAN]) en las cintas de las bandejas, la flecha
 * del suelo y el canto de la bata; el violeta del tema ([VIOLETA]) en la luz de noche de los jardines y en las lámparas
 * de cultivo de noche; el amarillo ([AMARILLO]) en los guantes y en la cinta del precinto.
 *
 * Del tema de la app (blanco clínico, cian y violeta) se queda eso; lo demás sale del cuento, que es botánica bajo
 * tierra: hormigón, vidrio y metal, tierra gris, barro cocido y latón viejo (`lectura_laboratorio.md`, §7 y §8).
 * **[Diseño propio]**: los tonos exactos.
 */
internal object ColorLaboratorio {
    // ---- Catorce colores de materia.
    val HORMIGON = Color(0xFFE2E4DC)                                            // las paredes, pintadas de blanco
    val PISO = Color(0xFFA6ABA6)                                                // el hormigón pulido del suelo
    val ACERO = Color(0xFF7D8890)                                               // los marcos, el riel, las patas
    val VIDRIO = Color(0xFFBFDCD6)                                              // el vidrio grueso, verdoso en el canto
    val TIERRA = Color(0xFF8F8C85)                                              // la tierra gris de arriba
    val TIERRA_LIMPIA = Color(0xFF4E3F33)                                       // la tierra que la grama ya limpió
    val GRAMA = Color(0xFF6FA04A)                                               // la grama, la hoja de los jardines
    val BARRO = Color(0xFFB0674A)                                               // las macetas, la taza, la caja
    val LATON = Color(0xFFC39A4C)                                               // el microscopio, la balanza, la llave
    val MADERA = Color(0xFF8C6A45)                                              // el estante, el taburete, las cajas
    val CIAN = Color(0xFF2E96A2)                                                // las cintas, la flecha, el canto de la bata
    val AMARILLO = Color(0xFFE0B63E)                                            // los guantes, el precinto
    val NEGRO = Color(0xFF23272E)                                               // la tableta, el plato, la goma
    val TELA = Color(0xFFEDEBE3)                                                // la bata, el papel de fibra, la sábana

    // ---- Las luces: la de las lámparas de cultivo, la lámpara de su mesa, el violeta de los jardines de noche, el rojo
    //      de aviso; y el azul violeta de las sombras.
    val LUZ_DIA = Color(0xFFFFF3DC)
    val LAMPARA = Color(0xFFFFBE72)
    val VIOLETA = Color(0xFF9A86F0)
    val AVISO = Color(0xFFE5483E)
    val SOMBRA = Color(0xFF5C6898)

    // ---- Mezclas con nombre, para no repetir la cuenta.
    val HOJA_OSC = lerp(GRAMA, NEGRO, 0.45f)                                     // el verde de lo hondo de una mata
    val TOMATE = lerp(AVISO, BARRO, 0.25f)                                       // los tomates de los jardines
    val CUELLO = lerp(GRAMA, NEGRO, 0.25f)                                       // el cuello verde de la bata
    val ESCOMBRO = lerp(TIERRA, HORMIGON, 0.35f)                                 // la piedra de las ruinas
    val HIERRO = lerp(ACERO, BARRO, 0.35f)                                       // los hierros oxidados de arriba
    val PAJA = lerp(AMARILLO, TIERRA, 0.45f)                                     // la hierba rala y amarilla de arriba
    val VELA = lerp(LAMPARA, Color.White, 0.25f)                                 // la cera de las colmenas, encendida
}

/**
 * Laboratorio: el mundo de la doctora, botánica del Claustro, un laboratorio sellado bajo una ciudad en ruinas, de 2287 a
 * 2291. Todo es ficción. Es un **interior con dos luces** (`docs/diseno/ESCENAS.md`, sección 5), sin ventanas («aquí
 * nadie la tiene»): **el laboratorio de la grama**, mirado de pie desde un rincón, en diagonal (`composicion_laboratorio.md`
 * de la tanda S17). Lo pintan [SalaLab], [CampanaLab], [MesaLab], [PantallaLab] y [LuzLab] (`EscenaLaboratorio*.kt`).
 *
 * - **Lo grande:** la campana de cultivo, un cilindro de vidrio grueso en medio de la sala.
 * - **Delante:** la mesa larga en diagonal, con las bandejas de tierra gris y lo del oficio, y las lámparas de cultivo
 *   colgadas bajas sobre ella.
 * - **Al fondo:** la pared de hormigón pintado con el estante de los cuadernos, la percha y la puerta de vidrio; por ella, el
 *   pasillo de Botánica y, al final, los jardines.
 * - **A la derecha:** la pantalla de la Vigía, con las ruinas de arriba a su hora de verdad: el único cielo del cuadro.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario del calendario en `AnioG8.kt`):
 * - **Dónde está** ([lugar], [cuadro], [provisional]): el laboratorio; la cuarentena, la campana y el cierre son el mismo
 *   cuadro con lo suyo; arriba, el laboratorio sin ella y el campamento en la pantalla, provisional.
 * - **Los tres arcos:** los jardines por la puerta ([jardines], [tramoJardines]), la grama en la mesa ([investigacion],
 *   [grama]) y lo verde de las ruinas en la pantalla ([superficie]).
 * - **La luz** ([cultivo], [encendidas], [lamparaDeMesa], [cielo], [lucesDeArriba], [luces]): las lámparas de cultivo caminan
 *   con el horario del riego, la pantalla enseña el cielo de arriba a su hora y tiñe la sala; de noche, tres luces.
 * - **Los días del cuento** ([hoyEs], [paso]): la germinación, la siega, la Fundación, el año nuevo, la luz larga, la
 *   noche larga, el apagón, el humo, el verde, los cuadernos de Hilda, Lauro y su bata.
 */
internal object EscenaLaboratorio : EscenaVida {
    override val vidas = listOf("cientifica")

    // ---------------------------------------------------------------- Dónde está

    /** Dónde está la doctora. [clave] es el estado del calendario; el laboratorio es lo que queda cuando no hay ninguno. */
    internal enum class Lugar(val clave: String) { LABORATORIO(""), CUARENTENA("cuarentena"), CAMPANA("experimento"), CIERRE("cierre"), ARRIBA("arriba") }

    /** Qué cuadro se pinta: por ahora uno solo, el laboratorio. */
    internal enum class Cuadro { LABORATORIO }

    /** Dónde está hoy, por el estado del calendario: lo vigila `EscenaLaboratorioPrueba`. Un estado que no se conoce, el laboratorio. */
    internal fun lugar(m: Momento): Lugar = Lugar.entries.firstOrNull { it.clave == m.estado } ?: Lugar.LABORATORIO

    /**
     * El cuadro de cada lugar. La cuarentena, la campana y el cierre pasan en el mismo laboratorio (el cuento: «en un
     * catre del laboratorio», «un cilindro de vidrio grueso en medio del laboratorio», «en el laboratorio con un vigía
     * sentado en la puerta»): son variantes del cuadro. Arriba, por ahora, también (ver [provisional]).
     */
    internal fun cuadro(l: Lugar): Cuadro = when (l) {
        Lugar.LABORATORIO -> Cuadro.LABORATORIO
        Lugar.CUARENTENA -> Cuadro.LABORATORIO
        Lugar.CAMPANA -> Cuadro.LABORATORIO
        Lugar.CIERRE -> Cuadro.LABORATORIO
        Lugar.ARRIBA -> Cuadro.LABORATORIO
    }

    internal fun cuadro(m: Momento): Cuadro = cuadro(lugar(m))

    /**
     * Si hoy se pinta una variante provisional: arriba (28 días en 2031 y cada año desde 2032) se pinta como el laboratorio
     * sin ella, con la mesa recogida, y en la pantalla de la Vigía el campamento de arriba con el cielo y el tiempo de ese
     * día: es lo que el Claustro ve de su subida **[Diseño propio]**. Si el autor quiere un cuadro propio de las ruinas,
     * irá en otra tanda.
     */
    internal fun provisional(m: Momento): Boolean = when (lugar(m)) {
        Lugar.LABORATORIO -> false
        Lugar.CUARENTENA -> false
        Lugar.CAMPANA -> false
        Lugar.CIERRE -> false
        Lugar.ARRIBA -> true
    }

    internal fun arriba(m: Momento): Boolean = lugar(m) == Lugar.ARRIBA
    internal fun cuarentena(m: Momento): Boolean = lugar(m) == Lugar.CUARENTENA
    internal fun enLaCampana(m: Momento): Boolean = lugar(m) == Lugar.CAMPANA
    internal fun cierre(m: Momento): Boolean = lugar(m) == Lugar.CIERRE

    /** El pasillo con su luz roja de aviso: la cuarentena («cada compuerta con su luz roja») y el cierre («las luces de aviso en todos los pasillos»). */
    internal fun luzRoja(m: Momento): Boolean = cuarentena(m) || cierre(m)

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Qué día es hoy del día especial [id] (1 el primero), o 0 si hoy no lo es. */
    internal fun diaNumero(m: Momento, id: String): Int = m.ficha.especiales.firstOrNull { it.dia.id == id }?.numero ?: 0

    // ---------------------------------------------------------------- Los jardines (arco `jardines`)

    /** Lo que ha crecido en los tanques de los jardines, de 0 a 1. */
    internal fun jardines(m: Momento): Float = m.arco("jardines").coerceIn(0f, 1f)

    /** Los cuatro tramos del cuento (`ENTORNOS` en `PiezasCientifica.kt`): rangos cerrados, manda el primero que se cumple. */
    internal enum class Jardines { VACIOS, BROTES, MEDIO, LLENOS }

    internal fun tramoJardines(m: Momento): Jardines {
        val v = jardines(m)
        return if (v <= 0.08f) Jardines.VACIOS else if (v <= 0.35f) Jardines.BROTES else if (v <= 0.75f) Jardines.MEDIO else Jardines.LLENOS
    }

    /**
     * El día de la siega grande (del 10 al 12 de septiembre), de 1 a 3, o 0. El arco cae a 0.1 el primer día, pero el
     * cuento todavía corta hojas: el primer día la nave se ve a medio segar, con las cestas; el segundo y el tercero, «una
     * nave de vidrio limpio» **[Diseño propio]**.
     */
    internal fun siega(m: Momento): Int = diaNumero(m, "siega")

    // ---------------------------------------------------------------- La grama (arco `investigacion`)

    /** Lo que lleva hecho de la grama, de 0 a 1. */
    internal fun investigacion(m: Momento): Float = m.arco("investigacion").coerceIn(0f, 1f)

    /**
     * Cómo se ve la grama en la mesa, por los hitos del arco **[Diseño propio]** (el cuento solo da los hitos):
     * - [TIERRA] (antes de `germina`, menos de 0.25): bandejas de tierra gris con su tapa, y nada verde: «once años
     *   sembrando grama en tierra de arriba y once años viéndola morir»;
     * - [HILO] (desde `germina`, 0.25): la bandeja cuarenta y siete destapada, con un hilo verde; hacia la campana, alguna
     *   más con su brote;
     * - [MATAS] (desde `entra_campana`, 0.55): las bandejas con matas de grama en la tierra gris;
     * - [TANQUE] (desde `funciona`, 0.9): al final de la mesa, un tanque de tierra limpia y oscura con la grama espesa;
     * - [CAJAS] (desde `sube`, 0.92): la grama en cajas, para subir. Mientras ella está arriba, las cajas no están.
     */
    internal enum class Grama { TIERRA, HILO, MATAS, TANQUE, CAJAS }

    internal fun grama(m: Momento): Grama {
        // Sin holgura: el valor del día del hito es justo el del punto del arco (el mismo Double llevado a Float).
        val v = investigacion(m)
        return if (v < 0.25f) Grama.TIERRA else if (v < 0.55f) Grama.HILO else if (v < 0.9f) Grama.MATAS else if (v < 0.92f) Grama.TANQUE else Grama.CAJAS
    }

    /**
     * Cuántas bandejas tienen ya un brote, de 0 a 20 **[Diseño propio]**: ninguna antes de que germine; desde el hilo de la
     * cuarenta y siete, una más por cada poco del arco; con las matas, casi todas.
     */
    internal fun brotes(m: Momento): Int {
        val v = investigacion(m)
        return when (grama(m)) {
            Grama.TIERRA -> 0
            Grama.HILO -> (1 + ((v - 0.25f) / 0.3f * 6f).toInt()).coerceIn(1, 7)
            Grama.MATAS -> (8 + ((v - 0.55f) / 0.35f * 12f).toInt()).coerceIn(8, 20)
            Grama.TANQUE, Grama.CAJAS -> 20
        }
    }

    // ---------------------------------------------------------------- Lo verde de arriba (arco `superficie`)

    /** Lo verde que ven las cámaras de la Vigía en las ruinas, de 0 a 1 (llega a 0.5). */
    internal fun superficie(m: Momento): Float = m.arco("superficie").coerceIn(0f, 1f)

    // ---------------------------------------------------------------- Lo que deja la gente

    /** Lauro, su asistente, está en el laboratorio: desde `lauro` (3 de febrero de la temporada 1) hasta la víspera de `lauro_se_va` (27 de septiembre de la 4). */
    internal fun lauro(m: Momento): Boolean = paso(m, 1, "02-03") && !paso(m, 4, "09-27")

    /** «En la percha del laboratorio sigue colgada su bata de repuesto, con la franja gris»: desde `lauro_se_va` hasta la víspera de `lauro_lejos` (17 de mayo de la 5). */
    internal fun bataDeLauro(m: Momento): Boolean = paso(m, 4, "09-27") && !paso(m, 5, "05-17")

    /** Los veinte cuadernos de tapas de tela de Hilda, en el estante: desde que muere (`hilda`, 20 de enero de la temporada 3). */
    internal fun cuadernosDeHilda(m: Momento): Boolean = paso(m, 3, "01-20")

    /** El día que Hilda deja el laboratorio (4 de diciembre de la temporada 1): la caja de cartón con sus cuadernos sobre la mesa. */
    internal fun cajaDeHilda(m: Momento): Boolean = hoyEs(m, "hilda_deja")

    // ---------------------------------------------------------------- Los días del cuento

    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")
    internal fun luto(m: Momento): Boolean = m.con("luto")
    internal fun alarma(m: Momento): Boolean = m.con("alarma")

    /** El día de la Fundación: «colgar banderas de papel entre los tanques… La de Botánica es verde». */
    internal fun fundacion(m: Momento): Boolean = hoyEs(m, "fundacion")

    /** El año nuevo: «se encendieron todas las lámparas de golpe»; el laboratorio cerrado, con un riego doble. */
    internal fun anioNuevo(m: Momento): Boolean = hoyEs(m, "anio_nuevo")

    /** La fiesta de la luz larga: «las lámparas de los jardines no se van a apagar hasta mañana». */
    internal fun luzLarga(m: Momento): Boolean = hoyEs(m, "luz_larga")

    /** La noche larga: «se apagan todas las lámparas», velas de la cera de las colmenas y telas en las puertas. */
    internal fun nocheLarga(m: Momento): Boolean = hoyEs(m, "noche_larga")

    /** El apagón (8 de julio de la temporada 1): media luz de día; de noche, todo apagado y las linternas de manivela. */
    internal fun apagon(m: Momento): Boolean = hoyEs(m, "apagon")

    /** El día que germina: la bandeja cuarenta y siete destapada bajo su lámpara, con la lupa encima. */
    internal fun germina(m: Momento): Boolean = hoyEs(m, "germina")

    /** El humo en las ruinas (8 de abril de la temporada 1), que se ve en la pantalla. */
    internal fun humo(m: Momento): Boolean = hoyEs(m, "humo")

    /** El día que la grama limpia un tanque entero (2 de marzo de la temporada 4). */
    internal fun funciona(m: Momento): Boolean = hoyEs(m, "funciona")

    /** El día de los de afuera: la sirena larga, el riego cerrado. */
    internal fun memoria(m: Momento): Boolean = hoyEs(m, "memoria")

    /** La revisión del destello (dos días de agosto): «un olor a ozono» y, el segundo día, «un fogonazo azul». */
    internal fun revisionDestello(m: Momento): Int = diaNumero(m, "revision_destello")

    /** Las jornadas (tres días de junio): el tercer día «se apagan las pantallas de la Vigía» para ver las fotos **[Diseño propio]**. */
    internal fun pantallaApagada(m: Momento): Boolean = (apagon(m) && m.luz.franja == Franja.TENUE) || diaNumero(m, "jornadas") == 3 && m.hacer == Hacer.TRABAJO

    // ---------------------------------------------------------------- El horario de la doctora

    /**
     * Si ella está en el laboratorio a esta hora **[Diseño propio]**: trabajando, comiendo de pie junto a la mesa («un
     * tomate de los jardines y un trozo de pan», «la bandeja con cuatro huecos») o con la música de la noche («pone música
     * en el laboratorio vacío, con el altavoz de la mesa»). No, mientras cena con el Consejo, duerme o está arriba. En la
     * campana está dentro, no en la mesa.
     */
    internal fun enLaMesa(m: Momento): Boolean = !arriba(m) && !enLaCampana(m) && when (m.hacer) {
        Hacer.TRABAJO, Hacer.COMER, Hacer.LIBRE -> true
        Hacer.DORMIR, Hacer.GENTE, Hacer.CAMINO, Hacer.CUIDAR, Hacer.REZAR, null -> false
    }

    /** Lo que hay en la mesa a esta hora, por «Un día suyo». */
    internal enum class Tarea { DESAYUNO, TRABAJO, COMIDA, MUSICA, RECOGIDO }

    /**
     * - [DESAYUNO] (de 6:30 a 7:30): las lámparas recién subidas, las tapas sobre las bandejas, el tomate y el pan.
     * - [TRABAJO] (de 7:30 a 12:30 y de 13:30 a 19:30): las tapas levantadas, la pipeta fuera, la lupa, la tableta.
     * - [COMIDA] (de 12:30 a 13:30): el trabajo a medias y la bandeja de cuatro huecos.
     * - [MUSICA] (de 21:00 a 22:30): el altavoz y la taza bajo su lámpara.
     * - [RECOGIDO]: cenando con el Consejo, durmiendo o arriba: todo en su sitio, a ras.
     */
    internal fun tarea(m: Momento): Tarea {
        if (arriba(m) || enLaCampana(m) || anioNuevo(m) && m.hora >= 6.0) return Tarea.RECOGIDO
        return when (m.hacer) {
            Hacer.COMER -> if (m.hora < 10.0) Tarea.DESAYUNO else Tarea.COMIDA
            Hacer.TRABAJO -> Tarea.TRABAJO
            Hacer.LIBRE -> Tarea.MUSICA
            Hacer.DORMIR, Hacer.GENTE, Hacer.CAMINO, Hacer.CUIDAR, Hacer.REZAR, null -> Tarea.RECOGIDO
        }
    }

    // ---------------------------------------------------------------- La luz

    /** De noche (la luz tenue de la ficha, de 21:00 a 5:00). */
    internal fun deNoche(m: Momento): Boolean = m.luz.franja == Franja.TENUE

    /** Cuántas lámparas de cultivo cuelgan sobre la mesa; la 0 es la del fondo (junto a la puerta) y la última, la cercana. */
    const val LAMPARAS = 5

    /** Cuándo se enciende y se apaga cada lámpara de cultivo, de la del fondo a la cercana (horas). */
    private val HORARIO_DEL_RIEGO = listOf(5.0f to 14.0f, 5.0f to 15.5f, 6.0f to 17.0f, 9.5f to 19.5f, 11.0f to 21.0f)

    /**
     * Cuánto luce de día la lámpara de cultivo [i], de 0 a 1 **[Diseño propio]**: caminan con el horario del riego. Por la
     * mañana, las del fondo (junto a la puerta); a mediodía, todas; por la tarde, las de este extremo. Así la luz de la
     * mesa cambia de sitio con la hora, aunque el cuento diga que la luz de Botánica es «pareja y blanca»: lo que la mueve
     * es el riego. De noche no lucen en blanco ([cultivoDeNoche]). El apagón las deja a media luz; la campana, a medias
     * («las lámparas del laboratorio apagadas a medias»); el año nuevo, todas a la vez.
     */
    internal fun cultivo(m: Momento, i: Int): Float {
        if (deNoche(m)) return 0f
        val (a, b) = HORARIO_DEL_RIEGO[i.coerceIn(0, LAMPARAS - 1)]
        val h = m.hora.toFloat()
        val k = if (anioNuevo(m)) 1f else ((h - a) / 0.34f + 1f).coerceIn(0f, 1f) * ((b - h) / 0.34f).coerceIn(0f, 1f)
        val medio = if (apagon(m)) 0.45f else if (enLaCampana(m)) 0.55f else 1f
        return (k * medio).coerceIn(0f, 1f)
    }

    /** Las lámparas de cultivo encendidas de día a esta hora (las que lucen más de la mitad). */
    internal fun encendidas(m: Momento): Set<Int> = (0 until LAMPARAS).filter { cultivo(m, it) > 0.5f }.toSet()

    /**
     * De noche, las lámparas de cultivo de la mesa pasan a su luz violeta, la misma de los jardines («la única que no imita
     * nada») **[Diseño propio]**, bajas. La noche larga y el apagón, apagadas.
     */
    internal fun cultivoDeNoche(m: Momento): Float = if (!deNoche(m) || nocheLarga(m) || apagon(m)) 0f else 0.4f

    /**
     * La lámpara de su mesa, de 0 a 1: la luz cálida de su sitio, «la última que se apaga». Encendida mientras trabaja, en
     * el desayuno de pie junto a la mesa (las lámparas de cultivo de su extremo todavía no se encienden) y con la música de la
     * noche; apagada en la comida, mientras cena con el Consejo, duerme o está arriba. El apagón la deja
     * sin corriente; la noche larga, apagada (quedan las velas).
     */
    internal fun lamparaDeMesa(m: Momento): Float {
        if (!enLaMesa(m) || nocheLarga(m) && deNoche(m)) return 0f
        if (apagon(m)) return if (deNoche(m)) 0f else 0.35f
        return when (tarea(m)) {
            Tarea.TRABAJO -> 0.7f
            Tarea.MUSICA -> 1f
            Tarea.DESAYUNO -> 0.8f
            Tarea.COMIDA, Tarea.RECOGIDO -> 0f
        }
    }

    /** La luz general de la sala (los paneles del techo), de 0 a 1: encendida de día; de noche, apagada; el apagón, a media luz. */
    internal fun general(m: Momento): Float = if (deNoche(m)) 0f else if (apagon(m)) 0.45f else 1f

    /**
     * El violeta de los jardines que llega por la puerta de vidrio, de 0 a 1: de noche; el día de la luz larga los jardines
     * se quedan en blanco ([jardinEnBlanco]); la noche larga y el apagón, a oscuras; con el pasillo en rojo, poco.
     */
    internal fun violeta(m: Momento): Float = if (!deNoche(m) || jardinEnBlanco(m) || nocheLarga(m) || apagon(m)) 0f else if (luzRoja(m)) 0.5f else 1f

    /** La noche de la luz larga, los jardines siguen en su blanco de día, y su luz entra por la puerta. */
    internal fun jardinEnBlanco(m: Momento): Boolean = luzLarga(m) && deNoche(m)

    /**
     * La luz de dentro de la campana, de 0 a 1 **[Diseño propio]**: de día, la tira de cultivo de su techo, que alumbra las
     * bandejas de prueba; de noche, la manta caliente deja un resplandor ámbar bajo; en el experimento, la lámpara de dentro,
     * a media luz («la campana se queda a media luz»).
     */
    internal fun luzDeLaCampana(m: Momento): Float = when {
        apagon(m) -> if (deNoche(m)) 0f else 0.3f
        enLaCampana(m) -> if (deNoche(m)) 0.75f else 0.6f
        deNoche(m) -> 0.45f
        else -> 0.7f
    }

    /**
     * El color de la luz de dentro de la campana: de día, la tira de cultivo de su techo, blanca como las de la mesa (es una
     * cámara de pruebas **[Diseño propio]**); de noche, el ámbar de la manta caliente; en el experimento, la lámpara de
     * dentro, cálida.
     */
    internal fun colorDeLaCampana(m: Momento): Color = if (deNoche(m) || enLaCampana(m)) Color(0xFFFFB264) else ColorLaboratorio.LUZ_DIA

    /** Las velas de la noche larga, encendidas. */
    internal fun velas(m: Momento): Boolean = nocheLarga(m) && (deNoche(m) || m.hora >= 19.5)

    /** Las linternas de manivela del apagón: cargadas de día en la mesa, una encendida de noche. */
    internal fun linterna(m: Momento): Boolean = apagon(m) && deNoche(m)

    // ---------------------------------------------------------------- El cielo de la pantalla

    /** El cielo de la pantalla de la Vigía a esta hora: el de arriba «en su noche de verdad». */
    internal enum class Cielo { ALBA, DIA, TARDE, NOCHE }

    /** La franja de luz de arriba a esta hora, con las seis franjas del día de verdad (sin tocar el motor). */
    internal fun franjaDeArriba(m: Momento): Franja = luzDe(m.hora, RegimenLuz.SEIS_FRANJAS).franja

    internal fun cielo(m: Momento): Cielo = when (franjaDeArriba(m)) {
        Franja.AMANECER -> Cielo.ALBA
        Franja.MANANA, Franja.MEDIODIA -> Cielo.DIA
        Franja.TARDE, Franja.ATARDECER -> Cielo.TARDE
        Franja.NOCHE -> Cielo.NOCHE
        Franja.ENCENDIDO -> Cielo.DIA
        Franja.TENUE -> Cielo.NOCHE
    }

    /**
     * El tiempo que hace arriba: el calendario no tiene clima (todo «sin clima», que da el cielo despejado), salvo los días
     * `sol` (despejado) y `lluvia` de la subida.
     */
    internal fun climaDeArriba(m: Momento): Clima = m.ficha.clima

    /** Las luces del cielo de arriba a esta hora, para la pantalla. **[Aproximación]**: la hora de arriba es la de abajo. */
    internal fun lucesDeArriba(m: Momento): Luces = lucesDe(luzDe(m.hora, RegimenLuz.SEIS_FRANJAS), climaDeArriba(m))

    /**
     * Si la pantalla enseña el disco del sol: solo arriba y desde el día `sol` (el segundo de la subida: «arriba nada habla
     * del sol antes de `sol`»). Los demás días la cámara mira las ruinas con el sol fuera del cuadro, y se ve su claridad.
     */
    internal fun solEnLaPantalla(m: Momento): Boolean = arriba(m) && !hoyEs(m, "sube")

    /** El día `sol` la pantalla deslumbra y tiñe media sala. */
    internal fun deslumbra(m: Momento): Boolean = hoyEs(m, "sol") && !deNoche(m)

    /** El color con que la pantalla tiñe la sala: el del cielo de arriba a su hora. De noche, un azul hondo. */
    internal fun colorDePantalla(m: Momento): Color {
        val l = lucesDeArriba(m)
        val c = lerp(lerp(l.medio, l.bajo, 0.5f), l.ambiente, 0.3f)
        val conSol = if (deslumbra(m)) lerp(c, Color(0xFFFFF2D4), 0.65f) else c
        return lerp(conSol, Color(0xFF7E9AE0), (l.noche * 0.6f).coerceIn(0f, 1f))
    }

    /** Cuánto alumbra la pantalla la sala, de 0 a 1: poco de día (manda la luz general), más de noche; deslumbra el día del sol. */
    internal fun brilloDePantalla(m: Momento): Float = when {
        pantallaApagada(m) -> 0f
        deslumbra(m) -> 1f
        deNoche(m) -> 0.55f
        else -> 0.6f
    }

    // ---------------------------------------------------------------- La luz de fondo

    /**
     * La luz de fondo de la sala: la que rebota en el hormigón blanco. De día, blanca y baja, con la sombra azul verdosa
     * (no plana: la forma la dan las lámparas); por la mañana algo más fría y baja, a mediodía la más alta, por la tarde
     * algo más cálida. De noche, el azul del Claustro («el Claustro se vuelve azul»).
     */
    internal fun luces(m: Momento): Luces {
        val g = general(m); val h = m.hora.toFloat()
        val dia = lerp(Color(0xFF41606C), Color(0xFF6A8A8E), ((h - 6f) / 6f).coerceIn(0f, 1f) * (1f - ((h - 15f) / 6f).coerceIn(0f, 1f)))
        val tarde = lerp(dia, Color(0xFF6C6672), ((h - 15f) / 5f).coerceIn(0f, 1f) * 0.6f)
        val noche = if (nocheLarga(m) || apagon(m)) Color(0xFF161B30) else Color(0xFF222B4C)
        val amb = lerp(noche, tarde, g)
        val bruma = lerp(Color(0xFF1C2238), Color(0xFF8C98A0), g)
        return Luces(
            alto = bruma, medio = bruma, bajo = bruma, ambiente = amb, bruma = bruma, brumaFuerza = 0.5f,
            solX = 0.5f, solAltura = -1f, solColor = Color.White, solFuerza = 0f, lunaX = 0.5f, lunaAltura = -1f,
            estrellas = 0f, noche = (1f - g).coerceIn(0f, 1f), nublado = 0f, eclipse = 0f,
        )
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) {
        when (cuadro(m)) {
            Cuadro.LABORATORIO -> with(CuadroLab) { cuadro(m) }
        }
    }

    override fun seMueve(m: Momento): Boolean = when (cuadro(m)) {
        Cuadro.LABORATORIO -> CuadroLab.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (cuadro(m)) {
            Cuadro.LABORATORIO -> with(CuadroLab) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = when (cuadro(m)) {
        Cuadro.LABORATORIO -> CuadroLab.luzDelGato(m, en)
    }

    override val tallaDelGato: Float get() = 1.2f

    override fun estorbos(m: Momento): List<Float> = when (cuadro(m)) {
        Cuadro.LABORATORIO -> CuadroLab.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (cuadro(m)) {
        Cuadro.LABORATORIO -> CuadroLab.anclas(m)
    }
}
