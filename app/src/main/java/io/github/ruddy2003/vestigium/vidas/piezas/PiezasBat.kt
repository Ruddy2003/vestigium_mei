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
 * El cuento de Bat, pastor nómada a orillas del Kerulen, en la estepa mongola, de 1220 a 1224. Sigue la gramática y el
 * tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día es del ganado: ordeña al alba, pastorea a caballo hasta el anochecer y por la noche se bebe y se canta.
 * - La mayor parte del año vive en el valle de invierno, al abrigo del cerro; cada verano el campamento sube a los
 *   pastos del llano, junto al río (estado «verano»), donde se ordeñan las yeguas y se hace el airag. Cada hora del
 *   verano tiene sus variantes, y también la mañana, el pueblo, el tiempo y el anochecer.
 * - El pueblo lee el arco «pasto» (la hierba). El invierno del hielo (temporada 4) se nota en los textos con fecha.
 * - No repite lo que ya dice «Su vida»: que casi no camina, las cuatro mudanzas del año, la carne y la leche en todas
 *   sus formas, el airag a litros, las pocas familias del campamento, la cabalgada hacia el oeste y el canto con la
 *   garganta.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG4.kt`):
 * - Khulan espera desde `khulan_espera` (temporada 1); Saran nace en `nace_saran` (2).
 * - El potro blanco nace en `potro` (temporada 1).
 * - Su madre enferma en `madre_tos` (temporada 3) y muere en `muere_madre` (4).
 * - Temür, su hermano, está en la guerra hasta `vuelve_temur` (temporada 5).
 * - Las fiestas que vuelven cada año no nombran a Saran, a su madre, a Temür ni al potro blanco.
 */
internal object PiezasBat {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta en la tienda de fieltro con el frío en la cara y el calor de las pieles en el cuerpo. Khulan ya está junto al fuego del centro, echando boñiga seca a las brasas. El humo sube derecho por el agujero del techo, hacia el cielo, que todavía es azul oscuro."),
        v("A {nombre} lo despiertan los perros, que ladran a algo en la oscuridad: un lobo, un zorro, el viento. Sale de la tienda con el arco, descalzo en la nieve dura, y mira. No hay nada. Las ovejas están apretadas en el corral, quietas. Vuelve a entrar con los pies quemados de frío.", meses = setOf(11, 12, 1, 2, 3)),
        v("Lo primero del día, antes de salir, {nombre} toma un cuenco de té hervido con leche, sal y un poco de mantequilla, que le da Khulan sin decir nada. Se lo bebe de pie junto a la puerta, mirando el cielo del este. Luego sale. Al salir no pisa el umbral, que trae desgracia."),
        v("{nombre} sale de la tienda al amanecer y echa unas gotas de leche hacia el cielo, hacia la tierra y hacia los cuatro lados, como hacía su padre, para el Cielo Azul y para los espíritus del lugar. Luego mira el cerro, el río, el corral. Todo en su sitio."),
        v("A {nombre} lo despierta Mönkh, que ya está vestido con su abrigo de piel y quiere ir con él a los caballos. Tiene siete años y monta mejor que muchos hombres del oeste, piensa {nombre}, que los vio montar. Se levanta. Hoy irán juntos."),
        v("{nombre} se despierta antes que nadie y se queda quieto escuchando la tienda: la respiración de los suyos, el viento en el fieltro, el crujido de los palos del techo, que se mueven como si la tienda respirara también. Afuera, un caballo resopla. Es la hora."),
    )

    /** El verano en los pastos del llano, junto al río (estado «verano»). */
    private fun ve(texto: String, hacer: Hacer? = null) = v(texto, estado = "verano", hacer = hacer)

    private val VERANO_MANANA = listOf(
        ve("{nombre} se despierta en la tienda del campamento de verano, junto al río, con la puerta abierta para que entre el fresco. Fuera ya cantan las alondras. Las yeguas están atadas a la cuerda larga, con sus potros al lado, esperando el ordeño. El cielo es enorme."),
        ve("A {nombre} lo despiertan las moscas y la luz, que en verano llegan temprano. Sale de la tienda y la estepa está verde hasta donde se ve, con flores amarillas y moradas, y las manadas de los otros campamentos son puntos lejos. Huele a hierba y a leche agria."),
        ve("{nombre} sale de la tienda al amanecer con un cuenco de airag del día anterior y echa unas gotas hacia el cielo, hacia la tierra y hacia los cuatro lados. En verano la ofrenda es de airag, que es lo mejor que hay. Luego se bebe el resto. Hoy hace calor."),
    )

    private val VERANO_TRABAJO = listOf(
        ve("A esta hora ordeña las yeguas con Khulan: él suelta al potro para que la yegua dé la leche, lo aparta, y Khulan, agachada, ordeña rápido en un cubo de cuero. Una yegua da poca leche cada vez, así que se ordeña muchas veces al día. Es el trabajo del verano.", Hacer.CUIDAR),
        ve("A esta hora ordeña las ovejas y las cabras, que en verano dan leche para hacer queso, cuajada, nata y la leche seca que se guarda para el invierno. Las mujeres ordeñan y los hombres sujetan. {nombre} sujeta una cabra que no quiere, y la cabra gana.", Hacer.CUIDAR),
        ve("A esta hora bate el airag en el odre grande de cuero que cuelga junto a la puerta de la tienda: cada uno que entra o sale le da unos golpes con el palo, cientos al día, y así la leche de yegua se pone agria, burbujea y se convierte en lo que se bebe en verano.", Hacer.CUIDAR),
        ve("A esta hora pastorea los caballos en el llano junto al río, a caballo, con el lazo en la mano, mirando que ninguno se aleje y que los sementales no se peleen. La hierba les llega a las rodillas. Los potros juegan. {nombre} se adormece en la silla, y el Negro sigue solo.", Hacer.TRABAJO),
        ve("A esta hora caza marmotas en las laderas del llano con Mönkh, que es lo que se hace a fines del verano, cuando están gordas: se acercan agitando un trapo blanco, que a las marmotas les da curiosidad, y se les tira la flecha. La carne se asa con piedras calientes dentro.", Hacer.TRABAJO),
        ve("A esta hora lleva las ovejas a beber al río, despacio, para que no se amontonen en la orilla. El agua está fría aunque sea verano, porque viene de las montañas. Las ovejas beben, se mojan las patas y balan. {nombre} se lava la cara y la nuca.", Hacer.TRABAJO),
        ve("A esta hora come carne de oveja hervida, sentado en la hierba fuera de la tienda, con la mano y el cuchillo, y bebe airag a grandes tragos del cuenco de madera. En verano no se come casi otra cosa que leche y carne, y se bebe airag hasta que se cae uno.", Hacer.COMER),
        ve("A esta hora come cuajada fresca con nata y un trozo de carne seca del invierno, que ya hay que terminar. Mönkh come a su lado, imitándolo: el mismo cuchillo, el mismo gesto, la misma cara. Khulan los mira y se ríe de los dos.", Hacer.COMER),
        ve("A esta hora come a caballo, sin bajarse, un puñado de leche seca y un trago del odre que lleva en la silla, mirando el rebaño. En la estepa, dice su padre, el que se baja a comer pierde las ovejas. Su padre exageraba. Pero no se baja.", Hacer.COMER),
        ve("A esta hora está con los del campamento fuera de las tiendas, en la hierba, bebiendo airag y cantando, porque en verano la noche tarda en llegar. Bolad canta con la garganta dos notas a la vez. Los niños corren entre las yeguas. Todo el mundo está un poco borracho.", Hacer.GENTE),
        ve("A esta hora juegan a la lucha en el pasto, los hombres del campamento, con el torso desnudo, agarrándose de los hombros, hasta que uno cae. Bolad gana siempre. {nombre} lo tumba una vez cada verano, y lo cuenta el resto del año.", Hacer.GENTE),
        ve("A esta hora llegan jinetes de otro campamento a beber airag y a contar noticias, y se quedan hasta que oscurece. Se habla del kan, de la guerra del oeste, de quién se casa con quién, de dónde hay buen pasto. {nombre} escucha más de lo que habla.", Hacer.GENTE),
        ve("A esta hora duerme en la tienda con la puerta abierta, porque en verano dentro hace calor. Se oyen los caballos, los perros, el río. Los mosquitos zumban. {nombre} se tapa la cara con el sombrero de fieltro y se duerme igual.", Hacer.DORMIR),
        ve("A esta hora duerme fuera de la tienda, sobre una piel, mirando las estrellas, que en el llano están tan cerca que parece que se pueden coger. El río suena. Mönkh duerme a su lado, con la boca abierta. Un caballo resopla en la oscuridad.", Hacer.DORMIR),
        ve("A esta hora duerme, borracho de airag, y sueña con caballos que corren por el cielo. Se despierta con sed y sale a beber agua del río. La noche de verano es corta: ya se ve una raya clara por el este. Vuelve a dormir un rato.", Hacer.DORMIR),
    )

    private val VERANO_PUEBLO = listOf(
        ve("El campamento de verano son seis tiendas en el llano abierto, junto al río, con la puerta al sur, y entre ellas la cuerda larga donde se atan las yeguas. Alrededor, la estepa verde, sin un árbol. Las carretas descansan con las varas apoyadas en el suelo."),
        ve("En el llano del verano se ven a lo lejos los humos de otros campamentos, cada uno junto a un recodo del río, a medio día uno de otro. Los pastos son de todos y de nadie. Si un campamento se acerca demasiado, se discute; si es de parientes, se bebe."),
        ve("El río en verano es ancho y lento, con orillas de hierba y de piedras, y bancos de arena donde los niños se bañan y los caballos beben. Hay garzas, patos, y peces que nadie pesca. Es el centro del verano: todo está cerca del río."),
    )

    private val VERANO_ANOCHECER = listOf(
        ve("Al caer la tarde, en el llano, el sol se va despacio, rojo y enorme, y el cielo tarda mucho en oscurecer. {nombre} encierra las ovejas, ata los caballos y se sienta fuera de la tienda con un cuenco de airag. No piensa en nada. Es lo que más le gusta del verano."),
        ve("Anochece en el campamento de verano, y alguien toca el violín de cabeza de caballo junto a un fuego, una música que suena como el viento cuando pasa por la crin de un caballo. Los demás escuchan callados. {nombre} cierra los ojos."),
        ve("Al anochecer vuelven los que estaban lejos con los caballos, y el campamento se llena de ruido: relinchos, perros, gritos, niños. Las mujeres reparten la carne. Los hombres beben. Arriba, la primera estrella, sola. {nombre} la saluda."),
    )

    private val VERANO_TIEMPO = listOf(
        ve("En el llano del verano el cielo es tan grande que se ve llegar la lluvia desde un día de camino. {cielo} {de_estacion}"),
        ve("{cielo} {de_estacion} En verano la estepa cambia de un día a otro: un día verde, otro amarilla, otro llena de flores."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la estepa el tiempo no se mira: se sufre o se disfruta, y siempre llega antes que la noticia."),
        v("{nombre} mira el cielo desde lo alto del cerro, sobre el valle. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El viejo chamán dice que el Cielo Azul se enfada cuando alguien orina en el agua, y que entonces manda granizo."),
        v("{cielo} En {estacion} los animales lo saben antes que la gente, y {nombre} los mira para saber qué viene."),
        v("Desde lo alto del cerro se ve la estepa entera, ondulada, y el río que la cruza como un hilo de plata, y a lo lejos, las montañas azules. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Su padre decía que el tiempo de la estepa es como un caballo salvaje: nunca se sabe por dónde va a salir."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está azul de lado a lado, sin una nube, ese azul que solo hay en la estepa.",
            "Hace un día claro y el sol brilla tanto que la nieve, o la hierba, parece de cristal.",
            "No hay una nube, y se ve tan lejos que las montañas parecen al alcance de la mano.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris, bajo, y la estepa parece más pequeña.",
            "Hay nubes grandes que corren sobre la estepa y dejan sombras que corren por el suelo.",
            "Está nublado, y la luz es pareja y triste.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y la estepa huele a hierba mojada y a tierra.",
            "Cae una lluvia fina que empapa el fieltro de las tiendas.",
            "Llueve, y el río crece y se pone del color del té.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los rayos caen en el llano y los caballos se juntan, asustados.",
            "El cielo se pone negro de golpe y cae granizo grande como huevos de pájaro.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y la estepa se queda blanca y callada, y las ovejas buscan la hierba debajo.",
            "Cae nieve fina y seca, que el viento levanta y lleva de un lado a otro.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento de la estepa, que no tiene nada que lo pare, y el fieltro de la tienda golpea.",
            "Hay viento, y los caballos se ponen de espaldas a él, con la cola entre las patas.",
        ),
        Clima.POLVO to listOf(
            "Sopla un viento seco que levanta polvo y arena, y el sol es una mancha amarilla.",
            "Hay polvo en el aire, y se mete en los ojos, en la comida y en el airag.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando nacen los corderos y la hierba tarda en salir.",
            "En primavera la estepa es todavía parda, y los animales están flacos después del invierno.",
            "Es el tiempo de los vientos y del polvo, cuando la nieve se va y la hierba no llega.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, el tiempo corto de la hierba alta, de la leche y del airag.",
            "En verano los animales engordan y la gente también.",
            "Es el tiempo de las moscas, de las tormentas y de las fiestas.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando la hierba se seca y se hace el fieltro.",
            "En otoño los animales están gordos y se prepara la carne del invierno.",
            "Es el tiempo de las primeras heladas y de las cacerías.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, el tiempo largo del frío, cuando la leche se hiela en el cuenco.",
            "En invierno el campamento se esconde del viento al abrigo del cerro.",
            "Es el tiempo en que los animales viven de lo que comieron en verano, y la gente también.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora pastorea las ovejas y las cabras a caballo, por las laderas del valle, buscando la mejor hierba que quede. El rebaño se mueve despacio, como una nube gris en el suelo. {nombre} silba, y los perros lo entienden.", hacer = Hacer.TRABAJO),
        v("A esta hora busca los caballos, que se fueron lejos durante la noche, siguiendo sus huellas en la tierra helada. Los encuentra detrás de una loma, juntos, con la crin llena de escarcha. Coge uno con el lazo largo y lo monta a pelo para traer a los demás.", hacer = Hacer.TRABAJO),
        v("A esta hora doma un potro de dos años en el corral, con Bolad: uno lo sujeta con el lazo y el otro le pone la silla y se sube. El potro salta, gira, se tira al suelo. {nombre} cae dos veces. A la tercera, se queda arriba. Mañana, otra vez.", hacer = Hacer.TRABAJO),
        v("A esta hora recoge boñiga seca en la ladera con un cesto a la espalda y un rastrillo de madera, que es el fuego de la estepa, porque no hay árboles. La de vaca es la mejor; la de caballo arde rápido; la de oveja, en invierno, calienta como ninguna.", hacer = Hacer.TRABAJO),
        v("A esta hora vigila el rebaño desde lo alto de una loma, quieto en el caballo, con el arco en la mano, porque ayer se vieron huellas de lobos cerca del corral. Los lobos son pacientes. {nombre} también. Se miran desde lejos, sin verse.", hacer = Hacer.TRABAJO),
        v("A esta hora repara la cerca del corral de invierno, que es de piedras y estiércol amontonados, donde el viento hizo un agujero. Trabaja con las manos desnudas y frías, y se las calienta con el aliento. Las ovejas lo miran trabajar sin ayudar.", hacer = Hacer.TRABAJO),

        v("A esta hora ordeña las vacas y las cabras con Khulan, en el corral, con el cubo de cuero entre las piernas y las manos frías. La leche sale caliente y humea en el aire helado. Una parte se hierve para el té; otra se cuaja; otra se deja agriar.", hacer = Hacer.CUIDAR),
        v("A esta hora da de comer a los corderos y cabritos más débiles, que se crían dentro de la tienda, junto al fuego, en un rincón con paja. Les da leche con un cuerno. Mönkh les ha puesto nombre a todos. {nombre} le dice que no se encariñe. Él se encariña igual.", hacer = Hacer.CUIDAR),
        v("A esta hora cuida a una yegua que está por parir, en el corral, mirándola, tocándole la tripa, hablándole bajito. Si hay problemas, tendrá que meter la mano. No hay problemas: el potro sale, se cae, se levanta y busca la teta. {nombre} sonríe.", hacer = Hacer.CUIDAR),

        v("A esta hora come carne de oveja hervida en la olla grande, con el caldo, sentado en el lado de los hombres de la tienda, cortando la carne con el cuchillo junto a la boca. La grasa es lo mejor: da calor. El hueso con tuétano es para el más viejo.", hacer = Hacer.COMER),
        v("A esta hora come leche seca, dura como piedra, que se chupa durante horas, y un trozo de carne seca, y bebe té salado con leche de un termo de cuero. Es la comida del pastor: lo que cabe en la bolsa de la silla. {nombre} no necesita más.", hacer = Hacer.COMER),
        v("A esta hora come lo que Khulan le pone en el cuenco: un caldo con trozos de carne y unos fideos de harina que cambió a unos mercaderes del sur. Mönkh sorbe tan fuerte que los perros levantan la cabeza. {nombre} sorbe igual de fuerte, para que aprenda.", hacer = Hacer.COMER),

        v("A esta hora está en la tienda con los suyos, alrededor del fuego, contando historias: de su abuelo, que robó caballos a los tártaros; de los espíritus del río; del kan, que de niño fue cautivo. Mönkh pregunta si es verdad. {nombre} le dice que las historias no son verdad ni mentira: son historias.", hacer = Hacer.GENTE),
        v("A esta hora visita la tienda de Bolad, con un odre de leche fermentada, y se sientan en el lado de los hombres a beber y a hablar de caballos. Bolad dice que su yegua torda es la más rápida del valle. {nombre} dice que no. Discuten hasta que se acaba el odre.", hacer = Hacer.GENTE),
        v("A esta hora juega a los huesos de tobillo con Mönkh junto al fuego: se tiran como dados y, según caen, son caballo, camello, oveja o cabra. Mönkh hace trampa. {nombre} lo deja ganar y luego le gana tres veces seguidas, para que aprenda a perder.", hacer = Hacer.GENTE),

        v("A esta hora duerme en la tienda, en el lado de los hombres, envuelto en pieles de oveja con el pelo hacia dentro, con Khulan y los niños al lado. El fuego se apaga despacio. El viento empuja el fieltro. Los perros ladran a algo, y luego callan.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con la guerra del oeste: con una ciudad de muros de barro, con un río ancho, con el fuego. Se despierta sudando, aunque haga frío. Oye a Khulan respirar. Se vuelve a dormir, despacio, contando caballos.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, cansado de la silla, con las piernas todavía dobladas como si montara. En la estepa se duerme como se monta: con un ojo abierto. Un ruido en el corral y ya está de pie. Hoy no hay ruido.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El campamento de invierno son seis tiendas en el fondo del valle, al abrigo del cerro, que para el viento del norte. Cada familia tiene la suya, con la puerta al sur. Detrás, los corrales de piedra y estiércol. Delante, el río, helado la mitad del año. {entorno}"),
        v("{entorno} La tienda de {nombre} es redonda, de fieltro blanco sobre una celosía de varas, con el fuego en el centro y un agujero arriba por donde sale el humo y entra la luz. El lado de los hombres a la izquierda de la puerta, el de las mujeres a la derecha, y al fondo el sitio de honor."),
        v("En el campamento viven seis familias, todas parientes de una manera u otra, con unos mil animales entre todas: ovejas, cabras, caballos, vacas y unos cuantos camellos. Los animales son la riqueza, el alimento, el fuego y la ropa. Sin ellos no hay nada. {entorno}"),
        v("Alrededor del campamento, la estepa se extiende en todas direcciones, ondulada, sin un árbol, hasta las montañas lejanas. Por ella pasan los jinetes del kan, los mercaderes del sur, los cazadores y los lobos. {entorno}"),
        v("{entorno} En lo alto del cerro hay un montón de piedras con palos y cintas azules, el ovoo, donde viven los espíritus del lugar. Cada vez que alguien pasa, deja una piedra y da tres vueltas. Desde allí se ve todo el valle."),
        v("Junto a las tiendas están las carretas, con las ruedas de madera altas como un hombre, cargadas con los cofres, las pieles y lo que no se usa ahora. Cuando el campamento se muda, todo cabe en ellas, hasta las tiendas. {entorno}"),
    )

    /** La hierba de la estepa (arco «pasto»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("pasto", 0.0, 0.05, frases = listOf(
            "La estepa está parda y seca, o blanca de nieve, y la hierba vieja cruje bajo los cascos.",
            "No hay hierba nueva: los animales comen lo que queda del verano pasado, que es poco.",
        )),
        Entorno("pasto", 0.05, 0.5, frases = listOf(
            "La hierba está corta y rala, ni verde del todo ni seca del todo.",
            "Hay hierba, pero poca: verde en las hondonadas y amarilla en las lomas, y los animales la buscan cabeza abajo.",
        )),
        Entorno("pasto", 0.5, 1.0, frases = listOf(
            "La hierba está alta y verde, y los animales engordan a ojos vistas.",
            "La estepa es un mar de hierba que se mueve con el viento, con flores de colores.",
        )),
    )

    private val GENTE = listOf(
        v("Khulan, su mujer, es más fuerte que él y lo sabe: monta, ordeña, levanta la tienda sola y discute con los mercaderes hasta que lloran. Lleva el campamento cuando él no está. Cuando él volvió de la guerra, le dijo que había engordado. Es su manera de decir que lo quiere."),
        v("Mönkh, su hijo, tiene los ojos de su madre y la cabeza dura de su padre. Monta desde los tres años, tira con un arco pequeño y quiere ir a la guerra, porque cree que es como cazar. {nombre} no le cuenta lo que vio. Todavía no."),
        v("Bolad es su vecino, su primo lejano y su mejor amigo, aunque se pelean por los caballos cada semana. Es gordo, fuerte y canta con la garganta dos notas a la vez. No fue a la guerra porque se rompió una pierna. Dice que fue a propósito. No fue a propósito."),
        v("El viejo chamán del valle vive solo en una tienda pequeña, con tambores y figuras de fieltro colgadas, y habla con los espíritus del río, del cerro y de los antepasados. Cura a los animales y a la gente, y lee el futuro en los omóplatos de oveja quemados. Nadie le lleva la contraria."),
        v("El Negro es el caballo de {nombre}: pequeño, peludo, feo y más resistente que cualquier caballo del oeste. Lo llevó a la guerra y lo trajo de vuelta. Come nieve cuando no hay agua y hierba seca cuando no hay otra. {nombre} le debe la vida dos veces."),
        v("La madre de {nombre} vive en su propia tienda, junto a la de él, y manda en todo el campamento sin levantar la voz. Tiene la cara arrugada como una manzana seca y los ojos más vivos del valle. Sabe los nombres de todos los antepasados, y cuál robó a cuál.", hasta = "madre_tos"),
        v("La madre de {nombre} está enferma desde el invierno, con una tos que no se va. Ya no sale de su tienda. Khulan le lleva caldo y el chamán le toca el tambor. {nombre} se sienta con ella cada noche, y ella le cuenta historias de su padre que no sabía.", desde = "madre_tos", hasta = "muere_madre"),
        v("La tienda de la madre de {nombre} ya no está: se quemó con sus cosas, como se hace, y su lugar en el campamento está vacío. {nombre} todavía mira hacia allí cada mañana al salir. Khulan lleva ahora su cuchillo de mango de hueso.", desde = "muere_madre"),
        v("Temür, su hermano menor, sigue en la guerra del oeste, con el ejército del kan, desde hace años. No se sabe nada de él más que lo que cuentan los que vuelven: que está vivo, que está lejos, que es valiente. Su madre deja cada noche un cuenco de leche para él.", hasta = "vuelve_temur"),
        v("Temür, su hermano, volvió de la guerra después de cinco años, con cicatrices, con caballos ajenos y sin ganas de hablar de lo que vio. Vive en una tienda junto a la de {nombre}. A veces se sientan juntos a mirar el río sin decir nada, que es lo que hacen los que volvieron.", desde = "vuelve_temur"),
        v("Khulan espera un hijo, y sigue montando, ordeñando y levantando la tienda como si nada, aunque {nombre} le pide que no. Las mujeres le dicen que será niña, por cómo le brillan los ojos. El chamán dice que será lo que el Cielo quiera.", desde = "khulan_espera", hasta = "nace_saran"),
        v("Saran, su hija, es pequeña, redonda y gritona, y la llevan atada en una cuna de madera en la carreta, o a la espalda de su madre a caballo. Se ríe cuando el caballo trota. {nombre} piensa que montará mejor que todos.", desde = "nace_saran", hasta = "saran_monta"),
        v("Saran, su hija, ya monta sola en el potro manso, atada a la silla para que no se caiga, y sigue a su madre por el campamento como un potrillo. Grita de alegría cuando trota. {nombre} tenía razón: montará mejor que todos.", desde = "saran_monta"),
        v("El potro blanco nació en la primavera de la yegua baya, y es el animal más bonito del campamento. Mönkh dice que es suyo. {nombre} dice que es del Cielo, porque los caballos blancos son de los espíritus. Mönkh dice que entonces el Cielo se lo presta.", desde = "potro"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de la estepa: porque se lo enseñó su padre, y a su padre, el suyo."),
        v("Hay cosas que solo se miran cuando se pierden. {dato_objeto}"),
        v("{dato_objeto} En la estepa se dice que un hombre sin caballo no es un hombre, y que un caballo sin hombre es un caballo libre."),
        v("{dato_objeto} Son cosas que se aprenden mirando, como casi todo."),
        v("Un pastor tiene pocas cosas, porque todo lo que tiene se carga en una carreta cuatro veces al año. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("arco", "el arco", "un arco", "El arco",
            "El arco mongol es corto y curvo, hecho de madera, cuerno y tendón pegados con cola de pescado; tarda un año en hacerse y tira más lejos que ningún otro; un hombre lo usa desde niño, a caballo, al galope, y suelta la flecha cuando las cuatro patas del caballo están en el aire."),
        ObjetoCuento("lazo", "la pértiga del lazo", "una pértiga de lazo", "La pértiga",
            "La pértiga del lazo es un palo largo, de dos hombres de alto, con una cuerda en la punta; con ella se atrapa a los caballos por el cuello, a galope, sin bajarse; un buen jinete la maneja como si fuera parte de su brazo."),
        ObjetoCuento("silla", "la silla de montar", "una silla de montar", "La silla",
            "La silla de montar mongola es de madera, alta delante y detrás, con estribos cortos; se monta casi de pie, con las piernas dobladas, para poder girarse y tirar con el arco hacia atrás; dormir sobre ella no es raro."),
        ObjetoCuento("ger", "la tienda", "una tienda de fieltro", "La tienda",
            "La tienda de fieltro se arma en lo que tarda en hervir una olla y se desarma en menos: una celosía de varas que se abre como un acordeón, un círculo arriba, varas que lo unen, y capas de fieltro atadas con cuerdas de crin; adentro no entra el viento."),
        ObjetoCuento("odre", "el odre", "un odre", "El odre",
            "El odre grande de cuero cuelga junto a la puerta de la tienda en verano, lleno de leche de yegua, y cada uno que pasa lo golpea con un palo; con los golpes, la leche se agria y se convierte en airag, que se bebe a cuencos."),
        ObjetoCuento("cuchillo", "el cuchillo", "un cuchillo", "El cuchillo",
            "El cuchillo se lleva al cinto en una funda con los palillos para comer, y sirve para todo: cortar carne, desollar, tallar, comer; se come cortando el trozo junto a la boca, hacia uno mismo, y nunca se apunta a nadie con él."),
        ObjetoCuento("eslabon", "el eslabón", "un eslabón", "El eslabón",
            "El eslabón es un hierro curvo que cuelga del cinto en una bolsita de cuero con un pedernal y yesca; se golpea el pedernal con él y saltan chispas; un hombre sin eslabón, en invierno, es un hombre muerto."),
        ObjetoCuento("morin_khuur", "el violín de cabeza de caballo", "un violín de cabeza de caballo", "El violín",
            "El violín de cabeza de caballo tiene dos cuerdas de crin, una caja de madera forrada de piel y una cabeza de caballo tallada en lo alto del mástil; se toca sentado, con el arco de crin, y suena como el viento cuando pasa por la crin de un caballo."),
        ObjetoCuento("cuenco", "el cuenco", "un cuenco de madera", "El cuenco",
            "El cuenco de madera, a veces con un borde de plata, es de cada uno y se lleva consigo; se bebe el té, el airag y el caldo, y se ofrece al huésped lleno, con las dos manos; un cuenco que se da vacío es una ofensa."),
        ObjetoCuento("deel", "el abrigo", "un abrigo", "El abrigo",
            "El abrigo largo de la estepa se cierra cruzado sobre el pecho y se ata con un fajín de tela, donde se guarda todo: el cuenco, el cuchillo, el eslabón; en invierno es de piel de oveja con el pelo hacia dentro, y de noche sirve de manta."),
        ObjetoCuento("argal", "la boñiga seca", "un cesto de boñiga seca", "La boñiga",
            "La boñiga seca de los animales es el fuego de la estepa, donde no hay árboles; se recoge en cestos, se apila junto a la tienda y arde con una llama baja y caliente, que no huele mal; sin boñiga, en invierno, no hay vida."),
        ObjetoCuento("copa", "la copa de plata", "una copa de plata", "La copa",
            "La copa de plata que vino de la guerra del oeste tiene pájaros grabados y letras de una lengua que nadie del valle sabe leer; era de una casa rica de una ciudad que ya no existe; en las fiestas se bebe airag en ella."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en la estepa como se piensa en el cielo: no se la elige, se vive debajo. Le da hierba, agua, animales y espacio. A sus {edad} años ha visto ciudades con muros de barro y torres, y no quiere vivir en ninguna. Allí el cielo es pequeño."),
        v("Piensa a veces en las ciudades que vio en el oeste, con sus mercados, sus jardines y sus libros, y en cómo ardieron. Las vio arder. No sabe si estuvo bien o mal. Sabe que el kan lo mandó, y que el kan habla con el Cielo. No lo piensa mucho. Cuando lo piensa, no duerme."),
        v("{mundo} Él piensa en cosas más cercanas: en el pasto, que este año es poco; en la yegua que cojea; en el lobo que ronda. Las cosas grandes son del kan, que las piensa en su tienda dorada, lejos."),
        v("{mundo} A sus {edad} años, {nombre} ha visto morir más animales que personas, y más personas de las que quisiera. En la estepa se muere de frío, de hambre, de caballo o de guerra. Él quiere morir viejo, en su tienda, con el olor del fuego. Es lo que quieren todos."),
        v("Piensa en sus hijos, que vivirán en un mundo donde el kan manda en todas partes, desde el mar del este hasta las ciudades del oeste. No sabe si será mejor o peor. Sabe que tendrán más caballos. Eso le parece bien."),
        v("{mundo} A veces, de noche, fuera de la tienda, mirando las estrellas, {nombre} piensa que el Cielo Azul lo ve todo, también lo que hizo en el oeste. Le pide perdón en voz baja, sin saber bien por qué. El Cielo no contesta. Nunca contesta."),
    )

    private val MUNDOS = mapOf(
        "bujara" to listOf(
            "En el campamento se habla de la guerra del oeste, de las ciudades del sha que el kan va tomando una por una.",
            "Se dice que el kan trae a los artesanos de las ciudades del oeste, y que los manda al este.",
            "Se habla del correo del kan, que pasa cada vez más deprisa y se lleva los mejores caballos.",
        ),
        "changchun" to listOf(
            "Se habla del monje chino que va a ver al kan, que dicen que sabe el secreto de no morir.",
            "Se dice que el sha murió en una isla, solo, y que su hijo sigue peleando.",
        ),
        "indo" to listOf(
            "Se habla del hijo del sha, que sigue peleando en el sur, y del kan, que lo persigue.",
            "Se dice que Muqali sigue tomando ciudades en el sur, en el país de los chinos.",
        ),
        "kalka" to listOf(
            "Se habla de los inviernos malos de antes, de cuántos animales se perdieron y de cómo se aguantó.",
            "Se dice que Jebe y Subutai llegaron más lejos que nadie, hasta un mar al oeste.",
        ),
        "temur" to listOf(
            "Se habla de los que vuelven de la guerra del oeste, con caballos, plata y silencios.",
            "Se dice que el kan viene de vuelta, por fin, después de tantos años.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} trae el rebaño al corral, contando las ovejas con los ojos, sin números: las conoce a todas. Falta una. La busca detrás de la loma y la encuentra, perdida y balando. La trae en brazos. En la tienda lo espera el fuego y el caldo."),
        v("Anochece en el valle, y el frío baja de golpe del cerro. Se encienden los fuegos de las tiendas, y el humo sube por los agujeros como seis hilos grises hacia las estrellas. {nombre} ata el Negro junto a la puerta, le da una palmada y entra."),
        v("Se cena en la tienda, alrededor del fuego, cada uno en su sitio: los hombres a un lado, las mujeres al otro, la madre en el sitio de honor. Se come carne, se bebe té, se habla de los animales. Luego, alguien canta, bajito. Fuera, el viento."),
        v("De noche, el campamento es un puñado de tiendas en la oscuridad inmensa de la estepa, con un poco de luz por los agujeros de arriba. Los perros vigilan. {nombre} sale una última vez a mirar el corral. Todo bien. Arriba, todas las estrellas del mundo."),
        v("La última luz del día se queda un momento en lo alto del cerro, sobre el ovoo, cuando el valle ya está a oscuras. {nombre} la ve desde el corral. Luego se va. Al este sale la luna, enorme y roja, y los perros le ladran.", ),
        v("Al anochecer el frío es tan grande que la leche se hiela en el cubo antes de llegar a la tienda, y la barba de {nombre} se llena de escarcha con su propio aliento. Dentro, el fuego. Fuera, la noche del invierno, que no tiene fin.", meses = setOf(11, 12, 1, 2, 3)),
        v("Al caer la tarde, en primavera, el campamento entero está en el corral, ayudando a parir a las ovejas que no pueden solas. Los corderos nacen con la luz de las antorchas, mojados, y se ponen de pie temblando. {nombre} tiene las manos llenas de sangre y está contento.", meses = setOf(3, 4)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "mes_blanco" to TextoEspecial(
            listOf(
                dia(
                    Parrafo.MANANA to "Hoy empieza el mes blanco, el año nuevo, con la luna nueva. {nombre} se pone el abrigo nuevo que le cosió Khulan, de piel azul con el borde de nutria, y antes del sol sale a saludar al Cielo con leche. Luego, el saludo a los mayores: se les sostiene los codos con las manos, con respeto.",
                    Parrafo.GENTE to "Todo el día se visitan las tiendas del campamento, una por una, empezando por la de los más viejos. En cada una hay carne hervida, dulces de leche, airag y una pila de pasteles de harina con forma de escalones. Se dice: ¿pasó bien el invierno? Se contesta: muy bien, aunque sea mentira.",
                    Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza, que tiene su animal y su suerte, y en el que se fue, que tuvo lo suyo. Piensa en los que no están en las tiendas para el mes blanco: los que siguen en la guerra, los que murieron. Les deja un cuenco de leche fuera, en la nieve.",
                ),
                dia(
                    Parrafo.GENTE to "Segundo día del mes blanco: hoy vienen los de otros campamentos del valle, a caballo, con regalos, y se bebe y se come otra vez. Bolad canta hasta quedarse sin voz. Mönkh come tantos dulces de leche que le duele la tripa.",
                ),
            ),
        ),
        "paricion" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezaron a parir las ovejas y las cabras, todas a la vez, como cada primavera, y el campamento no duerme. Hay corderos en el corral, corderos en la nieve, corderos dentro de la tienda, junto al fuego, envueltos en pieles. Balan toda la noche.",
                Parrafo.TRABAJO to "A esta hora ayuda a parir a una oveja que no puede sola: le mete la mano, busca las patas del cordero, tira despacio con cada empujón. El cordero sale, flojo, y {nombre} le sopla en la nariz hasta que respira. La oveja lo lame. Ya está.",
                Parrafo.PENSAMIENTO to "{nombre} mira el corral lleno de corderos nuevos y piensa que esto es la riqueza de verdad: no la plata del oeste, sino ovejas que paren. La plata no pare. Piensa que el kan, con todas sus ciudades, también come cordero.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora busca corderos perdidos en la ladera, que nacieron lejos del corral y se quedaron atrás. Encuentra dos, uno vivo y uno muerto. Trae los dos.",
                "A esta hora junta a una oveja que no quiere a su cordero con el cordero, atándola y frotándolo con su leche, hasta que lo acepta. A veces funciona.",
                "A esta hora cuida a los corderos débiles dentro de la tienda, junto al fuego, con Mönkh, que les da leche con un cuerno y les canta.",
                "A esta hora cuenta los corderos del año en el corral, de memoria, sin números: los ve y sabe cuántos son. Este año son muchos.",
            )),
        ),
        "sube_verano" to unDia(
            Parrafo.MANANA to "Hoy el campamento se muda a los pastos de verano, en el llano junto al río. Desde el amanecer se desarman las tiendas: se quita el fieltro, se pliega la celosía, se cargan las carretas con todo, hasta el fuego, que va en una olla con brasas. Los bueyes esperan.",
            Parrafo.PUEBLO to "La caravana del campamento sale del valle en fila: las carretas tiradas por bueyes, los rebaños, los caballos, los perros, los niños a caballo, las mujeres guiando los bueyes. Atrás queda el valle de invierno, con los corrales vacíos y la tierra negra donde estaban las tiendas.",
            Parrafo.ANOCHECER to "Al caer la tarde llegan al llano del verano, junto al río, y en lo que tarda en hervir una olla las tiendas ya están armadas, cada una en su sitio de siempre. Se enciende el fuego con las brasas que traían. {nombre} se sienta fuera. Ya es verano.",
        ),
        "primera_leche" to unDia(
            Parrafo.MANANA to "Hoy se ordeñan por primera vez las yeguas del año, y la primera leche no es para nadie: se le ofrece al Cielo. El chamán, vestido con su ropa de flecos, la echa hacia arriba con una cuchara de madera de nueve agujeros, hacia los cuatro lados, cantando.",
            Parrafo.TRABAJO to "A esta hora ata las yeguas a la cuerda larga con sus potros, que se ponen nerviosos: es la primera vez del año. Suelta un potro para que la yegua dé la leche, lo aparta, y Khulan ordeña. La leche es blanca, tibia y dulce. Mañana será airag.",
            Parrafo.PENSAMIENTO to "{nombre} mira la leche que sube hacia el cielo, en gotas que brillan al sol, y piensa que el Cielo bebe primero, como el más viejo de la tienda. Así debe ser. Luego, todo el verano, beberán ellos. Él tiene sed desde el invierno.",
        ),
        "esquila" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó la esquila de las ovejas, que ya tienen calor con la lana del invierno. Los hombres las tumban y las atan de las patas, y las mujeres y los muchachos las esquilan con tijeras de hierro, rápido, dejando la lana en una pieza, como un abrigo que se quita.",
                Parrafo.TRABAJO to "A esta hora tumba ovejas, una tras otra, y las sujeta mientras Khulan las esquila. Las ovejas patalean, balan y luego se quedan quietas, resignadas. Cuando se levantan, peladas, flacas y blancas, parecen otros animales. Las demás no las reconocen.",
                Parrafo.PUEBLO to "En el campamento hay montones de lana por todas partes, grises, blancas, marrones, y el aire huele a grasa de oveja. Las mujeres separan la lana buena de la mala. La buena es para el fieltro. La mala, para rellenar.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue esquilando ovejas. Le duelen las manos y la espalda. Mönkh lleva la lana a los montones y se cae encima de uno a propósito.",
                "A esta hora ata la lana en fardos y los sube a la carreta. La lana pesa más de lo que parece.",
                "A esta hora esquila él mismo, con las tijeras de Khulan, y le hace un corte a una oveja. Khulan se las quita sin decir nada.",
            )),
        ),
        "ovoo" to unDia(
            Parrafo.MANANA to "Hoy el campamento sube al ovoo del cerro, el montón de piedras de los espíritus del lugar, a darle las gracias por el verano y pedirle que siga. Cada uno lleva una piedra, leche, airag y una cinta azul. El chamán va delante, tocando su tambor.",
            Parrafo.PUEBLO to "En lo alto del cerro, junto al ovoo, la gente da tres vueltas en el sentido del sol, deja su piedra, ata su cinta a los palos, echa leche y airag. Luego se sientan en la hierba a comer y beber. Desde aquí se ve el valle de invierno, vacío, y el llano del verano, lleno.",
            Parrafo.ANOCHECER to "Al bajar del cerro, los jóvenes hacen carreras de caballos hasta el campamento, gritando, y los niños corren detrás a pie. Gana un muchacho de doce años. Lo celebran con airag. {nombre} piensa que a esa edad él también ganaba.",
        ),
        "fieltro" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó a hacerse el fieltro nuevo para las tiendas, con la lana de la esquila. Las mujeres extienden la lana en capas sobre un fieltro viejo, la mojan con agua caliente, la enrollan en un palo y la atan. Los hombres hacen el resto: arrastrarla.",
                Parrafo.TRABAJO to "A esta hora arrastra el rollo de fieltro a caballo, atado con una cuerda larga a la silla, de un lado a otro del llano, durante horas, para que la lana se apriete y se convierta en fieltro. El Negro lo mira con cara de que esto no es trabajo para un caballo de guerra.",
                Parrafo.PUEBLO to "En el campamento hay rollos de fieltro tendidos al sol, secándose, y las mujeres los golpean con palos y los pisan. El fieltro nuevo es blanco y huele a lana mojada. Con él se cubrirán las tiendas este invierno.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue arrastrando fieltro a caballo, con Bolad, que hace carreras con su rollo. El rollo de Bolad se deshace.",
                "A esta hora ayuda a extender el fieltro nuevo al sol para que se seque. Mañana se cortará para la tienda.",
                "A esta hora cose fieltro con Khulan, con una aguja de hueso y hilo de tendón, para hacer una puerta nueva para la tienda.",
            )),
        ),
        "baja_invierno" to unDia(
            Parrafo.MANANA to "Hoy el campamento vuelve al valle de invierno, al abrigo del cerro, antes de que lleguen las nieves. Se desarman las tiendas del verano, se cargan las carretas, se juntan los rebaños, gordos ya después del verano. Las primeras heladas blanquean la hierba.",
            Parrafo.PUEBLO to "La caravana sube del llano al valle por el mismo camino de siempre. Atrás queda el llano, con la hierba aplastada donde estaban las tiendas y los círculos negros de los fuegos. El río sigue, sin ellos. En el valle esperan los corrales, el cerro y el viento.",
            Parrafo.PENSAMIENTO to "{nombre} mira el valle de invierno desde lo alto del camino y piensa en los inviernos que ha pasado aquí, y en los que pasará. Cada uno es una batalla. Este año los animales están gordos. Eso es como tener buenas flechas.",
        ),
        "caza" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó la gran cacería del invierno: los hombres de todos los campamentos del valle forman un círculo enorme a caballo, de días de ancho, y lo van cerrando poco a poco, empujando hacia el centro a todos los animales que hay dentro: ciervos, lobos, zorros, liebres.",
                Parrafo.TRABAJO to "A esta hora cabalga en su lugar del círculo, con Bolad a un lado y un desconocido al otro, sin dejar que ningún animal se escape por su hueco. No se puede tirar todavía: solo empujar. El que deja escapar algo, paga. Es como la guerra, piensa, pero con ciervos.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que el kan hace la guerra igual que la cacería: un círculo enorme que se cierra. Por eso los hombres de la estepa son buenos en la guerra: la han practicado cazando toda la vida. A los ciervos, por lo menos, se los come uno.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en el círculo de la cacería, que ya es más pequeño. Se ven animales asustados que corren de un lado a otro.",
                "A esta hora el círculo se cierra y se puede tirar: {nombre} mata un ciervo con dos flechas. Un viejo dice que deje ir a los más jóvenes, para el año que viene.",
                "A esta hora reparte la caza entre los cazadores, según las reglas: el que mató, el que ayudó, el que vigiló. Nadie queda contento. Todos comen.",
                "A esta hora desuella el ciervo que le tocó, con el cuchillo, junto al fuego. La piel será para Khulan.",
            )),
        ),
        "matanza" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy se mata el ganado del invierno: las ovejas y una vaca que no aguantarían hasta la primavera. Se matan como se debe, rápido, sin derramar la sangre en la tierra, que es de los espíritus. La carne se corta y se deja congelar al aire, que ya hiela de día.",
                Parrafo.TRABAJO to "A esta hora desuella una oveja con Khulan, que separa las tripas, la sangre y la grasa sin desperdiciar nada: las tripas se rellenan de sangre, la grasa se guarda, los huesos se hierven. De una oveja, dice Khulan, solo se tira el balido.",
                Parrafo.PUEBLO to "En el campamento hay carne colgada de las varas de las carretas, congelándose, roja y blanca, y los perros esperan debajo con la boca abierta. Esta carne es el invierno: hasta la primavera no se mata más. Hay que contarla bien.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "En el campamento se sigue cortando y guardando la carne del invierno. Las mujeres hacen salchichas de sangre que se hierven en la olla grande.",
                "La carne del invierno ya está congelada y guardada en las carretas, tapada con fieltro. El campamento huele a grasa y a humo.",
            )),
        ),
        "noche_larga" to unDia(
            Parrafo.MANANA to "Hoy es el día más corto y la noche más larga del año. El sol apenas sube sobre el cerro y ya se va. Los viejos dicen que desde hoy los días crecen, aunque el frío todavía no ha llegado a lo peor. Esta noche se cuentan las historias de los antepasados.",
            Parrafo.GENTE to "Esta noche todo el campamento se junta en la tienda más grande, la de la madre de Bolad, alrededor del fuego, y los viejos cuentan las historias: del lobo azul y la cierva, que fueron los primeros; de los antepasados que robaban caballos; del kan cuando era niño y lo tenían cautivo con un cepo al cuello.",
            Parrafo.PENSAMIENTO to "{nombre} escucha las historias que ya se sabe y piensa que algún día las contará él, a los hijos de Mönkh, en una tienda como esta. Y que contará también la suya: la guerra del oeste, las ciudades, el fuego. No sabe todavía cómo la contará.",
        ),
        "copa" to unDia(
            Parrafo.MANANA to "Hoy, en la tienda de su madre, {nombre} le dio la copa de plata que trajo de la guerra del oeste, envuelta en un trapo de seda. La madre la miró mucho rato, la tocó, pasó el dedo por los pájaros grabados. Luego preguntó de dónde era. {nombre} le dijo que de una ciudad.",
            Parrafo.GENTE to "Su madre llenó la copa de airag, la levantó hacia el Cielo, echó unas gotas y bebió. Luego se la pasó a él. Luego a Khulan. Luego a Mönkh, que solo mojó los labios. Nadie preguntó qué pasó con la ciudad. Su madre no preguntó nada más.",
            Parrafo.PENSAMIENTO to "{nombre} mira la copa en las manos de su madre y piensa en la casa de donde la sacó, en la mujer que lloraba en la puerta, en el humo. Piensa que en la tienda de su madre la copa está mejor que en una ciudad quemada. No sabe si eso es verdad.",
        ),
        "bujara" to unDia(
            Parrafo.MANANA to "Hoy llegó al campamento un jinete del correo del kan, con caballo de refresco y la cara quemada por el viento, y contó la noticia: el kan tomó Bujará, una ciudad enorme del oeste, la de las mezquitas y los libros, y la quemó. Se bebió airag por la victoria.",
            Parrafo.GENTE to "El jinete contó que el kan subió al púlpito de la mezquita grande de Bujará y les dijo a los ricos de la ciudad que él era el castigo del Cielo por sus pecados. Los hombres del campamento se rieron y aplaudieron. Bolad repitió la frase tres veces, con voz grave.",
            Parrafo.PENSAMIENTO to "{nombre} no aplaude. Piensa en Temür, su hermano, que estará allí, y en las ciudades que él vio, parecidas a Bujará, con mercados y jardines. Piensa en la palabra castigo. Luego bebe airag, como todos, porque no beber sería raro.",
        ),
        "potro" to unDia(
            Parrafo.MANANA to "Hoy, al amanecer, la yegua baya parió en la ladera un potro blanco, blanco del todo, sin una mancha. Mönkh lo vio primero y gritó tan fuerte que despertó a todo el campamento. Ahora el potro está de pie, temblando, con las patas demasiado largas.",
            Parrafo.TRABAJO to "A esta hora cuida al potro blanco y a su madre, apartados del rebaño, en un rincón del corral con paja limpia. El potro mama, se cae, se levanta, vuelve a mamar. {nombre} le pasa la mano por el lomo, despacio, para que se acostumbre a la mano de un hombre.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un caballo blanco es un regalo de los espíritus, y que no se monta en la guerra. Será para las fiestas, para las ofrendas, para que lo vean. Mönkh dice que será suyo. {nombre} le dice que un caballo blanco no es de nadie. Mönkh no le cree.",
        ),
        "samarcanda" to unDia(
            Parrafo.MANANA to "Hoy llegó otra noticia del oeste: el kan tomó Samarcanda, la ciudad más grande y más rica del sha, con murallas dobles y miles de soldados. Dicen que los soldados del sha se rindieron y que igual los mataron. Dicen que los artesanos se los llevó el kan.",
            Parrafo.PUEBLO to "En el campamento se bebe por la victoria, otra vez, y los jóvenes que no fueron a la guerra hablan de ir. Las madres de los que fueron preguntan al jinete por sus hijos. El jinete no sabe nombres. Dice que muchos están bien. Dice lo que se dice.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Samarcanda, que no vio, pero que se imagina como las que vio: grande, verde, llena de gente que no entendía. Piensa que la guerra sigue sin él, y que sin él va igual de bien. Eso le da alivio, y también algo parecido a la vergüenza.",
        ),
        "correo" to unDia(
            Parrafo.MANANA to "Hoy pasaron por el campamento tres jinetes del correo del kan, con una tablilla de oro colgada del cuello, que les da derecho a cualquier caballo en cualquier campamento. Pidieron caballos de refresco. El campamento les dio los mejores. No se le niega nada a la tablilla.",
            Parrafo.TRABAJO to "A esta hora trae los caballos que le piden los del correo, los más rápidos, entre ellos la yegua torda de Bolad, que Bolad entrega con la cara de quien entrega a un hijo. Los jinetes cambian de caballo sin bajarse casi, comen un trozo de carne y se van.",
            Parrafo.PENSAMIENTO to "{nombre} mira cómo se van los del correo, al galope, hacia el oeste, y piensa en lo grande que es el mundo del kan, que necesita jinetes que no paren nunca para que las órdenes lleguen. Piensa que antes el mundo era del tamaño del valle. Ahora no tiene orillas.",
        ),
        "khulan_espera" to unDia(
            Parrafo.MANANA to "Hoy Khulan le dijo a {nombre}, mientras ordeñaban las yeguas, que espera un hijo. Se lo dijo sin mirarlo, como quien dice que va a llover. {nombre} soltó al potro que tenía sujeto, que salió corriendo hacia su madre, y la yegua dio una patada al cubo.",
            Parrafo.GENTE to "Esta noche, en la tienda, {nombre} se lo cuenta a su madre, que asiente como si ya lo supiera, y probablemente lo sabía. Mönkh pregunta si será un hermano. Khulan dice que será lo que el Cielo quiera. Mönkh dice que el Cielo quiere un hermano.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el hijo que viene, que nacerá cuando paran las ovejas, y en que no conocerá la guerra del oeste, si el Cielo quiere. Piensa que le gustaría que fuera niña, aunque no lo dice, porque los hombres no dicen eso. Lo piensa igual.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "pastor_mongol", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + VERANO_MANANA,
            Parrafo.TIEMPO to TIEMPO + VERANO_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + VERANO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + VERANO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + listOf(
                v("Desde el invierno del hielo, {nombre} piensa en el rebaño que perdió como se piensa en una herida: no duele siempre, pero duele cuando se toca. Un tercio de los animales, muertos de hambre sobre la nieve dura. Los cuenta cada noche, los que quedan. Crecen despacio.", desde = "hielo"),
            ),
            Parrafo.ANOCHECER to ANOCHECER + VERANO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
