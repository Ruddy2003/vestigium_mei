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
 * El cuento de Marguerite, aprendiz de un taller de litografía de París, de 1898 a 1902 (Art nouveau). Es una persona
 * inventada en un mundo real. Sigue la gramática y el tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026, y la
 * voz del tema (`docs/diseno/temas/nouveau.md`): delicada, curiosa y un poco teatral, de taller y de café; habla de lo que
 * se ve (la luz, el tallo, la piedra que se entinta, la lluvia) y de las flores como de un idioma.
 *
 * Lo que este cuento cuida (PLAN_TEMAS, 10f, y `docs/diseno/temas/fuentes_nouveau.md`):
 * - **El oficio es el de las fuentes** (sección 8): una piedra por color; la misma hoja pasa de una a otra con sus marcas
 *   de registro; el agua y la grasa se rechazan; el dibujo se fija con goma arábiga y ácido nítrico débil; la prensa a
 *   mano con su carro, su «râteau» y su molinete, y la de vapor; las planchas de zinc que van llegando.
 * - **El metro, solo desde el 19 de julio de 1900** (corrección 8): antes va a pie; las variantes del tren llevan
 *   `desde = "abre_metro"` y las del camino entero a pie, `hasta`. A Montmartre, siempre a pie.
 * - **La ley, solo lo comprobado** (corrección 9): once horas como máximo desde el 30 de marzo de 1900 en los talleres
 *   con mujeres, y diez y media desde el 1 de abril de 1902. Lo que ganaba una aprendiz no se dice: está [sin verificar].
 * - **Le Chat Noir cerró en 1897** (corrección 10): sale solo como lo que ya no está. Los cafés-conciertos son La Cigale y
 *   el Divan japonais, que deja de serlo en agosto de 1901.
 * - **La gente real sale de lejos**: Zola en el periódico, Mucha y Sarah Bernhardt en las vallas, Loubet y Millerand en
 *   los periódicos y en la ley, Guimard en la firma de los letreros. Nada de lo que hacen con ella es inventado sobre ellos.
 * - **Lo que va tal cual** son solo frases comprobadas en el texto de la obra (`fuentes_nouveau.md`, sección 11), entre
 *   comillas españolas y con su autor y su obra al lado: «…» (Zola, J'accuse…!). Lo demás, en estilo indirecto. Lo que
 *   dicen las flores, también en estilo indirecto, de la lista de *Le Langage des fleurs* (sección 3). La prueba
 *   (`CuentoG9Prueba`) comprueba cada frase contra la lista.
 * - **Nada de Art déco ni de «Belle Époque»,** nada de cabaré de película, y la torre del Campo de Marte solo de lejos.
 *
 * Lo que tiene que respetar de su calendario (`vidas/anios/AnioG9.kt`):
 * - El oficio avanza: muele y limpia desde el principio; grania desde `graniar` (1); entinta desde `entintar` (1); mete
 *   los pliegos en sus marcas desde `registro` (2); la prensa de vapor, desde `prensa_vapor` (2); dibuja letras desde
 *   `primeras_letras` (4); prepara ella las piedras desde `obrera` (5).
 * - El camino: entero a pie hasta `abre_metro` (3); pasa por las obras del metro de `obras_metro` (1) a `abre_metro`; en
 *   el tren desde `abre_metro`.
 * - Su gente: Berthe, con novio de `berthe_novio` (2) a `boda_berthe` (3), casada desde `boda_berthe`, con una niña desde
 *   `ahijada` (5); Paul, en la escuela hasta `paul_aprendiz` (3); Anselme, en el taller hasta `anselme_retira` (5); Henri,
 *   desde `lilas` (4), con permiso de su padre desde `henri_padre` (4), prometido desde `dice_si` (5).
 * - El Divan japonais, de `divan` (2) a `divan_teatro` (4).
 * - El estado «exposicion» (temporada 3, del 14 de abril a la víspera del cierre): el pueblo, el anochecer y lo que hace
 *   de noche son suyos; lo demás vale todos los días.
 * - No repite lo que ya dice «Su vida»: el principio del agua y la grasa dicho en una frase, la casa en el faubourg
 *   Saint-Antoine, los quince céntimos del metro.
 */
internal object PiezasMarguerite {
    private const val EXPOSICION = "exposicion"

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    /** Durante la Exposición Universal (temporada 3). */
    private fun ex(texto: String, hacer: Hacer? = null) = v(texto, estado = EXPOSICION, hacer = hacer)

    private val INVIERNO = setOf(11, 12, 1, 2)
    private val VERANO = setOf(6, 7, 8)

    // =====================================================================
    //  La mañana
    // =====================================================================

    private val MANANA = listOf(
        v("{nombre} se despierta antes de que suenen las campanas, en el cuarto que comparte con su hermano, detrás de una cortina. Su madre ya ha encendido la cocina. Por la ventana del patio entra una luz gris que huele a serrín y a cola de carpintero: en el faubourg, los talleres abren antes que las tiendas."),
        v("Lo primero que oye {nombre} cada mañana es el mazo de su padre en el taller del patio, que empieza antes que nadie. Luego, los demás: una garlopa, una sierra, un martillo de tapicero. Se sienta en la cama y escucha un momento cómo despierta el barrio entero, golpe a golpe, como un reloj de madera."),
        v("{nombre} se lava la cara en la palangana, con el agua que subió anoche de la fuente del patio, y se recoge el pelo en un moño apretado, para que no se le enrede en la prensa. Se pone el delantal azul encima del vestido. En el espejo roto de la cómoda se ve la cara a trozos."),
        v("La mañana empieza en la escalera: cinco pisos de peldaños gastados, olor a sopa de ayer y a carbón, una vecina que ya discute con su marido. {nombre} baja de dos en dos. Abajo, la portera barre el portal y le da los buenos días sin levantar la cabeza."),
        v("Cuando {nombre} abre la ventana, el faubourg ya está despierto: carros cargados de tablones, un aprendiz con una silla al hombro, el lechero con sus cántaros. Desde el quinto piso los tejados de cinc se ven grises y brillantes, y entre ellos, lejos, la columna de la Bastille."),
        v("En invierno {nombre} se viste a oscuras, con la lámpara de petróleo bajita para no gastar. El agua de la palangana está tan fría que le duelen los dedos. Afuera los faroles de gas todavía están encendidos, y la calle huele a niebla y a castañas asadas.", meses = INVIERNO),
        v("En verano la luz entra temprano por la ventana del patio, y {nombre} se despierta con las golondrinas y con el calor que ya sube de los tejados. Abre la ventana de par en par. En el patio, su padre saca al aire una cómoda recién barnizada para que se seque.", meses = VERANO),
        v("Desde que tiene el cuaderno de tapas de tela, {nombre} lo abre un momento cada mañana, junto a la ventana, antes de vestirse: un tallo, una mano, el perfil de su madre inclinada sobre la costura. No lo enseña. Lo cierra y lo esconde en el fondo del cesto de la ropa.", desde = "cuaderno_nuevo"),
        v("Desde que es obrera y no aprendiz, {nombre} se viste con un poco más de cuidado: el cuello limpio, el lazo bien puesto. No es por nadie, se dice. Su madre la mira desde la cocina y no dice nada, que es su manera de decir mucho.", desde = "obrera"),
    )

    // =====================================================================
    //  El tiempo y la estación
    // =====================================================================

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el faubourg dicen que el tiempo se lee en el humo de las chimeneas: si se queda bajo, va a llover."),
        v("{nombre} mira el cielo por encima de los tejados antes de salir, como lo miran en el taller, donde saben que la humedad estira el papel y descuadra los colores. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} En París el cielo cambia tres veces en un día, y nadie se fía de ninguna."),
        v("{cielo} En {estacion}, el Sena baja de otro color, y los muelles huelen distinto: a barro, a polvo o a hojas mojadas."),
        v("{de_estacion} {cielo} Monsieur Ferrand dice que con este tiempo el papel se estira o se encoge, y que un color mal casado se nota a diez pasos."),
        v("Desde la place de la Bastille se ve el trozo de cielo más grande de todo su camino. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un azul pálido, y el sol saca brillo a los tejados de cinc.",
            "No hay una nube, y la luz entra derecha por las ventanas del taller, buena para ver los colores.",
            "Hace un día claro, de esos en que el humo de las chimeneas sube recto.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, de un gris parejo, como una piedra recién graniada.",
            "Pasan nubes bajas, y la luz se apaga y se enciende sobre la calle.",
            "Está nublado, con esa luz blanca que no deja sombras y que aplana los colores.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, una lluvia fina que pone negros los adoquines y brillantes los paraguas.",
            "Cae una lluvia fría, y el agua corre por las cunetas hasta las bocas de las alcantarillas.",
            "Llueve a ratos, y entre un chaparrón y otro la calle huele a piedra mojada.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: el cielo se pone morado sobre los tejados y los truenos hacen temblar los cristales.",
            "Truena, y la lluvia cae de golpe, tan gruesa que salta en el empedrado.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, una nieve menuda que se queda en las cornisas y se vuelve barro en la calle.",
            "Ha nevado en la noche, y los tejados están blancos hasta donde se ve.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, una niebla amarillenta que se traga los faroles a veinte pasos.",
            "La niebla sube del río y deja las calles sin final.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento fuerte que levanta los sombreros y hace golpear los postigos.",
            "Hace viento, y los carteles mal pegados de las vallas aletean como pájaros.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en los balcones del faubourg salen las primeras macetas de geranios.",
            "En primavera los árboles echan sus hojas, y las floristas de la calle venden violetas.",
            "Es el tiempo de los días largos, cuando se sale del taller todavía con luz.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el taller huele a tinta caliente y a barniz.",
            "En verano las calles se riegan temprano para que no levanten polvo.",
            "Es el tiempo en que los parisinos que pueden se van al campo, y los que no, se quedan.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, y las hojas de los árboles se pegan a los adoquines mojados.",
            "En otoño oscurece pronto, y en el taller se encienden las lámparas antes de la hora.",
            "Es el tiempo de las castañas asadas en las esquinas y de las primeras nieblas.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, y la estufa del taller no alcanza a calentar las piedras.",
            "En invierno se sale de casa de noche y se vuelve de noche.",
            "Es el tiempo de los sabañones, del carbón caro y de las ventanas empañadas.",
        ),
    )

    // =====================================================================
    //  El trabajo del tramo (cada texto vale a cualquiera de sus horas)
    // =====================================================================

    private val TRABAJO = listOf(
        v("A esta hora duerme en el cuarto de atrás, en una cama estrecha, con la cortina que la separa de su hermano corrida. La casa cruje. En el patio, el gato de la portera persigue algo entre los tablones. Desde la calle llega, de vez en cuando, el ruido de un fiacre que vuelve tarde.", hacer = Hacer.DORMIR),
        v("A esta hora duerme y sueña con colores: un rojo que no casa con su azul, una hoja que se sale de las marcas. Se despierta un momento, con el corazón deprisa, y se da cuenta de que no hay ninguna prensa. Se da la vuelta y vuelve a dormirse.", hacer = Hacer.DORMIR),
        v("A esta hora duerme con las manos todavía oliendo a tinta, por mucho que se las haya lavado. Su madre dice que la tinta del taller no se va ni con jabón ni con rezos. Por la rendija de la ventana entra la luz amarilla del farol de la calle.", hacer = Hacer.DORMIR),

        v("A esta hora toma café con leche en un tazón de loza, muy caliente, y moja en él un trozo de pan del día anterior. Come deprisa, de pie, mirando el reloj, como todo el mundo en París. El café es más leche que café, y aun así despierta.", hacer = Hacer.COMER),
        v("A esta hora come pan con un trozo de queso y una manzana, sentada en un banco, con el delantal todavía puesto. A su lado, alguien lee en voz alta un trozo del folletín del periódico. Nadie quiere quedarse sin saber cómo sigue.", hacer = Hacer.COMER),
        v("A esta hora come la sopa de pan y verduras que hace su madre los lunes y que dura hasta el jueves, recalentada en una lata. La sopa es lo que se come en el faubourg, dice su padre, y lo demás son cuentos. Ella se la toma hasta el final y rebaña con la corteza.", hacer = Hacer.COMER),

        v("A esta hora cruza la place de la Bastille, con la columna en medio y los carros dando la vuelta alrededor, a gritos. {nombre} espera a que pase un carro de cerveceros tirado por cuatro caballos y cruza corriendo, con la falda recogida. Al otro lado huele a pan recién hecho.", hacer = Hacer.CAMINO),
        v("A esta hora va por la calle con el chal cruzado sobre el pecho y la cesta colgada del brazo, entre obreras, mozos de recados y señoras con prisa. Las vallas están llenas de carteles. {nombre} los mira todos, como quien lee un periódico.", hacer = Hacer.CAMINO),
        v("A esta hora se para un momento delante de un escaparate de la rue de Rivoli, donde hay grabados y carteles enmarcados, y cuenta los colores de cada uno: cinco, seis, siete. Adivina cuál se tiró primero. Luego sigue, deprisa, porque se le ha hecho tarde.", hacer = Hacer.CAMINO),
        v("A esta hora va a pie por la rue Saint-Antoine y la rue de Rivoli, media hora larga de adoquines, entre el ruido de los cascos y los gritos de los cocheros. Se sabe el camino de memoria: la panadería, la fuente, la esquina donde siempre hay un charco. Camina rápido, con los botines gastados de tanto ir y venir.", hacer = Hacer.CAMINO, hasta = "abre_metro"),
        v("A esta hora pasa junto a las vallas de las obras del metro, que se han comido media calle: tierra amarilla, tablones, hombres con picos dentro de una zanja. Dicen que debajo de París van a correr trenes. {nombre} mira el agujero y no se lo cree del todo.", hacer = Hacer.CAMINO, desde = "obras_metro", hasta = "abre_metro"),
        v("A esta hora baja las escaleras de la estación, entre la barandilla de fundición verde, con sus quince céntimos de segunda en la mano. Abajo huele a piedra mojada y a grasa de máquina. El tren llega con su luz amarilla y sus coches de madera, y {nombre} sube con todos los demás, apretada.", hacer = Hacer.CAMINO, desde = "abre_metro"),
        v("A esta hora va en el metro, de pie, agarrada a la barra, mientras el tren corre por debajo de las calles. En cada estación se abren las puertas y entra el aire de otra calle. Por las ventanas no se ve más que la pared del túnel, y aun así todo el mundo mira por ellas.", hacer = Hacer.CAMINO, desde = "abre_metro"),
        v("A esta hora sale del metro y sube las escaleras con los demás, y el aire de la calle, después del túnel, le parece fresco aunque no lo sea. Ha tardado un cuarto de hora en lo que antes era media hora larga a pie. Le sobra tiempo, y todavía no sabe qué hacer con él.", hacer = Hacer.CAMINO, desde = "abre_metro"),

        v("A esta hora pone a secar los pliegos recién tirados, colgados con pinzas de madera de unas cuerdas que cruzan el taller de pared a pared. El taller se llena de hojas a medio hacer: un cartel con solo el amarillo, otro con el amarillo y el rojo. Pasar entre ellos es como andar por un bosque de papel.", hacer = Hacer.TRABAJO),
        v("A esta hora muele tinta en la losa de mármol con la moleta, en redondo, con todo el cuerpo, hasta que el polvo de color y el barniz se vuelven una pasta lisa. Cada color tiene su losa y su espátula, y no se mezclan. El rojo es el que más cansa: nunca está bastante fino para Gustave.", hacer = Hacer.TRABAJO),
        v("A esta hora ayuda a Gustave, el prensista, a cambiar la piedra de la prensa: se saca la del azul, se mete la del rojo. Cada cartel lleva una piedra por color, y el mismo pliego pasa por todas, una detrás de otra. Las piedras pesan como un niño grande, y se llevan entre dos, sin hablar.", hacer = Hacer.TRABAJO),
        v("A esta hora limpia las losas, las espátulas, los rodillos y el suelo, donde siempre cae una gota de algo. En un taller de litografía la grasa está prohibida donde no tiene que estar, dice monsieur Ferrand: una huella de dedo en una piedra limpia sale impresa en mil carteles.", hacer = Hacer.TRABAJO),
        v("A esta hora mira cómo Anselme dibuja en la piedra con el lápiz graso, inclinado, con la lupa en un ojo. Dibuja las letras al revés, porque la piedra las devuelve del derecho en el papel. {nombre} no se cansa de mirarlo. Él hace como que no la ve.", hacer = Hacer.TRABAJO, hasta = "anselme_retira"),
        v("A esta hora grania una piedra vieja con arena fina y agua, frotándola en redondo con otra piedra, hasta que el dibujo del cartel de antes se va en un barro gris y la cara queda áspera y limpia, lista para otro. Es un trabajo de paciencia, y de brazos.", hacer = Hacer.TRABAJO, desde = "graniar"),
        v("A esta hora moja la piedra con la esponja, de un lado a otro, para que el agua se quede donde no hay dibujo, y pasa encima el rodillo de cuero cargado de tinta. La tinta se agarra solo a las líneas grasas. Lo hace despacio, como le enseñaron, sin dejar que la piedra se seque.", hacer = Hacer.TRABAJO, desde = "entintar"),
        v("A esta hora mete los pliegos en la prensa, uno por uno, con las dos manos, cuidando que las marcas del papel caigan justo sobre las de la piedra. Si un pliego se tuerce un pelo, el color se sale del dibujo y el cartel no vale. Gustave hace girar el molinete; ella saca la hoja, la mira y la cuelga.", hacer = Hacer.TRABAJO, desde = "registro"),
        v("A esta hora atiende la prensa de vapor, que resopla y traquetea y va mucho más deprisa que la de mano. Hay que darle los pliegos a su ritmo, ni antes ni después, con las manos lejos de los rodillos. Al principio le daba miedo. Ahora le habla en voz baja, como a un caballo.", hacer = Hacer.TRABAJO, desde = "prensa_vapor"),
        v("A esta hora dibuja letras en una piedra pequeña, al revés, con el lápiz graso, mientras Anselme mira por encima de su hombro. Las letras de ahora se curvan como tallos y terminan en hojas, y cada una tiene que parecer que crece. Anselme le borra una y le dice que vuelva a empezar.", hacer = Hacer.TRABAJO, desde = "primeras_letras"),
        v("A esta hora prepara ella misma una piedra dibujada: la cubre con goma arábiga y un poco de ácido, la deja reposar, la lava y tira una prueba. Antes se lo hacían otros. Ahora, si sale mal, la culpa es suya, y si sale bien, nadie le dice nada, que es como debe ser.", hacer = Hacer.TRABAJO, desde = "obrera"),

        v("A esta hora ayuda a su madre a poner la sopa, a picar los puerros, a lavar los platos en el barreño. Su padre lee el periódico en voz alta, con la lámpara cerca. Paul hace como que estudia. {nombre} escucha a medias, con las manos en el agua caliente.", hacer = Hacer.CUIDAR),
        v("A esta hora plancha el delantal de mañana con la plancha que se calienta en la cocina, y zurce una media. Su madre cose a su lado, en la máquina de pedal, una blusa que no es para ellas. Las dos hablan de la gente del patio sin levantar la vista.", hacer = Hacer.CUIDAR),
        v("A esta hora sube del patio dos cubos de agua de la fuente, cinco pisos, sin parar en ningún descansillo. Luego saca la ceniza de la cocina y le da al gato de la portera las espinas del pescado. La casa huele a sopa y a cola de carpintero.", hacer = Hacer.CUIDAR),

        v("A esta hora, algunas noches, sube a pie con Berthe hasta Montmartre, a La Cigale, y se sientan arriba, en lo más alto de la sala. Abajo, en el escenario, una cantante con un sombrero enorme canta una canción de amor y otra de borrachos. La sala entera se sabe los estribillos.", hacer = Hacer.GENTE),
        v("A esta hora se sienta con las vecinas en el patio, cuando hace bueno, en sillas sacadas de las casas, y se cuentan la vida del barrio: quién se casa, quién debe, quién se ha ido sin pagar. Los hombres fuman en el portal. Un niño toca el acordeón, mal y con ganas.", hacer = Hacer.GENTE),
        v("A esta hora va con Berthe a mirar los escaparates iluminados de los grandes almacenes, sin entrar, eligiendo lo que se comprarían si fueran otras. Berthe siempre elige sombreros. {nombre} siempre elige los colores, aunque no sepa todavía para qué.", hacer = Hacer.GENTE),
        v("A esta hora pasa con Berthe, en Montmartre, por delante del sitio donde estuvo Le Chat Noir, que cerró cuando murió su dueño. Berthe dice que allí iban los pintores y los poetas. {nombre} dice que ahora irán a otro sitio, porque los pintores siempre encuentran adónde ir.", hacer = Hacer.GENTE),
        v("A esta hora está en el Divan japonais de la rue des Martyrs, con sus farolillos y sus paredes pintadas a la japonesa, donde dicen que triunfó Yvette Guilbert. La sala es pequeña y huele a tabaco y a polvos de arroz. {nombre} se ríe con las canciones atrevidas y se tapa la boca.", hacer = Hacer.GENTE, desde = "divan", hasta = "divan_teatro"),
        v("A esta hora, algunas noches, va a casa de Berthe y Lucien, dos cuartos encima de un taller de torneros, y juegan a las cartas a la luz de la lámpara mientras Lucien cuenta chistes que ya ha contado. Berthe le pisa el pie por debajo de la mesa. {nombre} hace como que no ve nada.", hacer = Hacer.GENTE, desde = "boda_berthe"),
        v("A esta hora, algunas noches, pasea con Henri por los muelles del Sena, despacio, con Paul diez pasos detrás, porque su padre lo ha puesto de carabina. Henri habla de libros y ella de colores, y los dos se entienden más de lo que parece. Paul silba para que se sepa que está.", hacer = Hacer.GENTE, desde = "henri_padre"),
    )

    /** Las noches del año de la Exposición. */
    private val EXPOSICION_TRABAJO = listOf(
        ex("A esta hora, algunas noches, va con Berthe hasta el Sena a ver encendida la Exposición, aunque no les alcance para entrar: desde la orilla se ve brillar el Palacio de la Electricidad y la cascada del Château d'Eau, iluminada. La gente se para en los puentes a mirar, callada, como en misa.", Hacer.GENTE),
        ex("A esta hora vuelve a pie con Berthe desde Montmartre por calles llenas de extranjeros que preguntan el camino en lenguas que no entiende. Ella les indica con la mano. Todos parecen contentos de estar perdidos en París, y algunos les dan las gracias en un francés que da risa.", Hacer.GENTE),
        ex("A esta hora pasea con su padre y con Paul por la orilla del Sena, del lado de la Exposición, donde la noche se vuelve día. Su padre, que no se admira de nada, se para delante de una cúpula iluminada y dice que eso sí que es carpintería. Paul pide un helado. No se lo compran.", Hacer.GENTE),
    )

    // =====================================================================
    //  El pueblo y el entorno
    // =====================================================================

    private val PUEBLO = listOf(
        v("{entorno} El taller de monsieur Ferrand está en el bajo de una casa de cinco pisos, con un escaparate donde se ven los carteles que se tiran dentro. En el patio de atrás se guardan las piedras, de pie, en estantes, como libros enormes y grises."),
        v("El faubourg Saint-Antoine es un barrio de talleres: ebanistas, torneros, doradores, tapiceros, cada uno en su patio. {entorno} Por la calle van carros de tablones y aprendices con muebles al hombro, y todo huele a madera."),
        v("{entorno} Cerca del taller está el Palais-Royal, con su jardín y sus galerías de tiendas donde se venden medallas, bastones y soldados de plomo. A veces {nombre} se come allí su pan, en un banco, entre niñeras y jubilados."),
        v("Las vallas de París están cubiertas de carteles, unos encima de otros: chocolates, bicicletas, teatros, cafés-conciertos, papel de fumar. {entorno} Los pegadores pasan con su cubo de engrudo y su escalera, y en una mañana le cambian la cara a una calle."),
        v("Cuando cruza el puente a algún recado, ve el Sena lleno de barcazas de carbón y de arena, los muelles con sus libreros y las torres de Notre-Dame. {entorno} A lo lejos, delgada y oscura, asoma la torre de hierro del Campo de Marte."),
        v("{entorno} En la calle del taller hay de todo: una crémerie donde comen los obreros, un vendedor de vinos, una mercería, una imprenta de libros, una portera que lo ve todo. Los vecinos conocen a {nombre} por el delantal manchado de colores."),
        v("{entorno} Por la rue de Rivoli pasan carros de mudanzas, fiacres y vendedores ambulantes, bajo los soportales donde se refugia la gente cuando llueve. Los soportales son largos y regulares, y {nombre} los recorre a veces contando los arcos."),
        v("{entorno} Desde que abrió el metro, delante de la estación hay siempre gente que sube y baja por la escalera, entre la barandilla de fundición verde, que tiene algo de planta y algo de insecto. Los viejos todavía la miran con desconfianza. Los niños quieren bajar a ver.", desde = "abre_metro"),
    )

    private val EXPOSICION_PUEBLO = listOf(
        ex("Este año París está lleno de gente de fuera: provincianos con su ropa de domingo, ingleses, alemanes, americanos, que van con un plano abierto y la cabeza en alto. {entorno} En los escaparates se venden medallas, abanicos y postales de la Exposición."),
        ex("En todas las vallas, este año, hay carteles de la Exposición y de sus pabellones, unos encima de otros. {entorno} Por la rue de Rivoli bajan coches llenos de visitantes hacia el Sena, y los cocheros cobran lo que quieren."),
        ex("{entorno} Este año en las calles del centro no se cabe: hay más fiacres, más vendedores, más carteristas, dice la portera, y más luces. Los cafés sacan las mesas a la acera hasta tarde. Hasta el faubourg llegan los ecos de la fiesta."),
        ex("La Exposición ocupa las dos orillas del Sena, con sus palacios y los pabellones de cuarenta países, y desde los puentes se ven sus cúpulas y sus torrecitas como un pueblo de juguete. {entorno} El puente nuevo, el de Alejandro III, está siempre lleno de gente."),
    )

    /** La entrada de la estación delante del taller (el arco `metro`): las obras, la barandilla y los faroles. */
    private val ENTORNOS = listOf(
        Entorno("metro", 0.0, 0.05, frases = listOf(
            "En la calle todavía no se nota nada del metro: solo se habla de él en los periódicos.",
            "De ese metro que dicen no se ve nada todavía: la calle es la de siempre, de adoquines y de barro.",
        )),
        Entorno("metro", 0.05, 0.25, frases = listOf(
            "Al fondo de la calle se ven las vallas de las obras del metro, que todavía no llegan al taller.",
            "Las obras del metro avanzan por el barrio, y de vez en cuando se oye, lejos, el golpe de los picos.",
        )),
        Entorno("metro", 0.25, 0.7, frases = listOf(
            "En la esquina están las vallas de las obras del metro, y detrás, tierra amarilla, tablones y el ruido de los picos.",
            "Las obras del metro han levantado media calle, y los carros tienen que hacer cola para pasar por lo que queda.",
        )),
        Entorno("metro", 0.7, 0.95, frases = listOf(
            "En la esquina está la entrada de la estación, con su barandilla de fundición verde que se curva como un tallo.",
            "Delante del taller baja la escalera del metro, entre una barandilla verde que parece crecida del suelo.",
        )),
        Entorno("metro", 0.95, 1.0, frases = listOf(
            "En la entrada del metro, dos candelabros de fundición sostienen sus faroles y el letrero «Métropolitain», con unas letras que se doblan como hojas.",
            "Junto al taller, la entrada del metro tiene ya sus faroles, que de noche se ven desde lejos, como dos frutos encendidos.",
        )),
    )

    // =====================================================================
    //  La gente cercana
    // =====================================================================

    private val GENTE = listOf(
        v("Su padre es ebanista, como su abuelo, y trabaja en un taller del patio con dos oficiales: hace cómodas y aparadores que se venden con el nombre de otro en las tiendas buenas. Habla poco, fuma en pipa y dice que un mueble se juzga por los cajones, que es donde no mira nadie."),
        v("Su madre cose en casa, a destajo, en una máquina de pedal: blusas, camisas, cuellos, lo que le traigan de un almacén del Sentier. Cose desde que se levanta hasta que se acuesta, con las gafas en la punta de la nariz, y aun así canta."),
        v("Monsieur Ferrand, el patrón, es un hombre bajo, con chaleco y reloj de cadena, que se pasa el día entre el taller y los clientes. Cuando mira un cartel recién tirado lo pone a contraluz, frunce la boca y dice que está bien o que otra vez, y nunca otra cosa."),
        v("Gustave, el prensista, lleva veinte años en la prensa y tiene los brazos como troncos y los dedos negros hasta las uñas. Grita mucho y enseña poco, pero cuando ella acierta un registro difícil le da una palmada en el hombro que la deja torcida."),
        v("La abuela vive sola en dos cuartos al final del faubourg, con un canario y el retrato de su marido. Tiene un libro viejo, Le Langage des fleurs, firmado por una tal Charlotte de La Tour, que dicen que no es su nombre de verdad, y se lo sabe casi de memoria."),
        v("El tío Jules, el hermano de su madre, es jardinero en los invernaderos del Fleuriste municipal, en Auteuil, en la otra punta de París. Viene poco, porque las plantas no esperan, y cuando viene trae un esqueje en un tiesto y huele a tierra mojada."),
        v("El viejo Anselme dibuja las letras de los carteles desde antes de que ella naciera. Es flaco y encorvado, con una blusa gris y una lupa colgada del cuello, y en sus pruebas de letras copia versos para no aburrirse: le gustan Verlaine y Baudelaire.", hasta = "anselme_retira"),
        v("Berthe, la otra aprendiz, tiene un año más que ella, el pelo rojo y la lengua rápida. Se sabe todas las canciones de los cafés-conciertos y las canta en el taller, bajito, hasta que Gustave grita. Es su amiga desde el primer día, cuando le enseñó dónde colgar el chal.", hasta = "berthe_novio"),
        v("Berthe tiene novio, Lucien, un tornero del faubourg, de bigote rubio y manos grandes, y no habla de otra cosa. En el taller se lo cuenta todo a {nombre} en voz baja, entre pliego y pliego. {nombre} escucha, se ríe y piensa que ella no tiene ninguna prisa.", desde = "berthe_novio", hasta = "boda_berthe"),
        v("Berthe se casó con Lucien y vive con él encima de un taller de torneros. Sigue en el taller de Ferrand, pero a la salida ya no se queda a charlar: tiene prisa por llegar antes que él y encender la cocina. {nombre} le dice que parece una señora. Berthe le saca la lengua.", desde = "boda_berthe", hasta = "ahijada"),
        v("Berthe tiene una niña, Louise, y {nombre} es su madrina. Los domingos Berthe se la deja un rato, y ella le dibuja flores en la palma de la mano con el dedo, y le dice lo que quiere decir cada una. La niña se ríe de todas igual.", desde = "ahijada"),
        v("Paul, su hermano, tiene doce años y va todavía a la escuela del barrio, con la cartera de cuero y las rodillas peladas. Le pide que le dibuje caballos y luego dice que no se parecen. Ella le dibuja uno con alas, para que no pueda compararlo.", hasta = "paul_aprendiz"),
        v("Paul ha dejado la escuela y trabaja con su padre en el taller del patio, de aprendiz: barre virutas, sujeta tablones, lija hasta que no siente los dedos. Vuelve a casa con serrín en el pelo y la cara de los que han descubierto que trabajar cansa.", desde = "paul_aprendiz"),
        v("Henri, el cajista de la imprenta de la esquina, compone libros letra por letra, de plomo, y le dice que lo suyo es más difícil que lo de ella, porque él trabaja al revés todo el día. {nombre} le contesta que ella también. Se ven a la salida, en la esquina, como por casualidad.", desde = "lilas", hasta = "henri_padre"),
        v("Henri ha pedido permiso a su padre para pasearla los domingos, y su padre ha dicho que sí con la pipa en la boca, que es como dice las cosas importantes. Su madre ha dicho que ya veremos, que es como dice que sí. Paul se burla de ella todas las noches.", desde = "henri_padre", hasta = "dice_si"),
        v("Henri y ella se casarán cuando junten para los muebles. Su padre ya ha empezado, en secreto, una cómoda para ellos, de las buenas, con los cajones de roble, y el secreto lo conoce todo el patio. Henri dice que la pondrán donde se vea.", desde = "dice_si"),
        v("Anselme ya no viene al taller: vive con su hija en Belleville y le ha dejado sus lápices en una caja de puros. A veces {nombre} le escribe una carta con una letra dibujada en la esquina, para que vea que no se le olvida lo que le enseñó.", desde = "anselme_retira"),
    )

    // =====================================================================
    //  El objeto del día
    // =====================================================================

    private val OBJETO = listOf(
        v("{dato_objeto} En un taller, dice Anselme, las cosas pequeñas son las que mandan."),
        v("Hoy {nombre} se fija en algo que tiene en las manos todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo piensa mientras trabaja, y luego se le olvida, como se olvidan las cosas que siempre están."),
        v("Todo lo que hay en el taller y en la casa tiene su oficio y su manera, y casi nada se tira. {dato_objeto}"),
        v("{dato_objeto} Su padre dice que las cosas bien hechas no se notan hasta que faltan."),
        v("Lo de cada día también tiene su historia, aunque nadie la cuente. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("piedra", "la piedra litográfica", "una piedra litográfica", "La piedra litográfica",
            "La piedra litográfica es una losa gruesa, gris y lisa, que se dibuja con lápiz o tinta grasa; luego se moja, y la tinta del rodillo se agarra solo al dibujo, porque el agua y la grasa se rechazan; para un cartel en color hace falta una piedra por cada color."),
        ObjetoCuento("rodillo", "el rodillo de cuero", "un rodillo de cuero", "El rodillo de cuero",
            "El rodillo de entintar es un cilindro forrado de cuero, con dos mangos, que se carga de tinta en la losa y se pasa sobre la piedra mojada, de un lado a otro, hasta que el dibujo brilla negro; un rodillo mal cuidado deja rayas, y una raya sale en todos los carteles."),
        ObjetoCuento("moleta", "la moleta", "una moleta", "La moleta",
            "La moleta es una piedra de mano, lisa por abajo, con la que se muele la tinta en la losa, el polvo de color con el barniz, en redondo, hasta hacer una pasta tan fina que no cruje; moler bien es lo primero que aprende un aprendiz, y lo que más tarda."),
        ObjetoCuento("lapiz", "el lápiz litográfico", "un lápiz litográfico", "El lápiz litográfico",
            "El lápiz litográfico es negro y graso, y se afila con una navaja; lo que dibuja en la piedra es lo que luego se agarra a la tinta, y por eso la piedra no se toca con los dedos: hasta una huella sale impresa."),
        ObjetoCuento("cuaderno", "el cuaderno de dibujo", "un cuaderno de dibujo", "El cuaderno de dibujo",
            "El cuaderno de dibujo es de papel barato, cosido a mano, y en él se apunta lo que se ve: un tallo, una mano, una letra, una sombra; Anselme dice que el que no dibuja todos los días, aunque sea una hoja, se olvida de mirar."),
        ObjetoCuento("delantal", "el delantal", "un delantal", "El delantal",
            "El delantal es de tela azul gruesa, largo hasta los tobillos, con un bolsillo para el trapo y otro para el lápiz; al principio era de un solo color y ahora es de todos: cada mancha es de un cartel, y ella sabe de cuál."),
        ObjetoCuento("peine", "el peine de carey", "un peine de carey", "El peine de carey",
            "El peine de carey es de concha de tortuga, de color de miel con manchas oscuras, y sujeta el moño para que el pelo no se enrede en la prensa; fue de su madre, que lo llevó el día de su boda, y tiene un diente roto."),
        ObjetoCuento("lazo", "el lazo de seda", "un lazo de seda", "El lazo de seda",
            "El lazo de seda es violeta, como un iris, y lo compró en una mercería del faubourg con unas monedas ahorradas; solo se lo pone los domingos y para subir a Montmartre, y lo guarda doblado en un papel de seda para que no pierda el brillo."),
        ObjetoCuento("postal", "la tarjeta postal", "una tarjeta postal", "La tarjeta postal",
            "Las tarjetas postales ahora llevan dibujos: vistas de París, flores, mujeres de perfil entre tallos; se escriben con cuatro palabras y se mandan a la otra punta de la ciudad, y ella guarda en una caja de galletas las que tienen flores."),
        ObjetoCuento("libro_flores", "el libro de las flores", "un libro de flores", "El libro de las flores",
            "Le Langage des fleurs es el libro viejo de la abuela, donde cada flor quiere decir algo: la violeta, la modestia; el lila, la primera emoción de amor; la amapola, el consuelo; con un ramo bien escogido se puede escribir una carta sin escribir nada."),
        ObjetoCuento("abanico", "el abanico de papel", "un abanico de papel", "El abanico de papel",
            "El abanico es de papel plegado, impreso en el taller, con el anuncio de un chocolate de un lado y un ramo de lirios del otro; se reparten gratis en los teatros y en las tiendas, y en verano todas las mujeres del faubourg llevan uno."),
        ObjetoCuento("periodico", "el periódico", "un periódico", "El periódico",
            "El periódico que compra su padre trae de todo: la política, los crímenes, los anuncios y, abajo, el folletín, que es lo que lee primero toda la casa; cuando lo ha leído entero, su padre lo dobla en cuatro y lo guarda para encender la cocina."),
    )

    // =====================================================================
    //  Lo que piensa
    // =====================================================================

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en los colores como en personas: el amarillo, que va primero y no se ve hasta que llegan los demás; el rojo, que manda; el azul, que lo arregla todo al final. Piensa que un cartel es como una familia. Se ríe sola, y Berthe le pregunta de qué."),
        v("{nombre} piensa en el libro de la abuela, donde cada flor quiere decir algo: la violeta, la modestia; el lila, la primera emoción de amor; la margarita de los prados, que lo pensará. Piensa que tiene nombre de flor que duda. A sus {edad} años, no le parece mal."),
        v("{mundo} {nombre} piensa en las mujeres de los carteles, de perfil, con el pelo suelto como tallos y una flor en la mano, que miran la calle desde las vallas. Nadie en el faubourg se parece a ellas. Piensa que a lo mejor esa es la gracia: que no se parezcan a nadie."),
        v("{nombre} piensa en lo que dice Anselme: que lo que se dibuja en la piedra sale al revés, y que hay que pensar al revés para que salga derecho. Piensa que con la gente pasa lo mismo, y que su madre lo sabe desde hace mucho."),
        v("{mundo} {nombre} piensa en París, que cambia cada vez que lo mira: una calle abierta, un palacio que sube, una valla con carteles nuevos encima de los viejos. Piensa que la ciudad es como una piedra que se grania y se vuelve a dibujar. Le gustaría saber qué dibujo viene."),
        v("{nombre} piensa en un verso que Anselme copió en una prueba de letras y que ella se sabe sin entenderlo del todo: «Les parfums, les couleurs et les sons se répondent.» (Baudelaire, Correspondances). Piensa que en el taller es verdad: la tinta huele, el rojo suena fuerte y el azul, bajito."),
        v("{mundo} {nombre} piensa en algo que leyó en un libro viejo que alguien dejó en el taller: un poeta que decía que, al decir una flor, se levanta «l'absente de tous bouquets» (Mallarmé, Avant-dire), la ausente de todos los ramos. No lo entendió. Le gustó igual: la flor que no está en ningún ramo."),
        v("{nombre} piensa en el metro, en toda esa gente que ahora va por debajo de las calles como el agua por las cañerías, sin ver el cielo. Piensa que ella es una de esas gotas. Piensa que París tiene ahora dos pisos, y que el de abajo también es suyo.", desde = "abre_metro"),
        v("{nombre} piensa que ya no es aprendiz, y que no se nota tanto como creía: las mismas piedras, la misma prensa, el mismo Gustave gritando. Lo que cambia es que ahora le preguntan. Piensa que eso, para empezar, ya es mucho.", desde = "obrera"),
    )

    /** El estado del mundo de cada temporada (una frase para `{mundo}`). Están las cinco, aunque solo la 1 tenga sus especiales. */
    private val MUNDOS = mapOf(
        "obras" to listOf(
            "Este año París es una ciudad en obras: se levantan palacios para la Exposición, un puente nuevo, y se habla de trenes por debajo de las calles.",
            "En el taller dicen que vienen años de mucho trabajo, porque París entero se prepara para la Exposición, y una exposición es un mar de carteles.",
            "Este año en los cafés no se habla de otra cosa que del capitán Dreyfus, y en las casas se discute a gritos.",
        ),
        "vispera" to listOf(
            "Este año todo París mira hacia la Exposición que viene: se termina, se pinta, se dora, y hay andamios por todas partes.",
            "En el taller se trabaja más que nunca: todo el mundo quiere su cartel para cuando llegue la gente de fuera.",
            "Este año las obras del metro avanzan por debajo de la ciudad, y en las calles hay más vallas que nunca.",
        ),
        "exposicion" to listOf(
            "Este año París es el escaparate del mundo, con su Exposición, su metro y sus luces, y hasta en el faubourg se habla de ello como si fuera de cada uno.",
            "Este año pasan por París millones de visitantes, y todos se llevan a casa un cartel, una postal o un abanico.",
            "Este año la luz eléctrica es la reina de la Exposición, aunque en las casas del faubourg se siga cosiendo con lámpara de petróleo.",
        ),
        "faroles" to listOf(
            "Pasada la Exposición, París se queda con sus palacios nuevos, su puente y su metro, como una casa después de la fiesta.",
            "Este año el estilo nuevo, el de las curvas y los tallos, está en todas partes: en las entradas del metro, en las joyerías, en los cafés.",
            "Dicen que en Nancy los vidrieros y los ebanistas trabajan juntos, con flores y libélulas, y su padre lo cuenta como si le hubieran ganado una carrera.",
        ),
        "jornada" to listOf(
            "Este año la jornada en los talleres con mujeres baja a diez horas y media, y en las casas se hace la cuenta de lo que se gana y de lo que se pierde.",
            "Este año el metro sigue creciendo por debajo de París, y cada estación nueva es una fiesta pequeña en su barrio.",
            "Este año en las vallas hay menos carteles de palacios y más de chocolates, bicicletas y teatros, como antes.",
        ),
    )

    // =====================================================================
    //  El anochecer
    // =====================================================================

    private val ANOCHECER = listOf(
        v("Al salir del taller la calle ya huele a noche: se encienden los faroles de gas uno a uno, y el farolero pasa con su pértiga sin mirar a nadie. {nombre} se ata el chal y se mete entre la gente que vuelve a casa, como en un río que baja."),
        v("Anochece sobre el faubourg con olor a sopa y a barniz, y en todas las ventanas del patio se encienden lámparas. {nombre} se asoma un momento antes de cerrar los postigos: una vecina se peina, un viejo lee, un niño llora. Cada ventana es un cartel distinto."),
        v("Al caer la tarde la luz se pone dorada sobre los tejados de cinc y luego violeta, y {nombre} piensa qué piedras harían falta para ese color: un amarillo, un rosa, un azul encima. No le sale la cuenta. Ninguna piedra tiene el color de la tarde."),
        v("De noche, desde la ventana del quinto piso, París es un mar de tejados y chimeneas, y a lo lejos, hacia el oeste, el cielo tiene un resplandor amarillo, el de los bulevares iluminados. {nombre} lo mira un rato antes de acostarse. Luego apaga la lámpara."),
        v("Anochece, y en la cocina su madre cuenta el dinero de la semana sobre la mesa, en montoncitos: el carbón, el pan, el alquiler. {nombre} pone encima sus monedas. Su madre no le da las gracias, porque entre ellas no hace falta. Le sirve un café."),
        v("Cuando se pone el sol, los cafés del faubourg encienden sus lámparas sobre las mesas de mármol, y los obreros se paran a tomar un vaso en el mostrador antes de subir a casa. {nombre} pasa de largo. Desde la puerta de uno sale una canción que conoce."),
        v("Al caer la tarde, en invierno, ya es noche cerrada cuando sale del taller, y la niebla convierte cada farol en una flor de luz borrosa. Los pasos suenan en los adoquines mojados. {nombre} camina deprisa, con las manos dentro del chal.", meses = INVIERNO),
        v("En verano anochece tarde, y al salir del taller todavía hay sol en los pisos altos. {nombre} se da una vuelta por el muelle antes de volver, para ver el río ponerse de color de cobre, y llega a casa cuando su madre ya ha empezado a preocuparse.", meses = VERANO),
        v("Al caer la tarde sale del metro con los ojos acostumbrados a la luz amarilla del túnel, y la plaza le parece enorme y azul, con la columna en medio como un tallo de bronce. Por un momento no sabe dónde está. Luego huele el faubourg, y lo sabe.", desde = "abre_metro"),
    )

    private val EXPOSICION_ANOCHECER = listOf(
        ex("Al caer la tarde, este año, el cielo hacia el oeste se enciende de otro color: es la Exposición, que prende sus miles de lámparas a la orilla del Sena. Desde el quinto piso {nombre} ve el resplandor por encima de los tejados, como un amanecer equivocado."),
        ex("Anochece, y por las calles del centro bajan hacia el río coches y gente a pie, todos en la misma dirección, a ver encenderse la Exposición. {nombre} los mira pasar desde la puerta del taller. Mañana hay que madrugar. Mañana también estará encendida."),
        ex("De noche, este año, París no se acaba de dormir: en los bulevares hay gente hasta tarde, en los cafés se habla en todas las lenguas y los fiacres van y vienen. En el faubourg, en cambio, la noche es la de siempre, con su gato y su farol."),
        ex("Al caer la tarde vuelve a casa entre visitantes que llevan en la mano el plano de la Exposición y un abanico de recuerdo, y que se paran en medio de la acera a mirar los edificios. {nombre} los esquiva con paciencia de parisina. Ella también mira, pero sin pararse."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1 (el 1898)
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES_T1 = mapOf(
        "anio_nuevo" to unDia(
            Parrafo.MANANA to "Hoy es el día de Año Nuevo, y {nombre} se pone el vestido bueno, el lazo y los botines de los domingos, que le aprietan. Su madre le arregla el pelo dos veces. En la calle, los porteros esperan su aguinaldo en el portal, y los niños van con una naranja en la mano, como si fuera de oro.",
            Parrafo.TRABAJO to "Hoy no se trabaja: es el primer día del año, y hasta los talleres del faubourg están callados. Su padre no baja al patio; se queda en la cocina leyendo el periódico de cabo a rabo, con las gafas puestas, como un señor. A {nombre} el silencio de los talleres le parece más raro que cualquier ruido.",
            Parrafo.GENTE to "Por la tarde van todos a casa de la abuela, al otro extremo del faubourg, con un pastel envuelto en papel. La abuela les da un beso a cada uno, regaña a Paul por crecer y le enseña a {nombre} su libro de las flores, abierto en la página de la violeta, la modestia, como si no se lo hubiera enseñado nunca.",
        ),
        "entra_taller" to unDia(
            Parrafo.MANANA to "Hoy {nombre} sale de casa de noche todavía, con el delantal nuevo doblado en la cesta, y camina hasta el Palais-Royal por calles que conoce de toda la vida y que esta mañana le parecen de otra ciudad. A la puerta del taller se para, se alisa la falda y empuja.",
            Parrafo.TRABAJO to "Hoy, su primer día, monsieur Ferrand le enseña el taller en un minuto: las prensas, las losas de moler, las cuerdas de secar, los estantes de las piedras. Luego le da una escoba. Barre toda la mañana, y por la tarde la ponen a lavar espátulas. Nadie le explica nada, y ella lo mira todo.",
            Parrafo.GENTE to "En el taller la recibe Berthe, la otra aprendiz, pelirroja y risueña, que le enseña dónde se cuelga el chal, cuál es la losa de cada color y a quién no hay que hablarle antes de que se tome el café. Gustave, el prensista, ni la mira. El viejo Anselme, desde su mesa, la saluda con la cabeza.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que desde hoy tiene un oficio, o el principio de uno, y que su madre ha llorado un poco al darle el delantal. Piensa en los carteles de las vallas, que ha mirado toda la vida sin saber cómo se hacen. Desde hoy lo va a saber. Le da miedo y le da gusto, a partes iguales.",
        ),
        "jaccuse" to unDia(
            Parrafo.PUEBLO to "Hoy los vendedores de periódicos gritan un solo nombre por la rue de Rivoli: Zola. En la primera plana de L'Aurore, con letras enormes, hay una carta suya al presidente de la República sobre el caso del capitán Dreyfus, y acusa a unos y a otros con sus nombres. Los ejemplares se acaban en un momento.",
            Parrafo.GENTE to "En el taller no se habla de otra cosa. Gustave dice que Zola es un valiente; monsieur Ferrand, que eso no es asunto de un taller, y lo dice dos veces. Anselme lee el artículo en voz alta, despacio, y al final repite una frase: «la vérité est en marche, et rien ne l'arrêtera» (Zola, J'accuse…!). Que la verdad está en marcha, y nada la detendrá.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que no entiende bien de qué acusan al capitán, ni de qué acusa Zola a los que lo acusan. Piensa que esta noche, en casa, su padre y su abuela van a discutir, como discuten ahora todas las familias. Piensa que una carta en un periódico puede hacer más ruido que una prensa, y le parece raro y hermoso.",
        ),
        "moler_tinta" to unDia(
            Parrafo.TRABAJO to "Hoy Gustave le pone delante una losa de mármol, un montón de polvo rojo, un frasco de barniz y la moleta, y le dice que muela. Ella muele en redondo, con todo el cuerpo, una hora, dos horas. Gustave mete el dedo, frota la pasta entre las yemas, escucha y dice que todavía cruje. Ella vuelve a empezar.",
            Parrafo.GENTE to "A media tarde Berthe se le acerca con un trapo y le enseña el truco: no apretar más, sino durante más tiempo, y cambiar de brazo antes de que duela. Gustave las ve, gruñe, y no dice nada. Al final del día mete el dedo otra vez, frota, escucha, y se va sin hablar. Berthe le dice que eso es que está bien.",
            Parrafo.ANOCHECER to "Al caer la tarde vuelve a casa con los hombros de piedra y las uñas rojas, de un rojo que no se va con el agua. Su madre le mira las manos y le pregunta si ha matado a alguien. {nombre} se ríe tanto que se le olvida que le duelen. Esa noche se duerme antes que Paul.",
        ),
        "primera_paga" to unDia(
            Parrafo.TRABAJO to "Hoy es día de paga. Al final de la jornada monsieur Ferrand llama a cada uno por su nombre desde la puerta de su despacho y le pone en la mano un sobre pequeño. A los aprendices les toca al final. El sobre de {nombre} pesa poco, y aun así lo lleva todo el camino agarrado, con la mano dentro del bolsillo.",
            Parrafo.GENTE to "En casa le da el sobre entero a su madre, sin abrirlo, como hacen las hijas del faubourg. Su madre cuenta las monedas sobre la mesa, despacio, y le devuelve unas cuantas: para ella, le dice, para un lazo o para el café-concierto. Su padre levanta la vista del periódico, no dice nada y le guiña un ojo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hasta hoy todo lo que tenía se lo habían dado, y que estas monedas son las primeras que son suyas porque las ha ganado ella. Piensa en el lazo violeta de la mercería. Piensa que va a esperar una semana, para estar segura de que lo quiere de verdad.",
        ),
        "graniar" to unDia(
            Parrafo.TRABAJO to "Hoy Gustave le enseña a graniar una piedra: se echa arena fina y agua sobre la cara dibujada y se frota con otra piedra, en redondo, hasta que el cartel de antes desaparece y la superficie queda áspera como la piel de una naranja. Así la piedra vuelve a estar lista para otro dibujo. Tarda la tarde entera.",
            Parrafo.GENTE to "Mientras frota, Anselme se acerca a ver qué cartel está borrando: era uno suyo, de un teatro que ya cerró. Se queda mirando cómo se van sus letras, una por una, en el barro gris. Luego le dice que siga, que una piedra no es para guardar recuerdos sino para hacer carteles, y se vuelve a su mesa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que una piedra no se acaba nunca: se dibuja, se imprime, se borra y se vuelve a dibujar, y cada cartel que pasó por ella queda debajo, invisible, como las capas de pintura de una puerta vieja. Piensa que hoy ha borrado algo que hizo otro. Piensa que algún día alguien borrará algo suyo.",
        ),
        "seis_lineas" to unDia(
            Parrafo.PUEBLO to "Hoy los periódicos dicen que las seis primeras líneas del metro se declaran de utilidad pública: París tendrá trenes por debajo de las calles. En el mostrador de la crémerie, un cochero dice que eso no va a funcionar nunca, y un obrero, que ya iba siendo hora. La patrona dice que, mientras no le quiten la clientela, le da igual.",
            Parrafo.GENTE to "En el taller Gustave dice que los trenes por debajo de tierra son cosa de topos, y Berthe, que a ella le da miedo meterse en un agujero. Monsieur Ferrand, en cambio, se frota las manos: obras, estaciones, inauguraciones, todo eso son carteles. Anselme dice que ya verán los nombres de las estaciones en letras de las que hace él.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en un tren corriendo a oscuras por debajo de su camino, con la gente sentada dentro, leyendo el periódico, mientras arriba los carros pasan sin saber nada. Le parece un cuento. Piensa que en París los cuentos acaban siendo verdad antes que en otras partes, y que eso es lo que tiene París.",
        ),
        "la_cigale" to unDia(
            Parrafo.GENTE to "Esta noche Berthe la lleva por primera vez a La Cigale, en Montmartre, una sala de café-concierto de unas mil plazas, hecha donde estuvo un baile, La Boule Noire. Suben a pie desde el faubourg, una hora larga, cogidas del brazo. Se sientan en lo más alto, desde donde el escenario se ve chiquito y se oye todo.",
            Parrafo.ANOCHECER to "En el escenario sale una cantante con un vestido de lentejuelas y un sombrero con plumas, y canta la canción de una costurera que se enamora de un estudiante. La sala entera canta el estribillo. Luego sale un cómico que imita a un diputado, y Berthe se ríe tanto que se le cae el sombrero a la fila de abajo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, bajando a pie por las calles oscuras de la colina, que nunca había visto tanta gente contenta junta. Piensa que la cantante, de cerca, debía de tener su edad o poco más. Piensa que todo era pintura: el decorado, la cara, las plumas. Y que daba igual, porque la canción era de verdad.",
        ),
        "acido" to unDia(
            Parrafo.TRABAJO to "Hoy ayuda a Anselme a preparar una piedra recién dibujada: se cubre con goma arábiga y un poco de ácido nítrico débil, que fija el dibujo y protege lo demás. Al pasarle el frasco, una gota le cae en el dorso de la mano. Al principio no siente nada. Luego, sí.",
            Parrafo.GENTE to "Anselme le mete la mano en el cubo de agua sin decir palabra y se la tiene ahí un buen rato, sujeta por la muñeca, como a una niña. Gustave le trae un trapo limpio. Berthe le dice que todos tienen una marca así, y le enseña la suya, una manchita blanca junto al pulgar. Monsieur Ferrand le dice que tenga más cuidado.",
            Parrafo.ANOCHECER to "Al caer la tarde vuelve a casa con la mano envuelta en el trapo y una mancha que ya se pone parda. Su madre se la unta con aceite y le hace un vendaje de costurera, apretado y perfecto. Su padre le dice que en su oficio todo el mundo tiene una cicatriz, y le enseña la del formón. {nombre} se siente ya del gremio.",
        ),
        "grand_palais_obras" to unDia(
            Parrafo.PUEBLO to "Hoy monsieur Ferrand la manda con un rollo de pruebas a un cliente de los Champs-Élysées, y por el camino ve las obras del Grand Palais: un bosque de andamios, montañas de piedra tallada, grúas, y por encima, el esqueleto de hierro de una nave enorme. Cientos de obreros se mueven allí como hormigas.",
            Parrafo.GENTE to "En la valla de las obras, un vigilante le cuenta con orgullo que el palacio es para la Exposición, y que lo hacen cuatro arquitectos a la vez, porque no hubo manera de decidir cuál era el mejor y les mandaron juntar sus proyectos. {nombre} se ríe. El vigilante también. Luego le dice que circule.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que un palacio de hierro y vidrio es como un invernadero para personas, y se pregunta qué plantas van a poner dentro. Piensa que a lo mejor ninguna, que las plantas serán los cuadros. Piensa que tardará en verlo por dentro. Lo piensa sin pena: hay cosas que se miran mejor desde fuera.",
        ),
        "calor" to unDia(
            Parrafo.TIEMPO to "Hoy hace un calor de horno desde temprano: el sol pega en los tejados de cinc, el aire de la calle no se mueve y los caballos de los carros van con la cabeza baja. Las mujeres del faubourg se abanican en los portales. El cielo, blanco de tanto azul, no tiene ni una nube.",
            Parrafo.TRABAJO to "Hoy en el taller hace tanto calor que la tinta se ablanda en las losas y se escurre del rodillo, y el papel se pega a los dedos. Gustave abre todas las ventanas y moja el suelo, y aun así los colores salen gordos. Monsieur Ferrand para la prensa una hora en lo peor de la tarde. Nadie protesta.",
            Parrafo.ANOCHECER to "Al caer la tarde París entero sale a la calle a buscar el fresco, que no llega. En el patio, los vecinos sacan sillas y colchones, y su padre se duerme un rato en el portal. {nombre} moja un pañuelo en la fuente y se lo pone en la nuca. El cielo se pone rosa y luego violeta, sin enfriarse.",
        ),
        "bicicletas" to unDia(
            Parrafo.TRABAJO to "Hoy el taller tira un cartel para una marca de bicicletas: una mujer de perfil, con el pelo al viento, sobre una bicicleta que vuela entre flores. Son seis piedras, seis colores, y el pliego pasa seis veces por la prensa. {nombre} cuelga las hojas a secar y ve cómo, color tras color, la mujer va apareciendo.",
            Parrafo.PUEBLO to "Por la tarde, cuando el cartel está seco, unos mozos de la marca se llevan los rollos en un carro. Mañana estarán pegados por medio París, en las vallas de las obras, en los muros, en los costados de los quioscos. {nombre} se imagina las calles llenas de la misma mujer, y le parece raro que cada una haya pasado por sus manos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que nunca ha montado en bicicleta, y que la mujer del cartel tampoco existe: Anselme la dibujó copiando a una sobrina suya que pasó una tarde por el taller. Piensa que los carteles venden lo que la gente quiere ser, más que lo que quiere tener. Se lo dice a Berthe. Berthe dice que ella quiere la bicicleta.",
        ),
        "puente" to unDia(
            Parrafo.PUEBLO to "Hoy, de vuelta de un recado a la otra orilla, {nombre} baja al muelle y ve el puente nuevo, el de Alejandro III, que va cruzando el Sena con sus andamios y sus barcas de obra debajo. Dicen que la primera piedra la puso el zar de Rusia. La gente se para en el muelle a mirarlo, como si fuera a moverse.",
            Parrafo.GENTE to "Junto a ella, un viejo con bastón le explica a su nieto que el puente es por la amistad con Rusia, y el nieto pregunta dónde está Rusia. El viejo señala hacia el río, sin mucha seguridad. {nombre} se aguanta la risa. En la otra orilla, un pintor ha puesto su caballete y pinta el puente a medio hacer.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que París está lleno de cosas a medio hacer, y que a lo mejor es más bonita así, con sus andamios, que cuando esté terminada. Piensa que eso no se lo puede decir a nadie, porque se reirían. Piensa en dibujarlo esta noche en el papel de envolver, antes de que lo terminen.",
        ),
        "obras_metro" to unDia(
            Parrafo.PUEBLO to "Hoy los periódicos dicen que han empezado las obras de la línea 1 del metro, y de camino {nombre} ve las primeras vallas, los adoquines levantados y unos hombres con picos que empiezan a cavar. Los carros tienen que dar la vuelta, los cocheros gritan, y los niños del barrio se pegan a las vallas para ver el agujero.",
            Parrafo.GENTE to "En el taller, Gustave dice que con tanto agujero París se va a hundir, y Anselme, que París está hueco desde hace siglos, lleno de canteras viejas, y que no se ha hundido nunca. Berthe dice que ella no piensa bajar ahí ni muerta. Monsieur Ferrand dice que ya bajará, como todos, cuando cueste poco.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que durante años va a ver crecer algo que no se ve, porque está debajo. Piensa que con los carteles pasa lo mismo: todo el mundo los mira, y nadie ve la piedra. Piensa que le gustaría ver el primer tren. No sabe cuándo será. Dicen que para la Exposición.",
        ),
        "letras_reves" to unDia(
            Parrafo.TRABAJO to "Hoy Anselme la llama a su mesa, le pone delante una piedra pequeña y un espejo de mano, y le enseña: lo que se dibuja en la piedra sale al revés en el papel, así que las letras se dibujan como se verían en el espejo. Le hace escribir su nombre así, de derecha a izquierda. Le sale torcido, y luego menos.",
            Parrafo.GENTE to "Anselme le cuenta que lleva cuarenta años dibujando letras, que empezó con letras de las de antes, rectas y serias, y que ahora le piden letras que se curvan como tallos y terminan en hojas, como si crecieran. Dice que al principio le parecían tonterías. Ahora le gustan, pero no lo dice delante de Gustave.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su nombre escrito al revés en la piedra, que parece de otro idioma, y en que al imprimirlo volverá a ser suyo. Piensa que en el taller todo es así: se piensa al revés para que salga derecho. Piensa que ya no va a poder mirar un cartel sin ver la piedra detrás.",
        ),
        "medee" to unDia(
            Parrafo.PUEBLO to "Hoy, de camino, ve que en las vallas han pegado un cartel nuevo de Mucha para Sarah Bernhardt, el de Médée, alto como una puerta. La gente se para a mirarlo, y una señora lo lee entero, letra por letra, en voz alta. Un caballero dice que de uno de esos carteles, años atrás, la actriz encargó miles.",
            Parrafo.GENTE to "En el taller, Anselme dice que la imprenta que trabaja para Mucha, la de Champenois, en el bulevar Saint-Michel, tiene trescientos obreros y veinte prensas de vapor. Gustave dice que con veinte prensas cualquiera. Monsieur Ferrand no dice nada, pero se queda un rato mirando el cartel, a la vuelta del almuerzo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en las mujeres de Mucha, con su pelo que se enrosca como un tallo y su marco de flores, y en lo lejos que están de las mujeres del faubourg. Piensa que a lo mejor por eso gustan tanto. Piensa en cuántas piedras haría falta para un cartel así, y en quién las habrá entintado.",
        ),
        "auteuil" to unDia(
            Parrafo.MANANA to "Hoy es día de visita: {nombre} cruza París de punta a punta, de la Bastille hasta Auteuil, para ver a su tío Jules, que es jardinero en los invernaderos de la ciudad. Sale temprano, con un paquete de bizcochos de su madre, y llega con los pies cansados y la cara colorada.",
            Parrafo.PUEBLO to "Los invernaderos del Fleuriste municipal son grandes naves de fundición y vidrio, con las costillas de hierro curvas como las de una ballena y la luz que entra por todas partes, blanca y húmeda. Dentro hace calor de verano aunque fuera llueva, y huele a tierra y a hojas que no son de aquí.",
            Parrafo.GENTE to "El tío Jules la lleva entre las palmeras y los helechos, le dice el nombre de cada planta en latín y en francés, y le deja tocar una hoja grande como un paraguas. Tiene las uñas negras de tierra y habla de las plantas como otros hablan de sus hijos. Al despedirse, le da un esqueje en un tiesto para su madre.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los invernaderos son como los carteles: hierro y vidrio que se curvan para parecer plantas, y plantas de verdad dentro, que parecen dibujadas. Piensa que en París todo quiere parecerse a una flor. Piensa que su tío se reiría si se lo dijera. Se lo dice. Se ríe.",
        ),
        "entintar" to unDia(
            Parrafo.TRABAJO to "Hoy monsieur Ferrand le dice que entinte ella la piedra del azul. {nombre} moja la piedra con la esponja, de un lado a otro, carga el rodillo de cuero en la losa y lo pasa sobre la piedra, despacio, una vez, otra, hasta que el dibujo brilla negro y el resto queda limpio. El agua y la grasa, como siempre, no se mezclan.",
            Parrafo.GENTE to "Gustave tira la prueba en la prensa, la saca, la pone a contraluz y no dice nada durante un rato largo. Luego dice que el azul está parejo. Berthe le aprieta la mano por debajo de la mesa. Anselme, desde su sitio, levanta la lupa como quien levanta una copa. Monsieur Ferrand ya está hablando de otra cosa.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hoy, por primera vez, algo de lo que lleva un cartel lo ha puesto ella. Piensa que nadie lo sabrá nunca: ni el que lo pegue, ni el que lo mire. Piensa que eso no le importa. Piensa que le importa un poco. Piensa que esta noche no se lo va a contar a nadie, y que se lo va a contar a todos.",
        ),
        "inventario" to unDia(
            Parrafo.TRABAJO to "Hoy no se tira nada: es el inventario de fin de año. Se sacan todas las piedras de los estantes, se cuentan, se numeran con tiza, y se apunta en un libro cuáles están dibujadas y cuáles listas para graniar. Hay piedras de carteles de hace diez años que nadie ha querido borrar. Monsieur Ferrand las acaricia.",
            Parrafo.GENTE to "Mientras cuentan, Gustave cuenta también las historias del taller: el cartel que salió con el rojo corrido, el cliente que pagó con un cuadro, la noche en que se inundó el sótano. Cada año las cuenta distinto, y todos lo corrigen. Al final, monsieur Ferrand saca una botella de vino y unos vasos.",
            Parrafo.ANOCHECER to "Al caer la tarde se cierran los libros, se barre el taller y se apagan las lámparas una por una. Afuera, la calle está oscura y fría, con olor a castañas. {nombre} vuelve a casa pensando en el año que viene, que empieza con piedras limpias, y en lo que habrá dibujado en ellas dentro de doce meses.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "litografa", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + EXPOSICION_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + EXPOSICION_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + EXPOSICION_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES_T1,
    )
}
