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
 * El cuento de Tomás, grumete de un balandro pirata en las Bahamas y el Caribe, de 1718 a 1722. Sigue la gramática y
 * el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día va por guardias: trabaja tres veces, come dos y duerme dos. Los textos del trabajo, de la comida y del sueño
 *   valen a cualquiera de sus horas (no dicen «al alba» ni «de noche»).
 * - Cada marzo el balandro se carena en un cayo (estado «cayo»): dos semanas en tierra, con su mañana, su trabajo, su
 *   pueblo y su anochecer. En la temporada 1 pasa casi un mes en Nassau (estado «nassau»).
 * - El pueblo es el balandro y lee el arco «casco» (lo sucio que va el casco).
 * - No repite lo que ya dice «Su vida»: cómo llegó al barco, la campana y las guardias, el coy, la galleta y el grog.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG5.kt`):
 * - Kofi le enseña hasta que cae con la fiebre (`kofi_fiebre`, temporada 3) y muere en `muere_kofi`.
 * - El capitán es Grady hasta `capitan_nuevo` (temporada 2); después, Hollis.
 * - El velero se queda en Nassau el día de `votacion` (temporada 1): los textos de todos los días no lo nombran.
 * - Las fiestas que vuelven cada año no nombran a Kofi, a Grady ni a Hollis.
 */
internal object PiezasTomas {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta en su coy, colgado entre otros cuarenta en el entrepuente, tan juntos que cuando el barco se balancea se balancean todos a la vez, como racimos. Huele a sudor, a brea y a agua de sentina. Arriba suena la campana. Es su guardia."),
        v("A {nombre} lo despierta el contramaestre con una patada al coy y una palabra que no se dice delante de una madre. Sube a cubierta descalzo. El mar está gris y el cielo se pone rosa por el este, detrás de las velas. Hace fresco, por una vez."),
        v("Lo primero que hace {nombre} al subir a cubierta es mirar el horizonte, todo alrededor, como le enseñaron: una vela lejana puede ser una presa o la horca. Hoy no hay nada. Solo agua, en todas direcciones, y el sol saliendo de ella como si la quemara."),
        v("{nombre} amanece en cubierta, donde se quedó dormido sobre un rollo de cabo porque abajo hacía demasiado calor. Tiene la cara marcada por la cuerda y la camisa mojada de rocío. Un pelícano lo mira desde la borda. Ninguno de los dos se mueve."),
        v("A {nombre} lo despiertan los gritos del cocinero, que ya tiene encendido el fogón en su caja de arena y reparte agua caliente con azúcar a los de la guardia. {nombre} se pone en la fila con su taza de lata. El balandro corre con el viento de popa."),
        v("{nombre} se despierta con el estómago vacío y la boca salada. Por la escotilla entra un cuadrado de luz que se mueve con el barco, de un lado a otro, sobre los hombres que todavía duermen. Alguien ronca. Alguien reza en inglés. Sube."),
        v("A {nombre} lo despierta el agua de lluvia que gotea por las tablas de cubierta justo encima de su coy, como cada mañana de esta estación. Sube a destapar los barriles y a ponerlos bajo las velas: agua dulce del cielo, que no hay que robarle a nadie.", meses = setOf(6, 7, 8, 9, 10)),
    )

    /** La carena de cada año, en un cayo (estado «cayo»). */
    private fun cy(texto: String, hacer: Hacer? = null) = v(texto, estado = "cayo", hacer = hacer)

    private val CAYO_MANANA = listOf(
        cy("{nombre} se despierta en la playa del cayo, en la arena todavía tibia, bajo una vela vieja tendida entre dos palmeras. A unos pasos, el balandro está tumbado de costado en la orilla como una ballena varada, con el casco al aire, verde de algas. Hoy hay que rascar."),
        cy("A {nombre} lo despiertan los cangrejos, que de noche le pasan por encima, y los mosquitos, que no duermen nunca. Se levanta, se rasca y se mete en el agua hasta la cintura. El mar del cayo es tan claro que se ven la arena del fondo y los peces que pasan."),
        cy("{nombre} amanece en el cayo con la boca seca y el humo de la brea en la nariz. Los hombres duermen esparcidos por la playa, bajo las palmeras, con los mosquetes al lado por si viene un barco del rey. El vigía, en lo alto de la duna, saluda con la mano."),
    )

    private val CAYO_TRABAJO = listOf(
        cy("A esta hora rasca el casco del balandro, tumbado en la arena, con un raspador de hierro: algas, conchas, percebes y los gusanos que se comen la madera por dentro. Le cae todo en la cara. El sol le quema la espalda. El casco huele a mar podrido.", Hacer.TRABAJO),
        cy("A esta hora quema el casco con antorchas de hojas de palma, para matar los gusanos de la madera, y luego lo unta de brea y sebo calientes con una brocha de trapo. La brea le salpica los brazos. Abel dice que así el barco correrá como un delfín.", Hacer.TRABAJO),
        cy("A esta hora tira de los aparejos atados al palo para tumbar el balandro del otro lado, porque este ya está limpio. Cuarenta hombres tiran a la vez, cantando para ir juntos. El barco cruje, se inclina despacio y se acuesta en la arena como un animal cansado.", Hacer.TRABAJO),
        cy("A esta hora come en la playa, a la sombra, pescado asado en la hoguera, cocos y agua de un pozo que cavaron en la arena, que sabe un poco a sal. Después de semanas de galleta, el pescado fresco parece un banquete. Se chupa los dedos.", Hacer.COMER),
        cy("A esta hora come carne de tortuga asada en su concha y huevos de tortuga, que en el cayo se cogen con la mano. El cocinero los cuece en la arena caliente, debajo de la hoguera. {nombre} come hasta que no puede más, por si mañana no hay.", Hacer.COMER),
        cy("A esta hora duerme en la arena, a la sombra de unas palmeras, sin coy, sin campana, sin balanceo. Le cuesta dormirse en tierra: la tierra no se mueve, y eso lo marea. Al final se duerme con la oreja pegada a la arena, oyendo el mar.", Hacer.DORMIR),
        cy("A esta hora duerme bajo la vela tendida, con la camisa sobre la cara contra los mosquitos. Un cangrejo le camina por el pie. Lo aparta sin despertarse del todo. Al lado ronca Abel, el contramaestre, con la boca abierta y una mosca dentro.", Hacer.DORMIR),
        cy("A esta hora está junto a la hoguera de la playa con los demás, bebiendo ron y cantando canciones de marineros de cuatro países. Los que saben bailar bailan en la arena. Los que no, también. Nadie vigila más que el vigía, y el vigía también canta.", Hacer.GENTE),
        cy("A esta hora juega a los dados en la arena con los de su rancho, apostando lo que no tiene, que es su parte del próximo botín. Pierde. El que gana le perdona la deuda porque es un grumete. {nombre} jura que la próxima vez no le perdonará nadie.", Hacer.GENTE),
    )

    private val CAYO_PUEBLO = listOf(
        cy("El cayo es una lengua de arena blanca con veinte palmeras, una laguna verde por dentro y arrecifes por fuera, que guardan la entrada mejor que un cañón. No vive nadie. No tiene nombre en ningún mapa, o tiene uno que solo sabe el piloto."),
        cy("En la playa del cayo hay barriles, velas tendidas, cabos enrollados, los cañones bajados a tierra para aligerar el balandro y una hoguera donde hierve la brea en un caldero. Parece un mercado. Es un barco desarmado."),
        cy("Desde lo alto de la duna del cayo se ve el mar de todos los azules, el arrecife donde rompen las olas y, a lo lejos, nada. Es lo que se busca: un lugar donde nadie mira. Si aparece una vela del rey, con el barco tumbado, no hay escape."),
    )

    private val CAYO_ANOCHECER = listOf(
        cy("Al caer la tarde dejan el casco y se meten todos en el agua, a quitarse la brea con arena. {nombre} flota boca arriba en la laguna, mirando cómo el cielo se pone naranja y luego morado. Es lo más parecido a estar en casa que recuerda."),
        cy("Anochece en el cayo y se encienden las hogueras de la playa, aunque el capitán dice que se ven de lejos. Las tortugas salen del mar a poner sus huevos. Los hombres las miran sin tocarlas: mañana. Esta noche se las deja en paz."),
        cy("De noche el cayo es negro y el mar brilla con lucecitas verdes donde se mueve el agua. {nombre} mete la mano y le salen chispas de entre los dedos. Un viejo le dijo una vez que son las almas de los ahogados. Él cree que son peces pequeños. No está seguro."),
    )

    private val CAYO_TIEMPO = listOf(
        cy("En el cayo el tiempo importa más que en el mar: con el barco tumbado, una tormenta lo deshace. {cielo} {de_estacion}"),
        cy("{cielo} {de_estacion} El piloto sube a la duna cada rato a mirar el cielo, como si pudiera mandarle algo."),
    )

    /** El puerto de Nassau, en la temporada 1 (estado «nassau»). */
    private fun na(texto: String, hacer: Hacer? = null) = v(texto, estado = "nassau", hacer = hacer)

    private val NASSAU_MANANA = listOf(
        na("{nombre} se despierta en el balandro, anclado en el puerto de Nassau entre otros veinte barcos piratas, y lo primero que oye son las risas y los gritos de la playa, donde nadie ha dormido. Huele a ron, a pescado podrido y a humo. El sol ya quema."),
        na("A {nombre} lo despierta Kofi, que vuelve de tierra con la camisa rota y una piña en cada mano. Le da una. Dice que en Nassau la noche no termina, solo se cansa. {nombre} se come la piña sentado en la borda, mirando el puerto."),
        na("{nombre} amanece en tierra, en la playa de Nassau, debajo de una barca puesta boca abajo, sin acordarse bien de cómo llegó. Le duele la cabeza. Alrededor hay hombres dormidos en la arena, perros, botellas y un loro que dice algo en francés."),
    )

    private val NASSAU_TRABAJO = listOf(
        na("A esta hora carga barriles de agua dulce en la lancha y los lleva remando al balandro, ida y vuelta, ida y vuelta. En el puerto el barco tiene que estar listo para salir en cualquier momento: así lo quiere el capitán Grady, que no se fía ni del agua.", Hacer.TRABAJO),
        na("A esta hora ayuda a vender en la playa lo que se tomó en las presas: azúcar, ron, telas, un reloj, un loro. Los mercaderes de Nassau pagan poco y en plata, y los piratas no saben regatear. {nombre} sí, porque su madre vendía pescado. Se lo cuenta a Kofi.", Hacer.TRABAJO),
        na("A esta hora cose velas en la cubierta del balandro, a la sombra del toldo, con aguja y palma de cuero, junto al velero. En el puerto las velas no trabajan y se pueden coser con calma. Desde aquí se oye la música de la playa.", Hacer.TRABAJO),
        na("A esta hora come en la playa de Nassau lo que vende una mujer en una olla: tortuga guisada con pimientos, pan de yuca y un vaso de ron. Le cuesta una moneda que Kofi le prestó. En tierra todo cuesta. En el mar todo es de todos.", Hacer.COMER),
        na("A esta hora come fruta del mercado de Nassau, la que nunca hay en el barco: mangos, naranjas, una cosa morada que mancha los dedos. Come tanta que le duele la tripa. Kofi se ríe y le dice que el mar se cura con fruta y la fruta se cura con mar.", Hacer.COMER),
        na("A esta hora duerme en el balandro, en cubierta, porque abajo el calor del puerto no deja respirar. La bahía está quieta y el barco casi no se mueve. Se oyen otros barcos, guitarras, peleas y el ruido del agua contra el casco.", Hacer.DORMIR),
        na("A esta hora duerme en una hamaca que alguien colgó entre dos palmeras de la playa y que nadie le reclama. En tierra se duerme raro: sin campana, sin guardia, sin nadie que te patee el coy. Se despierta dos veces creyendo que el barco se hunde.", Hacer.DORMIR),
        na("A esta hora está en una taberna de la playa de Nassau, que es una vela vieja sobre cuatro palos, con Kofi y media tripulación. Se bebe, se canta, se juega y alguien siempre se pelea. Hay mujeres que ríen alto. {nombre} se queda en un rincón, mirando.", Hacer.GENTE),
        na("A esta hora escucha a los hombres de otros barcos contar sus presas, cada vez más grandes cuanto más ron beben. Uno dice que tomó un galeón con oro. Otro, que lo tomó dos veces. Kofi dice que el que más habla es el que menos ha tomado.", Hacer.GENTE),
        na("A esta hora escucha a los hombres discutir el perdón del rey en la arena, a gritos: unos dicen que hay que aceptarlo y volver a casa, otros que el rey perdona hoy y ahorca mañana. El capitán Grady escucha sin decir nada, fumando su pipa.", Hacer.GENTE),
    )

    private val NASSAU_PUEBLO = listOf(
        na("Nassau es un puerto de arena y palmeras con un fuerte medio caído, unas cuantas casas de madera y cientos de chozas, velas y tiendas en la playa. En la bahía hay barcos de todas las banderas, la mayoría robados. Aquí no manda nadie, o mandan todos."),
        na("En la playa de Nassau se vende todo lo que se robó en el mar: barriles de azúcar, rollos de tela, cajas de té, mosquetes, campanas de barco. Los mercaderes vienen de las colonias a comprarlo barato. Todos saben de dónde sale. Nadie lo dice."),
        na("El puerto de Nassau tiene dos entradas, a cada lado de la isla larga que lo cierra, y los piratas lo eligieron por eso: un barco grande no entra por la pequeña, y uno pequeño siempre puede salir por la otra. En la playa, los vigías miran el mar todo el día."),
    )

    private val NASSAU_ANOCHECER = listOf(
        na("Anochece en Nassau y la playa se llena de hogueras, de música y de gritos. {nombre} mira desde la borda del balandro: parece una fiesta que no termina nunca. Kofi le dice que no baje solo de noche. {nombre} no baja. Mira."),
        na("Al caer la tarde, en el puerto de Nassau, los barcos encienden faroles y el agua de la bahía se llena de luces que se mueven. Alguien dispara un cañonazo de broma, y todos los perros de la isla ladran a la vez."),
        na("De noche, en la bahía de Nassau, el balandro se mece apenas, y {nombre} duerme mal por la quietud. Echa de menos el ruido del mar abierto. Desde la playa llega una canción triste en una lengua que no conoce. La canta una mujer."),
    )

    private val NASSAU_TIEMPO = listOf(
        na("{cielo} {de_estacion} En el puerto el tiempo no importa: se mira por costumbre."),
        na("Desde la bahía de Nassau se ve el mar de fuera, más oscuro, más allá de la isla que cierra el puerto. {cielo} {de_estacion}"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el mar el tiempo es lo único que importa: decide si se come, si se duerme y si se vuelve."),
        v("{nombre} mira el cielo desde la cofa, como le enseñaron, para saber qué viene. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El piloto dice que el mar del Caribe es manso como un gato, y que como un gato saca las uñas sin avisar."),
        v("{cielo} {nombre} ya sabe leer el color del agua: azul oscuro es hondo, verde es bajo, blanco es arrecife. {de_estacion}"),
        v("Desde la cubierta, el mar se ve hasta donde se acaba, que es una raya. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los marineros viejos dicen que el tiempo lo manda el cielo y lo cobra el mar."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio de horizonte a horizonte, y el sol cae a plomo sobre la cubierta, que quema los pies.",
            "Hace un día claro, con el mar de un azul que no existe en ningún otro sitio.",
            "No hay una nube, y el viento empuja el balandro sin esfuerzo, como si soplara alguien.",
        ),
        Clima.NUBES to listOf(
            "Hay nubes blancas, altas y redondas, que pasan sobre el balandro como otro mar más lento.",
            "El cielo está cubierto y el mar se pone gris, del color del plomo.",
            "Pasan nubes bajas, y su sombra corre sobre el agua más rápido que el barco.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia tibia y gruesa que se bebe con la boca abierta y se guarda en barriles.",
            "Cae un chubasco de golpe, que deja la cubierta brillando y se va igual de rápido.",
            "Llueve, y el mar y el cielo son del mismo gris, sin raya entre los dos.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: el mar se levanta en montañas y el balandro sube y baja como una hoja.",
            "El cielo se pone negro, el viento aúlla en la jarcia y los rayos caen en el agua, cerca.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento fuerte y parejo, y el balandro corre escorado, con la borda casi en el agua.",
            "Hay mucho viento, y el mar está lleno de espuma blanca, como ovejas.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.LLUVIAS to listOf(
            "Es el tiempo de las lluvias, cuando el calor pesa y cada tarde puede traer un chubasco.",
            "En la estación de las lluvias el aire huele a tierra mojada aunque no haya tierra a la vista.",
            "Es la estación de las lluvias y, más adelante, de los huracanes, cuando los barcos prudentes buscan puerto.",
        ),
        Estacion.SECAS to listOf(
            "Es el tiempo seco, el de los alisios, que soplan del nordeste día y noche, frescos y constantes.",
            "En la estación seca el mar está más tranquilo y pasan más mercantes: es la buena temporada de la caza.",
            "Es el tiempo seco, cuando no llueve en semanas y el agua de los barriles se cuenta.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora baldea la cubierta con los de su guardia: cubos de agua de mar, arena y una piedra blanda que se frota de rodillas, como quien reza. La cubierta tiene que estar limpia aunque sea de piratas. Es lo que dice Abel, el contramaestre.", hacer = Hacer.TRABAJO),
        v("A esta hora sube a la jarcia a aferrar la gavia, descalzo, por los obenques, hasta la verga, donde se tumba sobre la tela y la recoge a puñados mientras el palo se balancea sobre el mar. No mira abajo. Mirar abajo es lo único prohibido.", hacer = Hacer.TRABAJO),
        v("A esta hora remienda velas en cubierta, sentado con las piernas cruzadas, con una aguja grande como un dedo y una palma de cuero para empujarla. La lona es dura y huele a sal. Abel le enseñó la puntada que no se deshace. Todavía le sale torcida.", hacer = Hacer.TRABAJO),
        v("A esta hora ceba los cañones con los artilleros, en un ejercicio que manda el capitán: limpiar el ánima con la escobilla, meter la pólvora, el taco y la bala, empujar con el atacador, apuntar. No se dispara: la pólvora es cara. Se hace como si.", hacer = Hacer.TRABAJO),
        v("A esta hora achica la sentina con la bomba, abajo del todo, en la oscuridad que apesta a agua podrida, a ratas y a algo peor. El balandro hace agua, como todos los barcos de madera. Bombear es el castigo de los grumetes y de los que llegan tarde a su guardia.", hacer = Hacer.TRABAJO),
        v("A esta hora está de vigía en la cofa, en lo alto del palo, con el catalejo del piloto, mirando el horizonte por si aparece una vela. Si la ve primero, le darán la mejor pistola de la presa. Hasta ahora solo ha visto nubes, pájaros y una ballena.", hacer = Hacer.TRABAJO),
        v("A esta hora trenza cabos viejos para hacer cuerda nueva, sentado a la sombra de la vela mayor, con un pasador de hueso. Trenzar es lo que hace un marinero cuando no hace otra cosa. Abel dice que un barco sin cuerda es leña que flota.", hacer = Hacer.TRABAJO),

        v("A esta hora come en su rancho, un círculo de seis hombres sentados en cubierta alrededor de una fuente: galleta mojada en el caldo, carne salada y frijoles. Hay que golpear la galleta contra la madera para que salgan los gusanos. Los que no salen, se comen.", hacer = Hacer.COMER),
        v("A esta hora come lo que se sacó de la última presa: queso holandés, jamón, aceitunas, vino en vez de ron. Mientras dure, en el balandro se come como en la mesa de un gobernador. Después volverá la galleta. {nombre} come despacio, para acordarse.", hacer = Hacer.COMER),
        v("A esta hora come pescado fresco que alguien sacó por la borda, frito en el fogón, y bebe su ración de grog con un agua que ya sabe a barril. Comer en el mar es comer con hambre, que es la mejor salsa, dice el cocinero.", hacer = Hacer.COMER),

        v("A esta hora duerme en su coy, en el entrepuente, entre los demás, que roncan, sudan y hablan dormidos en cinco lenguas. El barco cruje todo el tiempo. Al principio no podía dormir con tanto ruido. Ahora no podría dormir sin él.", hacer = Hacer.DORMIR),
        v("A esta hora duerme como duermen los marineros: a pedazos, de golpe, en cualquier sitio, con la cabeza sobre un brazo. Le toca dormir ahora porque luego le toca la guardia. Cuando suene su turno, se levantará antes de abrir los ojos.", hacer = Hacer.DORMIR),
        v("A esta hora duerme en cubierta, sobre las velas plegadas, porque en el entrepuente hace un calor de horno. El barco se balancea y él con el barco. Sueña con tierra: con la playa de su madre, con un perro que tenía. Se despierta con agua de mar en la cara.", hacer = Hacer.DORMIR),

        v("A esta hora está en cubierta con los de su guardia, oyendo a un violinista francés que toca canciones de su tierra. Los hombres cantan, desafinan, aplauden. Uno baila con una escoba. Las reglas del barco prohíben jugar por dinero. Se juega igual.", hacer = Hacer.GENTE),
        v("A esta hora escucha historias en la proa: de barcos fantasma, de islas que se mueven, de un capitán que enterró su oro y mató al que cavó el hoyo. {nombre} sabe que la mitad son mentira. No sabe qué mitad. Por eso escucha todas.", hacer = Hacer.GENTE),
        v("A esta hora juega a los dados en la cubierta con Will, el otro grumete, a la luz de un farol, apostando galletas. Pierde tres. Abel pasa, los mira y no dice nada: en el barco los dados están prohibidos después de que se apaga la luz, y todavía no se apaga.", hacer = Hacer.GENTE),
    )

    private val PUEBLO = listOf(
        v("{entorno} El balandro es pequeño y rápido, de un solo palo, con diez cañones y unos setenta hombres, que son demasiados para él: pero un barco pirata necesita gente para el abordaje, no para navegar. Se llama Fortuna. Antes se llamaba de otra manera."),
        v("En el balandro viven hombres de todas partes: ingleses, irlandeses, franceses, holandeses, africanos libres y escapados, un sueco, dos indios del Darién. Lo único que tienen en común es el barco. {entorno}"),
        v("{entorno} En la cubierta del balandro no hay sitio para nada: cañones, barriles, cabos, gallinas en jaulas, una cabra, los hombres. Debajo, el entrepuente, donde se duerme, tan bajo que solo {nombre} anda derecho. Más abajo, la bodega y la sentina."),
        v("Las reglas del balandro están escritas en un papel que firmaron todos al entrar, cada uno con su nombre o con una cruz, como {nombre}: cómo se reparte el botín, qué se paga por un brazo perdido, qué pasa al que roba a un compañero. {entorno}"),
        v("El balandro navega entre las islas: las Bahamas, que son cientos y casi todas vacías, las costas de Cuba y de la Española, los pasos por donde van los mercantes. {entorno} El piloto conoce cada bajo de arena, cada corriente y cada escondite."),
        v("{entorno} Desde lo alto del palo se ve todo el balandro, pequeño como un juguete: la cubierta llena de hombres, la estela blanca detrás, el mar alrededor, sin fin. Desde allí arriba {nombre} piensa que ningún barco es grande."),
    )

    /** Lo sucio que va el casco (arco «casco»): se limpia en cada carena. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("casco", 0.0, 0.3, frases = listOf(
            "El casco está limpio desde la carena, y el balandro corre como un pez: ningún mercante se le escapa.",
            "Con el casco limpio el balandro casi no hace agua, y la bomba descansa.",
        )),
        Entorno("casco", 0.3, 0.7, frases = listOf(
            "Al casco le empiezan a crecer algas y conchas, y el balandro ya no corre como en primavera.",
            "El casco se va ensuciando: se nota en la estela, más pesada, y en la bomba, que trabaja más.",
        )),
        Entorno("casco", 0.7, 1.0, frases = listOf(
            "El casco va sucio de algas, conchas y gusanos, y el balandro anda lento y pesado: hace falta carenar.",
            "Con el casco tan sucio el balandro hace agua por todas partes, y la bomba no para nunca.",
        )),
    )

    private val GENTE = listOf(
        v("Abel, el contramaestre, es un inglés de Bristol con la cara picada de viruelas y una voz que se oye en lo alto del palo con tormenta. Manda el trabajo, reparte los castigos y no perdona nada. Pero le enseñó a {nombre} el nudo que salva la vida. Eso no se olvida."),
        v("Joris, el cocinero, es un holandés con una pata de palo que perdió en una presa, y por eso cobró de más, como dicen las reglas. Cocina en una caja de arena en cubierta, maldice en tres lenguas y le guarda a {nombre} las cortezas del queso."),
        v("El piloto es un escocés viejo y callado que sabe leer las cartas de marear, las estrellas y el cielo. Es el único que sabe dónde está el barco. Por eso es el único al que nadie le levanta la mano. Duerme con sus cartas debajo de la almohada."),
        v("Su madre está en Las Palmas, en las Canarias, vendiendo pescado en el muelle, y no sabe dónde está {nombre}. Lo último que sabe es que se embarcó de paje en un mercante inglés. {nombre} se pregunta si lo da por muerto. Se pregunta si sería mejor."),
        v("En el balandro todo se vota: adónde ir, a quién atacar, quién manda. Cada hombre, un voto, también {nombre}, que es un grumete pero vota como los demás. El capitán manda en la batalla. Fuera de la batalla, manda la tripulación."),
        v("Will es el otro grumete, un muchacho de Boston que tiene un año menos que {nombre} y se cree más listo. Se pelean por todo y lo comparten todo: la galleta, los secretos, el miedo. Es lo más parecido a un hermano que tiene."),
        v("Kofi es un marinero de la Costa de Oro, libre, que se unió a los piratas cuando tomaron el barco de esclavos donde trabajaba. Es el mejor gaviero del balandro. Le enseña a {nombre} los nudos, la jarcia, a no tener miedo arriba. No le enseña de dónde son sus cicatrices.", hasta = "kofi_fiebre"),
        v("Kofi ya no está: murió de la fiebre amarilla y lo echaron al mar cosido en su coy, con una bala de cañón a los pies. {nombre} todavía sube a la cofa como él le enseñó, sin agarrarse, y a veces le habla allá arriba, en voz baja.", desde = "muere_kofi"),
        v("El capitán Ned Grady es irlandés, pelirrojo, valiente en el abordaje y prudente en todo lo demás, y por eso lo eligieron. Se le puede discutir: en un barco pirata el capitán no es un rey. Trata a {nombre} como a un sobrino que no le cae del todo bien.", hasta = "capitan_nuevo"),
        v("El capitán Hollis, al que eligieron cuando echaron a Grady, es un inglés de Jamaica que habla poco y pega rápido. La tripulación lo quiere porque tiene suerte. {nombre} no sabe si lo quiere. Sabe que ahora hay más presas y más muertos.", desde = "capitan_nuevo"),
    )

    private val OBJETO = listOf(
        v("Hoy, en la guardia, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió a fuerza de golpes, como se aprende todo en un barco."),
        v("En un barco cada cosa tiene su sitio y su nombre, y el que no lo sabe come menos. {dato_objeto}"),
        v("{dato_objeto} En el mar se dice que lo que no está atado se lo lleva el agua, y lo que está atado, también, pero más tarde."),
        v("{dato_objeto} Son cosas que un grumete aprende antes que su propio nombre en inglés."),
        v("Un marinero tiene poco: lo que cabe en un cofre, y muchas veces ni eso. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("cuchillo", "el cuchillo", "un cuchillo", "El cuchillo",
            "El cuchillo del marinero se lleva al cinto, en una funda de cuero, y sirve para todo: cortar un cabo enredado, comer, remendar, abrir cocos; en un abordaje se lleva entre los dientes, para tener las manos libres al subir."),
        ObjetoCuento("coy", "el coy", "un coy", "El coy",
            "El coy es un pedazo de lona colgado de dos ganchos en el entrepuente, que se balancea con el barco y no deja caer a nadie aunque haya tormenta; los marineros lo aprendieron de los indios de las islas, que le decían hamaca; cuando uno muere, lo cosen dentro."),
        ObjetoCuento("galleta", "la galleta", "una galleta de barco", "La galleta",
            "La galleta de barco es pan cocido dos veces, sin sal, duro como una tabla, que dura meses en los barriles; se moja en el caldo o en el grog para poder morderla; con el tiempo le salen gusanos, y los marineros comen a oscuras para no verlos."),
        ObjetoCuento("ron", "el ron", "un trago de ron", "El ron",
            "El ron se hace en las islas con la melaza del azúcar, y es la bebida de los piratas porque se roba en cada presa; se bebe rebajado con agua para que dure, a veces con limón y azúcar; cada uno tiene su ración, y el que se emborracha de guardia, paga."),
        ObjetoCuento("cabo", "el cabo", "un cabo", "El cabo",
            "En el barco no se dice cuerda, se dice cabo, y cada cabo tiene su nombre: la driza sube la vela, la escota la tensa, el obenque sujeta el palo; un marinero los conoce todos a oscuras, por el tacto, porque en la tormenta no se ve nada."),
        ObjetoCuento("campana", "la campana", "la campana de a bordo", "La campana",
            "La campana del barco suena cada media hora: un toque a la primera media hora de la guardia, dos a la hora, y así hasta ocho, cuando la guardia termina y vuelve a empezar; es el reloj de todos, y también el aviso de fuego y de enemigo."),
        ObjetoCuento("catalejo", "el catalejo", "un catalejo", "El catalejo",
            "El catalejo es un tubo de latón con lentes que hace ver cerca lo que está lejos; con él se distingue la bandera de un barco antes de que el barco lo vea a uno; es del piloto, y es lo más caro del balandro después de los cañones."),
        ObjetoCuento("mosquete", "el mosquete", "un mosquete", "El mosquete",
            "El mosquete es largo, pesado y lento: hay que meter por la boca la pólvora, la bala y el taco, empujar con la baqueta, cebar y disparar; en un abordaje se dispara una vez, y luego se usa al revés, como un garrote."),
        ObjetoCuento("polvora", "la pólvora", "un barril de pólvora", "La pólvora",
            "La pólvora se guarda en barriles en el pañol más hondo del barco, donde no entra ninguna luz y solo entra el artillero, descalzo; una chispa allí y el barco vuela entero; mojada no sirve, y el mar lo moja todo."),
        ObjetoCuento("bandera", "la bandera", "una bandera", "La bandera",
            "La bandera negra no se iza siempre: el balandro navega con la bandera que convenga, inglesa, holandesa o española, y solo al acercarse a la presa sube la negra; si la presa se rinde al verla, no se le hace daño; si no, se iza la roja, que quiere decir sin cuartel."),
        ObjetoCuento("dados", "los dados", "unos dados", "Los dados",
            "Los dados son de hueso, pequeños y gastados, y van de un bolsillo a otro según quién gane; en muchos barcos piratas el juego por dinero está prohibido en las reglas, porque acaba en peleas; en todos se juega igual."),
        ObjetoCuento("brujula", "la brújula", "una brújula", "La brújula",
            "La brújula es una aguja imantada que flota en una caja de madera junto al timón y señala siempre el norte; con ella, el reloj de arena y la corredera, el piloto sabe dónde está el barco aunque no se vea tierra; los que no saben leerla dependen de él."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas como piensa en el tiempo: pasan por encima, y uno sigue trabajando. A sus {edad} años sabe que el mar no pregunta de quién eres. Pregunta si sabes nadar."),
        v("Piensa a veces en el mercante donde llegó de paje, y en el día en que lo tomaron los piratas: los gritos, el humo, el capitán de rodillas. Le preguntaron si quería quedarse. Dijo que sí sin pensarlo. Todavía no sabe si lo pensó después."),
        v("{mundo} {nombre} no entiende de reyes ni de perdones. Entiende de partes: cuántas le tocan, cuánto vale cada una, qué se compra con ella en tierra. Nunca ha comprado nada en tierra. Tiene una bolsa de monedas escondida en el coy."),
        v("{mundo} A veces, en la cofa, mirando el horizonte, {nombre} piensa en la horca. Ha visto piratas colgados en jaulas a la entrada de un puerto, negros de sol, con los pájaros encima. Piensa que él es un grumete, y que a los grumetes no los cuelgan. Casi seguro."),
        v("Piensa en su madre, que le decía que el mar se lleva a los hombres y no los devuelve. Tenía razón: se llevó a su padre. Pero a él lo trajo aquí, donde se come todos los días, aunque sea galleta con gusanos. Su madre no come todos los días."),
        v("{mundo} A sus {edad} años, {nombre} ha visto más mar que tierra, más hombres muertos que mujeres vivas y más oro que pan. Le parece que el mundo está al revés. Luego piensa que a lo mejor el mundo es así, y que en tierra lo esconden."),
    )

    private val MUNDOS = mapOf(
        "indulto" to listOf(
            "Se habla del perdón del rey a los piratas, y de si hay que creerle o no.",
            "Se habla de los barcos de guerra del rey, que cada año son más en estas aguas.",
            "Se dice que el azúcar de Jamaica vale más que nunca, y que los barcos que lo llevan van cargados.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde el sol se mete en el mar por la proa, enorme y rojo, y por un momento todo el balandro es de color de fuego: las velas, los hombres, el agua. Luego, de golpe, es de noche. En el trópico no hay tarde: hay día y hay noche."),
        v("Anochece, y en el balandro se encienden los faroles: uno en la popa, uno en la bitácora, ninguno más, para que no los vean de lejos. Los hombres se juntan en la proa a fumar. Alguien canta bajito. {nombre} mira las estrellas, que el piloto llama por su nombre."),
        v("De noche el mar es negro, el cielo es negro, y entre los dos el balandro corre a ciegas, con el ruido del agua en el casco y el crujido de los palos. {nombre} se agarra a la borda. No tiene miedo. Lo tuvo el primer año, y se le gastó."),
        v("Al caer la tarde suena la campana del cambio de guardia, y los de un turno bajan y los del otro suben, cruzándose en la escala sin mirarse, como hormigas. {nombre} sube. Tiene cuatro horas de noche por delante, y la noche es larga en el mar."),
        v("La última luz del día se queda en lo alto del palo, en la gavia, cuando la cubierta ya está a oscuras. {nombre} la ve subir y apagarse. Luego sale la luna por la popa y pinta un camino de plata en el agua, hasta el balandro."),
        v("Anochece, y los peces voladores saltan junto al balandro y alguno cae en la cubierta, aleteando. {nombre} los recoge para el cocinero. Mañana habrá pescado frito para los de su rancho. Los demás tendrán que buscarse sus peces."),
        v("Al anochecer, en el tiempo de los huracanes, el piloto mira el cielo del este con cara de cuidado, y si hay un cerco alrededor de la luna, manda recoger vela. Nadie discute con el piloto en esta estación. {nombre} tampoco.", meses = setOf(8, 9, 10)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy empieza el año, y en el balandro se celebra como se celebra todo: con ron doble para cada hombre, por orden del capitán y por voto de la tripulación. El artillero dispara un cañonazo al aire al salir el sol. Los pájaros de la costa se levantan todos a la vez.",
            Parrafo.GENTE to "Todo el día se bebe y se canta en cubierta, y se recuerda a los que el año pasado estaban y ya no: los ahogados, los colgados, los que se quedaron en algún puerto. Se dice su nombre y se echa un trago al mar. El mar se bebe mucho ron en año nuevo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza y no sabe qué pedirle. En tierra se pide salud y pan. En el mar se pide viento, presas y no ver nunca una horca. Pide las tres cosas, por si acaso, y luego pide volver a ver a su madre, que no es cosa del mar.",
        ),
        "carena" to unDia(
            Parrafo.MANANA to "Hoy llegaron al cayo donde se carena el balandro cada año, una isla sin nombre con una playa honda y escondida. Se descarga todo: los cañones, la pólvora, los barriles, las velas. El barco tiene que quedar vacío y ligero, como una cáscara.",
            Parrafo.TRABAJO to "A esta hora ayuda a tumbar el balandro en la arena con la marea alta: se atan aparejos desde el palo a las palmeras de la playa y se tira, todos a la vez, cantando, hasta que el barco se acuesta de lado y enseña el casco, verde y lleno de conchas.",
            Parrafo.PENSAMIENTO to "{nombre} mira el balandro tumbado en la arena y piensa que así, de costado, parece un animal enfermo, y que un barco fuera del agua no vale nada. Si ahora llega un barco del rey, no hay escape. Por eso hay un vigía en la duna. {nombre} lo mira cada rato.",
        ),
        "botadura" to unDia(
            Parrafo.MANANA to "Hoy el balandro vuelve al agua, con el casco limpio, quemado, untado de brea y sebo, negro y brillante como una bota nueva. Con la marea alta se aflojan los aparejos y el barco se endereza solo, despacio, y flota. Todos gritan.",
            Parrafo.TRABAJO to "A esta hora carga otra vez todo lo que se bajó: los cañones con poleas, los barriles rodando por la arena, las velas, y la pólvora la última. El balandro se va hundiendo en el agua con cada carga, hasta la línea de siempre. Ya es otra vez un barco.",
            Parrafo.ANOCHECER to "Al caer la tarde el balandro sale del cayo con la marea y corre como no corría desde hacía meses: el casco limpio no tiene nada que lo frene. El piloto sonríe, que no es costumbre. {nombre} mira el cayo hacerse pequeño detrás, hasta que se lo come el mar.",
        ),
        "tortugas" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy el balandro fondeó junto a una playa donde salen las tortugas a poner sus huevos, como cada año en esta luna. Se las caza de noche: se espera a que salgan del agua, se les da la vuelta, y ahí se quedan, boca arriba, moviendo las patas, hasta que se las llevan.",
                Parrafo.TRABAJO to "A esta hora carga tortugas en la lancha, boca arriba, pesadas como un hombre, y las sube al balandro con una polea. En la bodega se las guarda vivas, echándoles agua de mar, durante semanas: carne fresca que no se pudre. Es la despensa del pirata.",
                Parrafo.PENSAMIENTO to "{nombre} mira las tortugas boca arriba en la bodega, con sus ojos viejos y tristes, y piensa que lloran. El cocinero le dice que es la sal, que les sale por los ojos. {nombre} le cree a medias. Luego come tortuga guisada y no piensa más.",
            )),
            sigue = mapOf(
                Parrafo.TRABAJO to listOf(
                    "A esta hora busca huevos de tortuga en la arena, cavando con las manos donde la tortuga dejó su rastro: cien huevos blandos y redondos en cada nido. Se comen crudos, cocidos o fritos. Se guardan en arena húmeda. Hoy {nombre} encontró cuatro nidos.",
                    "A esta hora les echa agua de mar a las tortugas de la bodega con un cubo, una por una, para que no se sequen. Le resoplan. Una le muerde el borde de la camisa. Hay que tenerlas vivas hasta que se coman: un barco lleva su carne caminando.",
                    "A esta hora ayuda al cocinero a matar y limpiar una tortuga para el rancho: la concha se abre con un hacha, la carne se corta, la grasa verde se guarda para el guiso. Huele a mar y a sangre. Los demás esperan con el cuenco en la mano.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "De noche, en la playa de las tortugas, se las ve salir del mar una tras otra a la luz de la luna, arrastrándose despacio hacia la arena seca. Los hombres esperan agachados en la oscuridad, sin hablar, como antes de un abordaje.",
                    "Al anochecer, en la playa de las tortugas, se oye cavar a las madres en la arena, despacio, con las patas de atrás. {nombre} se acerca a una sin hacer ruido. La tortuga pone sus huevos sin mirarlo, con los ojos mojados. {nombre} la deja ir.",
                ),
            ),
        ),
        "huracanes" to unDia(
            Parrafo.MANANA to "Hoy el piloto dijo en voz alta lo que todos saben: empieza el tiempo de los huracanes, que dura hasta el otoño. Desde hoy se navega cerca de los escondites, se mira el cielo del este cada hora y se duerme con la ropa puesta. Nadie discute.",
            Parrafo.TIEMPO to "El piloto le enseña a {nombre} las señales: la marejada que llega de lejos sin viento, el cielo amarillo al atardecer, el cerco de la luna, los pájaros que vuelan hacia tierra. Cuando se ven juntas, dice, hay que rezar y recoger vela, en ese orden.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los barcos que se tragó el mar en otros años, enteros, sin dejar ni una tabla. Un huracán no distingue piratas de mercantes ni de barcos del rey. Es lo único justo del mar, dice Abel. A {nombre} no le parece un consuelo.",
        ),
        "alisios" to unDia(
            Parrafo.MANANA to "Hoy volvieron los alisios: un viento del nordeste fresco y constante que no para ni de día ni de noche, y que se queda hasta la primavera. El piloto lo olió antes de que llegara. Las velas se llenan solas. Se acabó el tiempo de los huracanes.",
            Parrafo.TRABAJO to "A esta hora ajusta las velas al viento nuevo con los demás, tirando de las escotas, y el balandro se inclina y empieza a correr con una alegría que no tenía. Con los alisios se puede ir a cualquier parte. Y a cualquier parte van también los mercantes.",
            Parrafo.ANOCHECER to "Al caer la tarde el viento del nordeste sigue soplando, fresco, y por primera vez en meses se duerme bien en el entrepuente. {nombre} se tiende en su coy y oye las velas tensas y el agua que corre. Es la temporada de caza. Mañana empieza.",
        ),
        "san_tomas" to unDia(
            Parrafo.MANANA to "Hoy es Santo Tomás, el día de su santo, y {nombre} es el único a bordo que lo sabe. Su madre lo celebraba con un dulce de gofio y miel, y lo llevaba a misa en la iglesia del puerto. Aquí no hay misa ni gofio. Se lo dice a sí mismo, en voz baja: felicidades.",
            Parrafo.GENTE to "No se lo cuenta a nadie. Los hombres del balandro no celebran santos: la mitad son protestantes, otros no son nada y los demás los olvidaron. Si lo cuenta, se reirán. Así que esta tarde, en el rancho, se guarda para él la mejor galleta, sin que nadie sepa por qué.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en santo Tomás, el que no creyó hasta que tocó las heridas. Su madre decía que por eso le puso ese nombre: porque él también era terco. Piensa que tenía razón, y que no cree nada hasta que lo toca: ni el perdón del rey, ni el oro, ni el cielo.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, y en el balandro no se trabaja más que lo justo para que no se hunda. Se abren los barriles de la última presa: vino, ron, carne de cerdo, pasas. El cocinero hace un pudin de galleta, sebo y azúcar que todos dicen que es el mejor del Caribe.",
            Parrafo.GENTE to "Todo el día se bebe en cubierta, y por la tarde ya nadie se tiene en pie: unos cantan, otros lloran, otros se pelean y se abrazan. Alguien canta un villancico inglés y otros lo siguen. {nombre} canta el de su madre, en español, y nadie lo entiende, y todos aplauden.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la Navidad de su casa, en la misa de la noche en la iglesia del puerto, en las velas encendidas, en su madre con el pañuelo de los domingos. Aquí solo está el mar oscuro alrededor y setenta hombres borrachos. Le parece que Dios no mira hacia acá esta noche.",
        ),

        // ---------- Los de la temporada 1 ----------
        "presa_ron" to unDia(
            Parrafo.MANANA to "Hoy, al amanecer, el vigía vio una vela: una balandra mercante de Jamaica, pesada y lenta. El capitán Grady mandó izar la bandera inglesa, acercarse como amigos y, a tiro de pistola, subir la negra. La balandra se rindió sin disparar un tiro.",
            Parrafo.TRABAJO to "A esta hora pasa barriles de ron y sacos de azúcar de la presa al balandro, por una pasarela de tablas que se mueve con las olas, con Kofi delante y Will detrás. Los de la presa miran sentados en su cubierta, callados. A nadie se le hizo daño: se rindieron.",
            Parrafo.PENSAMIENTO to "{nombre} mira al capitán de la balandra, un hombre gordo y asustado que se quitó el sombrero ante Grady, y piensa en el capitán de su mercante, el día que lo tomaron a él. Igual de asustado. Piensa que el miedo de los demás es lo que hace ricos a los piratas.",
        ),
        "cofa" to unDia(
            Parrafo.MANANA to "Hoy Kofi le dijo a {nombre} que ya era hora: subir a la cofa más alta, la del tope, sin agarrarse más que con los pies y una mano, como los gavieros de verdad. El mar estaba picado y el palo se balanceaba como un péndulo. Kofi subió primero, silbando.",
            Parrafo.TRABAJO to "A esta hora sube por los obenques detrás de Kofi, despacio, con los dedos de los pies agarrados a los flechastes y el corazón en la boca. Arriba, el palo dibuja círculos en el cielo. Kofi le dice que no mire abajo, que mire el horizonte. Lo mira. Llega.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, en lo alto de la cofa, con el viento en la cara y el balandro pequeñito allá abajo, que nunca ha estado tan arriba en su vida. Kofi le da una palmada y le dice que ahora es un marinero. {nombre} no dice nada, porque le tiemblan las piernas.",
        ),
        "charleston" to unDia(
            Parrafo.MANANA to "Hoy un balandro amigo que venía del norte les dio la noticia a gritos, de borda a borda: Barbanegra está frente a Charles Town con cuatro barcos y cientos de hombres, y tiene cerrado el puerto. Dicen que no deja entrar ni salir a nadie.",
            Parrafo.GENTE to "Esta noche en el balandro no se habla de otra cosa: de Barbanegra, que se pone mechas encendidas bajo el sombrero para dar miedo, de su barco grande, de lo que pedirá a la ciudad. El capitán Grady dice que ese hombre va a hacer que el rey se enfade con todos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Barbanegra como en un cuento: un gigante con humo en la barba que tiene una ciudad entera de rodillas. Le gustaría verlo una vez, de lejos. Kofi le dice que los que lo ven de cerca no lo cuentan. Kofi dice muchas cosas para asustarlo.",
        ),
        "llega_nassau" to unDia(
            Parrafo.MANANA to "Hoy el balandro entró en el puerto de Nassau, la isla de los piratas, con la bandera negra arriba y un cañonazo de saludo. Le contestaron tres barcos desde la bahía. En la playa la gente gritaba y agitaba los sombreros. {nombre} nunca había visto tantos piratas juntos.",
            Parrafo.PUEBLO to "En la bahía de Nassau hay más de veinte barcos anclados, todos piratas o robados por piratas, y en la playa cientos de chozas, tiendas de lona, tabernas, mercaderes y mujeres. Arriba, un fuerte medio caído sin bandera. Nadie manda aquí. Eso es lo que lo hace famoso.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que Nassau es como el mar, pero en tierra: no tiene dueño. Piensa que podría quedarse aquí para siempre, si quisiera, y nadie lo buscaría. Luego ve a un hombre tirado en la arena, muerto o borracho, y piensa que a lo mejor no.",
        ),
        "rogers" to unDia(
            Parrafo.MANANA to "Hoy por la tarde aparecieron en la boca del puerto de Nassau unos barcos de guerra del rey, con un gobernador a bordo y el perdón para todos los piratas que lo acepten. Cerraron una de las dos entradas. En la playa se hizo un silencio que {nombre} no había oído nunca en Nassau.",
            Parrafo.GENTE to "Esta noche la tripulación se reúne en cubierta, con antorchas, a discutir: unos quieren aceptar el perdón, volver a casa, tener tierra; otros dicen que el rey perdona hoy y ahorca mañana. El capitán Grady deja hablar a todos. Mañana se vota. Kofi dice que él no tiene casa adonde volver.",
            Parrafo.ANOCHECER to "Ya de noche, en la bahía, un barco pirata se enciende de pronto y va solo, ardiendo, hacia los barcos del rey, que tienen que apartarse. Detrás del fuego, otro barco se escapa por la otra boca del puerto. Dicen que es el de Vane. En la playa la gente aplaude.",
        ),
        "votacion" to unDia(
            Parrafo.MANANA to "Hoy la tripulación votó en la cubierta del balandro, a mano alzada, uno por uno: aceptar el perdón del rey o seguir. Ganó seguir, por muchos votos. Seis hombres bajaron a tierra con sus cosas a aceptar el perdón. Los demás los despidieron sin rencor y sin abrazos.",
            Parrafo.GENTE to "{nombre} votó lo que votó Kofi, sin pensarlo mucho. Los seis que se quedan en Nassau dan la mano a todos, uno por uno. Uno de ellos, el velero, le regala a {nombre} su aguja grande y su palma de cuero. Le dice que aprenda bien la puntada, que algún día le servirá en tierra.",
            Parrafo.ANOCHECER to "Ya de noche, sin luces, el balandro sale de Nassau por la boca pequeña, despacio, con la marea, mientras la playa duerme. Nadie habla. Cuando pasan junto al último barco del rey, anclado y oscuro, todos contienen la respiración. Nadie los ve. Al amanecer, mar abierto.",
        ),
        "anzuelo" to unDia(
            Parrafo.MANANA to "Hoy, en una calma, Kofi dobló un clavo con dos piedras hasta hacer un anzuelo, le ató un sedal de hilo de vela y se lo dio a {nombre}. Le enseñó a ponerle un trozo de carne salada, a echarlo lejos de la borda y a esperar. A esperar, sobre todo.",
            Parrafo.TRABAJO to "A esta hora pesca desde la borda de popa, con el sedal enrollado en el dedo, sintiendo cada tirón del mar. Saca un pez pequeño y plateado, y luego otro, y luego uno grande de colores que se le escapa en el aire. Kofi dice que ese era el rey de los peces, y que volverá.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que Kofi sabe de todo: de la jarcia, de los nudos, de los peces, de las estrellas, de dónde viene la lluvia. No sabe leer, como él. Piensa que hay dos clases de saber, el de los libros y el del mar, y que Kofi tiene el segundo entero.",
        ),
        "primer_abordaje" to unDia(
            Parrafo.MANANA to "Hoy alcanzaron un bergantín mercante que no quiso rendirse: disparó dos cañonazos y luego trató de huir. El capitán Grady mandó acercarse por la popa, donde no tiene cañones, y preparar los garfios. Y le dijo a {nombre} que hoy subía con los demás.",
            Parrafo.TRABAJO to "A esta hora salta a la borda del bergantín con el cuchillo entre los dientes, detrás de Kofi, entre el humo, los gritos y los tiros de pistola. Alguien cae a su lado. No sabe quién. Corre por la cubierta ajena sin saber adónde. Luego todo se acaba: el bergantín se rinde.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, ya de vuelta en el balandro, temblando, que no hizo nada en el abordaje: no peleó, no hirió a nadie, solo corrió y gritó. Kofi le dice que eso es lo que hace todo el mundo la primera vez, y que los que dicen otra cosa mienten. Le da un trago de ron. Le sabe a gloria.",
        ),
        "barbanegra" to unDia(
            Parrafo.MANANA to "Hoy un pescador de las islas les contó la noticia, que ya corre por todo el mar: los del rey mataron a Barbanegra en una ensenada del norte, peleando en la cubierta de su barco. Dicen que le cortaron la cabeza y la colgaron del bauprés.",
            Parrafo.GENTE to "Esta noche en el balandro se bebe por Barbanegra, aunque nadie lo conoció. El capitán Grady dice que con él se muere una época, y que los del rey ya no se van a parar. Abel dice que se muere un hombre y nada más. Kofi no dice nada. Mira el mar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la cabeza de Barbanegra colgada del barco del rey, con la barba llena de humo apagado. Si a él, que tenía barcos y cientos de hombres, lo mataron, ¿qué no harán con un balandro pequeño? Por primera vez, el perdón del rey no le parece tan malo.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "grumete", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + CAYO_MANANA + NASSAU_MANANA,
            Parrafo.TIEMPO to TIEMPO + CAYO_TIEMPO + NASSAU_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + CAYO_TRABAJO + NASSAU_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + CAYO_PUEBLO + NASSAU_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + CAYO_ANOCHECER + NASSAU_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
