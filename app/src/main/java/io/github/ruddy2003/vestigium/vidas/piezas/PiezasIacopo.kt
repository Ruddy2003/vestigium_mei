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
 * El cuento de Iacopo, aprendiz en el taller de Domenico Ghirlandaio, en Florencia, de 1490 a 1494. Sigue la gramática
 * y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día es del taller: muele colores, prepara tablas y paredes, pinta lo que le dejan; por la tarde, la plaza y la
 *   taberna. Come dos veces y trabaja dos tramos, así que esos textos valen a cualquiera de sus horas.
 * - El pueblo lee los arcos «capilla» (los frescos de la capilla Tornabuoni, que se descubren al final de la temporada 1)
 *   y «tabla» (su primera tabla, de la temporada 4 a la 5).
 * - En la temporada 3 pasa seis semanas pintando una capilla en el Mugello (estado «campo»).
 * - No repite lo que ya dice «Su vida»: que entró a los trece, que duerme en el taller y come en la mesa del maestro,
 *   las sesenta mil personas, los bancos y los gremios, los libros impresos, el vino en cada comida y el taller propio.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG4.kt`):
 * - El maestro Domenico vive hasta `muere_maestro` (temporada 5).
 * - Caterina aparece en `caterina` (temporada 1); se prometen en `promesa` (4); el anillo, en `anillo` (5).
 * - Nanna, su hermana, se casa en `boda_nanna` (temporada 3).
 * - La tabla empieza en `tabla` (temporada 4) y se firma en `firma` (5).
 * - Lorenzo de Médici muere en `lorenzo` (temporada 3).
 * - Las fiestas que vuelven cada año no nombran a Caterina ni al maestro Domenico, ni dicen si Nanna está casada.
 */
internal object PiezasIacopo {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A {nombre} lo despierta el frío del taller, donde duerme en un jergón detrás de la mesa de moler, con Masino al lado roncando. Por las rendijas de la puerta entra la primera luz y el ruido de la calle: un carro, un gallo, una campana. Se levanta antes de que lo llamen."),
        v("{nombre} se despierta con las campanas de la ciudad, que empiezan una detrás de otra, primero la de la catedral, luego las de los conventos, hasta que suena toda Florencia. Se lava la cara en el cubo del patio y se pone la ropa de trabajo, manchada de todos los colores."),
        v("Lo primero del día, en el taller, es abrir las contraventanas para que entre la luz, barrer el suelo, que está lleno de yeso y de polvo de colores, y encender el fuego para la cola. Es el trabajo de los aprendices. {nombre} lo hace sin pensar, desde los trece años."),
        v("{nombre} sale a la calle temprano a buscar agua a la fuente y pan al horno, con la cesta del taller, y Florencia ya está despierta: los tenderos abren, los mendigos se colocan, los frailes pasan deprisa. Sobre los tejados, la cúpula de la catedral, roja, enorme, como siempre."),
        v("A {nombre} lo despierta Masino con un pie en las costillas, que es como se despiertan los aprendices. Se pelean un rato en el jergón, en silencio, para no despertar al maestro, que duerme arriba. Luego se levantan los dos, porque el que llega tarde a la mesa de moler, muele el doble."),
        v("{nombre} se despierta pensando en un dibujo que soñó, y antes de que se le olvide lo hace con carbón en un trozo de papel: una mano, una cara de perfil, unos pliegues. Luego lo esconde debajo del jergón, con los otros. Algún día se los enseñará a alguien."),
    )

    /** Las seis semanas pintando una capilla en el Mugello (estado «campo», temporada 3). */
    private fun ca(texto: String, hacer: Hacer? = null) = v(texto, estado = "campo", hacer = hacer)

    private val CAMPO_MANANA = listOf(
        ca("{nombre} se despierta en la casa del cura de la parroquia, en el Mugello, donde le han dado un cuarto con Bastiano, y lo primero que oye son pájaros, cientos, y una vaca. Ni campanas de ciudad, ni carros, ni gritos. Se asoma a la ventana: colinas, cipreses, niebla en el valle."),
        ca("A {nombre} lo despierta el gallo del cura, que canta debajo de su ventana como si le pagaran por ello. Se levanta, se lava en el pozo del patio y sube a la iglesia, donde la pared de la capilla lo espera, húmeda del enlucido de ayer."),
        ca("{nombre} sale temprano de la casa del cura y camina hasta la iglesia por un sendero entre olivos y viñas, con el sol todavía bajo. Los campesinos ya están en el campo y lo saludan con la mano. En Florencia nadie lo saluda. Aquí todos saben quién es: el pintor."),
    )

    private val CAMPO_TRABAJO = listOf(
        ca("A esta hora pinta en la capilla de la iglesia del pueblo, con Bastiano, que pinta las caras mientras {nombre} pinta los fondos: un paisaje de colinas como las de fuera, con un castillo y un río. Pinta deprisa, porque el enlucido se seca y el fresco no espera.", Hacer.TRABAJO),
        ca("A esta hora extiende el enlucido fresco en el trozo de pared que se pintará hoy, ni más ni menos, porque lo que no se pinte hoy se seca y hay que picarlo. Luego pone encima el cartón agujereado y lo golpea con una bolsita de carbón, y el dibujo pasa a la pared en puntitos negros.", Hacer.TRABAJO),
        ca("A esta hora muele colores en la sacristía, que el cura les prestó, con la piedra de moler que trajeron de Florencia en la mula. Los niños del pueblo se asoman a la puerta a mirar. {nombre} les deja moler un poco de ocre. Se ponen amarillos hasta las orejas.", Hacer.TRABAJO),
        ca("A esta hora come en la cocina del cura, con Bastiano, lo que pone la criada: sopa de pan con verduras del huerto, un queso de oveja, habas y vino de la parroquia, que es fuerte y áspero. El cura bendice la mesa y luego habla sin parar de su iglesia, que es lo único que le importa.", Hacer.COMER),
        ca("A esta hora come a la sombra de un olivo, junto a la iglesia, pan, cebolla, queso y unos higos que le regaló una vieja del pueblo. Bastiano duerme la siesta en la hierba con el sombrero en la cara. {nombre} dibuja el olivo. Le sale mal. Lo vuelve a dibujar.", Hacer.COMER),
        ca("A esta hora está en la plaza del pueblo, que es una fuente, una iglesia y una taberna, con los campesinos que vuelven del campo. Le preguntan por Florencia como si fuera otro mundo. Él exagera un poco. Ellos lo saben y le invitan a vino igual.", Hacer.GENTE),
        ca("A esta hora está sentado con Bastiano en el muro de la iglesia, mirando cómo se pone el sol sobre las colinas, y Bastiano le cuenta de su pueblo, San Gimignano, que tiene torres altas como chimeneas. {nombre} escucha y mira el cielo, para pintarlo mañana.", Hacer.GENTE),
        ca("A esta hora duerme en el cuarto de la casa del cura, en una cama de verdad, la primera que tiene en su vida, y no puede dormir de tan blanda. Afuera cantan los grillos y las ranas. Bastiano ronca. {nombre} cuenta las estrellas por la ventana.", Hacer.DORMIR),
        ca("A esta hora duerme profundo, cansado del aire del campo, que dicen que cansa más que el de la ciudad. Sueña con la pared de la capilla, con el enlucido que se seca antes de terminar. Se despierta asustado. Es de noche. La pared seguirá ahí mañana.", Hacer.DORMIR),
    )

    private val CAMPO_PUEBLO = listOf(
        ca("El pueblo es una docena de casas de piedra alrededor de una iglesia vieja, en lo alto de una colina del Mugello, entre olivos, viñas y bosques de castaños. Desde la plaza se ven otras colinas, otros pueblos, y a lo lejos, en un día claro, la cúpula de Florencia."),
        ca("La iglesia del pueblo es pequeña y oscura, de piedra, con un campanario cuadrado y una capilla nueva que pagó un mercader de lana de Florencia que nació aquí, para que se acuerden de él. Para eso están pintando: para que el pueblo se acuerde de alguien que se fue."),
    )

    private val CAMPO_ANOCHECER = listOf(
        ca("Al caer la tarde {nombre} limpia los pinceles en el pozo de la iglesia y mira lo que pintaron hoy, que a la luz del atardecer parece mejor de lo que es. Bastiano dice que mañana lo verá peor. Tiene razón. Bajan a cenar a la casa del cura."),
        ca("Anochece en el Mugello, y las colinas se ponen azules y luego negras, y en las casas del pueblo se encienden luces pequeñas, que no son nada comparadas con las de Florencia. {nombre} las mira desde la plaza. Le gusta más así, aunque no lo diga."),
        ca("De noche, en el campo, el silencio es tan grande que se oye el río del valle y los perros de los pueblos de enfrente. {nombre} piensa en Florencia, en el taller, en Caterina, en el ruido. Luego piensa que dentro de un mes lo echará de menos esto.",),
    )

    private val CAMPO_TIEMPO = listOf(
        ca("En el campo el cielo se ve entero, de colina a colina, y los campesinos lo leen mejor que los astrólogos de la ciudad. {cielo} {de_estacion}"),
        ca("{cielo} {de_estacion} En el Mugello el aire es más fresco que en Florencia, y huele a heno y a castaños."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el taller el tiempo importa por la luz y por el yeso, que con la humedad no se seca."),
        v("{nombre} mira el cielo desde la puerta del taller, recortado entre los tejados. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} El maestro de los colores dice que el azul del cielo de Florencia no se puede pintar, y que por eso lo intentan todos."),
        v("{cielo} En {estacion} la luz del taller cambia, y {nombre} mueve las tablas de sitio para seguirla."),
        v("Desde el puente se ve el río, verde y lento, con sus molinos y sus pescadores, y las colinas de alrededor llenas de villas y cipreses. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los astrólogos de la ciudad leen en el cielo el futuro de los príncipes. A {nombre} le basta con saber si va a llover sobre las tablas recién enyesadas."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y azul, de un azul que en el taller se intenta con azurita y nunca sale.",
            "Hace un día claro, y las colinas de alrededor se ven como si estuvieran pintadas.",
            "No hay una nube, y la luz entra en el taller tan limpia que se ven todos los defectos.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, y la luz del taller es pareja y sin sombras, buena para dibujar.",
            "Hay nubes sobre las colinas, y la cúpula de la catedral parece más oscura.",
            "Está nublado, y los colores se ven más apagados.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y las calles se vuelven arroyos y el yeso de las tablas no se seca.",
            "Cae una lluvia fina y terca, de las de Florencia, que no moja pero empapa.",
            "Llueve fuerte, y el río crece y se pone del color del barro.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos retumban en la cúpula y los rayos caen en las colinas.",
            "El cielo se pone negro sobre la ciudad y cae una tormenta de verano que limpia las calles.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, cosa rara en Florencia, y la cúpula se queda blanca como un pastel.",
            "Caen copos sobre la ciudad, y los niños hacen figuras de nieve en las plazas.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla en el río, y los puentes salen de ella como si flotaran.",
            "La niebla de la mañana tapa la cúpula, y la ciudad parece no tener cabeza.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento de los montes, frío, que se mete por las rendijas del taller.",
            "Hay viento, y las banderas del palacio de la Señoría golpean contra sus astas.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando las colinas se llenan de flores y la ciudad de fiestas.",
            "En primavera la luz es la mejor del año para pintar.",
            "Es el tiempo en que se secan bien los yesos y se empiezan las obras grandes.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, el tiempo del calor que pega los colores al pincel.",
            "En verano los ricos se van a sus villas y la ciudad huele a río.",
            "Es el tiempo de las fiestas de San Juan y de las moscas.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo de la vendimia en las colinas y de las primeras lluvias.",
            "En otoño la luz se pone dorada y los días se acortan.",
            "Es el tiempo de las castañas asadas en las esquinas.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando la cola se hiela en el bote y los dedos no obedecen.",
            "En invierno la luz del taller dura poco y hay que pintar deprisa.",
            "Es el tiempo de los braseros, de la niebla y de los sabañones.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora muele colores en la losa de pórfido, con la piedra de moler, en círculos, durante horas: ocre, tierra verde, azurita, cinabrio. Cada color tiene su manera y su tiempo. El maestro pasa, toca el polvo con un dedo, lo frota y dice: más. Siempre dice más.", hacer = Hacer.TRABAJO),
        v("A esta hora prepara una tabla para un retablo: la madera ya está seca y encolada, y él le da capas de yeso, primero grueso y luego fino, una tras otra, dejando secar cada una, y luego la raspa hasta que queda lisa como el marfil. Es un trabajo de días. No se puede tener prisa.", hacer = Hacer.TRABAJO),
        v("A esta hora pinta un ropaje en una tabla del maestro: el manto azul de una santa, con los pliegues que el maestro dibujó, del oscuro al claro, con pinceladas finas de temple, una encima de otra. El maestro pinta las caras y las manos. Los aprendices, lo demás.", hacer = Hacer.TRABAJO),
        v("A esta hora copia dibujos del maestro en un papel, con carbón y con pluma, una y otra vez: manos, pies, cabezas, pliegues. Es como se aprende. Masino copia a su lado y se ríe de las manos de {nombre}, que le salen con seis dedos. Son cinco. Casi.", hacer = Hacer.TRABAJO),
        v("A esta hora prepara la témpera: separa la yema de un huevo, la rompe con una aguja, la mezcla con un poco de agua y con el color molido, en un platito, hasta que está como la nata. Se seca rápido, así que se hace poca. El taller entero huele a huevo.", hacer = Hacer.TRABAJO),
        v("A esta hora dora el fondo de una tabla: sobre el yeso se pone una capa de bol rojo, se moja, se pone la hoja de oro con un pincel plano, sin tocarla con los dedos, y cuando se seca se bruñe con una piedra de ágata hasta que brilla como el sol. Un estornudo y se pierde un florín.", hacer = Hacer.TRABAJO),

        v("A esta hora come en la mesa del taller, con los otros aprendices y los oficiales, lo que trae la criada del maestro: sopa de pan con verduras, habas, un trozo de queso y vino aguado. Se come deprisa y con las manos sucias de color, aunque el maestro diga que se las laven.", hacer = Hacer.COMER),
        v("A esta hora come de pie, junto a la mesa de moler, un trozo de pan sin sal, que es como se hace en Florencia, con un poco de embutido y un vaso de vino. Masino le roba el embutido. Él le roba el vino. Así se queda todo igual.", hacer = Hacer.COMER),
        v("A esta hora come en la mesa de la casa, con la familia del maestro y los del taller: lentejas con tocino, pan, unas aceitunas y un vino tinto de las colinas. Davide cuenta cómo pintaron en Roma, años atrás, la capilla del papa Sixto. Los aprendices ya se lo saben de memoria.", hacer = Hacer.COMER),

        v("A esta hora pasea con Masino por la plaza de la Señoría, donde a esta hora se junta media Florencia: mercaderes, frailes, soldados, muchachas que pasean con sus madres, charlatanes que venden remedios. Miran a las muchachas. Las muchachas no los miran. O sí.", hacer = Hacer.GENTE),
        v("A esta hora está en una taberna del Oltrarno, cerca de su casa, con Masino y otros aprendices de otros talleres, bebiendo vino barato y discutiendo quién es mejor pintor: si su maestro o el de los otros. Siempre gana el suyo. Siempre gana el de los otros. Nadie paga a tiempo.", hacer = Hacer.GENTE),
        v("A esta hora visita la casa de sus padres en el barrio de Santo Spirito, donde su padre carda lana y su madre hila, y cenan sopa con él. Su madre le pregunta si come. Su padre le pregunta cuánto gana. Él contesta lo mismo a los dos: poco.", hacer = Hacer.GENTE),

        v("A esta hora duerme en el jergón del taller, detrás de la mesa de moler, con Masino al lado y el olor de la cola, del huevo y del barniz. Por la ventana se oye la ronda de la noche y algún borracho que canta. {nombre} no los oye: duerme como una piedra.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con colores que no existen, que se le ocurren de noche y que de día no sabe mezclar. Se despierta y piensa que un día los mezclará. Luego se vuelve a dormir, porque mañana hay que moler.", hacer = Hacer.DORMIR),
        v("A esta hora duerme mal, porque hace frío y el jergón es delgado, y se abraza a Masino para tener calor, que es lo que hacen los aprendices en invierno. Masino ronca. {nombre} lo empuja. Masino sigue roncando.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El taller está en una casa grande de la ciudad: abajo, una sala con mesas, caballetes, tablas, cartones colgados y la mesa de moler; arriba, la casa del maestro y su familia. La puerta da a la calle, y los clientes entran a mirar como si fuera una tienda. {entorno}"),
        v("{entorno} Florencia es una ciudad de piedra parda y tejados rojos, dentro de una muralla, partida en dos por el río, con la cúpula de la catedral en medio, más grande que todo, como una montaña hecha a mano. Desde cualquier calle se ve."),
        v("En las calles de Florencia hay más talleres que en ninguna ciudad: de pintores, de escultores, de orfebres, de tejedores de seda, de carpinteros que tallan cofres de boda. En cada uno hay aprendices como {nombre}, que muelen, barren y sueñan. {entorno}"),
        v("Al otro lado del río, en el Oltrarno, está el barrio de {nombre}: Santo Spirito, con su iglesia grande y sin fachada, y el Carmine, donde están los frescos de Masaccio que todos los aprendices van a copiar. Allí viven los cardadores, los tintoreros y los pobres. {entorno}"),
        v("{entorno} El Puente Viejo está lleno de tiendas de carniceros y curtidores, que tiran los restos al río, y huele tanto que los señores lo cruzan con un pañuelo en la nariz. Los aprendices lo cruzan sin pañuelo. Ya no lo huelen."),
        v("En la plaza del mercado viejo se vende todo: verduras, pescado, gallinas, telas, cuchillos, libros impresos. Hay un charlatán que saca muelas y un fraile que predica, y los dos tienen público. {nombre} compra ahí los huevos para la témpera. {entorno}"),
    )

    /** Lo que se ve del trabajo del taller: la capilla mientras se termina y la tabla de Iacopo. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("capilla", 0.0, 0.99, frases = listOf(
            "En Santa María Novella, la capilla Tornabuoni sigue con andamios.",
            "Los frescos de la capilla Tornabuoni están casi terminados.",
        )),
        Entorno("tabla", 0.01, 0.99, frases = listOf(
            "En un caballete del rincón está la tabla de Iacopo, a medio pintar.",
            "La Virgen de Iacopo espera en su caballete, tapada con un paño.",
        )),
        Entorno("tabla", 0.99, 1.0, frases = listOf(
            "La primera tabla de Iacopo ya está en casa del sedero.",
            "El sedero enseña a sus visitas la Virgen de Iacopo.",
        )),
        Entorno("capilla", 0.99, 1.0, frases = listOf(
            "En Santa María Novella, la gente va a ver la capilla Tornabuoni.",
            "En el taller hay tablas a medio hacer y cartones nuevos.",
        )),
    )

    private val GENTE = listOf(
        v("Masino es el otro aprendiz, de su edad, hijo de un carpintero de San Frediano, flaco, rápido y bocazas, que dibuja peor que {nombre} y pinta mejor. Comparten el jergón, el pan, las peleas y los secretos. Masino dice que un día serán más famosos que el maestro. {nombre} no lo cree."),
        v("Davide, el hermano del maestro, es el que lleva las cuentas, los encargos y los mosaicos del taller, más callado y más paciente que Domenico. Es el que enseña a los aprendices los trucos que el maestro olvida enseñar. {nombre} le tiene más confianza que a nadie."),
        v("Bastiano, el cuñado del maestro, es un pintor de San Gimignano, alegre y gordo, que pinta caras dulces de santos y cuenta chistes sucios mientras pinta. A {nombre} le deja pintar los fondos de sus tablas, y le corrige con un golpe del mango del pincel en los nudillos."),
        v("El padre de {nombre} es cardador de lana en el Oltrarno: trabaja para un mercader de la lana que le paga poco y tarde, y tiene las manos llenas de cortes y de grasa de oveja. No entiende la pintura. Le pregunta cada vez si ya es maestro. Cada vez {nombre} le dice que todavía no."),
        v("La madre de {nombre} hila lana en casa, para el mismo mercader que su marido, y le guarda siempre un trozo de algo cuando él viene: un queso, unas castañas, un dulce. Cree que su hijo pinta santos para el papa. {nombre} no le dice que pinta mantos."),
        v("El maestro Domenico es un hombre de cuarenta años, alto, rápido, con un genio que se enciende y se apaga como un candil. Pinta más rápido que nadie en Florencia y dice que le gustaría pintar todas las murallas de la ciudad. A {nombre} lo llama «el del Oltrarno». No sabe su nombre.", hasta = "muere_maestro"),
        v("Desde que murió el maestro Domenico, el taller lo llevan Davide y Benedetto, sus hermanos, y Bastiano. No es lo mismo: se pinta igual, pero se grita menos. A veces {nombre} levanta la cabeza esperando oírlo entrar con prisa, y luego se acuerda.", desde = "muere_maestro"),
        v("Nanna, su hermana, tiene diecisiete años y ayuda a su madre a hilar. Es la única de la familia que entiende lo que hace {nombre}: le pide que le dibuje la cara y luego dice que le salió fea. Su padre le busca marido. Ella dice que no tiene prisa.", hasta = "boda_nanna"),
        v("Nanna, su hermana, está casada con un tejedor de Oltrarno y vive a dos calles de sus padres. Ya espera un hijo. Cuando {nombre} va a verla, le pregunta por Caterina antes que por nada, y se ríe de lo colorado que se pone.", desde = "boda_nanna"),
        v("Caterina es la hija del vendedor de colores del Puente Viejo, la que despacha el azul y el oro en la tienda de su padre. Tiene las manos manchadas de azurita y una risa que se oye desde la calle. {nombre} compra colores que no hacen falta para verla.", desde = "caterina", hasta = "promesa"),
        v("Caterina está prometida con {nombre}, aunque su padre dice que no habrá boda hasta que él tenga taller propio, y eso pueden ser años. Se ven en misa, en la tienda y en las fiestas, siempre con alguien mirando. Ella le guarda los mejores azules.", desde = "promesa"),
        v("A veces {nombre} se acuerda de Miguel Ángel, el muchacho que estuvo en el taller hace poco, dibujando mejor que todos y peleándose con todos. Ahora está en el jardín de los Médici, aprendiendo a esculpir. Masino dice que es un engreído. {nombre} dice que es un genio. Los dos tienen razón.", hasta = "lorenzo"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del taller: porque se lo enseñó un oficial a golpes de pincel en los nudillos."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En el taller se dice que un pintor es lo que sabe moler, y que el que no sabe moler no sabe pintar."),
        v("{dato_objeto} Son cosas que se aprenden mirando, como casi todo en el taller."),
        v("Un aprendiz no tiene casi nada suyo: todo lo que toca es del maestro. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("pincel", "el pincel", "un pincel", "El pincel",
            "El pincel se hace en el taller: pelos de la cola de una ardilla, atados con hilo y metidos en el cañón de una pluma, de buitre para los gruesos y de paloma para los finos; uno bueno pinta un cabello, y se cuida más que los zapatos."),
        ObjetoCuento("losa", "la losa de moler", "una losa de moler", "La losa",
            "La losa de moler es una piedra de pórfido, rojo y durísimo, que vino de Roma, de alguna ruina; encima se muelen los colores con otra piedra redonda, en círculos, con agua, durante horas, y el pórfido no se gasta ni se mancha."),
        ObjetoCuento("ultramar", "el azul de ultramar", "un poco de azul de ultramar", "El ultramar",
            "El azul de ultramar se saca de una piedra que viene de más allá del mar, de montañas que nadie del taller ha visto; se muele, se amasa con cera y resina y se lava hasta que suelta el azul; vale más que el oro, y en los contratos se dice cuánto lleva el manto de la Virgen."),
        ObjetoCuento("huevo", "el huevo", "un huevo", "El huevo",
            "El huevo es lo que pega el color a la tabla: se usa la yema, sin la piel, mezclada con el color molido; seca rápido y queda mate; los pintores de Flandes usan aceite, que seca despacio y brilla, y en Florencia ya hay quien prueba."),
        ObjetoCuento("yeso", "el yeso", "un cubo de yeso", "El yeso",
            "El yeso para las tablas se apaga en agua durante semanas, removiéndolo cada día, para que quede fino y no se agriete; se mezcla con cola de piel de cabra, caliente, y se da en capas; cuantas más capas, más lisa la tabla y mejor el oro."),
        ObjetoCuento("tabla", "la tabla", "una tabla", "La tabla",
            "La tabla para pintar es de madera de álamo, seca durante años, unida con colas y con cuñas de madera; encima se pega una tela, luego el yeso, luego el dibujo, el oro y el color; una tabla mal secada se curva con los años y raja la pintura."),
        ObjetoCuento("carboncillo", "el carboncillo", "un carboncillo", "El carboncillo",
            "El carboncillo es un palito de sauce quemado dentro de una olla cerrada; dibuja negro, suave, y se borra con una pluma o con miga de pan; con él se hacen los primeros trazos de todo, y luego se repasan con tinta."),
        ObjetoCuento("carton", "el cartón", "un cartón", "El cartón",
            "El cartón es un dibujo del tamaño de la pared, en hojas de papel pegadas, con las figuras tal como irán en el fresco; se agujerea siguiendo las líneas y se golpea con una bolsita de carbón, y el dibujo pasa a la pared en puntitos."),
        ObjetoCuento("pan_oro", "el pan de oro", "una hoja de pan de oro", "El pan de oro",
            "El pan de oro lo hacen los batidores, que golpean un florín entre pieles hasta sacar cien hojas tan finas que vuelan con el aliento; se coge con un pincel plano pasado antes por el pelo."),
        ObjetoCuento("libro", "el libro impreso", "un libro impreso", "El libro",
            "El libro impreso del taller es de medidas y figuras, con letras de molde, y costó menos que una tabla pequeña; antes, un libro así valía un año de sueldo."),
        ObjetoCuento("florin", "el florín", "un florín", "El florín",
            "El florín es la moneda de oro de Florencia, con el lirio de un lado y san Juan del otro; un aprendiz gana unos pocos al año, y la capilla Tornabuoni costó más de mil."),
        ObjetoCuento("espejo", "el espejo", "un espejo", "El espejo",
            "El espejo del taller es de metal pulido, pequeño y un poco torcido, y sirve para mirar los cuadros al revés, que es como se ven los errores; también para pintarse a uno mismo, cuando no hay modelo; el maestro se pintó así, entre los personajes de la capilla."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en Florencia como se piensa en una madre: no se la elige, y se le perdona todo. Le dio un oficio, un maestro y una cúpula que mirar. A sus {edad} años no conoce otra ciudad, y no le hace falta: aquí está todo lo que se puede pintar."),
        v("Piensa a veces en los pintores que trabajaron antes que él en la ciudad, Giotto, Masaccio, los de las puertas del baptisterio, y en que todos empezaron moliendo colores como él. Lo piensa para darse ánimos. A veces funciona. A veces muele más fuerte."),
        v("{mundo} Él piensa en cosas más cercanas: en el ultramar, que es caro; en la mano que no le sale; en Masino, que le debe dinero. Las cosas grandes son de los Médici y de los frailes, que las piensan en sus palacios y en sus conventos."),
        v("{mundo} A sus {edad} años, {nombre} ha pintado mantos, cielos, piedras, árboles y un perro, pero ninguna cara. El maestro dice que las caras son lo último. {nombre} dibuja caras de noche, a escondidas, con la luz de la luna. Las caras de la gente del mercado. Algunas le salen."),
        v("Piensa en las paredes que pinta el taller, que durarán cientos de años, y en que en ellas hay manchas, pliegues y nubes suyas que nadie sabrá que son suyas. Le da rabia y le da paz. Algún día, piensa, pintará algo con su nombre."),
        v("{mundo} A veces, en la iglesia, delante de un fresco viejo, {nombre} se olvida de rezar y se pone a mirar cómo está hecho: dónde empieza cada día de trabajo, dónde se secó el yeso, dónde el pintor cambió de idea. Luego se acuerda de rezar. Reza deprisa."),
    )

    private val MUNDOS = mapOf(
        "capilla" to listOf(
            "En la ciudad se habla de la capilla Tornabuoni, donde el maestro pintó a media Florencia vestida de santos.",
            "Se habla de un fraile de Ferrara que predica en San Marcos contra el lujo y que cada vez tiene más público.",
            "Se dice que Lorenzo de Médici está enfermo de gota y que ya no sale casi.",
        ),
        "savonarola" to listOf(
            "Se habla de fray Girolamo, que dice que vendrá un castigo de Dios sobre Florencia por sus pecados.",
            "Se dice que el taller va a hacer mosaicos para la catedral, que es lo más difícil que hay.",
        ),
        "lorenzo" to listOf(
            "Se habla de Piero, el hijo de Lorenzo, que no se parece a su padre en nada.",
            "Se dice que en Roma los cardenales compran el papado como quien compra un caballo.",
        ),
        "colon" to listOf(
            "Se habla de los portugueses, que buscan las Indias por debajo de África, y de un genovés que las busca por el otro lado.",
            "Se dice que el rey de Francia quiere bajar a Italia a quedarse con Nápoles.",
        ),
        "franceses" to listOf(
            "Se habla del rey de Francia, que prepara un ejército para bajar a Italia, con cañones que nadie ha visto nunca.",
            "Se dice que fray Girolamo profetiza un castigo que vendrá de más allá de los montes.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde se acaba la luz en el taller, y los aprendices limpian: los pinceles en aceite, las losas con agua, el suelo con la escoba. El maestro tapa las tablas con paños. {nombre} sale a la calle, que todavía tiene luz, y respira algo que no es cola."),
        v("Anochece en Florencia, y las campanas tocan el avemaría una tras otra, desde la catedral hasta el último convento. Las tiendas cierran sus contraventanas. {nombre} cruza el puente hacia el Oltrarno, mirando cómo el río se pone de oro y luego negro."),
        v("Se cena en la mesa del taller, a la luz de una lámpara de aceite: sopa, pan, un poco de queso. El maestro habla de un encargo nuevo, y los aprendices escuchan sin dejar de comer. Luego, cada uno a su jergón. {nombre} dibuja un rato antes de dormir."),
        v("De noche, Florencia cierra sus puertas y apaga sus luces, y solo quedan los faroles de algunas esquinas y la ronda que pasa con sus antorchas. {nombre} mira la cúpula desde la ventana del taller, negra contra el cielo. Le parece que respira."),
        v("La última luz del día se queda un momento en lo alto de la cúpula de la catedral, en la linterna de mármol blanco, cuando la ciudad ya está a oscuras. {nombre} la ve desde la puerta del taller. Luego se va, y sale la primera estrella.",),
        v("Al anochecer, en verano, la ciudad entera sale a las calles a buscar el fresco: la gente se sienta en los bancos de piedra de los palacios, los muchachos cantan bajo las ventanas, las madres llaman a los niños. {nombre} se queda en la puerta del taller, mirando.", meses = setOf(6, 7, 8)),
        v("Al caer la tarde, en invierno, el taller se queda a oscuras enseguida y hace frío. Los aprendices se juntan alrededor del brasero con las manos extendidas. Masino cuenta una historia de fantasmas del Oltrarno. {nombre} se ríe. Luego no puede dormir.", meses = setOf(11, 12, 1, 2)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "carnaval" to unDia(
            Parrafo.MANANA to "Hoy es martes de carnaval, el último día antes de la cuaresma, y Florencia se vuelve loca: por las calles pasan carros con máscaras, música y canciones subidas de tono, que dicen que algunas las escribió el propio Lorenzo. El taller cierra a mediodía.",
            Parrafo.PUEBLO to "En la plaza de Santa Croce se juega a la pelota, a golpes, entre los jóvenes de los barrios, y la gente mira desde las tribunas y apuesta. Por las calles pasan carros con figuras de dioses antiguos, de virtudes y de diablos. Los niños tiran naranjas a los que pasan.",
            Parrafo.ANOCHECER to "Esta noche {nombre} y Masino van con una máscara de papel, que pintaron ellos, por las calles del Oltrarno, de taberna en taberna, cantando con desconocidos. A medianoche suenan las campanas: empieza la cuaresma. Vuelven al taller cantando bajito.",
        ),
        "cuaresma" to unDia(
            Parrafo.MANANA to "Hoy es miércoles de ceniza, y la ciudad amanece callada y con resaca. En la iglesia del barrio el cura pone ceniza en la frente de cada uno. Desde hoy no hay carne hasta Pascua: en la mesa del taller habrá arenques, habas y pan, cuarenta días.",
            Parrafo.TRABAJO to "A esta hora muele colores en silencio, que en cuaresma el maestro no quiere canciones en el taller. Masino tiene la cabeza en otra parte y muele mal. {nombre} le arregla el color sin decir nada. Hoy todos tienen algo de lo que arrepentirse.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la ceniza que tiene en la frente, que es como el carbón con que dibuja: polvo que deja una marca. Se mira en el espejo del taller. La cruz le salió torcida. Piensa que el cura no sabe dibujar.",
        ),
        "anunciacion" to unDia(
            Parrafo.MANANA to "Hoy es la Anunciación, y en Florencia empieza el año nuevo, como siempre, el día en que el ángel visitó a la Virgen. La gente va en procesión a la iglesia de la Santísima Anunciata, donde hay un fresco de la Anunciación que dicen que lo terminó un ángel mientras el pintor dormía.",
            Parrafo.TRABAJO to "A esta hora no trabaja, que es fiesta de guardar y año nuevo: el taller está cerrado. {nombre} va con Masino a la plaza de la Anunciata, a ver la feria y a mirar el fresco, que es lo que hacen los pintores en su día libre: mirar pintura.",
            Parrafo.PUEBLO to "En la plaza de la Anunciata hay feria: puestos de dulces, de figuras de cera, de velas, de cintas, y un gentío que entra y sale de la iglesia para ver el fresco milagroso. Los pintores van a mirarlo de cerca, para ver si de verdad lo pintó un ángel.",
            Parrafo.PENSAMIENTO to "{nombre} mira el fresco de la Anunciación y piensa que no lo pintó un ángel, que lo pintó un hombre, y que la cara de la Virgen no está tan bien. Luego le da vergüenza haberlo pensado en una iglesia. Pero sigue pensándolo.",
        ),
        "pascua" to unDia(
            Parrafo.MANANA to "Hoy es Pascua. Por la mañana, delante de la catedral, se enciende el carro: una torre de madera llena de cohetes y petardos, tirada por bueyes blancos con guirnaldas, a la que se lleva el fuego nuevo desde el altar. Cuando estalla, la plaza entera grita.",
            Parrafo.TRABAJO to "A esta hora no trabaja: es Pascua, y el taller está cerrado. {nombre} está en la plaza de la catedral con Masino, apretado entre la gente, esperando que estalle el carro, con la cara levantada y los oídos tapados.",
            Parrafo.PUEBLO to "Toda Florencia está en la plaza de la catedral para ver el carro, apretada, con ropa de fiesta. Cuando los petardos estallan y el humo sube por la fachada, los campesinos miran si el fuego sale bien, porque dicen que eso anuncia buena cosecha. Este año sale bien.",
            Parrafo.ANOCHECER to "Esta noche se cena carne por primera vez en cuarenta días: cordero asado con romero en la mesa del maestro, con pan blanco, huevos y vino bueno. Los aprendices comen como si no hubiera mañana. El maestro se ríe. {nombre} se lleva un hueso para roer en el jergón.",
        ),
        "calendimaggio" to unDia(
            Parrafo.MANANA to "Hoy es primero de mayo, y Florencia celebra la primavera: los muchachos cuelgan ramas en flor en las puertas de las casas de las muchachas que les gustan, y por la noche se canta bajo las ventanas. {nombre} y Masino se levantaron antes del amanecer a robar ramas de un huerto.",
            Parrafo.PUEBLO to "Por las calles hay ramas de espino y de cerezo en las puertas, guirnaldas en los balcones, y grupos de jóvenes vestidos de verde que cantan y bailan. Las muchachas miran desde las ventanas. Las madres miran a las muchachas. Los padres miran a los muchachos.",
            Parrafo.ANOCHECER to "Esta noche {nombre} canta con Masino y otros aprendices bajo una ventana del Oltrarno una canción de mayo que se sabe todo el mundo. Alguien les tira agua desde arriba. No saben si es una muchacha o su padre. Se van corriendo, mojados y felices.",
        ),
        "san_juan" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es San Juan, el patrón de Florencia, la fiesta más grande del año. Desde la mañana pasa la procesión con las reliquias, los frailes, los gremios con sus banderas y las ciudades sometidas, que traen al baptisterio velas enormes y telas como tributo. Las campanas no paran.",
                Parrafo.TRABAJO to "A esta hora no trabaja: es San Juan, y en Florencia no trabaja nadie. {nombre} está en la calle con Masino, viendo pasar la procesión, las banderas de los gremios y las velas enormes que traen las ciudades sometidas, más altas que un hombre.",
                Parrafo.PUEBLO to "La ciudad está adornada con telas en los balcones y alfombras en las ventanas, y en la plaza de la Señoría hay tribunas para los señores. Por la tarde se corre el palio, la carrera de caballos por las calles, y la gente se aprieta contra las paredes para verlos pasar.",
                Parrafo.ANOCHECER to "Esta noche hay hogueras en las plazas y luminarias en las torres, y la ciudad entera está en la calle hasta muy tarde. {nombre} y Masino suben a lo alto de un muro a ver los fuegos. Desde allí, Florencia arde de luces. Es la noche más bonita del año.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Sigue la fiesta de San Juan: en las plazas hay juegos, teatro y torneos, y en el río, barcas adornadas. El taller está cerrado.",
            )),
        ),
        "asuncion" to unDia(
            Parrafo.MANANA to "Hoy es la Asunción de la Virgen, a mitad de agosto, y Florencia está medio vacía: los ricos se han ido a sus villas del campo a huir del calor, y los que quedan van a misa temprano y luego buscan la sombra. El taller no trabaja.",
            Parrafo.TRABAJO to "A esta hora no trabaja: es fiesta. {nombre} va con Masino al río, a bañarse en un remanso debajo de los molinos, con otros muchachos del barrio, que se tiran desde las piedras. El agua está tibia y verde. Por un día, nadie muele colores.",
            Parrafo.PENSAMIENTO to "{nombre} flota boca arriba en el río y mira el cielo de agosto, que es de un azul que no está en ningún bote del taller. Piensa que si algún día consigue ese azul, será maestro. Masino le tira agua a la cara. El azul se va.",
        ),
        "san_lucas" to unDia(
            Parrafo.MANANA to "Hoy es San Lucas, el patrón de los pintores, porque dicen que pintó a la Virgen del natural. La cofradía de los pintores de Florencia dice misa en su capilla y luego come junta. Los maestros van con sus mejores ropas. Los aprendices, con las menos manchadas.",
            Parrafo.TRABAJO to "A esta hora no trabaja: es el día de los pintores, y el taller entero va a misa a la capilla de la cofradía, con el maestro delante y los aprendices detrás, peinados y lavados, que no se les reconoce.",
            Parrafo.GENTE to "En la comida de la cofradía se sientan juntos pintores que el resto del año se critican: los viejos que todavía doran fondos, los jóvenes que pintan paisajes de Flandes, los que trabajan para los Médici y los que no. Se bebe, se brinda, se discute. Al final, alguien canta.",
            Parrafo.PENSAMIENTO to "{nombre} mira a los maestros en la mesa grande, con sus barrigas y sus capas, y piensa que algún día estará sentado con ellos, no en la mesa de los aprendices. Le parece imposible y le parece seguro, según el vaso de vino que lleve.",
        ),
        "todos_santos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy es Todos los Santos, y mañana los difuntos. Las iglesias se llenan de velas, y la gente va a los cementerios de los conventos a rezar por sus muertos. {nombre} va con su madre a la iglesia de Santo Spirito, donde están enterrados sus abuelos.",
                Parrafo.TRABAJO to "A esta hora no trabaja, que es fiesta de guardar. Acompaña a su madre de iglesia en iglesia, encendiendo velas, y mira de reojo los frescos de cada una, uno por uno, como un ladrón.",
                Parrafo.PUEBLO to "En las calles se venden castañas asadas, el primer frío llega con niebla, y las campanas tocan a muerto toda la tarde. En las iglesias, los frescos de los santos se ven a la luz de las velas, y parece que se mueven. Los niños tienen miedo y van igual.",
                Parrafo.PENSAMIENTO to "{nombre} reza por sus abuelos, que no conoció, y por los pintores muertos cuyos frescos mira cada día. Piensa que los pintores no se mueren del todo, porque dejan las paredes. Los cardadores sí. Le da pena por su padre, y no sabe por qué.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Hoy es el día de los difuntos, y las campanas tocan a muerto desde la mañana. En las iglesias se dicen misas por todas las almas, y la ciudad está callada.",
            )),
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, y anoche hubo misa a medianoche en todas las iglesias de Florencia, con las calles llenas de gente con antorchas en el frío. En muchas iglesias han puesto el belén: figuras de madera y de barro, con el niño, la madre, los pastores y los animales.",
            Parrafo.TRABAJO to "A esta hora no trabaja: es Navidad. {nombre} va a ver los belenes de las iglesias con Masino, y los miran con ojos de pintor: este pastor está mal hecho, este buey tiene la cabeza grande. Luego se callan delante de uno que es tan bonito que no tiene nada que decir.",
            Parrafo.ANOCHECER to "Esta noche {nombre} cena en casa de sus padres, en el Oltrarno, con su hermana y unos tíos: capón, pan, castañas, dulces de miel y vino. Su padre bebe de más y le pregunta, otra vez, cuándo será maestro. Su madre le da un trozo de dulce para llevar al taller.",
        ),
        "ultramar" to unDia(
            Parrafo.MANANA to "Hoy el maestro le dio a {nombre}, por primera vez, la piedra azul de ultramar para que la muela: un trozo de lapislázuli del tamaño de una nuez, con vetas doradas, que vale más que su sueldo de un año. Se lo dio en la mano y le dijo: si se te cae, te vas.",
            Parrafo.TRABAJO to "A esta hora muele el lapislázuli en un mortero de bronce, despacio, y luego en la losa, hasta que es un polvo gris. Luego lo amasa con cera y resina, lo deja una noche, y mañana lo lavará en lejía para que suelte el azul. Hoy solo es polvo gris. Mañana será el cielo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el maestro confía en él, o que se le olvidó que tenía otro aprendiz. Prefiere creer lo primero. Mira el polvo gris en la losa y piensa en el manto de la Virgen que será, en Santa María Novella, durante cientos de años. Y nadie sabrá que lo molió él.",
        ),
        "savonarola_vuelve" to unDia(
            Parrafo.MANANA to "Hoy se habla en el taller de un fraile de Ferrara, fray Girolamo, que volvió al convento de San Marcos, y que predica en los claustros contra los ricos, contra el lujo y, dicen, contra los pintores que pintan a las mujeres de Florencia como si fueran santas. El maestro se ríe. Los oficiales, menos.",
            Parrafo.GENTE to "Esta tarde Masino lleva a {nombre} a oír al fraile en San Marcos, por curiosidad. El fraile es feo, flaco, con una nariz enorme, y habla con un acento raro y una voz que no le pega. Pero cuando habla del infierno, la gente llora. {nombre} sale con un nudo en el estómago.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en lo que dijo el fraile, que pintar a las mujeres de la ciudad como vírgenes es pecado, y piensa en la capilla Tornabuoni, llena de señoras de Florencia vestidas de santas. Piensa que el maestro irá al infierno. Luego piensa que él molió los colores.",
        ),
        "ropaje" to unDia(
            Parrafo.MANANA to "Hoy el maestro le dejó a {nombre}, por primera vez, pintar un ropaje en los frescos de la capilla Tornabuoni: el manto de una de las mujeres que miran el nacimiento de san Juan, al fondo, abajo, donde casi no se ve. Pero se ve. Y es suyo.",
            Parrafo.TRABAJO to "A esta hora está subido al andamio de la capilla, delante del enlucido fresco, con el cartón ya pasado y el color preparado. Pinta deprisa y con cuidado a la vez, del oscuro al claro, siguiendo los pliegues que dibujó el maestro. Le tiembla la mano. Luego no.",
            Parrafo.PENSAMIENTO to "{nombre} baja del andamio y mira el manto desde abajo, entre los otros, y le parece que se nota que es suyo, que está peor, más torpe. El maestro lo mira, dice «bien» y se va. {nombre} pasa la noche sin dormir, de alegría.",
        ),
        "caterina" to unDia(
            Parrafo.MANANA to "Hoy el maestro mandó a {nombre} a la tienda del vendedor de colores del Puente Viejo a comprar bol rojo y azurita, y lo despachó una muchacha con las manos manchadas de azul y una risa que se oía desde la calle: Caterina, la hija del dueño. Le dio de más. O de menos. No se fijó.",
            Parrafo.GENTE to "Esta tarde {nombre} vuelve a la tienda a comprar algo que no hace falta, y luego otra vez. Caterina se ríe de él. Su padre lo mira raro. Masino, que lo acompaña, dice que va a arruinar al taller comprando colores. {nombre} no le contesta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Caterina, en sus manos azules, en su risa. Piensa que le gustaría pintarla. Luego piensa que no sabe pintar caras. Esa noche dibuja una cara de memoria, a la luz de la luna. Le sale mal. La guarda igual.",
        ),
        "sueldo" to unDia(
            Parrafo.MANANA to "Hoy Davide, el hermano del maestro, le dio a {nombre} su primer sueldo, en una bolsita de cuero: tres florines por un año, que es lo que se le paga a un aprendiz que ya sabe hacer algo. Hasta ahora, solo le daban casa y comida. Tres florines. Los cuenta cuatro veces.",
            Parrafo.GENTE to "Esta tarde {nombre} va a casa de sus padres, en el Oltrarno, y le da un florín a su madre, que lo mira como si fuera una reliquia. Su padre dice que con eso no se vive. Luego, cuando cree que nadie lo ve, lo toca. {nombre} se queda a cenar.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en qué hará con los otros dos florines: un pincel de los buenos, papel para dibujar, una cinta para alguien. Piensa que el dinero es como el ultramar: se va enseguida y hay que pensar bien dónde se pone.",
        ),
        "capilla" to unDia(
            Parrafo.MANANA to "Hoy se descubrieron los frescos de la capilla Tornabuoni en Santa María Novella, después de cinco años de trabajo. Quitaron los andamios, barrieron, encendieron las velas, y los Tornabuoni vinieron con sus familias a mirarse en las paredes, vestidos de santos. Media Florencia vino detrás.",
            Parrafo.PUEBLO to "En la iglesia hay un gentío que mira hacia arriba, señalando: esa es la Tornabuoni, ese es Poliziano, ese es el maestro, que se pintó a sí mismo. Los frailes no saben dónde meter a tanta gente. Los del taller están en un rincón, sin que nadie los mire.",
            Parrafo.PENSAMIENTO to "{nombre} busca su manto en la pared, abajo, al fondo, y lo encuentra enseguida, aunque nadie más lo vería. Piensa que estará ahí cuando él sea viejo, y cuando ya no esté. Le dan ganas de decírselo a alguien. Se lo dice a Masino, que ya lo sabía.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "aprendiz", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + CAMPO_MANANA,
            Parrafo.TIEMPO to TIEMPO + CAMPO_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + CAMPO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + CAMPO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + listOf(
                v("Desde que empezó su primera tabla, una Virgen pequeña para la casa de un sedero, {nombre} piensa en ella todo el día, mientras muele los colores de los demás. Le pinta la cara a la Virgen y la borra, y la vuelve a pintar. Se parece a Caterina. No lo puede evitar.", desde = "tabla", hasta = "firma"),
                v("Su primera tabla está en casa del sedero, colgada en el cuarto de la esposa, con su nombre escrito por detrás, pequeño. {nombre} no la ha vuelto a ver. Sabe cada pincelada de memoria. Ahora sabe que puede, y eso es lo que más le asusta.", desde = "firma"),
                v("Desde que murió Lorenzo, Florencia es otra: más nerviosa, más triste, más dividida. Los Médici siguen mandando, pero Piero no es su padre. Fray Girolamo predica más fuerte. En el taller, los encargos bajan. {nombre} piensa que algo se acaba, y no sabe qué.", desde = "lorenzo"),
            ),
            Parrafo.ANOCHECER to ANOCHECER + CAMPO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
