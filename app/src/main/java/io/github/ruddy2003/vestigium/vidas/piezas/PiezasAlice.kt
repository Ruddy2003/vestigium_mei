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
 * El cuento de Alice, campesina de una aldea inglesa hacia 1300. Sigue la gramática y el tono de `PiezasDemeas.kt`, que
 * el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - La aldea es del señor: la tierra, el molino, el bosque. Quien manda de verdad es el mayordomo. Se bebe cerveza a
 *   todas horas y la hacen las mujeres. Las noches largas se duermen en dos tandas, el primer sueño y el segundo.
 * - El año lo marcan la Iglesia, el campo y los días de pagar.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG1.kt`):
 * - Edith, la madre de Walter, muere en la temporada 2 (`muere_edith`).
 * - Agnes vive en casa hasta que entra a servir en la casa del señor (`agnes_sirve`, temporada 2); desde entonces viene
 *   los domingos.
 * - Joan, su hermana, vive en la aldea vecina desde que se casa (`boda_joan`, temporada 1).
 * - Matilda nace en la temporada 3 (`nace_matilda`).
 * - El trabajo del campo cambia con la estación (`meses`): siembra en primavera, escarda, heno, espiga, terrones del
 *   trigo de invierno y trilla.
 * - Las fiestas que vuelven cada año no nombran a Edith, Agnes, Joan ni Matilda.
 */
internal object PiezasAlice {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se levanta antes que el sol, cuando todavía se oye roncar a Walter, y sopla las brasas del hogar, que está en medio de la casa, en el suelo. El humo sube y busca solo el agujero del techo. Afuera canta el gallo de la viuda de al lado, que es el más madrugador de la aldea."),
        v("La primera luz entra por la puerta, porque la casa no tiene más ventana que un agujero tapado con un trapo. {nombre} se echa agua del cubo en la cara, se ata la toca y sale al patio, donde las gallinas ya la esperan como si les debiera algo."),
        v("La despierta la campana de la iglesia, que llama a la primera misa aunque casi nadie vaya. {nombre} se santigua sin abrir los ojos, se queda un momento oyendo la lluvia o el silencio, y se levanta. La paja del jergón cruje, y John protesta en sueños."),
        v("En casa se desayuna de pie: un trozo de pan oscuro y un trago de cerveza floja, que es más segura que el agua del pozo. Walter ya se ha ido a la tierra del señor, que es la que se trabaja primero. {nombre} le guarda su pan para cuando vuelva."),
        v("{nombre} sale a la calle de la aldea, que es un camino de barro entre casas de paja, y saluda a las vecinas que también salen. Todas llevan algo: un cubo, una cesta, un niño. Se dicen buenos días y el tiempo que va a hacer, que cada una tiene su opinión."),
        v("Antes de salir, {nombre} se santigua delante de la cruz de palo clavada junto a la puerta. Afuera, el cielo empieza a clarear detrás de la torre de la iglesia, que es lo único de piedra que hay en la aldea, aparte del molino y de la casa del señor."),
        v("John se despierta con ella y la sigue al patio, descalzo, con el pelo lleno de paja. Le toca dar de comer a las gallinas, y lo hace como quien da de comer a un ejército. {nombre} lo mira desde la puerta y se acuerda de que a esa edad ella hacía lo mismo."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la aldea el tiempo no es conversación: es la cosecha, y la cosecha es el año entero."),
        v("{nombre} mira el cielo como lo miran los campesinos, pensando en el trigo. {cielo} De eso depende que haya pan en invierno, y de eso depende todo lo demás."),
        v("{de_estacion} {cielo} Las viejas dicen que antes los veranos eran más largos, y lo dicen todos los veranos."),
        v("{cielo} En {estacion} los campos cambian de color, y desde el camino se ve cómo cambian. {nombre} los mira cada mañana, por costumbre, y cada mañana le parecen un poco distintos."),
        v("Lo primero que hace al salir es mirar hacia la colina del bosque del señor: si se ve, el día será claro. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El cura dice que el tiempo lo manda Dios, y los campesinos dicen que sí, y luego le echan la culpa al vecino."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un azul pálido que en Inglaterra se agradece como un regalo.",
            "Hace un día claro, y desde el camino se ven las torres de tres iglesias de otras aldeas.",
            "No hay una nube, y el sol brilla en los charcos que dejó la lluvia de ayer.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris como el agua del pozo.",
            "Hay nubes bajas que corren hacia el este y no se deciden a llover.",
            "Está nublado, y la aldea entera tiene el color de la paja vieja.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fina de las que no paran en todo el día.",
            "Cae agua desde la madrugada, y el camino de la aldea es barro hasta el tobillo.",
            "Llueve, y por el techo de paja entra una gotera nueva que nadie había visto.",
        ),
        Clima.NIEVE to listOf(
            "Ha nevado, y las casas de paja tienen un sombrero blanco.",
            "Cae una nieve fina, y en la aldea no se oye nada.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla del río, tan espesa que la iglesia desaparece a treinta pasos.",
            "La niebla tapa los campos, y las vacas mugen sin que se las vea.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del oeste, el que trae la lluvia y se mete por las rendijas.",
            "Hay viento, y los setos se doblan y silban como si alguien los soplara.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: truena sobre el bosque y las vacas se apretujan bajo los árboles.",
            "El cielo se ha puesto negro, y el primer trueno hace santiguarse a toda la aldea.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando se siembra la cebada y los corderos ya corren por el prado.",
            "En primavera los días se alargan y el hambre del invierno se acaba despacio.",
            "Es primavera, y en los setos florece el espino blanco.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, cuando se trabaja de sol a sol y el sol no tiene prisa.",
            "En verano se siega el heno y luego el trigo, y no sobra un par de brazos.",
            "Es pleno verano, y en los campos ya huele a paja.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño: se siembra el trigo de invierno y se juntan bellotas para los cerdos.",
            "En otoño se pagan las cuentas y se mata lo que no va a pasar el invierno.",
            "Es otoño, y las primeras nieblas suben del río.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando la noche es larga y se duerme en dos tandas.",
            "En invierno se trabaja dentro: se trilla, se hila, se remienda.",
            "Es invierno, y anochece cuando una apenas ha empezado la tarde.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora hila. Lleva la rueca bajo el brazo y el huso colgando, que gira mientras ella camina, habla o espera. La lana le sale de los dedos hecha hilo, sin que parezca que haga nada. Todas las mujeres de la aldea hilan así, y ninguna recuerda haber aprendido.", hacer = Hacer.TRABAJO),
        v("A esta hora trabaja en el huerto de detrás de la casa: coles, puerros, cebollas, chícharos y unas hierbas para el potaje. Arranca las malas hierbas con la mano y espanta a las gallinas con el pie. El huerto es suyo, y es lo único que no tiene que repartir con el señor.", hacer = Hacer.TRABAJO),
        v("A esta hora hace cerveza: pone la cebada en remojo, la deja brotar, la seca, la muele y la hierve con agua en la olla grande. Huele a pan caliente en toda la calle. La cerveza dura pocos días, así que hay que hacerla siempre.", hacer = Hacer.TRABAJO),
        v("A esta hora lava ropa en el arroyo con otras mujeres, golpeándola contra las piedras con una pala de madera. El agua está fría siempre, en invierno y en verano. Se habla de todo, porque el ruido de las palas tapa lo que no conviene que se oiga.", hacer = Hacer.TRABAJO),
        v("A esta hora siembra la cebada y la avena detrás de Walter, que ara con los bueyes del común. Echa la semilla a puñados, de un lado a otro, y los cuervos la siguen a cierta distancia, esperando su parte. John corre detrás de ellos gritando.", hacer = Hacer.TRABAJO, meses = setOf(3, 4)),
        v("A esta hora escarda el trigo con un palo terminado en un gancho, arrancando los cardos antes de que crezcan. Va por los surcos con otras mujeres, despacio, inclinada. Al final del día tiene las manos llenas de pinchazos, y el trigo, más limpio.", hacer = Hacer.TRABAJO, meses = setOf(5, 6)),
        v("A esta hora voltea el heno en el prado con un rastrillo de madera, para que se seque por los dos lados. Si llueve antes de recogerlo, se pudre. Por eso todas miran al cielo cada poco, y rastrillan más deprisa cuando se nubla.", hacer = Hacer.TRABAJO, meses = setOf(6, 7)),
        v("A esta hora espiga en los campos ya segados: recoge las espigas que se cayeron, una por una, y las guarda en el delantal. Es derecho de los pobres, y en la aldea casi todos lo son un poco. Al final del día tiene un puñado de trigo y la espalda rota.", hacer = Hacer.TRABAJO, meses = setOf(9)),
        v("A esta hora rompe terrones con un mazo de madera detrás del arado, en el campo del trigo de invierno. Cada terrón que queda entero es trigo que no nace. Walter ara, ella golpea, y entre los dos no dicen diez palabras en toda la mañana.", hacer = Hacer.TRABAJO, meses = setOf(10, 11)),
        v("A esta hora trilla en el granero con el mayal, dos palos unidos por una correa que se hace girar para golpear las espigas. Hay que llevar el ritmo, o el palo de arriba le da a una en la cabeza. Con el polvo de la paja estornuda toda la mañana.", hacer = Hacer.TRABAJO, meses = setOf(12, 1, 2)),

        v("A esta hora ordeña la vaca, sentada en un taburete de tres patas, con la frente apoyada en el costado del animal, que está caliente. La leche suena en el cubo como lluvia en un tejado. Lo que no se beba hoy se hará queso.", hacer = Hacer.CUIDAR),
        v("A esta hora les da de comer a las gallinas y busca los huevos donde los pusieron, que nunca es donde deberían. Una gallina se ha empeñado en poner en el techo de paja. {nombre} sube por el huevo con una escalera, y la gallina la mira con desprecio.", hacer = Hacer.CUIDAR),
        v("A esta hora aviva el fuego del hogar, en el centro de la casa, y pone a calentar el potaje de ayer, que mejora con los días. Echa un puñado de chícharos, una cebolla, un hueso. La olla no se vacía nunca del todo: se le va echando.", hacer = Hacer.CUIDAR),

        v("A esta hora come en el campo, sentada en el borde del surco: pan oscuro, un trozo de queso y una cebolla, y un trago de cerveza del jarro que trajo John. Se come deprisa, porque el mayordomo pasa a caballo y mira quién se sienta.", hacer = Hacer.COMER),
        v("A esta hora come potaje de chícharos con un poco de tocino, en casa, con toda la familia alrededor de la olla. Cada uno tiene su cuchara de madera y la limpia con la lengua. El pan se moja hasta que la olla queda limpia.", hacer = Hacer.COMER),
        v("A esta hora come lo que hay, que hoy es pan con manteca y una manzana arrugada del año pasado. Bebe cerveza floja, como todos, a todas horas: el agua es para los animales y para los que quieren enfermar.", hacer = Hacer.COMER),

        v("A esta hora está sentada a la puerta con las vecinas, mientras queda luz, hilando y hablando. Se habla de quién se casa, de quién debe y de lo que dijo el cura el domingo. Los niños juegan en el barro, y nadie los riñe hasta que se caen al pozo, o casi.", hacer = Hacer.GENTE),
        v("A esta hora está en la casa de la viuda que vende cerveza, la que cuelga una rama en la puerta cuando tiene. Se sienta en un banco con otras mujeres y con los maridos de las otras mujeres, y todos beben lo mismo. Se canta poco y se discute mucho.", hacer = Hacer.GENTE),
        v("A esta hora está junto al fuego con Walter y los niños, remendando una camisa mientras él arregla el mango de una azada. No hablan mucho. Se oye el fuego, se oye la lluvia o no se oye nada, y con eso basta.", hacer = Hacer.GENTE),

        v("A esta hora duerme su primer sueño, en el jergón de paja, con la familia apretada alrededor y una manta de lana encima. La casa huele a humo y a animal. Lo último que se oye es el fuego, que se va apagando bajo la ceniza.", hacer = Hacer.DORMIR),
        v("A esta hora está despierta entre el primer sueño y el segundo, como todo el mundo. Se levanta, echa una rama al fuego, reza un poco y habla bajo con Walter de las cosas que de día no se dicen. Luego vuelve a dormirse sin darse cuenta.", hacer = Hacer.DORMIR),
        v("A esta hora duerme su segundo sueño, el de antes del alba, que es el más hondo. Ni los perros de la aldea la despiertan, y eso que los perros de la aldea le ladran a cualquier cosa, y sobre todo a nada.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La aldea son unas cuantas casas de madera, barro y paja a lo largo de un camino, con la iglesia de piedra en un extremo y el molino del señor en el otro. Detrás de cada casa hay un huerto, y detrás de los huertos, los campos. {entorno}"),
        v("{entorno} Los campos de la aldea son tres, y cada año uno descansa: en uno va el trigo, en otro la cebada, y en el tercero pastan los animales. Cada familia tiene tiras de tierra en los tres, mezcladas con las de los vecinos, para que la suerte se reparta."),
        v("El señor vive en la casa grande, la de piedra, y casi nunca está: tiene otras casas y otras aldeas. Quien manda de verdad es el mayordomo, Hugh, que sabe cuánto debe cada uno y no lo olvida. {entorno}"),
        v("En el molino del señor hay que moler el trigo de todos y pagarle al molinero una parte, que él calcula a su manera. Moler en casa con piedra propia está prohibido, y quien lo hace lo hace a escondidas. {entorno}"),
        v("{entorno} La iglesia es el único sitio de la aldea donde caben todos, y por eso allí se reza, se hacen los tratos y se dan las noticias. En el cementerio de al lado están los abuelos de todos, y los niños juegan entre ellos los domingos."),
        v("El bosque del señor empieza donde acaban los campos, y allí no se puede cazar ni cortar leña sin permiso. Se puede, en cambio, juntar ramas caídas y llevar los cerdos a comer bellotas, pagando. Todo lo que se hace en la aldea se paga de alguna manera. {entorno}"),
    )

    /** Lo que se ve alrededor según cómo va el trigo. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("campo", 0.7, frases = listOf(
            "En los campos el trigo está alto y dorado, y el viento lo peina hacia el mismo lado.",
            "Los campos de trigo ondulan como agua, y entre las espigas asoman las amapolas.",
        )),
        Entorno("campo", 0.0, 0.15, frases = listOf(
            "Los campos están pelados, con el rastrojo o la tierra recién arada.",
            "En los campos no hay más que barro, cuervos y algún espantapájaros.",
        )),
        Entorno("campo", 0.15, 0.7, frases = listOf(
            "En los campos el trigo crece verde, todavía bajo.",
            "El trigo de invierno asoma en hileras, verde contra la tierra oscura.",
        )),
    )

    private val GENTE = listOf(
        v("Walter, su marido, es un hombre callado que habla con los bueyes más que con la gente. Le debe al señor tres días de trabajo cada semana, y los hace sin quejarse en voz alta. En voz baja, a {nombre}, se queja de todo."),
        v("John tiene la edad de querer ser mayor y no poder. Lleva el agua, cuida las ocas y espanta a los cuervos, y cada cosa la hace como si fuera la más importante del mundo. {nombre} le dice que sí lo es, y casi se lo cree ella también."),
        v("Hugh, el mayordomo del señor, sabe cuántas gallinas tiene cada casa, cuántos huevos ponen y cuántos le debe cada una al señor por Pascua. Pasa a caballo por la aldea mirándolo todo, y la gente lo saluda con una sonrisa que se le borra en cuanto pasa."),
        v("Don Robert, el cura, es hijo de un campesino de la aldea vecina, y se le nota en las manos. Sabe latín, o eso dice, y bautiza, casa y entierra a todos. Le gusta la cerveza de {nombre}, y lo dice desde el púlpito, que es como mejor se vende."),
        v("La viuda de al lado tiene una vaca, un gallo y una lengua que no descansa nunca. {nombre} le presta fuego, sal y paciencia, y ella le presta noticias de toda la comarca. Es un trato justo, aunque las noticias no siempre sean verdad."),
        v("Edith, la madre de Walter, vive con ellos y duerme en el rincón más caliente, junto al fuego. Ya no va al campo, pero hila, cuida a los niños y sabe remedios para todo. Para el dolor de muelas, dice, nada como no tener muelas, y ella ya casi no tiene.", hasta = "muere_edith"),
        v("Agnes, la mayor, ya hila casi tan bien como su madre y cuida a su hermano mejor que nadie. Las vecinas empiezan a mirarla de otra manera, y {nombre} también. Todavía es una niña, se dice, y se lo dice todas las mañanas.", hasta = "agnes_sirve"),
        v("Agnes sirve ahora en la casa del señor, y viene los domingos con historias de la cocina grande: carne todos los días, pan blanco, un cocinero que grita en francés. {nombre} la escucha y le busca en la cara si come bien. Come bien.", desde = "agnes_sirve"),
        v("Su hermana Joan vive en la aldea vecina, casada con el herrero, y se ven en el mercado y en las fiestas. Joan habla rápido y {nombre} despacio, y entre las dos se cuentan un mes entero en lo que dura una misa.", desde = "boda_joan"),
        v("Matilda, la pequeña, duerme en una cuna de mimbre junto al fuego, y la casa entera gira alrededor de ella. Cuando llora, alguien la mece con el pie sin dejar lo que está haciendo. {nombre} la mira y piensa que cada hijo ha sido el primero.", desde = "nace_matilda"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de la casa: sin haberlas aprendido nunca."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En la aldea se dice que una casa vale lo que su olla; lo dicen las que tienen la olla vacía."),
        v("{dato_objeto} Son cosas que se aprenden mirando a las mayores, como casi todo en la aldea."),
        v("Una campesina tiene pocas cosas, y cada una tiene su historia y su remiendo. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("huso", "el huso", "un huso", "El huso",
            "El huso es un palito con un disco de piedra en la punta, que gira colgado y retuerce la lana hasta hacerla hilo; con él se hila andando, hablando y rezando, y en invierno hasta dormida, dice Walter."),
        ObjetoCuento("rueca", "la rueca", "una rueca", "La rueca",
            "La rueca es una vara donde se ata la lana sin hilar, y se lleva bajo el brazo; es tan de las mujeres que el cura dice que en el cielo las santas también hilan, y nadie lo contradice."),
        ObjetoCuento("cubo", "el cubo", "un cubo de madera", "El cubo",
            "El cubo es de duelas de madera atadas con aros de mimbre; con él se saca el agua del pozo, se ordeña la vaca y se lava la ropa, y si se raja, se le echa barro y aguanta otro año."),
        ObjetoCuento("jarra_cerveza", "la jarra de cerveza", "una jarra de cerveza", "La cerveza",
            "La cerveza de la casa se hace con cebada germinada, sin lúpulo, y dura pocos días antes de agriarse; en la aldea la beben todos, a todas horas, y la que hace buena cerveza tiene amigos."),
        ObjetoCuento("pan", "el pan", "un pan", "El pan",
            "El pan de todos los días es oscuro, de centeno y cebada, pesado como una piedra; el blanco, de trigo, es para el señor y para las fiestas, y el trigo de la familia se va casi entero en pagar."),
        ObjetoCuento("hoz", "la hoz", "una hoz", "La hoz",
            "La hoz es de hierro, curva, con mango de madera; con ella siegan las mujeres y los hombres por igual, se afila con una piedra que se escupe, y el que la deja mellada la paga."),
        ObjetoCuento("azada", "la azada", "una azada", "La azada",
            "La azada es una hoja de hierro en un palo de fresno; la hizo el herrero de la aldea vecina, y cuesta tanto que se hereda de madre a hija."),
        ObjetoCuento("cesta", "la cesta", "una cesta", "La cesta",
            "La cesta es de mimbre del arroyo, trenzada por Walter en las noches de invierno; sirve para los huevos, para las coles, para la lana y, en la siega, para la comida de todos."),
        ObjetoCuento("vela_sebo", "la vela de sebo", "una vela de sebo", "La vela",
            "La vela de sebo humea, gotea y huele a oveja; se enciende poco, porque cuesta, y casi siempre la única luz de la casa es la del fuego, que además calienta."),
        ObjetoCuento("cuentas", "las cuentas de rezar", "unas cuentas de rezar", "Las cuentas",
            "Las cuentas de rezar son unas bolitas de madera ensartadas en un cordel; con ellas se cuentan los padrenuestros, y se pasan con los dedos en la iglesia, en el campo y cuando hay tormenta."),
        ObjetoCuento("cuchara", "la cuchara de madera", "una cuchara de madera", "La cuchara",
            "Cada uno tiene su cuchara de madera, tallada por Walter con una marca distinta; se lleva colgada del cinturón, se limpia con la lengua y no se presta, como no se presta la boca."),
        ObjetoCuento("capucha", "la capucha", "una capucha", "La capucha",
            "La capucha es de lana basta, sin teñir, y tapa la cabeza y los hombros; con la lluvia pesa el doble y huele a oveja mojada, y aun así nadie sale sin ella de octubre a mayo."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el trigo como el cura piensa en el cielo: con esperanza y con miedo. Si el año es malo, el invierno se lleva a los viejos y a los niños. A sus {edad} años ha visto dos inviernos así, y los recuerda por los nombres de los que se llevaron."),
        v("Piensa a veces en Londres, que nadie de la aldea ha visto y todo el mundo se imagina: una ciudad donde cabrían cien aldeas, con un río lleno de barcos y un rey. Le gustaría verla una vez, de lejos, y volver a casa a contarlo."),
        v("{mundo} Ella piensa en cosas más cercanas: en la gotera, en la vaca, que está flaca, en si alcanzará la cebada para la cerveza. Las cosas grandes, dicen las viejas, son para los hombres, que luego no saben arreglarlas."),
        v("{mundo} A sus {edad} años, {nombre} ha enterrado a dos hijos que no llegaron al año. Están en el cementerio de la iglesia, junto al muro, sin piedra. Cuando va a misa pasa por ahí y les dice algo sin mover los labios."),
        v("No tiene nada suyo: la tierra es del señor, la casa es del señor y hasta ella, dice el mayordomo, es un poco del señor. Tiene a los suyos, sus manos y su cerveza, y hay días en que eso le parece mucho. Su madre tuvo lo mismo, y nunca se quejó en voz alta."),
        v("{mundo} Por las noches, entre el primer sueño y el segundo, se queda mirando el fuego y piensa en cosas que de día no tiene tiempo de pensar. Por la mañana no se acuerda de qué eran. Le queda solo la sensación de haber pensado algo importante."),
    )

    private val MUNDOS = mapOf(
        "paz" to listOf(
            "En la aldea se dice que el rey es fuerte, y que por eso los campos están tranquilos.",
            "Este año no se habla de guerras, sino de cosechas y de rentas.",
        ),
        "guerra" to listOf(
            "Se habla de la guerra del rey contra los escoceses, que nadie sabe dónde viven exactamente.",
            "Dicen que el rey pide más dinero para la guerra, y que la guerra no se acaba nunca.",
        ),
        "tregua" to listOf(
            "Se dice que hay tregua con los escoceses, y nadie sabe cuánto durará.",
            "Este año se habla menos de guerras y más de bodas.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando oscurece, la familia se junta alrededor del fuego del centro de la casa y se cena potaje con pan. No se enciende la vela, porque cuesta. Se reza un padrenuestro, se cubre el fuego con ceniza para que amanezca vivo, y a dormir, que mañana se madruga."),
        v("Al caer la tarde vuelven los hombres del campo con las herramientas al hombro, y las vacas del común solas por el camino, cada una a su casa sin que nadie la guíe. {nombre} las ve pasar desde la puerta. Siempre llegan antes que Walter."),
        v("Anochece temprano sobre la aldea, y la campana de la iglesia toca para que se cubran los fuegos. {nombre} se sienta un momento en el umbral, mirando cómo se apagan los resplandores de las otras casas. Pasa un perro, se sienta a su lado, y entre los dos no dicen nada."),
        v("De noche la aldea se queda a oscuras del todo: no hay más luz que la de las estrellas, si las hay, y la de algún fuego mal tapado. {nombre} se acuesta con el cuerpo molido. Lo último que oye es la lluvia en la paja, o el silencio, que en el campo también se oye."),
        v("Se cena en silencio, porque la boca está ocupada. Después {nombre} cuenta lo que se dice en el pozo y Walter lo que se dice en el campo, y casi siempre es lo mismo. Luego se reza, se tapa el fuego, y la aldea entera se apaga, casa por casa, como una vela grande."),
        v("La última luz se queda un rato en la torre de la iglesia, cuando la calle ya está a oscuras. {nombre} la mira desde la puerta. Esa torre la levantaron los abuelos de los abuelos, y a esa hora le parece que todavía están ahí arriba, mirando."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "arado" to unDia(
            Parrafo.MANANA to "Hoy se acaban las fiestas y vuelve el arado. En el campo grande hay carrera de arados: los mozos de cada punta de la aldea compiten a ver quién abre antes un surco derecho, y la aldea entera va a gritarles. El surco nunca sale derecho, y el que gana lo dice igual.",
            Parrafo.PUEBLO to "Después de la carrera se bendicen los arados delante de la iglesia, con agua y con un latín que nadie entiende, y se reza para que el año sea bueno. Los bueyes miran con la paciencia de siempre. Mañana empieza el trabajo de verdad, y lo saben hasta los bueyes.",
            Parrafo.ANOCHECER to "Esta noche se acaban las sobras de Navidad: un trozo de tocino, el último pastel, la cerveza fuerte que quedaba. {nombre} lo reparte entre todos a partes iguales, y se queda con la parte más pequeña, como siempre. Fuera hiela, y las estrellas parecen más cerca.",
        ),
        "candelaria" to unDia(
            Parrafo.MANANA to "Hoy es la Candelaria, cuarenta días después de Navidad. Cada familia lleva una vela a la iglesia para que la bendigan, y vuelve con ella encendida, tapándola con la mano para que el viento no la apague. Si llega encendida a casa, dicen, el año será bueno.",
            Parrafo.GENTE to "La vela bendita se guarda en el arcón, envuelta en un trapo, para encenderla cuando haya tormenta o cuando alguien se esté muriendo. {nombre} la pone en su sitio con cuidado, junto a la del año pasado, que no se usó, gracias a Dios.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que desde hoy los días se alargan, aunque todavía no se note. Las viejas dicen que si hace sol en la Candelaria el invierno durará más, y si llueve, se acabará pronto. Todas miran el cielo, y cada una ve lo que quiere.",
        ),
        "carnaval" to unDia(
            Parrafo.MANANA to "Hoy es martes de carnaval, el último día antes de la cuaresma. Hay que acabar con la manteca, los huevos y el tocino que quedan, porque mañana empiezan cuarenta días sin ellos. En toda la aldea huele a tortas fritas.",
            Parrafo.GENTE to "Por la tarde los hombres juegan a la pelota en el campo, una mitad de la aldea contra la otra, sin más regla que llevar la pelota hasta la puerta de la iglesia. Walter vuelve con un ojo morado, contento. {nombre} le pone barro frío en el ojo y no le pregunta quién ganó.",
            Parrafo.ANOCHECER to "Esta noche se come hasta que no cabe nada más, y {nombre} se acuesta con la barriga llena y la conciencia regular. Mañana es miércoles de ceniza, y don Robert les pondrá ceniza en la frente y les recordará que son polvo, por si se les había olvidado.",
        ),
        "anunciacion" to unDia(
            Parrafo.MANANA to "Hoy es la Anunciación, el día en que el ángel visitó a la Virgen, y uno de los cuatro días del año en que hay que pagarle la renta al señor. Walter se pone la camisa buena y va a la casa grande con las monedas contadas tres veces y un pollo debajo del brazo.",
            Parrafo.PUEBLO to "En la casa del señor el mayordomo cobra sentado detrás de una mesa, con un escribano al lado que lo apunta todo en un pergamino. Los campesinos hacen cola en el patio, con la gorra en la mano y la cara de quien paga. Nadie discute: discutir también se paga.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que a la Virgen la visitó un ángel y a ellos los visita el mayordomo, y que no es lo mismo. Se lo dice a la viuda de al lado, que se ríe tanto que se atraganta. Luego se santiguan las dos, por si acaso, y se vuelven a reír.",
        ),
        "pascua" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es Pascua. Las campanas, que llevaban calladas desde el jueves santo, vuelven a sonar, y en la aldea no queda nadie dormido. {nombre} se pone la toca blanca, la que solo sale tres veces al año, y va a misa con toda la familia.",
                Parrafo.PUEBLO to "Después de cuarenta días sin carne ni huevos, hoy se come de todo. Hay que llevarle huevos al señor, que es costumbre, y al cura, que también. Lo que queda es para la casa, y en la de {nombre} queda lo justo para que cada uno tenga uno.",
                Parrafo.ANOCHECER to "Esta noche se come cordero, quien tiene, y pan del bueno. Los niños se pelean por los huevos teñidos con piel de cebolla. {nombre} se queda dormida sentada junto al fuego, con la cuchara en la mano, y nadie la despierta.",
            ),
            dia(
                Parrafo.GENTE to "Segundo día de Pascua, y tampoco se trabaja. Se va de visita, y en cada casa hay que comer algo para no ofender. {nombre} vuelve más llena que ayer, jurando que no prueba otro huevo hasta el año que viene.",
                Parrafo.PUEBLO to "En el prado de la iglesia los jóvenes corren, luchan y se tiran al suelo, y los viejos los miran sentados en el muro del cementerio. Hay quien baila al son de una gaita, y quien canta peor que la gaita. Es un día bueno, y todos lo saben.",
            ),
        )),
        "hock" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es el lunes de Hocktide, el día en que mandan las mujeres. Salen en grupo por la aldea con cuerdas, atrapan a los hombres que pueden y no los sueltan hasta que pagan un rescate. Lo que se junta es para la iglesia, así que nadie se queja demasiado.",
                Parrafo.GENTE to "{nombre} y la viuda de al lado atrapan a Hugh, el mayordomo, que no corre lo bastante, y lo atan a un árbol entre las risas de toda la aldea. Hugh paga sin discutir, que es la única vez en el año que se le ve pagar algo. Hasta él se ríe, un poco.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que un día al año está bien, y que más no lo aguantarían los hombres. Luego piensa que tampoco lo aguantarían las mujeres, que además tienen que hacer la comida. Por eso, se dice, es un día solo.",
            ),
            dia(
                Parrafo.GENTE to "Hoy, martes de Hocktide, les toca a los hombres atrapar a las mujeres, y lo hacen peor, porque las mujeres ya están avisadas. Walter intenta atar a {nombre}, y ella le dice que primero termine de arreglar la cerca. Walter se va a arreglar la cerca.",
                Parrafo.ANOCHECER to "Esta noche don Robert cuenta lo que se juntó con los rescates, y es más que el año pasado. Lo anunciará el domingo desde el púlpito, y las mujeres sonreirán. Los hombres también, por no ser menos.",
            ),
        )),
        "mayo" to unDia(
            Parrafo.MANANA to "Hoy es primero de mayo. Antes del amanecer los jóvenes salen al bosque a traer ramas verdes y flores, y vuelven cantando a colgarlas en las puertas de las casas. {nombre} se despierta con el olor del espino blanco, que alguien ha puesto en la suya.",
            Parrafo.PUEBLO to "En el prado se baila todo el día y se elige a una reina de mayo, que es la moza más guapa de la aldea, o la que tiene más primos. Le ponen una corona de flores y la pasean. Los viejos dicen que antes las reinas eran más guapas, y las viejas les dan con el codo.",
            Parrafo.GENTE to "{nombre} baila un rato con Walter, que baila como ara: derecho y sin mirar atrás. Los niños corren entre los que bailan y se llevan más empujones que nadie. Al final del día todos tienen flores en el pelo y barro en los pies.",
        ),
        "rogativas" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empiezan las rogativas, tres días antes de la Ascensión. Detrás de la cruz y del cura, la aldea entera recorre los linderos de la parroquia, de mojón en mojón, para bendecir los campos y para que nadie se olvide de dónde acaban.",
                Parrafo.PUEBLO to "En cada mojón se para la procesión, el cura reza, y a algún niño se le da un golpe con una vara, o se le pone cabeza abajo contra el mojón, para que se acuerde del sitio toda la vida. Los niños lloran, los mayores se ríen, y el lindero queda bien marcado.",
                Parrafo.PENSAMIENTO to "{nombre} camina detrás de la cruz pensando en el trigo, que ya está verde. Reza por él como la que más, y de paso reza por la vaca, por la gotera y por que Walter no vuelva a pelearse con el molinero. Dios sabrá separar lo importante.",
            ),
            dia(
                Parrafo.GENTE to "Segundo día de rogativas. Hoy la procesión va hacia el río y el molino, y el molinero sale a la puerta con la cara de santo que pone una vez al año. John lleva una cruz pequeña de madera que le hizo su padre, y la lleva tan alta que choca con todo.",
                Parrafo.ANOCHECER to "Por la tarde se come en el prado lo que cada uno trae, y se comparte: pan, queso, cebollas y cerveza, que no falta nunca. Los niños corren entre los mayores como si fuera fiesta, que en el fondo lo es.",
            ),
            dia(
                Parrafo.PUEBLO to "Último día de rogativas. La procesión da la vuelta por el bosque del señor, por los linderos más lejanos, adonde casi nadie va el resto del año. En una cruz de piedra vieja, cubierta de musgo, el cura bendice hacia los cuatro lados.",
                Parrafo.ANOCHECER to "Al volver, a {nombre} le duelen los pies más que después de un día de siega. Los mete en un cubo de agua fría junto a la puerta, y la viuda de al lado se sienta a su lado y hace lo mismo. Mañana es la Ascensión, y los campos ya están bendecidos.",
            ),
        )),
        "cerveza_pentecostes" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es Pentecostés, y empieza la cerveza de la parroquia. Durante dos días se vende en el patio de la iglesia cerveza que hicieron las mujeres con cebada que dio cada casa, y lo que se gana es para arreglar el tejado de la iglesia, que tiene más agujeros que el de cualquiera.",
                Parrafo.PUEBLO to "En el patio de la iglesia hay mesas, bancos, un gaitero y un oso, que trajo un hombre de fuera y que baila peor de lo que toca el gaitero. Viene gente de las aldeas vecinas, porque se dice que la cerveza de aquí es buena, y es verdad. Se bebe por la iglesia, y se bebe mucho.",
                Parrafo.GENTE to "{nombre} hizo parte de la cerveza y está detrás de la mesa sirviéndola con las otras mujeres. Don Robert prueba la de cada una y dice que todas son la mejor, que es lo que tiene que decir un cura. La de {nombre} se acaba primero, y ella no dice nada, pero lo sabe.",
            ),
            dia(
                Parrafo.GENTE to "Segundo día de la cerveza de Pentecostés. Hoy hay juegos en el prado: lucha, carreras, tirar al palo. Walter gana una carrera de sacos y le dan un pastel, que comparte con los niños. Lo cuenta como si hubiera ganado una guerra.",
                Parrafo.ANOCHECER to "Al anochecer se cuenta lo que se ganó, y alcanza para la mitad del tejado. La otra mitad, dice don Robert, será el año que viene, si Dios quiere y la cerveza sale igual. {nombre} vuelve a casa cansada, con olor a cebada en el pelo.",
            ),
        )),
        "san_juan" to unDia(
            Parrafo.MANANA to "Hoy es San Juan, el día más largo del año. Desde temprano los chicos juntan leña, huesos y todo lo que arda para la hoguera de la noche, y lo amontonan en el cruce de caminos. El montón es más alto cada hora, y los mayores hacen como que no ven de dónde sale.",
            Parrafo.PUEBLO to "En las puertas de las casas se cuelgan ramas de hierba de San Juan, que espanta a los malos espíritus. En la iglesia se dice misa por el santo, y don Robert explica que la hoguera es por él. Los viejos asienten, y saben que la hoguera es más vieja que el santo.",
            Parrafo.ANOCHECER to "Al caer la noche, que hoy cae tardísimo, se enciende la hoguera y la aldea entera baila alrededor. Los jóvenes saltan por encima del fuego para tener suerte, y alguno se chamusca. {nombre} se lleva a casa una brasa en una teja, contra los rayos y contra el mal de ojo.",
        ),
        "heno" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siega del heno en el prado del señor. Los hombres van delante con las guadañas, en fila, cortando la hierba alta de un tajo largo, y las mujeres detrás, con rastrillos, extendiéndola para que se seque. El prado huele a verde como no huele nada más.",
                Parrafo.PUEBLO to "El heno del señor se siega primero, y luego el de cada uno, si queda tiempo y no llueve. Hugh pasa a caballo contando las hileras. El que trabaja bien en el prado del señor recibe al final un poco de cerveza y una gavilla, que es lo que allí se llama ser generoso.",
                Parrafo.GENTE to "John lleva la cerveza a los segadores en un jarro más grande que él, y la reparte con mucha seriedad. Los hombres le dicen que es el mejor aguador de la comarca. {nombre} lo ve desde su hilera, con el rastrillo en la mano, y por un momento se le olvida el sol.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días el prado está lleno de montones de heno que parecen casas pequeñas, y los niños juegan a esconderse entre ellos hasta que alguien los saca de una oreja.",
                "Si amenaza lluvia, toda la aldea corre a amontonar el heno y a cubrirlo. Hoy el cielo aguanta, y se trabaja más tranquilo, aunque mirando para arriba cada poco.",
                "Los carros del heno van y vienen al granero del señor, tan cargados que el carretero va sentado encima, a la altura de los tejados. Por la calle dejan un rastro verde que se comen las ocas.",
            )),
        ),
        "lammas" to unDia(
            Parrafo.MANANA to "Hoy es Lammas, la misa del pan. Cada casa lleva a la iglesia un pan hecho con el primer trigo del año, que todavía no está del todo seco, y don Robert lo bendice. Es la manera de darle gracias a Dios por la cosecha antes de que empiece.",
            Parrafo.PUEBLO to "Desde hoy se abren los prados que estaban cerrados para el heno, y los animales de toda la aldea entran a comer lo que rebrotó. Las vacas corren como terneras, de contentas. La aldea entera sale a verlas, porque es raro ver correr a una vaca.",
            Parrafo.PENSAMIENTO to "{nombre} mira su pan sobre el altar, junto a los demás, y le parece el más bonito, aunque esté un poco quemado de un lado. Piensa que dentro de unos días empezará la siega y que habrá que trabajar de sol a sol. Hoy, por lo menos, se reza.",
        ),
        "siega" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siega del trigo. Antes del alba la aldea entera sale al campo con las hoces, hombres y mujeres, y en las casas solo quedan los viejos y los niños de pecho. El señor da de comer a los que siegan su trigo, y eso, en la aldea, se celebra.",
                Parrafo.PUEBLO to "En los campos los segadores avanzan en fila, cortando el trigo a media altura; detrás van las mujeres atando gavillas con un puñado de paja, y los niños recogiendo lo que cae. El mayordomo cuenta las gavillas del señor, y el cura las del diezmo, una de cada diez.",
                Parrafo.ANOCHECER to "Al anochecer los segadores vuelven con las manos cortadas y la espalda doblada. {nombre} cena de pie, porque si se sienta no se levanta. Se acuesta sin quitarse la ropa, con olor a paja en el pelo, y se duerme antes de terminar el padrenuestro.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días la comida de la siega la pone el señor: pan, queso, potaje y cerveza, mucha cerveza, que en el campo se suda todo. Los segadores comen sentados en las gavillas, deprisa, y vuelven al surco.",
                "En los campos segados las gavillas se juntan en montones de diez, para contarlas mejor. El cura pasa marcando con una rama las que son de la Iglesia, y los campesinos lo siguen con los ojos, contando también.",
                "Estos días hasta el herrero y el molinero siegan, porque en la siega no hay oficio que valga. Los niños llevan agua y cerveza de un lado a otro, y vuelven sudados como hombres.",
                "Los carros entran al granero con el trigo del señor, y solo después, si queda tiempo, entra el de cada uno. Todos miran al cielo, porque una semana de lluvia ahora es un invierno de hambre.",
            )),
        ),
        "san_miguel" to unDia(
            Parrafo.MANANA to "Hoy es San Miguel, el día de cerrar las cuentas. El mayordomo de este año le entrega al señor lo que juntó, y en la casa grande se cuenta todo: el grano, los animales, las rentas, las multas. Hoy se paga, se cobra y se discute en voz baja.",
            Parrafo.PUEBLO to "Hoy también se elige al mayordomo del año que viene entre los campesinos, y nadie quiere serlo, porque el que lo es responde con lo suyo de lo que falte. Al final eligen, como siempre, a Hugh, que es el único que sabe contar y el único que quiere.",
            Parrafo.GENTE to "En casa se come ganso, que es costumbre de San Miguel, y el que no tiene ganso come el del vecino, si el vecino invita. La viuda de al lado invita, porque tiene ganso y no tiene con quién comerlo. {nombre} lleva pan y cerveza, y la viuda habla por los dos.",
        ),
        "san_martin" to unDia(
            Parrafo.MANANA to "Hoy es San Martín, el santo que partió su capa con un pobre, y el día de matar el cerdo. Desde temprano se oyen los chillidos por toda la aldea, uno detrás de otro, y luego el silencio. Lo que no se coma hoy se sala y se cuelga en el humo para el invierno.",
            Parrafo.PUEBLO to "Hoy en cada casa se hace morcilla con la sangre, salchichas con las tripas y tocino con lo demás. Del cerdo no se tira nada, dice todo el mundo, y es verdad: hasta la vejiga se infla y se les da a los niños para jugar a la pelota.",
            Parrafo.ANOCHECER to "Esta noche se come carne fresca hasta reventar, porque mañana ya estará salada. Se abre la cerveza fuerte y se cantan canciones de San Martín, que son más de bebida que de santo. {nombre} se acuesta con las manos oliendo a sal y a cerdo, y no le importa.",
        ),
        "navidad" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es Navidad. Se va a la misa de medianoche con antorchas, por un camino helado, y la iglesia está tan llena que la gente se calienta con el aliento de los demás. Al salir, ya de madrugada, los vecinos se desean buenas fiestas echando vaho por la boca.",
                Parrafo.GENTE to "Hoy los siervos comen en la casa del señor, que es costumbre: cada uno lleva su leña y su plato, y el señor pone la comida, el pan blanco y la cerveza. Se come más en un día que en un mes. Walter vuelve con un trozo de carne envuelto en un trapo, para los niños.",
                Parrafo.ANOCHECER to "Empiezan los doce días de Navidad, en que no se trabaja más que lo justo: dar de comer a los animales y mantener el fuego. Esta noche {nombre} se queda dormida junto al hogar, con los pies calientes y la barriga llena, como pocas noches del año.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días en la aldea se va de casa en casa cantando, a cambio de un trago de cerveza caliente con manzana. Los que cantan peor son los que más beben, porque nadie quiere oírlos dos veces.",
                "Hay escarcha en los techos de paja y hielo en el pozo, y los niños se deslizan por los charcos helados y se caen de culo. El cura los mira desde la puerta de la iglesia y no sabe si reñirlos o tirarse él también.",
                "Estos días se cuentan historias junto al fuego: de santos, de aparecidos, de lo que le pasó al abuelo de alguien en el bosque. Cada noche dan más miedo, y cada noche los niños piden otra.",
                "Para la noche de Reyes se guarda un pastel con un haba dentro: el que la encuentre será rey por una noche. Los niños ya discuten quién la encontrará, y alguno ha prometido no comer en tres días para que le quepa más.",
            )),
        ),
        "boda_joan" to unDia(
            Parrafo.MANANA to "Hoy se casa su hermana Joan con el herrero de la aldea vecina. La boda es en la puerta de la iglesia, como debe ser, delante de todo el mundo, y luego entran a oír misa. Joan lleva un vestido azul prestado y la cara de no haber dormido.",
            Parrafo.PUEBLO to "Después de la misa hay comida en casa del novio, en la otra aldea, y medio pueblo va caminando por el camino helado. El herrero ha matado un cordero, y su madre ha hecho tanta cerveza que se podría bañar en ella a un buey.",
            Parrafo.GENTE to "{nombre} piensa en su madre, que no ha llegado a ver esta boda, y desde el banco le cuenta en voz baja cómo va todo. Luego baila con Walter y con el herrero, que tiene las manos duras como su yunque. Joan llora y se ríe a la vez, que es lo que se hace en las bodas.",
        ),
        "ternero" to unDia(
            Parrafo.MANANA to "Hoy, antes del amanecer, parió la vaca. {nombre} la oyó quejarse y se levantó con la vela, y Walter detrás, y entre los dos tiraron del ternero cuando la vaca ya no podía más. Es rojo, con una mancha blanca en la frente, y se puso de pie enseguida.",
            Parrafo.GENTE to "John no se separa del ternero en todo el día. Le pone nombre, le habla y le enseña la aldea desde la puerta. Edith le dice que no se encariñe, que el ternero es para vender. John dice que entonces que lo vendan a él.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un ternero es leche dentro de dos años, o dinero dentro de uno, o carne para San Martín. Mientras tanto es un animal flaco que mira todo con los ojos muy abiertos, como si fuera la primera vez. Lo es, piensa.",
        ),
        "jubileo" to unDia(
            Parrafo.MANANA to "Hoy don Robert leyó en misa una carta que le mandó el obispo: el papa ha declarado un año santo, y quien vaya a Roma a rezar en las iglesias de los apóstoles tendrá el perdón de todos sus pecados. La iglesia se quedó en silencio, haciendo cuentas.",
            Parrafo.PUEBLO to "A la salida de misa no se habla de otra cosa. Roma está lejísimos, al otro lado del mar y de muchas montañas, y el camino cuesta más de lo que vale la casa de cualquiera. Aun así, el hijo del molinero dice que va a ir. Su padre dice que no.",
            Parrafo.PENSAMIENTO to "{nombre} hace la cuenta de sus pecados y le salen pocos y pequeños: alguna mentira, algún enojo, algo de cerveza que vendió aguada. No le parecen motivo para ir a Roma. Se queda con la idea, de todos modos, y la guarda, como se guarda un pan para el camino.",
        ),
        "caerlaverock" to unDia(
            Parrafo.MANANA to "Hoy pasó por la aldea un mensajero del rey y lo contó en la puerta de la iglesia: el rey ha ido al norte con sus barones, contra los escoceses, otra vez. Ha llamado a los caballeros, a los arqueros y al dinero, y del dinero se encargará el mayordomo.",
            Parrafo.PUEBLO to "En la aldea nadie ha visto nunca a un escocés, pero todos tienen una opinión sobre ellos. El herrero dice que son altos, el molinero que son bajos, y la viuda de al lado que comen niños. Los niños, por si acaso, no se alejan mucho de casa.",
            Parrafo.GENTE to "Walter dice que, gracias a Dios, esta vez no se llevan a nadie de la aldea, que el rey quiere arqueros de otras partes. {nombre} se lo cree a medias. Esa noche mira a John dormido y piensa que dentro de unos años tendrá edad de tirar con arco.",
        ),
        "catador" to unDia(
            Parrafo.MANANA to "Hoy {nombre} hizo cerveza para vender, y, como cada vez que una mujer de la aldea vende, vino el catador del señor a probarla. Es un hombre gordo, con una vara, que prueba la cerveza de todas y se pasa el día un poco contento. Tiene un oficio que muchos envidian.",
            Parrafo.TRABAJO to "A esta hora el catador prueba la cerveza de {nombre}, despacio, con los ojos cerrados, delante de las vecinas. Dice que es buena y que la medida es justa, y le pone el precio que manda la ley. {nombre} cuelga una rama en la puerta, que quiere decir que hay cerveza.",
            Parrafo.GENTE to "En pocas horas se le acaba la cerveza: la viuda de al lado se lleva un jarro, el molinero dos, y don Robert manda a un niño por otro. Con lo que gana compra sal y un poco de lana. Walter dice que debería hacer cerveza más a menudo, y ella dice que él debería beber menos.",
        ),
        "huso_agnes" to unDia(
            Parrafo.MANANA to "Hoy Agnes hiló un huso entero ella sola, de la mañana a la tarde, sin que se le rompiera el hilo ni una vez. Se lo trae a su madre como quien trae una liebre cazada. El hilo es parejo y fino, y {nombre} lo mira dos veces antes de decir nada.",
            Parrafo.GENTE to "Edith dice que a la edad de Agnes ella ya hilaba tres husos al día, y Agnes le contesta que entonces los husos serían más pequeños. Edith se ríe tanto que se le cae el suyo. {nombre} guarda el hilo de Agnes aparte, para tejerle algo con él.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que una mujer que hila bien siempre tendrá algo que vender, aunque no tenga tierra. Agnes ya lo tiene. Piensa también que pronto la querrán en otras casas, para servir o para casarla, y prefiere no pensarlo más por hoy.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "medieval", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
