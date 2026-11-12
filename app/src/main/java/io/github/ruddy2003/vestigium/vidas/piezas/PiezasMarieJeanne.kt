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
 * El cuento de Marie-Jeanne, lavandera en un barco-lavadero del Sena, en París, de 1789 a 1793. Sigue la gramática y
 * el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día empieza en la cola del pan, antes del alba; lava y entrega en dos tramos y come dos veces (esos textos valen
 *   a cualquiera de sus horas), y por la noche está en la plaza o en la taberna.
 * - En enero de la temporada 1 el Sena está helado (estado «helada»): no hay trabajo, y cada hora tiene sus variantes.
 *   Los dos días de Versalles (estado «marcha») los cuenta entero su día especial.
 * - El pueblo es el barco y el barrio, y lee el arco «pan» (lo que cuesta el pan).
 * - Las fiestas de la Iglesia tienen su párrafo de trabajo: esos días el barco está cerrado.
 * - No repite lo que ya dice «Su vida»: de rodillas con las manos en el agua, que casi todo se va en pan, la marcha de
 *   octubre, que no sabe leer.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG5.kt`):
 * - Jacques entra en la guardia en `jacques_guardia` (temporada 1), se va a la frontera en `jacques_frontera` (4) y
 *   vuelve con una pierna mala en `jacques_vuelve` (5).
 * - Paul ayuda en el barco desde `paul_barco` (temporada 1) y lee desde `paul_lee` (5).
 * - Antoine, el impresor, llega al barrio en `antoine` (temporada 2).
 * - Las fiestas que vuelven cada año no nombran a Jacques (que falta de julio de 1792 a junio de 1793) ni a Antoine.
 */
internal object PiezasMarieJeanne {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta en el cuarto del quinto piso, bajo el tejado, antes del alba, con el ruido de los carros de los hortelanos que bajan a los mercados. Se viste a oscuras para no despertar a Paul. Se pone el delantal de lona, el chal y los zuecos. Baja."),
        v("A {nombre} la despiertan las campanas de Saint-Gervais y los gallos de los patios, que en París hay muchos. Se lava la cara en la palangana con el agua del cántaro. Afuera, la calle huele a pan, a humo y a lo que se tiró por las ventanas."),
        v("{nombre} baja los cinco pisos de la escalera a oscuras, tanteando la pared, que suda humedad, y sale a la calle, que ya está viva: los panaderos, los aguadores, las lecheras con sus cántaros, los traperos con su farol. París no duerme nunca del todo."),
        v("Lo primero que hace {nombre} al salir es mirar el cielo por encima de los tejados, para saber si hoy secará la ropa. Las lavanderas de París viven mirando el cielo como los campesinos. Hoy hay nubes. Habrá que tender dentro, en el secadero del barco."),
        v("{nombre} se despierta con las manos doloridas, como todas las mañanas: hinchadas, rojas, con los nudillos agrietados de la lejía y del agua fría. Las abre y las cierra diez veces debajo de la manta, como le enseñó la vieja Thérèse. Luego se levanta."),
        v("A {nombre} la despierta Paul, que tuvo un sueño malo y se le mete en la cama, frío como un pez. Lo abraza un rato. Ya es hora. Le da un beso en la cabeza, lo deja dormido, se pone el chal y sale a la calle, donde ya se oye el pregón del primer aguador."),
        v("{nombre} sale a la calle cuando apenas clarea, y ya hace calor. En verano París huele fuerte: el río, las cloacas, los mataderos, el estiércol de los caballos. Las ventanas están abiertas. Desde una, alguien canta una canción contra la reina.", meses = setOf(6, 7, 8)),
    )

    /** El Sena helado, en enero de la temporada 1 (estado «helada»): no hay ropa que lavar. */
    private fun he(texto: String, hacer: Hacer? = null) = v(texto, estado = "helada", hacer = hacer)

    private val HELADA_MANANA = listOf(
        he("{nombre} se despierta con el frío metido en los huesos, en el cuarto del quinto piso, donde el agua del cántaro amaneció helada otra vez. Paul duerme pegado a ella. Jacques ya salió a buscar leña. No hay prisa por levantarse: el río no trabaja."),
        he("A {nombre} la despierta el silencio: sin el ruido de las paletas, sin los gritos de los barqueros, sin el agua. El Sena lleva semanas helado de orilla a orilla, y París entero está callado y blanco. Se queda un rato en la cama, para no gastar el calor."),
        he("{nombre} se levanta a oscuras y rasca con la uña la escarcha de dentro de la ventana para ver la calle: nieve sucia, un carro atascado, gente envuelta en mantas haciendo cola delante de la panadería. Hoy tampoco hay ropa que lavar. Hay que buscar pan."),
    )

    private val HELADA_TRABAJO = listOf(
        he("A esta hora hace la cola del pan en la nieve, envuelta en el chal y en una manta, con los zuecos rellenos de paja. Hace tanto frío que la gente no habla. La panadera abre tarde y el pan se acaba pronto. Hoy le toca. Mañana, no se sabe.", Hacer.CUIDAR),
        he("A esta hora no lava, porque los barcos-lavaderos están presos en el hielo: busca otra cosa. Hoy carga leña para un panadero de la calle Saint-Denis por un trozo de pan y unas monedas. Se le parten las manos. Otras lavanderas hacen lo mismo, o piden.", Hacer.TRABAJO),
        he("A esta hora va con las otras lavanderas a la orilla a mirar el barco, atrapado en el hielo, con las tablas crujiendo. El maestro Gaspard dice que si el hielo aprieta mucho, lo parte. Rompen el hielo alrededor con barras de hierro, a turnos. Es el único trabajo que hay.", Hacer.TRABAJO),
        he("A esta hora remienda ropa en el cuarto, junto a un brasero con cuatro brasas, para una señora del barrio que paga poco y tarde. Le tiemblan las manos y la aguja no entra. Paul le enhebra el hilo. Mientras cose, no piensa en el hambre. Casi.", Hacer.TRABAJO),
        he("A esta hora come la sopa que dan en la parroquia a los pobres del barrio, en una cola larga de gente que no se mira a la cara: agua caliente con pan y un poco de grasa. Lleva un cuenco para Paul. No le da vergüenza. Le dio el primer día.", Hacer.COMER),
        he("A esta hora comen los tres en el cuarto un pedazo de pan duro mojado en agua caliente con sal, y una cebolla. Jacques le da a Paul su parte de cebolla. Nadie dice nada. Afuera nieva otra vez.", Hacer.COMER),
        he("A esta hora duerme con la ropa puesta y todas las mantas encima, los tres en la misma cama, apretados, Paul en el medio. El frío entra por las tejas. Se oye toser al vecino. {nombre} se despierta a cada rato para tocarle la frente a Paul.", Hacer.DORMIR),
        he("A esta hora está en la taberna de abajo, la única con fuego, donde la gente del barrio se junta a calentarse con un vaso de vino que dura toda la noche. Se habla del frío, del pan, del rey, de los Estados Generales que vienen. {nombre} se sienta cerca de la estufa.", Hacer.GENTE),
    )

    private val HELADA_PUEBLO = listOf(
        he("El Sena está helado de orilla a orilla, blanco y quieto, y la gente cruza a pie por encima, entre los barcos atrapados. Los molinos del río están parados. Los aguadores rompen agujeros en el hielo para sacar agua. Los viejos dicen que nunca vieron un invierno así."),
        he("Los barcos-lavaderos están presos en el hielo junto al muelle, con los techos cubiertos de nieve, vacíos. Las lavanderas no tienen trabajo. Ni los barqueros, ni los descargadores del puerto. Medio barrio vive del río, y el río no se mueve."),
        he("En las esquinas del barrio hay hogueras que encienden los vecinos con lo que encuentran, y la gente se arrima a ellas con las manos extendidas. La leña cuesta como la carne. Se dice que en los cuartos de arriba se mueren viejos de frío, y nadie se entera hasta que huelen."),
    )

    private val HELADA_ANOCHECER = listOf(
        he("Anochece temprano y el frío se pone peor. {nombre} sube los cinco pisos con un poco de leña que encontró, y en el cuarto encienden el brasero un rato, solo un rato, para que dure. Se acuestan temprano. Dormir es la mejor manera de no tener hambre."),
        he("Al caer la tarde las calles se vacían y el barrio se queda a oscuras: no hay aceite para los faroles. Solo brilla el río helado, blanco bajo la luna. {nombre} lo mira desde la ventana y piensa en el barco, atrapado allá abajo. El hielo cruje como un viejo."),
        he("De noche se oye crujir el hielo del río desde todo el barrio, un ruido largo, como el de una puerta enorme que no termina de abrirse. Paul pregunta qué es. {nombre} le dice que es el Sena, que se queja del frío. Paul dice que él también."),
    )

    private val HELADA_TIEMPO = listOf(
        he("{cielo} Hace un frío que nadie recuerda: dicen que en las tabernas se hiela el vino en las jarras."),
        he("{cielo} {de_estacion} El Sena helado sigue sin moverse, y los viejos cuentan los días."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} Para una lavandera el tiempo es dinero: con sol, la ropa se seca; con lluvia, se queda mojada y huele."),
        v("{nombre} mira el cielo desde el barco, con las manos en el agua. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} La vieja Thérèse dice que le duelen las rodillas cuando va a llover, y nunca se equivoca."),
        v("{cielo} En {estacion} el río cambia: más alto, más bajo, más turbio, más frío. {nombre} lo sabe con las manos antes que con los ojos."),
        v("Sobre los tejados de la otra orilla se ve el cielo de París, que cambia deprisa. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En el barco se dice que el Sena tiene el humor de París: tranquilo un día, desbordado al siguiente."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio sobre los tejados de París, y la ropa tendida en el barco se seca en un par de horas.",
            "Hace un día claro, con el sol sobre el río, y las torres de Notre-Dame se ven doradas.",
            "No hay nubes, y la luz rebota en el agua y deslumbra a las lavanderas.",
        ),
        Clima.NUBES to listOf(
            "El cielo está gris y bajo, como un trapo mojado, y la ropa no acaba de secarse.",
            "Hay nubes que pasan rápido sobre el río, y el sol sale y se esconde.",
            "Está nublado, y París es de color de piedra.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve sobre el Sena, y la ropa tendida vuelve a mojarse, y hay que meterla corriendo.",
            "Cae una lluvia fina que llena las calles de barro y los zuecos de agua.",
            "Llueve, y el río sube, y el barco tira de sus amarras.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos retumban entre las casas y el río se pone negro.",
            "Cae un chaparrón con granizo, y las lavanderas se refugian bajo el techo del barco.",
        ),
        Clima.NIEVE to listOf(
            "Nieva sobre París, y la nieve se pone negra en cuanto toca la calle.",
            "Cae nieve, y el agua del río está tan fría que corta las manos.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla sobre el río, los puentes desaparecen y los barcos tocan la campana para no chocar.",
            "La niebla sube del Sena y se mete en las calles, y huele a agua y a humo.",
        ),
        Clima.VIENTO to listOf(
            "Sopla viento del oeste, y la ropa tendida en el barco restalla como banderas.",
            "Hace viento, las sábanas se vuelan de las cuerdas y hay que correr detrás.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando París lava las sábanas del invierno y el río viene crecido.",
            "En primavera los días se alargan y en el barco se trabaja hasta tarde.",
            "Es el tiempo de las lilas en los jardines de los ricos y de los charcos en las calles de los pobres.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, el buen tiempo de las lavanderas: el agua no muerde y los días son largos.",
            "En verano el Sena baja, huele fuerte y se llena de bañistas.",
            "Es el tiempo del calor, de las moscas y del pan caro, antes de la cosecha.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando el río se pone frío otra vez y las manos empiezan a doler.",
            "En otoño llegan a París el vino nuevo y las primeras nieblas.",
            "Es el tiempo de las castañas asadas en las esquinas y de los días que se acortan.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando el agua del río muerde y la ropa se hiela en las cuerdas.",
            "En invierno se lava a la luz de un farol y se vuelve a casa a oscuras.",
            "Es el tiempo de los sabañones, de la leña cara y de la sopa aguada.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora lava de rodillas en el borde del barco, en su puesto, con la tabla inclinada hacia el agua: moja la ropa en el río, la frota con jabón negro, la golpea con la paleta, la vuelve a mojar. Golpe tras golpe, sábana tras sábana. Las paletas de todo el barco suenan como un tambor.", hacer = Hacer.TRABAJO),
        v("A esta hora hace la colada en el caldero del barco: la ropa blanca va en capas en una tina, se cubre con un saco de ceniza y se le echa agua hirviendo por encima, una y otra vez, durante horas. La ceniza blanquea. El vapor le quema la cara.", hacer = Hacer.TRABAJO),
        v("A esta hora aclara en el río la ropa que salió de la tina, pieza por pieza, en el agua fría que corre, hasta que no queda ni rastro de ceniza ni de jabón. Luego la retuerce con la vieja Thérèse, cada una de una punta, hasta que no sale ni una gota.", hacer = Hacer.TRABAJO),
        v("A esta hora tiende la ropa en las cuerdas del piso de arriba del barco, el secadero, con las pinzas en la boca. Sábanas, camisas, enaguas, manteles de los ricos del Marais. Cuando se mueven todas con el viento, el barco parece que va a salir navegando.", hacer = Hacer.TRABAJO),
        v("A esta hora reparte la ropa limpia por las casas, doblada en una cesta grande que lleva a la cadera, por las calles y las escaleras de servicio. Cada pieza lleva una marca de hilo de color, para no confundirla. Una señora cuenta las servilletas delante de ella. Faltan dos. No faltan.", hacer = Hacer.TRABAJO),
        v("A esta hora recoge la ropa sucia de sus clientes, que la esperan en un fardo en la portería, y la apunta en un papel con rayas, porque no sabe escribir: una raya por camisa, un círculo por sábana. Las criadas le dan el fardo sin mirarla. La portera le da los buenos días.", hacer = Hacer.TRABAJO),

        v("A esta hora come en el barco, sentada en el banco de la lavandería con las otras: pan, un trozo de queso o una cebolla, y un trago de vino aguado de la jarra común. Las manos le huelen a lejía y el pan sabe a lejía. Da igual. Se come rápido.", hacer = Hacer.COMER),
        v("A esta hora come sopa de pan, puerros y agua, con una cuchara de madera, en un cuenco que lleva atado en el pañuelo. Es casi todo lo que come: pan en sopa, pan solo, pan con algo. Cuando el pan sube, se come menos de todo.", hacer = Hacer.COMER),
        v("A esta hora come lo que compra en un puesto del muelle: un arenque ahumado, un pedazo de pan, unas cerezas cuando es tiempo. La vendedora le fía, porque la conoce de siempre. {nombre} le paga cuando cobra de sus clientes.", hacer = Hacer.COMER),

        v("A esta hora hace la cola del pan delante de la panadería de la calle, antes de que abra, con otras mujeres envueltas en chales, en silencio o discutiendo. Cuando el pan sube, la cola empieza antes y es más larga. Hay que llegar pronto: el pan del día no alcanza para todos.", hacer = Hacer.CUIDAR),
        v("A esta hora espera en la cola del pan, con los zuecos en el barro y los brazos cruzados, y oye lo que se dice: que el panadero mezcla la harina con yeso, que los ricos esconden el trigo, que en el mercado hubo una pelea. Cuando se abre la puerta, todas empujan.", hacer = Hacer.CUIDAR),
        v("A esta hora, en la cola del pan, con la primera luz, cuenta las monedas que lleva en el pañuelo, una y otra vez, para estar segura. Le alcanza para una hogaza de cuatro libras, si no ha subido. La panadera la pesa delante de ella. Si falta, añade un trozo.", hacer = Hacer.CUIDAR),

        v("A esta hora duerme en la cama grande del cuarto, que es casi todo lo que tienen, con Paul y con quien esté. Por la ventana del tejado entra el ruido de la calle, que en París no se calla nunca: carros, borrachos, el sereno, un perro. Duerme igual.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con agua: con el río, con la ropa que se le escapa con la corriente, con su madre lavando en un arroyo de su pueblo de Normandía, cuando ella era pequeña. Se despierta un momento con las manos doloridas. Se las mete debajo de la almohada.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, como duermen los que trabajan con el cuerpo. El cuarto huele a jabón y a humo. Paul habla en sueños. En el piso de abajo, el zapatero todavía golpea suelas a la luz de una vela. Ella no oye nada.", hacer = Hacer.DORMIR),

        v("A esta hora está en la plaza del barrio con los vecinos, donde alguien se sube a un banco y lee en voz alta un periódico o un cartel pegado en la pared. Los demás escuchan, aplauden o gritan. {nombre} no sabe leer, pero sabe escuchar. Lo que oye aquí lo repite mañana en el barco.", hacer = Hacer.GENTE),
        v("A esta hora está en la taberna de la esquina con otras lavanderas, con un vaso de vino de fuera de la muralla, que es más barato. Se habla de los precios, de los hombres, de los curas, de la reina. Se canta. Alguna se pelea. Thérèse cuenta chistes que no se pueden repetir.", hacer = Hacer.GENTE),
        v("A esta hora sube a casa de una vecina a coser y a hablar, como hacen muchas noches las mujeres de la escalera, a la luz de una sola vela para todas. Se remiendan camisas y se remiendan vidas. Se sabe todo de todos. Paul se duerme en las rodillas de alguna.", hacer = Hacer.GENTE),
        v("A esta hora va con los vecinos a la asamblea del barrio, que se reúne en una iglesia, y se sienta con las mujeres al fondo. Los hombres hablan, votan, se gritan. Las mujeres también gritan, desde el fondo. {nombre} no lo entiende todo. Entiende cuando se habla del pan.", hacer = Hacer.GENTE, desde = "bastilla"),
    )

    private val PUEBLO = listOf(
        v("{entorno} El barco-lavadero es una casa de madera de dos pisos que flota en el Sena, amarrada al muelle cerca del puente de Notre-Dame: abajo, la lavandería, con los puestos a lo largo de los bordes; arriba, el secadero, con la ropa al aire. Cada lavandera paga su sitio."),
        v("En el barco lavan unas cuarenta mujeres, cada una en su puesto, de rodillas en un cajón de madera con paja, frente al agua. Golpean, frotan, cantan, discuten, ríen. Desde el muelle, el barco suena como un gallinero con tambores. {entorno}"),
        v("{entorno} El barrio de {nombre} es un enredo de calles estrechas y oscuras entre la plaza de Grève y el río, con casas de seis pisos donde abajo viven los tenderos y arriba, bajo el tejado, los pobres. Cuanto más alto, más barato y más frío."),
        v("Por el Sena bajan barcazas cargadas de leña, de trigo, de vino, de heno, hacia el puerto de la Grève. Los descargadores las vacían a hombros. Los aguadores llenan sus cubos. Los niños pescan. Las lavanderas lavan. El río es de todos y huele a todos. {entorno}"),
        v("{entorno} Desde el barco-lavadero se ve la otra orilla, las torres de Notre-Dame y, río abajo, el Louvre, enorme, con sus mil ventanas. {nombre} nunca ha entrado en ninguna de esas casas. Ha lavado sus sábanas."),
        v("En la calle de {nombre} se vive en la calle: los zapateros trabajan en la puerta, las vecinas pelan verduras en la escalera, los niños juegan en el arroyo del medio, que lleva de todo. Por encima, de ventana a ventana, cuerdas con ropa. Ropa que no lavó ella. {entorno}"),
    )

    /** Lo que cuesta el pan (arco «pan»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("pan", 0.0, 0.5, frases = listOf(
            "El pan está a su precio, y la hogaza llega hasta el domingo.",
            "Este mes el pan se puede pagar, y en la cola no se pelea nadie.",
        )),
        Entorno("pan", 0.5, 0.8, frases = listOf(
            "El pan está caro y se come menos, y en la cola se oyen insultos contra los panaderos.",
            "El pan sube, y lo que gana en un día casi se le va en una hogaza.",
        )),
        Entorno("pan", 0.8, 1.0, frases = listOf(
            "El pan nunca había costado tanto, y en las colas hay peleas, empujones y desmayos.",
            "El pan está tan caro que medio barrio vive de sopa, y se habla de colgar a alguien.",
        )),
    )

    private val GENTE = listOf(
        v("La vieja Thérèse lleva cuarenta años lavando en el barco, en el puesto de al lado, y le enseñó todo: a golpear sin romper, a doblar sin arrugar, a cobrar sin que se note. Tiene las manos como raíces y la lengua como una navaja. No le tiene miedo a nada, ni a Dios ni al maestro Gaspard."),
        v("El maestro Gaspard es el dueño del barco: cobra a cada lavandera por su sitio, por el agua caliente y por la ceniza, y vigila desde su cuarto de la popa con una pipa en la boca. No es malo. Es dueño. Para {nombre} es casi lo mismo."),
        v("Las lavanderas del barco son una familia que se pelea: cuarenta mujeres de todas las edades, viudas, casadas, solteras, madres, que se prestan el jabón, se roban los clientes y se defienden juntas de cualquiera que se meta con una. {nombre} es de las que más cantan."),
        v("La panadera de la esquina, madame Leblanc, la conoce desde hace años y le guarda la hogaza cuando llega tarde, si puede. Cuando el pan sube, le pide perdón con los ojos, como si fuera culpa suya. Dice que ella también paga más cara la harina. {nombre} le cree a medias."),
        v("La vecina del cuarto de al lado, la viuda Morin, cuida de Paul cuando hace falta, por unas monedas y por compañía. Vende flores de papel en el puente y sabe todos los chismes del barrio. Dice que de joven conoció a un duque. Nadie le cree."),
        v("Jacques, su compañero, es aguador: sube agua del Sena en dos cubos colgados de un palo por las escaleras de media calle, a tanto el cubo. Tiene los hombros como piedras y la risa fácil. No están casados por la iglesia. Ella dice que da igual. Él dice que un día.", hasta = "jacques_guardia"),
        v("Desde que cayó la Bastilla, Jacques está en la guardia del barrio: hace guardias de noche con un fusil viejo y una escarapela en el sombrero, y de día sigue subiendo agua por las escaleras. Vuelve de sus guardias con los pies helados y las noticias frescas. {nombre} le guarda la sopa.", desde = "jacques_guardia", hasta = "jacques_frontera"),
        v("Jacques está en la frontera, de voluntario, con un fusil de verdad y un uniforme que le queda grande. Escribe poco, porque no sabe, y lo que escribe se lo escribe un compañero. {nombre} se lleva las cartas al barco y se las hace leer tres veces.", desde = "jacques_frontera", hasta = "jacques_vuelve"),
        v("Jacques volvió de la frontera con una pierna mala que arrastra al andar y que ya no le deja subir escaleras con cubos. Ahora vende agua sentado junto a una fuente. No habla de la guerra. A veces, de noche, la habla dormido.", desde = "jacques_vuelve"),
        v("Paul, su hijo, se pasa el día en la calle con los otros niños del barrio, que lo saben todo antes que nadie y lo cuentan en casa por la noche. Tiene los ojos de Jacques y la cabeza en las nubes. {nombre} quiere que aprenda un oficio. Él quiere ser tambor.", hasta = "paul_barco"),
        v("Paul la ayuda en el barco: acarrea la ropa limpia en una cesta más grande que él, sube y baja la escalera del secadero, trae el agua caliente. Las lavanderas lo miman y le dan trozos de pan. {nombre} lo mira trabajar y piensa que es demasiado pronto, y que no hay otra cosa.", desde = "paul_barco", hasta = "paul_lee"),
        v("Paul sabe leer: aprendió en la escuela de la sección y ahora lee en voz alta todo lo que encuentra, los carteles, los periódicos, los papeles de envolver. {nombre} lo escucha con la boca abierta. Es el primero de su familia que lee.", desde = "paul_lee"),
        v("Antoine, el impresor del barrio, tiene una prensa en un sótano de la calle y las manos negras de tinta. Cada noche lee en voz alta, en la plaza, los periódicos que imprime y los que imprimen otros, y grita más en las partes que le gustan. {nombre} lo escucha siempre.", desde = "antoine"),
    )

    private val OBJETO = listOf(
        v("Hoy, entre golpe y golpe de paleta, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de la vieja Thérèse, que lo aprendió de su madre, que también lavaba en el Sena."),
        v("En el barco cada cosa cuesta algo, y lo que se pierde se paga. {dato_objeto}"),
        v("{dato_objeto} Los ricos no saben nada de eso. No les hace falta: para eso están las lavanderas."),
        v("{dato_objeto} Son cosas que se saben con las manos, no con la cabeza."),
        v("Una lavandera tiene poco: su paleta, su cesta, su chal, sus zuecos. Lo demás lo pone el río. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("paleta", "la paleta", "una paleta de lavar", "La paleta",
            "La paleta de lavar es una tabla de madera con mango, gastada en el centro de tantos golpes; con ella se golpea la ropa mojada sobre la tabla para que suelte la suciedad; una lavandera da miles de golpes al día, y se la oye desde lejos."),
        ObjetoCuento("tabla", "la tabla", "una tabla de lavar", "La tabla",
            "La tabla de lavar es una plancha de madera inclinada hacia el agua, sobre la que se frota y se golpea la ropa; está clavada al borde del puesto, y cada lavandera tiene la suya; con los años se pule como el mármol."),
        ObjetoCuento("jabon", "el jabón", "un trozo de jabón", "El jabón",
            "El jabón de las lavanderas es negro y blando, hecho con grasa y potasa, y se compra por libras; el blanco, de Marsella, es para la ropa fina de los ricos; los dos pican igual en las grietas de las manos."),
        ObjetoCuento("ceniza", "la ceniza", "un saco de ceniza", "La ceniza",
            "La ceniza de leña es lo que blanquea la ropa: se pone en un saco sobre la ropa en la tina y se le echa agua hirviendo, y el agua que pasa por la ceniza se vuelve lejía; con ella la ropa queda blanca, y las manos, en carne viva."),
        ObjetoCuento("cesta", "la cesta", "una cesta de ropa", "La cesta",
            "La cesta de la ropa es de mimbre, honda y con dos asas, y se lleva a la cadera o a la espalda; llena de ropa mojada pesa como un niño; las lavanderas suben con ellas las escaleras de medio París."),
        ObjetoCuento("pan", "el pan", "una hogaza", "El pan",
            "La hogaza de cuatro libras es el pan de los pobres de París, redonda y morena; un obrero se come una en dos días; cuando su precio sube, sube todo, y cuando pasa de cierto precio, París sale a la calle."),
        ObjetoCuento("escarapela", "la escarapela", "una escarapela", "La escarapela",
            "La escarapela es un lazo de cinta plegado en redondo que se lleva en el sombrero o en el pecho; la de París era azul y roja, sus colores, y en el verano de la Bastilla se le añadió el blanco, que era el del rey; con los años, el que no la lleva se vuelve sospechoso."),
        ObjetoCuento("periodico", "el periódico", "un periódico", "El periódico",
            "Los periódicos se venden por las calles a gritos, se pegan en las paredes y se leen en voz alta en las plazas y en las tabernas para los que no saben leer, que son muchos; cada uno dice una cosa distinta, y todos gritan."),
        ObjetoCuento("sueldo", "el sueldo", "un sueldo de cobre", "El sueldo",
            "El sueldo es una moneda de cobre, gorda y gastada, con la cara del rey; veinte sueldos hacen una libra; una lavandera gana unos pocos al día, y la hogaza de cuatro libras se le lleva casi todos cuando el pan sube."),
        ObjetoCuento("farol", "el farol", "un farol", "El farol",
            "Los faroles de la calle cuelgan de una cuerda en medio de la calzada, y se encienden con aceite al caer la noche, cuando hay aceite; los faroleros los bajan, los llenan y los vuelven a subir con una polea, uno por uno, todas las tardes."),
        ObjetoCuento("pinzas", "las pinzas", "unas pinzas de tender", "Las pinzas",
            "Las pinzas de tender son de una sola pieza de madera, partida por la mitad, que muerde la tela contra la cuerda; se compran por docenas a los que las tallan en los arrabales; se pierden siempre, y una lavandera lleva siempre unas cuantas en la boca."),
        ObjetoCuento("sueco", "los zuecos", "unos zuecos", "Los zuecos",
            "Los zuecos son de madera de una pieza, tallados a mano, con paja dentro para el frío; con ellos se anda por el barro de las calles y por el suelo mojado del barco sin mojarse los pies; suenan en las escaleras, y por el ruido se sabe quién llega."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas mientras golpea la ropa, y golpea más fuerte. A sus {edad} años sabe que el pan es lo primero, y que todo lo demás viene después. Si los de arriba no lo saben, alguien tendrá que enseñárselo."),
        v("Piensa a veces en su pueblo de Normandía, de donde vino a los quince años a servir en una casa, y en lo que haría allí ahora: lavar en el arroyo, cuidar vacas, casarse con un primo. No lo echa de menos. París es duro, pero en París pasan cosas."),
        v("{mundo} {nombre} no sabe leer, pero sabe contar: lo que le pagan, lo que cuesta el pan, lo que paga por su sitio en el barco, lo que queda. Lo que queda es casi nada. Lo cuenta cada noche, en la cama, como otras rezan."),
        v("{mundo} A veces, en el barco, con las manos en el agua helada, {nombre} piensa en las sábanas de seda que lava, de casas donde nunca ha entrado, y en la gente que duerme en ellas. Le gustaría dormir una noche en una. Una sola. Luego se le pasa."),
        v("Piensa en Paul, que crecerá en un París que ella no entiende del todo, con otras palabras, otras canciones, otras cosas que temer. Quiere que no se arrodille delante de nadie. No sabe cómo se consigue eso. Nadie se lo ha enseñado."),
        v("{mundo} A sus {edad} años, {nombre} ha lavado la ropa de curas, de nobles, de soldados, de mujeres de la calle y de muertos. Toda la ropa sucia se parece. Eso es lo que sabe de la gente, y le parece que es bastante."),
    )

    private val MUNDOS = mapOf(
        "bastilla" to listOf(
            "Se habla del rey y de sus deudas, y de los Estados Generales, que no se reunían desde hacía casi dos siglos.",
            "Se habla del pan, que nunca había costado tanto, y de quién tiene la culpa.",
            "Se dice en el barrio que los panaderos esconden la harina y que los ricos compran todo el trigo.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} sale del barco con la espalda rota y las manos en carne viva, y sube por las escaleras del muelle a la ciudad, que se enciende. En la plaza de Grève hay gente. Siempre hay gente en la plaza de Grève. Se para un momento a ver qué pasa."),
        v("Anochece, y el río se pone de color de plomo, y los barcos-lavaderos se quedan vacíos y oscuros, balanceándose en sus amarras. {nombre} es de las últimas en irse. Recoge su paleta, su tabla, su jabón. Mañana, otra vez."),
        v("Al caer la tarde, en la calle de {nombre}, los faroleros bajan los faroles con sus cuerdas, los llenan de aceite y los encienden, uno por uno. La calle se pone amarilla. Huele a sopa. Por las ventanas abiertas se oyen discusiones, risas y algún llanto de niño."),
        v("De noche, desde la ventana del quinto piso, se ven los tejados de París, un mar de chimeneas, y por encima las torres de Notre-Dame contra el cielo. Se oye la ciudad, que no se calla. {nombre} cierra el postigo y se sienta en la cama a quitarse los zuecos."),
        v("Anochece, y {nombre} llega a casa y lo primero que hace es meter las manos en un cuenco con un poco de sebo, que le alivia las grietas. Paul le cuenta lo que pasó en la calle. Ella lo escucha con los ojos cerrados. Las manos le arden. La voz de Paul la calma."),
        v("La última luz del día se queda en el agua del Sena cuando las calles ya están a oscuras, y por un momento el río parece de cobre. {nombre} lo mira desde el puente, cansada, con la cesta vacía. Luego sube a su barrio por las calles negras, sin miedo. Conoce a todo el mundo."),
        v("Al anochecer, en invierno, {nombre} vuelve a casa ya de noche, con la ropa mojada helándose encima y los pies que no siente. En el cuarto no hay fuego: la leña está a precio de oro. Se mete en la cama con Paul, a calentarse.", meses = setOf(12, 1, 2)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "reyes" to unDia(
            Parrafo.MANANA to "Hoy es la Epifanía, el día de los reyes, y en todas las panaderías de París hay tortas de hojaldre con un haba escondida dentro. La panadera de la esquina le guarda a {nombre} un trozo de la suya, envuelto en papel, para Paul. El que encuentra el haba es rey por un día.",
            Parrafo.GENTE to "Esta noche en la escalera las vecinas parten una torta entre todas, con una vela, y Paul se mete debajo de la mesa para decir, sin ver, a quién le toca cada trozo, como se hace. Le toca el haba a la viuda Morin, que se pone una corona de papel y se ríe hasta toser.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que es curioso un día en que cualquiera puede ser rey por un haba. Thérèse dice que así se elegía a los reyes al principio, y que por eso salen tan malos. {nombre} se ríe. Luego piensa que a lo mejor no es broma.",
        ),
        "carnaval" to unDia(
            Parrafo.MANANA to "Hoy es martes de carnaval, el último día antes de la cuaresma, y París entero está en la calle: máscaras, disfraces, caras llenas de harina, un buey gordo adornado de cintas que pasean por las calles, músicos. Las lavanderas tienen fama de ser las más ruidosas. {nombre} lleva una máscara de cartón.",
            Parrafo.TRABAJO to "A esta hora no trabaja: el barco está cerrado y las lavanderas están en la calle, con sus mejores enaguas, cantando canciones que no se pueden cantar delante de un cura. Thérèse baila con un panadero. {nombre} baila con quien se deje, y se deja todo el mundo.",
            Parrafo.GENTE to "Esta noche en la escalera se hacen crepes en todas las sartenes, con la poca harina que hay y un huevo prestado, y se saltan en el aire con una moneda en la otra mano, para la suerte del año. A Paul se le cae la suya al suelo. Se la come igual.",
        ),
        "colada" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empezó la gran colada de primavera, cuando las casas de París sacan todas las sábanas, mantas y cortinas que se ensuciaron en el invierno. Los barcos-lavaderos se llenan hasta los bordes. Habrá trabajo para dos semanas. Habrá dinero. Habrá dolor de espalda.",
                Parrafo.TRABAJO to "A esta hora carga la ropa blanca en la tina grande, en capas, con un saco de ceniza encima, y le echa agua hirviendo con un cazo de mango largo, una vez y otra, sin parar. El vapor llena el barco. Las lavanderas trabajan con las mangas subidas y la cara roja.",
                Parrafo.PENSAMIENTO to "{nombre} piensa, golpeando la décima sábana del día, que en París se lava en primavera lo que se ensució en invierno, y que alguien tendría que hacer lo mismo con todo lo demás. Thérèse la oye pensar en voz alta y le dice que se calle, que el maestro Gaspard escucha.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora tiende sábanas en el secadero del barco, tantas que no caben, y las cuelga también en cuerdas del muelle, que se llenan de blanco. Los paseantes miran. Los niños corren entre las sábanas como entre fantasmas. Paul con ellos.",
                "A esta hora golpea mantas de lana en la tabla, que pesan como muertos cuando están mojadas, y las retuerce con Thérèse, cada una de una punta, hasta que crujen. Las manos le sangran por las grietas. Se las envuelve en un trapo y sigue.",
                "A esta hora entrega fardos de ropa limpia en las casas, uno tras otro, y cobra. Las criadas cuentan las piezas; las señoras, no. Al final del día tiene el pañuelo lleno de monedas y los brazos sin fuerza. Es la mejor semana del año.",
            )),
        ),
        "pascua" to unDia(
            Parrafo.MANANA to "Hoy es Pascua, y las campanas de Notre-Dame, que estaban calladas desde el jueves, suenan todas a la vez, y luego las de todas las iglesias de París, cientos, hasta que no se oye otra cosa. El barco está cerrado. {nombre} se puso su vestido de los domingos.",
            Parrafo.TRABAJO to "A esta hora no trabaja: es Pascua, el día más grande del año, y ni el maestro Gaspard abre el barco. {nombre} va a misa con Paul a la parroquia, donde no cabe un alfiler, y luego pasea por los muelles con medio París, mirando, sin hacer nada. Es raro no hacer nada.",
            Parrafo.GENTE to "Esta noche se come en casa lo que no se come nunca: un huevo duro por cabeza, teñido de rojo con piel de cebolla, un trozo de tocino y pan blanco. Paul se come su huevo y se queda mirando el de su madre. Ella se lo da. Él dice que no. Se lo come.",
        ),
        "fete_dieu" to unDia(
            Parrafo.MANANA to "Hoy es la Fête-Dieu, y la procesión del Santísimo pasa por las calles del barrio. Desde ayer, las vecinas cuelgan de las ventanas sus mejores sábanas, y las que no tienen buenas piden prestadas las que lavan. Hay altares en las esquinas, con flores, velas y santos.",
            Parrafo.TRABAJO to "A esta hora no lava: es fiesta, y las lavanderas del barco están en la calle, porque media procesión pasa entre sábanas que lavaron ellas. {nombre} reconoce tres suyas, colgadas de un balcón de la calle de la Mortellerie, y se lo dice a Thérèse. Thérèse reconoce diez.",
            Parrafo.PENSAMIENTO to "{nombre} se arrodilla en la calle al paso del Santísimo, bajo el palio, con los curas, las velas y los niños vestidos de ángeles, y piensa que esas sábanas blancas de las ventanas son de sus manos. Piensa que Dios pasa entre la ropa limpia. Le parece bien.",
        ),
        "san_juan" to unDia(
            Parrafo.MANANA to "Hoy es la víspera de San Juan, y en la plaza de Grève han levantado un árbol enorme de leña y ramas, cargado de flores y coronas, que se quemará esta noche. Los niños corren alrededor desde la mañana. Paul quiere ir el primero. {nombre} le dice que primero se trabaja.",
            Parrafo.ANOCHECER to "Al anochecer se enciende la hoguera de San Juan en la plaza de Grève, y la gente baila alrededor, canta, salta las brasas para tener suerte. Las chispas suben hasta las ventanas del ayuntamiento. {nombre} salta con Paul de la mano. Thérèse dice que a su edad ya solo salta el corazón.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, mirando el fuego, en las hogueras de su pueblo de Normandía, más pequeñas y más alegres, y en su madre, que colgaba un manojo de hierbas de San Juan en la puerta contra el rayo. Se lleva una brasa en un tiesto, para la suerte del cuarto.",
        ),
        "asuncion" to unDia(
            Parrafo.MANANA to "Hoy es la Asunción, la fiesta de la Virgen de agosto, y en Notre-Dame hay misa grande y procesión por las calles de la Cité, con la imagen en andas, los canónigos y los cofrades con sus velas. El barco está cerrado. Hace calor, y el río huele.",
            Parrafo.TRABAJO to "A esta hora no trabaja: es fiesta de guardar. {nombre} lleva a Paul a ver la procesión, y luego bajan al río a bañarse con los otros pobres del barrio, en una orilla donde los hombres se bañan desnudos y las mujeres en camisa. Paul chapotea como un perro.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la Virgen también era de pueblo, y que también lavaría la ropa en un río, de rodillas, con las manos rojas. Lo piensa siempre en esta fiesta, y le gusta. Thérèse dice que en Jerusalén no hay ríos. {nombre} dice que alguno habrá.",
        ),
        "todos_santos" to unDia(
            Parrafo.MANANA to "Hoy es Todos los Santos, y mañana, el día de los difuntos. Las campanas de París doblan toda la tarde y toda la noche, lentas, unas contestando a otras. El barco está cerrado. {nombre} compra una vela pequeña para sus muertos, que están en Normandía y en una fosa común de París.",
            Parrafo.TRABAJO to "A esta hora no trabaja. Va con Paul a la iglesia, enciende la vela delante de un santo que no sabe cuál es y reza por su madre, por su padre, por un hermano que murió en el hospital, por una niña que tuvo antes que Paul y que no llegó a andar. No llora. Ya lloró.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los muertos de París, que se entierran en fosas comunes, todos juntos, sin nombre, y en los de su pueblo, cada uno con su cruz. Piensa que en París hasta los muertos viven apretados. Quiere que a ella la entierren en su pueblo. Sabe que no será.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, y el barco está cerrado. Anoche {nombre} fue a la misa de medianoche con las vecinas de la escalera, todas juntas, con un farol, por las calles heladas, y volvió con los pies dormidos y el corazón contento. Hoy hay tocino, castañas y vino que no es de taberna.",
            Parrafo.TRABAJO to "A esta hora no trabaja: es Navidad, y el barco está cerrado. Duerme un rato más, pasea con Paul por los muelles helados y se paran delante de las tiendas de la calle Saint-Honoré, donde todo parece de otro mundo. Paul pega la nariz a los cristales.",
            Parrafo.GENTE to "Se juntan en el cuarto de la viuda Morin las vecinas de la escalera, cada una con lo que tiene, y comen juntas: tocino, castañas, una sopa con un hueso, pan blanco. Se canta un villancico. Paul se come las castañas de todas y se duerme en un rincón con la boca llena.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el niño de la Navidad nació en un establo, pobre como Paul, y que los reyes fueron a verlo a él y no al revés. Le parece la historia más bonita del mundo. Le gustaría que se la contaran también a los reyes de ahora.",
        ),

        // ---------- Los de la temporada 1 ----------
        "hielo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y el Sena está helado de orilla a orilla desde hace semanas. Los barcos-lavaderos están presos en el hielo, crujiendo, con nieve en los techos. No hay ropa que lavar ni agua donde lavarla. {nombre} lleva un mes sin cobrar casi nada.",
            Parrafo.PUEBLO to "La gente del barrio cruza el río a pie por encima del hielo, como si fuera una calle, entre los barcos atrapados. Los niños se deslizan. Alguien ha puesto un puesto de vino caliente en medio del río. Los viejos dicen que no se acuerdan de un invierno así.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el año empieza mal: sin trabajo, sin leña, con el pan caro y el río muerto. Jacques dice que el rey va a reunir a los Estados Generales y que las cosas cambiarán. {nombre} piensa que los Estados Generales no se comen. Que cambien antes de que se acabe el pan.",
        ),
        "deshielo" to unDia(
            Parrafo.MANANA to "Hoy, al amanecer, se oyó un ruido como de cañones: el hielo del Sena se estaba rompiendo. Placas enormes se levantan, chocan, se montan unas encima de otras y bajan con la corriente. Los barcos-lavaderos tiran de sus amarras. La gente del muelle mira en silencio.",
            Parrafo.TRABAJO to "A esta hora ayuda a los hombres a apartar del barco con pértigas los témpanos que bajan con el río, para que no lo rompan. Uno se lleva un trozo de la proa. El maestro Gaspard grita. Al final de la tarde el barco aguanta, torcido, mojado, vivo. Mañana se lava.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, viendo correr otra vez el agua negra, que nunca se había alegrado tanto de ver el río. Mañana habrá ropa, agua helada, manos rotas, monedas. Le parece que eso es lo que la gente llama la felicidad: que vuelva lo de siempre.",
        ),
        "reveillon" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy llegó al barco la noticia, que corre por todo París: en el arrabal de Saint-Antoine, los obreros asaltaron la casa de un fabricante de papel pintado, Réveillon, porque dicen que dijo que un obrero puede vivir con quince sueldos al día. Con quince sueldos no se compra ni el pan.",
                Parrafo.PUEBLO to "Por la calle se ve pasar gente corriendo hacia el este, con palos, y luego soldados a caballo. Por la tarde se oyen tiros desde Saint-Antoine. Las lavanderas dejan las paletas y se quedan escuchando. El maestro Gaspard cierra la puerta del barco que da al muelle.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en los quince sueldos. Ella gana a veces menos. Piensa que el fabricante dijo lo que muchos ricos piensan, y que por decirlo le quemaron la casa. Le parece mal que quemen casas. Le parece peor que los ricos lo piensen. No sabe qué le parece peor.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "En el barrio no se habla de otra cosa que de Saint-Antoine: de los muertos, que dicen que son muchos; de los soldados, que dispararon contra la gente; de los heridos que no van al hospital por miedo. Las lavanderas lavan en silencio.",
                "Por la calle pasan carros con heridos hacia el Hôtel-Dieu, y las mujeres se asoman a ver si conocen a alguno. Una conoce a uno. Se pone a gritar. Las demás la sujetan.",
            )),
        ),
        "estados" to unDia(
            Parrafo.MANANA to "Hoy se abrieron en Versalles los Estados Generales, que el rey reunió por primera vez en casi dos siglos: los curas, los nobles y los del pueblo, cada uno con su ropa y su sitio. En el barco se habla de eso todo el día, entre golpe y golpe. Nadie sabe bien qué son.",
            Parrafo.GENTE to "Esta noche en la plaza alguien lee en voz alta un papel que dice lo que pide el pueblo de París: pan barato, que no paguen siempre los mismos, que el rey escuche. Los que escuchan aplauden cada frase. {nombre} aplaude también. Jacques dice que esta vez va en serio.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los del pueblo que fueron a Versalles a hablar por ella, abogados y comerciantes con trajes negros, que no han lavado una sábana en su vida. Piensa que a lo mejor hablan bien. Le gustaría que una lavandera fuera con ellos, por si acaso.",
        ),
        "bastilla" to unDia(
            Parrafo.MANANA to "Hoy desde la mañana París entero está en la calle buscando armas: se llevaron miles de fusiles de los Inválidos, y ahora van a la Bastilla, la fortaleza del este, a buscar pólvora. Se oyen tiros y cañonazos desde allí. Las lavanderas dejaron el barco y están en el muelle.",
            Parrafo.TRABAJO to "A esta hora no lava: hoy nadie lava en París. Está en el muelle con las otras, con la paleta todavía en la mano, oyendo los cañonazos, preguntando a los que pasan corriendo qué pasa, a quién, dónde. Cada uno dice una cosa distinta.",
            Parrafo.PUEBLO to "Por la tarde la noticia corre por las calles como un incendio: cayó la Bastilla. La gente grita, se abraza, llora. Pasan hombres con fusiles y picas, y en la punta de una pica, algo que {nombre} no quiere mirar. Las campanas tocan a rebato. Nadie sabe qué viene ahora.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hoy pasó algo que todavía no tiene nombre. La Bastilla era el sitio donde el rey encerraba a quien quería, y ahora es de la gente. Piensa en el pan, que sigue igual de caro. A lo mejor mañana no. Hoy no piensa más: hoy se grita.",
        ),
        "jacques_guardia" to unDia(
            Parrafo.MANANA to "Hoy Jacques volvió a casa con una escarapela azul y roja en el sombrero y un fusil viejo al hombro: se apuntó en la guardia del barrio, la de los vecinos, que vigila las calles de noche desde lo de la Bastilla. Paul se pone el sombrero y no se lo quita en toda la tarde.",
            Parrafo.GENTE to "Esta noche Jacques hace su primera guardia en la esquina de la calle, con otros cuatro vecinos, un farol y un fusil para los cinco. {nombre} les baja sopa caliente. Se ríen de él porque no sabe cargar el fusil. El zapatero de abajo le enseña. Ninguno ha disparado nunca.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Jacques con el fusil, tan contento, tan orgulloso, y le da miedo. Un aguador con un fusil sigue siendo un aguador. Pero los ricos tienen soldados, y los pobres, ahora, tienen a Jacques. Algo es algo. Le cose la escarapela para que no se le caiga.",
        ),
        "derechos" to unDia(
            Parrafo.MANANA to "Hoy en la plaza un hombre subido a un barril leyó en voz alta un papel que aprobó la Asamblea en Versalles: la declaración de los derechos del hombre. Que los hombres nacen libres e iguales. Que nadie puede ser preso sin ley. Que el poder es de la nación. La gente aplaudía cada frase.",
            Parrafo.GENTE to "Esta noche en la taberna las lavanderas discuten si eso de los hombres es también para las mujeres. Thérèse dice que no, que cuando dicen hombres dicen hombres. Otra dice que sí, que hombre quiere decir todos. Al final están de acuerdo en que da igual, mientras baje el pan.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en las palabras que leyó el hombre del barril, libres e iguales, y las repite bajito mientras golpea la ropa. No sabe si son verdad. Sabe que son bonitas. Nunca nadie había dicho delante de ella que era igual a nadie. Se le hace un nudo en la garganta.",
        ),
        "versalles" to TextoEspecial(listOf(
            mapOf(
                Parrafo.MANANA to "Hoy por la mañana una muchacha se puso a tocar un tambor en un mercado, y las mujeres empezaron a juntarse: verduleras, pescaderas, lavanderas, criadas. No hay pan. Fueron al ayuntamiento, y alguien gritó: a Versalles, a pedirle pan al rey. {nombre} soltó la paleta y fue.",
                Parrafo.TRABAJO to "A esta hora camina bajo la lluvia por el camino de Versalles, seis horas, con miles de mujeres, algunas con palos y cuchillos, y unos cañones que arrastran entre todas. Cantan, gritan, se ríen, se mojan. {nombre} va con las del barco, cogidas del brazo.",
                Parrafo.PUEBLO to "Por el camino de Versalles van mujeres hasta donde se ve, y detrás, dicen, viene la guardia de París. En los pueblos del camino la gente sale a mirar. Algunas mujeres de los pueblos se unen. Nadie las para.",
                Parrafo.ANOCHECER to "Al anochecer llegan a Versalles, empapadas, y se meten en la sala de la Asamblea, que se llena de mujeres sentadas en los bancos de los diputados, comiendo lo que traen. Unas cuantas entran a ver al rey. Dicen que el rey les prometió pan. {nombre} duerme en el suelo, en un portal.",
            ),
            mapOf(
                Parrafo.MANANA to "Hoy, antes del alba, unos hombres se metieron en el palacio y mataron a unos guardias. Por la mañana el rey salió al balcón con la reina y los niños, y la gente del patio gritó que se viniera a París. Y el rey dijo que sí. {nombre} lo vio, pequeño, allá arriba.",
                Parrafo.TRABAJO to "A esta hora camina de vuelta a París detrás del carruaje del rey, en una procesión enorme de mujeres, guardias, carros de harina del palacio y panes en la punta de las bayonetas. Las mujeres gritan que traen al panadero, a la panadera y al pequeño aprendiz. {nombre} grita también.",
                Parrafo.TIEMPO to "En el camino de vuelta llueve otra vez, a ratos, y nadie se acuerda de la ropa tendida en el barco. Las mujeres se tapan la cabeza con las enaguas y siguen cantando.",
                Parrafo.PUEBLO to "El camino de vuelta a París está lleno de gente que mira pasar la procesión, que tarda horas en avanzar: el carruaje del rey, rodeado de mujeres con ramas verdes, de guardias, de carros. En las ventanas aplauden. Nadie se va a casa.",
                Parrafo.ANOCHECER to "Ya de noche llega la procesión a París, y el rey va al ayuntamiento y luego a las Tullerías. {nombre} se separa de las demás en la plaza de Grève, sin voz, sin pies, empapada, y sube a su barrio como puede. Jacques la está esperando en la puerta.",
            ),
        )),
        "pies" to unDia(
            Parrafo.MANANA to "Hoy {nombre} volvió al barco con los pies envueltos en trapos, deshechos de dos días de camino, y las otras lavanderas la recibieron como a un soldado. Quieren que lo cuente todo: el palacio, la reina, el balcón, el rey. Lo cuenta cinco veces. Cada vez le sale un poco mejor.",
            Parrafo.GENTE to "Thérèse la escucha sin interrumpir, cosa rara, y al final le dice que es una tonta, que podían haberla matado, y luego la abraza fuerte. El maestro Gaspard no le cobra el sitio de los dos días. Dice que es por el rey. Nadie sabe qué quiere decir.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el rey está ahora en París, en las Tullerías, a un paseo del barco, y que la reina duerme a la orilla del mismo río donde ella lava. Piensa que ahora el pan bajará. Y que si no baja, ya saben el camino. Le duelen los pies. Le da igual.",
        ),
        "paul_barco" to unDia(
            Parrafo.MANANA to "Hoy Paul fue con su madre al barco, y no a jugar en el muelle sino a trabajar: el maestro Gaspard le deja acarrear la ropa limpia del secadero a las cestas, por unos sueldos a la semana. Paul se levantó antes que ella, se puso el gorro y la esperó en la puerta.",
            Parrafo.TRABAJO to "A esta hora le enseña a Paul a doblar una sábana sin que toque el suelo, cada uno de una punta, dando un paso adelante y otro atrás, como un baile. Paul la arruga. Lo vuelven a hacer. A la cuarta, sale bien. Las lavanderas aplauden y Paul se pone colorado como una amapola.",
            Parrafo.PENSAMIENTO to "{nombre} mira a Paul subir y bajar la escalera del secadero con una cesta más grande que él y piensa que es demasiado pequeño para trabajar y demasiado pobre para no hacerlo. A su edad, ella ya servía. Se dijo que con él sería distinto. Todavía puede serlo, piensa. Todavía.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "lavandera", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + HELADA_MANANA,
            Parrafo.TIEMPO to TIEMPO + HELADA_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + HELADA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + HELADA_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + HELADA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
