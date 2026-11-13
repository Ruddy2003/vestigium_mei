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
 * El cuento de José, peón acasillado en una hacienda pulquera de los Llanos de Apan hacia 1905. Sigue la gramática y el
 * tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Es tlachiquero: raspa los magueyes, chupa el aguamiel con el acocote y lo lleva al tinacal, donde se hace pulque.
 *   Vive en la hilera de cuartos de los peones, junto al casco de la hacienda, con su capilla, su tienda de raya y su
 *   tinacal. Cada mañana sale el tren del pulque hacia la capital.
 * - El párrafo del pueblo lee el arco «milpa»: el maíz del pegujal, el pedazo de tierra que le prestan.
 * - No repite lo que ya dice «Su vida»: el cuarto de adobe prestado, los seis días de sol a sol, los centavos y los
 *   vales, la comida, la deuda que se hereda, que no sabe leer y que falta poco para la Revolución.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG2.kt`):
 * - Rafael nace en la temporada 2 (`nace_rafael`); Petra lo espera desde `petra_espera` (1). Cuca nace en la 4
 *   (`nace_cuca`); Petra la espera desde `petra_otra_vez`.
 * - Doña Refugio, su madre, vive hasta `muere_refugio` (temporada 4).
 * - Nicolás, su compadre, está en la hacienda hasta `huye_nicolas` (temporada 5).
 * - Chencho lo acompaña a los magueyes desde `chencho_magueyes` (temporada 1).
 * - El estado «herido» (temporada 3): dos semanas sin raspar.
 * - Las fiestas que vuelven cada año no nombran a Rafael, a Cuca, a doña Refugio ni a Nicolás.
 */
internal object PiezasJose {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A {nombre} lo despierta la campana de la capilla de la hacienda, todavía de noche, y el frío de los Llanos, que a esta hora se mete por debajo de la puerta. Se levanta del petate, se echa el sarape encima y sale al patio, donde los otros peones ya se juntan para el alabado."),
        v("Petra ya está despierta desde hace rato, de rodillas frente al metate, moliendo el maíz que puso a cocer anoche con cal. El ruido de la piedra es lo primero que {nombre} oye cada día, desde que tiene memoria: antes era el de su madre, ahora es el de su mujer."),
        v("{nombre} se levanta a oscuras, se pone los huaraches y el sombrero, y toma un jarro de atole caliente de pie, junto al fogón, con una tortilla doblada. Afuera, en el corral, rebuzna el burro, que sabe la hora mejor que nadie."),
        v("Por la puerta abierta del cuarto se ve el llano entero, plano hasta el cerro, con los magueyes en hileras perfectas, como soldados de un ejército muy quieto. A esta hora, con la primera luz, sus pencas parecen de plata. {nombre} los mira como se mira el trabajo que espera."),
        v("En la hilera de cuartos de los peones se encienden los fogones uno a uno, y el humo sale por las puertas, porque los cuartos no tienen chimenea. {nombre} se lava la cara con agua de la tinaja, que está helada, y se peina con los dedos."),
        v("Antes de salir, {nombre} se persigna frente a la estampa de la Virgen de Guadalupe que está pegada a la pared de adobe, con una veladora apagada delante. No pide nada en concreto. Pide lo de siempre: que no haya deuda nueva, y que los niños no se enfermen."),
        v("La primera luz llega por detrás del cerro, roja, y en el casco de la hacienda ya se oye al mayordomo dando órdenes. {nombre} carga el acocote, el raspador y el odre, y sale con los otros tlachiqueros hacia los magueyes, en fila, sin hablar todavía."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En los Llanos el tiempo se mira por los magueyes, que aguantan todo, y por el maíz, que no aguanta nada."),
        v("{nombre} mira el cielo del llano, que aquí es enorme, más grande que la tierra. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los viejos de la hacienda saben leer el cielo como el administrador lee el libro de cuentas, y aciertan más."),
        v("{cielo} En tiempo de {estacion} el llano cambia de color, y {nombre} lo conoce por el color como se conoce a una persona por la cara."),
        v("A lo lejos, cuando el aire está limpio, se ven los volcanes, blancos, al otro lado del llano. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En la hacienda se dice que el tiempo lo manda Dios y el jornal lo manda el patrón, y que de los dos el que más cambia es el tiempo."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y azul como el manto de la Virgen, sin una nube de un cerro a otro.",
            "Hace un día claro, y el sol de la altura quema aunque el aire esté frío.",
            "No hay una nube, y el llano brilla tanto que hay que bajar el ala del sombrero.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris sobre el llano gris.",
            "Hay nubes altas que pasan deprisa y hacen correr sombras sobre los magueyes.",
            "Está nublado, y el cerro tiene la cabeza metida en las nubes.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y la tierra del llano huele como no huele en ningún otro momento del año.",
            "Cae un aguacero de la tarde, de los que llegan de golpe y se van igual.",
            "Llueve, y los caminos de la hacienda se vuelven barro que se pega a los huaraches.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los rayos caen sobre el llano, donde lo más alto es un hombre o un maguey, y los dos se agachan.",
            "Se viene una tormenta negra desde el cerro, de las que traen granizo.",
        ),
        Clima.NIEBLA to listOf(
            "Hay neblina en el llano, y los magueyes salen de ella de uno en uno, como fantasmas.",
            "La neblina tapa el cerro y el casco de la hacienda, y solo se oye la campana.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del llano, que no encuentra nada que lo pare y llega hasta los huesos.",
            "Hay viento, y las pencas secas de los magueyes viejos suenan como papel.",
        ),
        Clima.POLVO to listOf(
            "Hay tolvanera: el viento levanta el polvo del llano en remolinos altos como torres.",
            "El polvo flota sobre el llano y tiñe el sol de amarillo.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.SECAS to listOf(
            "Es tiempo de secas, y el llano está pardo y polvoriento hasta el horizonte.",
            "En secas las noches son tan frías que en las mañanas hay escarcha en los magueyes.",
            "Son las secas, cuando el maguey da su mejor aguamiel y el maíz no da nada.",
        ),
        Estacion.LLUVIAS to listOf(
            "Es tiempo de aguas, y el llano se pone verde en una semana, como si alguien lo hubiera pintado.",
            "En aguas llueve cada tarde, y las mañanas son limpias y brillantes.",
            "Es tiempo de lluvias, cuando crecen la milpa, los quelites y los charcos.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora raspa un maguey. Le quita la tapa de penca que puso ayer sobre el hueco del corazón, el cajete, y con el raspador de hierro le rasca las paredes hasta que suda. El maguey llora aguamiel toda la noche, y mañana habrá que rasparlo otra vez.", hacer = Hacer.TRABAJO),
        v("A esta hora chupa el aguamiel con el acocote, un guaje largo y hueco con un agujero en cada punta: mete una en el cajete del maguey, sorbe por la otra y el aguamiel sube. Luego lo vacía en el odre. Es dulce, y no se lo bebe, porque se lo cuentan.", hacer = Hacer.TRABAJO),
        v("A esta hora capa un maguey que ya va a echar el quiote, el tallo de la flor: le abre el corazón con un cuchillo antes de que florezca, para que toda la fuerza que iba a la flor se le vaya en aguamiel. Un maguey tarda diez años en llegar a esto, y meses en morirse.", hacer = Hacer.TRABAJO),
        v("A esta hora lleva el aguamiel al tinacal en el burro, con los dos odres llenos colgando a los lados. En la puerta, el mayordomo del tinacal mide cuánto trae cada tlachiquero, lo apunta y lo echa en las tinas de cuero, donde el pulque fermenta solo, con su olor.", hacer = Hacer.TRABAJO),
        v("A esta hora camina entre las hileras de magueyes del potrero que le toca, uno por uno, y los conoce como se conoce a la gente: este da mucho, este se está secando, a este le entró un gusano. Ninguno es suyo. Todos le dan de comer.", hacer = Hacer.TRABAJO),
        v("A esta hora planta hijuelos de maguey en una hilera nueva, con la coa, en hoyos que ya hizo ayer. Los que planta hoy darán aguamiel dentro de diez años, cuando Chencho sea un hombre. {nombre} piensa en eso cada vez que planta, y planta derecho.", hacer = Hacer.TRABAJO),

        v("A esta hora reza el alabado con los otros peones, en el patio de la hacienda, frente a la puerta de la capilla, todavía de noche. Es un canto largo y lento, de muchas estrofas, que se canta en coro y que nadie sabe dónde empezó. Lo cantaba su abuelo.", hacer = Hacer.REZAR),
        v("A esta hora canta el alabado con los sombreros en la mano, en el patio frío, y el canto sube hacia el cerro como el humo. El administrador escucha desde el portal de la casa grande. Cuando acaba el último verso, todos se ponen el sombrero y salen al campo.", hacer = Hacer.REZAR),
        v("A esta hora reza, y en el rezo pide por los suyos, por la lluvia y por el patrón, porque así lo enseñaron: primero los de uno, luego el cielo, luego el que manda. {nombre} reza deprisa la última parte. Dios entiende.", hacer = Hacer.REZAR),

        v("A esta hora come a la sombra de un maguey grande, en cuclillas, lo que trajo Petra en el itacate: tortillas todavía tibias envueltas en un trapo, frijoles, chile verde machacado y un jarro de pulque. Comparte con el burro las orillas de las tortillas.", hacer = Hacer.COMER),
        v("A esta hora come con los otros tlachiqueros en la orilla del potrero, sentados en las piedras. Cada uno saca lo suyo y se lo enseña a los demás, y casi siempre es lo mismo: tortilla, chile, frijol. Uno trae un pedazo de queso, y se lo miran como si fuera de oro.", hacer = Hacer.COMER),
        v("A esta hora come en el cuarto, sentado en el suelo junto al fogón, frijoles con epazote, tortillas recién hechas y nopales. Petra echa las tortillas en el comal una tras otra, y {nombre} se las come tan rápido como ella las hace, hasta que dice que ya.", hacer = Hacer.COMER),

        v("A esta hora está sentado afuera del cuarto con los vecinos de la hilera, en un banco de adobe, con un jarro de pulque que va de mano en mano. Se habla del tiempo, de los magueyes y del administrador, en voz baja, porque las paredes de la hacienda oyen.", hacer = Hacer.GENTE),
        v("A esta hora alguien saca una guitarra en la puerta de un cuarto y toca canciones viejas, de amores y de caballos. Los demás cantan mal, y las mujeres se ríen desde dentro. {nombre} no canta, pero lleva el ritmo con el pie en la tierra.", hacer = Hacer.GENTE),
        v("A esta hora escucha al más viejo de los peones contar historias de antes: de cuando pasaron los franceses por el llano, de bandidos que se escondían en los cerros, de un tesoro enterrado en el casco. Las cuenta cada noche, y cada noche el tesoro es más grande.", hacer = Hacer.GENTE),

        v("A esta hora duerme en el petate, con toda la familia en el mismo cuarto, tapados con los sarapes. Afuera ladran los perros de la hacienda a algo que solo ellos ven. El frío del llano entra por las rendijas, y los niños se le arriman como cachorros.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y en el silencio del llano se oye pasar a lo lejos el tren de la noche, con su silbido largo. {nombre} nunca se ha subido a un tren. Lo oye y se vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, como duermen los que caminan todo el día detrás de un burro. Petra dice que habla dormido y que cuenta magueyes. Él dice que no. Ella dice que se equivocó en la cuenta.", hacer = Hacer.DORMIR),
    )

    /** Las dos semanas con la mano infectada (estado «herido», temporada 3). */
    private val HERIDO = listOf(
        v("{nombre} se despierta con la mano latiendo como un corazón aparte, hinchada y caliente, envuelta en un trapo con hierbas que le puso Petra. Oye la campana del alabado y no se levanta. Los otros salen a los magueyes sin él, y el ruido de sus pasos se aleja.", estado = "herido"),
        v("A esta hora no raspa: está sentado a la puerta del cuarto con la mano en el regazo, mirando el llano donde trabajan los demás. Sin raspar no hay raya, y sin raya no hay maíz. Petra muele más despacio, para que dure. Nadie lo dice.", estado = "herido", hacer = Hacer.TRABAJO),
        v("A esta hora, sin poder trabajar, hace lo que se puede con una mano: le enseña a Chencho a afilar el raspador, arregla un mecate, espanta las gallinas. Se siente inútil como un maguey seco. Su mujer le dice que se esté quieto, y no se está.", estado = "herido", hacer = Hacer.TRABAJO),
        v("A esta hora come en el cuarto, a deshoras, lo que Petra le pone delante: tortillas, un caldo de quelites y un jarro de pulque, que dicen que baja la calentura. Come con la mano izquierda, torpe, y Lupe se ríe de cómo se le cae el frijol.", estado = "herido", hacer = Hacer.COMER),
        v("La curandera de la hilera viene cada tarde a verle la mano: la lava con agua de hierbas, le pone una cataplasma de nopal y le reza encima en voz baja. Dice que la púa estaba envenenada de envidia. {nombre} no cree eso, pero se deja.", estado = "herido"),
    )

    private val PUEBLO = listOf(
        v("La hacienda es un pueblo con dueño: tiene su casa grande con portales, su capilla, su tienda, su tinacal, sus corrales y sus trojes, y alrededor, en hilera, los cuartos de los peones, todos iguales. Todo está dentro de un muro alto, con una puerta que se cierra de noche. {entorno}"),
        v("{entorno} El tinacal es una nave larga y oscura, pintada por dentro con santos y frases, donde huele a pulque fuerte desde la puerta. Ahí no entran las mujeres, porque se dice que el pulque se corta. El que manda dentro es el mayordomo del tinacal, que no se ríe nunca."),
        v("Cada mañana, en la estación del ferrocarril, se cargan los barriles de pulque de todas las haciendas del llano en un tren que sale para la capital, donde se lo beben antes de que se eche a perder. Allá hay mil pulquerías, dicen. {nombre} no conoce ninguna. {entorno}"),
        v("En la tienda de raya se vende todo lo que un peón necesita, a un precio que pone el administrador, y se paga con los vales de la hacienda, que no sirven en ninguna otra parte. Lo que no alcanza se apunta en un libro grande. {entorno}"),
        v("{entorno} La hacienda tiene más tierra de la que se recorre a caballo en un día, y en ella hay magueyes, cebada, ganado y tres pueblos que antes fueron libres. Los peones de fuera, los que vienen por temporadas, duermen en las trojes. Los de dentro, como {nombre}, no se van."),
        v("Por el camino real, que pasa junto a la hacienda, se ve a veces a los rurales, a caballo, con sus sombreros grandes y sus carabinas, que cuidan el orden de don Porfirio en los llanos. Cuando pasan, los peones se quitan el sombrero y miran al suelo. {entorno}"),
    )

    /** El maíz del pegujal, según cómo va la milpa (arco «milpa»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("milpa", 0.9, frases = listOf(
            "En el pegujal, la milpa ya está amarilla y las mazorcas cuelgan pesadas, listas para la pizca.",
            "La milpa del pegujal está seca y crujiente, y las mazorcas, gordas.",
        )),
        Entorno("milpa", 0.4, 0.9, frases = listOf(
            "La milpa del pegujal está alta y verde, con los frijoles enredados en las cañas y las calabazas en el suelo.",
            "En el pegujal el maíz ya llega al hombro y empieza a echar jilote.",
        )),
        Entorno("milpa", 0.01, 0.4, frases = listOf(
            "En el pegujal, el maíz recién nacido asoma en hileras, verde contra la tierra negra.",
            "La milpa del pegujal es todavía una hilera de hojitas que cualquier granizo puede llevarse.",
        )),
        Entorno("milpa", 0.0, 0.01, frases = listOf(
            "El pegujal está pelón, con el rastrojo del año pasado, esperando las aguas.",
            "La tierra del pegujal está dura y parda, sin nada, y las aguas no han llegado.",
        )),
    )

    private val GENTE = listOf(
        v("Petra, su mujer, se levanta antes que nadie a moler el nixtamal y se acuesta después que nadie. Cría guajolotes y gallinas detrás del cuarto, y los vende en el mercado del domingo para comprar lo que la tienda no fía. Sabe lo que se debe, centavo por centavo."),
        v("Chencho, su hijo mayor, tiene la edad de querer ser grande y de no poder. Se pasa el día detrás de su padre o detrás del burro, imitando cómo camina el uno y cómo rebuzna el otro. {nombre} lo regaña por lo segundo y no por lo primero."),
        v("Lupe, la niña, ayuda a su madre en todo lo que la dejan y en mucho de lo que no: carga agua en un cántaro pequeño, espanta a los guajolotes, lava trapos en la acequia. Es callada y lo mira todo. {nombre} dice que es la que más se le parece."),
        v("Don Anselmo, el administrador, vive en la casa grande y manda en todo cuando el patrón no está, que es casi siempre. Es un hombre gordo y amable que nunca levanta la voz, y que cuando apunta una deuda nueva en el libro lo hace con una sonrisa, como si fuera un regalo."),
        v("El patrón vive en la capital, en una casa con balcones, y viene a la hacienda pocas veces al año, en el tren. Los peones lo ven llegar en coche desde la estación, con su familia y sus invitados, y se quitan el sombrero. {nombre} le ha hablado una vez en su vida."),
        v("Doña Refugio, su madre, vive con ellos, en un rincón del cuarto, y es la que más sabe de hierbas y de santos de toda la hilera. Ya no muele, porque le duelen las manos, pero les cuenta a los niños historias de aparecidos que no los dejan dormir.", hasta = "muere_refugio"),
        v("Nicolás, su compadre, es tlachiquero como él, y raspan potreros vecinos desde que eran muchachos. Es más bromista que {nombre} y debe más en la tienda, y dice que algún día se va a ir de la hacienda de noche, sin avisar. Lo dice riéndose.", hasta = "huye_nicolas"),
        v("Petra espera otro hijo, y sigue moliendo igual, de rodillas frente al metate, aunque la barriga ya no la deje acercarse. Las vecinas le traen atole y consejos. {nombre} le carga el agua de la acequia, que es lo único que ella le deja hacer.", desde = "petra_espera", hasta = "nace_rafael"),
        v("Rafael, el más pequeño, gatea por el suelo de tierra del cuarto y se mete todo en la boca, sobre todo la tierra. Su abuela dice que así se hacen fuertes los niños del campo. {nombre} lo levanta en el aire al volver de los magueyes, y el niño se ríe con todos los dientes que no tiene.", desde = "nace_rafael"),
        v("Petra espera otra vez, y esta vez está más cansada, aunque no lo dice. {nombre} se lo nota en la manera de levantarse del metate, apoyándose en la pared. Le dice que descanse, y ella le dice que el maíz no se muele solo.", desde = "petra_otra_vez", hasta = "nace_cuca"),
        v("Cuca, la más pequeña, duerme envuelta en el rebozo de su madre, a la espalda, mientras Petra muele o echa tortillas. Tiene el nombre de su abuela y, dice Petra, el mismo carácter, porque llora a la misma hora que lloraba la vieja. {nombre} se ríe y se calla.", desde = "nace_cuca"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del campo: sin que nadie se lo haya explicado nunca."),
        v("Hay cosas que solo se miran cuando se pierden. {dato_objeto}"),
        v("{dato_objeto} En la hacienda se dice que lo único que es del peón es lo que lleva puesto, y a veces ni eso."),
        v("{dato_objeto} Son cosas que se aprenden mirando al padre, como casi todo en el campo."),
        v("Un peón tiene pocas cosas, y cada una le ha durado más que un patrón. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("acocote", "el acocote", "un acocote", "El acocote",
            "El acocote es un guaje largo, una calabaza seca y hueca con un agujero en cada punta; por una se sorbe y por la otra entra el aguamiel, y los buenos tlachiqueros lo llenan de un solo trago largo, sin respirar."),
        ObjetoCuento("raspador", "el raspador", "un raspador", "El raspador",
            "El raspador es una cuchara de hierro con filo, forjada en la herrería de la hacienda; con él se rasca el corazón del maguey dos veces al día, y si no está afilado el maguey no llora."),
        ObjetoCuento("odre", "el odre", "un odre", "El odre",
            "El odre de llevar aguamiel es un cuero de cerdo entero, cosido y vuelto del revés, que se carga a la espalda o en el burro; huele a lo que ha llevado siempre, y un odre nuevo hace el pulque amargo."),
        ObjetoCuento("jicara", "la jícara", "una jícara", "La jícara",
            "La jícara es media calabaza seca, pulida por dentro, que sirve de vaso, de plato y de cucharón; en la mesa de los pobres no hay otra cosa, y en la de los ricos, para el pulque, tampoco."),
        ObjetoCuento("sombrero", "el sombrero", "un sombrero", "El sombrero",
            "El sombrero de palma de ala ancha se compra en la tienda de raya y dura años; da sombra, sirve de abanico y de canasta, y delante del patrón se quita y se aprieta contra el pecho con las dos manos."),
        ObjetoCuento("sarape", "el sarape", "un sarape", "El sarape",
            "El sarape es una manta de lana tejida a mano, con rayas, que se lleva al hombro de día y sirve de cobija de noche; el suyo se lo tejió su madre cuando se casó, y ya tiene más remiendos que lana."),
        ObjetoCuento("huaraches", "los huaraches", "unos huaraches", "Los huaraches",
            "Los huaraches tienen la suela de cuero viejo o de llanta y unas correas trenzadas; se hacen a la medida del pie, se arreglan en casa y se gastan tan despacio que la planta del pie acaba pareciéndose a ellos."),
        ObjetoCuento("metate", "el metate", "un metate", "El metate",
            "El metate es una piedra volcánica con tres patas y la cara curva, donde se muele el maíz con otra piedra larga, de rodillas; el de la casa era de la abuela, y se oye en todas las casas del llano antes del amanecer."),
        ObjetoCuento("comal", "el comal", "un comal", "El comal",
            "El comal es un disco de barro plano sobre las tres piedras del fogón, donde se echan las tortillas; cuando está bien caliente la tortilla se infla, y entonces está bien hecha, y si no se infla, la culpa es de la cocinera."),
        ObjetoCuento("vale", "el vale de la tienda", "un vale de la tienda", "El vale",
            "El vale de la hacienda es un cartón con un sello o una ficha de metal que dice cuánto vale; solo se cambia en la tienda de raya, por lo que hay y al precio que dice el administrador, y fuera de la hacienda no vale nada."),
        ObjetoCuento("rosario", "el rosario", "un rosario", "El rosario",
            "El rosario es de semillas ensartadas en un hilo, con una crucecita de hueso; lo compró Petra en una peregrinación y cuelga de un clavo junto a la estampa de la Virgen, y se reza con él cuando hay algo que pedir."),
        ObjetoCuento("pulque", "el pulque", "un jarro de pulque", "El pulque",
            "El pulque es blanco, espeso y un poco ácido, y huele fuerte; se hace en pocos días y se echa a perder en pocos más, y por eso se bebe cerca de donde nace, o en la capital, adonde llega cada mañana en el tren."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en la hacienda como se piensa en el cerro: está ahí desde antes que él y va a estar después. Nació en ella, como su padre. A sus {edad} años no conoce otra tierra, y no sabe si la querría."),
        v("Piensa a veces en el pulque que saca, que viaja cada mañana en el tren a la capital y se lo beben en mil pulquerías gente que él nunca verá. Le parece raro que su trabajo conozca la ciudad y él no. Algún día, dice, va a ir a verla. No sabe cuándo."),
        v("{mundo} Él piensa en cosas más cercanas: en la deuda de la tienda, en las lluvias, en la tos de un niño. Las cosas grandes son de los que tienen caballo, y en la hilera de cuartos solo el administrador tiene caballo."),
        v("{mundo} A sus {edad} años, {nombre} ha enterrado a dos hijos pequeños en el camposanto de la hacienda, junto a la capilla, uno de diarrea y otro de fiebre. Petra les lleva flores cada domingo. Él las lleva en noviembre, y el resto del año los piensa."),
        v("No tiene tierra ni la ha tenido nunca: el pegujal se lo prestan, como el cuarto, como todo. Su abuelo tenía un pedazo de tierra en el pueblo de abajo, dicen, hasta que la hacienda se la comió. {nombre} a veces pasa por ahí y la mira. Ahora es un potrero de magueyes."),
        v("{mundo} A veces, raspando un maguey, {nombre} piensa en Dios, que le dio al maguey ese corazón dulce para que los pobres tuvieran algo. Luego piensa que el aguamiel se lo cobra el patrón, y que Dios, en eso, no se metió."),
    )

    private val MUNDOS = mapOf(
        "porfiriato" to listOf(
            "En la tienda se habla de don Porfirio, que lleva tanto tiempo de presidente que los niños creen que se llama así el gobierno.",
            "Se dice que en la capital hay luz eléctrica y tranvías, y que los ricos se visten como en Francia.",
            "Este año no se habla de guerras, sino del precio del pulque, que baja.",
        ),
        "huelgas" to listOf(
            "Se dice, en voz baja, que en el norte los mineros se levantaron contra los gringos de la mina.",
            "En la hilera se habla de unos papeles que dicen que las deudas de los peones se deberían borrar.",
        ),
        "temblor" to listOf(
            "Se habla de los temblores, que asustan más en la capital que en el campo, porque allá las casas son altas.",
            "Se dice que en Veracruz los soldados dispararon contra los obreros de una fábrica, y nadie sabe cuántos murieron.",
        ),
        "entrevista" to listOf(
            "Se dice que don Porfirio va a dejar la presidencia, y nadie le cree.",
            "En la tienda se habla de elecciones, que es una palabra que en la hacienda no se usaba.",
        ),
        "antirreeleccion" to listOf(
            "Se habla de un tal Madero, un rico del norte que dice que don Porfirio ya debe irse.",
            "Se dice que hay clubes en los pueblos donde se habla mal del gobierno, y que los rurales los buscan.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando cae el sol, {nombre} vuelve del tinacal con el burro y los odres vacíos, y el llano se pone rojo y luego morado, y el frío baja de golpe, como si alguien hubiera abierto una puerta. En el cuarto, Petra ya tiene el fuego encendido y las tortillas en el comal."),
        v("Al caer la tarde suena otra vez la campana de la capilla, y en la hilera de cuartos las mujeres llaman a los niños, que vuelven sucios de todos los rincones de la hacienda. {nombre} se lava las manos pegajosas de aguamiel en la acequia, que ya está fría."),
        v("Anochece en los Llanos, y en el cielo salen tantas estrellas que no se ve dónde acaba una y empieza otra. En la casa grande se encienden las lámparas de petróleo; en los cuartos, los fogones. {nombre} se queda un rato afuera, mirando arriba, con el sarape hasta las orejas."),
        v("De noche cierran la puerta grande de la hacienda, con un portón de madera y una tranca de hierro, y nadie entra ni sale sin que lo sepa el velador. {nombre} oye el golpe de la tranca desde el cuarto, cada noche, y sabe que ya está todo, que el día se acabó."),
        v("Se cena junto al fogón, sentados en el suelo, con los niños medio dormidos encima. Petra cuenta lo que se dijo en la acequia y {nombre} lo que se dijo en el tinacal, y casi siempre es lo mismo, contado al revés. Luego se reza un padrenuestro y se apaga el fuego."),
        v("La última luz del día se queda un momento en la torre de la capilla, que es lo más alto de la hacienda, cuando los cuartos ya están a oscuras. {nombre} la mira desde la puerta. Esa luz es del patrón, piensa, como todo, y aun así la mira gratis."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "candelaria" to unDia(
            Parrafo.MANANA to "Hoy es día de la Candelaria, y en la capilla de la hacienda se bendicen las velas y el maíz de la siembra. Petra separó desde ayer las mejores mazorcas del año pasado, las más gordas y derechas, y las lleva en una canasta tapada con un trapo bordado.",
            Parrafo.PUEBLO to "El cura viene del pueblo de abajo, en burro, a decir la misa, y después rocía con agua bendita las canastas de semillas que las mujeres ponen en el suelo de la capilla. El maíz bendito se guarda aparte y se siembra el primero, en el surco del centro.",
            Parrafo.ANOCHECER to "Esta noche se comen tamales en la hilera de cuartos, de frijol y de chile, que hicieron las mujeres juntas en una olla grande. {nombre} guarda en el rincón del cuarto la vela bendita, envuelta en papel, para encenderla cuando haya tormenta o alguien esté muy enfermo.",
        ),
        "ceniza" to unDia(
            Parrafo.MANANA to "Hoy es miércoles de ceniza, y empieza la cuaresma. Antes de ir a los magueyes, los peones pasan por la capilla, donde el cura les pone en la frente una cruz de ceniza con el dedo y les dice que son polvo. {nombre} sale al campo con la cruz negra bajo el sombrero.",
            Parrafo.TRABAJO to "A esta hora raspa magueyes con la cruz de ceniza en la frente, como todos los tlachiqueros, que hoy parecen una cofradía. No se la quitan aunque suden: se va sola, con el día. A media mañana el sudor la ha vuelto una mancha, y sigue siendo una cruz.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en eso de que somos polvo, que en el llano no hace falta que lo diga el cura: se ve cada tarde, cuando sopla el viento. Polvo, piensa, y del llano. Le parece bien que por lo menos eso no lo apunten en la tienda.",
        ),
        "viernes_santo" to unDia(
            Parrafo.MANANA to "Hoy es Viernes Santo, y en la hacienda no se trabaja: ni se raspa, ni se muele, ni se hace ruido. Hasta el tinacal está cerrado. Las campanas de la capilla no suenan, y en su lugar suena una matraca de madera, seca, que da miedo a los niños.",
            Parrafo.PUEBLO to "A mediodía sale de la capilla la procesión del Santo Entierro, con el Cristo muerto en una urna de vidrio, cargado por los hombres de la hilera, y la Dolorosa detrás, vestida de negro, cargada por las mujeres. Dan la vuelta al casco, despacio, rezando, entre cirios.",
            Parrafo.GENTE to "Hoy no se come carne, que de todos modos casi nunca se come, y Petra hace tortitas de camarón seco con nopales en chile rojo, que es la comida de cuaresma. Los niños tienen que estar callados todo el día, y Chencho no aguanta ni una hora.",
        ),
        "judas" to unDia(
            Parrafo.MANANA to "Hoy es sábado de gloria, y a mediodía, cuando en la capilla se anuncia que Cristo resucitó, suenan otra vez las campanas, todas a la vez, y se acaba el silencio. Los peones se echan agua unos a otros, que es costumbre de este día, y los niños corren mojados.",
            Parrafo.PUEBLO to "En el patio de la hacienda cuelgan un Judas de cartón, alto como dos hombres, con bigote y levita, relleno de cohetes. Cuando le prenden la mecha, el Judas revienta en pedazos, girando, entre gritos y aplausos. Este año, dicen, se parece a alguien, y nadie dice a quién.",
            Parrafo.ANOCHECER to "Esta noche se bebe pulque en la hilera, que ya se puede, y se baila con la música de una guitarra y un violín. {nombre} recoge un pedazo de la cabeza del Judas, que cayó junto a su puerta, y se lo da a Chencho, que lo guarda como un tesoro.",
        ),
        "santa_cruz" to unDia(
            Parrafo.MANANA to "Hoy es día de la Santa Cruz, la fiesta de los albañiles y de los pozos. En la puerta del tinacal, en los pozos y en las obras de la hacienda se ponen cruces de madera adornadas con flores de papel y de campo, y los albañiles las bendicen con un trago.",
            Parrafo.TRABAJO to "A esta hora los tlachiqueros ponen una cruz pequeña de penca de maguey, adornada con flores amarillas, en la orilla de cada potrero, para que la cruz cuide los magueyes del gusano y del granizo. {nombre} pone la suya derecha y se persigna delante.",
            Parrafo.ANOCHECER to "Esta noche, en el patio, se queman cohetes frente a la cruz del tinacal y el mayordomo reparte pulque a cuenta de la hacienda, que es la única vez en el año que no se apunta. {nombre} bebe a la salud de la cruz, del tinacal y del mayordomo, en ese orden.",
        ),
        "san_isidro" to unDia(
            Parrafo.MANANA to "Hoy es día de San Isidro Labrador, el patrón de los que trabajan la tierra. A los bueyes de la hacienda les ponen flores en los cuernos y collares de pan, y los llevan en procesión hasta la capilla, donde el cura los bendice con agua y con prisa.",
            Parrafo.PUEBLO to "Detrás de los bueyes sale el santo, en andas, con su yunta pequeña de madera y su ángel que ara por él, y los peones lo pasean por la orilla de los campos de cebada, para que llueva. Se dice que San Isidro quita el agua y pone el sol. Hoy se le pide al revés.",
            Parrafo.PENSAMIENTO to "{nombre} mira al santo, que se fue a rezar y dejó a un ángel arando en su lugar, y piensa que eso es lo que querría él: alguien que raspe los magueyes mientras él descansa. Luego piensa que eso ya existe, y se llama patrón.",
        ),
        "siembra" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siembra en el pegujal, ahora que ya cayeron las primeras aguas y la tierra está blanda. {nombre} pide prestada la yunta de la hacienda para la tarde, después de los magueyes, y lleva en un morral el maíz bendito de la Candelaria.",
                Parrafo.TRABAJO to "A esta hora siembra: abre el surco con la coa, echa cuatro granos en cada hoyo, uno para el pájaro, uno para la hormiga, uno para que se pudra y uno para que nazca, y los tapa con el pie. Petra va detrás echando frijol y calabaza, que crecen juntos.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que esta tierra no es suya, y que el maíz que siembra sí lo es, por lo menos en parte. Si llueve bien, comerán todo el año sin deber en la tienda. Si no llueve, deberán. Mira el cielo, que está despejado, y sigue sembrando.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora, después de los magueyes, siembra el pegujal con lo que le queda de luz. Chencho le ayuda a echar los granos, y los cuenta en voz alta para no equivocarse, y se equivoca.",
                "A esta hora siembra el último pedazo del pegujal. Los otros peones también siembran los suyos, al lado, y se gritan de surco a surco quién va más adelantado.",
                "A esta hora mira lo que ya sembró: la tierra removida, oscura, con los hoyos tapados en hileras. Todavía no se ve nada. Pero lo que no se ve es lo que más le importa.",
            )),
        ),
        "san_antonio" to unDia(
            Parrafo.MANANA to "Hoy es día de San Antonio, el santo de la hacienda, y es la fiesta grande del año. Desde la madrugada truenan los cohetes en el patio, y en la capilla, adornada con flores y papel picado, el cura dice misa cantada. El patrón, cuando viene, se sienta adelante.",
            Parrafo.PUEBLO to "Después de la misa hay jaripeo en el corral grande: los vaqueros de la hacienda montan toros sin ensillar, y los peones gritan desde la cerca. Hay músicos de cuerda, puestos de dulces y de comida, y la hacienda reparte barbacoa y pulque a todos.",
            Parrafo.GENTE to "Las muchachas solteras le piden novio a San Antonio, y algunas, para obligarlo, lo ponen de cabeza en un rincón hasta que se los da. Lupe le pregunta a su madre si ella también le pidió novio al santo. Petra dice que no, que le pidió otra cosa, y mira a {nombre}.",
        ),
        "escarda" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la escarda en el pegujal: la milpa ya creció un palmo y hay que arrimarle tierra al pie, para que no la tumbe el viento, y quitarle las hierbas que le roban el agua. {nombre} va al pegujal después del tinacal, con la coa al hombro.",
                Parrafo.TRABAJO to "A esta hora escarda la milpa: con la coa jala la tierra hacia cada mata, formando un montoncito, y arranca a mano las hierbas que no sirven. Las que sí sirven, los quelites, las deja: son la comida de la semana.",
                Parrafo.PENSAMIENTO to "{nombre} mira la milpa, verde y derecha, y piensa que no hay en la hacienda nada más bonito que el maíz propio creciendo. Los magueyes son del patrón; esto, por lo menos mientras crece, parece suyo.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue escardando el pegujal, surco por surco, doblado. Se le llenan las uñas de tierra negra, que es buena señal.",
                "A esta hora escarda con Chencho, que arranca las hierbas buenas y las malas por igual, y {nombre} le enseña a distinguirlas una por una, hoja por hoja.",
                "A esta hora termina la escarda del pegujal y se queda mirándolo con las manos en la cintura. Ahora le toca al cielo.",
            )),
        ),
        "patrias" to TextoEspecial(
            listOf(
                dia(
                    Parrafo.MANANA to "Hoy es quince de septiembre, víspera de la fiesta de la Independencia, y también el santo de don Porfirio, que cumple años. En la hacienda se cuelgan banderas de papel verde, blanco y rojo en los portales de la casa grande, y los peones trabajan medio día.",
                    Parrafo.PUEBLO to "Por la noche, en el patio, el administrador sale al balcón de la casa grande, toca una campana y grita los vivas: a los héroes, a la patria, a don Porfirio. Los peones contestan desde abajo, con los sombreros en la mano. Luego hay cohetes y pulque.",
                    Parrafo.PENSAMIENTO to "{nombre} grita viva México con los demás, de corazón, aunque no sabe bien qué es México más allá del llano. Sabe que es grande, que tiene volcanes y una capital con tranvías, y que también es suyo, dicen. Grita más fuerte, por si acaso.",
                ),
                dia(
                    Parrafo.MANANA to "Hoy es dieciséis de septiembre, el día de la patria, y no se trabaja. En el pueblo de abajo hay desfile de los niños de la escuela y de los rurales a caballo, y algunos peones bajan a verlo con sus familias, caminando por el llano desde temprano.",
                    Parrafo.GENTE to "{nombre} baja al pueblo con Petra y los niños, que estrenan huaraches. En la plaza hay puestos de comida, músicos, un palo encebado con un premio arriba y un hombre que vende banderitas. Chencho intenta subir al palo y se resbala a la primera, entre risas.",
                    Parrafo.ANOCHECER to "Esta noche, de vuelta en la hacienda, los niños se duermen en cuanto llegan, con las banderitas en la mano. {nombre} se sienta en la puerta y mira hacia el pueblo, donde todavía truenan los cohetes. Mañana se raspa otra vez. Pero hoy fue un buen día.",
                ),
            ),
        ),
        "muertos" to TextoEspecial(
            listOf(
                dia(
                    Parrafo.MANANA to "Hoy es día de Todos Santos, cuando vuelven los muertos niños, y Petra pone el altar en el rincón del cuarto: una mesa con un mantel bordado, flores de cempasúchil, velas, pan, dulces de calabaza y juguetes de barro para los angelitos de la familia.",
                    Parrafo.PUEBLO to "En toda la hilera de cuartos huele a cempasúchil y a copal, que se quema en braseros de barro. Las flores amarillas forman caminos desde la puerta hasta el altar, para que los muertos no se pierdan. Los niños vivos miran los dulces de los muertos y no los tocan.",
                    Parrafo.PENSAMIENTO to "{nombre} piensa en los suyos que se fueron chiquitos, y los imagina entrando por la puerta, siguiendo las flores, para comerse los dulces que Petra les hizo. Les deja también un jarrito de atole. Los muertos niños, piensa, también tienen frío en el llano.",
                ),
                dia(
                    Parrafo.MANANA to "Hoy es día de los fieles difuntos, cuando vuelven los muertos grandes, y en el altar se cambian los juguetes por lo que les gustaba en vida: tortillas, mole, un jarro de pulque, un cigarro. Las velas arden desde la madrugada.",
                    Parrafo.PUEBLO to "Por la tarde las familias de la hacienda van al camposanto, junto a la capilla, a limpiar las tumbas y a cubrirlas de flores. Se quedan ahí hasta la noche, comiendo, rezando y platicando con los muertos, como si estuvieran de visita. Algunos se emborrachan, también como en vida.",
                    Parrafo.ANOCHECER to "Esta noche el camposanto brilla de velas, y desde la puerta de la hacienda parece un pueblo pequeño encendido en medio del llano. {nombre} vuelve tarde, con el sarape oliendo a copal, y se duerme pensando que sus muertos están contentos. Él también.",
                ),
            ),
        ),
        "pizca" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la pizca del maíz del pegujal. La milpa está seca y amarilla, y las mazorcas cuelgan pesadas dentro de sus hojas. {nombre} sale a pizcar después de los magueyes, con un costal al hombro y un pizcador de hueso atado a la mano.",
                Parrafo.TRABAJO to "A esta hora pizca: con el pizcador rasga la hoja de la mazorca, la desnuda y la arranca de un tirón, y la echa al costal. Toda la familia trabaja, cada uno en un surco, y Lupe va detrás recogiendo las que se caen.",
                Parrafo.PENSAMIENTO to "{nombre} pesa el costal en la mano y hace cuentas: con esto se come hasta la siembra, si nadie se enferma y si la tienda no cobra lo de antes. No se sabe nunca. Pero esta noche, por lo menos, el maíz es suyo.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue la pizca del pegujal, surco tras surco, con las manos cortadas por las hojas secas del maíz. Las mazorcas buenas van a un costal y las podridas a otro, para los guajolotes.",
                "A esta hora desgrana en la puerta del cuarto las mazorcas de ayer, frotándolas una contra otra, y los granos caen en un petate como lluvia amarilla. Los olotes se guardan para el fuego.",
                "A esta hora lleva al cuarto los costales llenos, uno por uno, en el burro. El maíz se guarda en un rincón, en un cuescomate de barro, lejos de los ratones y de los ojos del administrador.",
                "A esta hora corta las cañas secas de la milpa para dárselas al burro, que es lo último que queda en el pegujal. Después, la tierra se queda pelona hasta las aguas.",
            )),
        ),
        "guadalupe" to unDia(
            Parrafo.MANANA to "Hoy es día de la Virgen de Guadalupe, y antes del amanecer los peones le cantan las mañanitas en la capilla, con guitarras y velas, y truenan cohetes en el patio que despiertan a los perros de tres haciendas. Petra se puso su rebozo bueno.",
            Parrafo.PUEBLO to "Por el camino real pasan desde hace días los peregrinos que van a pie a la Villa, en la capital, a ver a la Virgen en su basílica: familias enteras, con estandartes, niños vestidos de indito y viejos que caminan de rodillas el último tramo. Algunos de la hacienda van con ellos.",
            Parrafo.PENSAMIENTO to "{nombre} nunca ha ido a la Villa, y le gustaría ir un año, a pie, con los peregrinos, aunque sean tres días de camino. La Virgen se le apareció a un indio pobre, piensa, y no a un hacendado. Eso le parece lo más importante que se sabe de ella.",
        ),
        "posadas" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las posadas, las nueve noches antes de Navidad en que se recuerda cómo José y María pedían posada en Belén. Cada noche le toca a una familia distinta de la hilera hacer la fiesta, y todos ponen algo: velas, cacahuates, colación, pulque.",
                Parrafo.PUEBLO to "Al anochecer los peones salen en procesión con velas, cargando a los santos peregrinos, y se dividen: unos se quedan afuera de un cuarto y piden posada cantando, y los de adentro les contestan, cantando, que no. A la tercera puerta les abren, y entran todos.",
                Parrafo.ANOCHECER to "Esta noche se rompe la piñata en el patio, una olla de barro cubierta de papel de colores y rellena de fruta y cacahuates, y los niños le pegan con un palo, con los ojos vendados, mientras los grandes cantan. Chencho le da al aire, y luego a la olla.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "Esta noche la posada es en otro cuarto de la hilera, y se canta lo mismo que anoche, con las mismas voces desafinadas. La piñata es más pequeña, y la fruta, más poca.",
                "Esta noche la posada la hace el compadre de un vecino, que trajo músicos del pueblo de abajo. Se baila en el patio hasta tarde, con frío, y nadie lo nota.",
                "Esta noche es la última posada, la de Nochebuena, y después se va a la misa de gallo en la capilla, a medianoche, con velas. Al salir, el frío del llano corta la cara, y las estrellas parecen más.",
            )),
        ),
        "deuda" to unDia(
            Parrafo.MANANA to "Hoy {nombre} fue a la tienda de raya por manta para una camisa y unos huaraches para Chencho, que ya andaba descalzo. No tenía vales. El dependiente le apuntó todo en el libro grande, con letra bonita, junto a lo que ya debía.",
            Parrafo.GENTE to "Petra le preguntó cuánto debían ahora, y él le dijo la cifra que le dijo el dependiente, que no sabe si es verdad, porque no sabe leer el libro. Petra hizo cuentas con frijoles sobre el petate. Le salió otra cifra, más chica. Ninguno de los dos dijo nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su padre le dejó una deuda y que él le va a dejar otra a Chencho, más grande, y que así es la hacienda: una cadena que no se ve. Piensa también que Chencho tiene huaraches nuevos. Las dos cosas son verdad a la vez.",
        ),
        "patron_llega" to unDia(
            Parrafo.MANANA to "Hoy llegó el patrón de la capital, para la fiesta del santo. Vino en el tren de la mañana, y desde la estación en coche de caballos, con su señora, sus hijos, sus invitados y un baúl detrás de otro. Los peones lo esperaron formados en el patio, con los sombreros en la mano.",
            Parrafo.PUEBLO to "Toda la hacienda se puso de fiesta para recibirlo: barrieron el patio, encalaron los portales, pusieron flores en la capilla. En la casa grande se abrieron las ventanas que están cerradas todo el año, y se oyó un piano, que {nombre} no había oído nunca.",
            Parrafo.GENTE to "El patrón pasó delante de los peones saludando con la mano, sin detenerse, y le preguntó al administrador cómo iba el pulque. A {nombre} le pareció más viejo que la última vez. Uno de los hijos del patrón, de la edad de Chencho, llevaba zapatos que brillaban como espejos.",
        ),
        "chencho_magueyes" to unDia(
            Parrafo.MANANA to "Hoy Chencho acompañó a su padre por primera vez a los magueyes, a la hora del alabado, con un acocote chico que le hizo {nombre} con un guaje pequeño. El niño estaba despierto antes que nadie, sentado en el petate, esperando.",
            Parrafo.GENTE to "En el potrero, {nombre} le enseña a Chencho a chupar el aguamiel con el acocote, sin tragárselo y sin que se le salga por la nariz. El niño se atraganta a la primera, tose y se ríe, y a la tercera ya lo hace, y vacía su poquito de aguamiel en el odre de su padre con mucho cuidado.",
            Parrafo.PENSAMIENTO to "{nombre} mira a su hijo entre los magueyes, chiquito, con el acocote más largo que su brazo, y piensa en su propio padre enseñándole a él en este mismo potrero. Le da orgullo y le da algo más, que no sabe nombrar, y que se parece a la tristeza.",
        ),
        "granizada" to unDia(
            Parrafo.MANANA to "Hoy, a media mañana, se puso negro el cielo sobre el cerro y cayó una granizada como no se veía en años: piedras de hielo del tamaño de garbanzos, que rebotaban en la tierra y sonaban en las pencas como balazos. Los tlachiqueros se refugiaron bajo los magueyes más grandes.",
            Parrafo.PUEBLO to "Cuando pasó, el llano estaba blanco como si hubiera nevado, y las pencas de los magueyes, peladas y rotas, lloraban un jugo verde. En la casa grande el administrador salió a ver los daños, con las manos en la espalda, y apuntó algo en una libreta.",
            Parrafo.PENSAMIENTO to "{nombre} mira los magueyes heridos y piensa en la milpa del pegujal, que salió mejor librada porque está más abajo, junto a la acequia. Hoy da gracias por eso. Luego piensa que el administrador va a decir que la granizada fue culpa de los peones, de algún modo.",
        ),
        "petra_espera" to unDia(
            Parrafo.MANANA to "Hoy, mientras le echaba las tortillas del itacate, Petra le dijo que espera otro hijo. Se lo dijo sin levantar la vista del comal, como se dice que va a llover. {nombre} se quedó parado en la puerta con el acocote en la mano y el burro esperando.",
            Parrafo.GENTE to "Su madre, doña Refugio, ya lo sabía desde hacía días, por cómo comía Petra, y lo dijo en voz alta para que lo oyeran todos. Lupe pregunta si va a ser niña, y Chencho dice que mejor niño, para que le ayude con el burro. Petra dice que va a ser lo que Dios quiera.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en otra boca y en otra deuda, y hace cuentas en el camino a los magueyes. No le salen. Luego piensa en una cara nueva en el petate, en una risa más en el cuarto, y las cuentas le dejan de importar. Hoy raspa contento.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "hacienda", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + HERIDO.take(1),
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + HERIDO.subList(1, 4),
            Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE + HERIDO[4],
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
