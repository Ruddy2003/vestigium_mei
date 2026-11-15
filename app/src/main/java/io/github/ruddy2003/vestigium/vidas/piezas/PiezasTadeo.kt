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
 * El cuento de Tadeo, ladrón de piezas y agente de la red en el puerto de Halde, un mundo minero bajo el Directorio
 * (Frente exterior). Es ficción. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026;
 * el «dato de su época» de cada párrafo es aquí un detalle de su puerto, dicho como lo diría alguien que vive en él.
 *
 * Lo propio de este cuento:
 * - Toma el género de *Andor* y nada de la saga: ni sus nombres, ni sus naves, ni sus soldados, ni sus palabras. La voz
 *   es la del tema (`docs/diseno/temas/frente.md`): seca, de frases cortas, sin grandilocuencia.
 * - Su trabajo sale tres veces al día (el desguace, los recados y la ventana de la noche): esos textos valen a cualquiera
 *   de sus horas, no dicen qué comida toca y no dicen que están en la ventana. La ventana de transmisión se cuenta en
 *   el anochecer.
 * - Dos estados: «cresta» (temporada 2, cuatro días escondido junto a la estación de radar; temporada 5, los tres días
 *   antes del golpe) y «escondido» (temporada 3, cuatro semanas en las galerías viejas de la mina).
 * - El pueblo lee el arco «ocupacion» (cuánta Guardia hay en el puerto, y el crucero de anillo encima).
 * - No repite lo que ya dice «Su vida»: el soplete y la báscula, la chaqueta donde esconde lo que vale, la relojera que
 *   le preguntó otra cosa, la radio que habla en cifras, el guiso de raíz y el café de cebada.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG9.kt`):
 * - Hasta `recluta` (temporada 1) es solo un ladrón de piezas: lo de la red sale desde ese día, y la ventana de
 *   transmisión desde `primera_ventana`. Odila es la relojera que le compra piezas; desde `recluta`, su jefa; desde la
 *   `redada` (3), desaparecida; desde `noticia_odila` (3), presa en el Hondo; desde la `fuga` (4), escondida y enferma;
 *   desde `odila_parte` (4), lejos; desde `carta_odila` (5), viva en otro mundo.
 * - Gaspar, desde `primer_recado` (1); preso desde `cae_gaspar` (3); muerto desde `noticia_gaspar` (4).
 * - Lis, desde `llega_lis` (2). Sela espera un hijo de `sela_espera` (1) a `nace_tula` (2); Tula, bebé hasta
 *   `tula_camina` (3). El inspector Morell, de `llega_morell` (3) a `se_va_morell` (5).
 * - La relojería, quemada de la `redada` (3) a `relojeria_abre` (5). La estación de radar de la Cresta, desde `radar`
 *   (2), y ciega desde el `golpe` (5).
 * - Las fiestas y los ritos que vuelven cada año no nombran a Odila, Gaspar, Lis, Tula ni Morell, ni dicen que es de la
 *   red (los dos primeros del año caen antes de `recluta`).
 */
internal object PiezasTadeo {
    private const val CRESTA = "cresta"
    private const val ESCONDIDO = "escondido"

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(12, 1, 2)

    private val MANANA = listOf(
        v("{nombre} se despierta a las nueve con los motores de Abel, que arrancan debajo de su cuarto, en el taller, uno detrás de otro, hasta que la cama tiembla. No hace falta despertador. Se queda un momento quieto, escuchando cuál de los motores falla. Siempre falla alguno."),
        v("Lo primero que oye {nombre} es la sirena del turno de las nueve, que llega desde la montaña con un poco de retraso, como todo lo que viene de la mina. Se levanta, se moja la cara en la palangana y se mira en el trozo de espejo. Tiene polvo rojo en las cejas. Siempre tiene."),
        v("Por la ventana del cuarto de {nombre} se ven las grúas del muelle, quietas todavía, con sus luces rojas apagándose una a una. En el alféizar hay una capa fina de polvo de mineral que no estaba anoche. La limpia con el dedo. Mañana habrá otra."),
        v("{nombre} se despierta porque el gato del taller de abajo le araña la puerta, como todas las mañanas, para que le dé lo que quedó de ayer. Le abre. El gato entra, gris como la pizarra, come y se va sin mirarlo. Es el único del puerto que no le debe nada."),
        v("Antes de vestirse, {nombre} mira la calle desde la ventana, sin acercarse al cristal, como hace siempre: quién está parado en la esquina, qué carro no es de aquí, qué ventana de enfrente está abierta. No hay nada raro. Se pone la chaqueta y baja la escalera de dos en dos."),
        v("{nombre} se despierta con la boca seca del aguardiente del Pozo y la cabeza llena de cuentas que no le salen. Se lava con el agua fría del cubo, se peina con los dedos y se pone la camisa de ayer. En la calle ya pasa el tranvía del primer turno, lleno de mineros dormidos."),
        v("El cristal de la ventana amanece blanco de escarcha por dentro, y {nombre} rasca un agujero con la uña para ver el puerto. Las grúas tienen hielo en los cables. El agua del cubo tiene una costra que hay que romper con el puño. Se lava deprisa, sin mirarse.", meses = INVIERNO),
    )

    /** Cuatro semanas en las galerías viejas de la mina, después de la redada (temporada 3). */
    private fun es(texto: String, hacer: Hacer? = null) = v(texto, estado = ESCONDIDO, hacer = hacer)

    private val ESCONDIDO_MANANA = listOf(
        es("{nombre} se despierta a oscuras, en el hueco de la galería vieja donde duerme, y no sabe si es de día o de noche. Enciende la linterna un segundo, con el trapo rojo delante, para ver el reloj. Son las nueve. Arriba, en el puerto, alguien está abriendo el desguace sin él."),
        es("Lo despierta la gota que cae del techo de la galería, siempre en el mismo sitio, al lado de su cabeza, desde hace días. Ya no la oye cuando está despierto. Solo cuando duerme. Se sienta en la manta, con la espalda contra la roca, y cuenta en voz baja los días que lleva abajo."),
        es("{nombre} se despierta temblando: en las galerías hace el mismo frío a todas horas, un frío de piedra que entra por debajo de la manta. Se frota los brazos. Escucha. Nada: ni pasos, ni voces, ni perros. Solo, muy lejos, la sirena del turno, que llega por la roca como un zumbido."),
    )

    /** Escondido en la Cresta, junto a la estación de radar (temporadas 2 y 5). */
    private fun cr(texto: String, hacer: Hacer? = null) = v(texto, estado = CRESTA, hacer = hacer)

    private val CRESTA_MANANA = listOf(
        cr("{nombre} se despierta entre las rocas de la Cresta, metido en el saco, con la cara mojada de rocío y el cuerpo entumecido. A trescientos pasos, detrás de la alambrada, la antena de la estación de radar da vueltas despacio, como cada minuto desde que llegaron. No ha parado en toda la noche."),
        cr("Lo despierta el otro de la célula con una mano en el hombro: le toca la guardia. {nombre} se arrastra hasta el borde de las rocas con los prismáticos y mira la estación de radar, abajo, con sus barracones y su antena que gira. Apunta en un papel la hora del cambio de turno."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En Halde el tiempo se mira en el muelle, por el color del humo de la mina."),
        v("{nombre} mira el cielo desde la ventana, entre las grúas, como lo miran los estibadores antes de bajar al muelle. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los viejos del puerto dicen que el polvo de la mina cambió el cielo de Halde, y que antes era de otro color."),
        v("{cielo} En {estacion} el puerto huele distinto: a ladrillo mojado, a hierro, a carbón de las estufas del barrio."),
        v("{de_estacion} {cielo} La radio de la Prefectura da el parte del tiempo cada mañana, y es lo único que dice que casi siempre acierta."),
        v("En el puerto el cielo se ve a trozos, entre chimeneas, grúas y cables, y nadie lo mira mucho rato. {cielo} {de_estacion}"),
    )

    private val ESCONDIDO_TIEMPO = listOf(
        es("Abajo, en las galerías, no hay tiempo: solo la gota que cae del techo y el aire quieto, que huele a piedra. {nombre} sabe que arriba es {estacion} porque el frío de la roca ha cambiado."),
        es("{nombre} no ve el cielo desde hace días. Por el pozo de la galería baja a veces un poco de aire de fuera, y por el olor adivina el tiempo que hace arriba. {cielo}"),
    )

    private val CRESTA_TIEMPO = listOf(
        cr("En la Cresta el tiempo se ve venir desde lejos, por encima del puerto. {cielo} {de_estacion} Arriba hace más frío que abajo, siempre."),
        cr("{cielo} Desde las rocas de la Cresta el puerto se ve pequeño, con su humo y sus grúas, y el tiempo le pasa por encima como a todo lo demás."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un color pálido, y las patrulleras se ven desde muy lejos.",
            "Hace un día claro y frío, y el sol saca brillo al mármol de la Prefectura.",
            "No hay nubes, y desde el muelle se ve la Cresta entera, roca por roca.",
        ),
        Clima.NUBES to listOf(
            "El cielo está bajo y gris, del mismo color que el humo de la mina.",
            "Pasan nubes pesadas que se enganchan en lo alto de las grúas.",
            "Está nublado, con esa luz de Halde que no hace sombra.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fina que lava el polvo de los tejados y corre roja por las cunetas.",
            "Cae un agua fría, y el ladrillo del puerto se pone casi negro.",
            "Llueve, y en el desguace los cascos viejos suenan como tambores.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, y los rayos caen en la Cresta uno detrás de otro.",
            "Truena sobre el puerto, y las patrulleras se quedan en tierra.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y la nieve se pone gris antes de llegar al suelo.",
            "Cae nieve sobre el puerto, y las grúas parecen de azúcar hasta que pasa el primer tranvía.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, una niebla amarilla con olor a carbón, y no se ve el otro lado del muelle.",
            "La niebla tapa la montaña, y la mina solo se adivina por sus sirenas.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento de la montaña, que mete por las rendijas el polvo de la mina.",
            "Hace viento, y los cables de las grúas silban toda la mañana.",
        ),
        Clima.POLVO to listOf(
            "El viento trae polvo de mineral, rojo y fino, y todo el mundo anda con un pañuelo en la boca.",
            "Hay polvo en el aire, y el sol se ve como una moneda de cobre.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en los solares del barrio crece una hierba que nadie plantó.",
            "En primavera el deshielo de la Cresta baja por las calles hasta el mar.",
            "Es el tiempo en que la mina abre sus galerías nuevas.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el calor del ladrillo dura hasta muy entrada la noche.",
            "En verano el polvo de la mina no se va nunca del todo.",
            "Es el tiempo de los convoyes de mineral.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y el viento de la montaña empieza a oler a nieve.",
            "En otoño el desguace enciende sus focos antes de la hora de salir.",
            "Es el tiempo del censo y de las colas.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y el agua del lavadero humea en la calle.",
            "En invierno oscurece antes de que salga el turno de la tarde.",
            "Es el tiempo de los vales de carbón y de las estufas encendidas.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora {nombre} corta con el soplete el casco del desguace, con las gafas de cristal verde y la cara pegada al calor. El metal se abre despacio, rojo, y cae por el otro lado con un ruido que se oye en medio puerto. Brun, desde su caseta, cuenta los golpes. Por los golpes sabe si se trabaja.", hacer = Hacer.TRABAJO),
        v("A esta hora arranca cables de las paredes de una cabina, metido hasta la cintura en un hueco que huele a aceite quemado. Los cables van al saco de la báscula. Una válvula de cobre, entera, con su hilo dentro, va a otro sitio: al bolsillo de dentro de la chaqueta, que es más hondo de lo que parece.", hacer = Hacer.TRABAJO),
        v("A esta hora lleva al hombro una caja de piezas por las calles del puerto, para un taller del muelle que la pidió ayer. Pesa. Camina por el lado de la sombra, con paso de quien no tiene prisa, y saluda a quien conoce. Nadie le pregunta qué lleva. En el puerto todo el mundo lleva algo.", hacer = Hacer.TRABAJO),
        v("A esta hora pesa la chatarra del día en la báscula de Brun, saco por saco, y Brun apunta las cifras en su libro con un lápiz que moja en la lengua. Siempre apunta un poco menos de lo que marca la aguja. {nombre} lo ve y no dice nada. Lo que se lleva en la chaqueta tampoco lo apunta nadie.", hacer = Hacer.TRABAJO),
        v("A esta hora sube con un carro de piezas hasta el taller de la Compañía, en la falda de la montaña, donde arreglan las vagonetas de la mina. El capataz paga en vales y mira las piezas como si fueran robadas. Lo son. No de donde él cree. Firma el recibo con una letra que no es la suya.", hacer = Hacer.TRABAJO),
        v("A esta hora arregla en un rincón del desguace una radio vieja que le encargaron en el mercado, con un destornillador fino y una lupa que le prestó la relojera. La radio vuelve a la vida con un chasquido y una música de baile de la Prefectura. {nombre} la apaga enseguida. Funciona. Con eso basta.", hacer = Hacer.TRABAJO),

        v("A esta hora lleva para la red un paquete envuelto en hule que no ha abierto, metido en un saco de tornillos, hasta una puerta del barrio donde nadie le abre: hay que dejarlo detrás del cubo de la basura y seguir andando. No mira atrás. Dobla la esquina. Ya no es suyo.", hacer = Hacer.TRABAJO, desde = "recluta"),
        v("A esta hora repasa de memoria las calles por las que hoy no puede pasar, las caras que no tiene que saludar y la palabra que tiene que decir si le preguntan por las válvulas. Lo repasa tres veces mientras corta, mientras pesa, mientras camina. La red no apunta nada. Todo va en la cabeza.", hacer = Hacer.TRABAJO, desde = "recluta"),
        v("A esta hora esconde un cartucho de datos dentro de una pieza de motor, en un hueco que ha hecho él mismo con la lima, y tapa el hueco con grasa. La pieza va en el carro con las demás. Si la abren, solo verán grasa. Si la pesan, pesará lo que tiene que pesar. Lo ha comprobado dos veces.", hacer = Hacer.TRABAJO, desde = "primer_recado"),
        v("A esta hora prepara la ventana de esta noche: memoriza la hora, mira dos veces el papel de la llave de cifrado y lo vuelve a enrollar en el lápiz, dentro de la chaqueta. Si alguien le pregunta qué hace, dirá que piensa en una chica. No le preguntará nadie. En el puerto nadie pregunta.", hacer = Hacer.TRABAJO, desde = "primera_ventana"),

        v("A esta hora come en el puesto de la esquina, de pie, apoyado en el mostrador: un tazón de guiso de raíz con un trozo de pan negro para mojar, y el café de cebada en un vaso de lata. Rufa, la del puesto, le sirve sin preguntar. Le cobra lo de siempre. Él le deja una moneda de más, como siempre.", hacer = Hacer.COMER),
        v("A esta hora come sentado en el ala de un casco del desguace, con los otros chatarreros, lo que cada uno trajo envuelto en papel de periódico: pan, cebolla, un trozo de tocino, un huevo duro. Se cambian las cosas sin hablar. Desde el ala se ve todo el muelle, y las patrulleras que pasan.", hacer = Hacer.COMER),
        v("A esta hora come en su cuarto, sentado en la cama, una lata de pescado del muelle con galletas de la Compañía, que saben a harina y a nada. Mastica mirando la ventana. Abajo, en el taller, Abel canta mal una canción de antes. El gato, en la escalera, espera la lata vacía.", hacer = Hacer.COMER),

        v("A esta hora cruza el mercado por el pasillo de las sopas, se para en un puesto de tornillos, compra uno que no necesita y mira en el cristal del puesto de enfrente quién viene detrás. Una mujer con un niño. Un minero. Nadie. Sigue andando. Dos calles después, lo vuelve a hacer.", hacer = Hacer.CAMINO),
        v("A esta hora camina por las calles de ladrillo del barrio, que se cruzan como hilos enredados, y no va nunca dos veces seguidas por el mismo camino. Hoy da la vuelta por el lavadero, por la escalera de los pescadores y por el patio de los toneles. Tarda el doble. No le importa.", hacer = Hacer.CAMINO),
        v("A esta hora va en el tranvía del puerto, colgado del estribo, con la cara al viento, entre estibadores que vuelven del muelle y mineros de permiso. El tranvía chirría en cada curva. En la parada de la Prefectura suben dos guardias, y {nombre} se baja en la siguiente, sin prisa, como si fuera la suya.", hacer = Hacer.CAMINO),

        v("A esta hora duerme en su cuarto encima del taller, de lado, con la chaqueta en la silla, al alcance de la mano. Cada tanto pasa una patrullera por encima del barrio y la luz de su anillo cruza el techo del cuarto, de una pared a la otra. Duerme igual. Lleva toda la vida durmiendo con esa luz.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y en el sueño corta un casco que no se acaba nunca, con un soplete que no quema. Se despierta un momento con el ruido de un carro en la calle, escucha hasta que se aleja y se vuelve a dormir. Es un ruido del puerto. Los conoce todos.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, como duermen los que cargan chatarra todo el día, con la boca abierta y una mano debajo de la almohada, donde guarda los vales. El gato del taller ha subido por la ventana y duerme a sus pies. Ninguno de los dos se entera de nada hasta las nueve.", hacer = Hacer.DORMIR),

        v("A esta hora está en el Pozo, el bar de abajo, que es un sótano de ladrillo con tres mesas, una gramola y una barra de chapa. Yusef le sirve un vaso de aguardiente de raíz con una ficha de latón. {nombre} se lo bebe despacio, de espaldas a la pared, mirando quién baja la escalera.", hacer = Hacer.GENTE),
        v("A esta hora juega a las cartas en el Pozo con tres estibadores, por fichas, en la mesa del fondo, la que no se ve desde la escalera. Pierde un poco. Gana un poco. Escucha más de lo que juega: qué carguero llega, quién se fue, qué dijo la Guardia en el muelle. En el Pozo todo se sabe antes que en la Prefectura.", hacer = Hacer.GENTE),
        v("A esta hora está en la barra del Pozo, con un vaso en la mano, oyendo la gramola, que solo tiene seis discos y los toca todos cada noche. Un minero viejo canta encima de uno. Yusef seca vasos con un trapo y no mira a nadie, que es su manera de mirar a todos.", hacer = Hacer.GENTE),
    )

    private val ESCONDIDO_TRABAJO = listOf(
        es("A esta hora recorre la galería vieja con la linterna tapada, contando los pasos, para aprenderse de memoria las salidas: la del río, la del pozo de ventilación, la del cementerio de vagonetas. Si un día bajan a buscarlo, tendrá que correr a oscuras. Cuenta otra vez.", Hacer.TRABAJO),
        es("A esta hora no tiene nada que hacer y eso es lo más difícil. Afila el cuchillo contra una piedra. Arregla la correa de la linterna. Escribe en la cabeza lo que le dirá a Sela cuando la vea. Lo borra y lo vuelve a escribir. Las galerías no tienen prisa. Él sí.", Hacer.TRABAJO),
        es("A esta hora come lo que le bajó Yusef la última vez, por el pozo viejo, en una cesta atada a una cuerda: pan duro, una lata de pescado, un trozo de queso y agua del río de abajo. Come despacio, para que dure. No sabe cuándo volverá a bajar la cesta.", Hacer.COMER),
        es("A esta hora va de un hueco a otro de la galería, agachado, sin linterna, con la mano en la pared, porque quedarse siempre en el mismo sitio deja olor, migas y huellas. El techo es bajo. Se da un golpe en la cabeza. No dice nada. Abajo no se dice nada.", Hacer.CAMINO),
        es("A esta hora duerme a ratos, envuelto en dos mantas, en el hueco más seco de la galería, con la linterna apagada en la mano. Cualquier ruido lo despierta: una piedra que cae, el agua, una rata. Se queda quieto hasta que vuelve el silencio. Luego vuelve a dormir, un rato más.", Hacer.DORMIR),
        es("A esta hora espera junto al pozo viejo la cesta de Yusef, que baja dos noches por semana con comida y un papel escrito con letra pequeña. Cuando baja, lee el papel con la linterna tapada y lo quema con la llama de una cerilla. Cuando no baja, se queda esperando igual, con la cara vuelta hacia arriba.", Hacer.GENTE),
        es("A esta hora, cuando calcula que arriba el Pozo está lleno, se sienta con la espalda contra la roca y se imagina la mesa del fondo: las cartas, el humo, Yusef secando vasos. Se sabe de memoria los seis discos de la gramola. Los canta por dentro, uno detrás de otro, sin mover la boca.", Hacer.GENTE),
    )

    private val CRESTA_TRABAJO = listOf(
        cr("A esta hora vigila la estación de radar tumbado entre las rocas, con los prismáticos, y apunta en un papel todo lo que se mueve: cuándo sale el camión del agua, cuántos hombres cambian el turno, cuánto tarda la antena en dar la vuelta. El viento le mete el polvo en los ojos. No parpadea.", Hacer.TRABAJO),
        cr("A esta hora escucha la radio de la estación con un receptor pequeño que le cabe en la mano, con el auricular metido en la oreja, y apunta las frecuencias que cambian. Habla en voz baja con el otro de la célula, que apunta otra cosa. No se miran. Los dos miran la antena.", Hacer.TRABAJO),
        cr("A esta hora come entre las rocas de la Cresta lo que subieron en la mochila: pan, cecina dura, un puñado de higos secos y agua de la cantimplora, que ya sabe a metal. No pueden hacer fuego. El humo se vería desde la estación. Comen frío y despacio, sin quitar los ojos de la antena.", Hacer.COMER),
        cr("A esta hora cambia de escondite: se arrastra entre las rocas, cuesta arriba, hasta otro hueco desde el que se ve la puerta de la estación. Tarda una hora en hacer cien pasos. Cuando llega, se queda tumbado, con la cara contra el suelo, hasta que el corazón se le calma.", Hacer.CAMINO),
        cr("A esta hora duerme en el saco, en el hueco de las rocas, mientras el otro de la célula hace la guardia. Duerme con las botas puestas. Sueña con la antena de la estación, que gira, y gira, y en el sueño lo mira a él. Se despierta cuando le tocan el hombro.", Hacer.DORMIR),
        cr("A esta hora habla en voz baja con el otro de la célula, tumbados lado a lado bajo una manta, de cosas que no son la misión: de qué comerán al bajar, de un perro que tuvo de niño, del Pozo. Hablar bajito calienta. Cuando pasa la luz de la estación por las rocas, se callan.", Hacer.GENTE),
    )

    private val PUEBLO = listOf(
        v("{entorno} El puerto de Halde es de ladrillo: almacenes largos con los tejados de chapa, grúas negras sobre el muelle, chimeneas que echan humo día y noche y, al fondo, la montaña de la mina, con su funicular de vagonetas que bajan llenas de mineral y suben vacías. Todo está cubierto de un polvo rojo que no se va."),
        v("El desguace de Brun ocupa un trozo de costa al final del muelle: cascos de naves del Directorio puestos en fila sobre la grada, como animales grandes que se murieron mirando el mar, y entre ellos montañas de chapa, cables y motores. En medio, la caseta de Brun y la báscula. {entorno}"),
        v("{entorno} El mercado del puerto son tres naves de hierro y cristal, con cien pasillos de puestos: piezas, sopas, ropa de trabajo, tabaco de raíz, radios viejas, pescado del muelle. Se paga en vales de la Compañía, y por debajo se cambia de todo. Es el sitio más ruidoso de Halde, y por eso el más seguro."),
        v("La Prefectura es lo único del puerto que brilla: un edificio de mármol blanco traído de la Capital, con columnas, escalinata y una cúpula de bronce, en medio de una plaza que se barre dos veces al día. En la puerta hay siempre dos guardias. Desde cualquier calle se ve la cúpula. Está puesta para eso. {entorno}"),
        v("{entorno} El barrio de ladrillo sube del muelle hacia la montaña en calles estrechas y empinadas, con patios donde se tiende la ropa y se crían gallinas. Abajo de todo está el lavadero de mineral, y su agua corre roja por la calle principal hasta el mar. Los niños juegan a saltarla."),
        v("La mina de la Compañía está en la montaña, al este, y desde el puerto se ve su boca, sus chimeneas y la fila de luces del camino. Tres veces al día suena su sirena, y tres veces al día cambian los turnos en el tranvía de la falda. Más abajo, detrás de una alambrada, está el Hondo. {entorno}"),
        v("Desde cualquier calle del puerto se ve arriba, en la Cresta, la estación de radar del Directorio: unos barracones grises y una antena grande que da vueltas día y noche, con una luz ámbar en la punta. De noche, la luz cruza el cielo cada minuto. La gente ya ni la mira. {entorno}", desde = "radar", hasta = "golpe"),
        v("{entorno} Arriba, en la Cresta, la estación de radar está a oscuras desde la noche del golpe: la antena, quieta, torcida hacia el mar, y los barracones sin una luz. El Directorio dice que la está arreglando. Desde el puerto no se ve que nadie la arregle. La gente la mira cada noche, por si acaso.", desde = "golpe"),
        v("En el mercado, en el pasillo del fondo, la relojería de Odila es un hueco negro: la puerta arrancada, el techo de vigas quemadas, los cristales en el suelo, y en la pared un sello de la Oficina que nadie se atreve a quitar. La gente pasa por delante sin mirar. Los niños, sí. {entorno}", desde = "redada", hasta = "relojeria_abre"),
        v("{entorno} En el pasillo del fondo del mercado, donde estuvo la relojería quemada, hay otra vez un puesto abierto: tablas nuevas sobre las vigas negras, un rótulo pintado a mano que dice «Radios» y una bombilla encendida. Las paredes siguen negras. Nadie las ha pintado. Nadie quiere.", desde = "relojeria_abre"),
    )

    private val ESCONDIDO_PUEBLO = listOf(
        es("Las galerías viejas de la mina se cerraron hace treinta años, después de un hundimiento, y nadie baja a ellas: túneles de techo bajo con los puntales podridos, vías oxidadas, vagonetas volcadas y agua que corre por el suelo hacia un río que no se ve. {nombre} las conoce desde niño. Su padre le enseñó la entrada."),
        es("Por el pozo de ventilación de la galería, muy arriba, se ve un trozo de cielo del tamaño de una moneda, y por él bajan los ruidos del puerto: la sirena de la mina, el tranvía, los perros de la Guardia. {nombre} los escucha como quien escucha una radio. Así sabe que el puerto sigue ahí."),
    )

    private val CRESTA_PUEBLO = listOf(
        cr("La estación de radar de la Cresta es pequeña vista de cerca: tres barracones de chapa gris, un generador que ronca, una alambrada con focos y la antena, más grande que todo lo demás, que gira sobre una torre de hierro. Dentro hay una docena de hombres. Fuera, piedras y viento. Y ellos dos."),
        cr("Desde las rocas de la Cresta se ve todo el puerto de Halde de una vez: el muelle, el desguace con sus cascos, el mercado, la cúpula de la Prefectura, el barrio de ladrillo y el mar. {nombre} busca la ventana de su cuarto. La encuentra. Está a oscuras, como tiene que estar."),
    )

    /** La Guardia que hay en el puerto (arco «ocupacion»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("ocupacion", 0.0, 0.45, frases = listOf(
            "Por el muelle pasan los de la Guardia de dos en dos, con los abrigos grises abiertos y los fusiles al hombro, sin mucha prisa.",
            "Una patrullera cruza el cielo del puerto cada tantas horas, baja y despacio, y los estibadores ya ni levantan la cabeza.",
        )),
        Entorno("ocupacion", 0.45, 0.8, frases = listOf(
            "En cada salida del mercado hay un control de la Oficina, con una mesa, una lámpara y una fila de gente con la credencial en la mano.",
            "La Guardia patrulla ahora de cuatro en cuatro, y en las esquinas del muelle hay sacos terreros que antes no estaban.",
        )),
        Entorno("ocupacion", 0.8, 1.0, frases = listOf(
            "Sobre el puerto cuelga el crucero de anillo del Directorio, tan grande que tapa un trozo de cielo, y de noche su anillo se ve encendido desde todas las ventanas.",
            "Desde que llegó el crucero la Guardia está en todas partes: en los tejados, en las grúas, en la puerta del Pozo, y nadie camina por el medio de la calle.",
        )),
    )

    private val GENTE = listOf(
        v("Brun, el dueño del desguace, es un hombre ancho y calvo que no ha cortado un casco en su vida y que sabe lo que vale cada tornillo de una nave. Paga poco, paga tarde y no pregunta nada. A {nombre} le dice chico, aunque tiene treinta y tantos. Es lo más parecido a un cariño que sabe decir."),
        v("Yusef, el del Pozo, es un hombre flaco y callado con un delantal de cuero que tiene más años que el bar. Sirve el aguardiente con una ficha de latón y se acuerda de lo que bebe cada uno y de con quién habla. Nunca le ha contado a nadie nada. Por eso le cuentan todo."),
        v("Sela, la hermana de {nombre}, es seis años menor que él y trabaja en el lavadero de mineral, con las manos metidas en agua roja diez horas al día. Tiene la risa de su madre y el mal genio de su padre. Le riñe por todo, le remienda las camisas y le guarda la mitad de su pan."),
        v("Abel, el del taller de motores, le alquila el cuarto de arriba por poco dinero y mucha conversación. Es viejo, tiene una pierna de hierro desde un accidente en el muelle y arregla todo lo que tiene motor. El gato gris del taller es suyo, o eso dice Abel. El gato no ha dicho nada."),
        v("En el desguace trabajan cuatro chatarreros más: el Pardo, que fue minero y tose; los dos hermanos Iriarte, que se pelean entre ellos y con nadie más; y un aprendiz de quince años que quiere ser piloto y se sienta en todas las cabinas antes de que las corten. {nombre} le enseña a usar el soplete."),
        v("El sargento de la puerta del desguace es un hombre de la Guardia con la nariz rota y un abrigo gris demasiado grande, que cobra una moneda por cada carro que sale sin mirar lo que lleva. {nombre} le paga cada semana. El sargento le da las gracias. Los dos saben que es un negocio."),

        v("Odila, la relojera del mercado, tiene un puesto al fondo del pasillo de las radios, lleno de relojes parados y de cajas de piezas. Lleva años comprándole a {nombre} lo que saca de los cascos, sin preguntarle nunca de dónde sale. Mira las piezas con una lupa metida en el ojo. A él, con el otro.", hasta = "recluta"),
        v("Odila, la relojera, es la que manda en la célula, aunque nunca lo dice. Es pequeña, tiene el pelo blanco recogido con un lápiz y unas manos que no tiemblan cuando sueldan ni cuando mienten. En su puesto se arreglan relojes y radios. Detrás del puesto, en el cuarto de atrás, se arregla todo lo demás.", desde = "recluta", hasta = "redada"),
        v("De Odila no se sabe nada desde la noche en que la Oficina quemó la relojería. Nadie la vio salir. Nadie vio cómo se la llevaban. En el mercado dicen que está en la Capital, o muerta, o en el Hondo, según quién lo diga. {nombre} no dice nada. Espera un mensaje que no llega.", desde = "redada", hasta = "noticia_odila"),
        v("Odila está viva, en el Hondo, la mina penal de la montaña, donde los presos sacan mineral encadenados por parejas. Lo dijo un mensaje cifrado. Nadie sabe cuánto aguantará allí una mujer de su edad. {nombre} piensa en ella cada vez que suena la sirena de la mina, y la sirena suena tres veces al día.", desde = "noticia_odila", hasta = "fuga"),
        v("Odila está escondida en un cuarto sin ventanas detrás del sótano del Pozo, desde la noche de la fuga. Está flaca, tose y no puede andar sin bastón, pero sigue dando órdenes con la misma voz, desde la cama. Yusef le sube sopa. {nombre} le baja noticias. Ella las escucha con los ojos cerrados.", desde = "fuga", hasta = "odila_parte"),
        v("Odila se fue de Halde en la bodega de un carguero de mineral, escondida entre los sacos, con un nombre falso y una tos que no se le curaba. {nombre} la ayudó a bajar la escalera del muelle. Ella le dio un beso en la frente y le dijo que no la buscara. No sabe si llegó a ningún sitio.", desde = "odila_parte", hasta = "carta_odila"),
        v("Odila vive en otro mundo, lejos, con otro nombre, y está viva: lo dice una carta sin firma que le llegó dentro de un paquete de piezas, con su letra pequeña y torcida. La carta habla del tiempo y de un jardín. {nombre} la ha leído tantas veces que se la sabe. No dice nada de la red. Lo dice todo.", desde = "carta_odila"),

        v("Gaspar es el más viejo de la célula, un minero ancho que bajó a la mina cuarenta años y salió con los pulmones negros y las manos como palas. Tiene un taller de soldadura en el muelle, que es donde recibe los recados. Se ríe con toda la cara. Le enseña a {nombre} a mentir sin parpadear.", desde = "primer_recado", hasta = "cae_gaspar"),
        v("A Gaspar se lo llevaron del muelle con una radio de maleta en el carro, y desde entonces está en el Hondo, o eso se dice. Su taller de soldadura tiene la persiana bajada y el sello de la Oficina. {nombre} pasa por delante cada día, por el otro lado de la calle, sin mirar. Gaspar no dijo ningún nombre. Se nota.", desde = "cae_gaspar", hasta = "noticia_gaspar"),
        v("Gaspar murió en el Hondo, en el invierno, de los pulmones o del frío, y nadie le devolvió el cuerpo a nadie. En el taller de soldadura del muelle hay ahora un hombre de la Compañía que arregla vagonetas. {nombre} lleva en el bolsillo el mechero de Gaspar, que funciona a la tercera, como funcionaba él.", desde = "noticia_gaspar"),
        v("Lis es la radiooperadora de la célula, la más joven, una chica delgada del barrio de los pescadores que se sabe de memoria todas las frecuencias y que cifra un mensaje más rápido de lo que {nombre} lo lee. Habla poco, fuma mucho y escucha la radio como otros rezan. No le tiene miedo a nada. Eso a él le da miedo.", desde = "llega_lis"),
        v("Sela espera un hijo para el invierno. Sigue yendo al lavadero cada día, con la tripa cada vez más grande debajo del delantal mojado, y no deja que nadie le diga que pare. {nombre} le lleva leche de cabra del mercado cada vez que puede. Ella dice que no hace falta. Se la bebe entera.", desde = "sela_espera", hasta = "nace_tula"),
        v("Tula, la hija de Sela, es una niña pequeña de ojos negros que duerme en un cajón forrado con mantas, en el cuarto de encima del lavadero. Cuando {nombre} la tiene en brazos, se queda quieto como en un control de la Guardia, sin respirar. Sela se ríe de él. La niña, también.", desde = "nace_tula", hasta = "tula_camina"),
        v("Tula, la hija de Sela, ya corre por el patio del lavadero detrás de las gallinas, y se cae, y se levanta, y vuelve a correr. Cuando ve a {nombre} en la puerta, le pide que la suba a los hombros para ver el puerto. Él la sube. Desde allí arriba, Tula le cuenta las grúas.", desde = "tula_camina"),
        v("El inspector Morell, de la Oficina de Seguridad, es un hombre joven y educado, con el abrigo azul siempre abrochado y unas gafas redondas que se limpia cuando piensa. No grita nunca. Pregunta. En el puerto dicen que en el último mundo donde lo mandaron no quedó en pie una sola célula.", desde = "llega_morell", hasta = "se_va_morell"),
    )

    private val ESCONDIDO_GENTE = listOf(
        es("Yusef es el único que sabe dónde está {nombre}. Baja la cesta por el pozo viejo dos noches por semana, con comida y un papel, y no se asoma nunca, para que nadie le vea la cara si lo siguen. En el papel no pone nombres. Solo pone si Sela está bien. Hasta ahora, sí."),
        es("En las galerías {nombre} no ve a nadie, y habla solo, en voz muy baja, para no olvidarse de cómo suena su voz. Habla con Sela, con Odila, con su padre. Les cuenta lo que hace. Ninguno le contesta. A veces, en el silencio, le parece que el gato del taller está allí. Nunca está."),
    )

    private val OBJETO = listOf(
        v("En el puerto de Halde las cosas pasan por muchas manos antes de llegar a las suyas. {dato_objeto}"),
        v("{dato_objeto} Eso lo sabe todo el que trabaja en el desguace o en el mercado."),
        v("Hoy {nombre} se fija en una cosa de todos los días. {dato_objeto}"),
        v("{dato_objeto} En el Pozo se dice que todo en Halde tiene dos usos: el que dice la Prefectura y el otro."),
        v("{dato_objeto} Nadie lo dice en voz alta, pero en el puerto todos lo saben."),
        v("Las cosas del puerto son viejas, pesadas y feas, y casi todas sirven para algo que no es lo suyo. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("cartucho", "el cartucho de datos", "un cartucho de datos", "El cartucho de datos",
            "Los cartuchos de datos son bloques de plástico color hueso del tamaño de una mano, con una etiqueta y una muesca en una esquina para que no entren al revés; en cada uno cabe un par de horas de cinta; quien no quiere que una cinta se lea la borra con un imán y la tira al mar, lejos."),
        ObjetoCuento("radio", "la radio de maleta", "una radio de maleta", "La radio de maleta",
            "La radio de maleta es una caja de baquelita negra con asa, que abierta tiene su pantalla redonda, su perilla de sintonía y una antena que se despliega en tres tramos; pesa como un niño de dos años; quien la lleva por la calle la lleva como si fuera ropa sucia."),
        ObjetoCuento("llave_cifrado", "la llave de cifrado", "una llave de cifrado", "La llave de cifrado",
            "La llave de cifrado es una tira de papel fino con columnas de letras, que se enrolla en un lápiz y se cambia cada semana; con ella un mensaje se vuelve números y los números, otra vez un mensaje; si hay que quemarla, arde en dos segundos y no deja ceniza."),
        ObjetoCuento("credencial", "la credencial", "una credencial", "La credencial",
            "La credencial del Directorio es una tarjeta de cartón encerado con la foto, el número de trabajador y el sello de bronce de la Prefectura; se enseña en cada control, en cada puerta del muelle, en cada tranvía; una falsa cuesta tres meses de desguace, y una buena, más."),
        ObjetoCuento("auricular", "el auricular", "un auricular", "El auricular",
            "El auricular de la radio es una concha de baquelita con un cable trenzado, que aprieta la oreja y deja en ella un círculo rojo; con él puesto el mundo se queda fuera, y eso es lo peligroso: quien escucha una ventana no oye los pasos en la escalera."),
        ObjetoCuento("microfilm", "el microfilm", "un microfilm", "El microfilm",
            "Un microfilm es un trocito de película del tamaño de una uña donde caben diez páginas; se lee con una lupa contra una bombilla; en el puerto se esconden en los tacones, en los botones, en el mango de un destornillador y, a veces, en el collar del gato del taller."),
        ObjetoCuento("carta_estelar", "la carta estelar", "una carta estelar", "La carta estelar",
            "Las cartas estelares van impresas en papel grueso, con los mundos como puntos y las rutas de salto como líneas de rayas, y se doblan en ocho; las del Directorio llevan un sello de bronce; las que se venden por debajo en el mercado no llevan ninguno, y sí alguna ruta que el Directorio no conoce."),
        ObjetoCuento("soplete", "el soplete", "un soplete", "El soplete",
            "El soplete del desguace es una lanza de cobre con dos mangueras, una de gas y otra de oxígeno, que corta el casco de una nave como un cuchillo caliente corta la grasa; se usa con gafas de cristal verde; deja en los brazos unas quemaduras pequeñas y redondas que tienen todos los chatarreros."),
        ObjetoCuento("linterna", "la linterna", "una linterna", "La linterna",
            "Las linternas del puerto son de minero: de latón, con una lente gruesa y una batería que se carga en la Compañía a cambio de un vale; dan una luz amarilla que no llega lejos; los que andan de noche les ponen un trapo rojo delante, para que no se vean desde arriba."),
        ObjetoCuento("chaqueta", "la chaqueta de vuelo", "una chaqueta de vuelo", "La chaqueta de vuelo",
            "La chaqueta de vuelo de Tadeo salió de la cabina de una nave del Directorio que llegó entera al desguace; es de cuero gastado, con el cuello de borrego y un bolsillo por dentro, hondo, que no sale en ningún dibujo de chaquetas; en el puerto la reconoce todo el mundo."),
        ObjetoCuento("valvula", "la válvula", "una válvula", "La válvula",
            "Las válvulas de las naves viejas son de cobre y cristal, del tamaño de un puño, y por dentro tienen un hilo que brilla naranja cuando funcionan; en el mercado una buena vale lo que una semana de desguace; muchas radios del puerto llevan dentro una que salió de un casco del Directorio."),
        ObjetoCuento("ficha", "la ficha del Pozo", "una ficha del Pozo", "La ficha del Pozo",
            "Las fichas del Pozo son discos de latón con un agujero en el medio, que Yusef da con cada vaso; la mayoría son solo fichas; las que tienen una raya en el borde quieren decir que hay algo debajo del vaso, y las que tienen dos, que esa noche no hay que volver."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} oye eso y piensa en los mineros, que bajan más horas por la misma paga, y en el desguace, que tendrá más prisa. Piensa que en Halde todo se mide por toneladas: el mineral, la chatarra, la gente. Piensa que él pesa poco. Que eso, a veces, sirve."),
        v("{nombre} piensa en su padre, que bajó a la mina cuando él tenía doce años y no subió. De su padre le queda una lámpara de latón y una manera de toser. A sus {edad} años es mayor de lo que llegó a ser su padre. Piensa que eso no lo tenía pensado. Que nadie en el barrio lo tiene."),
        v("{mundo} {nombre} piensa que robarle al Directorio no es robar: es recoger lo que se dejaron, lo que trajeron de fuera y se llevan de aquí. Piensa que la Prefectura no pensaría lo mismo. Piensa que la Prefectura no piensa. Apunta. Que no es lo mismo."),
        v("{nombre} piensa en la Capital, que no ha visto nunca: dicen que es toda de mármol blanco, con cúpulas de bronce y calles que se barren solas. Piensa que el mármol de la Prefectura vino de allí en un carguero, y que el carguero volvió lleno de mineral de Halde. Piensa que la Capital está hecha de Halde."),
        v("{mundo} {nombre} piensa en los cargueros que salen del muelle hacia otros mundos, y en las rutas de salto, que no ha visto más que pintadas en papel. A sus {edad} años no ha salido nunca del planeta. Piensa que no le hace falta. Piensa que eso es lo que dicen todos los que nunca van a salir."),
        v("{nombre} piensa en Sela, que se levanta antes que él y vuelve después, con las manos rojas del agua del lavadero. Piensa que su hermana no ha robado nunca nada, ni a la Compañía ni a nadie, y que le va igual de mal que a él. Piensa que entonces no es cosa de robar o no robar. Es otra cosa."),
        v("{mundo} {nombre} piensa en la red, que no sabe lo grande que es. Sabe que hay otras células, en otros barrios y en otros mundos, que no se conocen entre sí. Piensa que en este momento alguien como él, lejos, piensa lo mismo. Piensa que eso tendría que darle miedo. Le da compañía.", desde = "recluta"),
    )

    private val MUNDOS = mapOf(
        "cuota" to listOf(
            "La Prefectura ha vuelto a subir la cuota de mineral, y en la mina ya se habla de turnos dobles.",
            "Los altavoces del muelle repiten la cifra de la cuota cada mañana, como si la gente pudiera olvidarla.",
            "Dicen en el Pozo que la cuota de este año no la saca nadie, ni con turnos dobles.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde las grúas del muelle encienden sus luces rojas, una en cada punta, y el puerto parece un bosque de árboles con un ojo. Por el muelle vuelven los estibadores, sucios hasta las cejas. {nombre} se mezcla con ellos camino del barrio. Con el polvo encima, se parece a todos."),
        v("Anochece, y en la montaña se enciende la fila de luces del camino de la mina, por donde baja el turno de la tarde y sube el de la noche. Los dos se cruzan a medio camino sin hablar. Desde el puerto parece una sola línea de luz que no se mueve. {nombre} la mira desde la puerta del taller."),
        v("Al caer la tarde pasa una patrullera sobre el barrio, baja, con el anillo encendido, y la luz barre los tejados y los patios. Los perros ladran. La gente se mete en los portales sin que nadie se lo diga. Cuando se va, salen otra vez. {nombre} no se ha movido. Sabe que desde arriba no se ve nada."),
        v("Anochece en el puerto con el ruido de los cierres: las persianas del mercado, las puertas del desguace, la reja de la Prefectura, que cierra con una cadena. Las calles se quedan para los que tienen algo que hacer de noche. {nombre} es uno de ellos. Baja la escalera del Pozo."),
        v("De noche el humo de la mina se pone naranja por abajo, con las luces de los hornos, y el cielo de Halde no se pone nunca negro del todo. Desde la ventana de su cuarto {nombre} ve el muelle, las grúas y el mar oscuro. Las patrulleras, con sus anillos, cruzan como lunas pequeñas."),
        v("Al caer la tarde suena en la Prefectura la campana de la tarde, que no significa nada pero que todo el mundo obedece: los tenderos recogen, las madres llaman a los niños, la Guardia cambia la guardia. {nombre} vuelve del desguace por la calle del lavadero, con las manos en los bolsillos."),

        v("De noche, cuando el desguace se queda a oscuras, {nombre} vuelve a saltar la valla por el lado del mar, donde el sargento no mira, y trabaja a ciegas en una cabina con una linterna de minero. Las mejores piezas salen de noche. Las de día son de Brun. Las de noche son suyas.", hasta = "recluta"),
        v("A la hora de la ventana, {nombre} está donde esté la radio esa semana: un cuarto de atrás, un sótano, la cabina de un casco del desguace. Se pone el auricular. La pantalla redonda se enciende verde. Primero, ruido. Luego, la voz que lee números, de cinco en cinco. Luego, ruido otra vez. Apunta.", desde = "primera_ventana"),
        v("De noche, con la ventana de transmisión ya cerrada, {nombre} vuelve a su cuarto por los tejados del barrio, que es más largo pero más seguro, con el papel cifrado en la boca, por si tiene que tragárselo. Entra por la ventana. El gato del taller lo espera dentro, como si supiera.", desde = "primera_ventana"),
    )

    private val ESCONDIDO_ANOCHECER = listOf(
        es("Abajo no anochece, pero {nombre} sabe la hora por la sirena del último turno, que llega por la roca como un zumbido. A esta hora, arriba, la célula abriría la ventana. Ya no hay célula, o no sabe si la hay. Se pone la mano en la oreja, como si llevara el auricular. Solo oye el agua."),
        es("Cuando calcula que arriba es de noche, {nombre} sube por el pozo viejo hasta donde se ve el trozo de cielo, y se queda allí un rato, colgado de los hierros, respirando aire de fuera. Se ve pasar la luz de una patrullera. Luego nada. Baja otra vez a la galería, a oscuras."),
    )

    private val CRESTA_ANOCHECER = listOf(
        cr("Anochece en la Cresta, y la estación de radar enciende sus focos, que dejan las rocas de alrededor blancas como hueso. La luz ámbar de la antena barre la sierra cada minuto. Cuando pasa por encima de ellos, se aplastan contra el suelo. Abajo, el puerto enciende sus luces, pequeñas."),
        cr("De noche, en la Cresta, el frío baja de golpe y las estrellas se ven como no se ven nunca desde el puerto, sin humo delante. {nombre} las mira tumbado, entre guardia y guardia. Busca alguna ruta de salto de las de las cartas. No encuentra ninguna. Las rutas no se ven. Solo se saben."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))
    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "cambio_anio" to unDia(
            Parrafo.MANANA to "Hoy cambia el año del Directorio. Anoche, a las doce, sonaron a la vez todas las sirenas del puerto, las de la mina, las de los cargueros y las de la Prefectura, y {nombre} las oyó desde la cama, como cada año, contando hasta que se callaron. En la calle todavía huele a pólvora barata.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: el desguace está cerrado y la báscula, tapada con una lona. Duerme hasta tarde, se afeita despacio y baja al puesto de la esquina, donde dan el caldo del año nuevo, con un huevo dentro. Se lo come mirando la calle vacía.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el Directorio cuenta los años desde que llegó, y que antes Halde también tenía años, con otro nombre, que ya nadie usa. Su abuela sabía contarlos. Él no. Piensa que eso también se lo quitaron, y que nadie lo apuntó en ninguna lista.",
        ),
        "cuota" to unDia(
            Parrafo.MANANA to "Hoy la Prefectura anuncia la cuota de mineral del año. Los altavoces del puerto la dicen tres veces, con una música de metales, y el número es más alto que el del año pasado, como todos los años. En la cola del puesto de la esquina nadie dice nada. Todos han hecho la cuenta.",
            Parrafo.PUEBLO to "Por la tarde la Compañía pega la cifra en la puerta de la mina y en el muelle, en carteles con el sello de bronce: tantas toneladas, tantos turnos, tantos días sin fiesta. Debajo, en letra pequeña, lo que pasa si no se llega. Los mineros lo leen de pie, con la lámpara todavía en el casco.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la cuota no es un número sino una cuerda, y que cada año la aprietan un poco más. Piensa en los mineros, que bajarán más horas, y en el desguace, que tendrá más prisa. Piensa que alguien, en la Capital, ha escrito ese número sin haber visto nunca una mina.",
        ),
        "casco_nuevo" to unDia(
            Parrafo.MANANA to "Hoy llega el casco del año. Lo trae al amanecer un remolcador de la Compañía, despacio, colgado de tres cables, y lo deja caer en la grada del desguace con un golpe que hace temblar los cristales de medio barrio. {nombre} lo ve desde la ventana: una patrullera jubilada, larga, con el anillo partido.",
            Parrafo.TRABAJO to "Hoy {nombre} recorre el casco nuevo con Brun y una linterna, de proa a popa, apuntando con tiza lo que se puede vender y lo que va a la báscula. Huele a aceite viejo y a encierro. En una cabina encuentra una taza todavía en su soporte. Brun le dice que la tire. Él se la guarda.",
            Parrafo.PUEBLO to "Con el casco nuevo el desguace se llena de gente: chatarreros de otros patios, mirones, chicos que quieren tocar una nave del Directorio, aunque esté muerta. La Guardia pone a dos hombres en la puerta. El casco va a tardar el año entero en desaparecer, pieza por pieza, hasta que llegue el siguiente.",
        ),
        "lamparas" to unDia(
            Parrafo.MANANA to "Hoy es la Noche de las Lámparas, la única fiesta de Halde que no es del Directorio. Es tan vieja que la Prefectura no ha sabido prohibirla, así que la tolera. Desde por la mañana, en las ventanas del barrio de ladrillo, la gente cuelga lámparas de minero apagadas, esperando la noche.",
            Parrafo.PUEBLO to "Al caer la tarde bajan de la mina los mineros de todos los turnos, en fila, con la lámpara del casco encendida, por el camino de la montaña hasta el puerto. Desde abajo parece un río de luz que baja despacio. En el muelle los esperan las familias, con pan y aguardiente de raíz.",
            Parrafo.ANOCHECER to "Esta noche {nombre} está en el muelle, entre la gente, mirando llegar la fila de lámparas. Cuando llega el último minero, todas se apagan a la vez, y durante un minuto el puerto entero se queda a oscuras, en silencio, por los que no volvieron de la mina. Luego se encienden otra vez, y empieza el baile.",
        ),
        "concordia" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Concordia, el aniversario de cuando el Directorio llegó a Halde, aunque los carteles no dicen que llegó: dicen que trajo la paz. Desde temprano la Guardia forma en el muelle, en filas grises, con las viseras de los cascos brillando, delante de la tribuna de mármol de la Prefectura.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: la Concordia es fiesta obligatoria, y hay que ir al muelle a ver el desfile. Va. Se pone detrás, entre los estibadores, donde la Guardia no mira. Aplaude cuando aplauden. Cuenta los fusiles, las patrulleras que pasan bajas sobre el desfile, los hombres de la tribuna.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando las filas, que la Guardia es mucha y que nunca será bastante, porque por cada hombre de gris hay cien en el puerto que lo miran. Piensa que el desfile no es para que la gente vea a la Guardia: es para que nadie se cuente a sí mismo. Él se cuenta. Siempre.",
        ),
        "convoy" to unDia(
            Parrafo.MANANA to "Hoy sale el convoy de mineral hacia la Capital. Desde antes del amanecer los cargueros encienden los motores en el muelle, uno detrás de otro, y el ruido entra por la ventana del cuarto de {nombre} como un temblor que no se acaba. A las nueve despegan, en fila, con las patrulleras alrededor.",
            Parrafo.PUEBLO to "Cuando se va el convoy, el muelle se queda vacío y enorme, con las grúas quietas y los charcos de aceite donde estaban los cargueros. Los estibadores tienen dos días sin trabajo, y sin paga. En el mercado se vende menos. Todo el mineral del año va camino de un mundo que ninguno de ellos verá.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el mineral que se va, en las toneladas que sacaron los mineros a mano, y en lo que vuelve a cambio: vales, cartillas, patrulleras. Piensa que Halde es una mina con un puerto pegado, y que el Directorio no necesita de ella nada más. Lo piensa mirando el muelle vacío.",
        ),
        "feria" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy empieza la feria de la chatarra, tres días en que el desguace abre la puerta a todo el que quiera comprar. Desde temprano llegan carros, chicos, mineros de permiso, mecánicos de otros patios. Brun cobra una ficha por entrar y otra por salir con algo.",
                Parrafo.TRABAJO to "Hoy {nombre} no trabaja en el casco: vende en un puesto de tablas junto a la grada, con una lona encima, lo que dejó el año: tornillos, faros, asientos de piloto, relojes de cabina. Regatea por gusto. Lo mejor, lo que vale de verdad, no está en el puesto. Está debajo.",
                Parrafo.PUEBLO to "En la feria el desguace parece otro: hay puestos entre los cascos, una gramola en la cabina de una nave, gente subida a las alas para mirar el puerto desde arriba, niños que juegan a pilotos en los asientos arrancados. La Guardia también viene, a comprar barato y a mirar quién compra qué.",
            ),
            dia(
                Parrafo.GENTE to "Hoy, en la feria, se le acerca un minero viejo que busca una pieza para la radio de su casa, una que ya no se fabrica. {nombre} la tiene. Se la vende a mitad de precio y le pregunta para qué la quiere. El viejo le dice que para oír música. {nombre} no le cree, y no le dice nada.",
                Parrafo.TRABAJO to "Hoy {nombre} vende en el puesto de la feria un asiento de piloto entero a un tendero del mercado, y un faro de patrullera a un chico que no tiene con qué pagarlo y le paga con una pulsera de alambre. Brun dice que es mal negocio. {nombre} le dice que es la feria.",
            ),
            dia(
                Parrafo.PUEBLO to "Último día de la feria. Por la tarde los puestos se desmontan, y lo que no se vendió vuelve a la báscula, por peso. El suelo del desguace queda lleno de papeles, botellas y tornillos que nadie quiso. Mañana el casco del año volverá a ser solo un casco, y el soplete volverá a sonar.",
                Parrafo.ANOCHECER to "Al cerrar la feria los chatarreros se quedan en el desguace con una botella, sentados en el ala de una nave, viendo cómo se encienden las luces del puerto. Brun reparte lo que sobró de la caja. A {nombre} le toca poco. Se ríe igual. Es la única noche del año en que el desguace es de ellos.",
            ),
        )),
        "caidos" to unDia(
            Parrafo.MANANA to "Hoy es el día de los caídos de la mina. A las diez suena la sirena larga, la que solo suena este día, y todo el puerto se para un minuto: las grúas, los tranvías, el desguace, los puestos del mercado. Hasta la Guardia se queda quieta. Luego la sirena se calla y todo vuelve a moverse.",
            Parrafo.TRABAJO to "Hoy en el desguace los chatarreros trabajan callados, sin la radio de la caseta. El Pardo perdió a un hermano en la mina. Los Iriarte, a su padre. Todos han perdido a alguien. Brun deja que se vayan una hora antes. Nadie se va. Se quedan cortando hasta que oscurece.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su padre, en la mina, y en todos los que la sirena nombra sin decir sus nombres. Piensa que la Compañía tiene una lista con todos, con la fecha y la galería, y que nadie la ha visto nunca. Piensa que la sirena dura un minuto, y que debería durar un día entero.",
        ),
        "censo" to unDia(
            Parrafo.MANANA to "Hoy es el censo del Directorio, y todo Halde hace cola. {nombre} se pone en la fila de la Prefectura antes de que amanezca, con la credencial en la mano, entre mineros que bostezan y madres con niños dormidos. La fila da la vuelta a la plaza de mármol. Avanza un paso cada tanto.",
            Parrafo.GENTE to "Esta tarde, en la ventanilla, un empleado de la Prefectura le pregunta lo de cada año: nombre, número, trabajo, domicilio, parientes vivos. Apunta en un libro grande con tapas de bronce. {nombre} contesta lo mismo que el año pasado. El empleado no levanta la vista. Le sella la credencial.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el libro de tapas de bronce sabe dónde duerme cada persona del puerto, y que eso es lo que de verdad manda en Halde, más que la Guardia. Piensa que una mentira bien dicha en la ventanilla dura un año entero en el libro. Piensa que la suya va a durar.",
        ),
        "carbon" to unDia(
            Parrafo.MANANA to "Hoy la Prefectura reparte los vales de carbón del invierno. Hay que ir en persona, con la credencial, y la cola sale de la plaza y se mete por las calles del barrio. Hace frío. La gente golpea el suelo con los pies. Un vale por casa, para cuatro meses. Nadie dice que no alcanza. No hace falta.",
            Parrafo.GENTE to "Esta tarde {nombre} le lleva a Sela la mitad de sus vales de carbón, como cada invierno, porque el cuarto de su hermana tiene una ventana que no cierra y el suyo tiene debajo los motores de Abel, que calientan. Sela le dice que no hace falta. Él los deja en la mesa y se va antes de que se los devuelva.",
            Parrafo.ANOCHECER to "Al caer la tarde el barrio de ladrillo huele a carbón nuevo, el de los vales, que hace un humo amarillo y espeso. Salen columnas de todas las chimeneas a la vez, y el humo se queda bajo, entre los tejados. Las luces del puerto se ven borrosas. Ya es invierno, aunque el calendario no lo diga.",
        ),
        "noche_larga" to unDia(
            Parrafo.MANANA to "Hoy es la Noche Larga, la más larga del año. En los patios del barrio de ladrillo los vecinos juntan desde temprano lo que van a quemar: tablas, cajas, muebles rotos, y del desguace se llevan maderas viejas que Brun regala. Esta noche cada patio tendrá su hoguera.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja por la tarde: Brun cierra el desguace antes, y los chatarreros cargan un carro de maderas para las hogueras del barrio. {nombre} empuja el carro por las calles, repartiendo tablas en cada patio. Los niños corren detrás. Una vieja le da a cambio un trozo de pan dulce.",
            Parrafo.ANOCHECER to "Esta noche hay hogueras en todos los patios del barrio, y la gente se queda alrededor hasta tarde, pasándose una botella y cantando canciones de antes del Directorio, que nadie ha prohibido porque nadie en la Prefectura las conoce. {nombre} canta bajito. Se sabe la letra entera. No sabe de quién la aprendió.",
        ),

        // ---------- Los de la temporada 1 ----------
        "la_caja" to unDia(
            Parrafo.MANANA to "Hoy, cortando la cabina de una patrullera, {nombre} ha encontrado algo que no estaba en ninguna lista: una caja de navegación entera, sellada con el bronce del Directorio, detrás de un panel que nadie abrió. Pesa poco. Vale mucho. No sabe cuánto. Nadie en el desguace lo ha visto.",
            Parrafo.TRABAJO to "Hoy, a la hora de la báscula, {nombre} pasa por delante de Brun con la caja de navegación metida por dentro de la chaqueta, pegada a las costillas, y le enseña un saco de cables. Brun pesa los cables. Le paga los cables. No le mira la chaqueta. O se la mira y no dice nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, de noche, con la caja encima de la mesa, que una cosa así no la compra cualquiera en el mercado. Piensa en la relojera del puesto del fondo, la que mira las piezas con la lupa y nunca pregunta. Piensa en esperar unos días, a que nadie en el desguace se acuerde de la cabina.",
        ),
        "relojera" to unDia(
            Parrafo.MANANA to "Hoy {nombre} ha llevado la caja de navegación al puesto de Odila, la relojera, al fondo del mercado, envuelta en un trapo. Ella la ha mirado con la lupa en el ojo sin decir nada, la ha pesado en la mano y le ha pagado el doble de lo que él iba a pedir. Luego ha cerrado la cortina del puesto.",
            Parrafo.GENTE to "Esta tarde, con la cortina cerrada, Odila no le pregunta de dónde sacó la caja. Le pregunta otra cosa: si sabe leer los números de los paneles, si sabe moverse por el muelle de noche, si tiene a alguien que dependa de él. Él contesta que sí, que sí y que una hermana. Ella lo apunta en la cabeza.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, con los vales en el bolsillo, que nadie le había pagado nunca de más por nada, y que eso no es generosidad: es una pregunta. Piensa en los ojos de Odila detrás de la lupa, que miraban la caja y lo miraban a él. Piensa que la próxima vez sabrá qué le están preguntando.",
        ),
        "recluta" to unDia(
            Parrafo.MANANA to "Hoy Odila lo ha hecho pasar al cuarto de atrás de la relojería, entre relojes parados y radios a medio arreglar, y le ha dicho, sin bajar la voz, para quién compra lo que compra: para una red que trabaja contra el Directorio en muchos mundos. No ha dicho el nombre. No ha hecho falta.",
            Parrafo.TRABAJO to "Hoy {nombre} corta lo que queda del casco viejo con el soplete, como cualquier día, pero las manos no le obedecen del todo. Brun le grita dos veces que está cortando torcido. Tiene razón. Corta torcido toda la tarde, pensando en el cuarto de atrás de la relojería y en todo lo que no le han dicho todavía.",
            Parrafo.GENTE to "Esta tarde Odila le explica cómo es la red: células pequeñas, que no se conocen entre sí; cada uno sabe solo lo que tiene que saber; si cae uno, no cae nadie más. Le dice que puede decir que no, y que entonces ella se olvidará de que se lo ha contado. {nombre} no dice que no.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hasta hoy robaba para comer, y que desde hoy va a robar para otra cosa, y que no sabe bien para qué. Piensa en su padre, en la mina. Piensa que a lo mejor es eso. Piensa que tiene miedo, y que el miedo no es tan distinto del que tenía antes. Solo es más grande.",
        ),
        "primer_recado" to unDia(
            Parrafo.MANANA to "Hoy Odila le ha dado su primer recado para la red: una caja de válvulas, de las que él mismo saca de los cascos, para llevar a un taller del muelle. La caja pesa más de lo que pesan unas válvulas. Ella le ha dicho que no la abra, que no corra y que no mire atrás más de lo normal.",
            Parrafo.TRABAJO to "Hoy {nombre} cruza el puerto con la caja de válvulas al hombro, por el camino de siempre, saludando a quien saluda siempre y parándose donde se para siempre. En el control del muelle un guardia le pregunta qué lleva. Le dice que válvulas. El guardia levanta la tapa, ve válvulas y lo deja pasar.",
            Parrafo.GENTE to "En el taller del muelle lo recibe un hombre viejo y ancho, con las manos negras de mina, que se llama Gaspar o dice que se llama así. Abre la caja delante de él, saca las válvulas y debajo hay otra cosa, envuelta en hule. Gaspar le da una palmada en la espalda y le dice que ha tardado lo justo.",
        ),
        "patrullera_nueva" to unDia(
            Parrafo.MANANA to "Hoy ha llegado al puerto una patrullera nueva, más larga que las otras, con el anillo encendido de un blanco frío que no se había visto nunca en Halde. Ha bajado despacio sobre el muelle, a la hora del primer turno, y todo el mundo se ha parado a mirarla. Hace un ruido grave que se mete en los huesos.",
            Parrafo.PUEBLO to "La patrullera nueva se queda posada en el muelle grande, donde antes atracaban dos cargueros, con guardias alrededor y una lona en la escotilla. Los chicos del barrio se suben a los tejados para verla. Los estibadores hacen como que no la ven. Por la noche su anillo se queda encendido.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando la patrullera nueva, que el Directorio no manda una nave así a un puerto tranquilo. Piensa que alguien en la Capital está preocupado por Halde. Piensa que es la primera vez en su vida que eso le da gusto. Lo guarda en la cabeza para contárselo a Odila.",
        ),
        "primera_ventana" to unDia(
            Parrafo.TRABAJO to "Hoy, después del desguace, Odila lo ha llevado al cuarto de atrás de la relojería y le ha enseñado la radio de maleta, abierta encima de la mesa, con su pantalla redonda apagada. Le ha enseñado a encenderla, a buscar la frecuencia con la perilla y a no tocar nada más. Esta noche, le ha dicho, se escucha.",
            Parrafo.ANOCHECER to "Esta noche, a la hora de la ventana, {nombre} se pone el auricular en el cuarto de atrás de la relojería, con Odila al lado. La pantalla redonda se enciende verde. Al principio solo hay ruido. Luego, una voz que lee números, despacio, de cinco en cinco, durante cuatro minutos. Luego, ruido otra vez.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esa voz que leía números está en algún sitio, en otro mundo o en este, y que la oían a la vez otros como él, en otros cuartos de atrás, sin conocerse. Piensa que es la primera vez en su vida que es parte de algo más grande que el puerto. No sabe qué decían los números.",
        ),
        "seguido" to unDia(
            Parrafo.TRABAJO to "Hoy, cruzando el mercado con un recado, {nombre} ha visto tres veces el mismo abrigo azul oscuro: en el puesto de las sopas, en la escalera de las radios y en el reflejo de un escaparate. Ha cambiado de pasillo dos veces. El abrigo, también. Al final lo ha perdido entre los puestos de pescado.",
            Parrafo.PUEBLO to "El mercado de Halde es el mejor sitio del puerto para perder a alguien: tres naves de hierro y cristal con cien pasillos, puestos que se cruzan, lonas, humo de freír, gente con prisa y pasadizos entre los puestos que solo conocen los del mercado. {nombre} los conoce todos. Los aprendió robando.",
            Parrafo.ANOCHECER to "Esta noche, en el Pozo, Yusef le sirve el aguardiente con una ficha de dos rayas en el borde: esta noche no hay ventana para él. {nombre} se bebe el vaso sin prisa, juega una mano de cartas y se va a su cuarto por el camino más largo. Desde la ventana, a oscuras, mira la esquina un buen rato.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el hombre del abrigo azul no sabía quién era él, o no lo habría perdido. Piensa que lo seguía por seguir a alguien: por el paquete, por la cara, por la chaqueta. Piensa que no lo sabe. Y que no saberlo es peor. Esa noche duerme vestido.",
        ),
        "explosion" to unDia(
            Parrafo.MANANA to "Hoy, a las seis, ha sonado la sirena de los accidentes de la mina, que no suena casi nunca, y {nombre} se ha despertado de golpe sabiendo lo que era. Ha habido una explosión en la galería nueva, la que abrieron este año para llegar a la cuota. Desde la ventana se ve humo en la montaña.",
            Parrafo.PUEBLO to "Todo el puerto ha subido al camino de la mina a esperar, detrás de una fila de guardias que no dejan pasar: las mujeres del lavadero, los estibadores, los chatarreros, los niños. Por la tarde empiezan a bajar las camillas, tapadas. Cuentan doce. La Compañía dice que fue un descuido de los mineros.",
            Parrafo.GENTE to "Esta tarde Sela vuelve tarde del lavadero: ha estado en el camino de la mina con las otras mujeres, esperando las camillas. Uno de los doce era el novio de una compañera suya. Llega con los ojos secos y la cara gris de polvo. {nombre} le hace un caldo. Ninguno de los dos lo toca.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la galería nueva, abierta deprisa para llegar a la cuota, sin los puntales que pedían los mineros. Piensa que eso no fue un descuido. Piensa que en la Capital nadie va a saber los doce nombres. Esta noche, en la ventana, por primera vez, quiere que alguien los diga.",
        ),
        "sela_espera" to unDia(
            Parrafo.GENTE to "Hoy Sela lo ha esperado a la salida del desguace, cosa que no hace nunca, y le ha dicho, caminando, sin mirarlo, que espera un hijo para el invierno. El padre es un estibador que ya no está en Halde. Ella no quiere hablar de él. {nombre} le pregunta si necesita algo. Ella dice que no. Necesita todo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el niño que va a nacer en el barrio de ladrillo, con la cuota más alta que nunca y una patrullera nueva en el muelle. Piensa que tendría que dejar la red, por Sela. Piensa que justo por eso no la va a dejar. Piensa que ese niño no tiene por qué crecer en este Halde.",
            Parrafo.ANOCHECER to "Al caer la tarde acompaña a Sela hasta su cuarto, encima del lavadero de mineral, y se queda un rato en la puerta mientras ella calienta la sopa. El agua del lavadero corre roja por la calle, como siempre. Ella le pide que no haga tonterías. Él le dice que nunca. Los dos saben que miente.",
        ),
        "chispa" to unDia(
            Parrafo.MANANA to "Hoy ha amanecido pintada en el muro grande del muelle, de lado a lado, una chispa de tres trazos dentro de un círculo roto, en pintura roja, más alta que un hombre. Nadie sabe quién la pintó. A las siete ya había guardias delante, y a las ocho, dos presos con cubos y cepillos.",
            Parrafo.PUEBLO to "Por mucho que frotan, la chispa del muro no se va del todo: queda una sombra rosa en el ladrillo, y la gente pasa más despacio por delante para verla. Por la tarde aparece otra, pequeña, en la puerta del lavadero. Luego, otra en una grúa. La Guardia no da abasto con los cubos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la chispa del muro es la misma que Odila tiene grabada por dentro de la tapa de la radio, y que hasta hoy creía que era un secreto de cuatro personas. Piensa que a lo mejor la red es más grande de lo que creía. O que a lo mejor la chispa ya no es de la red. Es del puerto.",
        ),
        "juramento" to unDia(
            Parrafo.MANANA to "Hoy Odila le ha dicho que esta noche, en la ventana, la célula le va a dar su nombre. No el suyo: el de la red, el que usará en los mensajes y que solo sabrán cuatro personas en todo Halde. Le ha dicho que no lo elige él. Que nadie elige el suyo.",
            Parrafo.GENTE to "Esta tarde, en el sótano del Pozo, con Yusef vigilando la escalera, están juntos por primera vez los cuatro de la célula: Odila, Gaspar, una mujer del muelle que no dice su nombre y él. Gaspar sirve aguardiente en cuatro vasos. Brindan sin decir por qué. Ya lo saben.",
            Parrafo.ANOCHECER to "Esta noche, a la hora de la ventana, la voz de los números lee un mensaje que Odila descifra delante de él, letra por letra, con la llave de columnas: la célula K-7 tiene un agente nuevo, y su nombre es Cobre. {nombre} lo repite en voz baja. Cobre. Suena a mina. Suena a Halde.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que desde esta noche hay dos como él: el chatarrero del desguace, que tiene una hermana y un cuarto encima de un taller, y Cobre, que no tiene nada que perder porque no existe. Piensa que tendrá que cuidar mucho de que no se encuentren. Piensa que Cobre le gusta.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "agente_red", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + ESCONDIDO_MANANA + CRESTA_MANANA,
            Parrafo.TIEMPO to TIEMPO + ESCONDIDO_TIEMPO + CRESTA_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + ESCONDIDO_TRABAJO + CRESTA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + ESCONDIDO_PUEBLO + CRESTA_PUEBLO,
            Parrafo.GENTE to GENTE + ESCONDIDO_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + ESCONDIDO_ANOCHECER + CRESTA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
