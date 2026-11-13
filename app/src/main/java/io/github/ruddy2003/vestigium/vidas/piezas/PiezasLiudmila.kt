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
 * El cuento de Liudmila, obrera textil en Moscú, de 1975 a 1979. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el
 * autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día tiene dos tramos de telar, dos de camino (el metro de ida y, a la vuelta, las colas) y dos de casa (antes de
 *   salir y al volver): esos textos valen a cualquiera de sus horas y no dicen qué comida toca.
 * - Un estado: «sanatorio» (temporadas 2 y 4, tres semanas en el mar Negro; en la 4, con Lena).
 * - La nave de telares lee el arco «plan» (lo cumplido del plan del año, que se corre en diciembre).
 * - Las fiestas de guardar (el año nuevo, el 8 de marzo, los dos días de mayo, la Victoria, la Constitución y los dos de
 *   noviembre) tienen su párrafo de «no trabaja».
 * - No repite lo que ya dice «Su vida»: las 41 horas, las estaciones del metro como palacios, el pan, la leche y las
 *   salchichas de la cola, lo barato de los libros, los doce años menos de los hombres.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG6.kt`):
 * - Galina Petrovna, la suegra, vive con ellos; enferma desde `suegra_enferma` y muere en `suegra_muere` (temporada 4).
 * - El piso de dos cuartos hasta `mudanza` (temporada 3); desde entonces, el de tres cuartos en un barrio nuevo.
 * - Tamara Ivánovna es la jefa de turno hasta `jefa` (temporada 5); desde entonces, la jefa es Liudmila.
 * - Zina y Lena salen todos los años: sus textos de todos los días no dicen si Zina está casada ni qué edad tiene Lena.
 * - Las fiestas que vuelven cada año no nombran a Galina Petrovna ni a Tamara Ivánovna.
 */
internal object PiezasLiudmila {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(11, 12, 1, 2, 3)

    private val MANANA = listOf(
        v("A {nombre} la despierta el despertador de cuerda, un Slava que salta en la mesilla como un sapo, y lo apaga antes del segundo timbre para no despertar a nadie. Se levanta a oscuras. En la cocina, enciende el gas con una cerilla. La llama azul es lo primero del día."),
        v("{nombre} se despierta un minuto antes del despertador, como casi siempre, y se queda escuchando la casa: la respiración de Volodia, el agua en las tuberías, la radio del vecino. Se levanta. El suelo está frío. Busca las zapatillas con el pie."),
        v("{nombre} se despierta con la radio de la cocina, que no se apaga nunca del todo, porque en la pared hay un enchufe para ella sola: la gimnasia de la mañana, con un piano, y una voz que dice uno, dos, tres. Nadie hace la gimnasia. Pero suena."),
        v("Lo primero que hace {nombre} al levantarse es mirar por la ventana el termómetro clavado en el marco por fuera: hoy marca menos doce. Se pone las medias de lana debajo de las otras, el chal, el abrigo, el gorro. Vestirse en invierno es como hacer una maleta.", meses = INVIERNO),
        v("{nombre} se despierta con el sol ya en la ventana, porque en verano en Moscú casi no hay noche, y el cielo se pone claro antes de las cuatro. Ha dormido mal, como todos en junio. En el patio, los gorriones gritan. Pone la tetera. Mira por la ventana. Ya hace calor.", meses = setOf(6, 7)),
        v("A {nombre} la despierta Lena, que tiene una pesadilla y se ha metido en su cama sin decir nada, con los pies helados. {nombre} la abraza un momento. Luego se levanta despacio para no despertarla, y la tapa bien. Todavía le quedan veinte minutos a Lena. Que los duerma."),
        v("{nombre} se despierta con dolor de espalda, como cada vez más a menudo, de estar de pie entre los telares. Se estira en la cama, despacio, como le enseñó la médica de la fábrica. Cruje algo. Se levanta. En el pasillo, el olor del té de anoche, frío en la tetera."),
    )

    /** Tres semanas en un sanatorio del mar Negro, en las temporadas 2 y 4 (estado «sanatorio»). */
    private fun sa(texto: String, hacer: Hacer? = null) = v(texto, estado = "sanatorio", hacer = hacer)

    private val SANATORIO_MANANA = listOf(
        sa("{nombre} se despierta en el sanatorio, en un cuarto blanco con otras tres mujeres de otras fábricas, y por la ventana abierta entra el ruido del mar. No sabe qué hacer con las manos: nadie le pide nada. Una enfermera golpea la puerta. Es la hora de tomarse la tensión."),
        sa("{nombre} se despierta con el sol del sur en la cara, un sol que en Moscú no conoce nadie, y huele a pino, a sal y a magnolia. Sale al balcón en camisón. Abajo, el mar, azul, quieto. Los cipreses parecen velas. Se queda mirando mucho rato. Luego se acuerda de vestirse."),
        sa("A {nombre} la despierta el altavoz del sanatorio, que pone una marcha y luego anuncia la gimnasia en la terraza. Aquí sí la hacen todas, en fila, en bata, levantando los brazos al sol mientras un instructor cuenta. {nombre} se ríe de sí misma. Levanta los brazos."),
    )

    private val SANATORIO_TRABAJO = listOf(
        sa("A esta hora le toca un baño de lodo en el pabellón de los tratamientos: la tumban en una camilla y la cubren de un barro negro y caliente que huele a azufre, y la dejan así veinte minutos, mirando el techo. Dicen que es bueno para la espalda. La espalda no dice nada.", Hacer.TRABAJO),
        sa("A esta hora está en la playa de piedras, tumbada en una toalla, con un pañuelo en la cabeza y un periódico doblado sobre los ojos. A su lado, cien mujeres rojas como cangrejos. Los altavoces dicen que no se esté más de media hora al sol. Nadie hace caso.", Hacer.TRABAJO),
        sa("A esta hora se baña en el mar, despacio, entrando de puntillas sobre las piedras, con el agua tibia como una sopa. Flota boca arriba. El cielo es enorme. No hay telares, ni colas, ni suegra, ni plan. Hay agua. Se queda flotando hasta que se le arrugan los dedos.", Hacer.TRABAJO),
        sa("A esta hora pasea por el paseo de las palmeras con dos compañeras de cuarto, una maestra de Kazán y una tranviaria de Leningrado, comiendo helado de un vaso de papel. Se cuentan sus vidas como se cuentan en los trenes: todo, porque no se van a volver a ver.", Hacer.CAMINO),
        sa("A esta hora come en el comedor del sanatorio, en mesas de cuatro con mantel blanco, lo que dice la dieta que le pusieron al llegar: sopa de verduras, pescado hervido, puré, compota. No hay que hacer cola ni fregar. Se le hace raro hasta la segunda semana.", Hacer.COMER),
        sa("A esta hora duerme con la ventana abierta y el ruido del mar, que es el mejor sueño del año. Ni ronquidos, ni tuberías, ni despertador. Una de las compañeras de cuarto habla en sueños, en tártaro. {nombre} no entiende nada. Le parece una canción.", Hacer.DORMIR),
        sa("A esta hora lava su ropa en el lavabo del cuarto y la tiende en el balcón, y se lava el pelo con un champú búlgaro que compró en el quiosco del sanatorio. Se mira en el espejo. Está morena. Hace años que no se ve morena. Le parece que tiene otra cara.", Hacer.CUIDAR),
        sa("A esta hora hay baile en la terraza del sanatorio, con un acordeón y un tocadiscos, y las mujeres bailan entre ellas porque hay pocos hombres, y los que hay son viejos. {nombre} baila un vals con la tranviaria de Leningrado. Se pisan. Se ríen. Bailan otro.", Hacer.LIBRE),
    )

    private val SANATORIO_PUEBLO = listOf(
        sa("El sanatorio es un edificio blanco con columnas, como un palacio de los de antes, que ahora es de un sindicato. Tiene jardines con palmeras, una escalera hasta la playa, salas de tratamiento, un cine de verano y un busto de Lenin en la entrada, con flores frescas."),
        sa("La costa del mar Negro, en verano, es de los sindicatos: sanatorios, casas de reposo, campamentos de pioneros, uno al lado del otro, llenos de obreros de todo el país que vienen tres semanas al sol. Se oyen todas las lenguas de la Unión. Todas se ríen igual."),
    )

    private val SANATORIO_ANOCHECER = listOf(
        sa("Al caer la tarde {nombre} baja a la playa a ver ponerse el sol en el mar, cosa que no había visto nunca hasta que vino aquí. El sol se mete en el agua, rojo, enorme, y la gente aplaude. {nombre} también aplaude. Le da vergüenza. Aplaude igual."),
        sa("Anochece, y en el cine de verano del sanatorio ponen una comedia que ya ha visto dos veces en Moscú. La ve otra vez, sentada en un banco de madera bajo las estrellas, con los grillos más altos que la película. Se ríe en los mismos sitios. Le parece más graciosa aquí."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} El tiempo, aquí, no se comenta: se aguanta."),
        v("{nombre} mira el cielo al salir del portal, por si hay que volver por el paraguas. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Su madre, en las cartas, dice que el tiempo de ahora no es como el de antes."),
        v("{cielo} En {estacion} la ciudad cambia de color y de olor. {nombre} lo nota en la nariz antes que en los ojos."),
        v("{de_estacion} {cielo} La radio dice la temperatura cada hora, con décimas, como si fuera importante. Lo es."),
        v("El cielo, para una obrera de telar, es lo que se ve entre la casa y la fábrica. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y muy alto, de un azul pálido, como lavado muchas veces.",
            "Hace un día claro, y el sol hace brillar todas las ventanas a la vez.",
            "No hay nubes, y el aire está tan quieto que se oye un tren a lo lejos.",
        ),
        Clima.NUBES to listOf(
            "El cielo está gris y bajo, del color del hormigón, y no se sabe qué hora es.",
            "Pasan nubes pesadas y lentas, como camiones cargados.",
            "Está nublado, con una luz plana que no hace sombra.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y la gente camina deprisa con paraguas negros, todos iguales.",
            "Cae una lluvia fina y fría que se mete por el cuello del abrigo.",
            "Llueve, y los charcos se juntan en las aceras hasta hacer lagos.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta de verano, con truenos que hacen temblar los cristales.",
            "Truena, y cae un aguacero tan fuerte que la gente se mete en los portales.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y la nieve cae recta y callada, y lo tapa todo de blanco en una hora.",
            "Cae nieve seca, que cruje bajo las botas como azúcar.",
            "Nieva desde anoche, y las barrenderas con sus palas no dan abasto.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, y las farolas son manchas amarillas que flotan en el aire.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento helado que levanta la nieve del suelo y la tira a los ojos.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando la nieve se derrite y las calles son ríos de agua sucia.",
            "En primavera se abren las ventanas por primera vez y se quitan las tiras de papel de las rendijas.",
            "Es el tiempo de los charcos, de las botas de goma y de los primeros brotes en los árboles.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y las noches son tan cortas que el cielo no llega a ponerse negro.",
            "En verano la ciudad se queda medio vacía: los niños en los campamentos, los viejos en las dachas.",
            "Es el tiempo del kvas en los camiones cisterna de las esquinas.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando los árboles se ponen amarillos y el barro vuelve a las aceras.",
            "En otoño se hacen las conservas: pepinos, setas, mermeladas, en tarros por toda la cocina.",
            "Es el tiempo de las primeras heladas y de las calefacciones que todavía no se encienden.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando oscurece antes de salir de la fábrica y amanece después de entrar.",
            "En invierno los niños patinan en los patios, que los conserjes riegan para hacer pistas de hielo.",
            "Es el tiempo de los gorros de piel, del vapor que sale de la boca y de los carámbanos de los tejados.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora atiende sus ocho telares, que van todos a la vez, con un ruido que no deja oír nada: va de uno a otro por el pasillo, mira los hilos, para una máquina, anuda un hilo roto con un nudo de tejedora, rápido, sin mirar, y la vuelve a poner en marcha. Ocho máquinas. Dos manos.", hacer = Hacer.TRABAJO),
        v("A esta hora cambia la lanzadera de un telar, que se ha quedado sin hilo, por otra llena, sin parar la máquina, en el segundo justo, como una malabarista. Luego mira la tela que sale: un algodón de flores azules que alguien se pondrá un domingo.", hacer = Hacer.TRABAJO),
        v("A esta hora habla con Zina por señas, entre los telares, porque con el ruido no se oye ni un grito: un gesto quiere decir «se rompió», otro «llama al mecánico», otro «¿qué hay en la tienda?». Las tejedoras tienen un idioma de manos que no está escrito en ningún sitio.", hacer = Hacer.TRABAJO),
        v("A esta hora espera al mecánico de su sección, que se toma su tiempo, porque un telar se ha atascado y ella no puede hacer más que mirar. Cada minuto parado es tela que no se hace, y la tela que no se hace no cuenta para el plan ni para la prima. Por fin llega.", hacer = Hacer.TRABAJO),
        v("A esta hora barre el polvo de algodón que se acumula debajo de los telares, una pelusa blanca que flota en la nave como nieve y que se mete en la nariz, en el pelo, en los pulmones. Las tejedoras viejas tosen. {nombre} se ata el pañuelo más fuerte.", hacer = Hacer.TRABAJO),
        v("A esta hora hay reunión de la brigada al pie de los telares, con el jefe de la nave leyendo un papel: cuánto se ha hecho, cuánto falta, qué brigada va primera. La suya va segunda. Hay que apretar. Las mujeres se miran. Vuelven a los telares.", hacer = Hacer.TRABAJO),

        v("A esta hora come en el comedor de la fábrica, en una mesa larga de formica con las compañeras de la brigada: sopa de col, una albóndiga con trigo sarraceno, pan negro y un vaso de compota de frutas secas. Cuesta menos de un rublo. Se come deprisa y se habla más deprisa.", hacer = Hacer.COMER),
        v("A esta hora come en el comedor, de pie junto a la ventana, porque las mesas están llenas, un plato de borsch con una cucharada de crema agria y un trozo de pan. Mira el patio de la fábrica, la chimenea, el cartel con la cara de Lenin. Tiene sueño. El borsch está bueno.", hacer = Hacer.COMER),
        v("A esta hora come lo que se ha traído de casa, en el vestuario, con Zina: unos pepinos en salmuera, un huevo duro, pan con manteca, y un té del termo que se pasan la una a la otra. Zina le cuenta un chiste político bajito. {nombre} se ríe con la boca cerrada.", hacer = Hacer.COMER),

        v("A esta hora va en el metro, de pie, apretada entre abrigos que huelen a nieve y a tabaco, con la bolsa contra el pecho. Nadie habla. Muchos leen: un libro, un periódico doblado en cuatro, una revista. El tren corre por el túnel como un río subterráneo. Cuatro estaciones.", hacer = Hacer.CAMINO),
        v("A esta hora está en una cola delante de una tienda de comestibles, sin saber qué venden: ha visto una cola y se ha puesto, como todos, porque si hay cola es que hay algo. Pregunta a la de delante. Dice que naranjas. O que eran naranjas. {nombre} se queda. Ya se verá.", hacer = Hacer.CAMINO),
        v("A esta hora hace tres colas en la misma tienda: una para elegir y que le den el papel, otra en la caja para pagar, otra en el mostrador para recoger con el papel pagado. Una hora para un kilo de salchichón y una lata de guisantes. La de la caja es la peor. Siempre.", hacer = Hacer.CAMINO),
        v("A esta hora sube por la escalera mecánica del metro, larguísima, tan honda que no se ve el final, y lee los anuncios de los teatros que pasan despacio. Abajo, el tren. Arriba, el frío. En la escalera, un rato en que no hay que hacer nada. Es su descanso.", hacer = Hacer.CAMINO),

        v("A esta hora pone la casa en marcha: hierve el agua, hace la kasha de trigo sarraceno en el cazo, corta el pan, despierta a Lena dos veces, le peina las trenzas, le busca el otro calcetín. Todo a la vez, en una cocina de seis metros. Como un telar, pero con gente.", hacer = Hacer.CUIDAR),
        v("A esta hora lava la ropa en la bañera, a mano, con jabón de lavar marrón que no hace espuma, y la tiende en una cuerda sobre la bañera misma, porque no hay otro sitio. Las camisas de Volodia, las medias, el uniforme de Lena. Mañana estarán secas. O casi.", hacer = Hacer.CUIDAR),
        v("A esta hora cocina para mañana: una olla de sopa de col que durará tres días, unas patatas, unas albóndigas con más pan que carne. La suegra pela las patatas sentada a la mesa y le dice cómo lo haría ella. {nombre} no contesta. Remueve la sopa.", hacer = Hacer.CUIDAR, hasta = "suegra_enferma"),
        v("A esta hora ayuda a Lena con los deberes en la mesa de la cocina, apartando los platos: un problema de trenes, una poesía de Pushkin que hay que aprender de memoria. Lena se equivoca en el mismo verso tres veces. {nombre} también lo aprendió así.", hacer = Hacer.CUIDAR),

        v("A esta hora duerme al lado de Volodia, que ronca como un tractor viejo. Antes, ella le daba codazos. Ahora ya no le despierta. Duerme hondo, sin soñar, como duermen las que se levantan antes de que salga el sol.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con los telares: que se rompen todos los hilos a la vez y ella no da abasto. Se despierta con las manos moviéndose sobre la manta. Volodia le pregunta qué pasa. Ella le dice que nada, que los hilos. Él dice ah. Se duermen.", hacer = Hacer.DORMIR),
        v("A esta hora duerme con un pañuelo en la cabeza, porque por la ventana entra un hilo de aire frío que no tapan ni las tiras de papel de las rendijas. La calefacción está fuerte, demasiado, y hay que abrir el ventanuco para respirar. Así cada invierno: o calor, o frío.", hacer = Hacer.DORMIR),

        v("A esta hora ve la tele con la familia, apretados en el sofá: las noticias, con cosechadores que superan el plan, y luego una película de guerra que ya han visto. Volodia se duerme a los diez minutos. Lena protesta porque quiere ver otra cosa. No hay otra cosa.", hacer = Hacer.LIBRE),
        v("A esta hora lee en la cocina, con la lámpara encendida y un vaso de té, un libro que le ha prestado Zina por tres días, envuelto en papel de periódico para que no se vea qué es. Lo lee deprisa. Hay que devolverlo el viernes. Hay otras cinco esperándolo.", hacer = Hacer.LIBRE),
        v("A esta hora teje un jersey para Lena con lana que deshizo de otro jersey viejo, con las agujas haciendo clic, clic, mientras oye la radio: un concierto, una novela leída por un actor, un programa de preguntas. Las manos tejen solas. Están acostumbradas a los hilos.", hacer = Hacer.LIBRE),
    )

    private val PUEBLO = listOf(
        v("{entorno} La nave de telares es enorme, con techos de cristal sucio y cientos de máquinas en filas, todas haciendo el mismo ruido a la vez, un trueno que no para. Muchas tejedoras, al cabo de los años, ya no oyen bien. Hablan alto en casa."),
        v("La fábrica es una ciudad pequeña dentro de la ciudad: tiene su comedor, su policlínica, su biblioteca, su club con cine, su guardería, su periódico de pared, su sindicato y su lista de espera para todo, desde un piso hasta una nevera. {entorno}"),
        v("{entorno} En el patio de la fábrica, en un tablero de madera pintado de rojo, están las fotos de las mejores obreras del año, en blanco y negro, con su nombre debajo. Es el tablero de honor. La foto de {nombre} estuvo allí una vez."),
        v("Moscú, de camino, es ancho, gris y lleno de gente: avenidas de ocho carriles con pocos coches, edificios grandes como montañas, carteles rojos con letras blancas que dicen que el Partido y el pueblo son uno. Nadie los lee. Están ahí como el cielo. {entorno}"),
        v("{entorno} La tienda de comestibles del barrio tiene un letrero que dice solo eso, comestibles, y dentro, latas de pescado en pirámides, botes de pepinos y unas vendedoras con bata blanca que miran a los clientes como si debieran algo."),
        v("En el metro de Moscú cada estación es distinta, y {nombre} las conoce por los techos, porque de pie, apretada, es lo único que ve: un mosaico con aviones, otro con espigas de trigo, otro con soldados, una lámpara de bronce. Las conoce mejor que su propia casa. {entorno}"),
        v("La casa donde viven es un edificio viejo de cinco pisos sin ascensor, con un patio de tierra, unos árboles, un banco de viejas que saben todo de todos y una escalera que huele a gato y a col. El piso tiene dos cuartos y una cocina, para cuatro. Se apañan.", hasta = "mudanza"),
        v("El piso nuevo está en un barrio que hace cinco años era un campo: bloques blancos de nueve pisos, todos iguales, en filas, con barro entre medias y árboles recién plantados que todavía son palos. Tiene tres cuartos. Lejos del metro. Pero tres cuartos.", desde = "mudanza"),
    )

    /** Lo cumplido del plan del año en la fábrica (arco «plan»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("plan", 0.0, 0.25, frases = listOf(
            "En el cartel del plan, a la entrada de la nave, la raya roja del año todavía está abajo del todo.",
            "El año del plan acaba de empezar, y la fábrica va con calma, como un tren que arranca.",
        )),
        Entorno("plan", 0.25, 0.6, frases = listOf(
            "La raya roja del cartel del plan va por la mitad, y el jefe de la nave dice que van bien. Siempre dice que van bien.",
            "En la nave hay carteles nuevos de la emulación: qué brigada va primera, cuál va última.",
        )),
        Entorno("plan", 0.6, 0.9, frases = listOf(
            "La raya roja del cartel del plan sube, pero no tanto como debería, y en la oficina hay nervios.",
            "Se empieza a hablar de turnos de sábado, porque al plan le falta y el año se acaba.",
        )),
        Entorno("plan", 0.9, 1.0, frases = listOf(
            "La fábrica corre para cumplir el plan: turnos dobles, sábados, nadie se va a casa a su hora.",
            "En el cartel del plan la raya roja está casi arriba, y por los pasillos corren los jefes con papeles.",
        )),
    )

    private val GENTE = listOf(
        v("Volodia, su marido, es tornero en una fábrica de motores, un hombre grande y callado con unas manos que lo arreglan todo, menos a sí mismo. Es bueno. Bebe. No todos los días, pero cuando bebe, bebe hasta el fondo. {nombre} lo sabe. Lo quiere igual."),
        v("Lena, su hija, va a la escuela del barrio con su uniforme marrón y su delantal negro, y los días de fiesta, el delantal blanco. Es lista y contestona. Quiere ser médica, o cosmonauta, o actriz, según el día. {nombre} quiere que sea lo que no sea ella."),
        v("Zina es su compañera de telar, la de la máquina de al lado desde hace diez años, una mujer pequeña y rubia de Ivánovo que se ríe de todo y cuenta chistes que no se deben contar. Si {nombre} se pone enferma, Zina atiende sus telares. Y al revés."),
        v("Su madre vive en un pueblo cerca de Riazán, en la casa de madera donde nació {nombre}, con una vaca, un huerto y un pozo. Le manda patatas y tarros de mermelada en el tren, por un conocido, y una carta que siempre termina igual: que si come bien, que si Volodia bebe."),
        v("Las de su brigada son once mujeres de todas las edades, de todas las ciudades, que pasan juntas más horas que con sus maridos. Se saben las vidas de todas: los hijos, los abortos, las suegras, las borracheras. Se ayudan. Se pelean. Se prestan dinero hasta la paga."),
        v("Galina Petrovna, la suegra, vive con ellos desde que se quedó viuda, en la guerra. Tiene su cama, su icono escondido detrás de un libro y su sitio en la mesa. Lleva la casa y cría a Lena a su manera. {nombre} trabaja. La suegra manda. Así se reparten la casa.", hasta = "suegra_enferma"),
        v("Galina Petrovna, la suegra, ya no se levanta de la cama, y el médico del distrito viene cada semana, escribe una receta y se va encogiéndose de hombros. {nombre} le da la sopa a cucharadas al volver de la fábrica. La suegra le da las gracias. Nunca se las había dado.", desde = "suegra_enferma", hasta = "suegra_muere"),
        v("Desde que murió Galina Petrovna, la casa está más grande y más callada. {nombre} encuentra todavía sus cosas en los cajones: un dedal, un rosario escondido, unas cartas de su marido desde el frente. Volodia no quiere tirarlas. {nombre} las guarda en una caja.", desde = "suegra_muere"),
        v("Tamara Ivánovna es la jefa de turno, una mujer seca con el pelo teñido de color cobre y una insignia del Partido en la solapa. Es justa, que no es lo mismo que buena. Le tiene ley a {nombre}, porque nunca falla. Se lo dice una vez al año, el 8 de marzo.", hasta = "jefa"),
        v("Desde que es jefa de turno, {nombre} tiene una mesa pequeña al fondo de la nave, una carpeta de papeles y treinta mujeres a su cargo, entre ellas Zina, que la llama camarada jefa para hacerla rabiar. Gana veinte rublos más. Duerme peor.", desde = "jefa"),
    )

    private val OBJETO = listOf(
        v("En Moscú cada cosa cuesta una cola, y por eso cada cosa se cuida. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de su madre, en el pueblo, que lo aprendió de la suya."),
        v("Hoy {nombre} se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} En los periódicos de fuera dicen cosas de Moscú. No dicen eso."),
        v("{dato_objeto} Todo el mundo lo sabe, y por eso nadie lo dice."),
        v("Una casa de Moscú se hace con pocas cosas que duran mucho. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("bolsa", "la bolsa de red", "una bolsa de red", "La bolsa",
            "La bolsa de red es una malla de hilo que se dobla hasta caber en un puño y que todo el mundo lleva en el bolsillo; la llaman la «por si acaso», porque nunca se sabe cuándo van a sacar algo a la venta; llena, se ve lo que se lleva, y los demás preguntan dónde."),
        ObjetoCuento("moneda", "la moneda de cinco kopeks", "una moneda de cinco kopeks", "La moneda",
            "Con una moneda de cinco kopeks se entra en el metro de Moscú, por un torniquete que se la traga, y se va a cualquier estación, por lejos que esté; con una de tres se toma un vaso de gaseosa con sirope en las máquinas de la calle, que tienen un solo vaso de cristal para todos."),
        ObjetoCuento("libro", "el libro", "un libro", "El libro",
            "Los libros buenos se acaban el día que salen, y se consiguen cambiando papel viejo por vales en los puntos de reciclaje, o por amistades; los que no se pueden imprimir circulan copiados a máquina, en papel de cebolla, y se leen en una noche."),
        ObjetoCuento("abrigo", "el abrigo", "un abrigo de invierno", "El abrigo",
            "El abrigo de invierno es de paño grueso con un cuello de piel, de conejo o de algo que parece conejo, y dura diez o quince años; se compra una vez, se arregla muchas, se le cambia el forro, se le da la vuelta a la tela cuando se gasta por fuera."),
        ObjetoCuento("botas", "las botas", "unas botas de invierno", "Las botas",
            "Las botas de invierno buenas son de importación, de Yugoslavia, de Checoslovaquia o de Finlandia, y por ellas se hacen colas de horas; las del país aguantan, pero aprietan; las de fieltro, de los abuelos, son las únicas que no dejan pasar el frío."),
        ObjetoCuento("radio", "la radio de la cocina", "una radio de cable", "La radio",
            "En la pared de cada cocina hay una radio de cable, una cajita con una rueda para el volumen y sin rueda para cambiar: tiene uno o tres programas, los de la radio de la Unión, y no se apaga nunca del todo; da la hora, el parte y la gimnasia."),
        ObjetoCuento("televisor", "el televisor", "un televisor", "El televisor",
            "El televisor es un mueble pesado de madera, en blanco y negro, que tarda un minuto en calentarse y que a veces hay que golpear por un lado; tiene dos o tres canales, y a las nueve todos dan las noticias; encima, un tapete de ganchillo y un jarrón."),
        ObjetoCuento("samovar", "el samovar", "un samovar", "El samovar",
            "El samovar de la familia es de latón, eléctrico ya, aunque el de la abuela era de carbón y está en el armario; encima se pone la tetera con el té muy cargado, y del grifo de abajo sale el agua caliente para aclararlo; se bebe en vaso, con azúcar o con mermelada."),
        ObjetoCuento("termo", "el termo", "un termo", "El termo",
            "El termo chino, con flores pintadas en el costado, mantiene el té caliente todo el día; es lo único chino que hay en todas las casas, de antes de que se pelearan los dos países; si se cae, se rompe el cristal de dentro, y no hay otro."),
        ObjetoCuento("chal", "el chal", "un chal de lana", "El chal",
            "El chal de lana de Pávlovski Posad es negro o rojo, con flores grandes estampadas y flecos; las abuelas se lo ponen en la cabeza para ir a la iglesia, las madres sobre los hombros para las fiestas, y las hijas no se lo ponen, hasta que se lo ponen."),
        ObjetoCuento("pase", "el pase de la fábrica", "un pase de fábrica", "El pase",
            "El pase de la fábrica es una libreta pequeña de tapas rojas con la foto, el nombre y la sección; se enseña cada mañana al vigilante de la puerta, que lo mira sin mirarlo; sin pase no se entra, aunque el vigilante te conozca desde hace diez años."),
        ObjetoCuento("postal", "la postal", "una postal", "La postal",
            "Las postales de las fiestas se mandan por docenas, al año nuevo, al 8 de marzo, al 1 de mayo, al 7 de noviembre: un Kremlin con estrellas, unas mimosas, un cosmonauta, un abeto; se escriben siempre con las mismas palabras, salud, felicidad, paz, cielo limpio sobre la cabeza."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas un momento, como se piensa en el tiempo de otra ciudad, y luego se le van, porque lo de ella es la tela, la cola, la cena de mañana y los deberes de Lena. A sus {edad} años, el mundo grande le queda lejos. El pequeño, encima."),
        v("Piensa en su pueblo, en la casa de madera, en el río donde se bañaba de niña, en su madre ordeñando la vaca a oscuras. Se vino a Moscú a los diecisiete, a la escuela de la fábrica, con una maleta de cartón. No ha vuelto a vivir allí. No volverá. Pero piensa."),
        v("{mundo} {nombre} piensa que su vida es mejor que la de su madre: tiene piso, tiene agua caliente, tiene médico, tiene una hija que irá al instituto. Piensa que es verdad. Piensa también en las colas, en Volodia, en el plan. Piensa que las dos cosas son verdad."),
        v("{mundo} A veces, entre los telares, con el ruido que no deja pensar en nada, {nombre} piensa en nada, y es lo mejor del día. Las manos hacen solas. La cabeza se va. Al ruido se acostumbra uno como al frío. Luego, en casa, el silencio le zumba."),
        v("Piensa en el dinero: lo que gana ella, lo que gana Volodia, lo que se gasta en la tienda, lo que se ahorra en la caja de ahorros para una alfombra o un coche que no llegará. Las cuentas de la casa las lleva ella. Las de Volodia, la botella."),
        v("{mundo} A sus {edad} años, {nombre} sabe que no va a ser otra cosa que tejedora, y no le pesa: es buena en lo suyo, tiene su foto en el tablero de honor, la respetan. Pero piensa en Lena, y quiere para ella una mesa, un libro, una bata blanca. No un telar."),
    )

    private val MUNDOS = mapOf(
        "distension" to listOf(
            "Se habla de la distensión con los americanos, que ahora sonríen en las fotos con los nuestros.",
            "Se dice que en la tienda de la esquina van a traer naranjas de Marruecos para las fiestas.",
            "Se habla de la cosecha de este año, que en la tele es buenísima y en la tienda no se nota.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} vuelve a casa con la bolsa de red llena y los brazos cansados, por la avenida, entre miles de personas que hacen lo mismo. Las ventanas de los bloques se encienden una a una, como las teclas de una máquina de calcular. En cada una, una cocina."),
        v("Anochece temprano, y la ciudad se queda azul, luego negra, con las farolas naranjas y la nieve brillando debajo. En el patio, los niños todavía patinan. {nombre} llama a Lena desde la ventana. Lena dice cinco minutos. Son veinte.", meses = INVIERNO),
        v("Al caer la tarde, en la cocina, se reúnen todos alrededor de la mesa, apretados, con la radio puesta, a tomar té con mermelada. Se cuenta el día. Volodia cuenta lo de su fábrica. Lena, lo de la escuela. {nombre} escucha. Lo suyo no lo cuenta: es siempre lo mismo."),
        v("De noche, al salir del metro, se ven a lo lejos las estrellas rojas del Kremlin, encima de todo, encendidas siempre. {nombre} las ha visto mil veces y las mira igual. Le gustan. No sabe por qué. Son como un faro en una ciudad sin mar."),
        v("Anochece, y en el portal las viejas del banco se retiran a sus casas, comentando a quién han visto entrar y con qué. {nombre} las saluda al pasar. Le preguntan por la niña, por el marido, por la fábrica. Contesta poco. Mañana sabrán más que ella."),
        v("La última luz del día se queda en lo alto de la torre de la televisión de Ostánkino, que se ve desde todas partes, con su luz roja en la punta. {nombre} sube la escalera con la bolsa. Le pesa. Arriba, la puerta, la llave, el olor de la casa. Ya está. Ya es de noche."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))
    private fun dosDias(primero: Map<Parrafo, String>, segundo: Map<Parrafo, String>) = TextoEspecial(listOf(primero, segundo))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, la fiesta más grande de la casa, y {nombre} se ha acostado a las tres, después de la medianoche con el champán soviético, las mandarinas, la ensalada con mayonesa en la ensaladera grande y el discurso en la tele. Lena se quedó dormida en el sofá antes de las doce. Como cada año.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es año nuevo, y en toda la Unión no trabaja nadie. Se come lo que sobró de anoche, que es mucho, porque en año nuevo se pone en la mesa más de lo que cabe. Se ve en la tele el concierto de anoche, repetido. Se duerme a ratos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza y le pide pocas cosas: que no haya guerra, que Volodia beba menos, que Lena saque buenas notas, que le toque algo de la lista. Piensa que se pide lo mismo cada año. Piensa que algo, cada año, se cumple.",
        ),
        "ejercito" to unDia(
            Parrafo.MANANA to "Hoy es el día del ejército, la fiesta de los hombres, y en la fábrica las mujeres de la brigada han juntado dinero para regalarles algo a los pocos hombres de la nave: los mecánicos, el electricista, el vigilante. Una colonia, una maquinilla de afeitar, una postal con un tanque.",
            Parrafo.GENTE to "Esta tarde, en casa, {nombre} y Lena le regalan a Volodia unos calcetines de lana y una botella de colonia Triple. Lena le ha hecho una postal en la escuela con un avión y una estrella roja. Volodia hace como que se sorprende. Se pone la colonia. Huele a abuelo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los hombres tienen su día y las mujeres el suyo, el 8 de marzo, y que es justo. Piensa que los otros trescientos sesenta y tres días se reparten de otra manera. Piensa que eso no tiene fiesta. Le hace gracia. No se lo dice a Volodia.",
        ),
        "ocho_marzo" to unDia(
            Parrafo.MANANA to "Hoy es el 8 de marzo, el día de las mujeres, y Moscú huele a mimosa: en todas las esquinas venden ramitos amarillos que han traído del Cáucaso en avión, y los hombres hacen cola para comprarlos, torpes, con el abrigo abierto. Es el único día del año en que los hombres hacen cola.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Volodia ha prometido hacer él la comida, y está en la cocina desde hace dos horas con una receta del periódico y todas las ollas de la casa. Huele a quemado. {nombre} se queda en el sofá, con las mimosas en un vaso, sin entrar. Le cuesta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un día al año la tratan como a una reina y los otros como a una mula, y que la reina también tiene que fregar la cocina después de que Volodia cocine. Piensa que da igual. Las mimosas huelen bien. Hoy es hoy.",
        ),
        "cosmonautas" to unDia(
            Parrafo.MANANA to "Hoy es el día de los cosmonautas, el día en que Gagarin subió al cielo y volvió, y en la radio ponen canciones sobre las estrellas y entrevistas con hombres de uniforme que hablan de otros planetas como si fueran el pueblo de al lado. Lena dice que quiere ser cosmonauta.",
            Parrafo.GENTE to "Esta tarde, en la escuela de Lena, ha habido un acto con un piloto de verdad, que les ha contado cómo se ve la Tierra desde arriba: azul, redonda, sin fronteras. Lena lo cuenta en casa con los ojos muy abiertos. Volodia dice que él, de niño, quería ser piloto. Lena no le cree.",
            Parrafo.PENSAMIENTO to "{nombre} se acuerda de aquel abril, cuando era una muchacha en la escuela de la fábrica y la radio interrumpió todo para decir que un hombre estaba en el espacio. Salieron todos a la calle sin abrigo. Lloraban sin saber por qué. Piensa que eso no se lo puede contar a Lena. No la entendería.",
        ),
        "subbotnik" to unDia(
            Parrafo.MANANA to "Hoy es sábado de trabajo voluntario, el más cercano al cumpleaños de Lenin, y toda la fábrica viene a trabajar gratis: no a los telares, sino a barrer el patio, pintar las vallas, plantar árboles, limpiar las ventanas. Es voluntario. Viene todo el mundo. Así se hace.",
            Parrafo.TRABAJO to "A esta hora pinta de verde la valla del patio de la fábrica con Zina y otras de la brigada, con una brocha gorda y un bote de pintura que no alcanza para todo. Hay música en los altavoces. Hace sol. Se ríen, se manchan, se tiran pintura. Parece una fiesta. Casi lo es.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la historia que cuentan siempre, la de Lenin cargando un tronco en el primer sábado de trabajo voluntario, que viene en todos los libros de la escuela. Piensa que pintar una valla no es cargar un tronco. Pero la valla queda bonita. Eso es verdad.",
        ),
        "pascua_suegra" to unDia(
            Parrafo.MANANA to "Hoy es la Pascua, que no es fiesta en el calendario, y la suegra ha pintado huevos con piel de cebolla, que quedan de color ladrillo, y ha cocido un pan alto y dulce con pasas en una lata de conservas. Lo hace cada año, sin decir nada, sin pedir permiso. Nadie le dice nada.",
            Parrafo.GENTE to "Esta tarde la suegra ha ido a la iglesia, una de las pocas abiertas, con su chal negro en la cabeza, a bendecir el pan y los huevos. Ha vuelto con los ojos brillantes. Dice «Cristo ha resucitado» al entrar, en voz baja, mirando a Lena. Lena contesta lo que le enseñó la abuela, en voz más baja.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que no cree en nada, ni en Dios ni en lo otro, y que se come el pan de Pascua igual, porque está bueno. Piensa que la suegra cree por las dos. Piensa que a lo mejor eso basta. Se come otro trozo.",
        ),
        "primero_mayo" to dosDias(
            mapOf(
                Parrafo.MANANA to "Hoy es el 1 de mayo, la fiesta de los trabajadores, y desde temprano la fábrica forma su columna en el patio para el desfile: banderas rojas, retratos de los dirigentes en palos, globos, flores de papel, una pancarta con el nombre de la fábrica. Hay que ir. Se va contenta.",
                Parrafo.TRABAJO to "Hoy {nombre} no trabaja: desfila. La columna de la fábrica camina horas por las avenidas, parándose, avanzando, hasta pasar por la plaza Roja delante de la tribuna, donde unos señores pequeños con sombrero saludan con la mano. Se grita hurra. Se sigue andando. Se vuelve a casa a pie.",
                Parrafo.PENSAMIENTO to "{nombre} piensa, mientras desfila con un globo en la mano, que esto es lo más parecido a una procesión de las de su pueblo: los mismos pasos lentos, las mismas imágenes en palos, la misma gente vestida de domingo. Solo cambian los santos. Lo piensa y no lo dice.",
            ),
            mapOf(
                Parrafo.MANANA to "Hoy es el segundo día de fiesta de mayo, y Moscú se despierta tarde, con los papeles del desfile todavía en las aceras y los balcones llenos de banderas. {nombre} duerme hasta las nueve, que para ella es el mediodía. Luego, a la cocina: hoy vienen los amigos de Volodia.",
                Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es el segundo día de mayo. Se pone la mesa grande con todo lo que se ha juntado durante semanas: arenque con cebolla, ensalada, pepinos, embutido, empanadillas, una botella de vodka y otra de vino dulce de Georgia. Vienen seis. Comen como doce.",
                Parrafo.PENSAMIENTO to "{nombre} piensa, mirando a Volodia cantar con sus amigos, que hoy está guapo y contento, y que no ha bebido mucho, todavía. Piensa que lo quiere cuando canta. Piensa que mañana es lunes. Recoge los platos antes de que cante más.",
            ),
        ),
        "victoria" to unDia(
            Parrafo.MANANA to "Hoy es el día de la Victoria, y los viejos salen a la calle con sus chaquetas llenas de medallas que tintinean al andar, despacio, del brazo de sus mujeres o de sus nietos. La gente les regala claveles rojos. Ellos los aceptan sin saber qué decir. Algunos lloran.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Va con Lena al parque, donde se reúnen los veteranos de cada regimiento bajo un cartel con el número, buscándose unos a otros. Algunos encuentran a alguien. Se abrazan como niños. Lena le da un clavel a un viejo sin piernas. El viejo le besa la mano.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su padre, que volvió de la guerra sin hablar y no habló nunca de ella, y en el padre de Volodia, que no volvió. Piensa que en todas las familias falta alguien. Piensa que por eso la palabra paz está en todas las postales. No es una palabra. Es un deseo.",
        ),
        "septiembre" to unDia(
            Parrafo.MANANA to "Hoy es el 1 de septiembre, el primer día de clase, y Lena va a la escuela con el uniforme recién planchado, el lazo blanco enorme en el pelo y un ramo de gladiolos para la maestra, tan grande que casi no se le ve la cara. En todas las calles, niños con flores.",
            Parrafo.GENTE to "Esta tarde Lena cuenta cómo ha sido el primer día: la maestra nueva, el compañero nuevo, el pupitre junto a la ventana. Lo cuenta todo deprisa, comiendo, con la boca llena. {nombre} la escucha con la barbilla en la mano. El verano se ha acabado de golpe.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su primer día de escuela, en el pueblo, con un ramo de flores del campo y unos zapatos de su hermana mayor dos números más grandes. La maestra le dijo que tenía buena letra. Es lo único que se acuerda. Piensa que Lena se acordará de otra cosa.",
        ),
        "constitucion" to unDia(
            Parrafo.MANANA to "Hoy es el día de la Constitución, fiesta, y Moscú amanece con banderas rojas en las farolas y en los balcones de los edificios oficiales. Nadie sabe muy bien qué dice la Constitución. Pero es fiesta, y se duerme una hora más, y eso sí se sabe.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Aprovecha para hacer todo lo que no puede hacer los días de fábrica: limpiar las ventanas, coser los botones que faltan, ir a la tintorería, hacer la cola de la carnicería de la avenida, que los días de fiesta es más corta. Descansar, no descansa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un día de fiesta para una mujer es un día de trabajo en casa en vez de en la fábrica. Piensa que en la fábrica por lo menos le pagan. Se ríe sola, con el trapo de las ventanas en la mano. Abajo, en el patio, alguien toca el acordeón.",
        ),
        "revolucion" to dosDias(
            mapOf(
                Parrafo.MANANA to "Hoy es el aniversario de la Revolución, la fiesta grande del otoño, y en la tele dan el desfile de la plaza Roja: los soldados en filas perfectas, los tanques, los cohetes en camiones enormes, los señores de la tribuna con gorros de piel. Volodia lo mira entero. {nombre} lo oye desde la cocina.",
                Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta, dos días. Por la tarde, la columna de la fábrica desfila después de los soldados, con banderas y flores de papel, por las mismas calles que en mayo, pero con frío. Se canta para entrar en calor. Se vuelve a casa con la nariz roja.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en la Revolución, que es una cosa de los libros, de antes de que naciera su madre. Piensa que gracias a ella su madre aprendió a leer, y ella tiene piso, y Lena irá al instituto. Piensa que eso es verdad. Piensa que el resto es desfile.",
            ),
            mapOf(
                Parrafo.MANANA to "Hoy es el segundo día de la fiesta de noviembre, y la ciudad está tranquila, gris, con las banderas mojadas colgando de las farolas. Hace frío. Ha caído la primera nieve que no se derrite. {nombre} saca del armario el abrigo de invierno y le cose un botón.",
                Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es el segundo día de fiesta. Vienen los vecinos a tomar té, con una tarta comprada después de una hora de cola, de crema y bizcocho, con rosas de azúcar. Se juega a las cartas. Se habla de la tele, de los precios, de los hijos. Nadie habla de la Revolución.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que mañana se vuelve a los telares, y que hasta año nuevo no hay otra fiesta, solo el plan, la cola y la nieve. Piensa que diciembre es el mes más largo del año. Piensa que lo pasará, como todos. Se come otro trozo de tarta.",
            ),
        ),

        // ---------- Los de la temporada 1 ----------
        "nevera" to unDia(
            Parrafo.MANANA to "Hoy, por fin, después de tres años en la lista de espera del sindicato de la fábrica, le ha tocado a {nombre} la nevera: una ZIL blanca, grande, redonda por arriba, con un tirador cromado que suena como la puerta de un coche. La trae un camión. Los vecinos salen al rellano.",
            Parrafo.GENTE to "Esta tarde Volodia y un vecino suben la nevera por la escalera, cinco pisos, sudando, discutiendo por dónde se coge, y la ponen en la cocina, donde no cabe, así que quitan una silla. Lena abre y cierra la puerta para ver la luz. La suegra dice que antes la comida se guardaba en la ventana.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la nevera es lo más bonito de la casa. Piensa que ya no habrá que dejar la leche entre las dos ventanas en invierno, ni comprar todo el mismo día en verano. Piensa en su madre, que tiene un pozo frío. Le escribirá esta noche. Le dirá que tiene nevera.",
        ),
        "pionera" to unDia(
            Parrafo.MANANA to "Hoy Lena entra en los pioneros, en un acto en el salón de la escuela, con banderas, tambores y una corneta que desafina. Los niños forman en fila, con la camisa blanca, y uno a uno dicen la promesa en voz alta, y un pionero mayor les anuda al cuello el pañuelo rojo.",
            Parrafo.GENTE to "Esta tarde Lena no se quita el pañuelo rojo ni para cenar: dice que es un trozo de la bandera, que se lo han dicho. Lo plancha ella sola, con cuidado, sin quemarlo. La suegra la mira. Volodia le da un rublo. {nombre} le hace una foto con la cámara prestada del vecino.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su propio pañuelo rojo, que tuvo hace veinte años, en la escuela del pueblo, y que se le perdió en el río. Lloró una semana. Piensa que Lena no lo perderá: es más cuidadosa que ella. Piensa que Lena es mejor que ella en casi todo. Le gusta pensarlo.",
        ),
        "arlequin" to unDia(
            Parrafo.MANANA to "Hoy en la radio de la cocina han puesto otra vez esa canción, la del arlequín, de una cantante joven pelirroja que tiene una voz como un grito y una risa en medio de la canción, una risa de verdad, que nadie había puesto nunca en una canción. Es la tercera vez esta mañana.",
            Parrafo.TRABAJO to "A esta hora, en el vestuario, Zina canta la canción del arlequín, con la risa y todo, y las de la brigada se ríen con ella, y la cantan también, mal, todas a la vez. La jefa de turno asoma la cabeza. Se calla todo el mundo. Cuando se va, Zina se ríe como el arlequín.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esa muchacha canta como si no tuviera miedo de nada, ni de lo que digan, ni de quedar mal. Piensa que eso es raro. Piensa que le gustaría ser así un día, uno solo. Tararea la canción bajito, entre los telares, donde nadie la oye.",
        ),
        "apolo_soyuz" to unDia(
            Parrafo.MANANA to "Hoy la radio dice que una nave soviética y una americana se han unido en el espacio, se han dado la mano por una escotilla, los cosmonautas y los astronautas, y han comido juntos. Lo dicen en las noticias con una voz contenta, como si fuera una boda.",
            Parrafo.GENTE to "Esta tarde en la tele se ve, borroso, a un cosmonauta y a un americano dándose la mano en una cabina pequeña, flotando. Volodia dice que es propaganda. La suegra dice que es pecado subir tan alto. Lena dice que quiere ser la primera mujer que se dé la mano con una americana.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hace unos años, en la escuela de Lena, hacían simulacros de bomba atómica, y los niños se metían debajo de los pupitres. Y hoy se dan la mano en el cielo. Piensa que las cosas cambian. Piensa que pueden volver a cambiar. Mira la tele un rato más.",
        ),
        "botas" to unDia(
            Parrafo.MANANA to "Hoy, al salir de la fábrica, Zina corrió a decirle que en los grandes almacenes de la avenida habían sacado botas de mujer de Yugoslavia, de piel de verdad, con cremallera. {nombre} fue derecha, sin pasar por casa. La cola ya daba la vuelta a la manzana.",
            Parrafo.TRABAJO to "Esta tarde hace cuatro horas de cola por las botas, de pie, en el frío de octubre, con el número de su turno escrito con bolígrafo en la palma de la mano, para que nadie se cuele. Cada hora pasan lista. Si no estás, pierdes el número. A las ocho, le toca. Quedan del treinta y ocho. Ella calza treinta y siete.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, volviendo a casa con las botas en la caja bajo el brazo, que un número de más se arregla con un calcetín gordo, y que unas botas de Yugoslavia no se vuelven a ver en un año. Piensa que es feliz. Por unas botas. Se ríe de sí misma. Es feliz igual.",
        ),
        "borracho" to unDia(
            Parrafo.MANANA to "Hoy Volodia ha vuelto a casa de madrugada de la fiesta de su fábrica, la de la Revolución, que se ha alargado tres días, cantando en la escalera. Ha entrado, ha tropezado con la mesa de la cocina y ha roto un plato, uno de los buenos, de los de la boda. Luego se ha dormido en el suelo.",
            Parrafo.GENTE to "Esta tarde Volodia no se atreve a mirarla. Ha barrido los trozos del plato, ha bajado la basura, ha arreglado el grifo que goteaba desde hace un mes. La suegra le habla en voz baja en el pasillo. Lena hace como que no se ha enterado. Todos saben. Nadie dice nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el plato, que era de un juego de seis que les regaló su madre, y en que ya quedan cuatro. Piensa en cuántos platos le quedan a ella. No se lo dice. Pega los trozos grandes con pegamento y lo guarda en el armario. No servirá para nada. Lo guarda.",
        ),
        "plan_75" to unDia(
            Parrafo.MANANA to "Hoy empieza en la fábrica la carrera de diciembre: falta un trozo del plan del año, y el director ha mandado turnos dobles para todas las brigadas hasta el día 28. En la puerta han puesto un cartel rojo que dice que el plan es ley. Debajo, alguien ha añadido a lápiz que las piernas no.",
            Parrafo.TRABAJO to "A esta hora, en su segundo turno seguido, atiende los telares con los ojos que se le cierran y las manos que ya no se acuerdan de cansarse. Zina, al lado, canta para no dormirse. La jefa de turno pasa con termos de té. Dicen que al final de mes habrá prima. Dicen.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que todo el año se trabaja a medias, esperando el hilo, el repuesto, el mecánico, y en diciembre se trabaja por tres para cumplir lo que no se cumplió. Piensa que el plan es como su suegra: no hace caso en todo el año, y en diciembre lo quiere todo.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "obrera_moscu", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + SANATORIO_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + SANATORIO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + SANATORIO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + SANATORIO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
