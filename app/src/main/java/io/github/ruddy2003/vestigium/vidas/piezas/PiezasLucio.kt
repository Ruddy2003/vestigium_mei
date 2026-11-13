package io.github.ruddy2003.vestigium.vidas.piezas

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Entorno
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Genero
import io.github.ruddy2003.vestigium.vidas.ObjetoCuento
import io.github.ruddy2003.vestigium.vidas.Parrafo
import io.github.ruddy2003.vestigium.vidas.Piezas
import io.github.ruddy2003.vestigium.vidas.TextoEspecial
import io.github.ruddy2003.vestigium.vidas.Variante

/*
 * El cuento de Lucio, zapatero de Roma en tiempos de Trajano. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el
 * autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Vive en el quinto piso de una ínsula, donde no se puede cocinar; su taller da a la calle. Va a las termas, al foro y
 *   a los juegos. Roma es enorme, ruidosa y de todas partes.
 * - No repite lo que ya dice «Su vida»: el trigo del Estado, las termas por una moneda y los cien días de fiesta.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG1.kt`):
 * - Félix es aprendiz desde `felix` (temporada 1) hasta que abre su propio puesto (`puesto_felix`, temporada 5).
 * - Marco va a la escuela desde `escuela` (temporada 1) y trabaja en el taller por las tardes desde `marco_taller` (5).
 * - Sexto es marinero hasta `sexto_queda` (temporada 4); desde entonces trabaja en el taller.
 * - Tito nace en la temporada 4 (`nace_tito`); el embarazo de Tertia va de `espera_tertia` (3) a `nace_tito`.
 * - Las fiestas que vuelven cada año no nombran a Félix, a Sexto ni a Tito.
 */
internal object PiezasLucio {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta con el ruido de los carros, que en Roma solo pueden entrar de noche y a esta hora todavía pasan, cargados, haciendo temblar la ínsula. Baja a tientas los cinco pisos de escalera, que huele a humo y a cosas peores, y sale a una calle que ya está llena de gente."),
        v("La primera luz entra por la ventana sin vidrio, que se cierra con un postigo de madera, y con ella los gritos de la calle: el panadero, el aguador, un vecino que discute con otro de balcón a balcón. {nombre} se lava la cara en un cubo y se pone la túnica, que es la de ayer y será la de mañana."),
        v("Lo despierta el maestro de la escuela de la esquina, que empieza a gritarles a los niños antes de que salga el sol. {nombre} se queda un momento tumbado, oyéndolo recitar las letras, y piensa que ese hombre se gana la vida gritando y él cosiendo, y no sabe cuál de los dos tiene el peor oficio."),
        v("En casa no se cocina, por miedo al fuego: el desayuno es pan de ayer mojado en vino aguado y un puñado de aceitunas, comido de pie. Tertia ya está despierta, peinando a Lucila, que protesta. {nombre} les da un beso a las dos en la cabeza, y baja."),
        v("{nombre} baja a la calle cuando abren las tiendas. Los tenderos sacan sus mesas a la acera y la calle se estrecha hasta que no cabe un burro. Él levanta la persiana de madera del taller y cuelga una sandalia en la puerta, para que se sepa qué se vende."),
        v("Antes de abrir el taller, {nombre} saluda a los lares del cruce, una hornacina pintada en la esquina con dos figuritas que bailan, y les deja una miga de pan. Lo hace todo el barrio, y así los lares están gordos y el barrio tranquilo."),
        v("Desde la ventana de la ínsula se ven tejados rojos hasta donde alcanza la vista, y humo, y entre el humo, las colinas con los templos encima. {nombre} se asoma un momento, como cada mañana, para ver si la ciudad sigue ahí. Sigue, y hace el mismo ruido que ayer."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En Roma el tiempo es lo de menos: lo que importa es si hay pan, si hay agua en la fuente y si hay juegos."),
        v("{nombre} mira el cielo desde la puerta del taller, como lo miran los zapateros: pensando en el barro. {cielo} Con barro se gastan las suelas, y con suelas gastadas vienen los clientes."),
        v("{de_estacion} {cielo} Los viejos del barrio dicen que antes los inviernos eran más duros y los veranos más sanos, y nadie les lleva la contraria, porque los viejos de Roma hablan mucho y muy alto."),
        v("{cielo} En {estacion} Roma huele de una manera distinta, y {nombre} la reconocería con los ojos cerrados. Ahora huele a lo de siempre, más un poco de {estacion}."),
        v("Lo primero que hace al salir es mirar hacia el Capitolio, que se ve al final de la calle si no hay niebla. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En la calle se habla del tiempo como de los precios: todos se quejan y nadie hace nada."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio sobre los tejados, de un azul que hace brillar los mármoles de los templos.",
            "Hace un día claro, y desde lo alto de la ínsula se ven los montes de fuera de la ciudad.",
            "No hay una nube, y el sol cae a plomo sobre la calle, que no tiene una sombra libre.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris como el agua del Tíber.",
            "Hay nubes bajas que se enganchan en las colinas.",
            "Está nublado, y la ciudad entera parece de plomo.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el agua baja por la calle arrastrando todo lo que la gente tiró anoche.",
            "Cae una lluvia fina, y las piedras de la calle resbalan como si las hubieran untado.",
            "Llueve, y los toldos de las tiendas chorrean sobre los que pasan.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos retumban entre las colinas, y la gente dice que Júpiter está de malas.",
            "El cielo se ha puesto negro sobre el Capitolio, y el primer trueno vacía la calle.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla del río, tan espesa que las ínsulas desaparecen a partir del segundo piso.",
            "La niebla tapa el Tíber, y los barqueros se gritan para no chocar.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del norte, el que barre el humo y deja ver la ciudad entera.",
            "Hay viento, y los toldos de las tiendas dan latigazos.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en los balcones de las ínsulas florecen las macetas que nadie riega.",
            "En primavera vuelven los barcos al mar, y a Roma llegan otra vez noticias de todas partes.",
            "Es primavera, la estación de las fiestas de flores y de los resfriados.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, cuando el calor se queda atrapado en las calles estrechas y no se va ni de noche.",
            "En verano los ricos se van a sus casas del campo, y la ciudad se queda para los demás.",
            "Es pleno verano, la estación de las fiebres, y en el barrio se cuida más a los niños.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y llega el vino nuevo, que todavía no es vino del todo.",
            "En otoño se cierran los mares, y los barcos del trigo se quedan en puerto hasta la primavera.",
            "Es otoño, y las calles huelen a castañas asadas y a humo.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando en las ínsulas se pasa frío sin remedio: no hay hogar y el brasero es peligroso.",
            "En invierno se trabaja con las manos frías y se cobra lo mismo.",
            "Es invierno, y anochece cuando uno apenas ha empezado el trabajo de la tarde.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora corta suelas. Pone la plantilla de madera sobre el cuero, la sigue con la cuchilla de media luna y separa la pieza de un tirón. Cada pie de Roma es distinto, y él recuerda casi todos por su dueño.", hacer = Hacer.TRABAJO),
        v("A esta hora cose: agujerea el cuero con la lezna, pasa el hilo encerado y aprieta, punto tras punto, sentado en su taburete bajo con el zapato sobre las rodillas. Desde la calle lo ven trabajar, y algunos se paran a mirar como si fuera un espectáculo.", hacer = Hacer.TRABAJO),
        v("A esta hora clava tachuelas en la suela de una caliga, una bota de soldado, en filas que tienen que quedar derechas. Las tachuelas agarran en el barro y en la piedra, y se oyen desde lejos: por el ruido se sabe cuándo viene un soldado.", hacer = Hacer.TRABAJO),
        v("A esta hora atiende a una clienta que quiere unas sandalias como las de su vecina, pero mejores, y más baratas. {nombre} le mide el pie con una tira de cuero, le dice un precio y escucha la respuesta, que es otro precio. Así empieza toda venta en Roma.", hacer = Hacer.TRABAJO),
        v("A esta hora le enseña a Félix a coser una costura que no se vea, que es la más difícil. El chico tiene buenas manos y poca paciencia. {nombre} le deshace la costura una vez, y otra, y a la tercera no se la deshace, aunque no esté bien del todo.", hacer = Hacer.TRABAJO, desde = "felix", hasta = "puesto_felix"),
        v("A esta hora trabaja con Sexto, su hermano, que dejó el mar y ahora corta cuero en el taller. Sexto cose como se cosen las velas, con puntadas grandes y fuertes, y {nombre} se las rehace sin decirle nada. Sexto lo sabe, y lo deja.", hacer = Hacer.TRABAJO, desde = "sexto_queda"),

        v("A esta hora come en el puesto de Primigenia, el de la esquina, que tiene un mostrador de mármol con ollas metidas dentro: garbanzos, lentejas, un guiso de algo. Se come de pie, con el codo en el mostrador, y se paga con una moneda pequeña.", hacer = Hacer.COMER),
        v("A esta hora come pan con queso de cabra y unos higos secos, sentado en el umbral del taller. Le echa al pan un chorro de salsa de pescado, que todo lo arregla. Pasa un perro, lo mira, y {nombre} le da la corteza, que es lo que se merece un perro de Roma.", hacer = Hacer.COMER),
        v("A esta hora come con la familia lo que Tertia compró en la calle, porque en la ínsula no se puede encender fuego: gachas de trigo, pan, aceitunas y un poco de vino aguado. Se come sentado en el suelo del único cuarto, y se habla todos a la vez.", hacer = Hacer.COMER),

        v("A esta hora está en las termas del barrio. Se raspa el sudor con el estrígilo, se mete en el agua caliente hasta el cuello y escucha lo que se dice, que es mucho y casi todo mentira. Luego el agua fría, que no le gusta a nadie y que todos se meten igual.", hacer = Hacer.GENTE),
        v("A esta hora juega a la pelota en el patio de las termas con otros del barrio, en camisa, hasta sudar. Luego se baña. Un vendedor de salchichas pasa gritando entre los bañistas, otro vende pasteles, y un tercero arranca pelos de las axilas y grita más que los dos.", hacer = Hacer.GENTE),
        v("A esta hora está sentado en el agua tibia de las termas con dos vecinos, hablando de las carreras. Uno es de los verdes y otro de los azules, y {nombre} es de los que no discuten, que es otro bando.", hacer = Hacer.GENTE),

        v("A esta hora pasea por el foro, entre las estatuas de los que mandaron, mirando a los que mandan ahora. Hay abogados que hablan para que los oiga todo el mundo, y gente que se para a oírlos aunque no entienda nada. {nombre} se para también, un rato.", hacer = Hacer.LIBRE),
        v("A esta hora se sienta en las gradas de un templo a ver pasar la ciudad: senadores en litera, esclavos con recados, soldados, extranjeros de todos los colores. En Roma mirar no cuesta, y es de lo poco que no se paga.", hacer = Hacer.LIBRE),
        v("A esta hora lee los letreros pintados en las paredes, o hace que los lee: anuncios de juegos, de casas en alquiler, de candidatos, y alguna grosería. {nombre} sabe leer lo justo para saber cuáles son groserías, y se ríe de esas.", hacer = Hacer.LIBRE),

        v("A esta hora duerme en el cuarto de la ínsula, en un camastro, con la familia alrededor. Por la ventana entra el estruendo de los carros, que en Roma solo pueden circular de noche. {nombre} ya no los oye; si un día no pasaran, se despertaría.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, o casi, porque en una ínsula siempre hay ruido: en el piso de arriba alguien camina, en el de abajo alguien ronca, y al lado un niño llora. En una ínsula se duerme con mucha gente que uno no conoce.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, como duermen los que trabajan sentados todo el día. Tertia dice que se le oyen los dedos, que cose dormido. Él dice que no, y ella dice que los ha oído.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La calle de {nombre} es estrecha y torcida, con ínsulas de cinco pisos a los lados que casi se tocan por arriba. Abajo están las tiendas: un panadero, un barbero, un vendedor de vino, el puesto de Primigenia y su taller. {entorno}"),
        v("{entorno} Roma tiene tanta gente que no cabe en ella: se dice que un millón, aunque nadie la ha contado entera. Por la calle hay que abrirse paso a codazos, y el que se para a mirar algo se queda sin bolsa."),
        v("Por los acueductos llega a Roma más agua de la que se puede beber, y corre día y noche por las fuentes de las esquinas. Las mujeres del barrio hacen cola con los cántaros y se cuentan todo lo que pasa. Quien quiere saber algo, va a la fuente. {entorno}"),
        v("En el barrio el que no es de Roma es de otra parte, y casi nadie es de Roma: hay sirios, griegos, galos, egipcios y gente de sitios que {nombre} no sabría señalar. Todos dicen que son romanos, y a su manera tienen razón. {entorno}"),
        v("{entorno} Los ricos viven en las colinas, en casas con patio y fuente; los demás, en las ínsulas, que se caen o se queman con una frecuencia que nadie discute. Cuanto más arriba se vive, más barato y más lejos del agua."),
        v("En la esquina está el altar de los lares del barrio, y cada barrio tiene sus jefes, sus fiestas y sus peleas con el de al lado. {nombre} es de su barrio como otros son de su patria, y del de al lado no se fía. {entorno}"),
    )

    /** Lo que se ve alrededor según cómo van las obras del foro del emperador. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("foro", 0.999, frases = listOf(
            "En el foro nuevo se levanta la columna del emperador, con la guerra de Dacia enroscada hasta arriba, y la gente se tuerce el cuello para verla.",
            "El foro nuevo es lo más grande que ha visto nadie, y el barrio entero va a pasear por él.",
        )),
        Entorno("foro", 0.92, 0.999, frases = listOf(
            "El foro nuevo del emperador ya está abierto, y la gente va a verlo como quien va a ver el mar.",
            "En el foro nuevo todavía trabajan en una columna altísima, cubierta de andamios.",
        )),
        Entorno("foro", 0.0, 0.92, frases = listOf(
            "Las obras del foro del emperador se oyen desde lejos: martillos, poleas y carros de mármol día y noche.",
            "Por las calles que van al foro nuevo pasan columnas enteras, tumbadas en carros tirados por diez bueyes.",
        )),
    )

    private val GENTE = listOf(
        v("Tertia, su mujer, vende cintas y botones en un cesto a la puerta del taller, y sabe más de los vecinos que el propio barrio. Cuando {nombre} cobra poco, ella lo mira; cuando cobra mucho, también, pero de otra manera."),
        v("Lucila tiene la edad de preguntar por qué a todo y de no esperar la respuesta. Se pasa el día en el taller, jugando con los recortes de cuero, y le hace zapatos a una muñeca de trapo que ya tiene más zapatos que nadie en la ínsula."),
        v("Primigenia, la del puesto de comida de abajo, es viuda, grita como un centurión y fía a quien le cae bien. A {nombre} le cae bien desde que le arregló unas sandalias sin cobrarle. Desde entonces le pone más garbanzos que a nadie."),
        v("El vecino de enfrente es un barbero que afeita en la calle, en un taburete, con una navaja que da miedo y una conversación que da más. Entre cliente y cliente se asoma al taller a contarle algo, y {nombre} cose y dice que sí."),
        v("El casero de la ínsula manda cada mes a un esclavo a cobrar, y el esclavo sube los cinco pisos con cara de que le deben a él. {nombre} paga, protesta por el agua, que no sube, y el esclavo apunta la queja en una tablilla que nadie lee."),
        v("Marco anda por la calle con otros chicos todo el día, haciendo lo que hacen los chicos en Roma: nada bueno y nada malo del todo. {nombre} lo llama desde el taller cada tanto, para saber que sigue vivo.", hasta = "escuela"),
        v("Marco va cada mañana a la escuela de la esquina, con su tablilla de cera y su punzón, y vuelve sabiendo letras que su padre no sabe. Se las enseña a Lucila por la tarde, y {nombre} escucha desde el taller, haciendo como que no.", desde = "escuela", hasta = "marco_taller"),
        v("Marco ya trabaja en el taller por las tardes, después de la escuela, y corta suelas con más ganas que tino. {nombre} le corrige la mano igual que se la corrigió a él su padre, con un golpe en los nudillos que no duele.", desde = "marco_taller"),
        v("Félix, el aprendiz, es hijo de un liberto que fue esclavo de un senador, y habla de su padre como si el senador fuera él. Tiene buenas manos y la cabeza en las carreras. {nombre} le paga con comida y con oficio, que es lo que le pagaron a él.", desde = "felix", hasta = "puesto_felix"),
        v("Félix tiene ahora su propio puesto de remiendos en la esquina, y le manda a {nombre} los clientes que no sabe atender. {nombre} dice que es la competencia. Lo dice con orgullo, aunque haga como que es una queja.", desde = "puesto_felix"),
        v("Sexto, su hermano, es marinero en los barcos del trigo de Alejandría y pasa media vida en el mar. Cuando viene trae cosas raras —dátiles, una tela, un dios pequeño— y duerme en el suelo de la ínsula hasta que el barco vuelve a salir.", hasta = "sexto_queda"),
        v("Sexto, su hermano, dejó el mar y ahora vive con ellos y trabaja en el taller. Cose como se cosen las velas y cuenta historias de Alejandría que crecen cada vez. Lucila se las cree todas, y {nombre}, la mitad.", desde = "sexto_queda"),
        v("Tertia espera otro hijo, y las vecinas le traen amuletos, consejos y una partera que, dicen, no ha perdido a ninguno. {nombre} sube por ella el agua de la fuente los cinco pisos, que es lo único que ella le deja hacer.", desde = "espera_tertia", hasta = "nace_tito"),
        v("Tito, el pequeño, duerme en una cesta en un rincón del taller, entre los recortes de cuero, y ya tiene más zapatos que pies. {nombre} lo mira entre puntada y puntada y piensa que este, por lo menos, no va a andar descalzo nunca.", desde = "nace_tito"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del oficio: con las manos antes que con la cabeza."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En el barrio se dice que el zapatero siempre va mal calzado; lo dicen los que no le pagan."),
        v("{dato_objeto} Son cosas que se aprenden mirando al maestro, como casi todo en el oficio."),
        v("Un artesano de Roma tiene pocas cosas, y cada una le dio de comer alguna vez. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("lezna", "la lezna", "una lezna", "La lezna",
            "La lezna es un punzón de hierro con mango de hueso; con ella se abren los agujeros por donde pasa el hilo, y en un taller se pierde tres veces al día y aparece siempre debajo del zapatero."),
        ObjetoCuento("horma", "la horma", "una horma", "La horma",
            "La horma es un pie de madera, tallado a la medida de un cliente o de un pie cualquiera; sobre ella se monta el cuero para darle forma, y en el taller hay una fila de hormas que parecen los pies de una gente que se fue."),
        ObjetoCuento("cuchilla", "la cuchilla de media luna", "una cuchilla de media luna", "La cuchilla",
            "La cuchilla de zapatero tiene forma de media luna y corta el cuero empujando, no tirando; si está bien afilada corta como si nada, y si no, corta los dedos."),
        ObjetoCuento("cuero", "el cuero", "un trozo de cuero", "El cuero",
            "El cuero viene de las curtidurías del otro lado del río, que se huelen desde lejos porque curten con orines y cortezas; el de buey es para las suelas, el de cabra para lo de arriba, y el bueno se reconoce con los dedos."),
        ObjetoCuento("sandalia", "la sandalia", "una sandalia", "La sandalia",
            "Las sandalias de Roma no son todas iguales: las de casa son ligeras, las de calle llevan suela gruesa, y las de los ricos llevan tantas tiras que tardan en ponérselas lo que otros en caminar hasta el foro."),
        ObjetoCuento("caliga", "la caliga", "una caliga", "La caliga",
            "La caliga es la bota del soldado: una suela gruesa llena de tachuelas y un enrejado de tiras que deja los dedos al aire; los legionarios caminan con ellas de un extremo del imperio al otro, y vuelven a pedir otras."),
        ObjetoCuento("cerote", "el cerote", "un cerote", "El cerote",
            "El cerote es una mezcla de cera y pez con la que se untan los hilos de coser, para que no se pudran y entren mejor; deja las manos negras y pegajosas, y por eso a los zapateros se los reconoce en las termas."),
        ObjetoCuento("lucerna", "la lucerna", "una lucerna", "La lucerna",
            "La lucerna es una lamparita de barro con un pico para la mecha y aceite dentro; da una luz pequeña y humo, y en una ínsula es lo único que se permite encender, y aun eso con miedo."),
        ObjetoCuento("jarra_vino", "la jarra de vino", "una jarra de vino", "El vino",
            "El vino de los pobres de Roma es áspero y se bebe siempre aguado; sin agua se emborracha uno como un bárbaro, y con mucha agua no se emborracha nunca, que es otro defecto."),
        ObjetoCuento("pan", "el pan", "un pan", "El pan",
            "El pan de Roma se compra hecho, en la tahona de la esquina, redondo y marcado en ocho partes antes de cocerlo; hay pan blanco para los que pueden y pan oscuro para los demás, y en su casa el blanco es de fiesta."),
        ObjetoCuento("moneda", "la moneda", "una moneda", "La moneda",
            "La moneda de todos los días es el as, de cobre, con la cara del emperador de un lado; con un as se paga el baño, con dos un vaso de vino, y con muchos, un par de sandalias de las que él hace."),
        ObjetoCuento("tablilla", "la tablilla de cera", "una tablilla de cera", "La tablilla",
            "La tablilla de cera es una tablita con una capa de cera donde se escribe con un punzón y se borra con la otra punta; en ella apunta lo que le deben, que siempre es más de lo que debe él."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en Roma como se piensa en el tiempo: no se la elige, se vive en ella. Es demasiado grande, demasiado ruidosa y demasiado cara. A sus {edad} años, no se iría a ningún otro sitio."),
        v("Piensa a veces en lo raro de su oficio: toda Roma camina sobre algo que hizo alguien como él, y nadie se mira los pies. Senadores, soldados, ladrones: todos llevan suelas. Le gustaría que alguno, alguna vez, mirara para abajo."),
        v("{mundo} Él piensa en cosas más cercanas: en la renta, en el cuero que subió, en la tos de Lucila. Las cosas grandes, dicen en las termas, son para los que tienen tiempo, y en Roma el tiempo es de los ricos."),
        v("{mundo} A sus {edad} años, {nombre} ha perdido a dos hijos pequeños en dos veranos de fiebre. En Roma eso le pasa a casi todo el mundo, y casi todo el mundo lo cuenta como si le hubiera pasado solo a él. Él no lo cuenta."),
        v("No tiene tierra ni la quiere: la tierra es para los que tienen esclavos. Tiene un taller, unas manos y un barrio, y hay días en que eso le parece mucho. Su padre nació esclavo, y a su padre le habría parecido todo."),
        v("{mundo} A veces, por la noche, piensa en los dioses, que en Roma son tantos que cada calle tiene los suyos. {nombre} les deja a todos un poco de algo, por si acaso, y espera que no se peleen por las migas."),
    )

    private val MUNDOS = mapOf(
        "obras" to listOf(
            "En el barrio se habla de las obras del emperador, que no se acaban nunca.",
            "Dicen que el emperador está construyendo el foro más grande del mundo, con el oro de la Dacia.",
            "Se dice que en las obras del foro trabajan más hombres que en una legión.",
        ),
        "paz" to listOf(
            "Este año no se habla de guerras, sino de juegos y de precios.",
            "En las termas se dice que el emperador es bueno, y lo dicen hasta los que no lo han visto nunca.",
        ),
        "foro" to listOf(
            "Todo el mundo habla del foro nuevo, como si fuera suyo.",
            "Se dice que el emperador ha hecho de Roma la ciudad más hermosa del mundo, y casi nadie le lleva la contraria.",
        ),
        "guerra" to listOf(
            "Se habla de los partos, que casi nadie sabe dónde están.",
            "En las termas se cuentan victorias que todavía no han llegado.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando oscurece, {nombre} cierra el taller con la persiana de madera y sube los cinco pisos de la ínsula, que de noche están oscuros como un pozo. Arriba, Tertia ha encendido la lucerna y ha comprado algo caliente en la calle. Se cena con los dedos, y bien."),
        v("Al caer la tarde la calle cambia: cierran las tiendas, abren las tabernas y entran los carros que de día no pueden. {nombre} sube a casa con el ruido de las ruedas detrás, y se asoma un momento a la ventana a ver la ciudad, que se enciende a trozos."),
        v("Anochece sobre Roma, y en las colinas se encienden las antorchas de las casas ricas, mientras en el barrio solo brillan las lucernas. {nombre} se sienta en el alféizar con un vaso de vino aguado y mira el humo de toda la ciudad subir derecho, si no hay viento."),
        v("De noche Roma es peligrosa: hay ladrones, borrachos y cosas que caen de las ventanas. {nombre} no sale si no hace falta. Se queda en casa contando lo que ha ganado en el día, que no tarda mucho, y apaga la lucerna antes de que se acabe el aceite."),
        v("Se cena en silencio, porque la boca está ocupada. Después Tertia cuenta lo que se dice en la fuente y {nombre} lo que se dice en las termas, y casi siempre es lo mismo con otras palabras. Luego se cierra el postigo, por los mosquitos y por los vecinos."),
        v("La última luz se queda un rato en lo alto del Capitolio, en los tejados dorados de los templos, cuando la calle ya está a oscuras. {nombre} la mira desde la ventana. Esa luz es del emperador, piensa, y también un poco suya, porque nadie se la cobra."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "compitales" to unDia(
            Parrafo.MANANA to "Hoy son las Compitales, la fiesta de los lares del barrio. En el cruce de calles, junto a la hornacina, cada casa cuelga muñecos de lana, uno por cada persona libre, y bolas de lana por cada esclavo. Los lares cuentan así a quién tienen que cuidar.",
            Parrafo.PUEBLO to "El barrio entero se junta en el cruce para el sacrificio, y después hay juegos y comida en la calle. Los jefes del barrio, que son libertos, llevan toga y se dan importancia por un día. Hoy hasta los esclavos tienen permiso para estar contentos.",
            Parrafo.GENTE to "Tertia ha hecho un muñeco de lana para cada uno de la casa, con un trapo de color para distinguirlos. El de {nombre} tiene la barba de lana negra, y Lucila dice que se le parece. Los cuelgan juntos en la esquina, apretados, como duermen.",
        ),
        "lupercales" to unDia(
            Parrafo.MANANA to "Hoy son las Lupercales. Al pie del Palatino, en la cueva donde la loba crio a Rómulo y Remo, los sacerdotes sacrifican unas cabras y un perro. Después, unos jóvenes casi desnudos salen corriendo con tiras de piel de las cabras en la mano.",
            Parrafo.PUEBLO to "Los lupercos corren por las calles azotando a quien se cruza con las tiras de piel, y las mujeres se ponen en su camino a propósito, porque dicen que el golpe da hijos. La gente se ríe, grita y se aparta, todo a la vez.",
            Parrafo.PENSAMIENTO to "{nombre} mira correr a los lupercos y piensa que hay fiestas que nadie entiende y que todo el mundo celebra. Le pregunta a un vecino para qué sirve, y el vecino le dice que para que Roma siga siendo Roma. Le parece una buena respuesta.",
        ),
        "matronales" to unDia(
            Parrafo.MANANA to "Hoy son las Matronales, la fiesta de las madres de familia. Los maridos rezan por sus mujeres y les hacen regalos, y en las casas que tienen esclavos las señoras les sirven la comida a ellos. En casa de {nombre} no hay esclavos, así que solo queda el regalo.",
            Parrafo.GENTE to "{nombre} le regala a Tertia unas sandalias nuevas, que es lo que sabe hacer, con una tira teñida de rojo que le costó más que todo lo demás. Tertia dice que no hacía falta, se las pone en el acto y no se las quita en todo el día.",
            Parrafo.ANOCHECER to "Esta noche Tertia no hace nada, por un día, y {nombre} compra la cena y la sirve, y se equivoca en todo. Los niños se ríen de él. Tertia también, desde su sitio, con los pies nuevos cruzados uno encima del otro.",
        ),
        "quinquatrias" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las Quinquatrias, cinco días de fiesta para Minerva, la diosa de los oficios y de la sabiduría. Los artesanos le rezan para que las manos no les fallen, y los maestros cierran la escuela, que es lo que más celebran los niños.",
                Parrafo.PUEBLO to "En el templo de Minerva, en el Aventino, hay ofrendas de todos los oficios: un martillo pequeño, una lanzadera, una sandalia de juguete. {nombre} deja una lezna vieja, gastada, la primera que tuvo. Es poca cosa, pero Minerva sabe de herramientas.",
                Parrafo.GENTE to "Los niños del barrio andan sueltos por las calles, haciendo ruido para que se note que no hay escuela. Marco y sus amigos juegan a las nueces en la acera del taller. {nombre} los deja, y de vez en cuando les gana una nuez.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días hay gladiadores en la arena, que Minerva también es diosa de la guerra. La gente va a verlos y vuelve discutiendo quién debió ganar.",
                "Mientras duran las Quinquatrias los talleres trabajan menos, y los artesanos se reúnen en sus cofradías a comer y a beber en honor de la diosa. La de los zapateros tiene su sede en un cuarto detrás de una taberna, y no es casualidad.",
                "El último día de las Quinquatrias se purifican las trompetas de los sacrificios, y se oyen sonar por toda la ciudad. Al día siguiente los niños vuelven a la escuela, y los maestros a gritar.",
            )),
        ),
        "palilias" to unDia(
            Parrafo.MANANA to "Hoy son las Palilias, el cumpleaños de Roma. Era una fiesta de pastores, de cuando Roma era un puñado de chozas en una colina, y todavía se hace como entonces: se limpian los establos, se queman paja y hierbas, y se salta por encima del fuego.",
            Parrafo.PUEBLO to "En la ciudad ya no quedan ovejas, pero las hogueras se encienden igual, en las plazas y en los cruces, y los jóvenes saltan por encima para purificarse. Siempre hay alguno que se chamusca la túnica, y todos aplauden.",
            Parrafo.ANOCHECER to "Esta noche se come en la calle, con música, y se brinda por Roma, que cumple tantos años que hay que ser escriba para contarlos. {nombre} brinda también. Piensa que su ciudad es vieja como una abuela y ruidosa como un niño, y que por eso la quiere.",
        ),
        "florales" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las Florales, seis días para Flora, la diosa de las flores. La gente se viste de colores, en vez del blanco de otras fiestas, y se pone coronas de flores en la cabeza. En las puertas de las casas cuelgan guirnaldas.",
                Parrafo.PUEBLO to "En el circo se sueltan liebres y cabras, que son animales de huerto y no de guerra, y la gente se ríe viéndolas correr. En los teatros hay farsas que no se pueden contar delante de los niños, y por eso los niños se las saben todas.",
                Parrafo.GENTE to "Tertia le pone a Lucila una corona de flores que le queda grande y se le cae sobre los ojos. La niña camina así toda la mañana, a ciegas, chocando con todo, feliz. {nombre} la guía desde atrás con una mano en el hombro.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días por la calle se le tiran garbanzos y habas a la gente, que es costumbre de Flora y trae abundancia. Hay quien los recoge y se los lleva a casa para la cena.",
                "Mientras duran las Florales las calles huelen a flores pisadas y a vino derramado. Las tabernas están llenas desde temprano, y los ediles hacen como que no ven.",
                "En el teatro hay mimos todos los días, y la gente hace cola desde el alba para conseguir sitio. {nombre} no consigue sitio nunca, y se entera de todo por lo que le cuentan.",
            )),
        ),
        "vestalias" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las Vestalias, los días de Vesta, la diosa del fuego del hogar. Se abre el almacén sagrado de su templo, que está cerrado todo el año, y las mujeres de Roma van descalzas a llevarle ofrendas. A los hombres no se los deja entrar.",
                Parrafo.PUEBLO to "Estos días los molineros y los panaderos están de fiesta, porque Vesta es la del fuego del horno. Los burros que mueven las piedras de los molinos descansan, y los adornan con guirnaldas de flores y con panes colgados del cuello.",
                Parrafo.PENSAMIENTO to "{nombre} ve pasar un burro de molino coronado de flores y con un collar de panes, con la misma cara de cansancio de siempre. Piensa que es como la gente en las fiestas: adornada por fuera, molida por dentro. Le da al burro una corteza, y el burro no le da las gracias.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días Tertia va con otras mujeres del barrio al templo de Vesta, descalza, con una ofrenda de pan y sal. Vuelve con los pies sucios y la cara tranquila, y no cuenta nada de lo que vio.",
                "Mientras duran las Vestalias el pan sale mejor, dicen las panaderas, porque el fuego está contento. Las clientas dicen que sale igual, pero más caro.",
                "El último día de las Vestalias se barre el templo de Vesta y la basura se tira al Tíber, que se la lleva al mar. Hasta entonces no es buen tiempo para casarse, y en el barrio las bodas esperan.",
            )),
        ),
        "naves_trigo" to unDia(
            Parrafo.MANANA to "Hoy llegó la noticia de que la flota del trigo de Alejandría entró en Ostia. Son barcos enormes, panzudos, que traen en las bodegas el pan de media Roma, y en el barrio se nota: el panadero sonríe y dice que mañana bajará el precio.",
            Parrafo.PUEBLO to "De Ostia el trigo sube por el Tíber en barcazas tiradas por bueyes desde la orilla, hasta los almacenes del puerto de la ciudad. Allí lo pesan, lo apuntan y lo guardan. Lo que no se pierde por el camino acaba en el pan de todos.",
            Parrafo.GENTE to "Hay quien tiene un hermano o un hijo a bordo y se va a Ostia a esperarlo, y quien no tiene a nadie y va igual, a ver los barcos. Primigenia dice que el trigo de Egipto es mejor que el de Sicilia, y nunca ha comido ninguno de los dos por separado.",
        ),
        "vinalias" to unDia(
            Parrafo.MANANA to "Hoy son las Vinalias del campo, cuando se le pide a Júpiter que cuide las viñas antes de la vendimia. En la ciudad no hay viñas, pero hay vino, y eso basta para celebrar. Las tabernas abren antes y cierran después.",
            Parrafo.PUEBLO to "El sacerdote de Júpiter corta el primer racimo en una viña de las afueras, y hasta que no lo corta nadie puede vendimiar. En el barrio se cuenta, como cada año, que una vez se le olvidó y que ese año el vino salió avinagrado.",
            Parrafo.ANOCHECER to "Esta noche en la ínsula se bebe vino del bueno, del que se guarda para las fiestas, y alguien canta en el piso de abajo canciones de su tierra que nadie entiende. {nombre} se queda dormido sentado, con la cabeza contra la pared y el vaso en la mano.",
        ),
        "juegos_romanos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan los Juegos Romanos, quince días de fiesta. Sale la procesión del Capitolio al Circo Máximo: los magistrados en carro, los jóvenes a caballo, los atletas, los músicos, los bailarines y, al final, en andas, las estatuas de los dioses.",
                Parrafo.PUEBLO to "En el Circo Máximo cabe más gente que en muchas ciudades enteras. Hoy está lleno hasta arriba, y el ruido se oye desde el barrio de {nombre}, que está lejos. Cuando una cuadriga choca, el grito llega hasta las ínsulas.",
                Parrafo.GENTE to "{nombre} cierra el taller a mediodía y se va con Marco al circo, a las gradas de arriba, que son las más baratas y las más lejanas. Desde ahí los caballos son del tamaño de hormigas, pero el ruido es el mismo. Marco grita por los verdes, porque gritan todos sus amigos.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días hay carreras en el circo y teatro en los teatros, y la ciudad entera anda de fiesta. Por la tarde las calles del barrio están medio vacías, y los ladrones lo saben.",
                "En las tabernas se apuesta por los colores del circo: verdes, azules, rojos y blancos. Hay quien pierde la paga de un mes en una tarde, y vuelve al día siguiente a recuperarla.",
                "Estos días los aurigas famosos son más conocidos que el emperador, y sus nombres están pintados en las paredes del barrio. Los niños juegan a las carreras con carros de caña tirados por perros.",
                "Los Juegos traen gente de fuera, y la gente de fuera necesita zapatos. Estos días {nombre} remienda más sandalias de las que hace, y cobra más de lo que suele, porque los de fuera no saben lo que vale nada.",
            )),
        ),
        "juegos_plebeyos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan los Juegos Plebeyos, que son de la plebe, es decir, de {nombre} y de los suyos. Hay teatro durante días y, al final, carreras en el circo. Los magistrados que los pagan quieren ser elegidos para algo, y se nota en lo que gastan.",
                Parrafo.PUEBLO to "En el teatro se representan comedias viejas, que el público se sabe de memoria y repite en voz alta antes que los actores. Cuando un actor se equivoca, el público lo corrige. Cuando acierta, también.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que es raro que haya juegos para la plebe pagados por los ricos, y que la plebe los acepte encantada. Luego piensa que él también los acepta encantado, y deja de pensar en eso, porque empieza la obra.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días hay banquete en el Capitolio para los senadores y, en el barrio, banquete de pobres en la calle, con lo que cada uno trae. El de los senadores tiene más platos; el de la calle, más risas.",
                "En el teatro los actores llevan máscaras grandes, con la boca abierta para que la voz llegue hasta las gradas de arriba. Desde lejos parecen todos sorprendidos de estar ahí.",
                "El último día de los Juegos hay carreras en el circo, y medio barrio cierra las tiendas para ir. Los que se quedan oyen los gritos desde lejos y adivinan quién va ganando.",
            )),
        ),
        "saturnales" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las Saturnales, siete días en que el mundo se pone al revés. La toga se queda en casa y se sale con túnica y gorro de liberto, hasta los senadores. En el templo de Saturno, en el foro, le desatan a la estatua del dios las vendas de lana que lleva en los pies todo el año.",
                Parrafo.PUEBLO to "Estos días se puede jugar a los dados en la calle sin que nadie diga nada, y los esclavos se sientan a la mesa con sus amos, y a veces los amos les sirven. En cada casa se elige un rey de la fiesta, que manda cosas absurdas, y hay que obedecerle.",
                Parrafo.GENTE to "En casa de {nombre} el rey de la fiesta es Lucila, que manda que su padre ande a cuatro patas y que su madre cante. Los dos obedecen, porque es la ley. Marco protesta porque él es mayor, y Lucila manda que se calle, y se calla.",
                Parrafo.ANOCHECER to "Esta noche se regalan velas de cera y figuritas de barro, que se compran en el mercado de los regalos. {nombre} le regala a Tertia una vela, y Tertia a él otra, y los niños se pelean por las figuritas. Por toda la calle se oye gritar «¡Io, Saturnalia!».",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días la ciudad entera es una taberna: se bebe en la calle, se canta en la calle, se juega en la calle. Los que quieren silencio se van al campo, y los que se quedan se aguantan.",
                "En el mercado de los regalos se venden figuritas de barro, velas, nueces y cosas pequeñas que no sirven para nada y que todo el mundo compra. {nombre} vende sandalias de muñeca que hace con los recortes, y se le acaban.",
                "Estos días no hay escuela, ni tribunales, ni casi trabajo. Hay quien dice que las Saturnales deberían durar todo el año, y hay quien contesta que entonces ya no serían fiesta, y los dos se ponen a beber.",
            )),
        ),
        "felix" to unDia(
            Parrafo.MANANA to "Hoy llega Félix, el aprendiz. Lo trae su padre, un liberto con anillo de hierro y mucho que decir, que habla de su antiguo amo como de un pariente. El chico tiene catorce años, las manos grandes y cara de no saber si quiere estar aquí.",
            Parrafo.TRABAJO to "A esta hora le enseña a Félix lo primero de todo: a barrer el taller, a ordenar las hormas por tamaño y a no tocar la cuchilla. El chico lo hace todo deprisa y mal. {nombre} se acuerda de su primer día, que fue igual, y no le dice nada.",
            Parrafo.GENTE to "Esta noche Félix duerme en el taller, en un jergón detrás del mostrador, como los aprendices. Tertia le baja un plato de gachas y le pregunta si tiene frío, y el chico dice que no, temblando. Ella le baja una manta y no le pregunta más.",
        ),
        "carros_foro" to unDia(
            Parrafo.MANANA to "Hoy, antes del amanecer, lo despertó un estruendo que hizo temblar la ínsula: un carro con un bloque de mármol, tirado por muchos bueyes, que pasaba por la calle hacia las obras del foro nuevo. Desde la ventana se veía la piedra, blanca, brillando a oscuras.",
            Parrafo.PUEBLO to "Las obras del foro del emperador se comen un trozo de la ciudad entre dos colinas: le han cortado la ladera a una para que quepa. Dicen que habrá una plaza enorme, una basílica, bibliotecas y una columna. De momento hay polvo, ruido y carros.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el emperador construye para que lo recuerden, y que él cose para comer. Luego piensa que si el foro necesita tantos obreros, los obreros necesitarán sandalias, y empieza a hacer cuentas. Las cuentas le salen mejor que la filosofía.",
        ),
        "escuela" to unDia(
            Parrafo.MANANA to "Hoy Marco empieza a ir a la escuela de la esquina, la del maestro que grita. {nombre} le compró una tablilla de cera y un punzón, y le pagó al maestro el primer mes, que cuesta lo que un par de sandalias buenas. El chico va delante, sin mirar atrás.",
            Parrafo.GENTE to "La escuela es un toldo en un portal, con un banco para los niños y una silla para el maestro, que tiene una vara en la mano por si acaso. Marco se sienta en la punta del banco y copia letras. A media mañana {nombre} pasa por delante, como quien no quiere la cosa.",
            Parrafo.ANOCHECER to "Esta noche Marco le enseña a toda la familia las letras que aprendió, escritas en la cera con mucho cuidado. Son cuatro. {nombre} las mira y reconoce dos. Tertia dice que el chico va a ser abogado, y Lucila borra las cuatro con el dedo.",
        ),
        "sexto_llega" to unDia(
            Parrafo.MANANA to "Hoy llegó Sexto, su hermano, que es marinero en los barcos del trigo de Alejandría. Entró en el taller sin avisar, con la piel quemada por el sol, el pelo tieso de sal y un saco de dátiles al hombro. Lucila no lo reconoció y se escondió detrás de su madre.",
            Parrafo.GENTE to "Sexto cuenta historias de Alejandría: un faro tan alto que se ve desde el mar a un día de distancia, una biblioteca con más libros que gente en Roma, cocodrilos. {nombre} se cree la mitad, Tertia ninguna y Marco todas. Los dátiles se acaban antes que las historias.",
            Parrafo.PENSAMIENTO to "{nombre} mira a su hermano y piensa que se parecen menos cada vez que vuelve. Sexto ha visto medio mundo y él, media Roma. No sabe cuál de los dos tiene más, y prefiere no preguntárselo en voz alta, por si la respuesta es la que se imagina.",
        ),
        "incendio" to unDia(
            Parrafo.MANANA to "Hoy, antes del amanecer, se oyeron gritos en la calle: ardía una ínsula a dos calles, la de la panadería. {nombre} bajó corriendo los cinco pisos con los demás, y desde la esquina se veían las llamas salir por las ventanas de arriba.",
            Parrafo.PUEBLO to "Los vigiles, los guardias del fuego, llegaron con cubos, mantas mojadas y ganchos para tirar paredes. Los vecinos hicieron una fila desde la fuente y se pasaron los cubos de mano en mano hasta el amanecer. La ínsula se quemó entera, pero las de al lado no.",
            Parrafo.GENTE to "La gente de la ínsula quemada se quedó en la calle, con lo poco que pudo sacar. Tertia les bajó agua y pan, y una familia con tres niños se quedó a dormir en el taller. Nadie preguntó a quién le tocaba: esta vez les tocó a ellos.",
            Parrafo.ANOCHECER to "Esta noche {nombre} no duerme bien. Huele a humo en toda la calle y cada ruido le parece fuego. Se levanta dos veces a mirar la lucerna, que está apagada, y la segunda vez Tertia le dice que se acueste, que ya la ha mirado ella.",
        ),
        "botas_tribuno" to unDia(
            Parrafo.MANANA to "Hoy entró en el taller el esclavo de una casa del Palatino con un encargo: unas botas de piel teñida para su amo, que es tribuno, a la medida y para dentro de diez días. Trajo el pie de su amo dibujado en un pergamino y un adelanto en plata.",
            Parrafo.TRABAJO to "A esta hora trabaja en las botas del tribuno, con el cuero más fino que ha tocado en años, teñido de rojo, que se marca solo con mirarlo. Cose despacio, con hilo nuevo. Félix lo mira por encima del hombro y no se atreve ni a respirar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un tribuno y él caminan sobre el mismo cuero, solo que uno lo paga y el otro lo cose. Las botas irán al Senado y a los banquetes, y él no. Pero las costuras serán suyas, y eso no se lo quita nadie.",
        ),
        "lucila_pan" to unDia(
            Parrafo.MANANA to "Hoy Lucila bajó sola por el pan a la tahona de la esquina, con una moneda apretada en el puño. Tertia la miró bajar los cinco pisos desde la puerta, y {nombre} la miró cruzar la calle desde el taller, y los dos hicieron como que no miraban.",
            Parrafo.GENTE to "Lucila volvió con un pan, sin la moneda, que es como tenía que volver, y con una historia: que el panadero le había dado un trozo de más por ser tan pequeña. Tertia dice que el panadero nunca regala nada. Lucila dice que a ella sí.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hoy su hija cruzó una calle de Roma ella sola, entre carros, perros y gente con prisa, y volvió. Le parece poca cosa y muchísimo a la vez. Esta noche, cuando la niña se duerma, le va a hacer unas sandalias nuevas.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "roma", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
