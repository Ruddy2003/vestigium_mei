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
 * El cuento de Ama, pintora de cuevas en un valle del suroeste de Europa hace unos 17,000 años. Sigue la gramática y el
 * tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Como el de Naa, vive antes de la escritura: no dice ningún año ni usa palabras de calendario (semanas, meses); el
 *   tiempo se cuenta en lunas, nevadas y pasos de manadas.
 * - El invierno lo pasa en el abrigo de roca junto al río; el verano, en las tiendas de piel de la meseta (estado
 *   «verano»). Cada tarde pinta en lo hondo de la cueva; en verano, lejos de la cueva, graba en hueso y en piedra.
 * - El párrafo del pueblo lee el arco «nieve». El friso que pinta aparece en las variantes con fecha: llega a la mitad
 *   en `friso_mitad` (temporada 3) y se termina en `friso_fin` (5).
 * - No repite lo que ya dice «Su vida»: el medio año de invierno, los veinticinco del grupo, la carne y la grasa, las
 *   agujas de hueso, el ocre molido, las lámparas, los bisontes y las manos de mujeres.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG3.kt`):
 * - Lin nace en la temporada 2 (`nace_lin`); Ama la espera desde `ama_espera` (1).
 * - Yemi, su madre, vive hasta `muere_yemi` (temporada 3). A Sel la conoce en `sel` (3).
 * - El friso empieza con `primer_caballo` (temporada 1).
 * - Las fiestas que vuelven cada año no nombran a Lin, a Yemi ni a Sel.
 */
internal object PiezasAma {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta bajo el abrigo de roca, entre pieles, con el fuego reducido a brasas y el techo de piedra negro de humo encima. Alguien ya sopla las brasas para revivirlas. Fuera se oye el río, que nunca se calla, y un cuervo."),
        v("La primera luz entra de lado bajo la roca, porque el abrigo mira al sur, y toca primero las pieles colgadas que cortan el viento. {nombre} sale a orinar entre las piedras, con los pies descalzos en la escarcha, y vuelve corriendo al calor."),
        v("A {nombre} la despiertan los niños del grupo, que ya pelean por un hueso con tuétano junto al fuego. Se incorpora, se pone la ropa de piel, que huele a humo y a ella, y se ata a la cintura la bolsa con el ocre, el carbón y la piedra de moler."),
        v("{nombre} sale del abrigo y se queda un momento mirando el valle: el río abajo, los acantilados de caliza enfrente, con sus agujeros y sus cuevas, y la estepa más allá, sin un árbol hasta donde se ve. Busca manadas. Hoy no hay."),
        v("Lo primero del día es el fuego: hay que alimentarlo con huesos, que arden mejor que la poca leña del valle, y con grasa. {nombre} echa un hueso grande y mira cómo la grasa se enciende y chisporrotea. El olor despierta a los que quedan dormidos."),
        v("{nombre} se lava la cara en el agua del río, tan fría que duele, y se mira en un remanso quieto, donde la cara le sale temblando. Se pinta dos rayas de ocre en las mejillas, como su madre, como la madre de su madre. Hoy tiene que bajar a la cueva."),
        v("Antes de que salga del todo el sol, {nombre} toca con la palma la pared del abrigo, donde hay una mano roja soplada hace tantas generaciones que nadie sabe de quién es. Es lo que hace cada mañana. La mano de la pared es más pequeña que la suya."),
    )

    /** El verano en el campamento de la meseta (estado «verano»): más de tres meses cada año, así que cada párrafo tiene varias. */
    private fun ve(texto: String, hacer: Hacer? = null) = v(texto, estado = "verano", hacer = hacer)

    private val VERANO_MANANA = listOf(
        ve("{nombre} se despierta en la tienda de piel del campamento de la meseta, con la luz atravesando la piel como si fuera sangre. Aquí arriba no se oye el río: se oyen los pájaros, el viento en la hierba y a las mujeres que ya están fuera, junto al fuego."),
        ve("En la meseta el día empieza muy temprano, y a {nombre} la despierta la luz, no el frío. Sale de la tienda descalza, pisando la hierba mojada de rocío, y mira la estepa: hay una manada de caballos lejos, pastando, con la cola al viento."),
        ve("{nombre} sale de la tienda y se estira. Huele a hierba, a humo y a la carne que se seca en los palos. Los perros ya andan alrededor de los palos, mirando la carne, y un niño los espanta con una piedra. En el verano todo el mundo está de buen humor."),
    )

    private val VERANO_TIEMPO = listOf(
        ve("En la meseta el cielo es más grande que en el valle, sin acantilados que lo corten, y se ve venir el tiempo desde lejos. {cielo} {de_estacion}"),
        ve("{cielo} {de_estacion} Aquí arriba, en el campamento, no hay roca que proteja: lo que trae el cielo cae encima de las tiendas."),
        ve("{de_estacion} {cielo} Desde la meseta se ve el valle abajo, hundido, y el río brillando en el fondo como un hilo."),
    )

    private val VERANO_TRABAJO = listOf(
        ve("A esta hora caza en la meseta con los demás: se tienden en la hierba alta contra el viento, con los propulsores listos, y esperan a que la manada de caballos se acerque. {nombre} no respira. Cuando Tor da la señal, las azagayas vuelan todas a la vez.", Hacer.TRABAJO),
        ve("A esta hora recoge en la ladera de la meseta con las otras mujeres: bayas maduras, raíces dulces, hojas que se comen, huevos de los pájaros que anidan en la hierba. Llenan las bolsas de piel y se comen la mitad por el camino. En verano se puede.", Hacer.TRABAJO),
        ve("A esta hora busca ocre en una ladera de la meseta que solo ella conoce, donde la tierra sale roja en una grieta. Lo saca con un palo, lo guarda en una bolsa y lo esconde bajo una piedra, para llevarlo a la cueva en el otoño. No se lo dice a nadie.", Hacer.TRABAJO),

        ve("A esta hora, lejos de la cueva, {nombre} graba en vez de pintar: con una punta de pedernal dibuja un caballo en una placa de piedra plana, o un reno en un trozo de asta. Lo raya despacio, una línea encima de otra, hasta que el animal sale de la piedra.", Hacer.REZAR),
        ve("A esta hora talla un propulsor nuevo para Tor en un asta de reno, sentada a la sombra de la tienda, y en el mango le hace un caballo que vuelve la cabeza. Tarda muchos días. Cuando lo termine, el caballo mirará hacia la azagaya, como si quisiera ver dónde va.", Hacer.REZAR),
        ve("A esta hora mira a los caballos vivos de la meseta, tendida en la hierba lejos del campamento, quieta, para aprendérselos: cómo bajan la cabeza para pastar, cómo mueven las orejas, cómo corren cuando se asustan. Lo que ve en verano lo pinta en invierno.", Hacer.REZAR),

        ve("A esta hora come carne de caballo asada en las brasas, con bayas que recogieron los niños en la ladera, sentada en la hierba delante de la tienda. En verano se come fresco y se come mucho, porque lo que no se come se pudre, y porque el invierno vuelve.", Hacer.COMER),
        ve("A esta hora come de pie, junto al fuego del campamento, un trozo de hígado crudo todavía caliente, que es lo primero que se come de un animal recién cazado y lo que más fuerza da. Le chorrea por la barbilla. Los niños hacen cola para su trozo.", Hacer.COMER),
        ve("A esta hora come pescado del arroyo de la meseta, asado en un palo, y raíces dulces cocidas en un hoyo con piedras calientes. Se come en la hierba, al sol, sin prisa. Los perros esperan las espinas con la cabeza ladeada.", Hacer.COMER),

        ve("A esta hora descansa en la sombra de la tienda, porque en la meseta el sol pega, y raspa una piel con un raspador de pedernal para quitarle la grasa. Los niños juegan a cazar con palos. Uno es el caballo, y siempre gana el caballo.", Hacer.LIBRE),
        ve("A esta hora se baña en el arroyo de la meseta con las otras mujeres, en una poza donde el agua está menos fría, y se frotan con arena para quitarse el humo del invierno. Se ríen, se salpican, se tumban al sol en las piedras. Es el mejor rato del verano.", Hacer.LIBRE),
        ve("A esta hora duerme un rato en la hierba, fuera de la tienda, con la cara tapada con el brazo para que no le dé el sol. Se oyen los grillos, el viento y a los niños lejos. En invierno no se duerme nunca de día; en verano, sí.", Hacer.LIBRE),

        ve("A esta hora está junto al fuego del campamento con los demás, que cuentan la caza del día, cada vez más grande. Oko cuenta una historia vieja de un caballo que hablaba. En la meseta, en verano, la noche tarda mucho en llegar, y se habla hasta que llega.", Hacer.GENTE),
        ve("A esta hora baila con los demás alrededor del fuego del campamento, al son de la flauta de hueso y de unas piedras que se golpean. Bailan imitando a los animales: el caballo que patea, el reno que mira, el oso que se levanta. Los niños bailan de lobos.", Hacer.GENTE),
        ve("A esta hora recibe con los demás a dos cazadores de otro grupo que vienen por la meseta, siguiendo la misma manada. Se sientan al fuego, se dan carne, se cuentan dónde están los animales. Uno trae una concha del mar, y todos la tocan.", Hacer.GENTE),

        ve("A esta hora duerme en la tienda de piel, con el cielo todavía claro aunque ya sea de noche, porque en verano la luz no se va del todo. Oye a los lobos lejos, en la meseta. Las pieles huelen a hierba.", Hacer.DORMIR),
        ve("A esta hora duerme fuera de la tienda, en la hierba, sobre una piel, porque dentro hace calor. Encima tiene todas las estrellas, más que en el valle. Antes de dormirse busca las que tienen forma de animal, como le enseñó su madre.", Hacer.DORMIR),
        ve("A esta hora duerme, y los mosquitos de la meseta no la dejan en paz: se tapa la cara con la piel, se la destapa por el calor, se vuelve a tapar. Alguien echa hierba verde al fuego para que el humo los espante, y el humo la hace toser.", Hacer.DORMIR),
    )

    private val VERANO_PUEBLO = listOf(
        ve("El campamento de verano son cinco tiendas de piel sobre armazones de palos y astas, en círculo, con un fuego en medio, en un llano alto de hierba desde donde se ve venir a las manadas. Abajo, muy abajo, está el valle del río, y el abrigo, vacío."),
        ve("La meseta es un mar de hierba que se mueve con el viento, con flores amarillas y moradas, y arroyos que se esconden en los barrancos. Por ella pasan los caballos, los bisontes y los saigas, y detrás de ellos, el grupo. Nadie es dueño de nada aquí."),
        ve("Alrededor de las tiendas hay palos con tiras de carne secándose al sol y al humo, pieles estiradas en marcos, montones de huesos y un círculo de piedras donde se tallan las puntas. Los perros duermen a la sombra. Así es el campamento cuando hay caza."),
    )

    private val VERANO_ANOCHECER = listOf(
        ve("Al caer la tarde, en la meseta, el sol se va despacio sobre la estepa, enorme y rojo, y las manadas son sombras que se mueven lejos. {nombre} lo mira sentada en una piedra, con un hueso grabado en las manos, y piensa en la cueva, que la espera."),
        ve("Anochece tan tarde en el verano que los niños se duermen antes de que oscurezca, tirados en la hierba, y hay que cargarlos a las tiendas. {nombre} se queda fuera con los grandes, junto al fuego, hasta que sale la primera estrella."),
        ve("Al caer la tarde se cena alrededor del fuego del campamento, con la estepa entera dorada y el cielo sin una nube. Se oyen los caballos relinchar lejos. {nombre} echa un hueso al fuego y piensa que el verano es corto, y que hay que mirarlo bien."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} Aquí el tiempo se mira para saber dónde estarán las manadas, que saben del tiempo más que nadie."),
        v("{nombre} mira el cielo desde la boca del abrigo, por encima de los acantilados del otro lado del río. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Oko dice que antes los fríos eran peores, y que los mamuts bajaban hasta el río. Oko dice muchas cosas."),
        v("{cielo} En {estacion} el valle huele distinto, y {nombre} lo sabría con los ojos cerrados: a nieve, a barro, a hierba o a sangre de reno."),
        v("Desde lo alto del acantilado se ve la estepa hasta donde se acaba la vista, y más allá, dicen, el hielo, que no se acaba. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los viejos leen el cielo en el vuelo de las aves y en el color de la luna, y casi siempre aciertan."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, y la luz es tan fuerte que hay que entornar los ojos.",
            "Hace un día claro, de los que dejan ver la estepa entera desde el acantilado.",
            "No hay una nube, y el aire está tan quieto que se oye el río desde lejos.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris como el pedernal.",
            "Hay nubes bajas sobre los acantilados, y la luz es pobre todo el día.",
            "Está nublado, y el valle parece más pequeño.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el agua gotea de las pieles y de las rocas como una cortina.",
            "Cae una lluvia fría que se mete por las costuras de las pieles.",
            "Llueve, y el río crece y se pone del color de la tierra.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos retumban entre los acantilados y los niños se esconden en las pieles.",
            "El cielo se pone negro sobre la estepa, y el primer rayo cae lejos, en la llanura.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y el valle entero está blanco, con el río negro en medio.",
            "Caen copos grandes y lentos, y las huellas de ayer desaparecen.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla en el valle, y los acantilados de enfrente no se ven.",
            "La niebla sube del río, y los cazadores se llaman con silbidos para no perderse.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del hielo, el que viene del norte y corta la cara.",
            "Hay viento, y las pieles golpean contra los palos como tambores.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es el tiempo del deshielo, cuando el río crece y vuelven las aves.",
            "En el deshielo el valle huele a barro y a verde nuevo.",
            "Es el tiempo en que las manadas suben hacia el norte, y el grupo empieza a pensar en la meseta.",
        ),
        Estacion.VERANO to listOf(
            "Es el tiempo largo de la luz, cuando la noche apenas llega.",
            "En el tiempo de la luz hay bayas en las laderas y mosquitos en el río.",
            "Es el tiempo caliente, el corto, el que hay que aprovechar entero.",
        ),
        Estacion.OTONO to listOf(
            "Es el tiempo de la caza grande, cuando bajan los renos.",
            "En el tiempo de los renos el aire huele a humo de carne que se seca.",
            "Es el tiempo en que se prepara todo para el frío.",
        ),
        Estacion.INVIERNO to listOf(
            "Es el tiempo del frío, el largo, cuando no se sale del abrigo más que lo justo.",
            "En el frío la noche llega pronto y el fuego es lo único que importa.",
            "Es el tiempo de la cueva: de las lámparas, de la pintura y de las historias.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora caza con los demás en el valle, siguiendo las huellas de una manada de renos en la nieve. {nombre} lleva el propulsor y tres azagayas con punta de asta. Se acercan contra el viento, despacio, agachados. Los renos levantan la cabeza. Nadie se mueve.", hacer = Hacer.TRABAJO),
        v("A esta hora pone trampas para liebres y zorros blancos en las laderas, con cuerdas de tendón y piedras, como le enseñó su madre. Las de ayer tienen una liebre. {nombre} la saca, le agradece en voz baja, como se hace, y la cuelga del cinturón.", hacer = Hacer.TRABAJO),
        v("A esta hora pesca en el río con un arpón de asta de dientes curvos, de pie en una piedra, quieta, mirando el agua. Cuando pasa la sombra de un pez, el arpón entra. Falla dos veces y acierta a la tercera, y el pez salta en la orilla, plateado.", hacer = Hacer.TRABAJO),
        v("A esta hora recoge en la ladera lo que hay: raíces que se sacan con un palo, hongos, bayas secas que quedan en los arbustos, huevos de pájaro si es el tiempo. Con la carne sola no basta, le enseñó su madre. {nombre} llena la bolsa de piel despacio.", hacer = Hacer.TRABAJO),
        v("A esta hora muele el ocre en la piedra plana con otra piedra redonda, hasta que el polvo rojo sale fino como la harina. Luego lo mezcla con agua y con grasa en una concha. Para el negro, carbón de hueso, o una piedra negra que se rasca. Así se hace la pintura.", hacer = Hacer.TRABAJO),
        v("A esta hora talla puntas de pedernal sentada junto al fuego, golpeando un núcleo de piedra con un percutor de asta, y las láminas saltan finas y cortantes. Las mejores son para las azagayas; las pequeñas, para cortar piel. Se corta los dedos, como siempre.", hacer = Hacer.TRABAJO),

        v("A esta hora pinta en lo hondo de la cueva, a la luz de una lámpara de grasa que sostiene un niño. La pared es blanca y fría, con bultos que ya tienen forma de animal: {nombre} solo tiene que encontrarlos. Moja el dedo en el ocre y sigue el lomo de un caballo que la roca ya tenía.", hacer = Hacer.REZAR),
        v("A esta hora sopla pintura en la pared de la cueva: se llena la boca de ocre mezclado con agua y lo escupe en una nube fina contra la roca, sobre la mano apoyada. Cuando quita la mano, la mano queda. Es lo más raro que sabe hacer: dejarse en la piedra.", hacer = Hacer.REZAR),
        v("A esta hora está en lo hondo de la cueva, en silencio, con los que bajan a pintar. Nadie habla aquí abajo, salvo para cantar bajito. La luz de la lámpara hace moverse a los animales de las paredes, y {nombre} no sabe si los pintó ella o si estaban.", hacer = Hacer.REZAR),

        v("A esta hora come lo primero del día junto al fuego: carne de reno seca y grasa, y un hueso partido del que se saca el tuétano con un palito. Es lo que da fuerza para el frío. Los niños se pelean por los huesos, y la vieja Neta los reparte.", hacer = Hacer.COMER),
        v("A esta hora come carne asada en las piedras del fuego, que chisporrotea, y la comparte con los que no cazaron, como se hace: aquí nadie come solo. {nombre} le da el mejor trozo al viejo Oko, que ya no tiene dientes y lo chupa contento.", hacer = Hacer.COMER),
        v("A esta hora come pescado del río, asado entero en un palo junto a las brasas, y raíces cocidas en un hoyo con piedras calientes. Se come con los dedos, sentada en las pieles, y se tiran las espinas al fuego, que las quiere.", hacer = Hacer.COMER),

        v("A esta hora descansa bajo el abrigo, al sol si lo hay, cosiendo con aguja de hueso una costura que se abrió en la ropa. El hilo es de tendón, que se moja en la boca para ablandarlo. Las mujeres cosen juntas y hablan de todo, menos de los hombres, que es de lo que más hablan.", hacer = Hacer.LIBRE),
        v("A esta hora juega con los niños a lanzar azagayas pequeñas contra un aro de piel colgado, para que aprendan. Uno acierta, y grita, y los demás le quitan la azagaya. {nombre} se acuerda de cuando ella era la que acertaba.", hacer = Hacer.LIBRE),
        v("A esta hora talla un colgante en un diente de reno, haciéndole un agujero con una punta de pedernal que gira, despacio, hasta que pasa la luz. Lo va a llevar en el collar, con los otros. Cada diente es un reno que dio de comer al grupo.", hacer = Hacer.LIBRE),

        v("A esta hora está junto al fuego con el grupo, que se cuenta lo del día. Oko cuenta una historia de los tiempos en que los animales hablaban, y los niños la escuchan con la boca abierta aunque se la sepan. {nombre} también la escucha.", hacer = Hacer.GENTE),
        v("A esta hora alguien toca una flauta de hueso de ave junto al fuego, una música aguda y triste que rebota en el techo del abrigo. Otros golpean piedras y cantan. {nombre} canta con ellos, una canción que le enseñó su madre, que no dice nada y lo dice todo.", hacer = Hacer.GENTE),
        v("A esta hora se sienta junto al fuego con Tor, que le cuenta la caza de hoy con las manos, haciendo de reno y de cazador a la vez. {nombre} se ríe. Las sombras de las manos de Tor se mueven en el techo como animales.", hacer = Hacer.GENTE),

        v("A esta hora duerme bajo el abrigo, junto al fuego, entre pieles de reno, con los demás apretados alrededor para no perder el calor. Alguien vigila el fuego por turnos. Fuera aúllan los lobos, lejos, y el río suena.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con caballos que corren por la pared de la cueva, como si la roca fuera la estepa. Se despierta un momento, ve las brasas, oye respirar a todos, y se vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, con la piel de oso encima, que es la mejor y le toca a ella esta noche. El que vigila el fuego echa un hueso, y la luz sube y baja por el techo del abrigo, por las manos rojas, por los caballos.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El abrigo es una roca enorme que sale del acantilado como una visera, y debajo caben los fuegos, las pieles y la gente. Pieles colgadas de palos cortan el viento. Abajo está el río, y enfrente, otros acantilados con otros abrigos, donde viven otros grupos. {entorno}"),
        v("{entorno} El valle es estrecho y hondo, de caliza blanca, con el río serpenteando en medio y la estepa arriba, plana, donde pastan las manadas. Las cuevas del acantilado son la casa de los osos, de los búhos y de los animales pintados."),
        v("En el valle viven tres grupos, cada uno en su abrigo, a medio día de camino uno de otro. Se conocen, se casan entre ellos, se pelean y se ayudan, según el año. Las conchas del mar y la piedra buena para las puntas vienen de muy lejos, de mano en mano. {entorno}"),
        v("La cueva de las pinturas está en el acantilado, más allá del abrigo, y se entra por una boca baja que hay que pasar agachado. Adentro el suelo es de barro y las paredes se van estrechando, hasta las galerías hondas, donde no llega nunca el día. {entorno}"),
        v("{entorno} Por el valle pasan las manadas en su tiempo: los renos, los caballos, los bisontes, los saigas. Detrás van los lobos, los leones y los grupos de cazadores. Todos siguen a todos, y los animales lo saben."),
        v("Junto al río hay un vado de piedras por donde cruzan los renos en su tiempo, y allí se espera, cada año, en el mismo sitio, detrás de las mismas rocas. Los huesos de las cazas viejas se amontonan en la orilla, blancos. {entorno}"),
    )

    /** Lo que se ve en el valle según la nieve (arco «nieve»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("nieve", 0.7, frases = listOf(
            "La nieve cubre el valle hasta la rodilla, y el río corre negro entre orillas de hielo.",
            "Todo está blanco: el valle, los acantilados, la estepa, y solo el humo del abrigo sube oscuro.",
        )),
        Entorno("nieve", 0.15, 0.7, frases = listOf(
            "Hay nieve en las laderas y en la estepa, y barro en la orilla del río.",
            "La nieve se va a trozos, y debajo aparece la hierba aplastada y amarilla.",
        )),
        Entorno("nieve", 0.0, 0.15, frases = listOf(
            "El valle está verde y pardo, sin nieve, y el río corre ancho entre piedras.",
            "Hay hierba en el valle, y en la estepa de arriba se ven las manadas desde lejos.",
        )),
    )

    private val GENTE = listOf(
        v("Tor, su compañero, es el mejor con el propulsor de todo el valle, y lo sabe, y lo dice. Tiene una cicatriz en el brazo de un oso que no lo mató y una risa que se oye desde el río. Cuando {nombre} baja a pintar, él la espera en la boca de la cueva."),
        v("El viejo Oko es el que sabe las historias: de dónde vino el fuego, por qué los renos se van, quiénes pintaron las primeras manos. Ya no caza, porque no ve bien, pero sabe dónde estará la manada antes que nadie, y nadie sabe cómo lo sabe."),
        v("Duro es un muchacho del grupo, flaco y callado, que sigue a {nombre} a todas partes y le sostiene la lámpara en la cueva. Quiere aprender a pintar, y todavía no se atreve a pedirlo. {nombre} lo sabe, y lo deja mirar."),
        v("La vieja Neta es la que mejor cose del grupo y la que reparte la comida cuando hay poca, sin que nadie se queje. Tiene los dedos torcidos de tanto coser piel y una paciencia que no se acaba. Los niños le tienen más miedo que a Oko."),
        v("Los niños del grupo son siete, y andan siempre juntos, como una manada: imitan a los cazadores, roban grasa, se caen al río. {nombre} los mira y piensa que en ellos está el grupo de dentro de muchos inviernos, y que algunos pintarán."),
        v("Yemi, su madre, es la que le enseñó a pintar, a soplar el ocre y a encontrar los animales escondidos en la roca. Ya no baja a lo hondo de la cueva, porque le cuesta respirar ahí abajo, pero mira cada tarde las manos de {nombre} para saber qué pintó.", hasta = "muere_yemi"),
        v("{nombre} espera un hijo, y lo lleva bajo la ropa de piel, redondo. Las mujeres del grupo le dan la mejor grasa y le prohíben cargar. Tor le habla a la panza por las noches, junto al fuego, en voz baja, cuando cree que nadie lo oye.", desde = "ama_espera", hasta = "nace_lin"),
        v("Lin, su hija, duerme en un saco de piel de liebre atado a la espalda de su madre, o junto al fuego, entre las pieles. Tiene los ojos de Tor y las manos de {nombre}, pequeñitas, que se agarran a todo. Cuando su madre pinta, Lin mira la luz de la lámpara.", desde = "nace_lin"),
        v("A veces {nombre} piensa en Sel, que pinta bisontes en la cueva de otro grupo, al otro lado de la estepa. Sel pinta mejor el lomo; ella, las patas. Se lo dijeron una a otra en la reunión, y se rieron. Le gustaría volver a verla pintar.", desde = "sel"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del grupo: mirando, desde niña."),
        v("Hay cosas que solo se miran cuando se pierden. {dato_objeto}"),
        v("{dato_objeto} Oko dice que cada cosa tiene dentro el animal del que salió, y que hay que tratarla bien."),
        v("{dato_objeto} Son cosas que se aprenden mirando a la madre, como casi todo."),
        v("El grupo tiene pocas cosas, porque todo lo que no se puede cargar se queda. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("propulsor", "el propulsor", "un propulsor", "El propulsor",
            "El propulsor es un palo de asta con un gancho en la punta donde se apoya la azagaya; alarga el brazo, y con él la azagaya llega el doble de lejos y entra el doble de hondo; el de Tor tiene tallado en el mango un cervatillo que mira atrás."),
        ObjetoCuento("azagaya", "la azagaya", "una azagaya", "La azagaya",
            "La azagaya es una lanza corta con una punta de asta de reno pulida y afilada, pegada al palo con resina y atada con tendón; se tira con el propulsor, y cada cazador conoce las suyas por las rayas que les hizo."),
        ObjetoCuento("aguja", "la aguja", "una aguja", "La aguja",
            "La aguja se saca de un hueso de ave o de un trozo de asta, raspándola contra una piedra, y se le hace un ojo con una punta de pedernal que gira; es lo más pequeño y lo más valioso del grupo, y se guarda en un tubo de hueso."),
        ObjetoCuento("lampara", "la lámpara", "una lámpara", "La lámpara",
            "La lámpara es una piedra con un hueco donde arde la grasa de reno, con una mecha de líquenes o de enebro; da una luz pequeña, sin humo casi, y con ella se baja a lo hondo de la cueva, donde la noche es de verdad."),
        ObjetoCuento("ocre", "el ocre", "un trozo de ocre", "El ocre",
            "El ocre es una tierra roja o amarilla que se saca de ciertas laderas y se muele; sirve para pintar las paredes, la piel, los muertos y los vivos, y quien sabe dónde está el mejor ocre del valle lo guarda como un secreto."),
        ObjetoCuento("carbon", "el carbón", "un trozo de carbón", "El carbón",
            "El carbón sale del fuego, de los palos quemados, y pinta negro con solo tocarlo; con él se hacen los contornos de los animales, rápido, y luego el ocre los rellena, y en la cueva se queda negro durante más tiempo que ninguna persona."),
        ObjetoCuento("piel", "la piel de reno", "una piel de reno", "La piel",
            "La piel de reno es la más caliente que hay, porque cada pelo está hueco y guarda el aire; se raspa, se seca, se ablanda con grasa y con los dientes, y con ella se hace la ropa, la cama y la puerta del abrigo."),
        ObjetoCuento("collar", "el collar", "un collar", "El collar",
            "El collar es de dientes de ciervo, conchas de mar y piedrecitas agujereadas, ensartados en un tendón; las conchas vienen de un mar que nadie del grupo ha visto, pasadas de mano en mano por gente que tampoco lo vio."),
        ObjetoCuento("flauta", "la flauta de hueso", "una flauta de hueso", "La flauta",
            "La flauta se hace con el hueso largo del ala de un ave grande, con agujeros hechos con pedernal; suena aguda y triste, y en la cueva honda el sonido vuelve desde las paredes como si contestaran."),
        ObjetoCuento("pedernal", "el pedernal", "un pedernal", "El pedernal",
            "El pedernal es una piedra dura que, bien golpeada, se rompe en láminas que cortan como nada en el mundo; con él se hacen las puntas, los raspadores, los buriles para grabar, y con dos de ellos, chispas."),
        ObjetoCuento("asta", "el asta de reno", "un asta de reno", "El asta",
            "El asta de reno se cae cada año y vuelve a crecer; se recoge, se ablanda en agua y se talla, y de ella salen las puntas, los arpones, los propulsores, las agujas y los colgantes: el reno lo da todo dos veces."),
        ObjetoCuento("odre", "el odre", "un odre", "El odre",
            "El odre es un estómago de reno limpio y cosido, que se llena de agua en el río y se cuelga junto al fuego, para que no se hiele; el agua sabe a reno, como todo aquí."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el valle como se piensa en el cielo: no se lo elige, se vive debajo. Le da renos, agua, piedra y paredes para pintar. A sus {edad} inviernos, no sabe si hay otro sitio como este, y no tiene prisa por saberlo."),
        v("Piensa a veces en los que pintaron las primeras manos de la cueva, hace tantos inviernos que nadie los recuerda. Pintaron para alguien. Quizás para ella. Y piensa que ella también pinta para alguien que no conocerá, que vendrá con otra lámpara, y mirará."),
        v("{mundo} Ella piensa en cosas más cercanas: en la grasa que queda, en la manada que no vino, en el frío que falta. Las cosas grandes son de los espíritus de la cueva, que las piensan en la oscuridad."),
        v("{mundo} A sus {edad} inviernos, {nombre} ha visto nacer a nueve niños en el grupo y morir a cinco. Los muertos se entierran con ocre y con sus collares, junto al abrigo, para que no se vayan lejos. A veces, en la cueva, les pinta un animal."),
        v("Piensa en los animales que pinta, que no son los que se comen, o no solo: son los que se mueven en la roca cuando la lámpara tiembla. Su madre le enseñó que la pared es una piel, y que detrás están los animales de verdad, esperando. {nombre} les pone el ojo, para que vean."),
        v("{mundo} A veces, en lo hondo de la cueva, con la lámpara a punto de apagarse, {nombre} siente que no está sola, y no lo está: los caballos la miran. Les dice algo en voz baja, que es su manera de rezar, y sube a buscar a los vivos."),
    )

    private val MUNDOS = mapOf(
        "frio" to listOf(
            "En el grupo se habla del frío, que este invierno ha sido largo, y de las manadas, que tardan.",
            "Oko dice que este año los mamuts andan cerca, y que eso es señal de algo.",
            "Se habla de la cueva, de la pared nueva y de lo que se pintará en ella.",
        ),
        "leon" to listOf(
            "Se habla del león que ronda el abrigo, y de que nadie debe salir solo de noche.",
            "En el valle se dice que el grupo del otro abrigo tiene un pintor nuevo.",
        ),
        "reunion" to listOf(
            "Se habla de la reunión de los grupos, que se hace cada muchos inviernos en la cueva grande.",
            "Se dice que este año vendrá gente de muy lejos, con conchas del mar.",
        ),
        "frio_largo" to listOf(
            "Se habla del hambre, que llegó con la nieve que no se iba, y de quién comió menos.",
            "Oko dice que hay que pintar más, para que vuelvan los animales.",
        ),
        "friso" to listOf(
            "Se habla del friso de los caballos, que ya ocupa toda la pared de la galería.",
            "En el valle se dice que los otros grupos quieren venir a ver los caballos de la cueva.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} sale de la cueva con la lámpara apagada, y la luz de fuera la deja ciega un momento. El valle está rojo, y el humo del abrigo sube derecho. Se sienta junto al fuego con las manos todavía rojas de ocre, y alguien le da carne."),
        v("Anochece en el valle, y los acantilados de enfrente se ponen naranjas y luego grises. Se encienden los fuegos de los otros abrigos, lejos, puntitos en la oscuridad. {nombre} los cuenta, como cada noche. Están todos."),
        v("Se cena junto al fuego, sentados en las pieles, con los niños medio dormidos encima. Se habla poco, porque se come. Luego Oko cuenta algo, o alguien canta, y la noche llega sin que nadie la note."),
        v("De noche, bajo el abrigo, el fuego es el centro de todo: alrededor duermen todos, y fuera está el frío, los lobos, la oscuridad. {nombre} echa un hueso al fuego antes de dormirse, y mira cómo la grasa se enciende. Así se aguanta el invierno."),
        v("La última luz del día se queda un momento en lo alto del acantilado, cuando el valle ya está a oscuras. {nombre} la mira desde el fuego. Luego sale la luna, grande, y el río se vuelve de plata. Los niños dicen que en la luna también hay un abrigo."),
        v("Al anochecer vuelven los cazadores, con carne o sin ella, y el grupo se junta alrededor del fuego para saber. Hoy traen un reno. {nombre} ayuda a despellejarlo junto al río, a la luz de las antorchas, y el vapor sube de la carne caliente.", meses = setOf(10, 11)),
        v("Al caer la tarde el frío baja de golpe desde la estepa, y todos se meten bajo el abrigo, entre las pieles. {nombre} se sienta con la espalda contra la roca, que todavía guarda un poco del sol, y cierra los ojos un momento.", meses = setOf(11, 12, 1, 2, 3)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "frio_hondo" to unDia(
            Parrafo.MANANA to "Hoy hace el frío más hondo del invierno: el río se ha helado de orilla a orilla, el aire corta como pedernal y el aliento se congela en la piel que tapa la boca. Nadie sale del abrigo más que lo justo. El fuego no se deja bajar.",
            Parrafo.PUEBLO to "En el valle no se mueve nada: ni manadas, ni aves, ni humo de los otros abrigos, que también están cerrados. Solo el viento, que barre la nieve de la estepa y la tira por el acantilado. Los lobos aúllan de hambre, cerca.",
            Parrafo.ANOCHECER to "Esta noche duermen todos juntos, apretados, en el centro del abrigo, alrededor del fuego grande, los niños en medio. {nombre} siente en la espalda el frío de la noche y en la cara el calor del fuego. Así se pasa el frío hondo: pegados.",
        ),
        "aves" to unDia(
            Parrafo.MANANA to "Hoy volvieron las aves que se fueron: grandes bandadas de gansos que pasan altas sobre el valle, gritando, hacia el norte. Los niños salen del abrigo a mirarlas con la boca abierta. Oko dice que el invierno se está rompiendo, y esta vez tiene razón.",
            Parrafo.PUEBLO to "En el río, donde el hielo empieza a romperse, se paran los patos a descansar, y en las laderas se oyen los primeros pájaros pequeños. El sol calienta un poco al mediodía, bajo el abrigo, y las mujeres sacan las pieles a orearlas.",
            Parrafo.PENSAMIENTO to "{nombre} mira las aves que vuelven y piensa adónde van y de dónde vienen, de qué abrigos, de qué tierras sin hielo. Le gustaría saber volar para verlo. Luego piensa que para eso pinta: para ir donde no puede ir.",
        ),
        "deshielo" to unDia(
            Parrafo.MANANA to "Hoy el río se rompió: con un ruido como de trueno el hielo se partió en placas que el agua se lleva, chocando, y el río creció de golpe, gris de barro. El grupo sale a mirarlo desde la orilla alta, porque es una de las cosas más grandes que pasan en el año.",
            Parrafo.PUEBLO to "La nieve de las laderas se deshace en mil arroyos que bajan al río cantando, y el valle huele a barro, a tierra mojada y a algo verde que todavía no se ve. El vado de los renos está bajo el agua. Hasta que baje, no se cruza.",
            Parrafo.ANOCHECER to "Esta noche se oye el río más fuerte que nunca, desde el abrigo, como si hablara. Los niños dicen que tiene hambre. Oko dice que el río se está despertando del sueño del invierno, como todos. {nombre} se duerme con ese ruido, que es un ruido de vida.",
        ),
        "saigas" to unDia(
            Parrafo.MANANA to "Hoy pasaron los saigas por la llanura del valle: cientos, con sus narices grandes y sus cuernos claros, corriendo en grupo, rápidos como el viento. Los vieron venir desde el acantilado, una nube de polvo en la estepa, y los cazadores salieron corriendo.",
            Parrafo.TRABAJO to "A esta hora espera a los saigas en un barranco por donde tienen que pasar, con los otros, escondida detrás de una piedra, con el propulsor. Los saigas pasan tan rápido que no da tiempo a apuntar: se tira al grupo, y algo cae.",
            Parrafo.ANOCHECER to "Esta noche se asa carne de saiga en el fuego, que es más tierna que la de reno, y se come hasta reventar, porque después del invierno todo el mundo tiene hambre de carne fresca. Los niños tienen grasa hasta las orejas. {nombre} también.",
        ),
        "sube_verano" to unDia(
            Parrafo.MANANA to "Hoy el grupo sube a la meseta, al campamento de verano, detrás de las manadas. Se cargan las pieles de las tiendas, los palos, las herramientas, los niños pequeños, el fuego en un cuerno con brasas. El abrigo se queda vacío, con los fuegos apagados.",
            Parrafo.PUEBLO to "El camino sube por un barranco del acantilado hasta la estepa, donde la hierba ya está verde y alta. Desde arriba se ve el valle entero, el río, los abrigos, y más allá, la llanura sin fin. Allí arriba se montan las tiendas, en el mismo sitio que el año pasado.",
            Parrafo.PENSAMIENTO to "{nombre} mira por última vez la boca de la cueva antes de subir, y piensa en los caballos que deja en la oscuridad. Allí estarán cuando vuelva, esperando. Les dice en voz baja que no se muevan, y sube.",
        ),
        "salmones" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezaron a subir los salmones por el río que baja de la meseta: grandes, plateados, saltando contra la corriente para llegar a donde nacieron. El grupo baja al río con los arpones y las cestas, porque durante unos días hay más pescado del que se puede comer.",
                Parrafo.TRABAJO to "A esta hora pesca salmones en un rápido del río, de pie en el agua hasta las rodillas, con el arpón. Los peces saltan delante de ella, tantos que casi se pueden coger con la mano. {nombre} los tira a la orilla, uno tras otro, y los niños los persiguen.",
                Parrafo.PENSAMIENTO to "{nombre} mira a los salmones que suben y piensa que vuelven a donde nacieron para morir allí, todos, como si lo supieran. Le parece lo más valiente que ha visto. Le gustaría saber dónde nació ella, exactamente, en qué abrigo, junto a qué fuego.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora abre salmones en la orilla del río con una lámina de pedernal, y los cuelga a secar sobre el humo en palos, para el invierno. Las manos le huelen a pescado durante días.",
                "A esta hora pesca otra vez en el río, que sigue lleno de salmones cansados. Tor se cae al agua y sale con uno en la mano, y todos se ríen.",
                "A esta hora junta la grasa de los salmones en un odre, para las lámparas de la cueva. Con esta grasa, piensa, pintará en el invierno.",
            )),
        ),
        "baja_otono" to unDia(
            Parrafo.MANANA to "Hoy el grupo baja de la meseta al abrigo del río, antes de que lleguen los fríos. Se desarman las tiendas, se cargan las pieles y la carne seca, y se baja por el barranco en fila, con los niños y los perros, contentos y tristes a la vez.",
            Parrafo.PUEBLO to "El abrigo está como lo dejaron, con los fuegos fríos y las manos rojas en la pared, y huele a piedra y a búho. Se encienden los fuegos de nuevo, se cuelgan las pieles que cortan el viento, y en un momento el abrigo vuelve a ser casa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la cueva, que está ahí al lado, y en que mañana podrá bajar a ver si los caballos siguen donde los dejó. Tiene las manos impacientes. En el verano grabó animales en piedra, pero no es lo mismo. Lo de la cueva se queda.",
        ),
        "renos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó la caza grande: los renos bajan de la estepa en manadas enormes hacia el sur, y cruzan el río por el vado, como todos los años, en el mismo sitio. El grupo los espera escondido detrás de las rocas desde antes del amanecer, en silencio.",
                Parrafo.TRABAJO to "A esta hora está detrás de una roca del vado, con el propulsor y cinco azagayas, mientras los renos entran en el río, empujándose, con el agua al pecho. Cuando Oko da la señal con un grito, todos se levantan a la vez y tiran. El río se vuelve rojo.",
                Parrafo.PENSAMIENTO to "{nombre} mira el reno que mató, en la orilla, todavía caliente, y le agradece en voz baja, como se hace, porque el reno dio su carne, su piel, sus huesos, sus tendones y sus astas. Le pondrá el ojo en la cueva, piensa, para que siga viendo.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora despelleja renos en la orilla del río con las otras mujeres, rápido, antes de que la carne se enfríe. La piel se raspa, la carne se corta en tiras y el tuétano se come ahí mismo, caliente.",
                "A esta hora seca carne de reno en tiras, colgada sobre el humo de los fuegos, para el invierno. El abrigo entero huele a humo y a carne, y los lobos se acercan de noche.",
                "A esta hora espera otra manada en el vado. Hoy pasan menos renos, y más cansados. Se caza lo que se puede, y se deja pasar al resto, para que vuelvan el año que viene.",
                "A esta hora raspa pieles de reno al sol, bajo el abrigo, con un raspador de pedernal. Cada piel es una ropa, una cama o una pared para el invierno.",
            )),
        ),
        "coser" to unDia(
            Parrafo.MANANA to "Hoy las mujeres del grupo empiezan a coser la ropa del invierno con las pieles de los renos de la caza: chaquetas con capucha, pantalones, botas con el pelo hacia dentro. Se sientan juntas bajo el abrigo, cada una con su aguja de hueso y su tendón.",
            Parrafo.TRABAJO to "A esta hora cose unas botas nuevas para Tor, con la piel de las patas del reno, que es la más dura, y con el pelo hacia dentro para el frío. Hace agujeros con un punzón de hueso y pasa el tendón con la aguja. Es lento. Es lo que hay que hacer.",
            Parrafo.GENTE to "La vieja Neta cose más rápido que todas, y les corrige las costuras a las jóvenes sin mirarlas. Las mujeres hablan mientras cosen, de los hombres, de los niños, de quién se juntará con quién en la reunión. Se ríen mucho. Coser es la mejor fiesta del frío.",
        ),
        "primera_nieve" to unDia(
            Parrafo.MANANA to "Hoy cayó la primera nevada que se queda: el valle amaneció blanco, el río negro en medio, y los acantilados con su capa de nieve encima como una piel. Los niños salen a pisarla, a comerla, a tirársela. Los grandes miran el cielo y cuentan la leña.",
            Parrafo.PUEBLO to "En la nieve se ven todas las huellas: del zorro, de la liebre, del lobo, del reno que se quedó atrás. Los cazadores las leen como se lee un cielo, y saben quién pasó y cuándo. El valle entero es un dibujo hecho por los animales.",
            Parrafo.PENSAMIENTO to "{nombre} mira las huellas en la nieve y piensa que los animales también pintan, sin saberlo, con las patas. Ella pinta con los dedos, en la roca, que dura más. Pero las huellas son más verdaderas, piensa, porque el animal estaba ahí.",
        ),
        "noche_larga" to unDia(
            Parrafo.MANANA to "Hoy es el día más corto y la noche más larga del año: el sol apenas sale por encima del acantilado y ya se va. Oko dice que esta noche el sol está más lejos que nunca, y que hay que llamarlo para que vuelva. Se prepara un fuego grande.",
            Parrafo.PUEBLO to "Al caer la tarde se enciende el fuego más grande del invierno delante del abrigo, con huesos, grasa y toda la leña que se guardó, y el grupo entero se pone alrededor. Desde los otros abrigos del valle se ven otros fuegos grandes. Todos llaman al mismo sol.",
            Parrafo.ANOCHECER to "Esta noche no se duerme: se canta, se toca la flauta de hueso, se baila alrededor del fuego y se cuentan las historias más viejas, hasta que el cielo empieza a clarear. Cuando sale el sol, todos gritan. {nombre} grita también. Ha vuelto.",
        ),
        "primer_caballo" to unDia(
            Parrafo.MANANA to "Hoy {nombre} bajó sola a lo hondo de la cueva, a la galería donde la pared es lisa y blanca, y pintó su primer caballo grande, sin su madre al lado. Lo hizo con carbón, en una sola línea, del hocico a la cola, sin levantar la mano.",
            Parrafo.TRABAJO to "A esta hora rellena el caballo con ocre amarillo, soplado, y le pinta la crin negra con un pincel de pelo de caballo. La roca tenía un bulto donde va la panza, y el caballo parece respirar. Duro sostiene la lámpara y no dice nada.",
            Parrafo.PENSAMIENTO to "{nombre} mira su caballo a la luz de la lámpara, que tiembla, y el caballo se mueve. Piensa que este es el primero de muchos, que ocuparán toda la pared, en fila, corriendo hacia la oscuridad. No sabe cuánto tardará. Sabe que lo hará.",
        ),
        "mano" to unDia(
            Parrafo.MANANA to "Hoy su madre bajó con ella a la cueva, despacio, apoyada en su hombro, para enseñarle lo último que le quedaba por enseñarle: soplar el ocre de manera que la mano quede entera, con los dedos abiertos, sin que se corra la pintura.",
            Parrafo.TRABAJO to "A esta hora {nombre} apoya la mano en la roca, al lado de la mano de su madre, que es vieja y está pintada hace muchos inviernos, y sopla. Cuando quita la mano, las dos manos están juntas, la vieja y la nueva, como si se tocaran.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su madre le dio todo lo que sabe en una sola tarde, como si tuviera prisa. No le pregunta por qué. Mira las dos manos en la pared y piensa que, cuando ellas ya no estén, las manos seguirán ahí, tocándose.",
        ),
        "mamuts" to unDia(
            Parrafo.MANANA to "Hoy pasó a lo lejos, por la estepa, una manada de mamuts: cinco, grandes como colinas, con su pelo largo y oscuro y sus colmillos curvos, despacio, sin prisa. El grupo entero subió al acantilado a verlos. Nadie había visto mamuts en muchos inviernos.",
            Parrafo.PUEBLO to "Oko dice que cuando él era niño los mamuts bajaban hasta el río a beber, y que su padre mató uno con otros veinte cazadores, y que se comió durante toda una luna. Ahora casi no vienen. Se van al norte, dice, con el frío, que también se va.",
            Parrafo.PENSAMIENTO to "{nombre} mira a los mamuts irse por la estepa y piensa en pintarlos, para que se queden. Nunca ha pintado uno. Se los aprende de memoria, el lomo, la cabeza, la trompa, para no olvidarlos. Esta tarde, en la cueva, los busca en la roca.",
        ),
        "ama_espera" to unDia(
            Parrafo.MANANA to "Hoy {nombre} supo que espera un hijo: hace dos lunas que no sangra, y esta mañana la carne asada le dio asco. Se lo dijo primero a su madre, en el campamento de la meseta, y su madre le tocó la panza y sonrió.",
            Parrafo.GENTE to "Esta noche se lo dice a Tor, junto al fuego, en voz baja. Tor se queda callado mucho rato, mirando el fuego, y luego se levanta y se va a la oscuridad. Vuelve con la mejor piel de su caza, la de un zorro blanco, y se la pone sobre los hombros.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el niño que lleva, que nacerá cuando vuelvan las aves, y en que tendrá que bajar a la cueva con él a la espalda. Piensa en las manos de la pared. Algún día, piensa, este niño también soplará una.",
        ),
        "tiro_tor" to unDia(
            Parrafo.MANANA to "Hoy, en la caza de los renos, Tor mató uno con el propulsor desde más lejos que nadie en el valle: desde el otro lado del vado, a una distancia que todos miden con los ojos y nadie se cree. El reno cayó en el agua. Los demás se quedaron mirando a Tor.",
            Parrafo.GENTE to "Tor no dice nada, porque no hace falta: lo dicen los demás, toda la noche, junto al fuego, cada vez desde más lejos. Oko dice que solo una vez vio un tiro así, de su padre. Tor le da a Oko el mejor trozo del reno. {nombre} se ríe de lo serio que se pone.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el reno de Tor, que cayó en el agua de lejos, y en pintarlo en la cueva, con la azagaya volando hacia él. Luego piensa que no: en la cueva no se pintan cazadores. Solo animales. Los cazadores ya se pintan solos, contando.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "pintora_cueva", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + VERANO_MANANA,
            Parrafo.TIEMPO to TIEMPO + VERANO_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + VERANO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + VERANO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + listOf(
                v("Desde que el friso llegó a la mitad de la pared, {nombre} piensa en él hasta en la caza: qué caballo falta, hacia dónde mira, de qué color. Su madre ya no está para verlo. Lo pinta para ella igual, y a veces le habla mientras pinta.", desde = "friso_mitad"),
                v("El friso de los caballos está terminado, y {nombre} no sabe qué hacer con las manos. Baja a verlo cada tarde, con la lámpara, y se sienta delante. Los caballos corren hacia la oscuridad, todos, como los soñó. Ahora le toca a Lin.", desde = "friso_fin"),
            ),
            Parrafo.ANOCHECER to ANOCHECER + VERANO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
