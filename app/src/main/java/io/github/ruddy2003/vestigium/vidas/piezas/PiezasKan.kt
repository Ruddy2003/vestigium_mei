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
 * El cuento de K'an, escriba y pintor de códices en una ciudad maya de las tierras bajas, del 750 al 754. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día tiene dos trabajos: por la mañana pinta glifos en el papel de amate; por la tarde hace las cuentas del
 *   tiempo (el tzolk'in, el haab, la cuenta larga). Luego va al templo.
 * - El párrafo del pueblo lee la estela del fin del k'atun mientras se talla (arco «estela») y, el resto del tiempo, la
 *   milpa (arco «milpa»).
 * - En la temporada 3 pasa un mes fuera, en Yaxchilan, con los regalos de su señor (estado «embajada»): amanece,
 *   trabaja, come, reza y duerme allá.
 * - No repite lo que ya dice «Su vida»: «el de la escritura», los pinceles de pelo, las conchas de tinta, el biombo de
 *   amate, el tzolk'in de 260 días con el haab de 365, los vasos firmados, el atole, los tamales y el cacao de la corte.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG3.kt`):
 * - Ix Sak espera desde `ix_sak_espera` (temporada 2); su hija nace en `nace_hija` (3).
 * - Su maestro, Ah K'in, vive hasta `muere_maestro` (temporada 3).
 * - La estela se encarga en `encargo_estela` (1) y se levanta en `fin_katun` (2).
 * - Las fiestas que vuelven cada año no nombran a la hija ni al maestro.
 */
internal object PiezasKan {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta en el petate, con el canto de los pavos del patio y el ruido de la piedra de moler: Ix Sak ya está moliendo el maíz de las tortillas. Por la puerta entra el olor del fogón. Fuera todavía está oscuro y la selva hace su ruido de la noche."),
        v("A {nombre} lo despierta un mono aullador, lejos, en la selva, que ruge como si fuera un jaguar. Ch'ok se mete en su petate, asustado. {nombre} le dice que es un mono, y que los monos son los patronos de los escribas, y que hay que llevarse bien con ellos."),
        v("{nombre} sale al patio de la casa, que está sobre una plataforma de piedra, y mira hacia la plaza: las pirámides todavía son sombras, y en lo alto del templo mayor brilla un fuego. El aire huele a humo y a tierra mojada. Se lava la cara en una vasija."),
        v("Lo primero del día es el atole, caliente, en una jícara, sentado en el umbral de la casa mientras sale el sol entre los árboles. {nombre} lo toma despacio. Ix Sak le pregunta qué va a pintar hoy, y él le dice que lo que el señor quiera, como siempre."),
        v("Antes de salir, {nombre} se ata el pelo con una tira de tela y se mete un pincel en el moño, como llevan los escribas, para que se sepa lo que es. Revisa sus conchas de tinta, las tapa con hojas y las mete en una bolsa de algodón. Hoy va a la casa de la escritura."),
        v("{nombre} se despierta antes que nadie y sale a mirar el cielo del este, como le enseñó su maestro: qué estrella sale, dónde está la luna. Lo que ve lo anota luego, en la casa de la escritura. Un escriba que no mira el cielo, dice su maestro, solo copia."),
    )

    /** El mes en Yaxchilan con los regalos de su señor (estado «embajada», temporada 3). */
    private fun em(texto: String, hacer: Hacer? = null) = v(texto, estado = "embajada", hacer = hacer)

    private val EMBAJADA_MANANA = listOf(
        em("{nombre} se despierta en la casa de los huéspedes de Yaxchilan, una sala larga de piedra sobre una terraza, con otros hombres de su ciudad durmiendo en petates. Abajo se oye el río grande, ancho y café, que rodea la ciudad como un brazo. Aquí no hay pavos que lo despierten: hay loros."),
        em("A {nombre} lo despierta un tambor de la plaza de Yaxchilan, que prepara algo, siempre algo. Sale a la terraza: abajo, el río; enfrente, la selva de la otra orilla; arriba, en los cerros, los templos de los señores de aquí, pintados de rojo. Se acuerda de Ix Sak y de la niña."),
        em("{nombre} amanece lejos de casa, en una ciudad que no es la suya, y tarda un momento en saber dónde está. Luego oye el río y se acuerda: Yaxchilan. Se ata el pelo, se pone el pincel en el moño y sale. Aquí también hay que parecer escriba."),
    )

    private val EMBAJADA_TRABAJO = listOf(
        em("A esta hora está en una sala del palacio de Yaxchilan con los escribas de aquí, que le enseñan sus libros con orgullo y con cuidado, sin soltarlos. Escriben algunos glifos de otra manera. {nombre} los mira, los aprende de memoria y no dice nada.", Hacer.TRABAJO),
        em("A esta hora cuenta y vuelve a contar los regalos de su señor para el señor de Yaxchilan: mantas, plumas de quetzal, cacao en grano, vasos pintados por él. Lo apunta todo en una tira de amate, para que nadie diga después que faltó algo.", Hacer.TRABAJO),
        em("A esta hora mira los dinteles de piedra de los templos de Yaxchilan, tallados con señores y señoras que se sangran la lengua y ven serpientes. Son los más finos que ha visto. Copia los glifos en un trozo de amate, despacio, para enseñárselos a su maestro.", Hacer.TRABAJO),
        em("A esta hora come en la casa de los huéspedes lo que mandan de la cocina del señor: tamales de pavo, frijoles, chile y pescado del río grande, asado en hojas. Comen bien, porque son embajadores. {nombre} piensa que en casa, hoy, hay atole.", Hacer.COMER),
        em("A esta hora le sirven cacao espumoso en un vaso pintado, como a un señor, y {nombre} mira el vaso antes de beber: quién lo pintó, cómo hizo los glifos del borde. Lo hizo bien. Bebe, y le sabe mejor el cacao por eso.", Hacer.COMER),
        em("A esta hora está en un patio de Yaxchilan, entre la gente de fuera que llegó para ver al señor nuevo, mientras los sacerdotes queman copal y cantan. Hay humo, flautas y tambores. {nombre} no conoce las canciones, pero los dioses son los mismos.", Hacer.REZAR),
        em("A esta hora sube al templo de los antepasados de Yaxchilan con los otros embajadores a dejar copal de su ciudad. Desde arriba se ve el río dando la vuelta entera a la ciudad. {nombre} reza por los suyos, que están río arriba, a muchos días de remo.", Hacer.REZAR),
        em("A esta hora está con los hombres de su ciudad en la terraza de los huéspedes, contando lo que vieron y bebiendo balché. Se habla del señor nuevo, de sus mujeres y de lo que regalará. {nombre} escucha y piensa en lo que va a contar en casa.", Hacer.GENTE),
        em("A esta hora un escriba joven de Yaxchilan viene a sentarse con él y le pregunta cómo se pinta en su ciudad. {nombre} le enseña su manera de hacer el glifo del jaguar. El joven le enseña la suya. Se ríen: las dos son buenas.", Hacer.GENTE),
        em("A esta hora duerme en la casa de los huéspedes, con el río sonando abajo, más fuerte que el de su ciudad. Los otros roncan. Sueña con su hija, que no reconoce su cara, y se despierta antes del amanecer.", Hacer.DORMIR),
        em("A esta hora duerme mal, en un petate ajeno, entre los mosquitos del río grande. Alguien quema hierbas en la puerta para espantarlos, y el humo lo hace toser. Piensa en lo lejos que está su casa, y se duerme contando los días que faltan.", Hacer.DORMIR),
    )

    private val EMBAJADA_TIEMPO = listOf(
        em("Sobre el río grande el cielo se ve entero, de una orilla a otra, y se ven venir las nubes desde lejos. {cielo} {de_estacion}"),
        em("{cielo} {de_estacion} En Yaxchilan el calor del río se pega a la piel y no se va ni de noche."),
    )

    private val EMBAJADA_PUEBLO = listOf(
        em("Yaxchilan está en una curva del río grande que la rodea casi entera, con sus templos subidos en los cerros y sus palacios en terrazas, todo de piedra pintada. Es más grande que su ciudad, y más vieja, y lo sabe. Por el río suben y bajan canoas todo el día."),
        em("La ciudad está llena de gente de fuera: embajadores de otras ciudades con sus regalos, comerciantes, músicos, guerreros con tocados de plumas. Todos vinieron para lo mismo, para ver al señor nuevo, y todos se miran unos a otros para ver quién trajo más."),
        em("En los dinteles de las puertas de Yaxchilan hay señores tallados en piedra, con lanzas y con sus cautivos a los pies. Por todas partes hay glifos, más que en ninguna parte que {nombre} haya visto. Aquí la piedra habla más que la gente."),
    )

    private val EMBAJADA_ANOCHECER = listOf(
        em("Al caer la tarde {nombre} baja al río con los remeros de su ciudad, a ver si las canoas siguen bien amarradas. El río está color de oro. Del otro lado, la selva se pone negra y empieza a hacer ruido. Piensa en la vuelta, río arriba, que será más larga que la bajada."),
        em("Anochece en Yaxchilan, y en lo alto de los templos se encienden fuegos que se ven reflejados en el río. {nombre} se sienta en la terraza a mirarlos. Piensa en Ix Sak, que estará encendiendo el fogón, y en la niña, que estará dormida."),
        em("Se cena tarde en la casa de los huéspedes, con antorchas, y los hombres de su ciudad hablan de volver. {nombre} se retira antes y escribe en una tira de amate lo que vio hoy, para que no se le olvide. Es lo que hace un escriba."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} El tiempo, para un escriba, no es lo que hace afuera: es la cuenta. Pero lo de afuera también cuenta."),
        v("{nombre} mira el cielo desde la plaza, por encima de los templos. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los viejos de la milpa saben el tiempo mejor que los almanaques, y lo dicen con la boca torcida."),
        v("{cielo} En {estacion} la selva cambia de ruido y de olor, y {nombre} lo nota sin pensarlo."),
        v("Desde lo alto del templo se ve la selva hasta donde se acaba la vista, verde y plana, con otras pirámides saliendo de ella como islas. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El maestro de {nombre} dice que el cielo es un libro que los dioses escriben y borran cada día."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y el sol pega tanto que las piedras de la plaza queman los pies.",
            "Hace un día claro, de un azul que se parece al azul que se hace con añil y arcilla.",
            "No hay una nube, y el calor sube de la selva como de un comal.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto y pesado, y el aire no se mueve.",
            "Hay nubes grandes sobre la selva, y la luz es blanca y sin sombras.",
            "Está nublado, y los pájaros cantan menos.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve fuerte, de golpe, y el agua corre por las escaleras de los templos como por un río.",
            "Llueve toda la tarde, y la plaza se vuelve un espejo.",
            "Cae una lluvia caliente, y luego sale el sol y todo humea.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: Chaak golpea con su hacha y los truenos hacen temblar las vasijas.",
            "El cielo se pone negro sobre la selva y el primer rayo cae en un cerro.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla entre los árboles, y las pirámides salen de ella como de un lago.",
            "La niebla de la mañana tapa la plaza, y los primeros que pasan son solo voces.",
        ),
        Clima.POLVO to listOf(
            "El aire está gris del humo de las milpas que se queman, y el sol es una bola roja.",
            "Hay polvo y humo en el aire, y los ojos pican.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.LLUVIAS to listOf(
            "Es el tiempo de las lluvias, cuando la selva crece tanto que casi se oye.",
            "En lluvias los caminos de tierra se vuelven lodo y los de piedra blanca brillan.",
            "Es el tiempo del agua, de los mosquitos y de la milpa.",
        ),
        Estacion.SECAS to listOf(
            "Es el tiempo de secas, cuando los árboles pierden hojas y el agua se guarda en los depósitos de la plaza.",
            "En secas el polvo se pega a todo, también al papel.",
            "Es el tiempo seco, el de las fiestas grandes, porque no hay que trabajar la milpa.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora pinta en la casa de la escritura, sentado con las piernas cruzadas sobre el petate, con la tira de amate extendida delante. Primero traza los glifos en rojo, muy suave; luego los repasa en negro, de un trazo. Si tiembla la mano, se tira la hoja. Hoy no tiembla.", hacer = Hacer.TRABAJO),
        v("A esta hora prepara el papel: alisa la tira de amate, le pasa una capa fina de cal blanca con un pincel ancho y la deja secar al sol, estirada con piedras. Mientras se seca, muele la tinta negra con hollín y un poco de goma en una concha. El papel tiene que estar liso como el agua quieta.", hacer = Hacer.TRABAJO),
        v("A esta hora pinta una escena en un vaso de barro que le mandó el señor: un dios con cara de jaguar sentado en un trono, y frente a él, un señor que le ofrece algo. Pinta con un pincel de un solo pelo para los ojos. Un vaso lleva muchos días, y cada vaso es un regalo de su señor a otro señor.", hacer = Hacer.TRABAJO),
        v("A esta hora hace las cuentas del tiempo para los sacerdotes: qué día del tzolk'in cae con qué día del haab dentro de cuarenta días, y qué dios carga ese día. Las hace con puntos y rayas, en una tabla de estuco que se borra con agua. Si se equivoca en una cuenta, se equivoca el templo entero.", hacer = Hacer.TRABAJO),
        v("A esta hora copia un libro viejo que se está deshaciendo: las páginas se rompen en los dobleces y la tinta roja ya casi no se ve. {nombre} copia glifo por glifo, sin entender algunos, que son de antes. Lo que no se copia, se pierde. Por eso hay escribas.", hacer = Hacer.TRABAJO),
        v("A esta hora le enseña a los aprendices de la casa de la escritura, tres muchachos de familias buenas, a contar en puntos y rayas: un punto es uno, una raya es cinco, la concha es cero. Uno es rápido; otro, aplicado; el tercero dibuja monos en la tabla. {nombre} se acuerda de sí mismo.", hacer = Hacer.TRABAJO),

        v("A esta hora está en el templo, al pie de la escalera, con los otros escribas y los sacerdotes, mientras arriba se quema copal. Se canta en voz baja. El humo blanco sube hacia el cielo, que es donde va. {nombre} pide por la mano, que no le tiemble.", hacer = Hacer.REZAR),
        v("A esta hora deja una ofrenda en el altar de la casa de la escritura: una bolita de copal, unas gotas de cacao, un pincel viejo que ya no sirve. El altar es del dios de la escritura, que es viejo y tiene cara de sabio, y de los monos, que son sus ayudantes.", hacer = Hacer.REZAR),
        v("A esta hora acompaña al sacerdote del templo a mirar los almanaques, para saber si el día de mañana es bueno o malo para lo que quiere hacer el señor. {nombre} lee en voz alta los glifos del día. El sacerdote asiente. Mañana es bueno.", hacer = Hacer.REZAR),

        v("A esta hora come en la casa de la escritura, con los otros escribas, unos tamales que trae cada uno envueltos en hojas, y chile, y agua. Se come rápido, con los dedos limpios, para no manchar el papel. Uno cuenta un chisme del palacio, y los demás hacen como que no les interesa.", hacer = Hacer.COMER),
        v("A esta hora come en casa, sentado en el petate, con Ix Sak y Ch'ok: tortillas, frijoles negros, calabaza y un poco de chile. Hoy hay cacao, porque el señor le mandó un saquito, y Ix Sak lo bate pasándolo de una jícara a otra, desde alto, para que haga espuma.", hacer = Hacer.COMER),
        v("A esta hora come pavo en caldo con chile y masa, porque es día bueno y Ix Sak mató uno. Ch'ok quiere el muslo. El perro de la casa espera los huesos, sin moverse, mirando. {nombre} le da uno. Ix Sak dice que lo malcría.", hacer = Hacer.COMER),

        v("A esta hora está en el patio con Ix Sak, que teje en su telar de cintura, atado al tronco del árbol, mientras él le cuenta lo que pintó hoy. Ella le cuenta lo que tejió. Los dos hacen dibujos con las manos, y los dos saben que el de ella tarda más.", hacer = Hacer.GENTE),
        v("A esta hora juega con Ch'ok a escribir glifos en la tierra del patio con un palito. Ch'ok hace un jaguar que parece un perro. {nombre} le hace uno bien, y Ch'ok lo borra con el pie y hace otro, más feo. Lo importante es que lo intenta.", hacer = Hacer.GENTE),
        v("A esta hora visita a los vecinos, que tienen una casa en la misma plataforma, a beber un poco de balché y hablar de la milpa, de la lluvia y del señor. El vecino es alfarero, y le enseña los vasos que hará para que {nombre} los pinte.", hacer = Hacer.GENTE),

        v("A esta hora duerme en el petate, con Ix Sak y Ch'ok, bajo el techo de palma, que huele a humo. Fuera cantan las ranas y los grillos, y de vez en cuando un búho, que trae malas noticias, dicen. {nombre} no cree en eso, o casi.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con glifos que se mueven solos en el papel, que se comen unos a otros y cambian de lugar. Se despierta y ve el fuego bajo, y oye la lluvia, y se vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, cansado de la mano y de los ojos. El papel se le queda en los párpados, los glifos rojos y negros. Fuera, en la plaza, el guardia del templo golpea un tambor una vez, para decir que está despierto.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La plaza de la ciudad es grande y blanca, con el templo mayor de un lado, el palacio del señor del otro y, en medio, las estelas de los señores de antes, de pie, cada una con su cara y su fecha. Alrededor, en las colinas, están las casas de la gente, entre los árboles. {entorno}"),
        v("{entorno} La ciudad no tiene murallas: tiene caminos de piedra blanca, levantados sobre la tierra, que van a las otras ciudades y brillan en la noche cuando hay luna. Por ellos llega la sal, la obsidiana, las plumas y las noticias."),
        v("La casa de {nombre} está sobre una plataforma de piedra, con otras dos casas de su familia alrededor de un patio, donde está el fogón, el árbol del telar, los pavos y el perro. Las paredes son de palos y barro y el techo de palma. Las de piedra son para los dioses y los señores. {entorno}"),
        v("La casa de la escritura está junto al palacio: una sala de piedra con bancos, nichos para los libros y una puerta que da a la plaza, para tener luz. Ahí trabajan cinco escribas y tres aprendices. Huele a cal, a tinta y a papel viejo. {entorno}"),
        v("{entorno} Alrededor de la ciudad están las milpas de cada familia, robadas a la selva, y más allá, la selva de verdad, con sus jaguares, sus monos y sus árboles de chicle. La selva siempre está esperando que alguien se descuide para volver."),
        v("En el mercado de la plaza se cambia todo por todo: maíz por sal, sal por cacao, cacao por mantas, mantas por obsidiana. El cacao sirve de moneda, y se cuenta grano a grano, con los dedos y con la boca. {entorno}"),
    )

    /** Lo que se ve de la ciudad: la estela mientras se talla y, el resto del tiempo, la milpa. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("estela", 0.01, 0.99, frases = listOf(
            "En la plaza, rodeada de un andamio de palos, está la piedra de la estela nueva, y los escultores pican los glifos que dibujó él.",
            "En la plaza se oyen todo el día los golpes de los escultores sobre la estela del k'atun, que poco a poco va teniendo cara.",
        )),
        Entorno("milpa", 0.0, 0.04, frases = listOf(
            "La milpa de la familia está sin sembrar, con la tierra desnuda esperando las lluvias.",
            "En las milpas no crece nada ahora: tierra, tallos secos y ceniza.",
        )),
        Entorno("milpa", 0.04, 0.5, frases = listOf(
            "En la milpa ya salió el maíz, verde y chiquito, entre los frijoles y las calabazas.",
            "La milpa está verde, con el maíz a la altura de la rodilla.",
        )),
        Entorno("milpa", 0.5, 0.95, frases = listOf(
            "El maíz de la milpa está alto, más que un hombre, y ya tiene elotes.",
            "La milpa es una pared verde que se mueve con el viento.",
        )),
        Entorno("milpa", 0.95, 1.0, frases = listOf(
            "El maíz de la milpa está seco y doblado, listo para cosecharse.",
            "En la milpa las mazorcas cuelgan dobladas hacia abajo, para que no les entre el agua.",
        )),
    )

    private val GENTE = listOf(
        v("Ix Sak, su mujer, es tejedora, la mejor de su linaje, y sus mantas bordadas las quiere el señor para regalarlas. Es callada, terca y más lista que {nombre}, que lo sabe. Cuando él se equivoca en una cuenta, ella lo nota en su cara antes que él."),
        v("Ch'ok, su hijo, tiene los ojos de su madre y las manos de su padre, inquietas. Se pasa el día en la plaza viendo a los escultores, y vuelve con polvo de piedra en el pelo. Quiere ser escriba, o guerrero, o jugador de pelota, según el día."),
        v("El señor de la ciudad, el ajaw, es un hombre joven que se cree viejo. Habla poco, mira mucho y paga bien. A {nombre} lo llama por su nombre, lo que es un honor, y le encarga cosas imposibles, lo que es una costumbre."),
        v("Balam, el escultor de la ciudad, es un hombre enorme con las manos llenas de cicatrices, que talla en piedra lo que {nombre} dibuja en papel. Discuten siempre: {nombre} dice que la piedra no respeta el dibujo, y Balam dice que el dibujo no respeta la piedra."),
        v("Los otros escribas de la casa de la escritura son cuatro: dos viejos que ya no ven bien, uno de su edad que lo envidia, y uno más joven que lo admira. {nombre} prefiere al que lo envidia, porque ese le dice la verdad."),
        v("Ah K'in, su maestro, es el escriba más viejo de la ciudad: tiene los dedos manchados de tinta para siempre y los ojos casi blancos. Ya no pinta, pero sabe de memoria libros enteros, y se los dicta a {nombre} sentado al sol. Cuando se calla, es que se durmió.", hasta = "muere_maestro"),
        v("Ix Sak espera un hijo, y teje más rápido que nunca, como si tuviera prisa por terminar algo. Las mujeres del linaje le traen comida y consejos. {nombre} cuenta los días con puntos y rayas en la pared, a escondidas.", desde = "ix_sak_espera", hasta = "nace_hija"),
        v("Su hija, la pequeña, es redonda y morena, y se ríe de todo, sobre todo de su hermano. Ix Sak la lleva atada a la espalda mientras teje. Cuando {nombre} llega a casa con las manos manchadas de rojo, la niña quiere tocarlas.", desde = "nace_hija"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de su oficio: porque se lo enseñó su maestro, y a su maestro, el suyo."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En la casa de la escritura se dice que todo lo que tiene nombre se puede escribir, y que lo que no, no existe."),
        v("{dato_objeto} Son cosas que se aprenden mirando, como casi todo."),
        v("Una casa de escriba tiene pocas cosas, y casi todas sirven para escribir o para comer. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("pincel", "el pincel", "un pincel", "El pincel",
            "El pincel de escriba se hace con pelos de la cola de un venado o de un pecarí, atados a una caña delgada con hilo y resina; hay pinceles de muchos pelos para rellenar y de uno solo para los ojos, y los escribas los llevan en el moño, como una pluma."),
        ObjetoCuento("concha_tinta", "la concha de tinta", "una concha de tinta", "La concha de tinta",
            "La concha de tinta es media concha de caracol de mar, cortada a lo largo; en una se pone la tinta negra, de hollín, y en otra la roja, de hematita molida; los escribas de los dioses salen pintados con una concha de tinta en la mano."),
        ObjetoCuento("codice", "el libro", "un libro", "El libro",
            "El libro de los escribas es una tira larga de papel de amate cubierta de cal, doblada como un biombo y pintada por las dos caras, con tapas de madera; se lee abriéndolo hoja por hoja, y algunos son tan largos que, extendidos, ocupan una sala."),
        ObjetoCuento("vaso_cacao", "el vaso de cacao", "un vaso de cacao", "El vaso",
            "El vaso de cacao es un cilindro de barro fino, pintado con escenas y con una fila de glifos en el borde que dice de quién es, para qué sirve («su vaso para beber cacao») y, a veces, quién lo pintó; los señores se los regalan, y se entierran con ellos."),
        ObjetoCuento("copal", "el copal", "una bola de copal", "El copal",
            "El copal es la resina de un árbol, blanca y pegajosa, que se recoge haciéndole cortes en la corteza y se seca en bolas; quemado, da un humo blanco y dulce que sube derecho, y es la comida de los dioses."),
        ObjetoCuento("orejera", "la orejera", "una orejera", "La orejera",
            "La orejera es un disco que se mete en el agujero de la oreja, que se va agrandando desde niño; las de la gente son de madera o de barro, las de los señores de jade verde, y las de los escribas, a veces, de concha, con un glifo grabado."),
        ObjetoCuento("petate", "el petate", "un petate", "El petate",
            "El petate es una estera de palma tejida que sirve para dormir, para sentarse a trabajar y para todo; el del señor es el más grande y se llama igual que el poder, porque el que se sienta en él es el que manda."),
        ObjetoCuento("tamal", "el tamal", "un tamal", "El tamal",
            "El tamal es masa de maíz rellena de frijol, de chile, de calabaza o, en los días buenos, de carne de pavo, envuelta en hojas y cocida al vapor; se lleva al trabajo, se le ofrece a los dioses y a los muertos, y nunca falta."),
        ObjetoCuento("manta", "la manta", "una manta", "La manta",
            "La manta de algodón se teje en el telar de cintura, en tiras que luego se cosen, y se borda con hilos teñidos con plantas, con caracoles o con cochinilla; las mantas buenas valen tanto que se pagan tributos con ellas."),
        ObjetoCuento("cuchillo", "el cuchillo de obsidiana", "un cuchillo de obsidiana", "El cuchillo",
            "El cuchillo de obsidiana es una lámina de vidrio de volcán, negra y brillante, que corta mejor que nada; la obsidiana viene de lejos, de las montañas del sur, y con ella se corta el papel, la carne, el pelo y, en los ritos, la piel."),
        ObjetoCuento("caracol", "el caracol", "un caracol", "El caracol",
            "El caracol de mar grande, con la punta cortada, se sopla como una trompeta y suena hondo y largo, como un animal enorme; con él se llama a los ritos, se anuncian los días grandes y se da la señal en la guerra."),
        ObjetoCuento("amate", "el amate", "una hoja de amate", "El amate",
            "El papel de amate se hace con la corteza de una higuera: se hierve con cal, se pone en tiras cruzadas sobre una tabla y se golpea con una piedra acanalada hasta que las fibras se pegan; se oye el golpeteo desde lejos, y se sabe que alguien está haciendo papel."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el tiempo, que es su oficio: los días tienen nombre y cara, y cargan a su dios en la espalda, y cuando se acaban vuelven. A sus {edad} años sabe contar hasta antes del principio del mundo. Lo que no sabe es contar lo que viene."),
        v("Piensa a veces en los escribas de antes, los que pintaron los libros que él copia, que eran mejores que él y de los que no queda ni el nombre. Por eso, cuando puede, firma. No por orgullo, se dice. Para que alguien sepa que alguien lo hizo."),
        v("{mundo} Él piensa en cosas más cercanas: en el papel, que se acaba; en la tinta roja, que es cara; en Ch'ok, que no se está quieto. Las cosas grandes son del señor y de los dioses, que las piensan en otra escala de tiempo."),
        v("{mundo} A sus {edad} años, {nombre} ha visto subir al trono a un señor y morir a otro, y ha pintado los glifos de los dos. Cada uno se creía el último y el primero. La piedra los guarda a todos, en fila, en la plaza."),
        v("Piensa en las palabras, que se van con el aire, y en los glifos, que se quedan. Una vez pintados, los dioses los pueden leer cuando quieran, de día o de noche, aunque nadie los lea. A {nombre} eso le da miedo y le da calma, según el día."),
        v("{mundo} A veces, de noche, {nombre} piensa que el mundo ya se acabó otras veces y volvió a empezar, como dicen los libros, y que algún día se acabará otra vez. No sabe cuándo. Si lo supiera, lo escribiría."),
    )

    private val MUNDOS = mapOf(
        "estela" to listOf(
            "En la ciudad se habla de la estela que el señor mandó hacer para el fin del k'atun, y de si estará a tiempo.",
            "Se habla de los comerciantes de la costa, de lo que traen y de lo que piden.",
            "Se dice que el señor de la ciudad vecina quiere casar a su hija con el de aquí.",
        ),
        "katun" to listOf(
            "Se habla del fin del k'atun, que se acerca: veinte años que se cierran, y otros veinte que empiezan.",
            "Se dice que Venus salió por la mañana, y que son días de peligro para los señores.",
        ),
        "embajada" to listOf(
            "Se habla de Yaxchilan, que estuvo muchos años sin señor, y del que sube ahora.",
            "Se dice que en el oeste hay ciudades en guerra, y que el humo se ve desde los cerros.",
        ),
        "guerra" to listOf(
            "Se habla de la guerra con la ciudad vecina, de los guerreros y de los cautivos.",
            "Se dice que este año las lluvias se harán esperar.",
        ),
        "venus" to listOf(
            "Se habla de Venus, que sale por la mañana, y de lo que dicen los almanaques.",
            "Se dice que el señor quiere un libro nuevo para el templo, con las cuentas de Venus.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} sale de la casa de la escritura con los ojos cansados y la mano dormida, y cruza la plaza, que se pone naranja. Los escultores recogen sus herramientas. En los templos se encienden los primeros fuegos. En casa lo espera el humo del fogón."),
        v("Anochece en la ciudad, y la selva empieza su ruido: las ranas, los grillos, los monos aulladores lejos. Los murciélagos salen de la cueva del cerro en una nube que tarda en pasar. {nombre} los mira desde el patio, como cuando era niño."),
        v("Se cena en el patio, sentados en el petate, con el fogón en medio y los pavos dormidos en el árbol. Ix Sak cuenta algo de su linaje, Ch'ok se queda dormido en el regazo de su padre. {nombre} mira el fuego, que también escribe, y que borra."),
        v("De noche, desde el patio, se ven las luciérnagas en la milpa, miles, que se encienden y se apagan como si se hablaran. {nombre} piensa que si supiera leerlas, sabría lo que dicen. Ch'ok quiere atrapar una, y la suelta enseguida."),
        v("La última luz del día se queda un momento en lo alto del templo mayor, cuando la plaza ya está a oscuras. {nombre} la ve desde la puerta de su casa. Luego salen las estrellas, y {nombre} busca a Venus como a una conocida, porque lo es: unos meses sale de tarde, otros de mañana, y otros se esconde."),
        v("Al caer la tarde suena el caracol en el templo, largo y hondo, y la ciudad entera se calla un momento. Es la hora de los dioses de la noche. {nombre} se lava las manos de tinta en una vasija y el agua se vuelve roja y negra.", meses = setOf(1, 2, 3, 4, 11, 12)),
        v("Al anochecer empieza a llover, como cada tarde en este tiempo, y el agua cae del techo de palma en una cortina. {nombre} se sienta en el umbral a mirarla, con Ch'ok, y cuentan los truenos. Huele a tierra y a maíz.", meses = setOf(6, 7, 8, 9)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "wayeb" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan los cinco días sin nombre, los que sobran al final del año, cuando las puertas entre los mundos se quedan abiertas y los malos espíritus andan sueltos. Nadie trabaja, nadie se baña, nadie se pelea, nadie sale si no hace falta. La ciudad entera se queda quieta.",
                Parrafo.TRABAJO to "A esta hora está en casa, sin hacer nada, que es lo que hay que hacer. No se toca el papel: lo que se escriba estos días puede salir torcido para siempre. {nombre} mira sus pinceles, guardados en la bolsa, y siente las manos inútiles.",
                Parrafo.ANOCHECER to "Esta noche no se enciende fuego más que lo justo, y se duerme con la puerta tapada. Ch'ok pregunta si los espíritus pueden entrar. Ix Sak le dice que no, si se porta bien. {nombre} no dice nada, porque no lo sabe.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en casa, quieto, como toda la ciudad. Se cuentan historias en voz baja y se come lo que hay, frío. Faltan pocos días para que el año vuelva a tener nombre.",
                "A esta hora está sentado en el umbral, mirando la plaza vacía, donde no pasa nadie. Ni los perros ladran. Mañana, o pasado, el mundo vuelve a empezar.",
            )),
        ),
        "pop" to unDia(
            Parrafo.MANANA to "Hoy empieza el año nuevo, el día uno de Pop, y todo se renueva: se barren las casas, se tiran las vasijas viejas y los petates gastados, y se estrenan otros. Las mujeres se ponen sus mejores mantas. En la plaza, los sacerdotes encienden el fuego nuevo.",
            Parrafo.TRABAJO to "A esta hora estrena pinceles en la casa de la escritura, que se hicieron en los días sin nombre a escondidas, y pinta el primer glifo del año: el del día de hoy, con su número. Lo pinta despacio. El primero sale como sale el año.",
            Parrafo.PUEBLO to "En la plaza hay humo de copal y música de flautas, tambores y caracoles, y la gente se ríe otra vez, después de cinco días callada. Se llevan ofrendas al templo. Los montones de vasijas rotas se apilan junto a las casas, y los niños las rompen más.",
        ),
        "juego_pelota" to unDia(
            Parrafo.MANANA to "Hoy hay juego de pelota grande en la cancha de la plaza, entre los jugadores de la ciudad y los de una ciudad aliada. Desde temprano se oye a la gente en las gradas, apostando mantas, cacao y plumas. Ch'ok se fue al amanecer para tener sitio.",
            Parrafo.PUEBLO to "La cancha es un pasillo largo entre dos paredes inclinadas de piedra pintada, con marcadores tallados en el suelo. La pelota es de hule, pesada como un niño, y se golpea con la cadera, sin manos. Cuando cae, el que la deja caer pierde. La gente grita.",
            Parrafo.PENSAMIENTO to "{nombre} mira la pelota que vuela de una pared a otra y piensa en el sol y la luna, que también van y vienen, y en los gemelos de la historia vieja, que jugaron a la pelota contra los señores de la muerte y les ganaron. Ch'ok grita a su lado como un loco.",
        ),
        "quema" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó la quema de las milpas: los hombres de cada familia prenden fuego a la selva que cortaron hace meses y dejaron secar, para que la ceniza abone la tierra. El humo sube por todas partes alrededor de la ciudad, y el sol sale rojo.",
                Parrafo.PUEBLO to "La ciudad está envuelta en humo, y el aire huele a madera quemada. Los pájaros vuelan asustados, y algunos animales salen de la selva corriendo. Los que queman vigilan que el fuego no se escape. A veces se escapa.",
                Parrafo.ANOCHECER to "Esta noche se ve el fuego de las milpas alrededor de la ciudad, como un anillo rojo en la oscuridad. {nombre} lo mira desde el patio con Ch'ok, que no puede dormir de lo bonito. Huele a humo hasta en el petate.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Sigue el humo de la quema sobre la ciudad, y la ceniza cae como una lluvia gris sobre la plaza. Los escribas tapan los libros con mantas.",
                "La quema sigue en las milpas de más lejos, y el cielo está amarillo de humo todo el día. La gente tose y mira al cielo, esperando las lluvias.",
                "Ya casi se acabó la quema. Las milpas son manchas negras alrededor de la ciudad, humeando todavía, esperando la siembra.",
            )),
        ),
        "fin_tun" to unDia(
            Parrafo.MANANA to "Hoy se cierra un tun, un año de los de la cuenta larga, de trescientos sesenta días, y el señor sale a la plaza vestido de dios, con su tocado de plumas, a esparcir incienso y a contar los años. {nombre} hizo las cuentas: es hoy, y no se equivocó.",
            Parrafo.PUEBLO to "En la plaza, delante de las estelas de los señores de antes, el señor deja caer bolitas de copal en un brasero, y el humo sube. Los sacerdotes cantan los números. La gente mira en silencio. Los escribas están en primera fila, porque esto es suyo.",
            Parrafo.PENSAMIENTO to "{nombre} mira el humo del copal y piensa en la cuenta, que no se acaba nunca: los tun hacen k'atun, los k'atun hacen bak'tun, y así hasta más allá de lo que se puede pensar. Él lleva la cuenta de su pedazo. Otros la llevarán después.",
        ),
        "chaak" to unDia(
            Parrafo.MANANA to "Hoy suben los sacerdotes y los viejos de la milpa a la cueva del cerro, a pedirle lluvia a Chaak, el que tiene el hacha de los rayos. {nombre} va con ellos, para escribir las palabras. El camino es empinado y hace calor. En la cueva, dicen, vive el agua.",
            Parrafo.TRABAJO to "A esta hora está dentro de la cueva, en la oscuridad, con una antorcha, mientras los sacerdotes dejan ofrendas en el agua que gotea del techo: maíz, copal, cacao, jade. Se canta. El agua de la cueva es la más pura, la que no tocó el suelo. {nombre} la anota.",
            Parrafo.ANOCHECER to "Al bajar del cerro se nubla el cielo, y antes de llegar a casa caen las primeras gotas, grandes y calientes. Los viejos se miran y sonríen. {nombre} no sabe si fue Chaak o el tiempo, y piensa que da igual, porque llovió.",
        ),
        "siembra" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó la siembra, con las primeras lluvias: los hombres van a la milpa con la bolsa de semillas y el palo de sembrar, hacen un agujero, echan cinco granos y lo tapan con el pie. Es el trabajo más viejo del mundo. Los dioses hicieron a la gente de maíz.",
                Parrafo.TRABAJO to "A esta hora siembra en la milpa de su familia con su hermano y Ch'ok, aunque es escriba: en la siembra ayudan todos. Agujero, cinco granos, pie. Agujero, cinco granos, pie. Ch'ok cuenta los granos en voz alta para que se vea que sabe contar.",
                Parrafo.PENSAMIENTO to "{nombre} mira los granos de maíz en la palma de la mano, amarillos, rojos, negros y blancos, como las cuatro esquinas del mundo. Piensa que la gente está hecha de esto, según los libros. Y se lo cree, con la tierra entre los dedos.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue sembrando en la milpa, con la espalda doblada y el sol de después de la lluvia. Luego irá a la casa de la escritura, con las uñas negras.",
                "A esta hora siembra frijol y calabaza entre las matas del maíz, para que se ayuden: el frijol trepa por el maíz y la calabaza tapa el suelo.",
                "A esta hora vuelve a la casa de la escritura, porque ya se sembró lo de su familia. Las manos le huelen a tierra mojada, y el papel lo nota.",
            )),
        ),
        "elotes" to unDia(
            Parrafo.MANANA to "Hoy se cortan los primeros elotes de la milpa, tiernos, y antes de comerse ninguno se le ofrecen al dios del maíz, que es joven y hermoso y muere y renace cada año, como la mata. Las mujeres los llevan al altar de la casa envueltos en una manta.",
            Parrafo.ANOCHECER to "Esta tarde se comen elotes asados en el fogón, con chile y sal, y atole nuevo, dulce, de maíz tierno. Ch'ok se come tres y le duele la panza. Los vecinos traen los suyos. Es la primera comida del año que sabe a lo que viene.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el dios del maíz, que pinta a veces en los vasos: un joven con la cabeza alargada como una mazorca, que baila. Lo pinta bonito, porque es el más bonito de los dioses. Y piensa que esa cabeza, en verdad, es la de todos.",
        ),
        "cosecha" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la cosecha del maíz: las mazorcas, que se doblaron en la mata para secarse, se arrancan y se echan en canastas, y se llevan a casa en la espalda, con la frente atada a un mecapal. Los niños recogen las que se caen.",
                Parrafo.TRABAJO to "A esta hora cosecha en la milpa con su familia, aunque es escriba: en la cosecha ayudan todos. Arranca, echa, arranca. La canasta pesa cada vez más. {nombre} piensa que el papel es más ligero, y que el maíz es más importante.",
                Parrafo.PUEBLO to "Por los caminos que bajan de las milpas va la gente cargada de mazorcas, en fila, como hormigas. En las casas se llenan los graneros, que son jaulas de palos levantadas del suelo, para que no entren los ratones. Huele a maíz seco por toda la ciudad.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora desgrana mazorcas en el patio con Ix Sak y Ch'ok, frotando una contra otra hasta que caen los granos. Las manos se le ponen rojas. Es un trabajo que no se acaba.",
                "A esta hora cosecha otra vez en la milpa, la parte de abajo, donde el maíz salió peor. Hay que sacarlo todo igual.",
                "A esta hora apunta en la casa de la escritura cuánto maíz entra al granero del señor de cada familia, en puntos y rayas. Lo del señor primero.",
                "A esta hora carga mazorcas de la milpa al granero, con el mecapal en la frente, detrás de su hermano. El camino es de subida.",
            )),
        ),
        "cacao" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la cosecha del cacao en las huertas del señor, a la sombra de los árboles grandes, junto al río. Las mazorcas, rojas y amarillas, salen del tronco mismo del árbol, como si las hubiera pegado alguien. Se cortan con una vara que tiene una navaja en la punta.",
                Parrafo.TRABAJO to "A esta hora está en la huerta del señor, con una tabla de estuco, contando las mazorcas que se cortan y las canastas que salen. El cacao es del señor y se cuenta grano a grano, porque es moneda. {nombre} lleva la cuenta en puntos y rayas.",
                Parrafo.PENSAMIENTO to "{nombre} abre una mazorca y mira las semillas, envueltas en una pulpa blanca y dulce que los niños chupan. Esto, piensa, vale más que el oro de otras tierras. Se fermenta, se seca, se tuesta, se muele, y se bebe con espuma. Y se paga con ello.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora cuenta otra vez el cacao que sale de las huertas. Los cortadores le dicen los números, y él los apunta. Alguno se queda con un puñado. {nombre} hace como que no lo ve.",
                "A esta hora mira cómo se ponen a secar las semillas de cacao en petates, al sol, en el patio del palacio. Huele dulce y agrio. Hay que voltearlas cada tanto.",
                "A esta hora apunta cuántas cargas de cacao seco entran al almacén del señor. Hoy entraron muchas, y el señor estará contento.",
            )),
        ),
        "encargo_estela" to unDia(
            Parrafo.MANANA to "Hoy el señor llamó a {nombre} al palacio y le encargó los glifos de la estela del fin del k'atun: la piedra grande que se levantará en la plaza, con la cara del señor y la fecha del día, para que se lea mientras dure la piedra. Es lo más grande que le han encargado.",
            Parrafo.TRABAJO to "A esta hora dibuja en una tira de amate el primer boceto de los glifos de la estela: la fecha de la cuenta larga arriba, grande; luego el nombre del señor, sus títulos, lo que hizo. No cabe todo. Nunca cabe todo. Tiene que elegir.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que lo que pinte en el papel lo tallará Balam en la piedra, y que la piedra lo guardará más que ningún libro. Dentro de muchos k'atun, alguien leerá sus glifos sin saber de él. Le tiembla la mano por primera vez en años.",
        ),
        "comerciantes" to unDia(
            Parrafo.MANANA to "Hoy llegaron comerciantes de la costa por el camino blanco, con cargadores que traen en la espalda sal, conchas, pescado seco, plumas de colores y piedras verdes. Se instalan en la plaza, y media ciudad va a verlos. Ix Sak quiere hilo teñido de púrpura.",
            Parrafo.PUEBLO to "En la plaza los comerciantes extienden sus mantas y ponen encima lo que traen, y la gente se agacha a mirar y a regatear. Se paga con cacao, con mantas, con hachas de piedra. Los comerciantes hablan con un acento raro y cuentan cosas del mar.",
            Parrafo.GENTE to "Esta noche uno de los comerciantes, un viejo flaco que conoce a su maestro, cena en casa de {nombre}, y le cuenta del mar, que es tan grande que no se ve el otro lado, y de las ciudades de la costa. Ch'ok no se duerme hasta que el viejo se va.",
        ),
        "orejas_chok" to unDia(
            Parrafo.MANANA to "Hoy le perforan las orejas a Ch'ok, como se hace con los niños de su linaje cuando ya son lo bastante grandes. Ch'ok está serio desde que se levantó, y no quiere el atole. Ix Sak le pone su mejor manta. {nombre} le dice que él también lloró.",
            Parrafo.GENTE to "El abuelo de Ch'ok le perfora las orejas con una espina de maguey, rápido, y le pone dos palitos para que no se cierren. Ch'ok no llora: aprieta los dientes y mira a su padre. Luego todos le dan algo: un tamal, una pluma, un juguete de barro.",
            Parrafo.PENSAMIENTO to "{nombre} mira a su hijo con las orejas sangrando y los ojos secos, y piensa que ya no es tan pequeño. Algún día llevará orejeras de concha, como su padre. O de jade, si es más listo. {nombre} espera que sea más listo.",
        ),
        "sequia" to unDia(
            Parrafo.MANANA to "Hoy hace veinte días que no llueve, en pleno tiempo de lluvias, y el maíz de las milpas se dobla, amarillo. La gente mira el cielo, limpio y cruel. En el templo se quema copal desde temprano. Nadie lo dice, pero todos piensan en el hambre.",
            Parrafo.PUEBLO to "En la plaza los depósitos de agua bajan, y las mujeres hacen fila con sus cántaros desde antes del amanecer. Los viejos de la milpa suben a la cueva del cerro otra vez. Los sacerdotes buscan en los almanaques qué hizo mal la ciudad.",
            Parrafo.PENSAMIENTO to "{nombre} busca en los libros viejos de su maestro otras sequías, y las encuentra: en tal k'atun, en tal otro. Siempre volvió a llover. Se lo dice a Ix Sak, que lo mira con cara de que eso no se come. Tiene razón.",
        ),
        "maestro_glifos" to unDia(
            Parrafo.MANANA to "Hoy su maestro, Ah K'in, lo llamó a su casa para enseñarle lo último que le quedaba: cómo se escriben las fechas de la cuenta larga, desde el principio de este mundo, con los cinco números en fila y la cara de cada dios que los carga. {nombre} llevó su mejor pincel.",
            Parrafo.TRABAJO to "A esta hora está sentado frente a su maestro, que le dicta los números con los ojos cerrados y le corrige los glifos sin verlos, solo por el ruido del pincel. Nueve bak'tun, quince k'atun… {nombre} los pinta despacio. Su maestro sonríe cuando suenan bien.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su maestro le está dando todo lo que sabe, como si tuviera prisa. No le pregunta por qué. Mira sus manos viejas y manchadas, y las suyas, manchadas también, y piensa que así pasa la escritura: de mano en mano, como el maíz.",
        ),
        "manta" to unDia(
            Parrafo.MANANA to "Hoy Ix Sak terminó la manta para el señor, después de muchos meses en el telar: una manta blanca con una cenefa bordada de rojo y negro, con los glifos del señor que le dibujó {nombre} y que ella tejió. La extienden en el patio para verla entera.",
            Parrafo.GENTE to "Las mujeres del linaje vienen a ver la manta y la tocan con la punta de los dedos, en silencio. La abuela dice que nunca vio una así. Ix Sak se pone colorada y se va a moler maíz. {nombre} la sigue y le dice algo al oído, y ella se ríe.",
            Parrafo.PENSAMIENTO to "{nombre} mira sus glifos en la manta de Ix Sak, tejidos en hilo, y piensa que ella los hizo mejor que él: más despacio, más hondo, más difíciles de borrar. Mañana la llevarán al palacio. Él la va a cargar, y no va a dejar que nadie más la toque.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "escriba_maya", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + EMBAJADA_MANANA,
            Parrafo.TIEMPO to TIEMPO + EMBAJADA_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + EMBAJADA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + EMBAJADA_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + listOf(
                v("Desde que la estela está en la plaza, {nombre} pasa delante de ella cada mañana y lee sus glifos, sus propios glifos, tallados por Balam más hondos de lo que él los pintó. No se lo dice a nadie, pero cada vez se para un momento más.", desde = "fin_katun"),
                v("Desde que murió su maestro, {nombre} es el escriba más viejo de la casa de la escritura, aunque no sea el más viejo. Los otros le preguntan lo que antes le preguntaban al viejo. Él les contesta lo que su maestro les habría dicho, y a veces acierta.", desde = "muere_maestro"),
            ),
            Parrafo.ANOCHECER to ANOCHECER + EMBAJADA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
