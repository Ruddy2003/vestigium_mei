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
 * El cuento de Kichizō, carpintero de Edo hacia 1750. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor
 * aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Vive en una casa de vecindad de un callejón de Kanda: un cuarto con un fogón en la entrada de tierra, un pozo y
 *   una letrina para todo el callejón. Va a la obra, al baño público, a la casa de libros de alquiler y a las fiestas.
 * - No repite lo que ya dice «Su vida»: el baño de cada día, el millón de habitantes, los cinco gō de arroz, la
 *   enfermedad de Edo, que lee los silabarios y la peregrinación de su vida.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG2.kt`):
 * - Okiku nace en la temporada 2 (`nace_okiku`); Ume la espera desde `ume_espera` (temporada 1).
 * - Kinta va a la escuela del templo desde `terakoya` (temporada 1) y barre el taller de Genbei desde `kinta_taller` (5).
 * - Shinsuke es aprendiz desde `shinsuke` (temporada 4).
 * - Los estados: «aldea» (temporada 3, se cura en el campo), «sin_casa» (temporada 4, duermen en el templo mientras
 *   levantan la casa quemada) y «oyama» (temporada 5, la peregrinación al monte).
 * - Las fiestas que vuelven cada año no nombran a Okiku ni a Shinsuke, ni dicen que la casa esté en pie.
 */
internal object PiezasKichizo {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta con el pregón del vendedor de frijol fermentado, que pasa por el callejón antes que nadie. Se quita la colcha, la dobla, la mete en el armario de la pared y el cuarto vuelve a ser cuarto, que de noche es cama y de día es casa."),
        v("La primera luz entra por el papel de la ventana, que deja pasar el día pero no la mirada de los vecinos. {nombre} sale a la entrada de tierra, enciende el fogón con una brasa que guardó en la ceniza y pone el arroz, que en Edo se cuece una vez al día, por la mañana."),
        v("A {nombre} lo despierta el cubo del pozo, que alguien ya está subiendo: en el callejón hay un solo pozo para todas las casas y quien llega primero se lleva el agua limpia. Se echa agua fría en la cara, se ata el pañuelo a la cabeza y ya es carpintero."),
        v("Por el callejón pasan, uno detrás de otro, los vendedores de la mañana: el de las almejas, el del tofu, el de las verduras en salmuera, cada uno con su pregón cantado. Ume compra sin salir de casa, desde la puerta, y {nombre} desayuna arroz caliente, sopa y un pescadito seco."),
        v("{nombre} se levanta antes del sol y se viste de faena: los calzones ajustados, el delantal con bolsillo, la chaqueta corta con el emblema de su maestro en la espalda. Se mira en el cubo de agua por si acaso, y sale a la calle silbando, que es como salen los carpinteros de Edo."),
        v("En el altar pequeño de la pared, sobre la repisa, {nombre} deja un poco de arroz y un vaso de agua para los dioses de la casa, da dos palmadas y agacha la cabeza. Es un momento. Luego carga la caja de herramientas al hombro, que pesa lo que pesa su oficio, y sale."),
        v("Cuando abre la puerta corrediza, el callejón ya huele a humo de los fogones y a la letrina del fondo, que es de todos y nadie limpia. {nombre} saluda a la viuda Otake, que está barriendo su pedazo de tierra como si fuera un jardín, y sale hacia la calle grande."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} A un carpintero el tiempo le importa por la madera, que con humedad se hincha y con sequía se abre."),
        v("{nombre} mira el cielo por encima de los tejados del callejón, que se ven como una franja estrecha entre aleros. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En Edo se mira el tiempo por el viento más que por las nubes: el viento seco es el que trae los incendios."),
        v("{cielo} En {estacion} la ciudad cambia de ruido, de olor y de pregones, y {nombre} sabría decir el mes con los ojos cerrados, por lo que venden en la calle."),
        v("Desde el puente, de camino a la obra, se ve el monte Fuji cuando el aire está limpio, blanco y pequeño al fondo de todo. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los viejos del callejón dicen que antes los inviernos eran peores, y lo dicen todos los inviernos."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y alto, y los tejados de teja negra brillan como si estuvieran mojados.",
            "Hace un día claro, de los que dejan ver el Fuji al fondo de la ciudad.",
            "No hay una nube, y el sol pega de lleno en las paredes blancas de los almacenes.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, gris como el agua de los canales.",
            "Hay nubes bajas que no se deciden a llover.",
            "Está nublado, y la ciudad se ve aplastada bajo un techo de algodón sucio.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el callejón se vuelve un río de barro que hay que cruzar sobre tablas.",
            "Cae una lluvia fina que no moja hasta que ya se está empapado.",
            "Llueve, y por la ciudad se abren cientos de paraguas de papel aceitado.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos retumban sobre la bahía y la gente se tapa el ombligo, que es lo que dicen que se lleva el dios del trueno.",
            "El cielo se pone negro de golpe, y el primer trueno vacía la calle.",
        ),
        Clima.NIEVE to listOf(
            "Ha nevado, y los tejados de la ciudad están blancos hasta donde alcanza la vista.",
            "Caen copos grandes y lentos, y los niños del callejón salen a atraparlos con la lengua.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla del río, y los puentes se hunden en ella a la mitad.",
            "La niebla tapa los canales, y los barqueros se avisan con gritos para no chocar.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento seco del norte, el que levanta polvo y pone nerviosa a toda la ciudad.",
            "Hay viento, y en las torres de vigía los bomberos miran el horizonte más que de costumbre.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y la ciudad entera espera los cerezos como se espera una visita.",
            "En primavera llegan a Edo los barcos con madera de los bosques de Kiso, y en los almacenes de madera huele a resina.",
            "Es primavera, y los vendedores de la mañana pregonan brotes de bambú.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el calor húmedo se pega a la ropa desde la mañana.",
            "En verano se duerme bajo el mosquitero de malla verde, y se trabaja sudando como en el baño.",
            "Es pleno verano, cuando pasan por el callejón los vendedores de campanillas de viento y de grillos en jaula.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el mejor tiempo para trabajar la madera: ni se hincha ni se raja.",
            "En otoño las noches se llenan de grillos, y los vendedores pregonan castañas y caquis.",
            "Es otoño, y en los templos los arces se ponen rojos.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, el tiempo de los incendios: el viento seca la madera de toda la ciudad.",
            "En invierno se trabaja con las manos rojas de frío y se cena junto al brasero, con los pies metidos bajo una colcha.",
            "Es invierno, y por la noche pasan los vigilantes del barrio golpeando dos tablillas y gritando que cuidado con el fuego.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora cepilla una viga. El cepillo de Edo se tira hacia uno, no se empuja, y la viruta sale larga y fina como papel, enroscándose en el suelo. {nombre} pasa la mano por la madera para ver si ya está, y todavía no está.", hacer = Hacer.TRABAJO),
        v("A esta hora corta un ensamble: una cola de milano que encaja en otra viga sin un solo clavo. Mide con la escuadra, marca con la tinta, corta con la sierra y afina con el formón, y cuando las dos piezas entran a golpe de mazo, suenan como debe ser.", hacer = Hacer.TRABAJO),
        v("A esta hora está subido al armazón de una casa nueva, con las piernas abrazadas a un poste y el mazo en la mano, encajando las vigas del tejado. Desde arriba se ve el barrio entero. Abajo, el maestro Genbei grita algo que el viento se lleva.", hacer = Hacer.TRABAJO),
        v("A esta hora tira la línea de tinta: el hilo empapado sale del entintador, se tensa sobre la madera, se levanta con dos dedos y se suelta, y deja una raya negra recta como no la haría ninguna mano. Por esa raya va a pasar la sierra.", hacer = Hacer.TRABAJO),
        v("A esta hora descansan: en las obras de Edo se para a media mañana y a media tarde para el té. Los carpinteros se sientan en las vigas, fuman una pipa pequeña, y la dueña de la casa que se construye les manda pastelitos de arroz, que van incluidos en el precio.", hacer = Hacer.TRABAJO),
        v("A esta hora afila. Cada carpintero afila sus propias herramientas en una piedra mojada, despacio, y {nombre} afila el formón hasta que se afeita con él un pelo del brazo. Un hierro mal afilado, dice Genbei, es una vergüenza que se nota en la madera.", hacer = Hacer.TRABAJO),

        v("A esta hora come el arroz frío que trae desde casa en una caja de madera, con unas verduras en salmuera y un pedazo de pescado asado. Se come sentado en una viga, a la sombra del armazón, y se habla de lo que se paga en otras obras.", hacer = Hacer.COMER),
        v("A esta hora come en un puesto de fideos de la calle, de pie, sorbiendo fuerte como se debe. El vendedor lleva la cocina entera colgada de una vara al hombro, con el fuego encendido de un lado y los cuencos del otro, y se va a otra esquina en cuanto acaba.", hacer = Hacer.COMER),
        v("A esta hora come en casa, en cuclillas junto al fogón, el arroz de la mañana con té caliente echado encima y unas verduras en salmuera. Ume come después de él, aunque ella diga que no tiene hambre. Kinta come antes que los dos.", hacer = Hacer.COMER),

        v("A esta hora camina hacia la obra con la caja de herramientas al hombro, por calles que ya están llenas: vendedores, samuráis con sus dos espadas, mujeres con niños a la espalda, un palanquín con alguien dentro. En Edo se camina deprisa, porque todo el mundo llega tarde.", hacer = Hacer.CAMINO),
        v("A esta hora cruza el puente de Nihonbashi, desde donde se cuentan las distancias de todo el país. Por debajo pasan las barcas del mercado de pescado, y sobre el puente la gente se empuja. {nombre} se para un momento a mirar el agua. Siempre se para.", hacer = Hacer.CAMINO),
        v("A esta hora va de una obra a otra por las calles del barrio de los mercaderes, donde cada tienda tiene una cortina de tela con su nombre en la puerta. {nombre} lee los nombres a medida que pasa, despacio, para no perder la costumbre.", hacer = Hacer.CAMINO),

        v("A esta hora está en el baño público, con el agua caliente hasta el cuello, tan caliente que al principio duele. Se entra por una puerta baja, para que no se escape el vapor, y dentro está oscuro y todos hablan a la vez. Ahí se sabe todo lo que pasa en el barrio.", hacer = Hacer.GENTE),
        v("A esta hora está en el piso de arriba del baño, la sala de los hombres, donde se toma té y se juega al shōgi después del agua. {nombre} mira una partida con la toalla al hombro, da un consejo que nadie le pidió y se lo agradecen igual.", hacer = Hacer.GENTE),
        v("A esta hora se frota la espalda con su toalla en el baño, y luego se la frota al vecino, que se la frota a él. El agua de la tina es de todos y se entra ya lavado. Los que no se lavan antes se ganan una mirada de la que no se olvidan.", hacer = Hacer.GENTE),

        v("A esta hora lee un libro ilustrado que le alquiló el vendedor de libros, tumbado en el suelo del cuarto, con la cabeza cerca del farol. Son historias de fantasmas, de venganzas y de amores, con dibujos en cada página y el texto alrededor. Ume le pide que lea en voz alta.", hacer = Hacer.LIBRE),
        v("A esta hora se sienta en el umbral con un vecino y una botellita de sake tibio, mirando cómo se apaga el callejón. Hablan de precios, de incendios y de mujeres, en ese orden. El sake se acaba antes que la conversación.", hacer = Hacer.LIBRE),
        v("A esta hora está en la calle de los puestos de la noche, donde venden brochetas, sopa de fideos y pescado frito con harina. {nombre} come una brocheta de pie, mira a un cuentacuentos que cuenta a gritos una batalla, y le echa una moneda.", hacer = Hacer.LIBRE),

        v("A esta hora duerme en el cuarto de la casa de vecindad, en el suelo, con la colcha hasta la barbilla y la familia al lado. Las paredes son tan finas que se oye roncar al vecino, y el vecino lo oye a él. En el callejón nadie tiene secretos de noche.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y en la calle pasa el vigilante del barrio golpeando dos tablillas de madera y cantando que cuidado con el fuego. {nombre} lo oye entre sueños, se acuerda de la brasa del fogón, y se vuelve a dormir sin levantarse a mirar.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, con el cuerpo molido de subir a los andamios. Sueña con vigas, como todos los carpinteros, y en el sueño las vigas no encajan. Ume dice que habla dormido y que dice números.", hacer = Hacer.DORMIR),
    )

    /** Mientras se cura en la aldea de su hermano (estado «aldea», temporada 3). */
    private val ALDEA = listOf(
        v("{nombre} se despierta en la casa de su hermano Sakubei, en la aldea, con un gallo en la puerta y un silencio que no conocía. Aquí no hay pregones ni pozo de todos: hay arrozales hasta el horizonte y un camino de tierra. Se toca las piernas, como cada mañana, para ver si están mejor.", estado = "aldea"),
        v("En la aldea se desayuna arroz mezclado con cebada, que en Edo solo comen los pobres y que aquí come todo el mundo. {nombre} lo come sin protestar, porque dicen que eso es lo que cura. El médico del barrio le dijo que se fuera al campo, y en el campo está.", estado = "aldea"),
        v("A esta hora ayuda a su hermano en lo que puede, que es poco: arregla una cerca, endereza la puerta del granero, le hace a su sobrino un trompo con un pedazo de madera. Las piernas todavía le pesan, pero las manos no se han olvidado de nada.", estado = "aldea", hacer = Hacer.TRABAJO),
        v("A esta hora come con la familia de su hermano, sentado en el suelo de tierra apisonada: arroz con cebada, sopa de miso, verduras del huerto y frijoles. Su cuñada le sirve dos veces, porque viene de la ciudad y está flaco.", estado = "aldea", hacer = Hacer.COMER),
        v("A esta hora camina por el borde de los arrozales, despacio, hasta el santuario del bosque y de vuelta. Cada día llega un poco más lejos sin sentarse. Los campesinos lo saludan desde el agua, doblados sobre las matas, y él les devuelve el saludo con la mano.", estado = "aldea", hacer = Hacer.CAMINO),
        v("A esta hora se baña en la tina de madera de su hermano, calentada con leña, que es la única de la casa y se usa por turnos: primero el mayor, luego él, que es el invitado, y luego los demás. No es el baño de Edo, pero el agua caliente es la misma.", estado = "aldea", hacer = Hacer.GENTE),
        v("A esta hora se sienta en la puerta de la casa a ver caer la tarde sobre los arrozales, con una pipa que no le gusta y que fuma igual. Su hermano le pregunta por Edo y él le cuenta cosas, y su hermano dice que no se iría a vivir allá ni loco.", estado = "aldea", hacer = Hacer.LIBRE),
        v("A esta hora duerme en el cuarto de la aldea, con las ranas cantando en los arrozales tan fuerte que al principio no lo dejaban dormir. Ahora lo arrullan. Piensa en Ume y en los niños, solos en el callejón, y se duerme pensando.", estado = "aldea", hacer = Hacer.DORMIR),
        v("La aldea de su hermano es un puñado de casas con techo de paja entre arrozales, con un santuario en el bosque y un río. Aquí todo el mundo sabe quién es él: el hermano que se fue a la ciudad. Lo miran como se mira a un pariente que se ha vuelto rico, aunque no lo sea.", estado = "aldea"),
        v("Esta noche en la aldea no se ve ni una luz, salvo la de la casa, y las estrellas son tantas que parece que alguien las ha tirado. {nombre} se queda mirándolas un rato largo. En Edo, con el humo, no se ven así. Se acuesta temprano, como se acuesta aquí todo el mundo.", estado = "aldea"),
    )

    /** Mientras duermen en el templo y levantan la casa quemada (estado «sin_casa», temporada 4). */
    private val SIN_CASA = listOf(
        v("{nombre} se despierta en el suelo del salón del templo, entre otras familias del callejón quemado, con el olor del incienso mezclado con el del humo, que todavía tienen en la ropa. Los monjes ya están cantando. Ume dobla la única colcha que se salvó.", estado = "sin_casa"),
        v("Por la mañana los del callejón quemado hacen fila en la cocina del templo, donde los monjes reparten arroz a quien no tiene. {nombre} come su cuenco de pie y sale hacia lo que queda de su casa, que es un rectángulo de ceniza con el pozo en medio.", estado = "sin_casa"),
        v("A esta hora duerme en el templo, en el suelo de tablas del salón, con la familia apretada a su lado y otras diez familias alrededor. Un niño llora en algún rincón. Un monje pasa con una lámpara, mirando que no haya brasas encendidas.", estado = "sin_casa", hacer = Hacer.DORMIR),
        v("A esta hora come con los vecinos del callejón quemado en el patio del templo, todos juntos, de lo que trajo cada uno y de lo que dieron los monjes. Se come poco y se habla mucho de lo que se perdió. {nombre} perdió sus libros alquilados, que habrá que pagar.", estado = "sin_casa", hacer = Hacer.COMER),
        v("A esta hora no lee ni bebe: vuelve al solar quemado a mirar cómo va la casa nueva, aunque ya la haya mirado de día. Mide de memoria lo que falta. Los vecinos que no saben de carpintería le preguntan cuándo, y él les dice un número de días que siempre es menos que la verdad.", estado = "sin_casa", hacer = Hacer.LIBRE),
        v("Donde estaba el callejón quedan las piedras de los fogones, los pozos y un olor que no se va. Los dueños de las casas de vecindad ya mandaron la madera, porque en Edo se reconstruye más rápido de lo que se quema, y se quema muy rápido. {entorno}", estado = "sin_casa"),
        v("Esta noche, en el templo, {nombre} se sienta en el borde del corredor de madera con otros hombres del callejón, sin sake, mirando la luna sobre los tejados que no se quemaron. Nadie dice nada durante un rato. Luego alguien cuenta un chiste, y se ríen más de lo que vale.", estado = "sin_casa"),
    )

    /** La peregrinación al monte Ōyama con los carpinteros del barrio (estado «oyama», temporada 5). */
    private val OYAMA = listOf(
        v("{nombre} se despierta en una posada del camino, en un cuarto grande lleno de carpinteros del barrio que roncan como un coro. Hoy se sube al monte. Antes del sol se lavan en el río, como manda el rito, y se ponen la ropa blanca de los peregrinos.", estado = "oyama"),
        v("A esta hora sube los escalones de piedra del monte Ōyama, que son tantos que nadie los ha contado sin equivocarse. El grupo lleva en hombros un sable de madera enorme para el santuario, y se turnan para cargarlo. {nombre} carga su turno y uno más, por las piernas que se le curaron.", estado = "oyama", hacer = Hacer.TRABAJO),
        v("A esta hora comen en una casa de té del camino, sentados en bancos al aire libre, arroz con verduras de la montaña y tofu. Los carpinteros piden sake y los de la casa de té ya lo tienen listo, porque saben quiénes son los peregrinos de Edo.", estado = "oyama", hacer = Hacer.COMER),
        v("A esta hora camina por el camino del monte con el grupo, en fila, haciendo sonar las campanillas que llevan colgadas. Los campesinos de los pueblos los ven pasar y saben que son de Edo por cómo hablan: alto, deprisa y de todo.", estado = "oyama", hacer = Hacer.CAMINO),
        v("A esta hora se meten bajo la cascada que hay al pie del monte, con la ropa blanca pegada al cuerpo, para purificarse antes de subir. El agua es tan fría que corta la respiración. {nombre} aguanta lo que aguantan los demás y sale riéndose, como los demás.", estado = "oyama", hacer = Hacer.GENTE),
        v("A esta hora está en la posada con los del grupo, bebiendo y cantando las canciones de la cofradía de los carpinteros, que solo se cantan en el camino. Mañana hay que madrugar, y nadie se acuesta.", estado = "oyama", hacer = Hacer.LIBRE),
        v("A esta hora duerme en el suelo de la posada, envuelto en una colcha que huele a otros peregrinos. Le duelen las piernas, pero es un dolor de haber subido, que es otra cosa. Sueña que el sable de madera pesa más de lo que pesa.", estado = "oyama", hacer = Hacer.DORMIR),
        v("Desde lo alto del monte Ōyama se ve la llanura entera, con sus aldeas, sus ríos y, muy lejos, una mancha de humo que es Edo. {nombre} la busca con los ojos y la encuentra. Desde aquí cabe en una mano.", estado = "oyama"),
        v("Esta noche, en la posada del camino, los carpinteros se cuentan otra vez la subida, y ya es más alta y más difícil que esta mañana. {nombre} se ríe y añade un escalón. Para cuando vuelvan a Edo, el monte va a ser el Fuji.", estado = "oyama"),
    )

    private val PUEBLO = listOf(
        v("El callejón de {nombre} es una calle estrecha que sale de la calle grande por debajo de un portón, con dos hileras de casas de madera pegadas. Al fondo están el pozo, la letrina y el basurero, y en la esquina, un santuario del zorro del tamaño de una caja. {entorno}"),
        v("{entorno} Kanda es un barrio de artesanos: carpinteros, tintoreros, herreros y gente que hace cosas con las manos. En las calles grandes viven los mercaderes, en las casas buenas; en los callejones de atrás, los que trabajan para ellos."),
        v("En Edo la mitad de la ciudad es de los samuráis, con sus mansiones de muros blancos, y la gente común vive apretada en lo que queda. Dicen que muchos samuráis pobres hacen paraguas en casa para comer. {entorno}"),
        v("Cada barrio tiene su torre de vigía, una escalera alta con una campana arriba, y cuando hay fuego la campana suena y la ciudad entera se asoma. {nombre} conoce el toque de cada distancia: lejos, cerca, aquí. {entorno}"),
        v("{entorno} El dueño de las casas de vecindad no vive aquí: vive aquí su encargado, que cobra la renta, arregla las peleas y vende lo de la letrina a los campesinos como abono, que es su mejor negocio."),
        v("Los canales de Edo están llenos de barcas que traen todo lo que la ciudad come y se llevan todo lo que tira. En la orilla de cada canal hay almacenes de paredes blancas y gruesas, que no se queman. {entorno}"),
    )

    /** Lo que se ve en el callejón según cómo está la casa de vecindad (arco «casa»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("casa", 0.999, frases = listOf(
            "Las puertas corredizas del callejón están abiertas, y de cada casa sale el humo de un fogón.",
            "En el callejón cuelga la ropa lavada de casa a casa, y hay que agacharse para pasar.",
            "Las casas de vecindad son viejas y se inclinan un poco unas sobre otras, como vecinos que se cuentan algo.",
        )),
        Entorno("casa", 0.2, 0.999, frases = listOf(
            "En el solar del callejón ya se levantan los armazones de madera nueva, que huele a bosque entre la ceniza.",
            "La casa de vecindad nueva tiene ya postes y vigas, y le falta lo demás.",
        )),
        Entorno("casa", 0.0, 0.2, frases = listOf(
            "Donde estaba el callejón hay un rectángulo negro de ceniza, con los pozos y las piedras de los fogones en pie.",
            "Del callejón quemado solo queda en pie el santuario pequeño del zorro, que era de piedra.",
        )),
    )

    private val GENTE = listOf(
        v("Ume, su mujer, cose kimonos de encargo para una tienda de telas, sentada junto a la ventana con la aguja entre los dientes. Sabe lo que debe cada vecino. {nombre} sabe lo que le debe él, que es más que nadie."),
        v("Kinta tiene la edad en que todo lo que hace su padre le parece fácil hasta que lo intenta. Clava clavos torcidos en una tabla, y {nombre} se los endereza sin decirle nada, para que crea que le salieron bien."),
        v("La viuda Otake, la de la casa de al lado, es la más vieja del callejón y la que manda en él, aunque mande el encargado. Da consejos que nadie le pide. A {nombre} lo llama «el carpintero», como si no hubiera otros."),
        v("El maestro Genbei es quien le da las obras, y {nombre} trabaja para él desde que era un muchacho que barría virutas. Genbei grita mucho, paga a tiempo y dice que la madera se acuerda de todo."),
        v("Heiji, su amigo, es bombero de la brigada del barrio: en los incendios sube a los tejados con el estandarte de la brigada. El resto del tiempo es carpintero, y presume de los dos oficios."),
        v("El que alquila libros pasa por el callejón cada pocos días con una torre de libros atada a la espalda, más alta que él. Sabe lo que le gusta a cada casa: a {nombre}, los de fantasmas; a Ume, los de amores."),
        v("Kinta va cada mañana a la escuela del templo con su caja de pinceles y su cuaderno, y vuelve con las manos negras de tinta. Copia los caracteres hasta que la hoja se pone negra. {nombre} lo mira y piensa que él no tuvo eso.", desde = "terakoya", hasta = "kinta_taller"),
        v("Kinta ya barre por las tardes el taller del maestro Genbei, como barrió su padre a su edad,, y vuelve con virutas en el pelo y cara de importancia. {nombre} le pregunta qué aprendió, y el chico dice que nada, y luego se lo cuenta todo.", desde = "kinta_taller"),
        v("Ume espera otro hijo, y las vecinas le dan consejos que se contradicen y un amuleto del templo. {nombre} le sube el agua del pozo y le prohíbe cargar cosas, y ella le obedece cuando él está y no cuando no está.", desde = "ume_espera", hasta = "nace_okiku"),
        v("Okiku, la pequeña, gatea por el cuarto detrás de las virutas que trae su padre en la ropa, y se las mete en la boca si nadie la mira. {nombre} le hace juguetes con los restos de la madera: un pájaro, una barca, un perro.", desde = "nace_okiku"),
        v("Shinsuke, el aprendiz, duerme en la casa del maestro y come con ellos los días de obra. Tiene doce años y una paciencia que no es de su edad. {nombre} le enseña como le enseñaron a él: mirando.", desde = "shinsuke"),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas del oficio: con las manos antes que con la cabeza."),
        v("Hay cosas que solo se miran cuando faltan. {dato_objeto}"),
        v("{dato_objeto} En el callejón se dice que el carpintero siempre tiene la puerta mal cerrada, y lo dicen con razón."),
        v("{dato_objeto} Son cosas que se aprenden mirando al maestro, como casi todo en el oficio."),
        v("Un carpintero de Edo tiene pocas cosas, y casi todas caben en una caja que se carga al hombro. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("cepillo", "el cepillo", "un cepillo", "El cepillo",
            "El cepillo de carpintero es un taco de roble con un hierro metido en diagonal, y en Edo se trabaja tirando hacia uno; las virutas buenas dejan pasar la luz."),
        ObjetoCuento("sierra", "la sierra", "una sierra", "La sierra",
            "La sierra de Edo corta al tirar, no al empujar, y por eso puede ser fina como una hoja; hay una para cortar a lo largo de la veta y otra para cortar de través."),
        ObjetoCuento("formon", "el formón", "un formón", "El formón",
            "El formón es un hierro con filo en la punta y un mango que se golpea con el mazo; con él se abren las cajas de los ensambles, y en la caja hay una docena, de todos los anchos."),
        ObjetoCuento("entintador", "el entintador", "un entintador", "El entintador",
            "El entintador es una cajita de madera con un pozo de algodón empapado en tinta y un carrete de hilo; el hilo sale entintado y deja en la madera una raya recta de un extremo al otro; muchos tienen forma de grulla."),
        ObjetoCuento("escuadra", "la escuadra", "una escuadra", "La escuadra",
            "La escuadra de carpintero es de metal y tiene marcas en las dos caras: en una se mide, y en la otra hay una escala que da sin cuentas la diagonal de un cuadrado."),
        ObjetoCuento("farol", "el farol", "un farol", "El farol",
            "El farol de la casa es un cilindro de papel sobre un armazón de bambú, que se pliega como un acordeón; da poca luz, se apaga con el viento y se lleva en la mano para no caer al canal."),
        ObjetoCuento("brasero", "el brasero", "un brasero", "El brasero",
            "El brasero es un cuenco grande de cerámica lleno de ceniza, con unas brasas de carbón en medio; calienta las manos y no el cuarto, y en invierno la familia entera se sienta alrededor."),
        ObjetoCuento("sake", "la botellita de sake", "una botellita de sake", "El sake",
            "El sake de la gente común se compra suelto en la tienda de la esquina, del barril a la botellita que uno lleva; se bebe tibio, y nunca se sirve uno mismo."),
        ObjetoCuento("libro", "el libro de alquiler", "un libro de alquiler", "El libro",
            "Los libros que se alquilan en Edo están impresos con planchas de madera talladas, dibujo y letra juntos; pasan de casa en casa hasta deshacerse, con la huella de todos los dedos del barrio."),
        ObjetoCuento("toalla", "la toalla", "una toalla", "La toalla",
            "La toalla de algodón sirve para todo: para el baño, para el sudor, para atarse a la cabeza en la obra; las tiendas regalan las suyas con su nombre impreso, y la ciudad lleva anuncios en la frente."),
        ObjetoCuento("geta", "las sandalias de madera", "unas sandalias de madera", "Las geta",
            "Las geta son sandalias de madera con dos dientes debajo, que levantan el pie del barro; en el callejón se sabe quién llega por cómo suenan."),
        ObjetoCuento("abanico", "el abanico", "un abanico", "El abanico",
            "El abanico redondo de verano es de papel pegado sobre varillas de bambú y no se pliega; sirve para darse aire, para avivar el fuego y para espantar mosquitos."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en Edo como se piensa en el tiempo: no se la elige, se vive en ella. Es una ciudad que se quema y se levanta, y él se gana la vida en la segunda parte. A sus {edad} años, no se iría a ningún otro sitio."),
        v("Piensa a veces que las casas que hace se van a quemar, como se quema todo en Edo antes o después, y que alguien como él las volverá a hacer. No le da tristeza. Le parece que así es el oficio: hacer cosas que duran lo que duran, y hacerlas bien igual."),
        v("{mundo} Él piensa en cosas más cercanas: en el precio del arroz, en la madera que subió, en si Genbei le dará la obra del mes que viene. Las cosas grandes son de los samuráis, que tienen tiempo y espadas para pensarlas."),
        v("{mundo} A sus {edad} años, {nombre} ha visto arder cuatro veces su barrio, y las cuatro veces lo ha visto levantarse otra vez, más deprisa. Dicen que los incendios y las peleas son las flores de Edo. Él preferiría otras flores."),
        v("No tiene tierra ni la quiere: su hermano se quedó con la de su padre, y él se vino a la ciudad con una caja de herramientas. Su hermano tiene arroz y él tiene oficio. Los dos creen que salió ganando el otro."),
        v("{mundo} A veces, de camino a la obra, piensa en los dioses, que en Edo son tantos que hay uno en cada esquina. {nombre} les da una moneda pequeña a los que le pillan de paso, y espera que se la repartan."),
    )

    private val MUNDOS = mapOf(
        "paz" to listOf(
            "En el baño se habla de los precios, que suben, y del shōgun, que no se ve.",
            "Este año no se habla de guerras, que no las hay desde hace siglo y medio, sino de teatro y de incendios.",
            "Se dice que el antiguo shōgun, el que plantó los cerezos de Asukayama, está viejo y enfermo.",
        ),
        "luto" to listOf(
            "Se habla del antiguo shōgun, que fue bueno para la gente común, según dicen los que no lo conocieron.",
            "Se dice que la era ha cambiado de nombre, y la gente se equivoca al decir el año.",
        ),
        "kakke" to listOf(
            "En el baño se habla de la enfermedad de las piernas, que este verano tiene a medio barrio caminando como viejos.",
            "Se dice que la embajada de Ryūkyū viene a la ciudad, con su música y sus ropas de colores.",
        ),
        "fuego" to listOf(
            "Se habla de los incendios, que en Edo vienen como las estaciones, y de quién los empieza, que nadie sabe y todos dicen.",
            "En el barrio se dice que este año el viento ha sido malo, y que los incendios lo saben.",
        ),
        "calendario" to listOf(
            "Se habla del calendario, que el emperador quiere corregir, y la gente se pregunta si eso cambiará los días de fiesta.",
            "Se dice que los astrónomos de Kioto han medido el cielo otra vez, y que el cielo no estaba donde decían.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando oscurece, {nombre} vuelve al callejón con la caja de herramientas al hombro y la toalla del baño mojada en la mano. Ume ha encendido el farol de papel. Se cena arroz con té y unas verduras, sentados en el suelo, y la cena dura lo que dura el arroz."),
        v("Al caer la tarde se encienden los faroles en las puertas de las tiendas de la calle grande, cada uno con el nombre de la tienda pintado, y el callejón se queda a oscuras, salvo por las rendijas de las casas. {nombre} cierra la puerta corrediza y el día se acaba."),
        v("Anochece sobre Edo, y en las torres de vigía los bomberos de guardia miran la ciudad en todas direcciones, buscando un resplandor. {nombre} los ve desde el callejón, recortados contra el cielo, y piensa que de noche esa es la gente que trabaja por todos."),
        v("De noche, en el callejón, se oye todo: la tos del vecino, la discusión de la casa del fondo, un niño que no se quiere dormir, un gato. {nombre} se tumba en la colcha y escucha la ciudad respirar a través de las paredes de papel, y se duerme sin darse cuenta."),
        v("Se cena junto al fogón, con las puertas abiertas al callejón para que salga el humo. Kinta cuenta lo que hizo y Ume cuenta lo que dijeron las vecinas, y casi siempre es más interesante lo de las vecinas. Luego se apaga el fuego con cuidado, como se apaga en Edo."),
        v("La última luz se queda un momento en los tejados de los almacenes, de teja negra, cuando la calle ya está a oscuras. Pasan los vigilantes con sus tablillas, clac, clac, cantando que cuidado con el fuego. {nombre} comprueba la brasa del fogón dos veces antes de dormir."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "susuharai" to unDia(
            Parrafo.MANANA to "Hoy es el día de quitar el hollín de todo el año, y en todo Edo se limpia a la vez. {nombre} saca al callejón lo poco que tienen, se ata una toalla a la cabeza y barre las vigas del techo con una caña de bambú con hojas en la punta, que es la escoba de este día.",
            Parrafo.PUEBLO to "En el callejón cada casa saca sus cosas a la puerta, las colchas se tienden al sol y el hollín cae como nieve negra. En las tiendas grandes de los mercaderes, cuando acaban, los empleados mantean al dueño en el aire, tres veces, y el dueño se deja, porque trae suerte.",
            Parrafo.ANOCHECER to "Esta noche se come algo caliente con los vecinos, en el callejón, cansados y tiznados hasta las orejas, y la viuda Otake reparte sake dulce. La casa huele a limpio por primera vez en un año. {nombre} piensa que ya solo falta el año nuevo, y el dinero para pagarlo.",
        ),
        "setsubun" to unDia(
            Parrafo.MANANA to "Hoy es la víspera de la primavera, y en Edo se echan los demonios. En la puerta de cada casa cuelgan una rama de acebo con una cabeza de sardina clavada, porque a los demonios les pinchan las hojas y les apesta el pescado. En la del callejón cuelgan tres.",
            Parrafo.GENTE to "Al caer la tarde {nombre} tuesta frijoles en una sartén y los tira por la puerta gritando que fuera los demonios y que entre la suerte. Kinta grita más que él. Luego cada uno se come tantos frijoles como años tiene, y Kinta cuenta los suyos dos veces.",
            Parrafo.ANOCHECER to "Esta noche se oyen gritos de demonios por todo el barrio, de casa en casa, y risas. En los templos grandes los luchadores y los actores tiran frijoles a la gente desde un estrado. {nombre} barre los frijoles de la entrada antes de dormir, menos uno, por si acaso.",
        ),
        "omisoka" to unDia(
            Parrafo.MANANA to "Hoy es el último día del año, y en Edo es el día de pagar: todas las deudas del año se cobran hoy. Los cobradores de las tiendas recorren las calles con sus libros desde la mañana, y los deudores se esconden en el baño, que es donde menos se les busca.",
            Parrafo.TRABAJO to "A esta hora {nombre} va a casa del maestro Genbei a cobrar lo que le debe del último mes, y luego a la tienda de arroz, a la de sake y al que alquila libros, a pagar lo que debe él. Entre una cosa y otra le queda en la mano casi lo mismo que tenía.",
            Parrafo.ANOCHECER to "Esta noche se comen fideos de alforfón, largos, para que la vida también lo sea, y a medianoche las campanas de los templos dan ciento ocho golpes, uno por cada deseo que estorba. {nombre} los cuenta hasta treinta y se duerme. Los demás los cuenta Ume.",
        ),
        "shogatsu" to TextoEspecial(
            listOf(
                dia(
                    Parrafo.MANANA to "Hoy es año nuevo, y todo Edo cumple un año más a la vez: aquí la edad se cuenta así. {nombre} se levanta antes del sol para ver la primera salida del año desde la orilla, con medio barrio. En la puerta de la casa hay ya un pino y tres cañas de bambú, atados con una cuerda de paja.",
                    Parrafo.PUEBLO to "Hoy las tiendas están cerradas y las calles están llenas de gente con ropa nueva, que va a visitar a los suyos y a su maestro. Los niños juegan con cometas y las niñas con raquetas de madera y un volante de plumas. Nadie trabaja. Nadie barre: barrer hoy es echar la suerte.",
                    Parrafo.GENTE to "{nombre} va con Ume y Kinta a saludar al maestro Genbei, con un regalo envuelto en papel. Genbei les sirve sake con especias, que se bebe en año nuevo para tener salud, y les dice que este año habrá obra. Lo dice todos los años, y casi siempre acierta.",
                ),
                dia(
                    Parrafo.MANANA to "Hoy es el segundo día del año, y es el de escribir por primera vez. Kinta escribe con el pincel, en una hoja larga, un deseo para el año, con letra grande y temblorosa. {nombre} lo pega en la pared, aunque no sepa leer del todo lo que dice.",
                    Parrafo.TRABAJO to "A esta hora {nombre} hace el primer trabajo del año, que en Edo se hace hoy aunque sea poca cosa: cepilla una tabla, una sola, para que las manos no se olviden. La viruta sale bien. Es buena señal.",
                    Parrafo.ANOCHECER to "Esta noche se pone debajo de la almohada un dibujo del barco de los siete dioses de la suerte, que venden por las calles, para tener un buen primer sueño del año. Lo mejor es soñar con el Fuji, luego con un halcón y luego con una berenjena. {nombre} sueña con una viga.",
                ),
                dia(
                    Parrafo.MANANA to "Hoy es el tercer día del año, el último de fiesta. Se come lo que queda de las cajas de comida de año nuevo, que ya no sabe tan bien, y los pasteles de arroz, que se han puesto duros y se tuestan en el brasero hasta que se inflan.",
                    Parrafo.PUEBLO to "Por las calles pasan los músicos y bailarines de año nuevo, que van de casa en casa con un león de tela y madera. El león le muerde la cabeza a los niños, para que crezcan sanos, y los niños lloran, y las madres pagan.",
                    Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza, que tendrá incendios, obras, deudas y fiestas, como todos. No pide mucho: que no se queme su casa, que haya trabajo y que Kinta aprenda. Si se cumplen dos de las tres, será un buen año.",
                ),
            ),
        ),
        "taishi" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de los carpinteros, el día del príncipe Shōtoku, que hace mil años mandó construir los primeros templos y es el patrón del oficio. Hoy nadie toca la sierra: las herramientas se limpian, se ponen en fila y se les deja un poco de sake delante.",
            Parrafo.PUEBLO to "Los carpinteros del barrio se juntan en casa del maestro más viejo, donde cuelga un rollo pintado con la figura del príncipe. Se reza, se come y se bebe mucho, y los maestros discuten quién hizo la mejor obra del año. Siempre la hizo el que habla.",
            Parrafo.ANOCHECER to "Esta noche {nombre} vuelve tarde y cantando del banquete, con Heiji del brazo, por la calle a oscuras. Ume lo oye venir desde la esquina y le abre la puerta antes de que llame. No dice nada; le da agua y le quita las sandalias.",
        ),
        "hanami" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy están en flor los cerezos de Ueno, y media ciudad va a verlos. Ume preparó desde anoche una caja de comida con bolas de arroz, huevo dulce y pescado, y {nombre} carga una botella de sake. Kinta va delante, corriendo, como si los cerezos se fueran a ir.",
                Parrafo.PUEBLO to "En Ueno, por el templo, no se puede cantar ni hacer ruido, así que la gente mira los árboles y come callada. Los que quieren fiesta de verdad van a Asukayama, donde el antiguo shōgun mandó plantar cerezos para el pueblo, y allí se canta, se baila y se bebe.",
                Parrafo.PENSAMIENTO to "{nombre} se tumba bajo un cerezo y mira caer los pétalos, que caen todos a la vez como nieve rosa, y piensa que lo bonito de la flor es que dura poco. Luego piensa que lo mismo se dice de las casas de Edo, y deja de pensar, y se sirve otro sake.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días hay cerezos en flor en todos los templos y en las orillas del río, y la gente come debajo de ellos sobre esteras, aunque haga frío. Los vendedores de dulces de tres colores hacen el negocio del año.",
                "Mientras duran los cerezos, en el barrio no se habla de otra cosa: dónde están más bonitos, dónde hay menos gente, dónde se puede cantar. Luego llueve una noche y se acaba, y se habla de otra cosa.",
                "Estos días los pétalos caen en los canales y los cubren de rosa, y las barcas los parten al pasar. Los niños los juntan en montones y se los tiran unos a otros.",
            )),
        ),
        "hina" to unDia(
            Parrafo.MANANA to "Hoy es el día de las niñas. En las casas donde hay hijas se ponen las muñecas en escalones cubiertos de tela roja: el emperador y la emperatriz arriba, las damas, los músicos y los ministros abajo. Las de las tiendas ricas tienen siete escalones; las del callejón, uno.",
            Parrafo.PUEBLO to "En el barrio, las niñas van de casa en casa a ver las muñecas de las otras, y comen pasteles de arroz de colores y bolitas dulces. Se bebe sake blanco, dulce y espeso, que las niñas toman también, un poco, y se les pone la cara roja.",
            Parrafo.ANOCHECER to "Esta noche, en las casas con hijas, se guardan las muñecas en sus cajas, deprisa, porque se dice que si se quedan fuera una noche más, las hijas se casan tarde. Ume dice que eso lo inventaron las madres con prisa. {nombre} dice que da igual, y que mejor guardarlas.",
        ),
        "tango" to unDia(
            Parrafo.MANANA to "Hoy es el día de los niños varones. En las puertas de las casas se ponen hojas de lirio, que espantan los males, y en las casas de los mercaderes se cuelgan de una vara carpas de tela pintada, que se llenan de viento y parecen nadar contra la corriente.",
            Parrafo.GENTE to "Kinta se pone en la cabeza un casco de samurái de papel que le hizo su madre, y se pasea por el callejón con una espada de madera que le hizo su padre, golpeando a los demás niños. {nombre} le compra un pastel de arroz envuelto en hoja de roble, que se come hoy.",
            Parrafo.ANOCHECER to "Esta noche, en el baño público, el agua tiene hojas de lirio flotando, que se meten hoy en las tinas para la salud. Huele a hierba verde. {nombre} se queda en el agua más tiempo que nunca, por si de verdad sirve.",
        ),
        "ryogoku" to unDia(
            Parrafo.MANANA to "Hoy se abre el río en Ryōgoku: empieza el tiempo del fresco en el agua. Desde hoy y durante todo el verano, por las noches, el río se llena de barcas y en las orillas se ponen puestos de comida, y esta noche hay fuegos artificiales sobre el agua.",
            Parrafo.PUEBLO to "Al atardecer el puente de Ryōgoku está tan lleno de gente que cruje, y en el río las barcas van tan juntas que se podría cruzar a pie de una a otra. Los ricos alquilan barcas con farolillos y músicos; los demás miran desde el puente, que es gratis.",
            Parrafo.ANOCHECER to "Esta noche, cuando estalla el primer fuego sobre el río, la gente grita el nombre de la casa que los hace, como en el teatro se grita el nombre del actor. {nombre} grita también, con Kinta subido a los hombros. El cielo se abre y se cierra, y huele a pólvora.",
        ),
        "sanno" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de Sannō, una de las dos grandes de Edo, la que entra al castillo. Los barrios sacan sus carros adornados, altísimos, con muñecos encima, y los arrastran en procesión hasta dentro de los muros, donde los mira el shōgun. Este año le toca a Sannō; el que viene, a Kanda.",
            Parrafo.PUEBLO to "Por la calle pasan los carros uno tras otro, cada uno de un barrio, tirados por bueyes y por hombres que cantan, con músicos de flauta y tambor subidos en ellos. Detrás van los palanquines del santuario, que se mueven como si los dioses dentro estuvieran borrachos.",
            Parrafo.PENSAMIENTO to "{nombre} mira pasar los carros pensando en cómo están hechos, que es lo que piensa un carpintero de cualquier cosa. Los ensambles de uno no le gustan. Luego piensa que el shōgun los va a ver, y que no sabrá distinguir uno bueno de uno malo, y eso le consuela.",
        ),
        "tanabata" to unDia(
            Parrafo.MANANA to "Hoy es Tanabata, la noche en que la tejedora y el pastor, que son dos estrellas separadas por el río del cielo, se encuentran una vez al año. En los tejados de Edo se atan cañas de bambú con tiras de papel de colores, y en cada tira se escribe un deseo.",
            Parrafo.PUEBLO to "Hoy también se limpia el pozo del callejón, que se hace una vez al año y en este día. Los hombres sacan el agua a cubos hasta ver el fondo, bajan a limpiarlo y luego le dejan sal y sake en el borde. {nombre} baja, porque es carpintero y sabe de cuerdas y de poleas.",
            Parrafo.GENTE to "Kinta escribe su deseo en una tira de papel azul: que le salga bien la letra. Ume escribe el suyo y no lo enseña. {nombre} escribe el suyo con su letra de carpintero, grande y torcida, y lo ata arriba del todo, donde se ve desde el cielo.",
        ),
        "bon" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza el Bon, los días en que vuelven los muertos. En la puerta de cada casa se enciende un fuego pequeño de tallos secos para que los muertos de la familia encuentren el camino. En la repisa de los dioses hay fruta, flores y una vaca y un caballo hechos de berenjena y pepino.",
                Parrafo.PUEBLO to "Estos días las calles se llenan de faroles de papel, blancos y de colores, colgados de los aleros, y de gente que va a los templos a limpiar las tumbas. Los aprendices y los criados tienen un día libre para ir a ver a sus padres, y la ciudad entera se mueve.",
                Parrafo.PENSAMIENTO to "{nombre} enciende el fuego de la puerta pensando en su padre y en su madre, que están en la aldea, enterrados junto al arrozal, y que tendrán que caminar mucho para llegar hasta Edo. Les deja la puerta abierta y un poco de arroz, por si llegan con hambre.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "Esta noche se baila en el callejón, en círculo, al ritmo de un tambor, hombres y mujeres, viejos y niños, todos con el mismo paso lento. {nombre} baila mal, como todos los años, y nadie se fija.",
                "Esta noche hay faroles encendidos en todas las casas, y el callejón brilla como no brilla en todo el año. Los muertos, si vienen, no se van a perder.",
                "Esta noche es la última del Bon, y se despide a los muertos con otro fuego pequeño en la puerta y faroles que se sueltan en el río, que se los lleva brillando hacia el mar. {nombre} los mira irse desde el puente, hasta que no se ven.",
            )),
        ),
        "tsukimi" to unDia(
            Parrafo.MANANA to "Hoy es la noche de la luna de otoño, la más bonita del año, dicen. Ume prepara bolitas de arroz blancas, quince, apiladas en una torre sobre un plato, y {nombre} trae del río unas espigas de hierba de pampa para ponerlas en un jarro, junto a la ventana.",
            Parrafo.PUEBLO to "En la orilla del río y en las colinas de la ciudad se juntan los que quieren ver salir la luna, con sake y poemas. Los ricos alquilan barcas o casas de té con vista. En el callejón, que no tiene vista, se sube al tejado del que lo tiene más firme.",
            Parrafo.ANOCHECER to "Esta noche la luna sale enorme y naranja sobre los tejados, y luego se pone blanca. {nombre} la mira sentado en el tejado de la viuda Otake, que es el más firme porque se lo arregló él. Kinta dice que ve un conejo moliendo arroz. {nombre} también lo ve.",
        ),
        "almacen" to unDia(
            Parrafo.MANANA to "Hoy el maestro Genbei le dio una obra grande: el armazón de un almacén de paredes de barro para un mercader de arroz de la orilla del canal. Es de los que no se queman, con muros de un palmo y puertas de hierro, y la madera de dentro tiene que ser la mejor.",
            Parrafo.TRABAJO to "A esta hora mide la madera del almacén, vigas de ciprés que llegaron por el río desde los bosques del interior y que huelen a templo. {nombre} las marca con tinta una por una. Un error aquí no se arregla con barro: se ve durante cien años.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que este almacén va a sobrevivir a todas las casas que ha hecho, que se quemarán antes o después. Le gusta la idea. Piensa también que va a guardar el arroz de otros, y que él compra el suyo por puñados, y eso le gusta menos.",
        ),
        "terakoya" to unDia(
            Parrafo.MANANA to "Hoy Kinta empieza a ir a la escuela del templo. {nombre} le compró una caja de pinceles, un tintero de piedra y papel, y le pagó al maestro el primer mes con una moneda y un paquete de dulces, como se hace. El chico va delante, muy serio, con la caja en los brazos.",
            Parrafo.GENTE to "La escuela es un cuarto grande con esteras, donde veinte niños escriben sentados en el suelo frente a mesitas bajas, cada uno a su ritmo, y el maestro pasa corrigiendo. Hay niños y niñas, hijos de tenderos, de artesanos y de nadie. Kinta se sienta al fondo.",
            Parrafo.ANOCHECER to "Esta noche Kinta le enseña a la familia lo que escribió, en una hoja que ya está casi negra de tanto escribir encima. {nombre} distingue dos letras de su nombre. Ume dice que el chico será escribano en una tienda grande, y Kinta dice que no, que carpintero.",
        ),
        "techo_otake" to unDia(
            Parrafo.MANANA to "Hoy, antes de que lleguen las lluvias de las ciruelas, {nombre} sube al tejado de la viuda Otake a arreglarle la gotera, que le cae justo encima de la colcha desde el otoño pasado. La vieja lo mira desde abajo, dándole instrucciones que él no sigue.",
            Parrafo.TRABAJO to "A esta hora está en el tejado de la viuda cambiando unas tablas podridas y poniendo encima corteza de ciprés nueva, sujeta con varas de bambú. Es poca cosa para un carpintero, y la hace como si fuera un templo, porque la vieja está mirando.",
            Parrafo.GENTE to "La viuda Otake no le quiere pagar con dinero, porque dice que entre vecinos no se paga, y le paga con una olla de frijoles dulces que dura tres días. {nombre} dice que es demasiado. Ella dice que es poco para un techo que ya no llora.",
        ),
        "ume_espera" to unDia(
            Parrafo.MANANA to "Hoy, antes de que él saliera hacia la obra, Ume le dijo que espera otro hijo. Se lo dijo en la entrada de tierra, mientras le daba la caja de la comida, como quien dice que va a llover. {nombre} se quedó con la caja en la mano y la puerta abierta.",
            Parrafo.GENTE to "Al volver de la obra, {nombre} encuentra a la viuda Otake en su casa, que ya lo sabe, como lo sabe el callejón entero. Le da consejos a Ume sobre qué comer y qué no, y a él sobre qué no hacer. Kinta pregunta si será un hermano, y nadie se lo sabe decir.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en otra boca, otro kimono, otro puñado de arroz. Hace cuentas en el camino de vuelta y las cuentas no le salen. Luego piensa en una niña, sin saber por qué una niña, y las cuentas le dan igual.",
        ),
        "libro_ronin" to unDia(
            Parrafo.MANANA to "Hoy pasó el que alquila libros y le dejó a {nombre}, por dos días, la historia de los cuarenta y siete samuráis sin amo que vengaron a su señor, la que todo Edo va a ver al teatro desde hace unos años. Viene en tres tomos, con dibujos en cada página.",
            Parrafo.PUEBLO to "En el callejón todos conocen la historia, aunque pasó hace casi cincuenta años: los samuráis esperaron dos años, fingiendo que se habían olvidado, y una noche de nieve entraron en la casa del culpable. Después se mataron todos, como mandaba la ley. Su tumba está en un templo de la ciudad.",
            Parrafo.ANOCHECER to "Esta noche {nombre} lee en voz alta a la luz del farol, despacio, deletreando, con Ume, Kinta y dos vecinos sentados alrededor. Cuando llega a la noche de nieve, nadie respira. Cuando acaban, la viuda Otake, que escuchaba detrás de la pared, dice que ella ya lo sabía.",
        ),
        "incendio_canal" to unDia(
            Parrafo.MANANA to "Hoy, antes del amanecer, sonó la campana de la torre de vigía con el toque de cerca: ardía media calle al otro lado del canal, con el viento seco del norte empujando las llamas. {nombre} salió con la caja de herramientas, porque en Edo los incendios se apagan tirando casas.",
            Parrafo.PUEBLO to "La brigada de bomberos del barrio llegó con sus ganchos y sus escaleras, y Heiji subió al tejado con el estandarte, que marca dónde se va a parar el fuego. Los carpinteros tiraron las casas de al lado a golpes, para que el fuego no tuviera por dónde seguir. El canal hizo el resto.",
            Parrafo.GENTE to "Los que se quedaron sin casa durmieron en el templo, con lo que pudieron sacar envuelto en una colcha. Ume les llevó arroz caliente. Heiji volvió con las cejas quemadas y contento, porque el estandarte de su brigada se quedó en pie hasta el final.",
            Parrafo.ANOCHECER to "Esta noche {nombre} no duerme bien. Huele a humo en todo el barrio, y cada ráfaga de viento le parece una campana. Se levanta dos veces a mirar el fogón, que está apagado. Mañana empezarán a reconstruir lo quemado, y habrá trabajo. No sabe si eso lo alegra.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "edo", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + ALDEA.take(2) + SIN_CASA.take(2) + OYAMA.take(1),
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + ALDEA.subList(2, 8) + SIN_CASA.subList(2, 5) + OYAMA.subList(1, 7),
            Parrafo.PUEBLO to PUEBLO + ALDEA[8] + SIN_CASA[5] + OYAMA[7],
            Parrafo.GENTE to GENTE + listOf(
                v("Sakubei, su hermano, es más callado que él y más ancho, y tiene las manos de quien ha pasado la vida en el agua del arrozal. Su cuñada le sirve de más en cada comida, y sus sobrinos lo siguen a todas partes para que les cuente cosas de Edo, que él adorna.", estado = "aldea"),
                v("En el templo duermen apretadas diez familias del callejón, y la viuda Otake, que perdió su casa y sus muñecas, manda en el rincón como mandaba en el callejón. Ume ayuda a los monjes en la cocina. Kinta juega con los otros niños entre las tumbas, como si fuera fiesta.", estado = "sin_casa"),
                v("En el grupo de peregrinos van once carpinteros del barrio, Heiji y el maestro Genbei, que sube despacio y no deja que nadie le cargue nada. Se conocen de toda la vida, y en el camino se cuentan cosas que en el barrio no se cuentan.", estado = "oyama"),
            ),
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + ALDEA[9] + SIN_CASA[6] + OYAMA[8],
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
