package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Arco
import io.github.ruddy2003.vestigium.vidas.Calendario
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.DiaEspecial
import io.github.ruddy2003.vestigium.vidas.Estado
import io.github.ruddy2003.vestigium.vidas.TemporadaVida
import io.github.ruddy2003.vestigium.vidas.TipoArco

/*
 * Los calendarios del grupo 4 (tanda A4): las cinco temporadas (2027 a 2031) del hermano Odo (Scriptorium), el hermano
 * Bernat (Templario), Zaynab (Al-Ándalus), Bat (Mongol) e Iacopo (Renacimiento). Siguen las reglas de `AnioPiloto.kt`.
 * Qué es real y qué inventado, con cada fuente, está en `docs/historia/fuentes.md`.
 *
 * Como los del grupo 3, estos personajes tienen cuadro (tandas S2 y S3), y el comentario de cada calendario dice qué
 * tiene que mostrar para contar lo mismo que el cuento.
 *
 * Lo propio de este grupo:
 *
 * - **Odo, Bernat e Iacopo** siguen el año de la Iglesia en el calendario juliano, como Pierre y Alice. Lo que cuelga de
 *   la Pascua (la cuaresma, la Pascua, Pentecostés y el carnaval) está calculado. Ver [CALCULADOS].
 * - **Zaynab** sigue el calendario islámico, que es lunar: sus fiestas caen cada año unos once días antes. Las fechas
 *   salen del calendario tabular y la luna vista puede moverlas uno o dos días, así que van como «aproximado». Su
 *   Ramadán es un estado que vuelve cada año, como el verano de Ama.
 * - **Bat** sigue el año lunar de la estepa: su año nuevo (el mes blanco) se calculó como el de Nanai-vandak, con la
 *   luna nueva y el sol a la hora de Mongolia. Sus años llevan el animal del ciclo de doce.
 */
internal object AnioG4 {
    val TODOS: List<Calendario> by lazy { listOf(ODO, BERNAT, ZAYNAB, BAT, IACOPO) }

    /**
     * Días «históricos» sin enlace porque su fecha sale de un cálculo hecho en la sesión: el miércoles de ceniza y
     * Pentecostés de cada año, con la Pascua juliana (la Pascua y el carnaval ya estaban en [AnioPiloto.CALCULADOS]).
     */
    val CALCULADOS = setOf("cuaresma", "pentecostes")

    private val W = "https://en.wikipedia.org/wiki/"

    // Las fiestas fijas de la Iglesia, que comparten Odo, Bernat e Iacopo.
    private val F_CANDELARIA = "La Candelaria" to "${W}Candlemas"
    private val F_ANUNCIACION = "La Anunciación" to "${W}Annunciation"
    private val F_ASUNCION = "La Asunción" to "${W}Assumption_of_Mary"
    private val F_TODOS_SANTOS = "Todos los Santos" to "${W}All_Saints%27_Day"
    private val F_NAVIDAD = "La Navidad" to "${W}Christmas"

    // =====================================================================
    //  El hermano Odo, monje copista. Una abadía benedictina junto al Rin, en la diócesis de Maguncia, de 1150 a 1154.
    // =====================================================================
    //
    // Su gente (toda inventada): el abad Wolfram. El hermano Gerhard, el bibliotecario (el armarius), viejo, que le
    // enseñó a escribir y muere en la temporada 3. Heinrich, un niño que sus padres entregan a la abadía en la 2 y que
    // Odo enseña. El hermano Ruotger, el cillerero, que lleva la despensa. Gertrudis, su hermana, monja con la abadesa
    // Hildegarda, que le escribe desde el monte de San Ruperto. La abadía no tiene nombre.
    //
    // Lo real: el año de la Iglesia y de la Regla (la Candelaria, la cuaresma con su libro, san Benito dos veces al año,
    // la Asunción, Todos los Santos, san Martín, la Navidad), Hildegarda y su monasterio nuevo (1150) y su libro de
    // visiones (1151 o 1152), la muerte de Suger, la del rey Conrado y la elección de Federico Barbarroja (1152), la
    // muerte del papa Eugenio y la de Bernardo de Claraval (1153), la de Roger de Sicilia, la bajada de Federico a Italia y
    // el papa inglés (1154), y la biblioteca de Lorsch. Que las noticias lleguen a la abadía esos días es inventado.
    //
    // Claves de escena: fiesta, campanas, luto, nieve, tormenta, vendimia. Arcos: libro (la Biblia grande en dos tomos:
    // 0 = el pergamino en blanco, 1 = terminada; en la temporada 5 empieza otro libro) y huerto (el huerto del claustro).
    // Estado: «lorsch» (temporada 2: seis semanas en la abadía de Lorsch, copiando un libro que en la suya no hay).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el scriptorium con el atril y el libro
    // abierto, con más páginas pintadas según `libro`; el huerto del claustro según `huerto`; la nieve en el claustro en
    // invierno; y su atril vacío mientras el estado es «lorsch».

    private val F_HILDEGARDA = "Hildegarda de Bingen" to "${W}Hildegard_of_Bingen"
    private val F_SCIVIAS = "Scivias" to "${W}Scivias"
    private val F_SUGER = "Suger" to "${W}Suger"
    private val F_CONRADO = "Conrado III" to "${W}Conrad_III_of_Germany"
    private val F_FEDERICO = "Federico Barbarroja" to "${W}Frederick_I,_Holy_Roman_Emperor"
    private val F_EUGENIO = "El papa Eugenio III" to "${W}Pope_Eugene_III"
    private val F_BERNARDO = "Bernardo de Claraval" to "${W}Bernard_of_Clairvaux"
    private val F_ROGER = "Roger II de Sicilia" to "${W}Roger_II_of_Sicily"
    private val F_ADRIANO = "El papa Adriano IV" to "${W}Pope_Adrian_IV"
    private val F_BENITO = "San Benito" to "${W}Benedict_of_Nursia"
    private val F_REGLA = "La Regla de san Benito" to "${W}Rule_of_Saint_Benedict"
    private val F_MARTIN = "San Martín" to "${W}Martinmas"

    // Lo que cuelga de la Pascua juliana de 1150, 1151, 1152, 1153 y 1154.
    private val CUARESMA_ODO = listOf("03-01", "02-21", "02-13", "03-04", "02-17")    // el miércoles de ceniza
    private val PASCUA_ODO = listOf("04-16", "04-08", "03-30", "04-19", "04-04")
    private val PENTECOSTES_ODO = listOf("06-04", "05-27", "05-18", "06-07", "05-23")

    /** Lo que vuelve cada año en la abadía, en la temporada [t]. */
    private fun anioDeLaAbadia(t: Int): List<DiaEspecial> = listOf(
        fiesta("candelaria", "02-02", "La Candelaria: la procesión de las velas por el claustro", certeza = HISTORICO, fuente = F_CANDELARIA, escena = "campanas"),
        rito("cuaresma", CUARESMA_ODO[t - 1], "Empieza la cuaresma: a cada monje le dan un libro de la biblioteca", certeza = HISTORICO, fuente = F_REGLA),
        fiesta("san_benito", "03-21", "San Benito, el padre de la casa, que murió este día", certeza = HISTORICO, fuente = F_BENITO),
        fiesta("pascua", PASCUA_ODO[t - 1], "Pascua: vuelve el aleluya que calló toda la cuaresma", certeza = HISTORICO, escena = "campanas"),
        rito("sangria", "05-05", "La sangría: tres días de descanso y de mejor comida", 3, APROXIMADO),
        fiesta("pentecostes", PENTECOSTES_ODO[t - 1], "Pentecostés: el abad canta la misa grande", certeza = HISTORICO),
        fiesta("traslacion_benito", "07-11", "San Benito de verano: el día en que llegaron sus huesos", certeza = HISTORICO, fuente = F_BENITO),
        fiesta("asuncion", "08-15", "La Asunción de la Virgen", certeza = HISTORICO, fuente = F_ASUNCION),
        cosecha("vendimia", "09-25", "La vendimia en las viñas de la abadía, sobre el Rin", 10, APROXIMADO, escena = "vendimia"),
        fiesta("todos_santos", "11-01", "Todos los Santos y, al día siguiente, los difuntos", 2, HISTORICO, F_TODOS_SANTOS, escena = "campanas"),
        fiesta("san_martin", "11-11", "San Martín: se mata el cerdo y se prueba el vino nuevo", certeza = HISTORICO, fuente = F_MARTIN),
        fiesta("navidad", "12-25", "Navidad: los maitines más largos del año", 3, HISTORICO, F_NAVIDAD, escena = "campanas"),
    )

    val ODO = Calendario(
        vida = "copista",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 5, nubes = 12, lluvia = 5, nieve = 5, niebla = 4), mes(despejado = 6, nubes = 11, lluvia = 5, nieve = 4, niebla = 3),
            mes(despejado = 8, nubes = 11, lluvia = 7, nieve = 2, viento = 3), mes(despejado = 10, nubes = 10, lluvia = 8, viento = 2),
            mes(despejado = 12, nubes = 9, lluvia = 8, tormenta = 2), mes(despejado = 14, nubes = 7, lluvia = 6, tormenta = 3),
            mes(despejado = 16, nubes = 6, lluvia = 5, tormenta = 4), mes(despejado = 15, nubes = 7, lluvia = 5, tormenta = 4),
            mes(despejado = 12, nubes = 9, lluvia = 6, niebla = 3), mes(despejado = 8, nubes = 10, lluvia = 7, niebla = 6),
            mes(despejado = 5, nubes = 12, lluvia = 7, nieve = 1, niebla = 5), mes(despejado = 4, nubes = 12, lluvia = 5, nieve = 5, niebla = 5),
        ),
        temporadas = listOf(
            TemporadaVida("1150", "rupertsberg",
                "El abad le encarga a Odo una Biblia grande en dos tomos para el coro. Llegan las pieles para el pergamino. La abadesa Hildegarda se muda con sus monjas al monte de San Ruperto, y con ella su hermana Gertrudis, que le escribe. Termina la primera gran inicial.",
                anioDeLaAbadia(1) + listOf(
                    suceso("encargo_biblia", "01-12", "El abad le encarga una Biblia grande, en dos tomos, para el coro"),
                    suceso("pieles", "03-10", "Llegan del curtidor las pieles de ternero para el pergamino"),
                    mundo("rupertsberg", "05-20", "La abadesa Hildegarda se muda con sus monjas al monte de San Ruperto", certeza = APROXIMADO, fuente = F_HILDEGARDA),
                    mundo("granizo", "06-20", "Una granizada rompe las viñas de la ladera", certeza = INVENTADO, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("carta_gertrudis", "07-25", "Le llega una carta de su hermana Gertrudis desde el monte nuevo"),
                    suceso("primera_inicial", "10-15", "Termina la primera gran inicial de la Biblia, la del Génesis"),
                )),
            TemporadaVida("1151", "lorsch",
                "Le entregan a un niño, Heinrich, para que le enseñe las letras. Llega la noticia de la muerte de Suger. Pasa seis semanas en Lorsch copiando un libro que en su abadía no hay. Se dice que Hildegarda terminó su libro de visiones. El viejo Gerhard enferma y le deja la llave de los libros.",
                anioDeLaAbadia(2) + listOf(
                    suceso("heinrich", "01-20", "Sus padres entregan a la abadía a un niño, Heinrich, y el abad se lo da a Odo"),
                    mundo("suger", "02-08", "Llega la noticia: murió Suger, el abad de Saint-Denis", certeza = APROXIMADO, fuente = F_SUGER, escena = "luto"),
                    suceso("sale_lorsch", "06-05", "Sale hacia Lorsch a copiar un libro que en su abadía no hay", cadena = "lorsch"),
                    suceso("vuelve_lorsch", "07-08", "Vuelve de Lorsch con el libro copiado y los pies deshechos", cadena = "lorsch"),
                    mundo("scivias", "10-20", "Se dice que la abadesa Hildegarda terminó su libro de visiones", certeza = APROXIMADO, fuente = F_SCIVIAS),
                    suceso("gerhard_enfermo", "11-25", "El viejo Gerhard, el bibliotecario, cae enfermo y le deja la llave de los libros"),
                )),
            TemporadaVida("1152", "rey_nuevo",
                "Muere el viejo Gerhard. Llegan las noticias de la muerte del rey Conrado y de la elección de Federico, el de la barba roja. Heinrich lee solo un salmo entero. Un rayo cae en el campanario. Odo termina el primer tomo de la Biblia.",
                anioDeLaAbadia(3) + listOf(
                    suceso("muere_gerhard", "01-18", "Muere el hermano Gerhard, que le enseñó a escribir", escena = "luto"),
                    mundo("conrado", "02-25", "Llega la noticia: murió el rey Conrado", certeza = APROXIMADO, fuente = F_CONRADO, escena = "luto"),
                    mundo("barbarroja", "03-12", "Llega la noticia: en Fráncfort eligieron rey a Federico, el de la barba roja", certeza = APROXIMADO, fuente = F_FEDERICO, escena = "campanas"),
                    suceso("heinrich_lee", "06-12", "Heinrich lee solo, en voz alta, un salmo entero"),
                    mundo("rayo", "07-28", "Un rayo cae en el campanario y quema el tejado", certeza = INVENTADO, escena = "tormenta", clima = Clima.TORMENTA),
                    suceso("primer_tomo", "09-08", "Termina el primer tomo de la Biblia"),
                )),
            TemporadaVida("1153", "claraval",
                "El Rin se hiela. Heinrich corta su primera pluma. Odo termina la Biblia grande y el abad la lleva al coro. Llegan las noticias de la muerte del papa Eugenio y de Bernardo de Claraval. A Odo le empieza a fallar la vista y le dan una piedra de leer.",
                anioDeLaAbadia(4) + listOf(
                    mundo("helada", "01-15", "El Rin se hiela de orilla a orilla", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    suceso("heinrich_pluma", "03-14", "Heinrich corta su primera pluma él solo"),
                    suceso("fin_biblia", "06-29", "Termina la Biblia grande y el abad la lleva al coro", escena = "fiesta"),
                    mundo("eugenio", "07-28", "Llega la noticia: murió el papa Eugenio", certeza = APROXIMADO, fuente = F_EUGENIO, escena = "luto"),
                    mundo("bernardo", "09-05", "Llega la noticia: murió Bernardo, el abad de Claraval", certeza = APROXIMADO, fuente = F_BERNARDO, escena = "luto"),
                    suceso("piedra_leer", "10-20", "Ya no ve bien la letra pequeña: el abad le da una piedra de leer"),
                )),
            TemporadaVida("1154", "italia",
                "Llega la noticia de la muerte del rey de Sicilia. El abad nombra a Odo bibliotecario. Heinrich copia su primera página para la biblioteca. Odo empieza un libro nuevo, los sermones de Bernardo. El rey Federico baja a Italia, y en diciembre hay un papa inglés.",
                anioDeLaAbadia(5) + listOf(
                    mundo("roger", "03-15", "Llega la noticia: murió Roger, el rey de Sicilia", certeza = APROXIMADO, fuente = F_ROGER),
                    suceso("bibliotecario", "04-22", "El abad lo nombra bibliotecario, el puesto del viejo Gerhard"),
                    suceso("heinrich_pagina", "06-24", "Heinrich copia su primera página para la biblioteca"),
                    suceso("libro_nuevo", "08-28", "Empieza a copiar un libro nuevo: los sermones de Bernardo"),
                    mundo("italia", "10-20", "Se dice que el rey Federico baja a Italia con su ejército", certeza = APROXIMADO, fuente = F_FEDERICO),
                    mundo("adriano", "12-28", "Llega la noticia: hay papa nuevo, Adriano, y es inglés", certeza = APROXIMADO, fuente = F_ADRIANO),
                )),
        ),
        arcos = listOf(
            Arco("libro", TipoArco.OBRA, listOf(
                p(1, "01-12", 0.0), p(3, "09-08", 0.5), p(4, "06-29", 1.0), p(5, "08-27", 1.0), p(5, "08-28", 0.0, salto = true), p(5, "12-31", 0.2),
            )),
            Arco.cadaAnio("huerto", TipoArco.CULTIVO, "03-15" to 0.0, "05-15" to 0.5, "07-15" to 1.0, "10-15" to 0.6, "11-15" to 0.0),
        ),
        objetos = listOf("pluma", "pergamino", "tinta", "cortaplumas", "piedra_pomez", "atril", "salterio", "habito", "campana", "vela", "pan_oro", "tablilla"),
        estados = listOf(Estado("lorsch", "sale_lorsch", "vuelve_lorsch")),
    )

    // =====================================================================
    //  El hermano Bernat, sargento de la orden del Temple. El castillo de Safed, en Galilea, de 1250 a 1254.
    // =====================================================================
    //
    // Su gente (toda inventada): Ramon, su amigo, otro sargento catalán, que muere en Egipto (en Mansura) antes de que
    // empiece la temporada 1. Pere, un hermano joven que llega de Cataluña en la 2 y a quien Bernat enseña; muere de
    // fiebres en la 4. El comendador del castillo, un caballero francés que no le habla. Yusuf, el cristiano sirio que
    // cuida los caballos con él. Su madre, en un pueblo del Ampurdán, que le escribe por mano del cura. Su caballo, el
    // Moro.
    //
    // Lo real: el castillo de Safed, que el Temple levantó de nuevo desde 1240; el año de la Iglesia y las fiestas de la
    // Virgen, patrona de la orden; la batalla de Mansura (8 de febrero de 1250) y la muerte del maestre Guillermo de
    // Sonnac, la prisión del rey Luis y su llegada a Acre; los mamelucos en Egipto; la muerte del emperador Federico;
    // Alfonso X rey de Castilla y el retiro del maestre Renaud (1252); la muerte de la reina Blanca; la salida del rey
    // Luis hacia Francia (24 de abril de 1254) y la muerte de Conrado IV. Que las noticias lleguen esos días es inventado.
    //
    // Claves de escena: fiesta, campanas, luto, guerra, obra, siega, nieve, polvo. Arco: muralla (el castillo nuevo, que
    // sigue creciendo: torres y lienzos). Estados: «herido» (temporada 2: seis semanas en la enfermería después de un
    // ataque) y «acre» (temporada 3: un mes en la casa de la orden en Acre, junto al mar, por los caballos de los barcos).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el castillo sobre el monte, con andamios
    // según `muralla`, y el lago de Galilea abajo; la nieve los inviernos que nieva; los campos segados en mayo; y el
    // mar y el puerto de Acre en vez del monte mientras el estado es «acre».

    private val F_NATIVIDAD = "La Natividad de la Virgen" to "${W}Nativity_of_Mary"
    private val F_SONNAC = "Guillaume de Sonnac" to "${W}Guillaume_de_Sonnac"
    private val F_LUIS = "Luis IX" to "${W}Louis_IX_of_France"
    private val F_SHAJAR = "Shajar al-Durr" to "${W}Shajar_al-Durr"
    private val F_FEDERICO2 = "Federico II" to "${W}Frederick_II,_Holy_Roman_Emperor"
    private val F_RENAUD = "Renaud de Vichiers" to "${W}Renaud_de_Vichiers"
    private val F_ALFONSO = "Alfonso X" to "${W}Alfonso_X_of_Castile"
    private val F_BLANCA = "Blanca de Castilla" to "${W}Blanche_of_Castile"
    private val F_SEPTIMA = "La séptima cruzada" to "${W}Seventh_Crusade"

    // Lo que cuelga de la Pascua juliana de 1250, 1251, 1252, 1253 y 1254.
    private val CUARESMA_BERNAT = listOf("02-09", "03-01", "02-14", "03-05", "02-25")
    private val PASCUA_BERNAT = listOf("03-27", "04-16", "03-31", "04-20", "04-12")

    /** Lo que vuelve cada año en el castillo, en la temporada [t]. */
    private fun anioDelCastillo(t: Int): List<DiaEspecial> = listOf(
        fiesta("candelaria", "02-02", "La Candelaria: fiesta de la Virgen, patrona de la orden", certeza = HISTORICO, fuente = F_CANDELARIA),
        rito("cuaresma", CUARESMA_BERNAT[t - 1], "Empieza la cuaresma: la orden ayuna hasta Pascua", certeza = HISTORICO),
        fiesta("anunciacion", "03-25", "La Anunciación: fiesta de la Virgen", certeza = HISTORICO, fuente = F_ANUNCIACION),
        fiesta("pascua", PASCUA_BERNAT[t - 1], "Pascua: carne otra vez en el refectorio", certeza = HISTORICO, escena = "campanas"),
        cosecha("siega", "05-10", "La siega del trigo y la cebada en las aldeas del castillo", 10, APROXIMADO, escena = "siega"),
        fiesta("asuncion", "08-15", "La Asunción de la Virgen, la fiesta grande de la orden", certeza = HISTORICO, fuente = F_ASUNCION),
        fiesta("natividad_virgen", "09-08", "El nacimiento de la Virgen", certeza = HISTORICO, fuente = F_NATIVIDAD),
        cosecha("aceitunas", "10-20", "La cosecha de la aceituna en los olivares de la ladera", 15, APROXIMADO),
        fiesta("todos_santos", "11-01", "Todos los Santos: misa por los hermanos muertos", certeza = HISTORICO, fuente = F_TODOS_SANTOS),
        fiesta("navidad", "12-25", "Navidad en el castillo", certeza = HISTORICO, fuente = F_NAVIDAD, escena = "campanas"),
    )

    val BERNAT = Calendario(
        vida = "sargento",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 10, nubes = 9, lluvia = 8, nieve = 2, viento = 2), mes(despejado = 10, nubes = 9, lluvia = 7, nieve = 1, viento = 1),
            mes(despejado = 14, nubes = 8, lluvia = 6, viento = 2, polvo = 1), mes(despejado = 18, nubes = 6, lluvia = 3, polvo = 3),
            mes(despejado = 23, nubes = 4, lluvia = 1, polvo = 3), mes(despejado = 27, nubes = 2, polvo = 1),
            mes(despejado = 29, nubes = 2), mes(despejado = 29, nubes = 2),
            mes(despejado = 26, nubes = 3, polvo = 1), mes(despejado = 21, nubes = 5, lluvia = 2, polvo = 3),
            mes(despejado = 15, nubes = 8, lluvia = 5, tormenta = 2), mes(despejado = 11, nubes = 9, lluvia = 8, nieve = 1, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1250", "mansura",
                "Llegan de Egipto las noticias de Mansura: casi todos los templarios del ejército del rey murieron, entre ellos Ramon, el amigo de Bernat. El rey Luis, preso, sale libre y llega a Acre. En Egipto mandan los mamelucos. A Bernat lo ponen a subir la torre del sur. Le escribe su madre.",
                anioDelCastillo(1) + listOf(
                    guerra("mansura", "02-26", "Llega la noticia: en Mansura cayeron casi todos los templarios de Egipto", certeza = APROXIMADO, fuente = F_SONNAC, escena = "luto"),
                    suceso("misa_ramon", "03-03", "Se canta misa por los hermanos muertos en Egipto; entre ellos, su amigo Ramon", escena = "luto"),
                    guerra("maestre", "04-28", "Llega la noticia: el rey Luis, preso, y el maestre Guillermo, muerto", certeza = APROXIMADO, fuente = F_SONNAC, escena = "luto"),
                    mundo("rey_acre", "05-25", "El rey Luis llega a Acre, libre, con lo que queda de su ejército", certeza = APROXIMADO, fuente = F_LUIS),
                    suceso("torre_sur", "06-20", "Lo ponen a cargo de los que suben la torre del sur", escena = "obra"),
                    mundo("mamelucos", "08-05", "Se dice que en Egipto mandan ahora los mamelucos", certeza = APROXIMADO, fuente = F_SHAJAR),
                    suceso("carta_madre", "09-25", "Le llega una carta de su madre, abierta por el comendador"),
                    suceso("peregrinos", "11-20", "Escolta una caravana de peregrinos hasta Nazaret"),
                )),
            TemporadaVida("1251", "herida",
                "Llega la noticia de la muerte del emperador Federico. Nieva en el castillo. Llega de Cataluña un hermano joven, Pere, y se lo dan para que lo enseñe. Unos jinetes atacan a los que siegan y a Bernat lo hieren en la pierna: pasa seis semanas en la enfermería.",
                anioDelCastillo(2) + listOf(
                    mundo("nieve", "01-20", "Nieva en el castillo y se llenan las cisternas", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    mundo("federico", "02-20", "Llega la noticia: murió el emperador Federico", certeza = APROXIMADO, fuente = F_FEDERICO2),
                    suceso("pere", "03-12", "Llega de Cataluña un hermano joven, Pere, y se lo dan para que lo enseñe"),
                    guerra("herida", "06-12", "Unos jinetes atacan a los que siegan; Bernat sale y lo hieren en la pierna", certeza = INVENTADO, escena = "guerra"),
                    suceso("vuelve_muralla", "08-02", "Vuelve a la muralla, con la pierna rígida"),
                    suceso("moro_cojo", "10-05", "Su caballo, el Moro, se queda cojo, y Bernat no quiere otro"),
                )),
            TemporadaVida("1252", "acre",
                "Tiembla la tierra en el castillo. Pere hace sus votos y su primera carga de práctica. Llega la noticia de que en Castilla hay rey nuevo. Bernat pasa un mes en Acre, en la casa de la orden junto al mar, eligiendo los caballos que llegan en los barcos. Se dice que el maestre Renaud se retira a un monasterio.",
                anioDelCastillo(3) + listOf(
                    mundo("temblor", "01-28", "Tiembla la tierra en el castillo y se raja una bóveda", certeza = INVENTADO, escena = "obra"),
                    suceso("votos_pere", "04-20", "Pere hace sus votos en la capilla, y Bernat es su padrino"),
                    suceso("carga_pere", "07-05", "Pere hace su primera carga de práctica, y se cae del caballo"),
                    mundo("alfonso", "08-20", "Llega la noticia: en Castilla hay rey nuevo, Alfonso", certeza = APROXIMADO, fuente = F_ALFONSO),
                    suceso("sale_acre", "09-12", "Baja a Acre, a la casa de la orden, a buscar los caballos de los barcos", cadena = "acre"),
                    suceso("caballos", "09-28", "En el puerto de Acre desembarcan los caballos de Apulia y él elige seis", cadena = "acre"),
                    suceso("vuelve_acre", "10-15", "Vuelve a Safed con seis caballos y sal del mar en la ropa", cadena = "acre"),
                    mundo("renaud", "12-05", "Se dice que el maestre Renaud se retira a un monasterio", certeza = APROXIMADO, fuente = F_RENAUD),
                )),
            TemporadaVida("1253", "torre",
                "Llega la noticia de la muerte de la reina Blanca, la madre del rey Luis. Se termina la torre del sur. Pere muere de fiebres en la enfermería, y Bernat lo entierra. Un viento del desierto ciega el castillo tres días. Bernat enseña a montar a los turcópolos nuevos.",
                anioDelCastillo(4) + listOf(
                    mundo("blanca", "04-10", "Llega la noticia: murió la reina Blanca, la madre del rey Luis", certeza = APROXIMADO, fuente = F_BLANCA, escena = "luto"),
                    mundo("jamsin", "05-28", "Sopla tres días el viento caliente del desierto", 3, INVENTADO, escena = "polvo", clima = Clima.POLVO),
                    suceso("fin_torre", "07-10", "Se termina la torre del sur y se pone la cruz arriba", escena = "obra"),
                    suceso("muere_pere", "08-28", "Muere Pere de fiebres en la enfermería", escena = "luto"),
                    suceso("turcopolos", "11-20", "Le dan a enseñar a montar a los turcópolos nuevos"),
                    mundo("lluvias", "12-10", "Llegan las lluvias grandes: el camino de Acre se hace barro", certeza = INVENTADO, clima = Clima.LLUVIA),
                )),
            TemporadaVida("1254", "rey_se_va",
                "Llega un comendador nuevo que lee la Regla entera en francés. A Bernat lo ponen a cargo de la cuadra del castillo. El rey Luis zarpa de Acre hacia Francia. Bernat le dicta al capellán una carta para su madre. Muere Conrado, el hijo del emperador. Escolta a unos frailes hasta el lago.",
                anioDelCastillo(5) + listOf(
                    suceso("cuadra", "03-08", "Lo ponen a cargo de la cuadra del castillo"),
                    mundo("rey_se_va", "04-26", "Llega la noticia: el rey Luis zarpó de Acre hacia Francia", certeza = APROXIMADO, fuente = F_LUIS),
                    mundo("conrado", "07-02", "Llega la noticia: murió Conrado, el hijo del emperador", certeza = APROXIMADO, fuente = F_SEPTIMA),
                    suceso("carta_capellan", "06-18", "Le dicta al capellán una carta para su madre"),
                    suceso("frailes", "09-22", "Escolta a unos frailes que quieren ver el lago de Galilea"),
                    mundo("comendador", "01-20", "Llega un comendador nuevo, que trae la Regla en francés y la lee entera", certeza = INVENTADO),
                )),
        ),
        arcos = listOf(
            Arco("muralla", TipoArco.OBRA, listOf(p(1, "01-01", 0.5), p(1, "06-20", 0.55), p(4, "07-10", 0.8), p(5, "12-31", 0.9))),
        ),
        objetos = listOf("espada", "lanza", "escudo", "cota", "manto", "silla", "herradura", "almohaza", "regla", "escudilla", "vela", "campana"),
        estados = listOf(Estado("herido", "herida", "vuelve_muralla"), Estado("acre", "sale_acre", "vuelve_acre")),
    )

    // =====================================================================
    //  Zaynab, tejedora de seda. Córdoba, del 970 al 974.
    // =====================================================================
    //
    // Su gente (toda inventada): Yusuf, su marido, perfumista en el zoco. Alí, su hijo (6 años en la temporada 1).
    // Maryam, la hija que nace en la 3. Fátima, su suegra, que vive con ellos. El maestro Abu Bakr, dueño del taller de
    // seda. Juliana, su vecina, cristiana. Isaac, el tintorero judío que tiñe la seda del taller.
    //
    // Lo real: el año islámico (el Ramadán con su noche del destino, la fiesta del fin del ayuno, la de los sacrificios,
    // el año nuevo y Ashura), la muerte del hijo mayor del califa al-Hakam (970), el mihrab nuevo de la mezquita (971),
    // la flota de los vikingos (julio del 971), la caída de Ibn Abi Amir (Almanzor) como jefe de la ceca (marzo del 972),
    // el general Galib y su guerra contra los idrisíes en África (973 y 974), los fatimíes en El Cairo y su mezquita
    // nueva, y el calendario de ʿArīb ibn Saʿīd (973). Que las noticias lleguen esos días es inventado. Las fiestas
    // cristianas de la ciudad (Yannayr) y la seda nueva son costumbres de Córdoba sin una fecha de página.
    //
    // Claves de escena: fiesta, luto, lámparas (las noches de Ramadán), lluvia, nieve, guerra. Arcos: tela (la pieza que
    // teje en el telar grande: 0 = la urdimbre, 1 = terminada; hay dos, una en las temporadas 1 y 2 y otra de la 3 a la
    // 5) y río (el Guadalquivir: alto en invierno, bajo en verano). Estado: «ramadan» (cada año, del primer día del ayuno
    // a la víspera de la fiesta).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el patio con agua y plantas y el telar, con
    // la tela según `tela`; el río y el puente al fondo según `rio`; y, durante el estado «ramadan», las lámparas
    // encendidas de noche.

    private val F_CALENDARIO_ISLAMICO = "El calendario islámico" to "${W}Islamic_calendar"
    private val F_RAMADAN = "El Ramadán" to "${W}Ramadan"
    private val F_QADR = "La noche del destino" to "${W}Laylat_al-Qadr"
    private val F_FITR = "La fiesta del fin del ayuno" to "${W}Eid_al-Fitr"
    private val F_ADHA = "La fiesta del sacrificio" to "${W}Eid_al-Adha"
    private val F_ASHURA = "Ashura" to "${W}Ashura"
    private val F_HAKAM = "Al-Hakam II" to "${W}Al-Hakam_II"
    private val F_MEZQUITA = "La mezquita de Córdoba" to "${W}Mosque%E2%80%93Cathedral_of_C%C3%B3rdoba"
    private val F_GALIB = "Galib" to "${W}Ghalib_al-Nasiri"
    private val F_ALMANZOR = "Almanzor" to "${W}Almanzor"
    private val F_AZHAR = "La mezquita de al-Azhar" to "${W}Al-Azhar_Mosque"
    private val F_MUIZZ = "Al-Muizz" to "${W}Al-Mu%27izz_li-Din_Allah"
    private val F_CALENDARIO_CORDOBA = "El calendario de Córdoba" to "${W}Calendar_of_C%C3%B3rdoba"

    // El año islámico tabular del 359 al 364 de la hégira, pasado al juliano (970 a 974).
    private val RAMADAN = listOf("07-08", "06-28", "06-16", "06-05", "05-26")
    private val QADR = listOf("08-03", "07-24", "07-12", "07-01", "06-21")         // la noche del 27 de Ramadán
    private val FITR = listOf("08-07", "07-28", "07-16", "07-05", "06-25")
    private val ADHA = listOf("10-14", "10-04", "09-22", "09-11", "09-01")
    private val HEGIRA = listOf("11-04", "10-24", "10-12", "10-02", "09-21")       // el 1 de Muharram
    private val ASHURA = listOf("11-13", "11-02", "10-21", "10-11", "09-30")

    /** Lo que vuelve cada año en la ciudad, en la temporada [t]. */
    private fun anioDeCordoba(t: Int): List<DiaEspecial> = listOf(
        fiesta("yannayr", "01-01", "Yannayr, el año nuevo de los cristianos, que celebra media ciudad", certeza = APROXIMADO),
        fiesta("almunias", "03-20", "Las almunias en flor: la ciudad sale a los huertos del río", certeza = APROXIMADO),
        cosecha("seda_nueva", "06-10", "Llega la seda nueva de las alquerías de la sierra", 5, APROXIMADO),
        rito("ramadan", RAMADAN[t - 1], "Empieza el Ramadán: ayuno de sol a sol", certeza = APROXIMADO, fuente = F_RAMADAN, escena = "lamparas"),
        rito("qadr", QADR[t - 1], "La noche del destino: nadie duerme en la mezquita", certeza = APROXIMADO, fuente = F_QADR, escena = "lamparas"),
        fiesta("eid_fitr", FITR[t - 1], "La fiesta del fin del ayuno: ropa nueva y dulces", 3, APROXIMADO, F_FITR),
        fiesta("eid_adha", ADHA[t - 1], "La fiesta del sacrificio: cada casa mata su cordero", 4, APROXIMADO, F_ADHA),
        rito("hegira", HEGIRA[t - 1], "El año nuevo de la hégira", certeza = APROXIMADO, fuente = F_CALENDARIO_ISLAMICO),
        rito("ashura", ASHURA[t - 1], "Ashura: se ayuna y se da limosna", certeza = APROXIMADO, fuente = F_ASHURA),
        mundo("lluvias", "11-25", "Las primeras lluvias grandes: el río crece y se lleva la basura de las orillas", certeza = APROXIMADO, clima = Clima.LLUVIA),
    )

    val ZAYNAB = Calendario(
        vida = "cordobesa",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 13, nubes = 9, lluvia = 7, niebla = 2), mes(despejado = 14, nubes = 8, lluvia = 6), mes(despejado = 16, nubes = 8, lluvia = 6, viento = 1),
            mes(despejado = 17, nubes = 7, lluvia = 6), mes(despejado = 22, nubes = 5, lluvia = 3, tormenta = 1), mes(despejado = 27, nubes = 2, polvo = 1),
            mes(despejado = 29, nubes = 1, polvo = 1), mes(despejado = 28, nubes = 2, polvo = 1), mes(despejado = 23, nubes = 4, lluvia = 2, tormenta = 1),
            mes(despejado = 18, nubes = 7, lluvia = 5, tormenta = 1), mes(despejado = 14, nubes = 8, lluvia = 7, niebla = 1), mes(despejado = 13, nubes = 9, lluvia = 7, niebla = 2),
        ),
        temporadas = listOf(
            TemporadaVida("970", "principe",
                "Al taller le encargan una tela de seda con letras para el califa, y el maestro se la da a Zaynab. Alí empieza a ir a la escuela. Juliana, su vecina, se casa en la iglesia de San Acisclo. Muere el hijo mayor del califa. Isaac le enseña a sacar el carmesí. Se habla de la mezquita nueva de los fatimíes.",
                anioDeCordoba(1) + listOf(
                    suceso("encargo_tela", "02-10", "Al taller le encargan una tela con letras para el califa, y el maestro se la da a ella"),
                    suceso("ali_escuela", "03-15", "Alí empieza a ir a la escuela de la mezquita del barrio"),
                    suceso("boda_juliana", "05-12", "Juliana, su vecina cristiana, se casa en la iglesia de San Acisclo", escena = "fiesta"),
                    mundo("principe", "06-02", "Muere Abderramán, el hijo mayor del califa, que tenía ocho años", certeza = APROXIMADO, fuente = F_HAKAM, escena = "luto"),
                    suceso("carmesi", "09-20", "Isaac, el tintorero, le enseña a sacar el carmesí del grano de las encinas"),
                    mundo("azhar", "12-05", "Se dice que los fatimíes levantan una mezquita grande en su ciudad nueva de Egipto", certeza = APROXIMADO, fuente = F_AZHAR),
                )),
            TemporadaVida("971", "mihrab",
                "El río crece y se lleva un molino. Se reza por primera vez ante el mihrab nuevo de la mezquita. Zaynab termina la tela del califa y el maestro la lleva a Madinat al-Zahra. En pleno Ramadán llega la noticia de una flota de vikingos en la costa. Zaynab espera un hijo. Alí recita su primera sura.",
                anioDeCordoba(2) + listOf(
                    mundo("crecida", "02-20", "El río crece y se lleva un molino de la orilla", certeza = INVENTADO, clima = Clima.LLUVIA),
                    mundo("mihrab", "04-16", "Se reza por primera vez ante el mihrab nuevo de la mezquita grande", certeza = APROXIMADO, fuente = F_MEZQUITA, escena = "fiesta"),
                    suceso("tela_califa", "06-03", "Termina la tela del califa, y el maestro la lleva a Madinat al-Zahra"),
                    guerra("vikingos", "07-10", "Llega la noticia: una flota de los mayus apareció en la costa del océano", certeza = APROXIMADO, fuente = F_GALIB, escena = "guerra"),
                    suceso("zaynab_espera", "09-05", "Sabe que espera un hijo"),
                    suceso("ali_sura", "12-12", "Alí recita su primera sura entera delante del maestro de la escuela"),
                )),
            TemporadaVida("972", "maryam",
                "Nieva en Córdoba. Se dice que a Ibn Abi Amir lo quitaron de la ceca por las cuentas. Nace Maryam, y a los siete días se le da nombre. El maestro le da a Zaynab el telar grande y una tela nueva. Galib es nombrado jefe de todos los ejércitos.",
                anioDeCordoba(3) + listOf(
                    mundo("nieve", "01-28", "Nieva en Córdoba, y los niños salen a tocar la nieve", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    mundo("almanzor", "03-24", "Se dice que a Ibn Abi Amir, el de la ceca, lo quitaron por las cuentas", certeza = APROXIMADO, fuente = F_ALMANZOR),
                    suceso("nace_maryam", "05-02", "Nace su hija, Maryam", cadena = "maryam"),
                    suceso("nombre_maryam", "05-08", "A los siete días se le da nombre a la niña y se reparte carne", cadena = "maryam", escena = "fiesta"),
                    suceso("telar_grande", "08-28", "El maestro le da el telar grande y una tela nueva, para una boda de la corte"),
                    mundo("galib_jefe", "11-05", "Galib es ahora el jefe de todos los ejércitos del califa", certeza = APROXIMADO, fuente = F_GALIB),
                )),
            TemporadaVida("973", "africa",
                "Zaynab toma una aprendiza. El general Galib sale hacia África contra los idrisíes. Maryam da sus primeros pasos. Yusuf abre una tienda más grande en el zoco de los perfumistas. Se dice que el califa de los fatimíes se mudó a Egipto, y que el médico ʿArīb terminó su libro de los tiempos.",
                anioDeCordoba(4) + listOf(
                    suceso("aprendiza", "02-15", "Toma una aprendiza, Rabia, de once años"),
                    guerra("galib_africa", "04-25", "El general Galib sale hacia África contra los idrisíes", certeza = APROXIMADO, fuente = F_GALIB, escena = "guerra"),
                    suceso("maryam_camina", "05-20", "Maryam da sus primeros pasos en el patio"),
                    suceso("tienda", "08-05", "Yusuf abre una tienda más grande en el zoco de los perfumistas"),
                    mundo("el_cairo", "09-24", "Se dice que el califa de los fatimíes se mudó a su ciudad nueva de Egipto", certeza = APROXIMADO, fuente = F_MUIZZ),
                    mundo("calendario", "12-05", "El médico ʿArīb termina su libro de los tiempos, las estrellas y los cultivos", certeza = APROXIMADO, fuente = F_CALENDARIO_CORDOBA),
                )),
            TemporadaVida("974", "idrisi",
                "Hiela en Córdoba. El maestro le deja llevar el taller cuando él no está. Alí entra de aprendiz con su padre. Llega la noticia de que los fatimíes perdieron en África. Zaynab termina la tela de la boda. Galib vuelve en triunfo con el señor de los idrisíes, prisionero. Maryam dice su nombre.",
                anioDeCordoba(5) + listOf(
                    mundo("helada", "02-05", "Hiela en Córdoba y se rompen los cántaros del patio", certeza = INVENTADO),
                    suceso("taller", "03-02", "El maestro le deja llevar el taller cuando él no está"),
                    suceso("ali_aprendiz", "04-22", "Alí entra de aprendiz en la tienda de su padre"),
                    mundo("fatimies", "07-15", "Llega la noticia: los fatimíes perdieron en África", certeza = APROXIMADO, fuente = F_HAKAM),
                    suceso("tela_boda", "08-10", "Termina la tela de la boda y la llevan a palacio"),
                    mundo("ibn_gannun", "09-15", "Galib vuelve en triunfo con el señor de los idrisíes, prisionero", certeza = APROXIMADO, fuente = F_GALIB, escena = "fiesta"),
                    suceso("maryam_habla", "10-20", "Maryam dice su nombre, y luego el de su abuela"),
                )),
        ),
        arcos = listOf(
            Arco("tela", TipoArco.OBRA, listOf(
                p(1, "02-10", 0.0), p(2, "06-03", 1.0), p(3, "08-27", 1.0), p(3, "08-28", 0.0, salto = true), p(5, "08-10", 1.0),
            )),
            Arco.cadaAnio("rio", TipoArco.OTRO, "01-15" to 0.8, "03-15" to 1.0, "06-15" to 0.45, "09-15" to 0.2, "12-01" to 0.6),
        ),
        objetos = listOf("lanzadera", "peine", "huso", "seda", "carmesi", "candil", "alcuza", "agua_rosas", "alhena", "laud", "jarra", "dirham"),
        estados = listOf(Estado("ramadan", "ramadan", "eid_fitr")),
    )

    // =====================================================================
    //  Bat, pastor nómada. Las orillas del Kerulen, en la estepa mongola, de 1220 a 1224.
    // =====================================================================
    //
    // Su gente (toda inventada): Khulan, su mujer. Mönkh, su hijo (7 años en la temporada 1). Saran, la hija que nace en
    // la 2. Su madre, que muere en la 4, después del invierno malo. Temür, su hermano menor, que sigue con el ejército en
    // el oeste y vuelve en la 5. Bolad, el vecino del campamento. El viejo chamán. Su caballo, el Negro, y el potro
    // blanco que nace en la 1.
    //
    // Lo real: el año nuevo de la luna (el mes blanco), la primera leche de las yeguas, el ovoo del cerro, el fieltro de
    // las tiendas, la guerra del kan contra el sha de Juarezm (Bujará y Samarcanda en 1220, Urgench en 1221, la batalla
    // del Indo), el monje Changchun, que cruzó el Kerulen en 1221, Muqali y su muerte (1223), la batalla del Kalka (1223),
    // Jochi, que no quiso volver, y la muerte de Jebe. Que las noticias lleguen esos días es inventado.
    //
    // Claves de escena: fiesta, nieve, luto, polvo, partida, guerra. Arcos: rebaño (cuántos animales tiene: baja en el
    // invierno malo de la temporada 4) y pasto (la hierba de la estepa). Estado: «verano» (cada año, de la mudanza a los
    // pastos de verano a la vuelta al campamento de invierno).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: la tienda de fieltro con el corral y el
    // rebaño según `rebano`; la hierba según `pasto` y la nieve en invierno; y, durante el estado «verano», el
    // campamento junto al río, en el llano abierto, en vez del valle de invierno al abrigo del cerro.

    private val F_TSAGAAN = "El mes blanco (Tsagaan Sar)" to "${W}Tsagaan_Sar"
    private val F_KUMIS = "El airag (kumis)" to "${W}Kumis"
    private val F_OVOO = "El ovoo" to "${W}Ovoo"
    private val F_YURT = "La tienda de fieltro" to "${W}Yurt"
    private val F_BUJARA = "La toma de Bujará" to "${W}Siege_of_Bukhara"
    private val F_SAMARCANDA = "La toma de Samarcanda" to "${W}Siege_of_Samarkand_(1220)"
    private val F_QIU = "Changchun (Qiu Chuji)" to "${W}Qiu_Chuji"
    private val F_URGENCH = "La toma de Urgench" to "${W}Siege_of_Gurganj"
    private val F_INDO = "La batalla del Indo" to "${W}Battle_of_the_Indus"
    private val F_MUQALI = "Muqali" to "${W}Muqali"
    private val F_KALKA = "La batalla del Kalka" to "${W}Battle_of_the_Kalka_River"
    private val F_GENGIS = "Gengis Kan" to "${W}Genghis_Khan"
    private val F_JEBE = "Jebe" to "${W}Jebe"

    // El primer día del mes blanco, del 1220 al 1224 (calculado; ver `fuentes.md`).
    private val MES_BLANCO = listOf("02-06", "01-25", "01-14", "02-02", "01-22")

    /** Lo que vuelve cada año en el campamento, en la temporada [t]. */
    private fun anioDeLaEstepa(t: Int): List<DiaEspecial> = listOf(
        fiesta("mes_blanco", MES_BLANCO[t - 1], "El mes blanco: el año nuevo, con ropa nueva y visitas", 2, APROXIMADO, F_TSAGAAN),
        cosecha("paricion", "03-25", "Nacen los corderos y los cabritos en el campamento de primavera", 20, APROXIMADO),
        rito("sube_verano", "05-25", "Se desarma la tienda y el campamento se muda a los pastos de verano", certeza = APROXIMADO, escena = "partida"),
        rito("primera_leche", "06-16", "Se ordeñan las yeguas y se rocía al cielo la primera leche", certeza = APROXIMADO, fuente = F_KUMIS),
        cosecha("esquila", "06-25", "La esquila de las ovejas", 10, APROXIMADO),
        rito("ovoo", "07-12", "Se lleva leche y una piedra al ovoo del cerro", certeza = APROXIMADO, fuente = F_OVOO),
        cosecha("fieltro", "09-05", "Se hace el fieltro nuevo para las tiendas", 7, APROXIMADO, F_YURT),
        rito("baja_invierno", "09-25", "El campamento vuelve al valle de invierno, al abrigo del cerro", certeza = APROXIMADO, escena = "partida"),
        cosecha("caza", "11-10", "La gran cacería del invierno, en círculo", 10, APROXIMADO),
        cosecha("matanza", "11-28", "Se mata el ganado del invierno y la carne se congela", 3, APROXIMADO),
        rito("noche_larga", "12-22", "La noche más larga: se cuentan las historias de los antepasados", certeza = APROXIMADO),
    )

    val BAT = Calendario(
        vida = "pastor_mongol",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 20, nubes = 5, nieve = 3, viento = 3), mes(despejado = 19, nubes = 5, nieve = 2, viento = 2),
            mes(despejado = 16, nubes = 6, nieve = 2, viento = 5, polvo = 2), mes(despejado = 14, nubes = 6, lluvia = 1, nieve = 1, viento = 5, polvo = 3),
            mes(despejado = 15, nubes = 7, lluvia = 3, viento = 4, polvo = 2), mes(despejado = 14, nubes = 8, lluvia = 5, tormenta = 2, viento = 1),
            mes(despejado = 12, nubes = 8, lluvia = 7, tormenta = 4), mes(despejado = 13, nubes = 8, lluvia = 7, tormenta = 3),
            mes(despejado = 18, nubes = 7, lluvia = 3, viento = 2), mes(despejado = 19, nubes = 6, nieve = 1, viento = 4, polvo = 1),
            mes(despejado = 19, nubes = 5, nieve = 3, viento = 3), mes(despejado = 21, nubes = 5, nieve = 3, viento = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1220, el año del dragón", "bujara",
                "Bat le regala a su madre la copa de plata que trajo de la guerra. Llegan las noticias de Bujará y de Samarcanda, que el kan tomó. La yegua baya pare un potro blanco. Pasan los del correo del kan y se llevan caballos. Khulan espera un hijo.",
                anioDeLaEstepa(1) + listOf(
                    suceso("copa", "01-10", "Le regala a su madre la copa de plata que trajo de la guerra del oeste"),
                    guerra("bujara", "04-20", "Llega un mensajero: el kan tomó Bujará, la ciudad de los libros", certeza = APROXIMADO, fuente = F_BUJARA, escena = "guerra"),
                    suceso("potro", "05-02", "La yegua baya pare un potro blanco"),
                    guerra("samarcanda", "05-15", "Llega la noticia: cayó Samarcanda", certeza = APROXIMADO, fuente = F_SAMARCANDA, escena = "guerra"),
                    mundo("correo", "07-20", "Pasan los del correo del kan y se llevan caballos de refresco", certeza = INVENTADO),
                    suceso("khulan_espera", "08-12", "Khulan le dice que espera un hijo"),
                )),
            TemporadaVida("1221, el año de la serpiente", "changchun",
                "Nace Saran. Pasa por el Kerulen un monje chino muy viejo que va a ver al kan. Llega la noticia de que Urgench cayó. El potro blanco gana una carrera entre los campamentos. Bat mata un lobo que atacaba las ovejas.",
                anioDeLaEstepa(2) + listOf(
                    suceso("nace_saran", "03-12", "Nace su hija, Saran"),
                    mundo("changchun", "04-25", "Pasa por el Kerulen un monje chino muy viejo que va a ver al kan", certeza = APROXIMADO, fuente = F_QIU),
                    guerra("urgench", "07-01", "Llega la noticia: cayó Urgench, la capital del sha", certeza = APROXIMADO, fuente = F_URGENCH, escena = "guerra"),
                    suceso("carrera", "08-08", "Su potro blanco gana una carrera entre los campamentos"),
                    suceso("lobo", "12-05", "Mata un lobo que atacaba las ovejas"),
                )),
            TemporadaVida("1222, el año del caballo", "indo",
                "La madre de Bat enferma de la tos del invierno. Llega la noticia de que el kan venció al sultán junto a un río grande del sur. Mönkh mata su primera liebre con el arco. Se casa la hija de Bolad. Se dice que Muqali toma ciudades junto al río Amarillo.",
                anioDeLaEstepa(3) + listOf(
                    suceso("madre_tos", "01-30", "Su madre enferma de la tos del invierno"),
                    guerra("indo", "03-05", "Llega la noticia: el kan venció al sultán junto a un río grande del sur", certeza = APROXIMADO, fuente = F_INDO, escena = "guerra"),
                    suceso("primera_liebre", "06-05", "Mönkh mata su primera liebre con el arco, y le untan el dedo con grasa"),
                    suceso("boda_bolad", "10-15", "Se casa la hija de Bolad, el vecino, con un muchacho de otro clan", escena = "fiesta"),
                    guerra("muqali_rio", "12-15", "Se dice que Muqali toma ciudades junto al río Amarillo", certeza = APROXIMADO, fuente = F_MUQALI),
                )),
            TemporadaVida("1223, el año de la oveja", "kalka",
                "Una costra de hielo tapa los pastos y el ganado muere de hambre. Muere la madre de Bat. Llegan las noticias de la muerte de Muqali y de la victoria de Jebe y Subutai en un río del oeste. Mönkh cuida solo los caballos una noche. Bat cambia pieles por ovejas. Se dice que Jochi, el hijo mayor del kan, no quiere volver.",
                anioDeLaEstepa(4) + listOf(
                    mundo("hielo", "02-15", "Una costra de hielo tapa los pastos: el ganado empieza a morir de hambre", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                    suceso("muere_madre", "03-08", "Muere su madre, después del invierno malo", escena = "luto"),
                    mundo("muqali", "05-05", "Llega la noticia: murió Muqali, el general del kan", certeza = APROXIMADO, fuente = F_MUQALI, escena = "luto"),
                    guerra("kalka", "09-15", "Llega la noticia: Jebe y Subutai vencieron a los rus junto a un río del oeste", certeza = APROXIMADO, fuente = F_KALKA, escena = "guerra"),
                    suceso("monkh_noche", "07-25", "Mönkh cuida solo los caballos una noche entera"),
                    suceso("ovejas", "10-20", "Cambia pieles de zorro por diez ovejas a un clan del norte"),
                    mundo("jochi", "12-05", "Se dice que Jochi, el hijo mayor del kan, no quiere volver", certeza = APROXIMADO, fuente = F_GENGIS),
                )),
            TemporadaVida("1224, el año del mono", "temur",
                "Se dice que murió Jebe. Saran monta sola por primera vez. Vuelve Temür, el hermano de Bat, después de cinco años en la guerra. Mönkh corre en la carrera de los niños. El rebaño vuelve a crecer.",
                anioDeLaEstepa(5) + listOf(
                    mundo("jebe", "03-01", "Se dice que murió Jebe, el general que llegó más lejos", certeza = APROXIMADO, fuente = F_JEBE, escena = "luto"),
                    suceso("saran_monta", "05-05", "Saran monta sola por primera vez, en el potro manso"),
                    suceso("vuelve_temur", "07-28", "Vuelve su hermano Temür de la guerra, después de cinco años", escena = "fiesta"),
                    suceso("carrera_monkh", "08-25", "Mönkh corre en la carrera de los niños y llega tercero"),
                    mundo("lluvia_buena", "10-25", "Llovió bien todo el verano: la hierba alcanza para el invierno", certeza = INVENTADO),
                )),
        ),
        arcos = listOf(
            Arco("rebano", TipoArco.OTRO, listOf(
                p(1, "01-01", 0.7), p(3, "12-31", 0.85), p(4, "02-15", 0.85), p(4, "03-20", 0.5), p(5, "12-31", 0.65),
            )),
            Arco.cadaAnio("pasto", TipoArco.CULTIVO, "04-15" to 0.0, "06-15" to 0.7, "08-01" to 1.0, "09-30" to 0.5, "11-01" to 0.0),
        ),
        objetos = listOf("arco", "lazo", "silla", "ger", "odre", "cuchillo", "eslabon", "morin_khuur", "cuenco", "deel", "argal", "copa"),
        estados = listOf(Estado("verano", "sube_verano", "baja_invierno")),
    )

    // =====================================================================
    //  Iacopo, aprendiz de pintor. El taller de Domenico Ghirlandaio, en Florencia, de 1490 a 1494.
    // =====================================================================
    //
    // Su gente (inventada, salvo los pintores): su padre, cardador de lana en el barrio de Santo Spirito; su madre; Nanna,
    // su hermana, que se casa en la temporada 3. Masino, el otro aprendiz, su amigo. Caterina, la hija del vendedor de
    // colores, a quien conoce en la 1 y con quien se promete en la 4. Son reales el maestro Domenico, sus hermanos Davide y
    // Benedetto, el cuñado Bastiano Mainardi, y Miguel Ángel, que pasó por el taller de muchacho.
    //
    // Lo real: el año de la Iglesia y de la ciudad (el carnaval, la Anunciación, que en Florencia era el año nuevo, la
    // Pascua con el carro de fuego, el primero de mayo, San Juan, el patrón, la Asunción, San Lucas, el de los pintores,
    // Todos los Santos y la Navidad); la capilla Tornabuoni, terminada en 1490; fray Girolamo Savonarola, que vuelve a San
    // Marcos en 1490 y llena la catedral; la Visitación de 1491; la muerte de Lorenzo de Médici y el rayo en la cúpula
    // (1492); el papa Borgia; la nariz rota de Miguel Ángel; la carta de Colón (1493); la muerte del maestro Domenico (11
    // de enero de 1494); la estatua de nieve (20 de enero de 1494); la muerte de Poliziano; la caída de Piero de Médici y
    // la entrada del rey de Francia (noviembre de 1494).
    //
    // Claves de escena: fiesta, campanas, luto, fuego (el carro de Pascua), nieve, guerra, obra. Arcos: capilla (los
    // frescos de la capilla Tornabuoni: 1 = descubiertos) y tabla (su primera tabla propia: 0 = la madera con yeso, 1 =
    // firmada). Estado: «campo» (temporada 3: seis semanas pintando una capilla en una iglesia del Mugello).
    //
    // Lo que el cuadro tiene que mostrar para contar lo mismo que el cuento: el taller con su mesa de moler, los cartones
    // y las tablas, con la de Iacopo según `tabla`; la cúpula de la catedral al fondo; y, mientras el estado es «campo»,
    // una iglesia de pueblo entre colinas en vez de la ciudad.

    private val F_GHIRLANDAIO = "Domenico Ghirlandaio" to "${W}Domenico_Ghirlandaio"
    private val F_TORNABUONI = "La capilla Tornabuoni" to "${W}Tornabuoni_Chapel"
    private val F_SAVONAROLA = "Girolamo Savonarola" to "${W}Girolamo_Savonarola"
    private val F_LORENZO = "Lorenzo de Médici" to "${W}Lorenzo_de%27_Medici"
    private val F_ALEJANDRO = "El papa Alejandro VI" to "${W}Pope_Alexander_VI"
    private val F_MIGUEL_ANGEL = "Miguel Ángel" to "${W}Michelangelo"
    private val F_COLON = "La carta de Colón" to "${W}Columbus%27s_letter_on_the_first_voyage"
    private val F_POLIZIANO = "Poliziano" to "${W}Poliziano"
    private val F_PIERO = "Piero de Médici" to "${W}Piero_di_Lorenzo_de%27_Medici"
    private val F_CARLOS = "Carlos VIII" to "${W}Charles_VIII_of_France"
    private val F_CARRO = "El carro de Pascua" to "${W}Scoppio_del_carro"
    private val F_SAN_JUAN = "San Juan, patrón de Florencia" to "${W}Nativity_of_Saint_John_the_Baptist"

    // Lo que cuelga de la Pascua juliana de 1490, 1491, 1492, 1493 y 1494.
    private val CARNAVAL_IACOPO = listOf("02-23", "02-15", "03-06", "02-19", "02-11")
    private val CUARESMA_IACOPO = listOf("02-24", "02-16", "03-07", "02-20", "02-12")
    private val PASCUA_IACOPO = listOf("04-11", "04-03", "04-22", "04-07", "03-30")

    /** Lo que vuelve cada año en la ciudad, en la temporada [t]. */
    private fun anioDeFlorencia(t: Int): List<DiaEspecial> = listOf(
        fiesta("carnaval", CARNAVAL_IACOPO[t - 1], "Martes de carnaval: carros, máscaras y canciones por las calles", certeza = HISTORICO),
        rito("cuaresma", CUARESMA_IACOPO[t - 1], "Miércoles de ceniza: se acaba la carne y empiezan los sermones", certeza = HISTORICO),
        fiesta("anunciacion", "03-25", "La Anunciación: en Florencia empieza el año", certeza = HISTORICO, fuente = F_ANUNCIACION),
        fiesta("pascua", PASCUA_IACOPO[t - 1], "Pascua: el carro de fuego estalla delante de la catedral", certeza = HISTORICO, fuente = F_CARRO, escena = "fuego"),
        fiesta("calendimaggio", "05-01", "El primero de mayo: ramas en flor y canciones bajo las ventanas", certeza = APROXIMADO),
        fiesta("san_juan", "06-24", "San Juan, el patrón: la procesión, las ofrendas y la carrera de caballos", 2, HISTORICO, F_SAN_JUAN),
        fiesta("asuncion", "08-15", "La Asunción: la ciudad se vacía hacia el campo", certeza = HISTORICO, fuente = F_ASUNCION),
        fiesta("san_lucas", "10-18", "San Lucas, el de los pintores: la cofradía come junta", certeza = APROXIMADO),
        fiesta("todos_santos", "11-01", "Todos los Santos y los difuntos", 2, HISTORICO, F_TODOS_SANTOS, escena = "campanas"),
        fiesta("navidad", "12-25", "Navidad: los belenes de las iglesias", certeza = HISTORICO, fuente = F_NAVIDAD, escena = "campanas"),
    )

    val IACOPO = Calendario(
        vida = "aprendiz",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(
            mes(despejado = 10, nubes = 10, lluvia = 7, nieve = 1, niebla = 3), mes(despejado = 11, nubes = 9, lluvia = 7, niebla = 1),
            mes(despejado = 13, nubes = 9, lluvia = 8, viento = 1), mes(despejado = 13, nubes = 9, lluvia = 8),
            mes(despejado = 16, nubes = 8, lluvia = 6, tormenta = 1), mes(despejado = 21, nubes = 5, lluvia = 2, tormenta = 2),
            mes(despejado = 26, nubes = 3, tormenta = 2), mes(despejado = 25, nubes = 3, tormenta = 3),
            mes(despejado = 19, nubes = 6, lluvia = 3, tormenta = 2), mes(despejado = 13, nubes = 9, lluvia = 8, tormenta = 1),
            mes(despejado = 9, nubes = 10, lluvia = 10, niebla = 1), mes(despejado = 10, nubes = 10, lluvia = 8, nieve = 1, niebla = 2),
        ),
        temporadas = listOf(
            TemporadaVida("1490", "capilla",
                "Iacopo muele su primer azul de ultramar. Fray Girolamo, el de Ferrara, vuelve a San Marcos. Le dejan pintar un ropaje en los frescos de la capilla Tornabuoni. Conoce a Caterina, la hija del vendedor de colores. Cobra su primer sueldo. Se descubren los frescos terminados.",
                anioDeFlorencia(1) + listOf(
                    suceso("ultramar", "01-18", "El maestro le deja moler, por primera vez, el azul de ultramar"),
                    mundo("savonarola_vuelve", "05-20", "Vuelve a San Marcos un fraile de Ferrara que predica raro: fray Girolamo", certeza = APROXIMADO, fuente = F_SAVONAROLA),
                    suceso("ropaje", "06-05", "Le dejan pintar un ropaje en los frescos de la capilla"),
                    suceso("caterina", "07-10", "Conoce a Caterina, la hija del vendedor de colores"),
                    suceso("sueldo", "09-12", "Cobra su primer sueldo: tres florines al año"),
                    mundo("capilla", "12-22", "Se descubren los frescos de la capilla Tornabuoni en Santa María Novella", certeza = APROXIMADO, fuente = F_TORNABUONI, escena = "fiesta"),
                )),
            TemporadaVida("1491", "savonarola",
                "Iacopo va a dibujar a los apóstoles de Masaccio en el Carmine. Fray Girolamo llena la catedral, porque en San Marcos ya no cabe la gente. El taller termina la Visitación. Iacopo se pelea con Masino y se reconcilian. Caterina le da una cinta.",
                anioDeFlorencia(2) + listOf(
                    suceso("carmine", "01-25", "Va a dibujar a los apóstoles de Masaccio en la iglesia del Carmine"),
                    mundo("catedral", "03-06", "Fray Girolamo predica en la catedral: en San Marcos ya no cabe la gente", certeza = APROXIMADO, fuente = F_SAVONAROLA),
                    mundo("visitacion", "05-28", "El taller termina la Visitación, y el maestro pone el año en la tabla", certeza = APROXIMADO, fuente = F_GHIRLANDAIO),
                    suceso("pelea", "07-20", "Se pelea a golpes con Masino por un pincel, y luego beben juntos"),
                    suceso("cinta", "09-29", "Caterina le da una cinta azul, a escondidas de su padre"),
                    mundo("nieve_dic", "12-10", "Nieva en Florencia y el taller no tiene leña", certeza = INVENTADO, escena = "nieve", clima = Clima.NIEVE),
                )),
            TemporadaVida("1492", "lorenzo",
                "Iacopo posa de pastor para una tabla del maestro. Un rayo cae en la cúpula de la catedral, y a los pocos días muere Lorenzo, el Magnífico. Se dice que a Miguel Ángel le rompieron la nariz. Iacopo pasa seis semanas pintando una capilla en el Mugello. Hay papa nuevo, un Borgia. Se casa Nanna, su hermana.",
                anioDeFlorencia(3) + listOf(
                    suceso("modelo", "01-22", "Posa de pastor para una tabla del maestro, con un cordero en brazos"),
                    mundo("rayo", "04-05", "Un rayo cae en la cúpula de la catedral", certeza = APROXIMADO, fuente = F_LORENZO, escena = "tormenta", clima = Clima.TORMENTA),
                    mundo("lorenzo", "04-09", "Murió Lorenzo, el Magnífico, en su villa de Careggi", certeza = APROXIMADO, fuente = F_LORENZO, escena = "luto"),
                    mundo("nariz", "05-30", "Se dice que a Miguel Ángel le rompió la nariz otro aprendiz, de un puñetazo", certeza = APROXIMADO, fuente = F_MIGUEL_ANGEL),
                    suceso("sale_campo", "06-28", "Sale con Bastiano a pintar una capilla en una iglesia del Mugello", cadena = "campo"),
                    suceso("vuelve_campo", "08-08", "Vuelve del Mugello, quemado del sol y con la capilla pintada", cadena = "campo"),
                    mundo("borgia", "08-17", "Hay papa nuevo: el cardenal Borgia", certeza = APROXIMADO, fuente = F_ALEJANDRO),
                    suceso("boda_nanna", "10-08", "Se casa Nanna, su hermana, con un tejedor de Oltrarno", escena = "fiesta"),
                )),
            TemporadaVida("1493", "colon",
                "Iacopo empieza su primera tabla propia: una Virgen pequeña para la casa de un sedero. Llega a Florencia la carta del genovés que cruzó el océano. Iacopo pide a Caterina en matrimonio. Miguel Ángel le regala un crucifijo de madera al prior del Santo Spirito. Muere el padre de Masino.",
                anioDeFlorencia(4) + listOf(
                    suceso("tabla", "03-10", "Empieza su primera tabla propia: una Virgen pequeña para la casa de un sedero"),
                    mundo("colon", "06-05", "Llega a Florencia la carta del genovés que cruzó el océano y halló islas", certeza = APROXIMADO, fuente = F_COLON),
                    suceso("promesa", "07-14", "Pide a Caterina en matrimonio y el padre de ella dice que cuando tenga taller"),
                    suceso("padre_masino", "09-02", "Muere el padre de Masino, y Iacopo lo acompaña al entierro", escena = "luto"),
                    mundo("crucifijo", "11-12", "Miguel Ángel le regala un crucifijo de madera al prior del Santo Spirito", certeza = APROXIMADO, fuente = F_MIGUEL_ANGEL),
                    mundo("peste_rumor", "12-12", "Se dice que hay peste en los barrios de abajo", certeza = INVENTADO),
                )),
            TemporadaVida("1494", "franceses",
                "Muere el maestro Domenico, de una fiebre de peste. Tras una nevada grande, Piero de Médici le encarga a Miguel Ángel una estatua de nieve. Davide le pide a Iacopo que se quede en el taller. Iacopo firma su primera tabla. Muere Poliziano. Echan a Piero de la ciudad y entra el rey de Francia.",
                anioDeFlorencia(5) + listOf(
                    mundo("muere_maestro", "01-11", "Muere el maestro Domenico, de una fiebre de peste", certeza = HISTORICO, fuente = F_GHIRLANDAIO, escena = "luto"),
                    mundo("estatua_nieve", "01-20", "Tras una nevada grande, Piero le encarga a Miguel Ángel una estatua de nieve", certeza = HISTORICO, fuente = F_MIGUEL_ANGEL, escena = "nieve", clima = Clima.NIEVE),
                    suceso("davide", "02-24", "Davide, el hermano del maestro, le pide que se quede en el taller"),
                    suceso("firma", "05-25", "Firma su primera tabla y se la entregan al sedero"),
                    suceso("anillo", "08-02", "Le da a Caterina un anillo de plata, aunque todavía no tenga taller"),
                    mundo("poliziano", "09-24", "Muere el poeta Poliziano", certeza = HISTORICO, fuente = F_POLIZIANO, escena = "luto"),
                    mundo("piero", "11-09", "Echan a Piero de Médici de la ciudad", certeza = HISTORICO, fuente = F_PIERO),
                    guerra("rey_francia", "11-17", "Entra en Florencia el rey de Francia con su ejército", certeza = HISTORICO, fuente = F_CARLOS, escena = "guerra"),
                )),
        ),
        arcos = listOf(
            Arco("capilla", TipoArco.OBRA, listOf(p(1, "01-01", 0.85), p(1, "12-22", 1.0))),
            Arco("tabla", TipoArco.OBRA, listOf(p(4, "03-10", 0.0), p(5, "05-25", 1.0))),
        ),
        objetos = listOf("pincel", "losa", "ultramar", "huevo", "yeso", "tabla", "carboncillo", "carton", "pan_oro", "libro", "florin", "espejo"),
        estados = listOf(Estado("campo", "sale_campo", "vuelve_campo")),
    )
}
