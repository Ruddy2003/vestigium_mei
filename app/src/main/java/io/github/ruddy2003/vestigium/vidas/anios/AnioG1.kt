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
 * Los calendarios del grupo 1 (tanda A1): las cinco temporadas (2027 a 2031) de los seis primeros personajes comunes de
 * «Otras vidas»: Naa, Ata, Ipy, Lucio, Alice y Citlali. Siguen las reglas de `AnioPiloto.kt`. Qué es real y qué
 * inventado, con cada fuente, está en `docs/historia/fuentes.md`.
 *
 * Los personajes comunes no tienen cuadro (decisión 2 del plan): solo cuento, en «Otras vidas». Aun así cada calendario
 * dice sus claves de escena, sus arcos y sus estados, porque el cuento también los lee (el párrafo del entorno sale de
 * los arcos y las variantes dependen del estado).
 *
 * Lo propio de este grupo:
 *
 * - **Naa y Ata viven antes de la escritura.** No hay un solo hecho con fecha: todo lo que vuelve cada año (las lluvias,
 *   las manadas, la siega) va como «aproximado», con la página que lo describe en pueblos o sitios parecidos, y lo demás
 *   es inventado. Sus años no tienen número: cada temporada lleva el nombre de lo que pasó, como en las cuentas de
 *   inviernos de los pueblos sin escritura.
 * - **Ipy** usa el calendario civil egipcio de 365 días, que se corre un día cada cuatro años contra el nuestro. Sus
 *   fiestas se calcularon desde el día en que subió al trono Ramsés II (ver `fuentes.md`): son «aproximado», porque ese
 *   día es una reconstrucción de los egiptólogos.
 * - **Alice** tiene la Pascua juliana de cada año calculada, como Pierre. Ver [CALCULADOS].
 * - **Citlali** sigue las veintenas del año mexica con las fechas de Sahagún: «aproximado».
 */
internal object AnioG1 {
    val TODOS: List<Calendario> by lazy { listOf(NAA, ATA, IPY, LUCIO, ALICE, CITLALI) }

    /**
     * Días «históricos» sin enlace porque su fecha sale de un cálculo hecho en la sesión: la Pascua juliana de 1300 a
     * 1304 y lo que cuelga de ella, en el calendario de Alice. Son los mismos ids que los de Pierre.
     */
    val CALCULADOS = setOf("carnaval", "pascua", "rogativas")

    private val W = "https://en.wikipedia.org/wiki/"

    // =====================================================================
    //  Naa, cazadora y recolectora. Una sabana de África oriental, hace unos 15,000 años.
    // =====================================================================
    //
    // Su gente (toda inventada): Ekwa, su compañero, cazador. Lali, su hija (7 años en la temporada 1), y Tiko, su hijo
    // (3). Mai, la hija que nace en la temporada 2. Sese, su madre, la más vieja del campamento, que muere en la 3. Iru,
    // su hermana, que se va a otro campamento tras una pelea (2) y vuelve con un hijo (3). Bako, el viejo que sabe las
    // historias.
    //
    // Lo real: hace unos 15,000 años empezó en África el «periodo húmedo»: volvieron las lluvias y los lagos subieron.
    // El año de la sabana (las lluvias largas y las cortas, las crías de los ñus, la miel, los baobabs, el lago que se
    // seca, las bayas, la caza en las aguadas, el baile de la noche sin luna) sale de lo que se sabe de los hadza de
    // Tanzania y del Serengueti de hoy: es una analogía, no un registro de su época. [Aproximación]
    //
    // Claves de escena: lluvia, manadas, miel, aguada, bayas, mudanza (el campamento que se levanta), fuego, luto,
    // noche_sin_luna, lago. Arcos: lago (cuánta agua tiene: sube un poco cada año) y pasto (verde en las lluvias,
    // amarillo en la seca).
    //
    // Lo que el cuadro tendría que mostrar, si algún día lo tiene: el lago según `lago` (en la temporada 1 todavía se
    // cruza a pie en septiembre; desde la 3 ya no) y el pasto según `pasto`.

    private val F_HADZA = "Los hadza" to "${W}Hadza_people"
    private val F_SERENGETI = "El Serengueti: las lluvias y la gran migración" to "${W}Serengeti"
    private val F_EYASI = "El lago Eyasi, que en la seca se cruza a pie" to "${W}Lake_Eyasi"
    private val F_HUMEDO = "El periodo húmedo africano" to "${W}African_humid_period"
    private val F_BAOBAB = "El baobab africano" to "${W}Adansonia_digitata"
    private val F_GUIA = "El indicador grande, el pájaro que lleva a la miel" to "${W}Greater_honeyguide"

    /** La noche sin luna del baile, en cada temporada. La luna de hace 15,000 años no se puede calcular: el día se eligió. */
    private val EPEME = listOf("07-14", "07-03", "07-22", "07-11", "06-30")

    /** Lo que vuelve cada año en la sabana, en la temporada [t]. */
    private fun anioDeLaSabana(t: Int): List<DiaEspecial> = listOfNotNull(
        mundo("crias", "02-03", "Nacen las crías de los ñus en la llanura", 18, APROXIMADO, F_SERENGETI, "manadas"),
        mundo("lluvias_largas", "03-12", "Llegan las lluvias largas", certeza = APROXIMADO, fuente = F_SERENGETI, escena = "lluvia", clima = Clima.TORMENTA),
        cosecha("miel", "04-20", "El tiempo de la miel: el pájaro guía llama desde los árboles", 25, APROXIMADO, F_GUIA, "miel"),
        cosecha("baobab", "06-20", "Los baobabs sin hojas, y los frutos secos colgando", 20, APROXIMADO, F_BAOBAB),
        rito("noche_sin_luna", EPEME[t - 1], "La noche sin luna: con los fuegos apagados, los hombres bailan y las mujeres cantan", certeza = APROXIMADO, fuente = F_HADZA, escena = "noche_sin_luna"),
        cosecha("aguadas", "08-10", "Los hombres pasan las noches esperando junto a las aguadas", 30, APROXIMADO, F_HADZA, "aguada"),
        if (t <= 2) mundo("lago_seco", "09-25", "El lago se seca y se cruza a pie", certeza = APROXIMADO, fuente = F_EYASI, escena = "lago")
        else mundo("lago_alto", "09-25", "Es la seca y el lago no se va: ya no se cruza a pie", certeza = APROXIMADO, fuente = F_HUMEDO, escena = "lago"),
        mundo("lluvias_cortas", "10-28", "Vuelven las lluvias cortas", certeza = APROXIMADO, fuente = F_SERENGETI, escena = "lluvia", clima = Clima.LLUVIA),
        cosecha("bayas", "12-01", "El tiempo de las bayas: más de cien personas en un solo campamento", 24, APROXIMADO, F_HADZA, "bayas"),
    )

    val NAA = Calendario(
        vida = "paleolitico",
        estaciones = listOf("03-12" to Estacion.LLUVIAS, "06-01" to Estacion.SECAS, "10-28" to Estacion.LLUVIAS, "12-25" to Estacion.SECAS),
        clima = listOf(
            mes(despejado = 12, nubes = 6, lluvia = 4, tormenta = 1), mes(despejado = 13, nubes = 5, lluvia = 3, viento = 1),
            mes(despejado = 5, nubes = 8, lluvia = 10, tormenta = 3), mes(despejado = 3, nubes = 8, lluvia = 12, tormenta = 4),
            mes(despejado = 7, nubes = 8, lluvia = 7, tormenta = 2), mes(despejado = 18, nubes = 4, lluvia = 1, viento = 4),
            mes(despejado = 20, nubes = 2, viento = 5, polvo = 2), mes(despejado = 18, nubes = 2, viento = 6, polvo = 3),
            mes(despejado = 18, nubes = 3, viento = 5, polvo = 3), mes(despejado = 12, nubes = 6, lluvia = 4, tormenta = 1, viento = 2),
            mes(despejado = 8, nubes = 8, lluvia = 8, tormenta = 3), mes(despejado = 10, nubes = 8, lluvia = 6, tormenta = 2),
        ),
        temporadas = listOf(
            TemporadaVida("Hace unos 15,000 años: el año del agua que se queda", "lluvias_vuelven",
                "Pasan las lluvias y el lago guarda agua más tiempo que nunca, aunque en la seca todavía se cruza a pie. Lali saca sola su primer tubérculo. Ekwa mata una jirafa y el campamento se muda junto a ella. Naa espera otro hijo. Sese se lastima un pie.",
                anioDeLaSabana(1) + listOf(
                    suceso("palo_lali", "01-20", "Lali saca sola su primer tubérculo con el palo de cavar"),
                    suceso("gigantes", "03-30", "El viejo Bako cuenta la historia de los gigantes peludos que no tenían fuego", escena = "fuego"),
                    mundo("agua_queda", "05-25", "Pasan las lluvias y el agua del lago no se va", certeza = APROXIMADO, fuente = F_HUMEDO, escena = "lago"),
                    suceso("tiko_arroyo", "06-05", "Tiko sigue a los niños grandes hasta el arroyo y vuelve solo"),
                    suceso("jirafa", "07-28", "Ekwa mata una jirafa lejos: todo el campamento se muda junto a ella", escena = "mudanza"),
                    suceso("espera", "09-02", "Naa sabe que espera otro hijo"),
                    mundo("leon", "10-08", "Un león ronda el campamento tres noches seguidas", 3, INVENTADO, escena = "fuego"),
                    suceso("pie_sese", "11-12", "Su madre, Sese, se lastima un pie y ya no puede caminar lejos"),
                )),
            TemporadaVida("Hace unos 15,000 años: el año de Mai", "lluvias_vuelven",
                "El lago crece y se come la orilla vieja. Nace Mai, a la sombra, entre las mujeres. Iru discute con los de su fuego y se va a otro campamento. Sese vuelve a caminar con un bastón. Lali ve por primera vez cómo se ahúma a las abejas.",
                anioDeLaSabana(2) + listOf(
                    suceso("sese_camina", "02-25", "Sese vuelve a caminar, despacio, con un bastón"),
                    mundo("orilla", "04-05", "El lago crece y se come la orilla vieja: hay que mudarse más arriba", certeza = APROXIMADO, fuente = F_HUMEDO, escena = "mudanza"),
                    suceso("humo_abejas", "04-28", "Lali ve por primera vez cómo los hombres ahúman a las abejas", escena = "miel"),
                    suceso("nace_mai", "05-02", "Nace su hija, a la sombra, entre las mujeres", cadena = "mai"),
                    suceso("nombre_mai", "05-20", "Le ponen nombre a la niña: Mai", cadena = "mai"),
                    suceso("se_va_iru", "07-30", "Su hermana Iru discute con los de su fuego y se va a otro campamento"),
                    suceso("mai_rie", "10-15", "Mai se ríe por primera vez"),
                    mundo("estrellas", "11-22", "Una lluvia de estrellas cruza la noche entera", certeza = INVENTADO),
                )),
            TemporadaVida("Hace unos 15,000 años: el año en que se fue Sese", "lluvias_vuelven",
                "Muere Sese y dejan el campamento donde murió. Hay peces en el lago nuevo. Ekwa le hace a Tiko su primer arco. Mai camina. Iru vuelve al fuego de Naa con un hijo nuevo. Ya es la seca y el lago no se va.",
                anioDeLaSabana(3) + listOf(
                    suceso("muere_sese", "02-18", "Muere Sese, su madre, la más vieja del campamento", escena = "luto"),
                    rito("dejan_campamento", "02-20", "Dejan el campamento donde murió Sese y lo levantan lejos", certeza = APROXIMADO, fuente = F_HADZA, escena = "mudanza"),
                    suceso("arco_tiko", "04-02", "Ekwa le hace a Tiko su primer arco"),
                    mundo("peces", "06-10", "Hay peces en el lago nuevo: los atrapan con las manos en la orilla", certeza = APROXIMADO, fuente = F_HADZA, escena = "lago"),
                    suceso("mai_camina", "08-22", "Mai da sus primeros pasos"),
                    suceso("vuelve_iru", "10-05", "Iru vuelve al fuego de Naa con un hijo nuevo"),
                    suceso("ocre", "11-15", "Naa muele ocre con Iru para pintarse para el baile"),
                    suceso("cesta_lali", "12-12", "Lali llena sola su cesta de bayas", escena = "bayas"),
                )),
            TemporadaVida("Hace unos 15,000 años: el año del búfalo", "lluvias_vuelven",
                "Llueve tanto que el arroyo se vuelve río. Un búfalo hiere a Ekwa y Naa sale sola con las mujeres hasta que él vuelve a cazar. Lali llega con ellas hasta los montes del otro lado. Tiko habla con el pájaro de la miel.",
                anioDeLaSabana(4) + listOf(
                    suceso("bufalo", "03-20", "Un búfalo hiere a Ekwa en el muslo", cadena = "ekwa"),
                    mundo("arroyo_rio", "04-08", "Llueve tanto que el arroyo se vuelve río", certeza = INVENTADO, escena = "lluvia", clima = Clima.LLUVIA),
                    suceso("ekwa_caza", "05-18", "Ekwa vuelve a cazar", cadena = "ekwa"),
                    suceso("lali_lejos", "07-08", "Lali va por primera vez con las mujeres hasta los montes del otro lado"),
                    mundo("polvo", "08-28", "Días de viento y polvo: no se ve el otro lado del lago", 3, INVENTADO, clima = Clima.POLVO),
                    suceso("tiko_pajaro", "09-10", "Tiko silba y el pájaro de la miel le contesta", escena = "miel"),
                    suceso("mai_fuego", "11-02", "Naa le enseña a Mai a no acercarse al fuego", escena = "fuego"),
                    suceso("prima", "12-20", "Dos campamentos se juntan para las bayas y Naa vuelve a ver a su prima", escena = "bayas"),
                )),
            TemporadaVida("Hace unos 15,000 años: el año de la otra orilla", "lluvias_vuelven",
                "El lago es grande y una manada se queda todo el año junto al agua nueva. Cruzan con los niños a la otra orilla. Mai habla, Tiko caza su primer dik-dik y Lali canta con las mujeres. Naa le enseña a Lali dónde crecen los tubérculos que no se acaban.",
                anioDeLaSabana(5) + listOf(
                    suceso("mai_habla", "02-26", "Mai dice su primera palabra: «miel»"),
                    suceso("arbol_ocre", "04-01", "Naa marca con ocre el árbol grande del campamento"),
                    mundo("manada_queda", "05-10", "Una manada se queda todo el año junto al agua nueva", certeza = INVENTADO, escena = "manadas"),
                    suceso("tronco", "06-15", "Cruzan con los niños a la otra orilla, agarrados de un tronco", escena = "lago"),
                    suceso("dik_dik", "08-30", "Tiko caza su primer dik-dik"),
                    suceso("lali_canta", "10-10", "Lali canta por primera vez con las mujeres"),
                    suceso("tuberculos", "11-18", "Naa le enseña a Lali dónde crecen los tubérculos que no se acaban"),
                )),
        ),
        arcos = listOf(
            Arco("lago", TipoArco.OTRO, listOf(p(1, "01-01", 0.25), p(1, "05-25", 0.55), p(1, "09-25", 0.12), p(1, "12-31", 0.3), p(2, "05-20", 0.65), p(2, "09-25", 0.22),
                p(2, "12-31", 0.4), p(3, "05-20", 0.8), p(3, "09-25", 0.5), p(3, "12-31", 0.6), p(4, "05-20", 0.95), p(4, "09-25", 0.7), p(4, "12-31", 0.75),
                p(5, "05-20", 1.0), p(5, "09-25", 0.8), p(5, "12-31", 0.85))),
            Arco.cadaAnio("pasto", TipoArco.CULTIVO, "01-15" to 0.6, "02-25" to 0.45, "03-30" to 0.9, "05-20" to 1.0, "07-01" to 0.4, "09-15" to 0.1, "11-15" to 0.65),
        ),
        objetos = listOf("palo_cavar", "cesta", "piel", "calabaza", "collar", "ocre", "taladro_fuego", "panal", "fruto_baobab", "huevo_avestruz", "cuchillo_piedra", "arco"),
    )

    // =====================================================================
    //  Ata, agricultor. Çatalhöyük, Anatolia, hacia el 7000 a. C.
    // =====================================================================
    //
    // Su gente (toda inventada): Nesu, su mujer. Ilka, su hija (8 años en la temporada 1), y Tamu, su hijo (4). Seli, la
    // hija que nace en la temporada 4. Ari, su madre, que muere en la 2 y queda enterrada bajo la plataforma donde dormía.
    // Dosh, el vecino cuyo techo toca el suyo. Uma, la vieja que pinta las paredes.
    //
    // Lo real: el pueblo sin calles donde se entra por el techo, las paredes enlucidas de blanco, los muertos bajo el
    // piso, los cuernos de uro en las paredes, el trigo, las ovejas, la obsidiana de las montañas volcánicas y la casa
    // nueva que se levanta sobre la vieja. El monte Hasan, de dos picos, tuvo una erupción hace unos 8,970 años (con un
    // margen de 640): pudo ser en la vida de Ata. Hay en el pueblo una pintura que algunos leen como el pueblo con el monte
    // que humea; que la pintaran Uma y Ata es inventado.
    //
    // Claves de escena: enlucido, cosecha, fiesta, cuernos, humo, ceniza, luto, nieve, grietas, obra. Arcos: trigo, humo
    // (el penacho del monte) y casa (la casa nueva que levantan sobre la vieja en la temporada 4: 0 = la vieja, 1 =
    // terminada).
    //
    // Lo que el cuadro tendría que mostrar, si algún día lo tiene: el humo en el horizonte según `humo`, la casa según
    // `casa` y a nadie en el techo mientras Ata está en la «montaña» (el estado).

    private val F_CATAL = "Çatalhöyük" to "${W}%C3%87atalh%C3%B6y%C3%BCk"
    private val F_HASAN = "El monte Hasan y sus erupciones" to "${W}Hasan_Da%C4%9F"
    private val F_GOLLU = "Göllü Dağ, la montaña de la obsidiana" to "${W}G%C3%B6ll%C3%BC_Da%C4%9F"
    private val F_URO = "El uro" to "${W}Aurochs"

    /** La primera nieve de cada temporada. */
    private val NIEVE = listOf("12-15", "12-02", "12-22", "11-28", "12-10")

    /** Lo que vuelve cada año en el pueblo, en la temporada [t]. */
    private fun anioDelPueblo(t: Int): List<DiaEspecial> = listOf(
        cosecha("corderos", "02-25", "Nacen los corderos y los cabritos en los corrales", 20, APROXIMADO, F_CATAL, "corderos"),
        rito("enlucido", "04-15", "Enlucen de blanco las paredes y las plataformas de la casa", certeza = APROXIMADO, fuente = F_CATAL, escena = "enlucido"),
        cosecha("lentejas", "06-02", "Arrancan las lentejas y los chícharos", 8, APROXIMADO),
        cosecha("siega", "06-20", "La siega del trigo y la cebada", 24, APROXIMADO, F_CATAL),
        fiesta("techos", "07-20", "Fiesta en los techos: el pan del grano nuevo en los hornos de todos", certeza = APROXIMADO, fuente = F_CATAL),
        cosecha("obsidiana", "08-25", "Sale el grupo que sube a la montaña de la obsidiana", 12, APROXIMADO, F_GOLLU, ""),
        cosecha("siembra", "10-12", "La siembra del trigo de invierno", 20, APROXIMADO, F_CATAL),
        fiesta("uro", "11-20", "Cazan un uro: festín, y los cuernos van a una pared", 2, APROXIMADO, F_URO, "cuernos"),
        mundo("nieve", NIEVE[t - 1], "La primera nieve sobre los techos", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
    )

    val ATA = Calendario(
        vida = "neolitico",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 7, nubes = 9, lluvia = 3, nieve = 6, niebla = 4, viento = 2), mes(despejado = 7, nubes = 9, lluvia = 4, nieve = 5, niebla = 3, viento = 2),
            mes(despejado = 9, nubes = 9, lluvia = 7, nieve = 2, viento = 3), mes(despejado = 11, nubes = 9, lluvia = 8, tormenta = 1, viento = 2),
            mes(despejado = 14, nubes = 8, lluvia = 6, tormenta = 2, viento = 1), mes(despejado = 21, nubes = 5, lluvia = 2, tormenta = 2),
            mes(despejado = 26, nubes = 2, viento = 2, polvo = 1), mes(despejado = 26, nubes = 2, viento = 2, polvo = 1),
            mes(despejado = 22, nubes = 4, lluvia = 1, viento = 2, polvo = 1), mes(despejado = 15, nubes = 8, lluvia = 5, niebla = 2, viento = 1),
            mes(despejado = 10, nubes = 9, lluvia = 6, nieve = 1, niebla = 4, viento = 1), mes(despejado = 7, nubes = 9, lluvia = 5, nieve = 5, niebla = 5, viento = 1),
        ),
        temporadas = listOf(
            TemporadaVida("Hacia el 7000 a. C.: el año de los cuernos de su padre", "paz",
                "Cuelga en la pared los cuernos del uro que cazó su padre. Ilka teje su primera estera. El vecino Dosh levanta su casa nueva sobre la vieja. Ata sube con los de la obsidiana a la montaña y vuelve con un fardo. Tamu sube solo la escalera del techo.",
                anioDelPueblo(1) + listOf(
                    suceso("cuernos_padre", "01-18", "Cuelga en la pared los cuernos del uro que cazó su padre", escena = "cuernos"),
                    suceso("estera", "03-20", "Ilka teje su primera estera de juncos"),
                    suceso("casa_dosh", "05-12", "El vecino Dosh tumba su casa vieja para levantar la nueva encima", escena = "obra"),
                    mundo("granizo", "05-28", "Cae granizo sobre el trigo verde", certeza = INVENTADO, clima = Clima.TORMENTA),
                    suceso("sube_montana", "08-25", "Ata sube con los de la obsidiana a la montaña", cadena = "montana"),
                    suceso("baja_montana", "09-06", "Vuelve de la montaña con un fardo de obsidiana", cadena = "montana"),
                    suceso("escalera_tamu", "11-02", "Tamu sube solo la escalera del techo por primera vez"),
                )),
            TemporadaVida("Hacia el 7000 a. C.: el año del monte que humea", "humo",
                "Muere Ari, su madre, y la entierran bajo la plataforma donde dormía. El monte de dos picos echa humo. Nesu cambia una canasta por conchas del mar. Uma pinta en la pared el pueblo y el monte que humea, y Ata le muele el ocre.",
                anioDelPueblo(2) + listOf(
                    suceso("tos_ari", "01-30", "Su madre, Ari, empieza a toser y ya no sube al techo", cadena = "ari"),
                    suceso("muere_ari", "03-08", "Muere Ari", cadena = "ari", escena = "luto"),
                    suceso("entierran_ari", "03-09", "Entierran a Ari bajo la plataforma donde dormía", cadena = "ari", escena = "luto"),
                    mundo("monte_humea", "05-18", "El monte de dos picos echa humo en el horizonte", certeza = APROXIMADO, fuente = F_HASAN, escena = "humo"),
                    suceso("conchas", "07-02", "Nesu cambia una canasta por conchas que vienen del mar"),
                    suceso("pintura", "09-20", "Uma pinta en la pared el pueblo y el monte que humea, y Ata le muele el ocre"),
                    suceso("honda_tamu", "12-20", "Tamu aprende a tirar con la honda"),
                )),
            TemporadaVida("Hacia el 7000 a. C.: el año de la ceniza", "ceniza",
                "Tamu tiene fiebre y sana. Cae ceniza fina del monte y la espiga sale vacía en los campos del sur. Ata cambia obsidiana por grano con la gente del río. Ilka cuece pan sola. Nesu espera otro hijo.",
                anioDelPueblo(3) + listOf(
                    suceso("fiebre_tamu", "01-22", "Tamu tiene fiebre", 3, cadena = "fiebre"),
                    suceso("sana_tamu", "01-26", "Tamu se levanta y pide pan", cadena = "fiebre"),
                    mundo("ceniza", "04-02", "Cae ceniza fina del monte: el cielo se pone amarillo", 3, APROXIMADO, F_HASAN, "ceniza", Clima.POLVO),
                    mundo("espiga_vacia", "06-30", "La espiga sale vacía en los campos del sur", certeza = INVENTADO),
                    suceso("pan_ilka", "07-28", "Ilka cuece pan sola en el horno del techo"),
                    suceso("cambio_grano", "09-15", "Cambia obsidiana por grano con la gente del río"),
                    suceso("espera_nesu", "11-08", "Nesu espera otro hijo"),
                )),
            TemporadaVida("Hacia el 7000 a. C.: el año de la casa nueva", "paz",
                "Tiembla la tierra y se agrietan las paredes. Tumban la casa vieja hasta las rodillas, la rellenan y levantan la nueva encima. Nace Seli en la casa nueva. Ata cuelga ahí los cuernos de su padre.",
                anioDelPueblo(4) + listOf(
                    suceso("mano_ilka", "01-08", "Ilka pinta su primera mano en la pared"),
                    mundo("tiembla", "02-10", "Tiembla la tierra y se agrietan las paredes del lado del sol", certeza = INVENTADO, escena = "grietas"),
                    suceso("tumban", "03-25", "Tumban la casa vieja hasta las rodillas y la rellenan de tierra", cadena = "casa", escena = "obra"),
                    suceso("adobes", "04-01", "Hacen adobes con el lodo de la orilla", 10, cadena = "casa", escena = "obra"),
                    suceso("vigas", "05-20", "Ponen las vigas del techo nuevo", cadena = "casa", escena = "obra"),
                    suceso("nace_seli", "08-05", "Nace su hija Seli, en la casa nueva"),
                    suceso("cuernos_casa", "10-30", "Cuelga los cuernos de su padre en la pared de la casa nueva", escena = "cuernos"),
                )),
            TemporadaVida("Hacia el 7000 a. C.: el año de su uro", "paz",
                "El monte ya no humea. Tamu talla su primera lasca y Seli gatea por el techo. La familia de Dosh pide a Ilka para su hijo, para cuando crezcan. Ata vuelve a subir a la montaña y hiere su propio uro.",
                anioDelPueblo(5) + listOf(
                    mundo("hielo", "01-15", "El río se hiela en la orilla", certeza = INVENTADO, clima = Clima.NIEVE),
                    suceso("lasca_tamu", "03-15", "Tamu talla su primera lasca de obsidiana"),
                    suceso("promesa_ilka", "04-25", "La familia de Dosh pide a Ilka para su hijo, para cuando crezcan"),
                    suceso("seli_gatea", "06-12", "Seli gatea hasta el borde del techo y Ilka la atrapa"),
                    mundo("monte_quieto", "07-05", "El monte ya no humea", certeza = INVENTADO),
                    suceso("sube_montana", "08-12", "Ata sube con los de la obsidiana a la montaña", cadena = "montana"),
                    suceso("baja_montana", "08-24", "Vuelve de la montaña con un fardo de obsidiana", cadena = "montana"),
                    suceso("uro_ata", "10-15", "Ata hiere un uro, y los cuernos son suyos", escena = "cuernos"),
                )),
        ),
        arcos = listOf(
            Arco.cadaAnio("trigo", TipoArco.CULTIVO, "01-01" to 0.15, "03-15" to 0.3, "05-01" to 0.7, "06-19" to 1.0, "07-14" to 0.0, "10-11" to 0.0, "11-20" to 0.12),
            Arco("humo", TipoArco.DANOS, listOf(p(1, "01-01", 0.0), p(2, "05-17", 0.0), p(2, "05-18", 0.6, true), p(2, "12-31", 0.5), p(3, "04-01", 0.6), p(3, "04-02", 1.0, true),
                p(3, "04-05", 1.0), p(3, "06-01", 0.4), p(3, "12-31", 0.2), p(4, "12-31", 0.05), p(5, "07-05", 0.0))),
            Arco("casa", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(4, "03-24", 0.0), p(4, "04-01", 0.1), p(4, "04-11", 0.35), p(4, "05-20", 0.75), p(4, "06-15", 1.0))),
        ),
        objetos = listOf("hoz", "piedra_moler", "obsidiana", "estera", "honda", "cesta", "figurilla", "cuerno", "ocre", "escalera", "cuenco", "espejo_obsidiana"),
        estados = listOf(Estado("montana", "sube_montana", "baja_montana")),
    )

    // =====================================================================
    //  Ipy, campesino. Tebas, del 1250 al 1246 a. C.
    // =====================================================================
    //
    // Su gente (toda inventada): Henut, su mujer, que teje. Paser, su hijo (6 años en la temporada 1), y Tia, su hija (2).
    // Amenemope, el hijo que nace en la temporada 4. Baket, su madre, que muere en la 3. Nebamun, su hermano, que lleva
    // agua al otro lado del río. Huy, el vecino. Ramose, el escriba que mide los campos.
    //
    // Lo real: Ramsés II lleva treinta años en el trono y celebra su primer jubileo (hacia el 1249 a. C.); su hijo
    // Khaemuaset lo pregona río arriba; hay un segundo jubileo años después y en el otoño del 1246 a. C. sale de Hattusa
    // la princesa hitita que se casará con él. Las fiestas de Tebas (el año nuevo, Opet, Joiak, la Fiesta del Valle y
    // los cinco días de fuera del año) siguen el calendario civil, que no tenía bisiestos: sus fechas se calcularon desde
    // el día en que Ramsés subió al trono (III Shemu 27 = 31 de mayo de 1279 a. C.). [Aproximación]
    //
    // Claves de escena: fiesta, crecida, obra (los campesinos se van a la obra del rey), cosecha, luto, seco. Arcos: rio
    // (cuánto cubre el agua; la crecida de la temporada 3 es baja) y campo (el trigo y la cebada, de la siembra a la
    // siega).
    //
    // Lo que el cuadro tendría que mostrar, si algún día lo tiene: el agua sobre los campos según `rio`, el trigo según
    // `campo` y la casa vacía de día mientras Ipy está en la «obra» (el estado).

    private val F_CIVIL = "El calendario egipcio" to "${W}Egyptian_calendar"
    private val F_OPET = "La fiesta de Opet" to "${W}Opet_Festival"
    private val F_JOIAK = "El mes de Joiak y los ritos de Osiris" to "${W}Khoiak"
    private val F_VALLE = "La Bella Fiesta del Valle" to "${W}Beautiful_Festival_of_the_Valley"
    private val F_CRECIDA = "La crecida del Nilo" to "${W}Flooding_of_the_Nile"
    private val F_PERET = "Peret, la estación de la siembra" to "${W}Peret"
    private val F_SHEMU = "Shemu, la estación de la cosecha" to "${W}Shemu"
    private val F_SED = "El jubileo del rey (Heb Sed)" to "${W}Sed_festival"
    private val F_KHAEMUASET = "Khaemuaset, el hijo de Ramsés que pregonaba los jubileos" to "${W}Khaemweset"
    private val F_PRINCESA = "Maathorneferure, la princesa hitita" to "${W}Maathorneferure"

    // Las fechas julianas de cada fiesta en las cinco temporadas (1250, 1249, 1248, 1247 y 1246 a. C.).
    private val ANIO_NUEVO = listOf("07-02", "07-01", "07-01", "07-01", "07-01")   // I Akhet 1
    private val EPAGOMENOS = listOf("06-27", "06-26", "06-26", "06-26", "06-26")   // los cinco días antes del año nuevo
    private val OPET = listOf("08-15", "08-14", "08-14", "08-14", "08-14")         // II Akhet 15
    private val JOIAK = listOf("10-17", "10-16", "10-16", "10-16", "10-16")        // IV Akhet 18 a 30
    private val VALLE = listOf("05-02", "05-20", "05-09", "04-29", "05-18")        // la luna nueva de II Shemu

    /** Lo que vuelve cada año en Tebas, en la temporada [t]. */
    private fun anioDeTebas(t: Int): List<DiaEspecial> = listOf(
        rito("diques", "01-15", "Limpian los canales y levantan los diques", 10, APROXIMADO),
        cosecha("siega", "03-20", "La siega del trigo y la cebada", 30, APROXIMADO, F_SHEMU),
        fiesta("valle", VALLE[t - 1], "La Fiesta del Valle: flores para los muertos al otro lado del río", 2, APROXIMADO, F_VALLE),
        rito("escriba", "05-28", "El escriba Ramose mide el campo y cuenta el grano del rey", certeza = INVENTADO),
        fiesta("epagomenos", EPAGOMENOS[t - 1], "Los cinco días fuera del año: los cumpleaños de los dioses", 5, APROXIMADO, F_CIVIL),
        fiesta("anio_nuevo", ANIO_NUEVO[t - 1], "Año nuevo: empieza la estación de la inundación", certeza = APROXIMADO, fuente = F_CIVIL),
        mundo("crecida", "07-25", "El río empieza a subir frente a Tebas", certeza = APROXIMADO, fuente = F_CRECIDA, escena = "crecida"),
        fiesta("opet", OPET[t - 1], "La fiesta de Opet: la barca de Amón va de Karnak a Luxor", 11, APROXIMADO, F_OPET),
        mundo("leva", "08-28", "El campo está bajo el agua: llaman a los campesinos a la obra del rey", certeza = APROXIMADO, escena = "obra"),
        fiesta("joiak", JOIAK[t - 1], "Joiak: los ritos de Osiris, que muere y vuelve como el trigo", 13, APROXIMADO, F_JOIAK),
        mundo("baja_agua", "10-28", "Baja el agua y vuelve a su campo: se acaba la obra", certeza = APROXIMADO),
        cosecha("siembra", "11-05", "La siembra: el trigo y la cebada en el lodo nuevo", 20, APROXIMADO, F_PERET),
    )

    /** El río sobre los campos, año por año: sube desde fines de julio, cubre todo a mediados de septiembre y baja en octubre. */
    private val RIO = run {
        val altura = listOf(1.0, 1.0, 0.6, 1.0, 0.95)  // en la temporada 3 el río no sube lo que debe
        val puntos = mutableListOf(p(1, "01-01", 0.1))
        altura.forEachIndexed { i, h ->
            val t = i + 1
            puntos += listOf(p(t, "05-20", 0.0), p(t, "07-25", 0.03), p(t, "09-15", h), p(t, "10-28", h * 0.45), p(t, "11-30", 0.15), p(t, "12-31", 0.1))
        }
        puntos
    }

    val IPY = Calendario(
        vida = "egipto",
        estaciones = listOf("03-15" to Estacion.COSECHA, "07-25" to Estacion.INUNDACION, "11-01" to Estacion.SIEMBRA),
        clima = listOf(
            mes(despejado = 20, nubes = 6, lluvia = 1, viento = 2, niebla = 1), mes(despejado = 21, nubes = 5, viento = 3, niebla = 1),
            mes(despejado = 20, nubes = 3, viento = 4, polvo = 3), mes(despejado = 19, nubes = 2, viento = 4, polvo = 5),
            mes(despejado = 21, nubes = 1, viento = 4, polvo = 4), mes(despejado = 27, nubes = 1, viento = 2),
            mes(despejado = 28, nubes = 1, viento = 1), mes(despejado = 27, nubes = 2, viento = 1),
            mes(despejado = 26, nubes = 2, viento = 2), mes(despejado = 24, nubes = 3, viento = 2, polvo = 1),
            mes(despejado = 22, nubes = 5, viento = 2, niebla = 1), mes(despejado = 20, nubes = 6, lluvia = 1, viento = 2, niebla = 1),
        ),
        temporadas = listOf(
            TemporadaVida("1250 a. C., año 30 de Ramsés", "paz",
                "Buena crecida. Ipy va a la obra del rey mientras el campo está bajo el agua. Paser espanta a los pájaros del trigo y Tia dice el nombre de su abuela. Le prestan un buey. En invierno pasan por el río los pregoneros del jubileo del rey.",
                anioDeTebas(1) + listOf(
                    suceso("pajaros", "02-12", "Paser espanta a los pájaros del trigo con una honda"),
                    suceso("tia_habla", "04-25", "Tia dice el nombre de su abuela"),
                    suceso("techo", "06-08", "Arregla el techo de palma antes del calor"),
                    suceso("nebamun", "09-15", "Su hermano Nebamun cruza el río con historias de las tumbas de los reyes"),
                    suceso("buey", "12-02", "Le prestan un buey para arar, a cambio de tres sacos de cebada"),
                    mundo("pregoneros", "12-20", "Pasan por el río los pregoneros del jubileo del rey", certeza = APROXIMADO, fuente = F_KHAEMUASET),
                )),
            TemporadaVida("1249 a. C., año 31 de Ramsés", "jubileo",
                "El jubileo del rey: treinta años en el trono, y raciones de fiesta. Henut vende un lienzo de su telar. Un cocodrilo se lleva una cabra del vecino. Baket se cae y ya no camina sin bastón. Paser carga su primera canasta de lodo en el dique.",
                anioDeTebas(2) + listOf(
                    suceso("paser_dique", "01-17", "Paser carga su primera canasta de lodo en el dique"),
                    mundo("jubileo", "02-02", "El jubileo del rey: treinta años en el trono", 5, APROXIMADO, F_SED, "fiesta"),
                    suceso("raciones", "02-04", "Reparten raciones de fiesta por el jubileo: pan blanco y carne"),
                    suceso("lienzo", "06-05", "Henut vende en el mercado un lienzo de su telar"),
                    suceso("cocodrilo", "08-01", "Un cocodrilo se lleva una cabra del vecino Huy", escena = "crecida"),
                    suceso("cae_baket", "12-01", "Su madre, Baket, se cae y ya no camina sin bastón"),
                )),
            TemporadaVida("1248 a. C., año 32 de Ramsés", "crecida_baja",
                "El año malo: se acaba el grano en muchas casas y el templo reparte raciones. Muere Baket y en la Fiesta del Valle le llevan flores. El río no sube lo que debe. Henut espera otro hijo. Ipy vende la cabra por grano.",
                anioDeTebas(3) + listOf(
                    mundo("hambre", "02-15", "Se acaba el grano en muchas casas: el templo reparte raciones", certeza = INVENTADO),
                    suceso("muere_baket", "04-28", "Muere Baket, su madre", escena = "luto"),
                    suceso("tia_canta", "07-10", "Tia canta la canción del río que le enseñó la abuela"),
                    mundo("rio_bajo", "09-10", "El río no sube lo que debe: los campos altos se quedan secos", certeza = INVENTADO, escena = "seco"),
                    suceso("espera_henut", "10-02", "Henut espera otro hijo"),
                    suceso("vende_cabra", "12-08", "Vende la cabra por grano"),
                )),
            TemporadaVida("1247 a. C., año 33 de Ramsés", "paz",
                "Planta una palmera y paga la cebada que debía desde el año malo. Nace Amenemope. Esta vez el río cubre hasta los campos altos. Paser va con él a la obra. En invierno pregonan un segundo jubileo del rey.",
                anioDeTebas(4) + listOf(
                    suceso("palmera", "01-05", "Planta una palmera de dátiles junto a la casa"),
                    suceso("paga_cebada", "02-20", "Paga por fin la cebada que debía desde el año malo"),
                    suceso("nace_amenemope", "06-10", "Nace su hijo Amenemope"),
                    suceso("paser_obra", "09-01", "Paser va con él a la obra del rey por primera vez"),
                    mundo("crecida_buena", "09-12", "Esta vez el río cubre hasta los campos altos", certeza = INVENTADO, escena = "crecida"),
                    mundo("otro_jubileo", "12-28", "Pregonan otro jubileo del rey", certeza = APROXIMADO, fuente = F_KHAEMUASET),
                )),
            TemporadaVida("1246 a. C., año 34 de Ramsés", "paz",
                "El segundo jubileo. Se casa la hija del vecino. Amenemope camina, Tia aprende a hacer cerveza y Paser guía el arado. Ipy se hace un bote de papiro. En otoño llega la noticia: una princesa del país de Hatti viene a casarse con el rey.",
                anioDeTebas(5) + listOf(
                    mundo("segundo_jubileo", "02-10", "El segundo jubileo del rey", 5, APROXIMADO, F_SED, "fiesta"),
                    suceso("boda_huy", "03-02", "Se casa la hija del vecino Huy y toda la calle come junta", escena = "fiesta"),
                    suceso("amenemope_camina", "04-08", "Amenemope camina"),
                    suceso("cerveza_tia", "07-20", "Tia aprende de Henut a hacer cerveza"),
                    suceso("arado_paser", "11-14", "Paser guía el arado detrás del buey"),
                    mundo("princesa", "11-30", "Llega la noticia: una princesa del país de Hatti viene a casarse con el rey", certeza = APROXIMADO, fuente = F_PRINCESA),
                    suceso("bote", "12-15", "Se hace un bote de papiro para pescar en el canal"),
                )),
        ),
        arcos = listOf(
            Arco("rio", TipoArco.OTRO, RIO),
            Arco.cadaAnio("campo", TipoArco.CULTIVO, "01-01" to 0.35, "02-01" to 0.6, "03-15" to 1.0, "03-20" to 1.0, "04-19" to 0.0, "11-05" to 0.0, "12-10" to 0.2),
        ),
        objetos = listOf("azada", "hoz", "cesta", "jarra_cerveza", "pan", "cebolla", "sandalias", "amuleto", "red", "odre", "cuerda_medir", "datiles"),
        estados = listOf(Estado("obra", "leva", "baja_agua")),
    )

    // =====================================================================
    //  Lucio, zapatero. Roma, del 110 al 114 d. C.
    // =====================================================================
    //
    // Su gente (toda inventada): Tertia, su mujer. Marco, su hijo (9 años en la temporada 1), y Lucila, su hija (4). Tito,
    // el hijo que nace en la temporada 4. Félix, hijo de un liberto, que entra de aprendiz en la 1 y abre su propio puesto
    // en la 5. Sexto, su hermano, marinero de las naves del trigo, que deja el mar en la 4. Primigenia, la del puesto de
    // comida de abajo.
    //
    // Lo real: Trajano gobierna. Su foro se construye, se inaugura en el 112 y en el 113 se dedica la columna con la guerra
    // de Dacia en espiral; en octubre del 113 el emperador sale a la guerra contra los partos y en el 114 toma Armenia.
    // Las fiestas de Roma van en su fecha del calendario juliano, que es el que usaban.
    //
    // Claves de escena: fiesta, obra, humo, inundacion, luto, partida. Arco: foro (las obras del foro del emperador: 1 =
    // terminado, con su columna).
    //
    // Lo que el cuadro tendría que mostrar, si algún día lo tiene: el foro a medio hacer según `foro`.

    private val F_COMPITALES = "Las Compitales" to "${W}Compitalia"
    private val F_LUPERCALES = "Las Lupercales" to "${W}Lupercalia"
    private val F_MATRONALES = "Las Matronales" to "${W}Matronalia"
    private val F_QUINQUATRIAS = "Las Quinquatrias" to "${W}Quinquatria"
    private val F_PALILIAS = "Las Palilias" to "${W}Parilia"
    private val F_FLORALES = "Las Florales" to "${W}Floralia"
    private val F_VESTALIAS = "Las Vestalias" to "${W}Vestalia"
    private val F_ANONA = "El trigo para la plebe" to "${W}Cura_annonae"
    private val F_VINALIAS = "Las Vinalias" to "${W}Vinalia"
    private val F_ROMANOS = "Los Juegos Romanos" to "${W}Ludi_Romani"
    private val F_PLEBEYOS = "Los Juegos Plebeyos" to "${W}Ludi_Plebeii"
    private val F_SATURNALES = "Las Saturnales" to "${W}Saturnalia"
    private val F_110 = "El año 110" to "${W}AD_110"
    private val F_FORO = "El foro de Trajano" to "${W}Trajan%27s_Forum"
    private val F_112 = "El año 112" to "${W}AD_112"
    private val F_113 = "El año 113" to "${W}AD_113"
    private val F_COLUMNA = "La columna de Trajano" to "${W}Trajan%27s_Column"
    private val F_PARTOS = "La campaña de Trajano contra los partos" to "${W}Trajan%27s_Parthian_campaign"
    private val F_114 = "El año 114" to "${W}AD_114"

    /** Las fiestas de Roma que vuelven cada año. Son iguales todos los años: el calendario juliano no se corre. */
    private val fiestasDeRoma: List<DiaEspecial> = listOf(
        fiesta("compitales", "01-03", "Las Compitales: cada barrio honra a sus lares en el cruce de calles", certeza = APROXIMADO, fuente = F_COMPITALES),
        fiesta("lupercales", "02-15", "Las Lupercales: los lupercos corren por la ciudad", certeza = HISTORICO, fuente = F_LUPERCALES),
        fiesta("matronales", "03-01", "Las Matronales: los maridos regalan a sus mujeres", certeza = HISTORICO, fuente = F_MATRONALES),
        fiesta("quinquatrias", "03-19", "Las Quinquatrias: cinco días para Minerva", 5, HISTORICO, F_QUINQUATRIAS),
        fiesta("palilias", "04-21", "Las Palilias: el cumpleaños de Roma", certeza = HISTORICO, fuente = F_PALILIAS),
        fiesta("florales", "04-28", "Las Florales: seis días de flores y teatro", 6, HISTORICO, F_FLORALES),
        fiesta("vestalias", "06-07", "Las Vestalias: los burros de los molinos, coronados de flores", 9, HISTORICO, F_VESTALIAS),
        mundo("naves_trigo", "06-20", "Llegan a Ostia las naves del trigo de Alejandría", certeza = APROXIMADO, fuente = F_ANONA),
        fiesta("vinalias", "08-19", "Las Vinalias del campo: se bendicen las viñas antes de la vendimia", certeza = HISTORICO, fuente = F_VINALIAS),
        fiesta("juegos_romanos", "09-05", "Los Juegos Romanos: quince días de carreras y teatro", 15, HISTORICO, F_ROMANOS),
        fiesta("juegos_plebeyos", "11-04", "Los Juegos Plebeyos: dos semanas de juegos", 14, HISTORICO, F_PLEBEYOS),
        fiesta("saturnales", "12-17", "Las Saturnales: siete días al revés", 7, HISTORICO, F_SATURNALES),
    )

    val LUCIO = Calendario(
        vida = "roma",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 11, nubes = 9, lluvia = 8, tormenta = 1, niebla = 2), mes(despejado = 11, nubes = 9, lluvia = 7, tormenta = 1, niebla = 1, viento = 1),
            mes(despejado = 13, nubes = 9, lluvia = 7, tormenta = 1, viento = 1), mes(despejado = 14, nubes = 9, lluvia = 7, tormenta = 1),
            mes(despejado = 18, nubes = 7, lluvia = 5, tormenta = 1), mes(despejado = 23, nubes = 4, lluvia = 2, tormenta = 2),
            mes(despejado = 27, nubes = 2, lluvia = 1, tormenta = 1), mes(despejado = 25, nubes = 3, lluvia = 1, tormenta = 2),
            mes(despejado = 19, nubes = 6, lluvia = 4, tormenta = 2), mes(despejado = 14, nubes = 8, lluvia = 7, tormenta = 2),
            mes(despejado = 10, nubes = 9, lluvia = 9, tormenta = 2, niebla = 1), mes(despejado = 10, nubes = 10, lluvia = 8, tormenta = 1, niebla = 2),
        ),
        temporadas = listOf(
            TemporadaVida("110 d. C.", "obras",
                "Félix entra de aprendiz. Los carros de piedra del foro del emperador pasan de noche frente al taller. Marco va a la escuela. Sexto llega de Alejandría con dátiles. Arde una ínsula a dos calles. A Lucio le encargan unas botas de tribuno.",
                fiestasDeRoma + listOf(
                    suceso("felix", "01-12", "Félix, hijo de un liberto, entra de aprendiz al taller"),
                    mundo("carros_foro", "02-25", "Los carros de piedra del foro nuevo pasan de noche frente al taller", certeza = APROXIMADO, fuente = F_110, escena = "obra"),
                    suceso("escuela", "03-24", "Marco empieza a ir a la escuela del maestro de la esquina"),
                    suceso("sexto_llega", "06-24", "Sexto, su hermano, llega de Alejandría con un saco de dátiles"),
                    mundo("incendio", "07-12", "Arde una ínsula a dos calles: toda la noche pasan cubos de mano en mano", certeza = INVENTADO, escena = "humo"),
                    suceso("botas_tribuno", "10-20", "Le encargan unas botas de piel teñida para un tribuno"),
                    suceso("lucila_pan", "12-05", "Lucila baja sola por el pan a la tahona"),
                )),
            TemporadaVida("111 d. C.", "paz",
                "El Tíber se sale en enero. Félix cose solo su primer par de sandalias. Las naves del trigo se retrasan y sube el pan. Lucila cae con la fiebre del verano y sana. Lucio va por primera vez a las termas nuevas del emperador.",
                fiestasDeRoma + listOf(
                    mundo("tiber", "01-25", "El Tíber se sale y el agua llega a las tiendas del Campo de Marte", 4, INVENTADO, escena = "inundacion", clima = Clima.LLUVIA),
                    suceso("sandalias_felix", "04-05", "Félix cose sin ayuda su primer par de sandalias"),
                    mundo("pan_caro", "05-20", "Las naves del trigo se retrasan y sube el pan", certeza = INVENTADO),
                    suceso("renta", "07-01", "Paga la renta del año al administrador de la ínsula"),
                    suceso("fiebre_lucila", "07-25", "Lucila cae con la fiebre del verano", 7, cadena = "fiebre"),
                    suceso("sana_lucila", "08-02", "A Lucila se le quita la fiebre", cadena = "fiebre"),
                    suceso("termas", "10-08", "Va por primera vez a las termas nuevas del emperador"),
                )),
            TemporadaVida("112 d. C.", "foro",
                "Se inaugura el foro del emperador. Lucio alquila un taller más grande. Marco le lee en voz alta un cartel. Muere Marciana, la hermana del emperador. Tertia espera otro hijo. La nave de Sexto se pierde, pero él se salva.",
                fiestasDeRoma + listOf(
                    mundo("foro_abre", "01-01", "Inauguran el foro del emperador", certeza = APROXIMADO, fuente = F_FORO, escena = "fiesta"),
                    suceso("taller_grande", "04-01", "Alquila un taller más grande, con mostrador a la calle"),
                    suceso("marco_lee", "05-15", "Marco le lee en voz alta un cartel pintado en la pared"),
                    mundo("muere_marciana", "08-29", "Muere Marciana, la hermana del emperador: luto en la ciudad", certeza = APROXIMADO, fuente = F_112, escena = "luto"),
                    suceso("espera_tertia", "10-10", "Tertia espera otro hijo"),
                    suceso("naufragio", "11-25", "Llega la noticia: la nave de Sexto se perdió, pero él se salvó en una barca"),
                )),
            TemporadaVida("113 d. C.", "guerra",
                "Dedican la basílica y, en mayo, la columna del emperador. Nace Tito. Le encargan cien pares de botas militares para la campaña de oriente. Félix se enamora de la hija de Primigenia. El emperador sale de Roma hacia la guerra. Sexto deja el mar y se queda en el taller.",
                fiestasDeRoma + listOf(
                    mundo("basilica", "01-01", "Dedican la basílica Ulpia en el foro nuevo", certeza = APROXIMADO, fuente = F_113, escena = "fiesta"),
                    mundo("columna", "05-12", "Dedican la columna del emperador, con la guerra de Dacia en espiral", certeza = HISTORICO, fuente = F_COLUMNA, escena = "fiesta"),
                    suceso("nace_tito", "06-30", "Nace su hijo Tito"),
                    suceso("caligas", "09-01", "Le encargan cien pares de botas militares para la campaña de oriente"),
                    suceso("felix_novia", "08-08", "Félix se enamora de la hija de Primigenia"),
                    guerra("parte_emperador", "10-27", "El emperador sale de Roma hacia la guerra de oriente", certeza = APROXIMADO, fuente = F_PARTOS, escena = "partida"),
                    suceso("sexto_queda", "12-02", "Sexto deja el mar y se queda a trabajar en el taller"),
                )),
            TemporadaVida("114 d. C.", "guerra",
                "Félix abre su propio puesto. Marco empieza en el taller por las tardes. Tito da sus primeros pasos. Llegan las noticias de la guerra: Armenia y luego Mesopotamia. Lucila teje con su madre su primer cinturón.",
                fiestasDeRoma + listOf(
                    suceso("puesto_felix", "04-12", "Félix abre su propio puesto de remiendos en la esquina"),
                    suceso("marco_taller", "05-25", "Marco empieza a trabajar en el taller por las tardes"),
                    suceso("tito_camina", "07-08", "Tito da sus primeros pasos entre las hormas"),
                    guerra("armenia", "07-15", "Llega la noticia: el emperador tomó Armenia", certeza = APROXIMADO, fuente = F_114, escena = "fiesta"),
                    suceso("cinturon", "10-12", "Lucila teje con su madre su primer cinturón"),
                    guerra("mesopotamia", "12-10", "Llega la noticia: las legiones entran en Mesopotamia", certeza = APROXIMADO, fuente = F_114),
                )),
        ),
        arcos = listOf(
            Arco("foro", TipoArco.OBRA, listOf(p(1, "01-01", 0.6), p(2, "12-31", 0.9), p(3, "01-01", 0.92), p(4, "05-12", 1.0))),
        ),
        objetos = listOf("lezna", "horma", "cuchilla", "cuero", "sandalia", "caliga", "cerote", "lucerna", "jarra_vino", "pan", "moneda", "tablilla"),
    )

    // =====================================================================
    //  Alice, campesina. Una aldea de Inglaterra, de 1300 a 1304.
    // =====================================================================
    //
    // Su gente (toda inventada): Walter, su marido, siervo con media virgata de tierra. Agnes, su hija (11 años en la
    // temporada 1), que entra a servir en la casa del señor en la 2. John, su hijo (7). Matilda, la hija que nace en la 3.
    // Edith, la madre de Walter, que muere en la 2. Joan, su hermana, casada en el pueblo vecino. Hugh, el mayordomo del
    // señor. Don Robert, el cura.
    //
    // Lo real: las fiestas de la Iglesia y del año del campo, los días de pagar la renta, el jubileo del 1300, las
    // guerras de Eduardo I en Escocia, el príncipe de Gales, el robo del tesoro del rey en Westminster, la muerte del papa
    // Bonifacio y la toma de Stirling. Que las noticias lleguen a su aldea en esos días es inventado. La Pascua de cada
    // año está calculada.
    //
    // Claves de escena: fiesta, campanas, procesion, hogueras, cosecha, luto. Arco: campo (el trigo).

    private val F_ARADO = "El lunes del arado" to "${W}Plough_Monday"
    private val F_CANDELARIA = "La Candelaria" to "${W}Candlemas"
    private val F_ANUNCIACION = "La Anunciación, día de renta" to "${W}Lady_Day"
    private val F_HOCK = "Hocktide" to "${W}Hocktide"
    private val F_MAYO = "El primero de mayo" to "${W}May_Day"
    private val F_ALE = "La cerveza de la parroquia" to "${W}Church_ale"
    private val F_SAN_JUAN = "San Juan" to "${W}Midsummer"
    private val F_HENO = "La siega del heno" to "${W}Haymaking"
    private val F_LAMMAS = "Lammas, la misa del pan" to "${W}Lammas"
    private val F_MIGUEL = "San Miguel, día de cuentas" to "${W}Michaelmas"
    private val F_MARTIN = "San Martín" to "${W}Martinmas"
    private val F_NAVIDAD = "Los doce días de Navidad" to "${W}Twelve_Days_of_Christmas"
    private val F_JUBILEO = "El jubileo de 1300" to "${W}Jubilee_(Christianity)"
    private val F_CAERLAVEROCK = "El sitio de Caerlaverock" to "${W}Siege_of_Caerlaverock"
    private val F_1301 = "El año 1301" to "${W}1301"
    private val F_1302 = "El año 1302" to "${W}1302"
    private val F_TESORO = "Richard of Pudlicott y el robo del tesoro del rey" to "${W}Richard_of_Pudlicott"
    private val F_1303 = "El año 1303" to "${W}1303"
    private val F_STIRLING = "El sitio de Stirling" to "${W}Siege_of_Stirling_Castle_(1304)"
    private val F_1304 = "El año 1304" to "${W}1304"

    // Lo que cuelga de la Pascua juliana de 1300, 1301, 1302, 1303 y 1304.
    private val CARNAVAL = listOf("02-23", "02-14", "03-06", "02-19", "02-11")     // el martes antes de la ceniza
    private val PASCUA = listOf("04-10", "04-02", "04-22", "04-07", "03-29")
    private val HOCK = listOf("04-25", "04-17", "05-07", "04-22", "04-13")         // lunes y martes de la segunda semana después de Pascua
    private val ROGATIVAS = listOf("05-16", "05-08", "05-28", "05-13", "05-04")    // los tres días antes de la Ascensión
    private val PENTECOSTES = listOf("05-29", "05-21", "06-10", "05-26", "05-17")

    /** Las fiestas y las faenas de la aldea que vuelven cada año, en la temporada [t]. */
    private fun anioDeLaAldea(t: Int): List<DiaEspecial> = listOf(
        fiesta("arado", "01-07", "Se acaban las fiestas y vuelve el arado", certeza = APROXIMADO, fuente = F_ARADO),
        fiesta("candelaria", "02-02", "La Candelaria: velas benditas en la iglesia", certeza = HISTORICO, fuente = F_CANDELARIA),
        fiesta("carnaval", CARNAVAL[t - 1], "Martes de carnaval: tortas y lo último de la grasa antes de la cuaresma", certeza = HISTORICO),
        rito("anunciacion", "03-25", "La Anunciación: hay que pagar la renta al señor", certeza = HISTORICO, fuente = F_ANUNCIACION),
        fiesta("pascua", PASCUA[t - 1], "Pascua: se acaba la cuaresma", 2, HISTORICO, escena = "campanas"),
        fiesta("hock", HOCK[t - 1], "Hocktide: las mujeres atan a los hombres y cobran el rescate para la iglesia", 2, APROXIMADO, F_HOCK),
        fiesta("mayo", "05-01", "Primero de mayo: ramas verdes en las puertas", certeza = APROXIMADO, fuente = F_MAYO),
        rito("rogativas", ROGATIVAS[t - 1], "Las rogativas: la procesión recorre los linderos de la parroquia", 3, HISTORICO, escena = "procesion"),
        fiesta("cerveza_pentecostes", PENTECOSTES[t - 1], "Pentecostés: la cerveza de la parroquia", 2, APROXIMADO, F_ALE),
        fiesta("san_juan", "06-24", "La noche de San Juan: hogueras", certeza = HISTORICO, fuente = F_SAN_JUAN, escena = "hogueras"),
        cosecha("heno", "06-28", "La siega del heno en el prado del señor", 14, APROXIMADO, F_HENO),
        fiesta("lammas", "08-01", "Lammas: el primer pan del trigo nuevo va a la iglesia", certeza = HISTORICO, fuente = F_LAMMAS),
        cosecha("siega", "08-06", "La siega del trigo", 30, APROXIMADO),
        rito("san_miguel", "09-29", "San Miguel: el mayordomo hace las cuentas del año", certeza = HISTORICO, fuente = F_MIGUEL),
        fiesta("san_martin", "11-11", "San Martín: matan el cerdo para el invierno", certeza = HISTORICO, fuente = F_MARTIN),
        fiesta("navidad", "12-25", "Los doce días de Navidad", 12, HISTORICO, F_NAVIDAD),
    )

    val ALICE = Calendario(
        vida = "medieval",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 4, nubes = 10, lluvia = 8, nieve = 3, niebla = 4, viento = 2), mes(despejado = 5, nubes = 10, lluvia = 7, nieve = 3, niebla = 3, viento = 3),
            mes(despejado = 7, nubes = 10, lluvia = 8, nieve = 1, niebla = 2, viento = 4), mes(despejado = 9, nubes = 10, lluvia = 8, viento = 3),
            mes(despejado = 11, nubes = 10, lluvia = 7, tormenta = 1, viento = 2), mes(despejado = 13, nubes = 9, lluvia = 6, tormenta = 2),
            mes(despejado = 14, nubes = 8, lluvia = 6, tormenta = 3), mes(despejado = 13, nubes = 9, lluvia = 6, tormenta = 2),
            mes(despejado = 11, nubes = 9, lluvia = 7, niebla = 2, viento = 1), mes(despejado = 7, nubes = 10, lluvia = 9, niebla = 3, viento = 2),
            mes(despejado = 5, nubes = 10, lluvia = 9, niebla = 4, viento = 3), mes(despejado = 4, nubes = 10, lluvia = 8, nieve = 2, niebla = 4, viento = 3),
        ),
        temporadas = listOf(
            TemporadaVida("1300", "paz",
                "Su hermana Joan se casa en el pueblo vecino. Pare la vaca. Llega la noticia del jubileo de Roma. El rey marcha a Escocia con sus barones. Alice vende su cerveza y el catador del señor la aprueba. Agnes hila sola un huso entero.",
                anioDeLaAldea(1) + listOf(
                    suceso("boda_joan", "01-20", "Su hermana Joan se casa en el pueblo vecino", escena = "fiesta"),
                    suceso("ternero", "03-10", "Pare la vaca: un ternero rojo"),
                    mundo("jubileo", "04-20", "Llega la noticia: el papa promete el perdón a quien vaya a Roma este año", certeza = APROXIMADO, fuente = F_JUBILEO),
                    guerra("caerlaverock", "07-10", "Llega la noticia: el rey marcha a Escocia con sus barones", certeza = APROXIMADO, fuente = F_CAERLAVEROCK),
                    suceso("catador", "09-12", "Alice cuece cerveza para vender, y el catador del señor la aprueba"),
                    suceso("huso_agnes", "10-20", "Agnes hila sola un huso entero"),
                )),
            TemporadaVida("1301", "guerra",
                "El hijo del rey es ahora príncipe de Gales. Los oficiales del rey se llevan grano para la guerra de Escocia y llueve sobre la siega. Agnes aprende de su abuela a hacer queso y en otoño entra a servir en la casa del señor. Muere Edith, la madre de Walter.",
                anioDeLaAldea(2) + listOf(
                    mundo("principe_gales", "03-02", "Llega la noticia: el hijo del rey es ahora príncipe de Gales", certeza = APROXIMADO, fuente = F_1301),
                    suceso("queso", "06-14", "Agnes aprende de su abuela Edith a hacer queso"),
                    guerra("requisa", "07-20", "Los oficiales del rey se llevan grano para el ejército de Escocia", certeza = APROXIMADO, fuente = F_1301),
                    mundo("siega_mojada", "08-18", "Llueve sobre la siega: el trigo se moja en las gavillas", 5, INVENTADO, clima = Clima.LLUVIA),
                    suceso("agnes_sirve", "10-05", "Agnes entra a servir en la casa del señor"),
                    suceso("john_ocas", "11-03", "John cuida solo las ocas en el campo comunal"),
                    suceso("muere_edith", "11-28", "Muere Edith, la madre de Walter", escena = "luto"),
                )),
            TemporadaVida("1302", "tregua",
                "Tregua con los escoceses. Nace Matilda y la bautizan al día siguiente. Walter toma la media virgata de una viuda. Una peste de las vacas en el pueblo vecino cierra el paso del ganado.",
                anioDeLaAldea(3) + listOf(
                    mundo("tregua", "02-20", "Llega la noticia: tregua con los escoceses", certeza = APROXIMADO, fuente = F_1302),
                    suceso("nace_matilda", "04-03", "Nace su hija Matilda", cadena = "matilda"),
                    suceso("bautizo", "04-04", "Bautizan a Matilda en la pila de la iglesia", cadena = "matilda", escena = "campanas"),
                    suceso("media_virgata", "09-30", "Walter toma media virgata más, la de una viuda"),
                    mundo("peste_vacas", "10-25", "Una peste de las vacas en el pueblo vecino: se cierra el paso del ganado", certeza = INVENTADO),
                    suceso("joan_visita", "12-10", "Su hermana Joan viene del pueblo vecino a conocer a Matilda"),
                )),
            TemporadaVida("1303", "paz",
                "John guía los bueyes del arado. Matilda camina. Roban el tesoro del rey en Westminster. Las vacas se meten al trigo del señor y multan a Walter. Rehacen el techo de paja. En noviembre llega la noticia de que el papa murió preso.",
                anioDeLaAldea(4) + listOf(
                    suceso("john_bueyes", "01-14", "John guía por primera vez los bueyes del arado"),
                    suceso("matilda_camina", "05-05", "Matilda da sus primeros pasos"),
                    mundo("tesoro", "06-15", "Llega la noticia: robaron el tesoro del rey en la abadía de Westminster", certeza = APROXIMADO, fuente = F_TESORO),
                    suceso("multa", "07-14", "Las vacas se meten al trigo del señor y a Walter lo multan en la corte"),
                    suceso("techo", "09-10", "Rehacen el techo de paja antes del invierno"),
                    mundo("muere_papa", "11-20", "Llega la noticia: murió el papa, preso", certeza = APROXIMADO, fuente = F_1303, escena = "campanas"),
                )),
            TemporadaVida("1304", "guerra",
                "Alice vende huevos en el mercado del pueblo vecino. Agnes vuelve de la casa del señor con un pretendiente. El rey toma Stirling y llama al parlamento: habrá impuesto. John espanta a los cuervos con la honda.",
                anioDeLaAldea(5) + listOf(
                    suceso("huevos", "03-14", "Vende huevos en el mercado del pueblo vecino"),
                    suceso("pretendiente", "06-05", "Agnes vuelve de la casa del señor con un pretendiente"),
                    guerra("stirling", "08-25", "Llega la noticia: el rey tomó el castillo de Stirling", certeza = APROXIMADO, fuente = F_STIRLING),
                    suceso("cuervos", "10-15", "John espanta a los cuervos de la siembra con la honda"),
                    mundo("parlamento", "11-20", "Llega la noticia: el rey llama al parlamento, y habrá impuesto", certeza = APROXIMADO, fuente = F_1304),
                    suceso("capucha", "12-12", "Le teje a Matilda su primera capucha de lana"),
                )),
        ),
        arcos = listOf(
            Arco.cadaAnio("campo", TipoArco.CULTIVO, "03-01" to 0.2, "05-15" to 0.6, "07-25" to 1.0, "08-06" to 1.0, "09-05" to 0.0, "10-15" to 0.0, "12-01" to 0.1),
        ),
        objetos = listOf("huso", "rueca", "cubo", "jarra_cerveza", "pan", "hoz", "azada", "cesta", "vela_sebo", "cuentas", "cuchara", "capucha"),
    )

    // =====================================================================
    //  Citlali, macehual que cultiva una chinampa. México-Tenochtitlan, de 1500 a 1504.
    // =====================================================================
    //
    // Su gente (toda inventada): Yaotl, su marido, que trabaja la chinampa con ella y cumple su turno en las obras de la
    // ciudad. Xocoyotl, su hijo (4 años en la temporada 1). Xóchitl, la hija que nace en la temporada 2. Papan, su suegra,
    // que borda. Mazatl, su hermano, guerrero del barrio, que trae su primer cautivo en la 4.
    //
    // Lo real: la gran inundación de la ciudad, que se rehizo más grande bajo Ahuízotl; la muerte de Ahuízotl en el año 10
    // Conejo (1502), la elección y la coronación de Moctezuma, su primera campaña (Nopallan, en Oaxaca) y las guerras
    // contra Huexotzinco. Las veintenas van con las fechas de Sahagún (el año empezaba el 2 de febrero), que son
    // «aproximado»: hay otras dos tradiciones.
    //
    // Claves de escena: fiesta, inundacion, obra, luto, partida, seco. Arcos: chinampa (los daños de la inundación y cómo
    // la rehacen: 1 = ahogada), milpa (el maíz) y lago (sube en las lluvias).

    private val F_VEINTENAS = "El calendario mexica y sus dieciocho veintenas" to "https://es.wikipedia.org/wiki/Calendario_mexica"
    private val F_TLACAXIPEHUALIZTLI = "Tlacaxipehualiztli" to "${W}Tlacaxipehualiztli"
    private val F_TOXCATL = "Tóxcatl" to "${W}Toxcatl"
    private val F_ETZALCUALIZTLI = "Etzalcualiztli" to "${W}Etzalcualiztli"
    private val F_HUEY_TECUILHUITL = "Huey Tecuílhuitl" to "${W}Huey_Tecuilhuitl"
    private val F_OCHPANIZTLI = "Ochpaniztli" to "${W}Ochpaniztli"
    private val F_PANQUETZALIZTLI = "Panquetzaliztli" to "${W}Panquetzaliztli"
    private val F_CHINAMPA = "La chinampa" to "${W}Chinampa"
    private val F_TENOCHTITLAN = "Tenochtitlan" to "${W}Tenochtitlan"
    private val F_AHUIZOTL = "Ahuízotl" to "${W}Ahuitzotl"
    private val F_MOCTEZUMA = "Moctezuma II" to "${W}Moctezuma_II"

    /** Las veintenas y las faenas de la chinampa que vuelven cada año. Las fechas son las de Sahagún, en juliano. */
    private val anioDeLaChinampa: List<DiaEspecial> = listOf(
        rito("nemontemi", "01-28", "Los nemontemi: cinco días «en vano» antes del año nuevo", 5, APROXIMADO, F_VEINTENAS),
        fiesta("tlacaxipehualiztli", "03-13", "Termina Tlacaxipehualiztli, la fiesta de Xipe Tótec", certeza = APROXIMADO, fuente = F_TLACAXIPEHUALIZTLI),
        cosecha("siembra", "04-10", "Siembran el maíz en la chinampa, con las plantitas del almácigo", 10, APROXIMADO, F_CHINAMPA),
        fiesta("toxcatl", "05-12", "Termina Tóxcatl, la fiesta de Tezcatlipoca", certeza = APROXIMADO, fuente = F_TOXCATL),
        fiesta("etzalcualiztli", "06-01", "Etzalcualiztli: todos comen etzalli, maíz con frijol, por Tláloc", certeza = APROXIMADO, fuente = F_ETZALCUALIZTLI),
        fiesta("huey_tecuilhuitl", "07-11", "Huey Tecuílhuitl, la gran fiesta de los señores", certeza = APROXIMADO, fuente = F_HUEY_TECUILHUITL),
        fiesta("xocotl_huetzi", "08-20", "Xócotl Huetzi, la gran fiesta de los muertos: cae el tronco", certeza = APROXIMADO, fuente = F_VEINTENAS),
        fiesta("ochpaniztli", "09-09", "Ochpaniztli: se barren los caminos y las casas", certeza = APROXIMADO, fuente = F_OCHPANIZTLI),
        cosecha("cosecha", "09-25", "La cosecha del maíz en la chinampa", 15, APROXIMADO, F_CHINAMPA),
        fiesta("panquetzaliztli", "11-28", "Panquetzaliztli: se levantan banderas para Huitzilopochtli", certeza = APROXIMADO, fuente = F_PANQUETZALIZTLI),
        fiesta("atemoztli", "12-18", "Atemoztli, el descenso del agua", certeza = APROXIMADO, fuente = F_VEINTENAS),
    )

    val CITLALI = Calendario(
        vida = "tenochtitlan",
        estaciones = listOf("05-15" to Estacion.LLUVIAS, "11-01" to Estacion.SECAS),
        clima = listOf(
            mes(despejado = 22, nubes = 4, viento = 2, niebla = 1), mes(despejado = 21, nubes = 4, viento = 3, polvo = 1),
            mes(despejado = 19, nubes = 5, lluvia = 1, viento = 4, polvo = 2), mes(despejado = 16, nubes = 7, lluvia = 3, tormenta = 1, viento = 3, polvo = 1),
            mes(despejado = 12, nubes = 8, lluvia = 6, tormenta = 3, viento = 1), mes(despejado = 6, nubes = 9, lluvia = 10, tormenta = 5),
            mes(despejado = 5, nubes = 9, lluvia = 12, tormenta = 5), mes(despejado = 5, nubes = 9, lluvia = 12, tormenta = 5),
            mes(despejado = 6, nubes = 9, lluvia = 11, tormenta = 4), mes(despejado = 12, nubes = 8, lluvia = 6, tormenta = 2, niebla = 1),
            mes(despejado = 19, nubes = 6, lluvia = 2, niebla = 2), mes(despejado = 22, nubes = 5, lluvia = 1, viento = 1, niebla = 1),
        ),
        temporadas = listOf(
            TemporadaVida("1500, año 8 Pedernal", "inundacion",
                "Plantan un ahuejote nuevo. Xocoyotl rema por primera vez. Citlali vende su primera carga de chiles en Tlatelolco. A fines de agosto el agua inunda la ciudad y ahoga la chinampa. La rehacen con lodo y estacas, y en diciembre siembran el primer almácigo.",
                anioDeLaChinampa + listOf(
                    suceso("ahuejote", "02-14", "Plantan un ahuejote nuevo en la esquina de la chinampa"),
                    suceso("xoco_rema", "03-20", "Xocoyotl rema por primera vez la canoa"),
                    suceso("chiles", "05-25", "Vende en Tlatelolco su primera carga de chiles"),
                    mundo("inundacion", "08-28", "El agua se sale y cubre la ciudad: la chinampa se ahoga", 10, APROXIMADO, F_TENOCHTITLAN, "inundacion", Clima.LLUVIA),
                    suceso("rehacen", "10-20", "Empiezan a rehacer la chinampa con lodo y estacas", 15, cadena = "rehacer", escena = "obra"),
                    suceso("almacigo", "12-02", "Siembran el primer almácigo en la chinampa nueva", cadena = "rehacer"),
                )),
            TemporadaVida("1501, año 9 Casa", "reconstruccion",
                "Levantan de nuevo los templos y las casas que tumbó el agua. Nace Xóchitl y le ponen nombre en un día de buen signo. Mazatl sale por primera vez con los guerreros del barrio. La primera cosecha de la chinampa nueva. Papan le borda a Xóchitl su primer huipil.",
                anioDeLaChinampa + listOf(
                    mundo("ciudad_nueva", "02-15", "Levantan de nuevo los templos y las casas que tumbó el agua", 30, APROXIMADO, F_TENOCHTITLAN, "obra"),
                    suceso("nace_xochitl", "06-20", "Nace su hija", cadena = "xochitl"),
                    suceso("nombre_xochitl", "06-24", "Le ponen nombre en un día de buen signo: Xóchitl, «flor»", cadena = "xochitl", escena = "fiesta"),
                    suceso("mazatl_sale", "08-02", "Su hermano Mazatl sale por primera vez con los guerreros del barrio", escena = "partida"),
                    suceso("primera_cosecha", "10-02", "La primera cosecha de la chinampa nueva"),
                    suceso("huipil", "12-05", "Papan le borda a Xóchitl su primer huipil"),
                )),
            TemporadaVida("1502, año 10 Conejo", "luto",
                "Yaotl cambia la canoa vieja por una nueva. Muere el tlatoani Ahuízotl y eligen a Moctezuma, su sobrino. Xóchitl da sus primeros pasos. Xocoyotl atrapa sus primeros acociles. Papan ya no ve bien y deja de bordar.",
                anioDeLaChinampa + listOf(
                    suceso("canoa_nueva", "01-10", "Yaotl cambia la canoa vieja por una nueva"),
                    mundo("muere_ahuizotl", "03-28", "Muere el tlatoani Ahuízotl", certeza = APROXIMADO, fuente = F_AHUIZOTL, escena = "luto"),
                    mundo("eligen_moctezuma", "04-25", "Eligen tlatoani a Moctezuma, sobrino de Ahuízotl", certeza = APROXIMADO, fuente = F_MOCTEZUMA),
                    suceso("xochitl_camina", "06-28", "Xóchitl da sus primeros pasos en la chinampa"),
                    suceso("acociles", "08-05", "Xocoyotl atrapa sus primeros acociles con la red"),
                    suceso("papan_ojos", "10-28", "Papan, su suegra, ya no ve bien y deja de bordar"),
                )),
            TemporadaVida("1503, año 11 Caña", "guerra",
                "Siembran flores para vender. Vuelven los guerreros de Nopallan con cautivos y Mazatl trae el primero suyo. Coronan a Moctezuma. Citlali vende sus flores en Tlatelolco. Xocoyotl aprende su primer canto.",
                anioDeLaChinampa + listOf(
                    suceso("flores", "03-28", "Siembran flores en la orilla de la chinampa, para vender"),
                    guerra("nopallan", "06-15", "Vuelven los guerreros de Nopallan con cautivos para la coronación", certeza = APROXIMADO, fuente = F_MOCTEZUMA, escena = "fiesta"),
                    suceso("cautivo_mazatl", "06-16", "Mazatl trae su primer cautivo, y le dan su manta de guerrero"),
                    mundo("coronacion", "07-05", "Coronan a Moctezuma: fiestas en toda la ciudad", 4, APROXIMADO, F_MOCTEZUMA, "fiesta"),
                    suceso("vende_flores", "08-28", "Vende en Tlatelolco las flores de la chinampa"),
                    suceso("canto_xoco", "10-15", "Xocoyotl aprende su primer canto"),
                )),
            TemporadaVida("1504, año 12 Pedernal", "guerra",
                "Xocoyotl ayuda a sacar lodo del canal. Mazatl se casa. Xóchitl ayuda a palmear las tortillas. Las lluvias no llegan a tiempo y hay que regar la chinampa a mano. Yaotl cumple su turno en la calzada. Los guerreros salen contra Huexotzinco.",
                anioDeLaChinampa + listOf(
                    suceso("lodo_canal", "01-12", "Xocoyotl ayuda a sacar lodo del canal para la chinampa"),
                    suceso("boda_mazatl", "02-20", "Se casa su hermano Mazatl", escena = "fiesta"),
                    suceso("tortillas", "04-22", "Xóchitl le ayuda a palmear las tortillas"),
                    mundo("lluvias_tarde", "06-10", "No llegan las lluvias: el lago baja y la chinampa pide agua a cubetadas", 20, INVENTADO, escena = "seco"),
                    suceso("calzada", "08-12", "Yaotl cumple su turno de trabajo en la calzada"),
                    guerra("huexotzinco", "11-10", "Los guerreros salen contra Huexotzinco", certeza = APROXIMADO, fuente = F_MOCTEZUMA, escena = "partida"),
                )),
        ),
        arcos = listOf(
            Arco("chinampa", TipoArco.DANOS, listOf(p(1, "01-01", 0.0), p(1, "08-27", 0.0), p(1, "08-28", 1.0, true), p(1, "10-20", 1.0), p(1, "11-05", 0.6), p(1, "12-02", 0.3),
                p(2, "03-01", 0.0))),
            Arco.cadaAnio("milpa", TipoArco.CULTIVO, "04-09" to 0.0, "04-20" to 0.05, "06-15" to 0.4, "08-15" to 0.9, "09-25" to 1.0, "10-10" to 0.0),
            Arco.cadaAnio("lago", TipoArco.OTRO, "01-15" to 0.45, "05-15" to 0.25, "10-01" to 0.9),
        ),
        objetos = listOf("metate", "comal", "coa", "canoa", "remo", "huipil", "cesta", "flores", "jarro", "malacate", "petate", "tortilla"),
    )
}
