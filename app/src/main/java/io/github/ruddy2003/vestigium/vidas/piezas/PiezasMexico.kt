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
 * El cuento de «Alguien de México», la persona promedio de México, que aquí es un hombre de una ciudad grande del
 * centro del país. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - **No usa `{nombre}`.** Es «alguien»: el cuento dice «él» o nada, y no le pone nombre. Su género lo decidió esta
 *   tanda (T2b): hombre; el de «Alguien del mundo», mujer.
 * - Trabaja en la oficina de una distribuidora de refacciones para coches: pedidos, facturas, inventario y mensajes
 *   con los talleres. Va en camión y en metro, hora y media de ida y otra de vuelta. Su esposa, Daniela, atiende una
 *   farmacia de cadena.
 * - No repite lo que ya dice «Su vida»: los treinta años, el internet y las redes, las horas de trabajo, los pasos, el
 *   refresco y el azúcar, los setenta y cinco años, los menos de dos hijos y los pocos libros.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG2.kt`):
 * - Mateo va a la guardería hasta `mateo_kinder` (temporada 1), al kínder desde entonces y a la primaria desde
 *   `primaria` (4). Valeria nace en la 3 (`nace_valeria`); el embarazo va de `esperan` (2) a `nace_valeria`.
 * - Viven en un departamento rentado hasta `mudanza` (3), en uno más grande y más lejos hasta `casa` (5), y desde
 *   entonces en una casa de un fraccionamiento.
 * - El estado «playa» (temporada 2): una semana de vacaciones.
 * - Las fiestas que vuelven cada año no nombran a Valeria ni dicen dónde viven.
 */
internal object PiezasMexico {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("Lo despierta la alarma del teléfono a las seis, y la apaga, y lo despierta otra vez a las seis y diez, que es la de verdad. Antes de abrir bien los ojos ya está viendo los mensajes: el grupo de la familia, el del trabajo, el del futbol de los jueves. Nada urgente. Todo urgente."),
        v("Se levanta a oscuras para no despertar a nadie, se baña rápido porque el boiler tarda y el agua caliente no alcanza para dos, y se toma un café soluble de pie en la cocina, con un pan dulce de ayer. Mira por la ventana: las luces de los edificios de enfrente ya están prendidas."),
        v("Desde la calle sube el ruido de la mañana: el camión de la basura con su campana, el señor de los tamales con su grabación, los perros del vecino, un claxon que no se calla. Él se pone los audífonos para no oírlo, y se pone a oír otra cosa."),
        v("Se plancha la camisa sobre la cama, rápido, solo el cuello y los puños, que es lo que se ve. Se pone el gafete de la empresa colgado del cuello, los tenis que usa para el camino y mete en la mochila los zapatos de la oficina, el cargador y el tupper."),
        v("Le prepara a Mateo el desayuno, un licuado de plátano y unas galletas, mientras Daniela lo viste a medio dormir. El niño quiere ver caricaturas en el teléfono. Él se lo da, cinco minutos, que siempre son diez. Afuera todavía no sale el sol."),
        v("Antes de salir, besa a Daniela, le revuelve el pelo a Mateo y se persigna sin pensarlo frente a la Virgen de Guadalupe del calendario de la cocina, que regaló la tortillería. Revisa tres veces que traiga las llaves, la cartera y el teléfono. Siempre olvida algo.", hasta = "mudanza"),
        v("El departamento es chico: dos recámaras, una sala que también es comedor y un baño donde no caben dos. Él sale de puntitas, entre juguetes, para no pisar ninguno. Pisa uno. Mateo no se despierta, y él se va cojeando hacia el elevador, que no sirve.", hasta = "mudanza"),
        v("En el departamento nuevo, más grande y más lejos, el sol sale por la ventana de la cocina y le da en la cara mientras se toma el café. Mateo ya tiene su cuarto. Él todavía no se acostumbra al camino, que es media hora más largo. Sale media hora antes.", desde = "mudanza", hasta = "casa"),
        v("En la casa del fraccionamiento todavía es de noche cuando sale, y la calle es una fila de casas iguales, cada una con su coche o sin coche, su perro y su reja. Él camina hasta la parada de la combi con otros que salen a la misma hora, en silencio, como en un desfile.", desde = "casa"),
    )

    /** La semana en la playa (estado «playa», temporada 2). */
    private val PLAYA = listOf(
        v("Se despierta en un cuarto de hotel chico, con un aire acondicionado ruidoso y el mar a dos cuadras, sin alarma, a la hora que quiere el cuerpo, que es la misma de siempre. Se queda acostado oyendo respirar a Daniela y a Mateo, sin moverse, para que dure.", estado = "playa"),
        v("A esta hora está en el mar con Mateo, que no se quiere soltar de su cuello y luego no se quiere salir del agua. Las olas son tibias. Él lo levanta cada vez que viene una y el niño grita de miedo y de gusto, que en los niños es lo mismo.", estado = "playa", hacer = Hacer.TRABAJO),
        v("A esta hora comen en una palapa de la playa: pescado zarandeado, tortillas, arroz y una michelada, con los pies en la arena. Un vendedor les ofrece pulseras, otro sombreros, otro un tatuaje de henna. Él dice que no a todos, y a Mateo le compra un barquito.", estado = "playa", hacer = Hacer.COMER),
        v("A esta hora camina por la orilla con Daniela, mientras Mateo junta conchas, sin mirar el teléfono, que se quedó en el cuarto. Le da un poco de miedo no tenerlo. Luego se le olvida. Hablan de cosas que no hablan en la ciudad, porque en la ciudad no hay tiempo.", estado = "playa", hacer = Hacer.CAMINO),
        v("A esta hora se pone bloqueador, le pone bloqueador a Mateo, que se escapa, y le pone a Daniela en la espalda. Aun así se quema la nariz. Arreglan la mochila de la playa: toallas, agua, galletas, una cubeta. Parece que se van a vivir allá.", estado = "playa", hacer = Hacer.CUIDAR),
        v("A esta hora ve el atardecer desde la playa con una cerveza fría, mientras Mateo duerme en la toalla, lleno de arena. El sol se mete en el mar, rojo, enorme. Él le toma una foto y la sube, y luego se arrepiente de haberla tomado en vez de mirarlo.", estado = "playa", hacer = Hacer.LIBRE),
        v("A esta hora duerme en el cuarto del hotel, con la piel ardiendo del sol y el ruido del mar por la ventana. Sueña que está en la oficina y que no le alcanza la factura. Se despierta, oye el mar, y se vuelve a dormir sonriendo.", estado = "playa", hacer = Hacer.DORMIR),
        v("Al caer la tarde, en la playa, la gente se junta en el malecón a ver el sol, a comprar elotes y a caminar sin rumbo. Hay música de banda en una palapa y un mariachi en otra. Él camina con Mateo en los hombros y Daniela de la mano, y por una vez no tiene prisa.", estado = "playa"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} El tiempo se mira en el teléfono, que dice la temperatura y si va a llover, y casi siempre se equivoca."),
        v("Mira el cielo un momento desde la parada del camión, entre cables y espectaculares. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En el grupo de la familia su mamá manda cada mañana el clima y una frase bonita con una rosa."),
        v("{cielo} En tiempo de {estacion} la ciudad huele distinto, a tierra mojada o a polvo, y él lo nota en el camión con las ventanas abiertas."),
        v("A veces, desde el puente del periférico, se ven los cerros que rodean la ciudad, cuando el aire está limpio. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Su abuelo decía que antes llovía a la hora exacta, las cuatro de la tarde. Ahora llueve cuando quiere."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, y el sol de la altura pega fuerte desde las diez.",
            "Hace un día claro, de los que dejan ver los cerros del otro lado del valle.",
            "No hay una nube, y los vidrios de los edificios brillan tanto que lastiman.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, de un gris que no se decide.",
            "Hay nubes sobre los cerros que no dejan pasar el sol.",
            "Está nublado, y la ciudad se ve más chica, como encerrada.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y las calles se encharcan en cinco minutos y el tráfico se para.",
            "Cae una lluvia fina que no moja, dicen, hasta que ya se está empapado.",
            "Llueve, y en las esquinas aparecen vendedores de paraguas que no estaban hace un minuto.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: cae granizo sobre los coches y los pasos a desnivel se inundan.",
            "Truena sobre la ciudad, y en la oficina se va la luz un momento y vuelve.",
        ),
        Clima.NIEBLA to listOf(
            "Hay neblina en la mañana, y los edificios altos se pierden a la mitad.",
            "La neblina, mezclada con el esmog, tapa los cerros y hace la mañana gris.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento, y las lonas de los puestos del tianguis se inflan.",
            "Hay viento, y las bolsas de plástico vuelan sobre la avenida como pájaros.",
        ),
        Clima.POLVO to listOf(
            "Hay polvo en el aire, y el sol se ve como una moneda vieja.",
            "Hay esmog y polvo, y en el teléfono avisan que el aire está malo.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.SECAS to listOf(
            "Es tiempo de secas, de mañanas frías y tardes calientes, y de suéter que se carga en la mano.",
            "En secas florecen las jacarandas y pintan de morado las banquetas y los coches.",
            "Son las secas, cuando el esmog se queda encima de la ciudad como una tapa.",
        ),
        Estacion.LLUVIAS to listOf(
            "Es tiempo de lluvias: las mañanas son limpias y cada tarde se cae el cielo.",
            "En lluvias se sale con paraguas y se vuelve mojado de todos modos.",
            "Es tiempo de aguas, cuando las coladeras no se dan abasto y las calles se vuelven ríos.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora captura pedidos en la computadora de la distribuidora: balatas, amortiguadores, bandas, filtros, cada uno con su código de doce cifras que se sabe de memoria. Los talleres mandan los pedidos por mensaje, con fotos de la pieza rota en la mano grasosa del mecánico.", hacer = Hacer.TRABAJO),
        v("A esta hora hace facturas, una tras otra, con el sistema que se traba cada media hora. Cuando se traba, se levanta por un café, le pregunta al de sistemas si ya, y el de sistemas le dice que ya casi. Luego vuelve, y el sistema ya, o casi.", hacer = Hacer.TRABAJO),
        v("A esta hora contesta mensajes de los talleres en el teléfono de la empresa: si hay, cuánto cuesta, cuándo llega, por qué no ha llegado. Contesta a todos con la misma paciencia, que no tiene, y con muchos emojis de manita, que son lo que la sustituye.", hacer = Hacer.TRABAJO),
        v("A esta hora baja al almacén a contar piezas con el encargado, porque el sistema dice que hay veinte y en el anaquel hay doce. Cuentan dos veces. Siguen siendo doce. Él apunta la diferencia en una hoja, y la hoja va a dar a un cajón donde hay muchas como ella.", hacer = Hacer.TRABAJO),
        v("A esta hora hay junta con el gerente, que enseña unas gráficas en la pantalla y habla de metas, de ventas y de que hay que echarle ganas. Todos dicen que sí. Él apunta algo en su libreta, por si acaso, y piensa en lo que va a comer.", hacer = Hacer.TRABAJO),
        v("A esta hora resuelve un problema que nadie más sabe resolver: un pedido que salió a un taller de otra ciudad con el número de otro. Hace llamadas, manda fotos, pide favores. Al final llega la pieza correcta, y el taller le manda un audio de agradecimiento con mucho ruido de fondo.", hacer = Hacer.TRABAJO),

        v("A esta hora va en el camión, de pie, agarrado del tubo, entre gente que duerme sentada y gente que ve el teléfono de pie. El chofer pone cumbias a todo volumen y frena como si lo persiguieran. Él se sabe todas las paradas por cómo frena.", hacer = Hacer.CAMINO),
        v("A esta hora va en el metro, apretado entre cientos, en un vagón donde no cabe un alfiler y cabe uno más en cada estación. Los vendedores ambulantes pasan igual, con bocinas en la mochila, vendiendo discos, chicles y cargadores. Él compra un cargador, otra vez.", hacer = Hacer.CAMINO),
        v("A esta hora camina del metro a la oficina, diez minutos por una avenida llena de puestos de tacos, de jugos y de tamales, de gente con prisa y de coches parados. Compra un jugo verde, porque se prometió comer mejor, y un tamal, porque tiene hambre.", hacer = Hacer.CAMINO),

        v("A esta hora come en la cocinita de la oficina lo que trajo en el tupper: arroz, frijoles, un guisado de lo de ayer. Lo calienta en el microondas, donde hay fila, y se lo come viendo videos en el teléfono, con los compañeros, que también ven videos.", hacer = Hacer.COMER),
        v("A esta hora come en la fonda de la esquina la comida corrida: sopa de pasta, arroz, guisado, frijoles, tortillas, agua del día y gelatina de postre, por un precio que sube cada mes. La señora de la fonda ya le sabe el gusto, y le pone más tortillas.", hacer = Hacer.COMER),
        v("A esta hora come unos tacos de canasta parado en una esquina, con un refresco de botella de vidrio, rápido, porque hoy no hay tiempo. El taquero tiene una bicicleta con una canasta enorme tapada con un mantel, y los tacos salen sudados y calientes.", hacer = Hacer.COMER),

        v("A esta hora deja a Mateo en casa de su mamá, que se lo cuida mientras él y Daniela trabajan. Su mamá ya lo está esperando en la puerta, en bata, con el desayuno listo para el niño, que ya desayunó. Le dice que trae la camisa mal planchada. Él le dice que sí.", hacer = Hacer.CUIDAR),
        v("A esta hora lava los trastes de la cena de anoche, que se quedaron en el fregadero, mientras Daniela se arregla. En su casa los dos trabajan y los dos hacen lo de la casa, o eso dicen. Su mamá dice que en sus tiempos no era así. Él dice que por eso.", hacer = Hacer.CUIDAR),
        v("A esta hora busca los zapatos de Mateo, que nunca están donde los dejaron, y la mochila, y el suéter, mientras el niño se esconde detrás del sillón porque es juego. Él juega un poco, porque no hay tiempo, y lo encuentra enseguida, y el niño se ríe.", hacer = Hacer.CUIDAR),

        v("A esta hora ve una serie en la plataforma, en la sala, con Daniela, los dos en el sillón, cada uno con su teléfono en la mano además de la serie. Mateo ya se durmió, o finge. Al tercer capítulo Daniela se queda dormida. Él ve uno más.", hacer = Hacer.LIBRE),
        v("A esta hora juega futbol en una cancha de pasto sintético con los amigos de la secundaria, que se juntan cada semana desde hace quince años. Corren menos que antes y gritan más. Después se toman una cerveza en la banqueta y hablan de todo y de nada.", hacer = Hacer.LIBRE),
        v("A esta hora pasa videos en el teléfono, uno tras otro, acostado en la cama: chistes, futbol, recetas, noticias, un perro que baila, un político que grita. Se ríe solo. Cuando levanta la vista, ya pasó una hora, como cada noche, y se promete que mañana no.", hacer = Hacer.LIBRE),

        v("A esta hora duerme, con el teléfono cargando en el buró y la luz verde parpadeando en la oscuridad. Afuera ladran los perros de la cuadra, todos a la vez, por algo que solo ellos saben. Una moto pasa con el escape abierto, y luego nada.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, cansado, con Mateo atravesado en medio de la cama, porque se pasó a media noche y nadie tuvo fuerzas para regresarlo. El niño patea dormido. Él lo abraza sin despertar, y duerme peor y mejor.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, y sueña con facturas, con el camión que no llega y con un examen de la secundaria que nunca hizo. Se despierta un momento, mira la hora en el teléfono, ve que faltan tres horas para la alarma y se vuelve a dormir aliviado.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("Su colonia es de casas de dos y tres pisos, cada una pintada de un color y con varilla saliendo del techo, por si un día se construye otro piso. Hay una tienda en cada esquina, un puesto de tacos en cada cuadra, una iglesia, un gimnasio, un tianguis los miércoles y cables por todas partes."),
        v("La ciudad crece hacia todos lados: donde hace veinte años había milpas ahora hay fraccionamientos de casas iguales, plazas comerciales, bodegas y gasolineras. Para cruzarla de un lado a otro se tarda lo mismo que en ir a otra ciudad. Nadie se sorprende."),
        v("En el tianguis de los miércoles se vende de todo, en puestos con lonas rosas y azules: fruta, ropa de paca, películas, juguetes, tenis, comida. Se camina despacio, entre gritos de güerito, qué le damos. Él va cuando puede, por unos tacos de barbacoa."),
        v("Por la avenida pasa todo a la vez: camiones de pasajeros, coches nuevos y viejos, motos de reparto con cajas en la espalda, bicicletas, un señor con un carrito de elotes. El tráfico se para a cualquier hora, por cualquier cosa, y nadie sabe nunca por qué."),
        v("En la plaza comercial de la avenida hay un cine, un supermercado, tiendas de teléfonos y restaurantes de cadena, y los domingos se llena de familias que van a caminar con aire acondicionado. Mateo quiere subirse a los carritos de monedas. Él siempre trae monedas."),
        v("En la colonia todos se conocen de vista y casi nadie de nombre, salvo los vecinos de toda la vida, que saben todo de todos. Hay un grupo de mensajes de la cuadra donde se avisa de robos, perros perdidos, cortes de agua y fiestas. Él nunca escribe en él, pero lo lee todo."),
    )

    private val GENTE = listOf(
        v("Daniela, su esposa, atiende el mostrador de una farmacia de cadena, con bata blanca y gafete, y sabe de medicinas más que algunos doctores. Se conocieron en una boda de un primo, bailando una cumbia que ninguno de los dos sabía bailar. Todavía no saben, y la bailan igual."),
        v("Mateo, su hijo, tiene la edad de preguntar por qué y de no esperar la respuesta. Le gustan los dinosaurios, los camiones de bomberos y el teléfono de su papá, en ese orden, que cambia cada semana. Cuando él llega del trabajo, Mateo corre a la puerta y le pide el teléfono."),
        v("Su mamá vive a unas cuadras, en la casa donde él creció, y lo llama todos los días a la hora de la comida para preguntarle si ya comió. Le cuida a Mateo, le hace de comer los domingos y le da consejos que nadie le pide. Él se queja de ella con cariño, y ella de él igual."),
        v("El gerente de la distribuidora es un señor de lentes que llegó de la oficina de la capital hace un año, con muchas ideas y una camisa planchada en tintorería. Dice que son una familia. Él piensa que en las familias no se pagan las horas extras, y por eso tiene razón."),
        v("Su compadre Toño, el padrino de Mateo, es mecánico y tiene su taller en la colonia, donde él va a echarse una cerveza los sábados y a ver cómo arreglan los coches. Toño le compra las refacciones a la distribuidora, y siempre le pide descuento. Él siempre se lo consigue."),
        v("Mateo va a la guardería del Seguro Social, donde le enseñan canciones, colores y a lavarse las manos, y de donde vuelve con la ropa pintada de témpera y un dibujo que no se entiende. Él lo pega en el refri con un imán de una pizzería.", hasta = "mateo_kinder"),
        v("Mateo ya va al kínder, con uniforme de pants y una mochila de dinosaurios con rueditas, y vuelve contando lo que dijo la maestra y lo que hizo un niño que se llama Ian. Él le pregunta qué aprendió, y Mateo le canta una canción en inglés que tiene tres palabras.", desde = "mateo_kinder", hasta = "primaria"),
        v("Mateo ya va a la primaria, y la tarea es de los padres más que de él. En la noche hacen planas, sumas y maquetas en la mesa de la cocina, y él descubre que no se acuerda de nada de lo que le enseñaron. Lo busca en el teléfono, a escondidas del niño.", desde = "primaria"),
        v("Esperan otro hijo, y la noticia recorrió el grupo de la familia en un minuto, con stickers, corazones y audios de tías que lloran. Daniela sigue trabajando en la farmacia, de pie todo el día, y él le soba los pies en la noche. Su mamá ya está tejiendo algo.", desde = "esperan", hasta = "nace_valeria"),
        v("Valeria, la pequeña, se queda con su abuela mientras ellos trabajan, y cuando él llega en la noche ya está bañada y oliendo a talco. Él la carga antes de quitarse la mochila. Mateo dice que es suya, la hermana, y no deja que nadie más le haga caras.", desde = "nace_valeria"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Él lo sabe como se saben las cosas de la ciudad: sin haberlo aprendido nunca."),
        v("Hay cosas que solo se miran cuando faltan. {dato_objeto}"),
        v("{dato_objeto} En la colonia se dice que lo que no está en el teléfono no existe, y lo dicen por el teléfono."),
        v("{dato_objeto} Son cosas que se aprenden mirando a los demás en el camión, como casi todo en la ciudad."),
        v("En su mochila caben casi todas las cosas que necesita para un día, y algunas que no. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("telefono", "el teléfono", "un teléfono", "El teléfono",
            "El teléfono es de gama media, con la pantalla estrellada en una esquina y una funda del equipo de futbol; lo pagó a meses y le faltan seis, y ahí está todo: el banco, el trabajo, las fotos de Mateo, la música y el grupo de la familia."),
        ObjetoCuento("audifonos", "los audífonos", "unos audífonos", "Los audífonos",
            "Los audífonos son inalámbricos, comprados en el metro, y se pierden cada dos meses; con ellos se oye música en el camión, se contesta el teléfono sin manos y se hace como que no se oye al vendedor de chicles."),
        ObjetoCuento("tarjeta_metro", "la tarjeta del metro", "una tarjeta del metro", "La tarjeta",
            "La tarjeta del metro es de plástico, recargable, y sirve también para el metrobús y algunos camiones; se recarga en las máquinas de las estaciones, que a veces aceptan billetes y a veces no, y cada vez que pasa por el torniquete pita como un despertador."),
        ObjetoCuento("termo", "el termo", "un termo", "El termo",
            "El termo es de acero, de un litro, con el logo de una marca de cerveza que le regalaron en una feria; lleva café en la mañana y agua en la tarde, y está abollado de tantas caídas en el camión."),
        ObjetoCuento("tupper", "el tupper", "un tupper", "El tupper",
            "El tupper es de plástico, con la tapa de otro tupper que no es el suyo, porque las tapas y los tuppers nunca coinciden en ninguna casa de México; lleva la comida de ayer, y vuelve a casa vacío y oliendo a guisado."),
        ObjetoCuento("llaves", "las llaves", "unas llaves", "Las llaves",
            "Las llaves son cinco, en un llavero de una virgen y un destapador: la de la casa, la de la reja, la del zaguán, la de la oficina y una que no se sabe de qué es pero nadie se atreve a tirar."),
        ObjetoCuento("cargador", "el cargador", "un cargador", "El cargador",
            "El cargador del teléfono es lo único que no puede faltar en la mochila; el cable se pela cada pocos meses, y en el metro venden otros a tres por cien pesos, que duran lo que duran."),
        ObjetoCuento("refresco", "el refresco", "un refresco", "El refresco",
            "El refresco de cola se compra frío en la tienda de la esquina, en botella de vidrio retornable o de plástico, y se toma con la comida, con la cena, con los tacos y con el pan; en México se toma más que en casi cualquier parte, y se sabe."),
        ObjetoCuento("tamal", "el tamal", "un tamal", "El tamal",
            "El tamal de la mañana se compra en el carrito de la esquina, verde, rojo, de rajas o de dulce, y se come dentro de un bolillo, que es la guajolota, con un atole; es el desayuno de los que no desayunaron, que son muchos."),
        ObjetoCuento("credencial", "la credencial para votar", "una credencial para votar", "La credencial",
            "La credencial para votar es de plástico, con la foto, la firma, la huella y la dirección; sirve para votar y para todo lo demás, porque en México es la identificación de todos, y perderla es un trámite largo."),
        ObjetoCuento("mochila", "la mochila", "una mochila", "La mochila",
            "La mochila de la oficina es negra, de una marca de computadoras que no tiene, y lleva siempre lo mismo: el tupper, el termo, el cargador, los zapatos de la oficina, un paraguas, una pastilla para el dolor de cabeza y unas galletas, por si acaso."),
        ObjetoCuento("paraguas", "el paraguas", "un paraguas", "El paraguas",
            "El paraguas es negro, plegable, comprado en una esquina un día de aguacero por el doble de lo que vale; se voltea con el viento y se olvida en el camión, y en temporada de lluvias se compra uno cada mes."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} Él piensa en su ciudad como se piensa en el tiempo: no se la elige, se vive en ella. Es enorme, lenta, insegura y suya. A sus {edad} años no se iría a otra parte, salvo, a veces, a Estados Unidos, que es otra cosa."),
        v("Piensa a veces que su abuelo nació en un rancho, sin luz ni agua, y que él trabaja frente a una computadora y paga todo con el teléfono. Entre los dos hay sesenta años, y parecen trescientos. Su abuelo no lo entendería. Él tampoco entiende del todo a su abuelo."),
        v("{mundo} Él piensa en cosas más cercanas: en la renta, en la quincena, en la tarjeta de crédito, en la escuela de Mateo, en el camión. Las cosas grandes pasan en las noticias, y las noticias pasan muy rápido, y él no alcanza a verlas."),
        v("{mundo} A sus {edad} años, tiene un trabajo, una familia y tres horas de camino al día. Lo piensa en el metro, a veces, y le da orgullo y cansancio a la vez, como cuando termina un partido de futbol: ganó, pero le duelen las piernas."),
        v("Piensa en la gente con la que se cruza cada día en el metro, cientos, miles, a los que nunca volverá a ver. Le parece raro vivir tan cerca de tanta gente que no conoce. Luego le llega un mensaje del grupo de la familia, y deja de pensarlo."),
        v("{mundo} A veces, en el camión, mira a los niños que van a la escuela con sus mochilas y piensa en Mateo, y en qué será cuando sea grande: ingeniero, futbolista, o alguien que trabaje en un país lejano. No lo sabe. Le basta con que pueda elegir más que él."),
    )

    private val MUNDOS = mapOf(
        "aranceles" to listOf(
            "En la oficina se habla de los aranceles de Estados Unidos, y de si la distribuidora va a recortar gente.",
            "En el grupo de la familia se habla de la elección de jueces, que nadie entiende bien.",
            "Se habla de la Copa Oro, del precio del huevo y de la inseguridad, en ese orden o en otro.",
        ),
        "mundial" to listOf(
            "Se habla del Mundial, que se juega en México, y de que esta vez la selección pasa del quinto partido.",
            "En la oficina se habla de que van a bajar la jornada a cuarenta horas, y nadie se lo cree.",
        ),
        "elecciones" to listOf(
            "Se habla de las elecciones, y en las bardas de la colonia hay nombres de candidatos pintados encima de otros.",
            "En el grupo de la familia se habla de la boda de una prima y de los precios.",
        ),
        "olimpiada" to listOf(
            "Se habla de las Olimpiadas de Los Ángeles, que están cerca, y de los mexicanos que van.",
            "En la oficina se habla de que van a poner un sistema nuevo, y de que ese sí va a funcionar.",
        ),
        "fraccionamiento" to listOf(
            "Se habla de un asteroide que va a pasar muy cerca de la Tierra, y en el teléfono hay mil videos que lo explican mal.",
            "En el grupo de la cuadra se habla de la caseta de vigilancia, de las cuotas y de un perro que ladra toda la noche.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando sale de la oficina ya está oscureciendo, y la avenida es un río de luces rojas de coches parados. Él camina al metro con los audífonos puestos, entre miles que van al mismo lado. Es el único rato del día que es suyo, y lo pasa apretado."),
        v("Al caer la tarde la ciudad huele a tacos al pastor, a esquites y a lluvia, si llovió. Él se compra unos esquites en la salida del metro y se los come caminando, con chile y limón, y le gotea el caldo en la camisa de la oficina. No importa: ya no la ve nadie."),
        v("Se cena tarde, cuando todos llegan: unas quesadillas, unos huevos, lo que haya, con café con leche o refresco. Mateo cuenta lo que hizo, Daniela lo de la farmacia, él lo del camión. Luego se lavan los trastes, o se dejan para mañana, que también es cena."),
        v("De noche, desde la ventana, se ven las ventanas de los vecinos, cada una con su luz azul de televisión o de teléfono. Se oyen ruidos de cocina, una pelea, una canción de banda. Él se queda un momento mirando, con una cerveza, antes de meterse."),
        v("Anochece sobre la ciudad, y en el cielo no se ven estrellas: se ve un resplandor naranja, y a veces un avión que baja al aeropuerto. Él se queda un rato en la azotea, o en la ventana, oyendo a la ciudad, que no se calla nunca, y le gusta así."),
        v("La última luz del día se queda un momento en los cerros que rodean la ciudad, cuando abajo ya es de noche. Él la ve desde el camión, por la ventana, sin pensar en nada. Luego le llega un mensaje, y ya no la ve."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "reyes" to unDia(
            Parrafo.MANANA to "Hoy es Día de Reyes, y Mateo despertó a todos a las seis para ver qué le trajeron, junto a los zapatos que dejó anoche junto a la ventana: un camión de bomberos con luces y sirena, que ya no se calla. La carta que escribió con ayuda de su papá pedía cinco cosas. Llegó una.",
            Parrafo.GENTE to "En la tarde se parte la rosca en casa de su mamá, con chocolate caliente y toda la familia, y cada uno corta su pedazo con miedo. A él le sale el muñeco, como casi todos los años, porque su mamá le pone la rosca hacia su lado. Le toca pagar los tamales de la Candelaria.",
            Parrafo.PENSAMIENTO to "Él piensa en los Reyes de cuando era niño, que eran más pobres que los de ahora y le traían una pelota y unos calcetines, y que él esperaba igual con el corazón en la boca. Mira a Mateo con su camión que no se calla, y no le dice que baje el volumen.",
        ),
        "candelaria" to unDia(
            Parrafo.MANANA to "Hoy es día de la Candelaria, y le toca pagar los tamales, porque le salió el muñeco en la rosca. En la mañana, antes del trabajo, encarga cincuenta en la tamalería de la esquina: verdes, rojos, de rajas con queso y de dulce, rosas.",
            Parrafo.PUEBLO to "En la iglesia de la colonia hay misa para bendecir a los Niños Dios, que las señoras llevan vestidos con ropa nueva: de doctor, de futbolista, de santo. En el mercado hay puestos que solo venden ropa de Niño Dios, y señoras que pasan con el suyo en brazos.",
            Parrafo.ANOCHECER to "Esta noche se comen los tamales en casa de su mamá, con atole de chocolate y de arroz, toda la familia alrededor de la mesa. Su tío dice que el año pasado estaban mejores. Él dice que el año pasado los pagó él también. Todos se ríen.",
        ),
        "viernes_santo" to unDia(
            Parrafo.MANANA to "Hoy es Viernes Santo, en plena Semana Santa, y la ciudad está medio vacía: media ciudad se fue a la playa o a su pueblo y la otra media se quedó a descansar. No hay tráfico. Él sale a la calle y le parece otra ciudad, más grande y más callada.",
            Parrafo.PUEBLO to "En la colonia hay procesión del Santo Entierro, y en otras partes de la ciudad hacen la Pasión en la calle, con un muchacho del barrio cargando una cruz y miles mirando, y la pasan en la televisión. En los mercados venden pescado, romeritos y capirotada.",
            Parrafo.GENTE to "Hoy se come pescado en casa de su mamá, que no deja que nadie coma carne en Viernes Santo aunque nadie vaya a misa. Mateo pregunta por qué. Su mamá le explica, y Mateo pregunta por qué otra vez, y su mamá le dice que porque sí, que es la mejor explicación.",
        ),
        "dia_nino" to unDia(
            Parrafo.MANANA to "Hoy es Día del Niño, y Mateo tiene festival: le pintaron la cara de tigre, le dieron una bolsa de dulces y bailó una canción con sus compañeros, todos desacompasados. Daniela pidió permiso en la farmacia para ir. Él lo vio en video, en la oficina, cinco veces.",
            Parrafo.TRABAJO to "A esta hora, en la oficina, todos se enseñan los videos de sus hijos en los festivales del Día del Niño. El gerente también. Por una hora nadie habla de facturas. Luego alguien dice que ya, a trabajar, y nadie se mueve hasta que acaba el último video.",
            Parrafo.ANOCHECER to "Esta noche lleva a Mateo a comer pizza, que es lo que pidió, y luego a la plaza comercial a subirse a los carritos de monedas. Mateo se queda dormido en el camión de regreso con la cara todavía medio pintada de tigre. Él le toma una foto, para la abuela.",
        ),
        "madres" to unDia(
            Parrafo.MANANA to "Hoy es Día de las Madres, y desde temprano el grupo de la familia está lleno de flores, corazones y frases. Él le manda a su mamá un audio cantándole las mañanitas, desafinado, y su mamá le contesta con otro audio, llorando, que lo escuchó tres veces.",
            Parrafo.PUEBLO to "En la colonia no hay una flor libre: los puestos de flores se acaban desde la mañana, y los restaurantes tienen fila en la puerta. En la escuela de la esquina hay festival, y los mariachis van de casa en casa, contratados por los hijos que pueden pagar.",
            Parrafo.GENTE to "En la comida, en casa de su mamá, le dan entre los hermanos un regalo envuelto con un moño enorme, y su mamá dice que no hacía falta, que para qué gastan. Daniela también es mamá, y le dan flores, y Mateo le da un dibujo de ella con tres ojos. A ella le gusta más el dibujo.",
        ),
        "lluvias" to unDia(
            Parrafo.MANANA to "Hoy llegaron las lluvias. A las cuatro de la tarde el cielo se puso negro, sonó un trueno que hizo vibrar los vidrios de la oficina y cayó un aguacero con granizo, como si alguien hubiera volteado una cubeta sobre la ciudad.",
            Parrafo.PUEBLO to "En media hora las calles se inundaron, los pasos a desnivel se llenaron de agua, los coches se quedaron varados y el metro avanzó despacio, con las estaciones llenas de gente mojada esperando. En el teléfono, los videos de las calles convertidas en ríos.",
            Parrafo.ANOCHECER to "Esta noche llega a casa tres horas después de lo normal, empapado, con los zapatos de la oficina en la mano y los tenis mojados. Daniela le tiene un café caliente. Él se ríe de sí mismo en el espejo. Desde mañana, piensa, paraguas hasta octubre.",
        ),
        "grito" to unDia(
            Parrafo.MANANA to "Hoy es quince de septiembre, la noche del Grito, y la ciudad está adornada de verde, blanco y rojo: banderas en los coches, en los balcones, en los puestos. En la oficina se trabaja medio día, y se habla de la noche y de quién hace el pozole.",
            Parrafo.PUEBLO to "A las once de la noche la presidenta sale al balcón de Palacio Nacional, toca la campana y grita los vivas, y en todas las casas del país se ve por la televisión. En la colonia truenan cohetes, suenan las matracas, y los vecinos gritan desde las azoteas.",
            Parrafo.ANOCHECER to "Esta noche se hace pozole en casa de su mamá, rojo, con rábano, lechuga y orégano, y tostadas con crema. Llegan los tíos, los primos y los compadres, con cervezas y tequila. A las once todos gritan viva México frente a la tele, y Mateo grita más fuerte que nadie.",
        ),
        "simulacro" to unDia(
            Parrafo.MANANA to "Hoy es diecinueve de septiembre, el día de los temblores, y al mediodía hay simulacro. A la hora suena la alerta sísmica en los altavoces de la calle y en los teléfonos, con su chillido largo, que pone a todos la piel de gallina aunque se sepa que es de mentira.",
            Parrafo.TRABAJO to "A esta hora salen todos de la oficina, en fila, por la escalera, con el brigadista del chaleco naranja contando cabezas, y se paran en el estacionamiento, en el punto de reunión. Nadie corre. Todos se acuerdan de algo, y nadie lo dice.",
            Parrafo.PENSAMIENTO to "Él se acuerda del temblor de hace años, el de verdad, que le tocó en el trabajo, y de cómo se movía el suelo, y de las horas después, sin saber de su mamá. Desde entonces, cada diecinueve de septiembre, el cuerpo se le pone alerta solo. Hoy también.",
        ),
        "muertos" to TextoEspecial(
            listOf(
                dia(
                    Parrafo.MANANA to "Hoy es día de Todos Santos, y en casa de su mamá ya está puesto el altar: una mesa con mantel blanco, papel picado, flores de cempasúchil, velas, pan de muerto, calaveritas de azúcar con nombres y, en medio, la foto de su abuelo, con sombrero, y la de su abuela.",
                    Parrafo.PUEBLO to "En los mercados hay montañas de cempasúchil y de calaveras de azúcar, y en las calles, ofrendas en los negocios, catrinas de cartón y niños disfrazados pidiendo calaverita con una calabaza de plástico. Mateo va disfrazado de esqueleto, con la cara pintada.",
                    Parrafo.PENSAMIENTO to "Él pone en el altar una cerveza y unos cigarros para su abuelo, que fumaba hasta dormido, y piensa que esta noche viene a verlos. No sabe si lo cree. Pero se queda un rato frente a la foto, contándole en voz baja cómo va todo, por si acaso.",
                ),
                dia(
                    Parrafo.MANANA to "Hoy es día de los fieles difuntos, y él va con su mamá y con Mateo al panteón, temprano, con flores, una cubeta, una escoba y un refresco. El panteón está lleno de familias limpiando tumbas, comiendo y oyendo música, como si fuera un día de campo.",
                    Parrafo.GENTE to "Su mamá limpia la tumba de los abuelos como si limpiara la mesa, y les cambia las flores, y les platica de todos. Mateo pregunta si los abuelos lo oyen. Su mamá le dice que sí. Mateo les cuenta lo de su camión de bomberos, con todos los detalles.",
                    Parrafo.ANOCHECER to "Esta noche se come el pan de muerto del altar, que ya está un poco duro, con chocolate. Su mamá dice que ya no sabe a nada, porque los muertos se comieron el sabor. Él lo encuentra igual de bueno, y se come dos.",
                ),
            ),
        ),
        "buen_fin" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza el Buen Fin, cinco días de ofertas en todas las tiendas y en el teléfono, y desde la madrugada le llegan correos, mensajes y notificaciones de todo lo que se puede comprar a meses sin intereses. Él y Daniela hicieron una lista. La lista ya es más larga que el aguinaldo.",
                Parrafo.PUEBLO to "En la plaza comercial hay filas desde temprano, gente con carritos llenos de pantallas, estufas, colchones y licuadoras. Los vendedores gritan ofertas por micrófono. En el estacionamiento no cabe un coche. Todo el mundo compra cosas que necesitaba y cosas que no.",
                Parrafo.PENSAMIENTO to "Él piensa que la pantalla grande está muy barata, a dieciocho meses sin intereses, y que con eso se va a pagar sola. Luego piensa que todo se paga solo a dieciocho meses, y que por eso la tarjeta está como está. Al final la compra. Es para ver el futbol, se dice.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Hoy sigue el Buen Fin, y en el teléfono las ofertas cambian cada hora, como en la bolsa de valores. En la oficina todos se enseñan lo que compraron, y lo baratísimo que les salió.",
                "Hoy en la plaza hay todavía más gente, y en la sección de pantallas ya no queda la que querían. Daniela dice que mejor, que así no gastan. Él busca en el teléfono. La encuentra en otra tienda.",
                "Hoy llegan a la casa las cajas de lo que compraron en el teléfono, una tras otra, en motos de reparto. Mateo juega dentro de la caja de la pantalla, que es lo que más le gustó de todo.",
                "Hoy es el último día del Buen Fin, y las ofertas se acaban a medianoche, dicen los anuncios con un reloj que corre. Él ya no compra nada. Mira el estado de cuenta, y cierra la aplicación rápido.",
            )),
        ),
        "guadalupe" to unDia(
            Parrafo.MANANA to "Hoy es día de la Virgen de Guadalupe, y anoche, a las doce, en la televisión le cantaron las mañanitas en la Basílica, con artistas famosos. Su mamá las vio entera, cantando con la tele. En la iglesia de la colonia hubo mañanitas con mariachi y cohetes a las cinco de la mañana.",
            Parrafo.PUEBLO to "Por las carreteras llegan desde hace días los peregrinos que van a la Basílica: a pie, en bicicleta, en camiones, cargando imágenes de la Virgen en la espalda, con playeras iguales que dicen de qué pueblo vienen. Los niños van vestidos de inditos, con bigote pintado.",
            Parrafo.GENTE to "Su mamá le pide que la lleve a la iglesia de la colonia en la tarde, y él la lleva, aunque no vaya a misa desde la primera comunión. Ella pide por todos, en voz baja, con los ojos cerrados. Él se queda en la puerta, y también pide algo, por si acaso, sin decir qué.",
        ),
        "posadas" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las posadas, las nueve noches antes de Navidad, y ya está la agenda llena: la de la oficina, la de la escuela, la de la cuadra, la de la familia, la de los amigos del futbol. En el grupo de la familia se discute quién lleva qué. Nadie quiere llevar los vasos.",
                Parrafo.PUEBLO to "En la colonia hay piñatas colgadas de las azoteas, foquitos en los balcones y un nacimiento enorme en la puerta de la iglesia, con un Niño Dios que no se pone hasta Nochebuena. Por las calles se oyen villancicos de bocina y cohetes sueltos.",
                Parrafo.ANOCHECER to "Esta noche se rompe la piñata en la calle, una estrella de siete picos rellena de dulces, tejocotes, cañas y mandarinas, y los niños le pegan con los ojos vendados mientras los grandes cantan. Mateo le da al aire tres veces. Luego, ponche caliente con piquete para los grandes.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "Esta noche es la posada de la oficina, en un salón de fiestas, con comida, intercambio de regalos y un sonido que pone cumbias a todo volumen. El gerente baila. Todos lo graban. Él se lleva de regalo una taza con su nombre mal escrito.",
                "Esta noche es la posada de la cuadra, y se cierra la calle con lonas y mesas. Cada familia lleva algo, y siempre sobra comida y falta hielo. Los niños corren entre las mesas, y los grandes se quedan platicando hasta tarde.",
                "Esta noche es Nochebuena, y se cena en casa de su mamá: bacalao, romeritos, pavo y ensalada de manzana, con toda la familia. A medianoche se arrulla al Niño Dios, se abrazan todos y truenan cohetes. Mateo se queda dormido debajo de la mesa.",
            )),
        ),
        "lavadora" to unDia(
            Parrafo.MANANA to "Hoy sacó un crédito en una tienda departamental para comprar una lavadora, porque la vieja, que era de su mamá, por fin se murió con un ruido como de avión. La pagan en abonos semanales durante un año. El vendedor le explicó los intereses muy rápido, y él no preguntó.",
            Parrafo.GENTE to "En la tarde llegan dos muchachos con la lavadora en una camioneta y la suben entre los dos por la escalera, sudando. Daniela la estrena en la noche con la ropa de toda la semana, y se queda mirándola girar por la ventanita, como si fuera la televisión.",
            Parrafo.PENSAMIENTO to "Él hace cuentas en el camión: la renta, la luz, el teléfono, la guardería, la tarjeta y ahora la lavadora, cada semana. Las cuentas salen apenas, si nadie se enferma y no se rompe nada más. Pero no se rompe nada más. Hoy no.",
        ),
        "aranceles" to unDia(
            Parrafo.MANANA to "Hoy entraron en vigor los aranceles de Estados Unidos a lo que se vende desde México, y en las noticias no se habla de otra cosa: gráficas, presidentes, el peso que sube y baja. En la distribuidora, muchas de las refacciones vienen de allá o van para allá.",
            Parrafo.TRABAJO to "A esta hora hay junta de emergencia con el gerente, que habla de incertidumbre, de precios nuevos y de que no hay que preocuparse todavía. Todos se preocupan. Al salir, en el pasillo, se habla de quién se iría primero si hay recortes. Él no quiere estar en esa lista.",
            Parrafo.PENSAMIENTO to "Él piensa que hay un señor en otro país que firma un papel y que eso llega hasta su escritorio, hasta su quincena, hasta la lavadora que debe. Le parece injusto y le parece normal, las dos cosas a la vez. Esta noche no le dice nada a Daniela, para no preocuparla.",
        ),
        "jueces" to unDia(
            Parrafo.MANANA to "Hoy es la primera elección de jueces de la historia del país, y él va a votar a la escuela de la esquina con Daniela, temprano, antes de que haga calor. Las boletas son tantas y tan largas que le dan una guía, y aun así no conoce a casi nadie de las listas.",
            Parrafo.PUEBLO to "En la casilla casi no hay gente: unos señores mayores, unos muchachos con playeras de un partido y los funcionarios aburridos, que leen el teléfono. En otras elecciones había fila hasta la esquina. Hoy, se dirá después, votó muy poca gente.",
            Parrafo.PENSAMIENTO to "Él piensa que votó por gente que no conoce, para cargos que no entiende, porque cree que hay que votar. Luego piensa que a lo mejor así es siempre, y que hoy solo se nota más. Se pinta el dedo con la tinta y le toma una foto, por costumbre.",
        ),
        "copa_oro" to unDia(
            Parrafo.MANANA to "Hoy México le ganó a Estados Unidos la final de la Copa Oro, dos a uno, y desde que acabó el partido la colonia entera estalló: cláxones, gritos, cohetes, banderas en los coches. Él lo vio en casa de su compadre Toño, con los amigos y una hielera.",
            Parrafo.PUEBLO to "En la avenida hay caravanas de coches tocando el claxon con banderas por las ventanas, y en la glorieta del centro miles de aficionados con la playera verde, cantando y echándose cerveza. La policía mira desde lejos. Por una noche, todos de acuerdo.",
            Parrafo.GENTE to "Mateo no entiende mucho de futbol, pero grita gol cada vez que grita su papá, aunque no haya gol. Toño lo sube a los hombros para que vea la caravana. Daniela les manda una foto desde la farmacia, donde las compañeras también gritaron.",
        ),
        "mateo_kinder" to unDia(
            Parrafo.MANANA to "Hoy Mateo empezó el kínder, con uniforme de pants, una mochila de dinosaurios con rueditas y un lonche de sándwich y fruta picada que preparó Daniela. En la puerta de la escuela había cincuenta papás tomando fotos, y él también tomó cincuenta.",
            Parrafo.GENTE to "Mateo entró llorando, agarrado de la pierna de su mamá, y la maestra lo despegó con mucha experiencia y una calcomanía de estrella. Daniela lloró en la banqueta. Él no lloró, porque alguien tenía que manejar la situación, y se fue al trabajo con los ojos rojos.",
            Parrafo.PENSAMIENTO to "Él piensa en su propio primer día de escuela, que no recuerda, y en que Mateo tampoco lo va a recordar. Lo recordarán ellos. Piensa que los hijos crecen así, en días que solo recuerdan los padres. En el camión, ve las fotos una por una.",
        ),
        "iztapalapa" to unDia(
            Parrafo.MANANA to "Hoy explotó una pipa de gas en un puente de Iztapalapa, en la mañana, en plena hora del tráfico, y en el teléfono aparecen los videos casi enseguida: la bola de fuego, los coches en llamas, la gente corriendo con la ropa quemada. Él tiene que dejar de verlos.",
            Parrafo.TRABAJO to "A esta hora, en la oficina, nadie trabaja bien: todos ven los videos y las noticias a escondidas. Un compañero tiene una tía que vive cerca y le llama, y la tía está bien. Alguien pasa una lista para donar sangre. Él se apunta.",
            Parrafo.PENSAMIENTO to "Él piensa en los que iban al trabajo, como él, en un camión o en un coche, pensando en la quincena, y que de pronto ya no. Piensa en cuántas pipas de gas pasa cada día sin mirarlas. Esta noche le escribe a su mamá que la quiere, sin razón.",
        ),
        "renta" to unDia(
            Parrafo.MANANA to "Hoy el casero les mandó un mensaje: desde el mes que viene la renta sube, bastante, porque todo sube, dice, y porque hay otros interesados. Él lo lee tres veces en el camión. Le contesta que sí, que está bien, con un pulgar arriba, y luego se arrepiente del pulgar.",
            Parrafo.GENTE to "En la noche, con Daniela, hacen cuentas en la mesa de la cocina, con una calculadora y el teléfono. Salen menos. Daniela dice que a lo mejor hay que buscar algo más lejos, más barato. Él dice que más lejos es más camión. Los dos tienen razón.",
            Parrafo.PENSAMIENTO to "Él piensa que su papá compró un terreno a plazos a su edad, y que él no puede ni pagar la renta. Piensa que algo se rompió entre su papá y él, y que no fue culpa de ninguno de los dos. Esta noche no puede dormir hasta tarde, haciendo cuentas en la cabeza.",
        ),
        "primo" to unDia(
            Parrafo.MANANA to "Hoy llegó su primo Chuy de Estados Unidos, donde trabaja en la construcción desde hace diez años, para pasar las fiestas. Llegó manejando una camioneta enorme con placas de Texas, cargada de cajas, regalos y una bicicleta para Mateo. Toda la familia salió a la calle a verlo.",
            Parrafo.GENTE to "Chuy habla con palabras en inglés en medio de las frases y trae un reloj que brilla, pero se ríe igual que cuando eran niños y jugaban en la calle. Le cuenta lo que gana, en dólares, y lo que trabaja, que es mucho. Él le cuenta lo que gana, en pesos, y Chuy no dice nada.",
            Parrafo.PENSAMIENTO to "Él piensa en irse, como cada vez que viene Chuy: el dinero, la camioneta, los dólares. Luego piensa en Mateo, en Daniela, en su mamá, y en Chuy, que no ha visto crecer a sus hijos. Esta noche lo piensa otra vez, y otra vez se queda.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "mexico2025", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + PLAYA.take(1),
            Parrafo.TIEMPO to TIEMPO + v("En la playa el cielo es otro: alto, caliente, sin esmog, y el mar brilla tanto que hay que entrecerrar los ojos. Aquí el clima no se mira en el teléfono: se siente en la piel, que ya está roja.", estado = "playa"),
            Parrafo.TRABAJO to TRABAJO + PLAYA.subList(1, 7),
            Parrafo.PUEBLO to PUEBLO + v("El pueblo de la playa tiene una calle principal con hoteles chicos, tiendas de trajes de baño, puestos de mariscos y un malecón, y en las mañanas llegan las lanchas de los pescadores con lo de la noche. Todo el mundo va en chanclas. Él también, por primera vez en un año.", estado = "playa"),
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + PLAYA[7],
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS,
        especiales = ESPECIALES,
    )
}
