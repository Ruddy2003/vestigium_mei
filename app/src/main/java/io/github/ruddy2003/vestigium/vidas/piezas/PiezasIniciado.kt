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
 * El cuento de Eleazar, salador de pescado en Magdala e iniciado de un círculo escondido, del 28 al 32 d. C. (Arcontes).
 * Es una persona inventada en un mundo real. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el
 * 11 nov 2026, y la voz del tema (`docs/diseno/temas/arcontes.md`): solemne y en voz baja, sin «magia», sin «hechizo», sin
 * horror; lo de arriba se intuye y nunca se ve entero.
 *
 * Lo que este cuento cuida (PLAN_TEMAS, 10d, y `docs/diseno/temas/fuentes_arcontes.md`):
 * - **Jesús nunca se describe de frente.** Eleazar lo ve de lejos, de espaldas, entre la gente; nunca le ve la cara. Se le
 *   llama «el de Nazaret» o «el Nazareno».
 * - **Sus palabras van en estilo indirecto, con la cita al lado** («dice que el tiempo se ha cumplido», Marcos 1:14–15).
 *   Solo unas pocas frases cortas van tal cual, con el texto exacto de la Reina-Valera 1909 copiado de Wikisource, entre
 *   comillas españolas y seguidas de su cita: «Consumado es» (Juan 19:30). No más de 15 en todo el cuento; el texto de cada
 *   versículo está en la sección 14 de `docs/diseno/temas/fuentes_arcontes.md`, y la prueba (`CuentoG9Prueba`) comprueba
 *   que cada frase esté letra por letra en el versículo que cita.
 * - **La lectura gnóstica es la del personaje**, no la del cuento: lo cuenta como lo que él cree, lo que dice Ismael o lo que
 *   dice Judit. Eleazar mezcla lo que oye: que el Cristo bajó sobre un hombre de verdad en el bautismo (Cerinto, según
 *   Ireneo) y que su cuerpo solo parecía cuerpo (el docetismo de los Hechos de Juan); son dos escuelas, y él cree las dos
 *   según el día (corrección 7). Las citas de los textos gnósticos van entre paréntesis, como nota del que cuenta: en el
 *   año 28 esos libros no existían todavía (la decisión del autor está en la `inspiracion` de la vida).
 * - Ninguna de la gente real se dibuja; lo que hace con Eleazar es inventado.
 *
 * Lo que tiene que respetar de su calendario (`vidas/anios/AnioG9.kt`):
 * - El de Nazaret: desde `bautismo_28` (temporada 1) se le recuerda; anda por el lago desde `galilea_28`; muere en
 *   `cruz_30` (3), y desde `orilla_30` (3) Eleazar cree que lo vio en la orilla. Las barcas de la gente que lo sigue salen
 *   en el pueblo por el arco `barcas`.
 * - María, la vecina: oye voces hasta `maria_sigue_29` (2); se va con él hasta `sepulcro_30` (3); desde ahí, es la que vio
 *   el sepulcro vacío.
 * - Mara espera a Débora de `mara_espera_28` (1) a `nace_debora_29` (2). Shifra vive hasta `muere_shifra_31` (4). El
 *   círculo se reúne en casa de Ismael hasta `cueva_31` (4) y, desde ahí, en una cueva del Arbel. Simón de Samaria, desde
 *   `simon_32` (5).
 * - Dos estados: «jerusalen» (sus subidas a Jerusalén, con los días de camino) y «huida» (temporada 5, escondido en la
 *   ciudad de Samaria). Los días de camino son especiales largos (`sube_*` y `baja_*`) que cambian la mañana, el trabajo y
 *   el pueblo; por eso las variantes de «jerusalen» de la mañana, el trabajo y el pueblo son de la ciudad, y las del tiempo,
 *   la gente y el anochecer valen también para el camino. **Un día especial que caiga en un día de camino** (como el del
 *   vado del Jordán, o el de Jericó del 30) tiene que cambiar también la mañana, el trabajo y el pueblo.
 * - El objeto y lo que piensa no tienen variantes de estado: valen en Magdala, en Jerusalén y en Samaria.
 * - No repite lo que ya dice «Su vida»: el reparto del día (abrir, salar, tender, guardar en ánforas), la Tariquea de los
 *   griegos, el pan de cebada con pescado y aceitunas, y lo que enseña el viejo de Alejandría dicho en una frase.
 *
 * **Las temporadas 2 a 5 van en archivos aparte** (`EspecialesIniciadoT2.kt` a `EspecialesIniciadoT5.kt`), uno por
 * temporada, para que se puedan escribir a la vez sin tocar este archivo: cada uno guarda un [EspecialesIniciado] con sus
 * textos y su marca `escrita`. Este objeto los junta en [PIEZAS]: sus textos van a `especiales` y, cuando una temporada se
 * marca como escrita, entra sola en `temporadasEscritas` (y entonces las pruebas exigen el texto de cada uno de sus días).
 * Como en el calendario cada día lleva su año en el id, los textos de una temporada no pueden pisar los de otra.
 */
internal object PiezasIniciado {
    private const val JER = "jerusalen"
    private const val HUIDA = "huida"

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    /** De vuelta a Jerusalén: en las fiestas de peregrinación, con los días de camino. */
    private fun je(texto: String, hacer: Hacer? = null) = v(texto, estado = JER, hacer = hacer)

    /** Escondido en la ciudad de Samaria (temporada 5). */
    private fun hu(texto: String, hacer: Hacer? = null, desde: String? = null, meses: Set<Int>? = null) = v(texto, estado = HUIDA, hacer = hacer, desde = desde, meses = meses)

    private val INVIERNO = setOf(11, 12, 1, 2, 3)
    private val VERANO = setOf(6, 7, 8)

    // =====================================================================
    //  La mañana
    // =====================================================================

    private val MANANA = listOf(
        v("{nombre} se despierta antes que el sol, cuando las barcas de la noche todavía están en el agua y solo se oye el golpe de los remos. Mara duerme. Él se ata el cinto a oscuras, descuelga el cuchillo del clavo de la puerta y sale al patio, donde huele a sal y a pescado de ayer."),
        v("Lo despierta el gallo de los vecinos, que canta siempre antes de tiempo. {nombre} se queda quieto en la estera, mirando la rendija de la puerta, por donde entra una luz gris. Cada mañana mira esa rendija. Ismael dice que por las rendijas se ve lo que hay detrás."),
        v("{nombre} sube a la azotea antes del alba. Desde allí se ve el lago entero, negro todavía, con las luces de las barcas que vuelven, y al fondo los montes de la otra orilla, que se encienden primero. Abajo, en el patio, alguien sopla el fuego para que prenda."),
        v("La primera luz entra por la ventana alta de la casa y cae en la pared de enfrente, siempre en el mismo sitio, como una mancha de aceite. {nombre} la mira mientras se lava la cara en la jofaina. Hoy la mancha es más blanca que ayer, o eso le parece."),
        v("{nombre} se despierta con el ruido de la piedra de moler: alguien de la casa ya está haciendo la harina de cebada para el pan. Se levanta, se lava las manos como manda la costumbre, se sacude la sal seca de la túnica y se pone las sandalias."),
        v("Amanece con una niebla baja sobre el lago, que se levanta despacio, como un velo. {nombre} la mira desde la puerta. Debajo de la niebla, dicen los pescadores, el agua está más quieta que nunca. Él piensa que el mundo entero está debajo de un velo parecido.", meses = INVIERNO),
        v("En verano {nombre} se levanta todavía más temprano, para trabajar antes del calor. El aire está quieto y caliente ya antes del alba, y el lago parece de plomo. Bebe agua del cántaro colgado en la sombra, que es lo único fresco de la casa.", meses = VERANO),
        v("{nombre} se despierta y lo primero que oye es a Débora, que llora en su estera porque tiene hambre, y a Mara, que le canta bajito para que no despierte a nadie. Ya ha despertado a todos. {nombre} se ríe por dentro y se levanta.", desde = "nace_debora_29"),
    )

    private val JER_MANANA = listOf(
        je("{nombre} se despierta en el suelo, envuelto en el manto, entre los peregrinos de Magdala, con la espalda contra un olivo. Huele a humo de mil fuegos y a ganado. Abajo, al otro lado del valle, el Templo ya brilla, aunque el sol no ha salido del todo."),
        je("Lo despierta el toque de las trompetas del Templo, que llega por encima de la ciudad antes del alba. {nombre} se sienta y se sacude el rocío del manto. A su alrededor cientos de peregrinos se levantan a la vez, como un campo de trigo que se mueve con el viento."),
        je("{nombre} amanece lejos de casa, con frío en los huesos y los pies todavía hinchados del camino. Se lava la cara con el agua del odre, que ya sabe a cuero, y busca con los ojos a los de Magdala. Están todos. Cada mañana los cuenta antes de nada."),
    )

    private val HUIDA_MANANA = listOf(
        hu("{nombre} se despierta en el cuarto prestado, en la ciudad de Samaria, y por un momento no sabe dónde está. Luego oye a los de la casa de al lado, que hablan con otro acento, y se acuerda. Magdala está a tres días de camino. Se levanta despacio."),
        hu("Lo despierta el frío de los montes, que en Samaria es distinto del de la orilla: más seco y más limpio. {nombre} se asoma a la puerta. No hay lago. Hay colinas con olivos y, al fondo, el monte donde los samaritanos dicen que hay que adorar."),
        hu("{nombre} duerme mal y se levanta antes del alba, con el oído atento a los pasos de la calle. Los que huyeron con él dicen que los hombres de Saulo no llegan hasta aquí. Él no está tan seguro. Se ata el cinto y sale a buscar un jornal."),
    )

    // =====================================================================
    //  El tiempo y la estación
    // =====================================================================

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} Los pescadores dicen que el tiempo se lee en el agua, y casi nunca se equivocan."),
        v("{nombre} mira el cielo con la mano en la frente, como lo miran los pescadores antes de salir. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En el fondo de este valle, entre los montes, el aire se queda quieto como en una olla."),
        v("{cielo} En {estacion} el aire huele distinto: a tierra, a la lluvia que viene o al polvo que trae el viento."),
        v("{de_estacion} {cielo} Ismael dice que el tiempo también lo mandan los de arriba, los que guardan las puertas, y que por eso cambia sin avisar."),
        v("Desde la orilla se ve el cielo entero, de un monte a otro, y {nombre} lo mira un momento cada mañana. {cielo} {de_estacion}"),
    )

    private val JER_TIEMPO = listOf(
        je("{cielo} Lejos del lago el aire huele distinto: a polvo, a piedra caliente, a humo de otros fuegos. {nombre} lo nota en cuanto deja atrás la orilla."),
        je("Lejos de casa, {nombre} mira el cielo como si fuera el de otro sitio. {cielo} {de_estacion}"),
    )

    private val HUIDA_TIEMPO = listOf(
        hu("{cielo} En los montes de Samaria el viento no huele a lago, y {nombre} lo echa de menos sin decirlo. {de_estacion}"),
        hu("{de_estacion} {cielo} Aquí el tiempo se mira en las eras, no en el agua."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y alto, de un azul que se vuelve blanco cerca del sol.",
            "No hay ni una nube, y la luz pega en las piedras hasta hacer daño en los ojos.",
            "Hace un día claro, y los montes de lejos se ven como si estuvieran cerca.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto de nubes bajas que no acaban de llover.",
            "Pasan nubes grises que vienen del mar Grande y se van hacia el desierto.",
            "Está nublado, con una luz pareja que no hace sombras.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fina que hace brillar las piedras de las calles.",
            "Cae una lluvia de invierno, y los caminos se vuelven barro.",
            "Llueve de golpe, para de golpe y vuelve a llover.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: el viento baja de los montes y el cielo se pone del color del plomo.",
            "Truena lejos, y luego cerca, y la lluvia cae de lado.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, una niebla blanca que se come las casas de enfrente.",
            "La niebla tapa los montes, y el mundo parece más pequeño.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento fuerte que levanta los mantos y hace golpear las puertas.",
            "Hace viento, y los olivos enseñan el lado blanco de las hojas.",
        ),
        Clima.POLVO to listOf(
            "Sopla el viento caliente del desierto, y el aire se llena de un polvo amarillo.",
            "El cielo está turbio de polvo, y el sol se ve como una moneda vieja.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y los campos de cebada se ponen amarillos antes que los de trigo.",
            "En primavera las colinas se llenan de anémonas rojas que duran muy poco.",
            "Es el tiempo de las últimas lluvias y de la siega de la cebada.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el calor no se va ni de noche.",
            "En verano los higos maduran, y las avispas llegan antes que la gente.",
            "Es el tiempo de la trilla y de las eras.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y todo el mundo mira el cielo esperando la primera lluvia.",
            "En otoño se cogen las uvas y las aceitunas, y se prensa el aceite.",
            "Es el tiempo de arar la tierra, en cuanto cae la primera lluvia.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y las noches son largas y frías.",
            "En invierno los torrentes que bajan de los montes se ponen pardos.",
            "Es el tiempo de las lluvias y de los bancos grandes de peces.",
        ),
    )

    // =====================================================================
    //  El trabajo del tramo (cada texto vale a cualquiera de sus horas)
    // =====================================================================

    private val TRABAJO = listOf(
        v("A esta hora abre pescado en el secadero: un tajo en el vientre, las tripas al cesto, un puñado de sal dentro y otro fuera. Lo hace sin mirar, mil veces al día. Tiene las manos cortadas y blancas de sal, y la sal le escuece en cada corte.", hacer = Hacer.TRABAJO),
        v("A esta hora tiende el pescado abierto en los cañizos, al sol, en filas, como quien siembra. Las gaviotas esperan en el tejado del cobertizo y él las espanta con un trapo. Lo de hoy estará seco en tres días, si no llueve. Ezequías dice que siempre llueve.", hacer = Hacer.TRABAJO),
        v("A esta hora carga ánforas desde el secadero hasta el camino, donde esperan los burros de la caravana. Cada ánfora va llena de pescado en salmuera y sellada con barro, y pesa como un niño grande. Las lleva al hombro, una detrás de otra, sin parar.", hacer = Hacer.TRABAJO),
        v("A esta hora echa sal en las tinas de salmuera, remueve con el palo largo y prueba el agua con la punta de la lengua, como le enseñó Ezequías. Si está floja, el pescado se pudre; si está fuerte, se quema. Esta está bien. Casi siempre está bien.", hacer = Hacer.TRABAJO),
        v("A esta hora le da la vuelta al pescado de anteayer, que ya está tieso y dorado, y lo prueba arrancando un trozo con los dientes. Luego lo mete en las ánforas por capas, pescado y sal, pescado y sal, apretando con el puño. Natán mira cómo lo hace.", hacer = Hacer.TRABAJO),

        v("A esta hora duerme en la estera, junto a Mara, con el cuchillo colgado de su clavo y las sandalias a mano. Por la ventana alta entra el ruido del lago, que nunca se calla del todo. Duerme hondo, como duermen los que cargan todo el día.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con escaleras que suben y no se acaban, con puertas que alguien guarda. En el sueño alguien le pregunta su nombre en cada puerta. Al despertar, a veces, no se acuerda de lo que contestó.", hacer = Hacer.DORMIR),
        v("A esta hora duerme con las manos oliendo todavía a sal, aunque se las ha lavado tres veces. Un perro ladra en la orilla; una barca golpea contra otra. Son ruidos de Magdala. Los conoce todos, y ninguno lo despierta.", hacer = Hacer.DORMIR),

        v("A esta hora reza en la azotea, mirando hacia Jerusalén, las palabras que se dicen al alba, las que empiezan pidiendo a Israel que escuche. Las dice como su padre se las enseñó. Después, en silencio, dice otras, las del círculo, que no son para nadie de aquí abajo.", hacer = Hacer.REZAR),
        v("A esta hora está de pie en la puerta del patio, con el manto sobre la cabeza, esperando la primera luz para rezar. Cuando el borde de los montes se pone blanco, empieza. Reza despacio. Ismael dice que la luz de verdad no es esa, pero que se le parece.", hacer = Hacer.REZAR),
        v("A esta hora reza con los otros saladores en la orilla, antes de empezar, de cara al agua todavía oscura. Cada uno dice lo suyo, en voz baja. {nombre} dice lo de todos y, al final, sin mover los labios, una palabra que solo conocen los del círculo.", hacer = Hacer.REZAR),

        v("A esta hora come en el patio, sentado en el suelo, pan de cebada mojado en aceite, unas aceitunas y un trozo de pescado salado de los que no se venden. Mara le pasa el cántaro. Comen sin hablar mucho; hay que volver al secadero.", hacer = Hacer.COMER),
        v("A esta hora come en el secadero, a la sombra de los cañizos, con los otros saladores: pan, cebolla, higos secos y el pescado pequeño asado en las brasas. Ezequías come de pie, mirando el cielo, por si llueve.", hacer = Hacer.COMER),
        v("A esta hora come lentejas con pan en casa, con todos alrededor de la misma olla. Antes de comer se bendice el pan, y él lo bendice en voz alta, porque es el hombre de la casa desde que murió su padre. Natán imita cómo lo dice.", hacer = Hacer.COMER),

        v("A esta hora baja a la orilla con Ismael, que camina despacio, apoyado en su hombro, y habla poco. Mira el agua y dice una frase que no se entiende hasta la noche. {nombre} la guarda. En el círculo le llaman maestro; en la orilla, para todos, es solo un viejo.", hacer = Hacer.CAMINO),
        v("A esta hora camina por la orilla, entre las barcas varadas y las redes tendidas, cuando los pescadores se preparan para salir de noche. Se para a oír lo que se habla: quién pescó, quién debe, qué se dice en Tiberíades. En la orilla se sabe todo.", hacer = Hacer.CAMINO),
        v("A esta hora va a la orilla a ver cómo se pone el sol detrás del Arbel, con los pies en el agua, que a esta hora está tibia. Piensa en lo que dirá Ismael esta noche. Ismael dice que el lago es un espejo, y que los espejos enseñan lo de abajo como si fuera lo de arriba.", hacer = Hacer.CAMINO),
        v("A esta hora va a la orilla porque dicen que el de Nazaret anda cerca, en una barca o en una casa de Cafarnaúm. A veces está, y {nombre} lo ve de lejos, de espaldas, entre la gente. A veces no está, y la gente se queda igual, esperando, mirando el agua.", hacer = Hacer.CAMINO, desde = "galilea_28", hasta = "cruz_30"),
        v("A esta hora va a la orilla donde hablaba el de Nazaret, junto a las piedras planas. Ya no va nadie a esperarlo. {nombre} se sienta un rato y mira el agua. Ismael dice que no hay que buscarlo en la orilla. Él lo busca igual.", hacer = Hacer.CAMINO, desde = "cruz_30"),

        v("A esta hora está en la reunión de los que saben: siete u ocho personas sentadas en el suelo, alrededor de una lámpara de barro. Nadie dice su nombre en voz alta. Se habla bajo. Cuando alguien llama a la puerta, la lámpara se tapa con un cuenco.", hacer = Hacer.GENTE),
        v("A esta hora escucha a Ismael, que habla de Sofía, la que quiso conocer sola y perdió la luz, y del hijo que tuvo, deforme, con cara de león y cuerpo de serpiente (Apócrifo de Juan). Ismael lo cuenta como quien cuenta lo que pasó ayer, y {nombre} lo cree así.", hacer = Hacer.GENTE),
        v("A esta hora oye a los del círculo repetir, en voz muy baja, los nombres de las siete esferas, de abajo arriba, y lo que el alma tiene que decir en cada puerta. Ismael corrige a quien se equivoca. Equivocarse ahí, dice, es quedarse fuera.", hacer = Hacer.GENTE),
        v("A esta hora está en el cuarto de atrás de la casa de Ismael, el que da al huerto de la higuera, con la puerta atrancada y un trapo en la ventana para que no se vea la lámpara desde la calle. Judit trae el pan; Tobías, el vino. Ismael trae lo demás.", hacer = Hacer.GENTE, hasta = "cueva_31"),
        v("A esta hora está en la cueva del Arbel, la que da al lago, sentado en la roca con los del círculo alrededor de la lámpara. Desde que echaron a Ismael de la sinagoga se reúnen aquí. Fuera se oye el viento en la pared del monte. Dentro, solo voces bajas.", hacer = Hacer.GENTE, desde = "cueva_31"),
    )

    private val JER_TRABAJO = listOf(
        je("A esta hora vende pescado salado en el mercado de Jerusalén, cerca de la puerta del Pescado, con los otros de Galilea: lo que trajeron en los burros de parte de Ezequías. Los de la ciudad regatean como si les fuera la vida en ello. Él también.", Hacer.TRABAJO),
        je("A esta hora anda por los patios del Templo, entre los cambistas y los que venden palomas, y mira. Mira las columnas, los sacerdotes, el humo del altar. Todo es de oro y de piedra, y todo es del mundo de abajo, dice Ismael. Aquí le cuesta creerlo.", Hacer.TRABAJO),
        je("A esta hora espera en una fila de peregrinos para entrar en los patios de dentro, con su ofrenda, la que toca en la fiesta. La fila avanza despacio. Los levitas cantan. {nombre} se seca el sudor y piensa en el secadero, donde a esta hora estaría.", Hacer.TRABAJO),
        je("A esta hora duerme envuelto en el manto, en el suelo, con el hatillo bajo la cabeza y las sandalias atadas a la muñeca, para que no se las roben. A su alrededor duermen cientos. Alguien ronca. Alguien llora en sueños. Lejos, un perro.", Hacer.DORMIR),
        je("A esta hora duerme a ratos, al raso, despertándose con cada ruido: un burro, una voz, el paso de los guardias. Es el sueño de los peregrinos, que nunca es entero. Cuando abre los ojos ve estrellas, y se vuelve a dormir.", Hacer.DORMIR),
        je("A esta hora reza de cara al Templo, con los demás peregrinos, las palabras del alba. Aquí todo el mundo las dice a la vez, y la voz de miles sube como un viento. {nombre} reza con ellos y, debajo, dice las suyas.", Hacer.REZAR),
        je("A esta hora reza solo, un poco apartado de los de Magdala, mirando la primera luz sobre el monte de los Olivos. Lejos de casa la oración del alba le sale más despacio, como si las palabras pesaran más. Dice las del círculo dos veces, por si acaso.", Hacer.REZAR),
        je("A esta hora come lo que trajeron de casa: pan duro, aceitunas, queso de cabra y pescado salado, sentado en el suelo con los de Magdala. Lo que se vende en Jerusalén es caro para un salador. Lo que se trae de casa sabe a casa.", Hacer.COMER),
        je("A esta hora come de pie, entre la gente, pan comprado en un puesto y unos higos, sin dejar de mirar alrededor. En las fiestas Jerusalén está tan llena que hasta comer cuesta trabajo. {nombre} come deprisa y guarda la mitad.", Hacer.COMER),
        je("A esta hora baja del monte de los Olivos al valle y sube a la ciudad por las escaleras, entre una multitud que va y viene. Se pierde, se encuentra, se vuelve a perder. Jerusalén tiene más calles que Magdala casas.", Hacer.CAMINO),
        je("A esta hora da la vuelta a las murallas con los de Magdala, por fuera, para ver la ciudad entera. Las piedras de abajo son tan grandes que no se entiende cómo las subieron. Natán preguntaría cómo. {nombre} no sabría qué contestarle.", Hacer.CAMINO),
        je("A esta hora se sienta con los de Magdala junto al fuego, en el monte de los Olivos, y cada uno cuenta lo que ha visto en el día. Uno vio a un sacerdote resbalar en las escaleras; otro, a un soldado de Roma comprar dátiles. {nombre} ha visto cosas que no cuenta.", Hacer.GENTE),
        je("A esta hora busca entre los peregrinos a alguno de los que saben: los hay en todas partes, dice Ismael, y se reconocen por una palabra. Esta vez no encuentra a nadie, o nadie lo encuentra a él. Vuelve al fuego de los suyos.", Hacer.GENTE),
    )

    private val HUIDA_TRABAJO = listOf(
        hu("A esta hora trilla en una era de las afueras, detrás de dos bueyes que dan vueltas sobre las espigas, y luego avienta el grano contra el viento con la horquilla. Es trabajo de campo, y él es de agua. Le duelen músculos que no sabía que tenía.", Hacer.TRABAJO),
        hu("A esta hora carga cántaros de agua desde la fuente hasta las casas de arriba, por un jornal pequeño. La ciudad está en lo alto de un monte y el agua, abajo. Sube y baja todo el día. Al menos, piensa, nadie le pregunta de dónde viene.", Hacer.TRABAJO),
        hu("A esta hora ayuda en un lagar de los montes a pisar uva o a cargar cestos, según lo que haya, por la comida y un poco de plata. Los samaritanos cantan mientras trabajan canciones que él no conoce. Al tercer día ya las tararea.", Hacer.TRABAJO, meses = setOf(8, 9)),
        hu("A esta hora duerme en el cuarto prestado, en una estera que no es la suya, con el manto doblado bajo la cabeza. Por la ventana entra el olor de las eras. Sueña con el lago, y en el sueño el lago está seco.", Hacer.DORMIR),
        hu("A esta hora duerme con un ojo abierto, como dicen los pescadores, atento a cualquier paso en la escalera. Cuando oye uno, se queda quieto hasta que pasa. Siempre pasa. Vuelve a dormirse, poco.", Hacer.DORMIR),
        hu("A esta hora reza al alba de cara al sur, hacia Jerusalén, de donde ha huido, en una ciudad donde todos rezan de cara a su monte. Reza bajito, para que nadie se ofenda. Las palabras del círculo no miran a ningún monte. Esas las dice más alto, por dentro.", Hacer.REZAR),
        hu("A esta hora reza con los otros que huyeron, en el patio, antes de salir a buscar jornal. Rezan por Esteban, por los que se quedaron en Jerusalén y por los que van a ir a buscarlos. {nombre} reza también por Mara, por los niños y por el secadero.", Hacer.REZAR),
        hu("A esta hora come pan de trigo de Samaria, más blanco que el de cebada de casa, con aceite y un puñado de garbanzos tostados. Lo paga con lo que gana en las eras. No hay pescado. Hace semanas que no come pescado, y lo nota.", Hacer.COMER),
        hu("A esta hora come a la sombra de un olivo, con los jornaleros de la era, lo que el dueño reparte: pan, cebollas y agua con vinagre. Los samaritanos se sientan aparte. Al principio. Luego, poco a poco, se van juntando.", Hacer.COMER),
        hu("A esta hora baja a la puerta de la ciudad, donde se juntan los que vienen de Jerusalén, a ver si llega alguien con noticias. Casi siempre llega alguien. Casi nunca son buenas.", Hacer.CAMINO),
        hu("A esta hora camina por las calles de columnas de Samaria, las que hizo el viejo Herodes, sin mirar a nadie a la cara. Es un judío de Galilea en una ciudad de samaritanos y de griegos. Se le nota en el andar. Intenta que no se le note.", Hacer.CAMINO),
        hu("A esta hora se junta con los que huyeron, en un patio, a hablar en voz baja de lo que pasa en Jerusalén y de lo que harán. Unos quieren irse a Damasco; otros, a Antioquía; otros, volver. {nombre} solo quiere volver a Magdala, y no sabe si puede.", Hacer.GENTE),
        hu("A esta hora se sienta solo en el umbral, con la lámpara apagada en la mano, y repite en voz muy baja las palabras de las siete puertas, para no olvidarlas lejos del círculo. Una por una. Sin equivocarse.", Hacer.GENTE),
        hu("A esta hora va a la casa donde predica Felipe, uno de los siete, y se sienta al fondo, junto a la puerta, para poder irse. La casa está llena. Felipe habla de un modo que le recuerda a otro, en otra orilla. No se va.", Hacer.GENTE, desde = "felipe_32"),
    )

    // =====================================================================
    //  El pueblo y el entorno
    // =====================================================================

    private val PUEBLO = listOf(
        v("{entorno} Magdala es un pueblo de pescadores con una torre de piedra junto al agua, de la que le viene el nombre. Las casas son de piedra negra y adobe, con patios y azoteas, y todas huelen a pescado: el que se pesca, el que se sala y el que se vende."),
        v("El secadero de Ezequías está en la orilla, al sur del pueblo: un patio grande de tierra con cañizos tendidos sobre palos, tres tinas de salmuera, un cobertizo para las ánforas y un muelle de piedras donde atracan las barcas al amanecer. {entorno}"),
        v("{entorno} La sinagoga de Magdala está cerca del agua, con bancos de piedra alrededor de la sala y, en el centro, una piedra tallada con el candelabro de siete brazos del Templo. El sábado la llena el pueblo entero. Los demás días, los viejos se sientan a la puerta."),
        v("A una hora de camino hacia el sur está Tiberíades, la ciudad que Herodes Antipas levantó en honor de Tiberio, con su palacio y sus columnas. Mucha gente de Magdala no quiere vivir en ella: dicen que la hicieron encima de un cementerio. {entorno}"),
        v("{entorno} Detrás de Magdala se levanta el Arbel, un monte cortado a pico, con cuevas en la pared de roca. Los viejos cuentan que hace más de sesenta años se escondieron allí los enemigos del viejo Herodes, y que él los sacó de las cuevas uno por uno."),
        v("{entorno} En los días claros se ve al norte el Hermón, blanco de nieve casi todo el año, como un hombro que asoma por encima de los montes. Ismael dice que allí bajaron los Vigilantes (1 Enoc 6). {nombre} no lo mira mucho rato."),
        v("Por el camino que pasa junto a Magdala suben y bajan las caravanas entre Damasco y el mar: camellos, burros y mercaderes que hablan griego y arameo y se paran a comprar pescado salado. {entorno}"),
    )

    private val JER_PUEBLO = listOf(
        je("Jerusalén está encima de sus montes, toda de piedra dorada, y el Templo, en lo más alto, es tan blanco que a pleno sol no se puede mirar. En los patios de abajo se vende de todo; a los de arriba solo pasan los que están puros."),
        je("En la torre Antonia, pegada al Templo, están los soldados de Roma, y en las fiestas se asoman a los pórticos con las lanzas, para que se vea que están. La gente no los mira. Los mira de reojo."),
        je("Por las calles de Jerusalén, en las fiestas, no cabe la gente: peregrinos de Galilea, de Egipto, de Babilonia, de Roma, cada uno con su manera de hablar. En el monte de los Olivos, al otro lado del valle, se ven sus fuegos de noche."),
    )

    private val HUIDA_PUEBLO = listOf(
        hu("La ciudad de Samaria está en lo alto de un monte redondo, con murallas, un templo a César y calles con columnas que mandó hacer el viejo Herodes. Abajo, los valles están llenos de olivos y de eras. No se parece a nada de Galilea."),
        hu("Los de Samaria no suben a Jerusalén: dicen que hay que adorar en su monte, el Gerizim, y miran a los judíos que huyen con lástima y con desconfianza. Les dan trabajo igual. El trabajo no pregunta."),
        hu("Los que huyeron de Jerusalén viven repartidos por la ciudad, en cuartos alquilados, en patios, en casas de los que les creen. Se reconocen por la calle y no se saludan. Por la noche, sí.", desde = "samaria_32"),
    )

    /** Manda la primera regla que se cumple: primero las barcas de la gente que sigue al de Nazaret; si no hay, el secadero. */
    private val ENTORNOS = listOf(
        Entorno("barcas", 0.15, 1.0, frases = listOf(
            "En la orilla hay barcas que no son de Magdala, varadas en fila, y restos de hogueras de gente que ha venido de lejos a ver al de Nazaret.",
            "Desde que el de Nazaret anda por el lago, la orilla amanece llena de barcas de fuera y de ceniza de fuegos.",
        )),
        Entorno("secadero", 0.0, 0.5, frases = listOf(
            "Ahora hay poco pescado en los cañizos, y el secadero tiene huecos vacíos al sol.",
            "En el secadero hay más cañizos vacíos que llenos: el lago da poco en este tiempo.",
        )),
        Entorno("secadero", 0.5, 1.0, frases = listOf(
            "Los cañizos del secadero están llenos de pescado abierto, plateado al sol.",
            "El secadero está tan lleno que el olor a sal llega hasta la sinagoga.",
        )),
    )

    // =====================================================================
    //  La gente cercana
    // =====================================================================

    private val GENTE = listOf(
        v("Mara, su mujer, es de una familia de pescadores de Cafarnaúm y cose las redes mejor que nadie en la calle. Habla rápido, se ríe fuerte y no cree nada de lo que dice Ismael, o eso dice. Pero nunca pregunta adónde va {nombre} de noche."),
        v("Natán, su hijo, va a todas partes detrás de él: al secadero, a la orilla, a la sinagoga. Quiere saberlo todo y lo pregunta todo, y a veces pregunta lo que no se puede contestar: por qué el pescado tiene ojos, adónde se va el sol, quién hizo las estrellas."),
        v("El viejo Ezequías, el dueño del secadero, lleva cincuenta años salando pescado y tiene las manos como cuero viejo. Grita mucho, paga lo justo y conoce cada ánfora por el ruido que hace al golpearla. A {nombre} le enseñó todo lo que sabe, menos a callarse."),
        v("Ismael, el maestro del círculo, llegó de Alejandría hace muchos años, cuando {nombre} era niño. Habla griego y arameo y sabe leer las dos lenguas. Vive solo, de lo que le dan, en una casa con una higuera. Nadie en Magdala sabe bien a qué se dedica. Tampoco lo preguntan."),
        v("Judit, la del círculo, es una viuda que teje lino en un telar de pie, en su patio, y lo vende en Tiberíades. Va a Tiro dos veces al año a comprar tinte y vuelve con hojas copiadas escondidas entre las madejas. Tiene la voz grave y las manos azules."),
        v("Tobías, el barquero del círculo, es un hombre enorme y callado que lleva gente y carga de una orilla a otra del lago. Dice que en el agua, de noche, ha visto cosas que no cuenta. No sabe ni una letra, y es de los que mejor se saben las palabras."),

        v("Shifra, su madre, es viuda desde hace años y vive con ellos. Muele, amasa, cuenta historias de cuando el viejo Herodes era rey y no deja que nadie toque su cántaro de aceite. Tiene una tos de invierno que no se le va, y no quiere que se hable de ella.", hasta = "muere_shifra_31"),
        v("Desde que murió Shifra, su madre, en la casa nadie toca su cántaro de aceite. Está en su sitio, en el rincón, con su tapa de barro. {nombre} lo mira cada mañana al salir. Mara dice que un día habrá que usarlo. Ese día no ha llegado.", desde = "muere_shifra_31"),
        v("María, la vecina, vive en la casa de al lado. Hay días en que está bien y teje en la puerta, y noches en que grita con voces que no son la suya. Dicen que tiene siete demonios dentro (Lucas 8:2). {nombre} piensa en los siete de arriba.", hasta = "maria_sigue_29"),
        v("La casa de María, la vecina, está cerrada desde que se fue con el de Nazaret. Dicen que anda con él por los pueblos, con otras mujeres que lo ayudan con lo que tienen (Lucas 8:3). En su puerta ha crecido la hierba. {nombre} le riega la higuera.", desde = "maria_sigue_29", hasta = "sepulcro_30"),
        v("María, la vecina, fue la primera que vio el sepulcro vacío, y desde entonces anda entre Jerusalén y Galilea. Cuando vuelve a su casa, la gente del pueblo la mira como se mira a alguien que ha estado muy lejos. {nombre} la mira de otra manera.", desde = "sepulcro_30"),
        v("Mara espera un hijo para el verano. Sigue cosiendo redes en el patio, con la tripa cada vez más grande, y dice que esta vez será niña, porque tiene antojo de higos verdes. Shifra dice que eso no quiere decir nada. Las dos se ríen.", desde = "mara_espera_28", hasta = "nace_debora_29"),
        v("Débora, su hija, es pequeña, morena y ruidosa, y cuando {nombre} vuelve del secadero oliendo a sal le tiende los brazos igual. Él la levanta por encima de la cabeza. Ella se ríe y le mete los dedos en la barba.", desde = "nace_debora_29"),
    )

    private val JER_GENTE = listOf(
        je("Con {nombre} suben los de Magdala: una veintena de familias que caminan juntas y acampan juntas, con los burros cargados de pescado salado para vender y de harina para el camino. Cantan los salmos de la subida. En Jerusalén se pierden de vista y se buscan."),
        je("Junto a los de Magdala acampan siempre unos de Cafarnaúm que hablan de todo: de los precios, de los romanos, de los profetas, de quién se ha casado con quién. {nombre} escucha y no dice que es de un círculo. En una fiesta caben todas las maneras de creer."),
    )

    private val HUIDA_GENTE = listOf(
        hu("Con {nombre} huyeron un tejedor de Jerusalén, su mujer y dos hermanos que vendían aceite en el mercado de arriba. Ninguno es del círculo. Comen juntos lo que hay, rezan juntos, cada uno a su manera, y por las noches hablan de Esteban."),
        hu("El dueño del cuarto es un samaritano viejo que no le pregunta nada y le cobra poco. Tiene una nieta que lo mira como a un animal raro. {nombre} piensa en Débora, en Natán y en Mara, y le da a la niña un higo seco cada mañana."),
    )

    // =====================================================================
    //  El objeto del día
    // =====================================================================

    private val OBJETO = listOf(
        v("Todo lo que toca {nombre} en un día ha pasado antes por muchas manos. {dato_objeto}"),
        v("{dato_objeto} Así son las cosas de su gente: humildes, gastadas y con más de un uso."),
        v("Hoy {nombre} se fija en algo que tiene delante todos los días. {dato_objeto}"),
        v("{dato_objeto} Ismael dice que hasta las cosas de barro guardan una chispa."),
        v("{dato_objeto} Nadie lo piensa, pero sin eso no habría pueblo."),
        v("Lo de cada día también tiene su historia. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("lampara", "la lámpara de aceite", "una lámpara de aceite", "La lámpara de aceite",
            "Las lámparas de Galilea son de barro cocido, pequeñas como una mano, con un pico para la mecha de lino y un agujero arriba para echar el aceite de oliva; dan una luz amarilla y temblona y dejan negro el techo."),
        ObjetoCuento("anfora", "el ánfora", "un ánfora", "El ánfora",
            "Las ánforas del secadero son de barro, altas y estrechas, con dos asas y el fondo en punta para clavarlas en la arena; llevan pescado en salmuera, se tapan con barro y, cuando vuelven vacías, se vuelven a llenar."),
        ObjetoCuento("sal", "la sal", "un puñado de sal", "La sal",
            "La sal del secadero viene del mar de la Sal, al sur, en sacos a lomo de burro; es gris y gruesa y se muele con piedra; sin ella el pescado se pudre en un día, y con ella dura meses y viaja hasta Damasco."),
        ObjetoCuento("cuchillo", "el cuchillo de salar", "un cuchillo de salar", "El cuchillo de salar",
            "El cuchillo de salar es de hierro, corto y curvo, con el mango de madera de olivo envuelto en cuerda; se afila cada mañana en una piedra de la orilla; el de Eleazar era de su padre, y tiene la hoja gastada en media luna."),
        ObjetoCuento("red", "la red", "una red", "La red",
            "Las redes del lago son de lino, hechas a mano; las de arrastre se tiran entre dos barcas o desde la orilla, y las de echar son redondas, con pesas en el borde; se tienden a secar en las piedras y se remiendan todas las tardes."),
        ObjetoCuento("pesa", "la pesa de plomo", "una pesa de plomo", "La pesa de plomo",
            "Las pesas de las redes son de plomo, pequeñas, dobladas sobre la cuerda del borde para que la red baje; en Magdala hay muchas, porque las redes se rompen y las pesas se quedan, y el fondo de la orilla está lleno de ellas."),
        ObjetoCuento("aguja", "la aguja de redes", "una aguja de redes", "La aguja de redes",
            "La aguja de las redes es de hueso o de bronce, larga como un dedo, con un ojo grande y la punta roma; con ella se hacen los nudos de la malla, uno igual a otro, miles por red; las mujeres de Magdala la manejan sin mirar."),
        ObjetoCuento("cesto", "el cesto de juncos", "un cesto de juncos", "El cesto de juncos",
            "Los cestos del secadero son de juncos de la orilla, trenzados por las mujeres; en ellos llega el pescado de las barcas y se lleva la sal; un cesto bueno dura un año, y uno malo, un día de lluvia."),
        ObjetoCuento("jarra", "la jarra de barro", "una jarra de barro", "La jarra de barro",
            "Las jarras grandes de barro guardan el agua, el aceite y la harina, con la boca tapada con un plato o con barro; también guardan lo que no se quiere perder, porque nadie mira dentro de una jarra de harina."),
        ObjetoCuento("hojas", "las hojas copiadas", "unas hojas copiadas", "Las hojas copiadas",
            "Las hojas son de papiro, que viene de Egipto: se corta en tiras, se ponen unas cruzadas sobre otras y se aprietan hasta que se pegan solas; se escribe en ellas con tinta de hollín y una caña afilada; mojadas, se borran; secas de más, se rompen."),
        ObjetoCuento("barca", "la barca", "una barca", "La barca",
            "Las barcas del lago son de madera, de unos ocho metros, con vela y remos; caben en ellas doce o quince hombres; se untan de brea por fuera y se varan en la orilla al amanecer, una al lado de otra, como animales que descansan."),
        ObjetoCuento("amuleto", "el amuleto grabado", "un amuleto grabado", "El amuleto grabado",
            "Los amuletos de los puestos de Tiberíades son piedras talladas, verdes o rojas, con figuras: a la que tiene cabeza de gallo y piernas de serpiente la llaman Abraxas; la gente los lleva para la suerte, y Ismael dice que vale más el nombre que la piedra."),
    )

    // =====================================================================
    //  Lo que piensa (vale en Magdala, en Jerusalén y en Samaria)
    // =====================================================================

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa que el mundo es hermoso, el lago, los montes, la luz de la tarde, y que eso es justo lo que lo hace peligroso. Ismael dice que una cárcel bien dorada no parece cárcel. {nombre} mira lo que tiene delante y no sabe si lo odia o lo quiere."),
        v("{nombre} piensa en la chispa que, según Ismael, cada uno lleva dentro, perdida entre la carne como una brasa entre la ceniza. A sus {edad} años no sabe si la suya está encendida. Hay días en que la siente. Hoy no es uno de ellos."),
        v("{mundo} {nombre} piensa en los siete que guardan las puertas del cielo, los arcontes, cada uno con su esfera y su cara de animal. Ismael dice que no hay que tenerles miedo, sino saber qué decirles. {nombre} no sabe si eso basta."),
        v("{nombre} piensa en el dios que hizo el mundo, el que Ismael llama Yaldabaoth y del que dice que se cree el único porque no ve lo que hay encima de él. Es una cosa terrible de pensar en una sinagoga. La piensa igual, callado, mientras reza lo de todos."),
        v("{mundo} {nombre} piensa en las tres clases de gente de las que habla Ismael: los de pura materia, los de alma y los de espíritu (Ireneo, Contra las herejías 1.7.5). Mira a los saladores, a los pescadores, a su familia, y no se atreve a decidir quién es quién."),
        v("{nombre} piensa que la noche es más verdadera que el día, porque de día la luz del sol lo tapa todo y de noche se ven las estrellas, que son agujeros. Ismael dice que no, que las estrellas también son de ellos. {nombre} prefiere pensar que son agujeros."),
        v("{mundo} {nombre} piensa en Sofía, la que cayó, y le da pena, como le daría pena una mujer del pueblo que se perdió por querer saber demasiado. Piensa que querer saber no es pecado. Piensa que, si lo es, él también ha caído un poco."),
        v("{nombre} piensa en el de Nazaret y en lo que vio en el Jordán. Ismael dice que el Cristo de arriba bajó sobre un hombre de verdad, como una paloma (Cerinto, según Ireneo, Contra las herejías 1.26). Judit dice que su cuerpo solo parece cuerpo. Él cree las dos cosas, según el día.", desde = "bautismo_28", hasta = "cruz_30"),
        v("{nombre} piensa en el viernes de Jerusalén y no lo puede pensar entero. Ismael dice que el que colgaba del madero no era el Salvador, que el Salvador estaba encima, riéndose (Apocalipsis de Pedro). {nombre} quiere creerlo. Se acuerda del cielo oscuro.", desde = "cruz_30"),
        v("{nombre} piensa en el hombre que vio en la orilla al amanecer, de pie, de espaldas, hecho de una luz que no era la del sol. No le vio la cara. Piensa que nadie se la ha visto de verdad, y que por eso cada uno lo cuenta distinto.", desde = "orilla_30"),
        v("{nombre} piensa en Simón de Samaria, al que llaman el gran poder de Dios (Hechos 8:10). Habla de cosas que se parecen a las del círculo, y eso, en vez de tranquilizarlo, le da miedo. Piensa que no todo el que habla de la luz viene de ella.", desde = "simon_32"),
    )

    /** El estado del mundo de cada temporada (una frase para `{mundo}`). Están las cinco, aunque solo la 1 tenga sus especiales. */
    private val MUNDOS = mapOf(
        "antipas" to listOf(
            "En Tiberíades, Herodes Antipas cobra más que el año pasado, y sus cobradores cuentan hasta las ánforas vacías.",
            "Dicen en la orilla que el tetrarca se ha quedado con la mujer de su hermano, y que eso no se lo perdona nadie.",
            "Al sur, en Judea, manda Pilato, el prefecto de Roma, y de allá llegan noticias que nadie quiere oír.",
        ),
        "maestro" to listOf(
            "Por toda la orilla no se habla más que del de Nazaret: de lo que dijo, de a quién sanó, de dónde dormirá esta noche.",
            "Llegan barcas de Tiberíades, de Betsaida y de la otra orilla, llenas de gente que busca al de Nazaret.",
            "Los fariseos dicen que el de Nazaret no guarda el sábado como se debe, y la gente los oye y lo sigue igual.",
        ),
        "pilato" to listOf(
            "Se cuenta que Pilato tomó dinero del tesoro del Templo para traer agua a Jerusalén, y allí nadie lo olvida.",
            "En Jerusalén hay más soldados de Roma que nunca, y en Galilea se habla de ellos en voz baja.",
            "Los sacerdotes de Jerusalén y los de Herodes no se ponen de acuerdo en nada, salvo en desconfiar de los profetas de Galilea.",
        ),
        "comunidad" to listOf(
            "En Jerusalén, los del Nazareno lo ponen todo en común y comen juntos en las casas (Hechos 2:44–46).",
            "Cada vez son más los del Nazareno, y los sacerdotes del Templo ya no saben qué hacer con ellos.",
            "Los del Nazareno se reúnen en el pórtico de Salomón, a la vista de todos, y nadie más se atreve a juntarse con ellos.",
        ),
        "sanedrin" to listOf(
            "En Jerusalén, el consejo de los sacerdotes ya no quiere advertir a los del Nazareno: quiere callarlos.",
            "Los de las sinagogas de los griegos, en Jerusalén, discuten con los del Nazareno en cada esquina.",
            "Desde Jerusalén llegan noticias de amenazas, de juicios y de gente que ya no duerme tranquila.",
        ),
    )

    // =====================================================================
    //  El anochecer
    // =====================================================================

    private val ANOCHECER = listOf(
        v("Al caer la tarde el sol se esconde detrás del Arbel y el lago se pone del color del cobre, y luego del plomo. Las barcas salen una detrás de otra con sus antorchas en la proa. {nombre} las mira desde la orilla hasta que solo se ven sus luces."),
        v("Anochece en Magdala con el humo de las cocinas, el ruido de los cántaros en la fuente y los niños que no quieren entrar. {nombre} cierra el secadero, tapa las tinas con tablas y se lava las manos en el lago, que a esta hora está tibio."),
        v("Al caer la tarde {nombre} enciende la lámpara de casa con una brasa del fuego, la pone en su nicho y se queda un momento mirándola. Luego la tapa con la mano, como quien guarda algo, y sale. Tiene que llegar a la reunión antes de que salgan las estrellas."),
        v("De noche el lago es negro y el cielo está lleno de estrellas que se reflejan en el agua, de modo que parece que hay dos cielos, uno encima y otro debajo. Ismael dice que el de abajo es el de verdad, y se ríe. {nombre} nunca sabe si lo dice en broma."),
        v("Anochece, y por el camino de Tiberíades pasan las últimas caravanas, con sus campanillas y sus antorchas, buscando dónde dormir. Los perros del pueblo ladran a todas. {nombre} vuelve a casa por la orilla, con el manto sobre los hombros, sin prisa."),
        v("Cuando se pone el sol, en Magdala se encienden las lámparas una a una, en las ventanas, en los patios, en las barcas. Desde la orilla parece que el pueblo entero reza. {nombre} sabe que no es así. Pero le gusta pensarlo un rato."),
        v("Anochece, y {nombre} sube por el sendero del Arbel con la lámpara tapada, entre las piedras, hasta la cueva donde se reúne el círculo desde que echaron a Ismael. Abajo, el lago. Arriba, la pared de roca. En medio, él.", desde = "cueva_31"),
    )

    private val JER_ANOCHECER = listOf(
        je("Al caer la tarde los de Magdala encienden un fuego, cuecen la harina en las piedras calientes y se sientan alrededor, cansados, con los pies hacia las brasas. Alrededor hay cientos de fuegos como el suyo. {nombre} piensa en la lámpara de su casa, que hoy nadie enciende."),
        je("Anochece lejos de casa. {nombre} se envuelve en el manto, se tumba de espaldas y mira las estrellas, que son las mismas que en Magdala pero parecen más altas. Alguien canta un salmo de la subida. Otros lo siguen, bajito, hasta que se duermen."),
        je("De noche, entre los fuegos de los peregrinos, se habla de todo: de los romanos, de los precios, de los profetas, del Templo. {nombre} escucha y no dice nada. Lo que él sabe no es para decirlo junto a un fuego."),
    )

    private val HUIDA_ANOCHECER = listOf(
        hu("Al caer la tarde {nombre} se sienta en el umbral del cuarto y mira hacia el norte, donde está Galilea, detrás de los montes. No se ve nada. Se ve el Gerizim, que se pone morado. Se queda allí hasta que se hace de noche."),
        hu("De noche los que huyeron se juntan en una casa del barrio bajo y comen lo que cada uno ha traído. Alguien cuenta lo de Esteban otra vez, y nadie lo para. {nombre} enciende la lámpara y la tapa con la mano, como en Magdala. Es lo único que hace como en casa."),
        hu("Anochece sobre los olivares de Samaria, y los perros de las eras les ladran a las estrellas. {nombre} piensa en el lago, en las barcas que estarán saliendo, en Mara cerrando la puerta. Piensa que una puerta cerrada, aquí, no es lo mismo que allí."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1 (el año 28)
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))
    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)

    private val ESPECIALES_T1 = mapOf(
        "hermon_28" to unDia(
            Parrafo.MANANA to "Hoy amanece tan limpio que desde la orilla se ve el Hermón, al norte, blanco de nieve de arriba abajo, como si estuviera a una hora de camino. Ismael lo ha estado esperando toda la semana. Antes de que {nombre} baje al secadero, lo agarra del brazo y lo lleva a la punta de las piedras.",
            Parrafo.PUEBLO to "Desde la punta de las piedras se ve todo el lago y, por encima de los montes del norte, el Hermón, el monte más alto que {nombre} ha visto. Ismael le cuenta que allí bajaron los Vigilantes, doscientos ángeles que juraron juntos en la cumbre y se mezclaron con la gente (1 Enoc 6).",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ha visto ese monte toda la vida y nunca lo había mirado. Ismael dice que los Vigilantes les enseñaron a los hombres cosas que no debían saber, y que por eso el mundo es como es. {nombre} toca dentro del cinto su cuchillo de salar, que es de hierro, y no sabe por qué lo toca.",
        ),
        "tormenta_28" to unDia(
            Parrafo.TRABAJO to "Hoy a media mañana el viento cambia de golpe: baja de los montes por las quebradas, frío, y el lago, que estaba liso, se levanta en olas blancas. {nombre} y los saladores corren a tapar las tinas y a atar los cañizos. Una tabla sale volando y no la vuelven a ver.",
            Parrafo.PUEBLO to "Dos barcas que habían salido tarde no llegan a la orilla. Una vuelca delante del secadero, y los hombres nadan hasta las piedras, agarrados a los remos. La otra vuelca más lejos. Los del pueblo se meten en el agua hasta el pecho, con cuerdas, gritando nombres.",
            Parrafo.ANOCHECER to "Al caer la tarde la tormenta se va como vino, de golpe, y el lago se queda quieto, como si no hubiera pasado nada. De la segunda barca han salvado a tres de cuatro. En la casa del cuarto se oye llorar toda la noche. {nombre} no va a la reunión. Se queda en la orilla, mirando el agua.",
        ),
        "banco_28" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy, antes del alba, Tobías llega a la orilla gritando desde la barca: hay un banco de peces enorme frente a Magdala, tan apretado que se ve hervir el agua. Todas las barcas salen a la vez. Para cuando {nombre} llega al secadero, ya están volviendo, hundidas hasta la borda.",
                Parrafo.TRABAJO to "Hoy no hay horas: el pescado llega por cestos, por montones, y hay que abrirlo antes de que se caliente. {nombre} abre, sala y tiende sin levantar la cabeza, con los otros saladores, con las mujeres del pueblo y con Natán, que lleva la sal en un cuenco. Ezequías no para de gritar de contento.",
                Parrafo.PUEBLO to "Toda Magdala huele a pescado, más que nunca. Los cañizos no alcanzan, y se tiende en los tejados, en las piedras de la orilla, en las esteras de las casas. Los niños espantan a las gaviotas con palos. La caravana de Damasco tendrá ánforas para todo el año.",
                Parrafo.ANOCHECER to "De noche siguen trabajando a la luz de las lámparas y de las antorchas, con los brazos dormidos de tanto abrir. {nombre} se lava las manos en el lago y le arden todos los cortes a la vez. Se duerme sentado contra una tina. Lo despiertan para seguir.",
            )),
            sigue = mapOf(
                Parrafo.TRABAJO to listOf(
                    "Segundo día del banco de peces, y siguen llegando: menos que ayer, pero siguen. {nombre} ya no siente las manos. Abre, sala, tiende; abre, sala, tiende. Ezequías ha llamado a las mujeres de la calle de arriba y les paga en pescado. Hasta los viejos de la sinagoga han venido a ayudar.",
                    "Último día del banco de peces. El agua frente a Magdala ya está quieta, y las barcas vuelven medio vacías, como siempre. En el secadero queda trabajo para una semana: dar la vuelta al pescado, salarlo otra vez, llenar las ánforas. {nombre} trabaja más despacio. Por fin puede.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Al caer la tarde Ezequías saca una jarra de vino y la reparte entre todos, sin aguar, cosa que no hace nunca. Los saladores beben de pie, con las manos blancas de sal. Alguien empieza a cantar. {nombre} canta también, aunque está tan cansado que se le olvida la letra.",
                    "Anochece, y el secadero entero brilla de pescado tendido, plateado bajo la luna. {nombre} se queda un momento mirándolo antes de irse a casa. Piensa que el lago ha dado lo que tenía sin pedir nada a cambio. Piensa que el lago no sabe de quién es.",
                ),
            ),
        ),
        "purim_28" to unDia(
            Parrafo.MANANA to "Hoy es el segundo día de Purim. Ayer se leyó en la sinagoga el rollo de Ester, y hoy, que es Purim en Jerusalén y en las ciudades con murallas, en Magdala se sigue con los regalos y la comida. Desde temprano los niños corren por las calles, y las mujeres llevan platos tapados de casa en casa.",
            Parrafo.PUEBLO to "En la calle se vuelve a contar la historia de Ester, la reina, y de Amán, que quería matar a todos los judíos del reino y acabó colgado en su propia horca. Se cuenta a gritos y con risas, como si hubiera pasado en Tiberíades la semana pasada. Los viejos corrigen lo que los jóvenes se inventan.",
            Parrafo.GENTE to "Mara manda a Natán con un plato de tortas de miel a casa de María, la vecina, como manda la costumbre de Purim: se mandan regalos de comida a los vecinos. María abre la puerta, toma el plato y le da a Natán un puñado de dátiles. Hoy está bien. Hoy le sonríe.",
        ),
        "fiebre_28" to unDia(
            Parrafo.MANANA to "Hoy Shifra, su madre, no se levanta a moler. {nombre} se despierta con el silencio de la piedra y sabe que algo pasa antes de abrir los ojos. Shifra está en su estera, ardiendo, con los labios secos, y dice que no es nada, que la dejen, que ya se le pasará.",
            Parrafo.GENTE to "Mara le pone paños de agua fría en la frente y le da de beber agua con miel, a sorbos. Una vecina trae unas hierbas que huelen a tierra. Shifra se las toma sin protestar, y eso es lo que más asusta a {nombre}: su madre siempre protesta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el cuerpo es una casa prestada, como dice Ismael, y que la de su madre ya es vieja. Piensa que no quiere que se vaya todavía de ella. Piensa que eso es cosa de la carne, de los de abajo. Lo piensa igual, con todas sus fuerzas, toda la mañana.",
        ),
        "sana_28" to unDia(
            Parrafo.MANANA to "Hoy Shifra se levanta de la estera, flaca y amarilla, y lo primero que hace es protestar porque la piedra de moler está sucia. {nombre} la oye desde el patio y se echa a reír solo, con el cuchillo en la mano. Mara sale a decirle que no se ría tan alto. También se ríe.",
            Parrafo.GENTE to "Shifra pide pescado asado, del bueno, no del que sobra, y {nombre} va a la orilla a comprárselo a Tobías, que no quiere cobrárselo. Ella se lo come entero, con los dedos, chupando las espinas, y dice que está salado. Es el pescado más rico que se ha comido nunca en esa casa.",
            Parrafo.ANOCHECER to "Al caer la tarde Shifra se sienta otra vez en el umbral, envuelta en dos mantos, a mirar pasar a la gente, como antes. Le cuenta a Natán historias del viejo Herodes, el que hizo el Templo nuevo. Natán no se las cree. {nombre} se va a la reunión más tranquilo que en toda la semana.",
        ),
        "pesaj_28" to unDia(
            Parrafo.MANANA to "Hoy es la víspera de Pésaj. Desde ayer la casa entera se ha limpiado de levadura, rincón por rincón, con una lámpara, y esta mañana Shifra quema en el patio las últimas migas de pan. Este año no suben a Jerusalén: Shifra no está para caminos, y el secadero no espera.",
            Parrafo.TRABAJO to "Hoy {nombre} trabaja solo hasta media mañana, y luego Ezequías cierra el secadero. Se lava entero en el lago, se pone la túnica limpia y ayuda a Mara a cocer los panes sin levadura en las piedras calientes, planos y duros, como los que se comieron deprisa al salir de Egipto.",
            Parrafo.PUEBLO to "Media Magdala ha subido a Jerusalén, y el pueblo se ha quedado medio vacío, con casas cerradas y barcas varadas. Los que se quedan se juntan por familias. En Jerusalén hoy se sacrifican los corderos en el Templo; aquí, lejos, la Pascua se come con pan sin levadura y hierbas amargas.",
            Parrafo.ANOCHECER to "Al caer la noche se sientan a comer con las sandalias puestas, como manda la costumbre, y Shifra le pregunta a Natán por qué esta noche es distinta de las otras. Natán no lo sabe. {nombre} le cuenta la salida de Egipto. Por dentro piensa en otra salida: la de este mundo.",
        ),
        "cebada_28" to unDia(
            Parrafo.MANANA to "Hoy, el día después de la Pascua, empieza la siega de la cebada. En el Templo, lejos, se ofrece la primera gavilla; aquí, en los campos de detrás del pueblo, se oye ya el ruido de las hoces. Hasta que se ofrece esa gavilla, dicen los viejos, nadie puede comer del grano nuevo.",
            Parrafo.TRABAJO to "Hoy {nombre} no va al secadero: Ezequías se lo ha prestado a su cuñado para la siega, y siega cebada en un campo de la ladera, con una hoz que no conoce y la espalda doblada. Detrás van las mujeres atando gavillas. Al rato tiene las manos llenas de ampollas nuevas, encima de los cortes viejos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mientras siega, que la cebada crece sola, sin saber para quién, como el pescado del lago y como la gente. Piensa en la primera gavilla, la que se le da al Templo. Piensa que todo lo primero se le da a alguien. Se pregunta a quién le dará él lo primero de lo suyo.",
        ),
        "sello_28" to unDia(
            Parrafo.ANOCHECER to "Esta noche Ismael lo espera en la puerta de su casa, con la lámpara ya encendida, y no lo deja entrar hasta que se ha lavado las manos y la cara en el cuenco de la entrada. Dentro están todos los del círculo, callados, sentados en el suelo. Hoy, le dice Ismael, dejas de ser de los que solo oyen.",
            Parrafo.GENTE to "Ismael le enseña, una por una, las palabras que el alma tiene que decir en cada una de las siete puertas cuando sube, para que los guardianes la dejen pasar. Se las dice despacio, al oído, y le pide que las repita hasta que no se equivoque. Esas palabras no se escriben: pasan de boca a oído.",
            Parrafo.PENSAMIENTO to "Al final, Judit pone la lámpara en el suelo, delante de él, y todos le tocan la frente con dos dedos. {nombre} piensa que debería sentir algo, una luz, un temblor. No siente nada, solo el calor de la lámpara y el corazón. Luego, al salir, ve que la noche está más clara que nunca.",
        ),
        "caravana_28" to unDia(
            Parrafo.TRABAJO to "Hoy {nombre} carga ánforas desde el alba: cien, una por una, desde el cobertizo hasta el camino, donde esperan los camellos de la caravana de Damasco, arrodillados, rumiando. El jefe de la caravana cuenta las ánforas con una vara y golpea algunas con los nudillos, para oír si están llenas.",
            Parrafo.PUEBLO to "La caravana ocupa todo el camino de la orilla: camellos con campanillas, burros, mercaderes con mantos de colores, un hombre que vende pimienta y otro que vende telas de Siria. Los niños de Magdala los siguen. Los perros les ladran. Las mujeres miran las telas.",
            Parrafo.GENTE to "Ezequías regatea con el jefe de la caravana en griego, en arameo y con las manos, durante más de una hora, y al final los dos se dan la mano como si se odiaran. Luego Ezequías le dice a {nombre}, bajito, que ha sacado más de lo que esperaba. Esta noche, en casa del viejo, habrá carne.",
        ),
        "shavuot_28" to unDia(
            Parrafo.MANANA to "Hoy es Shavuot, la fiesta de las semanas: siete semanas después de la primera gavilla se celebra el trigo nuevo. En el Templo se ofrecen los dos panes hechos con la harina de este año. En Magdala la gente se pone la ropa buena y va temprano a la sinagoga.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es día de fiesta y de descanso. Va a la sinagoga con Natán de la mano y oye leer de la Ley lo que se manda para esta fiesta: que se cuenten las semanas desde la gavilla y que se lleve al Templo el pan nuevo. Natán pregunta quién cuenta las semanas. {nombre} le dice que todos.",
            Parrafo.PUEBLO to "Por la tarde las familias comen juntas en los patios pan del trigo nuevo, aceite y lo que haya, y los que tienen campo cuentan cuánto les ha dado. Los pescadores no tienen campo, y cuentan barcas. Media Magdala ha vuelto a subir a Jerusalén; los que se han quedado hacen como si no les importara.",
        ),
        "esferas_28" to unDia(
            Parrafo.GENTE to "Esta noche, en la reunión, Ismael alisa el suelo de tierra con la palma de la mano y dibuja con un palo siete círculos, uno dentro de otro, y uno más grande alrededor de todos. Los siete son las esferas de los que guardan el cielo; el de fuera, dice, es Leviatán, la serpiente que lo rodea todo (Orígenes, Contra Celso, libro 6).",
            Parrafo.PENSAMIENTO to "{nombre} mira el dibujo de la arena a la luz de la lámpara y piensa que el lago, el pueblo, el secadero y él mismo están en el centro, en el puntito que Ismael ha dejado sin tocar. Encima, siete puertas cerradas. Piensa que nunca había visto su casa desde tan lejos.",
            Parrafo.ANOCHECER to "Al terminar, Ismael borra el dibujo con el pie, despacio, círculo por círculo, de dentro afuera, y no deja ni una raya. Nada de esto se deja escrito, dice. {nombre} vuelve a casa por la orilla y, al mirar el cielo, le parece ver los círculos todavía, girando muy despacio.",
        ),
        "sharav_28" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy amanece con un viento raro, caliente, que viene del este, del desierto, por encima de los montes de la otra orilla. Los viejos lo huelen antes de que llegue. {nombre} sale al patio y el aire le quema la cara como el de un horno abierto. Las hojas de la higuera se doblan.",
                Parrafo.TIEMPO to "El cielo se ha puesto de un amarillo sucio, sin nubes, y el sol es una mancha blanca que no da sombra. El polvo se mete en la boca, en los ojos, en el pan. Las barcas no han salido: con este viento el lago se pone malo. Dicen que durará tres días. Siempre dura tres días.",
                Parrafo.TRABAJO to "Hoy en el secadero el pescado se seca demasiado deprisa, se arruga y se agrieta, y hay que taparlo con esteras mojadas. {nombre} va y viene con cubos de agua del lago, sin parar, mojando las esteras, que a la media hora están secas otra vez. Ezequías maldice el viento en tres lenguas.",
            )),
            sigue = mapOf(
                Parrafo.TIEMPO to listOf(
                    "Segundo día del viento del desierto. El aire sigue caliente y espeso, y el lago está de un color verde turbio que nadie quiere mirar. Por la noche no refresca. Los niños están irritables, los perros no ladran y los viejos dicen que es un viento de enfermedades.",
                    "Tercer día del viento del desierto, y por la tarde, de golpe, cambia: llega una brisa del oeste, del mar Grande, fresca, que huele a otra cosa. Toda Magdala sale a la calle a respirar. {nombre} se queda de pie en la orilla con los ojos cerrados.",
                ),
                Parrafo.TRABAJO to listOf(
                    "Hoy sigue tapando el pescado con esteras mojadas, y sigue perdiendo: una parte del de ayer se ha quedado duro como la madera. Ezequías dice que lo venderán igual, a los de las caravanas, que no saben distinguir. {nombre} no dice nada. Moja las esteras.",
                    "Hoy, con el viento ya cansado, recogen lo que se ha salvado y tiran lo que no. Ezequías cuenta las pérdidas en voz alta, ánfora por ánfora, y luego se calla. {nombre} le pone una mano en el hombro. Es la primera vez que lo hace.",
                ),
            ),
        ),
        "cobradores_28" to unDia(
            Parrafo.TRABAJO to "Hoy llegan al secadero dos cobradores de Herodes Antipas con un escriba y un soldado, y cuentan las ánforas una por una, las llenas y las vacías, y apuntan el número en una tablilla de cera. {nombre} tiene que moverlas todas para que las cuenten. El soldado mira a las gaviotas.",
            Parrafo.PUEBLO to "Los cobradores van de secadero en secadero por toda la orilla, y detrás de ellos va el silencio. En el mercado nadie regatea hoy. Los pescadores esconden en las casas parte de lo que han pescado. Todo el mundo sabe que lo que se cobra en Magdala se gasta en las columnas de Tiberíades.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que Herodes Antipas cuenta las ánforas como los arcontes cuentan las almas, una por una, apuntándolas, para que ninguna se escape. Piensa que es una idea tonta y peligrosa. Piensa que no se la contará a nadie. Esta noche se la contará a Ismael.",
        ),
        "voces_28" to unDia(
            Parrafo.ANOCHECER to "Esta noche, al volver de la reunión, {nombre} oye a María, la vecina, en su patio. Habla sola, con voces que no son la suya: una grave, una aguda, una que se ríe, otra que llora. Cuenta siete. Se queda quieto en la calle, a oscuras, hasta que las voces se callan.",
            Parrafo.GENTE to "María es la vecina de siempre: la que le regaló a Natán una honda, la que remienda las redes rotas mejor que nadie, la que se ríe con Mara en el patio. Y luego hay noches como esta. Mara dice que no hay que tenerle miedo. Mara no le tiene miedo a nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los siete que guardan las puertas del cielo y en las siete voces del patio de María. Piensa que tal vez los de arriba también bajan, y se meten donde pueden, en la gente que tiene la chispa más encendida. Piensa que no es una idea para pensarla de noche.",
        ),
        "natan_28" to unDia(
            Parrafo.TRABAJO to "Hoy Natán le pide abrir un pescado él solo, y {nombre} le da el cuchillo del abuelo, el que guarda envuelto en un trapo. Le enseña a sujetar el pescado, a meter la punta por debajo, a no cortarse. El primer tajo sale torcido. El segundo, menos. Al tercero Natán se corta, y no llora.",
            Parrafo.GENTE to "Ezequías, que lo ha visto todo desde la sombra, se acerca, le mira el corte a Natán, le echa sal encima y le dice que ya es salador. Natán aprieta los dientes de dolor y de orgullo a la vez. Por la tarde les enseña el corte a todos los niños de la calle.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su padre, que le enseñó lo mismo, con el mismo cuchillo, en el mismo secadero, cuando era más pequeño que Natán. Piensa que las cosas se repiten como el agua del lago, que va y viene. Le gustaría enseñarle a Natán también lo otro. Todavía no.",
        ),
        "rumor_juan_28" to unDia(
            Parrafo.PUEBLO to "Hoy llega a Magdala, con unos mercaderes que vienen del sur, la noticia de un profeta: un tal Juan, hijo de un sacerdote, que vive en el desierto, junto al Jordán, vestido de pelo de camello, y que mete a la gente en el río para el perdón de los pecados (Lucas 3:1–3).",
            Parrafo.GENTE to "En el secadero no se habla de otra cosa. Ezequías dice que profetas hay muchos y que casi todos acaban mal. Uno de los saladores dice que es Elías, que ha vuelto. Otro dice que es el Mesías. Tobías, que lo ha oído en Tiberíades, dice que el profeta dice que él no es nadie.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el agua del Jordán, que baja del Hermón, cruza el lago y sigue hasta el mar de la Sal, donde no vive nada. Le parece raro lavarse los pecados en un agua que va a morir. Esta noche se lo preguntará a Ismael. Ismael tendrá una respuesta. Siempre la tiene.",
        ),
        "rosh_28" to unDia(
            Parrafo.MANANA to "Hoy es Rosh Hashaná, el primer día del mes de tishrí, el año nuevo. Desde la sinagoga llega el sonido del cuerno de carnero, largo, luego entrecortado, luego largo otra vez, y en todo Magdala la gente deja lo que tiene en las manos para escucharlo.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es día de fiesta. Va a la sinagoga con toda la familia, y Shifra, que ya está fuerte, va delante, con su mejor manto. Después se come en casa lo mejor que hay: pescado fresco, pan de trigo, uvas y miel. Mañana empiezan a preparar la subida a Jerusalén.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el año empieza hoy y que no empieza nada, que los días son los mismos, uno detrás de otro. Ismael dice que hay trescientos sesenta y cinco cielos, tantos como días tiene el año del sol (Ireneo, Contra las herejías 1.24; Hipólito, Refutación 7.26), y que en cada uno hay un guardián. Hoy, piensa, se pasa una puerta.",
        ),
        "sube_sucot_28" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy, antes del alba, salen de Magdala los que suben a Jerusalén para Yom Kipur y para Sucot: treinta personas, ocho burros cargados de pescado salado y de harina, y un perro que nadie ha invitado. {nombre} va solo con el grupo: Mara se queda con Shifra y con Natán, que ha llorado porque no lo llevan.",
                Parrafo.TRABAJO to "Hoy no hay secadero: hay camino. Bajan por la orilla hacia el sur, pasan Tiberíades sin entrar y siguen el Jordán por el valle, donde hace más calor que en ninguna parte. {nombre} lleva un burro del ronzal y un odre de agua al hombro. A media tarde ya no habla nadie.",
                Parrafo.PUEBLO to "Por el camino se juntan otros grupos que suben: de Cafarnaúm, de Betsaida, de los pueblos de los montes. Al final de la tarde son cientos. Los que van delante cantan los salmos de la subida, y los de atrás los siguen sin oírlos bien, con un poco de retraso, como un eco.",
                Parrafo.ANOCHECER to "Esta noche duermen al raso, cerca del río, en un sitio donde ya hay ceniza de otros años. Se encienden los fuegos. Se comparte el pan. {nombre} se tumba de espaldas y mira las estrellas. Piensa en Mara, en la lámpara de casa, en las palabras de las siete puertas. Se las dice todas antes de dormir.",
            )),
            sigue = mapOf(
                Parrafo.MANANA to listOf(
                    "Segundo día de camino. Se levantan con la primera luz, sacuden el rocío de los mantos y cargan los burros sin hablar. El valle del Jordán amanece caliente, con niebla sobre el río. {nombre} tiene ampollas en los pies y no lo dice. Nadie lo dice. Todos las tienen.",
                    "Tercer día de camino. Amanece en el valle, entre cañas, y antes de que el sol esté alto se ven a lo lejos las palmeras de Jericó, verdes en medio de la tierra parda. Los de Magdala aprietan el paso. En Jericó hay agua, sombra y dátiles.",
                    "Cuarto día, el de la cuesta. Salen de Jericó de noche, para subir con el fresco, por un camino de piedras que no deja de subir. Al amanecer, desde lo alto, se ve por fin la ciudad, dorada, y el Templo encima. Los de Magdala se paran y se callan.",
                ),
                Parrafo.PUEBLO to listOf(
                    "Por el valle del Jordán no hay pueblos: solo cañaverales, tamariscos y, a lo lejos, los montes pelados de los dos lados. Hace un calor de horno. Los peregrinos caminan pegados al río, por la sombra que haya, y beben cada vez que pueden.",
                    "Jericó es un oasis de palmeras y huertos, con un palacio del viejo Herodes y acequias que traen el agua de los montes. Los de Magdala la miran como se mira a una mujer rica. Aquí se compran dátiles para el camino y se llenan todos los odres.",
                    "La cuesta de Jericó a Jerusalén es un camino de piedras entre montes pelados, donde dicen que hay ladrones, y nadie se separa del grupo. Arriba, de golpe, aparece Jerusalén, toda de piedra dorada, con el Templo blanco y oro encima, más grande que nada de lo que {nombre} ha visto.",
                ),
                Parrafo.TRABAJO to listOf(
                    "Hoy camina desde el alba hasta que oscurece, con un burro del ronzal, por el valle del Jordán. Se para cuando se para el grupo, bebe cuando bebe el grupo, reza cuando reza el grupo. En el camino nadie es salador ni barquero: todos son de Magdala.",
                    "Hoy, en el camino, ayuda a una familia de Betsaida a sacar un burro que se ha metido en el barro de la orilla del río. Tardan una hora. El burro sale, los mira a todos con desprecio y sigue andando como si nada. Los de Betsaida le dan a {nombre} un puñado de dátiles.",
                    "Hoy sube la cuesta empujando un burro que no quiere subir, paso a paso, con el sol en la espalda. A mitad de camino un viejo de su grupo se sienta en una piedra y dice que no puede más. {nombre} le carga el hatillo. El viejo llega arriba cantando.",
                ),
            ),
        ),
        "kipur_28" to unDia(
            Parrafo.MANANA to "Hoy es Yom Kipur, el día del perdón, y en todo Jerusalén nadie come ni bebe desde ayer al ponerse el sol hasta hoy al ponerse el sol. {nombre} se despierta con la boca seca en el monte de los Olivos. Nadie enciende fuego. Los niños piden pan y nadie les contesta.",
            Parrafo.TRABAJO to "Hoy no se trabaja, ni se vende, ni se camina más de lo necesario. {nombre} pasa el día en los patios del Templo, de pie, entre miles, esperando ver salir al sumo sacerdote, que hoy es el único que entra detrás del velo, en el lugar más santo, y del que la gente dice que sale temblando.",
            Parrafo.PUEBLO to "A media tarde ve salir de la ciudad un macho cabrío con una cinta roja, llevado por un hombre hacia el desierto: es el que carga con los pecados de todos y se los lleva lejos, a Azazel (Levítico 16). La gente se aparta a su paso. Algunos le gritan al hombre que se dé prisa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el macho cabrío que se va al desierto con los pecados de todos, y en Azazel, que en el libro de Enoc es uno de los Vigilantes, de los que bajaron a enseñar lo que no debían (1 Enoc). Piensa que hoy todo Jerusalén le manda algo a un ángel caído, y que nadie lo sabe.",
        ),
        "sucot_28" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza Sucot, la fiesta de las cabañas. Los de Magdala amanecen dentro de la suya, hecha ayer con ramas de olivo, de palmera y de sauce en la ladera del monte de los Olivos, con el techo tan ralo que se ven las estrellas, como manda la costumbre. {nombre} se ha dormido mirándolas.",
                Parrafo.TRABAJO to "Hoy, primer día de la fiesta, no se trabaja. {nombre} sube al Templo con el ramo en una mano, una palma atada con sauce y mirto, y un limón amarillo en la otra, como todos, y da la vuelta al altar con la multitud, agitando el ramo mientras los levitas cantan.",
                Parrafo.PUEBLO to "Jerusalén está cubierta de cabañas: en los tejados, en los patios, en las plazas, en las laderas de alrededor, como si la ciudad entera hubiera decidido vivir unos días en el campo. Los peregrinos han llegado de todas partes. Se oyen lenguas que {nombre} no sabía que existían.",
                Parrafo.ANOCHECER to "Esta noche, en el patio de las mujeres del Templo, se encienden unos candelabros enormes, tan altos que hay que subir con escaleras, y su luz llena toda Jerusalén, dicen, como si fuera de día. Los levitas tocan y los hombres bailan con antorchas. {nombre} mira la luz desde abajo, con la boca abierta.",
            )),
            sigue = mapOf(
                Parrafo.PUEBLO to listOf(
                    "Por las mañanas se ve bajar a los sacerdotes con un cántaro hasta la fuente de Siloé y subirlo lleno al Templo entre trompetas, para derramar el agua en el altar. Es la fiesta del agua, en la que se pide la lluvia del año. Los peregrinos los siguen por las calles.",
                    "En los días de fiesta las calles de Jerusalén son un mercado: se venden ramos, limones, palomas, vino, tortas y sandalias para los que llegaron descalzos. Los cambistas del Templo no dan abasto. Por todas partes hay soldados de Roma mirando desde los muros.",
                    "En el monte de los Olivos, alrededor de las cabañas de Magdala, acampan peregrinos de Babilonia, de Alejandría y de Cirene, que hablan griego entre ellos y arameo con los demás. Se cambian comida, noticias y consejos. Los de Alejandría hablan de un sabio de su ciudad que explica la Ley como un griego.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Al caer la tarde vuelve a la cabaña con los de Magdala, comen dentro, apretados, y luego se quedan fuera, junto al fuego, mirando la ciudad iluminada al otro lado del valle. Desde aquí, la luz del Templo parece salir de una grieta en la noche.",
                    "Esta noche vuelve al Templo a ver bailar a los hombres con las antorchas en el patio de las mujeres, delante de los levitas que cantan en las escaleras. Los viejos dicen que quien no ha visto esta alegría no ha visto ninguna. {nombre} piensa que es una alegría de abajo. Igual le gusta.",
                    "De noche, en la cabaña, {nombre} no puede dormir: la luz de los candelabros del Templo llega hasta aquí, por entre las ramas del techo, y le pinta en la manta unas rayas blancas que se mueven con el viento. Las mira hasta muy tarde. Piensa en la luz de verdad, la que no se apaga.",
                ),
            ),
        ),
        "baja_sucot_28" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy, el día después de la fiesta, los de Magdala desmontan la cabaña, cargan los burros con lo que no han vendido y salen de Jerusalén por la puerta del este, hacia Jericó. {nombre} se vuelve una vez a mirar el Templo. Luego ya no se vuelve.",
                Parrafo.TRABAJO to "Hoy baja la cuesta de Jericó, que es peor que subirla: las piedras se mueven, los burros resbalan y las rodillas duelen a cada paso. {nombre} va detrás de todos, sujetando el burro de un viejo por la cola, para que no se le vaya pendiente abajo.",
                Parrafo.PUEBLO to "Por el camino de bajada no se habla más que de lo que cuentan en Jericó: que en un vado del Jordán, a medio día de camino, hay un profeta que bautiza, y que media Judea va a verlo. Los de Magdala deciden pasar por allí. Les pilla de camino.",
                Parrafo.ANOCHECER to "Esta noche duermen en Jericó, bajo las palmeras, cerca de una fuente que no se calla en toda la noche. Hace calor, aunque es otoño. {nombre} se queda despierto oyendo el agua y pensando en el profeta del río. Cree que lo ha soñado antes de verlo.",
            )),
            sigue = mapOf(
                Parrafo.MANANA to listOf(
                    "Segundo día de bajada. Amanecen en Jericó, entre palmeras, y salen hacia el río con el fresco. El grupo va más deprisa que a la subida: ya no hay fiesta que esperar, hay casa. Natán, piensa {nombre}, estará contando los días.",
                    "Tercer día de bajada. Amanece junto al río, con niebla entre las cañas, y el grupo se pone en marcha hacia el norte en cuanto se ve el camino. Nadie habla del profeta. Todos piensan en él.",
                    "Cuarto día de bajada. Al fondo del valle se ve ya el lago, brillando entre los montes como la hoja de un cuchillo. Los de Magdala aprietan el paso sin decirlo. Esta noche dormirán en el valle, y mañana, en casa.",
                ),
                Parrafo.PUEBLO to listOf(
                    "El vado del Jordán está a medio día de Jericó: un sitio donde el río se ensancha y se puede cruzar con el agua por la rodilla, con cañas en las dos orillas y un sendero pisado por miles de pies. Desde lejos se ve el humo de los que acampan allí.",
                    "El valle del Jordán, a la vuelta, parece otro: más corto, más verde, con el río a la derecha. Los peregrinos de Galilea van en grupos que se adelantan unos a otros y se saludan desde lejos.",
                    "Al norte del valle aparece Escitópolis, la ciudad de los griegos, con su teatro y sus templos, y los de Magdala dan un rodeo para no pasar por dentro. Después, por fin, el lago.",
                ),
                Parrafo.TRABAJO to listOf(
                    "Hoy camina desde Jericó hacia el río, con el burro del ronzal y el odre lleno. El valle está tan caliente como a la ida. {nombre} va delante del grupo por primera vez, sin saber por qué tiene prisa.",
                    "Hoy camina por el valle hacia el norte, río arriba, con los pies llenos de ampollas y la cabeza llena de agua. No se acuerda del calor ni del camino. Se acuerda de un hombre flaco metido en el río.",
                    "Hoy camina el último trecho del valle, el más largo, porque ya se ve el lago y no se llega nunca. Al anochecer, un pescador de la orilla sur reconoce a uno del grupo y les da pescado asado. Saben a casa.",
                ),
            ),
        ),
        "juan_vado_28" to unDia(
            Parrafo.MANANA to "Hoy llegan al vado del Jordán con el sol ya alto, y hay más gente que en una fiesta: cientos de personas en las dos orillas, de pie, sentadas, metidas en el agua. Los de Magdala atan los burros a unos tamariscos y bajan al río como todos, sin saber muy bien a qué.",
            Parrafo.TRABAJO to "Hoy {nombre} pasa la mañana en la orilla, mirando. En medio del río, con el agua por la cintura, un hombre flaco, con un vestido de pelo de camello y el pelo largo, mete a la gente en el agua uno por uno y la saca. Es Juan. Cada vez que alguien sale, la gente de la orilla se calla.",
            Parrafo.PUEBLO to "Entre bautismo y bautismo, Juan habla. A los cobradores les dice que no cobren más de lo mandado; a los soldados, que no le saquen nada a nadie y que se contenten con su paga (Lucas 3:12–14). Y dice que detrás de él viene otro más fuerte, al que no es digno de desatarle las sandalias (Marcos 1:7).",
            Parrafo.ANOCHECER to "Esta noche acampan junto al río, con cientos de fuegos alrededor, y nadie habla de otra cosa. Unos se han bautizado; otros, no. {nombre}, no. Se ha quedado en la orilla con el agua por los tobillos, sin saber por qué no ha dado el paso. El agua estaba fría. Eso sí lo sabe.",
        ),
        "vuelve_sucot_28" to unDia(
            Parrafo.MANANA to "Hoy llegan a Magdala a media mañana, sucios, quemados por el sol, con los burros flacos y los odres vacíos. Natán sale corriendo por la orilla a recibirlos y se le tira a {nombre} encima. Mara viene detrás, despacio, sonriendo. Shifra espera en la puerta, como si no se hubiera movido en tres semanas.",
            Parrafo.GENTE to "En casa todos le preguntan por Jerusalén, por el Templo, por las luces de Sucot, y {nombre} cuenta lo que puede. Luego cuenta lo del Jordán, lo de Juan metido en el agua. Shifra dice que eso ya lo hacían los profetas de antes. Mara le pregunta si él se ha bautizado. Él dice que no. Ella no dice nada.",
            Parrafo.ANOCHECER to "Esta noche vuelve a la reunión del círculo, y antes de sentarse cuenta lo de Juan. Ismael escucha con los ojos cerrados. Al final dice solo que el agua es de abajo, que lava lo de fuera, y que ya hablarán. {nombre} se sienta en su sitio de siempre, y le parece que hace un año que no lo pisaba.",
        ),
        "agua_28" to unDia(
            Parrafo.GENTE to "Esta noche, en la reunión, Ismael habla de Juan. Dice que el agua del Jordán lava el cuerpo y la culpa de los de abajo, que es un bautismo de este mundo, y que hace falta otro, de luz, que no se da con las manos. Judit dice que tal vez Juan sea el último de los de abajo. Ismael no le contesta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Juan, metido en el río, flaco, gritando, y no le parece un hombre de abajo: le parece un hombre que espera algo. Piensa que tal vez Ismael tiene razón, y que esperar también es de este mundo. Si es así, él es muy de este mundo. No para de esperar.",
            Parrafo.ANOCHECER to "Al salir de la reunión, {nombre} baja a la orilla y se mete en el lago hasta las rodillas, a oscuras, solo. El agua está fría. Se queda quieto mucho rato. No sabe si se está bautizando o despidiéndose de algo. Luego vuelve a casa con los pies mojados, sin hacer ruido.",
        ),
        "baja_jordan_28" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy, antes del alba, {nombre} y Tobías salen en la barca de Tobías hacia el sur del lago, donde nace el Jordán. Ezequías le ha dado cuatro días sin paga y un sermón. Mara le ha dado pan para cuatro días y ningún sermón, que es peor. El lago está liso como un plato.",
                Parrafo.TRABAJO to "Hoy no sala pescado: rema. Tobías y él se turnan en los remos toda la mañana, hasta la punta sur del lago, donde el agua se hace río entre cañas. Allí dejan la barca a un pariente de Tobías y siguen a pie por la orilla del Jordán, hacia el sur, con el hatillo al hombro.",
                Parrafo.PUEBLO to "Por el valle del Jordán baja más gente que nunca: de Galilea, de la Decápolis, de los montes, que va a ver a Juan, que ahora bautiza en un vado más al norte que el de otoño, a un día del lago. Hay quien lleva enfermos en camillas. Hay quien va solo a mirar.",
                Parrafo.ANOCHECER to "Esta noche duermen en el valle, junto a una familia de Naín que va al Jordán a bautizar al hijo mayor. Comparten el fuego y el pan. Tobías, que nunca habla, habla toda la noche con el padre de la familia, de barcas y de peces. {nombre} mira el fuego y piensa en Juan.",
            ),
            dia(
                Parrafo.MANANA to "Segundo día de camino. Llegan al vado de Juan a media mañana. Hay todavía más gente que en otoño, y entre la gente, los de siempre: los que preguntan, los que gritan, los que lloran. Los discípulos de Juan ordenan la fila de los que quieren bajar al agua.",
                Parrafo.TRABAJO to "Hoy pasa el día en la orilla oyendo a Juan. Dice que él bautiza con agua, pero que el que viene detrás de él bautizará con el espíritu santo (Marcos 1:8). La gente se pregunta quién es ese que viene. Juan no dice su nombre. Puede que no lo sepa.",
                Parrafo.PUEBLO to "El vado es un sitio de cañas y de arena, donde el río se puede cruzar con el agua por la cintura. Juan duerme por ahí, dicen, sin casa, y come langostas y miel silvestre (Marcos 1:6). Sus discípulos lo siguen a todas partes y duermen donde él duerme.",
                Parrafo.ANOCHECER to "Esta noche Tobías le dice que mañana se bautiza. {nombre} le pregunta qué dirá Ismael. Tobías se encoge de hombros: dice que el agua no le puede hacer daño a un barquero. {nombre} se ríe. Luego se queda despierto, mirando las estrellas, que aquí abajo, en el valle, parecen más lejos que nunca.",
            ),
        )),
        "bautismo_28" to unDia(
            Parrafo.MANANA to "Hoy, de mañana, Tobías se mete en el río y Juan lo hunde en el agua entero, como a un niño, y lo saca. Tobías sale a la orilla chorreando, con los ojos muy abiertos, y no dice nada. {nombre} le pasa el manto. Siguen allí, mirando, porque nadie se va: hoy hay algo en el aire que no deja irse a nadie.",
            Parrafo.TRABAJO to "Hoy, a media tarde, entre los que bajan al agua hay un hombre de Galilea, de Nazaret, dicen, que se mete en el río como los demás. {nombre} lo ve de espaldas, desde lejos, entre las cabezas: un hombre más, con el agua por la cintura. Juan lo hunde y lo saca. Y entonces la gente de la orilla grita.",
            Parrafo.PUEBLO to "Unos dicen que el cielo se abrió; otros, que bajó una paloma; otros, que se oyó una voz que lo llamaba hijo (Marcos 1:9–11). {nombre} no ha oído ninguna voz. Ha visto, eso sí, una luz sobre el agua, blanca, que no venía del sol, y al hombre de espaldas saliendo del río con la luz encima, como un manto.",
            Parrafo.ANOCHECER to "Al caer la tarde el hombre de Nazaret ya no está: se ha ido hacia el desierto, dicen, solo. La gente se queda en la orilla hablando bajito y mirando el agua, por si vuelve a pasar algo. No pasa nada más. {nombre} y Tobías duermen junto al río sin decirse ni una palabra.",
        ),
        "vuelve_jordan_28" to unDia(
            Parrafo.MANANA to "Hoy emprenden la vuelta con la primera luz, río arriba, hacia el lago. Tobías camina delante, muy deprisa, como si lo llevara el viento. {nombre} va detrás, mirando el suelo. Ninguno de los dos ha dicho nada desde ayer, y ninguno quiere ser el primero.",
            Parrafo.TRABAJO to "Hoy camina todo el día por el valle sin hablar. Lo único que dice, a media tarde, es que tiene sed. Tobías le pasa el odre. Al llegar a la barca, en el sur del lago, reman hacia Magdala ya de noche, despacio, con el agua llena de estrellas que se rompen con cada golpe de remo.",
            Parrafo.ANOCHECER to "Esta noche, en la barca, a mitad del lago, Tobías deja de remar y le pregunta qué vio. {nombre} le dice que una luz. Tobías dice que él también. Luego vuelven a remar, y no lo hablan más. En la orilla de Magdala los espera un perro, solo, moviendo la cola.",
        ),
        "dos_ensenanzas_28" to unDia(
            Parrafo.GENTE to "Esta noche {nombre} cuenta en la reunión lo del Jordán: el hombre de Nazaret, la luz sobre el agua. Ismael escucha y luego dice que eso es lo que enseñan algunos: que Jesús era un hombre como los demás, y que en el bautismo bajó sobre él el Cristo de lo alto, en forma de paloma (Cerinto, según Ireneo, Contra las herejías 1.26).",
            Parrafo.PENSAMIENTO to "Judit no está de acuerdo. Dice que lo que {nombre} vio no era un hombre con algo encima, sino algo que solo parece un hombre, y que su cuerpo es como un vestido que la luz se pone para que podamos mirarla (Hechos de Juan). Discuten hasta tarde. {nombre} piensa que las dos cosas no pueden ser verdad. Y las cree las dos.",
            Parrafo.ANOCHECER to "Al final, Ismael apaga la lámpara con dos dedos y dice que ya se verá, que el que bajó también tendrá que subir, y que entonces se sabrá quién era. {nombre} vuelve a casa por la orilla. El lago está negro y quieto. Le parece que alguien camina por la otra orilla, con una luz. No hay nadie.",
        ),
        "mara_espera_28" to unDia(
            Parrafo.MANANA to "Hoy, antes de que {nombre} salga al secadero, Mara lo para en la puerta, le arregla el cinto, que no hacía falta, y le dice que espera otro hijo para el verano. Lo dice así, mirando el cinto. {nombre} se queda quieto con la mano en el marco de la puerta. Luego la abraza tan fuerte que ella protesta.",
            Parrafo.GENTE to "Shifra se entera en un momento, porque Shifra se entera de todo, y antes de que el sol esté alto lo sabe media calle. María, la vecina, le trae a Mara un amuleto de cuero para el parto, de los que se cuelgan al cuello. Mara lo acepta, le da las gracias y lo guarda en el arca. No se lo pone. Tampoco lo tira.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que va a traer otra chispa a este mundo, otra alma a la cárcel, como diría Ismael, y que debería entristecerse. No puede. Piensa en Natán cuando nació, rojo y furioso, agarrándole el dedo. Piensa que Ismael no tiene hijos. Piensa que eso, a lo mejor, explica algunas cosas.",
        ),
        "desierto_28" to unDia(
            Parrafo.PUEBLO to "Hoy llegan a Magdala unos que vienen del Jordán y cuentan que el hombre de Nazaret, el del bautismo, se fue solo al desierto y no ha vuelto. Que lleva semanas allí, entre las fieras, y que unos dicen que lo pone a prueba el adversario y otros que le sirven los ángeles (Marcos 1:12–13).",
            Parrafo.GENTE to "Tobías se ha pasado la mañana en el secadero, sin trabajar, oyendo a los del Jordán. Desde que se bautizó, Tobías está distinto: más callado, si cabe, y más amable. Ezequías dice que el agua del río le ha ablandado la cabeza. Tobías se ríe. Antes no se reía nunca.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el desierto, donde, según Ismael, los arcontes tienen más fuerza, porque allí no hay nada que los distraiga. Piensa en el hombre de Nazaret, solo, cuarenta días, delante de todos ellos. Piensa que, si es lo que dice Judit, ellos no saben lo que es. Y que eso los tiene que asustar.",
        ),
        "januca_28" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza Janucá, la fiesta de la Dedicación, que recuerda cuando los macabeos limpiaron el Templo de los ídolos de los griegos y lo volvieron a consagrar. Dura ocho días, y en las casas se encienden luces; por eso hay quien la llama la fiesta de las Luces (Josefo, Antigüedades judías 12).",
                Parrafo.TRABAJO to "Hoy se trabaja, porque Janucá no es día de descanso, pero en el secadero se trabaja contento. Ezequías ha comprado aceite de más para las lámparas de la fiesta y lo ha repartido entre los saladores, un cántaro pequeño para cada uno. {nombre} lo lleva a casa al hombro, con cuidado, como si llevara a un niño.",
                Parrafo.PUEBLO to "Por la tarde, en Magdala, las mujeres ponen lámparas encendidas en las puertas y en las ventanas que dan a la calle, y los niños las cuentan corriendo de casa en casa. Al anochecer el pueblo entero brilla junto al lago, y las barcas que salen a pescar llevan también una luz de más.",
                Parrafo.ANOCHECER to "Esta noche {nombre} enciende la lámpara de la fiesta en la puerta de su casa, con Natán, que quiere hacerlo él. Natán la enciende, se quema un dedo, se lo chupa y se queda mirando la llama. {nombre} le cuenta lo de los macabeos. Natán le pregunta por qué la luz se acaba. Él no le contesta. Ismael le contestaría.",
            )),
            sigue = mapOf(
                Parrafo.PUEBLO to listOf(
                    "En estos días de fiesta Magdala tiene luces en todas las puertas, y de noche, desde el lago, el pueblo parece una barca grande y encendida. Los pescadores dicen que con tantas luces los peces no se acercan a la orilla. Los peces no dicen nada.",
                    "En la sinagoga se cantan cada mañana de la fiesta los salmos de alabanza, y la gente se queda a la puerta, después, hablando del Templo, de los macabeos y de los griegos, que en este tiempo se recuerdan más. Algún viejo habla de los romanos. Lo hacen callar.",
                    "Las lámparas de la fiesta se quedan en las puertas toda la noche, y por la mañana las mujeres las recogen negras de humo para volver a llenarlas. En la calle de {nombre}, la de María, la vecina, es la que más dura. Nadie sabe de dónde saca tanto aceite.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Esta noche las lámparas de la fiesta están encendidas en todas las puertas de la calle, y {nombre} vuelve de la reunión por en medio, como por un pasillo de luz. Piensa que es lo más parecido que ha visto al camino de las siete puertas. Solo que aquí nadie pregunta nada.",
                    "Al caer la tarde Shifra enciende la lámpara de la puerta con una brasa y se sienta al lado, envuelta en su manto, a ver pasar a los vecinos. A cada uno le dice algo. {nombre} la oye desde dentro y piensa que su madre, en la puerta, con la luz al lado, parece una de las guardianas de Ismael.",
                    "De noche, desde la orilla, {nombre} mira el pueblo con todas las lámparas de la fiesta encendidas, y luego mira el cielo, con todas sus estrellas. Piensa que abajo la gente enciende luces para acordarse de algo, y que arriba, a lo mejor, alguien enciende las suyas para que se nos olvide.",
                ),
            ),
        ),
        "carcel_juan_28" to unDia(
            Parrafo.PUEBLO to "Hoy llega la noticia a Magdala: Herodes Antipas ha mandado prender a Juan, el del Jordán, y lo tiene encerrado en Maqueronte, su fortaleza del otro lado del mar de la Sal. Dicen que Juan le echaba en cara su boda: «No te es lícito tener la mujer de tu hermano» (Marcos 6:18).",
            Parrafo.GENTE to "Tobías se entera en la orilla y se queda mirando el agua, con la red en las manos, sin moverse. Ezequías dice que ya lo decía él, que los profetas acaban mal. Shifra dice que el padre de este Herodes también encerraba a todo el que le molestaba. Nadie dice nada más. Hay que trabajar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Juan encerrado en una fortaleza de piedra, en lo alto de un monte, en el desierto: el hombre del agua, sin agua. Piensa que los de abajo tienen sus fortalezas, sus soldados y sus cadenas, y que todo eso, para Ismael, es lo mismo que el mundo. Hoy el mundo es un poco más estrecho.",
        ),
        "galilea_28" to unDia(
            Parrafo.PUEBLO to "Hoy se dice en la orilla que el hombre de Nazaret, el que bautizó Juan, ha vuelto del desierto y anda por Galilea, por los pueblos del lago. Que predica en las sinagogas y en la calle. Que dice que el tiempo se ha cumplido, que el reino de Dios se ha acercado y que la gente cambie de vida (Marcos 1:14–15).",
            Parrafo.GENTE to "Tobías ha ido a oírlo en su barca y ha vuelto de noche, sin pescado y con los ojos brillantes. Dice que el de Nazaret habla como si las cosas fueran suyas, no como los escribas, que siempre dicen lo que dijo otro. Ezequías le pregunta si eso se come. Tobías no se ríe.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el reino del que habla el de Nazaret, el que se ha acercado. Ismael dice que el reino no está en ningún sitio, que está dentro y está fuera, y que el que se conoce a sí mismo lo encuentra (Evangelio de Tomás 3). Le parece raro que dos maestros que no se conocen digan casi lo mismo.",
        ),
        "pescadores_28" to unDia(
            Parrafo.MANANA to "Hoy, de madrugada, Ezequías lo manda con Tobías a Cafarnaúm, en la barca, a comprarles pescado a los de allí, porque en Magdala la pesca de la noche ha sido mala. Llegan con el sol saliendo. En la orilla, los pescadores de Cafarnaúm están echando las redes desde la playa.",
            Parrafo.TRABAJO to "Hoy, mientras Tobías regatea con los de Cafarnaúm, {nombre} ve más allá a un hombre de pie en la orilla, de espaldas, hablando con dos hermanos que echan la red en el agua. Los hermanos dejan la red, salen del agua y se van detrás de él. Un poco más allá, otros dos dejan a su padre en la barca y se van también (Marcos 1:16–20).",
            Parrafo.PUEBLO to "Los pescadores de Cafarnaúm lo cuentan a gritos: Simón, el que luego llamarán Pedro, y su hermano Andrés, y los dos hijos de Zebedeo, Santiago y Juan, se han ido con el de Nazaret, así, sin más, dejando las redes en la arena y al viejo Zebedeo con los jornaleros. Zebedeo no dice nada. Recoge las redes él solo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la luz que vio en el Jordán y en el hombre de espaldas en la orilla. No le ha visto la cara. Nunca se la ha visto. Piensa que tal vez no la tiene, que tal vez la cara es lo que cada uno le pone. Piensa en los cuatro que se han ido detrás de él sin mirar atrás. Él no se ha movido de la barca.",
        ),
        "cafarnaum_28" to unDia(
            Parrafo.PUEBLO to "Hoy, el día después del sábado, llegan las noticias de Cafarnaúm: que el de Nazaret enseñó en la sinagoga y que un hombre con un espíritu impuro se puso a gritar, y que él lo hizo callar y salir; que curó de la fiebre a la suegra de Simón; y que, al ponerse el sol, le llevaron a todos los enfermos del pueblo (Marcos 1:21–34).",
            Parrafo.GENTE to "En Magdala no se habla de otra cosa. Las mujeres de la fuente cuentan que la suegra de Simón se levantó y les sirvió la comida a todos. Shifra dice que a ella la curó una vecina con hierbas y nadie hizo tanto ruido. Mara le dice que se calle. María, la vecina, escucha desde su puerta, muy quieta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el espíritu impuro que gritó en la sinagoga de Cafarnaúm. Dicen que le dijo al de Nazaret: «Sé quién eres, el Santo de Dios» (Marcos 1:24). Piensa que los de abajo lo han reconocido antes que la gente. Es lo que más le asusta y lo que más le gusta de todo lo que ha oído.",
        ),
        "lo_ve_28" to unDia(
            Parrafo.MANANA to "Hoy {nombre} le pide el día a Ezequías, y Ezequías se lo da sin preguntar, cosa rara. Sale con Tobías antes del alba, a pie, por la orilla, hacia el norte, hacia los pueblos donde dicen que anda ahora el de Nazaret. Natán quería ir. Se ha quedado llorando en la puerta.",
            Parrafo.TRABAJO to "Hoy lo encuentran en un pueblo de la orilla, a media mañana, rodeado de gente: enfermos, mirones, pescadores, niños. {nombre} no consigue acercarse. Lo ve de lejos, de espaldas, entre cabezas y brazos: un hombre de pie que habla sin levantar la voz, y aun así se le oye. No le ve la cara.",
            Parrafo.PUEBLO to "Dice que ha venido a predicar también en los otros pueblos, que para eso ha salido (Marcos 1:38). Le llevan a un leproso, y {nombre} ve que la gente se aparta y que él no: lo toca y le dice «Quiero, sé limpio» (Marcos 1:41). Luego ya no se ve nada: la gente se cierra a su alrededor como el agua alrededor de una piedra.",
            Parrafo.ANOCHECER to "Vuelven a Magdala de noche, por la orilla, sin antorcha, guiándose por el ruido del agua. Tobías canta bajito un salmo. {nombre} no canta. Va pensando en la luz que vio en el Jordán, que hoy no ha visto. Hoy solo ha visto a un hombre. Y no sabe qué le ha dado más miedo.",
        ),
    )

    /** Las temporadas 2 a 5, cada una en su archivo (`EspecialesIniciadoT2.kt` a `EspecialesIniciadoT5.kt`). */
    private val OTRAS = listOf(ESPECIALES_INICIADO_T2, ESPECIALES_INICIADO_T3, ESPECIALES_INICIADO_T4, ESPECIALES_INICIADO_T5)

    /** Los textos especiales de cada temporada, por su número: la 1, de este archivo; las demás, de sus archivos. */
    val POR_TEMPORADA: Map<Int, Map<String, TextoEspecial>> by lazy { mapOf(1 to ESPECIALES_T1) + OTRAS.associate { it.temporada to it.textos } }

    val PIEZAS = Piezas(
        vida = "iniciado", genero = Genero.MASCULINO, temporadasEscritas = setOf(1) + OTRAS.filter { it.escrita }.map { it.temporada },
        variantes = mapOf(
            Parrafo.MANANA to MANANA + JER_MANANA + HUIDA_MANANA,
            Parrafo.TIEMPO to TIEMPO + JER_TIEMPO + HUIDA_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + JER_TRABAJO + HUIDA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + JER_PUEBLO + HUIDA_PUEBLO,
            Parrafo.GENTE to GENTE + JER_GENTE + HUIDA_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + JER_ANOCHECER + HUIDA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES_T1 + OTRAS.flatMap { it.textos.entries }.associate { it.key to it.value },
    )
}

/**
 * Los textos especiales de una temporada del cuento de Eleazar ([PiezasIniciado]), que van en su propio archivo para que
 * cada temporada se pueda escribir sin tocar las demás.
 *
 * - [temporada]: de 2 a 5.
 * - [escrita]: se pone en `true` cuando ya están los textos de **todos** sus días especiales; desde ese momento la
 *   temporada entra en `temporadasEscritas` y las pruebas los exigen (3 o 4 párrafos el primer día, cada día de los
 *   especiales largos con su texto o con `sigue`, nada repetido, nada de más de 470 letras).
 * - [textos]: por el id de cada día, que en este calendario lleva el año («pesaj_30», «cruz_30»). Solo puede tener ids de
 *   su temporada.
 */
internal class EspecialesIniciado(val temporada: Int, val escrita: Boolean, val textos: Map<String, TextoEspecial>) {
    companion object {
        /** Un día especial de un solo día: sus párrafos. */
        fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

        /** Los párrafos de uno de los días de un especial largo (para `TextoEspecial(listOf(dia(…), dia(…)), sigue = …)`). */
        fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    }
}
