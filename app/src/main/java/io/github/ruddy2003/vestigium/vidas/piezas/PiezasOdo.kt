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
 * El cuento del hermano Odo, monje copista en una abadía benedictina junto al Rin, de 1150 a 1154. Sigue la gramática y
 * el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día lo marcan las horas del coro: maitines de madrugada, prima, sexta y vísperas. Entre ellas copia, por la
 *   mañana y por la tarde, solo con luz de día.
 * - El pueblo es la abadía: el claustro, la iglesia, el scriptorium, el río. Lee el arco «huerto», el del claustro.
 * - La Biblia grande avanza en los pensamientos con fecha: la primera inicial (temporada 1), el primer tomo (3), la Biblia
 *   terminada (4) y el libro nuevo (5).
 * - En la temporada 2 pasa seis semanas en Lorsch (estado «lorsch»): amanece, copia, reza, come y duerme allá.
 * - No repite lo que ya dice «Su vida»: las siete horas de coro, la pluma de ganso, los meses de un libro, el libro de
 *   cuaresma, las cinco mil kilocalorías y el galón de cerveza.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG4.kt`):
 * - Heinrich llega en `heinrich` (temporada 2).
 * - El viejo Gerhard es el bibliotecario hasta que enferma (`gerhard_enfermo`, 2) y muere (`muere_gerhard`, 3).
 * - Gertrudis, su hermana, vive con Hildegarda en el monte de San Ruperto desde `rupertsberg` (1).
 * - Le falla la vista desde `piedra_leer` (4) y es bibliotecario desde `bibliotecario` (5).
 * - Las fiestas que vuelven cada año no nombran a Heinrich, a Gerhard ni a Gertrudis.
 */
internal object PiezasOdo {
    // Su nombre en la ficha es «Hermano Odo»; aquí se escribe «Odo», como lo llaman en el claustro, para que no salga
    // «Hermano» con mayúscula en medio de una frase.
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("Al hermano Odo lo despierta la campana en plena noche, como cada noche, y se levanta del jergón ya vestido, porque los monjes duermen con el hábito y el cinto puestos. En el dormitorio arde una sola lámpara. Los hermanos bajan en fila a la iglesia, con los ojos cerrados todavía."),
        v("Odo se despierta antes de la campana, como le pasa desde hace años, y se queda quieto escuchando: la respiración de veinte monjes, la lluvia en el tejado, el río abajo. Luego suena la campana, y el día empieza, que aquí empieza de noche."),
        v("Lo primero del día, aquí, es cantar. Odo baja a oscuras la escalera del dormitorio a la iglesia, se pone en su sitio del coro y canta los salmos de la noche con los ojos medio cerrados. El hermano que vigila pasa con un farol para ver que nadie se duerma. Alguien siempre se duerme."),
        v("Al amanecer, después de prima, Odo se lava la cara y las manos en la fuente del claustro, con el agua tan fría que le duelen los dedos. Se los frota para que despierten: hoy tienen que escribir. Luego va a la sala del capítulo, donde se lee un capítulo de la Regla, como todos los días."),
        v("En la sala del capítulo, después de prima, los hermanos confiesan delante de todos sus faltas pequeñas: uno rompió una escudilla, otro llegó tarde al coro, otro habló en el claustro. Odo confiesa que se durmió en maitines. El abad le pone una penitencia corta. Él la cumple de buena gana."),
        v("Odo sale de la iglesia después de prima y cruza el claustro, que a esa hora es azul y está en silencio. En el centro, el pozo. Alrededor, los arcos y los capiteles, con monstruos tallados que los novicios miran demasiado. Sube la escalera del scriptorium. Ya es de día."),
    )

    /** Las seis semanas en Lorsch (estado «lorsch», temporada 2). */
    private fun lo(texto: String, hacer: Hacer? = null) = v(texto, estado = "lorsch", hacer = hacer)

    private val LORSCH_MANANA = listOf(
        lo("Odo se despierta en el dormitorio de los huéspedes de Lorsch, una sala larga donde duermen los monjes de paso, y tarda un momento en saber dónde está. La campana suena distinta. Los salmos son los mismos. Baja a la iglesia de los otros, y canta con ellos como si fuera suya."),
        lo("A Odo lo despierta la campana de Lorsch, más grave que la suya, y baja a maitines con los monjes de la casa, que lo miran de reojo: un copista que viene a llevarse un libro. Él se pone al final del coro, como corresponde a un huésped, y canta bajito."),
        lo("Al amanecer Odo cruza el patio de Lorsch y pasa por debajo del pórtico viejo de la entrada, el de piedras rojas y blancas, que dicen que es del tiempo del emperador Carlos. Lo mira cada mañana. En su abadía no hay nada tan viejo, ni tan bonito, ni tan inútil."),
    )

    private val LORSCH_TRABAJO = listOf(
        lo("A esta hora copia en el scriptorium de Lorsch, en una mesa que le prestan junto a la ventana, el libro que vino a buscar, que no puede salir de aquí. El bibliotecario de la casa se lo trae por la mañana y se lo lleva por la tarde, contado hoja por hoja.", Hacer.TRABAJO),
        lo("A esta hora copia deprisa, más deprisa que nunca, porque el tiempo que le dieron es poco y el libro es largo. Escribe con letra más pequeña de lo que le gusta, para ahorrar pergamino, que trajo contado. Le duele la mano. Los copistas de aquí lo miran y no dicen nada.", Hacer.TRABAJO),
        lo("A esta hora le dejan ver, por un favor del abad, los armarios de la biblioteca de Lorsch: cientos de libros, algunos de cuando el imperio era joven, con tapas de marfil. Odo pasa las manos por los lomos sin abrirlos. Hay más libros aquí que en diez abadías como la suya.", Hacer.TRABAJO),
        lo("A esta hora canta el oficio con los monjes de Lorsch, que lo cantan un poco más lento que los suyos, y Odo se adelanta siempre medio verso. Lo nota el chantre, que lo mira. Odo se corrige. Al final de la semana ya canta como ellos.", Hacer.REZAR),
        lo("A esta hora reza en la iglesia de Lorsch, delante de la tumba de un santo de la casa, por los suyos, que están lejos, río abajo. Es la primera vez en años que duerme fuera de su abadía. Le pide al santo, que no lo conoce, que la cuide mientras no está.", Hacer.REZAR),
        lo("A esta hora come en el refectorio de Lorsch, en la mesa de los huéspedes, lo mismo que en casa, pero con más pescado, porque aquí son más ricos. El lector lee la vida de un santo que Odo no conocía. Se la aprende para contarla al volver.", Hacer.COMER),
        lo("A esta hora come en el refectorio de Lorsch, otra vez de huésped: pan, queso, habas y un vino del Rin mejor que el suyo, que no dirá en casa que era mejor. Los monjes de aquí hacen señas que él no entiende: cada abadía tiene las suyas. Se ríe por dentro.", Hacer.COMER),
        lo("A esta hora duerme en el dormitorio de los huéspedes de Lorsch, en un jergón que no es el suyo y que huele a otros. Sueña con el libro, con las palabras que copió hoy, que se mueven solas en la página. Se despierta antes de la campana.", Hacer.DORMIR),
        lo("A esta hora duerme mal, porque en Lorsch el dormitorio de los huéspedes está junto al camino y pasan carros toda la noche. Cuenta las hojas que faltan. Calcula los días. Le salen justos, si no llueve y si no se enferma. Se duerme haciendo la cuenta otra vez.", Hacer.DORMIR),
    )

    private val LORSCH_PUEBLO = listOf(
        lo("Lorsch es una abadía rica y vieja, en una llanura entre el Rin y los montes, con una iglesia grande, un pórtico del tiempo de los emperadores y una de las bibliotecas más famosas del Imperio. Por la puerta entran y salen carros, monjes y señores. Aquí nadie tiene prisa."),
        lo("Los monjes de Lorsch son más y más ricos que los de su abadía: tienen tierras hasta donde no se ve, molinos, viñas y aldeas que les pagan. Se nota en las capas, en los cálices y en la comida. También se nota en cómo miran a los monjes que vienen de fuera."),
    )

    private val LORSCH_ANOCHECER = listOf(
        lo("Al caer la tarde Odo le devuelve el libro al bibliotecario de Lorsch, que cuenta las hojas delante de él, despacio, y asiente. Luego va a vísperas. Después, en el claustro, cuenta las hojas que copió hoy. Doce. Mañana, si hay luz, catorce."),
        lo("Anochece en Lorsch, y desde la ventana del dormitorio de los huéspedes Odo ve la llanura que se oscurece y, lejos, una línea más oscura: el Rin. Su abadía está al otro lado, río abajo. Piensa en su atril vacío, y en quién se habrá sentado en su sitio."),
        lo("Después de completas los monjes de Lorsch se callan hasta el día siguiente, como los de su casa. Odo se acuesta en el silencio grande y piensa que el silencio es igual en todas partes, y que por eso un monje puede estar en cualquier abadía como en la suya. Casi."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la abadía el tiempo importa por la luz: sin luz no se copia."),
        v("Odo mira el cielo desde la ventana del scriptorium, sobre los tejados del claustro. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El hermano Ruotger, el cillerero, sabe el tiempo por sus rodillas, y nunca se equivoca."),
        v("{cielo} En {estacion} la luz entra distinta por la ventana, y Odo mueve el atril para seguirla."),
        v("Desde lo alto del campanario se ve el Rin, ancho y gris, con sus barcas y sus islas, y las viñas que suben por las laderas. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El abad dice que el tiempo es de Dios y que quejarse de él es quejarse de Dios. Los monjes se quejan igual, por señas."),
    )

    private val LORSCH_TIEMPO = listOf(
        lo("En Lorsch el cielo es más grande, porque la llanura es plana y no hay montes cerca que lo corten. {cielo} {de_estacion}"),
        lo("{cielo} {de_estacion} En la llanura de Lorsch hace más calor que junto al río, y el scriptorium se calienta por la tarde."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, y la luz entra en el scriptorium tan clara que se ven los pelos del pergamino.",
            "Hace un día claro, de los que dan para copiar dos hojas en vez de una.",
            "No hay una nube, y el sol calienta las piedras del claustro.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, y la luz del scriptorium es gris y pobre.",
            "Hay nubes bajas sobre el río, y los montes de la otra orilla no se ven.",
            "Está nublado, y hay que acercar el atril a la ventana.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el agua cae por las gárgolas del claustro como por bocas.",
            "Cae una lluvia fina y terca, y el pergamino se pone blando con la humedad.",
            "Llueve sobre el Rin, y el río se pone del color del barro.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos hacen temblar los vidrios y el sacristán toca la campana contra los rayos.",
            "El cielo se pone negro de golpe sobre el río, y los copistas tapan los libros.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y el claustro está blanco y callado; en el scriptorium la tinta se espesa de frío.",
            "Caen copos grandes sobre el huerto del claustro, y las huellas de los monjes se borran enseguida.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla en el río, y desde la ventana no se ve ni la otra orilla ni el campanario.",
            "La niebla sube del Rin y se mete por el claustro, y las voces del coro suenan apagadas.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento por el valle, y las contraventanas del scriptorium golpean.",
            "Hay viento, y en la iglesia las velas se apagan solas.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando el huerto del claustro despierta y los días se alargan.",
            "En primavera los días son más largos y se copia más.",
            "Es el tiempo de las primeras hojas en las viñas de la ladera.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, el tiempo de los días largos y de la siesta, que la Regla permite.",
            "En verano el scriptorium huele a cera caliente y a pergamino.",
            "Es el tiempo del calor, de las moscas y de la luz que no se acaba.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo de la vendimia y de las nieblas del río.",
            "En otoño los días se acortan y cada hora de luz vale el doble.",
            "Es el tiempo en que se guarda todo para el invierno.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando la tinta se hiela y los dedos no obedecen.",
            "En invierno solo hay fuego en la sala de la calefacción, y los copistas bajan a calentarse las manos.",
            "Es el tiempo de las noches largas y del oficio largo.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora copia en el scriptorium, sentado en un banco alto delante del atril, con el libro que copia a un lado y el pergamino nuevo delante. La pluma rasca. Nadie habla: el scriptorium es un lugar de silencio, y lo que se dice, se dice por señas. Una línea. Otra.", hacer = Hacer.TRABAJO),
        v("A esta hora prepara el pergamino: con la punta seca del punzón marca los agujeros del margen y traza las líneas, una por una, con una regla de madera, para que la letra vaya derecha. Es lo más aburrido del oficio, y lo más necesario. Una página mal rayada es una página torcida para siempre.", hacer = Hacer.TRABAJO),
        v("A esta hora corta plumas nuevas: escoge las mejores plumas de ganso, las endurece en arena caliente, les quita las barbas y les hace la punta con el cortaplumas, en un corte limpio y oblicuo. Una pluma dura una mañana. Él gasta cuatro o cinco al día.", hacer = Hacer.TRABAJO),
        v("A esta hora pinta una inicial: primero el dibujo con plomo, luego el yeso donde irá el oro, luego la hoja de oro, que se pega con el aliento y se bruñe con un diente de perro hasta que brilla como un espejo. Contiene la respiración. El oro se va con nada.", hacer = Hacer.TRABAJO),
        v("A esta hora corrige lo que copió ayer, palabra por palabra, contra el libro de donde lo copió. Encuentra dos errores: una palabra que saltó y una letra cambiada. Los raspa con el cortaplumas, con cuidado de no agujerear el pergamino, y los escribe otra vez encima.", hacer = Hacer.TRABAJO),
        v("A esta hora prepara la tinta: muele las agallas de los robles, las deja en vino y agua, les echa caparrosa, que la pone negra, y goma, que la espesa. La cuela y la guarda en un cuerno. Huele agrio. La tinta buena se pone más negra con los años; la mala se borra.", hacer = Hacer.TRABAJO),

        v("A esta hora canta en el coro, en su sitio, con el salterio abierto aunque no lo necesite: se sabe los ciento cincuenta salmos de memoria, como todos. Un lado del coro canta un verso y el otro contesta. Así, de un lado a otro, desde hace seiscientos años.", hacer = Hacer.REZAR),
        v("A esta hora está en la iglesia, de pie, en el coro, con la cogulla puesta. Las lámparas alumbran el libro grande del atril del coro, que leen entre todos. Se canta despacio. A Odo le gusta el coro más que nada: la iglesia es entonces una barca, y los monjes, sus remeros.", hacer = Hacer.REZAR),
        v("A esta hora está en el coro porque sonó la campana, y en la Regla se dice que nada se antepone al oficio. Si estaba copiando, dejó la letra a medias; si dormía, el sueño. Canta. Luego volverá a lo que dejó, que lo estará esperando.", hacer = Hacer.REZAR),

        v("A esta hora come en el refectorio, en silencio, mientras un hermano lee en voz alta desde un púlpito: hoy, la vida de un santo que se comió un león, o al revés. Pan, habas, un huevo, queso y vino aguado. Lo que falta se pide por señas: para el pan, se hace un círculo con los dedos.", hacer = Hacer.COMER),
        v("A esta hora come en el refectorio lo poco que hay hoy: pan, fruta cocida y un poco de cerveza. El lector lee un libro de los padres del desierto, que comían todavía menos. Odo se acuerda de ellos y no pide más pan. Luego pide más pan.", hacer = Hacer.COMER),
        v("A esta hora come pescado, porque hoy es día de pescado, que en la abadía son casi todos: salmón del Rin, ahumado, con una salsa de mostaza que hace el cocinero. Los monjes viejos comen despacio. Los jóvenes, deprisa, por si hay más. Nunca hay más.", hacer = Hacer.COMER),

        v("A esta hora duerme en el dormitorio, en su jergón de paja, con el hábito puesto y la cogulla encima, entre otros veinte monjes. La lámpara de la noche arde en un rincón, como manda la Regla. Alguien ronca. Odo ya no lo oye.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con letras: una A grande con un dragón enroscado, que se le escapa de la página. Se despierta y oye la lluvia. Falta poco para la campana. Se vuelve a dormir, con prisa.", hacer = Hacer.DORMIR),
        v("A esta hora duerme el sueño de los monjes, que es corto y está partido en dos por maitines. El dormitorio está frío y el jergón caliente. Hasta la próxima campana, nadie le pide nada.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La abadía es un cuadrado de piedra alrededor del claustro: al norte la iglesia, al este la sala del capítulo y el dormitorio encima, al sur el refectorio, al oeste las bodegas. Fuera están los huertos, la enfermería, el molino y las casas de los legos. {entorno}"),
        v("{entorno} El claustro tiene cuatro galerías de arcos, y cada arco una columna con un capitel tallado: leones, sirenas, hojas, un hombre que se come a sí mismo. En el centro, el pozo. Por aquí pasan los monjes en silencio cien veces al día."),
        v("El scriptorium está encima de la sala del capítulo, una sala larga con ventanas al sur, para que entre la luz, y mesas con atriles para cinco copistas. Huele a pergamino, a tinta y a cera. Junto a la puerta, el armario de los libros, que tiene llave. {entorno}"),
        v("Abajo de la abadía pasa el Rin, ancho y verde, con barcas que bajan cargadas de vino y suben tiradas por caballos desde la orilla. En la otra orilla hay viñas, castillos y otra abadía, que es rival. {entorno}"),
        v("{entorno} Los hermanos legos, que no cantan en el coro ni saben leer, trabajan los campos y las viñas, cuidan los animales, hacen el pan. Viven al otro lado del patio, en sus casas, y entran a la iglesia por otra puerta. Sin ellos la abadía no comería."),
        v("En la puerta de la abadía está el hermano portero, que abre a los pobres, a los peregrinos y a los mensajeros, y les da pan. Por esa puerta entra todo lo que viene de fuera: las noticias, las enfermedades, los niños que traen para monjes. {entorno}"),
    )

    /** El huerto del claustro (arco «huerto»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("huerto", 0.0, 0.02, frases = listOf(
            "El huerto del claustro está pelado: tierra negra, setos de boj y las ramas secas de los rosales.",
            "En el huerto del claustro no crece nada ahora, y el pozo tiene una costra de musgo.",
        )),
        Entorno("huerto", 0.02, 0.45, frases = listOf(
            "En el huerto del claustro asoman las primeras hojas de las hierbas: salvia, ruda, menta.",
            "El huerto del claustro se pone verde poco a poco, y el hermano jardinero lo vigila como a un enfermo.",
        )),
        Entorno("huerto", 0.45, 0.85, frases = listOf(
            "El huerto del claustro está verde y oloroso: salvia, hinojo, menta, y abejas que entran por los arcos.",
            "En el huerto del claustro crecen las hierbas de la enfermería en filas ordenadas, como renglones.",
        )),
        Entorno("huerto", 0.85, 1.0, frases = listOf(
            "El huerto del claustro está en su mejor momento, lleno de hierbas altas, flores y abejas.",
            "En el huerto del claustro las hierbas de la enfermería están listas para cortarse y secarse.",
        )),
    )

    private val GENTE = listOf(
        v("El abad Wolfram es un hombre alto y lento, de familia noble, que habla poco y mira mucho. Sabe el nombre de todos los monjes y lo que cada uno hace mal. A Odo lo aprecia, lo dice poco, y le da trabajo, que es su manera de decirlo."),
        v("El hermano Ruotger, el cillerero, lleva la despensa, las bodegas y las cuentas de la abadía, y es el monje más gordo y más alegre de la casa. Dice que alguien tiene que probar el vino para saber si es bueno. Odo le pide pergamino, y él siempre dice que no hay, y luego hay."),
        v("Los otros copistas del scriptorium son cuatro: dos viejos, uno que escribe bonito y despacio, y un joven que escribe deprisa y mal. Se hablan por señas y se pelean por la luz de la ventana. Odo se sienta en la esquina, donde la luz es menos, porque es el que menos se queja."),
        v("El hermano enfermero cuida a los viejos y a los enfermos en la enfermería, donde se come carne y hay fuego, y por eso los monjes jóvenes envidian a los enfermos. Sabe de hierbas, de sangrías y de huesos. Cuando a Odo le duele la mano, le da un ungüento que huele a romero."),
        v("Los novicios son tres muchachos de quince o dieciséis años que todavía no hacen los votos y están todo el día con el maestro de novicios, aprendiendo los salmos y las señas. Miran el scriptorium con miedo. Uno de ellos quiere ser copista. Odo le enseña cómo se coge la pluma."),
        v("El viejo hermano Gerhard es el bibliotecario de la abadía y el que le enseñó a Odo a escribir, hace treinta años, cuando era un niño recién entregado a la abadía. Tiene la llave de los libros colgada del cinto y la mano que ya le tiembla. Cuando Odo le pregunta algo, contesta con un libro.", hasta = "gerhard_enfermo"),
        v("El viejo Gerhard está en la enfermería, en una cama junto al fuego, y ya no sube al scriptorium. Odo le lleva cada tarde lo que copió, y Gerhard lo mira con los ojos casi blancos y le dice dónde tembló la mano. Siempre tiene razón.", desde = "gerhard_enfermo", hasta = "muere_gerhard"),
        v("Heinrich es el niño que sus padres entregaron a la abadía con un campo y una cruz de plata, y que el abad le dio a Odo para que le enseñara las letras. Tiene ocho años, los ojos grandes y la cabeza llena de preguntas que no se pueden hacer en el claustro. Las hace por señas.", desde = "heinrich"),
        v("Gertrudis, su hermana, es monja con la abadesa Hildegarda, en el monte de San Ruperto, al otro lado del río, a medio día de camino. Se escriben dos veces al año. Ella le cuenta las visiones de su abadesa y le pide pergamino. Él le manda pergamino.", desde = "rupertsberg"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Odo lo sabe como se saben las cosas de la abadía: porque se lo enseñó Gerhard, a quien se lo enseñó otro."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En la abadía se dice que el que escribe reza dos veces, y que el que copia mal peca dos veces."),
        v("{dato_objeto} Son cosas que se aprenden mirando, como casi todo en el claustro."),
        v("Un monje no tiene nada suyo, ni la pluma con la que escribe: todo es de la abadía. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("pluma", "la pluma", "una pluma", "La pluma",
            "La pluma se saca del ala de un ganso, de las cinco primeras plumas, que son las mejores; las del ala izquierda se curvan hacia la derecha y le quedan bien a la mano; se endurece en arena caliente y se corta tantas veces que al final queda un muñón."),
        ObjetoCuento("pergamino", "el pergamino", "una hoja de pergamino", "El pergamino",
            "El pergamino es piel de ternero o de oveja, remojada en cal, raspada de pelo y de carne, estirada en un bastidor y lijada con piedra pómez; de una piel salen dos o cuatro hojas, y una Biblia grande se come un rebaño entero."),
        ObjetoCuento("tinta", "la tinta", "un cuerno de tinta", "La tinta",
            "La tinta negra se hace con agallas de roble, esas bolitas que salen en las hojas cuando les pica una avispa, molidas y mezcladas con caparrosa y goma; la roja, con cinabrio o con minio, es para los títulos, y de ahí que se llamen rúbricas."),
        ObjetoCuento("cortaplumas", "el cortaplumas", "un cortaplumas", "El cortaplumas",
            "El cortaplumas es una navaja pequeña que el copista tiene en la mano izquierda mientras escribe con la derecha, para sujetar el pergamino, afilar la pluma y raspar los errores; un buen copista lo usa tanto como la pluma."),
        ObjetoCuento("piedra_pomez", "la piedra pómez", "una piedra pómez", "La piedra pómez",
            "La piedra pómez es una piedra ligera y llena de agujeros, que viene de los volcanes del sur y flota en el agua; con ella se lija el pergamino hasta dejarlo suave, y se borran las manchas que el cortaplumas no puede."),
        ObjetoCuento("atril", "el atril", "un atril", "El atril",
            "El atril del copista es una mesa inclinada, casi de pie, para que la pluma caiga derecha y la tinta baje despacio; tiene un borde abajo para que no resbale la hoja y un agujero para el cuerno de la tinta."),
        ObjetoCuento("salterio", "el salterio", "un salterio", "El salterio",
            "El salterio es el libro de los ciento cincuenta salmos, el que más se usa en una abadía, porque se cantan todos cada semana; los niños aprenden a leer con él, y cuando lo saben de memoria ya saben leer."),
        ObjetoCuento("habito", "el hábito", "un hábito", "El hábito",
            "El hábito negro de los benedictinos es una túnica de lana con capucha, la cogulla encima para el coro, un cinto y unas sandalias o unas botas; cada monje tiene dos, uno para llevar y otro para lavar, y se duerme con él puesto."),
        ObjetoCuento("campana", "la campana", "una campana", "La campana",
            "La campana de la abadía la toca el sacristán para cada hora del oficio, de día y de noche; tiene nombre, la bendijo un obispo, y se dice que su sonido espanta los rayos y los demonios, hasta donde se oye."),
        ObjetoCuento("vela", "la vela", "una vela de cera", "La vela",
            "La vela de cera de abeja es cara y se guarda para la iglesia; en el dormitorio y en la cocina se usa sebo, que humea y huele a carnero; en el scriptorium no se usa ninguna, porque de noche no se copia: la luz de vela engaña la letra."),
        ObjetoCuento("pan_oro", "el pan de oro", "una hoja de pan de oro", "El pan de oro",
            "El pan de oro es una moneda de oro batida con un martillo entre pieles hasta que queda tan fina que se ve la luz a través y se la lleva un suspiro; se pone sobre yeso húmedo, se aprieta con algodón y se bruñe hasta que brilla."),
        ObjetoCuento("tablilla", "la tablilla de cera", "una tablilla de cera", "La tablilla",
            "La tablilla de cera es una tabla de madera con un hueco relleno de cera verde o negra, donde se escribe con un punzón y se borra con la otra punta, que es plana; con ella aprenden los niños, y en ella se hacen los borradores."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} Odo piensa en la abadía como se piensa en el cuerpo propio: no se la elige, se vive dentro. Lo entregaron a la abadía de niño, y no conoce otra cosa. A sus {edad} años, no sabe si eso es pobreza o es suerte. Casi siempre cree que es suerte."),
        v("Piensa a veces en los que copiaron el libro que él copia, antes que él, y en los que lo copiaron antes que ellos, una cadena de manos hasta el que lo escribió primero. Él es una mano de esa cadena. Si se equivoca, se equivocan todos los que vengan."),
        v("{mundo} Él piensa en cosas más cercanas: en el pergamino, que se acaba; en la tinta, que se espesa; en la luz, que se va. Las cosas grandes son de los reyes, de los papas y de los abades, que las piensan sin mancharse los dedos."),
        v("{mundo} A sus {edad} años, Odo ha copiado once libros enteros y la mitad de otros tres. Si vive tanto como el viejo Gerhard, copiará otros veinte. Treinta libros, piensa, es lo que deja un hombre. Es más de lo que deja un rey."),
        v("Piensa en los que leerán lo que él copia, dentro de cien o doscientos años, cuando él ya sea polvo en el cementerio del claustro. No sabrán su nombre. Por eso, al final de cada libro, escribe una línea pequeña: «Rezad por el que lo escribió». Alguien rezará."),
        v("{mundo} A veces, en el coro, de noche, Odo se pierde en un salmo que ha cantado mil veces y de pronto le parece nuevo, como si lo oyera por primera vez. No se lo dice a nadie. Es lo más cerca que ha estado de Dios, y no tiene nada que ver con los libros."),
    )

    private val MUNDOS = mapOf(
        "rupertsberg" to listOf(
            "En la abadía se habla de la abadesa Hildegarda, que tiene visiones y escribe a los papas, y que se va a otro monte con sus monjas.",
            "Se habla de los que volvieron de la cruzada, pocos y pobres, y de lo que cuentan de Oriente.",
            "Se dice que el rey Conrado está enfermo y que no volvió bien de Oriente.",
        ),
        "lorsch" to listOf(
            "En la abadía se habla de Suger, el abad de Saint-Denis, que mandó en Francia más que el rey.",
            "Se dice que la abadesa Hildegarda escribe un libro de visiones, y que el papa ha leído un trozo.",
        ),
        "rey_nuevo" to listOf(
            "Se habla de los reyes, que van y vienen, y de los príncipes que los eligen en Fráncfort.",
            "Se dice que en Francia el rey y la reina no se quieren, y que eso traerá guerras.",
        ),
        "claraval" to listOf(
            "Se habla de Bernardo de Claraval y de sus monjes blancos, que ya son más que los negros.",
            "Se dice que en Roma los papas duran poco y los romanos mucho.",
        ),
        "italia" to listOf(
            "Se habla del rey Federico, que quiere bajar a Italia a ponerse la corona de emperador.",
            "Se dice que en Sicilia los reyes normandos tienen sabios moros y griegos en su corte.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde suena la campana de vísperas, y Odo limpia la pluma, tapa el cuerno de la tinta, cubre la página con un paño y baja a la iglesia. Desde el coro ve por la ventana alta cómo el cielo se pone naranja y luego gris. Después, la cena, y completas."),
        v("Anochece en la abadía, y después de completas empieza el gran silencio, el de la noche, que no se rompe por nada hasta la mañana. Odo sube al dormitorio con los demás, en fila. Se tumba en su jergón. Fuera, el río sigue sonando, que no tiene Regla."),
        v("Después de la cena, los monjes se juntan en el claustro para la lectura de la tarde, sentados en los bancos de piedra, mientras uno lee las conferencias de los padres antiguos. Luego completas. Odo se sabe la oración de completas desde los siete años."),
        v("De noche, la abadía es una isla de silencio en la oscuridad. Solo arde la lámpara del dormitorio y la de la iglesia, la que no se apaga nunca. Odo la ve desde la ventana antes de acostarse. Le gusta saber que alguien siempre está despierto."),
        v("La última luz del día se queda un momento en lo alto de los montes de la otra orilla, cuando el valle ya está a oscuras. Odo la ve desde el claustro, camino de completas. Luego se va. En la iglesia, las velas."),
        v("Al anochecer baja la niebla del río y se mete en el claustro, y las columnas se vuelven sombras. Odo cruza deprisa hacia la iglesia, con la capucha puesta. En invierno las vísperas se cantan casi a oscuras.", meses = setOf(10, 11, 12, 1, 2)),
        v("Al caer la tarde, en verano, todavía hay luz después de completas, y Odo se acuesta con el cielo claro detrás de la ventana del dormitorio. Los pájaros siguen cantando en el huerto. La Regla manda dormir. Él obedece, y escucha a los pájaros.", meses = setOf(5, 6, 7, 8)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "candelaria" to unDia(
            Parrafo.MANANA to "Hoy es la Candelaria, cuarenta días después de Navidad, y antes de misa el abad bendice las velas, una para cada monje, que se encienden en la puerta de la iglesia. Luego la comunidad entera da la vuelta al claustro en procesión, cantando, cada uno con su luz.",
            Parrafo.TRABAJO to "A esta hora no copia: en las fiestas grandes los monjes no trabajan con las manos, y el scriptorium se queda cerrado. Odo lee en el claustro, con la vela de la procesión todavía en la manga, apagada.",
            Parrafo.PUEBLO to "La procesión de las velas va por las cuatro galerías del claustro, despacio, y las llamas tiemblan con el aire frío de febrero. Los capiteles tallados aparecen y desaparecen. Desde la puerta, los legos y los criados miran. Es la fiesta más bonita del invierno.",
            Parrafo.PENSAMIENTO to "Odo lleva su vela con cuidado, protegiéndola con la mano, y piensa que así se lleva la fe: una llama pequeña, en un claustro frío, de un año a otro. Al final de la procesión, la apaga con los dedos. La cera se guarda para la iglesia.",
        ),
        "cuaresma" to unDia(
            Parrafo.MANANA to "Hoy empieza la cuaresma. Al amanecer el abad pone ceniza en la frente de cada monje, y les recuerda que son polvo. Luego, en la sala del capítulo, el bibliotecario reparte los libros: a cada monje uno, que tiene que leer entero antes de Pascua. Es la Regla.",
            Parrafo.TRABAJO to "A esta hora lee el libro que le tocó para la cuaresma, sentado en el claustro, despacio, como manda la Regla. Este año es uno de Gregorio sobre el libro de Job, largo como el invierno. Odo ya lo copió una vez. Leerlo es otra cosa: aquí no hay que mirar las letras, sino lo que dicen.",
            Parrafo.ANOCHECER to "Esta noche se cena menos que nunca: en cuaresma se come una vez al día, después de vísperas, y sin huevos ni queso. Odo tiene hambre y no dice nada. Los novicios tampoco dicen nada, por señas, pero se les nota en la cara.",
        ),
        "san_benito" to unDia(
            Parrafo.MANANA to "Hoy es san Benito, el padre de la casa, el que escribió la Regla, que murió este día hace seiscientos años, de pie, en el coro, sostenido por sus monjes. La abadía lo celebra en plena cuaresma, con el oficio solemne y las mejores capas.",
            Parrafo.TRABAJO to "A esta hora no copia, que es fiesta grande de la casa: canta, lee y descansa. Pasea por el claustro y mira los capiteles, que de tanto pasar no ve nunca. Hoy los ve. Un león con cara de abad. No se ríe.",
            Parrafo.PUEBLO to "En la iglesia se encienden todas las lámparas y se saca el relicario, y en el refectorio, aunque es cuaresma, hay pescado bueno y un vaso de vino. Hoy el lector lee la vida de san Benito, la que escribió el papa Gregorio, la del cuervo que le quitó el pan envenenado.",
            Parrafo.PENSAMIENTO to "Odo piensa en san Benito, que hizo una Regla para gente común, no para santos: cuánto se come, cuánto se duerme, cómo se castiga a los que llegan tarde. Lo piensa con cariño, como se piensa en un abuelo. Sin él, piensa, él estaría en un campo, o en una guerra.",
        ),
        "pascua" to unDia(
            Parrafo.MANANA to "Hoy es Pascua. En la vigilia de la noche se encendió el cirio nuevo con fuego nuevo, sacado de un pedernal en la puerta de la iglesia, y a la mañana vuelve el aleluya, que estuvo callado desde la cuaresma. Las campanas tocan todas, todo lo que pueden.",
            Parrafo.TRABAJO to "A esta hora canta la misa de Pascua en el coro, y el aleluya le sale de dentro, después de cuarenta días sin cantarlo. La iglesia está llena de flores, de luz y de gente del valle. Hoy nadie copia: hoy la Regla manda alegrarse, y se cumple.",
            Parrafo.ANOCHECER to "Esta noche se cena como no se ha cenado en cuarenta días: huevos, queso, pescado y vino, y pan blanco. Los monjes jóvenes comen demasiado. Odo también. En completas se oyen algunos estómagos, y el abad hace como que no.",
        ),
        "sangria" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy le toca la sangría, como a todos los monjes varias veces al año: el hermano enfermero le abre una vena del brazo con una lanceta y le saca un cuenco de sangre, para equilibrar los humores. Odo mira para otro lado. Luego le vendan el brazo.",
                Parrafo.TRABAJO to "A esta hora descansa, porque los sangrados no trabajan ni van a maitines tres días. Pasea por el huerto con el brazo vendado, lee un poco, duerme la siesta. Le parece raro tener las manos quietas. A los tres días, ya las echa de menos el atril.",
                Parrafo.ANOCHECER to "Esta noche los sangrados cenan en la enfermería, junto al fuego: un caldo, huevos, pan blanco y vino sin aguar, que es el premio por la sangre. Los demás los envidian. Odo come despacio, para que dure.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue descansando de la sangría, sentado al sol en el huerto de la enfermería. No escribe. Mira a las abejas.",
                "A esta hora es su último día de sangría. Ya se siente bien, y le pide al enfermero volver al scriptorium. El enfermero le dice que mañana.",
            )),
        ),
        "pentecostes" to unDia(
            Parrafo.MANANA to "Hoy es Pentecostés, cincuenta días después de Pascua, cuando bajó el Espíritu en lenguas de fuego. El abad canta la misa grande, con la capa roja, y en la iglesia se cuelgan guirnaldas. Desde el valle sube gente a la misa, con ropa de fiesta.",
            Parrafo.TRABAJO to "A esta hora no copia: es Pentecostés, y en las fiestas grandes el scriptorium está cerrado. Odo lee en el claustro un libro de la biblioteca, por gusto, que es la única vez del año que lee algo que no tiene que copiar.",
            Parrafo.PUEBLO to "En la puerta de la abadía hay más pobres que nunca, porque en las fiestas grandes se reparte pan y carne, y el portero no da abasto. Los legos ayudan. Hay niños, viejos, peregrinos, un ciego que canta. La abadía da de comer al valle.",
            Parrafo.PENSAMIENTO to "Odo piensa en las lenguas de fuego, que hicieron que los apóstoles hablaran todas las lenguas. Él solo habla dos, la de su madre y el latín. Pero copia en una tercera, piensa, que es la de las letras, y esa la entienden todos los que saben leerla, en cualquier tierra.",
        ),
        "traslacion_benito" to unDia(
            Parrafo.MANANA to "Hoy es san Benito de verano, el día en que se recuerda que sus huesos llegaron a Francia, hace siglos. Es fiesta grande otra vez, como en marzo, pero con sol. El oficio de la noche es más largo y la misa más bonita.",
            Parrafo.TRABAJO to "A esta hora no copia, que es fiesta grande. Se sienta en el huerto del claustro, al sol, con el salterio abierto en las rodillas, y se queda dormido. Lo despierta una abeja. Lo despierta la campana.",
            Parrafo.PUEBLO to "El claustro está lleno de flores del huerto, y hay pan blanco y pescado en el refectorio. Los legos tienen la tarde libre y bajan al río a bañarse, que es lo que hacen en las fiestas de verano. Se les oye gritar desde el scriptorium.",
            Parrafo.ANOCHECER to "Esta tarde, después de vísperas, el abad deja que los monjes paseen por el huerto grande, fuera del claustro, hasta la hora de la cena. Odo camina hasta la tapia que da al río y mira el agua. Es lo más lejos que va en meses.",
        ),
        "asuncion" to unDia(
            Parrafo.MANANA to "Hoy es la Asunción de la Virgen, la fiesta grande del verano, y la abadía amanece con el olor de las hierbas que se bendicen en la misa: ramilletes de salvia, ruda, hinojo y espigas que la gente del valle trae para que los bendigan y los cuelga luego en las casas.",
            Parrafo.TRABAJO to "A esta hora canta la misa de la Asunción en el coro, con la iglesia llena de gente del valle y de olor de hierbas. Hoy no se copia. El chantre ha preparado un canto nuevo, que sale regular. Nadie se lo dice.",
            Parrafo.PENSAMIENTO to "Odo piensa en la Virgen, que subió al cielo entera, y en que él ha pintado su cara en tres iniciales y nunca le salió bien. La pintan los otros con más gracia. Él pinta mejor los dragones. Le pide perdón por eso.",
        ),
        "vendimia" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó la vendimia en las viñas de la abadía, que suben por la ladera sobre el Rin. Los legos y los criados cortan los racimos, y los monjes jóvenes bajan a ayudar después de prima, con permiso del abad. El scriptorium se queda medio vacío.",
                Parrafo.TRABAJO to "A esta hora vendimia en la ladera, con el hábito remangado y las manos pegajosas, cortando racimos con un cuchillo pequeño y echándolos en un cesto. Hace sol. Los legos cantan, que en la viña se puede. Odo no canta, pero se come una uva de cada diez.",
                Parrafo.PUEBLO to "Por el camino de la viña bajan los carros llenos de cestos hacia el lagar de la abadía, donde los legos pisan la uva descalzos. Todo huele a mosto. Las avispas están borrachas. El cillerero corre de un lado a otro, contando.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora vuelve al scriptorium, porque la vendimia la terminan los legos. Tiene las manos todavía moradas, y tiene que lavarlas tres veces antes de tocar el pergamino.",
                "A esta hora copia, con el olor del mosto entrando por la ventana del scriptorium. Abajo, en el lagar, cantan. Cuesta concentrarse.",
                "A esta hora ayuda al cillerero a contar los toneles de vino nuevo en la bodega, con una tablilla de cera. Sale más vino que el año pasado. El cillerero está feliz.",
            )),
        ),
        "todos_santos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es Todos los Santos, y mañana los difuntos. La abadía canta hoy por los santos del cielo y mañana por los muertos de la casa, cuyos nombres están escritos en un libro largo que el abad lee entero en el capítulo. Son cientos.",
                Parrafo.TRABAJO to "A esta hora no copia: es fiesta grande, y el scriptorium está cerrado. Odo pasea por el cementerio del claustro, entre las tumbas sin nombre de los monjes de antes, y les habla un poco, por si alguno lo oye.",
                Parrafo.PUEBLO to "En el cementerio del claustro, junto a la iglesia, donde están enterrados los monjes de antes sin nombre en la tierra, se encienden lámparas. Los legos limpian las hierbas. En el valle se oyen las campanas de todas las iglesias.",
                Parrafo.PENSAMIENTO to "Odo escucha la lista de los muertos de la casa y piensa que algún día su nombre estará en ese libro, leído por un monje que no lo conoció, en un capítulo frío de noviembre. No le da miedo. Le da algo parecido a la paz.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Hoy es el día de los difuntos, y la abadía canta el oficio de muertos desde la madrugada. En el cementerio del claustro arden lámparas sobre la tierra sin nombres.",
            )),
        ),
        "san_martin" to unDia(
            Parrafo.MANANA to "Hoy es san Martín, el que partió su capa con un pobre, y en la abadía se mata el cerdo, se prueba el vino nuevo y los arrendatarios del valle traen lo que deben. En la puerta hay una fila de carros, de gallinas y de gente que discute con el cillerero.",
            Parrafo.TRABAJO to "A esta hora no copia: es san Martín y es fiesta. Ayuda al cillerero a contar lo que traen los arrendatarios en la puerta: gallinas, quesos, sacos de grano, un cerdo que no se deja contar. Apunta todo en una tablilla de cera.",
            Parrafo.PUEBLO to "En el patio de la abadía los legos matan los cerdos del invierno y los criados hacen salchichas, que no comerán los monjes, sino los enfermos, los huéspedes y los legos. En la bodega se abre el primer tonel de vino nuevo. El cillerero lo prueba tres veces.",
            Parrafo.ANOCHECER to "Esta noche, en el refectorio, se sirve el vino nuevo, todavía turbio, y los monjes lo prueban en silencio y opinan por señas: bueno, malo, ácido, como el del año pasado. Odo hace la seña de bueno. El cillerero, en la otra mesa, sonríe.",
        ),
        "navidad" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es Navidad, y anoche los maitines fueron los más largos del año, con las doce lecturas y los cantos que solo se cantan esta noche. Odo se duerme de pie en la séptima lectura y lo despierta un codazo. Luego, a medianoche, la primera misa.",
                Parrafo.TRABAJO to "A esta hora está en la iglesia, en la tercera misa del día, la del amanecer, con la iglesia llena de gente del valle que ha subido con la nieve. Hoy no se copia. La Regla manda que el día de Navidad todo sea alegría, hasta para los monjes.",
                Parrafo.ANOCHECER to "Esta noche se cena como en ningún otro día del año: pan blanco, pescado, queso, huevos, pasteles de miel y vino bueno, y el abad come con los monjes en el refectorio. Hasta el lector parece leer más contento. Fuera nieva.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora descansa: en los días de Navidad se trabaja poco. Pasea por el claustro nevado y piensa en la próxima inicial.",
                "A esta hora vuelve al scriptorium, aunque todavía es fiesta, porque la luz de estos días es poca y no quiere perderla. Copia con los dedos fríos.",
            )),
        ),
        "encargo_biblia" to unDia(
            Parrafo.MANANA to "Hoy el abad llamó a Odo a su celda, después del capítulo, y le encargó una Biblia grande, en dos tomos, para el coro, para que se lea en voz alta en los maitines. Una Biblia entera. Le dijo que tardaría años. Le dijo que la hiciera bien.",
            Parrafo.TRABAJO to "A esta hora hace cuentas en una tablilla de cera: cuántas hojas, cuántas líneas por hoja, cuántas pieles, cuántas plumas, cuántas iniciales grandes y pequeñas. Le salen más de doscientas pieles de ternero. Le salen cuatro años. Borra la tablilla. Vuelve a contar.",
            Parrafo.PENSAMIENTO to "Odo piensa que esta Biblia se leerá en el coro durante cien años, o doscientos, cuando él ya no esté. Monjes que no conocerá cantarán con sus letras. Le da vértigo. Le da miedo. Le da también una alegría que no le cabe en el hábito.",
        ),
        "pieles" to unDia(
            Parrafo.MANANA to "Hoy llegaron en un carro, desde el curtidor de la ciudad, las primeras pieles de ternero para el pergamino de la Biblia: cuarenta, blancas, raspadas, enrolladas y atadas con cuerda. El cillerero las cuenta en la puerta, refunfuñando por el precio.",
            Parrafo.TRABAJO to "A esta hora revisa las pieles una por una a contraluz, en la ventana del scriptorium, buscando agujeros, manchas y partes demasiado finas. Aparta las mejores para las páginas de las iniciales grandes. Las peores servirán para los cuadernos de los novicios.",
            Parrafo.PENSAMIENTO to "Odo piensa en los terneros, cuarenta, que murieron para que hubiera estas pieles. La Biblia entera necesitará un rebaño. Piensa que las palabras de Dios se escriben sobre animales muertos, y que eso tiene un sentido que no termina de entender. Luego se pone a trabajar.",
        ),
        "rupertsberg" to unDia(
            Parrafo.MANANA to "Hoy llegó a la abadía la noticia de que la abadesa Hildegarda se fue con sus monjas, unas veinte, del monte de San Disibodo al monte de San Ruperto, junto a Bingen, a fundar un monasterio suyo, sin monjes. Entre ellas va Gertrudis, la hermana de Odo.",
            Parrafo.GENTE to "En la abadía se habla mucho de la abadesa, por señas y en el locutorio: unos dicen que es una santa, otros que es una soberbia, todos que escribe al papa como si fuera su prima. El hermano Ruotger dice que una abadesa con visiones da más trabajo que diez abades sin ellas.",
            Parrafo.PENSAMIENTO to "Odo piensa en su hermana, que de niña tenía miedo a la oscuridad y que ahora se va a un monte pelado a construir un monasterio con una mujer que ve luces. Le parece más valiente que él, que no ha salido nunca de su claustro.",
        ),
        "granizo" to unDia(
            Parrafo.MANANA to "Hoy, a media mañana, el cielo se puso negro sobre el río y cayó una granizada como piedras de honda, que duró lo que se tarda en rezar un salmo largo. Rompió dos vidrios del scriptorium. Odo tapó la página con su cuerpo.",
            Parrafo.PUEBLO to "En la ladera, las viñas de la abadía quedaron destrozadas: hojas rotas, racimos verdes tirados en el suelo, ramas peladas. Los legos suben a ver el daño y bajan callados. El cillerero dice que este año habrá poco vino. Lo dice como si se hubiera muerto alguien.",
            Parrafo.PENSAMIENTO to "Odo mira la página que salvó del granizo, que solo tiene una gota de agua en el margen, y piensa que tuvo suerte. Las viñas no. Piensa que Dios cuida unas cosas y otras no, y que no hay manera de saber por qué. Lo piensa y lo deja estar.",
        ),
        "carta_gertrudis" to unDia(
            Parrafo.MANANA to "Hoy le llegó una carta de su hermana Gertrudis desde el monte de San Ruperto, traída por un barquero que sube el río: una tira de pergamino doblada, escrita con letra torcida. El abad la leyó primero, como manda la Regla, y luego se la dio.",
            Parrafo.GENTE to "Gertrudis le cuenta que el monasterio nuevo es todavía un montón de piedras y barro, que hace frío, que las monjas de familias ricas se quejan, y que la abadesa no se queja nunca. Le pide pergamino, plumas y que rece por ella. Al final, le dice que lo quiere. Eso no lo leyó el abad.",
            Parrafo.PENSAMIENTO to "Odo lee la carta tres veces y la guarda en la manga del hábito, contra la Regla, que dice que un monje no tiene nada. Se la confesará mañana en el capítulo. Hoy no. Hoy la lleva encima.",
        ),
        "primera_inicial" to unDia(
            Parrafo.MANANA to "Hoy terminó la primera gran inicial de la Biblia, la del Génesis: una «I» larga como un brazo que baja por todo el margen de la página, con los siete días de la creación pintados en siete medallones, uno encima de otro. Le llevó desde el verano.",
            Parrafo.TRABAJO to "A esta hora bruñe el oro de la inicial con un diente de perro, despacio, en círculos pequeños, hasta que el oro brilla tanto que refleja su cara. Luego sopla. Luego mira. El sol, la luna y las estrellas del cuarto día le salieron bien. Los peces del quinto, raros.",
            Parrafo.PENSAMIENTO to "Odo mira su inicial terminada y piensa que es lo mejor que ha hecho, y que es solo la primera de cien. Le parece un comienzo y un final al mismo tiempo. El abad sube al scriptorium, la mira un rato, y no dice nada. Es lo más que dice.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "copista", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + LORSCH_MANANA,
            Parrafo.TIEMPO to TIEMPO + LORSCH_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + LORSCH_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + LORSCH_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + listOf(
                v("Desde que terminó la primera inicial de la Biblia, Odo piensa en las que faltan como un caminante piensa en las leguas: no todas a la vez, sino la próxima. La próxima es la del Éxodo. Ya la está dibujando por dentro.", desde = "primera_inicial", hasta = "primer_tomo"),
                v("El primer tomo de la Biblia está terminado y encuadernado, con tapas de madera forradas de cuero, en el armario de los libros. Odo lo abre a veces, a escondidas, y mira sus propias letras como si fueran de otro. Falta el segundo.", desde = "primer_tomo", hasta = "fin_biblia"),
                v("La Biblia grande está en el coro, en su atril, y se lee en voz alta cada noche en maitines. Odo la oye leer y sabe dónde está cada palabra en la página, dónde tembló la mano, dónde se le fue una gota. Nadie más lo sabe. Mejor.", desde = "fin_biblia"),
                v("Ahora la llave de los libros la lleva Odo colgada del cinto, como la llevaba Gerhard. Cada mañana abre el armario, cuenta los libros, apunta quién se lleva cuál, y cada tarde los vuelve a contar. Ya no le queda tanto tiempo para copiar. No se queja: los libros también hay que cuidarlos.", desde = "bibliotecario"),
                v("Desde que le falla la vista, Odo lee con la piedra de leer, un cristal redondo que agranda las letras cuando se pone encima. Copia más despacio y con la cara más cerca del atril. Piensa que sus ojos se gastaron en la Biblia. Le parece buen uso.", desde = "piedra_leer"),
            ),
            Parrafo.ANOCHECER to ANOCHECER + LORSCH_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
