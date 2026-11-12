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
 * El cuento de Pay, escriba de la cuadrilla de la Tumba en Deir el-Medina, hacia el 1200 a. C. Sigue la gramática y el
 * tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Vive en la aldea de los obreros, en una calle de casas pegadas dentro de un muro, entre cerros del desierto. Cada
 *   mañana sube por el sendero del cerro al Valle donde se hace la tumba del rey, y lleva la cuenta de todo.
 * - El párrafo del pueblo lee los arcos «rio» (el valle verde o inundado al fondo) y «tumba» (cómo va la tumba del rey).
 * - No repite lo que ya dice «Su vida»: las setenta casas, el agua que sube en burro, las óstraca y los papiros, las
 *   ausencias, la semana de diez días con sus dos turnos, la paga en grano y cuántos leen.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG3.kt`):
 * - Meryt nace en la temporada 2 (`nace_meryt`); Tamit la espera desde `tamit_espera` (1).
 * - Tiy, su madre, vive hasta `muere_tiy` (temporada 3).
 * - Paneb es el capataz hasta `paneb_visir` (3); después no se sabe de él.
 * - El estado «valle» (temporada 4): la cuadrilla duerme en las chozas del paso para acabar la tumba del rey muerto.
 * - Las fiestas que vuelven cada año no nombran a Meryt, a Tiy ni a Paneb.
 */
internal object PiezasPay {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta antes que el sol, en el cuarto de delante, donde en un nicho de la pared están los dioses de la casa: Bes, el enano que espanta los malos sueños, y Taweret, la hipopótama de los partos. Les deja una miga de pan y un poco de agua, y sale."),
        v("La primera luz llega rosa por encima de los cerros del este, del otro lado del río, y la aldea, que está en una hondonada, todavía queda en sombra. Desde el tejado, donde duermen en verano, {nombre} ve el valle verde y el río, que brilla como un cuchillo."),
        v("En la calle de la aldea ya se oyen las piedras de moler de todas las casas a la vez, y las voces de las mujeres, que hablan de tejado a tejado. {nombre} se lava la cara con el agua de la jarra, que está fresca porque el barro suda, y se pone el faldellín limpio."),
        v("Tamit le da pan de ayer y una jarra de cerveza espesa, que es el desayuno de todo Egipto. {nombre} lo toma sentado en el banco de la entrada, con la paleta de escriba ya colgada del hombro: dos huecos para la tinta, roja y negra, y los pinceles de junco en su ranura."),
        v("{nombre} se ata las sandalias de papiro, mete en la bolsa dos lascas de caliza lisa para escribir y sale a la calle estrecha, donde los obreros de la cuadrilla ya van saliendo de sus casas con las herramientas. Al final de la calle está la puerta del muro, y detrás, el sendero."),
        v("Antes de salir, {nombre} se para delante del busto de su abuelo, que está en un nicho de la casa con una lámpara apagada delante. Es un espíritu excelente, como se dice de los muertos buenos, y a los espíritus excelentes se les cuenta lo que pasa. Hoy le cuenta poco."),
        v("A {nombre} lo despierta el rebuzno del burro del aguador, que llega a la puerta del muro con los cántaros llenos antes de que salga el sol. Las mujeres ya hacen fila. Él se queda un momento acostado en la estera, oyendo el agua caer en las jarras, que es el mejor ruido de la aldea."),
    )

    /** Las semanas en las chozas del paso, para acabar la tumba del rey muerto (estado «valle», temporada 4). */
    private val VALLE = listOf(
        v("{nombre} se despierta en una de las chozas de piedra del paso, en lo alto del cerro, entre la aldea y el Valle, con otros cuatro de la cuadrilla. Hace frío. Desde aquí se ve la aldea abajo, chiquita, y el humo de las casas donde están los suyos. Hoy tampoco bajará.", estado = "valle"),
        v("A esta hora duerme en la choza del paso, en el suelo de piedra, envuelto en la manta. Los de la cuadrilla roncan. Fuera, el viento del desierto silba entre las piedras, y a lo lejos aúlla un chacal. {nombre} piensa en Tamit y en los niños, y se duerme pensando.", estado = "valle", hacer = Hacer.DORMIR),
        v("A esta hora come en el paso lo que subieron de la aldea: pan, cebollas, pescado seco y cerveza. Comen deprisa, sentados en las piedras, mirando hacia el Valle. El capataz ya está de pie. Hay que acabar antes de que llegue el rey muerto, y el rey muerto no espera.", estado = "valle", hacer = Hacer.COMER),
        v("A esta hora baja por el sendero del paso al Valle, que desde aquí es un barranco blanco entre cerros sin una planta. La tumba del rey es una boca negra en la roca. Delante hay un montón de lascas de caliza más alto que un hombre: lo que salió de dentro.", estado = "valle", hacer = Hacer.CAMINO),
        v("A esta hora lleva la cuenta dentro de la tumba, con prisa: cuántas mechas se gastaron, cuánto yeso, qué corredor se dejó sin pintar porque no da tiempo. El corredor del fondo lo van a convertir en cámara del rey, dice el capataz. {nombre} lo apunta, y no lo discute.", estado = "valle", hacer = Hacer.TRABAJO),
        v("A esta hora, a la sombra de la choza, remienda sus sandalias y las de su hermano, que se gastaron en el sendero. No hay nadie que lo haga por él. En la aldea esto lo hacía Tamit; aquí, en el paso, cada uno es su propia mujer, dice Hay, y se ríe.", estado = "valle", hacer = Hacer.CUIDAR),
        v("A esta hora está con la cuadrilla junto al fuego de las chozas, contando cuánto falta para el entierro. Uno trae una jarra de cerveza que alguien le mandó de la aldea. Se pasa de mano en mano y alcanza para un trago. Se habla poco, de cansados.", estado = "valle", hacer = Hacer.GENTE),
        v("Las chozas del paso son una fila de cuartitos de piedra seca, sin techo casi, apoyados en la ladera, con un patio pequeño delante. Cada familia tiene la suya, con su nombre escrito en una piedra. Ahí se duerme cuando el trabajo aprieta. Ahora aprieta.", estado = "valle"),
        v("En el paso no hay mujeres ni niños, solo hombres cansados, piedras y viento. De vez en cuando sube un muchacho de la aldea con pan, agua y recados. Huy subió ayer con una cesta y un dibujo de su hermana para su padre: un pájaro con cuatro patas.", estado = "valle"),
        v("Al caer la tarde, desde el paso, {nombre} ve cómo se encienden las lámparas de la aldea, abajo, una a una. Busca la de su casa y la encuentra, o cree encontrarla. Luego se mete en la choza y se tapa con la manta, que huele a casa.", estado = "valle"),
    )

    /** Más del paso: son diez semanas, y con una sola variante por hueco el cuento se repetiría día tras día. */
    private fun va(texto: String, hacer: Hacer? = null) = v(texto, estado = "valle", hacer = hacer)
    private val VALLE_MANANA = listOf(
        va("{nombre} se despierta antes de que el capataz golpee la piedra para llamar a la cuadrilla. Fuera, el desierto está gris y frío, y abajo, en la aldea, todavía no hay humo. Se lava la cara con un poco de agua del cántaro, que hay que ahorrar, y se ata las sandalias."),
        va("Al amanecer el paso es lo primero que toca el sol, y por eso {nombre} lo ve antes que nadie: la luz roja en la cumbre de la montaña, la que tiene forma de pirámide, y luego en las chozas. Allí abajo, en el Valle, todavía es de noche, y en la tumba es siempre de noche."),
    )
    private val VALLE_TRABAJO = listOf(
        va("A esta hora reparte las mechas y los cinceles a la entrada de la tumba, y apunta quién se lleva cada uno, porque el cobre es del rey y cada cincel se devuelve pesado. Hoy se trabaja en dos turnos más largos que nunca. Nadie se queja: el rey está muerto y espera.", Hacer.TRABAJO),
        va("A esta hora está dentro de la tumba con los pintores, que ya no tienen tiempo de pintar: solo trazan en rojo lo que haría falta y pasan al corredor siguiente. {nombre} anota lo que queda sin hacer. Lo apunta todo. Algún día alguien sabrá que no se pudo.", Hacer.TRABAJO),
        va("A esta hora vuelve al paso por el sendero, con las piernas llenas de polvo blanco, detrás de la cuadrilla, que sube en fila sin hablar. Arriba están las chozas. Abajo, a la izquierda, la aldea, donde hoy tampoco va a dormir.", Hacer.CAMINO),
        va("A esta hora come pan y una cebolla a la sombra de la entrada de la tumba, con la espalda contra la roca fresca. Huy subió ayer con pan de su madre, y {nombre} lo raciona como si fuera grano del rey. Le sabe a casa.", Hacer.COMER),
        va("A esta hora se lava en el paso con un poco de agua, frotándose el polvo de caliza que se le mete por todas partes, y se cura con aceite una cortadura de la mano. En la aldea Tamit le pondría miel. Aquí no hay miel.", Hacer.CUIDAR),
        va("A esta hora escucha a Hay, su hermano, que cuenta en la puerta de la choza cómo era la tumba de Seti cuando la empezaron, y lo que tardaron. Los jóvenes de la cuadrilla no lo creen. {nombre} sí: él la apuntó, día por día.", Hacer.GENTE),
        va("A esta hora duerme mal, porque en el paso se oye todo: el viento, los chacales, las piedras que se sueltan de la montaña. Sueña con la tumba, con corredores que no se acaban y con un rey que pregunta si ya está.", Hacer.DORMIR),
    )
    private val VALLE_PUEBLO = listOf(
        va("El paso es una silla entre dos cerros, en el camino de la aldea al Valle, con un puesto de vigilancia y las chozas de la cuadrilla. Desde aquí se ve todo: la aldea, los templos de la orilla, el río, los campos verdes y, al otro lado, Tebas, con sus muros."),
    )
    private val VALLE_GENTE = listOf(
        va("Hay, su hermano, duerme en la misma choza y ronca como un buey. Durante el día pica la piedra con los demás y de noche le cuenta a {nombre} las mismas historias de cuando eran niños. En la aldea apenas se ven; aquí arriba se ven demasiado, y se pelean, y se ríen."),
        va("El capataz no duerme: sube y baja entre la tumba y el paso, cuenta a los hombres, mira los corredores y pregunta cuántos días faltan. {nombre} le da el número, y cada día el número es el mismo, porque la tumba crece tan despacio como se acaba el plazo."),
    )
    private val VALLE_ANOCHECER = listOf(
        va("Al anochecer la cuadrilla se sienta delante de las chozas a mirar el valle del río, que se pone violeta. Alguien canta una canción de la aldea, bajito. {nombre} piensa en Meryt, que ya habla, y en las palabras nuevas que estará aprendiendo sin él."),
        va("De noche en el paso hace frío, aunque de día queme. {nombre} se envuelve en la manta y mira las estrellas, que aquí arriba son más. Encuentra la de Isis, que cada noche se pone más temprano. Cuando vuelva a salir al amanecer, llegará la crecida, y el rey ya estará enterrado."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el desierto el tiempo se mira poco: casi siempre hace lo mismo, que es sol."),
        v("{nombre} mira el cielo desde lo alto del sendero, por encima de los cerros pelados del Valle. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Lo que cambia con las estaciones, aquí arriba, no es el cielo sino el río, que se ve allá abajo, a lo lejos."),
        v("{cielo} En tiempo de {estacion} el valle del río cambia de color, y desde los cerros {nombre} lo ve entero, como un tapiz extendido."),
        v("Sobre el Valle se levanta la cima, el cerro con forma de pirámide donde vive la diosa que ama el silencio. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los viejos de la aldea dicen que el sol de aquí es más fuerte que el de la otra orilla, porque está más cerca de los muertos."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y alto, de un azul que parece pintado con lapislázuli.",
            "Hace un día claro, y la caliza de los cerros brilla tanto que hay que entrecerrar los ojos.",
            "No hay una nube, y el sol pega sobre el sendero desde que sale.",
        ),
        Clima.NUBES to listOf(
            "Hay unas nubes raras sobre el desierto, que los viejos miran como si fueran un aviso.",
            "El cielo está velado, blanco, y el sol se ve como un disco de cobre.",
        ),
        Clima.LLUVIA to listOf(
            "Cae una lluvia rara, de las que pasan una vez en muchos años, y la aldea entera sale a mirarla.",
            "Llueve un poco, y el polvo del sendero se vuelve barro por una mañana.",
        ),
        Clima.TORMENTA to listOf(
            "Se viene una tormenta del desierto, negra, de las que no se ven casi nunca, con truenos sobre los cerros.",
            "Truena sobre el Valle, y el agua baja por los barrancos secos como un río que no existe.",
        ),
        Clima.NIEBLA to listOf(
            "Hay bruma sobre el valle del río, y desde los cerros los campos desaparecen.",
            "La bruma de la mañana tapa la otra orilla, y los templos de allá no se ven.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del norte, que refresca un poco y levanta la arena del sendero.",
            "Hay viento en el paso, y las lascas de caliza tintinean al rodar por la ladera.",
        ),
        Clima.POLVO to listOf(
            "Sopla el viento del desierto, cargado de arena, y el sol se vuelve amarillo.",
            "El aire está lleno de polvo, que se mete en los ojos, en el pan y en los pinceles.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.INUNDACION to listOf(
            "Es la estación de la inundación, y desde los cerros el valle es un lago con aldeas como islas.",
            "En la inundación el río cubre los campos, y en la otra orilla los campesinos no tienen qué sembrar.",
            "Es la inundación, el principio del año, cuando el río trae el barro negro que hace crecer todo.",
        ),
        Estacion.SIEMBRA to listOf(
            "Es la estación de la siembra, y el valle se pone verde, de un verde que desde el desierto hace daño.",
            "En la siembra las mañanas son frías en el sendero, y hay que subir con manta.",
            "Es la siembra, la estación más fresca, la mejor para trabajar dentro de una tumba.",
        ),
        Estacion.COSECHA to listOf(
            "Es la estación de la cosecha, y el valle se pone dorado y luego pardo.",
            "En la cosecha el calor empieza a apretar, y dentro de la tumba no se puede respirar.",
            "Es la cosecha, y de la otra orilla llega el grano nuevo para las raciones.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora pasa lista en la entrada de la tumba: cada obrero dice su nombre y {nombre} lo marca en la lasca de caliza. Al que falta, se le apunta el porqué: enfermo, haciendo cerveza, embalsamando a su hermano, peleado con su mujer. La lista va al visir.", hacer = Hacer.TRABAJO),
        v("A esta hora reparte las mechas para las lámparas: trapos de lino torcidos y untados de grasa, que se cuentan uno por uno, porque dentro de la tumba no hay más luz que esa. Al final del día se cuentan las que quedan. Las mechas son lo que más se roba.", hacer = Hacer.TRABAJO),
        v("A esta hora pesa los cinceles de cobre que devuelven los canteros, gastados, en una balanza de dos platos, contra pesas de piedra. El cobre es del rey, y cada pedacito que se pierde hay que explicarlo. {nombre} apunta el peso y manda los cinceles al herrero.", hacer = Hacer.TRABAJO),
        v("A esta hora está dentro de la tumba, en un corredor que baja hacia la roca, mirando cómo los pintores trazan las figuras en rojo sobre el yeso fresco, y cómo el maestro corrige en negro. {nombre} apunta qué pared se terminó, y respira el humo de las mechas.", hacer = Hacer.TRABAJO),
        v("A esta hora escribe el diario de la obra en un papiro, con letra rápida: el día, el mes, lo que se cavó, lo que se pintó, lo que se gastó, lo que se dijo. Algún día, piensa, alguien leerá esto y sabrá que hoy Khonsu llegó tarde.", hacer = Hacer.TRABAJO),
        v("A esta hora mide con una cuerda de nudos el corredor nuevo que abren los canteros, para ver si va recto y si baja lo que tiene que bajar, como dice el plano. Si se desvía, hay que decírselo al capataz, y al capataz no le gusta que se lo digan.", hacer = Hacer.TRABAJO),

        v("A esta hora come pan con cebolla y la cerveza espesa, de pie y con prisa, que en la obra no se espera a nadie. Tamit le metió además dos dátiles en la bolsa, que es lo dulce del día.", hacer = Hacer.COMER),
        v("A esta hora come pan, pescado seco y cebollas a la sombra de lo que haya, y el que puede se echa un rato en el suelo con la cabeza sobre la bolsa. Con la boca llena nadie habla, y eso, en la cuadrilla, ya es un descanso.", hacer = Hacer.COMER),
        v("A esta hora come con la cuadrilla en el Valle lo que trajo cada uno, y comparten: uno tiene higos, otro un trozo de queso, otro una jarra de cerveza buena. {nombre} pone unos panes de su casa, que Tamit hace con miel cuando puede.", hacer = Hacer.COMER),

        v("A esta hora sube por el sendero del cerro, que trepa desde la aldea en zigzag hasta el paso y baja por el otro lado al Valle. Va en fila con la cuadrilla, con la bolsa al hombro, sin hablar, porque la subida corta la respiración.", hacer = Hacer.CAMINO),
        v("A esta hora baja del Valle a la aldea por el sendero, con el sol de frente y la espalda cansada. Desde arriba se ve la aldea entera, con sus tejados planos, y más allá los templos de los reyes, a la orilla de los campos, y el río.", hacer = Hacer.CAMINO),
        v("A esta hora camina por el sendero con su hermano Hay, que habla de colores como otros hablan de mujeres: el rojo del ocre, el azul de Egipto que se muele de una piedra, el amarillo que hay que ir a buscar lejos. {nombre} escucha y apunta mentalmente el precio.", hacer = Hacer.CAMINO),

        v("A esta hora ayuda en la casa: sube al tejado los sacos de grano de la ración, repara el banco de la entrada, le enseña a Huy a sostener el pincel como se sostiene, con los dedos sueltos. En la aldea los escribas también cargan sacos.", hacer = Hacer.CUIDAR),
        v("A esta hora barre el patio de la cocina, al fondo de la casa, donde está el horno de barro, y apila la leña que trajo el aguador. Tamit amasa el pan de mañana. Hablan de lo del día, que es siempre lo mismo y nunca igual.", hacer = Hacer.CUIDAR),
        v("A esta hora le lleva una ofrenda a la capilla de su familia, en la ladera, junto a las tumbas de la aldea: un pan, una flor, un poco de cerveza. Las tumbas de los obreros son pequeñas y están pintadas por ellos mismos, mejor que algunas de reyes.", hacer = Hacer.CUIDAR),

        v("A esta hora está sentado en el banco de la puerta con los vecinos, en la calle estrecha de la aldea, con una jarra de cerveza que pasa de mano en mano. Se habla del capataz, de las raciones y de quién se casa con quién. En la aldea todo se sabe, y lo que no se sabe se inventa.", hacer = Hacer.GENTE),
        v("A esta hora juega al senet con Khonsu, el cantero de al lado, en un tablero de treinta casillas dibujado en una piedra, con fichas de barro. Se juega por nada, y se discute como si fuera por la herencia. Gana Khonsu, como casi siempre, y lo dice.", hacer = Hacer.GENTE),
        v("A esta hora escribe una carta para un vecino que no sabe escribir, sentado a la luz de la lámpara, con el vecino al lado dictando y corrigiendo. Es para su hermano, que vive en la otra orilla, y le pide que le devuelva un burro. {nombre} pone el burro más educado.", hacer = Hacer.GENTE),

        v("A esta hora duerme en el tejado de la casa, sobre una estera, porque dentro hace demasiado calor, con la familia alrededor y las estrellas encima. Desde aquí se oyen los perros de la aldea y, muy lejos, el río.", hacer = Hacer.DORMIR, meses = setOf(4, 5, 6, 7, 8, 9, 10)),
        v("A esta hora duerme en el cuarto de delante, en la cama de obra que hay junto a la pared, con la manta de lino hasta los ojos. Fuera, en la calle, pasa el vigilante de la aldea con un farol. {nombre} no lo oye.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, como duermen los que subieron y bajaron un cerro. Sueña con signos que no sabe leer, en una pared que nunca ha visto, y se despierta sin acordarse de ninguno.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y en la noche de la aldea se oyen los ruidos de siempre: un niño que llora, un burro, una puerta. Los escorpiones salen a esta hora, y por eso nadie duerme en el suelo descalzo. {nombre} tiene la sandalia a mano.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La aldea es una sola calle larga y estrecha, con casas pegadas a cada lado, de adobe y piedra, todas iguales, dentro de un muro. Las puertas están pintadas de rojo y algunas llevan el nombre del dueño escrito encima. Afuera del muro, solo cerros y arena. {entorno}"),
        v("{entorno} En la ladera de la aldea están las tumbas de los obreros, con pequeñas pirámides de adobe encima y capillas pintadas. Los que hacen las tumbas de los reyes se hacen también las suyas, en los días libres, con lo que sobra de la obra y lo que no sobra."),
        v("Al otro lado de los cerros está el Valle, donde se esconden las tumbas de los reyes, sin templos encima ni señales, para que nadie las encuentre. Solo la cuadrilla sabe dónde está cada una. {entorno}"),
        v("A la orilla de los campos, frente a la aldea, están los templos de los reyes muertos, enormes, con sus estatuas de piedra mirando al río. Allí van los sacerdotes cada día a darles de comer. Desde la aldea se ven sus muros. {entorno}"),
        v("{entorno} En la aldea viven egipcios, pero también gente de Nubia y de las tierras del norte, cuyos abuelos llegaron como prisioneros o como mercaderes. Todos hablan egipcio, y todos le rezan a los mismos dioses de la aldea, y a algunos más."),
        v("En la plaza de la aldea está el tribunal de los obreros, que juzga las peleas de los vecinos: quién le debe un burro a quién, quién robó una cama, quién insultó a la suegra del capataz. Lo que no se arregla ahí, se lo preguntan al oráculo del rey patrón. {entorno}"),
    )

    /** Lo que se ve desde la aldea y en el Valle, según el río y la tumba del rey. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("rio", 0.5, frases = listOf(
            "Desde los cerros se ve el valle del río convertido en un lago, con las aldeas de la otra orilla como islas.",
            "Allá abajo el río ha cubierto los campos, y el agua llega casi a los pies de los templos.",
        )),
        Entorno("tumba", 0.999, frases = listOf(
            "La tumba del rey ya está terminada y sellada, y el Valle está callado, sin el ruido de los cinceles.",
            "En el Valle ya no se trabaja: la tumba del rey está cerrada con piedra y barro, y nadie sabe qué dirá la cuadrilla mañana.",
        )),
        Entorno("tumba", 0.4, 0.999, frases = listOf(
            "En la tumba del rey ya se pintan los corredores de abajo, y en la entrada hay un andamio y un montón de lascas blancas.",
            "La tumba del rey baja ya muy hondo en la roca, y en sus paredes se pinta el viaje del sol por la noche.",
        )),
        Entorno("tumba", 0.0, 0.4, frases = listOf(
            "La tumba del rey nuevo es todavía un corredor que entra en la roca, con un montón de lascas recién cortadas a la entrada.",
            "En el Valle suena otra vez el cincel: la tumba nueva apenas empieza, y la roca está blanca y fresca.",
        )),
    )

    private val GENTE = listOf(
        v("Tamit, su mujer, hace el pan y la cerveza, teje lino, cría patos en el patio y lleva las cuentas de la casa mejor que él las de la tumba. Cuando él llega cansado, ella le cuenta todo lo que pasó en la aldea, que siempre es más que lo que pasó en el Valle."),
        v("Huy, su hijo, quiere ser escriba como su padre, y se pasa la tarde copiando signos en lascas de caliza que su padre le trae del Valle. Copia mal los pájaros y bien los ojos. {nombre} le corrige con paciencia, y a veces con un golpecito en la cabeza, como le corrigieron a él."),
        v("Hay, su hermano, es pintor de la cuadrilla, y tiene las manos siempre manchadas de colores. Dibuja dioses en las paredes del rey y monos en las lascas para hacer reír a los niños. {nombre} cree que su hermano tiene la mejor mano de la aldea. Hay también lo cree."),
        v("Khonsu, el cantero de la casa de al lado, es su amigo desde niños y su rival en el senet. Tiene los brazos como troncos y una risa que se oye desde la puerta del muro. Siempre llega tarde a la tumba, y {nombre} siempre lo apunta, y siempre lo perdona en la lista."),
        v("El aguador es un hombre de la otra orilla que sube cada mañana con su burro y sus cántaros, y sabe todo lo que pasa en el valle antes que nadie: quién murió, quién se casó, cuánto cuesta el grano. {nombre} le paga con pan y le escucha gratis."),
        v("Tiy, su madre, vive con ellos, en el cuarto de atrás, y es la que más sabe de partos, de remedios y de los muertos de la familia, a los que nombra uno por uno cuando le dejan ofrendas. Ya no sube al tejado, porque le duelen las rodillas, y manda desde abajo.", hasta = "muere_tiy"),
        v("Paneb, el capataz de su cuadrilla, es un hombre fuerte, rico y temido, que tiene amigos arriba y enemigos en todas las casas de la aldea. Se cuentan de él cosas que no se escriben. {nombre} lo apunta en la lista como a los demás, y no apunta nada más.", hasta = "paneb_visir"),
        v("Tamit espera un hijo, y sigue moliendo el grano y amasando el pan, con la barriga por delante. Las vecinas le traen amuletos de Taweret y consejos. {nombre} le compra a un vecino un amuleto nuevo, de piedra azul, que dicen que trae suerte en el parto.", desde = "tamit_espera", hasta = "nace_meryt"),
        v("Meryt, la pequeña, duerme en una cesta junto a la cama, con un amuleto de Bes atado a la muñeca. Cuando {nombre} llega del Valle, la niña ya lo conoce por los pasos, dice Tamit, y él se lo cree, porque quiere creerlo.", desde = "nace_meryt"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del oficio: con las manos antes que con la cabeza."),
        v("Hay cosas que solo se miran cuando faltan. {dato_objeto}"),
        v("{dato_objeto} En la aldea se dice que un escriba sin paleta es como un rey sin corona, y lo dicen los escribas."),
        v("{dato_objeto} Son cosas que se aprenden mirando al padre, como casi todo en la aldea."),
        v("En la casa de un escriba hay pocas cosas, y casi todas sirven para escribir o para comer. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("ostracon", "la lasca de caliza", "una lasca de caliza", "La lasca",
            "Las lascas de caliza salen a montones de la tumba, lisas y blancas; en ellas se escribe lo que no merece papiro: listas, cuentas, recados, ejercicios de los niños, y hasta chistes con dibujos de gatos que cuidan patos."),
        ObjetoCuento("papiro", "el papiro", "un papiro", "El papiro",
            "El papiro se hace en el delta con tiras del tallo de una planta del pantano, pegadas en dos capas y aplastadas; es caro, viene en rollos, y se escribe en él lo que tiene que durar: el diario de la obra, las cartas importantes, los libros."),
        ObjetoCuento("paleta", "la paleta de escriba", "una paleta de escriba", "La paleta",
            "La paleta de escriba es una tablilla larga de madera con dos huecos para las pastillas de tinta, una negra de hollín y otra roja de ocre, y una ranura para los pinceles; un escriba la lleva al hombro como un soldado su espada."),
        ObjetoCuento("pincel", "el pincel de junco", "un pincel de junco", "El pincel",
            "El pincel del escriba es un tallo de junco con la punta masticada hasta que se abre en fibras; se moja en agua, se frota en la pastilla de tinta y escribe hasta que se gasta, y entonces se muerde otra vez."),
        ObjetoCuento("cincel", "el cincel de cobre", "un cincel de cobre", "El cincel",
            "El cincel de cobre es lo que abre la roca de las tumbas, golpe a golpe con un mazo de madera; se gasta tan rápido que cada día se pesan los cinceles que se dan y los que vuelven, y el herrero los rehace."),
        ObjetoCuento("mecha", "la mecha", "una mecha", "La mecha",
            "La mecha es un trapo de lino torcido y untado de grasa, a veces con sal para que no humee; dentro de la tumba es la única luz, y por las mechas que se gastan se sabe cuántas horas trabajó la cuadrilla en la oscuridad."),
        ObjetoCuento("cerveza", "la cerveza", "una jarra de cerveza", "La cerveza",
            "La cerveza de la aldea se hace con pan de cebada remojado y fermentado en jarras, y se cuela con una cesta; es espesa, poco fuerte y alimenta, y se toma con una caña para no tragar las cascarillas."),
        ObjetoCuento("pan", "el pan", "un pan", "El pan",
            "El pan de la aldea se hace con el trigo de la ración, molido en casa entre dos piedras, y lleva siempre algo de arena de la piedra y del desierto; por eso a los viejos de la aldea se les gastan los dientes antes que a nadie."),
        ObjetoCuento("cantaro", "el cántaro", "un cántaro", "El cántaro",
            "El cántaro de agua es de barro poroso que suda, y por eso el agua de dentro se mantiene fresca; en la aldea no hay pozo, así que cada cántaro que se rompe es un viaje más del burro del aguador."),
        ObjetoCuento("escarabajo", "el escarabajo", "un escarabajo", "El escarabajo",
            "El escarabajo es un amuleto de piedra o de loza azul con la forma del bicho que empuja una bola de estiércol, como el sol empuja el día; debajo lleva grabado un nombre o un deseo, y se lleva colgado del cuello para que la vida vuelva a empezar."),
        ObjetoCuento("sandalias", "las sandalias", "unas sandalias", "Las sandalias",
            "Las sandalias de la aldea son de papiro trenzado o de hoja de palma, con una tira entre los dedos; en el sendero del cerro se gastan en un mes, y en las tumbas se dejan unas de repuesto para el muerto, que también tendrá que caminar."),
        ObjetoCuento("lampara", "la lámpara", "una lámpara", "La lámpara",
            "La lámpara es un cuenco de barro con aceite o grasa y una mecha que asoma por el borde; da una luz pequeña y amarilla que hace bailar las figuras pintadas de la pared, como si se movieran."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el Valle como se piensa en el tiempo: no se lo elige, se trabaja en él. Hace casas para reyes muertos, que vivirán en ellas para siempre. A sus {edad} años, le parece un buen oficio, aunque a veces piensa en quién hará las casas de los obreros vivos."),
        v("Piensa a veces que todo lo que escribe va a quedar: las listas, las cuentas, las faltas, las mechas, las peleas. Algún día alguien leerá que Khonsu llegó tarde porque estaba haciendo cerveza. Le da risa y le da algo de miedo, que lo lean."),
        v("{mundo} Él piensa en cosas más cercanas: en la ración, que llegó corta, en la tos de Huy, en la pared que no se terminó. Las cosas grandes son del rey y del visir, que viven en la otra orilla y en otro mundo."),
        v("{mundo} A sus {edad} años, {nombre} ha bajado a la tumba de un rey cuando todavía era roca, y ha salido de ella cuando ya era un palacio pintado para la noche. Ningún vivo lo verá terminado, salvo ellos. Eso le parece lo más raro de su vida."),
        v("Su padre fue escriba de la cuadrilla, y su abuelo, y él lo será hasta que le tiemble la mano, y después Huy. En la aldea los oficios se heredan como las casas. A veces piensa qué sería ser otra cosa, campesino en la otra orilla, y se le pasa enseguida."),
        v("{mundo} A veces, en la oscuridad de la tumba, con las mechas encendidas, {nombre} mira a los dioses pintados en la pared, que miran al rey muerto, y piensa que también lo miran a él, que está ahí, apuntando mechas. Les hace un gesto con la cabeza, por si acaso."),
    )

    private val MUNDOS = mapOf(
        "seti" to listOf(
            "En la aldea se habla de la tumba del rey Seti, que tiene que estar lista antes de que la necesite.",
            "Se dice que en el norte hubo un rey que no era rey, y que ya no está, y nadie sabe bien qué pasó.",
            "Se habla de las raciones, que llegan, y del visir, que viene poco.",
        ),
        "denuncia" to listOf(
            "Se habla de la denuncia contra el capataz Paneb, que está escrita en un papiro que nadie ha leído entero.",
            "En la calle se dice que la tumba de la reina va mejor que la del rey, y eso trae problemas.",
        ),
        "paneb" to listOf(
            "Se habla de lo que hará el visir con el capataz Paneb, y de quién mandará la cuadrilla si no vuelve.",
            "Se habla de quién será el capataz nuevo, y cada casa tiene su candidato.",
        ),
        "muere_rey" to listOf(
            "Se habla del rey muerto y del rey nuevo, que es joven y está enfermo, dicen.",
            "En la aldea se dice que el canciller Bay es el que manda, y que el rey solo firma.",
        ),
        "siptah" to listOf(
            "Se habla de la tumba del rey nuevo, que avanza, y de si el rey vivirá para necesitarla.",
            "Se dice que el canciller Bay tendrá su propia tumba en el Valle, como un rey, y en la aldea eso no gusta.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando cae el sol detrás de los cerros, la aldea se queda en sombra mucho antes que el valle del río, que todavía brilla al otro lado. {nombre} se sienta en el tejado con un trozo de pan y mira cómo se apaga la otra orilla, primero los campos y al final los templos."),
        v("Al caer la tarde se cierra la puerta del muro de la aldea, y en la calle estrecha se encienden las lámparas de las casas, una tras otra. Huele a pan, a cerveza y a humo de estiércol. {nombre} se lava los pies, que es lo mejor del día, y entra."),
        v("Se cena en el patio de la cocina, sentados en esteras: pan, cebollas, lentejas y a veces un pescado del río, que llega seco. Se habla de lo de siempre. Huy enseña lo que copió, Tamit cuenta lo de la aldea, y {nombre} escucha más de lo que habla."),
        v("De noche, en la aldea, se oyen las voces de las casas de al lado a través de las paredes de adobe, y un perro, y alguien que canta una canción de amor desafinada. Desde el tejado se ven las estrellas sobre los cerros, más de las que hay en ningún otro sitio."),
        v("Anochece sobre los cerros, y la cima con forma de pirámide se pone roja y luego morada. Los viejos dicen que ahí arriba vive la diosa que ama el silencio, y que castiga a los que mienten. {nombre} la mira cada tarde, y cada tarde se acuerda de algo que dijo."),
        v("La última luz del día se queda un momento en lo alto de los cerros, cuando la aldea ya está a oscuras. {nombre} la ve desde la calle, con la paleta todavía al hombro. Mañana hay que subir otra vez. Hoy, por lo menos, ya no."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "amenhotep" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es la fiesta de Amenhotep, el rey que fundó la aldea hace muchas generaciones y que ahora es su dios. Su estatua sale de la capilla en andas, cargada por los obreros más viejos, vestidos de blanco, con su madre al lado, también en andas. No se sube al Valle.",
                Parrafo.PUEBLO to "La procesión recorre la calle de la aldea, despacio, entre cantos e incienso, y la gente le hace preguntas al rey en voz alta: si el burro es de este o de aquel, si el ladrón de la cama fue el vecino. Si la estatua se inclina hacia adelante, es que sí; si se echa atrás, es que no.",
                Parrafo.PENSAMIENTO to "{nombre} mira al rey dios pasar en sus andas, inclinándose a un lado y a otro según quién lo cargue, y piensa que los dioses de la aldea son muy de la aldea: saben de burros, de camas y de peleas. Le parece bien. Para lo grande ya están los del otro lado del río.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Hoy sigue la fiesta de Amenhotep, con banquete en la calle y cerveza de la buena, que se hace para esta fiesta y que se sube a los tejados al caer la tarde.",
            )),
        ),
        "min" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de Min, el dios de la cosecha y de la fuerza de los hombres, el que hace crecer el trigo. En la otra orilla su estatua sale del templo, y el rey, o quien vaya en su nombre, corta con una hoz la primera espiga del año.",
            Parrafo.PUEBLO to "En la aldea no se sube a la tumba, y se hace pan con harina blanca, que es de fiesta, y lechugas, que son la planta de Min. Las mujeres se ríen de las lechugas, que dicen que hacen hijos, y los hombres hacen como que no las oyen.",
            Parrafo.GENTE to "Tamit le sirve a {nombre} una lechuga entera en la cena, con sal, sin decir nada, y las vecinas, que miran desde la puerta, se ríen. {nombre} se la come entera, también sin decir nada. Huy no entiende de qué se ríe todo el mundo.",
        ),
        "valle" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la Fiesta del Valle, la más grande de este lado del río. La barca de Amón sale de su templo, en la otra orilla, cruza el río en una barca dorada y viene a visitar a los reyes muertos en sus templos, y de paso a todos los muertos de la necrópolis.",
                Parrafo.PUEBLO to "Toda la gente de Tebas cruza el río para la fiesta y viene a las tumbas de sus muertos, a comer y a beber con ellos, con flores, música y lámparas. Las colinas se llenan de familias, y la aldea, que vive entre tumbas, es el centro de la fiesta por un día.",
                Parrafo.PENSAMIENTO to "{nombre} lleva flores y pan a la capilla de su familia y se queda a comer con sus muertos, como todos. Piensa que a los muertos les gusta la compañía, y que la mejor ofrenda es que alguien se acuerde de su nombre en voz alta. Dice los nombres de todos.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Hoy sigue la Fiesta del Valle, y la gente pasa la noche en las capillas de las tumbas, borracha de cerveza y de flores, que es la forma de estar cerca de los muertos. Al amanecer se vuelve a la otra orilla.",
            )),
        ),
        "viento_desierto" to unDia(
            Parrafo.MANANA to "Hoy sopla el viento del desierto, el caliente, el que viene del sur cargado de arena. El cielo está amarillo desde el amanecer y no se ve la otra orilla. En la aldea se cierran las puertas y se tapan las jarras de agua con trapos mojados.",
            Parrafo.TRABAJO to "A esta hora la cuadrilla no puede trabajar en la entrada de la tumba, por la arena, y se mete adentro, en los corredores, donde el aire está quieto y caliente. {nombre} apunta que hoy se trabajó poco, y por qué: «viento». Es la palabra más corta del diario.",
            Parrafo.ANOCHECER to "Esta noche hay arena en el pan, en la cama, en los dientes y en los pinceles. {nombre} se sacude el faldellín en el tejado y la arena le cae en la cara. Tamit dice que el viento del desierto es el aliento de un dios enojado. Él dice que es solo viento, y tose.",
        ),
        "epagomenos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan los cinco días que están fuera del año, los que se le robaron a la luna para que nacieran cinco dioses: Osiris, Horus, Seth, Isis y Neftis. Son días peligrosos, en que no se empieza nada. En la aldea no se sube al Valle.",
                Parrafo.PUEBLO to "En cada casa se cuelgan amuletos en las puertas y se enciende una lámpara de más, para que los demonios de los días sin año no entren. Las mujeres hacen pasteles para los dioses que nacen. Se cuenta cuál de los cinco nace cada día.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en estos cinco días que no son de nadie, entre un año y otro, como un corredor entre dos cámaras. Le dan miedo y le gustan. En la tumba diría que son una antesala. Se queda en casa, como manda la costumbre, y escribe lo que no tiene tiempo de escribir.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Hoy es el día en que nació Horus, y se hacen ofrendas al halcón, al que cuida al rey.",
                "Hoy es el día en que nació Seth, el de la tormenta, el que mató a su hermano, y es el peor de los cinco: nadie sale de casa si no hace falta.",
                "Hoy es el día en que nació Isis, la madre, la que sabe las palabras que curan, y las mujeres le dejan pasteles.",
                "Hoy es el día en que nació Neftis, la que llora a los muertos, el último de los días fuera del año. Mañana empieza el año.",
            )),
        ),
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, el primer día de la estación de la inundación, cuando el río empieza a crecer. En la aldea se intercambian regalos pequeños: amuletos, frascos de perfume, pasteles con forma de dios. {nombre} le regala a Tamit un frasquito de aceite perfumado de la otra orilla.",
            Parrafo.PUEBLO to "En todas las casas se encienden lámparas nuevas y se limpian los nichos de los dioses de la casa. En el valle, al otro lado, los campesinos miran el río para ver cuánto va a subir, porque de eso depende todo el año. En la aldea también, porque de eso depende la ración.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza, que tendrá diez días por semana, tres semanas por mes, doce meses y una tumba. Pide poco: que no le piquen los escorpiones, que la ración llegue entera, que el rey viva. Si se cumplen dos de tres, será un buen año.",
        ),
        "crecida" to unDia(
            Parrafo.MANANA to "Hoy, desde lo alto del sendero, se vio que el río empieza a subir: el agua, que estaba baja y verde, se ha puesto roja de barro, y ya entra por los canales de los campos. Los de la cuadrilla se paran a mirarlo un momento antes de bajar al Valle.",
            Parrafo.PUEBLO to "En la otra orilla los campesinos sacan sus cosas de las casas bajas y suben a las aldeas de los montículos, y sus animales con ellos. En unas semanas el valle será un lago. Para la aldea de los obreros, que está en el desierto, el agua no es peligro, sino una buena noticia: habrá grano.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el río, que sube cada año en la misma estación como si alguien se lo mandara, y que de él depende todo, hasta la tumba del rey, que se paga con grano. Le parece que el río es el verdadero rey. No lo dice, porque esas cosas no se dicen.",
        ),
        "opet" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la fiesta de Opet, en la otra orilla: la barca de Amón sale de su templo grande y va en procesión al templo del sur, para visitar a su mujer, y con él el rey. Son once días de fiesta, y la cuadrilla tiene permiso para cruzar el río a verla.",
                Parrafo.PUEBLO to "En la otra orilla la procesión avanza entre la multitud, con sacerdotes rapados que cargan la barca dorada en hombros, músicos, bailarinas, soldados y vendedores de todo. Se reparte pan, carne y cerveza del templo, y nadie se queda con hambre.",
                Parrafo.GENTE to "{nombre} cruza el río en una barca con Tamit, Huy y los vecinos, y se pasan el día entre la gente, comiendo lo que reparten. Huy se sube a los hombros de su padre para ver pasar la barca del dios. Se le cae un pan, y un perro se lo come.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días dura la fiesta de Opet en la otra orilla, y por las noches se ven desde los cerros las luces de los templos y se oye, muy bajito, la música.",
                "Mientras dura Opet el trabajo en la tumba va despacio: muchos de la cuadrilla están en la otra orilla, y {nombre} apunta las ausencias con una palabra que no necesita explicación: «fiesta».",
                "Hoy la barca de Amón vuelve al templo grande, y la fiesta de Opet se acaba. Los que cruzaron el río vuelven a la aldea cansados, con dolor de cabeza y con historias.",
            )),
        ),
        "joiak" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan los ritos de Joiak, los días de Osiris, el dios que murió y volvió a vivir, como el trigo que se entierra y brota. En la aldea las mujeres hacen figuras de Osiris con barro y semillas, y las riegan para que germinen.",
                Parrafo.PUEBLO to "En las capillas de la aldea se representa la muerte de Osiris y el llanto de Isis, con lámparas encendidas y cantos que se repiten cada noche. Los muertos de la aldea, dicen, participan también, porque cada muerto es un poco Osiris.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en Osiris, que murió y volvió, y en los reyes muertos para los que hace las tumbas, que también volverán, dicen, si la tumba está bien hecha. Por eso apunta cada mecha y cada cincel: para que nada falle. Se toma su oficio como un rezo.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días las figuras de Osiris de barro empiezan a echar brotes verdes, y las mujeres de la aldea se las enseñan unas a otras, orgullosas.",
                "Mientras duran los ritos de Joiak se llora a Osiris en las capillas, y en la tumba la cuadrilla trabaja más callada.",
                "El último día de Joiak se levanta el pilar de Osiris, que es su espalda, y se canta que el dios está de pie otra vez. Mañana empieza la estación de la siembra.",
            )),
        ),
        "racion" to unDia(
            Parrafo.MANANA to "Hoy llegó el grano del mes, en barcas por el canal y luego en burros por el sendero, desde los graneros del templo de la otra orilla: trigo para el pan y cebada para la cerveza. {nombre} lo espera en la puerta del muro con la balanza, las pesas y una lasca nueva.",
            Parrafo.TRABAJO to "A esta hora pesa el grano y lo reparte casa por casa, según lo que le toca a cada uno: más al capataz y al escriba, menos al aguador. Cada saco se apunta. Cuando el grano no alcanza, que pasa, hay que escribirlo, y nadie quiere ser el que lo escribe.",
            Parrafo.ANOCHECER to "Esta noche la aldea huele a pan nuevo y a cerveza que empieza a fermentar. En los tejados se apilan los sacos y las mujeres los cuentan otra vez. {nombre} se sienta a comer con la paleta todavía manchada de la cuenta, cansado y en paz.",
        ),
        "huy_signos" to unDia(
            Parrafo.MANANA to "Hoy Huy copió sus primeros signos solo, en una lasca de caliza que su padre le trajo del Valle: un búho, un pan, una boca y un pájaro que no se parece a ningún pájaro. Se los enseñó a toda la calle antes de que su padre los viera.",
            Parrafo.GENTE to "{nombre} mira la lasca de Huy con cara de escriba, frunciendo la frente, y le corrige el pájaro con el dedo. Luego le dice que el búho está bien. El niño se va contento, a enseñárselo a Khonsu, que no sabe leer y le dice que es el mejor búho que ha visto.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su padre corrigiéndole a él el mismo pájaro, en la misma casa, con el mismo dedo. Piensa que Huy será escriba de la cuadrilla, y que hará la cuenta de tumbas de reyes que todavía no han nacido. La idea le da vértigo, como asomarse al pozo de una tumba.",
        ),
        "escorpion" to unDia(
            Parrafo.MANANA to "Hoy, en el paso del cerro, de madrugada, al poner la mano en una piedra para apoyarse, le picó un escorpión amarillo. {nombre} gritó, Khonsu aplastó al bicho con la sandalia y le ató la muñeca con una tira de lino, fuerte.",
            Parrafo.TRABAJO to "A esta hora está tumbado en la choza del paso con la mano hinchada como un pan y la fiebre subiendo, mientras el curandero de la cuadrilla le recita el conjuro contra los escorpiones sobre la herida: Isis curó a su hijo Horus, Isis te curará a ti. {nombre} repite cada palabra.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, con la cabeza dando vueltas, que mañana tendrá que apuntarse a sí mismo en la lista de ausencias: «picado por un escorpión». Le da risa en medio del dolor. Es lo que más ha escrito de otros, y nunca de él.",
        ),
        "sana_escorpion" to unDia(
            Parrafo.MANANA to "Hoy ya no tiene la mano hinchada, y la fiebre se fue en la noche. {nombre} se levanta, mueve los dedos, coge el pincel y escribe su nombre en una lasca. Le sale bien. Se ríe solo, y Tamit, desde la cocina, le pregunta de qué.",
            Parrafo.TRABAJO to "A esta hora vuelve a pasar lista a la entrada de la tumba, con la mano vendada, y la cuadrilla lo recibe con bromas sobre el escorpión, que en la boca de Khonsu ya es del tamaño de un perro. {nombre} apunta su propia vuelta: «trabajó».",
            Parrafo.GENTE to "Esta noche Tamit cuelga en la puerta de la casa un amuleto nuevo contra los escorpiones, una estela pequeña con Horus de niño pisando cocodrilos. Huy lo mira mucho rato y pregunta si Horus también tenía miedo. {nombre} le dice que sí, y que por eso ganó.",
        ),
        "aguador" to unDia(
            Parrafo.MANANA to "Hoy el aguador no llegó: está enfermo, dicen, con fiebre, en su casa de la otra orilla. La aldea no tiene pozo, y las jarras de las casas están medio vacías. Las mujeres se reúnen en la calle a contar cuánta agua le queda a cada una.",
            Parrafo.PUEBLO to "El capataz manda a dos muchachos con los burros de la cuadrilla al pozo grande, que está lejos, y el agua llega tarde y poca. Se bebe lo justo, se cocina con menos y no se lava nada. La aldea entera huele a sed.",
            Parrafo.GENTE to "Tamit le da a {nombre} la mitad de su agua para la subida, sin decirle que es la suya. Él lo sabe, porque la jarra de ella está vacía. Le devuelve la mitad de la mitad sin decir nada. Así se discute en la casa cuando hay poco.",
        ),
        "tamit_espera" to unDia(
            Parrafo.MANANA to "Hoy, antes de que saliera hacia el Valle, Tamit le dijo que espera un hijo. Se lo dijo en el patio, mientras le daba el pan, como quien dice que va a llover, aunque aquí nunca llueve. {nombre} se quedó con el pan en la mano y la paleta al hombro.",
            Parrafo.GENTE to "Esta tarde Tamit hizo la prueba de los granos, como hacen las mujeres de la aldea: orinó sobre trigo y cebada en dos bolsitas, y si brota la cebada es niño, si brota el trigo, niña. Su madre dice que la prueba nunca falla. Su madre dice muchas cosas.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en otro niño en la casa, en otra boca para la ración, en otro nombre para decir delante de los muertos. Hace cuentas en el sendero, y no le salen. Luego piensa en una niña, sin saber por qué una niña, y las cuentas le dan igual.",
        ),
        "visir" to unDia(
            Parrafo.MANANA to "Hoy vino el visir a ver cómo va la tumba del rey: cruzó el río en una barca con toldo, subió por el sendero en una silla de manos y entró en el Valle con su séquito, sus escribas y sus abanicos. La cuadrilla lo esperó formada a la entrada de la tumba.",
            Parrafo.TRABAJO to "A esta hora le enseña al visir el diario de la obra, el papiro con todo lo que se hizo, y el visir lo lee de pie, deprisa, saltándose cosas. Pregunta por las mechas. {nombre} tiene la cuenta exacta. El visir asiente y dice algo que su escriba apunta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el visir, que es el hombre más poderoso después del rey, leyendo su letra. Le tiemblan las piernas y le crece algo en el pecho. Esta noche, en la aldea, contará que el visir le preguntó por las mechas, y lo contará cada año, hasta que se muera.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "escriba_tumba", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + VALLE.take(1) + VALLE_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + VALLE.subList(1, 7) + VALLE_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + VALLE[7] + VALLE_PUEBLO,
            Parrafo.GENTE to GENTE + VALLE[8] + VALLE_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + VALLE[9] + VALLE_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
