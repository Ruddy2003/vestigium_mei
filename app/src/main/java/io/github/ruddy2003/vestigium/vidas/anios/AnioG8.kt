package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Arco
import io.github.ruddy2003.vestigium.vidas.Calendario
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.DiaEspecial
import io.github.ruddy2003.vestigium.vidas.Estado
import io.github.ruddy2003.vestigium.vidas.RegimenLuz
import io.github.ruddy2003.vestigium.vidas.TemporadaVida
import io.github.ruddy2003.vestigium.vidas.TipoArco

/*
 * Los calendarios del grupo 8 (tanda A8): las cinco temporadas (2027 a 2031) de los personajes de ficción de la escriba
 * (Orden), la doctora (Laboratorio), Ruth (Colonos) y Brenna (Tierras Antiguas). Siguen las reglas de `AnioPiloto.kt`.
 *
 * Lo propio de este grupo:
 *
 * - **Todo es inventado**, también las fiestas, como en el grupo 7. Sus mundos (la Orden del Candil, el Claustro, El
 *   Remanso y los pueblos del llano, el Gremio de los Caminos) son de cada historia, sin nombres, lugares, facciones ni
 *   criaturas de ninguna obra, aunque la ficha diga en qué se inspira.
 * - **La doctora vive bajo tierra:** sin clima ni estaciones y con la luz de dos estados, como la residente. Dos días de
 *   su temporada 5, arriba, traen clima de verdad: el sol y la lluvia.
 * - **Brenna vive del paso de montaña**, que la nieve cierra cada invierno en una fecha distinta cada año.
 * - Los nombres de las temporadas: los años de su ficha («2287», «año 312 de los Caminos»).
 */
internal object AnioG8 {
    val TODOS: List<Calendario> by lazy { listOf(ESCRIBA, CIENTIFICA, RUTH, BRENNA) }

    /** Días «históricos» sin enlace porque su fecha sale de un cálculo: en la ficción no hay ninguno. */
    val CALCULADOS = emptySet<String>()

    private val I = INVENTADO
    private val SIN_CLIMA = List(12) { mapOf(Clima.SIN_CLIMA to 1) }

    // =====================================================================
    //  La escriba de una orden de caballeros. Peñaseca, una ciudadela en el páramo, de 2287 a 2291.
    // =====================================================================
    //
    // Su gente (toda inventada): Nieves, caballera de su patrulla y su amiga, que la carga herida hasta la ciudadela en
    // la temporada 4 y manda la patrulla desde entonces. Ulises, caballero viejo, que va con ella al sur en la 2, cuelga
    // el caparazón en la 3 y se queda en la fragua. Fermín, el custodio de la Cámara y su maestro, que se va quedando
    // ciego (temporadas 1 a 3), le da las llaves en la 3 y muere en la 4. Jimena, una niña de once años que encuentran
    // sola en la temporada 1: aprende a leer en la 2, jura como candilera en la 4 y sale a su primera patrulla en la 5.
    // Damián, el piloto de la Garza, con quien empieza en la 2, vive desde la 3 y tiene a Ciro, que nace en la 5. La
    // maestre Amparo, que manda la orden.
    //
    // Su mundo (inventado, sin nada de ninguna obra): la Orden del Candil guarda en la Cámara de Peñaseca, una ciudadela
    // sobre una meseta, las máquinas y los manuales de antes del Fuego, para estudiarlos y que nadie los use mal. Sus
    // rangos: candileros (los que empiezan), caballeros (con caparazón, la armadura de placas con motor), escribas,
    // el custodio de la Cámara y la maestre. Cada noche se recita el Código del Candil. La nave de la orden es la Garza,
    // un dirigible con dos motores que se amarra a la torre. El agua sale de un pozo al pie de la meseta y de una
    // cisterna que llenan las tormentas de julio y agosto.
    //
    // Claves de escena: fiesta, polvo, alarma, luto, tormenta, caza. Arcos: codice (lo descifrado del núcleo de memoria
    // que trae de la Bóveda de Valdecruz: de la temporada 2 a la 5), garza (la nave entera y en el aire = 1; rota en el
    // patio desde la tormenta de la temporada 3 hasta que vuelve a volar en la 4) y cisterna (el agua guardada: baja
    // hasta julio y sube con las tormentas; en la temporada 5, la sequía, casi toca fondo). Estados: «expedicion»
    // (temporada 2, siete semanas en la Garza hasta la Bóveda), «encierro» (temporada 3, un mes encerrada en el archivo
    // con el núcleo) y «herida» (temporada 4, cinco semanas en la enfermería tras la emboscada).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: Peñaseca en su meseta, con la Garza
    // amarrada a la torre según `garza` (entera y en el aire, o desinflada en el patio con la tela rota y andamios), la
    // cisterna y sus canales según `cisterna`, y en la ventana de la Cámara el núcleo y las hojas copiadas según
    // `codice`; durante «expedicion», un cañón seco del sur con la boca de la Bóveda y la Garza amarrada lejos de casa;
    // durante «encierro», la ventana del archivo encendida toda la noche (adentro, un catre entre los estantes); y
    // durante «herida», la enfermería, con un catre junto a la ventana que da al camino de las patrullas.

    /** Lo que vuelve cada año en Peñaseca. En la temporada [t] 2, el cierre de los postigos la pilla de viaje. */
    private fun anioDeLaOrden(t: Int): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo en Peñaseca: se lee el Código del Candil entero en el patio", certeza = I),
        rito("inventario", "02-10", "El inventario de invierno: se cuenta cada pieza guardada en la Cámara", certeza = I),
        fiesta("fundacion", "03-15", "El Día de la Fundación: los caballeros desfilan con los caparazones pintados", certeza = I),
        rito("juramento", "04-20", "Juran los candileros nuevos delante del Código, con una vela en la mano", certeza = I),
        if (t == 2) rito("postigos_lejos", "05-20", "Por la radio: en Peñaseca ya cerraron los postigos del polvo", certeza = I)
        else rito("postigos", "05-20", "Se cierran los postigos de la ciudadela: llegan los vientos del polvo", certeza = I, escena = "polvo"),
        fiesta("vigilia", "06-21", "La Vigilia de los Candiles: se encienden todas las lámparas viejas que aún sirven", certeza = I),
        rito("canales", "07-12", "Las primeras tormentas del verano: se abren los canales hacia la cisterna", certeza = I),
        cosecha("caza", "08-25", "La caza grande en el llano: se sala carne para todo el invierno", 5, certeza = I, escena = "caza"),
        rito("caidos", "09-20", "El Día de los Caídos: se dice en voz alta el nombre de cada caballero muerto", certeza = I, escena = "luto"),
        rito("consejo", "10-15", "El Consejo de otoño: la maestre reparte las misiones del año", certeza = I),
        rito("rutas", "11-15", "Se cierran las patrullas largas: empieza el invierno en la ciudadela", certeza = I),
        fiesta("noche_larga", "12-21", "La Noche Larga: velada en el refectorio con el primer manual de la orden", certeza = I),
    )

    val ESCRIBA = Calendario(
        vida = "escriba",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 12, nubes = 8, nieve = 4, viento = 4, polvo = 1, lluvia = 1), mes(despejado = 12, nubes = 7, nieve = 3, viento = 4, polvo = 2, lluvia = 1),
            mes(despejado = 12, nubes = 6, nieve = 1, viento = 6, polvo = 4, lluvia = 2), mes(despejado = 13, nubes = 5, viento = 6, polvo = 5, lluvia = 2),
            mes(despejado = 15, nubes = 4, viento = 4, polvo = 6, lluvia = 1, tormenta = 1), mes(despejado = 18, nubes = 3, viento = 2, polvo = 6, tormenta = 1),
            mes(despejado = 16, nubes = 4, polvo = 5, lluvia = 1, tormenta = 4), mes(despejado = 16, nubes = 4, polvo = 4, lluvia = 2, tormenta = 4),
            mes(despejado = 18, nubes = 4, viento = 2, polvo = 4, lluvia = 2), mes(despejado = 17, nubes = 5, viento = 4, polvo = 3, lluvia = 2),
            mes(despejado = 14, nubes = 7, viento = 5, polvo = 2, lluvia = 1, nieve = 1), mes(despejado = 12, nubes = 8, nieve = 4, viento = 4, polvo = 1),
        ),
        temporadas = listOf(
            TemporadaVida("2287", "patrullas",
                "La escriba patrulla el páramo con Nieves y el viejo Ulises. Encuentran sola a una niña, Jimena, y la orden la recoge. Una tormenta de polvo entierra el camino del norte. Fermín, el custodio, ya no ve las letras pequeñas. La escriba arregla el terminal del archivo. Unos saqueadores atacan el pozo. Descifra en un manual roto el nombre de la Bóveda de Valdecruz.",
                anioDeLaOrden(1) + listOf(
                    suceso("jimena", "02-24", "En la patrulla encuentran a una niña sola en una gasolinera vieja: Jimena"),
                    mundo("tormenta_polvo", "04-02", "Una tormenta de polvo de tres días entierra el camino del norte", 3, certeza = I, escena = "polvo", clima = Clima.POLVO),
                    suceso("terminal", "06-05", "Arregla el terminal del archivo, que llevaba diez años apagado"),
                    suceso("ojos_fermin", "07-28", "Fermín, el custodio, le confiesa que ya no ve las letras pequeñas"),
                    mundo("pozo", "09-05", "Unos saqueadores atacan el pozo del pie de la meseta, y los caballeros los echan", certeza = I, escena = "alarma"),
                    suceso("letras", "10-02", "Empieza a enseñarle las letras a Jimena, en el banco de la Cámara"),
                    suceso("valdecruz", "11-28", "Descifra en un manual roto el nombre de un depósito del sur: la Bóveda de Valdecruz"),
                    mundo("caravana", "12-08", "Sube la última caravana del año, con sal, tela y noticias del sur", certeza = I),
                )),
            TemporadaVida("2288", "boveda",
                "Una helada revienta las tuberías. La maestre Amparo manda a la escriba al sur en la Garza, con Ulises, Nieves y el piloto Damián. Siete semanas lejos: entran en la Bóveda de Valdecruz y sacan un núcleo de memoria intacto. Ve una ciudad muerta desde el aire. Empieza con Damián. Jimena lee sola su primera frase. El núcleo se enciende, pero nadie entiende lo que dice.",
                anioDeLaOrden(2) + listOf(
                    mundo("helada", "01-20", "Una helada revienta las tuberías del ala norte", certeza = I, clima = Clima.NIEVE),
                    suceso("elegida", "02-20", "La maestre Amparo la elige para ir a la Bóveda de Valdecruz"),
                    suceso("sale_garza", "04-26", "Sube a la Garza con Ulises, Nieves y cuatro caballeros, rumbo al sur"),
                    suceso("boveda", "05-16", "Entran en la Bóveda: manuales podridos y un núcleo de memoria intacto"),
                    suceso("ciudad_muerta", "05-30", "Desde la Garza ve una ciudad entera sin una sola luz"),
                    suceso("damian", "06-06", "Damián, el piloto, le enseña a leer las estrellas desde la cabina"),
                    suceso("vuelve_garza", "06-14", "La Garza vuelve a Peñaseca con el núcleo envuelto en mantas"),
                    mundo("granizo", "07-18", "Una granizada rompe los cristales del invernadero de la torre", certeza = I, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("primera_frase", "09-28", "Jimena lee sola su primera frase entera, en voz alta"),
                    suceso("nucleo", "11-05", "Logra que el núcleo se encienda: salen letras que nadie entiende"),
                )),
            TemporadaVida("2289", "archivo",
                "Fermín, ya ciego, le da a la escriba las llaves de la Cámara. La maestre la encierra un mes en el archivo con el núcleo, y sale la víspera de la Fundación con la mitad leída. Ulises cuelga el caparazón. Se seca el pozo. Una tormenta tumba a la Garza, y Damián se rompe una pierna. Empiezan a coserle tela nueva. Ella y Damián piden un cuarto para los dos.",
                anioDeLaOrden(3) + listOf(
                    suceso("llaves", "01-25", "Fermín, ciego ya, le da las llaves de la Cámara"),
                    suceso("entra_archivo", "02-15", "La maestre la encierra en el archivo con el núcleo, un catre y la comida contada"),
                    suceso("sale_archivo", "03-14", "Sale del archivo la víspera de la Fundación, con la mitad del núcleo leída"),
                    suceso("ulises", "06-10", "Ulises cuelga el caparazón y se queda a trabajar en la fragua"),
                    mundo("pozo_seco", "06-30", "Se seca el pozo de abajo: el agua de la cisterna se raciona hasta las tormentas", certeza = I, escena = "polvo"),
                    mundo("garza_cae", "07-30", "Una tormenta tumba a la Garza junto a la torre; Damián se rompe una pierna", certeza = I, escena = "tormenta", clima = Clima.TORMENTA),
                    mundo("costura", "10-01", "Empiezan a coser en el patio la tela nueva de la Garza", certeza = I),
                    suceso("cuarto", "11-25", "Damián y ella le piden a la maestre un cuarto para los dos"),
                )),
            TemporadaVida("2290", "emboscada",
                "Tres días de nieve. Jimena jura como candilera. Unos saqueadores emboscan la patrulla en el paso del Cuervo: Nieves carga con la escriba herida hasta la ciudadela, y pasa cinco semanas en la enfermería. La Garza vuela otra vez. La escriba le dice a Damián que espera un hijo. Muere Fermín mientras ella le lee.",
                anioDeLaOrden(4) + listOf(
                    mundo("nevada", "01-15", "Nieva tres días en el páramo, y el patio amanece blanco", 3, certeza = I, clima = Clima.NIEVE),
                    suceso("jimena_jura", "04-20", "Jimena jura como candilera: es la primera de fuera que lo hace en años"),
                    suceso("emboscada", "04-28", "Unos saqueadores emboscan la patrulla en el paso del Cuervo: la hieren en el costado", escena = "alarma"),
                    suceso("sale_enfermeria", "06-02", "Sale de la enfermería del brazo de Nieves y sube sola a la Cámara"),
                    mundo("garza_vuela", "09-10", "La Garza vuela otra vez, con su tela nueva cosida a mano", certeza = I, escena = "fiesta"),
                    suceso("espera", "10-20", "Le dice a Damián que espera un hijo"),
                    suceso("fermin_muere", "11-30", "Muere Fermín, el viejo custodio, mientras ella le lee", escena = "luto"),
                )),
            TemporadaVida("2291", "sequia",
                "Jimena sale a su primera patrulla. Sube una caravana del norte. Nace Ciro, el hijo de la escriba y Damián. Es el año más seco que se recuerda. La escriba lee la última página del núcleo: son los planos de una máquina que saca agua del aire. La maestre Amparo la nombra custodia de la Cámara.",
                anioDeLaOrden(5) + listOf(
                    suceso("jimena_patrulla", "02-20", "Jimena sale a su primera patrulla, con el candil de los que empiezan"),
                    mundo("caravana_norte", "04-12", "Sube una caravana del norte que nunca había llegado a la ciudadela", certeza = I),
                    suceso("ciro", "05-25", "Nace Ciro, su hijo, en la enfermería de la ciudadela", escena = "fiesta"),
                    mundo("sequia", "06-28", "El año más seco que se recuerda: la cisterna casi toca fondo", certeza = I, escena = "polvo", clima = Clima.POLVO),
                    suceso("ultima_pagina", "08-28", "Lee la última página del núcleo: son los planos de una máquina de agua", cadena = "nucleo"),
                    suceso("planos", "09-25", "Le enseña a la maestre los planos: la máquina saca agua del aire", cadena = "nucleo"),
                    suceso("custodia", "11-10", "La maestre Amparo la nombra custodia de la Cámara", cadena = "nucleo"),
                )),
        ),
        arcos = listOf(
            Arco("codice", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(2, "11-05", 0.0), p(3, "02-15", 0.2), p(3, "03-14", 0.5), p(4, "04-28", 0.75),
                p(4, "06-02", 0.75), p(5, "08-28", 1.0), p(5, "12-31", 1.0))),
            Arco("garza", TipoArco.OBRA, listOf(p(1, "01-01", 1.0), p(3, "07-29", 1.0), p(3, "07-30", 0.1, salto = true), p(3, "10-01", 0.1), p(4, "09-10", 1.0), p(5, "12-31", 1.0))),
            // Cada año baja hasta julio y sube con las tormentas; en la temporada 5, la sequía, casi toca fondo y sube poco.
            Arco("cisterna", TipoArco.OTRO, listOf(p(1, "01-01", 0.6)) + (1..5).flatMap { t ->
                if (t == 5) listOf(p(t, "07-11", 0.05), p(t, "08-31", 0.65), p(t, "12-31", 0.6))
                else listOf(p(t, "07-11", 0.2), p(t, "08-31", 0.95), p(t, "12-31", 0.6))
            }),
        ),
        objetos = listOf("pluma", "cuaderno", "lupa", "destornillador", "radio", "cantimplora", "racion", "manual", "candil", "brujula", "soldador", "codigo"),
        estados = listOf(Estado("expedicion", "sale_garza", "vuelve_garza"), Estado("encierro", "entra_archivo", "sale_archivo"), Estado("herida", "emboscada", "sale_enfermeria")),
    )

    // =====================================================================
    //  La doctora, científica del Claustro. Un laboratorio bajo una ciudad en ruinas, de 2287 a 2291.
    // =====================================================================
    //
    // Su mundo (todo inventado, y nada de ninguna obra): el Claustro, un laboratorio sellado bajo una ciudad en ruinas,
    // con unas trescientas personas, pasillos blancos, jardines hidropónicos bajo lámparas que imitan el día y un
    // destello que sube y baja a la gente sin caminar. Lo gobierna el Consejo, con un rector al frente, y se divide en
    // departamentos: Botánica (el suyo y el de los jardines), Medicina, Mentes (donde se hacen los asistentes), la Vigía
    // (las cámaras que miran arriba) y el Destello. Los asistentes se llaman Semejantes: parecen personas, trabajan como
    // personas y no entran en el censo. Arriba, en las ruinas, vive gente a la que el Claustro le ha enseñado a temer.
    // Su investigación: la grama, una hierba que saca el veneno de la tierra de arriba.
    //
    // Su gente (toda inventada): Lauro, el Semejante que le asignan en la temporada 1 y que hace preguntas; se va por el
    // destello en la 4 y ella lo ve de lejos, arriba, en la 5. Tomé, su hijo (14 años en la 1), que entra en Mentes en la
    // 2 y sube con ella en la 5. La doctora Hilda, su maestra, que deja el laboratorio en la 1 y muere en la 3. Coral, la
    // jefa de los jardines, su amiga; su hija Alba nace en la 3. Neva, una mujer de arriba a la que bajan en la 2 y que
    // ella vuelve a encontrar en la 5. Osvaldo, el ingeniero del destello. El rector Ezequiel Mora, que deja el cargo en la
    // 3, y la rectora Selma Ibarra, de la Vigía, que lo sustituye.
    //
    // La historia: la doctora no ha visto el sol desde los veintiún años, cuando subió una sola vez con su promoción. En
    // cinco años su grama pasa de germinar a limpiar la tierra; el Consejo decide guardarla para el Claustro, Lauro se la
    // lleva arriba, y al final ella sube a sembrarla y la encuentra ya crecida en las ruinas.
    //
    // Claves de escena: fiesta, alarma, luto, cosecha, destello (alguien sube o baja), sol (el cielo de arriba). Arcos:
    // jardines (lo que ha crecido en los tanques hidropónicos; se vacían en la temporada 2), investigacion (lo que lleva
    // hecho de la grama) y superficie (lo verde que ven las cámaras de la Vigía en las ruinas de arriba). Estados:
    // «cuarentena» (temporada 2, el Claustro cerrado por una fiebre), «experimento» (temporada 3, tres semanas encerrada
    // en la campana de cultivo), «cierre» (temporada 4, el Claustro cerrado mientras buscan a Lauro) y «arriba»
    // (temporada 5, cuatro semanas en la superficie).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el laboratorio blanco con los jardines según
    // `jardines`, la mesa de la grama según `investigacion` y, en una pantalla de la Vigía, la ciudad en ruinas cada vez
    // más verde según `superficie`; compuertas cerradas, luces de aviso y gente con mascarilla durante «cuarentena»; la
    // campana de vidrio con tierra gris y ella dentro durante «experimento»; pasillos con luz roja y el destello apagado
    // durante «cierre»; y la ciudad en ruinas bajo el cielo de verdad, con matas de grama entre las piedras, durante
    // «arriba».

    /** Lo que vuelve cada año en el Claustro. */
    private fun anioDelClaustro(): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo en el Claustro: todas las lámparas de los jardines se encienden a la vez", certeza = I),
        rito("censo", "02-12", "El censo del año: se cuenta a la gente del Claustro, y a los Semejantes no", certeza = I),
        fiesta("fundacion", "03-18", "El Día de la Fundación: el aniversario del día en que se selló el Claustro", certeza = I),
        rito("revision", "04-20", "La revisión médica del año: sangre, huesos y la vista, que se gasta bajo las lámparas", certeza = I),
        rito("jornadas", "06-02", "Las jornadas: cada departamento cuenta lo que descubrió en el año", 3, certeza = I),
        fiesta("luz_larga", "06-21", "La fiesta de la luz larga: las lámparas de los jardines no se apagan en toda la noche", certeza = I),
        rito("permisos", "07-25", "Se anuncian los permisos de hijos del año", certeza = I),
        rito("revision_destello", "08-18", "La revisión del destello: dos días sin que nadie suba ni baje", 2, certeza = I),
        cosecha("siega", "09-10", "La siega grande de los jardines hidropónicos", 3, certeza = I),
        rito("memoria", "10-15", "El Día de los de afuera: un minuto por los que se quedaron arriba cuando se selló", certeza = I, escena = "luto"),
        rito("consejo", "11-20", "La sesión grande del Consejo: se votan los proyectos del año que viene", certeza = I),
        fiesta("noche_larga", "12-21", "La noche larga: se apagan las luces y se oye la grabación del viento de arriba", certeza = I),
    )

    val CIENTIFICA = Calendario(
        vida = "cientifica",
        estaciones = Calendario.SIN_ESTACIONES,
        clima = SIN_CLIMA,
        luz = RegimenLuz.DOS_ESTADOS,
        temporadas = listOf(
            TemporadaVida("2287", "humo",
                "A la doctora le asignan a Lauro, un Semejante, como asistente. Las cámaras de la Vigía ven humo en las ruinas de arriba. Su grama germina por primera vez en tierra de la superficie. Una noche sin corriente. Lauro le pregunta de qué color es el cielo. Tomé, su hijo, quiere entrar en Mentes. Su maestra, Hilda, deja el laboratorio.",
                anioDelClaustro() + listOf(
                    suceso("lauro", "02-03", "Le asignan un asistente nuevo: un Semejante al que el departamento llama Lauro"),
                    mundo("humo", "04-08", "Las cámaras de la Vigía ven humo en las ruinas de arriba: alguien vive allí", certeza = I),
                    suceso("germina", "05-14", "La grama germina por primera vez en tierra traída de arriba"),
                    mundo("apagon", "07-08", "Se cae la corriente del reactor: una noche entera a oscuras bajo la ciudad", certeza = I, escena = "alarma"),
                    suceso("pregunta", "08-28", "Lauro le pregunta, mientras riega, de qué color es el cielo"),
                    suceso("tome", "10-30", "Tomé, su hijo, le dice que quiere entrar en Mentes, donde se hacen los Semejantes"),
                    suceso("hilda_deja", "12-04", "Su maestra, la doctora Hilda, deja el laboratorio y le regala sus cuadernos"),
                )),
            TemporadaVida("2288", "fiebre",
                "Bajan a Neva, una mujer de arriba, y la doctora la examina. Una fiebre cierra el Claustro casi un mes y el Consejo culpa a Neva. La doctora descubre que salió del agua de los jardines, y se vacían todos los tanques. Devuelven a Neva arriba. Tomé entra en Mentes. A Coral le dan permiso de tener un hijo.",
                anioDelClaustro().filter { it.id != "fundacion" } + listOf(
                    mundo("intrusa", "02-20", "Los vigías encuentran a una mujer de arriba junto a la toma de aire, y la bajan", certeza = I, escena = "destello"),
                    suceso("neva", "02-24", "Le toca examinar a la mujer de arriba: se llama Neva y no le tiene miedo"),
                    mundo("fiebre", "03-08", "Una fiebre en Botánica: el Consejo cierra el Claustro y culpa a la mujer de arriba", certeza = I, escena = "alarma"),
                    rito("fundacion_cerrada", "03-18", "El Día de la Fundación, sin fiesta: cada quien en su ala, con las compuertas cerradas", certeza = I),
                    suceso("agua", "03-27", "Descubre que la fiebre salió del agua de los jardines, no de la mujer de arriba"),
                    mundo("vaciado", "03-28", "Se vacían todos los tanques de los jardines, y se tira la cosecha entera", certeza = I),
                    mundo("reapertura", "04-05", "Se abren otra vez las compuertas: la fiebre se llevó a dos viejos de Botánica", certeza = I),
                    suceso("regalo_neva", "05-11", "Neva le regala una piedra lisa del río de arriba, lo único que traía"),
                    mundo("neva_sube", "05-12", "Devuelven a Neva arriba por el destello, con los ojos vendados", certeza = I, escena = "destello"),
                    suceso("prueba_tome", "07-10", "Tomé pasa la prueba de aptitud y entra en Mentes"),
                    suceso("coral", "07-26", "A Coral, la jefa de los jardines, le dan permiso de tener un hijo"),
                )),
            TemporadaVida("2289", "campana",
                "Muere Hilda, la maestra de la doctora. Lauro pregunta por qué a él no lo cuentan en el censo. Selma Ibarra sustituye al rector. Osvaldo baja tierra de arriba, y la doctora pasa tres semanas encerrada en la campana de cultivo con su grama. Nace Alba, la hija de Coral. El Consejo vota que los Semejantes sigan sin contar.",
                anioDelClaustro() + listOf(
                    suceso("hilda", "01-20", "Muere la doctora Hilda, su maestra, la que le enseñó a mirar raíces", escena = "luto"),
                    suceso("censo_lauro", "02-13", "Lauro le pregunta por qué a él no lo cuentan en el censo"),
                    mundo("rectora", "03-03", "El rector Ezequiel Mora deja el cargo, y el Consejo elige a Selma Ibarra, de la Vigía", certeza = I),
                    suceso("tierra", "04-12", "Osvaldo, el del destello, le baja diez sacos de tierra de arriba"),
                    suceso("entra_campana", "05-03", "Se encierra en la campana de cultivo, con tierra y aire de arriba, a probar la grama"),
                    suceso("sale_campana", "05-24", "Sale de la campana: la grama vivió, y ella también, con tres kilos menos"),
                    suceso("alba", "08-30", "Nace Alba, la hija de Coral, en la enfermería de Medicina", escena = "fiesta"),
                    mundo("voto", "11-21", "El Consejo vota que los Semejantes sigan sin contar en el censo", certeza = I),
                )),
            TemporadaVida("2290", "huida",
                "La grama de la doctora limpia por completo un tanque de tierra de arriba. La presenta en las jornadas. El Consejo decide sembrarla solo sobre el Claustro y no dar la semilla a nadie. Lauro se va por el destello con un saco de semillas, y cierran el Claustro dos semanas para buscarlo. Las cámaras ven verde en las ruinas.",
                anioDelClaustro() + listOf(
                    suceso("funciona", "03-02", "La grama limpia por completo un tanque de tierra de arriba: ya sirve"),
                    suceso("presenta", "06-03", "En las jornadas presenta la grama, y la sala entera se pone de pie"),
                    mundo("decision", "07-04", "El Consejo decide: la grama se sembrará solo sobre el Claustro, y su semilla no saldrá", certeza = I),
                    suceso("pide_rectora", "07-12", "Le pide a la rectora que comparta la semilla con los de arriba, y la rectora dice que no"),
                    suceso("lauro_se_va", "09-27", "Lauro se va de noche por el destello, con un saco de semillas de grama", escena = "destello"),
                    mundo("busqueda", "09-28", "El Consejo cierra el Claustro para buscar al Semejante que se fue", certeza = I, escena = "alarma"),
                    mundo("fin_busqueda", "10-12", "Se levanta el cierre: a Lauro no lo encuentran, y ya no lo buscan", certeza = I),
                    suceso("tome_memoria", "11-06", "A Tomé le mandan revisar la memoria de Lauro, y no le dice a nadie lo que encuentra"),
                    mundo("verde", "12-10", "Las cámaras de la Vigía ven una mancha verde en las ruinas, lejos del Claustro", certeza = I),
                )),
            TemporadaVida("2291", "sol",
                "El Consejo elige a la doctora para subir a sembrar la grama. Sube con Tomé y ve el sol por primera vez en veinticuatro años. En las ruinas encuentra grama ya crecida y a Neva regándola, y ve a Lauro de lejos. No lo pone en el informe. Vuelve a pedir que se comparta la semilla, y el Consejo acepta por un voto.",
                anioDelClaustro() + listOf(
                    suceso("elegida", "03-05", "El Consejo la elige para subir a sembrar la grama sobre el Claustro", cadena = "subida"),
                    suceso("sube", "04-26", "Sube por el destello con Tomé y dos vigías, con la grama en cajas", cadena = "subida", escena = "destello"),
                    suceso("sol", "04-27", "Ve el sol por primera vez en veinticuatro años", cadena = "subida", escena = "sol", clima = Clima.DESPEJADO),
                    suceso("encuentra", "05-08", "Encuentra grama crecida en un patio de las ruinas, y a Neva regándola", cadena = "subida", escena = "sol"),
                    suceso("lluvia", "05-13", "Le llueve encima por primera vez desde que era joven", cadena = "subida", clima = Clima.LLUVIA),
                    suceso("lauro_lejos", "05-17", "Ve a Lauro de lejos, con la gente de Neva, y no lo pone en el informe", cadena = "subida"),
                    suceso("baja", "05-24", "Baja al Claustro con la cara quemada de sol y tierra en las uñas", cadena = "subida", escena = "destello"),
                    suceso("pide", "08-25", "Vuelve a pedir al Consejo que la semilla se comparta con la gente de arriba", cadena = "semilla"),
                    suceso("tome_arriba", "10-10", "Tomé le dice que quiere volver arriba, a vivir con la gente de Neva", cadena = "semilla"),
                    mundo("acuerdo", "12-05", "El Consejo acuerda, por un voto, dar semilla de grama a la gente de arriba", certeza = I),
                )),
        ),
        arcos = listOf(
            Arco("jardines", TipoArco.OTRO, (1..5).flatMap { n ->
                if (n == 2) listOf(p(2, "01-01", 0.55), p(2, "03-27", 0.68), p(2, "03-28", 0.05, salto = true), p(2, "09-09", 0.75), p(2, "09-10", 0.1, salto = true), p(2, "12-31", 0.54))
                else listOf(p(n, "01-01", 0.55), p(n, "09-09", 1.0), p(n, "09-10", 0.1, salto = true), p(n, "12-31", 0.54))
            }),
            Arco("investigacion", TipoArco.OBRA, listOf(p(1, "01-01", 0.15), p(1, "05-14", 0.25), p(2, "03-08", 0.4), p(2, "04-05", 0.4), p(3, "05-03", 0.55),
                p(3, "05-24", 0.7), p(4, "03-02", 0.9), p(5, "04-26", 0.92), p(5, "05-24", 1.0), p(5, "12-31", 1.0))),
            Arco("superficie", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(4, "12-09", 0.0), p(4, "12-31", 0.1), p(5, "04-26", 0.2), p(5, "05-24", 0.35), p(5, "12-31", 0.5))),
        ),
        objetos = listOf("bata", "tableta", "pipeta", "microscopio", "semilla", "maceta", "guantes", "tarjeta", "auriculares", "cuaderno", "piedra", "taza"),
        estados = listOf(
            Estado("cuarentena", "fiebre", "reapertura"), Estado("experimento", "entra_campana", "sale_campana"),
            Estado("cierre", "busqueda", "fin_busqueda"), Estado("arriba", "sube", "baja"),
        ),
    )

    // =====================================================================
    //  Ruth, colona. Un pueblo que se reconstruye en un llano arruinado, de 2288 a 2292.
    // =====================================================================
    //
    // Su gente (toda inventada): Matías, su compañero, el que mantiene vivo el generador; sale herido de un brazo en el
    // asedio (temporada 3). Nora, su hija (nueve años en la temporada 1), que aprende a leer con las revistas viejas de la
    // abuela, hace su primera guardia en la 4 y habla por la radio en la 5. Elías, su hijo, que nace en la 3 y camina en la
    // 4. Agustina, su madre, la que le enseñó a leer, que muere después de la ventisca (temporada 4). Gabino, su hermano
    // menor, que va con ella a ayudar a otros pueblos y en la 4 se queda en Cuatro Molinos; vuelve de visita en la 5 con
    // su mujer y una hija. Marta, la que lleva el pueblo, que nombra a Ruth jefa de las guardias de noche en la 2.
    // Esteban, guardia del portón, que muere en el asedio. Joaquín y Lidia, que llegan del sur con dos hijos en la 2.
    // Celia, la voz de la radio de Los Silos, a la que Ruth solo oye hasta que la conoce en la 5.
    //
    // Su mundo: unas veinte personas en El Remanso, un pueblo junto a un arroyo del llano, con muros de tablas y láminas.
    // Siembran maíz y calabazas, purifican el agua con arena y carbón, montan guardia. Los pueblos del llano tienen un
    // pacto: cuando uno pide ayuda por la radio, los otros mandan a alguien. Los vecinos (Piedra Blanca, Cuatro Molinos,
    // Los Silos, donde está la radio que pone canciones de hace dos siglos), sus fiestas y sus saqueadores son de esta
    // historia, sin nombres, lugares ni sucesos de ninguna obra. De noche solo hay luz donde llega el generador.
    //
    // Claves de escena: fiesta, cosecha, luto, humo (los saqueadores y el muro que arde), nieve, tormenta y camino (sale
    // del pueblo). Arcos: muro (de tablas y láminas al principio; arde en el asedio y se rehace con piedra hasta la
    // temporada 5), maiz (lo que ha crecido en la milpa; el granizo de la temporada 2 tumba la mitad) y torre (la torre
    // del agua, que se levanta en el otoño de la temporada 2). Estados: «auxilio» (temporada 1, a Piedra Blanca después
    // del tornado; temporada 4, a Cuatro Molinos; temporada 5, a Los Silos), «asedio» (temporada 3, nueve días rodeados
    // por los saqueadores) y «encierro» (temporada 4, más de tres semanas tapados por la ventisca).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el pueblo dentro de su muro según `muro`
    // (tablas y láminas al principio, un hueco quemado en el lado este después del asedio y piedra que crece hasta
    // cerrarlo), la milpa con maíz y calabazas según `maiz`, la torre del agua según `torre` y la luz del generador de
    // noche; durante «auxilio», otro pueblo lejos de casa (techos por levantar, cercas, los silos con la antena); durante
    // «asedio», el portón cerrado, guardias en el muro y humo afuera; y durante «encierro», la nieve hasta las ventanas y
    // solo la luz del generador.

    private fun anioDelRemanso(): List<DiaEspecial> = listOf(
        fiesta("anio_nuevo", "01-01", "Año nuevo: el generador da luz toda la noche y la radio cuenta hacia atrás", certeza = I),
        rito("filtros", "02-10", "Se cambian la arena y el carbón de los filtros del agua", certeza = I),
        rito("llamada", "03-08", "La llamada de los pueblos: cada pueblo dice por la radio cuántos son", certeza = I),
        fiesta("fundacion", "04-12", "El día de la fundación: cada uno clava una tabla nueva en el muro", certeza = I),
        cosecha("siembra", "05-03", "Se siembra el maíz y, entre las matas, las calabazas", 3, certeza = I),
        fiesta("canciones", "06-21", "La noche de las canciones: la radio pone toda la noche música de hace dos siglos", certeza = I),
        rito("chatarra", "07-20", "Salida a la carretera vieja a buscar clavos, láminas y chatarra", 2, certeza = I, escena = "camino"),
        fiesta("elotes", "08-15", "La fiesta de los elotes: el primer maíz tierno, asado en la fogata", certeza = I),
        cosecha("cosecha", "09-15", "La cosecha del maíz y de las calabazas", 5, certeza = I),
        rito("trojes", "10-08", "Se guarda el maíz seco en las trojes y se cuentan los costales", certeza = I),
        rito("nombres", "11-02", "El día de los nombres: se leen junto al muro los de los que no llegaron", certeza = I, escena = "luto"),
        rito("invierno", "12-01", "Se tapan las rendijas del muro y se junta leña para el invierno", certeza = I),
        fiesta("noche_larga", "12-21", "La noche más larga: fogata grande y una lata de dulce para cada niño", certeza = I),
    )

    val RUTH = Calendario(
        vida = "colona",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 9, nubes = 10, nieve = 7, viento = 4, niebla = 1), mes(despejado = 9, nubes = 9, nieve = 6, viento = 4),
            mes(despejado = 9, nubes = 10, lluvia = 4, nieve = 3, viento = 5), mes(despejado = 10, nubes = 9, lluvia = 6, tormenta = 2, viento = 3),
            mes(despejado = 11, nubes = 8, lluvia = 6, tormenta = 5, viento = 1), mes(despejado = 13, nubes = 6, lluvia = 4, tormenta = 6, viento = 1),
            mes(despejado = 16, nubes = 5, lluvia = 3, tormenta = 5, polvo = 2), mes(despejado = 16, nubes = 5, lluvia = 3, tormenta = 4, polvo = 3),
            mes(despejado = 15, nubes = 7, lluvia = 4, tormenta = 2, viento = 2), mes(despejado = 13, nubes = 9, lluvia = 4, viento = 3, niebla = 2),
            mes(despejado = 9, nubes = 11, lluvia = 3, nieve = 3, viento = 3, niebla = 1), mes(despejado = 8, nubes = 11, nieve = 7, viento = 3, niebla = 1),
        ),
        temporadas = listOf(
            TemporadaVida("2288", "muros",
                "Una jauría ronda el muro. Ruth encuentra una caja de cereal de antes de la guerra, todavía dulce. Un tornado tumba los techos de Piedra Blanca, y Ruth va con su hermano Gabino a levantarlos. Nora, su hija, lee sola por primera vez. Se cierra el muro del lado este.",
                anioDelRemanso() + listOf(
                    mundo("jauria", "01-20", "Una jauría de perros salvajes ronda el muro tres noches seguidas", 3, certeza = I, escena = "nieve"),
                    suceso("cereal", "02-25", "Encuentra en la gasolinera vieja una caja de cereal de antes de la guerra, todavía dulce"),
                    mundo("tornado", "05-28", "Un tornado cruza el llano por el norte, y la radio dice que tocó Piedra Blanca", certeza = I, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("sale_piedra", "06-01", "Sale con Gabino y dos más hacia Piedra Blanca, a levantar los techos caídos", escena = "camino"),
                    suceso("vuelve_piedra", "06-16", "Vuelve de Piedra Blanca con un saco de sal en pago"),
                    suceso("nora_lee", "10-20", "Nora lee sola, por primera vez, una página de las revistas de la abuela"),
                    mundo("muro_este", "11-15", "Se cierra el muro por el lado este: ya no queda ningún hueco", certeza = I),
                )),
            TemporadaVida("2289", "forasteros",
                "Se ve humo de saqueadores en la carretera vieja. Llegan Joaquín y Lidia con sus dos hijos, y Ruth les abre el portón. Marta nombra a Ruth jefa de las guardias de noche. El granizo tumba la mitad del maíz. Ruth le dice a Matías que espera un hijo. Se levanta la torre del agua.",
                anioDelRemanso() + listOf(
                    mundo("humo_lejos", "03-25", "Se ve humo de saqueadores en la carretera vieja, y se doblan las guardias", certeza = I, escena = "humo"),
                    suceso("forasteros", "04-28", "Llegan Joaquín y Lidia con sus dos hijos, a pie desde el sur, y ella les abre el portón"),
                    suceso("jefa_guardia", "05-20", "Marta la nombra jefa de las guardias de noche"),
                    mundo("granizo", "07-08", "Una granizada tumba la mitad del maíz en media hora", certeza = I, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("espera", "07-30", "Le dice a Matías que espera un hijo"),
                    mundo("torre_agua", "10-25", "Se termina la torre del agua: un tanque viejo sobre cuatro postes", certeza = I),
                    suceso("jabon", "12-10", "Lidia le enseña a hacer jabón con ceniza y grasa"),
                )),
            TemporadaVida("2290", "asedio",
                "Nace Elías, el hijo de Ruth. Tres extraños miran el pueblo desde la loma. Los saqueadores lo rodean nueve días y queman el muro del este; muere Esteban en el portón y Matías sale herido. La gente de Piedra Blanca oye la radio y viene. Ruth propone rehacer el muro con piedra del arroyo.",
                anioDelRemanso() + listOf(
                    suceso("nace_elias", "03-12", "Nace Elías, su hijo, en la casa de las láminas azules", escena = "fiesta"),
                    mundo("extranos", "07-05", "Tres extraños miran el pueblo desde la loma y se van sin bajar", certeza = I),
                    mundo("asedio", "07-28", "Llegan los saqueadores: rodean el pueblo y nadie puede salir", certeza = I, escena = "humo"),
                    mundo("muro_arde", "08-02", "Los saqueadores prenden fuego al muro del lado este", certeza = I, escena = "humo"),
                    suceso("esteban", "08-03", "Muere Esteban en la guardia del portón, y a Matías lo hieren en un brazo", escena = "luto"),
                    mundo("fin_asedio", "08-06", "Llega la gente de Piedra Blanca, que oyó la radio, y los saqueadores se van", certeza = I),
                    suceso("piedra", "10-15", "Propone rehacer el muro con piedra del arroyo, y el pueblo dice que sí"),
                )),
            TemporadaVida("2291", "ventisca",
                "Una ventisca encierra al pueblo más de tres semanas. Muere Agustina, la madre de Ruth. Elías da sus primeros pasos. La radio dice que en Cuatro Molinos murió media gente en el invierno; Ruth va con Gabino a ayudar y vuelve sola, porque él se queda. Nora hace su primera guardia.",
                anioDelRemanso() + listOf(
                    mundo("ventisca", "01-14", "Empieza una ventisca que tapa los caminos: nadie sale del pueblo", 4, certeza = I, escena = "nieve", clima = Clima.NIEVE),
                    mundo("abren_camino", "02-06", "Se abre por fin un camino en la nieve hasta el arroyo", certeza = I, escena = "nieve"),
                    suceso("muere_madre", "02-20", "Muere su madre, Agustina, la que le enseñó a leer con revistas viejas", escena = "luto"),
                    suceso("elias_camina", "03-28", "Elías da sus primeros pasos, agarrado a las piedras nuevas del muro"),
                    mundo("molinos", "06-18", "La radio dice que en Cuatro Molinos murió la mitad de la gente en el invierno", certeza = I),
                    suceso("sale_molinos", "06-25", "Sale con Gabino hacia Cuatro Molinos, a levantar cercas y sembrar frijol", escena = "camino"),
                    suceso("vuelve_molinos", "07-12", "Vuelve de Cuatro Molinos sin Gabino, que se queda: allí hace falta gente"),
                    suceso("nora_guardia", "10-30", "Nora, de doce años, hace su primera guardia en el muro con ella"),
                )),
            TemporadaVida("2292", "piedra",
                "Llegan tres familias de Cuatro Molinos y el pueblo pasa de treinta. Ruth va con Nora a Los Silos a llevar semilla de maíz y conoce en persona a Celia, la voz de la radio. Se pone la última piedra del muro. Nora habla por la radio. Gabino vuelve de visita con su mujer y una hija.",
                anioDelRemanso() + listOf(
                    mundo("llegan_nuevos", "04-05", "Llegan tres familias de Cuatro Molinos: ya son más de treinta", certeza = I),
                    suceso("sale_semillas", "05-30", "Sale con Nora hacia Los Silos, a llevar semilla de maíz a un pueblo nuevo", cadena = "semillas", escena = "camino"),
                    suceso("conoce_celia", "06-05", "Conoce en persona a Celia, la voz de la radio de Los Silos", cadena = "semillas"),
                    suceso("vuelve_semillas", "06-12", "Vuelve a casa con Nora y un disco de canciones viejas para la radio", cadena = "semillas"),
                    mundo("muro_listo", "08-25", "Se pone la última piedra del muro: el pueblo entero queda dentro", certeza = I, escena = "fiesta"),
                    suceso("nora_radio", "09-28", "Nora habla por la radio por primera vez, y Celia la reconoce", cadena = "otono"),
                    suceso("gabino_visita", "10-25", "Gabino vuelve de visita con su mujer y una niña de meses", cadena = "otono"),
                )),
        ),
        arcos = listOf(
            Arco("muro", TipoArco.OBRA, listOf(
                p(1, "01-01", 0.35), p(1, "11-15", 0.5), p(3, "08-01", 0.62), p(3, "08-02", 0.3, salto = true),
                p(4, "04-12", 0.45), p(5, "08-25", 1.0), p(5, "12-31", 1.0),
            )),
            Arco("maiz", TipoArco.OTRO, (1..5).flatMap { t ->
                if (t == 2) listOf(
                    p(2, "01-01", 0.0), p(2, "05-05", 0.0), p(2, "07-07", 0.6), p(2, "07-08", 0.3, salto = true),
                    p(2, "09-14", 0.6), p(2, "09-15", 0.1, salto = true), p(2, "10-20", 0.0),
                ) else listOf(p(t, "01-01", 0.0), p(t, "05-05", 0.0), p(t, "09-14", 1.0), p(t, "09-15", 0.1, salto = true), p(t, "10-20", 0.0))
            }),
            Arco("torre", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(2, "08-01", 0.0), p(2, "10-25", 1.0), p(5, "12-31", 1.0))),
        ),
        objetos = listOf("azada", "escopeta", "radio", "lata", "garrafa", "martillo", "farol", "mazorca", "revista", "cantimplora", "manta", "filtro"),
        estados = listOf(
            Estado("auxilio", "sale_piedra", "vuelve_piedra"), Estado("auxilio", "sale_molinos", "vuelve_molinos"),
            Estado("auxilio", "sale_semillas", "vuelve_semillas"),
            Estado("asedio", "asedio", "fin_asedio"), Estado("encierro", "ventisca", "abren_camino"),
        ),
    )

    // =====================================================================
    //  Brenna, guía de caminos. Una posada al pie de un paso de montaña, años 312 a 316 de los Caminos.
    // =====================================================================
    //
    // Su gente (toda inventada): Hilde, la posadera de la posada del Cuerno, viuda y mayor, que le cuida a la hija en la
    // temporada 5. Tobin, un arpista del valle que llega en la 1 y se queda; vive con ella desde la 4 y en la 5 nace su
    // hija. El viejo Arn, el guía que le enseñó el paso, que no vuelve de una travesía en la niebla en la 2 (en la 3 el
    // deshielo devuelve su linterna, y nada más). Neli, la hija del herrero: le pide que le enseñe en la 2, entra de
    // aprendiz en la 3, guía su primer tramo en la 4 y su primera compañía sola en la 5. Orla, la maestra del Gremio,
    // que guarda el libro de crónicas hasta que se lo pasa a ella en la 5. Corvin, el capataz de los mineros, que abre la
    // veta nueva en la 1 y la tapa en la 4; Bastián, su hermano, que muere en el derrumbe.
    //
    // Su mundo, todo inventado y de ninguna obra: el Gremio de los Caminos, que cuenta los años desde que se grabó la
    // Primera Runa en la piedra más vieja del camino; las runas que marcan cada piedra del paso; los refugios de la Piedra
    // Partida y de la Cabra; las vetas de hierro de la ladera; y del otro lado, Torremuda, una ciudad en ruinas adonde
    // van los peregrinos con la Compañía de Verano. Sus fiestas: la Noche de las Linternas, la feria del deshielo, la
    // Primera Runa, la fiesta del Hierro de los mineros y la Noche del Arpa. Cada invierno la nieve cierra el paso unos
    // tres meses y medio; el día en que se cierra y el día en que se abre cambian de un año a otro.
    //
    // Claves de escena: fiesta, linternas, nieve, niebla, tormenta, luto, derrumbe. Arcos: nieve (la nieve del collado,
    // cada año), refugio (el refugio de piedra que se levanta en el collado de la temporada 3 a la 4, donde se perdió Arn)
    // y veta (la veta nueva: se abre en la 1, da todo su hierro en la 2 y se derrumba de golpe en la 4). Estados:
    // «paso_cerrado» (cada invierno, del cierre del paso a su apertura, en la posada), «travesia» (temporada 3, un mes
    // al frente de la Compañía de Verano, hasta Torremuda y de vuelta) y «crianza» (temporada 5, las semanas después de
    // que nace su hija, en la posada).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la posada del Cuerno al pie del paso, con el
    // camino de piedras marcadas que sube al collado; la nieve del collado según `nieve`; el refugio de piedra a medio
    // hacer y terminado según `refugio`; la boca de la veta nueva en la ladera, con luces y humo según `veta` y tapada
    // después del derrumbe. Durante «paso_cerrado», el camino borrado por la nieve, nadie en él y humo en la chimenea de
    // la posada. Durante «travesia», otro sitio: las torres caídas de Torremuda y su puerta con una runa grabada. Durante
    // «crianza», la ventana del cuarto alto de la posada encendida y una cuna junto a la puerta.

    /** El día en que se abre y el día en que se cierra el paso, de la temporada 1 a la 5: cambian con la nieve de cada año. */
    private val BRENNA_ABRE = listOf("03-22", "03-16", "03-28", "03-19", "03-25")
    private val BRENNA_CIERRA = listOf("12-06", "11-29", "12-11", "12-03", "12-08")

    /** Lo que vuelve cada año en el paso. En la temporada 3 ella misma lleva la Compañía de Verano; en la 4 no hay fiesta del Hierro. */
    private fun anioDelPaso(t: Int): List<DiaEspecial> = listOfNotNull(
        fiesta("anio_nuevo", "01-01", "Año nuevo de los Caminos: se graba la runa del año en la piedra de la posada", certeza = I),
        fiesta("linternas", "02-03", "La Noche de las Linternas: una luz en cada ventana por los que se quedaron en el paso", certeza = I, escena = "linternas"),
        mundo("abre_paso", BRENNA_ABRE[t - 1], "Se abre el paso: la primera compañía del año sube por la nieve vieja", certeza = I, escena = "nieve"),
        rito("bastones", "04-21", "Los guías repasan las runas de sus bastones en la piedra del Gremio", certeza = I),
        fiesta("feria", "05-14", "La feria del deshielo: suben los mercaderes del valle con sal, telas y vino", 2, certeza = I),
        fiesta("primera_runa", "06-21", "La fiesta de la Primera Runa: los guías suben a repasar la piedra más vieja del camino", certeza = I),
        if (t == 3) null else rito("compania_verano", "07-20", "Sale la Compañía de Verano: los peregrinos van a las ruinas de Torremuda", certeza = I),
        if (t == 4) rito("hierro_luto", "08-24", "No hay fiesta del Hierro: se encienden lámparas en la boca de la veta tapada", certeza = I, escena = "luto")
        else fiesta("hierro", "08-24", "La fiesta del Hierro: los mineros bajan de las vetas y pagan la cerveza oscura", certeza = I),
        cosecha("centeno", "09-06", "Se siega el centeno de la aldea, y los guías también siegan", 3, certeza = I),
        rito("recuento", "10-21", "El recuento del Gremio: se leen en voz alta las crónicas del año", certeza = I),
        rito("refugios", "11-10", "Se llenan los refugios del paso de leña y cecina para el invierno", 2, certeza = I),
        mundo("cierra_paso", BRENNA_CIERRA[t - 1], "Se cierra el paso: la nieve tapa las runas del collado", certeza = I, escena = "nieve", clima = Clima.NIEVE),
        fiesta("noche_arpa", "12-21", "La Noche del Arpa: se toca en la posada hasta que amanece", certeza = I),
        fiesta("fin_anio", "12-31", "La última noche del año: cada guía nombra en voz alta los caminos que hizo", certeza = I),
    )

    val BRENNA = Calendario(
        vida = "guia_caminos",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 5, nubes = 8, nieve = 12, niebla = 3, viento = 3), mes(despejado = 5, nubes = 8, nieve = 11, niebla = 2, viento = 3),
            mes(despejado = 6, nubes = 9, lluvia = 2, nieve = 9, niebla = 3, viento = 3), mes(despejado = 8, nubes = 9, lluvia = 6, nieve = 4, niebla = 3),
            mes(despejado = 10, nubes = 9, lluvia = 7, tormenta = 1, nieve = 1, niebla = 3), mes(despejado = 13, nubes = 8, lluvia = 5, tormenta = 3, niebla = 2),
            mes(despejado = 14, nubes = 7, lluvia = 4, tormenta = 5, niebla = 1), mes(despejado = 13, nubes = 7, lluvia = 4, tormenta = 5, niebla = 2),
            mes(despejado = 11, nubes = 8, lluvia = 5, tormenta = 1, niebla = 4, viento = 1), mes(despejado = 7, nubes = 9, lluvia = 6, nieve = 2, niebla = 6, viento = 1),
            mes(despejado = 5, nubes = 9, lluvia = 4, nieve = 6, niebla = 4, viento = 2), mes(despejado = 4, nubes = 8, nieve = 12, niebla = 3, viento = 4),
        ),
        temporadas = listOf(
            TemporadaVida("año 312 de los Caminos", "veta",
                "Brenna empieza a dibujar el mapa del paso entero en el libro de crónicas. Los mineros de Corvin abren una veta nueva, y ella les lleva la primera compañía. Llega Tobin, un arpista del valle, y se queda. Una tormenta la encierra dos noches en un refugio. El viejo Arn le enseña una runa que ya nadie usa.",
                anioDelPaso(1) + listOf(
                    suceso("mapa", "02-20", "Empieza a dibujar en el libro de crónicas el mapa del paso entero"),
                    mundo("veta_nueva", "04-08", "Los mineros de Corvin abren una veta nueva de hierro, más arriba que todas", certeza = I),
                    suceso("guia_mineros", "04-15", "Guía a la primera compañía de mineros hasta la boca de la veta nueva"),
                    suceso("tobin", "06-02", "Llega a la posada un arpista del valle, Tobin, y se queda"),
                    mundo("tormenta", "07-08", "Una tormenta encierra a su compañía dos noches en el refugio de la Piedra Partida", 2, certeza = I, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("runa_vieja", "10-02", "El viejo Arn le enseña una runa que ya no usa nadie: «camino que sigue»"),
                )),
            TemporadaVida("año 313 de los Caminos", "niebla",
                "Un alud se lleva el refugio de la Cabra. Tobin toca en la posada una canción hecha para Brenna. La veta nueva da más hierro que ninguna. Neli, la hija del herrero, le pide que le enseñe el camino. El viejo Arn sale en la niebla de otoño y no vuelve; lo buscan tres días.",
                anioDelPaso(2) + listOf(
                    mundo("alud", "02-25", "Un alud baja por la cara norte y se lleva el refugio de la Cabra, vacío", certeza = I, escena = "nieve", clima = Clima.NIEVE),
                    suceso("cancion", "05-03", "Tobin toca en la posada una canción hecha para ella, delante de todos"),
                    mundo("carros", "06-10", "Bajan de la veta nueva más carros de hierro que nunca: la aldea se llena de mineros", certeza = I),
                    suceso("neli", "08-05", "Neli, la hija del herrero, de trece años, le pide que le enseñe el camino"),
                    suceso("arn_sale", "10-08", "El viejo Arn sale con una compañía de peregrinos hacia el otro lado"),
                    mundo("niebla", "10-10", "Tres días de niebla cerrada en el collado: no se ven ni las runas", 3, certeza = I, escena = "niebla", clima = Clima.NIEBLA),
                    suceso("arn_falta", "10-13", "La compañía de Arn llega al otro lado sin él: volvió atrás por un rezagado", escena = "niebla"),
                    suceso("busqueda", "10-14", "Lo busca tres días con los guías por todo el collado, a gritos y con cuernos", 3, escena = "niebla"),
                    suceso("nombre_arn", "10-22", "Orla escribe el nombre de Arn en el libro con la runa de los que no volvieron", escena = "luto"),
                )),
            TemporadaVida("año 314 de los Caminos", "ruinas",
                "El deshielo devuelve la linterna de Arn. Neli entra de aprendiz y Brenna le da su primer bastón. El Gremio empieza un refugio de piedra en el collado. Brenna lleva la Compañía de Verano hasta Torremuda y encuentra allí la runa de Arn. Tobin le pide que vivan juntos.",
                anioDelPaso(3) + listOf(
                    mundo("pozo", "01-24", "Se hiela el pozo de la aldea hasta el fondo: se derrite nieve en la cocina", certeza = I, escena = "nieve", clima = Clima.NIEVE),
                    suceso("linterna_arn", "04-02", "En la nieve que se derrite aparece la linterna de Arn, y nada más", escena = "luto"),
                    suceso("neli_baston", "04-22", "Neli entra de aprendiz en el Gremio, y ella le da su primer bastón"),
                    mundo("refugio_empieza", "05-26", "El Gremio empieza a levantar un refugio de piedra en el collado donde se perdió Arn", certeza = I),
                    suceso("sale_ruinas", "07-20", "Sale al frente de la Compañía de Verano: lleva a los peregrinos hasta Torremuda"),
                    suceso("torremuda", "07-30", "Llega a Torremuda, la ciudad de las torres caídas, que nunca había visto"),
                    suceso("runa_puerta", "08-03", "En la puerta de Torremuda encuentra grabada la runa de Arn: «camino que sigue»"),
                    suceso("vuelve_ruinas", "08-19", "Vuelve a la posada con la compañía, y con la runa copiada en el libro"),
                    suceso("tobin_pide", "11-02", "Tobin le pide que vivan juntos en el cuarto alto de la posada"),
                )),
            TemporadaVida("año 315 de los Caminos", "derrumbe",
                "Brenna se muda con Tobin al cuarto alto. Neli guía sola su primer tramo. Se derrumba la veta nueva con cuatro mineros dentro; sacan a tres, y Bastián, el hermano de Corvin, muere. Corvin tapa la veta. Brenna le dice a Tobin que espera un hijo. Se termina el refugio del collado y le ponen el nombre de Arn.",
                anioDelPaso(4) + listOf(
                    suceso("cuarto_alto", "01-12", "Se muda con Tobin al cuarto alto de la posada, con el paso cerrado"),
                    suceso("neli_tramo", "05-20", "Neli guía sola su primer tramo, del puente a la Piedra Partida"),
                    mundo("derrumbe", "06-14", "Se derrumba la veta nueva con cuatro mineros dentro: tres días cavando", 3, certeza = I, escena = "derrumbe"),
                    suceso("rescate", "06-15", "Lleva de noche a los de rescate por la cara norte, con su linterna de mina", escena = "derrumbe"),
                    suceso("bastian", "06-17", "Sacan vivos a tres mineros; a Bastián, el hermano de Corvin, no", escena = "luto"),
                    mundo("veta_tapada", "06-30", "Corvin tapa con piedras la boca de la veta nueva", certeza = I),
                    suceso("espera", "09-12", "Le dice a Tobin que espera un hijo"),
                    mundo("refugio_listo", "09-28", "Se termina el refugio de piedra del collado, y le ponen el nombre de Arn", certeza = I, escena = "fiesta"),
                )),
            TemporadaVida("año 316 de los Caminos", "relevo",
                "Nace la hija de Brenna y de Tobin en el cuarto alto. Corvin se va al valle con las familias de la veta tapada. Brenna vuelve a guiar y Hilde le cuida a la niña. Un rayo parte el pino de la posada. Neli lleva sola su primera compañía. Orla le da a guardar el libro de crónicas.",
                anioDelPaso(5) + listOf(
                    suceso("nace", "04-12", "Nace su hija en el cuarto alto de la posada, mientras Tobin toca el arpa abajo", cadena = "hija", escena = "fiesta"),
                    suceso("vuelve_camino", "05-20", "Vuelve a guiar, y Hilde se queda con la niña en la cocina de la posada", cadena = "hija"),
                    mundo("se_van", "06-05", "Corvin se va al valle con las familias de los mineros de la veta tapada", certeza = I),
                    mundo("pino", "07-10", "Un rayo parte en dos el pino viejo de la posada", certeza = I, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("neli_sola", "08-21", "Neli lleva sola su primera compañía de un lado al otro del paso", cadena = "relevo"),
                    mundo("escalera", "09-15", "Llegan noticias del otro lado: en Torremuda han encontrado una escalera hacia abajo", certeza = I),
                    suceso("cronista", "10-28", "La maestra Orla, que ya ve poco, le da a guardar el libro de crónicas del Gremio", cadena = "relevo"),
                )),
        ),
        arcos = listOf(
            Arco.cadaAnio("nieve", TipoArco.OTRO, "01-01" to 0.9, "02-15" to 1.0, "04-01" to 0.7, "05-31" to 0.15, "07-15" to 0.0, "09-30" to 0.0, "11-15" to 0.5, "12-31" to 0.9),
            Arco("refugio", TipoArco.OBRA, listOf(p(1, "01-01", 0.0), p(3, "05-26", 0.0), p(3, "12-11", 0.4), p(4, "03-19", 0.4), p(4, "09-28", 1.0), p(5, "12-31", 1.0))),
            Arco("veta", TipoArco.OTRO, listOf(p(1, "01-01", 0.0), p(1, "04-07", 0.0), p(1, "04-08", 0.3, salto = true), p(2, "06-10", 1.0), p(4, "06-13", 1.0), p(4, "06-14", 0.0, salto = true), p(5, "12-31", 0.0))),
        ),
        objetos = listOf("baston", "linterna", "cuerno", "libro", "mapa", "cuerda", "cecina", "botas", "capa", "jarra", "pedernal", "montura"),
        estados = listOf(
            Estado("travesia", "sale_ruinas", "vuelve_ruinas"), Estado("crianza", "nace", "vuelve_camino"),
            Estado("paso_cerrado", "cierra_paso", "fin_anio"), Estado("paso_cerrado", "fin_anio", null), Estado("paso_cerrado", "anio_nuevo", "abre_paso"),
        ),
    )
}
