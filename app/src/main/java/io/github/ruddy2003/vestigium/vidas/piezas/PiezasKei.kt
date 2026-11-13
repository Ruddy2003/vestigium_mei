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
 * El cuento de Kei, repartidora en bicicleta en una megaciudad de neón. Es ficción. Sigue la gramática y el tono de
 * `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026; el «dato de su época» de cada párrafo es aquí un detalle de
 * su ciudad, dicho como lo diría alguien que vive en ella.
 *
 * Su día va al revés: duerme de 6 a 12, así que «la mañana» es el mediodía y «el anochecer» es cuando vuelve a casa,
 * al amanecer.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioPiloto.kt`) y del de Amaru:
 * - Cena por primera vez en el puesto de fideos del callejón el 12 de diciembre de la temporada 1 (el mismo día en que
 *   Amaru le sirve el doble caldo). Antes, el puesto no es suyo; hasta el 5 de diciembre de la temporada 3 no sabe
 *   cómo se llama él: es «el de los fideos».
 * - Río deja los repartos en la temporada 2; Dalia cierra el taller en la 4; la novata llega en la 3; paga la última
 *   cuota del implante en la 2; en la 5 ve que fuera de la ciudad hay luces.
 * - Está «sin_bici» del choque al día en que vuelve a rodar (temporada 2) y «sin_implante» los tres días en que se le
 *   apaga (temporada 3): sus variantes del trabajo, de la mañana y del barrio son otras.
 * - Las fiestas que vuelven cada año no nombran a Dalia, a Río, a la novata ni a Amaru, ni el puesto de fideos como
 *   suyo (salvo la última noche del año, que cae siempre después de conocerlo).
 */
internal object PiezasKei {
    private const val SIN_BICI = "sin_bici"
    private const val SIN_IMPLANTE = "sin_implante"

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta a mediodía, cuando la luz de la torre de enfrente le da en la cara a través de la cortina. La cortina es negra y gruesa, y no sirve de nada. El implante se enciende antes que ella: le pone en el ojo la hora, el tiempo y tres anuncios de café."),
        v("Lo primero que oye es el tren colgante que pasa a la altura de su ventana, cada cuatro minutos, desde que tiene memoria. Ya no la despierta; la despertaría el silencio, si un día faltara. Se levanta, se moja la cara y se toma de pie una lata de lo que haya en la nevera."),
        v("Su cuarto es tan pequeño que se viste sentada en la cama, sin levantarse. Por la ventana se ven otras ventanas, cientos, y en cada una alguien que se acaba de despertar o que todavía no se ha dormido. A esa distancia nadie saluda, pero todos se ven."),
        v("Se despierta con la boca seca y el cuerpo todavía en la bici: le duelen los muslos y tiene la marca del casco en la frente. El implante le recuerda que tiene cuarenta mensajes. Los deja para después, como todos los días, y el después nunca llega."),
        v("Antes de salir le echa un vistazo a la bici, que duerme en el pasillo, enchufada a la pared como un animal que bebe. Comprueba la batería, las ruedas y el candado, en ese orden. Si algo falla, falla la noche, y la aplicación no pregunta por qué."),
        v("En la escalera del edificio huele a fritura de veinte cocinas distintas, y por la puerta de cada piso sale una música. Baja los once pisos a pie, porque el ascensor es para los que pagan la cuota. A mitad de camino ya va despierta del todo."),
        v("{nombre} abre los ojos y el mundo ya está escrito: flechas en el suelo, precios en las paredes, el nombre del vecino flotando sobre su cabeza. El implante no sabe que acaba de despertarse y le ofrece un descuento en desayunos. Ella lo cierra con un parpadeo."),

        v("Se despierta y lo primero que mira es el pasillo, donde no está la bici: está en el taller de Dalia, torcida como una pregunta. Sin bici no hay pedidos, y sin pedidos no hay nada. Se queda un rato en la cama, mirando el techo, haciendo cuentas que no le salen.", SIN_BICI),
        v("Baja la escalera sin la bici al hombro, y le sobran las manos. En el pasillo queda el enchufe vacío, con su cable colgando. El implante le pregunta si quiere pedir un transporte; ella le contesta que no tiene con qué, y el implante le ofrece un préstamo.", SIN_BICI),
        v("Abre los ojos y el mundo está desnudo: sin flechas, sin precios, sin nombres. La pared de enfrente es solo una pared, con manchas. {nombre} se queda un rato quieta, mirándola, porque nunca la había visto así.", SIN_IMPLANTE),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En los niveles bajos el tiempo llega tarde y de rebote: primero se oye en los toldos y luego se nota en el suelo."),
        v("{nombre} mira el cielo como lo mira una repartidora, pensando en los frenos. {cielo} El implante le pone en el ojo la probabilidad de lluvia, que se equivoca menos que ella y más de lo que dice."),
        v("{de_estacion} {cielo} Los viejos del barrio dicen que antes llovía agua y que ahora llueve otra cosa. Lo dicen sin quitarse la mascarilla."),
        v("{cielo} Dicen que en temporada de {estacion} siempre es así, y que el año que viene será peor. {nombre} no les lleva la contraria: en esta ciudad, decir que algo será peor casi nunca es equivocarse."),
        v("Lo primero que hace al salir es mirar hacia arriba, entre las torres, por donde asoma una tira de cielo del ancho de una calle. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En esta ciudad el tiempo solo es noticia cuando se apagan los letreros. Lo demás se aguanta con un impermeable."),

        v("{cielo} {de_estacion} Sin el implante no sabe qué tiempo hará dentro de una hora, así que mira hacia arriba, como hacía su abuela.", SIN_IMPLANTE),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "Entre las torres se ve el cielo limpio y negro, sin estrellas: los letreros no las dejan.",
            "Hace una noche clara, y desde los puentes se ven las luces de la bahía.",
            "No hay nubes, y la luna asoma entre dos torres, chica y amarilla.",
        ),
        Clima.NUBES to listOf(
            "El cielo está bajo, y las nubes se encienden de rosa y de verde.",
            "Hay nubes tan bajas que las torres altas desaparecen a media altura.",
            "Está nublado, y los anuncios rebotan en las nubes y se ven dos veces.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia tibia que huele a metal.",
            "Cae agua del cielo, de los balcones, de los cables y de los aires acondicionados.",
            "Llueve fino, y en los charcos la ciudad tiene otra ciudad debajo.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, y los letreros parpadean con cada rayo.",
            "El cielo se ha roto, y el agua baja por las escaleras de los niveles como un río.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, y los letreros flotan en ella sin pared.",
            "La niebla baja a los niveles y se queda, tibia, oliendo a fritura.",
        ),
        Clima.POLVO to listOf(
            "Sopla viento del desierto y trae polvo, y el aire raspa.",
            "Hay polvo, y la luz de los anuncios se ve turbia, como en un vaso sucio.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.LLUVIAS to listOf(
            "Es la temporada de lluvias, cuando el agua sube por los desagües.",
            "En lluvias se doblan los pedidos, y las multas también.",
            "Es temporada de lluvias, y abajo se camina por pasarelas de rejilla.",
        ),
        Estacion.SECAS to listOf(
            "Es la temporada seca, la del polvo.",
            "En secas el aire de abajo se queda quieto y huele a lo que se frió ayer.",
            "Es temporada seca, y de noche el calor sube del suelo.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora reparte. La aplicación le canta el pedido en el oído y le dibuja la ruta en el suelo, una flecha azul que corre delante de la rueda. Fideos al piso cuarenta, medicinas al ocho, un pastel de cumpleaños al sótano de un casino. No pregunta para quién es nada.", hacer = Hacer.TRABAJO),
        v("A esta hora pedalea por los puentes de los niveles medios, entre coches que no ven a nadie y peatones que ven todavía menos. Lleva la caja térmica a la espalda y el reloj de la aplicación en el ojo, contando hacia atrás. Si llega a cero, multa.", hacer = Hacer.TRABAJO),
        v("A esta hora espera en la puerta de un restaurante con otros veinte repartidores, todos mirando la misma puerta. Nadie habla: cada uno escucha su propia música. Cuando sale una bolsa, veinte cabezas se levantan y diecinueve vuelven a bajar.", hacer = Hacer.TRABAJO),
        v("A esta hora sube en un ascensor de servicio, con la caja en las rodillas y el casco puesto. El ascensor tarda tres minutos en llegar al piso noventa, y en esos tres minutos el implante le pasa seis anuncios. Ella los ve todos, porque no hay adónde mirar.", hacer = Hacer.TRABAJO),
        v("A esta hora reparte en los niveles bajos, donde el mapa del implante dibuja calles que ya no existen y no dibuja las que sí. Ella se las sabe de memoria: los atajos, los puentes de tablones, las escaleras que no salen en el plano. Por eso le dan los pedidos de abajo.", hacer = Hacer.TRABAJO),

        v("A esta hora no reparte: la bici está en el taller y la aplicación la ha puesto «en pausa», que suena mejor que «sin cobrar». Ayuda a Dalia a enderezar ruedas a cambio de que le cobre menos. Dalia dice que nunca había tenido una ayudante tan mala.", SIN_BICI, Hacer.TRABAJO),
        v("A esta hora reparte a pie lo que puede: paquetes chicos, cerca, en los niveles bajos, donde las escaleras van más rápido que cualquier rueda. Le pagan la mitad. La aplicación le avisa cada media hora de que va lenta, como si ella no lo supiera.", SIN_BICI, Hacer.TRABAJO),
        v("A esta hora está en el taller de Dalia, mirando cómo suelda el cuadro de la bici, chispa a chispa, con la máscara puesta. Dalia trabaja sin hablar. Cuando termina un tramo, pasa el dedo por la soldadura como quien acaricia un perro.", SIN_BICI, Hacer.TRABAJO),

        v("A esta hora intenta repartir sin flechas, por la ciudad desnuda. Sin el mapa del ojo las calles son solo calles, y las torres se parecen todas. Se pierde dos veces en un barrio que creía conocer. A la tercera pregunta a un viejo, que se ríe y le señala el camino con la mano.", SIN_IMPLANTE, Hacer.TRABAJO),
        v("A esta hora no reparte: la aplicación no la encuentra, porque el implante no responde, y para la aplicación lo que no responde no existe. Se sienta en un banco a mirar la ciudad sin anuncios. Hay más gente de la que pensaba, y casi toda va con la cabeza agachada.", SIN_IMPLANTE, Hacer.TRABAJO),

        v("A esta hora come fideos sintéticos de un vaso, con agua caliente del grifo, que sale casi hirviendo si se la deja correr. Saben a lo que diga el sobre: hoy, a pollo. Nunca ha comido pollo, así que no puede quejarse.", hacer = Hacer.COMER),
        v("A esta hora come una barra de proteína de tanque, de pie, apoyada en la bici, sin quitarse los guantes. La barra sabe a vainilla y a nada, en ese orden. La empuja con una bebida energética que la tendrá despierta hasta que arriba amanezca.", hacer = Hacer.COMER),
        v("A esta hora come en un puesto del mercado de abajo, donde una señora vende bollos al vapor rellenos de algo que se llama carne. {nombre} no pregunta qué carne. La señora le cobra de menos porque le recuerda a una sobrina, y ella no la saca del error.", hacer = Hacer.COMER),

        v("A esta hora tiene tiempo para ella, que en su caso es tiempo de pantallas. Ve tres series a la vez, una en cada esquina del ojo, y no termina ninguna. A veces se queda dormida con una puesta, y sueña con el final que no vio.", hacer = Hacer.LIBRE),
        v("A esta hora está tirada en la cama, con el implante en modo música, mirando el techo. El techo tiene una mancha de humedad con forma de pez, que cambia un poco cada temporada de lluvias. Le ha puesto nombre, y no se lo ha dicho a nadie.", hacer = Hacer.LIBRE),
        v("A esta hora juega a un juego del implante en el que se construye una casa con jardín, con árboles de verdad y una puerta que da afuera. Lleva dos años construyéndola. Nunca ha visto un jardín, pero sabe exactamente cómo es el suyo.", hacer = Hacer.LIBRE),
        v("A esta hora sube a la azotea del edificio, que es lo más cerca del cielo que tiene, a ver pasar los drones como peces. Hay un banco roto, dos macetas con algo que fue una planta y un viejo que fuma y no le habla. A los dos les parece bien.", hacer = Hacer.LIBRE),
        v("A esta hora revisa las cuentas en el ojo: lo que cobró anoche, lo que le descontaron, lo que todavía debe del implante. Los números flotan delante de la pared, en verde los buenos y en rojo los demás. Casi todos son de los demás.", hacer = Hacer.LIBRE, hasta = "ultima_cuota"),

        v("A esta hora está en el bar del barrio con los repartidores que terminan turno, todos con la marca del casco en la frente. Se habla de multas, de clientes y de atajos, y se brinda por los que no llegaron a tiempo. El bar se llama de una manera y todos le dicen de otra.", hacer = Hacer.GENTE),
        v("A esta hora mira el amanecer que no se ve: arriba, en las torres, se encienden de rosa los cristales más altos, y abajo sigue siendo de noche. Sentada en la puerta del bar con otros dos, se termina la lata y espera a tener sueño, que tarda.", hacer = Hacer.GENTE),
        v("A esta hora juega a las cartas en el bar con unos repartidores viejos, que hacen trampas a la vista de todos para que nadie pueda decir que las hacen a escondidas. Se apuestan créditos de la aplicación, que valen poco y se pierden rápido.", hacer = Hacer.GENTE),
        v("A esta hora comparte una cerveza con Río, que reparte en la misma zona y conoce los mismos ascensores rotos. Hablan poco: con lo que se han visto pedalear, ya está todo dicho. Él le cuenta un chiste malo cada noche, y ella se ríe de lo malo que es.", hacer = Hacer.GENTE, hasta = "rio_se_va"),
        v("A esta hora está en el puesto de fideos del callejón, el del letrero del tazón, con los últimos repartidores de la noche. El cocinero pone el caldo y la radio, y no habla mucho. A ella le gusta eso: en toda la noche, es el único que no le vende nada.", hacer = Hacer.GENTE, desde = "primeros_fideos"),

        v("A esta hora duerme, con la cortina negra cerrada y el implante en modo noche, que solo quiere decir que los anuncios salen más bajitos. Duerme de lado, abrazada a la almohada, como si fuera en la bici.", hacer = Hacer.DORMIR),
        v("A esta hora duerme mientras la ciudad de arriba trabaja. Por la pared le llegan la tele del vecino, el tren colgante y el zumbido del edificio, que no para nunca. Ya no los oye: son parte del silencio.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y si sueña, sueña con flechas: dicen que a los repartidores el implante les sigue pintando rutas dormidos. Ella no se acuerda nunca de adónde la llevan, solo de que llegaba tarde.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La ciudad tiene tantos pisos hacia arriba como hacia abajo. Arriba están las torres de cien plantas, con jardines en las azoteas. Abajo están los niveles, uno debajo de otro, donde el sol no llega y los letreros hacen de sol. {entorno}"),
        v("{entorno} En los puentes de los niveles medios se venden implantes de segunda mano, baterías y comida que imita a otra comida. Todo cabe en una mesa plegable, y la mesa en una mochila, por si llega la seguridad."),
        v("Las torres de Áurea se ven desde cualquier parte, con el logo dorado dando vueltas en la punta. Dicen que dentro hay lagos, y que la gente de dentro no sale nunca, porque no le hace falta. {entorno}"),
        v("{entorno} En los niveles bajos los callejones tienen nombre de número, y los números no van en orden. Hay escaleras que bajan a otras escaleras, puertas que dan a paredes y tiendas que abren a las tres de la mañana y cierran a las cuatro."),
        v("Por encima de las calles pasan cables, tantos que de noche parecen una red de pescar puesta del revés. De los cables cuelgan zapatos, letreros y, a veces, un dron enredado que nadie baja. {entorno}"),
        v("El tren colgante cruza la ciudad de punta a punta en una hora, colgado de su riel como una lámpara. Desde abajo se ven pasar las caras de los que van dentro, cada una iluminada por su pantalla. {entorno}"),

        v("Sin el implante, la ciudad tiene otro ruido. Se oyen los pasos, el agua de los desagües, una mujer que canta en un balcón. Los letreros siguen ahí, pero son de luz y nada más: no le hablan a nadie.", SIN_IMPLANTE),
    )

    /** Lo que se ve alrededor según cómo va la ciudad. Manda la primera regla que se cumple; la última vale siempre. */
    private val ENTORNOS = listOf(
        Entorno("obra", 0.5, frases = listOf(
            "En la boca del callejón de los fideos siguen las máquinas, con pintadas hasta en las ruedas.",
            "Las máquinas de Áurea esperan en el nivel −3, con ropa tendida en los brazos.",
        )),
        Entorno("agua", 0.6, frases = listOf(
            "Abajo el agua llega a los tobillos, y se cruza con la bici al hombro.",
            "Abajo el agua no se va, y las ruedas levantan dos alas sucias.",
        )),
        Entorno("luces", 0.5, frases = listOf(
            "La ciudad está llena de luces de fin de año.",
            "Hay adornos de luz entre los edificios, y los drones dibujan estrellas.",
        )),
        Entorno("pintas", 0.5, frases = listOf(
            "Las paredes están llenas de pintadas contra Áurea, y ya nadie las borra.",
            "En cada esquina hay carteles pegados unos sobre otros: asamblea, se busca, no pasarán.",
        )),
        Entorno("obra", 0.0, 0.5, frases = listOf(
            "Arriba pasa un dron de Áurea con su luz dorada, y nadie levanta la vista.",
            "Los letreros cambian de idioma cada tanto, por si acaso.",
        )),
    )

    private val GENTE = listOf(
        v("Tiene veinte mil contactos en el implante y cuatro amigos, y a dos de los cuatro no los ha visto nunca en persona. Con uno juega cada noche una partida de algo; con el otro solo se mandan fotos de la luna, cuando se ve."),
        v("Su madre vive al otro lado de la ciudad, a dos horas de tren colgante, y la llama cada domingo para preguntarle si come. {nombre} le dice que sí, que es verdad a medias. Su madre le dice que está más flaca, que es verdad entera, aunque solo la vea en el ojo."),
        v("Los vecinos de la puerta de al lado son una familia de seis en un cuarto como el suyo. Los niños la esperan en la escalera cuando vuelve, para que les deje tocar el casco. {nombre} les cuenta hasta qué piso ha subido esa noche, y no le creen nada."),
        v("Los otros repartidores de su zona no saben su nombre, pero conocen su bici, que es azul, y su manera de tomar las curvas. Le dicen «la azul». Ella tampoco sabe cómo se llaman ellos: los conoce por la caja que llevan y por cómo frenan."),
        v("El dueño del cuarto donde vive no tiene cara: es una voz en el implante que cobra el día uno y contesta otro día. Lleva tres años en el cuarto y nunca lo ha visto. A veces se pregunta si existe, y entonces llega el recibo."),
        v("Dalia le arregla la bici desde que {nombre} llegó a la ciudad, en un taller del tamaño de un armario que huele a aceite y a soldadura. Le cobra lo que le parece, que nunca es lo mismo. Dice que una bici se parece a su dueña, y que la suya anda torcida de un lado.", hasta = "dalia_se_muda"),
        v("Río reparte en la misma zona, y cuando se cruzan en un semáforo se saludan con dos dedos en el casco. Es lo más parecido a un amigo que tiene en la calle. Una vez le prestó una batería en mitad de una tormenta, y ella todavía no se la ha devuelto.", hasta = "rio_se_va"),
        v("El de los fideos del callejón le pone el caldo antes de que se siente y no le pregunta nada. Tiene las manos quemadas de años y una radio vieja que solo pone canciones de antes. {nombre} no sabe cómo se llama, él no sabe cómo se llama ella, y así está bien.", desde = "primeros_fideos", hasta = "nombre_amaru"),
        v("Amaru, el de los fideos, ya la llama por su nombre, y ella a él. No ha cambiado nada más: el mismo banco, el mismo doble caldo, la misma radio. Pero ahora, cuando ella llega, él dice «Kei» sin levantar la vista, y suena distinto.", desde = "nombre_amaru"),
        v("La novata es una chica de los barrios de fuera que todavía frena con los pies. {nombre} le enseña las rutas de abajo, las que el mapa no tiene. La chica lo apunta todo en una libreta de papel, como si fuera de otro tiempo, y no se le olvida nada.", desde = "novata"),
    )

    private val OBJETO = listOf(
        v("Hoy se fija, sin querer, en algo que lleva siempre encima. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del oficio: a fuerza de que fallen."),
        v("Hay cosas que solo se notan cuando se rompen, y en esta ciudad se rompe todo. {dato_objeto}"),
        v("{dato_objeto} Es poca cosa, pero una repartidora lleva su vida en una caja y una mochila, y conoce cada cosa por su peso."),
        v("{dato_objeto} Si alguien le preguntara por qué, ella diría que así es la calle, y la calle no da explicaciones."),
        v("{dato_objeto} El implante le ofrece uno nuevo cada vez que lo mira, y ella cada vez le dice que no con un parpadeo."),

        v("{dato_objeto} Sin el implante encima, lo mira como si lo viera por primera vez.", SIN_IMPLANTE),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("casco", "el casco", "un casco", "El casco",
            "El casco es blanco, o lo fue: ahora tiene rayas de todas las caídas y una pegatina de un gato que ya casi no se ve; por dentro huele a ella, y por fuera, a la lluvia de la ciudad."),
        ObjetoCuento("bateria", "la batería", "una batería", "La batería",
            "La batería de la bici va debajo del asiento y dura una noche entera si no hace frío y no hay cuestas; cuando se acaba, la bici pesa como una nevera y hay que empujarla hasta el enchufe más cercano."),
        ObjetoCuento("candado", "el candado", "un candado", "El candado",
            "El candado es de cadena gruesa y pesa más que la mitad de lo que reparte; en los niveles bajos una bici sin candado dura lo que tarda una en subir un pedido, y a veces menos."),
        ObjetoCuento("caja", "la caja térmica", "una caja térmica", "La caja térmica",
            "La caja térmica va a la espalda y mantiene caliente lo caliente y frío lo frío durante una hora; por dentro tiene manchas de mil comidas distintas, y por fuera, un logo medio arrancado."),
        ObjetoCuento("guantes", "los guantes", "unos guantes", "Los guantes",
            "Los guantes son de goma por fuera y de algo que fue lana por dentro, y tienen las puntas cortadas para poder tocar las pantallas; en lluvias no se secan nunca y huelen a perro mojado."),
        ObjetoCuento("impermeable", "el impermeable", "un impermeable", "El impermeable",
            "El impermeable es amarillo, del amarillo que se ve en la niebla, y tiene un agujero en el codo tapado con cinta; dicen que el amarillo hace frenar a los coches, y algunos frenan."),
        ObjetoCuento("lata", "la lata", "una lata de bebida energética", "La lata",
            "La lata de bebida energética es verde, cabe en el portabotellas de la bici y tiene tanta cafeína que el implante la avisa cada vez que se toma una; ella le contesta tomándose otra."),
        ObjetoCuento("linterna", "la linterna del casco", "una linterna", "La linterna",
            "La linterna del casco es para los niveles donde se han apagado los letreros, que son más de los que dice el mapa; da una luz blanca y dura, que hace que todo parezca más sucio de lo que es."),
        ObjetoCuento("parche", "el parche", "un parche", "El parche",
            "Los parches para las ruedas vienen en una lata de diez y se pegan con saliva y paciencia; en una noche mala pone dos, y en una noche muy mala se le acaban y vuelve a casa a pie."),
        ObjetoCuento("auricular", "el auricular", "un auricular", "El auricular",
            "El auricular va en el oído izquierdo, por si el implante falla; es de los de antes de los implantes, y por él le habla la aplicación con una voz de mujer amable que no se cansa nunca."),
        ObjetoCuento("llave", "la llave de la bici", "una llave", "La llave",
            "La llave de la bici es una herramienta con seis bocas que lleva colgada del cuello; con ella aprieta, afloja y, una vez, espantó a uno que quería quedarse con la caja."),
        ObjetoCuento("barra", "la barra de proteína", "una barra de proteína", "La barra",
            "La barra de proteína es de tanque, como casi todo lo que se come abajo; viene en un papel plateado que cruje, sabe a vainilla durante dos segundos y alimenta, según la caja, como un filete."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} no tiene planes para dentro de diez años: tiene planes para dentro de diez minutos, que es lo que tarda el próximo pedido. Dicen que eso es malo. Ella piensa que los que lo dicen no reparten."),
        v("Piensa a veces en lo raro de su trabajo: pasarse la noche llevando cosas a gente que no ve, a sitios que no conoce, guiada por flechas que no ha dibujado. Si lo piensa mucho se marea, así que pedalea más fuerte y se le pasa."),
        v("{mundo} Ella piensa en cosas más cercanas: en el freno de atrás, que chilla; en si le alcanzará para el cuarto; en el pedido de mañana. Las cosas grandes, se dice, son para la gente de las torres, que tiene tiempo."),
        v("{mundo} Una repartidora no decide qué se come en la ciudad, pero sin ella no llega nada a ninguna mesa. {nombre} le da vueltas a eso en los semáforos largos. No se lo dice a nadie: en la calle, el orgullo es cosa de los que tienen coche."),
        v("Nadie en la ciudad mira hacia arriba, salvo los turistas y los que están a punto de caerse. {nombre} sí mira, a veces, en los puentes altos, cuando nadie la ve. No busca nada: solo quiere comprobar que el cielo sigue ahí, aunque no se vea."),
        v("{mundo} A sus {edad} años, {nombre} nunca ha salido de la ciudad. Dicen que afuera no hay nada, y ella no tiene por qué no creerlo. A veces, en lo alto de un puente, mira hacia donde se acaban las luces y se pregunta cómo es ese nada.", hasta = "borde"),
        v("Le debe al implante más de lo que gana en un año, y el implante le recuerda la cuota cada día uno con una música alegre. {nombre} piensa que es raro deberle dinero a algo que una lleva dentro del ojo. Luego piensa que todo el mundo debe algo, y se le pasa.", hasta = "ultima_cuota"),
    )

    private val MUNDOS = mapOf(
        "calma" to listOf(
            "En la calle se dice que este año los pedidos van bien, y que eso nunca dura.",
            "Las multas no han subido en meses, y los repartidores lo comentan con desconfianza.",
        ),
        "compra" to listOf(
            "Desde que Áurea es la dueña de Colibrí, la aplicación tiene otra voz, más amable y más cara.",
            "Se dice que Áurea cuenta los segundos de cada entrega, y que alguien, arriba, los mira.",
        ),
        "huelga" to listOf(
            "En la calle se habla de parar, en voz baja y luego no tan baja.",
            "Hay asambleas en los aparcamientos, y en cada una hay más bicis que en la anterior.",
        ),
        "desalojo" to listOf(
            "Se dice que Áurea quiere los niveles bajos para construir, y en los niveles bajos nadie sabe adónde iría.",
            "Hay carteles de reuniones de vecinos pegados encima de los anuncios, y los anuncios los tapan al día siguiente.",
        ),
        "tregua" to listOf(
            "Las cosas parecen más tranquilas este año, y la gente lo dice bajito, para no gastarlas.",
            "La cooperativa tiene más repartidores cada mes, y Colibrí, menos.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando arriba amanece, ella vuelve a casa. Sube los once pisos con la bici al hombro, la enchufa en el pasillo y se quita el casco, que le ha dejado la marca de siempre. Cierra la cortina negra contra el día que empieza, y el día, por fin, se calla un poco."),
        v("Al final de la noche la ciudad cambia de ruido: se apagan algunos letreros, pasan los camiones de la basura y en las torres empiezan a moverse los ascensores de los que trabajan de día. {nombre} pedalea hacia casa contra la corriente, como todos los días."),
        v("Vuelve a casa por el puente de siempre, despacio, con la batería en rojo y las piernas también. Abajo, en el agua negra de la bahía, se reflejan las luces de las torres. Es el único rato de la noche en que nadie le pide nada."),
        v("Antes de dormir se come lo que queda en la nevera, de pie, con la luz del implante. Luego apaga lo que se puede apagar, que no es mucho, y se tumba. El último anuncio de la noche le desea dulces sueños con una voz que no ha dormido nunca."),
        v("La última luz que ve no es la del día, sino la de un anuncio enorme en la torre de enfrente, que cambia de color cada diez segundos y le pinta el cuarto de rosa, de verde, de azul. Hace años que se duerme contando colores. Casi nunca llega al décimo."),
        v("Se ducha con agua tibia, porque a esa hora la caliente ya se acabó, y se mete en la cama con el pelo mojado. Por la pared le llega la voz del vecino, que se levanta para ir a trabajar. Se dan los buenos días a través del muro, y no se ven nunca."),

        v("Vuelve a casa a pie, por las escaleras de los niveles, con las piernas cansadas de otra manera. En el pasillo, el enchufe de la bici sigue vacío. Se acuesta sin cargar nada, y se le hace raro no tener nada que cargar.", SIN_BICI),
        v("Al final de la noche toma el tren colgante, que a esa hora va lleno de gente que vuelve de trabajar o que va. Por la ventana pasan los puentes que suele cruzar en bici, y desde ahí arriba le parecen de otra persona.", SIN_BICI),
        v("Vuelve a casa sin flechas, por las calles que se sabe de memoria, y se equivoca una sola vez. En su cuarto, sin anuncios, la oscuridad es oscuridad de verdad. Tarda en dormirse: nunca había oído tan bien su propia respiración.", SIN_IMPLANTE),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "faroles" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empieza la Noche de los Faroles, que en realidad son tres. Al despertar, {nombre} ya ve por la ventana los primeros faroles de papel colgados entre los edificios, rojos y amarillos, temblando con el aire de los aires acondicionados. El implante le ofrece faroles digitales; ella prefiere mirar los de verdad.",
                Parrafo.PUEBLO to "Por todas las calles cuelgan faroles de papel con una vela dentro, miles, entre los letreros de neón. Es la única luz de la ciudad que se mueve con el viento, y por eso se nota. Dicen que la fiesta es más vieja que la ciudad, y que la ciudad se la quedó porque no supo qué hacer con ella.",
                Parrafo.ANOCHECER to "Al terminar el turno se para en un puente a ver los faroles que suelta la gente desde las azoteas, que suben despacio hasta perderse en las nubes. Alguien a su lado pide un deseo en voz alta. Ella lo pide en voz baja, y no se lo dice ni al implante.",
            ),
            dia(
                Parrafo.GENTE to "Esta noche los repartidores llevan un farolito colgado de la caja, por costumbre: dicen que trae propinas. {nombre} lleva el suyo, uno de papel rojo, y a la tercera entrega ya se le ha quemado. Le queda el alambre, y lo sigue llevando, por si la suerte no se fija.",
                Parrafo.TRABAJO to "A esta hora reparte faroles: la mitad de los pedidos de esta noche son cajas de faroles de papel y velas, que la gente compra a última hora. Pesan poco y abultan mucho, y en los ascensores la miran como si llevara un pastel de bodas.",
            ),
            dia(
                Parrafo.PUEBLO to "Última noche de los Faroles. Las velas se han ido apagando y el papel de los faroles está mojado y caído, como ropa tendida después de la lluvia. Los barrenderos los descuelgan de madrugada. Algunos vecinos guardan el suyo para el año que viene, aunque nunca dura.",
                Parrafo.ANOCHECER to "Vuelve a casa con un farol que encontró tirado en la calle, todavía entero, y lo cuelga en la ventana. No tiene vela: le pone detrás la luz del implante, que da un rojo raro. Se duerme mirándolo, y por un rato su cuarto parece de otra ciudad.",
            ),
        )),
        "dia_red" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Red, el aniversario de la noche en que se encendió la ciudad, cuando se conectaron todas las torres a la vez. A medianoche se apagan todos los letreros durante un segundo, y luego se encienden juntos. Dicen que es para recordar. Nadie dice qué.",
            Parrafo.PUEBLO to "Las torres de Áurea proyectan en el cielo la historia de la ciudad, en luces de colores: primero el pantano, luego la primera torre, luego todas, hasta las de ahora. En la historia no salen los niveles de abajo. {nombre} la mira desde la calle y busca el suyo, por si acaso.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la ciudad de antes de la Red, cuando las calles no hablaban. Su abuela decía que entonces una se perdía, y que estaba bien perderse de vez en cuando. A ella le parece imposible: en esta ciudad ni siquiera se puede no saber dónde está una.",
        ),
        "lluvias" to unDia(
            Parrafo.MANANA to "Hoy han empezado las lluvias. Lo nota antes de abrir los ojos, por el ruido: el agua golpea los toldos, los aires acondicionados, la chapa de la ventana, y cada cosa suena distinto. El implante le avisa de que la temporada ha empezado oficialmente, como si el cielo pidiera permiso.",
            Parrafo.TIEMPO to "Llueve como solo llueve el primer día, de golpe y sin pedir perdón. Durante meses va a ser así, todas las tardes, y la ciudad entera cambia de forma: se abren los paraguas, se tienden las pasarelas, se cierran las tapas de los desagües para que el agua no suba por ellos.",
            Parrafo.PUEBLO to "En los niveles bajos el agua empieza a buscar su camino escaleras abajo, como cada año. Los tenderos suben la mercancía a los estantes altos, y los niños se ponen bolsas en los pies. Los repartidores se miran unos a otros con la cara de quien sabe lo que viene.",
        ),
        "dia_aurea" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Corporación, y por ley nadie trabaja, salvo los que trabajan para que los demás no trabajen. Al despertar, el implante le desea feliz día en nombre de Áurea, con música de violines. {nombre} lo cierra antes de que termine.",
            Parrafo.TRABAJO to "A esta hora mira el desfile de drones desde la azotea: miles de luces doradas que dibujan en el cielo el logo de Áurea, luego un corazón, luego una mano que saluda. La gente aplaude en las calles, porque los drones también cuentan quién aplaude.",
            Parrafo.PENSAMIENTO to "Es su único día libre pagado del año, y no sabe qué hacer con él. {nombre} piensa que es raro que te regalen un día y te digan qué tienes que mirar. Al final se duerme por la tarde y se despierta de noche, a la hora de siempre, sin nada que repartir.",
        ),
        "marea" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Esta noche ha subido el agua. La tormenta empujó el mar contra los diques y el agua entró por los túneles, como entra cada verano, solo que esta vez entró con ganas. El implante le pinta las calles anegadas de azul claro, como si fuera bonito.",
                Parrafo.TRABAJO to "A esta hora reparte por los niveles bajos con el agua por los tobillos, con la bici al hombro en los tramos hondos. Los pedidos de hoy son botas, bombas de achique y comida, mucha comida, para gente que no puede salir. Las propinas son buenas: el agua ablanda a la gente.",
                Parrafo.PUEBLO to "En el nivel −3 el agua ha llegado a las puertas, y los vecinos la sacan a cubos, en fila, pasándose el cubo de mano en mano como en una película vieja. Un puesto de fideos sigue abierto, con el cocinero subido a un banco, sirviendo caldo a los que achican.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "El agua sigue en los niveles bajos, quieta y tibia, con una capa de aceite que hace dibujos de colores bajo los letreros. Los niños navegan en barreños. Los mayores miran el agua como se mira a un pariente que se ha quedado demasiado tiempo.",
                "Las bombas de achique zumban día y noche, y el agua baja un dedo cada día. En las paredes queda una raya oscura donde llegó, y los vecinos la comparan con las de otros años: hay rayas de muchos años, unas encima de otras.",
                "Ya casi no hay agua en las calles, solo barro y un olor que tardará semanas en irse. Las tiendas sacan la mercancía mojada a la puerta, y se vende barata. {nombre} se compra unas zapatillas que se secaron al aire y ahora son de otro color.",
            )),
        ),
        "hora_muda" to unDia(
            Parrafo.MANANA to "Hoy es la Hora Muda. A las diez de la noche, por ley, se apagan todos los anuncios durante una hora, en los letreros y en los ojos. La gente la espera como se esperan los fuegos artificiales, solo que al revés.",
            Parrafo.PUEBLO to "Cuando llega la hora, la ciudad se queda con su luz de verdad: las farolas, las ventanas, los faros de los coches. Las calles parecen más anchas. La gente se para en la acera, mirando alrededor, como si acabara de llegar a otra ciudad que se parece mucho a la suya.",
            Parrafo.PENSAMIENTO to "{nombre} pedalea durante la Hora Muda sin una sola voz en el ojo, solo con las flechas del mapa, que no son anuncio. Piensa que el silencio se parece a su cuarto con la cortina cerrada, pero más grande. Cuando vuelven los anuncios, le parece que alguien ha subido el volumen del mundo.",
        ),
        "feria" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la feria de los niveles bajos, cuatro noches de puestos, música y luces de colores colgadas donde no llega la luz. Los vecinos sacan las mesas a la calle desde temprano. Hasta los guardias de Áurea vienen, sin uniforme, y fingen que nadie los reconoce.",
                Parrafo.PUEBLO to "En la feria hay de todo: comida de verdad, juegos de tirar aros, adivinas que leen el implante en vez de la mano, peleas de robots de juguete. Los puestos se apretujan en los callejones, y la música de uno se mete en la del otro hasta que ya no se sabe cuál es cuál.",
                Parrafo.GENTE to "Los repartidores de su zona se juntan en la feria después del turno, con las cajas todavía a la espalda. Se compran algodón de azúcar teñido de neón y lo comen sin quitarse los guantes. Uno gana un pez en una bolsa, no sabe qué hacer con él y se lo regala a un niño.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "La feria sigue. De noche los callejones huelen a azúcar quemada, a fritura y a lluvia, y las luces de colores se reflejan en los charcos. Los niños corren entre las piernas de los mayores, con la cara pintada y la boca pegajosa.",
                "En el escenario de la plaza de abajo tocan grupos de los niveles, uno cada hora, cada uno más fuerte que el anterior. La gente baila con el casco puesto, porque mañana hay que trabajar y quitárselo es perder el tiempo.",
                "Última noche de la feria. Los puestos venden barato lo que les queda, y los vecinos se llevan a casa sillas de colores, peces y luces que ya no encienden. Mañana el callejón volverá a ser un callejón, y nadie se creerá que aquí hubo baile.",
            )),
        ),
        "cosecha_torres" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la cosecha de las torres-granja, los rascacielos de cristal donde crecen las verduras bajo luces moradas, piso sobre piso. Durante unos días, en el mercado de abajo hay tomates, lechugas y zanahorias de verdad, con tierra todavía. Huelen raro: huelen a algo.",
                Parrafo.TRABAJO to "A esta hora reparte cajas de verdura, que pesan el triple que una de fideos y no caben en la caja térmica. Los clientes abren la puerta con una sonrisa que no ponen para nada más. Una señora le da una zanahoria de propina, y ella no sabe si comérsela o guardarla.",
                Parrafo.PENSAMIENTO to "{nombre} nunca ha visto crecer una planta, salvo la de la azotea, que más bien se muere. Piensa que una zanahoria sale de la tierra, y que la tierra, aquí, está debajo de muchos pisos de cemento. Se pregunta si allá abajo, debajo de todo, todavía queda algo de campo.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "En el mercado hay colas para comprar verdura, y la gente sale con las bolsas abrazadas como si llevara niños. Un puesto vende manzanas de una en una, envueltas en papel. Los niños las miran sin saber muy bien qué son.",
                "Las torres-granja brillan de morado toda la noche, con los cristales empañados. Desde los puentes se ven por dentro los pisos llenos de hojas verdes, y los drones que entran y salen cargados como abejas.",
                "La verdura de la cosecha dura poco: lo que no se vende en tres días se pone negro. Los vendedores la bajan de precio cada tarde, y a medianoche la regalan. Hay vecinos que solo comen verdura a medianoche, una semana al año.",
            )),
        ),
        "compra_colibri" to unDia(
            Parrafo.MANANA to "Hoy, al despertar, la aplicación tiene otro color: el verde de Colibrí se ha vuelto dorado, y en el ojo le aparece un mensaje que empieza con «Bienvenida a la familia Áurea». {nombre} lo lee dos veces, y la segunda le gusta menos.",
            Parrafo.TRABAJO to "A esta hora reparte con la aplicación nueva, que se parece a la vieja pero habla más. Le cuenta los segundos de cada entrega, le pone estrellas, le dice «buen trabajo» con una voz que suena a otra cosa. En un semáforo, otro repartidor le pregunta si a ella también, y ella dice que sí.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que no ha cambiado nada y que ha cambiado todo, y no sabe cuál de las dos cosas es verdad. Lo que reparte es lo mismo, la ciudad es la misma, la bici es la misma. Pero ahora, cuando pedalea, tiene la sensación de que alguien de arriba la está mirando.",
        ),
        "ausentes" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es la Noche de los Ausentes. En las puertas de las casas aparecen velas y retratos de luz de los que se fueron, que flotan un palmo sobre el suelo y sonríen siempre igual. El implante le pregunta si quiere poner a alguien. Ella le dice que no, y luego que sí.",
                Parrafo.PUEBLO to "Las calles de los niveles bajos están llenas de velas en el suelo, y hay que pedalear despacio para no apagarlas. Hay retratos de viejos, de niños, de perros. En una esquina hay uno de un repartidor con su casco, con una caja de fideos al lado, por si tiene hambre.",
                Parrafo.PENSAMIENTO to "{nombre} pone en la ventana el retrato de su abuela, el único que tiene, y se queda mirándolo. Su abuela sale riéndose de algo que ya nadie recuerda. Piensa que la luz no envejece, y que eso es bueno y es triste, las dos cosas a la vez.",
            ),
            dia(
                Parrafo.GENTE to "Segunda noche de los Ausentes. Su madre la llama, como cada año, para hablar de la abuela, y se cuentan las mismas historias que el año pasado, en el mismo orden. Al final las dos se ríen en el mismo sitio. {nombre} piensa que eso también es un retrato.",
                Parrafo.ANOCHECER to "Al volver a casa, de madrugada, las velas de la calle ya se han apagado casi todas, y los retratos siguen ahí, encendidos, sonriéndole a nadie. {nombre} apaga el de su abuela con un parpadeo. Antes de dormirse le da las buenas noches, bajito, por si acaso.",
            ),
        )),
        "descuentos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la Semana del Descuento, y la ciudad entera se ha despertado con ganas de que le traigan cosas. Al abrir los ojos, el implante ya le enseña la cola de pedidos: cientos, en rojo, parpadeando. Se toma la lata de un trago y sale sin peinarse.",
                Parrafo.TRABAJO to "A esta hora reparte como en ninguna otra semana del año: cajas, cajas y más cajas, a todas las torres, sin tiempo para comer. La aplicación le promete un extra si pasa de cien entregas al día. No dice cuánto, y ella ya ha aprendido a no preguntar.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en todas las cosas que reparte esta semana y que nadie necesita: luces, juguetes, una tostadora con forma de gato. Le da igual: cada caja es una entrega, y cada entrega es un trozo del cuarto. Lo que la gente compre es asunto de la gente.",
            )),
            sigue = mapOf(
                Parrafo.PUEBLO to listOf(
                    "La ciudad sigue pidiendo. En los ascensores de servicio hay atascos de repartidores, cada uno con su caja, mirándose los zapatos. En las puertas de las torres se forman montañas de paquetes que nadie recoge hasta la mañana.",
                    "En las calles no se ve a nadie comprando: todos compran desde casa, y lo que se ve son bicis, drones y furgonetas, cientos, cruzándose sin chocar casi nunca. Desde un puente alto parece un hormiguero al que le han echado azúcar.",
                    "Los contenedores de reciclaje están llenos de cajas vacías, y hay gente que vive de recogerlas y venderlas al peso. Pasan con carritos altísimos de cartón doblado, como si llevaran una casa a cuestas.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Vuelve a casa con las piernas de madera y la cuenta del día, por una vez, en verde. Ni siquiera se quita el impermeable: se tumba en la cama tal como está, y el implante le pone una estrella dorada en el ojo. Se duerme antes de que se apague.",
                    "Al amanecer se cruza con otros repartidores que vuelven igual que ella, con la cabeza colgando y la batería en las últimas. Nadie habla. Uno levanta dos dedos del manillar, y todos lo imitan, como una ola cansada.",
                ),
            ),
        ),
        "nochevieja" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es la última noche del año, y todos los letreros de la ciudad cuentan hacia atrás, a la vez, desde un número enorme. Al despertar, el de la torre de enfrente le pinta en la pared las horas que quedan. Las mira mientras se viste, y le parecen pocas para todo lo que hay que repartir.",
                Parrafo.PUEBLO to "La ciudad entera espera la cuenta: en las azoteas, en los puentes, en los callejones. Cuando llega a cero, los letreros se vuelven blancos durante un segundo y luego estallan de colores, y los drones escriben el año nuevo en el cielo con letras de luz. Los niños gritan. Los perros, también.",
                Parrafo.ANOCHECER to "A medianoche está en el callejón de los fideos, con el casco en la mano y un tazón de caldo, mirando la cuenta en el letrero de enfrente. Cuando llega a cero, el cocinero levanta su vaso hacia ella sin decir nada. Ella levanta el tazón. Brindar con caldo, piensa, es de buena suerte.",
            ),
            dia(
                Parrafo.GENTE to "El primer día del año la ciudad se levanta tarde, y los pedidos son de café, de agua y de pastillas para la cabeza. {nombre} los reparte con una sonrisa que no le cuesta: es el día del año en que la gente abre la puerta más despeinada. Las propinas, en cambio, tienen resaca.",
                Parrafo.ANOCHECER to "Vuelve a casa con la luz gris del primer día y la calle llena de restos de fiesta: confeti, botellas, una corona de papel. Se pone la corona en la escalera y se la quita antes de llegar a su piso. Se duerme pensando que este año va a ser distinto. Lo piensa todos los años, y le gusta pensarlo.",
            ),
        )),
        "bateria" to unDia(
            Parrafo.MANANA to "Hoy la batería se muere a media cuesta, en el puente del nivel −1, y {nombre} empuja la bici hasta el taller de Dalia con la caja todavía llena. Llega tarde a la entrega, y la aplicación le cobra la multa antes de que ella termine de explicarse.",
            Parrafo.TRABAJO to "A esta hora está en el taller, mirando cómo Dalia abre la batería vieja como quien abre un pescado. Dentro hay celdas hinchadas y un olor a quemado. Dalia dice que lleva meses muerta y que la bici andaba de puro orgullo.",
            Parrafo.GENTE to "Dalia le pone una batería nueva y le cobra la mitad, sin decirle por qué. {nombre} no pregunta: con Dalia no se pregunta, se agradece y se vuelve. Le promete traerle unos bollos del mercado, y Dalia le contesta que con bollos no se pagan las baterías, y que los quiere de carne.",
        ),
        "piso_cien" to unDia(
            Parrafo.MANANA to "Hoy le toca un pedido raro: una caja chica, sin nombre, para el piso cien de una torre de Áurea. El guardia de la entrada la mira de arriba abajo, le escanea el ojo y, después de pensarlo mucho, la deja pasar por el ascensor de servicio.",
            Parrafo.TRABAJO to "A esta hora sube los cien pisos en un ascensor de cristal, y la ciudad se va hundiendo a sus pies, cada vez más chica, hasta que los letreros parecen alfileres. Arriba hay un pasillo con alfombra y un hombre que abre la puerta sin mirarla. Firma, cierra, y ya.",
            Parrafo.PENSAMIENTO to "{nombre} se queda un momento junto a la ventana del pasillo antes de bajar. Desde arriba la ciudad es bonita y ordenada, como un tablero lleno de luces. Su nivel no se ve. Piensa que desde aquí nadie podría imaginarse que abajo llueve distinto.",
        ),
        "mil_pedidos" to unDia(
            Parrafo.MANANA to "Hoy, en la entrega número mil sin una sola multa, el implante le tira confeti en el ojo y le pone una medalla dorada junto al nombre. Dura tres segundos. Luego vuelve la flecha azul, con el pedido siguiente, que ya va tarde.",
            Parrafo.TRABAJO to "A esta hora los otros repartidores de su zona se enteran de la medalla, porque la aplicación se lo cuenta a todos. Le mandan mensajes: unos la felicitan, otros le preguntan cómo lo hace, y uno le dice que eso le pasa por no tener vida. Los tres tienen razón.",
            Parrafo.ANOCHECER to "Al terminar el turno se compra un pastel en un puesto que cierra tarde, uno pequeño, de los de cumpleaños, y se lo come sentada en el bordillo, con el casco puesto. No le dice a nadie por qué. La medalla, en el ojo, brilla un rato más y luego se apaga sola.",
        ),
        "mapa_falla" to unDia(
            Parrafo.MANANA to "Hoy, a media entrega, en un cruce del nivel −2, la flecha azul se le apaga. Luego se apaga el mapa entero, y por un momento el mundo es solo lo que es: paredes, humo, gente que pasa. El implante vuelve al minuto, pidiendo disculpas, como si nada.",
            Parrafo.TRABAJO to "A esta hora sigue repartiendo, pero mira el mapa de reojo, como se mira a un perro que ha mordido una vez. Cada vez que la flecha tiembla, se le encoge el estómago. Lleva el pedido de memoria, por si acaso, y llega antes que nunca.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que no sabría volver a casa sin el implante, y la idea le da más miedo que una bici sin frenos. Esa noche se aprende de memoria tres calles, por si acaso, como quien guarda una vela en un cajón.",
        ),
        "primeros_fideos" to unDia(
            Parrafo.MANANA to "Hoy el último pedido de la noche la deja en el nivel −3, en un callejón que no sale en el mapa, con hambre y sin batería. Al fondo hay un letrero magenta con un tazón que parpadea, y debajo, seis bancos de plástico. Huele a caldo. Es lo primero que huele bien en toda la noche.",
            Parrafo.TRABAJO to "A esta hora está sentada en un banco del puesto, con la bici enchufada a un cable que le ha tendido el cocinero sin que ella se lo pida. Pide fideos y, sin pensarlo, doble caldo. El cocinero la mira, asiente y le sirve un tazón tan lleno que tiene que bajar la cabeza para no derramarlo.",
            Parrafo.GENTE to "El de los fideos no habla: pone la radio, corta cebollín, limpia la barra con un trapo que ha sido de muchos colores. Desde la farmacia de enfrente, un viejo los mira por el cristal. {nombre} come despacio, por primera vez en meses, y paga sin mirar el precio.",
            Parrafo.ANOCHECER to "Vuelve a casa con la batería a medias y el estómago lleno, y por el camino se da cuenta de que no se ha fijado en el nombre del puesto. Da igual: sabe dónde está. Antes de dormirse guarda el callejón en el mapa del implante con un nombre que se inventa: «El caldo».",
        ),
    )

    val PIEZAS = Piezas(
        vida = "repartidora", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
