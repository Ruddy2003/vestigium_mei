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
 * El cuento del archivista de un pueblo de pescadores en la niebla, de 1926 a 1930. Sigue la gramática y el tono de
 * `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su ficha se llama «El archivista»: el cuento lo escribe a mano y no usa el hueco del nombre.
 * - Lo raro se insinúa y nunca se explica: un signo, un nombre que se repite, una isla que no existe. No hay criaturas
 *   ni nombres de ninguna obra.
 * - Come dos veces: esos textos valen a cualquiera de sus horas. Lee de noche, hasta tarde.
 * - Dos estados: «fiebre» (temporada 3, tres semanas en cama en la posada) y «faro» (temporada 4, tres semanas cuidando
 *   el faro con el torrero enfermo).
 * - El pueblo lee el arco «archivo» (lo catalogado).
 * - No repite lo que ya dice «Su vida»: el archivo municipal que vino a ordenar, las actas y los naufragios, el salitre,
 *   el pescado salado y la sopa de la posada, los pescadores que cambian de acera, la lámpara de aceite, el signo que no
 *   recuerda haber trazado.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG7.kt`):
 * - Baltasar, el torrero, hasta que muere (`torrero`, temporada 4); un torrero nuevo desde `torrero_nuevo` (5).
 * - El signo en sus cuadernos, desde que lo ve (`signo`, temporada 1). El mapa de la isla, desde `mapa` (2).
 * - Su profesor: le escribe en `profesor` (temporada 2) y no le contesta hasta `contesta` (5).
 * - Las fiestas que vuelven cada año no nombran a Baltasar.
 */
internal object PiezasArchivista {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(11, 12, 1, 2)

    private val MANANA = listOf(
        v("El archivista se despierta tarde, con la boca seca y la luz gris de la niebla en la ventana, sin saber cuánto ha dormido. Abajo, en la posada, ya suenan los platos. Se lava en la jofaina con agua fría y se mira en el espejo. Tiene ojeras."),
        v("Al archivista lo despierta la sirena del faro, que suena cada minuto cuando hay niebla, un mugido largo y triste que entra por la ventana y se queda en el cuarto. Cuenta los mugidos sin querer. Al décimo, se levanta. Fuera no se ve nada: ni el mar, ni el muelle."),
        v("El archivista se despierta con el cuaderno abierto encima del pecho y la pluma en la mano, como si se hubiera dormido escribiendo. Mira la última página. Hay unas líneas que no recuerda. Las lee. Son una lista de fechas. Cierra el cuaderno sin mirarlas otra vez."),
        v("Lo primero que hace el archivista al levantarse es abrir la ventana y respirar el aire del mar, que huele a sal, a algas y a algo más, dulce y podrido, que nadie en el pueblo parece notar. Lo pregunta una vez a la posadera. Ella dice que es el mar."),
        v("El archivista se despierta con las campanas de la iglesia, que tocan a misa, y con las voces de las mujeres que pasan por debajo de su ventana, de negro, hablando bajito. Cuando él se asoma, se callan. Cuando él se retira, vuelven a hablar."),
        v("El archivista se despierta helado, con la manta en el suelo, porque la estufa del cuarto se apaga de madrugada y el frío del mar entra por todas las rendijas. Se viste con el abrigo encima del camisón.", meses = INVIERNO),
    )

    /** Tres semanas en cama con una fiebre que el médico no entiende, en la temporada 3 (estado «fiebre»). */
    private fun fi(texto: String, hacer: Hacer? = null) = v(texto, estado = "fiebre", hacer = hacer)

    private val FIEBRE_MANANA = listOf(
        fi("El archivista se despierta empapado en la cama de la posada, con la fiebre que sube por las mañanas como la marea, y no sabe si ha soñado o ha estado despierto. En la silla, la posadera ha dejado un caldo y un paño. El caldo está frío. Lo ha estado mucho tiempo."),
        fi("Al archivista lo despierta su propia voz, que habla en el sueño en una lengua que no conoce. Se calla. Escucha. En el cuarto no hay nadie. En la pared, la mancha de humedad tiene una forma que ayer no tenía. Cierra los ojos. Cuando los abre, tiene la de siempre."),
        fi("El archivista se despierta débil, con los huesos que le duelen como si se los hubieran cambiado de sitio, y tarda un rato en saber dónde está. La posada. La fiebre. El pueblo. Oye el mar. Siempre se oye el mar, pero hoy se oye más cerca, debajo de la cama."),
    )

    private val FIEBRE_TRABAJO = listOf(
        fi("A esta hora no trabaja: está en la cama, con la fiebre, y el archivo está cerrado con su llave en el cajón de la mesilla. Piensa en los legajos que esperan, en la humedad que les entra sin él. Intenta levantarse. Las piernas no le obedecen. Se vuelve a tumbar.", Hacer.TRABAJO),
        fi("A esta hora el médico del pueblo, un viejo con las manos frías, le toma el pulso, le mira los ojos, le pregunta qué ha comido, qué ha leído. Qué ha leído. El archivista no contesta. El médico deja unas pastillas y dice que no entiende la fiebre. Lo dice cada día.", Hacer.TRABAJO),
        fi("A esta hora la posadera le da de comer a cucharadas una sopa de pescado blanco, sin espinas, mientras le habla de cosas del pueblo, de la pesca, del tiempo, sin mirarlo a los ojos. Él traga. Le sabe a sal y a nada. Ella le limpia la barbilla como a un niño.", Hacer.COMER),
        fi("A esta hora duerme a ratos, entre sueños de agua: que baja por una escalera de piedra sin fin, mojada, hacia algo que lo llama por su nombre. Se despierta antes de llegar abajo. Siempre antes. Tiene miedo de un día no despertarse antes.", Hacer.DORMIR),
        fi("A esta hora la niña del café sube a verlo, con permiso de la posadera, y se sienta en la silla sin decir nada, mirándolo. Le ha traído una concha. La deja en la mesilla. Él le da las gracias. Ella se va corriendo. La concha tiene una forma que le recuerda algo.", Hacer.CAMINO),
        fi("A esta hora intenta leer, en la cama, con la lámpara cerca, uno de los legajos que se trajo del archivo. Las letras se mueven. Las líneas se cruzan. Cierra los ojos y sigue viendo las letras. Deja el legajo en el suelo.", Hacer.LIBRE),
    )

    private val FIEBRE_PUEBLO = listOf(
        fi("El cuarto de la posada es pequeño y blanco, con una cama de hierro, una silla, una mesa con sus papeles, un lavabo y una ventana que da al mar. Desde la cama solo se ve el cielo y el borde del muelle. Durante semanas, es todo el pueblo que el archivista ve."),
        fi("La posada está en la calle del muelle, una casa de tres pisos de piedra gris con un letrero de madera que el salitre ha comido. Abajo, la taberna, donde los pescadores beben y hablan bajito. Desde la cama, el archivista oye sus voces subir por el suelo. Hablan de él."),
    )

    private val FIEBRE_ANOCHECER = listOf(
        fi("Al caer la tarde la fiebre baja un poco, y el archivista se incorpora en la cama para ver la luz del faro encenderse, girar, pasar por su ventana, irse. Cada vuelta, un segundo de luz en la pared. La cuenta. Le parece que el faro le habla. No sabe qué le dice."),
        fi("Anochece, y la posadera sube con una vela y la deja en la mesilla, y se queda un momento en la puerta, mirándolo, como si quisiera decirle algo. No se lo dice. Se va. El archivista mira la vela hasta que se duerme. Cuando se despierta, se ha consumido entera."),
    )

    /** Tres semanas cuidando el faro con el torrero enfermo, en la temporada 4 (estado «faro»). */
    private fun fa(texto: String, hacer: Hacer? = null) = v(texto, estado = "faro", hacer = hacer)

    private val FARO_MANANA = listOf(
        fa("El archivista se despierta en el faro, en el catre del cuarto de abajo, con el viento que golpea la torre como un puño y el ruido del mar contra las rocas, abajo, que no para nunca. Arriba, el torrero tose. Se levanta a apagar la lámpara: ya es de día."),
        fa("Al archivista lo despierta el silencio de la lámpara apagada, después de una noche de oírla girar sobre su cabeza. Sube los ciento veinte escalones de la torre, que ya se sabe, a limpiar los cristales."),
        fa("El archivista se despierta con frío en el faro, donde la estufa no calienta y la piedra suda. El torrero, en su cama, le dice desde lejos lo que hay que hacer hoy, con una voz que se apaga. El archivista lo apunta en el cuaderno, como un legajo."),
    )

    private val FARO_TRABAJO = listOf(
        fa("A esta hora limpia los cristales de la lámpara del faro, arriba del todo, con un paño de gamuza y alcohol, uno por uno, hasta que brillan. Los cristales son prismas que tuercen la luz y la mandan lejos. Mira a través de uno. El mar se dobla, se parte, se hace otro.", Hacer.TRABAJO),
        fa("A esta hora sube aceite para la lámpara del faro, en dos latas, por los ciento veinte escalones, parándose cada treinta a respirar. En lo alto, llena el depósito, recorta la mecha, da cuerda al mecanismo que hace girar la luz. El torrero se lo enseñó ayer.", Hacer.TRABAJO),
        fa("A esta hora escribe el cuaderno del faro, donde se apunta cada noche la hora a la que se enciende y se apaga, el tiempo, los barcos que pasan. Lo hace con su letra de archivista, cuidada. Pasa hacia atrás las páginas de otros años. Hay noches en blanco.", Hacer.TRABAJO),
        fa("A esta hora come en la cocina del faro lo que manda la posadera en una cesta por un muchacho: pan, pescado, queso, una botella de vino. Lo reparte con el torrero, que come poco y mira el mar por la ventana todo el rato. Comen en silencio.", Hacer.COMER),
        fa("A esta hora duerme a medias, en el catre de abajo, despertándose cada vez que el mecanismo de la lámpara cambia de ruido. Si se para, el faro se apaga, y un barco puede ir a las rocas. Escucha. Gira. Gira. Se duerme otra vez.", Hacer.DORMIR),
        fa("A esta hora baja por el sendero del acantilado al pueblo, a por lo que haga falta en el faro, con el viento empujándolo contra las rocas. Abajo, en el muelle, los pescadores lo miran con algo nuevo en la cara. Uno se quita la gorra. Él no sabe por qué.", Hacer.CAMINO),
        fa("A esta hora está arriba, en la sala de la lámpara, vigilando la luz, sentado en una silla de madera, mirando cómo el haz barre el mar en la oscuridad. Un barco pequeño, lejos, con una luz. Otro. Nada. Algo en el agua, donde no hay rocas. Mira otra vez. Nada.", Hacer.LIBRE),
    )

    private val FARO_PUEBLO = listOf(
        fa("El faro está en la punta del acantilado, al otro lado de la bahía, una torre blanca y gruesa con una casa pegada, donde vive el torrero desde hace cuarenta años. Desde el pueblo se ve la luz, no la torre."),
        v("Desde lo alto del faro la costa se ve entera: la bahía, el pueblo, las rocas, la playa grande, y más allá, cuando la niebla se levanta, un horizonte que no tiene nada. Ninguna isla. El archivista mira a donde el mapa del archivo pone una. Ninguna isla.", estado = "faro", desde = "mapa"),
    )

    private val FARO_ANOCHECER = listOf(
        fa("Al caer la tarde el archivista enciende la lámpara del faro, sube la mecha, da cuerda, y la luz empieza a girar sobre el mar oscuro, como un brazo que señala y se retira. Abajo, en el pueblo, se encienden las ventanas una a una. Le parece que le contestan."),
        fa("Anochece en el faro, y el viento arrecia, y la torre entera vibra como un instrumento. El torrero, en su cama, le cuenta cosas de los naufragios que ha visto, de los ahogados que el mar devolvió. El archivista no apunta nada. Esta vez escucha."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el pueblo se dice que el mar manda en el tiempo, y no al revés."),
        v("El archivista mira el cielo desde la ventana del archivo, entre los tejados. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los pescadores leen el cielo como él lee los legajos: buscando lo que no está escrito."),
        v("{cielo} En {estacion} la niebla del mar entra en el pueblo sin pedir permiso y se queda días."),
        v("{de_estacion} {cielo} En los registros del archivo, el tiempo de cada día está apuntado desde hace dos siglos."),
        v("Aquí el cielo es casi siempre del color del agua, y el agua del color del cielo. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, raro, de un azul tan pálido que parece lavado, y el mar brilla como metal.",
            "Hace un día claro, de los pocos, y desde el muelle se ve el horizonte entero, vacío.",
            "No hay nubes, y la luz es tan blanca que las casas parecen de hueso.",
        ),
        Clima.NUBES to listOf(
            "El cielo está bajo y gris, del color de las rocas, y el mar no se distingue de él.",
            "Pasan nubes pesadas desde el mar, y su sombra corre por el agua como un animal.",
            "Está nublado, con una luz plana que no deja sombras.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fina y salada que viene del mar de lado.",
            "Cae una lluvia fría que corre por las calles empinadas hasta el muelle.",
            "Llueve, y los tejados de pizarra brillan como escamas.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta en el mar, y los rayos caen lejos, en el agua, donde no hay nada.",
            "Truena, y las olas saltan por encima del muelle y llegan a las puertas de las casas.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, espesa, que se traga el muelle, las barcas, la mitad de la calle.",
            "La niebla del mar sube por el pueblo y deja gotas en los cristales y en el pelo.",
            "Hay una niebla tan blanca que la sirena del faro no deja de sonar.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento del mar que silba en las chimeneas y hace golpear las contraventanas.",
            "Hace viento, y las gaviotas se quedan quietas en el aire, sin avanzar.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en las rocas del acantilado salen unas flores amarillas que huelen a miel.",
            "En primavera el mar se calma, y las barcas salen otra vez de noche.",
            "Es el tiempo de los temporales cortos y de las mareas vivas.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, que aquí es corto y gris, con una niebla que no se levanta hasta media tarde.",
            "En verano la sardina llena los barcos, y el pueblo entero huele a pescado.",
            "Es el tiempo de las noches largas en el muelle y de la marea más baja.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo de los temporales que vienen del mar sin avisar.",
            "En otoño las barcas se varan en la arena, y los hombres reparan las redes en la playa.",
            "Es el tiempo de los naufragios, según los registros del archivo.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y el mar golpea el muelle con un ruido que se oye en todas las casas.",
            "En invierno el pueblo se cierra, y la gente sale poco, y habla menos.",
            "Es el tiempo de la oscuridad temprana y de la sirena del faro.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora copia a mano, en el archivo, un registro de bautizos de hace ciento cincuenta años que el salitre está borrando, letra a letra, con la pluma y una lupa. Los nombres se repiten: los mismos apellidos, una generación tras otra. Algunos, demasiado.", hacer = Hacer.TRABAJO),
        v("A esta hora abre una caja nueva del archivo, en la mesa grande, junto a la ventana, y saca los papeles uno a uno, despacio, porque se rompen: cartas, recibos, actas de pleno, un dibujo de un barco. Les quita el polvo con un pincel. Los ordena por fechas.", hacer = Hacer.TRABAJO),
        v("A esta hora pone a secar sobre una cuerda, en el cuarto del archivo, unos legajos que se mojaron con la humedad, sujetos con pinzas de tender ropa, como camisas de papel. Gotean. El cuarto huele a moho y a mar. Mira las hojas al trasluz.", hacer = Hacer.TRABAJO),
        v("A esta hora escribe fichas para el catálogo del archivo, en tarjetas de cartón, con su letra pequeña y cuidada: qué es, de qué fecha, en qué caja. Va por la ficha número dos mil y pico. Las guarda en un fichero de madera que mandó hacer al carpintero del pueblo.", hacer = Hacer.TRABAJO),
        v("A esta hora lee con la lupa un acta del ayuntamiento de hace un siglo, escrita con una letra torcida, sobre una reunión de los vecinos por algo que pasó en la playa. El acta no dice qué pasó. Dice que se acordó no hablar de ello. Lo apunta en su diario.", hacer = Hacer.TRABAJO),
        v("A esta hora atiende a una mujer del pueblo que viene a pedirle la partida de bautismo de su abuelo, para una herencia. Busca en los registros. La encuentra. La copia. La mujer la coge sin dar las gracias y se va deprisa, sin mirar las estanterías.", hacer = Hacer.TRABAJO),

        v("A esta hora come en la taberna de la posada, en una mesa del rincón, de espaldas a la pared, un plato de guiso de pescado con patatas y un trozo de pan. Los pescadores de las otras mesas hablan bajito. Cuando él levanta la vista, miran sus platos.", hacer = Hacer.COMER),
        v("A esta hora come en el archivo, en un rincón de la mesa, lo que le ha preparado la posadera envuelto en un trapo: pan, un trozo de queso duro, unas sardinas en aceite, una manzana. Come con una mano y pasa hojas con la otra. Las migas caen en los legajos.", hacer = Hacer.COMER),
        v("A esta hora come en la cocina de la posada, junto al fogón, una sopa de fideos y un huevo, mientras la posadera friega y le cuenta cosas del pueblo, de las bodas, de los entierros, sin preguntarle nunca nada a él. Es la única que le habla sin prisa.", hacer = Hacer.COMER),

        v("A esta hora camina por el muelle, de punta a punta, con el abrigo cerrado y las manos en los bolsillos, mirando el agua, que choca contra las piedras con un ruido de algo que respira.", hacer = Hacer.CAMINO),
        v("A esta hora sube por la calle empinada desde el archivo hasta la iglesia y baja por la otra, la del cementerio de las rocas, donde las tumbas miran al mar. Lee los nombres. Los conoce de los registros.", hacer = Hacer.CAMINO),
        v("A esta hora camina por la playa grande, al pie del acantilado, por la arena mojada, mirando lo que deja la marea: algas, maderas, una botella, unas conchas que no conoce, de un color violeta oscuro. Se guarda una en el bolsillo.", hacer = Hacer.CAMINO),

        v("A esta hora duerme, por fin, después de leer hasta que la lámpara se apagó sola, vestido, encima de la manta. Duerme pesado, sin sueños, o sin sueños que recuerde.", hacer = Hacer.DORMIR),
        v("A esta hora duerme mal, despertándose con los ruidos del mar, que de noche suena más fuerte, y con otros ruidos que no son del mar y que no sabe de qué son: algo que rasca, algo que gotea. Se queda quieto, escuchando. Se callan. Vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con el archivo, que en el sueño es más grande, con pasillos que bajan, y cajas que no ha abierto nunca, todas con su nombre escrito. Se despierta antes de abrir ninguna. Por la mañana no se acuerda del sueño. Solo del nombre.", hacer = Hacer.DORMIR),

        v("A esta hora lee en su cuarto de la posada, junto a la estufa, un libro de historia de la costa que trajo de la ciudad, buscando el pueblo en el índice. No aparece. Lo busca en el mapa del libro. Tampoco. Es como si el pueblo no hubiera existido para nadie de fuera.", hacer = Hacer.LIBRE),
        v("A esta hora escribe en su diario lo que ha encontrado hoy, con la letra cuidada de los legajos: un nombre, una fecha, una coincidencia. Lo escribe todo. Es su costumbre de archivista. Si un día alguien abre su diario, sabrá lo que él sabía. Si alguien lo abre.", hacer = Hacer.LIBRE),
        v("A esta hora lee en voz baja, en latín, un libro de cuentas de la parroquia de hace doscientos años, que tiene en los márgenes notas de otra mano, en otra tinta. Las notas no son cuentas. Son una oración, o algo que se parece. La copia, para estudiarla. No la lee en voz alta.", hacer = Hacer.LIBRE),
        v("A esta hora repasa su cuaderno y encuentra otra vez el signo en el margen, a lápiz, en una página de ayer. No recuerda haberlo hecho. Lo compara con los otros: es el mismo, cada vez más firme. Lo tapa con el dedo. Sigue leyendo. Ya no lo borra.", hacer = Hacer.LIBRE, desde = "signo"),
    )

    private val PUEBLO = listOf(
        v("{entorno} El archivo está en el piso de arriba del ayuntamiento, un cuarto largo con estanterías hasta el techo, cajas de cartón y de madera, una mesa grande, una ventana al puerto y una estufa que no calienta. Huele a papel, a polvo y a mar."),
        v("El pueblo son tres calles empinadas que bajan al puerto, casas de piedra gris con tejados de pizarra, una iglesia con un campanario que se inclina, una taberna, una lonja y el muelle. Detrás, el monte. Delante, el mar. Nada más en muchas leguas. {entorno}"),
        v("{entorno} El muelle es de piedra negra, viejo, comido por el mar, con argollas de hierro donde se atan las barcas, y unas escaleras que bajan al agua y se pierden dentro. En la marea baja se ven más escalones, muchos más. En la alta, ninguno."),
        v("En la lonja del pueblo se vende el pescado cada mañana, a gritos, sobre losas de piedra mojada. Las mujeres lo limpian con cuchillos cortos y tiran las tripas a las gaviotas. A veces sale en las redes algo que las mujeres apartan con el pie sin decir nada. {entorno}"),
        v("{entorno} La iglesia del pueblo es de piedra, pequeña y oscura, con barcos pintados en las paredes por los que se salvaron de un naufragio y se lo agradecieron a la Virgen. Hay muchos. En la sacristía, el párroco guarda los libros que no ha dejado subir al archivo."),
        v("El cementerio está en las rocas, al lado de la iglesia, con las tumbas mirando al mar y una cruz de hierro para los que el mar no devolvió. En la cruz hay nombres de dos siglos. El archivista los ha contado. Son más que los vivos del pueblo. {entorno}"),
    )

    /** Lo catalogado del archivo (arco «archivo»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("archivo", 0.0, 0.3, frases = listOf(
            "En el archivo, las cajas por abrir se apilan hasta el techo, y el fichero del catálogo tiene un solo cajón lleno.",
            "En el archivo, casi todo está todavía por abrir, en cajas con letreros de hace cien años.",
        )),
        Entorno("archivo", 0.3, 0.7, frases = listOf(
            "En el archivo ya hay estanterías enteras ordenadas, con sus etiquetas, y otras que siguen como las dejó el siglo pasado.",
            "El fichero del catálogo ya tiene cinco cajones llenos, y las cajas por abrir ocupan una sola pared.",
        )),
        Entorno("archivo", 0.7, 0.99, frases = listOf(
            "Al archivo le quedan pocas cajas por abrir, en un rincón, y el archivista las mira cada día sin decidirse.",
            "En el archivo casi todo está ya en su sitio, con su número, y el cuarto parece otro.",
        )),
        Entorno("archivo", 0.99, 1.0, frases = listOf(
            "El archivo está catalogado entero, caja por caja, y el fichero de madera no admite una tarjeta más.",
            "En el archivo no queda nada por abrir. Solo queda lo que se ha encontrado.",
        )),
    )

    private val GENTE = listOf(
        v("Doña Inés, la posadera, es una viuda alta y seca que lleva la posada desde que su marido se perdió en el mar. Le cobra poco, le lava la ropa y le guarda la sopa caliente. Nunca le pregunta qué encuentra en el archivo. A veces lo mira como si ya lo supiera."),
        v("La niña que le lleva el café al archivo cada mañana tiene diez años, unas trenzas negras y los ojos muy claros. Es la nieta de la posadera. Se queda en la puerta mirando las cajas, sin entrar. Le pregunta cosas. Él le contesta."),
        v("El párroco es un hombre gordo y nervioso que lleva treinta años en el pueblo y que no quiso darle al archivista los libros de la sacristía. Dice que son de la Iglesia. Cuando se cruzan en la calle, el párroco se santigua. Luego se ríe, como si fuera una broma."),
        v("Los pescadores del pueblo son hombres grandes, callados, con las manos llenas de cortes de las redes y unos ojos que miran siempre al mar, aunque estén de espaldas a él. Con el archivista no hablan. Hablan de él. Lo sabe por la niña."),
        v("El alcalde es el que lo contrató, un comerciante de la ciudad que compró la lonja y que quiere el archivo ordenado para venderle al gobierno no se sabe qué. Viene cada mes, mira las cajas, pregunta cuánto falta y se va en el tren de la tarde."),
        v("Baltasar, el torrero, vive solo en el faro desde hace cuarenta años y baja al pueblo una vez al mes. Es el único que se sienta a la mesa del archivista en la taberna. Le cuenta naufragios. Le pregunta por los registros. Le dice que hay cosas que mejor no ordenar.", hasta = "torrero"),
        v("Baltasar, el torrero, murió, y el faro lo encienden por turnos los hombres del pueblo, que no saben y lo encienden tarde. El archivista guarda la gorra que le dio en el faro. A veces sube al acantilado a mirar la torre apagada al anochecer, hasta que alguien la enciende.", desde = "torrero", hasta = "torrero_nuevo"),
        v("Al faro llegó un torrero nuevo, joven, que no habla con nadie y que no baja nunca al pueblo. Lo traen la comida en una barca. Por las noches, el archivista ve la luz del faro girar igual que antes. Pero gira más despacio. O eso le parece.", desde = "torrero_nuevo"),
        v("Su antiguo profesor de la universidad, el que le enseñó a leer letra antigua y le consiguió este trabajo, vive en la ciudad, entre libros. Le escribió una carta al llegar, contándole el archivo. El profesor contestó con una postal: que trabaje, que no se canse.", hasta = "profesor"),
        v("A su antiguo profesor le escribió una carta larga, con dibujos del signo y la lista de los nombres repetidos, preguntándole si había visto algo así. El profesor no contesta. Le escribe otra. Tampoco. El archivista revisa el correo cada mañana en la lonja.", desde = "profesor", hasta = "contesta"),
        v("Su antiguo profesor contestó por fin a sus cartas, con una sola línea, escrita con una letra que tiembla. El archivista no se la ha enseñado a nadie. La guarda dentro de su diario, doblada. La lee a veces. No le ayuda.", desde = "contesta"),
    )

    private val OBJETO = listOf(
        v("En un archivo cada papel cuenta algo, y algunos cuentan de más. {dato_objeto}"),
        v("{dato_objeto} El archivista lo aprendió en la universidad, en la ciudad, que ahora le parece muy lejos."),
        v("Hoy el archivista se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} En el pueblo nadie le pregunta por eso. En el pueblo nadie pregunta."),
        v("{dato_objeto} Las cosas de un archivista son pocas, y todas sirven para lo mismo: para que no se olvide."),
        v("En la costa todo se oxida, se pudre o se borra, y lo que dura, dura raro. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("legajo", "el legajo", "un legajo", "El legajo",
            "Un legajo es un montón de papeles atados con una cinta o un cordel, con un rótulo por fuera: actas de un año, cartas de un pleito, cuentas de una cofradía; los del archivo están manchados de humedad, y algunos se pegan, y hay que separarlos con un cuchillo de hueso."),
        ObjetoCuento("pluma", "la pluma", "una pluma", "La pluma",
            "La pluma del archivista es de acero, con un mango de madera mordido por la punta, y escribe con una tinta negra que él mismo mezcla con polvo y agua; la tinta de los registros viejos era de agallas de roble, y con los siglos se vuelve marrón y come el papel."),
        ObjetoCuento("lampara", "la lámpara de aceite", "una lámpara de aceite", "La lámpara",
            "La lámpara de aceite del cuarto de la posada tiene un tubo de cristal y una mecha que hay que recortar cada noche; da una luz amarilla que tiembla y hace bailar las sombras en la pared; con ella, las letras de los legajos parecen moverse."),
        ObjetoCuento("lupa", "la lupa", "una lupa", "La lupa",
            "La lupa del archivista es grande, con mango de latón, y con ella se leen las letras borradas, las tintas comidas, los nombres que alguien rascó con una navaja; vista con lupa, cada letra es un paisaje, con sus montañas y sus grietas."),
        ObjetoCuento("diario", "el diario", "un diario", "El diario",
            "El diario del archivista es un cuaderno de tapas de tela negra, con las hojas rayadas, donde apunta cada noche lo que ha encontrado; va por el tercero desde que llegó; en los márgenes, a veces, hay cosas que no recuerda haber escrito."),
        ObjetoCuento("llave", "la llave del archivo", "una llave de hierro", "La llave",
            "La llave del archivo es de hierro, larga como una mano, con un anillo para colgarla; solo hay una, y la tiene él; antes la tenía el secretario del ayuntamiento, que la guardaba en un cajón y no subió nunca al archivo en treinta años."),
        ObjetoCuento("cafe", "el café", "una taza de café", "El café",
            "El café del pueblo es de bellota tostada mezclada con un poco de café de verdad, cuando llega en el barco del correo; se toma negro, sin azúcar, muy caliente, en tazas gruesas de loza blanca con una raya azul; al archivista se lo lleva la niña en un jarro tapado."),
        ObjetoCuento("abrigo", "el abrigo", "un abrigo de paño", "El abrigo",
            "El abrigo del archivista es de paño negro, de la ciudad, demasiado fino para la costa; la humedad se le mete en los hombros y no sale; la posadera le ha cosido por dentro una lana de oveja del monte, y desde entonces huele a oveja y a mar."),
        ObjetoCuento("reloj", "el reloj", "un reloj de bolsillo", "El reloj",
            "El reloj de bolsillo del archivista era de su padre, de plata, con una tapa que se abre con un resorte; en el pueblo se atrasa o se adelanta sin razón, algunos días mucho; el relojero de la ciudad dice que es la humedad."),
        ObjetoCuento("carta", "la carta", "una carta", "La carta",
            "Las cartas llegan al pueblo en el barco del correo, una vez a la semana, si el mar deja, y se recogen en la lonja; las que salen tardan lo mismo; en el archivo hay cartas de hace cien años que nunca se recogieron, todavía cerradas, con su lacre."),
        ObjetoCuento("mapa", "el mapa", "un mapa de la costa", "El mapa",
            "Los mapas de la costa del archivo son de dos siglos, dibujados a mano con tinta y acuarela, con las rocas, las corrientes, los bancos de peces y los naufragios marcados con una cruz; en uno de ellos hay una isla que no está en ningún otro."),
        ObjetoCuento("vela", "la vela", "una vela", "La vela",
            "Las velas del pueblo son de sebo, que humean y huelen a grasa, y las de cera solo se ven en la iglesia; en las casas de los pescadores se pone una vela en la ventana que da al mar cuando hay una barca fuera de noche, y no se apaga hasta que vuelve."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} El archivista piensa en esas cosas mientras camina, y luego vuelve a sus papeles, que son más tranquilos que el pueblo. A sus {edad} años sabe que lo que no se escribe se pierde. Y empieza a pensar que hay cosas que se escriben para que no se pierdan, y otras para que no salgan."),
        v("Piensa en la ciudad, donde estudió, donde tenía amigos, cafés, librerías, una vida con ruido. Piensa que hace mucho que no la echa de menos. Lo piensa y se extraña. Piensa que el pueblo se le ha metido dentro como la humedad en los legajos. Despacio. Sin avisar."),
        v("{mundo} El archivista piensa en los nombres que se repiten en los registros, los mismos apellidos durante dos siglos, y en que en el pueblo nadie se casa con nadie de fuera. Piensa que eso lo explica todo. Piensa que no lo explica todo. Apunta la duda en el diario."),
        v("{mundo} A veces, en el archivo, con la niebla pegada a la ventana y el silencio de las cajas, el archivista siente que alguien lee por encima de su hombro. Se da la vuelta. No hay nadie. Sigue leyendo. La sensación se queda, como el olor del mar."),
        v("Piensa en el trabajo que le queda: cajas y cajas, años de papeles. Piensa que cuando termine se irá, volverá a la ciudad, a otra cosa. Lo piensa cada noche. Cada noche le parece menos verdad. Piensa que eso no lo apuntará en el diario."),
        v("{mundo} A sus {edad} años, el archivista sabe leer cualquier letra, por vieja que sea, en cuatro lenguas. Lo que no sabe es leer las caras de la gente del pueblo. Piensa que las caras también tienen su letra antigua. Piensa que algún día aprenderá. Que a lo mejor no quiere."),
    )

    private val MUNDOS = mapOf(
        "niebla" to listOf(
            "En la taberna se habla de la pesca de este año, que es buena, demasiado buena, dicen los viejos.",
            "Se dice en la lonja que han visto luces en el agua, de noche, lejos, donde no hay barcas.",
            "Se habla del tren de la costa, que quieren llevar hasta el pueblo, y de que los del pueblo no lo quieren.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde el archivista cierra el archivo con la llave de hierro y baja al muelle, como cada día, a ver encenderse el faro al otro lado de la bahía. La luz gira. Cuenta los segundos entre vuelta y vuelta. Siempre los mismos."),
        v("Anochece, y la niebla sube del mar y se come el pueblo calle a calle, y las ventanas se vuelven manchas amarillas que flotan. La sirena del faro empieza a sonar. El archivista vuelve a la posada pegado a las paredes."),
        v("Al caer la tarde las barcas salen a la pesca de la noche, una tras otra, con sus farolillos, y las mujeres las miran desde el muelle sin decir adiós, porque decir adiós trae mala suerte."),
        v("De noche, desde la ventana de su cuarto, el archivista ve el mar negro y, en el horizonte, a veces, unas luces que no son de barcas ni de estrellas: verdes, quietas, bajas. Las mira. Se apagan. Nadie en el pueblo habla de ellas. Él tampoco."),
        v("Anochece, y en la taberna de abajo los pescadores empiezan a cantar una canción vieja, en un dialecto que el archivista no entiende del todo, con un estribillo que se repite como una ola. La conoce de los legajos: está copiada en un acta de hace dos siglos. Prohibida."),
        v("La última luz del día se queda en el campanario de la iglesia, inclinado, cuando el resto del pueblo ya está en sombra. Suenan las campanas del ángelus. Los pescadores se quitan la gorra. El archivista también. Ya no sabe por qué lo hace. Lo hace."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y anoche, a las doce, las campanas de la iglesia sonaron doce veces, despacio, y nadie salió a la calle: en el pueblo el año se recibe con las puertas cerradas y una vela en la ventana que da al mar. El archivista la puso también, por si acaso.",
            Parrafo.TRABAJO to "Hoy el archivista no trabaja: el ayuntamiento está cerrado, y el archivo con él. Se queda en la posada, junto a la estufa, con la posadera y la niña, que juegan a las cartas sin apostar. Lo invitan a jugar. Pierde todas. La niña se ríe.",
            Parrafo.PENSAMIENTO to "El archivista piensa en el año que empieza y en lo que le pide: terminar el archivo, entender lo que ha encontrado, volver a la ciudad. Lo piensa en ese orden. Luego piensa que a lo mejor el orden es otro. No lo apunta.",
        ),
        "candelaria" to unDia(
            Parrafo.MANANA to "Hoy es la Candelaria, y en cada casa del pueblo se pone una vela encendida en la ventana que da al mar, de día y de noche, hasta que se consume. La posadera le da una al archivista para su ventana. Le dice que la encienda. Que no pregunte.",
            Parrafo.GENTE to "Esta tarde las mujeres del pueblo van a la iglesia con velas bendecidas, en fila, por la calle del muelle, cantando bajito. Las velas tiemblan con el viento. Ninguna se apaga. El archivista las mira pasar desde la puerta. Una vieja le pone una vela en la mano al pasar.",
            Parrafo.PENSAMIENTO to "El archivista piensa en las velas del mar, que en los registros del archivo aparecen cada año desde hace dos siglos, siempre el mismo día, con una nota que dice: por los que esperan. No dice a quién esperan. Piensa que lo sabrá. Piensa que prefiere no saberlo.",
        ),
        "aceite" to unDia(
            Parrafo.MANANA to "Hoy se sube al faro el aceite de la lámpara para todo el año, como cada año por estas fechas: veinte latas que llegan en el barco del correo y que cuatro hombres del pueblo llevan en una barca hasta el pie del acantilado, y luego a hombros por el sendero. El pueblo entero sale a mirar.",
            Parrafo.GENTE to "Esta tarde, en la taberna, los cuatro que subieron el aceite cuentan lo que el torrero les ha dicho arriba de lo que vio en el mar este invierno: barcos, ballenas, tormentas, luces. Cuando llegan a las luces, bajan la voz. Los demás se acercan. El archivista, desde su mesa, no oye nada.",
            Parrafo.PENSAMIENTO to "El archivista piensa en el torrero, solo en la torre, mirando el mar todas las noches de su vida. Piensa que él, en el archivo, hace algo parecido con los papeles. Piensa que los dos vigilan algo. No sabe qué vigila el torrero. Empieza a saber qué vigila él.",
        ),
        "bendicion" to unDia(
            Parrafo.MANANA to "Hoy es la bendición de las barcas, y todas están en el muelle, pintadas de nuevo, con flores en la proa y los nombres recién repasados. El párroco pasa de una a otra con el hisopo, rezando en latín. Los pescadores, de pie en cada barca, con la gorra en la mano.",
            Parrafo.GENTE to "Esta tarde las barcas dan una vuelta por la bahía, todas juntas, y las mujeres tiran flores al agua desde el muelle, por los que se llevó el mar. El agua se llena de flores. La corriente las lleva a todas hacia el mismo sitio, fuera de la bahía. El archivista lo apunta.",
            Parrafo.PENSAMIENTO to "El archivista piensa en el latín del párroco, que él entiende y los pescadores no. Piensa que el párroco ha dicho una palabra que no está en ninguna bendición que conozca. Piensa que a lo mejor la ha oído mal. Lo piensa toda la tarde.",
        ),
        "san_pedro" to unDia(
            Parrafo.MANANA to "Hoy es San Pedro, el de los pescadores, la fiesta grande del pueblo: el santo sale de la iglesia en andas, con su llave de madera dorada, y lo llevan por el muelle los hombres de las barcas, descalzos. Detrás, todo el pueblo, cantando. Delante, el párroco.",
            Parrafo.TRABAJO to "Hoy el archivista no trabaja: es fiesta, y el archivo está cerrado. Va a la procesión, al final, detrás de todos. En el muelle, el santo se para delante del agua, y los hombres lo inclinan hacia el mar, tres veces, como si saludara. El mar está quieto. Nadie respira.",
            Parrafo.PENSAMIENTO to "El archivista piensa que en los registros del archivo la procesión de San Pedro se hace desde hace tres siglos, y que antes se hacía otra cosa el mismo día, en el mismo muelle, que el acta no explica. Piensa que el santo se puso encima de algo. Como una tapa.",
        ),
        "carmen" to unDia(
            Parrafo.MANANA to "Hoy es la Virgen del Carmen, y la sacan de la iglesia en andas hasta el muelle y la suben a la barca más grande, adornada con flores y cintas, para darle la vuelta a la bahía. Detrás, todas las barcas del pueblo, en fila, a vela y a remo.",
            Parrafo.TRABAJO to "Hoy el archivista no trabaja: es fiesta. Lo han invitado a subir a una barca de la procesión, la de la posadera, que la lleva un sobrino. Va sentado en la proa, mareado, mirando la Virgen delante, sobre el agua. En el centro de la bahía, las barcas se paran. Se reza. Se sigue.",
            Parrafo.PENSAMIENTO to "El archivista piensa, en medio de la bahía, que nunca había estado tan lejos de la tierra, y que debajo de la barca hay una oscuridad que no se acaba. Piensa en los registros de los ahogados. Piensa que ahí abajo están todos. Se agarra a la borda.",
        ),
        "marea_baja" to unDia(
            Parrafo.MANANA to "Hoy es la noche de la marea más baja del año, la que en el pueblo llaman la noche seca, y desde la mañana se nota algo raro: las mujeres cierran las contraventanas de las casas que dan a la playa, los niños no salen, los perros están atados. Nadie dice por qué.",
            Parrafo.ANOCHECER to "Esta noche el mar se retira tanto que deja al aire la playa entera y unas rocas negras que no se ven nunca, y escaleras de piedra que bajan hacia el agua desde el muelle, muchas más de las que hay. Nadie baja a la playa. Nadie se asoma. El archivista mira desde la ventana. Hasta que la posadera la cierra.",
            Parrafo.PENSAMIENTO to "El archivista piensa en las escaleras que se ven en la marea más baja, y que no aparecen en ningún plano del archivo. Piensa que alguien las hizo. Que alguien bajaba por ellas. Piensa que nadie en el pueblo le contestará si pregunta. No pregunta.",
        ),
        "varada" to unDia(
            Parrafo.MANANA to "Hoy se acaba la temporada de la sardina, y las barcas se varan en la arena de la playa grande para el invierno, arrastradas con cuerdas y rodillos por todos los hombres del pueblo. Es un trabajo de todo el día. Las mujeres llevan vino y pan a la playa.",
            Parrafo.GENTE to "Esta tarde el archivista baja a la playa a ayudar a tirar de una cuerda, la primera vez que lo dejan. Los pescadores le hacen sitio sin decir nada. Tira con ellos. La barca se mueve. Cuando terminan, uno le da un trago de vino de su bota. Es lo más parecido a un saludo que le han dado.",
            Parrafo.PENSAMIENTO to "El archivista piensa, con las manos ardiendo de la cuerda, que hoy ha sido del pueblo durante una hora. Piensa que le ha gustado. Piensa que eso es peligroso, aunque no sabe por qué. Mañana, al archivo. Mañana, a ser otra vez el de fuera.",
        ),
        "difuntos" to unDia(
            Parrafo.MANANA to "Hoy es el día de difuntos, y todo el pueblo sube al cementerio de las rocas con flores, velas y comida, a pasar el día con los muertos: se limpian las tumbas, se reza, se come junto a ellas. Delante de la cruz de hierro de los que el mar no devolvió, se dejan conchas.",
            Parrafo.GENTE to "Esta tarde el archivista recorre el cementerio leyendo las tumbas, mientras las familias comen y hablan bajito con sus muertos. Una vieja le dice el nombre de cada uno de los suyos, y él le dice la fecha de su bautismo, que sabe del archivo. La vieja lo mira raro. Luego se lo agradece.",
            Parrafo.PENSAMIENTO to "El archivista piensa que él conoce a los muertos del pueblo mejor que los vivos: sus nombres, sus fechas, sus padrinos, sus causas de muerte. Piensa que algunos murieron dos veces, según los papeles. Piensa que hoy no es el día de pensar en eso.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, y anoche la misa del gallo llenó la iglesia hasta la puerta, con los pescadores de traje negro y las mujeres de mantilla, y un belén de figuras de barro donde los pastores eran pescadores y el portal, una barca. Nevó un poco sobre el mar.",
            Parrafo.TRABAJO to "Hoy el archivista no trabaja: es Navidad. Come en la posada con la posadera, la niña y unos parientes que han venido del monte: un besugo al horno, castañas, un dulce de almendra. Le dan un sitio en la mesa sin preguntar. Le sirven primero. Él no sabe qué decir.",
            Parrafo.PENSAMIENTO to "El archivista piensa en las Navidades de la ciudad, en su madre, en la casa con estufa y libros. Piensa que hace años que no la pasa allí. Piensa que esta mesa, con estos desconocidos, se parece más a una familia que nada de lo que tuvo. Lo piensa y se asusta un poco.",
        ),

        // ---------- Los de la temporada 1 ----------
        "legajo" to unDia(
            Parrafo.MANANA to "Hoy, en el fondo de una caja sin rótulo, el archivista ha encontrado un legajo de hace dos siglos atado con una cinta negra y sellado con lacre, que nadie ha abierto nunca: el lacre está entero. En el sello hay un dibujo gastado. No es el escudo del pueblo.",
            Parrafo.TRABAJO to "A esta hora abre el legajo sellado con un cuchillo de hueso, despacio, levantando el lacre sin romperlo. Dentro hay cartas, un plano del muelle y una lista de nombres con fechas, en dos columnas. La primera, de nacimientos. La segunda, no sabe de qué. Las fechas no coinciden con ningún registro.",
            Parrafo.PENSAMIENTO to "El archivista piensa que alguien cerró ese legajo para que nadie lo abriera, y que él lo ha abierto. Piensa que es su trabajo: abrir lo que está cerrado y ordenarlo. Piensa que nunca se le había ocurrido que eso pudiera estar mal. Ahora se le ocurre.",
        ),
        "barca" to unDia(
            Parrafo.MANANA to "Hoy, de madrugada, un temporal ha echado una barca del pueblo contra las rocas de la entrada de la bahía. Los hombres del pueblo bajaron con cuerdas y faroles, en la oscuridad, gritando nombres. Se salvaron todos. La barca está en las rocas, partida en dos.",
            Parrafo.GENTE to "Esta tarde el pueblo entero está en la playa, mirando la barca rota, mientras los cuatro que se salvaron beben en la taberna sin hablar. Uno dice que algo empujó la barca contra las rocas. Los otros lo hacen callar. El archivista, en la puerta, lo oye. Lo apunta.",
            Parrafo.PENSAMIENTO to "El archivista piensa en los registros de naufragios del archivo, que tiene a medio copiar, y en que hay muchos en esas mismas rocas, demasiados, casi siempre en la misma época del año. Piensa que es el viento. Piensa que siempre es el viento.",
        ),
        "nombre" to unDia(
            Parrafo.MANANA to "Hoy, copiando un registro de bautizos de hace cien años, el archivista ha leído un nombre que conoce: el de un niño bautizado este mes, el hijo de un pescador del muelle. El mismo nombre, el mismo apellido, el mismo padrino. Cien años antes.",
            Parrafo.TRABAJO to "A esta hora busca el nombre en los demás registros del archivo, hacia atrás, década por década. Lo encuentra cada cien años, exactamente: el mismo nombre, el mismo apellido, el mismo padrino, el mismo mes. Seis veces. La primera, hace seiscientos años, en latín.",
            Parrafo.PENSAMIENTO to "El archivista piensa que en los pueblos los nombres se repiten, que es costumbre, que no significa nada. Lo piensa tres veces, en voz baja, en el archivo vacío. Luego mira la fecha de la sexta vez. Luego cierra el registro. Luego lo abre otra vez.",
        ),
        "pez" to unDia(
            Parrafo.MANANA to "Hoy las barcas han vuelto de la pesca de la noche con las redes llenas de un pez que nadie sabe nombrar: plateado, plano, con unos ojos demasiado grandes y una aleta como una cresta. Los viejos dicen que no lo habían visto nunca. En la lonja nadie lo quiere comprar.",
            Parrafo.GENTE to "Esta tarde los peces raros están en un montón en el muelle, y las gaviotas no se los comen: los rodean, los miran, se van. Los pescadores los devuelven al mar a paladas, sin hablar. El archivista se guarda uno en un frasco con alcohol. La posadera le dice que lo tire.",
            Parrafo.PENSAMIENTO to "El archivista piensa que en el archivo hay un dibujo de un pez así, en un acta de hace doscientos años, con una nota que dice: el año de la pesca grande. No dice qué pasó después de la pesca grande. Piensa que tendrá que buscarlo. Piensa que no quiere.",
        ),
        "signo" to unDia(
            Parrafo.MANANA to "Hoy, al abrir el cuaderno donde apunta lo de cada día, el archivista ha visto en el margen de una página un dibujo a lápiz: un signo, como una letra de un alfabeto que no conoce, con tres trazos y un círculo. Está hecho con su lápiz. Con su mano. No lo recuerda.",
            Parrafo.TRABAJO to "A esta hora busca el signo en los papeles del archivo, en los sellos, en los márgenes, en los dibujos de los legajos. Lo encuentra en el lacre del legajo sellado, gastado. En una esquina del plano del muelle. En una piedra del cementerio, que dibujó la semana pasada sin fijarse.",
            Parrafo.PENSAMIENTO to "El archivista piensa que ha visto el signo tantas veces en el archivo que su mano lo ha aprendido sola, sin que él se diera cuenta, como se aprende una letra de niño. Piensa que es eso. Que tiene que ser eso. Lo piensa hasta que se lo cree. Casi.",
        ),
        "niebla" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy ha llegado del mar una niebla tan espesa que desde la ventana de la posada no se ve la calle, y la sirena del faro no ha parado de sonar desde el amanecer, un mugido cada minuto. Las barcas no han salido. Dicen que durará días. Los viejos dicen cuántos: cinco.",
                Parrafo.TRABAJO to "A esta hora trabaja en el archivo con la lámpara encendida a mediodía, porque por la ventana no entra luz, solo una blancura que se pega a los cristales. Los legajos están húmedos y blandos. Las letras parecen nadar. La sirena del faro marca los minutos. Pierde la cuenta de las horas.",
                Parrafo.PENSAMIENTO to "El archivista piensa que con esta niebla el pueblo está más solo que nunca: no se ve el mar, no se ve el monte, no se ve el faro, solo se oye. Piensa que el pueblo podría no estar en ningún sitio. Que podría estar en cualquier sitio. Que a lo mejor es eso lo que pasa con la niebla.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "Anochece en la niebla, y no se nota: la blancura se vuelve gris, el gris se vuelve negro, y la sirena sigue sonando cada minuto. En la calle se oyen pasos que no se ven. Alguien dice un nombre. Nadie contesta.",
                "Anochece, otra noche de niebla, y en el pueblo nadie sale. Las ventanas tienen velas. La sirena suena. En el muelle, alguien ha dejado un farol encendido en el último escalón, el que baja al agua. Nadie sabe quién.",
                "Anochece en la niebla, y desde su ventana el archivista ve, por un momento, la luz del faro atravesar la blancura, débil, como un ojo que se abre y se cierra. Luego, nada. La sirena. Nada. La sirena.",
            )),
        ),
    )

    val PIEZAS = Piezas(
        vida = "archivista", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + FIEBRE_MANANA + FARO_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + FIEBRE_TRABAJO + FARO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + FIEBRE_PUEBLO + FARO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + FIEBRE_ANOCHECER + FARO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
