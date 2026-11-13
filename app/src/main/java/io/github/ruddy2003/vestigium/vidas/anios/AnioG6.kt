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
 * Los calendarios del grupo 6 (tanda A6): las cinco temporadas (2027 a 2031) de Albert (Vapor victoriano), Nikos
 * (Abismo), Lou (Art déco), Liudmila (Soviético) y Giulia (Roma). Siguen las reglas de `AnioPiloto.kt`. Qué es real y
 * qué inventado, con cada fuente, está en `docs/historia/fuentes.md`.
 *
 * Como los de los grupos 3 a 5, estos personajes tienen cuadro (tandas S4 y S5), y el comentario de cada calendario dice
 * qué tiene que mostrar para contar lo mismo que el cuento.
 *
 * Lo propio de este grupo:
 *
 * - **Nikos** es griego ortodoxo, y su Iglesia seguía en 1910 el calendario juliano: sus fiestas de la Iglesia caen trece
 *   días después que las de los americanos (la Navidad, el 7 de enero). La Pascua ortodoxa está calculada.
 * - **Albert y Lou** siguen el calendario de su país; su Pascua está calculada. Los días de fiesta de Albert son los días
 *   en que más se trabaja: los trenes de excursión de los días de fiesta de los bancos.
 * - **Liudmila** sigue las fiestas soviéticas. El día de la Constitución pasa del 5 de diciembre al 7 de octubre con la
 *   Constitución nueva de 1977.
 * - **Giulia** vive de 2025 a 2029: 2025 ya pasó, 2026 es el año en que se escribe esto y 2027 a 2029 son futuro. En el
 *   futuro solo hay fiestas que vuelven cada año y cosas ya programadas, marcadas como «aproximado»; lo que de verdad
 *   pase se corrige en las tandas Y de cada año.
 * - Varios estados duran hasta el último día del año: como en el grupo 5, ese último día tiene su propio id y un estado
 *   de un día que lo cubre (un estado termina el día anterior a su «hasta»).
 */
internal object AnioG6 {
    val TODOS: List<Calendario> by lazy { listOf(ALBERT, NIKOS, LOU, LIUDMILA, GIULIA) }

    /** Días «históricos» sin enlace porque su fecha sale de un cálculo hecho en la sesión: no hay ninguno nuevo. */
    val CALCULADOS = emptySet<String>()

    private val W = "https://en.wikipedia.org/wiki/"

    private val F_ANIO_NUEVO = "El año nuevo" to "${W}New_Year%27s_Day"
    private val F_NAVIDAD = "La Navidad" to "${W}Christmas"

    // =====================================================================
    //  Albert, fogonero de locomotora. Una línea del noreste de Inglaterra, de 1880 a 1884.
    // =====================================================================
    //
    // Su gente (toda inventada): Hebburn, el maquinista viejo de su máquina, que le enseña el oficio. Ellen, que conoce
    // en la temporada 1, con quien se promete en la 2 y se casa en la 3; su hija Annie nace en la 4. Su madre, viuda, y su
    // hermano pequeño Tom, que baja a la mina en la 2. Los del depósito.
    //
    // Lo real: los días de fiesta de los bancos (desde 1871) y sus trenes de excursión; la gala de los mineros de Durham;
    // la noche de Guy Fawkes; el puente del Tay (1879); las elecciones de 1880; las leyes de la escuela (1880), de los
    // accidentes de trabajo (1880), de los bienes de las casadas (1882) y del voto (1884); el censo de 1881; el zar
    // asesinado y la muerte de Disraeli (1881); el vendaval de octubre de 1881; Egipto (1882); el teatro de Sunderland y
    // los atardeceres de Krakatoa (1883); Jartum, el temblor de Colchester, el accidente de Penistone y el meridiano de
    // Greenwich (1884). Que se sepa en el depósito esos días es inventado.
    //
    // Claves de escena: fiesta, nieve, luto, viento, excursion. Arco: viaducto (el viaducto nuevo sobre el valle, que se
    // levanta de 1880 a 1883). Estados: «baja» (temporada 3: un mes en casa con el brazo quemado) y «maquinista» (5: desde
    // que aprueba el examen, a los mandos de la máquina).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la máquina en la línea, con el viaducto a
    // medio hacer según `viaducto`; la cocina de su casa durante «baja»; y a Albert al otro lado de la plataforma, en el
    // regulador, durante «maquinista».

    private val F_FIESTAS_BANCOS = "Los días de fiesta de los bancos" to "${W}Bank_holiday"
    private val F_MARTES = "El martes de carnaval" to "${W}Shrove_Tuesday"
    private val F_PENTECOSTES = "El lunes de Pentecostés" to "${W}Whit_Monday"
    private val F_GALA = "La gala de los mineros" to "${W}Durham_Miners%27_Gala"
    private val F_GUY_FAWKES = "La noche de Guy Fawkes" to "${W}Guy_Fawkes_Night"
    private val F_BOXING = "El día de las cajas" to "${W}Boxing_Day"
    private val F_TAY = "El puente del Tay" to "${W}Tay_Bridge_disaster"
    private val F_ELECCIONES_1880 = "Las elecciones de 1880" to "${W}1880_United_Kingdom_general_election"
    private val F_ESCUELA = "La ley de la escuela de 1880" to "${W}Elementary_Education_Act_1880"
    private val F_PATRONOS = "La ley de los accidentes de trabajo" to "${W}Employers%27_Liability_Act_1880"
    private val F_ZAR = "El asesinato del zar" to "${W}Assassination_of_Alexander_II"
    private val F_CENSO = "El censo de 1881" to "${W}1881_United_Kingdom_census"
    private val F_DISRAELI = "Disraeli" to "${W}Benjamin_Disraeli"
    private val F_VENDAVAL = "El vendaval de octubre de 1881" to "${W}Eyemouth_disaster"
    private val F_PHOENIX = "Los asesinatos de Phoenix Park" to "${W}Phoenix_Park_Murders"
    private val F_EGIPTO = "La guerra de Egipto" to "${W}Anglo-Egyptian_War"
    private val F_CASADAS = "La ley de los bienes de las casadas" to "${W}Married_Women%27s_Property_Act_1882"
    private val F_SUNDERLAND = "El teatro de Sunderland" to "${W}Victoria_Hall_disaster"
    private val F_KRAKATOA = "Krakatoa" to "${W}1883_eruption_of_Krakatoa"
    private val F_JARTUM = "El cerco de Jartum" to "${W}Siege_of_Khartoum"
    private val F_COLCHESTER = "El temblor de Colchester" to "${W}1884_Colchester_earthquake"
    private val F_PENISTONE = "El accidente de Penistone" to "${W}Penistone_rail_crash"
    private val F_MERIDIANO = "La conferencia del meridiano" to "${W}International_Meridian_Conference"
    private val F_VOTO = "La ley del voto de 1884" to "${W}Representation_of_the_People_Act_1884"

    // La Pascua gregoriana de 1880 a 1884, y lo que cuelga de ella.
    private val MARTES = listOf("02-10", "03-01", "02-21", "02-06", "02-26")
    private val LUNES_PASCUA = listOf("03-29", "04-18", "04-10", "03-26", "04-14")
    private val LUNES_PENTECOSTES = listOf("05-17", "06-06", "05-29", "05-14", "06-02")
    private val GALA = listOf("07-10", "07-09", "07-08", "07-14", "07-12")                // el segundo sábado de julio
    private val LUNES_AGOSTO = listOf("08-02", "08-01", "08-07", "08-06", "08-04")       // el primer lunes de agosto

    /** Lo que vuelve cada año en la línea, en la temporada [t]: en las fiestas de los bancos, los trenes van llenos. */
    private fun anioDeLaLinea(t: Int): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo: el primero que cruza la puerta tiene que ser un hombre moreno", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
        fiesta("martes_tortitas", MARTES[t - 1], "Martes de carnaval: tortitas en todas las cocinas", certeza = HISTORICO, fuente = F_MARTES),
        fiesta("lunes_pascua", LUNES_PASCUA[t - 1], "Lunes de Pascua: trenes de excursión hasta la costa", certeza = HISTORICO, fuente = F_FIESTAS_BANCOS, escena = "excursion"),
        fiesta("lunes_pentecostes", LUNES_PENTECOSTES[t - 1], "Lunes de Pentecostés: las escuelas dominicales se van de excursión", certeza = HISTORICO, fuente = F_PENTECOSTES, escena = "excursion"),
        fiesta("gala", GALA[t - 1], "La gala de los mineros en Durham: trenes llenos de bandas y estandartes", certeza = APROXIMADO, fuente = F_GALA, escena = "excursion"),
        fiesta("lunes_agosto", LUNES_AGOSTO[t - 1], "El lunes de agosto: media comarca se va a la playa en tren", certeza = HISTORICO, fuente = F_FIESTAS_BANCOS, escena = "excursion"),
        fiesta("guy_fawkes", "11-05", "La noche de Guy Fawkes: hogueras y petardos en las calles", certeza = HISTORICO, fuente = F_GUY_FAWKES),
        fiesta("navidad", "12-25", "Navidad", certeza = HISTORICO, fuente = F_NAVIDAD),
        fiesta("boxing", "12-26", "El día de las cajas: aguinaldo para los que trabajan", certeza = HISTORICO, fuente = F_BOXING),
    )

    val ALBERT = Calendario(
        vida = "fogonero",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 5, nubes = 12, lluvia = 7, nieve = 4, niebla = 2, viento = 1), mes(despejado = 6, nubes = 11, lluvia = 6, nieve = 3, niebla = 1, viento = 1),
            mes(despejado = 8, nubes = 11, lluvia = 7, nieve = 2, viento = 3), mes(despejado = 9, nubes = 11, lluvia = 8, viento = 2),
            mes(despejado = 11, nubes = 10, lluvia = 8, niebla = 2), mes(despejado = 12, nubes = 10, lluvia = 8),
            mes(despejado = 12, nubes = 10, lluvia = 8, tormenta = 1), mes(despejado = 11, nubes = 10, lluvia = 9, tormenta = 1),
            mes(despejado = 10, nubes = 10, lluvia = 9, niebla = 1), mes(despejado = 8, nubes = 11, lluvia = 9, niebla = 2, viento = 1),
            mes(despejado = 6, nubes = 12, lluvia = 9, niebla = 2, viento = 1), mes(despejado = 5, nubes = 12, lluvia = 8, nieve = 3, niebla = 2, viento = 1),
        ),
        temporadas = listOf(
            TemporadaVida("1880", "eleccion",
                "En el depósito se habla del puente del Tay. A Albert y a Hebburn les dan una máquina recién salida del taller. Hay elecciones. Empiezan a levantar un viaducto sobre el valle. Albert conoce a Ellen en el té de la capilla. Leyes nuevas de la escuela y de los accidentes. Se apunta en el sindicato. Una nevada los deja atascados toda una noche.",
                anioDeLaLinea(1) + listOf(
                    mundo("tay", "01-09", "En el depósito se habla del puente del Tay, que se hundió con un tren entero", certeza = APROXIMADO, fuente = F_TAY),
                    suceso("maquina", "02-02", "Les dan una máquina recién salida del taller, verde y brillante"),
                    mundo("eleccion", "04-03", "Hay elecciones, y en la taberna se discute a gritos por Gladstone", certeza = APROXIMADO, fuente = F_ELECCIONES_1880),
                    mundo("viaducto", "05-03", "Empiezan a levantar un viaducto nuevo sobre el valle del río", certeza = INVENTADO),
                    suceso("ellen", "05-22", "Conoce a Ellen en el té de la capilla metodista"),
                    mundo("escuela", "08-28", "Una ley nueva manda a la escuela a todos los niños hasta los diez años", certeza = APROXIMADO, fuente = F_ESCUELA),
                    mundo("patronos", "09-08", "Una ley nueva obliga a las compañías a pagar por los accidentes de sus obreros", certeza = APROXIMADO, fuente = F_PATRONOS),
                    suceso("sindicato", "10-16", "Se apunta en el sindicato de los ferroviarios"),
                    mundo("nieve", "12-08", "Una nevada deja su tren atascado toda la noche en una trinchera de la línea", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                )),
            TemporadaVida("1881", "censo",
                "Albert le pide a Ellen que se case con él. Matan al zar de Rusia. La noche del censo. Muere Disraeli. Tom, su hermano, baja a la mina. Se cierra el primer arco del viaducto. Un vendaval enorme hunde los barcos de pesca de la costa. Un vagón descarrila delante de su máquina.",
                anioDeLaLinea(2) + listOf(
                    suceso("pedida", "02-14", "Le pide a Ellen que se case con él"),
                    mundo("zar", "03-16", "Llega la noticia: mataron al zar de Rusia con una bomba", certeza = APROXIMADO, fuente = F_ZAR),
                    mundo("censo", "04-03", "La noche del censo: hay que apuntar quién duerme en cada casa", certeza = HISTORICO, fuente = F_CENSO),
                    mundo("disraeli", "04-21", "Llega la noticia: murió Disraeli, el viejo primer ministro", certeza = APROXIMADO, fuente = F_DISRAELI),
                    suceso("tom", "06-20", "Su hermano Tom baja a la mina por primera vez"),
                    mundo("primer_arco", "08-20", "Se cierra el primer arco del viaducto nuevo", certeza = INVENTADO),
                    mundo("vendaval", "10-14", "Un vendaval enorme tumba chimeneas y hunde los barcos de pesca de la costa", certeza = HISTORICO, fuente = F_VENDAVAL, escena = "viento", clima = Clima.VIENTO),
                    suceso("descarrila", "11-22", "Un vagón de carbón descarrila delante de su máquina, y frenan a tiempo"),
                )),
            TemporadaVida("1882", "egipto",
                "Albert se casa con Ellen. Matan a dos señores del gobierno en Dublín. Se quema el brazo con el vapor y pasa un mes en casa. Bombardean Alejandría. Una ley nueva para las casadas. Se gana la guerra en Egipto. Ellen espera un hijo.",
                anioDeLaLinea(3) + listOf(
                    suceso("boda", "01-28", "Se casa con Ellen en la capilla, un sábado", escena = "fiesta"),
                    mundo("phoenix", "05-08", "Llega la noticia: mataron a dos señores del gobierno en un parque de Dublín", certeza = APROXIMADO, fuente = F_PHOENIX),
                    suceso("quemadura", "06-20", "Se quema el brazo con el vapor de un grifo roto de la caldera"),
                    mundo("alejandria", "07-13", "Llega la noticia: la flota bombardeó Alejandría", certeza = APROXIMADO, fuente = F_EGIPTO),
                    suceso("vuelve", "07-20", "Vuelve a la plataforma con el brazo todavía vendado"),
                    mundo("casadas", "08-21", "Una ley nueva deja a las casadas guardar lo que es suyo", certeza = APROXIMADO, fuente = F_CASADAS),
                    mundo("egipto", "09-16", "Llega la noticia: se ganó la guerra de Egipto", certeza = APROXIMADO, fuente = F_EGIPTO),
                    suceso("espera", "11-10", "Ellen le dice que espera un hijo"),
                )),
            TemporadaVida("1883", "krakatoa",
                "Una helada revienta los tubos del depósito. Albert y Ellen se mudan a una casa con patio. Nace Annie. La máquina de Albert es la primera que cruza el viaducto. Mueren 183 niños en un teatro de Sunderland. Suspende el examen de maquinista. Los atardeceres se ponen rojos por un volcán del otro lado del mundo.",
                anioDeLaLinea(4) + listOf(
                    mundo("helada", "01-20", "Una helada revienta los tubos del agua del depósito", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    suceso("casa", "04-14", "Se mudan a una casa con patio, en la calle de los ferroviarios"),
                    suceso("nace", "05-02", "Nace su hija, Annie"),
                    suceso("primer_tren", "06-01", "Su máquina es la primera que cruza el viaducto nuevo"),
                    mundo("sunderland", "06-17", "Llega la noticia de Sunderland: murieron 183 niños en un teatro", certeza = APROXIMADO, fuente = F_SUNDERLAND, escena = "luto"),
                    suceso("examen", "09-20", "Se presenta al examen de maquinista y lo suspende"),
                    mundo("krakatoa", "11-28", "Los atardeceres se ponen rojos como un incendio, y se dice que es por un volcán lejano", certeza = APROXIMADO, fuente = F_KRAKATOA),
                )),
            TemporadaVida("1884", "reforma",
                "Albert estudia las reglas de la línea por las noches. Cercan a Gordon en Jartum. Tiembla la tierra en el sur. Annie da sus primeros pasos. Descarrila un expreso cerca de Penistone. Albert aprueba el examen y pasa al regulador. En Washington deciden que el día empieza en Greenwich. Una ley nueva da el voto a más obreros.",
                anioDeLaLinea(5) + listOf(
                    suceso("estudia", "03-05", "Estudia las reglas de la línea por las noches, con Ellen preguntándole"),
                    mundo("jartum", "03-25", "Se dice que el general Gordon está cercado en Jartum", certeza = APROXIMADO, fuente = F_JARTUM),
                    mundo("colchester", "04-23", "Se dice que tembló la tierra en el sur y se cayeron chimeneas e iglesias", certeza = APROXIMADO, fuente = F_COLCHESTER),
                    suceso("annie_anda", "05-10", "Annie da sus primeros pasos en la cocina"),
                    mundo("penistone", "07-17", "Un expreso descarrila cerca de Penistone: se partió un eje de la máquina", certeza = APROXIMADO, fuente = F_PENISTONE, escena = "luto"),
                    suceso("aprobado", "09-10", "Aprueba el examen: ya es maquinista"),
                    mundo("meridiano", "10-24", "Se dice que en Washington decidieron que el día del mundo empieza en Greenwich", certeza = APROXIMADO, fuente = F_MERIDIANO),
                    mundo("voto", "12-08", "Una ley nueva da el voto a muchos más obreros", certeza = APROXIMADO, fuente = F_VOTO),
                    fiesta("fin_anio", "12-31", "La última noche del año", certeza = APROXIMADO),
                )),
        ),
        arcos = listOf(Arco("viaducto", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(1, "05-03", 0.0), p(2, "08-20", 0.45), p(4, "05-31", 1.0), p(5, "12-31", 1.0)))),
        objetos = listOf("pala", "carbon", "lata", "manometro", "silbato", "farol", "gorra", "reloj", "estopa", "horario", "pipa", "carnet"),
        estados = listOf(
            Estado("baja", "quemadura", "vuelve"),
            Estado("maquinista", "aprobado", "fin_anio"), Estado("maquinista", "fin_anio", null),
        ),
    )

    // =====================================================================
    //  Nikos, buzo esponjero de escafandra. Un barco de Tarpon Springs, en el golfo de México, de 1910 a 1914.
    // =====================================================================
    //
    // Su gente (toda inventada): el capitán del barco, Manolis, de su misma isla, Kálymnos. Yannis, su ayudante en la
    // cuerda, que entiende sus tirones. Los diez del barco. Su madre, en la isla, que le escribe y que en la temporada 3
    // le busca novia: Eleni, que llega a Florida en la 4 y con quien se casa en la 5.
    //
    // Lo real: los buzos griegos de Tarpon Springs, que llegaron desde 1905, sobre todo de Kálymnos, Symi y Halki; la
    // Epifanía de la cruz en el agua; el cometa Halley y la muerte de Mark Twain (1910); el huracán de octubre de 1910;
    // la guerra de Italia y Turquía, en la que Italia toma Rodas y el Dodecaneso (1911–1912); el Titanic (1912); las
    // guerras de los Balcanes (1912–1913); la guerra en Europa y el canal de Panamá (1914). Que llegue la noticia esos
    // días es inventado.
    //
    // El calendario de la Iglesia: la Iglesia griega seguía entonces el calendario juliano, que va trece días detrás del
    // de los americanos. Sus fiestas fijas caen trece días después: la Navidad el 7 de enero, San Basilio el 14, la
    // Epifanía el 19, el 25 de marzo de los griegos el 7 de abril, la Dormición el 28 de agosto y San Nicolás el 19 de
    // diciembre. Su Pascua es la ortodoxa (calculada).
    //
    // Claves de escena: fiesta, tormenta, luto, cruz, puerto. Arco: esponjas (lo que lleva el barco: se vende en la lonja a
    // mitad de año y al volver a puerto). Estados: «puerto» (cada invierno, de mediados de diciembre a fines de enero, en
    // Tarpon Springs) y «enfermo» (temporada 4: la enfermedad de los buzos).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el barco en el golfo, con Nikos bajando con
    // su casco de cobre y las esponjas en cubierta según `esponjas`; el muelle de Tarpon Springs, con los barcos amarrados,
    // durante «puerto»; y un cuarto de la casa de huéspedes durante «enfermo».

    private val F_EPIFANIA = "La Epifanía en Tarpon Springs" to "${W}Tarpon_Springs,_Florida"
    private val F_INDEPENDENCIA_GRIEGA = "El 25 de marzo de los griegos" to "${W}Greek_Independence_Day"
    private val F_CUATRO_JULIO = "El 4 de julio" to "${W}Independence_Day_(United_States)"
    private val F_SAN_NICOLAS = "San Nicolás" to "${W}Saint_Nicholas"
    private val F_HALLEY = "El cometa Halley" to "${W}Halley%27s_Comet"
    private val F_HURACAN_1910 = "El huracán de 1910" to "${W}1910_Cuba_hurricane"
    private val F_ITALIA_TURQUIA = "La guerra de Italia y Turquía" to "${W}Italo-Turkish_War"
    private val F_TITANIC = "El Titanic" to "${W}Sinking_of_the_Titanic"
    private val F_BALCANES = "La primera guerra de los Balcanes" to "${W}First_Balkan_War"
    private val F_GUERRA_1914 = "La crisis de julio" to "${W}July_Crisis"
    private val F_PANAMA = "El canal de Panamá" to "${W}Panama_Canal"

    private val PASCUA_ORTODOXA = listOf("05-01", "04-23", "04-07", "04-27", "04-19")

    /** Lo que vuelve cada año, en la temporada [t]: el invierno en puerto, la Iglesia y la lonja. */
    private fun anioDelBuzo(t: Int): List<DiaEspecial> = listOfNotNull(
        fiesta("anio_civil", "01-01", "El año nuevo de los americanos: cohetes en el muelle", certeza = APROXIMADO),
        fiesta("navidad_vieja", "01-07", "Navidad, con el calendario viejo de la Iglesia griega", certeza = APROXIMADO),
        fiesta("san_basilio", "01-14", "San Basilio, el año nuevo griego: la torta con la moneda dentro", certeza = APROXIMADO),
        fiesta("epifania", "01-19", "La Epifanía: el pope tira la cruz al agua y los muchachos se tiran a buscarla", certeza = APROXIMADO, fuente = F_EPIFANIA, escena = "cruz"),
        rito("zarpa", "01-26", "Los barcos salen otra vez a los bancos de esponjas", certeza = APROXIMADO),
        fiesta("pascua", PASCUA_ORTODOXA[t - 1], "La Pascua: cordero asado y huevos rojos", certeza = HISTORICO),
        if (t == 3) null else fiesta("veinticinco_marzo", "04-07", "El 25 de marzo de los griegos: la bandera azul y blanca en el mástil", certeza = APROXIMADO, fuente = F_INDEPENDENCIA_GRIEGA),
        rito("lonja", "06-26", "Se venden las esponjas en la lonja del muelle", certeza = APROXIMADO),
        fiesta("cuatro_julio", "07-04", "El 4 de julio de los americanos: banderas en el muelle", certeza = HISTORICO, fuente = F_CUATRO_JULIO),
        fiesta("dormicion", "08-28", "La Dormición de la Virgen, con el calendario viejo", certeza = APROXIMADO),
        rito("vuelta", "12-18", "Los barcos vuelven a puerto para el invierno", certeza = APROXIMADO, escena = "puerto"),
        fiesta("san_nicolas", "12-19", "San Nicolás, el de los marineros, con el calendario viejo", certeza = APROXIMADO, fuente = F_SAN_NICOLAS),
        fiesta("fin_anio", "12-31", "La última noche del año de los americanos", certeza = APROXIMADO),
    )

    val NIKOS = Calendario(
        vida = "buzo_esponjas",
        estaciones = listOf("06-01" to Estacion.LLUVIAS, "10-15" to Estacion.SECAS),
        clima = listOf(
            mes(despejado = 18, nubes = 8, lluvia = 3, viento = 2), mes(despejado = 18, nubes = 7, lluvia = 3, viento = 1), mes(despejado = 19, nubes = 7, lluvia = 3, viento = 2),
            mes(despejado = 21, nubes = 6, lluvia = 3), mes(despejado = 20, nubes = 6, lluvia = 4, tormenta = 1), mes(despejado = 12, nubes = 7, lluvia = 6, tormenta = 5),
            mes(despejado = 11, nubes = 7, lluvia = 6, tormenta = 7), mes(despejado = 11, nubes = 7, lluvia = 6, tormenta = 7), mes(despejado = 12, nubes = 7, lluvia = 6, tormenta = 5),
            mes(despejado = 17, nubes = 7, lluvia = 4, tormenta = 2, viento = 1), mes(despejado = 19, nubes = 7, lluvia = 3, viento = 1), mes(despejado = 18, nubes = 8, lluvia = 3, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1910", "cometa",
                "Nikos estrena traje. Muere Mark Twain y la Tierra pasa por la cola del cometa Halley, que se ve cada noche desde el barco. Nikos baja por primera vez a veinticinco brazas. Al buzo de otro barco lo sacan paralizado. Un huracán los obliga a refugiarse en una ensenada. Le llega carta de su madre.",
                anioDelBuzo(1) + listOf(
                    suceso("traje", "03-08", "Estrena un traje de lona nuevo, que todavía huele a goma"),
                    mundo("twain", "04-23", "Se dice que murió Mark Twain, que nació con el cometa y se fue con él", certeza = APROXIMADO, fuente = F_HALLEY),
                    mundo("cometa", "05-19", "La Tierra pasa por la cola del cometa, y nadie del barco quiere dormir", certeza = HISTORICO, fuente = F_HALLEY),
                    suceso("hondo", "05-30", "Baja por primera vez a veinticinco brazas de fondo"),
                    suceso("paralisis", "08-10", "Al buzo de otro barco lo sacan del agua paralizado"),
                    mundo("huracan", "10-18", "Un huracán que viene de Cuba los obliga a refugiarse en una ensenada", certeza = APROXIMADO, fuente = F_HURACAN_1910, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("carta", "11-20", "Le llega carta de su madre desde la isla"),
                )),
            TemporadaVida("1911", "libia",
                "Nikos tiene un ayudante nuevo en la cuerda, Yannis. Un tiburón lo sigue en el fondo. Encuentran un banco de esponjas nuevo. Se le rompe una bota de plomo. Italia le declara la guerra a Turquía por Libia, y en el barco se teme por las islas.",
                anioDelBuzo(2) + listOf(
                    suceso("yannis", "03-10", "Le ponen un ayudante nuevo en la cuerda: Yannis, un muchacho de Symi"),
                    suceso("tiburon", "06-08", "Un tiburón lo sigue en el fondo, dando vueltas, mientras corta esponjas"),
                    mundo("banco_nuevo", "07-25", "Encuentran un banco de esponjas nuevo, lejos, frente a una costa vacía", certeza = INVENTADO),
                    suceso("bota", "09-12", "Se le rompe una bota de plomo en el fondo y sube cojeando por la escala"),
                    mundo("libia", "10-05", "Se dice que Italia le declaró la guerra a Turquía por Libia", certeza = APROXIMADO, fuente = F_ITALIA_TURQUIA),
                    mundo("calma", "11-10", "Una calma de cinco días: no hay viento para volver a puerto", certeza = INVENTADO, clima = Clima.DESPEJADO),
                )),
            TemporadaVida("1912", "islas",
                "Nikos se hace una fotografía para mandarla a la isla. Se hunde el Titanic. Los italianos toman Rodas y las islas del Dodecaneso, también la suya. Nikos se compra un casco de cobre propio. Su madre le escribe que le ha buscado novia: Eleni. Empieza la guerra de los Balcanes. Manda el dinero del viaje de Eleni.",
                anioDelBuzo(3) + listOf(
                    suceso("foto", "02-20", "Se hace una fotografía en un estudio, con traje y bigote, para mandarla a la isla"),
                    mundo("titanic", "04-17", "Llega la noticia: se hundió el barco más grande del mundo", certeza = APROXIMADO, fuente = F_TITANIC, escena = "luto"),
                    mundo("dodecaneso", "05-28", "Llega la noticia: los italianos tomaron Rodas y las islas, también la suya", certeza = APROXIMADO, fuente = F_ITALIA_TURQUIA),
                    suceso("casco", "06-28", "Se compra un casco de cobre propio, de segunda mano"),
                    mundo("manguera", "07-28", "Una tormenta de verano revienta la manguera del aire de un buzo de otro barco", certeza = INVENTADO, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("eleni", "09-15", "Su madre le escribe que le ha buscado novia en la isla: Eleni"),
                    mundo("balcanes", "10-20", "Se dice que Grecia y sus vecinos le declararon la guerra a los turcos", certeza = APROXIMADO, fuente = F_BALCANES),
                    suceso("dinero", "11-20", "Manda a la isla el dinero del viaje de Eleni, en un giro del correo"),
                )),
            TemporadaVida("1913", "eleni",
                "Nikos le escribe a Eleni, por mano de otro. Se firma la paz de los Balcanes. Le da la enfermedad de los buzos y pasa cinco semanas en tierra. Vuelve al mar con miedo, y en septiembre baja otra vez hondo. Se pierde un barco de Key West. Llega Eleni desde la isla.",
                anioDelBuzo(4) + listOf(
                    suceso("escribe", "03-02", "Le dicta a un compañero que sabe letras su primera carta para Eleni"),
                    mundo("paz", "06-05", "Se dice que se firmó la paz de los Balcanes en Londres", certeza = APROXIMADO, fuente = F_BALCANES),
                    suceso("accidente", "06-12", "Sube del fondo demasiado deprisa y le da la enfermedad de los buzos"),
                    suceso("vuelve_mar", "07-18", "Vuelve al mar, y baja otra vez, con miedo"),
                    suceso("hondo_otra_vez", "09-20", "Vuelve a bajar a veinticinco brazas, por primera vez desde el accidente"),
                    mundo("perdido", "10-25", "Un barco de Key West se pierde en una tormenta con toda su gente", certeza = INVENTADO, escena = "luto"),
                    suceso("llega_eleni", "11-28", "Llega Eleni en barco desde la isla, con un baúl y el retrato de él"),
                )),
            TemporadaVida("1914", "guerra",
                "Nikos se casa con Eleni en la iglesia de San Nicolás. Alquilan una casa de madera cerca del muelle. Eleni espera un hijo. Hay guerra en Europa. Se abre el canal de Panamá. Eleni aprende a ensartar esponjas. Compran una cuna.",
                anioDelBuzo(5) + listOf(
                    suceso("boda", "02-17", "Se casa con Eleni en la iglesia de San Nicolás", escena = "fiesta"),
                    suceso("casa", "05-12", "Alquilan una casa de madera cerca del muelle"),
                    suceso("eleni_espera", "07-20", "Eleni le dice que espera un hijo"),
                    mundo("guerra", "08-03", "Llega la noticia: hay guerra en Europa", certeza = APROXIMADO, fuente = F_GUERRA_1914),
                    mundo("panama", "08-17", "Se dice que ya pasan barcos por el canal de Panamá", certeza = APROXIMADO, fuente = F_PANAMA),
                    suceso("eleni_esponjas", "09-22", "Eleni aprende a ensartar esponjas en el muelle con las otras mujeres"),
                    mundo("navidad_guerra", "10-20", "Se dice que la guerra de Europa no va a acabar para Navidad", certeza = APROXIMADO),
                    suceso("cuna", "11-12", "Compran una cuna de segunda mano y la pintan de azul"),
                )),
        ),
        arcos = listOf(Arco.cadaAnio("esponjas", TipoArco.OTRO, "01-26" to 0.0, "06-25" to 1.0, "06-26" to 0.1, "12-17" to 0.9, "12-18" to 0.0, saltos = setOf("06-26", "12-18"))),
        objetos = listOf("casco", "escafandra", "botas", "bomba", "cuerda", "cuchillo", "red", "esponja", "comboloi", "icono", "barco", "pan"),
        estados = listOf(
            Estado("puerto", "vuelta", "fin_anio"), Estado("puerto", "fin_anio", null), Estado("puerto", "anio_civil", "zarpa"),
            Estado("enfermo", "accidente", "vuelve_mar"),
        ),
    )

    // =====================================================================
    //  Lou, pianista de un club de jazz. Harlem, Nueva York, de 1926 a 1930.
    // =====================================================================
    //
    // Su gente (toda inventada): Lou es Louise, de Baltimore. Rafe, trompetista, con quien vive desde la temporada 2. El
    // dueño del club, Sully. Mama Dell, la dueña de la casa de huéspedes. Su madre, en Baltimore, que viene a verla tocar
    // en la 1. Los músicos de la calle.
    //
    // Lo real: Harlem y sus fiestas de alquiler; la Ley Seca; el Savoy (1926); Valentino y Gertrude Ederle (1926);
    // Lindbergh, Babe Ruth, el cine que habla, Florence Mills, Ellington en el Cotton Club y Show Boat (1927); Hoover y
    // el ratón de Disney (1928); la matanza de Chicago, la canción de Fats Waller y el crac de la bolsa (1929); el Empire
    // State y el Chrysler (1930); el circuito de teatros negros por el que salen de gira. Que lo vea o se entere esos días
    // es inventado.
    //
    // Claves de escena: fiesta, luto, calor, nieve. Arco: publico (cuánta gente llena el club: baja con el crac).
    // Estados: «gira» (temporada 3: el verano de gira por el sur) y «sin_club» (5: los meses sin club fijo, tocando en
    // fiestas de alquiler).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el sótano del club, con el piano y las mesas
    // más o menos llenas según `publico`; un escenario de teatro de pueblo durante «gira»; y el piano de una sala de casa,
    // con gente bailando entre los muebles, durante «sin_club».

    private val F_CUATRO_JULIO_US = F_CUATRO_JULIO
    private val F_ACCION_GRACIAS = "Acción de Gracias" to "${W}Thanksgiving_(United_States)"
    private val F_DIA_TRABAJO = "El día del Trabajo" to "${W}Labor_Day"
    private val F_SAVOY = "El Savoy" to "${W}Savoy_Ballroom"
    private val F_EDERLE = "Gertrude Ederle" to "${W}Gertrude_Ederle"
    private val F_VALENTINO = "Valentino" to "${W}Rudolph_Valentino"
    private val F_LINDBERGH = "Lindbergh" to "${W}Charles_Lindbergh"
    private val F_RUTH = "Babe Ruth" to "${W}Babe_Ruth"
    private val F_JAZZ_SINGER = "El cantor de jazz" to "${W}The_Jazz_Singer"
    private val F_FLORENCE = "Florence Mills" to "${W}Florence_Mills"
    private val F_COTTON = "El Cotton Club" to "${W}Cotton_Club"
    private val F_SHOW_BOAT = "Show Boat" to "${W}Show_Boat_(musical)"
    private val F_ELECCION_1928 = "Las elecciones de 1928" to "${W}1928_United_States_presidential_election"
    private val F_WILLIE = "Steamboat Willie" to "${W}Steamboat_Willie"
    private val F_CHICAGO = "La matanza de San Valentín" to "${W}Saint_Valentine%27s_Day_Massacre"
    private val F_WALLER = "La canción de Fats Waller" to "${W}Ain%27t_Misbehavin%27_(song)"
    private val F_CRAC = "El crac de 1929" to "${W}Wall_Street_crash_of_1929"
    private val F_EMPIRE = "El Empire State" to "${W}Empire_State_Building"
    private val F_CHRYSLER = "El Chrysler" to "${W}Chrysler_Building"
    private val F_TOBA = "El circuito de los teatros negros" to "${W}Theater_Owners_Booking_Association"

    private val PASCUA_NY = listOf("04-04", "04-17", "04-08", "03-31", "04-20")
    private val ACCION_GRACIAS = listOf("11-25", "11-24", "11-29", "11-28", "11-27")    // el último jueves de noviembre, como entonces
    private val DIA_TRABAJO = listOf("09-06", "09-05", "09-03", "09-02", "09-01")       // el primer lunes de septiembre

    /** Lo que vuelve cada año en Harlem, en la temporada [t]. */
    private fun anioDeHarlem(t: Int): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo: se toca hasta que sale el sol", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
        fiesta("pascua", PASCUA_NY[t - 1], "Domingo de Pascua: desfile de sombreros por las avenidas", certeza = HISTORICO),
        fiesta("cuatro_julio", "07-04", "El 4 de julio: petardos en las calles y el club lleno", certeza = HISTORICO, fuente = F_CUATRO_JULIO_US),
        fiesta("dia_trabajo", DIA_TRABAJO[t - 1], "El día del Trabajo: desfile de los sindicatos y el último fin de semana del verano", certeza = HISTORICO, fuente = F_DIA_TRABAJO),
        fiesta("accion_gracias", ACCION_GRACIAS[t - 1], "Acción de Gracias: pavo en la casa de huéspedes", certeza = HISTORICO, fuente = F_ACCION_GRACIAS),
        fiesta("navidad", "12-25", "Navidad", certeza = HISTORICO, fuente = F_NAVIDAD),
        fiesta("nochevieja", "12-31", "Nochevieja: la noche más larga del año en el club", certeza = APROXIMADO),
    )

    val LOU = Calendario(
        vida = "pianista",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 11, nubes = 9, lluvia = 5, nieve = 5, viento = 1), mes(despejado = 11, nubes = 9, lluvia = 4, nieve = 4, viento = 1),
            mes(despejado = 12, nubes = 9, lluvia = 7, nieve = 2, viento = 1), mes(despejado = 13, nubes = 8, lluvia = 9),
            mes(despejado = 15, nubes = 8, lluvia = 8), mes(despejado = 16, nubes = 7, lluvia = 6, tormenta = 2),
            mes(despejado = 17, nubes = 6, lluvia = 5, tormenta = 3), mes(despejado = 16, nubes = 7, lluvia = 5, tormenta = 3),
            mes(despejado = 16, nubes = 7, lluvia = 6, niebla = 1), mes(despejado = 15, nubes = 8, lluvia = 7, niebla = 1),
            mes(despejado = 12, nubes = 9, lluvia = 8, nieve = 1), mes(despejado = 11, nubes = 9, lluvia = 6, nieve = 4, viento = 1),
        ),
        temporadas = listOf(
            TemporadaVida("1926", "harlem",
                "Lou empieza a tocar en un club nuevo de la calle 133. Toca en su primera fiesta de alquiler. Abre el Savoy. Pierde un duelo de pianos. Se compra un piano viejo a plazos. Una ola de calor. Una muchacha cruza a nado el canal de la Mancha. Muere Valentino. La policía cierra el club una noche. Su madre viene de Baltimore a verla tocar.",
                anioDeHarlem(1) + listOf(
                    suceso("club", "01-15", "Empieza a tocar en un club nuevo de la calle 133, en un sótano"),
                    suceso("fiesta_alquiler", "02-20", "Toca en su primera fiesta de alquiler, en un piso de la Séptima Avenida"),
                    mundo("savoy", "03-12", "Abre el Savoy, el salón de baile más grande de Harlem", certeza = HISTORICO, fuente = F_SAVOY),
                    suceso("duelo", "04-24", "Pierde un duelo de pianos contra un viejo maestro del stride"),
                    suceso("piano", "06-05", "Se compra a plazos un piano viejo para su cuarto"),
                    mundo("calor", "07-20", "Ola de calor: nadie duerme en Harlem, todos suben a las azoteas", certeza = INVENTADO, escena = "calor", clima = Clima.DESPEJADO),
                    mundo("ederle", "08-07", "Se dice que una muchacha de Nueva York cruzó a nado el canal de la Mancha", certeza = APROXIMADO, fuente = F_EDERLE),
                    mundo("valentino", "08-24", "Muere Valentino, y miles de mujeres lloran en la calle frente a la funeraria", certeza = APROXIMADO, fuente = F_VALENTINO, escena = "luto"),
                    mundo("redada", "10-15", "La policía cierra el club una noche por el alcohol, y al otro día abre igual", certeza = INVENTADO),
                    suceso("madre", "11-06", "Su madre viene de Baltimore a verla tocar"),
                )),
            TemporadaVida("1927", "lindbergh",
                "Lou conoce a Rafe, trompetista. Graba un disco de prueba que no sale nunca. Lindbergh cruza el Atlántico y Nueva York lo recibe con papel picado. Lou y Rafe se van a vivir juntos. Babe Ruth hace sesenta jonrones. El cine habla. Entierran a Florence Mills. Ellington empieza en el Cotton Club. Se estrena Show Boat.",
                anioDeHarlem(2) + listOf(
                    suceso("rafe", "02-12", "Conoce a Rafe, un trompetista de Nueva Orleans"),
                    suceso("disco", "03-22", "Graba un disco de prueba en un estudio del centro"),
                    mundo("lindbergh", "05-21", "Lindbergh llega a París volando solo desde Nueva York", certeza = HISTORICO, fuente = F_LINDBERGH),
                    mundo("papel_picado", "06-13", "Nueva York recibe a Lindbergh con papel picado desde las ventanas", certeza = APROXIMADO, fuente = F_LINDBERGH),
                    suceso("juntos", "08-01", "Lou y Rafe se van a vivir juntos a un cuarto más grande"),
                    mundo("ruth", "09-30", "Babe Ruth hace su jonrón número sesenta", certeza = HISTORICO, fuente = F_RUTH),
                    mundo("cine_habla", "10-06", "Se estrena una película en la que los actores hablan y cantan", certeza = HISTORICO, fuente = F_JAZZ_SINGER),
                    mundo("florence", "11-06", "Entierran a Florence Mills, y medio Harlem sale a la calle", certeza = APROXIMADO, fuente = F_FLORENCE, escena = "luto"),
                    mundo("ellington", "12-04", "Duke Ellington empieza a tocar en el Cotton Club", certeza = HISTORICO, fuente = F_COTTON),
                    mundo("show_boat", "12-27", "Se estrena Show Boat en Broadway", certeza = HISTORICO, fuente = F_SHOW_BOAT),
                )),
            TemporadaVida("1928", "gira",
                "Lou tiene gripe. Rafe le regala un abrigo. La contratan para una gira de verano por los teatros negros del sur, y vuelve con dinero y con historias. Antes, el club se inunda. Rafe le pide que se case con él y ella le dice que todavía no. Gana Hoover. Un ratón silba en una película.",
                anioDeHarlem(3) + listOf(
                    suceso("gripe", "02-02", "Una gripe la tiene una semana sin tocar"),
                    suceso("abrigo", "03-10", "Rafe le regala un abrigo de piel de conejo, de segunda mano"),
                    suceso("contrato", "04-25", "La contratan para una gira de verano por los teatros negros del sur"),
                    mundo("sale_gira", "06-18", "Sale de gira en tren, con una compañía de revista", certeza = APROXIMADO, fuente = F_TOBA),
                    mundo("inundado", "05-22", "Llueve una semana entera y el sótano del club se inunda", certeza = INVENTADO, clima = Clima.LLUVIA),
                    suceso("hotel", "07-20", "En un pueblo de Georgia no las dejan dormir en el hotel, y duermen en casa del pastor"),
                    suceso("vuelve_gira", "08-30", "Vuelve de la gira con dinero, con historias y con los pies destrozados"),
                    suceso("no_todavia", "10-02", "Rafe le pide que se case con él, y ella le dice que todavía no"),
                    mundo("hoover", "11-07", "Gana Hoover las elecciones, y en el club nadie se sorprende", certeza = APROXIMADO, fuente = F_ELECCION_1928),
                    mundo("raton", "11-18", "Se estrena una película de dibujos en la que un ratón silba", certeza = HISTORICO, fuente = F_WILLIE),
                    suceso("radio", "12-12", "La sacan tocando en la radio, una sola canción, de madrugada"),
                )),
            TemporadaVida("1929", "crac",
                "Lou empieza a estudiar armonía. Matan a siete hombres en un garaje de Chicago. Sully le pone su nombre en la puerta. Lou toca en el club la canción de Fats Waller que se silba en todas partes. Rafe se va a Chicago con una orquesta. Lou se compra un vestido a plazos. La bolsa se hunde: el jueves negro y el martes negro. El club se vacía.",
                anioDeHarlem(4) + listOf(
                    mundo("chicago", "02-15", "Se dice que en Chicago mataron a siete hombres en un garaje", certeza = APROXIMADO, fuente = F_CHICAGO),
                    suceso("armonia", "03-12", "Empieza a estudiar armonía con un profesor viejo que tuvo un conservatorio"),
                    suceso("aumento", "04-20", "Sully le sube la paga y le pone su nombre en la puerta del club"),
                    mundo("waller", "06-22", "Una revista de Harlem estrena una canción de Fats Waller que se silba en todas partes", certeza = APROXIMADO, fuente = F_WALLER),
                    suceso("carta_rafe", "05-25", "Rafe le escribe desde una gira por el medio oeste: vuelve en julio"),
                    suceso("rafe_chicago", "08-12", "Rafe se va a Chicago con una orquesta, por un tiempo"),
                    suceso("compra", "09-14", "Se compra un vestido de lentejuelas, pagado en seis plazos"),
                    mundo("jueves_negro", "10-24", "El jueves negro: la bolsa se hunde", certeza = HISTORICO, fuente = F_CRAC),
                    mundo("martes_negro", "10-29", "El martes negro: la bolsa se hunde del todo", certeza = HISTORICO, fuente = F_CRAC),
                    suceso("vacio", "11-16", "Una noche toca para seis personas en un club vacío"),
                )),
            TemporadaVida("1930", "depresion",
                "Nieva sobre Harlem. El club cierra y Lou vive de las fiestas de alquiler. Empiezan a levantar el Empire State. Rafe vuelve de Chicago. Lou encuentra otro club. Abre el Chrysler. Lou y Rafe ensayan un número juntos y se casan.",
                anioDeHarlem(5) + listOf(
                    mundo("nevada", "01-20", "Una nevada deja Harlem blanco y en silencio", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    suceso("cierra_club", "02-17", "El club de Sully cierra: no hay gente ni dinero", cadena = "club"),
                    mundo("empire", "03-17", "Empiezan a levantar el Empire State, en la Quinta Avenida", certeza = HISTORICO, fuente = F_EMPIRE),
                    suceso("rafe_vuelve", "04-02", "Rafe vuelve de Chicago, sin orquesta y sin dinero"),
                    suceso("nuevo_club", "05-05", "Encuentra trabajo en otro club, más pequeño", cadena = "club"),
                    mundo("chrysler", "05-27", "Abre el Chrysler, el edificio más alto del mundo", certeza = HISTORICO, fuente = F_CHRYSLER),
                    suceso("ensayo", "07-28", "Ensaya con Rafe un número para dos, piano y trompeta, en un cuarto sin ventilador"),
                    suceso("boda", "09-14", "Se casa con Rafe, y toca en su propia boda", escena = "fiesta"),
                    mundo("cola_pan", "11-03", "Colas de hombres para la sopa en las calles de Harlem", certeza = INVENTADO),
                )),
        ),
        arcos = listOf(Arco("publico", TipoArco.OTRO, listOf(
            p(1, "01-01", 0.6), p(2, "12-31", 0.9), p(4, "10-23", 0.95), p(4, "11-20", 0.4), p(5, "02-17", 0.25), p(5, "05-05", 0.35), p(5, "12-31", 0.45),
        ))),
        objetos = listOf("piano", "partitura", "sombrero", "ficha", "taza", "disco", "radio", "collar", "zapatos", "panuelo", "cigarrillo", "metronomo"),
        estados = listOf(Estado("gira", "sale_gira", "vuelve_gira"), Estado("sin_club", "cierra_club", "nuevo_club")),
    )

    // =====================================================================
    //  Liudmila, obrera textil. Moscú, de 1975 a 1979.
    // =====================================================================
    //
    // Su gente (toda inventada): Volodia, su marido, tornero, bueno y bebedor. Lena, su hija (diez años en la temporada
    // 1). Galina Petrovna, su suegra, que vive con ellos y muere en la 4. Zina, su compañera de telar. La jefa de turno.
    //
    // Lo real: las fiestas soviéticas (el año nuevo, el día del ejército, el 8 de marzo, el día de los cosmonautas, el
    // sábado de trabajo voluntario, el 1 de mayo, el día de la Victoria, la Revolución de noviembre, el día de la
    // Constitución); la nave americana y la soviética unidas en el espacio y la canción de Pugachova (1975); el congreso del
    // Partido (1976); la bomba del metro, el incendio del hotel Rossiya y la Constitución nueva (1977); el primer checo en
    // el espacio (1978); el tratado de Viena y la entrada en Afganistán (1979). Que se sepa en la fábrica esos días es
    // inventado.
    //
    // Claves de escena: fiesta, nieve, luto, desfile. Arco: plan (lo cumplido del plan del año en la fábrica: se corre en
    // diciembre). Estado: «sanatorio» (temporadas 2 y 4: tres semanas en un sanatorio del mar Negro).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la nave de telares, con el cartel del plan
    // más o menos lleno según `plan`, o la cola de una tienda en la calle nevada; y una playa de piedras con un edificio
    // blanco durante «sanatorio».

    private val F_EJERCITO = "El día del ejército" to "${W}Defender_of_the_Fatherland_Day"
    private val F_OCHO_MARZO = "El 8 de marzo" to "${W}International_Women%27s_Day"
    private val F_COSMONAUTAS = "El día de los cosmonautas" to "${W}Cosmonautics_Day"
    private val F_SUBBOTNIK = "El sábado de trabajo voluntario" to "${W}Subbotnik"
    private val F_PRIMERO_MAYO = "El 1 de mayo" to "${W}International_Workers%27_Day"
    private val F_VICTORIA = "El día de la Victoria" to "${W}Victory_Day_(9_May)"
    private val F_REVOLUCION = "El día de la Revolución" to "${W}October_Revolution_Day"
    private val F_CONSTITUCION_1977 = "La Constitución de 1977" to "${W}1977_Soviet_Constitution"
    private val F_APOLO_SOYUZ = "Apolo y Soyuz" to "${W}Apollo%E2%80%93Soyuz"
    private val F_PUGACHOVA = "Alla Pugachova" to "${W}Alla_Pugacheva"
    private val F_CONGRESO = "El XXV congreso del Partido" to "${W}25th_Congress_of_the_Communist_Party_of_the_Soviet_Union"
    private val F_BOMBA_METRO = "Las bombas de Moscú de 1977" to "${W}1977_Moscow_bombings"
    private val F_ROSSIYA = "El hotel Rossiya" to "${W}Hotel_Rossiya"
    private val F_REMEK = "Vladimír Remek" to "${W}Vladim%C3%ADr_Remek"
    private val F_SALT = "El tratado de Viena" to "${W}SALT_II"
    private val F_AFGANISTAN = "La guerra de Afganistán" to "${W}Soviet%E2%80%93Afghan_War"

    private val SUBBOTNIK = listOf("04-19", "04-17", "04-16", "04-22", "04-21")           // el sábado más cerca del 22 de abril
    private val PASCUA_SUEGRA = listOf("05-04", "04-25", "04-10", "04-30", null)           // la Pascua ortodoxa; en 1979 ya no está la suegra
    private val CONSTITUCION = listOf("12-05", "12-05", "10-07", "10-07", "10-07")

    /** Lo que vuelve cada año en Moscú, en la temporada [t]. */
    private fun anioDeMoscu(t: Int): List<DiaEspecial> = listOfNotNull(
        fiesta("anio_nuevo", "01-01", "Año nuevo: el abeto, el champán soviético y el discurso en la tele", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
        fiesta("ejercito", "02-23", "El día del ejército: regalos para los hombres de la casa y de la fábrica", certeza = HISTORICO, fuente = F_EJERCITO),
        fiesta("ocho_marzo", "03-08", "El 8 de marzo: mimosas, bombones y la fiesta de las mujeres", certeza = HISTORICO, fuente = F_OCHO_MARZO),
        fiesta("cosmonautas", "04-12", "El día de los cosmonautas", certeza = HISTORICO, fuente = F_COSMONAUTAS),
        rito("subbotnik", SUBBOTNIK[t - 1], "Sábado de trabajo voluntario: todos a barrer el patio de la fábrica", certeza = APROXIMADO, fuente = F_SUBBOTNIK),
        PASCUA_SUEGRA[t - 1]?.let { rito("pascua_suegra", it, "La suegra pinta huevos para la Pascua, aunque no sea fiesta", certeza = APROXIMADO) },
        fiesta("primero_mayo", "05-01", "El 1 de mayo: desfile, banderas y dos días de fiesta", 2, HISTORICO, F_PRIMERO_MAYO, escena = "desfile"),
        fiesta("victoria", "05-09", "El día de la Victoria: los viejos con sus medallas", certeza = HISTORICO, fuente = F_VICTORIA),
        rito("septiembre", "09-01", "El primero de septiembre: Lena vuelve a la escuela con flores para la maestra", certeza = APROXIMADO),
        fiesta("constitucion", CONSTITUCION[t - 1], "El día de la Constitución", certeza = APROXIMADO, fuente = if (t >= 3) F_CONSTITUCION_1977 else null),
        fiesta("revolucion", "11-07", "El aniversario de la Revolución: desfile en la plaza Roja", 2, HISTORICO, F_REVOLUCION, escena = "desfile"),
    )

    val LIUDMILA = Calendario(
        vida = "obrera_moscu",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 5, nubes = 12, nieve = 12, niebla = 1, viento = 1), mes(despejado = 6, nubes = 11, nieve = 10, viento = 1),
            mes(despejado = 8, nubes = 11, nieve = 7, lluvia = 3, viento = 2), mes(despejado = 10, nubes = 10, lluvia = 7, nieve = 2, viento = 1),
            mes(despejado = 13, nubes = 9, lluvia = 8, tormenta = 1), mes(despejado = 13, nubes = 8, lluvia = 8, tormenta = 2),
            mes(despejado = 14, nubes = 7, lluvia = 8, tormenta = 2), mes(despejado = 13, nubes = 8, lluvia = 8, tormenta = 2),
            mes(despejado = 11, nubes = 10, lluvia = 8, niebla = 1), mes(despejado = 8, nubes = 12, lluvia = 9, nieve = 1, niebla = 1),
            mes(despejado = 5, nubes = 13, lluvia = 6, nieve = 6, niebla = 1), mes(despejado = 4, nubes = 13, nieve = 13, niebla = 1),
        ),
        temporadas = listOf(
            TemporadaVida("1975", "distension",
                "Por fin le toca a Liudmila la nevera de la lista. Lena entra en los pioneros. Una nave soviética y una americana se unen en el espacio. En la radio suena «Arlequín». Liudmila hace cuatro horas de cola por unas botas de Yugoslavia. Volodia llega borracho la noche de la Revolución.",
                anioDeMoscu(1) + listOf(
                    suceso("nevera", "03-20", "Por fin le toca la nevera de la lista de espera del sindicato"),
                    suceso("pionera", "05-19", "Lena entra en los pioneros: el pañuelo rojo y la promesa"),
                    mundo("arlequin", "06-25", "En la radio no paran de poner «Arlequín», de una cantante joven", certeza = APROXIMADO, fuente = F_PUGACHOVA),
                    mundo("apolo_soyuz", "07-17", "Una nave soviética y una americana se unen en el espacio", certeza = HISTORICO, fuente = F_APOLO_SOYUZ),
                    suceso("botas", "10-14", "Hace cuatro horas de cola por unas botas de Yugoslavia"),
                    suceso("borracho", "11-10", "Volodia llega borracho de la fiesta de la fábrica y rompe un plato"),
                    mundo("plan_75", "12-20", "La fábrica corre para cumplir el plan del año: turnos dobles", certeza = INVENTADO),
                )),
            TemporadaVida("1976", "congreso",
                "La suegra se cae en el hielo y se rompe la muñeca. Empieza el congreso del Partido. Plantan patatas en la dacha de unos amigos. Liudmila va tres semanas a un sanatorio del mar Negro. Volodia deja de beber, por un tiempo. Todo Moscú sale a buscar setas. Lena gana un concurso de poesía de la escuela.",
                anioDeMoscu(2) + listOf(
                    suceso("hielo", "01-22", "La suegra se cae en el hielo de la acera y se rompe la muñeca"),
                    mundo("congreso", "02-24", "Empieza el congreso del Partido: discursos en todas las radios", certeza = HISTORICO, fuente = F_CONGRESO),
                    suceso("dacha", "06-10", "Pasan un fin de semana en la dacha de unos amigos, plantando patatas"),
                    suceso("llega_sanatorio", "07-05", "Llega a un sanatorio del mar Negro, con el viaje que le dio la fábrica"),
                    suceso("vuelve_sanatorio", "07-26", "Vuelve del mar Negro morena y con una caja de melocotones"),
                    suceso("deja_beber", "09-12", "Volodia le promete que deja de beber, y lo cumple un tiempo"),
                    mundo("hongos", "10-03", "Todo Moscú sale al bosque a buscar setas: el metro va lleno de cestas", certeza = INVENTADO),
                    suceso("poesia", "11-25", "Lena gana un concurso de poesía de la escuela"),
                )),
            TemporadaVida("1977", "constitucion",
                "Se dice en voz baja que estalló una bomba en el metro. Arde el hotel Rossiya. Les dan un piso de tres cuartos en un barrio nuevo. Hay Constitución nueva, y el día de la Constitución cambia de fecha. Los sesenta años de la Revolución.",
                anioDeMoscu(3) + listOf(
                    mundo("bomba", "01-10", "Se dice en voz baja que estalló una bomba en el metro", certeza = APROXIMADO, fuente = F_BOMBA_METRO),
                    mundo("rossiya", "02-25", "Arde el hotel Rossiya, junto al Kremlin", certeza = HISTORICO, fuente = F_ROSSIYA),
                    suceso("piso", "06-15", "Les dan un piso de tres cuartos en un barrio nuevo, lejos del metro"),
                    suceso("mudanza", "06-25", "La mudanza, en un camión de la fábrica, con todos los vecinos mirando"),
                    suceso("zina", "08-20", "Zina, su compañera de telar, se casa, y Liudmila es su testigo"),
                    mundo("sesenta", "11-05", "Moscú se llena de banderas para los sesenta años de la Revolución", certeza = APROXIMADO, fuente = F_REVOLUCION),
                )),
            TemporadaVida("1978", "interkosmos",
                "Un checo vuela al espacio. Lena termina el curso con sobresalientes. Liudmila vuelve al sanatorio del mar Negro. La suegra enferma y muere en otoño. Lena cumple catorce años.",
                anioDeMoscu(4) + listOf(
                    mundo("remek", "03-03", "Un checo vuela al espacio con un cosmonauta soviético", certeza = APROXIMADO, fuente = F_REMEK),
                    suceso("cincos", "06-05", "Lena termina el curso con todo sobresalientes"),
                    suceso("llega_sanatorio", "07-10", "Vuelve al sanatorio del mar Negro, esta vez con Lena"),
                    suceso("vuelve_sanatorio", "07-31", "Vuelve del mar Negro con Lena quemada por el sol"),
                    suceso("suegra_enferma", "09-04", "La suegra cae enferma y ya no se levanta", cadena = "suegra"),
                    suceso("suegra_muere", "10-20", "Muere Galina Petrovna, la suegra", cadena = "suegra", escena = "luto"),
                    suceso("catorce", "12-12", "Lena cumple catorce años y quiere unos vaqueros"),
                )),
            TemporadaVida("1979", "afganistan",
                "Un frío que nadie recuerda. Lena entra en el Komsomol. A Liudmila la nombran jefa de turno. Brézhnev y Carter firman en Viena. Lena va a un campamento en Crimea. Moscú se arregla para las Olimpiadas. El ejército entra en Afganistán.",
                anioDeMoscu(5) + listOf(
                    mundo("frio", "01-03", "Un frío que nadie recuerda: casi cuarenta bajo cero", certeza = APROXIMADO, escena = "nieve", clima = Clima.NIEVE),
                    suceso("komsomol", "03-15", "Lena entra en el Komsomol"),
                    suceso("jefa", "05-21", "La nombran jefa de turno"),
                    mundo("viena", "06-19", "Brézhnev y Carter firman en Viena un tratado sobre los cohetes", certeza = APROXIMADO, fuente = F_SALT),
                    suceso("lena_campamento", "07-25", "Lena se va un mes a un campamento de verano en Crimea"),
                    mundo("olimpiadas", "08-15", "Moscú se arregla para las Olimpiadas: pintan fachadas y levantan estadios", certeza = APROXIMADO),
                    suceso("vaqueros", "10-30", "Consigue unos vaqueros para Lena, de contrabando, por un mes de sueldo"),
                    mundo("afganistan", "12-28", "Se dice que el ejército entró en Afganistán a ayudar", certeza = APROXIMADO, fuente = F_AFGANISTAN),
                )),
        ),
        arcos = listOf(Arco.cadaAnio("plan", TipoArco.OBRA, "01-01" to 0.0, "11-30" to 0.7, "12-28" to 1.0, "12-29" to 0.0, saltos = setOf("12-29"))),
        objetos = listOf("bolsa", "moneda", "libro", "abrigo", "botas", "radio", "televisor", "samovar", "termo", "chal", "pase", "postal"),
        estados = listOf(Estado("sanatorio", "llega_sanatorio", "vuelve_sanatorio")),
    )

    // =====================================================================
    //  Giulia, restauradora de frescos. Roma, de 2025 a 2029.
    // =====================================================================
    //
    // Su gente (toda inventada): Teresa, la jefa de la obra, que se jubila en la temporada 4. Samir, un restaurador
    // joven que llega en la 1. Su madre, en Testaccio. Matteo, con quien vive desde la 2. La iglesia (sin nombre), en
    // Trastevere, con una capilla de frescos que se restaura de 2025 a 2029.
    //
    // Lo real: las fiestas de Roma y de Italia; el jubileo de 2025; la muerte del papa Francisco, su funeral, el cónclave
    // y León XIV (2025); la canonización de Carlo Acutis (2025); el cierre de la Puerta Santa y los Juegos de invierno de
    // Milán y Cortina (2026). Desde 2027, solo lo programado: los Juegos de Los Ángeles (2028) y las elecciones europeas
    // (2029). Que lo vea esos días es inventado.
    //
    // Claves de escena: fiesta, luto, humo, agosto. Arco: fresco (cuánto de la capilla está restaurado). Estado:
    // «agosto» (cada año, la obra cerrada y Roma vacía).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la capilla con el andamio y la parte limpia
    // del fresco según `fresco`; y una Roma de calles vacías y persianas bajadas, con calor, durante «agosto».

    private val F_BEFANA = "La Befana" to "${W}Befana"
    private val F_NATALE_ROMA = "El cumpleaños de Roma" to "${W}Natale_di_Roma"
    private val F_LIBERAZIONE = "El 25 de abril" to "${W}Liberation_Day_(Italy)"
    private val F_REPUBBLICA = "La fiesta de la República" to "${W}Festa_della_Repubblica"
    private val F_SAN_PIETRO = "San Pedro y San Pablo" to "${W}Feast_of_Saints_Peter_and_Paul"
    private val F_FERRAGOSTO = "Ferragosto" to "${W}Ferragosto"
    private val F_INMACULADA = "La Inmaculada" to "${W}Feast_of_the_Immaculate_Conception"
    private val F_JUBILEO = "El jubileo de 2025" to "${W}Jubilee_2025"
    private val F_FRANCISCO = "La muerte del papa Francisco" to "${W}Death_and_funeral_of_Pope_Francis"
    private val F_CONCLAVE = "El cónclave de 2025" to "${W}2025_papal_conclave"
    private val F_ACUTIS = "Carlo Acutis" to "${W}Carlo_Acutis"
    private val F_INVIERNO_2026 = "Los Juegos de invierno de 2026" to "${W}2026_Winter_Olympics"
    private val F_LOS_ANGELES = "Los Juegos de 2028" to "${W}2028_Summer_Olympics"
    private val F_EUROPEAS = "Las elecciones europeas de 2029" to "${W}2029_European_Parliament_election"

    private val PASCUA_ROMA = listOf("04-20", "04-05", "03-28", "04-16", "04-01")

    /** Lo que vuelve cada año en Roma, en la temporada [t]. En 2025 el cumpleaños de Roma es el día en que muere el papa. */
    private fun anioDeRoma(t: Int): List<DiaEspecial> = listOfNotNull(
        fiesta("capodanno", "01-01", "Año nuevo: petardos en todas las terrazas", certeza = HISTORICO, fuente = F_ANIO_NUEVO),
        fiesta("befana", "01-06", if (t == 2) "La Befana, y el papa cierra la Puerta Santa" else "La Befana: calcetines con caramelos y carbón de azúcar", certeza = HISTORICO, fuente = if (t == 2) F_JUBILEO else F_BEFANA),
        fiesta("pascua", PASCUA_ROMA[t - 1], "Pascua: la bendición del papa a la ciudad y al mundo", certeza = HISTORICO),
        if (t == 1) null else mundo("natale_roma", "04-21", "El cumpleaños de Roma: romanos de mentira desfilan por el Circo Máximo", certeza = HISTORICO, fuente = F_NATALE_ROMA),
        fiesta("liberazione", "04-25", "El 25 de abril: la fiesta de la Liberación", certeza = HISTORICO, fuente = F_LIBERAZIONE),
        fiesta("repubblica", "06-02", if (t == 2) "Ochenta años de la República: desfile en la vía de los Foros" else "La fiesta de la República: desfile en la vía de los Foros", certeza = HISTORICO, fuente = F_REPUBBLICA, escena = "desfile"),
        fiesta("san_pietro", "06-29", "San Pedro y San Pablo, los patronos de Roma", certeza = HISTORICO, fuente = F_SAN_PIETRO),
        mundo("cierra_obra", "08-01", "La obra cierra por vacaciones, y Roma se vacía", certeza = APROXIMADO, escena = "agosto"),
        fiesta("ferragosto", "08-15", "Ferragosto: la ciudad entera, cerrada", certeza = HISTORICO, fuente = F_FERRAGOSTO),
        rito("vuelve_obra", "08-31", "Vuelve al andamio después del verano", certeza = APROXIMADO),
        fiesta("inmaculada", "12-08", "La Inmaculada: el papa en la plaza de España", certeza = HISTORICO, fuente = F_INMACULADA),
        fiesta("navidad", "12-25", "Navidad", certeza = HISTORICO, fuente = F_NAVIDAD),
    )

    val GIULIA = Calendario(
        vida = "restauradora",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 13, nubes = 10, lluvia = 7, viento = 1), mes(despejado = 13, nubes = 9, lluvia = 6), mes(despejado = 14, nubes = 9, lluvia = 7, viento = 1),
            mes(despejado = 14, nubes = 9, lluvia = 7), mes(despejado = 18, nubes = 7, lluvia = 5, tormenta = 1), mes(despejado = 22, nubes = 5, lluvia = 2, tormenta = 1),
            mes(despejado = 26, nubes = 3, lluvia = 1, tormenta = 1), mes(despejado = 24, nubes = 4, lluvia = 1, tormenta = 2), mes(despejado = 18, nubes = 6, lluvia = 4, tormenta = 2),
            mes(despejado = 14, nubes = 8, lluvia = 8, tormenta = 1), mes(despejado = 11, nubes = 9, lluvia = 10), mes(despejado = 12, nubes = 10, lluvia = 8, niebla = 1),
        ),
        temporadas = listOf(
            TemporadaVida("2025", "jubileo",
                "Roma está llena de peregrinos del jubileo. Giulia empieza la capilla de los frescos. Bajo el hollín aparece la cara de un ángel. Muere el papa Francisco el lunes de Pascua; su funeral, el cónclave y el papa nuevo, León XIV. Llega Samir a la obra. En septiembre canonizan a un muchacho de Milán. Un aguacero deja la obra sin luz y aparece una grieta vieja.",
                anioDeRoma(1) + listOf(
                    mundo("jubileo", "01-13", "Roma llena de peregrinos del jubileo: colas en la Puerta Santa", certeza = APROXIMADO, fuente = F_JUBILEO),
                    suceso("capilla", "01-20", "Sube por primera vez al andamio de la capilla de los frescos"),
                    suceso("angel", "03-18", "Bajo el hollín de siglos aparece la cara de un ángel"),
                    mundo("francisco", "04-21", "Muere el papa Francisco, el lunes de Pascua", certeza = HISTORICO, fuente = F_FRANCISCO, escena = "luto"),
                    mundo("funeral", "04-26", "El funeral del papa en la plaza de San Pedro", certeza = HISTORICO, fuente = F_FRANCISCO, escena = "luto"),
                    mundo("conclave", "05-07", "Empieza el cónclave: Roma entera mira una chimenea", certeza = HISTORICO, fuente = F_CONCLAVE),
                    mundo("leon", "05-08", "Humo blanco: hay papa nuevo, León XIV", certeza = HISTORICO, fuente = F_CONCLAVE, escena = "humo"),
                    suceso("samir", "06-16", "Llega Samir a la obra, un restaurador joven"),
                    mundo("acutis", "09-07", "Canonizan en San Pedro a un muchacho de Milán, Carlo Acutis", certeza = HISTORICO, fuente = F_ACUTIS),
                    mundo("aguacero", "10-15", "Un aguacero inunda la vía del Tritone y la obra se queda sin luz", certeza = INVENTADO, clima = Clima.TORMENTA),
                    suceso("grieta", "11-12", "Encuentra una grieta vieja detrás del fresco y paran la obra dos días"),
                )),
            TemporadaVida("2026", "olimpiadas",
                "El papa cierra la Puerta Santa. Empiezan los Juegos de invierno en Milán y Cortina. Giulia se va a vivir con Matteo. La madre de Giulia se opera de la cadera. Ochenta años de la República. Teresa le dice que piensa jubilarse.",
                anioDeRoma(2) + listOf(
                    mundo("olimpiadas", "02-06", "Empiezan los Juegos de invierno en Milán y Cortina", certeza = APROXIMADO, fuente = F_INVIERNO_2026),
                    suceso("matteo", "03-14", "Se va a vivir con Matteo a un piso con terraza, en el Pigneto"),
                    suceso("cadera", "05-12", "Su madre se opera de la cadera, y Giulia duerme en el hospital"),
                    suceso("teresa", "10-08", "Teresa le dice que piensa jubilarse dentro de un par de años"),
                    suceso("bovedas", "11-20", "Terminan de limpiar la bóveda de la capilla"),
                )),
            TemporadaVida("2027", "elecciones",
                "Giulia encuentra la firma del pintor escondida en un pliegue de un manto. Samir aprende a reintegrar el color. Su madre se muda más cerca. Les dan un premio a la obra.",
                anioDeRoma(3) + listOf(
                    suceso("firma", "03-04", "Encuentra la firma del pintor escondida en el pliegue de un manto"),
                    suceso("samir_color", "05-20", "Samir aprende a reintegrar el color, y lo hace mejor que ella a su edad"),
                    suceso("madre_cerca", "09-25", "Su madre se muda a un piso a dos calles del suyo"),
                    suceso("visita", "10-20", "Llega una visita de colegios a ver la obra, y Giulia les explica el fresco desde abajo"),
                    suceso("premio", "11-25", "Le dan un premio a la obra, y ella no va a recogerlo"),
                )),
            TemporadaVida("2028", "los_angeles",
                "Se jubila Teresa, y Giulia queda al frente de la obra. Los Juegos de Los Ángeles, de madrugada en la tele. Giulia y Matteo se casan en el ayuntamiento. Entra el primer frío en la capilla.",
                anioDeRoma(4) + listOf(
                    suceso("jubila", "02-28", "Se jubila Teresa, y Giulia queda al frente de la obra"),
                    suceso("boda", "05-27", "Se casa con Matteo en el Campidoglio, con veinte invitados", escena = "fiesta"),
                    mundo("los_angeles", "07-14", "Empiezan los Juegos de Los Ángeles, de madrugada en la tele", certeza = APROXIMADO, fuente = F_LOS_ANGELES),
                    suceso("humedad", "10-15", "La humedad del otoño levanta un trozo de color y hay que fijarlo de urgencia"),
                    suceso("andamio_abajo", "11-30", "Bajan el andamio de la pared del altar, ya terminada"),
                )),
            TemporadaVida("2029", "europa",
                "Giulia espera un hijo. Hay elecciones europeas. Samir se va a restaurar a Nápoles. Se quita el último andamio y la capilla, restaurada, vuelve a abrirse.",
                anioDeRoma(5) + listOf(
                    suceso("espera", "02-20", "Le dice a Matteo que espera un hijo"),
                    mundo("europeas", "06-08", "Hay elecciones europeas", certeza = APROXIMADO, fuente = F_EUROPEAS),
                    suceso("samir_napoles", "07-10", "Samir se va a restaurar a Nápoles"),
                    suceso("ultimo_andamio", "10-12", "Se quita el último andamio: la capilla, restaurada, vuelve a ver la luz", cadena = "fin"),
                    suceso("abre_capilla", "11-08", "Se abre la capilla a la gente, y Giulia se queda al fondo, mirando", cadena = "fin"),
                )),
        ),
        arcos = listOf(Arco("fresco", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(1, "01-20", 0.0), p(2, "11-20", 0.45), p(4, "11-30", 0.85), p(5, "10-12", 1.0), p(5, "12-31", 1.0)))),
        objetos = listOf("hisopo", "bisturi", "lampara", "motoneta", "telefono", "mono", "moka", "cuentahilos", "pigmento", "cuaderno", "llaves", "andamio"),
        estados = listOf(Estado("agosto", "cierra_obra", "vuelve_obra")),
    )
}
