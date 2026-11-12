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
 * El cuento de Ipy, campesino de una aldea de Tebas en tiempos de Ramsés II. Sigue la gramática y el tono de
 * `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su año lo manda el río: inundación, siembra y cosecha. No hay monedas: las cosas se cambian por grano. Casi nadie
 *   sabe leer, salvo los escribas. Al otro lado del río están las tumbas de los reyes y la aldea de los que las hacen.
 * - El campo es del templo; él lo trabaja y le dejan una parte.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG1.kt`):
 * - Del 28 de agosto al 27 de octubre de cada año, con el campo bajo el agua, está en la obra del rey, en la otra
 *   orilla: el estado «obra». Duerme allí. Sus variantes de la mañana, el trabajo, el pueblo, la gente y el anochecer
 *   son otras.
 * - Baket, su madre, muere en la temporada 3 (`muere_baket`): sus variantes llevan `hasta`.
 * - Amenemope nace en la temporada 4 (`nace_amenemope`); el embarazo de Henut va de `espera_henut` (temporada 3) a
 *   `nace_amenemope`.
 * - Las fiestas que vuelven cada año no nombran a Baket ni a Amenemope.
 * - El trabajo del campo cambia con la estación (`meses`): se espantan pájaros del trigo verde de noviembre a febrero,
 *   se siega de marzo a mayo, se trilla en junio y julio, se siembra en noviembre y diciembre.
 */
internal object PiezasIpy {
    /** El estado «obra» del calendario: los dos meses de la crecida, en que trabaja en la obra del rey. */
    private const val OBRA = "obra"

    /** Los meses en que se duerme en el techo, por el calor. */
    private val CALOR = setOf(4, 5, 6, 7, 8, 9, 10)

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se levanta antes que el sol, cuando el cielo sobre los montes del este se pone del color del cobre. Come de pie un pan y un trago de cerveza espesa, que es el desayuno de todo Egipto, y sale descalzo con la azada al hombro."),
        v("La primera luz entra por la puerta, que da al patio, y con ella las moscas. {nombre} se lava la cara en la jarra del agua y se pinta los ojos con un poco de negro, como todos, para que el sol no se los queme. Luego sale a la calle de barro, que todavía está fresca."),
        v("Lo despiertan los burros de la calle, que rebuznan a la vez como si se hubieran puesto de acuerdo. Desde el techo, donde la familia duerme cuando hace calor, se ve el río, gris y quieto, y al otro lado los montes de los muertos, rosados con la primera luz.", meses = CALOR),
        v("En la casa se desayuna sentado en el suelo: pan, cebolla, la cerveza de ayer. Henut ya está moliendo el grano del pan de hoy, de rodillas, y no levanta la vista. {nombre} le deja al lado un trozo de su cebolla, que es su manera de decir hasta luego."),
        v("{nombre} sale con el primer graznido de los gansos del vecino Huy, que son más puntuales que nadie. La calle huele a humo de estiércol y a pan que se cuece. Los vecinos salen de sus casas al mismo tiempo, como si se hubieran puesto de acuerdo, y nadie habla todavía."),
        v("Antes de salir, {nombre} toca el amuleto que lleva colgado del cuello, el ojo de Horus, para que el día no le muerda. Afuera, el sol sale sobre el desierto del este y en un momento la luz llega a todos los campos a la vez, como agua."),
        v("Paser se despierta con él y lo sigue hasta la puerta, frotándose los ojos. Quiere ir al campo, siempre quiere ir al campo. {nombre} le dice que hoy no, y Paser se vuelve a la estera sin protestar, porque sabe que mañana tampoco."),

        v("{nombre} despierta en el campamento de la obra, en la otra orilla, entre cientos de hombres que duermen como él, en el suelo, bajo un techo de palmas. Un escriba pasa contando cabezas antes de que salga el sol. Al que falta, se le apunta.", OBRA),
        v("Amanece sobre la obra del rey: bloques de piedra por todas partes, rampas de adobe, montones de arena y, en medio, unos muros enormes que crecen. {nombre} se lava la cara en el canal y come el pan de la ración, que a veces es bueno y a veces trae arena.", OBRA),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el campo el tiempo no es conversación: el que manda es el río, y el cielo solo acompaña."),
        v("{nombre} mira el cielo como lo miran los campesinos, que es mirando el río. {cielo} Lo que diga el cielo importa poco: lo que importa es cuánto suba el agua."),
        v("{de_estacion} {cielo} Los viejos de la aldea dicen que las crecidas de antes eran mejores, y lo dicen en todas las crecidas."),
        v("{cielo} En la estación de la {estacion} el campo cambia cada día, y quien no lo mira no lo reconoce. {nombre} lo mira todos los días, por si acaso."),
        v("Lo primero que hace al salir es mirar el río: de qué color viene, por dónde va, cuánto ha subido o bajado. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En Egipto casi nunca llueve, y por eso, cuando llueve, los niños salen a la calle a mirar el cielo como si fuera una fiesta."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y azul, como casi siempre, y el sol pega desde temprano.",
            "No hay una nube, y el desierto del oeste brilla tanto que hace daño mirarlo.",
            "Hace un día claro, de esos en que los montes de la otra orilla parecen estar al lado.",
        ),
        Clima.NUBES to listOf(
            "Hay unas nubes altas y finas que no van a dar nada, ni sombra.",
            "El cielo está velado, y el sol parece un disco de cobre visto a través del agua.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, que casi nunca pasa: unas gotas gordas que levantan polvo y olor a barro.",
            "Cae una lluvia rara y corta, y las paredes de adobe se manchan de oscuro.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del norte, el que hincha las velas de los barcos que suben el río.",
            "Hay viento, y los cañaverales de la orilla se doblan todos a la vez.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla sobre el río, y las barcas aparecen y desaparecen como fantasmas.",
            "Amaneció con niebla, y los campos están mojados de rocío hasta el mediodía.",
        ),
        Clima.POLVO to listOf(
            "El viento del desierto trae arena, y el cielo se pone amarillo y bajo.",
            "Hay arena en el aire, y se mete en los ojos, en el pan y en la cerveza.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.INUNDACION to listOf(
            "Es la estación de la inundación, cuando el río cubre los campos y la aldea queda como una isla.",
            "En la inundación el agua llega hasta el borde de las aldeas, y de una a otra se va en barca.",
            "Es tiempo de crecida, y el agua trae el lodo negro que hace de Egipto lo que es.",
        ),
        Estacion.SIEMBRA to listOf(
            "Es la estación de la siembra, cuando el agua se retira y deja la tierra negra y blanda.",
            "En la siembra todo el campo está verde y tierno, y los pájaros también lo saben.",
            "Es tiempo de siembra, el más fresco del año, y por las noches hace frío de verdad.",
        ),
        Estacion.COSECHA to listOf(
            "Es la estación de la cosecha, cuando el río está bajo y los campos se ponen dorados.",
            "En la cosecha cada día hace más calor, y el polvo se levanta con cada paso.",
            "Es tiempo de cosecha, y los escribas andan por los campos contando lo que es del rey.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora saca agua del canal con el cigoñal: un palo largo con un cubo en una punta y un contrapeso de barro en la otra. Sube, gira, vacía, baja. Es un trabajo de burro, y él lo hace porque no tiene burro.", hacer = Hacer.TRABAJO),
        v("A esta hora arregla la acequia que lleva el agua a su campo, sacando lodo con la azada y rehaciendo los bordes con las manos. Una acequia rota es agua perdida, y agua perdida es pan perdido. Los vecinos pasan, miran y le dan consejos que no pide.", hacer = Hacer.TRABAJO),
        v("A esta hora trenza cuerdas de fibra de palmera, sentado a la sombra de la pared, para el cigoñal, para el buey y para lo que haga falta. La cuerda buena se hace despacio, y la mala se rompe deprisa. {nombre} la hace despacio.", hacer = Hacer.TRABAJO),
        v("A esta hora espanta pájaros del trigo verde, que es el trabajo más tonto del año y el más necesario. Grita, agita una tela, tira piedras. Los pájaros se van al campo del vecino, y luego vuelven.", hacer = Hacer.TRABAJO, meses = setOf(11, 12, 1, 2)),
        v("A esta hora siega con la hoz de madera y dientes de pedernal, cortando el trigo por debajo de la espiga y dejando la paja en pie. Detrás de él va Henut atando gavillas. El sol pega sin piedad, y a nadie se le ocurre quejarse delante del escriba.", hacer = Hacer.TRABAJO, meses = setOf(3, 4, 5)),
        v("A esta hora trilla en la era: los bueyes del vecino dan vueltas sobre el trigo y él los arrea con una vara, cantando la canción de la trilla, que les dice cosas a los bueyes que los bueyes no entienden. Después hay que aventar, y el viento ayuda o no.", hacer = Hacer.TRABAJO, meses = setOf(6, 7)),
        v("A esta hora siembra: echa el grano a puñados sobre el lodo todavía blando y después hace pasar a las cabras para que lo entierren con las pezuñas. Así se ha hecho aquí desde siempre, y las cabras no saben que trabajan.", hacer = Hacer.TRABAJO, meses = setOf(11, 12)),

        v("A esta hora come pan con cebolla y un trozo de pescado seco del río, sentado en el borde del campo. Bebe cerveza espesa de un jarro que pasa de mano en mano. La cerveza es comida y bebida a la vez, y por eso nadie la desprecia.", hacer = Hacer.COMER),
        v("A esta hora come en casa, sentado en el suelo con la familia: pan, lentejas con cebolla, unos dátiles. Henut le ha puesto ajo a las lentejas, que dicen que da fuerza. {nombre} se come el ajo y no se siente más fuerte, pero sí más ajo.", hacer = Hacer.COMER),
        v("A esta hora come lo que hay, que hoy es pan y un poco de queso. El pan de Tebas tiene arena, como todo el pan de Egipto, y por eso a los viejos se les gastan los dientes. {nombre} mastica con cuidado, por los suyos.", hacer = Hacer.COMER),

        v("A esta hora descansa a la sombra, porque al mediodía el sol de Tebas no deja hacer nada más. Se tumba bajo el sicómoro del borde del campo con otros campesinos, y se habla de agua, de deudas y de nada. Alguno ronca.", hacer = Hacer.LIBRE),
        v("A esta hora duerme un rato en el techo, bajo un toldo de esteras, mientras el sol aprieta. Las moscas no descansan nunca. {nombre} las espanta con la mano sin abrir los ojos, y las moscas vuelven sin abrir los suyos.", hacer = Hacer.LIBRE),
        v("A esta hora no hace nada, y lo hace con calma. Se sienta a la puerta de la casa, a la sombra, y mira pasar a la gente, a los burros y al perro del vecino, que también busca la sombra. El calor aplasta la calle como una mano.", hacer = Hacer.LIBRE),

        v("A esta hora está con los vecinos en la puerta de la casa de Huy, que tiene el mejor banco de la calle. Se habla de la crecida, del escriba y de quién le debe cebada a quién. Los niños juegan a la guerra con cañas, y siempre gana el rey.", hacer = Hacer.GENTE),
        v("A esta hora está en el techo con Henut, mirando cómo se pone el sol detrás de los montes de la otra orilla, donde están las tumbas. Dicen que el sol se muere allí cada tarde y nace cada mañana. A esta hora, con la cerveza, {nombre} se lo cree.", hacer = Hacer.GENTE),
        v("A esta hora está junto al canal con otros hombres de la aldea, lavándose el polvo del día. El agua está tibia y huele a lodo. Alguien cuenta un chiste sobre el escriba, que el escriba ya se sabe, y todos se ríen igual.", hacer = Hacer.GENTE),

        v("A esta hora duerme en el techo, sobre una estera, porque dentro de la casa hace demasiado calor. Las estrellas están tan cerca que parece que se podrían tocar. Lo último que se oye es un perro, lejos, y el río, que no se oye pero se sabe.", hacer = Hacer.DORMIR, meses = CALOR),
        v("A esta hora duerme dentro de la casa, sobre la estera, tapado hasta las orejas con una manta de lino, porque las noches de la estación fresca pican. Afuera, el perro se ha metido en el portal, y nadie tiene valor para echarlo.", hacer = Hacer.DORMIR, meses = setOf(11, 12, 1, 2, 3)),
        v("A esta hora duerme, y la casa entera duerme con él: Henut a su lado, los niños hechos un ovillo, el perro atravesado en la puerta. De vez en cuando pasa un burro por la calle, solo, y nadie sabe nunca de quién es.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, con la cabeza sobre un reposacabezas de madera, que es más cómodo de lo que parece. Henut duerme a su lado y los niños a los pies. De vez en cuando Tia habla en sueños, y nadie entiende qué dice.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, como duermen los que trabajan con la azada. Ni los mosquitos lo despiertan, y eso que los mosquitos del río son de los que no perdonan.", hacer = Hacer.DORMIR),

        v("A esta hora arrastra piedra. Un bloque grande va sobre un trineo de madera, y veinte hombres tiran de las cuerdas mientras otro echa agua delante para que resbale. Un capataz marca el ritmo con palmas. {nombre} tira y no piensa, que es lo mejor que se puede hacer.", OBRA, Hacer.TRABAJO),
        v("A esta hora hace adobes para las rampas: barro del río, paja picada, el molde de madera, y el adobe al sol, en hileras largas que no se acaban nunca. Un escriba los cuenta por la tarde. Si faltan, falta la ración.", OBRA, Hacer.TRABAJO),
        v("A esta hora carga cestas de arena de un lado a otro de la obra, al hombro, por una rampa de adobe que sube con los muros. Desde arriba se ve el río cubriendo los campos, y entre ellos, en algún sitio, el suyo.", OBRA, Hacer.TRABAJO),
        v("A esta hora reparten la ración de la obra: pan, cebollas, cerveza y, algunos días, pescado. Se come sentado en las piedras, entre el polvo. El pan del rey no es mejor que el de Henut, pero es del rey, y eso lo dice el escriba.", OBRA, Hacer.COMER),
        v("A esta hora paran todos a la sombra de los muros, que dan la única sombra de la obra. Se duerme sentado, con la espalda contra la piedra del rey. Alguien canta bajito una canción de su aldea, y otros, que son de otras aldeas, se la aprenden.", OBRA, Hacer.LIBRE),
        v("A esta hora está con los de su cuadrilla junto al fuego del campamento, hombres de otras aldeas que solo ve en la obra. Se cuentan de dónde son, cómo les fue la cosecha y qué capataz es peor. Al final todos están de acuerdo en el capataz.", OBRA, Hacer.GENTE),
        v("A esta hora duerme en el campamento de la obra, en el suelo, entre cientos de hombres que roncan. Echa de menos el techo de su casa y su estera de siempre. Antes de dormirse piensa en Henut y en los niños, al otro lado del río, durmiendo sin él.", OBRA, Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La aldea son unas cuantas casas de adobe pegadas, con los techos planos llenos de cosas: leña, cebollas, esteras, un perro. Alrededor, los campos, y alrededor de los campos, el desierto, que empieza de golpe donde termina el agua. {entorno}"),
        v("{entorno} Al otro lado del río están los montes de los muertos, donde se entierra a los reyes en tumbas que nadie ve. En la orilla de los vivos, los templos de Amón son tan grandes que la aldea, a su lado, parece una cesta olvidada."),
        v("Por el río pasan barcos todo el día: barcazas de piedra, barcos de grano, la barca de algún señor con su toldo de colores. Los niños los saludan desde la orilla y los marineros, a veces, contestan. {entorno}"),
        v("En la aldea todo lo que no es campo es del templo o del rey, y lo que es campo, también un poco. El escriba Ramose lo tiene todo apuntado: quién siembra qué, cuánto da y cuánto debe. {entorno}"),
        v("{entorno} En el mercado de la orilla no se paga con nada que brille: se cambia una cosa por otra. Un par de sandalias vale un saco de grano, más o menos, y el más o menos es lo que se discute toda la mañana."),
        v("Los canales salen del río como venas y llevan el agua a cada campo, y en cada canal hay una compuerta de barro que se abre y se cierra cuando toca. Quien abre la suya antes de tiempo se gana enemigos para todo el año. {entorno}"),

        v("La obra está en la orilla de los muertos, al pie de los montes donde se entierra a los reyes. Es una casa para el rey, para cuando muera: muros que no se acaban, columnas con su cara, patios donde caben aldeas enteras. {entorno}", OBRA),
        v("{entorno} Desde la obra se ve el río, ancho como un lago, con las aldeas asomando del agua como islas. En una de ellas está la suya, y desde aquí es solo un puñado de palmeras.", OBRA),
    )

    /** Lo que se ve alrededor según el río y el trigo. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("rio", 0.8, frases = listOf(
            "El agua cubre los campos hasta donde se ve, y la aldea asoma como una isla.",
            "Los campos están bajo el río, y entre las palmeras se va en barca.",
        )),
        Entorno("rio", 0.4, 0.8, frases = listOf(
            "El agua cubre los campos bajos, y los altos asoman como lomos de buey.",
            "El río ha cubierto la mitad de los campos, y la otra mitad espera.",
        )),
        Entorno("campo", 0.7, frases = listOf(
            "El trigo está alto y dorado, y el viento lo peina hacia el sur.",
            "Los campos están verdes y altos, y entre ellos apenas se ve a la gente.",
        )),
        Entorno("campo", 0.1, 0.7, frases = listOf(
            "En los campos el trigo crece verde y tierno.",
            "El trigo asoma en el lodo, en hileras torcidas.",
        )),
        Entorno("rio", 0.1, 0.4, frases = listOf(
            "El agua se retira y deja los campos negros de lodo.",
            "Los campos salen del agua brillando, negros y blandos.",
        )),
        Entorno("rio", 0.0, 0.1, frases = listOf(
            "Los campos están secos y pelados, esperando el agua.",
            "En los campos no hay más que rastrojo y tierra agrietada.",
        )),
    )

    private val GENTE = listOf(
        v("Henut, su mujer, teje lino y hace el pan, y sabe cuánto grano queda en la tinaja sin mirar dentro. Cuando {nombre} llega cansado, ella le pone delante el jarro de cerveza antes de que lo pida. Cuando llega de mal humor, también, pero más lejos."),
        v("Paser tiene la edad de querer ser todo: barquero, soldado, escriba. Estos días quiere ser escriba, y hace rayas en el suelo con un palo, muy serio. {nombre} no le dice que los escribas son hijos de escribas, para que le dure el gusto."),
        v("Tia, la pequeña, va desnuda como todos los niños, con un mechón de pelo trenzado a un lado de la cabeza y nada más. Sigue a su madre a todas partes, y cuando su madre muele, ella muele un grano con una piedrita, y se lo come."),
        v("Huy, el vecino, tiene gansos y una opinión para cada cosa. Los gansos se meten en el patio de {nombre}, y las opiniones también. {nombre} aguanta las dos cosas porque Huy le presta el buey, y con el buey se aguanta casi todo."),
        v("Nebamun, su hermano, vive en la otra orilla y lleva agua a la aldea de los que hacen las tumbas de los reyes, que no tiene pozo. Cruza el río de vez en cuando con historias que no se pueden contar, y las cuenta igual, en voz baja, como quien reparte pan."),
        v("Su madre, Baket, vive con ellos y duerme junto a la puerta, donde entra el fresco. Ya no va al campo, pero muele, cuida a Tia y les dice a todos lo que tienen que hacer. Cuando {nombre} le lleva la contraria, ella le recuerda quién lo cargaba de niño.", hasta = "muere_baket"),
        v("Henut espera otro hijo, y las vecinas ya le traen amuletos de la diosa con cara de hipopótamo, la que cuida a las que van a parir. Ella los cuelga todos de la pared, por si acaso. {nombre} le carga el agua, que es lo único que ella le deja hacer.", desde = "espera_henut", hasta = "nace_amenemope"),
        v("Amenemope, el pequeño, duerme en una cesta colgada de la viga, donde no llegan los escorpiones. Cuando llora, Henut mueve la cesta con un pie sin dejar de moler. {nombre} lo mira y piensa que tiene la nariz de Baket, y no lo dice.", desde = "nace_amenemope"),

        v("En la cuadrilla de {nombre} hay hombres de cinco aldeas, y cada aldea cree que es la mejor. Uno sabe contar historias, otro sabe robar pan sin que lo vean y otro no sabe nada pero lo dice todo. {nombre} se lleva bien con los tres.", OBRA),
        v("Henut y los niños están al otro lado del río, en la aldea, y {nombre} solo los ve en las fiestas, cuando dejan cruzar. Paser le manda recados con los barqueros, que llegan a medias. El último decía algo de una cabra.", OBRA),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del campo: con las manos antes que con la cabeza."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En la aldea se dice que el que cuida lo suyo no necesita lo del vecino; lo dicen los que tienen vecinos."),
        v("{dato_objeto} Son cosas que se aprenden mirando a los mayores, como casi todo en la aldea."),
        v("Un campesino tiene pocas cosas, y cada una le cuesta muchos días de trabajo. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("azada", "la azada", "una azada", "La azada",
            "La azada es de madera: un mango y una hoja atados con una cuerda en forma de A; con ella se rompe la tierra, se abren acequias y se mata algún escorpión, y cuando se rompe la cuerda se rompe todo."),
        ObjetoCuento("hoz", "la hoz", "una hoz", "La hoz",
            "La hoz es de madera curva con dientes de pedernal pegados con resina; corta el trigo a la altura de la espiga, y los dientes se cambian cuando se gastan, que es casi en cada siega."),
        ObjetoCuento("cesta", "la cesta", "una cesta", "La cesta",
            "La cesta es de fibra de palmera trenzada, con dos asas; sirve para el grano, para el lodo, para los dátiles y, cuando hace falta, para un niño dormido."),
        ObjetoCuento("jarra_cerveza", "la jarra de cerveza", "una jarra de cerveza", "La cerveza",
            "La cerveza se hace en casa con pan de cebada a medio cocer, deshecho en agua y dejado fermentar en una jarra; sale espesa, con grumos, y se bebe con una caña para no tragarse lo de abajo."),
        ObjetoCuento("pan", "el pan", "un pan", "El pan",
            "El pan se cuece en moldes de barro calientes o pegado a la pared del horno; es de trigo, tiene arena de la piedra de moler y es la base de todo, hasta de la paga."),
        ObjetoCuento("cebolla", "la cebolla", "una cebolla", "La cebolla",
            "La cebolla es la carne de los pobres, dicen en la aldea: se come cruda, con pan, a cualquier hora; en las tumbas se dejan cebollas, por si allá también hay hambre."),
        ObjetoCuento("sandalias", "las sandalias", "unas sandalias", "Las sandalias",
            "Las sandalias son de papiro trenzado y le duran lo que dura una crecida; casi siempre anda descalzo, y las sandalias van en la mano, para no gastarlas, hasta llegar adonde se las tiene que poner."),
        ObjetoCuento("amuleto", "el amuleto", "un amuleto", "El amuleto",
            "El amuleto es un ojo de Horus de loza azul colgado de un cordel; protege de las enfermedades, de los escorpiones y del mal de ojo, y si no protege es que uno hizo algo para merecerlo."),
        ObjetoCuento("red", "la red", "una red", "La red",
            "La red es de lino, con pesas de barro en un borde; en el canal se pescan con ella peces chicos y, a veces, uno grande que nadie cree hasta que lo ve."),
        ObjetoCuento("odre", "el odre", "un odre", "El odre",
            "El odre es una piel de cabra cosida y untada de grasa; guarda el agua fresca en el campo, colgado de una rama a la sombra, y sabe a cabra, pero a cabra fresca."),
        ObjetoCuento("cuerda_medir", "la cuerda de medir", "una cuerda de medir", "La cuerda de medir",
            "La cuerda de medir es la del escriba, con nudos a la misma distancia; con ella se miden los campos después de cada crecida, porque el agua borra los linderos y alguien tiene que decir dónde termina uno y empieza el otro."),
        ObjetoCuento("datiles", "los dátiles", "unos dátiles", "Los dátiles",
            "Los dátiles son el dulce de los pobres: se comen frescos en otoño y secos el resto del año, y con ellos se endulza la cerveza; la palmera da sombra, fruta, fibra y troncos, y no pide nada."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el río como otros piensan en el rey: está ahí, manda y no se le discute. Si sube mucho, se lleva casas; si sube poco, se lleva la comida. A sus {edad} años ha visto de las dos, y prefiere la primera."),
        v("Piensa a veces en la casa de millones de años que el rey se está haciendo en la otra orilla, para cuando muera. Él no tendrá nada de eso: un hoyo en la arena, una estera y unas cebollas. Pero el sol, dicen, sale para todos, y a eso se agarra."),
        v("{mundo} Él piensa en cosas más cercanas: en la cebada que debe, en el techo, en la tos de Tia. Las cosas grandes, dicen los viejos, son para los sacerdotes, que no tienen que regar."),
        v("{mundo} A sus {edad} años, {nombre} ha perdido a dos hijos pequeños, de fiebres, en la misma crecida. Están enterrados en el borde del desierto, donde no llega el agua. Cuando pasa por ahí se para un momento, y luego sigue."),
        v("No tiene tierra suya: el campo es del templo y él lo trabaja, y le dejan una parte. Tiene una casa, una azada y una familia, y hay días en que eso le parece mucho. Su padre tuvo lo mismo, y a su padre le parecía bastante."),
        v("{mundo} Por las noches, desde el techo, mira las estrellas, que en Egipto se ven todas. Los sacerdotes saben leerlas y dicen cuándo subirá el río. {nombre} no sabe leer ni estrellas ni nada, y no le hace falta: el río sube igual."),
    )

    private val MUNDOS = mapOf(
        "paz" to listOf(
            "En la aldea se dice que el rey es fuerte, y que por eso hay paz.",
            "Este año no se habla de guerras, sino de las obras del rey.",
            "Dicen que el rey lleva tanto tiempo reinando que ya nadie se acuerda de otro.",
        ),
        "jubileo" to listOf(
            "Se habla del jubileo del rey, que lleva treinta años en el trono.",
            "Dicen que con el jubileo el rey se ha renovado, como el río cada año.",
        ),
        "crecida_baja" to listOf(
            "Este año el río no subió lo que debía, y en la aldea se nota.",
            "Se dice que los dioses están enojados, aunque nadie sabe por qué.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando oscurece, Henut enciende la lámpara de aceite y la casa se llena de una luz amarilla que no llega a los rincones. Se cena pan con cerveza y lo que haya, y se apaga pronto: el aceite cuesta. Mañana el sol sale temprano, como siempre."),
        v("Al caer la tarde, el sol se esconde detrás de los montes de la otra orilla y todo se pone rojo: el río, los campos, las paredes. {nombre} vuelve a casa por el borde del canal, despacio, con la azada al hombro y el día entero en las piernas."),
        v("Anochece deprisa, como siempre en Egipto: el sol se va y en un momento es de noche. Las ranas del canal empiezan a cantar todas a la vez. {nombre} se sienta en el techo con un jarro de cerveza y se queda mirando cómo salen las estrellas."),
        v("De noche la aldea se queda quieta, y solo se oyen los perros y el río. {nombre} sube a dormir al techo, con la estera bajo el brazo. Lo último que ve son las luces de los templos de la otra orilla, que no se apagan nunca.", meses = CALOR),
        v("Se cena en silencio, porque la boca está ocupada. Después Henut cuenta lo que se dice en el pozo y {nombre} lo que se dice en el campo, y casi siempre es lo mismo. Luego se tapa la jarra del agua para que no entren los bichos, y a dormir."),
        v("La última luz se queda un rato en los montes de los muertos, rosada, cuando la aldea ya está a oscuras. {nombre} la mira desde la puerta. Allí está enterrado su padre, en un hoyo en la arena, y a esa hora le parece que lo saluda."),

        v("Al caer la tarde se acaba el trabajo, y el escriba apunta en un trozo de cerámica lo que hizo cada cuadrilla. Los hombres se lavan en el canal, comen y se tumban. {nombre} mira hacia la otra orilla, donde se encienden las lámparas de las aldeas, y busca la suya.", OBRA),
        v("De noche el campamento de la obra huele a humo y a sudor, y se oye hablar con muchos acentos. Los guardias pasan con antorchas. {nombre} se duerme pronto, con las manos llenas de cuerda y los pies llenos de barro.", OBRA),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "diques" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la limpieza de los canales. Todos los hombres de la aldea salen con azadas y cestas a sacar el lodo que dejó la crecida y a levantar los diques que se cayeron. Un escriba del templo dice quién va a qué canal, y nadie está contento con el suyo.",
                Parrafo.PUEBLO to "Un canal sucio no lleva agua, y un dique roto la tira donde no debe. Por eso cada año, en la estación fresca, se rehacen todos. Es trabajo de todos para los campos de todos, y aun así cada uno mira de reojo si el vecino trabaja tanto como él.",
                Parrafo.PENSAMIENTO to "{nombre} saca lodo y piensa que el mismo lodo que hoy le pesa en la espalda es el que hace crecer el trigo. El río lo trae, el río lo deja y el río lo pide de vuelta. Todo en Egipto es así: un préstamo del río.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días los canales se llenan de hombres con barro hasta la cintura, pasándose cestas de mano en mano en una fila larga. Desde lejos parecen hormigas que trabajan para un rey que nunca baja a mirar.",
                "Cada tarde el escriba recorre los canales y apunta lo que se hizo. Si un tramo quedó mal, se rehace mañana, y se rehace con más ganas si el escriba lo miró dos veces.",
                "En los diques nuevos, todavía blandos, los niños dejan huellas de pies que el sol endurece. Los mayores los regañan, y luego dejan las suyas, más grandes, cuando nadie mira.",
            )),
        ),
        "siega" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siega. Antes de que salga el sol la aldea entera va al campo con las hoces de dientes de pedernal, y en las casas solo quedan los viejos y los perros. El trigo está alto y seco, y suena cuando lo toca el viento.",
                Parrafo.PUEBLO to "En los campos los segadores avanzan en fila cortando el trigo por debajo de la espiga; detrás van las mujeres juntando, y los niños recogiendo lo que cae. Un flautista toca para marcar el ritmo, y alguien canta la canción de la siega, que es muy larga y muy vieja.",
                Parrafo.ANOCHECER to "Al anochecer los segadores vuelven con las manos cortadas y la espalda doblada. {nombre} se lava en el canal y se queda un rato con los pies en el agua, sin pensar en nada. Henut lo llama para cenar dos veces, y a la tercera va.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días los burros van y vienen cargados de gavillas hasta la era, donde los bueyes pisan el trigo dando vueltas. El polvo de la trilla se ve desde lejos, como el humo de un incendio que no quema.",
                "En la era se avienta el grano lanzándolo al aire con palas de madera: el viento se lleva la paja y el grano cae. Si no hay viento, se espera, y se maldice al viento, y el viento llega cuando quiere.",
                "Los escribas del templo andan por los campos con su cuerda y su paleta, midiendo, contando sacos y apuntando lo que es del rey. Los campesinos los miran con la misma cara con que miran a las langostas.",
                "Las espigas que se caen son de los pobres: hay mujeres y niños que van detrás de los segadores recogiéndolas una a una. Nadie los echa, porque hoy recogen ellos y mañana puede recoger uno.",
            )),
        ),
        "valle" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es la Fiesta del Valle, con la luna nueva. La barca de Amón sale del templo de Karnak y cruza el río hacia la orilla de los muertos, a visitar los templos de los reyes. Detrás de ella cruzan miles de personas, en todo lo que flota.",
                Parrafo.PUEBLO to "En la otra orilla las familias van a las tumbas de los suyos con flores, pan, cerveza y comida para todo el día. Se come y se bebe junto a los muertos, y se canta para ellos. Hay quien se emborracha a propósito, para estar más cerca.",
                Parrafo.GENTE to "{nombre} cruza con Henut y los niños en la barca de un vecino, apretados entre cestas de comida y ramos de flores. Paser lleva el ramo más grande y no deja que nadie se lo toque. Tia se duerme antes de llegar a la otra orilla.",
            ),
            dia(
                Parrafo.PENSAMIENTO to "Anoche la familia durmió junto a la tumba de los suyos, como mucha gente. {nombre} les habló un poco antes de dormirse, en voz baja, como si pudieran oírlo: cómo va el campo, cómo crecen los niños. No le contestaron, pero tampoco lo interrumpieron.",
                Parrafo.ANOCHECER to "Hoy vuelven a la aldea cansados, quemados por el sol y con la cabeza pesada de cerveza. La barca de Amón ya volvió a su templo. {nombre} se acuesta temprano, y antes de dormirse piensa que el año que viene volverá a cruzar, y el otro, mientras pueda.",
            ),
        )),
        "escriba" to unDia(
            Parrafo.MANANA to "Hoy viene el escriba Ramose a medir el campo y a contar el grano. Llega en burro, con un ayudante que le lleva la paleta, la cuerda y la sombrilla. Se sienta a la sombra y empieza a preguntar, y cada pregunta cuesta algo.",
            Parrafo.PUEBLO to "El escriba mide el campo con la cuerda de nudos, cuenta los sacos de la era y lo apunta todo en un papiro, con tinta roja y negra. Lo que le toca al templo se carga en burros y se va. Lo que queda es de {nombre}, y es menos de lo que parecía esta mañana.",
            Parrafo.PENSAMIENTO to "{nombre} no sabe leer lo que el escriba escribe, y el escriba lo sabe. Por eso {nombre} cuenta los sacos él también, con piedritas, y cuando las cuentas no coinciden se calla, porque el escriba tiene la tinta y él solo piedritas.",
        ),
        "epagomenos" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empiezan los cinco días que no son de ningún mes, los que se agregan al final del año para que cuadre. Son días raros, de cuidado. Hoy nació Osiris, el que muere y vuelve, y no conviene empezar nada nuevo.",
                Parrafo.PUEBLO to "En la aldea se trabaja poco y se reza más. Las mujeres cuelgan amuletos en las puertas, los viejos no salen de casa, y los niños preguntan por qué y nadie les sabe contestar bien. Así son estos días.",
                Parrafo.GENTE to "Henut no deja que nadie barra la casa en estos cinco días, porque barrer se lleva la suerte. {nombre} no ha barrido en su vida, así que no le cuesta nada obedecer.",
            ),
            dia(
                Parrafo.PUEBLO to "Segundo de los días de fuera del año, el del nacimiento de Horus, el halcón. Se le dejan ofrendas pequeñas en la capilla de la aldea: un pan, unas flores, un poco de cerveza. Horus es el del ojo, y por eso hoy se mira todo con cuidado.",
                Parrafo.ANOCHECER to "Esta noche se cena temprano y se cierra bien la puerta, por si acaso. {nombre} se ríe de los que tienen miedo, pero cierra igual, y comprueba dos veces la tranca.",
            ),
            dia(
                Parrafo.PUEBLO to "Tercero de los cinco días, el de Set, el del desierto y las tormentas, el que mató a su hermano. Es el peor de todos: hoy no se cruza el río, no se sale de viaje y no se discute con nadie. En la aldea todos andan muy amables, por miedo.",
                Parrafo.PENSAMIENTO to "{nombre} pasa el día sin hacer nada, que le cuesta más que trabajar. Piensa que Set también es un dios, y que alguien tendrá que quererlo. Pero no lo dice en voz alta, por si Set lo oye y se encariña.",
            ),
            dia(
                Parrafo.PUEBLO to "Cuarto de los cinco días, el del nacimiento de Isis, la madre, la que juntó los pedazos de su marido. Es el día de las mujeres: van juntas a la capilla de la aldea con flores, y vuelven cantando. Los hombres se quedan en casa, cuidando mal a los niños.",
                Parrafo.GENTE to "Henut vuelve de la capilla con una flor de loto en el pelo y la cara tranquila. Dice que le ha pedido a Isis cosas que no le va a contar. {nombre} no pregunta: con los ruegos de las mujeres, mejor no saber.",
            ),
            dia(
                Parrafo.PUEBLO to "Último de los días de fuera del año, el de Neftis, la hermana callada. Mañana empieza el año nuevo, y en todas las casas se limpia, se tira lo roto y se preparan lámparas nuevas. La aldea huele a aceite y a polvo removido.",
                Parrafo.ANOCHECER to "Esta noche se encienden lámparas en todas las casas para recibir el año. {nombre} se queda despierto un rato en el techo, mirando hacia el este, por donde dicen que va a salir la estrella que trae la crecida. No la ve, pero hace como que sí.",
            ),
        )),
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, el primer día de la estación de la inundación. En los templos se hacen ofrendas desde el alba, y en las casas se estrena algo: una estera, un jarro, unas sandalias, si hay. Se le desea un buen año a todo el que pasa, y hasta a los burros.",
            Parrafo.PUEBLO to "En la aldea hay comida de fiesta: pan dulce con dátiles, cerveza fuerte y, en las casas que pueden, un ganso. Los niños llevan collares de flores. Se espera la crecida con la cara de quien espera a un pariente que siempre llega, aunque nunca se sabe en qué estado.",
            Parrafo.ANOCHECER to "Al anochecer se encienden lámparas en las puertas y la calle entera brilla. {nombre} se sienta en el techo con Henut y los niños a mirar el río, que todavía está bajo y quieto. Dicen que este año subirá bien. Lo dicen todos los años.",
        ),
        "crecida" to unDia(
            Parrafo.MANANA to "Hoy el río empezó a subir. Se nota primero en el color: el agua, que estaba clara, viene verde, y en unos días vendrá roja de lodo. Los viejos bajan a la orilla a mirarla, y uno clava un palo en el barro para ver mañana cuánto ha subido.",
            Parrafo.PUEBLO to "En la aldea todos hablan de lo mismo: cuánto va a subir. Si sube poco, no habrá agua para los campos altos; si sube mucho, se llevará casas. En los templos hay un pozo con marcas en la pared que dice cuánto, y los sacerdotes lo leen como si fuera un libro.",
            Parrafo.PENSAMIENTO to "{nombre} mira el agua verde y piensa que viene de muy lejos, de tierras que nadie de la aldea ha visto. Dicen que allá llueve, que el agua cae del cielo como si nada. A él le cuesta creerlo: aquí el agua viene del río, como debe ser.",
        ),
        "opet" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la fiesta de Opet. La barca de Amón, con la estatua del dios escondida en una cabina dorada, sale del templo de Karnak a hombros de los sacerdotes y va hasta el templo de Luxor, a visitar su otra casa. Todo Tebas sale a verla pasar.",
                Parrafo.PUEBLO to "Por el camino de las esfinges van la barca de Amón, la de su mujer, Mut, y la de su hijo, Jonsu, entre músicos, bailarinas y soldados. La gente se apretuja para verlas. Cuando la barca se detiene se le puede preguntar algo al dios, y el dios, a veces, contesta.",
                Parrafo.GENTE to "{nombre} lleva a Paser sobre los hombros para que vea la barca, y el chico grita cada vez que pasa algo dorado, que es todo el tiempo. Henut lleva a Tia, que se ha dormido con tanto ruido. El templo reparte pan y cerveza, y {nombre} hace la cola dos veces.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días Tebas es una sola fiesta: hay música en las calles, puestos de comida, acróbatas y gente de todas las aldeas que ha venido a ver al dios. El templo reparte pan y cerveza, y la cerveza del templo es más fuerte que la de casa.",
                "Mientras Amón está en Luxor, en el templo se hacen ceremonias que solo ven los sacerdotes. Afuera, la gente se las imagina. Cada uno cuenta una distinta, y todas son mejores que la verdad.",
                "El río ya está alto y el campo no pide nada, así que estos días se puede ir a la fiesta sin remordimiento. Es la mejor época para un campesino: el trabajo lo hace el agua, y el dios invita.",
            )),
        ),
        "leva" to unDia(
            Parrafo.MANANA to "Hoy viene el escriba a llamar a los hombres a la obra del rey: el campo está bajo el agua y no hay nada que hacer en él. {nombre} ya lo sabía, como cada año. Se lleva una estera, un cuenco y unas sandalias, y Henut le pone pan para el primer día.",
            Parrafo.GENTE to "Los hombres de la aldea se juntan en la orilla y suben a las barcas que los cruzan al otro lado. Las mujeres y los niños los despiden desde el agua, como si fueran a la guerra, aunque van a cargar piedra. Paser corre por la orilla hasta que se le acaba la orilla.",
            Parrafo.ANOCHECER to "Esta noche {nombre} duerme en el campamento de la obra, en el suelo, entre hombres de otras aldeas. Cuenta las noches que faltan con los dedos, y le faltan dedos. Desde aquí se ven las luces de su orilla, chiquitas, y se duerme mirándolas.",
        ),
        "joiak" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan los ritos de Osiris, en el mes de Joiak. Se cuenta otra vez su historia: lo mató su hermano Set, lo hizo pedazos, e Isis los fue juntando por todo Egipto hasta devolverle la vida. En los templos, dos mujeres lloran por él haciendo de sus hermanas.",
                Parrafo.PUEBLO to "Estos días se hacen figuras de Osiris con tierra y grano, y se riegan hasta que el grano germina y la figura se pone verde. Es el dios que muere y vuelve, como el trigo que se entierra y brota. Los campesinos lo entienden mejor que los sacerdotes.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que Osiris es a la vez el dios de los muertos y el del trigo, y que eso tiene sentido: los dos se meten en la tierra y los dos, dicen, vuelven. A su padre no lo ha visto volver todavía. El trigo sí vuelve, todos los años, y con eso se conforma.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días en las casas se pone una figurita de barro con grano, para que germine. La de Paser germinó torcida, y él dice que es porque Osiris estaba cansado.",
                "Mientras dura Joiak se cuentan por las noches las historias de Osiris: cómo lo engañaron con un cofre, cómo lo buscó Isis, cómo lo vengó su hijo Horus. Los niños se las saben y corrigen al que las cuenta mal.",
                "Al final de Joiak se levanta el pilar de Osiris, que es su espalda: se le pone de pie con cuerdas mientras todos gritan. Si el pilar se queda derecho, el año será bueno. Hasta ahora siempre se ha quedado derecho, por si acaso.",
            )),
        ),
        "baja_agua" to unDia(
            Parrafo.MANANA to "Hoy el escriba dice que se acaba la obra: el agua está bajando y los campos empiezan a asomar. {nombre} cruza el río con los demás en las barcas, más flaco que cuando se fue y con las manos de otro. En la orilla lo esperan Henut y los niños.",
            Parrafo.PUEBLO to "El campo sale del agua cubierto de lodo negro y brillante, que huele a río y a vida. Los linderos han desaparecido, como cada año, y los vecinos ya discuten dónde estaba cada uno. Mañana vendrá el escriba con su cuerda a decidirlo.",
            Parrafo.GENTE to "En casa, Paser le cuenta todo lo que ha pasado en estos dos meses, sin respirar y sin orden. Tia le revisa las manos llenas de callos como quien revisa un fardo. Henut no dice nada: le pone delante la mejor cerveza que ha hecho en todo el tiempo.",
        ),
        "siembra" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siembra. El agua se fue y dejó la tierra negra y blanda, y hay que sembrar antes de que se endurezca. {nombre} sale con el saco de semilla al hombro y la azada, y detrás de él, las cabras del vecino, que hoy trabajan.",
                Parrafo.PUEBLO to "En el campo se echa el grano a puñados sobre el lodo, y después se hace pasar a las cabras para que lo entierren con las pezuñas. Es la manera más vieja de sembrar, y la más barata. Las cabras no saben que trabajan, y se comen lo que pueden.",
                Parrafo.PENSAMIENTO to "{nombre} siembra y piensa que está enterrando la comida de todo el año. Si el grano no brota, no hay pan; si brota, hay pan y escriba. No sabe cuál de las dos cosas le preocupa más.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días todos los campos de la aldea están llenos de gente encorvada y de cabras que pisan. Los pájaros esperan en las palmeras, y los niños los espantan a gritos.",
                "En el campo ya sembrado asoman las primeras hojas, verdes y finas como pelos, entre el lodo que se agrieta. Los viejos dicen que el año viene bueno. Lo dicen cuando el año todavía no ha dicho nada.",
                "Por las tardes refresca, y la aldea huele a tierra mojada y a humo. Es la mejor época para estar en el campo: no aprieta ni el sol ni el agua.",
            )),
        ),
        "pajaros" to unDia(
            Parrafo.MANANA to "Hoy Paser se va solo al campo, con una honda que le hizo su padre, a espantar a los pájaros del trigo. Va muy serio, como quien va a la guerra. {nombre} lo mira irse desde la puerta y no le dice que la honda casi no tira.",
            Parrafo.GENTE to "Paser se pasa el día gritándoles a los pájaros y tirando piedras que caen a dos pasos. Los pájaros lo miran con curiosidad y siguen comiendo. Al mediodía Henut le lleva pan y lo encuentra dormido a la sombra, con la honda en la mano.",
            Parrafo.ANOCHECER to "Al volver, Paser dice que ha matado tres pájaros. Nadie le pregunta dónde están. {nombre} le dice que mañana podrá volver, y el chico se duerme contento, con las manos sucias y la honda debajo de la estera.",
        ),
        "tia_habla" to unDia(
            Parrafo.MANANA to "Hoy Tia dijo el nombre de su abuela, entero y claro, señalándola con el dedo. Baket estaba moliendo y se quedó quieta, con la piedra en la mano. Luego se rio como no se reía desde hacía tiempo y se la sentó en las rodillas.",
            Parrafo.GENTE to "Desde entonces Tia no dice otra cosa: Baket, Baket, Baket, a todas horas y a todo el mundo, hasta al perro. Baket dice que la niña es muy lista. Henut dice que también sabe decir «mamá», y lo dice un poco seria.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su madre lleva años diciendo que se va a morir pronto, y que ahora ya no podrá, porque la niña no la dejará. Le gustaría que fuera así. Mira a las dos juntas, la vieja y la chica, y se le pasa el cansancio del día.",
        ),
        "techo" to unDia(
            Parrafo.MANANA to "Hoy {nombre} arregla el techo antes de que llegue el calor fuerte. Las palmas viejas están secas y rotas, y por los agujeros entra el sol como cuchillos. Sube con un haz de palmas nuevas y un cubo de barro, y Paser le pasa las cosas desde abajo.",
            Parrafo.PUEBLO to "En el techo se hace casi la mitad de la vida: se duerme cuando hace calor, se secan los dátiles, se guardan la leña y las cebollas. Un techo roto es media casa rota. Los vecinos miran desde sus techos y opinan, y {nombre} los deja opinar.",
            Parrafo.ANOCHECER to "Esta noche duermen en el techo nuevo, que huele a palma fresca y a barro. {nombre} se tumba boca arriba y mira las estrellas, satisfecho. Paser le pregunta si las estrellas también tienen techo, y él le dice que sí, el cielo, y el chico se duerme pensándolo.",
        ),
        "nebamun" to unDia(
            Parrafo.MANANA to "Hoy aparece en la obra su hermano Nebamun, que lleva agua a la aldea de los que hacen las tumbas de los reyes, en el valle de al lado. Viene con su burro y sus odres, y sin permiso de nadie. Los capataces lo dejan pasar, porque trae agua fresca.",
            Parrafo.GENTE to "Nebamun le cuenta en voz baja lo que se dice en la aldea de las tumbas: que hay pinturas que nadie verá nunca, que un obrero faltó al trabajo porque le picó un escorpión, y que el escriba lo apuntó en la lista de faltas. {nombre} lo escucha con la boca abierta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que en la aldea de las tumbas saben leer hasta algunos de los que pican piedra, y que hacen casas para la eternidad mientras él hace adobes para una rampa. Luego piensa que la rampa también hace falta, y se le pasa. Nebamun se va al atardecer, con los odres vacíos.",
        ),
        "buey" to unDia(
            Parrafo.MANANA to "Hoy le prestan un buey para arar el campo de abajo, el último por sembrar, que tardó en secarse. Lo paga con tres sacos de cebada que {nombre} todavía no tiene. Es un buey viejo, flaco y con mal carácter, pero tira. Lo lleva al campo hablándole bajito, como a un pariente enojado.",
            Parrafo.GENTE to "Paser no se separa del buey en todo el día: lo acaricia, le habla, le pone nombre. El buey lo mira con la indiferencia de los bueyes. Henut le dice que no se encariñe, que el buey no es suyo. Paser le dice que el cariño tampoco es de nadie.",
            Parrafo.PENSAMIENTO to "{nombre} hace cuentas: tres sacos de cebada son muchos días de pan. Pero con el buey ara en un día lo que a mano le llevaría diez, y el campo de abajo da bien. Las cuentas le salen, apenas, como siempre. Lo que nunca sale es que sobre.",
        ),
        "pregoneros" to unDia(
            Parrafo.MANANA to "Hoy pasó por el río una barca con banderas y un pregonero de voz enorme, que se paraba frente a cada aldea a gritar la noticia: el rey va a celebrar su jubileo, porque cumple treinta años en el trono. Toda la aldea bajó a la orilla a oírlo.",
            Parrafo.PUEBLO to "El pregonero dice que lo manda Khaemuaset, el hijo del rey, el sacerdote de Ptah en Menfis, y que habrá fiestas en todo Egipto. Los viejos dicen que nunca han visto un jubileo. Es verdad: la mayoría de los reyes no dura tanto.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que treinta años son más de los que él tiene. El rey ya era rey cuando él nació, y para él no ha habido otro. Le parece que el rey es como el río: está ahí desde siempre, y uno no se imagina el mundo sin él.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "egipto", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
