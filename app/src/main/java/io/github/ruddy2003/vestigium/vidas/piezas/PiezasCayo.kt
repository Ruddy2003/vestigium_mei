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
 * El cuento de Cayo, legionario de la Legión VII Claudia en Viminacium, sobre el Danubio, hacia el 110. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Vive en un barracón del campamento, en el cuarto de su tienda. Fuera de la muralla está el poblado (las tabernas,
 *   los mercaderes, las mujeres de los soldados) y el río, con las barcas de la flota.
 * - El párrafo del pueblo lee los arcos «guarnicion» (el campamento medio vacío desde la temporada 4) y «muralla».
 * - No repite lo que ya dice «Su vida»: los veinticinco años de servicio, los ocho de la tienda que muelen su grano,
 *   las tres marchas al mes, los caminos y los puentes, la posca, que no puede casarse y que vino de Hispania.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG3.kt`):
 * - Sabina aparece desde `sabina` (temporada 1); espera desde `espera_sabina` (3); Gaia nace en `nace_gaia` (3).
 * - Atilio está en su tienda hasta `despedida` (4); desde entonces está en oriente. Durio llega en `recluta` (2).
 * - Es inmune (sin guardias) desde `inmune` (3) y jefe de su tienda desde `jefe_tienda` (4).
 * - El estado «camino» (temporada 2): seis semanas en el desfiladero.
 * - Las fiestas que vuelven cada año no nombran a Sabina, a Gaia, a Atilio ni a Durio.
 */
internal object PiezasCayo {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("Antes del alba suena la trompeta del cambio de guardia y {nombre} abre los ojos en el cuarto de su tienda, donde duermen ocho en cuatro literas de madera. Huele a cuero, a sudor y a la lucerna apagada. Alguien ya está moliendo el grano en el molino de mano del rincón."),
        v("{nombre} se levanta a oscuras, se ata las botas de clavos, que suenan en el suelo de tablas como una herradura, y sale al callejón entre los barracones. Hace frío. Por todas partes salen hombres como él, bostezando, con la capa sobre los hombros."),
        v("En la explanada del cuartel general el tribuno da la contraseña del día, escrita en una tablilla que pasa de centuria en centuria. Hoy es un nombre de dios. {nombre} la repite en voz baja para no olvidarla, porque sin ella no se pasa por ninguna puerta del campamento."),
        v("El cuarto de la tienda tiene dos piezas: en la de delante, las armas colgadas, los escudos apoyados en la pared y los petates; en la de detrás, las literas. {nombre} pasa de una a otra agachando la cabeza, porque la viga es baja y ya se ha dado tres veces."),
        v("{nombre} se lava en el pilón del callejón con agua que baja helada por un canal de piedra, se pasa la mano por la cara sin afeitar y se peina con los dedos. En el campamento hay barbero, pero se le paga, y hoy no es día de fiesta."),
        v("La primera luz llega gris sobre la muralla y sobre el río, que a esta hora echa una niebla blanca. Desde el camino de ronda se ve la otra orilla, plana y oscura, donde ya no es el imperio. {nombre} la mira cada mañana, como quien mira si sigue cerrada una puerta."),
        v("Lo despierta Barbatio, que ronca como un buey y se despierta antes que nadie para quejarse de que no ha dormido. {nombre} le tira una bota. Así empiezan casi todos los días en el cuarto, con una bota en el aire y un insulto de vuelta."),
    )

    /** Las seis semanas reparando el camino del desfiladero (estado «camino», temporada 2). */
    private val CAMINO = listOf(
        v("{nombre} se despierta en una tienda de cuero clavada en una orilla estrecha, entre la pared de roca y el río, que en el desfiladero corre tan fuerte que no deja oír nada más. La roca sube a pico hasta el cielo. Ahí arriba, colgado, está el camino que hay que arreglar.", estado = "camino"),
        v("A esta hora trabaja colgado sobre el río: abre agujeros en la roca con el pico y el cincel, mete vigas de roble y encima clava tablas, para que el camino de madera siga pegado a la pared. Abajo, el agua hace remolinos. Nadie mira abajo.", estado = "camino", hacer = Hacer.TRABAJO),
        v("A esta hora come en la orilla, sentado en una piedra, pan duro, tocino y queso, con las manos llenas de polvo de roca. El cocinero de la centuria no vino: cada uno se las arregla. El río es tan ruidoso que se come sin hablar.", estado = "camino", hacer = Hacer.COMER),
        v("A esta hora duerme en la tienda de cuero, en el suelo, con el estruendo del río metido en los huesos. Los ocho de la tienda caben apretados, y el que se mueve despierta a los demás. Fuera, un centinela se pasea con frío.", estado = "camino", hacer = Hacer.DORMIR),
        v("A esta hora está junto al fuego de la orilla con los de su tienda, curándose las manos con grasa y contando cuánto falta. Atilio dice que este camino lo hizo el emperador para que se viera desde Roma. Barbatio dice que desde Roma no se ve nada.", estado = "camino", hacer = Hacer.GENTE),
        v("En el desfiladero el río se estrecha entre dos paredes de piedra y corre como si lo persiguieran. En la roca hay una lápida grande con letras que dicen que el emperador abrió aquí este camino, y debajo de la lápida, el camino, que se lo come el agua cada invierno.", estado = "camino"),
        v("Al caer la tarde la sombra de la montaña cubre el desfiladero mucho antes que el resto del mundo. {nombre} se sienta en la orilla con los pies en el agua fría, que le quita el dolor, y mira cómo bajan las barcas de la flota, que ni se paran.", estado = "camino"),
    )

    /** Más del camino: son seis semanas, y con una sola variante por hueco el cuento se repetiría día tras día. */
    private fun ca(texto: String, hacer: Hacer? = null) = v(texto, estado = "camino", hacer = hacer)
    private val CAMINO_MANANA = listOf(
        ca("La trompeta suena en el desfiladero y rebota en las dos paredes, de modo que suena tres veces. {nombre} sale de la tienda con el cuerpo molido de ayer. El río sigue ahí, gritando. Se moja la cara en él y el agua está tan fría que le quita el sueño de un golpe."),
        ca("A {nombre} lo despierta el frío que sube del río por la noche y se mete en la tienda. Fuera, los de la guardia están encendiendo el fuego con la leña que bajó la corriente. Arriba, en la pared, se ven las vigas nuevas de ayer, pálidas contra la roca."),
    )
    private val CAMINO_TRABAJO = listOf(
        ca("A esta hora sube vigas de roble desde la orilla hasta el camino con cuerdas y una polea, tirando con otros seis al compás de Barbatio, que canta una canción sucia para marcar el ritmo. Cuando la viga llega arriba, alguien la agarra, y todos sueltan el aire.", Hacer.TRABAJO),
        ca("A esta hora pica la roca para ensanchar el camino donde la pared lo deja, golpe a golpe, con el pico de la legión, y las lascas caen al río sin hacer ruido, porque el río se come el ruido. Durio, el recluta, trabaja a su lado y no se queja. Todavía.", Hacer.TRABAJO),
        ca("A esta hora hace guardia en lo alto del camino, mirando la otra orilla, por donde podría venir alguien. No viene nadie. Abajo, sus compañeros clavan tablas y gritan, y él los oye a medias por encima del río. Desde aquí se ve el agua hacer remolinos blancos.", Hacer.TRABAJO),
        ca("A esta hora come de pie, apoyado en la pared, con el pan en una mano y la otra en la cuerda, porque el centurión no deja bajar a nadie hasta que se termine el tramo. Atilio le pasa la cantimplora de posca. Sabe a vinagre y a gloria.", Hacer.COMER),
        ca("A esta hora duerme a ratos, con el ruido del río, que en el sueño se vuelve tormenta, marcha y batalla. Se despierta dos veces creyendo que llueve. No llueve: es el río, que nunca se calla. Al lado, Durio duerme como un niño.", Hacer.DORMIR),
        ca("A esta hora juega a los dados con los de su tienda sobre una piedra plana de la orilla, a la luz del fuego, y pierde lo que no tiene. Barbatio hace trampa y todos lo saben. Durio pregunta si en todos los campamentos es así, y le dicen que en todos.", Hacer.GENTE),
    )
    private val CAMINO_PUEBLO = listOf(
        ca("El campamento de la obra no es un campamento: son unas tiendas en la única orilla plana que hay, una barca amarrada, un montón de vigas y una cuerda que sube por la roca hasta el camino. Lo demás es piedra y agua. Viminacium está río arriba, a dos días de remo."),
    )
    private val CAMINO_ANOCHECER = listOf(
        ca("Anochece en el desfiladero, y el fuego de la orilla es la única luz entre las dos paredes. {nombre} se cura las manos, llenas de astillas y ampollas, con un poco de grasa. Piensa en Sabina, a dos días de río, y en que todavía falta para volver."),
        ca("Al caer la tarde suben de la barca el pan y el vino que mandan del campamento, y la centuria se junta en la orilla a cenar. Se cuentan los días que faltan como se cuenta la paga, con cuidado y desconfiando. Luego {nombre} se mete en la tienda, sin hablar."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el campamento el tiempo se mira para saber si habrá marcha, y la habrá igual."),
        v("{nombre} mira el cielo desde el camino de ronda, por encima de las tejas rojas de los barracones. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los veteranos dicen que aquí el invierno es para los lobos y el verano para los mosquitos, y que en Hispania no es así."),
        v("{cielo} En {estacion} el Danubio cambia de color, y {nombre} sabría decir la estación mirando solo el agua."),
        v("Desde la torre de la puerta se ve el río ancho, gris, y la llanura del otro lado, que no se acaba nunca. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En la legión se dice que el mal tiempo es el mejor aliado, porque los bárbaros tampoco salen."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio sobre el río, y el sol brilla en los cascos de la guardia.",
            "Hace un día claro, de los que dejan ver los montes del sur, azules.",
            "No hay una nube, y el agua del Danubio parece de acero.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris como el río.",
            "Hay nubes bajas que se arrastran por la llanura del otro lado.",
            "Está nublado, y el campamento entero parece de piedra mojada.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y las calles del campamento se vuelven barro que se mete en las botas.",
            "Cae una lluvia fina que oxida lo que no se engrasa.",
            "Llueve, y los centinelas se cubren con la capa hasta los ojos.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los rayos caen sobre la llanura y el río se pone negro.",
            "Se viene una tormenta de verano que hace volar las tejas de los barracones.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y el campamento amanece blanco, con las torres como tartas.",
            "Ha nevado toda la noche, y hay que abrir camino con palas hasta las letrinas.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla del río, tan espesa que los centinelas se gritan para saber dónde están.",
            "La niebla tapa la otra orilla, y nadie sabe qué hay del otro lado.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del este, el que viene de la llanura y no se para con nada.",
            "Hay viento, y los estandartes del cuartel general dan latigazos en sus astas.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y vuelve la temporada de las obras y de las marchas.",
            "En primavera el río crece con el deshielo de las montañas.",
            "Es primavera, y en los huertos del poblado salen las primeras coles.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el sol pega sobre la armadura hasta que quema.",
            "En verano hay mosquitos en el río y polvo en la explanada.",
            "Es pleno verano, la estación de las campañas, aunque este año no haya guerra.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y se guardan las armas de campaña y se llenan los graneros.",
            "En otoño las nieblas del río no se levantan hasta mediodía.",
            "Es otoño, y del poblado llega el olor del vino nuevo.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, el tiempo de los cuarteles, cuando el río puede helarse.",
            "En invierno se hacen las guardias con dos capas y se sueña con el fuego.",
            "Es invierno, y anochece a media tarde sobre la llanura.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora hace instrucción en la explanada con una espada de madera que pesa el doble que la de verdad, contra un poste clavado en el suelo. Estocada, escudo, paso atrás. El centurión pasa entre los postes con su vara de sarmiento y no dice nada, que es peor.", hacer = Hacer.TRABAJO),
        v("A esta hora marcha por el camino del río con el petate al hombro, colgado de una horca de madera: la olla, la ración, el pico, la capa y las estacas para la empalizada. Al paso de la centuria, los campesinos se apartan a la cuneta y miran.", hacer = Hacer.TRABAJO),
        v("A esta hora pica piedra en la cantera del monte para la muralla nueva. Los bloques bajan al campamento en carros de bueyes, y {nombre} los encaja con otros cinco, con palancas y cuerdas, hasta que la piedra se asienta con un ruido que se siente en los pies.", hacer = Hacer.TRABAJO),
        v("A esta hora hace guardia en la torre de la puerta, mirando el río y el camino, con el escudo apoyado en el muro. Pasan barcas de la flota, un carro de grano, dos mercaderes. Cada uno dice la contraseña, y cada uno la dice mal, y pasa igual.", hacer = Hacer.TRABAJO, hasta = "inmune"),
        v("A esta hora lanza la jabalina contra un muñeco de paja, a treinta pasos, una y otra vez, hasta que le arde el hombro. La punta de hierro es larga y fina, para que se doble al clavarse y el enemigo no pueda devolverla. A paja nadie la devuelve.", hacer = Hacer.TRABAJO),
        v("A esta hora limpia la armadura, lámina por lámina, con arena y aceite, y le cambia una correa que se pudrió. Una armadura descuidada se oxida en tres días junto a este río, y el centurión lo nota en uno.", hacer = Hacer.TRABAJO),
        v("A esta hora nada en el Danubio con su centuria, con la ropa y el escudo atados a la espalda, porque un legionario tiene que saber cruzar un río. El agua está fría y la corriente empuja. {nombre} sale a la otra orilla tosiendo, y vuelve nadando.", hacer = Hacer.TRABAJO, meses = setOf(6, 7, 8)),

        v("A esta hora come en el cuarto con los de su tienda lo que cocinaron entre todos en el hornillo del pasillo: gachas de trigo con tocino, pan, cebolla y queso. Se come en cuclillas, con la cuchara propia, y se habla de la paga, que siempre llega tarde.", hacer = Hacer.COMER),
        v("A esta hora come una galleta de marcha, dura como una teja, mojada en posca para que se ablande, y un pedazo de tocino. No es buena comida, pero es la que hay cuando no hay tiempo para más. Barbatio dice que con galletas así se podría levantar la muralla.", hacer = Hacer.COMER),
        v("A esta hora come con los de su tienda alrededor del hornillo: lentejas, pan recién hecho, un trozo de carne de cerdo los días buenos y vino aguado. Cada uno pone algo y el que no pone lava la olla. {nombre} lava la olla una vez por semana, por lo menos.", hacer = Hacer.COMER),

        v("A esta hora está en los baños del campamento, fuera de la muralla, pasando de la sala caliente a la fría con los de su tienda. Ahí se juega a los dados en un rincón, se compran salchichas y se sabe todo lo que pasa en la legión antes que el legado.", hacer = Hacer.GENTE),
        v("A esta hora está en el poblado pegado al campamento, en una taberna de techo bajo donde se bebe vino peor que el del cuartel y se escucha a un griego tocar la flauta. Hay soldados, comerciantes, mujeres y algún bárbaro que vende pieles.", hacer = Hacer.GENTE),
        v("A esta hora juega a los dados en el cuarto con los de su tienda, a la luz de la lucerna, por monedas pequeñas o por turnos de guardia. Barbatio pierde siempre y siempre dice que mañana gana. Mañana pierde.", hacer = Hacer.GENTE),

        v("A esta hora duerme en la litera de abajo, con la espada colgada a mano y la capa encima. En la noche se oyen las trompetas de cada cambio de guardia, cuatro veces, y los pasos de la ronda sobre la muralla. {nombre} ya no se despierta con ninguno.", hacer = Hacer.DORMIR),
        v("A esta hora hace la guardia de la noche en el camino de ronda, el segundo turno, el peor: el que corta el sueño en dos. Mira la oscuridad del otro lado del río, donde a veces hay fuegos, y cuenta los pasos para no dormirse.", hacer = Hacer.DORMIR, hasta = "inmune"),
        v("A esta hora duerme hondo, como duermen los que picaron piedra todo el día. Sueña con Hispania, con el mar, que aquí no hay, y con su madre llamándolo a comer. Se despierta con la trompeta y ya no se acuerda de nada.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y el cuarto huele a ocho hombres y a ocho pares de botas. Alguien habla en sueños en tracio, otro ronca, otro se levanta a orinar y tropieza con un escudo. En la legión se duerme con todo el mundo.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El campamento es una ciudad cuadrada, con calles rectas, cuatro puertas y todo en su sitio: el cuartel general en medio, la casa del legado, los graneros, el hospital, los talleres y los barracones en filas, uno por centuria. Quien conoce un campamento los conoce todos. {entorno}"),
        v("{entorno} Fuera de la muralla, pegado a ella, crece el poblado: tabernas, herreros, tiendas de cuero, casas de barro y madera donde viven las mujeres de los soldados y los hijos que la ley no reconoce. Cuando la legión se va, el poblado se queda."),
        v("En el río están las barcas de la flota del Danubio, largas y estrechas, que patrullan arriba y abajo y llevan grano, cartas y soldados. Desde el muelle se ve pasar todo lo que va al este. {entorno}"),
        v("En el cuartel general, en una sala pequeña con lámparas encendidas, se guardan los estandartes de la legión y el águila, que es lo más sagrado que hay en el campamento. Debajo, en un sótano, está la caja de la paga. {entorno}"),
        v("{entorno} La legión es de muchas partes: hispanos, galos, tracios, sirios, gente de los montes de aquí mismo. Se habla latín de muchas maneras, y cuando se pelea, cada uno insulta en su lengua."),
        v("Detrás de los barracones están las letrinas, con un canal de agua corriendo debajo de los asientos de piedra y unas esponjas en un palo para limpiarse, que son de todos. Ahí se sienta uno al lado del otro y se cuenta lo que no se cuenta en otra parte. {entorno}"),
    )

    /** Lo que se ve alrededor según cuántos soldados hay y cómo va la muralla. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("guarnicion", 0.0, 0.99, frases = listOf(
            "Desde que se fue la vejilación a oriente, la mitad de los barracones están cerrados y el campamento suena hueco.",
            "Hay calles enteras del campamento sin fuegos: sus hombres están en la guerra, al otro lado del mar.",
        )),
        Entorno("muralla", 0.99, frases = listOf(
            "La muralla nueva es de piedra blanca, alta, con torres en las esquinas, y desde el río se ve brillar.",
            "La muralla ya es toda de piedra, y la vieja empalizada de troncos solo queda en el recuerdo de los veteranos.",
        )),
        Entorno("muralla", 0.01, 0.99, frases = listOf(
            "La muralla nueva de piedra sube por tramos, con andamios de madera, y al lado todavía queda la empalizada vieja.",
            "Por la puerta entran carros de bueyes con bloques de piedra para la muralla, y el polvo de la obra lo cubre todo.",
        )),
        Entorno("muralla", 0.0, 0.01, frases = listOf(
            "La muralla es todavía una empalizada de troncos sobre un terraplén de tierra, gris de años y de lluvia.",
            "Los troncos de la empalizada están podridos por abajo, y se dice que pronto la harán de piedra.",
        )),
    )

    private val GENTE = listOf(
        v("Barbatio, el más viejo de su tienda, lleva quince años en la legión y se queja de todo con una precisión de escriba: de la comida, de la paga, del frío y del centurión. Nunca ha faltado a una guardia ni ha perdido una pelea. {nombre} lo quiere como se quiere a un tío gruñón."),
        v("Fausto, el centurión, lleva una vara de sarmiento con la que golpea a quien se lo gana, una cota de escamas que brilla y una cicatriz de la guerra de la Dacia que le cruza la cara. Sabe el nombre de cada uno de sus ochenta hombres. Nadie sabe el suyo de pila."),
        v("Rufo, el veterano de la centuria, cumple este año o el que viene sus veinticinco de servicio, y cada noche cuenta lo que hará con su retiro: una viña, una mujer, un perro. Lleva contándolo tantos años que el perro ya tendría nietos."),
        v("El médico del hospital es un griego flaco que cose heridas como un sastre y receta para todo vinagre, miel y reposo. A los que fingen los descubre con una sola pregunta. A {nombre} le cae bien porque una vez le sacó una astilla sin que doliera."),
        v("Su madre, en Hispania, le escribe una vez al año por mano de un escribano del mercado, y la carta tarda meses en llegar. Siempre le dice lo mismo: que coma, que no se meta en peleas y que vuelva. {nombre} guarda las cartas en el fondo del petate."),
        v("Atilio, el de la litera de al lado, es de su misma tierra y se alistaron el mismo día, en la misma fila. Es el que lee las cartas en voz alta, el que sabe todos los chistes y el que siempre tiene un trozo de queso guardado. {nombre} le confiaría la espalda.", hasta = "despedida"),
        v("Atilio está en oriente, con la parte de la legión que se fue a la guerra, y su litera la ocupa un recluta que no sabe chistes. {nombre} no se acostumbra al silencio del lado izquierdo. Cuando llega una carta del este, la lee él, despacio, en voz alta.", desde = "despedida"),
        v("Durio, el recluta tracio, no sabía latín cuando llegó y ahora sabe los insultos y las órdenes, que es lo que se aprende primero. Es fuerte como un buey y tímido como una oveja. {nombre} le enseña a encajar el escudo con los demás, sin que le pisen los pies.", desde = "recluta"),
        v("Sabina sirve vino en una taberna del poblado y se ríe con la boca abierta, sin taparse, como no se ríen las mujeres de Hispania. {nombre} va a verla siempre que no está de guardia, y a veces cuando sí. No se pueden casar. Hacen como si sí.", desde = "sabina", hasta = "espera_sabina"),
        v("Sabina espera un hijo, y sigue sirviendo vino en la taberna con la barriga por delante, apartando a los borrachos con el codo. {nombre} le lleva lo que puede de la ración. Las otras mujeres del poblado le dicen que será niña, por cómo camina.", desde = "espera_sabina", hasta = "nace_gaia"),
        v("Gaia, su hija, vive con Sabina en el poblado, al otro lado de la muralla, y {nombre} la ve cuando le dan permiso, que nunca es bastante. Para la ley no es su hija ni tiene padre. Para la niña, el padre es el que llega con botas que suenan y la levanta hasta el cielo.", desde = "nace_gaia"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de la legión: a fuerza de que se las griten."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En la legión se dice que un soldado vale lo que valen sus botas, y que unas botas valen media paga."),
        v("{dato_objeto} Son cosas que se aprenden mirando a los veteranos, como casi todo en el ejército."),
        v("Un legionario tiene pocas cosas propias, y casi todas se las descontaron de la paga. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("gladio", "la espada", "una espada", "La espada",
            "La espada del legionario es corta, ancha y de dos filos, para pinchar más que para cortar; se lleva colgada a la derecha, para que no estorbe al escudo, y se saca con un gesto que se practica mil veces."),
        ObjetoCuento("pilum", "la jabalina", "una jabalina", "La jabalina",
            "La jabalina tiene un asta de madera y una punta de hierro larga y fina que se dobla al clavarse, para que no la devuelvan; cada legionario lleva dos, y las tira a treinta pasos antes de sacar la espada."),
        ObjetoCuento("escudo", "el escudo", "un escudo", "El escudo",
            "El escudo es curvo y grande, de tablas de madera pegadas y forradas de cuero, con un bollo de hierro en medio; pesa como un niño, se lleva con una funda de cuero en las marchas y lleva pintado el rayo de la legión."),
        ObjetoCuento("casco", "el casco", "un casco", "El casco",
            "El casco es de hierro, con un borde ancho atrás para el cuello y dos alas de metal para las mejillas; dentro lleva grabado el nombre de su dueño y el de su centurión, y a veces el de otro dueño anterior, tachado."),
        ObjetoCuento("caligas", "las botas", "unas botas", "Las botas",
            "Las botas del legionario son una suela gruesa llena de clavos de hierro y unas correas que dejan los dedos al aire; agarran en el barro y en la piedra, pero en el suelo de mármol de una ciudad resbalan como sobre hielo."),
        ObjetoCuento("dolabra", "el pico", "un pico", "El pico",
            "El pico del legionario tiene de un lado un hacha y del otro una punta para cavar; con él se hacen los fosos, las empalizadas y los caminos, y en la legión se dice que se ganan más guerras con el pico que con la espada."),
        ObjetoCuento("petate", "el petate", "un petate", "El petate",
            "El petate de marcha cuelga de una horca de madera al hombro: la olla, la ración de tres días, la capa, el pico, dos estacas y lo personal; con todo encima, el legionario parece una mula, y así lo llaman."),
        ObjetoCuento("posca", "la posca", "una cantimplora de posca", "La posca",
            "La posca es vinagre de vino mezclado con agua, a veces con unas hierbas; quita la sed mejor que el agua sola, no se pudre en la cantimplora y es lo único que se bebe en marcha."),
        ObjetoCuento("pan", "la galleta de marcha", "una galleta de marcha", "La galleta",
            "La galleta de marcha es pan cocido dos veces hasta que no le queda agua; dura semanas en el petate, se rompe con una piedra y se ablanda en posca, y no sabe a nada, que en campaña ya es un sabor."),
        ObjetoCuento("moneda", "el denario", "un denario", "El denario",
            "El denario es una moneda de plata con la cara del emperador; la paga llega tres veces al año, y antes de verla se descuentan la comida, las botas, el heno de las mulas y la cuota para el entierro de cada uno."),
        ObjetoCuento("tablilla", "la tablilla", "una tablilla", "La tablilla",
            "La tablilla es una hoja fina de madera donde se escribe con tinta, o una tabla con cera donde se escribe con punzón; en ella van las listas de guardia, las cuentas, las cartas y, en el cuartel general, el nombre de cada soldado vivo."),
        ObjetoCuento("lucerna", "la lucerna", "una lucerna", "La lucerna",
            "La lucerna es una lamparita de barro con aceite y una mecha; en los barracones hay una por cuarto, y alrededor de su luz pequeña se juega a los dados, se cosen las correas y se cuentan las mentiras de la noche."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en la legión como se piensa en el tiempo: no se la elige del todo, se vive en ella. Le da de comer, le da una espada y le quita los mejores años. A sus {edad} años, no sabe qué haría fuera."),
        v("Piensa a veces en la otra orilla del río, la de los bárbaros, que se ve cada mañana desde la torre. Allí también hay gente que se levanta, come y se queja del frío. Lo piensa y no lo dice, porque en la legión esas cosas no se dicen."),
        v("{mundo} Él piensa en cosas más cercanas: en la paga, en las botas, que se le están abriendo, en el turno de guardia de esta noche. Las cosas grandes son de los generales, que las piensan en tiendas con alfombras."),
        v("{mundo} A sus {edad} años, {nombre} ha visto morir a cuatro compañeros, ninguno en batalla: uno de fiebre, uno ahogado, uno aplastado por una piedra y uno de una pelea en una taberna. En la legión se muere más de la vida que de la guerra."),
        v("Le quedan muchos años para el retiro, y a veces los cuenta en la guardia de la noche: veranos, inviernos, marchas. Le darán tierras, dicen, en algún sitio que él no elegirá. Le gustaría que tuvieran un río, porque ya no sabe vivir sin uno."),
        v("{mundo} A veces, en la guardia, piensa en los dioses del campamento: Marte, Júpiter, la Fortuna de los baños, el genio de la centuria y los que traen los sirios y los tracios. {nombre} les reza a todos un poco, por si acaso, y espera que se pongan de acuerdo."),
    )

    private val MUNDOS = mapOf(
        "obras" to listOf(
            "En el campamento se habla de la muralla nueva, que la quieren de piedra para que dure siglos.",
            "Se dice que en Roma el emperador gasta el oro de la Dacia en un foro como no se ha visto.",
            "En los baños se habla de la paga, que se retrasa, y de un tribuno nuevo que no sabe montar.",
        ),
        "hielo" to listOf(
            "Se habla del invierno, que ha sido duro, y de los sármatas, que cruzaron el río helado.",
            "Se dice que el emperador está en Roma, contento, y que no habrá guerra este año.",
        ),
        "foro" to listOf(
            "Se habla del foro nuevo de Roma, que dicen que tiene una columna que llega a las nubes.",
            "En la legión se dice que el emperador mira hacia oriente, y que eso significa guerra.",
        ),
        "partida" to listOf(
            "Se habla de la guerra contra los partos, que ya empezó, y de quién se irá y quién se quedará.",
            "En el campamento se dice que el emperador viaja hacia oriente, y que se lleva lo mejor de cada legión.",
        ),
        "oriente" to listOf(
            "Se habla de las victorias de oriente, que llegan por carta, tarde y más grandes de lo que fueron.",
            "En los baños se cuentan cosas de Armenia y de Mesopotamia, donde nadie de aquí ha estado nunca.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde suena la trompeta de la retreta, se cierran las cuatro puertas del campamento y se encienden las antorchas de las torres. {nombre} vuelve a su cuarto con los demás, se quita las botas, que es lo mejor del día, y se sienta en la litera."),
        v("Anochece sobre el Danubio, y en el camino de ronda se encienden los fuegos de la guardia, uno en cada torre, en fila, hasta donde se ve. Del otro lado del río también hay fuegos, pocos. {nombre} los cuenta desde la muralla, como cada noche."),
        v("Se cena en el cuarto, a la luz de la lucerna, con los de la tienda alrededor del hornillo. Se habla de mujeres, de la paga y de los que ascendieron, en ese orden, y luego alguien saca los dados. {nombre} se acuesta antes, porque mañana hay marcha."),
        v("De noche el campamento suena como un animal grande que duerme: pasos de la ronda, una puerta que cruje, una mula que se queja en el establo, la trompeta de cada guardia. {nombre} lo oye desde la litera, tapado con la capa, y se duerme contando turnos."),
        v("La última luz se queda un momento sobre el río, roja, cuando el campamento ya está a oscuras. {nombre} la ve desde la puerta del barracón, con la capa sobre los hombros. En Hispania, piensa, a esta hora todavía es de día."),
        v("Al anochecer el poblado se enciende al otro lado de la muralla: lucernas en las ventanas, una flauta en una taberna, voces de mujeres. Los que tienen permiso salen. Los que no, miran desde el camino de ronda. {nombre} mira."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "votos" to unDia(
            Parrafo.MANANA to "Hoy se renuevan los votos por la salud del emperador. La legión entera forma en la explanada del cuartel general, con los estandartes delante y el águila en medio, brillando. El legado lee los votos del año y la legión los repite, cinco mil voces a la vez.",
            Parrafo.PUEBLO to "Delante del altar del cuartel general sacrifican un buey blanco con los cuernos dorados. El altar es nuevo, de piedra, con la fecha grabada; el del año pasado lo entierran con respeto detrás del cuartel, junto a los de los otros años, como se hace.",
            Parrafo.PENSAMIENTO to "{nombre} repite el juramento con los demás y piensa en el emperador, que es un hombre de Hispania, como él, de una ciudad del sur. Le parece raro rezar por la salud de alguien que no ha visto nunca. Pero si el emperador está sano, piensa, la paga llega.",
        ),
        "dia_del_emperador" to unDia(
            Parrafo.MANANA to "Hoy se cumplen años de la subida al trono del emperador, y en el campamento se celebra con un sacrificio ante los estandartes. La legión forma con la armadura limpia, y el tribuno lee un elogio del emperador que nadie escucha y todos aplauden.",
            Parrafo.TRABAJO to "A esta hora no hay instrucción ni obra: después del sacrificio, cada centuria recibe una ración de vino de verdad, no de posca, y carne del buey. Se come en los barracones, de pie, y se brinda por el emperador, por la legión y por la paga, en ese orden.",
            Parrafo.ANOCHECER to "Esta noche en el cuarto hay vino del bueno y Barbatio canta una canción de la Dacia que nadie entiende y todos corean. {nombre} se acuesta con la cabeza dando vueltas y el estómago lleno. El emperador, piensa, debería cumplir años más a menudo.",
        ),
        "quinquatrias" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las Quinquatrias, los días de Minerva y de Marte, cuando el ejército purifica sus armas para la temporada de guerra. En la explanada se amontonan los escudos, las espadas y las jabalinas de cada centuria, limpias como nunca, y el sacerdote las rocía de agua.",
                Parrafo.PUEBLO to "Delante del cuartel general hay una procesión de los estandartes, con los portaestandartes vestidos con pieles de oso y de lobo sobre el casco, y los trompeteros detrás. Se reza para que las armas no fallen y para que no haga falta usarlas, aunque eso se reza más bajito.",
                Parrafo.GENTE to "Barbatio dice que en sus quince años ha purificado tantas veces su espada que ya debe de estar más limpia que un altar. {nombre} le dice que la suya tiene una mella. Barbatio se la mira, la busca, y no la encuentra. Era broma, y se ríe.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días se trabaja menos y se reza más. En los barracones se repasan las correas, se cambian los clavos de las botas y se afilan las hojas, como si mañana empezara la guerra.",
                "Mientras duran las Quinquatrias hay carreras y luchas entre las centurias en la explanada, y apuestas en todos los barracones. La de {nombre} pierde la carrera y gana la lucha.",
                "El último día de las Quinquatrias se purifican las trompetas de la legión, que suenan todas a la vez delante del cuartel general. El ruido se oye desde la otra orilla del río. Así sabrán, dice Barbatio.",
            )),
        ),
        "natalicio_roma" to unDia(
            Parrafo.MANANA to "Hoy es el cumpleaños de Roma, y en el campamento se sacrifica una vaca a la Ciudad Eterna, que casi nadie de la legión ha visto. Se dice que Roma tiene un millón de habitantes, mármol en las calles y agua que corre en todas las casas. Aquí se lo creen todo.",
            Parrafo.PUEBLO to "En el poblado encienden hogueras de paja y los jóvenes saltan por encima, como se hacía en Roma cuando era un pueblo de pastores. Los soldados que tienen permiso saltan también, con la armadura, y uno se quema la capa y lo celebran como si fuera una victoria.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Roma, que está a un mes de marcha y que es la razón de todo esto: la muralla, la guardia, el río. Nunca la ha visto. Dicen que si un día lo licencian con honores, puede ir. Le gustaría ver si el agua corre de verdad en todas las casas.",
        ),
        "rosalias" to unDia(
            Parrafo.MANANA to "Hoy son las Rosalias de los estandartes. Las primeras rosas del poblado y de los huertos del campamento se cortan antes del alba y se trenzan en guirnaldas, y con ellas se adornan los estandartes de cada centuria y el águila de la legión.",
            Parrafo.PUEBLO to "Los portaestandartes sacan las insignias al sol, coronadas de rosas, y la legión desfila delante de ellas. Es la única vez en el año que el águila huele a flores y no a aceite de armas. Las mujeres del poblado miran desde la puerta, porque las rosas son de sus huertos.",
            Parrafo.PENSAMIENTO to "{nombre} mira el estandarte de su centuria, una mano de plata en lo alto de un asta con medallas, cubierto de rosas, y piensa que por ese trozo de metal moriría si hiciera falta. No sabe bien por qué. Pero lo sabe todo el mundo, y eso basta.",
        ),
        "neptunalias" to unDia(
            Parrafo.MANANA to "Hoy son las Neptunalias, la fiesta del dios de las aguas, y en el campamento junto al Danubio se toma en serio. El río es el que trae el grano, el que se lleva a los ahogados y el que separa este lado del otro. Se le ofrece un toro en la orilla.",
            Parrafo.PUEBLO to "En la orilla del río se levantan enramadas de ramas verdes, como dice la costumbre, y los soldados y la gente del poblado comen y beben a su sombra, con los pies en el agua. Los marineros de la flota echan vino al río desde sus barcas.",
            Parrafo.ANOCHECER to "Al caer la tarde, los más jóvenes de la centuria se tiran al río desde el muelle, entre gritos, y {nombre} se tira con ellos. El agua de verano está tibia por arriba y fría por abajo. Al salir se quedan en la orilla, secándose, mirando la otra orilla dorada.",
        ),
        "natalicio_emperador" to unDia(
            Parrafo.MANANA to "Hoy es el cumpleaños del emperador, y la legión lo celebra con un desfile en la explanada, un sacrificio en el cuartel general y vino de la legión. La armadura brilla como el primer día, porque el centurión pasó revista dos veces antes del amanecer.",
            Parrafo.TRABAJO to "A esta hora la legión desfila delante del legado, centuria por centuria, al paso, con los estandartes delante y las trompetas marcando. {nombre} lleva el escudo exacto, la jabalina exacta y la cara que hay que llevar, que es de piedra.",
            Parrafo.GENTE to "Barbatio dice que el emperador cumple años como cualquiera, y que si fuera listo regalaría una paga extra a la legión, como hacen los buenos. Rufo dice que este emperador ya les regaló la Dacia. Barbatio dice que la Dacia no se come.",
        ),
        "armilustrium" to unDia(
            Parrafo.MANANA to "Hoy es el Armilustrium, cuando se purifican las armas al final de la temporada de campaña y se guardan para el invierno. Se acaban las marchas largas y las obras de verano. Empieza el tiempo de los cuarteles, del frío y de las guardias.",
            Parrafo.TRABAJO to "A esta hora la centuria pasa delante del altar con las armas en la mano, una por una, mientras el sacerdote las rocía y las trompetas suenan. Luego se engrasan, se envuelven en paño y se cuelgan en el cuarto de delante, hasta la primavera.",
            Parrafo.PENSAMIENTO to "{nombre} engrasa su espada y piensa en otro año más de servicio, que ya se cuenta como se cuentan las cosechas. No ha habido guerra este año, solo piedra, río y marchas. Le parece bien. Una espada que no se usa, dice Rufo, es la mejor espada.",
        ),
        "saturnales" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las Saturnales, siete días al revés, también en el campamento. Se puede jugar a los dados delante de los centuriones, hablar alto en las guardias y llevar un gorro de fieltro en vez del casco. En cada tienda se elige un rey de la fiesta, que manda cosas absurdas.",
                Parrafo.PUEBLO to "En el poblado hay mercado de regalos: velas de cera, figuritas de barro, nueces y dulces. Los soldados compran velas para las mujeres y figuritas para los niños que la ley no reconoce como suyos. Las tabernas no cierran en toda la noche.",
                Parrafo.GENTE to "En la tienda de {nombre} el rey de la fiesta es Barbatio, por votación, y lo primero que manda es que nadie se queje en siete días, salvo él. Lo segundo, que {nombre} cante. {nombre} canta, mal, una canción de Hispania, y lo obligan a repetirla.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días el campamento es una taberna: se bebe en los barracones, se juega en las calles y los centuriones hacen como que no ven. Las guardias se hacen igual, pero cantando.",
                "Mientras duran las Saturnales se regalan velas y figuritas de barro, y en el poblado hay música cada noche. Hay quien dice que la legión entera podría caer en una noche de estas, y quien le contesta que los bárbaros también celebran algo.",
                "Estos días no hay instrucción, ni obra, ni marcha, ni casi trabajo. Hay quien dice que las Saturnales deberían durar todo el año, y quien le contesta que entonces ya no serían fiesta, y los dos se ponen a beber.",
                "El último día de las Saturnales se recoge, se duerme y se vuelve al orden. Mañana suena la trompeta a la hora de siempre, y el centurión vuelve a llevar la vara.",
            )),
        ),
        "sabina" to unDia(
            Parrafo.MANANA to "Hoy, en una taberna del poblado donde entró por casualidad a beber con Atilio, {nombre} conoció a Sabina, la que sirve el vino. Es de aquí, de los montes, habla latín con un acento que le gusta y se rió de su acento a él. Le sirvió el vino sin aguar, por error, o eso dijo.",
            Parrafo.GENTE to "Atilio dice que esa mujer le sirvió el vino sin aguar a propósito, y que eso en el poblado quiere decir algo. Barbatio dice que en el poblado todo quiere decir algo y nada quiere decir nada. {nombre} no dice nada, y piensa en volver mañana.",
            Parrafo.PENSAMIENTO to "{nombre} sabe que un legionario no puede casarse, y que la mayoría tiene una mujer en el poblado igual, con hijos que la ley no ve. Nunca había pensado en eso para él. Esta noche, en la guardia, lo piensa entero, de una punta a otra del camino de ronda.",
        ),
        "muralla" to unDia(
            Parrafo.MANANA to "Hoy empezaron a rehacer en piedra la muralla del campamento. Al amanecer llegaron los agrimensores con sus varas y sus cuerdas, marcaron el trazado sobre el terraplén, y el ingeniero de la legión dio la orden. La empalizada de troncos tiene los días contados.",
            Parrafo.TRABAJO to "A esta hora cava los cimientos de la muralla nueva con su centuria, con el pico y la pala, en una zanja que llega a la cintura. Detrás vienen los canteros con los primeros bloques. {nombre} encuentra en la tierra una moneda vieja, de otro emperador, y se la guarda.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esta muralla va a estar aquí cuando él ya no esté, ni sus hijos, ni los hijos de sus hijos. Le gusta la idea. Piensa también que la va a levantar él, piedra por piedra, y eso le gusta menos, porque las piedras pesan.",
        ),
        "carta_madre" to unDia(
            Parrafo.MANANA to "Hoy llegó una carta de su madre desde Hispania, después de cuatro meses de camino, escrita por un escribano del mercado en una tablilla de madera doblada. Atilio se la lee en voz alta, en el cuarto, despacio, mientras los demás hacen como que no escuchan.",
            Parrafo.GENTE to "Su madre dice que está bien, que el olivo dio poco, que su hermana se casó con un herrero, que coma y que no se meta en peleas. Al final, el escribano añadió por su cuenta que la señora lloró al dictar. Barbatio se suena la nariz y dice que es el polvo.",
            Parrafo.PENSAMIENTO to "{nombre} guarda la tablilla en el fondo del petate con las otras, y piensa en su madre dictando la carta en el mercado, con su mejor ropa. Hace siete años que no la ve. Esta noche le pide a Atilio que le escriba la respuesta, y no sabe qué decirle.",
        ),
        "dedos" to unDia(
            Parrafo.MANANA to "Hoy, en la obra de la muralla, un bloque de piedra se resbaló de la palanca y le aplastó dos dedos de la mano izquierda contra el bloque de abajo. {nombre} no gritó, que es lo que se hace. Luego se sentó en el suelo, blanco, y el centurión lo mandó al hospital.",
            Parrafo.TRABAJO to "A esta hora está en el hospital del campamento, en una sala larga con camas y olor a vinagre, mientras el médico griego le endereza los dedos y se los ata a una tablilla. Le da vino con algo amargo. Dice que no se los cortará. {nombre} le cree, porque quiere creerle.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que podría haber sido la mano entera, o la cabeza, y que un soldado sin mano izquierda no puede llevar escudo. Mira los dedos atados y les habla, en voz baja, para que se curen. El médico lo oye y no se ríe.",
        ),
        "dedos_sanan" to unDia(
            Parrafo.MANANA to "Hoy el médico le quitó la tablilla de los dedos y le hizo cerrar la mano. Se cierra. Le duele, se cierra torcida, pero se cierra. El médico dice que en un mes podrá llevar el escudo como antes. {nombre} cierra y abre la mano veinte veces, para creérselo.",
            Parrafo.TRABAJO to "A esta hora vuelve a la obra de la muralla, con la mano todavía débil, y el centurión lo pone a llevar la cuenta de los bloques en una tablilla en vez de a cargarlos. {nombre} cuenta mejor que carga, descubre. El centurión también lo descubre.",
            Parrafo.GENTE to "Esta noche, en el cuarto, los de su tienda le hacen un brindis por los dedos, que ya tienen nombre: Barbatio les puso los nombres de dos centuriones que odia. {nombre} los mueve en el aire, y todos aplauden como si fueran actores.",
        ),
        "noticia_foro" to unDia(
            Parrafo.MANANA to "Hoy llegó con el correo de la flota la noticia de que el emperador está levantando en Roma un foro enorme, con el oro de la Dacia, que es el oro que ganaron en la guerra muchos de esta misma legión. Barbatio dice que él puso ese oro, con su espalda.",
            Parrafo.PUEBLO to "En los baños no se habla de otra cosa: que el foro tendrá una plaza más grande que el campamento entero, una basílica, dos bibliotecas y una columna con la guerra esculpida. Uno dice que saldrá su cara en la columna. Nadie le cree, y él tampoco.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que en Roma levantan mármol con lo que aquí se ganó con sangre y con barro, y que la gente de Roma pasará por ese foro sin pensar en el Danubio. No le parece injusto. Le parece lejano, como todo lo de Roma.",
        ),
        "dados" to unDia(
            Parrafo.MANANA to "Hoy, en una partida de dados en el cuarto que empezó por unas nueces y acabó en serio, {nombre} le ganó a Barbatio la paga entera de un mes. Barbatio se quedó callado, que es lo más raro que ha pasado en la legión en quince años.",
            Parrafo.GENTE to "En la cena, {nombre} le devolvió a Barbatio la paga, toda, en un pañuelo, delante de los demás. Barbatio la cogió sin decir nada, y luego dijo que la próxima vez se la ganaría él, y que no se la devolvería. Todos se rieron, y Barbatio también.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que en la legión todo se comparte, la comida, el frío, la guardia, la muerte, y que el dinero no debería ser distinto. No lo dice así. Pero esta noche Barbatio le cede el lado de la litera que está más lejos de la puerta, y eso lo dice todo.",
        ),
        "primera_nieve" to unDia(
            Parrafo.MANANA to "Hoy amaneció nevado: la primera nevada del invierno cayó de noche, en silencio, y el campamento despertó blanco, con los tejados, las torres y la muralla cubiertos. Los centinelas de la última guardia tienen nieve en los hombros y la nariz roja.",
            Parrafo.TRABAJO to "A esta hora abre camino en la nieve con una pala, por las calles del campamento, desde los barracones hasta los graneros, las letrinas y la puerta. La nieve pesa. Los reclutas del sur la tocan con los dedos, como si fuera harina, y Barbatio les tira una bola.",
            Parrafo.ANOCHECER to "Esta noche el campamento está callado como nunca: la nieve se come los ruidos. En el cuarto se enciende el brasero, que es de todos, y los ocho se sientan alrededor con las capas puestas. Alguien cuenta una historia de fantasmas del río. Nadie se duerme pronto.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "legionario", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + CAMINO.take(1) + CAMINO_MANANA,
            Parrafo.TIEMPO to TIEMPO + v("En el desfiladero el cielo es una franja estrecha entre dos paredes de roca, y el sol solo baja hasta el río un rato al mediodía. {cielo} {de_estacion}", estado = "camino"),
            Parrafo.TRABAJO to TRABAJO + CAMINO.subList(1, 5) + CAMINO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + CAMINO[5] + CAMINO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + CAMINO[6] + CAMINO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
