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
 * El cuento de Albert, fogonero de locomotora en una línea del noreste de Inglaterra, de 1880 a 1884. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día es un turno de doce horas en la plataforma, entre el ténder y la puerta del fuego. Va y vuelve del depósito a
 *   pie y come dos veces: esos textos valen a cualquiera de sus horas.
 * - En las fiestas de los bancos los trenes de excursión van llenos: esos días tienen su propio párrafo de trabajo.
 * - Dos estados: «baja» (temporada 3, un mes en casa con el brazo quemado) y «maquinista» (temporada 5, desde que aprueba
 *   el examen: ya no palea, conduce).
 * - El pueblo es la línea y el depósito, y lee el arco «viaducto» (el viaducto nuevo sobre el valle).
 * - No repite lo que ya dice «Su vida»: el muchacho que limpiaba máquinas, las paladas cortas, el calor de frente y el
 *   viento de espaldas, el tocino frito en la pala, las pintas en la taberna, la meta de ser maquinista.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG6.kt`):
 * - Ellen, desde que la conoce (`ellen`, temporada 1); prometidos desde `pedida` (2); casados desde `boda` (3); espera
 *   desde `espera` (3); Annie, desde que nace (`nace`, 4) y andando desde `annie_anda` (5).
 * - Tom, su hermano, va a la escuela hasta `tom` (temporada 2) y desde entonces baja a la mina.
 * - Hebburn conduce la máquina hasta que Albert aprueba (`aprobado`, temporada 5).
 * - Las fiestas que vuelven cada año no nombran a Ellen, a Annie ni a Hebburn.
 */
internal object PiezasAlbert {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(11, 12, 1, 2)

    private val MANANA = listOf(
        v("A {nombre} lo despierta el llamador del depósito, un muchacho que va de puerta en puerta golpeando las ventanas con una vara larga, a la hora en que todavía no ha cantado ningún gallo. {nombre} contesta con un golpe en el cristal. El muchacho sigue a la casa de al lado."),
        v("{nombre} se despierta solo, un minuto antes de que llamen a la ventana, como siempre. Doce años de turnos le han puesto un reloj dentro. Se viste a oscuras: el pantalón de pana, el chaleco, la chaqueta azul, la gorra. En la cocina, el fuego dormido. Lo despierta él."),
        v("{nombre} sale de casa cuando la calle todavía es negra, y en muchas casas hay una luz encendida en la cocina: las de los que entran al primer turno, en la mina o en el depósito. Los vecinos se saludan sin palabras, con la cabeza, cada uno camino de lo suyo."),
        v("Lo primero que hace {nombre} al levantarse es mirar por la ventana el humo de las chimeneas del barrio: si sube derecho, no hay viento; si se tumba hacia el río, lo hay de cara. Hoy se tumba. Será un día de muchas paladas."),
        v("{nombre} se despierta con el olor del carbón, que no se le quita nunca: lo tiene en el pelo, en la piel, en la almohada. Se lava la cara y el cuello en la palangana con agua fría, frotando fuerte. El agua se pone gris. Mañana, otra vez."),
        v("A {nombre} lo despierta el silbato de un tren de mercancías que pasa de noche por el puente del río, largo, tres veces. Por el silbato sabe qué máquina es y quién la lleva. Es la del viejo Nichols. Va tarde, como siempre. {nombre} sonríe y se levanta."),
        v("{nombre} se levanta con escarcha en la ventana y el agua de la palangana dura como una losa. Rompe el hielo con el puño. Se pone dos camisas y la bufanda que le tejió su madre. En la plataforma, con el fuego delante, se las quitará. Con el viento de espaldas, se las volverá a poner.", meses = INVIERNO),
    )

    /** Un mes en casa con el brazo quemado, en la temporada 3 (estado «baja»). */
    private fun ba(texto: String, hacer: Hacer? = null) = v(texto, estado = "baja", hacer = hacer)

    private val BAJA_MANANA = listOf(
        ba("{nombre} se despierta a la hora de siempre, cuando pasa el llamador, y se queda en la cama mirando el techo, porque hoy tampoco va al depósito. Tiene el brazo vendado hasta el codo, y le late. Ellen ya está abajo. Se oye la tetera."),
        ba("A {nombre} lo despierta el dolor del brazo, que de noche se olvida de la venda y se roza con la sábana. Se levanta despacio. Por la ventana ve pasar a los del depósito, con sus latas, camino del turno. Él no. Se siente como un caballo en el prado."),
        ba("{nombre} se despierta tarde, por primera vez en años, con el sol ya en la ventana, y no sabe qué hacer con tanta mañana. Ellen le cambia la venda en la cocina, con agua hervida y un ungüento que le dio el boticario. No mira la herida. Él tampoco."),
    )

    private val BAJA_TRABAJO = listOf(
        ba("A esta hora no trabaja: tiene el brazo quemado y la compañía le da una parte del jornal mientras se cura. Se sienta en el escalón de la puerta con el brazo en alto y mira las nubes. Le parece que hacer nada cansa más que palear.", Hacer.TRABAJO),
        ba("A esta hora ayuda a Ellen en lo que puede con una sola mano: sostener la cesta de la ropa, desgranar guisantes, sujetar la madeja mientras ella la enrolla. Lo hace mal y ella se ríe. Dice que un fogonero con una mano es un estorbo con dos pies.", Hacer.TRABAJO),
        ba("A esta hora baja al depósito solo a mirar, con el brazo en cabestrillo, y se queda en la puerta viendo encender las máquinas. Los compañeros le preguntan por la herida. Hebburn, desde la plataforma, le dice que su máquina lo echa de menos. Que se dé prisa.", Hacer.TRABAJO),
        ba("A esta hora camina despacio por la orilla del río, con el brazo en cabestrillo, porque el médico le dijo que andar le haría bien. Los niños del barrio le preguntan qué le pasó. Él les dice que lo mordió una locomotora. Los niños se quedan pensando.", Hacer.CAMINO),
        ba("A esta hora come en la cocina, con una cuchara en la mano izquierda, que no sabe usar: la sopa se le cae, el pan se le escapa. Ellen le corta la carne en trozos pequeños, como a un niño. Él protesta. Ella no le hace caso.", Hacer.COMER),
        ba("A esta hora duerme mal, de lado, con el brazo encima de una almohada para que no le roce nada. Cada vez que se gira, se despierta. Ellen, al lado, se despierta con él, le dice algo bajito y se vuelve a dormir antes de terminar la frase.", Hacer.DORMIR),
        ba("A esta hora está en la taberna con los del depósito, con una pinta en la mano buena, y todos quieren ver la quemadura. No la enseña. Le cuentan las novedades del depósito: quién lleva su máquina, qué fogonero le pusieron a Hebburn, cuánto se queja Hebburn.", Hacer.GENTE),
    )

    private val BAJA_PUEBLO = listOf(
        ba("Desde la casa se oye el depósito todo el día: los silbatos, el golpe de los topes, el vapor que escapa. {nombre} los reconoce todos. Sabe qué tren sale, cuál llega tarde, cuál se ha quedado parado. Es como oír una casa desde el cuarto de al lado."),
        ba("Su calle, de día, es de las mujeres y de los niños: las mujeres tienden, barren los escalones, hablan de puerta a puerta; los niños juegan en el adoquinado. {nombre} no la había visto nunca así. Se siente un intruso en su propia calle."),
    )

    private val BAJA_ANOCHECER = listOf(
        ba("Al caer la tarde vuelven del depósito los del turno, negros de carbón, y la calle se llena de ruido de botas. {nombre} los mira desde la puerta. Algunos se paran a preguntarle por el brazo. Él les pregunta por las máquinas. Hablan un rato. Luego se van a lavar."),
        ba("Anochece, y Ellen le cambia la venda junto a la lámpara. La piel nueva está rosa y tirante, brillante. Ellen dice que ya falta poco. {nombre} abre y cierra la mano, despacio, para ver si todavía es la suya. Lo es."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la plataforma el tiempo se lleva encima: lo que cae del cielo cae sobre el fogonero."),
        v("{nombre} mira el cielo al salir de casa, por si hay que llevar el capote de hule. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Hebburn dice que las máquinas tienen frío como las personas, y que en invierno tiran peor."),
        v("{cielo} En {estacion} los raíles cambian: resbalan con la lluvia y la hoja, se dilatan con el sol, se hielan. {nombre} lo nota en las ruedas."),
        v("Desde la plataforma, a toda marcha, el campo pasa como un río. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En el norte se dice que si no te gusta el tiempo, esperes un cuarto de hora."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, y el humo de la máquina se ve desde muy lejos, como una pluma blanca.",
            "Hace un día claro, de los raros en el norte, y se ve el mar desde lo alto de la cuesta.",
            "No hay nubes, y el sol hace brillar los raíles hasta donde se ve.",
        ),
        Clima.NUBES to listOf(
            "El cielo está gris y bajo, del color del humo, y no se sabe dónde acaba el uno y empieza el otro.",
            "Pasan nubes pesadas que vienen del mar, y el campo cambia de luz cada minuto.",
            "Está nublado, como casi siempre aquí.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y la lluvia entra de lado en la plataforma y chisporrotea al tocar la puerta del fuego.",
            "Cae una lluvia fina y fría que no moja hasta que estás empapado.",
            "Llueve, y los raíles resbalan, y la máquina patina en las cuestas.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los rayos caen en el páramo y la lluvia golpea el ténder como piedras.",
            "Truena sobre las colinas, y los caballos de los campos corren junto a la vía, asustados.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y la vía desaparece bajo la nieve, y solo se ve por las dos rayas negras del tren de delante.",
            "Cae nieve sobre el páramo, y las trincheras de la línea se llenan hasta arriba.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, de la espesa, y las señales aparecen de golpe, demasiado cerca.",
            "La niebla del mar sube por el valle y se traga el viaducto, los árboles y las casas.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento del este que viene del mar y corta como un cuchillo.",
            "Hace viento de cara, y la máquina tiene que tirar el doble.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en los terraplenes de la línea salen las prímulas.",
            "En primavera los corderos corren junto a la vía, sin miedo al tren.",
            "Es el tiempo de los chaparrones y de los días que se alargan.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y en la plataforma, con el fuego delante y el sol encima, no se puede respirar.",
            "En verano los trenes de excursión van llenos de gente que va a ver el mar.",
            "Es el tiempo del heno en los campos y de las moscas en el depósito.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando las hojas mojadas hacen patinar las ruedas en las cuestas.",
            "En otoño la niebla sube del río cada mañana y se queda hasta el mediodía.",
            "Es el tiempo de los vendavales que vienen del mar.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando el agua de la caldera tarda en hervir y las manos se pegan al hierro.",
            "En invierno se sale y se vuelve de noche, y el día solo se ve desde la plataforma.",
            "Es el tiempo de la nieve en las trincheras y del hielo en los depósitos de agua.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora palea carbón en la plataforma, de pie, balanceándose con la máquina: abre la puerta del fuego con el pie, mete la pala, la sacude para que el carbón se reparta por el hogar, cierra. El calor le da en la cara como una bofetada. Una palada más. Otra.", hacer = Hacer.TRABAJO),
        v("A esta hora vigila el nivel del agua en el tubo de cristal de la caldera, que baila con los baches de la vía. Si el agua baja demasiado, la caldera revienta. Si sube, el agua sale por la chimenea. Abre el inyector. Escucha. El agua entra con un silbido.", hacer = Hacer.TRABAJO),
        v("A esta hora prepara el fuego antes de la cuesta larga, como le enseñó Hebburn: un fuego grueso y blanco por detrás, fino por delante, sin agujeros. Si llega a la cuesta con mal fuego, la máquina se para a media subida, y el maquinista no se lo perdona en un mes.", hacer = Hacer.TRABAJO),
        v("A esta hora carga agua en la torre de una estación, subido al ténder, sujetando la manga de cuero que echa agua a chorros mientras el maquinista mira el reloj. Se moja hasta los huesos. A los dos minutos, el fuego de la caldera lo seca otra vez.", hacer = Hacer.TRABAJO),
        v("A esta hora mira la vía por su lado, asomado a la plataforma, buscando las señales: los brazos de los semáforos, arriba vía libre, abajo peligro. Las canta en voz alta, como manda el reglamento. El maquinista contesta. Así dos veces, cada señal, todo el turno.", hacer = Hacer.TRABAJO),
        v("A esta hora limpia la máquina en el depósito, al final del turno, con estopa y aceite: las bielas, las ruedas, los bronces de la cabina, la chapa verde del costado. Una máquina sucia es una vergüenza para su gente. La de {nombre} brilla como un reloj.", hacer = Hacer.TRABAJO),

        v("A esta hora come en la cocina, en la mesa de siempre: pan, tocino, lo que haya sobrado del guiso y una taza de té tan dulce que la cuchara se queda de pie. Come mucho y rápido, como comen los que palean, sin hablar. En la cocina solo se oyen la cuchara y el reloj de la pared.", hacer = Hacer.COMER),
        v("A esta hora come junto al fuego de la cocina, con las botas todavía puestas: un plato de gachas de avena con sal, pan con manteca y té. Mientras come se calienta los pies en el guardafuegos, y el vapor le sale de los calcetines como de una máquina pequeña.", hacer = Hacer.COMER),
        v("A esta hora come lo que hay, que es lo mismo casi todos los días: pan, queso, una cebolla cruda que muerde como una manzana, y té. A veces, un arenque. Los domingos, carne. Un fogonero come el doble que un escribiente y gana la mitad. Las cuentas no salen nunca.", hacer = Hacer.COMER),

        v("A esta hora camina entre su casa y el depósito, por la orilla del río, con la lata del té colgando de la mano y la gorra calada. Se lo sabe con los ojos cerrados: el puente de hierro, la tapia del cementerio, la esquina de la taberna.", hacer = Hacer.CAMINO),
        v("A esta hora cruza el puente de hierro sobre el río, que tiembla cuando pasa un tren por el de al lado. Debajo, el agua negra, las barcazas de carbón, las gaviotas. Encima, el cielo, del color que tenga. {nombre} silba una canción de la taberna.", hacer = Hacer.CAMINO),
        v("A esta hora atraviesa las vías del patio del depósito, saltando de una traviesa a otra, entre las máquinas que se encienden y las que se apagan. Hay que mirar a los dos lados siempre. Un fogonero del depósito perdió una pierna aquí hace poco.", hacer = Hacer.CAMINO),

        v("A esta hora duerme como un tronco, en la cama, con el olor del carbón en la almohada. Ni los trenes de la noche, que pasan por el puente, lo despiertan. Sueña con el fuego: que se apaga, que no prende, que la cuesta no se acaba.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, cansado de doce horas de pie, con las piernas que todavía se balancean con la máquina, como los marineros en tierra. A veces se despierta creyendo que se le ha pasado el turno. Mira por la ventana. Es de noche todavía.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, y en la casa de al lado, a través de la pared, ronca el vecino, que es guardafrenos. Cuando el vecino se calla, {nombre} se despierta. Cuando vuelve a roncar, se duerme. Ya se ha acostumbrado.", hacer = Hacer.DORMIR),

        v("A esta hora está en la taberna de la esquina con los del depósito, con una pinta de cerveza negra, junto al fuego. Se habla de la compañía, que paga poco y exige mucho; de los maquinistas, que se creen reyes; del sindicato, que dice que algún día. Se canta un poco.", hacer = Hacer.GENTE),
        v("A esta hora juega a los dardos en la taberna contra un guardafrenos que no pierde nunca. Pierde. Paga la ronda. Los del depósito se ríen. {nombre} dice que en la plataforma no se puede practicar la puntería. El guardafrenos dice que en el vagón de cola tampoco.", hacer = Hacer.GENTE),
        v("A esta hora está en la puerta de su casa, sentado en el escalón, fumando en pipa con el vecino, mirando la calle. Pasan los niños, pasan las mujeres con los cántaros, pasa el sereno. Se habla poco. Lo justo. Luego, a dormir: mañana hay turno.", hacer = Hacer.GENTE),
    )

    /** A los mandos, desde que aprueba el examen (estado «maquinista», temporada 5). */
    private fun ma(texto: String, hacer: Hacer? = null) = v(texto, estado = "maquinista", hacer = hacer)

    private val MAQUINISTA_MANANA = listOf(
        ma("{nombre} se despierta antes que el llamador, como siempre, pero hoy se viste distinto: la chaqueta azul de maquinista, que le cosió Ellen con los botones de la compañía, y la gorra nueva. Se mira en el espejo de la cocina. Ellen, desde la escalera, se ríe de él."),
        ma("A {nombre} lo despierta el llamador con su vara, y por primera vez en su vida no es él quien tiene que llegar antes para encender el fuego: eso lo hace ahora su fogonero. Se queda un minuto más en la cama. Solo uno. Luego se levanta igual que siempre."),
        ma("{nombre} sale de casa a oscuras, camino del depósito, con la lata del té y un cuaderno pequeño donde apunta las horas de cada estación. Los vecinos lo saludan de otra manera desde que es maquinista. Él les contesta igual que siempre. Le cuesta."),
    )

    private val MAQUINISTA_TRABAJO = listOf(
        ma("A esta hora conduce la máquina, de pie en el lado derecho de la plataforma, con la mano en el regulador y la otra en la palanca de cambio, mirando la vía por la ventanilla. Detrás de él, el fogonero nuevo palea. {nombre} le mira el fuego sin decir nada. Está bien. Casi.", Hacer.TRABAJO),
        ma("A esta hora sube la cuesta larga con un tren de carbón de cuarenta vagones, abriendo el regulador poco a poco, escuchando la máquina como un médico escucha un pecho. Si patina, arena. Si se ahoga, paciencia. En lo alto, cierra. Detrás, el fogonero se seca la frente.", Hacer.TRABAJO),
        ma("A esta hora le enseña al fogonero nuevo, un muchacho de diecinueve años, a preparar el fuego para la cuesta, como Hebburn se lo enseñó a él: grueso por detrás, fino por delante. El muchacho no lo hace bien. {nombre} se acuerda de cuando él tampoco. Se lo repite sin gritar.", Hacer.TRABAJO),
    )

    private val MAQUINISTA_ANOCHECER = listOf(
        ma("Al caer la tarde {nombre} deja la máquina en el depósito y firma la hoja del turno con su nombre y una palabra nueva al lado: maquinista. Se queda mirándola un momento. Luego se va a casa a lavarse. Annie lo espera en la puerta."),
        ma("Anochece, y {nombre} vuelve a casa por la orilla del río, con las manos menos negras que antes, porque ya no palea. Las mira. Le parecen de otro. Piensa en Hebburn, que se jubila ahora que él conduce. Mañana irá a verlo a su huerto."),
    )

    private val PUEBLO = listOf(
        v("{entorno} El depósito es una nave enorme de ladrillo con un tejado de cristal negro de hollín, rieles por todas partes, fosos para mirar las máquinas por debajo y una placa giratoria en el patio. Huele a carbón, a aceite caliente y a vapor. Es la casa de {nombre} más que su casa."),
        v("La línea va del depósito a la costa por el valle del río: minas, pueblos de ladrillo, campos de piedra, el páramo arriba, y abajo el mar. Por ella bajan los trenes de carbón al puerto y suben los de pasajeros a las ciudades. {entorno}"),
        v("{entorno} Junto a la línea, las minas: castilletes de hierro, montañas de escoria, pueblos enteros de casas iguales en hileras. El carbón que palea {nombre} sale de ahí abajo, de la misma tierra, y vuelve a ella en humo."),
        v("La máquina de {nombre} es una seis ruedas acopladas, verde con filetes negros, con su número de bronce en la chimenea y su ténder cargado de carbón y de agua. No es la más rápida del depósito. Es la que mejor sube. Hebburn dice que tiene el carácter de una mula buena. {entorno}"),
        v("{entorno} La calle de los ferroviarios son dos hileras de casas de ladrillo, iguales, con un patio detrás y un retrete al fondo del patio. Las construyó la compañía para su gente, y hay lista de espera. {nombre} pasa por ella cada día, camino del puente, y mira las ventanas.", hasta = "casa"),
        v("{entorno} La calle de los ferroviarios son dos hileras de casas de ladrillo, iguales, con un patio detrás y un retrete al fondo del patio. Las construyó la compañía. Viven en ellas maquinistas, fogoneros, guardafrenos, guardagujas, y ahora {nombre}. Todos se levantan a la misma hora.", desde = "casa"),
        v("En la estación de la costa, al final de la línea, la máquina descansa media hora antes de volver, y se ve el mar entre los tejados. El jefe de esa estación, un escocés pelirrojo, le guarda siempre a {nombre} una taza de té caliente. {entorno}"),
        v("En las estaciones del camino, el jefe de estación con su gorra galoneada, el mozo con la carretilla, las señoras con sombrero, los mineros con su lata, los niños que saludan al tren. {nombre} los ve pasar todos los días y no conoce a ninguno. Ellos lo saludan igual. {entorno}"),
    )

    /** El viaducto nuevo sobre el valle (arco «viaducto»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("viaducto", 0.0, 0.02, frases = listOf(
            "La línea baja al fondo del valle y vuelve a subir por una cuesta que se come el carbón.",
            "Todavía no hay viaducto: el tren da un rodeo por el valle, y la cuesta de la otra orilla es la peor de la línea.",
        )),
        Entorno("viaducto", 0.02, 0.5, frases = listOf(
            "En el valle crecen los pilares del viaducto nuevo, rodeados de andamios y de grúas de vapor.",
            "En el fondo del valle trabajan cientos de hombres en los pilares del viaducto, y el tren pasa despacio a su lado.",
        )),
        Entorno("viaducto", 0.5, 0.99, frases = listOf(
            "Los arcos del viaducto ya cruzan medio valle, de piedra rubia, y los albañiles parecen hormigas allá arriba.",
            "Al viaducto le faltan pocos arcos, y desde la plataforma se ve cómo se cierran sobre el valle.",
        )),
        Entorno("viaducto", 0.99, 1.0, frases = listOf(
            "El tren cruza el valle por el viaducto nuevo, alto como una iglesia, y abajo se ven los tejados y el río.",
            "Desde que está el viaducto, la cuesta de la otra orilla ya no existe, y la máquina pasa por encima del valle como un pájaro.",
        )),
    )

    private val GENTE = listOf(
        v("{nombre} vive todavía con su madre y con Tom en la casa donde nacieron, en la calle de los mineros. Su madre es viuda: su padre murió en la mina cuando {nombre} tenía diez años. Lava ropa para fuera y no se queja nunca. Le dice a {nombre} que se lave el cuello. Siempre.", hasta = "boda"),
        v("Su madre vive con Tom en la casa donde nacieron los dos, en la calle de los mineros, a diez minutos. Es viuda: su padre murió en la mina cuando {nombre} tenía diez años. Viene los domingos, mira la cocina de Ellen sin decir nada, y le dice a {nombre} que se lave el cuello.", desde = "boda"),
        v("Los del depósito son su gente: fogoneros, maquinistas, limpiadores, el capataz, el muchacho del llamador. Se ven más que a sus familias. Se pelean, se ayudan, se emborrachan juntos, van a los entierros de los otros. Si la compañía echa a uno, se enfadan todos."),
        v("El capataz del depósito, el señor Thwaites, es un hombre gordo con reloj de cadena que fue maquinista treinta años y que sabe qué máquina tiene fiebre por cómo suena desde la oficina. A {nombre} lo trata bien porque nunca llega tarde. Es su manera de querer."),
        v("Nichols es el maquinista más viejo del depósito, el que lleva el mercancías de la noche, con una barba blanca manchada de hollín y una pipa que no se quita nunca. Conduce desde que había máquinas sin cabina. Dice que los de ahora se quejan de vicio."),
        v("Sid, el llamador del depósito, es un muchacho de catorce años, flaco como una vara, que se pasa la noche despierto en el cuarto del capataz y sale a despertar a los del primer turno. Sabe dónde vive cada uno y cómo duerme cada uno. Quiere ser fogonero. Todos los llamadores quieren."),
        v("Walter es otro fogonero del depósito, de su misma edad, compañero de la escuela y de la taberna. Le gana siempre a los dardos y a las cartas, y le presta dinero cuando hace falta. Quiere irse a América. Lleva años queriendo."),
        v("Tom, su hermano pequeño, va todavía a la escuela, a regañadientes, y por las tardes se escapa al depósito a ver las máquinas. Quiere ser fogonero, como {nombre}. Su madre quiere que sea cualquier cosa menos minero. {nombre} también.", hasta = "tom"),
        v("Tom, su hermano, baja a la mina desde los doce años, como bajó su padre: entra a oscuras, sale a oscuras, y los domingos duerme. Se ha puesto ancho y callado. {nombre} intenta que la compañía lo coja de limpiador en el depósito. Todavía no.", desde = "tom"),
        v("Hebburn, el maquinista de su máquina, es un hombre seco, de bigote gris, que habla poco y nunca grita, y que sabe todo lo que se puede saber de una locomotora. Le enseñó a {nombre} a hacer el fuego, a leer la vía y a callarse. Lo trata como a un hijo que todavía no está terminado.", hasta = "aprobado"),
        v("Hebburn se jubiló cuando {nombre} aprobó el examen, como si hubiera estado esperando a eso. Ahora cultiva puerros en un huerto junto a la vía y saluda a los trenes con la gorra. Cuando pasa la máquina de {nombre}, ella le silba dos veces.", desde = "aprobado"),
        v("Ellen trabaja en una mercería de la calle mayor, detrás de un mostrador lleno de botones, cintas y carretes, y tiene una risa que se oye desde la puerta. {nombre} pasa por la mercería más de lo que necesita botones. El dueño ya lo sabe.", desde = "ellen", hasta = "pedida"),
        v("Ellen y {nombre} están prometidos, y se casarán cuando él tenga ahorrado lo de los muebles. Los domingos pasean por la orilla del río, ella del brazo, él con la camisa blanca que solo se pone ese día. Ella le enseña a no hablar de máquinas todo el rato. Le cuesta.", desde = "pedida", hasta = "boda"),
        v("Ellen, su mujer, lleva la casa como llevaba la mercería: todo en su sitio, todo contado, nada desperdiciado. Le prepara la lata cada noche, le lava el carbón de la ropa y le hace leer en voz alta el periódico del domingo, para que no se le olviden las letras.", desde = "boda", hasta = "nace"),
        v("Annie, su hija, tiene los ojos de Ellen y los pulmones de una locomotora: cuando llora se oye desde el depósito, dicen los vecinos. Cuando {nombre} llega del turno, negro de carbón, lo primero que quiere es cogerla. Ellen dice que primero se lave. Él la coge igual.", desde = "nace"),
    )

    private val OBJETO = listOf(
        v("Hoy, en el turno, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de Hebburn, que lo aprendió de otro, cuando las máquinas no tenían ni techo."),
        v("En la plataforma cada cosa tiene su sitio, y lo que se mueve de su sitio se cae a la vía. {dato_objeto}"),
        v("{dato_objeto} Los pasajeros no saben nada de eso. No les hace falta: para eso están los de la plataforma."),
        v("{dato_objeto} Son cosas que se saben con el cuerpo, como se sabe bajar una escalera a oscuras."),
        v("Un ferroviario tiene pocas cosas, pero las cuida como un cura sus cálices. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("pala", "la pala", "una pala de fogonero", "La pala",
            "La pala del fogonero es ancha y corta, de mango gastado por las manos, y su hoja brilla de tanto rozar el carbón; un fogonero la mueve miles de veces por turno; limpia, sobre el fuego, sirve también de sartén."),
        ObjetoCuento("carbon", "el carbón", "un trozo de carbón", "El carbón",
            "El carbón de las locomotoras es de las minas de la misma comarca, negro y brillante, y se rompe en trozos del tamaño de un puño para que arda parejo; el bueno hace poco humo y poca escoria; el malo ahoga el fuego y la paciencia."),
        ObjetoCuento("lata", "la lata del té", "una lata de té", "La lata",
            "La lata del ferroviario es un cazo de hojalata con tapa y asa, donde se lleva el té y, encima, en una bandeja, el pan; en la plataforma se calienta en la puerta del fuego; cada uno conoce la suya por las abolladuras."),
        ObjetoCuento("manometro", "el manómetro", "un manómetro", "El manómetro",
            "El manómetro es un reloj redondo de latón, en lo alto de la caldera, con una aguja que dice la presión del vapor; tiene una raya roja que no se debe pasar; el fogonero lo mira más que a su propia novia."),
        ObjetoCuento("silbato", "el silbato", "un silbato de vapor", "El silbato",
            "El silbato de la locomotora es de bronce, en lo alto de la cabina, y se toca con una cadena; cada maquinista tiene su manera de silbar, y en los pueblos de la línea saben quién pasa por el silbido; un silbido largo en la noche es la voz del tren."),
        ObjetoCuento("farol", "el farol", "un farol", "El farol",
            "El farol de la máquina es de aceite, con un cristal grueso, y se cuelga en el frente de la locomotora; los de las señales cambian de color con un cristal rojo o verde; de noche, la línea es una fila de luces que el fogonero tiene que leer."),
        ObjetoCuento("gorra", "la gorra", "una gorra de visera", "La gorra",
            "La gorra del ferroviario es de paño azul con visera de charol, y la del maquinista lleva una cinta o un número; en la plataforma se lleva al revés, con la visera hacia atrás, para que el viento no se la lleve."),
        ObjetoCuento("reloj", "el reloj", "un reloj de bolsillo", "El reloj",
            "El reloj de bolsillo del maquinista tiene que estar en hora con el de la estación, que está en hora con el del telégrafo, que está en hora con Londres; un tren que llega dos minutos tarde es una falta; uno que llega dos minutos antes, un peligro."),
        ObjetoCuento("estopa", "la estopa", "un puñado de estopa", "La estopa",
            "La estopa son los restos de algodón y de cáñamo que sobran en las fábricas, y en el depósito se usan para limpiarlo todo: las manos, las bielas, el sudor; siempre hay un puñado metido en el bolsillo de la chaqueta."),
        ObjetoCuento("horario", "el horario", "un horario de trenes", "El horario",
            "El horario de servicio es un libro pequeño, impreso por la compañía, con las horas de todos los trenes de la línea, los cruces y las paradas; los maquinistas se lo saben de memoria y lo llevan igual en el bolsillo, por si acaso."),
        ObjetoCuento("pipa", "la pipa", "una pipa", "La pipa",
            "La pipa de barro o de raíz se fuma en la taberna, en el escalón de la puerta y en el cuarto del depósito, nunca en la plataforma; el tabaco negro de picadura se compra por onzas en el estanco de la esquina."),
        ObjetoCuento("carnet", "el carné del sindicato", "un carné del sindicato", "El carné",
            "El carné del sindicato de los ferroviarios es un librito de tapas de cartón donde se apuntan las cuotas pagadas, con un sello por mes; con él se tiene derecho a ayuda si hay accidente, enfermedad o entierro; la compañía prefiere no saber quién lo tiene."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas en la taberna, cuando se habla de ellas, y se le olvidan en la plataforma, donde solo hay fuego y vía. A sus {edad} años sabe que el mundo lo mueven las máquinas, y que las máquinas las mueven los de la pala."),
        v("Piensa a veces en su padre, que bajaba a la mina antes del alba y no veía el sol en todo el invierno, y que murió debajo de la tierra. Él trabaja al aire, con el cielo encima, aunque llueva. Le parece que ya subió un escalón. Quiere subir otro: el regulador."),
        v("{mundo} {nombre} piensa en la compañía, que tiene accionistas en Londres que nunca han visto una locomotora, y en los dividendos, que salen de su sudor y del de todos. No es socialista. Pero lo piensa. Y lo piensa más en la taberna, cuando habla Walter."),
        v("{mundo} A veces, en la plataforma, de noche, con el fuego abierto iluminándolo todo de rojo, {nombre} piensa que es el hombre más poderoso del mundo: lleva cuatrocientas toneladas con una pala. Luego se cierra la puerta del fuego, y vuelve a ser un fogonero."),
        v("Piensa en los accidentes, en los que ha visto y en los que le han contado: un puente que se cae, un eje que se parte, una señal que nadie vio. Piensa que cada día que vuelve a casa es un día ganado. No lo dice. Los ferroviarios no hablan de eso."),
        v("{mundo} A sus {edad} años, {nombre} ha recorrido más millas que un marinero sin salir del condado. Conoce cada curva de la línea, cada cuesta, cada puente. Si le vendaran los ojos, sabría dónde está por cómo suena la vía. Es lo que sabe hacer, y lo sabe bien."),
    )

    private val MUNDOS = mapOf(
        "eleccion" to listOf(
            "Se habla de Gladstone y de Disraeli, que se pelean por el país como dos perros viejos por un hueso.",
            "Se habla del puente del Tay, y de si los puentes de hierro son de fiar.",
            "Se dice que las compañías de ferrocarril ganan más que nunca y pagan lo mismo que siempre.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} deja la máquina en el depósito, entrega el turno y se lava en el lavabo de los ferroviarios, con un jabón que no quita el carbón sino que lo reparte. Luego, a casa, con la lata vacía colgando. Las piernas todavía le tiemblan del balanceo."),
        v("Anochece, y en su calle se encienden las lámparas de las cocinas, una tras otra. Huele a carbón de chimenea, que es otro olor que el de la máquina, más dulce. {nombre} llega a casa y se quita las botas en el escalón."),
        v("Al caer la tarde, desde el puente del río, {nombre} ve pasar los trenes que vuelven: los de pasajeros con sus ventanas iluminadas, los de carbón, largos, negros, lentos. Cada uno con su fogonero dentro. Los saluda con la mano. Ellos le silban."),
        v("De noche, desde la ventana del cuarto, se ve el resplandor rojo del depósito, donde las máquinas duermen con el fuego tapado, y de vez en cuando un silbato, un golpe de topes, un chorro de vapor. {nombre} lo oye como se oye la respiración de alguien que duerme al lado."),
        v("La última luz del día se queda en lo alto del castillete de la mina, a lo lejos, cuando la calle ya está a oscuras. Suena la sirena del cambio de turno de la mina. {nombre} piensa en su madre, que la oía cada noche esperando a su padre."),
        v("Anochece, y la niebla sube del río y se mete por las calles, y las lámparas de gas se ponen borrosas, como velas en una iglesia. {nombre} vuelve a casa con la bufanda hasta la nariz. En la esquina, la taberna, con su luz amarilla. Pasa de largo. Hoy, no."),
        v("Al anochecer, en invierno, ya hace horas que es de noche cuando {nombre} sale del depósito. No ha visto la luz del día más que desde la plataforma, de lado, a toda marcha. Llega a casa helado y negro. El fuego de la cocina es el mejor fuego del mundo.", meses = INVIERNO),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y como manda la costumbre del norte, el primero que cruzó la puerta de la casa después de medianoche fue un hombre moreno, con un trozo de carbón, pan y sal, para que no falte nada en el año. Fue {nombre}, que es moreno. Lo hace cada año.",
            Parrafo.GENTE to "Esta noche todavía se va de casa en casa por la calle, con una botella, a desear buen año: se entra, se bebe un trago, se come un trozo de pastel, se sale. Se habla de anoche, cuando a las doce en punto las máquinas del depósito silbaron todas a la vez y se oyó en todo el valle.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza y en lo que le pide: que no haya accidentes, que la compañía no baje los jornales, que la máquina no se rompa en una cuesta. Luego, bajito, pide lo otro: el regulador. Algún día.",
        ),
        "martes_tortitas" to unDia(
            Parrafo.MANANA to "Hoy es martes de carnaval, el día de las tortitas, el último antes de la cuaresma, y en todas las cocinas de la calle se fríen tortitas con lo que queda de huevos y de manteca. Huele a mantequilla quemada desde la esquina. Las campanas tocan al mediodía para que empiece la fiesta.",
            Parrafo.GENTE to "Al volver del turno, {nombre} se encuentra la cocina llena de humo y de risas, y una pila de tortitas con azúcar y limón. Se las come de pie, quemándose los dedos. En la calle los niños corren con sartenes, tirando las tortitas al aire. Alguna se queda en el tejado.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la cuaresma, para un fogonero, no cambia mucho: se come lo mismo, que es poco y mucho a la vez. Pero las tortitas son las tortitas. Se come la última, que siempre es la mejor, y piensa que mañana vuelve el pan con queso.",
        ),
        "lunes_pascua" to unDia(
            Parrafo.MANANA to "Hoy es lunes de Pascua, día de fiesta de los bancos, y media comarca quiere ir a la costa en tren: familias enteras con sus cestas, muchachos con sombrero de paja, viejos con sus nietos. La compañía pone trenes de excursión, uno detrás de otro. A los de la plataforma no les toca fiesta.",
            Parrafo.TRABAJO to "A esta hora lleva un tren de excursión de doce coches llenos hasta los topes, que canta todo el camino. En las estaciones la gente se asoma, saluda, pide más velocidad. En la costa bajan todos corriendo hacia el mar. A la vuelta vienen quemados del sol y dormidos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los días de fiesta de los demás son los días de más trabajo de los suyos. No le importa. Le gusta llevar un tren lleno de gente contenta. Piensa que algún día él también será de los que van en el coche, con una cesta, a ver el mar.",
        ),
        "lunes_pentecostes" to unDia(
            Parrafo.MANANA to "Hoy es lunes de Pentecostés, otro día de fiesta de los bancos, y las escuelas dominicales de toda la comarca se van de excursión: los niños desfilan por las calles con sus estandartes y su ropa nueva, y luego llenan los trenes hacia el campo o hacia el mar.",
            Parrafo.TRABAJO to "A esta hora lleva un tren lleno de niños de una escuela dominical, con sus maestras y sus pasteles, que cantan himnos y gritan cada vez que el tren entra en un túnel. En una curva, {nombre} toca el silbato tres veces seguidas, solo para ellos. Los oye gritar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en cuando él iba a la excursión de la escuela dominical, con su padre y su madre, en un tren como este. Su padre le dejó asomarse por la ventanilla. Fue la primera vez que vio una locomotora de cerca. Fue la primera vez que quiso subir a una.",
        ),
        "gala" to unDia(
            Parrafo.MANANA to "Hoy es la gala de los mineros en Durham, la gran reunión de las minas de todo el condado, y desde el amanecer los trenes bajan cargados de mineros con sus familias, sus bandas de música y sus estandartes de seda pintados, enrollados en los vagones. Es el día del año de los mineros.",
            Parrafo.TRABAJO to "A esta hora lleva un tren hacia Durham con una banda de música dentro que no para de tocar, y un estandarte enorme que no cabe en el vagón y va asomado por la ventanilla. Los mineros cantan. En la estación de Durham no cabe un alfiler. Vuelve vacío, y a la noche, otra vez lleno.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su padre, que iba a la gala cada año, y que una vez lo subió en hombros para que viera pasar los estandartes por las calles de Durham. Piensa en los que bajan cada día debajo de la tierra. Hoy los mineros son reyes. Mañana bajan otra vez.",
        ),
        "lunes_agosto" to unDia(
            Parrafo.MANANA to "Hoy es el lunes de agosto, día de fiesta de los bancos, el más esperado del verano: las fábricas cierran, las tiendas cierran, y todo el que tiene unos peniques se va a la playa en tren. Las estaciones están llenas desde que amanece. La compañía pone todos los trenes que tiene.",
            Parrafo.TRABAJO to "A esta hora lleva su tercer tren de excursión del día hacia la costa, con la máquina sudando como él y el carbón del ténder bajando a ojos vistas. En la estación de la playa hay tantos trenes esperando que hay que hacer cola para dar la vuelta. Vuelve de noche, con la gente dormida.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mientras echa carbón, en toda esa gente que hoy ve el mar, algunos por primera vez en su vida, y en que la llevan él y su máquina. Le parece que hace una cosa buena. Se lo dice a sí mismo en voz alta. Nadie lo oye: el fuego hace demasiado ruido.",
        ),
        "guy_fawkes" to unDia(
            Parrafo.MANANA to "Hoy es la noche de Guy Fawkes, y desde hace semanas los niños de la calle van con un muñeco de trapo en una carretilla, pidiendo un penique para el Guy. Han juntado leña, cajas, muebles viejos y traviesas robadas del depósito para la hoguera del descampado.",
            Parrafo.ANOCHECER to "Al anochecer se enciende la hoguera del descampado y se quema el Guy, que tiene la cara pintada y un sombrero viejo de {nombre}. Los niños gritan, tiran petardos, asan patatas en las brasas. Las caras se ponen rojas. El fuego se ve desde el puente del río.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando la hoguera, que para un fogonero esto es como ir de vacaciones a su propio trabajo. Se ríe. Luego piensa que el fuego de la hoguera es tonto: no mueve nada. El de su máquina mueve cuatrocientas toneladas. Le gusta más el suyo.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, y la compañía pone pocos trenes, pero pone. A {nombre} le toca el turno corto, que termina a mediodía. En el depósito, alguien ha colgado acebo en la puerta de la oficina del capataz, y el capataz hace como que no lo ha visto.",
            Parrafo.GENTE to "Por la tarde, en casa, se come el pudin de Navidad, que se hizo hace semanas y que arde con un chorro de brandy, con una moneda de plata escondida dentro. Se cantan villancicos con los vecinos. Los niños de la calle van de puerta en puerta cantando, por un penique o un trozo de pastel.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en las Navidades de cuando era niño, cuando su padre llegaba de la mina con una naranja para cada uno y la ponía en la mesa como si fuera oro. Piensa que una naranja, para un niño de minero, era oro. Este año compra naranjas para todos los niños de la calle.",
        ),
        "boxing" to unDia(
            Parrafo.MANANA to "Hoy es el día de las cajas, el día después de Navidad, cuando se da el aguinaldo a los que trabajan para uno todo el año: el cartero, el lechero, el muchacho del carbón, el llamador del depósito. {nombre} le da un chelín al llamador, que se lo guarda en el zapato.",
            Parrafo.TRABAJO to "A esta hora lleva un tren de los del día de fiesta, lleno de gente que va a visitar a la familia o a ver el partido de fútbol del pueblo de al lado, que se juega cada año este día. Los del fútbol vuelven cantando o vuelven callados, según el resultado.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hay dos clases de gente: la que da cajas y la que las recibe. Él las da y las recibe. La compañía le dio la suya: un pavo pequeño, como a todos los fogoneros. A los maquinistas les dan uno grande. Algún día, piensa, el pavo grande.",
        ),

        // ---------- Los de la temporada 1 ----------
        "tay" to unDia(
            Parrafo.MANANA to "Hoy en el depósito no se habla de otra cosa que del puente del Tay, en Escocia, que se hundió con un vendaval la noche de los Inocentes con un tren entero encima: la máquina, los coches, el fogonero, el maquinista, todos los pasajeros. No quedó nadie. Los buzos todavía buscan.",
            Parrafo.GENTE to "En el cuarto de los maquinistas, Hebburn lee el periódico en voz alta, despacio, y nadie interrumpe. Dicen que el puente era nuevo, que el viento era fuerte, que el hierro era malo. Nichols, el viejo, dice que a un puente no se le pregunta: se le cruza y se reza.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el fogonero del Tay, que estaría echando carbón como él, de espaldas a la vía, cuando el puente se abrió. Piensa en el instante en que lo supo. Piensa que en la línea hay tres puentes de hierro. Hoy, al cruzarlos, se agarra a la barandilla sin darse cuenta.",
        ),
        "maquina" to unDia(
            Parrafo.MANANA to "Hoy les dieron a Hebburn y a {nombre} una máquina recién salida del taller de la compañía: verde con filetes negros, los bronces relucientes, el número nuevo en la chimenea, y un olor a pintura fresca que todavía no se ha llevado el hollín. Hebburn la miró un rato largo sin decir nada.",
            Parrafo.TRABAJO to "A esta hora la prueba en la cuesta larga, con un tren de carbón ligero, para ver cómo tira. {nombre} le hace el fuego con cuidado, como a un caballo nuevo. La máquina sube sin quejarse. En lo alto, Hebburn le da una palmada a la caldera, como a un animal. Se ríe. Es raro verlo reír.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que una máquina nueva es como una novia: hay que conocerla, aprender sus manías, saber qué le gusta. Esta tira mejor cuando se le echa el carbón a la izquierda. Ya lo sabe. Piensa que le pondrá un nombre secreto. No se lo dirá a Hebburn.",
        ),
        "eleccion" to unDia(
            Parrafo.MANANA to "Hoy hay elecciones en el distrito, y en la plaza del mercado hay tribunas con banderas de colores, oradores que gritan, carteles pegados encima de otros carteles y peleas de borrachos. {nombre} vota a la salida del turno, como casi todos los del depósito: por los liberales, por Gladstone.",
            Parrafo.GENTE to "Esta noche en la taberna se discute a gritos por Gladstone y por Disraeli, y uno de los guardafrenos, que es conservador, tiene que irse antes de que lo echen. Walter se sube a una silla y da un discurso que nadie entiende. Se brinda por todo el mundo, por si acaso.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que votar es una cosa rara: un fogonero y un duque meten el mismo papel en la misma caja. El duque tiene un voto y él también. Le parece justo. Luego piensa que el duque tiene además la mina, la tierra y el periódico. Le parece menos justo.",
        ),
        "viaducto" to unDia(
            Parrafo.MANANA to "Hoy han llegado al valle del río los contratistas del viaducto nuevo: carretas, caballos, cientos de peones, tiendas de lona, una grúa de vapor. Van a levantar un puente de piedra de veinte arcos sobre el valle, para que el tren no tenga que bajar y subir la cuesta.",
            Parrafo.PUEBLO to "En el fondo del valle, donde antes había vacas y un molino, hay ahora un campamento de peones irlandeses y escoceses, con sus mujeres, sus niños y sus tabernas de lona. Los del pueblo los miran con desconfianza. Los peones los miran igual.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la cuesta de la otra orilla, la que se come el carbón, la que lo deja sin espalda cada día. Dentro de unos años no existirá: el tren pasará por arriba, recto, como un pájaro. Piensa que le gustaría ser el primero en cruzarlo. Se lo dice a Hebburn. Hebburn dice que ya veremos.",
        ),
        "ellen" to unDia(
            Parrafo.MANANA to "Hoy, al salir del turno, hay té en la capilla metodista, con bocadillos, pasteles, himnos y un predicador que habla una hora de la bebida. {nombre} va porque su madre insiste. Le sirve el té una muchacha de pelo castaño que se ríe de cómo él sujeta la taza. Se llama Ellen.",
            Parrafo.GENTE to "Ellen trabaja en la mercería de la calle mayor y vive con su tía. Habla rápido, se ríe alto y no le da miedo mirar a la gente a los ojos. Le pregunta a {nombre} si es verdad que los fogoneros fríen el tocino en la pala. Él le dice que sí. Ella dice que qué asco. Se ríen.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, volviendo a casa, que nunca había hablado tanto con una muchacha, ni tan a gusto, ni de tantas tonterías. Piensa que mañana necesitará botones. Muchos botones. No sabe para qué. Ya se le ocurrirá.",
        ),
        "escuela" to unDia(
            Parrafo.MANANA to "Hoy Hebburn leyó en el periódico, en el cuarto de los maquinistas, que hay una ley nueva que obliga a todos los consejos de escuela a mandar a los niños a clase, por lo menos hasta los diez años. Los padres que no los manden pagarán una multa. Se habló mucho de eso.",
            Parrafo.GENTE to "En la taberna, unos dicen que está bien, que los niños tienen que aprender; otros, que en las casas pobres un niño que trabaja es un plato más en la mesa. Un minero dice que su hijo baja a la mina a los diez años, con ley o sin ley. Nadie le contesta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Tom, que va a la escuela a regañadientes, y en él mismo, que dejó la escuela a los once años para limpiar máquinas. Sabe leer y firmar, y le basta. Pero piensa que si hubiera estudiado más, ahora sabría lo que dice el libro de reglas sin que se lo explicaran. Se lo dice a Tom. Tom no le hace caso.",
        ),
        "patronos" to unDia(
            Parrafo.MANANA to "Hoy se supo en el depósito que hay una ley nueva: si un obrero se lastima por culpa de la compañía, la compañía tiene que pagarle. Antes, si el accidente era culpa de un compañero, la compañía no pagaba nada. El sindicato la pidió durante años. Los del depósito brindan con té.",
            Parrafo.GENTE to "El capataz Thwaites dice que la compañía va a buscar la forma de no pagar igual, que para eso tiene abogados. Nichols dice que en sus tiempos al que perdía una pierna le daban una pata de palo y un empleo de guardabarreras, y gracias. Walter dice que los tiempos cambian. Thwaites dice que no tanto.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el fogonero que perdió la pierna en el patio del depósito el año pasado, que ahora vende fósforos en la puerta de la estación. Piensa que con esta ley a lo mejor le habrían pagado algo. Piensa que las leyes llegan siempre tarde para alguien.",
        ),
        "sindicato" to unDia(
            Parrafo.MANANA to "Hoy {nombre} se apuntó en el sindicato de los ferroviarios, en una reunión en la trastienda de la taberna, con la puerta cerrada y una vela. Pagó la primera cuota y le dieron un carné con su nombre. Walter fue su padrino. Hebburn no dijo ni que sí ni que no.",
            Parrafo.GENTE to "En la reunión había fogoneros, guardafrenos, mozos de estación, un par de maquinistas. Se habló de las horas, que son demasiadas: turnos de doce, catorce, dieciséis horas, y luego otra vez. Se habló de los accidentes que pasan por el cansancio. Se habló bajito. La compañía tiene orejas.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ahora es de algo más grande que el depósito, aunque nadie lo sepa. Guarda el carné en la lata del té, debajo del pan, donde no lo ve nadie. Piensa en su padre, que nunca se apuntó a nada. Piensa que su padre a lo mejor seguiría vivo si se hubiera apuntado.",
        ),
        "nieve" to unDia(
            Parrafo.MANANA to "Hoy cayó una nevada enorme sobre el páramo, y el viento la amontonó en la trinchera de la línea, donde la vía va hundida entre dos taludes. El tren de {nombre} entró en la trinchera, la máquina empujó, patinó, empujó, y se quedó clavada en la nieve hasta los topes.",
            Parrafo.TRABAJO to "A esta hora cava nieve con la pala del carbón delante de la máquina, con Hebburn, con el guardafrenos y con unos pastores que bajaron del páramo a ayudar. Cavan, la máquina avanza un metro, se para. Cavan otra vez. Se hace de noche. La nieve sigue cayendo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, en la plataforma, de noche, con el fuego tapado para que dure el carbón, que hay sitios peores para quedarse atascado: por lo menos aquí hay fuego. Los pasajeros del tren se acercan a calentarse a la máquina. Una señora le da un trozo de pastel. Es el mejor pastel de su vida.",
            Parrafo.ANOCHECER to "Anochece en la trinchera, y no se ve más que la nieve que cae delante del farol de la máquina. El guardafrenos sale a pie por la vía, con una linterna, a avisar a la estación más cercana. Dentro de los coches, los pasajeros se tapan con lo que tienen. Nadie va a casa esta noche.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "fogonero", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + BAJA_MANANA + MAQUINISTA_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + BAJA_TRABAJO + MAQUINISTA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + BAJA_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + BAJA_ANOCHECER + MAQUINISTA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
