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
 * El cuento de Amaru, que vende fideos en un puesto del nivel −3 de una megaciudad de neón. Es ficción. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Su día también va al revés: se despierta a las cinco de la tarde, así que «la mañana» es la tarde y «el anochecer»
 * es cuando cierra, de madrugada.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioPiloto.kt`) y del de Kei:
 * - La repartidora de la bici azul llega por primera vez el 12 de diciembre de la temporada 1 (el mismo día en que
 *   Kei cena en el puesto) y desde entonces pide doble caldo. Hasta el 5 de diciembre de la temporada 3 él no sabe
 *   cómo se llama.
 * - Su hermana le escribe en la temporada 1 después de años sin saber de ella; el guardia de Áurea empieza a cenar en
 *   la 2; Sami llega en la 3; en la 5 sube a la superficie y Celso se jubila. Las variantes que lo cuentan llevan
 *   `desde` o `hasta`.
 * - El letrero se apaga del 12 al 28 de noviembre de la temporada 2: lo dice `{entorno}`, que lee el arco `letrero`,
 *   el mismo que dibujará el cuadro.
 * - Las fiestas que vuelven cada año no nombran a Sami, al guardia ni a Kei, no ponen a Celso en la farmacia después
 *   de octubre, y solo la última noche del año habla de la repartidora (cae siempre después de conocerla).
 */
internal object PiezasAmaru {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta cuando el transformador del letrero cambia de zumbido, que es cuando en la superficie se pone el sol. No necesita reloj: lleva años despertándose con ese ruido. Se lava la cara en el fregadero, que es también la ducha, y baja por la escalera de caracol hasta el puesto."),
        v("Se despierta en el cuarto de arriba del puesto, que tiene una ventana que da a otra pared, a un palmo. Por la rendija entra la luz magenta del letrero de abajo, que lo tiñe todo, hasta las sábanas. Hace años que no sabe de qué color son de verdad."),
        v("Lo primero que hace al despertar es oler: si huele a caldo, es que la olla sigue viva desde ayer y el fuego no se ha apagado. Hoy huele. {nombre} baja en camiseta, levanta la tapa, prueba con el dedo y asiente, como quien saluda a un viejo amigo."),
        v("En el callejón ya hay luz, que aquí es siempre la misma: la de los letreros. Lo que cambia es el ruido. A esta hora se oyen las persianas de las tiendas que abren, los cubos de la basura y una niña que practica la flauta en un balcón, siempre la misma canción, siempre mal."),
        v("{nombre} se despierta con los dedos rígidos, como cada tarde, y los abre y cierra despacio, uno por uno, hasta que vuelven a ser suyos. Tiene manos de cocinero: anchas, con quemaduras viejas como mapas. Se las mira un rato, y luego se pone el delantal."),
        v("El cuarto de arriba del puesto huele a cebolla y a sueño. {nombre} se viste a oscuras, con la ropa de ayer, que es la de mañana, y baja descalzo, por costumbre, haciendo crujir los mismos tres escalones de siempre."),
        v("Al bajar, Celso ya está asomado a la puerta de la farmacia de guardia, con su bata blanca y su taza. Se saludan con la cabeza, de acera a acera, como todas las tardes desde hace quince años. Es su manera de comprobar que los dos siguen vivos.", hasta = "celso_se_jubila"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el nivel −3 el tiempo se sabe por los clientes: por lo mojados que llegan y por lo que piden."),
        v("{nombre} no ve el cielo desde hace años, pero lo oye en la radio y lo nota en el caldo. {cielo} Con humedad, la sopa tarda más en espesar, y la gente pide más."),
        v("{de_estacion} {cielo} Los repartidores traen el tiempo pegado a la ropa y lo cuentan con detalle, como si él no tuviera ventana. No la tiene."),
        v("{cielo} Los viejos del callejón dicen que en temporada de {estacion} siempre fue así, que antes era peor y que antes de antes, mejor. {nombre} les sirve otro caldo, que es lo que se le da a la gente que habla del tiempo."),
        v("Lo primero que hace al abrir es asomarse a la boca del callejón, por donde baja el aire de arriba. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En su puesto el tiempo solo es noticia cuando se mete por el desagüe o se lleva la luz. Lo demás se arregla con una olla más grande."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "Dicen que arriba la noche está limpia, de las que desde abajo no se ven.",
            "La radio dice que no hay nubes, y el aire baja más fresco por el callejón.",
            "Arriba no hay nubes, y por la boca del callejón entra un poco de luna.",
        ),
        Clima.NUBES to listOf(
            "Arriba está nublado, y el aire baja pesado y quieto.",
            "La radio dice que hay nubes bajas, rosadas por los anuncios.",
            "Está cubierto, y la humedad hace sudar las paredes.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve arriba, y abajo llega por goteras, por canalones y por la escalera.",
            "Llueve, y el toldo del puesto se llena hasta que alguien lo vacía con un palo.",
            "Llueve, y el agua llega al callejón después de pasar por cien balcones.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, y los truenos bajan por los conductos y hacen temblar los tazones.",
            "Arriba se ha roto el cielo, y el agua baja por el callejón como un río chico.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, y el letrero la tiñe de magenta, como una sopa de remolacha.",
            "La niebla baja hasta el callejón y se mezcla con el vapor de la olla.",
        ),
        Clima.POLVO to listOf(
            "Sopla viento seco y trae polvo, y hay que tapar las ollas.",
            "Hay polvo en el aire, y el caldo destapado coge gusto a calle.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.LLUVIAS to listOf(
            "Es la temporada de lluvias, y los bancos se suben a ladrillos.",
            "En lluvias se vende el doble de caldo: todo el mundo llega mojado.",
            "Es temporada de lluvias, y el letrero zumba raro cuando le cae agua.",
        ),
        Estacion.SECAS to listOf(
            "Es la temporada seca, la del calor quieto bajo los toldos.",
            "En secas la gente pide fideos fríos, que a él le parecen una ofensa al caldo.",
            "Es temporada seca, y el callejón huele a polvo y a aceite viejo.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora el puesto está lleno: los seis bancos ocupados y gente de pie detrás, esperando. {nombre} sirve sin mirar, de memoria: un tazón, el caldo, los fideos, el cebollín, la vuelta. Por la barra pasan caras que conoce de vista desde hace años y que no ha oído hablar nunca.", hacer = Hacer.TRABAJO),
        v("A esta hora hace fideos a mano, porque se acabaron los de la caja. Estira la masa, la dobla, la golpea contra la barra y la vuelve a estirar, hasta que entre sus manos aparecen cientos de hilos. Los clientes dejan de hablar para mirarlo. Es lo único de la noche que parece magia.", hacer = Hacer.TRABAJO),
        v("A esta hora es la hora floja, cuando ya cenaron los de la tarde y todavía no llegan los de la madrugada. {nombre} limpia la barra, rellena el cebollín y escucha la radio, que habla de cosas de arriba. En el puesto solo queda un hombre, mirando su tazón vacío como si fuera a decirle algo.", hacer = Hacer.TRABAJO),
        v("A esta hora vigila la olla, que es como vigilar a un enfermo: se le toma la temperatura, se le quita la espuma, se le da agua si tiene sed. El caldo lleva hirviendo desde la tarde, y al final de la noche sabrá a todo lo que ha pasado en el callejón.", hacer = Hacer.TRABAJO),
        v("A esta hora le sirve a un guardia que sale de turno, a una enfermera, a dos repartidores y a un hombre que no puede dormir y lo dice. Cada uno pide lo suyo, y él no se equivoca nunca. No sabe cómo se llaman, pero sabe cómo les gusta el caldo, que dice más.", hacer = Hacer.TRABAJO),
        v("A esta hora Sami le ayuda en la barra: cobra, sirve los tazones y les habla a los clientes, que con él nunca hablaban. {nombre} se ha quedado con la olla y los fideos. Hay noches en que la oye reírse con un repartidor y se da cuenta de que en el puesto se oye más que antes.", hacer = Hacer.TRABAJO, desde = "llega_sami"),

        v("A esta hora está en el mercado flotante, en las barcazas que se amarran en el canal del nivel −4. Compra cebollín, huesos, fideos secos y lo que haya barato, y regatea por todo, aunque sabe que va a pagar lo mismo. Regatear, dice, es una manera de saludar.", hacer = Hacer.CUIDAR),
        v("A esta hora compra el caldo de tanque, en bidones, en la barcaza de una mujer que no sonríe nunca y no se equivoca nunca. El caldo de tanque es la base: él le pone huesos, jengibre y paciencia, y sale otra cosa. La mujer lo sabe y le cobra como si no lo supiera.", hacer = Hacer.CUIDAR),
        v("A esta hora cruza de barcaza en barcaza con dos bolsas en cada mano, saltando el agua negra que se mueve entre ellas. Lleva años haciéndolo y no se ha caído nunca, salvo una vez, que no cuenta, porque era en lluvias y se cayó todo el mundo.", hacer = Hacer.CUIDAR),
        v("A esta hora barre el puesto antes de abrir, limpia los seis bancos con un trapo húmedo y pone los tazones en fila, boca abajo. Luego sube la cortina, enciende el letrero y espera a que el tazón magenta empiece a parpadear. Si no parpadea, le da un golpe en un lado.", hacer = Hacer.CUIDAR),

        v("A esta hora come, por fin, sentado en el banco de la punta: un tazón de su propio caldo, el último de la olla, que es el mejor. Lo come despacio, mirando la calle. Es el único tazón de la noche que no tiene que servirle a nadie.", hacer = Hacer.COMER),
        v("A esta hora come con los repartidores que terminan turno, que se sientan en los bancos y le cuentan la noche con la boca llena. Él escucha y come, que es lo mismo que hace todo el día, pero al revés. Alguno le deja propina en el tazón; él se la devuelve en caldo.", hacer = Hacer.COMER),
        v("A esta hora come lo que sobró: fideos rotos, un poco de carne, el cebollín que se iba a poner feo. Lo mezcla todo en un tazón grande y le echa caldo hirviendo. Ese plato no tiene nombre. Si lo vendiera, se lo quitarían de las manos, pero no lo vende: es suyo.", hacer = Hacer.COMER),
        v("A esta hora llegan los últimos repartidores, y entre ellos la de la bici azul, que se sienta siempre en el mismo banco y enchufa la bici al cable que él le dejó tendido. {nombre} le sirve doble caldo sin que lo pida. Comen los dos a la vez, cada uno en su lado de la barra, sin hablar.", hacer = Hacer.COMER, desde = "doble_caldo"),

        v("A esta hora es su tiempo, el de entre el cierre y el sueño. Se sienta en la escalera del cuarto con un té y la radio baja, y mira cómo se vacía el callejón. Pasan los barrenderos, los gatos, algún borracho que se despide de él como de un primo.", hacer = Hacer.LIBRE),
        v("A esta hora juega a las damas con Celso en la acera de enfrente, sobre un tablero pintado en una caja de cartón. Celso hace trampas, y {nombre} lo deja, porque así Celso no se va a dormir enfadado. Llevan años jugando y nadie sabe quién va ganando.", hacer = Hacer.LIBRE),
        v("A esta hora arregla cosas: el ventilador del puesto, la bisagra de la cortina, una silla que alguien rompió sentándose como no debía. Tiene una caja de herramientas que fue de su padre, y de la mitad no sabe para qué sirven. La otra mitad la usa para todo.", hacer = Hacer.LIBRE),
        v("A esta hora lee, despacio, un libro de papel de los que ya casi no hay, que encontró en una caja del mercado. Lee una página por día, a veces dos. Lleva tres años con el mismo libro y no tiene prisa por terminarlo, porque no tiene otro.", hacer = Hacer.LIBRE),

        v("A esta hora duerme en el cuarto de arriba, con el zumbido del transformador del letrero metido en el sueño. Si un día el zumbido se para, se despierta asustado, como quien deja de oír respirar a alguien.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, mientras arriba, en la superficie, la gente trabaja con el sol en la cara. Él ya no se acuerda bien de cómo es el sol. Se acuerda del calor en la nuca, eso sí, y a veces lo sueña.", hacer = Hacer.DORMIR),
        v("A esta hora duerme boca arriba, con las manos abiertas sobre el pecho, que es como le dejan de doler. Abajo, la olla hierve sola a fuego mínimo, como un perro que vigila la casa con un ojo abierto.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El callejón está en el nivel −3, donde nunca ha llegado el sol. Tiene treinta pasos de largo, catorce tiendas, una farmacia de guardia y su puesto, y encima, nueve pisos de ropa tendida. {entorno}"),
        v("{entorno} Por el callejón pasa de todo: repartidores con prisa, guardias sin ella, gente perdida. Los que se pierden suelen acabar en el puesto, y algunos vuelven luego a propósito."),
        v("Las tiendas del callejón abren de noche y venden cosas que no tienen nada que ver: pilas, pelucas, arreglos de implantes, té. Los dueños se prestan cambio, sal y paciencia. {entorno}"),
        v("{entorno} En los niveles bajos los letreros son de los de antes, de gas y de cristal, que zumban. Los de arriba no hacen ruido. Él prefiere los de abajo: un letrero que no zumba, dice, no está vivo."),
        v("Por encima del callejón cruzan tuberías, cables y una pasarela de rejilla, y cuando alguien pasa por ella cae polvo en las ollas. Por eso el puesto tiene toldo. {entorno}"),
        v("La farmacia de enfrente tiene una cruz verde que parpadea a destiempo de su letrero: el tazón magenta, la cruz verde, el tazón, la cruz. Los vecinos dicen que los dos letreros se están hablando. {entorno}"),
    )

    /** Lo que se ve alrededor según cómo va el callejón. Manda la primera regla que se cumple; la última vale siempre. */
    private val ENTORNOS = listOf(
        Entorno("obra", 0.5, frases = listOf(
            "En la boca del callejón siguen las máquinas de demolición, con gatos durmiendo debajo.",
            "Las máquinas de Áurea tapan media entrada, y los clientes pasan de lado.",
        )),
        Entorno("letrero", 0.9, frases = listOf(
            "Su letrero está apagado, y el callejón parece más oscuro y más triste.",
            "Con el letrero apagado, hay quien pasa de largo creyendo que el puesto cerró.",
        )),
        Entorno("agua", 0.6, frases = listOf(
            "En el suelo hay un palmo de agua, y los bancos están subidos a ladrillos.",
            "El agua corre por el centro del callejón, y los niños echan barquitos de papel.",
        )),
        Entorno("luces", 0.5, frases = listOf(
            "Hay luces de fin de año de balcón a balcón, y algunas hasta funcionan.",
            "Hay guirnaldas sobre el puesto, y el tazón magenta parpadea entre ellas.",
        )),
        Entorno("pintas", 0.5, frases = listOf(
            "Las paredes están llenas de pintadas y de carteles, y ya nadie los arranca.",
            "En la cortina de la tienda de pelucas alguien ha pintado un puño.",
        )),
        Entorno("letrero", 0.1, 0.5, frases = listOf(
            "El tazón del letrero parpadea a su manera, con una pieza que no es la suya.",
        )),
        Entorno("obra", 0.0, 0.5, frases = listOf(
            "Arriba, entre los balcones, pasa a veces la luz de un dron de Áurea.",
            "Por la escalera del fondo baja aire de arriba, que huele a lluvia aunque no llueva.",
        )),
    )

    private val GENTE = listOf(
        v("Los repartidores son sus clientes más fieles y los que menos hablan. Llegan, enchufan la bici, comen con el casco en las rodillas y se van. Conoce a muchos por la bici y a pocos por la cara. Cuando alguno deja de venir, tarda semanas en darse cuenta, y luego no se le olvida."),
        v("La dueña de la tienda de pelucas de al lado le guarda una llave del puesto, por si algún día a él le pasa algo, y le pide fiado cuando se le acaba el dinero, que es siempre. Él le fía. Ella le paga en pelucas, y ya tiene tres, que no se pone."),
        v("Hay un viejo que viene cada noche a la misma hora, pide un tazón pequeño y no se lo termina nunca. Se sienta, mira el caldo y se va. {nombre} le cobra la mitad y no le pregunta nada. Algún día, piensa, le preguntará; pero no esta noche."),
        v("Los niños del callejón le piden los fideos rotos, que él les da en un cucurucho de papel con un poco de sal. Se los comen crudos, crujientes, sentados en la escalera. Algunos ya son repartidores y siguen viniendo, y él sigue dándoles los rotos, aunque ya paguen."),
        v("Los vecinos de arriba son una familia grande que no se acaba nunca: siempre hay un primo, un cuñado, un ahijado. Cuando alguien cumple años bajan todos al puesto, piden doce tazones y se los comen de pie, cantando. Él no sabe nunca quién cumple, y canta igual."),
        v("Celso, el de la farmacia de guardia de enfrente, es su mejor amigo, aunque ninguno de los dos lo ha dicho nunca. Cruza cada noche a tomarse un caldo sin sal, por la tensión, y le cuenta quién ha comprado qué, sin nombres, que es su manera de ser discreto.", hasta = "celso_se_jubila"),
        v("Su hermana vive en otra ciudad, o vivía: hace años que no sabe de ella. Tiene una foto suya pegada detrás de la caja, de cuando eran niños, en la superficie, con el sol en la cara. Los clientes creen que es su hija, y él no los corrige.", hasta = "carta_hermana"),
        v("Su hermana le escribe ahora de vez en cuando, cartas de papel que tardan semanas, con la letra apretada. Él las guarda en la caja de los palillos y las relee en las horas flojas. Le contesta poco y tarde, porque escribir le cuesta más que hacer fideos a mano.", desde = "carta_hermana"),
        v("Hay una repartidora nueva, la de la bici azul, que viene casi todas las noches y pide doble caldo. No dice su nombre, ni él se lo pregunta. Se sienta en el banco de la punta, come callada y se va, y al día siguiente vuelve, que es lo único que un cocinero necesita saber.", desde = "doble_caldo", hasta = "nombre_kei"),
        v("La repartidora de la bici azul se llama Kei; se lo dijo una noche, sin venir a cuento, mientras pagaba. Ahora él le dice su nombre al servirle, y ella sonríe un poco. Es de las pocas clientas de las que sabe el nombre, y no le parece poca cosa.", desde = "nombre_kei"),
        v("Un guardia de Áurea cena en el puesto todas las noches, sin uniforme pero con la placa en el bolsillo, que se le nota. Pide poco, paga justo y no mira a nadie. Los demás clientes se sientan lejos de él, y él lo sabe, y viene igual.", desde = "guardia"),
        v("Sami, su sobrina, llegó con una mochila y una sonrisa de las que en el callejón no se ven, y se quedó. Duerme en el cuarto de arriba, en un colchón que él subió por la escalera de caracol a fuerza de paciencia. Le ha cambiado el orden de los tazones, y él finge que no se ha dado cuenta.", desde = "llega_sami"),
    )

    private val OBJETO = listOf(
        v("Hoy se fija, sin querer, en algo que usa todas las noches. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de la cocina: con los dedos, y con las quemaduras."),
        v("Hay cosas que solo se miran cuando faltan. {dato_objeto}"),
        v("{dato_objeto} Un puesto de seis bancos está hecho de pocas cosas, y cada una sirve para todo."),
        v("{dato_objeto} Si le preguntaran por qué, {nombre} diría que así lo hacía su padre."),
        v("En un puesto todo dura más de lo que debería. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("olla", "la olla grande", "una olla", "La olla",
            "La olla grande es de acero, del tamaño de un niño sentado, y el caldo no se vacía nunca del todo: cada tarde se le añade agua, huesos y lo que haga falta, y dicen que todavía queda en él una gota del primero."),
        ObjetoCuento("cucharon", "el cucharón", "un cucharón", "El cucharón",
            "El cucharón es de latón, con el mango envuelto en cinta para que no queme, y mide exacto un tazón; con él no hace falta pesar nada, y quien se queja de la ración se queja del cucharón, que no tiene la culpa."),
        ObjetoCuento("tazon", "el tazón", "un tazón", "El tazón",
            "Los tazones del puesto son de loza blanca y gruesa, de los que no se rompen ni a propósito; tiene cuarenta, y cada uno tiene una mella distinta, así que los conoce como a los clientes."),
        ObjetoCuento("palillos", "los palillos", "unos palillos", "Los palillos",
            "Los palillos del puesto son de bambú de tanque y se lavan y se vuelven a usar hasta que se astillan; los clientes de arriba piden de los que se tiran, y él se los da, y luego los lava."),
        ObjetoCuento("radio", "la radio", "una radio", "La radio",
            "La radio del puesto está en una repisa, entre la sal y los palillos, y agarra tres emisoras: una de música vieja, una de noticias de arriba y una que nadie sabe de dónde sale, que es la que más se oye."),
        ObjetoCuento("trapo", "el trapo", "un trapo", "El trapo",
            "El trapo de la barra ha sido de muchos colores y ahora es del color del caldo; limpia la barra, agarra las ollas calientes, espanta moscas y, cuando hace falta, seca alguna lágrima de cebolla."),
        ObjetoCuento("cebollin", "el cebollín", "un manojo de cebollín", "El cebollín",
            "El cebollín es lo único verde del puesto y lo único de verdad, cultivado en las torres-granja; se corta en el momento, fino como pestañas, y sin él, dicen los clientes, el caldo es solo agua con pena."),
        ObjetoCuento("banco", "el banco de la punta", "un banco de plástico", "El banco de la punta",
            "Los bancos del puesto son seis, de plástico rojo, y el de la punta cojea; los clientes nuevos lo evitan y los viejos lo prefieren, porque es el único desde el que se ve toda la calle sin girar la cabeza."),
        ObjetoCuento("termo", "el termo", "un termo", "El termo",
            "El termo es de metal abollado y lleva té de jengibre para la madrugada; se lo regalaron hace años, y todavía mantiene caliente lo de dentro, que es más de lo que puede decirse de casi nada."),
        ObjetoCuento("delantal", "el delantal", "un delantal", "El delantal",
            "El delantal es de lona negra, con un bolsillo para el cambio y otro para el lápiz, y tiene manchas de tantos caldos que se podría sacar de él una sopa; lo lava una vez por semana y nunca se le quita nada."),
        ObjetoCuento("cuchillo", "el cuchillo", "un cuchillo de cocina", "El cuchillo",
            "El cuchillo de cocina es de acero de los de antes, con el mango de madera gastado por su mano; lo afila cada noche contra el borde de un tazón, que es un truco de su padre y funciona, aunque nadie sepa por qué."),
        ObjetoCuento("ficha", "la ficha", "una ficha de pedido", "La ficha",
            "Las fichas de los pedidos son de plástico, con un número pintado a mano; el cliente se la lleva al banco y la devuelve con el tazón vacío, y hay una, la siete, que falta desde hace años y que nadie ha devuelto."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} no tiene planes para cuando sea viejo: piensa seguir haciendo caldo hasta que la olla le pese más que él. Dicen que eso no es un plan. Él dice que es el mejor plan que ha tenido, y el único que le ha salido bien."),
        v("Piensa a veces en lo raro de su oficio: darle de comer cada noche a cientos de personas que no conoce y que, por un rato, se sientan juntas en seis bancos. Si lo piensa mucho le parece importante, y entonces se le quema algo, y se le pasa."),
        v("{mundo} Él piensa en cosas más cercanas: en el precio del caldo de tanque, en el ventilador que hace un ruido nuevo, en si mañana llegará el cebollín. Las cosas grandes, se dice, ya tienen quien las estropee."),
        v("{mundo} A sus {edad} años, {nombre} lleva media vida en el mismo callejón. Ha visto cerrar tiendas, abrir otras, y volver con sus hijos a niños que le pedían fideos rotos. A veces piensa que el callejón es suyo, y otras que él es del callejón."),
        v("{mundo} Un cocinero de callejón no decide nada en esta ciudad, pero sin él mucha gente se iría a dormir con hambre. {nombre} le da vueltas a eso cuando baja la cortina. No se lo dice a nadie: dicho en voz alta, suena a letrero."),
        v("Hace años que no sube a la superficie. Dice que allá arriba la comida no sabe a nada, y es verdad, pero no es la razón. La razón es que ya no se acuerda bien de cómo era el sol, y le da miedo que no se parezca a lo que recuerda.", hasta = "superficie"),
    )

    private val MUNDOS = mapOf(
        "calma" to listOf(
            "En el callejón se dice que este año no ha cerrado nadie, y se dice tocando madera.",
            "La radio habla de cosas de arriba que en el callejón no pasan.",
        ),
        "compra" to listOf(
            "Desde que Áurea compró Colibrí, los repartidores comen más deprisa.",
            "Dicen que Áurea quiere comprarlo todo, y nadie sabe si es un chiste.",
        ),
        "huelga" to listOf(
            "Los repartidores hablan de parar, y en el puesto se oye más que en ningún sitio.",
            "Hay carteles de asambleas en las tuberías, y la radio dice que no pasa nada.",
        ),
        "desalojo" to listOf(
            "Se dice que Áurea quiere los niveles bajos para una torre, y nadie duerme bien.",
            "Los vecinos hablan en voz baja en la escalera, como si las paredes oyeran.",
        ),
        "tregua" to listOf(
            "Este año el callejón respira, y hasta la radio suena más bajita.",
            "Los vecinos tienen otra cara este año, aunque nadie se fía del todo.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando cierra, baja la cortina del puesto, que hace un ruido de trueno, y apaga el letrero. El tazón magenta se queda un momento encendido en los ojos de quien lo miraba, y luego desaparece. El callejón se queda con las luces de los demás, que son menos."),
        v("Al final de la noche limpia la olla, la barra y los tazones, en ese orden, y deja el caldo a fuego mínimo. Sube la escalera de caracol con las piernas de madera. Lo último que oye es el zumbido del letrero, que le canta para dormir."),
        v("Antes de dormir se sienta un rato en la puerta, con el último té del termo, y mira cómo se apagan los letreros del callejón uno a uno, como velas en una iglesia. Al final solo queda la cruz verde de la farmacia, porque alguien siempre está enfermo."),
        v("Cierra contando: los tazones que salieron, el dinero de la caja, el caldo que queda. Las tres cuentas nunca cuadran entre sí, y le parece normal: un puesto que cuadra es un puesto que miente. Apaga la radio a media canción y se va a dormir sin saber cómo termina."),
        v("La última luz de su día es la del letrero, que se apaga con un chasquido y deja el callejón un tono más oscuro. {nombre} se queda un momento a oscuras, escuchando la ciudad de arriba, que a esa hora se despierta. Luego sube, cierra los ojos y deja que la ciudad trabaje sin él."),
        v("Se lava las manos con agua fría, despacio, porque a esa hora le duelen todas las quemaduras a la vez. Se las seca en el delantal y lo cuelga en su clavo. El puesto, cerrado, parece mucho más chico que abierto, como le pasa a la gente cuando se duerme."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "faroles" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empieza la Noche de los Faroles. Al bajar, {nombre} encuentra el callejón lleno de faroles de papel colgados de balcón a balcón, rojos y amarillos, que alguien puso mientras él dormía. Entre ellos, el tazón magenta del letrero parece un farol que se equivocó de fiesta.",
                Parrafo.TRABAJO to "A esta hora sirve fideos largos, sin cortar, como se hace en la fiesta: dicen que un fideo largo es una vida larga, y que cortarlo trae desgracia. Los clientes los sorben enteros, salpicando, y nadie se queja de la mancha.",
                Parrafo.ANOCHECER to "Al cerrar, cuelga su propio farol en la puerta del puesto, uno viejo, de papel remendado, que saca cada año de una caja. No tiene vela: tiene una bombilla que conectó hace tiempo al letrero. Parpadea con el tazón, a la vez. Le parece justo.",
            ),
            dia(
                Parrafo.GENTE to "Segunda noche de los Faroles. Los vecinos del callejón bajan a cenar juntos, con farolitos en la mano, y el puesto se llena de una luz que no es la del letrero. Celso cruza con una botella de algo que guarda para esta noche. Hay quien se sienta en la escalera, porque los bancos no alcanzan.",
            ),
            dia(
                Parrafo.PUEBLO to "Última noche de los Faroles. El papel está húmedo y caído, y las velas ya se han apagado casi todas. Los niños del callejón descuelgan los que alcanzan y se los llevan a casa. Mañana el callejón volverá a tener solo la luz de los letreros, que nunca se van de fiesta.",
                Parrafo.ANOCHECER to "Esta noche guarda el farol viejo en su caja, despacio, alisándole el papel con la mano. Lleva años haciéndolo. No se acuerda de quién se lo dio. Sabe que fue alguien que ya no está, y con eso le basta para guardarlo bien.",
            ),
        )),
        "dia_red" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Red, el aniversario de la noche en que se encendió la ciudad. A medianoche todos los letreros se apagan un segundo y se vuelven a encender a la vez, y los clientes del puesto cuentan en voz alta hacia atrás, como niños.",
            Parrafo.PUEBLO to "Cuando se apagan todos los letreros, el callejón se queda un segundo a oscuras de verdad, la única vez en el año. En ese segundo se oye todo: la olla, el agua de las tuberías, la respiración de los que están en los bancos. Luego vuelve la luz, y la gente se ríe, un poco avergonzada.",
            Parrafo.PENSAMIENTO to "{nombre} se acuerda de cuando era niño y vivía arriba, y el Día de la Red se celebraba con fuegos. Su padre lo subía a hombros para verlos. Piensa que en el callejón nadie ha visto nunca un fuego artificial, y que, a cambio, aquí el segundo de oscuridad se nota más.",
        ),
        "lluvias" to unDia(
            Parrafo.MANANA to "Hoy empiezan las lluvias. {nombre} lo sabe al despertar, antes de que se lo diga la radio: el agua corre por la escalera del cuarto, como cada año, y el primer escalón está mojado. Baja de lado, pegado a la pared, y piensa que este año tendría que arreglar el canalón.",
            Parrafo.TIEMPO to "Llueve arriba como solo llueve el primer día, y abajo el agua empieza a buscar su camino por todas partes. Durante meses va a ser así: goteras, charcos, el toldo lleno. El callejón entero se prepara como quien se pone unas botas viejas que ya sabe que le aprietan.",
            Parrafo.PUEBLO to "En el callejón suben los bancos a ladrillos, tapan los enchufes del suelo y ponen sacos de arena en las puertas. Cada tienda tiene su manera, y cada dueño cree que la suya es la buena. La de las pelucas pone las pelucas en lo alto, como si se fueran a ahogar.",
        ),
        "dia_aurea" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Corporación, y por ley hay descanso obligatorio. El puesto tendría que estar cerrado, y lo está, más o menos: la cortina baja hasta la mitad, y los que saben se agachan para entrar. El letrero, apagado, por si pasa alguien que no sabe.",
            Parrafo.TRABAJO to "A esta hora sirve caldo por debajo de la cortina a los que trabajan aunque sea el día del descanso: guardias, enfermeras, repartidores. Por la radio se oye el desfile de drones de arriba, con aplausos grabados. Los clientes comen en silencio, como en un velorio pequeño.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un descanso que te obligan a tomar no es descanso: es otra forma de trabajo. Lo dice en voz alta, y Celso, desde la puerta de enfrente, le dice que no hable tan fuerte. Pero se ríe al decirlo, y cruza a por su caldo.",
        ),
        "marea" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Esta noche ha subido el agua. La tormenta empujó el mar por los túneles, y cuando {nombre} baja, el callejón tiene agua hasta el tobillo y los bancos flotan contra la pared. La olla, sobre su fuego, sigue hirviendo como si nada, que es lo que se espera de una olla.",
                Parrafo.TRABAJO to "A esta hora sirve caldo subido a un banco, con el agua por debajo, a los vecinos que achican. De vez en cuando se pone en la fila a pasar cubos, y vuelve a la olla. Esta noche nadie paga, y él no lo pide: el caldo que se da en una inundación se cobra después, de otra manera.",
                Parrafo.PUEBLO to "En el callejón todos achican: la de las pelucas, los del té, los niños con cubos de juguete. El agua sale por un lado y vuelve a entrar por el otro, pero nadie para. Desde la farmacia, Celso reparte botas de goma que tenía guardadas para esto, de todas las tallas menos la suya.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "El agua sigue en el callejón, quieta y tibia, con una capa de aceite que hace dibujos de colores bajo el letrero. Los clientes llegan descalzos, con los zapatos en la mano, y se sientan con los pies en alto, como reyes.",
                "Las bombas de achique del nivel zumban día y noche, y el agua baja un dedo cada día. En la pared del puesto queda una raya oscura donde llegó, encima de las rayas de otros años. {nombre} las mira, calcula que esta no es de las peores y se queda tranquilo a medias.",
                "Ya casi no queda agua: solo barro, y un olor que tardará semanas en irse. Los vecinos sacan todo a secar al callejón, colchones, sillas, pelucas, y el callejón parece un mercado de cosas mojadas. El puesto vuelve a abrir con los bancos en su sitio, uno cojeando más que antes.",
            )),
        ),
        "hora_muda" to unDia(
            Parrafo.MANANA to "Hoy es la Hora Muda: a las diez de la noche se apagan por ley todos los anuncios de la ciudad, durante una hora. El letrero del puesto no es un anuncio, dice {nombre}, es un letrero, y se queda encendido. Es el único del callejón, y por un rato el tazón magenta parece el faro de un puerto.",
            Parrafo.PUEBLO to "Durante la hora sin anuncios el callejón se ve como es: las paredes descascaradas, los cables, los balcones con ropa, las caras de la gente sin colores encima. Los clientes se quedan más rato del que pensaban, mirando alrededor, como si hubieran entrado en un sitio nuevo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su callejón, sin anuncios, se parece al de cuando él llegó. Piensa también que entonces era más joven, y que quizá lo que recuerda no es el callejón sino eso. Cuando vuelven los anuncios, sube un poco la radio, sin saber muy bien por qué.",
        ),
        "feria" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la feria de los niveles bajos, y el callejón se llena de puestos que no son de aquí: juegos de aros, adivinas, un hombre que vende pájaros de hojalata que cantan. {nombre} les presta enchufe a todos a cambio de nada, que es como se hace en la feria.",
                Parrafo.TRABAJO to "A esta hora no da abasto. La feria trae gente de otros niveles que ha oído hablar del caldo del callejón, y la fila llega hasta la esquina. Sirve sin levantar la vista, con el cucharón como un péndulo. Al final de la noche la olla se ha vaciado, que es cosa de una vez al año.",
                Parrafo.GENTE to "Los vecinos bajan a la feria vestidos de domingo, que en el callejón es una manera de hablar. Los niños llevan la cara pintada de colores que brillan en la oscuridad. La de las pelucas se ha puesto la más grande que tiene y saluda a todos como una reina de visita.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "La feria sigue, y el callejón huele a azúcar quemada, a fritura y a caldo, en un orden que depende de dónde se huela. La música de un puesto se mete en la del de al lado, y los dos músicos se miran con odio y siguen tocando.",
                "En la plaza de abajo hay escenario, y los grupos de los niveles tocan cada vez más fuerte. Desde el puesto se oye todo, y los tazones tiemblan en la estantería al ritmo del bajo. Un cliente dice que el caldo sabe distinto con música, y nadie le lleva la contraria.",
                "Última noche de la feria. Los puestos de fuera recogen y le dejan a {nombre} lo que les sobra: un pájaro de hojalata que ya no canta, una bolsa de caramelos, un farolito. Mañana el callejón será el de siempre, y él tendrá un pájaro en la repisa, junto a la radio.",
            )),
        ),
        "cosecha_torres" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la cosecha de las torres-granja, y en el mercado flotante hay verdura de verdad: coles, rábanos, cebolletas gordas con tierra en las raíces. {nombre} se gasta en una tarde lo que gasta en una semana, y vuelve con las bolsas abrazadas como si fueran niños dormidos.",
                Parrafo.TRABAJO to "A esta hora sirve caldo con verdura de verdad, cortada en trozos que se ven, no en polvo. Los clientes miran el tazón antes de comer, como si desconfiaran. Luego ya no hablan. Un repartidor le pregunta qué es esa cosa naranja, y él le dice que es una zanahoria, como quien presenta a un pariente.",
                Parrafo.PENSAMIENTO to "{nombre} se acuerda del huerto de su abuela, arriba, cuando todavía había huertos arriba. Se acuerda del olor de la tierra mojada, que no ha vuelto a oler desde entonces. Una col no es gran cosa, piensa, pero esta huele un poco a eso, y con eso tiene para toda la noche.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "En el mercado flotante hay colas para la verdura, y las barcazas de las torres-granja van un palmo más hundidas que de costumbre. Los vendedores gritan precios que bajan cada hora. A medianoche, lo que queda se regala.",
                "Estos días el callejón huele distinto: a apio, a cebolla nueva, a tierra que ha viajado desde muy arriba. Hasta la tienda de té pone un cesto de rábanos en la puerta, y nadie sabe si es para vender o para mirar.",
                "La verdura de la cosecha dura poco: lo que no se come en tres días se pone negro. Los vecinos se pasan recetas de balcón a balcón, a gritos, y el callejón entero huele a sopa. {nombre} dice que eso es competencia desleal, y se ríe.",
            )),
        ),
        "compra_colibri" to unDia(
            Parrafo.MANANA to "Hoy los repartidores llegan al puesto con la aplicación de otro color: el verde de Colibrí se ha vuelto dorado, el de Áurea. Se enseñan unos a otros los mensajes de bienvenida y se ríen sin ganas. {nombre} no tiene la aplicación, pero entiende el dorado: es el color de los que mandan.",
            Parrafo.GENTE to "Esta noche los repartidores comen más rápido que de costumbre, mirando el reloj del ojo. Uno dice que la aplicación nueva cuenta hasta los segundos del caldo. Otro dice que eso es mentira. El primero dice que ya lo verán, y se va antes de terminar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que, para él, nada ha cambiado: sigue teniendo seis bancos, una olla y un letrero. Pero si a los repartidores les cuentan los segundos, se los contarán aquí también, en el banco, con el tazón en las manos. Esa noche sirve un poco más deprisa sin que nadie se lo pida, y se enfada consigo mismo.",
        ),
        "ausentes" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es la Noche de los Ausentes. En la puerta del puesto, como cada año, {nombre} pone una vela y el retrato de luz de sus padres, que flotan un palmo sobre el suelo y miran el callejón con cara de no reconocerlo. A su lado deja un tazón de caldo, por si tienen hambre.",
                Parrafo.PUEBLO to "El callejón se llena de velas y de retratos: viejos, niños, perros, un repartidor con su casco. Los vecinos se paran delante de los retratos de los demás y cuentan cosas de ellos, aunque no los conocieran. Esta noche nadie es un desconocido, ni siquiera los que ya no están.",
                Parrafo.PENSAMIENTO to "{nombre} mira a sus padres de luz y piensa que nunca vieron el callejón: murieron arriba, con sol. Piensa que, si lo vieran, les parecería oscuro y pequeño, y que su madre diría que el caldo está flojo de sal. Esa noche le echa un poco más, por si acaso.",
            ),
            dia(
                Parrafo.GENTE to "Segunda noche de los Ausentes. Celso cruza con el retrato de su mujer y lo deja un rato en la barra, junto al de los padres de {nombre}. Los dos hombres se toman un caldo sin hablar, mirando los retratos. Cuando se le enfría el suyo, Celso se lo bebe igual.",
                Parrafo.ANOCHECER to "Al final de la noche apaga el retrato de sus padres con un toque y guarda la vela, que todavía tiene para otro año. El tazón que dejó en la puerta está frío y entero. Lo vacía despacio en el desagüe, como quien devuelve algo prestado.",
            ),
        )),
        "descuentos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la Semana del Descuento, y en el callejón no se nota en nada, salvo en los repartidores, que llegan con cara de haber visto el infierno y de tener que volver mañana. {nombre} pone el caldo más espeso esta semana. No lo anuncia: el que lo prueba, lo sabe.",
                Parrafo.TRABAJO to "A esta hora los repartidores comen de pie, sin quitarse la caja de la espalda, y se van antes de terminar. Algunos le piden el caldo en un vaso, para llevar, y él se lo da, aunque le parezca un crimen. Uno se duerme sentado con los palillos en la mano, y lo dejan dormir diez minutos.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en todas las cajas que pasan esta semana por encima de su cabeza, por los puentes y las pasarelas, llenas de cosas que ayer nadie necesitaba. Él no ha comprado nada en años, salvo ollas. Piensa que es rico de una manera que en la ciudad no se cuenta.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "La ciudad sigue pidiendo, y por la pasarela de encima del callejón pasan repartidores cada minuto, haciendo temblar la rejilla. Cae polvo en las ollas, y él las tapa y las destapa como quien sube y baja una persiana.",
                "En las puertas del callejón se acumulan cajas vacías, y la de las pelucas las dobla y las apila para venderlas al peso. Dice que es su propia semana del descuento. Le va mejor que a muchos de arriba.",
                "Al final de la semana los repartidores tienen otra cara: más flaca, más callada, con más dinero. Algunos dejan propina en el tazón, por primera vez en el año. {nombre} la guarda en un frasco aparte, para cuando vuelva a hacer falta.",
            )),
        ),
        "nochevieja" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy es la última noche del año, y todos los letreros de la ciudad cuentan hacia atrás, a la vez. El del puesto no sabe contar: es un tazón. {nombre} le pega debajo un papel con los números escritos a mano, y lo va cambiando cada hora con un rotulador.",
                Parrafo.PUEBLO to "El callejón se llena antes de medianoche: vecinos, clientes, gente de paso que se queda porque aquí hay sitio en la escalera. La de las pelucas baja con su botella de todos los años. Cuando los letreros llegan a cero, el callejón grita, y de los balcones caen serpentinas que alguien guardaba desde el año pasado.",
                Parrafo.ANOCHECER to "A medianoche le sirve un tazón de caldo a la repartidora de la bici azul, que llega con el casco en la mano, justo a tiempo. Cuando la cuenta llega a cero, él levanta su vaso hacia ella sin decir nada, y ella levanta el tazón. Brindar con caldo, piensa, le sienta bien al año.",
            ),
            dia(
                Parrafo.GENTE to "El primer día del año el puesto abre tarde, y los primeros clientes llegan despeinados y con gafas oscuras, pidiendo caldo como quien pide perdón. {nombre} se lo sirve con mucho jengibre y sin preguntas. Es el día del año en que la gente más agradece y menos habla.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que un año más es un año más, ni bueno ni malo, como una olla de caldo: depende de lo que se le eche. Este, se dice, le echará más cebollín. Lo dice todos los años, y todos los años lo cumple, que es más de lo que pueden decir otros.",
            ),
        )),
        "olla_nueva" to unDia(
            Parrafo.MANANA to "Hoy llega la olla nueva, en una barcaza del mercado flotante, envuelta en papel como un recién nacido. Es de acero brillante, sin una sola abolladura, y en el callejón todos salen a verla. La vieja espera en un rincón, negra, con el fondo remendado tres veces.",
            Parrafo.TRABAJO to "A esta hora pasa el caldo de la olla vieja a la nueva, cucharón a cucharón, despacio, sin derramar una gota. Los clientes miran en silencio. Cuando termina, raspa con cuidado el fondo de la vieja y echa también lo que sale, que es lo más importante: lo que no se ve.",
            Parrafo.PENSAMIENTO to "{nombre} hace la cuenta: once años de caldo en esa olla, todas las noches. Piensa en toda la gente que comió de ella y que no volverá a ver. No sabe qué hacer con la vieja. Al final la sube al cuarto y la usa para guardar las cosas que no sabe dónde poner.",
        ),
        "inspeccion" to unDia(
            Parrafo.MANANA to "Hoy viene la inspección de sanidad: una mujer con un escáner y una cara que no ha probado un caldo en su vida. Llega sin avisar, como siempre, a la hora en que más gente hay. Los clientes se quedan quietos en los bancos, con los palillos en el aire, como si los fueran a multar a ellos.",
            Parrafo.TRABAJO to "A esta hora la inspectora le pasa el escáner a todo: a la olla, a los tazones, al trapo, a sus manos. El escáner pita dos veces. Ella apunta algo, lo mira, apunta otra cosa. {nombre} no respira hasta que le da un papel amarillo, que quiere decir que pasa, aunque por poco.",
            Parrafo.GENTE to "Cuando se va la inspectora, el callejón entero aplaude, y Celso cruza con un caldo para él, que por una vez se lo sirve otro. La de las pelucas dice que el trapo tiene que irse. {nombre} dice que el trapo se queda, que el trapo es de la familia. Mañana compra otro, y guarda el viejo.",
        ),
        "carta_hermana" to unDia(
            Parrafo.MANANA to "Hoy llega una carta de papel, de las de antes, con su nombre escrito a mano en el sobre. El cartero del nivel la trae doblada y mojada en una esquina, y se la entrega como quien entrega algo que no entiende. Es la letra de su hermana. Hace tantos años que casi no la reconoce.",
            Parrafo.GENTE to "La carta es corta: su hermana vive en otra ciudad, tiene dos hijos, y uno se llama como su padre. Le pregunta si sigue con los fideos. Le dice que se acuerda del sol en la azotea de cuando eran niños. {nombre} la lee tres veces y la guarda en el delantal, entre el cambio y el corazón.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en todo lo que tendría que contarle a su hermana y no sabe por dónde empezar: años de caldo, un callejón, una olla nueva. Piensa que escribir le va a costar más que hacer fideos a mano. Esa noche, en una hora floja, escribe la primera línea en el dorso de una ficha, y la tacha.",
        ),
        "radio" to unDia(
            Parrafo.MANANA to "Hoy Celso cruza la calle con una caja debajo del brazo y se la deja en la barra sin decir nada. Dentro hay una radio de las de antes, de madera, con una rueda para buscar las emisoras y una luz amarilla que se enciende detrás de los números. Huele a tienda vieja.",
            Parrafo.GENTE to "Celso dice que la encontró en el fondo del almacén de la farmacia y que no la necesita, que es lo que se dice cuando se regala algo que se ha buscado mucho. {nombre} le sirve un caldo que no le cobra. Los dos fingen que no ha pasado nada, que es como se agradecen las cosas en el callejón.",
            Parrafo.ANOCHECER to "Esta noche prueba la radio nueva cuando ya ha cerrado, girando la rueda despacio. Entre ruidos aparece una emisora que no conocía, que pone canciones viejas de arriba, de cuando él era niño. Se queda escuchando hasta que se le enfría el té. No sabe cómo se llama la emisora, y no quiere saberlo.",
        ),
        "doble_caldo" to unDia(
            Parrafo.MANANA to "Hoy, casi al final de la noche, llega al callejón una repartidora que no conoce, empujando una bici azul sin batería. Tiene la cara de quien no ha comido desde ayer. {nombre} le tiende un cable para la bici sin que ella lo pida, porque así se hace con los que llegan empujando.",
            Parrafo.TRABAJO to "A esta hora la repartidora se sienta en el banco de la punta, el que cojea, y pide fideos con doble caldo. Lo dice sin pensar, como quien pide lo de siempre en un sitio donde no ha estado nunca. {nombre} le llena el tazón hasta el borde y le pone más cebollín del que toca, sin saber por qué.",
            Parrafo.ANOCHECER to "Al cerrar, {nombre} se da cuenta de que no le ha preguntado el nombre. Da igual: si vuelve, ya sabe lo que pide. Celso, desde la farmacia, le pregunta quién era. Él dice que una del doble caldo, y Celso asiente, como si eso fuera un nombre, que en el callejón lo es.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "fideos", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
