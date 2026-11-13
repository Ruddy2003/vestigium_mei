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
 * El cuento de Zaynab, tejedora de seda en Córdoba, del 970 al 974. Sigue la gramática y el tono de `PiezasDemeas.kt`,
 * que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Teje en el taller del maestro Abu Bakr, en un telar de lazos que hace dibujos y letras en la seda; por la tarde va
 *   al baño y al patio con las mujeres, y al anochecer cuida de la casa.
 * - Cada año, el Ramadán (estado «ramadan») le cambia el día entero: come antes del alba, trabaja en ayunas, rompe el
 *   ayuno al ponerse el sol y la ciudad vive de noche. Cada hora tiene sus variantes, y también la mañana, el pueblo, el
 *   tiempo y el anochecer.
 * - El pueblo lee el arco «rio» (el Guadalquivir, alto en invierno y bajo en verano). La tela que teje avanza en los
 *   pensamientos con fecha: la del califa (temporadas 1 y 2) y la de la boda (3 a 5).
 * - No repite lo que ya dice «Su vida»: la ciudad más grande de Europa, los faroles, los baños de cada barrio, el
 *   azúcar que llega de Oriente, la biblioteca del califa y el laúd que trajo un músico de Bagdad.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG4.kt`):
 * - Alí va a la escuela desde `ali_escuela` (temporada 1) y entra de aprendiz con su padre en `ali_aprendiz` (5).
 * - Juliana se casa en `boda_juliana` (temporada 1).
 * - Zaynab espera desde `zaynab_espera` (temporada 2); Maryam nace en `nace_maryam` (3).
 * - Rabia, la aprendiza, llega en `aprendiza` (temporada 4).
 * - Las fiestas que vuelven cada año no nombran a Maryam ni a Rabia, ni dicen si Alí va a la escuela.
 */
internal object PiezasZaynab {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A {nombre} la despierta el almuédano de la mezquita del barrio, que canta la llamada del alba desde el alminar, y luego los otros, más lejos, uno detrás de otro, como si se contestaran. Se levanta, se lava la cara, las manos y los pies en la fuente del patio, y reza."),
        v("{nombre} se despierta antes que nadie en la casa y sale al patio, donde el agua de la fuente corre igual de día que de noche. Huele a jazmín y a pan: la vecina ya enciende el horno. Arriba, el cielo se pone blanco por el lado de la sierra."),
        v("Lo primero del día es el pan: {nombre} amasa la masa que dejó fermentando por la noche, hace las tortas y se las da a Alí, que las lleva al horno del barrio, con la marca de la casa hecha con un dedo, para que no se confundan con las de los vecinos."),
        v("{nombre} se viste para salir: la túnica de lino, el manto encima, que le tapa el pelo y la mitad de la cara, y las babuchas. Se pone en las muñecas un poco del agua de rosas que Yusuf trae de la tienda. Luego sale a la calle, que todavía está en sombra."),
        v("Por la mañana temprano {nombre} camina por las callejas del barrio hacia el taller, que están frescas y estrechas y huelen a agua. Pasa por delante de la tienda de Yusuf, que ya abre las contraventanas. Él la saluda con la mano. Ella hace como que no lo ve, y sonríe."),
        v("{nombre} se despierta con el ruido de la noria del vecino, que saca agua del pozo con un burro dando vueltas, y que chirría siempre en el mismo sitio. Es el primer ruido de cada mañana desde que vive en esta casa. Si un día no lo oyera, pensaría que se murió el burro."),
    )

    /** El Ramadán de cada año (estado «ramadan»). */
    private fun ra(texto: String, hacer: Hacer? = null) = v(texto, estado = "ramadan", hacer = hacer)

    private val RAMADAN_MANANA = listOf(
        ra("A {nombre} la despierta, de noche todavía, el tambor del que pasa por las calles del barrio llamando a comer antes del alba. Se levanta medio dormida y pone en la mesa baja lo que guardó: pan, queso, dátiles, agua. Tienen que comer antes de que se vea un hilo blanco de un hilo negro."),
        ra("{nombre} se despierta en plena noche para la comida del alba, la única hasta que se ponga el sol. Alí se come los dátiles con los ojos cerrados. Yusuf bebe agua y más agua. Luego suena la llamada del alba, y ya no se come ni se bebe nada más hasta la noche."),
        ra("En Ramadán la casa se despierta de noche y se vuelve a dormir un rato con el alba. {nombre} no se vuelve a dormir: sale al patio, que está fresco, y mira cómo se apagan las estrellas sobre los tejados. Tiene el día entero por delante y el estómago vacío."),
    )

    private val RAMADAN_TRABAJO = listOf(
        ra("A esta hora teje en el taller, en ayunas, con la boca seca. En Ramadán el maestro deja que el trabajo vaya más despacio, porque las manos se cansan antes y la cabeza se va. {nombre} cuenta los hilos dos veces. Una equivocación en la seda no se deshace.", Hacer.TRABAJO),
        ra("A esta hora teje despacio, con la puerta del taller abierta para que entre el aire, porque en Ramadán el calor y la sed se juntan. Las otras tejedoras no cantan, como hacen otros meses. Se oye solo el golpe del peine del telar, una y otra vez, como un corazón.", Hacer.TRABAJO),
        ra("A esta hora devana seda en el patio del taller, a la sombra, que es un trabajo que se hace sentada y sin pensar. Las mujeres hablan bajito de lo que van a cocinar esta noche. Todas hablan de comida en Ramadán. {nombre} también.", Hacer.TRABAJO),
        ra("A esta hora no come, que es Ramadán. Alí, que todavía es pequeño para ayunar el día entero, sí: medio pan y unos dátiles que le da la abuela a escondidas, y {nombre} hace como que no lo ve. Ella hacía lo mismo, a su edad.", Hacer.COMER),
        ra("A esta hora, que otros meses es de comer, no come: desde la llamada del alba no entra nada en la boca hasta que se ponga el sol. {nombre} se enjuaga la boca con agua sin tragarla, como se permite, y vuelve a lo suyo. El estómago protesta. Ella le dice que espere.", Hacer.COMER),
        ra("A esta hora no come; en vez de comer, se sienta un rato con el Corán que le dejó su padre, y repasa con el dedo las palabras que se sabe de memoria, porque en Ramadán se intenta leerlo entero. Ella no sabe leer del todo. Va por la mitad, a su manera.", Hacer.COMER),
        ra("A esta hora reza la oración del alba con Yusuf en el patio, sobre una esterilla, de cara al sureste. En Ramadán reza más despacio y con más ganas, como si cada palabra pesara más. La fuente suena. Arriba, la última estrella.", Hacer.REZAR),
        ra("A esta hora reza y lee en voz baja un trozo del Corán que se sabe de memoria, porque en Ramadán se intenta repasarlo entero. {nombre} se sabe muchas suras, no todas. Las que no sabe, las escucha de Yusuf, que las sabe mejor.", Hacer.REZAR),
        ra("A esta hora está en el baño con las mujeres del barrio, pero en Ramadán no se queda mucho: el agua caliente da más sed, y las mujeres hablan poco. Se lava, se peina y vuelve a casa a preparar la cena, que es lo que importa hoy.", Hacer.GENTE),
        ra("A esta hora está en el patio con las vecinas, que se pasan platos de una casa a otra para la cena del ayuno: tú me das buñuelos, yo te doy almojábanas. Juliana, la vecina cristiana, que no ayuna, les trae agua fresca del pozo de su casa, que es la más fría del barrio.", Hacer.GENTE),
        ra("A esta hora cocina para romper el ayuno, con hambre y con sed, que es cuando mejor se cocina: sopa de sémola, pan, dátiles, cordero con garbanzos y buñuelos con miel. La casa huele a especias. Alí pregunta cada poco si ya se puso el sol. Todavía no.", Hacer.CUIDAR),
        ra("A esta hora rompe el ayuno: cuando el almuédano canta la llamada de la puesta del sol, {nombre} se come un dátil y bebe agua, como hacía el Profeta, y luego reza, y luego se sienta a comer con la familia en el patio. El primer trago de agua es lo mejor del día.", Hacer.CUIDAR),
        ra("A esta hora duerme poco, porque en Ramadán la noche es corta: se cena tarde, se va a la mezquita, se habla en el patio, y antes del alba hay que levantarse otra vez a comer. {nombre} duerme a trozos, como los niños.", Hacer.DORMIR),
        ra("A esta hora duerme en la azotea, porque hace calor, sobre una esterilla, con Yusuf y Alí al lado. Abajo, en las calles, todavía hay gente, lámparas y voces: en Ramadán la ciudad vive de noche. Sueña con agua. Siempre sueña con agua en Ramadán.", Hacer.DORMIR),
    )

    private val RAMADAN_PUEBLO = listOf(
        ra("En Ramadán la ciudad cambia de horario: de día está lenta y callada, con las tiendas medio cerradas y la gente con mal humor; de noche se llena de luces, de gente, de vendedores de buñuelos y de niños con farolillos. La mezquita grande se ilumina entera."),
        ra("En las calles del barrio, al caer la tarde, todo el mundo corre a su casa con algo en la mano: pan, una jarra de leche, un plato tapado. Cuando suena la llamada de la puesta del sol, la ciudad entera se calla de golpe y se pone a comer. Hasta los perros parecen saberlo."),
        ra("De noche, en Ramadán, la mezquita grande está llena hasta las puertas para las oraciones largas, y las lámparas de aceite de las arquerías se encienden todas, cientos, colgadas de cadenas. Dicen que desde la sierra se ve la mezquita brillar como una brasa."),
    )

    private val RAMADAN_ANOCHECER = listOf(
        ra("Al caer la tarde, en Ramadán, {nombre} pone la mesa en el patio y espera, con todos, mirando el cielo. Cuando suena la llamada desde el alminar, Alí grita, Yusuf sonríe, la suegra reza. Un dátil, agua. Después, todo lo demás. Es la mejor hora del año, cada día durante un mes."),
        ra("Anochece, y después de cenar {nombre} sale con Yusuf a las calles, que están llenas de gente y de luz. Compran buñuelos con miel en un puesto, saludan a los vecinos, oyen a un recitador en una esquina. Vuelven tarde. Mañana, otra vez el ayuno.", ),
        ra("De noche, en Ramadán, las vecinas se juntan en un patio a hablar y a comer pasteles hasta muy tarde, mientras los hombres están en la mezquita. Se cuentan historias, se ríen, se pasan a los niños dormidos de un regazo a otro. {nombre} vuelve a casa con los pies cansados y contenta."),
    )

    private val RAMADAN_TIEMPO = listOf(
        ra("{cielo} {de_estacion} En Ramadán el cielo se mira para saber cuánto falta para que se ponga el sol."),
        ra("En Ramadán los días de calor son más largos que ningún otro día. {cielo} {de_estacion}"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En Córdoba el tiempo se mira desde la azotea, por encima de los tejados."),
        v("{nombre} mira el cielo desde el patio, recortado entre los muros de la casa. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Fátima, la suegra, dice que el tiempo de antes era mejor. Fátima dice que todo lo de antes era mejor."),
        v("{cielo} En {estacion} la seda se porta distinto en el telar, y {nombre} lo nota en los dedos: más seca, más húmeda, más nerviosa."),
        v("Desde la azotea se ve la ciudad entera, blanca, con sus alminares, y al fondo la sierra verde y, a un lado, el palacio del califa. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Isaac, el tintorero, dice que el tiempo se ve en los tintes: con el aire húmedo, el carmesí sale más oscuro."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y azul, y la luz rebota en las paredes blancas hasta hacer daño.",
            "Hace un día claro, de los que dejan ver la sierra como si estuviera a un paso.",
            "No hay una nube, y el sol cae a plomo sobre las azoteas.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, y el patio está fresco y gris.",
            "Hay nubes sobre la sierra, y la ciudad espera la lluvia.",
            "Está nublado, y la luz del taller es floja para los hilos finos.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el agua cae de los tejados al patio en chorros que hacen saltar la fuente.",
            "Cae una lluvia mansa, y las calles del barrio huelen a tierra mojada.",
            "Llueve fuerte, y el río se pone del color del barro.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos retumban sobre la sierra y los niños se esconden debajo de las mantas.",
            "El cielo se pone negro de golpe y cae una tormenta de verano que no dura nada.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla en el río, y el puente parece salir de la nada.",
            "La niebla de la mañana tapa los alminares, y el almuédano canta desde una nube.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento, y las telas tendidas en las azoteas se hinchan como velas.",
            "Hay viento, y el polvo de las calles se mete en el taller.",
        ),
        Clima.POLVO to listOf(
            "El aire está pesado y amarillo, y el calor no deja respirar.",
            "Sopla un viento caliente del sur, y en las calles no hay nadie a mediodía.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, cosa que en Córdoba no pasa casi nunca, y los niños salen a tocar la nieve como si fuera sal.",
            "Caen copos sobre las azoteas, y se derriten antes de llegar al suelo.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando los patios se llenan de flores y el azahar perfuma las calles.",
            "En primavera la ciudad sale a las huertas del río a ver los almendros y los rosales.",
            "Es el tiempo de los gusanos de seda en las alquerías, que comen hojas de morera sin parar.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, el tiempo del calor que derrite las piedras.",
            "En verano la ciudad duerme la siesta y vive de noche.",
            "Es el tiempo de los melones, de las higueras y del agua fresca de los pozos.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo de la vendimia y de las primeras lluvias.",
            "En otoño el aire se pone limpio y los telares trabajan a gusto.",
            "Es el tiempo de las granadas y de la aceituna.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando la casa se calienta con braseros y el patio está húmedo.",
            "En invierno la luz del taller dura poco y hay que aprovecharla.",
            "Es el tiempo de las lluvias y de las naranjas amargas.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora teje en el telar del taller, sentada en el banco, pasando la lanzadera de un lado a otro y apretando cada pasada con el peine. Encima, un muchacho tira de los lazos que levantan los hilos del dibujo: un pájaro, una rama, una letra. La seda brilla aunque no le dé el sol.", hacer = Hacer.TRABAJO),
        v("A esta hora urde el telar para una tela nueva, que es lo más largo y lo más delicado: miles de hilos de seda, uno por uno, contados y puestos en orden. Si uno se cruza, la tela entera sale mal. {nombre} no habla con nadie mientras urde. Las otras ya lo saben.", hacer = Hacer.TRABAJO),
        v("A esta hora teje la banda de letras de la tela, la que dice el nombre del califa y una bendición, que es lo más difícil: las letras tienen que salir derechas y legibles, y el telar no perdona. {nombre} no sabe leer bien, pero sabe tejer las letras. El maestro las revisa de cerca, con los ojos entornados.", hacer = Hacer.TRABAJO),
        v("A esta hora devana madejas de seda en el patio del taller, haciendo ovillos con la seda cruda que llega de las alquerías de la sierra, amarilla y brillante. Se enreda. Se desenreda. Las tejedoras hablan mientras devanan, de todo y de todos. Es el mejor rato del día.", hacer = Hacer.TRABAJO),
        v("A esta hora lleva madejas al taller de Isaac, el tintorero, que tiene las manos rojas, azules y amarillas según el día, y el patio lleno de tinas que humean. Hoy tiñe de carmesí. Le enseña a {nombre} el color contra la luz. Ella dice que le falta. Él dice que no. Ella tiene razón.", hacer = Hacer.TRABAJO),
        v("A esta hora remata una tela terminada: corta los hilos, cose los bordes, la dobla con papel entre los pliegues para que no se marque, y se la da al maestro, que la mira contra la ventana, en silencio, mucho rato. Luego asiente. {nombre} respira.", hacer = Hacer.TRABAJO),

        v("A esta hora come lo que hay, sentada en el suelo con las piernas cruzadas: pan, queso fresco, aceitunas y agua de la jarra que suda. Se come con la mano derecha, de una fuente común, y con las manos limpias después, que la seda no perdona una mancha de aceite.", hacer = Hacer.COMER),
        v("A esta hora come pan caliente con aceite, unos higos secos y leche. Alí, si está, come con la boca llena y habla igual, y la suegra le riñe. {nombre} se ríe por dentro, que ella también lo hacía.", hacer = Hacer.COMER),
        v("A esta hora come berenjena frita con miel, que es lo que más le gusta, con pan y agua fresca. La berenjena la trajo Yusuf del zoco y Fátima la frió como solo ella sabe. {nombre} se chupa los dedos cuando nadie mira.", hacer = Hacer.COMER),

        v("A esta hora reza la oración del alba en el patio, sobre su esterilla, mirando hacia el sureste, hacia La Meca, que está muy lejos, más allá del mar y del desierto. Reza deprisa en invierno, por el frío, y despacio en verano, que es cuando más le gusta.", hacer = Hacer.REZAR),
        v("A esta hora reza con Yusuf en el patio, uno detrás del otro, mientras el almuédano termina la llamada. Fátima reza en su cuarto, sola, porque dice que reza mejor sin ver a nadie. Alí reza con ellos, o lo intenta, y mira a su padre para saber qué viene después.", hacer = Hacer.REZAR),
        v("A esta hora reza la oración del alba y luego se queda un rato sentada, con las cuentas en la mano, diciendo los nombres de Dios en voz baja mientras el cielo se aclara. Es el único rato del día en que nadie le pide nada. Lo alarga todo lo que puede.", hacer = Hacer.REZAR),

        v("A esta hora está en el baño de las mujeres del barrio, que abre para ellas por la tarde: la sala fría, la templada y la caliente, con el vapor que no deja ver. Las mujeres se lavan, se peinan, se ponen alheña y hablan de todo. El baño es el mejor sitio para enterarse de las cosas.", hacer = Hacer.GENTE),
        v("A esta hora está en el patio de la casa con las vecinas, sentadas junto a la fuente, cosiendo, pelando almendras y hablando. Juliana trae a su hermana. La vecina del otro lado trae dulces. Los niños corren alrededor de la fuente hasta que alguien les grita.", hacer = Hacer.GENTE),
        v("A esta hora pasea con Yusuf por el zoco, que a esta hora está lleno de gente que sale del trabajo: los vendedores de agua, los de especias, los de telas, los cambistas. Yusuf le enseña un perfume nuevo que le llegó de Oriente. Ella dice que huele a viejo. Él dice que eso es lo que vale.", hacer = Hacer.GENTE),

        v("A esta hora cuida la casa: barre el patio, riega las plantas, saca agua del pozo, enciende el fuego de la cocina. Fátima le da instrucciones desde la sombra. {nombre} hace lo que le dice, a su manera. Las dos lo saben. Así se llevan bien.", hacer = Hacer.CUIDAR),
        v("A esta hora prepara la cena: un guiso de cordero con membrillos y especias, que lleva horas en el fuego, y pan del horno del barrio. La casa huele a canela y a comino. Alí ronda la cocina. {nombre} lo echa. Vuelve. Lo vuelve a echar.", hacer = Hacer.CUIDAR),
        v("A esta hora cuida de Fátima, la suegra, que tiene las piernas hinchadas y se queja del frío: le pone un brasero junto a los pies y le frota las piernas con aceite. Fátima le cuenta, otra vez, cómo era Córdoba cuando ella era joven. {nombre} escucha. Siempre aprende algo nuevo.", hacer = Hacer.CUIDAR),

        v("A esta hora duerme en la sala de la casa, sobre colchones de lana en el suelo, con Yusuf a un lado y el brasero apagándose. Fuera, en el patio, la fuente sigue hablando sola. Un gato maúlla en la azotea. {nombre} ya no lo oye.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con dibujos de seda que se tejen solos, pájaros que se salen del telar y vuelan por el taller. Se despierta y oye a Alí hablar en sueños. Le pone la mano en la frente. Se vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme en la azotea, en verano, sobre una esterilla, porque dentro de la casa no se puede. Encima tiene todas las estrellas. Abajo, en la calle, pasa la ronda de la noche con su farol. Todo está bien.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La casa de {nombre} da a una calleja estrecha y quebrada, con una puerta pequeña y ninguna ventana: todo mira hacia dentro, al patio, con su fuente, su naranjo y sus macetas. Desde fuera es una pared blanca. Desde dentro, un jardín. {entorno}"),
        v("{entorno} El taller del maestro Abu Bakr está en el barrio de los tejedores, cerca del zoco, en una casa grande con seis telares en las salas que dan al patio. Se oye desde la calle el golpe de los peines, como una lluvia que no para."),
        v("La mezquita grande está en el centro de la ciudad, junto al río y al palacio viejo, con su bosque de columnas y sus arcos rojos y blancos que no se acaban nunca. Dicen que el califa le está poniendo un mihrab nuevo, todo de oro y mosaicos. {entorno}"),
        v("En el zoco hay una calle para cada oficio: los perfumistas, como Yusuf, los cambistas, los vendedores de seda, los herreros, los de los libros. Se oyen todas las lenguas: el árabe, el romance de la gente, el bereber de los soldados, el hebreo de los mercaderes. {entorno}"),
        v("{entorno} Al otro lado del río, por el puente viejo de piedra, están los arrabales y las huertas, y más allá el campo. Al oeste, a medio día de camino, en la falda de la sierra, la ciudad del califa, Madinat al-Zahra, que de noche brilla como si fuera de oro."),
        v("En el barrio de {nombre} viven musulmanes, cristianos y judíos, puerta con puerta: Juliana, la vecina, va a misa a la iglesia de San Acisclo; Isaac, el tintorero, guarda el sábado; Yusuf va a la mezquita el viernes. Se saludan cada mañana, y se pelean por el agua del pozo. {entorno}"),
    )

    /** El Guadalquivir según la estación (arco «rio»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("rio", 0.8, 1.0, frases = listOf(
            "El río va crecido y turbio, y las norias de la orilla giran tan deprisa que chirrían.",
            "El Guadalquivir baja ancho y marrón, lamiendo los arcos del puente.",
        )),
        Entorno("rio", 0.4, 0.8, frases = listOf(
            "El río corre tranquilo bajo el puente, y las lavanderas golpean la ropa en las piedras de la orilla.",
            "El Guadalquivir va con su agua de siempre, y las barcas de los pescadores salen al amanecer.",
        )),
        Entorno("rio", 0.0, 0.4, frases = listOf(
            "El río va bajo, y se ven las islas de grava y los juncos; algunos molinos están parados.",
            "El Guadalquivir es un hilo verde entre piedras secas, y los niños lo cruzan a pie.",
        )),
    )

    private val GENTE = listOf(
        v("Yusuf, su marido, es perfumista y tiene una tienda pequeña en el zoco, que huele tan fuerte que se nota desde la esquina: agua de rosas, almizcle, ámbar, maderas de Oriente. Es bueno, distraído y presumido. {nombre} lo quiere, y lo dice poco, porque él ya lo sabe."),
        v("Fátima, su suegra, vive con ellos en la casa desde que enviudó, en el cuarto que da al patio. Es pequeña, vieja y de lengua afilada. Le enseñó a {nombre} a cocinar como le gusta a Yusuf, y todavía la corrige. Por las noches le cuenta historias de cuando el califa era otro."),
        v("El maestro Abu Bakr, el dueño del taller, es un hombre alto y callado, que teje desde los diez años y que conoce cada hilo de cada telar. Trabaja para la corte y para los ricos. A {nombre} la trata con respeto, que es más de lo que hace con los demás."),
        v("Isaac, el tintorero, es judío, viejo y tiene las manos de todos los colores. Sabe sacar el carmesí del grano de las encinas, el azul del añil, el amarillo de la gualda, y guarda sus secretos como un avaro. A {nombre} le enseña alguno, porque dice que es la única que mira los colores de verdad."),
        v("Las otras tejedoras del taller son cinco: dos viejas que tejen sin mirar, dos jóvenes que miran todo, y una viuda que no habla. {nombre} es la que mejor teje las letras, y las otras lo saben, y eso a veces trae problemas, y a veces trae favores."),
        v("Juliana, la vecina, es cristiana, de una familia que vivía en Córdoba antes de que llegaran los árabes, y habla árabe mejor que muchos árabes. Va a casarse, y le enseña a {nombre} el ajuar que cose. {nombre} le enseña a ella a tejer un dibujo de pájaros.", hasta = "boda_juliana"),
        v("Juliana, la vecina, cristiana, ya está casada con un herrero del barrio, y vive en la casa de enfrente. Viene cada tarde al patio a hablar, con su costura. A veces trae a su marido, que no habla nada y se come todos los dátiles. {nombre} lo deja.", desde = "boda_juliana"),
        v("Alí, su hijo, tiene seis años y no se está quieto nunca: corre por el patio, se sube a la higuera, pregunta todo. Todavía no va a la escuela, pero ya sabe contar hasta cien y dice que de mayor será perfumista, como su padre, o califa.", hasta = "ali_escuela"),
        v("Alí, su hijo, va a la escuela de la mezquita del barrio, donde un maestro con una vara les enseña a leer y a recitar el Corán, sentados en el suelo con sus tablillas. Vuelve a casa recitando lo que aprendió, y {nombre} lo repite con él, que ella aprendió poco.", desde = "ali_escuela", hasta = "ali_aprendiz"),
        v("Alí, su hijo, ya trabaja en la tienda de su padre: barre, despacha, aprende a mezclar los aceites y a pesar el almizcle. Huele siempre a rosas. Cuando vuelve a casa, {nombre} le huele el pelo y adivina qué ha vendido hoy.", desde = "ali_aprendiz"),
        v("{nombre} espera un hijo, y teje igual, aunque el maestro le pone un cojín en el banco. Fátima le prepara caldos y le prohíbe cargar cántaros. Yusuf le trae de la tienda aceites para la tripa. Las vecinas le dicen si será niño o niña según cómo camina.", desde = "zaynab_espera", hasta = "nace_maryam"),
        v("Maryam, su hija, es pequeña, morena y gritona, y duerme en una cuna que Yusuf colgó de una viga del patio, que se mece con la brisa. Fátima la cuida mientras {nombre} está en el taller. Cuando vuelve, la niña la reconoce por el olor de la seda.", desde = "nace_maryam"),
        v("Rabia, la aprendiza, es una niña de once años, hija de una viuda del barrio, que el maestro le dio a {nombre} para que le enseñe. Es lista, torpe y tiene las manos pequeñas, que para la seda es bueno. {nombre} se ve en ella, y no se lo dice.", desde = "aprendiza"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del oficio: porque se lo enseñó una vieja tejedora, que lo sabía de otra."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En el taller se dice que la seda tiene memoria, y que se acuerda de las manos que la tocaron."),
        v("{dato_objeto} Son cosas que se aprenden mirando, como casi todo."),
        v("En la casa de una tejedora hay pocas cosas, y casi todas sirven para tejer, para cocinar o para oler bien. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("lanzadera", "la lanzadera", "una lanzadera", "La lanzadera",
            "La lanzadera es una pieza de madera con forma de barca, pulida por mil manos, con una canilla de hilo dentro; se lanza de un lado al otro del telar entre los hilos levantados, y en manos de una buena tejedora vuela tan rápido que no se ve."),
        ObjetoCuento("peine", "el peine del telar", "un peine de telar", "El peine",
            "El peine del telar es un marco con cientos de púas finas de caña, entre las que pasan los hilos de la urdimbre; después de cada pasada de la lanzadera se golpea contra la tela para apretarla, y de la fuerza de ese golpe depende que la seda sea fina o floja."),
        ObjetoCuento("huso", "el huso", "un huso", "El huso",
            "El huso es un palo fino con una rueda de piedra o de hueso abajo, que se hace girar para torcer el hilo; las mujeres hilan con él de pie, caminando, hablando, mirando a los niños, y nunca se les cae."),
        ObjetoCuento("seda", "la seda", "una madeja de seda", "La seda",
            "La seda se saca del capullo de un gusano que solo come hojas de morera; se cuecen los capullos y se tira del hilo, que puede ser larguísimo, de un solo capullo; en las alquerías de la sierra la crían, y en Córdoba la tejen."),
        ObjetoCuento("carmesi", "el carmesí", "un saquito de grana", "El carmesí",
            "El carmesí sale de un insecto pequeño que vive en unas encinas bajas, que parece una semilla y por eso lo llaman grano; se recoge, se seca y se muele, y da un rojo tan vivo que vale más que el oro, y que solo pueden vestir los ricos."),
        ObjetoCuento("candil", "el candil", "un candil", "El candil",
            "El candil es un cuenco de barro con un pico donde va la mecha y aceite de oliva dentro; da una luz pequeña y amarilla, huele a aceite quemado, y en cada casa hay tres o cuatro, uno en cada nicho de la pared."),
        ObjetoCuento("alcuza", "la alcuza", "una alcuza", "La alcuza",
            "La alcuza es una jarrita de barro o de cobre con un pico fino, donde se guarda el aceite de oliva para la cocina y para los candiles; la de la casa de Fátima es de cobre, vieja y abollada, y nadie más la toca."),
        ObjetoCuento("agua_rosas", "el agua de rosas", "un frasco de agua de rosas", "El agua de rosas",
            "El agua de rosas se hace destilando pétalos en un alambique, y sale clara y perfumada; se echa en los dulces, en las manos de los invitados, en la ropa y en los muertos, y en la tienda de Yusuf hay de tres calidades."),
        ObjetoCuento("alhena", "la alheña", "un poco de alheña", "La alheña",
            "La alheña es el polvo de las hojas de un arbusto, que mezclado con agua hace una pasta verde que tiñe de rojo; las mujeres se pintan con ella las manos, los pies y el pelo, sobre todo en las bodas y en las fiestas."),
        ObjetoCuento("laud", "el laúd", "un laúd", "El laúd",
            "El laúd tiene el cuerpo redondo como media pera, el mástil corto y doblado hacia atrás y cinco órdenes de cuerdas de seda; se toca con una pluma de águila, y en las casas buenas de Córdoba siempre hay alguien que lo sabe tocar."),
        ObjetoCuento("jarra", "la jarra", "una jarra", "La jarra",
            "La jarra de barro poroso suda por fuera, y al sudar enfría el agua de dentro; se cuelga en el patio, a la sombra, en una corriente de aire, y en pleno verano el agua sale fresca como de un pozo."),
        ObjetoCuento("dirham", "el dírham", "un dírham", "El dírham",
            "El dírham es la moneda de plata del califa, con letras por las dos caras y ninguna cara de persona, que el islam no lo quiere; con veinte dírhams se compra un cordero, y con uno, el pan de una semana."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en Córdoba como se piensa en el aire: no se la elige, se vive dentro. Le da trabajo, agua, una casa con patio y gente de todas partes. A sus {edad} años no ha salido nunca de ella, y no tiene prisa por salir."),
        v("Piensa a veces en las mujeres que tejieron antes que ella en este taller, y en las que tejerán después. Las telas se gastan, se rompen, se queman. Alguna, piensa, durará, guardada en un cofre de un rey, y alguien la mirará y no sabrá quién la tejió."),
        v("{mundo} Ella piensa en cosas más cercanas: en el hilo, que se enreda; en Alí, que tiene tos; en la cena. Las cosas grandes son del califa y de sus generales, que las piensan en sus palacios de la sierra."),
        v("{mundo} A sus {edad} años, {nombre} ha tejido más seda de la que podrá vestir en toda su vida, y toda se la han puesto otros. No le importa. Lo que le importa es que cuando ve pasar a una señora con una tela suya, la reconoce, y nadie más lo sabe."),
        v("Piensa en las letras que teje, que no sabe leer del todo, y que dicen cosas de Dios y del califa. Las teje con cuidado, porque las palabras de Dios no se pueden torcer. A veces piensa que Dios las lee mejor que nadie, y que a él no le importa que ella no sepa leerlas."),
        v("{mundo} A veces, de noche, en la azotea, {nombre} mira las estrellas, que Yusuf dice que tienen nombres árabes que hasta los cristianos usan, y piensa que el mundo es grande, y que Córdoba es su centro. Luego piensa que todos piensan eso de su ciudad. Se ríe sola."),
    )

    private val MUNDOS = mapOf(
        "principe" to listOf(
            "En el zoco se habla de la favorita del califa, la vasca, y de sus hijos, que son la esperanza del califato.",
            "Se habla de los fatimíes, que mandan en Egipto y hacen una ciudad nueva junto al Nilo.",
            "Se dice que el califa compra libros en todas partes del mundo y que su biblioteca ya no cabe en el palacio.",
        ),
        "mihrab" to listOf(
            "Se habla del mihrab nuevo de la mezquita grande, que tiene mosaicos de oro que hicieron artesanos de Bizancio.",
            "Se dice que los mayus, los del norte, vuelven a veces por la costa del océano, y que hay que vigilar.",
        ),
        "maryam" to listOf(
            "Se habla de Ibn Abi Amir, un joven listo de la corte, que dicen que tiene la confianza de la madre del heredero.",
            "Se dice que el general Galib es el hombre más poderoso del califato después del califa.",
        ),
        "africa" to listOf(
            "Se habla de África, donde los idrisíes cambian de bando cuando les conviene, y de si el califa mandará un ejército.",
            "Se dice que el califa de los fatimíes mira más a Egipto que al Magreb.",
        ),
        "idrisi" to listOf(
            "Se habla del general Galib, que pelea en África, y de lo que traerá cuando vuelva.",
            "Se dice que el califa está enfermo, y que el heredero es un niño.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} vuelve del taller por las callejas, que se llenan de gente que vuelve a casa. Los vendedores encienden sus candiles. Desde los alminares suena la llamada de la puesta del sol, y luego otra, y otra. En casa, la fuente y la cena."),
        v("Anochece en Córdoba, y la ciudad se enciende de candiles en las ventanas y faroles en las esquinas. Desde la azotea {nombre} ve la mezquita grande, iluminada, y al fondo, en la sierra, las luces de la ciudad del califa. Luego baja a cenar."),
        v("Se cena en el patio, junto a la fuente, con la familia y a veces una vecina: pan, guiso, fruta. Alí cuenta lo que hizo. Yusuf cuenta lo que vendió. Fátima cuenta lo que era antes. {nombre} escucha a los tres y no cuenta nada, que ya lo contará mañana en el taller."),
        v("De noche, la casa se cierra: la puerta de la calle con su tranca, los candiles uno por uno, el brasero tapado con ceniza. {nombre} da la última vuelta por el patio, mira la higuera, la fuente, el cielo. Luego se acuesta. Afuera ladra un perro, y otro le contesta.", ),
        v("La última luz del día se queda un momento en lo alto del alminar de la mezquita grande, rosa, cuando la ciudad ya está a oscuras. {nombre} la ve desde la azotea, recogiendo la ropa tendida. Luego se va. Arriba sale la primera estrella."),
        v("Al anochecer el patio se refresca por fin, y la familia sale a sentarse junto a la fuente con un vaso de agua de la jarra. Huele a jazmín. Alguien toca el laúd en una casa vecina. {nombre} cierra los ojos y escucha. Es el mejor rato del verano.", meses = setOf(6, 7, 8, 9)),
        v("Al caer la tarde, en invierno, la casa se reúne alrededor del brasero, en la sala, con las piernas tapadas con una manta. Fátima pela castañas. Alí se duerme. {nombre} hila un poco a la luz del candil, que no se puede estar sin hacer nada.", meses = setOf(11, 12, 1, 2)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "yannayr" to unDia(
            Parrafo.MANANA to "Hoy es Yannayr, el primer día del año de los cristianos, que en Córdoba celebra media ciudad, aunque los alfaquíes digan que no se debe. Los niños hacen casitas con frutas secas, las familias compran dulces y se regalan cosas pequeñas. Juliana trae al patio una bandeja de almojábanas.",
            Parrafo.PUEBLO to "En el zoco hay puestos de frutas secas, de figuritas de masa con forma de animales, de juguetes de barro. Los niños musulmanes, cristianos y judíos se pelean por lo mismo. Un alfaquí predica en una esquina que esto es costumbre de infieles. Nadie le hace caso.",
            Parrafo.PENSAMIENTO to "{nombre} se come una almojábana de Juliana y piensa que en Córdoba las fiestas son de todos, aunque los dioses no lo sean. Yannayr es de los cristianos, el Ramadán de los musulmanes, el sábado de los judíos. Y todos comen dulces en todas.",
        ),
        "almunias" to unDia(
            Parrafo.MANANA to "Hoy la familia sale a las almunias de la orilla del río, las huertas de recreo de los ricos, que en primavera abren sus puertas a la gente para ver los almendros, los rosales y los estanques. {nombre} prepara una cesta con pan, queso y dulces.",
            Parrafo.TRABAJO to "A esta hora no teje: el taller cierra el día de las almunias, como media ciudad. {nombre} pasea con Yusuf y Alí entre los parterres, se sienta junto a un estanque y mira los peces, que no tienen nada que tejer.",
            Parrafo.PUEBLO to "En las almunias del río hay árboles de flores, acequias que corren entre los parterres, estanques con peces, pabellones con columnas. La gente pasea, se sienta en la hierba, come, canta. Por un día, los jardines de los ricos son de todos.",
            Parrafo.ANOCHECER to "Al caer la tarde vuelven a casa por el puente, con Alí dormido en brazos de su padre y la cesta vacía. {nombre} lleva en el pelo una flor de almendro que le puso Yusuf. No se la quita hasta que se seca, tres días después.",
        ),
        "seda_nueva" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy llegó al taller la seda nueva del año, en mulas que bajan de las alquerías de la sierra: madejas amarillas y blancas, todavía con el olor de los capullos cocidos. El maestro las revisa una por una y discute el precio con el arriero.",
                Parrafo.TRABAJO to "A esta hora revisa las madejas de seda nueva con las otras tejedoras, a la luz del patio, buscando hilos rotos, nudos y partes que no tomaron bien el agua. La seda de este año es buena. {nombre} la toca y piensa en lo que tejerá con ella.",
                Parrafo.PUEBLO to "En el barrio de los tejedores es día de fiesta y de trabajo: los arrieros descargan, los maestros regatean, las tejedoras miran y opinan, los niños se cuelan entre las mulas. Toda la calle huele a seda nueva, que tiene un olor que no se parece a nada.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora devana la seda nueva en el patio del taller, haciendo madejas para el tintorero. Es un trabajo largo y tranquilo.",
                "A esta hora lleva seda nueva al taller de Isaac, que la espera para teñirla. Isaac la huele, la toca y dice que este año el carmesí saldrá bien.",
            )),
        ),
        "ramadan" to unDia(
            Parrafo.MANANA to "Hoy empezó el Ramadán: anoche se vio la luna nueva desde la azotea de la mezquita, y el almuédano lo anunció. Esta madrugada {nombre} se levantó a oscuras para la primera comida del alba, y desde la llamada del alba no ha comido ni bebido nada.",
            Parrafo.PUEBLO to "En la ciudad todo cambia de un día para otro: de día las tiendas abren tarde y cierran pronto, la gente anda despacio y de mal humor, y en la mezquita grande se cuelgan lámparas nuevas para las oraciones de la noche. Los vendedores de buñuelos preparan sus sartenes.",
            Parrafo.ANOCHECER to "Al caer la tarde, en el patio, la familia espera la llamada de la puesta del sol con la mesa puesta. Cuando suena, Alí grita, y todos comen un dátil y beben agua. El primer día del ayuno es el más largo. {nombre} piensa que faltan veintinueve.",
        ),
        "qadr" to unDia(
            Parrafo.MANANA to "Hoy es la noche veintisiete del Ramadán, la que muchos creen que es la noche del destino, cuando bajó el Corán y los ángeles bajan a la tierra. Esta noche nadie duerme: se reza hasta el alba. {nombre} va a ir a la mezquita grande con las vecinas.",
            Parrafo.PUEBLO to "La mezquita grande está llena hasta las calles, con todas sus lámparas encendidas, cientos, que hacen brillar las columnas y los arcos como si fueran de fuego. Las mujeres rezan en su parte, detrás. Se oye el Corán entero, recitado por voces que se turnan.",
            Parrafo.PENSAMIENTO to "{nombre} reza en la mezquita de noche, rodeada de mujeres, con las lámparas encima, y piensa que si esta es la noche del destino, Dios está escribiendo lo que pasará este año. Le pide que escriba cosas buenas para los suyos. Y para ella, un telar propio.",
        ),
        "eid_fitr" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es la fiesta del fin del ayuno: se acabó el Ramadán, y la ciudad entera amanece con ropa nueva. {nombre} viste a Alí con la túnica que le cosió, y ella estrena un manto de seda que tejió en secreto. Yusuf va a la oración grande de la explanada, con todos los hombres.",
                Parrafo.TRABAJO to "A esta hora no teje: el taller está cerrado los tres días de la fiesta, y el maestro Abu Bakr manda a cada tejedora un plato de dulces. {nombre} se lo come en el patio, con las manos limpias de seda por primera vez en semanas.",
                Parrafo.GENTE to "Esta tarde la familia visita a los parientes, y los parientes los visitan a ellos, y en cada casa hay dulces: buñuelos, pasteles de almendra, dátiles rellenos. Los niños reciben monedas. Alí junta cuatro dírhams y se cree rico. Fátima le quita dos, para guardarlos.",
                Parrafo.PENSAMIENTO to "{nombre} come un pastel de almendra a mediodía, en el patio, y le parece raro y maravilloso comer de día después de un mes. Piensa que el ayuno sirve para eso: para que el pan sepa a pan. Y para acordarse de los que no tienen pan ningún día.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora no teje, que sigue la fiesta y el taller está cerrado. Ayuda a Fátima a llevar dulces a los parientes de Yusuf.",
                "A esta hora pasa por el taller, aunque esté cerrado, a ver si su tela sigue bien tapada. Sigue. Vuelve a casa, a la fiesta.",
            ), Parrafo.GENTE to listOf(
                "Sigue la fiesta: hoy las vecinas se visitan con bandejas de dulces, y el patio de {nombre} se llena de mujeres, de niños y de risas.",
                "Último día de la fiesta. Mañana vuelve el taller. Hoy {nombre} pasea con Yusuf por el zoco, que está lleno de gente con ropa nueva.",
            )),
        ),
        "eid_adha" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es la fiesta del sacrificio, la grande, la que recuerda a Abraham, que iba a sacrificar a su hijo y Dios le dio un carnero. Yusuf compró un cordero hace una semana, que ha vivido en el patio y al que Alí le puso nombre. Hoy lo matan. Alí llora.",
                Parrafo.TRABAJO to "A esta hora corta la carne del cordero con Fátima en el patio: una parte para la casa, otra para los parientes, otra para los pobres, como manda la ley. Las manos le huelen a sangre y a sal. La cabeza se guarda para un guiso que solo sabe hacer Fátima.",
                Parrafo.ANOCHECER to "Esta noche se cena el cordero asado en el patio, con pan y especias, y vienen los parientes de Yusuf. Alí, que lloró por la mañana, se come dos raciones. Nadie se lo dice. {nombre} le da la tercera.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora reparte carne entre los vecinos pobres del barrio, con Alí, que lleva la cesta. Es la fiesta, y el taller está cerrado.",
                "A esta hora sala carne del cordero en tinajas, para que dure. La casa huele a sal y a sangre.",
                "A esta hora vuelve al taller, el último día de la fiesta, aunque no le toca: quiere ver cómo quedó una tela antes de que la entreguen.",
            )),
        ),
        "hegira" to unDia(
            Parrafo.MANANA to "Hoy empieza un año nuevo de la hégira, el que cuenta desde que el Profeta se fue de La Meca a Medina. En Córdoba no es una fiesta grande, como la del ayuno: se reza, se recuerda, se desea un buen año. Fátima dice que antes se celebraba más.",
            Parrafo.TRABAJO to "A esta hora teje como cualquier día, pero el maestro, antes de empezar, dice una bendición en voz alta por el año nuevo, y todas las tejedoras responden. Luego el golpe de los peines, como siempre. Un año nuevo se empieza trabajando, dice el maestro.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza, que es lunar y se va corriendo por las estaciones: este año el Ramadán caerá antes, y el que viene, antes todavía. Fátima dice que de niña ayunó en invierno, con los días cortos. A ella le tocan los veranos.",
        ),
        "ashura" to unDia(
            Parrafo.MANANA to "Hoy es Ashura, el décimo día del año, en que muchos ayunan, como ayunó el Profeta, y se dan limosnas. {nombre} ayuna, aunque no es obligatorio. Fátima también. Yusuf dice que él ya ayunó bastante en Ramadán. Fátima lo mira.",
            Parrafo.PUEBLO to "En el barrio, en Ashura, se reparten limosnas a los pobres en las puertas de las mezquitas, y en algunas casas se hace un guiso de granos y legumbres para regalar. Los niños piden por las casas. Los mendigos del puente tienen el mejor día del año.",
            Parrafo.ANOCHECER to "Al caer la tarde {nombre} rompe el ayuno con un poco de leche y pan, en el patio, y luego cocina el guiso de granos que su madre hacía para este día: trigo, garbanzos, habas y lentejas, con miel. Lo reparte entre las vecinas. Juliana se lleva dos platos.",
        ),
        "lluvias" to unDia(
            Parrafo.MANANA to "Hoy llegaron las primeras lluvias grandes del otoño, de golpe, después de meses de sequía, y la ciudad entera huele a tierra mojada. Las calles se vuelven arroyos. En el patio, la fuente se desborda y Alí chapotea feliz, hasta que lo ve su abuela.",
            Parrafo.PUEBLO to "En el río, el agua sube de un día para otro, turbia, y se lleva la basura de las orillas: ramas, cestos rotos, una barca vieja. Las lavanderas recogen la ropa a toda prisa. Los molineros miran sus ruedas. En los campos, los labradores se alegran.",
            Parrafo.PENSAMIENTO to "{nombre} mira llover desde la puerta del taller, con una madeja en la mano, y piensa que la seda nota la lluvia antes que la gente: se pone pesada, blanda, rara. Hoy no tejerá bien. Mañana, cuando el aire se lave, la seda volverá a cantar.",
        ),
        "encargo_tela" to unDia(
            Parrafo.MANANA to "Hoy al taller llegó un hombre del palacio con un encargo: una tela de seda con letras para el califa, para un manto de ceremonia. El maestro Abu Bakr la escuchó, asintió, y después, delante de todas, le dijo a {nombre} que la tejería ella. Nadie dijo nada. Todas la miraron.",
            Parrafo.TRABAJO to "A esta hora mira con el maestro el dibujo que mandaron de palacio: una banda de letras doradas sobre fondo carmesí, con una bendición para el califa y su nombre, entre dos filas de pájaros. Es lo más difícil que le han pedido. Calcula los hilos. Le tiemblan las manos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esta tela la llevará el califa, el hombre más poderoso de este lado del mundo, en una ceremonia que ella nunca verá. Sus manos estarán en ella. Le da vértigo. Luego piensa que también su suegra le dirá que no le salió tan bien. Se ríe.",
        ),
        "ali_escuela" to unDia(
            Parrafo.MANANA to "Hoy Alí empezó a ir a la escuela de la mezquita del barrio. {nombre} lo vistió con su túnica limpia, le peinó el pelo con agua y le dio una tablilla nueva de madera para escribir. Yusuf lo acompañó hasta la puerta. Alí no quiso que su madre fuera, para que no lo vieran.",
            Parrafo.GENTE to "El maestro de la escuela es un hombre viejo con una vara y una voz que se oye desde la calle, que enseña a veinte niños sentados en el suelo a recitar el Corán y a escribir las letras en sus tablillas. Al volver, Alí le enseña a su madre la primera letra. La hizo al revés.",
            Parrafo.PENSAMIENTO to "{nombre} mira la letra al revés de Alí y piensa que ella fue poco a la escuela, que su padre la sacó pronto para que aprendiera a tejer. Piensa que Alí sabrá leer lo que ella teje. Le parece un regalo, aunque él todavía no lo sepa.",
        ),
        "boda_juliana" to unDia(
            Parrafo.MANANA to "Hoy se casa Juliana, la vecina, con un herrero cristiano del barrio, en la iglesia de San Acisclo, con su cura, sus cantos en latín y sus velas. {nombre} no entra en la iglesia, pero la ayudó a vestirse esta mañana, y le pintó las manos con alheña, como a una novia musulmana.",
            Parrafo.PUEBLO to "Por la calle pasa el cortejo de la novia, con músicos que tocan el laúd y la flauta, y mujeres que cantan. Las vecinas, musulmanas, cristianas y judías, salen a las puertas a mirar y a echar agua de rosas. Juliana va en una mula blanca, roja de vergüenza.",
            Parrafo.ANOCHECER to "Esta noche hay fiesta en la casa del herrero, con comida para el barrio entero y música hasta tarde. {nombre} va con Yusuf, y Alí se duerme debajo de una mesa. Juliana la abraza al despedirse y le dice que ahora será su vecina de enfrente.",
        ),
        "principe" to unDia(
            Parrafo.MANANA to "Hoy se supo en la ciudad que murió el hijo mayor del califa, Abderramán, un niño de ocho años. Desde el palacio llegó la orden de luto. En la mezquita grande se reza por él. El zoco cierra a mediodía. Las mujeres hablan bajito en los patios.",
            Parrafo.PUEBLO to "En las calles, la gente habla de la madre del niño, la favorita del califa, una vasca que llegó esclava y que ahora tiene más poder que los visires. Dicen que no come. Dicen que le queda otro hijo, más pequeño. Dicen muchas cosas, y nadie sabe ninguna.",
            Parrafo.PENSAMIENTO to "{nombre} mira a Alí, que juega en el patio, y piensa en la madre del niño muerto, en su palacio, con todo el oro del mundo y sin su hijo. Le da un abrazo a Alí, tan fuerte que el niño protesta. No le explica por qué.",
        ),
        "carmesi" to unDia(
            Parrafo.MANANA to "Hoy Isaac, el tintorero, la llamó a su taller para enseñarle, por fin, cómo saca el carmesí: el grano de las encinas, molido, en agua con alumbre, a fuego lento, con la seda dentro, el tiempo justo. Le dijo que no se lo enseña a nadie. Le dijo que a ella sí.",
            Parrafo.TRABAJO to "A esta hora está en el patio de Isaac, entre tinas que humean, con las manos rojas hasta el codo, metiendo y sacando madejas de seda del tinte, mirándolas contra la luz. Isaac dice: más. Dice: menos. Dice: ahora. La seda sale roja como la sangre de un dios.",
            Parrafo.PENSAMIENTO to "{nombre} se mira las manos rojas, que tardarán días en limpiarse, y piensa que Isaac le ha dado un secreto de su familia, que es judía, a ella, que no lo es. No sabe por qué. Piensa que quizás porque es la única que mira los colores como él.",
        ),
        "azhar" to unDia(
            Parrafo.MANANA to "Hoy un mercader que vino de Oriente le contó a Yusuf en la tienda que los fatimíes, los enemigos del califa, están levantando una mezquita enorme en su ciudad nueva de Egipto, junto al Nilo, para que sea la más grande del mundo. Yusuf lo cuenta en la cena, enfadado.",
            Parrafo.GENTE to "Fátima dice que no habrá mezquita como la de Córdoba en ningún sitio, y que los fatimíes son herejes. Yusuf dice que eso dicen todos de la suya. Alí pregunta qué es un hereje. Nadie le contesta. {nombre} sirve más guiso, para que se callen.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Egipto, que no sabe bien dónde está, más allá del mar, y en la gente de allí, que tejerá seda también, con otras letras y para otro califa. Piensa que las tejedoras de allí y las de aquí podrían entenderse sin hablar. Con las manos.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "cordobesa", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + RAMADAN_MANANA,
            Parrafo.TIEMPO to TIEMPO + RAMADAN_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + RAMADAN_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + RAMADAN_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + listOf(
                v("Desde que le dieron la tela del califa, {nombre} piensa en ella hasta dormida: cada letra, cada pájaro, cada hilo de oro. La teje despacio, una mano de seda al día. El maestro la mira cada tarde y no dice nada. Ella sabe lo que significa.", desde = "encargo_tela", hasta = "tela_califa"),
                v("La tela del califa ya está en el palacio, y {nombre} no sabe si el califa se la puso ni si le gustó. El maestro dice que sí. Ella lo imagina con el manto en una ceremonia, y en el borde, sus letras. Le basta con imaginarlo.", desde = "tela_califa", hasta = "telar_grande"),
                v("Desde que el maestro le dio el telar grande, {nombre} teje la tela de una boda de la corte, con pájaros, árboles y una banda de letras que dice «felicidad perfecta». Es la tela más larga que ha tejido nunca. Va por la mitad, y le parece que no se acaba.", desde = "telar_grande", hasta = "tela_boda"),
                v("Desde que el maestro le deja llevar el taller, {nombre} llega antes que nadie y se va la última, revisa los telares de las demás y discute con los arrieros de la seda. Las tejedoras viejas la miran raro. El maestro, contento. Ella, cansada y contenta.", desde = "taller"),
            ),
            Parrafo.ANOCHECER to ANOCHECER + RAMADAN_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
