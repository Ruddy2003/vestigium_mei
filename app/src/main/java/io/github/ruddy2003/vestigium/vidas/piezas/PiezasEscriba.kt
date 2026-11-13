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
 * El cuento de la escriba de la Orden del Candil, en Peñaseca, una ciudadela sobre una meseta del páramo, de 2287 a
 * 2291. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su ficha se llama «Una escriba»: el cuento escribe «la escriba» a mano y no usa el hueco del nombre.
 * - Todo es inventado y de ninguna obra: la Orden del Candil, la Cámara, el Código del Candil, la Garza, los
 *   caparazones, los candileros, el Fuego. Los nombres nuevos (Rosendo, el cocinero; Casilda, la enfermera; Baldo, el
 *   herrero) no chocan con los del calendario ni con los de su grupo.
 * - Come tres veces y trabaja dos (patrulla por la mañana; taller y archivo por la tarde): esos textos valen a
 *   cualquiera de sus horas o cuentan uno de los dos tramos sin decir la hora. Cada noche recita el Código (REZAR).
 * - Tres estados: «expedicion» (temporada 2, siete semanas en la Garza hasta la Bóveda), «encierro» (temporada 3, un
 *   mes en el archivo con el núcleo) y «herida» (temporada 4, la enfermería). Cada uno tiene mañana, pueblo, anochecer
 *   y un texto para cada cosa que hace (dormir, comer, trabajar, recitar).
 * - El pueblo lee la Garza cuando está rota (arco «garza») y, si no, la cisterna (arco «cisterna»).
 * - Cielos y estaciones no nombran ningún sitio de la ciudadela: también salen en el sur y en la enfermería.
 * - Fiestas de guardar (no trabaja): el año nuevo, la Fundación, la Vigilia de los Candiles y la Noche Larga.
 * - No repite lo que ya dice «Su vida»: que no pelea en primera línea, las armaduras que pesan más que una persona, las
 *   raciones de campaña y lo que se caza, las guardias que no la dejan dormir seis horas, que dentro de la orden todos
 *   leen, el continente cruzado en la nave.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG8.kt`):
 * - Jimena, desde `jimena` (temporada 1): aprende las letras desde `letras` (1), lee desde `primera_frase` (2), es
 *   candilera desde `jimena_jura` (4) y patrulla desde `jimena_patrulla` (5).
 * - Fermín, el custodio: ve hasta `ojos_fermin` (1), le da las llaves en `llaves` (3) y muere en `fermin_muere` (4).
 * - Ulises patrulla hasta `ulises` (3); desde entonces, en la fragua.
 * - Damián, el piloto: solo eso hasta `damian` (2); juntos desde entonces; la pierna rota de `garza_cae` (3) a `cuarto`
 *   (3); viven juntos desde `cuarto`; ella espera un hijo desde `espera` (4); Ciro, desde `ciro` (5).
 * - Nieves, su amiga siempre; manda la patrulla desde `emboscada` (4).
 * - El núcleo, desde que se enciende (`nucleo`, temporada 2) hasta la última página (`ultima_pagina`, 5).
 * - Mientras espera un hijo y después, ningún texto de todos los días la manda a las patrullas largas: esas llevan
 *   fecha (`hasta = "espera"`) y las de todos los días son la ronda del borde de la meseta, el taller y el archivo.
 * - Las fiestas que vuelven cada año no nombran a Jimena, a Fermín, a Ulises, a Damián, a Nieves ni a Ciro, ni dicen
 *   quién es el custodio. Sí a la maestre Amparo y a Rosendo, que están los cinco años.
 */
internal object PiezasEscriba {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(12, 1, 2)

    private val MANANA = listOf(
        v("A la escriba la despierta el relevo de la guardia: dos golpes de bota en la puerta y una voz ronca que dice la hora y el viento. Se sienta en el borde de la cama con los ojos cerrados y cuenta hasta diez. Al diez ya está de pie. Despierta, casi."),
        v("La escriba se despierta antes que la campana, con el frío del páramo metido en los pies, y se queda un momento escuchando la ciudadela: el viento en las almenas, una puerta, el martillo de la fragua que ya empezó. El martillo va a su ritmo de siempre. Todo en orden."),
        v("Lo primero que hace la escriba al levantarse es mirar la mecha de su candil, como le enseñaron de candilera: si está negra, se recorta; si está blanca, se deja. Hoy está negra. La recorta con las tijeras pequeñas que lleva colgadas del cuello. Así empieza el día en la orden."),
        v("A la escriba la despierta el olor del refectorio, que sube por la escalera de piedra: gachas de cebada, grasa de cabra y el humo de la estufa, que tira mal desde hace años. Se lava la cara en la palangana. El agua está tan fría que le duelen los dientes."),
        v("La escriba se despierta con un artículo del Código en la boca, el que se le queda pegado cada noche como una canción. Hoy es el séptimo: lo que no se entiende, no se usa. Lo dice en voz baja mientras se ata las botas. Las botas no lo entienden. Se dejan atar igual."),
        v("Al abrir los ojos, la escriba mira la ventana estrecha para saber qué día hace antes de saber qué día es. Una franja de cielo, una de páramo. Hoy el páramo está quieto. Eso, aquí, ya es una noticia."),
        v("La escriba se despierta con escarcha por dentro del cristal y el aliento hecho humo. Se viste debajo de la manta, prenda por prenda, sin sacar más que las manos. Fuera, en el patio, alguien rompe a golpes de pala el hielo del abrevadero de las cabras.", meses = INVIERNO),
        v("La escriba se despierta en su celda del ala de las escribas, tan estrecha que desde la cama se tocan las dos paredes. En una tiene clavado un mapa del páramo; en la otra, la lista de lo que debe arreglar. La lista es más larga que el mapa.", hasta = "cuarto"),
        v("La escriba se despierta en el cuarto que comparte con Damián, y él ya está despierto, mirando el techo con las manos detrás de la cabeza, calculando vientos. Le pregunta cómo viene el día. Él dice que del oeste. Siempre dice que del oeste. Casi siempre acierta.", desde = "cuarto", hasta = "ciro"),
        v("A la escriba la despierta Ciro, que no sabe nada de guardias ni de campanas y tiene hambre cuando le parece. Ella le da de comer a oscuras, sentada en la cama, oyendo el viento. Damián finge que duerme. Ella lo sabe. Mañana le tocará a él fingir peor.", desde = "ciro"),
    )

    /** Siete semanas en la Garza, rumbo al sur y en la Bóveda de Valdecruz, en la temporada 2 (estado «expedicion»). */
    private fun ex(texto: String, hacer: Hacer? = null, desde: String? = null) = v(texto, estado = "expedicion", hacer = hacer, desde = desde)

    private val EXPEDICION_MANANA = listOf(
        ex("La escriba se despierta en una hamaca de la góndola de la Garza, que se mece un poco, como todo aquí. Por el ojo de buey entra un páramo que no es el suyo, de otro color. La nave cruje como un barco viejo. Ulises ronca en la hamaca de al lado, con las botas puestas."),
        ex("A la escriba la despierta el frío de la góndola, que de noche no guarda nada de calor, y el olor del gas de la tela, dulce y raro. Nieves ya está sentada, afilando algo. Le pasa una taza de agua sin decir buenos días. Aquí los días no se desean: se cuentan."),
        ex("Lo primero que hace la escriba al despertarse es apuntar en el cuaderno de viaje el día, el viento y lo que se ve por el ojo de buey. Lo hace antes de pensar. Si un día la Garza no vuelve, alguien encontrará el cuaderno. Sabrá, por lo menos, qué tiempo hacía."),
    )

    private val EXPEDICION_TRABAJO = listOf(
        ex("A esta hora pasa a limpio, en la mesa plegable de la góndola, el mapa de lo que han visto desde el aire: un río seco, un puente partido, una carretera que se mete en la arena y no vuelve a salir. Damián, el piloto, le dice el nombre de lo que tiene nombre. Casi nada tiene.", Hacer.TRABAJO),
        ex("A esta hora cuenta y vuelve a contar lo que lleva la Garza: el agua, el gas, la comida, las piezas de recambio de los dos motores. Lo apunta en una tabla. Cada día hay una columna menos llena. Ulises la mira por encima del hombro y dice: llegamos. No dice adónde.", Hacer.TRABAJO),
        ex("A esta hora revisa, en la bodega de la Garza, los sacos de manuales que sacaron de la Bóveda, uno por uno, con guantes, porque las hojas se deshacen como hojaldre. Separa lo que se puede leer de lo que ya es polvo. El núcleo va aparte, envuelto en mantas. Nadie se sienta encima.", Hacer.TRABAJO, desde = "boveda"),
        ex("A esta hora come en la góndola, sentada en un cajón, galleta dura mojada en agua y un trozo de queso que ya suda. Ulises reparte, contando en voz alta. Damián come al timón, con una mano. Por la ventana se ve un páramo que no se parece al de casa.", Hacer.COMER),
        ex("A esta hora duerme en una hamaca de la góndola, que se mece con el viento aunque la Garza esté amarrada, y sueña que se cae. No se cae. Las hamacas de la Garza tienen una correa para eso. Alguien la inventó después de que alguien se cayera.", Hacer.DORMIR),
        ex("A esta hora recita el Código del Candil en la góndola, con los demás, de rodillas, porque de pie se dan con el techo. Sin la sala de piedra, las voces suenan pequeñas, tapadas por el viento en la tela. Lo dicen entero igual. Lejos de casa, el Código es la casa.", Hacer.REZAR),
    )

    private val EXPEDICION_PUEBLO = listOf(
        ex("La Garza es una ballena de tela gris, larga como diez casas, con una góndola de madera colgada debajo y dos motores a los lados que suenan como dos viejos discutiendo. En la góndola caben doce personas. Caben mal. Lejos de Peñaseca, es todo el mundo que tienen."),
        ex("El sur no se parece al páramo de casa: la tierra es blanca, los cañones son hondos y estrechos, y de vez en cuando asoma un pueblo muerto, con los techos hundidos y una gasolinera. Desde arriba se cuentan. La escriba los marca en el mapa con una cruz pequeña."),
    )

    private val EXPEDICION_ANOCHECER = listOf(
        ex("Al caer la tarde, la tripulación se asoma a las ventanas de la góndola a ver ponerse el sol en un páramo que no es el suyo. Damián dice que el sol es el mismo de Peñaseca. Ulises dice que no se fía. Nieves dice que se callen, que se lo pierden."),
        ex("Anochece lejos de Peñaseca, y la escriba escribe en el cuaderno de viaje la última línea del día: rumbo, viento, lo visto. Luego mira hacia el norte, donde no se ve nada. Allí, ahora, la orden enciende sus candiles. Ella enciende el suyo, para no ser la que falta."),
    )

    /** Un mes encerrada en el archivo con el núcleo, en la temporada 3 (estado «encierro»). */
    private fun en(texto: String, hacer: Hacer? = null) = v(texto, estado = "encierro", hacer = hacer)

    private val ENCIERRO_MANANA = listOf(
        en("La escriba se despierta en el catre del archivo, entre dos estantes, con el candil todavía encendido porque se durmió leyendo. Por la ventana alta entra una raya gris. Alguien ha metido por el ventanuco de la puerta la ración del día. La puerta sigue cerrada con llave."),
        en("A la escriba la despierta el zumbido del núcleo, que no se apaga nunca, como un gato que ronronea dentro de un cajón. Se queda mirando el techo, con las letras de ayer bailándole detrás de los ojos. El núcleo no ha dormido. Ella tampoco mucho."),
        en("La escriba se despierta y mira las rayas de carbón que hace en la pared, una por día, como los presos de los cuentos. No se siente presa. Se siente encerrada con un problema, que es distinto, se dice. Pone otra raya. Se lo vuelve a decir."),
    )

    private val ENCIERRO_TRABAJO = listOf(
        en("A esta hora está delante del núcleo, sola, con la pantalla verde que escupe letras sin sentido y un cuaderno lleno de tablas. Busca lo que se repite. Algo se repite siempre. Hoy encuentra una palabra que sale cada treinta líneas. No sabe qué es. Ya es suya.", Hacer.TRABAJO),
        en("A esta hora no hay patrulla, ni taller, ni refectorio: solo el archivo, el núcleo y las hojas que ya cubren el suelo en dos filas, como un camino. Lo recorre para pensar. Cuando se equivoca, recoge una hoja. Cuando acierta, pone otra delante.", Hacer.TRABAJO),
        en("A esta hora le escribe a la maestre la nota del día, como pide el trato: cuántas líneas, cuántas dudas, cuánta comida queda. La pasa por debajo de la puerta. Al rato, por el mismo sitio, entra un papel con una sola palabra de la maestre Amparo: sigue.", Hacer.TRABAJO),
        en("A esta hora come la ración contada que entra por el ventanuco de la puerta: un pan, un cuenco de lentejas, un trozo de queso y una jarra de agua. Lo pone todo encima de un manual cerrado, que hace de mesa. Come sin mirar. Los ojos siguen en la pantalla.", Hacer.COMER),
        en("A esta hora duerme en el catre del archivo con el zumbido del núcleo de fondo y los números del día dando vueltas. A veces se despierta con una idea y la apunta a ciegas en la pared. Por la mañana, la mitad de las ideas son buenas. La otra mitad, ilegibles.", Hacer.DORMIR),
        en("A esta hora recita el Código del Candil sola, entre los estantes, en voz alta, porque sabe que al otro lado de los muros la orden lo dice a la vez. Se adelanta. Se espera. Al final no sabe si ha ido con ellos. Le basta con creer que sí.", Hacer.REZAR),
    )

    private val ENCIERRO_PUEBLO = listOf(
        en("El archivo es una sala larga sin más ventana que una alta, con estantes de hierro hasta el techo y una mesa en medio. Ahora hay también un catre, una jarra, un cubo y el núcleo, que zumba en su caja. La puerta tiene la llave por fuera. La llave la tiene la maestre."),
        en("Por la ventana alta del archivo se oye la ciudadela sin verla: la fragua, la campana, los pasos del patio, la risa de los candileros. La escriba aprende a saber la hora por los ruidos. A los tres días ya no se equivoca. A los diez, ya no los oye."),
    )

    private val ENCIERRO_ANOCHECER = listOf(
        en("Al caer la tarde la ventana alta del archivo se pone naranja, luego gris, y la escriba enciende el candil, que se quedará encendido toda la noche. Desde el patio, le han contado, su ventana parece un ojo abierto. Los candileros dicen que la escriba no duerme. Casi aciertan."),
        en("Anochece, y por debajo de la puerta del archivo entra una hoja doblada: un candil dibujado con carbón, sin firma. Ayer fue un pájaro. La escriba los pega en el estante con cera. No sabe quién los manda. Sospecha de todos, que es una manera de quererlos."),
    )

    /** Cinco semanas en la enfermería después de la emboscada, en la temporada 4 (estado «herida»). */
    private fun he(texto: String, hacer: Hacer? = null) = v(texto, estado = "herida", hacer = hacer)

    private val HERIDA_MANANA = listOf(
        he("La escriba se despierta en la enfermería con el costado tirante, como si alguien le hubiera cosido una cuerda por dentro, y alguien lo hizo. Casilda, la enfermera, ya pasa entre los catres con la palangana. Le pregunta cómo ha dormido. La escriba miente. Casilda lo apunta igual."),
        he("A la escriba la despiertan las botas de la patrulla en el camino, debajo de la ventana: la patrulla que sale sin ella. Cuenta los pasos. Conoce los de Nieves, que pisa fuerte con la derecha. Cuando ya no se oyen, se queda mirando el techo un rato largo."),
        he("La escriba se despierta y lo primero que hace es tocarse la venda, para ver si está seca. Está seca. Cada mañana seca es una victoria pequeña, de las que Casilda celebra con un gruñido. Hoy gruñe. La escriba sonríe con media cara: la otra media le duele."),
    )

    private val HERIDA_TRABAJO = listOf(
        he("A esta hora no patrulla ni baja al taller: está en el catre de la enfermería, con el costado vendado y el cuaderno sobre las rodillas, apuntando de memoria lo que vio en el paso del Cuervo. Las piedras, las sombras, el primer disparo. Las caras no. Las caras no las apunta.", Hacer.TRABAJO),
        he("A esta hora Nieves se sienta a los pies del catre y le cuenta la patrulla de hoy, paso a paso, porque la escriba se lo pide: qué camino, qué huellas, qué viento. La escriba lo apunta. Nieves manda la patrulla ahora, y todavía da el parte como quien pide permiso.", Hacer.TRABAJO),
        he("A esta hora Jimena le sube del taller cosas pequeñas para que no se aburra: un reloj sin tapa, una lámpara rota, un puñado de tornillos. La escriba los arregla en una bandeja, encima de la manta. Casilda dice que eso no es reposo. La escriba dice que es reposo con tornillos.", Hacer.TRABAJO),
        he("A esta hora come en el catre lo que Casilda le pone delante: caldo de huesos, pan mojado y una pasta de lentejas que sabe a enfermería. Casilda se queda hasta que se lo termina. Es la única orden de la ciudadela que no discute nadie, ni la maestre.", Hacer.COMER),
        he("A esta hora duerme a medias, porque cada vez que se da la vuelta el costado le avisa. En la enfermería hay otros dos heridos que se quejan en sueños. Ella cuenta sus quejas como contaría estrellas. A la tercera vuelta, se duerme.", Hacer.DORMIR),
        he("A esta hora recita el Código desde el catre, bajito, mientras por el pasillo llega el rumor de la sala grande. Casilda lo dice con ella, sentada en un taburete, sin dejar de doblar vendas. En el artículo de los heridos, las dos se callan un momento.", Hacer.REZAR),
    )

    private val HERIDA_PUEBLO = listOf(
        he("La enfermería está en la planta baja del ala sur: seis catres, una estufa, una mesa con frascos y una ventana que da al camino de las patrullas. Huele a vinagre, a lana y a caldo. El catre de la escriba es el de la ventana. Casilda dice que así se cura antes. No dice por qué."),
        he("Desde el catre de la enfermería la ciudadela es un ruido: la campana, la fragua, las cabras, la patrulla que sale y la que vuelve. La escriba ha aprendido a reconocerlos todos. Le falta uno, el suyo: el de sus botas en la piedra."),
    )

    private val HERIDA_ANOCHECER = listOf(
        he("Al caer la tarde, desde el catre, la escriba ve volver la patrulla por el camino, cubierta de polvo, con Nieves delante. Cuenta las cabezas. Están todas. Solo entonces se deja caer del todo en la almohada."),
        he("Anochece, y Casilda enciende el candil de la enfermería y lo pone lejos de los catres, porque dice que los heridos miran mucho la llama y se ponen tristes. La escriba lo mira igual, de reojo. No se pone triste. Se pone impaciente."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el páramo, dicen los caballeros, el cielo se mira antes que el camino."),
        v("La escriba mira el cielo antes que nada, como todos en la orden, porque en el páramo el tiempo es medio trabajo. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En el archivo hay un libro de antes del Fuego que adivinaba el tiempo con números. Ya no acierta."),
        v("{cielo} En {estacion} el páramo cambia de color, pero no de humor: sigue sin querer a nadie."),
        v("{de_estacion} {cielo} Los caballeros huelen el polvo antes de verlo. La escriba todavía no."),
        v("En el páramo el tiempo se ve venir de lejos, con medio día de ventaja: no hay nada que lo tape. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y alto, de un azul duro, y la luz pega en las piedras como un martillo.",
            "No hay una nube, y el horizonte se ve entero, recto, como trazado con regla.",
            "Hace un día claro, de los que dejan ver lo lejos: mesetas, torres caídas y nada más.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto de nubes grises que pasan altas y no sueltan nada.",
            "Hay nubes bajas sobre el páramo, y sus sombras corren por el suelo como rebaños.",
            "Está nublado, con una luz blanca que aplasta los colores y no deja sombras.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, una nieve seca y fina que el viento arrastra por el suelo como arena blanca.",
            "Cae nieve, y el páramo se queda callado, como si alguien le hubiera echado una manta.",
            "Nieva despacio, y las piedras negras asoman entre lo blanco como letras en una página.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento del oeste que no para, y lo que no está atado acaba en otro sitio.",
            "Hace viento, y silba en cada grieta y en cada argolla con una nota distinta.",
            "El viento empuja de lado, y hay que andar inclinado, como quien sube una cuesta que no existe.",
        ),
        Clima.POLVO to listOf(
            "Hay polvo en el aire, un polvo ocre que tapa el sol y se mete en los dientes.",
            "Sopla el viento del polvo, y el cielo se vuelve del color del cobre viejo.",
            "El aire está espeso de polvo, y a cien pasos ya no se ve nada, ni el suelo.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, cosa rara, y el páramo huele a piedra mojada y a algo verde que no se ve.",
            "Cae una lluvia corta y fuerte, y la tierra seca no sabe qué hacer con ella.",
            "Llueve, y el agua corre en hilos rojos por las grietas del suelo.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, y los rayos caen sobre el páramo uno tras otro, tan lejos que no se oyen.",
            "Truena, el cielo se pone morado y llueve de golpe, como si se volcara un cubo.",
            "Una tormenta cruza el páramo, y huele a hierro quemado antes del primer rayo.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en las grietas del páramo salen unas flores amarillas que duran una semana.",
            "En primavera vuelven los vientos y, con ellos, el polvo de cada año.",
            "Es el tiempo de las crías de cabra y de los caminos que se secan.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el páramo arde de día y se queda helado de noche, como siempre.",
            "En verano llegan las tormentas, las únicas que traen agua de verdad.",
            "Es el tiempo de los días largos, de las lagartijas y de la sed.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y el viento se vuelve frío y limpio, y el aire deja ver más lejos.",
            "En otoño se sala la carne, se remiendan las mantas y se cuentan las velas.",
            "Es el tiempo de las últimas caravanas y de las patrullas largas.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y el páramo se queda quieto y gris, con escarcha en las piedras hasta tarde.",
            "En invierno se patrulla menos y se lee más.",
            "Es el tiempo del frío seco que agrieta los labios y las manos.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora está en el taller de la Cámara, con una máquina de antes del Fuego abierta sobre la mesa como un animal en la tabla del carnicero. Saca los tornillos uno a uno y los pone en fila, por orden, sobre un trapo. El orden es la mitad del trabajo. La otra mitad es acordarse.", hacer = Hacer.TRABAJO),
        v("A esta hora copia en el archivo un manual que se deshace, página a página, con la pluma y una lupa. Copia también los dibujos: un tornillo, una flecha, una mano que gira algo. No sabe qué gira la mano. Lo copia igual. Algún día alguien lo sabrá.", hacer = Hacer.TRABAJO),
        v("A esta hora limpia con un pincel y alcohol las tripas de una radio vieja que trajo la patrulla, llena de arena hasta en los agujeros más pequeños. Sopla. Sale arena. Vuelve a soplar. Al final, la radio hace un ruido como de mar. Es un buen ruido.", hacer = Hacer.TRABAJO),
        v("A esta hora hace la ronda del borde de la meseta con dos caballeros, despacio, mirando abajo con el catalejo: el camino, el pozo, las rocas donde se esconde quien se esconde. Apunta en el cuaderno: nada. La nada se apunta con la misma letra que todo lo demás.", hacer = Hacer.TRABAJO),
        v("A esta hora ordena en el archivo lo que trajo la patrulla, sobre una mesa larga: piezas, papeles, un zapato de niño. Cada cosa lleva su etiqueta con un número. El zapato también. En la orden no se decide qué es basura hasta que se ha apuntado.", hacer = Hacer.TRABAJO),
        v("A esta hora va de patrulla por el páramo, detrás de los caballeros, con el cuaderno colgado al cuello y los ojos en el suelo. Apunta lo que ve: huellas, una lata, un cable que asoma de la tierra. Un cable puede ser nada. O una ciudad enterrada.", hacer = Hacer.TRABAJO, hasta = "espera"),
        v("A esta hora baja con la patrulla a una casa hundida en la arena, por un hueco que era una ventana, con una cuerda atada a la cintura. Abajo huele a encierro y a ratón. Encuentra una caja de libros. Los ratones la encontraron antes. Salva tres.", hacer = Hacer.TRABAJO, hasta = "espera"),
        v("A esta hora consulta el terminal del archivo, que zumba y tarda, como un viejo al que se le pregunta algo. Escribe una palabra. Espera. La pantalla verde le devuelve una lista de nueve manuales, tres de ellos perdidos. Apunta los otros seis. Los perdidos también.", hacer = Hacer.TRABAJO, desde = "terminal"),
        v("A esta hora repara un aparato en el banco de la Cámara mientras Jimena, a su lado, copia letras en la pizarra y le pregunta para qué sirve la ka. La escriba no lo sabe. Le dice que para las cosas raras. Jimena escribe kabra. Fermín, desde su banco, se ríe.", hacer = Hacer.TRABAJO, desde = "letras", hasta = "primera_frase"),
        v("A esta hora está delante del núcleo, en la Cámara, con la pantalla verde que escupe letras y un cuaderno al lado lleno de tablas. Prueba una clave. Basura. Prueba otra. Basura con comas. Las comas, piensa, son un progreso.", hacer = Hacer.TRABAJO, desde = "nucleo", hasta = "ultima_pagina"),
        v("A esta hora vuelve de la patrulla con el cuaderno al cuello y un trapo en el hombro que no es suyo sino de Ciro, y que se le olvidó quitarse al salir. Nieves lo ha visto a la primera piedra. No ha dicho nada. Al rato le ha pasado su pañuelo, por si acaso.", hacer = Hacer.TRABAJO, desde = "ciro"),

        v("A esta hora come en el refectorio, en el banco largo de las escribas, un cuenco de gachas de cebada con un trozo de cabra salada encima. Se puede comer de pie si hay prisa. Hoy hay prisa. Se sienta igual: la cabra está dura y pide tiempo.", hacer = Hacer.COMER),
        v("A esta hora come en el taller sin soltar del todo lo que tenía entre manos: pan negro, queso de cabra y una cebolla cruda que muerde como una manzana. Las migas caen en la mesa de las piezas. Las sopla. Una arandela sale volando con ellas.", hacer = Hacer.COMER),
        v("A esta hora come en la cocina, de pie junto al fogón, la sopa que Rosendo le sirve antes que a nadie porque una vez le arregló el molinillo. Rosendo no lo dice. Lo hace. Cada vez le echa más grasa, como si la escriba se fuera a romper.", hacer = Hacer.COMER),
        v("A esta hora come en el refectorio con toda la orden, cien cucharas contra cien cuencos, un ruido de lluvia de metal. El lector de turno lee en voz alta un trozo de manual: cómo se cambia un fusible. Todos lo saben ya. Lo escuchan igual, con la boca llena.", hacer = Hacer.COMER),

        v("A esta hora duerme, por fin, con la ropa puesta y las botas al lado de la cama, en fila, como le enseñaron: si suena la campana, se meten los pies sin mirar. Duerme hondo y corto. En la orden nadie duerme mucho; se duerme bien.", hacer = Hacer.DORMIR),
        v("A esta hora hace su turno de guardia en la muralla, envuelta en una manta, con el candil tapado para que no se vea desde abajo. Mira el páramo negro. Cuenta estrellas para no dormirse. Cuando llega a cien, vuelve a empezar: a cien se duerme siempre.", hacer = Hacer.DORMIR),
        v("A esta hora duerme y sueña que el manual que copia tiene una página más de las que tiene, y que esa página lo explica todo. Se despierta antes de leerla. Apunta a oscuras en el cuaderno: página más. Por la mañana no entiende su propia letra.", hacer = Hacer.DORMIR),
        v("A esta hora la despierta el relevo para su guardia, y sube a la torre con los ojos cerrados, contando los escalones, que son ochenta y uno. Arriba hace frío. Abajo, la ciudadela duerme entera. Es la hora en que Peñaseca es suya.", hacer = Hacer.DORMIR),

        v("A esta hora está en la sala del Código con toda la orden, de pie, cada uno con su candil, recitando de memoria el Código del Candil. Cuarenta artículos. Ella los sabe todos, y aun así hay uno, el diecinueve, en el que siempre se le adelanta la lengua. Hoy también.", hacer = Hacer.REZAR),
        v("A esta hora recita el Código del Candil con los demás, y las voces de cien personas en la sala de piedra suenan como una sola, grave, que tiembla en el pecho. Cuando se acaba la última frase, nadie se mueve. Se oye el viento. Luego, todos a la vez, apagan los candiles.", hacer = Hacer.REZAR),
        v("A esta hora, después del Código, se queda en la sala con las escribas para leer en voz alta el artículo de la semana y discutirlo, como manda la costumbre. Hoy discuten una coma. La discusión dura una hora larga. Gana la coma.", hacer = Hacer.REZAR),
        v("A esta hora recita el Código por el pasillo, camino de la guardia, porque llega tarde a la sala, y lo dice bajito, deprisa, como quien cuenta monedas. Una candilera la oye y se le une. Terminan juntas en la puerta de la muralla, sin aliento y con la última palabra a la vez.", hacer = Hacer.REZAR),
    )

    private val PUEBLO = listOf(
        v("{entorno} Peñaseca está en lo alto de una meseta de roca roja, con paredes que caen a pico por tres lados y, por el cuarto, una escalera tallada que sube del llano. Se sube despacio. A quien sube deprisa lo ven desde arriba mucho antes de que llegue."),
        v("La Cámara está en el corazón de la ciudadela, una sala de muros gruesos con una sola ventana y una puerta de hierro con tres cerraduras. Dentro hace siempre el mismo frío. Huele a aceite, a papel y a metal dormido. Las máquinas esperan en estantes, con su etiqueta, como libros que no se abren. {entorno}"),
        v("{entorno} El refectorio es una nave larga de piedra con dos mesas y una estufa en medio que tira mal. En la pared del fondo está pintado el primer artículo del Código, con letras de un palmo que el humo ha ido borrando. Nadie las repinta. Todos se lo saben."),
        v("La fragua está junto a la muralla del oeste, y su martillo marca el día de la ciudadela mejor que la campana. Ahí se arreglan los caparazones: placas abolladas, motores que tosen, correas rotas. Huele a carbón y a aceite quemado. {entorno}"),
        v("{entorno} La torre de amarre es lo más alto de Peñaseca: una aguja de hierro y piedra con una argolla arriba del tamaño de una rueda de carro. Desde lo alto se ve el páramo entero, y los niños dicen que también el mar. No se ve el mar. Pero lo dicen."),
        v("Las murallas de Peñaseca son de piedra del páramo sin labrar, levantadas por los primeros de la orden sin ninguna máquina. En las almenas hay muescas, una por cada año. La escriba las ha contado dos veces y le han salido dos números distintos. {entorno}"),
        v("{entorno} En la mesa de la Cámara, junto a la ventana, está el núcleo de Valdecruz: una caja negra del tamaño de un pan, con una luz verde que no se apaga nunca. Alrededor, montones de hojas copiadas, sujetas con piedras. La pila crece despacio, como crece la nieve.", desde = "nucleo"),
    )

    /** La Garza mientras está rota en el patio (arco «garza») y, si no, la cisterna (arco «cisterna»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("garza", 0.0, 0.15, frases = listOf(
            "La Garza sigue tendida en el patio, desinflada, con la tela rota y unos andamios alrededor como costillas.",
            "En el patio, la Garza es un montón de tela gris y un armazón torcido que nadie se atreve a mover.",
        )),
        Entorno("garza", 0.15, 0.99, frases = listOf(
            "En el patio cosen la tela nueva de la Garza, y el armazón ya tiene media piel.",
            "La Garza crece en el patio costura a costura, cada día un poco más parecida a sí misma.",
        )),
        Entorno("cisterna", 0.0, 0.3, frases = listOf(
            "La cisterna está tan baja que el agua se reparte con cazo, y cada cazo se apunta.",
            "En la cisterna ya se ve el fondo, verde, y el agua se raciona por cuencos.",
        )),
        Entorno("cisterna", 0.3, 0.7, frases = listOf(
            "La cisterna está a medias, y en su escalera el agua llega por el peldaño doce.",
            "El agua de la cisterna da para lo justo, sin derroches y sin miedo.",
        )),
        Entorno("cisterna", 0.7, 1.0, frases = listOf(
            "La cisterna está llena hasta el borde, y el agua suena en los canales como una fiesta.",
            "La cisterna rebosa, y los candileros se pelean por bajar a medirla.",
        )),
    )

    private val GENTE = listOf(
        v("La maestre Amparo manda la orden desde antes de que la escriba jurara: una mujer pequeña, de pelo blanco cortado a cuchillo, que habla poco y nunca dos veces lo mismo. Cuando pasa por el taller, todos trabajan más deprisa. Ella no lo pide. Le basta con pasar."),
        v("Nieves es caballera, y su amiga desde que eran candileras y se escapaban a la torre a ver las tormentas. Lleva el caparazón como otras llevan un abrigo. Se ríe fuerte, come mucho y no ha perdido nunca un pulso. La escriba tampoco ha ganado ninguno."),
        v("Rosendo, el cocinero, lleva treinta años en la cocina de Peñaseca y sabe hacer once platos con cabra y ninguno sin ella. Dice que el Código le parece bien, pero que ningún artículo habla de la sal. Lo considera un fallo grave."),
        v("Casilda, la enfermera, cose heridas mejor que nadie y lo sabe. Tiene unas gafas de antes del Fuego que la escriba le ha arreglado dos veces, y por eso le perdona cosas: que coma tarde, que duerma poco. Otras no se las perdona. Las lleva apuntadas."),
        v("Baldo, el herrero, es un hombre enorme y tímido que endereza las placas de los caparazones a martillazos y luego les pide perdón. La escriba le lleva piezas imposibles. Él las mira mucho, se rasca la barba y dice que no. Al día siguiente las tiene hechas."),
        v("Los candileros de este año son una docena, de catorce a dieciséis, y van a todas partes en grupo, como gorriones. Le preguntan a la escriba cosas que no se preguntan, como para qué servía una máquina. Ella les dice lo que sabe. Y lo que no sabe, también se lo dice."),
        v("Ulises es el caballero más viejo de la patrulla, con un caparazón que tiene más remiendos que placas y una rodilla que cruje. Lleva cuarenta años en el páramo y dice que conoce cada piedra. Las piedras, por cómo tropieza, no lo conocen a él.", hasta = "ulises"),
        v("Ulises colgó el caparazón en la pared de la fragua, y ahí sigue, limpio, como un hombre sentado. Él trabaja al lado, con Baldo, enderezando las placas de los demás. Cuando la patrulla sale, se asoma a la puerta. Cuando vuelve, también.", desde = "ulises"),
        v("Fermín, el custodio de la Cámara, fue su maestro y lo sigue siendo: un viejo flaco que huele a aceite y a papel, con las llaves colgadas del cinturón. Lee con el brazo estirado del todo. Dice que es el brazo, que se le ha acortado.", hasta = "ojos_fermin"),
        v("Fermín ya no ve las letras pequeñas, ni las medianas. La escriba le lee los manuales en voz alta, en el banco de la Cámara, y él corrige de memoria lo que ella lee mal. Nunca se equivoca. Ella no lee tan mal. Deja que él lo crea.", desde = "ojos_fermin", hasta = "llaves"),
        v("Fermín, ciego ya, le dio las llaves de la Cámara y no ha dejado de bajar a ella. Se sienta en su banco y escucha el taller: sabe por el ruido qué está desmontando la escriba y si lo hace bien. Cuando no, carraspea.", desde = "llaves", hasta = "fermin_muere"),
        v("Fermín murió, y su banco de la Cámara sigue en su sitio, porque nadie se atreve a moverlo. La escriba deja encima, de vez en cuando, el manual que está leyendo, abierto. Es una costumbre tonta. No piensa dejarla.", desde = "fermin_muere"),
        v("Jimena, la niña que encontraron sola en la gasolinera, tiene once años y unos ojos que lo miran todo como si fuera a desaparecer. Come a escondidas, guarda pan en los bolsillos y sigue a la escriba a todas partes, a dos pasos. Si la escriba se para, ella también.", desde = "jimena", hasta = "letras"),
        v("Jimena aprende las letras en el banco de la Cámara, con una pizarra y un trozo de yeso. Ya las sabe todas menos la equis, que dice que es una letra que se ha caído. Escribe su nombre en todas partes, paredes incluidas. Rosendo la persigue con un trapo.", desde = "letras", hasta = "primera_frase"),
        v("Jimena ya lee sola, despacio, moviendo los labios, y lee todo lo que encuentra: etiquetas, cajas, el Código pintado en el refectorio. Quiere jurar como candilera. Pregunta cada semana cuánto falta. La escriba le dice que menos que la semana pasada.", desde = "primera_frase", hasta = "jimena_jura"),
        v("Jimena es candilera, con su candil de latón y un uniforme que le queda ancho de hombros; se lo arregló la escriba, mal. Entrena en el patio y es la que más se cae y la primera que se levanta. Todavía no sale de patrulla. Lo pide cada día.", desde = "jimena_jura", hasta = "jimena_patrulla"),
        v("Jimena patrulla ya, con el candil de los que empiezan colgado del cinto y un cuaderno al cuello, como la escriba. Al volver le enseña lo que ha apuntado. La letra es torcida. Lo que ha visto, no: lo ha visto todo.", desde = "jimena_patrulla"),
        v("Damián es el piloto de la Garza, un hombre alto que habla poco y mira mucho al cielo, como si esperara a alguien. Cuando la escriba sube a la torre a revisar la radio, él está allí, engrasando un motor. Se saludan con la cabeza. Hasta ahí llega todo.", hasta = "damian"),
        v("Damián, el piloto, y la escriba están juntos desde el viaje al sur, aunque cada uno duerme en su ala. Se ven en la torre, entre los motores de la Garza, donde no sube nadie. Él le enseña nombres de estrellas. Ella le enseña nombres de tornillos. Van empatados.", desde = "damian", hasta = "garza_cae"),
        v("Damián tiene la pierna rota desde la tormenta que tumbó la Garza, entablillada con dos varillas de la propia nave. Se pasa el día con la pierna en alto, protestando, mirando por la ventana hacia el patio. La escriba le lleva noticias de la Garza como quien lleva medicinas.", desde = "garza_cae", hasta = "cuarto"),
        v("Damián y la escriba viven juntos en un cuarto pequeño al pie de la torre, con una ventana, dos catres juntados y un plano de la Garza clavado en la pared. Él cojea un poco cuando hace frío. Ella le arregla las botas. Él le arregla el humor.", desde = "cuarto", hasta = "espera"),
        v("La escriba espera un hijo, y Damián se ha vuelto un hombre que pregunta cosas: si tiene frío, si tiene hambre, si ha comido. Ella dice que sí a todo para que se calle. Él se calla un rato. Luego vuelve a preguntar.", desde = "espera", hasta = "ciro"),
        v("Ciro, el hijo de la escriba y Damián, tiene los puños apretados y una voz que llega hasta la fragua. Duerme en un cajón de manuales vaciado y forrado de lana. Damián dice que es la cuna más culta del páramo. La escriba le ha puesto una etiqueta.", desde = "ciro"),
        v("Nieves manda la patrulla desde el día del paso del Cuervo, cuando cargó con la escriba herida hasta la ciudadela. No habla de ese día. Cuando la escriba intenta darle las gracias, Nieves cambia de tema y le pregunta qué hay de comer.", desde = "emboscada"),
    )

    private val OBJETO = listOf(
        v("En la orden cada cosa tiene su número, su sitio y su historia. {dato_objeto}"),
        v("{dato_objeto} La escriba lo aprendió de candilera, en su primer año, a fuerza de equivocarse."),
        v("Hoy la escriba se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} En el páramo, lo que no se cuida se pierde, y lo que se pierde no vuelve."),
        v("Las cosas de antes del Fuego se cuentan por piezas; las de ahora, por manos. {dato_objeto}"),
        v("{dato_objeto} El Código lo dice casi así: lo que sirve se guarda, y lo que se guarda se cuida."),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("pluma", "la pluma", "una pluma de acero", "La pluma",
            "La pluma de la escriba es de acero, de una caja de antes del Fuego que tenía cien iguales; en la Cámara quedan treinta y cuatro, y cada escriba recibe una al jurar y la devuelve el día que deja de escribir."),
        ObjetoCuento("cuaderno", "el cuaderno", "un cuaderno de trapo", "El cuaderno",
            "Los cuadernos de la orden se hacen en la ciudadela con papel de trapo, cosidos a mano; las escribas llevan uno al cuello en cada patrulla, atado con un cordón, porque una mochila se pierde y un cuello casi nunca."),
        ObjetoCuento("lupa", "la lupa", "una lupa", "La lupa",
            "La lupa de la escriba tiene la lente de un aparato de antes del Fuego que nadie sabe qué hacía, montada en un aro de latón de la fragua; con ella se leen los números grabados en las piezas, que son la mitad de su nombre."),
        ObjetoCuento("destornillador", "el destornillador", "un destornillador", "El destornillador",
            "El destornillador es la herramienta que más se pide en la Cámara: las máquinas de antes del Fuego tienen tornillos de cien cabezas distintas, y la escriba lleva en el cinturón un mango con nueve puntas que se cambian."),
        ObjetoCuento("radio", "la radio", "una radio de patrulla", "La radio",
            "La radio de patrulla pesa como un niño de dos años y se lleva a la espalda por turnos; habla con la torre de Peñaseca hasta un día de camino, y más allá solo llega la de la Garza, que tiene una antena como una caña de pescar."),
        ObjetoCuento("cantimplora", "la cantimplora", "una cantimplora", "La cantimplora",
            "Las cantimploras de la orden son de aluminio viejo, abolladas, con el nombre del dueño rayado en el costado; en patrulla se bebe por turnos, a la señal del que manda, y quien bebe sin señal friega el refectorio una semana."),
        ObjetoCuento("racion", "la ración", "una ración de patrulla", "La ración",
            "La ración de patrulla es una bolsa de tela con pan de cebada cocido dos veces, carne seca y un puñado de pasas; dura tres días si se come bien, y cinco si se come como manda el Código."),
        ObjetoCuento("manual", "el manual", "un manual de antes del Fuego", "El manual",
            "Los manuales de antes del Fuego están escritos en una lengua parecida a la de ahora, pero llena de palabras que ya no nombran nada; la escriba lleva su propio diccionario, a mano, con dos mil palabras adivinadas."),
        ObjetoCuento("candil", "el candil", "un candil de latón", "El candil",
            "El candil es el signo de la orden y también su lámpara: un vaso de latón con aceite de cabra y una mecha; cada miembro tiene el suyo, y cuando alguien muere se le apaga en la sala del Código, delante de todos."),
        ObjetoCuento("brujula", "la brújula", "una brújula", "La brújula",
            "La brújula de la patrulla es de antes del Fuego, con la aguja temblorosa y el cristal rayado; cerca de ciertas ruinas se vuelve loca y gira, y los caballeros la usan para saber dónde hay metal enterrado."),
        ObjetoCuento("soldador", "el soldador", "un soldador", "El soldador",
            "El soldador grande del taller es una punta de cobre que se calienta en la fragua y se usa deprisa, antes de que se enfríe; para las piezas pequeñas la escriba tiene otro, que funciona con una batería del tamaño de un ladrillo."),
        ObjetoCuento("codigo", "el Código", "una copia del Código", "El Código",
            "El Código del Candil tiene cuarenta artículos y cabe en una hoja; el original, escrito por los primeros de la orden, está en la sala del Código, detrás de un cristal, y nadie lo ha tocado en un siglo."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} La escriba piensa en esas cosas mientras camina, y luego las guarda donde lo guarda todo: en una lista. A sus {edad} años sabe que casi todo lo que asusta cabe en una lista. Y que lo que no cabe es lo que de verdad hay que mirar."),
        v("{mundo} La escriba piensa en los de antes del Fuego, que tenían máquinas para todo y no supieron guardarlas. Piensa que no eran tontos. Piensa que eso es lo que más miedo le da."),
        v("Piensa en su pueblo, al otro lado del páramo, y en su madre, que la dejó irse con la orden sin llorar delante. Piensa que lloró después. Piensa que un día volverá a contarle todo lo que ha visto. Lleva años pensándolo. El páramo sigue igual de ancho."),
        v("{mundo} A sus {edad} años, la escriba sabe abrir una máquina de antes del Fuego y volverla a cerrar sin que sobre un tornillo. Piensa que con la gente no le sale tan bien. Siempre le sobra algo. O le falta."),
        v("{mundo} La escriba piensa en el Código, que dice que el saber se guarda para que nadie lo use mal. Piensa: ¿y para que alguien lo use bien? El Código no lo dice. Piensa que esa pregunta, en voz alta, sería una falta. La deja en voz baja."),
        v("Piensa en lo que ha arreglado este año y en lo que no. Lo que no, lo recuerda mejor: una radio que nunca habló, un reloj que se negó. Piensa que los fracasos enseñan más. Piensa que ojalá enseñaran menos y más deprisa."),
        v("La escriba piensa en el núcleo, que habla en una lengua de números que nadie ha oído desde antes del Fuego. Piensa que alguien lo escribió para que alguien lo leyera. Piensa que ese alguien, sin saberlo, era ella. Le da un poco de vértigo. Y de orgullo.", desde = "nucleo", hasta = "ultima_pagina"),
        v("Piensa en la máquina de los planos, la que saca agua del aire, y en lo que diría el Código: guardarla. Piensa en la cisterna, que casi tocó fondo. Piensa que el Código lo escribió gente que tenía agua.", desde = "ultima_pagina"),
        v("Piensa en Ciro, que ha nacido en una ciudadela llena de máquinas que nadie usa. Piensa que le enseñará a leer antes que a andar. Damián dice que primero a andar. Lo discuten cada noche. Ciro, mientras, no hace ninguna de las dos cosas.", desde = "ciro"),
    )

    private val MUNDOS = mapOf(
        "patrullas" to listOf(
            "En el refectorio se habla de unos saqueadores del norte que llevan camiones de antes del Fuego y no saben arreglarlos.",
            "Se dice en la fragua que este año las patrullas encuentran más cosas, y que es porque el viento destapa más.",
            "Se habla de la caravana de la sal, que cada año sube más tarde y con menos mulas.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde la escriba sube a la muralla del oeste a ver el sol meterse en el páramo, que se pone rojo, luego morado, luego nada. Los de guardia encienden los candiles de las almenas uno a uno. Desde abajo, dicen, Peñaseca parece una corona."),
        v("Anochece, y el frío baja de golpe, como si alguien hubiera abierto una puerta. En el patio las cabras se apretujan contra el muro. La escriba se pone el segundo jersey, el que tiene un agujero en el codo. El agujero también tiene frío."),
        v("Al caer la tarde suena la campana grande, tres golpes, y en toda la ciudadela se dejan las herramientas: la fragua se calla, el taller se apaga, los candileros dejan de correr. Durante un momento no se oye nada. Luego, el viento, que no obedece a ninguna campana."),
        v("Anochece sobre el páramo, y abajo, muy lejos, se enciende una luz que no es de la orden: una hoguera, un viajero, un saqueador. Los de la muralla la miran sin hablar. A la hora, se apaga. La escriba lo apunta: luz al sureste, una hora."),
        v("La última luz del día se queda arriba de la torre de amarre cuando el resto de la ciudadela ya está en sombra. Un pájaro negro se posa en la argolla. La escriba no sabe cómo se llama. Lo busca en los manuales. Los manuales no traen pájaros de ahora."),
        v("Al caer la tarde la escriba se lava las manos en la pila del taller, con arena y un jabón que huele a cabra, hasta que se le va la grasa de las uñas. No se va nunca entera. Se mira las manos a la luz del candil. Son manos de taller. Le gustan así."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo en Peñaseca, y al salir el sol toda la orden se junta en el patio, con los candiles encendidos aunque sea de día, para leer el Código entero en voz alta, artículo por artículo. Lo lee la maestre Amparo. Tarda una hora. El frío del patio hace que nadie se distraiga.",
            Parrafo.TRABAJO to "Hoy la escriba no trabaja: es año nuevo, y la Cámara está cerrada con sus tres cerraduras. Después del Código hay pan blanco, el único del año, y un vino de cebada que Rosendo guarda para hoy. La escriba se come dos panes. Rosendo, que los cuenta, le pone un tercero.",
            Parrafo.PENSAMIENTO to "La escriba piensa en el año que empieza y le pide una sola cosa: que no se rompa nada que ella no sepa arreglar. Luego piensa que eso es pedir poco. Luego piensa que en el páramo pedir poco es pedir mucho. Se termina el vino. Deja de pensar.",
        ),
        "inventario" to unDia(
            Parrafo.MANANA to "Hoy empieza el inventario de invierno, y la Cámara se abre de par en par desde primera hora: cada máquina, cada manual, cada tornillo guardado se saca, se cuenta, se mira y se vuelve a guardar. La lista del año pasado tiene veinte hojas. La de este, ya se verá.",
            Parrafo.TRABAJO to "A esta hora cuenta piezas en la Cámara, de rodillas delante de un estante, mientras una candilera le canta números y ella los compara con la lista. Todo cuadra hasta la caja treinta. En la caja treinta sobra una pieza. Nadie sabe de dónde ha salido. Se apunta: sobra una.",
            Parrafo.PENSAMIENTO to "La escriba piensa que el inventario es la fiesta más rara de la orden, aunque nadie la llame fiesta: un día entero contando lo que no se usa. Piensa que es la manera que tiene la orden de decir que todo sigue ahí. Piensa que eso, en el páramo, ya es mucho decir.",
        ),
        "fundacion" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Fundación, y los caballeros desfilan por el patio con los caparazones recién pintados, cada uno con el dibujo de su familia o de su miedo: un candil, un lobo, una mano abierta. Los motores rugen. Los candileros aplauden. Las cabras huyen al fondo del corral.",
            Parrafo.TRABAJO to "Hoy la escriba no trabaja: es fiesta, y el taller está cerrado con candado. Mira el desfile desde la escalera de la Cámara, con un cuenco de leche de cabra caliente entre las manos. Un caballero joven lleva pintada en el pecho una pluma, por las escribas. Ella levanta el cuenco.",
            Parrafo.GENTE to "Esta tarde, en el refectorio, los caballeros viejos cuentan la Fundación como se la contaron a ellos: los primeros, que subieron a la meseta con las máquinas a cuestas sin saber leer los manuales, solo sabiendo que había que guardarlos. Cada año la historia es un poco más larga.",
            Parrafo.PENSAMIENTO to "La escriba piensa en los primeros, que subieron cosas que no entendían porque adivinaron que valían. Piensa que ella hace lo mismo cada día, con un poco más de luz. Piensa que dentro de cien años alguien contará también su parte. Espera que la cuenten corta.",
        ),
        "juramento" to unDia(
            Parrafo.MANANA to "Hoy juran los candileros nuevos en la sala del Código, de uno en uno, delante de toda la orden, con una vela en una mano y la otra sobre el cristal que guarda el original. Algunos tiemblan tanto que la llama baila. Nadie se ríe. Todos temblaron alguna vez.",
            Parrafo.GENTE to "Esta tarde los recién jurados reciben su candil de latón, que tienen que llenar ellos mismos y encender sin ayuda. A uno se le apaga tres veces. Los caballeros miran al techo, muy serios. A la cuarta, prende. Se oye suspirar a cien personas a la vez.",
            Parrafo.PENSAMIENTO to "La escriba piensa en su propio juramento, con las manos sudadas y la vela torcida, y en que se le olvidó medio artículo nueve y lo dijo inventado. Nadie se dio cuenta. O se dieron todos. Piensa que la orden empezó a quererla ese día, por la parte inventada.",
        ),
        "postigos" to unDia(
            Parrafo.MANANA to "Hoy se cierran los postigos de Peñaseca, porque llegan los vientos del polvo, y en toda la ciudadela suenan las contraventanas de hierro, una tras otra, como un trueno que da la vuelta a las murallas. Las rendijas se tapan con trapos mojados. Hasta que pasen los vientos, se vive a media luz.",
            Parrafo.PUEBLO to "Con los postigos cerrados, la ciudadela es otra: pasillos con candiles encendidos a todas horas, ventanas negras, un aire quieto que huele a trapo húmedo y a humo. Por las rendijas silba el viento. En el refectorio alguien ha pintado un sol en un postigo, por dentro. Es un sol pequeño.",
            Parrafo.PENSAMIENTO to "La escriba piensa que cada año la orden se encierra un poco para que no le entre el polvo, y que lo hace tan bien que a veces se le olvida abrir. Piensa que eso también le pasa a ella. Piensa que este año abrirá antes. Lo piensa todos los años.",
        ),
        "vigilia" to unDia(
            Parrafo.MANANA to "Hoy es la Vigilia de los Candiles, la noche más corta del año, y desde primera hora se sacan de la Cámara todas las lámparas viejas que todavía sirven: de aceite, de pilas, de cristal, una de bronce con forma de pez. Se limpian en el patio y se cuentan. Este año sirven cuarenta y una.",
            Parrafo.TRABAJO to "Hoy la escriba no trabaja: es fiesta, aunque se pase la tarde con las lámparas, que para ella no es trabajo. Cambia una mecha, aprieta un tornillo, sopla un cristal. A la de pilas le hace un apaño con alambre. La maestre pasa, la ve y no dice nada. Eso es un permiso.",
            Parrafo.ANOCHECER to "Esta noche, al oscurecer, se encienden todas las lámparas viejas a la vez en las almenas, y Peñaseca brilla en lo alto de la meseta como no brilla en todo el año. Desde el llano debe de verse a una jornada. La orden se queda en silencio, mirando la luz de antes.",
            Parrafo.PENSAMIENTO to "La escriba piensa en la gente que hizo esas lámparas, que las tuvo en la mesa sin darles importancia, como ella tiene un cuenco. Piensa que alguien encendió la de bronce mil veces sin mirarla. Esta noche la miran cien. Piensa que la lámpara, si pudiera, se reiría.",
        ),
        "canales" to unDia(
            Parrafo.MANANA to "Hoy se abren los canales de la cisterna, como cada año cuando llegan las tormentas del verano: los candileros corren por las azoteas quitando las tapas de piedra, se barren los canales con escobas de ramas y se sacan las arañas. Todo el mundo mira al cielo. El agua, cuando venga, tiene que encontrar el camino limpio.",
            Parrafo.TRABAJO to "A esta hora baja a la cisterna con una lámpara a revisar las rejillas de los canales, que se atascan con nidos, arena y algún hueso. Saca un nido vacío, entero, tejido con hilo de cobre. Se lo guarda. Algún pájaro del páramo sabe más de cables que muchos candileros.",
            Parrafo.PENSAMIENTO to "La escriba piensa que en Peñaseca todo depende de unas pocas tardes de tormenta, y que la orden, que guarda tantas máquinas, no tiene ninguna que haga llover. Piensa que si la encontrara, la guardaría y la usaría a la vez. Piensa que eso el Código no lo dice. Tampoco lo prohíbe.",
        ),
        "caza" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la caza grande, cinco días en el llano. Antes del alba los caballeros bajan la escalera de la meseta con los caparazones, las lanzas y las redes, y detrás, los carros vacíos. En estas fechas las cabras salvajes bajan a los charcos del verano. La orden las espera allí.",
                Parrafo.TRABAJO to "A esta hora está en el campamento del pie de la meseta, apuntando cada pieza que traen los caballeros: cuántas, de qué tamaño, quién la cazó. Luego, las manos a la sal. La carne se abre, se sala y se cuelga en cuerdas al sol. Para la noche, hasta su pluma huele a sal.",
                Parrafo.PENSAMIENTO to "La escriba piensa que la caza es lo único del año en que la orden hace lo mismo que la gente de fuera: cazar, salar, guardar para el invierno. Piensa que por eso gusta tanto. Durante cinco días nadie es caballero ni escriba. Son gente con hambre que se adelanta.",
            )),
            sigue = mapOf(
                Parrafo.MANANA to listOf(
                    "Hoy sigue la caza grande, y la escriba baja otra vez al llano antes del alba, con los cuchillos afilados y un saco de sal a la espalda. El campamento ya huele a carne y a humo. Los perros de la orden no han dormido. Los caballeros, tampoco mucho.",
                    "Hoy, otro día de caza, la escriba se despierta con las manos tiesas de sal y tiene que abrirlas despacio, dedo a dedo, como quien abre una caja vieja. Abajo, en el llano, los caballeros ya han salido. Se oyen sus motores entre las rocas, lejos.",
                    "Hoy la escriba baja al campamento y encuentra las cuerdas de ayer llenas de carne oscura, seca por fuera, y una nube de moscas que no tienen prisa. Se pasan el día espantándolas con ramas. Las moscas, a juzgar por cómo vuelven, lo toman por un juego.",
                    "Hoy es el último día de la caza, y en el campamento ya no se caza: se recoge. La carne salada se mete en sacos, los sacos en los carros, y los carros suben la escalera de la meseta empujados por todos, gritando a cada escalón. Arriba, Rosendo los espera con la lista.",
                ),
                Parrafo.TRABAJO to listOf(
                    "A esta hora sala carne en el campamento con otros seis, en una mesa de tablas, frotando la sal con las palmas hasta que escuecen. Lleva la cuenta en una tablilla, con rayas. Cada diez rayas, una cruz. Ya hay muchas cruces. Las manos ya no le escuecen: ya no las siente.",
                    "A esta hora pesa la carne colgada con una balanza de antes del Fuego que solo funciona si se le da un golpe. Le da el golpe. La aguja salta. Apunta. Le da otro golpe, por si acaso. La aguja salta a otro número. Apunta la media.",
                    "A esta hora remienda una red que han roto las cabras, sentada en una piedra, con hilo de cáñamo y la paciencia que le sobra del taller. Un caballero joven la mira hacer. Le pregunta dónde aprendió. Ella le dice que en un manual. Él no sabe si es broma.",
                    "A esta hora cuenta los sacos que suben a la ciudadela, uno por uno, y los compara con la tablilla del campamento. Faltan dos. Los encuentra en el carro de los perros, que no saben contar pero sí oler. Al final, cuadra. Este invierno no se pasará hambre.",
                ),
            ),
        ),
        "caidos" to unDia(
            Parrafo.MANANA to "Hoy es el Día de los Caídos, y la orden se junta en la sala del Código a oscuras, con un solo candil encendido sobre la mesa. La maestre dice en voz alta el nombre de cada caballero muerto desde la Fundación, y después de cada nombre, todos responden: guardado. La lista es larga. Nadie se sienta.",
            Parrafo.GENTE to "Esta tarde los caballeros viejos limpian los caparazones de los muertos, que se guardan de pie, vacíos, en dos filas, en una sala aparte. Los limpian con trapos y aceite, sin hablar, como se lava a alguien que duerme. Los candileros miran desde la puerta, sin atreverse a entrar.",
            Parrafo.PENSAMIENTO to "La escriba piensa que las escribas no salen en la lista, aunque alguna haya muerto en patrulla, porque la lista es de caballeros. Piensa que eso habría que cambiarlo. Piensa que cambiar una costumbre en la orden cuesta más que descifrar un manual. Apunta la idea. La subraya dos veces.",
        ),
        "consejo" to unDia(
            Parrafo.MANANA to "Hoy es el Consejo de otoño: la maestre Amparo reparte las misiones del año en la sala grande, de pie, con un mapa del páramo extendido sobre la mesa y cuatro piedras en las esquinas para que no se enrolle. A cada uno le dice su nombre y su camino. Nadie discute en voz alta. Se discute después.",
            Parrafo.GENTE to "Esta tarde, en el refectorio, todos comparan su misión con la de los demás, como los niños comparan regalos. Los que van lejos presumen. Los que se quedan dicen que alguien tiene que quedarse. Rosendo, que se queda siempre, sirve la sopa sin opinar. Opina con la sal.",
            Parrafo.PENSAMIENTO to "La escriba piensa en el mapa del Consejo, con sus caminos marcados con tinta y sus huecos en blanco, donde no ha ido nadie de la orden. Piensa que los huecos son lo más bonito del mapa. Piensa que no se lo dirá a la maestre, por si le da una misión.",
        ),
        "rutas" to unDia(
            Parrafo.MANANA to "Hoy se cierran las patrullas largas: vuelven las últimas de los caminos lejanos, sucias hasta las cejas, y la puerta de la escalera se atranca con la barra de hierro hasta la primavera. Desde hoy solo se patrulla cerca. El invierno ha empezado, aunque el calendario no se haya enterado.",
            Parrafo.TRABAJO to "A esta hora recibe en el taller lo que traen las últimas patrullas: dos sacos de cosas que pueden ser algo, envueltas en trapos. Las abre una a una sobre la mesa. Un motor pequeño. Una cuchara. Un libro sin tapas que empieza en la página cuarenta. Ese lo pone el primero de la cola.",
            Parrafo.PENSAMIENTO to "La escriba piensa que el invierno es su estación, aunque pase frío: menos camino, más mesa, más manual. Piensa que los caballeros cuentan los días que faltan para la primavera y que ella los cuenta al revés. No se lo ha dicho nunca a ninguno. Sospecha que lo saben.",
        ),
        "noche_larga" to unDia(
            Parrafo.MANANA to "Hoy es la Noche Larga, la más larga del año, y en Peñaseca se pasa despierto: se encienden la estufa grande del refectorio y todas las velas guardadas, y se saca de la Cámara, en una caja de madera, el primer manual de la orden. Es pequeño y está gastado. Es el que empezó todo.",
            Parrafo.TRABAJO to "Hoy la escriba no trabaja: es la Noche Larga, y la tarde se le va en preparar el refectorio, colgar ramas secas y ayudar a Rosendo con la sopa de huesos que solo hace hoy. A la hora de la velada, la sala está llena. Huele a cera, a sopa y a cien personas con la ropa buena.",
            Parrafo.ANOCHECER to "Esta noche, en el refectorio, la maestre lee en voz alta el primer manual de la orden, que explica cómo se arregla una bomba de agua. No es un libro sagrado. Es un manual, y por eso se lee. Al terminar, todos aplauden a la bomba. Es la broma de cada año. Funciona siempre.",
            Parrafo.PENSAMIENTO to "La escriba piensa que la orden empezó con un manual de bombas porque alguien, al principio, tuvo sed. Piensa que todo lo demás vino después: el Código, los caparazones, los rangos. Piensa que debajo de todo sigue habiendo alguien con sed. Le parece una buena base.",
        ),

        // ---------- Los de la temporada 1 ----------
        "jimena" to unDia(
            Parrafo.MANANA to "Hoy, en la patrulla del este, han entrado en una gasolinera vieja a buscar cables y han encontrado a una niña sola, sentada detrás del mostrador, con un cuchillo en una mano y un perro de trapo en la otra. No ha dicho nada. Nieves ha dejado el arma en el suelo. La niña, el cuchillo no.",
            Parrafo.TRABAJO to "A esta hora la niña camina detrás de la patrulla, a diez pasos, sin acercarse ni irse. Se llama Jimena: es lo único que ha dicho. La escriba le ha dado su ración, y Jimena se la ha comido entera mirando a todos, y ha guardado las migas en el bolsillo. Ulises ha dicho: once años. Ha acertado.",
            Parrafo.GENTE to "Esta tarde, en Peñaseca, la maestre Amparo ha mirado a la niña un rato largo, sin decir nada, y luego ha dicho: que coma. Rosendo le ha puesto un cuenco tan lleno que se derramaba. Jimena ha comido con una mano. Con la otra sujetaba el cuchillo. Nadie se lo ha quitado.",
            Parrafo.PENSAMIENTO to "La escriba piensa en la gasolinera, en cuánto tiempo llevaría la niña allí, sola, comiendo qué. Piensa que la orden recorre el páramo buscando cosas de antes para guardarlas. Hoy ha encontrado algo de ahora. Piensa que en la Cámara no hay estante para eso.",
        ),
        "tormenta_polvo" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy, al amanecer, se ha levantado en el norte una pared de polvo más alta que la meseta, ocre por abajo y negra por arriba, y ha llegado a Peñaseca antes de que acabaran de cerrar los postigos. Ahora todo es rojo: el aire, la luz, la saliva. La campana toca sin parar para que nadie se pierda en el patio.",
                Parrafo.TRABAJO to "A esta hora está en la Cámara, con todas las rendijas tapadas con trapos mojados, envolviendo en tela las máquinas más delicadas, porque el polvo entra por donde no entra el aire. Fermín la ayuda. Con esta luz, los dos trabajan a tientas. No hablan. Hablar es tragar tierra.",
                Parrafo.PENSAMIENTO to "La escriba piensa en el camino del norte, que esta mañana existía y esta noche estará debajo de un palmo de tierra. Piensa que así se perdieron las ciudades de antes: no de golpe, sino a palmos. Piensa que la orden también es un palmo. Uno que se quita cada día, con un pincel.",
            )),
            sigue = mapOf(
                Parrafo.MANANA to listOf(
                    "Hoy sigue la tormenta de polvo, y la ciudadela vive a oscuras detrás de los postigos, con candiles encendidos a todas horas. El polvo se ha metido en el pan, en las sábanas, en los oídos. Los caballeros se atan pañuelos a la cara y parecen saqueadores. Se ríen de eso. Con la boca cerrada.",
                    "Hoy, al tercer día, el viento empieza a cansarse y el polvo cae despacio, como una nevada roja. Por la tarde se puede abrir un postigo. Fuera, el patio está liso, sin una sola huella, como una página nueva. Del camino del norte no queda ni el recuerdo.",
                ),
                Parrafo.TRABAJO to listOf(
                    "A esta hora barre el polvo de la Cámara con una escoba y un pañuelo en la boca, y lo saca en cubos que parecen llenarse solos. Detrás de ella, el polvo vuelve a posarse, más fino. Es como vaciar un río con un cuenco. Lo vacía igual. En la orden eso se llama paciencia.",
                    "A esta hora sale con la patrulla a buscar el camino del norte, que la tormenta ha enterrado, y lo van encontrando a palmos, por las piedras grandes que asoman. Clavan estacas con trapos rojos. Cada estaca es una palabra. Al final del día, el camino ya dice algo.",
                ),
            ),
        ),
        "terminal" to unDia(
            Parrafo.MANANA to "Hoy la escriba ha llegado al archivo antes que nadie, con el soldador caliente y una pieza nueva en el bolsillo: un condensador sacado de una radio muerta. Lleva tres semanas con el terminal del archivo, apagado desde hace diez años. Hoy, si no se equivoca, enciende. Si se equivoca, huele a quemado.",
            Parrafo.TRABAJO to "A esta hora suelda la última pata del condensador, sopla, cierra la tapa y gira la llave. Nada. La gira otra vez. Un zumbido. Una línea verde que cruza la pantalla y se queda quieta. Luego, letras. Fermín pregunta desde la puerta qué pasa. La escriba no le contesta. Está llorando, un poco.",
            Parrafo.PENSAMIENTO to "La escriba piensa que el terminal ha pasado diez años esperando a alguien que supiera qué le faltaba. Le faltaba una pieza del tamaño de una uña. Piensa en cuántas cosas del páramo estarán igual, a una uña de funcionar. Le dan ganas de salir a buscarlas todas, hoy mismo.",
        ),
        "ojos_fermin" to unDia(
            Parrafo.MANANA to "Hoy, en la Cámara, Fermín le ha pedido que le lea la etiqueta de una caja, una etiqueta grande, de letras gordas. Ella se la ha leído. Él ha asentido, ha dejado la caja y se ha sentado en su banco. Luego, mirando al suelo, le ha dicho que ya no ve las letras pequeñas. Ni las otras.",
            Parrafo.GENTE to "Esta tarde Fermín le cuenta que lleva un año disimulando: se aprendía las etiquetas de memoria, reconocía los manuales por el lomo, firmaba donde le decían. Lo cuenta riéndose, orgulloso de la trampa. La escriba se ríe con él. Luego le coge la mano. Él la deja.",
            Parrafo.PENSAMIENTO to "La escriba piensa que Fermín le enseñó a leer los manuales, y que ahora le tocará leérselos a él. Piensa que es lo justo. No quiere que sea justo: quiere que Fermín vea. Repasa en la cabeza las lupas de la Cámara, las lentes, cuál podría servirle. Sabe que ninguna.",
        ),
        "pozo" to unDia(
            Parrafo.MANANA to "Hoy, antes del alba, ha sonado la campana de alarma: unos saqueadores han subido de noche hasta el pozo del pie de la meseta, con bidones y un carro, y han herido al guarda. Desde la muralla se veían sus antorchas. Los caballeros han bajado la escalera con los caparazones a medio cerrar.",
            Parrafo.TRABAJO to "A esta hora está en el pozo con la patrulla, apuntando lo que dejaron los saqueadores al huir: un carro con una rueda rota, tres bidones, una bota. En el carro hay una marca pintada, una mano negra. La copia en el cuaderno con cuidado. La mano tiene seis dedos. No sabe si es un error.",
            Parrafo.PENSAMIENTO to "La escriba piensa que los saqueadores no querían máquinas ni manuales: querían agua. Piensa que la orden guarda el saber de antes del Fuego mientras abajo, en el páramo, la gente se mata por un bidón. Piensa que alguien, abajo, ve la orden al revés. Y que no está del todo equivocado.",
        ),
        "letras" to unDia(
            Parrafo.MANANA to "Hoy, después de meses de verla mirar los manuales del revés, la escriba ha sentado a Jimena en el banco de la Cámara, le ha dado una pizarra y un trozo de yeso y le ha dibujado la primera letra. Jimena la ha mirado con desconfianza, como a un perro que no conoce. Luego la ha copiado. Torcida.",
            Parrafo.TRABAJO to "A esta hora repara una radio con una mano y con la otra corrige la pizarra de Jimena, que pregunta para qué sirve cada letra como si fueran herramientas. La a, para empezar. La o, para asombrarse. La escriba se lo va inventando sobre la marcha. Fermín, en su banco, se ríe bajito.",
            Parrafo.PENSAMIENTO to "La escriba piensa en la primera palabra que leyó sola, de niña, en una lata de la orden que llegó a su pueblo: agua. Piensa que se le abrió algo en la cabeza que no se ha vuelto a cerrar. Piensa en abrirle eso a Jimena. Da un poco de miedo. Vale la pena.",
        ),
        "valdecruz" to unDia(
            Parrafo.MANANA to "Hoy la escriba ha terminado de pegar las hojas del manual roto que trajo una patrulla del verano, un libro quemado por un lado y hecho jirones. Lleva un mes con él, tira a tira, con cola de hueso y pinzas. Esta mañana ha encajado la última. La página entera dice un nombre: Bóveda de Valdecruz.",
            Parrafo.TRABAJO to "A esta hora busca el nombre en todo el archivo: en los mapas, en los índices, en el terminal. Encuentra una línea en un listado: un depósito de antes del Fuego, en un cañón del sur, sellado. Debajo, una lista de lo que guarda, cortada por la mitad. La mitad que queda ya es mucho.",
            Parrafo.PENSAMIENTO to "La escriba piensa en la Bóveda, sellada en un cañón del sur desde antes del Fuego, esperando a que alguien leyera su nombre en un papel quemado. Ella lo ha leído. Piensa que eso, en la orden, obliga. No sabe todavía a qué. Sospecha que lo sabrá pronto.",
        ),
        "caravana" to unDia(
            Parrafo.MANANA to "Hoy ha subido la última caravana del año, doce mulas y cuatro carros por la escalera de la meseta, con sal, tela, aceite y noticias del sur. Los caravaneros traen la cara quemada y los ojos alegres de quien llega. En el patio se monta el mercado en un momento. Huele a especias y a mula.",
            Parrafo.GENTE to "Esta tarde la escriba cambia dos radios arregladas por un rollo de tela y una caja de pilas que quizá funcionen. Una caravanera le pide que le lea una carta que lleva desde el verano en el bolsillo. Es de su hermana: está bien, ha tenido un hijo. La mujer llora un poco y le regala sal.",
            Parrafo.PENSAMIENTO to "La escriba piensa en las noticias del sur que ha traído la caravana: pueblos nuevos, caminos cortados, una luz en un cañón que nadie sabe explicar. Piensa en la Bóveda de Valdecruz, que está en el sur. Piensa que las noticias son como los manuales rotos: hay que pegarlas.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "escriba", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + EXPEDICION_MANANA + ENCIERRO_MANANA + HERIDA_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + EXPEDICION_TRABAJO + ENCIERRO_TRABAJO + HERIDA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + EXPEDICION_PUEBLO + ENCIERRO_PUEBLO + HERIDA_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + EXPEDICION_ANOCHECER + ENCIERRO_ANOCHECER + HERIDA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
