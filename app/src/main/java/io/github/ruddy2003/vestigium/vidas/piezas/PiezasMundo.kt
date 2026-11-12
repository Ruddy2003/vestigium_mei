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
 * El cuento de «Alguien del mundo», la persona promedio del mundo, que aquí es una mujer de una ciudad del norte de la
 * India. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - **No usa `{nombre}`.** Es «alguien»: el cuento dice «ella» o nada, y no le pone nombre. Su género lo decidió esta
 *   tanda (T2b): mujer; el de «Alguien de México», hombre.
 * - Trabaja en la oficina de un almacén de envíos de compras por internet. Va en un rickshaw eléctrico compartido. Su
 *   marido se llama Ravi y maneja una moto de reparto; su hermano menor, Sonu, estudia en Delhi.
 * - No repite lo que ya dice «Su vida»: los treinta años, el teléfono, las horas de internet y de música, los pasos,
 *   la comida y el azúcar, los setenta y tres años de vida, los dos hijos y las cinco mil caras.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG2.kt`):
 * - Anu va al kínder desde `anu_kinder` (temporada 1). Kabir nace en la 3 (`nace_kabir`); el embarazo va de `esperan`
 *   (2) a `nace_kabir`.
 * - Viven en dos cuartos alquilados en la azotea de una casa hasta `mudanza` (3); desde entonces, en un departamento.
 * - El estado «aldea» (temporada 2): las dos semanas en la aldea de sus padres, en Bihar.
 * - Las fiestas que vuelven cada año no nombran a Kabir ni dicen dónde viven.
 */
internal object PiezasMundo {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("La despierta la alarma del teléfono, que está debajo de la almohada, y lo primero que ve con los ojos medio cerrados son los mensajes de la noche: el grupo de la familia, el grupo del trabajo, un video de su madre que no se ve bien. Los mira todos antes de poner un pie en el suelo."),
        v("Se levanta antes que los demás, cuando la ciudad todavía está gris, y pone el agua para el chai en la estufa de gas: agua, leche, té, azúcar, jengibre y un cardamomo machacado con el mango del cuchillo. Mientras hierve, mira por la ventana cómo se despiertan los vecinos."),
        v("Desde la calle ya suben los ruidos de la mañana: el claxon de los primeros rickshaws, el vendedor de verduras con su carretilla y su pregón, una campana del templo de la esquina y, en algún teléfono, una canción de película a todo volumen. Así suena su ciudad desde que tiene memoria."),
        v("Se baña con una cubeta y un jarro, porque el agua caliente del calentador no alcanza para todos, y se peina frente al espejo pequeño con el pelo todavía mojado. Se pone un kurta de algodón, el bindi y unos aretes. En la oficina nadie la mira, pero ella se arregla igual."),
        v("Hace los parathas del desayuno en la plancha, uno tras otro, mientras con la otra mano revisa en el teléfono si llegó el sueldo, que llega por el banco y desaparece igual de rápido. Ravi se los come de pie, con chai, y sale en la moto antes de que ella termine el último."),
        v("En la repisa del rincón hay una imagen de la diosa Lakshmi, dos de Ganesha, una foto de su abuelo y una lamparita de aceite. Ella la enciende cada mañana, junta las manos un momento y pide lo de siempre, sin palabras. Luego le toma una foto a la lamparita, porque salió bonita."),
        v("Despierta a Anu, que no se quiere despertar, la viste a medias dormida, le peina dos trenzas con listones y le mete en la mochila la lonchera con el almuerzo. La niña bosteza todo el rato. Abajo ya pita el autobús amarillo de la escuela.", desde = "anu_kinder"),
        v("Sube a la azotea a tender la ropa antes de que caliente el sol: desde ahí arriba se ven mil azoteas iguales, con tinacos negros de plástico, antenas, ropa colgada y gente que hace lo mismo que ella. Algunos hacen yoga. Un mono pasa saltando por los cables.", hasta = "mudanza"),
        v("Desde la ventana del sexto piso, en el departamento nuevo, se ve la ciudad de otra manera: más grande, más gris, con grúas en todas partes levantando edificios iguales al suyo. Ella todavía no se acostumbra a tener ascensor. Lo toma siempre, aunque baje.", desde = "mudanza"),
    )

    /** Las dos semanas en la aldea, con su madre enferma (estado «aldea», temporada 2). */
    private val ALDEA = listOf(
        v("Se despierta en la casa de sus padres, en la aldea, con el canto de los gallos y el olor del fuego de estiércol con que su madre ya está calentando el agua. Aquí no hay alarma: hay luz. Sale al patio descalza, como cuando era niña.", estado = "aldea"),
        v("A esta hora ayuda a su madre, que todavía está débil, con lo de la casa: barre el patio con la escoba de ramas, saca agua de la bomba de mano y le cocina el arroz y las lentejas, como su madre le enseñó y como ya no hace en la ciudad.", estado = "aldea", hacer = Hacer.TRABAJO),
        v("A esta hora atiende el trabajo por el teléfono, desde la orilla del arrozal, donde hay más señal: contesta mensajes del almacén, revisa una lista, manda una foto. Su jefe le dio permiso, con la condición de que conteste. Contesta, y mira el campo.", estado = "aldea", hacer = Hacer.CAMINO),
        v("A esta hora come sentada en el suelo de la cocina con su madre y su padre, arroz, lentejas, verduras del huerto y un pescado del estanque. Su madre come poco, y ella la mira comer. Su padre le pregunta cuánto gana en la ciudad, y ella le dice menos de lo que gana.", estado = "aldea", hacer = Hacer.COMER),
        v("A esta hora le da masaje en los pies a su madre, con aceite de mostaza tibio, sentada en el catre del patio, mientras las vecinas vienen a preguntar y a mirar a la que se fue a la ciudad. Todas le dicen que está más gorda, que en la aldea es un elogio.", estado = "aldea", hacer = Hacer.CUIDAR),
        v("A esta hora ve videos en el teléfono, acostada en el catre del patio bajo el mosquitero, con la señal que va y viene. Ravi le manda una foto de Anu dormida con su muñeca. Ella la mira mucho rato y luego la guarda, para verla mañana.", estado = "aldea", hacer = Hacer.LIBRE),
        v("A esta hora duerme en el catre del patio, como dormía de niña en verano, bajo el mosquitero y las estrellas, que aquí se ven todas. Las ranas de los arrozales cantan tan fuerte que no se oye nada más. Duerme como no duerme en la ciudad.", estado = "aldea", hacer = Hacer.DORMIR),
        v("La aldea de sus padres es un puñado de casas de ladrillo y de barro entre arrozales, con un estanque, un templo pequeño bajo un árbol enorme y una escuela. Media aldea se fue a trabajar a las ciudades. Los que quedan son viejos, niños y mujeres, y un señor que vende recargas de teléfono.", estado = "aldea"),
        v("En la aldea todos la conocen: la hija del maestro, la que se fue a la ciudad, la que trabaja en una oficina con computadora. Sus tías le preguntan por qué no ha tenido más hijos. Sus primas le preguntan por la ciudad, con una envidia que no esconden.", estado = "aldea"),
        v("Al anochecer, en la aldea, se va la luz como todas las noches, y su madre enciende una lámpara de queroseno. Se oyen los grillos y, a lo lejos, un altavoz del templo. Ella se sienta junto a su madre en la oscuridad y le cuenta cosas de Anu, y su madre se ríe bajito.", estado = "aldea"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la ciudad el tiempo se mira en el teléfono, que dice la temperatura antes de que una salga a la calle."),
        v("Mira el cielo un momento desde la puerta, por encima de los cables enredados que cruzan la calle de poste a poste. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En el grupo de la familia su madre manda cada mañana el pronóstico de la aldea, como si ella pudiera hacer algo con eso."),
        v("{cielo} En {estacion} la ciudad cambia de olor y de ruido, y ella lo nota sobre todo en el humo de la calle."),
        v("Desde el rickshaw se ve el cielo a pedazos, entre los edificios y los anuncios gigantes de teléfonos y de cursos de inglés. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Su abuela decía que el tiempo se había vuelto loco desde que había teléfonos. Ella cree que se volvió loco antes."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un azul pálido, y el sol pega fuerte desde temprano.",
            "Hace un día claro, de los pocos en que se ven los cerros del otro lado del río.",
            "No hay una nube, y el sol hace brillar los techos de lámina de las tiendas.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, de un gris blanco que no da sombra.",
            "Hay nubes pesadas que no se deciden a llover.",
            "Está nublado, y el aire pesa, húmedo, como una toalla mojada.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y las calles se llenan de agua hasta los tobillos en diez minutos.",
            "Cae una lluvia tibia que la gente recibe sin abrir el paraguas, por gusto.",
            "Llueve fuerte, y los motociclistas se paran bajo los puentes a esperar.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: el cielo se pone morado, los rayos caen sobre la ciudad y se va la luz.",
            "Llega una tormenta de polvo y lluvia que dobla los árboles y tumba los anuncios.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, una niebla espesa y gris, mezclada con humo, que no deja ver la otra acera.",
            "La niebla es tan densa que los coches van con las luces de emergencia prendidas a mediodía.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento caliente de la llanura, que seca los labios y levanta las bolsas de plástico.",
            "Hay viento, y las cometas de los niños bailan sobre las azoteas.",
        ),
        Clima.POLVO to listOf(
            "Hay polvo en el aire, tanto que el sol es un círculo blanco que se puede mirar de frente.",
            "El polvo flota sobre la ciudad y se mete en los ojos, en la nariz y en el chai.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.VERANO to listOf(
            "Es verano, el de verdad, cuando a mediodía la calle se vacía y se oye zumbar a los aparatos de aire.",
            "En verano se cuelgan trapos mojados en las ventanas para que entre algo de fresco, y se espera el monzón.",
            "Es pleno verano, y en la ciudad se va la luz cada tarde, a la hora del calor.",
        ),
        Estacion.LLUVIAS to listOf(
            "Es el monzón, y todo está verde y mojado, hasta las paredes.",
            "En el monzón las calles se vuelven ríos, y los mosquitos, reyes.",
            "Es tiempo de lluvias, cuando la ropa no se seca nunca y huele a humedad.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, la temporada de las fiestas, y la ciudad entera se prepara para Diwali.",
            "En otoño las noches refrescan y el aire es suave, casi limpio.",
            "Es otoño, y en los mercados venden lámparas de barro y dulces por montones.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y en las mañanas hace un frío húmedo que se mete en los huesos.",
            "En invierno el humo de las fogatas y de los coches se queda pegado a la ciudad.",
            "Es invierno, y en las esquinas la gente se calienta las manos en fogatas de basura.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora está frente a la computadora del almacén, revisando en una pantalla los paquetes del día: miles de puntitos que se mueven por el mapa de la ciudad, cada uno con un número. Cuando uno se queda quieto mucho rato, ella llama al repartidor.", hacer = Hacer.TRABAJO),
        v("A esta hora contesta llamadas de clientes que no recibieron su paquete, y los clientes están siempre enojados. Ella les habla con la voz suave que le enseñaron en la capacitación, les da un número de reclamo y les dice que lo siente. Lo siente de verdad, a veces.", hacer = Hacer.TRABAJO),
        v("A esta hora pasa a la computadora las listas que le mandan por mensaje los encargados de turno, en fotos torcidas de papeles escritos a mano. Afuera, en el patio del almacén, las bandas transportadoras no paran: cajas, cajas, cajas.", hacer = Hacer.TRABAJO),
        v("A esta hora hay junta con el jefe, que habla en inglés y en hindi mezclados, con números en la pizarra: entregas a tiempo, devoluciones, quejas. Todo tiene que subir menos las quejas. Ella apunta en su cuaderno, aunque los números ya estén en el teléfono.", hacer = Hacer.TRABAJO),
        v("A esta hora se toma un chai en la máquina del pasillo con las otras dos mujeres de la oficina, que son de otras ciudades y viven en un cuarto compartido. Hablan de sueldos, de suegras y de una serie que ven las tres. Son diez minutos. Valen el día.", hacer = Hacer.TRABAJO),
        v("A esta hora arregla un problema que nadie más sabe arreglar: un paquete que el sistema dice que está en dos sitios a la vez. Llama, escribe, espera. Al final lo encuentra en un tercer sitio. Su jefe le dice que bien, en inglés. Ella lo apunta en su lista de cosas bien hechas.", hacer = Hacer.TRABAJO),

        v("A esta hora va en el rickshaw eléctrico compartido, apretada entre una señora con bolsas del mercado y un muchacho con audífonos, por calles llenas de motos, vacas, coches y carretas. El chofer toca el claxon cada cinco segundos, por si acaso.", hacer = Hacer.CAMINO),
        v("A esta hora espera el autobús en la parada, bajo un techito de lámina, mirando el teléfono como todos los que esperan. El autobús llega lleno. Se sube por la puerta de las mujeres, que también está llena, pero de mujeres.", hacer = Hacer.CAMINO),
        v("A esta hora cruza a pie la calle grande, que es una hazaña: no hay semáforo que se respete, y hay que meterse entre las motos con la mano levantada, despacio, sin correr, para que la vean. Lo ha hecho mil veces. No se acostumbra.", hacer = Hacer.CAMINO),

        v("A esta hora come lo que trajo en la lonchera de acero de tres pisos: arroz en uno, lentejas en otro, rotis y verdura en el tercero. Come en su escritorio, mirando videos de cocina en el teléfono, que es una manera rara de comer y la de todos.", hacer = Hacer.COMER),
        v("A esta hora come con la familia sentados en el suelo, sobre una estera, o en el sofá, frente a la televisión: rotis, dal, una verdura con papa y un poco de arroz. La tele pasa noticias que nadie oye. Anu come con las manos y se mancha hasta las orejas.", hacer = Hacer.COMER),
        v("A esta hora come en un puesto de la calle frente al almacén, con sus compañeras: samosas, chole bhature o un plato de arroz con curry por unas monedas, que paga con el teléfono escaneando un código pegado en el carrito. Hasta el vendedor de samosas cobra así.", hacer = Hacer.COMER),

        v("A esta hora barre y trapea los cuartos, recoge la ropa seca de la azotea o del balcón, deja las verduras lavadas para la cena. Lo hace con el teléfono en la oreja, hablando con su madre en la aldea, que le cuenta todo lo que pasó desde ayer, que es nada.", hacer = Hacer.CUIDAR),
        v("A esta hora hace las compras en la tienda de la esquina, un cuartito lleno hasta el techo de todo, donde el dueño la conoce y le apunta en una libreta lo que no alcanza. O lo pide en el teléfono, y llega en diez minutos en una moto. Lo segundo le da un poco de vergüenza.", hacer = Hacer.CUIDAR),
        v("A esta hora le enseña letras a Anu en un cuaderno, o se las repasa, mientras la niña dibuja en el margen. Las letras son en inglés y en hindi, y Anu ya sabe más inglés que su madre a su edad. Ella no se lo dice, pero le da orgullo y algo de tristeza.", hacer = Hacer.CUIDAR),

        v("A esta hora ve una serie en el teléfono, acostada, con los audífonos, mientras Ravi ve el críquet en la televisión. Cada uno en su pantalla, juntos. De vez en cuando él le grita un marcador y ella le enseña una escena. Así se ven las parejas de ahora, piensa.", hacer = Hacer.LIBRE),
        v("A esta hora hace una videollamada a la aldea, y su madre pone la cámara tan cerca que solo se le ve un ojo. Anu le enseña sus dibujos a la abuela por la pantalla. El abuelo pregunta lo mismo de siempre: si comen bien, si duermen bien, si les alcanza.", hacer = Hacer.LIBRE),
        v("A esta hora pasa videos cortos en el teléfono, uno tras otro, sin pensar: recetas, bailes, chistes, una boda, un gato, un político. Se ríe sola. Cuando levanta la vista ya pasó una hora, como cada noche, y se promete que mañana no.", hacer = Hacer.LIBRE),

        v("A esta hora duerme, con el ventilador del techo girando y haciendo ruido, que es el ruido con el que se duerme en esta ciudad. Si se va la luz, el ventilador se para y ella se despierta por el silencio, sudando, antes que por el calor.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, con el teléfono cargando junto a la cabeza y la luz verde parpadeando en la oscuridad. Afuera ladran los perros de la calle, que son de nadie y de todos. Una moto pasa tarde, y luego nada.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, cansada, en la misma cama con Ravi y con Anu en medio, que se mueve como un pez. En algún momento de la noche la niña acaba atravesada, con los pies en la cara de su padre. Nadie se despierta.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("Su barrio es una colonia de casas de ladrillo de tres y cuatro pisos, pegadas, cada una de un color, con tiendas abajo y familias arriba. Las calles son estrechas y tienen de todo: un templo, una mezquita pequeña, una escuela de inglés, un taller de motos y vacas echadas a la sombra."),
        v("La ciudad crece por todos lados: donde hace diez años había campos de trigo ahora hay colonias, centros comerciales y bodegas enormes como la suya, que reparten lo que la gente compra en el teléfono. Nadie sabe bien cuánta gente vive aquí. Siempre son más."),
        v("En la esquina hay un mercado de verduras que empieza al amanecer y no se acaba nunca, con montañas de cebollas, chiles, mangos o coliflores, según el mes. Los vendedores pregonan y las vacas se comen lo que se cae. Todo se paga con el teléfono, hasta un manojo de cilantro."),
        v("Por la calle grande pasa de todo a la vez: autobuses pintados, camiones con frases escritas detrás, motos con familias enteras encima, rickshaws, coches nuevos, una boda con banda de música y caballo blanco. El ruido no se apaga nunca. Lo extraño es el silencio."),
        v("Su ciudad tiene un río que es sagrado y está sucio, como casi todos, y en sus orillas hay escalinatas de piedra donde la gente se baña, reza y quema a sus muertos. Al lado, un centro comercial con escaleras eléctricas y un McDonald's. A nadie le parece raro."),
        v("En el barrio todos se conocen y todos se fijan: quién salió, a qué hora volvió, con quién habla. La señora de abajo sabe la vida de todo el edificio. Ella se queja, como todas. Pero cuando Anu se enfermó, fue la señora de abajo la primera que subió con un remedio."),
    )

    private val GENTE = listOf(
        v("Ravi, su marido, reparte paquetes en moto para una empresa de la competencia de la suya, y los dos se ríen de eso. Se casaron hace siete años en un matrimonio arreglado por sus familias, y él dice que fue la mejor decisión que no tomó. Ella dice que la tomó ella."),
        v("Anu, su hija, es una niña seria que hace preguntas difíciles: por qué las vacas no van a la escuela, por qué el teléfono sabe dónde estamos, por qué la abuela vive tan lejos. Ella le contesta lo que sabe, y cuando no sabe, le busca la respuesta en el teléfono, y Anu se queda mirando la pantalla."),
        v("Su madre, en la aldea, le llama dos veces al día, a la hora del desayuno y a la de la cena, para preguntarle lo mismo. Su padre, que fue maestro de primaria, le manda cada mañana un mensaje con una frase sabia y una flor dibujada. Ella nunca las lee y siempre contesta con un corazón."),
        v("Sonu, su hermano menor, estudia ingeniería en Delhi, en una escuela privada que pagan entre ella y sus padres, y habla de irse a trabajar a otro país. Le manda videos de sus amigos y le pide dinero a fin de mes. Ella se lo manda, y le dice que estudie."),
        v("Su jefe es un hombre joven de Bangalore que viste camisa de marca y llama a todos por el nombre en inglés. Es amable y está siempre cansado. Ella le tiene respeto y un poco de lástima, porque él también tiene un jefe, y su jefe tiene otro, y así hasta un país que nadie ha visto."),
        v("La señora de abajo, la viuda del dueño de la casa, sabe todo lo que pasa en el barrio y lo cuenta todo, con detalles que inventa. Le guarda las llaves, le cuida a Anu cuando hace falta y le dice cómo debería hacer cada cosa. Ella la escucha, y hace lo que quiere.", hasta = "mudanza"),
        v("Anu va al kínder en un autobús amarillo, con uniforme de cuadros, corbata y una mochila más grande que ella. Vuelve contando lo que dijo la maestra y lo que hizo un niño que se llama como un actor. Ella se ríe y le pregunta por las letras. Anu se las sabe todas.", desde = "anu_kinder"),
        v("Esperan otro hijo, y la noticia ya recorrió el grupo de la familia con cien mensajes de felicitación, consejos y bendiciones de tías que no conoce. Su madre le manda una lista de lo que debe comer. Ella sigue yendo a la oficina, más cansada, y no le hace caso a la lista.", desde = "esperan", hasta = "nace_kabir"),
        v("Kabir, el pequeño, duerme en una cuna de tela colgada del techo, que se mece con el ventilador, o en los brazos de quien esté más cerca. Anu lo cuida como una madre pequeña y le canta canciones de los dibujos animados. Ella los mira y piensa que ya está completa.", desde = "nace_kabir"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Ella lo sabe como se saben las cosas de la ciudad: sin haberlo aprendido nunca."),
        v("Hay cosas que solo se miran cuando faltan. {dato_objeto}"),
        v("{dato_objeto} En el barrio se dice que una casa se conoce por lo que tiene en la cocina, y una persona por lo que tiene en el teléfono."),
        v("{dato_objeto} Son cosas que se aprenden mirando a la madre, o en un video."),
        v("En su casa hay pocas cosas y todas sirven para algo, y muchas para dos. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("telefono", "el teléfono", "un teléfono", "El teléfono",
            "El teléfono es de una marca china, con la pantalla un poco rota en una esquina y una funda con una flor; ahí está todo: el banco, el trabajo, la familia, las fotos de Anu, las recetas y las películas, y si se pierde, se pierde la vida entera."),
        ObjetoCuento("audifonos", "los audífonos", "unos audífonos", "Los audífonos",
            "Los audífonos son inalámbricos, blancos, baratos, y se pierden cada dos meses; con ellos se oye música en el rickshaw, se contestan llamadas sin manos y se hace como que no se oye cuando alguien habla en la calle."),
        ObjetoCuento("moto", "la moto", "una moto", "La moto",
            "La moto de la casa es de ciento diez centímetros cúbicos y lleva lo que haga falta: a Ravi y sus paquetes, a la familia entera los domingos, un tanque de gas, un mueble; en su ciudad hay más motos que coches, y casi tantas como personas."),
        ObjetoCuento("chai", "el chai", "un chai", "El chai",
            "El chai se hace hirviendo juntos el té, la leche, el azúcar y las especias, hasta que sube; en la calle lo venden en vasitos de barro que se tiran después de usarlos, y en la oficina, en vasos de papel que también se tiran."),
        ObjetoCuento("tiffin", "la lonchera", "una lonchera", "La lonchera",
            "La lonchera es de acero, de tres pisos que se encajan uno sobre otro con una abrazadera; en cada piso va una cosa, para que no se mezclen, y en las oficinas de la ciudad, a mediodía, se oye el mismo ruido metálico de miles de loncheras abriéndose."),
        ObjetoCuento("cometa", "la cometa", "una cometa", "La cometa",
            "Las cometas de su ciudad son cuadradas, de papel de colores y varitas de bambú, y se vuelan desde las azoteas con un hilo cubierto de vidrio molido, para cortar el de las cometas de los vecinos; el que corta grita, y el cortado baja corriendo a buscarla."),
        ObjetoCuento("lampara", "la lamparita de barro", "una lamparita de barro", "La lamparita",
            "La lamparita de barro es un platito con un pico donde se pone aceite y una mecha de algodón; da una llama pequeña y quieta, y en Diwali se encienden cientos en las ventanas, en las escaleras y en los bordes de las azoteas."),
        ObjetoCuento("paraguas", "el paraguas", "un paraguas", "El paraguas",
            "El paraguas es negro, plegable, y sirve contra la lluvia en el monzón y contra el sol en verano; dura una temporada si hay suerte, y en la calle venden otros nuevos, siempre más caros cuando más llueve."),
        ObjetoCuento("ventilador", "el ventilador del techo", "un ventilador", "El ventilador",
            "El ventilador del techo tiene tres aspas blancas y un regulador de cinco velocidades en la pared; está encendido desde marzo hasta octubre, día y noche, y cuando se va la luz y se para, la casa entera se despierta."),
        ObjetoCuento("tarjeta", "la tarjeta de identidad", "una tarjeta de identidad", "La tarjeta",
            "La tarjeta de identidad tiene un número de doce cifras, su foto y sus huellas guardadas en una computadora del gobierno; con ella se abre una cuenta, se compra un teléfono y se cobra la ayuda del gobierno, y ella se sabe el número de memoria."),
        ObjetoCuento("mochila", "la mochila", "una mochila", "La mochila",
            "La mochila de la oficina es negra, con el nombre de una marca que no es la verdadera, y lleva siempre lo mismo: la lonchera, el cargador, una botella de agua, un paraguas, un pañuelo, una medicina para el dolor de cabeza y unas galletas, por si acaso."),
        ObjetoCuento("botella", "la botella de agua", "una botella de agua", "La botella",
            "La botella de agua es de plástico duro, de un litro, y se llena con el agua del filtro de la casa, porque la de la llave no se bebe; en verano se acaba antes de mediodía, y en la calle la venden en bolsitas de plástico, fría, a cualquier precio."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} Ella piensa en su ciudad como se piensa en el tiempo: no se la elige, se vive en ella. Es ruidosa, sucia, cara y llena de oportunidades, como dicen los anuncios. A sus {edad} años no se iría a otra, salvo a una más grande."),
        v("Piensa a veces que su madre se casó a los dieciséis, no fue a la escuela después de los doce y no ha salido de su distrito más que dos veces. Ella fue a la universidad, trabaja en una computadora y paga el teléfono de su madre. Entre las dos hay más de treinta años, y parecen trescientos."),
        v("{mundo} Ella piensa en cosas más cercanas: en el alquiler, en la escuela de Anu, que cuesta lo que cuesta, en el préstamo del teléfono. Las cosas grandes pasan en las noticias, y las noticias pasan muy rápido."),
        v("{mundo} A sus {edad} años, ella tiene una vida que cabe en un teléfono y una familia que no cabe en ninguna parte: está repartida entre la aldea, la ciudad y Delhi, y pronto, dice Sonu, en otro país. Se pregunta a veces dónde está su casa. Contesta que en el grupo de la familia."),
        v("Piensa en la gente con la que habla cada día por teléfono, clientes y repartidores, cientos, a los que nunca verá la cara. Le parece que el mundo está lleno de gente que se habla sin verse. Luego piensa que con su madre le pasa lo mismo, y le llama."),
        v("{mundo} A veces, en el rickshaw, mira a las niñas que van a la escuela con sus trenzas y sus mochilas, y piensa en Anu, y en qué será cuando sea grande: doctora, ingeniera, o alguien que trabaje en un país lejano. Ella no lo sabrá todo. Le basta con saber que podrá elegir."),
    )

    private val MUNDOS = mapOf(
        "kumbh" to listOf(
            "En el teléfono no se habla de otra cosa que del Kumbh Mela, al que van millones a bañarse al río sagrado.",
            "En la oficina se habla del críquet, de los precios y de la frontera, que nunca está tranquila.",
            "Se dice que el país es ya el más poblado del mundo, y la calle lo confirma cada mañana.",
        ),
        "censo" to listOf(
            "Se habla del censo, que va casa por casa y pregunta cosas que nadie sabe contestar bien.",
            "En la oficina se habla del Mundial de críquet, que se jugó en casa, y de nada más.",
        ),
        "kabir" to listOf(
            "Se habla del censo, que dicen que dirá por fin cuántos son.",
            "En el grupo de la familia se habla de los precios de la leche y de la boda de una prima.",
        ),
        "olimpiada" to listOf(
            "Se habla de las Olimpiadas de Estados Unidos, donde vuelve el críquet, y todos creen que la India ganará el oro.",
            "En la oficina se habla de las máquinas nuevas del almacén, que dicen que harán el trabajo de muchos.",
        ),
        "elecciones" to listOf(
            "Se habla de las elecciones, y en las calles hay carteles de los partidos en cada poste.",
            "Se dice que un asteroide va a pasar muy cerca de la Tierra, y en el teléfono hay mil videos que lo explican mal.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando sale del almacén ya es de noche, y la calle está iluminada por los anuncios, los focos de los puestos de comida y las luces de las motos, miles. Ella se sube al rickshaw con los audífonos puestos y cierra los ojos un momento. Es el único rato del día que es suyo."),
        v("Al caer la tarde la ciudad huele a fritura, a humo y a incienso, y en los templos suenan las campanas de la oración de la tarde. Ella pasa frente al de la esquina y junta las manos sin pararse. La diosa entiende las prisas.", hasta = "mudanza"),
        v("Se cena tarde, cuando todos llegan, y después se lavan los trastes, se recoge la cocina y se deja el agua del chai lista para mañana. Ella se sienta por fin en la cama con el teléfono, que es su manera de sentarse. Ravi ya ronca."),
        v("De noche, desde la ventana, se ven las luces de las casas de enfrente, cada una con su televisión encendida en el mismo canal, y se oye la misma serie desde todas las ventanas. Ella la oye y sabe en qué capítulo van, sin verla."),
        v("Anochece en la ciudad, y en el cielo no se ven estrellas: se ve un resplandor naranja, y a veces un avión. Ella se queda un momento en el balcón, o en la azotea, con un vaso de agua, y escucha a la ciudad, que no se calla nunca, y le gusta así."),
        v("La última luz del día se queda un momento en lo alto de los edificios nuevos, que tienen grúas encima, cuando abajo ya es de noche. Ella la mira desde el rickshaw, sin pensar en nada. Luego le llega un mensaje, y ya no la mira."),
        v("Al caer la tarde, en el edificio nuevo, se encienden a la vez cientos de ventanas iguales, y detrás de cada una hay una familia como la suya, cenando, discutiendo, mirando el teléfono. Ella las mira desde su balcón y piensa que nunca había vivido tan cerca de tanta gente que no conoce.", desde = "mudanza"),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "sankranti" to unDia(
            Parrafo.MANANA to "Hoy es Makar Sankranti, el día en que el sol entra en el signo de Capricornio y empieza a volver el calor. Por la mañana se come khichdi, arroz con lentejas, y dulces de ajonjolí con melaza, que se regalan a los vecinos. En la aldea, su madre se habrá bañado en el río antes del sol.",
            Parrafo.PUEBLO to "Todo el día el cielo de la ciudad está lleno de cometas de colores, cientos, miles, que suben desde las azoteas. Los niños y los hombres las vuelan con hilos cubiertos de vidrio y tratan de cortar las de los demás. Cuando una cae cortada, se oye el grito desde toda la calle.",
            Parrafo.ANOCHECER to "Al atardecer las últimas cometas bajan, y en algunas azoteas sueltan farolitos de papel con una vela dentro, que suben despacio, naranjas, hasta perderse. Ella los mira con Anu en brazos. La niña dice que son estrellas que se fueron a dormir.",
        ),
        "republica" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la República, el día en que hace muchos años empezó a regir la Constitución. No hay oficina. En la televisión pasan el desfile de Delhi: soldados con turbantes, camellos, tanques, carros alegóricos de cada estado y aviones que pintan el cielo con humo de colores.",
            Parrafo.PUEBLO to "En la escuela del barrio izan la bandera y los niños cantan el himno, formados, con uniformes planchados. En las calles venden banderitas de plástico y globos tricolores, y los coches llevan banderas en el espejo. Es un día de fiesta seria, de esas que se ven sentados.",
            Parrafo.GENTE to "Ravi ve el desfile entero en la televisión y le explica a Anu qué es cada cosa, inventando la mitad. Anu agita una banderita de papel cada vez que pasan los camellos. Ella hace los parathas de la fiesta y se sienta con ellos a ver el final, cuando pasan los aviones.",
        ),
        "holi" to unDia(
            Parrafo.MANANA to "Hoy es Holi, la fiesta de los colores, la de la primavera. Anoche se encendió en la esquina una hoguera grande, la de Holika, para quemar lo malo, y hoy, desde la mañana, todo el barrio sale a la calle con polvos de colores, pistolas de agua y globos llenos de agua pintada.",
            Parrafo.PUEBLO to "En la calle no se salva nadie: a quien pasa se le echa color en la cara, rojo, verde, amarillo, rosa, y se le dice «feliz Holi», y nadie puede enojarse. Hay música a todo volumen, baile, y dulces rellenos de coco que se reparten de casa en casa. Al mediodía la ciudad entera es de colores.",
            Parrafo.ANOCHECER to "Al atardecer se bañan, y el agua sale de todos los colores, y en la piel queda rosa durante días. Ella se ríe en el espejo de la cara de Anu, que tiene el pelo verde. Luego se ponen ropa limpia y visitan a los vecinos, con dulces, y los vecinos las visitan a ellas.",
        ),
        "calor" to unDia(
            Parrafo.MANANA to "Hoy llegó el calor de verdad, el seco, el que viene de la llanura con un viento que quema la cara a mediodía. En el teléfono la temperatura marca un número que da miedo. Ella moja una sábana y la cuelga en la ventana, como hacía su madre, para que el aire entre fresco.",
            Parrafo.PUEBLO to "A mediodía la calle se queda vacía: solo quedan los vendedores de agua de limón, de jugo de caña y de helados de colores, a la sombra de un toldo. Los repartidores en moto llevan la cabeza envuelta en un trapo. En los puestos venden enfriadores de aire, que se acaban.",
            Parrafo.ANOCHECER to "Esta noche se va la luz, como pasa cuando hace calor y todos prenden los aparatos a la vez. Sin ventilador, la casa es un horno. Suben todos a dormir a la azotea, o al balcón, con una estera, bajo el cielo, como se dormía antes. Hay quien lo prefiere.",
        ),
        "monzon" to unDia(
            Parrafo.MANANA to "Hoy llegó el monzón. Desde la mañana el cielo estaba negro, y a mediodía cayó el primer aguacero, enorme, de golpe, con un olor a tierra mojada que subió desde toda la ciudad. La gente salió a la calle a mojarse, con la cara levantada, como si fuera un regalo.",
            Parrafo.PUEBLO to "En media hora las calles se llenaron de agua hasta las rodillas, los coches se quedaron parados y los rickshaws avanzaron como barcos. Los niños se tiraron a los charcos. Los vendedores sacaron elotes asados con limón y chile, que es lo que se come cuando llueve.",
            Parrafo.ANOCHECER to "Esta noche, con la lluvia golpeando en el techo, se hace pakora en la cocina, verdura frita en harina de garbanzo, y chai, que es lo que se toma la primera noche del monzón. Ella abre la ventana para oler la lluvia. Mañana habrá mosquitos, pero hoy, no.",
        ),
        "raksha" to unDia(
            Parrafo.MANANA to "Hoy es Raksha Bandhan, el día de los hermanos. Las hermanas les atan a sus hermanos en la muñeca un hilo de colores, el rakhi, y ellos les prometen cuidarlas. Ella compró uno con una piedrita brillante en el mercado, el más bonito que encontró, aunque Sonu lo vaya a perder en una semana.",
            Parrafo.GENTE to "Sonu llega de Delhi en el autobús de la mañana, con su mochila y su teléfono nuevo, y ella le pone el punto rojo en la frente, le ata el rakhi y le mete un dulce en la boca. Él le regala dinero en un sobre, como se hace, y ella se lo devuelve a escondidas en la mochila.",
            Parrafo.PENSAMIENTO to "Ella mira a su hermano, que era un niño hace dos días y ahora estudia ingeniería y habla de irse lejos, y piensa en el hilo que le ató, que no lo va a cuidar de nada. Pero lo ató fuerte. Y él no se lo quita en toda la tarde, aunque los amigos se rían.",
        ),
        "independencia" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Independencia, y no hay oficina. Desde temprano, en la televisión, el primer ministro iza la bandera en el Fuerte Rojo de Delhi y da un discurso largo. En las azoteas de toda la ciudad ya hay niños con cometas, porque este día también se vuelan cometas.",
            Parrafo.PUEBLO to "En la escuela del barrio los niños cantan canciones patrióticas, y en todas las casas hay banderas en los balcones. Los altavoces de las calles ponen las mismas canciones viejas de películas, sobre la patria y los soldados. Los vendedores de banderitas hacen el negocio del año.",
            Parrafo.GENTE to "Anu va a la ceremonia de su escuela vestida de blanco, con una banderita en la mano y un poco de color tricolor en las mejillas. Canta el himno con los ojos cerrados, muy seria, y se equivoca en una palabra. Ella la graba en el teléfono y se lo manda a toda la familia.",
        ),
        "dussehra" to unDia(
            Parrafo.MANANA to "Hoy es Dussehra, el día en que el bien vence al mal, cuando el dios Rama mató a Ravana, el rey de diez cabezas. Durante nueve noches se representó la historia en un escenario del parque, con actores del barrio, y hoy se acaba con fuego.",
            Parrafo.PUEBLO to "Al atardecer, en el campo grande, hay tres muñecos gigantes, de varios pisos de alto, hechos de bambú y papel de colores y rellenos de cohetes: Ravana, su hermano y su hijo. Un actor vestido de Rama les dispara una flecha encendida, y los muñecos arden, estallando, entre los gritos de miles.",
            Parrafo.PENSAMIENTO to "Ella mira arder a Ravana con Anu en los hombros de Ravi, y piensa que cada año lo queman y cada año vuelve, más alto. Así es el mal, dice su padre, que fue maestro. Ella piensa que así es también el bien, que hay que volver a ganar cada año.",
        ),
        "diwali" to unDia(
            Parrafo.MANANA to "Hoy es Diwali, la fiesta de las luces, la más grande del año. Desde hace días la casa está limpia, porque la diosa Lakshmi solo entra en las casas limpias, y ayer se compró un utensilio nuevo de acero, como se hace. Hoy ella dibuja en la entrada un diseño de polvos de colores.",
            Parrafo.PUEBLO to "Al anochecer la ciudad entera se enciende: lamparitas de barro en cada ventana, en cada escalera, en el borde de cada azotea, y guirnaldas de foquitos en los edificios. Toda la noche truenan cohetes, tantos que el aire se pone blanco de humo y huele a pólvora hasta el día siguiente.",
            Parrafo.GENTE to "Se reza a Lakshmi delante de la repisa del rincón, con flores, dulces y monedas, toda la familia junta, y después se visita a los vecinos con cajas de dulces y se reciben las suyas. Anu enciende sus primeras bengalas con su padre, con miedo, y luego no quiere parar.",
            Parrafo.ANOCHECER to "Esta noche ella se queda despierta hasta tarde mirando las luces desde la ventana, con un dulce en la mano, mientras los cohetes no dejan dormir a nadie. Hace videollamada con la aldea, y su madre le enseña las lamparitas de su patio. Son las mismas, a cientos de kilómetros.",
        ),
        "chhath" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza Chhath, la fiesta del sol de su tierra, la de Bihar, que en la ciudad celebran los que vienen de allá. Son cuatro días de ayuno y de ofrendas. Hoy se bañan, limpian la casa y comen una sola comida pura, arroz, lentejas y calabaza, cocinada sin cebolla ni ajo.",
                Parrafo.PUEBLO to "En la orilla del río ya se están preparando los escalones, con cañas de azúcar y adornos, y por la ciudad suenan las canciones de Chhath, que son las mismas desde siempre, en la lengua de su aldea. Los que vienen de Bihar se reconocen por las canciones.",
                Parrafo.PENSAMIENTO to "Ella hace Chhath como lo hacía su madre, como lo hacía su abuela, aunque aquí no haya aldea ni campos. Piensa que algunas cosas se llevan dentro, como el acento, y que en la ciudad se vuelven más fuertes, no más débiles.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Hoy ayuna todo el día, sin agua, y al atardecer come un dulce de arroz con melaza, que es la primera comida en horas. Luego empieza otro ayuno, el largo, de un día y medio. Las vecinas de Bihar se juntan a cantar en su casa.",
                "Al atardecer va al río con la familia y miles de personas, con cestas de bambú llenas de frutas y dulces, y se mete en el agua hasta la cintura para ofrecerle al sol que se pone. El río está lleno de mujeres de colores, con el agua dorada.",
                "Antes del amanecer vuelve al río, entre miles, a esperar el sol que sale, de pie en el agua fría, con la ofrenda en las manos. Cuando el sol asoma, todos lo saludan a la vez. Luego se rompe el ayuno, y se reparte la ofrenda a todo el que pase.",
            )),
        ),
        "niebla" to unDia(
            Parrafo.MANANA to "Hoy amaneció la primera niebla gorda del invierno: no se veía la casa de enfrente. Por la ventana entra un aire húmedo, frío, que huele a humo de los campos quemados y de las fogatas. En el teléfono dicen que los trenes del norte van con horas de retraso, y los aviones no salen.",
            Parrafo.PUEBLO to "En la calle la gente camina despacio, con bufandas y gorros de lana, y las motos pasan con las luces prendidas, apareciendo y desapareciendo. En las esquinas, los vendedores de cacahuates tostados y de chai hacen fogatas pequeñas, y la gente se para a calentarse las manos.",
            Parrafo.ANOCHECER to "Esta noche se duerme con dos cobijas y Anu en medio, y aun así hace frío, porque las casas de aquí están hechas para el calor. Ella se queda despierta un rato oyendo los trenes, que pitan en la niebla para avisar que existen.",
        ),
        "kumbh" to unDia(
            Parrafo.MANANA to "Hoy empezó el Kumbh Mela en Prayagraj, donde se juntan los ríos sagrados, y en el teléfono no hay otra cosa: millones de personas bañándose en el agua al amanecer, sabios desnudos cubiertos de ceniza, tiendas de campaña hasta el horizonte. Se dice que es la reunión más grande del mundo.",
            Parrafo.GENTE to "Su madre llama para decirle que ella y su padre quieren ir, que es la oportunidad de su vida, que un Kumbh así solo pasa cada muchos años. Ella les dice que hay demasiada gente, que es peligroso, que son viejos. Su madre le dice que por eso.",
            Parrafo.PENSAMIENTO to "Ella mira en el teléfono a los millones en el agua y piensa que nunca ha estado en un lugar con tanta gente, aunque su ciudad esté llena. Piensa en sus padres, chiquitos entre la multitud. Y piensa que ella, en el fondo, también querría ir, aunque no lo diga.",
        ),
        "padres_kumbh" to unDia(
            Parrafo.MANANA to "Hoy sus padres volvieron del Kumbh, después de dos días en tren y uno a pie, y le hicieron una videollamada desde la casa: cansados, sucios y felices. Su madre se metió al río a la hora buena, la de antes del sol, y dice que sintió algo que no sabe explicar.",
            Parrafo.GENTE to "Le mandaron por paquetería una botella de agua del río sagrado, bien cerrada, con una cinta roja, que llegó a la oficina. Ella la pone en la repisa del rincón, junto a la diosa. Su madre dice que una gota en la boca de un enfermo lo cura. Ella no lo cree, y la guarda igual.",
            Parrafo.PENSAMIENTO to "Ella piensa en su madre de sesenta años metiéndose a un río helado entre millones de personas, por fe. Le da miedo y le da envidia. Ella no cree tanto en nada, piensa. Ni siquiera en el teléfono, que es lo que más usa.",
        ),
        "trofeo" to unDia(
            Parrafo.MANANA to "Hoy la India ganó la Champions Trophy de críquet, contra Nueva Zelanda, y desde que cayó el último punto la ciudad entera estalló: cohetes, cláxones, gritos en todas las azoteas. Ravi vio el partido entero en el teléfono, en el trabajo, sin que su jefe se enterara.",
            Parrafo.PUEBLO to "En la calle grande hay muchachos en motos con banderas, tocando el claxon sin parar, y gente bailando delante de las tiendas con música de los altavoces. Los vendedores de dulces regalan uno a cada quien. Hoy en su ciudad todos son del mismo equipo, y eso no pasa nunca.",
            Parrafo.GENTE to "Ravi llega a casa con la cara pintada de tricolor y una caja de dulces, y levanta a Anu en el aire gritando el nombre de un jugador. Anu no sabe quién es, pero grita también. Ella se ríe, les toma una foto y la manda al grupo de la familia, donde ya hay cincuenta mensajes.",
        ),
        "anu_kinder" to unDia(
            Parrafo.MANANA to "Hoy Anu empezó el kínder, en una escuela privada de inglés que cuesta un tercio del sueldo de ella, con uniforme de cuadros, corbata, zapatos negros y una mochila de dibujos animados. La niña estaba lista desde las seis, sentada en la cama, con la mochila puesta.",
            Parrafo.GENTE to "En la puerta de la escuela había cien madres tomando fotos, y ella también tomó cincuenta. Anu entró sin llorar y sin voltear, de la mano de una maestra joven. Ella se quedó un rato mirando la puerta cerrada, con el teléfono en la mano, como si fuera a abrirse.",
            Parrafo.PENSAMIENTO to "Ella piensa que su hija va a aprender inglés desde niña, como ella no pudo, y que eso le va a abrir puertas que ella ni ve. Piensa también en lo que cuesta. Hace cuentas en el rickshaw, y no le salen. Las hace otra vez, y le salen peor. Pero Anu entró sin llorar.",
        ),
        "sindoor" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy, de madrugada, el ejército lanzó ataques contra Pakistán, y desde que ella abrió los ojos el teléfono no para: videos, mapas, explosiones, noticias ciertas y noticias falsas, todas a la vez. En el grupo de la familia su padre manda mensajes patrióticos, uno tras otro.",
                Parrafo.PUEBLO to "En la calle la gente habla de la guerra en las esquinas, en el mercado, en el rickshaw, y todos saben más que el que está al lado. En las teles de las tiendas los presentadores gritan. Algunos tiran cohetes en la noche, como si fuera un partido. A ella no le gusta.",
                Parrafo.PENSAMIENTO to "Ella piensa en la frontera, que está lejos y de repente parece cerca, y en los que viven allí, de los dos lados, que no eligieron nada. Piensa en Anu. Esta noche apaga el teléfono antes de dormir, por primera vez en mucho tiempo, para no ver más.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Hoy sigue la guerra en el teléfono, con más videos y más gritos en la televisión. En la oficina nadie trabaja bien: todos miran las noticias a escondidas, bajo el escritorio. Se dice que hay drones, aviones, misiles.",
                "Hoy se habla de que la guerra puede crecer, y en el grupo de la familia su madre pregunta si deberían comprar arroz de más. Ella le dice que no, que no pasa nada. No sabe si pasa algo.",
                "Hoy anuncian que se acabó, que hay un alto el fuego, y la ciudad respira. En la calle la gente vuelve a hablar de precios y de críquet. En el teléfono, los mismos de ayer celebran que ganamos. Ella no sabe bien quién ganó, y está contenta de que se haya acabado.",
            )),
        ),
        "ahmedabad" to unDia(
            Parrafo.MANANA to "Hoy, al mediodía, se cayó un avión en Ahmedabad, segundos después de despegar, sobre unos edificios de estudiantes de medicina. En el teléfono aparecen los videos casi enseguida: el humo negro, la columna de fuego. Iba lleno, rumbo a Londres. Dicen que solo sobrevivió uno.",
            Parrafo.TRABAJO to "A esta hora, en la oficina, nadie trabaja: todos miran el mismo video, una y otra vez, en silencio, sin poder dejar de mirar. Una compañera tiene un primo que vive en Ahmedabad y le llama, y el primo está bien. Ella siente un alivio que no es suyo.",
            Parrafo.PENSAMIENTO to "Ella nunca se ha subido a un avión, y piensa que ahora no se subirá en mucho tiempo. Piensa en los que iban a Londres, a ver a sus hijos, a estudiar, a trabajar, con sus maletas llenas de dulces y de especias. Esta noche le escribe a Sonu que lo quiere, sin razón.",
        ),
        "aumento" to unDia(
            Parrafo.MANANA to "Hoy su jefe la llamó a su oficina de vidrio, le habló de números durante diez minutos en inglés y al final le dijo que le suben el sueldo, un poco, porque es de las que más resuelven. Ella dijo «gracias, señor» en inglés, y salió con la cara caliente.",
            Parrafo.GENTE to "En la noche se lo cuenta a Ravi, que la abraza y dice que hay que celebrar, y piden pizza por el teléfono, que es lo que hacen en las películas. Anu nunca había comido pizza y dice que no le gusta, y se come tres pedazos. Ella llama a su madre, que llora.",
            Parrafo.PENSAMIENTO to "Ella hace cuentas con el sueldo nuevo: la escuela de Anu, el préstamo, lo de Sonu, algo para la aldea, y a lo mejor, algún día, un departamento con ascensor. Las cuentas siguen sin salir, pero salen menos mal. Ya gana más de lo que ganó su padre en toda su vida.",
        ),
        "telefono" to unDia(
            Parrafo.MANANA to "Hoy se compró un teléfono nuevo, en abonos de doce meses, en una tienda del centro comercial con luces blancas y vendedores de camisa azul. El viejo tenía la pantalla rota en tres partes y la batería no duraba ni la mañana. El nuevo brilla tanto que da miedo sacarlo en la calle.",
            Parrafo.GENTE to "Ravi le pasa todo del teléfono viejo al nuevo: fotos, mensajes, contactos, las aplicaciones del banco. Tarda toda la tarde. Anu quiere el teléfono viejo para jugar, y se lo dan, sin chip, y la niña hace como que habla con la abuela, con la misma voz que su madre.",
            Parrafo.PENSAMIENTO to "Ella piensa que lo que más le dolía de perder el teléfono viejo eran las fotos, cuatro años de Anu, y que ahora están en el nuevo y también en una nube que no sabe dónde está. Le parece raro que su vida esté en una nube. Luego toma una foto de Anu con el teléfono viejo, y la guarda.",
        ),
        "mundial_femenil" to unDia(
            Parrafo.MANANA to "Hoy la India ganó el Mundial femenino de críquet, por primera vez en la historia, en un estadio lleno, contra Sudáfrica. Ella vio el final en el teléfono, en la cocina, con el corazón en la boca, y cuando cayó el último punto gritó tan fuerte que la señora de abajo subió a ver qué pasaba.",
            Parrafo.PUEBLO to "En la calle hay cohetes, como cuando ganan los hombres, aunque menos, y en el teléfono las jugadoras lloran abrazadas con la copa, y las niñas del barrio salen a jugar críquet en la calle con un bat de madera y una pelota de tenis. Las mamás las miran desde las ventanas.",
            Parrafo.GENTE to "Anu le pregunta si las niñas también pueden jugar críquet, y ella le enseña en el teléfono a la capitana levantando la copa. Anu se queda mirándola. Al rato agarra el bat de plástico de su padre y sale al pasillo a pegarle a una pelota. Ravi le lanza. Ella los graba.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "mundo2025", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + ALDEA.take(1),
            Parrafo.TIEMPO to TIEMPO + v("En la aldea el cielo se ve entero, de un lado al otro de los arrozales, sin edificios ni cables, y las nubes del monzón pasan bajas y oscuras. Su padre las mira y dice cuándo va a llover, y acierta más que el teléfono.", estado = "aldea"),
            Parrafo.TRABAJO to TRABAJO + ALDEA.subList(1, 7),
            Parrafo.PUEBLO to PUEBLO + ALDEA[7],
            Parrafo.GENTE to GENTE + ALDEA[8],
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + ALDEA[9],
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS,
        especiales = ESPECIALES,
    )
}
