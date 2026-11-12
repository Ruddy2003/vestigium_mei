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
 * El cuento de Citlali, macehual que cultiva una chinampa en México-Tenochtitlan hacia 1500. Sigue la gramática y el tono
 * de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - La ciudad está en el lago y casi todo se mueve en canoa. No hay bestias de carga, ni ruedas, ni metal de uso
 *   diario: hay obsidiana, barro, madera y piedra volcánica. El cacao y las mantas sirven de moneda.
 * - Las fiestas de las veintenas incluyen sacrificios. El cuento los nombra como los vería alguien de allí, con respeto
 *   y sin detalle, y no los juzga.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG1.kt`):
 * - En la temporada 1 el agua ahoga la chinampa el 28 de agosto. Hasta el 19 de octubre viven con unos parientes del
 *   barrio alto: el estado «fuera», con su mañana, su trabajo, su pueblo, su gente y su anochecer. Desde el 20 de
 *   octubre rehacen la chinampa, hasta el primer almácigo, el 2 de diciembre. Las variantes del trabajo que cultivan la chinampa llevan `hasta = "inundacion"` o `desde = "almacigo"`;
 *   las de todos los días (sacar lodo, clavar estacas, hacer almácigos, la canoa) valen también mientras se rehace.
 * - Las flores para vender empiezan en la temporada 4 (`flores`).
 * - Xóchitl nace en la temporada 2 (`nace_xochitl`).
 * - Papan deja de bordar cuando pierde la vista (`papan_ojos`, temporada 3).
 * - Mazatl recibe su manta de guerrero en la temporada 4 (`cautivo_mazatl`) y se casa en la 5 (`boda_mazatl`).
 * - Las fiestas que vuelven cada año no nombran a Xóchitl.
 */
internal object PiezasCitlali {
    /** El estado «fuera» del calendario: desde que el agua entra en la casa hasta que vuelven a rehacer la chinampa. */
    private const val FUERA = "fuera"

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se levanta a oscuras, antes que nadie, porque el maíz no se muele solo. Aviva el fuego de las tres piedras y pone a calentar el comal. Afuera, en el canal, se oye el agua que golpea despacio contra la orilla de la chinampa."),
        v("Lo primero que oye al despertar es el tambor del templo, que marca la hora desde lo alto, y luego los patos del canal. {nombre} se levanta del petate, se lava la cara con agua del jarro y se recoge el pelo como lo llevan las mujeres casadas."),
        v("La primera luz entra por la puerta de la casa, que es de carrizo y barro, con techo de paja. Desde ella se ve el canal, todavía oscuro, y al fondo los templos de la ciudad, blancos, con el humo de los braseros encima."),
        v("En casa se desayuna atole caliente, espeso, en un cajete de barro, de pie junto al fuego. Yaotl ya ha sacado la canoa y está achicando el agua de la noche. {nombre} le lleva su atole hasta la orilla, y él se lo bebe de un trago, sin soltar el remo."),
        v("{nombre} sale a la orilla de la chinampa cuando las garzas todavía duermen en los ahuejotes. Se echa agua del canal en los brazos y en la cara, que a esa hora está fría y huele a lodo. Por el canal ya pasan las primeras canoas, cargadas, hacia el centro de la ciudad."),
        v("Antes de empezar, {nombre} le echa un poco de copal al fuego para los dioses de la casa, que tienen su sitio junto a la pared, pequeños, de barro y pintados. El humo huele dulce. Afuera, el sol sale por detrás de los volcanes, que esta mañana se ven blancos."),
        v("Xocoyotl se despierta con ella y se queda sentado en el petate, mirando el fuego con los ojos medio cerrados. Quiere tortilla, siempre quiere tortilla. {nombre} le da la primera, que siempre sale fea, y el niño se la come como si fuera la mejor."),

        v("{nombre} despierta en el patio de los parientes del barrio alto, entre petates ajenos y gente que ronca. Lo primero que hace es mirar hacia su barrio, abajo: el agua sigue ahí, quieta, con el brillo de la mañana encima.", FUERA),
        v("La primera luz encuentra a {nombre} ya despierta, sentada contra la pared del patio con Xocoyotl dormido en las piernas. La casa de los parientes es pequeña, y ahora duermen en ella tres familias. Nadie se queja, porque nadie tiene adónde ir.", FUERA),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la ciudad del lago el tiempo es asunto de Tláloc, y a Tláloc se le respeta."),
        v("{nombre} mira el cielo pensando en el agua, como todos los que viven de la chinampa. {cielo} Si llueve de más se ahogan las matas, y si llueve de menos se secan, y las dos cosas pasan."),
        v("{de_estacion} {cielo} Las viejas del barrio dicen que antes el lago era más limpio y las lluvias más puntuales. Las jóvenes les dicen que sí, y no se lo creen."),
        v("{cielo} En tiempo de {estacion} el lago cambia de color, y la ciudad con él. {nombre} lo mira cada mañana desde la orilla, por costumbre, y cada mañana le parece un poco distinto."),
        v("Lo primero que hace al salir es mirar hacia los volcanes: si se ven, el día será claro. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los sacerdotes leen el cielo en sus libros pintados, y los chinamperos lo leen en el lago. A veces coinciden."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, y los dos volcanes se ven tan cerca que parece que se podrían tocar desde la canoa.",
            "Hace un día claro, de esos en que el lago brilla tanto que hay que entrecerrar los ojos.",
            "No hay una nube, y el sol de esta tierra alta quema aunque el aire sea fresco.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, y el lago tiene el color del jade viejo.",
            "Hay nubes bajas sobre los cerros, que esconden los volcanes.",
            "Está nublado, y las garzas vuelan bajo sobre los canales.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fuerte y corta, de las de la tarde, que llena las canoas de agua.",
            "Cae agua desde temprano, y los canales se llenan de hojas y de ranas.",
            "Llueve, y en la chinampa el lodo se pega a los pies como si quisiera quedarse con ellos.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los rayos caen sobre los cerros, y la gente dice que Tláloc está trabajando.",
            "El cielo se ha puesto negro sobre el lago, y con el primer trueno todas las canoas buscan la orilla.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento seco que levanta polvo en la orilla salada del lago.",
            "Hay viento, y el lago se pica en olas pequeñas que mecen las canoas.",
        ),
        Clima.NIEBLA to listOf(
            "Hay neblina sobre el lago, y las canoas aparecen de la nada a pocos pasos.",
            "Amaneció con neblina, y la ciudad parece flotar sobre una nube.",
        ),
        Clima.POLVO to listOf(
            "El viento trae polvo de las orillas secas del lago, y el sol se ve rojo.",
            "Hay polvo en el aire, y se mete en los ojos, en la masa y en la boca.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.LLUVIAS to listOf(
            "Es tiempo de lluvias, cuando cada tarde se nubla y el maíz crece a ojos vistas.",
            "En las lluvias el lago sube y los canales se llenan hasta el borde de las chinampas.",
            "Es tiempo de lluvias, y los sapos cantan toda la noche.",
        ),
        Estacion.SECAS to listOf(
            "Es tiempo de secas, cuando el cielo está limpio y las mañanas son frías.",
            "En las secas el lago baja, y en las orillas aparece la tierra blanca del salitre.",
            "Es tiempo de secas, y la chinampa se riega a mano, con agua del canal.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora saca lodo del fondo del canal con una red de mango largo y lo echa sobre la chinampa. El lodo es negro, huele a lago y es lo que hace que aquí todo crezca. Es el trabajo más pesado del día, y el que más se nota.", hacer = Hacer.TRABAJO),
        v("A esta hora clava estacas en la orilla de la chinampa y teje entre ellas una cerca de carrizo, para que el lodo no se escape al canal. Junto a las estacas crecen los ahuejotes, cuyas raíces sujetan la tierra mejor que cualquier cerca.", hacer = Hacer.TRABAJO),
        v("A esta hora hace almácigos: corta el lodo en cuadritos con un cuchillo de obsidiana, le hace a cada uno un agujero con el dedo y pone dentro una semilla. Cuando las plantitas crezcan se trasplantan con su cuadrito, como quien cambia a un niño de cuna.", hacer = Hacer.TRABAJO),
        v("A esta hora achica el agua de la canoa con un guaje partido y tapa con resina una grieta del fondo. La canoa es de un solo tronco, y sin ella no hay manera de ir a ninguna parte. Se cuida como se cuida a un animal de la casa.", hacer = Hacer.TRABAJO),
        v("A esta hora recoge chiles y tomates entre las matas, agachada, y llena el chiquihuite antes de que caliente el sol. Su chinampa es de las buenas del barrio, y lo sabe todo el que pasa en canoa.", hacer = Hacer.TRABAJO, hasta = "inundacion"),
        v("A esta hora escarda el maíz de la chinampa, que ya le llega a la cintura, y le arrima tierra al pie con la coa. Entre las matas crecen el frijol y la calabaza, que se ayudan entre sí como buenos vecinos.", hacer = Hacer.TRABAJO, hasta = "inundacion", meses = setOf(5, 6, 7, 8)),
        v("A esta hora pasa las plantitas del almácigo a la chinampa, cada una con su cuadrito de lodo. Las pone en hileras, a un palmo una de otra, y las riega con agua del canal. Dentro de unos meses, si Tláloc quiere, será maíz.", hacer = Hacer.TRABAJO, desde = "almacigo", meses = setOf(3, 4, 5)),
        v("A esta hora escarda el maíz, que crece alto y verde, y le arrima tierra al pie con la coa. Entre las matas trepan el frijol y la calabaza. {nombre} arranca la hierba mala y deja los quelites, que también se comen.", hacer = Hacer.TRABAJO, desde = "almacigo", meses = setOf(6, 7, 8, 9)),
        v("A esta hora cosecha lo que da la chinampa: chiles, tomates, calabacitas, quelites. Lo va echando en el chiquihuite, lo mejor para vender y lo demás para la casa. La chinampa da tanto que a veces no da tiempo de recogerlo todo.", hacer = Hacer.TRABAJO, desde = "almacigo"),
        v("A esta hora corta flores en la orilla de la chinampa, cempasúchil y otras, y las ata en ramos para el mercado. Las flores no se comen, pero se venden mejor que muchas cosas que sí. En la ciudad siempre hay alguna fiesta, y las fiestas piden flores.", hacer = Hacer.TRABAJO, desde = "flores"),

        v("A esta hora muele el nixtamal en el metate, de rodillas, empujando la mano de piedra adelante y atrás hasta que el maíz se vuelve masa. Es un ruido que se oye en todas las casas del barrio a la misma hora, como si la ciudad entera respirara.", hacer = Hacer.CUIDAR),
        v("A esta hora tortea: hace bolitas de masa, las palmea entre las manos hasta dejarlas redondas y finas, y las pone en el comal caliente. Cuando se inflan, están. Hay que hacer muchas, porque en casa se comen todas.", hacer = Hacer.CUIDAR),
        v("A esta hora barre la casa y el patio con una escoba de varas, como se barre todo en la ciudad. Después va a la fuente por agua dulce, la que llega por el acueducto desde el cerro, con el jarro en la cabeza y Xocoyotl agarrado de la falda.", hacer = Hacer.CUIDAR),

        v("A esta hora come tortillas con frijoles y chile, sentada en el petate junto al fuego, con Xocoyotl pegado a ella. Hay también un poco de pescado blanco del lago, envuelto en hojas de maíz y cocido en el comal.", hacer = Hacer.COMER),
        v("A esta hora come en la chinampa, a la sombra de un ahuejote: unas tortillas que trajo dobladas en una tela, chile y unos acociles tostados. Bebe agua del guaje. El lago está quieto, y a esta hora hasta los patos descansan.", hacer = Hacer.COMER),
        v("A esta hora come lo que hay, que hoy son tamales de frijol y un atole de chía. En casa casi nunca se come carne: algún pato, un guajolote en fiesta, insectos del lago. Nadie lo echa de menos, porque nadie se ha acostumbrado a otra cosa.", hacer = Hacer.COMER),

        v("A esta hora va en canoa al mercado de Tlatelolco, por los canales, con la carga de la chinampa a los pies. Yaotl rema y ella cuida que no se moje nada. Por el camino se cruzan con otras canoas, y cada una lleva algo distinto: leña, piedra, flores, gente.", hacer = Hacer.CAMINO),
        v("A esta hora está en el mercado de Tlatelolco, que es tan grande que no se le ve el final. Cada cosa tiene su calle: el chile, las plumas, las mantas, la obsidiana. Los jueces del mercado vigilan que nadie engañe, y a veces lo consiguen.", hacer = Hacer.CAMINO),
        v("A esta hora vuelve del mercado en canoa con lo que cambió: una olla nueva, un poco de sal, unas mantas pequeñas que sirven de moneda, cacao. El cacao también es moneda, y hay quien lo falsifica rellenando la cáscara con tierra. {nombre} muerde cada grano antes de aceptarlo.", hacer = Hacer.CAMINO),

        v("A esta hora está en el patio con la familia, mientras se hace de noche, y Papan cuenta historias de cuando la ciudad era la mitad. Los niños se duermen en su regazo. Desde el templo del barrio llegan los tambores de la casa del canto, donde los jóvenes aprenden a bailar.", hacer = Hacer.GENTE),
        v("A esta hora canta con las vecinas mientras hilan algodón con el malacate, sentadas en el suelo. Son cantos viejos, que hablan de flores y de lo poco que dura todo. Los cantan riéndose, como si no dijeran lo que dicen.", hacer = Hacer.GENTE),
        v("A esta hora habla con Yaotl en la orilla de la chinampa, con los pies en el agua, de lo que se vendió y de lo que se va a sembrar. No hablan mucho. Se oye el lago, se oyen los sapos y algún remo lejano, y con eso basta.", hacer = Hacer.GENTE),

        v("A esta hora duerme en el petate, con la familia alrededor y una manta de algodón encima. La casa huele a humo y a maíz. Lo último que se oye son los sapos del canal, que no se cansan nunca.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y la ciudad entera duerme con ella, menos los sacerdotes, que velan en los templos y tocan las caracolas a medianoche. {nombre} las oye entre sueños y se da la vuelta.", hacer = Hacer.DORMIR),
        v("A esta hora duerme con Xocoyotl pegado a la espalda y los pies hacia el fuego, que ya es brasa. Afuera pasa una canoa sin hacer ruido, y luego no pasa nada.", hacer = Hacer.DORMIR),

        v("A esta hora va en canoa hasta su chinampa a ver cuánto ha bajado el agua y a salvar lo que se pueda: una estaca, una olla que flota, el techo de paja, que se fue al canal. Vuelve con poco, y con barro hasta las rodillas.", FUERA, Hacer.TRABAJO),
        v("A esta hora ayuda en el dique del barrio con otras mujeres, pasando cestas de piedra y lodo de mano en mano para cerrar una brecha. Nadie la ha llamado: va porque el agua no pregunta de quién es la chinampa que se lleva.", FUERA, Hacer.TRABAJO),
        v("A esta hora muele en el metate de la prima de Yaotl, por turnos, porque en la casa hay un solo metate y muchas bocas. Las mujeres se reparten el trabajo sin hablarlo, y las tortillas salen igual de buenas aunque ninguna esté en su casa.", FUERA, Hacer.CUIDAR),
        v("A esta hora come con los parientes lo que hay, que es poco y se reparte entre muchos: tortillas, un poco de chile, quelites del patio. Nadie se sirve dos veces. Los niños comen primero, y los viejos dicen que ellos no tienen hambre.", FUERA, Hacer.COMER),
        v("A esta hora va en canoa al mercado, que funciona a medias, a cambiar lo que salvó por maíz. Por el camino pasa sobre calles que ahora son canales y junto a casas que ahora son islas. La gente rema callada, mirando el agua.", FUERA, Hacer.CAMINO),
        v("A esta hora está en el patio con los parientes, hablando de lo mismo que ayer: cuánto bajó el agua, quién perdió qué, cuándo se podrá volver. Papan cuenta otras inundaciones de cuando era niña. Todas se acabaron, dice, y esta también se acabará.", FUERA, Hacer.GENTE),
        v("A esta hora duerme en el patio de los parientes, en un petate prestado, con Xocoyotl abrazado y Yaotl a la espalda. El suelo es duro y ajeno. Cada vez que se despierta oye el agua, y tarda en acordarse de dónde está.", FUERA, Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("La ciudad está en medio del lago, sobre islas hechas por la gente, y se llega a ella por calzadas largas o en canoa. Las calles son mitad tierra y mitad agua, y por las de agua pasan más cosas. {entorno}"),
        v("{entorno} En el centro de la ciudad está el recinto sagrado, con el templo mayor y sus dos capillas en lo alto: una para Huitzilopochtli y otra para Tláloc. Desde la chinampa se ve la cima, y el humo que sale siempre de ella."),
        v("El barrio de {nombre} es un calpulli, un barrio de familias que se conocen desde siempre, con su templo, su escuela y sus jefes. Las tierras son del barrio, y a cada familia le toca la suya mientras la trabaje. {entorno}"),
        v("Las chinampas son islas largas de lodo y raíces, separadas por canales, y cada una es un huerto que no descansa nunca. De lejos parecen un tablero verde con rayas de agua, y las garzas lo recorren como si fuera suyo. {entorno}"),
        v("{entorno} En el lago hay agua salada y agua dulce, separadas por un gran dique de piedra y estacas que mandó hacer un rey antiguo. La chinampa necesita la dulce, y por eso el dique se cuida como un templo."),
        v("En la ciudad todo se lleva a cuestas o en canoa, porque no hay bestias de carga ni ruedas. Los cargadores van por las calzadas con la carga sujeta a la frente con una correa, y en el lago las canoas van tan llenas que parecen islas que se mueven. {entorno}"),

        v("Desde el barrio alto se ve la ciudad como nunca: medio bajo el agua, con los templos asomando y las canoas yendo por encima de lo que eran calles y huertos. En algún sitio de esa agua está su chinampa. {entorno}", FUERA),
    )

    /** Lo que se ve alrededor según la chinampa (ahogada en la temporada 1), el maíz y el lago. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("chinampa", 0.9, frases = listOf(
            "La chinampa está bajo el agua, y de ella solo asoman las copas de los ahuejotes.",
            "Donde estaba la chinampa hay agua quieta, con hojas y ramas flotando.",
        )),
        Entorno("chinampa", 0.3, 0.9, frases = listOf(
            "La chinampa todavía está a medio rehacer, con el lodo fresco y las estacas nuevas.",
            "De la chinampa vieja quedan las raíces de los ahuejotes, y encima, lodo nuevo que todavía no da nada.",
        )),
        Entorno("milpa", 0.7, frases = listOf(
            "El maíz de la chinampa está alto, con los jilotes asomando.",
            "Las milpas de las chinampas forman una pared verde a los lados del canal.",
        )),
        Entorno("milpa", 0.1, 0.7, frases = listOf(
            "En la chinampa el maíz crece verde, todavía bajo.",
            "El maíz nuevo asoma en hileras, entre el frijol y la calabaza.",
        )),
        Entorno("lago", 0.7, frases = listOf(
            "El lago está alto, y el agua llega hasta el borde de las chinampas.",
            "Con las lluvias, los canales van llenos, y las canoas pasan casi a la altura de las casas.",
        )),
        Entorno("lago", 0.0, 0.7, frases = listOf(
            "El lago está bajo, y en las orillas aparece la costra blanca del salitre.",
            "Los canales van más bajos que de costumbre, y las canoas rozan el fondo.",
        )),
    )

    private val GENTE = listOf(
        v("Yaotl, su marido, trabaja la chinampa con ella y cumple su turno en las obras de la ciudad cuando lo llaman, como todos los hombres del barrio. Habla poco y rema bien. Cuando se enoja, rema más fuerte, y {nombre} sabe que está enojado por la espuma."),
        v("Xocoyotl tiene la edad de querer remar y no poder con el remo. Se pasa el día en la orilla de la chinampa persiguiendo ranas y ajolotes, y vuelve lleno de lodo hasta las orejas. {nombre} lo baña en el canal, y él protesta como si lo estuvieran ahogando."),
        v("Papan, su suegra, vive con ellos y duerme junto al fuego. Sabe los nombres de los días y lo que trae cada uno, y le dice a todo el mundo cuándo conviene hacer las cosas. Cuando {nombre} no le hace caso, Papan le cuenta lo que le pasó a una que tampoco le hizo caso."),
        v("Su hermano Mazatl vive en el barrio de al lado y se ha criado en la casa de los jóvenes, donde se aprende a pelear, a trabajar y a cantar. Viene a comer cuando tiene hambre, que es siempre. Habla de la guerra como de una fiesta, y {nombre} prefiere no oírlo."),
        v("Las de la chinampa de al lado son tres hermanas que hablan sin parar y se pelean sin parar, de una chinampa a otra, por encima del canal. {nombre} las oye todo el día. Cuando se callan, se preocupa."),
        v("Papan borda huipiles con hilos de colores, sentada en la puerta, y en el mercado los vende mejor que nadie. Tiene los dedos torcidos de tanto bordar y, dice ella, la vista de un halcón. Le enseña a {nombre} sus puntadas, y {nombre} las aprende mal.", hasta = "papan_ojos"),
        v("Papan ya no ve bien y ha dejado de bordar. Se sienta en la puerta igual, al sol, y les dice a las muchachas del barrio qué color va con cuál. Las muchachas le hacen caso, porque Papan, aunque no vea, sigue teniendo razón.", desde = "papan_ojos"),
        v("Xóchitl, la pequeña, va a la espalda de {nombre} en un rebozo, o duerme en una cuna de carrizo colgada del techo. Cuando llora, Papan le canta una canción de cuna que habla de flores. La niña se calla a la segunda flor.", desde = "nace_xochitl"),
        v("Mazatl ya tiene su manta de guerrero, la que le dieron por su primer cautivo, y la lleva aunque haga calor. Viene a comer menos, porque ahora tiene más amigos. Cuando viene, Xocoyotl no se le despega.", desde = "cautivo_mazatl"),
        v("Mazatl se casó y vive con su mujer en su barrio. Vienen juntos los días de fiesta: ella es callada y lista, y en la primera visita ya le había corregido a Papan una receta. Papan todavía no ha decidido si eso le gusta.", desde = "boda_mazatl"),

        v("Los parientes del barrio alto son primos de Yaotl, y los reciben como se recibe a la familia en una desgracia: sin preguntar hasta cuándo. La prima de Yaotl tiene tres hijos y la lengua rápida, y ya se ha hecho amiga de Papan, que es lo más difícil.", FUERA),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de la casa: sin haberlas aprendido nunca."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En el barrio se dice que una casa se conoce por su metate; lo dicen las que tienen uno bueno."),
        v("{dato_objeto} Son cosas que se aprenden mirando a las mayores, como casi todo en el barrio."),
        v("Una macehual tiene pocas cosas, y cada una sirve para más de una cosa. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("metate", "el metate", "un metate", "El metate",
            "El metate es una piedra volcánica de tres patas, inclinada, sobre la que se muele el maíz con otra piedra larga, la mano; el suyo se lo dio su madre al casarse, y está gastado en el centro como una cuna."),
        ObjetoCuento("comal", "el comal", "un comal", "El comal",
            "El comal es un disco de barro que se pone sobre las tres piedras del fuego; en él se cuecen las tortillas, se tuestan los chiles y se calientan las manos en las mañanas de secas."),
        ObjetoCuento("coa", "la coa", "una coa", "La coa",
            "La coa es un palo largo con la punta ancha y endurecida al fuego; con ella se abre la tierra, se siembra y se arrima tierra a las matas, y no hay chinampero en la ciudad que no tenga una."),
        ObjetoCuento("canoa", "la canoa", "una canoa", "La canoa",
            "La canoa es de un solo tronco, ahuecado a fuego y a hacha de piedra; lleva la carga, a la familia y, en las lluvias, también el agua que se le mete."),
        ObjetoCuento("remo", "el remo", "un remo", "El remo",
            "El remo es de madera, largo y de pala ancha; se rema de pie, en la popa, y a quien rema bien se le reconoce porque la canoa no se va de lado."),
        ObjetoCuento("huipil", "el huipil", "un huipil", "El huipil",
            "El huipil es una camisa larga, sin mangas, de algodón, bordada en el cuello; el de diario es liso, y el de fiesta lleva colores, y se guarda en una petaca para que no le dé el humo."),
        ObjetoCuento("cesta", "el chiquihuite", "un chiquihuite", "El chiquihuite",
            "El chiquihuite es una cesta de carrizo, sin asas, que se lleva en la cabeza o en la canoa; sirve para los chiles, para las tortillas y para las flores, y cuando se rompe, se hace otro en una tarde."),
        ObjetoCuento("flores", "las flores", "unas flores", "Las flores",
            "Las flores son la riqueza más rara de la ciudad: no se comen y todos las quieren; se llevan a los templos, se les regalan a los señores y se nombran en los cantos, donde todo lo bueno es una flor."),
        ObjetoCuento("jarro", "el jarro", "un jarro", "El jarro",
            "El jarro es de barro rojo pulido, con un asa; en él se guarda el agua dulce, tapada con un trapo para que no le entren mosquitos, y sabe a barro fresco."),
        ObjetoCuento("malacate", "el malacate", "un malacate", "El malacate",
            "El malacate es una rueda pequeña de barro que se pone en el huso, con dibujos de flores; con él se hila el algodón, haciéndolo girar dentro de un cajete para que no se caiga, y el suyo era de su abuela."),
        ObjetoCuento("petate", "el petate", "un petate", "El petate",
            "El petate es una estera de tule tejido; sobre él se duerme, se come y se sienta uno, y cuando alguien muere, se le envuelve en él."),
        ObjetoCuento("tortilla", "la tortilla", "una tortilla", "La tortilla",
            "La tortilla es de maíz cocido con cal, molido y torteado, y se come con todo: es plato, cuchara y comida a la vez; un día sin tortilla, dicen las viejas, es un día que no cuenta."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el lago como otros piensan en los dioses: con respeto y con miedo. El lago da la chinampa, los peces y el agua, y a veces se lo lleva todo. A sus {edad} años ya lo ha visto dar y quitar."),
        v("Piensa a veces en lo que hay más allá de los volcanes, de donde vienen los comerciantes con plumas, cacao y conchas. Dicen que hay tierras calientes donde el cacao crece en los árboles, y un mar que no se acaba. Le gustaría verlo, y le da miedo."),
        v("{mundo} Ella piensa en cosas más cercanas: en el chile que no se vendió, en la tos de Xocoyotl, en si alcanzará el maíz hasta la cosecha. Las cosas grandes, dicen las viejas, son para los señores, que tienen quien les muela."),
        v("{mundo} A sus {edad} años, {nombre} ha perdido a un hijo que no llegó a caminar. Lo enterraron en el patio, junto a la casa, envuelto en su petate. Cuando barre ese rincón, barre más despacio."),
        v("No tiene tierra suya: la chinampa es del barrio, y su familia la tiene mientras la trabaje. Tiene su metate, sus hijos y su canoa, y hay días en que eso le parece mucho. Su madre tuvo lo mismo, y cantaba."),
        v("{mundo} Por las noches, cuando cantan en la casa del canto, piensa en las palabras de los cantos viejos, que dicen que todo aquí dura poco, como una flor. A {nombre} le parece verdad, y no le parece triste: las flores, piensa, también se cortan para la fiesta."),
    )

    private val MUNDOS = mapOf(
        "inundacion" to listOf(
            "En el barrio se habla de los manantiales nuevos que el tlatoani quiere traer a la ciudad.",
            "Se dice que el agua del lago anda inquieta este año.",
            "Hay quien dice que el lago guarda rencor, y quien dice que el lago no guarda nada.",
        ),
        "reconstruccion" to listOf(
            "Se habla de los templos nuevos, que serán más grandes que los que tumbó el agua.",
            "Dicen que el tlatoani quiere una ciudad tan hermosa que dé envidia a los dioses.",
        ),
        "luto" to listOf(
            "Se habla del tlatoani muerto y del nuevo, que dicen que es serio y sabio.",
            "En el barrio se dice que con el nuevo tlatoani todo será más ordenado, y más caro.",
        ),
        "guerra" to listOf(
            "Se habla de guerras lejanas, de cautivos y de tributos nuevos.",
            "Dicen que los guerreros vuelven cargados, y que no todos vuelven.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando oscurece, la familia se junta alrededor del fuego y se cenan las tortillas que quedaron, con chile y frijoles. No hay más luz que la del fuego. Papan cuenta algo, los niños se duermen, y {nombre} tapa las brasas con ceniza para que amanezcan vivas."),
        v("Al caer la tarde vuelven las canoas del mercado, una detrás de otra, por los canales que se ponen dorados. {nombre} las ve pasar desde la orilla, sentada con los pies en el agua. Los remeros se saludan de canoa a canoa, cansados."),
        v("Anochece sobre el lago, y en lo alto de los templos se encienden los braseros, que se ven desde todas las chinampas como estrellas bajas. {nombre} se queda un rato mirándolos desde la puerta. Luego entra, porque con la noche llega el frío."),
        v("De noche la ciudad se queda quieta, y solo se oyen los sapos, el agua y, a ratos, una caracola desde el templo. {nombre} se acuesta en el petate con el cuerpo molido. Lo último que ve es el resplandor del fuego en el techo de paja."),
        v("Se cena en silencio, porque la boca está ocupada. Después {nombre} cuenta lo que se dijo en el mercado y Yaotl lo que se dijo en el canal, y casi siempre es lo mismo. Luego se tapa el fuego, y la casa se queda a oscuras, con el lago respirando afuera."),
        v("La última luz se queda un rato en la cima del volcán, rosada, cuando la ciudad ya está a oscuras. {nombre} la mira desde la orilla. Dicen que en los montes viven los ayudantes de Tláloc, y a esa hora le parece que también la miran a ella."),

        v("Al caer la tarde las familias del patio encienden un solo fuego, para ahorrar leña, y se sientan todas alrededor. Se cena junto, se habla bajo y se mira hacia abajo, hacia el agua. {nombre} se duerme sentada, apoyada en Yaotl.", FUERA),
        v("De noche el barrio alto está lleno de gente que no es de ahí, y se oyen voces de otros barrios en todos los patios. {nombre} se acuesta en el petate prestado y escucha el agua, abajo, que no se ha ido.", FUERA),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "nemontemi" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan los nemontemi, los cinco días que sobran al final del año y que no son de nadie. Son días baldíos, de mala suerte: no se empieza nada, no se discute, y casi no se sale de casa. Hasta el fuego se cuida de otra manera.",
                Parrafo.PUEBLO to "Estos días la ciudad está callada, como si contuviera el aliento. Los templos no hacen fiestas, los mercados están casi vacíos y por los canales pasa poca canoa. Quien nace en estos días, dicen, nace sin suerte, y quien tropieza se queda tropezando todo el año.",
                Parrafo.PENSAMIENTO to "{nombre} se pasa el día en casa, haciendo lo menos posible, que es más difícil que trabajar. Papan dice que hay que tener cuidado hasta con lo que se piensa. {nombre} intenta no pensar en nada, y se pasa el día pensando en que no tiene que pensar.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días baldíos el barrio entero anda despacio, hablando bajo. Los niños, que no entienden de suertes, juegan igual, y las madres los riñen en voz baja.",
                "Mientras duran los nemontemi se cocina poco: se come lo que se tenía guardado, frío. La masa espera, el comal espera, y la chinampa también.",
                "En los días baldíos se cuentan las desgracias del barrio: el que se cayó al canal, la que perdió la canoa, el que se rio de los dioses. Todas pasaron en días como estos, dicen, y nadie lo pone en duda.",
            )),
        ),
        "tlacaxipehualiztli" to unDia(
            Parrafo.MANANA to "Hoy termina la veintena de Tlacaxipehualiztli, la fiesta de Xipe Tótec, el dios de la primavera y de la piel nueva. Durante estos días los sacerdotes han hecho los sacrificios en el templo, y algunos van por las calles vestidos con la piel de los sacrificados, que es lo que el dios pide.",
            Parrafo.PUEBLO to "En el barrio la gente sale a ver pasar a los que visten a Xipe, y los niños se esconden detrás de sus madres. Dicen que la piel vieja se quita para que salga la nueva, como la hoja que envuelve a la mazorca. Los mayores lo entienden; los niños, no del todo.",
            Parrafo.PENSAMIENTO to "{nombre} mira la procesión con la cabeza baja, como se mira lo que es de los dioses. Piensa que el mundo se gasta y hay que pagarle para que siga, y que ella paga con maíz y con flores, que es lo que tiene. Los dioses, dicen, piden más. Ella no pregunta cuánto.",
        ),
        "siembra" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siembra del maíz en la chinampa. Las plantitas del almácigo ya tienen cuatro hojas, y hay que pasarlas a la tierra grande, cada una con su cuadrito de lodo. Trabaja toda la familia: unos cortan, otros cargan, otros plantan.",
                Parrafo.PUEBLO to "En todas las chinampas del barrio se ve gente agachada, plantando. Entre el maíz se siembran frijol y calabaza, que crecen juntos y se ayudan: el maíz da el palo, el frijol trepa por él y la calabaza cubre el suelo para que no se seque. Así se ha hecho siempre.",
                Parrafo.PENSAMIENTO to "{nombre} planta y piensa que cada mata de maíz es una tortilla del invierno, y cada tortilla, un día. Hace la cuenta y le salen muchos días, y se tranquiliza. Luego piensa en el agua, que no se puede contar, y se le pasa la tranquilidad.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días la chinampa se llena de hileras verdes, derechas como las de un tejido. Las garzas pasan a mirarlas, y los niños las espantan, por si acaso.",
                "Mientras dura la siembra se riega cada tarde con agua del canal, a cubetadas, hasta que la tierra brilla. Duelen más los brazos que la espalda, y la espalda duele mucho.",
                "En el barrio todos siembran a la vez, y por los canales van canoas llenas de plantitas de un lado a otro: unos dan, otros piden, otros cambian. Las chinampas se ayudan como las familias.",
            )),
        ),
        "toxcatl" to unDia(
            Parrafo.MANANA to "Hoy termina la veintena de Tóxcatl, la fiesta de Tezcatlipoca, el señor del espejo humeante. Todo el año un joven muy hermoso ha vivido como el dios, vestido de flores y tocando la flauta por las calles, y hoy sube los escalones del templo rompiendo una flauta en cada uno.",
            Parrafo.PUEBLO to "En el recinto sagrado hay danzas y cantos desde temprano, y la gente lleva collares de maíz tostado, que se reventó en el comal como flores blancas. Es fiesta de secas, de esperar la lluvia. Hasta los que no van al recinto se ponen algo blanco.",
            Parrafo.ANOCHECER to "Esta noche {nombre} piensa en el joven de la flauta, que vio pasar por su calle tantas veces este año. Era tan hermoso que las mujeres salían a la puerta a verlo. Ahora es del dios, y ya hay otro, elegido entre los cautivos, que empieza su año de flores.",
        ),
        "etzalcualiztli" to unDia(
            Parrafo.MANANA to "Hoy es Etzalcualiztli, la fiesta de Tláloc, el dios de la lluvia, y de su mujer, la del agua. En todas las casas se cuece etzalli, maíz con frijol, espeso, y se come hasta hartarse, porque así lo quiere Tláloc. Es la fiesta de la gente del agua: pescadores, chinamperos y remeros.",
            Parrafo.PUEBLO to "Los sacerdotes de Tláloc bajan al lago a cortar juncos y van por los canales cantando, mojados hasta la cintura. Dicen que hoy pueden castigar a quien se les cruce mal, y la gente se aparta. Los chinamperos les dejan ofrendas en la orilla, y miran al cielo.",
            Parrafo.GENTE to "En casa {nombre} cuece una olla enorme de etzalli, y los vecinos pasan con sus cajetes a probarlo, y ella pasa con el suyo a probar el de ellos. Es fiesta de compartir: cada casa da y cada casa recibe. Xocoyotl come de todas las ollas y no se acuerda de cuál le gustó más.",
        ),
        "huey_tecuilhuitl" to unDia(
            Parrafo.MANANA to "Hoy termina la veintena de Huey Tecuílhuitl, la gran fiesta de los señores. Los señores dan de comer al pueblo durante días: tamales, atole, chía, y la gente hace cola desde el alba. Es tiempo de hambre, cuando el maíz viejo se acaba y el nuevo todavía no está.",
            Parrafo.PUEBLO to "Es también la fiesta de Xilonen, la diosa del maíz tierno, el jilote que empieza a asomar. Las mujeres se sueltan el pelo y bailan con mazorcas en las manos, y hasta que termina la fiesta nadie come elotes. Los niños los miran crecer en las milpas con impaciencia.",
            Parrafo.PENSAMIENTO to "{nombre} come los tamales de los señores y piensa que es raro: el resto del año los señores reciben de los macehuales, y hoy los macehuales reciben de ellos. Le parece como el agua del lago, que va y viene. Luego se come otro tamal, por si el agua no vuelve.",
        ),
        "xocotl_huetzi" to unDia(
            Parrafo.MANANA to "Hoy termina la veintena de Xócotl Huetzi, la gran fiesta de los muertos. En el patio del templo han levantado un tronco altísimo, pelado, con una figura de masa en la punta, y los jóvenes de los barrios se lanzan a trepar por él para alcanzarla.",
            Parrafo.PUEBLO to "Los que trepan se empujan, se agarran de los pies y se caen unos sobre otros, y la gente grita desde abajo. El que llega arriba y tira la figura se lleva la gloria del año. Después el tronco se derriba, y con él se acaba la fiesta: por eso se llama así, cae el tronco.",
            Parrafo.GENTE to "Mazatl trepa con los de su barrio y llega más arriba que nunca antes de resbalar. Baja con las manos en carne viva, diciendo que el tronco estaba untado. {nombre} le cura las manos con un ungüento de la vieja de la esquina, y él se queja menos del ungüento que del tronco.",
        ),
        "ochpaniztli" to unDia(
            Parrafo.MANANA to "Hoy termina la veintena de Ochpaniztli, la del barrido. Se barren los caminos, las casas, los templos y los patios, todo a la vez, como si la ciudad entera se lavara la cara. Es la fiesta de Toci, la abuela de los dioses, y de las parteras y las curanderas.",
            Parrafo.PUEBLO to "Por las calles pasan las parteras y las curanderas en procesión, tirándose flores y peleándose de mentiras, riéndose. Los guerreros reciben armas y mantas nuevas. Después viene la cosecha, y la ciudad quiere estar limpia para recibirla.",
            Parrafo.PENSAMIENTO to "{nombre} barre la casa con la escoba nueva, la de varas verdes, y piensa que lo que se barre hoy se lleva lo malo del año. Barre los rincones con más cuidado que nunca. Luego se queda mirando el montón de basura, por si reconoce algo.",
        ),
        "cosecha_ahogada" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy, cuando otros años empezaba la cosecha, {nombre} sale a la chinampa a ver qué se salvó del agua. Casi nada: unas matas de maíz tumbadas y negras, unas calabazas podridas, el frijol perdido. Lo poco que queda en pie lo corta con cuidado, como quien recoge lo que deja un incendio.",
                Parrafo.PUEBLO to "En todo el barrio pasa lo mismo: las chinampas bajas se ahogaron enteras y las altas dieron la mitad. La gente se presta maíz, se cuenta lo que perdió y se consuela diciendo que a otros les fue peor. De los graneros del templo ha salido maíz para los barrios más castigados.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que este invierno será largo, y que habrá que cambiar en el mercado cosas que no quería cambiar. Piensa también que el agua se llevó la cosecha pero no a nadie de su casa, y que eso hay que agradecérselo a alguien. No sabe muy bien a quién, y les da las gracias a todos.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días el barrio entero huele a lodo podrido y a humo: se queman las matas muertas para limpiar las chinampas. Las garzas buscan entre los restos lo que no se llevó el agua.",
                "En el mercado el maíz está caro, y el chile también, y las flores no las compra nadie. La gente cambia mantas, cerámica y lo que puede por un poco de grano para el invierno.",
                "En el barrio se dice que el tlatoani mandará rehacerlo todo, la ciudad y los diques, más grande y mejor. La gente lo cree, porque lo necesita, y porque este tlatoani suele cumplir.",
            )),
        ),
        "panquetzaliztli" to unDia(
            Parrafo.MANANA to "Hoy termina la veintena de Panquetzaliztli, el levantamiento de banderas, la fiesta grande de Huitzilopochtli, el dios de la ciudad. En las casas, en los árboles y en las chinampas se ponen banderitas de papel de amate, y la ciudad entera ondea.",
            Parrafo.PUEBLO to "En el templo mayor se celebra el nacimiento del dios, que nació ya armado para defender a su madre. Hay danzas toda la noche, carreras del templo a los barrios y una figura enorme del dios hecha de masa de amaranto y miel, que luego se parte y se come.",
            Parrafo.GENTE to "{nombre} pone banderitas de papel en la puerta y en la orilla de la chinampa, y Xocoyotl se pone una en la cabeza, atada con un hilo. Cuando llega a la casa el trozo de la figura del dios, Papan lo reparte en pedacitos, uno para cada uno, con mucha seriedad.",
        ),
        "atemoztli" to unDia(
            Parrafo.MANANA to "Hoy termina la veintena de Atemoztli, la del descenso del agua. Cae en plena secas, para pedir que la lluvia baje a tiempo. En las casas se hacen figuritas de los cerros con masa de amaranto, porque en los cerros viven los ayudantes de Tláloc.",
            Parrafo.PUEBLO to "Toda la noche se vela a los cerritos de masa, con cantos y flautas, y al amanecer se les abre el pecho, como a los cautivos, y se reparten entre la familia. Así se les da de comer a los dioses de la lluvia, y así se les pide.",
            Parrafo.ANOCHECER to "Esta noche {nombre} vela con la familia los cerritos de masa que hizo con Papan, y canta los cantos de Tláloc, bajito, para no despertar a los niños. Afuera el cielo está limpio y frío. Faltan muchas lunas para la lluvia, y ya se le está pidiendo.",
        ),
        "ahuejote" to unDia(
            Parrafo.MANANA to "Hoy {nombre} y Yaotl plantan un ahuejote nuevo en la esquina de la chinampa, donde el viejo se secó. Es apenas una vara clavada en el lodo, más alta que Xocoyotl y más flaca que él. Yaotl dice que en diez años dará sombra, y Xocoyotl pregunta si él también.",
            Parrafo.TRABAJO to "A esta hora clavan la vara de ahuejote en la orilla, muy honda, para que el agua no se la lleve, y la atan a una estaca. Las raíces del ahuejote son las que sujetan la chinampa: sin ellas, el lodo se iría al canal con la primera lluvia fuerte.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su chinampa la hicieron los abuelos de Yaotl, con lodo, estacas y árboles como este, y que algún día la trabajarán sus nietos. Le parece bien estar en medio, plantando. El ahuejote, flaco y derecho, parece estar de acuerdo.",
        ),
        "xoco_rema" to unDia(
            Parrafo.MANANA to "Hoy Xocoyotl remó por primera vez la canoa, él solo, de una punta de la chinampa a la otra. El remo era el doble de largo que él, y la canoa fue hacia todos lados menos hacia adelante. Pero llegó, y al llegar se quedó de pie en la popa, como un señor.",
            Parrafo.GENTE to "Yaotl lo siguió por la orilla todo el tiempo, sin decir nada, por si se caía. Papan lo vio desde la puerta y dijo que el niño había nacido en un buen día para el agua. {nombre} lo bañó después, porque al final sí se cayó, pero ya en la orilla.",
            Parrafo.ANOCHECER to "Esta noche Xocoyotl le cuenta a todo el que pasa que remó solo, y le enseña las manos, que tienen dos ampollas. {nombre} le pone agua fría en las ampollas. El niño se duerme abrazado a su remo de juguete, y seguro que sueña que va a Tlatelolco.",
        ),
        "chiles" to unDia(
            Parrafo.MANANA to "Hoy {nombre} lleva a Tlatelolco su primera carga de chiles del año, los primeros que dio la chinampa. Los escogió uno por uno, los más rojos y brillantes, y los acomodó en el chiquihuite como si fueran plumas.",
            Parrafo.PUEBLO to "En el mercado se sienta en la calle de los chiles, entre otras vendedoras que la miran de reojo. Vende la mitad en un rato y cambia la otra mitad por sal, una olla y un puñado de granos de cacao, que muerde uno por uno, por si alguno está relleno de tierra.",
            Parrafo.PENSAMIENTO to "{nombre} vuelve en canoa con la olla nueva entre los pies y piensa que la chinampa le paga bien a quien la trabaja. Piensa también en lo que le dijo una vendedora vieja: que los años buenos se pagan después. Mira el lago, quieto, y decide no creerle.",
        ),
        "inundacion" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy, de madrugada, el agua empezó a subir sin que lloviera más que otros días. Sube por los canales, pasa por encima de las orillas y entra en las chinampas, en los patios, en las casas. Dicen que viene del manantial nuevo que el tlatoani mandó traer a la ciudad, que trae más agua de la que cabe.",
                Parrafo.PUEBLO to "Al mediodía el agua cubre las chinampas bajas del barrio, y solo asoman las copas de los ahuejotes y los techos. La gente sube a las canoas lo que puede: el metate, las ollas, el maíz guardado, los viejos. Los guajolotes nadan, y los perros también.",
                Parrafo.GENTE to "Yaotl carga a Papan hasta la canoa, y {nombre} sube a Xocoyotl, el metate y el maíz, en ese orden. Se van a casa de unos parientes del barrio alto, donde el agua no llega. Desde la canoa {nombre} mira su chinampa desaparecer bajo el agua, y no dice nada.",
                Parrafo.ANOCHECER to "Esta noche duermen todos en el patio de los parientes, apretados, con la ropa mojada. Nadie duerme mucho. Se oye el agua por todas partes y, a lo lejos, los gritos de los que todavía buscan a alguien. {nombre} tiene a Xocoyotl abrazado toda la noche.",
            )),
            sigue = mapOf(
                Parrafo.PUEBLO to listOf(
                    "Estos días el agua no baja. La ciudad entera anda en canoa por encima de las calles, y los guerreros y los cargadores trabajan en los diques y en las calzadas, con lodo hasta el pecho, para que no se rompan.",
                    "En el barrio alto la gente se aprieta en los patios con los que llegaron del barrio bajo. Se comparte el maíz, el agua de beber y el sitio para dormir. Nadie pregunta quién debe qué: ya se preguntará después.",
                    "Se dice que el tlatoani mismo tuvo que salir corriendo de su palacio, y que se golpeó la cabeza en una puerta. Se dice también que los sacerdotes le arrojan ofrendas al manantial para que se calme. Se dicen muchas cosas.",
                ),
                Parrafo.ANOCHECER to listOf(
                    "Esta noche tampoco se duerme bien. El agua sigue ahí, oscura, quieta, con cosas que flotan. {nombre} se levanta varias veces a mirar hacia su barrio, y no ve más que agua y la luz de algún brasero.",
                    "Al caer la tarde el agua parece bajar un poco, o eso dicen los que quieren creerlo. {nombre} hace una marca en un poste del patio para saberlo mañana. Mañana la marca sigue igual.",
                    "De noche, en el patio de los parientes, los viejos cuentan otras inundaciones de otros tiempos, que también se acabaron. Lo cuentan para consolar, y consuela un poco. Los niños se duermen oyéndolo.",
                ),
            ),
        ),
        "rehacen" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy el agua ya bajó lo bastante, y {nombre} y Yaotl vuelven a la chinampa a empezar de nuevo. Lo que encuentran es un lodazal con las estacas torcidas y la cerca de carrizo deshecha. Los ahuejotes, por lo menos, siguen en pie, y la casa, aunque sin techo, también.",
                Parrafo.PUEBLO to "Rehacer una chinampa es hacerla otra vez: clavar estacas nuevas, tejer la cerca, echar capas de lodo del fondo y de hierbas del lago, y apisonarlas hasta que aguanten. En todo el barrio se hace lo mismo, al mismo tiempo, y por los canales no pasa una canoa vacía.",
                Parrafo.PENSAMIENTO to "{nombre} saca lodo del fondo y piensa que este mismo lodo es el que se llevó su cosecha, y que ahora es el que se la va a devolver. El lago es así: quita y da con la misma mano. Ella no sabe si eso la consuela o la enoja, y sigue sacando.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días las chinampas del barrio se levantan otra vez, capa por capa, como un pan que se vuelve a amasar. Los vecinos se prestan canoas, redes y brazos, y por la tarde se sientan juntos a comer lo poco que hay.",
                "Los hombres del barrio van también a los diques y a las calzadas, por turnos, a rehacer lo que rompió el agua. Yaotl vuelve cada tarde con lodo hasta el pelo y sin ganas de hablar.",
                "En la ciudad se ven cuadrillas por todas partes: se reparan los templos, se rellenan las calles, se limpian los canales. Dicen que el tlatoani quiere una ciudad más alta y más hermosa que la que se llevó el agua.",
            )),
        ),
        "almacigo" to unDia(
            Parrafo.MANANA to "Hoy {nombre} siembra el primer almácigo en la chinampa nueva. La tierra todavía está blanda y huele a fondo de lago, pero ya es tierra. Corta el lodo en cuadritos, le hace a cada uno un agujero con el dedo y pone dentro una semilla de maíz de las que guardó.",
            Parrafo.TRABAJO to "A esta hora riega el almácigo con cuidado, con un guaje, para que el agua no se lleve las semillas. Son pocas, porque el maíz guardado es poco. Cada una que nazca será una mata, y cada mata, unas cuantas mazorcas. {nombre} las cuenta dos veces.",
            Parrafo.GENTE to "Papan viene a ver el almácigo apoyada en Xocoyotl, que la lleva del brazo muy serio. Dice que la chinampa nueva es mejor que la vieja, porque el lodo nuevo es más fuerte. {nombre} no sabe si es verdad. Pero le gusta oírlo, y le da las gracias.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "tenochtitlan", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
