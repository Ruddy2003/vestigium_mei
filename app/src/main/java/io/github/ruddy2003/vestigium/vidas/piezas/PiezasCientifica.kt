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
 * El cuento de la doctora, científica del Claustro, un laboratorio sellado bajo una ciudad en ruinas, de 2287 a 2291.
 * Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su ficha se llama «La doctora»: el cuento escribe «la doctora» a mano y no usa el hueco del nombre.
 * - Bajo tierra no hay cielo ni estaciones: el párrafo del tiempo cuenta el aire, las lámparas y los jardines. Arriba
 *   (estado «arriba», temporada 5) el párrafo del tiempo es del estado y no usa el hueco del cielo, porque esos días el
 *   clima del calendario sigue siendo «sin clima». Los cielos de sol y de lluvia son de la superficie: los traen dos
 *   días de la temporada 5.
 * - Su día tiene dos tramos de laboratorio y dos comidas: esos textos valen a cualquiera de sus horas. Por la tarde, la
 *   mesa del Consejo (el único tramo que puede decir «cena»); por la noche, música.
 * - Cuatro estados: «cuarentena» (temporada 2, encerrada en el ala de Botánica), «experimento» (3, tres semanas en la
 *   campana de cultivo), «cierre» (4, el Claustro cerrado mientras buscan a Lauro) y «arriba» (5, cuatro semanas en la
 *   superficie). Arriba nada habla del sol antes de `sol`, de la lluvia, de Neva antes de `encuentra` ni de Lauro antes
 *   de `lauro_lejos`.
 * - El pueblo lee el arco «jardines» (lo que ha crecido en los tanques; vacíos tras el vaciado de la temporada 2).
 * - Los Semejantes se cuentan por lo que hacen y por lo que les pasa, sin sermones.
 * - No repite lo que ya dice «Su vida»: los veinte años sin sol, los pasillos blancos, las luces que imitan el día, el
 *   destello en vez de caminar, la dieta calculada al gramo, las ocho horas de sueño, las sesenta de trabajo, las
 *   trescientas personas, los asistentes que nadie cuenta.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG8.kt`):
 * - Lauro, su asistente Semejante: desde `lauro` (temporada 1); pregunta desde `pregunta` (1); se va en `lauro_se_va`
 *   (4); lo ve de lejos en `lauro_lejos` (5). Antes de `lauro`, un asistente viejo sin nombre.
 * - Tomé, su hijo: quiere entrar en Mentes desde `tome` (1), entra en `prueba_tome` (2), quiere vivir arriba desde
 *   `tome_arriba` (5). Sube con ella (estado «arriba»).
 * - Hilda, su maestra: en el laboratorio de al lado hasta `hilda_deja` (1), retirada hasta que muere (`hilda`, 3).
 * - Coral, la jefa de los jardines: permiso de hijo en `coral` (2); su hija Alba nace en `alba` (3) y anda desde
 *   `funciona` (4).
 * - Neva, la mujer de arriba: encerrada en Medicina de `neva` a `neva_sube` (2); arriba otra vez desde `encuentra` (5).
 * - El rector Ezequiel Mora hasta `rectora` (3); la rectora Selma Ibarra desde `rectora`.
 * - La decisión del Consejo (`decision`, 4) hasta el acuerdo (`acuerdo`, 5); la mancha verde desde `verde` (4).
 * - Las fiestas y los ritos que vuelven cada año no nombran a Lauro, Tomé, Hilda, Coral, Alba, Neva ni a quien
 *   gobierna el Consejo: dicen «su hijo», «la jefa de los jardines», «el Consejo». Bruno, el ingeniero del destello, sí.
 * - Fiestas de guardar (no trabaja): `anio_nuevo`, `fundacion`, `luz_larga`, `noche_larga`.
 */
internal object PiezasCientifica {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("A la doctora la despiertan las lámparas del cuarto, que pasan del azul de la noche al blanco en un minuto exacto, y la grabación de un pájaro que nadie del Claustro ha oído en vivo. Ella sabe su nombre en latín. No sabe si canta así o si lo grabaron mal."),
        v("La doctora se despierta antes que las lámparas y se queda a oscuras, repasando lo que tiene que medir hoy: raíces, humedad, sales, el peso de tres bandejas. Cuando sube la luz, ya tiene la lista hecha. Se levanta con la sensación de llegar tarde a algo."),
        v("Al despertar, la doctora se mira las uñas: tierra. Se las cepilló anoche, y la tierra sigue ahí, en una raya fina que no se va. Su madre, que también era de Botánica, tenía la misma raya. Se pone la bata. La raya queda escondida en el bolsillo."),
        v("Lo primero que hace la doctora al levantarse es mirar en la tableta los datos de la noche de sus bandejas: temperatura, humedad, cuánto han bebido las raíces. Lo hace sentada en la cama, con un pie en el suelo. Hasta que no ve los números, no pone el otro."),
        v("A la doctora la despierta el agua de los riegos, que corre a primera hora por la pared de su cuarto, camino de los jardines. Suena como dicen los libros que suenan los ríos: con prisa, siempre hacia abajo. Este, además, va por dentro de una pared."),
        v("La doctora se despierta con hambre y con una idea, las dos a la vez, y no sabe a cuál atender primero. Apunta la idea en el cuaderno de la mesilla, con letra de dormida. Se lava la cara con agua tibia. Cuando vuelve a leerla, la idea ya no se entiende."),
    )

    /** El Claustro cerrado por una fiebre, en la temporada 2: ella, encerrada en el ala de Botánica (estado «cuarentena»). */
    private fun cu(texto: String, hacer: Hacer? = null) = v(texto, estado = "cuarentena", hacer = hacer)

    private val CUARENTENA_MANANA = listOf(
        cu("La doctora se despierta y se pone la mano en la frente antes de abrir los ojos, como todo el Claustro desde que cerraron las compuertas. Fresca. Se pone la mascarilla, que huele a la goma de anoche, y sale al pasillo de Botánica, que es todo el mundo que le dejan."),
        cu("A la doctora la despierta el altavoz con el parte de la fiebre: cuántos enfermos nuevos en Botánica, cuántos en Medicina, cuántos mejor. Escucha con los ojos cerrados, contando. Luego el altavoz recuerda que la mujer de arriba sigue aislada. Lo dice cada mañana, como quien da un remedio."),
        cu("La doctora se despierta en un catre del laboratorio, porque a su cuarto, al otro lado de la compuerta, no puede volver. Le duele la espalda. Las bandejas de grama la miran desde la mesa, verdes, sin enterarse de nada. Por lo menos ellas no tienen fiebre."),
    )

    private val CUARENTENA_TRABAJO = listOf(
        cu("A esta hora toma muestras de todo lo que se toca en Botánica: las manillas, los grifos, la tierra de las bandejas, el agua de los tanques, el aire de las rejillas. Doscientos tubos con su etiqueta, que Medicina recoge por una esclusa. Mientras las compuertas sigan cerradas, se toma todo cada día. La mujer de arriba no ha tocado nada de esto.", Hacer.TRABAJO),
        cu("A esta hora cuida lo que queda vivo en Botánica, con guantes y mascarilla, y con Lauro, que no se contagia y no lleva ninguna. Riegan, apuntan, tiran lo que se pudre. Los compañeros sanos están cada uno en su cuarto, por orden de Medicina. Lauro dice que el laboratorio suena distinto sin ellos. Tiene razón.", Hacer.TRABAJO),
        cu("A esta hora come en el laboratorio, de una bandeja que dejan en la esclusa con su nombre escrito en cinta: puré, pan de algas, una fruta pequeña envuelta en plástico. Se baja la mascarilla para cada bocado y se la sube para masticar. Come de pie, mirando las bandejas. No tiene hambre. Se lo come todo.", Hacer.COMER),
        cu("A esta hora duerme mal en el catre del laboratorio, con la mascarilla puesta aunque nadie se lo pide, y se despierta con cada tos que viene del pasillo. Escucha. La tos se calla. Se toca la frente. Fresca. Al otro lado de la compuerta, en algún cuarto, su hijo duerme. Eso espera.", Hacer.DORMIR),
        cu("A esta hora no hay mesa del Consejo, ni comedor grande, ni nada: las reuniones están prohibidas. La doctora habla con su hijo por la pantalla del pasillo, diez minutos, con la mascarilla bajada para que le vea la boca. Tomé dice que está bien. Ella le mira el color de la cara. Dice la verdad.", Hacer.GENTE),
        cu("A esta hora se pone los auriculares en el catre y busca en la tableta la música de siempre: un cuarteto de cuerda, lento, de hace cuatrocientos años. Lo pone bajito. Fuera, el altavoz da otra vez los números. Ella sube el volumen. Durante diez minutos, la fiebre es de otros.", Hacer.LIBRE),
    )

    private val CUARENTENA_PUEBLO = listOf(
        cu("Con la fiebre, el Claustro se ha partido en pedazos: cada ala detrás de su compuerta, cada compuerta con su luz roja. En los pasillos de Botánica hay sábanas en el suelo, cubos de lejía y carteles escritos a mano. El aire huele a desinfectante y, debajo, a tierra. La tierra siempre gana."),
        cu("Por el vidrio de la compuerta de Botánica se ven los jardines, al fondo del pasillo grande, con sus lámparas encendidas para nadie. {entorno} Un vigía con mascarilla pasa cada hora y mira hacia dentro, como si la fiebre se pudiera ver."),
    )

    private val CUARENTENA_ANOCHECER = listOf(
        cu("Cuando las lámparas bajan a la luz de noche, el altavoz da el último parte: dos enfermos más, tres que ya no tienen fiebre. La doctora lo apunta en el cuaderno, en una columna que empezó el primer día. La columna es larga. Se tumba en el catre con la bata puesta."),
        cu("Al bajar las lámparas, la doctora se acerca a la compuerta y apoya la frente en el vidrio frío. Al otro lado, el pasillo grande, vacío, con su luz roja. Alguien, lejos, ha dejado sonando una grabación con música. No sabe qué es. Se queda hasta que se acaba."),
    )

    /** Tres semanas encerrada en la campana de cultivo, con tierra y aire de arriba, en la temporada 3 (estado «experimento»). */
    private fun ex(texto: String, hacer: Hacer? = null) = v(texto, estado = "experimento", hacer = hacer)

    private val EXPERIMENTO_MANANA = listOf(
        ex("La doctora se despierta en la campana, en el catre plegable entre las cajas de tierra gris, con la garganta raspada por el aire de arriba, que huele a polvo y a hierro. Al otro lado del vidrio, el laboratorio sigue con su luz de siempre. Se toca la garganta. Apunta: raspa."),
        ex("A la doctora la despierta Lauro dando dos golpes suaves en el vidrio de la campana, la señal convenida. Le enseña por el cristal la tableta con los números de la noche: el aire, la tierra, ella. Ella levanta un pulgar. Él levanta otro, un segundo tarde, como quien aprende un idioma."),
        ex("La doctora se despierta y lo primero que mira son sus bandejas: veinte, en dos filas, en tierra de arriba. La grama sigue verde. Ella también, más o menos. Se peina con los dedos, porque el peine se quedó fuera. Nadie la ve. Lo hace igual."),
    )

    private val EXPERIMENTO_TIEMPO = listOf(
        ex("Dentro de la campana el tiempo es otro: el aire de arriba, seco, con un regusto a metal, y una luz que entra por el vidrio desde las lámparas del laboratorio. {de_estacion}"),
        ex("{de_estacion} En la campana la doctora lleva su propio parte del tiempo: polvo en el aire, sequedad en la boca, y la grama, que no se queja."),
    )

    private val EXPERIMENTO_TRABAJO = listOf(
        ex("A esta hora mide la grama dentro de la campana, bandeja por bandeja, con la regla y la lupa: el largo de las hojas, el color, cuántas raíces asoman por los agujeros. Dicta los números en voz alta. Lauro, fuera, los apunta. Luego ella se mide a sí misma: pulso, tos, peso. También los dicta.", Hacer.TRABAJO),
        ex("A esta hora saca muestras de la tierra de arriba con una pipeta larga, en las bandejas con grama y en las que no tienen, y las mete en tubos para la esclusa. La tierra sin grama huele a quemado. La que tiene grama huele, casi, a tierra. Lo apunta así, aunque no sea una medida.", Hacer.TRABAJO),
        ex("A esta hora come lo que entra por la esclusa de la campana: una bandeja con puré, pan de algas y un vaso de agua contado. Come sentada en una caja de tierra, con la bandeja en las rodillas. Todo le sabe a polvo, porque el aire de dentro sabe a polvo. Lo apunta también.", Hacer.COMER),
        ex("A esta hora duerme en el catre de la campana, con una mascarilla de tela por el polvo y las lámparas del laboratorio apagadas a medias. Lauro se queda fuera toda la noche, sentado en un taburete, porque ella se lo ha pedido. Cada vez que abre los ojos, él está.", Hacer.DORMIR),
        ex("A esta hora, en vez de la mesa del Consejo, tiene visitas al otro lado del vidrio: Tomé, con la bata de Mentes, y Coral, con la barriga que ya se nota y una lechuga en la mano para que la vea. Hablan por el intercomunicador. Coral dice que está flaca. Tomé dice que está loca. Los dos tienen razón.", Hacer.GENTE),
        ex("A esta hora le pide a Lauro que ponga música en el altavoz del laboratorio, y él pone el cuarteto lento que a ella le gusta, sin que se lo diga: lo ha aprendido. La doctora se tumba en el catre a oírlo, con la grama alrededor. Por el vidrio, Lauro escucha también. No se sabe qué oye.", Hacer.LIBRE),
    )

    private val EXPERIMENTO_PUEBLO = listOf(
        ex("La campana de cultivo es un cilindro de vidrio grueso en medio del laboratorio, de cuatro pasos de ancho, con una esclusa, un catre, cajas de tierra gris de arriba y veinte bandejas de grama. Desde fuera parece un acuario. Desde dentro, también."),
        ex("Alrededor de la campana, el laboratorio sigue con su vida: Lauro en la mesa, los de Botánica que pasan y miran de reojo, la gente de Medicina con sus tubos. Algunos días se juntan más mirones que de costumbre. La doctora los saluda con la mano. Ellos, con cara de visita al hospital."),
    )

    private val EXPERIMENTO_ANOCHECER = listOf(
        ex("Cuando bajan las lámparas, la campana se queda a media luz y el vidrio se vuelve un espejo. La doctora se ve a sí misma, flaca, con la bata gris de polvo, rodeada de verde. Le parece una planta más. Apunta el día en el cuaderno y tacha un número. Faltan menos."),
        ex("Al bajar las lámparas, la doctora apoya la mano en el vidrio de la campana, y Lauro, al otro lado, apoya la suya, porque se lo ha visto hacer a Tomé. No sabe lo que significa. Ella tampoco sabría explicárselo. Se quedan así un rato, cada uno con su lado del vidrio."),
    )

    /** El Claustro cerrado mientras buscan a Lauro, en la temporada 4 (estado «cierre»). Lauro ya se fue. */
    private fun ci(texto: String, hacer: Hacer? = null) = v(texto, estado = "cierre", hacer = hacer)

    private val CIERRE_MANANA = listOf(
        ci("La doctora se despierta con la luz roja del pasillo metida por la rendija de la puerta, como cada mañana desde el cierre. No suena el destello. Nadie lo notaba hasta que ha faltado: ese zumbido de fondo, de toda la vida, que ahora es silencio. Se viste. Hoy también la van a preguntar."),
        ci("A la doctora la despiertan dos vigías que llaman a la puerta, educados, con una lista. Quieren ver el cuarto otra vez. Lo miran todo: el armario, debajo de la cama, el cajón de las semillas. No encuentran nada, porque no hay nada. Se van dando las gracias. Ella no vuelve a dormirse."),
        ci("La doctora se despierta y, durante un segundo, se le olvida. Luego ve en la mesilla la tarjeta de Lauro, la gris, sin número, que él dejó en el laboratorio la última noche. Los vigías no la han pedido. Ella no la ha dado. La guarda en el bolsillo de la bata."),
    )

    private val CIERRE_TRABAJO = listOf(
        ci("A esta hora trabaja en el laboratorio con un vigía sentado en la puerta, que no la mira y no se va. Riega la grama, mide, apunta, como siempre. En el estante del fondo hay un hueco donde estaba el saco de semillas. Ella no lo mira. El vigía, sí, cada tanto.", Hacer.TRABAJO),
        ci("A esta hora la preguntan otra vez en una sala de la Vigía, con la rectora detrás de un vidrio: qué decía Lauro, qué preguntaba, adónde miraba, si le habló del cielo. Ella contesta la verdad, en frases cortas. Que preguntaba mucho. Que lo apuntaba todo. Que no sabe adónde ha ido. Eso último también es verdad.", Hacer.TRABAJO),
        ci("A esta hora come en su cuarto, de una bandeja que trae un vigía, porque el comedor grande está cerrado mientras dure la búsqueda. Puré, pan de algas, una pastilla de vitaminas. Come mirando la pared. Antes, a esta hora, Lauro le recordaba que comiera. Ahora se acuerda sola. Casi.", Hacer.COMER),
        ci("A esta hora duerme a ratos, con la luz roja del pasillo pintando la puerta, y en cada rato sueña lo mismo: Lauro en lo alto de una escalera que no se acaba, con el saco al hombro, preguntándole algo que ella no oye. Se despierta antes de entenderlo. Se queda mirando el techo.", Hacer.DORMIR),
        ci("A esta hora no hay mesa del Consejo: el Consejo está reunido día y noche en la Vigía, mirando las pantallas de arriba. Tomé viene al cuarto con dos bandejas, se sienta en la cama y come con ella sin decir nada. Antes de irse le pregunta si sabía algo. Ella dice que no. Él asiente despacio, como quien guarda una cosa.", Hacer.GENTE),
        ci("A esta hora se pone los auriculares y no pone nada: solo los lleva puestos, para no oír el altavoz, que repite cada hora que quien sepa algo del Semejante desaparecido debe decirlo a la Vigía. Luego pone el cuarteto lento. Lauro le preguntó una vez qué sentía al oírlo. No supo contestarle. Tampoco ahora.", Hacer.LIBRE),
    )

    private val CIERRE_PUEBLO = listOf(
        ci("Con el cierre, el Claustro está quieto y rojo: las luces de aviso en todos los pasillos, las compuertas bajadas entre las alas, el destello apagado, con Bruno sentado delante como quien vela a un enfermo. Los vigías van de puerta en puerta con sus listas. Buscan a uno que no está en el censo."),
        ci("En Botánica han precintado el estante de las semillas con una cinta amarilla, y en la mesa de Lauro está todo como lo dejó: el cuaderno cerrado, el lápiz paralelo al cuaderno, la regla paralela al lápiz. Nadie se ha sentado ahí. Los vigías lo fotografiaron todo. La doctora no se acerca."),
    )

    private val CIERRE_ANOCHECER = listOf(
        ci("Cuando bajan las lámparas, la luz roja de los pasillos se queda sola, y el Claustro parece un cuarto de revelar fotos. La doctora cierra la puerta. En el silencio sin destello se oyen cosas que nunca se oyen: el agua en los tubos, una tos tres puertas más allá, su propio corazón."),
        ci("Al bajar las lámparas, el altavoz da el parte de la búsqueda: ninguna novedad. La doctora piensa que es la mejor noticia que ha oído en días. Se avergüenza un poco de pensarlo. Se le pasa. Apaga la luz de la mesilla."),
    )

    /** Cuatro semanas en la superficie, sembrando grama con Tomé y dos vigías, en la temporada 5 (estado «arriba»). */
    private fun ar(texto: String, hacer: Hacer? = null, desde: String? = null) = v(texto, estado = "arriba", hacer = hacer, desde = desde)

    private val ARRIBA_MANANA = listOf(
        ar("La doctora se despierta arriba, en la tienda, con la luz que entra gris por la lona y un frío que no viene de ninguna rejilla. Fuera canta algo. No es una grabación: se calla cuando quiere y empieza otra vez, desafinado. Ella se queda quieta, oyendo, hasta que se le duermen los pies."),
        ar("A la doctora la despierta Tomé, que ya está fuera de la tienda, llamándola bajito para que venga a ver. Siempre hay algo que ver: un insecto con las alas de colores, una nube con forma de algo, una mata que ayer no estaba. Ella sale descalza. El suelo está frío y no es liso. Nada aquí es liso."),
        ar("La doctora se despierta con la cara tirante y los labios partidos, y tarda un momento en saber por qué: el aire. Aquí el aire no lo ajusta nadie. Se pone la crema de Medicina, que no sirve de mucho, y sale de la tienda. Los vigías ya han calentado agua con hierbas en un hornillo."),
    )

    private val ARRIBA_TIEMPO = listOf(
        ar("Arriba el tiempo no lo dice ningún panel: lo dice el cielo, que cambia mientras se le mira. La doctora se pasa el día levantando la vista, como una niña. Los vigías se ríen de ella, con la cabeza también levantada."),
        ar("Aquí arriba el viento trae cosas: polvo, semillas, olor a algo que se quema lejos, de repente un frío. La doctora quiso apuntar en el cuaderno cada cambio. Ha tenido que conformarse con los que la sorprenden. Siguen siendo todos."),
        ar("Sobre las ruinas, el cielo es más grande de lo que decían los libros, y se mueve. Cuando pasan nubes, pasan con sus sombras por las piedras, como animales. La doctora no sabe sus nombres. En el Claustro las nubes no tienen nombre, porque no hacen falta."),
    )

    private val ARRIBA_TRABAJO = listOf(
        ar("A esta hora siembra grama entre las piedras de las ruinas, encima del Claustro, en surcos cortos que abre con una pala de mano. Tomé va detrás echando semillas, cien en cada palmo, como ella le enseñó. Los vigías miran las calles con las armas al hombro. La tierra de arriba es gris y suelta. Se le mete en todo.", Hacer.TRABAJO),
        ar("A esta hora mide la tierra de las ruinas con el equipo de la caja: un tubo, unos frascos, la tableta. Tierra sin grama: veneno. Tierra donde ya sembraron: un poco menos. Lo apunta todo de rodillas, con el sudor en los ojos. Aquí el sudor no lo seca ninguna rejilla: lo seca el viento, cuando quiere.", Hacer.TRABAJO),
        ar("A esta hora come sentada en una piedra, de una caja de raciones del Claustro: pan de algas, pasta de proteína, una fruta pequeña de los jardines que ya se está arrugando. Aquí todo sabe distinto, más fuerte, como si el aire le echara sal. Tomé come de pie, mirando lejos. Siempre mira lejos.", Hacer.COMER),
        ar("A esta hora duerme en la tienda, dentro del saco, con la cabeza contra una pared rota que guarda un rato el calor del día. Fuera se oye el viento, y algo pequeño que corre entre las piedras. Los vigías hacen guardias de dos horas. Ella se despierta con cada cambio. Luego aprende a no despertarse.", Hacer.DORMIR),
        ar("A esta hora comen los cuatro alrededor del hornillo, en el hueco de lo que fue una casa, mientras el cielo se oscurece. Los vigías cuentan lo que han visto toda su vida en las pantallas y lo comparan con lo que ven aquí. No se parece. Tomé no habla. Escucha los ruidos de la noche como quien aprende una lengua.", Hacer.GENTE),
        ar("A esta hora se sienta sola en un muro roto, con los auriculares al cuello, sin ponérselos. Tenía pensado oír el cuarteto lento bajo el cielo de verdad. No lo hace. El viento suena en las ruinas como un instrumento de una sola cuerda, y no quiere taparlo con nada.", Hacer.LIBRE),
    )

    private val ARRIBA_PUEBLO = listOf(
        ar("Arriba, sobre el Claustro, la ciudad es un campo de piedras: torres partidas por la mitad, calles llenas de escombros, hierros torcidos como raíces. Entre las piedras crece una hierba rala y amarilla. En medio de todo, una caseta de hormigón que parece una ruina más: la boca del destello."),
        ar("Las ruinas no están quietas: en las grietas de los muros hay nidos, en el agua de los sótanos hay bichos, y en lo alto de una torre caída alguien plantó una vez un árbol, que sigue allí, torcido. La doctora lo mira cada mañana. Le parece el ser vivo más terco que ha conocido."),
    )

    private val ARRIBA_GENTE = listOf(
        ar("Con ella han subido Tomé y dos vigías, una mujer callada que no se quita la gorra y un muchacho que habla por los cuatro. Los vigías conocen estas ruinas de las pantallas, calle por calle, y aun así se pierden. Tomé no se pierde nunca. Dice que es más fácil que Mentes."),
        ar("Arriba, Tomé cambia a ojos vista: anda distinto, mira distinto, duerme como un tronco. Se le está quemando la nariz. Se ríe de cosas que la doctora no ve: una piedra que rueda, el viento que le quita la gorra a la vigía. Ella lo mira y casi no lo reconoce. Le gusta."),
        ar("Neva vive con su gente en un patio de las ruinas, al otro lado de la ciudad, donde la grama ya crece sola. Viene a verlos con una cesta de cosas que la doctora no sabe nombrar. A Tomé le enseña a hacer fuego. A ella, dónde no pisar. Del cuarto de Medicina se acuerdan como de un chiste malo.", desde = "encuentra"),
    )

    private val ARRIBA_ANOCHECER = listOf(
        ar("Al caer la tarde, la luz no baja de golpe como en el Claustro: se va despacio, se pone de colores, se queda en lo alto de las torres cuando abajo ya es de noche. La doctora la mira irse con la boca abierta. Tomé la mira a ella. Ninguno de los dos dice nada."),
        ar("Anochece arriba, y van saliendo las estrellas, una, luego diez, luego tantas que la doctora deja de contar, ella que todo lo cuenta. Uno de los vigías le dice los nombres de algunas, mal aprendidos en una pantalla. Ella le corrige uno. Lo sabe de un libro. No sabe de qué le sirve."),
    )

    private val ARRIBA_PENSAMIENTO = listOf(
        ar("La doctora piensa que lleva toda la vida estudiando una hierba para un sitio que no conocía, y que el sitio es así: grande, sucio, lleno de cosas vivas que nadie ha pedido. Piensa que la grama se va a sentir en casa. Piensa que ella todavía no. Piensa en la palabra todavía."),
        ar("Piensa en el Claustro, ahí abajo, debajo de sus pies, con sus pasillos, sus lámparas y su aire ajustado, y en que desde arriba no se nota que existe. Piensa que eso era lo que querían los que lo sellaron. Piensa que lo consiguieron demasiado bien."),
        ar("Piensa en Lauro, que quería saber de qué color era el cielo. Piensa que ahora lo sabe. Piensa que a lo mejor era lo único que quería saber, y que todo lo demás, el saco, el destello, la noche, fue solo el camino. Piensa que ella ha hecho lo mismo, más despacio.", desde = "lauro_lejos"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el Claustro el parte del tiempo es el del panel del aire: grados, humedad, oxígeno."),
        v("La doctora mira el panel del aire del pasillo de Botánica al pasar, por costumbre. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los únicos que saben si arriba llueve son los vigías, y no lo cuentan en el comedor."),
        v("{cielo} {de_estacion} Aquí abajo se dice «hace buen día» cuando no se ha roto nada."),
        v("{de_estacion} {cielo} En los libros de la escuela el tiempo pasaba por encima de la gente. Aquí pasa por los tubos."),
        v("Bajo la ciudad, el día lo hacen las lámparas y el reloj, y nadie los discute. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.SIN_CLIMA to listOf(
            "Las lámparas de los jardines dan un blanco tan parejo que ninguna hoja tiene sombra.",
            "El aire sale de las rejillas a veintiún grados, con su olor de siempre a filtro y a tierra.",
            "Los ventiladores del pozo de aire zumban hoy más grave, y nadie sabe por qué.",
            "La humedad está alta, y por los vidrios de los tanques bajan gotas sin prisa.",
            "En las pantallas de la Vigía hay un cielo sobre las ruinas que casi nadie mira.",
            "La luz del laboratorio es pareja y blanca, sin una sombra que diga qué hora es.",
        ),
        Clima.DESPEJADO to listOf(
            "Arriba el cielo está limpio, de un azul tan hondo que da vértigo levantar la cabeza.",
            "No hay una nube sobre las ruinas, y el sol calienta las piedras rotas hasta que queman.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve sobre las ruinas, una lluvia fina que nadie ha filtrado y que huele a polvo mojado.",
            "Cae agua del cielo, sin tubos ni rejillas, y corre por las piedras rotas de la ciudad.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.NINGUNA to listOf(
            "En el Claustro no hay estaciones: un año se separa del siguiente por la siega.",
            "Bajo la ciudad el año no tiene frío ni calor: tiene fiestas y siegas.",
            "Aquí abajo los meses cambian en el calendario de la pared, y en nada más.",
            "Las estaciones de arriba son una lección de la escuela: se aprenden, no se sienten.",
            "Aquí hace siempre la temperatura que alguien decidió el día del sellado.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora está en el laboratorio de la grama, inclinada sobre una bandeja con la lupa, contando raíces nuevas en un cuadrado de tierra del tamaño de una mano. Las cuenta dos veces. Si no coinciden, una tercera. Casi nunca hace falta.", hacer = Hacer.TRABAJO),
        v("A esta hora pesa tierra en la balanza fina, en cucharadas de un gramo, para cuarenta tubos con su etiqueta. La tierra de arriba es gris y se pega a los guantes por la electricidad. Sopla. Se le pega a la cara. Uno de Medicina le dice al pasar que parece un fantasma.", hacer = Hacer.TRABAJO),
        v("A esta hora mira al microscopio una gota de agua de una bandeja, buscando lo que las raíces han sacado de la tierra: unos cristales pequeños y oscuros, como arena quemada. Cuantos más, mejor. Apunta: unos cuantos. Y luego, al lado, el número exacto.", hacer = Hacer.TRABAJO),
        v("A esta hora riega las bandejas de grama una por una con una pipeta, porque la manguera es demasiado bruta para lo que hace falta: tres gotas aquí, cinco allá, según la tableta. Son doscientas bandejas. Es el rato del día en que mejor piensa.", hacer = Hacer.TRABAJO),
        v("A esta hora escribe el informe del mes para el Consejo, en la tableta, con gráficas que nadie va a mirar y un resumen de tres líneas, que es lo único que leerán. Tarda una hora en las gráficas y otra en las tres líneas. Le salen peor las líneas.", hacer = Hacer.TRABAJO),
        v("A esta hora siembra semillas de grama en bandejas nuevas, con una pinza, una por agujero, en filas de diez. Son tan pequeñas que un estornudo se lleva una tarde de trabajo. No estornuda. Tiene la nariz entrenada. Es lo único del cuerpo que tiene entrenado.", hacer = Hacer.TRABAJO),
        v("A esta hora trabaja con Lauro en la mesa larga: ella mide y dicta, él apunta sin mirar la tableta, con una letra perfecta. Cuando ella se equivoca de número, él no dice nada, pero deja el dedo quieto sobre la pantalla. Ella se da cuenta. Lo corrige. Él sigue.", hacer = Hacer.TRABAJO, desde = "lauro", hasta = "lauro_se_va"),
        v("A esta hora riega las bandejas con Lauro, cada uno por un lado de la mesa, y Lauro pregunta. Por qué esta bandeja más que aquella. Por qué la grama no tiene flor. Por qué ella tararea cuando riega. La doctora contesta las dos primeras. No sabía que tarareaba.", hacer = Hacer.TRABAJO, desde = "pregunta", hasta = "lauro_se_va"),
        v("A esta hora trabaja sola en el laboratorio, desde que Lauro se fue, y tarda el doble en todo, porque mide y apunta a la vez. Mentes le ha ofrecido otro Semejante. Ella ha dicho que todavía no. No sabe qué es lo que espera. Mide. Apunta. Vuelve a medir.", hacer = Hacer.TRABAJO, desde = "lauro_se_va"),

        v("A esta hora come en el comedor grande, en la mesa de Botánica, de una bandeja con cuatro huecos: un puré de judías, pan de algas, hojas de los jardines y un cubo de algo que se llama proteína y sabe a eso. Come deprisa, con la tableta al lado. La bandeja vuelve vacía.", hacer = Hacer.COMER),
        v("A esta hora come en el laboratorio, de pie junto a la mesa, porque hay una bandeja que no quiere perder de vista: un tomate de los jardines y un trozo de pan, con las manos oliendo a tierra. Coral dice que comer de pie es de caballos. La doctora dice que los caballos comen hierba. Ella también, casi.", hacer = Hacer.COMER),
        v("A esta hora come en una esquina del comedor grande, sola, con un libro en la tableta y una taza de hierbas que se le enfría siempre. Lee lo mismo dos veces sin enterarse. Cuando se da cuenta, ya ha comido. No recuerda qué. Era verde.", hacer = Hacer.COMER),
        v("A esta hora come en el laboratorio, y Lauro, que no come, se sienta enfrente a esperar a que termine, con las manos en las rodillas. Al principio le daba apuro. Ahora le cuenta lo que come, y él lo apunta sin que nadie se lo haya pedido. Tiene una lista. Ella no quiere verla.", hacer = Hacer.COMER, desde = "lauro", hasta = "lauro_se_va"),

        v("A esta hora duerme de lado, con una mano bajo la almohada y la otra fuera, como cuando era niña, en su cuarto de Botánica, que huele un poco a tierra porque todo lo que ella toca huele a tierra. Las lámparas están en su azul de noche. Respira hondo.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con raíces: largas, blancas, que salen de las bandejas, bajan por las patas de la mesa y se meten por los desagües hacia algún sitio. En el sueño no se asusta. Las sigue. Se despierta antes de saber adónde van.", hacer = Hacer.DORMIR),
        v("A esta hora duerme a medias, porque por los tubos de la pared pasan los riegos de la noche, y su cuerpo, de tantos años, se despierta para comprobar que suenan. Suenan. Se da la vuelta. El Claustro entero duerme encima y debajo de ella.", hacer = Hacer.DORMIR),

        v("A esta hora cena en la mesa del Consejo, en la cabecera del comedor grande, donde se sientan los jefes de departamento y los que el Consejo quiere oír. Se habla de presupuestos, de averías, de quién ha pedido qué. La doctora come callada y habla solo cuando le preguntan. Le preguntan poco. Lo prefiere.", hacer = Hacer.GENTE),
        v("A esta hora está en la mesa del Consejo, con los de Medicina enfrente y los de la Vigía al lado, que siempre traen alguna historia de las pantallas: un perro, una tormenta, una torre que se cae. Todos escuchan como niños. Luego alguien dice «bueno» y se vuelve a hablar del agua.", hacer = Hacer.GENTE),
        v("A esta hora termina de cenar en la mesa del Consejo y se queda un rato en la sobremesa, con su taza de hierbas, oyendo discutir a los jefes de Mentes y de la Vigía sobre algo que no entiende. Asiente cuando los demás asienten. Nadie se da cuenta. O todos hacen lo mismo.", hacer = Hacer.GENTE),
        v("A esta hora cena en la mesa del Consejo, a tres sillas del rector Mora, que come despacio y lo oye todo. Hoy le ha pasado el pan sin mirarla y le ha preguntado por la grama. Ella ha dicho «crece». Él ha dicho «bien». En la mesa del Consejo, eso es una conversación larga.", hacer = Hacer.GENTE, hasta = "rectora"),
        v("A esta hora cena en la mesa del Consejo, enfrente de la rectora Ibarra, que no se sienta en la cabecera sino en medio, para ver a todos. La rectora pregunta cosas concretas: cuánto, cuándo, cuántos. La doctora contesta con números. Es el idioma en que mejor se entienden.", hacer = Hacer.GENTE, desde = "rectora"),

        v("A esta hora se tumba en la cama con los auriculares y pone un cuarteto de cuerda de hace cuatrocientos años, siempre el mismo, el lento. Lo conoce tanto que sabe dónde respira el violonchelo. Cierra los ojos. Durante veinte minutos no hay grama, ni Consejo, ni nada que medir.", hacer = Hacer.LIBRE),
        v("A esta hora va a la sala de música del Claustro, donde hay un piano de antes del sellado que nadie sabe afinar del todo. Un viejo de Medicina toca cuando le apetece, y hoy le apetece. Hay cinco personas escuchando, cada una en una silla lejos de las otras. Así se escucha aquí.", hacer = Hacer.LIBRE),
        v("A esta hora pone música en el laboratorio vacío, con el altavoz de la mesa, porque tiene la teoría, sin publicar, de que a la grama le sienta bien el piano. No tiene pruebas: tiene una bandeja con música y otra sin, y las dos crecen igual. Ella le sigue poniendo música a la primera, por si acaso.", hacer = Hacer.LIBRE),
    )

    private val PUEBLO = listOf(
        v("{entorno} Botánica ocupa el ala más honda del Claustro: un pasillo de laboratorios con puertas de vidrio, un cuarto de semillas frío como una nevera y, al fondo, los jardines, una nave cuyo techo se pierde en el resplandor de las lámparas."),
        v("Los jardines son lo más bonito del Claustro: filas de tanques con las raíces colgando como barbas blancas, y encima lechugas, tomates, judías que trepan por hilos. Huele a verde mojado. Los niños vienen a mirar las raíces. {entorno}"),
        v("{entorno} El comedor grande está en el centro del Claustro, debajo de la vieja plaza de la ciudad: mesas largas y una pared entera de pantallas de la Vigía con las ruinas de arriba en directo. Casi nadie las mira. Los niños, sí."),
        v("La Vigía está en lo más alto del Claustro, pegada al techo de hormigón: una sala oscura llena de pantallas, donde los vigías miran las ruinas por turnos, día y noche. Dicen que con los años se aprenden cada piedra de memoria. {entorno}"),
        v("{entorno} La sala del destello es redonda, con un círculo de metal en el suelo y una cabina de vidrio para quien lo maneja. Huele a ozono. Quien se pone en el círculo cierra los ojos sin que se lo digan. Al abrirlos está en otro sitio, algo mareado."),
        v("Los pasillos del Claustro tienen nombres de árboles que nadie ha visto: el pasillo del Roble, el del Haya, el del Almendro. Hay flechas de colores en el suelo, y aun así los niños se pierden, y aparecen en los jardines comiendo tomates. {entorno}"),
        v("En la pared de pantallas del comedor grande, en una de abajo, hay una mancha verde en las ruinas, lejos del Claustro, que no estaba. La gente se para delante con la bandeja. Los vigías dicen que es moho. Nadie se lo cree. Cada tanto alguien jura que crece. {entorno}", desde = "verde"),
    )

    /** Lo que ha crecido en los jardines (arco «jardines»): vacíos tras el vaciado de la temporada 2. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("jardines", 0.0, 0.08, frases = listOf(
            "Los tanques de los jardines están vacíos y lavados, con las lámparas encendidas sobre agua limpia y nada más.",
            "En los jardines no queda una hoja: solo los tanques, los hilos colgando y el olor a lejía.",
        )),
        Entorno("jardines", 0.08, 0.35, frases = listOf(
            "En los jardines han sembrado otra vez, y los tanques tienen una pelusa verde de brotes que todavía no son nada.",
            "Los jardines están recién sembrados, con más agua que hoja, y Coral pasa revista a los brotes uno por uno.",
        )),
        Entorno("jardines", 0.35, 0.75, frases = listOf(
            "Los jardines están verdes y medio llenos, y las judías ya han subido hasta la mitad de los hilos.",
            "En los jardines el verde va ganando a los tanques, y el aire de la nave huele a hoja mojada.",
        )),
        Entorno("jardines", 0.75, 1.0, frases = listOf(
            "Los jardines están en lo más alto, tan llenos que hay que apartar las hojas para pasar, y ya se habla de la siega.",
            "En los jardines los tomates se doblan de rojo, y Coral cuenta los días que faltan para segar.",
        )),
    )

    private val GENTE = listOf(
        v("Bruno, el ingeniero del destello, es un hombre grande que le habla a la máquina como a un caballo viejo. Ha mandado arriba y abajo a todo el que ha viajado en veinte años. Antes de cada viaje dice lo mismo: «Ojos cerrados y rodillas blandas»."),
        v("Coral, la jefa de los jardines, es su amiga desde que las dos se escondían entre los tanques a comer tomates verdes. Ahora manda sobre todos los tomates del Claustro y se los sigue comiendo verdes. Habla con las plantas. Dice que la doctora también, pero por escrito."),
        v("Los vigías son gente aparte: duermen de día y hablan de las ruinas como de un barrio que conocen. Saben cuántas torres quedan en pie y en cuál anidan los pájaros. Arriba no han estado casi nunca. La doctora sospecha que les da miedo y envidia a partes iguales."),
        v("Los Semejantes llevan una franja gris en la manga, porque sin ella nadie sabría quién es quién. No comen, no se cansan y descansan unas horas cada noche en Mentes. Si alguien les da las gracias, contestan «de nada» tras una pausa muy corta, que se nota si se escucha."),
        v("Tomé, su hijo, desarma todo lo que cae en sus manos para ver cómo va por dentro, y casi siempre lo vuelve a armar. De pequeño desarmó la tableta de su madre. Ella se enfadó una semana y luego le consiguió un destornillador. Se parecen en eso. No se lo dice."),
        v("Los de Botánica comen juntos en una punta del comedor grande, hablando de raíces, y los de Medicina, en la otra, hacen como que no los oyen. Es una rivalidad vieja que nadie recuerda cómo empezó. Se mantiene por gusto."),

        v("Su asistente es un Semejante viejo que lleva doce años lavándole los tubos y apuntándole los números sin equivocarse nunca. Últimamente se le olvidan cosas. Mentes dice que lo van a cambiar. La doctora no ha preguntado qué hacen con los que cambian.", hasta = "lauro"),
        v("Lauro, su asistente nuevo, es un Semejante con cara de hombre joven que aprende todo a la primera y lo dobla todo en cuatro. No habla si no le hablan. Cuando le hablan, escucha con la cabeza un poco ladeada. La doctora todavía no sabe si eso lo hace él o se lo hicieron en Mentes.", desde = "lauro", hasta = "pregunta"),
        v("Lauro, su asistente, pregunta. Es raro en un Semejante, y en Mentes no lo saben. Pregunta por qué la grama se inclina hacia la lámpara, por qué Medicina no se sienta con Botánica, por qué su cuarto no tiene ventana, si aquí nadie la tiene. La doctora contesta lo que sabe. Cada vez sabe menos.", desde = "pregunta", hasta = "lauro_se_va"),
        v("Desde que Lauro se fue, nadie dice su nombre en Botánica: se dice «el que se fue», o no se dice nada. En la percha del laboratorio sigue colgada su bata de repuesto, con la franja gris, porque nadie sabe a quién le toca quitarla. A la doctora tampoco le toca. La deja.", desde = "lauro_se_va", hasta = "lauro_lejos"),
        v("A Lauro lo vio arriba, de lejos, entre la gente de Neva, con la bata cortada a la altura de las rodillas y sin la franja gris en la manga. Él la miró. Ella lo miró. Ninguno levantó la mano. En el informe hay una casilla para lo que se ha visto. Ella la dejó en blanco.", desde = "lauro_lejos"),

        v("Hilda, su maestra, tiene ochenta años y el laboratorio de al lado, y sigue llegando cada mañana la primera, con un bastón que no usa y una bolsa de semillas que sí. Fue ella la que le enseñó a mirar raíces. Todavía le corrige las etiquetas. La doctora se deja.", hasta = "hilda_deja"),
        v("Hilda ya no baja al laboratorio: vive en el ala de los mayores, en un cuarto lleno de macetas, y la doctora le lleva cada semana un brote de lo que sea para que lo mire con la lupa. Hilda lo mira. Dice lo que está mal. Luego pide el siguiente.", desde = "hilda_deja", hasta = "hilda"),
        v("Hilda murió, y sus veinte cuadernos de tapas de tela están en el estante del laboratorio. La doctora abre uno cuando no sabe qué hacer. A veces encuentra la respuesta. Casi siempre encuentra una nota en el margen, con la letra temblona de Hilda, que dice: mira otra vez.", desde = "hilda"),

        v("Tomé está en el último curso de la escuela y todavía no ha dicho qué prueba quiere hacer. Coral apuesta por los jardines. Bruno, por el destello. La doctora no apuesta: lo ha visto mirar a los Semejantes con una atención que no le pone a nada más.", hasta = "tome"),
        v("Tomé estudia para la prueba de Mentes hasta tarde, en unos libros de tapas grises que en casa nadie entiende. Quiere aprender a hacer Semejantes. La doctora le pregunta para qué. Él dice que para entenderlos. Ella no sabe si eso la tranquiliza.", desde = "tome", hasta = "prueba_tome"),
        v("Tomé trabaja en Mentes desde que pasó la prueba, y ha aprendido a no contar lo que hace allí. Llega con olor a metal caliente y una bata que no es la de Botánica. A veces se queda mirando a los Semejantes del comedor como un mecánico mira un motor. A veces, como un amigo.", desde = "prueba_tome", hasta = "tome_arriba"),
        v("Tomé quiere volver arriba, a vivir con la gente de Neva, y se lo ha dicho a su madre de pie en la puerta del laboratorio, como quien avisa de que va a llover. Ella no ha dicho que no. Tampoco que sí. Lo mira ir y venir de Mentes como quien mira algo que ya se está yendo.", desde = "tome_arriba"),

        v("Coral quiere un hijo y lo ha pedido varios años seguidos en los permisos, y varios años seguidos le han dicho que no, con buenas palabras. No se queja. Lo apunta en la libreta donde apunta las cosechas. Dice que algún año las dos listas saldrán bien a la vez.", hasta = "coral"),
        v("A Coral le dieron por fin el permiso de hijo, y anda por los jardines como si el suelo fuera blando. Hace una cuna con la madera de las cajas de semillas. Le pregunta a la doctora cosas de cuando tuvo a Tomé. La doctora se acuerda de menos de lo que quisiera.", desde = "coral", hasta = "alba"),
        v("Coral tiene a Alba, una niña que nació con un grito que se oyó en tres alas, y la lleva a los jardines atada al pecho, para que duerma con el ruido del agua. Duerme. La doctora la coge cuando Coral la deja. Se le había olvidado lo mucho que pesa tan poco.", desde = "alba", hasta = "funciona"),
        v("Alba, la hija de Coral, ya anda por los jardines agarrándose a los tanques y arrancando lo que encuentra, que casi siempre es lo que no debe. A la doctora la llama por un nombre que se ha inventado ella. Nadie sabe de dónde lo sacó. A la doctora le encanta.", desde = "funciona"),

        v("Neva, la mujer de arriba, vive encerrada en un cuarto de Medicina con un vigía en la puerta. Tiene las manos más duras que la doctora ha tocado nunca y una cicatriz en el cuello que no explica. Pregunta por todo menos por cuándo la van a soltar. Eso ya lo sabe: cuando quieran.", desde = "neva", hasta = "neva_sube"),
        v("A Neva la devolvieron arriba con los ojos vendados, para que no supiera por dónde. La doctora guarda la piedra lisa que le regaló en el cajón de la mesa. A veces mira las pantallas de la Vigía buscando a una mujer entre las ruinas. No la encuentra. Sigue mirando.", desde = "neva_sube", hasta = "encuentra"),
        v("Neva vive arriba, en un patio de las ruinas, y riega la grama como si la hubiera inventado ella. Desde que bajó, la doctora la busca en las pantallas de la Vigía. A veces la ve, pequeña, con un cubo. Neva levanta la mano hacia la cámara. Sabe dónde está.", desde = "encuentra"),

        v("El rector Ezequiel Mora lleva el Claustro desde antes de que naciera Tomé, con voz suave y una memoria de elefante para los presupuestos. A la doctora la saluda siempre con la misma pregunta: «¿Ya limpia algo?». Ella siempre contesta lo mismo: «Todavía».", hasta = "rectora"),
        v("La rectora Selma Ibarra pasó veinte años en la Vigía, mirando las ruinas por las pantallas, y gobierna el Claustro igual: desde lejos, sin pestañear, sin perder detalle. Es justa. No es amable. La doctora todavía no ha decidido si eso es peor o mejor que lo contrario.", desde = "rectora"),
    )

    private val OBJETO = listOf(
        v("En el Claustro nada viene de fuera, así que todo se cuida como si fuera lo último. {dato_objeto}"),
        v("{dato_objeto} La doctora lo sabe desde siempre, como se saben las cosas de casa."),
        v("Hay cosas que la doctora toca cien veces al día sin verlas. Hoy ve una. {dato_objeto}"),
        v("{dato_objeto} Los de arriba, dicen, no tienen nada parecido. De los de arriba se dicen muchas cosas."),
        v("{dato_objeto} En Botánica cada cosa tiene su etiqueta, y la doctora ha escrito casi todas."),
        v("Bajo la ciudad las cosas duran mucho, porque no hay sol que las gaste ni lluvia que las pudra. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("bata", "la bata", "una bata", "La bata",
            "La bata de Botánica es blanca con el cuello verde, de una tela que aguanta cien lavados; la de la doctora tiene una quemadura de ácido en la manga y los bolsillos dados de sí por las semillas; los Semejantes llevan la misma, con una franja gris en la manga."),
        ObjetoCuento("tableta", "la tableta", "una tableta", "La tableta",
            "La tableta de la doctora es una placa de vidrio negro, más vieja que ella, con una esquina pegada con resina; guarda todos sus números desde la primera bandeja; si se perdiera, se perderían veinte años, y por eso hay una copia en un cajón, y otra copia de la copia."),
        ObjetoCuento("pipeta", "la pipeta", "una pipeta", "La pipeta",
            "La pipeta es un tubo de vidrio fino con una pera de goma, que coge el agua gota a gota; la doctora tiene seis, numeradas; la tres es la buena, la que no gotea, y se la lleva en el bolsillo de la bata cuando se va, por si acaso."),
        ObjetoCuento("microscopio", "el microscopio", "un microscopio", "El microscopio",
            "El microscopio de Botánica es de antes del sellado, de latón y vidrio, pesado como un niño; tiene un tornillo que se atasca y hay que darle un golpe exacto con dos dedos en un sitio exacto; la doctora es la única que sabe cuál."),
        ObjetoCuento("semilla", "la semilla", "una semilla de grama", "La semilla",
            "La semilla de grama es tan pequeña que caben cien en una uña: un grano pardo, liso, que no parece nada; la doctora las cuenta con una pinza sobre un plato negro, para verlas; de cada cien, dice, una sale terca, y esa es la que sirve."),
        ObjetoCuento("maceta", "la maceta", "una maceta", "La maceta",
            "Las macetas del Claustro son de barro cocido en el horno de Mantenimiento, todas iguales, con un número grabado en el fondo; una maceta rota no se tira: se muele y se vuelve a cocer; la doctora tiene una que ya ha sido maceta cuatro veces."),
        ObjetoCuento("guantes", "los guantes", "unos guantes", "Los guantes",
            "Los guantes de Botánica son de goma fina, amarillos, y se lavan y se secan colgados de pinzas como orejas; cada uno tiene su par, con el nombre escrito en el puño; los de la doctora llevan un remiendo en el índice, donde siempre se pincha."),
        ObjetoCuento("tarjeta", "la tarjeta", "una tarjeta", "La tarjeta",
            "La tarjeta del Claustro es una placa de plástico con el nombre y el número de censo, que abre las puertas y da la comida; si se pierde, una no existe hasta que le hacen otra; los Semejantes llevan una gris, sin número, que abre solo las puertas de su trabajo."),
        ObjetoCuento("auriculares", "los auriculares", "unos auriculares", "Los auriculares",
            "Los auriculares de la doctora son grandes, de los que tapan la oreja entera, con la espuma cambiada tres veces; por ellos suena el archivo de música del Claustro, que guarda miles de grabaciones de antes del sellado; ella, de todas, oye unas diez."),
        ObjetoCuento("cuaderno", "el cuaderno", "un cuaderno", "El cuaderno",
            "El cuaderno de la doctora es de papel de verdad, hecho en Mantenimiento con fibra de los jardines, y casi nadie en el Claustro escribe ya en papel; ella sí, porque el papel no se apaga; va por el cuarenta y tantos, todos en un estante, en orden."),
        ObjetoCuento("piedra", "la piedra", "una piedra", "La piedra",
            "En el Claustro no hay piedras sueltas: todo es hormigón, vidrio y metal; una piedra de arriba se guarda como una joya, y quien tiene una la saca a veces del cajón solo para notarla en la mano, fría, pesada y de verdad."),
        ObjetoCuento("taza", "la taza", "una taza", "La taza",
            "Las tazas del comedor son de barro gris, con el borde grueso, y en ellas se toma la infusión de hierbas de los jardines, lo único caliente que se bebe por gusto; la de la doctora tiene una muesca en el borde, y ella bebe siempre por el otro lado."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} La doctora oye esas cosas en el comedor y las deja pasar, como el agua por un filtro. A sus {edad} años sabe que en el Claustro todo se dice dos veces: una en voz alta y otra en los pasillos. La segunda suele ser la buena."),
        v("{mundo} La doctora piensa en la grama, que es una hierba sin flor y sin gracia, y en que, si sale bien, un día crecerá donde nadie la vea, limpiando la tierra sin que nadie le dé las gracias. Piensa que es lo más parecido a un buen trabajo que conoce."),
        v("{mundo} A veces, con la lupa en la mano, la doctora piensa que lleva toda la vida mirando cosas muy pequeñas para arreglar una muy grande. Piensa que es una manera rara de arreglar el mundo. Piensa que es la única que sabe."),
        v("Piensa en su hijo, que crece en un sitio donde todo está calculado, y en que ella también creció así y no salió mal. Piensa que no salió mal. Lo piensa otra vez, más despacio, para ver si suena igual. Suena casi igual."),
        v("{mundo} La doctora piensa en la gente de arriba, que en los cuentos de la escuela eran sombras con cuchillos. Piensa que en las pantallas nunca los ha visto hacer nada que dé miedo. Piensa que a lo mejor ellos también cuentan cuentos de los de abajo. Seguro que sí.", hasta = "sube"),
        v("Piensa que lleva media vida midiendo cosas que crecen despacio, y que se le ha pegado: ya no espera que nada cambie de un día para otro. En Botánica es una virtud. Piensa que fuera de Botánica, a lo mejor, es otra cosa. A sus {edad} años, no sabe cuál."),
        v("Piensa en el sol, que vio una vez, a los veintiún años, con su promoción, desde la boca del destello: diez minutos, con unas gafas negras y un vigía mirando el reloj. Piensa que no se acuerda de cómo era. Se acuerda de que lloró. No sabe si fue por la luz.", hasta = "sol"),
        v("Piensa en el sol de arriba, que vio por fin de verdad, sin gafas y sin reloj, y en que no se parece a ninguna lámpara. Piensa que a veces, en el laboratorio, cierra los ojos y lo busca. Piensa que lo encuentra donde lo dejó: detrás de los párpados, caliente.", desde = "sol"),
        v("Piensa en la decisión del Consejo, que guarda la grama para el Claustro como quien guarda la llave de un pozo. Piensa que tiene su lógica. Piensa que la lógica es lo que menos le preocupa. Lo que le preocupa es que a nadie en la mesa le pareció raro.", desde = "decision", hasta = "acuerdo"),
        v("Piensa en el voto del Consejo, que salió por uno, y en que ese uno podría haber sido cualquiera: alguien que esa noche durmió bien, o mal, o que se acordó de algo. Piensa que la semilla sube arriba por una mala noche o por una buena. Piensa que casi todo funciona así.", desde = "acuerdo"),
    )

    private val MUNDOS = mapOf(
        "humo" to listOf(
            "Se habla en el comedor de las cámaras nuevas que pide la Vigía, para mirar más lejos de lo que miran.",
            "Se dice que el reactor anda con avisos que Mantenimiento no explica, y que hay quien guarda velas en el cajón.",
            "Se habla de las ruinas de arriba, donde los vigías juran que a veces algo se mueve.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando las lámparas bajan a la luz de noche, el Claustro se vuelve azul y se oyen las puertas cerrándose ala por ala, como una ola. La doctora vuelve a su cuarto por el pasillo del Roble, contando las flechas del suelo. Cuarenta y dos. Siempre cuarenta y dos."),
        v("Al bajar las lámparas, los jardines pasan a su luz violeta, la de la noche, y las hojas parecen de otro sitio. La doctora se queda un rato en la puerta, mirándolas. Es la luz que más le gusta del Claustro. Es la única que no imita nada."),
        v("Cuando bajan las lámparas, la doctora apunta en el cuaderno lo último del día: cuánto bebió cada bandeja, qué se torció, qué mirar mañana. Lo pone en el estante, con el lomo a ras de los demás. Si no, no duerme."),
        v("Al bajar las lámparas, en el pasillo de Botánica alguien silba siempre la misma melodía, una que nadie sabe de dónde salió. La doctora la conoce desde niña. Una noche, por curiosidad, la buscó en el archivo de música. No estaba. Mejor."),
        v("La última luz del Claustro es la de la Vigía, que no se apaga nunca: por la rendija de su puerta sale el resplandor de las pantallas, con las ruinas de arriba en su noche de verdad. La doctora pasa por delante sin mirar. Casi."),
        v("Cuando bajan las lámparas, la doctora pasa por el cuarto de Tomé y escucha detrás de la puerta: respira, o pasa páginas, o le habla bajito a algo que está arreglando. Cualquiera de las tres cosas le vale. Sigue hasta su cuarto. Ya puede dormir."),
        v("Al bajar las lámparas, Lauro apaga una a una las luces de las mesas del laboratorio, y cuando llega a la de ella espera a que diga «ya». Ella siempre dice «un momento». Él espera el momento exacto. Luego apaga. Se dan las buenas noches. Él se va a Mentes, adonde se van.", desde = "lauro", hasta = "lauro_se_va"),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y anoche, a las doce, el Claustro entero estaba en los jardines a oscuras, contando hacia atrás. Al llegar a cero se encendieron todas las lámparas de golpe, y durante un segundo nadie vio nada. Luego todo el mundo se abrazó a ciegas, con el primero que tenía al lado.",
            Parrafo.TRABAJO to "Hoy la doctora no trabaja: el laboratorio está cerrado, y hasta las bandejas tienen el día libre, con un riego doble programado anoche. Duerme hasta tarde, que para ella es una hora más. Luego no sabe qué hacer con las manos. Se las mete en los bolsillos de la bata, que se ha puesto sin darse cuenta.",
            Parrafo.GENTE to "Esta tarde, en el comedor grande, hay fruta de los jardines para todos, la que se guardó para hoy: fresas pequeñas y ácidas, tres por cabeza. Los niños las cambian por favores. Su hijo le ofrece una de las suyas. Ella le dice que no, que se la coma. Él se la come. Se ríen.",
            Parrafo.PENSAMIENTO to "La doctora piensa en el año que empieza y no le pide nada, porque no es de pedir: le pone tareas. Medir más. Equivocarse menos. Dormir sin la tableta en la cama. Piensa que la tercera no la va a cumplir. La apunta igual, para tener algo que tachar en diciembre.",
        ),
        "censo" to unDia(
            Parrafo.MANANA to "Hoy es el censo del año, y desde temprano hay una cola que da la vuelta al comedor grande: cada uno pasa por una mesa, dice su nombre, enseña la tarjeta y una vigía lo apunta en una lista larga. Los nacidos en el año pasan en brazos, y se les aplaude. El número del Claustro sube o baja. Hoy se sabe.",
            Parrafo.GENTE to "Esta tarde, en la mesa del censo, los Semejantes ayudan a llevar las listas, a ordenar la cola, a recoger las tarjetas. No se ponen en la cola. Nadie se lo dice: no hace falta. Un niño le pregunta a uno por qué no lo cuentan. El Semejante le contesta, con mucha amabilidad, que no lo sabe. La madre se lleva al niño.",
            Parrafo.PENSAMIENTO to "La doctora piensa que el censo cuenta bocas, camas y tarjetas, y que por eso los Semejantes no salen: no comen, no ocupan cama de las que se cuentan, no tienen número. Piensa que es una razón muy ordenada. Piensa que las razones muy ordenadas a veces son como una etiqueta bien puesta en el frasco equivocado.",
        ),
        "fundacion" to unDia(
            Parrafo.MANANA to "Hoy es el Día de la Fundación, y a primera hora todo el Claustro se junta delante de la compuerta vieja, la que ya no se abre, pintada de verde y con una placa. Se guarda un minuto de silencio. Luego suena por los altavoces la grabación del cierre: un golpe sordo, largo, y después nada. Los niños se tapan los oídos. Los viejos, no.",
            Parrafo.TRABAJO to "Hoy la doctora no trabaja: es la fiesta del Claustro, y hasta Mantenimiento descansa, salvo los del aire y los del agua. Pasa la mañana en los jardines, ayudando a colgar banderas de papel entre los tanques, con los colores de los departamentos. La de Botánica es verde. La cuelga ella, subida a una escalera que le sujeta un niño.",
            Parrafo.GENTE to "Esta tarde hay comida de fiesta en el comedor grande: un pastel de zanahoria de los jardines con miel de las colmenas de Botánica, que dan un tarro al año por cabeza. El Consejo brinda con zumo. Alguien empieza el himno de la fundación, que nadie se sabe entero. La segunda estrofa la tararean todos.",
            Parrafo.PENSAMIENTO to "La doctora piensa en los que sellaron el Claustro, que cerraron la compuerta sabiendo quién se quedaba fuera. Piensa que hicieron bien, o que hicieron lo que pudieron, que no es lo mismo. Piensa que la fiesta celebra lo primero. Piensa que a lo mejor un día habrá otra para lo segundo.",
        ),
        "revision" to unDia(
            Parrafo.MANANA to "Hoy es la revisión médica del año, y a la doctora le toca a primera hora: una sala de Medicina, una aguja, una máquina que le mira los huesos y una pared con letras cada vez más pequeñas. Las de abajo ya no las ve. El médico apunta sin decir nada. Ella sabe lo que apunta.",
            Parrafo.GENTE to "Esta tarde los de Botánica comparan sus resultados en la mesa del comedor, como niños con las notas: quién ha perdido vista, quién ha ganado peso, a quién le han mandado vitaminas. A todos les falta vitamina. Es la falta de sol, dice Medicina cada año. Cada año lo apuntan, como si fuera nuevo.",
            Parrafo.PENSAMIENTO to "La doctora piensa en sus ojos, que se le gastan de mirar por la lupa bajo las lámparas, y en que todo el trabajo de su vida está hecho con unos ojos que se van. Piensa que tiene que darse prisa. Luego piensa que la grama no entiende de prisas. Se pone las gafas.",
        ),
        "jornadas" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empiezan las jornadas, tres días en que cada departamento sube al estrado del comedor grande a contar lo que ha descubierto en el año. Se ponen sillas en filas. Se apagan las pantallas de la Vigía para que nadie se distraiga. Hay quien trae cojín. Hay quien trae libreta. La doctora trae las dos cosas.",
                Parrafo.TRABAJO to "A esta hora escucha a los de Medicina, que abren las jornadas como cada año, con sus gráficas de tos, de huesos y de vista. Las gráficas bajan, despacio. Medicina dice que es normal. Alguien pregunta qué es normal. Medicina pasa a la siguiente gráfica. La doctora apunta la pregunta en la libreta.",
                Parrafo.PENSAMIENTO to "La doctora piensa que en las jornadas el Claustro se mira en un espejo una vez al año, y que el espejo no miente, pero cada uno elige dónde ponerse. Piensa que ella también lo hace. Piensa en qué gráfica va a enseñar primero cuando le toque. La que sube. Siempre la que sube.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en las jornadas, oyendo a Mentes contar cómo son los Semejantes de este año: más rápidos, más callados, más parecidos. El público aplaude lo de más rápidos. Lo de más parecidos se recibe con un silencio que nadie sabe muy bien de qué es.",
                "A esta hora sigue en las jornadas, y le toca a la Vigía: fotos de las ruinas, una torre que se ha caído este invierno, unos pájaros que anidan donde antes no. Se apagan las luces para verlas mejor. Al encenderse, la gente parpadea, como si volviera de un viaje.",
            )),
        ),
        "luz_larga" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de la luz larga, y desde que se encienden, las lámparas de los jardines no se van a apagar hasta mañana. La gente baja con mantas y almohadas a dormir entre los tanques, como dicen que se dormía arriba en verano, al raso. La jefa de los jardines reparte sitios para que nadie se acueste encima de las lechugas.",
            Parrafo.TRABAJO to "Hoy la doctora no trabaja: es la luz larga, y Botánica está de fiesta en su propia casa, que son los jardines. Pasa el día ayudando a montar los toldos de tela donde se dormirá, y a llevar agua. A media tarde ya está más cansada que un día de laboratorio. Y más contenta.",
            Parrafo.ANOCHECER to "Esta noche las lámparas no bajan, y el Claustro se queda despierto en los jardines, hablando bajito sobre las mantas. Los niños se duermen con la luz en la cara. La doctora se tumba boca arriba entre dos tanques. Mira el techo, blanco de luz. Hace como que es un cielo.",
            Parrafo.PENSAMIENTO to "La doctora piensa que la fiesta imita un día de arriba que casi nadie de aquí ha vivido, y que aun así todos saben cómo se hace: se tumban, se callan, se dejan calentar. Piensa que hay cosas que se heredan sin libros. Piensa que a lo mejor el cuerpo se acuerda de lo que la cabeza olvidó.",
        ),
        "permisos" to unDia(
            Parrafo.MANANA to "Hoy se anuncian los permisos de hijos del año, en el comedor grande, después de comer: el Consejo lee los nombres de las parejas despacio, uno por uno. Este año son pocas. Siempre son pocas. Cada nombre, un aplauso. Entre nombre y nombre, un silencio en el que se oye respirar a los que esperan el suyo.",
            Parrafo.GENTE to "Esta tarde las parejas con permiso pasan por las mesas recibiendo abrazos, y las que no lo tienen abrazan también, con la cara apretada. La doctora se acuerda del año en que leyeron su nombre: estaba en el laboratorio y se enteró por otro. Bajó corriendo con la bata llena de tierra. Le aplaudieron igual.",
            Parrafo.PENSAMIENTO to "La doctora piensa que en el Claustro un hijo es una cuenta de aire, agua, comida y cama, y que por eso hay permisos. Piensa que la cuenta está bien hecha. Piensa en la cara de los que no oyeron su nombre. Piensa que eso no sale en la cuenta. Piensa que debería.",
        ),
        "revision_destello" to TextoEspecial(listOf(
            mapOf(
                Parrafo.MANANA to "Hoy empieza la revisión del destello, dos días en que nadie sube ni baja, y la sala redonda está abierta como un reloj desmontado: placas en el suelo, cables en ovillos, un olor a ozono que se queda en la ropa. Bruno, el ingeniero, va de una pieza a otra con una linterna en la boca.",
                Parrafo.GENTE to "Esta tarde la doctora baja a la sala del destello a llevarle a Bruno una taza de hierbas, como cada año. Él se la bebe sin mirar, de un trago, con los ojos en un circuito. Le explica cómo funciona. Ella no entiende nada. Asiente. Lo que entiende es que a él le brilla la cara.",
                Parrafo.PENSAMIENTO to "La doctora piensa que el destello es lo único del Claustro que lleva a otro sitio, y que dos días al año no lleva a ninguno. Piensa que a todos les da un poco de alivio, aunque nadie lo diga. Piensa que una puerta cerrada da paz. Piensa que no sabe si eso es bueno.",
            ),
            mapOf(
                Parrafo.MANANA to "Hoy es el segundo día de la revisión del destello, y el Claustro se ha acostumbrado deprisa a estar cerrado: nadie espera a nadie de arriba, nadie prepara un viaje. La sala redonda sigue abierta. Bruno ha dormido en ella. Dice que la máquina se pone nerviosa si la deja sola.",
                Parrafo.ANOCHECER to "Al bajar las lámparas, en la sala del destello hacen la prueba: un zumbido que sube, un chasquido, un fogonazo azul en el círculo vacío. Bruno levanta el puño. Los que esperaban en la puerta aplauden sin saber muy bien a qué. Mañana se podrá subir otra vez. Nadie tiene que subir mañana.",
            ),
        )),
        "siega" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza la siega grande, tres días en que medio Claustro baja a los jardines con tijeras, cestas y cajas: se cortan las hojas, se arrancan las raíces, se vacían los tanques para sembrarlos otra vez. Los niños no tienen escuela. Toda la nave huele a tallo cortado, un olor verde y dulce que se pega al pelo.",
                Parrafo.TRABAJO to "A esta hora siega lechugas en una fila de tanques, con las tijeras de Botánica y las manos mojadas hasta el codo. Corta, sacude, echa a la cesta. A su lado, un niño de ocho años corta mejor que ella y se lo dice. Ella le da la razón. El niño sigue cortando, satisfecho.",
                Parrafo.PENSAMIENTO to "La doctora piensa que la siega es el único día en que todo el Claustro trabaja en lo mismo y con las manos. Piensa que se nota en las caras. Piensa que deberían segar algo cada semana, aunque no hiciera falta, solo por eso. Lo propondrá en el Consejo. No lo propondrá.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sigue en la siega, arrancando raíces de los tanques: salen enteras, blancas, largas como cabelleras, y gotean. Las echan en un carro para el abono. La doctora mira cada raíz antes de tirarla. Es su oficio. No lo puede evitar.",
                "A esta hora sigue en la siega, lavando los tanques vacíos con cepillos largos para la siembra nueva. El agua sucia baja por los desagües con ruido de río. Al final del día los jardines son una nave de vidrio limpio, y parecen más grandes, y más tristes.",
            )),
        ),
        "memoria" to unDia(
            Parrafo.MANANA to "Hoy es el Día de los de afuera, y a media mañana suena en todo el Claustro una sirena larga: un minuto en que nadie se mueve. Se paran las máquinas que se pueden parar. En los jardines se cierra el riego. Se oye el aire de las rejillas, y nada más. Es el único minuto del año en que el Claustro se oye a sí mismo.",
            Parrafo.GENTE to "Esta tarde, en el comedor grande, alguien lee los pocos nombres que se saben de los que se quedaron arriba cuando se selló: los apuntó un portero en un papel que se guarda en un marco. Son once. Los demás no tenían nombre para nadie. Después de los once, la lectora se calla un rato largo. Ese rato es por los otros.",
            Parrafo.PENSAMIENTO to "La doctora piensa que los de afuera tuvieron hijos, y los hijos hijos, y que algunos viven todavía arriba, entre las ruinas de las pantallas. Piensa que el Claustro les dedica un minuto al año y les tiene miedo los otros trescientos sesenta y cuatro días. Piensa que las dos cosas no pegan. Nadie lo dice.",
        ),
        "consejo" to unDia(
            Parrafo.MANANA to "Hoy es la sesión grande del Consejo, en la sala de la cúpula, y se votan uno por uno los proyectos del año que viene. Los jefes de departamento esperan su turno en un banco del pasillo, con las tabletas apretadas contra el pecho, como alumnos a la puerta de un examen. La doctora es la sexta.",
            Parrafo.TRABAJO to "A esta hora defiende el proyecto de la grama ante el Consejo, de pie, con tres gráficas y cinco minutos. Habla de raíces, de tierra limpia, de años. Le preguntan por el coste. Dice el coste. Le preguntan por los plazos. Dice que las plantas no conocen los plazos. Alguien sonríe. Alguien apunta.",
            Parrafo.PENSAMIENTO to "La doctora piensa, sentada en el banco del pasillo mientras votan, que su vida entera depende de una mesa de gente que no sabe distinguir una raíz de un cable. Piensa que no es culpa suya. Piensa que la mesa piensa lo mismo de ella, al revés. Piensa que, con todo, el Claustro funciona.",
        ),
        "noche_larga" to unDia(
            Parrafo.MANANA to "Hoy es la noche larga, y desde la mañana se nota en el Claustro una alegría quieta: se reparten velas de la cera de las colmenas, se cuelgan telas en las puertas, los niños ensayan una canción. Esta noche será la más oscura del año. Y la más ruidosa.",
            Parrafo.TRABAJO to "Hoy la doctora no trabaja: es la noche larga, y el laboratorio se cierra a primera hora, con las bandejas en su riego automático. Pasa la tarde en los jardines, ayudando a preparar los cestos de fruta seca que se reparten a oscuras. Se mancha de pasas hasta los codos. Le dura el olor dos días.",
            Parrafo.ANOCHECER to "Esta noche se apagan todas las lámparas, y en la oscuridad del Claustro suena por los altavoces el viento de arriba, grabado hace muchos años: un silbido que sube y baja, golpes de algo que se suelta, un rugido lejano. Los niños gritan de gusto. La doctora cierra los ojos. Con los ojos cerrados es igual de oscuro, y más grande.",
            Parrafo.PENSAMIENTO to "La doctora piensa que el viento de la grabación es más viejo que el Claustro, y que el de arriba, el de ahora, será otro, con otros golpes. Piensa que nadie ha grabado el de ahora. Piensa que le gustaría. Luego piensa que para eso habría que subir. Se tapa con la manta hasta la nariz.",
        ),

        // ---------- Los de la temporada 1 ----------
        "lauro" to unDia(
            Parrafo.MANANA to "Hoy, al llegar al laboratorio, la doctora ha encontrado a alguien esperándola junto a su mesa, de pie, con las manos juntas: un hombre joven con la bata de Botánica y la franja gris en la manga. Se ha presentado con una voz tranquila. Ha dicho «Mentes me llama Lauro». No ha dicho «me llamo».",
            Parrafo.GENTE to "Esta tarde Lauro ya ha aprendido la mesa entera: dónde va cada pipeta, cuánto se riega cada bandeja, cómo le gustan a ella las etiquetas, con la fecha arriba. Lo ha aprendido mirando una vez. Al terminar ha preguntado si había hecho algo mal. Ella ha dicho que no. Él ha dicho «gracias», como si fuera una pregunta.",
            Parrafo.PENSAMIENTO to "La doctora piensa en el asistente viejo, el que se llevaron ayer a Mentes, y en que no se despidió de él porque no sabía si uno se despide de un Semejante. Piensa que ahora ya es tarde para saberlo. Mira a Lauro limpiar una pipeta. Piensa que esta vez se fijará.",
        ),
        "humo" to unDia(
            Parrafo.MANANA to "Hoy, en la pared de pantallas del comedor grande, en una de arriba a la derecha, se ve humo: una columna fina y gris que sale de entre las ruinas, lejos, y se dobla con el viento. Alguien la señaló a la hora de comer. Ahora hay cuarenta personas delante, con las bandejas en la mano, sin comer.",
            Parrafo.GENTE to "Esta tarde los vigías no dan abasto: todo el mundo les pregunta qué es. Ellos dicen lo que saben, que es poco. Un fuego. Alguien que lo encendió. Alguien que vive allí. En la mesa del Consejo se habla en voz baja. En el laboratorio, Lauro se queda mirando la tableta con el humo, sin decir nada, más rato del necesario. La doctora se da cuenta. No le pregunta.",
            Parrafo.PENSAMIENTO to "La doctora piensa en ese humo, que es un fuego, que es alguien que tenía frío o hambre, ahí arriba, encima de su cabeza. Piensa que siempre supo que había gente. Piensa que saberlo y verlo no es lo mismo. Piensa que esta noche nadie en el Claustro va a dormir del todo.",
        ),
        "germina" to unDia(
            Parrafo.MANANA to "Hoy, al levantar la tapa de la bandeja cuarenta y siete, la doctora ha visto un hilo verde. Uno. Del grosor de un pelo, torcido, saliendo de la tierra gris de arriba, la que mata todo lo que se siembra en ella. Ha cerrado la tapa. La ha vuelto a abrir. Seguía ahí. Se ha sentado en el suelo.",
            Parrafo.TRABAJO to "A esta hora no hace otra cosa que mirar la bandeja cuarenta y siete: la fotografía, la mide, apunta la hora exacta, la temperatura, la humedad, el número de la lámpara. Lauro le trae una silla. Hilda viene del laboratorio de al lado y mira por la lupa un rato largo. Dice: «Bueno». En Hilda, «bueno» es un abrazo.",
            Parrafo.PENSAMIENTO to "La doctora piensa que lleva once años sembrando grama en tierra de arriba y once años viéndola morir, y que hoy ha salido un hilo. Piensa que un hilo no es nada. Piensa que es todo. Piensa que va a ser una noche muy larga sin dormir, y que le da igual.",
        ),
        "apagon" to unDia(
            Parrafo.MANANA to "Hoy las lámparas han parpadeado al encenderse, tres veces, y luego se han quedado a media luz. El altavoz dice que el reactor tiene una avería y que no hay que preocuparse. Lo dice dos veces. A la segunda, todo el mundo se preocupa. En Botánica se cargan las linternas de manivela.",
            Parrafo.ANOCHECER to "Esta noche, antes de la hora de bajar las lámparas, se ha ido todo: la luz, las rejillas, el zumbido. Un silencio que no se había oído nunca. La doctora ha corrido a oscuras hasta los jardines, con Lauro delante y una linterna, a tapar las bandejas. Sin bombas el agua no corre, y sin agua la grama aguanta seis horas.",
            Parrafo.PENSAMIENTO to "La doctora piensa, sentada a oscuras entre los tanques, esperando la luz, que todo el Claustro cuelga de una máquina, y la máquina de unos pocos que la entienden. Piensa que la grama, en cambio, solo necesita tierra, agua y un poco de luz. Piensa que la grama es más libre que ella. Lo piensa hasta que vuelve la luz.",
        ),
        "pregunta" to unDia(
            Parrafo.MANANA to "Hoy la doctora ha llegado tarde al laboratorio, cosa que no le pasa nunca, porque se ha quedado dormida con el cuaderno en el pecho. Lauro ya lo tenía todo preparado: las bandejas en fila, las pipetas limpias, la tableta abierta en el día de hoy. La ha saludado. Ha esperado. Parecía que quería decir algo.",
            Parrafo.TRABAJO to "A esta hora riega las bandejas con Lauro, y de pronto él se ha parado con la pipeta en el aire y le ha preguntado de qué color es el cielo. No el de las pantallas: el de verdad. Ella ha dicho que azul. Él ha dicho que en las pantallas es gris. Ella ha dicho que depende. Él ha dicho: «¿De qué?».",
            Parrafo.GENTE to "Esta tarde, en la mesa del Consejo, la doctora no oye nada de lo que se dice: está pensando en el azul. Intenta acordarse del cielo que vio a los veintiún años. Se acuerda de las gafas negras, del frío, del vigía con el reloj. Del color, no. Le da vergüenza no saber contestarle a un Semejante.",
            Parrafo.PENSAMIENTO to "La doctora piensa que nadie en el Claustro pregunta de qué color es el cielo, porque se da por sabido. Piensa que Lauro lo ha preguntado porque a él nadie le ha dado nada por sabido. Piensa que a lo mejor es la pregunta más científica que le han hecho en años. No se lo dirá.",
        ),
        "tome" to unDia(
            Parrafo.MANANA to "Hoy, antes de que la doctora se fuera al laboratorio, Tomé se ha plantado en la puerta del cuarto con las manos en los bolsillos y le ha dicho que quiere hacer la prueba de Mentes. Lo ha dicho de un tirón, mirando al suelo, como quien confiesa que ha roto algo. Luego se ha ido a la escuela sin esperar respuesta.",
            Parrafo.GENTE to "Esta tarde la doctora ha buscado a Coral en los jardines y le ha contado lo de Tomé entre dos tanques, en voz baja. Coral se ha reído. Le ha dicho que el chico lleva desde los diez años desmontando todo lo que se mueve, y que qué esperaba. Que Mentes es eso: desmontar lo que se mueve. A la doctora no le ha hecho gracia.",
            Parrafo.PENSAMIENTO to "La doctora piensa que su hijo quiere aprender a hacer Semejantes, y que ella no sabe qué piensa de eso, ella, que lo piensa todo. Piensa en Lauro, que pregunta. Piensa que a lo mejor Tomé quiere saber de dónde salen las preguntas. Piensa que esa sí sería una buena razón. Esta noche no se lo dirá.",
        ),
        "hilda_deja" to unDia(
            Parrafo.MANANA to "Hoy Hilda no ha llegado la primera. Ha llegado tarde, despacio, con el bastón por fin apoyado en el suelo, y ha dejado en la mesa de la doctora una caja de cartón con veinte cuadernos de tapas de tela. Ha dicho que ya está. Que se le cansan los ojos y que las raíces no esperan a nadie.",
            Parrafo.TRABAJO to "A esta hora ayuda a Hilda a vaciar su laboratorio: las macetas, las lupas, una foto de su promoción, un frasco de semillas sin etiqueta que Hilda no sabe de qué son y que no deja tirar. Lauro lo lleva todo en cajas al ala de los mayores. Hilda le da las gracias a Lauro mirándolo a los ojos. Es la única que lo hace.",
            Parrafo.PENSAMIENTO to "La doctora piensa en los cuadernos de Hilda, que ha hojeado por la tarde, y en que en el margen de cada página hay una nota con la misma letra temblona: mira otra vez. Piensa que Hilda se ha pasado sesenta años diciéndose eso. Piensa que ahora se lo dice a ella.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "cientifica", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + CUARENTENA_MANANA + EXPERIMENTO_MANANA + CIERRE_MANANA + ARRIBA_MANANA,
            Parrafo.TIEMPO to TIEMPO + EXPERIMENTO_TIEMPO + ARRIBA_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + CUARENTENA_TRABAJO + EXPERIMENTO_TRABAJO + CIERRE_TRABAJO + ARRIBA_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + CUARENTENA_PUEBLO + EXPERIMENTO_PUEBLO + CIERRE_PUEBLO + ARRIBA_PUEBLO,
            Parrafo.GENTE to GENTE + ARRIBA_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO + ARRIBA_PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + CUARENTENA_ANOCHECER + EXPERIMENTO_ANOCHECER + CIERRE_ANOCHECER + ARRIBA_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
