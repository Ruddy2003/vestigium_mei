package io.github.ruddy2003.vestigium.vidas.piezas

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Entorno
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Estado
import io.github.ruddy2003.vestigium.vidas.Genero
import io.github.ruddy2003.vestigium.vidas.ObjetoCuento
import io.github.ruddy2003.vestigium.vidas.Parrafo
import io.github.ruddy2003.vestigium.vidas.Piezas
import io.github.ruddy2003.vestigium.vidas.TextoEspecial
import io.github.ruddy2003.vestigium.vidas.Variante

/*
 * El cuento de Demeas, remero. Es el modelo que copian las tandas de textos: la gramática y el tono se fijaron aquí.
 *
 * LA GRAMÁTICA
 * - Ocho párrafos, uno por lugar, siempre en este orden: la mañana, el tiempo, el trabajo, el pueblo, la gente, el
 *   objeto, lo que piensa y el anochecer.
 * - Cada párrafo se tiene que poder leer junto a cualquier otro: no empieza con «también», «por eso» ni «después», y
 *   no da por dicho nada de otro párrafo.
 * - Todo en presente y en tercera persona. Así se lee igual a las ocho de la mañana que a las once de la noche.
 * - El párrafo del trabajo es el único que cambia con la hora: empieza con «A esta hora» y hay variantes para cada
 *   cosa que el personaje hace en «Un día suyo» (trabajar, comer, estar con otros, dormir).
 * - Huecos: {nombre}, {edad} (con letras), {estacion}, {cielo} y {de_estacion} (oraciones enteras, de las listas de
 *   abajo), {el_objeto}, {un_objeto}, {dato_objeto} (oración entera), {entorno} y {mundo} (oraciones enteras que
 *   pueden quedar vacías). Para el género, {cansado|cansada}.
 * - Un día especial cambia 3 o 4 párrafos el primer día. Si dura más, cada día siguiente tiene su texto, o uno o dos
 *   lugares «que siguen» con varias versiones que se turnan.
 * - Una fiesta que vuelve cada año comparte su texto: no nombra a nadie que vaya a morir o que todavía no haya nacido.
 * - Quien deja de estar (o empieza a estar) se marca en la variante con `hasta` o `desde` y el id del día en que pasa.
 * - Lo que solo se hace una parte del año se marca con `meses`. Así no sale remando en un párrafo y con la nave
 *   guardada en otro.
 * - El párrafo del objeto no nombra el objeto: lo presenta su propia oración, {dato_objeto}.
 *
 * EL TONO
 * - Frases cortas y cosas que se ven, se oyen o se huelen. Dos o tres oraciones por párrafo: el cuento entero se lee
 *   en un minuto.
 * - Un dato de su época por párrafo, dicho como lo diría alguien de allí, sin explicarlo como en un libro.
 * - Sin años, sin «en aquella época», sin palabras de hoy y sin moraleja. Sin signos de admiración, salvo en lo que
 *   alguien grita.
 * - Humor seco, de poca cosa. El personaje no es un héroe ni una víctima: es alguien que hace su trabajo.
 * - No repite lo que ya dice «Su vida» (cuánto gana, cuántos reman): lo da por sabido.
 */
internal object PiezasDemeas {
    private const val MAR = "mar"

    /** De mayo a octubre el «Aura» está en el agua; el resto del año duerme en su cobertizo. */
    private val CON_NAVE = (5..10).toSet()

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se levanta a oscuras, cuando cantan los gallos del vecino. Moja pan de cebada en un poco de vino puro, que es todo el desayuno, y sale sin despertar a nadie."),
        v("La primera luz entra por la puerta del patio, que es la única ventana de la casa. {nombre} se echa en la cara agua de la tinaja y se ata las sandalias sentado en el umbral."),
        v("Lo despierta el martilleo de los astilleros, que empieza antes que el día. Desde la puerta se ve un trozo de mar, gris todavía, y los mástiles quietos del puerto."),
        v("El sol sale por detrás del monte Himeto y la cuesta del puerto se llena de sombras largas. {nombre} las pisa de camino a los cobertizos de las naves, con el pan todavía en la mano."),
        v("En casa se desayuna de pie: pan de ayer, unas aceitunas, un trago. Melita ya está en el telar cuando {nombre} sale, y no levanta la vista: va contando hilos."),
        v("{nombre} toca al pasar el Hermes de piedra que hay junto a la puerta, como hace quien sale temprano, y baja hacia el puerto con el frío de la noche todavía metido en las calles."),
        v("Cares se despierta con él y lo sigue descalzo hasta la esquina. Ahí se queda, mirando cómo su padre baja la cuesta, hasta que Melita lo llama."),

        v("{nombre} despierta en la arena, entre hombres que duermen pegados a su nave. El trirreme pasó la noche varado, con la popa en la playa, y hay que empujarlo al agua antes de que caliente el sol.", MAR),
        v("Amanece en una playa que no es de nadie que él conozca. El cómitre pasa entre los que duermen dando voces, y cada uno busca su remo, su cojín y su correa antes de abrir del todo los ojos.", MAR),
        v("La primera luz encuentra las naves en fila sobre la arena, como focas. {nombre} se lava en el mar, que a esa hora está frío, y desayuna harina de cebada amasada con agua y un chorro de vino.", MAR),
        v("Ha dormido mal: la arena guarda el frío y los centinelas se relevan a gritos. Cuando el piloto mira el cielo y dice que se puede salir, {nombre} ya tiene las manos en el casco para empujar.", MAR),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion}"),
        v("{nombre} mira el cielo como lo miran los remeros: pensando en el mar. {cielo}"),
        v("{de_estacion} {cielo}"),
        v("{cielo} Los marineros viejos dicen que es lo normal en {estacion}, y también dicen que antes no era así."),
        v("Lo primero que hace al despertar es mirar hacia el mar, por costumbre. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Entre gente de mar, el tiempo solo es noticia cuando hunde algo."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, sin una nube.",
            "Hace un día claro, de esos en que las islas parecen más cerca.",
            "No hay una nube, y la luz es tan dura que el mar no se puede mirar de frente.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, de un gris parejo.",
            "Hay nubes altas que no se deciden a nada.",
            "Está nublado y el mar tiene el color del plomo.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve desde la madrugada, una lluvia fina que lo empapa todo sin hacer ruido.",
            "Llueve, y el agua corre por donde puede.",
            "Cae una lluvia terca, de las que no dejan secar la ropa.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: truena sobre el mar y nadie con juicio suelta amarras.",
            "El cielo se ha puesto negro por el sur, y el trueno llega antes que la lluvia.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del norte, el que levanta el mar y arranca los toldos.",
            "Hay viento, y las olas llegan con espuma.",
            "El viento no para, y trae sal hasta donde no hay mar.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera: los días se alargan y el mar vuelve a ser camino.",
            "En primavera todo huele a brea caliente, porque es cuando se repasan los cascos.",
            "Es el tiempo en que vuelven las golondrinas y las naves salen de sus cobertizos.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, la estación de remar y la de la guerra.",
            "En verano la brea de las tablas se ablanda al sol y se pega a las manos.",
            "Es pleno verano, y las cigarras no callan ni de noche.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño: se acaba el tiempo de navegar y cada quien mira lo que guardó.",
            "En otoño el mar empieza a cerrarse y las naves vuelven una por una.",
            "El otoño trae las primeras lluvias y el olor del mosto.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando el mar se cierra y los remeros buscan jornal en tierra.",
            "En invierno las naves duermen bajo techo y el puerto se queda a medio hablar.",
            "Es invierno, y anochece antes de que uno termine lo que empezó.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora rema. El «Aura» ha salido a ejercitarse frente al puerto: arrancar, parar, virar en redondo, una y otra vez, hasta que las tres filas de remos entran al agua como una sola.", hacer = Hacer.TRABAJO, meses = CON_NAVE),
        v("A esta hora carga. Cuando no hay salida, los remeros se alquilan en el muelle: sacos de trigo, ánforas, fardos de pieles. Se paga menos que el remo y cansa de otra manera.", hacer = Hacer.TRABAJO),
        v("A esta hora tiene las manos negras de brea. Están repasando las costuras del casco, tabla por tabla, y el trierarca mira desde la sombra: la nave es de la ciudad, pero los arreglos los paga él.", hacer = Hacer.TRABAJO),
        v("A esta hora está en su banco, el de la fila de arriba, donde el remo entra en el agua más empinado y pesa más. El flautista marca el golpe y nadie habla: el aire se guarda para remar.", hacer = Hacer.TRABAJO, meses = CON_NAVE),
        v("A esta hora espera, que también es trabajo. Sentado en la rampa con los de su banco, aguarda a que el piloto decida si hoy se sale, y mientras tanto engrasa la correa del remo.", hacer = Hacer.TRABAJO, meses = CON_NAVE),
        v("A esta hora cose. En el arsenal siempre hay velas que remendar y cuero que cortar para los remos, y se paga por pieza: quien cose derecho cobra, y quien no, repite.", hacer = Hacer.TRABAJO),
        v("A esta hora acarrea madera para los carpinteros de ribera: pino para las tablas, abeto para los remos, roble para la quilla. Viene de lejos, por mar, y cada tronco está contado.", hacer = Hacer.TRABAJO),

        v("A esta hora rema, como casi todas las horas de estos días. La costa pasa despacio por la derecha y él la ve alejarse, porque un remero siempre mira hacia donde ya estuvo.", MAR, Hacer.TRABAJO),
        v("A esta hora lleva el golpe largo, el que se puede aguantar el día entero. El flautista toca siempre lo mismo y el cómitre grita de vez en cuando, más por costumbre que por falta.", MAR, Hacer.TRABAJO),
        v("A esta hora descansa su fila mientras reman las otras dos. Tiene el remo recogido y la vista en la espalda del de adelante, que es casi todo lo que ve un remero en un viaje.", MAR, Hacer.TRABAJO),
        v("A esta hora reman fuerte: el piloto quiere doblar el cabo antes de que cambie el viento. A su fila, la de arriba, le llega el aire; a los de abajo solo les llega el sudor de los de arriba.", MAR, Hacer.TRABAJO),

        v("A esta hora come: torta de cebada, un puñado de aceitunas y pescado en sal, sentado donde le agarró el hambre. El vino va con tres partes de agua, que el día es largo.", hacer = Hacer.COMER),
        v("A esta hora come, de pie y deprisa: pan, cebolla, queso de cabra, y los higos secos que Melita le mete en el zurrón sin decirle nada.", hacer = Hacer.COMER),
        v("A esta hora se sienta a comer con los de su banco. Cada uno pone lo que trae y se parte como salga; el que trae boquerones fritos de Falero come con más amigos.", hacer = Hacer.COMER),

        v("A esta hora comen en la playa, con la nave varada y los remos clavados en la arena: harina de cebada amasada con agua y aceite, cebolla, y lo que vendan los del lugar, si es que venden.", MAR, Hacer.COMER),
        v("A esta hora la tripulación entera come en tierra, porque en un trirreme no cabe ni una olla. {nombre} moja su torta en vino y vigila de reojo su remo, no se lo vayan a cambiar por otro peor.", MAR, Hacer.COMER),

        v("A esta hora está en la plaza del puerto, donde todo se sabe antes que en Atenas. Los que llegan en los barcos de grano traen las noticias, y cada cual las reparte como si fueran suyas.", hacer = Hacer.GENTE),
        v("A esta hora bebe con los de su banco en la taberna de la rampa. Hablan de pagas atrasadas y de pilotos, y discuten cuál es la nave más rápida de la flota, que es discutir por gusto: cada uno dice la suya.", hacer = Hacer.GENTE),
        v("A esta hora se corta el pelo en la barbería del puerto, que es donde de verdad se discute de política. El barbero opina de todo mientras afila, y nadie le lleva la contraria a un hombre con una navaja.", hacer = Hacer.GENTE),
        v("A esta hora juega a las tabas con los vecinos, en la calle, mientras queda luz. Se apuestan habas, porque los óbolos hacen falta, y gana casi siempre el mismo.", hacer = Hacer.GENTE),

        v("A esta hora ya hay hogueras en la playa. Los remeros se juntan por naves y por barrios, y alguien cuenta por décima vez la tormenta más grande que ha visto, que cada vez es más grande.", MAR, Hacer.GENTE),
        v("A esta hora está sentado en la arena con Sosias, remendando las correas de los remos. Hablan poco: llevan tanto tiempo remando uno junto al otro que ya se lo han dicho todo.", MAR, Hacer.GENTE),
        v("A esta hora la playa es una ciudad de una sola noche: se canta, se regatea con los pastores que bajan a vender queso, y los pilotos dibujan cabos en la arena con un palo.", MAR, Hacer.GENTE),

        v("A esta hora duerme. Los remeros duermen como piedras: el cuerpo cobra de noche lo que prestó de día.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, boca arriba y con las manos a medio cerrar, que es como se le quedan después del remo.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y si sueña, sueña con el golpe: dicen que un remero sigue llevando el compás aunque esté dormido.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El Pireo es una ciudad trazada a cordel, con calles rectas que se cruzan en escuadra; dicen que la dibujó un hombre de Mileto. {entorno}"),
        v("En el puerto grande descargan trigo, pieles, pescado seco y gente que habla raro. Todo lo que Atenas come pasa primero por delante de su casa. {entorno}"),
        v("Desde la colina de Muniquia se ven los tres puertos: el grande, de los mercaderes, y los dos chicos, de las naves de guerra. {entorno}"),
        v("{entorno} En la calle de los cordeleros trenzan cables tan largos que hay que salir a la plaza para estirarlos."),
        v("Los cobertizos de las naves bajan en fila hasta el agua, uno junto a otro, como los pesebres de un establo. {entorno}"),
        v("El camino a Atenas va entre dos muros largos, y por él suben los carros de pescado antes de que amanezca del todo. {entorno}"),

        v("La costa del Peloponeso es tierra enemiga y se nota: los pueblos se vacían cuando asoman las velas, y solo quedan los perros y el humo de las cocinas.", MAR),
        v("Navegan con la tierra siempre a la vista, de cabo en cabo. Al fondo hay montes azules que {nombre} no sabe nombrar; el piloto sí, y los dice en voz alta, como quien cuenta ovejas.", MAR),
        v("Cien naves juntas ocupan el mar hasta donde alcanza la vista. Los pescadores de por aquí recogen las redes y se apartan sin prisa: saben que nadie viene por ellos.", MAR),
        v("Cada tarde hay que buscar una playa con agua dulce cerca, y no todas la tienen. Donde la hay, la flota entera hace cola con los odres.", MAR),
    )

    /** Lo que se ve alrededor según cómo va la guerra. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("peste", 0.5, frases = listOf(
            "Hay humo de piras a todas horas, y nadie pregunta ya por quién.",
            "Las calles están medio vacías: quien puede, no sale.",
        )),
        Entorno("refugiados", 0.8, frases = listOf(
            "Ahora hay chozas hasta en los huecos de la muralla, y familias enteras viviendo en los santuarios.",
            "Entre los dos muros ya no cabe un carro: está todo lleno de gente del campo, con sus cabras y sus tinajas.",
        )),
        Entorno("estragos", 0.25, frases = listOf(
            "Desde lo alto del muro se ve la llanura quemada, donde antes había olivos.",
            "La gente del campo que se quedó en la ciudad sube cada tarde al muro a mirar hacia su tierra.",
        )),
        Entorno("refugiados", 0.3, 0.8, frases = listOf(
            "Todavía quedan chozas de gente del campo arrimadas a los muros.",
        )),
    )

    private val GENTE = listOf(
        v("Melita teje junto a la puerta, donde hay luz. Vende lo que le sobra y con eso compra la lana siguiente; dice que el telar es su barco, solo que el suyo no se hunde."),
        v("Cares tiene la edad de preguntarlo todo: por qué flota la madera, quién les pinta los ojos a las naves, si los delfines duermen. {nombre} contesta lo que sabe e inventa el resto."),
        v("Sosias rema a su lado desde hace años. Se conocen el resuello: cada uno sabe cuándo el otro va a aflojar antes de que afloje."),
        v("La vecina le pide fuego a Melita cada mañana y a cambio trae las noticias de todo el barrio. En su calle nadie cierra la puerta de día: quien la cierra tiene algo que esconder."),
        v("En casa son pocos y el dinero es justo, pero en las fiestas grandes la ciudad reparte carne, y ese día Melita guisa como si fueran veinte."),
        v("Los de su banco son su otra familia: sabe cómo ronca cada uno y a qué dios le promete algo cada vez que hay mala mar. Ninguno ha cumplido todavía."),
        v("Estratón, su padre, ya no rema, pero baja cada día al puerto a ver quién entra y quién sale. Vuelve con noticias que nadie le ha pedido y las cuenta dos veces.", hasta = "muere_estraton"),
        v("Su padre remó en Salamina y lo cuenta con las manos: aquí los persas, aquí el estrecho, aquí nosotros. Cares ya se lo sabe, y lo corrige cuando se salta una nave.", hasta = "muere_estraton"),
        v("Clío, su hermana, viene los días de mercado con un cesto y con chismes. Desde que se casó con Sosias dice que ya entiende por qué los remeros llegan a casa sin ganas de hablar.", desde = "boda_clio"),
        v("El tío Eufronio se ha quedado a vivir en el patio, bajo un toldo, con sus sacos de carbón y su mal genio. Dice que es por pocos días. Lo lleva diciendo desde que llegó.", desde = "llega_eufronio"),

        v("En casa quedaron Melita y Cares, y él los piensa a la hora en que allá se enciende la lámpara. No hay manera de mandar recado: las noticias de la flota llegan cuando llega la flota.", MAR),
        v("Sosias rema a su lado, tan cerca que se dan con el codo si uno pierde el compás. Después de tantos días ya no se dan: respiran a la vez.", MAR),
        v("En el banco de adelante va un muchacho de Falero que hace su primer viaje y todavía cuenta los días. {nombre} dejó de contarlos hace años; solo cuenta las playas.", MAR),
        v("El trierarca duerme en la popa, bajo un toldo, y es el único de a bordo que no rema. Paga bien y a tiempo, y por eso se le perdona que se maree.", MAR),
    )

    private val OBJETO = listOf(
        v("Hay días que tienen su cosa. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe sin haberlo pensado nunca, como se saben las cosas de todos los días."),
        v("Hoy se acuerda, sin venir a cuento, de algo que conoce desde siempre. {dato_objeto}"),
        v("Hay cosas que uno no mira hasta que faltan. {dato_objeto}"),
        v("{dato_objeto} Es poca cosa, pero un remero tiene pocas cosas y las conoce todas."),
        v("{dato_objeto} Si un extranjero le preguntara por qué es así, {nombre} no sabría qué decirle: siempre ha sido así."),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("remo", "el remo", "un remo", "El remo",
            "Su remo es de abeto, largo como dos hombres y medio, y lleva una marca cerca del puño: en la flota cada remero carga con el suyo y responde por él."),
        ObjetoCuento("cojin", "el cojín de remero", "un cojín de remero", "El cojín de remero",
            "El cojín de remero es de piel de oveja con su lana: sin él, las tablas del banco se cobran la piel en media mañana."),
        ObjetoCuento("estrobo", "la correa del remo", "una correa de remo", "La correa del remo",
            "La correa del remo es un lazo de cuero que lo sujeta a su estaca; se engrasa con sebo para que no chille, y si se rompe a media boga el remo se va al agua y el remero queda en vergüenza."),
        ObjetoCuento("anfora", "el ánfora del aceite", "un ánfora", "El ánfora",
            "El ánfora de la casa guarda el aceite del año; tiene el fondo en punta, así que no se sostiene sola y vive recostada en un rincón, como un borracho."),
        ObjetoCuento("lampara", "la lámpara", "una lámpara", "La lámpara",
            "La lámpara de la casa es de barro, del tamaño de una mano, y quema aceite de oliva con una mecha de lino; da la luz justa para verle la cara al que está enfrente."),
        ObjetoCuento("red", "la red", "una red", "La red",
            "La red de la casa viene de su padre y tiene más remiendo que malla; cuando no hay paga la echan desde las rocas de la bocana, y algo sale siempre."),
        ObjetoCuento("higos", "los higos secos", "unos higos secos", "Los higos",
            "Los higos secos van ensartados en un junco, como cuentas; son el dulce de los pobres y lo único que aguanta un viaje entero sin echarse a perder."),
        ObjetoCuento("dracma", "la dracma", "una dracma", "La dracma",
            "La dracma es de plata y tiene la lechuza por un lado y la diosa por el otro; como casi nadie lleva bolsa, las monedas chicas viajan en la boca, debajo de la lengua."),
        ObjetoCuento("aulos", "la flauta del barco", "una flauta doble", "La flauta",
            "La flauta del barco no es suya, pero la oye más que la voz de su mujer: son dos cañas que se soplan a la vez y marcan el golpe del remo desde que se sale hasta que se vara."),
        ObjetoCuento("copa", "la copa buena", "una copa", "La copa",
            "La copa buena de la casa es de barro negro, ancha y con dos asas; se saca cuando hay visita, y el vino llega a ella ya mezclado con agua, que beberlo puro es cosa de bárbaros."),
        ObjetoCuento("manto", "el manto", "un manto", "El manto",
            "El manto es un paño de lana sin una sola costura: de día va al hombro, de noche es manta, y en el barco se dobla y sirve de almohada."),
        ObjetoCuento("pan", "el pan", "un pan", "El pan",
            "El pan de todos los días es de cebada y no sube: una torta gris que se amasa y se come casi cruda; el de trigo, blanco, es para las fiestas."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} no es hombre de discursos, pero vota, y antes de votar escucha a todos, hasta a los que gritan."),
        v("Piensa a veces en lo raro que es su oficio: mover una casa de madera por el agua a fuerza de brazos, sin ver nunca hacia dónde va."),
        v("{mundo} Él piensa en cosas más cercanas: en si alcanzará el aceite, en el techo, que hay que repasar antes de las lluvias."),
        v("No tiene tierra ni la tendrá. Tiene un remo, un voto y un sitio en un banco, y hay días en que eso le parece mucho y días en que le parece nada."),
        v("{mundo} Un remero no decide adónde va la nave, pero sin él no va a ninguna parte. {nombre} le da vueltas a eso más de lo que admitiría."),
        v("{mundo} A sus {edad} años, {nombre} sabe que el cuerpo no dura siempre al remo. De lo que hará después no habla con nadie."),
    )

    private val MUNDOS = mapOf(
        "guerra" to listOf(
            "En todas partes se habla de Esparta, y cada cual sabe lo que habría que hacer.",
            "Dicen que la guerra será corta. Lo dicen los que no reman.",
            "Dicen que la ciudad es una isla mientras tenga naves, y a los remeros les gusta cómo suena.",
        ),
        "peste" to listOf(
            "De la enfermedad se habla en voz baja, como si pudiera oír.",
            "Nadie hace planes para más allá de este mes.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando oscurece, Melita enciende la lámpara y la casa se encoge alrededor de la llama. Se cena lo que hay: lentejas, pan, un poco de queso. Luego se apaga, porque el aceite cuesta."),
        v("Al caer el sol el puerto cambia de ruido: callan los martillos y empiezan las tabernas. {nombre} vuelve a casa por la calle ancha, con el cuerpo molido y las manos calientes."),
        v("Anochece, y sube del mar un fresco que huele a algas. {nombre} se sienta en el umbral a no hacer nada, que es lo que mejor sabe hacer un remero en tierra."),
        v("De noche el Pireo se guía por pocas luces: los fuegos de los vigías en la bocana y alguna lámpara detrás de una puerta. {nombre} se duerme pronto, que mañana hay que estar en la rampa antes del alba."),
        v("Se cena con las manos, mojando el pan en la olla. Cares se duerme sentado antes de terminar, y {nombre} lo lleva a su jergón sin que abra los ojos."),
        v("La última luz se queda un rato en lo alto de la Acrópolis, que desde aquí es una raya blanca a lo lejos. Después solo quedan las estrellas, que {nombre} conoce por su nombre de marinero."),

        v("Antes de que se vaya la luz varan las naves de popa, una junto a otra, y se reparten las guardias. {nombre} cena junto al fuego de su banco y se duerme con el manto por la cabeza.", MAR),
        v("La noche en la playa es de arena fría y de estrellas hasta el borde del mar. Alguien canta bajito una canción del Pireo, y al segundo verso ya la canta media nave.", MAR),
        v("Cuando oscurece cuentan los días que llevan fuera, y nadie da la misma cuenta. {nombre} se acuesta con el remo al lado, como quien duerme con su perro.", MAR),
        v("Al anochecer el piloto mira el poniente y dice qué mar habrá mañana. Acierta casi siempre. {nombre} se envuelve en el manto y deja que la tierra, por una vez, se esté quieta.", MAR),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "leneas" to unDia(
            Parrafo.PUEBLO to "Hoy son las Leneas, la fiesta de Dioniso que cae en lo peor del invierno. Con el mar cerrado no hay extranjeros en la ciudad, y los poetas de comedia aprovechan para decir lo que en primavera no dirían.",
            Parrafo.TRABAJO to "A esta hora está en el teatro, apretado en un banco de madera con medio Pireo. En escena, un actor con barriga postiza se burla de un general, y el general, que está sentado abajo, tiene que reírse.",
            Parrafo.ANOCHECER to "Vuelve de Atenas de noche, entre los dos muros, con otros cien que repiten los chistes para que no se les olviden. Para cuando llega a casa ya los cuenta mejor que el poeta.",
        ),
        "boda_clio" to unDia(
            Parrafo.MANANA to "Hoy se casa Clío, su hermana, con Sosias, el que rema a su lado. Es el mes de las bodas. Desde temprano las mujeres van y vienen de la fuente con el agua para el baño de la novia.",
            Parrafo.TRABAJO to "A esta hora no trabaja: hoy el día es de la familia. En el patio se asa un cabrito, y Melita y las vecinas amasan las tortas de sésamo, que en una boda no pueden faltar.",
            Parrafo.GENTE to "Estratón entrega a su hija con las palabras de siempre, y le tiembla un poco la voz. Sosias, que en el barco no se calla, hoy no encuentra qué decir. {nombre} piensa que ya no va a poder hablar mal de la familia mientras rema.",
            Parrafo.ANOCHECER to "Al anochecer llevan a la novia en carro hasta su casa nueva, con antorchas y con el canto de boda, y los chiquillos les tiran higos y nueces. {nombre} camina detrás y canta más fuerte que nadie.",
        ),
        "antesterias" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empiezan las Antesterias, la fiesta del vino nuevo. Se abren las tinajas que llevan cerradas desde la vendimia, y lo primero que sale es para el dios.",
                Parrafo.GENTE to "Melita ha untado la puerta con brea, y en casa todos mastican hojas de espino: estos días, dicen, los muertos andan sueltos por la ciudad, y conviene que pasen de largo.",
                Parrafo.ANOCHECER to "Esta noche se prueba el vino en todas las casas, también en la suya, y se le da a todo el mundo: al viejo, al niño y, donde hay esclavos, a los esclavos.",
            ),
            dia(
                Parrafo.TRABAJO to "A esta hora bebe. Es el día de las Jarras: cada uno lleva la suya, del mismo tamaño que la de los demás, y a la señal de la trompeta gana el que la vacía primero, sin decir palabra.",
                Parrafo.GENTE to "A los niños que ya cumplieron tres años les regalan hoy una jarrita y una corona de flores. La de Cares está en un estante desde hace tiempo, con el asa rota.",
            ),
            dia(
                Parrafo.MANANA to "Último día de las Antesterias, el de las Ollas. En cada casa se cuece una olla de granos de todas clases que nadie prueba: es para Hermes, el que guía a los muertos.",
                Parrafo.ANOCHECER to "Al caer la tarde se echa a las ánimas con la frase de siempre: «¡Fuera, que ya se acabaron las Antesterias!». Melita la grita desde la puerta, y Cares la repite hasta que lo mandan callar.",
            ),
        )),
        "dionisias" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las Grandes Dionisias. Ahora que el mar se abre, la ciudad está llena de forasteros, y antes de las obras desfila todo: los toros del sacrificio, los cestos, los falos de madera en andas.",
                Parrafo.TRABAJO to "A esta hora está en la calle, viendo pasar la procesión hacia el teatro. Los que pagan los coros van delante, vestidos de púrpura y oro, y la gente los mide con la vista: hoy se sabe quién tiene dinero y quién lo debe.",
                Parrafo.PUEBLO to "En el teatro, a la vista de todos, se pone en el suelo el tributo que mandan las ciudades aliadas, talento por talento. Los extranjeros miran la plata; los atenienses miran a los extranjeros mirarla.",
                Parrafo.GENTE to "Su tribu compite con un coro de cincuenta hombres que cantan y bailan en corro. Sosias está en él y lleva un mes ensayando; desde la grada, {nombre} canta la letra por lo bajo sin darse cuenta.",
            )),
            sigue = mapOf(
                Parrafo.TRABAJO to listOf(
                    "A esta hora está en el teatro, desde el alba, con el pan y las aceitunas en el regazo. Hoy tocan tres tragedias seguidas del mismo poeta y, al final, una farsa de sátiros para que la gente no se vaya triste.",
                    "A esta hora sigue sentado en la grada de madera, con medio pueblo. Cuando una obra aburre, el público patea las tablas y chista; cuando gusta, no se oye ni una tos.",
                    "A esta hora mira hacia la escena por encima de mil cabezas. Los actores son solo tres y llevan máscara, así que el mismo hombre hace de rey y, un rato después, de su viuda.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Baja del teatro con los ojos cansados de tanto mirar lejos. En la taberna cada uno ha dado ya su premio, y ninguno coincide con el de los jueces.",
                    "De vuelta al Pireo se discute de las obras como se discute de las naves: a gritos y sin escuchar. {nombre} defiende al poeta que hizo llorar a Sosias.",
                ),
            ),
        ),
        "medea" to unDia(
            Parrafo.PUEBLO to "Hoy Eurípides presenta una tragedia nueva: la de Medea, la extranjera a la que Jasón deja por otra y que, para vengarse, mata a sus propios hijos. En la grada hay quien se tapa la cara.",
            Parrafo.GENTE to "Cuando Medea se va por el aire en un carro tirado por dragones, sin castigo, el teatro se queda mudo. Sosias dice que eso no puede acabar así. El viejo de al lado dice que así acaba todo.",
            Parrafo.PENSAMIENTO to "Los jueces le dan a Eurípides el tercer premio, que es el último. {nombre} habría votado lo mismo, y sin embargo no se quita a esa mujer de la cabeza en toda la tarde.",
        ),
        "platea" to unDia(
            Parrafo.MANANA to "Amanece con una noticia que corre más que los heraldos: anoche, con lluvia y sin luna, trescientos tebanos entraron en Platea, la ciudad amiga del otro lado de los montes.",
            Parrafo.PUEBLO to "En la plaza del puerto nadie compra ni vende. Dicen que los de Platea se rehicieron y tienen presos a los tebanos; dicen que los van a matar; dicen que ya los mataron. Todo el mundo dice.",
            Parrafo.PENSAMIENTO to "Hace catorce años que se juró la paz con Esparta, y era para treinta. {nombre} era un muchacho entonces. Hoy entiende, como todos, que se acabó, y que este verano va a remar de otra manera.",
        ),
        "botadura" to unDia(
            Parrafo.MANANA to "Hoy echan al agua el «Aura», que pasó el invierno en seco bajo su cobertizo. Lo bajan por la rampa a fuerza de hombros y de sogas, con cuidado: un trirreme es largo, ligero, y se tuerce como un junco.",
            Parrafo.TRABAJO to "A esta hora le ciñen al casco los cables de refuerzo, que lo abrazan de proa a popa y lo mantienen entero. Luego cada remero ocupa su sitio por primera vez en meses y busca en la madera la muesca de su talón.",
            Parrafo.ANOCHECER to "El trierarca derrama vino en el agua desde la proa y pide un buen año. Esta noche el «Aura» duerme a flote, y {nombre} huele a brea aunque se lave tres veces.",
        ),
        "muniquias" to unDia(
            Parrafo.MANANA to "Hoy son las Muniquias, la fiesta de Ártemis en la colina que guarda el puerto. Hay luna llena, y a la diosa se le llevan unas tortas redondas con velitas encendidas alrededor.",
            Parrafo.GENTE to "Los viejos del Pireo suben la cuesta despacio. Dicen que la diosa alumbró con su luna a los griegos en Salamina, y los que estuvieron no dicen ni que sí ni que no: dicen que remaban y miraban al de adelante.",
            Parrafo.ANOCHECER to "Desde abajo, las velitas de las tortas se ven como un collar alrededor del santuario. Cares quiere saber si la diosa se las come con todo y cera, y nadie sabe contestarle.",
        ),
        "targelias" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empiezan las Targelias, la fiesta de Apolo antes de la siega. El primer día es para limpiar la ciudad de sus culpas, y nadie lo pasa a gusto: se barre, se come poco, se habla bajo.",
                Parrafo.PUEBLO to "Sacan de la ciudad a dos pobres diablos cargados con las faltas de todos, con collares de higos al cuello, y la gente los despide a varazos de higuera. Mañana volverán a ser dos hombres cualquiera.",
                Parrafo.PENSAMIENTO to "{nombre} mira pasar a los dos hombres y piensa que, de todos los oficios de la ciudad, ese es el único peor pagado que el suyo.",
            ),
            dia(
                Parrafo.MANANA to "Segundo día de las Targelias, el alegre. Se le ofrece a Apolo la primera papilla del grano nuevo, cocida en olla, y por las calles desfilan coros de hombres y de muchachos.",
                Parrafo.GENTE to "Melita cuece su propia olla de primicias, con un poco de cada cosa que ha dado la tierra este año. Cares mete el dedo antes de tiempo y se lleva un manotazo y una cucharada.",
            ),
        )),
        "siega" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Ha empezado la siega en el Ática. La cebada va primero y el trigo después, y durante unas semanas no hay brazo que sobre: hasta los remeros se alquilan de segadores.",
                Parrafo.TRABAJO to "A esta hora siega, doblado, con una hoz prestada. Es otro cansancio que el del remo: allá se empuja con las piernas, aquí manda la cintura, y a media mañana ya no sabe cuál prefiere.",
                Parrafo.ANOCHECER to "Le pagan con una medida de grano y con la cena, que se come en la era, entre la paja. Vuelve a casa de noche, con briznas en el pelo y las manos cortadas por otro sitio que el de siempre.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "En las eras de fuera de los muros se trilla con mulas que dan vueltas sobre la mies, y luego se avienta con palas: el grano cae, la paja vuela, y el aire entero pica en la garganta.",
                "Por el camino de Atenas bajan los carros de gavillas, tan cargados que parecen pajares con ruedas. Los niños corren detrás recogiendo lo que se cae.",
                "Estos días la plaza está medio vacía: quien tiene un pedazo de tierra está en él, y quien no, en el de otro, con una hoz.",
            )),
        ),
        "llega_eufronio" to unDia(
            Parrafo.MANANA to "Esta mañana aparece en la puerta el tío Eufronio, carbonero de Acarnas, con una carreta, dos cabras y la cara del que no ha dormido. Pericles ha mandado que la gente del campo se meta tras los muros.",
            Parrafo.PUEBLO to "Por las puertas de la ciudad no dejan de entrar carros. Traen tinajas, niños, gallinas, y hasta las puertas y las vigas de sus casas, para que el enemigo no tenga qué quemar. Las ovejas las mandan en barcas a Eubea.",
            Parrafo.GENTE to "Eufronio descarga en el patio sin pedir permiso y sin dejar de hablar: de sus olivos, de sus hornos de carbón, de que él a los espartanos los espera con el hacha. Melita le pone un plato delante, y come.",
            Parrafo.ANOCHECER to "Esta noche duerme en la casa el doble de gente, y las cabras en la calle, atadas al Hermes. {nombre} tarda en dormirse: nunca había oído roncar a su tío.",
        ),
        "primera_invasion" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy han entrado. El rey de Esparta ha cruzado la frontera con el ejército más grande que se recuerda, y acampa en Acarnas, a una mañana de camino de los muros.",
                Parrafo.PUEBLO to "Desde lo alto del muro se ve el humo. Primero una columna, luego seis, luego nadie las cuenta. Los de Acarnas, que son muchos y son tercos, quieren salir a pelear, y Pericles no convoca la asamblea para que no lo voten.",
                Parrafo.GENTE to "Eufronio sube al muro, mira hacia su pueblo y baja sin decir nada, que en él es lo más raro que puede pasar. Luego afila el hacha, por hacer algo.",
                Parrafo.PENSAMIENTO to "A los remeros les han dicho que su turno llegará por mar. {nombre} lo cree, pero hoy le cuesta: es difícil ver arder el campo de otro con los brazos cruzados.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "El enemigo sigue en la llanura. Cada día el humo sale de un sitio distinto, y la gente del campo sabe decir, solo por dónde sale, de quién es lo que arde.",
                "La ciudad está llena hasta los tejados. Hay familias viviendo en las torres de la muralla, en los pórticos y en los santuarios; solo la Acrópolis sigue cerrada.",
                "La caballería sale cada mañana a estorbar a los que saquean cerca de los muros, y vuelve cubierta de polvo. Es todo lo que Pericles deja hacer por tierra.",
                "En los muros hay guardia de día y de noche, y a los remeros también les toca. Desde arriba se ven de noche las hogueras del campamento enemigo, tantas como si fuera otra ciudad.",
            )),
        ),
        "zarpa_flota" to unDia(
            Parrafo.MANANA to "Hoy zarpa la flota: cien naves, a rodear el Peloponeso y devolver el daño por mar. {nombre} baja al puerto con el remo, el cojín y la correa, y con pan para tres días en el zurrón.",
            Parrafo.TRABAJO to "A esta hora las cien naves salen por la bocana, una detrás de otra, y el puerto entero grita desde los muelles. Luego el flautista da el golpe, los remos caen a la vez y ya no se oye más que el agua.",
            Parrafo.GENTE to "Melita lo acompaña hasta donde dejan pasar, con Cares de la mano. No lloran, que trae mala suerte. Estratón le da un golpe en el hombro y le dice lo que su padre le dijo a él: «Tú mira al de adelante».",
            Parrafo.ANOCHECER to "La primera noche varan en una playa desde la que todavía se ve, muy lejos, la colina de Muniquia. Mañana ya no.",
        ),
        "panateneas_en_el_mar" to unDia(
            Parrafo.MANANA to "Hoy en Atenas son las Panateneas. A esta hora la procesión estará cruzando la plaza con el manto nuevo de la diosa, y él está en una playa del otro lado del Peloponeso, sacudiéndose la arena del pelo.",
            Parrafo.GENTE to "Los remeros hacen su fiesta como pueden: el trierarca reparte vino doble y alguien sacrifica un cabrito comprado a un pastor. En casa, hoy, la ciudad reparte carne de cien vacas. Sosias no habla de otra cosa.",
            Parrafo.PENSAMIENTO to "Es la primera vez que {nombre} falta. Piensa en Cares subido a los hombros del abuelo para ver pasar a los jinetes, y rema toda la tarde con la cabeza en otra parte.",
        ),
        "eclipse" to unDia(
            Parrafo.MANANA to "El día empieza como cualquier otro de este verano: empujar la nave, embarcar, remar con la costa a la derecha.",
            Parrafo.TRABAJO to "Pasado el mediodía la luz se vuelve rara, como de tarde. Alguien grita y todos levantan la vista: al sol le falta un pedazo. Se va adelgazando hasta quedar en media luna, y en pleno día asoman unas estrellas.",
            Parrafo.GENTE to "Nadie rema. El flautista se ha quedado con la flauta en la boca, sin soplar. El piloto, que ha visto de todo, dice que eso pasa y que se pasa; en la voz se le nota que no está seguro.",
            Parrafo.ANOCHECER to "El sol vuelve a estar entero antes de ponerse, y esa noche en la playa no se habla de otra cosa. Unos dicen que es un aviso y otros que hoy cambia la luna y nada más. {nombre} no dice nada: lo vio, y le basta.",
        ),
        "vuelve_flota" to unDia(
            Parrafo.MANANA to "Hoy vuelven. Al doblar el último cabo aparece la colina de Muniquia y, detrás, hasta donde alcanza la vista, el campo del color de la ceniza. El flautista, sin que nadie se lo mande, cambia de tonada.",
            Parrafo.TRABAJO to "A esta hora suben el «Aura» a su rampa por primera vez en todo el verano, con el Pireo entero mirando desde los muelles. El trierarca paga lo que debe, moneda sobre moneda, y los remeros cuentan dos veces.",
            Parrafo.GENTE to "Cares ha crecido un palmo y al principio no se atreve a acercarse. Melita le toca la cara, las manos, los hombros, como quien cuenta que no falte nada. Estratón pregunta por la nave antes que por él.",
            Parrafo.ANOCHECER to "Esta noche duerme bajo techo por primera vez en más de dos meses, y no puede: le falta la arena, el ruido del mar, los ronquidos de toda una tripulación. Se duerme al amanecer.",
        ),
        "misterios" to unDia(
            Parrafo.MANANA to "Hoy sale de Atenas la procesión de los Misterios, camino de Eleusis. Son miles, coronados de mirto, con la imagen de Yaco por delante, y gritan su nombre a cada paso.",
            Parrafo.PUEBLO to "El camino sagrado es largo, de sol a sol. En el puente del río esperan unos encapuchados que insultan a los que pasan, sobre todo a los importantes, y es de mala educación enfadarse.",
            Parrafo.PENSAMIENTO to "Lo que pasa después, de noche y a puerta cerrada, no lo cuenta nadie: está prohibido, y la prohibición se cumple. Los que han ido solo dicen que desde entonces le temen menos a la muerte. {nombre} los mira y se lo cree a medias.",
        ),
        "vendimia" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Ha empezado la vendimia. Bajan del campo los cestos de uva, negros de avispas, y en cada lagar hay sitio para quien quiera pisar a cambio de la cena y de un odre de mosto.",
                Parrafo.TRABAJO to "A esta hora pisa uva, agarrado a una soga que cuelga del techo para no resbalar. Un flautista marca el paso, igual que en el barco, y los otros pisadores se ríen de él porque rema hasta con los pies.",
                Parrafo.ANOCHECER to "Vuelve con las piernas moradas hasta la rodilla y un odre de mosto al hombro. Cares mete el dedo, lo chupa y pide más; Melita dice que eso todavía no es vino, y que por eso mismo es peligroso.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días todo huele a mosto: las calles, los carros, la gente. Las tinajas se llenan y se dejan destapadas, hirviendo solas, y hay que avisar a los niños de que no se asomen.",
                "En los lagares se pisa de la mañana a la noche. El mosto corre por un caño de piedra hasta una pila, y de ahí a las tinajas, que esperan medio enterradas en el suelo.",
                "Los que tienen viña andan contando cestos, y los que no, los días que faltan para probar el vino nuevo, que son muchos: hasta la fiesta de las tinajas, en pleno invierno.",
            )),
        ),
        "tesmoforias" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empiezan las Tesmoforias, la fiesta de Deméter, que es solo de las casadas. Melita ha salido antes del alba con un hato de ropa y comida para tres días, y no ha dicho adónde.",
                Parrafo.GENTE to "En casa quedan los hombres, que no saben dónde se guarda la sal. {nombre} hace una sopa que Cares mira con desconfianza y se come con resignación.",
                Parrafo.PENSAMIENTO to "Lo que hacen las mujeres allá arriba es secreto, y al que intenta averiguarlo le va mal. {nombre} no pregunta. Sabe que tiene que ver con la siembra, que ya empieza, y con que la tierra devuelva lo que se le da.",
            ),
            dia(
                Parrafo.PUEBLO to "Segundo día de las Tesmoforias, el del ayuno. Las mujeres lo pasan sentadas en el suelo, sin comer, y la ciudad se queda quieta con ellas: hoy no hay asamblea ni tribunales.",
                Parrafo.ANOCHECER to "La casa sin Melita no suena igual: falta el golpe del telar. Los hombres cenan pan con queso, casi a oscuras, porque nadie se acordó de comprar aceite.",
            ),
            dia(
                Parrafo.MANANA to "Último día de las Tesmoforias, el del buen parto. Allá arriba las mujeres piden hijos sanos y cosechas grandes, que para ellas es la misma petición.",
                Parrafo.ANOCHECER to "Melita vuelve al anochecer, cansada y contenta, con olor a humo y a piñones. No cuenta nada. Mira la olla, mira a los hombres, y se pone a fregar sin hacer preguntas.",
            ),
        )),
        "apaturias" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empiezan las Apaturias, la fiesta de las hermandades. Todo ateniense pertenece a una desde que nace, como su padre y su abuelo, y es ella la que dice quién es ciudadano y quién no.",
                Parrafo.GENTE to "Se sienta con hombres a los que ve tres días al año y que conocen a su familia desde antes de que él naciera. Le preguntan por el niño, por el barco y por la paga, en ese orden.",
                Parrafo.ANOCHECER to "La primera noche es de cena. Cada hermandad se junta en su casa, y en la de {nombre}, que es de gente de mar, se come pescado, se bebe sin medir y se nombra a los que faltan este año.",
            ),
            dia(
                Parrafo.PUEBLO to "Segundo día de las Apaturias, el del sacrificio. Se mata un buey por cuenta de la ciudad, para Zeus y Atenea, los de la hermandad, y el humo de la grasa llega hasta la calle.",
                Parrafo.GENTE to "{nombre} se lleva a casa su parte de carne envuelta en hojas de higuera. Es poca, pero es de buey, y en su mesa el buey se ve dos o tres veces al año.",
            ),
            dia(
                Parrafo.MANANA to "Tercer día de las Apaturias, el de los muchachos. Los padres presentan a los hijos nacidos en el año y juran ante todos que son suyos y de madre ateniense; a los mayores se les corta el pelo.",
                Parrafo.PENSAMIENTO to "A Cares lo presentó hace años, con un cordero que le costó la paga de un mes. Se acuerda del miedo que pasó: si alguno de la hermandad se llevaba la carne del altar, era que no le creía.",
            ),
        )),
        "funeral" to unDia(
            Parrafo.MANANA to "Hoy la ciudad entierra a los primeros muertos de la guerra. Sus huesos llevan dos días expuestos bajo una tienda, y cada familia ha podido dejarle al suyo lo que ha querido.",
            Parrafo.PUEBLO to "Diez carros llevan diez cajas de ciprés, una por tribu, y detrás va una camilla vacía, con su paño, por los que no se encontraron. El cortejo sale hacia el cementerio del barrio de los alfareros, el más hermoso de fuera de los muros.",
            Parrafo.GENTE to "Habla Pericles, subido a una tarima para que lo oigan todos. Casi no habla de los muertos: habla de la ciudad, de por qué merece que se muera por ella. Las mujeres lloran aparte. Los hombres miran al suelo.",
            Parrafo.PENSAMIENTO to "{nombre} conocía a dos de los que van en la caja de su tribu. Piensa que el discurso era muy bueno y que ellos no lo oyeron, y con ese pensamiento torcido se vuelve a casa por el camino largo.",
        ),
        "dionisias_pireo" to unDia(
            Parrafo.MANANA to "Hoy el Pireo celebra sus propias Dionisias, las del campo, que aquí son las del puerto. No tienen el lujo de las de Atenas, y a cambio no hay que subir la cuesta.",
            Parrafo.PUEBLO to "La procesión baja por la calle ancha con una tinaja de vino, una parra, un chivo y un falo de madera más alto que un hombre, en ese orden. Detrás van los niños, que son los que más gritan.",
            Parrafo.ANOCHECER to "Por la tarde hay concurso de saltar sobre un odre untado de aceite: gana el que aguante de pie. {nombre} dura lo que dura un golpe de remo, y Cares se ríe tanto que le da hipo.",
        ),
        "cojin" to unDia(
            Parrafo.MANANA to "Esta mañana Estratón saca del arcón un bulto envuelto en un paño y se lo pone delante sin decir nada. Es un cojín de remero, viejo, con la piel gastada hasta brillar.",
            Parrafo.GENTE to "«Con este remé en Salamina», dice su padre. «Ya no lo voy a necesitar». {nombre} tiene el suyo, más nuevo y más blando, pero entiende que no le están regalando un cojín.",
            Parrafo.PENSAMIENTO to "Echa la cuenta: cuarenta y nueve años tiene ese cuero. Ha aguantado a los persas, a tres naves y a su padre. Piensa que en primavera lo llevará debajo del suyo, donde nadie lo vea.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "remero", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        estados = listOf(Estado(MAR, "zarpa_flota", "vuelve_flota"), Estado(MAR, "zarpa_epidauro", "vuelve_epidauro")),
        especiales = ESPECIALES,
    )
}
