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
 * El cuento de Ochiyo, sirvienta de una casa de té en una posta del Tōkaidō, de 1830 a 1834. Sigue la gramática y el
 * tono de `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día es del camino: enciende el fuego y barre antes del alba, sirve té y prepara baños hasta la noche, y tiene
 *   una hora suya. Trabaja dos tramos y come dos veces: esos textos valen a cualquiera de sus horas.
 * - En la temporada 1, de mayo a agosto, el camino se llena de peregrinos hacia Ise (estado «okage»): cada hora tiene
 *   sus variantes, y también la mañana, el pueblo, el tiempo y el anochecer.
 * - Sus dos días libres del año (`yabuiri` y `yabuiri7`) tienen su propio párrafo de trabajo: ese día no trabaja.
 * - El pueblo es la posta y lee el arco «arroz» (lo que cuesta el arroz: sube con el hambre de Tenpō).
 * - No repite lo que ya dice «Su vida»: las 53 postas, que entró de niña, los dos días libres, que ve cientos de caras.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG5.kt`):
 * - Osen llega en `osen` (temporada 1), se escapa una noche en `osen_escapa` (3) y se casa en `osen_boda` (4).
 * - Sakichi, desde que lo conoce (`sakichi`, temporada 1); prometidos desde `pedida` (4); casados desde `boda` (5).
 * - Las fiestas que vuelven cada año no nombran a Osen ni a Sakichi.
 * - El 3 de febrero de 2027 es setsubun: su texto no cambia el párrafo del trabajo, que tiene que variar con la hora.
 */
internal object PiezasOchiyo {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta en el cuarto de las sirvientas, detrás de la cocina, sobre su colchón de algodón, entre las otras que todavía duermen. Está oscuro. Se oye el primer gallo de la posta y, a lo lejos, los cascabeles de un caballo de carga. Se levanta sin hacer ruido."),
        v("A {nombre} la despierta la señora Tsuru con dos palmadas en la puerta corrediza, como todas las mañanas, antes que el gallo. Se ata el pelo, se ata el delantal, se lava la cara con agua del pozo, tan fría que le duelen los dientes. Ya se oyen pasos en el camino."),
        v("{nombre} abre los postigos de la casa de té y entra la luz gris del amanecer, y con ella el olor del camino: tierra, estiércol de caballo, pino. Ya pasa gente: dos mensajeros a la carrera, un monje, un hombre con un mono atado. Ella los saluda a todos."),
        v("Lo primero que hace {nombre} cada mañana es mirar hacia el oeste, por encima de los tejados de la posta, a ver si se ve el Fuji. Hoy se ve, entero, blanco arriba y azul abajo, como pintado en el cielo. La señora Tsuru dice que da suerte verlo temprano."),
        v("{nombre} se despierta antes que nadie y se queda un momento quieta en la oscuridad, escuchando la casa: el crujido de las vigas, la respiración de las otras, un ratón en el techo, el agua del canal que pasa por detrás. Luego se levanta. El día de la posta es largo."),
        v("A {nombre} la despierta un viajero que llama a la puerta antes del alba porque quiere salir temprano y no encuentra sus sandalias. {nombre} las busca a oscuras entre cien pares en la entrada y las encuentra. El viajero le da las gracias con una reverencia y se va."),
        v("{nombre} se despierta con frío en los pies y la escarcha dibujada en el papel de la ventana. En la cocina, el agua de la tinaja tiene una capa de hielo que hay que romper con el cucharón. Se pone dos kimonos, uno encima del otro, y sale.", meses = setOf(12, 1, 2)),
    )

    /** La gran peregrinación a Ise, de mayo a agosto de la temporada 1 (estado «okage»). */
    private fun ok(texto: String, hacer: Hacer? = null) = v(texto, estado = "okage", hacer = hacer)

    private val OKAGE_MANANA = listOf(
        ok("{nombre} se despierta con el ruido de los peregrinos, que pasan por el camino desde antes del alba, cantando, en grupos de diez, de veinte, de cien. Llevan sombreros de paja y banderas de tela con el nombre de su pueblo. Van a Ise. Van todos a Ise."),
        ok("A {nombre} la despierta una canción: un grupo de peregrinas durmió en la puerta de la casa de té, en el suelo, porque las posadas están llenas, y se despiertan cantando. Son mujeres de una aldea del norte, con los hijos a la espalda. Le piden agua. Ella les da."),
        ok("{nombre} abre los postigos y el camino ya está lleno de gente, de lado a lado, caminando hacia el oeste: campesinos, niños, viejos, criados que dejaron a sus amos sin pedir permiso. Dicen que este año el dios de Ise los llama a todos. Ella se los queda mirando."),
    )

    private val OKAGE_TRABAJO = listOf(
        ok("A esta hora sirve té a los peregrinos, que no paran de llegar, en tazas que no da tiempo a lavar. Muchos no pueden pagar. La señora Tsuru les da el té igual: dice que este año dar a los peregrinos de Ise es dar al dios. Y que el dios paga después.", Hacer.TRABAJO),
        ok("A esta hora reparte bolas de arroz a los peregrinos en la puerta de la casa de té, de una cesta enorme, con el arroz que mandaron los ricos de la posta. Dar de comer a los que van a Ise trae suerte. Las manos le huelen a arroz todo el día.", Hacer.TRABAJO),
        ok("A esta hora lava los pies a los peregrinos que llegan rendidos, con agua tibia en una tina de madera, y les cambia las sandalias de paja rotas por otras nuevas que da la casa. Algunos lloran de cansancio. Una vieja le dice que rezará por ella en Ise.", Hacer.TRABAJO),
        ok("A esta hora sirve en la casa de té, llena hasta la puerta, con peregrinos sentados en el suelo de la entrada, en el banco de fuera, en la calle. Se habla en dialectos que {nombre} no entiende. Se canta. Se baila. Nunca había visto la posta así.", Hacer.TRABAJO),
        ok("A esta hora come de pie en la cocina un cuenco de arroz con un encurtido, en lo que tarda en hervir el agua de la tetera, porque con los peregrinos no hay tiempo de sentarse. La señora Tsuru come igual, de pie, a su lado. Ninguna de las dos se queja.", Hacer.COMER),
        ok("A esta hora come lo que sobró en los cuencos de los peregrinos, todo mezclado, y no sabe mal. Osen come a su lado sin levantar la vista. Por la ventana pasan más y más, sin parar, como un río que nunca se vacía.", Hacer.COMER),
        ok("A esta hora barre la entrada de la casa de té, que amanece llena de paja de sandalias rotas, cáscaras, papeles y algún peregrino dormido. Los despierta con cuidado. Enciende el fuego. El agua de la tetera grande tarda en hervir, y ya hay gente esperando.", Hacer.CUIDAR),
        ok("A esta hora enciende el fuego de la cocina y llena todas las teteras de la casa, que este año no bastan, y pide prestadas dos más a la casa de al lado. Fuera ya se oyen las canciones. Hoy también serán miles.", Hacer.CUIDAR),
        ok("A esta hora duerme en el cuarto de las sirvientas, rendida, mientras por el camino siguen pasando peregrinos con faroles, cantando bajito. Ni el ruido la despierta. Sueña que ella también va a Ise, sin permiso, y que nadie la castiga.", Hacer.DORMIR),
        ok("A esta hora duerme a ratos, porque los peregrinos que duermen en la entrada hablan, roncan y se levantan a orinar. Una niña llora. Su madre le canta. {nombre} se queda escuchando la canción, que no conoce, y se duerme con ella.", Hacer.DORMIR),
        ok("A esta hora, la única suya del día, se sienta en el banco de fuera a mirar pasar a los peregrinos con sus faroles, en la oscuridad del camino, como un río de luces que va hacia el oeste. Osen se sienta a su lado. Ninguna de las dos dice nada.", Hacer.LIBRE),
        ok("A esta hora, en su rato libre, trenza sandalias de paja para los peregrinos de mañana, con unas manos que ya lo hacen solas. La señora Tsuru dice que este año la casa ha dado más sandalias que nunca. {nombre} ha perdido la cuenta.", Hacer.LIBRE),
    )

    private val OKAGE_PUEBLO = listOf(
        ok("La posta está llena de peregrinos del amanecer a la noche: en las posadas, en las casas de té, en los templos, en la calle. Duermen en los porches, bajo los aleros, en los campos. Los dueños de las posadas dan de comer gratis a muchos. Es un año raro."),
        ok("Por el camino real pasan peregrinos de todas las provincias, a pie, con sus banderas de tela, y a veces bailando. Pasan niños solos, mujeres solas, criados que dejaron el trabajo a medias. Nadie los para: van a Ise, y a los que van a Ise no se les para."),
        ok("En la entrada de la posta los ricos del lugar pusieron mesas con arroz, té y sandalias para los peregrinos, y carteles que dicen que es gratis. Los peregrinos dan las gracias con una reverencia y siguen. El camino nunca había estado tan lleno."),
    )

    private val OKAGE_ANOCHECER = listOf(
        ok("Anochece y los peregrinos siguen pasando con faroles de papel, cantando, una fila de luces que se pierde hacia el oeste, hacia Ise. {nombre} cierra los postigos de la casa de té, pero sigue oyéndolos. Algunos llevan semanas caminando."),
        ok("Al caer la tarde, en la plaza de la posta, unos peregrinos se ponen a bailar con abanicos, como locos, y la gente de la posta baila con ellos. {nombre} mira desde la puerta. La señora Tsuru le da un empujón: anda, baila. Baila un rato."),
        ok("De noche hay en el camino más gente que de día: los peregrinos que no encontraron sitio duermen en la cuneta, envueltos en sus esteras, uno al lado del otro, hasta donde se ve. {nombre} les lleva agua hasta que la señora Tsuru la llama."),
    )

    private val OKAGE_TIEMPO = listOf(
        ok("{cielo} {de_estacion} Los peregrinos caminan igual con sol o con lluvia: dicen que el dios de Ise los cuida."),
        ok("Desde la puerta de la casa de té se ve el camino lleno de sombreros de paja hasta la curva. {cielo} {de_estacion}"),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la posta el tiempo se mide por los viajeros: con buen tiempo, muchos; con lluvia, pocos y mojados."),
        v("{nombre} mira el cielo desde la puerta de la casa de té, con la escoba en la mano. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} La señora Tsuru dice que el Fuji avisa del tiempo: si se pone el sombrero de nubes, llueve."),
        v("{cielo} En {estacion} el camino cambia de color y de olor, y {nombre} lo nota en las sandalias de los viajeros: barro, polvo, nieve o pétalos."),
        v("Por encima de los tejados de la posta se ve el Fuji, o se ve el sitio donde está. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los porteadores del camino dicen que el tiempo de la montaña cambia más rápido que el humor de un samurái."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio y el Fuji se ve entero, con su nieve, tan cerca que parece que se podría tocar.",
            "Hace un día claro, de esos en que el camino se ve hasta la otra posta.",
            "No hay una nube, y el sol calienta el banco de la entrada, donde se sientan los viajeros.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto y el Fuji se esconde detrás de las nubes, como si no estuviera.",
            "Hay nubes bajas que bajan de la montaña y se enredan en los pinos del camino.",
            "Está nublado, con una luz blanca que no hace sombra.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y los viajeros llegan con sus capas de paja chorreando y el barro hasta las rodillas.",
            "Cae una lluvia fina que hace brillar las piedras del camino y los tejados.",
            "Llueve, y en la casa de té se oye el agua en el tejado y los pasos mojados del camino.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos bajan de la montaña y los caballos de carga relinchan en las cuadras.",
            "El cielo se pone negro y cae un aguacero que convierte el camino en un río de barro.",
        ),
        Clima.NIEVE to listOf(
            "Nieva, y el camino se queda blanco y callado, y los viajeros dejan huellas negras.",
            "Cae nieve sobre los tejados de la posta, y el Fuji y el cielo son del mismo blanco.",
        ),
        Clima.VIENTO to listOf(
            "Sopla un viento frío de la montaña que hace temblar el papel de las ventanas.",
            "Hay viento, y los sombreros de los viajeros salen volando por el camino, y ellos detrás.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla, y los viajeros aparecen de golpe en el camino, como fantasmas, con sus faroles.",
            "La niebla tapa la posta entera, y solo se oyen los cascabeles de los caballos que pasan.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y los cerezos de la posta florecen y se deshojan en una semana.",
            "En primavera el camino se llena de viajeros que salieron con el buen tiempo.",
            "Es el tiempo de las lluvias suaves y de las ranas en los arrozales.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, el tiempo del calor húmedo, de las cigarras y de las lluvias largas.",
            "En verano los viajeros caminan temprano y descansan a la sombra de la casa de té.",
            "Es el tiempo de los mosquitos, de los abanicos y del arroz verde.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando los arces del monte se ponen rojos y se corta el arroz.",
            "En otoño el aire está limpio y el Fuji se ve más que nunca.",
            "Es el tiempo de los caquis, de las castañas y de la luna grande.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando el agua del pozo humea y los viajeros piden té caliente sin parar.",
            "En invierno el Fuji está blanco hasta abajo y el viento de la montaña corta la cara.",
            "Es el tiempo de los braseros, de la escarcha y de los días cortos.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora sirve té a los viajeros sentados en el banco de la entrada: té verde en tazas de barro, y bolitas de arroz asadas con salsa dulce en un palito, que son la especialidad de la casa. Los viajeros comen deprisa, pagan con monedas de cobre y siguen camino.", hacer = Hacer.TRABAJO),
        v("A esta hora llama a los viajeros desde la puerta de la casa de té, como todas las sirvientas de la posta, cada una desde la suya: pasen, descansen, hay té caliente, hay baño. Hay que tener buena voz y buena cara. {nombre} tiene las dos, dice la señora Tsuru.", hacer = Hacer.TRABAJO),
        v("A esta hora friega la tina grande del baño y la llena, cubo a cubo, para los viajeros que se queden, y cuida el fuego de debajo hasta que el agua está tan caliente que pica. El primero en bañarse es siempre el huésped de más rango; la última, ella.", hacer = Hacer.TRABAJO),
        v("A esta hora lava tazas, cuencos y teteras en el patio de atrás, en una tina de agua fría, cientos al día. Se le arrugan los dedos. La vieja Kane le pasa las sucias y ella le devuelve las limpias, sin hablar, como dos manos de la misma persona.", hacer = Hacer.TRABAJO),
        v("A esta hora arregla los cuartos de arriba: barre los tatamis, dobla los colchones, pone el brasero o el mosquitero según el tiempo, cambia el agua de las flores. Un mercader de Osaka que no se decide a irse le pide que le cante algo. Ella le sirve más té.", hacer = Hacer.TRABAJO),
        v("A esta hora sirve arroz, sopa y pescado asado en bandejas a los viajeros que piden de comer, de rodillas, en los cuartos de tatami. Los samuráis comen en silencio; los mercaderes, hablando de precios; los peregrinos, rezando antes. {nombre} los conoce por cómo comen.", hacer = Hacer.TRABAJO),

        v("A esta hora come en la cocina, de rodillas junto al fogón, un cuenco de arroz con sopa de miso y un trozo de rábano encurtido. Es lo mismo que comió ayer y lo que comerá mañana. Le gusta igual. La vieja Kane le pone más arroz cuando nadie mira.", hacer = Hacer.COMER),
        v("A esta hora come deprisa, con los palillos, lo que quedó en la olla: arroz, unas algas, un poco de pescado seco. Los viajeros no esperan. Cuando la señora Tsuru la llama, deja el cuenco a medias y va. Se lo terminará frío.", hacer = Hacer.COMER),
        v("A esta hora come con las otras sirvientas, sentadas en fila en el suelo de la cocina, cada una con su cuenco. Hablan de los viajeros: del samurái que no dio las gracias, del mercader que dio de más, del correo guapo. Se ríen bajito para que no las oigan.", hacer = Hacer.COMER),

        v("A esta hora, antes del alba, enciende el fuego de la cocina con unas brasas que guardó tapadas desde la noche, sopla, añade ramitas de pino, y cuando prende pone el agua de la tetera grande. En la casa de té el fuego no se apaga nunca del todo.", hacer = Hacer.CUIDAR),
        v("A esta hora barre la entrada de la casa de té y el trozo de camino de delante, que es de la casa, y lo riega con agua para que no levante polvo. Todas las sirvientas de la posta hacen lo mismo a la vez, cada una delante de la suya. El camino amanece limpio y mojado.", hacer = Hacer.CUIDAR),
        v("A esta hora saca agua del pozo, cubo a cubo, y llena las tinajas de la cocina, las del baño y las del té. El pozo es hondo y la cuerda pesa. Cuando termina tiene los brazos dormidos y ya es de día. Se oye al primer viajero.", hacer = Hacer.CUIDAR),

        v("A esta hora duerme en el cuarto de las sirvientas, en su colchón fino, tapada hasta la nariz, entre las otras. El cuarto es pequeño y no tiene ventana. Por la pared se oye a los huéspedes roncar, toser y, a veces, hablar en sueños de dinero.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, cansada de todo el día de pie, y sueña con la aldea de su madre: el arroyo, el arrozal en escalones, el olor del humo de la casa. Se despierta un momento con un ruido del camino, se acuerda de dónde está y se vuelve a dormir.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo. Fuera, el camino real descansa, pero no del todo: pasa un correo corriendo con su farol, pasa un caballo, ladra un perro. El sereno de la posta golpea sus dos tablillas de madera para avisar del fuego. {nombre} no lo oye.", hacer = Hacer.DORMIR),

        v("A esta hora, la única suya del día, se sienta en el borde de la cocina con los pies colgando y se arregla el pelo. Las otras sirvientas hablan bajito. {nombre} se mira en el agua de un cuenco. Tiene la cara cansada y los ojos contentos.", hacer = Hacer.LIBRE),
        v("A esta hora, en su rato libre, se lava en la tina del baño cuando ya se bañaron todos, en el agua tibia que queda, que es lo único caliente que el día tiene para ella. Es el mejor momento. Cierra los ojos y no piensa en nada.", hacer = Hacer.LIBRE),
        v("A esta hora, libre, sale un momento a la puerta de la casa de té y mira el camino a oscuras, por donde mañana pasarán otros cientos de personas que no volverá a ver. Arriba, las estrellas. Al oeste, la mancha negra del Fuji. Luego entra y cierra.", hacer = Hacer.LIBRE),
    )

    private val PUEBLO = listOf(
        v("{entorno} La posta es una calle larga junto al camino real, con posadas, casas de té, cuadras, un templo y la casa del jefe de la posta, donde se cambian los caballos y los porteadores. Por ella pasa todo el que va de Edo a Kioto, o de Kioto a Edo."),
        v("La casa de té de la señora Tsuru está a la entrada de la posta, con un banco largo fuera, cortinas de tela azul en la puerta y un farol con el nombre de la casa. Dentro, el suelo de tierra, los tatamis, la cocina al fondo y, arriba, los cuartos para dormir. {entorno}"),
        v("{entorno} Por el camino real pasa gente de todas clases: mensajeros a la carrera, mercaderes con sus fardos, monjes, peregrinos, actores, mendigos, samuráis con sus dos espadas y, a veces, la procesión de un daimio, que ocupa el camino entero durante horas."),
        v("En la posta todo el mundo vive del camino: las posadas, las casas de té, los herreros que hierran los caballos, los que venden sandalias de paja, los porteadores y los que cruzan el río con los viajeros a hombros. {entorno}"),
        v("{entorno} Detrás de la posta empiezan los arrozales, en escalones hasta el monte, y detrás del monte, el Fuji, que se ve desde casi todas partes. Los viajeros se paran en la entrada de la casa de té para mirarlo. La señora Tsuru dice que por eso puso la casa ahí."),
        v("En medio de la posta hay un tablón donde se cuelgan las órdenes del gobierno, escritas en letras grandes: los precios de los caballos, lo que está prohibido, las recompensas por los ladrones. Casi nadie las lee. Todos saben lo que dicen. {entorno}"),
    )

    /** Lo que cuesta el arroz (arco «arroz»): sube con el hambre de Tenpō. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("arroz", 0.0, 0.4, frases = listOf(
            "El arroz está a su precio de siempre, y en la casa de té los cuencos se sirven llenos.",
            "Hay arroz, y los viajeros comen y beben sin pensar en el precio.",
        )),
        Entorno("arroz", 0.4, 0.7, frases = listOf(
            "El arroz está más caro que otros años, y la señora Tsuru sirve los cuencos un poco menos llenos.",
            "Se dice que en el norte la cosecha fue mala, y el arroz de la posta ya se nota más caro.",
        )),
        Entorno("arroz", 0.7, 1.0, frases = listOf(
            "El arroz cuesta el doble que antes, y en la casa de té se sirve mezclado con mijo y con cebada.",
            "Por el camino pasan pobres del norte pidiendo arroz, y en la posta ya no se da como antes.",
        )),
    )

    private val GENTE = listOf(
        v("La señora Tsuru, la dueña de la casa de té, es viuda, pequeña y dura como un nudo de pino. Lleva la casa desde que murió su marido, regatea con los mercaderes de arroz y no deja que nadie le falte el respeto a una de sus sirvientas. A {nombre} la trata como a una sobrina."),
        v("La vieja Kane, la cocinera, lleva cuarenta años en la casa y sabe lo que va a pedir cada viajero antes de que abra la boca. Tiene las manos quemadas de tantos años de fogón y la lengua afilada. A {nombre} le enseñó todo lo que sabe, a gritos."),
        v("Gonta es un porteador del camino, grande como un buey y con la espalda llena de tatuajes, que cruza el río con los viajeros a hombros y lleva fardos por la montaña. Es bruto y bueno. Le trae a {nombre} castañas del monte en otoño y nunca dice por qué."),
        v("Su madre vive en una aldea del monte, a medio día de camino, con su hermano pequeño, en una casa con techo de paja y un arrozal que no es suyo. {nombre} le manda casi todo lo que gana. Su madre le manda arroz de su arrozal, que es poco, con algún arriero que baja."),
        v("Su hermano pequeño, Tarō, quiere ser porteador como Gonta, o correo, o cualquier cosa que lo saque de la aldea. Cuando {nombre} va a verlos, le pide que le cuente del camino: quién pasó, qué llevaba, adónde iba. Ella le cuenta, y le inventa un poco."),
        v("En la casa de té trabajan cuatro sirvientas, que duermen juntas, comen juntas y se pelean por atender a los viajeros que dan propina. {nombre} es la mayor después de la vieja Kane, y la que manda cuando la señora Tsuru no está. No le gusta mandar. Lo hace bien."),
        v("Osen, la sirvienta nueva, tiene la cara redonda y un miedo enorme a todo: a los samuráis, a los perros, a la señora Tsuru. Duerme al lado de {nombre} y por las noches le pregunta cosas. {nombre} le contesta como le contestaban a ella.", desde = "osen", hasta = "osen_escapa"),
        v("Osen ya no es la niña asustada que llegó: mira a los mozos del camino y los mozos la miran. Desde la noche en que se escapó, la señora Tsuru la vigila. {nombre} también, aunque disimula.", desde = "osen_escapa", hasta = "osen_boda"),
        v("Osen se casó con el hijo del herrero de la posta y vive en la herrería, al otro lado de la calle. Viene a la casa de té a ayudar cuando hay muchos viajeros, con su cara redonda de siempre, y se ríe de todo.", desde = "osen_boda"),
        v("Sakichi es un correo que corre el camino con una caja de cartas colgada de una vara y una campanilla, de posta en posta. Cuando pasa por la de {nombre} se para a beber un té, aunque los correos no se paran. Ella se lo sirve más caliente que a nadie.", desde = "sakichi", hasta = "pedida"),
        v("Sakichi y {nombre} están prometidos: se casarán cuando termine su contrato con la señora Tsuru. Él pasa por la posta cada pocos días, corriendo, y cada vez le deja algo en el banco de la entrada: una naranja, un papel doblado, una flor del camino.", desde = "pedida", hasta = "boda"),
        v("Sakichi, su marido, sigue corriendo el camino, y vuelve a la posta cada pocos días a dormir en el cuarto que la señora Tsuru les dio detrás de la cocina. {nombre} reconoce su campanilla desde lejos, antes de verlo, y sale a la puerta.", desde = "boda"),
    )

    private val OBJETO = listOf(
        v("Hoy, entre viajero y viajero, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de la vieja Kane, que lo aprendió de otra vieja, en esta misma cocina."),
        v("En una casa de té cada cosa tiene su manera, y una sirvienta que no la sabe pierde el puesto. {dato_objeto}"),
        v("{dato_objeto} Los viajeros no se fijan en esas cosas, y está bien: es para que no tengan que fijarse."),
        v("{dato_objeto} Son cosas que pasan por sus manos cien veces al día, sin que las mire."),
        v("Una sirvienta tiene muy poco suyo: un kimono para las fiestas, un peine, unas sandalias. Lo demás es de la casa. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("tetera", "la tetera", "una tetera", "La tetera",
            "La tetera grande de hierro cuelga sobre el fuego de la cocina de la mañana a la noche, siempre caliente; el té verde no se hace con agua hirviendo, sino con agua que ya hirvió y se dejó reposar un momento, para que no amargue; es lo primero que aprende una sirvienta."),
        ObjetoCuento("taza", "la taza", "una taza", "La taza",
            "Las tazas de la casa de té son de barro, sin asa, de las que caben en una mano; se ofrecen con las dos manos y se reciben con las dos; las rajadas se guardan para los porteadores, que no se quejan."),
        ObjetoCuento("farol", "el farol", "un farol", "El farol",
            "El farol de la puerta es de papel encerado sobre un armazón de bambú, con el nombre de la casa pintado en negro; se enciende al caer la tarde, y los viajeros que llegan de noche lo buscan de lejos, como los barcos buscan un faro."),
        ObjetoCuento("sombrero", "el sombrero", "un sombrero de viaje", "El sombrero",
            "El sombrero de los viajeros es de paja o de junco, ancho y plano, atado bajo la barbilla; protege del sol, de la lluvia y de las miradas, y se deja colgado en la entrada de la casa de té, cien sombreros iguales que solo su dueño reconoce."),
        ObjetoCuento("sandalias", "las sandalias", "unas sandalias de paja", "Las sandalias",
            "Las sandalias de paja se gastan en pocos días de camino, y por eso se venden en todas las postas, colgadas en ristras de los aleros; cuestan muy poco; los viajeros dejan las viejas en la cuneta, y a la salida de cada posta hay montones."),
        ObjetoCuento("abanico", "el abanico", "un abanico", "El abanico",
            "El abanico plegable es de papel y varillas de bambú; en verano no para, y una sirvienta lo usa para avivar el fuego, para espantar las moscas de los dulces y para taparse la boca cuando se ríe delante de un samurái."),
        ObjetoCuento("dango", "los dangos", "un palito de dangos", "Los dangos",
            "Los dangos son bolitas de harina de arroz, cocidas y ensartadas en un palito de bambú, de tres en tres o de cuatro en cuatro, que se asan y se untan de salsa dulce; son lo que más se vende en la casa de té, y los viajeros los comen sin pararse."),
        ObjetoCuento("ryo", "las monedas", "unas monedas", "Las monedas",
            "Las monedas del camino son casi todas de cobre, redondas, con un agujero cuadrado en el centro para ensartarlas en un cordel; las de plata y oro las llevan los mercaderes ricos y los samuráis; una sirvienta casi nunca toca una de oro."),
        ObjetoCuento("palanquin", "el palanquín", "un palanquín", "El palanquín",
            "El palanquín es una caja colgada de una vara que llevan dos porteadores al hombro, corriendo con un paso corto y un grito para ir juntos; los de los señores son lacados y con cortinas; los del camino, una cesta de bambú donde el viajero va doblado."),
        ObjetoCuento("pipa", "la pipa", "una pipa", "La pipa",
            "La pipa de los viajeros es larga y fina, de bambú con la cazoleta de metal, tan pequeña que solo caben tres chupadas de tabaco; se fuma, se golpea contra el brasero para vaciarla y se vuelve a llenar; en la casa de té siempre hay un brasero para las pipas."),
        ObjetoCuento("peine", "el peine", "un peine", "El peine",
            "El peine de boj es de madera amarilla y dura, de dientes finos, y se usa con aceite de camelia para que el pelo brille y se quede en su sitio; una mujer cuida su peine como un samurái su espada, y algunos pasan de madre a hija."),
        ObjetoCuento("furoshiki", "el pañuelo de envolver", "un pañuelo de envolver", "El pañuelo",
            "El pañuelo de envolver es un cuadrado grande de tela teñida que sirve para llevar cualquier cosa: ropa, regalos, comida, libros; se atan las esquinas y se lleva en la mano o a la espalda; los viajeros llevan todo lo suyo envuelto en uno."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} lo oye de los viajeros, como lo oye todo, y lo guarda. A sus {edad} años sabe más del mundo que su madre, sin haber salido nunca de la posta: lo que se sabe en Edo llega a la casa de té tres días después, con los correos."),
        v("Piensa a veces en los viajeros que van a Kioto, a Edo, a Osaka, a lugares que ella solo conoce por el nombre. Le gustaría ir a uno, una vez, aunque fuera a pie. Una mujer no viaja sin permiso. Pero puede pensarlo, y lo piensa."),
        v("{mundo} {nombre} piensa en su contrato con la señora Tsuru, que termina dentro de unos años, y en lo que hará después. Su madre quiere que vuelva a la aldea a casarse. Ella no quiere volver a la aldea. No sabe todavía qué quiere. Quiere el camino.", hasta = "pedida"),
        v("{mundo} A veces, de noche, {nombre} piensa en la cantidad de gente que ha visto pasar por la puerta de la casa de té: miles, decenas de miles. Ninguno se acordará de ella. Ella se acuerda de muchos. Le parece injusto, y luego le parece bien."),
        v("Piensa en el Fuji, que está siempre ahí, cambiando de color con las horas y con las estaciones, mientras los viajeros pasan y se van. Le gustaría ser como el Fuji. Luego piensa que el Fuji está solo. Prefiere ser ella."),
        v("{mundo} A sus {edad} años, {nombre} ha servido té a samuráis, a monjes, a actores, a ladrones y a un hombre que decía ser un dios. Todos se sientan igual en el banco y todos soplan el té antes de beber. Es lo más cierto que ha aprendido."),
        v("Piensa en lo que le manda a su madre cada mes, contado en monedas de cobre ensartadas en un cordel, y en lo que guarda para ella, que es casi nada. Algún día tendrá su propia casa de té, piensa. Pequeña, con un banco fuera y un farol con su nombre."),
    )

    private val MUNDOS = mapOf(
        "okage" to listOf(
            "Se habla de Ise, del gran santuario, y de que este es un año de peregrinos.",
            "Se habla del shogun, que vive en Edo en un castillo que nadie de la posta ha visto.",
            "Se dice que en Edo un pintor viejo hace estampas del Fuji que se venden como el arroz.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde {nombre} enciende el farol de la puerta, y en toda la posta se encienden a la vez los de las otras casas, una fila de luces a lo largo del camino. Los viajeros que llegan tarde aprietan el paso. Las sirvientas los llaman desde las puertas."),
        v("Anochece, y el Fuji se pone rosa, luego morado, luego gris, y desaparece en la oscuridad cuando todavía hay luz en el camino. {nombre} lo mira desaparecer desde la cocina, con las manos en el agua de fregar. Mañana volverá a estar."),
        v("Al caer la tarde llegan a la posta los últimos viajeros, cansados, con los pies hinchados, y se pelean por los cuartos de las posadas. La casa de té se llena de gente que no quiere seguir. {nombre} les trae agua para los pies antes que el té."),
        v("De noche la posta se queda en silencio, salvo los faroles y algún perro, y el camino real, sin gente, parece otro: más ancho, más largo, más oscuro. {nombre} cierra los postigos de la casa de té, uno por uno, y echa la tranca."),
        v("Anochece, y de las posadas de la posta salen ruidos de fiesta: el shamisen de una cantante, risas de mercaderes que cerraron un trato, un viajero borracho que canta. En la casa de la señora Tsuru no hay cantantes. Hay té, y silencio, y se duerme bien."),
        v("La última luz del día se queda en lo alto del Fuji, en la nieve, cuando la posta ya está a oscuras. {nombre} la ve desde la puerta, con el farol en la mano. Un viajero que pasa se para a mirarla también. Ninguno de los dos dice nada. Luego él sigue."),
        v("Al anochecer, en verano, las luciérnagas salen de los arrozales de detrás de la posta y vuelan sobre el camino, cientos de lucecitas verdes. Los niños de la posta las cazan con abanicos. {nombre} cogió una de niña y la tuvo toda la noche en la mano.", meses = setOf(6, 7, 8)),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "susuharai" to unDia(
            Parrafo.MANANA to "Hoy es el día de la gran limpieza del fin de año, el mismo en toda la posta: se saca el hollín de un año entero de las vigas, de los rincones, de detrás de los armarios. La señora Tsuru reparte pañuelos para la cabeza y cañas de bambú con hojas en la punta.",
            Parrafo.TRABAJO to "A esta hora barre el hollín del techo con una caña larga, subida a una escalera, con un pañuelo en la boca, mientras le cae encima una lluvia negra. Gonta, que vino a ayudar, sujeta la escalera y se ríe de su cara. Ella le echa encima un puñado de polvo.",
            Parrafo.GENTE to "Cuando terminan, como manda la costumbre, la señora Tsuru y las sirvientas mantean a Gonta, que se deja, gritando, y luego a la vieja Kane, que no se deja. Después se toma sopa de judías dulces, todos juntos en la cocina, negros de hollín hasta las orejas.",
        ),
        "shogatsu" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy es el primer día del año. En la puerta de la casa de té hay dos pinos con cañas de bambú atadas con cuerda de paja, y en el altar, dos tortas de arroz redondas, una encima de la otra, con una naranja amarga. {nombre} se puso su kimono de fiesta, el único, el de rayas azules.",
                Parrafo.GENTE to "La señora Tsuru le da a cada sirvienta un sobre con unas monedas y una tela nueva de algodón para el año, como se hace, y les sirve sake dulce con especias en un cuenco de laca. {nombre} bebe un sorbo y se le suben los colores. Todas se ríen de ella.",
                Parrafo.PENSAMIENTO to "{nombre} piensa que hoy todo el mundo cumple un año a la vez: ella, la señora Tsuru, los viajeros, el emperador. Es lo bueno del año nuevo. Piensa en su madre y en su hermano, que hoy comen tortas de arroz en la aldea sin ella. Pronto tendrá su día libre.",
            )),
            sigue = mapOf(Parrafo.TRABAJO to listOf(
                "A esta hora sirve té con tortas de arroz tostadas a los viajeros del año nuevo, que no paran: unos van a ver a su familia, otros a los templos, otros siguen su camino como si no fuera fiesta. Todos dicen lo mismo al entrar: feliz año nuevo.",
                "A esta hora sirve la sopa del año nuevo, con tortas de arroz blandas, verduras y un poco de pescado, a los viajeros que se quedan. Las tortas se pegan a los dientes. La vieja Kane dice que cada año algún viejo se atraganta con ellas. Este año, todavía no.",
            )),
        ),
        "setsubun" to unDia(
            Parrafo.MANANA to "Hoy es setsubun, la víspera de la primavera, y en la puerta de la casa de té hay una ramita de acebo con una cabeza de sardina clavada, para que no entren los demonios, que no soportan el olor. La señora Tsuru la puso al amanecer. Huele fatal.",
            Parrafo.GENTE to "Esta noche la señora Tsuru tira habas tostadas por la casa, cuarto por cuarto, gritando: fuera los demonios, dentro la suerte. Las sirvientas la siguen y cierran las puertas detrás, para que la suerte no se escape. Luego cada una come tantas habas como años tiene, y una más.",
            Parrafo.PENSAMIENTO to "{nombre} cuenta sus habas, una por una, y piensa que cada año son más. La vieja Kane se come las suyas a puñados y no las cuenta. Dice que a su edad ya no hace falta. {nombre} piensa que a lo mejor la suerte es eso: no contar.",
        ),
        "yabuiri" to unDia(
            Parrafo.MANANA to "Hoy es su día libre, uno de los dos que tiene al año. {nombre} sale de la posta antes del alba, con su kimono de fiesta, un pañuelo de envolver lleno de regalos para su madre y unas sandalias nuevas, y sube por el sendero del monte hacia su aldea, a medio día de camino.",
            Parrafo.TRABAJO to "A esta hora no trabaja: camina, o está con su madre, o duerme en la casa de techo de paja donde nació, con el frío del monte y el humo del hogar. Ayuda a su madre en lo que puede, porque no sabe estar quieta, y su madre la riñe por eso.",
            Parrafo.GENTE to "Su madre la mira de arriba abajo, le toca la cara, le dice que está flaca, y luego le sirve el mejor arroz que tiene. Tarō le pregunta por los viajeros, por los samuráis, por los correos. {nombre} le cuenta todo lo que vio y un poco más. Él escucha con la boca abierta.",
        ),
        "hina" to unDia(
            Parrafo.MANANA to "Hoy es el día de las niñas, y la señora Tsuru sacó del arcón las muñecas de su hija, que murió pequeña: el emperador y la emperatriz, las damas de la corte, los músicos, en escalones cubiertos de tela roja. Las pone cada año en el cuarto del fondo, con flores de melocotonero.",
            Parrafo.GENTE to "Las sirvientas, que ya no son niñas, se sientan un rato delante de las muñecas con sake blanco y dulces de arroz de colores, como si lo fueran. La señora Tsuru las mira sin decir nada. La vieja Kane dice que en su aldea las muñecas eran de papel y se echaban al río.",
            Parrafo.PENSAMIENTO to "{nombre} mira las muñecas de la hija de la señora Tsuru, tan finas, con sus kimonos de seda, y piensa en la niña que jugó con ellas y que no llegó a crecer. Piensa que la señora Tsuru las saca por ella, no por las sirvientas. Le pone una flor más.",
        ),
        "daimio_ida" to unDia(
            Parrafo.MANANA to "Hoy pasó por la posta la procesión de un daimio que va a Edo a pasar su año junto al shogun, como manda la ley: cientos de hombres en fila, con lanzas, cajas lacadas, caballos, banderas, y en medio el palanquín del señor, cerrado. Duró la mañana entera.",
            Parrafo.PUEBLO to "Al paso de la procesión, la posta entera se arrodilla a los lados del camino, con la frente en el suelo: los viajeros, los mercaderes, las sirvientas, los niños. No se puede mirar al señor. Los heraldos van delante gritando que se aparten. La calle se queda en silencio, salvo los pasos.",
            Parrafo.PENSAMIENTO to "{nombre} mira de reojo, con la frente casi en el suelo, los pies de los porteadores del palanquín: las sandalias iguales, el paso igual. Piensa que el señor va ahí dentro, a oscuras, sin ver el Fuji ni a nadie. Piensa que ella, de rodillas en el barro, ve más que él.",
        ),
        "tango" to unDia(
            Parrafo.MANANA to "Hoy es el día de los niños, y en las casas de la posta donde hay hijos varones ondean en lo alto de unas varas carpas de tela y de papel, una por cada hijo, que se llenan de viento y parecen nadar en el cielo. La casa de té no tiene hijos. Tiene una carpa pequeña, que puso la vieja Kane.",
            Parrafo.TRABAJO to "A esta hora prepara el baño de los viajeros con hojas de lirio en el agua caliente, como se hace este día, porque dicen que alejan los males y dan fuerza. El baño huele a hierba. Los viajeros salen contentos, oliendo a jardín, y piden más té.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Tarō, que hoy en la aldea tendrá una carpa de papel en una caña, si su madre tuvo tiempo de hacérsela. La carpa sube el río contra la corriente, dicen, y por eso es la de los niños. Piensa que su hermano tendrá que subir mucho río.",
        ),
        "tanabata" to unDia(
            Parrafo.MANANA to "Hoy es Tanabata, la noche en que la tejedora y el pastor, que son dos estrellas separadas por el río del cielo, se encuentran una vez al año. En la puerta de la casa de té hay una rama de bambú llena de tiras de papel de colores con deseos escritos, que se mueven con el aire.",
            Parrafo.GENTE to "Las sirvientas cuelgan sus deseos en el bambú de la casa: la vieja Kane, que no le duelan las manos; las jóvenes, un marido. {nombre} le pide a la señora Tsuru que le escriba el suyo, porque tiene mejor letra, y no le dice qué pone. La señora Tsuru lo escribe sin preguntar.",
            Parrafo.PENSAMIENTO to "{nombre} mira el cielo de la noche buscando las dos estrellas, la tejedora y el pastor, a cada lado de la mancha blanca del río del cielo. Piensa que verse una vez al año es poco. Luego piensa en su madre, a la que ve dos veces, y le parece que las estrellas exageran.",
        ),
        "bon" to TextoEspecial(
            listOf(mapOf(
                Parrafo.MANANA to "Hoy empieza Bon, los días en que los muertos vuelven a visitar a los vivos. En la puerta de la casa de té, la señora Tsuru encendió un fuego pequeño de tallos de cáñamo para que su marido y su hija encuentren el camino. En el altar hay fruta, flores y un caballo hecho con un pepino.",
                Parrafo.GENTE to "La señora Tsuru pasa el día delante del altar de la casa, hablando bajito con su marido muerto, como si estuviera vivo y hubiera llegado tarde. Las sirvientas no la molestan. La vieja Kane cocina sin carne ni pescado, como se debe estos días, y nadie protesta.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en su padre, que murió cuando ella era pequeña y que hoy vendrá a la casa de techo de paja de la aldea, no a la posta. Le gustaría estar allí para recibirlo. Piensa que su padre sabrá que ella está aquí, y que a lo mejor pasa a verla de camino.",
            )),
            sigue = mapOf(Parrafo.ANOCHECER to listOf(
                "Al anochecer, en la plaza de la posta, se baila la danza de Bon en corro alrededor de un tablado con un tambor, todos a la vez, la gente de la posta y los viajeros que se quedan, con los mismos gestos lentos. {nombre} baila al lado de Gonta, que baila como un oso. Se ríen.",
                "Esta noche, la última de Bon, se despide a los muertos: se encienden fuegos en las puertas y se echan al río farolillos de papel, que se van con la corriente, cientos de lucecitas en el agua oscura. {nombre} echa uno por su padre. Lo mira irse hasta que no lo ve.",
            )),
        ),
        "yabuiri7" to unDia(
            Parrafo.MANANA to "Hoy es su segundo día libre del año, el de verano. {nombre} sube al monte por el sendero de los arrozales, verdes y llenos de ranas, con un pañuelo de envolver lleno de regalos: un abanico para su madre, dulces para su hermano, un poco de tabaco para el viejo de la casa de al lado.",
            Parrafo.TRABAJO to "A esta hora no trabaja: está en la aldea, ayudando a su madre a quitar las malas hierbas del arrozal, con el agua hasta los tobillos y las sanguijuelas en las piernas, como cuando era niña. Su madre le dice que no, que hoy descanse. Ella sigue.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, sentada a la puerta de la casa de su madre, mirando los arrozales del valle, que aquí todo es igual que cuando se fue: el arroyo, el olor, los vecinos. Lo que cambió es ella. Ya no sabría vivir aquí. Mañana, de vuelta al camino. No le pesa.",
        ),
        "tsukimi" to unDia(
            Parrafo.MANANA to "Hoy es la noche de la luna llena de otoño, la más bonita del año. En la ventana de la casa de té que da al este, la señora Tsuru puso una bandeja con bolitas de arroz en pirámide, un jarrón con hierba de las pampas, unas castañas y un poco de sake para la luna.",
            Parrafo.ANOCHECER to "Al anochecer sale la luna detrás del monte, redonda, amarilla, enorme, y la posta entera se calla para mirarla. Los viajeros se sientan en el banco de la casa de té con una taza de sake. {nombre} se queda en la puerta, con la bandeja vacía contra el pecho. Nadie le dice que trabaje.",
            Parrafo.PENSAMIENTO to "{nombre} mira la luna y piensa que su madre, en la aldea, la está mirando también, a la misma hora, y su hermano, y la gente de Edo y de Kioto, y los viajeros del camino. Es la única cosa que miran todos a la vez. Piensa que eso es casi como estar juntos.",
        ),
        "daimio_vuelta" to unDia(
            Parrafo.MANANA to "Hoy pasó por la posta otra procesión, la de un daimio que vuelve de Edo a su tierra después de su año junto al shogun. Venía más deprisa que las que van, con los hombres cansados y con ganas de llegar. Los heraldos gritaban igual, y la posta se arrodilló igual.",
            Parrafo.TRABAJO to "A esta hora sirve té a los rezagados de la procesión, que van detrás, a su paso: porteadores de cajas, mozos de caballos, criados que se quedaron atrás. No pueden pararse mucho. Beben de pie, de un trago, pagan y corren para alcanzar a los demás. Uno le guiña un ojo.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que los señores pasan la vida yendo y viniendo de Edo, un año allí y otro en su tierra, con cientos de hombres detrás, como una marea. Piensa que el shogun lo quiere así para tenerlos ocupados y que no peleen. La vieja Kane dice que eso no se dice.",
        ),
        "fin_anio" to unDia(
            Parrafo.MANANA to "Hoy empezaron a pasar por la posta los cobradores del fin de año, con sus libros de cuentas, porque antes del año nuevo todas las deudas se tienen que pagar. A la casa de té vinieron dos: el del arroz y el del sake. La señora Tsuru los recibió con cara de pocos amigos.",
            Parrafo.GENTE to "La señora Tsuru pasa la tarde con el ábaco en la cocina, contando monedas, mientras las sirvientas hacen como que no oyen. Al final paga a los dos cobradores, pero al del sake le regatea tanto que el hombre se va casi pidiendo perdón. La vieja Kane aplaude cuando se va.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en las deudas de su madre en la aldea, que son por el arroz y por la renta, y en que este año ella mandó un poco más. Piensa que el fin de año es como un río que hay que cruzar: todo el mundo se moja, y algunos se ahogan. Ella quiere llegar seca al otro lado.",
        ),

        // ---------- Los de la temporada 1 ----------
        "osen" to unDia(
            Parrafo.MANANA to "Hoy llegó a la casa de té una sirvienta nueva, Osen, de una aldea del otro lado del río, con su padre, que la dejó en la puerta con un hatillo y se fue sin volverse. Tiene doce años, la cara redonda y los ojos llenos de lágrimas que no deja caer.",
            Parrafo.TRABAJO to "A esta hora le enseña a Osen a servir el té, como le enseñaron a ella: de rodillas, con las dos manos, la taza girada hacia el viajero, la mirada baja, sin hablar si no le hablan. Osen derrama la primera taza encima de un mercader. El mercader se ríe. Osen llora.",
            Parrafo.GENTE to "Esta noche Osen duerme al lado de {nombre} en el cuarto de las sirvientas y llora bajito, para que no la oigan. {nombre} le da la mano en la oscuridad sin decir nada, como hizo con ella otra sirvienta el día que llegó. Al rato, Osen se duerme.",
        ),
        "okage" to unDia(
            Parrafo.MANANA to "Hoy empezaron a pasar por la posta peregrinos hacia Ise, y no paraban: primero cientos, luego miles, a pie, en grupos, con sombreros de paja y banderas de tela con el nombre de su pueblo. Dicen que este año el dios de Ise llama a todos, y que van sin pedir permiso a nadie.",
            Parrafo.PUEBLO to "La posta está llena de peregrinos de lado a lado del camino, y las posadas no dan abasto. Los ricos del lugar ponen mesas en la calle con arroz y té gratis para los que van a Ise, porque dar a un peregrino es dar al dios. La señora Tsuru saca su tetera más grande a la puerta.",
            Parrafo.PENSAMIENTO to "{nombre} mira a una muchacha de su edad que pasa sola, sin nada más que lo puesto, cantando, y piensa que se escapó de alguna casa para ir a Ise, y que nadie la castigará porque va al dios. Piensa que ella también podría. Luego ve la tetera grande hirviendo y se queda.",
        ),
        "plata" to unDia(
            Parrafo.MANANA to "Hoy un samurái joven de camino a Edo se sentó en el banco de la casa de té, entre los peregrinos, y pidió un té. {nombre} se lo sirvió. Bebió despacio, mirando el Fuji, sin decir nada. Al irse, dejó en la bandeja una moneda de plata, que paga cien tés.",
            Parrafo.GENTE to "La señora Tsuru le dice que la plata es suya, que se la dieron a ella y no a la casa, y que la guarde bien. La vieja Kane dice que algo querría el samurái. Las otras sirvientas dicen que era guapo. {nombre} no dice nada. Se la cose en el forro del kimono.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el samurái, que miraba el Fuji como si fuera la última vez, y se pregunta adónde iba y por qué estaba triste. No lo sabrá nunca. Piensa que la moneda es para su madre, para el arroz del invierno. Luego piensa que a lo mejor no toda.",
        ),
        "okage_fin" to unDia(
            Parrafo.MANANA to "Hoy, por primera vez en tres meses, el camino amaneció casi vacío: unos pocos peregrinos rezagados, cojeando, y los viajeros de siempre. La gran peregrinación a Ise se terminó como empezó, sin que nadie supiera por qué. La posta parece dormida.",
            Parrafo.TRABAJO to "A esta hora junta la paja de miles de sandalias rotas que quedó en la entrada de la casa de té, en las cunetas, en los campos, y la quema en un montón detrás de la cocina. El humo huele a camino. Gonta le ayuda. Dice que nunca volverán a ver tanta gente junta.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los miles de caras que pasaron estos meses por la puerta de la casa de té, todas cansadas, todas contentas, todas hacia el oeste. Ninguna volvió por aquí: los peregrinos vuelven por otros caminos. Piensa que el mundo es mucho más grande que la posta.",
        ),
        "sakichi" to unDia(
            Parrafo.MANANA to "Hoy un correo del camino tropezó en la puerta de la casa de té, con su caja de cartas colgada de una vara, y se torció el tobillo. {nombre} lo hizo sentar en el banco, le trajo agua fría y le vendó el pie con un trapo. Se llama Sakichi. Es de Edo y corre el camino.",
            Parrafo.GENTE to "Sakichi se queda en el banco todo el día, con el pie en alto, esperando a otro correo que le lleve la caja, y hablando con {nombre} cada vez que pasa: de Edo, de los teatros, de las tiendas del puente, de Kioto, que es más bonita, dice, pero más aburrida. Ella pasa mucho por el banco.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Sakichi, que se fue al caer la tarde cojeando, con una reverencia y la promesa de pararse a beber té cada vez que pase. Los correos no se paran nunca, dice la vieja Kane. {nombre} piensa que este sí se parará. No sabe por qué lo sabe. Lo sabe.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "posadera", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + OKAGE_MANANA,
            Parrafo.TIEMPO to TIEMPO + OKAGE_TIEMPO,
            Parrafo.TRABAJO to TRABAJO + OKAGE_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + OKAGE_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + OKAGE_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
