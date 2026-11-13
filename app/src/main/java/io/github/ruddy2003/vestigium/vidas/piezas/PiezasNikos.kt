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
 * El cuento de Nikos, buzo esponjero de escafandra en un barco de Tarpon Springs, en el golfo de México, de 1910 a 1914.
 * Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día en el mar tiene dos tramos de trabajo (bajar al fondo y, después, limpiar y ensartar esponjas en cubierta) y
 *   tres comidas: esos textos valen a cualquiera de sus horas, y los de trabajo cuentan una cosa u otra.
 * - Dos estados: «puerto» (cada invierno, de mediados de diciembre a fines de enero, en tierra, en Tarpon Springs) y
 *   «enfermo» (temporada 4: cinco semanas en un cuarto de la casa de huéspedes, con la enfermedad de los buzos).
 * - El barco lee el arco «esponjas» (lo que lleva en cubierta y en la bodega); en tierra no se lee.
 * - Las fiestas de la Iglesia caen trece días después que las de los americanos: el cuento lo dice cuando hace falta.
 * - No repite lo que ya dice «Su vida»: el casco atornillado al traje, los dos hombres de la bomba, los tirones de la
 *   cuerda, pisar las esponjas para quitarles la piel, el pan duro mojado, dormir en cubierta bajo las estrellas.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG6.kt`):
 * - Stelios es su ayudante en la cuerda hasta `yannis` (temporada 2); desde entonces, Yannis.
 * - Eleni: su madre se la busca desde `eleni` (3); llega desde la isla en `llega_eleni` (4); casados desde `boda` (5),
 *   en la casa de madera desde `casa` (5), y espera un hijo desde `eleni_espera` (5).
 * - Las fiestas que vuelven cada año no nombran a Eleni, a Yannis ni a Stelios.
 */
internal object PiezasNikos {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A {nombre} lo despierta el capitán Manolis golpeando una sartén con un cucharón, cuando el cielo todavía es negro por el oeste y gris por el este. Los diez del barco se levantan de sus mantas en cubierta, se estiran, escupen por la borda. El mar está quieto como un plato."),
        v("{nombre} se despierta con el rocío encima, la manta mojada y la boca salada. Se lava la cara con un cubo de agua del mar. Lo primero que hace es mirar el agua por la borda: si está clara, se verá el fondo; si está turbia, se trabajará a ciegas, con las manos."),
        v("{nombre} se despierta antes que nadie y se sienta en la proa a mirar cómo sale el sol por detrás de la costa baja de Florida, que es solo una raya de árboles. En la isla, el sol salía del mar, por detrás de otra isla. Aquí sale de la tierra. Todavía no se acostumbra."),
        v("A {nombre} lo despiertan las gaviotas, que se pelean en el palo por los restos del pescado de anoche, y el olor de las esponjas que se secan en las cuerdas, que es un olor a mar podrido que ya no nota nadie del barco. Se santigua tres veces. Se levanta."),
        v("{nombre} se despierta con el barco cabeceando, porque ha cambiado el viento en la noche y han echado otra ancla. El capitán ya está en la popa, mirando el cielo con los ojos entornados. Dice que se podrá bajar. Que no se podrá mucho tiempo."),
        v("Lo primero que hace {nombre} cada mañana es besar el icono de san Nicolás que lleva colgado al cuello en una bolsita de cuero, y rezar en voz baja lo que le enseñó su madre. Después se pone de pie, se rasca la barba y mira el mar. Hoy toca bajar hondo."),
    )

    /** En tierra, en Tarpon Springs, cada invierno (estado «puerto»). */
    private fun pu(texto: String, hacer: Hacer? = null, desde: String? = null, hasta: String? = null) = v(texto, estado = "puerto", hacer = hacer, desde = desde, hasta = hasta)

    private val PUERTO_MANANA = listOf(
        pu("{nombre} se despierta en una cama de verdad, con sábanas, en la casa de huéspedes de la señora Kalliopi, y durante un momento no sabe dónde está, porque la cama no se mueve. Se queda mirando el techo. Luego oye las campanas de San Nicolás y se acuerda.", hasta = "casa"),
        pu("{nombre} se despierta tarde, como se despierta en tierra, con el sol ya en la ventana, y oye desde la cama los ruidos del pueblo: un carro, un gallo, una mujer que llama a un niño en griego, otra que le contesta en inglés. En el muelle, los barcos amarrados chocan entre sí."),
        pu("{nombre} se levanta y baja a la calle a por pan a la panadería griega, que hace el pan de la isla, redondo y con sésamo. La panadera le pregunta por su madre. Todo el pueblo sabe la vida de todos. Es como estar en Kálymnos con palmeras."),
        pu("{nombre} se despierta en la casa de madera junto al muelle, con Eleni ya levantada, y el olor del café en el fuego. Por la ventana se ven los palos de los barcos amarrados. El suyo está ahí, quieto, esperando. Él también espera. En enero se vuelve a salir.", desde = "casa"),
    )

    private val PUERTO_TRABAJO = listOf(
        pu("A esta hora remienda el traje de lona en el muelle, sentado en un cajón, con una aguja curva y un hilo encerado, y le pone parches de goma donde se ha gastado. Un traje roto en el fondo es agua dentro y un buzo ahogado. Cose despacio. Mira cada puntada dos veces.", Hacer.TRABAJO),
        pu("A esta hora ayuda a carenar el barco en la rampa del astillero: rascar el casco, calafatear las tablas con estopa y brea, pintar. El barco, fuera del agua, parece pequeño y torpe, como un pez en una mesa. Manolis pasa la mano por las tablas, una por una.", Hacer.TRABAJO),
        pu("A esta hora repasa la bomba del aire con el capitán, pieza por pieza: los émbolos, las válvulas, el cuero de los cierres, la manguera, que se mira palmo a palmo contra la luz buscando grietas. La vida del buzo pasa por esa manguera. {nombre} la revisa como un cura su misal.", Hacer.TRABAJO),
        pu("A esta hora come en la mesa larga de la casa de huéspedes, con otros buzos de otros barcos: sopa de huevo y limón, pan, aceitunas, queso blanco, vino de la isla que alguien ha traído en un barril. Se habla de fondos, de capitanes y de compradores. En tierra se come despacio.", Hacer.COMER, hasta = "casa"),
        pu("A esta hora come en la casa de madera, en la mesa que hizo él mismo con tablas del astillero: lo que Eleni cocina como en la isla, con lo que se encuentra en Florida, que no es lo mismo. Ella dice que el aceite de aquí no sabe a nada. Él dice que la comida sabe a ella.", Hacer.COMER, desde = "casa"),
        pu("A esta hora come de pie en el café del muelle, un plato de garbanzos y un trozo de pan, con un café espeso y dulce que se toma en tres sorbos. Alrededor, buzos, capitanes, compradores de esponjas, todos hablando a la vez. Nadie escucha a nadie. Todos se entienden.", Hacer.COMER),
        pu("A esta hora duerme en una cama que no se mueve, y en tierra duerme peor que en el mar: le falta el balanceo, el ruido del agua contra el casco, las estrellas. Se despierta a media noche y sale a la ventana a mirar el muelle. Los barcos se balancean. Él no.", Hacer.DORMIR),
        pu("A esta hora duerme hondo, con el cansancio de todo un año de bajar, que en tierra le cae encima de golpe como una ola. Duerme diez, once horas. La casera dice que los buzos en invierno duermen como osos. {nombre} no sabe qué es un oso. Se lo imagina.", Hacer.DORMIR),
        pu("A esta hora está en el café de los griegos, con los del barco, jugando a las cartas y al tavli, fumando, bebiendo café y un anís que se pone blanco con el agua. Alguien saca un laúd. Se canta. Se discute de política de la isla, que está muy lejos, como si estuviera a la vuelta de la esquina.", Hacer.GENTE),
        pu("A esta hora pasea por la calle del muelle, arriba y abajo, con los del barco, con la camisa blanca y el sombrero, como se pasea en la isla por la tarde. Se saluda a todo el mundo. Las muchachas griegas pasean del brazo con sus madres, y se miran de lejos con los muchachos.", Hacer.GENTE),
    )

    private val PUERTO_PUEBLO = listOf(
        pu("Tarpon Springs, en invierno, está lleno de barcos: amarrados de dos en dos y de tres en tres a lo largo del río, con los palos tan juntos que parecen un bosque sin hojas. Los marineros cosen, pintan, beben, juegan. El pueblo huele a brea, a café y a esponja."),
        pu("En la lonja de las esponjas, un patio grande con cuartos alrededor, cada capitán tiene su puerta, donde guarda lo que sacó el año. Los compradores americanos pasan, miran, aprietan las esponjas con la mano, ofrecen poco. Los capitanes piden mucho. Así se pasa el invierno."),
        pu("La iglesia de San Nicolás es de madera, pequeña, blanca, con una cúpula que alguien pintó de azul para que se pareciera a las de la isla. Dentro huele a cera y a incienso, y los iconos vinieron en barco de Grecia. Los domingos no cabe nadie. Los buzos se quedan en la puerta."),
        pu("En las calles de Tarpon Springs se oye más griego que inglés: en los cafés, en las tiendas, en el muelle, en la iglesia. Los americanos del pueblo miran a los griegos con una mezcla de desconfianza y de interés, como se mira a un perro grande que no se sabe si muerde."),
    )

    private val PUERTO_ANOCHECER = listOf(
        pu("Al caer la tarde las campanas de San Nicolás llaman a vísperas, y las mujeres del barrio griego, de negro, van a la iglesia con sus velas. {nombre} se queda en la puerta, con el sombrero en la mano. Por la ventana del café de enfrente se oye una canción de la isla."),
        pu("Anochece sobre el río, y los barcos amarrados encienden sus faroles, uno tras otro, y el agua se llena de luces que se mueven. En los cafés del muelle hay ruido y humo hasta tarde. {nombre} vuelve a casa por la orilla, despacio. En tierra la noche dura mucho."),
    )

    /** Cinco semanas en tierra con la enfermedad de los buzos, en la temporada 4 (estado «enfermo»). */
    private fun en(texto: String, hacer: Hacer? = null) = v(texto, estado = "enfermo", hacer = hacer)

    private val ENFERMO_MANANA = listOf(
        en("{nombre} se despierta en el cuarto de la casa de huéspedes con el mismo dolor de siempre en las rodillas y en los codos, como si alguien le hubiera metido arena caliente en los huesos. Intenta mover las piernas. La derecha responde. La izquierda, a medias. Mejor que ayer."),
        en("A {nombre} lo despierta la casera, que le trae un caldo y le abre la ventana para que entre el aire. Él no quiere caldo, quiere mar. Ella le dice que ya volverá, que otros han vuelto. No le dice que otros no. Él ya lo sabe."),
        en("{nombre} se despierta de un sueño en el que estaba en el fondo, entre las esponjas, y subía, y subía, y no llegaba nunca arriba. Está sudando. Se sienta en la cama. Le tiembla la mano. Por la ventana se ve el río, y ningún barco: están todos en el mar."),
    )

    private val ENFERMO_TRABAJO = listOf(
        en("A esta hora camina por el cuarto agarrado a la pared, de la cama a la ventana y de la ventana a la cama, como le dijo el médico, para que las piernas no se olviden. Diez veces. Veinte. Las piernas no se olvidan, pero se quejan. Él también, en griego, bajito.", Hacer.TRABAJO),
        en("A esta hora está sentado en el porche de la casa de huéspedes, con las piernas tapadas con una manta aunque haga calor, mirando el río vacío. Pasa algún viejo buzo, de los que ya no bajan, y se sienta a su lado sin decir nada. Fuman juntos. Es su manera de decir que entiende.", Hacer.TRABAJO),
        en("A esta hora ensarta esponjas viejas en una cuerda, sentado en la cama, para tener las manos ocupadas: las que se quedaron en la lonja sin vender. No sirve de nada. Pero las manos, quietas, piensan, y no quiere que piensen. Ensarta otra.", Hacer.TRABAJO),
        en("A esta hora come en la cama un plato de arroz con leche que le hizo la casera, porque dice que es lo que se da a los enfermos en América. En la isla se les da caldo de gallina. {nombre} se come el arroz igual. Se lo come todo. La casera queda contenta.", Hacer.COMER),
        en("A esta hora duerme mal: cuando se gira, le duelen los codos; cuando no se gira, le duele la espalda. Oye los grillos, los perros, las campanas de la iglesia dando las horas. Cuenta las campanadas. Cuenta las semanas. Cuenta lo que pierde.", Hacer.DORMIR),
        en("A esta hora lo visita el pope, que se sienta en la silla del cuarto, le pone la mano en la frente y reza un rato en voz alta. Luego le cuenta las noticias del pueblo: quién se casa, quién se pelea, qué barco tiene más esponjas. {nombre} lo escucha como a un periódico.", Hacer.GENTE),
    )

    private val ENFERMO_PUEBLO = listOf(
        en("En verano, con los barcos en el mar, Tarpon Springs se queda de las mujeres, de los niños, de los viejos y de los tenderos. Las calles del muelle están vacías. Los cafés, medio cerrados. Solo se oyen las cigarras y, por la tarde, la tormenta, que llega siempre a la misma hora."),
        en("El cuarto de la casa de huéspedes es pequeño y blanco, con una cama de hierro, una silla, un lavabo, una ventana que da al río y un icono de la Virgen en la esquina, con su lamparilla. Es todo lo que ve {nombre} durante semanas. Se lo sabe de memoria. Cada grieta."),
    )

    private val ENFERMO_ANOCHECER = listOf(
        en("Al caer la tarde llega la tormenta de todos los días, con truenos que hacen temblar la ventana, y después el aire se queda limpio y fresco. {nombre} se asoma, agarrado al marco, y respira hondo. El aire de tierra no huele a nada. Echa de menos el olor del barco."),
        en("Anochece, y la casera enciende la lamparilla del icono. {nombre} reza lo que sabe, y le promete a san Nicolás una vela del tamaño de un hombre si puede volver a bajar. Luego piensa que a lo mejor es mejor no volver a bajar. No se lo promete a nadie."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} Un buzo mira el cielo como mira el fondo: buscando lo que no está bien."),
        v("{nombre} mira el cielo con los ojos entornados, como le enseñó su padre en la isla. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En el golfo el tiempo cambia más deprisa que en el Egeo, y avisa menos."),
        v("{cielo} {de_estacion} El agua del golfo cambia de color y de humor con los meses. {nombre} lo nota en la piel."),
        v("{de_estacion} {cielo} El capitán dice que en el golfo hay dos tiempos: el bueno y el que mata."),
        v("Lo primero que mira un esponjero al despertar es el cielo; lo segundo, el agua. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y azul, sin una nube, y el sol pega fuerte desde temprano.",
            "Hace un día claro, de los que dejan ver lejos, y el agua tiene el color de una botella.",
            "No hay nubes, y la luz es tan fuerte que hace llorar los ojos.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto de nubes bajas y blancas que no se deciden a llover.",
            "Pasan nubes grandes desde el golfo, y sus sombras corren sobre el agua.",
            "Está nublado, y el aire es pesado y húmedo como una toalla.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia caliente que no refresca y que deja todo pegajoso.",
            "Cae un aguacero que dura media hora y se va como vino.",
            "Llueve, y la lluvia hace en el agua un ruido de aceite friéndose.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: el cielo se pone morado por el oeste y caen rayos sobre el agua.",
            "Truena, y el viento levanta de golpe olas cortas y blancas.",
            "Llega una tormenta de las de la tarde, negra y rápida, con granizo.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del norte, frío y seco, que pica el agua.",
            "Hace viento, y las cuerdas silban y los palos crujen.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.LLUVIAS to listOf(
            "Es la temporada de las lluvias, cuando cada tarde llega una tormenta.",
            "En estos meses el calor no se va ni de noche, y el agua del golfo está tibia como un baño.",
            "Es el tiempo de los huracanes, y todos miran el cielo del sur.",
        ),
        Estacion.SECAS to listOf(
            "Es la temporada seca, cuando el aire se aclara y las noches refrescan.",
            "En estos meses llegan los nortes, vientos fríos que enturbian el agua.",
            "Es el tiempo bueno para bajar: el agua clara y el mar tranquilo.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora baja por la escala de cuerda, despacio, con el casco puesto y las botas de plomo, hasta que el agua le cubre la cabeza y el mundo se vuelve verde y silencioso. Solo se oye el aire que entra por la válvula. Abajo, el fondo, con sus esponjas como cabezas negras.", hacer = Hacer.TRABAJO),
        v("A esta hora camina por el fondo del golfo, inclinado hacia delante contra la corriente, con la red colgada a la cintura, buscando esponjas entre las algas y las rocas. Corta una con el cuchillo, la mete en la red, busca otra. El fondo es una pradera gris que no se acaba.", hacer = Hacer.TRABAJO),
        v("A esta hora está en el fondo, a veinte brazas, y el agua es tan clara que ve pasar un banco de peces plateados por encima de su cabeza, como una nube. Se queda mirándolo. Luego tira de la cuerda: más aire. Arriba, los de la bomba se ponen a darle más deprisa.", hacer = Hacer.TRABAJO),
        v("A esta hora sube despacio por la escala, parándose en cada peldaño, como hay que subir, con la red llena de esponjas negras que chorrean. Arriba le quitan el casco, y el aire del mundo le entra en la cara como un golpe. Se sienta en cubierta. Respira. Fuma.", hacer = Hacer.TRABAJO),
        v("A esta hora limpia esponjas en cubierta con los demás, sentados en círculo, con las piernas cruzadas: las aprietan, las lavan en un cubo, les cortan con el cuchillo lo que sobra, las dejan como pan blanco. Las manos se le ponen rojas e hinchadas. Mañana, más.", hacer = Hacer.TRABAJO),
        v("A esta hora ensarta esponjas en una cuerda larga, con una aguja de madera, para colgarlas a secar del palo. Cuarenta en cada cuerda. Las cuenta en griego, en voz alta, porque si se equivoca el capitán se enfada. Las esponjas secas son la paga de todos.", hacer = Hacer.TRABAJO),
        v("A esta hora le toca la bomba del aire de otro buzo, con un compañero, dándole a la manivela de la rueda, uno a cada lado, sin parar, mirando la manguera que baja al agua. El de abajo vive de lo que hacen sus brazos. No se habla. No se para. Se cuenta en voz baja.", hacer = Hacer.TRABAJO),

        v("A esta hora come en cubierta, sentado en un rollo de cuerda: pan de barco, aceitunas negras, un trozo de queso duro, una cebolla, agua con un poco de vino. Come con la navaja, sin plato. Las gaviotas esperan las migas en la borda, muy serias, como acreedores.", hacer = Hacer.COMER),
        v("A esta hora come lo que ha cocinado el viejo Michalis en el hornillo de la proa: una sopa de lentejas con mucho aceite y mucho ajo, que se come de una olla común, cada uno con su cuchara. Michalis dice que las lentejas son buenas para el aire. Nadie sabe qué quiere decir. Todos comen.", hacer = Hacer.COMER),
        v("A esta hora come pescado: un mero que ha subido Manolis con un sedal, asado en las brasas con limón y sal, dividido en diez trozos iguales. El capitán se queda con la cabeza, que es lo mejor y es suya por derecho. {nombre} se queda con un trozo de la cola. Está bueno.", hacer = Hacer.COMER),

        v("A esta hora duerme en cubierta, envuelto en su manta, con la cabeza sobre un rollo de cuerda y el cielo entero encima. Le duelen los oídos, como siempre después de bajar. Oye el agua contra el casco, la respiración de los demás, un pez que salta. Se duerme.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con el fondo: que camina por un campo de esponjas que no se acaba, y que en el fondo hay una casa blanca, como las de la isla, con una ventana abierta. Siempre se despierta antes de llegar.", hacer = Hacer.DORMIR),
        v("A esta hora duerme a ratos, porque el barco cabecea y el ancla cruje, y porque Michalis ronca como una bomba de aire estropeada. Se cambia de sitio, a la proa. Allí hace más viento. Allí no se oye a Michalis. Allí se duerme.", hacer = Hacer.DORMIR),

        v("A esta hora está en cubierta con los del barco, sentados en corro, con el farol en medio: uno pasa las cuentas del comboloi, otro cose una red, otro cuenta una historia que todos han oído cien veces. Se ríen igual en los mismos sitios. El mar está negro alrededor.", hacer = Hacer.GENTE),
        v("A esta hora juega a las cartas en cubierta con el capitán, con Michalis y con otro buzo, sobre una tabla, apostando cigarrillos. El capitán hace trampas y todos lo saben. Nadie dice nada: es el capitán. {nombre} pierde tres cigarrillos. Los da por bien perdidos.", hacer = Hacer.GENTE),
        v("A esta hora escucha a Michalis contar las historias de los buzos de antes, los que bajaban desnudos con una piedra atada al cuerpo y aguantando la respiración, en los mares de la isla. Michalis bajó así, de muchacho. Dice que era mejor. Nadie le cree. Todos le escuchan.", hacer = Hacer.GENTE),
    )

    private val PUEBLO = listOf(
        v("{entorno} El barco es una goleta pequeña, de dos palos, pintada de blanco y azul como las barcas de la isla, con un ojo pintado en la proa para que vea el camino. Se llama Agios Nikolaos, como la mitad de los barcos de Tarpon Springs. En él caben diez hombres, la bomba y las esponjas."),
        v("Los bancos de esponjas están lejos de la costa, a un día o dos de vela, en un mar llano y verde donde no se ve tierra. Solo agua, cielo y otros barcos esponjeros, cada uno en su sitio, que se saludan de lejos con un trapo. {entorno}"),
        v("{entorno} En la cubierta todo tiene su lugar: la bomba del aire en medio, la escala en la borda, los cascos y los trajes colgados, el hornillo en la proa, las mantas en la popa. Diez hombres viven meses en un sitio donde no cabe una mesa."),
        v("Junto al barco, amarrado a la popa, va el bote pequeño, el que lleva al buzo al sitio exacto donde se baja. Desde el bote se mira el fondo con un cubo de cristal, como una ventana en el agua. Así se buscan las esponjas antes de bajar. {entorno}"),
        v("De día, el barco se queda quieto sobre el banco, anclado, con la bomba en marcha y la manguera bajando al agua, y alrededor solo el mar llano. Los pelícanos se posan en el agua a mirar, como viejos en una plaza. A veces sale un delfín, resopla, se va. {entorno}"),
        v("{entorno} El golfo de México, aquí, es poco hondo y muy ancho, con el fondo de arena y de roca blanda, y el agua tan clara en los días buenos que se ven los peces a diez brazas desde la borda. En los días malos no se ve ni la mano."),
    )

    /** Lo que lleva el barco (arco «esponjas»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("esponjas", 0.0, 0.2, frases = listOf(
            "En la bodega hay pocas esponjas todavía, y las cuerdas del palo están casi vacías.",
            "El barco va ligero: hace poco que se vendió lo de la bodega, y hay que empezar otra vez.",
        )),
        Entorno("esponjas", 0.2, 0.6, frases = listOf(
            "Del palo cuelgan ya muchas cuerdas de esponjas secándose al sol, como racimos.",
            "La bodega se va llenando: las esponjas secas, apretadas en sacos, huelen a mar y a dinero.",
        )),
        Entorno("esponjas", 0.6, 1.0, frases = listOf(
            "El barco va cargado hasta arriba: esponjas en la bodega, en cubierta, colgadas de todas partes.",
            "Ya casi no queda sitio para dormir entre tantas esponjas, y el capitán sonríe todo el día.",
        )),
    )

    private val GENTE = listOf(
        v("El capitán Manolis es de su misma isla, de un pueblo de la otra punta, y fue buzo veinte años antes de comprarse el barco. Tiene las piernas torcidas y una oreja que no oye. Habla poco y grita mucho. Sabe dónde hay esponjas como un perro sabe dónde hay un hueso."),
        v("Los diez del barco son de tres islas: Kálymnos, Symi y Halki. Los de cada isla se ríen de los de las otras, de cómo hablan, de cómo comen, de cómo bajan. Pero en el barco son uno, y cuando un buzo sube mal, todos corren a la escala sin preguntar de qué isla es."),
        v("El viejo Michalis es el cocinero del barco, y fue buzo desnudo de muchacho, y buzo de casco de mayor, hasta que el fondo le dejó una pierna que arrastra. Cocina mal y cuenta bien. Se sabe todas las canciones de la isla, y todas las mentiras de los buzos."),
        v("Su madre vive en Kálymnos, en la casa blanca de la cuesta, con las dos hermanas pequeñas de {nombre}. Es viuda: su padre se quedó en el fondo, en el mar de Libia, cuando {nombre} tenía doce años. Le escribe por mano del maestro, y {nombre} contesta por mano de quien sepa."),
        v("El pope de San Nicolás, el padre Theodoros, vino de la isla con los primeros buzos y conoce a todos por su nombre y por el de su padre. Bendice los barcos antes de salir y entierra a los que no vuelven. Cuando bendice el barco de {nombre}, {nombre} cierra los ojos."),
        v("El comprador de esponjas del capitán es un americano gordo de Nueva York, el señor Hale, que habla un griego de diez palabras, todas de dinero. Viene dos veces al año. Aprieta las esponjas, frunce la boca, dice un precio. El capitán dice que es un ladrón. Le vende igual."),
        v("Stelios, su ayudante en la cuerda, es un hombre callado de Halki que lleva la cuerda de {nombre} como quien lleva la mano de un niño: siente cada tirón antes de que llegue. Quiere comprarse su propio barco. Lleva años ahorrando, en un calcetín, debajo de la manta.", hasta = "yannis"),
        v("Yannis, su ayudante en la cuerda, es un muchacho de Symi de diecinueve años, que todavía no sabe leer bien los tirones pero aprende deprisa. Stelios, el de antes, se fue a otro barco de capitán. Yannis mira a {nombre} como {nombre} miraba a Stelios. Le da un poco de vergüenza.", desde = "yannis"),
        v("Su madre le escribe que en la isla hay una muchacha, Eleni, hija de una prima, que sabe coser, sabe leer y no tiene miedo del mar. Le manda su retrato. {nombre} lo lleva en el bolsillo de la camisa, envuelto en un papel encerado para que no se moje. Lo mira mucho.", desde = "eleni", hasta = "llega_eleni"),
        v("Eleni ya está en Tarpon Springs, viviendo en casa de la mujer del capitán, esperando la boda. Es más alta que en el retrato y más seria, y le habla de usted. {nombre} no sabe qué decirle. Ella tampoco. Se miran mucho y hablan del tiempo.", desde = "llega_eleni", hasta = "boda"),
        v("Eleni, su mujer, se ha quedado en tierra, en Tarpon Springs, como todas las mujeres de los buzos, esperando a que vuelva el barco. {nombre} piensa en ella abajo, en el fondo, donde se piensa mejor. Le ha prometido no subir nunca deprisa. Nunca.", desde = "boda", hasta = "eleni_espera"),
        v("Eleni espera un hijo, y {nombre} tiene ahora dos razones para subir despacio por la escala, peldaño a peldaño, como se debe. Los del barco se ríen de él porque cuenta los meses con los dedos. Él no se ríe. Cuenta.", desde = "eleni_espera"),
    )

    private val OBJETO = listOf(
        v("En el barco hay pocas cosas, y cada una puede salvarte o matarte. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de su padre, en la isla, que lo aprendió del suyo."),
        v("Hoy {nombre} se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Los americanos de las tiendas, que venden las esponjas en sus bañeras, no saben nada de eso."),
        v("{dato_objeto} En la isla se dice que un buzo es tan bueno como sus cosas, y sus cosas tan buenas como él."),
        v("Un esponjero cuida sus cosas como un pastor sus cabras. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("casco", "el casco", "un casco de buzo", "El casco",
            "El casco del buzo es de cobre y de bronce, redondo como una calabaza, con tres ventanillas de cristal grueso y una válvula para que salga el aire viejo; pesa como un niño de diez años; en el agua, casi nada."),
        ObjetoCuento("escafandra", "el traje", "un traje de buzo", "El traje",
            "El traje del buzo es de lona y goma, de una pieza, con puños de goma apretados en las muñecas y un cuello de cobre; se entra en él por el cuello, como en un saco; dentro, el buzo suda en tierra y se hiela en el fondo."),
        ObjetoCuento("botas", "las botas de plomo", "unas botas de plomo", "Las botas",
            "Las botas del buzo tienen suelas de plomo que pesan como dos piedras de molino pequeñas; en cubierta se camina con ellas como un pato; abajo, sujetan al buzo al fondo para que la corriente no se lo lleve."),
        ObjetoCuento("bomba", "la bomba del aire", "una bomba de aire", "La bomba",
            "La bomba del aire es una caja de madera con dos ruedas a los lados, que mueven dos hombres; empuja el aire por la manguera hasta el casco del buzo; si se para, el buzo se ahoga; si va despacio, el buzo se marea; por eso se cuenta en voz alta."),
        ObjetoCuento("cuerda", "la cuerda", "una cuerda de vida", "La cuerda",
            "La cuerda de vida va atada a la cintura del buzo y sube hasta la mano del ayudante; por ella se habla sin palabras: un tirón, estoy bien; dos, más aire; tres, más cuerda; cuatro, súbeme; muchos seguidos, súbeme ya."),
        ObjetoCuento("cuchillo", "el cuchillo", "un cuchillo de buzo", "El cuchillo",
            "El cuchillo del buzo es corto, ancho y afilado como una navaja de afeitar, y va en una funda de bronce sujeta a la pierna; sirve para cortar las esponjas por el pie, para soltar la cuerda si se enreda y, dicen, para los tiburones."),
        ObjetoCuento("red", "la red", "una red de esponjas", "La red",
            "La red de las esponjas es un saco de malla gruesa, con un aro de hierro en la boca, que el buzo lleva colgado al cinturón; cuando está llena, se tira de la cuerda y la suben por separado, para que el buzo no suba con el peso."),
        ObjetoCuento("esponja", "la esponja", "una esponja", "La esponja",
            "La esponja viva es un animal, negro y viscoso, que no se mueve y que huele mal; muerta, limpia y seca, es dorada y blanda, y la compran en Nueva York, en Londres y en París para lavar a los niños, los caballos y los coches; las mejores son las de lana."),
        ObjetoCuento("comboloi", "el comboloi", "un comboloi", "El comboloi",
            "El comboloi es un hilo con cuentas de ámbar o de hueso que los griegos pasan entre los dedos, una a una, sin rezar, solo para tener las manos ocupadas; el sonido de las cuentas es el sonido de los cafés de la isla."),
        ObjetoCuento("icono", "el icono", "un icono", "El icono",
            "El icono de san Nicolás, patrón de los marineros, es una tabla pequeña pintada con el santo de barba blanca y un libro en la mano; en el barco va clavado en la popa, con una lamparilla de aceite que no se apaga nunca, ni con tormenta."),
        ObjetoCuento("barco", "el barco", "un barco esponjero", "El barco",
            "El barco esponjero es una goleta corta y ancha, hecha por carpinteros griegos con madera de Florida, con un bote atado detrás; aguanta mal los temporales y bien las esponjas; algunos vinieron de la isla, pero la mayoría se hicieron aquí."),
        ObjetoCuento("pan", "el pan de barco", "un pan de barco", "El pan",
            "El pan de barco se cuece dos veces, en rodajas, hasta que queda duro como una teja; así no se pudre en semanas de mar; antes de comerlo se moja en agua del mar o en aceite; en la isla lo cuecen las mujeres para los hombres que se van."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas un momento, y luego se le van, porque en el fondo no hay noticias: solo agua, esponjas y el ruido del aire. A sus {edad} años, el mundo de arriba le parece más raro que el de abajo."),
        v("Piensa en su padre, que se quedó en el fondo del mar de Libia, buscando esponjas para otro, cuando él tenía doce años. Piensa que hace lo mismo que su padre, en otro mar. Piensa que su padre no tenía casco de cobre. Se consuela con eso."),
        v("{mundo} {nombre} piensa en la isla, en la casa blanca de la cuesta, en su madre que mira el mar cada tarde aunque el mar que mira no es este. Piensa que algún día volverá con dinero. Todos los del barco lo piensan. Pocos vuelven."),
        v("{mundo} A veces, en el fondo, {nombre} se para y mira hacia arriba, hacia la luz, y ve la quilla del barco como una sombra pequeña en el techo del mundo. Piensa que ahí arriba hay diez hombres que lo mantienen vivo. Les da las gracias. No se lo dice nunca."),
        v("Piensa en el dinero: cuánto le toca de las esponjas, cuánto manda a la isla, cuánto guarda, cuánto debe al capitán por el traje. Las cuentas de un buzo son sencillas: lo que bajas, lo que subes, lo que te queda. Y si un día no subes, nada."),
        v("{mundo} A sus {edad} años, {nombre} sabe muchas cosas que no sirven en tierra: leer el agua, oír la bomba, sentir la corriente con la espalda. Y no sabe muchas que sirven: leer, escribir, hablar inglés. Piensa que un día aprenderá. Siempre es otro día."),
    )

    private val MUNDOS = mapOf(
        "cometa" to listOf(
            "Se habla del cometa Halley, que vuelve cada setenta y seis años, y de si trae desgracias.",
            "Se dice que en la isla los turcos han subido otra vez los impuestos a los esponjeros.",
            "Se habla de los pescadores de Key West, que no quieren a los griegos en sus aguas.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde se cuelgan las últimas cuerdas de esponjas del palo, y el barco parece un árbol de Navidad de cosas raras. El sol se mete en el agua, rojo, enorme. Michalis enciende el farol. Alguien empieza a cantar bajito, y los demás le siguen."),
        v("Anochece sobre el golfo, y el agua se pone del color del vino de la isla, y luego negra. Se encienden los faroles de los otros barcos, lejos, uno aquí, otro allá. {nombre} los cuenta. Son como un pueblo esparcido en el agua."),
        v("Al caer la tarde {nombre} se sienta en la borda con los pies colgando sobre el agua, como hacía de niño en el muelle de la isla. Mira el sol ponerse. Por un momento, si entorna los ojos, el horizonte es el mismo que el de Kálymnos. Luego abre los ojos."),
        v("De noche, desde cubierta, se ven más estrellas que en ningún sitio: el cielo está lleno, como un pan con sésamo. El capitán sabe los nombres de algunas, los de la isla. {nombre} se sabe una: la que se mueve poco, la que guía a los marineros."),
        v("Anochece, y el agua alrededor del barco se ilumina de verde cuando la tocas, con una luz que no se sabe de dónde sale. Los del barco dicen que son las almas de los buzos muertos. {nombre} mete la mano y la saca llena de luz. La sacude. Se apaga."),
        v("La última luz del día se queda en las nubes del este, rosa, cuando el mar ya está oscuro. {nombre} se quita la camisa mojada, se pone la seca, se envuelve en la manta. El barco se balancea. Las esponjas gotean. Mañana, otra vez abajo."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_civil" to unDia(
            Parrafo.MANANA to "Hoy es el año nuevo de los americanos, y anoche, a las doce, los barcos del muelle tocaron las sirenas y tiraron cohetes. Los griegos lo celebran también, porque cualquier fiesta es buena, pero el año nuevo de verdad, el de san Basilio, llega dentro de dos semanas.",
            Parrafo.GENTE to "Esta mañana todo el pueblo está cansado. En el café de los griegos, los buzos toman café amargo para la cabeza y se cuentan lo que hicieron anoche, que es más de lo que hicieron. Alguien rompió una silla. Alguien cantó en la calle. Alguien se quedó dormido en un bote.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que en América hay dos años nuevos, dos Navidades y dos de todo, y que eso es lo que pasa cuando uno vive entre dos sitios: que no está del todo en ninguno. Piensa que por lo menos se come dos veces.",
        ),
        "navidad_vieja" to unDia(
            Parrafo.MANANA to "Hoy es Navidad para los griegos, que siguen el calendario viejo de la Iglesia, trece días detrás del de los americanos. Los americanos ya quitaron sus árboles. En la iglesia de San Nicolás, la misa empieza antes del alba y dura hasta que sale el sol. Huele a incienso y a lana mojada.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja, ni nadie: ni se cose, ni se carena, ni se toca la bomba. Es Navidad. Los buzos se ponen la ropa buena, que huele a baúl, y van a misa, y luego a comer a casa de quien tenga casa. Los que no tienen van a casa de los que tienen.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la Navidad de la isla, con nieve en lo alto del monte y el mar gris, y la iglesia llena de viejas con velas. Aquí hace calor, hay palmeras y la iglesia es de madera. Pero el pope canta lo mismo. Cierra los ojos y es la isla.",
        ),
        "san_basilio" to unDia(
            Parrafo.MANANA to "Hoy es san Basilio, el año nuevo de los griegos, y en todas las casas del barrio se parte la vasilópita, la torta del santo, con una moneda escondida dentro. El primer trozo es para Cristo, el segundo para la casa, y luego uno para cada uno. Al que le toque la moneda le irá bien el año.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es el día de san Basilio. Los del barco van de casa en casa, con la ropa buena, a desear buen año, y en cada casa les dan un trozo de torta y una copa. Al llegar a la última, ya no ven bien la moneda.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, con su trozo de torta en la mano, en la moneda: si le tocara, ¿qué le pediría al año? Que el fondo esté claro. Que la bomba no se pare. Que la cuerda no se enrede. No le toca. Se lo pide igual.",
        ),
        "epifania" to unDia(
            Parrafo.MANANA to "Hoy es la Epifanía, el día de las aguas, y el pope, con sus ropas doradas, lleva a todo el pueblo en procesión hasta el agua del río, con los iconos, las velas, los niños de blanco y una paloma blanca en una jaula. Los americanos del pueblo vienen a mirar desde lejos.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: hoy el pope tira una cruz de madera al agua, y los muchachos se tiran a buscarla. {nombre} se tira con ellos, aunque ya no es un muchacho. El agua de enero está fría. Bucea sin casco, como su padre. No la encuentra. Sale riéndose.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, secándose al sol, que el que encuentra la cruz tiene suerte todo el año. Piensa que un buzo necesita más suerte que nadie. Piensa que el año que viene se tirará antes que nadie. Lo piensa cada año.",
        ),
        "zarpa" to unDia(
            Parrafo.MANANA to "Hoy salen los barcos a los bancos de esponjas. Desde antes del alba el muelle está lleno de mujeres, de niños, de viejos, de perros. El pope bendice cada barco con agua bendita y una rama de albahaca. Se cargan los últimos sacos de pan, los barriles de agua, las cuerdas.",
            Parrafo.PUEBLO to "Los barcos salen por el río uno detrás de otro, con las velas hinchadas y las banderas griegas y americanas en los palos. En la orilla, las mujeres agitan pañuelos blancos hasta que los barcos se hacen pequeños. Luego se quedan un rato mirando el agua vacía.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando el pueblo que se aleja, que ahora vienen meses de agua y de cielo y de fondo, y que no volverá a ver una calle hasta el verano. Piensa en los que se fueron otros años y no volvieron. Se santigua. Mira hacia delante.",
        ),
        "pascua" to unDia(
            Parrafo.MANANA to "Hoy es la Pascua de los griegos, la fiesta más grande del año, y anoche, a medianoche, en la cubierta, el capitán encendió una vela del farol y dijo «Cristo ha resucitado», y todos contestaron «en verdad ha resucitado», y se dieron un beso, y se rompieron huevos rojos.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja ni baja: es Pascua. El capitán ha comprado un cordero en el último puerto, y se asa entero en cubierta, dándole vueltas en un espetón, desde que amanece. Los diez del barco se turnan para darle vueltas. Huele en todo el golfo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la Pascua de la isla, en las campanas de todas las iglesias a la vez, en los cohetes, en su madre con el vestido negro de fiesta. Aquí hay cordero, hay huevos, hay canciones. Pero no hay campanas. Michalis toca la sartén con un cucharón. No es lo mismo. Casi.",
        ),
        "veinticinco_marzo" to unDia(
            Parrafo.MANANA to "Hoy es el 25 de marzo de los griegos, que con el calendario viejo cae hoy: el día en que Grecia se levantó contra los turcos. La isla de {nombre} todavía no es de Grecia, pero en el barco se iza la bandera azul y blanca en el palo, junto a la americana.",
            Parrafo.GENTE to "Esta tarde, en cubierta, se cantan las canciones de la guerra de la independencia, las que cantaban los abuelos, y el capitán cuenta, como cada año, cómo su abuelo luchó en el mar contra los turcos con un barco de pesca. Cada año el barco es más grande y los turcos más.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la isla, que todavía no es de Grecia, y en los griegos de Grecia, que tienen su rey y su bandera. Piensa que algún día la isla será griega. Todos los del barco lo piensan. Piensa que él es griego en América, y que en América eso no le importa a nadie.",
        ),
        "lonja" to unDia(
            Parrafo.MANANA to "Hoy el barco ha vuelto a Tarpon Springs, a mitad de año, a vender lo que lleva en la bodega. Se descargan las esponjas en sacos, se llevan a la lonja, se extienden en el suelo del cuarto del capitán. Vienen los compradores. El capitán se pone su chaqueta buena.",
            Parrafo.GENTE to "Esta tarde el capitán reparte las partes en el café de los griegos, en dólares de papel, contando en voz alta, y cada uno firma con su nombre o con una cruz. Las esponjas se han vendido por calidades: las de lana, las amarillas, las de hierba, las de dedo. El capitán dice que lo han robado. Lo dice cada año.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su parte, que hoy le pagan, en dólares de papel, y en lo que va a hacer con ella: mandar a la isla, guardar, pagar al capitán. Piensa que medio año de fondo cabe en un sobre. Piensa que es poco. Piensa que en la isla es mucho.",
        ),
        "cuatro_julio" to unDia(
            Parrafo.MANANA to "Hoy es el 4 de julio de los americanos, y el barco todavía está en el muelle, antes de volver a salir. El pueblo está lleno de banderas de rayas y estrellas, hay un desfile con una banda de música y los niños tiran petardos en la calle. Los griegos ponen su bandera al lado. Por si acaso.",
            Parrafo.GENTE to "Esta tarde hay carreras de botes en el río, y los barcos griegos echan su bote contra los de los americanos. Los del barco de {nombre} reman como si les fuera la vida. Llegan segundos. Lo celebran como si hubieran ganado. Los americanos no entienden por qué.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los americanos tienen una fiesta para celebrar que se fueron de los ingleses. Piensa que los de la isla tendrán una el día que la isla sea griega. Piensa que algún día. Luego tira un petardo, porque hoy es americano, por lo menos un rato.",
        ),
        "dormicion" to unDia(
            Parrafo.MANANA to "Hoy es la Dormición de la Virgen, con el calendario viejo, la fiesta grande del verano en la isla, y en el barco se enciende una vela delante del icono de la popa. El capitán se lava la cara y se pone una camisa limpia. Todos hacen lo mismo. Hay una camisa limpia para cada uno, una sola.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja ni baja: es la fiesta de la Virgen, y en la isla dicen que la Virgen no quiere buzos en el agua el día de su fiesta. Se limpian los trajes, se repara la red, se pesca con sedal desde la borda. Se descansa. Es raro descansar en el mar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la fiesta de la Dormición en la isla, con la procesión de la Virgen por las calles, las mesas en la plaza, los bailes hasta el amanecer. Piensa que su madre estará en la iglesia, rezando por él. Le reza a la Virgen por su madre. Están en paz.",
        ),
        "vuelta" to unDia(
            Parrafo.MANANA to "Hoy los barcos vuelven a Tarpon Springs para el invierno, cargados de esponjas hasta los palos. Se ve el pueblo desde lejos, y el campanario de San Nicolás, y la gente en el muelle. Al entrar en el río, todos los barcos tocan sus cuernos a la vez. El pueblo contesta con las campanas.",
            Parrafo.PUEBLO to "En el muelle esperan las mujeres, los niños, los viejos, el pope con su cruz. Se buscan las caras en los barcos. Algunas mujeres sonríen. Alguna se queda quieta, mirando un barco en el que falta alguien. Las demás se le acercan, sin decir nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, al pisar el muelle, que ha vuelto otra vez, que el fondo lo ha dejado subir un año más. Las piernas le tiemblan en tierra, como a todos. Se arrodilla y toca el suelo. Los americanos se ríen. Los griegos, no.",
        ),
        "san_nicolas" to unDia(
            Parrafo.MANANA to "Hoy es san Nicolás, con el calendario viejo: el santo de los marineros y de la iglesia del pueblo, la fiesta más grande del barrio griego. Hay misa larga con todos los popes de la comarca, procesión con el icono del santo por las calles y bendición de los barcos amarrados.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es el santo del pueblo y de la mitad de los hombres, que se llaman Nikolaos. {nombre} es uno de ellos: hoy es su santo. Los del barco le cantan, le tiran del pelo y le invitan a una copa en cada café del muelle.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su santo, que lo ha cuidado un año más en el fondo, y le pone una vela en la iglesia, gruesa como su brazo. Piensa en su padre, que también se llamaba Nikolaos y que también le ponía velas. A su padre no lo cuidó. Pone otra vela, por su padre.",
        ),
        "fin_anio" to unDia(
            Parrafo.MANANA to "Hoy es el último día del año de los americanos, y el pueblo prepara la fiesta de esta noche: los cafés compran barriles, los muchachos juntan cohetes, los barcos amarrados se adornan con farolillos. Los griegos dicen que el año de verdad termina con san Basilio. Pero la fiesta es la fiesta.",
            Parrafo.GENTE to "Esta noche se espera el año en el muelle, con los barcos, con los cohetes, con las sirenas. Los griegos cantan sus canciones, los americanos las suyas, y a las doce todos gritan y nadie sabe en qué idioma. {nombre} grita en griego. Le contestan en inglés.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que se va, en las bajadas, en las esponjas, en los que se quedaron abajo, en los que subieron. Piensa en el año que viene. Le pide lo de siempre: subir. Subir siempre, despacio, peldaño a peldaño.",
        ),

        // ---------- Los de la temporada 1 ----------
        "traje" to unDia(
            Parrafo.MANANA to "Hoy {nombre} estrena un traje de buzo nuevo, que el capitán le compró a crédito en Tarpon Springs antes de salir, y que todavía huele a goma y a tienda. Es más tieso que el viejo. Para meterse dentro necesita a dos hombres, que tiran y empujan, y se ríen.",
            Parrafo.TRABAJO to "A esta hora baja por primera vez con el traje nuevo, y en el fondo nota que no tiene ninguna gota de agua dentro, que los puños aprietan bien, que el cuello cierra. El viejo tenía tres parches y una fuga en la rodilla izquierda. Este es como una casa nueva.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el traje viejo, que le salvó la vida tantas veces, y que ahora se usará para hacer parches. Piensa que debería darle las gracias. Lo dobla con cuidado. Se ríe de sí mismo. Lo dobla igual.",
        ),
        "twain" to unDia(
            Parrafo.MANANA to "Hoy llega al barco, por un barco que vuelve de Tarpon Springs, un periódico griego de Nueva York de hace una semana. Lo lee en voz alta el único del barco que sabe leer bien. Dice que ha muerto un escritor americano muy famoso, que nació cuando pasó el cometa y que dijo que se iría con él.",
            Parrafo.GENTE to "Esta tarde, en cubierta, se discute si es verdad que un hombre puede saber cuándo se va a morir. Michalis dice que sí, que su abuelo lo supo, y que mandó hacer el ataúd una semana antes. El capitán dice que eso es de viejas. Michalis dice que su abuelo era un hombre.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el escritor, que vino con el cometa y se fue con él, como un pasajero que sube y baja en el mismo puerto. Piensa que es una manera bonita de irse. Mira el cielo, buscando el cometa. Todavía no se ve bien. Ya se verá.",
        ),
        "cometa" to unDia(
            Parrafo.MANANA to "Hoy, dicen los periódicos que llegan de tierra, la Tierra pasa por la cola del cometa, y en algunos sitios la gente compra píldoras contra el gas del cometa y tapa las rendijas de las puertas. En el barco nadie ha comprado píldoras. Pero nadie tiene ganas de bajar al fondo.",
            Parrafo.ANOCHECER to "Esta noche nadie del barco quiere dormir. Todos están en cubierta, mirando el cielo, donde la cola del cometa cruza las estrellas como un velo blanco. Michalis reza en voz alta. El capitán fuma. {nombre} tiene el icono en la mano. No pasa nada. Pasa la noche.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el cielo está lleno de cosas que no entiende, como el fondo. Piensa que el cometa vuelve cada setenta y seis años, y que cuando vuelva él ya no estará. Piensa que alguien estará mirándolo desde un barco. A lo mejor un hijo suyo. A lo mejor un nieto.",
        ),
        "hondo" to unDia(
            Parrafo.MANANA to "Hoy el capitán ha llevado el barco a un banco de esponjas que está más hondo que los demás, a veinticinco brazas, donde casi nadie baja, y donde por eso hay más esponjas. Ha elegido a {nombre} para bajar primero. Es un honor. Es un peligro. Es lo mismo.",
            Parrafo.TRABAJO to "A esta hora baja a veinticinco brazas por primera vez, y abajo la luz es azul oscura, casi de noche, y el frío entra por el traje como agua. El fondo está cubierto de esponjas grandes como sombreros. Corta deprisa. Tiene poco tiempo. Sube muy despacio, contando.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, en cubierta, temblando de frío, que ha bajado más hondo que nunca y que ha subido. Le duele la cabeza. Le zumban los oídos. Pero ha subido. El capitán le da una palmada en la espalda sin decir nada. Es lo más que dice el capitán.",
        ),
        "paralisis" to unDia(
            Parrafo.MANANA to "Hoy, en el barco de al lado, han sacado del agua al buzo de un tirón, porque subía mal, y en cubierta se cayó y no se pudo levantar. Tiene las piernas muertas. Es un muchacho de Symi, de veinte años, que bajó demasiado tiempo y subió demasiado deprisa.",
            Parrafo.GENTE to "Esta tarde los del barco de {nombre} reman hasta el barco de al lado a ver al muchacho. Está tumbado en cubierta, tapado con una manta, mirando el cielo. Habla. Dice que no siente nada de la cintura para abajo. Su capitán lo lleva a tierra. Nadie sabe si volverá a andar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el muchacho, que podía ser él, que puede ser él cualquier día. Piensa en la enfermedad de los buzos, que nadie entiende: unos suben deprisa y no les pasa nada, otros suben despacio y se quedan así. Piensa que el fondo elige. No sabe cómo.",
            Parrafo.ANOCHECER to "Al caer la tarde, en el barco de {nombre} nadie canta. Se come callado. El capitán dice que mañana se baja igual, y que se sube más despacio. Todos asienten. {nombre} se va a la proa y mira el agua negra mucho rato. Luego se duerme, con el icono en la mano.",
        ),
        "huracan" to unDia(
            Parrafo.MANANA to "Hoy el cielo del sur amaneció de un color amarillo raro, y el mar tenía unas olas largas que venían de muy lejos, sin viento. El capitán miró el cielo un rato largo y dijo que viene un huracán de Cuba. Se levantan las anclas. Se busca refugio.",
            Parrafo.TRABAJO to "A esta hora navegan a toda vela hacia una ensenada de la costa, entre manglares, con el viento cada vez más fuerte y la lluvia cada vez más de lado. Llegan. Echan tres anclas. Amarran el barco a los árboles. Esperan. El viento grita entre las cuerdas como una mujer.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, agarrado al palo, empapado, con la ensenada blanca de espuma alrededor, que el mar de Florida es más traicionero que el de la isla: allí avisa, aquí te engaña. Reza a san Nicolás. Promete una vela. El barco aguanta. La vela será grande.",
        ),
        "carta" to unDia(
            Parrafo.MANANA to "Hoy un barco que viene de Tarpon Springs trae el correo, y hay una carta para {nombre}, de la isla, con su nombre escrito por la mano del maestro del pueblo. Hace dos meses que salió. {nombre} la guarda sin abrir hasta la tarde, cuando haya terminado de trabajar.",
            Parrafo.GENTE to "Esta tarde, en cubierta, le pide al que sabe leer que se la lea. Su madre dice que está bien, que las hermanas están bien, que ha llovido, que la cabra parió, que el maestro le manda saludos. Que no suba deprisa. Que se acuerde de rezar. Que vuelva. {nombre} escucha con los ojos cerrados.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su madre, dictándole la carta al maestro, en la mesa de la cocina, eligiendo cada palabra. Piensa que tiene que aprender a leer, para leer sus cartas solo, sin que nadie más oiga lo que ella le dice. Se lo promete. Otra vez.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "buzo_esponjas", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + PUERTO_MANANA + ENFERMO_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + PUERTO_TRABAJO + ENFERMO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + PUERTO_PUEBLO + ENFERMO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + PUERTO_ANOCHECER + ENFERMO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
