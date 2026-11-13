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
 * El cuento de Lou (Louise, de Baltimore), pianista de un club de jazz de Harlem, de 1926 a 1930. Sigue la gramática y el
 * tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día va al revés: duerme por la mañana, ensaya por la tarde y toca de noche. Come dos veces: esos textos valen a
 *   cualquiera de sus horas.
 * - Dos estados: «gira» (temporada 3, el verano de gira por los teatros negros del sur) y «sin_club» (temporada 5, los
 *   meses sin club fijo, tocando en fiestas de alquiler).
 * - El club lee el arco «publico» (cuánta gente lo llena: baja con el crac).
 * - No repite lo que ya dice «Su vida»: levantarse pasado el mediodía, la mano izquierda del stride, el metro de las
 *   nueve, el alcohol en tazas de café, las siete horas de piano, la sesión de después hasta que sale el sol.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG6.kt`):
 * - El club de Sully, desde `club` (temporada 1) hasta `cierra_club` (5); después, otro club más pequeño desde
 *   `nuevo_club` (5).
 * - Rafe, desde que lo conoce (`rafe`, temporada 2); juntos desde `juntos` (2); en Chicago desde `rafe_chicago` (4)
 *   hasta `rafe_vuelve` (5); casados desde `boda` (5).
 * - Las fiestas que vuelven cada año no nombran a Rafe ni a Sully.
 */
internal object PiezasLou {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(12, 1, 2)
    private val VERANO = setOf(6, 7, 8)

    private val MANANA = listOf(
        v("{nombre} se despierta con el ruido de la calle, que a esta hora ya lleva mucho rato despierta: los carros del hielo, los vendedores de periódicos, los niños que salen de la escuela. Se tapa la cabeza con la almohada. No sirve. Se levanta con los ojos medio cerrados."),
        v("{nombre} abre los ojos y lo primero que ve son sus manos encima de la sábana, y las abre y las cierra diez veces, despacio, como le enseñó su primera maestra de piano en Baltimore. Los dedos están rígidos. Los calienta debajo del brazo. Las manos son su sueldo."),
        v("{nombre} se despierta en el cuarto de la casa de Mama Dell con olor a café y a pan de maíz que sube por la escalera. La persiana está bajada y el sol entra por las rendijas en rayas. Se queda un momento escuchando la casa: una radio, una pelea, alguien que canta en el baño."),
        v("{nombre} se despierta con una canción en la cabeza, una que tocó anoche sin pensar, en la última hora, y que ya no se acuerda de dónde salió. Se sienta en la cama y la tararea, para no perderla. Busca un lápiz. Apunta las notas en el margen de un periódico."),
        v("A {nombre} la despierta Mama Dell golpeando la puerta con los nudillos, porque dejó un recado: una llamada al teléfono de la tienda de abajo, de un músico que quiere verla. {nombre} dice que ya va. Se vuelve a dormir. Mama Dell golpea otra vez."),
        v("{nombre} se despierta con la ventana cerrada y el cuarto helado, porque el radiador de la casa de Mama Dell funciona cuando quiere. Se pone el abrigo encima del camisón y calienta agua en el hornillo para lavarse. Por la ventana, Harlem está gris y blanco.", meses = INVIERNO),
        v("{nombre} se despierta empapada, con la sábana pegada al cuerpo, porque en el cuarto no se puede respirar: el calor de Nueva York en verano no se va ni de noche. Abre la ventana. Entra el ruido y entra el calor. No entra aire.", meses = VERANO),
    )

    /** El verano de gira por el sur, en la temporada 3 (estado «gira»). */
    private fun gi(texto: String, hacer: Hacer? = null) = v(texto, estado = "gira", hacer = hacer)

    private val GIRA_MANANA = listOf(
        gi("{nombre} se despierta en el vagón, sentada, con la cabeza apoyada en el hombro de Hattie, la cantante, y no sabe en qué estado está. Por la ventanilla pasan campos de algodón, chozas de madera, una iglesia blanca. El tren va despacio. El vagón es el de los negros, al lado de la máquina."),
        gi("{nombre} se despierta en una casa que no conoce, en un cuarto que le alquiló una señora de la iglesia, porque en el pueblo no hay hotel donde la dejen dormir. Hay un cuadro de Jesús en la pared y una colcha de retazos. Huele a pollo frito. La señora ya está en la cocina."),
        gi("{nombre} se despierta en el camerino del teatro, encima de dos sillas juntas, porque llegaron tarde y no había otro sitio. Le duele la espalda entera. Por la puerta entra la luz del escenario vacío. Alguien ya está ensayando en el piano. Desafina. Lo van a afinar ella."),
    )

    private val GIRA_TRABAJO = listOf(
        gi("A esta hora toca en el foso de un teatro de pueblo, lleno hasta el techo de gente negra que ha venido de toda la comarca, con la ropa del domingo. La compañía hace su revista: cómicos, bailarines, la cantante, un número de perros. Ella los acompaña a todos. Al público le gusta todo.", Hacer.TRABAJO),
        gi("A esta hora toca en un piano que tiene tres teclas muertas y el pedal atado con un alambre, en un teatro de madera en el que hace tanto calor que el sudor le cae en las teclas. Toca alrededor de las teclas muertas, como se camina alrededor de un charco. Nadie se da cuenta.", Hacer.TRABAJO),
        gi("A esta hora el público pide otra vez la canción de la cantante, y otra, y la compañía la repite tres veces. Luego, cuando se van los cómicos, alguien grita que toque {nombre} sola. Toca un stride de Harlem, rápido, lleno de notas. La gente se pone de pie. Así, cada noche.", Hacer.TRABAJO),
        gi("A esta hora va en el tren, entre un pueblo y otro, en el vagón de los negros, que es viejo, con los asientos de madera y la carbonilla de la máquina entrando por las ventanillas. Los de la compañía juegan a las cartas, duermen, cosen los trajes. Ella mira por la ventanilla.", Hacer.CAMINO),
        gi("A esta hora come en la cocina de la señora que les alquila los cuartos, porque en el restaurante del pueblo no la atienden: pollo frito, judías verdes, pan de maíz, té con hielo y azúcar. La señora le pregunta por Nueva York como si fuera la luna. {nombre} le cuenta.", Hacer.COMER),
        gi("A esta hora come de pie, en el andén de una estación, lo que les vende por la ventanilla una mujer con una cesta: pollo frío envuelto en papel de periódico, galletas, una gaseosa caliente. En la cantina de la estación pone «solo blancos». No se mira el cartel. Se come.", Hacer.COMER),
        gi("A esta hora duerme en el tren, sentada, con la cabeza contra la ventanilla y el bolso apretado entre las rodillas, porque en la gira se duerme donde se puede. Cada vez que el tren frena, se despierta. Cada vez que arranca, se vuelve a dormir.", Hacer.DORMIR),
        gi("A esta hora ensaya con la compañía en el escenario vacío, con las luces apagadas: la cantante prueba un tono, los bailarines cuentan los pasos, el director grita. {nombre} cambia la canción de tono cuatro veces hasta que la cantante dice que sí. Mañana será otro tono.", Hacer.LIBRE),
        gi("A esta hora lava su ropa en una palangana, en el cuarto alquilado, y la tiende en la ventana, y se arregla las uñas, que se le rompen con los pianos malos. Cose un botón del vestido del escenario. Las medias tienen tres carreras. Las pinta de laca para que no corran.", Hacer.CUIDAR),
        gi("A esta hora está en el porche de la casa donde duermen, con los de la compañía y los vecinos, que han venido a ver a la gente del teatro. Alguien saca una guitarra. {nombre} no tiene piano, y canta, que no sabe. Se ríen todos. Los grillos hacen más ruido que ellos.", Hacer.GENTE),
    )

    private val GIRA_PUEBLO = listOf(
        gi("Los pueblos del sur se parecen todos: una calle mayor con tiendas de blancos, una vía de tren, un barrio negro al otro lado de la vía, con calles de tierra, iglesias de madera y un teatro donde una vez por semana llega una compañía de Nueva York. Esta semana, la suya."),
        gi("El teatro de esta semana es un almacén de algodón con un escenario al fondo, un telón de terciopelo comido por las polillas y sillas de todas las casas del barrio. Sobre la puerta hay un cartel pintado a mano con los nombres de la compañía. El de {nombre} va el penúltimo. Pequeño."),
    )

    private val GIRA_ANOCHECER = listOf(
        gi("Al caer la tarde el pueblo se llena de gente que viene al teatro: en carros, en mulas, a pie, con los niños en brazos. Las mujeres llevan sombreros con flores. Los hombres, camisas blancas almidonadas. Antes de la función hay una hora de saludos en la puerta. Es la fiesta del mes."),
        gi("Anochece en el sur, de golpe, con un cielo naranja y luego morado, y salen las luciérnagas de los campos, miles, como chispas. {nombre} nunca las había visto tantas. En Harlem no hay luciérnagas. Hay letreros. Se queda mirándolas hasta que la llaman para la función."),
    )

    /** Los meses sin club fijo, en la temporada 5 (estado «sin_club»). */
    private fun sc(texto: String, hacer: Hacer? = null) = v(texto, estado = "sin_club", hacer = hacer)

    private val SIN_CLUB_MANANA = listOf(
        sc("{nombre} se despierta sin saber si esta noche tiene trabajo. Mira la lista que tiene clavada en la pared: las fiestas de alquiler de la semana, con la dirección y lo que pagan. Esta noche, una en la calle 139. Paga tres dólares y la cena. Hay que ir."),
        sc("{nombre} se despierta y cuenta el dinero que queda en la lata de galletas debajo de la cama. Alcanza para el cuarto de esta semana. La que viene, ya se verá. Se viste con el vestido de siempre, el negro, que va con todo, y sale a preguntar por los clubes."),
    )

    private val SIN_CLUB_TRABAJO = listOf(
        sc("A esta hora toca en una fiesta de alquiler, en el piso de una familia que no puede pagar el alquiler este mes: han quitado los muebles del salón, han puesto una mesa con pollo y ponche, y cobran la entrada en la puerta. {nombre} toca junto a la ventana. La sala está llena. El dinero es para el casero.", Hacer.TRABAJO),
        sc("A esta hora toca en un piano de pared de un piso de la calle 141, con las teclas amarillas y una foto de boda encima, mientras treinta personas bailan en un cuarto para diez. El suelo tiembla. Los vecinos de abajo golpean el techo con una escoba. Nadie les hace caso.", Hacer.TRABAJO),
        sc("A esta hora toca en una fiesta donde hay otro pianista, y se turnan, y luego se pican, y acaban tocando a cuatro manos una cosa que no se sabe ninguno de los dos. La gente deja de bailar para mirarlos. Al final les pagan a los dos. A cada uno, la mitad.", Hacer.TRABAJO),
        sc("A esta hora camina por Harlem de una fiesta a otra, con el bolso bajo el brazo, porque esta noche le han salido dos: la primera hasta la una, la segunda desde las dos. Las calles están más oscuras que antes. Hay más hombres en las esquinas, sin nada que hacer.", Hacer.CAMINO),
    )

    private val SIN_CLUB_LIBRE = listOf(
        sc("A esta hora ensaya en su cuarto, en el piano de las cuotas, con la sordina puesta para no molestar a los huéspedes que duermen de día. Prepara las canciones que piden en las fiestas, que no son las del club: más viejas, más del sur, para bailar pegados. Las toca hasta que le salen sin pensar.", Hacer.LIBRE),
        sc("A esta hora recorre los clubes que quedan abiertos, uno por uno, preguntando en la puerta si buscan pianista. En uno le dicen que vuelva el lunes. En otro, que ya tienen. En otro, que no pueden pagar ni al que tienen. Apunta cada respuesta en un cuaderno. El lunes volverá.", Hacer.LIBRE),
    )

    private val SIN_CLUB_PUEBLO = listOf(
        sc("Harlem ya no es lo que era hace un año: hay tiendas cerradas con tablas en la Séptima Avenida, más gente pidiendo en las esquinas, colas para el pan en las iglesias. Los clubes que quedan abiertos tienen menos gente y pagan menos. Las fiestas de alquiler, en cambio, son más que nunca."),
        sc("Las fiestas de alquiler se anuncian con tarjetas pequeñas que se reparten en la calle y en el metro, con versos y dibujos: «Ven a sacudirte la tristeza, la casa de la señora tal, sábado por la noche». En cada tarjeta, la dirección y el piso. En cada piso, un piano."),
    )

    private val SIN_CLUB_ANOCHECER = listOf(
        sc("Al caer la tarde {nombre} se arregla delante del espejo roto del cuarto: el pelo planchado, los labios rojos, el collar de perlas falsas. Se mira. Está bien. Nadie diría que no sabe si mañana tendrá para el alquiler. Esa es la idea. Sale a la calle con la cabeza alta."),
        sc("Anochece, y desde la ventana del cuarto se ven las ventanas de enfrente encenderse una a una, y en alguna ya suena un piano, otra fiesta, otro pianista. {nombre} escucha. Toca bien. Toca mejor que ella en la mano derecha. Se apunta la dirección para la semana que viene."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} El tiempo cambia la música: con frío se toca más rápido; con calor, más lento."),
        v("{nombre} mira el cielo desde la ventana, entre dos tejados. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Mama Dell dice que el tiempo de Nueva York lo inventó alguien que odiaba a la gente."),
        v("{cielo} {de_estacion} En Baltimore, dice su madre, el tiempo era más educado."),
        v("{de_estacion} {cielo} Para una pianista el tiempo está en las manos: el frío las cierra, el calor las suelta."),
        v("Una persona que duerme de día ve el cielo a medias, por la tarde, al salir. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y alto, de un azul que hace brillar las ventanas.",
            "Hace un día claro, y el sol pega en los ladrillos rojos de las casas.",
            "No hay nubes, y la luz es dura y blanca, sin sombra.",
        ),
        Clima.NUBES to listOf(
            "El cielo está gris, del color de la acera, y no se sabe qué hora es.",
            "Pasan nubes rápidas desde el río, y el sol sale y se esconde.",
            "Está nublado, con un cielo bajo y sucio de humo.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y la gente corre por la avenida con periódicos en la cabeza.",
            "Cae una lluvia fina y constante que hace brillar las calles como charol.",
            "Llueve a cántaros, y el agua baja por las escaleras del metro como un río.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta de verano, con truenos que hacen temblar los cristales.",
            "Truena sobre la ciudad, y la lluvia cae tan fuerte que rebota en la acera.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y las calles se quedan blancas y calladas, como si se hubiera ido la gente.",
            "Cae nieve, que en la calle se vuelve barro gris en una hora.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla del río, y los letreros luminosos se ven borrosos, como en un sueño.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento helado que baja por las avenidas como por un tubo.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en las ventanas de Harlem aparecen macetas con geranios.",
            "En primavera la gente vuelve a sentarse en los escalones de las casas.",
            "Es el tiempo de los sombreros nuevos y de los paseos por la avenida.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el calor se pega a la ropa desde que sale el sol.",
            "En verano la ciudad huele a asfalto caliente y a basura.",
            "Es el tiempo de dormir en las escaleras de incendios.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando el aire se aclara y las noches refrescan.",
            "En otoño los árboles del parque se ponen amarillos, y el viento los deja desnudos.",
            "Es el tiempo en que los clubes se llenan otra vez.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando el viento del río corta la cara y los radiadores silban.",
            "En invierno los hombres hacen fuego en bidones en las esquinas.",
            "Es el tiempo del abrigo, los guantes y las manos que no quieren abrirse.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora toca para los que bailan, con el pie marcando el tiempo en el suelo y la cabeza un poco ladeada, escuchando la pista. Cuando la pista se calienta, ella acelera un poco. Cuando se cansa, afloja. Los que bailan no lo saben. Bailan lo que ella quiere.", hacer = Hacer.TRABAJO),
        v("A esta hora acompaña a una cantante que canta un blues lento, y {nombre} le deja espacio entre las frases, y le contesta con la mano derecha, como en una conversación. La cantante la mira de reojo y sonríe. En una mesa del fondo, una mujer llora en silencio.", hacer = Hacer.TRABAJO),
        v("A esta hora toca con la banda: trompeta, clarinete, trombón, batería y ella, que es el suelo donde pisan todos. Cuando la trompeta se pierde, ella la encuentra. Cuando la batería corre, ella la frena. Nadie la mira. Sin ella, todo se cae.", hacer = Hacer.TRABAJO),
        v("A esta hora un señor blanco de esmoquin, de los que suben a Harlem por la noche, le pide una canción de Broadway, de las de los blancos, y le deja un billete encima del piano. {nombre} la toca. Luego, sin parar, la convierte en otra cosa, más rápida, más suya. El señor no se da cuenta.", hacer = Hacer.TRABAJO),
        v("A esta hora toca sola, en el descanso de la banda, para los pocos que se quedan en las mesas: una cosa lenta, inventada, que no tiene nombre. Es su rato. El humo sube hacia la lámpara. Los camareros dejan de recoger los vasos para escucharla. Luego vuelve la banda.", hacer = Hacer.TRABAJO),
        v("A esta hora toca en un piano que un cliente borracho ha regado de ginebra, y las teclas se pegan, y ella toca igual, más fuerte. A su lado, el baterista se ríe, porque sabe. Cuando termina la canción, se limpia los dedos en el pañuelo. Huelen a ginebra toda la noche.", hacer = Hacer.TRABAJO),

        v("A esta hora come en la cocina de la casa de Mama Dell, en la mesa larga de los huéspedes: verduras con tocino, pan de maíz, judías, un trozo de pollo si es domingo. Come deprisa, porque los demás huéspedes hablan mucho y ella no tiene ganas. Mama Dell le guarda siempre el trozo de en medio.", hacer = Hacer.COMER),
        v("A esta hora come en un restaurante de la avenida Lenox, de los que tienen la carta escrita en una pizarra, sentada en la barra: costillas de cerdo, puré, un café. A su lado, otros músicos, porteros, chóferes, gente de la noche. Todos comen a horas raras. Nadie lo pregunta.", hacer = Hacer.COMER),
        v("A esta hora come en su cuarto, de pie junto a la ventana, un bocadillo de jamón y un vaso de leche, mirando la calle. Come sin hambre, porque hay que comer. Abajo, en la acera, unos niños juegan a la rayuela. Una mujer los llama desde una ventana. Ninguno sube.", hacer = Hacer.COMER),

        v("A esta hora duerme con un pañuelo negro sobre los ojos, porque la persiana deja pasar el sol, y un algodón en cada oído, porque Harlem de día no se calla. Duerme hondo. Sueña con un piano que tiene más teclas que el suyo, muchas más, y no le alcanzan las manos.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y en el cuarto de al lado un vendedor de seguros se levanta, se lava, canta, se va a trabajar. Ella no lo oye. Llegó a casa cuando él se despertaba. Se cruzaron en la escalera. Él le dijo buenos días. Ella le dijo buenas noches.", hacer = Hacer.DORMIR),
        v("A esta hora duerme de lado, abrazada a la almohada, con las manos dentro de unos guantes de algodón untados de vaselina, para que no se le agrieten. Lo hacía su madre, que lavaba ropa ajena en Baltimore. {nombre} lo hace por el piano. Es la misma piel.", hacer = Hacer.DORMIR),

        v("A esta hora ensaya en el piano del club vacío, con las sillas encima de las mesas y el olor de la noche anterior todavía en el aire. Repite un pasaje difícil veinte veces, cada vez un poco más rápido. El que barre el suelo silba lo que ella toca. Lo silba mal.", hacer = Hacer.LIBRE),
        v("A esta hora escucha discos en el gramófono de Mama Dell, en el salón de la casa, con la oreja casi pegada a la bocina: los de los pianistas buenos, para robarles lo que pueda. Pone el mismo pedazo diez veces. Mama Dell dice que va a gastar el disco. Lo gasta.", hacer = Hacer.LIBRE),
        v("A esta hora estudia partituras en la cama, con el lápiz detrás de la oreja, tocando con los dedos sobre la colcha lo que ve en el papel. Sabe leer música bien, que es raro entre los pianistas de Harlem. Su madre pagó las clases lavando ropa. Le debe cada nota.", hacer = Hacer.LIBRE),

        v("A esta hora se arregla para la noche delante del espejo: se plancha el pelo con un peine caliente que huele a quemado, se pinta los labios, se pone el vestido de flecos y los zapatos de tacón. Se mira las manos. Se lima una uña. Se mira otra vez. Ya es otra.", hacer = Hacer.CUIDAR),
        v("A esta hora lava su ropa en el lavabo del pasillo y la tiende en una cuerda que cruza el cuarto, y plancha el vestido de la noche con la plancha de Mama Dell, que hay que pedir con tiempo. Cose un fleco que se soltó. Los flecos se sueltan siempre.", hacer = Hacer.CUIDAR),
        v("A esta hora se da un baño en la bañera del piso, que se comparte entre seis huéspedes y que hay que reservar como un teatro. El agua sale tibia. Se queda dentro hasta que alguien golpea la puerta. Luego otro rato. Se frota las manos con aceite. Las manos, primero.", hacer = Hacer.CUIDAR),

        v("A esta hora va en el metro de la avenida Lenox, entre gente que vuelve del trabajo y gente que va al suyo, con el bolso en las rodillas y las partituras dentro. Un hombre le pregunta si es actriz. Ella le dice que es enfermera. Así no pregunta más.", hacer = Hacer.CAMINO),
        v("A esta hora camina por la calle 133 hacia el club, entre letreros que se encienden, porteros que fuman en las puertas de los sótanos, coches grandes de los que bajan blancos con abrigos de piel. Los porteros la saludan. Es de las de dentro. Eso se nota.", hacer = Hacer.CAMINO),
        v("A esta hora sube las escaleras del metro a la calle y Harlem le cae encima: la música de diez sitios a la vez, el olor de las costillas asadas, los vendedores de lotería clandestina, las parejas del brazo. Respira hondo. Ya está. Ya es de noche. Ya es suya.", hacer = Hacer.CAMINO),

        v("A esta hora está en la sesión de después, en el sótano de otro club, con los músicos de toda la calle, tocando para ellos. El que toca mejor se queda en el piano. El que no, se aparta. {nombre} se queda un buen rato. Cuando se levanta, se levanta porque quiere.", hacer = Hacer.GENTE),
        v("A esta hora está en un restaurante que abre toda la noche, con los músicos, comiendo pollo con gofres, que es lo que se come cuando ya no se sabe qué hora es. Se habla de quién toca dónde, de quién paga, de quién no paga. Se ríe mucho. Se está cansada. No se quiere ir.", hacer = Hacer.GENTE),
        v("A esta hora vuelve a casa por la avenida vacía, con las piernas cansadas y la cabeza llena de música, mientras los lecheros dejan las botellas en las puertas. En el cielo, por el este, una raya clara. {nombre} se quita los zapatos y camina descalza el último tramo.", hacer = Hacer.GENTE),
    )

    private val PUEBLO = listOf(
        v("Harlem es una ciudad negra dentro de Nueva York: doscientas mil personas venidas del sur, del Caribe, de Baltimore como ella, en casas de piedra parda que eran de los blancos. Aquí hay médicos, poetas, predicadores, pandilleros y pianistas. Todos se cruzan en la misma acera. {entorno}"),
        v("{entorno} La calle 133, entre la avenida Lenox y la Séptima, es la calle de los clubes: un sótano detrás de otro, con su puerta, su portero y su escalera hacia abajo. De día parece una calle cualquiera. De noche, la llaman la calle de la jungla."),
        v("La avenida Lenox, de noche, es un río de luces, coches, abrigos de piel, uniformes de portero y gente que va de un sitio a otro sin prisa. Por ella bajan del metro los blancos de abajo, que suben a Harlem a ver lo que no ven en su casa. {entorno}"),
        v("{entorno} La casa de Mama Dell es una casa de piedra parda de cuatro pisos, con doce huéspedes, un baño por piso y un salón con piano y gramófono donde no se puede fumar los domingos. Las reglas están en un cartel en la entrada. Nadie las cumple todas."),
        v("Harlem de día es otro sitio: iglesias, barberías, tiendas de comestibles, niños que van a la escuela, mujeres que tienden ropa en las azoteas. Los que viven de noche pasan por él como fantasmas, con gafas oscuras, buscando un café. {entorno}"),
        v("{entorno} En la Séptima Avenida, los domingos, se pasea: los hombres con sombrero y bastón, las mujeres con sus mejores vestidos, los niños de la mano. Es el desfile de Harlem. Los predicadores predican en las esquinas, subidos a una caja. Los músicos duermen."),
    )

    /** Cuánta gente llena el club (arco «publico»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("publico", 0.0, 0.3, frases = listOf(
            "El club está casi vacío: unas pocas mesas, la banda entera y los camareros mirando la puerta.",
            "En el club sobran sillas y faltan clientes, y los que vienen piden poco y se van pronto.",
        )),
        Entorno("publico", 0.3, 0.5, frases = listOf(
            "El club tiene medio público, que es más de lo que se espera ahora, y menos de lo que hace falta.",
            "En el club hay gente, pero hay huecos entre las mesas por donde pasa el aire.",
        )),
        Entorno("publico", 0.5, 0.75, frases = listOf(
            "El club se llena la mayoría de las noches, sobre todo los viernes y los sábados.",
            "En el club hay ambiente: las mesas llenas, la pista con gente, el humo bajo.",
        )),
        Entorno("publico", 0.75, 1.0, frases = listOf(
            "El club no cabe un alma: hay cola en la escalera y el portero elige a quién deja bajar.",
            "En el club se baila hasta entre las mesas, y los camareros pasan las bandejas por encima de las cabezas.",
        )),
    )

    private val GENTE = listOf(
        v("Mama Dell es la dueña de la casa de huéspedes, una mujer enorme de Carolina del Sur que lleva un delantal blanco planchado y que sabe todo lo que pasa en el edificio y en la calle. A {nombre} la trata como a una hija que le salió artista. Le cobra igual."),
        v("Su madre vive en Baltimore, en la misma casa de siempre, y le escribe cada semana una carta que empieza con un versículo de la Biblia y termina preguntando si va a la iglesia. {nombre} contesta que sí. No va. Su madre lo sabe. Sigue preguntando."),
        v("Boots es el baterista con quien toca casi siempre, un hombre flaco de Nueva Orleans que nunca para de mover las piernas, ni sentado, ni dormido. Tiene un diente de oro y una risa de urraca. Es lo más parecido a un hermano que {nombre} tiene en Nueva York."),
        v("Hattie es la cantante del cuarto de enfrente en la casa de Mama Dell, una muchacha de Georgia con una voz que hace callar a los borrachos y un novio distinto cada mes. Le presta a {nombre} sus pendientes. {nombre} le presta sus partituras. Ninguna devuelve nada."),
        v("El viejo Pops es el rey del stride de Harlem, un hombre grande con un bombín que no se quita nunca, ni para tocar, y que tiene una mano izquierda que suena como dos. Todos los pianistas jóvenes quieren tocar como él. Pops dice que primero hay que sufrir. Que luego, ya se verá."),
        v("Sully es el dueño del club, un irlandés gordo de cara roja que paga a la policía, paga a los de la ginebra y paga a los músicos, en ese orden. No sabe nada de música. Sabe de gente: sabe que cuando toca {nombre} el público se queda. Eso le basta.", desde = "club", hasta = "cierra_club"),
        v("El dueño del club nuevo es un antiguo boxeador callado, que mira la caja cada hora y apaga las luces de la sala cuando no hay nadie para ahorrar. Paga poco, pero paga. En estos tiempos, eso es mucho. A {nombre} le dice señorita. Nadie le decía señorita desde Baltimore.", desde = "nuevo_club"),
        v("Rafe es un trompetista de Nueva Orleans que toca en un club de la misma calle y que viene a la sesión de después a escucharla. Tiene una sonrisa ancha y una trompeta abollada. La acompaña a casa. En la puerta, se quita el sombrero. Se va silbando.", desde = "rafe", hasta = "juntos"),
        v("Rafe y {nombre} viven juntos en un cuarto más grande de la casa de Mama Dell, con una ventana a la calle y un piano viejo en una esquina. Mama Dell dijo que en su casa no, y luego dijo que bueno. Rafe le arregla los grifos a Mama Dell. Así se paga el pecado.", desde = "juntos", hasta = "rafe_chicago"),
        v("Rafe está en Chicago con una orquesta, por un tiempo, y le escribe cartas cortas con faltas de ortografía y dibujos de trompetas. Dice que Chicago es frío y que la orquesta toca demasiado limpio. Dice que vuelve pronto. {nombre} guarda las cartas en la caja de las partituras.", desde = "rafe_chicago", hasta = "rafe_vuelve"),
        v("Rafe ha vuelto de Chicago, más flaco y con la trompeta empeñada, y {nombre} la desempeñó con su dinero sin preguntar. Ahora vuelven a vivir juntos en el cuarto grande. Se pelean por cosas pequeñas. Se reconcilian tocando. Es la manera que tienen.", desde = "rafe_vuelve", hasta = "boda"),
        v("Rafe, su marido, toca la trompeta en el mismo club que ella, ahora, y al terminar la noche vuelven juntos a casa por la avenida, del brazo, cansados. Ella lleva las partituras. Él, la trompeta. En la puerta de Mama Dell, él le quita los zapatos para que suba sin ruido.", desde = "boda"),
    )

    private val OBJETO = listOf(
        v("Una pianista no lleva su instrumento encima, así que lleva otras cosas. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de los músicos viejos de Harlem, que lo aprendieron en el sur."),
        v("Hoy {nombre} se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Los blancos que suben a Harlem por la noche no se fijan en eso. Ven lo de fuera."),
        v("{dato_objeto} En Harlem cada cosa tiene su historia, y la historia sale más cara que la cosa."),
        v("La noche se hace con poco, pero ese poco hay que cuidarlo. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("piano", "el piano", "un piano", "El piano",
            "El piano vertical de un club de Harlem suele ser de segunda o tercera mano, con la tapa quemada de cigarrillos y las teclas amarillas como dientes; algunos pianistas le ponen chinchetas en los martillos para que suene más brillante, como un banjo."),
        ObjetoCuento("partitura", "la partitura", "una partitura", "La partitura",
            "Las partituras de las canciones de moda se venden en las tiendas de música por treinta y cinco centavos, con una portada de colores y una cantante sonriendo; los pianistas de los clubes casi no las miran: se las aprenden de oído y luego las cambian."),
        ObjetoCuento("sombrero", "el sombrero", "un sombrero cloché", "El sombrero",
            "El sombrero de moda es el cloché, una campana de fieltro que se encaja hasta las cejas y que solo queda bien con el pelo corto; las mujeres de Harlem lo llevan con una pluma, una flor o un broche, y lo inclinan un poco hacia el ojo izquierdo."),
        ObjetoCuento("ficha", "la papeleta del empeño", "una papeleta de empeño", "La papeleta",
            "La papeleta del empeño es un trozo de cartón con un número que da el prestamista a cambio de lo que se le deja: un reloj, un abrigo, una trompeta; los músicos de Harlem empeñan y desempeñan sus cosas cada mes, según vaya el trabajo."),
        ObjetoCuento("taza", "la taza", "una taza de café", "La taza",
            "En los clubes de Harlem el alcohol se sirve como se puede: en tazas, en vasos de agua, en botellas de gaseosa; la ginebra se hace en bañeras, y el whisky viene de Canadá en barcos que nadie ve; si llega la policía, todo el mundo está tomando café."),
        ObjetoCuento("disco", "el disco", "un disco de pizarra", "El disco",
            "Los discos de setenta y ocho revoluciones son de pizarra, pesados y frágiles, y tienen una canción por cada cara, de tres minutos; los de música negra se venden en Harlem y en el sur con etiquetas de colores, y se llaman discos de raza."),
        ObjetoCuento("radio", "la radio", "una radio", "La radio",
            "La radio es nueva en las casas, una caja de madera con válvulas que se encienden como bombillas y un altavoz con forma de flor; algunas emisoras transmiten en directo desde los clubes, y una pianista de Harlem puede sonar en una granja de Ohio."),
        ObjetoCuento("collar", "el collar", "un collar de perlas", "El collar",
            "El collar de perlas largo, de dos vueltas, que llega hasta la cintura, es el de las mujeres que bailan el charlestón: al saltar, las perlas saltan con ellas; las de verdad cuestan una fortuna; las de cristal, cincuenta centavos, y brillan igual."),
        ObjetoCuento("zapatos", "los zapatos", "unos zapatos de tacón", "Los zapatos",
            "Los zapatos de baile tienen una tira sobre el empeine con un botón, para que no se salgan al saltar, y un tacón medio; las pianistas los llevan igual, aunque no bailen, porque el pie del pedal también se ve, y en Harlem todo se ve."),
        ObjetoCuento("panuelo", "el pañuelo", "un pañuelo", "El pañuelo",
            "El pañuelo de la pianista es de algodón blanco, grande, y va siempre encima del piano o metido en la manga: para el sudor de la frente, para las teclas pegajosas, para los dedos; al final de la noche está gris; se lava cada mañana."),
        ObjetoCuento("cigarrillo", "el cigarrillo", "un cigarrillo", "El cigarrillo",
            "En los clubes se fuma tanto que el humo forma una nube bajo el techo, y las lámparas parecen lunas en la niebla; los músicos dejan el cigarrillo encendido en el borde del piano mientras tocan, y la madera tiene una fila de quemaduras como teclas negras."),
        ObjetoCuento("metronomo", "el metrónomo", "un metrónomo", "El metrónomo",
            "El metrónomo es una pirámide de madera con una aguja que va de un lado a otro, tic, tac, al tiempo que se le pide; los profesores de piano lo usan para que los alumnos no corran; los músicos de jazz lo usan poco: el tiempo lo llevan en el pie."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas un momento, entre dos canciones, y luego vuelve al piano, que no sabe de política ni de periódicos. A sus {edad} años, ha aprendido que la música no cambia el mundo. Pero lo hace soportable una noche más."),
        v("Piensa en Baltimore, en la casa de su madre, en el piano de la iglesia donde aprendió a tocar los himnos los domingos. Piensa en lo que diría su madre si viera dónde toca ahora. Piensa que su madre, a escondidas, a lo mejor sonreiría."),
        v("{mundo} {nombre} piensa en los pianistas hombres de Harlem, que la miran por encima del hombro hasta que la oyen tocar. Luego la miran de otra manera. Piensa que tiene que tocar el doble de bien para que la tomen la mitad en serio. Toca el doble de bien."),
        v("{mundo} A veces, tocando, {nombre} se olvida de dónde está: del humo, del ruido, de los blancos borrachos, de la policía en la puerta. Solo quedan las teclas y una cosa que sale por los dedos sin que ella la piense. Esos momentos son los que paga el resto."),
        v("Piensa en el dinero: lo que gana, lo que manda a Baltimore, lo que debe del piano, lo que cuesta el cuarto. Piensa que una pianista de Harlem gana más que su madre lavando ropa, y menos que el portero del club. Piensa que algún día grabará un disco. Algún día."),
        v("{mundo} A sus {edad} años, {nombre} sabe que tiene algo que no se aprende: un oído que oye lo que todavía no ha sonado. Y sabe que eso no basta, que hace falta suerte, contactos, un hombre que firme los papeles. Piensa en eso, y luego deja de pensar, y toca."),
    )

    private val MUNDOS = mapOf(
        "harlem" to listOf(
            "Se habla de la Ley Seca, que no cumple nadie, ni los policías que la tienen que hacer cumplir.",
            "Se dice que los blancos ricos de abajo suben cada noche a Harlem como quien va al zoológico.",
            "Se habla de Marcus Garvey, el de los barcos para volver a África, que está preso en Atlanta.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde Harlem se enciende: primero las ventanas, luego los letreros de los teatros, luego las puertas de los sótanos. La gente sale a la calle con otra ropa y otra cara. {nombre} sale de casa con el bolso apretado y la cabeza ya en la primera canción."),
        v("Anochece sobre la avenida Lenox, y el cielo se pone de color de ciruela entre los edificios, y el primer saxofón de la noche suena en algún sótano, todavía solo, probando. {nombre} lo oye desde la escalera del metro. Conoce al que toca. Toca bien."),
        v("Al caer la tarde las mujeres de la casa de Mama Dell se cruzan en el pasillo con rulos en la cabeza, unas que van a trabajar de noche y otras que vuelven de trabajar de día. Se saludan, se prestan horquillas, se cuentan dos cosas. {nombre} sale la última."),
        v("De noche, desde la puerta del club, se ve la calle llena de coches con chófer esperando a sus dueños, que están dentro, bailando con quien en su barrio no les dejarían ni hablar. {nombre} mira a los chóferes, que fuman y esperan. Algunos son negros. Esperan fuera."),
        v("Anochece, y en la azotea de la casa de enfrente un hombre toca la armónica, solo, mirando la ciudad. {nombre} se asoma a la ventana a escucharlo un rato antes de irse. Es un blues del campo, viejo, lento. De los que tocaba su abuelo. Se lo lleva en la cabeza al club."),
        v("La última luz del día se queda en lo alto de los edificios de abajo, en el centro, que brillan como dientes de oro, mientras Harlem ya está a oscuras. {nombre} piensa que allá abajo se termina el día, y aquí empieza. Se pone los guantes. Sale.", meses = INVIERNO),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y {nombre} se acuesta cuando ya es de día, después de tocar desde las nueve de anoche hasta que la gente dejó de bailar porque no se tenía en pie. A las doce en punto la banda tocó una canción vieja escocesa y todo el club cantó, abrazado, en un idioma que nadie entendía.",
            Parrafo.GENTE to "Al salir del club, la avenida está llena de gente con gorros de papel, serpentinas en el pelo y botellas que nadie esconde. Los músicos de toda la calle se juntan en un restaurante a comer huevos con jamón. Se brinda con café. Con café de verdad, por una vez.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza, y le pide pocas cosas: trabajo, salud en las manos, un piano mejor. Piensa en su madre, que a esta hora ya estará en la iglesia, rezando por su hija la pianista. Piensa que por lo menos alguien reza por ella.",
        ),
        "pascua" to unDia(
            Parrafo.MANANA to "Hoy es domingo de Pascua, y Harlem entero sale a la Séptima Avenida con la ropa nueva: los sombreros de las mujeres son jardines, los trajes de los hombres son de colores claros, los niños van tiesos como estatuas. Es el desfile de Pascua. {nombre} se levanta antes, por una vez, para verlo.",
            Parrafo.GENTE to "Esta tarde Mama Dell hace jamón al horno para todos los huéspedes, con huevos pintados para los niños de la casa, y obliga a todos a sentarse a la mesa y a rezar antes de comer. {nombre} reza. Hattie también. Boots se ríe en medio de la oración. Mama Dell le pega con la cuchara.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la Pascua de Baltimore, en la iglesia de su madre, en el coro con túnicas blancas, en ella al piano tocando los himnos con las manos temblando de miedo. Piensa que ya no le tiemblan las manos. Piensa que a veces le gustaría que le temblaran.",
        ),
        "cuatro_julio" to unDia(
            Parrafo.MANANA to "Hoy es el 4 de julio, y desde que amanece huele a pólvora: los niños tiran petardos en las calles, en los portales, en los cubos de basura. Hay desfile con bandas de música y veteranos negros de la guerra, con sus uniformes y sus medallas, que caminan muy derechos.",
            Parrafo.TRABAJO to "A esta hora toca para una sala llena hasta la puerta, con todas las ventanas abiertas y el calor de una caldera. Se tocan marchas patrióticas en ritmo de jazz, y la gente baila el himno. Un viejo se escandaliza. Lo sacan a bailar. Se le pasa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el día de la independencia, y en de quién es. Piensa en su abuelo, que nació esclavo en Maryland. Piensa en los veteranos del desfile, que lucharon en Francia y volvieron a un país que no los deja votar en el sur. Toca la marcha igual. Más rápido.",
        ),
        "dia_trabajo" to unDia(
            Parrafo.MANANA to "Hoy es el día del Trabajo, el último fin de semana del verano, y los sindicatos desfilan por la Quinta Avenida con sus estandartes. Los músicos de los clubes no desfilan: casi ninguno tiene sindicato, ni horario, ni patrón que firme nada. {nombre} tampoco.",
            Parrafo.TRABAJO to "A esta hora toca en el club, que esta noche está lleno de gente que vuelve de la playa, de Coney Island, quemada por el sol, con arena en los zapatos y ganas de que el verano no se acabe. Piden las canciones del verano. {nombre} las toca todas, una detrás de otra, sin parar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que trabaja más que nadie y que nadie la cuenta como trabajadora. Los que desfilan hoy tienen horario, sindicato y domingo. Ella tiene un piano y una noche. Piensa que a lo mejor un día habrá un desfile de pianistas. Piensa que llegarían tarde.",
        ),
        "accion_gracias" to unDia(
            Parrafo.MANANA to "Hoy es Acción de Gracias, y la casa de Mama Dell huele a pavo desde el amanecer. Mama Dell se ha levantado a las cuatro para meterlo en el horno, y ha hecho pan de maíz, boniatos con azúcar, verduras con tocino, pastel de calabaza. La mesa larga tiene mantel. Una vez al año.",
            Parrafo.GENTE to "Esta tarde se sientan a la mesa todos los huéspedes, y los que no tienen familia en Nueva York, que son casi todos, y Mama Dell trincha el pavo como un general. Cada uno dice por qué da gracias. Boots da gracias por el pavo. Hattie, por el novio de este mes. {nombre}, por las manos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su madre, que hoy come sola en Baltimore, o con las vecinas de la iglesia. Le mandará una postal mañana. Piensa que la familia es la que tienes a la mesa, y que esta mesa, con sus ruidos y sus peleas, es la suya. Pide más boniatos.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, y en el salón de Mama Dell hay un árbol pequeño con bolas de cristal y velas, y debajo un regalo para cada huésped, envuelto en papel de periódico: unos calcetines, una bufanda, un pañuelo. {nombre} recibe guantes. Mama Dell sabe lo de las manos.",
            Parrafo.TRABAJO to "A esta hora toca en el club, que esta noche tiene acebo en las paredes y un árbol en la esquina, y está lleno de gente que no tiene adónde ir. La banda toca villancicos en ritmo de jazz. Una mujer canta Noche de paz en una mesa, sola, desafinada. Nadie se ríe.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en las Navidades de Baltimore, en la iglesia, en la naranja y el caramelo que le daban en la escuela dominical. Piensa que es la primera Navidad que pasa lejos de su madre. Luego piensa que no, que es la quinta. Se le olvida contar.",
        ),
        "nochevieja" to unDia(
            Parrafo.MANANA to "Hoy es Nochevieja, la noche más larga del año en el club, y {nombre} duerme todo lo que puede, porque esta noche se toca hasta que se acabe la gente. Mama Dell le sube un café a la cama. Le dice que se lo tome despacio. Le dice que la noche es larga.",
            Parrafo.TRABAJO to "A esta hora toca en el club lleno, con serpentinas colgando del techo, globos, gorros de papel, matasuegras y una cuenta atrás que grita todo el mundo. A las doce se apagan las luces, se oye un beso en cada mesa, y la banda arranca. Esta noche no se termina nunca.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, en el último minuto del año, mientras todos cuentan hacia atrás, en todo lo que ha tocado este año: cuántas canciones, cuántas noches, cuántas teclas. Piensa que no lo puede contar. Piensa que por eso se cuenta hacia atrás: porque hacia delante no se puede.",
        ),

        // ---------- Los de la temporada 1 ----------
        "club" to unDia(
            Parrafo.MANANA to "Hoy {nombre} empieza a tocar en un club nuevo de la calle 133, en un sótano que antes era una carbonera y que ahora tiene paredes pintadas de rojo, mesas pequeñas, una pista del tamaño de un colchón y un piano vertical casi nuevo. El dueño es un irlandés que se llama Sully.",
            Parrafo.TRABAJO to "A esta hora toca su primera noche en el club, con una banda que no conoce, y los primeros veinte minutos se miran unos a otros como perros que se huelen. Luego la trompeta se lanza, ella le contesta, y la banda se suelta. Al final de la noche ya son una banda. Casi.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un club nuevo es como un piano nuevo: hay que aprender dónde suena bien y dónde no. Este suena bien en la esquina de la izquierda, cerca de la escalera. Piensa que se va a quedar aquí un tiempo. No sabe cuánto. Nadie sabe nunca cuánto.",
        ),
        "fiesta_alquiler" to unDia(
            Parrafo.MANANA to "Hoy le han pedido que toque, después del club, en su primera fiesta de alquiler: un piso de la Séptima Avenida donde una viuda con tres hijos no tiene para pagar el mes. Han quitado los muebles, han puesto una cortina en la puerta de la cocina, y en la cocina venden pollo frito, chitterlings y ginebra.",
            Parrafo.GENTE to "Esta noche, ya tarde, toca en el salón del piso, en un piano de pared que la viuda alquiló para la noche, y el piso está tan lleno que se baila en el pasillo y en el baño. Llegan otros pianistas, se turnan, se pican. Al amanecer la viuda cuenta el dinero en la mesa. Le alcanza para dos meses.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esta música es otra cosa que la del club: aquí nadie paga por mirarla, todos pagan por estar. Aquí los que bailan son los vecinos. Piensa que esta es la música de verdad de Harlem, la que no ven los blancos. Piensa que le gusta más.",
        ),
        "savoy" to unDia(
            Parrafo.MANANA to "Hoy abre el Savoy, en la avenida Lenox, un salón de baile tan grande que ocupa una manzana entera, con una pista de mármol y espejos, dos escenarios para dos orquestas, luces de colores y una alfombra en la escalera. Dicen que caben cuatro mil personas. Dicen que dejan entrar a negros y blancos juntos.",
            Parrafo.ANOCHECER to "Al caer la tarde {nombre} va con Boots y con Hattie a ver la puerta del Savoy antes de ir al club, y hay una cola que da la vuelta a la manzana, con la gente vestida como para una boda. Por las ventanas sale la música de las dos orquestas. Se quedan mirando hasta que se les hace tarde.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que en el Savoy tocan orquestas de catorce músicos, con arreglos escritos, y que ella toca en un sótano con cinco. Piensa que algún día le gustaría tocar en un sitio así, con los espejos multiplicando a la gente. Piensa que allí no hay pianistas mujeres. Todavía.",
        ),
        "duelo" to unDia(
            Parrafo.MANANA to "Hoy, en la sesión de después, el viejo Pops la ha retado a un duelo de pianos, delante de todo el mundo. En Harlem se hace así: dos pianistas, un piano, y se turnan hasta que uno no puede más. El público decide. Dicen que Pops no ha perdido nunca. {nombre} ha dicho que sí.",
            Parrafo.GENTE to "Esta madrugada toca contra Pops, y él toca una cosa, y ella la toca más rápido, y él toca otra con la izquierda sola, y ella no puede. Pops sonríe. Toca una tercera con las manos cruzadas. La gente grita. Ella se levanta. Ha perdido. Pops le da la mano. La gente aplaude a los dos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, volviendo a casa, que ha perdido, y que nunca había aprendido tanto en una noche. Piensa en la mano izquierda de Pops, que suena como dos. Mañana practicará la izquierda. Pasado también. Piensa que algún día volverá a retarlo. Se lo dirá a Pops. Pops se reirá.",
        ),
        "piano" to unDia(
            Parrafo.MANANA to "Hoy ha llegado a la casa de Mama Dell el piano que {nombre} se ha comprado a plazos: un piano vertical viejo, de caoba, con las velas de los candelabros todavía puestas y una tecla rota. Lo han subido entre cuatro hombres por la escalera, con cuerdas, gritando, rayando la pared.",
            Parrafo.TRABAJO to "Esta tarde toca su piano por primera vez, en su cuarto, con la ventana abierta. Suena desafinado, con un zumbido en los graves, y la tecla rota se queda abajo. Pero es suyo. Toca una escala. Toca un himno de Baltimore. Toca un blues. Mama Dell sube a escuchar desde la puerta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en las cuotas del piano, que son muchas, y en que tendrá que tocar muchas noches para pagarlo. Piensa que da igual. Piensa en su madre, que nunca tuvo un piano, que pagaba a la maestra para que la dejara practicar en el suyo. Le escribe esta noche. Le dirá que tiene piano.",
        ),
        "calor" to unDia(
            Parrafo.MANANA to "Hoy hace un calor que no se puede respirar, el tercer día seguido, y nadie en Harlem ha dormido esta noche. La gente ha subido a dormir a las azoteas, a las escaleras de incendios, a los parques. {nombre} ha dormido en la azotea de Mama Dell, sobre una manta, con Hattie y otras dos huéspedes.",
            Parrafo.TRABAJO to "A esta hora toca en el club, que está a cuarenta grados, con un ventilador que mueve el humo de un lado a otro sin quitarlo. La banda toca en camiseta. Ella se ha recogido el pelo y se ha quitado las medias. Las teclas resbalan de sudor. El público baila igual. Más despacio.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Baltimore, que también tiene calor, pero tiene el puerto, y por la noche sube un poco de brisa del agua. Aquí no sube nada. Piensa en un vaso de limonada con hielo, en un río, en una nevada. Piensa que en enero se quejará del frío.",
        ),
        "ederle" to unDia(
            Parrafo.MANANA to "Hoy los periódicos dicen que una muchacha de Nueva York, hija de un carnicero alemán, ha cruzado a nado el canal de la Mancha, de Francia a Inglaterra, y que lo ha hecho más rápido que todos los hombres que lo cruzaron antes. Los vendedores de periódicos lo gritan en todas las esquinas.",
            Parrafo.GENTE to "Esta noche en el club no se habla de otra cosa. Boots dice que no puede ser, que una mujer no nada tanto. Hattie le dice que una mujer canta tanto como él toca, y que nada más. Los camareros apuestan a ver cuántas horas tardó. Gana el que dijo catorce.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la muchacha, sola en el agua fría durante catorce horas, nadando hacia una costa que no se ve. Piensa que hay que estar un poco loca para hacer eso. Piensa que hay que estar un poco loca para ser pianista en Harlem. Se siente de su equipo.",
        ),
        "valentino" to unDia(
            Parrafo.MANANA to "Hoy los periódicos dicen que ha muerto Valentino, el actor del cine mudo, el de los ojos, el que hacía de jeque y de bailarín de tango, a los treinta y un años, en un hospital de Nueva York. Las mujeres lloran en las calles. Los periódicos dicen que algunas se han desmayado al saberlo.",
            Parrafo.GENTE to "Esta noche Hattie llora en el camerino del club, porque estaba enamorada de Valentino, como todas, y se pinta los ojos dos veces porque se le corre el rímel. {nombre} le toca un tango en el descanso, para ella. La gente no sabe por qué un tango. Hattie sí.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Valentino, que no era de aquí, que vino de Italia sin nada, que bailaba en los salones por dinero antes de ser famoso. Piensa que no era tan distinto de un músico de Harlem. Piensa que la fama también se muere a los treinta y uno. Se le pasa enseguida.",
        ),
        "redada" to unDia(
            Parrafo.MANANA to "Hoy la policía ha cerrado el club, a la una de la mañana, en medio de una canción: han bajado la escalera cinco agentes con porras, han encendido todas las luces, han roto botellas, han apuntado nombres. Sully ha hablado con el jefe en voz baja, en un rincón, mucho rato.",
            Parrafo.TRABAJO to "A esta hora no toca: el club está cerrado con un candado y un papel pegado en la puerta. {nombre} y la banda se quedan en la acera, con los instrumentos, sin saber qué hacer. Boots propone ir a otra sesión. Van. Allí toca gratis. Mañana, dice Sully, se abre otra vez.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la Ley Seca es una cosa rara: el alcohol está prohibido, todo el mundo bebe, la policía cobra por no ver, y de vez en cuando, para que se vea, cierra un club una noche. Piensa que Sully pagará más este mes. Piensa que lo pagarán los músicos.",
        ),
        "madre" to unDia(
            Parrafo.MANANA to "Hoy ha llegado su madre de Baltimore, en el tren de la mañana, con un sombrero negro, un vestido de iglesia y una cesta con pollo frito para el viaje. Ha venido a verla tocar. Se lo pidió {nombre} hace un año, y su madre dijo que nunca. Y ha venido. No dice por qué.",
            Parrafo.TRABAJO to "A esta hora toca en el club con su madre sentada en la primera mesa, tiesa como un palo, con su sombrero de iglesia y un vaso de agua de verdad, que pidió Sully especialmente. {nombre} toca primero un himno, con ritmo de jazz. Su madre lo reconoce. Mueve un pie. Solo un pie.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando a su madre, que es la primera vez que la ve oírla tocar de verdad, sin himnos, sin iglesia, sin miedo. Al final de la noche su madre le dice que toca muy fuerte. Y luego, en la puerta, que toca muy bien. {nombre} se acordará de eso toda la vida.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "pianista", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + GIRA_MANANA + SIN_CLUB_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + GIRA_TRABAJO + SIN_CLUB_TRABAJO + SIN_CLUB_LIBRE,
            Parrafo.PUEBLO to PUEBLO + GIRA_PUEBLO + SIN_CLUB_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + GIRA_ANOCHECER + SIN_CLUB_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
