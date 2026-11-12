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
 * El cuento del hermano Bernat, sargento del Temple en el castillo de Safed, en Galilea, de 1250 a 1254. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su nombre en la ficha es «Hermano Bernat»; aquí se escribe «Bernat», como en la orden, para que no salga «Hermano»
 *   con mayúscula en medio de una frase.
 * - Su día es de monje y de soldado: las horas en la capilla (maitines a medianoche, prima, vísperas y completas) y,
 *   entre ellas, los caballos, las armas, la guardia y la obra.
 * - El pueblo es el castillo y sus aldeas. Lee el arco «muralla»: la torre del sur, que sube hasta la temporada 4.
 * - Dos estados: «herido» (temporada 2: seis semanas en la enfermería) y «acre» (3: un mes en la casa de la orden, junto
 *   al mar). En los dos amanece, trabaja, reza, come y duerme donde está.
 * - No repite lo que ya dice «Su vida»: que no es caballero, el pardo y no el blanco, los tres votos, la Regla que manda
 *   cuántos caballos y qué castigo, la arena, los peregrinos y que casi toda su gente cabe en unos castillos.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG4.kt`):
 * - Ramon, su amigo, está en Egipto con el ejército del rey hasta `mansura` (temporada 1); después se le recuerda muerto.
 * - Pere llega en `pere` (temporada 2) y muere en `muere_pere` (4).
 * - El Moro queda cojo desde `moro_cojo` (temporada 2).
 * - Las fiestas que vuelven cada año no nombran a Ramon ni a Pere.
 */
internal object PiezasBernat {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A Bernat lo despierta la campana de prima, cuando todavía es de noche, y se levanta del catre ya vestido, con la camisa y las calzas, como manda la Regla, que no quiere que un hermano duerma desnudo. Se ata el cinto. Fuera huele a humo, a caballo y a piedra mojada."),
        v("Bernat se despierta antes de la campana con el ruido de los albañiles, que en Safed empiezan a picar piedra con la primera luz. Desde la ventana estrecha del dormitorio ve el cielo del este que se pone gris sobre los montes. Abajo, en el fondo, el lago todavía es negro."),
        v("Lo primero del día es la capilla: Bernat baja con los demás hermanos a decir prima, de pie, deprisa, porque los sargentos no saben latín y rezan padrenuestros, tantos como manda la Regla para cada hora. Él los cuenta con los dedos. Siempre le sobra uno."),
        v("Al amanecer Bernat sube a la muralla a mirar: el valle, los caminos, las aldeas con sus humos, el lago de Galilea abajo, como un plato de plata. Al norte, lejos, el monte grande con nieve en la cabeza. Nadie en los caminos. Baja a las cuadras."),
        v("Bernat se lava la cara en el agua de la cisterna, que está fría aunque fuera haga calor, y se moja la barba, que la orden manda llevar larga y el pelo corto. Se mira en el agua. Tiene más canas que el año pasado, y menos que el que viene."),
        v("Lo primero que hace Bernat cada mañana, antes que rezar, aunque no lo diría en el capítulo, es ir a ver al Moro, su caballo, que lo saluda con un resoplido. Le pasa la mano por el cuello, le mira las patas y le habla en catalán, que es la lengua que entiende."),
    )

    /** Las seis semanas en la enfermería (estado «herido», temporada 2). */
    private fun he(texto: String, hacer: Hacer? = null) = v(texto, estado = "herido", hacer = hacer)

    private val HERIDO_MANANA = listOf(
        he("Bernat se despierta en la enfermería con la pierna ardiendo debajo del vendaje y el olor de los ungüentos del enfermero. Oye la campana de prima y no puede bajar a la capilla. Reza sus padrenuestros en el catre, mirando las vigas del techo."),
        he("A Bernat lo despiertan los otros enfermos de la sala: un hermano viejo que tose, un turcópolo con fiebre que habla en griego dormido. Por la ventana entra el ruido de la obra. Él intenta mover la pierna. Se mueve un poco más que ayer."),
        he("Bernat se despierta tarde, porque en la enfermería se duerme lo que se quiere, y eso es lo único bueno de estar herido. El enfermero ya le ha dejado el caldo junto al catre. Fuera, el castillo hace su día sin él, y él lo oye todo."),
    )

    private val HERIDO_TRABAJO = listOf(
        he("A esta hora remienda correas y cinchas sentado en el catre de la enfermería, con la pierna estirada en un banco, porque no sabe estar con las manos quietas. Pere le trae del almacén lo que hay que coser. Las hace mejor que en la cuadra, que aquí hay tiempo.", Hacer.TRABAJO),
        he("A esta hora camina por el patio de la enfermería apoyado en una muleta, de un muro a otro, diez veces, como le mandó el enfermero. A la quinta le duele. A la octava, más. A la décima se sienta en la escalera, sudando, y mira la torre de la obra, que sube sin él.", Hacer.TRABAJO),
        he("A esta hora enseña a Pere, que se sienta en el suelo junto a su catre, cómo se cuida un caballo con una pata caliente, cómo se ata una lanza al arzón, qué se hace cuando una flecha entra en la cota. Pere apunta todo en la cabeza. Bernat repite lo importante tres veces.", Hacer.TRABAJO),
        he("A esta hora reza en el catre de la enfermería, porque no puede bajar a la capilla, y dice sus padrenuestros con los otros enfermos, cada uno a su ritmo. Por la ventana se oye el canto de los hermanos que sí bajaron. Bernat los sigue con los labios.", Hacer.REZAR),
        he("A esta hora el capellán sube a la enfermería a darle la comunión a los enfermos, con un monaguillo y una campanilla. Bernat se confiesa de lo de siempre: de impaciencia, sobre todo. El capellán le dice que la paciencia también es una batalla.", Hacer.REZAR),
        he("A esta hora come en la enfermería, donde a los heridos se les da carne aunque sea viernes, para que se repongan: caldo de gallina, pan blanco, un trozo de cordero. Bernat come con hambre y con un poco de vergüenza, que en el refectorio hoy hay habas.", Hacer.COMER),
        he("A esta hora come en el catre, con la escudilla sobre las rodillas. El enfermero le pone vino sin aguar, que dice que hace sangre. Yusuf sube a contarle cómo está el Moro: bien, aburrido, comiendo más de la cuenta. Bernat se ríe, por primera vez en días.", Hacer.COMER),
        he("A esta hora duerme a ratos, con la pierna que late y los gemidos de los otros enfermos. Sueña con los jinetes que llegaron por el trigal, con el golpe, con el suelo. Se despierta sudando. La lámpara de la enfermería sigue encendida, como siempre.", Hacer.DORMIR),
        he("A esta hora duerme hondo, por fin, con una bebida de adormidera que le dio el enfermero. No oye la campana de maitines. Mañana se lo confesará al capellán, y el capellán le dirá que eso no es pecado. Ya lo sabe. Se lo confesará igual.", Hacer.DORMIR),
    )

    private val HERIDO_PUEBLO = listOf(
        he("La enfermería del castillo es una sala larga y abovedada, con catres en fila, una lámpara que no se apaga y un fuego en invierno. Huele a vinagre, a hierbas y a sangre vieja. El enfermero es un hermano de Provenza que sabe más que los médicos de Acre, y lo dice."),
        he("Desde la ventana de la enfermería se ve el patio del castillo, la cisterna, la herrería y la torre del sur, con sus andamios y sus albañiles. Bernat la mira todo el día. Cuando no está él, piensa, la piedra sube igual. Le molesta un poco."),
    )

    private val HERIDO_ANOCHECER = listOf(
        he("Al caer la tarde la enfermería se queda en penumbra, y el enfermero pasa con la lámpara mirando a cada uno. Le cambia el vendaje a Bernat, huele la herida, asiente. No huele mal. Eso, en una herida, es lo único que importa."),
        he("Anochece, y desde la enfermería Bernat oye completas en la capilla, y luego el silencio de la noche del castillo, que es el silencio de cien hombres que duermen. Él no duerme. Cuenta con los dedos los días que lleva aquí, se pierde y vuelve a empezar."),
        he("De noche, en la enfermería, el hermano viejo del catre de al lado le cuenta a Bernat la batalla de Hattin, que vio su abuelo, como si la hubiera visto él. Se la ha contado ya cuatro veces. Bernat la escucha igual: en la enfermería, una historia vale más que el sueño."),
    )

    /** El mes en Acre, en la casa de la orden, junto al mar (estado «acre», temporada 3). */
    private fun ac(texto: String, hacer: Hacer? = null) = v(texto, estado = "acre", hacer = hacer)

    private val ACRE_MANANA = listOf(
        ac("Bernat se despierta en la casa del Temple en Acre, en un dormitorio que da al mar, con el ruido de las olas contra el muro y los gritos de las gaviotas. Por un momento no sabe dónde está. Luego huele la sal, y se acuerda. Baja a prima con los hermanos de la casa."),
        ac("A Bernat lo despierta la campana de la casa de Acre, y luego todas las campanas de la ciudad, que en Acre hay iglesias de todas las naciones y cada una toca a su hora. Venecianos, genoveses, pisanos, sirios. Es como si la ciudad discutiera desde el amanecer."),
        ac("Al amanecer Bernat sube a la torre de la casa del Temple, la grande, que mira al mar, y ve el puerto con sus barcos, la ciudad amontonada detrás de las murallas y, muy lejos, en tierra, los montes donde está Safed. Desde aquí parecen pequeños."),
    )

    private val ACRE_TRABAJO = listOf(
        ac("A esta hora está en el puerto de Acre esperando los barcos que traen caballos de Apulia, que vienen colgados con cinchas en la bodega para que no se rompan las patas con las olas. Cuando los sacan, bajan temblando y flacos. Bernat los mira uno por uno.", Hacer.TRABAJO),
        ac("A esta hora prueba caballos en el campo que hay fuera de las murallas de Acre: los monta, los hace trotar, los para de golpe, les mira los dientes y los cascos. Los mercaderes juran que todos son de linaje de reyes. Bernat no les cree ni la mitad.", Hacer.TRABAJO),
        ac("A esta hora acompaña al hermano mariscal por las cuadras de la casa de Acre, que son enormes, con caballos de toda la orden, y aprende cómo los cuidan aquí: con más agua, que la humedad del mar los enferma de otra manera. Bernat apunta en la cabeza.", Hacer.TRABAJO),
        ac("A esta hora reza en la capilla de la casa del Temple en Acre, que es más grande y más rica que la de Safed, con lámparas de plata y un relicario que trajeron de Jerusalén cuando se perdió. Bernat reza delante, con los hermanos de aquí, que rezan más deprisa.", Hacer.REZAR),
        ac("A esta hora oye misa en la capilla de Acre, y desde la ventana se oye el mar. Bernat piensa en el mar que lo trajo, hace años, desde Barcelona, y en el que lo llevaría de vuelta, si alguna vez lo dejaran. No lo dejarán. Reza.", Hacer.REZAR),
        ac("A esta hora come en el refectorio de la casa de Acre, más grande y con mejor comida que el de Safed: pescado del puerto, pan blanco, aceitunas, vino de Chipre. Se come en silencio, de dos en dos de la misma escudilla, mientras un capellán lee. El compañero de Bernat come muy deprisa.", Hacer.COMER),
        ac("A esta hora come con los hermanos de Acre, que hablan por señas de las noticias del puerto: un barco de Venecia, una pelea entre genoveses, el rey que visita las murallas. Bernat no sabe todas las señas de aquí. Asiente a todo.", Hacer.COMER),
        ac("A esta hora duerme en el dormitorio de la casa de Acre, que es húmedo y caliente, y huele a mar. Las olas golpean el muro toda la noche. Bernat, que es de un pueblo de tierra adentro, no termina de acostumbrarse. Sueña que el castillo flota.", Hacer.DORMIR),
        ac("A esta hora duerme mal, por el calor pegajoso de Acre y los mosquitos, y por el ruido de la ciudad, que no duerme nunca: borrachos en el puerto, perros, una campana que alguien toca a deshora. Echa de menos el silencio frío de Safed.", Hacer.DORMIR),
    )

    private val ACRE_PUEBLO = listOf(
        ac("Acre es la ciudad más grande que le queda a los cristianos en esta tierra, amontonada detrás de dos murallas, con un puerto lleno de barcos y barrios de cada nación: venecianos, genoveses, pisanos, alemanes, y las casas grandes de las órdenes. Todos se odian un poco. Todos comercian."),
        ac("La casa del Temple en Acre es un castillo dentro de la ciudad, junto al mar, con una torre enorme, almacenes, cuadras, un puerto propio y el tesoro de la orden. Por aquí pasan el dinero, los caballos, los hermanos y las órdenes del maestre. Safed, desde aquí, es una cosa pequeña."),
        ac("En las calles de Acre se oyen todas las lenguas: el francés de los señores, el italiano de los mercaderes, el árabe de los cristianos de aquí, el griego, el armenio, y hasta el catalán de algún marinero. Bernat se para a escucharlo. Le dan ganas de llorar, y no llora."),
    )

    private val ACRE_ANOCHECER = listOf(
        ac("Al caer la tarde Bernat se sienta en el muro de la casa del Temple que da al mar y mira cómo el sol se mete en el agua, rojo, entre los barcos. Nunca lo ha visto así en Safed, donde el sol se esconde detrás de los montes. Le parece un lujo."),
        ac("Anochece en Acre, y en el puerto se encienden las lámparas de los barcos y las tabernas, y el ruido sube. En la casa del Temple, después de completas, se cierran las puertas. Bernat se queda escuchando la ciudad, que sigue, como el mar."),
        ac("Al anochecer vuelve del puerto con los caballos que eligió hoy, por calles estrechas llenas de gente, y los mete en la cuadra de la casa. Los cuenta. Están todos. Les habla en catalán, para que se vayan acostumbrando a él.", ),
    )

    private val ACRE_TIEMPO = listOf(
        ac("En Acre el aire es húmedo y huele a sal, y el cielo baja hasta el mar sin montes que lo corten. {cielo} {de_estacion}"),
        ac("{cielo} {de_estacion} Junto al mar el calor se pega a la ropa, y la cota de malla se oxida si no se aceita cada día."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el castillo el tiempo se mira desde la muralla, para saber si los caminos están abiertos."),
        v("Bernat mira el cielo desde la torre, sobre el lago y los montes. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Yusuf, que nació en estas tierras, dice que el viento del este trae fiebres y el del oeste lluvia. Casi siempre acierta."),
        v("{cielo} En {estacion} el castillo es otro, y Bernat lo sabe por la cota de malla: en invierno quema de fría y en verano de caliente."),
        v("Desde lo alto de la muralla se ve el lago de Galilea, abajo, y más allá los montes de Damasco, por donde vienen los enemigos. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los hermanos viejos dicen que el tiempo de esta tierra no es como el de casa: o seca o inunda, y nunca avisa."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y el sol pega en las piedras de la muralla hasta que queman.",
            "Hace un día claro, de los que dejan ver el monte de la nieve, al norte, como si estuviera cerca.",
            "No hay una nube, y el lago brilla abajo como un espejo.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, y el lago abajo es gris como el plomo.",
            "Hay nubes bajas que se enganchan en las torres del castillo.",
            "Está nublado, y la luz es pareja y sin sombras.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el agua corre por los canales del patio hacia las cisternas, que se llenan.",
            "Cae una lluvia fuerte, de las de esta tierra, que no dura pero inunda.",
            "Llueve, y el barro del camino de Acre se pega a las patas de los caballos.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los rayos caen sobre los montes y los caballos patean en las cuadras.",
            "El cielo se pone negro sobre el lago, y llega la tormenta con viento y granizo.",
        ),
        Clima.NIEVE to listOf(
            "Nieva sobre el castillo, cosa rara, y los hermanos que vinieron de Francia se ríen como niños.",
            "Caen copos sobre las almenas, y la nieve se derrite al tocar el suelo.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento frío del norte, que baja del monte de la nieve.",
            "Hay viento, y las banderas de la torre golpean como látigos.",
        ),
        Clima.POLVO to listOf(
            "Sopla el viento caliente del desierto, y el aire es amarillo y pesado.",
            "Hay polvo en el aire, y el lago no se ve.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando los campos se ponen verdes y los caminos se llenan de peregrinos.",
            "En primavera hay flores en las laderas y los enemigos vuelven a moverse.",
            "Es el tiempo de la siega, que aquí llega antes que en casa.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, cuando no llueve nunca y el agua se cuenta en las cisternas.",
            "En verano el calor no deja dormir, ni con las piedras del castillo.",
            "Es el tiempo de las guardias largas y los caminos de polvo.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo de la aceituna y de las primeras lluvias.",
            "En otoño se recoge lo que queda y se reparan las murallas.",
            "Es el tiempo en que se espera la lluvia como se espera a un amigo.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, el tiempo de las lluvias y del frío en los montes.",
            "En invierno el castillo es húmedo y frío, y los caminos se cierran.",
            "Es el tiempo en que los enemigos se quedan en casa, y los hermanos también.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora está en las cuadras con Yusuf, cepillando al Moro con la almohaza, limpiándole los cascos y mirándole los dientes. Un caballo cuidado es una vida salvada, dice el mariscal. Bernat lo cree. En la batalla, el caballo es lo único que no te miente.", hacer = Hacer.TRABAJO),
        v("A esta hora hace guardia en la muralla, en la torre del este, mirando el camino de Damasco con los ojos entornados por el sol. No pasa nadie. Un pastor con sus cabras. Un halcón. Bernat cuenta las piedras de la almena, como cada guardia. Siguen siendo catorce.", hacer = Hacer.TRABAJO),
        v("A esta hora hace instrucción en el patio con los sargentos y los turcópolos: cargar con la lanza, girar, volver a formar, todo en silencio, a la señal del gonfanonero. El que rompe la fila, paga. Bernat no la rompe nunca. Es lo único de lo que está orgulloso.", hacer = Hacer.TRABAJO),
        v("A esta hora vigila a los que trabajan en la obra de la torre del sur: albañiles cristianos que cobran, y cautivos sarracenos que no, que suben piedras por una rampa con cadenas en los pies. Bernat no los mira mucho a los ojos. Ellos a él tampoco.", hacer = Hacer.TRABAJO),
        v("A esta hora cuida sus armas en el cuarto de los arneses: aceita la cota de malla para que no se oxide, afila la espada en la piedra, revisa la lanza y la correa del escudo. La Regla castiga al que pierde o descuida su equipo. A Bernat no le hace falta la Regla para eso.", hacer = Hacer.TRABAJO),
        v("A esta hora escolta un carro de grano desde una aldea del castillo, a caballo, con dos turcópolos, por un camino de piedras entre olivos. Los campesinos los saludan sin alegría. Pagan su parte a la orden, como antes la pagaban a otros. Bernat los entiende.", hacer = Hacer.TRABAJO),

        v("A esta hora está en la capilla del castillo con los hermanos, de pie, diciendo sus padrenuestros mientras el capellán canta las horas en latín. Bernat no entiende el latín, pero sabe cuándo arrodillarse, cuándo levantarse y cuándo decir amén. Eso, dice el capellán, también es rezar.", hacer = Hacer.REZAR),
        v("A esta hora reza en la capilla a la Virgen, patrona de la orden, por los hermanos muertos, que son muchos y cada año más. Dice sus nombres por dentro, uno por uno. Le lleva tiempo. Luego reza por los vivos, que le lleva menos.", hacer = Hacer.REZAR),
        v("A esta hora reza en la capilla entre los hermanos, que huelen a caballo y a hierro. La Regla dice cuántos padrenuestros por cada hora: trece por unas, siete por otras. Bernat los lleva con un cordón de nudos que le hizo su madre, uno por cada padrenuestro.", hacer = Hacer.REZAR),

        v("A esta hora come en el refectorio, en silencio, mientras un capellán lee la Regla en francés desde el púlpito. Los hermanos comen de dos en dos de la misma escudilla, y Bernat comparte la suya con un sargento de Tolosa que come muy despacio. Pan, habas, aceite, vino aguado.", hacer = Hacer.COMER),
        v("A esta hora come carne, porque hoy es uno de los tres días de carne de la semana: cordero con garbanzos y pan. Los hermanos comen con hambre de soldados. Lo que sobra va para los pobres de la puerta, que lo esperan sentados en las piedras.", hacer = Hacer.COMER),
        v("A esta hora come en el refectorio lo que haya, deprisa, que la campana no espera: pan, un poco de queso, un cuenco de lentejas. Los que vienen de guardia comen con la cota todavía puesta. Bernat mastica despacio, que es su única rebeldía.", hacer = Hacer.COMER),

        v("A esta hora duerme en el dormitorio de los sargentos, en su catre, con la camisa y las calzas puestas y la espada al lado, como manda la Regla. Una lámpara arde toda la noche. Alguien se queja en sueños en una lengua que Bernat no conoce.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con su pueblo del Ampurdán, con el viento que allí sopla, con su madre en la puerta. Se despierta y oye el viento de aquí, que es otro. Tarda un momento en saber cuál es el de verdad.", hacer = Hacer.DORMIR),
        v("A esta hora duerme el sueño corto del soldado, partido en dos por maitines, que se despierta con cualquier ruido y se vuelve a dormir enseguida. El castillo está en silencio. Solo se oye, de vez en cuando, el paso del centinela en la muralla.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El castillo de Safed está en lo alto de un monte, con dos murallas, una dentro de otra, torres redondas, fosos tallados en la roca, cisternas, molinos, una herrería y una capilla. Desde lejos parece una corona de piedra puesta sobre el monte. {entorno}"),
        v("{entorno} Abajo del castillo hay un pueblo de casas de piedra en la ladera, con judíos, cristianos de aquí y algunos sarracenos, que venden pan, aceite y tela a la guarnición. El mercado de los martes llena la plaza de burros."),
        v("Alrededor del castillo hay aldeas de campesinos, cristianos y sarracenos, que trabajan los campos y los olivares y pagan su parte a la orden. Los campesinos no quieren a los hermanos, ni a los que vinieron antes. Quieren que llueva. {entorno}"),
        v("En el castillo viven más de mil personas: los hermanos caballeros, los sargentos, los turcópolos, los ballesteros, los criados, los albañiles y los cautivos que trabajan en la obra. Es una ciudad dentro de una muralla, y huele como una. {entorno}"),
        v("{entorno} Desde las murallas de Safed se ve casi toda Galilea: el lago abajo, el valle del Jordán, los montes de Damasco al este. Por eso está aquí el castillo: para ver venir a los enemigos antes de que lleguen, y avisar a Acre con fuegos."),
        v("La capilla del castillo es pequeña y oscura, con una Virgen de madera pintada que trajo un hermano de Francia, y velas que siempre arden. Aquí se reza, se hace el capítulo de los viernes, se confiesan las faltas y se entierra a los que mueren. {entorno}"),
    )

    /** La obra del castillo (arco «muralla»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("muralla", 0.0, 0.6, frases = listOf(
            "Del lado del sur, la torre nueva es todavía un muñón de piedra con andamios de madera y una rampa por donde suben los cautivos.",
            "En la obra del sur hay montones de piedras, mortero y andamios, y la torre nueva apenas pasa la altura de un hombre.",
        )),
        Entorno("muralla", 0.6, 0.8, frases = listOf(
            "La torre del sur ya pasa la altura de la muralla, y los albañiles trabajan en lo alto, colgados de los andamios.",
            "La torre nueva del sur sube cada semana, piedra a piedra, y ya se ve desde el lago.",
        )),
        Entorno("muralla", 0.8, 1.0, frases = listOf(
            "La torre del sur está terminada, con su cruz arriba, y los albañiles ya pican en el muro del este.",
            "El castillo está casi entero: la torre nueva, los fosos y las murallas dobles.",
        )),
    )

    private val GENTE = listOf(
        v("El comendador del castillo es un caballero de Borgoña, alto y seco, que no ha hablado con Bernat más de diez veces en tres años, y siempre para darle una orden. Es justo, dicen los sargentos, y nunca sonríe. Bernat lo prefiere así a que sea al revés.", hasta = "comendador"),
        v("El comendador nuevo es un caballero de Poitou, joven y nervioso, que lee la Regla entera en voz alta y quiere saber el nombre de cada sargento. A Bernat le preguntó por el Moro. Los sargentos viejos no se fían de él todavía. Bernat, un poco.", desde = "comendador"),
        v("El Moro cojea desde el otoño de la pata de atrás, y el mariscal quiere darle otro caballo a Bernat. Bernat no quiere. Lo monta poco, lo cuida mucho y lo saca a pasear por la ladera las tardes de fiesta, despacio, como a un viejo.", desde = "moro_cojo"),
        v("Yusuf es el mozo de cuadras, un cristiano de esta tierra que reza en una lengua que Bernat no entiende y habla con los caballos en árabe. Sabe más de caballos que todos los hermanos juntos. Bernat le confía al Moro, que es lo único que le confía a nadie."),
        v("El capellán del castillo es un hermano gordo y amable que sabe latín, lee la Regla en voz alta en el refectorio y confiesa a los sargentos sin cansarse. Le escribe las cartas a Bernat, y le cambia las palabras para que su madre no se preocupe. Bernat lo sabe y se lo agradece."),
        v("Los turcópolos son jinetes de esta tierra, hijos de cristianos y de conversos, que pelean con arco a caballo como los turcos. Cobran de la orden y no hacen votos. Bernat los envidia un poco: pueden casarse, beber y reír en voz alta."),
        v("El Moro es el caballo de Bernat: castaño oscuro, de crin negra, nervioso con los extraños y tranquilo con él. Lo trajeron de Apulia de potro. Es lo único que tiene en el mundo, y ni siquiera es suyo, que es de la orden. Bernat le habla en catalán."),
        v("La madre de Bernat vive en un pueblo del Ampurdán, junto al mar, y le escribe una vez al año por mano del cura. Bernat le contesta por mano del capellán. Ninguno de los dos sabe escribir. Los dos saben que las cartas no dicen lo que dirían si supieran."),
        v("Ramon, su amigo, otro sargento catalán con quien vino desde Barcelona, está en Egipto con el ejército del rey Luis, con los hermanos que bajaron a la guerra. Bernat no tiene noticias desde el invierno. Reza por él cada noche, sin decirle a nadie que tiene miedo.", hasta = "mansura"),
        v("Ramon, su amigo, murió en Mansura, en Egipto, con casi todos los hermanos que bajaron con el rey. Bernat no tiene su cuerpo ni sabe dónde quedó. Tiene su cuchillo, que Ramon le dejó al irse, y lo lleva en el cinto. A veces le habla. Ramon no contesta.", desde = "misa_ramon"),
        v("Pere es el hermano joven que llegó de Cataluña y que el comendador le dio para que lo enseñe: alto, flaco, con los ojos de alguien que todavía no ha visto una batalla. Hace demasiadas preguntas y escucha todas las respuestas. A Bernat le recuerda a Ramon.", desde = "pere", hasta = "muere_pere"),
        v("Pere está enterrado en el cementerio del castillo, junto a la capilla, con una cruz de madera sin nombre, como todos los hermanos. Murió de fiebres, no de una flecha, que es como mueren casi todos aquí. Bernat pasa por delante cada mañana y no se para.", desde = "muere_pere"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Bernat lo sabe como se saben las cosas de la orden: porque se lo dijeron una vez y luego se lo gritaron."),
        v("Hay cosas que solo se miran cuando se pierden. {dato_objeto}"),
        v("{dato_objeto} En la orden se dice que un hermano sin su equipo es medio hermano, y que la otra mitad se la quita la Regla."),
        v("{dato_objeto} Son cosas que se aprenden en el castillo, como casi todo."),
        v("Un hermano no tiene nada suyo: todo lo que lleva es de la orden, hasta la camisa. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("espada", "la espada", "una espada", "La espada",
            "La espada de un sargento es más sencilla que la de un caballero, sin adornos, de hoja ancha y punta redonda, para cortar más que para pinchar; se afila en una piedra de agua y se aceita para que no se oxide, y la Regla prohíbe adornarla con oro o plata."),
        ObjetoCuento("lanza", "la lanza", "una lanza", "La lanza",
            "La lanza es un palo de fresno largo como dos hombres, con una punta de hierro; se lleva bajo el brazo en la carga, apretada contra el costado, y casi siempre se rompe al primer golpe; por eso en la cuadra hay más lanzas que caballos."),
        ObjetoCuento("escudo", "el escudo", "un escudo", "El escudo",
            "El escudo es de madera forrada de cuero, con forma de almendra cortada arriba, pintado de blanco con la cruz roja de la orden; se cuelga del cuello con una correa larga, y en la marcha se lleva a la espalda, donde para las flechas que vienen por detrás."),
        ObjetoCuento("cota", "la cota de malla", "una cota de malla", "La cota",
            "La cota de malla está hecha de miles de anillos de hierro, cada uno cerrado con un remache, y pesa como un niño de cinco años; se lleva sobre un jubón acolchado, se oxida con nada, y para limpiarla se mete en un saco con arena y se sacude."),
        ObjetoCuento("manto", "el manto pardo", "un manto pardo", "El manto",
            "El manto de un sargento es pardo o negro, de lana basta, con la cruz roja cosida en el pecho; el de los caballeros es blanco; perder el manto por una falta es el peor castigo después de que te echen, y se queda uno sin él un año y un día."),
        ObjetoCuento("silla", "la silla de montar", "una silla de montar", "La silla",
            "La silla de montar de guerra tiene el arzón de delante y el de detrás altos, para que el jinete no se caiga cuando la lanza choca; es de madera forrada de cuero, con estribos largos, y se monta casi de pie."),
        ObjetoCuento("herradura", "la herradura", "una herradura", "La herradura",
            "La herradura es de hierro, con agujeros para seis u ocho clavos, y la hace y la pone el herrero del castillo; un caballo de guerra gasta un juego cada mes en estos caminos de piedra, y la herrería del castillo no para nunca."),
        ObjetoCuento("almohaza", "la almohaza", "una almohaza", "La almohaza",
            "La almohaza es un peine de hierro con dientes cortos para quitar el barro y el pelo muerto de los caballos; se pasa en círculos, contra el pelo y luego a favor, y a los caballos les gusta tanto que cierran los ojos."),
        ObjetoCuento("regla", "la Regla", "un libro de la Regla", "La Regla",
            "La Regla de la orden está escrita en francés para que la entiendan los hermanos que no saben latín, y tiene cientos de artículos: cómo se come, cuántos caballos, qué se hace en batalla, cuál es el castigo de cada falta; se lee en voz alta en el refectorio, a trozos, todo el año."),
        ObjetoCuento("escudilla", "la escudilla", "una escudilla", "La escudilla",
            "La escudilla de madera se comparte entre dos hermanos en el refectorio, como manda la Regla, para que cada uno vigile que el otro coma lo que necesita; si uno de los dos come demasiado, el otro se queda con hambre, y se aprende a comer despacio."),
        ObjetoCuento("vela", "la vela", "una vela", "La vela",
            "La vela de la capilla es de cera de abeja, y la del dormitorio de sebo; la Regla manda que en el dormitorio haya luz toda la noche, para que los hermanos no estén a oscuras y el enemigo no sorprenda a nadie desnudo."),
        ObjetoCuento("campana", "la campana", "una campana", "La campana",
            "La campana de la capilla del castillo marca las horas del rezo, de día y de noche, y también los peligros: tocada deprisa, llama a las armas; tocada lenta, a un muerto; desde las aldeas se oye, y la gente sabe qué pasa en el castillo sin subir."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} Bernat piensa en la orden como se piensa en una familia que no se eligió: le da de comer, lo viste, le dice qué hacer y, si muere, reza por él. A sus {edad} años no sabe si eso es bastante. Casi siempre cree que sí. Algunas noches, no."),
        v("Piensa a veces en por qué vino. Tenía diecinueve años, una madre viuda, un hermano mayor que se quedaba la tierra, y un fraile que predicaba la cruzada en la plaza. Eso es todo. Ahora tiene una cruz en el pecho y un castillo que defender. No se arrepiente. No del todo."),
        v("{mundo} Él piensa en cosas más cercanas: en el Moro, que come menos; en la cota, que tiene un anillo roto; en la guardia de esta noche. Las cosas grandes son del maestre y del rey, que las piensan en Acre."),
        v("{mundo} A sus {edad} años, Bernat ha visto morir a más hermanos de fiebre que de flechas, y a más de flechas que de espadas. La guerra, piensa, es sobre todo esperar y enfermarse. Lo de las canciones pasa poco, y dura un momento."),
        v("Piensa en los campesinos de las aldeas, que hablan otra lengua, rezan a otro dios o al mismo de otra manera, y le pagan su parte a la orden sin mirarlo. Se pregunta si él, en su lugar, lo miraría. Concluye que no. Luego sigue trabajando."),
        v("{mundo} A veces, de noche, en la muralla, mirando las luces de las aldeas sarracenas del otro lado del lago, Bernat piensa que allí también hay un hombre de guardia mirando hacia aquí, con frío y con sueño, que piensa lo mismo que él. Lo saluda en silencio."),
    )

    private val MUNDOS = mapOf(
        "mansura" to listOf(
            "En el castillo se habla de Egipto, de la batalla de Mansura y de los hermanos que no volverán.",
            "Se dice que el rey Luis pagó un rescate enorme por salir preso de Egipto, y que ahora está en Acre.",
            "Se habla del maestre nuevo, y de si será tan bueno como el que se perdió.",
        ),
        "herida" to listOf(
            "Se habla de la muerte del emperador Federico, que estuvo excomulgado y fue rey de Jerusalén.",
            "Se dice que los de Damasco y los de Egipto se pelean entre ellos, y que eso es bueno para los cristianos.",
        ),
        "acre" to listOf(
            "Se habla del rey Luis, que gasta su dinero en arreglar las murallas de las ciudades de la costa.",
            "Se dice que el maestre Renaud se peleó con el rey y que lo van a cambiar.",
        ),
        "torre" to listOf(
            "Se habla de la reina Blanca, la madre del rey Luis, que murió en Francia, y de que el rey tendrá que volver.",
            "Se dice que vienen los tártaros desde el este, y que nadie sabe de qué lado estarán.",
        ),
        "rey_se_va" to listOf(
            "Se habla de que el rey Luis se vuelve a Francia, y de que sin él la tierra quedará más sola.",
            "Se dice que en Europa los papas y los reyes se pelean, y que nadie piensa en esta tierra.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde suena la campana de vísperas, y Bernat deja lo que está haciendo y va a la capilla con los demás. Por la puerta abierta entra la luz naranja del sol que se pone detrás de los montes. Después, la cena, completas, y el gran silencio."),
        v("Anochece en Safed, y desde la muralla Bernat ve cómo el lago se pone morado y luego negro, y cómo se encienden las luces de las aldeas, abajo, una por una. En el castillo se cierran las puertas con barras de hierro. Él baja a completas."),
        v("Después de completas, nadie habla en el castillo hasta la mañana, salvo para dar una alarma. Bernat sube al dormitorio de los sargentos, se quita el cinto, deja la espada junto al catre. La lámpara de la noche ya está encendida. Se duerme enseguida, como siempre."),
        v("De noche, el castillo es una isla de luces en la oscuridad del monte: las antorchas de la muralla, la lámpara de la capilla, la del dormitorio. Bernat las ve desde el patio, antes de subir. Desde las aldeas, piensa, el castillo debe parecer una estrella caída."),
        v("La última luz del día se queda un momento en lo alto del monte de la nieve, al norte, rosada, cuando todo lo demás ya está a oscuras. Bernat la mira desde la torre cada tarde que puede. Es lo más bonito que hay en esta tierra, y no es de nadie."),
        v("Al anochecer baja un frío de golpe, que en esta tierra no avisa, y los hermanos de guardia se ponen las capas y los guantes. Bernat sube a la muralla con la suya, y se queda mirando el camino de Damasco hasta que no se ve.", meses = setOf(11, 12, 1, 2, 3)),
        v("Al caer la tarde, en verano, el calor sigue saliendo de las piedras del castillo aunque el sol ya se haya ido, y los hermanos se sientan en el patio a esperar que refresque. No refresca. Bernat va a completas con la camisa pegada a la espalda.", meses = setOf(6, 7, 8, 9)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "candelaria" to unDia(
            Parrafo.MANANA to "Hoy es la Candelaria, fiesta de la Virgen, que es la patrona de la orden, y en la capilla del castillo el capellán bendice las velas, una para cada hermano. Luego la guarnición entera da la vuelta al patio en procesión, cada uno con su luz.",
            Parrafo.PUEBLO to "La procesión de las velas da la vuelta al patio del castillo, entre la herrería, las cuadras y la cisterna, con los hermanos delante y los criados detrás. Los cautivos de la obra miran desde su rincón. El viento apaga unas velas, y los hermanos se las vuelven a encender unos a otros.",
            Parrafo.PENSAMIENTO to "Bernat lleva su vela con la mano que no sirve para la espada, y piensa en la Virgen, que es lo único dulce de la orden. A ella le reza de verdad, no los padrenuestros de las horas. Le pide siempre lo mismo: volver a ver a su madre una vez.",
        ),
        "cuaresma" to unDia(
            Parrafo.MANANA to "Hoy empieza la cuaresma: el capellán pone ceniza en la frente de cada hermano, y desde hoy hasta Pascua no hay carne, ni huevos, ni queso, y se come una vez al día. La orden ayuna como los monjes, aunque pelea como los soldados.",
            Parrafo.TRABAJO to "A esta hora hace instrucción con el estómago vacío, como todos, y los caballos parecen notar que los jinetes están más flojos. El mariscal no perdona: el enemigo no sabe que es cuaresma, dice. Bernat carga, gira, vuelve. Le zumban los oídos.",
            Parrafo.ANOCHECER to "Esta noche, la única comida del día: pan, habas y agua con un poco de vino. Los hermanos comen en silencio, de dos en dos, con más hambre que nunca. Bernat le deja a su compañero de escudilla la mitad del pan. Mañana, quizás, se arrepienta.",
        ),
        "anunciacion" to unDia(
            Parrafo.MANANA to "Hoy es la Anunciación, otra fiesta de la Virgen, y en plena cuaresma la orden la celebra con misa solemne y un respiro en el ayuno. En la capilla se encienden todas las velas y se pone la Virgen de madera en andas, con un manto nuevo.",
            Parrafo.PUEBLO to "En la capilla, la Virgen de madera pintada, la que trajo un hermano de Francia, lleva hoy un manto azul que cosieron las mujeres del pueblo de abajo. Los hermanos se arrodillan delante, uno por uno. Fuera, en las laderas, ya hay flores amarillas.",
            Parrafo.PENSAMIENTO to "Bernat se arrodilla delante de la Virgen y piensa en el ángel que le anunció a ella lo que iba a pasar. A él nadie le anuncia nada: las noticias llegan tarde, por caminos, y siempre son de muertos. Le pide a la Virgen que la próxima sea buena.",
        ),
        "pascua" to unDia(
            Parrafo.MANANA to "Hoy es Pascua, y se acaba la cuaresma. Anoche, en la vigilia, el capellán encendió el fuego nuevo en la puerta de la capilla y el cirio grande. Hoy toca la campana todo lo que puede, y los hermanos se saludan con el beso de la paz.",
            Parrafo.TRABAJO to "A esta hora no hay instrucción ni obra: es Pascua, y en el castillo solo hacen guardia los de turno. Bernat no tiene turno. Pasea por la muralla, despacio, mirando las laderas verdes y el lago azul, y se siente casi en paz.",
            Parrafo.ANOCHECER to "Esta noche, en el refectorio, hay carne por primera vez en cuarenta días: cordero asado con hierbas, pan blanco y vino sin aguar. Los hermanos comen con tanto apetito que el capellán tiene que leer más alto. Bernat come despacio, para que dure.",
        ),
        "siega" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó la siega en las aldeas del castillo: los campesinos cortan el trigo y la cebada con hoces, en los campos de la ladera, desde el amanecer. La orden manda hermanos a caballo a vigilar, porque la siega es cuando atacan los de Damasco: el trigo es lo que quieren.",
                Parrafo.TRABAJO to "A esta hora vigila a caballo los campos donde se siega, con la lanza en la mano y el sol en la nuca, mientras los campesinos cortan y atan los haces. Los mira sin mirarlos. Mira sobre todo el horizonte, por donde vendrían los jinetes.",
                Parrafo.PUEBLO to "Por los caminos que suben al castillo van los burros cargados de haces, y en las eras de las aldeas se trilla y se avienta. El aire está lleno de paja dorada. La parte de la orden sube al castillo, a los graneros, y los campesinos la miran subir.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue vigilando la siega, a caballo, al borde de un campo. Hace calor. Los campesinos cantan algo que no entiende. No viene nadie.",
                "A esta hora ayuda a subir los sacos de grano de la orden a los graneros del castillo. Pesan. Los cuenta el hermano del granero, que no se fía de nadie.",
                "A esta hora vigila la trilla en la era de una aldea, sentado a la sombra de un olivo, con la espada en las rodillas. Un niño le trae agua. Bernat le da las gracias en árabe, mal.",
            )),
        ),
        "asuncion" to unDia(
            Parrafo.MANANA to "Hoy es la Asunción de la Virgen, la fiesta grande de la orden, y el castillo se viste: banderas en las torres, flores en la capilla, la Virgen de madera con el manto bueno. Hasta los cautivos tienen el día libre. El calor aprieta desde temprano.",
            Parrafo.TRABAJO to "A esta hora está en la misa solemne de la Asunción, en la capilla llena, con los caballeros delante y los sargentos detrás. Hace un calor que derrite las velas. El capellán predica largo. Bernat aguanta de pie, con la cota de las fiestas encima.",
            Parrafo.ANOCHECER to "Esta noche se cena como en las fiestas grandes: carne, pan blanco, uvas, higos y vino bueno. Después, en el patio, alguien canta una canción de Provenza y nadie lo manda callar, que es fiesta. Bernat canta bajito, en catalán, la que sabe su madre.",
        ),
        "natividad_virgen" to unDia(
            Parrafo.MANANA to "Hoy es el nacimiento de la Virgen, y es fiesta en la orden, que tiene muchas fiestas de la Virgen y las cumple todas. Misa, procesión corta por el patio, comida mejor. Las primeras nubes del otoño pasan sobre el lago sin dejar caer nada.",
            Parrafo.PUEBLO to "En el pueblo de abajo, los cristianos de esta tierra también celebran hoy a la Virgen, con su propio cura y sus propios cantos, que suenan raros, como de otro tiempo. Desde la muralla se oyen. Bernat no los entiende, y le gustan.",
            Parrafo.PENSAMIENTO to "Bernat piensa en la Virgen niña, en su madre, en su casa, y luego, sin querer, en la suya, en el Ampurdán, y en su madre vieja. Se pregunta si estará viva. La carta tarda un año. Puede que ya no. Lo piensa y reza más fuerte.",
        ),
        "aceitunas" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empezó la cosecha de la aceituna en los olivares de la ladera, que son viejos como el mundo, retorcidos y grises. Los campesinos varean las ramas con palos largos y las mujeres recogen las aceitunas del suelo en cestos. Hace fresco por fin.",
                Parrafo.TRABAJO to "A esta hora vigila a caballo la cosecha de la aceituna, entre los olivos, porque también ahora vienen a veces los de Damasco a robar. No viene nadie. Un niño le ofrece una aceituna cruda, por broma. Bernat la muerde, y el niño se ríe de su cara.",
                Parrafo.PUEBLO to "En la almazara del pueblo de abajo, la piedra de moler gira día y noche tirada por un burro con los ojos tapados, y el aceite nuevo sale verde y amargo. La parte de la orden sube al castillo en tinajas. Todo el pueblo huele a aceite.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue vigilando la cosecha entre los olivos. Los campesinos cantan mientras varean. Es lo más tranquilo que hace en todo el año.",
                "A esta hora sube tinajas de aceite al almacén del castillo con Yusuf y una mula. El hermano del almacén las cuenta dos veces.",
                "A esta hora hace guardia en la muralla, porque los demás están en los olivares. El castillo está medio vacío y en silencio.",
                "A esta hora ayuda a los campesinos a cargar un carro que se atascó en el barro, porque ha llovido. No es su trabajo. Lo hace igual.",
            )),
        ),
        "todos_santos" to unDia(
            Parrafo.MANANA to "Hoy es Todos los Santos, y la orden dice misa por los hermanos muertos, que son muchos: el capellán lee sus nombres, los que sabe, y los que no sabe los encomienda todos juntos. La lista es más larga cada año. Hoy la escuchan todos de pie.",
            Parrafo.PUEBLO to "En el cementerio del castillo, junto a la capilla, las cruces de madera de los hermanos están en fila, sin nombre, como manda la orden. Hoy se les pone una vela a cada una. Con el viento, se apagan. Los criados las vuelven a encender toda la tarde.",
            Parrafo.PENSAMIENTO to "Bernat escucha los nombres de los muertos y cuenta los que conoció. Cada año son más. Piensa que algún día alguien oirá el suyo, o no lo oirá, porque se habrá perdido en un campo lejano, como tantos. Le da igual. Le importa que alguien rece.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad en el castillo, y anoche hubo misa a medianoche en la capilla, con todas las velas y el capellán cantando la vieja historia del niño en Belén, que está aquí al lado, a pocos días de camino, y que nadie de la guarnición ha visto.",
            Parrafo.TRABAJO to "A esta hora hace guardia en la muralla, porque alguien tiene que hacerla aunque sea Navidad, y le tocó a él. Hace frío. Llueve un poco. Desde la torre se oye a los hermanos cantar en la capilla. Bernat canta con ellos desde arriba, para nadie.",
            Parrafo.ANOCHECER to "Esta noche, en el refectorio, hay carne, pan blanco, dátiles, higos secos y vino del bueno. Los hermanos se desean paz en voz baja, que hoy se permite. Bernat le dice a su compañero de escudilla, en catalán, que le desea un año sin flechas.",
        ),
        "mansura" to unDia(
            Parrafo.MANANA to "Hoy llegó un mensajero de Acre con la noticia: en Egipto, en un lugar llamado Mansura, los hermanos cargaron contra la ciudad detrás del hermano del rey y cayeron casi todos, dentro de las calles, sin salida. De los que bajaron, volvieron muy pocos.",
            Parrafo.GENTE to "En el castillo nadie habla, ni por señas. El comendador reúne a los hermanos en la capilla y les dice lo que sabe, que es poco: no hay lista de los muertos todavía. Bernat no pregunta por Ramon. No hace falta preguntar. Todos están pensando en alguien.",
            Parrafo.PENSAMIENTO to "Bernat piensa en Ramon, que se fue a Egipto riendo, con su cuchillo nuevo, diciendo que volvería con un caballo sarraceno. Piensa que no volverá. No lo sabe, pero lo sabe. Esa noche no duerme, y en maitines dice los padrenuestros de dos en dos.",
        ),
        "misa_ramon" to unDia(
            Parrafo.MANANA to "Hoy el capellán dijo misa por los hermanos muertos en Egipto, y leyó los nombres que llegaron de Acre en una carta. Entre ellos, el de Ramon. Bernat lo oyó de pie, sin moverse. Luego se arrodilló, y tardó en levantarse.",
            Parrafo.GENTE to "Después de la misa, el comendador, que nunca le habla, se le acerca y le pone la mano en el hombro un momento, sin decir nada. Luego se va. Yusuf le trae al Moro ensillado, sin que nadie se lo pida. Bernat sale a cabalgar solo por la ladera.",
            Parrafo.PENSAMIENTO to "Bernat piensa en Ramon, en el barco que los trajo desde Barcelona, mareados los dos, en las noches de guardia contándose mentiras de mujeres. No sabe dónde quedó su cuerpo. Piensa que en Egipto, junto a un río. Le parece demasiado lejos para estar solo.",
        ),
        "maestre" to unDia(
            Parrafo.MANANA to "Hoy llegó otra noticia de Egipto, peor que la primera: el ejército del rey se rindió, el rey Luis está preso de los sarracenos, y el maestre de la orden, el hermano Guillermo, murió en la retirada.",
            Parrafo.PUEBLO to "En el castillo se dobla la guardia y se manda gente a vigilar los caminos, por si los de Damasco aprovechan. En el pueblo de abajo, la gente mira el castillo y habla bajito. Unos están contentos. No lo dicen. Se les nota.",
            Parrafo.PENSAMIENTO to "Bernat piensa que la orden no tiene maestre, el rey no tiene ejército y Egipto no tiene fin. Piensa que esta tierra se los come a todos, uno por uno, y que los que quedan son cada vez menos. Luego va a las cuadras y cepilla al Moro, que no sabe nada.",
        ),
        "rey_acre" to unDia(
            Parrafo.MANANA to "Hoy llegó la noticia de que el rey Luis salió libre de Egipto, después de pagar un rescate enorme, y llegó a Acre por mar con lo que le queda de su ejército: pocos, flacos, enfermos. Dicen que se quedará en la tierra en vez de volver a Francia.",
            Parrafo.PUEBLO to "En el castillo hay algo parecido a la alegría por primera vez en meses: el rey está libre, el rey se queda. Los hermanos hablan por señas de lo que hará: arreglar las murallas, pagar caballeros, casar princesas. En la cuadra, alguien silba.",
            Parrafo.PENSAMIENTO to "Bernat piensa en el rey, que dicen que es un santo y que lavaba los pies de los pobres en Francia. Un santo que perdió una guerra, piensa. Le parece que eso es lo que más se parece a esta tierra: los santos aquí también pierden.",
        ),
        "torre_sur" to unDia(
            Parrafo.MANANA to "Hoy el comendador puso a Bernat a cargo de la cuadrilla que sube la torre del sur del castillo: albañiles, cautivos, mulas y un maestro de obras de Marsella que grita mucho. Bernat no sabe nada de piedras. El comendador le dijo que sabe de hombres, y que basta.",
            Parrafo.TRABAJO to "A esta hora está en la obra de la torre, entre polvo, gritos y piedras que suben con poleas. El maestro de obras le explica cómo se pone una piedra. Bernat asiente. Luego vigila a los cautivos, que suben la rampa con las cadenas sonando, y les da agua.",
            Parrafo.PENSAMIENTO to "Bernat mira la torre que no existe todavía, un hueco con andamios, y piensa que la verá terminada, si Dios quiere. Le parece una cosa rara para un sargento: hacer en vez de deshacer. Le gusta más de lo que diría.",
        ),
        "mamelucos" to unDia(
            Parrafo.MANANA to "Hoy se dice en el castillo que en Egipto ya no manda el sultán de antes, sino los mamelucos, los soldados esclavos que lo servían, y que el que manda es uno de ellos, casado con la viuda del sultán. Es la noticia más rara del año.",
            Parrafo.GENTE to "Los hermanos viejos discuten en el patio, en voz baja, después de vísperas, si eso es bueno o malo: unos dicen que los esclavos pelearán peor, otros que mejor, porque no tienen nada que perder. Yusuf, que oye todo, dice que los mamelucos son los que ganaron en Mansura.",
            Parrafo.PENSAMIENTO to "Bernat piensa en los mamelucos, que eran esclavos y ahora son reyes, y en sí mismo, que era libre y ahora obedece en todo. Le parece que el mundo está al revés. Luego piensa que quizás siempre lo estuvo, y que solo ahora se da cuenta.",
        ),
        "carta_madre" to unDia(
            Parrafo.MANANA to "Hoy le llegó a Bernat una carta de su madre, desde el Ampurdán, que tardó un año en llegar. El comendador la leyó primero, como manda la Regla, y luego se la dio, abierta. Bernat no sabe leer: se la lee el capellán, en voz alta, en catalán.",
            Parrafo.GENTE to "La madre le cuenta que su hermano tuvo otro hijo, que la viña dio poco, que el cura está viejo, que la tramontana se llevó el tejado del corral. Le dice que rece. Le dice que coma. Le dice que vuelva. El capellán lee esa parte más despacio.",
            Parrafo.PENSAMIENTO to "Bernat le pide al capellán que le lea la carta otra vez, y otra. Luego la guarda. Piensa en contestarle esta misma noche, y en qué le dirá: que está bien, que come, que no puede volver. Le pedirá al capellán que lo diga más bonito.",
        ),
        "peregrinos" to unDia(
            Parrafo.MANANA to "Hoy Bernat salió del castillo antes del amanecer con otros cuatro sargentos y seis turcópolos, a escoltar una caravana de peregrinos que quieren llegar a Nazaret, donde vivió la Virgen. Son veinte: franceses, alemanes, dos mujeres viejas y un obispo.",
            Parrafo.TRABAJO to "A esta hora cabalga junto a los peregrinos por los caminos de Galilea, vigilando las lomas, mientras ellos cantan y lloran y señalan cada piedra como si fuera santa. Una de las viejas le pregunta si ha visto milagros. Bernat le dice que no. Ella no le cree.",
            Parrafo.PENSAMIENTO to "Bernat mira a los peregrinos, que vienen de tan lejos para tocar una piedra, y piensa que él vive en esta tierra santa desde hace años y ya no la ve. Para él son caminos, cisternas y emboscadas. Le da un poco de vergüenza. Mira las piedras otra vez.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "sargento", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + HERIDO_MANANA + ACRE_MANANA,
            Parrafo.TIEMPO to TIEMPO + ACRE_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + HERIDO_TRABAJO + ACRE_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + HERIDO_PUEBLO + ACRE_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + HERIDO_ANOCHECER + ACRE_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
