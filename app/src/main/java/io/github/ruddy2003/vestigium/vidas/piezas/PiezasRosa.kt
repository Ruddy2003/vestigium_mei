package io.github.ruddy2003.vestigium.vidas.piezas

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Genero
import io.github.ruddy2003.vestigium.vidas.ObjetoCuento
import io.github.ruddy2003.vestigium.vidas.Parrafo
import io.github.ruddy2003.vestigium.vidas.Piezas
import io.github.ruddy2003.vestigium.vidas.TextoEspecial
import io.github.ruddy2003.vestigium.vidas.Variante

/*
 * El cuento de Rosa, secretaria en la Ciudad de México hacia 1960. Sigue la gramática y el tono de `PiezasDemeas.kt`,
 * que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Trabaja en un despacho de abogados del centro: toma dictado en taquigrafía, escribe a máquina con papel carbón y
 *   contesta el teléfono. Va en tranvía. Come en casa de su mamá, en la colonia Guerrero, y vuelve a la oficina.
 * - No tiene arcos: el párrafo del pueblo no usa `{entorno}`.
 * - No repite lo que ya dice «Su vida»: el tranvía, las ocho horas, los cinco millones, la Torre Latinoamericana, la
 *   comida de su mamá, el refresco, las radionovelas, la única televisión de la vecindad, los siete hijos de su
 *   generación, la secundaria y las fotonovelas.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG2.kt`):
 * - Arturo es su novio hasta `boda` (temporada 2) y su marido desde entonces. Le pide matrimonio en `arturo_pide` (1).
 * - Vive en la vecindad de su mamá hasta `vuelve_acapulco` (2); desde entonces, en su departamento de Santa María la
 *   Ribera.
 * - Claudia nace en la temporada 3 (`nace_claudia`); el embarazo va de `espera_rosa` (2) a `nace_claudia`.
 * - Chela va a la secundaria desde `chela_secundaria` (1). Beto trabaja en un taller desde `beto_taller` (2).
 * - Los estados: «acapulco» (la luna de miel, temporada 2) e «incapacidad» (las ocho semanas con Claudia, temporada 3).
 * - Las fiestas que vuelven cada año no nombran a Arturo ni a Claudia, ni dicen dónde vive Rosa.
 */
internal object PiezasRosa {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A {nombre} la despierta el radio de algún vecino, que ya está tocando boleros a todo volumen antes de las seis. Se queda un momento en la cama oyendo la canción, que se sabe entera, y la tararea mientras busca las pantuflas con los pies."),
        v("{nombre} se levanta antes de que salga el sol, se lava la cara con agua fría y se mira en el espejito redondo de la pared. Se hace el chongo, se pone las medias con cuidado de no correrlas, que cuestan una fortuna, y se pinta los labios como le enseñó Lupita."),
        v("Por la ventana se oye pasar al camión de la basura, con su campana, y luego al panadero en bicicleta, con el canasto de pan en la cabeza. {nombre} le compra dos conchas desde la puerta, todavía en bata, y se come una de pie, con café con leche."),
        v("La primera luz llega gris y polvosa sobre las azoteas, entre tinacos, jaulas de pájaros y ropa tendida. Desde ahí arriba se ve la ciudad que se despierta: humo de los camiones, campanas de las iglesias y, a lo lejos, cuando el aire está limpio, los volcanes."),
        v("{nombre} plancha la blusa del día en la mesa, sobre una cobija doblada, con la plancha eléctrica que se compró con su primer aguinaldo. Es su mejor blusa o la segunda mejor, y en la oficina todas saben cuál es cuál. Hoy le toca la segunda."),
        v("Antes de salir, {nombre} se persigna frente a la Virgen de Guadalupe del calendario de la pared, que regaló la tortillería, y se echa un poco de agua de colonia en las muñecas. Agarra la bolsa, los guantes y el suéter, por si refresca, y sale corriendo."),
        v("En el patio de la vecindad ya hay fila en los lavaderos y el olor de los primeros chiles que se tuestan en algún comal. Doña Carmen le da a {nombre} un taco de frijoles envuelto en papel de estraza, por si le da hambre, y le dice que se cuide de los hombres del tranvía.", hasta = "vuelve_acapulco"),
        v("{nombre} se levanta sin hacer ruido en su departamento de dos piezas, le deja a Arturo el café en la estufa de petróleo y sale. Todavía no se acostumbra a tener una puerta propia, que se cierra con llave, y a que nadie le grite desde el patio que se le hace tarde.", desde = "vuelve_acapulco"),
    )

    /** La luna de miel en Acapulco (estado «acapulco», temporada 2). */
    private val ACAPULCO = listOf(
        v("{nombre} se despierta en un cuarto de hotel de Acapulco, chico y limpio, con un ventilador en el techo y el ruido del mar entrando por la ventana. Arturo duerme a su lado. Se queda mirándolo un rato, y luego al mar, que nunca había visto, y no sabe a cuál de los dos mirar más.", estado = "acapulco"),
        v("A esta hora está en la playa de Caleta con Arturo, en traje de baño, con el agua hasta la cintura y miedo de soltarse de su mano. Las olas son pequeñas y tibias. {nombre} se ríe cada vez que una le moja la cara, como una niña.", estado = "acapulco", hacer = Hacer.TRABAJO),
        v("A esta hora comen pescado a la talla en una enramada de la playa, con los pies en la arena, y toman agua de coco en el coco. Un niño les vende collares de conchas, y Arturo le compra uno para que el niño se vaya, y se lo pone a ella.", estado = "acapulco", hacer = Hacer.COMER),
        v("A esta hora pasean por la costera, tomados de la mano, viendo los hoteles nuevos, los coches de los turistas gringos y los barcos de pesca que vuelven. Arturo le explica cómo están hechos los hoteles. Ella no escucha nada y le dice que sí.", estado = "acapulco", hacer = Hacer.CAMINO),
        v("A esta hora ven a los clavadistas de La Quebrada tirarse desde las rocas al mar, desde una altura que da vértigo, con una antorcha en la mano. {nombre} se tapa los ojos en cada salto y los abre para ver si salió.", estado = "acapulco", hacer = Hacer.LIBRE),
        v("A esta hora se arregla en el cuartito del hotel para salir a cenar, con el vestido de flores que se hizo para el viaje y el collar de conchas. El espejo está manchado y la luz es mala. No importa: Arturo la mira como si fuera la artista de una película.", estado = "acapulco", hacer = Hacer.CUIDAR),
        v("A esta hora duerme en el hotel con la ventana abierta, oyendo el mar, que no se calla nunca. Por la mañana tendrán el sol en la cara y la piel roja. {nombre} sueña que el mar llega hasta la colonia Guerrero, y que a su mamá no le gusta.", estado = "acapulco", hacer = Hacer.DORMIR),
        v("Al caer la tarde, en Acapulco, el sol se mete en el mar con un ruido que solo ella oye. Arturo y {nombre} lo ven desde la playa, sentados en la arena, hasta que no queda nada. Luego se van a cenar, despacio, que aquí nadie tiene prisa.", estado = "acapulco"),
    )

    /** Las semanas en casa con Claudia recién nacida (estado «incapacidad», temporada 3). */
    private val INCAPACIDAD = listOf(
        v("{nombre} se despierta con el llanto de Claudia, de madrugada, y le da el pecho en la oscuridad, sentada en la cama. Arturo duerme. A esta hora antes salía corriendo al tranvía; ahora el tranvía pasa sin ella, y ella lo oye pasar desde la ventana, sin extrañarlo.", estado = "incapacidad"),
        v("A esta hora no está en la oficina: está en casa, con la niña, en la incapacidad que le da el Seguro Social, que le paga parte del sueldo. Lava pañales, los tiende en la azotea y los vuelve a lavar. Su mamá viene todos los días y le dice cómo se hacía antes.", estado = "incapacidad", hacer = Hacer.TRABAJO),
        v("A esta hora no toma el tranvía: sale a caminar con Claudia en brazos hasta la esquina, a la farmacia o al mercado, para que le dé el aire. Las señoras del barrio se paran a verle la cara a la niña y le dicen que es igualita a su papá.", estado = "incapacidad", hacer = Hacer.CAMINO),
        v("A esta hora come en casa de su mamá, con la niña en una canasta junto a la mesa, caldo de pollo, que dicen que es lo que hay que comer ahora, y tortillas calientes. Chela le carga a Claudia mientras ella come, y no se la quiere devolver.", estado = "incapacidad", hacer = Hacer.COMER),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la ciudad el tiempo se mira desde la parada del tranvía, que es donde se espera."),
        v("{nombre} mira el cielo por encima de los edificios del centro, antes de entrar a la oficina. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} La ciudad está a más de dos mil metros, y el sol aquí quema aunque haga frío."),
        v("{cielo} En tiempo de {estacion} la ciudad huele distinto, a tierra mojada o a polvo, y {nombre} lo nota en cuanto abre la ventana."),
        v("Desde la azotea se ven el Popocatépetl y el Iztaccíhuatl cuando el aire está limpio, blancos al fondo del valle. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Su mamá dice que antes había lagos, y canoas, y que el aire era tan limpio que se veían los volcanes todos los días."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y de un azul muy oscuro, como solo se ve en la altura.",
            "Hace un día claro, de los que dejan ver los volcanes nevados al fondo del valle.",
            "No hay una nube, y el sol hace brillar los vidrios de la Torre Latinoamericana.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris como el cemento nuevo.",
            "Hay nubes bajas sobre los cerros que rodean la ciudad.",
            "Está nublado, y el valle se ve aplastado bajo una tapa de algodón.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y las calles del centro se vuelven ríos que hay que cruzar sobre tablas.",
            "Cae una lluvia fina que empaña los vidrios del tranvía.",
            "Llueve, y los vendedores de la calle sacan paraguas y plásticos de quién sabe dónde.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: el cielo se pone negro a las cuatro de la tarde y cae un aguacero con granizo.",
            "Truena sobre la ciudad, y en la oficina se va la luz un momento.",
        ),
        Clima.NIEBLA to listOf(
            "Hay neblina en la mañana, y los edificios altos del centro desaparecen a partir del quinto piso.",
            "La neblina tapa la avenida, y los coches van con los faros prendidos.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento, y las lonas de los puestos del mercado se inflan como velas.",
            "Hay viento, y en la Alameda caen hojas secas de los fresnos.",
        ),
        Clima.POLVO to listOf(
            "Hay tolvanera: el viento trae el polvo del lago seco de Texcoco y lo deja en los dientes de toda la ciudad.",
            "El polvo flota sobre el valle y tiñe el sol de amarillo.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.SECAS to listOf(
            "Es tiempo de secas, de cielos limpios en la mañana y polvo en la tarde.",
            "En secas las mañanas son frías y los mediodías calientes, y hay que salir con suéter y volver con él en el brazo.",
            "Son las secas, cuando florecen las jacarandas y pintan de morado las banquetas.",
        ),
        Estacion.LLUVIAS to listOf(
            "Es tiempo de aguas: las mañanas son limpias y cada tarde cae un aguacero.",
            "En aguas se sale a la oficina con paraguas y se vuelve con los zapatos mojados.",
            "Es tiempo de lluvias, cuando las coladeras del centro no se dan abasto.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora toma dictado. El licenciado Ortega camina por su despacho con las manos en la espalda, dictando una carta, y {nombre} la escribe en taquigrafía, en ganchitos y rayas que solo ella entiende, tan rápido como él habla. Él cree que piensa despacio; ella sabe que habla despacio.", hacer = Hacer.TRABAJO),
        v("A esta hora escribe a máquina la carta que le dictaron, con dos copias en papel carbón, sin una sola equivocación, porque una equivocación es volver a empezar las tres hojas. Las teclas suenan como lluvia, y la campanita del final del renglón, como un timbre.", hacer = Hacer.TRABAJO),
        v("A esta hora contesta el teléfono del despacho, que suena cada pocos minutos: «Despacho del licenciado Ortega, buenos días». Apunta los recados en una libreta, con letra clara, y le dice a cada uno que el licenciado está en junta, que es lo que el licenciado le dijo que dijera.", hacer = Hacer.TRABAJO),
        v("A esta hora archiva. Los expedientes del despacho llenan tres archiveros de metal verde, por orden alfabético, y {nombre} es la única que sabe dónde está cada uno. Cuando el licenciado pide algo, ella lo trae en un minuto. Él cree que es suerte.", hacer = Hacer.TRABAJO),
        v("A esta hora le lleva el café al licenciado, con dos de azúcar, y a los clientes que esperan, que son señores de sombrero y señoras de abrigo. Luego vuelve a su escritorio, junto al de Lupita, que le cuenta en voz baja quién de los clientes está en pleito con su mujer.", hacer = Hacer.TRABAJO),
        v("A esta hora escribe un contrato largo, de diez hojas, a doble espacio, con los márgenes que pide el notario. Le duelen los dedos y la espalda. Al lado, Lupita escribe otro, y entre las dos hacen tanto ruido que el licenciado cierra la puerta de su despacho.", hacer = Hacer.TRABAJO),

        v("A esta hora va en el tranvía, de pie, agarrada del tubo, entre señores que leen el periódico y señoras con canastas. El tranvía suena su campana en cada esquina y se mece como un barco. {nombre} mira por la ventana la ciudad que pasa y se sabe el camino de memoria.", hacer = Hacer.CAMINO),
        v("A esta hora camina por la avenida Madero, entre joyerías, tiendas de ropa y cafés, con la gente que va a las oficinas. Los hombres llevan traje y sombrero, y algunos le dicen algo al pasar. Ella no voltea, que es lo que se hace.", hacer = Hacer.CAMINO),
        v("A esta hora espera el tranvía en la parada, con la bolsa apretada contra el pecho por los carteristas. Pasan coches, camiones, un carretón de mulas con hielo y un vendedor de globos. El tranvía viene lleno. Se sube igual, por la puerta de atrás.", hacer = Hacer.CAMINO),

        v("A esta hora come en casa de su mamá, que tiene la mesa puesta desde la una: sopa de fideo, arroz rojo, un guisado, frijoles y tortillas, y de postre, gelatina. Se come despacio, que hay dos horas, y se platica de todo. Luego, el café, y de regreso a la oficina.", hacer = Hacer.COMER),
        v("A esta hora come con su familia en la mesa de la cocina de su mamá, con el radio prendido en la estación de las canciones. Beto llega tarde, como siempre, y se come lo de dos. Chela cuenta lo de la escuela. Doña Carmen sirve y no se sienta hasta el final.", hacer = Hacer.COMER),
        v("A esta hora, cuando no le alcanza el tiempo para ir a casa, come una torta de milanesa en un puesto cerca de la oficina, con Lupita, y un refresco de toronja. Se sientan en una banca de la Alameda, entre palomas, a ver pasar a la gente bonita.", hacer = Hacer.COMER),

        v("A esta hora escucha la radionovela con su mamá, sentadas junto al radio, en silencio, sin moverse, mientras la protagonista sufre. Cuando acaba el capítulo, con la música de siempre, las dos suspiran. Mañana sabrán si el hijo perdido era el hijo perdido.", hacer = Hacer.LIBRE),
        v("A esta hora va al cine con Arturo, a ver una película de Cantinflas en un cine del centro, enorme, con cortinas de terciopelo y un candil en el techo. Se ríen hasta llorar. A la salida compran elotes en la esquina y caminan hasta la parada.", hacer = Hacer.LIBRE),
        v("A esta hora lee una fotonovela tumbada en la cama, la de esta semana, donde una muchacha pobre se enamora del hijo del patrón. {nombre} sabe cómo va a acabar, y la lee igual, hasta que se le cierran los ojos, con la historieta abierta sobre la cara.", hacer = Hacer.LIBRE),

        v("A esta hora se arregla para salir: se cepilla el pelo cien veces, como dice la revista, se pinta las uñas de rojo y se pone la falda azul, que es la que más le gusta. Su mamá la mira desde la puerta y le dice que está guapa, y luego que no llegue tarde.", hacer = Hacer.CUIDAR),
        v("A esta hora ayuda en la casa antes de salir: tiende las camas, barre, deja lavados los trastes del desayuno. Las mujeres trabajan dos veces, dice su mamá, una en la calle y otra en la casa. {nombre} le dice que ella va a cambiar eso. Su mamá se ríe.", hacer = Hacer.CUIDAR),
        v("A esta hora se plancha el pelo con unos tubos que se dejó toda la noche, y se pone un pañuelo en la cabeza para que no se le deshaga el peinado en el tranvía. Se mira en el espejo de lado y de frente. Está bien. Está muy bien, si nadie le pregunta.", hacer = Hacer.CUIDAR),

        v("A esta hora duerme, y en la calle pasa el camotero con su silbato de vapor, largo y triste, que se oye a varias cuadras. {nombre} lo oye entre sueños y piensa en camotes, y se vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, cansada, con los dedos todavía moviéndose sobre una máquina de escribir que no está. Sueña con cartas que no se acaban, y con el licenciado dictando en una lengua que no existe.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, y la ciudad entera duerme con ella, menos los taxistas, los panaderos y los que vuelven de bailar del salón de la colonia. El último tranvía pasa a las doce, y después la calle se queda callada.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La colonia Guerrero es un barrio viejo de vecindades, talleres, cantinas y vecinos que se conocen desde siempre. En sus calles hay un mercado, una iglesia, dos cines y un salón de baile. Su mamá dice que se ha vuelto peligrosa; lo dice desde hace treinta años."),
        v("El centro de la ciudad es de edificios viejos de piedra, con balcones de hierro, y de edificios nuevos de vidrio, todos juntos. En las calles hay vendedores de todo: billetes de lotería, chicles, juguetes, tamales. Un organillero toca en la esquina, y le dan un veinte."),
        v("La ciudad crece todos los días: hacia el sur, hacia el norte, hacia donde haya tierra. Donde antes había milpas ahora hay colonias nuevas, con calles sin pavimentar y casas a medio hacer. {nombre} ve los anuncios en el periódico: terrenos a plazos."),
        v("En la Alameda, frente al Palacio de Bellas Artes, la gente se sienta en las bancas a dar de comer a las palomas, a leer el periódico o a besarse. Los domingos hay fotógrafos ambulantes con caballitos de cartón para los niños. {nombre} cruza por ahí cada día."),
        v("Por las calles pasan tranvías, camiones de pasajeros pintados de colores, coches americanos con aletas y, todavía, algún carretón tirado por mulas. Los agentes de tránsito dirigen el tráfico con el silbato, subidos en una tarima redonda, y nadie les hace caso."),
        v("En las azoteas de la ciudad vive otra ciudad: cuartos de servicio, lavaderos, gallinas, tendederos y antenas de televisión, cada vez más. Desde la azotea de la oficina {nombre} ve un mar de antenas, y le parece que la ciudad entera está esperando una señal."),
    )

    private val GENTE = listOf(
        v("Doña Carmen, su mamá, es viuda y cose ajeno en una máquina de pedal, junto a la ventana. Sabe hacer de todo con nada y tiene una opinión sobre todo, que da sin que se la pidan. A {nombre} le dice que ya no está para esperar, cada que puede."),
        v("Beto, su hermano, quiere ser futbolista, y se pasa las tardes pateando una pelota de trapo contra la pared de la vecindad hasta que alguien le grita. Dice que lo va a ver un día un señor del América. {nombre} le dice que sí, que seguro, y le da para el camión."),
        v("Chela, su hermana menor, quiere ser como {nombre}: secretaria, con medias y un escritorio en el centro. Le pide prestado el lápiz labial a escondidas y se pinta frente al espejo, y luego se lo quita con la manga antes de que la vea su mamá."),
        v("Lupita, la secretaria del escritorio de al lado, es su mejor amiga en la oficina: sabe la vida de todos los abogados, la letra de todas las canciones y el precio de todas las medias del centro. Escribe a máquina más rápido que nadie, y habla más rápido todavía."),
        v("El licenciado Ortega, su jefe, es un señor de bigote y traje gris que trata a {nombre} con una cortesía que nunca se equivoca y nunca se acerca. Le dice «señorita» aunque se case. Cuando gana un pleito, les compra pasteles a las secretarias."),
        v("El voceador de la esquina de la oficina grita los titulares de los periódicos de la tarde, más gordos de lo que son, y a {nombre} le guarda La Prensa cada día sin que se lo pida. Ella le paga y le pregunta por sus hijos, que son seis."),
        v("Arturo, su novio, es dibujante en un despacho de ingenieros y tiene las manos siempre manchadas de tinta china. La visita los sábados en la vecindad, con flores o con una revista, y se sienta en la sala con doña Carmen enfrente, vigilando, sin decir nada.", hasta = "boda"),
        v("Arturo, su marido, llega del despacho con los planos enrollados bajo el brazo y le enseña las casas que dibuja, que son para otros. Un día, dice, va a dibujar la suya. {nombre} le dice que con que tenga una cocina grande, ella está conforme.", desde = "boda"),
        v("Beto ya trabaja de ayudante en un taller mecánico, y llega a comer con las manos negras de grasa y el overol lleno de manchas. Sigue jugando futbol los domingos en un llano, con un equipo de la colonia. Dice que el taller es mientras.", desde = "beto_taller"),
        v("{nombre} espera un hijo, y en la oficina todavía no se le nota, pero ya lo saben todos, porque Lupita no sabe guardar un secreto. El licenciado le cede el sillón de los clientes cuando se marea. Doña Carmen le teje chambritas de color amarillo, por si acaso.", desde = "espera_rosa", hasta = "nace_claudia"),
        v("Claudia, su hija, se queda con doña Carmen mientras {nombre} está en la oficina, y cuando ella llega a comer la niña ya la está esperando, o eso dice su mamá. {nombre} la carga antes de quitarse los zapatos, y come con ella en las piernas.", desde = "nace_claudia"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de la ciudad: sin haberlo aprendido nunca."),
        v("Hay cosas que solo se miran cuando faltan. {dato_objeto}"),
        v("{dato_objeto} En la oficina se dice que una secretaria se conoce por su bolsa, y que nadie sabe lo que lleva dentro."),
        v("{dato_objeto} Son cosas que se aprenden mirando a las demás, como casi todo en la ciudad."),
        v("En la vida de una muchacha del centro hay pocas cosas, y todas caben en una bolsa o en un cajón. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("maquina_escribir", "la máquina de escribir", "una máquina de escribir", "La máquina de escribir",
            "La máquina de escribir de la oficina es negra, pesa como un niño y tiene las letras gastadas de tanto uso; cada tecla golpea una cinta de tinta contra el papel, y una buena secretaria la oye y sabe, sin mirar, si se equivocó."),
        ObjetoCuento("radio", "el radio", "un radio", "El radio",
            "El radio de la casa es un mueble de madera con una tela dorada al frente y una perilla que brilla verde al prenderse; tarda un rato en calentarse, y entonces suena la ciudad entera: canciones, novelas, noticias y anuncios de jabón."),
        ObjetoCuento("fotonovela", "la fotonovela", "una fotonovela", "La fotonovela",
            "La fotonovela es una revista de papel barato con una historia contada en fotografías y globos de diálogo, como una película quieta; sale cada semana, se lee en el camión y se presta hasta que se deshace."),
        ObjetoCuento("boleto_tranvia", "el boleto del tranvía", "un boleto del tranvía", "El boleto",
            "El boleto del tranvía es un papelito de color que da el cobrador al subir, a cambio de unos centavos; se guarda en la mano todo el viaje, por si sube el inspector, y al bajar se tira, y las calles del centro están llenas."),
        ObjetoCuento("bolsa", "la bolsa", "una bolsa", "La bolsa",
            "La bolsa de todos los días es de piel negra, de broche dorado, y la compró a plazos en una tienda de la calle de Madero; dentro lleva el monedero, un pañuelo, el lápiz labial, un peine, la libreta de taquigrafía y un rosario."),
        ObjetoCuento("lapiz_labial", "el lápiz labial", "un lápiz labial", "El lápiz labial",
            "El lápiz labial viene en un tubito de metal dorado que se desenrosca; el suyo es rojo, como el de las artistas de cine, y le dura dos meses si lo usa con cuidado y uno si se lo presta a Chela."),
        ObjetoCuento("taza_cafe", "la taza de café", "una taza de café", "El café",
            "El café de la oficina se hace en una cafetera de peltre en la parrilla eléctrica del rincón, y sabe a lo que sabe; el bueno es el de olla, el de su mamá, con canela y piloncillo, que se toma en jarrito de barro."),
        ObjetoCuento("pan_dulce", "el pan dulce", "un pan dulce", "El pan dulce",
            "El pan dulce se compra en la panadería de la esquina, que tiene charolas y pinzas para que cada uno escoja: conchas, cuernos, orejas, polvorones, chilindrinas; cada uno tiene su nombre y su forma, y en cada casa hay uno que es el favorito."),
        ObjetoCuento("refresco", "el refresco", "un refresco", "El refresco",
            "El refresco viene en botella de vidrio grueso, que se devuelve en la tienda para que la vuelvan a llenar; los hay de cola, de naranja, de toronja y de tamarindo, y se toman tan fríos que duelen los dientes."),
        ObjetoCuento("rosario", "el rosario", "un rosario", "El rosario",
            "Su rosario es de cuentas de vidrio azul, con una medalla de la Virgen; se lo regaló su papá en su primera comunión, y lo lleva en la bolsa aunque reza poco, porque es lo único que le queda de él."),
        ObjetoCuento("television", "la televisión", "una televisión", "La televisión",
            "La televisión es una caja de madera con una pantalla de vidrio curvo, gris y azul, que tarda en calentarse; hay pocos canales y pocas horas de programas, y la gente se junta delante como antes se juntaba delante de la lumbre."),
        ObjetoCuento("calendario", "el calendario", "un calendario", "El calendario",
            "El calendario de la pared lo regala cada año la tortillería, con una pintura de una princesa azteca dormida y un guerrero que la cuida; abajo vienen los meses, los santos de cada día y el nombre de la tortillería, que es lo que importa."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en la ciudad como se piensa en el tiempo: no se la elige, se vive en ella. Es enorme, ruidosa y suya. A sus {edad} años, no se iría a vivir a otro sitio, aunque su mamá diga que en el pueblo de sus abuelos la gente era más feliz."),
        v("Piensa a veces que su mamá no fue a la escuela, y que ella escribe cartas para abogados que hablan en nombre de gente importante. Le parece que entre su mamá y ella hay cien años, y no treinta. Y piensa que entre ella y Chela habrá otros cien."),
        v("{mundo} Ella piensa en cosas más cercanas: en el sueldo, que no alcanza, en las medias, que se corren, en el pelo, que no se queda. Las cosas grandes son de los licenciados, que tienen tiempo de leer el periódico entero."),
        v("{mundo} A sus {edad} años, {nombre} tiene un sueldo, una máquina de escribir y una vida que su mamá no tuvo. Lo piensa en el tranvía, de vez en cuando, y le da orgullo y algo de miedo, como cuando se asoma a la ventana de un piso alto."),
        v("Su papá trabajaba en los ferrocarriles y murió cuando ella tenía dieciocho, de un pulmón. {nombre} todavía oye su silbido cuando llegaba a la vecindad. Piensa que le habría gustado verla en la oficina, con su escritorio y su teléfono. Seguro le habría dicho que no trabajara tanto."),
        v("{mundo} A veces, en el tranvía, {nombre} piensa en lo que va a ser de ella dentro de diez años: una casa, unos hijos, a lo mejor la oficina, a lo mejor no. No lo sabe. Lo bueno de la ciudad, piensa, es que todo puede pasar, y lo malo es lo mismo."),
    )

    private val MUNDOS = mapOf(
        "sesquicentenario" to listOf(
            "En la oficina se habla de que se cumplen ciento cincuenta años de la Independencia, y de las fiestas que se preparan.",
            "Se dice que el presidente va a nacionalizar la luz, como se hizo con el petróleo.",
            "En el tranvía se habla del precio de la tortilla y de los cines nuevos.",
        ),
        "boda" to listOf(
            "Se habla de Cuba y de los barbudos, y en la oficina los licenciados discuten si eso es bueno o malo.",
            "Se dice que la ciudad tiene ya más coches que nunca, y que pronto no se va a poder pasar.",
        ),
        "claudia" to listOf(
            "Se habla del Mundial de Chile y de la selección, que esta vez sí, dicen.",
            "Se dice que los rusos y los gringos se pelean por Cuba, y en la oficina se oye la radio todo el día.",
        ),
        "olimpiada" to listOf(
            "Se habla de que México va a hacer unas Olimpiadas, y nadie se lo cree del todo.",
            "Se dice que en Estados Unidos los negros marchan por sus derechos, y en la oficina se oye en la radio.",
        ),
        "tlatelolco" to listOf(
            "Se habla de las elecciones y del candidato del partido, que ya se sabe que va a ganar.",
            "Se dice que en Tlatelolco están haciendo una ciudad entera de edificios, con escuelas y tiendas.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando sale de la oficina ya está oscureciendo, y en el centro se prenden los anuncios luminosos de las azoteas, de colores, uno tras otro. {nombre} los mira desde la parada del tranvía, como cada tarde, y piensa que la ciudad de noche se parece a una feria."),
        v("Al caer la tarde la ciudad se llena de gente que vuelve a su casa: tranvías llenos, camiones llenos, banquetas llenas. {nombre} se sube como puede, con la bolsa contra el pecho, y llega a su parada despeinada y con los zapatos pisados."),
        v("Se cena poco: un pan con café con leche, o unos frijoles con queso, o lo que haya quedado de la comida. Se cena oyendo el radio, sin hablar mucho, y luego cada quien a lo suyo. {nombre} se quita las medias con cuidado y las cuelga en la silla."),
        v("De noche, en la calle, se oye el pregón del tamalero, con su carrito y su olla humeante, y el silbato del camotero, y a lo lejos un mariachi que alguien contrató para una serenata. {nombre} se asoma a la ventana a ver de quién es, y nunca lo sabe."),
        v("Anochece sobre la ciudad, y en el cielo, en vez de estrellas, se ve el resplandor naranja de las luces. {nombre} se sienta un momento en la cama con los pies cansados y piensa en lo que pasó hoy, que fue poco y mucho, como todos los días."),
        v("La última luz del día se queda un momento en lo alto de la Torre Latinoamericana, cuando la calle ya está a oscuras. {nombre} la ve desde el tranvía, por la ventana de atrás, y se queda mirándola hasta que la tapan los edificios."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "reyes" to unDia(
            Parrafo.MANANA to "Hoy es Día de Reyes, y en la madrugada los niños de la vecindad encontraron sus juguetes junto a los zapatos que dejaron anoche en la ventana: un trompo, una muñeca de cartón, una pelota. Los gritos se oyen desde temprano en todo el patio.",
            Parrafo.GENTE to "En la tarde se parte la rosca en casa de doña Carmen, con chocolate caliente, y cada uno corta su rebanada con cuidado, para no partir al muñeco. A Beto le sale, y le toca hacer los tamales el día de la Candelaria. Dice que es trampa. Todos se ríen de él.",
            Parrafo.ANOCHECER to "Esta noche el patio de la vecindad de su mamá está lleno de niños estrenando juguetes, de trompos que zumban y de muñecas que lloran cuando se las acuesta. {nombre} se queda viéndolos un rato desde la puerta, con su taza de chocolate, y se acuerda de los suyos.",
        ),
        "candelaria" to unDia(
            Parrafo.MANANA to "Hoy es día de la Candelaria, y a los que les salió el muñeco en la rosca les toca pagar los tamales. En la iglesia de la colonia hay misa para bendecir a los Niños Dios, que las señoras llevan en brazos vestidos con ropa nueva, de santo, de doctor o de rey.",
            Parrafo.PUEBLO to "En el mercado los puestos de ropa para Niño Dios están llenos desde hace una semana: trajecitos de seda, coronas, sandalias diminutas. Las tamaleras no se dan abasto. Por la calle pasan señoras con su Niño Dios envuelto en un chal, como si fuera de verdad.",
            Parrafo.ANOCHECER to "Esta noche se comen tamales verdes, de rajas y de dulce, con atole de chocolate, en casa de doña Carmen, todos alrededor de la mesa. El que pagó dice que le salieron carísimos. Los demás dicen que estaban buenísimos, que es lo que se dice.",
        ),
        "juarez" to unDia(
            Parrafo.MANANA to "Hoy es el natalicio de don Benito Juárez y no hay oficina. {nombre} se despierta a la hora de siempre, por costumbre, y se acuerda y se vuelve a dormir. Es el mejor momento del día: el de acordarse de que no hay que ir.",
            Parrafo.PUEBLO to "En la Alameda, frente al Hemiciclo a Juárez, de mármol blanco, hay ceremonia: banda de guerra, discursos, coronas de flores, niños de escuela formados. El presidente no va, va alguien en su nombre. La gente que pasea se para a mirar un rato, y luego sigue.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Juárez, que era un indio pastor de ovejas y llegó a presidente, y que aprendió a leer ya grande. Lo dicen los libros de la escuela de Chela. Le parece que si él pudo, ella puede aprender inglés, que es lo que pide el licenciado.",
        ),
        "viernes_santo" to unDia(
            Parrafo.MANANA to "Hoy es Viernes Santo, y cierran las oficinas. La ciudad está callada como no se le oye nunca: no hay tranvías llenos, no hay claxon, muchas tiendas están cerradas. Media ciudad se fue a Acapulco o a su pueblo, y la otra media está en la iglesia.",
            Parrafo.PUEBLO to "En Iztapalapa hacen la Pasión en las calles, con un muchacho del barrio cargando una cruz de verdad y miles de personas mirando, y en el radio la transmiten como un partido. En las iglesias del centro los santos están tapados con telas moradas.",
            Parrafo.GENTE to "{nombre} va con su mamá y con Chela a las siete casas, que es visitar siete iglesias en una tarde, rezando en cada una. Doña Carmen camina despacio, con su rebozo negro. Hoy no se come carne: se comen romeritos, tortitas de camarón y capirotada.",
        ),
        "madres" to unDia(
            Parrafo.MANANA to "Hoy es Día de las Madres, y antes del amanecer un mariachi le canta las mañanitas a doña Carmen en el patio de la vecindad, pagado entre los hijos. Las vecinas se asoman en bata. Doña Carmen sale con el rebozo en los hombros y llora, como todos los años.",
            Parrafo.TRABAJO to "A esta hora, en la oficina, el licenciado Ortega les da a las secretarias que son mamás la tarde libre, y a las que no, un clavel rojo. Lupita dice que ella es mamá de su gato. El licenciado le da la tarde, porque hoy no quiere pleitos.",
            Parrafo.GENTE to "En la comida, en casa de doña Carmen, los hijos le dan su regalo, pagado entre los tres y envuelto con un moño enorme. Su mamá dice que no hacía falta, que para qué, que ella no necesita nada. Luego lo abre despacio, sin romper el papel, que guarda.",
        ),
        "aguaceros" to unDia(
            Parrafo.MANANA to "Hoy, a las cuatro de la tarde, empezaron las aguas. El cielo se puso negro sobre el valle, sonó un trueno que hizo temblar los vidrios de la oficina y cayó un aguacero como si alguien hubiera volteado una cubeta sobre la ciudad.",
            Parrafo.PUEBLO to "En media hora las calles del centro eran ríos: el agua llegaba a las rodillas en algunas esquinas y los coches se quedaban parados con el motor ahogado. Los tranvías avanzaban despacio, abriendo olas. Unos muchachos cobraban por cargar a la gente en la espalda para cruzar.",
            Parrafo.ANOCHECER to "Esta noche {nombre} llega a su casa empapada, con los zapatos en la mano y las medias perdidas, y se ríe de sí misma en el espejo. Afuera sigue lloviendo, ya sin ganas. Desde mañana, piensa, hay que salir con paraguas hasta octubre.",
        ),
        "patrias" to TextoEspecial(
            listOf(
                dia(
                    Parrafo.MANANA to "Hoy es quince de septiembre, y la ciudad está adornada de verde, blanco y rojo: banderas en los balcones, foquitos en los edificios del Zócalo, papel picado en las vecindades. En la oficina se trabaja medio día, y se habla de la noche.",
                    Parrafo.PUEBLO to "A las once de la noche el presidente sale al balcón de Palacio Nacional, toca la campana de Dolores y grita los vivas, y el Zócalo, lleno hasta las calles de alrededor, contesta con un rugido. Hay cohetes, matracas, sombreros de charro y huevos llenos de confeti.",
                    Parrafo.GENTE to "{nombre} va al Zócalo con Beto, Chela y unos vecinos, y llegan tan apretados que no ven el balcón, pero oyen el grito y gritan con todos. Comen elotes y buñuelos. Beto les rompe a las muchachas cascarones de confeti en la cabeza, y una le rompe uno a él.",
                ),
                dia(
                    Parrafo.MANANA to "Hoy es dieciséis de septiembre, el día de la Independencia, y no se trabaja. Por la avenida pasa el desfile militar: soldados, cadetes, tanques, caballos, aviones que pasan bajito sobre los edificios. La gente lo ve desde las banquetas y desde los balcones.",
                    Parrafo.GENTE to "{nombre} ve el desfile con su mamá desde la banqueta de la avenida, sentadas en dos sillas que llevaron de la casa. Doña Carmen aplaude a los cadetes, que son muchachos de la edad de Beto, y dice que qué guapos, y que pobres de sus madres.",
                    Parrafo.ANOCHECER to "Esta noche se cena pozole en casa de doña Carmen, rojo, con rábano, lechuga y orégano, y tostadas. Llegan los tíos, los primos y los vecinos, y se canta con el radio. La fiesta patria, dice su mamá, es la única en que todos los mexicanos están de acuerdo.",
                ),
            ),
        ),
        "muertos" to TextoEspecial(
            listOf(
                dia(
                    Parrafo.MANANA to "Hoy es día de Todos Santos, y en casa de doña Carmen ya está puesto el altar: una mesa con mantel blanco, flores de cempasúchil, velas, pan de muerto, calaveritas de azúcar con nombres en la frente y, en medio, la foto del papá de {nombre}, con su uniforme de ferrocarrilero.",
                    Parrafo.PUEBLO to "En los mercados hay montañas de flores amarillas y moradas, de calaveras de azúcar y de chocolate, de papel picado y de pan. Los niños piden calaverita por las calles con una caja de zapatos con una vela dentro. En los periódicos salen versos de calaveras, burlándose de los políticos.",
                    Parrafo.PENSAMIENTO to "{nombre} pone en el altar los cigarros que fumaba su papá y un vasito de tequila, y piensa que esta noche viene a verlos. No sabe si lo cree. Pero se queda un rato frente a la foto, contándole en voz baja lo de la oficina, por si acaso.",
                ),
                dia(
                    Parrafo.MANANA to "Hoy es día de los fieles difuntos, y {nombre} va con su mamá al panteón, temprano, en el camión, con flores, una cubeta, un trapo y una escoba. El panteón está lleno de familias limpiando tumbas, comiendo y platicando, como si fuera un día de campo.",
                    Parrafo.GENTE to "Doña Carmen limpia la tumba de su marido con el trapo, como si limpiara la mesa, y le cambia las flores, y le platica: que Beto ya tiene trabajo o no, que Chela está en la secundaria, que {nombre} sigue en la oficina. {nombre} la deja hablar, y se va un rato a caminar entre las tumbas.",
                    Parrafo.ANOCHECER to "Esta noche, en casa, se come el pan de muerto del altar, que ya está un poco duro, con chocolate. Su mamá dice que ya no sabe a nada, porque los muertos se comieron el sabor. {nombre} lo encuentra igual de bueno, y se come dos.",
                ),
            ),
        ),
        "revolucion" to unDia(
            Parrafo.MANANA to "Hoy es el aniversario de la Revolución, y por la avenida pasa el desfile deportivo: miles de muchachos de las escuelas en pants blancos, haciendo tablas gimnásticas, pirámides humanas, charros a caballo y muchachas con bastones. No se trabaja.",
            Parrafo.PUEBLO to "Las banquetas de la avenida están llenas de gente que mira el desfile, con niños en los hombros y banderas de papel. Los que desfilan van serios, muy derechos, y saludan al pasar frente al balcón donde está el presidente. Al final pasan los veteranos de la Revolución, viejitos, en coches abiertos.",
            Parrafo.PENSAMIENTO to "{nombre} mira a los veteranos, que son viejos de sombrero y bigote blanco, y piensa que su abuelo pudo haber sido uno de ellos, si no se hubiera muerto en la bola. Su mamá nunca habla de eso. Ella tampoco pregunta. Aplaude cuando pasan.",
        ),
        "guadalupe" to unDia(
            Parrafo.MANANA to "Hoy es día de la Virgen de Guadalupe, y anoche, a las doce, millones le cantaron las mañanitas en la Basílica, con mariachis, cantantes famosos y la transmisión en el radio. {nombre} las oyó en la cama, con su mamá, cantando bajito con el aparato.",
            Parrafo.PUEBLO to "Por la calzada de Guadalupe llegan peregrinos de todo el país desde hace días: a pie, en bicicleta, en camiones adornados con flores, cargando imágenes de la Virgen en la espalda. Los niños van vestidos de inditos, con bigote pintado con corcho quemado. Algunos llegan de rodillas.",
            Parrafo.GENTE to "{nombre} va a la Villa en la tarde con doña Carmen, en el tranvía lleno, y se forman para pasar frente a la imagen. Su mamá pide por todos, en voz baja, con los ojos cerrados. {nombre} pide por su mamá. A la salida compran gorditas de la Villa en una canasta.",
        ),
        "posadas" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las posadas, las nueve noches antes de Navidad, y en la oficina ya se habla de la de los licenciados, que es la última. En la vecindad de su mamá las familias se turnan las noches: hoy le toca a la del zapatero, que es la más pobre y la más alegre.",
                Parrafo.PUEBLO to "Al anochecer los vecinos salen con velas y cantan la letanía detrás de los peregrinos, que son dos figuritas de barro en una charola: unos afuera piden posada, cantando, y los de adentro contestan, cantando, que no. A la tercera les abren, y entran todos.",
                Parrafo.ANOCHECER to "Esta noche se rompe la piñata en el patio, una olla de barro forrada de papel de colores en forma de estrella, con siete picos, rellena de cañas, tejocotes, cacahuates y colación. Los niños le pegan con los ojos vendados y los grandes cantan. Luego, ponche caliente.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "Esta noche hay posada en otra casa de la vecindad, y se canta lo mismo que anoche, con las mismas voces, y se rompe otra piñata, y se toma el mismo ponche. Nadie se cansa.",
                "Esta noche es la posada de la oficina, en el despacho del licenciado, con sidra, canapés y un tocadiscos. Los abogados bailan mal y las secretarias se ríen. {nombre} baila una con Lupita, que lleva.",
                "Esta noche es Nochebuena, la última posada, y después se cena en casa de doña Carmen bacalao, romeritos y buñuelos con miel. A medianoche se arrulla al Niño Dios y se acuesta en el nacimiento. Afuera truenan los cohetes.",
            )),
        ),
        "chela_secundaria" to unDia(
            Parrafo.MANANA to "Hoy Chela empezó la secundaria, con uniforme nuevo, falda azul marino, blusa blanca y calcetas, y un portafolios de cuero que le regaló {nombre}. Se fue caminando con dos amigas, muy derecha, y no volteó a decir adiós, para que no se viera que tenía miedo.",
            Parrafo.GENTE to "Doña Carmen la vio irse desde la puerta, con la mano en el pecho. Ella no terminó ni la primaria. Le dice a {nombre} que entre las dos van a sacar adelante a esa niña, y que va a ser maestra, o doctora, o lo que quiera. {nombre} le dice que sí.",
            Parrafo.ANOCHECER to "Esta noche Chela no para de hablar: de los maestros, de las compañeras, de un muchacho que la miró, de la clase de inglés, que no entendió nada. {nombre} le ayuda con la tarea en la mesa de la cocina. Al rato las dos se están riendo de lo mismo, sin saber de qué.",
        ),
        "maquina" to unDia(
            Parrafo.MANANA to "Hoy, al llegar a la oficina, {nombre} encontró sobre su escritorio una máquina de escribir nueva, eléctrica, gris, que zumba al prenderse. El licenciado Ortega la compró para el despacho y se la dio a ella, porque es la más rápida. Lupita dice que no es justo, y que la felicita.",
            Parrafo.TRABAJO to "A esta hora aprende a usar la máquina eléctrica, que escribe sola en cuanto se toca una tecla y no perdona: al principio le salen cartas llenas de letras repetidas. Pero las teclas no pesan, y al final del día escribe más rápido que nunca, y le duelen menos los dedos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esta máquina es el futuro, como dice el anuncio, y que ella está sentada delante del futuro, en la calle de Madero, ganando un sueldo. Piensa en su mamá cosiendo en su máquina de pedal, y en que las dos se ganan la vida con una máquina.",
        ),
        "arturo_pide" to unDia(
            Parrafo.MANANA to "Hoy, en la tarde, en la nevería de la esquina de la Alameda, Arturo le pidió que se casara con él. Lo hizo mal, como se hacen esas cosas: se le cayó el anillo en la nieve de limón. Pero lo dijo. Y {nombre} le dijo que sí antes de que lo terminara de decir.",
            Parrafo.GENTE to "Después tuvieron que ir a la vecindad a pedirle permiso a doña Carmen, que ya lo sabía por la cara que traían. Arturo le habló de su sueldo, de sus planes y de un departamento que vio en Santa María la Ribera. Doña Carmen escuchó todo, seria, y al final le dio un abrazo.",
            Parrafo.PENSAMIENTO to "{nombre} mira el anillo, que es delgadito y de oro de pocos quilates, y que le queda un poco grande. Piensa que se va a casar, y que dejará la vecindad, y a su mamá, y que la oficina, quién sabe. Todo eso le da miedo. Pero lo vuelve a mirar, y sonríe.",
        ),
        "beto_prueba" to unDia(
            Parrafo.MANANA to "Hoy Beto fue a una prueba con un equipo de futbol de verdad, en un campo de la colonia Roma. Se levantó a las cinco, se puso los tachones que le compró {nombre} y salió sin desayunar, porque dice que con el estómago vacío se corre más. Su mamá le metió una torta en la bolsa.",
            Parrafo.GENTE to "Beto volvió en la tarde con la cara colorada, una raspada en la rodilla y la noticia de que el entrenador le dijo que volviera el año que viene. No sabe si eso es que sí o que no. Doña Carmen dice que es que no. Beto dice que es que sí. {nombre} no dice nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su hermano corriendo detrás de una pelota en un campo de tierra, con todos sus sueños puestos ahí. Le da ternura y le da miedo. Los sueños de los pobres, piensa, son más grandes porque no cuestan nada. Lo malo es cuando se cobran.",
        ),
        "luz" to unDia(
            Parrafo.MANANA to "Hoy el gobierno compró las compañías de luz extranjeras, y desde hoy la electricidad es de la nación. En el radio lo dicen desde temprano, con música patriótica entre noticia y noticia, y en la oficina los licenciados no hablan de otra cosa.",
            Parrafo.PUEBLO to "En el Zócalo hay una manifestación de apoyo al presidente, con camiones de obreros y de empleados que llegan con pancartas: «La luz es nuestra». Los edificios de alrededor tienen focos de colores. Algunos dicen que ahora la luz va a ser más barata, y otros que se va a ir más seguido.",
            Parrafo.PENSAMIENTO to "{nombre} prende la luz de su escritorio y piensa que ese foco ya es un poco suyo, como el petróleo de los coches, según dice el licenciado. No sabe bien qué cambia. Pero le gusta la idea de que algo tan grande sea de todos, aunque el recibo le llegue igual.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "cdmx1960", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + ACAPULCO.take(1) + INCAPACIDAD.take(1),
            Parrafo.TIEMPO to TIEMPO + listOf(
                v("En Acapulco el cielo es otro: alto, caliente, sin polvo, y el mar refleja el sol hasta lastimar los ojos. Rosa se tapa con el sombrero de palma que le compró Arturo. Aquí no hay secas ni aguas: hay calor.", estado = "acapulco"),
                v("{nombre} mira el cielo por la ventana del departamento, con Claudia en brazos, a la hora en que antes estaba en el tranvía. {cielo} {de_estacion}", estado = "incapacidad"),
            ),
            Parrafo.TRABAJO to TRABAJO + ACAPULCO.subList(1, 7) + INCAPACIDAD.subList(1, 4),
            Parrafo.PUEBLO to PUEBLO + listOf(
                v("Acapulco es una bahía redonda rodeada de cerros verdes, con hoteles nuevos en la costera, barcos de pesca, palmeras y turistas gringos con la piel roja. Por la playa pasan vendedores de todo: cocos, collares, sombreros, fotos. Rosa nunca había visto tanta gente sin prisa.", estado = "acapulco"),
                v("Santa María la Ribera es una colonia de casas viejas con balcones y de edificios nuevos de departamentos, con una alameda en medio y un kiosco morisco de hierro de colores, donde los domingos toca una banda. {nombre} pasea a Claudia por ahí cada tarde, en una carriola prestada.", estado = "incapacidad"),
            ),
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + ACAPULCO[7],
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS,
        especiales = ESPECIALES,
    )
}
