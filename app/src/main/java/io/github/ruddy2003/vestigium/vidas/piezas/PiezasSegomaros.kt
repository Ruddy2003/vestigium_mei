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
 * El cuento de Segomaros, aprendiz de los druidas en Gergovia, del 54 al 50 a. C. (Galia). Es una persona inventada en
 * un mundo real. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026, y la voz del tema
 * (`docs/diseno/temas/galia.md`): directa y orgullosa, de campamento y de asamblea, con el respeto del bosque; habla de lo
 * que se ve (el humo, el caballo, la lluvia, el grito que corre) y de lo que se cree sin explicarlo.
 *
 * Lo que este cuento cuida (PLAN_TEMAS, 10e, y `docs/diseno/temas/fuentes_galia.md`):
 * - **Las creencias son las suyas,** y solo las que dicen los textos: el alma no muere y pasa a otro cuerpo (César,
 *   *Guerra de las Galias* 6.14; Diodoro 5.28), la cuenta por noches y el padre de todos, que los romanos llaman Dis
 *   Pater (6.18), lo sagrado que no se escribe (6.14), el muérdago y la hoz de oro (Plinio 16.249–251). Los dioses son los
 *   de Lucano (Teutates, Esus, Taranis) y los que César nombra a la romana (6.17). Nada de mitos irlandeses ni galeses,
 *   nada de ogham, nada de druidismo moderno, y no se inventa ninguna creencia ni ningún rito que los textos no digan.
 * - **César es fuente enemiga y escribe para ganar:** lo que dice César va como lo que dice César, entre paréntesis o
 *   con «César cuenta». Lo que dicen Plinio, Diodoro, Estrabón o Lucano, igual, con su pasaje.
 * - **Las palabras de otros van en estilo indirecto,** con la cita al lado. Lo poco que va tal cual es de la traducción
 *   de César de José Goya y Muniain (dominio público), entre comillas españolas y con su pasaje al lado: «…» (César 7.89).
 *   La prueba (`CuentoG9Prueba`) comprueba cada frase contra el texto copiado de Wikisource el 15 nov 2026.
 * - **Vercingétorix y la demás gente real salen de lejos** y como los cuentan las fuentes; nada de lo que hacen con él es
 *   inventado sobre ellos. Sacrificios y guerra, sin detalle cruento.
 * - **La entrega como victoria de honra** es decisión del autor y lectura del personaje: se cuenta como lo que él cree.
 *
 * Lo que tiene que respetar de su calendario (`vidas/anios/AnioG9.kt`):
 * - La escuela: va hasta `deja_escuela` (temporada 3) y vuelve desde `vuelve_escuela` (5). En medio, repite los versos
 *   solo.
 * - Su yegua, de `yegua` (1) a `sale_caballeria` (3), cuando Esunertos se la lleva de Alesia; el potro, desde `potro` (4).
 *   La herida del brazo, desde `caballeria` (3).
 * - Esunertos le enseña armas hasta que empieza el cerco de Alesia (`alesia`, 3); de `liberados` a `esunertos` no se sabe
 *   de él; desde `esunertos`, se sabe que cayó.
 * - Su hermana: prometida de `promesa` (2) a `boda` (4); casada en la montaña desde `boda`; con un hijo desde `sobrino` (5).
 * - Vercingétorix: no sale en los días de siempre hasta que lo echan de Gergovia (`echado`, 3); rey hasta la entrega;
 *   preso desde `entrega`.
 * - Tres estados de la temporada 3: «campana» (con los jinetes), «alesia» (el cerco) y «cautivo» (de la entrega a que
 *   César devuelve a los cautivos arvernos). En cada uno, la mañana, el tiempo, el pueblo, la gente, el anochecer y el
 *   trabajo de cada hora son suyos; el objeto y lo que piensa valen en todas partes.
 * - No repite lo que ya dice «Su vida»: la meseta con su muralla y sus casas de paja dicha en una frase, el robledal de
 *   la escuela, que lo sagrado no se escribe aunque se usen letras griegas para las cuentas, y el alma que pasa a otro
 *   cuerpo dicha en una frase.
 */
internal object PiezasSegomaros {
    private const val CAMPANA = "campana"
    private const val ALESIA = "alesia"
    private const val CAUTIVO = "cautivo"

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    /** Con los jinetes de Vercingétorix (temporada 3). */
    private fun ca(texto: String, hacer: Hacer? = null) = v(texto, estado = CAMPANA, hacer = hacer)

    /** En el cerco de Alesia (temporada 3). */
    private fun al(texto: String, hacer: Hacer? = null, desde: String? = null, hasta: String? = null) = v(texto, estado = ALESIA, hacer = hacer, desde = desde, hasta = hasta)

    /** Cautivo de los romanos, de la entrega a que lo devuelven (temporada 3). */
    private fun cu(texto: String, hacer: Hacer? = null) = v(texto, estado = CAUTIVO, hacer = hacer)

    private val INVIERNO = setOf(11, 12, 1, 2)
    private val VERANO = setOf(6, 7, 8)

    // =====================================================================
    //  La mañana
    // =====================================================================

    private val MANANA = listOf(
        v("{nombre} se despierta antes que nadie en la casa grande, con el fuego del centro hecho brasas y el humo quieto bajo las vigas. Se envuelve en el manto, pasa por encima de los que duermen y sale. Afuera la meseta todavía está a oscuras, y el ganado ya se mueve en los corrales."),
        v("Lo despierta el frío que entra por debajo de la puerta de tablas. {nombre} se sienta en la piel de oveja donde ha dormido y escucha: los perros, un gallo, alguien que sopla las brasas. Los suyos cuentan el tiempo por noches, y la noche que va delante de este día ya se acabó."),
        v("{nombre} sale de la casa cuando el cielo se pone gris por el lado del río. Desde el borde de la meseta se ve la llanura con su niebla, y al otro lado, lejos, la fila de los montes redondos. Abajo, entre los robles, sube un hilo de humo: en la escuela ya hay alguien despierto."),
        v("La primera luz entra por el hueco del humo, en lo alto del techo de paja, y baja como un palo blanco hasta el hogar. {nombre} la mira mientras se ata las calzas. Su madre ya está en pie, removiendo la olla. Le dice algo sin volverse, y él le contesta lo mismo que cada mañana."),
        v("{nombre} se lava la cara en el agua del cubo, que en las noches de frío amanece con una tela de hielo. Se peina con los dedos, se echa el manto sobre los hombros y lo sujeta con la fíbula. Afuera huele a humo de leña, a estiércol y a pan."),
        v("En invierno la mañana tarda en llegar. {nombre} sale con una tea encendida a los corrales, entre una niebla que se queda pegada a la meseta, y las vacas lo reciben con su aliento blanco. Todo está mojado. Hasta el silencio parece mojado.", meses = INVIERNO),
        v("En verano amanece pronto y el aire ya está tibio. {nombre} sale descalzo a la puerta, con el manto colgado del hombro, y oye a las golondrinas que entran y salen de los aleros de paja. En la llanura, el trigo todavía no se mueve.", meses = VERANO),
        v("Desde que dejó la escuela, {nombre} se despierta sin saber para qué. Antes la mañana tenía su camino: bajar al robledal, saludar al maestro, empezar los versos. Ahora sale, mira el bosque desde arriba, y se va a los caballos.", desde = "deja_escuela", hasta = "vuelve_escuela"),
        v("{nombre} se despierta con el brazo de la herida dormido, y lo abre y lo cierra despacio hasta que vuelve a ser suyo. La marca es una raya blanca, larga como un dedo. Su madre no la mira. Él tampoco, casi nunca.", desde = "caballeria"),
    )

    private val CAMPANA_MANANA = listOf(
        ca("{nombre} se despierta en el suelo, envuelto en el manto, entre los caballos atados y los fuegos apagados del campamento. Alrededor duermen cientos de jinetes de una docena de pueblos. El rocío le ha mojado la barba. Su yegua lo mira, esperando el grano."),
        ca("Lo despierta el cuerno del campamento, ronco, antes del alba. {nombre} se levanta de un salto, como todos, y busca con la mano la lanza, que está donde la dejó. Los que hacen la guardia de la noche vuelven a los fuegos frotándose las manos."),
        ca("{nombre} amanece lejos de Gergovia, en un claro del bosque donde el ejército acampó de noche. Huele a humo, a cuero mojado y a caballo. Se lava la cara en un arroyo con los demás, sin hablar. Hoy, otra vez, se monta."),
    )

    private val ALESIA_MANANA = listOf(
        al("{nombre} se despierta en Alesia, en el suelo de una casa de los mandubios donde duermen veinte hombres. Desde la muralla se oye, abajo, a los romanos que ya están cavando: el golpe de las palas, los gritos de los que mandan. Cavan todos los días, desde el alba."),
        al("Lo despierta el hambre, antes que la luz. {nombre} se queda quieto un rato para no gastar fuerzas. Luego se levanta, se ata el manto y sube a la muralla a ver lo que hicieron los romanos en la noche: siempre hay algo más, una torre, un foso, una empalizada."),
        al("{nombre} amanece en la colina de Alesia, entre dos ríos, y lo primero que ve es la línea romana, que ya rodea el monte entero como un cinturón. Detrás, otra más lejos, mirando hacia afuera. Al principio no entendía para qué. Ahora sí."),
    )

    private val CAUTIVO_MANANA = listOf(
        cu("{nombre} se despierta en el suelo, al raso, dentro de un cercado de estacas, entre miles de galos sin armas. Los guardias romanos cambian de turno en la puerta. Nadie habla mucho. Hay quien todavía duerme con la mano donde estaba la espada."),
        cu("Lo despierta la voz de un soldado que grita en una lengua que no entiende. {nombre} se sienta. Los de su lado son arvernos, todos: César los ha apartado de los demás. Dicen que para algo. Nadie sabe para qué."),
    )

    // =====================================================================
    //  El tiempo y la estación
    // =====================================================================

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} Los viejos de la meseta dicen que el tiempo se lee en los montes de enfrente, y casi nunca se equivocan."),
        v("{nombre} mira el cielo desde el borde de la meseta, como lo miran los pastores antes de subir el ganado. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Aquí arriba el viento llega antes que a la llanura, y se va después."),
        v("{cielo} En {estacion} el bosque huele distinto: a hoja podrida, a resina, a tierra mojada o a polvo."),
        v("{de_estacion} {cielo} Los druidas enseñan cosas de los astros y de la naturaleza de las cosas (César, Guerra de las Galias 6.14), y {nombre} mira el cielo como quien repasa una lección."),
        v("Desde Gergovia se ve medio mundo: la llanura, el río, los montes del oeste con sus cumbres redondas. {cielo} {de_estacion}"),
    )

    private val CAMPANA_TIEMPO = listOf(
        ca("{cielo} Lejos de la meseta, {nombre} mira el cielo para saber si mañana se podrá cabalgar. {de_estacion}"),
        ca("{de_estacion} {cielo} En el campamento el tiempo se mide en lo que pesan los mantos mojados y en lo que tardan en secarse."),
    )

    private val ALESIA_TIEMPO = listOf(
        al("{cielo} Desde la muralla de Alesia, el cielo es lo único que los romanos no han cercado. {de_estacion}"),
        al("{de_estacion} {cielo} {nombre} mira las colinas de alrededor, por donde tiene que llegar la ayuda, y mira el cielo encima de ellas."),
    )

    private val CAUTIVO_TIEMPO = listOf(
        cu("{cielo} {de_estacion} Sin techo y sin manto bueno, {nombre} siente el tiempo en los huesos."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y alto, y los montes de lejos se ven como si estuvieran cerca.",
            "No hay una nube, y la luz pega de lado y alarga las sombras.",
            "Hace un día claro, de esos en que se ve, muy lejos, el humo de otros fuegos.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto de nubes bajas que se arrastran por las cumbres.",
            "Pasan nubes grises que vienen del oeste y se van hacia el río.",
            "Está nublado, con una luz pareja que no deja sombras.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fina que gotea de todo lo que tiene borde.",
            "Cae una lluvia fría, y los caminos de la llanura se vuelven barro.",
            "Llueve a ratos, y entre un chubasco y otro el bosque echa vapor.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: el cielo se pone negro sobre los montes y el trueno rueda de un lado a otro de la llanura.",
            "Truena lejos, y luego cerca, y la lluvia cae de golpe, gruesa y caliente.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, una nieve menuda que lo cubre todo y apaga los ruidos.",
            "Ha nevado en la noche, y todo está blanco hasta donde se ve.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, una niebla blanca que llena los valles y deja las alturas como islas.",
            "La niebla tapa los robles de abajo, y el mundo se acaba a treinta pasos.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento fuerte que levanta los mantos y hace silbar la paja y las ramas.",
            "Hace viento, y los robles del bosque crujen como barcas viejas.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y los robles echan sus hojas nuevas, de un verde casi amarillo.",
            "En primavera los arroyos bajan llenos de los montes, y el ganado vuelve a los prados.",
            "Es el tiempo de sembrar, y en el que paren las yeguas.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el trigo de la llanura se pone amarillo.",
            "En verano las tormentas llegan por la tarde y se van antes de la noche.",
            "Es el tiempo de la siega y de los caminos secos, el tiempo en que se viaja.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y caen las bellotas de los robles: los cerdos andan debajo, comiéndoselas.",
            "En otoño la niebla se queda en los valles hasta tarde.",
            "Es el tiempo de arar y de sembrar el trigo del año que viene.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y las noches son largas: hay más noche que día.",
            "En invierno el ganado se queda en los corrales, y se come lo guardado.",
            "Es el tiempo del fuego en el centro de la casa y de las historias largas.",
        ),
    )

    // =====================================================================
    //  El trabajo del tramo (cada texto vale a cualquiera de sus horas)
    // =====================================================================

    private val TRABAJO = listOf(
        v("A esta hora duerme junto al fuego de la casa grande, envuelto en el manto, sobre una piel de oveja, con los demás de la casa alrededor. El fuego del centro chasquea de vez en cuando. Afuera, en el corral, una vaca se mueve; un perro le contesta a otro perro de la otra punta de la meseta.", hacer = Hacer.DORMIR),
        v("A esta hora duerme y repite versos sin querer, moviendo los labios. Su madre dice que habla dormido, y que de noche dice más versos que de día. Él no se acuerda nunca de nada. Se da la vuelta, se tapa la cabeza con el manto y sigue.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, como duermen los que han pasado el día a caballo. La casa cruje con el viento. El humo sale despacio por el techo de paja, y por el hueco se ve, si uno abre los ojos, una estrella o dos.", hacer = Hacer.DORMIR),

        v("A esta hora saca las vacas del corral y las lleva al abrevadero, entre la niebla, con una vara en la mano. Las vacas de la meseta son pequeñas, de cuernos cortos, y saben el camino mejor que él. Él las cuenta dos veces, por si acaso.", hacer = Hacer.CUIDAR),
        v("A esta hora les echa a los cerdos las bellotas del robledal que se guardaron en otoño. Los cerdos de la meseta son oscuros y flacos, de cerdas largas, y empujan la tabla del corral con el hocico. {nombre} les habla. Ellos no contestan.", hacer = Hacer.CUIDAR),
        v("A esta hora ayuda a los mozos de su padre a ordeñar las ovejas, en cuclillas, con el cubo de madera entre las rodillas. La leche sale tibia y huele a hierba. Luego sube los cubos a la casa, uno en cada mano, sin derramar.", hacer = Hacer.CUIDAR),

        v("A esta hora come sentado en el suelo de la casa grande, sobre una piel, como se come aquí: gachas de cebada con leche, un trozo de pan y queso. Su padre come callado. Su madre sirve a todos antes que a ella.", hacer = Hacer.COMER),
        v("A esta hora come carne de cerdo cocida en el caldero, que se saca con un gancho de hierro, y pan para mojar el caldo. Cada uno coge su trozo con las manos y lo corta con el cuchillo. La mejor pieza es para el más valiente, como es costumbre (Diodoro 5.28).", hacer = Hacer.COMER),
        v("A esta hora come de pie, junto a la puerta, un pedazo de pan con queso y unas manzanas pequeñas y ácidas del bosque, mirando el cielo para ver si llueve. Su madre le dice que se siente. Él dice que sí, y no se sienta.", hacer = Hacer.COMER),

        v("A esta hora repite versos en voz baja, uno detrás de otro, como se pasan las cuentas de un collar. Si se equivoca en uno, vuelve a empezar desde el principio, como le enseñaron. Lo que se aprende así no se olvida, dicen; o no se olvida mientras uno viva.", hacer = Hacer.REZAR),
        v("A esta hora dice todos los versos que sabe, de corrido, mirando un punto fijo, sin parar. César escribirá que hay quien pasa veinte años en la escuela de los druidas (6.14). {nombre} lleva menos, y le parece que lleva toda la vida.", hacer = Hacer.REZAR),
        v("A esta hora piensa en los versos como se piensa en un camino: este recodo, esta piedra, este roble partido. Así se los enseñaron a guardar. Si se olvida uno, no hay dónde mirarlo: lo que no se escribe, se recuerda, o se pierde.", hacer = Hacer.REZAR),
        v("A esta hora está en el robledal, sentado en la hojarasca con los otros aprendices, delante del maestro. El maestro dice un verso; ellos lo repiten. Lo dice otra vez; lo repiten otra vez. Así toda la mañana, hasta que el verso es de ellos.", hacer = Hacer.REZAR, hasta = "deja_escuela"),
        v("A esta hora el maestro lo hace decir solo, delante de todos, los versos de ayer. {nombre} se pone de pie y los dice con los ojos cerrados. Korisios, a su lado, mueve los labios con él, por si le hace falta. Casi nunca le hace falta.", hacer = Hacer.REZAR, hasta = "deja_escuela"),
        v("A esta hora, cuando antes bajaba a la escuela, repite los versos solo, en el corral, con la espalda contra la pared tibia del establo. Los dice enteros, cada día, para no perder ninguno. Desde arriba se ve el humo del robledal.", hacer = Hacer.REZAR, desde = "deja_escuela", hasta = "vuelve_escuela"),
        v("A esta hora vuelve a estar en el robledal, con el maestro y con aprendices nuevos, más jóvenes que él, que lo miran como a un viejo porque estuvo en la guerra. Repite con ellos versos que ya sabía. Le parece que los oye por primera vez.", hacer = Hacer.REZAR, desde = "vuelve_escuela"),

        v("A esta hora se entrena con los guerreros de su padre en el prado de detrás de la muralla: la lanza, el escudo largo de madera, la espada de hierro. Los golpes se dan de verdad, con la madera. Él sale siempre con un moretón nuevo.", hacer = Hacer.TRABAJO),
        v("A esta hora cepilla y cura los caballos de su padre, pequeños y fuertes, de crin corta, que conocen su voz. Les mira los cascos uno por uno, les quita las piedras, les unta las mataduras. Un caballero sin caballo, dice su padre, es un hombre a pie.", hacer = Hacer.TRABAJO),
        v("A esta hora monta con los jinetes de la casa por los prados de la meseta: galopar, parar en seco, volver, tirar la lanza sin bajarse. Los guerreros gritan para asustar a los caballos, y los caballos aprenden a no asustarse. Él también aprende.", hacer = Hacer.TRABAJO),
        v("A esta hora Esunertos lo hace correr con el escudo en alto hasta que no puede más, y luego le dice que otra vez. Esunertos es de la gente de su padre y lleva un torques de hierro, no de oro. Dice que el oro no para los golpes.", hacer = Hacer.TRABAJO, hasta = "alesia"),
        v("A esta hora monta su potro en el prado de detrás de la muralla, para que aprenda lo que sabía la yegua. El potro no sabe nada y lo quiere saber todo a la vez. {nombre} le habla bajo, con el brazo de la herida pegado al cuerpo.", hacer = Hacer.TRABAJO, desde = "potro"),

        v("A esta hora lleva un mensaje de su padre a otro oppidum de los arvernos, a caballo, por los caminos de tierra entre los bosques. El mensaje no va escrito: lo lleva en la cabeza, palabra por palabra, como un verso, y lo dirá igual a quien tenga que oírlo.", hacer = Hacer.CAMINO),
        v("A esta hora vuelve de llevar un recado al otro lado del río, con el caballo cansado y el manto lleno de polvo. Por el camino se cruza con pastores, con un carro de bueyes y con un mercader que pregunta lo que pasa en Gergovia. Él contesta poco.", hacer = Hacer.CAMINO),
        v("A esta hora sube por el camino de la puerta, de vuelta de un mensaje, y desde abajo mira la muralla de la meseta, con sus troncos cruzados y sus piedras. Los de la puerta lo conocen y lo dejan pasar sin preguntar. Él les dice lo que hay de nuevo por ahí.", hacer = Hacer.CAMINO),

        v("A esta hora está en el banquete de la casa grande, sentado en el suelo sobre pieles, alrededor del fuego y del caldero. Un bardo canta, con un instrumento parecido a una lira, las alabanzas de unos y las burlas de otros (Diodoro 5.31). Los alabados beben; los burlados también.", hacer = Hacer.GENTE),
        v("A esta hora escucha a los guerreros de su padre contar sus hazañas, cada uno la suya, y cada vez más grande. Se sientan en pieles de lobo o de perro, como dice Diodoro (5.28), y se pasan el cuerno de beber de mano en mano. {nombre} bebe poco y escucha mucho.", hacer = Hacer.GENTE),
        v("A esta hora escucha al bardo de la casa, que canta los hechos de los abuelos para que no se pierdan: los bardos, dice Lucano, mandan a los tiempos lejanos la fama de los valientes (Farsalia 1). {nombre} se sabe los cantos casi enteros. Los versos de la escuela son otra cosa.", hacer = Hacer.GENTE),
    )

    private val CAMPANA_TRABAJO = listOf(
        ca("A esta hora duerme en el suelo del campamento, con la cabeza sobre la silla de la yegua y la lanza al lado. Duerme a ratos. Lo despiertan los relinchos, los pasos de la guardia, la tos de alguien. Cuando abre los ojos ve las brasas y, encima, las estrellas.", Hacer.DORMIR),
        ca("A esta hora duerme bajo un carro, con otros tres, para que no le caiga el rocío. Sueña con la casa grande y con el olor de la olla. Al despertar, por un momento, cree que está en Gergovia.", Hacer.DORMIR),
        ca("A esta hora lleva a beber a los caballos de la gente de su padre a un arroyo, entre la niebla, con otros mozos. Los caballos del ejército son miles, y el arroyo se pone turbio en un momento. Hay que llegar antes que los demás.", Hacer.CUIDAR),
        ca("A esta hora le revisa a su yegua las patas y los cascos, uno por uno, porque un caballo cojo en campaña es un caballo perdido. Le da un puñado de grano de su ración. Ella se lo agradece con un empujón de la cabeza.", Hacer.CUIDAR),
        ca("A esta hora come lo que hay en el campamento: pan duro, queso, un poco de carne asada en la lanza. El grano escasea, porque se quema todo lo que pudieran comer los romanos. {nombre} come despacio, para que dure.", Hacer.COMER),
        ca("A esta hora come sentado en el suelo junto a un fuego, con los jinetes de su pueblo, de un caldero que va de mano en mano. Hablan de los romanos y de los caballos. De los caballos, más.", Hacer.COMER),
        ca("A esta hora, cuando antes estaba en la escuela, repite los versos a caballo, al paso, en voz tan baja que solo los oye la yegua. Es lo único de su vida de antes que trajo entero. Los versos no pesan.", Hacer.REZAR),
        ca("A esta hora se aparta un momento del campamento, entre los árboles, y dice los versos de la mañana como si estuviera en el robledal. Le parece oír al maestro corrigiéndolo. No está.", Hacer.REZAR),
        ca("A esta hora sale con los jinetes a buscar a los que forrajean para los romanos, por los caminos y por los bosques, como manda Vercingétorix: no presentar batalla, pero no dejarles comer. Es un trabajo de esperar mucho y de correr poco.", Hacer.TRABAJO),
        ca("A esta hora vigila desde una loma el humo de las granjas que se queman, las de los suyos y las de otros, para que César no encuentre trigo. Lo queman ellos mismos. {nombre} nunca pensó que vería a nadie quemar su propio trigo.", Hacer.TRABAJO),
        ca("A esta hora lleva un mensaje de un campamento a otro, al galope, por caminos que no conoce, con la palabra de un jefe en la cabeza. Dice el mensaje entero, sin cambiar nada, y vuelve. Para eso le sirve, por fin, la memoria.", Hacer.CAMINO),
        ca("A esta hora va con otros jinetes a ver por dónde marcha César, de lejos, desde los bosques. Se ve el polvo de las legiones, largo como un río. Cuentan lo que ven y vuelven a contarlo. Nadie tiene que escribir nada.", Hacer.CAMINO),
        ca("A esta hora se sienta al fuego con los jinetes de la gente de su padre, y alguien canta un canto de guerra. No hay bardo; lo canta un guerrero que se equivoca en la letra. Todos lo siguen igual. {nombre} lo sigue también.", Hacer.GENTE),
        ca("A esta hora escucha en el fuego lo que se cuenta de Vercingétorix: que pide rehenes y soldados a todos los pueblos, y que castiga con dureza a los que dudan, como contará César (7.4). Se cuenta con orgullo. También se cuenta con miedo.", Hacer.GENTE),
    )

    private val ALESIA_TRABAJO = listOf(
        al("A esta hora duerme en el suelo de la casa prestada, con el estómago vacío y la espada al lado. Duerme poco. Abajo, en la llanura, las hogueras de los romanos dan la vuelta entera al monte, y su luz se ve hasta con los ojos cerrados.", Hacer.DORMIR),
        al("A esta hora duerme y sueña con comida: con el caldero de la casa grande, con el pan de su madre. Se despierta con la boca llena de saliva y se vuelve a dormir enseguida, para no pensar.", Hacer.DORMIR),
        al("A esta hora ayuda a cuidar el ganado que trajeron los mandubios y que Vercingétorix manda repartir por cabeza. Cada día hay menos. Las vacas que quedan se arriman a la muralla, flacas, como si supieran.", Hacer.CUIDAR),
        al("A esta hora se cura la herida del brazo con agua limpia y un trapo, como le enseñaron en la escuela, que también enseña de plantas. La herida se cierra bien. {nombre} le da gracias a quien sea por eso.", Hacer.CUIDAR, hasta = "socorro"),
        al("A esta hora come su ración, la que Vercingétorix manda repartir poco a poco y por tasa (César 7.71): un puñado de grano cocido y nada más. La come despacio, grano por grano, para que parezca más.", Hacer.COMER),
        al("A esta hora no come, porque ya comió lo de hoy. Se sienta en la muralla al sol, para no gastar fuerzas, y mira cómo comen los romanos allá abajo, delante de sus tiendas. El humo de sus calderos sube derecho.", Hacer.COMER),
        al("A esta hora dice los versos sentado en la muralla, mirando los fosos romanos. Si va a morir aquí, piensa, los versos se van con él a donde vaya el alma. Por eso los dice enteros, cada día, sin saltarse ninguno.", Hacer.REZAR),
        al("A esta hora dice los versos con otro aprendiz que encontró en el cerco, un muchacho de los bitúriges que también dejó su escuela. Se corrigen el uno al otro. Sus versos no son iguales; se parecen.", Hacer.REZAR),
        al("A esta hora hace su guardia en la muralla, de madera y piedra, mirando la llanura entre los dos ríos donde los romanos cavan. Cuando se acercan demasiado, les tiran piedras y dardos. Ellos siguen cavando.", Hacer.TRABAJO),
        al("A esta hora ayuda a levantar el muro de piedras secas que los galos hicieron delante de la muralla, al pie del monte. Se cargan piedras, se cargan troncos, se cava. Los dos bandos cavan, cada uno lo suyo.", Hacer.TRABAJO),
        al("A esta hora lleva recados de un jefe a otro por dentro de la muralla, de una puerta a la otra, entre la gente. Alesia es pequeña para tantos. Por todas partes hay hombres sentados, esperando, mirando las colinas.", Hacer.CAMINO),
        al("A esta hora baja con otros hasta el pie del monte a ver de cerca las obras de los romanos, de noche, sin hacer ruido: el foso, las estacas escondidas, la empalizada con sus torres. Vuelve y lo cuenta. Nadie dice nada.", Hacer.CAMINO),
        al("A esta hora se sienta al fuego, que es pequeño porque hay poca leña, con los arvernos del cerco. Nadie canta. Alguien cuenta una historia de cuando era niño, y todos la escuchan como si fuera importante.", Hacer.GENTE),
        al("A esta hora se junta con los demás alrededor de los jefes, que hablan de cuánto falta para que llegue la ayuda. Los días se cuentan en voz alta. Cada noche son menos los que faltan, y cada noche cuesta más creerlo.", Hacer.GENTE),
    )

    private val CAUTIVO_TRABAJO = listOf(
        cu("A esta hora duerme en el suelo del cercado, contra la espalda de otro para no tener frío, con el manto mojado de rocío. Alrededor, miles de hombres duermen igual. Los guardias romanos pasean con sus antorchas por fuera.", Hacer.DORMIR),
        cu("A esta hora espera en una fila a que los romanos repartan el agua, un cubo para cada veinte. Antes de beber, deja beber a un viejo de su pueblo que tiembla. El viejo le dice el nombre de su casa, por si acaso.", Hacer.CUIDAR),
        cu("A esta hora come lo que tiran por encima de la empalizada: pan de soldado, duro y gris. Lo parten en pedazos iguales, como hermanos. Es más de lo que había en la ciudad los últimos días.", Hacer.COMER),
        cu("A esta hora dice los versos sentado en el suelo del cercado, con los ojos cerrados. Lo que le han quitado son las armas. Los versos no se los pueden quitar, porque no están escritos en ninguna parte más que en él.", Hacer.REZAR),
        cu("A esta hora, cuando antes montaba con los guerreros, está sentado en el suelo del cercado, sin armas y sin caballo, con las manos vacías sobre las rodillas. No sabe qué hacer con ellas. Nunca había tenido las manos vacías tanto tiempo.", Hacer.TRABAJO),
        cu("A esta hora lo llevan con otros arvernos de un cercado a otro, en fila, entre soldados. Pasan delante del campamento de César. Él no mira las tiendas. Mira el suelo, donde todavía hay armas tiradas.", Hacer.CAMINO),
        cu("A esta hora se sienta con los de su pueblo y se cuentan quién está y quién no: de esta casa, de aquella. Se dicen nombres en voz baja. Cada nombre que alguien ha visto vivo es una fiesta pequeña.", Hacer.GENTE),
    )

    // =====================================================================
    //  El pueblo y el entorno
    // =====================================================================

    private val PUEBLO = listOf(
        v("{entorno} Las casas de Gergovia son grandes, de postes de madera, paredes de zarzo con barro y techos de paja que bajan casi hasta el suelo, como las describe Estrabón (4.4.3). La de su padre es de las mayores: dentro caben la familia, los guerreros y los perros."),
        v("En el lado del sol de la meseta están los talleres: el del herrero, que golpea todo el día; el del bronce; el que acuña las monedas de oro con el caballo. {entorno} Los arvernos tienen oro, y se nota en los cuellos de los nobles."),
        v("{entorno} Al pie de la meseta está el robledal, y en medio un manantial donde la gente deja cosas para los dioses. En los lugares sagrados el oro se deja en el suelo, y nadie lo toca (Diodoro 5.27). {nombre} lo ha visto: monedas, anillos, un torques."),
        v("Desde la muralla se ve el río al este, el que los romanos llaman Elaver, ancho y lento, que casi nunca se vadea antes del otoño (César 7.35). {entorno} Por la orilla suben y bajan los carros hacia el norte y hacia el sur."),
        v("Por la puerta de la meseta entran y salen todo el día carros de bueyes, pastores con sus ovejas, mujeres con cántaros y niños con perros. {entorno} En la puerta siempre hay alguien que pregunta qué hay de nuevo."),
        v("{entorno} Al oeste, al otro lado de la llanura, se levanta la fila de los montes viejos, de cumbres redondas y peladas, como calderos puestos boca abajo. Los pastores suben allí en verano. En invierno se ponen blancos."),
        v("El mercado de Gergovia está junto a la puerta: sal, pieles, cerámica, hierro en barras, y mercaderes que hablan griego y llevan sus cuentas con letras griegas. {entorno} Algunos vienen de Italia, con ánforas de vino atadas con paja.", hasta = "grito"),
        v("El mercado de Gergovia está otra vez lleno: sal, pieles, cerámica y mercaderes de Italia con sus ánforas de vino, que hablan bajo y pagan bien. {entorno} Desde Alesia todo el mundo vende a los romanos.", desde = "mercaderes"),
    )

    private val CAMPANA_PUEBLO = listOf(
        ca("El campamento de Vercingétorix está en un bosque, cerca de un río, con los fuegos de cada pueblo apartados de los de los demás: los arvernos con los arvernos, los bitúriges con los bitúriges. Por todas partes hay caballos."),
        ca("Desde el campamento se ve, lejos, el humo de lo que se quema: granjas, aldeas, almiares. Es la orden de Vercingétorix, para que los romanos no encuentren qué comer. El humo se queda en el horizonte todo el día."),
        ca("El campamento cambia de sitio cada pocos días, siguiendo a César de lejos. Se arma y se desarma deprisa: los fuegos, los carros, los caballos atados a los árboles. Al irse, solo queda la ceniza."),
    )

    private val ALESIA_PUEBLO = listOf(
        al("Alesia, el oppidum de los mandubios, está en lo alto de una colina entre dos ríos, rodeada de otras colinas, como dice Estrabón (4.2.3). Desde la muralla se ven los dos ríos y, entre ellos, la llanura llena de romanos."),
        al("Alrededor de Alesia los romanos han cavado una línea de fosos, empalizadas y torres que da la vuelta entera al monte, de once millas, dice César (7.69). Más afuera, otra más larga, mirando hacia afuera. En medio están ellos, los romanos."),
        al("Dentro de Alesia no cabe nadie más: los mandubios, ochenta mil combatientes de toda la Galia, dice César (7.71), caballos, ganado. Las calles están llenas de gente sentada que no tiene nada que hacer más que mirar y esperar."),
        al("Las colinas de fuera, que antes estaban vacías, están llenas de los fuegos del socorro, de todos los pueblos de la Galia. Desde la muralla se ven, de noche, como un cielo caído.", desde = "socorro"),
    )

    private val CAUTIVO_PUEBLO = listOf(
        cu("Los cautivos están en cercados de estacas al pie de Alesia, entre las líneas romanas, que ya nadie defiende ni ataca. Arriba, la ciudad está callada. En la llanura los romanos recogen las armas en montones."),
        cu("Desde el cercado se ven los campamentos de César, sus tiendas en filas, sus caballos, sus calderos humeando. Todo está en orden. Los romanos ya hablan de irse a sus cuarteles de invierno."),
    )

    /** Manda la primera regla que se cumple: los romanos al pie de la meseta, las granjas quemadas y, si no, el trigo. */
    private val ENTORNOS = listOf(
        Entorno("romanos", 0.5, 1.0, frases = listOf(
            "Abajo, en la llanura, están los campamentos de César: tiendas en filas dentro de empalizadas, y una zanja que los une.",
            "Al pie de la meseta, donde antes estaban los campos, ahora hay campamentos romanos, y su humo sube todo el día.",
        )),
        Entorno("danos", 0.25, 1.0, frases = listOf(
            "En la llanura todavía se ven, negras, las granjas que quemaron los romanos.",
            "Abajo, entre los campos, hay casas sin techo y corrales vacíos desde que pasaron los romanos.",
        )),
        Entorno("trigo", 0.0, 0.05, frases = listOf(
            "Los campos de la llanura están en rastrojo o recién arados.",
            "Abajo, la llanura es de tierra parda, con los surcos recién abiertos.",
        )),
        Entorno("trigo", 0.05, 0.5, frases = listOf(
            "En la llanura el trigo apenas asoma, verde y ralo.",
            "Abajo, los campos tienen un verde tierno que el frío no acaba de dejar crecer.",
        )),
        Entorno("trigo", 0.5, 0.95, frases = listOf(
            "En la llanura el trigo está alto y verde, y el viento lo peina.",
            "Abajo, los campos de trigo ondean como el pelo de un caballo.",
        )),
        Entorno("trigo", 0.95, 1.0, frases = listOf(
            "El trigo de la llanura está amarillo y pesado: falta poco para la siega.",
        )),
    )

    // =====================================================================
    //  La gente cercana
    // =====================================================================

    private val GENTE = listOf(
        v("Su padre es un caballero arverno, con sus guerreros, sus clientes y sus caballos. Habla poco y manda mucho. Fue él quien lo mandó a la escuela, porque un druida en la familia es una honra, y él quien le enseñó a montar, porque un caballero es otra."),
        v("Su madre manda en la casa grande: en la olla, en el telar, en los mozos y en su padre, aunque su padre no lo sepa. Teje mantos de lana a cuadros, rojos, verdes y amarillos, y sabe de memoria de quién es hijo cada uno en la meseta."),
        v("El maestro, Vebrumaros, es un druida viejo, de barba blanca y manos finas, que habla despacio y no repite nunca una cosa dos veces. Dicen que de joven estudió en Britania, adonde van los que quieren saber a fondo lo de los druidas (César 6.13)."),
        v("Korisios, el otro aprendiz, es hijo de un herrero y tiene una memoria de piedra: lo que oye una vez, ya no se le va. Es callado, flaco y serio, y se ríe pocas veces, pero cuando se ríe se ríe tanto que el maestro lo manda callar."),
        v("El herrero de la meseta, el padre de Korisios, trabaja junto a la puerta, con los brazos negros hasta los codos. Le hace a su padre las puntas de lanza y a {nombre} le arregla la fíbula cuando se le rompe, sin cobrarle, por la amistad de los hijos."),
        v("Su hermana es más chica que él, morena, de lengua rápida, y se burla de sus versos imitando la voz del maestro. Les peina las crines a los caballos cuando él no mira. Su madre dice que es la que más se parece a su padre.", hasta = "promesa"),
        v("A su hermana la han prometido con un joven arverno de la montaña, de buena casa. Ella dice que le da lo mismo. Luego pregunta, como quien no quiere, cómo es la montaña en invierno, y si allá arriba se oye el cuerno de Gergovia.", desde = "promesa", hasta = "boda"),
        v("Su hermana vive en la montaña desde la boda, con la familia de su marido, a dos días a caballo. Cuando {nombre} lleva un mensaje por allá, se desvía para verla. Siempre le tiene guardado un queso.", desde = "boda", hasta = "sobrino"),
        v("Su hermana tiene un hijo, en la montaña, y ha mandado decir que tiene la cara de su padre. Su madre ha empezado a tejerle un manto pequeño, a cuadros, con los colores de la casa.", desde = "sobrino"),
        v("Esunertos es uno de los guerreros de su padre: tiene diez años más que él, la nariz rota dos veces y una risa que se oye desde la puerta. Le enseña armas y caballos sin piedad, y lo defiende de cualquiera que se burle de un aprendiz de druida.", hasta = "alesia"),
        v("De Esunertos no se sabe nada desde que salió de Alesia con la caballería, con la yegua torda. Nadie lo vio en el socorro, o nadie se acuerda. {nombre} pregunta a todos los que vuelven. Nadie sabe.", desde = "liberados", hasta = "esunertos"),
        v("Esunertos cayó en la última batalla de Alesia, fuera de las líneas, con el socorro. Su lanza la trajo un guerrero de la casa y está colgada en la pared de la casa grande. {nombre} cree que su alma ya está en otro cuerpo, en alguna parte, y lo dice en voz alta.", desde = "esunertos"),
    )

    private val CAMPANA_GENTE = listOf(
        ca("Con {nombre} van los jinetes de la gente de su padre, una veintena, con Esunertos delante. Su padre va con los nobles, cerca de Vercingétorix, y {nombre} lo ve poco. Cuando se ven, su padre le mira la yegua, no la cara."),
        ca("En el campamento hay jinetes de todos los pueblos de la Galia que se han sumado: bitúriges, cadurcos, senones, carnutos. Hablan como ellos, con otra música. Al principio se miran de lado. Luego, se pasan el cuerno."),
    )

    private val ALESIA_GENTE = listOf(
        al("Esunertos está en Alesia con él, y le dice que el cerco no va a durar, que los romanos no pueden cavar tanto. Los romanos siguen cavando. Esunertos sigue diciéndolo, cada vez más bajo.", hasta = "sale_caballeria"),
        al("Esunertos se fue de Alesia con la caballería, de noche, montando la yegua torda, porque el caballo suyo se lo mataron en la batalla. Iba a llamar a los arvernos a la guerra. {nombre} piensa en él cada vez que mira las colinas.", desde = "sale_caballeria"),
        al("En Alesia están los arvernos con su rey, y entre ellos los jefes que {nombre} ha visto toda la vida en Gergovia, ahora flacos y sin oro en el cuello. Su padre salió con la caballería. Nadie sabe dónde está.", desde = "sale_caballeria"),
    )

    private val CAUTIVO_GENTE = listOf(
        cu("En el cercado de los arvernos hay gente de Gergovia: el herrero del bronce, dos primos de su madre, un guerrero de su padre que no habla desde la entrega. De su padre no sabe nada: salió con la caballería y no volvió a Alesia."),
    )

    // =====================================================================
    //  El objeto del día
    // =====================================================================

    private val OBJETO = listOf(
        v("Todo lo que tiene {nombre} lo hizo alguien de la meseta, con sus manos, y casi todo dura más que una vida. {dato_objeto}"),
        v("{dato_objeto} Así son las cosas de su gente: hechas para durar y para que se vea de quién son."),
        v("Hoy {nombre} se fija en algo que tiene delante todos los días. {dato_objeto}"),
        v("{dato_objeto} Su padre dice que una cosa bien hecha es como un nombre: dice quién eres antes de que hables."),
        v("{dato_objeto} Nadie lo piensa, pero sin eso no habría Gergovia."),
        v("Lo de cada día también tiene su historia, aunque nadie la escriba. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("torques", "el torques", "un torques", "El torques",
            "El torques es un collar rígido de metal, abierto por delante, con dos remates gruesos que se miran; los nobles lo llevan de oro, y los griegos y los romanos se admiran de cuánto oro llevan los galos al cuello y en los brazos (Diodoro 5.27)."),
        ObjetoCuento("moneda", "la moneda de oro", "una moneda de oro", "La moneda de oro",
            "Las monedas de oro de los arvernos copian una moneda griega muy vieja: de un lado una cabeza; del otro, un carro con dos caballos que, de tanto copiarse, se ha vuelto un solo caballo; los arvernos fueron de los primeros en acuñarlas."),
        ObjetoCuento("hoz", "la hoz del muérdago", "una hoz pequeña", "La hoz del muérdago",
            "La hoz con que se corta el muérdago es pequeña y curva y brilla como el oro (Plinio 16.249–251); hay quien piensa que era de bronce dorado y que lo que importaba era no tocar el muérdago con hierro; el maestro la guarda envuelta en un paño blanco."),
        ObjetoCuento("tablilla", "la tablilla de cuentas", "una tablilla de cuentas", "La tablilla de cuentas",
            "Las tablillas de cuentas de los mercaderes y de los nobles se escriben con letras griegas: nombres, cantidades, deudas, cabezas de ganado; los galos usan esas letras para casi todo, menos para lo que enseñan los druidas (César 6.14)."),
        ObjetoCuento("espada", "la espada de hierro", "una espada de hierro", "La espada de hierro",
            "La espada gala es de hierro, larga y recta, hecha para cortar más que para pinchar; va en una vaina de hierro colgada de una cadena a la cintura, y la de un buen herrero tiene grabados animales que se enroscan."),
        ObjetoCuento("lanza", "la lanza", "una lanza", "La lanza",
            "La lanza tiene una hoja de hierro ancha y larga, y un asta de madera de fresno del alto de un hombre y medio; con ella se pelea a pie y a caballo, y es lo primero que un muchacho aprende a manejar."),
        ObjetoCuento("escudo", "el escudo", "un escudo", "El escudo",
            "El escudo galo es largo, casi del alto de un hombre, de tablas de madera forradas de cuero, con una pieza de hierro en el centro que cubre la mano; se pinta con los colores de la casa, y pesa más de lo que parece."),
        ObjetoCuento("manto", "el manto de lana", "un manto de lana", "El manto de lana",
            "El manto es de lana gruesa, tejida a cuadros de colores, rojo, verde y amarillo, y se sujeta al hombro con una fíbula; de noche es manta, con lluvia es techo, y en un banquete dice de qué casa es cada uno."),
        ObjetoCuento("fibula", "la fíbula", "una fíbula", "La fíbula",
            "La fíbula es un broche de bronce o de hierro, con un muelle y una aguja, como un imperdible; sujeta el manto al hombro, y las hay sencillas, de herrero, y las hay con un remate de coral rojo que vino del mar del sur."),
        ObjetoCuento("caldero", "el caldero de bronce", "un caldero de bronce", "El caldero de bronce",
            "Los calderos de las casas grandes son de bronce, grandes como para medio cerdo, y cuelgan de una cadena de hierro sobre el fuego del centro; en ellos se cuece la carne del banquete, y un caldero así pasa de padres a hijos."),
        ObjetoCuento("cuerno", "el cuerno de beber", "un cuerno de beber", "El cuerno de beber",
            "El cuerno de beber es de buey, con la boca rematada de bronce; se llena de cerveza o, en los días grandes, de vino de Italia, y en el banquete va de mano en mano, y el que lo deja caer paga una ronda de burlas."),
        ObjetoCuento("carnyx", "el carnyx", "un carnyx", "El carnyx",
            "El carnyx es la trompeta de guerra: un tubo de bronce más alto que un hombre, que se toca hacia arriba, con la boca en forma de cabeza de jabalí; Diodoro dice que las trompetas de los galos hacen un ruido espantoso (5.30), y es verdad."),
    )

    // =====================================================================
    //  Lo que piensa (vale en Gergovia, en la campaña, en Alesia y cautivo)
    // =====================================================================

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el alma, que según el maestro no muere y, pasado un tiempo, vuelve a vivir en otro cuerpo (César 6.14; Diodoro 5.28). Piensa en quién habrá sido la suya antes. No sabe. El maestro dice que no hace falta saberlo."),
        v("{nombre} piensa que, si un día se olvidaran todos los versos, no habría dónde encontrarlos: ni en una tablilla, ni en un muro, ni en ninguna parte. Están en la cabeza de unos pocos. A sus {edad} años, eso le da miedo y le da orgullo a la vez."),
        v("{mundo} {nombre} piensa en los dioses de los que hablan los viejos: Teutates, Esus, Taranis, los que Lucano nombrará (Farsalia 1). Los romanos les dan a los dioses de la Galia sus propios nombres, y dicen que el que más se honra aquí es su Mercurio (César 6.17)."),
        v("{nombre} piensa en la noche. Los suyos dicen que descienden todos de un mismo padre, el dios del mundo de abajo, al que los romanos llaman Dis Pater, y por eso cuentan el tiempo por noches: en los cumpleaños y al empezar los meses y los años, la noche va delante del día (César 6.18)."),
        v("{mundo} {nombre} piensa en lo que el maestro enseña de los astros, de su movimiento y de lo grande que es la tierra (César 6.14). Piensa que el mundo es más grande que la Galia, y que la Galia ya es muy grande. Le cuesta pensarlo entero."),
        v("{nombre} piensa en su padre, que quería un guerrero, y en el maestro, que quiere un druida. Los druidas no van a la guerra ni pagan tributos (César 6.14). Los caballeros salen a campaña cada vez que hace falta (6.15). Él sabe montar y sabe versos, y no sabe todavía qué es."),
        v("{mundo} {nombre} piensa que el alma no muere, y que por eso, dicen, los galos no le tienen miedo a la muerte: César lo llama «grandísimo incentivo para el valor, poniendo aparte el temor de la muerte» (César 6.14). Él lo cree. Lo que no sabe es si basta con creerlo."),
        v("{nombre} piensa en lo que hizo cuando dejó la escuela. Los druidas no van a la guerra, y él se fue. El maestro no le dijo nada, y eso fue peor que si le hubiera dicho algo. Piensa que los versos siguen en él, aunque él ya no esté donde se aprenden.", desde = "deja_escuela", hasta = "vuelve_escuela"),
        v("{nombre} piensa en Vercingétorix, el rey, al que ha visto de lejos, a caballo, entre sus jefes. Dicen que quiere juntar a toda la Galia en una sola voluntad. Nadie lo ha conseguido nunca. Él cree que este sí.", desde = "rey", hasta = "entrega"),
        v("{nombre} piensa en el día de Alesia, en el rey que salió con sus mejores armas, como contará Plutarco (César 27), y se entregó para salvar a los suyos. César cuenta que dijo no haber hecho la guerra por él, «sino por la defensa de la común libertad» (César 7.89). Para {nombre} aquello fue una victoria de honra. Lo cree así.", desde = "entrega"),
    )

    /** El estado del mundo de cada temporada (una frase para `{mundo}`). Están las cinco, aunque solo la 1 tenga sus especiales. */
    private val MUNDOS = mapOf(
        "escuela" to listOf(
            "Este año César anda lejos, en el norte y más allá del mar, y en la meseta se habla de él como de una tormenta que pasa por otro valle.",
            "Los mercaderes cuentan que en el norte los pueblos se cansan de los romanos, pero aquí, entre los arvernos, todo sigue como siempre.",
            "Dicen que los romanos tienen cuarteles de invierno en tierra de los belgas, y que los belgas lo llevan mal.",
        ),
        "inquietud" to listOf(
            "Este año en toda la Galia se habla en voz baja, y los nobles se miran unos a otros antes de decir nada.",
            "Dicen que César castiga a los pueblos que se le levantan, y que a los que no se levantan los vigila.",
            "En la meseta hay quien dice que hay que esperar y quien dice que ya se ha esperado bastante.",
        ),
        "levantamiento" to listOf(
            "Este año los mensajeros no paran de ir y venir entre los pueblos de la Galia, y nadie en la meseta duerme tranquilo.",
            "Este año se juran en la Galia cosas que no se habían jurado nunca, en los bosques y en las asambleas.",
            "Este año en todas las casas se cuentan las lanzas, los caballos y el grano, por si acaso, y luego se vuelven a contar.",
        ),
        "resistencia" to listOf(
            "Este año la guerra se apaga en unos sitios y se enciende en otros, como un fuego mal cubierto.",
            "Los arvernos han dado rehenes a César, y los nobles de la meseta vuelven a hablar con los romanos.",
            "Dicen que en el sur y en el norte todavía hay quien pelea, pero cada vez son menos.",
        ),
        "conquista" to listOf(
            "Este año ya no hay guerra en la Galia, y en el mercado se oye hablar latín.",
            "Los mercaderes de Italia van y vienen sin miedo por los caminos de la Galia, con sus carros y sus ánforas.",
            "Dicen que César tiene otros pleitos en Roma, y que la Galia ya no le preocupa.",
        ),
    )

    // =====================================================================
    //  El anochecer
    // =====================================================================

    private val ANOCHECER = listOf(
        v("Al caer la tarde el sol se esconde detrás de los montes del oeste, y la llanura se llena de sombra antes que la meseta. Desde el borde, {nombre} ve encenderse los fuegos de las granjas de abajo, uno a uno. Luego entra, porque empieza la noche, y con ella el día de mañana."),
        v("Anochece en Gergovia con el humo de todas las casas, el mugido del ganado que vuelve y los perros que no quieren callarse. {nombre} cierra el corral, le da un último puñado de grano a su caballo y sube a la casa grande, de donde sale olor a carne."),
        v("Al caer la tarde {nombre} se sienta en el borde de la muralla, con los pies colgando sobre la cuesta, y dice para sí los versos del día, uno detrás de otro, mientras se apaga la luz. Abajo, el robledal se vuelve negro antes que el cielo."),
        v("De noche la meseta es una isla de fuegos sobre la llanura oscura. Arriba, el cielo está lleno de estrellas, y {nombre} busca las que le enseñó el maestro. Hace frío. Dentro de la casa grande, alguien empieza a cantar."),
        v("Anochece, y en la casa grande se echa leña al fuego del centro hasta que las llamas llegan a las vigas. Las sombras de los guerreros se hacen enormes en las paredes de barro. {nombre} se sienta en su sitio, sobre su piel, y espera el banquete."),
        v("Cuando se pone el sol, en la meseta se cierran las puertas de la muralla, y los de la guardia encienden su hoguera junto a la entrada. {nombre} los saluda al pasar. Esta noche no le toca a él. Algún día le tocará."),
        v("Al caer la tarde {nombre} sube con su madre a echar el último vistazo al ganado. Desde la puerta del corral se ve la llanura, y al fondo, lejos, la línea oscura del bosque. Su madre dice que va a helar. Siempre acierta.", meses = INVIERNO),
    )

    private val CAMPANA_ANOCHECER = listOf(
        ca("Al caer la tarde el campamento se llena de fuegos, cientos, entre los árboles. Huele a carne asada y a leña verde. {nombre} desensilla la yegua, la seca con un puñado de hierba y se sienta junto al fuego de los suyos, con la lanza al alcance de la mano."),
        ca("Anochece, y en el horizonte se ven las hogueras del campamento romano, en filas rectas, como las de una ciudad. Las de los galos están como caen. {nombre} las mira un rato. Luego se vuelve de espaldas y se envuelve en el manto."),
        ca("De noche, en el campamento, se cuentan las noticias del día: qué pueblo se ha sumado, qué ciudad ha ardido, por dónde va César. Las noticias llegan a gritos, de fuego en fuego. {nombre} las repite al fuego de al lado."),
    )

    private val ALESIA_ANOCHECER = listOf(
        al("Al caer la tarde las hogueras de los romanos se encienden alrededor del monte, en las dos líneas, una dentro de la otra, hasta cerrar el círculo. Alesia está en medio, a oscuras, porque no hay leña que gastar. {nombre} las cuenta hasta que se cansa."),
        al("Anochece sobre Alesia, y desde las torres romanas llegan voces y el ruido de las palas, que no paran de noche. {nombre} se sienta en la muralla, con el manto sobre los hombros, y mira las colinas de fuera, oscuras todavía.", hasta = "socorro"),
        al("De noche, en Alesia, los hombres hablan poco y duermen mal. Alguien reza en voz alta a Teutates; alguien le contesta que se calle. {nombre} dice sus versos sin voz, para él, y se queda mirando las estrellas por encima del cerco."),
        al("Anochece, y en las colinas de fuera arden los fuegos del socorro, que llegan de toda la Galia. Desde la muralla los de dentro les gritan, y el viento les trae, a veces, un grito de vuelta.", desde = "socorro"),
    )

    private val CAUTIVO_ANOCHECER = listOf(
        cu("Al caer la tarde los guardias encienden hogueras alrededor del cercado, y los cautivos se juntan en el centro, lejos de la luz. {nombre} mira hacia el sur, donde está Gergovia, a muchos días de camino. No ve nada. Se queda mirando igual."),
        cu("Anochece en la llanura de Alesia, y desde las tiendas romanas llegan risas, canciones en su lengua, olor a vino. En el cercado de los arvernos nadie canta. {nombre} cuenta las noches que lleva aquí, porque así se cuenta el tiempo."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1 (el 54 a. C.)
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))
    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)

    private val ESPECIALES_T1 = mapOf(
        "muerdago" to unDia(
            Parrafo.MANANA to "Hoy es el sexto día de la luna, y {nombre} baja al robledal antes del alba con los otros aprendices. El suelo está duro de escarcha, y los robles, sin hojas, dejan ver lo que tienen en las ramas. En uno, alto, hay una mata redonda y verde que no es del roble: el muérdago.",
            Parrafo.TRABAJO to "Debajo del roble se prepara el sacrificio y el banquete, y traen dos toros blancos con los cuernos atados por primera vez. Un druida vestido de blanco sube al árbol, corta el muérdago con la hoz de oro, y abajo lo recogen en un manto blanco (Plinio, Historia natural 16.249–251). Luego viene el sacrificio, que {nombre} mira sin moverse.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el muérdago casi nunca crece en un roble, y que por eso, cuando crece, se tiene por cosa de los dioses (Plinio 16.249–251). Plinio dice que en su lengua le dan un nombre que quiere decir el que todo lo cura; los que leen a Plinio no se ponen de acuerdo en si habla de la planta o de la luna. {nombre} no se lo pregunta.",
        ),
        "nieve_escuela" to unDia(
            Parrafo.MANANA to "Hoy ha nevado en la noche, y la meseta amanece blanca y callada. {nombre} abre la puerta y la nieve le llega a la rodilla. No se oye el herrero, ni los carros, ni los perros. Solo, de vez en cuando, el golpe sordo de la nieve que se cae de un techo.",
            Parrafo.TRABAJO to "Hoy la escuela no es en el robledal sino en la casa del maestro, al pie de la meseta, junto al fuego, con los aprendices sentados en el suelo y las manos hacia las brasas. El maestro dice los versos más bajo que nunca, y ellos tienen que acercarse para oírlos. Nadie se distrae.",
            Parrafo.ANOCHECER to "Al caer la tarde vuelve a nevar, despacio, y {nombre} sube a la meseta pisando sus propias huellas de la mañana. Arriba, las casas son montañas pequeñas de nieve con humo encima. Su madre lo espera en la puerta con una tea, y le sacude la nieve del manto antes de dejarlo entrar.",
        ),
        "mil_versos" to unDia(
            Parrafo.TRABAJO to "Hoy el maestro lo hace levantarse delante de todos y decir, de corrido, los versos que aprendió este invierno. {nombre} empieza con una voz que no es la suya. A la mitad la voz vuelve. Llega al final sin equivocarse en uno, y se queda de pie, sin saber qué hacer con las manos.",
            Parrafo.GENTE to "El maestro no dice nada: asiente una vez, y pasa al siguiente. Eso, entre los aprendices, es lo más que se puede esperar. A la salida, Korisios le da un golpe en el hombro y le dice que se equivocó en una palabra, la del final. No es verdad. Los dos se ríen camino arriba.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ahora esos versos son suyos, que ya no se le pueden caer como se cae una moneda del cinto. Piensa que los dirá algún día a un muchacho que todavía no ha nacido, igual que se los dijeron a él. Piensa que eso es lo más parecido a no morirse que conoce.",
        ),
        "yegua" to unDia(
            Parrafo.MANANA to "Hoy su padre lo llama al corral antes de que baje a la escuela. Junto a él hay una yegua torda, joven, de crin clara, con las patas finas y los ojos tranquilos. Su padre le pone el ronzal en la mano y le dice que es suya. No le dice nada más, porque no hace falta.",
            Parrafo.TRABAJO to "Hoy, en el prado de detrás de la muralla, monta la yegua por primera vez delante de los guerreros. Ella lo prueba: un respingo, un giro, una carrera corta. Él no se cae. Esunertos grita que la yegua es mejor jinete que él. Los guerreros se ríen. La yegua, también, parece.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en lo que le dijo su padre al dársela, sin mirarlo: que un caballero no va a pie. Piensa que su padre nunca le ha preguntado qué quiere ser. Piensa que el maestro tampoco. Piensa que a lo mejor nadie en la meseta se lo pregunta a nadie, y que así está bien.",
        ),
        "siembra" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siembra de primavera, y antes del alba la casa grande baja entera a la llanura: los mozos con los bueyes, las mujeres con los sacos de grano, los niños con palos para espantar a los pájaros. {nombre} baja con ellos, con la azada al hombro. Hoy los versos esperan.",
                Parrafo.TRABAJO to "Hoy ara detrás de los bueyes, con el arado de madera y su reja de hierro, que abre la tierra sin darle la vuelta. Los bueyes van despacio y él también. Detrás, las mujeres echan la cebada a puñados, con un gesto ancho, y los niños la tapan arrastrando ramas.",
                Parrafo.PUEBLO to "Toda la llanura al pie de Gergovia está llena de gente: cada casa en sus campos, con sus bueyes, y por los caminos un ir y venir de carros con grano y con estiércol. Desde abajo la meseta se ve alta, con su muralla y su humo, como si fuera de otra gente.",
                Parrafo.ANOCHECER to "Al caer la tarde vuelven a la meseta con los bueyes cansados y los pies llenos de barro. {nombre} tiene las manos abiertas por la azada. Su madre se las unta con grasa junto al fuego y le dice que un druida con callos es una cosa rara. Él dice que todavía no es druida.",
            )),
            sigue = mapOf(
                Parrafo.TRABAJO to listOf(
                    "Otro día de siembra en la llanura. {nombre} ya no ara: siembra a puñados, como las mujeres, con el saco colgado del cuello, y le sale torcido. Una vieja le corrige el gesto sin decir nada, tomándole la muñeca. A la tercera vez, le sale derecho.",
                    "El último día de siembra se acaba el campo grande, el de la orilla del arroyo. Se tapan los surcos, se cierran los portillos para que no entre el ganado y se deja una parte del grano para el año que viene. La tierra queda lisa, parda, esperando.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Al caer la tarde se queda abajo con los mozos, junto a una hoguera en la orilla del campo, para que los ciervos no se coman lo sembrado. Asan avellanas guardadas del otoño. Arriba, en la meseta, se encienden los fuegos de las casas, y parecen estrellas bajas.",
                    "Anochece, y la casa grande sube a la meseta con los sacos vacíos. En el banquete de esta noche hay carne, porque la siembra se ha acabado. {nombre} se duerme sentado, con el cuerno en la mano, y su padre se lo quita sin despertarlo.",
                ),
            ),
        ),
        "celtilo" to unDia(
            Parrafo.GENTE to "Esta noche, junto al fuego, un viejo de la casa cuenta la historia de Celtilo, que fue el mayor príncipe de toda la Galia y al que mataron los suyos por querer hacerse rey (César 7.4). El viejo lo conoció. Dice que nadie en la Galia tenía tanta gente detrás, y que eso fue lo que lo mató. Celtilo tenía un hijo, que vive en Gergovia.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en lo que es un rey. Los arvernos no tienen; mandan los nobles, entre todos, y se vigilan unos a otros. El maestro dice que encima de los nobles están los druidas, que juzgan, y encima de los druidas, los dioses. Piensa que un rey sería alguien que no quiere a nadie encima.",
            Parrafo.ANOCHECER to "Al caer la noche, cuando se apaga el fuego, el viejo sigue hablando de Celtilo para nadie, en voz baja. {nombre} se queda a oírlo hasta el final. Luego sale a la puerta. La meseta duerme. En alguna de esas casas duerme el hijo de Celtilo, y {nombre} no sabe cuál.",
        ),
        "parte_maestro" to unDia(
            Parrafo.MANANA to "Hoy el maestro sale hacia la tierra de los carnutos, a la asamblea de los druidas, con otros dos druidas de los arvernos y un mozo con los caballos. {nombre} baja al robledal a despedirlo. El maestro le pone la mano en la cabeza, como cuando era niño, y le dice que no olvide nada.",
            Parrafo.TRABAJO to "Hoy en la escuela no está el maestro, y Korisios hace repetir los versos a los más pequeños, porque es el que mejor los sabe. Lo hace serio, igual que el maestro, sin repetir nada dos veces. {nombre} le corrige una palabra, por fastidiarlo. Korisios tenía razón.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la tierra de los carnutos, que se tiene por el centro de toda la Galia (César 6.13), y en el camino largo hasta allá, de muchos días, por bosques y por ríos. Piensa en los druidas de todos los pueblos juntos en un mismo sitio. No se lo puede imaginar. Lo intenta igual.",
        ),
        "asamblea" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Estos días, lejos, en un lugar sagrado de la tierra de los carnutos, se reúnen los druidas de toda la Galia, y el maestro está con ellos. {nombre} lo piensa en cuanto abre los ojos. Allí, dice el maestro, preside uno que tiene autoridad sobre todos los demás (César 6.13).",
                Parrafo.TRABAJO to "Hoy, en el robledal, los aprendices hablan de la asamblea más que de los versos. A ella acuden todos los que tienen pleitos, de todas partes, y aceptan lo que los druidas juzgan (César 6.13). A quien no lo acata le prohíben los sacrificios, y todos se apartan de él. Korisios dice que eso es peor que morir.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que a lo mejor un día irá él a esa asamblea, con su manto y sus versos, y juzgará un pleito de lindes entre dos familias que no conoce. Piensa que para eso hay que saber mucho más de lo que sabe. Piensa que a lo mejor para eso son los veinte años de escuela.",
            )),
            sigue = mapOf(
                Parrafo.PENSAMIENTO to listOf(
                    "{nombre} piensa en el maestro, allá lejos, viejo, sentado entre los druidas de todos los pueblos. Piensa que a lo mejor allí el maestro es uno más, y no el que lo sabe todo. Le cuesta pensarlo. Le gusta pensarlo un poco.",
                    "{nombre} piensa en los pleitos que estarán juzgando hoy en la tierra de los carnutos: muertes, herencias, lindes (César 6.13). Piensa que la Galia está llena de gente peleada, y que a pesar de eso viene a juntarse, una vez al año, en el mismo sitio.",
                ),
            ),
        ),
        "vuelve_maestro" to unDia(
            Parrafo.MANANA to "Hoy vuelve el maestro, a media mañana, cansado del camino, con el manto lleno de polvo y la barba más blanca, o eso le parece a {nombre}. Los aprendices bajan corriendo al robledal. El maestro se sienta en su piedra, pide agua y no dice nada hasta que se la bebe entera.",
            Parrafo.GENTE to "El maestro cuenta poco de la asamblea, y lo que cuenta lo cuenta despacio: quién estaba, qué se juzgó, qué pueblos andan peleados entre sí. De los romanos dice solo que en todas partes se habla de ellos. Korisios le pregunta qué se habla. El maestro dice que de todo, y que mal.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el maestro ha vuelto distinto, como más callado, y no sabe si es el cansancio. Piensa que, si la asamblea de los druidas habla de los romanos, es que los romanos ya no son cosa de otros. Piensa que no se lo va a decir a su padre. Su padre ya lo sabe.",
        ),
        "letras" to unDia(
            Parrafo.PUEBLO to "Hoy hay mercado en Gergovia, y junto a la puerta un mercader del sur ha extendido su manta con sal, cuentas de vidrio y cuchillos. Mientras vende, apunta cada cosa en una tablilla con letras griegas: tanto de sal, tanto de hierro, tanto que le deben. Los que compran miran cómo escribe.",
            Parrafo.GENTE to "El mercader le pregunta a {nombre}, al verlo mirar, si sabe letras. {nombre} dice que algunas. El mercader le enseña a escribir su nombre en la tablilla, con las letras griegas, como lo escriben los galos del sur. Él lo mira un rato. Su nombre, escrito, le parece de otro.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en por qué los druidas no escriben lo que enseñan, si los galos escriben casi todo lo demás. César dirá que, según él, es por dos cosas: para que la enseñanza no se divulgue, y para que los alumnos no se fíen de lo escrito y dejen de ejercitar la memoria (César 6.14). {nombre} cree que las dos son verdad.",
        ),
        "tormenta" to unDia(
            Parrafo.TIEMPO to "Hoy, después del calor de la mañana, el cielo se pone negro sobre los montes del oeste, y la tormenta cruza la llanura en un momento, con un viento que dobla los robles. Los truenos rebotan entre la meseta y los montes. La lluvia llega de golpe, caliente, en cortinas.",
            Parrafo.PUEBLO to "En la meseta todos corren a meter el ganado y a sujetar los techos de paja con piedras y cuerdas. Un rayo cae en el robledal de la otra ladera, y un roble viejo arde a pesar de la lluvia, como una tea. Desde la muralla lo miran todos, en silencio, hasta que se apaga.",
            Parrafo.ANOCHECER to "Al caer la tarde la tormenta se va hacia el río, y el cielo se limpia de golpe, rojo y amarillo. Huele a tierra mojada y a madera quemada. {nombre} baja con Korisios a ver el roble del rayo: está partido de arriba abajo, negro por dentro. Ninguno de los dos lo toca.",
        ),
        "siega_seca" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siega, y la casa grande baja a la llanura con las hoces. Este año no ha llovido desde la primavera, y el trigo ha salido corto y ralo, con las espigas pequeñas. Su padre lo mira desde el borde del campo con la cara de los años malos.",
                Parrafo.TRABAJO to "Hoy siega con los mozos de su padre, agachado, con la hoz de hierro en una mano y el puñado de tallos en la otra. Corta, ata, deja la gavilla, y otra vez. El trigo de este año es tan corto que hay que agacharse más. A media mañana ya le duele la espalda como si fuera viejo.",
                Parrafo.PUEBLO to "Por toda la llanura se siega a la vez, y en todos los campos se ve lo mismo: gavillas pequeñas y pocas. Los viejos dicen que no se acuerdan de un año tan seco. César escribirá que este año la cosecha de la Galia fue corta por falta de aguas (César 5.24).",
                Parrafo.ANOCHECER to "Al caer la tarde suben a la meseta con los carros medio vacíos. En la casa grande se cuentan las gavillas, y su madre hace la cuenta del invierno en voz baja, con los dedos. Habrá que matar antes algún cerdo, dice, y guardar más bellotas. Nadie la contradice.",
            )),
            sigue = mapOf(
                Parrafo.TRABAJO to listOf(
                    "Otro día de siega en la llanura. {nombre} ya no siente la espalda; siente las manos, llenas de cortes de la paja. Siega en fila con los demás, cantando lo que cantan los segadores para llevar el ritmo. El canto no se escribe tampoco. Nadie lo ha escrito nunca.",
                    "Siguen segando. Hoy le toca atar las gavillas detrás de los segadores y apilarlas en montones de diez, con las espigas hacia dentro, por si llueve. No llueve. Nadie cree ya que vaya a llover. Los montones de este año se cuentan rápido.",
                    "Hoy se trilla lo segado en la era de la casa, junto a la muralla, golpeando las espigas con palos sobre el suelo apisonado. Luego se avienta con el viento de la tarde. El grano cae a un lado y la paja vuela al otro. Del grano, este año, hay poco.",
                ),
                Parrafo.PUEBLO to listOf(
                    "En la meseta se habla del trigo en todas las puertas: quién ha sacado más, quién menos, quién tendrá que pedir. Los mercaderes del mercado ya han subido el precio del grano, y la gente los mira mal.",
                    "Desde la muralla se ve la llanura segada, de un amarillo sucio, con los montones de gavillas como granos de arena. Los rebaños ya bajan a comerse el rastrojo. Este año, comen poco también.",
                ),
            ),
        ),
        "britania" to unDia(
            Parrafo.PUEBLO to "Hoy en el mercado no se habla de otra cosa: unos mercaderes que vienen del norte dicen que César ha vuelto a cruzar el mar hacia Britania, esta vez con ochocientas naves, cinco legiones y dos mil jinetes. La gente hace la cuenta con los dedos y no le salen las cuentas.",
            Parrafo.GENTE to "Su padre escucha la noticia sin decir nada, y luego dice que, mientras César esté al otro lado del mar, no está aquí. Esunertos dice que ojalá se ahogue. El maestro, cuando {nombre} se lo cuenta, no dice nada, pero se queda mirando hacia el norte un buen rato.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Britania, que para él es casi solo un nombre. Allí, dicen, nació la enseñanza de los druidas, y allí van todavía los que la quieren saber a fondo (César 6.13). Piensa que ahora allí también hay romanos. Piensa que eso, al maestro, le tiene que doler.",
        ),
        "banquete" to unDia(
            Parrafo.GENTE to "Esta noche hay banquete grande en la casa de su padre, con invitados de otros oppida, y ha venido un bardo de fuera, flaco y con la voz enorme. Con su instrumento, parecido a una lira, canta las alabanzas del abuelo de {nombre}, que fue un gran caballero, y las burlas de un vecino tacaño, que está presente (Diodoro 5.31).",
            Parrafo.TRABAJO to "Esta tarde ayuda a preparar el banquete: a traer leña, a colgar el caldero grande de su cadena, a cortar el cerdo en pedazos y a llenar los cuernos. La carne se cuece desde temprano, y el olor sube hasta las vigas. Los perros no se apartan del fuego.",
            Parrafo.ANOCHECER to "Ya de noche, el vecino burlado se levanta, se ríe más fuerte que nadie y le da al bardo un anillo de bronce para que no siga. El bardo se lo guarda y sigue igual. Todos aplauden. {nombre} se acuesta tarde, con la cabeza llena de música y el cuerno todavía en la mano.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los bardos y los druidas guardan lo mismo, la memoria, pero de otra manera: los bardos, a gritos, en los banquetes, para que todos lo oigan; los druidas, en voz baja, en el bosque, para que lo oigan pocos. Piensa que él está en medio. Siempre está en medio.",
        ),
        "estrellas" to unDia(
            Parrafo.ANOCHECER to "Esta noche es tan clara que el maestro saca a los aprendices del robledal y los sube a un claro, donde se ve el cielo entero. Les habla de los astros y de su movimiento, de cómo giran y vuelven, y de lo grande que es la tierra (César 6.14). Habla hasta que hace frío, y luego sigue.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que las estrellas vuelven siempre al mismo sitio, aunque tarden, y que el alma, dice el maestro, también vuelve, en otro cuerpo. Piensa que todo da la vuelta. Piensa que a lo mejor por eso los suyos cuentan por noches: porque de noche se ve cómo gira todo.",
            Parrafo.GENTE to "Korisios se queda dormido en la hierba a la mitad, y el maestro no lo despierta. Otro aprendiz pregunta qué hay más allá de las estrellas, y el maestro le dice que eso no se enseña en una noche. {nombre} pregunta cuánto mide la tierra. El maestro le dice una cifra que no le cabe en la cabeza.",
        ),
        "manto" to unDia(
            Parrafo.MANANA to "Hoy su madre lo para en la puerta antes de que salga y le echa encima un manto nuevo, de lana gruesa, a cuadros rojos, verdes y amarillos, que ha estado tejiendo todo el verano a escondidas. Le queda largo. Ella dice que crecerá. Él ya no crece, pero no se lo dice.",
            Parrafo.GENTE to "Su hermana dice que el manto es demasiado bueno para un aprendiz que se pasa el día sentado en la hojarasca, y que se lo va a llenar de musgo. Su madre le dice que se calle. Su padre, al verlo, toca la lana con dos dedos y dice que es buena. Eso, en su padre, es un discurso.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el manto lleva los colores de la casa, y que con él puesto cualquiera sabe de dónde es. Piensa que en el robledal todos son iguales, de la casa que sean. Piensa que lo llevará igual. Hace frío, y el manto es muy bueno.",
        ),
        "tasgecio" to unDia(
            Parrafo.PUEBLO to "Hoy llega a Gergovia, por un mercader, la noticia de que los carnutos han matado a Tasgecio, el rey que César les había puesto, al tercer año de su reinado. Lo han matado los suyos (César 5.25). En la puerta de la meseta se forma un corro, y cada uno lo cuenta a su manera.",
            Parrafo.GENTE to "En la casa grande, su padre dice que un rey puesto por los romanos es un rey de los romanos, y que los carnutos han hecho lo que tenían que hacer. Un noble que está de visita dice que César no lo va a dejar pasar. Su padre dice que ya veremos. Nadie dice nada más.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Celtilo, al que también mataron los suyos, por querer ser rey sin que nadie se lo diera. Y ahora Tasgecio, por ser rey porque se lo dio un romano. Piensa que en la Galia nadie quiere tener a nadie encima. Piensa que eso, a la larga, es bueno y es malo.",
        ),
        "samonios" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan las tres noches de Samonios, el primer mes del año. Los suyos cuentan el tiempo por noches, y al empezar los meses y los años la noche va delante del día (César 6.18): por eso la fiesta empezó anoche, al ponerse el sol, y {nombre} amanece ya dentro de ella.",
                Parrafo.PUEBLO to "En la meseta se apilan troncos para las hogueras de las tres noches, y se matan cerdos para el banquete. Las mujeres sacan los mantos buenos y los torques salen de sus cofres. Los niños corren por las calles con teas apagadas, esperando que caiga la tarde.",
                Parrafo.ANOCHECER to "Al caer la noche se encienden las hogueras en la meseta, y desde el borde se ven otras, lejos, en los oppida vecinos y en las granjas de la llanura, como si alguien hubiera sembrado fuegos. {nombre} se queda mirándolas con el manto sobre los hombros. Es la primera noche del año.",
            )),
            sigue = mapOf(
                Parrafo.ANOCHECER to listOf(
                    "La segunda noche de Samonios hay banquete en la casa grande hasta muy tarde, y el bardo canta los hechos del año que se va: lo bueno y lo malo, sin olvidar nada, con nombres. {nombre} escucha su propio nombre en un verso, y se pone rojo hasta las orejas.",
                    "La tercera noche de Samonios se apagan las hogueras despacio, y la meseta se queda oscura y tranquila. {nombre} sale a la puerta antes de dormir. El año nuevo huele a ceniza y a helada. Cuenta las noches que faltan para el siguiente Samonios. Son muchas.",
                ),
                Parrafo.PUEBLO to listOf(
                    "En la meseta nadie trabaja más de lo que hace falta en estas noches. Los vecinos van de casa en casa con un cuerno de cerveza, y en cada puerta se dice algo del año que empieza. Los perros, hartos de huesos, duermen al sol.",
                    "Hoy suben a la meseta los de las granjas de abajo, la gente de su padre, con sus regalos para la casa grande: un cordero, un queso, un cesto de manzanas. Su padre los recibe en la puerta, uno por uno. {nombre} los mira llegar y los mira irse.",
                ),
            ),
        ),
        "atuatuca" to unDia(
            Parrafo.PUEBLO to "Hoy corre por Gergovia una noticia del norte, de boca en boca, más rápido que los caballos: los eburones de Ambiórix, en tierra de los belgas, han caído sobre una legión romana y cinco cohortes en su invernada y han acabado con ellas (César 5.24–37). Una legión y media. La gente lo repite como si no lo creyera.",
            Parrafo.GENTE to "Esunertos llega a la casa grande gritando la noticia antes de cruzar la puerta, y los guerreros de su padre se levantan. Su padre los manda sentar. Dice que una legión no es César, y que César va a volver a cobrársela. Esunertos dice que entonces habrá que estar listos. Su padre no contesta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hasta hoy los romanos le parecían como el invierno: algo que llega y no se puede parar. Piensa que hoy alguien los ha parado. Piensa en el maestro, que dirá que no se alegre de muertes. Se alegra un poco, de todas formas, y luego se avergüenza.",
        ),
        "versos_juicios" to unDia(
            Parrafo.TRABAJO to "Hoy, al terminar los versos de la mañana, el maestro lo aparta de los demás y le dice que en primavera empezará con los versos de los juicios: los de las herencias, las lindes y las muertes, con los que los druidas sentencian los pleitos (César 6.13). Se lo dice como quien dice que va a llover.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los versos de los juicios son los que llevan a uno a decidir sobre la vida de los otros, y que hasta ahora los suyos solo eran para él. Piensa que el maestro lo cree preparado. Piensa que él no se cree preparado. Piensa que a lo mejor eso es estar preparado.",
            Parrafo.ANOCHECER to "Al caer la tarde sube a la meseta despacio, por el camino largo, para pensar. Desde la cuesta se ve la llanura gris de diciembre y, sobre la muralla, el humo de las casas. En la casa grande no le cuenta nada a nadie todavía. Se lo cuenta a la yegua, que no lo dirá.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "aprendiz_druida", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + CAMPANA_MANANA + ALESIA_MANANA + CAUTIVO_MANANA,
            Parrafo.TIEMPO to TIEMPO + CAMPANA_TIEMPO + ALESIA_TIEMPO + CAUTIVO_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + CAMPANA_TRABAJO + ALESIA_TRABAJO + CAUTIVO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + CAMPANA_PUEBLO + ALESIA_PUEBLO + CAUTIVO_PUEBLO,
            Parrafo.GENTE to GENTE + CAMPANA_GENTE + ALESIA_GENTE + CAUTIVO_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + CAMPANA_ANOCHECER + ALESIA_ANOCHECER + CAUTIVO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES_T1,
    )
}
