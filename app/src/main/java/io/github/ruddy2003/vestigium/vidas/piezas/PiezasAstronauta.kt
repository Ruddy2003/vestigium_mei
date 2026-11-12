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
 * El cuento de la astronauta, ingeniera de vuelo en una nave rumbo a Alfa Centauri, años 15 a 19 del viaje. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su ficha se llama «Una astronauta», que en medio de una frase no funciona: como el del naturalista, este cuento
 *   escribe «la astronauta» a mano y no usa el hueco del nombre.
 * - No hay cielo ni estaciones: el párrafo del tiempo cuenta la ventana y la luz de a bordo.
 * - Su día tiene dos tramos de trabajo, dos comidas y dos ratos propios (el ejercicio en el anillo y la ventana): esos
 *   textos valen a cualquiera de sus horas.
 * - Dos estados: «parada» (cada octubre, diez días con el reactor parado y media luz) e «ingravidez» (temporada 3, tres
 *   semanas con el anillo parado y sin peso).
 * - El pueblo es la nave, y lee el arco «granja» (lo que ha crecido en los cultivos).
 * - No repite lo que ya dice «Su vida»: los quince años, la décima de la velocidad de la luz, el reactor, el anillo que
 *   gira, el trigo enano, las papas, la soya, las ciento sesenta personas, los dos años de los mensajes.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG7.kt`):
 * - Su madre, viva en la Tierra hasta que llega la noticia (`noticia`, temporada 2); desde entonces, recordada.
 * - Sana, su aprendiz desde `aprendiz` (temporada 1), ingeniera desde `sana_aprueba` (2), madre desde `hija_sana` (5).
 * - Ade, el botánico: amigo hasta `ade` (temporada 3); viven juntos desde `camarote` (4).
 * - La comandante Varga manda hasta `comandante` (temporada 4). Nilo, el hijo de Bao, desde `nilo` (3).
 * - Jefa de ingenieros desde `jefa` (temporada 4).
 * - Las fiestas que vuelven cada año no nombran a Sana, a Ade, a Varga, a Nilo ni a su madre.
 */
internal object PiezasAstronauta {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A la astronauta la despiertan las luces del pasillo, que suben despacio, de azul a blanco, imitando un amanecer que ninguno de los nacidos a bordo ha visto. Se queda un momento quieta en la litera, escuchando la nave: el zumbido del aire, el agua en los tubos, un golpe lejano."),
        v("La astronauta se despierta un minuto antes de que suban las luces, como siempre, con el oído ya atento al ruido de la nave. Lleva tantos años escuchándola que sabe si algo va mal antes que los sensores. Hoy suena bien. Se levanta. El suelo del anillo está tibio."),
        v("La astronauta se despierta con la boca seca, porque el aire de a bordo siempre es un poco seco, y bebe del grifo de la pared un agua que ha pasado por todos los cuerpos de la nave y por todos los filtros, y que sabe a nada. Ya no piensa en eso. Casi nunca."),
        v("Lo primero que hace la astronauta al levantarse es mirar la pantalla pequeña de su camarote, que dice la hora de a bordo, la de la Tierra y los días que faltan. Los días que faltan son muchos. Ya no los cuenta: los mira, como se mira el tiempo que va a hacer."),
        v("La astronauta se despierta soñando con lluvia, con una lluvia de verdad, sobre un tejado, como cuando era niña en la Tierra. Abre los ojos. El techo del camarote está a un palmo. No llueve. No ha llovido desde que salió. No volverá a llover para ella, salvo si llega."),
        v("A la astronauta la despierta el aviso de la sala de máquinas, tres pitidos cortos en el auricular: una válvula, un sensor, nada grave, pero suyo. Se viste a oscuras, con la ropa de ayer, y sale al pasillo descalza. Las botas, después. Primero, la válvula."),
    )

    /** Diez días con el reactor parado y media luz, cada octubre (estado «parada»). */
    private fun pa(texto: String, hacer: Hacer? = null) = v(texto, estado = "parada", hacer = hacer)

    private val PARADA_MANANA = listOf(
        pa("La astronauta se despierta en la media luz de la parada, una luz naranja y baja que dura todo el día, porque el reactor está apagado y la nave vive de las baterías. Hace un poco de frío. Se pone dos capas. Hoy toca abrir otra tapa del reactor."),
        pa("A la astronauta la despierta el silencio, que es lo más raro de la parada: sin el reactor, la nave no zumba, y se oyen cosas que no se oyen nunca, una tos en otro camarote, una gota, el corazón propio. Se queda escuchando. Luego se levanta. Hay trabajo."),
        pa("La astronauta se despierta cansada, con las manos todavía doloridas de ayer dentro del reactor, y la media luz no ayuda. Bebe un té tibio, porque en la parada el agua caliente está racionada. Se mira las manos. Mañana, otra vez dentro."),
    )

    private val PARADA_TRABAJO = listOf(
        pa("A esta hora está dentro del reactor apagado, metida en un tubo estrecho con una lámpara en la frente, revisando las soldaduras una a una con un espejo y una lupa. Es la única vez al año que alguien puede entrar ahí.", Hacer.TRABAJO),
        pa("A esta hora cambia las piezas gastadas del reactor con Sana o con quien le toque, pasándose las herramientas en silencio, con guantes gruesos, despacio. Cada pieza nueva lleva años esperando en un cajón para este momento. La pone. Aprieta. Anota.", Hacer.TRABAJO),
        pa("A esta hora revisa las baterías que mantienen viva la nave durante la parada: el aire, el agua, las luces mínimas, la granja. Mira los números. Bajan como deben. Si bajan más de la cuenta, habrá que apagar cosas. Hoy no. Hoy aguantan.", Hacer.TRABAJO),
        pa("A esta hora come en el comedor a media luz, comida fría porque las cocinas están apagadas: pan de trigo enano, pasta de soya, papas cocidas ayer, una manzana de la granja. Todos comen callados, con abrigo. Parece un refugio de montaña de los de antes.", Hacer.COMER),
        pa("A esta hora duerme con dos mantas y el gorro puesto, porque sin el reactor la nave se enfría poco a poco, como una casa con la estufa apagada. Duerme hondo, del cansancio. Sueña con tubos que no se acaban.", Hacer.DORMIR),
        pa("A esta hora se lava con un paño húmedo y un poco de agua tibia, lo que toca en la parada, y se pone crema en las manos, que tiene rojas y agrietadas de los guantes. Se revisa una quemadura pequeña del soldador. No es nada. Se la tapa.", Hacer.CUIDAR),
        pa("A esta hora camina por el anillo a media luz, porque el ejercicio no se salta ni en la parada, y el anillo, con poca luz, parece más largo, como una calle de noche. Se cruza con otros que caminan con linternas. Se saludan con la cabeza.", Hacer.LIBRE),
        pa("A esta hora está en la sala común con los demás, alrededor de una sola lámpara, contando historias, porque las pantallas están apagadas para ahorrar. Los niños escuchan con la boca abierta. Alguien cuenta la de la tormenta de la Tierra. Siempre alguien la cuenta.", Hacer.GENTE),
    )

    private val PARADA_PUEBLO = listOf(
        pa("Durante la parada la nave se queda a media luz y en silencio, como un barco al ancla de noche. Los pasillos están naranjas, las pantallas apagadas, la granja con sus lámparas al mínimo. Solo la sala de máquinas tiene luz blanca: ahí está el trabajo de todo el año."),
        pa("En la parada se ahorra todo: el agua caliente, la luz, el aire de las zonas vacías, que se cierran con compuertas. La nave entera vive en la mitad de sí misma. Huele a metal frío y a gente junta."),
    )

    private val PARADA_ANOCHECER = listOf(
        pa("Al final de la jornada la astronauta sale del reactor con la cara negra y las rodillas doloridas, y se sienta un momento en el suelo del pasillo, a media luz, sin pensar en nada. Alguien le pasa una taza. Está tibia. Es lo mejor del día."),
        pa("Cuando baja la luz, en la parada, no hay mucha diferencia: de la media luz a la casi oscuridad. La astronauta cuenta los días que quedan para encender el reactor. Cuatro. Tres. Los cuenta como los niños las noches antes de una fiesta."),
    )

    /** Tres semanas con el anillo parado, sin peso, en la temporada 3 (estado «ingravidez»). */
    private fun ig(texto: String, hacer: Hacer? = null) = v(texto, estado = "ingravidez", hacer = hacer)

    private val INGRAVIDEZ_MANANA = listOf(
        ig("La astronauta se despierta flotando en el saco de dormir, atada a la pared del camarote con dos correas, y por un momento no sabe dónde es arriba. Luego se acuerda: el anillo está parado, no hay arriba. Se suelta, se impulsa con un dedo y sale por la puerta de cabeza."),
        ig("A la astronauta la despierta un cepillo de dientes que flota delante de su cara, que alguien no sujetó bien anoche. Lo atrapa. No es suyo. En la ingravidez, las cosas de todos se pasean por toda la nave. Hay una caja en el comedor para lo que se encuentra."),
        ig("La astronauta se despierta con la cara hinchada, como todos estos días, porque sin peso la sangre se sube a la cabeza y se queda ahí. Se mira en el espejo: parece otra, más joven, más redonda. Se ríe. Se recoge el pelo, que flota como algas."),
    )

    private val INGRAVIDEZ_TRABAJO = listOf(
        ig("A esta hora trabaja en el cojinete del anillo, flotando, sujeta con los pies a unas asas, mientras la pieza enorme se mueve despacio con unas grúas pequeñas. Sin peso, una pieza de dos toneladas se empuja con un dedo; pero si se escapa, no se para.", Hacer.TRABAJO),
        ig("A esta hora persigue por la sala de máquinas una gota grande de aceite que se ha escapado de un tubo y flota, temblando, como una medusa de oro. La atrapa con una toalla. Hay que atraparlas todas: sin peso, una gota en un circuito es un incendio.", Hacer.TRABAJO),
        ig("A esta hora revisa los cultivos de la granja con Ade, que no duerme desde que se paró el anillo, porque sin peso las raíces no saben hacia dónde crecer y las plantas se enredan. Atan los tallos a unas varillas. Las plantas, como ella, se acostumbran.", Hacer.TRABAJO),
        ig("A esta hora come flotando en el comedor, agarrada a la mesa con una mano, de bolsas con pajita, porque los platos no sirven: la sopa se escaparía como una pompa. Los niños juegan a soltar guisantes y atraparlos con la boca. Nadie los regaña.", Hacer.COMER),
        ig("A esta hora duerme atada a la pared en su saco, con los brazos flotando delante, como los sonámbulos de los dibujos. Se despierta dos veces porque ha soñado que se caía. No se puede caer. No hay adónde.", Hacer.DORMIR),
        ig("A esta hora se lava con una toalla húmeda y un champú que no hace espuma, porque el agua, sin peso, se queda pegada a la piel en bolas que no caen. Se seca riéndose. Lleva años en esta nave y nunca la había visto así.", Hacer.CUIDAR),
        ig("A esta hora hace el ejercicio en una máquina atada a la pared, tirando de unas gomas, porque sin peso los huesos se olvidan de ser huesos. Es aburrido. Por la ventana pequeña de la sala, las estrellas, quietas. Tira de la goma. Cuenta.", Hacer.LIBRE),
        ig("A esta hora está en la sala común, donde la gente flota en todas las posturas, unos del techo, otros del suelo, hablando cara a cara y cabeza abajo. Alguien ha puesto música, y se baila como se puede, girando. Nilo, el hijo de Bao, flota dormido en una red.", Hacer.GENTE),
    )

    private val INGRAVIDEZ_PUEBLO = listOf(
        ig("Con el anillo parado, la nave es otra: los pasillos ya no tienen suelo ni techo, la gente se mueve impulsándose por las paredes, y por todas partes hay cosas flotando, un calcetín, un lápiz, una hoja de lechuga. Hay redes en las esquinas para atraparlas."),
        ig("Sin peso, el anillo es un tubo enorme que no lleva a ninguna parte: se puede ir de punta a punta en un solo impulso, volando. Los niños lo hacen todo el día. Los mayores, cuando nadie los ve, también."),
    )

    private val INGRAVIDEZ_ANOCHECER = listOf(
        ig("Cuando baja la luz, la astronauta se queda flotando en la ventana grande de la proa, con las piernas dobladas, como dentro de una burbuja, y mira las estrellas, que no se mueven nunca. Sin peso, mirar es más fácil: el cuerpo no pesa, la cabeza tampoco."),
        ig("Al final del día los de la nave se cuentan los golpes: uno se dio en la cabeza con una puerta, otro se quedó flotando en medio de una sala sin poder alcanzar ninguna pared y tuvieron que lanzarle una cuerda. Se ríen. Faltan diez días. Ya casi se acostumbraron."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la nave el tiempo no se mira por la ventana: se lee en una pantalla."),
        v("La astronauta pasa por la ventana del pasillo y mira, como cada día. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los nacidos a bordo no entienden qué es el tiempo de fuera. Lo dibujan con nubes de cuento."),
        v("{cielo} {de_estacion} Afuera hace doscientos setenta grados bajo cero, siempre. Dentro, veintiuno."),
        v("{de_estacion} {cielo} La astronauta se acuerda a veces del viento. No sabe ya cómo sonaba."),
        v("Por la ventana no pasa nada, nunca, y por eso se mira tanto. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.SIN_CLIMA to listOf(
            "Por la ventana, las estrellas no titilan: son puntos quietos, duros, de colores que en la Tierra no se ven.",
            "Fuera está negro y lleno de estrellas, como siempre, y el Sol es una más, la más brillante, detrás.",
            "Por la ventana de proa, la estrella de delante brilla un poco más que ayer, aunque no se note.",
            "La luz de a bordo está en blanco de mediodía, que en la nave es la luz de casi todo el día.",
            "El aire de la nave está a veintiún grados y a la humedad de siempre, como dice la pantalla del pasillo.",
            "Fuera, la Vía Láctea es una banda de polvo blanco que cruza la ventana de un lado a otro.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.NINGUNA to listOf(
            "En la nave no hay estaciones: el año lo marcan la granja y el calendario de la pared.",
            "Aquí no hay invierno ni verano, solo la siembra y la cosecha del trigo enano.",
            "En la nave todos los días tienen la misma luz y la misma temperatura.",
            "A bordo los meses se cuentan, pero no se notan.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora está en la sala de máquinas, delante de la pared de pantallas, leyendo los números del reactor como un médico lee una radiografía: la temperatura, la presión, el campo que sostiene el fuego. Todo verde. Anota. Vuelve a mirar.", hacer = Hacer.TRABAJO),
        v("A esta hora cambia un filtro del aire en una sala de la granja, metida hasta la cintura en un hueco de la pared, mientras las plantas le rozan la espalda. El filtro viejo está gris de polvo de trigo. Lo mete en una bolsa.", hacer = Hacer.TRABAJO),
        v("A esta hora revisa las bombas del agua, una por una, escuchándolas con un tubo de metal pegado a la oreja, como hacían los mecánicos de antes. Las pantallas dicen que están bien. Ella quiere oírlo. Una bomba, antes de romperse, cambia de voz.", hacer = Hacer.TRABAJO),
        v("A esta hora fabrica una pieza en la impresora del taller, en el centro de la nave, donde no hay peso y las herramientas cuelgan de cuerdas. La pieza es un codo de tubo, nada, pero no hay otro en cuarenta años de camino. La mide tres veces antes de soltarla.", hacer = Hacer.TRABAJO),
        v("A esta hora repasa el escudo de proa en las pantallas: la placa de delante, que recibe todo el polvo del camino a una velocidad que lo convierte en balas. Busca grietas, marcas, calor. Hoy, nada. La nave entera vive detrás de esa placa.", hacer = Hacer.TRABAJO),
        v("A esta hora enseña algo a quien le toque en la sala de máquinas: a cerrar una válvula a mano, a leer un esquema, a no tocar lo que no se conoce. En la nave todos tienen que saber un poco de todo. Si un día ella falta, alguien tendrá que saber lo suyo.", hacer = Hacer.TRABAJO),

        v("A esta hora come en el comedor común, en una mesa larga, lo que hay: pan de trigo enano, un guiso de papas con algas, una ensalada de la granja y un vaso de agua. Se come con calma.", hacer = Hacer.COMER),
        v("A esta hora come de pie en la sala de máquinas, de un recipiente que ha traído del comedor, sin dejar de mirar las pantallas. Un puré de soya con especias, que en la nave se cultivan en un rincón de la granja, porque sin especias todo sabe a lo mismo.", hacer = Hacer.COMER),
        v("A esta hora come en el comedor con los ingenieros, que siempre hablan de lo mismo: lo que falla, lo que va a fallar, lo que fallaba en la Tierra. Hoy hay huevos, que solo salen una vez a la semana, y se celebra como una fiesta pequeña.", hacer = Hacer.COMER),

        v("A esta hora duerme en su litera, en el camarote del anillo, con la cortina corrida y el ruido de la nave alrededor, que ya es como el ruido del mar para la gente de costa: si se para, se despierta. No se para. Duerme.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con la Tierra, con cosas pequeñas que ya no se acuerda despierta: el olor de una calle mojada, el ruido de una moto, un perro. En el sueño es todo muy nítido. Por la mañana se le olvida, menos el perro.", hacer = Hacer.DORMIR),
        v("A esta hora duerme mal, porque el reloj del cuerpo no termina de creerse el día de la nave, y se despierta a una hora que no es de nadie. Mira el techo. Escucha. Cuenta las vueltas del anillo por el leve tirón que se nota en la espalda. Se duerme a la vuelta mil.", hacer = Hacer.DORMIR),

        v("A esta hora se ducha en la cabina del pasillo, dos minutos de agua tibia medidos por un reloj, y luego se pasa una toalla que guarda el agua para la granja. Se mira en el espejo: el pelo con canas, las arrugas de la risa. Le parece que le sientan bien.", hacer = Hacer.CUIDAR),
        v("A esta hora pasa por la enfermería a la revisión de cada día: la tensión, el pulso, un pinchazo en el dedo, una pregunta sobre cómo ha dormido. Bao, el médico, apunta todo sin mirarla, con una sonrisa. En la nave, cuidar el cuerpo es parte del trabajo.", hacer = Hacer.CUIDAR),
        v("A esta hora ordena su camarote, que mide lo que una cama y un armario, y donde cada cosa tiene su sitio porque no hay sitio para otra cosa: la ropa doblada, la foto pegada, la planta en su vaso. Limpia el polvo. En la nave también hay polvo. Es de la gente.", hacer = Hacer.CUIDAR),

        v("A esta hora corre por el pasillo del anillo, que da la vuelta entera a la nave y no se acaba nunca: si corres lo suficiente, vuelves al principio. Se cruza con los mismos de siempre. Los saluda en cada vuelta. A la quinta, ya no se saludan.", hacer = Hacer.LIBRE),
        v("A esta hora está en la ventana de la proa, la grande, sentada en el suelo con las piernas cruzadas, mirando hacia delante. La estrella de delante es la más gorda del cielo. Se la queda mirando como se mira el fuego. No piensa en nada. Es su rato.", hacer = Hacer.LIBRE),
        v("A esta hora lee en su litera un libro de la biblioteca de a bordo, una novela de la Tierra escrita hace doscientos años, sobre gente que viaja en barco. Se ríe en una página. Esa gente se mareaba, se mojaba, llegaba en tres meses. Le dan envidia.", hacer = Hacer.LIBRE),

        v("A esta hora está en la sala común con los demás, en los sofás viejos que vinieron de la Tierra, jugando a las cartas, hablando de nada, escuchando a alguien que toca la guitarra. Siempre las mismas canciones. Las saben todos.", hacer = Hacer.GENTE),
        v("A esta hora ve una película de la Tierra en la sala común, con veinte personas y un cuenco de palomitas de maíz de la granja. Es una película de un pueblo con playa. Los niños preguntan qué es la arena. Los mayores no saben explicarlo bien.", hacer = Hacer.GENTE),
        v("A esta hora juega al ajedrez con Bao, el médico, en una mesa del pasillo, como cada semana desde que salieron. Llevan miles de partidas. Él gana un poco más. Ella dice que es porque él tiene más tiempo libre. Él dice que es porque juega mejor.", hacer = Hacer.GENTE),
    )

    private val PUEBLO = listOf(
        v("{entorno} La granja ocupa tres salas del anillo: estantes de plantas de suelo a techo bajo lámparas rosadas, tanques de algas que burbujean, un rincón con gallinas que no saben que están en el espacio. Huele a tierra mojada."),
        v("La nave es una rueda que gira alrededor de un eje: en la rueda, el anillo, con peso, donde se vive; en el eje, sin peso, los talleres, los almacenes y los motores. Se pasa de uno a otro por unas escaleras que van perdiendo el peso a cada peldaño. {entorno}"),
        v("{entorno} El pasillo del anillo da la vuelta entera a la nave, y desde cualquier punto se ve que sube hacia delante y hacia atrás, como una calle que se curva hacia el cielo. No hay horizonte. La vista se acaba en la curva."),
        v("La sala de máquinas está en el centro de la nave, donde no hay peso: una sala redonda llena de pantallas, tubos, cables sujetos con bridas, herramientas que cuelgan de cuerdas para que no se vayan. Es el sitio de la astronauta. {entorno}"),
        v("{entorno} El comedor común es la plaza de la nave: una sala larga con mesas, plantas en las paredes, un tablón de avisos y una ventana pequeña. Ahí se come, se discute, se celebra, se vota. Los niños hacen los deberes en las mesas del fondo."),
        v("La ventana grande está en la proa: un ojo de cristal grueso como un brazo, con un banco delante donde siempre hay alguien. Por ella se ve lo que hay delante: estrellas, y una más brillante que las otras, que es hacia donde van. Nadie se cansa de mirar. {entorno}"),
    )

    /** Lo que ha crecido en la granja (arco «granja»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("granja", 0.0, 0.3, frases = listOf(
            "En la granja, el trigo enano está recién sembrado: unas bandejas de tierra oscura con brotes que apenas asoman.",
            "La granja parece vacía después de la cosecha, con las bandejas limpias y las lámparas esperando.",
        )),
        Entorno("granja", 0.3, 0.7, frases = listOf(
            "En la granja, el trigo enano ya está a media altura, verde, y se mueve con el aire de los ventiladores.",
            "Los estantes de la granja están llenos de verde, y en el aire hay un olor de hojas y de tierra.",
        )),
        Entorno("granja", 0.7, 1.0, frases = listOf(
            "En la granja, el trigo enano está alto y dorado, a punto de cosecha, y huele a pan sin hacer.",
            "La granja está tan llena que hay que pasar de lado entre los estantes, apartando las espigas.",
        )),
    )

    private val GENTE = listOf(
        v("Bao, el médico de la nave, es un hombre pequeño y tranquilo que subió con su mujer cuando eran jóvenes, y que conoce los cuerpos de todos los de a bordo mejor que ellos mismos. Sabe quién duerme mal, quién come poco, quién está triste."),
        v("Los ingenieros de la nave son doce, y la astronauta los conoce como se conoce a una familia que no se ha elegido: quién es lento pero seguro, quién es rápido y se equivoca, quién canta cuando trabaja. Se pelean poco. En una nave, pelearse es un lujo."),
        v("Los nacidos a bordo son cada vez más: niños que no han visto nunca un cielo, ni un árbol de verdad fuera de la granja, ni un desconocido. Para ellos la nave es el mundo, y la Tierra, un cuento. La astronauta los mira jugar en el pasillo y no sabe si darles pena o envidia."),
        v("La comandante de la nave es la que decide cuando no hay tiempo de votar: si hay que apagar algo, cerrar algo, encerrar a todos en el refugio del centro. El resto del tiempo decide la asamblea. La astronauta la respeta. No siempre está de acuerdo. Se lo dice."),
        v("Los de la granja son la gente más tranquila de la nave, con las manos siempre manchadas de tierra y una paciencia de árbol. Hablan de las plantas como de personas. La astronauta, que habla de las máquinas como de personas, los entiende."),
        v("Su madre vive en la Tierra, en una casa junto al mar, y le manda mensajes en cada paquete del correo: lo que ha comido, lo que ha leído, el tiempo que hace. Le llegan con dos años de retraso. La astronauta le contesta. Su madre lo leerá dentro de dos años más.", hasta = "noticia"),
        v("Su madre murió en la Tierra, y la noticia llegó dos años tarde. La astronauta guarda sus mensajes, todos, en una carpeta del ordenador, y a veces pone uno al azar, el de un martes cualquiera, y la escucha contarle el tiempo que hacía en una playa que ya no existe así.", desde = "noticia"),
        v("Sana, su aprendiz, subió a la nave con cinco años y no se acuerda de la Tierra. Tiene veinte, unas manos rápidas y una forma de preguntar que obliga a pensar. La astronauta le enseña las máquinas. Sana le enseña a no tenerles tanto respeto.", desde = "aprendiz", hasta = "sana_aprueba"),
        v("Sana ya es ingeniera, y trabaja al lado de la astronauta en la sala de máquinas como una igual, aunque a veces todavía le pregunta, por costumbre, antes de abrir una tapa. La astronauta le dice que ya no hace falta. Sana le pregunta igual.", desde = "sana_aprueba", hasta = "hija_sana"),
        v("Sana tiene una hija, nacida a bordo, que llegará a Alfa Centauri con veinticinco años y no habrá visto nunca otra cosa que esta nave. La astronauta la tiene en brazos cuando Sana trabaja. La niña le agarra el dedo. Tiene la fuerza de las cosas nuevas.", desde = "hija_sana"),
        v("Ade es el botánico de la granja, un hombre grande y lento que habla bajito a las plantas y que se ríe con todo el cuerpo. Lleva años trayéndole a la astronauta una fruta de la granja cada vez que la ve. Ella las acepta todas. Ya no sabe si es por la fruta.", hasta = "ade"),
        v("Ade le ha pedido que vivan juntos, y la astronauta no le ha dicho todavía ni que sí ni que no. En una nave de ciento sesenta personas, todo el mundo lo sabe. Todo el mundo espera. Ade también, con su paciencia de árbol.", desde = "ade", hasta = "camarote"),
        v("Ade y la astronauta viven juntos en un camarote del anillo, uno un poco más grande que los otros, con una planta en cada rincón que no deja sitio para nada. Él se levanta antes, a ver la granja. Ella se acuesta antes, cansada del reactor. Se ven en medio.", desde = "camarote"),
    )

    private val OBJETO = listOf(
        v("En la nave no hay nada que sobre, y nada que no tenga una historia. {dato_objeto}"),
        v("{dato_objeto} La astronauta lo sabe desde antes de subir, de los años de preparación en la Tierra."),
        v("Hoy la astronauta se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Los nacidos a bordo no se lo preguntan: para ellos el mundo siempre fue así."),
        v("{dato_objeto} En cuarenta años de camino, cada cosa tiene que durar o poder rehacerse."),
        v("Una nave es como una isla: lo que no hay, no hay. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("tableta", "la tableta", "una tableta", "La tableta",
            "La tableta de trabajo es una lámina fina que se dobla como un papel y que muestra los esquemas de toda la nave, pieza por pieza; tiene una copia de los manuales de cada máquina, porque en cuarenta años no habrá a quién llamar para preguntar."),
        ObjetoCuento("llave", "la llave inglesa", "una llave inglesa", "La llave",
            "La llave inglesa de la astronauta es de titanio, vieja, con su nombre grabado en el mango; vino de la Tierra en su equipaje personal, que era de dos kilos, y es lo único de ese equipaje que usa cada día."),
        ObjetoCuento("botella", "la botella", "una botella de agua", "La botella",
            "La botella de agua de cada uno lleva su nombre y se rellena en los grifos de la pared; el agua de la nave ha pasado miles de veces por los filtros y por las personas; en cuarenta años, cada gota habrá estado en todos."),
        ObjetoCuento("foto", "la foto", "una foto de papel", "La foto",
            "La foto de papel es una rareza a bordo, donde todo está en las pantallas; la de la astronauta es una playa con una mujer saludando, impresa en la Tierra antes de salir, con las esquinas gastadas de tanto tocarla."),
        ObjetoCuento("arnes", "el arnés", "un arnés", "El arnés",
            "El arnés de trabajo se lleva en el centro de la nave, donde no hay peso, con cuerdas y mosquetones para sujetarse a las asas de las paredes; sin él, un empujón de más te deja flotando en medio de una sala, sin poder alcanzar nada."),
        ObjetoCuento("lampara", "la lámpara de frente", "una lámpara de frente", "La lámpara",
            "La lámpara de frente es la de los mineros de antes, más pequeña: los ingenieros la llevan para meterse en los huecos de la nave, donde no llega ninguna luz; la de la astronauta tiene un filtro rojo para no deslumbrar a quien trabaja al lado."),
        ObjetoCuento("auriculares", "el auricular", "un auricular", "El auricular",
            "El auricular de la nave es un botón que se mete en la oreja y que nunca se quita, ni para dormir: por él llegan los avisos de las máquinas, las llamadas, las alarmas; cada uno lo tiene afinado a lo suyo, y el de la astronauta suena con la voz del reactor."),
        ObjetoCuento("semilla", "la semilla", "una semilla", "La semilla",
            "En el almacén frío de la nave hay semillas de miles de plantas de la Tierra, en tubos de cristal, para sembrar el planeta de delante si se puede; muchas no se plantarán nunca a bordo, y esperan cuarenta años dormidas, como cuentos sin leer."),
        ObjetoCuento("traje", "el traje", "un traje de salida", "El traje",
            "El traje para salir al casco es blanco, grueso, con una mochila de aire y un casco con visera dorada contra el sol de cerca; se tarda una hora en ponérselo y otra en comprobarlo; dentro, se oye la propia respiración como un mar."),
        ObjetoCuento("taza", "la taza", "una taza con tapa", "La taza",
            "Las tazas de a bordo tienen tapa y pajita, por si el anillo se para y las cosas flotan; la de la astronauta es azul, con una mella en el borde, y se la regaló un compañero que se quedó en la Tierra."),
        ObjetoCuento("cuaderno", "el cuaderno", "un cuaderno de papel", "El cuaderno",
            "El papel de la nave se hace con las fibras del trigo y se usa poco, para lo importante: el cuaderno de la astronauta tiene todas las reparaciones del viaje, a mano, con dibujos; dice que, si fallan las pantallas, el papel sigue."),
        ObjetoCuento("planta", "la planta", "una planta en un vaso", "La planta",
            "Cada camarote tiene derecho a una planta de la granja, en un vaso con tierra; la de la astronauta es una albahaca que ha replantado de esqueje en esqueje desde el primer año del viaje; es la misma, dice, aunque ya no queda ni una hoja de la primera."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} La astronauta piensa en esas cosas un rato, y luego vuelve a las máquinas, que no se enteran de nada. A sus {edad} años sabe que lo que pasa en la Tierra, cuando llega, ya ha pasado hace dos años: las noticias de a bordo son siempre historia."),
        v("Piensa en la llegada, que será dentro de muchos años, cuando ella sea vieja o no esté. Piensa en el planeta de delante, que nadie ha pisado, y en los niños de la nave bajando a él. Piensa que ella es el puente."),
        v("{mundo} La astronauta piensa en por qué subió, y a veces no se acuerda de todas las razones. Se acuerda de una: quería ver algo que nadie hubiera visto. Lo está viendo. Es negro, con estrellas, y no cambia. Piensa que habría que haberlo pensado mejor. Y se ríe."),
        v("{mundo} A veces, en la sala de máquinas, la astronauta piensa que es la persona que más lejos ha estado de su madre de toda la historia de las madres. Lo piensa y se le hace raro. Luego mira una pantalla. Un número sube. Lo baja. La nave sigue."),
        v("Piensa en la Tierra, que desde aquí es un punto que no se ve, al lado de otro punto, el Sol. Piensa en todos los que están ahí, miles de millones, haciendo su vida sin pensar en esta nave. Piensa que está bien. Que la nave tampoco piensa mucho en ellos. Casi."),
        v("{mundo} A sus {edad} años, la astronauta sabe que va a morir en esta nave o en el planeta de delante, y que no hay otra opción. Lo sabía al subir. Lo sabe distinto ahora. No le da miedo. Le da una especie de calma, como saber el final de una novela larga."),
    )

    private val MUNDOS = mapOf(
        "crucero" to listOf(
            "En el comedor se habla de la asamblea de noviembre, y de si este año habrá permiso para dos niños o para tres.",
            "En el correo de la Tierra venían noticias de un gobierno que ya habrá caído y de unas elecciones que ya se habrán ganado.",
            "Se habla del planeta de delante, del que cada año se sabe un poco más y se entiende un poco menos.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando las luces de a bordo empiezan a bajar, de blanco a naranja y de naranja a azul, la nave entera baja la voz con ellas. Los niños se van a los camarotes. La astronauta pasa por la granja, que de noche huele más."),
        v("Al bajar la luz, la astronauta camina hasta la ventana de la proa y se queda un momento de pie, mirando la estrella de delante, como quien sale a la puerta de casa a mirar el cielo antes de dormir. Siempre está ahí. Siempre un poco más cerca, aunque no se note."),
        v("Cuando se apagan las luces del pasillo, quedan encendidas las pequeñas del suelo, azules, como las de un avión de noche. La astronauta vuelve a su camarote siguiéndolas. Detrás de cada puerta, alguien duerme, alguien lee, alguien llora un poco. Así cada noche."),
        v("Al final del día la astronauta escribe en su cuaderno lo que ha hecho, a mano, con letra pequeña: qué se rompió, qué se arregló, qué hay que mirar mañana. Cierra el cuaderno. Apaga la luz de la litera. La nave sigue andando, a treinta mil kilómetros por segundo, en la oscuridad."),
        v("Cuando baja la luz, en la sala común alguien pone un disco viejo de la Tierra, y los mayores se quedan callados escuchándolo, cada uno pensando en lo suyo. Los nacidos a bordo no entienden por qué. Se aburren. Se van. Los mayores se quedan."),
        v("La última luz del día de a bordo se queda en la granja, que es lo último que se apaga, porque las plantas necesitan sus horas. La astronauta pasa por delante y ve el trigo a contraluz, dorado, como un campo de la Tierra en una tarde de agosto."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo en la nave, y anoche, a la hora de la Tierra, se brindó en el comedor con el vino de la granja, uno ácido y flojo que se hace con las uvas de un solo emparrado. Se contó hacia atrás. Los niños gritaron. Afuera, nadie se enteró.",
            Parrafo.TRABAJO to "Hoy la astronauta no trabaja, salvo la guardia de una hora en la sala de máquinas, que hace ella para que los jóvenes duerman. Luego, el día libre: una comida larga, una película, un paseo por el anillo con la nave casi vacía de gente despierta.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que en la Tierra también es año nuevo, pero hace dos años, o dentro de dos, según cómo se mire. Piensa que el año nuevo de la nave no es de nadie más. Le pide al año que viene lo de siempre: que no se rompa nada que no se pueda arreglar.",
        ),
        "revision" to unDia(
            Parrafo.MANANA to "Hoy es la revisión médica del año, la grande, y toda la nave pasa por la enfermería en turnos: sangre, huesos, ojos, corazón, una máquina que mira por dentro. La astronauta tiene turno a primera hora. Se presenta en ayunas, como manda Bao.",
            Parrafo.TRABAJO to "A esta hora está tumbada en la camilla de la enfermería mientras la máquina le mira los huesos, que el anillo no termina de cuidar del todo. Bao le enseña la pantalla: un poco menos de hueso que el año pasado. Lo normal. Le receta más ejercicio. Se lo receta cada año.",
            Parrafo.PENSAMIENTO to "La astronauta piensa en su cuerpo como en una máquina más de la nave, que hay que revisar, ajustar, cambiar de piezas si se puede. Piensa que es la máquina que menos entiende. Piensa que es la única que no tiene repuesto.",
        ),
        "correo" to unDia(
            Parrafo.MANANA to "Hoy llega el paquete de mensajes de la Tierra, que salió hace casi dos años y ha viajado detrás de la nave, alcanzándola despacio. La antena lo recoge durante la noche. Por la mañana, cada uno tiene una carpeta nueva en su pantalla. Toda la nave está callada, leyendo.",
            Parrafo.GENTE to "Esta tarde en el comedor se comentan las noticias de la Tierra, que ya son viejas: quién ganó unas elecciones, qué se inventó, qué se rompió. Alguien llora en un rincón con su carpeta abierta. Los demás lo dejan tranquilo. En cada correo, alguien llora en un rincón.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que cada mensaje es una carta de alguien que ya no es como era cuando la escribió. Piensa que ella tampoco es la misma que recibió el último. Piensa que hablar con la Tierra es hablar con fantasmas que hablan con fantasmas. Contesta igual.",
        ),
        "partida" to unDia(
            Parrafo.MANANA to "Hoy es el aniversario de la partida, el día en que la nave encendió los motores en la órbita de la Tierra y empezó a irse. En el comedor hay una tarta de trigo con un número de velas. Por la noche se apagarán todas las luces de a bordo para mirar atrás.",
            Parrafo.ANOCHECER to "Esta noche se apagan todas las luces de la nave, todas, y la gente se junta en las ventanas a buscar el Sol: un punto amarillo, el más brillante, detrás. Alguien señala. Los niños no saben cuál es. Los mayores, sí. Nadie habla. Luego se encienden las luces.",
            Parrafo.PENSAMIENTO to "La astronauta piensa en el día de la partida, en el ruido, en el miedo, en la Tierra azul haciéndose pequeña en la pantalla hasta ser un punto. Piensa que se despidió de todo de una vez, como quien arranca una venda. Piensa que todavía le duele, un poco, donde estaba la venda.",
        ),
        "siembra" to unDia(
            Parrafo.MANANA to "Hoy se siembra el trigo enano en la granja, el de todo el año, y medio barco va a ayudar: se llenan las bandejas de tierra, se ponen las semillas una a una con unas pinzas, se riegan con el agua contada. Los niños lo hacen mejor que nadie: tienen los dedos pequeños.",
            Parrafo.TRABAJO to "A esta hora siembra en la granja con los demás, de rodillas delante de un estante, poniendo semillas en la tierra con una pinza, una cada dos dedos, como manda el botánico. Le duelen las rodillas. Le gusta. Es el único día del año en que tiene tierra en las uñas.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que sembrar en una nave es como sembrar en la Tierra, pero más en serio: si el trigo no sale, no hay pan, y no hay a quién comprárselo. Piensa en sus abuelos, que sembraban en un campo. Piensa que no se imaginaban esto. Ella tampoco.",
        ),
        "juegos" to unDia(
            Parrafo.MANANA to "Hoy son los juegos del anillo, la fiesta del verano de la nave, aunque en la nave no haya verano: carreras por el pasillo que da la vuelta entera, saltos en la zona sin peso, un torneo de ajedrez, una obra de teatro de los niños. Todos llevan una cinta de color en el brazo.",
            Parrafo.TRABAJO to "Hoy la astronauta no trabaja: corre la carrera del anillo, la de veteranos, que es una vuelta entera, y llega la quinta de ocho, sin aire, con las piernas de goma. Le dan una cinta de tela que dice que ha corrido. La guarda en el cajón con las otras.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que la gente necesita fiestas aunque no haya estaciones, aunque no haya sol que vuelva, aunque el año sea solo un número en una pantalla. Piensa que por eso se inventaron. Se pone la cinta en la muñeca. La lleva todo el día.",
        ),
        "cosecha" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la cosecha del trigo enano, la de todo el año, cuatro días de trabajo en la granja para toda la nave. Las espigas están doradas, duras, crujen. El botánico da las instrucciones desde una escalera, con una voz que no se le oye en todo el año.",
                Parrafo.TRABAJO to "A esta hora corta espigas en la granja con unas tijeras pequeñas, una a una, y las mete en una bolsa de tela. A su lado, unos niños desgranan en una mesa. El polvo del trigo flota en el aire y brilla con las lámparas. Todo el mundo estornuda. Todo el mundo se ríe.",
                Parrafo.PENSAMIENTO to "La astronauta piensa que en esta bolsa está el pan de todo el año de la nave, y que si un día la cosecha falla, no hay otro sitio adonde ir. Piensa que la Tierra vivió así diez mil años, mirando el cielo. Ellos miran las lámparas.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en la cosecha, desgranando espigas en una mesa de la granja, con el polvo del trigo en el pelo y en la garganta. Los niños llevan la cuenta de las bolsas. Van por veinte. Faltan diez.",
                "A esta hora pesa el grano cosechado en la balanza de la granja y apunta los kilos en la pantalla: un poco más que el año pasado. El botánico sonríe sin decir nada. Esa sonrisa es la fiesta de la cosecha.",
                "A esta hora ayuda a limpiar las bandejas vacías de la granja después de la cosecha, raspando la tierra vieja para cambiarla. Las raíces del trigo salen enteras, como pelucas. Se guardan para el compost.",
            )),
        ),
        "correo_otono" to unDia(
            Parrafo.MANANA to "Hoy llega el segundo paquete de mensajes del año, más corto que el de la primavera de la Tierra. Hay menos mensajes cada vez: la gente de la Tierra se cansa, se muere, se olvida. Los que quedan escriben más largo. La astronauta abre su carpeta despacio.",
            Parrafo.GENTE to "Esta tarde, en el comedor, alguien lee en voz alta un mensaje de la agencia de la Tierra que dirigió la salida, con la voz seria de las cosas oficiales. Dice que están orgullosos. Que no los olvidan. La gente aplaude un poco. Luego se vuelve a hablar de la cosecha.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que cada año los mensajes de la Tierra se parecen más a cartas de un pueblo al que no se volverá: cariñosas, lejanas, de gente que ya vive otra vida. Piensa que está bien. Que la nave también vive otra vida. Piensa que tienen que soltarse un poco, los dos.",
        ),
        "parada" to unDia(
            Parrafo.MANANA to "Hoy se para el reactor para su revisión del año, y la astronauta es quien lo apaga, despacio, siguiendo una lista de cien pasos. Al final, el zumbido de todo el año se calla. La nave se queda a media luz, naranja, en silencio. Alguien, en el pasillo, dice ay.",
            Parrafo.TRABAJO to "A esta hora espera a que el reactor se enfríe lo bastante para abrirlo, mirando los números que bajan, con el turno de ingenieros al lado. Mientras tanto, prepara las herramientas en una mesa, en orden, como un cirujano. Diez días. Todo tiene que estar hecho en diez días.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que parar el reactor es como parar un corazón para operarlo: hay que hacerlo, y no hay que pensar demasiado en ello mientras se hace. Piensa que la nave vive de baterías ahora. Piensa que ella también, un poco. Se pone el abrigo.",
        ),
        "fin_parada" to unDia(
            Parrafo.MANANA to "Hoy se enciende otra vez el reactor, después de diez días de media luz, y la astronauta da la orden desde la sala de máquinas con toda la nave escuchando por los altavoces. Primero, un temblor. Luego, el zumbido. Luego, las luces blancas, de golpe, en todos los pasillos.",
            Parrafo.GENTE to "Esta tarde, en el comedor, hay comida caliente por primera vez en diez días, y la gente se quita los abrigos y las gorras como en una primavera. Alguien le da a la astronauta un abrazo que no esperaba. Alguien le pone una flor de la granja detrás de la oreja. Se la deja.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que lo ha vuelto a hacer: un año más de reactor, un año más de camino. Piensa que cada año es más difícil, porque las piezas se gastan y las suyas también. Piensa que no se lo dirá a nadie. Se come el guiso caliente despacio, con los ojos cerrados.",
        ),
        "asamblea" to unDia(
            Parrafo.MANANA to "Hoy es la asamblea del año, en el comedor, con todos los adultos de la nave sentados en las mesas y los niños mirando desde la puerta. Se vota qué se planta, qué se arregla, cuántos niños pueden nacer, quién hace qué. Dura todo el día. Siempre dura todo el día.",
            Parrafo.TRABAJO to "Hoy la astronauta no trabaja: está en la asamblea, presentando el informe de las máquinas, con números en una pantalla y la voz un poco temblorosa, aunque lo ha hecho cada año desde que salieron. Le hacen preguntas. Contesta. Le piden más piezas de repuesto. No hay más.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que la asamblea es lo más bonito y lo más pesado de la nave: ciento sesenta personas que tienen que ponerse de acuerdo sobre todo, porque no hay nadie más. Piensa que en la Tierra se votaba cada cuatro años y nadie iba. Aquí va todo el mundo.",
        ),
        "luces" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de las luces, la de los que se quedaron: cada uno cuelga en el pasillo del anillo una lámpara pequeña por cada persona que dejó en la Tierra, con su nombre escrito en un papel. El pasillo entero se llena de lucecitas. Se tarda una hora en recorrerlo.",
            Parrafo.ANOCHECER to "Esta noche se apagan las luces grandes y el pasillo del anillo queda iluminado solo por las lamparitas, cientos, con sus nombres. La gente camina despacio, leyendo. Los niños preguntan quién es cada uno. Nadie sabe todos. Todos saben algunos.",
            Parrafo.PENSAMIENTO to "La astronauta cuelga sus lámparas, una por cada uno, y piensa en cada nombre mientras la cuelga. Piensa que algunos ya habrán muerto sin que ella lo sepa todavía. Piensa que da igual: la lámpara es por cómo eran cuando se fue. Así los tiene a todos.",
        ),

        // ---------- Los de la temporada 1 ----------
        "polvo" to unDia(
            Parrafo.MANANA to "Hoy, a media mañana, un grano de polvo del camino, más pequeño que una lenteja, ha chocado contra el escudo de proa a la velocidad de la nave, y la nave entera ha sonado como una campana. Todo el mundo se ha quedado quieto. Luego, las alarmas.",
            Parrafo.TRABAJO to "A esta hora revisa el escudo de proa en las pantallas, centímetro a centímetro, con toda la sala de máquinas detrás mirando. Encuentra el sitio: un cráter del tamaño de un puño en la placa de delante, que ha aguantado. Respira. La placa está hecha para eso. Lo sabía. Respira otra vez.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que un grano de polvo, a esta velocidad, golpea como un camión. Piensa que el camino está lleno de granos de polvo. Piensa que no hay que pensar en eso. Esta noche lo pensará igual.",
        ),
        "video_madre" to unDia(
            Parrafo.MANANA to "Hoy, en la carpeta del correo, la astronauta ha encontrado un mensaje de su madre, grabado hace casi dos años: su madre en la cocina de su casa, junto al mar, con la ventana detrás y el pelo más blanco que la última vez. Lo ha visto tres veces antes de vestirse.",
            Parrafo.GENTE to "Esta tarde le cuenta a Bao, en la partida de ajedrez, lo que le dice su madre: que ha plantado tomates, que la vecina se ha mudado, que el mar está como siempre. Bao escucha sin mover una pieza. Al final le dice que su madre parece contenta. La astronauta dice que sí. Pierde la partida.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que su madre, ahora mismo, en la Tierra, ya es dos años más vieja que en el mensaje. Piensa que lo que le conteste lo verá dentro de dos años más. Piensa que hablan como dos estrellas: con una luz que ya salió hace mucho.",
        ),
        "casco" to unDia(
            Parrafo.MANANA to "Hoy la astronauta sale por primera vez al casco de la nave, a reparar una antena que se ha soltado, después de quince años dentro. Una hora para ponerse el traje. Otra para comprobarlo. La compuerta se abre. Fuera, nada. Todo.",
            Parrafo.TRABAJO to "A esta hora está fuera, agarrada al casco con las dos manos y las dos cuerdas, avanzando hacia la antena como un insecto por una pared. Debajo de sus pies no hay nada en billones de kilómetros. No mira. Mira la antena. Aprieta un tornillo. Aprieta otro.",
            Parrafo.PENSAMIENTO to "La astronauta piensa, al volver dentro, con las piernas temblando, que ahí fuera no hay arriba ni abajo, ni ruido, ni viento, ni nada que diga que estás vivo salvo tu respiración. Piensa que nunca había estado tan sola. Piensa que nunca había estado tan bien.",
        ),
        "aprendiz" to unDia(
            Parrafo.MANANA to "Hoy le han asignado a la astronauta una aprendiz: Sana, una muchacha de veinte años que subió a la nave con cinco y que no se acuerda de la Tierra más que por un olor de jazmín. Llega a la sala de máquinas con un cuaderno nuevo y una cara muy seria.",
            Parrafo.TRABAJO to "A esta hora le enseña a Sana la sala de máquinas, pantalla por pantalla, tubo por tubo, diciéndole qué no debe tocar nunca, qué puede tocar con permiso y qué tiene que tocar si un día ella no está. Sana apunta todo. Pregunta por qué en cada cosa. La astronauta no siempre sabe por qué.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que Sana será la ingeniera de la nave cuando ella ya no pueda, y que quizá sea la que la lleve hasta el final. Piensa que le está dando su trabajo, poco a poco, como se da una herencia. Piensa que tiene que enseñárselo todo. Hasta lo que no sabe.",
        ),
        "fuga" to unDia(
            Parrafo.MANANA to "Hoy, temprano, ha saltado la alarma de un tanque de agua del anillo: una fuga en una soldadura, que pierde un litro cada minuto. En la nave el agua es la vida, y un litro por minuto es mucha vida. Todos los ingenieros, a la sala de máquinas.",
            Parrafo.TRABAJO to "A esta hora está metida en el hueco del tanque, con el agua hasta los tobillos y una lámpara en la frente, sellando la soldadura con un parche caliente mientras alguien achica con un cubo. Tarda una hora. Al final, la gota deja de caer. Se queda sentada en el agua un rato.",
            Parrafo.PENSAMIENTO to "La astronauta piensa en los litros perdidos, que el sistema recogerá del suelo y volverá a limpiar, porque en la nave no se pierde nada del todo. Piensa que es la nave más tacaña de la historia. Piensa que por eso siguen vivos.",
        ),
        "planeta" to unDia(
            Parrafo.MANANA to "Hoy los astrónomos de a bordo han enseñado en el comedor una foto nueva del telescopio de proa: junto a la estrella de delante, un punto pequeño que no es una estrella. Un planeta. No se sabe cómo es. Se sabe que está ahí, y que va hacia donde van.",
            Parrafo.GENTE to "Esta tarde no se habla de otra cosa: los niños dibujan el planeta con árboles y ríos; los mayores discuten si tendrá agua, aire, algo vivo. Ade dice que si tiene tierra, él la sembrará. Bao dice que si tiene bichos, él los curará. La astronauta no dice nada. Mira la foto.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que ese punto es el final del viaje, y que ella, si llega, tendrá más de setenta años. Piensa que no sabe si lo pisará. Piensa que da igual: lo ha visto, ahora, en una foto, y es suyo un poco. Como un pueblo que se ve desde el tren y no se visita.",
        ),
        "nace_nino" to unDia(
            Parrafo.MANANA to "Hoy ha nacido un niño a bordo, el séptimo del viaje, en la enfermería, de madrugada, con Bao y dos parteras de la granja. Pesa tres kilos. Tiene mucho pelo. Toda la nave lo sabe antes del desayuno. En el tablón del comedor alguien ha dibujado una cuna.",
            Parrafo.GENTE to "Esta tarde se hace la fila para verlo, en la puerta de la enfermería, de dos en dos, sin hacer ruido. La astronauta le lleva una manta tejida con hilo de la granja. El niño duerme con los puños cerrados. No sabe que ha nacido entre dos estrellas.",
            Parrafo.PENSAMIENTO to "La astronauta piensa que ese niño llegará a Alfa Centauri con treinta años, y que la Tierra será para él un cuento de los viejos. Piensa que él es para lo que vino. Piensa que ella es para él. Lo piensa sin tristeza. Le parece justo.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "astronauta", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + PARADA_MANANA + INGRAVIDEZ_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + PARADA_TRABAJO + INGRAVIDEZ_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + PARADA_PUEBLO + INGRAVIDEZ_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + PARADA_ANOCHECER + INGRAVIDEZ_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
