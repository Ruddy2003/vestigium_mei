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
 * Los calendarios del grupo 3 (tanda A3): las cinco temporadas (2027 a 2031) de Cayo (Romano), Pay (Egipto), Ama
 * (Prehistoria), K'an (Mesoamérica) y Nanai-vandak (Ruta de la Seda). Siguen las reglas de `AnioPiloto.kt`. Qué es real
 * y qué inventado, con cada fuente, está en `docs/historia/fuentes.md`.
 *
 * A diferencia de los grupos 1 y 2, estos personajes **tienen cuadro**: el de su tema (tandas S1 y S2). Por eso el
 * comentario de cada calendario dice qué tiene que mostrar el cuadro para contar lo mismo que el cuento.
 *
 * Lo propio de este grupo:
 *
 * - **Pay** usa el calendario civil egipcio, como Ipy (`AnioG1.kt`), cincuenta años después: sus fiestas caen trece
 *   días antes que las de Ipy. La Fiesta del Valle va en la luna nueva de su mes, calculada.
 * - **Ama** vive antes de la escritura, como Naa: sus años no tienen número y lo que vuelve cada año es «aproximado».
 * - **K'an** cuenta el tiempo con la cuenta larga, el tzolk'in y el haab. Las fechas del año maya se pasaron al juliano
 *   con la correlación GMT (584283); otras correlaciones las mueven uno o dos días, y por eso van como «aproximado».
 * - **Nanai-vandak** va y viene por la ruta: pasa el verano en Chang'an (temporadas 1, 3 y 5) o en Samarcanda (2 y 4)
 *   y el invierno en el camino. Las fiestas chinas siguen la luna y se calcularon como las de Kichizō.
 */
internal object AnioG3 {
    val TODOS: List<Calendario> by lazy { listOf(CAYO, PAY, AMA, KAN, NANAI) }

    /** Días «históricos» sin enlace porque su fecha sale de un cálculo hecho en la sesión. Este grupo no tiene. */
    val CALCULADOS = emptySet<String>()

    private val W = "https://en.wikipedia.org/wiki/"

    // =====================================================================
    //  Cayo, legionario. El campamento de la Legión VII Claudia en Viminacium, sobre el Danubio, del 110 al 114 d. C.
    // =====================================================================
    //
    // Su gente (toda inventada): Sabina, que sirve vino en una taberna del poblado pegado al campamento y con quien no
    // puede casarse mientras sirva. Gaia, la hija de los dos, que nace en la temporada 3. Los de su tienda (el
    // contubernio): Atilio, su amigo, hispano como él, que se va a la guerra de oriente en la 4; Barbatio, el que juega a
    // los dados; Durio, un recluta tracio que llega en la 2. Fausto, el centurión. Su madre, en Hispania, que le escribe
    // por mano de un escribano.
    //
    // Lo real: el calendario religioso del ejército (los votos por el emperador, el día en que subió al trono, las
    // Quinquatrias, el cumpleaños de Roma, las Rosalias de los estandartes, las Neptunalias, el cumpleaños del
    // emperador, el Armilustrium y las Saturnales), el foro y la columna de Trajano, la muerte de Marciana, el puente de
    // piedra sobre el Danubio, la guerra contra los partos y las noticias de Armenia y Mesopotamia. Que se sepan en el
    // campamento esos días es inventado.
    //
    // Claves de escena: fiesta, rosas (los estandartes adornados), humo, luto, obra, partida, nieve. Arcos: muralla (la
    // muralla del campamento, que se rehace en piedra: 0 = la empalizada de madera, 1 = toda de piedra) y guarnicion
    // (cuántos soldados hay: baja de golpe cuando parte la vejilación hacia oriente, en la temporada 4). Estado:
    // «camino» (temporada 2: seis semanas reparando el camino del desfiladero, lejos del campamento).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la muralla según `muralla`, con andamios
    // mientras sube; menos tiendas y fuegos en el campamento según `guarnicion`; el río helado cuando nieva en enero; y
    // nada del campamento mientras el estado es «camino».

    private val F_FERIALE = "El calendario del ejército (Feriale Duranum)" to "${W}Feriale_Duranum"
    private val F_TRAJANO = "Trajano" to "${W}Trajan"
    private val F_ROSALIA = "Las Rosalias" to "${W}Rosalia_(festival)"
    private val F_ARMILUSTRIUM = "El Armilustrium" to "${W}Armilustrium"
    private val F_SATURNALIA = "Las Saturnales" to "${W}Saturnalia"
    private val F_110 = "El año 110" to "${W}AD_110"
    private val F_PUENTE = "El puente de Trajano" to "${W}Trajan%27s_Bridge"
    private val F_FORO_TRAJANO = "El foro de Trajano" to "${W}Trajan%27s_Forum"
    private val F_112 = "El año 112" to "${W}AD_112"
    private val F_COLUMNA_TRAJANO = "La columna de Trajano" to "${W}Trajan%27s_Column"
    private val F_PARTOS = "La campaña de Trajano contra los partos" to "${W}Trajan%27s_Parthian_campaign"
    private val F_114 = "El año 114" to "${W}AD_114"

    /** El calendario religioso del campamento, igual todos los años (el juliano no se corre). */
    private val anioDelCampamento: List<DiaEspecial> = listOf(
        rito("votos", "01-03", "Los votos por la salud del emperador: la legión jura y sacrifica un buey", certeza = APROXIMADO),
        rito("dia_del_emperador", "01-28", "El día en que el emperador subió al trono: sacrificio ante los estandartes", certeza = HISTORICO, fuente = F_TRAJANO),
        fiesta("quinquatrias", "03-19", "Las Quinquatrias: se purifican las armas", 5, HISTORICO, F_FERIALE),
        fiesta("natalicio_roma", "04-21", "El cumpleaños de Roma: una vaca para la Ciudad Eterna", certeza = HISTORICO, fuente = F_FERIALE),
        fiesta("rosalias", "05-10", "Las Rosalias: los estandartes se adornan con rosas", certeza = APROXIMADO, fuente = F_ROSALIA, escena = "rosas"),
        fiesta("neptunalias", "07-23", "Las Neptunalias: una ofrenda al dios de las aguas, a orillas del Danubio", certeza = HISTORICO, fuente = F_FERIALE),
        fiesta("natalicio_emperador", "09-18", "El cumpleaños del emperador: desfile, sacrificio y vino de la legión", certeza = HISTORICO, fuente = F_TRAJANO),
        rito("armilustrium", "10-19", "El Armilustrium: las armas se purifican y se guardan para el invierno", certeza = HISTORICO, fuente = F_ARMILUSTRIUM),
        fiesta("saturnales", "12-17", "Las Saturnales: siete días al revés, también en el campamento", 7, HISTORICO, F_SATURNALIA),
    )

    val CAYO = Calendario(
        vida = "legionario",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 6, nubes = 10, nieve = 8, niebla = 5, viento = 2), mes(despejado = 7, nubes = 10, nieve = 6, niebla = 3, viento = 2),
            mes(despejado = 9, nubes = 10, lluvia = 6, nieve = 2, viento = 4), mes(despejado = 11, nubes = 9, lluvia = 8, viento = 2),
            mes(despejado = 13, nubes = 8, lluvia = 8, tormenta = 2), mes(despejado = 15, nubes = 6, lluvia = 6, tormenta = 3),
            mes(despejado = 20, nubes = 4, lluvia = 3, tormenta = 4), mes(despejado = 20, nubes = 4, lluvia = 3, tormenta = 4),
            mes(despejado = 16, nubes = 7, lluvia = 5, tormenta = 1, niebla = 1), mes(despejado = 11, nubes = 9, lluvia = 6, niebla = 4),
            mes(despejado = 6, nubes = 11, lluvia = 7, niebla = 6), mes(despejado = 5, nubes = 10, nieve = 7, niebla = 6, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("110 d. C.", "obras",
                "Cayo conoce a Sabina en la taberna del poblado. Empiezan a rehacer en piedra la muralla del campamento. Le llega una carta de su madre. Una piedra le aplasta dos dedos y sanan. Llega la noticia del foro del emperador. Gana a los dados la paga de Barbatio y se la devuelve.",
                anioDelCampamento + listOf(
                    suceso("sabina", "01-20", "Conoce a Sabina, que sirve vino en una taberna del poblado junto al campamento"),
                    mundo("muralla", "04-03", "Empiezan a rehacer en piedra la muralla del campamento", certeza = INVENTADO, escena = "obra"),
                    suceso("carta_madre", "06-20", "Le llega una carta de su madre desde Hispania, escrita por un escribano"),
                    suceso("dedos", "08-12", "Una piedra de la muralla le aplasta dos dedos", cadena = "dedos"),
                    suceso("dedos_sanan", "08-30", "Ya puede cerrar la mano: vuelve a la obra", cadena = "dedos"),
                    mundo("noticia_foro", "09-01", "Llega la noticia: el emperador levanta en Roma un foro con el oro de la Dacia", certeza = APROXIMADO, fuente = F_110),
                    suceso("dados", "11-10", "Le gana a Barbatio en los dados la paga de un mes, y se la devuelve"),
                    mundo("primera_nieve", "12-01", "La primera nevada: el campamento amanece blanco", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                )),
            TemporadaVida("111 d. C.", "hielo",
                "El Danubio se hiela y los sármatas cruzan sobre el hielo a quemar una aldea. Llega Durio, un recluta tracio. Cayo pasa seis semanas reparando el camino del desfiladero. Pasa un convoy hacia la Dacia por el puente de piedra. Le alquila un cuarto a Sabina en el poblado. Gana el concurso de jabalina.",
                anioDelCampamento + listOf(
                    mundo("rio_helado", "01-10", "El Danubio se hiela de orilla a orilla", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    guerra("sarmatas", "01-18", "Los sármatas cruzan el río helado y queman una aldea de la otra orilla", certeza = INVENTADO, escena = "humo"),
                    suceso("recluta", "03-30", "Llega a su tienda Durio, un recluta tracio que no sabe latín"),
                    suceso("sale_camino", "06-01", "Sale con su centuria a reparar el camino del desfiladero, río abajo", cadena = "camino"),
                    suceso("vuelve_camino", "07-15", "Vuelve al campamento del camino del desfiladero, quemado por el sol", cadena = "camino"),
                    mundo("convoy", "08-20", "Pasa un convoy hacia la Dacia, que cruzará por el puente de piedra del emperador", certeza = APROXIMADO, fuente = F_PUENTE),
                    suceso("cuarto_sabina", "10-05", "Le alquila a Sabina un cuarto en el poblado, con lo que tenía ahorrado"),
                    suceso("jabalina", "11-20", "Gana el concurso de lanzamiento de jabalina de la cohorte"),
                )),
            TemporadaVida("112 d. C.", "foro",
                "Llega la noticia de que inauguraron el foro del emperador. Sabina espera un hijo. El Danubio crece y se lleva el embarcadero. Pasa revista el legado. A Cayo lo hacen inmune por su mano en la obra. Termina la muralla. Llega la noticia de la muerte de Marciana. Nace Gaia, que la ley no reconoce como suya.",
                anioDelCampamento + listOf(
                    mundo("foro_abre", "01-12", "Llega la noticia: inauguraron en Roma el foro del emperador", certeza = APROXIMADO, fuente = F_FORO_TRAJANO),
                    suceso("espera_sabina", "03-05", "Sabina le dice que espera un hijo"),
                    mundo("crecida", "05-20", "El Danubio crece con el deshielo y se lleva el embarcadero", certeza = INVENTADO, clima = Clima.LLUVIA),
                    suceso("revista", "06-15", "Pasa revista el legado de la legión: hay que bruñir todo dos veces"),
                    suceso("inmune", "07-01", "Por su mano en la obra lo hacen inmune: ya no hace guardias ni limpia letrinas"),
                    mundo("marciana", "09-10", "Llega la noticia: murió Marciana, la hermana del emperador", certeza = APROXIMADO, fuente = F_112, escena = "luto"),
                    suceso("nace_gaia", "11-02", "Nace su hija Gaia, que la ley no reconoce como suya"),
                )),
            TemporadaVida("113 d. C.", "partida",
                "Hacen a Cayo jefe de su tienda. Llega la noticia de la columna del emperador. Sabina abre su propia taberna. En septiembre sale hacia la guerra de oriente una parte de la legión, y con ella Atilio. Gaia camina. Llega la noticia de que el emperador salió de Roma hacia la guerra.",
                anioDelCampamento + listOf(
                    suceso("jefe_tienda", "04-02", "Lo hacen jefe de su tienda: manda a los siete de su contubernio"),
                    mundo("columna", "05-28", "Llega la noticia: en Roma levantaron una columna con la guerra de la Dacia en espiral", certeza = APROXIMADO, fuente = F_COLUMNA_TRAJANO),
                    suceso("taberna", "07-05", "Sabina abre su propia taberna en el poblado, con lo que ahorró"),
                    suceso("despedida", "09-24", "Se despide de Atilio, que se va con los que marchan a oriente"),
                    guerra("vejilacion", "09-25", "Sale hacia la guerra de oriente una parte de la legión, con sus estandartes", certeza = APROXIMADO, fuente = F_PARTOS, escena = "partida"),
                    suceso("gaia_camina", "11-15", "Gaia da sus primeros pasos en la taberna de su madre"),
                    mundo("emperador_parte", "11-20", "Llega la noticia: el emperador salió de Roma hacia la guerra contra los partos", certeza = APROXIMADO, fuente = F_PARTOS),
                )),
            TemporadaVida("114 d. C.", "oriente",
                "Llegan reclutas nuevos para llenar el hueco. Le llega una carta de Atilio desde Antioquía. Gaia dice sus primeras palabras. Terminan los baños de piedra del campamento. Llega la noticia de Armenia. Lo proponen para optio. Le talla a Gaia un caballito de madera. En diciembre, la noticia de Mesopotamia.",
                anioDelCampamento + listOf(
                    mundo("reclutas", "03-01", "Llegan reclutas nuevos para llenar el hueco de los que se fueron", certeza = INVENTADO),
                    suceso("carta_atilio", "04-10", "Le llega una carta de Atilio desde Antioquía"),
                    suceso("gaia_habla", "06-01", "Gaia dice sus primeras palabras, en latín y en la lengua de su madre"),
                    mundo("banos", "06-25", "Terminan los baños nuevos del campamento, de piedra", certeza = INVENTADO),
                    guerra("armenia", "07-15", "Llega la noticia: el emperador tomó Armenia", certeza = APROXIMADO, fuente = F_114, escena = "fiesta"),
                    suceso("optio", "08-20", "El centurión lo propone para optio de la centuria"),
                    suceso("caballito", "10-25", "Le talla a Gaia un caballito de madera"),
                    guerra("mesopotamia", "12-10", "Llega la noticia: las legiones entran en Mesopotamia", certeza = APROXIMADO, fuente = F_114),
                )),
        ),
        arcos = listOf(
            Arco("muralla", TipoArco.OBRA, listOf(p(1, "04-03", 0.0), p(1, "10-31", 0.3), p(2, "04-01", 0.3), p(2, "10-31", 0.6), p(3, "04-01", 0.6), p(3, "10-31", 1.0))),
            Arco("guarnicion", TipoArco.OTRO, listOf(p(1, "01-01", 1.0), p(4, "09-25", 0.6, salto = true), p(5, "03-01", 0.65, salto = true))),
        ),
        objetos = listOf("gladio", "pilum", "escudo", "casco", "caligas", "dolabra", "petate", "posca", "pan", "moneda", "tablilla", "lucerna"),
        estados = listOf(Estado("camino", "sale_camino", "vuelve_camino")),
    )

    // =====================================================================
    //  Pay, escriba de la tumba. Deir el-Medina, Tebas oeste, del 1200 al 1196 a. C.
    // =====================================================================
    //
    // Su gente (toda inventada): Tamit, su mujer. Huy, su hijo (8 años en la temporada 1), que aprende los signos.
    // Meryt, la hija que nace en la temporada 2. Tiy, su madre, que muere en la 3. Hay, su hermano, pintor de la
    // cuadrilla. El aguador, que sube el agua en burro desde el río. Paneb, el capataz, y Amennakht, el obrero que lo
    // denunció, son reales; lo que Pay ve de ellos es inventado.
    //
    // Lo real: la aldea de los obreros de la Tumba; el rey Seti II, que muere en el 1197 a. C., y su tumba (KV15),
    // terminada deprisa; el rey Siptah, que lo sucede, y su tumba nueva (KV47); el canciller Bay; la denuncia contra el
    // capataz Paneb. Las fiestas siguen el calendario civil, que no tenía bisiestos: son las de Ipy corridas trece días
    // (ver `fuentes.md`). [Aproximación]
    //
    // Claves de escena: fiesta, procesion, luto, crecida, polvo, lluvia. Arcos: tumba (la tumba del rey que hace la
    // cuadrilla: 1 = terminada; en la temporada 4 se cierra la de Seti II y se empieza de cero la de Siptah) y rio (el
    // agua de la crecida sobre los campos, que se ve desde la aldea).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el valle verde o inundado al fondo según
    // `rio`; los montones de lascas de caliza y el andamio de la entrada de la tumba según `tumba`; y la aldea sin los
    // hombres mientras el estado es «valle» (duermen en las chozas del paso para acabar la tumba del rey muerto).

    private val F_CIVIL_PAY = "El calendario egipcio" to "${W}Egyptian_calendar"
    private val F_OPET_PAY = "La fiesta de Opet" to "${W}Opet_Festival"
    private val F_JOIAK_PAY = "El mes de Joiak" to "${W}Khoiak"
    private val F_VALLE_PAY = "La Bella Fiesta del Valle" to "${W}Beautiful_Festival_of_the_Valley"
    private val F_AMENHOTEP = "Amenhotep I, el rey patrón de la aldea" to "${W}Amenhotep_I"
    private val F_CRECIDA_PAY = "La crecida del Nilo" to "${W}Flooding_of_the_Nile"
    private val F_DEIR = "Deir el-Medina" to "${W}Deir_el-Medina"
    private val F_PANEB = "Paneb, el capataz" to "${W}Paneb"
    private val F_SETI = "Seti II" to "${W}Seti_II"
    private val F_KV15 = "La tumba de Seti II (KV15)" to "${W}KV15"
    private val F_KV47 = "La tumba de Siptah (KV47)" to "${W}KV47"
    private val F_BAY = "Bay, el canciller" to "${W}Bay_(chancellor)"

    // Las fechas julianas de cada fiesta en las cinco temporadas (1200 a 1196 a. C.); ver `fuentes.md`.
    private val ANIO_NUEVO_PAY = listOf("06-19", "06-19", "06-19", "06-18", "06-18")   // I Akhet 1
    private val EPAGOMENOS_PAY = listOf("06-14", "06-14", "06-14", "06-13", "06-13")
    private val OPET_PAY = listOf("08-02", "08-02", "08-02", "08-01", "08-01")         // II Akhet 15
    private val JOIAK_PAY = listOf("10-04", "10-04", "10-04", "10-03", "10-03")        // IV Akhet 18 a 30
    private val MIN_PAY = listOf("02-24", "02-24", "02-24", "02-24", "02-23")          // I Shemu 11
    private val VALLE_PAY = listOf("03-20", "04-07", "03-28", "03-17", "04-05")        // la luna nueva de II Shemu

    /** Lo que vuelve cada año en la aldea, en la temporada [t]. En la 4 la Fiesta del Valle la ven desde la obra. */
    private fun anioDeLaAldea(t: Int): List<DiaEspecial> = listOf(
        fiesta("amenhotep", "01-10", "La fiesta de Amenhotep I: la estatua del rey patrón sale en andas y da oráculos", 2, APROXIMADO, F_AMENHOTEP, escena = "procesion"),
        fiesta("min", MIN_PAY[t - 1], "La fiesta de Min: el dios de la cosecha sale de su templo", certeza = APROXIMADO),
        if (t == 4) fiesta("valle_obra", VALLE_PAY[t - 1], "La Fiesta del Valle, vista desde la obra: la procesión pasa y ellos siguen picando", 2, APROXIMADO, F_VALLE_PAY)
        else fiesta("valle", VALLE_PAY[t - 1], "La Fiesta del Valle: la barca de Amón cruza el río para visitar a los muertos", 2, APROXIMADO, F_VALLE_PAY, escena = "procesion"),
        mundo("viento_desierto", "05-10", "El viento del desierto: arena en el pan, en los ojos y en las tumbas", certeza = APROXIMADO, escena = "polvo", clima = Clima.POLVO),
        fiesta("epagomenos", EPAGOMENOS_PAY[t - 1], "Los cinco días fuera del año: los cumpleaños de los dioses", 5, APROXIMADO, F_CIVIL_PAY),
        fiesta("anio_nuevo", ANIO_NUEVO_PAY[t - 1], "Año nuevo: empieza la estación de la inundación", certeza = APROXIMADO, fuente = F_CIVIL_PAY),
        mundo("crecida", "07-25", "Desde los cerros se ve subir el río sobre los campos", certeza = APROXIMADO, fuente = F_CRECIDA_PAY, escena = "crecida"),
        fiesta("opet", OPET_PAY[t - 1], "La fiesta de Opet: la barca de Amón va de Karnak a Luxor", 11, APROXIMADO, F_OPET_PAY),
        fiesta("joiak", JOIAK_PAY[t - 1], "Joiak: los ritos de Osiris, que muere y vuelve", 13, APROXIMADO, F_JOIAK_PAY),
        rito("racion", "11-28", "Llega el grano del mes: Pay lo pesa y lo reparte casa por casa", certeza = APROXIMADO, fuente = F_DEIR),
    )

    val PAY = Calendario(
        vida = "escriba_tumba",
        estaciones = listOf("03-15" to Estacion.COSECHA, "07-25" to Estacion.INUNDACION, "11-01" to Estacion.SIEMBRA),
        clima = listOf(
            mes(despejado = 25, nubes = 3, viento = 2, niebla = 1), mes(despejado = 23, nubes = 2, viento = 2, polvo = 1),
            mes(despejado = 23, nubes = 2, viento = 3, polvo = 3), mes(despejado = 22, nubes = 2, viento = 2, polvo = 4),
            mes(despejado = 24, nubes = 1, viento = 2, polvo = 4), mes(despejado = 28, nubes = 1, polvo = 1),
            mes(despejado = 29, nubes = 1, polvo = 1), mes(despejado = 28, nubes = 2, polvo = 1),
            mes(despejado = 27, nubes = 2, viento = 1), mes(despejado = 27, nubes = 2, viento = 1, lluvia = 1),
            mes(despejado = 26, nubes = 3, niebla = 1), mes(despejado = 25, nubes = 4, niebla = 1, lluvia = 1),
        ),
        temporadas = listOf(
            TemporadaVida("1200 a. C.", "seti",
                "La cuadrilla decora la tumba del rey Seti II. Huy copia sus primeros signos. A Pay le pica un escorpión en el paso de la montaña y sana. El aguador cae enfermo y la aldea bebe de las jarras guardadas. Tamit espera un hijo. El visir visita las obras.",
                anioDeLaAldea(1) + listOf(
                    suceso("huy_signos", "04-15", "Huy copia sus primeros signos en una lasca de caliza"),
                    suceso("escorpion", "05-25", "Le pica un escorpión en el paso de la montaña", cadena = "escorpion"),
                    suceso("sana_escorpion", "06-01", "Ya no tiene hinchada la pierna: vuelve a subir al Valle", cadena = "escorpion"),
                    mundo("aguador", "08-25", "El aguador cae enfermo: la aldea bebe de las jarras guardadas", certeza = INVENTADO),
                    suceso("tamit_espera", "09-10", "Tamit le dice que espera un hijo"),
                    mundo("visir", "12-10", "El visir cruza el río a ver cómo va la tumba del rey", certeza = INVENTADO),
                )),
            TemporadaVida("1199 a. C.", "denuncia",
                "Huy lleva solo el burro al pozo. Nace Meryt. Corre por la aldea la denuncia del obrero Amennakht contra el capataz Paneb. Hay pinta las estrellas del techo de la tumba de la reina. Pay encarga una estela para el altar de la casa. En diciembre el grano no llega a tiempo.",
                anioDeLaAldea(2) + listOf(
                    suceso("huy_burro", "03-01", "Huy lleva solo el burro al pozo del río"),
                    suceso("nace_meryt", "05-05", "Nace su hija Meryt"),
                    mundo("denuncia", "05-28", "Corre por la aldea la denuncia del obrero Amennakht contra el capataz Paneb", certeza = APROXIMADO, fuente = F_PANEB),
                    suceso("estrellas", "09-20", "Su hermano Hay pinta las estrellas del techo de la tumba de la reina"),
                    suceso("estela_casa", "11-05", "Encarga una estela pequeña para el altar de la casa"),
                    mundo("grano_tarde", "12-12", "El grano del mes no llega: los obreros se sientan delante del templo a esperar", certeza = INVENTADO),
                )),
            TemporadaVida("1198 a. C.", "paneb",
                "Muere Tiy, la madre de Pay, y la entierran en la colina. Meryt camina. Se dice que llevaron al capataz Paneb ante el visir. Huy empieza a aprender los textos de la escuela con su padre. Se desploma un trozo de techo en la tumba de la reina.",
                anioDeLaAldea(3) + listOf(
                    suceso("enferma_tiy", "01-25", "Su madre, Tiy, cae enferma", 5, cadena = "tiy"),
                    suceso("muere_tiy", "02-05", "Muere su madre, y la entierran en la colina de la aldea", cadena = "tiy", escena = "luto"),
                    suceso("meryt_camina", "04-25", "Meryt da sus primeros pasos en el patio"),
                    mundo("paneb_visir", "06-25", "Se dice que llevaron al capataz Paneb ante el visir", certeza = APROXIMADO, fuente = F_PANEB),
                    suceso("huy_escuela", "09-01", "Huy empieza a copiar con su padre los textos de la escuela"),
                    mundo("derrumbe", "10-25", "Se desploma un trozo de techo en la tumba de la reina: nadie sale herido", certeza = INVENTADO),
                )),
            TemporadaVida("1197 a. C.", "muere_rey",
                "Muere el rey Seti II. La cuadrilla duerme en las chozas del paso para acabar su tumba, que se termina deprisa, y lo entierran a los setenta días. Empieza la tumba del nuevo rey, Siptah. Huy ayuda a contar las mechas. Tamit teje en un telar nuevo. A Hay le tiembla la mano.",
                anioDeLaAldea(4) + listOf(
                    mundo("muere_rey", "03-02", "Llega la noticia: murió el rey; hay que acabar su tumba antes del entierro", certeza = APROXIMADO, fuente = F_SETI, escena = "luto"),
                    mundo("entierro_rey", "05-11", "Entierran al rey en su tumba del Valle", certeza = APROXIMADO, fuente = F_KV15, escena = "procesion"),
                    mundo("tumba_nueva", "06-25", "Empiezan a picar la tumba del nuevo rey, Siptah", certeza = APROXIMADO, fuente = F_KV47),
                    suceso("huy_mechas", "08-20", "Huy le ayuda a contar las mechas del mes"),
                    suceso("telar", "10-25", "Tamit estrena un telar que le hizo su hermano Hay"),
                    suceso("mano_hay", "12-01", "A su hermano Hay le tiembla la mano: deja los detalles a los jóvenes"),
                )),
            TemporadaVida("1196 a. C.", "siptah",
                "Huy escribe su primera carta. Viene al Valle el canciller Bay, que tendrá su propia tumba entre las de los reyes. Meryt canta. Lo nombran escriba de una de las dos cuadrillas. Hay enseña a pintar a los jóvenes. Llueve en el desierto y el agua entra en una tumba.",
                anioDeLaAldea(5) + listOf(
                    suceso("huy_carta", "03-10", "Huy escribe su primera carta: a su abuela, en la colina"),
                    mundo("canciller", "04-25", "Viene al Valle el canciller Bay, el que manda de verdad junto al joven rey", certeza = APROXIMADO, fuente = F_BAY),
                    suceso("meryt_canta", "06-10", "Meryt canta una canción del río que nadie le enseñó"),
                    suceso("ascenso", "08-25", "Lo nombran escriba de la cuadrilla del lado derecho"),
                    suceso("hay_maestro", "10-20", "Su hermano Hay enseña a pintar a los muchachos de la cuadrilla"),
                    mundo("lluvia_desierto", "11-20", "Llueve en el desierto: el agua baja por el Valle y entra en una tumba", certeza = INVENTADO, escena = "lluvia", clima = Clima.TORMENTA),
                )),
        ),
        arcos = listOf(
            Arco("tumba", TipoArco.OBRA, listOf(p(1, "01-01", 0.55), p(4, "03-01", 0.8), p(4, "05-10", 1.0), p(4, "06-25", 0.0, salto = true), p(5, "12-31", 0.35))),
            Arco.cadaAnio("rio", TipoArco.OTRO, "05-20" to 0.0, "07-25" to 0.03, "09-15" to 1.0, "10-28" to 0.45, "11-30" to 0.15),
        ),
        objetos = listOf("ostracon", "papiro", "paleta", "pincel", "cincel", "mecha", "cerveza", "pan", "cantaro", "escarabajo", "sandalias", "lampara"),
        estados = listOf(Estado("valle", "muere_rey", "entierro_rey")),
    )

    // =====================================================================
    //  Ama, pintora de cuevas. Un valle de caliza del suroeste de Europa, hace unos 17,000 años.
    // =====================================================================
    //
    // Su gente (toda inventada): Tor, su compañero, cazador. Lin, la hija que nace en la temporada 2. Yemi, su madre, la
    // que le enseñó a pintar, que muere en la 3. Oko, el viejo que sabe las historias. Duro, un muchacho del grupo, que
    // caza su primer reno en la 4. Sel, que pinta bisontes en la cueva de otro grupo y a quien Ama conoce en la reunión
    // de la 3.
    //
    // Lo real: el final de la última glaciación en el suroeste de Europa (el Magdaleniense): los renos que se cazan en
    // otoño, los caballos y los saigas, los salmones del río, los mamuts y los leones de las cavernas, el propulsor, las
    // agujas de hueso, las lámparas de grasa y las manos sopladas en la roca. El año del grupo (el abrigo junto al río en
    // invierno y el campamento de verano en la meseta) es una reconstrucción. [Aproximación]
    //
    // Sus años no tienen número, como los de Naa: cada temporada lleva el nombre de lo que pasó.
    //
    // Claves de escena: fuego, nieve, caza, luto, reunion, aurora. Arcos: friso (el friso de caballos que Ama pinta en lo
    // hondo de la cueva: de 0 a 1 en las cinco temporadas) y nieve (cuánta nieve cubre el valle). Estado: «verano» (del
    // 1 de junio al 19 de septiembre de cada año el grupo vive en el campamento de la meseta, no en el abrigo).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el abrigo de roca junto al río, con la nieve
    // según `nieve`; las tiendas de piel en la meseta mientras el estado es «verano»; y, en la luz de la lámpara, el
    // friso a medio pintar según `friso`.

    private val F_MAGDALENIENSE = "El Magdaleniense" to "${W}Magdalenian"
    private val F_GLACIACION = "El último máximo glacial" to "${W}Last_Glacial_Maximum"
    private val F_SAIGA = "El saiga" to "${W}Saiga_antelope"
    private val F_SALMON = "El salmón" to "${W}Atlantic_salmon"
    private val F_RENO = "El reno" to "${W}Reindeer"
    private val F_AGUJA = "La aguja de coser" to "${W}Sewing_needle"
    private val F_MAMUT = "El mamut lanudo" to "${W}Woolly_mammoth"
    private val F_LEON = "El león de las cavernas" to "${W}Cave_lion"
    private val F_LASCAUX = "Lascaux" to "${W}Lascaux"
    private val F_MANOS = "Las manos sopladas" to "${W}Hand_stencil"
    private val F_PROPULSOR = "El propulsor" to "${W}Spear-thrower"
    private val F_CABALLO = "El caballo salvaje" to "${W}Wild_horse"
    private val F_BISONTE = "El bisonte de la estepa" to "${W}Steppe_bison"

    /** Lo que vuelve cada año en el valle, igual todos los años. */
    private val anioDelValle: List<DiaEspecial> = listOf(
        mundo("frio_hondo", "02-01", "El frío más hondo: el río se hiela y no se sale del abrigo", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
        mundo("aves", "03-15", "Vuelven las aves que se fueron: el invierno se está rompiendo", certeza = APROXIMADO),
        mundo("deshielo", "04-10", "El río se deshiela y crece, gris de barro", certeza = APROXIMADO, fuente = F_GLACIACION),
        mundo("saigas", "05-15", "Pasan los saigas por la llanura del valle", certeza = APROXIMADO, fuente = F_SAIGA),
        rito("sube_verano", "06-01", "Suben al campamento de verano, en la meseta", certeza = APROXIMADO, fuente = F_MAGDALENIENSE),
        cosecha("salmones", "07-20", "Suben los salmones por el río: se pescan con arpón", 15, APROXIMADO, F_SALMON),
        rito("baja_otono", "09-20", "Bajan de la meseta al abrigo del río", certeza = APROXIMADO),
        cosecha("renos", "10-01", "La caza de otoño: los renos cruzan el vado", 20, APROXIMADO, F_RENO, escena = "caza"),
        rito("coser", "11-01", "Se cose la ropa de invierno con agujas de hueso", certeza = APROXIMADO, fuente = F_AGUJA),
        mundo("primera_nieve", "11-20", "La primera nevada que se queda", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
        fiesta("noche_larga", "12-21", "La noche más larga: fuego grande y cantos hasta que vuelve la luz", certeza = INVENTADO, escena = "fuego"),
    )

    val AMA = Calendario(
        vida = "pintora_cueva",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 6, nubes = 8, nieve = 12, niebla = 3, viento = 3), mes(despejado = 6, nubes = 8, nieve = 11, niebla = 2, viento = 3),
            mes(despejado = 7, nubes = 9, nieve = 8, niebla = 3, viento = 4), mes(despejado = 8, nubes = 10, lluvia = 5, nieve = 3, niebla = 2, viento = 2),
            mes(despejado = 11, nubes = 10, lluvia = 7, viento = 2), mes(despejado = 15, nubes = 8, lluvia = 5, tormenta = 2),
            mes(despejado = 18, nubes = 7, lluvia = 4, tormenta = 2), mes(despejado = 17, nubes = 7, lluvia = 4, tormenta = 2),
            mes(despejado = 12, nubes = 9, lluvia = 6, niebla = 3), mes(despejado = 8, nubes = 10, lluvia = 6, nieve = 2, niebla = 5),
            mes(despejado = 6, nubes = 9, nieve = 7, niebla = 6, viento = 2), mes(despejado = 6, nubes = 8, nieve = 11, niebla = 3, viento = 3),
        ),
        temporadas = listOf(
            TemporadaVida("El año del caballo rojo", "frio",
                "Ama pinta su primer caballo en lo hondo de la cueva. Su madre, Yemi, le enseña a soplar el ocre para dejar la mano en la roca. Pasa a lo lejos una manada de mamuts. Ama espera un hijo. Tor mata un reno con el propulsor desde más lejos que nadie.",
                anioDelValle + listOf(
                    suceso("primer_caballo", "01-15", "Ama pinta su primer caballo en lo hondo de la cueva"),
                    suceso("mano", "02-20", "Su madre le enseña a soplar el ocre para dejar la mano en la roca"),
                    mundo("mamuts", "05-05", "Pasa a lo lejos una manada de mamuts", certeza = APROXIMADO, fuente = F_MAMUT),
                    suceso("ama_espera", "08-15", "Ama sabe que espera un hijo"),
                    suceso("tiro_tor", "10-10", "Tor mata un reno con el propulsor desde más lejos que nadie"),
                )),
            TemporadaVida("El año de la niña", "leon",
                "Un león de las cavernas ronda el abrigo en lo más frío. Nace Lin y le dan nombre junto al fuego. Ama talla una lámpara nueva de piedra. El viejo Oko cuenta la historia de los primeros pintores.",
                anioDelValle + listOf(
                    mundo("leon", "01-25", "Un león de las cavernas ronda el abrigo: nadie sale de noche", certeza = APROXIMADO, fuente = F_LEON),
                    suceso("nace_lin", "04-20", "Nace su hija Lin", cadena = "lin"),
                    suceso("nombre_lin", "04-28", "Le dan nombre a la niña junto al fuego", cadena = "lin"),
                    suceso("lampara", "08-10", "Talla una lámpara nueva de piedra, con mango"),
                    suceso("historia_oko", "12-01", "El viejo Oko cuenta la historia de los primeros pintores"),
                )),
            TemporadaVida("El año de la reunión", "reunion",
                "Muere Yemi, la madre de Ama. Lin camina. En verano se juntan varios grupos en la cueva grande del valle de al lado, y Ama conoce a Sel, que pinta bisontes. El friso llega a la mitad.",
                anioDelValle + listOf(
                    suceso("enferma_yemi", "01-24", "Su madre, Yemi, cae enferma con una tos que no se va", 4, cadena = "yemi"),
                    suceso("muere_yemi", "02-10", "Muere Yemi, la que le enseñó a pintar", cadena = "yemi", escena = "luto"),
                    suceso("lin_camina", "04-15", "Lin da sus primeros pasos en el abrigo"),
                    fiesta("reunion", "07-01", "Se juntan varios grupos en la cueva grande del valle de al lado", 5, INVENTADO, escena = "reunion"),
                    suceso("sel", "07-03", "Conoce a Sel, que pinta bisontes en la cueva de otro grupo"),
                    suceso("friso_mitad", "11-10", "El friso de los caballos llega a la mitad de la pared"),
                )),
            TemporadaVida("El año del frío largo", "frio_largo",
                "La nieve no se va y la primavera llega tarde: hay hambre hasta que pasan los saigas. Lin dice sus primeras palabras. Duro caza su primer reno. Ama le enseña a moler el ocre.",
                anioDelValle + listOf(
                    suceso("lin_habla", "02-15", "Lin dice sus primeras palabras"),
                    mundo("nevada_tarde", "03-01", "La nieve no se va: la primavera llega tarde", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    mundo("hambre", "03-25", "Se acaba la carne guardada: se comen las raíces y el sebo de las lámparas", certeza = INVENTADO),
                    suceso("duro_reno", "10-12", "El muchacho Duro mata su primer reno"),
                    suceso("ocre_duro", "12-05", "Ama le enseña a Duro a moler el ocre"),
                )),
            TemporadaVida("El año del friso", "friso",
                "Una noche de invierno el cielo se llena de una luz verde que baila. Lin deja su mano de niña junto a la de su madre. Tor le regala un collar de conchas que vienen del mar. Sel vuelve de visita. El grupo baja a ver el friso terminado.",
                anioDelValle + listOf(
                    mundo("aurora", "01-20", "El cielo se llena de una luz verde que baila", certeza = INVENTADO, escena = "aurora"),
                    suceso("mano_lin", "03-10", "Lin deja la mano de niña junto a la de su madre, en la roca"),
                    suceso("collar", "06-20", "Tor le regala un collar de conchas que vienen del mar"),
                    suceso("sel_vuelve", "08-05", "Sel viene de visita al campamento de verano"),
                    rito("friso_fin", "10-28", "El grupo baja a lo hondo de la cueva a ver el friso terminado", certeza = INVENTADO, escena = "fuego"),
                )),
        ),
        arcos = listOf(
            Arco("friso", TipoArco.OBRA, listOf(p(1, "01-15", 0.0), p(1, "12-31", 0.15), p(2, "12-31", 0.35), p(3, "12-31", 0.6), p(4, "12-31", 0.8), p(5, "10-28", 1.0))),
            Arco.cadaAnio("nieve", TipoArco.OTRO, "01-15" to 1.0, "03-15" to 0.7, "04-25" to 0.0, "10-31" to 0.0, "11-20" to 0.2),
        ),
        objetos = listOf("propulsor", "azagaya", "aguja", "lampara", "ocre", "carbon", "piel", "collar", "flauta", "pedernal", "asta", "odre"),
        estados = listOf(Estado("verano", "sube_verano", "baja_otono")),
    )

    // =====================================================================
    //  K'an, escriba y pintor de códices. Una ciudad maya de las tierras bajas, del 750 al 754.
    // =====================================================================
    //
    // Su gente (toda inventada): Ix Sak, su mujer, tejedora. Ch'ok, su hijo (6 años en la temporada 1). La hija que nace
    // en la temporada 3. Su viejo maestro, el escriba Ah K'in, que muere en la 3. El señor de la ciudad, el ajaw, para
    // quien pinta. La ciudad no tiene nombre.
    //
    // Lo real: el año del haab (los cinco días sin nombre y el año nuevo), los fines de tun y el fin del k'atun
    // 9.16.0.0.0 (751), la subida al trono de Pájaro Jaguar IV en Yaxchilan (752), el juego de pelota, Venus como
    // lucero de la mañana (calculado) y el año del maíz. Todo pasado al juliano con la correlación GMT. [Aproximación]
    //
    // Claves de escena: fiesta, copal (el humo del incienso), quema (la milpa), luto, tormenta, guerra. Arcos: estela (la
    // estela del fin del k'atun que se talla en la plaza: 1 = levantada) y milpa (el maíz). Estado: «embajada»
    // (temporada 3: viaja en canoa a Yaxchilan con los regalos de su señor).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la estela con andamio y piedra a medio
    // tallar según `estela`; la milpa según `milpa` y humo en abril cuando la queman; y la casa del escriba sin él
    // mientras el estado es «embajada».

    private val F_HAAB = "El haab" to "${W}Haab%27"
    private val F_CUENTA_LARGA = "La cuenta larga" to "${W}Mesoamerican_Long_Count_calendar"
    private val F_PELOTA = "El juego de pelota" to "${W}Mesoamerican_ballgame"
    private val F_DRESDE = "El Códice de Dresde y las tablas de Venus" to "${W}Dresden_Codex"
    private val F_PAJARO_JAGUAR = "Pájaro Jaguar IV" to "${W}Bird_Jaguar_IV"
    private val F_ESTELAS = "Las estelas mayas" to "${W}Maya_stelae"
    private val F_CACAO = "El cacao" to "${W}Theobroma_cacao"
    private val F_DIOS_MAIZ = "El dios del maíz" to "${W}Maya_maize_god"

    // El año maya en el juliano, del 750 al 754 (correlación GMT 584283; ver `fuentes.md`).
    private val WAYEB = listOf("01-25", "01-25", "01-25", "01-24", "01-24")
    private val POP = listOf("01-30", "01-30", "01-30", "01-29", "01-29")
    private val FIN_TUN = listOf("05-08", null, null, "04-22", "04-17")   // en la 2 es el fin del k'atun; en la 3 está fuera

    /** Lo que vuelve cada año en la ciudad, en la temporada [t]. */
    private fun anioDeLaCiudad(t: Int): List<DiaEspecial> = listOfNotNull(
        rito("wayeb", WAYEB[t - 1], "Los cinco días sin nombre: no se trabaja, no se sale, no se pelea", 5, APROXIMADO, F_HAAB),
        fiesta("pop", POP[t - 1], "El año nuevo del haab: se renueva todo lo de la casa", certeza = APROXIMADO, fuente = F_HAAB),
        fiesta("juego_pelota", "03-10", "El gran juego de pelota en la cancha de la plaza", certeza = APROXIMADO, fuente = F_PELOTA),
        cosecha("quema", "04-05", "La quema de la milpa: el humo tapa el sol varios días", 7, APROXIMADO, escena = "quema"),
        FIN_TUN[t - 1]?.let { fiesta("fin_tun", it, "El fin de un tun: el señor esparce incienso y se cuentan los años", certeza = APROXIMADO, fuente = F_CUENTA_LARGA, escena = "copal") },
        if (t == 3) null else rito("chaak", "05-12", "Le piden lluvia a Chaak en la cueva del cerro", certeza = APROXIMADO),
        cosecha("siembra", "05-20", "La siembra del maíz con las primeras lluvias", 10, APROXIMADO),
        fiesta("elotes", "08-15", "Los primeros elotes: se le ofrecen al dios del maíz", certeza = APROXIMADO, fuente = F_DIOS_MAIZ),
        cosecha("cosecha", "10-15", "La cosecha del maíz", 20, APROXIMADO),
        cosecha("cacao", "12-01", "La cosecha del cacao en las huertas del señor", 15, APROXIMADO, F_CACAO),
    )

    val KAN = Calendario(
        vida = "escriba_maya",
        estaciones = listOf("05-20" to Estacion.LLUVIAS, "11-01" to Estacion.SECAS),
        clima = listOf(
            mes(despejado = 18, nubes = 8, lluvia = 3, niebla = 2), mes(despejado = 20, nubes = 7, lluvia = 2, niebla = 1),
            mes(despejado = 22, nubes = 6, lluvia = 2, polvo = 1), mes(despejado = 22, nubes = 5, lluvia = 2, polvo = 2),
            mes(despejado = 15, nubes = 8, lluvia = 5, tormenta = 3), mes(despejado = 7, nubes = 8, lluvia = 10, tormenta = 5),
            mes(despejado = 7, nubes = 9, lluvia = 10, tormenta = 4), mes(despejado = 7, nubes = 9, lluvia = 10, tormenta = 4),
            mes(despejado = 5, nubes = 9, lluvia = 11, tormenta = 5), mes(despejado = 8, nubes = 9, lluvia = 10, tormenta = 3),
            mes(despejado = 14, nubes = 9, lluvia = 5, niebla = 2), mes(despejado = 17, nubes = 8, lluvia = 4, niebla = 2),
        ),
        temporadas = listOf(
            TemporadaVida("750", "estela",
                "El señor encarga la estela del fin del k'atun y K'an dibuja sus glifos. Llegan comerciantes de la costa. Le perforan las orejas a Ch'ok. Las lluvias se cortan en julio. Su maestro le enseña los glifos de la cuenta larga. Ix Sak termina una manta para el señor.",
                anioDeLaCiudad(1) + listOf(
                    suceso("encargo_estela", "02-20", "El señor le encarga los glifos de la estela del fin del k'atun"),
                    mundo("comerciantes", "03-25", "Llegan comerciantes de la costa con sal, conchas y plumas", certeza = INVENTADO),
                    suceso("orejas_chok", "06-15", "Le perforan las orejas a su hijo Ch'ok, como a los niños de su linaje"),
                    mundo("sequia", "07-10", "Las lluvias se cortan: la milpa se dobla y se reza por agua", certeza = INVENTADO),
                    suceso("maestro_glifos", "09-10", "Su viejo maestro le enseña a escribir las fechas de la cuenta larga"),
                    suceso("manta", "11-05", "Ix Sak termina una manta bordada para el señor"),
                )),
            TemporadaVida("751", "katun",
                "Venus sale como lucero de la mañana. K'an pinta un vaso para el señor con su nombre en el borde. Se cierra el k'atun y se levanta la estela con sus glifos. El señor le manda pintar los vasos para la fiesta de su hijo. Ix Sak espera un hijo. Un huracán tumba la milpa. Ch'ok aprende sus primeros glifos.",
                anioDeLaCiudad(2) + listOf(
                    mundo("venus", "01-18", "Venus sale otra vez como lucero de la mañana: días de peligro", certeza = APROXIMADO, fuente = F_DRESDE),
                    suceso("vaso_senor", "03-15", "Pinta un vaso para el señor, con su nombre en el borde"),
                    fiesta("fin_katun", "05-03", "Se cierra un k'atun: el señor levanta la estela y la plaza se llena de copal", 2, APROXIMADO, F_ESTELAS, escena = "copal"),
                    suceso("vasos_fiesta", "06-20", "El señor le manda pintar los vasos para la fiesta de su hijo"),
                    suceso("ix_sak_espera", "08-01", "Ix Sak le dice que espera un hijo"),
                    mundo("huracan", "09-05", "Un huracán tumba la milpa y arranca techos de palma", certeza = INVENTADO, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("chok_glifos", "09-20", "Ch'ok pinta sus primeros glifos en una tablilla de estuco"),
                )),
            TemporadaVida("752", "embajada",
                "Nace su hija. K'an viaja en canoa a Yaxchilan con los regalos de su señor y ve subir al trono a Pájaro Jaguar IV. Muere su viejo maestro. Venus vuelve a salir por la mañana. En noviembre se ve humo al oeste: una ciudad vecina está en guerra.",
                anioDeLaCiudad(3) + listOf(
                    suceso("nace_hija", "03-20", "Nace su hija"),
                    suceso("sale_embajada", "04-12", "Sale en canoa río abajo con los regalos de su señor para Yaxchilan", cadena = "embajada"),
                    mundo("entronizacion", "04-27", "En Yaxchilan sube al trono Pájaro Jaguar IV, y K'an lo ve", certeza = APROXIMADO, fuente = F_PAJARO_JAGUAR, escena = "fiesta"),
                    suceso("vuelve_embajada", "05-15", "Vuelve a su ciudad, con regalos de Yaxchilan para su señor", cadena = "embajada"),
                    suceso("vaso_ajeno", "06-05", "Entre los regalos de Yaxchilan hay un vaso pintado por otro escriba, mejor que él"),
                    suceso("muere_maestro", "07-15", "Muere su viejo maestro, el escriba Ah K'in", escena = "luto"),
                    mundo("venus_752", "08-21", "Venus sale como lucero de la mañana", certeza = APROXIMADO, fuente = F_DRESDE),
                    guerra("humo_oeste", "11-20", "Se ve humo al oeste: una ciudad vecina está en guerra", certeza = INVENTADO, escena = "guerra"),
                )),
            TemporadaVida("753", "guerra",
                "Los guerreros de la ciudad vuelven con cautivos. La hija de K'an camina. K'an firma un vaso con su nombre. Ch'ok prepara solo el papel de amate. K'an empieza a copiar un almanaque de Venus.",
                anioDeLaCiudad(4) + listOf(
                    guerra("cautivos", "02-20", "Los guerreros de la ciudad vuelven de la guerra con cautivos", certeza = INVENTADO, escena = "guerra"),
                    suceso("hija_camina", "03-28", "Su hija da sus primeros pasos en el patio"),
                    suceso("firma_vaso", "06-10", "Firma un vaso con su nombre: «lo pintó K'an»"),
                    suceso("chok_amate", "09-15", "Ch'ok prepara solo el papel de amate, con cal y corteza"),
                    suceso("almanaque", "11-20", "Empieza a copiar un almanaque de Venus para el templo"),
                    mundo("lluvia_tarde", "06-25", "Las lluvias llegan tarde: el maíz nace ralo", certeza = INVENTADO),
                )),
            TemporadaVida("754", "venus",
                "Ch'ok mezcla su primera tinta roja. Venus sale por la mañana. La hija de K'an habla. K'an termina el almanaque de Venus. Ix Sak vende sus mantas en el mercado.",
                anioDeLaCiudad(5) + listOf(
                    suceso("tinta_chok", "03-05", "Ch'ok mezcla su primera tinta roja, con hematita"),
                    mundo("venus_754", "03-31", "Venus sale como lucero de la mañana", certeza = APROXIMADO, fuente = F_DRESDE),
                    suceso("hija_habla", "06-01", "Su hija dice sus primeras palabras"),
                    suceso("almanaque_fin", "08-25", "Termina el almanaque de Venus y lo lleva al templo"),
                    suceso("mantas", "10-20", "Ix Sak vende sus mantas en el mercado y compra cacao"),
                    mundo("murcielagos", "07-20", "Una tormenta saca de la cueva una nube de murciélagos", certeza = INVENTADO, clima = Clima.TORMENTA),
                )),
        ),
        arcos = listOf(
            Arco("estela", TipoArco.OBRA, listOf(p(1, "02-20", 0.0), p(2, "05-03", 1.0))),
            Arco.cadaAnio("milpa", TipoArco.CULTIVO, "04-05" to 0.0, "05-20" to 0.05, "08-15" to 0.8, "10-15" to 1.0, "11-05" to 0.0),
        ),
        objetos = listOf("pincel", "concha_tinta", "codice", "vaso_cacao", "copal", "orejera", "petate", "tamal", "manta", "cuchillo", "caracol", "amate"),
        estados = listOf(Estado("embajada", "sale_embajada", "vuelve_embajada")),
    )

    // =====================================================================
    //  Nanai-vandak, mercader sogdiano de caravana. Entre Samarcanda y Chang'an, del 750 al 754.
    // =====================================================================
    //
    // Su gente (toda inventada): Takhsich, su mujer, que se queda en Samarcanda. Farn, su hijo (16 años en la temporada
    // 1), que va con él desde la 3 y se queda en Chang'an como su agente. Nanai-dhat, su hija, que se casa en la 2. Shi
    // Ping, un sogdiano de Tashkent que tiene tienda en el mercado del oeste de Chang'an. Tughan, el muchacho turco que
    // cuida los camellos. En Chang'an lo llaman Kang, como a todos los de Samarcanda.
    //
    // Lo real: la revolución de los abasíes (750), la toma de Tashkent por Gao Xianzhi (750), la batalla de Talas
    // (751), el primer taller de papel de Samarcanda (751), Abu Muslim, la guerra de los Tang contra el Tíbet (753), la
    // muerte del califa as-Saffah (754), el censo de la capital (754), el general An Lushan, y las fiestas chinas que
    // siguen la luna (calculadas). Que lo sepa esos días es inventado.
    //
    // El camino: en invierno y primavera viaja; en verano está en Chang'an (temporadas 1, 3 y 5) o en Samarcanda (2 y
    // 4); en otoño vuelve a salir. El año nuevo chino y la fiesta de las linternas lo agarran siempre en el camino.
    //
    // Claves de escena: fiesta, linternas, polvo, nieve, guerra, luto. Arco: ruta (dónde está: 0 = Samarcanda, 1 =
    // Chang'an; en medio, los oasis, los desiertos y los pasos). Estados: «changan» y «samarcanda» (el verano en cada
    // ciudad); el resto del año está en el camino.
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la caravana y el caravasar, con el paisaje
    // que diga `ruta` (estepa y montañas cerca de 0, desierto y oasis en medio, el corredor de loess cerca de 1); la
    // ciudad y su mercado mientras el estado es «changan» o «samarcanda».

    private val F_ANO_CHINO = "El año nuevo chino" to "${W}Chinese_New_Year"
    private val F_LINTERNAS = "La fiesta de las linternas" to "${W}Lantern_Festival"
    private val F_DUANWU = "La fiesta del barco dragón" to "${W}Dragon_Boat_Festival"
    private val F_QIXI = "Qixi" to "${W}Qixi_Festival"
    private val F_ZHONGQIU = "La fiesta de la luna de otoño" to "${W}Mid-Autumn_Festival"
    private val F_SOGDIA = "Sogdiana" to "${W}Sogdia"
    private val F_ABASIES = "La revolución abasí" to "${W}Abbasid_Revolution"
    private val F_GAO = "Gao Xianzhi" to "${W}Gao_Xianzhi"
    private val F_TALAS = "La batalla de Talas" to "${W}Battle_of_Talas"
    private val F_751 = "El año 751" to "${W}751"
    private val F_ABU_MUSLIM = "Abu Muslim" to "${W}Abu_Muslim"
    private val F_GESHU = "Geshu Han" to "${W}Geshu_Han"
    private val F_MOGAO = "Las cuevas de Mogao" to "${W}Mogao_Caves"
    private val F_754 = "El año 754" to "${W}754"
    private val F_AN_LUSHAN = "An Lushan" to "${W}An_Lushan"

    // Las fiestas chinas, en el juliano, del 750 al 754 (ver `fuentes.md`).
    private val ANO_CHINO = listOf("02-11", "02-01", "01-21", "02-08", "01-28")
    private val LINTERNAS = listOf("02-25", "02-15", "02-04", "02-22", "02-11")
    private val DUANWU = listOf("06-13", null, "06-20", null, "05-31")
    private val QIXI = listOf("08-13", null, "08-20", null, "07-30")
    private val ZHONGQIU = listOf("09-19", null, "09-26", null, "09-06")

    /** Lo que vuelve cada año, en la temporada [t]: el invierno en el camino y el verano en una de las dos ciudades. */
    private fun anioDeLaRuta(t: Int): List<DiaEspecial> = listOfNotNull(
        fiesta("ano_chino", ANO_CHINO[t - 1], "El año nuevo de los chinos, en un caravasar del camino", 2, APROXIMADO, F_ANO_CHINO, escena = "fiesta"),
        fiesta("linternas", LINTERNAS[t - 1], "La fiesta de las linternas, en el oasis", certeza = APROXIMADO, fuente = F_LINTERNAS, escena = "linternas"),
        mundo("tormenta_negra", "03-20", "Una tormenta de arena negra para la caravana un día entero", certeza = APROXIMADO, escena = "polvo", clima = Clima.POLVO),
        DUANWU[t - 1]?.let { fiesta("duanwu", it, "La fiesta del barco dragón en los estanques de Chang'an", certeza = APROXIMADO, fuente = F_DUANWU) },
        QIXI[t - 1]?.let { fiesta("qixi", it, "Qixi: las muchachas de Chang'an le piden habilidad a la tejedora del cielo", certeza = APROXIMADO, fuente = F_QIXI) },
        ZHONGQIU[t - 1]?.let { fiesta("zhongqiu", it, "La luna de otoño: se mira la luna con vino en los tejados de Chang'an", certeza = APROXIMADO, fuente = F_ZHONGQIU) },
        if (t == 2 || t == 4) cosecha("vendimia", "09-01", "La vendimia en los viñedos de Samarcanda: vino nuevo", 10, APROXIMADO, F_SOGDIA) else null,
    )

    val NANAI = Calendario(
        vida = "mercader_sogdiano",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 18, nubes = 6, nieve = 4, viento = 3), mes(despejado = 17, nubes = 6, nieve = 3, viento = 3, polvo = 1),
            mes(despejado = 16, nubes = 7, lluvia = 2, viento = 3, polvo = 3), mes(despejado = 16, nubes = 7, lluvia = 3, viento = 2, polvo = 3),
            mes(despejado = 19, nubes = 5, lluvia = 3, tormenta = 1, polvo = 2), mes(despejado = 20, nubes = 4, lluvia = 3, tormenta = 2, polvo = 1),
            mes(despejado = 21, nubes = 4, lluvia = 3, tormenta = 2), mes(despejado = 21, nubes = 4, lluvia = 3, tormenta = 2),
            mes(despejado = 21, nubes = 5, lluvia = 2, viento = 2), mes(despejado = 20, nubes = 6, lluvia = 1, viento = 2, polvo = 1),
            mes(despejado = 18, nubes = 7, nieve = 2, viento = 3), mes(despejado = 18, nubes = 6, nieve = 4, viento = 3),
        ),
        temporadas = listOf(
            TemporadaVida("750", "abasies",
                "En el camino hacia el este se le muere un camello y llega la noticia de que los abasíes vencieron a los omeyas. Pasa el verano en Chang'an: compra seda, escribe a casa, ve bailar la danza del torbellino y se entera de que Gao Xianzhi tomó Tashkent. En otoño sale hacia el oeste; en Dunhuang enciende una lámpara a los budas.",
                anioDeLaRuta(1) + listOf(
                    suceso("camello", "01-20", "Se le muere un camello en el desierto, con la carga de plata"),
                    mundo("abasies", "03-01", "Llega con una caravana del oeste la noticia: los abasíes vencieron a los omeyas", certeza = APROXIMADO, fuente = F_ABASIES),
                    suceso("llega_changan", "04-15", "Llega a Chang'an: el mercado del oeste, el barrio de los sogdianos y la casa de Shi Ping"),
                    suceso("compra_seda", "05-10", "Compra seda de Sichuan en el mercado del oeste"),
                    suceso("carta_casa", "06-25", "Escribe una carta a Takhsich, que tardará un año en llegar"),
                    mundo("tashkent", "07-20", "Llega la noticia: el general Gao Xianzhi tomó Tashkent a traición", certeza = APROXIMADO, fuente = F_GAO),
                    suceso("torbellino", "08-25", "En una taberna ve bailar a una muchacha de Samarcanda la danza del torbellino"),
                    suceso("sale_changan", "09-22", "Sale de Chang'an hacia el oeste con los camellos cargados de seda"),
                    rito("mogao", "11-20", "En Dunhuang enciende una lámpara en las cuevas de los mil budas", certeza = APROXIMADO, fuente = F_MOGAO),
                    mundo("nieve_paso", "12-15", "Nieva en el paso: la caravana espera tres días en un caravasar", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                )),
            TemporadaVida("751", "talas",
                "Cruza las montañas nevadas hacia Kashgar y llega a Samarcanda en abril. Se habla de Abu Muslim. Hace las cuentas con sus socios. Farn le pide ir con la caravana. Casa a su hija. Llega la noticia de la batalla de Talas, y luego unos prisioneros chinos hacen papel en Samarcanda. En otoño vuelve al camino.",
                anioDeLaRuta(2) + listOf(
                    suceso("kashgar", "03-05", "Cruza las montañas nevadas y baja a Kashgar"),
                    suceso("llega_samarcanda", "04-25", "Llega a Samarcanda: Takhsich lo espera en la puerta"),
                    mundo("abu_muslim", "05-05", "Se habla de Abu Muslim, el gobernador, que aplastó una revuelta en Bujará", certeza = APROXIMADO, fuente = F_ABU_MUSLIM),
                    suceso("cuentas", "05-20", "Hace las cuentas del viaje con sus socios: la seda dejó ganancia"),
                    suceso("farn_pide", "06-15", "Farn le pide ir con él en la próxima caravana"),
                    suceso("boda_hija", "07-10", "Casa a su hija Nanai-dhat con el hijo de otro mercader", escena = "fiesta"),
                    guerra("talas", "08-15", "Llega la noticia: los árabes vencieron a los chinos junto al río Talas", certeza = APROXIMADO, fuente = F_TALAS),
                    mundo("papel", "09-12", "Unos prisioneros chinos hacen papel en un taller de Samarcanda", certeza = APROXIMADO, fuente = F_751),
                    suceso("sale_samarcanda", "09-28", "Sale de Samarcanda hacia el este, con Farn"),
                    mundo("pamir", "11-01", "La caravana cruza los pasos altos con la primera nieve", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    suceso("turfan", "12-10", "Pasa el invierno en Turfan, entre viñas enterradas"),
                    fiesta("templo_fuego", "06-01", "La fiesta del templo del fuego, con música y vino", certeza = INVENTADO),
                )),
            TemporadaVida("752", "farn",
                "Farn aprende a cargar un camello y llegan a Chang'an en abril. Juegan al polo en la ciudad. Farn aprende el mercado y se enamora de la hija de un sogdiano de Chang'an. El río Wei moja el mercado. Nanai-vandak deja a Farn como su agente y vuelve al oeste; en Kucha oye a los mejores músicos de la ruta.",
                anioDeLaRuta(3) + listOf(
                    suceso("farn_camello", "01-05", "Farn aprende a cargar un camello sin que se caiga la carga"),
                    suceso("llega_changan", "04-25", "Llega a Chang'an con Farn, que nunca ha visto una ciudad tan grande"),
                    mundo("polo", "05-05", "Juegan al polo en la explanada del palacio, y media ciudad va a verlo", certeza = INVENTADO),
                    suceso("farn_mercado", "05-20", "Farn aprende a regatear en el mercado del oeste"),
                    suceso("seda_fina", "06-01", "Compra seda fina de la corte, de la que no se vende"),
                    suceso("farn_novia", "07-15", "Farn se enamora de la hija de Shi Ping"),
                    mundo("crecida_wei", "07-25", "El río Wei crece y el agua entra en el mercado del oeste", certeza = INVENTADO, clima = Clima.LLUVIA),
                    suceso("sale_changan", "09-28", "Sale de Chang'an hacia el oeste; Farn se queda como su agente"),
                    mundo("nieve_hexi", "10-25", "Nieva en el corredor del oeste: la caravana se abriga en un fuerte", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    suceso("kucha", "12-20", "En Kucha oye a los mejores músicos de la ruta"),
                )),
            TemporadaVida("753", "tibet",
                "Unos bandidos atacan la caravana en el desierto. Llega a Samarcanda en abril, vende la seda fina y recibe carta de Farn. Nace su primer nieto. Llega la noticia de que los chinos le quitaron tierras al Tíbet. En otoño sale otra vez; en Dunhuang enciende otra lámpara.",
                anioDeLaRuta(4) + listOf(
                    guerra("bandidos", "01-10", "Unos bandidos atacan la caravana en el desierto; los guardias los espantan", certeza = INVENTADO),
                    suceso("camello_blanco", "03-05", "Compra un camello blanco de dos jorobas, el más caro del mercado de Kashgar"),
                    suceso("llega_samarcanda", "04-28", "Llega a Samarcanda: su nieto está por nacer"),
                    suceso("venta", "05-15", "Vende la seda fina de la corte a un mercader de Persia"),
                    suceso("carta_farn", "06-10", "Le llega una carta de Farn desde Chang'an, que tardó un año"),
                    fiesta("templo_fuego", "06-01", "La fiesta del templo del fuego, con música y vino", certeza = INVENTADO),
                    mundo("caravana_india", "07-20", "Llega a Samarcanda una caravana de la India con pimienta y algodón", certeza = INVENTADO),
                    mundo("tibet", "07-01", "Llega la noticia: los chinos le quitaron al Tíbet las nueve curvas del río Amarillo", certeza = APROXIMADO, fuente = F_GESHU),
                    suceso("nieto", "08-05", "Nace su primer nieto"),
                    suceso("sale_samarcanda", "09-25", "Sale de Samarcanda hacia el este"),
                    mundo("pamir", "11-01", "La caravana cruza los pasos altos con la primera nieve", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    rito("mogao", "12-20", "En Dunhuang enciende una lámpara en las cuevas de los mil budas", certeza = APROXIMADO, fuente = F_MOGAO),
                )),
            TemporadaVida("754", "an_lushan",
                "Llega a Chang'an en abril y Farn lo espera en la puerta. Se dice que la capital tiene dos millones de habitantes. En el mercado se habla del general An Lushan, que es medio sogdiano. Farn se casa con la hija de Shi Ping. Llega la noticia de la muerte del califa. En otoño vuelve al oeste.",
                anioDeLaRuta(5) + listOf(
                    suceso("llega_changan", "04-20", "Llega a Chang'an: Farn lo espera en la puerta del oeste"),
                    mundo("censo", "05-10", "Se dice que en la capital viven dos millones de personas", certeza = APROXIMADO, fuente = F_754),
                    suceso("seda_fina", "05-25", "Compra seda fina de la corte, como la otra vez"),
                    mundo("an_lushan", "06-15", "En el mercado se habla del general An Lushan, que es medio sogdiano y muy poderoso", certeza = APROXIMADO, fuente = F_AN_LUSHAN),
                    suceso("nieto_camina", "06-01", "Le llega carta de Samarcanda: su nieto ya camina"),
                    suceso("boda_farn", "07-10", "Farn se casa con la hija de Shi Ping", escena = "fiesta"),
                    mundo("califa", "08-01", "Llega la noticia: murió el califa as-Saffah", certeza = APROXIMADO, fuente = F_754),
                    suceso("sale_changan", "09-15", "Sale de Chang'an hacia el oeste, sin Farn"),
                    suceso("liangzhou", "10-20", "En Liangzhou cena con los sogdianos del corredor, que le preguntan por Farn"),
                    mundo("nieve_hexi", "12-01", "Nieva en el corredor del oeste: la caravana se abriga en un fuerte", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                )),
        ),
        arcos = listOf(
            Arco("ruta", TipoArco.OTRO, listOf(
                p(1, "01-01", 0.6), p(1, "04-15", 1.0), p(1, "09-22", 1.0), p(1, "12-31", 0.45),
                p(2, "04-25", 0.0), p(2, "09-28", 0.0), p(2, "12-31", 0.45),
                p(3, "04-25", 1.0), p(3, "09-28", 1.0), p(3, "12-31", 0.45),
                p(4, "04-28", 0.0), p(4, "09-25", 0.0), p(4, "12-31", 0.45),
                p(5, "04-20", 1.0), p(5, "09-15", 1.0), p(5, "12-31", 0.6),
            )),
        ),
        objetos = listOf("seda", "monedas", "almizcle", "vidrio", "vino", "carta", "balanza", "campana", "laud", "sello", "fieltro", "te"),
        estados = listOf(Estado("changan", "llega_changan", "sale_changan"), Estado("samarcanda", "llega_samarcanda", "sale_samarcanda")),
    )
}
