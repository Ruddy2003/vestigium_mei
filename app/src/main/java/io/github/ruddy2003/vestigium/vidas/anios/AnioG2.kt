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
 * Los calendarios del grupo 2 (tanda A2): las cinco temporadas (2027 a 2031) de los otros seis personajes comunes de
 * «Otras vidas»: Kichizō, Mary, José, Rosa, Alguien del mundo y Alguien de México. Siguen las reglas de
 * `AnioPiloto.kt` y de `AnioG1.kt`. Qué es real y qué inventado, con cada fuente, está en `docs/historia/fuentes.md`.
 *
 * Los personajes comunes no tienen cuadro (decisión 2 del plan): solo cuento. Aun así cada calendario dice sus claves
 * de escena, sus arcos y sus estados, porque el cuento también los lee.
 *
 * Lo propio de este grupo:
 *
 * - **Kichizō** usa el calendario lunisolar japonés: sus fiestas caen cada año en otra fecha. Se calcularon con las
 *   lunas nuevas y los términos solares de esos años (ver `fuentes.md`); son «aproximado», porque el calendario oficial
 *   de la época pudo diferir en un día.
 * - **Mary, José y Rosa** tienen la Pascua gregoriana de cada año calculada, y de ella cuelgan el martes de carnaval,
 *   el miércoles de ceniza, el Viernes Santo y Pentecostés. Llevan fuente: la página de la fiesta.
 * - **Alguien del mundo y Alguien de México viven de 2025 a 2029.** Cuando se escribió esta tanda (noviembre de 2026),
 *   2025 y 2026 ya habían pasado, pero 2027 a 2029 eran futuro. En esas temporadas solo hay fiestas que vuelven cada
 *   año y cosas ya programadas (unas elecciones, unos juegos olímpicos, un censo), marcadas como «aproximado»: lo que
 *   de verdad pase se corrige en las tandas Y de cada año. «Alguien del mundo» vive en la India: ver su calendario.
 */
internal object AnioG2 {
    val TODOS: List<Calendario> by lazy { listOf(KICHIZO, MARY, JOSE, ROSA, MUNDO, MEXICO) }

    /** Días «históricos» sin enlace porque su fecha sale de un cálculo hecho en la sesión. Este grupo no tiene. */
    val CALCULADOS = emptySet<String>()

    private val W = "https://en.wikipedia.org/wiki/"
    private val ES = "https://es.wikipedia.org/wiki/"

    // =====================================================================
    //  Kichizō, carpintero. Edo (hoy Tokio), de 1750 a 1754.
    // =====================================================================
    //
    // Su gente (toda inventada): Ume, su mujer. Kinta, su hijo (6 años en la temporada 1). Okiku, la hija que nace en la
    // 2. Genbei, el maestro carpintero que le da las obras. Otake, la viuda vieja de la casa de al lado. Sōkichi, el que
    // alquila libros de casa en casa. Heiji, su amigo, bombero de la brigada del barrio. Sakubei, su hermano, que se
    // quedó en la aldea, en el campo de Musashi. Shinsuke, el aprendiz que entra en la 4.
    //
    // Lo real: las fiestas del año de Edo (el año nuevo lunar, el setsubun, las muñecas, los cerezos, el día de los
    // niños, los fuegos de Ryōgoku, las fiestas de Sannō y de Kanda, que se turnan cada año, Tanabata, el Bon y la luna
    // de otoño), la muerte del antiguo shōgun Yoshimune, el temblor de Takada, el cambio de era a Hōreki, la embajada de
    // Ryūkyū y la corrección del calendario. Los incendios de su barrio son inventados, pero Edo ardía casi cada
    // invierno. La «enfermedad de Edo» (el beriberi, por comer casi solo arroz blanco) se curaba yéndose al campo.
    //
    // Claves de escena: fiesta, faroles, fuegos, humo, luto, nieve. Arco: casa (la casa de vecindad: 1 = en pie; se
    // quema en la temporada 4 y la levantan de nuevo). Estados: «aldea» (temporada 3: se cura en el campo, con su
    // hermano), «sin_casa» (temporada 4: duermen en el templo mientras levantan la casa) y «oyama» (temporada 5: la
    // peregrinación al monte).

    private val F_SETSUBUN = "Setsubun" to "${W}Setsubun"
    private val F_OMISOKA = "Ōmisoka, la última noche del año" to "${W}%C5%8Cmisoka"
    private val F_SHOGATSU = "El año nuevo japonés" to "${W}Japanese_New_Year"
    private val F_HANAMI = "Hanami, mirar los cerezos" to "${W}Hanami"
    private val F_HINA = "Hinamatsuri, el día de las niñas" to "${W}Hinamatsuri"
    private val F_TANGO = "El día de los niños" to "${W}Children%27s_Day_(Japan)"
    private val F_RYOGOKU = "Los fuegos del río Sumida" to "${W}Sumidagawa_Fireworks_Festival"
    private val F_SANNO = "La fiesta de Sannō" to "${W}Sann%C5%8D_Matsuri"
    private val F_KANDA = "La fiesta de Kanda" to "${W}Kanda_Matsuri"
    private val F_TANABATA = "Tanabata" to "${W}Tanabata"
    private val F_BON = "El Bon" to "${W}Bon_Festival"
    private val F_TSUKIMI = "Tsukimi, mirar la luna" to "${W}Tsukimi"
    private val F_TAKADA = "El temblor de Takada" to "${W}1751_Takada_earthquake"
    private val F_YOSHIMUNE = "Tokugawa Yoshimune" to "${W}Tokugawa_Yoshimune"
    private val F_HOREKI = "La era Hōreki" to "${W}H%C5%8Dreki"

    // Las fechas del año lunar de Edo en 1750, 1751, 1752, 1753 y 1754 (ver `fuentes.md`, «Cómo se calcularon»).
    private val SUSUHARAI = listOf("01-20", "01-10", "01-29", "01-16", "01-06")    // 12/13 del año lunar anterior
    private val OMISOKA = listOf("02-06", "01-26", "02-14", "02-02", "01-22")
    private val SHOGATSU = listOf("02-07", "01-27", "02-15", "02-03", "01-23")     // 1/1
    private val SETSUBUN = listOf("02-03", "02-03", "02-03", null, "02-03")        // la víspera del inicio de la primavera
    private val TAISHI = listOf("02-28", "02-17", "03-07", "02-24", "02-13")       // 1/22
    private val HINA = listOf("04-09", "03-29", "04-16", "04-06", "03-26")         // 3/3
    private val TANGO = listOf("06-08", "05-29", "06-16", "06-06", "06-25")        // 5/5
    private val RYOGOKU = listOf("07-01", "06-21", "07-09", "06-29", "07-18")      // 5/28
    private val SANNO = listOf("07-18", null, "07-25", null, "08-03")              // 6/15, los años pares
    private val KANDA = listOf(null, "11-02", null, "10-11", null)                 // 9/15, los años nones
    private val TANABATA = listOf("08-08", "08-27", "08-16", "08-05", "08-24")     // 7/7
    private val BON = listOf("08-14", "09-02", "08-22", "08-11", "08-30")          // 7/13 a 7/16
    private val TSUKIMI = listOf("09-15", "10-03", "09-22", "09-12", "10-01")      // 8/15

    /** Las fiestas de Edo que vuelven cada año, en la temporada [t]. */
    private fun anioDeEdo(t: Int): List<DiaEspecial> = listOfNotNull(
        rito("susuharai", SUSUHARAI[t - 1], "Susuharai: la limpieza del hollín antes del año nuevo", certeza = APROXIMADO),
        SETSUBUN[t - 1]?.let { fiesta("setsubun", it, "Setsubun: echan frijoles a los demonios y llaman a la suerte", certeza = APROXIMADO, fuente = F_SETSUBUN) },
        fiesta("omisoka", OMISOKA[t - 1], "La última noche del año: fideos largos y las campanas de los templos", certeza = APROXIMADO, fuente = F_OMISOKA),
        fiesta("shogatsu", SHOGATSU[t - 1], "El año nuevo: pino en la puerta y tres días sin trabajo", 3, APROXIMADO, F_SHOGATSU, escena = "fiesta"),
        fiesta("taishi", TAISHI[t - 1], "Taishi-kō: los carpinteros honran al príncipe Shōtoku y no tocan la sierra", certeza = APROXIMADO),
        fiesta("hanami", "04-02", "Los cerezos de Ueno en flor: comidas bajo los árboles", 4, APROXIMADO, F_HANAMI),
        fiesta("hina", HINA[t - 1], "El día de las niñas: muñecas en escalones y sake blanco", certeza = APROXIMADO, fuente = F_HINA),
        fiesta("tango", TANGO[t - 1], "El día de los niños: carpas de tela en las varas y baño de lirios", certeza = APROXIMADO, fuente = F_TANGO),
        fiesta("ryogoku", RYOGOKU[t - 1], "Se abre el río en Ryōgoku: fuegos artificiales sobre el Sumida", certeza = APROXIMADO, fuente = F_RYOGOKU, escena = "fuegos"),
        SANNO[t - 1]?.let { fiesta("sanno", it, "La fiesta de Sannō: los carros del santuario entran al castillo", certeza = APROXIMADO, fuente = F_SANNO) },
        KANDA[t - 1]?.let { fiesta("kanda", it, "La fiesta de Kanda: los palanquines del santuario de su barrio", certeza = APROXIMADO, fuente = F_KANDA) },
        fiesta("tanabata", TANABATA[t - 1], "Tanabata: deseos en tiras de papel colgados del bambú", certeza = APROXIMADO, fuente = F_TANABATA),
        fiesta("bon", BON[t - 1], "El Bon: vuelven los muertos y se baila en la calle", 4, APROXIMADO, F_BON, escena = "faroles"),
        fiesta("tsukimi", TSUKIMI[t - 1], "La luna de otoño: bolas de arroz y hierbas de pampa en la ventana", certeza = APROXIMADO, fuente = F_TSUKIMI),
    )

    val KICHIZO = Calendario(
        vida = "edo",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 18, nubes = 7, lluvia = 2, nieve = 2, viento = 2), mes(despejado = 15, nubes = 8, lluvia = 3, nieve = 2, viento = 2),
            mes(despejado = 12, nubes = 9, lluvia = 7, viento = 3), mes(despejado = 11, nubes = 10, lluvia = 8, viento = 1),
            mes(despejado = 11, nubes = 10, lluvia = 9, niebla = 1), mes(despejado = 5, nubes = 11, lluvia = 14),
            mes(despejado = 11, nubes = 9, lluvia = 8, tormenta = 3), mes(despejado = 14, nubes = 8, lluvia = 5, tormenta = 4),
            mes(despejado = 8, nubes = 9, lluvia = 10, tormenta = 3, viento = 1), mes(despejado = 11, nubes = 10, lluvia = 9, viento = 1),
            mes(despejado = 15, nubes = 9, lluvia = 5, viento = 1), mes(despejado = 19, nubes = 7, lluvia = 2, nieve = 1, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1750", "paz",
                "Genbei le da a Kichizō la obra de un almacén para un mercader de arroz. Kinta empieza en la escuela del templo. Kichizō le arregla el techo a la viuda Otake. Ume espera otro hijo. Lee la historia de los cuarenta y siete rōnin. En diciembre arde media calle al otro lado del canal.",
                anioDeEdo(1) + listOf(
                    suceso("almacen", "03-12", "El maestro Genbei le da la obra de un almacén para un mercader de arroz"),
                    suceso("terakoya", "04-20", "Kinta empieza a ir a la escuela del templo"),
                    suceso("techo_otake", "05-15", "Le arregla la gotera a la viuda Otake antes de las lluvias"),
                    suceso("ume_espera", "10-08", "Ume le dice que espera otro hijo"),
                    suceso("libro_ronin", "11-12", "El que alquila libros le trae la historia de los cuarenta y siete rōnin"),
                    mundo("incendio_canal", "12-26", "Arde media calle al otro lado del canal: los carpinteros tiran casas para cortar el fuego", certeza = INVENTADO, escena = "humo", clima = Clima.VIENTO),
                )),
            TemporadaVida("1751", "luto",
                "Nace Okiku. Llega la noticia de un gran temblor en Echigo. Muere el antiguo shōgun Yoshimune y se calla la música en la ciudad. Kinta escribe su nombre. Sakubei baja de la aldea con rábanos. En diciembre la era cambia de nombre: ahora es Hōreki.",
                anioDeEdo(2) + listOf(
                    suceso("nace_okiku", "05-08", "Nace su hija Okiku"),
                    mundo("takada", "05-28", "Llega la noticia: un gran temblor tiró miles de casas en Echigo", certeza = APROXIMADO, fuente = F_TAKADA),
                    mundo("yoshimune", "07-12", "Muere el antiguo shōgun Yoshimune: se calla la música en la ciudad", certeza = HISTORICO, fuente = F_YOSHIMUNE, escena = "luto"),
                    suceso("kinta_nombre", "09-20", "Kinta escribe su nombre en silabario, sin ayuda"),
                    suceso("sakubei_baja", "11-20", "Su hermano Sakubei baja de la aldea con un costal de rábanos"),
                    mundo("horeki", "12-14", "Cambia el nombre de la era: desde hoy es Hōreki", certeza = APROXIMADO, fuente = F_HOREKI),
                )),
            TemporadaVida("1752", "kakke",
                "Okiku da sus primeros pasos. En el verano a Kichizō se le hinchan las piernas: es la enfermedad de Edo. Se va un mes a la aldea de su hermano, come cebada y vuelve sano. Kinta carga por primera vez la caja de herramientas. Pasa por la ciudad la embajada de Ryūkyū.",
                anioDeEdo(3) + listOf(
                    suceso("okiku_camina", "04-25", "Okiku da sus primeros pasos en el piso de tablas"),
                    suceso("piernas", "08-01", "Se le duermen y se le hinchan las piernas: la enfermedad de Edo", cadena = "kakke"),
                    suceso("va_aldea", "08-27", "Se va a la aldea de su hermano, a curarse lejos del arroz blanco", cadena = "kakke"),
                    suceso("vuelve_aldea", "09-19", "Vuelve de la aldea con las piernas sanas", cadena = "kakke"),
                    suceso("caja_kinta", "11-05", "Kinta carga por primera vez la caja de herramientas de su padre"),
                    mundo("ryukyu", "12-18", "Pasa por la ciudad la embajada del rey de Ryūkyū, con su música", certeza = APROXIMADO, fuente = F_HOREKI, escena = "fiesta"),
                )),
            TemporadaVida("1753", "fuego",
                "En marzo el fuego llega a su callejón y la casa de vecindad se quema entera. Duermen en el templo mientras los vecinos la levantan otra vez; Kichizō corta las vigas. Abre de nuevo el baño público. Entra de aprendiz Shinsuke. Okiku dice sus primeras palabras. Cae la primera nevada.",
                anioDeEdo(4) + listOf(
                    mundo("incendio_callejon", "03-14", "El fuego llega a su callejón: la casa de vecindad se quema entera", certeza = INVENTADO, escena = "humo", clima = Clima.VIENTO),
                    suceso("levantan_casa", "04-20", "Levantan otra vez la casa de vecindad: Kichizō corta las vigas", 30, cadena = "casa"),
                    mundo("sento_abre", "05-08", "Abre otra vez el baño público del barrio", certeza = INVENTADO),
                    suceso("vuelven_casa", "05-25", "Vuelven a dormir bajo su propio techo", cadena = "casa"),
                    suceso("shinsuke", "07-02", "Entra de aprendiz Shinsuke, un muchacho de doce años"),
                    suceso("okiku_habla", "11-10", "Okiku lo llama por su nombre de padre por primera vez"),
                    mundo("nevada", "12-20", "La primera nevada: Kichizō sacude la nieve de los techos que hizo", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                )),
            TemporadaVida("1754", "calendario",
                "Kinta empieza a barrer el taller de Genbei. Kichizō sube al monte Ōyama con los carpinteros del barrio. Ume abre un puesto de dulces de arroz. Shinsuke corta derecha su primera tabla. En diciembre, por orden del emperador, se corrige el calendario.",
                anioDeEdo(5) + listOf(
                    suceso("kinta_taller", "05-10", "Kinta empieza a barrer el taller del maestro Genbei"),
                    suceso("sale_oyama", "08-15", "Sale con los carpinteros del barrio en peregrinación al monte Ōyama", cadena = "oyama"),
                    suceso("vuelve_oyama", "08-19", "Vuelve de Ōyama con un sable de madera para el altar de la casa", cadena = "oyama"),
                    suceso("puesto_ume", "10-20", "Ume abre un puesto de dulces de arroz a la entrada del callejón"),
                    suceso("tabla_shinsuke", "11-12", "Shinsuke corta derecha su primera tabla"),
                    mundo("calendario", "12-02", "Llega el pregón: el calendario se corrige por orden del emperador", certeza = APROXIMADO, fuente = F_HOREKI),
                )),
        ),
        arcos = listOf(
            Arco("casa", TipoArco.DANOS, listOf(p(1, "01-01", 1.0), p(4, "03-14", 0.0, salto = true), p(4, "04-20", 0.1), p(4, "05-25", 1.0))),
        ),
        objetos = listOf("cepillo", "sierra", "formon", "entintador", "escuadra", "farol", "brasero", "sake", "libro", "toalla", "geta", "abanico"),
        estados = listOf(Estado("aldea", "va_aldea", "vuelve_aldea"), Estado("sin_casa", "incendio_callejon", "vuelven_casa"), Estado("oyama", "sale_oyama", "vuelve_oyama")),
    )

    // =====================================================================
    //  Mary, hiladora. Manchester, de 1840 a 1844.
    // =====================================================================
    //
    // Su gente (toda inventada): Thomas, su padre, tejedor de telar de mano sin trabajo desde que llegaron los telares
    // de vapor. Ann, su madre, que lava ropa ajena. Jane, su hermana (13 años en la temporada 1), que anuda hilos rotos
    // en la misma fábrica. Billy, su hermano (8), que va a la escuela dominical y entra a la fábrica en la 2. Jem, un
    // mecánico de un taller de máquinas, con quien se casa en la 3. Tom, el hijo que nace en la 4. Bridget, su amiga
    // irlandesa de la fábrica. El señor Holt, el capataz.
    //
    // Lo real: el correo de un penique, la boda de la reina, el pabellón de la Liga contra la ley del grano, el censo de
    // 1841, las elecciones, el nacimiento del príncipe, la petición de la Carta, la huelga general de 1842 (que llegó a
    // Manchester el 13 de agosto y se acabó el 26 de septiembre), el cometa de 1843, la estación Victoria, la ley de
    // fábricas de 1844 y la tienda de los pioneros de Rochdale. La Pascua de cada año está calculada; de ella cuelgan el
    // martes de carnaval, el Viernes Santo y la semana de Pentecostés, con su desfile de las escuelas dominicales y las
    // carreras de Kersal Moor.
    //
    // Claves de escena: fiesta, campanas, desfile, fogata, paro (las chimeneas sin humo), niebla, nieve. Estados: «paro»
    // (la huelga de la temporada 3: la fábrica parada) y «en_casa» (las cuatro semanas después de que nace Tom).

    private val F_CARNAVAL = "El martes de carnaval" to "${W}Shrove_Tuesday"
    private val F_VIERNES_SANTO = "El Viernes Santo" to "${W}Good_Friday"
    private val F_WHIT = "El lunes de Pentecostés" to "${W}Whit_Monday"
    private val F_KERSAL = "La semana de Pentecostés en Manchester" to "${W}Whit_Friday"
    private val F_GUY_FAWKES = "La noche de Guy Fawkes" to "${W}Guy_Fawkes_Night"
    private val F_NAVIDAD_EN = "La Navidad" to "${W}Christmas"
    private val F_PENIQUE = "El correo de un penique" to "${W}Uniform_Penny_Post"
    private val F_BODA_REINA = "La boda de la reina Victoria" to "${W}Wedding_of_Queen_Victoria_and_Prince_Albert"
    private val F_PABELLON = "El Free Trade Hall" to "${W}Free_Trade_Hall"
    private val F_CENSO_1841 = "El censo de 1841" to "${W}1841_United_Kingdom_census"
    private val F_ELECCION_1841 = "Las elecciones de 1841" to "${W}1841_United_Kingdom_general_election"
    private val F_PRINCIPE = "Eduardo VII" to "${W}Edward_VII"
    private val F_CARTA = "El cartismo" to "${W}Chartism"
    private val F_HUELGA_1842 = "La huelga general de 1842" to "${W}1842_General_Strike"
    private val F_COMETA_1843 = "El gran cometa de 1843" to "${W}Great_Comet_of_1843"
    private val F_VICTORIA = "La estación Victoria" to "${W}Manchester_Victoria_station"
    private val F_LEY_1844 = "La ley de fábricas de 1844" to "${W}Factories_Act_1844"
    private val F_ROCHDALE = "Los pioneros de Rochdale" to "${W}Rochdale_Society_of_Equitable_Pioneers"

    // Lo que cuelga de la Pascua gregoriana de 1840, 1841, 1842, 1843 y 1844.
    private val CARNAVAL_MARY = listOf("03-03", "02-23", "02-08", "02-28", "02-20")
    private val VIERNES_MARY = listOf("04-17", "04-09", "03-25", "04-14", "04-05")
    private val WHIT_MARY = listOf("06-08", "05-31", "05-16", "06-05", "05-27")     // el lunes de Pentecostés
    private val KERSAL = listOf("06-10", "06-02", "05-18", "06-07", "05-29")        // de miércoles a sábado de esa semana

    /** Lo que vuelve cada año en Manchester, en la temporada [t]. El año nuevo falta en la 5: ese día abre la estación. */
    private fun anioDeManchester(t: Int): List<DiaEspecial> = listOfNotNull(
        if (t == 5) null else fiesta("ano_nuevo", "01-01", "Año nuevo: los niños piden un penique de puerta en puerta", certeza = APROXIMADO),
        fiesta("carnaval", CARNAVAL_MARY[t - 1], "Martes de carnaval: tortitas en la sartén", certeza = HISTORICO, fuente = F_CARNAVAL),
        fiesta("viernes_santo", VIERNES_MARY[t - 1], "Viernes Santo: uno de los dos días del año en que la fábrica cierra", certeza = HISTORICO, fuente = F_VIERNES_SANTO),
        fiesta("whit", WHIT_MARY[t - 1], "Lunes de Pentecostés: las escuelas dominicales desfilan con sus estandartes", certeza = HISTORICO, fuente = F_WHIT, escena = "desfile"),
        fiesta("kersal", KERSAL[t - 1], "Las carreras de caballos de Kersal Moor", 4, HISTORICO, F_KERSAL),
        fiesta("guy_fawkes", "11-05", "La noche de Guy Fawkes: fogatas en los baldíos", certeza = HISTORICO, fuente = F_GUY_FAWKES, escena = "fogata"),
        fiesta("navidad", "12-25", "Navidad: el otro día del año en que la fábrica cierra", certeza = HISTORICO, fuente = F_NAVIDAD_EN, escena = "campanas"),
    )

    val MARY = Calendario(
        vida = "manchester",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 3, nubes = 10, lluvia = 9, nieve = 3, niebla = 5, viento = 2), mes(despejado = 4, nubes = 10, lluvia = 8, nieve = 3, niebla = 4, viento = 2),
            mes(despejado = 5, nubes = 11, lluvia = 9, nieve = 1, niebla = 3, viento = 3), mes(despejado = 7, nubes = 11, lluvia = 9, niebla = 2, viento = 2),
            mes(despejado = 9, nubes = 11, lluvia = 9, niebla = 1, viento = 1), mes(despejado = 9, nubes = 11, lluvia = 9, tormenta = 1),
            mes(despejado = 9, nubes = 10, lluvia = 10, tormenta = 2), mes(despejado = 8, nubes = 10, lluvia = 11, tormenta = 1),
            mes(despejado = 7, nubes = 11, lluvia = 10, niebla = 2), mes(despejado = 5, nubes = 10, lluvia = 11, niebla = 4, viento = 2),
            mes(despejado = 4, nubes = 10, lluvia = 11, niebla = 5, viento = 2), mes(despejado = 3, nubes = 10, lluvia = 10, nieve = 2, niebla = 5, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1840", "paz",
                "Una carta cuesta ya un penique. La reina se casa. Levantan un pabellón de madera para las reuniones contra la ley del grano. Mary conoce a Jem a la salida de la iglesia y estrena vestido en Pentecostés. El capataz le descuenta un día. Billy aprende las letras. Su padre vende el telar.",
                anioDeManchester(1) + listOf(
                    mundo("penique", "01-10", "El correo de un penique: una carta cuesta lo mismo a cualquier parte", certeza = APROXIMADO, fuente = F_PENIQUE),
                    mundo("pabellon", "01-13", "Gran banquete contra la ley del grano en un pabellón de madera", certeza = APROXIMADO, fuente = F_PABELLON),
                    mundo("boda_reina", "02-10", "La reina se casa con el príncipe Alberto: repican las iglesias", certeza = HISTORICO, fuente = F_BODA_REINA, escena = "campanas"),
                    suceso("conoce_jem", "04-25", "Conoce a Jem, un mecánico, a la salida de la iglesia"),
                    suceso("vestido", "05-20", "Con lo ahorrado se compra tela estampada para el vestido de Pentecostés"),
                    suceso("multa", "07-10", "El capataz Holt le descuenta un día por llegar tarde"),
                    suceso("billy_letras", "08-25", "Billy aprende las letras en la escuela dominical"),
                    suceso("telar_vendido", "10-15", "Su padre vende el telar de mano: ya nadie le compra tela"),
                    mundo("niebla_humo", "11-20", "Una niebla de humo tan espesa que no se ve la fábrica de enfrente", certeza = INVENTADO, escena = "niebla", clima = Clima.NIEBLA),
                )),
            TemporadaVida("1841", "hambre",
                "Su padre cae con la fiebre y se levanta. La noche del censo los apuntan a todos. Hay elecciones. No hay pedidos y la fábrica trabaja tres días por semana. Jem la lleva a Belle Vue. Billy entra a la fábrica. Nace el hijo de la reina. Jem le pide que se casen cuando haya trabajo.",
                anioDeManchester(2) + listOf(
                    suceso("fiebre_padre", "03-10", "Su padre cae con la fiebre de los cuartos húmedos", 10, cadena = "fiebre"),
                    suceso("sana_padre", "03-25", "Su padre se levanta de la cama", cadena = "fiebre"),
                    mundo("censo", "06-06", "La noche del censo: apuntan a todos los que duermen en la casa", certeza = HISTORICO, fuente = F_CENSO_1841),
                    mundo("elecciones", "07-01", "Elecciones: votan los que tienen casa propia; ella mira desde la calle", certeza = APROXIMADO, fuente = F_ELECCION_1841),
                    mundo("tres_dias", "08-02", "No hay pedidos: la fábrica solo trabaja tres días por semana", 30, INVENTADO),
                    suceso("belle_vue", "09-11", "Jem la lleva un sábado a los jardines de Belle Vue"),
                    suceso("billy_fabrica", "10-04", "Billy cumple nueve años y entra a la fábrica a barrer bajo las máquinas"),
                    mundo("principe", "11-10", "Llega la noticia: nació el hijo de la reina", certeza = APROXIMADO, fuente = F_PRINCIPE, escena = "campanas"),
                    suceso("jem_pide", "12-10", "Jem le pide que se casen en cuanto haya trabajo"),
                )),
            TemporadaVida("1842", "huelga",
                "Bridget le enseña una canción de Irlanda. El Parlamento rechaza la petición de la Carta. A Jane se le machuca un dedo. En agosto llegan los huelguistas y paran las fábricas: seis semanas sin jornal, sopa de la capilla y presos. Vuelven al trabajo a fines de septiembre. Mary se casa con Jem y se mudan a un cuarto.",
                anioDeManchester(3) + listOf(
                    suceso("cancion_bridget", "04-15", "Bridget, su amiga irlandesa, le enseña una canción de su tierra"),
                    mundo("peticion", "05-04", "Llega la noticia: el Parlamento rechaza la Carta y sus tres millones de firmas", certeza = APROXIMADO, fuente = F_CARTA),
                    suceso("dedo_jane", "07-05", "A Jane se le machuca un dedo en la máquina"),
                    mundo("huelga", "08-13", "Llegan los huelguistas y paran las fábricas: sacan los tapones de las calderas", certeza = HISTORICO, fuente = F_HUELGA_1842, escena = "paro"),
                    mundo("presos", "08-20", "Se llevan presos a los delegados de los oficios", certeza = APROXIMADO, fuente = F_HUELGA_1842, escena = "paro"),
                    suceso("sopa", "09-05", "Hacen fila para la sopa de la capilla"),
                    mundo("fin_huelga", "09-26", "Se acaba la huelga: vuelven a las fábricas con el jornal recortado", certeza = HISTORICO, fuente = F_HUELGA_1842),
                    suceso("boda_jem", "10-30", "Se casa con Jem en la iglesia de la parroquia", escena = "campanas"),
                    suceso("cuarto", "11-12", "Se mudan a un cuarto con ventana a la calle, encima de una tienda"),
                )),
            TemporadaVida("1843", "paz",
                "Billy le lee a su padre el periódico de los cartistas. Llega la noticia de un cometa enorme en los cielos del sur. Mary espera un hijo. Su padre carga bultos en la estación. Revienta una caldera en Ancoats. Nace Tom y Mary pasa cuatro semanas en casa. En diciembre, una niebla negra.",
                anioDeManchester(4) + listOf(
                    suceso("billy_lee", "01-20", "Billy le lee en voz alta a su padre el periódico de los cartistas"),
                    mundo("cometa", "03-20", "Llega la noticia: un cometa enorme en los cielos del sur", certeza = APROXIMADO, fuente = F_COMETA_1843),
                    suceso("espera_tom", "05-02", "Mary sabe que espera un hijo"),
                    suceso("padre_estacion", "07-20", "Su padre encuentra trabajo cargando bultos en la estación del tren"),
                    suceso("jane_baile", "08-19", "Jane va a su primer baile en la taberna de la esquina"),
                    mundo("caldera", "09-12", "Revienta la caldera de una fábrica en Ancoats", certeza = INVENTADO, escena = "humo"),
                    suceso("nace_tom", "10-20", "Nace su hijo Tom", cadena = "tom"),
                    suceso("vuelve_fabrica", "11-17", "Vuelve a la fábrica; su madre le cuida a Tom", cadena = "tom"),
                    mundo("niebla_negra", "12-05", "Una niebla negra: a mediodía hay que encender velas", certeza = INVENTADO, escena = "niebla", clima = Clima.NIEBLA),
                )),
            TemporadaVida("1844", "ley",
                "Abre la estación Victoria. Jem le enseña a leer algo más que su nombre. Se mudan a una casa de dos cuartos. Jane se casa con un tintorero. Bridget se va a Liverpool a buscar un barco a América. Llevan a Tom a ver los animales de Belle Vue. Llega la ley nueva: doce horas como máximo para las mujeres. Tom camina.",
                anioDeManchester(5) + listOf(
                    mundo("victoria", "01-01", "Abre la estación Victoria: los trenes de Leeds llegan al centro", certeza = HISTORICO, fuente = F_VICTORIA),
                    suceso("mary_lee", "02-26", "Jem le enseña a leer algo más que su nombre"),
                    suceso("jem_oficial", "03-30", "En el taller de máquinas hacen oficial a Jem"),
                    suceso("casa_dos", "04-20", "Se mudan a una casa de dos cuartos en una calle de Ancoats"),
                    suceso("boda_jane", "06-22", "Jane se casa con un tintorero", escena = "campanas"),
                    suceso("bridget_va", "07-30", "Bridget se va a Liverpool a buscar un barco para América"),
                    suceso("animales", "08-31", "Llevan a Tom a ver los animales de Belle Vue"),
                    mundo("ley_fabricas", "10-01", "Llega la ley nueva: las mujeres no trabajarán más de doce horas", certeza = APROXIMADO, fuente = F_LEY_1844),
                    suceso("tom_camina", "10-24", "Tom da sus primeros pasos en el cuarto"),
                    mundo("rochdale", "12-28", "Llega la noticia: en Rochdale unos tejedores abrieron su propia tienda", certeza = APROXIMADO, fuente = F_ROCHDALE),
                )),
        ),
        objetos = listOf("bobina", "lanzadera", "chal", "zuecos", "tetera", "pan", "vela", "carta", "penique", "estandarte", "periodico", "cuna"),
        estados = listOf(Estado("paro", "huelga", "fin_huelga"), Estado("en_casa", "nace_tom", "vuelve_fabrica")),
    )

    // =====================================================================
    //  José, peón acasillado. Una hacienda pulquera de los Llanos de Apan, en Hidalgo, de 1905 a 1909.
    // =====================================================================
    //
    // Su gente (toda inventada): Petra, su mujer, que echa tortillas y cría guajolotes. Chencho (Crescencio), su hijo
    // (8 años en la temporada 1), y Lupe, su hija (5). Rafael, el hijo que nace en la 2, y Cuca (Refugio), la hija que
    // nace en la 4. Doña Refugio, su madre, que muere en la 4. Nicolás, su compadre, tlachiquero como él, que huye de la
    // hacienda en la 5. Don Anselmo, el administrador. El patrón, que vive en la capital y solo viene a la fiesta del
    // santo. La hacienda (inventada) es de San Antonio.
    //
    // Lo real: las fiestas de la Iglesia y las patrias, la huelga de Cananea, el programa del Partido Liberal, la huelga
    // de Río Blanco, los temblores de 1907 y 1909, la entrevista de Díaz con Creelman, la gira de Madero y las
    // inundaciones de 1909. Que las noticias lleguen a la hacienda en esos días es inventado. La Pascua de cada año está
    // calculada; de ella cuelgan el miércoles de ceniza, el Viernes Santo y la quema de Judas.
    //
    // Claves de escena: fiesta, cohetes, procesion, altar (muertos), luto. Arco: milpa (el maíz del pegujal, el pedazo
    // de tierra que le prestan: se siembra en junio y se pizca en noviembre). Estado: «herido» (temporada 3: la mano
    // infectada por una púa de maguey).

    private val F_CANDELARIA_ES = "La Candelaria" to "${ES}Fiesta_de_la_Candelaria"
    private val F_CENIZA = "El miércoles de ceniza" to "${ES}Mi%C3%A9rcoles_de_Ceniza"
    private val F_VIERNES_ES = "El Viernes Santo" to "${ES}Viernes_Santo"
    private val F_JUDAS = "La quema de Judas" to "${ES}Quema_de_Judas"
    private val F_CRUZ = "El día de la Cruz" to "${ES}D%C3%ADa_de_la_Cruz"
    private val F_ISIDRO = "San Isidro Labrador" to "${ES}Isidro_Labrador"
    private val F_ANTONIO = "San Antonio de Padua" to "${ES}Antonio_de_Padua"
    private val F_GRITO = "El Grito de Dolores" to "${ES}Grito_de_Dolores"
    private val F_MUERTOS = "El Día de Muertos" to "${ES}D%C3%ADa_de_Muertos"
    private val F_GUADALUPE = "La Basílica de Guadalupe" to "${ES}Bas%C3%ADlica_de_Santa_Mar%C3%ADa_de_Guadalupe"
    private val F_POSADAS = "Las posadas" to "${ES}Las_Posadas"
    private val F_CANANEA = "La huelga de Cananea" to "${ES}Huelga_de_Cananea"
    private val F_PLM = "El programa del Partido Liberal Mexicano" to "${ES}Programa_del_Partido_Liberal_Mexicano"
    private val F_RIO_BLANCO = "La huelga de Río Blanco" to "${ES}Huelga_de_R%C3%ADo_Blanco"
    private val F_TEMBLOR_1907 = "El terremoto de Guerrero de 1907" to "${ES}Terremoto_de_Guerrero_de_1907"
    private val F_CREELMAN = "La entrevista Díaz-Creelman" to "${ES}Entrevista_D%C3%ADaz-Creelman"
    private val F_MADERO = "Francisco I. Madero" to "${ES}Francisco_I._Madero"
    private val F_1909 = "El año 1909" to "${ES}1909"

    // Lo que cuelga de la Pascua gregoriana de 1905, 1906, 1907, 1908 y 1909.
    private val CENIZA_JOSE = listOf("03-08", "02-28", "02-13", "03-04", "02-24")
    private val VIERNES_JOSE = listOf("04-21", "04-13", "03-29", "04-17", "04-09")
    private val GLORIA_JOSE = listOf("04-22", "04-14", "03-30", "04-18", "04-10")    // el sábado de gloria

    /** Las fiestas y las faenas de la hacienda que vuelven cada año, en la temporada [t]. */
    private fun anioDeLaHacienda(t: Int): List<DiaEspecial> = listOf(
        fiesta("candelaria", "02-02", "La Candelaria: se bendicen las semillas y las velas", certeza = HISTORICO, fuente = F_CANDELARIA_ES),
        rito("ceniza", CENIZA_JOSE[t - 1], "Miércoles de ceniza: la cruz en la frente y empieza la cuaresma", certeza = HISTORICO, fuente = F_CENIZA),
        rito("viernes_santo", VIERNES_JOSE[t - 1], "Viernes Santo: procesión del Santo Entierro y nada de trabajo", certeza = HISTORICO, fuente = F_VIERNES_ES, escena = "procesion"),
        fiesta("judas", GLORIA_JOSE[t - 1], "Sábado de gloria: queman un Judas de papel lleno de cohetes", certeza = HISTORICO, fuente = F_JUDAS, escena = "cohetes"),
        fiesta("santa_cruz", "05-03", "El día de la Santa Cruz: cruces con flores en el tinacal y en los pozos", certeza = APROXIMADO, fuente = F_CRUZ),
        fiesta("san_isidro", "05-15", "San Isidro Labrador: bendicen a los bueyes con flores en los cuernos", certeza = HISTORICO, fuente = F_ISIDRO),
        cosecha("siembra", "06-01", "La siembra del maíz en el pegujal, con las primeras lluvias", 10, APROXIMADO),
        fiesta("san_antonio", "06-13", "La fiesta del santo de la hacienda: misa, cohetes y jaripeo", certeza = APROXIMADO, fuente = F_ANTONIO, escena = "cohetes"),
        cosecha("escarda", "07-20", "La escarda: le arriman tierra a la milpa", 8, APROXIMADO),
        fiesta("patrias", "09-15", "Las fiestas patrias: el grito en el casco de la hacienda", 2, HISTORICO, F_GRITO, escena = "fiesta"),
        fiesta("muertos", "11-01", "Todos Santos y los fieles difuntos: el altar con flor de cempasúchil", 2, HISTORICO, F_MUERTOS, escena = "altar"),
        cosecha("pizca", "11-10", "La pizca del maíz del pegujal", 20, APROXIMADO),
        fiesta("guadalupe", "12-12", "El día de la Virgen de Guadalupe: mañanitas y cohetes al amanecer", certeza = HISTORICO, fuente = F_GUADALUPE, escena = "cohetes"),
        fiesta("posadas", "12-16", "Las posadas: nueve noches de pedir posada y romper piñatas", 9, HISTORICO, F_POSADAS, escena = "fiesta"),
    )

    val JOSE = Calendario(
        vida = "hacienda",
        estaciones = listOf("05-25" to Estacion.LLUVIAS, "10-15" to Estacion.SECAS),
        clima = listOf(
            mes(despejado = 22, nubes = 5, viento = 2, niebla = 1), mes(despejado = 19, nubes = 5, viento = 3, polvo = 2),
            mes(despejado = 18, nubes = 6, viento = 4, polvo = 3), mes(despejado = 15, nubes = 8, lluvia = 2, viento = 3, polvo = 2),
            mes(despejado = 11, nubes = 10, lluvia = 5, tormenta = 3, viento = 1), mes(despejado = 6, nubes = 9, lluvia = 10, tormenta = 5),
            mes(despejado = 5, nubes = 10, lluvia = 11, tormenta = 5), mes(despejado = 5, nubes = 10, lluvia = 11, tormenta = 4, niebla = 1),
            mes(despejado = 6, nubes = 10, lluvia = 10, tormenta = 3, niebla = 1), mes(despejado = 13, nubes = 9, lluvia = 5, tormenta = 1, niebla = 2),
            mes(despejado = 19, nubes = 7, lluvia = 1, niebla = 2, viento = 1), mes(despejado = 22, nubes = 5, niebla = 2, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1905", "porfiriato",
                "En la tienda de raya le apuntan otra deuda. El patrón llega de la capital para la fiesta del santo. Chencho lo acompaña por primera vez a los magueyes. Una granizada pela las pencas. Petra espera otro hijo.",
                anioDeLaHacienda(1) + listOf(
                    suceso("deuda", "03-20", "En la tienda de raya le apuntan otra deuda: manta y unos huaraches"),
                    suceso("patron_llega", "06-12", "Llega el patrón de la capital en el tren, para la fiesta del santo"),
                    suceso("chencho_magueyes", "08-10", "Chencho lo acompaña por primera vez a los magueyes, con un acocote chico"),
                    mundo("granizada", "08-28", "Una granizada pela las pencas de los magueyes", certeza = INVENTADO, clima = Clima.TORMENTA),
                    suceso("petra_espera", "10-05", "Petra le dice que espera otro hijo"),
                )),
            TemporadaVida("1906", "huelgas",
                "Nace Rafael y lo bautizan, con Nicolás de padrino. Llega la noticia de la huelga de Cananea. Corre a escondidas un papel de los liberales que pide ocho horas y el fin de las deudas. La abuela le enseña a Lupe a echar tortillas.",
                anioDeLaHacienda(2) + listOf(
                    suceso("nace_rafael", "05-25", "Nace su hijo Rafael", cadena = "rafael"),
                    mundo("cananea", "06-08", "Llega la noticia: los mineros de Cananea se fueron a la huelga y hubo muertos", certeza = APROXIMADO, fuente = F_CANANEA),
                    suceso("bautizo_rafael", "06-24", "Bautizan a Rafael; su compadre Nicolás es el padrino", cadena = "rafael"),
                    mundo("papel_liberal", "08-12", "Corre a escondidas un papel de los liberales: ocho horas de trabajo y fin de las deudas", certeza = APROXIMADO, fuente = F_PLM),
                    suceso("lupe_tortillas", "10-12", "Su madre le enseña a Lupe a echar tortillas"),
                )),
            TemporadaVida("1907", "temblor",
                "Llega la noticia de Río Blanco. Una noche tiembla la tierra. Rafael camina. En julio se le entierra una púa de maguey y la mano se le infecta: dos semanas sin raspar y sin raya. Chencho empieza a arrear las mulas del aguamiel.",
                anioDeLaHacienda(3) + listOf(
                    mundo("rio_blanco", "01-14", "Llega la noticia: en Río Blanco los soldados dispararon contra los obreros", certeza = APROXIMADO, fuente = F_RIO_BLANCO),
                    mundo("temblor", "04-14", "De noche tiembla la tierra: el temblor de Guerrero se siente hasta aquí", certeza = HISTORICO, fuente = F_TEMBLOR_1907),
                    suceso("rafael_camina", "05-28", "Rafael da sus primeros pasos en el patio"),
                    suceso("pua", "07-02", "Se le entierra una púa de maguey en la mano y se le infecta", cadena = "mano"),
                    suceso("vuelve_raspar", "07-16", "Vuelve a raspar, con la mano vendada", cadena = "mano"),
                    suceso("chencho_mulas", "10-08", "Chencho empieza a arrear las mulas que llevan el aguamiel al tinacal"),
                )),
            TemporadaVida("1908", "entrevista",
                "Muere doña Refugio, la madre de José. En la tienda leen el periódico: don Porfirio dice que ya no se reelige. Petra espera otra vez. A Nicolás le niegan el fiado. En diciembre nace Cuca, con el nombre de la abuela.",
                anioDeLaHacienda(4) + listOf(
                    suceso("enferma_refugio", "01-10", "Su madre cae con una tos que no se le quita", 7, cadena = "refugio"),
                    suceso("muere_refugio", "01-20", "Muere doña Refugio, su madre", cadena = "refugio", escena = "luto"),
                    mundo("creelman", "03-12", "En la tienda leen el periódico en voz alta: don Porfirio dice que ya no se reelige", certeza = APROXIMADO, fuente = F_CREELMAN),
                    suceso("petra_otra_vez", "03-28", "Petra espera otra vez"),
                    suceso("fiado", "08-15", "A su compadre Nicolás le niegan el fiado en la tienda de raya"),
                    suceso("nace_cuca", "12-01", "Nace su hija Refugio, con el nombre de la abuela"),
                )),
            TemporadaVida("1909", "antirreeleccion",
                "Nicolás huye de noche de la hacienda. Chencho raspa solo su primer maguey. Llega la noticia de un tal Madero que habla contra la reelección. Tiembla otra vez. Las inundaciones del norte matan a más de mil. Petra vende sus guajolotes en Apan.",
                anioDeLaHacienda(5) + listOf(
                    suceso("huye_nicolas", "03-25", "Su compadre Nicolás huye de noche de la hacienda, debiendo en la tienda"),
                    suceso("chencho_solo", "05-20", "Chencho, de doce años, raspa solo su primer maguey"),
                    mundo("madero", "06-20", "Llega la noticia: un tal Madero recorre el país hablando contra la reelección", certeza = APROXIMADO, fuente = F_MADERO),
                    mundo("temblor_acapulco", "07-30", "Tiembla otra vez: el temblor que tumbó Acapulco se siente hasta aquí", certeza = HISTORICO, fuente = F_1909),
                    mundo("inundaciones", "09-05", "Llega la noticia: las inundaciones del norte mataron a más de mil personas", certeza = APROXIMADO, fuente = F_1909),
                    suceso("guajolotes", "10-10", "Petra vende sus guajolotes en el tianguis de Apan"),
                )),
        ),
        arcos = listOf(
            Arco.cadaAnio("milpa", TipoArco.CULTIVO, "06-01" to 0.0, "06-10" to 0.05, "08-20" to 0.75, "10-20" to 1.0, "11-10" to 1.0, "11-30" to 0.0),
        ),
        objetos = listOf("acocote", "raspador", "odre", "jicara", "sombrero", "sarape", "huaraches", "metate", "comal", "vale", "rosario", "pulque"),
        estados = listOf(Estado("herido", "pua", "vuelve_raspar")),
    )

    // =====================================================================
    //  Rosa, secretaria. Ciudad de México, de 1960 a 1964.
    // =====================================================================
    //
    // Su gente (toda inventada): doña Carmen, su mamá, viuda, que cose ajeno. Beto, su hermano (17 años en la temporada
    // 1), que quiere ser futbolista, y Chela, su hermana (14). Arturo, su novio, dibujante en un despacho de ingenieros,
    // con quien se casa en la 2. Claudia, la hija que nace en la 3. Lupita, la secretaria del escritorio de al lado. El
    // licenciado Ortega, su jefe. Viven en una vecindad de la colonia Guerrero; casada, Rosa se muda a Santa María la
    // Ribera.
    //
    // Lo real: las fiestas de la ciudad (los Reyes, la Candelaria, el natalicio de Juárez, el Día de las Madres, el
    // Grito, los muertos, el desfile de la Revolución, la Virgen de Guadalupe y las posadas), la nacionalización de la
    // luz, bahía de Cochinos, el triunfo de México en el Mundial de Chile, la visita de Kennedy, la crisis de los
    // misiles, la elección de México para los Juegos Olímpicos, la muerte de Kennedy, las elecciones de 1964, el Museo
    // de Antropología, los Juegos de Tokio, la unidad de Tlatelolco y el nuevo presidente. La Pascua de cada año está
    // calculada.
    //
    // Claves de escena: fiesta, adornos, desfile, altar, aguacero. Estados: «acapulco» (temporada 2: la luna de miel) e
    // «incapacidad» (temporada 3: las semanas en casa con Claudia recién nacida).

    private val F_REYES = "El Día de Reyes" to "${ES}D%C3%ADa_de_Reyes"
    private val F_JUAREZ = "Benito Juárez" to "${ES}Benito_Ju%C3%A1rez"
    private val F_MADRE = "El Día de la Madre" to "${ES}D%C3%ADa_de_la_Madre"
    private val F_REVOLUCION = "La Revolución mexicana" to "${ES}Revoluci%C3%B3n_mexicana"
    private val F_LOPEZ_MATEOS = "Adolfo López Mateos" to "${ES}Adolfo_L%C3%B3pez_Mateos"
    private val F_COCHINOS = "La invasión de bahía de Cochinos" to "${ES}Invasi%C3%B3n_de_bah%C3%ADa_de_Cochinos"
    private val F_MUNDIAL_62 = "La Copa Mundial de 1962" to "${ES}Copa_Mundial_de_F%C3%BAtbol_de_1962"
    private val F_MISILES = "La crisis de los misiles" to "${ES}Crisis_de_los_misiles_en_Cuba"
    private val F_MEXICO_68 = "Los Juegos Olímpicos de México" to "${ES}Juegos_Ol%C3%ADmpicos_de_M%C3%A9xico_1968"
    private val F_KENNEDY = "El asesinato de Kennedy" to "${ES}Asesinato_de_John_F._Kennedy"
    private val F_ELECCIONES_64 = "Las elecciones de 1964" to "${ES}Elecciones_federales_de_M%C3%A9xico_de_1964"
    private val F_MUSEO = "El Museo Nacional de Antropología" to "${ES}Museo_Nacional_de_Antropolog%C3%ADa_(M%C3%A9xico)"
    private val F_TOKIO = "Los Juegos Olímpicos de Tokio" to "${ES}Juegos_Ol%C3%ADmpicos_de_Tokio_1964"
    private val F_TLATELOLCO = "La unidad Nonoalco-Tlatelolco" to "${ES}Conjunto_Urbano_Nonoalco_Tlatelolco"
    private val F_DIAZ_ORDAZ = "Gustavo Díaz Ordaz" to "${ES}Gustavo_D%C3%ADaz_Ordaz"

    private val VIERNES_ROSA = listOf("04-15", "03-31", "04-20", "04-12", "03-27")   // de la Pascua de 1960 a 1964

    /** Lo que vuelve cada año en la ciudad, en la temporada [t]. */
    private fun anioDeLaCiudad(t: Int): List<DiaEspecial> = listOf(
        fiesta("reyes", "01-06", "Día de Reyes: la rosca, y al que le sale el muñeco le tocan los tamales", certeza = HISTORICO, fuente = F_REYES),
        fiesta("candelaria", "02-02", "La Candelaria: tamales y atole en casa de su mamá", certeza = HISTORICO, fuente = F_CANDELARIA_ES),
        fiesta("juarez", "03-21", "El natalicio de Juárez: no hay oficina", certeza = HISTORICO, fuente = F_JUAREZ),
        rito("viernes_santo", VIERNES_ROSA[t - 1], "Viernes Santo: cierran las oficinas y la ciudad se queda callada", certeza = HISTORICO, fuente = F_VIERNES_ES),
        fiesta("madres", "05-10", "El Día de las Madres: serenata y comida para doña Carmen", certeza = HISTORICO, fuente = F_MADRE),
        mundo("aguaceros", "06-15", "Empiezan los aguaceros de la tarde: se inundan las calles del centro", certeza = APROXIMADO, escena = "aguacero", clima = Clima.TORMENTA),
        fiesta("patrias", "09-15", "Las fiestas patrias: el Grito en el Zócalo y el desfile", 2, HISTORICO, F_GRITO, escena = "adornos"),
        fiesta("muertos", "11-01", "Todos Santos y los fieles difuntos: el altar con fotos y cempasúchil", 2, HISTORICO, F_MUERTOS, escena = "altar"),
        fiesta("revolucion", "11-20", "El aniversario de la Revolución: el desfile deportivo por el centro", certeza = HISTORICO, fuente = F_REVOLUCION, escena = "desfile"),
        fiesta("guadalupe", "12-12", "El día de la Virgen de Guadalupe: los peregrinos llenan la calzada", certeza = HISTORICO, fuente = F_GUADALUPE),
        fiesta("posadas", "12-16", "Las posadas: piñatas en el patio de la vecindad y la de la oficina", 9, HISTORICO, F_POSADAS, escena = "adornos"),
    )

    val ROSA = Calendario(
        vida = "cdmx1960",
        estaciones = listOf("05-20" to Estacion.LLUVIAS, "10-20" to Estacion.SECAS),
        clima = listOf(
            mes(despejado = 21, nubes = 6, niebla = 2, polvo = 2), mes(despejado = 19, nubes = 6, viento = 1, polvo = 2),
            mes(despejado = 18, nubes = 8, lluvia = 1, viento = 2, polvo = 2), mes(despejado = 15, nubes = 10, lluvia = 3, tormenta = 1, polvo = 1),
            mes(despejado = 12, nubes = 11, lluvia = 5, tormenta = 3), mes(despejado = 6, nubes = 10, lluvia = 9, tormenta = 5),
            mes(despejado = 5, nubes = 10, lluvia = 11, tormenta = 5), mes(despejado = 5, nubes = 10, lluvia = 11, tormenta = 5),
            mes(despejado = 6, nubes = 10, lluvia = 10, tormenta = 4), mes(despejado = 12, nubes = 10, lluvia = 6, tormenta = 2, niebla = 1),
            mes(despejado = 18, nubes = 8, lluvia = 2, niebla = 2), mes(despejado = 21, nubes = 6, niebla = 3, polvo = 1),
        ),
        temporadas = listOf(
            TemporadaVida("1960", "sesquicentenario",
                "Chela entra a la secundaria. En la oficina le dan a Rosa una máquina de escribir eléctrica. Arturo le pide matrimonio en la nevería. Beto va a una prueba con un equipo de futbol. El gobierno compra las compañías de luz, y en septiembre se cumplen ciento cincuenta años del Grito.",
                anioDeLaCiudad(1) + listOf(
                    suceso("chela_secundaria", "02-08", "Chela entra a la secundaria"),
                    suceso("maquina", "04-25", "En la oficina le dan una máquina de escribir eléctrica"),
                    suceso("arturo_pide", "07-23", "Arturo le pide matrimonio en la nevería"),
                    suceso("beto_prueba", "08-20", "Beto va a una prueba con un equipo de futbol"),
                    mundo("luz", "09-27", "El gobierno compra las compañías de luz: la electricidad es de la nación", certeza = HISTORICO, fuente = F_LOPEZ_MATEOS),
                )),
            TemporadaVida("1961", "boda",
                "Llega la noticia de la invasión de Cuba. Rosa se casa con Arturo en junio y se van de luna de miel a Acapulco en autobús. Se mudan a un departamento de dos piezas en Santa María la Ribera. Beto entra de ayudante a un taller mecánico. En octubre Rosa sabe que espera un hijo.",
                anioDeLaCiudad(2) + listOf(
                    mundo("cochinos", "04-18", "Llega la noticia: desembarcan en Cuba por bahía de Cochinos", certeza = APROXIMADO, fuente = F_COCHINOS),
                    suceso("boda", "06-17", "Se casa con Arturo en la parroquia del barrio", cadena = "boda", escena = "fiesta"),
                    suceso("va_acapulco", "06-18", "Se van de luna de miel a Acapulco, en autobús", 5, cadena = "boda"),
                    suceso("vuelve_acapulco", "06-24", "Vuelven de Acapulco y se mudan a su departamento de dos piezas", cadena = "boda"),
                    suceso("beto_taller", "08-21", "Beto entra de ayudante a un taller mecánico"),
                    suceso("espera_rosa", "10-10", "Rosa sabe que espera un hijo"),
                )),
            TemporadaVida("1962", "claudia",
                "Chela cumple quince años con vals en el patio. Nace Claudia y Rosa pasa ocho semanas en casa: oye en la radio que México le gana a Checoslovaquia en el Mundial y ve pasar a Kennedy por la televisión. Vuelve a la oficina en julio. En octubre, la crisis de los misiles.",
                anioDeLaCiudad(3) + listOf(
                    suceso("quince_chela", "03-03", "Los quince años de Chela, con vals en el patio de la vecindad", escena = "fiesta"),
                    suceso("nace_claudia", "06-02", "Nace su hija Claudia", cadena = "claudia"),
                    mundo("mundial", "06-07", "México le gana a Checoslovaquia: su primer triunfo en un Mundial", certeza = HISTORICO, fuente = F_MUNDIAL_62),
                    mundo("kennedy_visita", "06-29", "Kennedy visita la ciudad: miles de personas en el Paseo de la Reforma", certeza = APROXIMADO),
                    suceso("vuelve_oficina", "07-28", "Vuelve a la oficina; su mamá le cuida a Claudia", cadena = "claudia"),
                    mundo("misiles", "10-22", "Kennedy anuncia el bloqueo de Cuba: en la oficina se oye la radio todo el día", certeza = HISTORICO, fuente = F_MISILES),
                )),
            TemporadaVida("1963", "olimpiada",
                "A Arturo lo hacen jefe de dibujantes. Claudia da sus primeros pasos. Beto lleva a su novia a comer con su mamá. México será la sede de los Juegos Olímpicos. En noviembre matan a Kennedy y en la oficina lloran junto a la radio.",
                anioDeLaCiudad(4) + listOf(
                    suceso("arturo_jefe", "03-08", "A Arturo lo hacen jefe de dibujantes en el despacho"),
                    suceso("claudia_camina", "05-25", "Claudia da sus primeros pasos en el pasillo"),
                    suceso("novia_beto", "08-04", "Beto lleva a su novia a comer a casa de su mamá"),
                    mundo("sede_olimpica", "10-18", "México será la sede de los Juegos Olímpicos", certeza = HISTORICO, fuente = F_MEXICO_68),
                    mundo("matan_kennedy", "11-22", "Matan a Kennedy en Dallas: en la oficina lloran junto a la radio", certeza = HISTORICO, fuente = F_KENNEDY),
                )),
            TemporadaVida("1964", "tlatelolco",
                "Claudia dice sus primeras frases. Arturo mete la solicitud para un departamento en la unidad nueva de Tlatelolco. Rosa vota para presidente. Rosa espera otro hijo. Abre el Museo de Antropología; en la televisión de la vecina ven los Juegos de Tokio. Inauguran Tlatelolco y toma posesión el nuevo presidente.",
                anioDeLaCiudad(5) + listOf(
                    suceso("claudia_habla", "03-02", "Claudia dice sus primeras frases"),
                    suceso("solicitud", "04-27", "Arturo mete la solicitud para un departamento en la unidad nueva de Tlatelolco"),
                    mundo("elecciones", "07-05", "Elecciones: Rosa vota para presidente", certeza = HISTORICO, fuente = F_ELECCIONES_64),
                    suceso("espera_otro", "08-10", "Rosa sabe que espera otro hijo"),
                    mundo("museo", "09-17", "Abre el Museo Nacional de Antropología en Chapultepec", certeza = HISTORICO, fuente = F_MUSEO),
                    mundo("tokio", "10-10", "En la televisión de la vecina ven la inauguración de los Juegos de Tokio", certeza = HISTORICO, fuente = F_TOKIO),
                    mundo("tlatelolco", "11-21", "Inauguran la unidad Nonoalco-Tlatelolco", certeza = HISTORICO, fuente = F_TLATELOLCO),
                    mundo("presidente", "12-01", "Toma posesión el nuevo presidente", certeza = HISTORICO, fuente = F_DIAZ_ORDAZ),
                )),
        ),
        objetos = listOf("maquina_escribir", "radio", "fotonovela", "boleto_tranvia", "bolsa", "lapiz_labial", "taza_cafe", "pan_dulce", "refresco", "rosario", "television", "calendario"),
        estados = listOf(Estado("acapulco", "va_acapulco", "vuelve_acapulco"), Estado("incapacidad", "nace_claudia", "vuelve_oficina")),
    )

    // =====================================================================
    //  Alguien del mundo, una persona promedio. Una ciudad del norte de la India, de 2025 a 2029.
    // =====================================================================
    //
    // Dónde vive: su ficha dice «una ciudad de Asia, probablemente». Para que su año tenga fiestas y noticias, aquí vive
    // en la India, el país más poblado del mundo, donde la familia promedio tiene unos dos hijos, como la persona de su
    // ficha. La ciudad no tiene nombre: una ciudad mediana del norte, con calor seco en mayo, monzón en verano y niebla
    // en invierno. Su familia es de una aldea de Bihar, a una noche de tren.
    //
    // Su gente (toda inventada): su pareja (su nombre y su género los decide el cuento). Anu, su hija (4 años en la
    // temporada 1). Kabir, el hijo que nace en la 3. Su madre y su padre, que siguen en la aldea. Trabaja en la oficina
    // de un almacén de envíos de compras por internet.
    //
    // Lo real: las fiestas del año (Makar Sankranti, el Día de la República, Holi, Raksha Bandhan, el Día de la
    // Independencia, Dussehra, Diwali y Chhath), el monzón, el Kumbh Mela, los títulos de críquet de 2025 y 2026, los
    // cuatro días de guerra con Pakistán, el avión de Ahmedabad, las dos fases del censo y, ya en el futuro, los Juegos
    // de Los Ángeles, el paso del asteroide Apophis y las elecciones generales. Las fiestas que siguen la luna se
    // calcularon (ver `fuentes.md`) y se compararon con las fechas que da Wikipedia.
    //
    // Claves de escena: fiesta, colores (Holi), lamparas (Diwali), cometas, aguacero, niebla, calor. Estado: «aldea»
    // (temporada 2: va a cuidar a su madre).

    private val F_SANKRANTI = "Makar Sankranti" to "${W}Makar_Sankranti"
    private val F_REPUBLICA = "El Día de la República" to "${W}Republic_Day_(India)"
    private val F_HOLI = "Holi" to "${W}Holi"
    private val F_MONZON = "El monzón del sur de Asia" to "${W}Monsoon_of_South_Asia"
    private val F_INDEPENDENCIA_IN = "El Día de la Independencia" to "${W}Independence_Day_(India)"
    private val F_RAKSHA = "Raksha Bandhan" to "${W}Raksha_Bandhan"
    private val F_DUSSEHRA = "Dussehra" to "${W}Vijayadashami"
    private val F_DIWALI = "Diwali" to "${W}Diwali"
    private val F_CHHATH = "Chhath" to "${W}Chhath"
    private val F_KUMBH = "El Kumbh Mela de 2025" to "${W}2025_Prayag_Maha_Kumbh_Mela"
    private val F_TROFEO = "La Champions Trophy de 2025" to "${W}2025_ICC_Champions_Trophy"
    private val F_SINDOOR = "La operación Sindoor" to "${W}Operation_Sindoor"
    private val F_AHMEDABAD = "El vuelo 171 de Air India" to "${W}Air_India_Flight_171"
    private val F_MUNDIAL_FEM = "El Mundial femenino de críquet de 2025" to "${W}2025_Women%27s_Cricket_World_Cup"
    private val F_T20 = "El Mundial T20 de 2026" to "${W}2026_Men%27s_T20_World_Cup"
    private val F_CENSO_IN = "El censo de la India de 2027" to "${W}2027_census_of_India"
    private val F_LA28 = "Los Juegos Olímpicos de Los Ángeles" to "${W}2028_Summer_Olympics"
    private val F_APOPHIS = "El asteroide Apophis" to "${W}99942_Apophis"
    private val F_ELECCIONES_IN = "Las elecciones generales de 2029" to "${W}2029_Indian_general_election"

    // Las fiestas que siguen la luna, de 2025 a 2029 (ver `fuentes.md`).
    private val SANKRANTI = listOf("01-14", "01-14", "01-15", "01-15", "01-14")
    private val HOLI = listOf("03-14", "03-04", "03-22", "03-11", "03-01")          // el día de los colores
    private val RAKSHA = listOf("08-09", "08-28", "08-17", "08-05", "08-24")
    private val DUSSEHRA = listOf("10-02", "10-20", "10-09", "09-27", "10-16")
    private val DIWALI = listOf("10-20", "11-08", "10-29", "10-17", "11-05")
    private val CHHATH = listOf("10-25", "11-13", "11-02", "10-21", "11-09")         // los cuatro días

    /** Lo que vuelve cada año, en la temporada [t]. */
    private fun anioDeLaIndia(t: Int): List<DiaEspecial> = listOf(
        fiesta("sankranti", SANKRANTI[t - 1], "Makar Sankranti: cometas en las azoteas y dulces de ajonjolí", certeza = HISTORICO, fuente = F_SANKRANTI, escena = "cometas"),
        fiesta("republica", "01-26", "El Día de la República: el desfile de Delhi en la televisión", certeza = HISTORICO, fuente = F_REPUBLICA),
        fiesta("holi", HOLI[t - 1], "Holi: polvos de colores y agua en la calle", certeza = APROXIMADO, fuente = F_HOLI, escena = "colores"),
        mundo("calor", "05-01", "Llega el calor seco: el viento caliente de la tarde cierra las ventanas", certeza = APROXIMADO, escena = "calor"),
        mundo("monzon", "06-25", "Llega el monzón: el primer aguacero lava la ciudad", certeza = APROXIMADO, fuente = F_MONZON, escena = "aguacero", clima = Clima.LLUVIA),
        fiesta("raksha", RAKSHA[t - 1], "Raksha Bandhan: las hermanas atan un hilo en la muñeca de los hermanos", certeza = APROXIMADO, fuente = F_RAKSHA),
        fiesta("independencia", "08-15", "El Día de la Independencia: banderas y cometas", certeza = HISTORICO, fuente = F_INDEPENDENCIA_IN, escena = "cometas"),
        fiesta("dussehra", DUSSEHRA[t - 1], "Dussehra: queman en el parque un demonio de papel lleno de cohetes", certeza = APROXIMADO, fuente = F_DUSSEHRA),
        fiesta("diwali", DIWALI[t - 1], "Diwali: lámparas de barro en la ventana y cohetes toda la noche", certeza = APROXIMADO, fuente = F_DIWALI, escena = "lamparas"),
        fiesta("chhath", CHHATH[t - 1], "Chhath: las ofrendas al sol en la orilla del río, como en la aldea", 4, APROXIMADO, F_CHHATH),
        mundo("niebla", "12-22", "La niebla del invierno: los trenes llegan con horas de retraso", certeza = APROXIMADO, escena = "niebla", clima = Clima.NIEBLA),
    )

    val MUNDO = Calendario(
        vida = "mundo2025",
        estaciones = listOf("03-01" to Estacion.VERANO, "06-25" to Estacion.LLUVIAS, "10-01" to Estacion.OTONO, "12-01" to Estacion.INVIERNO),
        clima = listOf(
            mes(despejado = 12, nubes = 6, niebla = 12, lluvia = 1), mes(despejado = 17, nubes = 6, niebla = 4, lluvia = 1),
            mes(despejado = 22, nubes = 5, lluvia = 1, viento = 2), mes(despejado = 22, nubes = 3, viento = 3, polvo = 3),
            mes(despejado = 19, nubes = 3, tormenta = 2, viento = 3, polvo = 4), mes(despejado = 11, nubes = 7, lluvia = 5, tormenta = 4, polvo = 3),
            mes(despejado = 3, nubes = 10, lluvia = 13, tormenta = 5), mes(despejado = 3, nubes = 11, lluvia = 13, tormenta = 4),
            mes(despejado = 8, nubes = 10, lluvia = 9, tormenta = 3), mes(despejado = 22, nubes = 6, lluvia = 1, niebla = 2),
            mes(despejado = 18, nubes = 4, niebla = 8), mes(despejado = 12, nubes = 6, niebla = 12),
        ),
        temporadas = listOf(
            TemporadaVida("2025", "kumbh",
                "Empieza el Kumbh Mela y sus padres van a bañarse al río. La India gana un trofeo de críquet. Anu entra al kínder. Cuatro días de guerra con Pakistán en el teléfono. Se cae un avión en Ahmedabad. Le suben el sueldo. Compra un teléfono nuevo en abonos. La India gana el Mundial femenino de críquet.",
                anioDeLaIndia(1) + listOf(
                    mundo("kumbh", "01-13", "Empieza el Kumbh Mela: millones van a bañarse a Prayagraj", certeza = HISTORICO, fuente = F_KUMBH),
                    suceso("padres_kumbh", "02-10", "Sus padres van al Kumbh y le traen agua del río en una botella"),
                    mundo("trofeo", "03-09", "La India gana la Champions Trophy de críquet: cohetes en la calle", certeza = HISTORICO, fuente = F_TROFEO),
                    suceso("anu_kinder", "04-07", "Anu entra al kínder, con uniforme y mochila"),
                    guerra("sindoor", "05-07", "Ataques contra Pakistán: cuatro días de noticias de guerra en el teléfono", 4, HISTORICO, F_SINDOOR),
                    mundo("ahmedabad", "06-12", "Se cae un avión en Ahmedabad al despegar", certeza = HISTORICO, fuente = F_AHMEDABAD),
                    suceso("aumento", "07-15", "Le suben el sueldo en el almacén"),
                    suceso("telefono", "09-20", "Compra un teléfono nuevo, en abonos"),
                    mundo("mundial_femenil", "11-02", "La India gana el Mundial femenino de críquet", certeza = HISTORICO, fuente = F_MUNDIAL_FEM),
                )),
            TemporadaVida("2026", "censo",
                "La India gana el Mundial de críquet. Pasa el empadronador del censo a anotar la casa. Su madre enferma en la aldea y va a cuidarla dos semanas. En septiembre saben que esperan otro hijo. Anu aprende a andar en bicicleta.",
                anioDeLaIndia(2) + listOf(
                    mundo("t20", "03-08", "La India gana el Mundial de críquet: otra vez cohetes", certeza = HISTORICO, fuente = F_T20),
                    mundo("censo_casas", "05-20", "Pasa el empadronador del censo a anotar la casa", certeza = APROXIMADO, fuente = F_CENSO_IN),
                    suceso("madre_enferma", "07-18", "Le avisan que su madre está enferma en la aldea", cadena = "aldea"),
                    suceso("va_aldea", "07-20", "Toma el tren de noche a la aldea", cadena = "aldea"),
                    suceso("vuelve_aldea", "08-04", "Vuelve a la ciudad: su madre ya está mejor", cadena = "aldea"),
                    suceso("esperan", "09-28", "Saben que esperan otro hijo"),
                    suceso("anu_bici", "12-05", "Anu aprende a andar en bicicleta en el patio del edificio"),
                )),
            TemporadaVida("2027", "kabir",
                "El censo anota a cada persona de la casa. Nace Kabir y la abuela viene de la aldea a ayudar. Anu lee en voz alta un letrero en inglés. En noviembre se mudan a un departamento de dos cuartos en un edificio nuevo.",
                anioDeLaIndia(3) + listOf(
                    mundo("censo_personas", "02-15", "El censo: anotan a cada persona de la casa, con su edad", certeza = APROXIMADO, fuente = F_CENSO_IN),
                    suceso("nace_kabir", "05-28", "Nace su hijo Kabir", cadena = "kabir"),
                    suceso("abuela_llega", "06-01", "Su madre llega de la aldea a ayudar con el bebé", cadena = "kabir"),
                    suceso("abuela_se_va", "07-10", "Su madre vuelve a la aldea", cadena = "kabir"),
                    suceso("anu_lee", "09-10", "Anu lee en voz alta un letrero en inglés"),
                    suceso("mudanza", "11-25", "Se mudan a un departamento de dos cuartos en un edificio nuevo"),
                )),
            TemporadaVida("2028", "olimpiada",
                "Le dan la supervisión de un turno en el almacén. Kabir da sus primeros pasos. Empiezan los Juegos Olímpicos de Los Ángeles, con críquet. Compran una moto usada. En diciembre, la boda de un primo en la aldea.",
                anioDeLaIndia(4) + listOf(
                    suceso("turno", "04-05", "Le dan la supervisión de un turno en el almacén"),
                    suceso("kabir_camina", "05-30", "Kabir da sus primeros pasos"),
                    mundo("olimpiada", "07-14", "Empiezan los Juegos Olímpicos de Los Ángeles: vuelve el críquet", certeza = APROXIMADO, fuente = F_LA28),
                    suceso("moto", "09-08", "Compran una moto usada, en abonos"),
                    suceso("boda_primo", "12-05", "La boda de un primo en la aldea: tres días de fiesta", 3, escena = "fiesta"),
                )),
            TemporadaVida("2029", "elecciones",
                "Anu gana un premio de dibujo. El asteroide Apophis pasa más cerca que los satélites. Elecciones generales. Kabir dice frases enteras. Su pareja consigue trabajo en una clínica. Su padre deja el campo y le pasa la tierra a un primo.",
                anioDeLaIndia(5) + listOf(
                    suceso("anu_premio", "03-20", "Anu gana un premio de dibujo en la escuela"),
                    mundo("apophis", "04-13", "El asteroide Apophis pasa más cerca que los satélites: lo ven en el teléfono", certeza = APROXIMADO, fuente = F_APOPHIS),
                    mundo("elecciones", "05-10", "Elecciones generales: vota en la escuela del barrio", certeza = APROXIMADO, fuente = F_ELECCIONES_IN),
                    suceso("kabir_habla", "06-10", "Kabir dice frases enteras"),
                    suceso("pareja_clinica", "08-30", "Su pareja consigue trabajo en una clínica"),
                    suceso("padre_tierra", "10-01", "Su padre deja el campo y le pasa la tierra a un primo"),
                )),
        ),
        objetos = listOf("telefono", "audifonos", "moto", "chai", "tiffin", "cometa", "lampara", "paraguas", "ventilador", "tarjeta", "mochila", "botella"),
        estados = listOf(Estado("aldea", "va_aldea", "vuelve_aldea")),
    )

    // =====================================================================
    //  Alguien de México, una persona promedio. Una ciudad de México, de 2025 a 2029.
    // =====================================================================
    //
    // Dónde vive: una ciudad grande del centro del país, sin nombre. Casi ocho de cada diez mexicanos viven en ciudades.
    //
    // Su gente (toda inventada): su pareja (su nombre y su género los decide el cuento). Mateo, su hijo (3 años en la
    // temporada 1). Valeria, la hija que nace en la 3. Su mamá, que vive a unas calles. Trabaja en la oficina de una
    // distribuidora de refacciones.
    //
    // Lo real: las fiestas del año (los Reyes, la Candelaria, la Semana Santa, el Día del Niño, el Día de las Madres, el
    // Grito, los muertos, el Buen Fin, la Virgen de Guadalupe y las posadas), las lluvias, el simulacro de septiembre,
    // los aranceles de Estados Unidos, la primera elección de jueces, la Copa Oro, la explosión de Iztapalapa, el
    // Mundial de 2026 y, ya en el futuro, las elecciones de 2027, los Juegos de Los Ángeles y el paso de Apophis.
    //
    // Claves de escena: fiesta, adornos, altar, aguacero, alerta (el simulacro). Estado: «playa» (temporada 2: una
    // semana de vacaciones).

    private val F_DIA_NINO = "El Día del Niño" to "${ES}D%C3%ADa_del_Ni%C3%B1o"
    private val F_SIMULACRO = "El simulacro del 19 de septiembre" to "${ES}Simulacro_de_terremoto"
    private val F_BUEN_FIN = "El Buen Fin" to "${ES}El_Buen_Fin"
    private val F_ARANCELES = "La guerra comercial de 2025" to "${ES}Guerra_comercial_de_Estados_Unidos_con_Canad%C3%A1_y_M%C3%A9xico_de_2025"
    private val F_JUECES = "La elección judicial de 2025" to "${ES}Elecciones_federales_judiciales_extraordinarias_de_M%C3%A9xico_de_2025"
    private val F_2025 = "El año 2025" to "${ES}2025"
    private val F_IZTAPALAPA = "La explosión de Iztapalapa" to "${ES}Deflagraci%C3%B3n_de_Iztapalapa_de_2025"
    private val F_MUNDIAL_26 = "La Copa Mundial de 2026" to "${ES}Copa_Mundial_de_F%C3%BAtbol_de_2026"
    private val F_ELECCIONES_27 = "Las elecciones de 2027" to "${ES}Elecciones_federales_de_M%C3%A9xico_de_2027"
    private val F_LA28_ES = "Los Juegos Olímpicos de Los Ángeles" to "${ES}Juegos_Ol%C3%ADmpicos_de_Los_%C3%81ngeles_2028"

    private val VIERNES_MEX = listOf("04-18", "04-03", "03-26", "04-14", "03-30")   // de la Pascua de 2025 a 2029
    private val BUEN_FIN = listOf("11-13", "11-12", "11-11", "11-16", "11-15")       // de jueves al lunes del puente

    /** Lo que vuelve cada año, en la temporada [t]. */
    private fun anioDeMexico(t: Int): List<DiaEspecial> = listOf(
        fiesta("reyes", "01-06", "Día de Reyes: la rosca, y al que le sale el muñeco le tocan los tamales", certeza = HISTORICO, fuente = F_REYES),
        fiesta("candelaria", "02-02", "La Candelaria: los tamales que se deben desde Reyes", certeza = HISTORICO, fuente = F_CANDELARIA_ES),
        rito("viernes_santo", VIERNES_MEX[t - 1], "Semana Santa: la ciudad se vacía y el tráfico desaparece", certeza = HISTORICO, fuente = F_VIERNES_ES),
        fiesta("dia_nino", "04-30", "El Día del Niño: festival en la escuela", certeza = HISTORICO, fuente = F_DIA_NINO),
        fiesta("madres", "05-10", "El Día de las Madres: comida en casa de su mamá", certeza = HISTORICO, fuente = F_MADRE),
        mundo("lluvias", "06-10", "Empiezan las lluvias de la tarde: el tráfico no avanza", certeza = APROXIMADO, escena = "aguacero", clima = Clima.TORMENTA),
        fiesta("grito", "09-15", "La noche del Grito: pozole y banderitas", certeza = HISTORICO, fuente = F_GRITO, escena = "adornos"),
        mundo("simulacro", "09-19", "El simulacro: suena la alerta sísmica y todos salen a la calle", certeza = APROXIMADO, fuente = F_SIMULACRO, escena = "alerta"),
        fiesta("muertos", "11-01", "Todos Santos y los fieles difuntos: el altar con fotos y cempasúchil", 2, HISTORICO, F_MUERTOS, escena = "altar"),
        fiesta("buen_fin", BUEN_FIN[t - 1], "El Buen Fin: ofertas en las tiendas y en el teléfono", 5, APROXIMADO, F_BUEN_FIN),
        fiesta("guadalupe", "12-12", "El día de la Virgen de Guadalupe: mañanitas en la iglesia del barrio", certeza = HISTORICO, fuente = F_GUADALUPE),
        fiesta("posadas", "12-16", "Las posadas: la de la oficina, la de la escuela y la de la familia", 9, HISTORICO, F_POSADAS, escena = "adornos"),
    )

    val MEXICO = Calendario(
        vida = "mexico2025",
        estaciones = listOf("05-25" to Estacion.LLUVIAS, "10-20" to Estacion.SECAS),
        clima = listOf(
            mes(despejado = 21, nubes = 6, niebla = 1, polvo = 3), mes(despejado = 19, nubes = 6, viento = 1, polvo = 2),
            mes(despejado = 18, nubes = 8, lluvia = 1, viento = 2, polvo = 2), mes(despejado = 15, nubes = 10, lluvia = 3, tormenta = 1, polvo = 1),
            mes(despejado = 12, nubes = 11, lluvia = 5, tormenta = 3), mes(despejado = 6, nubes = 10, lluvia = 9, tormenta = 5),
            mes(despejado = 5, nubes = 10, lluvia = 11, tormenta = 5), mes(despejado = 5, nubes = 10, lluvia = 11, tormenta = 5),
            mes(despejado = 6, nubes = 10, lluvia = 10, tormenta = 4), mes(despejado = 12, nubes = 10, lluvia = 6, tormenta = 2),
            mes(despejado = 18, nubes = 8, lluvia = 2, niebla = 1), mes(despejado = 21, nubes = 6, niebla = 1, polvo = 2),
        ),
        temporadas = listOf(
            TemporadaVida("2025", "aranceles",
                "Saca un crédito para una lavadora. Entran los aranceles de Estados Unidos y en la oficina temen recortes. La primera elección de jueces. México gana la Copa Oro. Mateo entra al kínder. Explota una pipa de gas en Iztapalapa. Les suben la renta. Un primo vuelve de Estados Unidos para las fiestas.",
                anioDeMexico(1) + listOf(
                    suceso("lavadora", "02-20", "Saca un crédito para comprar una lavadora"),
                    mundo("aranceles", "03-04", "Entran los aranceles de Estados Unidos: en la oficina temen recortes", certeza = HISTORICO, fuente = F_ARANCELES),
                    mundo("jueces", "06-01", "La primera elección de jueces y ministros: va a votar muy poca gente", certeza = HISTORICO, fuente = F_JUECES),
                    mundo("copa_oro", "07-06", "México gana la Copa Oro: claxonazos en la avenida", certeza = HISTORICO, fuente = F_2025),
                    suceso("mateo_kinder", "08-25", "Mateo entra al kínder"),
                    mundo("iztapalapa", "09-10", "Explota una pipa de gas en Iztapalapa: lo ven en el teléfono toda la noche", certeza = HISTORICO, fuente = F_IZTAPALAPA),
                    suceso("renta", "10-08", "Les suben la renta del departamento"),
                    suceso("primo", "12-02", "Un primo vuelve de Estados Unidos para pasar las fiestas"),
                )),
            TemporadaVida("2026", "mundial",
                "Operan a su mamá de la vesícula. No alcanzan boletos para el Mundial. México le gana a Sudáfrica en la inauguración, y lo ven en una fonda. Una semana de vacaciones en la playa, en autobús. En noviembre saben que esperan otro hijo.",
                anioDeMexico(2) + listOf(
                    suceso("operan_mama", "03-03", "Operan a su mamá de la vesícula", 5, cadena = "mama"),
                    suceso("mama_casa", "03-09", "Su mamá vuelve a su casa", cadena = "mama"),
                    suceso("boletos", "05-15", "No alcanzan boletos para el Mundial: lo verán en una fonda"),
                    mundo("inauguracion", "06-11", "Empieza el Mundial: México le gana a Sudáfrica en la inauguración", certeza = HISTORICO, fuente = F_MUNDIAL_26, escena = "fiesta"),
                    suceso("va_playa", "07-20", "Se van una semana a la playa, en autobús", cadena = "playa"),
                    suceso("vuelve_playa", "07-27", "Vuelven de la playa, quemados por el sol", cadena = "playa"),
                    suceso("esperan", "11-22", "Saben que esperan otro hijo"),
                )),
            TemporadaVida("2027", "elecciones",
                "Se mudan a un departamento un poco más grande y más lejos. Elecciones intermedias. Nace Valeria. Le roban el teléfono en el camión. Bautizan a Valeria.",
                anioDeMexico(3) + listOf(
                    suceso("mudanza", "03-01", "Se mudan a un departamento un poco más grande, más lejos del centro"),
                    mundo("elecciones", "06-06", "Elecciones intermedias: vota en la casilla de la esquina", certeza = APROXIMADO, fuente = F_ELECCIONES_27),
                    suceso("nace_valeria", "07-15", "Nace su hija Valeria", cadena = "valeria"),
                    suceso("robo", "08-28", "Le roban el teléfono en el camión"),
                    suceso("bautizo", "10-16", "Bautizan a Valeria; su mamá es la madrina", cadena = "valeria", escena = "fiesta"),
                )),
            TemporadaVida("2028", "olimpiada",
                "Le dan la plaza fija en la empresa. Empiezan los Juegos Olímpicos de Los Ángeles. Valeria da sus primeros pasos. Mateo entra a la primaria. Les aprueban el crédito para una casa en un fraccionamiento lejano.",
                anioDeMexico(4) + listOf(
                    suceso("plaza", "03-10", "Le dan la plaza fija en la empresa"),
                    mundo("olimpiada", "07-14", "Empiezan los Juegos Olímpicos de Los Ángeles", certeza = APROXIMADO, fuente = F_LA28_ES),
                    suceso("valeria_camina", "07-25", "Valeria da sus primeros pasos"),
                    suceso("primaria", "08-28", "Mateo entra a la primaria"),
                    suceso("credito_casa", "10-27", "Les aprueban el crédito para una casa en un fraccionamiento lejano"),
                )),
            TemporadaVida("2029", "fraccionamiento",
                "Se mudan a la casa del fraccionamiento, a hora y media del trabajo. El asteroide Apophis pasa más cerca que los satélites. Valeria dice sus primeras frases. Adoptan a un perro. Termina un curso en línea de contabilidad. Mateo mete su primer gol.",
                anioDeMexico(5) + listOf(
                    suceso("casa", "02-24", "Se mudan a la casa del fraccionamiento, a hora y media del trabajo"),
                    mundo("apophis", "04-13", "El asteroide Apophis pasa más cerca que los satélites: lo ven en el teléfono", certeza = APROXIMADO, fuente = F_APOPHIS),
                    suceso("valeria_habla", "05-20", "Valeria dice sus primeras frases"),
                    suceso("perro", "07-20", "Adoptan a un perro que se quedó en la puerta de la casa"),
                    suceso("curso", "09-05", "Termina un curso en línea de contabilidad"),
                    suceso("gol", "10-24", "Mateo mete su primer gol en la liga del fraccionamiento"),
                )),
        ),
        objetos = listOf("telefono", "audifonos", "tarjeta_metro", "termo", "tupper", "llaves", "cargador", "refresco", "tamal", "credencial", "mochila", "paraguas"),
        estados = listOf(Estado("playa", "va_playa", "vuelve_playa")),
    )
}
