package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Arco
import io.github.ruddy2003.vestigium.vidas.Calendario
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.DiaEspecial
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Estado
import io.github.ruddy2003.vestigium.vidas.TemporadaVida
import io.github.ruddy2003.vestigium.vidas.TipoArco

/*
 * Los calendarios del grupo 5 (tanda A5): las cinco temporadas (2027 a 2031) de Tomás (Pirata), Ochiyo (Japón Edo),
 * Marie-Jeanne (Revolución francesa), Jean-Baptiste (Napoleónico) y el joven naturalista (Botánico). Siguen las reglas
 * de `AnioPiloto.kt`. Qué es real y qué inventado, con cada fuente, está en `docs/historia/fuentes.md`.
 *
 * Como los de los grupos 3 y 4, estos personajes tienen cuadro (tandas S3 y S4), y el comentario de cada calendario dice
 * qué tiene que mostrar para contar lo mismo que el cuento.
 *
 * Lo propio de este grupo:
 *
 * - **Tomás** vive entre ingleses, que en 1718 todavía usaban el calendario juliano: las fechas de las noticias son las
 *   que dan las páginas, que son las de entonces.
 * - **Ochiyo** sigue el año lunar japonés, calculado como el de Kichizō, con los términos solares medios que se usaban
 *   antes de 1844 (ver `fuentes.md`).
 * - **Marie-Jeanne y Jean-Baptiste** siguen el calendario gregoriano; su Pascua está calculada. A Marie-Jeanne las fiestas
 *   de la Iglesia se le van apagando con la Revolución: las del rey se acaban en 1792 y casi todas las demás en 1793.
 * - **Jean-Baptiste** pasa de la campaña de Rusia a su pueblo: en las temporadas 3 a 5 la mayor parte del año está en
 *   casa (estado «pueblo»).
 * - **El naturalista** sigue el viaje del bergantín que dio la vuelta al mundo entre 1831 y 1836; sus días en tierra son
 *   estados.
 */
internal object AnioG5 {
    val TODOS: List<Calendario> by lazy { listOf(TOMAS, OCHIYO, MARIE_JEANNE, JEAN_BAPTISTE, NATURALISTA) }

    /** Días «históricos» sin enlace porque su fecha sale de un cálculo hecho en la sesión: la Fête-Dieu gregoriana. */
    val CALCULADOS = setOf("fete_dieu")

    private val W = "https://en.wikipedia.org/wiki/"

    private val F_ANIO_NUEVO = "El año nuevo" to "${W}New_Year%27s_Day"
    private val F_NAVIDAD = "La Navidad" to "${W}Christmas"
    private val F_ASUNCION = "La Asunción" to "${W}Assumption_of_Mary"
    private val F_TODOS_SANTOS = "Todos los Santos" to "${W}All_Saints%27_Day"

    // =====================================================================
    //  Tomás, grumete de un balandro pirata. Las Bahamas y el Caribe, de 1718 a 1722.
    // =====================================================================
    //
    // Su gente (toda inventada): el capitán Ned Grady, irlandés, elegido por votación y depuesto por votación en la 2; el
    // capitán nuevo, Hollis. Kofi, un marinero africano libre que le enseña el oficio y muere de fiebre en la 3. Abel, el
    // contramaestre, que lo castiga en la 4. El cocinero, el piloto y unos setenta más. Su madre, en las Canarias, que
    // no sabe dónde está.
    //
    // Lo real: Nassau y su república de piratas; el perdón del rey y la llegada del gobernador Woodes Rogers (22 de julio
    // de 1718), de la que Charles Vane escapa; Barbanegra y su cerco de Charles Town, y su muerte (22 de noviembre de
    // 1718); Stede Bonnet ahorcado (10 de diciembre de 1718); la guerra con España (1718–1720); Bartholomew Roberts, su
    // carrera y su muerte (10 de febrero de 1722); Rackham, Anne Bonny y Mary Read (agosto a noviembre de 1720); Vane
    // ahorcado (marzo de 1721); Edward Low (1722). Que las noticias lleguen esos días es inventado.
    //
    // Claves de escena: fiesta, carena, tormenta, abordaje, luto, calma. Arco: casco (lo sucio que va el casco, que se
    // limpia en cada carena: 0 = limpio). Estados: «cayo» (cada año, la carena: el balandro tumbado en la arena de un cayo)
    // y «nassau» (temporada 1: el balandro en el puerto de Nassau hasta que llega el gobernador).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el balandro en el mar, con algas en el casco
    // según `casco`; tumbado en la playa de un cayo, con la tripulación en tierra, durante «cayo»; y anclado entre otros
    // barcos frente a las casas de Nassau durante «nassau».

    private val F_SANTO_TOMAS = "Santo Tomás" to "${W}Thomas_the_Apostle"
    private val F_CARENA = "La carena" to "${W}Careening"
    private val F_ALISIOS = "Los alisios" to "${W}Trade_winds"
    private val F_BARBANEGRA = "Barbanegra" to "${W}Blackbeard"
    private val F_VANE = "Charles Vane" to "${W}Charles_Vane"
    private val F_BONNET = "Stede Bonnet" to "${W}Stede_Bonnet"
    private val F_GUERRA_ESPANA = "La guerra de la Cuádruple Alianza" to "${W}War_of_the_Quadruple_Alliance"
    private val F_ROBERTS = "Bartholomew Roberts" to "${W}Bartholomew_Roberts"
    private val F_RACKHAM = "Jack Rackham" to "${W}Calico_Jack"
    private val F_MARY_READ = "Mary Read" to "${W}Mary_Read"
    private val F_LOW = "Edward Low" to "${W}Edward_Low"

    /** Lo que vuelve cada año en el mar. */
    private fun anioDelMar(): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo: ron doble para toda la tripulación", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
        rito("carena", "03-10", "Se carena el balandro en un cayo: el barco tumbado en la arena", certeza = APROXIMADO, fuente = F_CARENA, escena = "carena"),
        rito("botadura", "03-24", "El balandro vuelve al agua con el casco limpio", certeza = APROXIMADO),
        cosecha("tortugas", "05-20", "Tortugas en la playa: se cazan y se guardan vivas para comer", 5, APROXIMADO),
        mundo("huracanes", "08-01", "Empieza el tiempo de los huracanes", certeza = APROXIMADO),
        mundo("alisios", "11-15", "Vuelven los alisios del nordeste, constantes y frescos", certeza = APROXIMADO, fuente = F_ALISIOS),
        rito("san_tomas", "12-21", "Santo Tomás, su santo: nadie más a bordo lo sabe", certeza = HISTORICO, fuente = F_SANTO_TOMAS),
        fiesta("navidad", "12-25", "Navidad a bordo: se bebe hasta que nadie se tiene en pie", certeza = HISTORICO, fuente = F_NAVIDAD),
    )

    val TOMAS = Calendario(
        vida = "grumete",
        estaciones = listOf("05-25" to Estacion.LLUVIAS, "11-20" to Estacion.SECAS),
        clima = listOf(
            mes(despejado = 21, nubes = 6, lluvia = 2, viento = 2), mes(despejado = 21, nubes = 5, lluvia = 1, viento = 1), mes(despejado = 22, nubes = 5, lluvia = 2, viento = 2),
            mes(despejado = 21, nubes = 6, lluvia = 3), mes(despejado = 17, nubes = 7, lluvia = 5, tormenta = 2), mes(despejado = 13, nubes = 8, lluvia = 6, tormenta = 3),
            mes(despejado = 14, nubes = 8, lluvia = 6, tormenta = 3), mes(despejado = 12, nubes = 8, lluvia = 6, tormenta = 5), mes(despejado = 11, nubes = 8, lluvia = 6, tormenta = 5),
            mes(despejado = 13, nubes = 8, lluvia = 6, tormenta = 4), mes(despejado = 17, nubes = 7, lluvia = 4, viento = 2), mes(despejado = 20, nubes = 6, lluvia = 3, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1718", "indulto",
                "El balandro toma una presa de ron. Kofi le enseña a Tomás a subir a la cofa. Barbanegra cierra el puerto de Charles Town. En Nassau llega el gobernador del rey con el perdón, la tripulación vota no aceptarlo y escapa de noche. Kofi le enseña a pescar. Tomás sube por primera vez al abordaje. Matan a Barbanegra.",
                anioDelMar() + listOf(
                    guerra("presa_ron", "01-18", "Toman una balandra de Jamaica cargada de ron y azúcar", certeza = INVENTADO, escena = "abordaje"),
                    suceso("cofa", "04-20", "Kofi le enseña a subir a la cofa más alta sin agarrarse"),
                    mundo("charleston", "06-08", "Llega la noticia: Barbanegra tiene cerrado el puerto de Charles Town", certeza = APROXIMADO, fuente = F_BARBANEGRA),
                    suceso("llega_nassau", "06-28", "El balandro entra en Nassau a vender lo robado y a beber", cadena = "nassau"),
                    mundo("rogers", "07-22", "Llega a Nassau el gobernador del rey, con el perdón para los piratas", certeza = HISTORICO, fuente = F_VANE),
                    suceso("votacion", "07-24", "La tripulación vota no aceptar el perdón, y salen de Nassau de noche", cadena = "nassau"),
                    suceso("anzuelo", "09-08", "Kofi le enseña a pescar desde la borda con un anzuelo hecho de un clavo"),
                    suceso("primer_abordaje", "10-10", "Sube por primera vez al abordaje de una presa, con un cuchillo en la boca", escena = "abordaje"),
                    mundo("barbanegra", "12-12", "Llega la noticia: mataron a Barbanegra en una ensenada del norte", certeza = APROXIMADO, fuente = F_BARBANEGRA),
                )),
            TemporadaVida("1719", "guerra_espana",
                "Ahorcan a Bonnet y estalla la guerra con España. Diez días de calma dejan al balandro sin agua. Atacan un barco español demasiado grande y a Tomás se le clava una astilla. En el reparto le toca media parte. Un huracán los esconde tres días en una ensenada. Se oye hablar de un tal Roberts. La tripulación echa al capitán y elige a otro.",
                anioDelMar() + listOf(
                    mundo("bonnet", "01-25", "Llega la noticia: ahorcaron en Charles Town al caballero pirata", certeza = APROXIMADO, fuente = F_BONNET, escena = "luto"),
                    guerra("guerra_espana", "02-15", "Llega la noticia: hay guerra con España, y lo español es presa", certeza = APROXIMADO, fuente = F_GUERRA_ESPANA),
                    mundo("calma", "04-28", "La calma: días sin viento y el agua se acaba", 4, INVENTADO, escena = "calma", clima = Clima.DESPEJADO),
                    guerra("galeon", "06-18", "Atacan un barco español grande y tienen que huir a cañonazos", certeza = INVENTADO, escena = "abordaje"),
                    suceso("astilla", "06-19", "Se le clava en el brazo una astilla de la borda rota"),
                    suceso("reparto", "08-20", "Reparto del botín: a Tomás le toca media parte"),
                    mundo("huracan", "09-18", "Un huracán los obliga a esconderse tres días en una ensenada", 3, INVENTADO, escena = "tormenta", clima = Clima.TORMENTA),
                    mundo("roberts", "10-20", "Se dice que un galés llamado Roberts es capitán y toma barcos en el Brasil", certeza = APROXIMADO, fuente = F_ROBERTS),
                    suceso("capitan_nuevo", "11-25", "La tripulación echa al capitán por votación y elige a otro"),
                )),
            TemporadaVida("1720", "rackham",
                "El piloto le enseña a Tomás a leer la brújula. Encuentran en un cayo a un hombre abandonado. Kofi muere de fiebre amarilla. Se oye de un tal Rackham, que robó un balandro en Nassau con dos mujeres a bordo, y luego de su captura y de su horca. Una tormenta les rompe la gavia.",
                anioDelMar() + listOf(
                    suceso("brujula", "01-20", "El piloto le enseña a leer la brújula y las estrellas"),
                    suceso("naufrago", "04-10", "Encuentran en un cayo a un hombre que lleva meses abandonado"),
                    suceso("kofi_fiebre", "07-02", "Kofi cae con la fiebre amarilla", cadena = "kofi"),
                    suceso("muere_kofi", "07-09", "Muere Kofi, y lo echan al mar envuelto en su coy", cadena = "kofi", escena = "luto"),
                    mundo("william", "09-05", "Se dice que un tal Rackham robó un balandro en Nassau, con dos mujeres a bordo", certeza = APROXIMADO, fuente = F_RACKHAM),
                    mundo("tormenta_gavia", "10-05", "Una tormenta les rompe el mastelero de la gavia", certeza = INVENTADO, escena = "tormenta", clima = Clima.TORMENTA),
                    mundo("rackham_preso", "11-08", "Llega la noticia: agarraron a Rackham borracho en la costa de Jamaica", certeza = APROXIMADO, fuente = F_RACKHAM),
                    mundo("rackham_ahorcado", "12-08", "Llega la noticia: ahorcaron a Rackham en Port Royal", certeza = APROXIMADO, fuente = F_RACKHAM, escena = "luto"),
                )),
            TemporadaVida("1721", "roberts",
                "La tripulación vota darle a Tomás una parte entera. Pasan ballenas. Ahorcan a Vane. Se dice que Roberts tiene parado el comercio de las islas y que una de las mujeres de Rackham murió en la cárcel. Tomás se pelea con el contramaestre. Los persigue una fragata del rey. Le dicta a un preso una carta para su madre.",
                anioDelMar() + listOf(
                    suceso("parte_entera", "02-10", "La tripulación vota darle una parte entera, como a un hombre"),
                    mundo("ballenas", "02-25", "Pasan ballenas junto al balandro, soplando, toda una tarde", certeza = INVENTADO),
                    mundo("vane", "04-20", "Llega la noticia: ahorcaron a Vane en Port Royal", certeza = APROXIMADO, fuente = F_VANE, escena = "luto"),
                    mundo("comercio", "05-08", "Se dice que Roberts tiene parado el comercio de las islas", certeza = APROXIMADO, fuente = F_ROBERTS),
                    mundo("mary_read", "06-20", "Se dice que una de las mujeres de Rackham murió en la cárcel", certeza = APROXIMADO, fuente = F_MARY_READ),
                    suceso("pelea", "07-15", "Se pelea con el contramaestre y lo atan al palo un día entero"),
                    guerra("fragata", "09-10", "Los persigue una fragata del rey dos días enteros", 2, INVENTADO, escena = "tormenta"),
                    suceso("carta", "10-25", "Le dicta a un preso que sabe escribir una carta para su madre"),
                )),
            TemporadaVida("1722", "fin",
                "Se dice que habrá otro perdón. Le dejan a Tomás el timón una guardia entera. Matan a Roberts en África. Los tiburones siguen al balandro tres días. En un puerto holandés Tomás se compra una camisa y piensa en quedarse. Se oye de un tal Low, que tortura a los pescadores. Toman una goleta de bacalao. Tomás decide dejar el balandro.",
                anioDelMar() + listOf(
                    mundo("perdon_rumor", "01-25", "Se dice que el rey dará otro perdón a los que se entreguen", certeza = INVENTADO),
                    suceso("timon", "03-01", "Le dejan llevar el timón una guardia entera"),
                    mundo("roberts_muerto", "04-05", "Llega la noticia: mataron a Roberts en la costa de África", certeza = APROXIMADO, fuente = F_ROBERTS, escena = "luto"),
                    mundo("tiburones", "05-05", "Los tiburones siguen al balandro tres días", certeza = INVENTADO),
                    suceso("camisa", "06-10", "En un puerto holandés se compra una camisa nueva y piensa en quedarse"),
                    mundo("low", "07-25", "Se dice que un tal Low tortura a los pescadores del norte", certeza = APROXIMADO, fuente = F_LOW),
                    guerra("goleta", "09-05", "Toman una goleta de Boston cargada de bacalao", certeza = INVENTADO, escena = "abordaje"),
                    suceso("decision", "10-20", "Decide que, en el próximo puerto, dejará el balandro"),
                )),
        ),
        arcos = listOf(Arco.cadaAnio("casco", TipoArco.OTRO, "03-09" to 0.95, "03-24" to 0.0, "12-31" to 0.8)),
        objetos = listOf("cuchillo", "coy", "galleta", "ron", "cabo", "campana", "catalejo", "mosquete", "polvora", "bandera", "dados", "brujula"),
        estados = listOf(Estado("cayo", "carena", "botadura"), Estado("nassau", "llega_nassau", "votacion")),
    )

    // =====================================================================
    //  Ochiyo, sirvienta de una casa de té. Una posta del Tōkaidō desde la que se ve el Fuji, de 1830 a 1834.
    // =====================================================================
    //
    // Su gente (toda inventada): la señora Tsuru, dueña de la casa de té. Osen, la sirvienta nueva, que llega en la
    // temporada 1 con doce años. Gonta, un porteador del camino, bruto y bueno. Sakichi, un correo que corre entre Edo y
    // Kioto, a quien conoce en la 1 y con quien se promete en la 4. Su madre y su hermano pequeño, en una aldea de los
    // montes, a medio día.
    //
    // Lo real: el año lunar y sus fiestas (el año nuevo, el setsubun, el día de las niñas, el de los niños, Tanabata, Bon,
    // la luna de otoño); las procesiones de los daimios que van y vuelven de Edo; la peregrinación a Ise de 1830, con
    // millones de peregrinos; las vistas del Fuji de Hokusai (1830–1832); el viaje de Hiroshige por el Tōkaidō en 1832 y
    // sus estampas (1833–1834); y el hambre de Tenpō, desde 1833. Que se sepa en la posta esos días es inventado.
    //
    // Claves de escena: fiesta, linternas, peregrinos, procesión, lluvia, nieve. Arco: arroz (el precio del arroz: sube
    // con el hambre). Estado: «okage» (temporada 1: los meses en que el camino se llena de peregrinos).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la casa de té junto al camino, con el Fuji al
    // fondo; el camino lleno de peregrinos mientras el estado es «okage»; y la procesión del daimio los
    // días que pasa.

    private val F_SHOGATSU = "El año nuevo japonés" to "${W}Japanese_New_Year"
    private val F_SETSUBUN = "Setsubun" to "${W}Setsubun"
    private val F_HINA = "El día de las niñas" to "${W}Hinamatsuri"
    private val F_TANGO = "El día de los niños" to "${W}Kodomo_no_Hi"
    private val F_TANABATA = "Tanabata" to "${W}Tanabata"
    private val F_BON = "Bon" to "${W}Bon_Festival"
    private val F_TSUKIMI = "La luna de otoño" to "${W}Tsukimi"
    private val F_SANKIN = "Las procesiones de los daimios" to "${W}Sankin-k%C5%8Dtai"
    private val F_ISE = "El santuario de Ise" to "${W}Ise_Grand_Shrine"
    private val F_HOKUSAI = "Las vistas del Fuji de Hokusai" to "${W}Thirty-six_Views_of_Mount_Fuji"
    private val F_HIROSHIGE = "Las estampas del Tōkaidō de Hiroshige" to "${W}The_Fifty-three_Stations_of_the_T%C5%8Dkaid%C5%8D"
    private val F_TENPO = "El hambre de Tenpō" to "${W}Tenp%C5%8D_famine"

    // El año lunar de 1830 a 1834 (términos solares medios; ver `fuentes.md`).
    private val SHOGATSU = listOf("01-25", "02-13", "02-02", "02-20", "02-09")
    private val YABUIRI = listOf("02-09", "02-28", "02-17", "03-07", "02-24")      // 1/16, su día libre de invierno
    private val SUSUHARAI = listOf("01-07", "01-26", "01-15", "02-02", "01-22")    // 12/13 del año anterior
    private val HINA = listOf("04-25", "04-15", "04-03", "04-22", "04-11")
    private val TANGO = listOf("06-25", "06-14", "06-03", "06-22", "06-11")
    private val TANABATA = listOf("08-24", "08-14", "08-02", "08-21", "08-11")
    private val BON = listOf("08-30", "08-20", "08-08", "08-27", "08-17")
    private val YABUIRI7 = listOf("09-02", "08-23", "08-11", "08-30", "08-20")     // 7/16, su día libre de verano
    private val TSUKIMI = listOf("10-01", "09-20", "09-09", "09-28", "09-17")

    /** Lo que vuelve cada año en la posta, en la temporada [t]. En 1832 el setsubun cae en el año nuevo y no va aparte. */
    private fun anioDeLaPosta(t: Int): List<DiaEspecial> = listOfNotNull(
        rito("susuharai", SUSUHARAI[t - 1], "La gran limpieza del fin de año: se saca el hollín de las vigas", certeza = APROXIMADO),
        fiesta("shogatsu", SHOGATSU[t - 1], "El año nuevo: pinos en la puerta y caminantes que no paran", 3, APROXIMADO, F_SHOGATSU),
        if (t == 3) null else rito("setsubun", "02-03", "Setsubun: se tiran habas para echar a los demonios", certeza = APROXIMADO, fuente = F_SETSUBUN),
        fiesta("yabuiri", YABUIRI[t - 1], "Su día libre de invierno: va a ver a su madre a la aldea", certeza = APROXIMADO),
        fiesta("hina", HINA[t - 1], "El día de las niñas: las muñecas en la casa de la dueña", certeza = APROXIMADO, fuente = F_HINA),
        mundo("daimio_ida", "05-10", "Pasa la procesión de un daimio camino de Edo", certeza = APROXIMADO, fuente = F_SANKIN, escena = "procesion"),
        fiesta("tango", TANGO[t - 1], "El día de los niños: carpas de tela en las varas y lirios en el baño", certeza = APROXIMADO, fuente = F_TANGO),
        fiesta("tanabata", TANABATA[t - 1], "Tanabata: deseos de papel colgados del bambú", certeza = APROXIMADO, fuente = F_TANABATA, escena = "fiesta"),
        fiesta("bon", BON[t - 1], "Bon: vuelven los muertos y se baila en la plaza de la posta", 3, APROXIMADO, F_BON, escena = "linternas"),
        fiesta("yabuiri7", YABUIRI7[t - 1], "Su día libre de verano: otra vez a la aldea", certeza = APROXIMADO),
        fiesta("tsukimi", TSUKIMI[t - 1], "La luna de otoño: dulces de arroz y hierbas en la ventana", certeza = APROXIMADO, fuente = F_TSUKIMI),
        mundo("daimio_vuelta", "10-28", "Pasa la procesión de un daimio que vuelve de Edo a su tierra", certeza = APROXIMADO, fuente = F_SANKIN, escena = "procesion"),
        mundo("fin_anio", "12-12", "Empiezan a pasar los cobradores del fin de año", certeza = APROXIMADO),
    )

    val OCHIYO = Calendario(
        vida = "posadera",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 20, nubes = 6, lluvia = 2, nieve = 2, viento = 1), mes(despejado = 17, nubes = 7, lluvia = 3, nieve = 1),
            mes(despejado = 14, nubes = 8, lluvia = 8, viento = 1), mes(despejado = 13, nubes = 8, lluvia = 9),
            mes(despejado = 13, nubes = 9, lluvia = 9), mes(despejado = 8, nubes = 9, lluvia = 13),
            mes(despejado = 11, nubes = 9, lluvia = 9, tormenta = 2), mes(despejado = 15, nubes = 8, lluvia = 6, tormenta = 2),
            mes(despejado = 10, nubes = 8, lluvia = 9, tormenta = 3), mes(despejado = 13, nubes = 8, lluvia = 9, niebla = 1),
            mes(despejado = 16, nubes = 7, lluvia = 6, niebla = 1), mes(despejado = 21, nubes = 6, lluvia = 3, viento = 1),
        ),
        temporadas = listOf(
            TemporadaVida("1830, año 13 de Bunsei", "okage",
                "Llega a la casa de té una sirvienta nueva, Osen. De la primavera al verano pasan por la posta miles de peregrinos camino de Ise. Un samurái le deja a Ochiyo una moneda de plata. En otoño conoce a Sakichi, un correo de Edo.",
                anioDeLaPosta(1) + listOf(
                    suceso("osen", "03-05", "Llega a la casa de té una sirvienta nueva, Osen, de doce años"),
                    mundo("okage", "05-01", "Empiezan a pasar peregrinos hacia Ise: miles cada día, de todas partes", certeza = APROXIMADO, fuente = F_ISE, escena = "peregrinos"),
                    suceso("plata", "06-08", "Un samurái le deja una moneda de plata por un té"),
                    mundo("okage_fin", "08-12", "Ya casi no pasan peregrinos: el camino descansa", certeza = INVENTADO),
                    suceso("sakichi", "10-12", "Conoce a Sakichi, un correo que corre entre Edo y Kioto"),
                )),
            TemporadaVida("1831, año 14 de Bunsei", "hokusai",
                "Ochiyo se quema la mano con la tetera grande. Un viajero deja en la casa de té una estampa del Fuji con una ola enorme. Su hermano pequeño enferma. Las lluvias cierran el río tres días. Sakichi le regala un peine.",
                anioDeLaPosta(2) + listOf(
                    suceso("quemadura", "01-12", "Se quema la mano con la tetera grande"),
                    mundo("ola", "04-28", "Un viajero deja en la casa de té una estampa del Fuji con una ola enorme", certeza = APROXIMADO, fuente = F_HOKUSAI),
                    suceso("hermano", "07-10", "Le llega recado de la aldea: su hermano pequeño está enfermo"),
                    mundo("rio_cerrado", "09-08", "El río crece con las lluvias y nadie lo puede cruzar en tres días", 3, INVENTADO, escena = "lluvia", clima = Clima.LLUVIA),
                    suceso("peine", "11-10", "Sakichi le regala un peine de boj"),
                )),
            TemporadaVida("1832, año 3 de Tenpō", "hiroshige",
                "Ochiyo aprende a escribir su nombre. Osen se escapa una noche y vuelve. Gonta se pelea con el criado de un samurái. Pasa la comitiva del shogun que lleva caballos al emperador, y con ella un pintor de Edo que dibuja el camino. Nieva sobre el Fuji antes de tiempo.",
                anioDeLaPosta(3) + listOf(
                    suceso("nombre", "03-12", "Sakichi le enseña a escribir su nombre"),
                    suceso("osen_escapa", "05-25", "Osen se escapa una noche con un mozo de caballos y vuelve al amanecer"),
                    suceso("gonta", "07-15", "Gonta se pelea con el criado de un samurái y lo encierran un día"),
                    mundo("comitiva", "09-16", "Pasa la comitiva del shogun que lleva caballos al emperador, con un pintor de Edo", certeza = APROXIMADO, fuente = F_HIROSHIGE, escena = "procesion"),
                    mundo("nieve_fuji", "10-10", "Nieva sobre el Fuji antes de tiempo", certeza = INVENTADO, escena = "nieve"),
                )),
            TemporadaVida("1833, año 4 de Tenpō", "hambre",
                "Ochiyo le da a Sakichi un amuleto para el camino, y él le pide que se case con él cuando termine su contrato. Llueve y hace frío todo el verano y el arroz no madura. El arroz sube y por el camino pasan pobres del norte. A la posta llegan las estampas de las cincuenta y tres postas.",
                anioDeLaPosta(4) + listOf(
                    suceso("amuleto", "04-05", "Le da a Sakichi un amuleto del santuario para el camino"),
                    suceso("pedida", "05-22", "Sakichi le pide que se case con él cuando termine su contrato"),
                    mundo("verano_frio", "07-28", "Llueve y hace frío todo el verano: el arroz no madura", certeza = APROXIMADO, fuente = F_TENPO, clima = Clima.LLUVIA),
                    suceso("osen_boda", "09-08", "Osen se casa con el hijo del herrero de la posta"),
                    mundo("pobres", "11-20", "El arroz cuesta el doble, y por el camino pasan pobres del norte", certeza = APROXIMADO, fuente = F_TENPO),
                    mundo("estampas", "12-05", "Llegan a la posta estampas de las cincuenta y tres postas del camino", certeza = APROXIMADO, fuente = F_HIROSHIGE),
                )),
            TemporadaVida("1834, año 5 de Tenpō", "estampas",
                "Ochiyo busca su posta en las estampas del camino y la encuentra. El hambre sigue. La señora Tsuru le da permiso para casarse en otoño. Su madre baja de la aldea a conocer a Sakichi. Se casan.",
                anioDeLaPosta(5) + listOf(
                    suceso("su_posta", "03-02", "Busca su posta en las estampas del camino, y la encuentra"),
                    mundo("hambre_sigue", "05-25", "El hambre sigue: en la posta se sirve menos arroz y más mijo", certeza = APROXIMADO, fuente = F_TENPO),
                    suceso("permiso", "06-28", "La señora Tsuru le da permiso para casarse en otoño"),
                    suceso("madre_viene", "09-28", "Su madre baja de la aldea a conocer a Sakichi"),
                    suceso("boda", "11-08", "Se casa con Sakichi en la casa de té, y se queda a servir en ella", escena = "fiesta"),
                )),
        ),
        arcos = listOf(Arco("arroz", TipoArco.OTRO, listOf(p(1, "01-01", 0.3), p(4, "07-01", 0.35), p(4, "12-31", 0.8), p(5, "12-31", 0.9)))),
        objetos = listOf("tetera", "taza", "farol", "sombrero", "sandalias", "abanico", "dango", "ryo", "palanquin", "pipa", "peine", "furoshiki"),
        estados = listOf(Estado("okage", "okage", "okage_fin")),
    )

    // =====================================================================
    //  Marie-Jeanne, lavandera. Un barco-lavadero en el Sena, en París, de 1789 a 1793.
    // =====================================================================
    //
    // Su gente (toda inventada): Jacques, su compañero, aguador, que entra en la guardia nacional en la 1 y se va de
    // voluntario a la frontera en la 4; vuelve cojo en la 5. Paul, su hijo (7 años en la 1). La vieja Thérèse, que lava a
    // su lado. El maestro Gaspard, dueño del barco. Antoine, el impresor del barrio, que lee en voz alta los periódicos.
    //
    // Lo real: el invierno de 1788–1789, en que se heló el Sena; los motines de Réveillon (abril de 1789); los Estados
    // Generales; la Bastilla; la Declaración de los derechos; la marcha a Versalles (5 y 6 de octubre de 1789); la
    // Constitución civil del clero; la fiesta de la Federación (14 de julio de 1790); Varennes y el Campo de Marte (1791);
    // la guerra (20 de abril de 1792), el 10 de agosto, las matanzas de septiembre, Valmy y la República; la muerte del rey
    // (21 de enero de 1793), la de Marat, la leva en masa, la de la reina, el calendario nuevo y la fiesta de la Razón. Y
    // el año de la Iglesia, que se va apagando.
    //
    // Claves de escena: fiesta, hielo, guerra, luto, colada, fuego. Arco: pan (lo que cuesta el pan: sube en el verano
    // de 1789, baja y vuelve a subir en 1792 y 1793). Estados: «helada» (temporada 1: el Sena helado, sin trabajo) y
    // «marcha» (temporada 1: los dos días de Versalles).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el barco-lavadero en el río, con la ropa
    // tendida, y la cola del pan según `pan`; el río helado y el barco vacío durante «helada»; y el camino de Versalles
    // lleno de mujeres durante «marcha».

    private val F_GALETTE = "La torta de los reyes" to "${W}Galette_des_rois"
    private val F_CORPUS = "La Fête-Dieu" to "${W}Feast_of_Corpus_Christi"
    private val F_REVEILLON = "Los motines de Réveillon" to "${W}R%C3%A9veillon_riots"
    private val F_ESTADOS = "Los Estados Generales" to "${W}Estates_General_of_1789"
    private val F_BASTILLA = "La toma de la Bastilla" to "${W}Storming_of_the_Bastille"
    private val F_DECLARACION = "La Declaración de los derechos" to "${W}Declaration_of_the_Rights_of_Man_and_of_the_Citizen"
    private val F_VERSALLES = "La marcha a Versalles" to "${W}Women%27s_March_on_Versailles"
    private val F_CLERO = "La Constitución civil del clero" to "${W}Civil_Constitution_of_the_Clergy"
    private val F_FEDERACION = "La fiesta de la Federación" to "${W}F%C3%AAte_de_la_F%C3%A9d%C3%A9ration"
    private val F_VARENNES = "La huida a Varennes" to "${W}Flight_to_Varennes"
    private val F_CAMPO_MARTE = "La matanza del Campo de Marte" to "${W}Champ_de_Mars_massacre"
    private val F_MIRABEAU = "Mirabeau" to "${W}Honor%C3%A9_Gabriel_Riqueti,_comte_de_Mirabeau"
    private val F_CONSTITUCION = "La Constitución de 1791" to "${W}French_Constitution_of_1791"
    private val F_GIRONDINOS = "La caída de los girondinos" to "${W}Insurrection_of_31_May_%E2%80%93_2_June_1793"
    private val F_GUERRA_1792 = "La guerra de la primera coalición" to "${W}War_of_the_First_Coalition"
    private val F_10_AGOSTO = "El 10 de agosto" to "${W}10_August_(French_Revolution)"
    private val F_SEPTIEMBRE = "Las matanzas de septiembre" to "${W}September_massacres"
    private val F_VALMY = "Valmy" to "${W}Battle_of_Valmy"
    private val F_CALENDARIO = "El calendario republicano" to "${W}French_Republican_calendar"
    private val F_LUIS_XVI = "La muerte de Luis XVI" to "${W}Execution_of_Louis_XVI"
    private val F_MARAT = "La muerte de Marat" to "${W}Death_of_Marat"
    private val F_LEVA = "La leva en masa" to "${W}Lev%C3%A9e_en_masse"
    private val F_REINA = "La muerte de la reina" to "${W}Execution_of_Marie_Antoinette"
    private val F_RAZON = "La fiesta de la Razón" to "${W}Cult_of_Reason"

    // Lo que cuelga de la Pascua gregoriana de 1789 a 1793.
    private val CARNAVAL_PARIS = listOf("02-24", "02-16", "03-08", "02-21", "02-12")
    private val PASCUA_PARIS = listOf("04-12", "04-04", "04-24", "04-08", "03-31")
    private val FETE_DIEU = listOf("06-11", "06-03", "06-23", null, null)             // en 1792 y 1793 ya no sale la procesión

    /**
     * Lo que vuelve cada año en París, en la temporada [t]: las fiestas del rey y de la Iglesia se van apagando. En 1791
     * la víspera de San Juan cae el mismo día que la Fête-Dieu, y se queda la procesión.
     */
    private fun anioDeParis(t: Int): List<DiaEspecial> = listOfNotNull(
        fiesta("reyes", "01-06", "La Epifanía: la torta de los reyes, con su haba", certeza = APROXIMADO, fuente = F_GALETTE),
        fiesta("carnaval", CARNAVAL_PARIS[t - 1], "Martes de carnaval: máscaras en la calle y crepes en la escalera", certeza = HISTORICO),
        cosecha("colada", "03-20", "La gran colada de primavera: París lava las sábanas del invierno", 5, APROXIMADO, escena = "colada"),
        fiesta("pascua", PASCUA_PARIS[t - 1], "Pascua: las campanas de Notre-Dame", certeza = HISTORICO, escena = "campanas"),
        FETE_DIEU[t - 1]?.let { fiesta("fete_dieu", it, "La Fête-Dieu: la procesión pasa por la calle con sábanas en las ventanas", certeza = HISTORICO, fuente = F_CORPUS) },
        if (t <= 4 && t != 3) fiesta("san_juan", "06-23", "La noche de San Juan: la hoguera de la plaza de Grève", certeza = APROXIMADO, escena = "fuego") else null,
        if (t >= 3) fiesta("aniversario", "07-14", "El aniversario de la Bastilla: baile en las calles", certeza = HISTORICO, fuente = F_BASTILLA) else null,
        if (t <= 4) fiesta("asuncion", "08-15", "La Asunción: la procesión de la Virgen por la Cité", certeza = HISTORICO, fuente = F_ASUNCION) else null,
        if (t <= 4) fiesta("todos_santos", "11-01", "Todos los Santos: las campanas de los difuntos", certeza = HISTORICO, fuente = F_TODOS_SANTOS) else null,
        if (t <= 4) fiesta("navidad", "12-25", "Navidad: la misa de medianoche", certeza = HISTORICO, fuente = F_NAVIDAD) else null,
    )

    val MARIE_JEANNE = Calendario(
        vida = "lavandera",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 6, nubes = 12, lluvia = 6, nieve = 3, niebla = 4), mes(despejado = 7, nubes = 11, lluvia = 6, nieve = 2, niebla = 2),
            mes(despejado = 9, nubes = 10, lluvia = 8, viento = 3), mes(despejado = 11, nubes = 9, lluvia = 8, viento = 1),
            mes(despejado = 13, nubes = 8, lluvia = 8, tormenta = 1), mes(despejado = 15, nubes = 7, lluvia = 6, tormenta = 2),
            mes(despejado = 17, nubes = 6, lluvia = 5, tormenta = 3), mes(despejado = 16, nubes = 7, lluvia = 5, tormenta = 3),
            mes(despejado = 14, nubes = 8, lluvia = 6, niebla = 1), mes(despejado = 10, nubes = 9, lluvia = 8, niebla = 3),
            mes(despejado = 7, nubes = 11, lluvia = 8, niebla = 4), mes(despejado = 6, nubes = 12, lluvia = 7, nieve = 2, niebla = 4),
        ),
        temporadas = listOf(
            TemporadaVida("1789", "bastilla",
                "El Sena está helado y no hay ropa que lavar. Arden las casas de Réveillon. Se abren los Estados Generales. Cae la Bastilla y Jacques se apunta en la guardia del barrio. En la plaza leen los derechos del hombre. Marie-Jeanne camina con las mujeres hasta Versalles y vuelve con el rey. Paul empieza a ayudar en el barco.",
                anioDeParis(1) + listOf(
                    mundo("hielo", "01-01", "El Sena está helado de orilla a orilla y los barcos-lavaderos, presos en el hielo", certeza = APROXIMADO, escena = "hielo", clima = Clima.NIEVE),
                    mundo("deshielo", "01-20", "Se rompe el hielo del Sena y vuelve el trabajo", certeza = APROXIMADO),
                    guerra("reveillon", "04-27", "En el arrabal de Saint-Antoine queman la casa del fabricante Réveillon", 3, HISTORICO, F_REVEILLON, escena = "fuego"),
                    mundo("estados", "05-05", "Se abren en Versalles los Estados Generales", certeza = HISTORICO, fuente = F_ESTADOS),
                    guerra("bastilla", "07-14", "Cae la Bastilla", certeza = HISTORICO, fuente = F_BASTILLA, escena = "guerra"),
                    suceso("jacques_guardia", "07-20", "Jacques se apunta en la guardia del barrio y le dan una escarapela"),
                    mundo("derechos", "08-28", "En la plaza leen en voz alta la declaración de los derechos del hombre", certeza = APROXIMADO, fuente = F_DECLARACION),
                    guerra("versalles", "10-05", "Las mujeres de París caminan hasta Versalles a pedir pan", 2, HISTORICO, F_VERSALLES, escena = "marcha"),
                    suceso("pies", "10-07", "Vuelve al barco con los pies deshechos y el rey ya en París"),
                    suceso("paul_barco", "11-16", "Paul empieza a ayudar en el barco: acarrea la ropa limpia"),
                )),
            TemporadaVida("1790", "federacion",
                "Marie-Jeanne cava con las lavanderas en el Campo de Marte para la fiesta de la Federación, y la ve con Jacques en las tribunas. Se dice que los curas tendrán que jurar. Paul tiene sarampión. Llega al barrio Antoine, un impresor. Thérèse se cae al río. El río se lleva una sábana.",
                anioDeParis(2) + listOf(
                    suceso("sarampion", "02-08", "Paul tiene el sarampión y Marie-Jeanne no va al barco en una semana"),
                    suceso("antoine", "05-10", "Se muda al barrio Antoine, un impresor que lee en voz alta los periódicos"),
                    suceso("cavar", "07-06", "Va con las lavanderas del barco a cavar en el Campo de Marte"),
                    mundo("clero", "07-12", "Se dice que los curas serán pagados por la nación y tendrán que jurar", certeza = APROXIMADO, fuente = F_CLERO),
                    fiesta("federacion", "07-14", "La fiesta de la Federación en el Campo de Marte, bajo la lluvia", certeza = HISTORICO, fuente = F_FEDERACION, clima = Clima.LLUVIA),
                    suceso("therese_rio", "09-24", "La vieja Thérèse se cae al río y la sacan entre todas"),
                    suceso("sabana", "11-12", "La corriente se lleva una sábana de un cliente, y la tiene que pagar"),
                )),
            TemporadaVida("1791", "varennes",
                "El cura del barrio jura la Constitución y medio barrio deja de ir a su misa. El maestro Gaspard sube el alquiler del sitio en el barco. Entierran a Mirabeau. El rey huye y lo traen de vuelta en silencio. En el Campo de Marte la guardia dispara sobre la gente que firmaba una petición; Jacques no estaba. Antoine imprime un periódico propio. El rey acepta la Constitución. Thérèse tose.",
                anioDeParis(3) + listOf(
                    mundo("juramento", "01-09", "El cura del barrio jura la Constitución en la misa, y medio barrio se va", certeza = APROXIMADO, fuente = F_CLERO),
                    suceso("alquiler", "03-01", "El maestro Gaspard sube lo que cobra por el sitio en el barco"),
                    mundo("mirabeau", "04-04", "Entierran a Mirabeau, y medio París va detrás del carro", certeza = APROXIMADO, fuente = F_MIRABEAU, escena = "luto"),
                    mundo("varennes", "06-25", "Traen al rey de vuelta a París entre dos filas de guardias, en silencio", certeza = HISTORICO, fuente = F_VARENNES),
                    guerra("campo_marte", "07-17", "En el Campo de Marte la guardia dispara sobre los que firmaban una petición", certeza = HISTORICO, fuente = F_CAMPO_MARTE, escena = "luto"),
                    suceso("periodico", "09-10", "Antoine imprime un periódico propio y se lo lee a las lavanderas"),
                    mundo("constitucion", "09-14", "El rey acepta la Constitución, y la leen en las plazas", certeza = APROXIMADO, fuente = F_CONSTITUCION),
                    suceso("abrigo", "11-20", "Se compra un abrigo de segunda mano en el mercado de los Inocentes"),
                    suceso("therese_tos", "12-10", "La vieja Thérèse tose todo el día, pero no suelta la paleta"),
                )),
            TemporadaVida("1792", "republica",
                "Hay guerra. Jacques se va de voluntario a la frontera. Asaltan las Tullerías y cae el rey. Las matanzas de septiembre. Llega la noticia de Valmy y hay República. Marie-Jeanne lava sola el sitio de los dos.",
                anioDeParis(4) + listOf(
                    mundo("guerra", "04-20", "Hay guerra con el rey de Bohemia y de Hungría", certeza = HISTORICO, fuente = F_GUERRA_1792),
                    suceso("paul_cola", "05-20", "Paul hace solo la cola del pan, de noche, por primera vez"),
                    suceso("jacques_frontera", "07-25", "Jacques se va de voluntario a la frontera"),
                    guerra("tullerias", "08-10", "Asaltan las Tullerías: cae el rey", certeza = HISTORICO, fuente = F_10_AGOSTO, escena = "guerra"),
                    guerra("septiembre", "09-02", "Las matanzas de las cárceles", 5, HISTORICO, F_SEPTIEMBRE, escena = "luto"),
                    mundo("valmy", "09-22", "Llega la noticia: en Valmy se pararon los prusianos", certeza = APROXIMADO, fuente = F_VALMY),
                    mundo("republica", "09-23", "Ya no hay rey: es la República, y el año primero", certeza = APROXIMADO, fuente = F_CALENDARIO),
                    suceso("sola", "10-15", "Lava sola el sitio de los dos, con Paul acarreando"),
                )),
            TemporadaVida("1793, año I y II de la República", "terror",
                "Matan al rey en la plaza. El Sena crece. Le dan la tarjeta de la sección para el pan. Jacques escribe desde un hospital, caen los girondinos y Jacques vuelve cojo. Matan a Marat. Todos los mozos a la guerra. Paul aprende a leer en la escuela de la sección. Matan a la reina. Llega el calendario nuevo y la fiesta de la Razón en Notre-Dame. Ya solo hay un pan, el de la igualdad.",
                anioDeParis(5) + listOf(
                    guerra("rey", "01-21", "Le cortan la cabeza al rey en la plaza de la Revolución", certeza = HISTORICO, fuente = F_LUIS_XVI, escena = "luto"),
                    mundo("crecida", "02-25", "El Sena crece y los barcos-lavaderos se balancean en sus amarras", certeza = INVENTADO, clima = Clima.LLUVIA),
                    suceso("tarjeta", "03-15", "Le dan la tarjeta de la sección para comprar el pan"),
                    suceso("jacques_carta", "05-02", "Le llega una carta de Jacques: está herido en un hospital del norte"),
                    guerra("girondinos", "06-02", "Las secciones rodean la Convención con cañones, y caen los girondinos", certeza = HISTORICO, fuente = F_GIRONDINOS),
                    suceso("jacques_vuelve", "06-15", "Jacques vuelve de la frontera con una pierna mala"),
                    mundo("marat", "07-15", "Llega la noticia: mataron a Marat en su bañera", certeza = APROXIMADO, fuente = F_MARAT, escena = "luto"),
                    mundo("leva", "08-24", "Todos los mozos solteros a la guerra: la leva en masa", certeza = APROXIMADO, fuente = F_LEVA),
                    suceso("paul_lee", "09-10", "Paul aprende a leer en la escuela de la sección"),
                    guerra("reina", "10-16", "Le cortan la cabeza a la reina", certeza = HISTORICO, fuente = F_REINA, escena = "luto"),
                    mundo("calendario", "10-24", "Llega el calendario nuevo: meses nuevos, semanas de diez días", certeza = HISTORICO, fuente = F_CALENDARIO),
                    fiesta("razon", "11-10", "La fiesta de la Razón en Notre-Dame", certeza = HISTORICO, fuente = F_RAZON),
                    mundo("pan_igualdad", "12-10", "Ya solo hay un pan para todos: el pan de la igualdad", certeza = APROXIMADO),
                )),
        ),
        arcos = listOf(Arco("pan", TipoArco.OTRO, listOf(
            p(1, "01-01", 0.6), p(1, "07-14", 1.0), p(1, "11-01", 0.6), p(2, "12-31", 0.4), p(3, "12-31", 0.45), p(4, "12-31", 0.7), p(5, "09-29", 1.0), p(5, "12-31", 0.9),
        ))),
        objetos = listOf("paleta", "tabla", "jabon", "ceniza", "cesta", "pan", "escarapela", "periodico", "sueldo", "farol", "pinzas", "sueco"),
        estados = listOf(Estado("helada", "hielo", "deshielo"), Estado("marcha", "versalles", "pies")),
    )

    // =====================================================================
    //  Jean-Baptiste, soldado de infantería. De la campaña de Rusia a un pueblo de Borgoña, de 1812 a 1816.
    // =====================================================================
    //
    // Su gente (toda inventada): Lucien, su amigo de la compañía, del mismo pueblo, que muere en el Berezina. El sargento
    // Morel, que le enseña a leer en 1813. Su madre y su hermana, en un pueblo de viñas de Borgoña. Margot, la hija del
    // tonelero, con quien se casa en la temporada 5.
    //
    // Lo real: la campaña de Rusia (el Niemen el 24 de junio de 1812, Smolensk, Borodino, Moscú del 14 de septiembre al 19
    // de octubre, el incendio, Maloyaroslavets, la nieve, Krasnoi, el Berezina, el emperador que se va en trineo); la
    // fiesta del emperador el 15 de agosto; Lützen, Bautzen, Leipzig y Hanau (1813); París y la abdicación (1814); Elba
    // (desde el 30 de mayo de 1814); los Cien Días y Waterloo (1815); Ney fusilado; el año sin verano (1816). Que lo vea
    // o se entere esos días es inventado.
    //
    // Claves de escena: guerra, nieve, fuego, marcha, luto, vendimia, fiesta. Arco: compania (cuántos de su compañía
    // quedan: baja en la retirada de Rusia y en Waterloo). Estados: «cuartel» (temporada 1: las últimas semanas en el
    // cuartel, antes de salir hacia el este), «moscu» (1: las cinco semanas en Moscú), «retirada» (1: de Moscú al Niemen),
    // «hospital» (2: el invierno en un hospital de Prusia) y «pueblo» (temporadas 3 a 5: en casa).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el vivac de la compañía, con menos fuegos según
    // `compania`; el patio del cuartel durante «cuartel»; Moscú quemada detrás durante «moscu»; la nieve y la columna rota
    // durante «retirada»; una sala de catres durante «hospital»; y la casa del pueblo con las viñas durante «pueblo».

    private val F_INVASION = "La campaña de Rusia" to "${W}French_invasion_of_Russia"
    private val F_SMOLENSK = "Smolensk" to "${W}Battle_of_Smolensk_(1812)"
    private val F_BORODINO = "Borodino" to "${W}Battle_of_Borodino"
    private val F_INCENDIO = "El incendio de Moscú" to "${W}Fire_of_Moscow_(1812)"
    private val F_MALOYAROSLAVETS = "Maloyaroslavets" to "${W}Battle_of_Maloyaroslavets"
    private val F_KRASNOI = "Krasnoi" to "${W}Battle_of_Krasnoi"
    private val F_BEREZINA = "El Berezina" to "${W}Battle_of_Berezina"
    private val F_SAN_NAPOLEON = "San Napoleón" to "${W}Saint_Napoleon"
    private val F_LUTZEN = "Lützen" to "${W}Battle_of_L%C3%BCtzen_(1813)"
    private val F_BAUTZEN = "Bautzen" to "${W}Battle_of_Bautzen_(1813)"
    private val F_LEIPZIG = "Leipzig" to "${W}Battle_of_Leipzig"
    private val F_HANAU = "Hanau" to "${W}Battle_of_Hanau"
    private val F_SEXTA = "La guerra de la sexta coalición" to "${W}War_of_the_Sixth_Coalition"
    private val F_DRESDE = "Dresde" to "${W}Battle_of_Dresden"
    private val F_MONTMIRAIL = "Montmirail" to "${W}Battle_of_Montmirail"
    private val F_LUIS_XVIII = "Luis XVIII" to "${W}Louis_XVIII"
    private val F_LIGNY = "Ligny" to "${W}Battle_of_Ligny"
    private val F_PARIS_1814 = "La toma de París" to "${W}Battle_of_Paris_(1814)"
    private val F_FONTAINEBLEAU = "El tratado de Fontainebleau" to "${W}Treaty_of_Fontainebleau_(1814)"
    private val F_NAPOLEON = "Napoleón" to "${W}Napoleon"
    private val F_CIEN_DIAS = "Los Cien Días" to "${W}Hundred_Days"
    private val F_WATERLOO = "Waterloo" to "${W}Battle_of_Waterloo"
    private val F_NEY = "Ney" to "${W}Michel_Ney"
    private val F_SIN_VERANO = "El año sin verano" to "${W}Year_Without_a_Summer"

    private val PASCUA_PUEBLO = listOf(null, null, "04-10", "03-26", "04-14")

    /** Lo que vuelve cada año, en la temporada [t]: en campaña, poco; en el pueblo, el año de las viñas. */
    private fun anioDelSoldado(t: Int): List<DiaEspecial> = listOfNotNull(
        if (t <= 3) fiesta("anio_nuevo", "01-01", "Año nuevo", certeza = HISTORICO, fuente = F_ANIO_NUEVO) else null,
        if (t >= 4) fiesta("san_vicente", "01-22", "San Vicente, el de los viñadores: la procesión entre las viñas", certeza = APROXIMADO) else null,
        PASCUA_PUEBLO[t - 1]?.let { fiesta("pascua", it, "Pascua", certeza = HISTORICO, escena = "campanas") },
        if (t <= 2) fiesta("san_napoleon", "08-15", "San Napoleón, el día del emperador: ración doble", certeza = HISTORICO, fuente = F_SAN_NAPOLEON) else null,
        if (t in 3..4) cosecha("vendimia", "09-28", "La vendimia en las viñas del pueblo", 10, APROXIMADO, escena = "vendimia") else null,
        if (t >= 3) fiesta("san_martin", "11-11", "San Martín: se prueba el vino nuevo y se mata el cerdo", certeza = APROXIMADO) else null,
        fiesta("navidad", "12-25", "Navidad", certeza = HISTORICO, fuente = F_NAVIDAD),
        if (t <= 2) fiesta("nochevieja", "12-31", "La última noche del año", certeza = APROXIMADO)
        else fiesta("nochevieja_casa", "12-31", "La última noche del año, en casa", certeza = APROXIMADO),
    )

    val JEAN_BAPTISTE = Calendario(
        vida = "granadero",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 7, nubes = 10, lluvia = 4, nieve = 7, niebla = 3), mes(despejado = 8, nubes = 10, lluvia = 4, nieve = 5, niebla = 1),
            mes(despejado = 10, nubes = 10, lluvia = 6, nieve = 3, viento = 2), mes(despejado = 12, nubes = 9, lluvia = 8, viento = 1),
            mes(despejado = 15, nubes = 8, lluvia = 7, tormenta = 1), mes(despejado = 17, nubes = 6, lluvia = 5, tormenta = 2),
            mes(despejado = 18, nubes = 6, lluvia = 4, tormenta = 3), mes(despejado = 17, nubes = 6, lluvia = 5, tormenta = 3),
            mes(despejado = 15, nubes = 8, lluvia = 6, niebla = 1), mes(despejado = 11, nubes = 9, lluvia = 7, niebla = 3, nieve = 1),
            mes(despejado = 7, nubes = 10, lluvia = 6, nieve = 5, niebla = 2), mes(despejado = 6, nubes = 10, lluvia = 4, nieve = 8, niebla = 3),
        ),
        temporadas = listOf(
            TemporadaVida("1812", "rusia",
                "La compañía sale del cuartel hacia el este, cruza el Rin, pasa hambre en Polonia y cruza el Niemen. Smolensk, Borodino y Moscú, que arde. Cinco semanas en la ciudad quemada y luego la retirada: Maloyaroslavets, la nieve, Krasnoi, el Berezina, donde muere Lucien. El emperador se va en trineo. Jean-Baptiste vuelve a cruzar el Niemen sobre el hielo.",
                anioDelSoldado(1) + listOf(
                    suceso("botas", "01-20", "Le dan botas nuevas y un capote para el invierno"),
                    suceso("sale_cuartel", "03-01", "La compañía sale del cuartel hacia el este"),
                    suceso("rin_ida", "04-02", "Cruza el Rin hacia Alemania, y Lucien canta todo el puente"),
                    suceso("polonia", "05-10", "En Polonia no hay qué comer: la compañía vive de lo que encuentra"),
                    guerra("niemen", "06-24", "Cruzan el Niemen: empieza la guerra con Rusia", certeza = HISTORICO, fuente = F_INVASION, escena = "marcha"),
                    guerra("smolensk", "08-17", "Smolensk arde", 2, HISTORICO, F_SMOLENSK, escena = "fuego"),
                    guerra("borodino", "09-07", "La gran batalla del río Moskova", certeza = HISTORICO, fuente = F_BORODINO, escena = "guerra"),
                    mundo("moscu", "09-14", "Entran en Moscú: la ciudad está vacía", certeza = HISTORICO, fuente = F_INVASION),
                    mundo("incendio", "09-15", "Moscú arde cuatro días", 4, HISTORICO, F_INCENDIO, escena = "fuego"),
                    mundo("sale_moscu", "10-19", "El ejército sale de Moscú cargado de lo que robó", certeza = HISTORICO, fuente = F_INVASION, escena = "marcha"),
                    guerra("maloyaroslavets", "10-24", "Maloyaroslavets: el camino del sur se cierra", certeza = HISTORICO, fuente = F_MALOYAROSLAVETS, escena = "guerra"),
                    mundo("nieve", "11-06", "Empieza a nevar y ya no para", certeza = APROXIMADO, fuente = F_INVASION, escena = "nieve", clima = Clima.NIEVE),
                    guerra("krasnoi", "11-15", "Krasnoi: los cosacos cortan el camino", 4, HISTORICO, F_KRASNOI, escena = "nieve"),
                    guerra("berezina", "11-26", "El Berezina: los puentes de los pontoneros", 3, HISTORICO, F_BEREZINA, escena = "nieve"),
                    suceso("lucien", "11-29", "Queman los puentes, y Lucien se queda en la otra orilla", escena = "luto"),
                    mundo("trineo", "12-06", "Se dice que el emperador se fue a París en un trineo", certeza = APROXIMADO, fuente = F_INVASION),
                    suceso("niemen_vuelta", "12-14", "Cruza otra vez el Niemen, sobre el hielo, con los que quedan"),
                )),
            TemporadaVida("1813", "alemania",
                "Jean-Baptiste se cura los pies en un hospital de Prusia y lo meten en un regimiento de muchachos. Lützen y Bautzen, y dos meses de armisticio. El sargento Morel le enseña a leer. Dresde. Le escribe a su madre su primera carta. Leipzig y la retirada por Hanau. Cruza el Rin de vuelta a Francia.",
                anioDelSoldado(2) + listOf(
                    suceso("hospital", "01-10", "Llega a un hospital de Prusia con los pies helados"),
                    suceso("carta_madre", "02-15", "Le llega una carta de su madre, y se la lee un compañero"),
                    suceso("regimiento", "03-20", "Lo meten en un regimiento nuevo de muchachos de dieciocho años"),
                    guerra("lutzen", "05-02", "Lützen: los muchachos aguantan", certeza = HISTORICO, fuente = F_LUTZEN, escena = "guerra"),
                    guerra("bautzen", "05-20", "Bautzen: dos días de batalla", 2, HISTORICO, F_BAUTZEN, escena = "guerra"),
                    mundo("armisticio", "06-04", "Armisticio: dos meses sin batallas", certeza = HISTORICO, fuente = F_SEXTA),
                    suceso("leer", "07-10", "El sargento Morel le enseña a leer en las horas muertas del campamento"),
                    guerra("dresde", "08-26", "Dresde: dos días de lluvia y de batalla", 2, HISTORICO, F_DRESDE, escena = "guerra", clima = Clima.LLUVIA),
                    suceso("primera_carta", "09-20", "Le escribe a su madre su primera carta, solo, con muchas faltas"),
                    guerra("leipzig", "10-16", "Leipzig: la batalla de las naciones", 4, HISTORICO, F_LEIPZIG, escena = "guerra"),
                    guerra("hanau", "10-30", "Hanau: abren el camino de vuelta a cañonazos", 2, HISTORICO, fuente = F_HANAU, escena = "guerra"),
                    suceso("rin", "11-08", "Cruza el Rin de vuelta a Francia"),
                    mundo("tifus", "12-05", "El tifus entra en el campamento de Maguncia", certeza = INVENTADO, escena = "luto"),
                )),
            TemporadaVida("1814", "abdicacion",
                "La guerra entra en Francia. Cae París, el emperador abdica y se va a Elba, y vuelve el rey. Jean-Baptiste vuelve a su pueblo, ayuda a su madre en la viña, conoce a Margot y vendimia. Le lee a su madre el almanaque.",
                anioDelSoldado(3) + listOf(
                    suceso("francia", "02-01", "La guerra entra en Francia: pelean en la nieve de la Champaña"),
                    guerra("montmirail", "02-11", "Montmirail: ganan una más, en la nieve", certeza = HISTORICO, fuente = F_MONTMIRAIL, escena = "nieve"),
                    mundo("paris", "04-02", "Llega la noticia: los aliados entraron en París", certeza = APROXIMADO, fuente = F_PARIS_1814),
                    mundo("abdica", "04-12", "Llega la noticia: el emperador abdica en Fontainebleau", certeza = APROXIMADO, fuente = F_FONTAINEBLEAU),
                    mundo("rey_vuelve", "05-03", "Llega la noticia: el rey ha vuelto a París", certeza = APROXIMADO, fuente = F_LUIS_XVIII),
                    mundo("elba", "05-30", "El emperador está en la isla de Elba", certeza = HISTORICO, fuente = F_NAPOLEON),
                    suceso("licencia", "06-05", "Lo licencian con un papel y unas monedas"),
                    suceso("vuelve_pueblo", "06-20", "Vuelve a su pueblo y su madre no lo reconoce"),
                    suceso("margot", "08-10", "Conoce a Margot, la hija del tonelero, en la fiesta del pueblo"),
                    suceso("vino_nuevo", "10-25", "Prueba el vino nuevo en la bodega del tonelero"),
                    suceso("almanaque", "11-25", "Le lee a su madre, en voz alta, el almanaque del año que viene"),
                )),
            TemporadaVida("1815", "waterloo",
                "Jean-Baptiste pasa el invierno en el pueblo. El emperador vuelve de Elba y él vuelve al uniforme. Ligny y Waterloo. Vuelve al pueblo con la compañía deshecha, y al emperador se lo llevan a una isla. Fusilan al mariscal Ney. Pide la mano de Margot.",
                anioDelSoldado(4) + listOf(
                    fiesta("invierno_casa", "01-01", "Año nuevo en casa, con su madre y su hermana", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
                    mundo("vuelve_emperador", "03-08", "Llega la noticia: el emperador desembarcó en Francia", certeza = APROXIMADO, fuente = F_CIEN_DIAS),
                    suceso("poda", "02-10", "Poda las viñas con su madre, en el frío"),
                    suceso("se_va", "04-05", "Vuelve a ponerse el uniforme y se va"),
                    suceso("veteranos", "05-10", "Lo ponen en una compañía de veteranos, que se conocen de Rusia"),
                    guerra("ligny", "06-16", "Ligny: ganan, y se dice que mañana se acaba", certeza = HISTORICO, fuente = F_LIGNY, escena = "guerra"),
                    guerra("waterloo", "06-18", "Waterloo", certeza = HISTORICO, fuente = F_WATERLOO, escena = "guerra"),
                    suceso("vuelve_waterloo", "07-20", "Vuelve al pueblo por segunda vez, a pie, sin uniforme"),
                    mundo("isla", "08-15", "San Napoleón, y ya no hay emperador: se lo llevan a una isla perdida en el mar", certeza = APROXIMADO, fuente = F_NAPOLEON),
                    suceso("pide", "11-05", "Pide la mano de Margot al tonelero"),
                    mundo("ney", "12-12", "Llega la noticia: fusilaron al mariscal Ney", certeza = APROXIMADO, fuente = F_NEY, escena = "luto"),
                )),
            TemporadaVida("1816", "sin_verano",
                "Jean-Baptiste se casa con Margot. Hiela en mayo. Le escribe el sargento Morel. No llega el verano: llueve y hace frío, y la uva no madura. La vendimia se hace tarde y mal. El pan sube y hay hambre. Margot espera un hijo.",
                anioDelSoldado(5) + listOf(
                    fiesta("anio_en_casa", "01-01", "Año nuevo en casa", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
                    suceso("boda", "02-20", "Se casa con Margot en la iglesia del pueblo", escena = "fiesta"),
                    suceso("poda_margot", "03-20", "Poda la viña con Margot"),
                    mundo("helada_mayo", "05-12", "Hiela en mayo y se queman los brotes de la viña", certeza = INVENTADO, clima = Clima.NIEVE),
                    mundo("frio", "06-15", "No llega el verano: llueve y hace frío como en otoño", certeza = APROXIMADO, fuente = F_SIN_VERANO, clima = Clima.LLUVIA),
                    suceso("morel", "06-30", "Le llega una carta del sargento Morel, que vive en Lyon"),
                    mundo("lluvia_julio", "07-20", "Llueve todo julio: el trigo se pudre en el campo", certeza = APROXIMADO, fuente = F_SIN_VERANO, clima = Clima.LLUVIA),
                    suceso("margot_espera", "08-20", "Margot le dice que espera un hijo"),
                    cosecha("vendimia_fria", "10-25", "La vendimia más tardía que se recuerda, con la uva verde", 7, APROXIMADO, escena = "vendimia"),
                    mundo("pan_caro", "11-28", "El pan cuesta el doble y hay motines en los mercados", certeza = APROXIMADO, fuente = F_SIN_VERANO),
                    suceso("uva_verde", "09-15", "Sube a la viña cada día: la uva sigue verde"),
                    suceso("pension", "10-12", "Le llega la media paga de los soldados licenciados"),
                )),
        ),
        arcos = listOf(Arco("compania", TipoArco.OTRO, listOf(
            p(1, "01-01", 1.0), p(1, "09-07", 0.8), p(1, "10-19", 0.7), p(1, "12-14", 0.1), p(2, "03-20", 0.9, salto = true), p(2, "10-19", 0.6), p(3, "04-12", 0.4),
            p(4, "04-05", 0.9, salto = true), p(4, "06-18", 0.3), p(5, "12-31", 0.3),
        ))),
        objetos = listOf("fusil", "bayoneta", "mochila", "capote", "chaco", "cantimplora", "galleta", "pipa", "carta", "cuchara", "botas", "manta"),
        estados = listOf(
            Estado("cuartel", "botas", "sale_cuartel"), Estado("moscu", "moscu", "sale_moscu"), Estado("retirada", "sale_moscu", "niemen_vuelta"),
            Estado("hospital", "hospital", "regimiento"),
            Estado("pueblo", "vuelve_pueblo", "nochevieja_casa"), Estado("pueblo", "invierno_casa", "se_va"), Estado("pueblo", "vuelve_waterloo", "nochevieja_casa"),
            Estado("pueblo", "anio_en_casa", "nochevieja_casa"),
            // Un estado se acaba el día de su «hasta»; este cubre ese último día del año.
            Estado("pueblo", "nochevieja_casa", null),
        ),
    )

    // =====================================================================
    //  El joven naturalista. A bordo de un bergantín de la marina, alrededor del mundo, de 1832 a 1836.
    // =====================================================================
    //
    // Su gente (inventada, salvo el capitán): el capitán, que es FitzRoy pero aquí no se nombra, como el naturalista no
    // lleva nombre en su ficha. El muchacho que le ayuda a preparar los especímenes. El cirujano, que le presta libros. Sus
    // hermanas, que le escriben. Los fueguinos que viajan a bordo de vuelta a su tierra.
    //
    // Lo real: el viaje del Beagle (1831–1836), en el que iba Charles Darwin, día por día según la página: Bahía, Río,
    // Punta Alta, Tierra del Fuego, la misión de los fueguinos, el general Rosas, los huesos gigantes, Valparaíso, los
    // Andes, el terremoto de 1835, las Galápagos, Australia, las islas Keeling, el Cabo y la vuelta a Falmouth. Lo que
    // siente y lo que le pasa por dentro es inventado.
    //
    // Claves de escena: fiesta, tormenta, selva, fósiles, nieve, terremoto, islas. Arco: cajones (cuántos cajones de
    // especímenes lleva: se vacía cuando manda un envío). Estados: «tierra» (sus temporadas largas en tierra: Río, la
    // pampa, los Andes), «enfermo» (temporada 3: el mes en cama en Valparaíso), «islas» (temporada 4: las Galápagos) y
    // «casa» (temporada 5: de vuelta en Inglaterra).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el bergantín en el mar, con la mesa de
    // trabajo y los cajones según `cajones`; la selva, la pampa o la montaña durante «tierra»; un cuarto con una ventana al puerto durante «enfermo»; las islas negras de lava
    // con tortugas durante «islas»; y una casa inglesa con jardín durante «casa».

    private val F_BEAGLE = "El viaje del Beagle" to "${W}Second_voyage_of_HMS_Beagle"
    private val F_FITZROY = "El capitán FitzRoy" to "${W}Robert_FitzRoy"
    private val F_TERREMOTO = "El terremoto de 1835" to "${W}1835_Concepci%C3%B3n_earthquake"

    val NATURALISTA = Calendario(
        vida = "naturalista",
        estaciones = Calendario.SIN_ESTACIONES,
        clima = listOf(
            mes(despejado = 14, nubes = 8, lluvia = 5, tormenta = 2, viento = 2), mes(despejado = 14, nubes = 8, lluvia = 5, tormenta = 2, viento = 2),
            mes(despejado = 13, nubes = 9, lluvia = 5, tormenta = 2, viento = 2), mes(despejado = 12, nubes = 9, lluvia = 6, tormenta = 1, viento = 2),
            mes(despejado = 11, nubes = 10, lluvia = 6, niebla = 1, viento = 3), mes(despejado = 10, nubes = 10, lluvia = 6, nieve = 1, viento = 3),
            mes(despejado = 10, nubes = 10, lluvia = 6, nieve = 1, viento = 3), mes(despejado = 11, nubes = 10, lluvia = 5, nieve = 1, viento = 3),
            mes(despejado = 12, nubes = 9, lluvia = 5, viento = 3), mes(despejado = 13, nubes = 9, lluvia = 5, viento = 2),
            mes(despejado = 14, nubes = 8, lluvia = 5, tormenta = 1, viento = 2), mes(despejado = 14, nubes = 8, lluvia = 5, tormenta = 2, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1832", "brasil",
                "El naturalista se marea en el Atlántico, pisa la primera isla volcánica, cruza el ecuador y llega a la selva de Bahía. En Río alquila una casa en una bahía y pasa allí dos meses. Manda su primer cajón a Inglaterra. En Punta Alta encuentra huesos gigantes en la roca. En diciembre llegan a Tierra del Fuego.",
                listOf(
                    fiesta("anio_nuevo", "01-01", "Año nuevo en alta mar, mareado", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
                    suceso("mareo", "01-05", "Lleva una semana mareado en la hamaca, sin comer más que pasas"),
                    mundo("praia", "01-16", "Primera tierra: una isla volcánica de Cabo Verde", certeza = APROXIMADO, fuente = F_BEAGLE),
                    rito("ecuador", "02-17", "Cruzan el ecuador: a los nuevos los rapan y los echan al agua", certeza = APROXIMADO),
                    mundo("bahia", "02-28", "Llegan a Bahía: la primera selva", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("esclavos", "03-15", "En Bahía discute con el capitán sobre los esclavos", certeza = APROXIMADO, fuente = F_BEAGLE),
                    mundo("rio", "04-04", "Llegan a la bahía de Río de Janeiro", certeza = APROXIMADO, fuente = F_BEAGLE),
                    suceso("botafogo", "04-25", "Alquila una casa en una bahía de Río y se queda en tierra"),
                    suceso("escarabajos", "05-20", "Cuenta sesenta y ocho escarabajos distintos en una sola tarde"),
                    suceso("vuelve_bordo", "07-05", "Vuelve al bergantín con cajones llenos"),
                    mundo("envio", "08-19", "Sale hacia Inglaterra, en el correo de Falmouth, su primer cajón de especímenes", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("punta_alta", "09-22", "En Punta Alta encuentran huesos de animales gigantes en la roca", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("carta", "11-02", "Le llegan, todas juntas, seis cartas de sus hermanas"),
                    mundo("fuego", "12-18", "Llegan a Tierra del Fuego: hombres desnudos en la nieve", certeza = HISTORICO, fuente = F_BEAGLE, escena = "nieve"),
                    fiesta("navidad", "12-25", "Navidad cerca del cabo de Hornos, con tormenta", certeza = HISTORICO, fuente = F_NAVIDAD, clima = Clima.TORMENTA),
                    mundo("cabo_hornos", "12-28", "El cabo de Hornos: olas como colinas", certeza = APROXIMADO, escena = "tormenta", clima = Clima.TORMENTA),
                )),
            TemporadaVida("1833", "pampa",
                "Dejan a los fueguinos en su tierra con una misión. El capitán compra una goleta. El naturalista contrata a un muchacho que lo ayude y pasa el invierno en un pueblo del Plata. Cabalga meses por la pampa con gauchos, conoce a un general que hace la guerra a los indios saca de una barranca un esqueleto casi entero y escapa de una revuelta en Buenos Aires. Manda a casa todos sus fósiles. En Navidad comen guanaco en Puerto Deseado.",
                listOf(
                    fiesta("anio_nuevo", "01-01", "Año nuevo entre las islas del sur", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
                    mundo("mision", "01-23", "Dejan a los fueguinos en su tierra, con chozas, huertos y un misionero", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("goleta", "03-12", "El capitán compra con su dinero una goleta para ayudar al bergantín", certeza = APROXIMADO, fuente = F_FITZROY),
                    suceso("ayudante", "04-20", "Contrata a un muchacho de la tripulación para que le ayude con los especímenes"),
                    suceso("maldonado", "07-05", "Pasa el invierno en un pueblo del Plata, pagando a los niños por cada pájaro"),
                    suceso("sale_pampa", "08-08", "Sale a caballo por la pampa con unos gauchos"),
                    mundo("rosas", "08-12", "Conoce a un general que hace la guerra a los indios y le da un pasaporte", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("huesos", "08-25", "Vuelve a las barrancas de la bahía a buscar más huesos"),
                    suceso("esqueleto", "09-10", "Saca de la barranca un esqueleto casi entero, con los huesos en su sitio"),
                    mundo("carcarana", "10-01", "Busca fósiles en las barrancas de un río de la pampa", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("vuelve_pampa", "11-03", "Escapa de una revuelta en Buenos Aires y vuelve al bergantín", certeza = APROXIMADO, fuente = F_BEAGLE),
                    mundo("envio_fosiles", "11-14", "Manda a Inglaterra, en el correo de Falmouth, todos sus fósiles", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("fiebre", "12-02", "Le dan unas fiebres que lo tumban en la hamaca dos semanas"),
                    mundo("deseado", "12-23", "Llegan a Puerto Deseado: viento, piedra y guanacos", certeza = HISTORICO, fuente = F_BEAGLE),
                    fiesta("navidad", "12-25", "Navidad en Puerto Deseado: guanaco asado", certeza = HISTORICO, fuente = F_NAVIDAD),
                    suceso("diario", "06-01", "Llena su tercer cuaderno de notas"),
                    suceso("hermanas", "02-20", "Le escribe a su hermana pequeña sobre las ballenas"),
                    mundo("ballenas", "05-10", "Ballenas alrededor del bergantín toda una mañana", certeza = INVENTADO),
                )),
            TemporadaVida("1834", "andes",
                "Llegan a Puerto San Julián. Vuelven a la misión de los fueguinos y la encuentran vacía. Cruzan el estrecho al Pacífico y llegan a Valparaíso. El naturalista sube a los Andes y cae enfermo un mes. El capitán tiene problemas con el almirantazgo por la goleta. Salen hacia las islas del sur.",
                listOf(
                    fiesta("anio_nuevo", "01-01", "Año nuevo en la costa de la Patagonia", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
                    mundo("san_julian", "01-09", "Llegan a Puerto San Julián", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("mision_vacia", "03-05", "Vuelven a la misión de los fueguinos: el misionero se fue y las chozas están vacías", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("pacifico", "06-10", "Ve por primera vez el océano Pacífico"),
                    mundo("valparaiso", "07-23", "Llegan a Valparaíso", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("andes", "08-14", "Sale a caballo hacia los Andes", cadena = "andes"),
                    mundo("almirantazgo", "09-05", "El almirantazgo le riñe al capitán por la goleta que compró", certeza = APROXIMADO, fuente = F_FITZROY),
                    suceso("enfermo", "09-20", "Cae enfermo en el camino de vuelta y pasa un mes en cama", cadena = "andes"),
                    suceso("vuelve_andes", "10-27", "Vuelve al bergantín flaco y con el pelo más blanco", cadena = "andes"),
                    mundo("chonos", "11-11", "Salen a medir las islas del sur, entre la lluvia", certeza = HISTORICO, fuente = F_BEAGLE, clima = Clima.LLUVIA),
                    fiesta("navidad", "12-25", "Navidad en las islas del sur, lloviendo", certeza = HISTORICO, fuente = F_NAVIDAD, clima = Clima.LLUVIA),
                    suceso("conchas", "05-05", "Encuentra conchas de mar en lo alto de un cerro"),
                    suceso("carta_hermana", "04-02", "Le llega una carta de su hermana: en casa hay una boda"),
                    mundo("tormenta_estrecho", "05-25", "Una tormenta en el estrecho parte un bote", certeza = INVENTADO, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("libro", "02-15", "El cirujano le presta un libro de geología que lee tres veces"),
                    mundo("volcan", "12-01", "Ven un volcán de la costa echando fuego en la noche", certeza = INVENTADO, escena = "fuego"),
                )),
            TemporadaVida("1835", "galapagos",
                "Un terremoto sacude Valdivia y destruye Concepción. El naturalista cruza los Andes y escribe a su familia que lo que ha visto es importante. En Lima hay una revuelta. Pasan cinco semanas en las islas Galápagos, y al salir hacia Tahití ve en sus notas que los pájaros de cada isla son distintos. Llegan a Nueva Zelanda.",
                listOf(
                    fiesta("anio_nuevo", "01-01", "Año nuevo en las islas de Chiloé", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
                    mundo("terremoto", "02-20", "Tiembla la tierra en Valdivia: el suelo se mueve como un barco", certeza = HISTORICO, fuente = F_TERREMOTO, escena = "terremoto"),
                    mundo("concepcion", "03-04", "Llegan a Concepción: la ciudad, en ruinas", certeza = HISTORICO, fuente = F_BEAGLE, escena = "terremoto"),
                    mundo("andes_2", "03-14", "Sale otra vez hacia los Andes, a cruzarlos", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("carta_casa", "04-23", "Vuelve de los Andes y escribe a su familia que lo que ha visto es importante", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("copiapo", "07-05", "Vuelve al bergantín en Copiapó y van a Lima, que está en revuelta", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("galapagos", "09-15", "Llegan a las islas Galápagos: lava negra y tortugas enormes", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("caparazones", "09-20", "Le dicen que las tortugas de cada isla tienen el caparazón distinto", certeza = APROXIMADO, fuente = F_BEAGLE),
                    mundo("sale_galapagos", "10-20", "Salen de las islas hacia Tahití", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("sinsontes", "10-28", "Pasando en limpio sus notas, ve que los pájaros de cada isla son distintos", certeza = APROXIMADO, fuente = F_BEAGLE),
                    mundo("tahiti", "11-15", "Llegan a Tahití", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("nueva_zelanda", "12-19", "Llegan a Nueva Zelanda", certeza = HISTORICO, fuente = F_BEAGLE),
                    fiesta("navidad", "12-25", "Navidad en Nueva Zelanda", certeza = HISTORICO, fuente = F_NAVIDAD),
                    suceso("lima", "08-05", "En Lima no puede bajar a tierra y cuenta especímenes en el camarote"),
                    suceso("tortuga", "09-25", "Monta sobre una tortuga gigante, que no se da cuenta"),
                    suceso("hermanas", "06-02", "Lee y relee una carta de sus hermanas de hace un año"),
                    mundo("mar_pacifico", "11-01", "Tres semanas de mar abierto, azul y vacío", certeza = INVENTADO),
                )),
            TemporadaVida("1836", "vuelta",
                "Llegan a Australia. En las islas Keeling estudia cómo crecen los arrecifes. En el Cabo se entera de que en Inglaterra leen sus cartas, y visita a un astrónomo famoso. Salen hacia casa y llegan a Falmouth. El naturalista vuelve a casa después de cinco años.",
                listOf(
                    fiesta("anio_nuevo", "01-01", "Año nuevo camino de Australia", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
                    mundo("australia", "01-12", "Ven Australia por primera vez", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("ornitorrinco", "02-20", "Ve un ornitorrinco en un arroyo"),
                    mundo("keeling", "04-01", "Llegan a las islas Keeling: un anillo de coral en el océano", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("arrecife", "04-12", "Entiende cómo crece un arrecife de coral, o cree que lo entiende"),
                    mundo("cabo", "05-31", "Llegan al cabo de Buena Esperanza", certeza = HISTORICO, fuente = F_BEAGLE),
                    mundo("astronomo", "06-15", "Va con el capitán a visitar a un astrónomo famoso", certeza = APROXIMADO, fuente = F_BEAGLE),
                    mundo("cartas_leidas", "06-02", "Se entera de que en Inglaterra se leen sus cartas en las sociedades de sabios", certeza = APROXIMADO, fuente = F_BEAGLE),
                    mundo("parte", "08-17", "Salen hacia casa", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("cajones", "09-10", "Cuenta los cajones que lleva: no le cabe uno más"),
                    mundo("falmouth", "10-02", "Después de una travesía de tormentas, anclan en Falmouth, en Inglaterra", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("llega_casa", "10-05", "Llega a su casa después de cinco años"),
                    fiesta("navidad", "12-25", "Navidad en casa", certeza = HISTORICO, fuente = F_NAVIDAD),
                    fiesta("fin_anio", "12-31", "La última noche del año, en casa, con sus hermanas", certeza = APROXIMADO),
                    suceso("diario_libro", "11-10", "Empieza a pasar en limpio su diario para hacer un libro"),
                    mundo("calor_indico", "03-10", "Calma chicha en el océano Índico: el calor no deja dormir", certeza = INVENTADO),
                    mundo("santa_elena", "07-08", "Paran seis días en una isla volcánica en medio del Atlántico", certeza = HISTORICO, fuente = F_BEAGLE),
                    suceso("mareo_final", "08-25", "Se marea otra vez, como el primer día"),
                )),
        ),
        arcos = listOf(Arco("cajones", TipoArco.OTRO, listOf(
            p(1, "01-01", 0.0), p(1, "08-18", 0.5), p(1, "08-19", 0.1, salto = true), p(2, "11-13", 0.8), p(2, "11-14", 0.1, salto = true),
            p(3, "12-31", 0.5), p(4, "12-31", 0.85), p(5, "09-10", 1.0), p(5, "12-31", 1.0),
        ))),
        objetos = listOf("hamaca", "libreta", "lupa", "red", "frasco", "escopeta", "martillo", "caja", "brujula", "carta", "limon", "galleta"),
        estados = listOf(
            // El mes enfermo de la temporada 3 cae dentro de «tierra»: va primero para que mande sobre ella.
            Estado("enfermo", "enfermo", "vuelve_andes"),
            Estado("tierra", "botafogo", "vuelve_bordo"), Estado("tierra", "sale_pampa", "vuelve_pampa"), Estado("tierra", "andes", "vuelve_andes"),
            Estado("tierra", "andes_2", "carta_casa"), Estado("islas", "galapagos", "sale_galapagos"), Estado("casa", "llega_casa", "fin_anio"), Estado("casa", "fin_anio", null),
        ),
    )
}
