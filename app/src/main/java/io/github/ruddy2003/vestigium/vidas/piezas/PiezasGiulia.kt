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
 * El cuento de Giulia, restauradora de frescos en Roma, de 2025 a 2029. Sigue la gramática y el tono de
 * `PiezasDemeas.kt`, que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - Su día tiene dos tramos de andamio, dos comidas y dos ratos de casa (antes de salir y al volver): esos textos valen a
 *   cualquiera de sus horas y no dicen qué comida toca.
 * - Un estado: «agosto» (cada año, la obra cerrada y Roma vacía).
 * - La capilla lee el arco «fresco» (cuánto está restaurado).
 * - Los textos del andamio valen desde que sube por primera vez (`capilla`, temporada 1) hasta que se quita el último
 *   (`ultimo_andamio`, temporada 5); después, otros. Los de todos los días no dicen dónde vive.
 * - Las fiestas de guardar tienen su párrafo de «no trabaja».
 * - No repite lo que ya dice «Su vida»: la motoneta antes de las ocho, los diez metros, el metro cuadrado de semanas, el
 *   bar de la una, el café de pie y el mercado, el teléfono en el bolsillo del overol.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG6.kt`):
 * - Teresa dirige la obra hasta `jubila` (temporada 4); desde entonces, Giulia.
 * - Samir, desde que llega (`samir`, temporada 1) hasta que se va a Nápoles (`samir_napoles`, 5).
 * - Matteo: su novio hasta `matteo` (temporada 2); viven juntos en el Pigneto desde entonces; casados desde `boda` (4);
 *   espera un hijo desde `espera` (5).
 * - Su madre vive en Testaccio hasta `madre_cerca` (temporada 3); desde entonces, a dos calles de Giulia.
 * - Las fiestas que vuelven cada año no nombran a Teresa, a Samir ni a Matteo.
 */
internal object PiezasGiulia {
    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta con las gaviotas, que en Roma gritan desde los tejados como si les debieran dinero, y con el primer autobús que frena en la esquina. Se queda un minuto con los ojos cerrados. Luego pone la moka en el fuego y abre la ventana. Huele a pan."),
        v("{nombre} se despierta antes del despertador, con la cabeza ya en la capilla: en lo que dejó a medias ayer, en un azul que no se deja. Lo piensa en la ducha. Lo piensa en la escalera. Cuando llegue, ya sabrá qué hacer."),
        v("A {nombre} la despierta el teléfono, que vibra en la mesilla con los mensajes de la noche: el grupo de la obra, su madre, un amigo que pregunta si esta noche hay plaza. Los mira sin gafas, con un ojo. Contesta a su madre. Los demás, luego."),
        v("{nombre} se levanta, se recoge el pelo, se pone el mono de trabajo, que ya se tiene de pie solo, y se mira las manos: tiene las uñas cortas, los nudillos secos y una mancha de pigmento verde que no se quita desde el martes. Se pone crema. No sirve. Sale."),
        v("{nombre} se despierta con la luz que entra de lado por la persiana, rayada, y piensa en la luz rasante, la que se usa en la obra para ver los relieves de la pared. Todo, a esta hora, tiene relieve: la sábana, la pared, su brazo. Se ríe de sí misma. Se levanta."),
        v("{nombre} se despierta con la lluvia en la ventana y el ruido de los coches sobre el agua, y piensa en la humedad de la capilla, que con este tiempo sube por las paredes como una enfermedad. Hoy habrá que medir. Se pone dos jerséis bajo el mono.", meses = setOf(10, 11, 12, 1, 2, 3)),
    )

    /** La obra cerrada y Roma vacía, cada agosto (estado «agosto»). */
    private fun ag(texto: String, hacer: Hacer? = null) = v(texto, estado = "agosto", hacer = hacer)

    private val AGOSTO_MANANA = listOf(
        ag("{nombre} se despierta tarde, sin despertador, con el ventilador girando en el techo y el calor ya entrando por las rendijas de la persiana. No hay obra. No hay andamio. Se queda en la cama escuchando la calle, que no hace ruido. Roma, en agosto, duerme."),
        ag("{nombre} se despierta temprano, porque en agosto lo único fresco es la primera hora, y sale al balcón con la moka. La calle está vacía: las persianas bajadas, un gato, un cartel en la panadería que dice cerrado por vacaciones hasta el día 25. Bebe el café despacio."),
        ag("{nombre} se despierta empapada, con la sábana en el suelo, después de una noche de treinta grados en la que el ventilador solo movía el aire caliente de un lado a otro. Se ducha con agua fría. A los cinco minutos, otra vez sudando."),
    )

    private val AGOSTO_TRABAJO = listOf(
        ag("A esta hora no está en la obra, que está cerrada, sino en la mesa de casa, escribiendo la relación técnica del año con el ventilador encima: qué se ha limpiado, con qué, cuánto tiempo, qué se ha encontrado. Cada foto, con su número. Cada número, con su foto.", Hacer.TRABAJO),
        ag("A esta hora ordena las fotos de la capilla en el ordenador, cientos, miles, de cada centímetro de la pared antes, durante y después. Algún día alguien las mirará para saber qué hizo ella. Las ordena como le gustaría encontrarlas.", Hacer.TRABAJO),
        ag("A esta hora lee un artículo de una revista de restauración sobre un gel nuevo para limpiar sales de los frescos, y toma notas en el cuaderno. En septiembre lo probará en un trozo pequeño, escondido, donde no se vea si sale mal.", Hacer.TRABAJO),
        ag("A esta hora va en el tren viejo de Ostia hasta el mar, con media Roma que no se ha ido y que también va al mar, con sombrillas, neveras portátiles, niños y abuelas. Cuarenta minutos de calor. Luego, la playa, el agua tibia, el olor de la crema.", Hacer.CAMINO),
        ag("A esta hora come sandía en un quiosco de la orilla del Tíber, de los que solo abren en verano, sentada en un taburete de plástico, con el jugo cayéndole por la muñeca. El sandiero es un viejo que la llama señorita. Lleva treinta agostos en el mismo sitio.", Hacer.COMER),
        ag("A esta hora come una ensalada fría en casa, con las persianas bajadas y la luz verde que entra por las rendijas: tomate, mozzarella y albahaca del balcón. No se puede cocinar con este calor. No se puede casi nada.", Hacer.COMER),
        ag("A esta hora duerme la siesta en el sofá, con un paño mojado en la frente, y en toda la ciudad, detrás de todas las persianas, todo el mundo hace lo mismo. Ni un coche. Solo las cigarras de los pinos. Roma suena como un pueblo.", Hacer.DORMIR),
        ag("A esta hora riega las plantas del balcón, que se mueren de sed cada tarde y resucitan cada mañana, y luego se riega a sí misma, la cabeza bajo el grifo de la cocina. Ordena un cajón. Lo vuelve a desordenar. En agosto, el tiempo no pasa.", Hacer.CUIDAR),
        ag("A esta hora pasea por el centro vacío, por calles que el resto del año son un río de turistas y que ahora son de ella: la plaza que se ve entera, la fuente que se oye, la iglesia abierta con alguien rezando dentro. Se sienta en un escalón. Nadie la echa.", Hacer.LIBRE),
        ag("A esta hora está en el cine al aire libre de la isla del Tíber, en una silla de plástico, con amigos que tampoco se han ido, viendo una película vieja con subtítulos. Corre un poco de aire del río. Alguien trae cervezas. La película es lo de menos.", Hacer.GENTE),
    )

    private val AGOSTO_PUEBLO = listOf(
        ag("Roma, en agosto, se vacía: los romanos se van a la playa, a la montaña, al pueblo de los abuelos, y en las tiendas cuelgan carteles escritos a mano. Quedan los turistas, que se mueven en manadas por las mismas cuatro calles, y los viejos, que no se van a ningún sitio."),
        ag("En agosto, el asfalto de Roma se ablanda y huele, y las piedras de las plazas devuelven de noche el calor que han tragado de día. Las fuentes de la calle, los grifos de hierro de toda la vida, son lo único fresco. La gente bebe de ellas tapando el chorro con el dedo."),
    )

    private val AGOSTO_ANOCHECER = listOf(
        ag("Al caer la tarde la ciudad respira, por fin, y la gente sale a la calle como sale de debajo del agua, despacio. Huele a jazmín y a basura caliente. {nombre} sube a la azotea del edificio a ver la puesta de sol entre las antenas. Hay más gente arriba. Nadie habla."),
        ag("Anochece, y no refresca. Las ventanas están todas abiertas, con las luces apagadas para no atraer mosquitos, y de cada una sale una tele distinta. {nombre} se tumba en el suelo del salón, que es lo más fresco de la casa, y mira el techo. Septiembre parece muy lejos."),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En la capilla el tiempo de fuera se nota en la pared: en la humedad, en la luz."),
        v("{nombre} mira el cielo desde la motoneta, en un semáforo, entre dos autobuses. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Los romanos hablan del tiempo como de un pariente pesado: con quejas y con cariño."),
        v("{cielo} {de_estacion} Una restauradora sabe que el tiempo es lo que estropea las paredes, y también lo que les da su color."),
        v("{de_estacion} {cielo} Su madre dice que en Roma hay dos estaciones: la del calor y la de esperar el calor."),
        v("Roma tiene un cielo que han pintado mil pintores, y ninguno lo ha pintado igual. {cielo} {de_estacion}"),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, de un azul tan intenso que parece pintado con lapislázuli.",
            "Hace un día claro, y la luz pone de color de miel las fachadas.",
            "No hay una nube, y las cúpulas se recortan contra el cielo como de papel.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto de nubes blancas y altas que no quieren llover.",
            "Pasan nubes grandes desde el mar, y la ciudad se enciende y se apaga.",
            "Está nublado, con una luz gris perla que no hace sombras.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y los adoquines brillan y resbalan como jabón.",
            "Cae una lluvia fina, de las que los romanos no reconocen como lluvia hasta que se mojan.",
            "Llueve fuerte, y las calles se convierten en ríos que bajan hacia el Tíber.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta, con truenos que rebotan en las cúpulas y un agua que cae en sábanas.",
            "Truena, y en dos minutos las calles se vacían y se llenan de vendedores de paraguas.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla sobre el río, y los puentes desaparecen a la mitad.",
        ),
        Clima.VIENTO to listOf(
            "Sopla la tramontana, fría y limpia, que deja el cielo como un cristal.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, y en las escalinatas ponen azaleas y en los muros salen las alcaparras.",
            "En primavera las glicinias cuelgan de las tapias y huelen desde lejos.",
            "Es el tiempo de comer en la calle otra vez.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, y el calor sube de las piedras y no se va ni de noche.",
            "En verano las cigarras cantan en los pinos de los parques.",
            "Es el tiempo de las sandías y de las persianas bajadas.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, cuando los estorninos llenan el cielo al atardecer en nubes que cambian de forma.",
            "En otoño la luz se vuelve dorada y baja, la mejor del año para mirar una pared.",
            "Es el tiempo de las castañas asadas en las esquinas.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, que en Roma es corto y húmedo, y las iglesias son más frías que la calle.",
            "En invierno los romanos se ponen el abrigo como si estuvieran en Moscú.",
            "Es el tiempo de las alcachofas en el mercado y de las estufas en las terrazas de los bares.",
        ),
    )

    /** Textos del andamio, desde que sube por primera vez hasta que se quita el último. */
    private fun an(texto: String) = v(texto, hacer = Hacer.TRABAJO, desde = "capilla", hasta = "ultimo_andamio")

    private val TRABAJO = listOf(
        an("A esta hora limpia el fresco en el andamio, con compresas de papel japonés empapadas en una solución que ablanda la suciedad: las pone, espera los minutos exactos, las quita, mira. Debajo del gris aparece un color. A veces, uno que nadie ha visto en trescientos años."),
        an("A esta hora quita con el bisturí, bajo la lupa, una costra de cal que tapa el pie de un santo, raspando de lado, un milímetro, otro milímetro, sin apoyar nunca la punta. Si apoya, se lleva el color. Respira por la nariz. El pie aparece. Tiene los dedos largos."),
        an("A esta hora consolida un trozo de color que se está levantando, inyectando con una jeringa fina un poco de cola detrás de la escama, y apretando luego con un papel y la yema del dedo, despacio, mientras cuenta hasta treinta. La escama vuelve a su sitio. Esta, sí."),
        an("A esta hora reintegra una laguna de color con rayitas verticales muy finas, de pincel de tres pelos, como se hace en Italia desde hace setenta años: de lejos, se ve el manto entero; de cerca, se ve que la falta es una falta. Mentir, no. Ayudar a ver, sí."),
        an("A esta hora mira la pared con la lámpara de luz rasante, que pone la luz de lado y hace salir todos los relieves: las juntas de cada jornada del pintor, las líneas que marcó con una cuerda, una huella de dedo de hace siglos. Las apunta todas."),
        v("A esta hora toma muestras de color con un bisturí, del tamaño de un grano de arroz, y las lleva al laboratorio en sobres numerados, para saber qué pigmento puso el pintor y qué puso después alguien que lo quiso arreglar. Casi siempre, alguien quiso arreglarlo.", hacer = Hacer.TRABAJO),
        v("A esta hora fotografía la pared, centímetro a centímetro, con una regla de colores al lado para que nadie dude después de cuál era el color de verdad. Cada foto va a un archivo, cada archivo a una carpeta. Restaurar es también llevar las cuentas de un muro.", hacer = Hacer.TRABAJO),
        v("A esta hora atiende a una funcionaria de la Superintendencia, que ha venido a ver la obra con una carpeta y unas gafas en la punta de la nariz. Mira, pregunta, apunta, frunce la boca. Al final dice que está bien. {nombre} respira. Dice lo mismo cada vez.", hacer = Hacer.TRABAJO),
        v("A esta hora prepara la relación final de la obra en una mesa de la sacristía, con los cientos de fotos, las muestras, las fichas: qué se encontró, qué se hizo, qué no se hizo y por qué. El fresco ya está limpio. Ahora hay que contarlo, para el que venga dentro de cien años.", hacer = Hacer.TRABAJO, desde = "ultimo_andamio"),
        v("A esta hora visita una iglesia del Aventino donde la Superintendencia quiere que mire unos frescos con humedades, para un presupuesto. Sube a una escalera, enciende la linterna, toca la pared con la palma. Está mojada. Va a ser largo. Va a ser bonito.", hacer = Hacer.TRABAJO, desde = "ultimo_andamio"),

        v("A esta hora come en un horno de la calle de al lado de la iglesia, de pie en la barra, un trozo de pizza blanca rellena de mortadela, doblada en un papel de estraza que se pone transparente con el aceite. Cinco minutos. Luego, un café. Luego, otra vez arriba.", hacer = Hacer.COMER),
        v("A esta hora come en casa un plato de pasta: espaguetis con tomate y albahaca, o con pecorino y pimienta, que se hace en diez minutos y que no está tan bueno como el de su madre. Come con el teléfono al lado, leyendo noticias que se le olvidan en seguida.", hacer = Hacer.COMER),
        v("A esta hora come en una trattoria pequeña de manteles de cuadros, con un plato de alcachofas a la romana, o de garbanzos, o de lo que haya en la pizarra, y un vaso de vino de la casa que viene de los Castelli. El camarero la conoce. No le trae la carta.", hacer = Hacer.COMER),

        v("A esta hora cruza el Tíber en motoneta, por el puente, con el río verde y lento debajo y la cúpula de San Pedro a la derecha, encima de todo, como un sombrero. Se lo ha visto mil veces. Lo mira igual. Detrás, un taxi le pita. Arranca.", hacer = Hacer.CAMINO),
        v("A esta hora atraviesa la ciudad en motoneta por las calles de atrás, que conoce como su casa, esquivando turistas, adoquines sueltos, una furgoneta parada en doble fila, un cura en bicicleta. En los semáforos se pone la primera, como los motoristas de verdad.", hacer = Hacer.CAMINO),
        v("A esta hora va en el tranvía, porque llueve o porque la motoneta no arranca, de pie entre estudiantes y señoras con carritos, mirando por la ventanilla los muros de Trastevere. El tranvía se para cada cien metros. Llega tarde. Como todos.", hacer = Hacer.CAMINO),

        v("A esta hora duerme con la ventana entreabierta y los ruidos de Roma entrando: una moto, una pelea de enamorados en la acera, una botella que rueda, las campanas de alguna iglesia que dan una hora que no es. Duerme bien igual. Duerme en Roma desde que nació.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y sueña con caras de santos que la miran desde la pared con los ojos tapados de hollín, y que ella tiene que limpiar antes de que se haga de día. Siempre se despierta cuando va por la segunda cara.", hacer = Hacer.DORMIR),
        v("A esta hora duerme hondo, con los brazos doloridos de tenerlos todo el día levantados, como los pintores de las bóvedas que se quedaban con el cuello torcido. Se despierta una vez, se da la vuelta, piensa en un azul. Se vuelve a dormir.", hacer = Hacer.DORMIR),

        v("A esta hora pone la casa en orden: la cama, los platos, la ropa que no se secó en el tendedero del balcón, las plantas que se secan. Pone la radio. Una tertulia de fútbol, a gritos. La deja. Le hace compañía, aunque no le interesa nada.", hacer = Hacer.CUIDAR),
        v("A esta hora se lava el pelo para quitarse el polvo de la obra, que es polvo de yeso, de cal y de siglos, y que se mete en todas partes. El agua sale gris. Se frota las manos con un cepillo de uñas. Se pone crema otra vez. Mañana, lo mismo.", hacer = Hacer.CUIDAR),
        v("A esta hora llama a su madre, como cada día, para nada: para saber si ha comido, si ha visto a la vecina, si le duele la cadera, si sabe que en la tele dijeron no sé qué. Hablan diez minutos. Cuelgan. Su madre vuelve a llamar porque se le olvidó algo.", hacer = Hacer.CUIDAR),

        v("A esta hora pasea por la orilla del Tíber, abajo, junto al agua, donde no llega el ruido del tráfico, entre corredores y pescadores que no pescan nada. Los plátanos de arriba dejan caer sus hojas como manos. Camina hasta el puente siguiente, y vuelve.", hacer = Hacer.LIBRE),
        v("A esta hora toma el aperitivo en un bar de la plaza con una amiga de la universidad, un spritz naranja y un platito de patatas y aceitunas. Se cuentan las semanas. La amiga trabaja en un ministerio y se aburre. Se tienen envidia por turnos.", hacer = Hacer.LIBRE),
        v("A esta hora entra en una iglesia que no es la suya, cualquiera, de las mil que hay, solo a mirar un cuadro o un fresco que le gusta, como quien va a ver a un amigo. Se sienta en un banco. Se lo mira entero. Nadie le cobra nada. Es lo mejor de Roma.", hacer = Hacer.LIBRE),

        v("A esta hora está en una plaza del barrio con amigos, sentados en los escalones de una fuente, con cervezas de la tienda que abre hasta tarde. Se habla de alquileres, de trabajos, de política, de nada. Un músico toca la guitarra. Mal. Le dan un euro.", hacer = Hacer.GENTE),
        v("A esta hora está en la terraza de un amigo, en lo alto de un edificio viejo, con vino, aceitunas y la ciudad entera a los pies, las cúpulas iluminadas y las gaviotas que no duermen. Alguien dice que Roma es la ciudad más bonita del mundo. Nadie le lleva la contraria.", hacer = Hacer.GENTE),
        v("A esta hora ve una serie en el sofá, con los pies encima de la mesa y un helado en un vaso, y se queda dormida a la mitad del segundo capítulo. Se despierta con los créditos. No sabe quién era el asesino. Lo volverá a ver. O no.", hacer = Hacer.GENTE),
    )

    private val PUEBLO = listOf(
        v("{entorno} La iglesia es pequeña y vieja, en una calle de Trastevere por la que pasan cada día miles de turistas sin entrar: una fachada sencilla, una puerta gastada, y dentro, a la derecha, la capilla de los frescos, que es la suya, aunque sea de todos."),
        v("Trastevere, por la mañana, es un barrio: el panadero, la frutería, los viejos en el bar, los niños que van a la escuela, las motos aparcadas en la acera. Por la noche, es otra cosa: un parque de atracciones de bares, mesas en la calle y turistas. {entorno}"),
        v("{entorno} En la capilla, el andamio tiene tres pisos de tablas, unidos por escaleras de mano, y en cada piso una mesa con frascos, pinceles, hisopos, papeles japoneses, lupas, bisturíes. Arriba hace calor. Abajo, frío. A media altura, la temperatura de la pared.", hasta = "ultimo_andamio"),
        v("{entorno} En la capilla ya no hay andamio, y por primera vez en años se ve entera desde la puerta, de abajo arriba, como la veían los fieles. {nombre} todavía levanta el pie, sin querer, al entrar, para subir a una escalera que no está.", desde = "ultimo_andamio"),
        v("Roma es una ciudad hecha de capas, como una pared con siete enlucidos: debajo de una iglesia hay una casa romana, y debajo, un templo, y debajo, otra cosa. {nombre} trabaja en una de esas capas. Le gusta pensar que alguien, algún día, trabajará en la suya. {entorno}"),
        v("{entorno} Al lado de la iglesia hay un bar con tres mesas fuera y un camarero que se sabe lo que toma cada uno de la obra sin preguntar: un capuchino, un cortado, un café largo. Lo apunta en una libreta. Se paga el viernes. Es el banco de la obra."),
        v("El tráfico de Roma es una conversación a gritos entre coches, motos, autobuses, taxis y peatones que cruzan donde quieren, mirando al conductor a los ojos. {nombre} lo atraviesa cada día. Nunca ha tenido un accidente. Toca madera. {entorno}"),
    )

    /** Cuánto de la capilla está restaurado (arco «fresco»). Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("fresco", 0.0, 0.05, frases = listOf(
            "Los frescos de la capilla están todavía negros de hollín de velas, y solo se adivinan las caras.",
            "En la capilla, debajo del gris, hay figuras que todavía nadie ha visto en siglos.",
        )),
        Entorno("fresco", 0.05, 0.45, frases = listOf(
            "En la pared de la capilla hay ya unos cuadrados limpios, de colores vivos, como ventanas abiertas en el gris.",
            "En la capilla, la parte limpia es todavía pequeña, y parece pintada ayer al lado de la sucia.",
        )),
        Entorno("fresco", 0.45, 0.85, frases = listOf(
            "La bóveda de la capilla ya está limpia, azul y dorada, y las paredes van detrás.",
            "En la capilla, lo limpio ya es más que lo sucio, y la gente que entra a misa se para a mirar arriba.",
        )),
        Entorno("fresco", 0.85, 0.99, frases = listOf(
            "A la capilla le falta poco: un trozo de pared, unos rincones, los últimos retoques.",
            "En la capilla ya casi no queda gris, y el andamio parece estorbar.",
        )),
        Entorno("fresco", 0.99, 1.0, frases = listOf(
            "La capilla está terminada, sin andamio, con todos sus colores a la vista.",
            "En la capilla, los frescos miran otra vez a la gente como cuando se pintaron.",
        )),
    )

    private val GENTE = listOf(
        v("Su madre fue costurera toda la vida, en un taller de la vía del Corso, y tiene unas manos que todavía enhebran una aguja sin gafas. Llama a {nombre} todos los días y le pregunta lo mismo. A veces {nombre} no lo coge. Luego llama ella."),
        v("Los de la obra son pocos: unos restauradores, un andamiero que viene cuando hay que mover los tubos, el sacristán, un viejo que lo vigila todo desde la puerta, y el párroco, que sube al andamio una vez al mes con sotana y casco. Se conocen como una familia pequeña."),
        v("Chiara es su amiga desde la escuela de restauración, que trabaja en un museo de Florencia y la llama los domingos para quejarse de los turistas florentinos, que son peores que los romanos. {nombre} dice que eso es imposible. Discuten. Es su manera de quererse."),
        v("El sacristán de la iglesia, el señor Fabrizi, tiene ochenta años y abre la puerta cada mañana con una llave de hierro del tamaño de una mano. Le trae a {nombre} un café en un vaso de plástico, sin que se lo pida. Dice que conoció a su abuela. Lo dice de todo el mundo."),
        v("El párroco es un hombre joven, de Calabria, que llegó hace dos años y que mira los frescos con más curiosidad que fe. Le pregunta a {nombre} por cada santo como si fuera un vecino. Ella le cuenta lo que sabe. Él le cuenta lo que dicen los libros. Se completan."),
        v("Teresa es la jefa de la obra, una mujer de sesenta y tantos años con el pelo blanco cortado a cepillo, que restauró la basílica de Asís después del terremoto y que no levanta la voz nunca. A {nombre} le enseñó todo lo que sabe. Le sigue enseñando.", hasta = "jubila"),
        v("Teresa se ha jubilado y vive en Ostia, frente al mar, y {nombre} ha quedado al frente de la obra. La llama cuando no sabe qué hacer, que es menos a menudo de lo que pensaba. Teresa contesta siempre lo mismo: tú lo sabes, mira la pared.", desde = "jubila"),
        v("Samir es el restaurador joven de la obra, nacido en Roma de padres egipcios, con unas manos finísimas y una paciencia que da miedo. Pregunta todo, apunta todo, no se cansa nunca. {nombre} se ve a sí misma hace quince años. Le da un poco de envidia. Mucho cariño.", desde = "samir", hasta = "samir_napoles"),
        v("Samir se fue a restaurar a Nápoles, a una iglesia del centro con frescos de la peste, y le manda fotos por el teléfono a {nombre}, de noche, con preguntas: qué harías tú aquí. Ella le contesta con más fotos. La obra de Roma lo echa de menos. Ella también.", desde = "samir_napoles"),
        v("Matteo, su novio, trabaja en una librería de Monti y vive en un piso con dos compañeros en el Pigneto. Se ven tres noches a la semana, unas en casa de él, otras en casa de ella, y nunca se acuerdan de quién tiene el cepillo de dientes de quién.", hasta = "matteo"),
        v("Matteo y {nombre} viven juntos en un piso del Pigneto con una terraza llena de macetas que nadie riega, y una gata vieja que vino con él. Él cocina, ella friega. Discuten de política y de la gata. Se reconcilian en la terraza, de noche, mirando las vías del tren.", desde = "matteo", hasta = "boda"),
        v("Matteo, su marido, sigue en la librería de Monti, y a veces pasa por la iglesia a la hora de cerrar y se sienta en un banco, mirando el andamio, a esperarla. Dice que es el único sitio de Roma donde no hay ruido. {nombre} baja con polvo en el pelo. Él se lo quita.", desde = "boda", hasta = "espera"),
        v("{nombre} espera un hijo, y desde que lo sabe no toca los disolventes: los usa Samir, o el que haya, mientras ella dirige desde abajo con la mascarilla puesta. Matteo le ha comprado un libro de nombres. Ella le ha dicho que se llamará como un santo de la capilla. Él no sabe si es broma.", desde = "espera"),
    )

    private val OBJETO = listOf(
        v("En la obra cada cosa tiene su mesa, y cada mesa su cosa. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo aprendió de Teresa, que lo aprendió de otros, en una cadena que llega hasta los pintores."),
        v("Hoy {nombre} se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} Los turistas que se asoman a la capilla no se fijan en eso. Miran el fresco. Hacen bien."),
        v("{dato_objeto} Restaurar es, en buena parte, saber qué no hay que hacer."),
        v("Una restauradora necesita pocas cosas, pero buenas, y las cuida como un cirujano. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("hisopo", "el hisopo", "un hisopo", "El hisopo",
            "El hisopo de la restauradora es un palito de madera con un poco de algodón enrollado en la punta, que se hace a mano cada mañana, decenas; se moja en la solución, se rueda sobre la pintura, nunca se frota, y se tira cuando se ensucia; al final del día hay una montaña de hisopos grises."),
        ObjetoCuento("bisturi", "el bisturí", "un bisturí", "El bisturí",
            "El bisturí de restaurar es el mismo que el de los cirujanos, con hojas que se cambian cada poco porque se mellan contra la cal; se usa casi tumbado, raspando de lado, y nunca con la punta: la punta es para el médico; el filo, para la pared."),
        ObjetoCuento("lampara", "la lámpara de luz ultravioleta", "una lámpara de luz ultravioleta", "La lámpara",
            "La lámpara de luz ultravioleta hace brillar en la pared lo que el ojo no ve: los barnices, las colas, los retoques de otros siglos, cada uno de un color; con ella, un fresco cuenta todas las veces que alguien lo tocó, como una cara cuenta sus cicatrices."),
        ObjetoCuento("motoneta", "la motoneta", "una motoneta", "La motoneta",
            "La motoneta es la manera romana de ir a cualquier sitio: pequeña, ruidosa, aparcada en la acera, entre otras cien; la suya tiene quince años, un arañazo en el costado y un casco blanco que se deja colgado del retrovisor, como todos, porque nadie roba un casco."),
        ObjetoCuento("telefono", "el teléfono", "un teléfono", "El teléfono",
            "El teléfono de una restauradora está lleno de fotos de paredes: grietas, manchas, caras de santos a medio limpiar, lupas pegadas a la pantalla; entre ellas, de vez en cuando, una foto de una persona, que parece perdida en medio de tanta pared."),
        ObjetoCuento("mono", "el mono de trabajo", "un mono de trabajo", "El mono",
            "El mono de trabajo es blanco, o lo fue, de algodón grueso, con bolsillos en todas partes: para los hisopos, los lápices, el bisturí, el teléfono; tiene manchas de todos los colores de la capilla, y se puede leer en él, si se sabe, toda la obra."),
        ObjetoCuento("moka", "la moka", "una cafetera moka", "La moka",
            "La moka es la cafetera de aluminio de todas las casas de Italia, de tres piezas que se enroscan, que hace un ruido de tren cuando sube el café; no se lava nunca con jabón, solo con agua, para que guarde el sabor; cada familia tiene la suya, y la defiende."),
        ObjetoCuento("cuentahilos", "el cuentahílos", "un cuentahílos", "El cuentahílos",
            "El cuentahílos es una lupa pequeña y plegable, de las que usaban los tejedores para contar los hilos de una tela; las restauradoras lo llevan colgado del cuello, y con él miran los granos del pigmento, las grietas, los pelos de pincel que se quedaron en la pintura hace siglos."),
        ObjetoCuento("pigmento", "el pigmento", "un frasco de pigmento", "El pigmento",
            "Los pigmentos se guardan en frascos de cristal, en polvo, con nombres antiguos: tierra de Siena, ocre, verde tierra, negro de huesos; el más caro era el azul de lapislázuli, que venía de Afganistán y costaba más que el oro, y que los pintores ponían solo en el manto de la Virgen."),
        ObjetoCuento("cuaderno", "el cuaderno", "un cuaderno de obra", "El cuaderno",
            "El cuaderno de obra es de tapas duras, con las hojas cuadriculadas, y en él se apunta todo, cada día, a mano: qué se hizo, dónde, con qué, cuánto tiempo, qué temperatura había, qué humedad; los ordenadores se rompen; los cuadernos, si no se mojan, duran siglos."),
        ObjetoCuento("llaves", "las llaves", "un manojo de llaves", "Las llaves",
            "Las llaves de la iglesia son un manojo de hierro que pesa como un martillo: la de la puerta grande, la de la sacristía, la de la capilla, la del armario de la obra, y una pequeña que nadie sabe qué abre, pero que nadie se atreve a quitar del aro."),
        ObjetoCuento("andamio", "el andamio", "un andamio", "El andamio",
            "El andamio de una capilla se monta tubo a tubo, con abrazaderas, sin tocar nunca la pared pintada, y tiene que llegar a todos los rincones sin tapar ninguno para siempre; montarlo cuesta una semana; acostumbrarse a vivir encima, un mes; bajarse de él al final, un poco de pena."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en esas cosas un momento, en la capilla, y luego se le van, porque la pared no sabe de noticias: tiene quinientos años y ha visto pasar papas, guerras, pestes y turistas. A sus {edad} años, a {nombre} le consuela eso."),
        v("Piensa en el pintor que pintó la capilla, del que no se sabe ni el nombre, y en cómo trabajaba: deprisa, antes de que se secara el yeso, un trozo cada día, sin poder corregir. Ella tiene todo el tiempo del mundo y él no tenía ninguno. Le tiene envidia y respeto."),
        v("{mundo} {nombre} piensa que restaurar es un oficio raro: si lo haces bien, no se nota. Nadie dirá nunca qué bonita está esta pared gracias a {nombre}. Dirán qué bonita está esta pared. Y eso, piensa, es exactamente lo que tienen que decir."),
        v("{mundo} A veces, con la cara a un palmo de la cara de un santo, {nombre} siente que el santo la mira. No cree en santos. Pero le habla bajito, mientras le limpia la nariz, como se le habla a un enfermo. El santo se deja."),
        v("Piensa en el dinero, que en su oficio es poco y llega tarde: contratos de un año, facturas que se pagan a los seis meses, un sueldo que no alcanza para el alquiler de Roma. Piensa que lo haría gratis. Piensa que no se lo debe decir nunca a nadie."),
        v("{mundo} A sus {edad} años, {nombre} sabe que va a pasar su vida limpiando paredes de otros, y que eso es una manera de vivir en muchos siglos a la vez. Lo piensa en la motoneta, en un semáforo. Luego el semáforo se pone verde."),
    )

    private val MUNDOS = mapOf(
        "jubileo" to listOf(
            "Se habla de los peregrinos del jubileo, que llenan las calles con sus mochilas y sus cruces de madera.",
            "Se habla de los alquileres de Roma, que suben cada mes porque los pisos se los quedan los turistas.",
            "Se habla de las obras del jubileo, que por fin se han terminado, más o menos.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Al caer la tarde la luz se pone de color naranja y entra de lado por las calles, y todas las fachadas de Roma parecen de oro viejo. {nombre} vuelve a casa en motoneta, despacio, con la cara al sol. Es la hora en que la ciudad está más guapa. Lo sabe. No se cansa."),
        v("Anochece, y los estorninos llegan del campo en nubes enormes, negras, que se doblan en el cielo como un pañuelo, y luego se meten en los árboles de la orilla del río, gritando. {nombre} se para a mirarlos. Los turistas les hacen fotos. Los romanos se tapan la cabeza.", meses = setOf(10, 11, 12, 1, 2)),
        v("Al caer la tarde, en la iglesia, {nombre} apaga los focos uno a uno, y la capilla se queda con la luz de las velas, como la vieron los que la pintaron. Los santos se ponen otra vez serios. Cierra la puerta. Hasta mañana."),
        v("De noche, desde el puente, se ve el río negro con las luces de los faroles temblando en el agua, y la cúpula de San Pedro iluminada al fondo, blanca, flotando. {nombre} se para en mitad del puente. Hace esto desde niña. No lo va a dejar de hacer."),
        v("Anochece, y en las calles de su barrio la gente sale a la puerta de los bares con el vaso en la mano, y los niños juegan al balón contra la pared de una iglesia, y una vieja les grita desde una ventana. Así cada noche desde hace dos mil años, más o menos."),
        v("La última luz del día se queda en lo alto de las cúpulas, rosa, cuando las calles ya están en sombra. Suenan campanas en tres iglesias a la vez, cada una a su ritmo. {nombre} las distingue. Una de ellas es la suya. Llega tarde, como siempre."),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        // ---------- Los que vuelven cada año ----------
        "capodanno" to unDia(
            Parrafo.MANANA to "Hoy es año nuevo, y Roma se despierta tarde, con olor a pólvora y las calles llenas de restos de petardos. Anoche, a las doce, la ciudad entera explotó: cohetes desde todas las terrazas, bengalas, gente gritando. Antes se tiraban los cacharros viejos por la ventana. Ya no, casi.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es año nuevo, y la iglesia está cerrada a la obra. Come las lentejas que manda la tradición, para tener dinero en el año, con un trozo de embutido de cerdo. Cada lenteja, una moneda. Se come el plato entero. Por si acaso.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el año que empieza, y le pide pocas cosas: que no falte trabajo, que su madre esté bien, que la pared no le dé sorpresas. Piensa que la pared siempre da sorpresas. Piensa que para eso le gusta.",
        ),
        "befana" to unDia(
            Parrafo.MANANA to "Hoy es la Befana, la vieja de la escoba que trae los regalos a los niños la noche antes, en un calcetín colgado en la chimenea: caramelos a los buenos, carbón de azúcar a los malos. Su madre le ha dejado a {nombre}, como cada año, un calcetín con carbón. Dice que se lo merece.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Va a la plaza Navona, donde está el mercado de la Befana, con puestos de dulces, juguetes, globos y una vieja disfrazada que da miedo a los niños. Compra turrón. Mira a los niños. Se acuerda de cuando ella tenía miedo de la vieja.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la Befana se lleva todas las fiestas, como dice el refrán, y que desde mañana se vuelve a la vida de siempre, al andamio, al frío de la iglesia. Piensa que le apetece. Piensa que es rara. Se come el carbón de azúcar, que es lo mejor.",
        ),
        "pascua" to unDia(
            Parrafo.MANANA to "Hoy es Pascua, y en la plaza de San Pedro el papa da la bendición a la ciudad y al mundo desde el balcón, ante una plaza llena de gente de todos los países. {nombre} la ve por la tele, en casa de su madre, con el mantel bueno y los huevos de chocolate de los niños de la vecina.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es Pascua. Se come en casa de su madre, como cada año: cordero al horno con patatas, alcachofas, huevos duros bendecidos y la paloma, un bizcocho con forma de pájaro cubierto de almendras. Su madre la regaña porque no come. Come el doble.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que la Pascua, en su oficio, está en todas partes: en la mitad de las paredes que ha limpiado hay un Cristo que resucita, con su bandera blanca y su tumba abierta. Piensa que los pintores pintaron tantas resurrecciones porque querían creérsela. Ella también, un poco.",
        ),
        "liberazione" to unDia(
            Parrafo.MANANA to "Hoy es el 25 de abril, la fiesta de la Liberación, el día en que se recuerda el fin de la guerra y de los fascistas, y en Roma hay coronas de laurel en las lápidas de los partisanos, banderas en los balcones y una marcha que sale de la Porta San Paolo.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Va a la marcha con unos amigos, detrás de una pancarta, cantando la canción de los partisanos, la del invasor y la flor, que se sabe desde la escuela. Hay viejos con pañuelos rojos que la cantan llorando. Ella la canta más fuerte.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en su abuelo, que se escondió en un sótano de Testaccio cuando los alemanes ocupaban Roma y no salió en meses. Él nunca contaba nada. Ella lo sabe por su madre. Piensa que la libertad, como un fresco, hay que limpiarla cada tanto. Si no, se tapa.",
        ),
        "repubblica" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta de la República, el día en que Italia votó dejar de tener rey, y por la vía de los Foros desfilan soldados, bomberos, carabineros a caballo, alpinos con su pluma, y por encima pasan los aviones de la patrulla acrobática dejando una bandera de humo verde, blanco y rojo.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Sube a la terraza de un amigo a ver pasar los aviones, que vuelan tan bajo que hacen temblar los cristales, y dejan en el cielo azul tres rayas de colores que se deshacen despacio. Los niños de la terraza aplauden. Ella también.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su abuela votó por primera vez ese día, con su mejor vestido, y que votó por la República a escondidas de su marido, que era monárquico. Se lo contó cien veces. Piensa que la república de las abuelas empezó con un secreto.",
        ),
        "san_pietro" to unDia(
            Parrafo.MANANA to "Hoy es San Pedro y San Pablo, los patronos de Roma, fiesta en la ciudad, y en la basílica de San Pedro el papa celebra con los arzobispos de todo el mundo, que vienen a recibir una banda de lana blanca. La estatua de bronce de San Pedro lleva hoy tiara y capa roja.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es el santo de la ciudad. Por la noche hay fuegos artificiales sobre el castillo de Sant'Angelo, como en los tiempos de los papas, y la orilla del río se llena de gente. {nombre} los ve desde un puente, sentada en el pretil, con los pies colgando.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en la estatua de bronce de San Pedro, que tiene el pie gastado de tantos besos de peregrinos, liso como un huevo. Piensa que es la restauración al revés: millones de bocas quitando metal, un beso cada vez. Piensa que no se debe restaurar ese pie. Nunca.",
        ),
        "cierra_obra" to unDia(
            Parrafo.MANANA to "Hoy cierra la obra por vacaciones, hasta el final de agosto, y en la capilla se tapan las mesas con plásticos, se guardan los frascos en el armario con llave, se apagan los focos, se deja todo como si fuera a volver mañana. Cuesta irse. Siempre cuesta.",
            Parrafo.TRABAJO to "A esta hora deja la capilla en orden para el verano: cubre la pared con un papel que la protege del polvo, mide la humedad por última vez, apunta los números en el cuaderno y le pide al sacristán que no abra las ventanas. El sacristán dice que no. Lo dice cada año.",
            Parrafo.PENSAMIENTO to "{nombre} piensa, al cerrar la puerta de la iglesia, que la pared va a estar sola un mes, en la oscuridad, como estuvo siglos. No le pasará nada. Ha aguantado pestes y saqueos. Piensa que aguantará un agosto. Piensa que ella no sabe si lo aguantará.",
        ),
        "ferragosto" to unDia(
            Parrafo.MANANA to "Hoy es Ferragosto, la fiesta de la Asunción y el día más vacío del año en Roma: está todo cerrado, hasta los bares, hasta las farmacias, menos una de guardia por barrio. En la calle no hay nadie. Una ambulancia lejos. Un turista perdido con un mapa.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es Ferragosto. Va con unos amigos a comer al campo, a un lago de los volcanes del norte de Roma, con una nevera llena de pasta fría, sandía y vino blanco. Se bañan. Se duermen a la sombra. Vuelven quemados y contentos.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el Ferragosto viene de los romanos de antes, de las fiestas del emperador Augusto, que daba descanso a todos en agosto. Piensa que es lo más romano que queda: dos mil años de vacaciones. Piensa que algunas cosas sí duran.",
        ),
        "vuelve_obra" to unDia(
            Parrafo.MANANA to "Hoy vuelve a la obra, después de un mes, y la iglesia huele a cerrado, a cera y a polvo. Abre las ventanas. Enciende los focos. Quita los plásticos de las mesas. Mira la pared. La pared está igual, como si no hubiera pasado nada. Para ella, no ha pasado nada.",
            Parrafo.TRABAJO to "A esta hora sube al andamio por primera vez desde julio, despacio, y se queda un rato mirando el trozo que dejó a medias, sin tocarlo, para volver a entrar en él. Luego moja un hisopo. Lo pasa. Ya está. Es como volver a montar en bicicleta. Una bicicleta de quinientos años.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que septiembre es su verdadero año nuevo: la ciudad que vuelve, los niños que vuelven a la escuela, el tráfico que vuelve, ella que vuelve al andamio. Piensa que agosto estuvo bien. Piensa que esto está mejor.",
        ),
        "inmaculada" to unDia(
            Parrafo.MANANA to "Hoy es la Inmaculada, fiesta, y en la plaza de España, al amanecer, los bomberos han subido con su escalera más larga a dejar una corona de flores en el brazo de la estatua de la Virgen, en lo alto de la columna. Por la tarde, el papa viene a rezar a sus pies.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es fiesta. Va con su madre a la plaza de España a ver llegar al papa, apretadas entre miles de personas que llevan flores, y que las dejan al pie de la columna hasta hacer una montaña. Su madre lleva rosas blancas. Las deja. Reza un poco.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que hoy empieza la Navidad en Roma: se encienden las luces de las calles, se monta el árbol en la plaza de San Pedro, se abren los mercados. Piensa que a ella la Navidad se le nota en el frío de la capilla. Mañana, otro jersey.",
        ),
        "navidad" to unDia(
            Parrafo.MANANA to "Hoy es Navidad, y anoche se cenó pescado en casa de su madre, como manda la costumbre romana: el bacalao frito, los espaguetis con almejas, las anguilas, que ya no come nadie pero que su madre sigue haciendo. Luego, la misa de medianoche. Luego, los regalos.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es Navidad. Se come otra vez en casa de su madre, con los tíos, los primos, los niños de los primos, la tele encendida sin volumen, el panettone y el turrón, las cartas para jugar a la tómbola con fichas de garbanzos. Se ríe mucho. Se come demasiado.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los belenes de las iglesias de Roma, que va a ver todos los años, uno por uno, como una peregrinación: el de los barrenderos, que tiene casitas hechas con piedras de las calles; el de la plaza; el de su capilla, que ella misma ayudó a montar. Piensa que el suyo es el más feo. Le gusta más.",
        ),

        // ---------- Los de la temporada 1 ----------
        "jubileo" to unDia(
            Parrafo.MANANA to "Hoy Roma está llena de peregrinos del jubileo, el año santo que abrió el papa en Navidad: grupos con banderas, gorras iguales y una cruz de madera al frente, que hacen cola durante horas para pasar por la Puerta Santa de San Pedro. Los autobuses van llenos. Las calles, más.",
            Parrafo.PUEBLO to "La ciudad se ha arreglado para el jubileo: han hecho una plaza nueva delante del castillo de Sant'Angelo, han limpiado fuentes, han cambiado adoquines, han abierto calles que llevaban años cerradas por obras. Por primera vez en años, Roma parece terminada. Casi.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que su capilla, dentro de unos años, también será un sitio al que vengan peregrinos, o turistas, que es lo mismo con otra mochila. Piensa que limpiar un fresco es prepararlo para gente que todavía no ha nacido. Le gusta esa idea.",
        ),
        "capilla" to unDia(
            Parrafo.MANANA to "Hoy {nombre} sube por primera vez al andamio de la capilla de los frescos, recién montado, con Teresa delante y la lámpara en la mano. Arriba, a un palmo de la cara, la pared: negra de hollín de siglos de velas, con figuras que se adivinan como fantasmas debajo de una sábana.",
            Parrafo.TRABAJO to "A esta hora hace las primeras pruebas de limpieza, en cuadraditos de tres centímetros, en un rincón escondido: con agua, con una solución suave, con otra más fuerte. Cada cuadradito con su número, su foto, su apunte. En el cuarto, aparece un rojo. Teresa dice: ese.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que va a pasar años en esta pared, y que la conocerá mejor que su propia cara. Piensa que al final del trabajo sabrá cosas del pintor que no sabe nadie: dónde se equivocó, dónde tenía prisa, dónde se cansó. Será como leer un diario.",
        ),
        "angel" to unDia(
            Parrafo.MANANA to "Hoy, limpiando un rincón alto de la capilla que parecía solo una mancha negra, ha aparecido debajo del hollín la cara de un ángel: un muchacho de pelo rizado, con los ojos levantados y una boca a punto de decir algo. Nadie sabía que estaba ahí. Nadie lo había visto en siglos.",
            Parrafo.TRABAJO to "A esta hora termina de limpiar la cara del ángel, despacio, con hisopos pequeños, primero un ojo, luego el otro. Teresa sube a mirarlo. Se queda callada mucho rato. Luego baja sin decir nada y llama a la Superintendencia. {nombre} se queda arriba, con el ángel.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que este es el momento por el que se hace este trabajo: no el sueldo, no el premio, sino una cara que vuelve después de tres siglos y te mira. Piensa que el ángel tiene la cara de alguien del barrio. Del barrio de hace quinientos años. Del de ahora también.",
        ),
        "francisco" to unDia(
            Parrafo.MANANA to "Hoy, lunes de Pascua, ha muerto el papa Francisco, temprano, en su casa del Vaticano, un día después de haber salido a la plaza a bendecir a la gente desde el coche. Las campanas de todas las iglesias de Roma tocan a muerto. La ciudad se ha quedado callada.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: es lunes de Pascua, fiesta. Iba a ir con amigos a comer al campo, como se hace este día. No va nadie. Se queda en casa con la tele encendida, viendo la plaza de San Pedro llenarse de gente que no sabe qué hacer, que reza, que llora, que mira.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en Francisco, que no era de aquí, que hablaba de los pobres y que se reía. No es creyente. Pero lo quería, como se quiere a un abuelo de otro. Piensa que Roma, que ha visto morir a doscientos papas, no se acostumbra. Hoy tampoco.",
        ),
        "funeral" to unDia(
            Parrafo.MANANA to "Hoy es el funeral del papa en la plaza de San Pedro, con reyes, presidentes, cardenales y doscientas mil personas. Roma está cerrada: calles cortadas, helicópteros, policía en cada esquina. El ataúd es de madera sencilla, como él quiso.",
            Parrafo.TRABAJO to "Hoy {nombre} no trabaja: la iglesia está cerrada. Sale a la calle a ver pasar el coche con el ataúd, que cruza Roma despacio hasta Santa María la Mayor, donde lo van a enterrar, porque él lo pidió así. La gente aplaude al paso. {nombre} no sabe si aplaudir. Aplaude.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que él quiso que lo enterraran fuera del Vaticano, en una iglesia de la ciudad, entre la gente, cerca de un icono de la Virgen que visitaba antes y después de cada viaje. Piensa que eso es lo más romano que hizo. Que se quedó en Roma.",
        ),
        "conclave" to unDia(
            Parrafo.MANANA to "Hoy empieza el cónclave: los cardenales se encierran en la capilla Sixtina para elegir papa, y toda Roma, y medio mundo, mira una chimenea de hierro puesta en el tejado. Si sale humo negro, no hay papa. Si sale blanco, sí. Es la cosa más antigua y más moderna del mundo a la vez.",
            Parrafo.GENTE to "Esta noche {nombre} está en la plaza de San Pedro con miles de personas, mirando la chimenea en las pantallas gigantes, con el teléfono en la mano. A las nueve sale humo. Negro. La plaza suspira junta. Un señor a su lado dice que mañana. Lo dice con seguridad, como si supiera.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los frescos de la Sixtina, que los cardenales tienen encima mientras votan: el Juicio Final, con sus condenados y sus salvados. Piensa que los restauraron hace treinta años, y que la gente se escandalizó de los colores. Piensa que votar debajo de eso debe de dar miedo.",
        ),
        "leon" to unDia(
            Parrafo.MANANA to "Hoy, a última hora de la tarde, ha salido humo blanco de la chimenea de la Sixtina, y las campanas de San Pedro han empezado a tocar, y luego todas las de Roma. Hay papa. Es americano, de Chicago, ha vivido muchos años en Perú, y se llama León, León XIV.",
            Parrafo.TRABAJO to "Esta tarde estaba en el andamio cuando empezaron las campanas. Ha bajado corriendo, con el mono puesto, ha saltado a la motoneta y ha llegado a la plaza de San Pedro justo cuando el papa nuevo salía al balcón, emocionado, a saludar y a hablar de paz.",
            Parrafo.GENTE to "En la plaza la gente grita, llora, se abraza con desconocidos, levanta los teléfonos. Una monja africana le agarra el brazo a {nombre} sin darse cuenta. Su madre la llama: ¿lo estás viendo? Ella le dice que lo está viendo de verdad, que está ahí.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que ha visto la cosa que verán en los libros de historia, desde la plaza, con el polvo de la capilla todavía en el pelo. Piensa que un papa nuevo, para su oficio, es un cliente nuevo: el dueño de la mitad de las paredes de Roma. Se ríe sola de pensarlo.",
        ),
        "samir" to unDia(
            Parrafo.MANANA to "Hoy llega a la obra un restaurador joven, Samir, que acaba de terminar la escuela, con una mochila llena de pinceles, un cuaderno nuevo y una cara de no haber dormido de los nervios. Teresa se lo presenta a {nombre} y le dice: enséñale. {nombre} se acuerda de cuando ella llegó así.",
            Parrafo.TRABAJO to "A esta hora le enseña a Samir a hacer hisopos: un palito, un pellizco de algodón, una vuelta, otra vuelta, apretar sin aplastar. Samir hace diez. Están mal. Hace otros diez. Mejor. Al final del día tiene cien hisopos perfectos y las yemas de los dedos rojas. No se queja.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que enseñar es raro: tienes que decir con palabras lo que haces sin pensar, y al decirlo te das cuenta de lo que sabes. Piensa que Samir le va a enseñar a ella también. Piensa que va a ser bueno. Muy bueno. Se lo nota en cómo mira la pared.",
        ),
        "acutis" to unDia(
            Parrafo.MANANA to "Hoy en San Pedro el papa ha hecho santo a un muchacho de Milán que murió de leucemia a los quince años, Carlo Acutis, que hacía páginas de internet sobre milagros y que vestía vaqueros y zapatillas. Con él, a otro joven de Turín. La plaza está llena de muchachos.",
            Parrafo.GENTE to "Esta tarde {nombre} se cruza con grupos de adolescentes con camisetas que tienen la cara del santo nuevo, que cantan en el autobús canciones con guitarra. Samir dice que es el primer santo que sabía más de ordenadores que él. {nombre} dice que eso no es difícil.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en los santos que limpia en su pared, con sus túnicas, sus barbas y sus palmas, y en cómo se pintaría a este: con vaqueros, una sudadera y un ordenador. Piensa que dentro de quinientos años alguna restauradora lo limpiará, y le parecerá rarísimo. Le gusta pensarlo.",
        ),
        "aguacero" to unDia(
            Parrafo.MANANA to "Hoy ha caído sobre Roma un aguacero de los que llegan de golpe, en una hora lo que llueve en un mes, y la vía del Tritone se ha convertido en un río, con los coches flotando, y las alcantarillas escupiendo agua. En la iglesia se ha ido la luz.",
            Parrafo.TRABAJO to "A esta hora está en la capilla a oscuras, con una linterna, mirando si entra agua por algún sitio. Por la ventana de arriba entra un hilo. Lo seca con trapos, sube al andamio, mira la pared con la linterna, toca. Está seca. Respira. Baja. Se sienta en un banco a esperar la luz.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que el agua es la enemiga de los frescos: entra por las grietas, sube por las paredes, arrastra las sales que hacen saltar el color. Piensa que Roma es una ciudad construida sobre agua, con un río y mil fuentes, que cada tanto se acuerda. Piensa que hoy se ha acordado.",
        ),
        "grieta" to unDia(
            Parrafo.MANANA to "Hoy, limpiando la parte baja de la pared, {nombre} ha visto, debajo de un enlucido viejo, una grieta que baja en diagonal, larga como un brazo, rellena con yeso de otro siglo. Teresa ha parado la obra. Hay que llamar al ingeniero. Dos días sin tocar nada.",
            Parrafo.TRABAJO to "A esta hora espera al ingeniero en la capilla, que llega con unos aparatos que pegan a la pared con cinta y que dicen cuánto se mueve. Dice que es vieja, que no se mueve, que la tapó alguien hace doscientos años. Que se puede seguir. Pero que hay que vigilarla. Ella la mira como a un perro dormido.",
            Parrafo.PENSAMIENTO to "{nombre} piensa en el que tapó la grieta hace doscientos años, otro restaurador, o un albañil, que hizo lo que pudo con lo que tenía. Piensa que ella también tapará cosas que alguien, dentro de doscientos años, encontrará. Piensa que ojalá piense de ella lo mismo: que hizo lo que pudo.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "restauradora", genero = Genero.FEMENINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA + AGOSTO_MANANA,
            Parrafo.TIEMPO to TIEMPO,
            Parrafo.TRABAJO to TRABAJO + AGOSTO_TRABAJO,
            Parrafo.PUEBLO to PUEBLO + AGOSTO_PUEBLO,
            Parrafo.GENTE to GENTE,
            Parrafo.OBJETO to OBJETO,
            Parrafo.PENSAMIENTO to PENSAMIENTO,
            Parrafo.ANOCHECER to ANOCHECER + AGOSTO_ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
