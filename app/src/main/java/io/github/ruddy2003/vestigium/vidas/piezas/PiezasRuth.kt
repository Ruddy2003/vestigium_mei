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
 * El cuento de Ruth, colona de El Remanso, un pueblo que se reconstruye junto a un arroyo del llano, de 2288 a 2292.
 * Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Tiene nombre propio: el cuento usa el hueco del nombre.
 * - Su día tiene dos tramos de trabajo (el campo y el muro; la guardia y las reparaciones) y dos comidas: esos textos
 *   valen a cualquiera de sus horas y no dicen cuál. Por la tarde, la fogata y la radio.
 * - Tres estados: «auxilio» (temporadas 1, 4 y 5, en otro pueblo; sus textos no dicen cuál ni con quién va), «asedio»
 *   (3, nueve días rodeados) y «encierro» (4, la ventisca). El tiempo, la gente, el objeto y lo que piensa no tienen
 *   variantes de estado: valen en el pueblo y fuera de él.
 * - El pueblo lee el arco «muro» y solo cuenta lo que le falta o lo que tiene cerrado, porque los mismos valores vuelven
 *   con el muro de tablas y con el de piedra; de qué está hecho lo dicen las variantes con fecha.
 * - Fiestas de guardar (el trabajo dice «no trabaja»): el año nuevo, la fundación, la noche de las canciones, los elotes
 *   y la noche más larga. Los demás ritos (los filtros, la llamada, la siembra, la chatarra, la cosecha, las trojes, los
 *   nombres, el invierno) son días de trabajo.
 * - No repite lo que ya dice «Su vida»: las veinte personas, los muros de tablas y láminas levantados con lo que
 *   encontraron, que sabe leer porque su madre le enseñó con revistas, el cereal dulcísimo, la luz donde llega el
 *   generador, que a todos les gustan las canciones de la radio.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG8.kt`):
 * - Nora: aprende a leer hasta `nora_lee` (temporada 1); hace guardia desde `nora_guardia` (4); habla por la radio
 *   desde `nora_radio` (5). Los textos sin fecha no dicen su edad.
 * - Elías: Ruth lo espera desde `espera` (temporada 2); nace en `nace_elias` (3); camina desde `elias_camina` (4).
 * - Agustina, su madre, hasta `muere_madre` (temporada 4). Gabino, en el pueblo hasta `vuelve_molinos` (4); en Cuatro
 *   Molinos después; con mujer y niña desde `gabino_visita` (5). Esteban, hasta `esteban` (3). Matías, herido de un brazo
 *   desde `esteban`: los textos sin fecha no le hacen usar las dos manos. Joaquín y Lidia, desde `forasteros` (2); el
 *   jabón, desde `jabon`. Celia, solo su voz hasta `conoce_celia` (5). Marta, siempre.
 * - Ruth es jefa de las guardias de noche desde `jefa_guardia` (temporada 2).
 * - El muro: de tablas hasta `muro_arde` (3), con el hueco del este hasta `muro_este` (1); quemado hasta `piedra` (3);
 *   de piedra que crece hasta `muro_listo` (5); terminado después. La torre del agua, desde `torre_agua` (2).
 * - Las fiestas que vuelven cada año no nombran a Nora, Elías, Agustina, Gabino, Esteban, Matías, Joaquín, Lidia ni Celia,
 *   ni hablan de la torre, del hueco del este ni de qué está hecho el muro.
 */
internal object PiezasRuth {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(12, 1, 2)
    private val MILPA = setOf(6, 7, 8, 9)
    private val LENA = setOf(11, 12, 1, 2)

    private val MANANA = listOf(
        v("A {nombre} la despierta el gallo del viejo Eusebio, que canta a oscuras porque nadie le ha explicado la hora. Se levanta sin encender nada, a tientas, y se pone las botas, que dejó junto a la puerta con los cordones abiertos, listas para correr."),
        v("{nombre} se despierta antes de que suene nada, con el oído puesto en el silencio, como todos en el pueblo: si se oye algo raro, se oye mejor a esta hora. No se oye nada. El arroyo, el viento en las láminas, Nora que habla dormida. Se levanta."),
        v("Lo primero que hace {nombre} al levantarse es asomarse a la puerta a oler el aire: a humo, a lluvia, a polvo, a perro mojado. El llano lo cuenta todo por la nariz antes que por los ojos. Hoy huele a lo de siempre. Se da por contenta."),
        v("{nombre} se despierta con el ronroneo del generador, que Matías enciende un rato antes del alba para cargar la radio. Desde la cama lo oye toser, arrancar, coger el paso. Mientras suene, el pueblo está vivo. Se levanta y pone agua a calentar."),
        v("{nombre} se despierta con las manos dormidas y el cuerpo lleno de los golpes de ayer: el martillo, la azada, la escalera. Se las frota con la grasa de una lata que guarda junto a la cama. Huele mal. Funciona. Las piernas tardan un poco más."),
        v("Al abrir la puerta, {nombre} cuenta lo que ve, como le enseñaron de niña: el muro, la milpa, el arroyo, las casas, el humo de las cocinas. Todo está. Luego entra a despertar a Nora, que tarda en levantarse lo que tarda el agua en hervir."),
        v("{nombre} se despierta con escarcha por dentro de las láminas de la casa, blanca como sal, y el aliento hecho humo. Mete los pies en las botas de un brinco. El fuego de anoche es ceniza tibia; lo sopla hasta que se acuerda de ser fuego.", meses = INVIERNO),
    )

    /** En otro pueblo del llano que pidió ayuda por la radio, en las temporadas 1, 4 y 5 (estado «auxilio»). */
    private fun au(texto: String, hacer: Hacer? = null) = v(texto, estado = "auxilio", hacer = hacer)

    private val AUXILIO_MANANA = listOf(
        au("{nombre} se despierta lejos de casa, en el suelo de una casa ajena, con su manta y los ruidos de otro pueblo: otro perro, otra campana, un gallo que canta a otra hora. Tarda un momento en saber dónde está. Lo primero que busca es su cantimplora."),
        au("A {nombre} la despierta el olor de un fogón ajeno, donde una mujer del pueblo hace tortillas para los que vinieron a ayudar. Le da la primera. Ella dice que no, que primero los suyos. La mujer le dice que hoy los suyos son ellos. Se la come."),
        au("{nombre} se despierta con un cansancio que no es el de su casa, más pesado, el de trabajar para otros con prisa. Se lava la cara en un balde. Afuera ya se oyen martillos. Piensa en el Remanso. Piensa que allí también se oyen ahora."),
    )

    private val AUXILIO_TRABAJO = listOf(
        au("A esta hora trabaja en lo que el pueblo ajeno necesita más: levantar lo caído, clavar lo suelto, cargar lo pesado. No conoce las herramientas ni los nombres de nadie. No hace falta. Un martillo es un martillo, y un gracias suena igual en todo el llano.", Hacer.TRABAJO),
        au("A esta hora cava con gente que no conocía, codo con codo, sin hablar mucho. Los de aquí trabajan con la cara de los que han perdido algo. {nombre} no les pregunta qué. Se lo van diciendo igual, poco a poco, entre palada y palada.", Hacer.TRABAJO),
        au("A esta hora come lo que le dan en el pueblo ajeno, que es poco y se da con vergüenza: un caldo flaco, una tortilla, media cebolla. Ella saca del morral un puñado de maíz tostado del Remanso y lo pone en medio. Desaparece. Así se come en el llano.", Hacer.COMER),
        au("A esta hora duerme en una casa ajena, sobre un suelo de tierra apisonada, con la escopeta al lado como en la suya. Duerme poco. Le falta algo y tarda en saber qué: el ronquido de Matías al otro lado de la cama. Lo echa de menos. No se lo dirá.", Hacer.DORMIR),
        au("A esta hora está junto al fuego de otro pueblo, oyendo sus historias y contando las del Remanso, que aquí suenan nuevas. Los niños la miran como a una forastera. Uno le pregunta si en su pueblo también hay muro. Ella dice que sí. El niño parece aliviado.", Hacer.GENTE),
    )

    private val AUXILIO_PUEBLO = listOf(
        au("El pueblo al que han venido a ayudar se parece al Remanso y no se parece: el mismo muro de lo que hubo a mano, la misma milpa, una antena parecida, pero otro olor en las cocinas y otro acento en las bocas. A {nombre} le parece estar en casa de una prima."),
        au("En el pueblo ajeno hay mucho por hacer y pocos para hacerlo, y se nota en todo: cercas a medio levantar, techos con lonas, gente que trabaja hasta que no se ve. Los que vienen de fuera traen manos y algo que no se dice: que el llano no los ha olvidado."),
    )

    private val AUXILIO_ANOCHECER = listOf(
        au("Al caer la tarde, {nombre} se sienta un rato aparte, en el borde del pueblo ajeno, mirando hacia donde queda su casa. No se ve nada: el llano, el cielo, la raya entre los dos. Sabe que allí se está encendiendo la bombilla de la plaza. Se lo imagina."),
        au("Anochece en el pueblo ajeno, y en la radio se oyen las noticias del llano: dicen que la gente del Remanso está ayudando. {nombre} oye el nombre de su pueblo en boca de otros y se le hace un nudo en la garganta. Se va a dormir pronto."),
    )

    /** Nueve días rodeados por los saqueadores, en la temporada 3 (estado «asedio»). */
    private fun ase(texto: String, hacer: Hacer? = null) = v(texto, estado = "asedio", hacer = hacer)

    private val ASEDIO_MANANA = listOf(
        ase("{nombre} se despierta en el andador del muro, donde se quedó dormida sentada, con la escopeta entre las rodillas. Es de día. Afuera, en el llano, siguen las hogueras de los saqueadores, humeando, en un círculo que no se ha movido. Cuenta los humos. Los de ayer."),
        ase("A {nombre} la despierta el llanto de Elías, que tiene hambre y no sabe nada de saqueadores. Le da el pecho a oscuras, en el cuarto de atrás, el más lejos del muro. Escucha. Afuera nadie grita. Lo deja dormido junto a Nora y sube a su tramo."),
        ase("{nombre} se despierta sin haber dormido del todo, con el cuerpo tieso y la boca seca. El pueblo entero ha pasado la noche así, con un ojo en el muro. Bebe medio vaso de agua. El resto se guarda. Nadie sabe cuántos días faltan."),
    )

    private val ASEDIO_TRABAJO = listOf(
        ase("A esta hora está en su tramo del muro con la escopeta y diez cartuchos contados, agachada detrás de una lámina con un agujero a la altura de los ojos. Algo se mueve entre los coches de la carretera vieja. No dispara. Ha dicho a todos que sin ver la cara no se dispara.", Hacer.TRABAJO),
        ase("A esta hora reparte a los suyos por el muro y lleva agua de tramo en tramo, en cubetas, para mojar la madera por si los de fuera traen fuego. Mojan, esperan, vuelven a mojar. Cada cubeta es agua que no se bebe. Marta lleva la cuenta en voz alta.", Hacer.TRABAJO),
        ase("A esta hora come agachada en el andador, sin soltar la escopeta, una tortilla fría con sal. Solo se come lo que no hay que cocinar: el humo de un fogón dice a los de fuera dónde está la gente. Nora le sube un jarro de agua y baja corriendo, encogida.", Hacer.COMER),
        ase("A esta hora duerme dos horas en el suelo de la casa común, con los que bajan del muro, todos vestidos y con las botas puestas. Duerme de golpe, como si se cayera. A las dos horas alguien le toca el hombro. Se levanta antes de abrir los ojos.", Hacer.DORMIR),
        ase("A esta hora no hay fogata: está prohibido hacer luz. El pueblo se junta a oscuras alrededor de la radio, con el volumen tan bajo que hay que pegar la oreja. La voz de Los Silos dice que ya se avisó, que aguanten. Nadie contesta. Aguantan.", Hacer.GENTE),
    )

    private val ASEDIO_PUEBLO = listOf(
        ase("Con los saqueadores afuera, el pueblo es otro: el portón cerrado con cadenas y un carro atravesado, las ventanas tapadas con tablas, los niños y los viejos en la casa común, las gallinas de Eusebio calladas como si supieran. De noche, ni una luz."),
        ase("Desde el muro se ven las hogueras de los saqueadores alrededor del pueblo, a tiro largo, entre los coches de la carretera y en la loma. De día casi no se les ve. Se les oye: risas, un motor, alguien que golpea una lata, para que no se olvide que están."),
    )

    private val ASEDIO_ANOCHECER = listOf(
        ase("Al caer la tarde no se enciende el generador: un pueblo con luz es un blanco. El Remanso se queda a oscuras con el llano, y en el llano se encienden las hogueras de los de fuera, una a una, como un collar alrededor del cuello. {nombre} sube al muro."),
        ase("Anochece, y {nombre} recorre el muro a oscuras, tramo por tramo, tocando el hombro de cada guardia para que sepa que no está solo. Nadie habla. Se oyen los grillos y, más allá, voces que no son de aquí. Abajo, Elías llora un momento. Alguien lo calma."),
    )

    /** Más de tres semanas tapados por la ventisca, en la temporada 4 (estado «encierro»). */
    private fun en(texto: String, hacer: Hacer? = null) = v(texto, estado = "encierro", hacer = hacer)

    private val ENCIERRO_MANANA = listOf(
        en("{nombre} se despierta a oscuras y tarda en saber si es de día, porque la nieve tapa la ventana hasta arriba y la luz que entra es azul, como bajo el agua. Por la chimenea baja el ruido del viento, que no ha parado. Se oye a todo el pueblo no salir."),
        en("A {nombre} la despierta la tos de su madre, una tos seca que no se va desde que empezó la ventisca. Le lleva un caldo y le echa otra manta encima. Agustina dice que no es nada, que es el frío. {nombre} le toca la frente. Hace como que le cree."),
        en("Lo primero que hace {nombre} al levantarse es cavar: la nieve ha vuelto a tapar la puerta durante la noche, y hay que abrir otra vez el pasillo hasta la casa de la radio, con una pala hecha de una lámina. Al otro lado alguien cava hacia ella. Se encuentran a medias."),
    )

    private val ENCIERRO_TRABAJO = listOf(
        en("A esta hora cava pasillos en la nieve entre las casas, que ya le llegan al pecho, para que se pueda ir de una puerta a otra sin perderse. Detrás de ella, Gabino saca la nieve con una cubeta. El viento la vuelve a meter. Cavan otra vez, con más rabia.", Hacer.TRABAJO),
        en("A esta hora cuenta con Marta lo que queda en las trojes, costal por costal, y lo apunta con carbón en la pared: tanto maíz, tanta calabaza, tanta leña. Lo divide entre los días que faltan, que nadie sabe cuántos son. Lo vuelve a dividir, por si se equivocó.", Hacer.TRABAJO),
        en("A esta hora come en la casa común, donde se juntan todos para ahorrar leña: un atole aguado de maíz, el mismo para todos, servido con el mismo cucharón. Los niños piden más. Los mayores dicen que ya comieron. {nombre} también lo dice, aunque no.", Hacer.COMER),
        en("A esta hora duerme con los suyos en la misma cama, para darse calor: Nora, Elías en medio, Matías, y ella en la orilla, la del frío. El viento empuja la casa como si quisiera entrar. Se despierta cada vez que cruje. Cruje toda la noche.", Hacer.DORMIR),
        en("A esta hora está con todo el pueblo alrededor de la estufa de la casa común, oyendo la radio, que llega entre chasquidos: otros pueblos también están tapados. Celia, desde Los Silos, nombra pueblos y pide que contesten. Cuando dice el Remanso, Matías contesta por todos.", Hacer.GENTE),
    )

    private val ENCIERRO_PUEBLO = listOf(
        en("Con la ventisca, el Remanso ha desaparecido debajo de la nieve: del muro solo asoma el borde, las casas son lomas blancas con un hilo de humo, y entre ellas corren los pasillos que se cavan cada mañana. Visto desde arriba, parecería un hormiguero."),
        en("El llano no existe estos días: más allá del muro solo hay blanco y viento, y el viento lo cambia todo de sitio, de manera que la nieve de ayer a la izquierda hoy está a la derecha. La carretera vieja, los coches, la gasolinera: tapados, como si nunca hubieran estado."),
    )

    private val ENCIERRO_ANOCHECER = listOf(
        en("Al caer la tarde Matías enciende el generador una hora, solo una, para la radio y la bombilla de la casa común, y el pueblo entero mira esa bombilla como se mira un fuego. Cuando se apaga, se oye un suspiro de todas las bocas a la vez. Luego, el viento."),
        en("Anochece, aunque casi no se nota, y {nombre} hace la última ronda por los pasillos de nieve con un farol, llamando a cada puerta. Desde dentro contestan: aquí estamos. En la casa de Eusebio contesta una gallina. Se ríe sola en el pasillo helado."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el llano el tiempo se mira antes que nada, porque de él depende todo lo demás."),
        v("{nombre} mira el cielo antes que el muro, como le enseñaron. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} La radio de Los Silos da el tiempo cada tarde, y acierta más o menos como los viejos."),
        v("{cielo} {de_estacion} Los viejos dicen que antes el tiempo lo decía una pantalla. Ahora lo dicen las rodillas."),
        v("{de_estacion} {cielo} En el llano no hay nada que pare el viento ni la nieve, salvo lo que la gente levanta."),
        v("El llano es tan plano que el tiempo se ve venir con horas de adelanto. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio de punta a punta del llano, de un azul tan grande que marea mirarlo.",
            "No hay una nube, y desde lejos se ve hasta la loma, cada piedra con su sombra.",
            "Hace un día claro, de esos en que el llano parece más ancho de lo que es.",
        ),
        Clima.NUBES to listOf(
            "Pasan nubes grises y bajas que corren hacia el este, y sus sombras cruzan el llano como rebaños.",
            "El cielo está tapado, de un gris parejo, y la luz no deja sombras en ninguna parte.",
            "Está nublado, y el llano tiene el color de una lata vieja.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia mansa que llena las cubetas puestas bajo cada canal de lámina.",
            "Cae una lluvia fina que vuelve la tierra del llano un barro rojo que se pega a las botas.",
            "Llueve, y el ruido del agua sobre las láminas no deja oír ni a la radio.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, y los rayos bajan al llano lejos, de tres en tres, sin nada alto donde caer.",
            "Truena tan fuerte que tiemblan las láminas, y el agua cae de lado, empujada por el viento.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, sin viento, y el llano se queda blanco y callado, borrado hasta el horizonte.",
            "Cae una nieve menuda que se mete por todas las rendijas y cruje bajo las botas.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, tan espesa que desde la puerta no se ve la casa de enfrente.",
            "La niebla sube del arroyo y se queda pegada al llano, y las voces suenan cerca y no se sabe de dónde.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del llano, que silba en las láminas y se lleva lo que no esté atado.",
            "Hace un viento que empuja a la gente al andar y hace correr las matas secas como liebres.",
        ),
        Clima.POLVO to listOf(
            "Hay polvo: una nube parda que se levanta del llano y tapa el sol, y que cruje entre los dientes.",
            "El viento trae polvo seco del sur, y todo se pone del mismo color, la ropa, el cielo y la gente.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y el llano se llena de flores pequeñas, amarillas y moradas, que duran dos semanas.",
            "En primavera el arroyo baja crecido y turbio, y los filtros trabajan el doble.",
            "Es el tiempo de arar la milpa y de mirar el cielo esperando la primera lluvia buena.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el maíz crece tanto que los viejos dicen que se le oye crecer de noche.",
            "En verano el sol del llano pega sin sombra, y se trabaja con un trapo mojado en la nuca.",
            "Es el tiempo de las tormentas de la tarde y de los cuervos en la milpa.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y en la milpa solo quedan los rastrojos, que se queman para abonar la tierra.",
            "En otoño las noches empiezan a helar, y se sacan las mantas de las cajas.",
            "Es el tiempo de las nieblas del arroyo y de contar lo que hay en las trojes.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y el llano se queda pelado y gris, con el viento del norte como único vecino.",
            "En invierno se cuenta la leña cada noche, y se sale del muro lo menos posible.",
            "Es el tiempo de las heladas, de los perros con hambre y de las historias largas junto a la estufa.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora está de guardia en el andador del muro, junto al portón, con la escopeta en las rodillas y los ojos en el llano. Cuenta lo que se mueve: un halcón, una liebre, polvo en la carretera que resulta ser viento. Apunta en la tabla: nada. Es la mejor palabra del día.", hacer = Hacer.TRABAJO),
        v("A esta hora endereza clavos sobre una piedra plana con el martillo, uno por uno, clavos torcidos sacados de tablas viejas. Un clavo derecho vale más que una lata de comida. Los va echando en un frasco. El ruido se oye en todo el pueblo, y a nadie le molesta.", hacer = Hacer.TRABAJO),
        v("A esta hora atiende los barriles de los filtros: el agua del arroyo entra turbia por arriba, cruza la grava, la arena y el carbón, y sale clara por un grifo de cocina. {nombre} llena un vaso y lo mira contra la luz. Lo prueba. Sabe a nada. Así tiene que saber.", hacer = Hacer.TRABAJO),
        v("A esta hora recorre el muro por dentro, despacio, empujando con el hombro, buscando lo que se ha aflojado, lo que se ha caído, por dónde podría pasar un perro o un niño. Encuentra dos sitios. Los marca con un trapo rojo. Mañana serán uno, si alguien se pone.", hacer = Hacer.TRABAJO),
        v("A esta hora sale con dos más a revisar las trampas del llano, fuera del muro, con la escopeta al hombro y un saco. En una hay un conejo. En otra, solo la pata de un conejo, y huellas grandes alrededor. {nombre} las mira un rato. Vuelven más deprisa de lo que salieron.", hacer = Hacer.TRABAJO),
        v("A esta hora sube al techo de la casa común a clavar una lámina que levantó el viento, con los clavos en la boca y el martillo en el cinturón. Desde arriba se ve el pueblo entero, pequeño, apretado dentro de su muro, como algo que alguien guarda en el puño.", hacer = Hacer.TRABAJO),
        v("A esta hora está en la milpa con la azada, quitando la hierba que crece entre las matas más deprisa que el maíz. Las calabazas se arrastran por el suelo con hojas grandes como sombreros. Cada tanto se endereza y se lleva una mano a los riñones.", hacer = Hacer.TRABAJO, meses = MILPA),
        v("A esta hora parte leña junto a la casa común, con un hacha de mango atado con alambre. Cada tronco viene de lejos: en el llano no hay árboles, solo lo que el arroyo trae en las crecidas y los postes de cercas que ya no cercan nada. La pila crece. El frío, también.", hacer = Hacer.TRABAJO, meses = LENA),
        v("A esta hora trabaja en el lado este del muro, el que nunca se ha terminado, poniendo postes y tablas donde ahora solo hay un carro volcado y alambre. Cada tabla que se clava ahí es una noche menos que alguien pasa en vela mirando el hueco.", hacer = Hacer.TRABAJO, hasta = "muro_este"),
        v("A esta hora cambia tablas en el muro, donde la madera se ha podrido o la ha mordido un perro, por otras que salen de casas que ya no son de nadie. Cada tabla trae su historia: una puerta azul, el costado de un armario, el letrero de algo que se vendía.", hacer = Hacer.TRABAJO, hasta = "piedra"),
        v("A esta hora sube piedra del arroyo en una carretilla de una rueda, cuesta arriba, hasta el tramo del muro que se levanta esta semana. Las piedras son redondas y no quieren estarse quietas. Hay que buscarle a cada una su sitio, como a la gente.", hacer = Hacer.TRABAJO, desde = "piedra", hasta = "muro_listo"),
        v("A esta hora escribe en una tabla, con carbón, la lista de las guardias de la noche: quién, a qué hora, en qué tramo. Hay que poner juntos a los que no se duermen con los que sí, y separar a los que hablan. Borra y vuelve a escribir. Es más difícil que clavar.", hacer = Hacer.TRABAJO, desde = "jefa_guardia"),

        v("A esta hora come sentada en una piedra, de una lata abollada que sirve de plato: atole de maíz con un poco de miel de abeja salvaje, que Eusebio saca de un poste hueco con la cara tapada. Raspa el fondo con el dedo. En el llano no se deja nada.", hacer = Hacer.COMER),
        v("A esta hora come en la casa común, en la mesa hecha con una puerta, tortillas recién echadas y frijoles con chile seco. Las tortillas las hace el primero que llega al comal. Hoy le tocó a ella, y le salieron gordas. Nadie se queja con la boca llena.", hacer = Hacer.COMER),
        v("A esta hora come calabaza asada con sal, de pie, junto a la lumbre, y un trozo de conejo que sobró. Come deprisa, con las manos, mirando hacia el muro por la puerta abierta. Es costumbre de todo el pueblo: nadie come de espaldas al llano.", hacer = Hacer.COMER),
        v("A esta hora come con Nora en el escalón de la casa, las dos con una tortilla doblada llena de frijoles. Nora le cuenta algo larguísimo y sin final. {nombre} asiente con la boca llena. Cuando Nora le pregunta qué opina, dice que sí. Era la respuesta equivocada.", hacer = Hacer.COMER),

        v("A esta hora duerme con la escopeta debajo de la cama y las botas junto a la puerta, como duermen todos en el pueblo. Duerme hondo, pero no del todo: una parte de ella se queda sentada en el muro, escuchando el llano por las dos.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con una ciudad de las revistas viejas, con escaleras que se mueven solas y luz en todas las ventanas. En el sueño sabe usarlo todo. Al despertar no se acuerda de cómo. Solo de que era fácil.", hacer = Hacer.DORMIR),
        v("A esta hora duerme a ratos: el silbido del guardia que cambia de turno, el perro del pueblo que le ladra a algo, el generador que se apaga. Cada ruido la sienta en la cama. Escucha. Nada. Se vuelve a tumbar sin haber abierto del todo los ojos.", hacer = Hacer.DORMIR),

        v("A esta hora está en la fogata de la plaza con los demás, con la radio encima de un barril: la voz de Los Silos da las noticias de los pueblos del llano y luego pone canciones de antes. Nadie entiende la mitad de las palabras. Las cantan igual, a gritos.", hacer = Hacer.GENTE),
        v("A esta hora está junto a la fogata asando lo que haya, mientras los viejos cuentan cosas de antes que cada año son un poco más grandes. Eusebio jura que su abuelo tuvo un coche que hablaba. Nadie le cree. Todos quieren que lo cuente otra vez.", hacer = Hacer.GENTE),
        v("A esta hora pasa por la fogata un vasito del aguardiente de Eusebio, que quema como el nombre que le pusieron: el diablito. {nombre} da un trago y lo pasa. Alguien saca una armónica oxidada y toca la única canción que sabe. Se la saben todos, de tanto oírla.", hacer = Hacer.GENTE),
        v("A esta hora está en la fogata escuchando a Nora, que lee en voz alta para los niños una página de las revistas viejas: un anuncio de algo que se llamaba aspiradora. Nadie sabe para qué servía. Nora se lo inventa, muy seria. Los niños se lo creen.", hacer = Hacer.GENTE, desde = "nora_lee"),
    )

    private val PUEBLO = listOf(
        v("{entorno} El Remanso son unas casas de láminas alrededor de una plaza de tierra, donde arde la fogata. En medio hay un poste con una campana hecha de una bombona de gas: un golpe, a comer; muchos golpes, a las armas."),
        v("La casa de la radio es la única con ventana de cristal. En el cuarto de atrás está el generador, que bebe lo que destila Eusebio y lo que se encuentra; en el de delante, la radio, con el micrófono envuelto en un calcetín para que no le entre el polvo. {entorno}"),
        v("{entorno} El arroyo corre pegado al lado norte del pueblo, entre sauces bajos, y es la razón de que el pueblo esté aquí: agua que no se acaba en verano. Junto a la orilla están los barriles de los filtros, pintados de blanco, con un letrero: no se bebe sin pasar."),
        v("La milpa está entre las casas y el muro del sur, una tierra negra que se abona con todo lo que el pueblo no se come. En una esquina están las trojes, dos cuartos de adobe sobre patas de piedra, para que no suban los ratones. {entorno}"),
        v("{entorno} Desde el andador del muro se ve la carretera vieja, que cruza el llano de punta a punta, con sus coches oxidados en fila, como si esperaran a que cambie un semáforo. Más allá, la gasolinera sin techo. Más allá, nada: tierra y cielo, cosidos por una raya."),
        v("En la casa común se come cuando llueve, se cura a los enfermos, se reparte lo que se encuentra y se discute todo lo demás. En la pared hay un mapa del llano pintado a mano, con los pueblos amigos en azul y lo demás en blanco. {entorno}"),
        v("El muro es una colcha de todo: tablas, láminas, puertas de coche, el letrero de una tienda de muebles, la caja de un camión. Por fuera es feo. Por dentro tiene un andador de madera para los guardias, y cada tramo lleva el nombre de quien lo clavó.", hasta = "muro_arde"),
        v("Del lado este, el muro es todavía una fila de tablas negras y láminas torcidas por el fuego, que huelen a quemado cuando llueve. Por los huecos se ve el llano. De noche se tapan con carros y alambre, y de día no se les quita el ojo de encima.", desde = "muro_arde", hasta = "piedra"),
        v("El muro nuevo se levanta con piedra del arroyo, redonda y gris, pegada con barro y cal, por tramos: donde ya hay piedra, llega al hombro; donde no, siguen las tablas viejas. Cada tramo de piedra lleva metida en la mezcla una mazorca seca. Dicen que da suerte.", desde = "piedra", hasta = "muro_listo"),
        v("El muro de piedra rodea ya el pueblo entero, gris, a la altura de un hombre con los brazos arriba, con su andador de madera por dentro. Los niños que han llegado después no saben que antes era de tablas. Los viejos se lo cuentan, por si acaso.", desde = "muro_listo"),
        v("La torre del agua se ve desde el llano antes que el pueblo: un tanque viejo sobre cuatro postes, pintado con cal, del que baja un tubo hasta los filtros. Le pusieron una bomba que mueve el viento. Cuando no hay viento, se sube el agua a cubetas. Casi siempre hay viento.", desde = "torre_agua"),
    )

    /** Lo que le falta al muro (arco «muro»). Los mismos valores vuelven con tablas y con piedra: no dicen de qué es. */
    private val ENTORNOS = listOf(
        Entorno("muro", 0.0, 0.4999, frases = listOf(
            "Por el lado este el muro tiene todavía un hueco, que de noche se tapa con un carro volcado.",
            "Del lado del este el muro no está cerrado, y por ahí se mira primero cada noche.",
        )),
        Entorno("muro", 0.5, 0.62, frases = listOf(
            "Ya no queda ningún hueco en el muro, aunque en muchos tramos un hombre alto ve por encima.",
            "El muro ya cierra el pueblo entero, y aun así cada noche se recorre de punta a punta.",
        )),
        Entorno("muro", 0.6201, 0.999, frases = listOf(
            "El muro de piedra sube cada semana, y ya casi no se ven tablas entre las piedras.",
            "Al muro de piedra le quedan pocos tramos, y los niños ya no alcanzan a asomarse.",
        )),
        Entorno("muro", 0.9995, 1.0, frases = listOf(
            "El muro de piedra está terminado y rodea el pueblo como un brazo.",
            "El muro de piedra está entero, y desde el llano parece que siempre estuvo ahí.",
        )),
    )

    private val GENTE = listOf(
        v("Marta es la que lleva el pueblo, aunque nadie la eligió: un día hizo falta alguien que dijera qué hacer, y ella lo dijo, y salió bien. Es bajita, cojea de una pierna y tiene una voz que se oye desde el portón. Cuando se equivoca, lo dice antes que nadie."),
        v("Matías, su compañero, es el del generador: lo conoce como se conoce a una mula vieja, sus toses, sus mañas, lo que le gusta. Le habla bajito cuando no quiere arrancar. Si arranca, dice que es por él. Si no, que es por la gasolina, que ya no es lo que era."),
        v("Nora, su hija, quiere saber todo lo de antes de la guerra: qué era un avión, para qué servía un semáforo, por qué la gente de las revistas sonríe tanto. {nombre} contesta lo que sabe y se inventa el resto. Luego se lo cuenta a Matías, para que digan lo mismo."),
        v("El viejo Eusebio es el más viejo del pueblo y el único que tiene gallinas, cinco, cada una con el nombre de una actriz de las revistas. Destila un aguardiente de maíz que sirve para beber, para curar y para el generador, según el día. Él dice que para las tres cosas a la vez."),
        v("Chayo es la que cura, una mujer grande y callada que sabe de hierbas, de huesos y de partos, y que guarda en una caja de galletas las pocas pastillas de antes que quedan en el llano. Nadie sabe ya para qué sirve cada una. Chayo tampoco, pero no lo dice."),
        v("Los niños del pueblo van a la escuela tres mañanas por semana, en la casa común, con una pizarra que es una puerta pintada de negro. Aprenden a contar con frijoles, a leer con lo que hay y a reconocer de lejos un humo que no es de los suyos."),
        v("Nora está aprendiendo a leer con las revistas viejas de la abuela, letra por letra, con el dedo. Se atasca en las palabras largas y se enfada con ellas como si le hubieran hecho algo. {nombre} la deja pelear sola. Así aprendió ella.", hasta = "nora_lee"),
        v("Nora ya hace guardia en el muro, en los turnos cortos de la tarde, con una escopeta que le queda grande y una seriedad que también. {nombre} la mira de reojo desde su tramo. Nora no aparta los ojos del llano. Es lo único que su madre le ha pedido.", desde = "nora_guardia"),
        v("Nora habla por la radio del Remanso con los otros pueblos, con una voz que pone más grave para parecer mayor. Celia la llama por su nombre. Desde entonces Nora anda por el pueblo con la barbilla un poco más alta, y {nombre} hace como que no lo nota.", desde = "nora_radio"),
        v("Agustina, su madre, vive con ellos, en el rincón más tibio de la casa. Ya no ve bien, pero desgrana maíz más deprisa que nadie y canta las canciones de la radio con letras que se ha inventado. Dice que las suyas son mejores. Algunas, lo son.", hasta = "muere_madre"),
        v("La silla de Agustina sigue en el rincón tibio de la casa, y nadie se sienta en ella. Encima están las revistas viejas, atadas con un cordel. A veces {nombre} canta sin darse cuenta una canción de la radio con la letra equivocada, la de su madre. Y la deja así.", desde = "muere_madre"),
        v("Gabino, su hermano menor, vive en la casa de al lado y come en la de ella. Es el primero que dice que sí cuando la radio pide ayuda, y el primero que se ríe cuando algo sale mal. {nombre} lo regaña por las dos cosas. Él se ríe también de eso.", hasta = "vuelve_molinos"),
        v("Gabino se quedó en Cuatro Molinos, porque allí hacía falta gente, y su casa de al lado está vacía, con la puerta atada con alambre. Habla por la radio cada pocas semanas. Dice que está bien, que come, que el frijol creció. {nombre} oye lo que no dice.", desde = "vuelve_molinos", hasta = "gabino_visita"),
        v("Gabino vive en Cuatro Molinos con su mujer y su niña, que tiene los ojos de Agustina, dice {nombre}, aunque nadie más lo ve. Cuando hablan por la radio, Gabino pone a la niña cerca del micrófono para que la oigan. En el Remanso todos se callan para oírla.", desde = "gabino_visita"),
        v("Esteban guarda el portón desde antes de que hubiera portón: un hombre flaco y alto, con sombrero de paja y una escopeta más vieja que él. Conoce cada sombra del llano. Si dice que algo se mueve, se mueve. Si dice que no, {nombre} duerme tranquila.", hasta = "esteban"),
        v("En el portón hay ahora un banco vacío y un sombrero de paja colgado de un clavo, que nadie quita. Esteban guardó esa puerta hasta el último día. Los que hacen ahora su guardia saludan al sombrero al llegar, sin decir nada, y miran el llano como lo miraba él.", desde = "esteban"),
        v("Matías no ha recuperado del todo el brazo de la herida del asedio: no lo sube por encima del hombro y no aprieta con esa mano. Ha aprendido a arreglar el generador con la otra y con los dientes. Nora le pasa las herramientas. Él dice que ella va más rápido.", desde = "esteban"),
        v("Joaquín y Lidia llegaron del sur con dos hijos y una mula, y viven en una casa que dejaron otros. Joaquín sabe de pozos; Lidia, de todo lo demás. Todavía cuentan poco de dónde vienen. Cuando alguien les pregunta, Lidia contesta con otra pregunta.", desde = "forasteros", hasta = "jabon"),
        v("Lidia hace jabón con ceniza y grasa en una olla grande, y desde que le enseñó a {nombre}, el pueblo entero huele distinto los domingos. Joaquín cavó un pozo junto a la milpa que da agua hasta en agosto. Sus dos hijos ya hablan como los del Remanso.", desde = "jabon"),
        v("Celia es la voz de la radio de Los Silos: da las noticias del llano, pone las canciones y contesta a los pueblos que llaman. Nadie del Remanso la ha visto nunca. Cada uno se la imagina a su manera. {nombre} se la imagina alta, con el pelo corto y las manos manchadas de tinta.", hasta = "conoce_celia"),
        v("Celia, la voz de la radio, ya tiene cara para {nombre}: una mujer chiquita, de trenza gris, con una risa que por la radio no se oye entera. Cuando habla por las tardes, {nombre} la ve sentada delante del micrófono con su taza de hierbas. Le sonríe al aparato.", desde = "conoce_celia"),
        v("{nombre} espera un hijo, y el pueblo entero lo sabe ya: Chayo le palpa la barriga cada semana, Marta le quita las guardias largas y Eusebio le guarda los huevos de sus gallinas. Ella dice que no está enferma. Le hacen caso a medias.", desde = "espera", hasta = "nace_elias"),
        v("Elías, su hijo, nació en la casa de las láminas azules y duerme en un cajón de fruta forrado con una manta. Tiene un grito que despierta al guardia del portón y unas manos que lo agarran todo. Matías dice que tiene mejores pulmones que el generador.", desde = "nace_elias", hasta = "elias_camina"),
        v("Elías ya camina, o corre, que en él es lo mismo: va de casa en casa agarrándose a lo que encuentra, y hay que cerrar la puerta de la milpa con dos vueltas de alambre. Dice palabras que solo entiende Nora. Nora las traduce, y a veces se las inventa.", desde = "elias_camina"),
    )

    private val OBJETO = listOf(
        v("En el llano nada se tira: lo que no sirve para una cosa, sirve para otra. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe desde niña, como lo saben todos en el pueblo."),
        v("Hoy {nombre} mira dos veces algo que usa todos los días sin mirarlo. {dato_objeto}"),
        v("{dato_objeto} Los de antes de la guerra lo habrían tirado. Aquí se hereda."),
        v("{dato_objeto} En el Remanso cada cosa tiene dueño, historia y, casi siempre, un remiendo."),
        v("Lo que sale de la carretera vieja se cuida más que lo que se compra, porque ya no se puede comprar. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("azada", "la azada", "una azada", "La azada",
            "La azada de la casa es la hoja de una pala vieja, cortada y afilada a piedra, con un mango de sauce del arroyo; el mango se cambia cada dos años, y la hoja lleva grabados con un clavo los nombres de tres dueños."),
        ObjetoCuento("escopeta", "la escopeta", "una escopeta", "La escopeta",
            "La escopeta de dos cañones es más vieja que cualquiera de los vivos y se dispara poco, porque los cartuchos se rellenan a mano; cada cartucho gastado se apunta en la pared de la casa común."),
        ObjetoCuento("radio", "la radio", "una radio", "La radio",
            "La radio del pueblo es un aparato gris con perillas de colores distintos, cada una de otro aparato muerto; funciona con el generador o con una manivela, y cuando hay tormenta se desconecta."),
        ObjetoCuento("lata", "la lata", "una lata de antes", "La lata",
            "Las latas de antes de la guerra ya no tienen etiqueta, y abrir una es una lotería: duraznos, frijoles o algo gris que nadie quiere; las vacías sirven de vaso, de plato y de maceta."),
        ObjetoCuento("garrafa", "la garrafa", "una garrafa", "La garrafa",
            "Las garrafas del agua son de un plástico de antes que no se rompe ni se pudre; se lavan con arena y se marcan con el nombre de la casa; una garrafa sin tapón vale la mitad."),
        ObjetoCuento("martillo", "el martillo", "un martillo", "El martillo",
            "El martillo de la casa tiene la cabeza de un martillo y el mango de otro, porque los dos se rompieron por distinto lado; con él se enderezan clavos, se parten nueces y, una vez, se espantó a un perro."),
        ObjetoCuento("farol", "el farol", "un farol", "El farol",
            "Los faroles del pueblo son frascos de cristal con una mecha de trapo y grasa de lo que se haya cazado; dan una luz amarilla que no llega lejos y huelen a cocina; adonde no llega el generador, no hay otra luz."),
        ObjetoCuento("mazorca", "la mazorca", "una mazorca", "La mazorca",
            "Las mejores mazorcas de cada cosecha no se comen: se cuelgan trenzadas de las vigas de la casa común, y de sus granos sale la milpa del año siguiente; los pueblos del llano se cambian unas por otras, para que el maíz no se canse."),
        ObjetoCuento("revista", "la revista", "una revista vieja", "La revista",
            "Las revistas de antes de la guerra son el tesoro de las casas: anuncios de cosas que nadie sabe para qué servían, gente que sonríe sin motivo; se guardan en cajas de lata y nunca, nunca, se usan para el fuego."),
        ObjetoCuento("cantimplora", "la cantimplora", "una cantimplora", "La cantimplora",
            "La cantimplora es de aluminio, de un ejército que ya no existe, con una funda de fieltro verde comida por las polillas; nadie sale del muro sin ella, y se llena antes que nada."),
        ObjetoCuento("manta", "la manta", "una manta", "La manta",
            "Las mantas del pueblo se tejen con los abrigos viejos, deshechos hebra a hebra, y por eso son de todos los colores; cada casa tiene las suyas, y en invierno se prestan por noches, como se presta el fuego."),
        ObjetoCuento("filtro", "el filtro", "un filtro de agua", "El filtro",
            "El filtro del agua es un barril con grava, arena y carbón de leña y un grifo abajo; lo que entra turbio sale claro; sin filtro, el arroyo es una enfermedad que corre."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en eso un rato y luego se le olvida, porque en el llano lo que está lejos importa menos que lo que está cerca. A sus {edad} años ha aprendido que el mundo se acaba en el muro. Y que el muro hay que hacerlo cada día."),
        v("Piensa en los de antes de la guerra, que tenían de todo y lo perdieron todo, y se pregunta si sabían que lo tenían. Piensa que seguramente no. Piensa que ella sí sabe lo que tiene: lo cuenta cada mañana. Es lo único en que les lleva ventaja."),
        v("{mundo} {nombre} piensa en los otros pueblos del llano, con sus muros, sus milpas y sus radios, y en que cada uno es una vela en un cuarto oscuro. Piensa que el pacto de la radio es lo que hace que no sean velas sueltas. Que juntos son casi una lámpara."),
        v("A veces, de guardia, mirando el llano vacío, {nombre} se imagina que por la carretera vieja vuelven los coches, todos a la vez, con la gente de las revistas dentro, sonriendo. Se le pasa enseguida. No sabría qué decirles."),
        v("Piensa en Nora, que no ha conocido otra cosa que esto y no lo echa de menos, porque no hay nada que echar de menos. Piensa que a lo mejor es mejor así. Luego piensa que no, que a Nora le tocará algo más que un muro. Que para eso lo levantan."),
        v("{mundo} A sus {edad} años, {nombre} sabe sembrar, disparar, curar una herida, leer el cielo y enderezar un clavo. No sabe qué es un avión, aunque lo ha visto en las revistas. Piensa que aquí no le serviría de nada. Le gustaría verlo igual."),
        v("Piensa en Elías, que nació con un muro alrededor y no sabrá nunca lo que es dormir sin él. Piensa que eso es lo que le dan: un sitio donde dormir sin escuchar. Piensa que es poco. Piensa que es todo lo que ella no tuvo de niña.", desde = "nace_elias"),
    )

    private val MUNDOS = mapOf(
        "muros" to listOf(
            "Por la radio se cuenta que en el sur hay un pueblo que ya cría cerdos, y en la fogata nadie se lo cree.",
            "Se habla de la carretera vieja, que cada año da menos chatarra y más trabajo.",
            "Se dice en el llano que los perros salvajes son cada año más listos, y que ya no temen al fuego.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde, Matías enciende el generador y se prenden las tres bombillas del pueblo: la de la plaza, la del portón y la de la casa de la radio. Fuera de su luz, el llano se pone negro de golpe, como si alguien cerrara una puerta. {nombre} cierra la suya."),
        v("Anochece, y en el muro cambia la guardia: los que bajan se cruzan con los que suben y se dicen, sin pararse, lo que han visto. Hoy, nada. {nombre} oye la palabra desde la plaza y respira. Es la que el pueblo quiere oír cada noche."),
        v("Al caer la tarde, el cielo del llano se pone de todos los colores a la vez, porque no hay nada que lo tape, y el pueblo se para un momento a mirarlo, con la cuchara o el martillo en la mano. Eusebio dice que es lo único bonito que dejó la guerra."),
        v("Anochece, y desde el llano llegan los ruidos de la noche: un coyote, otro que le contesta, el viento en las láminas de los coches de la carretera. {nombre} los conoce todos. Si un día oye uno que no conoce, ese será el que importa."),
        v("La última luz del día se queda un rato en la antena de la casa de la radio, arriba del todo, cuando el pueblo ya está en sombra. {nombre} la mira apagarse. Luego entra en casa, cuenta las cabezas de los suyos y echa la tranca a la puerta."),
        v("Anochece, y la fogata de la plaza se va quedando en brasas mientras la radio, bajita, pone la última canción de la noche, siempre la misma: un hombre que canta que todo saldrá bien. Nadie sabe si se lo cree. Todos se van a dormir después de oírla."),
        v("Al caer la tarde, {nombre} recorre el muro con la lista de las guardias y deja a cada uno en su tramo con dos palabras y una palmada. Es la jefa de las guardias de noche desde que Marta lo dijo. Hay noches en que todavía le extraña que la obedezcan.", desde = "jefa_guardia"),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y anoche el pueblo entero se quedó en la plaza, alrededor de la radio, para oír a Los Silos contar los segundos. A cero, todos gritaron y se abrazaron, y alguien disparó un cartucho al aire, que le costará una semana de regaños.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es el primer día del año, y hasta la guardia la hacen los que perdieron a las cartas. Duerme hasta que el sol entra por la puerta, que es un lujo de una vez al año, y luego se sienta en el escalón a no hacer nada. Le cuesta.",
            Parrafo.GENTE to "Esta tarde se comen los tamales del año nuevo, envueltos en hojas de maíz guardadas desde la cosecha, y cada uno dice en voz alta una cosa que quiere para el año. Uno dice lluvia. Otro, que no vengan los perros. Un niño dice un caballo. Nadie le dice que no.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza y no le pide nada, porque en el llano pedir trae mala suerte. Piensa solo en lo que hay: el muro, la milpa, los suyos, el agua. Lo cuenta. Está todo. Si dentro de un año lo cuenta igual, habrá sido un buen año.",
        ),
        "filtros" to unDia(
            Parrafo.MANANA to "Hoy toca abrir los barriles del agua, y desde temprano el pueblo bebe de lo guardado en garrafas. Junto al arroyo se apilan los costales de arena lavada y de carbón de leña que se fueron juntando durante meses para este día. Los niños tienen prohibido acercarse.",
            Parrafo.TRABAJO to "A esta hora vacía un barril con una pala pequeña: arriba, la arena, negra y apestosa de todo un año de arroyo; debajo, el carbón, que se deshace entre los dedos; al fondo, la grava, que se lava y se queda. Luego lo rellena, capa por capa, con lo nuevo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el agua va primero, antes que el muro y que el maíz: un pueblo sin agua limpia se muere en un mes sin que nadie lo ataque. Piensa que el enemigo más peligroso del llano no se ve y vive en el arroyo. Echa la primera cubeta.",
        ),
        "llamada" to unDia(
            Parrafo.MANANA to "Hoy es la llamada de los pueblos, y la radio de Los Silos empieza temprano a llamarlos a todos, uno por uno, del más cercano al más lejano. Cada pueblo contesta con su nombre y con su número: los que nacieron, los que llegaron, los que ya no están. Todo el llano escucha.",
            Parrafo.GENTE to "Esta tarde le toca al Remanso, y el pueblo entero se mete en la casa de la radio, apretado, para oír cómo Marta dice el número al micrófono. Después hay un silencio, y desde Los Silos lo repiten. Así queda dicho. Así existe el pueblo un año más.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los números de los otros pueblos, que suben o bajan cada año, y en que detrás de cada número que baja hay alguien a quien alguien echa de menos. Piensa que la llamada es una manera de contar a los vivos. Y otra de no olvidar a los demás.",
        ),
        "fundacion" to unDia(
            Parrafo.MANANA to "Hoy es el día de la fundación, y cada uno tiene preparada su tabla: lijada, con su nombre grabado con un clavo caliente y algo más, lo que quiera, una palabra o un dibujo. Los niños que ya saben escribir lo escriben solos. Los demás, con la mano de alguien encima.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Clava su tabla nueva en el andador de la guardia, junto a las de otros años, que ya están grises y se leen mal. En la de este año ha escrito su nombre y, debajo, una palabra que no le enseña a nadie. Tres clavos, ni uno más.",
            Parrafo.GENTE to "Esta tarde hay comida grande en la plaza, con lo que se guardó para este día: una gallina vieja en caldo, tortillas, calabaza en dulce. Los más viejos cuentan cómo llegaron, cada año un poco distinto. Los niños se saben la historia mejor que ellos y los corrigen.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los que fundaron el pueblo, que llegaron sin nada a un arroyo en mitad del llano y dijeron: aquí. Piensa que fundar un pueblo no es construirlo. Es quedarse la primera noche, cuando todavía no hay muro, y no irse por la mañana.",
        ),
        "siembra" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la siembra, y el pueblo entero sale a la milpa con los granos de las mejores mazorcas del año pasado, en morrales colgados del cuello. La tierra está oscura y blanda. Los viejos la huelen y dicen que sí. Nadie siembra antes de que la huelan.",
                Parrafo.TRABAJO to "A esta hora va por el surco abriendo agujeros con un palo de punta dura, un paso, un agujero, y dejando caer en cada uno cuatro granos: uno para el cuervo, uno para la hormiga, uno para el viento y uno para el pueblo. Un niño va detrás tapando con el pie.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que sembrar es lo más raro que hacen: enterrar lo que podrían comerse hoy para comer más dentro de unos meses. Piensa que nadie que tenga miedo siembra. Piensa que cada año, por estas fechas, el pueblo entero decide no tener miedo.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en la siembra, ahora con las calabazas: cada tres matas de maíz, un agujero más ancho con tres semillas planas y blancas. Las calabazas taparán el suelo con sus hojas y no dejarán crecer la hierba. Se ayudan entre ellas, como la gente.",
                "A esta hora acaba el último surco y riega con cubetas que van de mano en mano, en fila, desde los barriles hasta la tierra. La fila canta para no contar. Cuando cae la última cubeta, alguien grita que ya está. Ahora, a esperar.",
            )),
        ),
        "canciones" to unDia(
            Parrafo.MANANA to "Hoy es la noche de las canciones, y el pueblo se pasa el día preparándola: se junta leña para la fogata más grande del verano, se barre la plaza, se sacan las sillas de todas las casas. Alguien revisa el generador tres veces, porque esta noche no puede fallar.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja, salvo la guardia, que esta noche se hace en turnos cortos para que nadie se pierda más de dos canciones. Pasa la tarde lavándose el pelo con el jabón bueno y buscando la camisa menos remendada. Encuentra una con un solo remiendo.",
            Parrafo.ANOCHECER to "Esta noche, en cuanto se va el sol, la radio empieza y ya no se calla: una canción detrás de otra, sin noticias ni avisos, hasta que clarea. El pueblo baila alrededor de la fogata. Los viejos bailan como si fueran jóvenes. Los jóvenes, como si no hubiera mañana.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, bailando, en la gente que hizo estas canciones, hace tanto, que no podía imaginarse un pueblo de láminas en mitad del llano bailándolas alrededor de una fogata. Piensa que las canciones han llegado más lejos que ellos. Que es lo mejor que dejaron.",
        ),
        "chatarra" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy sale la salida grande a la carretera vieja, dos días, con dos carretillas y seis personas, a buscar entre los coches oxidados lo que todavía sirva: clavos, láminas, tornillos, alambre, algún vidrio entero. Salen antes de que amanezca, para andar con el fresco.",
                Parrafo.TRABAJO to "A esta hora desarma un camión volcado a la orilla de la carretera, con una palanca y mucha paciencia: las láminas del costado, los tornillos de las ruedas, un trozo de cable. Todo quema de sol. Debajo del asiento encuentra la foto de una familia. La deja donde estaba.",
                Parrafo.ANOCHECER to "Esta noche duermen en la cabina de un tráiler, con la puerta atada por dentro y la escopeta a mano, contando chistes en voz baja para no oír el llano. Desde la ventanilla se ve, muy lejos, una lucecita: la bombilla de la plaza del pueblo. Nadie lo dice. Todos la miran.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que la carretera vieja es un cementerio que les da de comer: cada coche fue de alguien, y ese alguien iba a alguna parte. Piensa que nunca sabrán adónde. Lo que sacan de aquí se vuelve muro. Le parece una manera de que lleguen.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora vuelve por la carretera con las carretillas cargadas hasta arriba, empujando en las cuestas, parándose a beber cada poco. Lo que pesa es lo que vale. Cuando se ve el pueblo a lo lejos, nadie dice nada, pero todos aprietan el paso.",
            )),
        ),
        "elotes" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de los elotes, y desde temprano los niños recorren la milpa buscando las mazorcas tiernas, apretando un grano con la uña: si sale leche, sirve. Las juntan en canastas en la plaza. Cada año alguien se come la primera cruda, a escondidas.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta, y la guardia la hacen los que perdieron la apuesta de quién comía más elotes el año pasado. Ella pasa el día deshojando en la plaza y guardando las hojas buenas para los tamales del invierno. Tiene las manos verdes y dulces.",
            Parrafo.GENTE to "Esta tarde los elotes se asan en la fogata con su hoja, hasta que se ponen negros por fuera, y se comen con sal y chile, quemándose los dedos. Hay quien se come seis. Hay quien dice que siete. La radio pone canciones. Nadie las oye: están comiendo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el primer elote del año sabe a algo más que a maíz: a que la milpa aguantó, a que no hubo plaga ni granizo que se la llevara entera, a que el invierno será un poco menos largo. Se come el suyo despacio. Se lo ha ganado.",
        ),
        "cosecha" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la cosecha, cinco días en que nadie hace otra cosa, salvo la guardia: el pueblo entero entra en la milpa con machetes, costales y canastas, y no sale hasta que la última mazorca esté dentro. Las matas secas suenan como papel al cortarlas.",
                Parrafo.TRABAJO to "A esta hora tumba matas de maíz con el machete, de un golpe cada una, y otros detrás arrancan las mazorcas y las echan en los costales. El polvo de las hojas se pega a la piel y pica. Nadie se rasca. Si te rascas, dice Eusebio, pica el doble.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que en estos cinco días se decide el invierno: lo que entre en las trojes es lo que habrá, ni un grano más. Piensa en los inviernos malos que le contaron, cuando se comieron la semilla. Piensa que eso no pasará este año. Corta otra mata.",
            )),
            sigue = mapOf(
                Parrafo.TRABAJO to listOf(
                    "A esta hora sigue en la cosecha, desgranando mazorcas en la plaza con otros, frotando una contra otra hasta que los granos caen en el cesto como lluvia. Le duelen los pulgares. A su lado alguien canta. Los granos llevan el ritmo.",
                    "A esta hora recoge calabazas en la milpa, las grandes, las que se escondieron debajo de sus propias hojas, y las va rodando hasta la carretilla. Algunas pesan como un niño. Las más bonitas se guardan para semilla y para la noche más larga.",
                    "A esta hora escoge con cuidado las mejores mazorcas de la cosecha, las más largas, las más llenas, las que aguantaron el viento, y las aparta en un rincón. Esas no se comen. Esas son el año que viene, y el otro, y el otro.",
                    "A esta hora carga costales desde la milpa hasta la casa común, uno al hombro, cuesta arriba, tantas veces que deja de contarlas. Al final del día los costales hacen una pared. Los niños se suben encima a mirar el llano.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Al caer la tarde de otro día de cosecha, el pueblo se queda tirado en la plaza, sin fuerzas para encender la fogata. La enciende alguien. Hay atole caliente. Nadie habla mucho: les duelen hasta las palabras.",
                    "Anochece, y en la milpa medio cortada quedan los rastrojos de pie, como gente que espera algo. {nombre} los mira desde el muro. Mañana caerán también. Huele a tierra removida y a hoja seca.",
                    "Al caer la tarde, alguien cuenta en voz alta los costales llenos, y todos escuchan como si fueran noticias de la radio. Son más que ayer. Menos de los que hacen falta. Mañana, más.",
                    "Anochece sobre la última tarde de cosecha, y la milpa ya no es milpa: es tierra pelada con rastrojos, y el pueblo, de pronto, se ve más grande y más solo. Las trojes, en cambio, están llenas. Alguien saca el aguardiente de Eusebio.",
                ),
            ),
        ),
        "trojes" to unDia(
            Parrafo.MANANA to "Hoy se guarda el maíz seco en las trojes, y desde temprano hay una fila de gente pasando costales de mano en mano, desde la casa común hasta la puerta de las trojes, alta, para que no suban los ratones. Dentro, alguien los acomoda y los cuenta en voz alta.",
            Parrafo.TRABAJO to "A esta hora está dentro de las trojes, en una penumbra que huele a polvo dulce, apilando costales contra la pared y apuntando cada uno con una raya de carbón. Cada cinco rayas, una cruzada. Se le meten granos en las botas. Los cuenta también.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando las rayas de carbón, que eso es el invierno del pueblo escrito en una pared: tantas rayas, tantas semanas. Piensa que algunos pueblos del llano no tienen pared con rayas este año. Que alguno llamará por la radio antes de la primavera.",
        ),
        "nombres" to unDia(
            Parrafo.MANANA to "Hoy es el día de los nombres, y el pueblo amanece callado: no se oye martillo ni azada, y la radio está apagada. Junto al muro, en la tabla grande donde se escriben, alguien ha repasado con carbón los nombres que la lluvia del año borró un poco. Son muchos.",
            Parrafo.GENTE to "Esta tarde el pueblo se junta junto al muro y Marta lee los nombres, uno por uno, despacio: los que murieron en el llano, los que salieron y no llegaron, los que nadie sabe. Después de cada nombre, todos dicen a la vez: aquí. Lo dicen por ellos, que ya no pueden.",
            Parrafo.ANOCHECER to "Al caer la tarde se encienden faroles a lo largo del muro, uno por cada nombre de la tabla, y se dejan arder hasta que se apagan solos. Desde el llano, el pueblo debe de verse como una corona de luces pequeñas. Esta noche el guardia no mira hacia fuera. Mira las luces.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los nombres que conoce de la tabla, que cada año son más, y en los que no conoce, que fueron alguien para alguien. Piensa que decir aquí por un muerto es un poco mentira y un poco verdad. En el llano no hay mucha diferencia.",
        ),
        "invierno" to unDia(
            Parrafo.MANANA to "Hoy el pueblo se prepara para el invierno, y desde temprano se trabaja en el muro con barro, paja y trapos, tapando cada rendija por donde el viento del norte entra a robar el calor. Los niños buscan rendijas con una vela: donde la llama se tuerce, hay una.",
            Parrafo.TRABAJO to "A esta hora vuelve de la orilla del arroyo con un haz de ramas secas a la espalda, el tercero, y lo echa en la pila de leña de la casa común, que ya le llega al hombro. Faltan dos más. En el llano, la leña es lo más difícil de encontrar después de la gente.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el invierno que viene, que nadie sabe cómo será, y en que contra lo que no se sabe solo se puede tapar rendijas y juntar leña. Piensa que es poco. Piensa que es lo que hay. Vuelve por otro haz antes de que se vaya la luz.",
        ),
        "noche_larga" to unDia(
            Parrafo.MANANA to "Hoy es la noche más larga del año, y el pueblo la espera desde hace semanas: en la casa común están escondidas las latas de dulce, una por niño, guardadas desde la salida a la carretera vieja. Los niños saben dónde. Hacen como que no.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Pasa la tarde ayudando a levantar la fogata más grande del año, en el centro de la plaza, con la leña que sobra y con todo lo que ya no sirve, que este día arde. Un sillón sin patas corona el montón.",
            Parrafo.ANOCHECER to "Esta noche, cuando oscurece, se enciende la fogata, que se ve desde todo el llano, y los niños abren sus latas: duraznos, peras, una vez algo que nadie supo qué era. Se lo comen con los dedos, despacio, para que dure lo que dura la noche.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que desde mañana los días serán un poco más largos, un minuto, dos, y que nadie lo notará hasta febrero. Piensa que así llegan las cosas buenas al llano: de minuto en minuto. Las malas llegan de golpe. Por eso se celebran las buenas.",
        ),

        // ---------- Los de la temporada 1 ----------
        "jauria" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy, al amanecer, había huellas de perro en la nieve alrededor de todo el muro, muchas, de muchos tamaños, dando vueltas. Esteban, desde el portón, dice que eran más de doce. Anoche se los oyó pasar, y no ladraban. Eso es lo que le preocupa. Los perros que no ladran cazan.",
                Parrafo.TRABAJO to "A esta hora refuerza con Gabino el hueco del lado este, el del carro volcado, metiendo más alambre de púas y estacas afiladas en la nieve. Si vienen, vendrán por aquí. Gabino hace bromas para que no se note que mira al llano cada poco. Se nota.",
                Parrafo.ANOCHECER to "Esta noche nadie duerme del todo. {nombre} está en el muro con la escopeta y un farol, y en el llano, con la luna sobre la nieve, se mueven sombras bajas, en fila, alrededor del pueblo. No se acercan. Dan vueltas. A veces un par de ojos verdes la mira y se apaga.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que los perros también tienen hambre en invierno, y que antes de la guerra eran de alguien, dormían dentro, tenían nombre. Piensa que el llano los volvió lo que son, igual que a la gente. Eso no le quita las ganas de disparar. Solo se las ordena.",
            )),
            sigue = mapOf(
                Parrafo.MANANA to listOf(
                    "Hoy, otra vez, hay huellas alrededor del muro, más cerca que ayer. Una gallina de Eusebio no está. Nadie sabe cómo salió. Eusebio dice que no salió: que algo entró y salió con ella. Marta manda doblar la guardia y que los niños duerman en la casa común.",
                    "Hoy amanece con la nieve limpia alrededor del muro: ni una huella nueva. Esteban dice que se han ido, o que han aprendido a no dejar rastro. El pueblo respira a medias. Esta noche, de todas formas, la guardia seguirá doble.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Esta noche los perros se acercan más: se les oye arañar el lado del hueco, gruñir, empujar el carro. {nombre} dispara una vez al aire. Se van. Vuelven. Se van otra vez. Al amanecer, en la nieve, una mancha oscura que nadie quiere mirar de cerca.",
                    "Esta noche no se oye nada, y es peor. {nombre} y Esteban pasan horas en el portón, quietos, escuchando el llano blanco. A la tercera hora, muy lejos, un aullido. Luego otro, más lejos. Esteban dice que se van al sur. {nombre} se lo cree a medias.",
                ),
            ),
        ),
        "cereal" to unDia(
            Parrafo.MANANA to "Hoy {nombre} ha ido sola a la gasolinera vieja a buscar tornillos y ha vuelto con algo mejor: detrás de una nevera caída, una caja de cartón con el dibujo de un tigre sonriente, sin abrir. Dentro, una bolsa plateada. Dentro de la bolsa, algo que cruje.",
            Parrafo.TRABAJO to "A esta hora tendría que estar enderezando clavos, pero está en la casa común con la caja encima de la mesa y medio pueblo alrededor. Eusebio la huele. Marta lee las letras de la caja en voz alta, despacio: hay palabras que no conoce nadie. Se decide abrirla delante de todos.",
            Parrafo.GENTE to "Esta tarde se reparte el cereal: una cucharada para cada uno, en la palma de la mano, los niños primero. Nora se lo come grano a grano para que le dure. Agustina cierra los ojos al masticar y dice que así sabía algo que comió una vez de niña. No se acuerda de qué.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que alguien metió esa caja en una tienda pensando que la compraría alguien el martes. El martes no llegó nunca. Ha tardado mucho en llegar a la boca de alguien, pero ha llegado. Se chupa el azúcar de los dedos. No se lava las manos en todo el día.",
        ),
        "tornado" to unDia(
            Parrafo.MANANA to "Hoy amanece con un calor pegajoso y un silencio raro: no cantan los pájaros, y el gallo de Eusebio se ha escondido debajo de una carretilla. Los viejos miran al norte. El cielo de ese lado tiene un color de moretón que nadie quiere nombrar.",
            Parrafo.TRABAJO to "A esta hora el cielo del norte se pone verde, y de él baja una cuerda negra que toca el llano y se mueve. Marta toca la campana. El pueblo entero corre al sótano de la casa común, apretado, a oscuras, mientras afuera el viento arranca láminas y las tira como hojas.",
            Parrafo.GENTE to "Esta tarde, cuando pasa, salen a contar: tres láminas, un pedazo del techo de la casa común, una gallina que nadie encuentra. Nada más. Entonces habla la radio. La voz de Los Silos dice que la cuerda negra tocó Piedra Blanca, que allí no quedó un techo y que piden manos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Piedra Blanca, a dos días de camino, donde estuvo una vez de niña para una boda. Piensa en sus techos rojos, en la gente que bailaba. Piensa que el pacto dice que alguien tiene que ir. Mira a Gabino, que ya la está mirando. Los dos saben quién.",
        ),
        "sale_piedra" to unDia(
            Parrafo.MANANA to "Hoy, antes del alba, {nombre} sale hacia Piedra Blanca con Gabino y dos más, con martillos, una bolsa de clavos, maíz tostado y las cantimploras llenas. Nora la abraza en el portón sin decir nada. Agustina le mete en el morral una tortilla de más. Matías le dice que vuelva.",
            Parrafo.TRABAJO to "A esta hora camina por la carretera vieja hacia el norte, entre coches oxidados y matorrales, con el sol de frente y el morral cada vez más pesado. Gabino silba. Los otros dos discuten de nada. Cada tanto se paran a mirar el llano en todas direcciones. Nada. Siguen.",
            Parrafo.ANOCHECER to "Esta noche duermen dentro de un autobús volcado a la orilla de la carretera, con la puerta atrancada y un fuego chico dentro de una lata. Gabino se duerme enseguida. {nombre} hace la primera guardia, mirando por una ventana sin cristal un llano que no es el suyo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el Remanso, que a esta hora estará encendiendo la bombilla de la plaza sin ella. Piensa que es la primera noche que duerme fuera desde que nació Nora. Piensa que el pueblo aguantará. Que ella también. Lo piensa varias veces, por si acaso.",
        ),
        "vuelve_piedra" to unDia(
            Parrafo.MANANA to "Hoy {nombre} vuelve de Piedra Blanca con Gabino y los otros dos, morenos, flacos, con las manos llenas de cortes y un saco de sal a la espalda. Desde lejos ven el humo de las cocinas del Remanso, y luego el muro, y en el muro a Nora, que los ha visto primero.",
            Parrafo.GENTE to "Esta tarde, en la fogata, cuentan lo de Piedra Blanca: los techos levantados, la gente que lloraba al verlos llegar, el viejo que les dio la sal y les besó las manos. Gabino exagera. {nombre} lo corrige. Nora se queda dormida en sus rodillas antes del final.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la sal es lo que menos pesaba del viaje. Piensa en las caras de Piedra Blanca cuando vieron llegar a cuatro desconocidos con martillos. Piensa que el día que el Remanso pida ayuda, alguien vendrá así. Tiene que creerlo. Lo cree.",
        ),
        "nora_lee" to unDia(
            Parrafo.MANANA to "Hoy, mientras {nombre} desgranaba maíz en el escalón, Nora se ha sentado a su lado con una revista de la abuela y, sin pedir ayuda y sin el dedo, ha leído una página entera en voz alta: el anuncio de un coche rojo que prometía la libertad. Ha leído libertad sin trabarse.",
            Parrafo.GENTE to "Esta tarde Nora lee la página otra vez en la fogata, delante de todos, y luego otra, y otra, hasta quedarse sin voz. Agustina la escucha con los ojos cerrados y los labios moviéndose, porque se sabe las revistas de memoria. Cuando Nora se equivoca, no la corrige.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su hija ya puede leer cualquier cosa que encuentre en el llano: un letrero, una lata, una carta, un aviso de peligro. Piensa que eso es mejor arma que la escopeta. Piensa que también es una puerta. Y que ya no se podrá cerrar.",
        ),
        "muro_este" to unDia(
            Parrafo.MANANA to "Hoy se cierra por fin el lado este del muro, el del hueco que llevaba años tapado con un carro volcado. Desde temprano está ahí medio pueblo con las últimas tablas, una puerta de armario y dos láminas de la carretera. Esteban vigila sin vigilar: mira cómo trabajan.",
            Parrafo.TRABAJO to "A esta hora clava la última tabla del lado este, una tabla gris con una flecha pintada que señala hacia algún sitio que ya no existe. La sujeta Gabino. Ella le pone cuatro clavos, porque es la última. El pueblo entero aplaude. No sabe dónde poner el martillo.",
            Parrafo.ANOCHECER to "Esta noche, por primera vez, el pueblo está cerrado entero, y el carro volcado vuelve a ser un carro, apartado junto a las trojes. Marta ha dicho que esta noche el lado este no necesita guardia. Esteban va igual, por costumbre. {nombre} lo acompaña.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un muro cerrado no es más fuerte que uno abierto, solo más tranquilo. Piensa que hoy el pueblo dormirá de otra manera, sin la mitad del oído puesta en el hueco. Piensa que dormir entero también es algo que se construye. Tabla a tabla.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "colona", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + AUXILIO_MANANA + ASEDIO_MANANA + ENCIERRO_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + AUXILIO_TRABAJO + ASEDIO_TRABAJO + ENCIERRO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + AUXILIO_PUEBLO + ASEDIO_PUEBLO + ENCIERRO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + AUXILIO_ANOCHECER + ASEDIO_ANOCHECER + ENCIERRO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
