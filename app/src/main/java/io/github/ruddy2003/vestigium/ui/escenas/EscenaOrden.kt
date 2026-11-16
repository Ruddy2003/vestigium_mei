package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Clima
import kotlin.math.abs

/**
 * La paleta de Orden: quince colores de materia y cinco de luz.
 *
 * **Manda la piedra roja**: la roca de la meseta, honda y oscura en su pared a pico, y la piedra del páramo sin labrar de
 * las murallas, más clara y cálida. La acompañan el ocre del páramo y del polvo, el gris de la tela de la Garza y el
 * hierro pavonado de la aguja de la torre y de los caparazones. Los acentos se repiten: el latón del candil vuelve en la
 * argolla de la torre, en el aro de los ojos de buey de la góndola y en las placas remendadas; el carmesí (lo único que
 * la orden pinta o cuelga, **[Diseño propio]**) en el pendón de la torre y en los caparazones pintados de la Fundación; el
 * verde del núcleo, en la pantalla del archivo y en el fondo de la cisterna baja.
 *
 * Del tema de la app (una orden de acero, gótica, con un escudo de engrane y espada, que no es el cuento: se anota en el
 * informe) se quedan el latón (`#C9A04A`), el acero pavonado (`#4A4E58`), el carmesí (`#9E1B22`) y el verde fósforo
 * (`#7CE0A0`). Lo demás sale del cuento: «roca roja», «piedra del páramo sin labrar», «una ballena de tela gris», «una
 * góndola de madera», «un vaso de latón con aceite de cabra», «un polvo ocre», «el color del cobre viejo».
 */
internal object ColorOrden {
    // ---- Quince colores de materia.
    val ROCA = Color(0xFF9C4A34)                                                // la pared de la meseta
    val PIEDRA = Color(0xFFC07658)                                              // las murallas, sin labrar
    val LOSA = Color(0xFFB4705A)                                                // el suelo del adarve, gastado
    val PARAMO = Color(0xFFC89E6C)                                              // el llano, ocre
    val ARENA = Color(0xFFDCC092)                                               // el polvo, la arena que se arrima
    val TELA = Color(0xFFA7A8A2)                                                // la Garza: gris cálido
    val HIERRO = Color(0xFF4A4E58)                                              // pavonado: la aguja, los caparazones
    val LATON = Color(0xFFC9A04A)                                               // el candil, la argolla, los ojos de buey
    val MADERA = Color(0xFF8A6442)                                              // la góndola, las vigas, los andamios
    val CARMESI = Color(0xFF9E1B22)                                             // lo que la orden pinta o cuelga
    val PAPEL = Color(0xFFE6DCC6)                                               // las hojas copiadas, la tela nueva
    val CABRA = Color(0xFFD9CCB2)                                               // las cabras claras
    val HOLLIN = Color(0xFF2E2A2C)                                              // lo más oscuro: hollín, juntas, la fragua
    val VIDRIO = Color(0xFFB4CCC6)                                              // el invernadero de la torre
    val NIEVE = Color(0xFFEDEFF3)                                               // la nieve posada (nunca blanco puro)

    // ---- Las cinco luces: el candil, la fragua, el núcleo, la luna y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFB45A)
    val FRAGUA = Color(0xFFFF7A30)
    val NUCLEO = Color(0xFF7CE0A0)
    val LUNA = Color(0xFFC6D4F2)
    val SOMBRA = Color(0xFF7A70B0)

    // ---- Mezclas con nombre.
    val ROCA_HONDA = lerp(ROCA, SOMBRA, 0.35f)
    val PIEDRA_OSC = lerp(PIEDRA, ROCA, 0.5f)
    val MORTERO = lerp(PIEDRA, ARENA, 0.45f)
    val TELA_NUEVA = lerp(TELA, PAPEL, 0.45f)                                    // los paños nuevos de la costura
    val TELA_VIEJA = lerp(TELA, PARAMO, 0.22f)                                   // los paños quemados de sol
    val HIERRO_CLARO = lerp(HIERRO, TELA, 0.4f)
    val MADERA_OSC = lerp(MADERA, HOLLIN, 0.45f)
    val LIQUEN = lerp(ARENA, Color(0xFF9AA060), 0.55f)                            // el liquen de las almenas
    val CABRA_OSC = lerp(MADERA, HOLLIN, 0.6f)
    val MESETA = lerp(ROCA, PARAMO, 0.45f)                                      // las mesetas lejanas
}

/**
 * Orden: el mundo de la escriba de la Orden del Candil, en **Peñaseca, una ciudadela en lo alto de una meseta de roca
 * roja en el páramo**, de 2287 a 2291 (`AnioG8.kt`). **Manda el cuento y no el tema de la app** (el tema es una orden de
 * acero gótica; el cuento, una ciudadela rústica de piedra sin labrar con un dirigible de tela remendada): el cuadro pinta
 * el cuento. **Sin nada de la iconografía de ninguna obra:** el signo de la orden es el candil; la Garza es una ballena
 * blanda de tela gris con una góndola de madera, y los caparazones se ven abiertos, vacíos y en piezas.
 *
 * **Un solo cuadro** (`composicion_orden.md` de la tanda S18), de pie en el adarve de la muralla del oeste, sobre el escalón
 * de la banqueta (los ojos a [CamOrden.OJO] metros), mirando a lo largo del camino de ronda y un poco hacia fuera: a la
 * izquierda y abajo, el patio, con la fragua, la Cámara, la cisterna, el corral y, al fondo, el archivo, la sala del Código
 * y la puerta de la escalera; al fondo del adarve, la torre de amarre con la Garza; a la derecha, por encima del parapeto
 * y sus almenas, la otra pared de la meseta, que cae a pico, y el páramo. Los planos, cada uno en su archivo:
 * - **El páramo** ([ParamoOrden], `EscenaOrdenParamo.kt`): el cielo, las mesetas en filas con sus torres caídas, el llano con
 *   el camino, el pozo, la gasolinera y la casa hundida, y la pared de la meseta a pico, al otro lado de la ensenada.
 * - **El patio** ([PatioOrden], `EscenaOrdenPatio.kt`): la muralla del fondo con la puerta de la escalera, los edificios, la
 *   cisterna y sus canales, el corral y la fragua.
 * - **La torre** ([TorreOrden], `EscenaOrdenTorre.kt`): la aguja de hierro y piedra con su argolla y su invernadero, y la
 *   Garza en todas sus fases.
 * - **El adarve** ([AdarveOrden], `EscenaOrdenAdarve.kt`): el suelo del camino de ronda, el parapeto con sus almenas, las
 *   muescas y los candiles, y el sitio del objeto del día.
 * - **La luz** ([LuzOrden], `EscenaOrdenLuz.kt`): las sombras (la de la Garza, la de las almenas, la que sube al
 *   atardecer), la luz en el aire y la noche a dos luces.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; cada cosa en una función que lee `EscenaOrdenPrueba`):
 * - **Dónde está** ([lugar]): Peñaseca, o fuera de ella en la expedición (la Garza no está: la argolla vacía), encerrada en
 *   el archivo (su ventana, encendida toda la noche) o herida (la ventana de la enfermería).
 * - **Los arcos:** la Garza ([garza], [costura]), la cisterna ([cisterna], [tramoDeLaCisterna], [peldano]) y el códice
 *   ([nucleo], [nucleoEnLaCamara], [pila]).
 * - **Lo de cada hora:** los candiles de las almenas ([candiles]), las ventanas ([salaDelCodigo], [ventanaDeLaCamara],
 *   [ventanaDelArchivo], [enfermeria]), la fragua ([fragua]) y la puerta de la escalera ([puerta]).
 * - **Lo de cada temporada:** los postigos del polvo ([postigos]), la barra del invierno ([barra]), el invernadero roto
 *   ([invernaderoRoto]), el caparazón de Ulises en la fragua ([caparazonDeUlises]).
 * - **Los días señalados** ([hoyEs], [paso]): los caparazones pintados de la Fundación, el mercado de las caravanas, los
 *   candiles de día en año nuevo, las tapas de los canales, la alarma, el luto, la Vigilia, la caza, la Noche Larga.
 */
internal object EscenaOrden : EscenaVida {
    override val vidas = listOf("escriba")

    // ---------------------------------------------------------------- Dónde está

    /** Dónde está la escriba. [clave] es el estado del calendario; Peñaseca es lo que queda cuando no se cumple ninguno. */
    internal enum class Lugar(val clave: String) { PENASECA(""), EXPEDICION("expedicion"), ENCIERRO("encierro"), HERIDA("herida") }

    internal fun lugar(m: Momento): Lugar = Lugar.entries.firstOrNull { it.clave == m.estado } ?: Lugar.PENASECA

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    // ---------------------------------------------------------------- La Garza (arco `garza`)

    /**
     * Cómo está la Garza:
     * - [AMARRADA]: «se amarra a la torre»: el morro en la argolla, girando con el viento del día;
     * - [VOLANDO]: el día que vuelve a volar con su tela nueva (`garza_vuela`): en el aire, suelta, sobre el páramo;
     * - [AUSENTE]: durante la expedición, la argolla vacía (como el Pireo sin la nave de Demeas);
     * - [CAIDA]: con el arco hasta 0.15, «tendida en el patio, desinflada, con la tela rota y unos andamios alrededor como
     *   costillas» (el primer tramo del cuento);
     * - [COSIENDO]: con el arco por encima de 0.15 y hasta que vuela, «crece en el patio costura a costura»: el armazón con
     *   la piel nueva, tanta como diga [costura].
     */
    internal enum class Garza { AMARRADA, VOLANDO, AUSENTE, CAIDA, COSIENDO }

    /** El arco `garza` de hoy, tal cual (1 si no lo hubiera). */
    internal fun arcoGarza(m: Momento): Double = m.ficha.arcos["garza"] ?: 1.0

    internal fun garza(m: Momento): Garza = when {
        lugar(m) == Lugar.EXPEDICION -> Garza.AUSENTE
        hoyEs(m, "garza_vuela") -> Garza.VOLANDO
        arcoGarza(m) <= 0.15 -> Garza.CAIDA
        arcoGarza(m) < 1.0 -> Garza.COSIENDO
        else -> Garza.AMARRADA
    }

    /** Cuánta piel nueva tiene el armazón, de 0 (solo costillas) a 1 (entera): de 0.1 del arco (la caída) a 1 (vuela). */
    internal fun costura(m: Momento): Float = when (garza(m)) {
        Garza.AMARRADA, Garza.VOLANDO -> 1f
        Garza.AUSENTE, Garza.CAIDA -> 0f
        Garza.COSIENDO -> ((arcoGarza(m) - 0.1) / 0.9).toFloat().coerceIn(0f, 1f)
    }

    /** Desde el día de la `costura` (1 oct 2029) hay rollos de tela nueva junto a la Garza caída, aunque el arco no pase de 0.15. */
    internal fun telaNueva(m: Momento): Boolean = garza(m) == Garza.CAIDA && paso(m, 3, "10-01")

    /**
     * Hacia dónde se tiende la Garza amarrada desde la argolla, en radianes: 0 hacia la izquierda del cuadro, positivo
     * alejándose, negativo acercándose. Gira con el viento del día **[Diseño propio]**: cada día su rumbo, siempre hacia la
     * izquierda, para que no tape los astros; con viento fuerte, más de lado (tendida por el viento).
     */
    internal fun rumbo(m: Momento): Float {
        val a = m.azar("rumbo").real()
        val base = -0.55f + 1.25f * a
        return if (m.viento > 0.6f) base * 0.5f - 0.05f else base
    }

    // ---------------------------------------------------------------- La cisterna (arco `cisterna`)

    /** Los tres tramos del cuento (`ENTORNOS` en `PiezasEscriba.kt`), con los rangos cerrados y manda el primero que se cumple. */
    internal enum class Cisterna {
        /** 0 a 0.3: «tan baja que el agua se reparte con cazo»; «ya se ve el fondo, verde». */
        BAJA,
        /** 0.3 a 0.7: «a medias, y en su escalera el agua llega por el peldaño doce». */
        MEDIA,
        /** 0.7 a 1: «llena hasta el borde, y el agua suena en los canales como una fiesta». */
        LLENA,
    }

    internal fun cisterna(m: Momento): Float = (m.ficha.arcos["cisterna"] ?: 0.6).toFloat().coerceIn(0f, 1f)

    internal fun tramoDeLaCisterna(m: Momento): Cisterna {
        val a = m.ficha.arcos["cisterna"] ?: 0.6
        return if (a <= 0.3) Cisterna.BAJA else if (a <= 0.7) Cisterna.MEDIA else Cisterna.LLENA
    }

    /** Cuántos peldaños tiene la escalera de la cisterna, de la boca al fondo. */
    const val PELDANOS = 24

    /** Hasta qué peldaño (contando desde la boca) llega el agua: el doce con la cisterna a medias, como dice el cuento. */
    internal fun peldano(m: Momento): Int = (PELDANOS - Math.round(cisterna(m) * PELDANOS)).coerceIn(0, PELDANOS)

    /** Si el agua corre por los canales: las tormentas del verano, que llenan la cisterna, y los días de lluvia. */
    internal fun canalesCorren(m: Momento): Boolean = m.ficha.clima == Clima.TORMENTA || m.ficha.clima == Clima.LLUVIA

    // ---------------------------------------------------------------- El códice (arco `codice`)

    /** El núcleo de Valdecruz: no está en Peñaseca hasta que vuelve la Garza; apagado hasta el día que se enciende. */
    internal enum class Nucleo { AUSENTE, APAGADO, ENCENDIDO }

    internal fun nucleo(m: Momento): Nucleo = when {
        !paso(m, 2, "06-14") -> Nucleo.AUSENTE
        !paso(m, 2, "11-05") -> Nucleo.APAGADO
        else -> Nucleo.ENCENDIDO
    }

    /** Si el núcleo está en la mesa de la Cámara, junto a su ventana: no durante el encierro (está con ella en el archivo). */
    internal fun nucleoEnLaCamara(m: Momento): Boolean = nucleo(m) != Nucleo.AUSENTE && lugar(m) != Lugar.ENCIERRO

    /** Lo descifrado, de 0 a 1 (el arco `codice`). */
    internal fun codice(m: Momento): Float = (m.ficha.arcos["codice"] ?: 0.0).toFloat().coerceIn(0f, 1f)

    /** Los tramos de la pila de hojas copiadas junto al núcleo **[Diseño propio]** (el cuento solo dice que «crece despacio»). */
    internal enum class Pila { NINGUNA, POCAS, MEDIA, ALTA }

    internal fun pila(m: Momento): Pila {
        if (!nucleoEnLaCamara(m)) return Pila.NINGUNA
        val c = codice(m)
        return when { c < 0.02f -> Pila.NINGUNA; c <= 0.34f -> Pila.POCAS; c <= 0.67f -> Pila.MEDIA; else -> Pila.ALTA }
    }

    // ---------------------------------------------------------------- Lo de cada hora

    /** Cuántas almenas tiene el adarve a la vista, de la de delante a la torre (cada una con su candil). */
    const val ALMENAS = 18

    /**
     * Cuántos candiles de las almenas están encendidos, de 0 a [ALMENAS]: «Los de guardia encienden los candiles de las
     * almenas uno a uno» al caer la tarde (de las 19:20 a las 20:40), toda la noche y hasta el alba. El año nuevo, «con los
     * candiles encendidos aunque sea de día», y los tres días de la tormenta de polvo, «a todas horas». El día de luto,
     * apagados **[Diseño propio]** (en la sala, «un solo candil»).
     */
    internal fun candiles(m: Momento): Int {
        if (m.con("luto")) return 0
        if (hoyEs(m, "anio_nuevo") || hoyEs(m, "tormenta_polvo")) return ALMENAS
        val h = m.hora.toFloat()
        return when {
            h >= 20.65f || h < 5.65f -> ALMENAS
            h >= 19.3f -> (ALMENAS * (h - 19.3f) / 1.35f).toInt().coerceIn(1, ALMENAS)
            else -> 0
        }
    }

    /** La Vigilia de los Candiles: de noche, las lámparas viejas encendidas en las almenas, cada una distinta. */
    internal fun vigilia(m: Momento): Boolean = hoyEs(m, "vigilia") && (m.hora >= 20.0 || m.hora < 2.0)

    /**
     * Las ventanas de la sala del Código, de 0 a 1: encendidas a la hora de recitar (`REZAR`, de 20:00 a 23:00) y la Noche
     * Larga toda la velada; el día de los Caídos, a oscuras «con un solo candil» (poca luz).
     */
    internal fun salaDelCodigo(m: Momento): Float = when {
        hoyEs(m, "noche_larga") && (m.hora >= 18.0 || m.hora < 1.0) -> 1f
        m.hacer == Hacer.REZAR -> if (m.con("luto")) 0.22f else 1f
        else -> 0f
    }

    /**
     * La ventana de la Cámara: el verde del núcleo (siempre, desde que se enciende: «una luz verde que no se apaga nunca»)
     * y el candil de su trabajo, de las 13:00 a las 19:00 («taller y archivo»), si ella está en Peñaseca.
     */
    internal class VentanaDeLaCamara(val verde: Float, val candil: Float)

    internal fun ventanaDeLaCamara(m: Momento): VentanaDeLaCamara {
        val verde = if (nucleoEnLaCamara(m) && nucleo(m) == Nucleo.ENCENDIDO) 1f else 0f
        val trabaja = lugar(m) == Lugar.PENASECA && m.hacer == Hacer.TRABAJO && m.hora >= 12.5
        return VentanaDeLaCamara(verde, if (trabaja) 1f else 0f)
    }

    /**
     * La ventana alta del archivo, de 0 a 1: en el encierro, «encendida toda la noche» (desde que oscurece hasta el alba),
     * «un ojo abierto»; los demás días, el candil de la tarde de taller y archivo, poco a la vista de día.
     */
    internal fun ventanaDelArchivo(m: Momento): Float = when {
        lugar(m) == Lugar.ENCIERRO -> if (m.hora >= 17.5 || m.hora < 7.0) 1f else 0.5f
        lugar(m) == Lugar.PENASECA && m.hacer == Hacer.TRABAJO && m.hora >= 12.5 -> 0.35f
        else -> 0f
    }

    /** La ventana de la enfermería, de 0 a 1: en la herida, su candil «lejos de los catres», de noche. */
    internal fun enfermeria(m: Momento): Float = if (lugar(m) == Lugar.HERIDA && (m.hora >= 18.5 || m.hora < 6.0)) 0.7f else 0f

    /**
     * La lumbre de la fragua, de 0 a 1: encendida de las 6:00 a las 19:00 («la fragua se calla» con la campana de la tarde),
     * salvo los días de fiesta y de luto; de noche, el rescoldo.
     */
    internal fun fragua(m: Momento): Float = when {
        m.con("fiesta") || m.con("luto") -> 0.2f
        m.hora >= 6.0 && m.hora < 19.0 -> 1f
        else -> 0.2f
    }

    /** La puerta de la escalera de la meseta (al fondo, en la muralla del cuarto lado). */
    internal enum class Puerta { ABIERTA, CERRADA, ATRANCADA }

    /**
     * La barra de hierro de la puerta de la escalera: «se atranca con la barra de hierro hasta la primavera» desde que se
     * cierran las rutas (15 de noviembre) hasta el 20 de marzo **[Diseño propio]**; se quita el día que sube la caravana.
     */
    internal fun barra(m: Momento): Boolean = (mesDia(m) >= "11-15" || mesDia(m) < "03-21") && !hoyEs(m, "caravana")

    /**
     * Abierta por la mañana mientras la patrulla está fuera (6:00 a 12:00, si no está atrancada), los días de caza y de
     * caravana de día, y el de la alarma; atrancada con la barra en invierno; si no, cerrada.
     */
    internal fun puerta(m: Momento): Puerta = when {
        (hoyEs(m, "caravana") || hoyEs(m, "caravana_norte") || m.con("caza")) && m.hora >= 7.0 && m.hora < 19.0 -> Puerta.ABIERTA
        barra(m) -> Puerta.ATRANCADA
        m.con("alarma") -> Puerta.ABIERTA
        m.hora >= 6.0 && m.hora < 12.0 -> Puerta.ABIERTA
        else -> Puerta.CERRADA
    }

    // ---------------------------------------------------------------- Lo de cada temporada

    /**
     * Los postigos de hierro del polvo, cerrados: desde el 20 de mayo (`postigos`) «hasta que pasen los vientos», que
     * **[Diseño propio]** acaban el día que se abren los canales (12 de julio); y los tres días de la tormenta de polvo.
     */
    internal fun postigos(m: Momento): Boolean = mesDia(m) in "05-20".."07-11" || hoyEs(m, "tormenta_polvo")

    /** El invernadero de la torre con los cristales rotos: desde la granizada (18 jul 2028) hasta que se arreglan, el 1 de septiembre **[Diseño propio]**. */
    internal fun invernaderoRoto(m: Momento): Boolean = paso(m, 2, "07-18") && !paso(m, 2, "09-01")

    /** El caparazón de Ulises, colgado en la pared de la fragua desde el 10 de junio de 2029 (`ulises`), en piezas. */
    internal fun caparazonDeUlises(m: Momento): Boolean = paso(m, 3, "06-10")

    /**
     * Las flores amarillas de las grietas: «unas flores amarillas que duran una semana», en primavera; **[Diseño propio]**
     * la primera semana de abril.
     */
    internal fun flores(m: Momento): Boolean = mesDia(m) in "04-01".."04-07" && m.ficha.clima != Clima.NIEVE

    /** La nieve posada en el patio, el adarve y el páramo: los días que nieva («el patio amanece blanco»). */
    internal fun nieve(m: Momento): Float = if (m.ficha.clima == Clima.NIEVE) 1f else 0f

    // ---------------------------------------------------------------- Los días señalados

    /** La Fundación: los caparazones recién pintados, vacíos y abiertos, en fila sobre sus armazones en el patio. */
    internal fun caparazonesPintados(m: Momento): Boolean = hoyEs(m, "fundacion")

    /** El mercado de las caravanas: «En el patio se monta el mercado» (los toldos, los fardos, las mulas atadas). */
    internal fun mercado(m: Momento): Boolean = (hoyEs(m, "caravana") || hoyEs(m, "caravana_norte")) && m.hora >= 8.0 && m.hora < 19.0

    /** Las tapas de piedra de los canales, levantadas, y las escobas de ramas apoyadas: el día de los canales. */
    internal fun tapasLevantadas(m: Momento): Boolean = hoyEs(m, "canales")

    /** La alarma (el pozo, la emboscada): la campana, y de noche antorchas en el adarve y en el patio. */
    internal fun alarma(m: Momento): Boolean = m.con("alarma")

    /** El luto (los Caídos, la muerte de Fermín): un paño oscuro en la almena **[Diseño propio]** y los candiles apagados. */
    internal fun luto(m: Momento): Boolean = m.con("luto")

    /** La caza grande: cuerdas con la carne salada al sol en el patio, y abajo, el humo del campamento. */
    internal fun caza(m: Momento): Boolean = m.con("caza")

    /** Las fiestas: el pendón carmesí de la torre, izado **[Diseño propio]**. */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    /** La sequía: el polvo, la cisterna casi al fondo, la tierra cuarteada. */
    internal fun sequia(m: Momento): Boolean = hoyEs(m, "sequia")

    /** La granizada: el invernadero roto y el granizo blanco en el adarve. */
    internal fun granizo(m: Momento): Boolean = hoyEs(m, "granizo")

    /** El pozo seco (temporada 3): el brocal sin cubo y la tierra pálida alrededor. */
    internal fun pozoSeco(m: Momento): Boolean = paso(m, 3, "06-30") && !paso(m, 3, "08-31")

    /** La noche del Juramento: velas en la sala del Código (más luz en sus ventanas). */
    internal fun juramento(m: Momento): Boolean = hoyEs(m, "juramento")

    /** El inventario: la puerta de hierro de la Cámara abierta de par en par y cajas fuera. */
    internal fun inventario(m: Momento): Boolean = hoyEs(m, "inventario") && m.hora >= 7.0 && m.hora < 19.0

    /**
     * Lo que el cuadro de hoy cuenta, en una línea: dos momentos con la misma huella se pintan igual (salvo el cielo).
     * La usa la prueba para comprobar que cada día del cuento cambia algo.
     */
    internal fun huella(m: Momento): String = listOf(
        lugar(m), garza(m), tramoDeLaCisterna(m), nucleo(m), pila(m), candiles(m), vigilia(m), salaDelCodigo(m), puerta(m),
        postigos(m), invernaderoRoto(m), caparazonDeUlises(m), caparazonesPintados(m), mercado(m), tapasLevantadas(m),
        alarma(m), luto(m), caza(m), fiesta(m), sequia(m), granizo(m), juramento(m), inventario(m), ventanaDelArchivo(m),
        enfermeria(m), fragua(m), nieve(m), m.ficha.clima, telaNueva(m), pozoSeco(m), flores(m),
    ).joinToString("|")

    // ---------------------------------------------------------------- La luz

    private fun Luces.con(alto: Color = this.alto, medio: Color = this.medio, bajo: Color = this.bajo, ambiente: Color = this.ambiente, bruma: Color = this.bruma, solFuerza: Float = this.solFuerza, brumaFuerza: Float = this.brumaFuerza) = Luces(
        alto = alto, medio = medio, bajo = bajo, ambiente = ambiente, bruma = bruma, brumaFuerza = brumaFuerza,
        solX = solX, solAltura = solAltura, solColor = solColor, solFuerza = solFuerza, lunaX = lunaX, lunaAltura = lunaAltura,
        estrellas = estrellas, noche = noche, nublado = nublado, eclipse = 0f,
    )

    /**
     * La luz del páramo. De día, «el cielo está limpio y alto, de un azul duro, y la luz pega en las piedras como un
     * martillo»: el cenit más hondo y la bruma seca y cálida. Con polvo, «el cielo se vuelve del color del cobre viejo»;
     * con tormenta, «el cielo se pone morado». De noche, el aire seco del páramo: un azul hondo y limpio.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f)
        return when (m.ficha.clima) {
            Clima.POLVO -> {
                val k = if (hoyEs(m, "tormenta_polvo") || sequia(m)) 1f else 0.7f
                l.con(alto = lerp(l.alto, Color(0xFF8A5A3A), 0.55f * k), medio = lerp(l.medio, Color(0xFFB07A48), 0.6f * k), bajo = lerp(l.bajo, Color(0xFFC89A62), 0.6f * k),
                    ambiente = lerp(l.ambiente, Color(0xFFE8B888), 0.35f * k * dia), bruma = lerp(l.bruma, Color(0xFFC0884E), 0.7f * k),
                    solFuerza = l.solFuerza * (1f - 0.85f * k), brumaFuerza = (l.brumaFuerza + 0.1f * k).coerceAtMost(1f))
            }
            Clima.TORMENTA -> l.con(alto = lerp(l.alto, Color(0xFF4A3A62), 0.5f), medio = lerp(l.medio, Color(0xFF6A5480), 0.45f), bruma = lerp(l.bruma, Color(0xFF6A5A7A), 0.4f))
            else -> l.con(alto = lerp(l.alto, Color(0xFF4C7EAE), 0.3f * dia * (1f - l.nublado)),
                bruma = lerp(lerp(l.bruma, Color(0xFFE8D2AE), 0.25f * dia), Color(0xFF4E5490), 0.3f * l.noche),
                ambiente = lerp(l.ambiente, Color(0xFF9098CE), 0.35f * l.noche * (1f - l.nublado)))
        }
    }

    /**
     * De dónde viene la luz que tumba las sombras: de día, el sol (`Rayo`); de noche, **la luna**, que también da su
     * sombra, corta y azul, y su lado de luz fría (la noche a dos luces), como en Abismo y Botánico. Sin luna, nada.
     */
    internal fun rayo(l: Luces): Rayo {
        val r = Rayo(l)
        if (r.fuerza > 0.05f || l.noche < 0.55f) return r
        val luna = luna(l) / l.noche.coerceAtLeast(0.01f)
        if (luna <= 0.05f) return r
        val cruda = ((l.lunaX - 0.5f) * 2.6f).coerceIn(-1f, 1f); val lat = if (abs(cruda) < 0.35f) (if (cruda < 0f) -0.35f else 0.35f) else cruda
        val cot = 1.25f
        return Rayo(dia = 0f, lat = lat, cot = cot, tan = 1f / cot, ax = -lat * cot, ay = -0.33f * (1.3f - 0.6f * abs(lat)) * cot,
            fuerza = 0.4f * luna, color = Color(0xFF6E72B4), contraste = 0.3f + 0.45f * luna)
    }

    /** Cuánto alumbra la luna, de 0 a 1: de noche y sin nubes ni polvo. */
    internal fun luna(l: Luces): Float = (((l.estrellas - 0.3f) / 0.7f).coerceIn(0f, 1f) * (1f - l.nublado)) * l.noche

    /** El sol que hay, de 0 a 1: nada de noche, con el cielo cubierto ni con polvo (el polvo deja el sol en menos de un tercio). */
    internal fun sol(l: Luces): Float = (Rayo(l).fuerza / 0.72f).coerceIn(0f, 1f).let { if (l.nublado > 0.3f || it < 0.3f) 0f else it }

    /**
     * Las dos luces de la noche, de 0 a 1: la cálida (los candiles de las almenas, las ventanas, la fragua) y la fría (la
     * luna). Una noche clara tiene las dos a la vez.
     */
    internal fun dosLuces(m: Momento): Pair<Float, Float> {
        val l = luces(m)
        val calida = maxOf(candiles(m) / ALMENAS.toFloat(), salaDelCodigo(m), fragua(m) * 0.3f, ventanaDeLaCamara(m).verde * 0.4f) * l.noche
        return calida to luna(l)
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.PENASECA, Lugar.EXPEDICION, Lugar.ENCIERRO, Lugar.HERIDA -> with(CuadroOrden) { cuadro(m) }
        }
    }

    override fun seMueve(m: Momento): Boolean = true                             // el humo de la fragua, las llamas de los candiles

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        with(CuadroOrden) { vivo(m, reloj) }
    }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = CuadroOrden.luzDelGato(m, en)

    override fun estorbos(m: Momento): List<Float> = CuadroOrden.estorbos(m)

    override fun anclas(m: Momento): List<Ancla> = CuadroOrden.anclas(m)
}
