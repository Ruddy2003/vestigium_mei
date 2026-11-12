package io.github.ruddy2003.vestigium.vidas.piezas

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Genero
import io.github.ruddy2003.vestigium.vidas.ObjetoCuento
import io.github.ruddy2003.vestigium.vidas.Parrafo
import io.github.ruddy2003.vestigium.vidas.Piezas
import io.github.ruddy2003.vestigium.vidas.TextoEspecial
import io.github.ruddy2003.vestigium.vidas.Variante

/*
 * El cuento de Mary, hiladora de Manchester hacia 1840. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor
 * aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Trabaja en una fábrica de algodón de Ancoats, en la sala de las máquinas de hilar, con el calor húmedo, la pelusa y
 *   un ruido que no deja hablar. Vive en una casa adosada por detrás a otra, en un patio con una letrina para todos.
 * - No tiene arcos: el párrafo del pueblo no usa `{entorno}`.
 * - No repite lo que ya dice «Su vida»: la campana antes de las seis, las doce horas, el té dulce, el cuarto sin agua,
 *   el informe de 1842 y que sabe firmar.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG2.kt`):
 * - Jem aparece desde `conoce_jem` (temporada 1); es su novio hasta `boda_jem` y su marido desde entonces (3).
 * - Vive con sus padres hasta `cuarto` (temporada 3); desde entonces, con Jem.
 * - Su padre teje en casa hasta `telar_vendido` (1), hace lo que sale hasta `padre_estacion` (4) y desde entonces carga
 *   bultos en la estación.
 * - Billy va a la escuela dominical, y desde `billy_fabrica` (2) trabaja también en la fábrica.
 * - Tom nace en la temporada 4 (`nace_tom`); el embarazo va de `espera_tom` a `nace_tom`.
 * - Bridget está hasta `bridget_va` (temporada 5).
 * - Los estados: «paro» (la huelga de la temporada 3) y «en_casa» (las cuatro semanas después de que nace Tom).
 * - Las fiestas que vuelven cada año no nombran a Jem, a Tom ni a Bridget, ni dicen con quién vive.
 */
internal object PiezasMary {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A {nombre} la despierta el golpe en la ventana: el despertador de la calle, un viejo con una vara larga, va de casa en casa tocando los vidrios hasta que alguien responde. Ella golpea el vidrio desde dentro, para que se vaya, y se levanta a oscuras."),
        v("Lo primero que se oye son los zuecos. A esta hora la calle entera se llena del ruido de cientos de zuecos con herraduras sobre los adoquines, todos hacia el mismo lado, como un río de madera. {nombre} se pone los suyos y se une al río."),
        v("{nombre} se levanta con el cuerpo todavía cansado del día anterior, se echa agua fría del cubo en la cara y se envuelve en el chal. No hay tiempo para el té. El té, si hay, será a las ocho, en la fábrica, en el bote de lata que lleva en la mano."),
        v("Por la ventana, todavía de noche, se ven encenderse una a una las ventanas de la fábrica, cientos de ellas, en hileras, como un barco enorme varado en medio de la ciudad. {nombre} la mira mientras se ata los zuecos y piensa, como cada mañana, que es bonita de lejos."),
        v("La primera luz, cuando la hay, es gris y huele a carbón. {nombre} sale al patio, donde las vecinas ya hacen fila en la bomba de agua y en la letrina, y saluda con la cabeza, porque a esta hora nadie habla. Hablar es para el sábado."),
        v("La campana de la fábrica suena a lo lejos, y en las calles de Ancoats todos aceleran el paso, porque a las seis en punto el portero cierra la puerta y al que llega tarde le descuentan el día. {nombre} corre los últimos metros con el chal en la cabeza."),
        v("{nombre} se despierta antes de que la llamen, como le pasa casi siempre, y se queda un momento quieta en la oscuridad oyendo respirar a los demás. Es el único rato del día que es suyo. Luego tose, porque siempre tose al despertar, y se levanta."),
        v("{nombre} se levanta del colchón que comparte con Jane, sin despertarla, aunque Jane también tiene que levantarse. Su madre ya está junto al fogón apagado, envuelta en una manta, y su padre todavía duerme, porque para su padre ya no hay campana.", desde = "telar_vendido", hasta = "cuarto"),
        v("{nombre} se levanta sin despertar a Jem, que entra más tarde al taller, y le deja en la mesa pan y el té recalentado. En el cuarto nuevo todavía no está acostumbrada al silencio: no hay hermanos, no hay padre tosiendo. Solo Jem, que ronca bajito.", desde = "cuarto"),
    )

    /** Mientras dura la huelga y la fábrica está parada (estado «paro», temporada 3). */
    private val PARO = listOf(
        v("Hoy no ha sonado la campana. {nombre} se despierta a las cinco por costumbre y se queda acostada, oyendo una calle sin zuecos, que es un ruido que no conocía. Las chimeneas no echan humo. Por primera vez en años, el cielo de Ancoats se ve casi azul.", estado = "paro"),
        v("Por la mañana {nombre} sale a la calle con las demás a ver si hay noticias. Hay grupos en todas las esquinas, y hombres que leen en voz alta lo que dicen los periódicos de los huelguistas. Nadie sabe cuánto va a durar. Todos saben que el pan no espera.", estado = "paro"),
        v("A esta hora, en vez de estar en la fábrica, busca algo que hacer por unos peniques: lava ropa con su madre, cuida niños de una vecina, recoge carbón caído junto al canal. No hay trabajo, porque nadie trabaja, y los que podrían pagar tampoco pagan.", estado = "paro", hacer = Hacer.TRABAJO),
        v("A esta hora camina hasta la plaza, donde hay reunión de los huelguistas. Un hombre subido a un carro habla de la Carta y del jornal de antes, y la gente aplaude. Hay soldados en las calles de alrededor, mirando. {nombre} se queda atrás, por si acaso.", estado = "paro", hacer = Hacer.CAMINO),
        v("A esta hora come lo que hay, que es poco: pan con té muy flojo, sin azúcar, o unas papas hervidas para todos. Su madre sirve a los niños primero y a ella al final. Nadie se queja, porque quejarse no llena.", estado = "paro", hacer = Hacer.COMER),
        v("La fábrica está parada, con las puertas cerradas y un guardia en la entrada, y la calle parece otra. Los niños juegan donde antes no se podía, y los hombres se sientan en los escalones sin saber qué hacer con las manos. Las tiendas fían cada vez menos.", estado = "paro"),
        v("Esta noche no hay que levantarse temprano, y sin embargo nadie se acuesta tarde: no hay vela que gastar. {nombre} se sienta en la puerta con las vecinas, a oscuras, y se cuentan lo que se dice: que van a ganar, que van a perder, que llegan más soldados.", estado = "paro"),
    )

    /** Las cuatro semanas en casa después de que nace Tom (estado «en_casa», temporada 4). */
    private val EN_CASA = listOf(
        v("{nombre} se despierta con el llanto de Tom, que tiene hambre a la hora de la campana, como si ya supiera. Le da el pecho a oscuras, sentada en la cama, mientras oye pasar los zuecos de la calle sin ella. Le parece raro no ir. Le parece raro todo.", estado = "en_casa"),
        v("A esta hora no está en la fábrica: está en casa con Tom, que duerme y despierta y vuelve a dormir, y ella lava sus trapos, los tiende junto al fuego y los vuelve a lavar. Su madre viene cada día a ayudarla y a decirle que lo hace todo mal, con cariño.", estado = "en_casa", hacer = Hacer.TRABAJO),
        v("A esta hora no camina a ninguna parte: se asoma a la puerta con Tom en brazos, envuelto en el chal, para que le dé el aire, y las vecinas que pasan se paran a verle la cara. Todas dicen a quién se parece. Ninguna dice lo mismo.", estado = "en_casa", hacer = Hacer.CAMINO),
        v("A esta hora come sentada junto al fuego, con Tom dormido en la cuna, pan con manteca y té con azúcar, que Jem le compra estos días aunque no alcance. Come despacio por primera vez en años, sin campana.", estado = "en_casa", hacer = Hacer.COMER),
        v("Al caer la tarde oye la campana de salida a lo lejos y, al rato, los zuecos que vuelven por la calle. Jem entra con frío y las manos negras, se las lava antes de tocar a Tom y se sienta a mirarlo dormir. {nombre} piensa que dentro de unos días ella volverá a ser zuecos.", estado = "en_casa"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En Manchester el tiempo se mira poco, porque en la fábrica no hay ni tiempo ni ventanas que se abran."),
        v("{nombre} mira el cielo un momento al salir, por encima de los tejados y de las chimeneas. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El humo de las chimeneas se queda colgado sobre la ciudad, y el cielo de Manchester tiene casi siempre el mismo color, que no es ninguno."),
        v("{cielo} En {estacion} la sala de hilar está igual que siempre, caliente y húmeda, porque el algodón lo necesita así. La {estacion} es lo que se encuentra al salir."),
        v("De camino a la fábrica se cruza el canal, que es negro y huele a todo lo que le tiran. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Las viejas del patio dicen que el tiempo era mejor cuando había menos chimeneas, y lo dicen tosiendo."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está casi limpio, y el sol se ve como una moneda de cobre detrás del humo.",
            "Hace un día claro, de los pocos, y desde el puente se ven los montes verdes del otro lado de la ciudad.",
            "No hay nubes, y el sol hace brillar los ladrillos mojados de la calle.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris como el agua del canal.",
            "Hay nubes bajas que se confunden con el humo.",
            "Está nublado, como casi siempre, y la luz del día parece de tarde.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el agua baja negra por las paredes de ladrillo.",
            "Cae una lluvia fina que no para, la de Manchester, que no moja hasta que ya se está empapada.",
            "Llueve, y los patios se llenan de charcos que no se secan en una semana.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos retumban sobre los tejados y la lluvia cae de lado.",
            "El cielo se pone negro de golpe, más negro que el humo, y empieza a llover a cántaros.",
        ),
        Clima.NIEVE to listOf(
            "Ha nevado, y la nieve dura blanca una hora, hasta que el hollín la pinta de gris.",
            "Caen copos que se ponen negros antes de llegar al suelo.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, una niebla amarilla de humo que pica en la garganta y no deja ver la otra acera.",
            "La niebla es tan espesa que las farolas de gas parecen velas.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del oeste, que barre el humo y deja ver, por un rato, la ciudad entera.",
            "Hay viento, y las chimeneas echan el humo de lado, como banderas sucias.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en los baldíos entre las casas crecen hierbas que nadie plantó.",
            "En primavera los días se alargan y, por fin, se va a la fábrica con algo de luz.",
            "Es primavera, aunque en Ancoats solo se nota en los días.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y la sala de hilar es un horno; algunas se desmayan junto a las máquinas.",
            "En verano los patios huelen tanto que se cierran las ventanas aunque haga calor.",
            "Es pleno verano, cuando el sol sale antes que la campana.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y vuelven las nieblas que se meten en los pulmones.",
            "En otoño se compra carbón para el invierno, si se puede, y si no, se reza.",
            "Es otoño, y se vuelve a ir a la fábrica de noche.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando se entra a la fábrica de noche y se sale de noche, y el día se ve por la ventana.",
            "En invierno la gente se muere de toses que en verano no habrían matado a nadie.",
            "Es invierno, y el frío de la calle es peor después del calor de la sala de hilar.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora atiende su máquina de hilar, una hilera de cien husos que giran tan deprisa que no se ven. Camina de un extremo a otro mirando los hilos, y cuando uno se rompe lo anuda sin parar la máquina, con dos dedos, en lo que dura un parpadeo.", hacer = Hacer.TRABAJO),
        v("A esta hora cambia las bobinas llenas por vacías, una tras otra, a lo largo de la máquina, mientras la máquina sigue. El ruido es tan fuerte que en la sala nadie habla: las mujeres se entienden moviendo mucho los labios, y {nombre} lee los labios mejor que las letras.", hacer = Hacer.TRABAJO),
        v("A esta hora suda en la sala de hilar, donde el aire es caliente y mojado porque el algodón se rompe si está seco. La pelusa flota como nieve y se pega al pelo, a las pestañas, a la garganta. {nombre} tose y sigue, como todas.", hacer = Hacer.TRABAJO),
        v("A esta hora pasa el capataz, el señor Holt, con su reloj en la mano y su libreta de multas en el bolsillo. Hay multa por hablar, por abrir una ventana, por silbar, por llegar tarde y por tener sucia la máquina. {nombre} no levanta la vista.", hacer = Hacer.TRABAJO),
        v("A esta hora limpia la máquina, que se limpia andando: hay que quitarle la pelusa con un cepillo mientras gira, sin que la falda ni el pelo se acerquen a los engranajes. Todas en la sala conocen a alguna a la que la máquina le llevó un dedo.", hacer = Hacer.TRABAJO),
        v("A esta hora trabaja con la cabeza en otra parte, que es como se aguantan las doce horas: las manos saben lo que hacen y la cabeza se va. Hoy la cabeza de {nombre} se va a un prado que vio una vez, de niña, en las afueras, con vacas.", hacer = Hacer.TRABAJO),

        v("A esta hora camina a la fábrica por las calles de Ancoats, todavía oscuras, entre casas de ladrillo negro de hollín, con el bote del té en la mano. Las farolas de gas están encendidas en las calles grandes; en las suyas, no.", hacer = Hacer.CAMINO),
        v("A esta hora cruza el puente sobre el canal con cientos de obreros, todos con el mismo paso y el mismo chal. Por el canal pasan barcazas cargadas de carbón tiradas por caballos, que van a las calderas de las fábricas, y el agua no refleja nada.", hacer = Hacer.CAMINO),
        v("A esta hora camina deprisa, con Bridget, su amiga irlandesa, que vive en la calle de al lado y le cuenta cosas de su tierra que {nombre} no se cree del todo. Bridget habla deprisa y se ríe más. Es la única que la hace reír antes de las seis.", hacer = Hacer.CAMINO, hasta = "bridget_va"),
        v("A esta hora camina sin pensar, medio dormida, porque el camino se lo saben los pies: la esquina de la taberna, la de la iglesia, el puente, la puerta. {nombre} podría hacerlo con los ojos cerrados, y algunas mañanas lo hace.", hacer = Hacer.CAMINO),

        v("A esta hora es el rato de comer, que se toma junto a la máquina, de pie o sentada en el suelo: pan con algo de manteca y el té del bote, que ya está tibio. Las mujeres se cuentan cosas a gritos, porque las máquinas no paran del todo.", hacer = Hacer.COMER),
        v("A esta hora es la hora de comer, y {nombre} corre a casa con las demás, porque no está lejos: papas hervidas con sal, un poco de tocino si es día de suerte, y té. Se come deprisa, de pie, y se corre de vuelta antes de que suene otra vez la campana.", hacer = Hacer.COMER),
        v("A esta hora come en el patio de la fábrica, sentada en un escalón, el pan y el queso que trajo envueltos en un pañuelo. Junto a ella, las demás comen lo mismo o menos. Una vieja de la sala reparte pellizcos de su pan entre las más jóvenes, que son las que tienen más hambre.", hacer = Hacer.COMER),

        v("A esta hora remienda. En casa siempre hay algo roto: una falda, una camisa, la chaqueta de alguien. {nombre} cose junto a la vela, con la cara muy cerca del trapo, porque los ojos ya no le dan después de doce horas mirando hilos.", hacer = Hacer.CUIDAR),
        v("A esta hora va al mercado, que de noche se llena de gente que acaba de cobrar y compra lo que sobró del día, más barato: papas, cebollas, un hueso para la sopa, pan del día anterior. {nombre} regatea con el frutero, que es de Irlanda y regatea mejor.", hacer = Hacer.CUIDAR),
        v("A esta hora barre el cuarto, saca las cenizas del fogón y llena el cubo en la bomba del patio, donde hay fila también de noche. Ayuda a su madre con la ropa ajena, que cuelga de una cuerda de pared a pared y hay que agacharse para pasar.", hacer = Hacer.CUIDAR),
        v("A esta hora se sienta junto al fuego con las manos en el regazo, por fin, y no hace nada, que es lo que más necesita. Alguien le pone una taza de té en la mano. Se duerme sentada un momento, con la taza, y se despierta antes de tirarla.", hacer = Hacer.CUIDAR),

        v("A esta hora duerme, con el cuerpo pesado como un saco. Afuera llueve contra la ventana, y en la casa de detrás, que comparte la pared de atrás con la suya, alguien tose toda la noche. {nombre} no lo oye. A esta hora no oye nada.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con la máquina, como sueñan todas: los husos girando, un hilo que se rompe y no lo encuentra. Se despierta con los dedos moviéndose solos, anudando nada, y se vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, porque a las cinco hay que levantarse y lo sabe hasta dormida. Las ratas corren por el techo y los borrachos cantan en la esquina. En Ancoats la noche hace tanto ruido como el día, pero un ruido que no cansa.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La calle de {nombre} es una hilera de casas de ladrillo pegadas una a otra y espalda con espalda con las de la calle de atrás, de modo que no tienen ventana trasera. Al fondo, en un patio, hay una bomba de agua y dos letrinas para treinta casas."),
        v("Ancoats es un barrio de fábricas: siete u ocho chimeneas altísimas, cada una con su nombre en ladrillo blanco, y alrededor, apretadas, las casas de los que trabajan en ellas. El canal pasa por en medio, negro, con barcazas. Aquí el día lo marca la campana."),
        v("En la calle de al lado viven los irlandeses, que llegaron con lo puesto y aceptan cualquier jornal, y por eso los odian los que también aceptan cualquier jornal. Hay sótanos donde viven familias enteras con el cerdo. {nombre} no entra en esa calle de noche."),
        v("En el centro de la ciudad hay almacenes de los comerciantes del algodón, grandes como palacios, con columnas en la puerta, y casas de los dueños de las fábricas. Ellos viven afuera, en el campo, y vienen en coche de caballos. Nadie de Ancoats los ha visto nunca."),
        v("Las tabernas de la esquina se llenan los sábados de hombres que cobraron, y algunos domingos de reuniones de los cartistas, que piden el voto para los que no tienen casa propia. Se leen los periódicos en voz alta para los que no saben leer, que son casi todos."),
        v("El río de la ciudad es negro y espeso, y no se mueve: lleva todo lo que sueltan las fábricas, los mataderos y las letrinas. Los niños juegan en la orilla igual. Dicen que antes había truchas. Nadie que viva hoy en Ancoats ha visto una trucha."),
    )

    private val GENTE = listOf(
        v("Su madre, Ann, lava ropa ajena para las casas buenas, y la casa siempre huele a jabón y a ropa mojada. Tiene las manos rojas y agrietadas hasta el codo y una voz que se oye desde el patio. A {nombre} le dice cada noche que coma, aunque no haya qué."),
        v("Jane, su hermana, trabaja en la misma sala, de anudadora: va detrás de las máquinas atando los hilos rotos, agachada bajo los carros que van y vienen. Tiene las rodillas negras y unas manos más rápidas que las de {nombre}, y lo sabe."),
        v("Billy, su hermano pequeño, va a la escuela dominical de la iglesia, donde le enseñan a leer la Biblia los domingos, que es el único día que puede. Vuelve a casa recitando versículos que no entiende, y su padre le pide que se los repita."),
        v("El señor Holt, el capataz de la sala, era obrero como ellas y ahora es capataz, y por eso es peor que el dueño, dicen todas. Lleva el reloj de la fábrica en el bolsillo y lo mira como si fuera suyo. {nombre} le tiene miedo y no se lo enseña."),
        v("La vecina de la casa de detrás, la señora Riley, tiene siete hijos vivos y tres muertos, y los cuenta así, todos, cuando se le pregunta. Cuida a los niños de las que trabajan por un penique al día, y les da un jarabe para que duerman que {nombre} no le daría a un perro."),
        v("Su padre, Thomas, teje en casa, en un telar de mano que ocupa medio cuarto, y cada semana le pagan menos por la misma tela, porque los telares de vapor la hacen más barata. Trabaja más horas que ella, y gana la mitad. No se queja; solo tose.", hasta = "telar_vendido"),
        v("Su padre, Thomas, vendió el telar y ahora hace lo que sale: descargar carbón, barrer un almacén, llevar recados. Hay días que no sale nada, y se queda en casa mirando el rincón donde estaba el telar. Fue tejedor treinta años, y lo sigue siendo, sin telar.", desde = "telar_vendido", hasta = "padre_estacion"),
        v("Su padre, Thomas, carga bultos en la estación del tren, de sol a sol, y vuelve con la espalda doblada y algo de dinero cada sábado. Dice que el tren es el futuro. Lo dice el hombre a quien las máquinas le quitaron el oficio, y lo dice sin rencor.", desde = "padre_estacion"),
        v("Billy ya trabaja en la fábrica, de barrendero: se mete debajo de las máquinas en marcha a recoger la pelusa del suelo, porque es pequeño y cabe. {nombre} lo vigila desde su sitio, y cada vez que se mete debajo, deja de respirar hasta que sale.", desde = "billy_fabrica"),
        v("Jem, el mecánico, la espera algunos días a la salida de la fábrica, con las manos negras de grasa y una flor robada de algún jardín. No habla mucho; acompaña. {nombre} hace como que no lo esperaba, todos los días, y llega a casa sonriendo.", desde = "conoce_jem", hasta = "boda_jem"),
        v("Jem, su marido, trabaja en un taller de máquinas del otro lado del canal, haciendo las piezas de las máquinas que ella atiende. Le explica cómo funcionan los engranajes, y ella le explica cómo duele un dedo cogido en ellos. Se entienden.", desde = "boda_jem"),
        v("{nombre} espera un hijo, y lo esconde en la fábrica todo lo que puede, apretándose el delantal, porque a las que se les nota las cambian a peores sitios. Las otras mujeres de la sala lo saben y no dicen nada, y le dejan los trabajos que no son de agacharse.", desde = "espera_tom", hasta = "nace_tom"),
        v("Tom, su hijo, se queda con su abuela mientras ella está en la fábrica, y Ann lo cuida como cuidó a los suyos, entre la ropa mojada. Cuando {nombre} vuelve, Tom ya la conoce por los pasos, o eso dice ella, y estira los brazos.", desde = "nace_tom"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de casa: sin haberlo aprendido nunca."),
        v("Hay cosas que solo se miran cuando faltan. {dato_objeto}"),
        v("{dato_objeto} En el patio se dice que los pobres no tienen cosas, sino lo que les queda."),
        v("{dato_objeto} Son cosas que se aprenden mirando a la madre, como casi todo."),
        v("En la casa de una obrera hay pocas cosas, y cada una sirve para tres. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("bobina", "la bobina", "una bobina", "La bobina",
            "La bobina es un carrete de madera donde se enrolla el hilo que sale de la máquina; una llena pesa poco, y en un turno pasan por sus manos cientos de ellas, que se van a otras fábricas a ser tela."),
        ObjetoCuento("lanzadera", "la lanzadera", "una lanzadera", "La lanzadera",
            "La lanzadera es una pieza de madera con puntas de hierro que lleva el hilo de un lado al otro del telar; la de su padre está gastada por el centro, con la forma de su mano, y la guarda aunque ya no tenga en qué usarla."),
        ObjetoCuento("chal", "el chal", "un chal", "El chal",
            "El chal de lana es lo único de abrigo que tiene una obrera: se lleva sobre la cabeza cuando llueve, sobre los hombros en la fábrica y encima de la colcha por la noche; en la calle, por el chal se sabe quién es obrera y quién no."),
        ObjetoCuento("zuecos", "los zuecos", "unos zuecos", "Los zuecos",
            "Los zuecos tienen la suela de madera de aliso y la parte de arriba de cuero, y debajo llevan herraduras de hierro como los caballos; duran años, se oyen de lejos y son lo único que no se moja en las calles de Manchester."),
        ObjetoCuento("tetera", "la tetera", "una tetera", "La tetera",
            "La tetera es de lata, abollada, y está siempre junto al fuego; el té de los pobres se hace con las mismas hojas varias veces, hasta que sale casi agua, y con azúcar se toma igual, porque calienta y quita el hambre un rato."),
        ObjetoCuento("pan", "el pan", "un pan", "El pan",
            "El pan es lo que más se come en la casa y lo que más sube cuando el trigo sube; los que hacen las leyes tienen tierras y quieren el trigo caro, dicen en la taberna, y por eso el pan de Ancoats cuesta lo que cuesta."),
        ObjetoCuento("vela", "la vela", "una vela", "La vela",
            "Las velas de los pobres son de sebo, que humea y huele a grasa; se encienden solo un rato y se apagan con los dedos, y en invierno, cuando se sale y se vuelve de noche, una vela es casi toda la luz de la semana."),
        ObjetoCuento("carta", "la carta", "una carta", "La carta",
            "Una carta es una hoja doblada y sellada con cera, sin sobre, que se lleva al correo; antes la pagaba el que la recibía, según la distancia, y muchos pobres las devolvían sin abrirlas porque no podían pagarlas."),
        ObjetoCuento("penique", "el penique", "un penique", "El penique",
            "El penique es una moneda grande de cobre con la cara de la reina; con un penique se compra un pan pequeño, una vela o una carta, y en una semana de trabajo ella gana menos peniques que horas tiene la semana."),
        ObjetoCuento("estandarte", "el estandarte de la escuela dominical", "un estandarte", "El estandarte",
            "El estandarte de la escuela dominical es de seda pintada, con el nombre de la iglesia y un versículo, y lo llevan dos hombres con varas y cuatro niños con cintas; se saca una vez al año, en Pentecostés, y se guarda el resto del año envuelto en papel."),
        ObjetoCuento("periodico", "el periódico", "un periódico", "El periódico",
            "El periódico de los cartistas sale cada semana y cuesta casi lo que una comida, así que se compra entre muchos y se lee en voz alta en la taberna; el que lee bien es importante, y el que no sabe leer escucha y luego cuenta lo que entendió."),
        ObjetoCuento("cuna", "la cuna", "una cuna", "La cuna",
            "La cuna de la familia es una caja de madera con unos patines curvos debajo, para mecerla con el pie mientras se hace otra cosa; ha servido a todos los hijos, a los vivos y a los que no, y pasa de casa en casa cuando hace falta."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en la fábrica como se piensa en el tiempo: no se la elige, se vive dentro. Le quita doce horas al día y le da pan. A sus {edad} años no conoce otra cosa, y a veces le da miedo pensar que no la conocerá nunca."),
        v("Piensa a veces en el algodón, que viene de muy lejos, de América, en barcos, y que lo cosechan esclavos, dicen en la taberna. Piensa que ella y esos esclavos tocan el mismo algodón, en las dos puntas, y que ninguno de los dos se lo pondrá nunca de vestido bueno."),
        v("{mundo} Ella piensa en cosas más cercanas: en el alquiler, en la tos de su padre, en la multa de la semana. Las cosas grandes son de los que tienen voto, y en su calle no lo tiene nadie, ni los hombres."),
        v("{mundo} A sus {edad} años, {nombre} ha visto morir a dos hermanos pequeños, uno de fiebre y otro de nada, de no crecer. En Ancoats eso no es una desgracia, sino lo normal, y por eso nadie habla de ello. Ella tampoco, pero se acuerda."),
        v("Su abuela nació en una aldea, con vacas y un huerto, y vino a la ciudad cuando su padre era niño, porque en la fábrica se ganaba más. {nombre} piensa que su abuela cambió el cielo por un jornal, y que ella nunca ha visto el cielo que su abuela dejó."),
        v("{mundo} A veces, en la sala, entre el ruido, {nombre} canta por dentro una canción de la iglesia, la única que se sabe entera. Nadie la oye; ni ella misma. Pero le sirve para llevar el paso de la máquina y para no pensar en la hora."),
    )

    private val MUNDOS = mapOf(
        "paz" to listOf(
            "En la taberna se habla del pan, que está caro, y de los que quieren quitar la ley del grano.",
            "Se dice que la reina es joven y buena, y lo dicen hasta los que piensan que no sirve para nada.",
            "En la fábrica se habla de que el dueño compró máquinas nuevas, y nadie sabe si eso es bueno.",
        ),
        "hambre" to listOf(
            "En el barrio se habla de que no hay pedidos y de que las fábricas paran días enteros.",
            "Se dice que este año hay más hambre que nunca, y los viejos dicen que han visto años peores, pero no muchos.",
        ),
        "huelga" to listOf(
            "Se habla de la Carta, de la huelga y de los soldados, y de si los dueños van a ceder.",
            "En el patio se dice que en todo el norte las fábricas están paradas, y que esta vez sí.",
        ),
        "ley" to listOf(
            "Se habla de la ley nueva de las fábricas, que dicen que pondrá un tope a las horas.",
            "En la taberna se dice que el tren llega ya hasta el centro, y que el mundo se ha vuelto pequeño.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando suena la campana de salida, ya es de noche en invierno y casi de noche en verano. {nombre} sale de la fábrica con la cabeza zumbando, y durante un rato el silencio de la calle le parece un ruido. Las orejas tardan en volver a oír."),
        v("Al caer la tarde se encienden las farolas de gas en las calles grandes, y las ventanas de la fábrica se apagan de golpe, todas a la vez, cuando paran las máquinas. {nombre} vuelve a casa con las demás, despacio, porque ahora ya no hay prisa."),
        v("Se cena junto al fuego, cuando hay carbón: papas, pan, té, y los días buenos un arenque. Se habla poco, porque todos están cansados, y se habla de la fábrica, porque es lo único que pasa. Luego se apaga la vela, que no hay más."),
        v("De noche, en el patio, las vecinas se sientan en los escalones a fumar en pipa y a contarse lo del día, y {nombre} se sienta con ellas cuando no puede más de cansada. Las estrellas no se ven por el humo. Se ve el resplandor rojo de las fundiciones."),
        v("Anochece en Ancoats, y por las calles pasan los borrachos del sábado, o los del lunes, que también los hay. {nombre} cierra la puerta con la tranca y se acuesta vestida, porque hace frío y porque mañana no habrá tiempo de vestirse."),
        v("La última luz del día se queda un rato en lo alto de las chimeneas, que son lo único alto del barrio, cuando abajo ya es de noche. {nombre} la mira desde la ventana, un momento, y piensa que ahí arriba, por lo menos, todavía es de día."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "ano_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y la fábrica trabaja igual, porque el año nuevo no es fiesta para el algodón. Pero anoche, a las doce, sonaron las campanas de todas las iglesias, y el primer hombre que entró en cada casa traía un trozo de carbón en la mano, para que no falte el fuego.",
            Parrafo.PUEBLO to "Por la calle los niños van de puerta en puerta pidiendo un penique por el año nuevo, y algunas puertas se abren y otras no. En las casas buenas del centro hay bailes; en las tabernas de Ancoats, también, con un violinista ciego que toca lo mismo toda la noche.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza, que tendrá doce horas cada día, como el que acaba. Pide poco: que no se enferme nadie en casa, que no le pongan multas, que el pan no suba. Si se cumple una de las tres, será un año normal.",
        ),
        "carnaval" to unDia(
            Parrafo.MANANA to "Hoy es martes de carnaval, el último día antes de la cuaresma, y en todas las casas se hacen tortitas en la sartén para acabar con la manteca y los huevos. En la fábrica se habla de tortitas toda la mañana, en voz baja, moviendo los labios.",
            Parrafo.PUEBLO to "En la calle, al salir, los niños corren con sartenes, tirando tortitas al aire para darles la vuelta, y la mitad caen al suelo y se las comen los perros. En algunos pueblos de alrededor juegan hoy un partido de pelota que dura todo el día, entre dos parroquias, sin reglas.",
            Parrafo.ANOCHECER to "Esta noche se hacen tortitas en el fogón, finas, con limón cuando hay y con azúcar cuando no. Cada uno da la vuelta a la suya tirándola al aire, y a {nombre} se le pega en el techo. Se ríen hasta que se cae.",
        ),
        "viernes_santo" to unDia(
            Parrafo.MANANA to "Hoy es Viernes Santo, y la fábrica está cerrada: es uno de los dos días del año en que no suena la campana. {nombre} se despierta a las cinco, por costumbre, y se queda en la cama oyendo la calle sin zuecos. Luego se vuelve a dormir, que es lo más parecido a un milagro.",
            Parrafo.PUEBLO to "Por las calles pasan los vendedores de bollos de cruz, calientes, gritando a uno el penique, a dos el penique. Mucha gente se va a pie a las afueras, a los prados, a ver algo verde, y vuelve por la noche con flores en las manos y barro en los zuecos.",
            Parrafo.GENTE to "Hoy toda la familia va a la iglesia por la mañana, con la ropa del domingo, y luego camina hasta el campo, más allá de las últimas fábricas, hasta donde se ve un prado con ovejas. Su padre se queda mirando las ovejas un rato largo, sin decir nada.",
        ),
        "whit" to unDia(
            Parrafo.MANANA to "Hoy es lunes de Pentecostés, el día del desfile de las escuelas dominicales, y media ciudad estrena ropa. Las niñas llevan vestidos blancos, los niños pantalones nuevos, y los vecinos les dan un penique al verlos pasar, por la ropa, que es la costumbre.",
            Parrafo.PUEBLO to "Las escuelas de las iglesias desfilan por el centro de la ciudad, cada una detrás de su estandarte de seda, con una banda de música delante y los niños en filas, cantando. Las calles se llenan de gente que mira. Los católicos desfilan el viernes, con los suyos.",
            Parrafo.GENTE to "Billy desfila con su escuela, peinado con agua y con una camisa limpia que le queda grande, porque era de otro. {nombre} lo ve pasar desde la acera y le grita su nombre, y Billy hace como que no la oye, muy serio, y luego se da la vuelta para saludarla.",
        ),
        "kersal" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las carreras de caballos de Kersal Moor, en el páramo de las afueras, y media ciudad va a verlas. Es la semana de Pentecostés, y en muchas fábricas se trabaja menos estos días. Los caminos al páramo se llenan de gente a pie desde el amanecer.",
                Parrafo.PUEBLO to "En el páramo hay casetas de lona con cerveza, puestos de pasteles, músicos, peleas de gallos, adivinas y ladrones de bolsillo. Los caballos corren entre dos filas de gente que grita, y pasan tan deprisa que la mayoría no los ve. Lo importante es haber estado.",
                Parrafo.PENSAMIENTO to "{nombre} mira a los ricos en sus coches, en lo alto de la colina, mirando los caballos con anteojos, y a los pobres abajo, en el barro, mirando a los ricos. Piensa que en las carreras todos miran algo que no es suyo, y que eso los iguala un poco.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días el páramo de las carreras se llena cada tarde, y las tabernas de la ciudad se vacían. Los predicadores de las capillas dicen que las carreras son cosa del diablo, y llevan a los niños de las escuelas a merendar al campo, lejos de los caballos.",
                "Mientras duran las carreras se apuesta en todas las esquinas, y se pierde lo que no se tiene. Hay quien vuelve a casa con dinero en el bolsillo y quien vuelve sin zuecos.",
                "El último día de las carreras se corre la más importante, y luego el páramo se queda lleno de basura, de barro pisado y de gente dormida en la hierba. Mañana la campana sonará igual.",
            )),
        ),
        "guy_fawkes" to unDia(
            Parrafo.MANANA to "Hoy es la noche de Guy Fawkes, el que quiso volar el Parlamento con pólvora hace más de doscientos años. Los niños llevan días pidiendo un penique para el muñeco por las calles, con un monigote de trapo relleno de paja sentado en una carretilla.",
            Parrafo.PUEBLO to "Al anochecer se encienden hogueras en los baldíos entre las casas, con todo lo que se ha podido juntar: tablas viejas, cajas, muebles rotos. Encima ponen al monigote, que arde entre gritos. Algunos tiran petardos, y los perros del barrio se esconden.",
            Parrafo.ANOCHECER to "Esta noche el cielo de Ancoats está rojo de hogueras, además de las fundiciones. {nombre} se queda junto a la del baldío, calentándose las manos, mirando las chispas subir. Alguien asa papas en las brasas y las reparte, quemando los dedos.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, el otro día del año en que la fábrica cierra. Las campanas de las iglesias tocan desde temprano y {nombre} se levanta cuando quiere, que hoy es tarde. Afuera, por una vez, la calle huele a algo que no es humo: huele a comida.",
            Parrafo.PUEBLO to "Por la noche pasaron los cantores de villancicos, de puerta en puerta, con un farol, y algunas casas les dieron algo caliente. Hoy, en las calles, la gente se saluda con una cortesía que no tiene el resto del año, y hasta el señor Holt, si se lo encuentran, se quita el sombrero.",
            Parrafo.ANOCHECER to "Esta noche hay en la mesa un trozo de carne asada, que se pagó a peniques durante todo el año en la carnicería, y un pudín de pasas hervido en un trapo. Se come despacio, para que dure. Después se canta. {nombre} piensa que, si todos los días fueran así, no sabría qué hacer con ellos.",
        ),
        "penique" to unDia(
            Parrafo.MANANA to "Hoy empieza el correo de un penique: desde hoy una carta cuesta lo mismo a cualquier parte del reino, y la paga el que la manda. Antes costaba tanto que los pobres no escribían. En la fábrica se habla de eso, y de a quién se podría escribir.",
            Parrafo.GENTE to "Su madre quiere escribir a su hermana, que vive en una aldea de Cheshire y a la que no ve desde hace doce años. Como en casa nadie sabe escribir bien, le paga dos peniques al maestro de la escuela dominical para que lo haga. {nombre} le dicta los saludos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ahora, con un penique, se puede mandar una carta a Londres, adonde no irá nunca. Le parece extraño que el papel viaje más que la gente. Luego piensa que, si aprende a escribir, podría escribir a alguien. No sabe todavía a quién.",
        ),
        "pabellon" to unDia(
            Parrafo.MANANA to "Hoy hay un gran banquete contra la ley del grano en un pabellón de madera que levantaron en el campo de San Pedro, donde caben miles de personas sentadas. Lo pagan los que quieren el pan barato, que son los dueños de las fábricas y también los obreros, por otros motivos.",
            Parrafo.PUEBLO to "El campo de San Pedro es donde hace veinte años los soldados a caballo cargaron contra una multitud que pedía el voto, y mataron a más de una docena. Su padre estaba allí. Hoy, en el mismo sitio, hay banderas, discursos y un pan enorme pintado en una pancarta.",
            Parrafo.GENTE to "Su padre no va al banquete, porque no le dejan entrar sin entrada, pero se queda afuera con otros, escuchando lo que se oye. Cuando vuelve a casa dice que los dueños quieren el pan barato para pagar jornales baratos. Su madre dice que, de todos modos, el pan barato se come.",
        ),
        "boda_reina" to unDia(
            Parrafo.MANANA to "Hoy se casa la reina, en Londres, con un príncipe alemán. En Manchester repican las campanas de todas las iglesias, y en algunas fábricas cuelgan banderas. La campana de la suya suena a la hora de siempre, sin banderas.",
            Parrafo.PUEBLO to "En el centro hay banderas en los balcones, y en las tabernas se brinda por la reina con cerveza. Se venden por las calles dibujos de la boda, que no se parecen a nada, y la gente los compra igual. La reina tiene veinte años, como las de la sala de hilar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la reina, que tiene casi su edad, vestida de blanco en una iglesia de Londres, y en ella misma, de gris, en la sala de hilar. Le gustaría saber si la reina se cansa. Seguramente sí, de otras cosas.",
        ),
        "conoce_jem" to unDia(
            Parrafo.MANANA to "Hoy, domingo, a la salida de la iglesia, un muchacho con las manos negras de grasa le preguntó a {nombre} si era la hermana de Billy, el de la escuela. Era una excusa, y los dos lo sabían. Se llama Jem y es mecánico en un taller de máquinas del otro lado del canal.",
            Parrafo.GENTE to "Jem la acompañó hasta la esquina de su calle hablando de engranajes, que es de lo que sabe. {nombre} no entendió nada y le pareció lo más interesante que había oído en años. Su madre la vio llegar desde la puerta, y no le preguntó nada, que es peor.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Jem todo el camino y toda la tarde, y luego se riñe por pensar en un muchacho que no sabe si volverá a ver. Pero sabe en qué iglesia está los domingos. Y sabe que las manos de un mecánico, por negras que estén, no huelen a algodón.",
        ),
        "vestido" to unDia(
            Parrafo.MANANA to "Hoy, con lo que ahorró penique a penique desde Navidad, {nombre} se compró en el mercado una tela de algodón estampada, azul con florecitas blancas, para hacerse un vestido para Pentecostés. Es la primera tela nueva que compra en su vida.",
            Parrafo.GENTE to "Su madre le ayuda a cortar el vestido sobre la mesa, con un patrón de papel de periódico, y Jane mira y opina. Las dos dicen que el azul le queda bien. {nombre} pasa la mano por la tela una y otra vez, como si fuera a irse.",
            Parrafo.ANOCHECER to "Esta noche cose el vestido a la luz de la vela, con puntadas pequeñas, hasta que se le cierran los ojos. Piensa que esa tela salió quizás de su propia fábrica, de su propia máquina, y que por fin una bobina de las suyas ha vuelto a casa.",
        ),
        "multa" to unDia(
            Parrafo.MANANA to "Hoy {nombre} llegó tarde: se le rompió la correa del zueco a dos calles y llegó corriendo descalza, con el zueco en la mano, justo cuando el portero cerraba la puerta. Esperó fuera media hora, hasta el desayuno, y después la dejaron entrar.",
            Parrafo.TRABAJO to "A esta hora el señor Holt apunta en su libreta la multa: un cuarto de día por llegar tarde y otro tanto por haber pedido explicaciones. {nombre} quería decir lo del zueco. No lo dice. Atiende su máquina con la cara roja, sin levantar la vista.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hoy ha trabajado el día entero para que le paguen medio, y que el dueño ni sabe que ella existe. Hace cuentas: el pan de dos días. Por un zueco. Esta noche le pedirá a su padre que se lo arregle, que de eso todavía sabe.",
        ),
        "billy_letras" to unDia(
            Parrafo.MANANA to "Hoy Billy volvió de la escuela dominical sabiendo todas las letras, y las escribió con un carbón en el suelo de la casa, de la A a la Z, sin equivocarse más que en dos. Su madre no sabe leer y las miró como se mira algo bonito que no se entiende.",
            Parrafo.GENTE to "Billy quiere enseñarle a leer a {nombre}, y ella se deja, aunque esté cansada, porque al niño se le ve tan contento. Empiezan por su nombre. Ella lo sabe firmar, pero nunca había pensado que cada raya fuera un sonido, y se queda mirándolo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su hermano va a leer, y que eso es como tener una ventana en una casa que no tiene ventana trasera. Piensa que ella también podría, los domingos, si no estuviera tan cansada. Quizás el domingo que viene.",
        ),
        "telar_vendido" to unDia(
            Parrafo.MANANA to "Hoy su padre vendió el telar de mano. Vino un hombre con una carretilla, lo desarmó en una hora y se lo llevó por piezas, para leña o para otro tejedor más tonto, dijo su padre. En la casa quedó un hueco en el suelo, más limpio que el resto, con la forma del telar.",
            Parrafo.GENTE to "Su padre se ha quedado sentado junto al hueco toda la tarde, con la lanzadera en la mano, la única pieza que no vendió. Tejió treinta años en ese telar. Su madre le ha puesto delante una taza de té y no le ha dicho nada, y él tampoco.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que las máquinas que ella atiende son las que le quitaron el oficio a su padre, y que el jornal de ella es ahora el que paga la casa. No sabe si sentirse culpable o mayor. Se siente las dos cosas, y cansada.",
        ),
        "niebla_humo" to unDia(
            Parrafo.MANANA to "Hoy amaneció una niebla de humo tan espesa que desde la puerta no se veía la fábrica de enfrente, que es enorme. {nombre} fue al trabajo tocando las paredes, siguiendo el ruido de los zuecos de las demás, con el chal sobre la boca.",
            Parrafo.PUEBLO to "La niebla es amarilla y huele a azufre, y se mete por las rendijas de las casas y de la fábrica. Los coches de caballos van al paso, con un hombre delante con un farol. En la sala de hilar las lámparas de gas están encendidas a mediodía, y aun así no se ve el final de la sala.",
            Parrafo.ANOCHECER to "Esta noche todo el barrio tose, de casa en casa, como si se contestaran. {nombre} tose también y escupe negro. Su madre le da agua caliente con un poco de miel que guarda para estas noches. Afuera, la niebla no se mueve.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "manchester", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + PARO.take(2) + EN_CASA.take(1),
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + PARO.subList(2, 5) + EN_CASA.subList(1, 4),
            Parrafo.PUEBLO to PUEBLO + PARO[5],
            Parrafo.GENTE to GENTE + v("En casa, sin fábrica, están todos a la vez y no caben: su padre sentado junto al fuego apagado, su madre lavando lo poco que le dan, Jane y Billy sin saber qué hacer con un día entero. Billy pregunta si se puede ir a jugar. Se puede: es lo único que sobra.", estado = "paro"),
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + PARO[6] + EN_CASA[4],
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS,
        especiales = ESPECIALES,
    )
}
