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
 * El cuento de Jean-Baptiste, soldado de infantería, de la campaña de Rusia a su pueblo de Borgoña, de 1812 a 1816.
 * Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día es de marcha: dos tramos de camino, dos comidas (esos textos valen a cualquiera de sus horas), el vivac y el
 *   sueño al raso. Los textos de todos los días son los de campaña.
 * - Tiene cinco estados, cada uno con su mañana, su pueblo, su anochecer y un texto para cada cosa que hace: el cuartel
 *   (enero y febrero de 1812), Moscú, la retirada, el hospital de Prusia (invierno de 1813) y el pueblo, donde pasa casi
 *   tres de sus cinco años. En el pueblo los dos tramos de camino son la viña, y cambian con los meses.
 * - El pueblo de campaña lee el arco «compania» (cuántos de su compañía quedan).
 * - No repite lo que ya dice «Su vida»: la conscripción y el medio millón del Niemen, los kilómetros y los kilos, la
 *   ración del papel, Moscú en llamas.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG5.kt`):
 * - Lucien marcha con él hasta que se queda en la otra orilla del Berezina (`lucien`, temporada 1).
 * - El capitán Fabre manda la compañía hasta `regimiento` (temporada 2); el sargento Morel, desde entonces hasta
 *   `licencia` (3), y le enseña a leer desde `leer` (2). Le escribe desde Lyon desde `morel` (5).
 * - Margot, desde que la conoce (`margot`, temporada 3); prometidos desde `pide` (4); casados desde `boda` (5); espera
 *   un hijo desde `margot_espera` (5).
 * - Navidad vuelve los cinco años, en campaña y en el pueblo: su texto vale en los dos sitios. Las fiestas que vuelven
 *   cada año no nombran a Lucien, a Morel ni a Margot.
 */
internal object PiezasJeanBaptiste {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val INVIERNO = setOf(11, 12, 1, 2, 3)

    // =====================================================================
    //  En campaña (los textos de todos los días)
    // =====================================================================

    private val MANANA = listOf(
        v("A {nombre} lo despierta el tambor de la diana, en la oscuridad, cuando todavía faltan horas para que salga el sol. Se levanta del suelo con la manta pegada al cuerpo por el rocío, se pone el capote y la mochila, coge el fusil. Alrededor, cien hombres hacen lo mismo sin hablar."),
        v("{nombre} se despierta junto a las cenizas del fuego de la compañía, con un hombro dormido y la boca seca. Alguien ya sopla las brasas para calentar agua. El sargento pasa entre los dormidos tocándolos con la punta de la bota. Hay que irse."),
        v("Lo primero que hace {nombre} al despertarse es mirarse los pies: si están bien, el día irá bien. Los envuelve en trapos limpios, si los tiene, se pone las botas, duras de barro seco, y aprieta los cordones. Un soldado sin pies, dicen los veteranos, es un soldado muerto."),
        v("A {nombre} lo despierta un caballo que pasa al galope junto al vivac, un correo con órdenes. Se incorpora. Todavía es de noche. Las hogueras de todo el ejército brillan en el campo hasta donde se ve, miles, como un pueblo enorme que dentro de una hora se va a mover."),
        v("{nombre} se despierta con la lluvia en la cara, porque se durmió bajo un carro que no tapaba nada. Tiene el capote empapado y los cartuchos, por suerte, secos en la cartuchera. Se sacude como un perro. Nadie se queja: quejarse no seca."),
        v("{nombre} se despierta antes que la diana, como casi siempre, y se queda un momento quieto, mirando las estrellas que se apagan. Piensa en su madre, que a esta hora estará encendiendo el fuego en el pueblo. Luego suena el tambor y deja de pensar."),
        v("A {nombre} lo despierta Lucien, que ya está de pie, orinando contra un árbol y cantando una canción del pueblo, que es el mismo pueblo de los dos. Le tira una piña a la cabeza. Hay que irse. Lucien siempre se despierta contento. {nombre} no entiende cómo.", hasta = "lucien"),
        v("{nombre} se despierta con escarcha en el bigote y la manta tiesa. Mueve los dedos de los pies dentro de las botas, uno por uno, hasta que los siente. Alrededor, los hombres se levantan despacio, como viejos, golpeándose los brazos.", meses = setOf(12, 1, 2)),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En campaña el tiempo no es tema de conversación: es el enemigo que no se ve."),
        v("{nombre} mira el cielo en un alto de la marcha, sentado en la mochila. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los veteranos dicen que el sol es para los oficiales, la lluvia para los soldados y la nieve para los muertos."),
        v("{cielo} En {estacion} los caminos cambian, y {nombre} lo sabe por las botas: polvo, barro, hielo, o barro otra vez."),
        v("Desde la columna se ve el campo de un país que no es el suyo, con su cielo, que tampoco. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Su madre decía que el tiempo de la viña lo manda Dios. El de la guerra, piensa {nombre}, también."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, y el sol brilla en todo lo que es de metal.",
            "Hace un día claro, de esos en que se ven las colinas lejanas y el humo de los pueblos.",
            "No hay una nube, y el sol pega fuerte desde temprano.",
        ),
        Clima.NUBES to listOf(
            "El cielo está gris y bajo, y la luz no hace sombras.",
            "Hay nubes grandes que corren, y sus sombras pasan por el campo.",
            "Está nublado, de un gris de plomo que no se decide.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y los caminos se convierten en barro hasta los tobillos.",
            "Cae una lluvia fina y fría que se mete por el cuello y no se va.",
            "Llueve a cántaros, y el agua corre por todas partes buscando dónde meterse.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos suenan como cañones, y los caballos se espantan.",
            "Cae un aguacero con rayos que deja el aire oliendo a hierro.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y el mundo se queda blanco y callado.",
            "Cae nieve fina y dura que el viento mete en los ojos.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, y no se ve más allá de veinte pasos.",
            "La niebla tapa el campo, y cualquier árbol parece un hombre.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento que levanta los capotes y se lleva los sombreros.",
            "Hace viento, y todo cuesta el doble.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando los caminos se secan y los campos se ponen verdes.",
            "En primavera los ríos bajan crecidos y los días se alargan.",
            "Es el tiempo de los brotes nuevos y del barro que se vuelve polvo.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, el tiempo del polvo, de la sed y del trigo maduro.",
            "En verano los días son largos y las noches, cortas y calientes.",
            "Es el tiempo de las moscas, de las tormentas y de los días largos.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando llueve y los caminos se ponen imposibles.",
            "En otoño las noches se alargan y el fuego se agradece.",
            "Es el tiempo de las vendimias y de las primeras nieblas.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, el tiempo del frío que no deja dormir.",
            "En invierno los días no terminan de empezar y ya se acaban.",
            "Es el tiempo de la escarcha, de los pies helados y de las noches largas.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora marcha en la columna, a paso de camino, por una carretera que no se acaba, con la mochila tirando de los hombros y el fusil cambiando de hombro cada hora. Delante, la espalda del de delante. Detrás, el aliento del de detrás. Se cuentan los pasos para no pensar.", hacer = Hacer.CAMINO),
        v("A esta hora marcha cantando con los demás, para llevar el paso y para no pensar: canciones de soldados, canciones de borrachos, una que habla de una muchacha que espera. Cuando se acaba una, alguien empieza otra. Los sargentos no cantan, pero llevan el ritmo con la cabeza.", hacer = Hacer.CAMINO),
        v("A esta hora hace alto con la compañía en la cuneta, unos minutos cada hora, como manda el reglamento: se sienta en la mochila, se quita el chacó, bebe un trago de la cantimplora. Nadie habla. Luego el tambor, y otra vez de pie. Cada vez cuesta más levantarse.", hacer = Hacer.CAMINO),
        v("A esta hora hace la instrucción en un prado con la compañía: cargar, apuntar, fuego, calar la bayoneta, formar el cuadro. Se repite hasta que se hace sin pensar, porque en la batalla no se piensa. El sargento grita. {nombre} ya no se equivoca.", hacer = Hacer.CAMINO),
        v("A esta hora marcha por un pueblo que no conoce, de casas cerradas y gente que mira desde las ventanas sin saludar. Los perros le ladran a la columna. Un niño corre a su lado un rato, imitando el paso, hasta que su madre lo llama. {nombre} le guiña un ojo.", hacer = Hacer.CAMINO),
        v("A esta hora está de guardia en un puesto avanzado, delante del campamento, con otros tres, mirando un bosque donde puede haber enemigos o nada. No se puede fumar ni hacer fuego. Se oyen pájaros. Cada ruido es un enemigo hasta que es un pájaro.", hacer = Hacer.CAMINO),
        v("A esta hora marcha bajo un sol que pega en el chacó como en una olla, con la camisa pegada a la espalda y la cantimplora vacía desde hace horas. Hay hombres que caen en la cuneta y se quedan ahí. Los carros de atrás los recogen, o no.", hacer = Hacer.CAMINO, meses = setOf(6, 7, 8)),
        v("A esta hora marcha por un camino de barro helado que se rompe bajo las botas, con el viento de cara y la nariz que ya no siente. Se marcha con la cabeza baja, mirando los talones del de delante. Hablar cuesta calor. No habla nadie.", hacer = Hacer.CAMINO, meses = INVIERNO),

        v("A esta hora come lo que hay en la mochila, de pie o sentado en una piedra: un pedazo de pan duro, una cebolla, un trozo de tocino si queda. Lo corta con la navaja y se lo come despacio, mirando el campo. En el papel le tocan carne y vino todos los días. El papel no se come.", hacer = Hacer.COMER),
        v("A esta hora come la sopa de la compañía, hecha en una marmita para ocho con lo que se encontró: patatas, coles, un pollo que no era de nadie, agua del río. Cada uno mete su cuchara por turnos. El primero que se queja hace la próxima.", hacer = Hacer.COMER),
        v("A esta hora come galleta de munición, dura como una teja, mojada en un trago de aguardiente que le cambió a una cantinera por un botón de plata. Comer, en campaña, es una cosa que se hace cuando se puede y con lo que se puede.", hacer = Hacer.COMER),

        v("A esta hora monta el vivac con los de su rancho: buscar leña, encender el fuego, colgar la marmita, cortar ramas para dormir encima y no en el barro. Cada uno tiene su tarea. La suya es el agua: baja al río con seis cantimploras colgadas y vuelve con cinco llenas y una perdida.", hacer = Hacer.CUIDAR),
        v("A esta hora sale a buscar comida con otros cuatro por las granjas de alrededor: gallinas, patatas, pan, lo que haya. A veces se paga. Casi nunca. Los campesinos los miran con odio. {nombre} piensa en los campesinos de su pueblo y no puede mirarlos a la cara.", hacer = Hacer.CUIDAR),
        v("A esta hora limpia el fusil junto al fuego, pieza por pieza, con un trapo y un poco de grasa, y cose un botón del capote que se le cayó en la marcha. Luego se cura las ampollas de los pies con sebo. Un soldado es un hombre que se pasa la vida arreglando cosas.", hacer = Hacer.CUIDAR),

        v("A esta hora duerme al raso, envuelto en el capote y la manta, con la mochila de almohada y el fusil al lado, entre los demás, cerca del fuego que se apaga. La tierra está dura. No importa: después de un día de marcha se duerme encima de una piedra.", hacer = Hacer.DORMIR),
        v("A esta hora duerme y sueña con la viña de su madre, con la uva madura, con el olor del lagar. Se despierta con frío y vuelve a dormirse. A su lado alguien llora en sueños. Por la mañana nadie hablará de eso.", hacer = Hacer.DORMIR),
        v("A esta hora duerme en un granero con media compañía, sobre la paja, apretados como sardinas, calientes por primera vez en días. Huele a vaca y a hombre. Hay ratas. {nombre} duerme como un niño.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("{entorno} La compañía son unos ciento cuarenta hombres, o lo eran al salir: un capitán, un teniente, sargentos, cabos, un tambor y los soldados, de todas partes de Francia y de fuera. Se marcha juntos, se come juntos, se duerme juntos. Es lo más parecido a una familia que tiene ahora."),
        v("El ejército en marcha ocupa el camino entero durante horas: la infantería, la caballería, la artillería con sus caballos, los carros, las cantineras, los rezagados. Desde una colina parece un río de hombres. Desde dentro, es la espalda del de delante. {entorno}"),
        v("{entorno} En el regimiento hay hombres que hablan bretón, alemán, italiano, flamenco, y franceses de todas partes que no se entienden entre ellos. Se entienden en las órdenes y en las canciones. Al emperador, dicen, lo entienden todos."),
        v("Las cantineras van con el regimiento en un carro con toldo, vendiendo aguardiente, tabaco, pan y agujas, y lavando ropa y curando heridas por unas monedas. Hay una, la Grande Louise, que conoce a todos por su nombre y los llama hijos. {entorno}"),
        v("{entorno} Los veteranos de la compañía, los que estuvieron en Austerlitz y en España, tienen la cara curtida y no hablan de lo que vieron. Los nuevos les preguntan. Ellos se ríen y cambian de tema. {nombre} ya no pregunta."),
        v("Detrás de la columna van los carros de los heridos y de los enfermos, que se quejan con cada bache, y detrás de los carros, los rezagados, que caminan como pueden. Detrás de los rezagados, dicen, no hay nada. {entorno}"),
    )

    /** Cuántos quedan de su compañía (arco «compania»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("compania", 0.8, 1.0, frases = listOf(
            "La compañía va casi entera, y por la noche hay tantos fuegos como ranchos.",
            "Todavía están casi todos los que salieron, y se pasa lista sin silencios.",
        )),
        Entorno("compania", 0.5, 0.8, frases = listOf(
            "A la compañía le faltan ya muchos: muertos, heridos, enfermos, perdidos por el camino.",
            "Cuando se pasa lista hay nombres que se quedan sin contestar, y el sargento los dice igual.",
        )),
        Entorno("compania", 0.0, 0.5, frases = listOf(
            "De la compañía quedan unos pocos, y caben todos alrededor de un solo fuego.",
            "Se pasa lista y casi nadie contesta. El sargento ya no lee los nombres de los que faltan.",
        )),
    )

    private val GENTE = listOf(
        v("Su madre vive en el pueblo, en Borgoña, en la casa de piedra con la parra en la puerta, y trabaja sola la viña de la familia, que es pequeña y empinada. Viuda desde hace años. No sabe escribir. Le manda recados con los que vuelven: que coma, que rece, que vuelva."),
        v("Claudine, su hermana pequeña, lloró tanto cuando él se fue que el cura tuvo que llevársela de la plaza. Le cosió en el forro del capote una medalla de la Virgen y un mechón de su pelo. {nombre} los toca a veces, por fuera del paño, sin que lo vean."),
        v("Los de su rancho son ocho, y comparten la marmita, el fuego y la paja: un bretón que no habla francés, un parisino que habla por todos, dos hermanos de Alsacia, un viejo de Italia, y otros que cambian cuando caen. Se pelean todo el día. Se defenderían con la vida."),
        v("El emperador pasó una vez a caballo junto a la compañía, a diez pasos, con su abrigo gris y su sombrero, sin mirar a nadie. Los hombres gritaron viva el emperador hasta quedarse roncos. {nombre} también. Luego se sintió raro, como después de beber."),
        v("El tambor de la compañía es un muchacho de quince años, Petit-Jean, que toca la diana, la marcha, el paso de carga y la retirada, y que en la batalla va delante, sin fusil, tocando. Todos lo cuidan como a un hermano pequeño. Él no se deja cuidar."),
        v("Lucien es de su mismo pueblo, el hijo del herrero, y se alistaron el mismo día. Se lo cuenta todo, hasta lo que no se cuenta. Si uno cae, el otro lo lleva: eso se juraron en la taberna del pueblo, borrachos, la noche antes de irse.", hasta = "lucien"),
        v("Lucien se quedó en la otra orilla del Berezina, cuando quemaron los puentes. {nombre} no sabe si murió allí, en el agua, en la nieve, o prisionero de los rusos. No lo sabrá nunca. Tiene que decírselo al herrero, su padre. No sabe cómo.", desde = "lucien", hasta = "vuelve_pueblo"),
        v("{nombre} piensa todavía en Lucien, que se quedó en la otra orilla del Berezina. Se lo contó a su padre, el herrero, el día que volvió al pueblo, y el herrero no dijo nada: siguió golpeando el hierro. Desde entonces, cuando pasa por la herrería, lo saluda con la mano.", desde = "vuelve_pueblo"),
        v("El capitán Fabre empezó de soldado, como ellos, con la Revolución, y subió a fuerza de batallas. Duerme en el suelo con sus hombres y come lo mismo que ellos. Por eso lo siguen. Por eso y porque no tiene miedo.", hasta = "regimiento"),
        v("El sargento Morel es un veterano de Egipto y de España, flaco, con una cicatriz que le cruza la cara, que sabe leer y escribir y no se lo dice a nadie. Le ha tomado cariño a {nombre}, porque no se queja. No se lo dice. Le da tabaco.", desde = "regimiento", hasta = "leer"),
        v("El sargento Morel le enseña a leer en las horas muertas, con un trozo de carbón y la tapa de una caja: primero las letras, luego su nombre, luego las proclamas del emperador. {nombre} aprende despacio y con rabia. Morel dice que tiene la cabeza dura, y que eso es bueno.", desde = "leer", hasta = "licencia"),
        v("Margot, la hija del tonelero, le dijo cuando se fue otra vez que lo esperaría, o que a lo mejor no, según lo que tardara. {nombre} la piensa en cada alto del camino. Sabe que no lo dijo en serio. Lo de no esperarlo.", desde = "margot"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de los veteranos, que lo aprendieron en otras guerras."),
        v("En el ejército cada cosa tiene su nombre, su sitio y su castigo si se pierde. {dato_objeto}"),
        v("{dato_objeto} Son cosas que no se olvidan, aunque se quiera."),
        v("{dato_objeto} Un soldado lleva su casa a la espalda, y la casa pesa."),
        v("Un soldado tiene lo que lleva encima, y nada más. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("fusil", "el fusil", "un fusil", "El fusil",
            "El fusil de infantería mide casi lo que un hombre, pesa como un niño de dos años y dispara una bala de plomo del tamaño de una cereza; se carga por la boca, en muchos pasos que se aprenden a fuerza de repetir; a cien pasos se acierta, a doscientos se reza."),
        ObjetoCuento("bayoneta", "la bayoneta", "una bayoneta", "La bayoneta",
            "La bayoneta es una hoja de tres filos que se encaja en la boca del fusil; con ella el fusil se convierte en una lanza; en las cargas se usa poco, porque casi siempre uno de los dos corre antes; en el vivac sirve para asar carne."),
        ObjetoCuento("mochila", "la mochila", "una mochila", "La mochila",
            "La mochila es de piel de vaca con el pelo por fuera, cuadrada, con la manta enrollada encima; dentro van la camisa de repuesto, los zapatos, el cepillo, la cuchara, la comida de cuatro días y lo que cada uno quiere llevar, que con los días es cada vez menos."),
        ObjetoCuento("capote", "el capote", "un capote", "El capote",
            "El capote es un abrigo largo de paño gris o pardo que llega a las pantorrillas; de día abriga, de noche es la cama, y cuando llueve se empapa y pesa el doble; en Rusia se le cosieron pieles por dentro, los que tenían pieles."),
        ObjetoCuento("chaco", "el chacó", "un chacó", "El chacó",
            "El chacó es un sombrero alto de fieltro y cuero, con visera, una placa de latón delante y un pompón; protege poco del sable y nada de la lluvia; los soldados guardan dentro la pipa, el tabaco y las cartas."),
        ObjetoCuento("cantimplora", "la cantimplora", "una cantimplora", "La cantimplora",
            "La cantimplora es una botella de lata o una calabaza forrada de cuero, que se lleva colgada; cuando hay vino lleva vino, cuando hay aguardiente lleva aguardiente, y casi siempre lleva agua de algún arroyo, que hay que beber sin mirarla."),
        ObjetoCuento("galleta", "la galleta", "una galleta de munición", "La galleta",
            "La galleta de munición es pan cocido dos veces, plano y duro, que no se pudre; se rompe con la culata del fusil y se moja en la sopa; un soldado lleva varias en la mochila para cuando no llega el pan, que es casi siempre."),
        ObjetoCuento("pipa", "la pipa", "una pipa", "La pipa",
            "La pipa de barro blanco tiene el tubo largo y se rompe por cualquier cosa, y por eso los soldados la llevan corta; se fuma en los altos, en el vivac, en las guardias; el tabaco es lo primero que se pide a un compañero y lo último que se le niega."),
        ObjetoCuento("carta", "la carta", "una carta", "La carta",
            "Las cartas del ejército van con los correos, en sacos, durante semanas o meses; muchos soldados no saben escribir y se las escribe un compañero o un sargento por unas monedas; las madres que no saben leer se las hacen leer al cura."),
        ObjetoCuento("cuchara", "la cuchara", "una cuchara", "La cuchara",
            "La cuchara de estaño o de madera se lleva en la mochila o metida en el chacó, y es lo más importante después del fusil: sin cuchara no se come de la marmita del rancho; un soldado que pierde la cuchara come con los dedos, y quema."),
        ObjetoCuento("botas", "las botas", "unas botas", "Las botas",
            "Las botas de munición son en realidad zapatos de cuero hasta el tobillo, iguales para el pie derecho y para el izquierdo, con suela de clavos, que se cubren con polainas; con la lluvia se ablandan, con el frío se endurecen; un ejército, dicen, marcha sobre sus pies."),
        ObjetoCuento("manta", "la manta", "una manta", "La manta",
            "La manta es de lana gris, áspera, y va enrollada encima de la mochila; de noche se duerme envuelto en ella con el capote encima; en campaña se vende, se cambia, se roba y se presta, y en Rusia valía más que una cuchara de plata."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas lo justo, entre paso y paso. A sus {edad} años sabe que un soldado no decide nada: ni adónde va, ni cuándo come, ni cuándo muere. Decide si se levanta por la mañana. Siempre se levanta."),
        v("Piensa a veces en la conscripción, en el día en que sacó un número malo en la plaza del ayuntamiento, delante de todo el pueblo, y en su madre, que no dijo nada y le preparó la ropa. El hijo de un vecino rico pagó a otro para que fuera en su lugar. Su madre no tenía con qué."),
        v("{mundo} {nombre} piensa en el emperador, que lo sabe todo y lo puede todo, dicen, y que una vez pasó a diez pasos de él. Piensa que un hombre así no se puede equivocar. Luego piensa en los muertos del camino, y no termina el pensamiento."),
        v("{mundo} A veces, de noche, junto al fuego, {nombre} cuenta los pueblos por los que ha pasado y no se acuerda de ninguno: todos tienen una iglesia, una fuente y gente que mira con miedo. Piensa que en su pueblo mirarían igual a un ejército extranjero."),
        v("Piensa en la vendimia del pueblo, que ya pasó o está por llegar, ya no lo sabe bien; en el olor del mosto, en las manos moradas, en las canciones del lagar. Le parece la vida de otro. Le gustaría volver a ser ese otro."),
        v("{mundo} A sus {edad} años, {nombre} ha andado más que su padre y su abuelo juntos, ha visto ríos que no sabía que existían y ha disparado a hombres que no conocía. Nadie en el pueblo lo entenderá cuando vuelva. Si vuelve. Por eso no piensa en volver. Casi."),
    )

    private val MUNDOS = mapOf(
        "rusia" to listOf(
            "Se habla del emperador y del zar, que antes eran amigos y ahora no.",
            "Se dice que nunca se juntó un ejército tan grande, de tantos países.",
            "Se habla de España, donde la guerra no se acaba nunca y a los franceses los matan en los caminos.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde la columna se para en un campo y en un momento el campo es un pueblo: fuegos, marmitas, caballos, gritos, las tiendas de los oficiales. {nombre} deja la mochila, se quita las botas y se queda un rato mirándose los pies, que humean. Luego va a por leña."),
        v("Anochece, y las hogueras del ejército se encienden una tras otra hasta el horizonte, miles, como si las estrellas se hubieran caído al campo. Alguien toca un violín. Alguien canta en otra lengua. {nombre} se envuelve en el capote cerca del fuego y escucha."),
        v("Al caer la tarde se pasa lista. El sargento lee los nombres a la luz de un farol, y cada uno contesta: presente. Cuando uno no contesta, se hace un silencio corto y el sargento sigue. {nombre} contesta fuerte, para que se oiga bien que está."),
        v("De noche, en el vivac, los veteranos cuentan historias de otras campañas: de Italia, donde había vino y muchachas; de Egipto, donde había arena y pirámides; de España, donde había curas con escopetas. {nombre} escucha y fuma una pipa prestada."),
        v("La última luz del día se queda en los cañones de los fusiles puestos en pabellón, que brillan rojos un momento y luego se apagan. {nombre} mira el cielo del oeste, donde está Francia. Su pueblo está allí, debajo de alguna de esas nubes. Lo busca. No lo encuentra."),
        v("Anochece, y los que están de guardia salen hacia los puestos, y los demás se tumban junto al fuego. Se habla bajito. Un caballo relincha. A lo lejos, muy lejos, se oye un cañonazo, uno solo, y luego nada. Nadie dice nada. {nombre} cierra los ojos."),
    )

    // =====================================================================
    //  Los estados
    // =====================================================================

    /** El cuartel, en enero y febrero de la temporada 1 (estado «cuartel»). */
    private fun cu(texto: String, hacer: Hacer? = null) = v(texto, estado = "cuartel", hacer = hacer)

    private val CUARTEL = listOf(
        cu("{nombre} se despierta en el dormitorio del cuartel, en una cama de paja que comparte con otro, como manda el reglamento, en una sala con cuarenta camas y una estufa. Huele a humo, a cuero y a pies. El tambor toca la diana en el patio. Fuera hace frío."),
        cu("A {nombre} lo despierta el cabo gritando los nombres de los que hacen la guardia hoy. Él no está. Se da la vuelta. Lucien, en la cama de al lado, ya está despierto, sacándole brillo a las botas nuevas como si fueran de un general."),
        cu("{nombre} se despierta antes de la diana y mira por la ventana del dormitorio el patio del cuartel, con la nieve sucia barrida contra las paredes y los centinelas golpeando los pies contra el suelo. Hoy, como ayer, instrucción. Se dice que en primavera, campaña."),
    )

    private val CUARTEL_TRABAJO = listOf(
        cu("A esta hora hace la instrucción en el patio del cuartel, con la nieve barrida a los lados: marchar, girar, cargar, apuntar, presentar armas, cien veces. Se dice que en primavera habrá campaña, muy lejos. El sargento dice que se instruyan como si fuera mañana.", Hacer.CAMINO),
        cu("A esta hora sale con la compañía a una marcha de entrenamiento por los caminos de alrededor, con la mochila cargada, ida y vuelta, para hacer piernas. Los nuevos se quejan. Los veteranos se ríen de los nuevos y no se quejan, porque saben lo que viene.", Hacer.CAMINO),
        cu("A esta hora come en el cuartel la sopa de la compañía, con pan de munición, un poco de carne y un cuarto de vino, en escudillas de hojalata, sentado en el banco largo. Es la mejor comida que ha tenido en años. Lucien dice que solo por esto valía la pena.", Hacer.COMER),
        cu("A esta hora limpia su equipo en el dormitorio, para la revista de mañana: el fusil, con aceite; las correas, con blanco; los botones, con ceniza; las botas, con sebo. Un soldado brilla, dice el capitán Fabre, o se queda sin permiso.", Hacer.CUIDAR),
        cu("A esta hora duerme en el cuartel, en la cama de paja compartida, bajo dos mantas, con la estufa que se apaga. Por las ventanas entra el frío y el ruido de la ciudad. Sueña con el pueblo. Se despierta con los ronquidos del de al lado y se vuelve a dormir.", Hacer.DORMIR),
    )

    private val CUARTEL_PUEBLO = listOf(
        cu("El cuartel es un convento viejo que la Revolución les quitó a las monjas, con un patio grande, cuadras en el claustro y dormitorios donde dormían ellas. En las paredes todavía hay santos pintados, con bigotes de carbón que les pusieron los soldados."),
        cu("La ciudad del cuartel está llena de soldados este invierno, más que nunca: llegan regimientos de todas partes, se van hacia el este, llegan otros. Se dice que se prepara algo grande. Los comerciantes suben los precios y las madres esconden a las hijas."),
    )

    private val CUARTEL_ANOCHECER = listOf(
        cu("Al caer la tarde, con permiso hasta la retreta, {nombre} y Lucien van a una taberna de la ciudad, beben un vino malo, miran a las muchachas que no los miran, y vuelven corriendo cuando suena el tambor. Lucien se ríe todo el camino."),
        cu("Anochece en el cuartel y se cierran las puertas. En el dormitorio, a la luz de una vela, los soldados juegan a las cartas, cosen, escriben cartas los que saben, o se las hacen escribir. {nombre} le dicta a un cabo una para su madre."),
    )

    private val CUARTEL_TIEMPO = listOf(
        cu("{cielo} {de_estacion} Desde la ventana del dormitorio se ve el patio del cuartel y, encima, un cuadrado de cielo."),
        cu("En el patio del cuartel los centinelas miran el cielo como si les fuera a dar órdenes. {cielo} {de_estacion}"),
    )

    /** Moscú, de septiembre a octubre de la temporada 1 (estado «moscu»). */
    private fun mo(texto: String, hacer: Hacer? = null) = v(texto, estado = "moscu", hacer = hacer)

    private val MOSCU = listOf(
        mo("{nombre} se despierta en el suelo de un palacio de Moscú, sobre una alfombra de seda, entre cuadros dorados y espejos rotos, con un candelabro de plata en la mochila que no sabe para qué quiere. Fuera, la ciudad quemada humea todavía. Huele a ceniza."),
        mo("A {nombre} lo despierta Lucien con un vaso de cristal lleno de un licor dulce que encontró en una bodega, y una lata de té de la China. Se lo beben sentados en una escalera de mármol, mirando las ruinas negras de enfrente. Lucien dice que son ricos. No tienen pan."),
        mo("{nombre} se despierta y no sabe dónde está: un techo pintado con santos dorados, una cama con cortinas, frío. Luego se acuerda: Moscú. El ejército vive en lo que quedó de la ciudad, esperando a que el zar pida la paz. El zar no la pide."),
    )

    private val MOSCU_TRABAJO = listOf(
        mo("A esta hora busca con los de su rancho por las casas que no se quemaron, en los sótanos, en las iglesias: comida, ropa de abrigo, vino, lo que sea. Encuentran pieles, plata, libros en una lengua que nadie lee y casi nada de comer. Se llevan las pieles.", Hacer.CAMINO),
        mo("A esta hora hace guardia en una plaza de Moscú llena de escombros y de caballos muertos, junto a una iglesia con cúpulas de oro. Pasan soldados de todas las naciones cargados de cosas. Un viejo ruso, sentado en una piedra, los mira pasar sin moverse.", Hacer.CAMINO),
        mo("A esta hora busca leña entre las ruinas, que es lo único que sobra en Moscú: vigas quemadas, muebles, puertas talladas, marcos de cuadros. La lleva a la casa donde vive la compañía en un carrito de niño que encontró. Lucien empuja. Se ríen como niños.", Hacer.CAMINO),
        mo("A esta hora come lo que encontraron: mermelada, azúcar, pescado salado, vino de Francia de las bodegas de los nobles, pan duro. Se come mucho y mal, como en una boda de pobres. Lucien dice que en el pueblo no se lo van a creer.", Hacer.COMER),
        mo("A esta hora cose en el capote unas pieles que encontró en un sótano, para el invierno, porque los veteranos dicen que el invierno aquí no es como en Francia. {nombre} cose mal. Un veterano lo mira, se ríe, le quita la aguja y se lo cose él.", Hacer.CUIDAR),
        mo("A esta hora duerme en una casa de madera que no se quemó, con media compañía, sobre alfombras y abrigos de pieles robados. Por la ventana se ven las ruinas a la luz de la luna, negras, y alguna casa que todavía humea, sola.", Hacer.DORMIR),
    )

    private val MOSCU_PUEBLO = listOf(
        mo("Moscú es una ciudad enorme, de iglesias con cúpulas de colores y palacios de madera y de piedra, y está casi toda quemada: barrios enteros de chimeneas negras, de paredes caídas, de ceniza. Los rusos se fueron antes de que llegaran. Solo quedaron unos pocos."),
        mo("En lo que queda de Moscú el ejército vive como en una feria de ladrones: soldados que venden lo que robaron a otros soldados, cantineras con joyas, oficiales en carrozas de nobles. Todo es de todos. Nada sirve para comer."),
        mo("En el centro de Moscú está el Kremlin, una ciudad dentro de la ciudad, con murallas rojas y torres, donde vive el emperador. Desde fuera se ven las cúpulas doradas. Los soldados lo miran de lejos y se preguntan qué piensa allá dentro."),
    )

    private val MOSCU_ANOCHECER = listOf(
        mo("Al caer la tarde, en Moscú, los soldados encienden fuegos dentro de los palacios, con los muebles, y se sientan alrededor en sillas doradas. Alguien toca un piano que encontró. Desafina. Nadie se ríe. Cada noche hace un poco más de frío."),
        mo("Anochece sobre Moscú, y la ciudad quemada es negra contra el cielo, con sus cúpulas que todavía brillan. {nombre} la mira desde una ventana rota. Piensa que el fin del mundo debe de ser así: oro y ceniza, y un silencio enorme."),
        mo("De noche, en Moscú, los veteranos hablan bajo: de que el zar no va a pedir la paz, de que el invierno se acerca, de que están muy lejos de casa. Los nuevos no escuchan. Lucien canta. {nombre} escucha a los veteranos."),
    )

    private val MOSCU_TIEMPO = listOf(
        mo("{cielo} {de_estacion} En Moscú los veteranos miran el cielo todas las mañanas, buscando la primera nieve."),
        mo("Sobre las ruinas de Moscú el cielo es enorme. {cielo} {de_estacion}"),
    )

    /** La retirada de Rusia, de octubre a diciembre de la temporada 1 (estado «retirada»). */
    private fun re(texto: String, hacer: Hacer? = null, meses: Set<Int>? = null, hasta: String? = null) = v(texto, estado = "retirada", hacer = hacer, meses = meses, hasta = hasta)

    private val NIEVE = setOf(11, 12)

    private val RETIRADA = listOf(
        re("{nombre} se despierta junto a un fuego que se apagó, con el cuerpo dolorido y el estómago vacío. Alrededor, miles de hombres se levantan para seguir andando hacia el oeste, por el mismo camino por el que vinieron, que ya está pelado. Nadie habla. Se anda."),
        re("A {nombre} lo despierta un disparo cerca: los cosacos rondan el vivac de noche y se llevan a los que duermen lejos del fuego. Lucien ya está de pie con el fusil. Esperan. No pasa nada más. Se van con la primera luz.", hasta = "lucien"),
        re("{nombre} se despierta con la nieve encima, junto a un fuego apagado, entre hombres que no se levantan. Se levanta él. Siempre se levanta. Se sacude la nieve, busca el fusil, mira hacia el oeste. Hay que andar.", meses = NIEVE),
        re("{nombre} se despierta con los pies que no siente y la barba helada, y lo primero que hace es golpearse los pies con la culata hasta que le duelen, que es buena señal. Al lado, un caballo muerto que anoche estaba vivo. Ya le han cortado la carne.", meses = NIEVE),
    )

    private val RETIRADA_TRABAJO = listOf(
        re("A esta hora anda hacia el oeste en una columna que ya no es columna: soldados de todos los regimientos mezclados, carros, mujeres, heridos, caballos que caen y no se levantan. Se anda sin orden, sin tambor, mirando el suelo. El que se para, se queda.", Hacer.CAMINO),
        re("A esta hora anda por un camino lleno de cosas tiradas: cofres abiertos, cuadros, candelabros, carrozas rotas, todo lo que se robó en Moscú y ya nadie puede cargar. {nombre} tiró el candelabro de plata hace días. Se quedó las pieles.", Hacer.CAMINO),
        re("A esta hora anda detrás de los que quedan de su compañía, que son pocos, contando las cabezas cada rato para saber que siguen ahí. Los cosacos aparecen a lo lejos, por los lados, y desaparecen. Se aprieta el paso. Se cuenta otra vez.", Hacer.CAMINO),
        re("A esta hora anda en la nieve, que le llega a las rodillas, con trapos en los pies y una piel encima del capote, detrás de alguien que también anda. El viento le corta la cara. No siente las manos. Cuenta los pasos hasta cien y vuelve a empezar.", Hacer.CAMINO, NIEVE),
        re("A esta hora come carne de caballo, asada en el fuego en la punta de la bayoneta, medio cruda, sin sal, con pólvora por encima para que sepa a algo. Hace días que no hay otra cosa. Alguien dice que sabe a buey. No sabe a buey.", Hacer.COMER),
        re("A esta hora come un puñado de harina cocida con agua en la tapa de la marmita, y lo que haya: una patata helada, un nabo, un trozo de caballo. Lo come de espaldas a los demás, rápido, para no tener que compartir. Le da vergüenza. Come igual.", Hacer.COMER),
        re("A esta hora busca leña para el fuego entre los carros rotos y las casas quemadas, y se la disputa a otros que también la buscan. Al final tiene un brazado de ruedas y tablas. El fuego de su rancho es pequeño y lo rodean demasiados. Se sientan muy juntos.", Hacer.CUIDAR),
        re("A esta hora se envuelve los pies en trapos nuevos, de una camisa de seda que encontró, y se ata los zapatos con cordel, porque las suelas ya no aguantan. A un muerto que calzaba lo mismo le quita los suyos. No le pide perdón. Ya no se pide perdón.", Hacer.CUIDAR),
        re("A esta hora duerme sentado junto al fuego, sin tumbarse, porque el que se tumba en el suelo helado no se levanta, dicen. Duerme a ratos, con el fusil entre las piernas. Cada vez que abre los ojos, el fuego es más pequeño y hay menos gente alrededor.", Hacer.DORMIR),
        re("A esta hora duerme apretado contra otros en lo que queda de una granja, sin techo, con un caballo muerto a la espalda para el viento. Sueña con pan. No con el pueblo, ni con su madre: con pan, caliente, entero.", Hacer.DORMIR),
    )

    private val RETIRADA_PUEBLO = listOf(
        re("{entorno} El camino de la retirada está sembrado de muertos y de caballos, de carros volcados, de cañones abandonados, de fuegos apagados. Por los lados, a lo lejos, se ven jinetes: los cosacos, que siguen a la columna como los lobos al rebaño."),
        re("{entorno} Los pueblos del camino de vuelta están quemados, los que quemaron ellos mismos al venir. No hay nada que comer en ellos. Los que se apartan del camino a buscar algo no vuelven."),
        re("Del ejército que entró en Rusia quedan pedazos que andan: regimientos sin coronel, compañías sin capitán, soldados sin regimiento. Las águilas van envueltas en trapos. Nadie manda. Todos van hacia el mismo sitio. {entorno}"),
    )

    private val RETIRADA_ANOCHECER = listOf(
        re("Al caer la tarde, cada grupo busca un sitio para el fuego, y se pelea por él. Los que no tienen fuego se acercan a los fuegos de otros y piden sitio. A veces se lo dan. A veces no. {nombre} le hace sitio a uno. Por la mañana el uno ya no está."),
        re("De noche, en la retirada, se oyen los lobos detrás de la columna, y los cosacos, que gritan en la oscuridad para que nadie duerma. {nombre} se pega a los demás. Mira las estrellas, que en Rusia están muy lejos."),
        re("Anochece pronto, y con la noche llega el frío de verdad, que no se parece a nada. Los hombres se meten dentro de los caballos muertos para dormir. {nombre} lo ha visto. No lo ha hecho. Todavía.", meses = NIEVE),
        re("Al anochecer, los que quedan de la compañía se juntan alrededor de un fuego y alguien dice los nombres de los que faltaron hoy, bajito, como una oración. Luego nadie habla. Se oye la leña. {nombre} mira las llamas hasta que se le cierran los ojos."),
    )

    private val RETIRADA_TIEMPO = listOf(
        re("{cielo} {de_estacion} En la retirada el tiempo importa más que los cosacos: decide quién llega."),
        re("{cielo} El frío de Rusia no es como el de Borgoña: entra por la boca y se queda dentro. {de_estacion}"),
    )

    /** El hospital de Prusia, en el invierno de la temporada 2 (estado «hospital»). */
    private fun ho(texto: String, hacer: Hacer? = null) = v(texto, estado = "hospital", hacer = hacer)

    private val HOSPITAL = listOf(
        ho("{nombre} se despierta en el hospital, en una iglesia llena de catres y de paja, con olor a podrido, a vinagre y a muerte. Lo primero que hace es mirarse los pies, vendados. Los dedos todavía están. Algunos de la sala ya no tienen."),
        ho("A {nombre} lo despiertan los que se quejan, que son casi todos, y las monjas que pasan con un cubo de sopa y un farol. Una le habla en alemán, despacio, como a un niño. Él no entiende nada y dice que sí."),
        ho("{nombre} se despierta y cuenta los catres vacíos de la sala: hoy son tres más que ayer. Los sacaron de noche. Fuera nieva sobre la ciudad. Se queda mirando el techo, que tiene una pintura vieja de ángeles, hasta que pasa el cirujano."),
    )

    private val HOSPITAL_TRABAJO = listOf(
        ho("A esta hora aprende otra vez a caminar, por el pasillo del hospital, agarrado a la pared, con los pies vendados como dos panes. Cada paso le duele hasta los dientes. Una monja lo mira y le dice algo que suena a ánimo. Llega hasta la puerta. Mañana, hasta el patio.", Hacer.CAMINO),
        ho("A esta hora ayuda en la sala como puede, porque ya puede: lleva agua a los que no se mueven, ayuda a cambiar la paja, sostiene a uno mientras el cirujano le corta un dedo. No mira. Oye.", Hacer.CAMINO),
        ho("A esta hora come la sopa del hospital, caldo con pan negro, de una escudilla que le acerca una monja, y se la come entera, despacio, mirando a los que no pueden comer. Es la primera comida caliente desde Rusia. Le sabe a gloria y a culpa.", Hacer.COMER),
        ho("A esta hora se cura los pies él solo, con lo que le dejó el cirujano: agua tibia, un trapo limpio, grasa. La piel se le cae a tiras. Debajo sale piel rosa, nueva, como de niño. El cirujano dice que tuvo suerte. {nombre} no sabe si suerte es la palabra.", Hacer.CUIDAR),
        ho("A esta hora duerme en su catre, por fin caliente, con el ruido de la sala alrededor: toses, rezos, delirios en cinco lenguas. Sueña con la nieve. Se despierta gritando una vez. El de al lado le dice que se calle. Luego le pregunta si está bien.", Hacer.DORMIR),
    )

    private val HOSPITAL_PUEBLO = listOf(
        ho("El hospital es una iglesia de una ciudad de Prusia llena de soldados franceses enfermos y heridos de la retirada, cientos, en catres, en paja, en el suelo. Las monjas y unos pocos cirujanos no dan abasto. Los que se curan se van; los que no, también."),
        ho("Desde la ventana del hospital se ve la ciudad, de casas con tejados en punta y nieve en los tejados, y la gente que pasa sin mirar. Los prusianos todavía son aliados, en el papel. En la calle, nadie les sonríe."),
    )

    private val HOSPITAL_ANOCHECER = listOf(
        ho("Al caer la tarde las monjas encienden las lámparas de la sala y rezan en alemán, y los que pueden rezan con ellas en francés, o en latín, o en nada. {nombre} mueve los labios. Piensa en Lucien. Luego deja de rezar y solo piensa en Lucien."),
        ho("Anochece temprano en Prusia, y la sala se queda a oscuras salvo una lámpara junto a la puerta. Alguien canta bajito una canción de soldados. Otros la siguen. Se para cuando el que la empezó se duerme."),
    )

    private val HOSPITAL_TIEMPO = listOf(
        ho("{cielo} {de_estacion} Desde su catre, por una ventana alta, {nombre} ve un trozo del cielo de Prusia."),
        ho("Por las ventanas de la iglesia entra una luz gris de invierno. {cielo} {de_estacion}"),
    )

    /** El pueblo de Borgoña, en las temporadas 3 a 5 (estado «pueblo»). */
    private fun pu(texto: String, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = v(texto, estado = "pueblo", hacer = hacer, desde = desde, hasta = hasta, meses = meses)

    private val PUEBLO_MANANA = listOf(
        pu("{nombre} se despierta en la cama de su infancia, en la casa de piedra de su madre, con el gallo del vecino y el olor del pan, y por un momento no sabe dónde está. Luego se acuerda, y se queda quieto, con los ojos cerrados, oyendo a su madre trajinar abajo. Está en casa.", hasta = "boda"),
        pu("A {nombre} lo despierta la campana de la iglesia del pueblo, la de siempre, la que tocaba cuando era niño. Se levanta, se lava en el cubo del patio y mira las viñas de la ladera, que empiezan detrás de la casa y suben hasta el bosque. Ningún paisaje del mundo le gusta más."),
        pu("{nombre} se despierta antes del alba, con la costumbre del tambor, y ya no se puede dormir. Baja a la cocina, aviva el fuego y se sienta junto a la ventana a ver amanecer sobre las viñas. Nadie le grita. Nadie lo cuenta. Es raro, y es bueno."),
        pu("Lo primero que hace {nombre} cada mañana es mirarse los pies, como en la guerra. Luego se acuerda de que ya no hace falta, y se ríe solo. Se pone los zuecos, que no le hacen daño, y sale a la puerta. Hace el tiempo que hace. No importa. No hay que marchar."),
        pu("A {nombre} lo despierta un sueño de nieve, como muchas noches, y se queda un rato sentado en la cama, sudando, hasta que oye la respiración de la casa y el gallo y sabe dónde está. Su madre no le pregunta nada. Le pone un tazón de leche caliente en la mesa."),
        pu("{nombre} se despierta al lado de Margot, en la cama que les hizo el tonelero, en el cuarto de arriba de la casa de su madre. Margot duerme todavía, con el pelo en la cara. Él se levanta sin hacer ruido. Hay que ir a la viña. Desde la puerta, se vuelve a mirarla.", desde = "boda"),
        pu("{nombre} se despierta con escarcha en la ventana y el fuego apagado, y baja a encenderlo antes de que se levante su madre. Fuera, las viñas están desnudas y negras bajo la helada. En invierno el pueblo duerme mucho. Él no se acostumbra.", meses = setOf(12, 1, 2)),
    )

    private val PUEBLO_TRABAJO = listOf(
        pu("A esta hora trabaja en la viña de su madre, en la ladera, que es pequeña, empinada y vieja, y que después de tantos años sin él está llena de hierba y de cepas muertas. La va limpiando cepa a cepa. Su padre la plantó. Él la va a salvar.", Hacer.CAMINO),
        pu("A esta hora trabaja por un jornal en las viñas de un señor del pueblo, que son grandes y llanas y dan un vino que se vende en París. El capataz lo mira como se mira a los soldados que vuelven: con un poco de respeto y un poco de miedo. Trabaja bien. Eso le basta.", Hacer.CAMINO),
        pu("A esta hora arregla los muretes de piedra de la viña, que sujetan la tierra de la ladera y que las lluvias tiran cada año. Coloca piedra sobre piedra, sin mortero, como le enseñó su padre. Es un trabajo lento y callado. Le gusta. Se parece a no pensar.", Hacer.CAMINO),
        pu("A esta hora poda las cepas con la podadera, en el frío, dejando dos yemas en cada sarmiento, como se hace aquí desde siempre. Los sarmientos cortados se atan en haces para el fuego. Las manos se le agrietan. Las de la guerra se agrietaban peor.", Hacer.CAMINO, meses = setOf(1, 2, 3)),
        pu("A esta hora cava la tierra de la viña con la azada, entre las cepas, para quitar la hierba y que la tierra respire, y ata los brotes nuevos a los rodrigones con mimbre. La viña está verde y tierna. Una helada ahora la mataría. {nombre} mira el cielo.", Hacer.CAMINO, meses = setOf(4, 5, 6)),
        pu("A esta hora quita hojas a las cepas para que el sol llegue a los racimos, que ya engordan, y espanta a los pájaros. Hace calor en la ladera. Bebe agua del cántaro que tiene escondido a la sombra de un murete. A lo lejos, el campanario. Ni un tambor.", Hacer.CAMINO, meses = setOf(7, 8)),
        pu("A esta hora trabaja en la bodega del tonelero, ayudando a lavar las barricas, a trasegar el vino nuevo, a sacar los orujos del lagar. El tonelero no habla: enseña con las manos. {nombre} aprende rápido. Margot aparece en la puerta de vez en cuando.", Hacer.CAMINO, desde = "margot", meses = setOf(10, 11, 12)),

        pu("A esta hora come en la cocina de su madre, en la mesa de siempre: sopa de pan con queso, una tortilla, un trozo de tocino, vino de la casa en un vaso de verdad. Su madre le sirve más de lo que pide y lo mira comer. No dice nada. No hace falta.", Hacer.COMER),
        pu("A esta hora come en la viña, a la sombra del murete, el pan y el queso que trajo envueltos en un trapo, con el vino aguado del cántaro. Desde aquí se ve el pueblo entero, los tejados, el campanario, el humo de las cocinas. Come despacio. Nadie le va a quitar el sitio.", Hacer.COMER),
        pu("A esta hora come con la familia del tonelero, que lo invitó, en una mesa larga, con Margot enfrente, que no lo mira y lo ve todo. El tonelero le pregunta por la guerra. {nombre} habla de Moscú, no del Berezina. Margot le pasa el pan sin que nadie lo pida.", Hacer.COMER, desde = "margot"),

        pu("A esta hora da de comer a la cabra y a las gallinas de su madre, saca agua del pozo, corta leña. Son las tareas que hacía de niño, y las hace igual, sin pensar, como si nunca se hubiera ido. Pero se ha ido. Las manos lo saben.", Hacer.CUIDAR),
        pu("A esta hora repara lo que se rompió en la casa mientras no estaba: una contraventana, la puerta del corral, una pata de la mesa, el techo de la bodega. Su madre se las arreglaba sola. Ahora se las deja a él y se sienta a mirarlo trabajar.", Hacer.CUIDAR),
        pu("A esta hora va a la taberna del pueblo, donde los viejos juegan a las cartas y los jóvenes le piden que cuente la guerra. Cuenta poco. Bebe un vaso. Se va pronto. Los viejos dicen que ha cambiado. Él no sabe qué contestar a eso.", Hacer.CUIDAR),

        pu("A esta hora duerme en la cama de su casa, con colchón de lana y sábanas que huelen a sol, y le cuesta dormir de lo blanda que es. Algunas noches baja la manta al suelo y duerme ahí. Su madre lo encuentra por la mañana y no dice nada.", Hacer.DORMIR),
        pu("A esta hora duerme, y casi siempre sueña con la nieve o con el río. Esta noche no: sueña con la viña en flor, con el olor dulce que tiene en junio, y se despierta tranquilo. Afuera canta un pájaro. Se vuelve a dormir.", Hacer.DORMIR),
    )

    private val PUEBLO_PUEBLO = listOf(
        pu("El pueblo es una calle que sube por la ladera entre casas de piedra rubia con tejados de teja, una iglesia, una fuente, una taberna, la herrería y la tonelería, y alrededor, viñas, todo viñas, hasta el bosque. Aquí nació {nombre}. Aquí nacieron todos los suyos."),
        pu("En el pueblo todo el mundo vive de la viña: el que tiene viñas, el que trabaja en las de otros, el tonelero que hace las barricas, el herrero que hace las podaderas, el cura que bendice las cepas. Cuando la cosecha es buena, todos comen. Cuando no, todos rezan."),
        pu("Desde la puerta de la casa de su madre se ven las viñas de la ladera, ordenadas en filas, y abajo, en el valle, el río, el camino real y los álamos. Por ese camino se fue {nombre}. Por ese camino volvió. A veces lo mira como si fuera a ver llegar a alguien."),
        pu("En el pueblo hay otros que volvieron de la guerra: uno sin brazo, que se sienta en la puerta de la iglesia; dos de España, que no hablan; un viejo de Egipto, que habla demasiado. Se saludan con la cabeza. No necesitan más. Los que no volvieron viven en la boca de sus madres."),
        pu("La casa de su madre es de piedra, con dos pisos: abajo la cocina, con el fuego y la mesa; arriba los cuartos; debajo, la bodega, fresca, con tres barricas y olor a vino viejo. En la puerta, una parra que da sombra en verano y uvas que nadie vende. Es lo que tienen. Basta."),
    )

    private val PUEBLO_GENTE = listOf(
        pu("Su madre es una mujer pequeña y seca, viuda desde hace años, que trabaja la viña, la casa y la huerta sin pararse nunca. Desde que {nombre} volvió, lo mira como si fuera a desaparecer otra vez. Le cocina lo que le gustaba de niño. Él no le dice que ya no le gusta."),
        pu("Claudine, su hermana, cose para las señoras del pueblo y sabe todo lo que pasa en él. Le cuenta a {nombre} quién se casó, quién murió, quién se fue, quién volvió de la guerra y quién no. Le hace preguntas sobre Rusia que nadie más se atreve a hacer. Él le contesta algunas."),
        pu("El cura del pueblo es el mismo que lo bautizó, más viejo, más sordo y más rojo de cara. Le pidió que fuera a misa, por lo menos los domingos. {nombre} va. Se sienta atrás, con los hombres, y mira el techo. El cura dice que Dios lo trajo de vuelta. {nombre} piensa en Lucien."),
        pu("El herrero, el padre de Lucien, sigue golpeando el hierro en su herrería de la plaza, de la mañana a la noche, como si nada. Cuando {nombre} pasa, el herrero levanta la mano sin dejar de golpear. Nunca han vuelto a hablar de Lucien. No hace falta."),
        pu("Margot, la hija del tonelero, tiene los ojos verdes, las manos fuertes de lavar barricas y una risa que se oye desde la viña. {nombre} la conoció en la fiesta del pueblo y desde entonces pasa por la tonelería más de lo que hace falta. El tonelero se ha dado cuenta. Margot también.", desde = "margot", hasta = "pide"),
        pu("Margot y {nombre} están prometidos: el tonelero dijo que sí, con la condición de que él aprenda el oficio. Se casarán después del invierno. Margot ya cose su ropa de casada con Claudine, y las dos se ríen de él cuando pasa.", desde = "pide", hasta = "boda"),
        pu("Margot, su mujer, vive con él en la casa de su madre, y entre las dos se reparten la casa sin pelearse, cosa que nadie esperaba. Margot trabaja en la viña con él y en la tonelería con su padre. Por las noches le enseña canciones que él no sabía.", desde = "boda", hasta = "margot_espera"),
        pu("Margot espera un hijo para la primavera, y sigue trabajando como si nada, aunque {nombre} le pide que no. Su madre ya cose ropa pequeña. {nombre} la mira y piensa en el niño, que nacerá en un pueblo sin guerra. Le parece el mejor regalo que le ha hecho nadie.", desde = "margot_espera"),
        pu("Le escribe de vez en cuando el sargento Morel, desde Lyon, donde trabaja en un taller de seda y se aburre. {nombre} le contesta él mismo, con letra torcida y muchas faltas, porque Morel le enseñó. Es lo único de la guerra que guarda con gusto.", desde = "morel"),
    )

    private val PUEBLO_PENSAMIENTO = listOf(
        pu("{mundo} {nombre} piensa en eso lo justo, desde la viña, que es donde mejor se piensa. Ya ha visto bastante mundo para saber que lo que pasa en París llega tarde al pueblo, y que lo que pasa en el pueblo no llega nunca a París. Le parece bien."),
        pu("Piensa en la guerra a ratos, cuando no quiere: un olor, un ruido, la nieve en la ladera, y vuelve el Berezina. Luego se le pasa. Su madre dice que el tiempo lo cura todo. Él piensa que el tiempo no cura: tapa. Como la nieve."),
        pu("{mundo} {nombre} piensa en el emperador, al que vio pasar a diez pasos y al que gritó viva hasta quedarse ronco. Piensa en los que se quedaron en Rusia por él. No sabe qué pensar del emperador. Sabe qué pensar de la viña: que hay que podarla en febrero."),
        pu("Piensa en los años que se le fueron en la guerra, que eran los mejores, dicen los viejos. Él no sabe si eran los mejores. Sabe que ahora tiene una viña, una casa, una madre, una hermana. Y piensa que muchos de los que marcharon con él darían cualquier cosa por eso."),
        pu("{mundo} A sus {edad} años, {nombre} ha visto Moscú quemada y ha cruzado ríos helados, y ahora lo que más le preocupa es que llueva en septiembre. Le parece ridículo. Luego le parece lo más sensato del mundo."),
    )

    private val PUEBLO_ANOCHECER = listOf(
        pu("Al caer la tarde {nombre} baja de la viña con la azada al hombro, como bajaba su padre, y los vecinos lo saludan desde las puertas. En la fuente, las mujeres llenan los cántaros. Huele a sopa y a leña. Nadie toca un tambor. Nadie pasa lista."),
        pu("Anochece sobre las viñas, que se ponen moradas y luego negras, y en el valle se encienden las luces del otro pueblo. {nombre} se sienta en el banco de piedra de la puerta y fuma una pipa. Su madre se sienta a su lado y le pregunta si se va a quedar. Él dice que sí."),
        pu("De noche el pueblo se queda en silencio, salvo un perro y el agua de la fuente. {nombre} no puede dormirse sin un ruido de campamento, y sale a la calle a mirar las estrellas, que son las mismas que en Rusia. Aquí no dan miedo."),
        pu("Al caer la tarde las campanas tocan el ángelus, y en las viñas de la ladera la gente se para, se quita el sombrero y reza un momento. {nombre} también se para. No reza. Mira a la gente rezar, quieta entre las cepas, y le parece lo más bonito que ha visto."),
        pu("La última luz del día se queda en el campanario cuando el pueblo ya está a oscuras. {nombre} la mira desde la ventana. En Rusia, a esta hora, se buscaba leña y se contaban los muertos. Aquí se cena. Se sienta a la mesa con los suyos y da gracias, aunque no sabe a quién."),
        pu("Al anochecer {nombre} y Margot se sientan en el banco de la puerta, con su madre, y hablan de la viña, del tiempo, de los vecinos. Margot le apoya la cabeza en el hombro. Su madre se va adentro con una excusa. Se oye la fuente.", desde = "boda"),
    )

    private val PUEBLO_TIEMPO = listOf(
        pu("{cielo} {de_estacion} En el pueblo el tiempo vuelve a importar como cuando era niño: por la viña."),
        pu("{nombre} mira el cielo desde la viña, como lo miran todos en el pueblo, para saber qué le va a hacer a la uva. {cielo} {de_estacion}"),
        pu("{de_estacion} {cielo} Los viejos del pueblo dicen que este año el tiempo está loco, pero lo dicen todos los años."),
        pu("{cielo} Este año no llega el verano: llueve, hace frío, y la uva no madura. Los viejos no se acuerdan de nada igual. {de_estacion}", desde = "frio", meses = setOf(6, 7, 8, 9, 10)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy empieza el año, y el capitán manda repartir un cuarto de vino de más a cada hombre, como cada año nuevo, haya lo que haya. Los soldados se desean buen año dándose la mano, y los veteranos dicen lo de siempre: que el año que viene, en casa.",
            Parrafo.GENTE to "Se brinda por el emperador, por Francia, por los que no están. Los veteranos cuentan dónde pasaron otros años nuevos: en Italia, en Egipto, en España, en el barro de Polonia. {nombre} escucha y piensa que algún día él también contará los suyos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza y no le pide nada, porque en el ejército pedir trae mala suerte. Solo piensa en la viña de su madre, que en enero duerme, y en que alguien tendrá que podarla en febrero. Él no. Otro.",
        ),
        "san_napoleon" to unDia(
            Parrafo.MANANA to "Hoy es San Napoleón, el día del emperador, que es también su cumpleaños, y en todo el ejército hay misa, desfile y ración doble de vino y de carne, si hay carne. Los soldados se lavan, cepillan el capote y sacan brillo a los botones. Desde el amanecer se oyen salvas.",
            Parrafo.GENTE to "Esta noche se bebe por el emperador alrededor de los fuegos, y se cantan canciones que hablan de él: de cuando era pequeño y cabo, de sus victorias, de su sombrero. Los veteranos que lo vieron de cerca lo cuentan otra vez. {nombre} grita viva el emperador hasta quedarse ronco.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que es raro tener un santo nuevo, puesto ahí para el emperador, y que en el pueblo el cura lo celebrará igual, porque se lo mandan. Su madre irá a misa. Rezará por el emperador y por él. Por él primero, piensa, conociéndola.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad. {nombre} se acuerda, nada más abrir los ojos, de la misa del gallo en la iglesia del pueblo, de las velas, del frío de la piedra, de su madre cantando más alto que nadie. Es lo primero que piensa todas las Navidades, esté donde esté.",
            Parrafo.GENTE to "Hoy se comparte lo que haya con los que estén: un trozo de pan blanco, un vaso de vino, unas castañas, una canción. Alguien empieza un villancico y los demás lo siguen, mal, con la voz rota. {nombre} canta el de su pueblo, el que cantaba su madre.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los que estaban la Navidad pasada y ya no están, y les dedica un trago. Piensa que la Navidad sirve para eso: para contar a los que faltan y dar gracias por los que quedan. Este año cuenta. No dice los números en voz alta.",
        ),
        "nochevieja" to unDia(
            Parrafo.MANANA to "Hoy es el último día del año, y {nombre} lo pasa como ha pasado casi todos los de este año: lejos, con frío, comiendo poco. Se le ocurre contar cuántos días ha estado en su vida lejos del pueblo, y no le salen las cuentas. Los días se le han mezclado.",
            Parrafo.ANOCHECER to "Al caer la noche, los de su rancho juntan lo que tienen para despedir el año: un poco de aguardiente, un trozo de pan, una salchicha que alguien guardaba. Se sientan alrededor de un fuego pequeño. Nadie dice que el año que viene será mejor. Nadie se atreve.",
            Parrafo.PENSAMIENTO to "{nombre} hace la cuenta del año que se va, de lo que tenía al empezar y de lo que tiene ahora. Le faltan cosas. Le faltan hombres. Le sobran recuerdos. Piensa que mañana empieza otro, y que no se lo ha pedido nadie.",
        ),

        // ---------- Los de la temporada 1 ----------
        "botas" to unDia(
            Parrafo.MANANA to "Hoy el sargento repartió en el cuartel botas nuevas y capotes, nuevos de verdad, sin agujeros de bala ni manchas de otro. Los veteranos se miraron entre ellos. Dicen que cuando el ejército da ropa nueva en invierno es que en primavera se va lejos. A {nombre} las botas le quedan bien.",
            Parrafo.GENTE to "Lucien se pasea por el dormitorio con el capote nuevo puesto, como un señor, y se mira en un cubo de agua. Dice que con esto puede ir al fin del mundo. Todos se ríen. Un veterano no se ríe. Dice que con esto no se va a ningún sitio frío.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en las botas viejas de su padre, que se las dejó al morir y que le quedaban grandes, y que se quedaron en el pueblo. Estas son del emperador. Aquellas eran de su padre. Le parece que pesan distinto, aunque pesen lo mismo.",
        ),
        "sale_cuartel" to unDia(
            Parrafo.MANANA to "Hoy, al amanecer, la compañía salió del cuartel en formación, con el tambor delante y la bandera del regimiento, por la calle mayor de la ciudad, hacia el este. La gente salió a las ventanas. Algunas muchachas tiraban flores. Nadie dijo adónde iban.",
            Parrafo.TRABAJO to "A esta hora marcha por la carretera del este con la mochila llena, las botas nuevas que todavía duelen y el capote enrollado encima. El primer día de marcha es siempre el peor y el mejor: duele todo, y todo es nuevo. Lucien canta. {nombre} se ríe con él.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando hacia atrás desde la primera colina, que la ciudad se queda a la espalda y que no sabe cuándo la volverá a ver. Delante solo hay camino. Piensa que lo bueno de ser soldado es que no hace falta saber adónde se va. Lo malo es lo mismo.",
        ),
        "rin_ida" to unDia(
            Parrafo.MANANA to "Hoy la compañía cruzó el Rin por un puente de barcas, enorme, que se movía con el agua, con miles de hombres pasando a la vez. Del otro lado ya es Alemania, o algo que antes era Alemania y ahora es del emperador. Lucien cruzó cantando a gritos, y medio puente cantó con él.",
            Parrafo.PUEBLO to "Del otro lado del Rin los pueblos son distintos: casas con vigas de madera pintadas, tejados empinados, iglesias con torres en punta, gente que habla una lengua de garganta. Los niños corren a ver pasar al ejército. Los mayores cierran las puertas.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando el río desde el puente, que el Rin es el río más grande que ha visto, más que el Saona, y que por él baja agua de montañas que no conoce hacia un mar que no conoce. Le parece que el mundo es grande. No sabe todavía cuánto.",
        ),
        "polonia" to unDia(
            Parrafo.MANANA to "Hoy la compañía entró en Polonia por un camino de arena, entre bosques de pinos y pueblos de casas de madera con techo de paja, más pobres que ningún pueblo de Francia. Los almacenes del ejército no llegan. El capitán Fabre dice que cada rancho se busque la comida.",
            Parrafo.TRABAJO to "A esta hora busca comida con Lucien por las granjas de alrededor del camino, y no hay nada: los que pasaron antes se lo llevaron todo. Encuentran unas patatas viejas y un pollo flaco. Una mujer los mira desde la puerta con un niño en brazos. Lucien le deja una moneda.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que si así es Polonia, que es amiga, cómo será Rusia, que es enemiga. Los veteranos dicen que la guerra se alimenta de la guerra, que el ejército come lo que encuentra. Piensa que aquí ya no queda qué encontrar, y que todavía ni ha empezado.",
        ),
        "niemen" to unDia(
            Parrafo.MANANA to "Hoy, al amanecer, la compañía cruzó el Niemen por uno de los puentes que tendieron de noche los pontoneros, delante del emperador, que miraba desde una colina. Del otro lado está Rusia. No había nadie esperando: ni un soldado, ni un cosaco. Solo bosque.",
            Parrafo.TRABAJO to "A esta hora marcha por el primer camino de Rusia, de arena, entre bosques que no se acaban, bajo un calor que nadie esperaba. No hay enemigo. Solo polvo, sed y moscas. Los caballos ya empiezan a caer. Lucien dice que los rusos se esconden porque tienen miedo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, al pisar la otra orilla, que acaba de entrar en un país del que no sabe nada: ni su lengua, ni su dios, ni su tamaño. El sargento dice que Rusia es más grande que todo lo que conocen juntos. {nombre} piensa que eso no puede ser verdad.",
        ),
        "smolensk" to TextoEspecial(listOf(
            mapOf(
                Parrafo.MANANA to "Hoy, por fin, la batalla: los rusos se defienden en Smolensk, una ciudad con murallas viejas sobre un río. Desde la mañana la artillería dispara contra los arrabales. La compañía espera en un campo de trigo, oyendo pasar las balas por encima. {nombre} tiene la boca seca.",
                Parrafo.TRABAJO to "A esta hora avanza con la compañía hacia los arrabales de Smolensk, entre casas de madera que arden, en el humo, disparando a ventanas donde a lo mejor hay alguien. Un hombre de su fila cae sin un ruido. {nombre} no lo mira. Sigue. Lucien está a su lado.",
                Parrafo.PENSAMIENTO to "{nombre} piensa, en la primera noche después de la batalla, mirando la ciudad que arde, que ya ha estado en una batalla de verdad. Esperaba sentir algo. No siente nada, salvo hambre y unas ganas enormes de dormir. Se pregunta si eso es ser valiente.",
            ),
            mapOf(
                Parrafo.MANANA to "Hoy, al amanecer, Smolensk estaba vacía: los rusos se fueron de noche por el otro lado del río y dejaron la ciudad ardiendo. La compañía entra en una ciudad de ruinas y de muertos. No hay nada que comer: los rusos se lo llevaron todo, o lo quemaron.",
                Parrafo.PUEBLO to "En las calles de Smolensk hay casas que todavía arden, iglesias con las puertas abiertas, heridos rusos que nadie recoge y perros que comen lo que no deben. Los soldados buscan comida entre las ruinas. Encuentran poca. El humo se ve desde lejos.",
            ),
        )),
        "borodino" to unDia(
            Parrafo.MANANA to "Hoy, antes del alba, se leyó a todo el ejército una proclama del emperador: que esta es la batalla que querían, que la victoria depende de ellos, que les dará la abundancia y la vuelta a casa. Luego empezaron los cañones, cientos al mismo tiempo. La tierra tembló toda la mañana.",
            Parrafo.TRABAJO to "A esta hora avanza con la compañía contra unos reductos de tierra llenos de cañones rusos, a paso de carga, en el humo, tropezando con los muertos de los que fueron antes. La compañía se deshace a su alrededor. Llegan. Los echan. Vuelven a llegar. {nombre} ya no sabe cuántas veces.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, por la noche, sentado entre los muertos porque no hay otro sitio donde sentarse, que nunca ha visto tantos, ni los verá. El campo está cubierto de hombres y de caballos hasta donde se ve. Lucien está vivo. Él está vivo. Es todo lo que piensa.",
        ),
        "moscu" to unDia(
            Parrafo.MANANA to "Hoy, desde una colina, vieron Moscú: cientos de cúpulas doradas y de colores, torres, palacios, una ciudad enorme brillando al sol. Los soldados gritaron Moscú, Moscú, como los marineros gritan tierra. Por la tarde entraron. La ciudad estaba vacía. No salió nadie a recibirlos.",
            Parrafo.PUEBLO to "Las calles de Moscú están desiertas: casas cerradas, tiendas abiertas y sin nadie, iglesias con las puertas de par en par, un gato. Los rusos se fueron todos, o casi todos. Los soldados entran en las casas y encuentran mesas puestas, comida, vino. Nadie lo entiende.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esto es la victoria: una ciudad entera para ellos, con todo dentro. Pero una ciudad vacía da miedo, como una casa donde alguien acaba de morir. Lucien se prueba un abrigo de pieles en una tienda abandonada y se ríe. {nombre} no se ríe.",
        ),
        "incendio" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy, antes del alba, empezaron los incendios en Moscú: primero en un barrio, luego en otro, luego en todos. Dicen que los rusos dejaron hombres para quemar la ciudad y que se llevaron las bombas de agua. El viento sopla fuerte. Las casas de madera arden como paja.",
                Parrafo.TRABAJO to "A esta hora corre por las calles de Moscú entre las llamas, con la compañía, sacando de las casas lo que se puede antes de que se queme: comida, ropa, vino, cosas que brillan. El aire quema. El humo no deja ver. Un techo se cae delante de ellos. Se dan la vuelta.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que nunca ha visto nada tan grande ni tan terrible: una ciudad entera ardiendo, de noche, con un ruido como el de un río. Piensa que los rusos han quemado su propia ciudad para no dársela. Piensa que un pueblo que hace eso no se va a rendir.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "De noche Moscú arde todavía, el cielo está rojo como de día, y se puede leer una carta a la luz del incendio, dicen los que saben leer. La compañía duerme fuera de la ciudad, en un campo, mirando el fuego. Nadie duerme.",
                "Al anochecer el fuego sigue, más lento, y por las calles pasan soldados de todas las naciones cargados como mulas: pieles, cuadros, plata, botellas, un loro. {nombre} carga un saco de harina y un abrigo. Lucien carga un reloj que no funciona.",
                "Esta noche el fuego empieza a apagarse, por fin, porque ya casi no queda nada que quemar. Moscú es un campo de chimeneas negras. El viento trae ceniza. {nombre} tiene ceniza en el pelo, en la boca, en la comida, en los sueños.",
            )),
        ),
        "sale_moscu" to unDia(
            Parrafo.MANANA to "Hoy el ejército salió de Moscú, después de cinco semanas esperando una paz que no llegó. Salió cargado como una feria: carros llenos de cosas robadas, carrozas, mujeres, prisioneros. La columna tarda horas en salir de la ciudad. Por la mañana ya hace frío.",
            Parrafo.TRABAJO to "A esta hora anda en la columna que sale de Moscú, con la mochila llena de lo que encontró: harina, azúcar, un abrigo de pieles, un candelabro de plata que pesa como un niño. Lucien lleva un cuadro de un santo. Un veterano les dice que lo tiren todo menos la harina y las pieles.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando hacia atrás las ruinas de Moscú, que vinieron hasta aquí, tan lejos, para nada: para una ciudad quemada y un zar que no quiere hablar. Piensa en el camino de vuelta, que es el mismo de venir, y que ya estaba vacío cuando vinieron.",
        ),
        "maloyaroslavets" to unDia(
            Parrafo.MANANA to "Hoy hubo batalla en un pueblo junto a un río, Maloyaroslavets, que se tomó y se perdió y se volvió a tomar muchas veces, hasta que no quedó una casa en pie. Al final quedó en manos de los franceses. Pero el camino del sur, el de las tierras con comida, sigue cerrado.",
            Parrafo.TRABAJO to "A esta hora espera con la compañía en la reserva, oyendo la batalla del pueblo, viendo pasar a los heridos hacia atrás. No los llaman. Por la tarde pasan junto al pueblo, que arde. Hay muertos en las calles, en el río, en los jardines. {nombre} pasa sin mirar. Ya sabe hacerlo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, cuando se sabe que se vuelve por el camino de Smolensk, el que ya está comido y quemado, que eso quiere decir hambre. Los veteranos lo saben y no dicen nada. Los nuevos no lo saben. Lucien pregunta por qué están todos tan callados.",
        ),
        "nieve" to unDia(
            Parrafo.MANANA to "Hoy empezó a nevar, grandes copos lentos que al principio parecían bonitos, y ya no paró. Al mediodía el camino estaba blanco y los caballos resbalaban. Por la tarde hizo un frío que nadie había sentido nunca. Los veteranos se miraron. Ya sabían lo que venía.",
            Parrafo.ANOCHECER to "Al anochecer, en la nieve, la compañía busca un sitio para el vivac y no lo encuentra: todo es blanco. Hacen fuego con lo que pueden y se sientan alrededor, apretados. Algunos se quedan lejos del fuego, de cansancio. Por la mañana esos ya no se levantarán.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la nieve del pueblo, que caía unos días en enero y se iba, y que los niños esperaban todo el año. Esta no es la misma nieve. Esta no se va. Piensa que el capote que le dieron en enero no es para esto. Le cose por dentro las pieles de Moscú.",
        ),
        "krasnoi" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy, cerca de un pueblo que se llama Krasnoi, los rusos esperaban en las colinas junto al camino, con cañones y cosacos, y la columna tuvo que pasar entre ellos. Se pasó como se pudo, en la nieve, bajo la metralla. Muchos no pasaron. La Guardia del emperador abrió el paso.",
                Parrafo.TRABAJO to "A esta hora anda en la nieve, en lo que queda de la compañía, con el fusil cargado, mirando las colinas donde están los rusos, que disparan cuando quieren. No se puede parar ni correr. Se anda. Los cosacos atacan a los rezagados. Gritan como lobos.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que esto ya no es una guerra: es una cacería, y ellos son la presa. Piensa en Lucien, que tose desde hace días y anda cada vez más despacio. Le quita la mochila y se la carga él. Lucien protesta. Luego no protesta.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora anda detrás de la Guardia, que va en orden en medio de los que ya no van en orden, con sus gorros de piel y sus caras de piedra. Seguirlos da valor. Alguien dice que mientras la Guardia ande, el ejército existe.",
                "A esta hora anda con Lucien colgado del brazo, en la nieve, contando los pasos en voz alta para que Lucien los cuente también. Cien. Otros cien. Lucien se ríe de algo que no tiene gracia. {nombre} se ríe con él para que no se pare.",
            )),
        ),
        "berezina" to TextoEspecial(listOf(
            mapOf(
                Parrafo.MANANA to "Hoy llegaron al río Berezina, que no estaba helado, como esperaban, sino lleno de hielo suelto que bajaba con la corriente. Los rusos tienen los puentes. Los pontoneros del ejército se metieron en el agua helada hasta el pecho a construir dos puentes de madera. Muchos no salieron.",
                Parrafo.TRABAJO to "A esta hora espera su turno para cruzar, en la orilla, en una multitud de miles de soldados, carros, caballos y mujeres que empujan hacia los puentes. Los rusos disparan desde lejos. {nombre} sujeta a Lucien del capote para no perderlo entre la gente. No lo suelta.",
                Parrafo.PENSAMIENTO to "{nombre} piensa, mirando los puentes, que todo el ejército tiene que pasar por ahí, por dos tablados sobre el agua helada, y que no hay sitio para todos. Piensa que esta vez el que no pase se queda en Rusia. Piensa en Lucien. Tiene que pasar con Lucien.",
            ),
            mapOf(
                Parrafo.TRABAJO to "A esta hora espera en la orilla, en el frío, junto a un fuego, con Lucien, que tiene fiebre y ya no puede andar. Los oficiales dicen que se cruce de noche, que de día los rusos disparan. Lucien dice que mañana. {nombre} dice que mañana.",
                Parrafo.ANOCHECER to "Al anochecer, junto al río, miles de hombres esperan sin cruzar, alrededor de fogatas, como si los puentes no fueran a desaparecer nunca. {nombre} le da a Lucien su último trozo de caballo. Lucien se lo come despacio y le dice que mañana estará mejor.",
            ),
            mapOf(
                Parrafo.TRABAJO to "A esta hora cruza el puente con la compañía, por orden del capitán, sin Lucien, que se quedó junto al fuego de la orilla con otros enfermos, para cruzar con la primera luz. {nombre} le dejó su manta. Le dijo que lo esperaba del otro lado.",
                Parrafo.ANOCHECER to "Al anochecer, desde la otra orilla, {nombre} mira los fuegos de los que no cruzaron, que son miles, y busca el de Lucien. No lo distingue. Todos son iguales. Se envuelve en el capote sin manta y espera la mañana, con los ojos abiertos.",
            ),
        )),
        "lucien" to unDia(
            Parrafo.MANANA to "Hoy, con la primera luz, {nombre} esperaba a Lucien al otro lado del río, junto al puente. A media mañana llegó la orden: quemar los puentes, porque vienen los rusos. Miles de rezagados se quedaron en la otra orilla, gritando. Algunos se tiraron al agua. Lucien no apareció.",
            Parrafo.GENTE to "Los de la compañía que quedan, que ya son pocos, no dicen nada. El sargento le pone una mano en el hombro y se va. Un desconocido le da un trago de aguardiente. {nombre} se lo bebe mirando el humo del puente, del otro lado, donde estaba Lucien. Donde está.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en lo que se juraron en la taberna del pueblo, la noche antes de irse: si uno cae, el otro lo lleva. No lo llevó. Lo dejó junto a un fuego con su manta y le dijo que lo esperaba. Lo esperó. Piensa que eso no cuenta. Piensa que nunca contará.",
        ),
        "trineo" to unDia(
            Parrafo.MANANA to "Hoy corrió la noticia por lo que queda del ejército: el emperador se fue a París en un trineo, con unos pocos, a toda prisa, y dejó el mando a otro. Unos dicen que va a buscar otro ejército. Otros dicen otras cosas. Nadie las dice muy alto.",
            Parrafo.GENTE to "Esta noche, alrededor del fuego, un veterano que gritaba viva el emperador en Borodino escupe en la nieve y no dice nada. Otro dice que el emperador sabe lo que hace. Un tercero dice que el emperador va caliente en un trineo con pieles. Nadie le contesta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el emperador, que pasó a diez pasos de él una vez, y en el trineo, y en el frío. Piensa que un general se va cuando la batalla está perdida. Piensa que entonces está perdida. Lo piensa despacio, porque nunca lo había pensado. Se da cuenta de que lo sabía.",
        ),
        "niemen_vuelta" to unDia(
            Parrafo.MANANA to "Hoy {nombre} cruzó otra vez el Niemen, el mismo río que cruzaron en junio con música y banderas, ahora sobre el hielo, a pie, con los pocos que quedan de la compañía. En junio eran ciento cuarenta. Hoy son catorce. Del otro lado no hay nadie esperando.",
            Parrafo.PUEBLO to "Del otro lado del Niemen hay un pueblo con casas de verdad, con humo en las chimeneas, y la gente cierra las puertas al ver llegar a esos hombres envueltos en pieles y harapos, con las barbas heladas, que parecen fantasmas o mendigos. No parecen un ejército. No lo son.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que salió de Rusia, que está vivo, que tiene los pies, las manos y la cabeza. No piensa en nada más. No puede. Se sienta en la nieve, del lado de acá del río, y llora por primera vez desde que empezó todo. Nadie lo mira. Todos lloran.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "granadero", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + CUARTEL + MOSCU + RETIRADA + HOSPITAL + PUEBLO_MANANA,
            Parrafo.TIEMPO to TIEMPO + CUARTEL_TIEMPO + MOSCU_TIEMPO + RETIRADA_TIEMPO + HOSPITAL_TIEMPO + PUEBLO_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + CUARTEL_TRABAJO + MOSCU_TRABAJO + RETIRADA_TRABAJO + HOSPITAL_TRABAJO + PUEBLO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + CUARTEL_PUEBLO + MOSCU_PUEBLO + RETIRADA_PUEBLO + HOSPITAL_PUEBLO + PUEBLO_PUEBLO,
            Parrafo.GENTE to GENTE + PUEBLO_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + PUEBLO_PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + CUARTEL_ANOCHECER + MOSCU_ANOCHECER + RETIRADA_ANOCHECER + HOSPITAL_ANOCHECER + PUEBLO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
