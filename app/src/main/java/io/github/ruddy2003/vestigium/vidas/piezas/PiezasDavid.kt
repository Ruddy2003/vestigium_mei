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
 * El cuento de David, dibujante de carteles de conciertos en San Francisco, de 1965 a 1969 (Psicodelia). Es una persona
 * inventada en un mundo real. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026, y la
 * voz del tema (`docs/diseno/temas/psicodelia.md`): cálida, abierta, de barrio y de amigos, un poco soñadora; habla de la
 * calle, de la niebla, del cartel de la semana, de la radio de noche, del baile y de la gente.
 *
 * Lo que este cuento cuida (PLAN_TEMAS, 10g, y `docs/diseno/temas/fuentes_psicodelia.md`):
 * - **La regla del tema, la más importante:** las drogas no aparecen nunca, ni de lejos, ni en otros personajes, ni
 *   insinuadas, ni en jerga; tampoco el alcohol ni el tabaco. Nada de la sección 9 de las fuentes. La prueba
 *   (`CuentoG9Prueba`) busca en todos los textos una lista de palabras que no pueden salir.
 * - **Los nombres** (corrección 6): las salas por su calle («la sala de la calle Sutter», «el auditorio de Geary»), la
 *   radio por su frecuencia («el 106.9»), el periódico del barrio sin su título y el festival como «Monterey». Sin
 *   nombres de bandas ni de canciones: la canción con la guitarra de distorsión y la del sitar se describen.
 * - **Las casas, casi todas grises** (corrección 1): una sola pintada en su calle hasta 1969, y la de la señora
 *   Petrakis, desde `pintan_casa` (5). Las calles enteras de colores son de los años 70.
 * - **La gente real sale de lejos:** Bill Graham en el auditorio de Geary, Alfred Roller en un libro viejo, un poeta de
 *   Berkeley que habla del poder de las flores. Nada de lo que hacen con él es inventado sobre ellos.
 * - **Lo que va tal cual** son solo frases comprobadas en el texto de la obra (`fuentes_psicodelia.md`, sección 10), en
 *   inglés, entre comillas españolas, con su autor y su obra al lado, y su español en estilo indirecto (principio 7):
 *   Thoreau, Whitman y Gibran. Lo demás, en estilo indirecto.
 * - **Lo que no se afirma** porque está [sin verificar]: cómo se imprimían los carteles (solo que los de las salas
 *   llevaban tintas fluorescentes), a qué hora terminaban los bailes, qué línea de trolebús pasaba por Haight, el
 *   incienso y las tiendas de discos del barrio.
 *
 * Lo que tiene que respetar de su calendario (`vidas/anios/AnioG9.kt`):
 * - El trabajo: la imprenta del señor Okada desde `imprenta` (1) y hasta su primer cartel (`primer_cartel`, 2); el cartel
 *   de la semana desde `primer_cartel`; las tintas fluorescentes desde `fluorescente` (3); los pinceles de Sal desde
 *   `sal_retira` (5).
 * - Las noches: el sótano de la calle Page de `sotano_page` (1) a `sutter` (2); la sala de la calle Sutter de `sutter` a
 *   `cierra_sutter` (5); el auditorio de Geary, con su cartel, de `primer_cartel` a `graham_se_va` (4); la FM de noche
 *   desde `fm_noche` (3); el tocadiscos desde `tocadiscos` (1).
 * - Su gente: Nora hasta `nora_se_va` (4); Gil hasta `gil_se_va` (5), con su banda desde `gil_banda` (2); Sal de
 *   `rotulista` (1) a `sal_retira` (5), y retirado después; Ellen desde `conoce_ellen` (2), con él desde `ellen_novia` (3) y
 *   en el departamento desde `ellen_se_muda` (4).
 * - El pasaje: quince centavos hasta `pasaje` (5), veinte desde entonces. El periódico del barrio, de `periodico` (2) a
 *   `ultimo_periodico` (4). La comida de los Diggers, de `comida_panhandle` (3) a `diggers_fin` (4).
 * - El estado «monterey» (temporada 3, los tres días del festival): la mañana, el tiempo, el trabajo, el pueblo, la gente y
 *   el anochecer son suyos, para que el cuento no lo deje en la calle Page.
 * - No repite lo que ya dice «Su vida»: que llegó en el autobús de Stockton, el cuarto de la calle Page, las letras de
 *   Viena dichas en una frase.
 */
internal object PiezasDavid {
    private const val MONTEREY = "monterey"

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    /** Los tres días del festival de Monterey (temporada 3). */
    private fun mo(texto: String, hacer: Hacer? = null) = v(texto, estado = MONTEREY, hacer = hacer)

    private val INVIERNO = setOf(12, 1, 2)
    private val VERANO = setOf(6, 7, 8)

    // =====================================================================
    //  La mañana (se despierta pasado el mediodía)
    // =====================================================================

    private val MANANA = listOf(
        v("{nombre} se despierta pasado el mediodía, cuando la niebla ya se ha retirado de la calle y el sol entra de lado por la cortina. Desde la cocina llegan el olor del café y el murmullo de la radio. Se queda un momento quieto, mirando el techo alto de una casa que fue de alguien rico."),
        v("Lo primero que ve {nombre} al abrir los ojos es la mesa de dibujo, inclinada junto a la ventana, con el papel de ayer sujeto con chinchetas. Lo segundo, la luz: blanca si hay niebla, dorada si no. Se pone las gafas redondas antes que los pantalones, porque sin ellas el mundo es una mancha."),
        v("{nombre} baja descalzo a la cocina, donde siempre hay alguien: uno que llega, otro que se va, una cafetera que no se enfría nunca. Se sirve una taza y mira por la ventana del patio las escaleras de madera de las casas vecinas, con su ropa tendida."),
        v("Cuando {nombre} se levanta, la calle ya lleva horas despierta: el lechero pasó, los niños se fueron a la escuela, la señora Petrakis barrió dos veces la escalera de la entrada. Él abre la ventana del primer piso y le llega, lejos, el zumbido de un trolebús, como de abejas."),
        v("La mañana de {nombre} empieza cuando la de los demás ya terminó. Se lava la cara en el lavabo del pasillo y se peina con los dedos. En los nudillos le quedan las manchas de tinta de ayer: azul, negro, un poco de rojo. Las deja. Mañana habrá otras."),
        v("En invierno {nombre} se despierta con la lluvia golpeando el vidrio y el cuarto frío, porque la estufa de gas de la casa la enciende la señora Petrakis cuando le parece. Se envuelve en la manta y se sienta así a la mesa de dibujo, como un monje en su celda.", meses = INVIERNO),
        v("En verano la niebla tarda en irse, y a veces no se va: {nombre} se despierta con la ventana blanca, como si la casa estuviera dentro de una nube. Lejos, sobre la bahía, suena una sirena de niebla, grave y larga. Él se queda escuchándola antes de decidirse a salir de la cama.", meses = VERANO),
        v("Desde que Ellen vive en el departamento, {nombre} se despierta con el olor de su café, más fuerte que el de nadie, y con el tintineo de sus frascos de tinta, que ella lava en el fregadero con un cepillo de dientes viejo. Ella ya está vestida. Siempre está vestida antes que él.", desde = "ellen_se_muda"),
        v("Desde que pintaron la casa, {nombre} abre la ventana y lo primero que ve es el marco amarillo, recién pintado, contra la niebla. Le parece que la casa sonríe. La señora Petrakis dice que es demasiado, y luego sale a la acera a mirarla cuando cree que nadie la ve.", desde = "pintan_casa"),
    )

    private val MONTEREY_MANANA = listOf(
        mo("{nombre} se despierta en Monterey, en la parte de atrás de la camioneta de un amigo, con la boca seca y la niebla de la bahía pegada a los vidrios. Hay gente durmiendo en el pasto, envuelta en mantas de colores. Alguien ya calienta agua en un hornillo. Por un momento no sabe dónde está, y luego sí."),
        mo("La mañana en Monterey huele a pino, a mar y a café hecho en un hornillo. {nombre} se lava la cara en una llave del recinto ferial, junto a un muchacho que se lava los pies. Se miran y se ríen. Nadie de los que están ahí ha dormido bien, y a nadie le importa."),
    )

    // =====================================================================
    //  El tiempo y la estación
    // =====================================================================

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En San Francisco dicen que el tiempo cambia de una cuadra a la otra, y es verdad: en Haight puede haber sol mientras la niebla se come el parque."),
        v("{nombre} mira el cielo desde la escalera de la entrada, como lo mira todo el barrio antes de decidir si lleva chaqueta. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Aquí el frío no viene del invierno sino del mar, y llega sin avisar, a cualquier hora."),
        v("{cielo} En {estacion}, la ciudad huele distinto: a mar, a eucalipto mojado del parque o a asfalto caliente de las cuestas."),
        v("{de_estacion} {cielo} Los que llegan de fuera se ríen del verano de San Francisco hasta que les toca su primera tarde de niebla en manga corta."),
        v("Desde la esquina, calle arriba, se ven los árboles del Panhandle y, detrás, un trozo grande de cielo. {cielo} {de_estacion}"),
    )

    private val MONTEREY_TIEMPO = listOf(
        mo("En Monterey el aire huele a pino y a sal, y la niebla de la bahía va y viene sobre el recinto ferial como un telón que nadie maneja. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un azul que parece recién lavado, y las casas grises se ven casi blancas.",
            "No hay una nube, y desde las cuestas se ven los cerros del otro lado de la bahía.",
            "Hace un día claro, de luz dura, que saca sombras largas de los postes y de los árboles.",
        ),
        Clima.NUBES to listOf(
            "Está nublado, con un gris parejo que borra las sombras.",
            "Pasan nubes bajas desde el mar, deprisa, y la luz se enciende y se apaga sobre los tejados.",
            "El cielo está cubierto, de un blanco de papel sin dibujar.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fina y terca que pone brillantes las aceras y oscuras las casas.",
            "Cae una lluvia fría desde el mar, y el agua baja por las cuestas como por un tobogán.",
            "Llueve a ratos, y entre chaparrón y chaparrón la calle huele a madera mojada.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, una niebla espesa que entra por el Golden Gate y se traga la calle a una cuadra.",
            "La niebla cubre la ciudad, y las casas de enfrente son solo una sombra con ventanas.",
            "Está la niebla, blanca y fresca, que se mete por la ropa y deja gotas en las gafas.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento del mar, fuerte y frío, y hace temblar los vidrios de las ventanas altas.",
            "Hace viento, y las hojas de los eucaliptos del parque vuelan hasta la calle.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en los jardincitos de las casas florecen los geranios y trepa la hiedra.",
            "En primavera los días se alargan, y el viento de la tarde barre la calle.",
            "Es el tiempo de las mañanas claras, antes de que llegue la niebla del verano.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, que aquí quiere decir niebla por la mañana y chaqueta por la tarde.",
            "En verano la ciudad se llena de gente de fuera que no trae abrigo.",
            "Es el tiempo de la niebla, que entra cada tarde por el puente como una marea blanca.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo más cálido del año en San Francisco, aunque nadie de fuera se lo crea.",
            "En otoño se va la niebla, y la luz de la tarde se pone dorada sobre las casas.",
            "Es el tiempo de los días quietos y claros, los mejores de la ciudad.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, el tiempo de las lluvias, y las cuestas brillan de agua.",
            "En invierno oscurece pronto, y a las cinco ya están encendidas las ventanas.",
            "Es el tiempo de los aguaceros que llegan del mar y se van en una hora.",
        ),
    )

    // =====================================================================
    //  El trabajo del tramo (cada texto vale a cualquiera de sus horas)
    // =====================================================================

    private val TRABAJO = listOf(
        v("A esta hora duerme en el colchón del suelo, bajo la ventana, con la cortina cerrada para que la luz no lo despierte. En el pasillo alguien camina de puntillas. Afuera, la ciudad ya empezó su día sin él: pasan coches, ladra un perro, suena un timbre en la casa de al lado.", hacer = Hacer.DORMIR),
        v("A esta hora duerme y sueña con letras: una A que se estira como un tallo, una O que se llena de colores hasta reventar. Se despierta un momento, con la mano buscando un lápiz, y se da cuenta de que no está en la mesa. Se vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, con los dedos todavía manchados de tinta. La casa cruje como un barco viejo. En la planta baja, la señora Petrakis oye en la radio, bajito para no despertar a los de arriba, un programa en otro idioma que solo ella entiende.", hacer = Hacer.DORMIR),
        v("A esta hora duerme junto a Ellen, que se acostó más tarde que él porque se quedó lavando sus vidrios. Ella respira despacio, con una mano manchada de verde sobre la almohada. A los pies de la cama, el gato del vecino, que entró por la ventana sin pedir permiso.", hacer = Hacer.DORMIR, desde = "ellen_se_muda"),

        v("A esta hora come en la cocina del departamento, en la mesa de formica con una pata coja, lo que haya: arroz, frijoles, huevos, pan del día anterior. Cada uno pone algo en la olla. Las cuentas se llevan en una libreta colgada de un clavo, y nadie las revisa.", hacer = Hacer.COMER),
        v("A esta hora se hace un sándwich de mantequilla de cacahuate y lo come de pie, junto a la ventana, mirando la calle. Abajo, un cartero sube la cuesta con la bolsa al hombro. {nombre} piensa que comer de pie es una manera de no perder el tiempo, y lo pierde mirando.", hacer = Hacer.COMER),
        v("A esta hora come con los del departamento, que hablan todos a la vez: de una banda nueva, del alquiler, de un amigo que se fue a México. Alguien toca la mesa con los dedos como si fuera un teclado. La radio, encima de la nevera, pone algo que nadie escucha.", hacer = Hacer.COMER),
        v("A esta hora come una sopa de lata calentada en el hornillo, con galletas saladas, y le parece un banquete: el dinero no alcanza para mucho. La señora Petrakis, que lo sabe, a veces sube un plato de hojas de parra rellenas, lo deja en la mesa sin decir nada y se va.", hacer = Hacer.COMER),
        v("A esta hora come lo que sobró del guiso de los Diggers, que alguien del departamento trajo del Panhandle en un tarro de vidrio. Está frío y sabe a domingo. {nombre} se lo come con una cuchara de madera, de pie, mirando el reloj de la cocina.", hacer = Hacer.COMER, desde = "comida_panhandle", hasta = "diggers_fin"),

        v("A esta hora dibuja en la mesa inclinada, junto a la ventana, con la luz de la tarde de lado. Primero, el lápiz: cajas, líneas de guía, la forma que van a llenar las letras. Luego la tinta china, con el pincel fino, despacio, sin respirar en las curvas. Una letra mal hecha se nota a una cuadra.", hacer = Hacer.TRABAJO),
        v("A esta hora practica letras en papel de envolver, una hoja tras otra: la misma palabra cien veces, cada vez más apretada, hasta que las letras se tocan y llenan su caja como el agua llena un vaso. Dicen los rotulistas que la letra buena es la que no deja aire. Él todavía deja un poco.", hacer = Hacer.TRABAJO),
        v("A esta hora mezcla colores en tarros de mermelada lavados, con un palito, y los prueba en un borde del papel: un naranja junto a un azul, un rojo junto a un verde. Hay parejas que se quedan quietas y parejas que tiemblan cuando se miran. A él le gustan las que tiemblan.", hacer = Hacer.TRABAJO),
        v("A esta hora trabaja en la imprenta del señor Okada: corta papel en la guillotina, entinta la prensa vieja, saca volantes de una tienda de muebles y tarjetas de una boda. El señor Okada no habla mucho y lo mira todo. Cuando algo sale torcido, solo levanta una ceja, y basta.", hacer = Hacer.TRABAJO, desde = "imprenta", hasta = "primer_cartel"),
        v("A esta hora compone a mano, en la imprenta, el texto de un volante: letra de plomo por letra de plomo, al revés, en el componedor. Le gustan más las letras que pinta que las que vienen hechas, pero de estas aprende lo que es el espacio entre una y otra, que es casi todo.", hacer = Hacer.TRABAJO, desde = "imprenta", hasta = "primer_cartel"),
        v("A esta hora dibuja el cartel de la semana: los nombres de las bandas arriba, la fecha y la sala abajo y, en medio, una figura que se enrosca. Las letras tienen que llenar toda la forma aunque se deformen, como en los carteles de Viena. El que lo mire tiene que pararse a leerlo. Esa es la idea.", hacer = Hacer.TRABAJO, desde = "primer_cartel"),
        v("A esta hora corre contra el reloj: el cartel tiene que estar mañana en la imprenta para que el jueves esté en los postes. Moja el pincel, lo seca en un trapo, vuelve a mojarlo. En la radio de la cocina suena algo que no oye. Cuando levanta la cabeza, ya está oscuro.", hacer = Hacer.TRABAJO, desde = "primer_cartel"),
        v("A esta hora repasa el cartel con una lupa, buscando el error que siempre hay: una fecha mal puesta, una letra que se comió a otra. Lo encuentra y lo arregla con blanco de retocar y un pincel de tres pelos. Luego pone el cartel de pie contra la pared y se aleja hasta la puerta para verlo como lo verá la calle.", hacer = Hacer.TRABAJO, desde = "primer_cartel"),
        v("A esta hora dibuja pensando en las tintas fluorescentes: un naranja que brilla, un verde que parece encendido por dentro. En el papel blanco no se ven como serán; tiene que imaginarlas. Es como dibujar a oscuras, y le gusta más de lo que creía.", hacer = Hacer.TRABAJO, desde = "fluorescente"),
        v("A esta hora pinta con los pinceles del viejo Sal, de pelo largo, que todavía huelen a su taller. Los cuida como él le enseñó: lavados, secos, con la punta hacia arriba en un tarro. Cada vez que le sale una curva entera de una sola pasada, se acuerda de él.", hacer = Hacer.TRABAJO, desde = "sal_retira"),

        v("A esta hora baja la calle hasta la parada y espera el trolebús con las manos en los bolsillos de la chaqueta. Llega con su ruido de chispas en los cables, se para con un suspiro, y {nombre} sube y se sienta junto a la ventana, con la carpeta de dibujos en las rodillas.", hacer = Hacer.CAMINO),
        v("A esta hora va en el autobús por la ciudad que se enciende: las tiendas de Market con sus letreros, los coches con los faros puestos, la gente que vuelve cansada del trabajo. Él va en sentido contrario, como siempre: cuando los demás vuelven, él sale.", hacer = Hacer.CAMINO),
        v("A esta hora sube a pie una cuesta que parece una pared, con la niebla que baja al mismo tiempo que él sube. En San Francisco no hay caminos planos, dicen en el barrio, solo cuestas y descansos. Arriba se para un momento a recuperar el aire y a mirar las luces de abajo.", hacer = Hacer.CAMINO),
        v("A esta hora echa sus quince centavos en la cajita del conductor y se agarra de la barra, porque el autobús arranca antes de que uno se siente. Cuenta las monedas que le quedan para volver. Le alcanzan, y le sobra una, que guarda para el café de mañana.", hacer = Hacer.CAMINO, hasta = "pasaje"),
        v("A esta hora echa en la cajita del conductor los veinte centavos que cuesta ahora el pasaje, cinco más que antes. El conductor ni lo mira. {nombre} se sienta atrás, donde se ve toda la calle por la ventanilla, y mira pasar las casas como un cartel que se desenrolla.", hacer = Hacer.CAMINO, desde = "pasaje"),
        v("A esta hora va hacia la calle Sutter, donde la sala de baile ocupa los dos pisos de arriba de un edificio viejo. Desde la esquina ya se oye la música, y en la puerta hay gente esperando. Él lleva bajo el brazo, enrollado, el cartel de la semana que viene.", hacer = Hacer.CAMINO, desde = "primer_cartel", hasta = "cierra_sutter"),

        v("A esta hora, las noches que no hay baile, se queda en la cocina del departamento con los demás, hablando de todo y de nada hasta tarde. Alguien trae una guitarra, alguien canta mal. La señora Petrakis golpea el techo con el palo de la escoba a las once, por costumbre, y nadie le hace caso.", hacer = Hacer.GENTE),
        v("A esta hora pasea por Haight con amigos del barrio, de escaparate en escaparate, viendo quién pasa. Se paran a oír a un muchacho que toca la flauta en una esquina y le dejan unas monedas en el estuche. Luego siguen, sin rumbo, hasta que la niebla los manda a casa.", hacer = Hacer.GENTE),
        v("A esta hora está en un café del barrio, con una taza que se enfría, dibujando en servilletas a la gente de las otras mesas: una muchacha con un sombrero enorme, un viejo que lee, dos que discuten de poesía. Las servilletas se las regala a quien sale en ellas. Algunos las guardan.", hacer = Hacer.GENTE),
        v("A esta hora, en una casa del barrio, unos amigos tocan en la sala con los muebles corridos contra la pared, y él escucha sentado en el suelo, con la espalda en el radiador. Hay más gente de la que cabe. Cuando se va, la música todavía se oye desde la esquina.", hacer = Hacer.GENTE),
        v("A esta hora baja al sótano de la calle Page donde se junta gente a tocar, una pieza larga tras otra, por cincuenta centavos. No hay escenario: los músicos tocan en el suelo, entre todos. {nombre} se sienta contra la pared y dibuja las manos de los que tocan.", hacer = Hacer.GENTE, desde = "sotano_page", hasta = "sutter"),
        v("A esta hora, como cada jueves, viernes y sábado, está en la sala de la calle Sutter, en el piso de arriba, entre cientos de personas que bailan. En las paredes, las luces líquidas: gotas de colores que se abren, se juntan y se separan como si respiraran. Él mira más las paredes que el escenario.", hacer = Hacer.GENTE, desde = "sutter", hasta = "cierra_sutter"),
        v("A esta hora está en el auditorio de Geary, que se llena hasta las escaleras. Las bandas tocan piezas largas, y entre una y otra la gente se sienta en el suelo a esperar. {nombre} busca su cartel en la pared de la entrada: siempre hay alguien mirándolo, y con eso le basta.", hacer = Hacer.GENTE, desde = "primer_cartel", hasta = "graham_se_va"),
        v("A esta hora está junto a la mesa de las luces, al fondo de la sala, donde Ellen mueve con las manos los vidrios llenos de aceite y de tinta sobre el retroproyector. En la pared de enfrente todo se vuelve flores que se abren. Él le pasa los frascos; ella no le da las gracias, porque no hace falta.", hacer = Hacer.GENTE, desde = "conoce_ellen"),
        v("A esta hora, las noches de baile, va a las salas que quedan, más grandes y más lejos, donde ya no conoce a todos los que entran. Las luces siguen en las paredes y la música sigue fuerte. {nombre} se queda al fondo, cerca de la mesa de las luces, como siempre.", hacer = Hacer.GENTE, desde = "cierra_sutter"),

        v("A esta hora, de vuelta en el departamento, pone la radio bajita en la cocina y se sienta a la mesa con un vaso de leche. La casa duerme. Por la ventana, la calle está vacía y mojada de niebla, con los faroles hechos bolas de luz. Es la hora en que mejor piensa.", hacer = Hacer.LIBRE),
        v("A esta hora dibuja para nadie, en el cuaderno, lo que vio en la noche: una mano levantada, el perfil de alguien contra la luz, una letra que se le ocurrió en el autobús. Lo que dibuja a esta hora no lo enseña. A veces, una semana después, lo encuentra en un cartel.", hacer = Hacer.LIBRE),
        v("A esta hora se queda sentado en la escalera de la entrada, con la chaqueta puesta, oyendo la ciudad, que casi no hace ruido. Pasa un taxi. Un gato cruza la calle sin prisa. En una ventana de enfrente todavía hay luz: otro que tampoco duerme. Lo saluda con la mano, y el otro le contesta.", hacer = Hacer.LIBRE),
        v("A esta hora pone un disco en el tocadiscos de maleta, con el volumen al mínimo, y se tumba en el suelo del cuarto con los ojos cerrados. Una cara entera, sin levantarse. Cuando el brazo vuelve a su sitio con un clic, el silencio de la casa le parece otra música.", hacer = Hacer.LIBRE, desde = "tocadiscos"),
        v("A esta hora oye el 106.9, donde un locutor de voz tranquila pone discos enteros, una cara tras otra, sin anuncios a gritos, como un amigo que te enseña lo que le gusta. {nombre} no sabe el nombre de la mitad de lo que suena. Le da igual.", hacer = Hacer.LIBRE, desde = "fm_noche"),
        v("A esta hora Gil ensaya en la sala, muy bajito, en el órgano de transistores que compró a plazos, la misma frase una y otra vez. {nombre} se sienta en el sofá a oírlo. Cuando por fin le sale, los dos se ríen sin hacer ruido, para no despertar a la casa.", hacer = Hacer.LIBRE, desde = "gil_banda", hasta = "gil_se_va"),
    )

    /** Los tres días del festival de Monterey. */
    private val MONTEREY_TRABAJO = listOf(
        mo("A esta hora duerme en la parte de atrás de la camioneta de un amigo, estacionada cerca del recinto ferial, sobre unas mantas, con la puerta entreabierta. Huele a pino y a mar. Afuera, alguien sigue tocando una guitarra, muy bajito, para nadie.", Hacer.DORMIR),
        mo("A esta hora come un sándwich sentado en el pasto, fuera del ruedo, con los zapatos quitados. Alrededor hay más gente de la que ha visto nunca junta, y nadie tiene prisa. Un desconocido le ofrece una naranja; él le da la mitad de su pan.", Hacer.COMER),
        mo("A esta hora no dibuja: está en el recinto ferial de Monterey, con el cuaderno cerrado en el bolsillo de la chaqueta, oyendo. Cuando una banda termina y empieza otra, saca el lápiz y apunta una forma, un color, para no olvidarlo. Lo demás no se puede apuntar.", Hacer.TRABAJO),
        mo("A esta hora camina con la gente por las calles de Monterey hacia el recinto ferial, entre puestos de flores y muchachos que reparten volantes. Huele a eucalipto. Las entradas cuestan de tres a seis dólares y medio, y él lleva una de las baratas doblada en la cartera.", Hacer.CAMINO),
        mo("A esta hora está en el ruedo del recinto ferial, en el concierto de la noche, de pie como todos, aunque tenga asiento. La música llega con el viento del mar. Hay tanta gente que el ruedo, dicen, no tenía permiso para tantos.", Hacer.GENTE),
        mo("A esta hora no puede dormir, y da vueltas por el recinto ferial casi vacío, entre papeles y sillas plegables, mientras unos limpian y otros duermen en el pasto. Una niebla fina viene del mar. Piensa en contarlo todo en el departamento, y sabe que no va a saber.", Hacer.LIBRE),
    )

    // =====================================================================
    //  El pueblo y el entorno
    // =====================================================================

    private val PUEBLO = listOf(
        v("{entorno} La calle Page sube en cuesta desde el Panhandle, con sus casas victorianas de madera, altas y estrechas, de ventanas salientes. Casi todas son grises o están desteñidas, y casi todas, divididas en departamentos."),
        v("El barrio se llama Haight-Ashbury por el cruce de dos calles. {entorno} En Haight hay una ferretería, una panadería, tiendas de ropa usada, un café y, al final, el parque."),
        v("{entorno} A una cuadra está el Panhandle, ocho cuadras de árboles entre dos calles que llevan hasta el Golden Gate Park. Allí juegan los niños, pasean los perros y alguien siempre toca algo."),
        v("{entorno} Desde la esquina se ven las otras calles del barrio, cuesta arriba y cuesta abajo, con sus casas grises de molduras blancas, como pasteles viejos. Alguna, pintada de un color que no pide permiso."),
        v("{entorno} En la planta baja vive la señora Petrakis, la dueña, que riega sus geranios en la escalera y sabe a qué hora entra y sale cada uno. Dice que la madera de la casa es buena, y que con eso basta."),
        v("{entorno} La ciudad está hecha de cuestas, y desde lo alto de cada una se ve otra cosa: el puente, la bahía o la niebla que se acerca como una ola. Los trolebuses suben despacio, chisporroteando."),
        v("{entorno} En Haight venden en la calle el periódico del barrio, con sus poemas, sus dibujos y sus anuncios de bailes. Se lee entre varios y se queda en la cocina hasta que alguien envuelve algo con él.", desde = "periodico", hasta = "ultimo_periodico"),
        v("{entorno} La casa de la señora Petrakis es ahora turquesa, con las molduras amarillas, y desde la esquina se ve como una flor entre las grises. Los que pasan se paran a mirarla. La señora Petrakis hace como que no se da cuenta.", desde = "pintan_casa"),
    )

    private val MONTEREY_PUEBLO = listOf(
        mo("Monterey es una ciudad pequeña junto a una bahía, al sur de San Francisco, y estos días está llena de gente que no es de aquí. En el recinto ferial hay puestos de flores, de ropa y de collares, y un ruedo de sillas plegables frente al escenario."),
        mo("Entre un concierto y otro, la gente pasea por el recinto ferial como por una feria de pueblo: hay flores, banderines, comida y, en un rincón, alguien que vende campanillas de templo. {nombre} se compra una con lo que le sobra. Suena como un templo pequeño."),
    )

    /** Los carteles de baile en los postes de la calle (el arco `carteles`). */
    private val ENTORNOS = listOf(
        Entorno("carteles", 0.0, 0.1, frases = listOf(
            "En los postes de la calle solo hay avisos de cuartos en alquiler y de gatos perdidos.",
            "Los postes de la esquina están limpios, con un aviso viejo de una venta de muebles.",
        )),
        Entorno("carteles", 0.1, 0.55, frases = listOf(
            "En los postes de la esquina ya hay algún cartel de baile, con letras que cuesta leer.",
            "En un poste de la esquina, un cartel de colores anuncia el baile del jueves.",
        )),
        Entorno("carteles", 0.55, 0.9, frases = listOf(
            "Los postes de la esquina están llenos de carteles de baile, unos encima de otros.",
            "En cada poste hay tres o cuatro carteles de baile, de colores que se pelean.",
        )),
        Entorno("carteles", 0.9, 1.0, frases = listOf(
            "No queda un poste sin carteles: los de esta semana tapan los de la semana pasada.",
            "Los carteles de baile cubren postes, vallas y escaparates, como una enredadera de colores.",
        )),
    )

    // =====================================================================
    //  La gente cercana
    // =====================================================================

    private val GENTE = listOf(
        v("La señora Petrakis, la dueña de la casa, es viuda y vive en la planta baja con un canario y una foto de su marido de uniforme. Cobra el alquiler el primero de cada mes, en persona, y si alguien se retrasa no dice nada: solo se queda en la puerta un minuto más de la cuenta."),
        v("Su madre le escribe desde Stockton cada dos semanas, con su letra redonda de maestra, y le cuenta del calor, de los vecinos y de su padre, que sigue arreglando tractores en el taller de la carretera. Al final siempre le pregunta si come bien. Él siempre le contesta que sí."),
        v("El señor Kaplan, el del almacén de la esquina, le fía la leche y el pan hasta el viernes y le pregunta cada vez para qué sirven tantos dibujos. {nombre} se lo explica. El señor Kaplan dice que en sus tiempos los carteles servían para vender cosas. {nombre} le dice que estos también."),
        v("Los vecinos de la casa de al lado tienen cuatro niños que juegan a la pelota en la cuesta, y la pelota siempre acaba rodando hasta la esquina. {nombre} la ha rescatado tantas veces que los niños lo llaman por su nombre y le piden que les dibuje cosas en la acera con tiza."),
        v("En el departamento entra y sale gente: amigos de amigos que llegan con una mochila y se quedan una semana en el sofá, primos, músicos sin casa. Nadie pregunta mucho. Hay una sola regla, que el que come friega, y se cumple a medias."),
        v("Gil Ortega comparte el departamento desde antes que él. Es de San José, toca todo lo que tiene teclas y trabaja de día en una tienda de música, cargando pianos. Habla poco y se ríe mucho. Cuando {nombre} llegó, fue el que le dijo cuál era el escalón que cruje.", hasta = "gil_se_va"),
        v("Nora, la otra del departamento, es de Portland y cose ropa con telas de cortinas viejas para las tiendas de Haight. Tiene la máquina de coser junto a la ventana de la sala y cose hasta tarde, con la radio puesta. A él le cose los botones sin que se lo pida, y le cobra en dibujos.", hasta = "nora_se_va"),
        v("El viejo Sal es rotulista desde antes de la guerra: pinta letras de oro en los escaparates, nombres de tiendas en las fachadas, flechas, precios. Tiene un taller en la calle Valencia lleno de tarros de pintura y de pinceles colgados por el mango. Dice que una letra se respira, no se piensa.", desde = "rotulista", hasta = "sal_retira"),
        v("El viejo Sal ya no tiene taller: vive con su hija, al sur de la ciudad, y le escribe postales con letras pintadas a pincel, perfectas, que dicen poco. {nombre} las guarda sujetas al borde de la mesa de dibujo, donde las ve cada tarde.", desde = "sal_retira"),
        v("El señor Okada, el impresor, es un hombre menudo, siempre con un lápiz detrás de la oreja, que imprime en una prensa vieja que él mismo arregla. Habla poco y lo mira todo dos veces. Cuando un trabajo sale bien, dice que está bien, y eso es mucho.", desde = "imprenta"),
        v("Ellen trabaja en las luces de las salas. Es de Chicago, lleva el pelo corto y tiene las manos siempre manchadas de colores, de las tintas que mezcla con aceite entre dos vidrios. Habla de las luces como otros hablan de música. {nombre} la busca con la mirada al fondo de la sala, y ella hace como que no lo ve.", desde = "conoce_ellen", hasta = "ellen_novia"),
        v("Ellen y él salen juntos: los domingos van al parque a ver quién toca, y entre semana ella le enseña a mezclar tintas en un plato y él a ella a pintar una letra de una pasada. Ninguno de los dos aprende mucho. Los dos lo intentan otra vez cada semana.", desde = "ellen_novia", hasta = "ellen_se_muda"),
        v("Ellen vive ahora en el departamento, con sus frascos de tinta en el alféizar y el retroproyector debajo de la mesa de la sala, tapado con un mantel. Cuando él dibuja, ella lee en el sofá, y de vez en cuando le pregunta qué color va a ir en el centro. Nunca acierta. Siempre pregunta.", desde = "ellen_se_muda"),
    )

    private val MONTEREY_GENTE = listOf(
        mo("En Monterey anda con Ellen y con Gil, que no se separan de él más de lo necesario: entre tanta gente, perderse es fácil. Ellen mira las luces del escenario como quien mira el trabajo de otro. Gil sabe quién toca y a qué hora, y los lleva de un lado a otro con el programa doblado."),
    )

    // =====================================================================
    //  El objeto del día
    // =====================================================================

    private val OBJETO = listOf(
        v("{dato_objeto} En el departamento todo es de segunda mano, y todo tiene su historia."),
        v("Hoy {nombre} se fija en algo que tiene a mano todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo piensa un momento mientras trabaja, y luego se le olvida, como se olvidan las cosas que siempre están."),
        v("Las cosas de su cuarto tienen cada una su sitio, aunque nadie más lo sepa. {dato_objeto}"),
        v("{dato_objeto} Dicen que las cosas pequeñas son las que se quedan, cuando las grandes ya se fueron."),
        v("Hay cosas que uno usa tanto que deja de verlas, hasta que un día las mira. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("pincel", "el pincel de rotulista", "un pincel de rotulista", "El pincel de rotulista",
            "El pincel de rotulista tiene el pelo largo y fino, y se carga de pintura una sola vez para hacer una curva entera de una pasada; si se detiene a la mitad, la letra se nota cansada."),
        ObjetoCuento("tinta", "el frasco de tinta china", "un frasco de tinta china", "La tinta china",
            "La tinta china es negra como un pozo y se seca brillante; se trabaja con plumilla o con pincel, y lo que se hace con ella ya no se borra: se tapa con blanco o se empieza otra vez."),
        ObjetoCuento("regla", "la regla T", "una regla T", "La regla T",
            "La regla T se apoya en el borde de la mesa de dibujo y corre de arriba abajo para trazar líneas paralelas; con ella se marcan las guías donde se sientan las letras, aunque luego las letras se salgan."),
        ObjetoCuento("carpeta", "la carpeta de dibujos", "una carpeta de dibujos", "La carpeta de dibujos",
            "La carpeta de dibujos es de cartón, con cintas para cerrarla, y en ella se lleva lo que uno sabe hacer para enseñárselo a quien pueda darle trabajo; la que trajo del valle ya tiene las esquinas blandas."),
        ObjetoCuento("cuaderno", "el cuaderno de bocetos", "un cuaderno de bocetos", "El cuaderno de bocetos",
            "El cuaderno de bocetos es pequeño, para que quepa en el bolsillo de la chaqueta, y en él se apunta lo que pasa delante: una mano, una letra, una forma; lo que no se apunta, se olvida."),
        ObjetoCuento("gafas", "las gafas redondas", "unas gafas redondas", "Las gafas redondas",
            "Las gafas redondas, de armazón de metal fino, son baratas y se tuercen con nada; sin ellas, de lejos, todo es una mancha de colores, y por eso las cuida más que a cualquier otra cosa."),
        ObjetoCuento("tocadiscos", "el tocadiscos de maleta", "un tocadiscos de maleta", "El tocadiscos de maleta",
            "Un tocadiscos de maleta se cierra como un equipaje, con su asa y su altavoz en la tapa, y se lleva de un cuarto a otro; suena pequeño, pero en una casa de madera llena todos los rincones."),
        ObjetoCuento("radio", "la radio de válvulas", "una radio de válvulas", "La radio de válvulas",
            "La radio de válvulas tarda un rato en calentarse antes de sonar, y su lucecita naranja se enciende poco a poco; de noche se oyen en ella emisoras lejanas que de día no llegan."),
        ObjetoCuento("disco", "el disco de larga duración", "un disco de larga duración", "El disco de larga duración",
            "Un disco de larga duración trae dos caras de unos veinte minutos cada una, en una funda de cartón con un dibujo; muchas veces se compra por la funda, antes de haberlo oído."),
        ObjetoCuento("boton", "el botón de solapa", "un botón de solapa", "El botón de solapa",
            "El botón de solapa con el símbolo de la paz es una chapa redonda de las que se venden por unos centavos; el símbolo lo dibujó un inglés para una marcha contra las bombas atómicas, y nadie lo registró nunca."),
        ObjetoCuento("cinta", "la cinta para la frente", "una cinta para la frente", "La cinta para la frente",
            "La cinta para la frente es una tira de tela bordada que sujeta el pelo para que no caiga sobre el dibujo; empezó siendo cosa práctica, y ahora en el barrio la llevan muchos solo porque sí."),
        ObjetoCuento("flor", "la flor", "una flor", "La flor",
            "Una flor en el ojal o detrás de la oreja no cuesta nada: se corta de un jardín o se pide en un puesto, y se regala sin pensarlo, como un saludo."),
    )

    // =====================================================================
    //  Lo que piensa
    // =====================================================================

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en los colores como en gente del barrio: el naranja, que habla fuerte; el azul, que escucha; el verde, que se pelea con el rojo y luego no se separa de él. Se ríe solo."),
        v("{nombre} piensa en una frase de Thoreau, de un libro de segunda mano: «If a man does not keep pace with his companions, perhaps it is because he hears a different drummer.» (Thoreau, Walden). Que quien no lleva el paso de los demás quizá oye otro tambor. A sus {edad} años, le parece bien oír otro."),
        v("{mundo} {nombre} piensa que una ciudad es como un cartel pegado encima de otro: debajo de cada casa gris hubo una de colores, y debajo de esta niebla, otra niebla. Le gustaría saber qué cartel quedará arriba."),
        v("{nombre} piensa en un verso de Whitman, del libro viejo que lleva en la chaqueta: «I sound my barbaric yawp over the roofs of the world.» (Whitman, Leaves of Grass). Que lanza su grito bárbaro sobre los tejados del mundo. Piensa que un cartel es eso: un grito de colores pegado en un poste."),
        v("{mundo} {nombre} piensa en Stockton, en el calor seco del valle y en los campos planos hasta donde se ve, y en que allí nadie le preguntaría para qué sirve una letra bonita. Aquí tampoco se lo preguntan. Aquí no hace falta."),
        v("{nombre} piensa que lo que más le gusta de su oficio es el momento antes de empezar, con el papel en blanco y todas las letras posibles todavía dentro del pincel. Luego hay que escoger una. Piensa que con casi todo pasa igual."),
        v("{nombre} piensa en una frase que Ellen copió en la tapa de su caja de frascos, de un libro que se lee mucho en el barrio: «Work is love made visible.» (Gibran, The Prophet). Que el trabajo es el amor hecho visible. Piensa en las manos manchadas de ella, y en las suyas.", desde = "conoce_ellen"),
        v("{nombre} piensa en el viejo Sal, que le enseñó que una letra se respira, y en que algún día alguien le pedirá a él que se lo enseñe. Piensa que un oficio es una fila de manos que se pasan un pincel, y que a él le toca estar en medio.", desde = "sal_retira"),
        v("{mundo} {nombre} piensa en la casa turquesa, y en que la señora Petrakis tardó años en decir que sí. Piensa que la casa, pintada, sigue siendo la misma, y que eso es lo bueno.", desde = "pintan_casa"),
    )

    /** El estado del mundo de cada temporada (una frase para `{mundo}`). Están las cinco, aunque solo la 1 tenga sus especiales. */
    private val MUNDOS = mapOf(
        "barrio" to listOf(
            "Este año el barrio está tranquilo y barato, lleno de estudiantes, músicos y familias que no se van.",
            "Este año se habla de la guerra en los periódicos, lejos, y de la autopista del Panhandle, cerca.",
            "Este año en las radios suenan guitarras nuevas, y en los cafés del barrio se lee poesía en voz alta.",
        ),
        "salas" to listOf(
            "Este año abren salas de baile con un cartel nuevo cada semana, y los postes cambian de cara cada jueves.",
            "Este año en el barrio hay más gente joven, más tiendas de ropa usada y más música por las ventanas.",
            "Este año en las paredes de las salas empiezan a moverse luces de colores, y nadie sabe bien cómo se hacen.",
        ),
        "verano" to listOf(
            "Este año el barrio sale en las revistas, y llega gente de todo el país con una mochila y una flor.",
            "Este año todo el mundo habla del verano: del que viene, del que está pasando o del que pasó.",
            "Este año los carteles de las salas se arrancan de los postes en cuanto se pegan, y se venden en las tiendas.",
        ),
        "despues" to listOf(
            "Este año el barrio ya no es lo que fue el verano pasado, y los que se quedan lo dicen con orgullo y con pena.",
            "Este año se habla de la guerra más que nunca, y en las paredes hay casi tantos símbolos de la paz como carteles.",
            "Este año unas salas cierran y otras abren, más grandes, y los carteles siguen saliendo cada semana.",
        ),
        "cambio" to listOf(
            "Este año el barrio cambia: cierran tiendas, se van amigos, llegan otros que no conocen a nadie.",
            "Este año en la ciudad pintan más casas de colores, una aquí y otra allá, como flores después de la lluvia.",
            "Este año los carteles de las salas se venden en todo el país, aunque los postes del barrio estén más vacíos.",
        ),
    )

    // =====================================================================
    //  El anochecer
    // =====================================================================

    private val ANOCHECER = listOf(
        v("Al caer la tarde la niebla entra por el Golden Gate y avanza sobre la ciudad como una ola lenta, comiéndose primero el parque y luego las casas, una por una. {nombre} la mira llegar desde la ventana del primer piso. En diez minutos, la acera de enfrente ya no existe."),
        v("Anochece sobre la calle Page, y en las ventanas salientes de las casas se encienden las lámparas, cada una de un color: amarillo, rosa, una verde. {nombre} piensa que, vista desde fuera, la calle es un cartel que se va llenando. Se pone la chaqueta y baja."),
        v("Al caer la tarde la luz se pone dorada sobre las casas grises, y por un momento todas parecen de colores. Luego se apaga. {nombre} intenta guardar ese color en la cabeza para un dibujo, y sabe que no le va a salir. Ningún tintero tiene el color de la tarde."),
        v("De noche, desde la escalera de la entrada, se oyen las mil cosas del barrio: una radio, un piano, una discusión, una risa que baja por la cuesta. {nombre} se queda un rato sentado antes de salir, escuchando. La ciudad suena como si se estuviera afinando."),
        v("Cuando se pone el sol, en Haight se encienden los escaparates y la calle se llena de gente que no tiene prisa por llegar a ningún sitio. {nombre} la cruza entre todos, saludando a medias. Huele a pan de la panadería que cierra tarde y a la niebla que ya viene."),
        v("Anochece, y en la cocina alguien pone agua a hervir, alguien busca una camisa limpia, alguien pregunta adónde se va esta noche. {nombre} no contesta, porque ya lo sabe. Se lava las manos de tinta, que nunca quedan limpias del todo, y apaga la lámpara de la mesa de dibujo."),
        v("En invierno anochece temprano y llueve, y las cuestas brillan bajo los faroles como ríos de luz. {nombre} sale con el cuello de la chaqueta levantado y corre hasta la parada, saltando los charcos. El trolebús llega con los vidrios empañados, lleno de gente que huele a lluvia.", meses = INVIERNO),
        v("En verano la niebla llega a la hora de siempre, puntual como un tren, y la temperatura baja de golpe. Los que vinieron de fuera en camiseta se abrazan a sí mismos en las esquinas. {nombre} ya lo sabe: lleva la chaqueta desde la tarde, aunque haya salido el sol.", meses = VERANO),
        v("Desde que tiene la lámpara de luz negra para probar sus carteles, al anochecer la ventana de su cuarto brilla de un violeta raro que se ve desde la esquina. La señora Petrakis dice que parece una pecera. Él le dice que es la luz de la tinta, y ella no le cree.", desde = "luz_negra"),
    )

    private val MONTEREY_ANOCHECER = listOf(
        mo("Al caer la tarde en Monterey la niebla entra desde la bahía, y los que se quedaron sin asiento se envuelven en mantas en el pasto, junto al ruedo. Las luces del escenario se encienden una a una. {nombre} piensa en la calle Page, a unas horas de aquí, con su ventana a oscuras."),
        mo("Anochece en Monterey, y por encima del ruido del recinto ferial se oyen las gaviotas de la bahía. La gente se pasa abrigos y termos de café. Alguien pinta flores en la mejilla de los que se dejan, y {nombre} se deja."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1 (el 1965)
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES_T1 = mapOf(
        "llega" to unDia(
            Parrafo.MANANA to "Hoy {nombre} llega a San Francisco, el primer día del año, en el autobús que salió de Stockton de noche, con una maleta de cartón y la carpeta de dibujos entre las rodillas. Por la ventanilla ve aparecer la bahía, gris, y luego la ciudad, blanca de niebla, subida a sus cuestas como un pueblo de cuento.",
            Parrafo.TRABAJO to "Hoy no trabaja: busca dónde vivir. Con un periódico doblado en la mano y un plano de la ciudad, sube y baja cuestas, toca timbres, pregunta precios que no puede pagar. A media tarde, un muchacho de una tienda de música le dice que en su departamento de la calle Page sobra un cuarto.",
            Parrafo.GENTE to "El departamento está en el primer piso de una casa victoriana gris, y lo comparten Gil, el muchacho de la tienda de música, y Nora, que cose. La dueña, la señora Petrakis, una viuda vestida de negro, lo mira de arriba abajo en la escalera, le cobra el primer mes y le dice que la madera es vieja pero buena, como ella.",
            Parrafo.ANOCHECER to "Al caer la tarde {nombre} se queda solo en su cuarto vacío, con la maleta en el suelo y la carpeta apoyada en la pared. Abre la ventana: la niebla entra fría, y con ella el ruido de una ciudad que no se parece a nada que conozca. Se sienta en el suelo a oírla. Piensa que ya está aquí, y que ya no se va.",
        ),
        "imprenta" to unDia(
            Parrafo.TRABAJO to "Hoy empieza en la imprenta del señor Okada, cerca de Market: un local estrecho con una prensa vieja, una guillotina, cajas de letras de plomo y olor a tinta y a papel. Le toca barrer, cortar papel y mirar. Por la tarde lo dejan meter en la prensa los volantes de una tienda de muebles, uno por uno, sin romper ninguno.",
            Parrafo.GENTE to "El señor Okada es menudo, de pelo blanco cortado a cepillo, con un lápiz detrás de la oreja. Mira la carpeta de dibujos página por página, sin prisa, y al final dice que las letras están bien, pero que les falta aire entre ellas. Le pregunta si sabe llegar a su hora. {nombre} le dice que aprenderá. El señor Okada casi sonríe.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su primer trabajo en la ciudad es imprimir lo que otros dibujan, y que para empezar está bien. Piensa en su padre, que dice que antes de arreglar un motor hay que haberlo desarmado cien veces. Piensa que él va a desarmar muchos volantes.",
        ),
        "autopista" to unDia(
            Parrafo.PUEBLO to "Hoy en el café del barrio no se habla más que de la autopista: dicen que la ciudad quiere pasarla por el Panhandle, el parque largo y estrecho que está a una cuadra de su casa, y que por eso nadie quiere comprar aquí y los cuartos son tan baratos. Un viejo dice que la pararán. Otro, que nunca paran nada.",
            Parrafo.GENTE to "En la cocina, la señora Petrakis dice que vive en esta casa desde hace cuarenta años y que, con autopista o sin ella, de aquí no la saca nadie. Nora le da la razón. Gil dice que, si la autopista baja los alquileres, él no se queja; la señora Petrakis lo mira de una manera que lo hace callar. {nombre} se ríe dentro de la taza.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el Panhandle, con sus árboles viejos, sus bancos y sus perros, convertido en una cinta de cemento llena de coches. Le parece imposible y le parece probable, las dos cosas a la vez, como casi todo lo que deciden otros. Esa noche lo dibuja de memoria, para tenerlo por si acaso.",
        ),
        "rotulista" to unDia(
            Parrafo.TRABAJO to "Hoy el señor Okada lo manda con un paquete de volantes a una tienda de la calle Valencia, y en el escaparate de al lado ve a un viejo subido en una escalera que pinta letras de oro por dentro del vidrio, al revés, sin regla y sin dibujo, de una sola pasada. {nombre} se queda mirando tanto rato que casi se le olvida entregar el paquete.",
            Parrafo.GENTE to "El viejo se llama Sal y es rotulista desde antes de la guerra. Baja de la escalera, le mira las manos manchadas de tinta y le pregunta si dibuja. {nombre} le enseña la carpeta. Sal pasa las hojas con el pulgar, deprisa, y le dice que tiene buen ojo y mala mano, y que lo segundo se arregla. Que vuelva el sábado.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en las letras de oro del escaparate, que brillaban como si fueran de luz y no de pintura. Piensa que nunca se había fijado en quién pinta los nombres de las tiendas, y que de pronto los ve en todas partes. Vuelve a casa leyendo la ciudad entera, letra por letra.",
        ),
        "primeras_letras" to unDia(
            Parrafo.TRABAJO to "Hoy, en el taller de la calle Valencia, Sal le pone en la mano un pincel de pelo largo y le hace pintar una sola letra, una S, cien veces en papel de periódico. La S tiene que salir de una pasada, sin parar en la curva y sin volver atrás. Las primeras parecen gusanos. La número cien parece casi una S.",
            Parrafo.GENTE to "Sal no corrige: mira, gruñe y le pone otra hoja delante. A media tarde le enseña su truco: la letra no se pinta con los dedos sino con el brazo entero, desde el hombro, y se respira antes de empezar, no a la mitad. Luego le enseña sus pinceles, colgados por el mango, como quien enseña a sus hijos.",
            Parrafo.ANOCHECER to "Al caer la tarde vuelve a casa con el hombro dolorido y los dedos negros, y en el trolebús va pintando eses en el aire con el dedo, sin darse cuenta, hasta que una señora lo mira raro. En la cocina, Nora le pregunta qué le pasa. Él le dice que está aprendiendo a respirar.",
        ),
        "casa_pintada" to unDia(
            Parrafo.PUEBLO to "Hoy, al bajar la cuesta, se fija en una casa victoriana de la calle que hasta ayer era gris: alguien la ha pintado de azules intensos y verdes, con las molduras de otro color, y entre las demás parece un pájaro de colores entre palomas. En la ciudad dicen que un pintor empezó con la suya hace un par de años, y que algunos vecinos lo copian.",
            Parrafo.GENTE to "Delante de la casa se ha parado más gente: una señora que dice que es una vergüenza, un cartero que dice que ya era hora, unos niños que cuentan los colores con el dedo. La señora Petrakis, que pasa con la bolsa de la compra, se queda mirándola un buen rato y no dice nada, que en ella es mucho decir.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que estas casas fueron de colores cuando las hicieron, y que las guerras y los años las pusieron grises. Piensa que pintarlas otra vez es como limpiar un cartel viejo y descubrir que debajo seguía estando el dibujo. Piensa en qué color pintaría la de la señora Petrakis. No se lo va a decir.",
        ),
        "sotano_page" to unDia(
            Parrafo.PUEBLO to "Hoy Gil le cuenta que en un sótano de la calle Page, a unas casas de la suya, se junta gente a tocar por las noches, y que la entrada cuesta cincuenta centavos. No hay cartel ni letrero: se sabe porque se sabe. Desde la acera, si uno se agacha junto a la ventanita del sótano, se oye la música que sube.",
            Parrafo.GENTE to "En el sótano hay una bombilla, unas alfombras viejas y más gente de la que cabe, sentada en el suelo. Los músicos tocan sin escenario, entre todos, piezas largas que no se sabe cuándo terminan. Gil cierra los ojos. Nora lleva el ritmo con el pie. Un muchacho flaco recoge las monedas en una lata de galletas.",
            Parrafo.ANOCHECER to "Esta noche salen del sótano tarde, con los oídos zumbando, y la niebla está tan espesa que la casa de enfrente no se ve. Suben la cuesta los tres, despacio, sin hablar. {nombre} piensa que nunca había oído la música tan cerca: como si la estuvieran tocando dentro de él.",
        ),
        "distorsion" to unDia(
            Parrafo.TRABAJO to "Hoy en la imprenta la radio pone una y otra vez una canción nueva con una guitarra que no suena a guitarra: rasposa, como si la tocaran a través de un papel de lija. Dicen que es un pedal que se pisa. El señor Okada baja el volumen cada vez que suena. Alguien lo vuelve a subir.",
            Parrafo.GENTE to "En la cocina, Gil explica con las manos cómo funciona el pedal: rompe a propósito el sonido de la guitarra, lo ensucia, y por eso suena así. Nora dice que suena a enjambre. La señora Petrakis, desde abajo, dice que suena a avería. {nombre} dice que suena a un color: un naranja que se sale del borde.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que una letra también se puede romper a propósito, como esa guitarra: estirarla, ensuciarla, sacarla de su caja, hasta que suene distinta. Piensa que Sal se enfadaría. Piensa que no se lo va a enseñar a Sal todavía.",
        ),
        "niebla_verano" to unDia(
            Parrafo.MANANA to "Hoy {nombre} se despierta con la ventana blanca: ha llegado la niebla del verano, la que entra por el Golden Gate y ya no se va en semanas. La casa de enfrente es una sombra con ventanas, y las gotas resbalan por el vidrio. Se pone la chaqueta para ir a la cocina, en junio, y no le parece raro.",
            Parrafo.TIEMPO to "Hoy la niebla cubre toda la ciudad, espesa y fresca, y huele a mar. Los que vienen de fuera no se lo creen: el verano de San Francisco es el más fresco de las grandes ciudades del país, y el calor se espera para septiembre. Los de aquí ni lo comentan: sacan el abrigo, como cada año.",
            Parrafo.ANOCHECER to "Al caer la tarde la niebla se pone más espesa, y los faroles se vuelven bolas de luz que no alumbran nada. La señora Petrakis cierra los postigos de la planta baja diciendo que esta humedad se le mete en los huesos. {nombre} sale igual. En la niebla, la ciudad es un dibujo a medio borrar, y le gusta.",
        ),
        "vitrina" to unDia(
            Parrafo.TRABAJO to "Hoy pinta solo, por primera vez, un escaparate entero: el nombre de la lavandería de la esquina, en letras rojas con su sombra negra, al revés y por dentro del vidrio, para que se lean bien desde la calle. Le lleva toda la tarde. Sal pasa a mirar a la mitad, no dice nada y se va, que es su manera de decirle que siga.",
            Parrafo.GENTE to "El señor Duarte, el dueño de la lavandería, que le paga con cinco dólares y un mes de lavado gratis, sale a la acera a mirar el vidrio desde lejos, se rasca la cabeza y dice que está bien. Luego le pregunta si puede poner también el precio de las camisas. {nombre} pinta el precio de las camisas.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que desde hoy, cada vez que alguien lleve su ropa a la esquina, leerá algo que pintó él, sin saberlo. Piensa que es lo más pequeño y lo más grande que ha hecho. Esa noche vuelve dos veces a la esquina, solo para ver el vidrio con la luz de la tienda encendida.",
        ),
        "luces" to unDia(
            Parrafo.GENTE to "Hoy en la cocina un amigo de Gil que viene de fuera cuenta que ha visto, en un salón, unas luces de colores proyectadas sobre una banda mientras tocaba: manchas que se movían con la música, como tinta en el agua. Nadie sabe bien cómo se hace. Gil dice que con una linterna y un vaso. Nora dice que eso no puede ser.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en esas luces que nadie le ha sabido explicar, en colores que se mueven solos sobre las paredes y sobre la gente. Piensa que un dibujo que se moviera sería como un cartel vivo. No sabe cómo se haría. Le da rabia no saberlo, y le da gusto que exista algo que no sabe.",
            Parrafo.ANOCHECER to "Al caer la tarde, en su cuarto, pone delante de la lámpara un vaso de agua con unas gotas de tinta, y en la pared aparece una mancha azul que tiembla y se abre. La mira un buen rato, hasta que la tinta se mezcla del todo y el agua se queda gris. Lo apunta en el cuaderno: tinta, agua, luz.",
        ),
        "libro_viena" to unDia(
            Parrafo.TRABAJO to "Hoy Sal saca de un cajón del taller un libro grande y viejo, con las tapas rotas, de carteles de Viena de hace más de sesenta años, y lo abre sobre la mesa. Las letras de un tal Alfred Roller llenan todo el cartel, apretadas, estiradas, como si las hubieran metido a presión en su forma. {nombre} no se atreve a tocar las páginas.",
            Parrafo.GENTE to "Sal cuenta que el libro era de un patrón suyo, alemán, que se lo dejó al jubilarse, y que esos de Viena se llamaban la Secesión y tenían una revista que se llamaba Primavera Sagrada. Dice que ahora no le interesan a nadie. Luego, al ver la cara de {nombre}, le dice que se lo puede llevar una semana, y que si le cae una gota de café, lo mata.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en esas letras que no dejan aire, que se doblan para caber, y que se pueden leer aunque cueste. Piensa que leer algo que cuesta es como subir una cuesta: al final se ve más. Piensa que algún día hará un cartel así, y que la gente se parará en la calle a descifrarlo.",
        ),
        "calor_septiembre" to unDia(
            Parrafo.MANANA to "Hoy {nombre} se despierta sudando, con el sol entrando de lleno por la ventana, sin una gota de niebla. Abre de par en par. En la acera, la señora Petrakis ha sacado una silla a la sombra y se abanica con un periódico. Desde la ventana, él le pregunta si es así todos los años. Ella dice que sí, y que nadie se lo cree.",
            Parrafo.TIEMPO to "Hoy hace calor de verdad, el que en San Francisco no llega en julio sino en septiembre, el mes más cálido del año: no hay niebla, el cielo está limpio de punta a punta y las cuestas brillan al sol. La ciudad entera parece sorprendida, como si se le hubiera olvidado que tenía verano.",
            Parrafo.ANOCHECER to "Al caer la tarde nadie quiere quedarse dentro: la gente se sienta en las escaleras de las casas, con las ventanas abiertas y la radio puesta, y la calle se llena de voces. {nombre} baja con un vaso de limonada y se sienta en el último escalón. Por una vez, el atardecer es largo y tibio, y nadie mira el reloj.",
        ),
        "tocadiscos" to unDia(
            Parrafo.PUEBLO to "Hoy, en una tienda de cosas usadas de la calle Haight, entre lámparas sin pantalla y sillas cojas, encuentra un tocadiscos de maleta, con su asa y su altavoz en la tapa, que funciona si se le da un golpecito. Cuesta seis dólares. Tiene cuatro. El dueño, que lo ve volver a mirarlo tres veces, se lo deja en cuatro.",
            Parrafo.GENTE to "En el departamento lo reciben como a un huésped importante. Gil lo abre, le limpia el brazo con un pañuelo y le endereza una pieza con unas pinzas. Nora trae los tres discos que tiene. La señora Petrakis sube a ver qué es ese ruido y se queda en la puerta, escuchando, con la escoba en la mano, hasta que termina la cara.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que es la primera cosa grande que compra con su dinero en la ciudad, y que es para oír música, no para trabajar. Piensa que a su padre le parecería un gasto tonto. Luego piensa que su padre tiene en el taller una radio que no apaga nunca. Le escribe para contárselo.",
        ),
        "flores" to unDia(
            Parrafo.PUEBLO to "Hoy en el café del barrio alguien cuenta lo que dicen que propuso un poeta en Berkeley, al otro lado de la bahía, para las marchas contra la guerra: llevar flores, repartirlas, ponerlas donde se espera otra cosa. Lo llama el poder de las flores. En la mesa de al lado se ríen. En la de él, no.",
            Parrafo.GENTE to "Nora vuelve esa tarde con un ramo de margaritas del puesto de la esquina y pone una en cada taza de la cocina, una en el pelo de Gil y otra en el ojal de {nombre}. La señora Petrakis encuentra una en su buzón y sube a preguntar quién ha sido. Nadie confiesa. Ella se la lleva igual.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que una flor contra la guerra se parece más a un dibujo que a un arma: algo que no sirve para nada y que cambia lo que se mira. Piensa que un cartel podría ser eso también. Dibuja una margarita en el borde del cuaderno, y luego otra, y luego una hoja entera de margaritas que se tocan.",
        ),
        "menu_cafe" to unDia(
            Parrafo.TRABAJO to "Hoy pinta el pizarrón del café del barrio: el menú entero, el café, el té, la sopa del día, los pasteles, en tiza de colores, con letras que se curvan y se enroscan alrededor de los precios como enredaderas. Le lleva tres horas y le pagan con comida para un mes. El dueño dice que ahora la gente lee el menú aunque no tenga hambre.",
            Parrafo.GENTE to "Mientras pinta, la gente del café opina: una muchacha le pide que haga más grande la sopa, porque es lo que más se vende; un viejo de boina le dice que la letra se lee mal; un niño quiere que dibuje un gato. Él escucha a todos y hace lo que le parece. Al final dibuja un gato pequeño en una esquina. El niño aplaude.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que unas letras que cuesta leer hacen que la gente se quede mirando más rato, y que mirando más rato se le antoja más la sopa. Piensa que eso es lo que hace un buen cartel: detener. Piensa que el viejo de la boina tiene razón, y él también, y que los dos caben en el mismo pizarrón.",
        ),
        "accion_gracias" to unDia(
            Parrafo.TRABAJO to "Hoy no dibuja: es el día de Acción de Gracias, y la cocina del departamento está ocupada desde temprano por un pavo demasiado grande para el horno, que hay que meter de lado. A él le toca pelar papas, muchas, y vigilar que nadie abra la puerta del horno cada cinco minutos. No lo consigue.",
            Parrafo.GENTE to "A la mesa se sientan ocho, o diez, o los que vayan llegando: los del departamento, amigos que no pudieron volver a sus casas, un vecino que pasaba. La señora Petrakis sube con una bandeja de pasteles de miel y nueces y se queda, aunque había dicho que no. Antes de comer, cada uno dice algo por lo que da las gracias.",
            Parrafo.ANOCHECER to "Al caer la tarde están todos tirados en la sala, llenos, sin ganas de moverse, mientras afuera la niebla cubre la calle. Alguien pone un disco. {nombre} le escribe a su madre una postal contándole el día, plato por plato, con un dibujo del pavo en la esquina, que le sale más grande que la mesa.",
        ),
        "baile_geary" to unDia(
            Parrafo.TRABAJO to "Hoy sale de la imprenta antes de hora, con permiso del señor Okada, que le pregunta adónde va con tanta prisa. Le dice que a un baile. El señor Okada lo mira por encima de las gafas y le dice que en sus tiempos los bailes eran en domingo. {nombre} le dice que este es en viernes. El señor Okada no le encuentra la gracia.",
            Parrafo.GENTE to "Esta noche va con Gil y con Nora al auditorio de Geary, un salón de baile viejo, a un baile benéfico que organiza un tal Bill Graham, el primero que hace allí. Dentro hay más gente de la que esperaba: músicos, poetas, estudiantes, gente del barrio y alguna señora de abrigo que no sabe muy bien dónde está.",
            Parrafo.ANOCHECER to "Esta noche tocan unas bandas, una tras otra, y la gente baila como si se conociera de siempre. {nombre} no sabe bailar, así que mira: las manos, los pies, las caras, la luz amarilla del salón. Salen muy tarde, a la niebla, y vuelven a pie, cuesta arriba, sin sentir las piernas.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en cómo anunciaría él un baile así, si alguien se lo pidiera: con letras que llenaran todo el papel, con colores que se pelearan entre ellos, con un dibujo que hiciera pararse a la gente en la calle. Piensa que nadie se lo va a pedir. Piensa que, por si acaso, ya sabe cómo empezaría.",
        ),
        "sitar" to unDia(
            Parrafo.TRABAJO to "Hoy en la imprenta la radio pone una canción pop que tiene, en medio, un sonido que nunca se había oído en la radio: unas cuerdas que vibran y se doblan, como si la nota no quisiera terminar. El locutor dice que es un sitar, un instrumento de la India. El señor Okada, por primera vez, sube él mismo el volumen.",
            Parrafo.GENTE to "En la cocina, Gil, que sabe de todo lo que suena, explica que el sitar tiene unas cuerdas que no se tocan y que suenan solas, por simpatía, cuando suenan las otras. Nora dice que eso le pasa a ella con la gente. La señora Petrakis dice que en su pueblo había algo parecido, y se pone a cantar bajito una canción de allá.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en esas cuerdas que suenan sin que nadie las toque, solo porque otras suenan cerca. Piensa que los colores hacen lo mismo: un naranja junto a un azul hace temblar al azul. Piensa que en un cartel podría pasar eso, que una letra hiciera sonar a la de al lado.",
        ),
        "navidad" to unDia(
            Parrafo.TRABAJO to "Hoy no dibuja: es Navidad, y la mesa de dibujo está tapada con una sábana para que nadie le ponga tazas encima. En la sala hay un árbol pequeño, comprado en la esquina el último día, adornado con tiras de colores recortadas de pruebas de imprenta y con bolas que la señora Petrakis prestó con la condición de que se las devuelvan enteras.",
            Parrafo.GENTE to "A media tarde suena el teléfono del pasillo y alguien grita su nombre por la escalera: es su madre, de larga distancia desde Stockton, que habla deprisa porque cada minuto cuesta. Le cuenta del pavo, de su padre, de la misa. Le pregunta si está bien. Él le dice que sí, y que le manda un dibujo por correo. Ella dice que ya tiene la pared llena.",
            Parrafo.ANOCHECER to "Al caer la tarde la calle está callada como pocas veces, con luces de colores en algunas ventanas y la niebla quieta. En la cocina, los del departamento comen las sobras de todo lo que cada uno trajo. La señora Petrakis sube con un pan dulce con canela que hace cada año, y a cada uno le corta un trozo y le dice lo que le desea.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "cartelista", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + MONTEREY_MANANA,
            Parrafo.TIEMPO to TIEMPO + MONTEREY_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + MONTEREY_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + MONTEREY_PUEBLO,
            Parrafo.GENTE to GENTE + MONTEREY_GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + MONTEREY_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES_T1,
    )
}
