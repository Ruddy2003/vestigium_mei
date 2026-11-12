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
 * El cuento de Nanai-vandak, mercader sogdiano de caravana entre Samarcanda y Chang'an, del 750 al 754. Sigue la
 * gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Al revés que los demás, lo normal es el camino: las variantes sin estado son las de la caravana, y las dos
 *   ciudades donde pasa el verano son estados («changan» y «samarcanda»), cada uno con su mañana, su trabajo de cada
 *   hora, su pueblo, su tiempo y su anochecer.
 * - En el camino, el párrafo del pueblo lee el arco «ruta»: los valles de Sogdiana, las montañas, los desiertos y los
 *   oasis, o el corredor y las colinas de loess cerca de Chang'an.
 * - En Chang'an lo llaman Kang, como a todos los de Samarcanda.
 * - No repite lo que ya dice «Su vida»: los treinta kilómetros diarios, el medio año de viaje, las cuatro lenguas, lo
 *   que compra y vende, la fama de beber de los sogdianos y las cartas que tardan un año.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG3.kt`):
 * - Farn, su hijo, se queda en Samarcanda hasta `sale_samarcanda` (temporada 2), viaja con él desde entonces, se
 *   enamora de la hija de Shi Ping en `farn_novia` (3), se queda en Chang'an cuando su padre sale hacia el oeste (los
 *   días entre `farn_novia` y `kucha` lo dicen sin fecha exacta) y se casa en `boda_farn` (5).
 * - Su hija, Nanai-dhat, se casa en `boda_hija` (temporada 2); el nieto nace en `nieto` (4).
 * - Takhsich, su mujer, vive siempre en Samarcanda. Tughan, el muchacho de los camellos, va siempre con él.
 * - Las fiestas que vuelven cada año, y los días de llegar y salir de Chang'an y de Dunhuang, que se repiten, no
 *   nombran a Farn, a su hija ni al nieto.
 */
internal object PiezasNanai {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    // =====================================================================
    //  El camino (sin estado)
    // =====================================================================

    private val MANANA = listOf(
        v("{nombre} se despierta en el caravasar antes de que amanezca, con el ruido de los camellos, que gruñen cuando los cargan como si se quejaran de la vida. Tughan ya está con ellos. El patio huele a estiércol, a humo y a té. {nombre} se levanta con los huesos fríos."),
        v("A {nombre} lo despierta el frío, que entra por las rendijas del cuarto del caravasar, una celda de adobe sin ventana donde duermen cuatro mercaderes y sus fardos. Se pone el caftán de lana, el gorro de fieltro y las botas. Fuera, las estrellas todavía brillan."),
        v("{nombre} sale al patio del caravasar y mira el cielo del este, por donde va. Los camelleros cuentan los fardos a la luz de una antorcha, atan las cuerdas, cargan el agua. Se oyen seis lenguas a la vez. Él las entiende todas, más o menos."),
        v("Lo primero del día es contar: los camellos, los fardos, los sacos de agua, las monedas del cinturón. {nombre} lo hace cada mañana, a oscuras, con los dedos, como lo hacía su padre. Si falta algo, es mejor saberlo antes de salir que después."),
        v("Antes de salir, {nombre} enciende un poco de incienso en un rincón del patio y reza en voz baja al dios de los mercaderes, a la diosa Nana, de la que lleva el nombre, y al fuego, que es lo más puro que hay. Los otros mercaderes rezan a sus dioses. Hay sitio para todos."),
        v("{nombre} se despierta en la tienda de fieltro, en mitad de la nada, porque no hubo caravasar donde parar. Los camellos están echados alrededor, como una muralla que respira. El aliento sale blanco. Tughan aviva el fuego de estiércol seco."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el camino el tiempo no es una conversación: es lo que decide si se llega o no se llega."),
        v("{nombre} mira el cielo desde lo alto de una duna, o de una loma, mientras pasan los camellos. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los camelleros viejos saben oler la tormenta de arena un día antes, y los camellos, dos."),
        v("{cielo} En {estacion} la ruta es otra, y {nombre} lo sabe por los pies, por la cara y por los camellos."),
        v("Desde el camino se ven, muy lejos, las montañas nevadas, siempre a un lado, como si acompañaran a la caravana. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Tughan dice que en su estepa el cielo es más grande. {nombre} le dice que es el mismo, y que el que es pequeño es él."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y altísimo, de un azul que parece recién lavado.",
            "Hace un día claro, y la luz es tan dura que todo tiene dos colores: el suyo y su sombra.",
            "No hay una nube, y el sol pega aunque haga frío.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris como el fieltro.",
            "Hay nubes bajas, y las montañas no se ven.",
            "Está nublado, y la luz es plana y triste.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y la tierra se vuelve barro que se pega a las patas de los animales.",
            "Cae una lluvia fina y fría que se mete en los fardos.",
            "Llueve, cosa rara, y la gente sale a mirarla.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos retumban y los camellos se echan al suelo, quietos.",
            "El cielo se pone negro de golpe, y cae una tormenta corta y furiosa.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y el mundo se queda blanco y callado.",
            "Caen copos finos y duros que el viento mete por todas partes.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento que no para, y hay que hablar a gritos.",
            "Hay viento del norte, frío, que hace llorar los ojos.",
        ),
        Clima.POLVO to listOf(
            "El aire está amarillo de polvo, y el sol es un disco pálido que se puede mirar.",
            "Hay polvo en el viento, y se mete en la boca, en los ojos y en la comida.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando la nieve se va de los pasos y los ríos crecen.",
            "En primavera las estepas se ponen verdes por unos días, y los camellos engordan.",
            "Es el tiempo de los vientos, que levantan el polvo del desierto.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, cuando nadie con juicio cruza el desierto.",
            "En verano el calor no deja dormir y los melones están dulces.",
            "Es el tiempo del calor, de las moscas y de la fruta.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo de salir al camino, antes de que cierren los pasos.",
            "En otoño las caravanas se juntan en las puertas de las ciudades para salir.",
            "Es el tiempo de las uvas y de los primeros fríos.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y el frío del camino se mete en los huesos y no sale.",
            "En invierno los ríos se hielan y se cruzan a pie.",
            "Es el tiempo de los pasos cerrados y de las noches largas.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora camina al lado de los camellos, como todos, porque montar cansa más al camello que andar cansa al hombre. El camino es una línea de huellas y de huesos blancos de animales que no llegaron. {nombre} no los mira. Mira adelante.", hacer = Hacer.CAMINO),
        v("A esta hora va al frente de su parte de la caravana, con el camello que lleva la campana, y detrás los otros, atados uno a otro por la nariz, en fila. La campana suena a cada paso. En el desierto, si se calla, es que algo pasó.", hacer = Hacer.CAMINO),
        v("A esta hora monta un rato el caballo, para descansar los pies, y adelanta a la caravana para mirar el camino: si hay agua en el pozo, si hay gente en el horizonte, si hay humo. Hoy no hay nada. Nada es lo mejor que puede haber en el camino.", hacer = Hacer.CAMINO),
        v("A esta hora camina y hace cuentas de cabeza: lo que costó la seda, lo que costará el camino, lo que valdrá en Samarcanda, lo que se quedarán los aduaneros de cada ciudad. Las cuentas siempre salen bien al principio del viaje. Al final, ya se verá.", hacer = Hacer.CAMINO),
        v("A esta hora llega con la caravana a la puerta de un fuerte chino del camino, donde los soldados revisan los papeles: el permiso de paso, con el sello de cada puesto, y la lista de lo que lleva. {nombre} los saluda en chino, sonríe y paga. Pasan.", hacer = Hacer.CAMINO),

        v("A esta hora come de pie, deprisa, pan duro mojado en té caliente con sal y un trozo de carne seca. En el camino no se come sentado más que una vez al día, y no es esta. {nombre} mastica mirando los fardos, por si alguno se afloja.", hacer = Hacer.COMER),
        v("A esta hora come junto a los camellos echados, o al abrigo del muro del caravasar, según dónde lo agarre el hambre: pan, queso de cabra, albaricoques secos, un poco de vino aguado. Los camellos rumian a su lado. {nombre} se quita las botas un momento.", hacer = Hacer.COMER),
        v("A esta hora come con los otros mercaderes, sentados en el suelo alrededor de una olla de carne de cordero con nabos que hizo el cocinero de la caravana. Cada uno saca su cuchillo y su cuenco. Se come callado, porque se está cansado.", hacer = Hacer.COMER),

        v("A esta hora está en el patio del caravasar con los otros mercaderes, cambiando noticias como se cambia mercancía: dónde hay guerra, qué precio tiene la seda en Kucha, quién murió, quién se casó, qué camino está cortado. Una noticia buena vale más que un fardo.", hacer = Hacer.GENTE),
        v("A esta hora negocia con un mercader de otra caravana, que va en sentido contrario, junto al fuego del caravasar: él le cambia almizcle por pimienta, el otro le cambia plata por seda. Regatean una hora, con té, y al final los dos creen que ganaron.", hacer = Hacer.GENTE),
        v("A esta hora escribe una carta a Takhsich a la luz de una lámpara, en el cuarto del caravasar, en un trozo de papel chino doblado: que está bien, que los camellos están bien, que llegará cuando llegue. Se la dará a alguien que vaya hacia el oeste.", hacer = Hacer.GENTE),
        v("A esta hora bebe vino con los camelleros junto al fuego, y alguien toca el laúd y canta una canción de Sogdiana, de las que hablan de una muchacha que espera. Todos cantan el estribillo. {nombre} canta más fuerte que nadie, que es lo que se espera de él.", hacer = Hacer.GENTE),

        v("A esta hora duerme en el cuarto del caravasar, sobre una manta de fieltro, con la bolsa de las monedas bajo la cabeza y el cuchillo a mano. En el patio, los camellos rumian. Alguien tose. Un perro ladra. Todo bien.", hacer = Hacer.DORMIR),
        v("A esta hora duerme en la tienda de fieltro, en mitad del desierto, con los camellos echados alrededor y un guardia despierto. El frío de la noche es tan grande como el calor del día. Sueña con Samarcanda, con un patio con una fuente.", hacer = Hacer.DORMIR),
        v("A esta hora duerme mal, con el viento golpeando la puerta del caravasar y los camellos inquietos. Se levanta a mirar si los fardos están bien atados. Lo están. Se vuelve a acostar y escucha el viento hasta que se duerme.", hacer = Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El caravasar es un cuadrado de muros de adobe con una sola puerta, grande, que se cierra de noche; dentro, un patio con un pozo, establos para los animales y cuartos pequeños alrededor para la gente. Todos los caravasares son iguales, y por eso uno se siente en casa en todos. {entorno}"),
        v("{entorno} La caravana es una ciudad que camina: ochenta camellos, veinte caballos, burros, perros, camelleros, guardias, un cocinero, mercaderes de cuatro pueblos y un monje que va a la India. Tiene su jefe, sus peleas y sus chismes."),
        v("En el camino hay torres de señales cada cierta distancia, de adobe, con soldados chinos aburridos que encienden humo si ven enemigos. Saludan a la caravana desde arriba. Algunos piden vino. Algunos lo reciben. {entorno}"),
        v("{entorno} El camino no es un camino: es la dirección que siguen las caravanas, marcada por pozos, por caravasares, por montones de piedras y por los huesos de los que no llegaron. Los guías lo saben de memoria. Cuando el viento borra las huellas, lo saben igual."),
        v("En los oasis del camino hay ciudades pequeñas con muros, mercado, templos de Buda y casas de sogdianos que llegaron hace generaciones y se quedaron. Siempre hay alguien que habla su lengua y que tiene un primo en Samarcanda. {entorno}"),
        v("Detrás de la caravana va siempre otra, y delante otra, a días de distancia, por el mismo camino. Se dejan mensajes en los caravasares, se avisan de los peligros, se compiten los precios. {entorno}"),
    )

    /** Lo que se ve en el camino según dónde está (arco «ruta»: 0 = Samarcanda, 1 = Chang'an). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("ruta", 0.0, 0.15, frases = listOf(
            "Alrededor están los valles de Sogdiana, con sus acequias, sus huertos de albaricoques y sus castillos de adobe en las colinas.",
            "Ya se ven los huertos y las viñas de los valles de Sogdiana: casa está cerca, o acaba de quedar atrás.",
        )),
        Entorno("ruta", 0.15, 0.35, frases = listOf(
            "Alrededor hay montañas nevadas y pasos altos, donde el aire falta y los caballos sangran por la nariz.",
            "El camino sube entre montañas de piedra, por gargantas donde el río ruge debajo y el viento arriba.",
        )),
        Entorno("ruta", 0.35, 0.72, frases = listOf(
            "Alrededor está el desierto, de arena o de piedra negra, y a lo lejos, una mancha verde de álamos: el próximo oasis.",
            "El desierto no se acaba, y de pronto se acaba: un oasis con acequias, melones, álamos y una muralla de adobe.",
        )),
        Entorno("ruta", 0.72, 0.9, frases = listOf(
            "Alrededor está el corredor del oeste: a un lado, montañas nevadas; al otro, el desierto; en medio, fuertes y torres de señales.",
            "El camino va por el corredor del oeste, una franja de oasis y de fuertes entre las montañas y el desierto, con ciudades llenas de sogdianos.",
        )),
        Entorno("ruta", 0.9, 1.0, frases = listOf(
            "Ya se ven las colinas amarillas de tierra fina de cerca de la capital, con casas cavadas en ellas como nidos de golondrina.",
            "Alrededor hay colinas de tierra amarilla y fina, cortadas por barrancos, con campos de mijo y aldeas cavadas en la ladera: la capital está cerca.",
        )),
    )

    private val GENTE = listOf(
        v("Takhsich, su mujer, se queda en Samarcanda con la casa, los criados, las cuentas y la familia, y lleva el negocio de allá mejor que él el de acá. Se casaron hace veinte años y han vivido juntos, entre viaje y viaje, quizá cinco. Sus cartas son cortas y no se le escapa nada."),
        v("Tughan, el muchacho que cuida los camellos, es un turco de la estepa de catorce o quince años, que no sabe cuántos tiene. Habla con los camellos en su lengua y los camellos le hacen caso, cosa que no hacen con nadie. {nombre} le paga poco y lo trata como a un sobrino."),
        v("Shi Ping es un sogdiano de Tashkent que tiene tienda en el mercado del oeste de Chang'an, y la casa donde se queda {nombre} cuando está allá. Es gordo, alegre y más chino que los chinos: viste como ellos, come como ellos y cuenta como ellos. Pero bebe como un sogdiano."),
        v("El jefe de la caravana es un sogdiano viejo y seco, que ha hecho el camino más veces que nadie y a quien todos obedecen, hasta los guardias. Decide cuándo se sale, dónde se para, cuánto se espera. {nombre} lo respeta y le tiene un poco de miedo."),
        v("En la caravana viaja un monje budista que va a la India a buscar libros, flaco, callado y sonriente, que reza en voz baja mientras camina y no se queja nunca de nada. {nombre} le da pan y él le da paz, dice. {nombre} cree que sale ganando el monje."),
        v("Farn, su hijo, tiene dieciséis años y se quedó en Samarcanda con su madre, aunque quiere venir. Escribe en cada carta que ya es un hombre, y que sabe cargar un camello, que no sabe. {nombre} le guarda las cartas en la bolsa del pecho.", hasta = "sale_samarcanda"),
        v("Farn, su hijo, viaja con él, aprendiendo el camino: a cargar un camello, a regatear, a no fiarse, a fiarse de quien hay que fiarse. Se cansa, se queja y aprende rápido. {nombre} lo mira caminar delante y piensa que así lo miraba su padre.", desde = "sale_samarcanda", hasta = "farn_novia"),
        v("Farn, su hijo, se enamoró de la hija de Shi Ping, y {nombre} sabe ya que el muchacho no va a volver con él a Samarcanda: se quedará en Chang'an como su agente. No sabe si alegrarse o no. Se alegra, y se le nota menos de lo que quisiera.", desde = "farn_novia", hasta = "kucha"),
        v("Farn, su hijo, se quedó en Chang'an como su agente, en casa de Shi Ping, de cuya hija está enamorado. Le escribe cartas largas, de comerciante: precios, nombres, chismes. Al final de cada una dice que extraña a su madre. A su padre no lo dice.", desde = "kucha", hasta = "boda_farn"),
        v("Farn, su hijo, está casado con la hija de Shi Ping y vive en Chang'an, como un chino más, aunque sigue siendo de Samarcanda. Lleva bien el negocio. {nombre} piensa que así es como los sogdianos se quedan en todas partes: un hijo cada vez.", desde = "boda_farn"),
        v("Su hija, Nanai-dhat, está en edad de casarse, y Takhsich le busca marido entre los hijos de los mercaderes de Samarcanda. {nombre} quiere estar en la boda. Le lleva de regalo, en un fardo aparte, la seda más fina que encuentre.", hasta = "boda_hija"),
        v("Su hija, Nanai-dhat, está casada con el hijo de otro mercader de Samarcanda y vive en su casa nueva, a dos calles de la de su madre. Escribe poco, porque está ocupada. {nombre} la imagina mandando en su casa, como su madre.", desde = "boda_hija", hasta = "nieto"),
        v("Tiene un nieto en Samarcanda, el hijo de Nanai-dhat, que lleva su nombre, o casi. Lo ha visto poco. Lo lleva en la cabeza todo el camino, y a cada caravana que va al oeste le da un juguete para él: un caballito de barro, una campanilla.", desde = "nieto"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del oficio: de su padre, que lo supo del suyo."),
        v("Hay cosas que solo se miran cuando se pierden. {dato_objeto}"),
        v("{dato_objeto} En el camino se dice que un mercader vale lo que vale lo que lleva, y que lo que lleva vale lo que dice él."),
        v("{dato_objeto} Son cosas que se aprenden en el camino, como casi todo."),
        v("Un mercader lleva pocas cosas suyas, porque todo lo demás es mercancía. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("seda", "la seda", "un rollo de seda", "La seda",
            "La seda se hace con el hilo de un gusano que solo los chinos saben criar, y se vende en rollos de un largo fijo que en China sirven también de moneda: se paga con ellos a los soldados, los impuestos y las deudas; en Persia y en Bizancio vale su peso en plata."),
        ObjetoCuento("monedas", "las monedas", "unas monedas", "Las monedas",
            "Las monedas que lleva son de dos clases: las chinas, de cobre, redondas con un agujero cuadrado, que se ensartan en cordones de mil; y las de plata del oeste, con la cara de reyes que ya no existen; en el camino valen las dos, y ninguna vale lo mismo en dos ciudades."),
        ObjetoCuento("almizcle", "el almizcle", "una bolsa de almizcle", "El almizcle",
            "El almizcle sale de una bolsa que tiene en la panza un ciervo pequeño de las montañas, sin cuernos y con colmillos; huele tan fuerte que un poco perfuma una casa durante años, y vale más que el oro a igual peso."),
        ObjetoCuento("vidrio", "el vidrio", "un vaso de vidrio", "El vidrio",
            "El vidrio del oeste, de Persia y de Siria, es casi desconocido en China, donde lo pagan como piedra preciosa; un vaso de vidrio tallado viaja envuelto en lana y paja durante medio año, y si llega entero, vale un camello."),
        ObjetoCuento("vino", "el vino", "un odre de vino", "El vino",
            "El vino de uva viaja en odres de piel o en tinajas, y en China es un lujo de bárbaros que les gusta a los poetas; los sogdianos lo hacen en sus valles desde siempre y lo beben en cuencos de plata, sin agua cuando nadie mira."),
        ObjetoCuento("carta", "la carta", "una carta", "La carta",
            "Una carta de mercader se escribe en papel, se dobla, se ata con un hilo y se mete en una funda de seda con el nombre de quien la recibe; viaja de mano en mano, de caravana en caravana, y a veces llega, y a veces se queda para siempre en un caravasar."),
        ObjetoCuento("balanza", "la balanza", "una balanza", "La balanza",
            "La balanza de mercader es una barra de hueso o de madera con marcas, un platillo colgado de un lado y una pesa que se corre por el otro; con ella se pesa la plata, el almizcle, la pimienta, y se discute más por la balanza que por el precio."),
        ObjetoCuento("campana", "la campana", "una campana de camello", "La campana",
            "La campana de camello es de bronce, grande y de sonido hondo, y se cuelga al cuello del primero de la fila; los camellos de detrás la siguen sin ver, como los niños a una canción, y en una tormenta de arena es lo único que mantiene junta a la caravana."),
        ObjetoCuento("laud", "el laúd", "un laúd", "El laúd",
            "El laúd de los sogdianos tiene el cuerpo en forma de pera y el mástil doblado hacia atrás, con cuatro cuerdas que se tocan con una púa grande; los chinos lo aprendieron de ellos, y en las tabernas de Chang'an lo tocan muchachas de Samarcanda."),
        ObjetoCuento("sello", "el sello", "un sello", "El sello",
            "El sello de mercader es una piedra tallada con un dibujo propio, que se aprieta en barro o en cera sobre los fardos y sobre las cartas para que se sepa que son suyos y que nadie los abrió; perder el sello es como perder el nombre."),
        ObjetoCuento("fieltro", "el fieltro", "una manta de fieltro", "El fieltro",
            "El fieltro se hace con lana apretada, mojada y pisada durante horas, sin tejer; no deja pasar el viento ni el agua, y de él se hacen las tiendas, las mantas, los gorros, las botas y los tapetes de la estepa."),
        ObjetoCuento("te", "el té", "un puñado de té", "El té",
            "El té de los chinos viene en tortas de hojas prensadas que se rompen, se tuestan y se hierven con sal, a veces con jengibre o con leche; en el camino se bebe caliente para el frío y para el cansancio, y cada pueblo lo hace a su manera."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el camino como se piensa en el mar: no tiene dueño, todos lo usan y a veces se traga a alguno. Le ha dado todo lo que tiene. A sus {edad} años, no sabe vivir sin él, y no sabe si eso es bueno o malo."),
        v("Piensa a veces en las ciudades del camino, que son de pueblos distintos, con reyes distintos, dioses distintos y monedas distintas, y que todas lo dejan pasar porque lleva algo que quieren. Él no es de ninguna. Es de Samarcanda, que es de todas."),
        v("{mundo} Él piensa en cosas más cercanas: en el precio de la seda, en el camello que cojea, en la carta que no llega. Las cosas grandes son de los reyes y los generales, que las piensan sin saber lo que cuesta un camello."),
        v("{mundo} A sus {edad} años, {nombre} ha cruzado el desierto muchas veces, y ha visto morir a gente en él, de sed, de frío y de flechas. No se acostumbra. Cada vez que sale de una ciudad hacia el desierto, piensa en ellos un momento, y luego sigue."),
        v("Piensa en las lenguas que habla, que son como casas: en sogdiano reza y cuenta, en persa regatea, en turco da órdenes, en chino sonríe. A veces sueña en una que no sabe cuál es, y al despertarse no se acuerda de lo que dijo."),
        v("{mundo} A veces, de noche, en el desierto, mirando las estrellas, que son las mismas en Samarcanda y en Chang'an, {nombre} piensa que el mundo es mucho más pequeño de lo que creen los que no se mueven, y mucho más grande de lo que creen los que sí."),
    )

    private val MUNDOS = mapOf(
        "abasies" to listOf(
            "En los caravasares se habla de los abasíes, que vencieron a los omeyas en el oeste, y de qué cambiará para los mercaderes.",
            "Se dice que el general chino Gao Xianzhi anda por el oeste con su ejército, y nadie sabe para qué.",
            "Se habla del precio de la seda en Chang'an, que sube.",
        ),
        "talas" to listOf(
            "Se habla de la batalla de Talas, entre árabes y chinos, y de quién mandará ahora en el camino.",
            "Se dice que en Samarcanda unos prisioneros chinos hacen papel, y que el papel de allá saldrá más barato.",
        ),
        "farn" to listOf(
            "Se habla de los sogdianos de Chang'an, que son cada vez más, y de los que se casan con chinas.",
            "Se dice que el emperador está viejo y que manda su favorita, y la familia de ella.",
        ),
        "tibet" to listOf(
            "Se habla de la guerra de los chinos contra el Tíbet, al sur del camino, que va bien para los chinos.",
            "Se dice que los bandidos andan sueltos en el desierto, y que hay que viajar con más guardias.",
        ),
        "an_lushan" to listOf(
            "Se habla del general An Lushan, que es medio sogdiano, medio turco, gordo como un buey y querido por el emperador.",
            "Se dice que la capital está más llena que nunca, y que no cabe nadie más.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde llega la caravana a un caravasar, y el patio se llena de animales, de gritos y de polvo. {nombre} descarga, cuenta los fardos, paga al encargado y busca un cuarto. Luego se sienta junto al fuego, por fin, y se quita las botas."),
        v("Anochece en el desierto, y el cielo se pone de todos los colores y luego negro, de golpe, y salen tantas estrellas que no se ve el negro. {nombre} las mira desde la puerta del caravasar. El frío baja enseguida. Se mete dentro."),
        v("Se cena en el patio del caravasar, alrededor del fuego, con los mercaderes de otras caravanas: carne de cordero, pan, cebolla, vino. Se habla de precios, de caminos y de mujeres, en este orden, y luego en el orden contrario. {nombre} se va a dormir antes que nadie."),
        v("De noche, en el caravasar, se cierra la puerta grande con una tranca, y el mundo de fuera se queda fuera: los lobos, los bandidos, el frío. Dentro hay fuego, voces y animales. {nombre} piensa que un caravasar es lo más parecido a una casa que tiene."),
        v("La última luz del día se queda un momento en lo alto de las montañas nevadas, roja, cuando el camino ya está a oscuras. {nombre} la ve desde el lomo de un camello, llegando tarde al caravasar. Tughan canta algo en turco para que los camellos no se duerman."),
        v("Al caer la tarde el frío del camino es tan fuerte que la barba se le hiela con el aliento. {nombre} llega al caravasar con las manos que no siente, y las mete en el fuego casi hasta quemarse. Esta noche nadie bebe agua: bebe vino caliente.", meses = setOf(11, 12, 1, 2)),
        v("Al anochecer se levanta viento del desierto y el polvo entra en el caravasar por todas partes. Se cena con la boca tapada, se duerme con la cara tapada. {nombre} tiene arena en los dientes, en las orejas y en la carta que escribía.", meses = setOf(3, 4, 10)),
    )

    // =====================================================================
    //  El verano en las ciudades (estados «changan» y «samarcanda»)
    // =====================================================================

    private fun ch(texto: String, hacer: Hacer? = null) = v(texto, estado = "changan", hacer = hacer)
    private fun sa(texto: String, hacer: Hacer? = null) = v(texto, estado = "samarcanda", hacer = hacer)

    private val CHANGAN = listOf(
        ch("{nombre} se despierta en la casa de Shi Ping, en el barrio de junto al mercado del oeste, con los tambores de la mañana: en Chang'an los barrios tienen muros y puertas, que se cierran de noche y se abren con los tambores. Suenan cientos, por toda la ciudad, uno detrás de otro."),
        ch("A {nombre}, al que aquí llaman Kang, lo despierta el ruido de la calle del barrio: vendedores de pan con sésamo, un monje que pide, un carro de agua, niños. Se lava en el patio, se pone ropa china, que le queda rara, y sale. En Chang'an hay que parecer de Chang'an."),
        ch("{nombre} sale temprano al patio de Shi Ping, donde hay un granado y una pila de piedra, y toma té con su anfitrión, que ya está haciendo cuentas. Hablan en sogdiano, para que los criados no entiendan. Los criados son sogdianos también, y entienden."),

        ch("A esta hora está en el mercado del oeste, en la tienda de Shi Ping, comprando seda: rollos y rollos que se desenrollan, se tocan, se miden, se huelen. Los vendedores chinos juran por sus antepasados que es la mejor. {nombre} jura por los suyos que es la peor. Así se empieza.", Hacer.CAMINO),
        ch("A esta hora recorre las calles del mercado del oeste, que son una ciudad dentro de la ciudad, con sus filas de tiendas de cada cosa: la de la seda, la de los caballos, la de las medicinas, la de las joyas, la del vino. Se oyen todas las lenguas del mundo. {nombre} las oye todas.", Hacer.CAMINO),
        ch("A esta hora vende lo que trajo del oeste: el vidrio, el almizcle, la plata, los tapetes. Los compradores chinos tocan el vidrio como si fuera a deshacerse. {nombre} les cuenta de dónde viene, alargando el camino un poco, porque el camino también se cobra.", Hacer.CAMINO),
        ch("A esta hora come tortas con sésamo y una sopa de cordero del puesto de la esquina del barrio, que abre antes que nadie y cierra después que todos. El vendedor es de Samarcanda, le habla en sogdiano y le sirve más que a los chinos.", Hacer.COMER),
        ch("A esta hora come en casa de Shi Ping, a la manera china, sentado en una esterilla ante una mesa baja, con palillos que todavía le cuestan: arroz, pescado, verduras, carne de cerdo. Shi Ping se ríe de él. La mujer de Shi Ping le da una cuchara, por compasión.", Hacer.COMER),
        ch("A esta hora come un cuenco de fideos con vinagre y ajo, que es lo que comen los de Chang'an, sentado en el patio de Shi Ping con un criado que le enseña a sorberlos sin mancharse. No aprende. A su lado, un huésped persa se ríe de él en persa, y él le contesta en persa.", Hacer.COMER),
        ch("A esta hora va con los sogdianos de Chang'an al templo del fuego del barrio, una sala pequeña donde arde un fuego que no se apaga nunca, cuidado por un sacerdote con la boca tapada. Se reza, se habla de negocios y se reparten noticias de casa.", Hacer.GENTE),
        ch("A esta hora está con Shi Ping y otros mercaderes en la casa de uno de ellos, bebiendo vino y escuchando a una muchacha que toca el laúd. Se habla de la corte, de los precios y de los generales. Antes de que suenen los tambores de la noche hay que estar en el barrio de cada uno.", Hacer.GENTE),
        ch("A esta hora escribe cartas en el patio de Shi Ping, a la luz de una lámpara: a Takhsich, a sus socios de Samarcanda, a un mercader de Liangzhou. Las meterá en la bolsa de una caravana que sale pronto hacia el oeste. Alguna llegará.", Hacer.GENTE),
        ch("A esta hora duerme en el cuarto de huéspedes de Shi Ping, sobre una cama de verdad, que le parece blanda como una nube y en la que le cuesta dormirse. Fuera, en la calle del barrio cerrado, pasa la ronda nocturna. En Chang'an, de noche, no se sale.", Hacer.DORMIR),
        ch("A esta hora duerme con la ventana abierta por el calor, y entran los mosquitos y el ruido de las cigarras, que en Chang'an no se callan nunca en verano. {nombre} sueña con el desierto, que de noche, por lo menos, es fresco.", Hacer.DORMIR),

        ch("Chang'an es la ciudad más grande que existe: un rectángulo inmenso de murallas, con avenidas más anchas que muchos ríos, cien barrios cerrados con muros y puertas, dos mercados, el palacio del emperador al norte y templos por todas partes. Aquí se acaba el camino."),
        ch("El mercado del oeste es el de los extranjeros: sogdianos, persas, turcos, uigures, árabes, indios, gente de lugares que {nombre} no sabe dónde están. Abre al mediodía con el sonido de los tambores y cierra antes del anochecer con el de los gongs. Lo que no se vende hoy, mañana."),
        ch("En el barrio de los sogdianos de Chang'an hay un templo del fuego, tabernas con vino de uva, tiendas de joyas y casas con patios donde se habla sogdiano. Los niños hablan chino entre ellos, y sogdiano con sus abuelos. Dentro de dos generaciones, piensa {nombre}, solo chino."),

        ch("Al caer la tarde suenan los tambores de la noche por toda la ciudad, y la gente corre para llegar a su barrio antes de que cierren las puertas. {nombre} llega a tiempo, por poco. Dentro del barrio la vida sigue: tabernas, lámparas, música. Fuera, las avenidas se quedan vacías."),
        ch("Anochece sobre Chang'an, y desde el patio de Shi Ping se ven las pagodas recortadas contra el cielo rojo, y se oyen las campanas de los templos. {nombre} se sienta con un vaso de vino. Esta ciudad, piensa, se puede querer, aunque no sea suya."),
        ch("Se cena tarde en casa de Shi Ping, con toda la familia, los criados y algún huésped, en el patio, por el calor. La hija de Shi Ping sirve el vino. Se habla en sogdiano y en chino, mezclados. {nombre} come demasiado, como siempre en Chang'an."),

        ch("En Chang'an el cielo se ve poco, entre los tejados y los árboles de las avenidas, y el aire es pesado de verano. {cielo} {de_estacion}"),
        ch("{cielo} {de_estacion} En la capital el tiempo le importa poco: lo que importa son los precios, y los precios no dependen del cielo."),
    )

    private val SAMARCANDA = listOf(
        sa("{nombre} se despierta en su casa de Samarcanda, en su cama, al lado de Takhsich, y tarda un momento en creérselo. Por la ventana entra el ruido del agua en la acequia del patio y el olor de los albaricoques. Takhsich ya está despierta, mirándolo, como si contara si está entero."),
        sa("A {nombre} lo despiertan los criados de la casa, que barren el patio y riegan las flores. Se lava en la fuente, se pone ropa de casa, limpia y blanda, y desayuna pan caliente con miel y leche cuajada. Después de medio año de camino, lo de todos los días le parece una fiesta."),
        sa("{nombre} sale temprano a la azotea de su casa a mirar Samarcanda: los tejados planos, las cúpulas de los templos, la ciudadela en lo alto, los jardines, las montañas al sur. Las golondrinas pasan gritando. Es su ciudad, y la mira como se mira a alguien que no se veía hace mucho."),

        sa("A esta hora está en el mercado de Samarcanda, vendiendo la seda que trajo del este: los mercaderes de Persia y de Bizancio la tocan, la miden, la regatean. Él sabe lo que costó, en dinero y en camino. Lo dice en persa, despacio, y no baja el precio.", Hacer.CAMINO),
        sa("A esta hora hace cuentas con sus socios en la sala de su casa, la que tiene las paredes pintadas con cazadores y banquetes: lo que se gastó, lo que se ganó, lo que se perdió con el camello muerto. Las cuentas se escriben en sogdiano, de derecha a izquierda, y se discuten en voz alta.", Hacer.CAMINO),
        sa("A esta hora compra lo que llevará al este en otoño: plata, vidrio, tapetes, almizcle de las montañas, vino en tinajas. Cada cosa se elige con cuidado, pensando en el camino, en el peso y en lo que pedirán en Chang'an. Comprar bien es la mitad del viaje.", Hacer.CAMINO),
        sa("A esta hora come en su casa, en la sala pintada, sentado en tapetes con la familia: cordero asado, pan del horno, arroz con zanahorias, melones que se deshacen en la boca y uvas. Takhsich le sirve lo mejor sin decir nada. Él se lo come sin decir nada. Así se dicen las cosas.", Hacer.COMER),
        sa("A esta hora come pan del horno, recién salido, con queso, nueces y uvas, sentado en la azotea con Takhsich, mirando los tejados de Samarcanda. En el camino come mirando el horizonte; aquí mira el plato, y a ella. {nombre} piensa que esto es lo que se lleva en la memoria cuando se va.", Hacer.COMER),
        sa("A esta hora come fruta en el jardín de su casa, bajo la parra, sin hambre, solo porque está ahí: albaricoques, melocotones, granadas. En el camino la fruta es un sueño. {nombre} se come seis albaricoques seguidos y Takhsich se ríe de él.", Hacer.COMER),
        sa("A esta hora está en el templo del fuego de Samarcanda, el grande, donde arde el fuego más viejo de la ciudad, con su familia, para dar gracias por haber vuelto entero. El sacerdote, con la boca tapada, le echa sándalo al fuego. {nombre} reza por los que siguen en el camino.", Hacer.GENTE),
        sa("A esta hora visita a los parientes, que son muchos, y en cada casa le dan vino y le preguntan por el camino, por Chang'an, por los precios. En cada casa cuenta lo mismo, un poco más grande. Al final del día, el camino es el doble de largo y el doble de peligroso.", Hacer.GENTE),
        sa("A esta hora se sienta en el patio con Takhsich, al fresco, con un vaso de vino, y se cuentan lo que pasó en medio año. Ella le cuenta más que él, y mejor. Él escucha. Es lo único que hace bien en casa, dice ella.", Hacer.GENTE),
        sa("A esta hora duerme en su casa, en la azotea, porque en verano en Samarcanda se duerme arriba, bajo las estrellas, sobre tapetes. Se oyen las otras familias en las otras azoteas, hablando bajito. {nombre} se duerme enseguida, como no se duerme en ningún caravasar.", Hacer.DORMIR),
        sa("A esta hora duerme hondo, en su cama, con Takhsich al lado y el ruido de la acequia en el patio. Sueña con el camino, y en el sueño se pierde, y se despierta asustado. Takhsich le pone la mano en el pecho sin despertarse. Se vuelve a dormir.", Hacer.DORMIR),

        sa("Samarcanda está en un valle verde, entre montañas, regada por mil acequias que sacan el agua del río y la llevan a cada huerto, a cada patio, a cada calle. Tiene murallas, una ciudadela en lo alto y mercados donde se vende todo lo del mundo. Es la ciudad de los sogdianos."),
        sa("En Samarcanda mandan ahora los árabes, que tienen su mezquita y su gobernador, pero la ciudad sigue siendo de los sogdianos: sus templos del fuego, sus casas pintadas, sus mercaderes, su vino. Los árabes cobran impuestos. Los sogdianos los pagan y siguen comerciando."),
        sa("Las casas de los mercaderes de Samarcanda tienen una sala grande con las paredes pintadas de arriba abajo: cacerías, banquetes, héroes de las historias, dioses. La de {nombre} tiene un rey que caza leones montado en un camello. Su padre la mandó pintar, con el dinero de un viaje."),

        sa("Al caer la tarde {nombre} sale a pasear por las calles de Samarcanda con Takhsich, que no lo suelta del brazo, y saluda a los vecinos, que le preguntan cuándo se va. Les dice que nunca. Nadie se lo cree, y él tampoco."),
        sa("Anochece sobre Samarcanda, y desde la azotea se ve el humo de las cocinas, se oyen las campanas de los camellos que llegan y el canto de un sacerdote. {nombre} bebe vino de su valle, que es mejor que todos los vinos del camino, y no piensa en nada."),
        sa("Se cena en el patio, junto a la acequia, con lámparas, con la familia entera y algún vecino que se apunta. Se come, se bebe y se canta. {nombre} cuenta la historia del camello muerto por tercera vez esta semana, y Takhsich lo deja, porque es verano."),

        sa("En Samarcanda el cielo es alto y limpio, con las montañas nevadas al sur, siempre. {cielo} {de_estacion}"),
        sa("{cielo} {de_estacion} En casa, el tiempo es lo que le pasa al huerto, no lo que le pasa a uno."),
    )

    /**
     * Separa las variantes de una ciudad por párrafo: las que tienen actividad son del trabajo; las demás van en el orden
     * en que están escritas: tres mañanas, tres pueblos, tres anocheceres y dos tiempos.
     */
    private fun de(lista: List<Variante>, lugar: Parrafo): List<Variante> {
        val otras = lista.filter { it.hacer == null }
        check(otras.size == 11) { "Cada ciudad lleva 3 mañanas, 3 pueblos, 3 anocheceres y 2 tiempos" }
        return when (lugar) {
            Parrafo.TRABAJO -> lista.filter { it.hacer != null }
            Parrafo.MANANA -> otras.subList(0, 3)
            Parrafo.PUEBLO -> otras.subList(3, 6)
            Parrafo.ANOCHECER -> otras.subList(6, 9)
            Parrafo.TIEMPO -> otras.subList(9, 11)
            else -> emptyList()
        }
    }
    private fun ciudades(lugar: Parrafo) = de(CHANGAN, lugar) + de(SAMARCANDA, lugar)

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "ano_chino" to TextoEspecial(
            listOf(
                dia(
                    Parrafo.MANANA to "Hoy es el año nuevo de los chinos, y la caravana se queda en el caravasar de un pueblo del camino, porque los chinos no trabajan y los caminos están vacíos. En las puertas han pegado tablillas de madera de melocotonero contra los malos espíritus. Se oyen petardos de bambú.",
                    Parrafo.PUEBLO to "En el pueblo se queman cañas de bambú, que revientan con estruendo para espantar a los demonios, y la gente sale a la calle con ropa nueva, se inclina ante los viejos y se desea larga vida. Los soldados del fuerte están borrachos desde la mañana.",
                    Parrafo.GENTE to "Esta noche el encargado del caravasar, que es chino, invita a los mercaderes a beber el vino de año nuevo, con hierbas, que se bebe empezando por el más joven y terminando por el más viejo. {nombre} bebe de los últimos. Le desean larga vida. Él les desea buenos precios.",
                ),
                dia(
                    Parrafo.MANANA to "Hoy sigue la fiesta del año nuevo de los chinos, y la caravana sigue quieta en el caravasar. {nombre} aprovecha para remendar las cinchas, revisar los fardos y dormir. Fuera se oyen todavía petardos de bambú, más flojos, y niños que corren.",
                ),
            ),
        ),
        "linternas" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de las linternas, la primera luna llena del año de los chinos, y en el oasis donde para la caravana todos preparan farolillos de papel y de seda para la noche. Los monjes del templo de Buda cuelgan cientos en los árboles. {nombre} compra uno, rojo, con un pez pintado.",
            Parrafo.PUEBLO to "Al caer la noche se encienden las linternas en todo el oasis: en las puertas, en los árboles, en las torres del templo, en las manos de la gente. Por una noche no hay toque de queda, y todo el mundo pasea, mira y se mira. La luna llena sale grande y amarilla.",
            Parrafo.ANOCHECER to "Esta noche {nombre} pasea con su linterna roja entre la gente del oasis, con Tughan detrás, que no ha visto nunca nada igual y no dice nada. Luego cuelga la linterna en la puerta del caravasar. Mañana se sale con la primera luz. Esta noche, no se duerme.",
        ),
        "tormenta_negra" to unDia(
            Parrafo.MANANA to "Hoy se levantó una tormenta de arena negra en el desierto, de las que tapan el sol y vuelven el día noche: un muro oscuro que se acercó por el horizonte y se tragó la caravana. Los camelleros gritaron, los camellos se echaron al suelo, y todos se acurrucaron detrás.",
            Parrafo.TRABAJO to "A esta hora está tirado en la arena detrás de un camello echado, con la cara tapada con un trapo mojado, sin ver nada, oyendo solo el viento y la campana del camello de delante, que suena loca. No se puede hacer nada más que esperar. {nombre} espera.",
            Parrafo.ANOCHECER to "Al caer la tarde la tormenta se va tan de golpe como llegó, y la caravana se levanta, cubierta de arena, a contarse: falta un burro y un fardo de pimienta. Todos los hombres están. {nombre} da gracias a todos los dioses que conoce, uno por uno.",
        ),
        "duanwu" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta del barco dragón, el quinto día de la quinta luna, y en Chang'an se cuelgan hierbas en las puertas contra las enfermedades del verano y se comen bolitas de arroz pegajoso envueltas en hojas. Shi Ping le da a {nombre} una docena.",
            Parrafo.PUEBLO to "En el gran estanque del sureste de la ciudad compiten los barcos dragón, largos, estrechos, con cabeza de dragón, remados por veinte hombres al ritmo de un tambor. Las orillas están llenas de gente que grita y apuesta. Hay vendedores de todo. Es la ciudad entera.",
            Parrafo.PENSAMIENTO to "{nombre} pregunta por qué se hace la fiesta, y le cuentan de un poeta antiguo que se tiró a un río por tristeza, y de que los barcos buscan su cuerpo, y las bolitas de arroz son para que los peces no se lo coman. Le parece una historia rara para una fiesta tan alegre.",
        ),
        "qixi" to unDia(
            Parrafo.MANANA to "Hoy es la noche del siete de la séptima luna, cuando la tejedora del cielo y el pastor se encuentran una sola vez al año sobre un puente de urracas. En la casa de Shi Ping, las mujeres preparan una mesa en el patio con fruta, flores y agujas.",
            Parrafo.PUEBLO to "Esta noche las muchachas de Chang'an salen a los patios a enhebrar agujas a la luz de la luna, y a pedirle a la tejedora habilidad para coser y un buen marido. Por toda la ciudad hay risas en los patios y gente mirando al cielo, buscando las dos estrellas.",
            Parrafo.ANOCHECER to "Esta noche Shi Ping le enseña a {nombre} las dos estrellas, una a cada lado del río del cielo, que nunca se juntan salvo hoy. {nombre} se acuerda de Takhsich, al otro lado del mundo, y piensa que ellos tampoco se juntan más que una vez al año, o menos.",
        ),
        "zhongqiu" to unDia(
            Parrafo.MANANA to "Hoy es la luna llena de la mitad del otoño, la más grande y clara del año, y en Chang'an la gente la espera para mirarla desde los tejados y los jardines, con vino y fruta. {nombre} prepara su salida hacia el oeste, que ya está cerca, pero esta noche no.",
            Parrafo.PUEBLO to "Al caer la noche Chang'an sube a los tejados: familias enteras, poetas, monjes, borrachos, todos mirando la luna que sale enorme y amarilla sobre las murallas. Se recitan poemas, se bebe, se cuentan historias de la diosa que vive en la luna con un conejo.",
            Parrafo.ANOCHECER to "Esta noche {nombre} sube al tejado de Shi Ping con un vaso de vino y mira la luna, que es la misma que verá desde el desierto dentro de unos días, y desde Samarcanda en medio año. Le da tranquilidad que la luna viaje con él. No le cobra nada.",
        ),
        "camello" to unDia(
            Parrafo.MANANA to "Hoy se le murió un camello en el desierto, el más viejo, el que llevaba la plata: se echó en la arena a media mañana y ya no quiso levantarse. Tughan le habló en su lengua, le tiró de la cuerda, lo acarició. El camello cerró los ojos.",
            Parrafo.TRABAJO to "A esta hora reparte la carga del camello muerto entre los otros, que gruñen y protestan, y deja atrás lo que no cabe: dos tinajas de vino, que se beberán esta noche para que no se pierdan. La plata, toda, se la cuelga del suyo. Es un día malo.",
            Parrafo.PENSAMIENTO to "{nombre} mira el camello muerto, que ya está rodeado de buitres en el cielo, y piensa que ese camello lo llevó de Samarcanda a Chang'an cuatro veces. Le debe más que a mucha gente. Le dice algo en voz baja antes de irse. Tughan llora sin esconderse.",
        ),
        "abasies" to unDia(
            Parrafo.MANANA to "Hoy llegó con una caravana del oeste una noticia grande: en el país de los árabes, los de la bandera negra, los abasíes, vencieron a los omeyas y mataron a su califa, y ahora mandan ellos. En el caravasar no se habla de otra cosa.",
            Parrafo.GENTE to "Esta noche los mercaderes discuten junto al fuego qué cambiará: unos dicen que los abasíes son de Jorasán, cerca de Sogdiana, y que serán mejores; otros, que todos los califas cobran igual. {nombre} escucha y piensa en Samarcanda, que está en medio de todo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Takhsich y en los niños, en Samarcanda, a medio año de camino, y en lo que estará pasando allá, que él sabrá cuando ya haya pasado. Así es el camino: todo llega tarde, las noticias, las cartas, él mismo. Mañana, a seguir.",
        ),
        "llega_changan" to unDia(
            Parrafo.MANANA to "Hoy la caravana llegó por fin a Chang'an, después de meses de camino: primero se vieron las murallas, largas como montañas, y luego la puerta del oeste, enorme, con soldados que revisan los papeles. {nombre} se sacude el polvo del caftán antes de entrar. Hay que llegar bien.",
            Parrafo.PUEBLO to "Dentro de las murallas, la avenida es más ancha que el río que acaban de cruzar, y está llena de gente, de caballos, de sillas de manos, de monjes y de soldados. Los camellos de la caravana, que vienen del desierto, se asustan del ruido. Tughan también, aunque no lo dice.",
            Parrafo.GENTE to "En el barrio de junto al mercado del oeste lo espera Shi Ping en la puerta de su casa, con los brazos abiertos y la barriga por delante, y lo abraza a la manera sogdiana, y luego se inclina a la manera china. Le ha guardado el mejor cuarto. Le ha guardado vino.",
        ),
        "compra_seda" to unDia(
            Parrafo.MANANA to "Hoy {nombre} va con Shi Ping al mercado del oeste a comprar la seda de Sichuan, la del sur, que es la mejor que llega a Chang'an: tejida con dibujos de pájaros y flores, en colores que no existen en ningún otro lugar. Lleva la plata en el cinturón y la cara de no tener prisa.",
            Parrafo.TRABAJO to "A esta hora regatea con el vendedor de seda desde hace dos horas, con té, con sonrisas, con amenazas de irse a otra tienda, con elogios a sus antepasados. El vendedor baja un poco. {nombre} sube un poco. Shi Ping se aburre y se va a comer. Al final, se cierra el trato.",
            Parrafo.PENSAMIENTO to "{nombre} toca la seda que compró, fría y suave como el agua, y piensa en el camino que le espera: medio año, desiertos, montañas, bandidos, aduanas. Esta seda vale aquí lo que vale. En Samarcanda valdrá tres veces más. En Bizancio, diez. El camino es lo que se paga.",
        ),
        "carta_casa" to unDia(
            Parrafo.MANANA to "Hoy {nombre} se sentó en el patio de Shi Ping a escribirle una carta larga a Takhsich, en papel chino, con tinta china y letras sogdianas. Le cuenta del camino, del camello muerto, de la seda, de Chang'an. Sabe que tardará un año en llegar, si llega.",
            Parrafo.GENTE to "En la carta le pregunta a Takhsich por la casa, por los niños, por las deudas y por la viña. Le dice que la extraña, de la manera en que se dice eso en una carta que van a leer muchos por el camino: con una frase sobre el vino de casa, que ella entenderá.",
            Parrafo.PENSAMIENTO to "{nombre} dobla la carta, la ata con un hilo y la mete en una funda de seda con el nombre de Takhsich, y la sella. Piensa que cuando ella la lea, él ya estará en el camino de vuelta, y que la carta y él se cruzarán sin verse, en algún caravasar.",
        ),
        "tashkent" to unDia(
            Parrafo.MANANA to "Hoy llegó a Chang'an una noticia del oeste: el general chino Gao Xianzhi tomó Tashkent, la ciudad de los sogdianos del norte, a traición, después de firmar la paz con su rey, y saqueó su tesoro y se llevó al rey preso a la capital. En el barrio de los sogdianos no se habla de otra cosa.",
            Parrafo.GENTE to "Shi Ping, que es de Tashkent, no ha dicho nada en todo el día. Esta noche bebe solo en el patio. {nombre} se sienta a su lado y no dice nada tampoco. Al final Shi Ping le dice que su hermano vive allá. {nombre} le sirve más vino.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los sogdianos siempre están en medio: entre los chinos y los árabes, entre los turcos y los persas, haciendo negocios con todos y sirviendo a todos. Y que cuando los grandes se pelean, los de en medio son los primeros que pagan.",
        ),
        "torbellino" to unDia(
            Parrafo.MANANA to "Hoy {nombre} fue con Shi Ping a una taberna sogdiana del mercado del oeste, de las que tienen música y bailarinas, porque Shi Ping dijo que había una muchacha de Samarcanda que bailaba la danza del torbellino como nadie. {nombre} no quería ir. Fue.",
            Parrafo.GENTE to "La muchacha bailó sobre una alfombra redonda, girando sobre sí misma tan rápido que su falda se volvía una flor y no se le veía la cara, al ritmo de los tambores y del laúd. Los chinos de la taberna gritaban. {nombre} no gritó: se acordó de las fiestas de su ciudad.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la muchacha baila en Chang'an lo que se baila en Samarcanda, y que los chinos creen que es una danza de aquí, de la capital, de moda. Así viajan las cosas: un baile, un laúd, un vino. Detrás de cada una hay un sogdiano que las trajo.",
        ),
        "sale_changan" to unDia(
            Parrafo.MANANA to "Hoy la caravana salió de Chang'an hacia el oeste, por la misma puerta por la que entró, con los camellos cargados de seda hasta las orejas. {nombre} se despidió de Shi Ping en la puerta de su casa y no miró atrás, porque mirar atrás trae mala suerte en el camino.",
            Parrafo.PUEBLO to "Fuera de la puerta del oeste hay un puente sobre un río, y una costumbre china: los que se despiden rompen una rama de sauce y se la dan al que se va. Hay sauces pelados de tantas despedidas. A {nombre} alguien le da una rama, y la guarda.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el camino que empieza: medio año hasta Samarcanda, si todo va bien. Piensa en lo que puede pasar, y deja de pensarlo, porque no sirve. Lo único que sirve es caminar. Mira la rama de sauce, que ya se está secando.",
        ),
        "mogao" to unDia(
            Parrafo.MANANA to "Hoy la caravana llegó a Dunhuang, el último oasis grande antes del desierto, y {nombre} fue a las cuevas de los mil budas, excavadas en un acantilado de tierra, para encender una lámpara y pedir un buen viaje. Todos los mercaderes lo hacen, sean de la religión que sean.",
            Parrafo.PUEBLO to "Las cuevas son cientos, una al lado de otra y unas encima de otras, en la pared del acantilado, y dentro están pintadas de arriba abajo con budas, con cielos, con músicos que vuelan, con mercaderes que hacen lo mismo que él: encender lámparas. Algunas las pagaron sogdianos.",
            Parrafo.PENSAMIENTO to "{nombre} enciende su lámpara de aceite ante un buda enorme que sonríe con los ojos cerrados, y no sabe bien qué pedirle, porque no es su dios. Le pide lo mismo que a los suyos: volver. El buda no dice nada, pero parece que lo oyó.",
        ),
        "nieve_paso" to unDia(
            Parrafo.MANANA to "Hoy empezó a nevar en el paso, tanto que el jefe de la caravana decidió no salir del caravasar hasta que pare. La nieve tapa la puerta, el patio, los camellos echados. El mundo se ha quedado blanco y callado. Hay que esperar, y esperar cuesta dinero.",
            Parrafo.TRABAJO to "A esta hora está en el cuarto del caravasar, junto al brasero, contando lo que cuesta cada día de espera: la comida de los hombres, el forraje de los camellos, el cuarto. Lo cuenta tres veces. Sale lo mismo. Le duele igual.",
            Parrafo.ANOCHECER to "Esta noche los mercaderes juegan a los dados en el cuarto más grande, por monedas, por fardos, por vino. Hay uno que pierde un camello entero y se ríe. {nombre} no juega: mira y bebe. Fuera sigue nevando. Mañana, quizás, se sale.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "mercader_sogdiano", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + ciudades(Parrafo.MANANA),
            Parrafo.TIEMPO to TIEMPO + ciudades(Parrafo.TIEMPO),
            Parrafo.TRABAJO to TRABAJO + ciudades(Parrafo.TRABAJO),
            Parrafo.PUEBLO to PUEBLO + ciudades(Parrafo.PUEBLO),
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + ciudades(Parrafo.ANOCHECER),
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
