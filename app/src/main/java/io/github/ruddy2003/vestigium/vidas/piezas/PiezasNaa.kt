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
 * El cuento de Naa, cazadora y recolectora en una sabana de África oriental, hace unos 15,000 años. Sigue la gramática
 * y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Vive antes de la escritura, los metales y los perros. El «dato de su época» de cada párrafo sale de lo que se sabe
 *   de los cazadores y recolectores de hoy (los hadza de Tanzania, los san) y de la sabana: el palo de cavar endurecido
 *   al fuego, las cáscaras de huevo de avestruz para el agua, el pájaro que guía a la miel, las flechas envenenadas,
 *   la comida que se reparte. Es una analogía, como dice `fuentes.md`.
 * - Nada de palabras que no existían para ella: ni días de la semana, ni meses, ni números grandes. El tiempo se cuenta
 *   en lunas, lluvias y secas.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG1.kt`):
 * - Sese, su madre, muere en la temporada 3 (`muere_sese`): sus variantes llevan `hasta`.
 * - Mai nace en la temporada 2 (`nace_mai`); el embarazo va de `espera` (temporada 1) a `nace_mai`.
 * - Iru, su hermana, se va a otro campamento en la temporada 2 (`se_va_iru`) y vuelve en la 3 (`vuelve_iru`).
 * - Ekwa está herido del 20 de marzo al 18 de mayo de la temporada 4: las fiestas que vuelven cada año no lo nombran.
 * - Las fiestas que vuelven cada año no nombran a Sese, a Mai ni a Iru.
 */
internal object PiezasNaa {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta con el frío que llega justo antes del sol, el único frío del día. Sopla las brasas que guardó la ceniza y les echa ramitas hasta que el fuego vuelve a hablar. Alrededor, el campamento todavía es un montón de bultos que respiran."),
        v("La luz llega primero a la copa del baobab y luego baja por el tronco, despacio, como si no quisiera despertar a nadie. {nombre} ya está sentada junto al fuego, con los ojos medio cerrados, escuchando a los pájaros que se pelean por el primer sol."),
        v("La despierta Tiko, que le pone los pies fríos en la espalda, como todas las mañanas. {nombre} se los aparta sin abrir los ojos, y él se los vuelve a poner. Así hasta que el sol asoma por encima de las colinas y ya no queda más remedio que levantarse."),
        v("Antes de que salga el sol se oye a los hombres afuera, hablando bajo de huellas y de vientos. {nombre} sale de la choza de hierba, que se hizo en una tarde y aguanta lo que tiene que aguantar, y se estira hasta que le suena la espalda."),
        v("La mañana empieza con agua: {nombre} bebe de la cáscara de huevo de avestruz que llenó anoche y se moja la cara con lo que le queda en la mano. El agua de ayer sabe a ayer, pero es agua. Hasta el arroyo hay un buen trecho, y nadie va a oscuras."),
        v("Lo primero que hace al despertar es mirar el cielo por el hueco de la choza, para saber cómo va a ser el día. Lo segundo, contar a los niños. Están todos: Lali enroscada como un antílope pequeño, Tiko con la boca abierta. Entonces se levanta."),
        v("Sese ya está despierta cuando {nombre} abre los ojos, porque los viejos duermen poco y miran mucho. Está sentada junto al fuego, sacándole brasas con un palito, y le dice sin mirarla qué tiempo va a hacer. Casi siempre acierta.", hasta = "muere_sese"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el campamento el tiempo no se comenta: se camina con él o contra él, y quien camina contra él vuelve con la cesta vacía."),
        v("{nombre} huele el aire antes de mirar el cielo, como le enseñó su madre. {cielo} Del cielo depende hacia dónde se va hoy, qué se va a encontrar y cuánto habrá que cargar de vuelta."),
        v("{de_estacion} {cielo} Los viejos dicen que antes las lluvias eran más cortas, y los niños se lo creen porque no tienen con qué comparar. {nombre} tampoco tiene, y no se lo cree."),
        v("{cielo} En tiempo de {estacion} la sabana cambia de color de un día para otro, y quien no la mira no la reconoce. {nombre} la mira todos los días, por si acaso."),
        v("Lo primero que mira al salir es la línea de las colinas: si se ve clara, el día será largo. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los animales lo saben antes que la gente, y la gente que sabe mira a los animales. {nombre} mira sobre todo a los pájaros, que casi nunca se equivocan."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y alto, y el sol pega desde temprano.",
            "No hay una nube, y las colinas lejanas tiemblan con el calor.",
            "Hace un día claro, de los que dejan ver a los animales desde muy lejos.",
        ),
        Clima.NUBES to listOf(
            "Hay nubes grandes que pasan despacio y dejan sombras que corren por el pasto.",
            "El cielo está cubierto, y la luz es pareja, sin sombras.",
            "Las nubes se juntan sobre las colinas sin decidirse a llover.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia tibia que pega el pelo a la cara.",
            "Cae agua desde antes del amanecer, y la tierra roja se vuelve barro.",
            "Llueve a ratos, y entre un aguacero y otro todo huele a tierra mojada.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: el trueno rueda por la llanura y los niños se meten en las chozas.",
            "El cielo se ha puesto negro sobre el lago y caen relámpagos lejos, sin ruido todavía.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento seco que trae olor a hierba quemada.",
            "Hay viento, y el pasto alto se dobla todo hacia el mismo lado.",
        ),
        Clima.POLVO to listOf(
            "El viento levanta polvo rojo, y el sol se ve pálido como una cáscara de huevo.",
            "Hay tanto polvo en el aire que el otro lado del lago desaparece.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.LLUVIAS to listOf(
            "Es tiempo de lluvias, cuando la sabana se pone verde de golpe y hay comida en todas partes.",
            "En las lluvias el campamento se aparta del lago y los animales se dispersan por la llanura.",
            "Es tiempo de lluvias, y las abejas trabajan más que nadie.",
        ),
        Estacion.SECAS to listOf(
            "Es tiempo de secas, cuando el pasto se pone amarillo y los animales se juntan cerca del agua.",
            "En las secas los animales se acercan al agua, y la gente detrás de ellos.",
            "Es tiempo de secas, y el agua hay que ir a buscarla cada día más lejos.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora cava. Va con las otras mujeres al terreno donde crecen los tubérculos y saca la raíz con el palo de cavar, endurecido al fuego. Hay que cavar hondo, a veces hasta el codo. Mientras cavan, hablan, y lo que se dice ahí abajo no se repite en el campamento.", hacer = Hacer.TRABAJO),
        v("A esta hora junta. Lleva la cesta a la cadera y va tocando las ramas, mirando qué está maduro y qué no. Las hojas tiernas, los frutos, las semillas: cada cosa tiene su sitio y su tiempo, y {nombre} los sabe como sabe el nombre de sus hijos.", hacer = Hacer.TRABAJO),
        v("A esta hora prepara la comida. Asa los tubérculos enterrados en la brasa, les quita la piel quemada y los parte para que se enfríen. Lo que no se come hoy se reparte, porque la comida que se guarda se echa a perder y la que se da, vuelve.", hacer = Hacer.TRABAJO),
        v("A esta hora muele. Con una piedra plana y otra redonda aplasta semillas y pulpa seca hasta hacerlas polvo. Es un ruido que se oye en todo el campamento, y que los niños imitan con la boca. Las manos de {nombre} saben hacerlo solas.", hacer = Hacer.TRABAJO),
        v("A esta hora camina detrás de las otras mujeres, con un hombre armado delante por si sale algún animal. Las mujeres van juntas y hablan fuerte a propósito: así los leones las oyen venir y se apartan. Es el único momento en que hacer ruido es prudente.", hacer = Hacer.TRABAJO),

        v("A esta hora cuida el fuego y a los niños, que es casi lo mismo: los dos se escapan si una se distrae. Echa una rama, regaña a Tiko, echa otra. Las brasas de la noche tienen que durar, porque prender un fuego nuevo con el taladro cuesta un buen rato.", hacer = Hacer.CUIDAR),
        v("A esta hora peina a Lali con los dedos y le saca las espinas que trajo ayer en el pelo. La niña protesta, y {nombre} le cuenta una historia para que se esté quieta. La historia es siempre la misma, y la niña siempre quiere saber cómo termina.", hacer = Hacer.CUIDAR),
        v("A esta hora reparte el agua de la cáscara de avestruz entre los niños, un trago cada uno, y tapa el agujero con hierba. Después sacude la piel con la que duermen, que amaneció llena de tierra. Nadie le da las gracias, y nadie espera que se las dé.", hacer = Hacer.CUIDAR),

        v("A esta hora descansa a la sombra, que en la sabana es una cosa que se busca y se defiende. Se tumba con las otras mujeres bajo un árbol y deja que el calor pase por encima. Se habla de quién tiene hambre, de quién mira a quién, de nada.", hacer = Hacer.LIBRE),
        v("A esta hora no hace nada, y lo hace bien. El sol está arriba y lo aplasta todo, hasta a los animales, que se quedan quietos donde pueden. {nombre} se trenza una pulsera de fibra sin mirar, para que las manos tampoco se aburran.", hacer = Hacer.LIBRE),
        v("A esta hora duerme un rato a la sombra, con la cabeza sobre el brazo. Despierta cuando la sombra se ha ido y el sol le llega a los pies, y se arrastra un poco más allá. Así se pasa el mediodía: siguiendo a la sombra como quien sigue a un animal.", hacer = Hacer.LIBRE),

        v("A esta hora come lo que se trajo hoy: tubérculos asados, un puñado de frutos, algo de carne si los hombres tuvieron suerte. Cada familia come junto a su fuego, pero los pedazos van y vienen de un fuego a otro hasta que todo el mundo ha comido.", hacer = Hacer.COMER),
        v("A esta hora come y reparte. La regla no está dicha, pero todos la saben: quien trae comida la da, y quien no da se queda solo. {nombre} le pasa un trozo a una vieja que hoy no salió, sin decir nada, y la vieja lo toma sin decir nada.", hacer = Hacer.COMER),
        v("A esta hora come despacio, chupando la fibra de la raíz hasta dejarla blanca. Los niños ya terminaron y se pelean por el último hueso. A {nombre} le gusta comer cuando ellos ya acabaron: es la única manera de comer sin que nadie le pida.", hacer = Hacer.COMER),

        v("A esta hora está junto al fuego grande, donde se juntan todos al caer la noche. Se cuenta lo que se vio en el día: un rastro de búfalo, un panal en un árbol hueco, un león echado. Lo que se cuenta bien se vuelve a contar mañana, un poco más grande.", hacer = Hacer.GENTE),
        v("A esta hora canta con las otras mujeres, un canto sin palabras que sube y baja como el viento en el pasto. Los hombres contestan desde el otro lado del fuego. Los niños se duermen en medio, sin enterarse de quién empezó.", hacer = Hacer.GENTE),
        v("A esta hora escucha. Alguien cuenta otra vez la caza de aquel antílope grande, y cada vez el antílope es más grande. {nombre} se ríe en los mismos sitios que la vez pasada, porque así se escucha una historia que ya se sabe.", hacer = Hacer.GENTE),

        v("A esta hora duerme en la choza de hierba, pegada a los niños, con una piel encima. La noche está llena de ruidos que conoce: la hiena lejos, el búho cerca, el viento en la hierba. Los ruidos que no conoce la despiertan, y se queda escuchando hasta que se van.", hacer = Hacer.DORMIR),
        v("A esta hora duerme a ratos. Alguien se levanta a echar leña, alguien tose, un niño llora y se calla. En el campamento siempre hay alguien despierto, y por eso los demás pueden dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme con la mano sobre Tiko, que se mueve como un pez. Fuera, el fuego se ha vuelto brasa, y la brasa alumbra apenas las caras de los que todavía hablan bajo.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El campamento son unas cuantas chozas de hierba en semicírculo, con un fuego delante de cada una. Se hacen en una tarde y se dejan sin pena: cuando la comida se acaba, se va uno a otra parte. {entorno}"),
        v("{entorno} Desde la loma se ve el lago, blanco de sal en las orillas, y alrededor la llanura que no se acaba. Los flamencos están en el agua como una mancha rosa que se mueve."),
        v("Alrededor del campamento la tierra está llena de señales: huellas, excrementos, ramas rotas, plumas. Para quien sabe leerlas, la sabana habla todo el tiempo. {entorno}"),
        v("El baobab grande está donde siempre, más viejo que cualquier historia. Los hombres le clavan palos en el tronco para subir a buscar miel y fruta, y de lejos parece un árbol con escalera. {entorno}"),
        v("{entorno} En el campamento viven unas treinta personas, y todas se conocen desde siempre. Cuando dos se pelean, uno de los dos se va con otro grupo una temporada, y así se arregla casi todo."),
        v("En la llanura pastan cebras, ñus y antílopes, tan juntos que de lejos parecen un solo animal. Los leones los miran desde la hierba alta, y la gente mira a los leones. {entorno}"),
    )

    /** Lo que se ve alrededor según el lago (que sube cada año) y el pasto. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("lago", 0.85, frases = listOf(
            "El lago está más grande que nunca: hay agua donde los viejos cruzaban a pie.",
            "El lago llega hasta los árboles de la orilla, y algunos tienen los pies en el agua.",
        )),
        Entorno("lago", 0.0, 0.2, frases = listOf(
            "El lago está casi seco: en medio queda una costra blanca que cruje al pisarla.",
            "Del lago queda poco más que un charco grande, y la gente lo cruza a pie.",
        )),
        Entorno("pasto", 0.8, frases = listOf(
            "El pasto está alto y verde, y los niños desaparecen en él.",
            "Todo está verde, y hay flores pequeñas donde ayer había polvo.",
        )),
        Entorno("pasto", 0.0, 0.3, frases = listOf(
            "El pasto está amarillo y seco, y cruje bajo los pies.",
            "La llanura está parda, y el polvo se levanta con cada paso.",
        )),
        Entorno("lago", 0.2, 0.85, frases = listOf(
            "El lago tiene su agua de siempre, o un poco más.",
            "En el lago hay agua, y en el agua, flamencos.",
        )),
    )

    private val GENTE = listOf(
        v("Ekwa, su compañero, sale a cazar casi todos los días, y casi todos los días vuelve con nada. Cuando vuelve con algo, el campamento entero lo sabe antes de que hable. Él cuenta igual lo que no cazó que lo que cazó, y {nombre} lo prefiere así."),
        v("Lali anda siempre detrás de las mujeres grandes, imitando cómo caminan y cómo cavan. Tiene un palo de cavar a su medida que le hizo su padre, y lo cuida más que a nada. Cuando encuentra algo, se lo enseña a {nombre} con las dos manos."),
        v("Tiko tiene la edad de querer ir a todas partes y llorar en cuanto llega. Sigue a los niños mayores, que lo dejan seguirlos para tener a quién mandar. {nombre} lo pierde de vista diez veces al día y lo encuentra diez veces, siempre lleno de tierra."),
        v("El viejo Bako ya no caza, pero sabe dónde están los animales mejor que los que cazan. Se pasa el día sentado, enderezando flechas al fuego y diciéndoles a los jóvenes por dónde ir. Los jóvenes van por otro lado, y vuelven sin nada."),
        v("En el campamento nadie manda, y eso hay que trabajarlo todos los días. Si alguien se cree más que otro, los demás se burlan de él hasta que se le pasa. {nombre} se ha reído de más de uno, y de ella también se han reído."),
        v("Su madre, Sese, es la más vieja del campamento y la que más trabaja: cava, muele y cuida a los niños de todos. Dice que si se sienta, se muere. {nombre} la regaña para que descanse, y Sese hace como que no la oye.", hasta = "muere_sese"),
        v("Su hermana Iru tiene el fuego al lado del suyo, y entre las dos se reparten los niños, la leña y las quejas. Iru habla más, {nombre} escucha más, y así se entienden.", hasta = "se_va_iru"),
        v("Iru volvió con un niño nuevo y con historias del otro campamento, que según ella come mejor y canta peor. Ahora su fuego está otra vez junto al de {nombre}, y las dos hacen como que nunca se pelearon con nadie.", desde = "vuelve_iru"),
        v("{nombre} lleva otro hijo dentro, y las mujeres ya le dejan las cargas más ligeras sin que se lo pida. Ella las agarra igual. Sese le dice que esta vez será niña, porque lo sabe por la forma de la barriga. Sese siempre dice lo mismo.", desde = "espera", hasta = "nace_mai"),
        v("Mai va colgada a la espalda de {nombre} en una piel, todo el día, y mira el mundo por encima del hombro de su madre. Cuando {nombre} cava, Mai duerme; cuando para, Mai despierta. Parece que lo hace a propósito.", desde = "nace_mai"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas que se usan: con las manos antes que con la cabeza."),
        v("Hay cosas que solo se miran cuando se pierden. {dato_objeto}"),
        v("{dato_objeto} En el campamento cada uno tiene pocas cosas, y todas caben en la espalda."),
        v("{dato_objeto} Son cosas que se aprenden mirando a las mayores, como casi todo en el campamento."),
        v("Quien se muda cada pocas lunas no guarda lo que no sirve. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("palo_cavar", "el palo de cavar", "un palo de cavar", "El palo de cavar",
            "Su palo de cavar es de madera dura, con la punta afilada y endurecida al fuego; lo hizo ella misma, y cuando se gasta lo vuelve a afilar con una piedra, así que cada año es un poco más corto."),
        ObjetoCuento("cesta", "la cesta", "una cesta", "La cesta",
            "Su cesta es de hierba trenzada, ancha de boca y con una correa de piel que se apoya en la frente; carga en ella raíces, frutos y, a veces, un niño dormido encima de todo."),
        ObjetoCuento("piel", "la piel de antílope", "una piel de antílope", "La piel",
            "La piel de antílope le sirve de manta, de bolsa y de cuna; está curtida con grasa y con humo, huele a fuego viejo y es lo único que se pone encima cuando hace frío."),
        ObjetoCuento("calabaza", "la calabaza", "una calabaza", "La calabaza",
            "La calabaza seca, con un tapón de hierba, guarda agua o miel; crecen solas donde alguien tiró las semillas, y nadie sabe ya quién fue el primero en tirarlas."),
        ObjetoCuento("collar", "el collar", "un collar", "El collar",
            "Su collar es de cuentas hechas con cáscara de huevo de avestruz, cada una agujereada y pulida a mano; tardó una estación entera en hacerlo, y piensa dárselo a Lali cuando sea mujer."),
        ObjetoCuento("ocre", "el ocre", "un trozo de ocre", "El ocre",
            "El ocre es una piedra roja que se muele y se mezcla con grasa; con él se pintan la piel para los bailes y para el sol, y sirve también para curtir."),
        ObjetoCuento("taladro_fuego", "el taladro de fuego", "un taladro de fuego", "El taladro de fuego",
            "El taladro de fuego son dos palos: uno se apoya en el suelo y el otro se hace girar entre las palmas hasta que sale humo; prender un fuego así cuesta, y por eso las brasas se cuidan como a un niño."),
        ObjetoCuento("panal", "el panal", "un panal", "El panal",
            "La miel del panal es lo más dulce que conoce; los hombres la sacan del árbol con humo, y la cera que sobra la esconden, para que el pájaro que los guió siga con hambre y vuelva a guiarlos."),
        ObjetoCuento("fruto_baobab", "el fruto del baobab", "un fruto de baobab", "El fruto del baobab",
            "El fruto del baobab tiene la cáscara dura como madera y por dentro una pulpa blanca que se deshace en polvo; mezclada con agua es casi una comida entera, y sabe agrio."),
        ObjetoCuento("huevo_avestruz", "la cáscara de huevo de avestruz", "una cáscara de huevo de avestruz", "La cáscara de avestruz",
            "Una cáscara de huevo de avestruz, con un agujero tapado con hierba, guarda agua para todo un día; en las secas se entierran llenas en los caminos, para encontrarlas a la vuelta."),
        ObjetoCuento("cuchillo_piedra", "el cuchillo de piedra", "un cuchillo de piedra", "El cuchillo de piedra",
            "El cuchillo es una lasca de piedra con el filo más fino que una hoja; corta carne, piel y fibra, y cuando se mella se tira y se saca otra de la misma piedra."),
        ObjetoCuento("arco", "el arco", "un arco", "El arco",
            "El arco de Ekwa es una rama flexible con cuerda de tendón; sus flechas buenas llevan veneno en la punta, y los niños aprenden muy pronto a no tocarlas."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el agua como otros piensan en la comida: dónde está, cuánta queda, cuánto falta para que vuelva. A sus {edad} años ha visto lagos que se van y lagos que vuelven, y ya no se fía de ninguno."),
        v("Piensa a veces en lo que hay más allá de las colinas, donde nunca ha ido. Dicen que hay más gente, que hablan raro y que comen cosas que aquí no se comen. Le da curiosidad y un poco de miedo, y casi siempre gana el miedo."),
        v("{mundo} Ella piensa en cosas más cercanas: en la raíz que no encontró, en el pie de Tiko, que tiene una espina, en si Ekwa volverá con carne. Las cosas grandes, dicen los viejos, son para los que no tienen que cavar."),
        v("{mundo} A sus {edad} años, {nombre} ya ha enterrado a dos hijos que no llegaron a caminar. No habla de ellos, pero cuando pasa por donde nacieron se queda callada un rato. Los demás la dejan: todos tienen algún sitio así."),
        v("No tiene nada que no pueda cargar, y no echa nada de menos. Tiene sus manos, sus hijos y un fuego que alguien le presta si se le apaga el suyo. Hay días en que eso le parece mucho, y días en que le parece todo."),
        v("{mundo} Por las noches mira las estrellas y se acuerda de las historias que le contaron de ellas, que son muchas y no coinciden. A {nombre} le gusta que no coincidan: así cada uno puede creer la que más le guste."),
    )

    private val MUNDOS = mapOf(
        "lluvias_vuelven" to listOf(
            "Los viejos dicen que las lluvias ya no son las de antes: ahora duran más.",
            "Se dice que el lago crece un poco cada año, y nadie sabe por qué.",
            "Hay quien dice que el agua viene a quedarse, y quien dice que se irá como vino.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando el sol baja, el campamento se junta alrededor de los fuegos y el humo se queda flotando entre las chozas. {nombre} acomoda a los niños en la piel y les cuenta otra vez la historia del pájaro que guía a la miel. Se duermen antes del final."),
        v("Al caer la tarde vuelven los hombres, uno por uno, con lo que traen o sin nada. Se nota de lejos cuál trae algo: camina más despacio, para que lo vean. {nombre} los mira llegar desde el fuego, sin levantarse."),
        v("Anochece deprisa, como siempre cerca del lago: un momento hay luz y al siguiente ya no. Las hienas empiezan a reírse a lo lejos. {nombre} echa al fuego una rama gruesa, de las que duran toda la noche, y se sienta a mirarla arder."),
        v("De noche la sabana es de otros: de los leones, de las hienas, de cosas que no tienen nombre. Por eso los fuegos no se apagan y siempre hay alguien despierto. {nombre} se acuesta tranquila, porque esta noche no le toca a ella."),
        v("Se cena lo que quedó, frío, y se habla bajo. Las estrellas salen todas de golpe, tantas que no cabe una más. {nombre} se tumba boca arriba, con Tiko dormido sobre la barriga, y se queda mirándolas hasta que se le cierran los ojos."),
        v("La última luz se queda un rato en el agua del lago, roja, cuando el campamento ya está a oscuras. {nombre} la mira desde la puerta de la choza. Luego se mete adentro, entre el calor de los niños, y el día se acaba."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "crias" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy, en la llanura del otro lado, empiezan a nacer las crías de los ñus. Desde la loma se ve: entre los animales grandes aparecen otros chiquitos, mojados, que al poco rato ya están de pie. Las hienas también lo han visto.",
                Parrafo.PUEBLO to "En pocos días nacen tantas crías que la llanura parece moverse sola. Los leones y las hienas comen hasta cansarse, y los hombres del campamento salen detrás de ellos a quitarles lo que dejan. Hay carne para todos, y nadie tiene que pelearla mucho.",
                Parrafo.PENSAMIENTO to "{nombre} mira a una madre ñu que lame a su cría hasta que se levanta, y le parece que ese animal tonto sabe lo mismo que ella. Luego un león se lleva a otra cría, y la madre se queda mirando el sitio vacío. {nombre} aparta la vista.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días la llanura está llena de crías que corren detrás de sus madres sin saber todavía hacia dónde. Los buitres dan vueltas en el cielo, altos, esperando. En el campamento se come carne cada noche, y los niños tienen la cara brillante de grasa.",
                "Las manadas no se quedan: comen el pasto corto y siguen andando hacia donde llovió. Detrás dejan la tierra pisada y llena de huellas, que los cazadores leen como quien escucha una historia larga.",
                "De noche se oye a las hienas pelearse por lo que dejaron los leones. Es un ruido feo, como de risa, que no deja dormir a los niños. Los viejos dicen que se ríen de los que mueren, y los niños se lo creen.",
            )),
        ),
        "lluvias_largas" to unDia(
            Parrafo.MANANA to "Hoy llegan las lluvias largas. Desde antes del amanecer el aire huele distinto, pesado, y los pájaros vuelan bajo. A media mañana el cielo se oscurece sobre las colinas y avanza hacia el campamento como una manada.",
            Parrafo.TIEMPO to "El primer aguacero cae de golpe, con gotas gordas que levantan polvo antes de mojarlo. Los niños salen corriendo a bañarse en él, y las mujeres ponen las cáscaras de huevo y las calabazas a llenarse. En un momento, toda la sabana huele a tierra.",
            Parrafo.ANOCHECER to "Esta noche se duerme apretado dentro de las chozas, con el agua golpeando la hierba del techo. Por las rendijas entra un fresco que no se sentía desde hace muchas lunas. {nombre} se queda despierta escuchando, y le parece la mejor música que hay.",
        ),
        "miel" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy los hombres vuelven a oír al pájaro de la miel, que llama desde las ramas con su voz de piedrecitas. Es el tiempo de la miel. Uno de ellos le contesta con un silbido, y el pájaro vuela un trecho, se para y lo espera.",
                Parrafo.PUEBLO to "En el tiempo de la miel los hombres siguen al pájaro hasta los árboles huecos donde viven las abejas. Les echan humo con un tizón para atontarlas, abren el tronco a golpes y sacan los panales chorreando. Lo que se comen allí mismo no se cuenta.",
                Parrafo.GENTE to "Uno de los hombres vuelve con la boca pegajosa y un panal envuelto en hojas, y los niños se le echan encima como moscas. {nombre} reparte la miel con los dedos, un poco para cada uno, y se chupa los suyos al final, despacio, porque es lo mejor del día.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días cada hombre que vuelve trae la boca pegajosa y alguna picadura. La miel se come con cera, con larvas y con todo, y los niños mastican la cera hasta que no sabe a nada.",
                "El pájaro de la miel anda de árbol en árbol, llamando. Los hombres no le dejan la cera en el sitio: la esconden, para que siga con hambre y vuelva a buscarlos mañana. El pájaro, que no es tonto, vuelve igual.",
                "Con la miel, el campamento anda contento y perezoso. Se canta más, se pelea menos, y los viejos dicen que así deberían ser todos los días, y luego se quejan de que les duelen las muelas.",
            )),
        ),
        "baobab" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy el baobab grande amaneció sin hojas: las tiró todas, como cada seca, y se quedó con las ramas al aire, como raíces puestas al revés. De ellas cuelgan los frutos, largos y peludos, que ya están secos y suenan si se sacuden.",
                Parrafo.PUEBLO to "Los hombres suben al baobab por los palos clavados en el tronco y tiran los frutos abajo, donde los niños los recogen corriendo. Se rompen contra una piedra, y por dentro tienen una pulpa blanca que se deshace en polvo. Con agua, es comida para un día.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que el baobab ya era viejo cuando Bako era niño, y que lo será cuando Tiko sea viejo. Dicen que los árboles así guardan dentro el agua de muchas lluvias. Ella lo toca al pasar, por si se le pega algo de esa paciencia.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días el campamento come baobab de todas las maneras: la pulpa en polvo con agua, las semillas tostadas, las hojas que quedan, secas y molidas. Los niños tienen la boca blanca y agria todo el día.",
                "La sabana se pone parda y los animales empiezan a acercarse al lago. Los baobabs, sin hojas, se ven desde muy lejos y sirven para orientarse: cada uno tiene su nombre, y su historia.",
                "Las cáscaras vacías del fruto se guardan: sirven de cuenco, de juguete y de tambor. Al final de estos días hay cáscaras por todo el campamento, y alguien siempre se tropieza con una a oscuras.",
            )),
        ),
        "noche_sin_luna" to unDia(
            Parrafo.PUEBLO to "Hoy no habrá luna, y todos lo saben desde hace días: la han visto adelgazar hasta desaparecer. Esta noche se baila. Los hombres sacan de sus bolsas las plumas negras de avestruz, los cascabeles para el tobillo y las capas oscuras.",
            Parrafo.GENTE to "Las mujeres se sientan aparte, donde no se las ve, y empiezan a cantar en la oscuridad. Uno a uno, los hombres salen de detrás de un árbol y bailan golpeando el suelo con el pie derecho, y cada uno le dedica su baile a alguien: a un hijo, a una mujer, a un amigo.",
            Parrafo.ANOCHECER to "Esta noche los fuegos se apagan y el campamento queda más negro que el cielo. Solo se oyen el canto, los cascabeles y el golpe de los pies en la tierra. Cuando las mujeres se levantan y entran al baile, {nombre} va con ellas, y por un rato no sabe dónde termina ella y dónde empieza el canto.",
        ),
        "aguadas" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Desde hoy los hombres pasan las noches fuera, tumbados junto a las aguadas, esperando a los animales que bajan a beber a oscuras. Salen al caer la tarde con el arco y las flechas envenenadas, y vuelven al amanecer, con frío y casi siempre sin nada.",
                Parrafo.PUEBLO to "En las secas los animales no tienen más remedio que venir al agua, y el agua es poca. Alrededor de cada charco la tierra está pisoteada y llena de huellas de todas las formas. Quien sabe esperar, come.",
                Parrafo.ANOCHECER to "Esta noche el campamento tiene menos hombres y más fuego: las mujeres echan más leña, por si acaso. {nombre} oye a lo lejos un animal grande que bebe y otro que no se decide. Piensa en los que esperan junto al agua, quietos, y se le enfría la espalda.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "Esta noche los hombres vuelven a salir hacia las aguadas, en silencio, con el arco a la espalda. Las mujeres se quedan con los niños y con el fuego. Alguien cuenta que una vez un león y un cazador esperaron toda la noche junto al mismo charco sin verse.",
                "Al caer la tarde se oye a los animales que se acercan al agua: el resoplido de las cebras, el paso pesado de algo más grande. En el campamento se habla bajo, como si los animales pudieran oír desde allá.",
                "De noche, cuando un cazador vuelve con algo, se le oye antes de verlo: los demás le gritan desde lejos y el campamento entero se despierta. Esta noche no vuelve nadie, y el silencio también se oye.",
            )),
        ),
        "lago_seco" to unDia(
            Parrafo.MANANA to "Hoy el lago terminó de secarse. Donde había agua queda una costra blanca de sal que cruje al pisarla, y en medio, un charco que los flamencos se disputan. Ya se puede cruzar a pie a la otra orilla, como en todas las secas.",
            Parrafo.PUEBLO to "Cruzar el lago seco ahorra días de camino, y por eso hoy pasa mucha gente: grupos de otros campamentos, cargados, con los niños a la espalda. Se saludan desde lejos y algunos se paran a hablar. Así se sabe quién nació, quién murió y dónde hay comida.",
            Parrafo.GENTE to "Los niños corren por el fondo del lago, donde nunca se puede correr, y vuelven con los pies blancos. Lali encuentra una concha y la trae como si fuera un tesoro. {nombre} le dice que ahí antes había agua, y la niña no se lo cree.",
        ),
        "lluvias_cortas" to unDia(
            Parrafo.MANANA to "Hoy vuelven las lluvias cortas. Llevan muchas lunas sin caer, y la tierra está tan seca que el agua, al principio, no entra: se queda encima, en bolitas de polvo. Luego entra, y la sabana entera suspira.",
            Parrafo.TIEMPO to "Estas lluvias son menos que las grandes, pero llegan cuando más falta hacen. En pocos días el pasto rebrota verde entre lo seco, y los animales se van del lago a buscarlo. El campamento también se moverá pronto: el agua ya no está en un solo sitio.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el año es así: llueve, se seca, vuelve a llover. Nadie lo decide y nadie lo arregla. Lo único que se puede hacer es estar donde haya agua cuando haga falta, y ella lleva toda la vida haciéndolo bien.",
        ),
        "bayas" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza el tiempo de las bayas. Los arbustos de las colinas están cargados de frutitos rojos y dulces, y de todas partes llega gente a comerlos. El campamento crece de un día para otro: llegan primos, conocidos y desconocidos.",
                Parrafo.PUEBLO to "En el tiempo de las bayas se juntan más de cien personas en un mismo sitio, y el campamento se vuelve casi un pueblo. Hay más fuegos, más ruido, más peleas y más parejas nuevas. Cuando se acaben las bayas, cada grupo volverá a irse por su lado.",
                Parrafo.GENTE to "Las mujeres salen juntas a las colinas, todas a la vez, con las cestas, y vuelven con los dedos rojos y la boca roja: se come más de lo que se trae. {nombre} se encuentra con mujeres que no veía desde las bayas del año pasado, y se ponen al día sin dejar de comer.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días el campamento grande no duerme nunca del todo: siempre hay un fuego donde alguien canta. Los jóvenes de un grupo miran a los del otro, y los viejos miran a los jóvenes. Así empiezan muchas cosas que se ven al año siguiente.",
                "Las bayas se comen enteras, con semilla y todo, y por los caminos aparecen semillas por todas partes. Los viejos dicen que así nacen los arbustos nuevos, y que la gente siembra sin saberlo.",
                "Con tanta gente junta hay que ir cada día más lejos a buscar bayas, leña y agua. Las mujeres salen más temprano y vuelven más tarde. Pronto alguien dirá que ya no queda nada, y el campamento grande empezará a deshacerse.",
            )),
        ),
        "palo_lali" to unDia(
            Parrafo.MANANA to "Hoy Lali sale con las mujeres y con su palo de cavar, el pequeño, que su padre le endureció al fuego. Va delante de todas, como si supiera adónde. {nombre} la deja ir delante: hoy no la va a corregir.",
            Parrafo.TRABAJO to "A esta hora Lali está metida hasta el codo en un hoyo, sacando tierra con el palo y con las uñas. Tarda tanto que las otras mujeres ya tienen la cesta llena. Al final saca la raíz entera, sin romperla, y se queda mirándola como si la hubiera hecho ella.",
            Parrafo.GENTE to "Al volver, Lali le enseña su raíz a todo el campamento, uno por uno, y todos le dicen que es la mejor raíz que han visto. Ekwa la asa en la brasa y le da a Lali el primer pedazo. Lali dice que está buenísima, aunque esté medio cruda.",
        ),
        "gigantes" to unDia(
            Parrafo.GENTE to "Esta noche el viejo Bako cuenta la historia de los primeros: unos gigantes peludos que no tenían fuego, que comían la carne cruda y cazaban corriendo detrás de los animales hasta que caían muertos. Los niños se le arriman y se tapan la boca para no gritar.",
            Parrafo.PENSAMIENTO to "{nombre} oye esa historia desde niña, y cada vez los gigantes son un poco más grandes. Piensa que debía de ser muy difícil vivir sin fuego, de noche, con los leones. Luego piensa que algún día alguien contará su vida como una historia, y le da risa.",
            Parrafo.ANOCHECER to "Cuando Bako termina, nadie quiere irse a dormir lejos del fuego. Tiko se queda dormido abrazado a la pierna de {nombre}, y ella tiene que cargarlo hasta la choza. Antes de cerrar los ojos mira hacia la oscuridad, por si acaso hay algún gigante.",
        ),
        "agua_queda" to unDia(
            Parrafo.MANANA to "Hoy, cuando ya deberían haberse terminado las lluvias, el lago sigue lleno. Más que lleno: el agua ha subido hasta unos árboles que siempre estuvieron en seco. Los viejos bajan a mirarlo y se quedan callados, que en ellos es mala señal, o muy buena.",
            Parrafo.PUEBLO to "En la orilla nueva los árboles tienen los pies en el agua, y las garzas se posan en ramas donde antes había lagartijas. Los flamencos están más cerca del campamento que nunca. Los niños los espantan para verlos volar, rosados, todos a la vez.",
            Parrafo.PENSAMIENTO to "{nombre} no sabe qué quiere decir que el agua se quede. Sese dice que su abuela contaba de un tiempo en que el lago era grande todo el año, y que nadie le creía. Esta noche, junto al fuego, todos le van a pedir a Sese que lo cuente otra vez.",
        ),
        "tiko_arroyo" to unDia(
            Parrafo.MANANA to "Hoy Tiko se escapó detrás de los niños grandes, que iban al arroyo a buscar ranas. Nadie lo vio irse. {nombre} se da cuenta cuando ya es tarde para alcanzarlos, y se pasa la mañana mirando hacia el arroyo cada vez que levanta la cabeza.",
            Parrafo.GENTE to "Los niños grandes vuelven sin él. Dicen que se quedó atrás, que no quería caminar más. {nombre} ya se está levantando cuando aparece Tiko solo, por el camino, con una rana muerta en la mano y la cara de quien ha hecho algo enorme.",
            Parrafo.ANOCHECER to "Esta noche Tiko le cuenta a todo el que quiera oírlo que fue solo hasta el arroyo y volvió solo. Ekwa le dice que eso es lo que hacen los cazadores. {nombre} le dice que si lo vuelve a hacer lo ata a un árbol, y los dos saben que no lo atará.",
        ),
        "jirafa" to unDia(
            Parrafo.MANANA to "Hoy Ekwa volvió corriendo antes del mediodía: mató una jirafa lejos, a media mañana de camino, con una flecha que tardó en hacer efecto. Una jirafa no se puede traer. Así que el campamento entero levanta las chozas y se va hacia la jirafa.",
            Parrafo.PUEBLO to "Mudarse es fácil cuando todo cabe en la espalda: las pieles, las cestas, las cáscaras de agua, los niños. Antes de que el sol baje ya hay chozas nuevas de hierba junto a la jirafa, y fuegos encendidos, y humo, y carne asándose.",
            Parrafo.GENTE to "Ekwa no dice nada de su jirafa: los demás lo dicen por él. Así es la costumbre: el cazador se calla y el campamento presume. Los hombres que ya mataron un animal grande se reparten, aparte, las partes que solo ellos pueden comer, y los demás no miran.",
            Parrafo.ANOCHECER to "Esta noche se come jirafa hasta que duele la barriga, y la carne que sobra se seca en tiras colgadas sobre el humo. Los niños juegan con los huesos. {nombre} se acuesta en una choza nueva, en un sitio nuevo, y se duerme oliendo a grasa y a hierba recién cortada.",
        ),
        "espera" to unDia(
            Parrafo.MANANA to "Hoy {nombre} sabe que espera otro hijo. Lo sospechaba desde hace unas lunas, y esta mañana, al oler la carne asada, se ha tenido que apartar del fuego. Sese la mira desde lejos y se ríe, porque ella también lo sabía.",
            Parrafo.GENTE to "Se lo dice a Ekwa por la tarde, sin rodeos, mientras él arregla una flecha. Ekwa deja la flecha, la mira, y vuelve a la flecha. Más tarde lo oye contárselo a otros hombres junto al fuego, como si fuera una presa. Lali pregunta si será niña, y Tiko si se podrá comer.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los dos hijos que no llegaron a caminar y en los dos que sí. Uno de cada dos, como a todas. No lo piensa con tristeza sino con cuidado, como quien cruza el lago seco: sabe por dónde se hunde y por dónde no.",
        ),
        "leon" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy al amanecer había huellas de león alrededor del campamento, frescas, grandes como una mano abierta. Dio una vuelta entera por la noche, entre las chozas y la llanura, y nadie lo oyó. Los hombres las miran y no dicen nada bueno.",
                Parrafo.PUEBLO to "Las mujeres salen hoy a cavar todas juntas, más juntas que nunca, hablando fuerte y golpeando los palos, y con dos hombres armados en lugar de uno. Los niños se quedan en el campamento, cerca del fuego, con los viejos. Nadie protesta.",
                Parrafo.ANOCHECER to "Esta noche se encienden más fuegos que de costumbre y se ponen ramas de espino alrededor de las chozas. Los hombres se turnan para velar. {nombre} duerme con los niños en medio, y se despierta cada vez que cruje una rama.",
            ),
            dia(
                Parrafo.GENTE to "El león volvió anoche. Se le oyó toser, cerca, y los hombres le gritaron y le tiraron tizones hasta que se fue. Bako dice que es uno viejo, que ya no puede cazar animales rápidos y que por eso ronda a la gente. Lo dice como si lo conociera.",
                Parrafo.ANOCHECER to "Otra noche con los fuegos altos. Nadie canta. Los niños preguntan si el león se come a los niños, y los mayores les dicen que no, que solo a los que no se duermen. Se duermen todos enseguida.",
            ),
            dia(
                Parrafo.PUEBLO to "Hoy los hombres siguieron el rastro del león hasta unas rocas, lejos, y volvieron diciendo que se fue hacia las colinas. Nadie está muy seguro, pero todos deciden creerlo. Las mujeres vuelven a cavar donde siempre.",
                Parrafo.ANOCHECER to "Esta noche los fuegos vuelven a ser los de siempre, y alguien se atreve a cantar. {nombre} se acuesta todavía con un ojo abierto, por costumbre. Antes de dormirse, quita de la piel de dormir una espina de espino que se le había quedado prendida.",
            ),
        )),
        "pie_sese" to unDia(
            Parrafo.MANANA to "Hoy Sese se lastimó un pie con una piedra afilada, escondida en el pasto, al volver de cavar. La herida es fea y no deja de sangrar. {nombre} se la lava con agua, le pone hojas machacadas y se la envuelve con una tira de piel.",
            Parrafo.GENTE to "Sese dice que no es nada, y se levanta para demostrarlo, y se vuelve a sentar. Las mujeres le traen comida sin que la pida. Bako, que cojea desde hace años, le explica cómo se camina con un palo, y Sese le contesta que ella no es tan vieja como él.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un campamento se mueve al paso del que camina más despacio, y que desde hoy esa es su madre. No lo dice. Piensa también que Sese la cargó a ella muchas lunas, y que ahora le toca a ella, aunque Sese no se deje.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "paleolitico", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
