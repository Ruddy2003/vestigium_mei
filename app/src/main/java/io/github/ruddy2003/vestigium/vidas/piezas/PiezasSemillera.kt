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
 * El cuento de la recolectora de semillas, en una ciudad que el bosque recuperó, años 40 a 44 tras el silencio. Sigue
 * la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su ficha se llama «Una recolectora»: el cuento escribe «la recolectora» a mano y no usa el hueco del nombre.
 * - Su día tiene dos tramos de trabajo (el huerto de la azotea y, después, secar y guardar semillas) y dos comidas: esos
 *   textos valen a cualquiera de sus horas. A mediodía recorre la ciudad.
 * - Un estado: «expedicion» (temporada 2, al jardín botánico viejo; temporada 5, a la costa). Sus textos valen para los
 *   dos viajes y no dicen quién va con ella.
 * - El pueblo lee el arco «puente» mientras se arregla el puente viejo, y si no, el arco «huerto».
 * - No repite lo que ya dice «Su vida»: los siete años después del silencio, las sesenta personas del edificio de
 *   oficinas, los abedules y la hiedra de los semáforos, las latas con etiqueta a lápiz, las pantallas que no ha visto.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG7.kt`):
 * - Lía, desde que empieza a enseñarle (`lia`, temporada 1).
 * - La vieja Amalia sube a la azotea hasta `amalia_cae` (temporada 3); en cama hasta `amalia_muere` (4); después,
 *   recordada.
 * - Teo, el carpintero: amigo hasta `teo` (temporada 2); viven juntos desde entonces; ella espera desde `espera` (4) y su
 *   hija nace en `nace` (4).
 * - El puente viejo, terminado desde `puente_listo` (temporada 4).
 * - Las fiestas que vuelven cada año no nombran a Lía, a Amalia ni a Teo.
 */
internal object PiezasSemillera {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(12, 1, 2)

    private val MANANA = listOf(
        v("A la recolectora la despierta la luz, que entra por la ventana sin cristal de la oficina donde duerme, tapada con una tela. No hay otro despertador. Se levanta con el primer gris, se pone las botas y sube la escalera hasta la azotea, doce pisos, de dos en dos."),
        v("La recolectora se despierta con los pájaros, que en la ciudad son miles y no se callan desde que empieza a clarear: mirlos, gorriones, unos verdes que vinieron de no se sabe dónde. Se queda escuchándolos un momento, envuelta en la manta. Luego, arriba."),
        v("La recolectora se despierta con frío y con hambre, las dos cosas a la vez, como casi todas las mañanas. Bebe agua del cubo de la lluvia, que está helada, y se lava la cara con ella. En el pasillo ya se oyen pasos: el edificio se despierta entero a la misma hora."),
        v("Lo primero que hace la recolectora al levantarse es asomarse a la ventana a mirar el cielo, para saber si hoy se riega o no, si se tapan los semilleros o no, si se sale a la calle o no. Hoy, sale. Se ata el pelo con una cuerda y coge la mochila."),
        v("La recolectora se despierta antes que nadie, en la oficina del piso nueve, y se queda un rato tumbada mirando el techo de placas, que tiene una mancha con forma de pájaro. La mira cada mañana. Le parece que algún día va a echar a volar."),
        v("La recolectora se despierta con la escarcha en la ventana y el aliento hecho humo, y no quiere salir de la manta. Sale. En invierno el huerto duerme, pero las semillas no: hay que darles la vuelta en las latas para que no se pudran. Se pone dos jerséis y sube.", meses = INVIERNO),
    )

    /** De viaje lejos de casa: al jardín botánico viejo (temporada 2) o a la costa (5). Estado «expedicion». */
    private fun ex(texto: String, hacer: Hacer? = null) = v(texto, estado = "expedicion", hacer = hacer)

    private val VIAJE_MANANA = listOf(
        ex("La recolectora se despierta lejos de casa, en el suelo de una tienda vieja, entre estanterías vacías, con la mochila de almohada. Los demás todavía duermen. Fuera se oye un arroyo que antes era una calle. Se levanta sin hacer ruido y sale a ver dónde está."),
        ex("La recolectora se despierta en un tejado donde subieron anoche para que no los encontraran los perros salvajes, con el cielo encima y el rocío en la manta. Hace frío. Ve la ciudad de lejos, entera, verde, por primera vez desde fuera. No parece suya."),
        ex("La recolectora se despierta en una casa que nadie ha pisado en cuarenta años: platos en la mesa, abrigos en el perchero, una muñeca en la escalera, todo bajo un polvo gris. Durmieron en el salón. Nadie toca nada. Por la mañana se van como entraron."),
    )

    private val VIAJE_TRABAJO = listOf(
        ex("A esta hora busca semillas en un sitio que no conoce, de rodillas, con la lupa, entre plantas que no ha visto nunca: unas de hoja gruesa, otras con flores como campanas. Las dibuja en el cuaderno antes de tocarlas. Algunas pican. Algunas matan. No sabe cuáles.", Hacer.TRABAJO),
        ex("A esta hora seca en una piedra al sol las semillas que ha juntado esta mañana, y las mete en bolsitas de tela con un nombre que inventa ella, porque no sabe el de verdad: la de la flor morada, la del árbol que llora, la que huele a limón. Algún día sabrá cómo se llaman.", Hacer.TRABAJO),
        ex("A esta hora camina con los demás por una carretera vieja, rota, con árboles creciendo en medio del asfalto, cargando la mochila que cada día pesa más. Se cruzan con un ciervo que no se asusta. Con un coche oxidado que tiene un árbol dentro. Siguen.", Hacer.CAMINO),
        ex("A esta hora come lo que llevan: pan duro, unas nueces, una lata de antes del silencio que abren con el cuchillo y que huele bien, por suerte. Comen sentados en un muro, en silencio, mirando el camino que queda. Comparten el agua de la cantimplora.", Hacer.COMER),
        ex("A esta hora duerme a medias, con un ojo abierto, porque de noche fuera de la ciudad se oyen cosas: perros, lobos, ramas que se rompen. Hacen turnos para vigilar. Cuando le toca a ella, se sienta con la espalda contra la pared y mira la oscuridad. Nada. Casi nada.", Hacer.DORMIR),
        ex("A esta hora se cura una ampolla del pie con un poco de miel y un trapo limpio, y remienda el codo de la chaqueta, que se rompió en una zarza. Busca agua, la hierve en un fuego pequeño, llena la cantimplora. De viaje, cuidarse es la mitad del trabajo.", Hacer.CUIDAR),
        ex("A esta hora está junto a un fuego pequeño con los que van con ella, contando lo que han visto hoy, apuntando en el mapa el camino de mañana. Alguien canta bajito. Nadie más. Lejos de casa se habla menos. Se escucha más.", Hacer.GENTE),
    )

    private val VIAJE_PUEBLO = listOf(
        ex("Fuera de la ciudad que conoce, todo es igual y distinto: los mismos edificios rotos, la misma hiedra, los mismos árboles en las calles, pero ningún huerto, ningún humo, ninguna voz. Ciudades enteras sin nadie. Ella no sabía que había tantas."),
        ex("El camino es una carretera que nadie arregla desde hace cuarenta años, rota por las raíces, con puentes caídos que hay que vadear, señales que nadie lee, coches parados con las puertas abiertas como si la gente se hubiera ido ayer."),
    )

    private val VIAJE_ANOCHECER = listOf(
        ex("Al caer la tarde buscan un sitio para dormir antes de que se haga de noche: un edificio con escalera, una iglesia, una casa con la puerta entera. Encienden un fuego pequeño, para que no se vea de lejos. La recolectora piensa en su azotea. Le parece muy lejos."),
        ex("Anochece en un sitio que no conoce, y el cielo es el mismo de siempre, lleno de estrellas, como en casa. La recolectora lo mira mucho rato. Le da un poco de paz pensar que el cielo es el mismo. Que en casa, ahora, lo están mirando también."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} Para quien vive de las plantas, el tiempo lo es todo: lo que trae, lo que se lleva, lo que deja crecer."),
        v("La recolectora mira el cielo, como cada mañana, antes de decidir nada. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} La vieja Amalia decía que antes del silencio el tiempo salía en una pantalla. Nadie lo cree."),
        v("{cielo} En {estacion} la ciudad cambia de color: el bosque se la va comiendo a un ritmo distinto cada mes."),
        v("{de_estacion} {cielo} Sin tejados que la paren, la lluvia entra en todas partes, y el bosque lo agradece."),
        v("Quien guarda semillas depende del cielo más que de nadie. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un azul que ya nadie mancha, y se ven las montañas detrás de los edificios.",
            "Hace un día claro, y el sol hace brillar los cristales que todavía quedan en las torres.",
            "No hay nubes, y el aire huele a hierba y a piedra caliente.",
        ),
        Clima.NUBES to listOf(
            "El cielo está gris y bajo, y las torres más altas se meten en las nubes.",
            "Pasan nubes grandes, y sus sombras corren por los tejados verdes.",
            "Está nublado, con una luz suave que no quema las plantas.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el agua cae por las fachadas en cascadas que nadie desvía.",
            "Cae una lluvia fina, y los cubos de la lluvia se llenan despacio.",
            "Llueve fuerte, y las avenidas se convierten en arroyos entre los árboles.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, y los rayos buscan las torres de metal, que ya nadie protege.",
            "Truena sobre la ciudad, y el eco rebota entre los edificios vacíos como en un cañón.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y la ciudad se queda blanca y callada, y solo se ven huellas de animales.",
            "Cae nieve sobre los tejados verdes, y las ramas de los abedules se doblan.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, y las torres aparecen y desaparecen como barcos.",
            "La niebla del río sube por las calles y se queda entre los árboles.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento fuerte que silba en las ventanas rotas como un órgano.",
            "Hace viento, y las hojas sueltas de los libros viejos vuelan por las calles.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en cada grieta de la acera sale algo verde.",
            "En primavera los árboles de las avenidas florecen todos a la vez, y la ciudad huele a miel.",
            "Es el tiempo de sembrar y de esperar.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y la hiedra crece tanto que se oye por la noche.",
            "En verano el sol pega fuerte, y las plantas piden agua dos veces al día.",
            "Es el tiempo de las frutas que caen solas de los árboles de los parques.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo de las semillas, cuando todas las plantas sueltan lo que guardan.",
            "En otoño las hojas tapan las calles y no las barre nadie.",
            "Es el tiempo de llenar las latas y de bajar la leña.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y el huerto duerme debajo de la paja.",
            "En invierno el edificio entero se junta alrededor de las estufas.",
            "Es el tiempo de remendar, de contar las latas y de esperar.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora trabaja en el huerto de la azotea, de rodillas entre los bancales que se hicieron con tierra subida a cubos, quitando hierbas, aclarando plantas, mirando el envés de cada hoja por si hay bichos. Habla con las plantas en voz baja. Las plantas no contestan. Crecen.", hacer = Hacer.TRABAJO),
        v("A esta hora riega el huerto con el agua de los depósitos de la lluvia, un cubo detrás de otro, a mano, despacio, para no desperdiciar ni una gota. El agua sale verde de algas. A las plantas les da igual. Los tomates crecen más que antes del silencio, dice la gente.", hacer = Hacer.TRABAJO),
        v("A esta hora deja una planta de cada clase sin cosechar, para que espigue y dé semilla: una lechuga que se pone alta y amarga, un puerro con una bola de flores, una zanahoria con un paraguas blanco. Son las madres. No se tocan hasta que suelten lo suyo.", hacer = Hacer.TRABAJO),
        v("A esta hora extiende semillas a secar en bandejas de las de antes, de metal, en el cuarto más seco del edificio, y las va moviendo con los dedos para que se sequen parejas. Cada bandeja tiene su papel con el nombre. Si se mezclan, se pierde un año.", hacer = Hacer.TRABAJO),
        v("A esta hora separa semillas de su paja, soplando sobre un cuenco: la paja vuela, las semillas se quedan. Lo hace como se lo enseñó Amalia, que lo aprendió de alguien que lo hacía antes del silencio, que lo aprendió de alguien de antes. Una cadena de soplidos.", hacer = Hacer.TRABAJO),
        v("A esta hora prueba si las semillas del año pasado todavía sirven: pone diez en un trapo húmedo y espera a ver cuántas brotan. Si brotan ocho, bien. Si brotan dos, hay que sembrarlas este año o se pierden. Apunta los números en la pared, con carbón.", hacer = Hacer.TRABAJO),

        v("A esta hora come en la cocina común del tercer piso, de pie, un cuenco de gachas de cebada con un poco de miel, y un trozo de pan oscuro. Come rápido. Hay mucho que hacer. Alrededor, los otros comen igual, hablando poco, con la boca llena.", hacer = Hacer.COMER),
        v("A esta hora come lo que da el huerto: unas papas asadas en las brasas, unas hojas crudas, un huevo si las gallinas han puesto, y un puñado de nueces. Come sentada en el borde de la azotea, con las piernas colgando sobre la calle, doce pisos más abajo.", hacer = Hacer.COMER),
        v("A esta hora come una sopa de lentejas con lo que haya, hecha en la olla grande, y la comparte con los niños del edificio, que siempre tienen más hambre que ella. Les da la mitad. Ellos le dan las gracias con la boca llena. Así se aprende a repartir.", hacer = Hacer.COMER),

        v("A esta hora recorre las calles con la mochila, buscando semillas en los jardines de antes, que se volvieron selvas: rosales gigantes, árboles frutales que nadie poda, hortalizas asilvestradas entre las ortigas. Sabe dónde está cada una. Tiene un mapa en la cabeza.", hacer = Hacer.CAMINO),
        v("A esta hora camina por la avenida grande, por el centro, donde antes iban los coches y ahora hay un prado con flores y algún ciervo. Los edificios se le echan encima, verdes de hiedra. Un zorro la mira desde una ventana del segundo piso. Ella lo saluda.", hacer = Hacer.CAMINO),
        v("A esta hora entra en un parque que ya es un bosque, siguiendo un sendero que hicieron los animales, a buscar bellotas, avellanas, las semillas del arce que vuelan como hélices. Se llena los bolsillos. Una ardilla le grita desde un árbol. Le está robando.", hacer = Hacer.CAMINO),

        v("A esta hora duerme en su rincón de la oficina, en un colchón de paja con dos mantas, con la ventana tapada con una tela. Duerme hondo, como duerme la gente que camina todo el día. No sueña. O sueña con plantas, que es lo mismo.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y los ruidos de la ciudad de noche entran por la ventana: un búho, un zorro que grita como una niña, el viento en los cables viejos, un edificio que cruje. Ella ya no los oye. Los niños de fuera, cuando vienen, no pueden dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, temprano, porque cuando se va la luz no hay nada que hacer: las velas se guardan para cuando hacen falta. El edificio entero duerme a la vez, como un nido. Se oye respirar a sesenta personas. Es como dormir dentro de un animal.", hacer = Hacer.DORMIR),

        v("A esta hora sube agua del río en dos cubos, por la escalera, doce pisos, con los brazos ardiendo. En el piso seis se para a descansar. Es lo peor del día. Lo hace todo el mundo por turnos. Hoy le toca a ella.", hacer = Hacer.CUIDAR),
        v("A esta hora corta leña en el patio con el hacha, de las ramas que caen solas de los árboles de la calle, y la sube al cuarto de las estufas. En invierno es lo que más falta. En verano también se corta: para el invierno.", hacer = Hacer.CUIDAR),
        v("A esta hora remienda ropa junto a la ventana, con hilo de antes y una aguja que tiene desde niña, porque la ropa de antes del silencio se acaba y la que se hace ahora es basta. Un pantalón tiene más remiendos que pantalón. Le pone otro.", hacer = Hacer.CUIDAR),

        v("A esta hora está en la fogata de la azotea con los del edificio, alrededor del fuego, con los niños dormidos en las rodillas de quien sea. Se cuenta lo del día. Alguien canta una canción de antes del silencio cuya letra nadie entiende del todo. La cantan igual.", hacer = Hacer.GENTE),
        v("A esta hora escucha, en la fogata, a alguien que cuenta una historia de las que se repiten: la de la ciudad que se quedó a oscuras, la del primer invierno, la del que se fue y volvió con un caballo. Los niños la saben de memoria. Corrigen al que la cuenta.", hacer = Hacer.GENTE),
        v("A esta hora, en la fogata, enseña a los niños a reconocer semillas por la forma, con los ojos cerrados: esta es de calabaza, esta de girasol, esta de frijol. Los niños se equivocan y se ríen. Los mayores apuestan quién acertará más. Siempre gana la más pequeña.", hacer = Hacer.GENTE),
    )

    private val PUEBLO = listOf(
        v("{entorno} La azotea del edificio es un campo: bancales de tierra entre las chimeneas, un gallinero en una caseta del ascensor, depósitos de agua de lluvia, un emparrado sobre los paneles viejos del tejado. Desde ahí se ve toda la ciudad, verde, hasta el río."),
        v("La ciudad es un bosque con forma de ciudad: las avenidas son claros, los parques son selvas, los edificios son acantilados cubiertos de hiedra donde anidan los halcones. En las calles hay ciervos, zorros, jabalíes. La gente vive en unos pocos edificios, como en islas. {entorno}"),
        v("{entorno} El edificio era de oficinas: tiene veinte pisos, pero solo se usan los doce de abajo y la azotea. Las mesas de antes se hicieron leña, las sillas se quedaron. En cada oficina vive una familia. Los ascensores son armarios. La escalera es la calle."),
        v("En el sótano del edificio, el más fresco y seco, está el almacén de semillas: estanterías llenas de latas, cada una con su nombre, su año, su sitio. Es lo más valioso que tienen. La puerta tiene dos cerraduras. La llave la lleva ella. {entorno}"),
        v("{entorno} El río cruza la ciudad lento y limpio, mucho más limpio que antes, dicen los viejos. Tiene peces, castores, garzas. El puente viejo está roto por la mitad. Al otro lado vive la gente del río, otro campamento, con los que se cambian cosas una vez al año.", hasta = "puente_listo"),
        v("{entorno} El río cruza la ciudad lento y limpio, con peces, castores y garzas. El puente viejo ya está arreglado, con tablas, cuerdas y piedras, y por él se pasa a pie a la orilla de la gente del río. Hay quien cruza cada semana. Antes, una vez al año.", desde = "puente_listo"),
        v("Las calles de la ciudad tienen todavía sus letreros, oxidados, colgando de las paredes con nombres de tiendas que nadie recuerda qué vendían. La recolectora los lee en voz alta cuando pasa: farmacia, banco, peluquería. Son palabras que no sirven para nada. Le gustan. {entorno}"),
    )

    /** El puente mientras se arregla (arco «puente») y, si no, el huerto (arco «huerto»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("puente", 0.01, 0.99, frases = listOf(
            "En el río, el puente viejo se va cerrando con tablas y cuerdas, y cada día se ve más gente trabajando encima.",
            "Desde la azotea se ve el puente a medio arreglar, con un hueco en el medio que cada semana es más pequeño.",
        )),
        Entorno("huerto", 0.0, 0.2, frases = listOf(
            "El huerto de la azotea está casi desnudo, con la tierra oscura y los bancales tapados.",
            "En la azotea, los bancales esperan, con algún brote tímido y mucha tierra.",
        )),
        Entorno("huerto", 0.2, 0.7, frases = listOf(
            "El huerto de la azotea está verde y lleno, y ya no se ve la tierra entre las hojas.",
            "En la azotea, las plantas crecen a ojos vistas, y los tomates suben por las cañas.",
        )),
        Entorno("huerto", 0.7, 1.0, frases = listOf(
            "El huerto de la azotea está en su punto: todo maduro, todo pesado, todo a punto de soltar semilla.",
            "En la azotea las plantas se doblan de tanto fruto, y las abejas no dan abasto.",
        )),
    )

    private val GENTE = listOf(
        v("Los del edificio son unas sesenta personas: familias, viejos, niños, dos o tres que llegaron solos de fuera. Se reparten el trabajo en una pizarra del vestíbulo, con tiza. Se pelean, se reconcilian, se cuidan. No hay jefe. Hay una asamblea que dura mucho."),
        v("Los niños del edificio son once, y la siguen a todas partes como patitos cuando sale a buscar semillas. Ella les enseña los nombres de las plantas. Ellos le enseñan dónde están los nidos. Ninguno ha visto nunca a alguien que no sea del edificio o del río."),
        v("La gente del río vive al otro lado, en una fábrica vieja junto al agua: pescadores, herreros, gente seria que habla con un acento distinto. Cambian sal, pescado seco y cuchillos por semillas, miel y tela. Se respetan. Se miran con un poco de desconfianza. Así está bien."),
        v("El viejo Bruno cuida las abejas del edificio, en unas colmenas hechas con cajones de los archivos de las oficinas. Tiene las manos llenas de picaduras y no se queja nunca. Dice que las abejas son las que de verdad mandan en el huerto. Tiene razón."),
        v("Nadia es la que cura en el edificio: sabe de hierbas, de huesos rotos, de partos y de fiebres, por un libro de medicina que se sabe de memoria y por lo que le enseñó su madre. Le pide a la recolectora semillas de manzanilla, de caléndula, de menta. Siempre le faltan."),
        v("La vieja Amalia tenía doce años cuando llegó el silencio y se acuerda de todo: los coches, las luces, las tiendas llenas, la gente que hablaba con una caja en la mano. Sube cada mañana a la azotea con un bastón y se sienta al sol. Le cuenta cosas que parecen cuentos.", hasta = "amalia_cae"),
        v("La vieja Amalia ya no sube a la azotea: se cayó en la escalera y está en la cama, en su oficina del piso dos, con la ventana abierta para oír a los pájaros. La recolectora le lleva cada día una flor del huerto. Amalia la huele y dice el nombre. Nunca se equivoca.", desde = "amalia_cae", hasta = "amalia_muere"),
        v("La vieja Amalia murió, y con ella lo último que quedaba de antes del silencio en la memoria de alguien. La recolectora guarda su bastón junto a la puerta del almacén de semillas. A veces le pregunta algo en voz alta, como antes. Nadie contesta. Lo sabe.", desde = "amalia_muere"),
        v("Lía es la niña a la que enseña a guardar semillas, una niña de nueve años con las rodillas siempre raspadas y una memoria que asusta: se sabe ya el nombre de todas las latas. La recolectora la ve y se ve a ella misma, de pequeña, con Amalia. Así siguen las cosas.", desde = "lia"),
        v("Teo es el carpintero del edificio, un hombre callado que hace de todo con madera de las oficinas: bancales, escaleras, cucharas, una cuna que nadie ha pedido. Le guarda a la recolectora las tablas más rectas para sus estanterías. Ella le guarda las semillas más raras.", hasta = "teo"),
        v("Teo y la recolectora viven juntos en una oficina del piso nueve, con una ventana grande que da al río. Él ha hecho una cama de verdad, con patas, la única del edificio. Por las noches lee en voz alta los libros de plantas que ella encuentra, despacio, porque lee mal. Ella lo corrige.", desde = "teo", hasta = "espera"),
        v("La recolectora espera un hijo, y Teo ha terminado por fin la cuna que hizo hace años sin que nadie se la pidiera. Ahora la lija todas las noches. La recolectora le dice que ya está bien. Él la lija otra vez. Los del edificio les traen cosas: una manta, un gorrito, un sonajero de semillas.", desde = "espera", hasta = "nace"),
        v("Su hija nació en la azotea, junto a la fogata, y es la primera niña del edificio en años. La recolectora la lleva atada a la espalda cuando trabaja en el huerto, y la niña mira las plantas con los ojos muy abiertos. Teo dice que va a ser carpintera. Ella dice que ya veremos.", desde = "nace"),
    )

    private val OBJETO = listOf(
        v("En un mundo sin tiendas, cada cosa que se tiene es un tesoro o una herramienta. {dato_objeto}"),
        v("{dato_objeto} La recolectora lo aprendió de la vieja Amalia, que lo aprendió de los de antes."),
        v("Hoy la recolectora se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Los niños del edificio no saben que antes no era así. Para ellos, siempre fue así."),
        v("{dato_objeto} Lo que se rompe, se arregla; lo que no se arregla, sirve para otra cosa."),
        v("Las cosas de antes del silencio se acaban, y las de ahora se hacen despacio. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("lata", "la lata", "una lata de semillas", "La lata",
            "Las latas de semillas son las de antes, de galletas, de té, de tabaco, con dibujos que ya casi no se ven; cierran bien y no las abren los ratones; cada una guarda una planta, y todas juntas guardan el huerto de los próximos cien años."),
        ObjetoCuento("lapiz", "el lápiz", "un lápiz", "El lápiz",
            "Los lápices de antes son lo que más se cuida en el edificio después de las semillas: se afilan con cuchillo hasta que no se pueden coger; cuando se acaben, habrá que escribir con carbón, que se borra con la humedad."),
        ObjetoCuento("mochila", "la mochila", "una mochila", "La mochila",
            "La mochila de la recolectora es de las de antes, de tela fuerte con cremalleras que ya no cierran, remendada con trozos de otras mochilas; dentro lleva bolsitas de tela, una lupa, un cuchillo, un cuaderno y siempre un poco de pan."),
        ObjetoCuento("cuchillo", "el cuchillo", "un cuchillo", "El cuchillo",
            "Los cuchillos los hace la gente del río con hojas de muelles de coches viejos, templadas en su fragua, con mango de hueso; cortan como los de antes, o mejor, y se cambian por mucha miel."),
        ObjetoCuento("tarro", "el tarro", "un tarro de cristal", "El tarro",
            "Los tarros de cristal de antes son muy buscados: sirven para guardar miel, semillas pequeñas, conservas; un tarro roto es un pequeño luto en el edificio, porque nadie sabe ya hacer cristal."),
        ObjetoCuento("mapa", "el mapa", "un mapa", "El mapa",
            "El mapa de la ciudad es uno de antes, de papel, de los que daban gratis en algún sitio, lleno de calles con nombres de personas; encima, la recolectora ha marcado a lápiz dónde está cada árbol, cada huerto salvaje, cada peligro."),
        ObjetoCuento("libro", "el libro", "un libro de plantas", "El libro",
            "Los libros de plantas que se salvaron de la biblioteca están en una estantería de la oficina de la recolectora, envueltos en tela contra la humedad; tienen dibujos de flores, raíces y semillas, con nombres en una lengua de antes que ella aprendió a leer."),
        ObjetoCuento("botas", "las botas", "unas botas", "Las botas",
            "Las botas de antes ya casi no se encuentran, y las que se encuentran se cosen y recosen hasta que no queda nada de la primera suela; la gente del río hace ahora suelas de goma de neumáticos viejos, que duran años."),
        ObjetoCuento("cuerda", "la cuerda", "una cuerda", "La cuerda",
            "La cuerda se hace ahora con ortigas, como en los tiempos muy antiguos: se cortan, se secan, se separan las fibras y se tuercen; la recolectora lleva siempre un rollo atado a la mochila, para subir, para bajar, para atar."),
        ObjetoCuento("silbato", "el silbato", "un silbato", "El silbato",
            "Cada uno del edificio lleva un silbato colgado del cuello, de los de antes, de árbitro o de socorrista; tres silbidos cortos quieren decir peligro, uno largo quiere decir aquí estoy; en una ciudad tan grande y tan callada, se oye muy lejos."),
        ObjetoCuento("manta", "la manta", "una manta", "La manta",
            "Las mantas de ahora se tejen con lana de las ovejas que se volvieron salvajes en los parques y que la gente del río caza a veces; pican, pesan y abrigan como nada de antes; la de la recolectora tiene rayas de los colores de las plantas que la tiñeron."),
        ObjetoCuento("semilla", "la semilla", "una semilla", "La semilla",
            "Una semilla es una planta dormida con su comida para el viaje; algunas aguantan un año, otras diez, otras cien; la recolectora sabe cuánto duerme cada una, y por eso siembra cada año un poco de las más viejas, para que no se mueran en la lata."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} La recolectora piensa en esas cosas un momento y luego vuelve a lo suyo, que es lo que hay que hacer hoy: regar, guardar, recorrer. A sus {edad} años sabe que el mundo grande se acabó antes de que ella naciera. El pequeño, el de la azotea, es suyo."),
        v("Piensa en los de antes del silencio, que tenían todo y no sabían guardar semillas, porque las compraban en sobres. Piensa que ella no tiene nada y sabe guardar semillas. No sabe quién salió ganando. Piensa que ellos también se lo preguntarían."),
        v("{mundo} La recolectora piensa que cada lata del sótano es una carta para alguien que todavía no ha nacido: aquí tienes tomates, aquí tienes trigo, aquí tienes flores. Piensa que no sabrá nunca quién la abre. Le basta con saber que alguien la abrirá."),
        v("{mundo} A veces, de rodillas en el huerto, la recolectora se queda quieta escuchando la ciudad: sin coches, sin máquinas, sin voces de lejos. Solo pájaros, viento, agua. Piensa que antes debía de ser muy ruidosa. Piensa que le habría dado miedo."),
        v("Piensa en lo que pasará cuando se acaben las cosas de antes: los lápices, las agujas, los tarros, las botas. Piensa que habrá que hacerlo todo de nuevo, con las manos. Piensa que ya lo están haciendo. Que la gente del río ya hace cuchillos. Que se puede."),
        v("{mundo} A sus {edad} años la recolectora sabe que no verá cómo termina esto, si es que termina: si la ciudad vuelve a llenarse, si el bosque se la come entera, si llega alguien de lejos. Lo que sabe es que en el sótano hay semillas para cien años. Eso, sí."),
    )

    private val MUNDOS = mapOf(
        "hiedra" to listOf(
            "Se habla de lo rápido que crece el bosque este año: hay calles que en primavera eran calles y ya son bosque.",
            "Se dice que la gente del río ha visto humo lejos, al sur, donde no vive nadie.",
            "Se habla de los lobos, que antes no venían y ahora se oyen algunas noches en las afueras.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde la luz se pone dorada sobre los tejados verdes, y los pájaros vuelven a los edificios en bandadas enormes, gritando. La recolectora sube a la azotea a ver la puesta del sol. Desde ahí, la ciudad parece un bosque con ventanas."),
        v("Anochece, y en las ventanas del edificio se encienden unas pocas velas, y en la azotea, la fogata. Es la única luz en kilómetros. Desde lejos, dicen los del río, el edificio parece un faro. La recolectora se sienta cerca del fuego, con las manos sucias de tierra."),
        v("Al caer la tarde la recolectora baja al sótano a dejar las semillas del día en su lata, con la etiqueta, y cierra la puerta con las dos llaves. Sube despacio, piso por piso, oyendo las voces de las familias detrás de las puertas. Así se acaba cada día."),
        v("De noche, desde la azotea, se ven todas las estrellas, muchas más de las que veían los de antes, que tenían luces en las calles. La vieja Amalia decía que de niña solo veía la luna. La recolectora no se lo cree del todo. Mira el cielo lleno. Es suyo."),
        v("Anochece, y en la calle de abajo salen los animales de la noche: zorros, tejones, un jabalí con sus crías que hoza en lo que era un jardín. La recolectora los mira desde la ventana. Ellos no la miran a ella. La ciudad, de noche, es de ellos."),
        v("La última luz del día se queda en lo alto de la torre de cristal más alta, que tiene árboles creciendo en los pisos de arriba como un jardín colgado. Brilla un momento, naranja. Luego, nada. La recolectora se mete en la manta. Mañana, temprano.", hasta = "torre"),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es el primer día del año, y en el vestíbulo del edificio se juntan todos para contarse en voz alta, uno por uno, por su nombre: los que quedan, los que han nacido, y en silencio los que se han ido. Es la costumbre. Se cuenta como se cuentan las latas.",
            Parrafo.GENTE to "Esta tarde hay comida grande en la cocina común: lo mejor que se ha guardado, miel, un queso de la gente del río, una gallina vieja en la olla. Se brinda con una bebida de manzana fermentada que pica en la nariz. Los niños se duermen en las escaleras.",
            Parrafo.PENSAMIENTO to "La recolectora piensa en el año que empieza y en lo que le pide: que no haya helada tarde, que no se pudran las latas, que no falte nadie al año que viene. Piensa que pide lo mismo que pedirían sus abuelos. Que algunas cosas no cambian ni con el fin del mundo.",
        ),
        "deshielo" to unDia(
            Parrafo.MANANA to "Hoy se ha roto el hielo del río, con un ruido de cristales que se oyó desde la azotea, y los témpanos bajan por el agua chocando entre sí. Es la señal: empieza el año de las semillas. La recolectora baja al sótano y abre las latas de lo que se siembra primero.",
            Parrafo.TRABAJO to "A esta hora prepara los semilleros en el cuarto más cálido del edificio, junto a la estufa: cajas de madera con tierra fina, en las que pone semillas de tomate, de pimiento, de lechuga, en filas, con un palito. Las tapa con un cristal. Ahora, a esperar.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que el río sabe cuándo empieza el año mejor que nadie, mejor que el calendario de la pared que dibujó alguien de antes. Piensa que el año de verdad empieza cuando se rompe el hielo. Piensa que habría que cambiar la fiesta. No lo propone.",
        ),
        "reparto" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta del reparto, el día en que se abren las latas del sótano y se reparten las semillas del año entre todas las azoteas del edificio y las de la gente del río. La recolectora sube las latas en una caja. Todo el mundo espera en el vestíbulo, con sus bolsitas.",
            Parrafo.TRABAJO to "A esta hora reparte semillas con una cuchara pequeña, a cada uno lo suyo, apuntando en una libreta quién se lleva qué. Da explicaciones: esta a pleno sol, esta a la sombra, esta en remojo una noche. Algunos no escuchan. Ya aprenderán, cuando no salga.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que hoy da lo que ha guardado todo el año, y que dentro de un año se lo devolverán multiplicado, si todo va bien. Piensa que es como un banco de los de antes, de esos de los que hablaba la gente, pero que funciona.",
        ),
        "silencio" to unDia(
            Parrafo.MANANA to "Hoy es el día del silencio, el aniversario del día en que se apagó todo: las luces, las máquinas, las voces de lejos. Nadie que esté vivo sabe del todo qué pasó. Los viejos cuentan cosas distintas. Hoy no se trabaja hasta el mediodía.",
            Parrafo.GENTE to "A mediodía, en la azotea, todo el edificio se queda callado un minuto, de pie, mirando la ciudad, por los de antes. No se oye nada: ni un coche, ni una máquina. Lo mismo que se oye todos los días. Pero hoy se escucha. Luego alguien tose, y se acaba.",
            Parrafo.PENSAMIENTO to "La recolectora piensa en los de antes, que eran miles de millones, y en este edificio, que son sesenta. Piensa que no los echa de menos, porque no los conoció. Piensa que echa de menos lo que sabían. Por eso guarda semillas: para que algo de lo que sabían no se pierda.",
        ),
        "trueque" to TextoEspecial(listOf(
            mapOf(
                Parrafo.MANANA to "Hoy empieza la feria del trueque con la gente del río, dos días, en la orilla junto al puente viejo, con mesas hechas de puertas. Del edificio se llevan semillas, miel, tela, velas. Del río traen sal, pescado seco, cuchillos, cuerda. Todo el mundo se pone lo mejor que tiene.",
                Parrafo.TRABAJO to "A esta hora está en su mesa de la feria, con las latas abiertas y las bolsitas llenas, cambiando semillas por sal y por un cuchillo nuevo. Regatea poco: sabe lo que valen. Los del río preguntan cómo se siembra cada una. Ella les explica. Eso no se cobra.",
                Parrafo.PENSAMIENTO to "La recolectora piensa que la feria es lo más parecido a una ciudad que ha visto: cien personas juntas, hablando, discutiendo, riéndose. Piensa que así debían de ser las ciudades de antes, pero todos los días. Piensa que se cansaría. Hoy no se cansa.",
            ),
            mapOf(
                Parrafo.MANANA to "Hoy es el segundo día de la feria del trueque, el de las fiestas: se baila en la orilla, se come junto al fuego, se cantan las canciones de los dos lados del río, que son casi las mismas con palabras distintas. Los jóvenes de un lado miran a los del otro.",
                Parrafo.GENTE to "Esta noche se baila junto al río, con un tambor de los del río y una flauta del edificio, y la recolectora baila hasta que le duelen los pies. Al final, los del río vuelven a su orilla en barcas, con antorchas. Se despiden con silbidos largos. Hasta el año que viene.",
                Parrafo.PENSAMIENTO to "La recolectora piensa, viendo las antorchas alejarse por el río, que la gente se necesita, aunque se mire con desconfianza. Piensa que el silencio separó a todo el mundo. Piensa que cada feria los junta un poco. Muy poco. Algo.",
            ),
        )),
        "cerezas" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empiezan a recogerse las cerezas de los árboles de la avenida grande, que alguien plantó antes del silencio para que fueran bonitas y ahora dan de comer. Tres días. Todo el edificio sale con cestas y escaleras. Los pájaros ya llevan una semana comiéndoselas.",
                Parrafo.TRABAJO to "A esta hora está subida a un cerezo de la avenida, con una cesta colgada del cuello, cogiendo cerezas negras y dulces y comiéndose una de cada cinco. Abajo, los niños recogen las que caen. Tiene los dedos rojos. La boca, también.",
                Parrafo.PENSAMIENTO to "La recolectora guarda los huesos de las mejores cerezas en un bolsillo, para secarlos y plantarlos. Piensa en quien plantó estos árboles, hace sesenta años, para adornar una calle. Piensa que no sabía que estaba plantando comida para después del fin del mundo.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue recogiendo cerezas en la avenida, subida a una escalera que alguien sostiene abajo, espantando a los mirlos con un trapo. La cesta pesa. Los niños llevan las llenas al edificio en una carretilla.",
                "A esta hora deshuesa cerezas en la cocina común con las mujeres y los niños, con los dedos y con un palito, para secarlas al sol y para hacer mermelada con la poca miel que queda. Los huesos buenos van a una lata.",
            )),
        ),
        "cosecha" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la cosecha grande de las azoteas, cinco días en que todo el edificio deja lo demás y sube a recoger: las calabazas, las papas, los frijoles secos, el trigo de la azotea del piso doce, las manzanas del patio. Hay que darse prisa: el otoño llega de golpe.",
                Parrafo.TRABAJO to "A esta hora arranca papas en un bancal de la azotea, con las manos, porque las palas se rompen, y las va dejando al sol para que se sequen. Debajo de cada mata hay seis, ocho, diez. Las cuenta. Las mejores, para semilla.",
                Parrafo.PENSAMIENTO to "La recolectora piensa que en cinco días se decide cómo se pasará el invierno. Piensa en los inviernos malos que le contaron: el tercero, el noveno, cuando se comieron las semillas y al año siguiente no hubo nada. Piensa que eso no pasará mientras ella tenga la llave.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en la cosecha, desgranando frijoles secos sobre una sábana en la azotea, golpeándolos con un palo. Las vainas crujen. Los frijoles saltan. Los niños los persiguen por el suelo.",
                "A esta hora sube calabazas del patio a la azotea en una cesta atada a una cuerda, con una polea vieja. Arriba, alguien las recibe. Cada calabaza es una semana de sopas en invierno.",
                "A esta hora elige con cuidado las mejores plantas de cada clase para semilla, las más grandes, las más sanas, las que aguantaron la sequía, y las ata con un trapo rojo. Esas no se comen. Son el año que viene.",
            )),
        ),
        "latas" to unDia(
            Parrafo.MANANA to "Hoy se sellan las latas de semillas del año, en el sótano, con cera de las abejas del edificio derretida en un cazo. Es el último paso. Lo que se cierra hoy no se abre hasta la primavera, o hasta dentro de diez años, si es una lata de reserva.",
            Parrafo.TRABAJO to "A esta hora sella latas en el sótano, una por una, a la luz de una vela: mete las semillas secas, pone la etiqueta, cierra la tapa, pasa la cera caliente por la junta con un pincel. Huele a miel y a polvo. Cuenta las latas. Hay más que el año pasado.",
            Parrafo.PENSAMIENTO to "La recolectora piensa en las latas de reserva, las del fondo, que no se abren nunca salvo que todo salga mal: una lata de cada planta, por si un año se pierde todo. Piensa que espera no tener que abrirlas nunca. Piensa que por eso existen.",
        ),
        "tapar" to unDia(
            Parrafo.MANANA to "Hoy se tapan los huertos de las azoteas para el invierno, con paja, hojas secas y tablas viejas, para que la helada no queme la tierra y los bichos buenos tengan dónde dormir. El huerto se queda como una cama deshecha. Hasta la primavera.",
            Parrafo.TRABAJO to "A esta hora extiende paja sobre los bancales de la azotea, a brazadas, con el viento de noviembre que se la quiere llevar. La sujeta con tablas y piedras. Los ajos y las habas se quedan sembrados debajo: dormirán y saldrán en marzo.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que tapar el huerto es como acostar a un niño: se le pone la manta, se le dice que hasta mañana, se apaga la luz. Piensa que dentro de cuatro meses lo despertará. Piensa que lo echará de menos.",
        ),
        "noche_larga" to unDia(
            Parrafo.MANANA to "Hoy es la noche más larga del año, y en el edificio se prepara la fogata grande en la azotea más alta: leña que se ha guardado todo el otoño, un montón como una casa. Los niños hacen coronas de acebo y de hiedra. Por la noche, nadie duerme hasta que se apague.",
            Parrafo.ANOCHECER to "Esta noche arde la fogata grande en la azotea, y se ve desde todas partes, desde la orilla del río, desde las colinas. Alrededor se canta, se baila, se asan castañas. A medianoche se tira al fuego una rama por cada cosa mala del año. Arden muchas ramas.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que desde mañana los días crecen, aunque todavía no se note, y que las semillas del sótano lo saben. Piensa que la fogata es para ellas también. Tira su rama al fuego. No dice qué cosa mala era. Nadie lo dice.",
        ),

        // ---------- Los de la temporada 1 ----------
        "lobos" to unDia(
            Parrafo.MANANA to "Esta noche se oyeron lobos en la avenida grande, aullando entre los edificios, por primera vez desde que la recolectora nació. Los viejos dicen que no venían desde los primeros años. Esta mañana hay huellas en la nieve del patio, grandes como una mano.",
            Parrafo.GENTE to "Esta tarde, en el vestíbulo, se reúne todo el edificio para hablar de los lobos: que si cerrar el patio, que si cuidar las gallinas, que si no salir solo. Se decide salir de dos en dos y llevar el silbato. Los niños tienen miedo. Algunos, ganas de verlos.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que los lobos vuelven porque hay ciervos, y los ciervos porque hay bosque, y el bosque porque no hay gente. Piensa que la ciudad ya es más de ellos que de los de su edificio. Piensa que tendrán que aprender a vivir juntos. Como con todo.",
        ),
        "torre" to unDia(
            Parrafo.MANANA to "Hoy, a media mañana, se ha caído la torre de cristal de la plaza, la que llevaba años inclinada: primero un crujido largo, luego un ruido que hizo temblar todo el edificio, y luego una nube de polvo que tapó el sol. Todo el mundo subió a la azotea a mirar.",
            Parrafo.GENTE to "Esta tarde, cuando se asienta el polvo, van a ver lo que queda: una montaña de cristal y hierro donde estaba la torre, y los árboles que crecían en sus pisos de arriba tumbados encima. Bruno dice que se han perdido tres colmenas salvajes. La recolectora busca semillas entre las ruinas.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que los edificios de antes se van cayendo uno a uno, como dientes de viejo, y que un día se caerá el suyo. Piensa que tendrán que irse antes, a casas más pequeñas, de piedra. Piensa que el bosque no tiene prisa. Ellos, un poco.",
        ),
        "lia" to unDia(
            Parrafo.MANANA to "Hoy la madre de Lía, una niña de nueve años del piso cinco, le ha pedido a la recolectora que le enseñe a guardar semillas, porque la niña no habla de otra cosa. Lía la espera en la escalera con una lata vacía y una cara de que ya lo sabe todo.",
            Parrafo.TRABAJO to "A esta hora le enseña a Lía a separar semillas de una lechuga espigada: sacudir la flor seca en una bolsa, soplar la paja, mirar que no queden bichos. Lía lo hace mal la primera vez y bien la segunda. A la tercera, mejor que ella.",
            Parrafo.PENSAMIENTO to "La recolectora piensa en Amalia, que le enseñó a ella, y en que algún día Lía enseñará a otro niño. Piensa que las semillas pasan de mano en mano, pero el saber guardarlas también. Piensa que ahora tiene una heredera. Le da un poco de vértigo.",
        ),
        "rayo" to unDia(
            Parrafo.MANANA to "Esta noche ha caído un rayo en el edificio de enfrente, el de los cristales azules, y ha empezado a arder por los pisos de arriba, donde había nidos y hojas secas. Ha ardido toda la noche, con una luz que se veía desde la cama. Nadie vivía ahí. Por suerte.",
            Parrafo.GENTE to "Esta mañana todo el edificio ha estado en la azotea vigilando las chispas, con cubos de agua, por si saltaban al huerto. Saltaron dos. Las apagaron. Teo se quemó una mano. Nadia se la curó con miel. El edificio de enfrente sigue humeando, negro.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que el fuego es lo único que asusta de verdad en una ciudad de bosque. Piensa en el almacén de semillas, en el sótano, que no arde. Piensa que por eso está en el sótano. Lo pensó Amalia hace treinta años. Tenía razón.",
        ),
        "libro" to unDia(
            Parrafo.MANANA to "Hoy, en la biblioteca vieja, en una sala del fondo que se había hundido y que nadie había vuelto a mirar, la recolectora ha encontrado un libro de plantas con dibujos de colores, enorme, que pesa como un niño. Está casi seco. Casi entero.",
            Parrafo.TRABAJO to "A esta hora pasa las hojas del libro en la mesa de su oficina, despacio, con las manos limpias: cada planta dibujada con su flor, su hoja, su raíz, su semilla, y un nombre largo en una lengua que no habla nadie. Reconoce muchas. Otras, no las ha visto nunca.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que en este libro hay plantas que ya no existen, o que existen lejos, o que esperan en algún jardín abandonado. Piensa que algún día irá a buscarlas. Piensa que el libro es como un mapa de tesoros. Lo envuelve en tela. Lo guarda.",
        ),
        "ciervos" to unDia(
            Parrafo.MANANA to "Hoy, de mañana, ha pasado una manada de ciervos por la calle del mercado, por debajo de la azotea: veinte, treinta, con un macho de cuernos enormes delante, caminando despacio sobre el asfalto roto como si fuera suyo. Lo es.",
            Parrafo.GENTE to "Esta tarde los cazadores del edificio salen detrás de los ciervos con arcos, y vuelven al anochecer con uno pequeño. Habrá carne para una semana. Los niños miran el ciervo muerto en el patio con los ojos muy abiertos. Uno llora. Los mayores no le dicen nada.",
            Parrafo.PENSAMIENTO to "La recolectora piensa que los ciervos se comen los brotes de los jardines salvajes, y que si son muchos, habrá menos semillas. Piensa que todo está unido a todo. Que el lobo cuida las semillas comiéndose al ciervo. Piensa que es una cadena rara. Funciona.",
        ),
        "amalia_cuenta" to unDia(
            Parrafo.MANANA to "Hoy hace frío y llueve, y la vieja Amalia no ha subido a la azotea, así que la recolectora ha bajado a verla a su oficina. La ha encontrado junto a la estufa, con ganas de hablar. Amalia le ha dicho que se siente. Que hoy le va a contar cómo era.",
            Parrafo.GENTE to "Esta tarde Amalia le cuenta cómo era la ciudad antes del silencio: las luces de noche, que no dejaban ver las estrellas; los coches, que hacían ruido todo el tiempo; las tiendas con fruta de otros países; la gente que no se miraba. La recolectora escucha sin decir nada.",
            Parrafo.PENSAMIENTO to "La recolectora piensa en la ciudad que le cuenta Amalia y no se la imagina. Piensa que es como un cuento de gigantes. Piensa que Amalia es la última que lo vio. Que cuando ella se vaya, la ciudad de antes se irá con ella. Le pide que se lo cuente otra vez.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "semillera", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + VIAJE_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + VIAJE_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + VIAJE_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + VIAJE_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
