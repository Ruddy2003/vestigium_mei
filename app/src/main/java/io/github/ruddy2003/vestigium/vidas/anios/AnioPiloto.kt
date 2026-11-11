package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Arco
import io.github.ruddy2003.vestigium.vidas.Calendario
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.DiaEspecial
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.RegimenLuz
import io.github.ruddy2003.vestigium.vidas.TemporadaVida
import io.github.ruddy2003.vestigium.vidas.TipoArco

/*
 * Los calendarios del piloto: las cinco temporadas (2027 a 2031) de Demeas, Pierre, Kei y Amaru. Son el patrón que
 * copian las tandas A. Qué es real y qué inventado, con cada fuente, está en `docs/historia/fuentes.md`.
 *
 * Cómo se escribe un calendario:
 *
 * 1. Unos 20 días especiales por temporada, y no todos son fiestas: las fiestas que vuelven cada año, una o dos
 *    cosechas, lo que pasa en su mundo y cuatro o cinco sucesos de su vida.
 * 2. Una fiesta que vuelve lleva el mismo `id` en todas las temporadas (comparte su texto) aunque cambie de fecha.
 *    Lo propio de un año (la obra que se estrenó en esa fiesta) va en otro día de un solo día, encima.
 * 3. Certeza: «histórico» solo con la fuente abierta en la sesión (o con una fecha calculada, ver [CALCULADOS]);
 *    «aproximado» si pasó pero la fecha se eligió; «inventado» para lo demás. Los sucesos de su vida siempre son
 *    inventados. En un personaje de ficción todo es inventado.
 * 4. Las fechas son las del calendario de su época (el juliano para Demeas y Pierre).
 * 5. La historia sigue de un año al otro: lo que se rompe sigue roto, quien nace crece, y el `resumen` de cada
 *    temporada lo cuenta en una línea para quien escriba los textos.
 * 6. En la temporada 5 los sucesos de su vida no dependen de un día fijo, porque desde 2032 se mueven hasta 45 días.
 * 7. Lo que dura meses (una guerra, una peste, una obra) va en `mundo` y en un arco, no en un día de 200 días.
 * 8. Las claves de `escena` son pocas y se repiten (fiesta, luto, humo…): cada escena decide qué dibuja con ellas.
 * 9. Si el personaje está de viaje, la fiesta de siempre que cae en el viaje lleva otro `id` (y otro texto): la vive
 *    de lejos. Así el texto compartido de esa fiesta no lo pone en dos sitios a la vez.
 */
internal object AnioPiloto {
    val TODOS: List<Calendario> by lazy { listOf(DEMEAS, PIERRE, KEI, AMARU) }

    /**
     * Días «históricos» sin enlace porque su fecha sale de un cálculo hecho en la sesión y no de una página: la Pascua
     * juliana de cada año (y lo que cuelga de ella) y el eclipse de 1241. El cálculo está en `docs/historia/fuentes.md`.
     */
    val CALCULADOS = setOf("carnaval", "pascua", "rogativas", "eclipse_mediodia")

    // =====================================================================
    //  Demeas, remero de un trirreme. Atenas, del 431 al 427 a. C.
    // =====================================================================
    //
    // Su gente (toda inventada): Melita, su mujer, que teje. Cares, su hijo (6 años en la temporada 1). Estratón, su
    // padre, que remó en Salamina; muere en la peste (temporada 2). Clío, su hermana, que se casa con Sosias, su
    // compañero de banco en el trirreme «Aura». Eufronio, su tío, carbonero de Acarnas, que se refugia en la ciudad
    // cuando entran los peloponesios. Glicera, la niña de unos vecinos muertos en la peste, a la que recogen
    // (temporada 2). Irene, su hija, que nace en la temporada 3.
    //
    // Lo real: la guerra, las invasiones de cada verano, la peste, los viajes de la flota, las fiestas y las obras de
    // teatro. Que Demeas vaya en esas naves es inventado. Las fiestas áticas seguían la luna: su fecha de cada año se
    // calculó con las lunas nuevas de 431 a 427 a. C. [Aproximación]
    //
    // Claves de escena: fiesta, antorchas, luto, humo (los campos que arden), piras (la peste), eclipse, partida y
    // regreso (la flota). Arcos: campo (el grano), estragos (el campo arrasado que se ve desde los muros), refugiados
    // (las chozas junto a los Muros Largos), peste, flota (cuántas naves hay en el puerto; 1 = todas) e higuera (la
    // que planta en el patio por su padre).

    private val W = "https://en.wikipedia.org/wiki/"
    private val F_GUERRA = "La guerra del Peloponeso" to "${W}Peloponnesian_War"
    private val F_PESTE = "La peste de Atenas" to "${W}Plague_of_Athens"
    private val F_PERICLES = "Pericles" to "${W}Pericles"
    private val F_ORACION = "El discurso fúnebre de Pericles" to "${W}Pericles%27_Funeral_Oration"
    private val F_MITILENE = "La rebelión de Mitilene" to "${W}Mytilenean_revolt"
    private val F_DEBATE = "El debate sobre Mitilene" to "${W}Mytilenean_Debate"
    private val F_PLATEA = "El sitio de Platea" to "${W}Siege_of_Plataea"
    private val F_POTIDEA = "La batalla y el sitio de Potidea" to "${W}Battle_of_Potidaea"
    private val F_RIO = "La batalla de Río" to "${W}Battle_of_Rhium"
    private val F_CNEMO = "Cnemo" to "${W}Cnemus"
    private val F_LAQUES = "Laques" to "${W}Laches_(general)"
    private val F_ECLIPSES = "Eclipses de sol del siglo V a. C." to "${W}List_of_solar_eclipses_in_the_5th_century_BC"
    private val F_LENEAS = "Las Leneas" to "${W}Lenaia"
    private val F_ANTESTERIAS = "Las Antesterias" to "${W}Anthesteria"
    private val F_DIONISIAS = "Las Dionisias" to "${W}Dionysia"
    private val F_MEDEA = "«Medea», de Eurípides" to "${W}Medea_(play)"
    private val F_HIPOLITO = "«Hipólito», de Eurípides" to "${W}Hippolytus_(play)"
    private val F_MUNIQUIA = "Muniquia" to "${W}Munichia"
    private val F_TARGELIAS = "Las Targelias" to "${W}Thargelia"
    private val F_BENDIDIAS = "Las Bendidias" to "${W}Bendidia"
    private val F_PANATENEAS = "Las Panateneas" to "${W}Panathenaea"
    private val F_MISTERIOS = "Los misterios de Eleusis" to "${W}Eleusinian_Mysteries"
    private val F_TESMOFORIAS = "Las Tesmoforias" to "${W}Thesmophoria"
    private val F_APATURIAS = "Las Apaturias" to "${W}Apaturia"

    // La fecha juliana de cada fiesta ática en las cinco temporadas (431, 430, 429, 428 y 427 a. C.).
    private val LENEAS = listOf("01-20", "02-08", "01-29", "01-17", "02-05")            // 12 de gamelión
    private val ANTESTERIAS = listOf("02-18", "03-08", "02-26", "02-15", "03-06")       // 11 a 13 de antesterión
    private val DIONISIAS = listOf("03-18", "04-06", "03-25", "03-15", "04-03")         // 10 a 14 de elafebolión
    private val MUNIQUIAS = listOf("04-23", "05-11", "04-30", "04-19", "05-08")         // 16 de muniquión
    private val TARGELIAS = listOf("05-12", "05-31", "05-19", "05-09", "05-28")         // 6 y 7 de targelión
    private val BENDIDIAS = listOf("", "", "06-01", "05-22", "06-10")                   // 19 de targelión, desde el 429
    private val PANATENEAS = listOf("08-02", "08-20", "08-08", "07-29", "08-16")        // 28 de hecatombeón
    private val MISTERIOS = listOf("09-21", "10-10", "09-28", "09-17", "10-06")         // 19 de boedromión
    private val TESMOFORIAS = listOf("10-12", "10-31", "10-19", "10-09", "10-27")       // 11 a 13 de pianepsión
    private val APATURIAS = listOf("10-20", "11-08", "10-27", "10-17", "11-04")         // 19 a 21 de pianepsión
    private val DIONISIAS_PIREO = listOf("12-07", "12-26", "12-15", "12-04", "12-23")   // 8 de posideón

    /** Las fiestas de Atenas que vuelven cada año, en la temporada [t] (de 1 a 5). */
    private fun fiestasAticas(t: Int): List<DiaEspecial> = listOfNotNull(
        fiesta("leneas", LENEAS[t - 1], "Las Leneas: comedias en lo más frío del invierno", certeza = APROXIMADO, fuente = F_LENEAS),
        fiesta("antesterias", ANTESTERIAS[t - 1], "Las Antesterias: se abren las tinajas del vino nuevo", 3, APROXIMADO, F_ANTESTERIAS),
        fiesta("dionisias", DIONISIAS[t - 1], "Las Grandes Dionisias: cinco días de teatro", 5, APROXIMADO, F_DIONISIAS),
        fiesta("muniquias", MUNIQUIAS[t - 1], "Las Muniquias: la fiesta de Ártemis en la colina del puerto", certeza = APROXIMADO, fuente = F_MUNIQUIA),
        fiesta("targelias", TARGELIAS[t - 1], "Las Targelias: las primicias para Apolo", 2, APROXIMADO, F_TARGELIAS),
        if (t >= 3) fiesta("bendidias", BENDIDIAS[t - 1], "Las Bendidias: carrera de antorchas en el Pireo", certeza = APROXIMADO, fuente = F_BENDIDIAS, escena = "antorchas") else null,
        if (t == 2) fiesta("grandes_panateneas", "08-15", "Las Grandes Panateneas, en el año de la peste", 6, APROXIMADO, F_PANATENEAS)
        else if (t == 1) fiesta("panateneas_en_el_mar", PANATENEAS[0], "Hoy son las Panateneas, y él está en el mar", certeza = APROXIMADO, fuente = F_PANATENEAS, escena = "")
        else fiesta("panateneas", PANATENEAS[t - 1], "Las Panateneas: la procesión sube a la Acrópolis", certeza = APROXIMADO, fuente = F_PANATENEAS),
        fiesta("misterios", MISTERIOS[t - 1], "La procesión de los Misterios sale hacia Eleusis", certeza = APROXIMADO, fuente = F_MISTERIOS),
        fiesta("tesmoforias", TESMOFORIAS[t - 1], "Las Tesmoforias: tres días que son solo de las mujeres", 3, APROXIMADO, F_TESMOFORIAS),
        fiesta("apaturias", APATURIAS[t - 1], "Las Apaturias: las fratrías cenan juntas", 3, APROXIMADO, F_APATURIAS),
        fiesta("dionisias_pireo", DIONISIAS_PIREO[t - 1], "Las Dionisias del Pireo: procesión y vino en el puerto", certeza = APROXIMADO, fuente = F_DIONISIAS),
    )

    private const val VENDIMIA_ATICA = "La vendimia"
    private const val SIEGA_ATICA = "La siega de la cebada y el trigo"

    val DEMEAS = Calendario(
        vida = "remero",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 10, nubes = 9, lluvia = 8, tormenta = 1, viento = 2), mes(despejado = 10, nubes = 9, lluvia = 7, tormenta = 1, viento = 3),
            mes(despejado = 13, nubes = 9, lluvia = 6, viento = 2), mes(despejado = 16, nubes = 8, lluvia = 4, viento = 2),
            mes(despejado = 20, nubes = 6, lluvia = 2, viento = 2), mes(despejado = 25, nubes = 3, lluvia = 1, viento = 1),
            mes(despejado = 25, nubes = 1, viento = 4), mes(despejado = 25, nubes = 1, viento = 5),
            mes(despejado = 22, nubes = 4, lluvia = 2, viento = 2), mes(despejado = 15, nubes = 8, lluvia = 5, tormenta = 1, viento = 1),
            mes(despejado = 11, nubes = 9, lluvia = 8, tormenta = 1, viento = 1), mes(despejado = 9, nubes = 9, lluvia = 9, tormenta = 1, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("431 a. C.", "guerra",
                "Empieza la guerra. Su hermana se casa con Sosias. Los peloponesios arrasan Acarnas y su tío Eufronio se refugia con ellos. Rema dos meses y medio alrededor del Peloponeso (del 5 de julio al 18 de septiembre) y ve el eclipse en el mar.",
                fiestasAticas(1) + listOf(
                    suceso("boda_clio", "01-27", "Su hermana Clío se casa con Sosias", escena = "fiesta"),
                    fiesta("medea", "03-20", "Eurípides estrena «Medea» y queda en tercer lugar", certeza = APROXIMADO, fuente = F_MEDEA),
                    guerra("platea", "04-04", "Los tebanos entran de noche en Platea: empieza la guerra", certeza = APROXIMADO, fuente = F_GUERRA),
                    suceso("botadura", "04-12", "Echan al agua el «Aura» después del invierno"),
                    cosecha("siega", "05-15", SIEGA_ATICA, 25, APROXIMADO),
                    suceso("llega_eufronio", "06-12", "Su tío Eufronio llega de Acarnas con todo en una carreta"),
                    guerra("primera_invasion", "06-23", "Los peloponesios entran al Ática y arrasan los campos", 28, APROXIMADO, F_GUERRA, "humo"),
                    guerra("zarpa_flota", "07-05", "Cien naves zarpan a rodear el Peloponeso", certeza = APROXIMADO, fuente = F_GUERRA, escena = "partida"),
                    mundo("eclipse", "08-03", "El sol se vuelve una media luna en pleno día", certeza = HISTORICO, fuente = F_ECLIPSES, escena = "eclipse", clima = Clima.DESPEJADO),
                    guerra("vuelve_flota", "09-18", "La flota vuelve al Pireo", certeza = APROXIMADO, escena = "regreso"),
                    cosecha("vendimia", "09-22", VENDIMIA_ATICA, 10, APROXIMADO),
                    rito("funeral", "11-22", "Entierran a los primeros muertos de la guerra y habla Pericles", certeza = APROXIMADO, fuente = F_ORACION, escena = "luto"),
                    suceso("cojin", "12-18", "Su padre le regala el cojín con el que remó en Salamina"),
                )),
            TemporadaVida("430 a. C.", "peste",
                "La invasión más larga y, con ella, la peste. Rema con Pericles contra Epidauro; al volver encuentra muerto a su padre y cae enfermo él. Se levanta. Recogen a Glicera. Planta una higuera.",
                fiestasAticas(2) + listOf(
                    suceso("cares_sube", "02-16", "Cares sube por primera vez al «Aura»"),
                    guerra("segunda_invasion", "05-05", "Los peloponesios entran otra vez, y se quedan cuarenta días", 40, APROXIMADO, F_GUERRA, "humo"),
                    mundo("peste_llega", "05-14", "La peste aparece en el Pireo", certeza = APROXIMADO, fuente = F_PESTE, escena = "piras"),
                    guerra("zarpa_epidauro", "06-06", "Pericles zarpa con cien naves contra Epidauro", certeza = APROXIMADO, fuente = F_PERICLES, escena = "partida"),
                    suceso("muere_estraton", "06-22", "Muere Estratón, su padre, mientras él está en el mar", escena = "luto"),
                    guerra("vuelve_epidauro", "07-10", "La flota vuelve con la peste a bordo", certeza = APROXIMADO, escena = "regreso"),
                    suceso("enferma", "07-14", "Cae enfermo", 9, cadena = "fiebre"),
                    suceso("se_levanta", "08-06", "Se levanta de la cama", cadena = "fiebre"),
                    mundo("multan_pericles", "09-12", "La asamblea multa a Pericles y le quita el mando", certeza = APROXIMADO, fuente = F_PERICLES),
                    suceso("vuelve_al_remo", "09-20", "Vuelve al banco de remo", cadena = "fiebre"),
                    suceso("glicera", "10-18", "Recogen a Glicera, la niña de los vecinos"),
                    suceso("higuera", "11-18", "Planta una higuera en el patio, por su padre"),
                )),
            TemporadaVida("429 a. C.", "peste",
                "La peste sigue, pero este año nadie invade y se puede segar. Nace Irene. Ya no puede enfermar: lleva agua a los que sí. Formión vence en el golfo, muere Pericles y una noche el enemigo llega hasta Salamina.",
                fiestasAticas(3) + listOf(
                    guerra("potidea", "01-22", "Llega la noticia: Potidea se ha rendido", certeza = APROXIMADO, fuente = F_POTIDEA),
                    suceso("nace_irene", "03-12", "Nace su hija", cadena = "irene"),
                    suceso("nombre_irene", "03-21", "Le ponen nombre a la niña: Irene, «paz»", cadena = "irene", escena = "fiesta"),
                    cosecha("siega", "05-12", SIEGA_ATICA, 25, APROXIMADO),
                    guerra("sitio_platea", "05-24", "Este año no entran al Ática: van a sitiar Platea", certeza = APROXIMADO, fuente = F_PLATEA),
                    suceso("agua_muros", "07-06", "Lleva agua a los enfermos de los Muros Largos"),
                    cosecha("vendimia", "09-08", VENDIMIA_ATICA, 12, APROXIMADO),
                    guerra("formion", "09-10", "Llega la noticia: Formión venció dos veces en el golfo, con veinte naves", certeza = APROXIMADO, fuente = F_RIO, escena = "fiesta"),
                    mundo("muere_pericles", "10-05", "Muere Pericles", certeza = APROXIMADO, fuente = F_PERICLES, escena = "luto"),
                    guerra("alarma", "11-12", "Fuegos de alarma en la noche: el enemigo está en Salamina", 2, APROXIMADO, F_CNEMO, "antorchas"),
                    suceso("glicera_padre", "11-25", "Glicera lo llama padre por primera vez"),
                )),
            TemporadaVida("428 a. C.", "guerra",
                "La peste afloja. Vuelve Formión, vuelve la invasión y Mitilene se rebela. Rema en las cien naves que Atenas saca para que Esparta las vea. Cares aprende a nadar. Va con Eufronio a ver qué quedó de Acarnas.",
                fiestasAticas(4) + listOf(
                    fiesta("hipolito", "03-17", "Eurípides gana el primer premio con «Hipólito»", certeza = APROXIMADO, fuente = F_HIPOLITO),
                    guerra("vuelve_formion", "03-30", "Formión vuelve al Pireo con las naves que capturó", certeza = APROXIMADO, escena = "regreso"),
                    cosecha("siega", "05-01", "La siega, deprisa, antes de que lleguen", 17, APROXIMADO),
                    guerra("tercera_invasion", "05-18", "Los peloponesios entran al Ática por tercera vez", 25, APROXIMADO, F_MITILENE, "humo"),
                    guerra("mitilene", "06-24", "Mitilene se rebela: cuarenta naves salen hacia Lesbos", certeza = APROXIMADO, fuente = F_MITILENE, escena = "partida"),
                    suceso("cares_nada", "07-22", "Cares aprende a nadar en el puerto de Zea"),
                    guerra("cien_naves", "08-22", "Cien naves más, con ciudadanos y metecos al remo, para que Esparta las vea", 10, APROXIMADO, F_MITILENE, "partida"),
                    cosecha("vendimia", "09-08", VENDIMIA_ATICA, 12, APROXIMADO),
                    suceso("acarnas", "09-26", "Acompaña a Eufronio a Acarnas, a ver qué quedó"),
                    suceso("remo_nuevo", "11-06", "Le dan un remo nuevo, de abeto, y le pone su marca"),
                    guerra("fuga_platea", "12-20", "Llega la noticia: los de Platea escaparon de noche por encima del muro", certeza = APROXIMADO, fuente = F_PLATEA),
                )),
            TemporadaVida("427 a. C.", "guerra",
                "La cuarta invasión. Mitilene se rinde y la asamblea la condena; al día siguiente se arrepiente y él rema día y noche en el segundo trirreme, que llega a tiempo. Sosias se va a Sicilia. En invierno vuelve la peste.",
                fiestasAticas(5) + listOf(
                    suceso("cares_rema", "02-20", "Cares rema por primera vez, en una barca del puerto"),
                    suceso("tela_melita", "04-18", "Melita vende en el ágora la primera tela de su telar"),
                    guerra("cuarta_invasion", "05-15", "La cuarta invasión, la peor después de la del año de la peste", 35, APROXIMADO, F_MITILENE, "humo"),
                    guerra("mitilene_cae", "07-08", "Llega la noticia: Mitilene se ha rendido", certeza = APROXIMADO, fuente = F_MITILENE),
                    guerra("condena", "07-14", "La asamblea condena a toda Mitilene y sale un trirreme con la orden", certeza = APROXIMADO, fuente = F_DEBATE),
                    guerra("segundo_trirreme", "07-15", "La asamblea se arrepiente: un segundo trirreme sale a alcanzar al primero", 2, APROXIMADO, F_DEBATE, "partida"),
                    guerra("llegan_a_tiempo", "07-17", "El segundo trirreme llega a tiempo", certeza = APROXIMADO, fuente = F_DEBATE),
                    guerra("platea_cae", "08-12", "Llega la noticia: Platea se rindió y la arrasaron", certeza = APROXIMADO, fuente = F_PLATEA, escena = "luto"),
                    suceso("higos", "09-01", "La higuera del patio da sus primeros higos"),
                    cosecha("vendimia", "09-08", VENDIMIA_ATICA, 12, APROXIMADO),
                    guerra("sicilia", "09-22", "Veinte naves salen hacia Sicilia", certeza = APROXIMADO, fuente = F_LAQUES, escena = "partida"),
                    suceso("techo", "11-12", "Arregla el techo antes de las lluvias"),
                    mundo("peste_vuelve", "12-02", "La peste vuelve a Atenas", certeza = APROXIMADO, fuente = F_PESTE, escena = "piras"),
                    mundo("sismos", "12-12", "Tiembla la tierra en Atenas", certeza = APROXIMADO),
                )),
        ),
        arcos = listOf(
            Arco.cadaAnio("campo", TipoArco.CULTIVO, "01-15" to 0.3, "03-20" to 0.6, "05-10" to 1.0, "06-08" to 1.0, "06-10" to 0.0, "11-01" to 0.0, "12-20" to 0.2, saltos = setOf("06-10")),
            Arco("estragos", TipoArco.DANOS, listOf(p(1, "01-01", 0.0), p(1, "06-22", 0.0), p(1, "07-20", 0.5), p(2, "05-04", 0.3), p(2, "06-13", 1.0), p(3, "12-31", 0.5),
                p(4, "05-17", 0.45), p(4, "06-12", 0.8), p(5, "05-14", 0.6), p(5, "06-19", 0.95), p(5, "12-31", 0.75))),
            Arco("refugiados", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(1, "06-10", 0.0), p(1, "06-23", 1.0), p(1, "07-20", 1.0), p(1, "08-20", 0.5), p(2, "04-28", 0.5),
                p(2, "05-05", 1.0), p(2, "12-31", 0.9), p(3, "12-31", 0.7), p(4, "05-12", 0.65), p(4, "05-18", 1.0), p(4, "06-12", 1.0), p(4, "07-15", 0.7),
                p(5, "05-09", 0.65), p(5, "05-15", 1.0), p(5, "06-19", 1.0), p(5, "07-25", 0.7), p(5, "12-31", 0.7))),
            Arco("peste", TipoArco.DANOS, listOf(p(1, "01-01", 0.0), p(2, "05-13", 0.0), p(2, "07-01", 1.0), p(3, "03-01", 0.9), p(3, "12-31", 0.35), p(4, "08-01", 0.05),
                p(5, "11-25", 0.0), p(5, "12-31", 0.5))),
            Arco("flota", TipoArco.OTRO, listOf(p(1, "01-01", 1.0), p(1, "07-05", 0.3, true), p(1, "09-18", 1.0, true), p(2, "06-06", 0.3, true), p(2, "07-10", 1.0, true),
                p(2, "07-22", 0.4, true), p(2, "09-01", 1.0, true), p(2, "12-01", 0.85, true), p(3, "06-20", 0.7, true), p(4, "03-30", 0.9, true), p(4, "06-24", 0.65, true),
                p(4, "08-22", 0.1, true), p(4, "09-01", 0.65, true), p(5, "07-20", 0.7, true), p(5, "09-22", 0.55, true))),
            Arco("higuera", TipoArco.CULTIVO, listOf(p(1, "01-01", 0.0), p(2, "11-18", 0.08, true), p(3, "12-31", 0.3), p(4, "12-31", 0.5), p(5, "12-31", 0.7))),
        ),
        objetos = listOf("remo", "cojin", "estrobo", "anfora", "lampara", "red", "higos", "dracma", "aulos", "copa", "manto", "pan"),
    )

    // =====================================================================
    //  Pierre, cantero. La obra de la catedral de Amiens, de 1240 a 1244.
    // =====================================================================
    //
    // Su gente (toda inventada, salvo los maestros): Mahaut, su mujer. Jehanne, su hija (14 años en la temporada 1),
    // que se casa con un carpintero de la obra en la 4. Colin, su hijo (11), que entra de aprendiz. Agnès, que nace en
    // la temporada 2. Gautier, el del mortero, su amigo de la logia. Enguerrand, mercader de waide (la hierba del
    // tinte azul que hizo rica a Amiens), que le encarga un dintel el año en que no hay obra. Los maestros de la obra
    // sí existieron: Thomas de Cormont y su hijo Renaud; no se sabe en qué año pasó la obra de uno a otro.
    //
    // Lo real: en 1240 se acabó el dinero y la obra del coro se detuvo; se sacaron las reliquias de san Honorato a
    // pedir por los pueblos; en 1243 se hizo una capilla del coro; en 1243 y 1244 llegaron los frailes; la guerra del
    // rey contra los ingleses, el eclipse de 1241, el papa nuevo y la enfermedad del rey. Las fiestas de la Iglesia
    // van en su fecha; la Pascua de cada año está calculada.
    //
    // Claves de escena: fiesta, procesion, hogueras, luto, eclipse, obra_parada, inundacion y campanas. Arcos: coro
    // (lo que ha subido la cabecera), andamios (cuánta obra hay: 0.2 es el invierno, con los muros tapados de paja),
    // campo (el trigo), guede (los campos de waide, con sus cogidas) y agua (la crecida del Somme).

    private val WF = "https://fr.wikipedia.org/wiki/"
    private val F_AMIENS = "La catedral de Amiens" to "${WF}Cath%C3%A9drale_Notre-Dame_d%27Amiens"
    private val F_ARNOUL = "Arnoul de la Pierre, obispo de Amiens" to "${WF}Arnoul_de_la_Pierre"
    private val F_FIRMIN = "San Fermín de Amiens" to "${WF}Firmin_d%27Amiens"
    private val F_GUEDE = "La waide o hierba pastel" to "${WF}Gu%C3%A8de"
    private val F_HONORATO = "San Honorato de Amiens" to "${W}Honoratus_of_Amiens"
    private val F_CORONADOS = "Los Cuatro Coronados" to "${W}Four_Crowned_Martyrs"
    private val F_CANDELARIA = "La Candelaria" to "${W}Candlemas"
    private val F_SAN_JUAN = "La Natividad de san Juan Bautista" to "${W}Nativity_of_John_the_Baptist"
    private val F_ASUNCION = "La Asunción" to "${W}Assumption_of_Mary"
    private val F_TODOS_SANTOS = "Todos los Santos" to "${W}All_Saints%27_Day"
    private val F_SAN_MARTIN = "San Martín" to "${W}St._Martin%27s_Day"
    private val F_NAVIDAD = "Los doce días de Navidad" to "${W}Twelve_Days_of_Christmas"
    private val F_TAILLEBOURG = "La batalla de Taillebourg" to "${W}Battle_of_Taillebourg"
    private val F_SAINTONGE = "La guerra de Saintonge" to "${W}Saintonge_War"
    private val F_LEGNICA = "La batalla de Legnica" to "${W}Battle_of_Legnica"
    private val F_PAPA = "La elección papal de 1243" to "${W}1243_papal_election"
    private val F_1243 = "El año 1243" to "${W}1243"
    private val F_1244 = "El año 1244" to "${W}1244"

    // Lo que cuelga de la Pascua juliana de 1240, 1241, 1242, 1243 y 1244.
    private val CARNAVAL = listOf("02-28", "02-12", "03-04", "02-24", "02-16")     // el martes antes de la ceniza
    private val PASCUA = listOf("04-15", "03-31", "04-20", "04-12", "04-03")
    private val ROGATIVAS = listOf("05-21", "05-06", "05-26", "05-18", "05-09")    // los tres días antes de la Ascensión

    /** Las fiestas y las faenas de Amiens que vuelven cada año, en la temporada [t] (de 1 a 5). */
    private fun fiestasDeAmiens(t: Int): List<DiaEspecial> = listOf(
        fiesta("candelaria", "02-02", "La Candelaria: velas benditas, y pronto vuelve la obra", certeza = HISTORICO, fuente = F_CANDELARIA),
        fiesta("carnaval", CARNAVAL[t - 1], "Martes de carnaval: el último día de carne antes de la cuaresma", certeza = HISTORICO),
        fiesta("pascua", PASCUA[t - 1], "Pascua: la obra calla y las campanas no", 2, HISTORICO, escena = "campanas"),
        fiesta("rogativas", ROGATIVAS[t - 1], "Las rogativas: la procesión sale a bendecir los campos", 3, HISTORICO, escena = "procesion"),
        cosecha("guede", "06-18", "La primera cogida de la waide, la hierba del azul", 8, APROXIMADO, F_GUEDE),
        fiesta("san_juan", "06-24", "San Juan: hogueras, y la cabeza del Bautista en su altar", certeza = HISTORICO, fuente = F_SAN_JUAN, escena = "hogueras"),
        cosecha("siega", "08-01", "La siega del trigo", 20, APROXIMADO),
        fiesta("asuncion", "08-15", "La Asunción: la fiesta de Nuestra Señora, la dueña de la catedral", certeza = HISTORICO, fuente = F_ASUNCION, escena = "procesion"),
        fiesta("san_firmin", "09-25", "San Fermín, patrón de Amiens: sale su arca de plata", certeza = HISTORICO, fuente = F_FIRMIN, escena = "procesion"),
        fiesta("todos_santos", "11-01", "Todos los Santos y el día de los difuntos", 2, HISTORICO, F_TODOS_SANTOS, "luto"),
        fiesta("cuatro_coronados", "11-08", "Los Cuatro Coronados, patronos de los canteros", certeza = APROXIMADO, fuente = F_CORONADOS),
        fiesta("san_martin", "11-11", "San Martín: se acaba la temporada de obra y empieza el jornal de invierno", certeza = HISTORICO, fuente = F_SAN_MARTIN),
        fiesta("cabeza_bautista", "12-17", "El día en que llegó a Amiens la cabeza de san Juan", certeza = APROXIMADO, fuente = F_AMIENS, escena = "procesion"),
        fiesta("navidad", "12-25", "Los doce días de Navidad", 12, HISTORICO, F_NAVIDAD),
    )

    val PIERRE = Calendario(
        vida = "cantero",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 4, nubes = 9, lluvia = 7, nieve = 4, niebla = 5, viento = 2), mes(despejado = 5, nubes = 9, lluvia = 7, nieve = 3, niebla = 4, viento = 3),
            mes(despejado = 7, nubes = 9, lluvia = 8, nieve = 1, niebla = 2, viento = 4), mes(despejado = 9, nubes = 8, lluvia = 8, tormenta = 1, viento = 3),
            mes(despejado = 11, nubes = 8, lluvia = 7, tormenta = 2, viento = 2), mes(despejado = 13, nubes = 7, lluvia = 6, tormenta = 3),
            mes(despejado = 15, nubes = 6, lluvia = 5, tormenta = 4), mes(despejado = 14, nubes = 7, lluvia = 5, tormenta = 4),
            mes(despejado = 11, nubes = 8, lluvia = 7, niebla = 3, viento = 1), mes(despejado = 6, nubes = 9, lluvia = 9, niebla = 4, viento = 2),
            mes(despejado = 3, nubes = 9, lluvia = 9, niebla = 6, viento = 3), mes(despejado = 3, nubes = 9, lluvia = 8, nieve = 3, niebla = 5, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1240", "escasez",
                "Se acaba el dinero de la obra y el coro se detiene. Sacan las reliquias de san Honorato a pedir por los pueblos. Colin entra de aprendiz. A él lo dejan a medio jornal y un mercader de waide le encarga un dintel.",
                fiestasDeAmiens(1) + listOf(
                    suceso("primera_piedra", "03-12", "Pone su marca en la primera piedra del año"),
                    rito("reliquias_honorato", "05-16", "Sacan las reliquias de san Honorato a pedir por los pueblos para la obra", certeza = APROXIMADO, fuente = F_HONORATO, escena = "procesion"),
                    suceso("colin_aprendiz", "07-02", "Su hijo Colin entra de aprendiz en la logia"),
                    mundo("sin_dinero", "09-03", "Se acaba el dinero de la fábrica: se detiene la obra del coro", certeza = APROXIMADO, fuente = F_AMIENS, escena = "obra_parada"),
                    suceso("medio_jornal", "09-10", "Lo dejan a medio jornal: no hay piedra que pagar"),
                    suceso("encargo_dintel", "10-02", "Enguerrand, el de la waide, le encarga el dintel de su casa"),
                    suceso("entrega_dintel", "12-01", "Entrega el dintel y le pagan una parte en paño azul"),
                )),
            TemporadaVida("1241", "miedo",
                "La obra vuelve despacio. Llegan noticias de los tártaros y en octubre el sol se apaga a mediodía. Cede un andamio y se lastima el hombro; pasa el verano sin tallar. Nace Agnès.",
                fiestasDeAmiens(2) + listOf(
                    suceso("vuelve_obra", "03-04", "Lo llaman otra vez al coro: la obra vuelve, despacio"),
                    suceso("cae_andamio", "05-27", "Cede un andamio: cae y se lastima el hombro", cadena = "hombro"),
                    mundo("tartaros", "06-10", "Llegan noticias de los tártaros: han vencido en Polonia y en Hungría", certeza = APROXIMADO, fuente = F_LEGNICA),
                    suceso("nace_agnes", "07-15", "Nace su hija Agnès"),
                    suceso("vuelve_a_tallar", "08-25", "Vuelve a tallar, primero piedra blanda", cadena = "hombro"),
                    mundo("eclipse_mediodia", "10-06", "El sol se apaga a mediodía", certeza = HISTORICO, escena = "eclipse", clima = Clima.DESPEJADO),
                    suceso("capitel_hiedra", "11-20", "Termina un capitel de hojas de hiedra, el primero desde la caída"),
                )),
            TemporadaVida("1242", "guerra",
                "El Somme se sale en enero. El rey llama a la hueste contra los ingleses y Gautier se va con los sargentos de la comuna; vuelve en otoño. Él sube otra vez al andamio alto y Colin talla su primera dovela.",
                fiestasDeAmiens(3) + listOf(
                    mundo("crecida", "01-18", "El Somme se sale y anega el barrio de Saint-Leu", 6, INVENTADO, escena = "inundacion", clima = Clima.LLUVIA),
                    guerra("hueste", "04-28", "El rey llama a la hueste: los sargentos de la comuna salen hacia Poitou", certeza = APROXIMADO, fuente = F_SAINTONGE),
                    suceso("andamio_alto", "05-04", "Sube otra vez al andamio alto"),
                    guerra("taillebourg", "07-21", "El rey vence a los ingleses en el puente de Taillebourg", 2, HISTORICO, F_TAILLEBOURG),
                    guerra("campanas_victoria", "08-03", "La noticia llega a Amiens: tocan todas las campanas", certeza = INVENTADO, escena = "campanas"),
                    suceso("vuelve_gautier", "09-14", "Gautier vuelve de la hueste, flaco y con historias"),
                    suceso("dovela_colin", "10-12", "Colin talla su primera dovela"),
                )),
            TemporadaVida("1243", "tregua",
                "Maese Thomas deja la obra a su hijo Renaud. Tregua con Inglaterra y papa nuevo. Se casa Jehanne. Lo nombran aparejador de una capilla del coro, y la cierran en otoño. Llegan a Amiens los frailes predicadores.",
                fiestasDeAmiens(4) + listOf(
                    suceso("maese_renaud", "02-03", "Maese Thomas deja la obra a su hijo Renaud"),
                    mundo("tregua", "04-27", "Tregua con el rey de Inglaterra", certeza = APROXIMADO, fuente = F_1243),
                    suceso("boda_jehanne", "06-09", "Se casa su hija Jehanne con un carpintero de la obra", escena = "fiesta"),
                    mundo("papa_nuevo", "06-25", "Hay papa otra vez, después de año y medio: Inocencio IV", certeza = HISTORICO, fuente = F_PAPA, escena = "campanas"),
                    suceso("aparejador", "08-28", "Lo nombran aparejador de la capilla nueva"),
                    mundo("predicadores", "09-08", "Llegan a Amiens los frailes predicadores", certeza = APROXIMADO, fuente = F_ARNOUL),
                    rito("cierran_capilla", "10-20", "Cierran la bóveda de la capilla nueva del coro", certeza = APROXIMADO, fuente = F_AMIENS),
                )),
            TemporadaVida("1244", "cruzada",
                "Nace su primer nieto y Colin recibe su marca. Llegan los frailes menores. Lo llaman de Beauvais y decide quedarse. Jerusalén se pierde; en diciembre el rey enferma, sana y promete la cruzada.",
                fiestasDeAmiens(5) + listOf(
                    suceso("nieto", "02-08", "Nace su primer nieto"),
                    suceso("marca_colin", "04-25", "Colin recibe su propia marca de cantero"),
                    mundo("menores", "05-30", "Llegan a Amiens los frailes menores", certeza = APROXIMADO, fuente = F_ARNOUL),
                    suceso("beauvais", "07-07", "Lo llaman de la obra de Beauvais, y decide quedarse"),
                    suceso("gargola", "10-10", "Termina la gárgola que lleva dos inviernos tallando"),
                    mundo("jerusalen", "11-20", "Llega la noticia: Jerusalén se ha perdido", certeza = APROXIMADO, fuente = F_1244, escena = "luto"),
                    mundo("rey_enfermo", "12-12", "El rey está muy enfermo; sana y promete ir a la cruzada", 3, APROXIMADO, F_1244, "procesion"),
                )),
        ),
        arcos = listOf(
            Arco("coro", TipoArco.OBRA, listOf(p(1, "01-01", 0.10), p(1, "03-01", 0.10), p(1, "09-03", 0.17), p(2, "03-04", 0.17), p(2, "11-11", 0.22), p(3, "03-01", 0.22),
                p(3, "11-11", 0.31), p(4, "03-01", 0.31), p(4, "11-11", 0.43), p(5, "03-01", 0.43), p(5, "11-11", 0.54), p(5, "12-31", 0.54))),
            Arco("andamios", TipoArco.OTRO, listOf(p(1, "01-01", 0.2), p(1, "02-02", 0.2), p(1, "03-15", 1.0), p(1, "09-02", 1.0), p(1, "09-03", 0.25, true), p(2, "03-03", 0.2),
                p(2, "03-04", 0.6, true), p(2, "05-27", 0.35, true), p(2, "07-15", 0.6), p(2, "11-11", 0.6), p(2, "11-25", 0.2), p(3, "02-10", 0.2), p(3, "03-20", 0.8),
                p(3, "11-11", 0.8), p(3, "11-25", 0.2), p(4, "02-10", 0.2), p(4, "03-20", 1.0), p(4, "11-11", 1.0), p(4, "11-25", 0.2), p(5, "02-10", 0.2), p(5, "03-20", 1.0),
                p(5, "11-11", 1.0), p(5, "11-25", 0.2), p(5, "12-31", 0.2))),
            Arco.cadaAnio("campo", TipoArco.CULTIVO, "03-01" to 0.25, "05-15" to 0.6, "07-20" to 1.0, "08-01" to 1.0, "08-21" to 0.0, "10-10" to 0.0, "12-01" to 0.12),
            Arco.cadaAnio("guede", TipoArco.CULTIVO, "03-01" to 0.0, "06-15" to 1.0, "06-26" to 0.5, "08-01" to 0.9, "08-10" to 0.45, "09-15" to 0.8, "09-25" to 0.0),
            Arco("agua", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(3, "01-17", 0.0), p(3, "01-21", 1.0), p(3, "01-24", 1.0), p(3, "02-08", 0.0))),
        ),
        objetos = listOf("mazo", "cincel", "escuadra", "compas", "plomada", "marca", "cuerda", "jarra", "pan", "plantilla", "capucha", "vela"),
    )

    // =====================================================================
    //  Kei, repartidora, y Amaru, vendedor de fideos. Una megaciudad de neón, de 2077 a 2081.
    // =====================================================================
    //
    // Todo es inventado. Los dos viven en la misma ciudad y sus historias se tocan: el callejón de Amaru, en el nivel
    // −3, queda en las rutas de Kei; ella empieza a cenar en su puesto (temporada 1) y en la 3 él aprende su nombre.
    // Lo que le pasa a la ciudad es lo mismo en los dos calendarios, con el mismo id y la misma fecha ([neon]).
    //
    // El mundo: Áurea, una corporación, compra Colibrí, la aplicación de repartos (temporada 1), y sube las multas
    // (2). Los repartidores hacen huelga y hay toque de queda en los niveles bajos (3). Áurea quiere demoler el
    // nivel −3 y los vecinos se plantan (4). Un tribunal suspende la demolición y las máquinas se van (5).
    //
    // La gente de Kei: Dalia, que le arregla la bici; Río, otro repartidor, que deja las calles; la novata a la que
    // enseña las rutas. La gente de Amaru: Celso, el de la farmacia de guardia de enfrente, y Sami, su sobrina, que
    // llega a ayudarle en la temporada 3.
    //
    // Claves de escena: fiesta, faroles, velas, apagon, huelga, toque, maquinas, cadena y agua. Arcos comunes: agua
    // (cuánto se anega el callejón en las lluvias), luces (los adornos de fin de año), pintas (carteles y pintadas),
    // obra (las máquinas de demolición) y letrero (0 = el tazón magenta parpadea bien; 1 = apagado). Kei tiene además
    // bici (0 = entera; 1 = torcida).

    /** Lo que vuelve cada año en la ciudad, en la temporada [t]. Es igual para Kei y para Amaru. */
    private fun fiestasDeNeon(t: Int): List<DiaEspecial> = listOf(
        fiesta("faroles", listOf("01-24", "02-12", "02-02", "01-22", "02-09")[t - 1], "La Noche de los Faroles: tres noches de luz de papel entre el neón", 3, INVENTADO, escena = "faroles"),
        fiesta("dia_red", "03-14", "El Día de la Red: el aniversario de la noche en que se encendió la ciudad", certeza = INVENTADO),
        mundo("lluvias", "05-15", "Empiezan las lluvias", certeza = INVENTADO, clima = Clima.LLUVIA),
        fiesta("dia_aurea", "06-01", "El Día de la Corporación: desfile de drones y descanso obligatorio", certeza = INVENTADO),
        mundo("marea", "07-20", "Sube el agua: el nivel −3 se anega hasta el tobillo", 5, INVENTADO, escena = "agua", clima = Clima.TORMENTA),
        fiesta("hora_muda", "08-08", "La Hora Muda: por ley, una hora sin un solo anuncio", certeza = INVENTADO, escena = "apagon"),
        fiesta("feria", "09-15", "La feria de los niveles bajos: cuatro noches de puestos y música", 4, INVENTADO),
        cosecha("cosecha_torres", "10-05", "La cosecha de las torres-granja: hay verdura de verdad en el mercado", 8, INVENTADO),
        fiesta("ausentes", "11-01", "La Noche de los Ausentes: velas y retratos de luz para los muertos", 2, INVENTADO, escena = "velas"),
        fiesta("descuentos", "11-24", "La Semana del Descuento: la ciudad entera pide algo", 5, INVENTADO),
        fiesta("nochevieja", "12-31", "La última noche del año: todos los letreros cuentan hacia atrás", 2, INVENTADO),
    )

    /** Lo que le pasa a la ciudad en la temporada [t] y solo ese año. Es igual para Kei y para Amaru. */
    private fun sucesosDeNeon(t: Int): List<DiaEspecial> = when (t) {
        1 -> listOf(
            mundo("compra_colibri", "10-18", "Áurea compra Colibrí, la aplicación de los repartos", certeza = INVENTADO),
        )
        2 -> listOf(
            mundo("suben_multas", "02-01", "Colibrí sube la multa por llegar tarde", certeza = INVENTADO),
            mundo("apagon", "08-20", "Apagón en el nivel −3: tres noches sin letreros", 3, INVENTADO, escena = "apagon"),
        )
        3 -> listOf(
            guerra("huelga", "03-03", "Empieza la huelga de los repartidores", 12, INVENTADO, escena = "huelga"),
            guerra("carga_puente", "03-15", "La seguridad de Áurea carga contra los huelguistas en el puente", certeza = INVENTADO, escena = "huelga"),
            mundo("toque", "03-16", "Toque de queda en los niveles bajos", 10, INVENTADO, escena = "toque"),
            mundo("cede_colibri", "06-20", "Colibrí cede: baja la multa y vuelve a pagar la espera", certeza = INVENTADO),
        )
        4 -> listOf(
            mundo("aviso_demolicion", "02-15", "Aviso de demolición: Áurea quiere el nivel −3 para una torre", certeza = INVENTADO),
            mundo("firmas", "05-02", "Los vecinos del callejón juntan firmas contra la demolición", certeza = INVENTADO),
            mundo("maquinas", "09-01", "Llegan las máquinas a la boca del callejón, y ahí se quedan", certeza = INVENTADO, escena = "maquinas"),
            guerra("cadena_humana", "11-10", "Cadena humana delante de las máquinas", certeza = INVENTADO, escena = "cadena"),
        )
        else -> listOf(
            mundo("tribunal", "03-20", "Un tribunal suspende la demolición del nivel −3", certeza = INVENTADO, escena = "fiesta"),
            mundo("se_van_maquinas", "08-30", "Las máquinas se van del callejón", certeza = INVENTADO),
        )
    }

    private fun neon(t: Int, vararg propios: DiaEspecial): List<DiaEspecial> = fiestasDeNeon(t) + sucesosDeNeon(t) + propios

    private val ESTACIONES_NEON = listOf("05-15" to Estacion.LLUVIAS, "11-01" to Estacion.SECAS)
    private val CLIMA_NEON = listOf(
        mes(despejado = 3, nubes = 10, lluvia = 4, niebla = 10, polvo = 3), mes(despejado = 3, nubes = 10, lluvia = 4, niebla = 10, polvo = 3),
        mes(despejado = 3, nubes = 10, lluvia = 5, niebla = 9, polvo = 3), mes(despejado = 2, nubes = 10, lluvia = 7, niebla = 8, polvo = 2),
        mes(despejado = 1, nubes = 8, lluvia = 12, tormenta = 3, niebla = 6), mes(despejado = 1, nubes = 6, lluvia = 14, tormenta = 5, niebla = 4),
        mes(despejado = 1, nubes = 5, lluvia = 14, tormenta = 7, niebla = 3), mes(despejado = 1, nubes = 5, lluvia = 14, tormenta = 6, niebla = 4),
        mes(despejado = 1, nubes = 7, lluvia = 13, tormenta = 4, niebla = 5), mes(despejado = 2, nubes = 9, lluvia = 9, tormenta = 2, niebla = 8),
        mes(despejado = 3, nubes = 10, lluvia = 5, niebla = 10, polvo = 2), mes(despejado = 3, nubes = 10, lluvia = 4, niebla = 10, polvo = 3),
    )
    private val ARCOS_NEON = listOf(
        Arco.cadaAnio("agua", TipoArco.OTRO, "05-01" to 0.0, "06-15" to 0.3, "07-19" to 0.6, "07-22" to 1.0, "08-10" to 0.5, "10-01" to 0.2, "11-05" to 0.0),
        Arco.cadaAnio("luces", TipoArco.ADORNOS, "01-03" to 1.0, "01-10" to 0.1, "12-10" to 0.1, "12-31" to 1.0),
        Arco("pintas", TipoArco.ADORNOS, listOf(p(1, "01-01", 0.0), p(3, "03-02", 0.0), p(3, "03-14", 0.8), p(3, "12-31", 0.3), p(4, "02-14", 0.3), p(4, "11-10", 0.9), p(5, "12-31", 0.5))),
        Arco("obra", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(4, "09-01", 0.6, true), p(5, "08-30", 0.1, true))),
        Arco("letrero", TipoArco.DANOS, listOf(p(1, "01-01", 0.0), p(2, "11-12", 1.0, true), p(2, "11-29", 0.15, true), p(4, "06-15", 0.0, true))),
    )

    val KEI = Calendario(
        vida = "repartidora", estaciones = ESTACIONES_NEON, clima = CLIMA_NEON, luz = RegimenLuz.SIEMPRE_NOCHE,
        temporadas = listOf(
            TemporadaVida("2077", "calma",
                "Reparte de noche sin una sola multa. Una entrega la sube al piso cien. El mapa del implante le falla por primera vez. Áurea compra Colibrí. Cena por primera vez en un puesto de fideos del nivel −3.",
                neon(1,
                    suceso("bateria", "02-20", "Dalia le cambia la batería de la bici a mitad de precio"),
                    suceso("piso_cien", "04-05", "Una entrega la sube al piso cien: ve la ciudad desde arriba"),
                    suceso("mil_pedidos", "06-18", "Mil pedidos seguidos sin una multa"),
                    suceso("mapa_falla", "09-02", "Se le apaga el mapa del ojo a media entrega"),
                    suceso("primeros_fideos", "12-12", "Cena por primera vez en un puesto de fideos del nivel −3"),
                )),
            TemporadaVida("2078", "compra",
                "Suben las multas. Choca en la lluvia y Dalia le suelda el cuadro. Río deja los repartos por un empleo en una torre. Reparte a oscuras en el apagón. Termina de pagar el implante.",
                neon(2,
                    suceso("choque", "03-22", "Choca en la lluvia y la bici queda torcida", cadena = "choque", clima = Clima.LLUVIA),
                    suceso("vuelve_a_rodar", "04-08", "Vuelve a rodar, con el cuadro soldado por Dalia", cadena = "choque"),
                    suceso("rio_se_va", "07-07", "Río deja los repartos: le dieron un empleo en una torre"),
                    suceso("ultima_cuota", "11-05", "Paga la última cuota del implante"),
                )),
            TemporadaVida("2079", "huelga",
                "Se suma a la huelga. Una noche se le apaga el implante y ve la ciudad sin flechas ni anuncios. Colibrí cede. Enseña las rutas a una novata. El de los fideos le dice su nombre: Amaru.",
                neon(3,
                    suceso("sin_implante", "05-10", "Se le apaga el implante: una noche entera sin flechas ni anuncios", 2, cadena = "implante"),
                    suceso("reinicio", "05-13", "Le reinician el implante, y tarda en acostumbrarse otra vez", cadena = "implante"),
                    suceso("novata", "09-09", "Enseña a una novata las rutas de los niveles bajos"),
                    suceso("nombre_amaru", "12-05", "El de los fideos le dice su nombre: Amaru"),
                )),
            TemporadaVida("2080", "desalojo",
                "La novata ya reparte sola. Fundan la cooperativa de repartidores y hace su primera entrega con la aplicación propia. Dalia cierra el taller y se muda. Está en la cadena humana del callejón.",
                neon(4,
                    suceso("novata_sola", "01-20", "La novata hace sola su primer turno de noche"),
                    suceso("cooperativa", "04-04", "Fundan la cooperativa de repartidores: firma con el número 31"),
                    suceso("primera_entrega", "07-12", "Primera entrega con la aplicación de la cooperativa"),
                    suceso("dalia_se_muda", "10-03", "Dalia cierra el taller y se muda a otro distrito"),
                )),
            TemporadaVida("2081", "tregua",
                "Pinta la bici de azul otra vez. Pedalea hasta el borde de la ciudad y ve que afuera hay luces. Apaga los anuncios del implante, ahora que puede. Lleva a la novata a cenar al callejón.",
                neon(5,
                    suceso("bici_azul", "02-14", "Pinta la bici de azul otra vez"),
                    suceso("borde", "05-25", "Pedalea hasta el borde de la ciudad y ve que afuera hay luces"),
                    suceso("sin_anuncios", "08-12", "Apaga los anuncios del implante: ahora puede"),
                    suceso("cena_novata", "10-20", "Lleva a la novata a cenar fideos al callejón"),
                )),
        ),
        arcos = ARCOS_NEON + Arco("bici", TipoArco.DANOS, listOf(p(1, "01-01", 0.0), p(2, "03-22", 1.0, true), p(2, "04-08", 0.3, true), p(4, "12-31", 0.3), p(5, "02-14", 0.0, true))),
        objetos = listOf("casco", "bateria", "candado", "caja", "guantes", "impermeable", "lata", "linterna", "parche", "auricular", "llave", "barra"),
    )

    val AMARU = Calendario(
        vida = "fideos", estaciones = ESTACIONES_NEON, clima = CLIMA_NEON, luz = RegimenLuz.SIEMPRE_NOCHE,
        temporadas = listOf(
            TemporadaVida("2077", "calma",
                "Cambia la olla grande después de once años. Pasa la inspección por poco. Le escribe su hermana. Celso le regala una radio. Una repartidora nueva pide fideos con doble caldo.",
                neon(1,
                    suceso("olla_nueva", "01-15", "Cambia la olla grande: la vieja llevaba once años"),
                    suceso("inspeccion", "04-22", "Inspección de sanidad: pasa por poco"),
                    suceso("carta_hermana", "08-25", "Le escribe su hermana, después de años sin saber de ella"),
                    suceso("radio", "10-30", "Celso, el de la farmacia, le regala una radio de las de antes"),
                    suceso("doble_caldo", "12-12", "Una repartidora nueva pide fideos con doble caldo"),
                )),
            TemporadaVida("2078", "compra",
                "Sube el caldo de tanque. Un guardia de Áurea se vuelve cliente de todas las noches. Se quema una mano con la olla. En el apagón cocina con gas y velas y el callejón entero come ahí. Se le descompone el letrero y lo arregla con una pieza del mercado flotante.",
                neon(2,
                    suceso("sube_caldo", "02-18", "Sube el precio del caldo de tanque"),
                    suceso("guardia", "04-15", "Un guardia de Áurea empieza a cenar en el puesto todas las noches"),
                    suceso("quemadura", "06-10", "Se quema la mano con la olla", cadena = "mano"),
                    suceso("dos_manos", "06-24", "Vuelve a servir con las dos manos", cadena = "mano"),
                    suceso("letrero_roto", "11-12", "Se descompone el letrero: el tazón deja de parpadear", cadena = "letrero"),
                    suceso("letrero_arreglado", "11-29", "Arregla el letrero con una pieza del mercado flotante", cadena = "letrero"),
                )),
            TemporadaVida("2079", "huelga",
                "Da de comer fiado a los huelguistas. Con el toque de queda cierra temprano y casi no junta la renta. Llega Sami, su sobrina, a ayudar. Aprende el nombre de la repartidora de la bici azul: Kei.",
                neon(3,
                    suceso("olla_comun", "03-05", "Olla común: da de comer fiado a los huelguistas"),
                    suceso("renta", "04-20", "Casi no junta la renta del mes"),
                    suceso("llega_sami", "07-30", "Llega Sami, su sobrina, a ayudar en el puesto"),
                    suceso("nombre_kei", "12-05", "Aprende el nombre de la repartidora de la bici azul: Kei"),
                )),
            TemporadaVida("2080", "desalojo",
                "Le pegan el aviso de demolición en la cortina del puesto. Firma con los vecinos. Sami pinta la cortina y deja el letrero como nuevo. Está en la cadena humana, con el cucharón en la mano.",
                neon(4,
                    suceso("mural", "06-15", "Sami pinta la cortina del puesto y deja el letrero como nuevo"),
                    suceso("caldo_vecinos", "09-20", "Lleva la olla a la junta de vecinos"),
                    suceso("celso_enfermo", "12-08", "Celso pasa una semana en cama y él le cuida la farmacia", 7),
                )),
            TemporadaVida("2081", "tregua",
                "Sami inventa un fideo nuevo y se vende. Sube a la superficie por primera vez en nueve años. El callejón sale en el mapa con el nombre del puesto. Celso se jubila.",
                neon(5,
                    suceso("fideo_sami", "01-28", "Sami inventa un fideo nuevo, y se vende"),
                    suceso("superficie", "04-12", "Sube a la superficie por primera vez en nueve años"),
                    suceso("nombre_callejon", "07-03", "El callejón sale en el mapa con el nombre de su puesto: «El Tazón»"),
                    suceso("celso_se_jubila", "10-08", "Celso se jubila y le deja la farmacia a su hija"),
                )),
        ),
        arcos = ARCOS_NEON,
        objetos = listOf("olla", "cucharon", "tazon", "palillos", "radio", "trapo", "cebollin", "banco", "termo", "delantal", "cuchillo", "ficha"),
    )
}
