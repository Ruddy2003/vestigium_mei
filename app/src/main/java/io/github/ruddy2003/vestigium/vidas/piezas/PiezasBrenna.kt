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
 * El cuento de Brenna, guía de caminos en una posada al pie de un paso de montaña, años 312 a 316 de los Caminos.
 * Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Tiene nombre propio: el cuento usa el hueco `{nombre}`.
 * - Su día no tiene TRABAJO: lo suyo es CAMINO (dos tramos: guiar a la compañía y el paso con sus marcas), CUIDAR (mapas
 *   y monturas) y GENTE (la posada y el arpa). Come dos veces y camina dos veces: esos textos no dicen la hora ni el
 *   sitio (valen en la cocina de la posada antes de salir y en una piedra del camino).
 * - Tres estados: «paso_cerrado» (cada invierno, en la posada y la aldea: abre camino con pala, sube con raquetas a la
 *   primera piedra, enseña runas a los chicos, copia mapas), «travesia» (temporada 3, al frente de la Compañía de Verano
 *   hasta Torremuda) y «crianza» (temporada 5, con la recién nacida en el cuarto alto). El paso cerrado se repite cada
 *   año: sus textos no nombran a nadie que cambie.
 * - El pueblo lee primero el refugio del collado (arco «refugio»: a medio hacer y terminado) y, si aún no se empieza,
 *   la veta nueva (arco «veta»: antes de abrirse, recién abierta y en su mejor momento).
 * - Cielos y estaciones no dicen «la posada» ni «la aldea»: también salen en la travesía.
 * - No repite lo que ya dice «Su vida»: las compañías de mercaderes, mineros y peregrinos, cada piedra marcada y cada
 *   refugio, el bastón y la linterna de mina, el pan de centeno con queso duro y cecina, el guiso y la cerveza oscura,
 *   los mapas en el libro de crónicas, que sabe runas y poca letra, el arpa y los que cuentan lo del otro lado.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG8.kt`):
 * - Tobin: llega en `tobin` (temporada 1); la canción, `cancion` (2); le pide vivir juntos en `tobin_pide` (3); viven
 *   en el cuarto alto desde `cuarto_alto` (4); ella espera desde `espera` (4); la hija nace en `nace` (5).
 * - El viejo Arn, hasta `arn_falta` (2); le enseña la runa «camino que sigue» en `runa_vieja` (1); su linterna vuelve
 *   en `linterna_arn` (3); su runa en Torremuda, `runa_puerta` (3); el refugio con su nombre, `refugio_listo` (4).
 * - Neli: desde `neli` (2); aprendiz desde `neli_baston` (3); guía tramos desde `neli_tramo` (4); sola desde
 *   `neli_sola` (5).
 * - Corvin, el capataz, desde `veta_nueva` (1) hasta que se va en `se_van` (5); Bastián, su hermano, desde
 *   `guia_mineros` (1) hasta que muere en `bastian` (4).
 * - Orla guarda el libro de crónicas hasta `cronista` (5); desde entonces lo guarda ella. Hilde, todos los años.
 * - El mapa del paso entero, desde `mapa` (1). El pino de la posada, hasta `pino` (5). El refugio de la Cabra, hasta
 *   `alud` (2).
 * - Las fiestas que vuelven cada año no nombran a Tobin, Arn, Neli, Corvin, Bastián, Orla ni a la niña.
 * - Fiestas de guardar (el párrafo del trabajo dice «no trabaja»): anio_nuevo, feria (los dos días), primera_runa,
 *   hierro, noche_arpa y fin_anio. La Noche de las Linternas (02-03) no toca el trabajo: cae con el paso cerrado y en
 *   una de las fechas en que la prueba compara el trabajo de la 1 y de las 8.
 */
internal object PiezasBrenna {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    /** Los meses en que, con el paso abierto, todavía (o ya) hay nieve en el camino. */
    private val NIEVE_ABIERTO = setOf(3, 4, 5, 11, 12)
    private val VERANO = setOf(6, 7, 8, 9)

    private val MANANA = listOf(
        v("A {nombre} la despierta el cuerno del Gremio, que suena tres veces en la plaza cuando hay compañía esperando. Se viste a oscuras, por costumbre: sabe dónde está cada cosa. Las botas, al final, que con ellas puestas ya no hay vuelta atrás."),
        v("{nombre} se despierta antes que el gallo de Hilde, como casi siempre, y se queda quieta escuchando el viento, que le dice más del paso que cualquier guía. Lo escucha como quien lee una carta. Luego se levanta."),
        v("Lo primero que hace {nombre} al levantarse es asomarse al ventanuco y mirar la cumbre. Si tiene una nube encima, como un sombrero, habrá que darse prisa. Si no la tiene, también, pero de mejor humor. Se ata el pelo con una tira de cuero y baja."),
        v("{nombre} se despierta con los pies fríos y el olor del pan que Hilde ya tiene en el horno. Abajo, en la sala de la posada, se oyen voces forasteras: la compañía de hoy, que no ha dormido de nervios. Siempre son los nervios. El paso, de lejos, asusta más."),
        v("A {nombre} la despiertan las mulas, que patean las tablas del establo cuando huelen que hay viaje. Las mulas lo saben antes que nadie. Se pone la camisa de lana gruesa, la de los días largos, y se echa agua helada a la cara con las dos manos."),
        v("{nombre} se despierta de un sueño en el que guiaba por un paso que no conocía, con runas que no sabía leer. Le pasa a veces. Se queda tumbada repasando con el dedo, sobre la manta, las runas de verdad, del puente al collado. Cuando están todas, se levanta."),
        v("{nombre} se despierta en el cuarto alto con el brazo de Tobin encima, pesado como un tronco, y se escurre por debajo sin despertarlo, que es un arte. Tobin duerme como los que trabajan de noche. Ella se viste mirando el camino, todavía azul.", desde = "cuarto_alto"),
        v("A {nombre} la despierta la niña antes que el cuerno, con un llanto corto que ya sabe leer: hambre, no miedo. Le da el pecho sentada en la cama, a oscuras, oyendo despertarse la posada. Luego se la pasa a Tobin, que la coge sin abrir los ojos.", desde = "nace"),
    )

    // =====================================================================
    //  El paso cerrado: cada invierno, en la posada y la aldea (estado «paso_cerrado»)
    // =====================================================================

    /** Del cierre del paso a la víspera de su apertura, todos los años: no nombra a nadie que cambie. */
    private fun pc(texto: String, hacer: Hacer? = null) = v(texto, estado = "paso_cerrado", hacer = hacer)

    private val CERRADO_MANANA = listOf(
        pc("{nombre} se despierta tarde, porque con el paso cerrado no hay compañía que esperar, y se queda bajo las mantas oyendo el silencio de la nieve, que es un silencio distinto, más gordo. Abajo crepita el primer fuego. Huele a pan. Se levanta por el pan."),
        pc("A {nombre} la despierta la pala de Hilde rascando la nieve de la puerta de la posada. Se asoma: el camino del establo ha desaparecido otra vez. Se pone dos camisas y baja a ayudar antes de que Hilde se rompa la espalda, que es lo que le dice siempre."),
        pc("{nombre} se despierta con la escarcha pintada por dentro del cristal: helechos blancos que se deshacen con el aliento. Con el paso cerrado no hay prisa. Dibuja con el dedo en la escarcha la runa de «refugio» y se vuelve a meter un momento bajo la manta."),
        pc("Con el paso cerrado, lo primero que hace {nombre} al levantarse es lo mismo que con el paso abierto: mirar el collado. No se ve, tapado de nube o de nieve. Lo mira igual. Los guías dicen que si no lo miras cada mañana, en primavera no te reconoce."),
        pc("A {nombre} la despiertan los gritos de los niños de la aldea, que han hecho un tobogán en la cuesta de la posada y bajan en tablas de barril. De pequeña, ella era la que más gritaba. Se viste y sale a mirar. Le dejan una tabla. Grita."),
    )

    private val CERRADO_TRABAJO = listOf(
        pc("A esta hora abre con la pala el camino de la aldea, de la posada a la herrería y de la herrería al Gremio, con los otros guías, que en invierno son paleros. Es el único camino que se puede guiar ahora. Lo hacen a conciencia, con los bordes rectos.", Hacer.CAMINO),
        pc("A esta hora sube con raquetas hasta la primera piedra marcada, la del puente, a ver cuánta nieve hay. La runa no se ve: hay que cavar con las manos. Mide con el bastón. Dos varas. Lo apunta en una tablilla. Mañana subirá otra vez. Así todo el invierno.", Hacer.CAMINO),
        pc("A esta hora enseña runas en la casa del Gremio a los chicos de la aldea que quieren ser guías, con un palo en la ceniza del hogar, como se ha hecho siempre. Les dibuja la de «agua» y pregunta. Uno dice «pez». Otro, «culebra». Le queda mucho invierno.", Hacer.CAMINO),
        pc("A esta hora copia mapas en la mesa grande del Gremio, con la nieve en la ventana y los pies junto al brasero, pasando a limpio lo que apuntó en hojas sueltas mientras el paso estaba abierto. Con el paso cerrado, el camino entero cabe en una mesa.", Hacer.CAMINO),
        pc("A esta hora come en la cocina de la posada, en el banco junto al horno, un cuenco de gachas de centeno con tocino que Hilde hace en invierno porque dice que pega el alma al cuerpo. Se lo come entero. Se le pega.", Hacer.COMER),
        pc("A esta hora come en la sala con los que pasan el invierno en la aldea, todos a una mesa para gastar un solo fuego: sopa de cebolla, pan duro mojado y castañas, que se reparten a puñados y se pelan quemándose los dedos. Nadie tiene prisa. No hay adónde ir.", Hacer.COMER),
        pc("A esta hora come de pie, con el cuenco en la mano, mirando por la ventana la nieve sobre el paso, un guiso de cabra con nabos que lleva en el fuego desde ayer. Hilde le dice que se siente. Ella dice que ya se sentará en primavera.", Hacer.COMER),
        pc("A esta hora duerme mucho y bien, como se duerme en la aldea con el paso cerrado: con un ladrillo caliente envuelto en un trapo a los pies y tres mantas. Fuera nieva sin ruido. Dentro cruje la madera. No hay cuerno que la despierte.", Hacer.DORMIR),
        pc("A esta hora se despierta con la nieve que resbala del tejado y cae entera delante de la puerta, con un golpe sordo. Escucha. Nada más. Piensa que en el collado habrá ya tres varas. Se vuelve a dormir pensando en el collado, que también duerme.", Hacer.DORMIR),
        pc("A esta hora pasa el rato en el establo con las mulas, que en invierno engordan y se aburren. Les rasca el cuello, les quita el barro seco del pelo, les canta. Las mulas la miran con cara de saber que en primavera todo eso se paga en cuestas.", Hacer.CUIDAR),
        pc("A esta hora remienda junto al fuego las monturas que se rompieron en otoño, cosiendo el fieltro con una aguja de saco y un hilo encerado. En la mesa se secan unos mapas que se mojaron en el último viaje. Huele a lana mojada y a sebo.", Hacer.CUIDAR),
        pc("A esta hora está en la sala de la posada con media aldea, porque con el paso cerrado la posada es la casa de todos. Alguien cuenta otra vez la historia del oso que bajó a la plaza. Todos se la saben. Todos se ríen en el mismo sitio.", Hacer.GENTE),
        pc("A esta hora juega a las tabas en la mesa larga con los guías parados, que en invierno se aburren como mulas y apuestan cecina, botones y turnos de pala. {nombre} gana una semana de pala ajena. Lo celebra con una jarra. Fuera nieva.", Hacer.GENTE),
        pc("A esta hora escucha junto al fuego los cuentos largos, los que solo se cuentan con el paso cerrado porque duran varias noches: el del guía que se casó con una niebla, el de la mula que sabía runas. {nombre} se los sabe. Cierra los ojos para oírlos mejor.", Hacer.GENTE),
    )

    private val CERRADO_PUEBLO = listOf(
        pc("Con el paso cerrado, el camino ha desaparecido bajo la nieve: no hay piedras, ni runas, ni puente, solo una ladera blanca y lisa que sube hasta el cielo. No hay nadie en ella. Solo el humo de la chimenea de la posada sube derecho, como un camino para nadie."),
        pc("Con el paso cerrado, la aldea se encoge alrededor de la posada: las casas de las afueras se cierran, la gente se junta, las mulas se juntan, y el único ruido del día es la herrería. Las sendas entre las casas son zanjas de nieve más altas que un niño."),
        pc("La posada del Cuerno, con el paso cerrado, es otra: sin compañías ni mercaderes, con las mesas pegadas al fuego, las mantas tendidas en las vigas y Hilde contando cada semana los sacos de harina en voz alta, para que todos sepan cuánto invierno queda."),
        pc("Desde la puerta de la posada se ve el collado, cuando se ve, blanco del todo, con una ceja de nieve colgada sobre la cornisa que cualquier día caerá. Los guías la miran y apuestan cuándo. Nadie apuesta a que no."),
    )

    private val CERRADO_ANOCHECER = listOf(
        pc("Anochece pronto con el paso cerrado, y la aldea se mete en casa con la última luz. {nombre} sale un momento a la puerta a respirar el frío, que corta. Arriba, las estrellas parecen más cerca, como si la nieve las subiera. Entra con la nariz roja."),
        pc("Al caer la tarde, en la posada, se encienden las velas de sebo y se cierran los postigos, y la nieve de fuera se vuelve azul. Hilde pone a calentar vino con miel. {nombre} lo bebe a sorbos, con las dos manos en la jarra, sin decir nada. No hace falta."),
        pc("Anochece, y el viento baja del collado y tira la nieve contra los postigos como arena. Alguien echa otro tronco. {nombre} piensa en los refugios del paso, llenos de leña y vacíos de gente, esperando. Le parecen casas con los ojos cerrados."),
        pc("La última luz del día, con el paso cerrado, se queda un momento en la nieve del collado, rosa, cuando la aldea ya está gris. Dura nada. {nombre} la mira desde la ventana del Gremio. Es lo único que sube por el paso en todo el invierno."),
    )

    // =====================================================================
    //  La travesía hasta Torremuda, en la temporada 3 (estado «travesia»)
    // =====================================================================

    /** Un mes al frente de la Compañía de Verano: en el camino, en los refugios y en Torremuda. */
    private fun tr(texto: String, hacer: Hacer? = null, desde: String? = null, hasta: String? = null) = v(texto, estado = "travesia", hacer = hacer, desde = desde, hasta = hasta)

    private val TRAVESIA_MANANA = listOf(
        tr("{nombre} se despierta en el suelo de piedra de un refugio del otro lado, con treinta peregrinos roncando alrededor, y tarda un momento en saber dónde está. Lejos de la posada. Al frente de la Compañía de Verano. Se pone las botas deprisa, antes de pensarlo más."),
        tr("A {nombre} la despierta el frío que sube del suelo y el murmullo de los peregrinos, que ya rezan, cada uno a lo suyo. Ella no reza. Mira el cielo, cuenta las mulas, cuenta la gente. Ese es su rezo. Luego da tres golpes con el bastón en una piedra: se sale."),
        tr("{nombre} se despierta al raso, envuelta en la capa, con el rocío en la cara y una mula olisqueándole el pelo. La compañía duerme todavía. El campamento huele a ceniza fría. Por un momento, el mundo es solo suyo y de la mula."),
        tr("{nombre} se despierta pensando en Torremuda, en sus torres tumbadas en la hierba. Desde que la ha visto, sueña con ella cada noche, y en los sueños las torres están de pie. Se lava la cara en un arroyo helado para que se le pase. No se le pasa.", desde = "torremuda"),
    )

    private val TRAVESIA_TRABAJO = listOf(
        tr("A esta hora va al frente de la Compañía de Verano por un camino que no tiene runas, o que las tuvo y se borraron, buscando las marcas viejas en las piedras con los dedos. Detrás, treinta peregrinos confían en ella. Ella confía en las piedras. Las piedras callan.", Hacer.CAMINO),
        tr("A esta hora baja con la compañía por un valle del otro lado, ancho y verde, con un río que nadie de la aldea ha nombrado nunca. Ella le pone nombre en el mapa, de momento: el río Cansado, porque así van todos. Un peregrino se ríe. Ya es algo.", Hacer.CAMINO),
        tr("A esta hora cruza con la compañía un vado con el agua por las rodillas, haciendo una cadena de manos para que la corriente no se lleve a nadie. Una vieja se suelta. {nombre} la agarra por la capa. La vieja le da las gracias y sigue rezando donde lo había dejado.", Hacer.CAMINO),
        tr("A esta hora anda delante de la compañía mirando el horizonte, por donde dicen que un día asomarán las torres de Torremuda. Todavía no hay nada: montes y más montes. Los peregrinos preguntan cuánto falta. Ella dice que poco. No lo sabe. Nunca ha estado allí.", Hacer.CAMINO, hasta = "torremuda"),
        tr("A esta hora recorre con los peregrinos las calles de Torremuda, entre torres caídas como árboles después de un vendaval. Los peregrinos tocan las piedras y lloran. Ella busca runas, por costumbre. Encuentra una de «agua» en un muro. Hay agua. Algunas cosas no cambian.", Hacer.CAMINO, desde = "torremuda", hasta = "runa_puerta"),
        tr("A esta hora reparte la comida de la compañía, que lleva ella en las mulas y cuenta cada día: tantas tortas, tanta cecina, tanto queso. Las cuentas salen justas para la vuelta, si nadie enferma y nadie llora de hambre. Siempre llora alguno.", Hacer.COMER),
        tr("A esta hora come con los peregrinos, sentados en corro en la hierba, lo que haya: pan duro, cebollas, unas truchas que un muchacho ha cogido a mano en el arroyo. Los peregrinos bendicen la comida un buen rato. {nombre} espera con la trucha en la mano, educada.", Hacer.COMER),
        tr("A esta hora duerme a medias, con la espalda contra la pared del refugio y el bastón entre las rodillas, porque la guía duerme la última y se despierta la primera. Fuera aúllan unos perros salvajes, o lo que sean. Luego se callan. Duerme.", Hacer.DORMIR),
        tr("A esta hora duerme al raso, con la compañía en corro alrededor del fuego y las mulas atadas a una cuerda. Cada vez que el fuego baja, se despierta y echa una rama. Arriba hay demasiadas estrellas. En la aldea, las montañas tapan la mitad.", Hacer.DORMIR),
        tr("A esta hora dibuja a la luz del fuego lo que ha visto hoy, con los dedos manchados de tinta: un puente roto, una fuente, una piedra con media runa. Es la primera vez que este lado del paso entra en sus mapas. Lo hace despacio, como quien escribe a un desconocido.", Hacer.CUIDAR),
        tr("A esta hora revisa los cascos y las cargas de las mulas de la compañía, que no conocen estos caminos y desconfían de cada piedra. A una le pone un emplasto de barro y hierbas en la pata. Otra le suelta una coz. {nombre} piensa que la entiende.", Hacer.CUIDAR),
        tr("A esta hora está en el corro del fuego con los peregrinos, que cantan canciones lentas de su tierra, en una lengua que ella no entiende. Luego le piden una de la aldea. Canta la del cuerno, que es de beber. Los peregrinos la aprenden. La cantan muy serios.", Hacer.GENTE),
        tr("A esta hora escucha junto al fuego lo que los peregrinos cuentan de Torremuda: que la levantaron gigantes, que la tiró una sola noche de viento, que debajo hay otra ciudad. {nombre} no dice nada. Mañana habrá camino, que es lo único que sabe de cierto.", Hacer.GENTE),
    )

    private val TRAVESIA_PUEBLO = listOf(
        tr("Del otro lado del paso el mundo es más ancho: valles verdes que bajan y bajan, ríos sin puentes, piedras con runas que nadie repasa desde hace mucho y que el musgo se come. Los caminos se bifurcan sin avisar. Aquí los guías tienen que adivinar."),
        tr("El campamento de la compañía es un corro de capas en la hierba, las mulas atadas, una hoguera y treinta pares de botas secándose del revés en palos. Al amanecer solo queda la ceniza. Cada noche, la aldea entera cabe en ese corro."),
        tr("Torremuda es una ciudad de piedra gris tumbada en la hierba: torres caídas, calles que se adivinan bajo el musgo, escaleras que no llevan a ningún sitio. Huele a piedra mojada y a tomillo. Nadie vive allí. Se nota en el silencio, que es de los que pesan.", desde = "torremuda"),
    )

    private val TRAVESIA_GENTE = listOf(
        tr("La Compañía de Verano son treinta peregrinos de lejos, dos mercaderes que aprovechan el viaje y un predicador que bendice cada piedra marcada, por si acaso. {nombre} es la que manda. Todavía le extraña. A ellos, no: la siguen como si llevara toda la vida haciéndolo."),
        tr("Entre los peregrinos va una vieja, Mabel, que hace el viaje a Torremuda por tercera vez y conoce el camino mejor que algunos guías. No se calla nunca. {nombre} la lleva a su lado. Cada vez que Mabel dice «por aquí no», ella mira. A veces tiene razón."),
    )

    private val TRAVESIA_ANOCHECER = listOf(
        tr("Al caer la tarde la compañía se para donde ella dice, ni antes ni después, y los peregrinos se dejan caer en la hierba como sacos. {nombre} busca leña, agua y un sitio sin viento, en ese orden. Luego se sienta. Es la última en sentarse. Siempre."),
        tr("Anochece del otro lado de las montañas, y el sol se pone por donde no debe, por un sitio que no es el de la aldea. {nombre} lo mira raro: todo es igual y está torcido. Se envuelve en la capa y busca el paso, allá atrás, pequeño y negro."),
        tr("Anochece, y {nombre} se acuerda de Torremuda al atardecer, con las torres caídas echando sombras larguísimas sobre la hierba, como si siguieran de pie y solo la luz se acordara. No se lo cuenta a nadie. Lo apunta en una esquina del mapa.", desde = "torremuda"),
    )

    // =====================================================================
    //  La crianza: las semanas con la recién nacida, en la temporada 5 (estado «crianza»)
    // =====================================================================

    /** En el cuarto alto y la cocina de la posada, con la niña. Desde la temporada 6 se mueve de fecha: no dice el mes. */
    private fun cr(texto: String, hacer: Hacer? = null) = v(texto, estado = "crianza", hacer = hacer)

    private val CRIANZA_MANANA = listOf(
        cr("{nombre} se despierta en el cuarto alto con la niña dormida encima del pecho, caliente como un pan, y no se mueve. No se mueve en mucho rato. Por la ventana se oye el cuerno del Gremio: hay compañía. Otros la llevarán. Ella se queda donde está."),
        cr("A {nombre} la despierta la niña, con hambre, como cada pocas horas, y le da el pecho junto a la ventana del cuarto alto, mirando clarear el paso. Tobin duerme con una mano colgando de la cama. La niña mama con los ojos abiertos. Mira a su madre como a una montaña."),
        cr("{nombre} se despierta sin saber qué hora es ni qué día, que es lo que pasa en las primeras semanas, y lo primero que hace es buscar la cuna con la mano. Está. La niña respira. Tobin, dormido en el suelo junto a la cuna, también. Ella se ríe sin ruido."),
    )

    private val CRIANZA_TRABAJO = listOf(
        cr("A esta hora sube con la niña atada a la espalda en un fardo de lana hasta la primera piedra marcada, la del puente, que es lo más lejos que va estos días. Le enseña la runa a la niña, que duerme. Se queda un rato mirando el paso. Luego baja.", Hacer.CAMINO),
        cr("A esta hora pasea con la niña en brazos por el camino de la aldea, despacio, como si guiara a una compañía muy pequeña y muy importante. Los vecinos se paran a mirar. Los guías le preguntan cuándo vuelve. Ella dice que pronto. La niña no dice nada.", Hacer.CAMINO),
        cr("A esta hora come en la cocina de la posada, con la niña en un brazo y la cuchara en el otro, el caldo de gallina que Hilde le pone delante cada día porque dice que hace leche. {nombre} no sabe si hace leche. Sabe que está bueno.", Hacer.COMER),
        cr("A esta hora come con Tobin en el cuarto alto, sentados en la cama, porque la niña se ha dormido por fin y no se atreven a moverse. Comen pan y queso sin hacer ruido, pasándose el cuchillo con cuidado, como si fuera de cristal. Se aguantan la risa. Casi.", Hacer.COMER),
        cr("A esta hora duerme a trozos, entre toma y toma, con la cuna pegada a la cama. Tobin se levanta a pasear a la niña cuando llora, cantándole bajito canciones del valle que le salen sin terminar. Ella lo oye en sueños. Duerme mejor así.", Hacer.DORMIR),
        cr("A esta hora se despierta con el silencio, que es peor que el llanto, y se asoma a la cuna con el corazón en la boca. La niña duerme con los puños cerrados junto a las orejas. Respira. {nombre} se queda mirándola un rato largo. Luego vuelve a la cama.", Hacer.DORMIR),
        cr("A esta hora pasa a limpio en la mesa del cuarto alto los mapas que se le quedaron a medias, con la niña dormida en una cesta a su lado. Moja la pluma, dibuja una runa, mira la cesta. Dibuja otra. Nunca había dibujado tan despacio. Nunca le habían salido tan bien.", Hacer.CUIDAR),
        cr("A esta hora baja al establo con la niña dormida en el fardo, a ver a las mulas, que la huelen con mucho interés y le soplan en la cara. La niña ni se entera. {nombre} revisa las monturas por costumbre. Ahora las revisa otro, y las revisa mal.", Hacer.CUIDAR),
        cr("A esta hora está en la sala de la posada con la niña en brazos, y todos pasan a verla: los guías, los mineros, los mercaderes que suben, que le dejan monedas en la manta para la suerte. Tobin toca bajito, para no despertarla. La sala entera habla en susurros.", Hacer.GENTE),
        cr("A esta hora está en la cocina con Hilde, que baña a la niña en un barreño junto al horno, con el agua probada con el codo. La niña se enfada. Hilde le canta una canción de posaderas, que es de contar jarras. La niña se calma. {nombre} se la aprende.", Hacer.GENTE),
    )

    private val CRIANZA_PUEBLO = listOf(
        cr("Desde la ventana del cuarto alto se ve el paso entero, y estos días {nombre} lo mira como se mira la casa propia desde la calle: las compañías que suben sin ella, la nieve que se va, las runas que nadie repasa. Le pican los pies. Le dura poco."),
        cr("La cocina de la posada se ha llenado de cosas de niña: una cesta junto al horno, pañales de lino tendidos sobre el fogón, una manta que tejieron las mujeres de la aldea entre todas, con una runa en cada esquina. La de «refugio», las cuatro."),
    )

    private val CRIANZA_ANOCHECER = listOf(
        cr("Anochece, y {nombre} se sienta en la ventana del cuarto alto con la niña dormida, mirando cómo se apaga la luz en el collado, como cada tarde de su vida. Ahora lo mira con alguien. Le cuenta bajito lo que ve. La niña no lo entiende. Ya lo entenderá."),
        cr("Al caer la tarde sube desde la sala el arpa de Tobin, que toca la canción de cuna que hizo antes de que naciera la niña, y la posada entera baja la voz para dejarla dormir. {nombre} la mece al ritmo del arpa. Las dos se duermen antes de que termine."),
    )

    // =====================================================================
    //  Todos los días
    // =====================================================================

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el paso dicen que quien se fía del cielo de la mañana no ha subido nunca."),
        v("{nombre} mira el cielo antes que nada, como manda el oficio: un guía lee las nubes antes que las runas. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En la montaña el tiempo cambia más deprisa que el humor de una mula."),
        v("{cielo} {de_estacion} Los guías tienen un nombre para cada viento, y algunos no se dicen delante de los niños."),
        v("{de_estacion} {cielo} {nombre} lo huele en el aire antes de verlo, como todos los que viven de la montaña."),
        v("Arriba, en las cumbres, el tiempo va siempre un día por delante del de abajo. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está tan azul que duele, y las cumbres parecen al alcance de la mano.",
            "No hay una nube, y se cuentan una a una las piedras de la ladera de enfrente.",
            "Hace un día raso, de los que los guías llaman de mirar lejos.",
        ),
        Clima.NUBES to listOf(
            "Las nubes vienen bajas y se enganchan en las cumbres como lana en una zarza.",
            "Está nublado, con un cielo gris y quieto que no promete nada.",
            "Pasan nubes grandes, y sus sombras corren por las laderas más que un caballo.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve una lluvia fina y fría, de las que no mojan hasta que ya estás empapado.",
            "Llueve, y las piedras brillan y resbalan, y cada arroyo baja con más ganas.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta en las cumbres, y los truenos ruedan por los valles como piedras en un saco.",
            "Se ha puesto negro de golpe, y los rayos caen tan cerca que el aire huele a quemado.",
        ),
        Clima.NIEVE to listOf(
            "Nieva despacio, copos gordos que se posan sin ruido y lo dejan todo callado.",
            "Cae una nieve menuda y seca que se mete por el cuello y por las costuras.",
            "Nieva, y la montaña desaparece detrás de una cortina blanca.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, tan espesa que el mundo se acaba a diez pasos.",
            "La niebla sube de los valles y se queda pegada a las piedras, mojándolo todo sin llover.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento de las cumbres que silba entre las piedras y hace llorar los ojos.",
            "Hace un viento que empuja de lado y obliga a caminar torcido, como un borracho.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y el deshielo baja por todos lados: cada piedra tiene su hilo de agua.",
            "En primavera la nieve se pone blanda y traicionera, y los aludes se despiertan.",
            "Es el tiempo del barro, de las primeras flores y de las prisas.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, el corto verano de la montaña, con flores hasta en las grietas.",
            "En verano las noches siguen frías, aunque el sol del día queme la nuca.",
            "Es el tiempo de los caminos largos y de las tormentas de la tarde.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y los alerces de las laderas se ponen amarillos de arriba abajo.",
            "En otoño llegan las nieblas, y los guías cuentan los días que faltan para la nieve.",
            "Es el tiempo de la leña, de la cecina colgada y de las últimas compañías.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y la nieve lo ha borrado todo: caminos, runas, piedras.",
            "En invierno el día es corto, y la noche entra en las casas a media tarde.",
            "Es el tiempo del frío que raja las piedras y de los cuentos largos junto al fuego.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora va delante de la compañía a su paso, que es lento y no para nunca: el paso de los guías, que los de abajo creen que es de viejos hasta que ven que no lo aguantan. Cada cien pasos mira atrás sin volverse del todo. Cuenta. Siguen todos.", hacer = Hacer.CAMINO),
        v("A esta hora está parada junto a una piedra marcada, quitando con la mano el musgo de la runa para que se lea bien. Es la de «agua cerca». La compañía aprovecha para sentarse. Ella les dice que luego cuesta más. Se sientan igual. Luego cuesta más.", hacer = Hacer.CAMINO),
        v("A esta hora cruza con la compañía el puente de troncos del torrente, de uno en uno, con las mulas tapadas de ojos para que no vean el agua. Ella pasa la primera y la última: va, vuelve, va. El torrente ruge debajo. Un mercader cruza rezando. Llega rezando.", hacer = Hacer.CAMINO),
        v("A esta hora sube por la pedrera, donde el camino se borra entre las piedras sueltas y solo las runas dicen por dónde ir. Detrás, la compañía pisa donde pisa ella, en fila, como patos. Una piedra rueda ladera abajo. Nadie habla hasta que se para.", hacer = Hacer.CAMINO),
        v("A esta hora tiene a la compañía parada en la cornisa, donde el camino tiene el ancho de una mula y media, esperando a que pase otra compañía que baja. Ella y el otro guía se saludan con el bastón. Las mulas se miran mal. Se pasa. Se respira.", hacer = Hacer.CAMINO),
        v("A esta hora sube con la compañía bajo un sol de altura, de los que pican y no calientan, y les hace taparse la nuca con un trapo. Bebe poco y a menudo, y les dice que hagan igual. Un peregrino se bebe la bota entera en la primera fuente. Lo pagará.", hacer = Hacer.CAMINO, meses = VERANO),
        v("A esta hora abre huella en la nieve vieja, con raquetas, hundiendo el bastón delante para saber si debajo hay piedra o hay aire. La compañía va detrás, en su huella. Uno se sale y se hunde hasta la cintura. Lo sacan entre cuatro, riéndose de nervios.", hacer = Hacer.CAMINO, meses = NIEVE_ABIERTO),
        v("A esta hora lleva a Neli, la hija del herrero, pegada a los talones, aunque no es aprendiz ni nadie le ha dado permiso: se ha colado en la compañía con un pan y un palo. {nombre} hace como que no la ve. Neli pregunta por cada runa. {nombre} contesta sin volverse.", hacer = Hacer.CAMINO, desde = "neli", hasta = "neli_baston"),
        v("A esta hora deja que Neli vaya delante un trecho, con su bastón de aprendiz, leyendo las runas en voz alta. Neli se equivoca en una. {nombre} carraspea. Neli corrige sin volverse. La compañía no se entera de nada, que es justo lo que tiene que pasar.", hacer = Hacer.CAMINO, desde = "neli_baston", hasta = "neli_sola"),
        v("A esta hora pasa por la última piedra antes del collado, donde Arn le enseñó, medio tapada de liquen, la runa de «camino que sigue». Nadie más la ve. Ella la toca con el guante al pasar, como quien saluda. Luego sigue. Para eso está.", hacer = Hacer.CAMINO, desde = "runa_vieja"),

        v("A esta hora come de pie, con el bastón apoyado en el hombro, una tira de cecina que hay que masticar como si fuera una discusión. La compañía la mira comer. Si la guía come tranquila, piensan, es que todo va bien. Ella come tranquila siempre. Por eso.", hacer = Hacer.COMER),
        v("A esta hora come en cuclillas, con la espalda contra lo que haya, pared o roca, partiendo el queso con la navaja en trozos iguales para los que se olvidaron de traer. Siempre hay alguno. {nombre} lleva comida de más desde que es guía. Nunca le sobra.", hacer = Hacer.COMER),
        v("A esta hora come deprisa un trozo de pan con la manteca que Hilde le mete en el zurrón envuelta en una hoja de col. Mientras mastica, cuenta cabezas: la compañía, las mulas, otra vez la compañía. Siempre falta uno. Siempre aparece.", hacer = Hacer.COMER),
        v("A esta hora comparte la comida con la compañía, y cada uno saca lo suyo: un mercader, tocino; un peregrino, higos secos; un minero, un pan negro como una piedra. Todo va al medio, sobre un trapo. Así se hace en el paso: lo de cada uno, de todos.", hacer = Hacer.COMER),
        v("A esta hora come lo que Tobin le ha metido en el zurrón sin decírselo: un trozo de bizcocho de miel envuelto en un papel con el dibujo de un pájaro, o de una mula, no está claro. Se lo come mirando la montaña. Guarda el papel entre sus mapas.", hacer = Hacer.COMER, desde = "cancion"),

        v("A esta hora duerme como duermen los que han caminado todo el día: de golpe, boca abajo, sin soñar. Ni las mulas, ni el viento, ni los ronquidos de los viajeros que suben por la pared la despiertan. Solo el cuerno. El cuerno, siempre.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y en el sueño sigue subiendo: las piernas se le mueven bajo la manta, como a los perros. Cuando el sueño llega al collado, se da la vuelta y baja. Nunca llega al otro lado. Al despertar no se acuerda de nada, solo del cansancio.", hacer = Hacer.DORMIR),
        v("A esta hora duerme a ratos, porque un viajero de los que suben mañana pasea por el pasillo, nervioso, y hace crujir las tablas. Ella se tapa la cabeza con la manta. Ya le dirá que el miedo se pasa en la primera cuesta. Casi siempre se pasa.", hacer = Hacer.DORMIR),
        v("A esta hora duerme en el cuarto alto, pegada a Tobin, que llega tarde de tocar con las manos frías y el olor de la sala. Ella se queja sin despertarse del todo. Él se ríe bajito. En el tejado, el viento hace sonar las losas como un arpa mala.", hacer = Hacer.DORMIR, desde = "cuarto_alto"),
        v("A esta hora duerme con un oído despierto, el de las madres, puesto en la cuna que Tobin hizo con una caja de manzanas del valle. La niña respira. Ella respira. Cuando la niña suspira, ella abre los ojos. Luego los cierra. Así toda la noche.", hacer = Hacer.DORMIR, desde = "nace"),

        v("A esta hora está en el establo revisando los cascos de las mulas uno por uno, con la pata de cada una entre las rodillas. La Terca, la más vieja, se deja hacer con cara de santa y luego le pisa el pie. Todos los días. {nombre} ya se lo pone a propósito.", hacer = Hacer.CUIDAR),
        v("A esta hora engrasa monturas y correas con sebo de oveja en el banco del establo, frotando con un trapo hasta que el cuero brilla. Una correa seca se rompe en la peor cuesta, y la peor cuesta está siempre en el peor sitio. Huele a cuero y a mula. Le gusta.", hacer = Hacer.CUIDAR),
        v("A esta hora dibuja en una hoja suelta, en la mesa de la cocina, lo que ha visto hoy en el camino: una piedra caída, un arroyo que ha cambiado de sitio, una runa que se borra. Lo dibuja con carbón, deprisa. Luego lo pasará a tinta, despacio.", hacer = Hacer.CUIDAR),
        v("A esta hora repasa en el libro de crónicas el mapa del paso entero, el que empezó un invierno, y le añade lo de hoy: una fuente nueva, la piedra que se movió. Cada página es un tramo. El collado, de momento, no cabe en ninguna. Ya cabrá.", hacer = Hacer.CUIDAR, desde = "mapa"),
        v("A esta hora le enseña a Neli a curar una mula que cojea: cómo se le levanta la pata, cómo se le mira el casco, cómo se le habla, que es lo que más importa. Neli le habla demasiado. La mula la mira con desprecio. {nombre} se ríe tanto que se tiene que sentar.", hacer = Hacer.CUIDAR, desde = "neli_baston"),

        v("A esta hora está en la sala de la posada, junto al fuego, con una jarra y los pies hacia las brasas, escuchando a un mercader que jura que al otro lado de las montañas vio un lago que hierve. Nadie le cree. Todos quieren que siga.", hacer = Hacer.GENTE),
        v("A esta hora juega a las tabas con los mineros en la mesa larga, y les gana, como siempre, porque tiene la mano más rápida del valle. Los mineros dicen que hace trampa. Ella dice que ellos tienen las manos llenas de hierro. Pagan la ronda.", hacer = Hacer.GENTE),
        v("A esta hora escucha a los guías viejos, que en la mesa del rincón discuten quién cruzó el paso con más nieve, como cada noche. Cada año la nieve de sus historias sube un palmo. {nombre} se calla. Algún día ella también exagerará. Todavía no.", hacer = Hacer.GENTE),
        v("A esta hora ayuda a Hilde a servir, porque la sala está llena de una compañía que sube al amanecer y que no quiere dormir. Lleva jarras, recoge cuencos, pone paz entre dos que se pelean por un banco. Hilde le guiña un ojo. Le perdona la cerveza.", hacer = Hacer.GENTE),
        v("A esta hora escucha tocar a Tobin desde la escalera, sentada en un peldaño, donde él no la ve. O eso cree ella. Tobin toca una cosa lenta, del valle, y la sala se calla. Cuando termina, mira a la escalera. Ella ya no está. Siempre llega tarde a la mirada.", hacer = Hacer.GENTE, desde = "tobin", hasta = "cancion"),
        v("A esta hora está sentada al lado del arpa mientras Tobin toca, pasándole la jarra entre canción y canción. Los mineros les silban. Tobin les dedica una canción de burla que se inventa sobre la marcha. Ellos la cantan con él. Ella se tapa la cara.", hacer = Hacer.GENTE, desde = "cancion"),
    )

    private val PUEBLO = listOf(
        v("{entorno} La posada del Cuerno es una casa larga de piedra y madera al pie del paso, con un cuerno de carnero clavado sobre la puerta y un establo más grande que la casa. Dentro huele a humo, a guiso y a botas mojadas. Fuera, empieza la montaña."),
        v("La aldea son cuarenta casas de piedra con tejados de losa, una herrería, la casa del Gremio, un molino que solo muele cuando baja el agua y una plaza con la piedra donde suena el cuerno. Todas las puertas miran al paso, como si esperaran a alguien. {entorno}"),
        v("{entorno} El camino sale de la posada, cruza el torrente por el puente de troncos y sube en zetas por la ladera hasta perderse en el collado. Cada cierto trecho, una piedra con su runa: agua, peligro, refugio. Quien las lee no se pierde nunca. Casi nunca."),
        v("La casa del Gremio es la más vieja de la aldea, con las paredes llenas de bastones, uno por cada guía que hubo, y una mesa de piedra para apuntar las compañías. Sobre la puerta hay una copia de la Primera Runa. La de verdad está arriba, en el camino. {entorno}"),
        v("{entorno} Desde la puerta de la posada se ve el paso entero: la ladera de los alerces, la pedrera, la cornisa y, arriba, el collado, una muesca entre dos cumbres por donde el cielo se asoma al otro lado. Los viajeros lo miran y tragan saliva."),
        v("Al otro lado del torrente están las vetas de hierro, unas bocas negras en la ladera con sus montones de escoria, y las casas de los mineros, más pobres que las de la aldea y más ruidosas. De día suenan los picos. De noche, las canciones. {entorno}"),
        v("El pino viejo de la posada es más alto que el tejado y más viejo que el Gremio, dicen. Los guías lo usan para saber el viento: si silba, se sube; si gime, no. De sus ramas bajas cuelgan las linternas de los que vuelven tarde. {entorno}", hasta = "pino"),
        v("Delante de la posada sigue el pino viejo partido por el rayo, una mitad verde y la otra negra, de pie, como un guía que no quiere irse. Hilde no deja que lo corten. Dice que todavía da sombra. Da media. {entorno}", desde = "pino"),
        v("{entorno} Desde la aldea, con buena vista, se distinguen los dos refugios del paso: el de la Piedra Partida, a media subida, y el de la Cabra, colgado de la cara norte como un nido de golondrina. Dentro de cada uno hay leña y una vela para quien llegue.", hasta = "alud"),
    )

    /** Primero el refugio del collado (a medio hacer, terminado); si aún no se empieza, la veta nueva. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("refugio", 0.001, 0.999, frases = listOf(
            "Arriba, en el collado donde se perdió el viejo guía, el refugio nuevo sube piedra a piedra.",
            "En el collado se ve el refugio a medio hacer, todavía sin tejado, con su andamio de troncos.",
        )),
        Entorno("refugio", 0.999, 1.0, frases = listOf(
            "En el collado ya está el refugio de Arn, y en la ladera la veta nueva sigue tapada con piedras.",
            "Arriba brilla el tejado del refugio de Arn; abajo, ya nadie entra en la veta tapada.",
        )),
        Entorno("veta", 0.001, 0.6, frases = listOf(
            "Más arriba que las vetas viejas humea la boca de la veta nueva, todavía pequeña.",
            "Por la ladera sube una senda recién marcada, y por ella, en fila, los mineros de la veta nueva.",
        )),
        Entorno("veta", 0.6, 1.0, frases = listOf(
            "La veta nueva está en su mejor momento, y los carros de hierro bajan cargados todo el día.",
            "En la ladera, la veta nueva tiene ya tres bocas y un camino de carros que no para.",
        )),
        Entorno("veta", 0.0, 0.0, frases = listOf(
            "En la ladera, más arriba de las vetas viejas, la roca rojiza sigue entera y callada.",
            "Las vetas viejas de la ladera dan poco, y los mineros miran cada vez más arriba.",
        )),
    )

    private val GENTE = listOf(
        v("Hilde, la posadera del Cuerno, es viuda desde hace tanto que habla del marido como de un tiempo que hizo: bueno, a ratos. Manda con un cucharón de madera y un silencio que da más miedo que el cucharón. A {nombre} le guarda la silla junto al fuego."),
        v("Orla, la maestra del Gremio, enseñó las runas a todos los guías de la aldea, a {nombre} también, con un palo en la ceniza del hogar. Cada invierno ve un poco menos, y ahora lee las runas con los dedos, como si las acariciara. No se equivoca nunca."),
        v("Garmo, el tuerto, guía desde antes de que {nombre} naciera, y presume de haber perdido el ojo con un águila, aunque Hilde dice que fue con una puerta. Sube más despacio que nadie y llega antes que todos. Él dice que es por el ojo que le falta: pesa menos."),
        v("El herrero de la aldea le hace las contras de las botas y las puntas del bastón, y no le cobra, porque dice que si un guía se cae, la culpa es del hierro. Es un hombre enorme y callado que solo canta cuando martillea. Canta muy mal. Martillea muy bien."),
        v("Los mercaderes del valle suben cada año con las mismas mulas y las mismas quejas: el camino, el precio, la cerveza. {nombre} los conoce a todos. Sabe quién se marea en la cornisa y quién trae dulces para los niños de la aldea. A ese lo guía mejor."),
        v("Los peregrinos que van a Torremuda son otra cosa: gente de lejos, callada, con capas gastadas y los pies hechos polvo, que va a ver unas ruinas porque fueron sus abuelos. {nombre} no les pregunta qué buscan. Ellos tampoco lo saben muy bien."),
        v("El viejo Arn le enseñó el paso cuando ella tenía doce años, piedra por piedra, sin explicarle nada: andando delante y esperando a que ella mirara. Todavía guía, con una linterna de mina más vieja que ella. Habla poco. Cuando habla, se apunta.", hasta = "arn_falta"),
        v("Desde que le enseñó la runa de «camino que sigue», el viejo Arn le habla más, como si tuviera prisa por dejarle algo: compañías de hace cuarenta años, pasos que ya no cruza nadie. {nombre} lo apunta todo en hojas sueltas, por si acaso.", desde = "runa_vieja", hasta = "arn_falta"),
        v("El viejo Arn no volvió de la niebla. Lo buscaron tres días con cuernos y a gritos, y la montaña no contestó. En la casa del Gremio, su bastón sigue en su clavo, con los de los guías vivos. {nombre} pasa por delante cada día y no lo mira. Lo ve igual.", desde = "arn_falta", hasta = "linterna_arn"),
        v("Del viejo Arn el deshielo devolvió la linterna, abollada y sin cristal, y nada más. Ahora cuelga en la casa del Gremio junto a su bastón. {nombre} le puso una mecha nueva, aunque nadie la enciende. Le pareció que había que hacerlo.", desde = "linterna_arn"),
        v("Tobin es un arpista del valle que subió a tocar tres noches y se ha quedado, no se sabe hasta cuándo. Tiene los dedos largos, la risa fácil y ningún sentido de la orientación: se pierde yendo al establo. {nombre} dice que eso no tiene arreglo. Lo mira igual.", desde = "tobin", hasta = "cancion"),
        v("Desde la canción, toda la aldea sabe lo de Tobin y {nombre}, y lo comenta con la boca llena. Él sigue en su cuartito de la posada y ella en el suyo, y se encuentran en la escalera más de lo que hace falta. Hilde ha dejado de fingir que no lo ve.", desde = "cancion", hasta = "tobin_pide"),
        v("Tobin le ha pedido que vivan juntos en el cuarto alto de la posada, el de la ventana al paso, y ella le ha dicho que sí, que cuando se cierre el paso, que antes no hay tiempo de mudanzas. Tobin cuenta los días con los dedos. Le sobran dedos.", desde = "tobin_pide", hasta = "cuarto_alto"),
        v("Tobin y {nombre} viven en el cuarto alto de la posada, con una cama ancha, el arpa en un rincón y los mapas de ella clavados en la pared. Él se acuesta tarde y ella se levanta temprano, y se encuentran en medio, a oscuras, que según Tobin es lo mejor del día.", desde = "cuarto_alto", hasta = "espera"),
        v("{nombre} espera un hijo, y Tobin ya le ha compuesto tres canciones de cuna, que toca en la sala para practicar, y que los mineros silban luego por la aldea sin darse cuenta. Ella sigue subiendo al paso. Nadie se atreve a decirle que no. Hilde lo intenta.", desde = "espera", hasta = "nace"),
        v("La niña de {nombre} y de Tobin tiene los ojos de él y los puños de ella, y llora tan fuerte que Hilde dice que será guía, porque se la oirá desde el collado. Tobin le toca el arpa para dormirla. Se duerme él antes.", desde = "nace"),
        v("Cuando {nombre} sube al paso, la niña se queda con Hilde en la cocina de la posada, en una cesta junto al horno, entre el olor del pan y el ruido de las ollas. Hilde le habla como a una clienta, de precios y de quejas. La niña escucha muy seria.", desde = "vuelve_camino"),
        v("Neli, la hija del herrero, tiene trece años, las rodillas siempre peladas y una sola idea en la cabeza: ser guía. Le ha pedido a {nombre} que le enseñe, y desde entonces la espera en la puerta del establo con una pregunta nueva cada vez. A veces, con tres.", desde = "neli", hasta = "neli_baston"),
        v("Neli es aprendiz del Gremio, con el bastón que le dio {nombre}, y lleva las runas apuntadas en el brazo con carbón para no olvidarlas. Su padre, el herrero, le ha forjado una punta para el bastón. Ella la pule cada noche, como quien reza.", desde = "neli_baston", hasta = "neli_tramo"),
        v("Neli ya guía sola su tramo, del puente a la Piedra Partida, y vuelve cada vez con algo que contar: una mula que casi habló, un mercader que le dio una moneda de propina. {nombre} la escucha con una cara seria que le cuesta mucho trabajo.", desde = "neli_tramo", hasta = "neli_sola"),
        v("Neli ha llevado sola una compañía de un lado al otro del paso, y en la aldea ya la llaman guía sin añadir nada. Todavía mira a {nombre} antes de hablar en el Gremio. {nombre} hace como que no se da cuenta. Se da cuenta de todo.", desde = "neli_sola"),
        v("Corvin, el capataz de los mineros, es un hombre ancho como una puerta que abrió la veta nueva con sus manos y la quiere como a un hijo. Bebe poco, paga a tiempo y no grita nunca. Cuando habla de la veta, pone los ojos de quien habla de una mujer.", desde = "veta_nueva", hasta = "bastian"),
        v("Bastián, el hermano pequeño de Corvin, es el minero que más se ríe de la aldea: del frío, de la roca, de su hermano y de sí mismo. Cada vez que {nombre} se lo cruza en la senda de la veta, le pregunta si se casa con él. Ella le dice que mañana.", desde = "guia_mineros", hasta = "bastian"),
        v("Corvin ya no baja a la posada. Desde que la veta se llevó a Bastián, pasa los días sentado en una piedra junto a la boca tapada, mirando las piedras que él mismo puso. {nombre} le sube pan a veces y se lo deja al lado. Al día siguiente el pan ya no está.", desde = "bastian", hasta = "se_van"),
        v("Las casas de los mineros de la veta tapada están vacías desde que Corvin se llevó a las familias al valle. Quedan las puertas atrancadas y un perro que no quiso irse. {nombre} le da las sobras. El perro la acompaña hasta el puente y se vuelve.", desde = "se_van"),
        v("Orla guarda el libro de crónicas del Gremio en un arcón de su casa y no deja que nadie lo saque, ni siquiera {nombre}, que tiene que ir a dibujar sus mapas a su cocina, con Orla al lado oliendo la tinta para saber si es buena. Nunca lo es del todo.", hasta = "cronista"),
        v("{nombre} guarda ahora el libro de crónicas del Gremio, que Orla le puso en las manos sin ceremonia, como quien pasa una olla caliente. Orla sigue yendo a verlo cada semana. Le pide que le lea lo último. Escucha con los ojos cerrados y corrige las comas.", desde = "cronista"),
    )

    private val OBJETO = listOf(
        v("En el paso, una cosa que falla a medio camino es una cosa que mata, así que todo se mira dos veces. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de los guías viejos, que lo aprendieron de otros más viejos."),
        v("Hoy {nombre} se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Los del valle no lo entienden hasta que suben. Luego, sí."),
        v("{dato_objeto} En la aldea no se tira nada: lo que ya no sirve para el camino sirve para la posada."),
        v("Las cosas de un guía son pocas y pesan poco, porque todo lo que se lleva se lleva a la espalda. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("baston", "el bastón", "un bastón de guía", "El bastón",
            "El bastón de un guía es de fresno, de su altura y un palmo más, con punta de hierro y las runas del paso talladas de arriba abajo; cuando un guía muere, su bastón se cuelga en la casa del Gremio, y ya no lo toca nadie."),
        ObjetoCuento("linterna", "la linterna", "una linterna de mina", "La linterna",
            "La linterna de mina es de hierro y de cuerno raspado tan fino que deja pasar la luz; quema sebo, aguanta el viento de la cornisa y se cuelga del bastón o del cuello; en la niebla, su luz amarilla es lo último que se pierde de vista."),
        ObjetoCuento("cuerno", "el cuerno", "un cuerno de carnero", "El cuerno",
            "El cuerno de los guías es de carnero, con la boca de latón; cada guía tiene su toque, y en la aldea se sabe quién vuelve por cómo suena; tres toques largos quieren decir que alguien se ha perdido, y entonces sale todo el mundo."),
        ObjetoCuento("libro", "el libro de crónicas", "un libro de crónicas", "El libro",
            "El libro de crónicas del Gremio tiene tapas de madera forradas de piel y más años que la aldea; en él se apuntan las compañías, los aludes, los inviernos y los que no volvieron; las primeras páginas están en runas, y las últimas, en letra."),
        ObjetoCuento("mapa", "el mapa", "un mapa del paso", "El mapa",
            "Los mapas del paso se dibujan a mano, con tinta de agallas, en piel de cabra raspada; no se pinta el camino, que cambia, sino las piedras marcadas, que no cambian; un buen mapa se lee como una canción: de runa en runa."),
        ObjetoCuento("cuerda", "la cuerda", "una cuerda de cáñamo", "La cuerda",
            "La cuerda de un guía es de cáñamo trenzado, de treinta brazas, y se lleva en bandolera; en la nieve se ata con ella a toda la compañía, a tres pasos uno de otro, para que el que se hunda tenga de dónde lo saquen."),
        ObjetoCuento("cecina", "la cecina", "una tira de cecina", "La cecina",
            "La cecina del paso es de cabra o de oveja, salada y curada al humo del hogar todo el invierno; dura un año, pesa poco y se mastica media hora, y por eso los guías dicen que es comida y entretenimiento a la vez."),
        ObjetoCuento("botas", "las botas", "unas botas de guía", "Las botas",
            "Las botas de un guía son de cuero de vaca engrasado, con suela doble claveteada para agarrar en el hielo; se hacen a medida en el valle y duran tres años, si se cuidan; las nuevas se estrenan en la posada, andando un mes por la sala."),
        ObjetoCuento("capa", "la capa", "una capa de lana", "La capa",
            "La capa de los guías es de lana de oveja sin lavar, que guarda la grasa y deja resbalar el agua, con capucha y un forro de piel en los hombros; de noche, en un refugio, es manta, y en la nieve, si hace falta, es tienda."),
        ObjetoCuento("jarra", "la jarra", "una jarra de barro", "La jarra",
            "Las jarras de la posada del Cuerno son de barro cocido con una raya negra, y cada cliente de siempre tiene la suya colgada de una viga, con su marca; la cerveza oscura sabe distinto en la jarra propia, y nadie bebe en la de otro."),
        ObjetoCuento("pedernal", "el pedernal", "un pedernal", "El pedernal",
            "El pedernal y el eslabón van en una bolsita de cuero con yesca seca de hongo; un guía tiene que saber encender fuego con los dedos helados y el viento en contra, y lo practica en la posada, por las noches, para que no se le olvide."),
        ObjetoCuento("montura", "la montura", "una montura de carga", "La montura",
            "Las monturas de las mulas del paso son de haya forrada de fieltro, con ganchos para las cargas; una carga mal puesta tira a una mula en la cornisa, así que la montura la revisa el guía, nunca el mercader."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas mientras camina, y luego se le olvidan, porque en la montaña no se puede pensar en dos cosas a la vez. A sus {edad} años sabe que la montaña no tiene nada contra nadie. Y que eso es justo lo que da miedo."),
        v("{mundo} {nombre} piensa que una runa es una frase que alguien dejó en una piedra para un desconocido: agua cerca, cuidado, vuelve. Piensa que nadie escribe nada tan amable en ningún otro sitio. Piensa que la gente del valle debería grabar más piedras."),
        v("Piensa en los que se quedaron en el paso, los de las linternas de febrero, cuyos nombres están en el libro con una runa al lado. Piensa que cada guía sabe que el suyo puede acabar ahí. Piensa que por eso los guías se ríen tanto en la posada. Por eso."),
        v("{mundo} A veces, en lo alto del collado, con el viento en la cara y el mundo entero abajo, {nombre} se siente más grande de lo que es. Luego mira atrás, a la compañía que resopla, y se le pasa. Piensa que para eso sirve una compañía."),
        v("Piensa en los mapas que dibuja y en que ninguno estará terminado nunca: cada invierno la nieve mueve una piedra, cada primavera un arroyo cambia de sitio. Piensa que un mapa es una carta que se le escribe a la montaña. La montaña contesta siempre. Tarde."),
        v("{mundo} A sus {edad} años, {nombre} sabe leer el paso entero, piedra a piedra, y no sabe leer un libro sin mover los labios. Piensa que es al revés que casi todo el mundo. Piensa que no se cambiaría. O casi."),
        v("Piensa en Arn, que conocía el paso mejor que nadie y se perdió en él. Piensa que no fue la niebla: volvió atrás por un rezagado, y eso lo hace un guía, no un viejo. Piensa que ella habría vuelto también. Piensa que esa es la parte del oficio que no se enseña.", desde = "arn_falta"),
        v("Piensa en la runa de Arn grabada en la puerta de Torremuda, «camino que sigue», y en quién la grabó, y cuándo. Piensa que hubo guías que llegaron al otro lado mucho antes que ella. Piensa que el paso no termina en el collado. Que a lo mejor no termina.", desde = "runa_puerta"),
        v("Piensa en la niña, que un día querrá subir al paso, porque todos los niños de la aldea quieren. Piensa que le enseñará las runas con un palo en la ceniza, como a ella. Piensa que tendrá miedo cada vez que suba. Piensa que eso no se lo dirá nunca.", desde = "nace"),
    )

    private val MUNDOS = mapOf(
        "veta" to listOf(
            "En la posada se habla del precio del hierro, que sube en cada feria, y de quién se va a hacer rico.",
            "Se dice que los mineros han oído sonar la roca a hueco más arriba de las vetas viejas.",
            "Se habla de que el Gremio quiere marcar más caminos: cada año suben más compañías que guías.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} se sienta en el poyo de piedra de la puerta de la posada a ver cómo la sombra sube por el paso, piedra a piedra, hasta que solo queda luz en el collado. Luego, tampoco. Entonces entra. Siempre espera a que se apague el collado."),
        v("Anochece, y en la aldea se encienden las ventanas una a una, y arriba, en la ladera, las linternas de los mineros que bajan de las vetas hacen una fila de lucecitas que se mueve despacio, como una procesión. {nombre} las cuenta. Bajan todas."),
        v("Al caer la tarde suena en la plaza el cuerno del Gremio, un toque largo, para que los guías que siguen en el paso sepan que abajo se les espera. {nombre} lo oye desde el establo. Le gusta oírlo desde abajo. Arriba, sola, suena distinto."),
        v("Anochece en la posada, y la sala se llena de humo, de voces y del olor del guiso que Hilde remueve desde hace horas. Los viajeros se quitan las botas y las ponen en fila junto al fuego. Huele a pies. {nombre} dice que es el olor del oficio."),
        v("La última luz del día se queda en las cumbres, rosa, mucho después de que el valle esté a oscuras. Los viajeros salen a mirarla con la boca abierta. Los de la aldea no salen: la ven cada día. {nombre} sale igual. Hay cosas que no se gastan."),
        v("Al caer la tarde {nombre} sube un momento a su cuarto antes de bajar a la sala, se quita las botas y se queda descalza en el suelo de madera, sin hacer nada, mirando el techo. Lo hace todos los días. Es su manera de llegar."),
        v("Anochece, y en la sala de la posada Tobin afina el arpa cuerda a cuerda, con la oreja pegada a la madera, y todo el mundo baja la voz sin que nadie lo pida. {nombre} se sienta en la escalera. Desde ahí se oye mejor. Y se le ve la cara.", desde = "tobin"),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año (no nombran a nadie que cambie) ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo de los Caminos, y anoche, con la última campanada, el guía de más años grabó la runa del año en la piedra de la puerta de la posada, a la luz de las antorchas, con toda la aldea mirando desde la nieve. Saltaron chispas. Los niños intentaron cogerlas.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es año nuevo, y con el paso cerrado tampoco habría dónde. Ayuda a Hilde a sacar las mesas a la nieve para el almuerzo de todos, que se come de pie y con la capa puesta: pan de fiesta con miel, cecina nueva y un vino caliente que hace olvidar los pies.",
            Parrafo.PENSAMIENTO to "{nombre} pasa el dedo por la runa nueva, todavía áspera, y piensa en todos los años grabados en esa piedra, uno debajo de otro, hasta los que ya no se leen. Piensa que la piedra es el libro de los que no saben letra. Piensa que este año también acabará ahí, aunque todavía no se sepa qué dirá.",
        ),
        "linternas" to unDia(
            Parrafo.MANANA to "Hoy es la Noche de las Linternas, y desde la mañana la aldea entera limpia sus linternas y les pone mecha nueva, porque esta noche habrá una en cada ventana por los que se quedaron en el paso. Hilde saca de un arcón las de la posada, que son muchas. Hay una de más. Nadie pregunta por qué.",
            Parrafo.ANOCHECER to "Esta noche, al oscurecer, se encienden a la vez todas las linternas de la aldea, una en cada ventana, y la nieve se vuelve dorada hasta donde llega la luz. Nadie habla. Desde la puerta de la posada, la aldea parece una compañía parada en la nieve, esperando a que vuelva alguien.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los nombres del libro de crónicas que llevan al lado la runa de los que no volvieron. Piensa que cada linterna es una frase: aquí hay luz, aquí se te espera. Piensa que los que se perdieron en el paso no pueden verla. Piensa que se enciende igual. Por si acaso.",
        ),
        "abre_paso" to unDia(
            Parrafo.MANANA to "Hoy, antes del alba, tres guías han subido con raquetas hasta la Piedra Partida y han vuelto con la noticia: la nieve aguanta. El cuerno del Gremio ha sonado siete veces, que es el toque de paso abierto, y en la aldea se han abierto todas las puertas a la vez, como si fuera fiesta. Lo es.",
            Parrafo.PUEBLO to "La posada amanece llena de gente que lleva semanas esperando en el valle y ha subido de noche para ser la primera: mercaderes con sal, dos mineros, una familia que va a ver a una abuela. Fuera, el camino es una zanja blanca que sube por la nieve vieja, dura y gris. Hoy vuelve a tener huellas.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el primer día del paso abierto se parece al primer día de cualquier cosa: todos tienen prisa y nadie sabe bien adónde. Piensa que la montaña no ha cambiado en todo el invierno. Piensa que los que han cambiado son ellos. Que siempre son ellos.",
        ),
        "bastones" to unDia(
            Parrafo.MANANA to "Hoy es el día de los bastones, y los guías de la aldea van llegando a la piedra del Gremio con el bastón al hombro, como cada primavera, para repasar las runas talladas, que el invierno ha gastado. Traen cuchillos pequeños, cera y una seriedad de entierro que se les pasa enseguida.",
            Parrafo.TRABAJO to "A esta hora está sentada en la piedra del Gremio con su bastón en las rodillas, repasando con la punta del cuchillo cada runa, de abajo arriba, del puente al collado, como si subiera el paso con los dedos. Al lado, otro guía se ha equivocado en una y la rasca, maldiciendo bajito.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su bastón es el paso entero en pequeño, y que si un día se pierde en la niebla le bastará con tocarlo para saber dónde está. Lo piensa y se ríe de sí misma. Luego le pasa los dedos otra vez, de abajo arriba. Por si acaso, también.",
        ),
        "feria" to TextoEspecial(listOf(
            mapOf(
                Parrafo.MANANA to "Hoy empieza la feria del deshielo, y los mercaderes del valle han subido con sus mulas cargadas de sal, telas, vino y cacharros de cobre, y han llenado la plaza de toldos de colores. Huele a canela, a mula y a barro. Los niños de la aldea no saben adónde mirar.",
                Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es feria, y en feria los guías no suben. Pasea entre los toldos con unas monedas en el puño, regateando por una aguja de coser buena y una cinta roja que no le hace ninguna falta. Se lleva la cinta. La aguja, mañana.",
                Parrafo.GENTE to "Esta tarde, en la plaza, los mercaderes que ha guiado durante años la llaman desde los toldos para darle algo: un puñado de pasas, un trago de vino, una noticia del valle. Uno le regala una piedra de sal tallada en forma de mula. Se parece a la Terca. Se lo dice. Él se ofende.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que la feria es la única vez al año que el valle sube entero a la aldea, en vez de bajar la aldea al valle. Piensa que el paso no es un muro, que es una puerta. Piensa que eso, en la feria, se ve mejor que nunca.",
            ),
            mapOf(
                Parrafo.MANANA to "Hoy es el segundo día de la feria, el de los tratos grandes: los mercaderes venden más barato lo que les queda y compran lo que tiene la aldea, queso, lana, cecina, hierro. Hilde ha vendido diez quesos antes de que salga el sol. Cuenta las monedas dos veces.",
                Parrafo.TRABAJO to "Hoy {nombre} no trabaja tampoco: es el último día de feria. Vuelve a por la aguja de coser y la consigue a mitad de precio, porque el mercader ya quiere irse. Luego ayuda a cargar las mulas de los que bajan, por gusto, y de paso les revisa las monturas. No lo puede evitar.",
            ),
        )),
        "primera_runa" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de la Primera Runa, el día más largo del año, y los guías del Gremio suben juntos hasta la piedra más vieja del camino, la que tiene la primera runa que se grabó, para repasarla con un cincel y untarla de sebo. Detrás va media aldea, con comida y vino.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: sube a la Primera Runa sin compañía que guiar, con el bastón y nada más, entre los otros guías, cantando la canción del camino, que tiene una estrofa por piedra y dura toda la subida. Se equivoca en la de la cornisa. Como todos los años.",
            Parrafo.GENTE to "Esta tarde, junto a la piedra, la aldea come sentada en la hierba lo que ha subido: pan de fiesta, queso, cerezas del valle. Los guías nuevos tocan la runa con la mano por primera vez. Los niños la tocan también, a escondidas. Los guías hacen como que no los ven.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el primero que grabó esa runa, cuando no había camino, solo montaña. Piensa que no sabía si alguien la iba a leer. Piensa que la grabó igual. Piensa que es lo más valiente que se ha hecho nunca en el paso, y que nadie sabe su nombre.",
        ),
        "compania_verano" to unDia(
            Parrafo.MANANA to "Hoy sale la Compañía de Verano hacia Torremuda, la más grande del año, y la plaza está llena desde el alba de peregrinos con sus capas, sus bastones nuevos y sus caras de no saber lo que les espera. Los de la aldea les regalan pan. Ellos lo agradecen como si fuera oro.",
            Parrafo.TRABAJO to "A esta hora sube con la Compañía de Verano hasta el collado, ayudando al guía que la lleva este año, poniendo orden en una fila larga como una procesión. En el collado se despiden. Ella se queda arriba mirando cómo bajan por el otro lado, hasta que son puntos. Luego, nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Torremuda, al otro lado, adonde va cada verano la compañía. Piensa que cada peregrino lleva una Torremuda en la cabeza, y que ninguna es la de piedra. Piensa que a lo mejor eso no importa. Que se viaja igual.",
        ),
        "hierro" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta del Hierro, y los mineros bajan de las vetas a la aldea con la cara lavada, la ropa de fiesta y una herradura colgada del cuello, que es su manera de decir que hoy la montaña les da permiso. En la posada se abre el barril de cerveza oscura más grande del año.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: el Hierro es fiesta para todos, y nadie sube al paso. Ayuda a los mineros a colgar en la plaza las herraduras del año en una cuerda de lado a lado. Suenan con el viento como campanas pequeñas. Debajo se baila.",
            Parrafo.GENTE to "Esta tarde los mineros pagan la cerveza, como manda la fiesta, y la posada se llena de canciones de mina, que son de golpes y de gritos, y de una muy lenta que se canta con los ojos cerrados, por los que se quedó la roca. Todos se ponen de pie. Luego vuelven los golpes.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los mineros, que trabajan dentro de la montaña mientras ella trabaja encima. Piensa que ella ve el cielo todo el día y ellos casi nunca. Piensa que por eso cantan tan fuerte cuando bajan. Piensa que ella también cantaría.",
        ),
        "centeno" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la siega del centeno de la aldea, tres días, y los guías siegan también, como manda la costumbre, porque el pan que se come en el paso sale de estos campos. Están en la ladera baja, en terrazas, y el centeno, alto y gris, se mueve con el viento como el lomo de un animal.",
                Parrafo.TRABAJO to "A esta hora siega con la hoz, en fila con los demás, agachada, cortando el centeno a puñados y dejándolo en gavillas. No se le da bien. Los guías siegan peor que nadie, y los de la aldea se ríen de ellos con cariño. Le sale una ampolla en la mano. La enseña como un trofeo.",
                Parrafo.PENSAMIENTO to "{nombre} piensa, con la espalda rota, que cada torta de centeno que lleva al paso empieza aquí, en esta cuesta, con esta ampolla. Piensa que el camino tiene raíces. Piensa que nunca lo había pensado así. Se le olvidará en cuanto suelte la hoz.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en la siega del centeno, ya con trapos en las manos, atando gavillas detrás de los segadores con un nudo de guía, el que no se suelta nunca. Los de la aldea dicen que ese nudo es de presumir. Lo copian.",
                "A esta hora carga las últimas gavillas del centeno en las mulas para bajarlas a la era, y las guía por la ladera como si fueran una compañía de mercaderes. Las mulas, con centeno, van más contentas que con sal. Ella también.",
            )),
        ),
        "recuento" to unDia(
            Parrafo.MANANA to "Hoy es el recuento del Gremio, y al anochecer los guías se juntarán en la casa del Gremio para oír leer en voz alta las crónicas del año: cada compañía, cada alud, cada mula perdida, cada nacimiento y cada muerte de la aldea. {nombre} se ha lavado el pelo. Es la costumbre.",
            Parrafo.GENTE to "Esta tarde, en la casa del Gremio, lee quien guarda el libro, despacio, con una vela al lado, y los guías escuchan de pie. Cuando se lee una compañía, el guía que la llevó dice «hecho» en voz alta. {nombre} dice «hecho» muchas veces. Cada vez le da un poco de vergüenza.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que oír su año leído en voz alta es como verlo desde el collado: todo parece más pequeño y más ordenado de lo que fue. Piensa que el libro no apunta el miedo, ni el frío, ni las risas. Piensa que alguien tendría que apuntarlos. A lo mejor ella.",
        ),
        "refugios" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy se llenan los refugios del paso para el invierno, como cada año antes de que se cierre: leña, cecina, sal, mantas, pedernal y una vela por refugio, que suben en mulas y a la espalda. Hay que hacerlo en dos días, antes de la nieve. Los guías han salido con estrellas.",
                Parrafo.TRABAJO to "A esta hora sube a la Piedra Partida con una mula cargada de leña y un saco de cecina a la espalda, por un camino que ya tiene escarcha en las piedras. En el refugio apila la leña contra la pared, cuelga la cecina de la viga y deja la vela en su nicho. Todo en su sitio, para quien llegue.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en quien llegará este invierno a la Piedra Partida, perdido o herido, y encontrará la leña, la cecina, la vela. Piensa que no sabrá quién las dejó. Piensa que no hace falta. Piensa que esa es la mejor manera de dar algo: sin estar.",
            )),
            sigue = mapOf(
                Parrafo.TRABAJO to listOf(
                    "A esta hora sube otra carga a los refugios de más arriba, por encima de la cornisa, con un viento que ya huele a nieve. En cada uno deja la leña y la vela, y con un carbón apunta en la pared su runa, debajo de las de los otros guías. La pared es un libro.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Anochece cuando bajan de los refugios con las mulas vacías y las manos negras de leña, y en la posada Hilde los espera con sopa para todos. Los guías comen sin hablar. El paso, detrás, queda listo para el invierno. Ya puede venir.",
                ),
            ),
        ),
        "cierra_paso" to unDia(
            Parrafo.MANANA to "Hoy ha nevado toda la noche en el collado, y al amanecer las runas de arriba ya no se ven. Los guías han subido hasta donde se puede y han vuelto con la nieve por la cintura. El cuerno del Gremio ha sonado una vez, larga y grave: paso cerrado. Hasta la primavera no sube nadie.",
            Parrafo.PUEBLO to "La posada se vacía de golpe: los últimos mercaderes bajan deprisa al valle, antes de que la nieve los pille también abajo, y los mineros de fuera se van con ellos. Hilde cierra las habitaciones que no se usarán y cuenta las mantas. La aldea se queda sola con su invierno.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que cerrar el paso es como cerrar una puerta en una casa grande: se oye menos, se ve menos, y de pronto te das cuenta de lo pequeña que es la habitación donde estás. Piensa que tiene todo el invierno para acostumbrarse. Siempre lo consigue en marzo.",
        ),
        "noche_arpa" to unDia(
            Parrafo.MANANA to "Hoy es la Noche del Arpa, la más larga del año, y en la posada se prepara desde la mañana: se barre la sala, se cuelgan ramas de pino de las vigas, se afinan las cinco arpas de la aldea, y Hilde hace pan de nueces para aguantar hasta el alba. Esta noche no duerme nadie.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es la Noche del Arpa, y entre la fiesta y el paso cerrado la aldea entera descansa. Ayuda a colgar las ramas y a poner las velas, y luego se sienta en la escalera a oír cómo afinan las arpas, que es un ruido feo que promete uno bonito.",
            Parrafo.ANOCHECER to "Esta noche, al oscurecer, empieza a sonar el arpa en la posada, y ya no para hasta que amanece: pasa de mano en mano, de las de los viejos a las de los niños, y cada uno toca lo que sabe. Cuando alguien se equivoca, todos cantan más fuerte para taparlo. Fuera nieva. Dentro no se nota.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la aldea no duerme la noche más larga del año: se la gasta entera tocando, como quien se come el pan antes de que se ponga duro. Piensa que es una manera de decirle al invierno que no da miedo. Piensa que al invierno le da igual. Y a ellos también.",
        ),
        "fin_anio" to unDia(
            Parrafo.MANANA to "Hoy es la última noche del año, y en la casa del Gremio se enciende el fuego grande y se cuelgan los bastones de los guías vivos junto a los de los muertos. Esta noche cada guía nombrará en voz alta los caminos que hizo. {nombre} ya ha contado los suyos tres veces.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es el último día del año, y con el paso cerrado solo queda prepararse para la noche. Se lava, se pone la camisa buena, la que tiene una runa bordada en el cuello, y le saca brillo al cuerno. Hilde le dice que parece una novia. Ella dice que parece una guía.",
            Parrafo.GENTE to "Esta noche, en el Gremio, los guías se ponen de pie uno tras otro y nombran sus caminos: tantas compañías, tantos mercaderes, tantas noches en refugio. Los viejos los dicen de memoria. Los jóvenes, con los dedos. Cuando le toca, {nombre} los dice sin dedos. Se nota que lo ha ensayado.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un año de guía cabe en una lista que se dice en lo que dura un trago. Piensa que lo que no cabe es lo de en medio: la cara de un peregrino en el collado, una mula que se salvó, una niebla. Piensa que eso lo sabe solo ella. Que eso es lo suyo.",
        ),

        // ---------- Los de la temporada 1 ----------
        "mapa" to unDia(
            Parrafo.MANANA to "Hoy {nombre} ha ido a casa de Orla, la maestra del Gremio, con un tintero, tres plumas nuevas y una idea que le ronda desde el otoño: dibujar en el libro de crónicas el paso entero, del puente al otro lado, piedra por piedra. Orla ha escuchado, ha sacado el libro del arcón y ha dicho: empieza.",
            Parrafo.TRABAJO to "A esta hora está a la mesa de Orla con el libro abierto en una página en blanco, que da más miedo que la cornisa en enero. Dibuja el puente con carbón, flojo, para poder borrarlo. Lo borra. Lo vuelve a dibujar. Orla, que ya ve poco, pregunta qué tal va. Ella dice que va el puente.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el paso entero no cabe en un libro, y que lo va a meter igual. Piensa que tardará años. Piensa que cuando termine, cualquiera que sepa leer podrá cruzar sin guía. Piensa que eso le quita trabajo. Piensa que da igual. Que eso es lo que se hace.",
        ),
        "veta_nueva" to unDia(
            Parrafo.MANANA to "Hoy han subido por la ladera de enfrente los mineros de Corvin, con picos, cuerdas y un carro de puntales, más arriba que todas las vetas viejas, hasta una roca rojiza que el capataz lleva dos años mirando. Al final del día han salido de la boca nueva gritando, con una piedra en alto. Hierro.",
            Parrafo.GENTE to "Esta tarde Corvin, el capataz, ha bajado a la posada, ancho como una puerta y cubierto de polvo rojo, y ha pagado una ronda para todos sin decir por qué. No hacía falta. Lo decía la piedra que ha dejado encima de la mesa, que brilla. Los guías se la pasan de mano en mano y la sopesan.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que una veta nueva quiere decir mineros nuevos, carros, compañías, trabajo para todos los guías. Piensa que la montaña da, a veces, de golpe. Piensa que lo que da de golpe a veces lo quita de golpe. Lo piensa y se calla. Hoy es día de ronda.",
        ),
        "guia_mineros" to unDia(
            Parrafo.MANANA to "Hoy {nombre} lleva a la primera compañía de mineros hasta la boca de la veta nueva: doce hombres con sus herramientas y cuatro mulas cargadas de puntales, por una senda que hace una semana no existía. La ha marcado ella estos días, piedra a piedra, con runas recién grabadas que todavía huelen a polvo.",
            Parrafo.TRABAJO to "A esta hora va delante de los mineros por la senda nueva, que arriba cruza una pedrera suelta. Los mineros pisan fuerte, como en la mina, y las piedras ruedan. Ella les enseña a pisar suave. El más joven se ríe y dice que pisará como ella si ella baila con él en la fiesta del Hierro.",
            Parrafo.GENTE to "Esta tarde, en la posada, el minero joven se presenta: se llama Bastián, es el hermano pequeño de Corvin, y se pasa la noche contando que la guía lo ha salvado tres veces en la pedrera, cada vez con más detalles. A la cuarta, la pedrera ya es un precipicio. {nombre} no lo desmiente. Se ríe.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ha hecho un camino nuevo, pequeño, de una semana, y que ya tiene nombre: los mineros lo llaman la senda de la guía. Piensa que cada camino del paso lo hizo alguien, alguna vez. Piensa que nunca se le había ocurrido que pudiera ser ella.",
        ),
        "tobin" to unDia(
            Parrafo.MANANA to "Hoy ha llegado a la posada, a pie y solo, un arpista del valle con el arpa a la espalda envuelta en una manta, preguntando si allí se pagaba por tocar. Hilde le ha dicho que se pagaba en sopa. Él ha dicho que le valía. Se llama Tobin. Ha subido con unas botas de llano, destrozadas.",
            Parrafo.GENTE to "Esta tarde Tobin, el arpista nuevo, se ha perdido yendo al establo, que está a veinte pasos, y {nombre} lo ha encontrado delante de la herrería mirando la puerta como quien mira un mapa. Le ha preguntado si sabía dónde estaba. Él ha dicho que no, pero que el sitio le gustaba mucho.",
            Parrafo.ANOCHECER to "Esta noche Tobin ha tocado en la sala de la posada, y la sala, que no se calla nunca, se ha callado. Tocaba cosas del valle, de ríos y de trigo, que aquí nadie conoce. {nombre} lo ha escuchado desde la escalera. Cuando ha terminado, se ha dado cuenta de que no había bebido de su jarra.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los que suben a la aldea sin oficio de montaña duran lo que tarda en llegar el primer frío de verdad. Piensa que este se irá también. Piensa que es una pena. Piensa en las botas destrozadas. Piensa que le hacen falta unas nuevas, por si acaso.",
        ),
        "tormenta" to TextoEspecial(listOf(
            mapOf(
                Parrafo.MANANA to "Hoy, a media subida, el cielo se ha puesto negro por el collado en lo que se tarda en atar una bota, y {nombre} ha metido a su compañía, ocho mercaderes con sus mulas, en el refugio de la Piedra Partida un momento antes del primer rayo. El segundo ha caído en la pedrera. El tercero ha partido una roca.",
                Parrafo.TRABAJO to "A esta hora está en el refugio de la Piedra Partida con la compañía encerrada, contando la leña, la comida y las mantas, y repartiendo turnos para salir a ver a las mulas, atadas bajo el saliente. Fuera la tormenta no para. Dentro, un mercader llora, otro reza, otro ronca. Ella hace cuentas.",
                Parrafo.ANOCHECER to "Esta noche la tormenta sigue en el collado, y los truenos hacen temblar el refugio piedra por piedra. {nombre} enciende la vela del nicho, la que se deja para quien llegue. Hoy han llegado ellos. Los mercaderes se juntan alrededor de la llama como polillas. Alguien empieza un cuento.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que ha llegado al refugio con tiempo de sobra, un minuto entero, y que ese minuto lo ha ganado mirando el cielo cada cien pasos durante años. Piensa que nadie lo sabrá nunca. Piensa que los mercaderes creen que ha tenido suerte. Les deja creerlo.",
            ),
            mapOf(
                Parrafo.MANANA to "Hoy, el segundo día en el refugio de la Piedra Partida, la tormenta ha amainado al amanecer y ha vuelto enseguida, como quien se ha dejado algo. {nombre} ha salido a ver el camino: hay piedras caídas en la pedrera y un arroyo nuevo donde ayer no había nada. Hoy no se sale.",
                Parrafo.TRABAJO to "A esta hora reparte la última cecina de la compañía en trozos iguales y pone a los mercaderes a jugar a las tabas para que no piensen. Uno les gana a todos y quiere apostar las mulas. {nombre} le dice que las mulas son del paso hasta que estén abajo. Él se ríe. Ella no.",
                Parrafo.ANOCHECER to "Esta noche, por fin, la tormenta se va valle abajo, y el cielo se abre de golpe, lleno de estrellas lavadas. {nombre} sale del refugio y se queda mirando el collado, negro y limpio. Mañana se baja. Dentro, los mercaderes cantan por primera vez en dos días. Desafinan mucho.",
            ),
        )),
        "runa_vieja" to unDia(
            Parrafo.MANANA to "Hoy, bajando del collado con la compañía, el viejo Arn la ha parado junto a una piedra por la que ella ha pasado mil veces y le ha enseñado en una esquina, medio tapada de liquen, una runa pequeña que no había visto nunca. «Camino que sigue», le ha dicho. Ya no la usa nadie.",
            Parrafo.GENTE to "Esta tarde, en la posada, Arn le ha explicado la runa con el dedo mojado en cerveza sobre la mesa: es la de «camino», con una raya más que no termina. Se grababa donde un camino parecía acabarse y no se acababa. Los guías viejos la ponían para los que vendrían detrás. Luego la mesa se ha secado.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ha pasado cien veces por delante de esa runa sin verla, y que a lo mejor hay muchas más así en el paso, esperando a que alguien les quite el liquen. Piensa que Arn ha esperado años para enseñársela. Piensa que eso es una manera de despedirse. No le gusta pensarlo.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "guia_caminos", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + CERRADO_MANANA + TRAVESIA_MANANA + CRIANZA_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + CERRADO_TRABAJO + TRAVESIA_TRABAJO + CRIANZA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + CERRADO_PUEBLO + TRAVESIA_PUEBLO + CRIANZA_PUEBLO,
            Parrafo.GENTE to GENTE + TRAVESIA_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + CERRADO_ANOCHECER + TRAVESIA_ANOCHECER + CRIANZA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
