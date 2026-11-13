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
 * El cuento del joven naturalista que dio la vuelta al mundo en un bergantín de la marina, de 1832 a 1836. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su ficha se llama «Un joven naturalista», que en medio de una frase no funciona («a Un joven naturalista lo
 *   despierta…»). Estos textos no usan el hueco del nombre: dicen «el naturalista». Como su ficha, no nombran a Darwin,
 *   al capitán ni al barco.
 * - Come tres veces y trabaja dos tramos: esos textos valen a cualquiera de sus horas.
 * - Los textos de todos los días son los del bergantín. Sus días en tierra (estado «tierra») cambian de lugar con los
 *   años, y cada lugar tiene sus fechas: la casa de Río (temporada 1), la pampa con los gauchos (2) y los Andes (3 y 4).
 *   El mes enfermo en Valparaíso («enfermo», en medio de los Andes de la 3), las Galápagos («islas», 4) y su casa en
 *   Inglaterra («casa», 5) tienen los suyos. Cada uno, con su mañana, su pueblo, su anochecer y un texto para cada cosa
 *   que hace.
 * - El pueblo del bergantín lee el arco «cajones» (cuántos especímenes lleva: se vacía con cada envío).
 * - No repite lo que ya dice «Su vida»: el mareo de siempre, los escarabajos, fósiles y aves, el jugo de limón, los
 *   cajones que viajan a Inglaterra, las islas de las tortugas y los pinzones.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG5.kt`):
 * - Los tres fueguinos viajan a bordo hasta la misión (`mision`, temporada 2).
 * - El muchacho que le ayuda, desde `ayudante` (temporada 2).
 * - Enfermo de `enfermo` a `vuelve_andes` (temporada 3): es un estado propio, que en el calendario va antes que «tierra».
 * - Las fiestas que vuelven cada año (el año nuevo, siempre a bordo, y la Navidad, que en la temporada 5 pasa en casa)
 *   valen en todos esos sitios y no nombran a los fueguinos ni al muchacho.
 */
internal object PiezasNaturalista {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    // =====================================================================
    //  A bordo (los textos de todos los días)
    // =====================================================================

    private val MANANA = listOf(
        v("El naturalista se despierta en su hamaca, colgada en la cabina de popa encima de la mesa de las cartas, tan cerca del techo que no puede sentarse. Para salir tiene que hacer una maniobra que tardó semanas en aprender. El barco se mueve. Él también. Se marea antes de abrir los ojos."),
        v("Al naturalista lo despierta el ruido de la cubierta: los marineros baldeando, el agua que corre, las órdenes del oficial de guardia. Por la claraboya entra una luz gris. Se queda un rato quieto, esperando a saber si hoy el estómago le dejará trabajar. Hoy, quizá."),
        v("Lo primero que hace el naturalista cada mañana es subir a cubierta y mirar el mar, el cielo y, si la hay, la tierra. Lleva la libreta en el bolsillo. Apunta la hora, el viento, el color del agua, los pájaros que siguen al barco. Lo apunta todo, aunque no sepa todavía para qué."),
        v("El naturalista se despierta con la cabeza llena de nombres en latín, que se le mezclan en sueños con los de sus hermanas. Se ríe solo. Baja de la hamaca, se lava la cara con agua de mar y sube a cubierta, donde el capitán ya está, como siempre, con el catalejo."),
        v("Al naturalista lo despierta un marinero que asoma la cabeza por la puerta de la cabina: un pájaro que nadie conoce se ha posado en la jarcia. El naturalista sale de la hamaca de un salto, en camisa, con la red en la mano. El pájaro, claro, ya se ha ido."),
        v("El naturalista se despierta antes del alba y sube a cubierta a ver salir el sol del mar, que es lo único del mar que le gusta sin reservas. El agua se pone de colores. El barco cruje. Durante unos minutos se olvida del mareo. Luego se acuerda."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el barco el tiempo lo es todo: decide si se navega, si se baja a tierra y si se puede trabajar en la mesa."),
        v("El naturalista apunta el tiempo en su libreta, como el capitán en el diario de a bordo. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El capitán mira el barómetro cada hora, como otros miran el reloj, y casi siempre acierta."),
        v("{cielo} El naturalista ha aprendido a leer el cielo como un marinero, y el mar como un geólogo. {de_estacion}"),
        v("Desde la cubierta del bergantín el horizonte está en todas partes. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los marineros dicen que el mar tiene memoria, y que se acuerda de cada tormenta."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, y el sol hace brillar el agua y la tierra.",
            "Hace un día claro, de los que dejan ver hasta muy lejos.",
            "No hay una nube, y la luz es tan fuerte que hay que entornar los ojos.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, de un gris parejo.",
            "Pasan nubes altas que cambian de forma, y el naturalista las dibuja.",
            "Está nublado, y la luz es blanca y sin sombras.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y todo se moja: la ropa, los papeles, los especímenes mal tapados.",
            "Cae una lluvia fina que no para en todo el día.",
            "Llueve fuerte, y el agua corre en arroyos por donde puede.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, con un viento que aúlla y una lluvia que barre todo.",
            "El cielo se pone negro y el viento se vuelve loco.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento fuerte que no deja estar de pie sin agarrarse a algo.",
            "Hace mucho viento, y todo lo que no está atado vuela.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, y el mundo se acaba a cien pasos.",
            "La niebla se pega a todo, y huele a sal y a algas.",
        ),
        Clima.NIEVE to listOf(
            "Cae nieve, y el frío entra por todas partes.",
            "Nieva, y todo se queda blanco y callado.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.NINGUNA to listOf(
            "Al otro lado del mundo las estaciones van al revés, y el naturalista ya no sabe muy bien en qué mes vive.",
            "En el viaje las estaciones no se notan como en casa: se notan las latitudes.",
            "Cerca del ecuador siempre es verano; al sur, casi siempre invierno; el calendario no sirve de mucho.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora trabaja en la mesa de la cabina de popa, debajo de su hamaca, con la lupa, el escalpelo y los frascos: diseca un pájaro, lo rellena de estopa, le pone una etiqueta con la fecha, el lugar y un número. El número va también a la libreta. Sin número, un pájaro es solo un pájaro.", hacer = Hacer.TRABAJO),
        v("A esta hora echa por la popa una red de tela fina atada a un cabo y la arrastra un rato detrás del barco. Al sacarla, el fondo está lleno de animalitos que brillan, transparentes, que nadie ha dibujado nunca. Los mira con la lupa hasta que se marea. Luego los mira un poco más.", hacer = Hacer.TRABAJO),
        v("A esta hora pone en alcohol, en frascos de cristal, los animales blandos que encontró: medusas, gusanos de mar, peces pequeños, un pulpo. Escribe una etiqueta y la mete dentro, para que no se pierda. Los frascos se guardan en cajas con serrín. El alcohol se acaba siempre antes de lo previsto.", hacer = Hacer.TRABAJO),
        v("A esta hora seca plantas entre hojas de papel secante, en una prensa de madera que aprieta con correas, y cambia los papeles de las de ayer, que se enmohecen con la humedad del barco. Es un trabajo de paciencia. El naturalista no es paciente. Lo hace igual.", hacer = Hacer.TRABAJO),
        v("A esta hora pasa en limpio sus notas de campo, de la libreta sucia y mojada a un cuaderno grande, con buena letra: lo que vio, dónde, cuándo, qué cree que es. Muchas veces, al escribirlo, se da cuenta de que no sabe qué es. Lo escribe igual.", hacer = Hacer.TRABAJO),
        v("A esta hora está en cubierta con el martillo de geólogo, rompiendo las piedras que trajo de la última costa, buscando dentro conchas, cristales, fósiles. Los marineros se quejan de que les llena la cubierta de basura. El primer teniente lo llama el basurero. Él se ríe.", hacer = Hacer.TRABAJO),

        v("A esta hora come con el capitán, en una mesa pequeña y bien puesta, con mantel y cubiertos de plata aunque el barco se mueva. Se habla de ciencia, de política, de religión. El capitán tiene opiniones firmes sobre todo. El naturalista aprende a no llevarle la contraria antes del postre.", hacer = Hacer.COMER),
        v("A esta hora come lo que se puede comer cuando se está mareado: una galleta seca, unas pasas, un poco de agua. Se lo come tumbado en la hamaca, despacio, mirando el techo. El cirujano dice que se le pasará con el tiempo. Lleva meses esperando.", hacer = Hacer.COMER),
        v("A esta hora come carne en conserva, de las latas que el barco lleva por cientos, con galleta y un poco de vino. Y un trago de jugo de limón, que sabe a rayos. El naturalista se lo bebe de golpe, con los ojos cerrados, como un niño la medicina.", hacer = Hacer.COMER),

        v("A esta hora duerme en su hamaca, que se balancea con el barco y lo acuna como a un niño, que es lo único bueno del mar. Debajo, en la mesa, los frascos tintinean. Encima, a un palmo, el techo. Duerme mal, como siempre en el mar, y sueña con tierra firme.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, por fin, después de horas despierto escuchando crujir el barco y pensando en lo que vio. Sueña con escarabajos del tamaño de perros que le hablan en latín. Se despierta riéndose. El oficial de guardia, en cubierta, pregunta si pasa algo.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, rendido de un día en tierra, sin enterarse de los golpes del mar ni de la campana. Tiene las botas puestas y la libreta en el pecho. Nadie lo despierta. El capitán dice que a los naturalistas hay que dejarlos dormir donde caen.", hacer = Hacer.DORMIR),

        v("A esta hora escribe su diario a la luz de una vela, con letra pequeña para ahorrar papel: lo que vio hoy, lo que pensó, lo que no entiende. Escribe también una carta a sus hermanas, que no saldrá hasta que pasen por un puerto. A veces la carta y el diario dicen lo mismo.", hacer = Hacer.LIBRE),
        v("A esta hora lee en la cabina, con la vela atada a la mesa, un libro de geología de un tal Lyell que dice que la Tierra cambia despacio, con cosas pequeñas, durante tiempos enormes. Cada piedra que ve le da la razón. Lo subraya tanto que ya casi no se lee.", hacer = Hacer.LIBRE),
        v("A esta hora sube a cubierta a fumar un cigarro con los oficiales jóvenes, que lo llaman el filósofo, y a mirar las estrellas, que al sur son otras: la Cruz del Sur, las nubes de Magallanes, estrellas sin nombre en inglés. El oficial de guardia se las va señalando.", hacer = Hacer.LIBRE),
    )

    private val PUEBLO = listOf(
        v("{entorno} El bergantín es pequeño para todo lo que lleva: víveres para meses, botes, cañones, veintidós cronómetros en cajas acolchadas, una biblioteca y la gente apretada en cada rincón. Todo tiene su sitio, porque todo está pensado. Lo que no tiene sitio, no viaja."),
        v("En la cabina de popa, que comparte con dos oficiales, el naturalista tiene un cajón para sus libros, un rincón de la mesa para trabajar y dos ganchos para su hamaca. Ahí vive. Ahí trabaja. Ahí se marea. Es el cuarto más pequeño que ha tenido nunca, y el más lleno. {entorno}"),
        v("{entorno} En la cubierta del bergantín siempre hay algo: marineros remendando velas, el carpintero cepillando, los infantes de marina limpiando sus fusiles, un oficial con el sextante midiendo el sol. El barco no descansa nunca. Ni de noche."),
        v("El trabajo del bergantín no es llevar al naturalista: es medir costas, sondar puertos, dibujar cartas para que los barcos ingleses no se pierdan. El naturalista va de añadido. A veces se siente un pasajero que estorba. Los marineros le dicen que no, por si acaso. {entorno}"),
        v("{entorno} Alrededor del bergantín, el mar, siempre distinto: azul, verde, gris, lleno de vida o vacío, liso o en montañas. A veces lo siguen delfines; a veces, ballenas; a veces, pájaros que llevan días sin ver tierra. El naturalista los apunta a todos."),
        v("Cuando el bergantín está fondeado frente a una costa, los botes van y vienen todo el día con oficiales que miden, marineros que buscan agua y leña, y el naturalista, que vuelve siempre con las manos llenas y la ropa rota. {entorno}"),
    )

    /** Cuántos especímenes lleva (arco «cajones»): se vacía con cada envío. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("cajones", 0.0, 0.3, frases = listOf(
            "Sus cajones están casi vacíos desde el último envío, y la cabina parece grande.",
            "Hace poco que mandó un envío a Inglaterra, y tiene sitio otra vez para todo.",
        )),
        Entorno("cajones", 0.3, 0.7, frases = listOf(
            "Los cajones se van llenando de pájaros, piedras, frascos y huesos, y la cabina se estrecha.",
            "Ya hay cajones debajo de la mesa, encima del armario y detrás de la puerta.",
        )),
        Entorno("cajones", 0.7, 1.0, frases = listOf(
            "Los cajones están tan llenos que ya no caben en la cabina, y el primer teniente protesta cada día.",
            "No le cabe un cajón más: hay especímenes hasta debajo de la hamaca.",
        )),
    )

    private val GENTE = listOf(
        v("El capitán es un aristócrata joven, de mal genio y de gran talento, que trabaja más que nadie, duerme poco y tiene opiniones firmes sobre Dios y sobre todo lo demás. Quiso llevar a alguien con quien hablar en la mesa. A veces hablan. A veces discuten. Siempre se reconcilian."),
        v("El cirujano del barco es un hombre callado que sabe de plantas y de huesos y que le presta libros, frascos y alcohol cuando se le acaba el suyo. Lo cuida cuando se marea. Le dice que el mareo no mata a nadie. El naturalista le contesta que por ahora."),
        v("Sus hermanas le escriben cartas largas, llenas de noticias de casa: quién se casó, qué dijo su padre, cómo está el perro, qué se lee en Londres. Llegan todas juntas, meses después, en un paquete. El naturalista las lee por orden de fecha, despacio, para que duren."),
        v("Los oficiales jóvenes del bergantín lo llaman el filósofo, con cariño, y lo ayudan a cargar piedras sin entender para qué. Le traen lo que encuentran: un cangrejo, una pluma, un hueso de ballena. Él les da las gracias como si le hubieran traído oro."),
        v("Su padre no quería que hiciera este viaje: decía que era una tontería, que lo apartaría de la carrera de cura y de todo lo serio. Al final cedió, y paga sus gastos. El naturalista le escribe cartas muy serias, para que sepa que no es una tontería."),
        v("En el bergantín viajan tres fueguinos que el capitán se llevó a Inglaterra hace años y que ahora devuelve a su tierra, vestidos de ingleses: dos hombres y una muchacha. Jemmy, el más joven, habla inglés, lleva guantes y se peina con cuidado. Dice que los de otra tribu son muy malos. Se ríe al decirlo.", hasta = "mision"),
        v("El muchacho que le ayuda con los especímenes es un grumete del barco, despierto y callado, que en un mes aprendió a disecar pájaros mejor que él. El naturalista le paga de su bolsillo. Va con él a todas partes, con la escopeta y las bolsas. Sin él, ya no sabría trabajar.", desde = "ayudante"),
    )

    private val OBJETO = listOf(
        v("Hoy, entre una cosa y otra, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} El naturalista lo aprendió en el viaje, a fuerza de equivocarse."),
        v("En un barco cada cosa tiene su sitio, y lo que se suelta se rompe. {dato_objeto}"),
        v("{dato_objeto} En Cambridge nadie le habló de esas cosas. No hacía falta: allí no se movía el suelo."),
        v("{dato_objeto} Son cosas pequeñas, pero sin ellas no hay viaje."),
        v("Un naturalista viaja con poco y vuelve con mucho. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("hamaca", "la hamaca", "una hamaca", "La hamaca",
            "La hamaca de la marina es un rectángulo de lona colgado de dos ganchos por cuerdas, con una manta y un colchón fino; se balancea con el barco y por eso el que duerme en ella no se cae; por la mañana se enrolla y se ata, para que no estorbe."),
        ObjetoCuento("libreta", "la libreta", "una libreta de campo", "La libreta",
            "La libreta de campo es pequeña, de tapas de cuero, para llevar en el bolsillo y escribir con lápiz, de pie, a caballo o bajo la lluvia; lo que se apunta en ella se pasa luego en limpio a un cuaderno grande; si se pierde, se pierde un pedazo del viaje."),
        ObjetoCuento("lupa", "la lupa", "una lupa", "La lupa",
            "La lupa de bolsillo tiene una lente que agranda lo pequeño y se pliega en una funda de cuerno o de latón; con ella se ven las patas de un insecto, los granos de una roca, los pelos de una hoja; un naturalista la lleva colgada del cuello."),
        ObjetoCuento("red", "la red", "una red", "La red",
            "La red de los naturalistas es una bolsa de tela fina en un aro, con un mango largo; con ella se cazan mariposas en tierra, y en el mar, atada a un cabo y arrastrada detrás del barco, saca animalitos que no se ven a simple vista."),
        ObjetoCuento("frasco", "el frasco", "un frasco", "El frasco",
            "El frasco de cristal con tapa de corcho se llena de alcohol, o de aguardiente cuando no hay alcohol, para guardar los animales blandos; el alcohol se evapora, el corcho se pudre y el cristal se rompe, y por eso se envuelven en estopa y se meten en cajas."),
        ObjetoCuento("escopeta", "la escopeta", "una escopeta", "La escopeta",
            "La escopeta de caza dispara perdigones, que derriban un pájaro sin destrozarlo; un naturalista caza para coleccionar, no para comer, aunque a veces come lo que caza; con los pájaros pequeños hay que usar perdigón fino, o no queda nada que guardar."),
        ObjetoCuento("martillo", "el martillo", "un martillo de geólogo", "El martillo",
            "El martillo del geólogo tiene un lado plano y otro en punta, para romper las rocas y sacar lo que hay dentro; con él se ve si una piedra es de las que se hicieron con fuego o de las que se hicieron en el fondo del mar; un geólogo sin martillo solo mira."),
        ObjetoCuento("caja", "el cajón", "un cajón de especímenes", "El cajón",
            "Los cajones de especímenes son de madera, clavados y forrados de papel alquitranado para que no entre el agua, con un número pintado en la tapa; van llenos de pájaros, pieles, piedras, huesos y frascos, y viajan a Inglaterra en barcos de correo durante meses."),
        ObjetoCuento("brujula", "la brújula", "una brújula", "La brújula",
            "La brújula de bolsillo tiene una aguja que señala el norte y un círculo con los grados; con ella el naturalista apunta hacia dónde se inclinan las capas de roca y hacia dónde iba cuando encontró algo; en el barco, la grande está junto al timón."),
        ObjetoCuento("carta", "la carta", "una carta", "La carta",
            "Las cartas del viaje tardan meses en llegar, en barcos de correo que paran en cada puerto; se escriben largas, por los dos lados del papel y a veces cruzadas, para ahorrar; llegan muchas juntas, y otras no llegan nunca."),
        ObjetoCuento("limon", "el limón", "un limón", "El limón",
            "El jugo de limón se da cada día a la tripulación de los barcos de la marina, mezclado con agua y azúcar, contra el escorbuto, la enfermedad de los marinos que pasan meses sin fruta fresca; nadie sabe bien por qué funciona; funciona."),
        ObjetoCuento("galleta", "la galleta", "una galleta de barco", "La galleta",
            "La galleta de barco es pan cocido dos veces, sin sal, dura como una teja, que se guarda en barriles y dura meses; para el que está mareado es lo único que el estómago acepta; para los demás, lo que se come cuando no hay otra cosa."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} El naturalista piensa en esas cosas en la mesa, cuando se habla de ellas, y luego se le olvidan delante de un escarabajo. A sus {edad} años le interesa más un escarabajo que un ministro. Su padre dice que eso se cura con la edad. Él espera que no."),
        v("Piensa a veces en Cambridge, en las tardes cazando escarabajos con sus amigos, en el profesor que le enseñó a mirar las plantas y que le consiguió este viaje. Le escribe cartas largas, con dibujos. Le manda cajones. Quiere que esté orgulloso de él."),
        v("{mundo} El naturalista piensa en la edad de la Tierra, que el libro de geología dice que es enorme, y en las conchas que se encuentran en lo alto de los acantilados. Piensa que el mar estuvo allí. Piensa que la tierra se mueve, despacio. Lo piensa en voz baja."),
        v("{mundo} A veces, en cubierta, de noche, el naturalista piensa en lo lejos que está de casa: meses de mar en cualquier dirección. Le da miedo y le gusta. Piensa que nunca volverá a ser el mismo. Piensa que a lo mejor eso es lo que vino a buscar."),
        v("Piensa en lo que será al volver: cura de pueblo, como quiere su padre, con una parroquia, un jardín y tiempo para los escarabajos. Le parece bien. Le parece que tiene tiempo para decidir. Le parece, cada vez más, que no va a ser cura."),
        v("{mundo} A sus {edad} años, el naturalista ha visto más mundo que todos sus profesores juntos, y se da cuenta de que sabe mucho menos que ellos. Piensa que eso es bueno. Piensa que los que saben mucho de libros deberían salir más."),
    )

    private val MUNDOS = mapOf(
        "brasil" to listOf(
            "En la mesa del capitán se habla de la reforma del Parlamento en Inglaterra, que unos aplauden y otros temen.",
            "Se habla del Brasil, donde reina un emperador niño y mandan otros por él.",
            "Se habla de los esclavos de las colonias, que en Inglaterra muchos quieren ya libres.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde el naturalista sube a cubierta a ver ponerse el sol en el mar, que cerca del ecuador es rápido y violento: un momento todo es oro y rojo, y al siguiente es de noche. Luego, alrededor del barco, se encienden lucecitas: el mar brilla de noche, y nadie sabe bien por qué."),
        v("Anochece, y en la cabina se encienden las velas, y el naturalista se sienta a escribir mientras el barco cruje y el mar golpea el casco. Por la claraboya se ven las estrellas, que pasan de un lado a otro con cada balanceo. Escribe hasta que se le cierran los ojos."),
        v("Al caer la tarde, en la proa, los marineros cantan y bailan un rato al son de un violín, como se hace en los barcos de la marina. El naturalista los mira desde la borda. A veces baila. Lo hace mal. Lo aplauden igual."),
        v("De noche, en el mar, el bergantín es una cáscara de luz en la oscuridad, con un farol en la popa. El oficial de guardia pasea. El timonel canta bajito. El naturalista, en su hamaca, oye todo eso y el mar, y se siente muy pequeño."),
        v("La última luz del día se queda en las velas altas cuando la cubierta ya está a oscuras. El naturalista la mira desde abajo, con la cabeza echada atrás, hasta que se apaga. Luego busca la Cruz del Sur, que en su casa no se ve nunca. Ahí está."),
        v("Anochece, y el capitán y el naturalista pasean por la cubierta, de proa a popa y de popa a proa, hablando de todo, como dos presos en un patio. A veces se ríen. A veces discuten. Luego se dan las buenas noches y cada uno se va a su cabina."),
    )

    // =====================================================================
    //  En tierra (estado «tierra»): cada lugar con sus fechas
    // =====================================================================

    private fun ti(texto: String, hacer: Hacer?, desde: String?, hasta: String?) = v(texto, estado = "tierra", hacer = hacer, desde = desde, hasta = hasta)

    /** La casa de Botafogo, junto a Río (temporada 1). */
    private fun ri(texto: String, hacer: Hacer? = null) = ti(texto, hacer, null, "vuelve_bordo")

    private val RIO = listOf(
        ri("El naturalista se despierta en la casa que alquiló en Botafogo, una bahía junto a Río, con la ventana abierta a un jardín de plátanos y naranjos y, detrás, la montaña cubierta de selva. Lo despiertan los pájaros, cientos, que no conoce. Por primera vez en meses, el suelo no se mueve."),
        ri("Al naturalista lo despierta un mono que grita en el árbol de la ventana, y luego los vendedores que pasan por el camino con cestos en la cabeza. Se lava con agua fresca de la fuente del patio. El aire huele a flores, a fruta podrida y a tierra mojada. Hoy subirá al bosque."),
        ri("El naturalista se despierta antes del alba, cuando la selva todavía está fresca, y sale al jardín a mirar cómo la luz baja por la montaña. A esa hora vuelan unas mariposas grandes y azules que brillan como metal. Intenta cazar una. No lo consigue. Es el mejor momento del día."),
    )

    private val RIO_TRABAJO = listOf(
        ri("A esta hora sube con la red y la escopeta por los senderos de la selva, detrás de la casa, y se pierde en un mundo verde, húmedo y lleno de ruido: árboles altísimos cubiertos de plantas, lianas, flores en las ramas, pájaros, hormigas que marchan como ejércitos. No sabe adónde mirar primero.", Hacer.TRABAJO),
        ri("A esta hora caza escarabajos debajo de las piedras y de las cortezas, y los mete en frascos con alcohol, uno por uno, con unas pinzas. En una sola mañana encuentra más clases que en un año en Inglaterra. Le tiemblan las manos. Lo apunta todo.", Hacer.TRABAJO),
        ri("A esta hora, en la galería de la casa, diseca los pájaros que cazó antes de que el calor los pudra: los abre, los vacía, los rellena, los cose, les pone la etiqueta. Las hormigas acechan. Una araña enorme lo mira desde el techo. Él la mira a ella. Luego la caza también.", Hacer.TRABAJO),
        ri("A esta hora come en la galería de la casa de Botafogo fruta que no sabe nombrar, pan, carne con frijoles negros y café, mucho café, el mejor que ha probado nunca. Se ríe solo de lo bien que se come en tierra. Sus hermanas no se lo van a creer.", Hacer.COMER),
        ri("A esta hora come en una venta del camino, con arrieros y esclavos que descansan a la sombra: arroz, frijoles, carne seca y harina de mandioca por encima de todo. Lo miran con curiosidad: un inglés con una red de mariposas. Él los mira a ellos.", Hacer.COMER),
        ri("A esta hora duerme en una cama de verdad, en la casa de Botafogo, con la ventana abierta y una tela contra los mosquitos que no sirve de mucho. Los grillos y las ranas hacen un ruido de feria. Duerme como no dormía desde Inglaterra.", Hacer.DORMIR),
        ri("A esta hora duerme mientras afuera llueve sobre la selva una lluvia de trópico, enorme, que suena como un río sobre el tejado. Las luciérnagas se refugian en la galería. Una se le posa en la mano. Sigue durmiendo.", Hacer.DORMIR),
        ri("A esta hora escribe en la mesa de la galería, a la luz de una lámpara rodeada de insectos, todo lo que vio hoy en la selva. Le faltan palabras. Escribe que es como estar en un invernadero enorme hecho por Dios. Lo tacha. Lo vuelve a escribir.", Hacer.LIBRE),
        ri("A esta hora pasea por la playa de la bahía con el dibujante del barco, que vive en la misma casa, y mira la gran piedra del Pan de Azúcar recortada contra el cielo. Hablan de pintura y de piedras. Los cangrejos corren por la arena. Se está bien.", Hacer.LIBRE),
    )

    private val RIO_PUEBLO = listOf(
        ri("La casa de Botafogo es pequeña y blanca, con tejado rojo, galería y jardín, entre la playa y la selva. Desde ella se ven la bahía, el Pan de Azúcar y, cuando está, el bergantín fondeado lejos. Cerca viven otros ingleses, mercaderes de café."),
        ri("Río de Janeiro es una ciudad grande, ruidosa y hermosa, de iglesias, de calles estrechas, de mercados, de esclavos que cargan todo lo que se carga, y de selva que empieza donde se acaba la última casa. El naturalista no se acostumbra a los esclavos. No quiere acostumbrarse."),
        ri("Detrás de la casa empieza la selva, que sube por las montañas hasta las nubes: árboles enormes, palmeras, helechos más altos que un hombre, flores rojas, lianas. Por el bosque bajan arroyos de agua clara. Todo está vivo, y todo se come a todo."),
    )

    private val RIO_ANOCHECER = listOf(
        ri("Al caer la tarde, en Botafogo, el cielo se pone rojo sobre la montaña y la bahía se queda quieta como un espejo. Las ranas empiezan a cantar todas a la vez. El naturalista se sienta en la galería con su diario. Los murciélagos salen del tejado."),
        ri("Anochece en la selva de golpe, como en todo el trópico, y el bosque se llena de ruidos que de día no estaban: insectos, ranas, pájaros nocturnos, algo grande que se mueve. El naturalista baja a la casa con el farol, despacio, oyéndolo todo."),
        ri("De noche, en el jardín de Botafogo, vuelan las luciérnagas entre los naranjos, cientos, encendiéndose y apagándose. El naturalista coge una y la mira con la lupa a la luz de la vela. Apunta que la luz sale de la cola. Apunta que es lo más bonito que ha visto."),
    )

    private val RIO_TIEMPO = listOf(
        ri("{cielo} {de_estacion} En Río el tiempo cambia en una hora: sol, nube, aguacero, sol."),
        ri("Desde la galería de Botafogo se ve el cielo sobre la bahía. {cielo} {de_estacion}"),
    )

    /** La pampa, con los gauchos (temporada 2). */
    private fun pa(texto: String, hacer: Hacer? = null) = ti(texto, hacer, "sale_pampa", "vuelve_pampa")

    private val PAMPA = listOf(
        pa("El naturalista se despierta en el suelo, envuelto en el poncho, con la silla de montar de almohada, en medio de la pampa, que es lisa como el mar y no tiene un árbol. Los gauchos ya han encendido fuego con huesos y cardos, y huele a carne asada. El cielo es enorme."),
        pa("Al naturalista lo despierta el frío, que en la pampa muerde al amanecer, y los caballos, que resoplan atados cerca. Se levanta con todo el cuerpo dolorido de la silla. Un gaucho le pasa un mate caliente sin decir nada. Él no da las gracias hasta el último: si las da antes, no le sirven más."),
        pa("El naturalista se despierta en una posta del camino, una choza de barro con techo de paja y perros flacos, donde se cambian los caballos. El dueño le ofrece carne y mate. Afuera, el horizonte, liso, en todas las direcciones, y un ñandú que corre."),
    )

    private val PAMPA_TRABAJO = listOf(
        pa("A esta hora cabalga por la pampa con los gauchos, horas y horas, a un galope corto que los caballos aguantan todo el día, sin ver otra cosa que hierba, cielo y algún ñandú. Los gauchos le enseñan a tirar las boleadoras. Se le enredan en las patas de su propio caballo. Los gauchos se ríen hasta caerse.", Hacer.TRABAJO),
        pa("A esta hora busca huesos en las barrancas de un río de la pampa, con el martillo y una navaja, tumbado de lado en la tierra, sacándolos poco a poco: un diente enorme, un pedazo de caparazón, una mandíbula. No sabe de qué son. Sabe que son de algo que ya no existe.", Hacer.TRABAJO),
        pa("A esta hora caza con los gauchos: ñandúes con las boleadoras, armadillos que se meten en la tierra, una vizcacha. Él dispara a los pájaros pequeños que nadie quiere y los mete en la bolsa. Los gauchos le preguntan para qué. Él les dice que para saber. No lo entienden.", Hacer.TRABAJO),
        pa("A esta hora come lo que comen los gauchos: carne de vaca asada en el fuego, sin pan, sin sal, sin verdura, y mate amargo. Solo carne. Los gauchos dicen que la carne es todo lo que un hombre necesita. El naturalista lleva semanas así y nunca se ha sentido mejor.", Hacer.COMER),
        pa("A esta hora come armadillo asado en su concha, que es lo mejor que ha probado en la pampa, y un trozo de ñandú, que no. Los gauchos se ríen de su cara. El fuego es de huesos y de cardo seco, porque en la pampa no hay leña. Huele raro. Sabe bien.", Hacer.COMER),
        pa("A esta hora duerme al raso, envuelto en el poncho, sobre las pieles de la silla, con el cielo entero encima, sin un árbol que lo tape. Las estrellas bajan hasta el suelo. Los perros vigilan. Los gauchos roncan. El naturalista no ha dormido mejor en su vida.", Hacer.DORMIR),
        pa("A esta hora duerme en el suelo de una posta, entre los arreos y los perros, con unas pulgas que no lo dejan en paz. Afuera el viento de la pampa sopla sin nada que lo pare. Sueña que sigue cabalgando.", Hacer.DORMIR),
        pa("A esta hora escribe su diario junto al fuego, con la libreta en las rodillas, mientras los gauchos cantan con una guitarra canciones tristes sobre caballos y mujeres. Escribe que son los hombres más libres que ha visto. Luego escribe que también los más crueles. Las dos cosas son verdad.", Hacer.LIBRE),
        pa("A esta hora escucha a los gauchos contar historias de la guerra contra los indios, que es cruel de los dos lados, y de un general que los manda y al que todos temen. El naturalista apunta los nombres de los ríos, de los fortines, de los muertos. Le cuesta dormirse después.", Hacer.LIBRE),
    )

    private val PAMPA_PUEBLO = listOf(
        pa("La pampa es una llanura de hierba sin fin, sin un árbol, sin una piedra, donde el horizonte está siempre a la misma distancia aunque se cabalgue todo el día. Por ella corren vacas, caballos, ñandúes y venados. Y gauchos, que son de ella como los ñandúes."),
        pa("Las postas del camino son chozas de barro con un corral, a una jornada unas de otras, donde se cambian los caballos y se come carne. En algunas hay soldados del general, porque hay guerra con los indios. En otras no hay nadie: los indios pasaron antes."),
        pa("Por la pampa se viaja con una tropilla de caballos sueltos, que se cambian cuando el que se monta se cansa. Un gaucho no camina nunca: si tiene que ir a la puerta de enfrente, va a caballo. El naturalista ya no se acuerda de cuándo caminó por última vez."),
    )

    private val PAMPA_ANOCHECER = listOf(
        pa("Al caer la tarde en la pampa el sol se pone en el horizonte como en el mar, enorme y rojo, y la hierba se vuelve de oro. Los gauchos paran, encienden fuego, ponen la carne en el asador. El naturalista desensilla su caballo y se tumba de espaldas. No hay nada más grande que este cielo."),
        pa("Anochece, y en la pampa se oyen los teros, que gritan cuando alguien se acerca, y a lo lejos los perros de una estancia. Los gauchos hablan bajo junto al fuego. Uno vigila por si vienen los indios. El naturalista mira las estrellas y cuenta las que no conocía."),
        pa("De noche hace frío en la pampa, y los gauchos se acercan al fuego y se pasan el mate en silencio. Uno toca la guitarra. El naturalista, envuelto en su poncho, piensa que su padre no lo reconocería. Se ríe solo. El gaucho de al lado le pregunta de qué."),
    )

    private val PAMPA_TIEMPO = listOf(
        pa("{cielo} {de_estacion} En la pampa el tiempo se ve venir desde un día antes: no hay nada que lo tape."),
        pa("El cielo de la pampa es más grande que ningún otro. {cielo} {de_estacion}"),
    )

    /** Los Andes (temporadas 3 y 4). El mes enfermo de la 3 es otro estado, «enfermo», que manda sobre este. */
    private fun an(texto: String, hacer: Hacer? = null) = ti(texto, hacer, "andes", null)

    private val ANDES = listOf(
        an("El naturalista se despierta en una cabaña de pastores en la montaña, con el frío metido en los huesos y la luz del amanecer en las cumbres nevadas, rosas, altísimas. Los arrieros cargan las mulas. El aire es tan fino que cuesta respirar. Todo es piedra, nieve y cielo."),
        an("Al naturalista lo despierta el arriero, que ya tiene el fuego encendido y el agua para el mate. Duermen al raso, junto a un arroyo helado, entre piedras enormes. Las mulas comen la poca hierba que hay. Arriba, un cóndor da vueltas sin mover las alas."),
        an("El naturalista se despierta con dolor de cabeza, por la altura, que los arrieros llaman la puna y curan comiendo cebolla. Come cebolla. Le sigue doliendo la cabeza. Sale y ve las montañas, y se le olvida el dolor."),
    )

    private val ANDES_TRABAJO = listOf(
        an("A esta hora sube con las mulas por un sendero de piedra al borde de un precipicio, y se para cada poco a romper una roca con el martillo. Las capas de piedra están dobladas como mantas, y en lo alto hay conchas de mar. Conchas de mar, en la cumbre de los Andes. Las mete en la bolsa con manos que tiemblan.", Hacer.TRABAJO),
        an("A esta hora dibuja en la libreta las capas de roca de una ladera, de colores distintos, una encima de otra, como las páginas de un libro enorme. Piensa que cada capa tardó miles de años en hacerse. Piensa que la montaña es un libro de historia. El arriero lo espera, fumando.", Hacer.TRABAJO),
        an("A esta hora cabalga por un valle entre montañas peladas, de rocas rojas, amarillas y moradas, sin un árbol, con un río de agua gris que baja de la nieve. Los arrieros cantan. Las mulas buscan el camino solas. El naturalista va callado, con la cabeza llena de piedras.", Hacer.TRABAJO),
        an("A esta hora come charqui, carne seca que los arrieros llevan en las alforjas, cocida en agua con un poco de harina, y bebe mate. En la altura el agua hierve antes y las papas no se cuecen nunca, dicen los arrieros. Lo ha comprobado: no se cuecen.", Hacer.COMER),
        an("A esta hora come en una choza de pastores queso de cabra, pan duro y un poco de vino que le ofrecen con desconfianza. Les paga bien. Le preguntan si busca oro, como todos los ingleses. Él les enseña una concha. No lo entienden.", Hacer.COMER),
        an("A esta hora duerme al raso, envuelto en todas sus mantas, junto a una roca que todavía guarda el calor del sol, a una altura donde el agua de la cantimplora amanece helada. Las estrellas son tantas que el cielo parece blanco. Le cuesta respirar. Duerme a ratos.", Hacer.DORMIR),
        an("A esta hora duerme en una cabaña de piedra de los pastores, con el humo del fuego que no sale y los perros que se le acuestan encima para calentarse. Huele a cabra. No le importa. Fuera, el viento de la montaña silba entre las piedras.", Hacer.DORMIR),
        an("A esta hora escribe en el diario junto al fuego, con los dedos entumecidos, todo lo que vio: las conchas en la cumbre, las capas de roca, los colores de la montaña. Escribe que la tierra se levantó, despacio, desde el fondo del mar. Escribe que nadie se lo va a creer.", Hacer.LIBRE),
        an("A esta hora mira las montañas desde la puerta de la cabaña, a la luz de la luna, blancas y enormes, y piensa que antes estuvieron debajo del mar. Le da vértigo. Los arrieros duermen. Él no puede.", Hacer.LIBRE),
    )

    private val ANDES_PUEBLO = listOf(
        an("Los Andes son una pared de montañas que cruza el continente de norte a sur, con cumbres nevadas que no se acaban, valles profundos y pasos por donde solo van las mulas y los arrieros. En lo alto no vive nadie. Solo los cóndores."),
        an("Los arrieros que lo acompañan conocen cada paso de la montaña, cada refugio, cada sitio donde hay agua y hierba para las mulas. Son callados, duros, y se ríen de él cuando se para a romper piedras. Le llaman el loco de las piedras, con cariño."),
        an("En los valles de la montaña hay minas de cobre y de plata, donde trabajan hombres que suben cargas enormes por escaleras de palo, desde el fondo, todo el día. El naturalista los ve salir con la piel brillante de sudor. Le parecen los hombres más fuertes del mundo."),
    )

    private val ANDES_ANOCHECER = listOf(
        an("Al caer la tarde en la montaña, las cumbres se ponen rosas y luego moradas, y los valles se llenan de sombra azul. Los arrieros paran junto a un arroyo, descargan las mulas, encienden fuego con arbustos secos. El naturalista se sienta en una piedra y no puede dejar de mirar."),
        an("Anochece en los Andes, y el frío baja de golpe de la nieve. Los arrieros se envuelven en sus ponchos y se acercan al fuego. El naturalista se pone toda la ropa que tiene. Un cóndor pasa, muy alto, todavía con sol en las alas."),
        an("De noche, en la montaña, el silencio es tan grande que se oye el agua del arroyo a cien pasos y la respiración de las mulas. El naturalista mira las estrellas, más que nunca, y piensa que el mundo es mucho más viejo de lo que le enseñaron."),
    )

    private val ANDES_TIEMPO = listOf(
        an("{cielo} {de_estacion} En la montaña el tiempo cambia en minutos: los arrieros miran las cumbres para saber qué viene."),
        an("El aire de los Andes es tan limpio que se ven montañas a muchos días de camino. {cielo} {de_estacion}"),
    )

    /** El mes enfermo en Valparaíso, en medio de los Andes de la temporada 3 (estado «enfermo»). */
    private fun en(texto: String, hacer: Hacer? = null) = v(texto, estado = "enfermo", hacer = hacer)

    private val ENFERMO = listOf(
        en("El naturalista se despierta en la cama, en la casa de un amigo inglés de Valparaíso, con fiebre otra vez y el cuerpo deshecho. Por la ventana se ven el puerto y, lejos, la cordillera nevada. Volvió de la montaña tan enfermo que casi lo traen en brazos. El médico dice reposo."),
        en("Al naturalista lo despierta la criada de la casa con un caldo y una medicina que sabe a tierra. Le cuesta levantar la cabeza. Tiene los cajones de la montaña en un rincón del cuarto, sin abrir. Los mira. No tiene fuerzas ni para eso."),
        en("El naturalista se despierta sin fiebre por primera vez en días, y se queda quieto, sin creérselo, mirando el techo. Afuera cantan los pájaros del puerto. Pide papel. Se lo dan. Escribe la fecha. Luego se vuelve a dormir."),
    )

    private val ENFERMO_TRABAJO = listOf(
        en("A esta hora no puede trabajar: está en la cama, débil, con fiebre. Le pide a la criada que le acerque los cajones de la montaña, y mira las conchas, una por una, sin levantarse. Las vuelve a guardar. Mañana, quizá, podrá escribir.", Hacer.TRABAJO),
        en("A esta hora intenta escribir en la cama, con la libreta apoyada en las rodillas, lo que vio en la montaña antes de enfermar, para que no se le olvide. Escribe tres líneas y se duerme. Al despertar, las tres líneas no se entienden.", Hacer.TRABAJO),
        en("A esta hora come lo que le dejan comer: caldo, pan blando, un poco de vino con agua. Le cuesta. Tiene la boca amarga de la medicina. La dueña de la casa se sienta a su lado y le cuenta chismes de Valparaíso para que coma. Funciona un poco.", Hacer.COMER),
        en("A esta hora duerme con fiebre, en sueños revueltos de montañas, conchas y mulas que se caen por los precipicios. Se despierta empapado. Bebe agua. Vuelve a dormir. Afuera, en el puerto, suenan las campanas de los barcos.", Hacer.DORMIR),
        en("A esta hora, en la cama, lee las cartas de sus hermanas que le esperaban en Valparaíso, y las vuelve a leer, y se las sabe de memoria. Una dice que su padre pregunta por él todos los días. Se gira hacia la pared para que no le vean la cara.", Hacer.LIBRE),
    )

    private val ENFERMO_PUEBLO = listOf(
        en("Valparaíso es un puerto de casas blancas subidas a los cerros, unas encima de otras, con escaleras por calles y el mar delante, lleno de barcos de todas las naciones. Desde la cama, por la ventana, el naturalista ve un pedazo. Le parece la ciudad más bonita del mundo. Tiene fiebre."),
    )

    private val ENFERMO_ANOCHECER = listOf(
        en("Al caer la tarde la luz del puerto entra por la ventana del cuarto, naranja, y el naturalista la mira moverse por la pared hasta que se apaga. En la calle, alguien canta. Se oyen los cascos de los caballos. Mañana estará mejor, se dice. Lo dice cada tarde."),
        en("Anochece, y la dueña de la casa le trae una vela y un vaso de leche caliente. El naturalista le da las gracias en un español que mejoró mucho en la montaña. Ella se ríe de su acento. Él también."),
        en("De noche, en la casa de Valparaíso, el naturalista oye el mar desde la cama y piensa en el bergantín, fondeado en la bahía, sin él. El capitán vino a verlo dos veces. La segunda, le trajo un libro. No puede leerlo. Lo tiene en el pecho, cerrado."),
    )

    private val ENFERMO_TIEMPO = listOf(
        en("Desde la cama, por la ventana, el naturalista ve un trozo de cielo sobre el puerto. {cielo} {de_estacion}"),
    )

    /** Las islas Galápagos (temporada 4, estado «islas»). */
    private fun ga(texto: String, hacer: Hacer? = null) = v(texto, estado = "islas", hacer = hacer)

    private val ISLAS = listOf(
        ga("El naturalista se despierta en la playa de una isla negra, de lava, donde acampó con el muchacho: la tienda, unas mantas, un barril de agua. Ya hace calor al amanecer. Alrededor, la lava retorcida, unos cactus y una tortuga enorme que pasa cerca sin mirarlos, despacio, como un carro."),
        ga("Al naturalista lo despiertan los pájaros, que en estas islas no tienen miedo: un pinzón se le posa en la bota, otro en el borde de la taza. Los puede coger con la mano. Nadie les ha enseñado a temer a los hombres. Él piensa que pronto aprenderán."),
        ga("El naturalista se despierta en el bergantín, fondeado frente a una isla de lava negra que humea por algunas grietas, con olor a azufre. Los marineros ya han bajado a cazar tortugas para la despensa. Él baja con la red. Las islas parecen de otro mundo."),
    )

    private val ISLAS_TRABAJO = listOf(
        ga("A esta hora camina por la lava de la isla, que corta las botas, cazando pájaros pequeños con la escopeta o con la mano, y los mete en bolsas con una etiqueta: la isla, la fecha. A veces se le olvida apuntar la isla. Más adelante, eso le pesará.", Hacer.TRABAJO),
        ga("A esta hora sigue a una tortuga gigante por un sendero que las tortugas han hecho en siglos, hacia el agua del interior de la isla, para ver cuánto tarda. Apunta los pasos. La tortuga no tiene prisa. Él sí. Al final se sienta en una piedra a esperarla.", Hacer.TRABAJO),
        ga("A esta hora mira las iguanas de la costa, negras, feas como dragones pequeños, que se meten en el mar a comer algas y vuelven a calentarse al sol en las rocas. Abre una para ver qué comió. Algas. Apunta que es el único lagarto que conoce que vive del mar.", Hacer.TRABAJO),
        ga("A esta hora come carne de tortuga asada en su concha, que es buena, y bebe agua del barril, que es mala, a la sombra de la tienda. Los marineros dicen que en estas islas se puede vivir de tortuga para siempre. Él piensa que por eso ya quedan menos.", Hacer.COMER),
        ga("A esta hora come con el muchacho galleta y carne de tortuga fría, sentados en la lava, mirando el mar. Los pinzones vienen a picar las migas a sus pies. Les da migas. Luego apunta qué pinzones comieron qué migas.", Hacer.COMER),
        ga("A esta hora duerme en la tienda, en la playa, con el ruido del mar y de los leones marinos, que gruñen y resoplan toda la noche como viejos. Hace calor. El suelo de lava es duro. Duerme poco. Sueña con pájaros.", Hacer.DORMIR),
        ga("A esta hora duerme a bordo, en su hamaca, con el bergantín fondeado y quieto, y por la claraboya entra el olor de las islas: azufre, algas, tortuga asada. Por una vez no se marea. Dice que es lo único que le gusta de estas islas. No es verdad.", Hacer.DORMIR),
        ga("A esta hora escribe en el diario, a la luz de la vela, que las tortugas de cada isla son un poco distintas, y los pájaros también, y que no sabe por qué. Escribe que, si es así, habría que pensarlo mejor. No escribe qué. No lo sabe todavía.", Hacer.LIBRE),
        ga("A esta hora se sienta en la lava a ver ponerse el sol detrás de otra isla, con un pinzón en la rodilla. Piensa que estas islas son nuevas, hechas de lava hace poco, y que todo lo que vive en ellas llegó de algún sitio. Se pregunta de dónde. Y cómo cambió.", Hacer.LIBRE),
    )

    private val ISLAS_PUEBLO = listOf(
        ga("Las islas Galápagos son volcanes negros en medio del océano, a muchos días de cualquier tierra, de lava retorcida, cactus, arbustos grises y playas de arena blanca. Viven en ellas tortugas enormes, iguanas, leones marinos y pájaros sin miedo. Y casi nadie más."),
        ga("En una de las islas hay una colonia de presos de la república del Ecuador, con un gobernador inglés, casas de madera, huertos y cerdos. Los presos cazan tortugas y las salan. El gobernador dice que sabe de qué isla es cada tortuga por su concha."),
        ga("Cada isla es un poco distinta: una más verde, otra más seca, otra con más volcanes. En las playas, los leones marinos duermen amontonados. En las rocas, las iguanas negras toman el sol. En el agua, tortugas de mar, peces de colores, tiburones. Todo cerca. Todo raro."),
    )

    private val ISLAS_ANOCHECER = listOf(
        ga("Al caer la tarde en las islas, el sol se pone en el mar con colores violentos, y las iguanas se quedan quietas en las rocas, mirando al oeste como si rezaran. El naturalista vuelve al campamento con las bolsas llenas y las botas rotas."),
        ga("Anochece, y los leones marinos de la playa se callan un momento, y luego empiezan otra vez. El aire huele a sal y a azufre. El naturalista enciende la vela y pone las etiquetas del día. En dos bolsas le falta la isla. Se rasca la cabeza."),
        ga("De noche, en la isla, se oye a las tortugas moverse entre los arbustos, despacio, como piedras que caminan. Arriba, todas las estrellas del mundo. El naturalista no puede dormir. Piensa en pájaros."),
    )

    private val ISLAS_TIEMPO = listOf(
        ga("{cielo} {de_estacion} Las islas están en el ecuador, y sin embargo el mar que las rodea está frío."),
        ga("Sobre las islas negras el cielo parece más grande. {cielo} {de_estacion}"),
    )

    /** De vuelta en Inglaterra (temporada 5, estado «casa»). */
    private fun ca(texto: String, hacer: Hacer? = null) = v(texto, estado = "casa", hacer = hacer)

    private val CASA = listOf(
        ca("El naturalista se despierta en su cuarto de siempre, en la casa de su padre, en una cama que no se mueve, y tarda un momento en entender por qué todo está tan quieto. Fuera, el jardín, el río, los árboles de otoño. Por la escalera sube el olor del desayuno. Está en casa."),
        ca("Al naturalista lo despierta su hermana pequeña llamando a la puerta, como cuando eran niños, para preguntar si ya está despierto. Lo está. Hacía cinco años que nadie lo despertaba así. Se queda un rato más en la cama, solo por el gusto de poder."),
        ca("El naturalista se despierta y lo primero que hace, todavía, es esperar el balanceo del barco. No llega. Se ríe. Se levanta, se viste con ropa de tierra, que le queda ancha, y baja al jardín a mirar los pájaros ingleses, que conoce todos por su nombre."),
    )

    private val CASA_TRABAJO = listOf(
        ca("A esta hora abre en el salón uno de los cajones que vinieron del viaje, con su padre y sus hermanas mirando: pájaros, piedras, huesos, frascos. Les cuenta de dónde es cada cosa. Su padre escucha sin decir nada. Al final le dice que le ha cambiado la forma de la cabeza.", Hacer.TRABAJO),
        ca("A esta hora escribe cartas a los sabios de Londres y de Cambridge a los que quiere enseñar sus colecciones: quién podrá estudiar los pájaros, quién los fósiles, quién las plantas. Son muchos cajones. Necesita ayuda. Ya no es un estudiante que caza escarabajos.", Hacer.TRABAJO),
        ca("A esta hora pasa en limpio su diario del viaje, cuaderno por cuaderno, en la mesa de la biblioteca de su padre, para hacer un libro. Hay que cortar mucho. No quiere cortar nada. Cada página es un sitio y una tarde. Su hermana le dice que corte los mareos.", Hacer.TRABAJO),
        ca("A esta hora come en el comedor de su padre, con mantel blanco y cubiertos de plata, cordero con verduras del huerto, pan blanco y vino de verdad. Todos lo miran comer. Come como un gaucho, con prisa y mucho. Sus hermanas se ríen. Él se ríe con ellas.", Hacer.COMER),
        ca("A esta hora toma té en el salón con sus hermanas, con pasteles y mermelada, y les cuenta cosas del viaje: los fueguinos, el terremoto, las tortugas. Ellas le preguntan si pasó miedo. Les dice que sí, muchas veces. Ellas se quedan calladas.", Hacer.COMER),
        ca("A esta hora duerme en una cama ancha y quieta, con sábanas limpias y fuego en la chimenea, y le cuesta dormirse de tanta quietud. A veces se despierta creyendo que está en la hamaca y que el barco se hunde. Luego oye el reloj del pasillo y se vuelve a dormir.", Hacer.DORMIR),
        ca("A esta hora duerme hondo, como no dormía en cinco años, sin mareo, sin guardias, sin campana. Sueña con islas negras y tortugas. Al despertar no sabe si soñó con las islas o si las islas fueron un sueño.", Hacer.DORMIR),
        ca("A esta hora pasea por el jardín de su padre, junto al río, despacio, mirando los árboles que conoce desde niño. Todo es más pequeño de lo que recordaba. Piensa en la selva de Río, en la pampa, en los Andes. El jardín no ha cambiado nada. Él sí.", Hacer.LIBRE),
        ca("A esta hora, junto al fuego de la biblioteca, lee las cartas de los sabios que leyeron las suyas, y no se lo cree: lo esperan, quieren ver lo que trajo, quieren que hable. Su hermana le dice que se ha hecho famoso sin darse cuenta.", Hacer.LIBRE),
    )

    private val CASA_PUEBLO = listOf(
        ca("La casa de su padre es grande, de ladrillo rojo, sobre una colina junto al río, con un jardín que su padre cuida como a un paciente, un invernadero y caballos. Desde las ventanas se ven los campos de Inglaterra, verdes y ordenados, con sus setos. Todo es pequeño, limpio y quieto."),
        ca("La ciudad, abajo, es pequeña, de casas con vigas, iglesias, un mercado y un puente sobre el río. La gente lo saluda por la calle como si se hubiera ido la semana pasada. Él saluda igual. Por dentro, todavía le parece raro que todos hablen inglés."),
        ca("Inglaterra en otoño huele a hojas mojadas, a humo de carbón y a lluvia. Los árboles se ponen amarillos. Por los caminos pasan carros de carbón y diligencias. El naturalista lo mira todo como si fuera un país extranjero. Le dicen que ahora el extranjero es él."),
    )

    private val CASA_GENTE = listOf(
        ca("Su padre es un médico enorme, de voz grave, que habla mucho y escucha poco, y que lo quiere más de lo que sabe decir. Desde que volvió le pregunta cada noche por el viaje, en su sillón, con una pregunta distinta. Al final asiente. Ya no dice que fue una tontería."),
        ca("Sus hermanas lo rodean desde que volvió: le cosen ropa nueva, le preparan sus comidas preferidas y le hacen contar el viaje una y otra vez. La pequeña, la que más le escribía, guarda todas sus cartas en una caja. Se la enseña. Él no sabía que había escrito tanto."),
        ca("En la casa hay ahora un cuarto lleno de cajones del viaje, que huelen a mar, a alcohol y a pájaro seco. Las criadas no quieren entrar. El jardinero mira las piedras con desconfianza. Su padre dice que la casa parece un museo. Lo dice orgulloso."),
    )

    private val CASA_PENSAMIENTO = listOf(
        ca("Piensa en lo que hará ahora: la iglesia ya no, eso lo sabe. Los sabios le dicen que siga, que escriba, que estudie lo que trajo. Su padre no se opone. Piensa que el viaje no ha terminado: que acaba de empezar, aquí, en una mesa."),
        ca("El naturalista piensa en las islas, en los pájaros de cada isla, en las tortugas de cada isla, en las conchas de la montaña y en los huesos de la pampa. Piensa que todo eso está unido de alguna manera. No sabe cómo. Tiene toda la vida para averiguarlo."),
        ca("Piensa en el capitán, en el muchacho, en los marineros, que siguen en el bergantín sin él, y en que no los volverá a ver todos juntos. Cinco años en una cáscara de madera. Piensa que fueron su familia. Les escribe a algunos. Las cartas le salen torpes."),
    )

    private val CASA_ANOCHECER = listOf(
        ca("Al caer la tarde en casa de su padre se encienden las lámparas, y la familia se reúne en el salón: su padre en su sillón, sus hermanas con la labor, él con un libro que no lee. Se habla del tiempo. Afuera llueve. No hay nada más tranquilo en el mundo."),
        ca("Anochece temprano en el otoño inglés, y el naturalista mira por la ventana el jardín a oscuras. Piensa en el bergantín, que estará en algún puerto, sin él. Piensa en el muchacho, en el capitán, en los marineros. Los echa de menos. No lo esperaba."),
        ca("De noche, en la biblioteca, el naturalista abre sus cuadernos de notas y empieza a hacer listas: las islas, los pájaros, los fósiles. Las listas no le dicen nada todavía. Pero tiene la sensación de que van a decirle algo. Se queda hasta tarde."),
    )

    private val CASA_TIEMPO = listOf(
        ca("{cielo} En Inglaterra se habla del tiempo como en el barco, pero sin miedo."),
        ca("Desde la ventana de su cuarto se ve el cielo de Inglaterra, bajo y gris casi siempre. {cielo} Es otoño, y los días se acortan."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy empieza el año, y en el bergantín se celebra con lo que se puede: el capitán manda dar una ración de más a la tripulación, se iza la bandera, y en la cabina los oficiales brindan con vino. El naturalista brinda también, aunque no sabe si su estómago está de acuerdo.",
            Parrafo.GENTE to "Esta noche se brinda por el rey, por Inglaterra, por las madres, por las novias que esperan y por el viaje, que nadie sabe cuándo terminará. Los marineros cantan en la proa. El naturalista piensa en sus hermanas, que brindarán por él a otra hora, porque aquí el sol no sale cuando sale en casa.",
            Parrafo.PENSAMIENTO to "El naturalista piensa en el año que empieza y en todo lo que no sabe de él: adónde irán, qué verá, qué encontrará. Piensa que es la mejor manera de empezar un año: sin saber nada. Luego se marea, y piensa que también hay maneras peores.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, y el naturalista se despierta pensando en las Navidades de cuando era niño: la iglesia, el ganso, el pudin en llamas, sus hermanas cantando junto al piano. Cada año que pasa las recuerda mejor, como si se fueran acercando.",
            Parrafo.GENTE to "Hoy se come mejor que otros días, un ganso si lo hay, o lo que haya, y un pudin, y se brinda por el rey, por la familia, por los ausentes. Se cantan villancicos. El naturalista canta mal y fuerte, como cantaba de niño, y hoy nadie se lo reprocha.",
            Parrafo.PENSAMIENTO to "El naturalista piensa que la Navidad es la misma en todas partes: en un barco, en una playa, en una casa. Lo que cambia es quién falta. Cuenta a los que faltan este año y les manda, de lejos, un pensamiento. A algunos no los volverá a ver. Lo sabe. Brinda igual.",
        ),

        // ---------- Los de la temporada 1 ----------
        "mareo" to unDia(
            Parrafo.MANANA to "Hoy hace más de una semana que el naturalista casi no sale de la hamaca. El mar lo ha deshecho. No come más que pasas y galleta seca. El capitán viene a verlo cada día y le dice que se le pasará. El naturalista ya no le cree.",
            Parrafo.TRABAJO to "A esta hora no trabaja: está tumbado en la hamaca, con los ojos cerrados, intentando no pensar en el mar. Cuando se mueve, vomita. Cuando no se mueve, también. Piensa en las tardes de Cambridge. Piensa que nadie le dijo que el mar era esto.",
            Parrafo.PENSAMIENTO to "El naturalista piensa, entre arcada y arcada, que ha cometido el mayor error de su vida, y que su padre tenía razón. Luego piensa en las islas, las selvas y los volcanes que lo esperan, y se le pasa un poco. Luego vuelve el mar y no piensa nada.",
        ),
        "praia" to unDia(
            Parrafo.MANANA to "Hoy, por fin, tierra: el bergantín fondeó frente a Praia, en una isla volcánica de Cabo Verde, seca, de rocas negras, con unas palmeras y un pueblo de casas bajas. El naturalista bajó en el primer bote. Pisó la arena y se le doblaron las rodillas de alegría.",
            Parrafo.TRABAJO to "A esta hora camina por la isla con el martillo, rompiendo rocas, y encuentra en un acantilado, a bastante altura sobre el mar, una capa blanca de conchas entre dos capas de roca negra. Conchas como las de la playa de hoy. Se queda mucho rato mirándola. El mar estuvo allí arriba.",
            Parrafo.PENSAMIENTO to "El naturalista piensa, mirando la capa de conchas, que el libro de geología que lleva tenía razón: la tierra sube y baja, despacio, durante siglos. Piensa que podría escribir un libro sobre esto. Lo piensa por primera vez. Le da vergüenza pensarlo. Lo piensa igual.",
        ),
        "ecuador" to unDia(
            Parrafo.MANANA to "Hoy cruzaron el ecuador, y anoche subió a bordo, con mucho ruido, un Neptuno de mentira con barba de estopa y su corte de marineros disfrazados, para recibir a los que lo cruzan por primera vez. El naturalista es uno de ellos. Se pasó la noche temiendo la mañana.",
            Parrafo.TRABAJO to "A esta hora lo sientan con los ojos vendados en una tabla junto a una vela llena de agua, le enjabonan la cara con brea y pintura, lo afeitan con un aro de hierro oxidado y lo echan de espaldas al agua mientras los marineros le tiran cubos. Sale tosiendo y riéndose. Ya es un hombre de mar.",
            Parrafo.GENTE to "Los marineros se lo pasan en grande con los nuevos, y más con el naturalista, que es un caballero y no se queja. El capitán mira desde la popa, serio, pero con los ojos riéndose. Al final, todos beben a la salud de Neptuno. El naturalista bebe dos veces.",
        ),
        "bahia" to unDia(
            Parrafo.MANANA to "Hoy el bergantín llegó a Bahía, en el Brasil: una ciudad de iglesias sobre una colina verde, una bahía enorme llena de barcos y, detrás, la selva. La primera selva. El naturalista no ha dormido de los nervios. Bajó en el primer bote, con la red y la caja de los escarabajos.",
            Parrafo.TRABAJO to "A esta hora camina solo por la selva, borracho de alegría: árboles que no conoce, flores en los troncos, plantas que crecen sobre otras plantas, mariposas enormes, ruidos por todas partes. No sabe qué coger primero. Coge de todo. Las bolsas no le alcanzan.",
            Parrafo.PENSAMIENTO to "El naturalista piensa que ha leído a Humboldt mil veces y que Humboldt no exageraba: se quedó corto. Piensa que un día así compensa todos los mareos del mundo. Esta noche escribe cuatro cartas. En ninguna habla del mareo.",
        ),
        "esclavos" to unDia(
            Parrafo.MANANA to "Hoy, en la mesa, el capitán contó que visitó a un dueño de esclavos que les preguntó delante de él si querían ser libres, y que todos dijeron que no. El naturalista le dijo que qué iban a contestar delante de su amo. El capitán se levantó de la mesa, furioso.",
            Parrafo.GENTE to "El capitán le mandó decir que ya no podían vivir juntos, y el naturalista pensó en dejar el barco. Los oficiales lo invitaron a comer con ellos. Por la tarde el capitán le mandó una disculpa con un oficial y una invitación a su mesa. El naturalista fue. No se habló más de esclavos.",
            Parrafo.PENSAMIENTO to "El naturalista piensa en los esclavos que ha visto en Bahía, cargando, encadenados, vendidos en la plaza, y en los gritos que oyó una noche detrás de una pared. Piensa que en su familia todos están contra la esclavitud, desde el abuelo. Piensa que su familia tiene razón, y el capitán no.",
        ),
        "rio" to unDia(
            Parrafo.MANANA to "Hoy el bergantín entró en la bahía de Río de Janeiro, la más hermosa del mundo, dicen, y no exageran: montañas de piedra que salen del agua, islas, playas, selva hasta el cielo y la ciudad blanca al fondo. El naturalista ayudó a maniobrar con los marineros, gritando de contento.",
            Parrafo.GENTE to "En Río lo esperaban cartas de casa, las primeras desde que salió, y las lee sentado en la cubierta, una tras otra, sin levantar la cabeza. Sus hermanas le cuentan todo. Una le dice que una muchacha a la que él quería se ha prometido con otro. Se queda callado un rato largo.",
            Parrafo.PENSAMIENTO to "El naturalista piensa en la muchacha, que ya no lo va a esperar, y en que él tampoco le pidió que lo esperara. Piensa que el viaje durará años y que no se puede pedir a nadie que espere años. Luego mira la bahía. Piensa que hay cosas que consuelan sin pedir permiso.",
        ),
        "botafogo" to unDia(
            Parrafo.MANANA to "Hoy el naturalista se mudó a la casita de Botafogo, una bahía junto a Río, con el dibujante del barco, que la encontró. Mientras el bergantín vuelve al norte a medir, él se quedará en tierra, dos meses o más. Por el camino, el bote se llenó de agua y casi pierde los libros.",
            Parrafo.TRABAJO to "A esta hora saca de los cajones los libros mojados, los papeles, las redes, los frascos, y los pone a secar al sol en la galería, uno por uno. La mitad se salva. La otra mitad, casi. Lo que más le duele es un cuaderno de notas. Lo seca hoja por hoja, con paciencia de santo.",
            Parrafo.PENSAMIENTO to "El naturalista piensa, sentado en la galería, mirando la selva que empieza en el jardín, que por fin tiene lo que vino a buscar: tiempo, tierra firme y un mundo entero sin estudiar. Piensa que sería feliz aquí toda la vida. Luego se acuerda de que dijo lo mismo en Bahía.",
        ),
        "escarabajos" to unDia(
            Parrafo.MANANA to "Hoy el naturalista salió temprano con la red y los frascos a un bosque nuevo, detrás de la montaña, y no volvió hasta la noche. Se tumbó en el suelo, levantó cortezas, sacudió ramas sobre un paraguas abierto al revés. Cada escarabajo era otro. Perdió la cuenta. Luego la recuperó.",
            Parrafo.TRABAJO to "A esta hora separa los escarabajos del día en la mesa de la galería, con pinzas, uno por uno, y los cuenta: sesenta y ocho clases distintas, en una sola tarde, en un solo bosque. En Inglaterra no se juntan tantas en un año. Los vuelve a contar. Sesenta y ocho.",
            Parrafo.PENSAMIENTO to "El naturalista piensa en sus amigos de Cambridge, con los que cazaba escarabajos en los setos, y en la cara que pondrán cuando les cuente esto. Piensa que el mundo es mucho más rico de lo que dicen los libros. Piensa que alguien tendría que contarlo todo. Piensa que a lo mejor él.",
        ),
        "vuelve_bordo" to unDia(
            Parrafo.MANANA to "Hoy el naturalista dejó la casa de Botafogo y volvió al bergantín, que zarpa hacia el sur, con cajones llenos de pájaros, insectos, plantas, frascos y piedras. Los marineros lo vieron subir tanta cosa y apostaron dónde la iba a meter. Ganó el que dijo que en la cabina del capitán.",
            Parrafo.PUEBLO to "El bergantín sale de la bahía de Río con la marea, y la ciudad blanca, las montañas y la selva se van quedando atrás, hasta que solo queda la gran piedra del Pan de Azúcar. Luego, nada. El mar, otra vez. El naturalista ya lo siente en el estómago.",
            Parrafo.PENSAMIENTO to "El naturalista piensa en los dos meses de Botafogo como en un sueño: la selva, los pájaros, las luciérnagas, la cama que no se movía. Piensa que nunca se cansará de esto. El capitán dice que al sur no hay selvas, solo viento. Se marea antes de terminar de pensarlo.",
        ),
        "envio" to unDia(
            Parrafo.MANANA to "Hoy salió hacia Inglaterra, en el barco correo de Falmouth, el primer cajón de especímenes del naturalista, clavado, forrado, numerado, con una carta larga para su profesor de Cambridge. Tardará meses en llegar. Si llega. El naturalista lo vio irse en el bote como quien ve irse a un hijo.",
            Parrafo.TRABAJO to "A esta hora vacía la mesa de la cabina, que estaba llena desde Río, y reordena lo que queda: lo frágil, abajo; lo que huele, fuera; lo que pica, en frascos. Por primera vez en meses, ve el suelo de la cabina. Lo mira con cariño.",
            Parrafo.PENSAMIENTO to "El naturalista piensa en su profesor abriendo el cajón en Cambridge, en lo que pensará de los pájaros mal disecados, de las etiquetas manchadas, de las piedras. Tiene miedo de que le parezca poco. Tiene miedo de que le parezca mucho y no se lo crea. Tiene miedo, en general.",
        ),
        "punta_alta" to unDia(
            Parrafo.MANANA to "Hoy, en un paseo por la bahía con el capitán, pararon en un sitio que se llama Punta Alta, y en unas barrancas bajas, a la orilla, el naturalista vio salir de la roca dientes y huesos enormes. Huesos de animales gigantes que ya no existen. Se puso de rodillas y empezó a cavar con las manos.",
            Parrafo.TRABAJO to "A esta hora saca huesos de la roca con el martillo y la navaja, con cuidado, uno por uno: una mandíbula con dientes, un pedazo de caparazón, un hueso de pierna más grueso que su cintura. Los marineros lo ayudan a cargarlos en el bote, protestando. El primer teniente dice que va a hundir el barco.",
            Parrafo.PENSAMIENTO to "El naturalista piensa en los animales de esos huesos, que vivieron aquí, en esta misma playa, enormes, y que ya no están. Se pregunta por qué. Se pregunta si se parecían a algún animal de ahora. Le parece que el armadillo que cazó ayer tiene el mismo caparazón, pero pequeño. Lo apunta.",
        ),
        "carta" to unDia(
            Parrafo.MANANA to "Hoy, en el puerto, llegó el correo de Inglaterra, y para el naturalista había un paquete con seis cartas de sus hermanas, escritas a lo largo de meses, que llegaron todas juntas. Las puso en orden de fecha sobre la hamaca y se encerró con ellas en la cabina toda la tarde.",
            Parrafo.GENTE to "Sus hermanas le cuentan todo: que su padre está bien y gruñe igual, que el perro se escapó y volvió, que en Londres se habla de la reforma, que la muchacha ya se casó. Le preguntan si come, si duerme, si se marea. Le piden que no se olvide de ellas. Él se ríe y llora a la vez.",
            Parrafo.PENSAMIENTO to "El naturalista piensa, con las cartas en el regazo, que su casa está ahí, en esas hojas, más que en ningún sitio. Piensa que tardará años en volver a verlas. Empieza a escribirles esa misma noche una carta enorme. No sabe cuándo saldrá. Escribe igual.",
        ),
        "fuego" to unDia(
            Parrafo.MANANA to "Hoy el bergantín llegó a Tierra del Fuego: montañas negras, bosques oscuros, nieve en las cumbres, el cielo gris y bajo, y en la costa, junto a un fuego, unos hombres casi desnudos, pintados de rojo y blanco, que gritaban y movían los brazos. El naturalista los miró con el catalejo, sin respirar.",
            Parrafo.GENTE to "Jemmy y los otros dos fueguinos que viajan a bordo, vestidos de ingleses, miran a los de la costa como si no fueran de su gente. Jemmy dice que son de otra tribu, mala. Los marineros se ríen. El naturalista no se ríe. Mira a Jemmy, con sus guantes, y a los de la playa, y no entiende nada.",
            Parrafo.PENSAMIENTO to "El naturalista piensa que nunca había visto a hombres así, y lo escribe en su diario con palabras duras. Luego piensa en Jemmy, que en tres años aprendió inglés, modales y a peinarse. Piensa que la diferencia entre unos y otros no está en la sangre. No sabe dónde está. Lo apunta.",
        ),
        "cabo_hornos" to unDia(
            Parrafo.MANANA to "Hoy el bergantín intentó doblar el cabo de Hornos y el mar no lo dejó: olas como colinas, grises, con la cresta blanca, que levantaban el barco hasta el cielo y lo dejaban caer en un valle de agua. El viento arrancaba la espuma. El capitán no se movió de la cubierta en todo el día.",
            Parrafo.TRABAJO to "A esta hora no trabaja: está en su hamaca, agarrado a las cuerdas, oyendo crujir el barco como si se fuera a partir. El agua entra por la claraboya. Los frascos se rompen en la mesa. El naturalista reza, por primera vez en mucho tiempo, sin vergüenza.",
            Parrafo.PENSAMIENTO to "El naturalista piensa que el mar no es un paisaje: es una fuerza, como el fuego, y que los hombres que viven en él son de otra pasta. Piensa en los marineros que están arriba, ahora, en las vergas, con este viento. Piensa que nunca volverá a quejarse de un mareo. Se marea.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "naturalista", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + RIO + PAMPA + ANDES + ENFERMO + ISLAS + CASA,
            Parrafo.TIEMPO to TIEMPO + RIO_TIEMPO + PAMPA_TIEMPO + ANDES_TIEMPO + ENFERMO_TIEMPO + ISLAS_TIEMPO + CASA_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + RIO_TRABAJO + PAMPA_TRABAJO + ANDES_TRABAJO + ENFERMO_TRABAJO + ISLAS_TRABAJO + CASA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + RIO_PUEBLO + PAMPA_PUEBLO + ANDES_PUEBLO + ENFERMO_PUEBLO + ISLAS_PUEBLO + CASA_PUEBLO,
            Parrafo.GENTE to GENTE + CASA_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + CASA_PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + RIO_ANOCHECER + PAMPA_ANOCHECER + ANDES_ANOCHECER + ENFERMO_ANOCHECER + ISLAS_ANOCHECER + CASA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
