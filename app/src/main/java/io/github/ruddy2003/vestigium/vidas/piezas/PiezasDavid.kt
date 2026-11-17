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
 * - **El consumo, solo las noches que toca** (el autor quitó la regla de «sin drogas» el 16 nov 2026; la guía,
 *   `docs/diseno/temas/psicodelia.md`, y el plan, `psicodelia_plan.md`, sección 3). La marihuana, los viernes: el
 *   anochecer de esa noche sale de las variantes de [HUMO_VIERNES] (`Variante.noche`), y en ellas David fuma. El LSD y los
 *   hongos, en sus días especiales (los de la clave `noche_tinta` del calendario), con texto propio. Fuera de esas noches y
 *   de cuatro días de la ciudad que lo nombran sin que él consuma (la primera tienda de Haight, la ley del 6 de octubre de
 *   1966, el encuentro del parque y la clínica gratuita), ninguna sustancia se nombra. Lo vigila `CuentoG9Prueba`.
 * - **Cómo se cuenta:** con la voz de siempre, concreta, de barrio, sin adorno. Lo que cambia es lo que ve y oye. Cada
 *   sustancia es distinta: la marihuana afloja y alarga (la música, el hambre, la risa tonta, el sueño); el LSD dura la
 *   noche entera y cambia la forma de las cosas (las superficies respiran, las luces dejan cola, el dibujo que entiende y
 *   luego no); los hongos son más de cuerpo y de tierra, con náusea al principio, y se acaban hacia la una. Sin glorificar
 *   (no le dan ideas geniales; a veces dibuja peor al día siguiente; una noche no pasa nada; el cansancio existe; una
 *   noche es mala para él y otra para un amigo, y pasan sin drama) y sin sermón (ningún narrador advierte, nadie es
 *   castigado). La jerga que celebra no sale nunca, ni siquiera en boca de alguien, y nada toca el seguimiento privado.
 *   Ni el alcohol ni el tabaco aparecen. **Nadie real consume:** la gente real sigue saliendo de lejos.
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
    //  La noche de los viernes: la marihuana (la noche de la semana «humo»)
    // =====================================================================
    //
    // Salen en lugar del anochecer de siempre los viernes que no son días especiales, desde la primera vez (`primer_humo`).
    // En todas fuma él. Unas pasan antes de salir y otras a la vuelta, para que no choquen con lo que hace a esa hora.

    private const val HUMO = "humo"

    /** Una variante del anochecer de los viernes. */
    private fun h(texto: String, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, desde = desde, hasta = hasta, meses = meses, noche = HUMO)

    private val HUMO_VIERNES = listOf(
        h("Es viernes, y antes de salir alguien lía en la mesa de la cocina un cigarrillo de marihuana y lo pasa de mano en mano. {nombre} fuma dos veces, despacio, y tose la segunda. Al rato la radio suena más honda, como si la hubieran bajado a un pozo, y en la cocina se queda un humo dulce que flota debajo de la lámpara, en una capa plana, sin subir ni bajar."),
        h("Al caer la tarde del viernes fuman en la escalera de atrás, la del patio, para que el olor no llegue a la planta baja. {nombre} da una calada larga y la suelta mirando la ropa tendida, que de pronto le parece lo más gracioso del mundo. Se ríe sin poder parar, y los demás se ríen de verlo, y al final nadie sabe de qué. Luego tiene una sed enorme y se bebe dos vasos de agua del grifo, uno detrás de otro."),
        h("Los viernes, cuando fuma, a {nombre} se le abre un hambre de lobo. Esta noche, de vuelta en el departamento, se come de pie media hogaza de pan con mantequilla de cacahuate y un plátano, mirando la nevera abierta como quien mira un escaparate. Cada bocado le dura mucho y sabe a más de lo que es. Cuando por fin se sienta, ya no se acuerda de qué iba a hacer, y le da igual."),
        h("De vuelta del baile, de madrugada, {nombre} y unos amigos se sientan en un banco del Panhandle y fuman marihuana a escondidas de nadie, porque no hay nadie. Los faroles tienen un halo amarillo que se ensancha y se encoge, como si respirara. Alguien cuenta un chiste malo y a los cuatro les da la risa durante diez minutos. Luego tienen frío, y hambre, y suben la cuesta muy despacio, hablando de comida."),
        h("Hay viernes en que fumar le da sueño y nada más. Esta noche {nombre} fuma un poco antes de salir, se sienta en el sofá a esperar a los demás y, cuando se da cuenta, ya se han ido sin él y la casa está a oscuras. Se ríe solo de sí mismo, en voz baja. Se hace un té, se come tres galletas saladas y se acuesta temprano, por una vez, con la radio puesta."),
        h("Los viernes, antes de salir, se reparten el cigarrillo de marihuana entre cuatro o cinco, y cada uno lo sostiene un momento de más. {nombre} fuma, se lo pasa a quien tiene al lado y nota cómo se le aflojan los hombros, que llevaba subidos desde la tarde. Las cucharas del escurridor brillan. La conversación da vueltas sobre sí misma, cada vez más despacio, y nadie tiene prisa por salir."),
        h("Antes de salir, Gil enciende un cigarrillo de marihuana junto a la ventana abierta y se lo pasa a {nombre}, que fuma y lo devuelve sin hablar. La niebla entra por la ventana y se mezcla con el humo, y por un momento no se sabe dónde acaba una y empieza el otro. Gil dice que la niebla también se fuma, si uno se descuida. {nombre} se ríe tanto que tiene que sentarse en la escalera.", hasta = "gil_se_va"),
        h("Después de fumar, Gil se sienta al órgano de la sala y toca una sola nota, larga, y la deja sonar. {nombre}, tumbado en la alfombra con los ojos cerrados, casi la ve: una franja ancha, de color naranja, que se va estrechando hasta perderse. Se lo dice. Gil toca otra, más grave, y le pregunta de qué color es esa. Morada. Se pasan así media hora, hasta que la señora Petrakis golpea el techo.", desde = "gil_banda", hasta = "gil_se_va"),
        h("Los viernes Nora deja la máquina de coser y fuma con ellos en el sofá, con las piernas cruzadas. {nombre} da dos caladas y se queda mirando los retazos de tela del suelo: un cuadro rojo junto a uno verde, que tiemblan un poco cuando los mira mucho rato. Se lo dice a Nora. Nora dice que a ella eso le pasa sin fumar, de tanto coser de noche, y le da la razón al rojo.", hasta = "nora_se_va"),
        h("Esta noche, después de fumar marihuana en la cocina, {nombre} pone un disco y se tumba en el suelo con la cabeza junto al altavoz. La canción, que conoce de memoria, se le alarga: entre una nota y la siguiente cabe una habitación. Oye cosas que nunca había oído, una pandereta escondida al fondo, alguien que respira antes de cantar. Cuando termina la cara, se queda quieto, esperando a que vuelva.", desde = "tocadiscos"),
        h("Es viernes de baile, y en la escalera de la sala de la calle Sutter alguien le pasa un cigarrillo de marihuana que ya ha dado la vuelta a medio pasillo. {nombre} fuma una vez y lo pasa al siguiente. Arriba, las luces líquidas de la pared le parecen más lentas y más espesas, como miel al sol, y la música le llega un poco tarde, como si viniera de la sala de al lado.", desde = "sutter", hasta = "cierra_sutter"),
        h("Esta noche fuman en el cuarto de {nombre}, sentados en el colchón, y él se queda mirando la mesa de dibujo en la penumbra. El cartel de la semana, sujeto con chinchetas, le parece de pronto muy bueno, luego malísimo, y luego otra vez bueno. Sabe que mañana, con la luz de la tarde, no será ni lo uno ni lo otro. Apaga la lámpara para no tocarlo.", desde = "primer_cartel"),
        h("Esta noche Ellen fuma con él en la escalera de la entrada, antes del baile, y le cuenta que las luces de la sala se mueven mejor si uno no las mira de frente. {nombre} da una calada y prueba a mirar de lado los faroles de la calle. Es verdad: tiemblan. Ellen se ríe de que le haya creído. Él no sabe si era broma, y no se lo pregunta.", desde = "conoce_ellen", hasta = "ellen_se_muda"),
        h("Los viernes, desde que Ellen vive en el departamento, fuman los dos en la cama antes de salir, con la ventana entreabierta. Ella sopla el humo hacia la lámpara para ver cómo se deshace en la luz, y {nombre} lo mira deshacerse con ella, sin prisa, como quien mira un cartel. Llegan tarde a la sala. Los viernes no llegan nunca a tiempo.", desde = "ellen_se_muda"),
        h("De madrugada, de vuelta, fuma lo que quedó de la marihuana del viernes con la radio en el 106.9, bajito. La voz tranquila del locutor le parece la de un amigo que habla desde la cocina de al lado. {nombre} se queda dormido sentado, con la colilla apagada en un plato, y la radio sigue hablando para nadie.", desde = "fm_noche"),
        h("En invierno los viernes fuman en la cocina con la ventana cerrada, porque llueve, y el humo de la marihuana se queda pegado al techo como una nube de tormenta en pequeño. {nombre} lo mira con la cabeza echada atrás en la silla. La lluvia en el vidrio suena más fuerte que de costumbre, y cada gota, si la escucha, cae sola.", meses = INVIERNO),
        h("En verano, los viernes, fuman en el tejado plano de la parte de atrás, sentados en una manta, mientras la niebla tapa la ciudad desde abajo como un algodón. {nombre} fuma, pasa el cigarrillo y se queda mirando cómo se encienden las ventanas dentro de la niebla, blandas y borrosas, como gotas de tinta en papel mojado.", meses = VERANO),
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
        // La marihuana, la primera vez (desde entonces, los viernes), y las cinco noches de LSD de su primer año, cuando
        // todavía es legal: en las primeras mira más de lo que toma.
        "primer_humo" to unDia(
            Parrafo.GENTE to "Esta noche, en la cocina, Gil saca de una lata de té un cigarrillo de marihuana, delgado y torcido, hecho a mano, y lo enciende con la llama del hornillo. Nora da una calada y lo pasa. Cuando le llega a {nombre}, lo mira un momento, como quien mira un pincel que no conoce, y fuma. Tose tanto que Gil le tiene que dar golpes en la espalda.",
            Parrafo.ANOCHECER to "Al rato no pasa nada, y luego pasa algo pequeño: la radio de la nevera suena más cerca y más lenta, y el reloj de la cocina tarda más entre un golpe y otro. {nombre} se ríe de una cosa que dice Nora, y luego no se acuerda de qué era, y eso también le da risa. A medianoche se come él solo un paquete entero de galletas saladas.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que en Stockton esto habría sido un escándalo, y que aquí nadie ha levantado la voz. Piensa que lo que más le ha gustado no es el humo, que sabe a hojas quemadas, sino el rato de después, la música larga y la cocina tibia. Se acuesta con la ropa puesta y duerme como hace meses que no dormía.",
        ),
        "primer_acido" to unDia(
            Parrafo.GENTE to "Esta noche un amigo de Gil que estudia química en Berkeley deja en la mesa de la cocina unos terrones de azúcar envueltos en papel de aluminio. Dice que es LSD, que todavía es legal y que dura toda la noche. Gil y Nora toman uno cada uno. {nombre} parte el suyo con el cuchillo del pan, se come la mitad y guarda la otra en el bolsillo de la camisa, por si acaso.",
            Parrafo.ANOCHECER to "Durante una hora no siente nada y mira a los demás: a Gil, que se ríe mirándose las manos; a Nora, que llora un poco sin estar triste. Luego las vetas de la mesa de formica empiezan a moverse muy despacio, como agua debajo del hielo, y la bombilla del techo echa unos rayos finos que antes no tenía. {nombre} no se levanta de la silla en toda la noche. No le hace falta.",
            Parrafo.PENSAMIENTO to "Al amanecer, con la cocina gris y fría y los dientes apretados sin saber desde cuándo, {nombre} piensa que ha visto la cocina como si la hubiera dibujado otro, uno mejor que él. Piensa que no lo podría contar. Encuentra en el bolsillo la otra mitad del terrón, envuelta en su papel, y la deja en el cajón de los cubiertos, debajo del abrelatas.",
        ),
        "tejado" to unDia(
            Parrafo.TRABAJO to "Hoy dibuja poco y mal, porque sabe lo que viene esta noche y no se concentra: Gil ha conseguido LSD, legal y barato, de un muchacho de la calle Haight, y quieren tomarlo en el tejado de la casa, donde la señora Petrakis no sube nunca. {nombre} pasa la tarde borrando la misma letra, una R que no se deja.",
            Parrafo.ANOCHECER to "Desde el tejado, de noche, ve llegar la niebla por el lado del mar, y con el LSD ya no es una nube: es algo que respira, que se levanta y se echa sobre las casas como un animal blanco. Las luces de las ventanas se estiran en hilos de colores cuando mueve la cabeza. Durante horas {nombre} y Gil no dicen nada. No hace falta decir nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, ya de madrugada, con las manos heladas y la ropa húmeda de niebla, que la ciudad es un dibujo que alguien rehace cada noche, y que esta noche ha visto el lápiz. Al bajar, la escalera tiene un escalón de más, o uno de menos; mañana no se acordará de cuál. Se mete en la cama temblando y no duerme hasta que entra el sol.",
        ),
        "nada" to unDia(
            Parrafo.GENTE to "Esta noche toman LSD en la sala del departamento él, Gil y una muchacha de Portland, amiga de Nora, que ha venido a pasar el fin de semana. Ponen discos, apagan la lámpara grande, encienden una vela. Luego esperan. Gil dice que hay que esperar una hora. Pasa una hora, y pasan dos.",
            Parrafo.ANOCHECER to "A {nombre} no le pasa casi nada. La vela tiene un halo un poco más ancho que de costumbre, los dibujos de la alfombra parecen un poco más cerca, y eso es todo. A la muchacha de Portland, tampoco. A medianoche se ríen de los dos, se comen lo que queda de la sopa de lata y se quedan hablando de sus pueblos hasta tarde, como cualquier noche.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que les han vendido otra cosa, o que el cuerpo tiene sus días, como la niebla. Piensa que la noche ha estado bien de todos modos, y que lo que recordará de ella son las historias de la muchacha sobre los ríos de Oregón, no la vela. Se duerme a las tres, como siempre, sin nada que contar.",
        ),
        "playa" to unDia(
            Parrafo.PUEBLO to "Hoy, al atardecer, toma con Gil el autobús hasta el final de la ciudad, donde la calle se acaba en la arena y empieza el océano. A esa hora la playa está vacía, con la niebla baja y un viento que trae sal. Llevan una manta, dos naranjas y el LSD, que todavía se compra sin esconderse, envuelto en papel de aluminio.",
            Parrafo.ANOCHECER to "Esta noche, en la orilla, cada ola que rompe deja una línea de espuma que brilla un momento, blanca y luego verde, y tarda en apagarse. {nombre} la sigue con los ojos hasta que se le confunde con la siguiente. El ruido del mar le entra por los pies. Las estrellas, entre la niebla, se mueven un poco, o es él. Se le olvida que hace frío, hasta que se acuerda.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, al amanecer, con arena en todas partes y la mandíbula cansada, que nunca podrá pintar esa espuma con ninguna tinta que exista. Piensa que tampoco hace falta. En el primer autobús de la mañana va con la frente pegada al vidrio, y las casas grises le parecen nuevas, recién hechas.",
        ),
        "mesa_dibujo" to unDia(
            Parrafo.TRABAJO to "Hoy, por la tarde, deja la mesa de dibujo preparada como para un trabajo: el papel tenso, los tarros limpios, el pincel de pelo largo. Esta noche quiere probar algo: tomar LSD y pintar, a ver qué sale. Gil le dice que lo que sale de noche hay que mirarlo de día. {nombre} le dice que ya lo sabe.",
            Parrafo.ANOCHECER to "Esta noche, con el LSD, las letras se le mueven en el papel antes de pintarlas: la S se enrosca sola, la O respira, el negro de la tinta china tiene dentro todos los colores. {nombre} pinta durante horas, muy deprisa, una hoja tras otra, convencido de que por fin entiende algo. Sobre las cuatro se le cae el pincel de la mano y se queda dormido en la silla.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, ya de madrugada, que nunca ha pintado tan bien. Mañana, con la luz de la tarde, verá que no hay una hoja que sirva: las letras torcidas, los trazos temblones, los espacios mal, y lo que esta noche entendía ya no lo entenderá. Guardará las hojas en una carpeta aparte y volverá a la S de Sal, cien veces, con el pulso todavía flojo.",
        ),
        "navidad" to unDia(
            Parrafo.TRABAJO to "Hoy no dibuja: es Navidad, y la mesa de dibujo está tapada con una sábana para que nadie le ponga tazas encima. En la sala hay un árbol pequeño, comprado en la esquina el último día, adornado con tiras de colores recortadas de pruebas de imprenta y con bolas que la señora Petrakis prestó con la condición de que se las devuelvan enteras.",
            Parrafo.GENTE to "A media tarde suena el teléfono del pasillo y alguien grita su nombre por la escalera: es su madre, de larga distancia desde Stockton, que habla deprisa porque cada minuto cuesta. Le cuenta del pavo, de su padre, de la misa. Le pregunta si está bien. Él le dice que sí, y que le manda un dibujo por correo. Ella dice que ya tiene la pared llena.",
            Parrafo.ANOCHECER to "Al caer la tarde la calle está callada como pocas veces, con luces de colores en algunas ventanas y la niebla quieta. En la cocina, los del departamento comen las sobras de todo lo que cada uno trajo. La señora Petrakis sube con un pan dulce con canela que hace cada año, y a cada uno le corta un trozo y le dice lo que le desea.",
        ),
    )

    // =====================================================================
    //  Las noches de LSD y de hongos y los días que lo nombran (temporadas 2 a 5)
    // =====================================================================
    //
    // Las temporadas 2 a 5 todavía no tienen escritos sus días especiales; estas noches, sí, porque sin su texto el día
    // diría el título y nada más. También van los cuatro días de la ciudad que nombran el consumo sin que él consuma: la
    // primera tienda de Haight, la ley del 6 de octubre, el encuentro del parque y la clínica gratuita.

    private val ESPECIALES_NOCHES = mapOf(
        // ------------------------------------------------------------------ 1966
        "primera_tienda" to unDia(
            Parrafo.PUEBLO to "Hoy abre en la calle Haight una tienda nueva, en un local que fue una zapatería, con el escaparate pintado a mano. Dentro venden carteles, libros de bolsillo, campanillas y papel de liar, y en el barrio todos saben para qué es: para los que fuman marihuana y toman LSD, que cada vez son más. En el café dicen que es la primera tienda así en todo el país.",
            Parrafo.GENTE to "La señora Petrakis pasa por delante con la bolsa de la compra, mira el escaparate y sigue sin detenerse, con la boca apretada. Gil entra a curiosear y sale con un cartel enrollado bajo el brazo. El señor Kaplan, el del almacén de la esquina, dice que en sus tiempos una tienda vendía lo que la gente necesitaba. Nadie le contesta, pero Gil se ríe.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hace un año nada de esto tenía escaparate, y que ahora lo tiene, con su nombre pintado. Piensa que una ciudad cambia primero en las tiendas y después en la gente, o al revés, no lo sabe. Se fija en las letras del escaparate, que están mal espaciadas, con una E que se come a la R, y piensa que él las habría hecho mejor.",
        ),
        "acido_telas" to unDia(
            Parrafo.GENTE to "Esta noche Nora, que casi nunca quiere, quiere: toma el LSD con él en la sala, sentados en el suelo entre los retazos de tela que va a coser esta semana. Gil está en un ensayo con su banda. La casa está callada, y la señora Petrakis ya ha golpeado el techo con la escoba, como cada noche, aunque nadie hiciera ruido.",
            Parrafo.ANOCHECER to "Con el LSD, las telas empiezan a hablar entre ellas: los cuadros escoceses se deslizan unos sobre otros, las rosas de una cortina vieja se abren y se cierran, un terciopelo morado se vuelve hondo como un pozo. {nombre} mete la mano en él y casi espera mojarse. Nora va cogiendo un retazo y otro, muy seria, y los pone de dos en dos para ver cuáles tiemblan juntos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, de madrugada, que Nora sabe de colores más que nadie que él conozca, y que nunca lo dice. Piensa que esta noche han visto lo mismo, y que eso es raro. Mañana ella coserá una falda con los retazos que más temblaban, y él le dirá que es la mejor que ha hecho, y será verdad, aunque de día tiemblen menos.",
        ),
        "parque_alba" to unDia(
            Parrafo.ANOCHECER to "Esta noche toma LSD con Gil y dos amigos en el departamento, y a las tres, cuando la casa se les queda chica, bajan al parque. Los faroles del Panhandle dejan detrás, cuando {nombre} gira la cabeza, una cola de luz que tarda en borrarse. Los árboles de noche son más altos que los de día. Caminan sin hablar, siguiendo la avenida, hasta que el cielo se pone gris.",
            Parrafo.PUEBLO to "Al amanecer, en el Golden Gate Park, los eucaliptos sueltan tiras de corteza que cuelgan como papel pintado viejo, y debajo el tronco es liso, de colores de piel: crema, rosa, un verde muy pálido. {nombre} pone la palma de la mano en uno y le parece tibio. El pasto está empapado de rocío. Un hombre que pasea un perro los saluda como si nada.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el parque no sabe que existe, y que por eso es tan bonito. Piensa también que tiene frío, que le duelen las piernas y que todavía falta mucho para que se le pase del todo. Vuelven a pie, con la ropa húmeda. Dormirá hasta las cinco de la tarde y se despertará con la cabeza hueca, como un tarro recién lavado.",
        ),
        "disco_entero" to unDia(
            Parrafo.TRABAJO to "Hoy sale pronto de la imprenta y vuelve a casa con un disco nuevo bajo el brazo, uno de larga duración que compró por la funda, con un dibujo de remolinos que le gustó. No lo pone. Lo guarda para la noche, porque esta noche toma LSD solo en su cuarto, con la puerta cerrada, la cortina corrida y la manta extendida en el suelo.",
            Parrafo.ANOCHECER to "Esta noche pone el disco y se tumba con la cabeza junto al altavoz. La primera canción dura una hora, o eso le parece: cada nota se abre como una gota de tinta en el agua y deja su color flotando en el techo. {nombre} ve la guitarra como una línea naranja que se enrosca, y el bajo como un charco morado que se ensancha. Cuando el brazo vuelve a su sitio, se echa a llorar sin estar triste.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que una cara de un disco dura veinte minutos, y que esta noche ha durado una vida. Piensa que no sabe si la música era así o si él la ha hecho así. Mañana lo volverá a poner, sin nada, para saberlo. Ya sabe lo que pasará: sonará más pequeño, y la funda seguirá siendo lo mejor del disco.",
        ),
        "sutter_luces" to unDia(
            Parrafo.GENTE to "Esta noche va con Gil y con Nora a la sala de la calle Sutter, que abrió hace una semana, y en el pasillo de la entrada, antes de subir, se toman el LSD que les ha dado un amigo, con un trago de agua de la fuente. Arriba ya hay cientos de personas. La banda afina. Al fondo, en una tarima, alguien prepara las luces de la pared.",
            Parrafo.ANOCHECER to "Cuando empieza la música, las luces líquidas ya no están en la pared: le caen encima, gotas rojas y verdes que se abren sobre la gente y le resbalan por los brazos. La música tiene forma, y la forma tiene color, y {nombre} no sabe cuál de las tres cosas está oyendo. Baila, él, que no sabe bailar. Durante horas el tiempo no pasa; luego pasa todo de golpe, y son las dos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, en la calle, con la niebla en la cara y los oídos zumbando, que esa sala es un cartel por dentro: letras que se mueven, colores que se pelean, gente metida en el dibujo. Piensa que un cartel de papel nunca podrá ser eso, y que tiene que intentarlo de todos modos. Esa noche ya no duerme.",
        ),
        "mala_noche" to unDia(
            Parrafo.ANOCHECER to "Esta noche el LSD le sale mal. A la hora de tomarlo, la pared de su cuarto empieza a acercarse, muy despacio, y el dibujo que tiene sujeto con chinchetas lo mira con unos ojos que antes no tenía. {nombre} se sienta en el suelo, abrazado a las rodillas, con el corazón golpeándole en el cuello. Piensa que no va a terminar nunca. Sabe que sí, pero no lo cree.",
            Parrafo.GENTE to "Gil lo encuentra así y no le pregunta nada: le trae un vaso de agua, apaga la lámpara del techo, enciende la pequeña, pone un disco suave y se sienta a su lado en el suelo. De vez en cuando le dice que es el LSD, que se pasa, que se mire las manos, que son las de siempre. Se queda allí hasta que entra la luz por la cortina.",
            Parrafo.PENSAMIENTO to "Al amanecer la pared vuelve a su sitio, y el dibujo es solo un dibujo, y un poco malo. {nombre} está tan cansado que no puede ni comer. Piensa que no le ha pasado nada y que ha pasado mucho, las dos cosas. Dormirá hasta la tarde. Cuando se levante, Gil estará en la cocina, como siempre, y ninguno de los dos lo mencionará.",
        ),
        "geary_estrobo" to unDia(
            Parrafo.GENTE to "Esta noche va al auditorio de Geary con Gil y dos amigos de la banda, y toman el LSD en el autobús, cada uno con su terrón de azúcar, como quien se come un caramelo. En la entrada, su cartel de la semana está pegado junto a la puerta. {nombre} se para a mirarlo y le parece que las letras ya empiezan a moverse, aunque todavía es pronto.",
            Parrafo.ANOCHECER to "Dentro, una luz que parpadea muy deprisa corta a la gente en pedazos: cada uno baila a saltos, como en una película a la que le faltaran cuadros. {nombre} se mira la mano y ve cinco manos, una detrás de otra. Se le pierden los amigos. Durante un rato largo no sabe dónde está la puerta, ni le importa. Los encuentra a las dos, sentados en la escalera, riéndose de él.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esa luz que corta es un truco, y que el truco funciona aunque uno lo sepa. Piensa en hacer un cartel con las letras partidas así, a saltos, y sabe que mañana la idea le parecerá tonta. La apunta igual en el cuaderno, con una letra que mañana tampoco va a entender.",
        ),
        "luces_ellen" to unDia(
            Parrafo.GENTE to "Esta noche Ellen le deja quedarse junto a su mesa, al fondo de la sala de la calle Sutter, mientras trabaja. Él ha tomado LSD antes de venir; ella no, porque dice que con las manos así no se puede trabajar. Le pide que no toque nada. {nombre} se sienta en una caja de madera, con las rodillas en la barbilla, y mira.",
            Parrafo.ANOCHECER to "Ellen mueve dos vidrios con aceite y tinta sobre el retroproyector, y en la pared una mancha azul se traga a una roja. Con el LSD, {nombre} ve cada gota por dentro, con sus burbujas, sus orillas que se rizan, sus colores que se pelean y se casan. Ve también las manos de ella, manchadas, que van más deprisa que la música. No sabe qué mira más.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que lo que en la pared parece magia es una mujer con un pañuelo en el pelo y un cuentagotas, sudando bajo una lámpara caliente. Piensa que eso le gusta más que la magia. Al final de la noche ella le dice que tiene cara de haber visto un fantasma. Él le dice que no, que ha visto cómo se hace.",
        ),
        "ultima_legal" to unDia(
            Parrafo.PUEBLO to "Hoy en el café del barrio se habla de la ley nueva: desde el jueves, el LSD será ilegal en California, y lo que hasta ahora se compraba en la calle sin esconderse habrá que conseguirlo de otra manera. Unos dicen que no cambiará nada. Otros, que harán algo en el Panhandle ese día. La mayoría sigue con su café y su periódico.",
            Parrafo.ANOCHECER to "Esta noche lo toman en la cocina, como otras veces, con la radio puesta y la ventana abierta. Nadie habla de la ley. Las baldosas del suelo se ordenan y se desordenan, la lámpara echa rayos de colores finos como pelos, y la cara de Gil, cuando se ríe, se llena de arrugas que se mueven solas. A las cuatro {nombre} se asoma a la ventana, y la calle está igual que siempre.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que dentro de unos días lo que tienen sobre la mesa será un delito, y que la mesa seguirá siendo la misma mesa, con su pata coja. Piensa que una ley es una línea que alguien traza en un papel, como una guía de lápiz, y que las letras a veces se salen de las guías. No sabe qué más pensar de eso, y se va a dormir.",
        ),
        "ley_lsd" to unDia(
            Parrafo.PUEBLO to "Hoy, el día en que el LSD pasa a ser ilegal en California, miles de personas se juntan en el Panhandle, a una cuadra de su casa, contra la ley nueva, y lo hacen a su manera: sin gritos, con campanillas, tambores y gente sentada en el pasto. Algunos llevan la cara pintada. No hay discursos largos. Hay mucha gente que él conoce de vista.",
            Parrafo.GENTE to "{nombre} baja con Gil y con Nora y se quedan en un borde, mirando. Gil reconoce a medio barrio. Nora dice que nunca había visto tanta gente tan tranquila. Los policías miran desde la esquina, con los brazos cruzados, sin saber muy bien qué hacer con una protesta en la que nadie protesta. La señora Petrakis lo mira todo desde su ventana.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hoy no ha tomado nada, y que no le ha hecho falta para que el día sea raro. Piensa en la ley, que desde hoy está en el aire como la niebla, y en que la gente del pasto se porta como si no la hubiera. Esa noche dibuja de memoria a una muchacha que llevaba campanillas atadas a los tobillos.",
        ),
        "primera_ilegal" to unDia(
            Parrafo.GENTE to "Esta noche toman LSD por primera vez desde que es ilegal. Lo trae un amigo de un amigo de Gil, que no se quita la chaqueta, cobra, no se sienta y se va. Gil baja la persiana de la cocina, que nunca bajan, y habla más bajo de lo necesario. Nora dice que parecen ladrones en su propia casa, y se ríen, pero bajito.",
            Parrafo.ANOCHECER to "Luego, con la persiana bajada, la noche es como las otras: los dibujos del hule de la mesa nadan despacio, la llama azul del hornillo se abre en pétalos de gas, la música de la radio se estira. {nombre} se olvida de la ley a la media hora. Se acuerda a las tres, cuando pasa un coche por la calle y los faros barren el techo, y se le encoge el estómago un momento.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que lo que ha cambiado no está en el LSD, sino en la persiana, en el amigo de la chaqueta y en el coche que pasa. Piensa que antes era una cosa de la cocina y ahora es un secreto, y que no le gustan los secretos. Al amanecer Gil sube la persiana y entra la luz, gris, como cualquier mañana.",
        ),
        "lluvia_ventana" to unDia(
            Parrafo.MANANA to "Hoy {nombre} se despierta con la lluvia en el vidrio y decide, antes de levantarse, que esta noche no irá al baile: se quedará en casa, solo, con el LSD que le sobró de la otra vez, la ventana y la lluvia. Pasa la tarde sin prisa, preparando el cuarto como quien espera una visita: la manta, una vela, una naranja en el alféizar.",
            Parrafo.ANOCHECER to "Esta noche toma el LSD con la frente casi pegada al vidrio. Cada gota que resbala lleva dentro toda la calle, al revés y pequeña, con su farol y su casa de enfrente; cuando dos se juntan, las dos calles se juntan. {nombre} pasa horas siguiendo gotas con el dedo por este lado del vidrio. La lluvia hace en el tejado un ruido de arena, muy fino y muy largo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, de madrugada, que nunca se había fijado en que la lluvia está hecha de una en una, gota a gota, como una letra está hecha de trazos. Piensa que mañana se le habrá olvidado. Para que no se le olvide del todo, dibuja una gota en el cuaderno, con la calle dentro, y le sale una mancha. Se ríe y se acuesta.",
        ),
        "escaparates" to unDia(
            Parrafo.PUEBLO to "Hoy los escaparates de la calle Haight ya tienen sus luces de Navidad: guirnaldas de bombillas de colores, estrellas de papel de plata, un árbol de cartón en la ventana de la panadería. A las diez de la noche la calle está llena de gente con abrigo que mira los escaparates sin comprar nada, y huele a pan y a niebla.",
            Parrafo.ANOCHECER to "Esta noche baja con Gil a la calle con el LSD ya empezando, y las bombillas de los escaparates le sueltan, cuando parpadea, unos rayos largos que se cruzan en el aire como los hilos de una red. Los abrigos de la gente tienen su propia luz. {nombre} se queda media hora delante de la panadería, mirando el árbol de cartón, hasta que el panadero sale a preguntarle si está bien. Le dice que sí. Es verdad.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, ya en casa, con los pies helados, que la Navidad del barrio es fea y pobre, y que esta noche le ha parecido la más bonita que ha visto. Piensa que las dos cosas son verdad. Piensa en su madre, que pone el mismo árbol desde hace veinte años, y le dan ganas de llamarla, pero son las cuatro de la mañana.",
        ),
        // ------------------------------------------------------------------ 1967
        "encuentro_parque" to unDia(
            Parrafo.PUEBLO to "Hoy miles de personas llenan los Polo Fields del Golden Gate Park, sentadas en el pasto bajo un sol de invierno: poetas que leen desde un escenario pequeño, bandas que tocan, campanillas, banderas, niños, perros. Muchos han venido contra la ley del LSD de octubre, a su manera. Desde lo alto de una loma, la gente parece una alfombra de lana de muchos colores.",
            Parrafo.GENTE to "Entre la gente, unos muchachos reparten LSD gratis, de mano en mano, como quien reparte volantes. A {nombre} le ponen un terrón en la palma, y él lo mira y lo guarda en el bolsillo de la chaqueta. Hoy no. Hoy quiere ver. Ellen, a su lado, se ríe de su cara de dibujante, que mira todo como si fuera a tener que pintarlo de memoria.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que nunca ha visto tanta gente junta sin que nadie mande. Piensa en la ley, que no se nota en ningún sitio del pasto. Por la noche, en casa, saca el terrón del bolsillo, lo envuelve en papel de aluminio y lo deja en el cajón de los cubiertos, junto al otro, el de la primera vez, que sigue allí.",
        ),
        "ensayo_gil" to unDia(
            Parrafo.GENTE to "Esta noche la banda de Gil ensaya en un garaje de la calle Clayton, entre cajas de refrescos vacías y una lavadora rota, y {nombre} va a oírlos sentado en un neumático. Gil le ha dado LSD en la puerta, y él lo ha tomado. Los músicos no: dicen que tocando no se puede, que se les van los dedos y se pierden en la canción.",
            Parrafo.ANOCHECER to "Cuando suena el bajo, a {nombre} le sube por el suelo de cemento, por las piernas, hasta el pecho, y el pecho le vibra como el parche de un tambor. El órgano de Gil pinta en el aire franjas anchas que se ondulan. La bombilla desnuda del techo se balancea, y cada vez que va y vuelve deja detrás una sombra de luz. Tocan la misma canción once veces. A él le parecen once canciones distintas.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ahora entiende por qué los músicos repiten tanto, y en seguida se le olvida qué era lo que entendía. Piensa en los dedos de Gil, tan seguros. A las cinco, en la calle, el frío le aclara la cabeza de golpe, y está tan cansado que se sienta en un portal a esperar el autobús, aunque todavía no pasa.",
        ),
        "colores_tiemblan" to unDia(
            Parrafo.TRABAJO to "Hoy deja sin terminar el cartel de la semana: tiene el naranja y el azul puestos uno junto al otro, con la misma fuerza, como le gusta, y no sabe por qué tiemblan ni cómo hacer que tiemblen más. Lo pone de pie contra la pared y lo mira desde la puerta. Esta noche toma LSD en su cuarto, con el cartel delante.",
            Parrafo.ANOCHECER to "Esta noche, con el LSD, el borde entre el naranja y el azul del cartel se pone a hervir: una línea fina, blanca, que salta de un lado a otro, que no es de ningún color y que arde. {nombre} lo entiende todo de golpe: cómo se miran los colores, cómo se empujan, por qué los ojos no pueden con los dos a la vez. Lo dice en voz alta en el cuarto vacío, para fijarlo. Luego pasa una hora mirando la línea.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, al amanecer, que mañana lo va a explicar todo. Mañana, cuando se siente delante del cartel, no quedará nada: un naranja junto a un azul y una palabra apuntada en el cuaderno, borde, subrayada dos veces, que no le dirá nada. Terminará el cartel como sabía terminarlo antes. Saldrá bien. No mejor.",
        ),
        "naranja" to unDia(
            Parrafo.GENTE to "Esta noche toman LSD en la cocina él, Gil, Nora y Ellen, que ha venido a comer y se ha quedado. La radio está en el 106.9, que de madrugada pone discos enteros, sin anuncios. Gil hace té que nadie se toma. Nora ha puesto en la mesa el frutero con las naranjas que les manda cada mes una tía suya del valle.",
            Parrafo.ANOCHECER to "A las once {nombre} coge una naranja y empieza a pelarla. La cáscara tiene poros como la luna, y cada poro, al apretarlo, suelta una niebla fina que huele a todo el verano. La pela en una sola tira, despacio, y la tira cae en espiral sobre la mesa como una letra. Cuando termina es la una. Se come los gajos uno por uno, y cada uno es un sol pequeño y frío.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que nunca había mirado una naranja, y que lleva toda la vida comiéndolas. Piensa que Ellen se ha reído de él dos horas, con cariño, y que ella también se ha pasado una hora con una cuchara. Al amanecer guarda la tira de cáscara entre dos hojas del cuaderno. Mañana estará seca, marrón y pequeña, y no se parecerá a nada.",
        ),
        "radio_noche" to unDia(
            Parrafo.ANOCHECER to "Esta noche toma el LSD en su cuarto, a oscuras, con la radio de válvulas en el suelo, junto a la almohada. Su lucecita naranja crece hasta llenar el techo. El locutor del 106.9 pone discos enteros, una cara tras otra, y entre disco y disco habla bajito, y su voz es una línea gris que se pasea por el cuarto. Con los ojos cerrados, {nombre} ve rombos rojos y verdes que giran, como un cartel que se arma solo.",
            Parrafo.GENTE to "A las tres Ellen entra sin hacer ruido, porque ha terminado en la sala, y se tumba a su lado sin quitarse el abrigo. No ha tomado nada. Le pregunta qué oye. {nombre} intenta decírselo y no le salen las palabras, y ella le dice que da igual, que también lo oye, y se quedan los dos mirando la lucecita de la radio.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, al amanecer, que la voz de un desconocido le ha hecho compañía toda la noche, y que no sabe cómo se llama. Piensa que la radio de noche es como una ventana encendida en la casa de enfrente. Cuando el locutor se despide, a las seis, él también se despide, en voz alta, y Ellen se ríe medio dormida.",
        ),
        "escaleras" to unDia(
            Parrafo.ANOCHECER to "Esta noche, con el LSD, la escalera de la casa se vuelve un problema. {nombre} baja a la cocina por agua y cuenta los escalones, como siempre: dieciséis. Al subir son diecinueve. Vuelve a bajar: catorce. La barandilla de madera se le ondula bajo la mano como una serpiente dormida, tibia. Se sienta a mitad de camino y se ríe hasta que le duele la barriga, tapándose la boca.",
            Parrafo.GENTE to "La señora Petrakis se despierta igual: sale a la puerta de la planta baja en bata, con la jaula del canario tapada detrás, y le pregunta desde abajo si está enfermo. Él le dice que no, que está contando. Ella le dice que a estas horas no se cuenta nada, y que se vaya a la cama. Mañana le pondrá mala cara toda la tarde, y le subirá un plato de todos modos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los números son como las letras: parecen fijos y no lo son, dependen de quién los mira y a qué hora. Piensa que eso es una tontería, y que esta noche no lo es. Llega a su cuarto a las cuatro, después de contar treinta y dos escalones, y se duerme vestido, encima de la manta.",
        ),
        "hongos_mexico" to unDia(
            Parrafo.GENTE to "Esta noche un amigo de Ellen que ha vuelto de México saca de la mochila una bolsa de papel con unos hongos secos, oscuros, arrugados como orejas viejas. Dice que en un pueblo de la sierra los usan desde siempre, y que aquí casi nadie los ha visto. Los reparte en la mesa de la cocina, contados, como quien reparte un dulce raro. Saben a tierra y a cartón mojado.",
            Parrafo.ANOCHECER to "Durante media hora {nombre} tiene el estómago revuelto y bosteza sin parar. Luego el cuerpo se le pone pesado y tibio, como lleno de arena caliente, y las vetas del suelo de madera corren despacio, como el agua de un río. Lo que es de madera o de planta está vivo: la mesa, el geranio de la ventana, el mango de la escoba. Llora de risa con Ellen sin saber de qué. A la una se le está pasando.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esto no se parece al LSD: es más de dentro, más de la barriga y de la tierra, y se va antes, como una visita que no se queda a dormir. Piensa que no sabe si volverá a ver hongos así, porque nadie sabe bien dónde se consiguen. A las tres duerme profundamente, por primera vez en una noche así.",
        ),
        "clinica" to unDia(
            Parrafo.PUEBLO to "Hoy abre en el barrio una clínica gratuita, en un piso de la calle Haight, con un letrero hecho a mano: atienden sin cobrar a quien llegue. Desde la primera mañana hay cola en la escalera: muchachos recién llegados con los pies llagados, una muchacha con fiebre, alguno que ha pasado una mala noche con el LSD y todavía no sabe bien dónde está.",
            Parrafo.GENTE to "La señora Petrakis dice que ya era hora de que alguien se ocupara de esos chicos que duermen en el parque, y que ella les llevaría sopa si no fuera por la escalera. El señor Kaplan dice que a los médicos les va a fiar. Ellen conoce a una de las enfermeras, que trabaja allí gratis después de su turno en el hospital, y dice que lleva tres días sin dormir.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el barrio se llena cada semana de gente que llega sin nada, con una mochila y una dirección escrita en la mano, y que no todos tienen una cocina como la suya. Piensa que la clínica es lo contrario de un cartel: no anuncia nada, solo abre la puerta. Le gustaría pintarle el letrero, y no se atreve a ofrecerlo.",
        ),
        "buena_vista" to unDia(
            Parrafo.PUEBLO to "Hoy, al atardecer, sube con Ellen a Buena Vista Park, la loma de árboles torcidos que hay encima del barrio, por los senderos de tierra entre los cipreses y los eucaliptos. Desde arriba se ve la bahía, el centro y, al oeste, la niebla que espera detrás de los cerros como un rebaño. Llevan una manta, agua y el LSD en un papelito doblado.",
            Parrafo.ANOCHECER to "Esta noche, ya con el LSD, ven entrar la niebla. No llega: crece, se derrama por encima de los cerros como leche que hierve, se traga los tejados uno por uno, y las luces de las calles se encienden debajo en racimos blandos, temblorosos, como brasas bajo una manta. {nombre} tiene la mano de Ellen en la suya y no sabe cuál de las dos es la suya. Durante horas no hablan.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que aquí mismo, en febrero, se tomaron de la mano por primera vez, mirando lo mismo, y que esta noche lo ha visto por dentro. Piensa que no se lo podrá contar a nadie, y que no hace falta, porque Ellen estaba. Bajan al amanecer, mojados y callados, y en la calle Haight ya hay un hombre barriendo la acera.",
        ),
        "calle_llena" to unDia(
            Parrafo.PUEBLO to "Hoy la calle Haight está como nunca: llena de gente de todo el país, muchachos de dieciséis años con una mochila, turistas en autobús que miran por la ventanilla, vendedores, perros, guitarras en cada portal. Huele a sudor, a pan y a niebla. Los carteles de los postes se arrancan en cuanto se pegan. Se camina a empujones.",
            Parrafo.ANOCHECER to "Esta noche baja a la calle con el LSD empezando, y es demasiado: las caras se le acercan y se le alejan, cada una con su color, los gritos de un vendedor le cortan el aire en zigzag, alguien le pone una margarita en la mano y otro se la quita. {nombre} siente que la calle respira más deprisa que él. A la media hora da media vuelta, sube la cuesta casi corriendo y cierra la puerta del departamento con las dos manos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, en su cuarto, con la cortina cerrada y un disco suave, que hay noches para la calle y noches para el cuarto, y que esta era de cuarto y no lo supo a tiempo. El resto de la noche es tranquila: las vetas del techo, la música. Piensa que el barrio ya no es suyo como en invierno. No le da pena. Le da cansancio.",
        ),
        "trolebus" to unDia(
            Parrafo.ANOCHECER to "Esta noche toma el LSD con Gil y luego, sin rumbo, se suben al trolebús y lo dejan ir hasta el final de la línea y vuelta. Por la ventanilla, las luces de las tiendas se estiran en cintas que corren junto al vidrio. Cada vez que el trole pasa por un cruce de cables suelta una chispa azul que a {nombre} le cae despacio, como una nevada. El conductor los mira por el espejo y no dice nada.",
            Parrafo.GENTE to "En el trolebús de madrugada va gente que vuelve del trabajo: una mujer con uniforme de enfermera dormida contra la ventanilla, dos hombres del puerto con las manos negras, un viejo que lee el periódico de ayer. {nombre} los mira y los quiere a todos un poco, con una ternura que le da vergüenza. Gil se ha dormido con la boca abierta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esa gente hace todas las noches el camino que él hace hoy una vez, y que para ellos las chispas son solo chispas. Piensa que mañana él también lo pensará. Bajan en su esquina a las cinco, y la cuesta les parece una montaña. Gil dice que la próxima vez se quedan en casa. Siempre lo dice.",
        ),
        "sutter_ellen" to unDia(
            Parrafo.TRABAJO to "Hoy termina el cartel antes de hora, porque Ellen le ha pedido que la ayude esta noche en la sala: el muchacho que le pasa los frascos está enfermo. {nombre} pasa la tarde lavando vidrios con ella en el fregadero, con un cepillo de dientes viejo, y aprendiéndose el orden de los colores, que ella tiene escrito a lápiz en la tapa de la caja.",
            Parrafo.ANOCHECER to "Esta noche toma el LSD a las ocho, cuando ya se sabe los frascos. En la sala, la pared es un mar de tinta que se abre y se cierra con la música, y él le pasa los colores sin equivocarse, rojo, verde, el amarillo espeso, como si los oyera. Ve el calor de la lámpara subir en ondas. Hacia la una Ellen le quita un frasco de la mano y le dice que descanse, y él se sienta en el suelo, debajo de la luz.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, de vuelta, que ha estado del otro lado de las luces, donde se hacen, y que desde allí son más bonitas y menos mágicas. Piensa que mañana tendrá las manos verdes y amarillas, como Ellen. Piensa que la próxima vez prefiere no tomar nada, para no perderse en el orden de los frascos. Se lo dirá a ella mañana.",
        ),
        "hongos_patio" to unDia(
            Parrafo.GENTE to "Esta noche vuelve el amigo de Ellen, el de México, con otra bolsa de papel, más pequeña. Esta vez solo alcanza para tres: Ellen, Gil y él. Se los comen en el patio de atrás, sentados en cajas, entre la ropa tendida de la señora Petrakis, que ya duerme. El primer bocado sabe a sótano. Gil hace una mueca y se traga los suyos de golpe.",
            Parrafo.ANOCHECER to "Esta noche el patio huele a tierra mojada más que nunca. El musgo entre los ladrillos se pone de un verde nuevo, hondo, como terciopelo, y {nombre} pasa los dedos por encima mucho rato. Las sábanas tendidas se inflan con la brisa como velas de un barco. Siente las piernas de plomo y la cara caliente. Se ríen bajito los tres, de nada. Antes de las dos se ha terminado.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los hongos lo dejan más cerca del suelo que de las estrellas, y que eso le gusta. Piensa que el patio, que es feo, con su cubo de basura y su manguera, ha sido esta noche un jardín. Se acuesta temprano para lo que suele, con la ropa oliendo a tierra, y por la mañana tendrá hambre de pan y nada más.",
        ),
        "canario" to unDia(
            Parrafo.ANOCHECER to "Esta noche toma el LSD en la sala con Gil, y es una noche lenta, sin sobresaltos: la alfombra con sus dibujos que respiran, una vela con un halo del tamaño de un plato, un disco detrás de otro. A las cuatro Gil se queda dormido en el sofá. {nombre} sigue despierto, con la cabeza llena de colores que ya no son de nada, los dientes apretados y frío en los pies.",
            Parrafo.GENTE to "Con el primer gris de la ventana empieza a cantar, abajo, el canario de la señora Petrakis. {nombre} baja descalzo y se sienta en el último escalón, junto a su puerta cerrada, a oírlo. Cada nota le llega limpia, separada de las demás, como una gota. La señora Petrakis abre la puerta en bata, lo ve y no dice nada. Le trae un vaso de leche tibia.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que de toda la noche lo mejor ha sido un pájaro amarillo que no sabe nada de nada. Piensa que la señora Petrakis sabe más de lo que dice. Sube con el vaso vacío en la mano y duerme hasta la tarde. Cuando despierte, el canario estará cantando otra vez, y le sonará como siempre.",
        ),
        "cuarto_ellen" to unDia(
            Parrafo.PUEBLO to "Hoy cruza la ciudad hasta el cuarto de Ellen, en una casa de huéspedes del otro lado del parque, cerca del mar, donde el viento sopla siempre un poco más. El cuarto es pequeño: una cama, una cocinilla, un radiador de hierro que silba y su retroproyector tapado con una toalla. Por la ventana se ve una gasolinera, y detrás, oscuro, el océano.",
            Parrafo.ANOCHECER to "Esta noche toman el LSD juntos, sentados en la cama, con la luz apagada. El silbido del radiador es un hilo de colores que cruza el cuarto de una pared a otra, y la gasolinera de enfrente, con sus letreros, es una feria. Ellen enciende el retroproyector sin vidrios, y en la pared queda un cuadrado de luz blanca, vacío, en el que {nombre} ve, durante horas, todo lo que ella podría poner.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, de madrugada, con la cabeza en el regazo de Ellen, que le gustaría vivir con ella, y no se lo dice, porque esta noche todo lo que piensa le parece verdad y quiere decirlo de día. Al amanecer el radiador se apaga con un golpe, y el cuarto se queda frío y pequeño, y él lo sigue pensando. Lo seguirá pensando todo el invierno.",
        ),
        "molino" to unDia(
            Parrafo.ANOCHECER to "Esta noche toman LSD en el departamento él, Ellen y Gil, y a las tres salen a caminar por el parque, de punta a punta, hasta el mar. Los árboles del Golden Gate Park pasan a su lado como gente alta que no habla. Al final, junto a la playa, hay un molino de viento viejo, quieto, que con el LSD se balancea muy despacio contra el cielo, como un barco anclado.",
            Parrafo.PUEBLO to "Al amanecer, la playa del océano está gris y vacía, con la marea baja y las gaviotas caminando por la arena mojada, que brilla como un espejo sucio. Detrás de ellos, la ciudad todavía duerme bajo la niebla. Hace un frío que se mete en los huesos, y el mar hace el mismo ruido que hacía la primera vez que {nombre} vino aquí de noche.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esta playa es el final de la ciudad y el final del país, y que más allá no hay nada hasta otro continente. Piensa que esta noche no ha visto nada que no hubiera visto ya, y que no importa: le ha gustado caminar. Vuelven a pie por el parque, sin hablar, con Ellen colgada de su brazo. En casa dormirá catorce horas.",
        ),
        // ------------------------------------------------------------------ 1968
        "estufa" to unDia(
            Parrafo.MANANA to "Hoy {nombre} se despierta con el cuarto helado y la lluvia en el vidrio, y no sale de la manta hasta que la señora Petrakis enciende la estufa de gas, a media tarde. Nora ya tiene en el pasillo las cajas de su mudanza, y se le ve triste. Para esta noche Gil ha conseguido LSD, y han decidido quedarse en casa, por el frío.",
            Parrafo.ANOCHECER to "Esta noche lo toman en la sala, los tres envueltos en mantas delante de la estufa. La llama azul se abre en pétalos que suben, se separan y vuelven, y el calor que sale de ella se ve: ondas transparentes que tuercen la pared de enfrente. {nombre} no puede dejar de mirarla. Afuera llueve. A ratos Nora le coge la mano, y su mano está fría y la llama caliente, y las dos cosas son muy grandes.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esta es la última noche así con Nora, que se vuelve a Portland dentro de unos días, y que ninguno de los tres lo ha dicho. Piensa que se acordará de la llama azul más que de lo que hablaron. A las cinco Nora se duerme en el sofá, y él la tapa con su propia manta y se queda mirando la estufa, temblando un poco.",
        ),
        "manos" to unDia(
            Parrafo.TRABAJO to "Hoy pinta toda la tarde el cartel de un baile de febrero, en rojo y negro, y cuando termina tiene las manos hechas un mapa: tinta china en las uñas, rojo en los nudillos, un poco de blanco de retocar en el pulgar. No se las lava. Esta noche toma LSD solo, en su cuarto, porque Gil tiene ensayo y Ellen trabaja en la sala.",
            Parrafo.ANOCHECER to "Esta noche, con el LSD, se mira las manos y no puede dejar de mirarlas. Las líneas de la piel son ríos, y la tinta se ha metido por ellos y corre, negra y roja, siguiendo un mapa que él no conocía. Los dedos se alargan cuando los abre. {nombre} cierra el puño y lo vuelve a abrir cien veces, como quien prueba un pincel nuevo. Pasa así una hora entera, y luego otra.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que estas manos llevan haciendo letras desde que llegó, y que nunca les ha dado las gracias. Le da risa la idea, y luego no tanta. Piensa en las manos de su padre, negras de grasa de tractor, que también eran un mapa. Al amanecer se las lava por fin, despacio, con agua caliente, y el agua se pone gris.",
        ),
        "cerros" to unDia(
            Parrafo.PUEBLO to "Hoy, al anochecer, sube a pie con Ellen y con Gil, por una cuesta que no se acaba, hasta lo alto de los dos cerros que hay en medio de la ciudad, desde donde se ve todo: el centro, la bahía, los puentes, las calles rectas que suben y bajan sin preocuparse de las cuestas. Arriba sopla un viento frío que no deja estar quieto a nadie.",
            Parrafo.ANOCHECER to "Esta noche, con el LSD, la ciudad de abajo es un circuito encendido: las calles son hilos de luz que laten, los coches corren por ellos como gotas, los puentes son dos collares tendidos sobre el agua negra. {nombre} ve la cuadrícula de las calles encogerse y ensancharse con su respiración. Cuando entra la niebla, las luces se apagan por barrios, como si alguien las fuera tapando con la mano.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que desde aquí arriba no se ve su calle, ni su casa, ni su ventana, y que eso le da paz. Piensa que la ciudad entera es un cartel que nadie ha firmado. Bajan de madrugada, con las rodillas temblando de la cuesta y del frío. Ellen dice que no vuelve a subir nunca. Volverán en primavera.",
        ),
        "retroproyector" to unDia(
            Parrafo.GENTE to "Desde que Ellen vive en el departamento, el retroproyector está debajo de la mesa de la sala, tapado con un mantel. Esta noche lo saca a la cocina, lo enchufa con un alargador y pone encima un plato de cristal con aceite, agua y tinta. Han tomado LSD los dos y Gil, que se sienta en el suelo con la espalda contra la nevera.",
            Parrafo.ANOCHECER to "En el techo de la cocina se abre una mancha morada que se come a una amarilla, y se mueve con el temblor de la nevera, con los pasos de Gil y con la música de la radio. Ellen sopla en el plato, y el techo entero se encrespa como el mar. {nombre} está tumbado en las baldosas, mirando arriba, y le parece que la cocina va boca abajo y que él cuelga del techo. No tiene miedo. Tiene ganas de reírse, y se ríe.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la cocina de la calle Page, con su mesa coja y su libreta de cuentas colgada del clavo, ha sido esta noche la sala más bonita de la ciudad. Piensa que Ellen lo sabía, y que por eso trajo el plato. A las cinco el aceite se ha mezclado con el agua y el techo es un charco marrón, y apagan la luz.",
        ),
        "cartel_late" to unDia(
            Parrafo.TRABAJO to "Hoy repasa el cartel para luz negra que terminó hace dos semanas, el de tinta fluorescente sobre un fondo negro de terciopelo, y no está contento: el verde no se separa bastante del naranja. Lo deja colgado en su cuarto, con la lámpara de luz negra apuntándole, y decide mirarlo esta noche de otra manera.",
            Parrafo.ANOCHECER to "Esta noche, con el LSD, el cartel late. Bajo la luz violeta, el verde fluorescente sale del papel como un vapor y se queda flotando delante, a un palmo; el naranja se hunde hacia el fondo, y entre los dos se abre un hueco donde caben las letras. {nombre} pasa horas sentado en la cama, con la boca seca. Sus dientes y los puños de su camisa brillan en la oscuridad, como si fueran de otro.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el verde sí se separa, y que lo que le faltaba no era tinta, sino mirarlo a oscuras, que es como lo verá la gente en la sala. Mañana no tocará el cartel. Piensa que a veces lo que no está bien es el ojo y no el dibujo. Lo apunta en el cuaderno antes de que se le olvide, y se le olvida igual.",
        ),
        "hongos_cocina" to unDia(
            Parrafo.GENTE to "Esta noche Ellen trae unos hongos secos que le ha dado una compañera de las luces, que los trajo del sur en una caja de galletas. Son pocos y raros, y nadie sabe bien de dónde vienen. Gil no quiere, porque está ensayando para el disco. {nombre} y ella se los comen sentados en el suelo de la cocina, con un vaso de leche para pasar el sabor, que es de tierra y de moho.",
            Parrafo.ANOCHECER to "Esta noche les sube por las piernas un calor de baño caliente, y una pesadez dulce que los deja tumbados en las baldosas, mirando el techo. Las grietas de la pintura se ramifican como raíces, y crecen, y de las raíces salen hojas pequeñas que se abren y se cierran. Ellen llora de risa con un ruido de hipo, y a él se le contagia. A medianoche ya están sentados otra vez, cansados y tranquilos, comiendo pan.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los hongos son una cosa de dos, de cocina y de suelo, y el LSD una cosa de la ciudad entera. Piensa que no cambiaría lo uno por lo otro. Piensa que la señora Petrakis, con la muñeca todavía vendada, se reiría de verlos tirados en el suelo como niños. Se van a la cama antes de las dos.",
        ),
        "espejo" to unDia(
            Parrafo.ANOCHECER to "Esta noche, a la hora de tomar el LSD, se mira por casualidad en el espejo del lavabo del pasillo, y la cara que lo mira cambia: se alarga, se le hunden los ojos, la nariz le crece y se le encoge, la piel se le llena de un dibujo de escamas finas. No es miedo, todavía, pero se le parece. {nombre} cuelga una toalla sobre el espejo y vuelve a la sala, donde están Ellen y Gil con un disco.",
            Parrafo.GENTE to "En la sala, Ellen le pregunta qué le pasa, y él le dice que se ha visto la cara. Ella le dice que tiene la cara de siempre, un poco más pálida, y le hace mirar la suya, de cerca, y la de ella no cambia. Gil le pasa un vaso de agua y pone otro disco, uno que él conoce de memoria. Al rato ya está bien, y se olvida del espejo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, de madrugada, que la toalla sigue colgada en el pasillo, y que mañana la señora Petrakis, si sube, preguntará por qué. Piensa que hay noches en que uno no quiere verse, y que no pasa nada por no verse. Al amanecer la quita él mismo, y en el espejo hay un muchacho cansado, con ojeras, y ese sí es él.",
        ),
        "amigo_mal" to unDia(
            Parrafo.GENTE to "Esta noche viene a casa Marty, el batería de la banda de Gil, que es de Fresno y tiene diecinueve años, y toman LSD los cuatro en la sala. Marty nunca lo ha tomado. A la hora se levanta, va a la ventana, vuelve, se sienta, se levanta otra vez. Dice que las paredes le hablan y que no le gusta lo que dicen. Tiene la cara blanca y las manos mojadas.",
            Parrafo.ANOCHECER to "Lo cuidan entre los tres. Ellen apaga las luces fuertes y deja una lámpara pequeña con un pañuelo encima; Gil le habla de Fresno, de su madre, de cualquier cosa, con la voz de siempre; {nombre} lo saca a dar vueltas a la manzana, despacio, del brazo. Las casas se doblan un poco a su paso, y él no dice nada de eso. Marty se agarra a su brazo como a una barandilla. Dan siete vueltas.",
            Parrafo.PENSAMIENTO to "Al amanecer Marty se duerme en el sofá, tapado con la chaqueta de Gil, con la boca abierta, como un niño. {nombre} piensa que se ha pasado la noche asustado por él y que casi no se ha dado cuenta de lo que veía. Mañana Marty estará cansado y callado, dará las gracias y comerá dos platos de arroz. No volverá a tomarlo, y nadie le preguntará por qué.",
        ),
        "hongos_cipreses" to unDia(
            Parrafo.PUEBLO to "Hoy, al atardecer, va con Ellen y un amigo de ella hasta el extremo oeste de la ciudad, donde los acantilados caen al mar y los cipreses crecen torcidos, peinados por el viento, todos hacia el mismo lado. Hay senderos de tierra entre las rocas y, abajo, la boca de la bahía, con los barcos que entran. El amigo trae en un frasco unos hongos que nadie sabe de dónde salen.",
            Parrafo.ANOCHECER to "Esta noche los hongos le dan náuseas al principio, y luego un cansancio feliz, de cuerpo. La corteza de los cipreses se mueve como la piel de un animal dormido, y las agujas tiemblan cada una por su cuenta. {nombre} se sienta en la tierra, con la espalda contra un tronco, y siente el árbol respirar contra él, más despacio que él. El mar suena abajo como una respiración enorme. A medianoche tiene frío y ganas de casa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los hongos lo llevan siempre a la tierra, a los árboles, al barro, nunca a la ciudad. Piensa que eso tiene sentido, aunque no sabría decir cuál. Vuelven en el último autobús, que pasa casi vacío, y él va con las manos oliendo a resina hasta la calle Page. Esa noche duerme ocho horas seguidas.",
        ),
        "cansado" to unDia(
            Parrafo.TRABAJO to "Hoy lleva tres días durmiendo mal, por un cartel que no salía y que ha entregado esta tarde en la imprenta, con la tinta todavía fresca. Le duelen los ojos de tanto mirar de cerca. Había quedado con Ellen en tomar LSD esta noche, y lo toman, aunque él sabe, mientras se lo pone en la lengua, que no es la noche.",
            Parrafo.ANOCHECER to "Esta noche los colores llegan, como siempre, y las paredes respiran, como siempre, y a {nombre} le da igual. Está demasiado cansado para mirar. Las luces le parecen bonitas de una manera lejana, como un cartel visto desde la otra acera. A la una se tumba en la cama con los ojos abiertos, y tarda hasta las seis en dormirse, viendo dibujos que no le interesan.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el LSD no le arregla nada, que se suma a lo que ya hay, y que hoy lo que había era cansancio. Piensa que tendría que haberlo sabido. Mañana tendrá la cabeza de cartón y las manos lentas, y el cartel de la semana siguiente le saldrá peor que este, y lo sabrá antes de empezarlo.",
        ),
        "hongos_mal" to unDia(
            Parrafo.GENTE to "Esta noche Gil trae unos hongos que le han dado en un ensayo, secos, en un sobre de papel de estraza. No sabe de dónde salen, ni le importa. Se los comen él, Gil y Ellen en la sala, con un disco de fondo. Saben peor que los otros, a sótano y a hierro. Ellen se come solo la mitad y deja la otra en el sobre.",
            Parrafo.ANOCHECER to "A {nombre} le sientan mal desde el principio: náuseas, sudor frío y una pesadez en la barriga que no se va. Las cosas apenas se mueven; la alfombra se ondula un poco, la lámpara tiene un halo, nada más. Vomita en el lavabo del pasillo, con la frente apoyada en los azulejos, que le parecen el sitio más fresco del mundo. Luego se tumba en el suelo del baño, tapado con una toalla, hasta que pasa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, a las dos, ya en la cama, con la boca amarga, que el cuerpo también tiene su opinión, y que esta noche ha dicho que no. No le da miedo; le da fastidio. Mañana estará flojo todo el día, y dibujará despacio. Ellen le trae agua con azúcar y se sienta en la cama a leer hasta que él se duerme.",
        ),
        "palabras" to unDia(
            Parrafo.GENTE to "Esta noche toman LSD en la cocina solo él y Gil, como al principio, cuando no estaban Ellen ni la banda. Ellen trabaja en la sala. Gil anda raro desde que la banda grabó su disco: dice que quizá se vayan al sur, que allí hay más trabajo. {nombre} no le pregunta. Ponen agua para un té que no se tomarán.",
            Parrafo.ANOCHECER to "A las once Gil coge una cuchara y dice cuchara, y lo repite, y {nombre} también, cuchara, cuchara, hasta que la palabra se despega de la cuchara y se queda flotando sola en la cocina, un ruido redondo sin nada dentro. Les da un ataque de risa que les dura media hora. Luego lo intentan con mesa, con niebla, con Stockton. Ninguna aguanta. Todas se vacían.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que las letras que pinta son también eso, rayas que no quieren decir nada hasta que alguien las lee. Piensa que Gil se va a ir, y que esta noche lo ha sabido sin que se lo diga. Al amanecer no dice nada, y Gil tampoco. Se toman el té frío, de pie, junto a la ventana.",
        ),
        "bufanda" to unDia(
            Parrafo.MANANA to "Hoy {nombre} se despierta pensando en su madre, que estuvo aquí el sábado pasado y miró la sala de baile vacía con la cara con que mira las iglesias. Le dejó una bufanda tejida, gris, que él no se ha quitado en toda la semana. Esta noche toma LSD con Ellen en casa, sin planes, con la estufa encendida.",
            Parrafo.ANOCHECER to "Esta noche, desde la ventana de su cuarto, la ciudad está envuelta en una niebla quieta, y las luces de las casas de enfrente son bolas de lana encendida, cada una de su color. Con el LSD, la bufanda gris tiene dentro todos los colores, como la tinta china, y cada punto del tejido es una letra pequeña. {nombre} pasa una hora leyendo la bufanda. Ellen se duerme. No pasa nada más, y está bien.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su madre tejió esto en las noches de Stockton, punto por punto, mientras él estaba aquí, y que nunca sabrá lo que él ha visto en ella esta noche. Piensa que no se lo contaría, y no por esconderlo, sino porque no tiene palabras para contarlo. Se duerme de día, con la bufanda puesta.",
        ),
        // ------------------------------------------------------------------ 1969
        "carteles_bajo_la_lluvia" to unDia(
            Parrafo.PUEBLO to "Hoy sigue lloviendo, como toda la semana, y en los postes del barrio los carteles de baile se despegan y resbalan, hinchados de agua, dejando ver los de debajo, y los de debajo de esos. Hay esquinas donde se leen tres semanas a la vez. Por las cunetas bajan tiras de papel de colores, cuesta abajo, como peces muertos.",
            Parrafo.ANOCHECER to "Esta noche sale a caminar con el LSD, bajo un paraguas, sin rumbo. Las letras de los carteles mojados se derriten de verdad: corren por el papel, se mezclan con las de debajo, se vuelven palabras que no existen. {nombre} se para delante de un cartel suyo del año pasado y ve cómo la tinta naranja le baja por la cara a la figura del centro, como lágrimas. Le parece bonito y triste.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los carteles se hacen para una semana, y que eso es lo bueno, que no hay que guardarlos. Piensa también que la sala de la calle Sutter cierra dentro de unos días, que lo sabe todo el barrio y que nadie lo dice. Vuelve a casa empapado a las tres, y se acuesta sin secarse el pelo.",
        ),
        "cien_eses" to unDia(
            Parrafo.TRABAJO to "Hoy pasa por el taller del viejo Sal, que dice que va a cerrarlo pronto, porque las manos ya no le obedecen como antes y su hija quiere que se vaya a vivir con ella. {nombre} se queda toda la tarde ayudándolo a ordenar tarros. No hablan mucho. Al despedirse, Sal le aprieta el hombro más de lo normal.",
            Parrafo.ANOCHECER to "Esta noche, en casa, toma el LSD y se sienta a la mesa de dibujo con papel de periódico y el pincel de pelo largo, como la primera tarde con Sal, y pinta una S, y otra, y otra. Cada una se mueve en el papel cuando la termina: se estira, se enrosca, respira como un animal pequeño. Pinta cien. Ninguna es igual a otra. Ninguna es buena. Esta noche no le importa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que Sal le enseñó a respirar antes de la curva, y que esta noche ha respirado cien veces. Piensa que mañana tirará las hojas sin mirarlas, porque sabe cómo serán de día. Piensa que no ha tomado el LSD para pintar mejor, sino para estar con Sal un rato más, aunque Sal no lo sepa. Es la primera vez que lo piensa así.",
        ),
        "media_dosis" to unDia(
            Parrafo.GENTE to "Esta noche Ellen ha quedado con unas amigas, y Gil ensaya hasta tarde. Un muchacho del barrio, de los nuevos, le ofrece LSD en la escalera de la entrada, y {nombre}, que hace semanas que no lo toma, se queda con uno. Se toma la mitad en la cocina, solo, con la radio. La otra mitad la guarda en el cajón de los cubiertos, junto a los terrones viejos.",
            Parrafo.ANOCHECER to "Esta noche es poca cosa, una versión pequeña de las otras: los bordes de la mesa se ondulan un poco, la radio se estira, la bombilla echa unos rayos finos. Lo de siempre, y menos. A {nombre} no le molesta, pero tampoco le interesa tanto como antes. A las dos se lava los dientes, apaga la luz de la cocina y se acuesta, y mira el techo hasta que se duerme.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, de pasada, que hace dos años esto le parecía lo más importante de la semana, y que ahora le parece una noche más, ni mejor ni peor. Piensa que le interesa más el cartel que tiene a medias. No lo piensa mucho más. Mañana se levantará temprano, para lo que suele, y dibujará bien.",
        ),
        "nota_larga" to unDia(
            Parrafo.GENTE to "Desde que Gil se fue a Los Ángeles, su órgano está en la sala, tapado con una sábana, y nadie lo toca. La casa está más callada. Esta noche Ellen y {nombre} toman LSD juntos en la sala, por primera vez en meses, sin decirse por qué. Ella quita la sábana del órgano y lo enchufa.",
            Parrafo.ANOCHECER to "Esta noche {nombre} aprieta una sola tecla, la más grave, y la deja apretada. El sonido llena la sala de abajo arriba, como agua que sube, de un color morado oscuro, y hace temblar los vidrios de la ventana y los frascos de Ellen en el alféizar. Lo deja sonar tanto rato que ya no lo oye: lo ve. Ellen pone la mano encima de la suya y aprieta otra tecla, más aguda, amarilla.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que Gil dejó el órgano para que alguien lo tocara, y que él no sabe tocarlo, y que esta noche lo ha tocado de todos modos. Piensa en escribirle para contárselo, y sabe que no lo hará. Al amanecer vuelven a tapar el órgano con la sábana, con cuidado, como a alguien que duerme.",
        ),
        "hongos_casa_pintada" to unDia(
            Parrafo.MANANA to "Hoy, al abrir la ventana, la casa todavía lo sorprende: hace tres semanas que la pintaron de turquesa, con las molduras amarillas, y aún huele un poco a pintura. La señora Petrakis ha puesto geranios nuevos en la escalera, rojos, para que hagan juego. Para esta noche, Ellen tiene unos hongos que le dejó una amiga que se va de la ciudad.",
            Parrafo.ANOCHECER to "Esta noche se los comen en la escalera de la entrada, cuando la calle ya está tranquila. Tras la náusea de siempre, el cuerpo se le pone tibio y pesado, y los geranios se encienden: cada pétalo rojo tiene venas, y las venas laten despacio. El turquesa de la fachada respira detrás de él como un animal grande y tranquilo. {nombre} apoya la espalda en la puerta y se ríe sin ruido. A la una ya han subido.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que esta casa, que durante cuatro años fue gris, hoy le ha parecido otra, y que en parte es la pintura y en parte los hongos, y que no hace falta separar. Piensa que la señora Petrakis duerme abajo sin saber que su escalera ha sido esta noche un cuadro. Mañana le dirá que los geranios están muy bonitos. Es verdad.",
        ),
        "noche_quieta" to unDia(
            Parrafo.ANOCHECER to "Esta noche toman LSD en su cuarto, con la ventana abierta a la niebla de junio, y no hacen nada: ella lee a ratos un libro que no avanza, él mira las grietas del techo, que se mueven como ramas al viento. Las cosas cambian de forma, como siempre, y {nombre} ya sabe cómo cambian. No le sorprende. Le gusta, despacio, como un disco que ya se conoce. Hablan poco. A las cuatro duermen.",
            Parrafo.GENTE to "Ellen dice, de madrugada, que le gustaría tener un estudio de verdad para las luces, con una pared blanca solo para proyectar. {nombre} le dice que el cuarto de atrás está lleno de trastos, pero que es grande y tiene una pared lisa. Ella lo mira un momento largo y no contesta. Él sabe que mañana se pondrán a vaciarlo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ya no toma LSD para ver algo nuevo, porque lo que ve ya lo ha visto; lo toma, cuando lo toma, para estar así con Ellen, quieto. Piensa que quizá pronto ni eso. No le preocupa. Piensa que hay cosas que se acaban solas, sin que nadie las acabe, como la niebla de la tarde.",
        ),
        "extranos" to unDia(
            Parrafo.PUEBLO to "Hoy la calle Haight está distinta de los otros veranos: escaparates tapados con tablas, gente durmiendo en los portales, vendedores que ofrecen cosas en voz baja, coches de policía más a menudo. Los que llegan ahora no conocen a nadie, y nadie los conoce. Hay menos tiendas de ropa usada y más de comida rápida.",
            Parrafo.ANOCHECER to "Esta noche baja a la calle con el LSD y con Ellen, y las caras que pasan, que no conoce, se le vuelven todas iguales, como dibujadas por la misma mano cansada. Los letreros luminosos de las tiendas nuevas zumban en un verde sucio. {nombre} busca en los postes algún cartel suyo, y lo encuentra roto, a medias. A las doce ya están en casa, en la cocina, con la puerta cerrada y un disco.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el barrio se le ha ido de entre las manos sin que se diera cuenta, como la arena. Piensa que no es culpa de nadie, o de todos. Piensa que mañana pintará en el cuarto de atrás con Ellen, y que eso sí es suyo. Se duerme pronto, antes de que se le pase el efecto, cosa que no le había ocurrido nunca.",
        ),
        "hongos_estudio" to unDia(
            Parrafo.GENTE to "Esta noche, en el estudio del cuarto de atrás, Ellen y {nombre} se comen los últimos hongos que les quedan, los de la amiga que se fue, secos del todo, casi deshechos. Ponen una manta en el suelo, apagan la luz grande y dejan encendido el retroproyector con un vidrio de tinta verde, quieta. Afuera todavía hace calor, el de septiembre.",
            Parrafo.ANOCHECER to "Esta noche los hongos son suaves y cortos. El verde de la pared se vuelve un estanque, y {nombre} siente el suelo de madera debajo de la espalda como si fuera tierra, tibia, con raíces. Le pesan los brazos y los párpados, y está contento de que le pesen. Ellen le habla de Chicago, de un lago helado de cuando era niña, y él lo ve en el verde de la pared. A medianoche se han dormido.",
            Parrafo.PENSAMIENTO to "{nombre} piensa al despertar, de madrugada, con la manta enredada y la pared ya oscura, que esta ha sido la noche más tranquila de todas, y que quizá por eso le ha gustado. Piensa que no queda ningún hongo, y que no va a buscar más. No lo piensa con pena. Vuelve a dormirse con la mano de Ellen en el pelo.",
        ),
        "el_azul" to unDia(
            Parrafo.TRABAJO to "Hoy dibuja en el estudio del cuarto de atrás el cartel de un baile de noviembre, con Ellen al otro lado, probando sus vidrios contra la pared blanca. El aprendiz viene a mirar un rato y se va. Trabajan hasta tarde. Cuando terminan, Ellen saca de su caja de frascos dos papelitos doblados que le regalaron en la sala y le pregunta si quiere.",
            Parrafo.ANOCHECER to "Esta noche lo toman en el estudio, sentados en el suelo frente a la pared, donde ella proyecta un solo color, azul, quieto, sin mover los vidrios. Con el LSD el azul se hace hondo, como un pozo, y en el fondo hay otro azul, y otro. {nombre} lo mira mucho rato. Es bonito, y lo sabe, y lo sabía antes de tomarlo. A las tres le dice a Ellen, de pasada, que cree que ya no lo va a tomar más. Ella dice que vale.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que no es una decisión, sino algo que se ha ido gastando, como una suela. No hay mucho más que pensar. Piensa en el cartel de mañana y en el azul, que quiere poner en el centro, de un tono que esta noche ha visto y que mañana, sin nada, buscará en los tarros. Lo encontrará casi.",
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
            Parrafo.ANOCHECER to ANOCHECER + MONTEREY_ANOCHECER + HUMO_VIERNES,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES_T1 + ESPECIALES_NOCHES,
    )
}
