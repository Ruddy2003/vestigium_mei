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
 * El cuento de Ata, agricultor de Çatalhöyük, hacia el 7000 a. C. Sigue la gramática y el tono de `PiezasDemeas.kt`,
 * que el autor aprobó el 11 nov 2026.
 *
 * Lo propio de este cuento:
 * - El pueblo no tiene calles: se camina por los techos y se entra a las casas por un agujero, con una escalera. Los
 *   muertos se entierran bajo las plataformas de la casa, las paredes se enlucen de blanco y en algunas hay pinturas y
 *   cuernos de uro. Todo eso es real (`fuentes.md`).
 * - No hay metales, ni gallinas, ni caballos, ni rueda, ni escritura. Hay trigo, lentejas, ovejas, cabras, perros,
 *   obsidiana, juncos del pantano y pan cocido en hornos.
 *
 * Lo que este cuento tiene que respetar de su calendario (`vidas/anios/AnioG1.kt`):
 * - Del 25 de agosto al 5 de septiembre de la temporada 1 (y en la 5) está en la montaña de la obsidiana: el estado
 *   «montana». Sus variantes de la mañana, el trabajo, el pueblo, la gente y el anochecer son otras.
 * - Ari, su madre, muere en la temporada 2 (`muere_ari`): sus variantes llevan `hasta`.
 * - Seli nace en la temporada 4 (`nace_seli`); el embarazo de Nesu va de `espera_nesu` (temporada 3) a `nace_seli`.
 * - Las fiestas que vuelven cada año no nombran a Ari ni a Seli, y no dicen si Ata sube o no a la montaña.
 */
internal object PiezasAta {
    /** El estado «montana» del calendario: los días en que está en la montaña de la obsidiana. */
    private const val MONTANA = "montana"

    private fun v(texto: String, estado: String? = null, hacer: Hacer? = null, desde: String? = null, hasta: String? = null, meses: Set<Int>? = null) = Variante(texto, estado, hacer, desde, hasta, meses)

    private val MANANA = listOf(
        v("{nombre} se despierta cuando la luz entra por el agujero del techo, que es a la vez puerta, ventana y chimenea. Abajo todavía está oscuro. Sube la escalera de madera con los ojos medio cerrados y sale al techo, donde el aire de la mañana corta."),
        v("Desde el techo se ve todo el pueblo: cientos de techos planos pegados unos a otros, como un panal tirado en la llanura, y en cada uno alguien que se despereza. {nombre} saluda a Dosh, el del techo de al lado, sin decir nada: un gesto con la barbilla basta."),
        v("Lo despierta Tamu, que se le sube encima haciendo todo el ruido que puede. {nombre} se queda un momento más en la plataforma, mirando las paredes blancas, donde la luz del agujero dibuja un cuadrado. Cuando el cuadrado llega al horno, ya es hora."),
        v("Antes de salir, {nombre} aviva el horno, que está junto a la escalera para que el humo suba solo. Nesu amasó anoche, y la masa espera tapada con un paño. Él se lleva un pan de ayer, duro, que se va comiendo por el camino."),
        v("La mañana huele a humo de estiércol seco, que es lo que se quema en el pueblo: la leña está lejos. Desde todos los techos sube un hilo gris, y en los días sin viento el pueblo amanece con una nube encima. {nombre} tose una vez y se le pasa."),
        v("Abajo, en la casa, duermen los muertos: el abuelo bajo la plataforma grande, una tía bajo la del rincón. {nombre} los pisa todos los días al levantarse, con cuidado, y los saluda sin palabras. Así se vive aquí: los de antes, debajo; los de ahora, encima."),
        v("{nombre} se despierta con el balido de las ovejas en los corrales del borde del pueblo, que piden salir. Por el agujero, el cielo todavía está gris. Se echa la capa de piel por los hombros y sube, con la escalera crujiendo bajo los pies."),

        v("{nombre} despierta en la ladera de la montaña, junto a un fuego que ya es ceniza. Ha dormido sobre la piedra, envuelto en la capa, y le duele todo. Los otros se levantan igual de despacio, con la cara gris de frío, y nadie habla hasta que el sol calienta.", MONTANA),
        v("Amanece en la montaña de la obsidiana, lejos del pueblo, y lo primero que ve son las piedras negras brillando entre la hierba seca, como agua que se hubiera vuelto dura. Se come un puñado de grano tostado y se va con los demás a buscar la veta buena.", MONTANA),
    )

    private val TIEMPO = listOf(
        v("{cielo} {de_estacion} En el pueblo el tiempo se mira desde los techos, que es donde se pasa media vida, y desde los techos se ve venir de lejos."),
        v("{nombre} mira el cielo pensando en el trigo, como todos los del pueblo. {cielo} De eso depende la cosecha, y de la cosecha, todo lo demás."),
        v("{de_estacion} {cielo} Los viejos dicen que antes nevaba más, y antes llovía más, y antes todo era más. Los jóvenes los dejan hablar."),
        v("{cielo} En {estacion} la llanura cambia de color, y desde los techos se ve cómo cambia. {nombre} la mira cada mañana, por costumbre, y cada mañana le parece un poco distinta."),
        v("Lo primero que hace al salir al techo es mirar hacia el monte de dos picos, que se ve en los días claros. {cielo} {de_estacion}"),
        v("{de_estacion} {cielo} Las ovejas lo saben antes que nadie, y se apretujan o se dispersan según lo que venga. {nombre} las mira a ellas antes que al cielo."),
    )

    private val CIELOS = mapOf(
        Clima.DESPEJADO to listOf(
            "El cielo está limpio, y a lo lejos se ve el monte de dos picos, azul de tan lejano.",
            "Hace un día claro, de esos en que la llanura parece no acabarse nunca.",
            "No hay una nube, y el sol brilla en los techos blancos de enlucido.",
        ),
        Clima.NUBES to listOf(
            "El cielo está cubierto, de un gris parejo que apaga los colores.",
            "Hay nubes bajas que arrastran su sombra por la llanura.",
            "Está nublado, y el monte de dos picos no se ve.",
        ),
        Clima.LLUVIA to listOf(
            "Llueve, y el agua corre por los techos y se mete por los agujeros de las escaleras.",
            "Cae una lluvia fina desde la madrugada, y la llanura huele a barro.",
            "Llueve, y en los techos nadie hace nada más que tapar los agujeros.",
        ),
        Clima.NIEVE to listOf(
            "Ha nevado, y el pueblo es un solo techo blanco con agujeros que echan humo.",
            "Cae una nieve fina, y los corrales están en silencio.",
        ),
        Clima.NIEBLA to listOf(
            "Hay niebla del pantano, tan espesa que desde un techo no se ve el de al lado.",
            "La niebla tapa la llanura, y el pueblo parece estar solo en el mundo.",
        ),
        Clima.VIENTO to listOf(
            "Sopla el viento de la llanura, que no encuentra en qué pararse en muchos días de camino.",
            "Hay viento, y en los techos hay que sujetar todo lo que se ha puesto a secar.",
        ),
        Clima.TORMENTA to listOf(
            "Hay tormenta: los truenos ruedan por la llanura y todo el mundo baja a las casas.",
            "El cielo se ha puesto negro sobre el monte, y el primer trueno hace llorar a los niños.",
        ),
        Clima.POLVO to listOf(
            "El viento trae polvo de la llanura, y el sol se ve amarillo.",
            "Hay polvo en el aire, tanto que se mete en el pan.",
        ),
    )

    private val ESTACIONES = mapOf(
        Estacion.PRIMAVERA to listOf(
            "Es primavera, cuando el trigo de invierno crece verde y los corderos ya corren.",
            "En primavera la llanura se llena de flores y el pantano, de ranas.",
            "Es primavera, el tiempo de enlucir las casas y de que el mundo vuelva a verse.",
        ),
        Estacion.VERANO to listOf(
            "Es verano, cuando se siega y el sol no deja sombra en los techos.",
            "En verano la llanura se pone dorada y luego parda, y el polvo se mete en todo.",
            "Es pleno verano, y por la noche se duerme en los techos, al fresco.",
        ),
        Estacion.OTONO to listOf(
            "Es otoño, el tiempo de sembrar y de juntar estiércol para el invierno.",
            "En otoño las grullas pasan altas, gritando, hacia donde no hace frío.",
            "Es otoño, y los días se acortan sobre la llanura.",
        ),
        Estacion.INVIERNO to listOf(
            "Es invierno, cuando se vive abajo, junto al horno, y se sale poco.",
            "En invierno la llanura está blanca o parda, y el viento no tiene dónde pararse.",
            "Es invierno, y el humo de todas las casas se junta sobre el pueblo como una nube baja.",
        ),
    )

    private val TRABAJO = listOf(
        v("A esta hora saca las ovejas del corral del borde del pueblo y se las entrega al muchacho que las lleva a pastar. Las cuenta al salir, como cada día. El muchacho también las cuenta, y casi nunca les sale el mismo número.", hacer = Hacer.CUIDAR),
        v("A esta hora enciende el horno con estiércol seco y unas ramas, y sopla hasta que prende. Nesu pega el pan a las paredes calientes, y la casa se llena de humo y de olor. {nombre} sale al techo a toser y vuelve a entrar, porque el olor gana.", hacer = Hacer.CUIDAR),
        v("A esta hora limpia la casa, que aquí es cosa seria: se barre el suelo, se sacan las cenizas, se tapa cualquier grieta. Las casas del pueblo están siempre limpias por dentro. La basura se tira fuera, en un montón que crece y crece.", hacer = Hacer.CUIDAR),

        v("A esta hora camina hacia los campos, que cada año quedan un poco más lejos del pueblo. Va por la orilla del río, con la hoz colgada del cinturón y un pan en la bolsa. El camino es largo y se hace hablando.", hacer = Hacer.CAMINO),
        v("A esta hora cruza el pantano por el sendero de siempre, saltando de piedra en piedra. Las ranas se callan cuando pasa y vuelven a cantar en cuanto se aleja. En el agua quieta se ve reflejado, con la cesta a la espalda, y no se reconoce.", hacer = Hacer.CAMINO),
        v("A esta hora vuelve al pueblo con la cesta cargada, por el mismo camino de la mañana, que de vuelta siempre es más largo. Desde lejos se ve el pueblo, gris sobre la llanura, con el humo de los hornos encima. {nombre} calcula con los ojos lo que le falta.", hacer = Hacer.CAMINO),

        v("A esta hora muele grano en la piedra, de rodillas, empujando la piedra de arriba sobre la de abajo hasta que la harina cae por el borde. Es un trabajo que hacen todos, y que deja los mismos callos en todas las rodillas. {nombre} lo hace mirando la pared.", hacer = Hacer.TRABAJO),
        v("A esta hora talla obsidiana. Golpea el núcleo negro con una piedra redonda y salen lascas finas como hojas, que cortan más que cualquier cosa. Con ellas se hacen hoces, cuchillos y puntas. Hay que tener cuidado: la obsidiana no avisa antes de cortar.", hacer = Hacer.TRABAJO),
        v("A esta hora trenza cuerdas de fibra para las escaleras y para atar las vigas. Retuerce la fibra con las palmas, la dobla y la vuelve a torcer, hasta que la cuerda aguanta su peso. Lo hace sentado en el techo, y se le duerme la pierna.", hacer = Hacer.TRABAJO),
        v("A esta hora arregla el techo: una capa de barro y paja sobre las vigas, apisonada con los pies. Un techo mal hecho gotea en toda la casa, y aquí el techo es también el patio, la calle y la plaza. {nombre} lo pisa con cuidado, como quien pisa la casa de otro.", hacer = Hacer.TRABAJO),
        v("A esta hora escarda el trigo, que ya está alto y verde. Arranca a mano las hierbas que le roban el agua, inclinado, avanzando por los surcos. Al final del día no sabe si le duele más la espalda o el orgullo.", hacer = Hacer.TRABAJO, meses = setOf(3, 4, 5, 6)),
        v("A esta hora siembra: echa el grano en la tierra removida, puñado a puñado, y lo tapa con el pie. El trigo de invierno tiene que estar en la tierra antes de las heladas, y el cielo no espera a nadie.", hacer = Hacer.TRABAJO, meses = setOf(10, 11)),
        v("A esta hora corta juncos en la orilla del pantano, que en invierno son lo único que hay que recoger. Con ellos se hacen esteras, cestas y techos. Vuelve con un haz más grande que él a la espalda, y de lejos parece un arbusto que camina.", hacer = Hacer.TRABAJO, meses = setOf(12, 1, 2)),
        v("A esta hora junta el estiércol de los corrales y lo pone a secar en tortas sobre el techo, en hileras. Es el fuego del invierno, y huele a lo que es. Los niños lo pisan sin querer, y luego lo pisan queriendo.", hacer = Hacer.TRABAJO, meses = setOf(7, 8, 9)),

        v("A esta hora come pan plano, recién despegado de la pared del horno, con un poco de queso de oveja y lentejas cocidas. Se come sentado en el techo, a la sombra de un toldo de esteras. Las migas se las llevan los gorriones, que no piden permiso.", hacer = Hacer.COMER),
        v("A esta hora come lo que llevó en la bolsa: pan duro, un puñado de chícharos tostados, una cebolla silvestre. Come donde lo agarra el hambre, en el campo, sentado en una piedra. El agua del río sabe a barro, pero es fresca.", hacer = Hacer.COMER),
        v("A esta hora come con la familia, todos alrededor del horno, cada uno con su cuenco. Hoy hay carne de oveja, que no hay todos los días. {nombre} le pasa a Tamu la parte de la grasa sin que Nesu lo vea, y Nesu lo ve.", hacer = Hacer.COMER),

        v("A esta hora está en el techo con los vecinos, que es donde se pasa la tarde. Los techos están pegados, y de uno a otro se pasa sin bajar. Se habla del trigo, de las ovejas y de quién va a levantar casa nueva este año, que es lo que se habla siempre.", hacer = Hacer.GENTE),
        v("A esta hora juega a las tabas con Dosh, sentados en el techo de Dosh, con huesitos de oveja. Se apuesta poco, porque hay poco. Dosh hace trampa y {nombre} lo sabe, y lo deja, porque así Dosh está contento y le presta su escalera.", hacer = Hacer.GENTE),
        v("A esta hora escucha a la vieja Uma, que está pintando una pared en una casa del otro lado y ha subido a descansar. Uma habla de cosas antiguas, de cuando el pueblo era la mitad, y todos la escuchan, aunque la mitad se la invente.", hacer = Hacer.GENTE),

        v("A esta hora duerme en la plataforma, envuelto en una piel, con la familia alrededor. La casa es pequeña y caben todos apretados, que es como se pasa menos frío. Lo último que se oye es el horno, que todavía cruje un poco al enfriarse.", hacer = Hacer.DORMIR),
        v("A esta hora duerme, y el pueblo entero duerme con él, como una sola casa con cientos de cuartos. De vez en cuando alguien camina por los techos, y su paso suena encima de las cabezas de todos.", hacer = Hacer.DORMIR),
        v("A esta hora duerme con Tamu pegado a la espalda y los pies hacia el horno. Afuera, un perro ladra en algún techo y otro le contesta desde otro. Luego se callan, y solo queda el viento.", hacer = Hacer.DORMIR),

        v("A esta hora cuida el fuego del campamento de la montaña, que aquí arriba es lo único que tienen. Lo alimenta con ramas de los arbustos y con lo que encuentra. Huele distinto al del pueblo, más limpio, y {nombre} piensa en el horno de casa.", MONTANA, Hacer.CUIDAR),
        v("A esta hora camina por la ladera con los demás, de una veta a otra, con la carga cada vez más pesada. Desde aquí arriba se ve la llanura entera y, muy lejos, una mancha gris que es el pueblo. {nombre} se para a mirarla más de lo que debe.", MONTANA, Hacer.CAMINO),
        v("A esta hora busca en la ladera la veta buena, la que da piedra sin burbujas ni grietas. Golpea, mira, tira, golpea otra vez. Los trozos buenos los envuelve en piel para que no se corten entre ellos, y los malos se quedan en la montaña para siempre.", MONTANA, Hacer.TRABAJO),
        v("A esta hora talla núcleos allí mismo, para cargar menos: le quita la corteza a la piedra y deja solo lo que sirve. A su alrededor el suelo está lleno de lascas negras que brillan, de todos los que vinieron antes. Hay que pisar con cuidado.", MONTANA, Hacer.TRABAJO),
        v("A esta hora come lo que trajeron del pueblo: grano tostado, queso seco y un pan que ya no es pan sino piedra. El agua es de un arroyo frío que baja de lo alto. Nadie se queja, porque quejarse no da de comer.", MONTANA, Hacer.COMER),
        v("A esta hora está con los de la montaña alrededor del fuego, contando historias de otros viajes. El que más veces ha subido dice que una vez encontraron una veta tan grande que se veían la cara en ella. Nadie le cree, y todos quieren creerle.", MONTANA, Hacer.GENTE),
        v("A esta hora duerme sobre la piedra, envuelto en la capa y pegado a los demás para no pasar frío. El cielo aquí arriba tiene más estrellas que en el pueblo, o eso le parece. Antes de dormirse piensa en la casa, y en que los niños ya estarán dormidos.", MONTANA, Hacer.DORMIR),
    )

    private val PUEBLO = listOf(
        v("El pueblo no tiene calles: las casas están pegadas unas a otras, y se camina por los techos. Para entrar en una casa se baja por una escalera desde el agujero del techo, como quien baja a un pozo. {entorno}"),
        v("{entorno} Desde los techos se ve la llanura entera, plana hasta donde se acaba la vista, y el río que la cruza dando vueltas. En el pantano de la orilla hay juncos, aves y mosquitos, y de todo eso vive un poco el pueblo."),
        v("En el pueblo viven miles de personas, más de las que nadie ha contado nunca. Cuando una casa se hace vieja se tumba hasta la rodilla, se rellena y se levanta otra encima, y así el pueblo crece hacia arriba, como un monte. {entorno}"),
        v("Dentro de las casas las paredes son blancas, y en algunas hay pinturas: animales, manos, gente pequeña que corre detrás de un uro. Hay casas con cuernos de uro clavados en la pared, uno encima de otro, que asustan a los niños de las otras casas. {entorno}"),
        v("{entorno} En los techos se hace casi todo lo que no es dormir: se muele, se teje, se cuece, se habla. En los días buenos el pueblo entero está arriba, y de lejos parece que alguien hubiera sembrado gente sobre un monte."),
        v("La basura se tira fuera del pueblo, en montones donde escarban los perros. Allí van a parar las cenizas, los huesos, las lascas rotas y lo que nadie quiere. Los montones han crecido tanto que ya son como colinas pequeñas. {entorno}"),

        v("La montaña de la obsidiana está lejos, a varios días de camino hacia donde sale el sol. No es de nadie: viene gente de otros pueblos a sacar piedra, y todos se saludan de lejos, con la mano abierta, para que se vea que no llevan nada en ella.", MONTANA),
        v("Desde la ladera se ven otras montañas, peladas, y entre ellas valles con el humo de otros pueblos. Hay mucho mundo, más del que se ve desde los techos. {nombre} no sabe si eso lo alegra o lo cansa.", MONTANA),
    )

    /** Lo que se ve alrededor según el humo del monte, la casa nueva y el trigo. Manda la primera regla que se cumple. */
    private val ENTORNOS = listOf(
        Entorno("humo", 0.7, frases = listOf(
            "El monte de dos picos echa humo, y a veces cae una ceniza fina que deja los techos grises.",
            "El humo del monte se ve desde todos los techos, y nadie habla de otra cosa.",
        )),
        Entorno("humo", 0.2, 0.7, frases = listOf(
            "En el horizonte, el monte de dos picos todavía echa un hilo de humo.",
            "El monte sigue humeando, menos que antes, y la gente ya casi no lo mira.",
        )),
        Entorno("casa", 0.05, 0.99, frases = listOf(
            "En la casa de la familia hay obra: la vieja está rellena hasta la rodilla y la nueva sube encima, adobe a adobe.",
            "Toda la familia anda con barro hasta las orejas: la casa nueva todavía no tiene techo.",
        )),
        Entorno("trigo", 0.6, frases = listOf(
            "Los campos de trigo están verdes y altos, y desde los techos parecen un lago.",
            "En los campos el trigo ya está alto, y el viento lo peina hacia el mismo lado.",
        )),
        Entorno("trigo", 0.0, 0.2, frases = listOf(
            "Los campos están pardos, sin nada, esperando la siembra o la nieve.",
            "En los campos no hay más que tierra y pájaros.",
        )),
        Entorno("trigo", 0.2, 0.6, frases = listOf(
            "En los campos el trigo nuevo asoma, todavía bajo.",
            "El trigo está a media altura, y los vecinos ya calculan cuánto dará.",
        )),
    )

    private val GENTE = listOf(
        v("Nesu, su mujer, sabe hacer pan, esteras y cuentas de piedra, y sabe también cuándo {nombre} miente, que es lo más difícil de las cuatro cosas. Cuando discuten, ella sube al techo y él se queda abajo, y al rato los dos están en el techo."),
        v("Ilka teje esteras de juncos con su madre, y ya le salen casi derechas. Tiene los dedos rápidos y la lengua más. {nombre} la mira trabajar y piensa que esa niña va a mandar en la casa que le toque, sea la que sea."),
        v("Tamu quiere ir a todas partes con su padre, y en todas partes se cae. Tiene rodillas de niño de pueblo: siempre peladas. Se pasa el día detrás de los perros de los techos, que lo aguantan como a un perro más."),
        v("Dosh, el del techo de al lado, es su vecino desde que los dos eran niños, y sus casas comparten pared. Se prestan grano, herramientas y opiniones. Cuando Dosh ronca, {nombre} lo oye desde su plataforma, y Dosh dice que es al revés."),
        v("La vieja Uma pinta las paredes de las casas cuando alguien se lo pide: animales, manos, cosas que nadie más sabe dibujar. Se le paga con grano o con queso. Cuando pinta no deja que nadie la mire, y todos la miran."),
        v("Su madre, Ari, vive con ellos y duerme en la plataforma del rincón, la más cercana al horno. Ya no va a los campos, pero muele, teje y manda. Dice que en su tiempo el pueblo era más pequeño y la gente más amable.", hasta = "muere_ari"),
        v("Nesu espera otro hijo, y ya lo sabe todo el pueblo, a veces antes que ella. Las vecinas le traen cosas: un puñado de lentejas, una figurilla de barro, consejos que no pidió. {nombre} sube y baja la escalera por ella, que es lo único que ella le deja hacer.", desde = "espera_nesu", hasta = "nace_seli"),
        v("Seli, la pequeña, duerme en una cesta junto al horno, el rincón más caliente de la casa. Todos bajan la voz cuando duerme y todos se callan cuando llora, que es casi siempre. {nombre} la mira desde la escalera y no sabe a quién se parece.", desde = "nace_seli"),

        v("Los que suben a la montaña son siempre los mismos, más uno o dos nuevos que no saben nada. A los nuevos se les carga más y se les explica menos, como se hizo con todos. {nombre} ya no es nuevo, y lo nota en que lo dejan hablar.", MONTANA),
        v("Nesu y los niños están lejos, en el pueblo, y {nombre} los cuenta en la cabeza antes de dormir, como cuenta las ovejas. Se pregunta si Tamu se habrá caído de algún sitio y si Ilka estará mandando en la casa, como manda cuando él no está. Seguro que sí, las dos cosas.", MONTANA),
    )

    private val OBJETO = listOf(
        v("Hoy, sin saber por qué, se fija en algo de todos los días. {dato_objeto}"),
        v("{dato_objeto} {nombre} lo sabe como se saben las cosas de la casa: sin haberlo aprendido nunca."),
        v("Hay cosas que solo se miran cuando se rompen. {dato_objeto}"),
        v("{dato_objeto} En el pueblo se dice que una casa vale lo que guarda dentro; lo dicen los que guardan poco."),
        v("{dato_objeto} Son cosas que se aprenden mirando a los mayores, como casi todo en el pueblo."),
        v("Cada casa del pueblo tiene las mismas cosas, y cada una las tiene a su manera. {dato_objeto}"),
    )

    private val OBJETOS = listOf(
        ObjetoCuento("hoz", "la hoz", "una hoz", "La hoz",
            "La hoz es un hueso curvo con lascas de obsidiana metidas en una ranura y pegadas con resina; cuando una lasca se mella se cambia por otra, y la hoz dura más que su dueño."),
        ObjetoCuento("piedra_moler", "la piedra de moler", "una piedra de moler", "La piedra de moler",
            "La piedra de moler es una losa ancha y otra pequeña que se empuja encima; la de su casa la trajeron de lejos, porque en la llanura no hay piedra, y está gastada en el centro como una cuna."),
        ObjetoCuento("obsidiana", "la obsidiana", "un trozo de obsidiana", "La obsidiana",
            "La obsidiana es piedra negra de las montañas de fuego, brillante como agua quieta; corta mejor que nada, y se cambia por conchas del mar, por pedernal y por cosas que en el pueblo no tienen nombre."),
        ObjetoCuento("estera", "la estera de juncos", "una estera de juncos", "La estera",
            "La estera es de juncos del pantano trenzados en cuadros; cubre el suelo de la plataforma, se enrolla para llevarla y, cuando alguien muere, a veces se le entierra envuelto en ella."),
        ObjetoCuento("honda", "la honda", "una honda", "La honda",
            "La honda es una tira de cuerda con un hueco en medio, y las piedras son bolas de barro que se hacen a puñados; con ella los niños espantan pájaros del trigo y los hombres, a veces, cazan."),
        ObjetoCuento("cesta", "la cesta", "una cesta", "La cesta",
            "La cesta es de juncos, tan apretada que si se le unta barro por dentro sirve para guardar grano; en la casa hay cestas en todos los rincones, y todas tienen dueño."),
        ObjetoCuento("figurilla", "la figurilla de barro", "una figurilla de barro", "La figurilla",
            "La figurilla es una mujer gorda de barro, sentada, del tamaño de un puño; se guarda en el arcón del grano, y nadie dice para qué, pero nadie la saca de ahí."),
        ObjetoCuento("cuerno", "el cuerno de uro", "un cuerno de uro", "El cuerno",
            "Los cuernos de uro de la pared están clavados en un pilar de barro y enlucidos de blanco; en la casa se les mira de reojo, como a un abuelo serio que no habla pero se entera de todo."),
        ObjetoCuento("ocre", "el ocre", "un trozo de ocre", "El ocre",
            "El ocre rojo se muele en una piedra pequeña y se mezcla con grasa o con agua; con él se pinta en las paredes, en las caras y, a veces, en los huesos de los muertos antes de enterrarlos."),
        ObjetoCuento("escalera", "la escalera", "una escalera de madera", "La escalera",
            "La escalera de la casa es un tronco con muescas, y es la única puerta; cuando se sube y se baja cien veces al día, uno acaba conociendo cada muesca por su nombre."),
        ObjetoCuento("cuenco", "el cuenco de madera", "un cuenco de madera", "El cuenco",
            "El cuenco es de madera tallada, liso por dentro de tanto uso; se come en él, se bebe en él y se guarda en él lo que no cabe en otra parte, y cada uno sabe cuál es el suyo por una muesca."),
        ObjetoCuento("espejo_obsidiana", "el espejo de obsidiana", "un espejo de obsidiana", "El espejo",
            "El espejo es un trozo de obsidiana pulido durante días con arena y agua hasta que la cara aparece dentro, oscura; Nesu lo guarda envuelto en piel y lo saca poco, como si gastara la cara al mirarla."),
    )

    private val PENSAMIENTO = listOf(
        v("{mundo} {nombre} piensa en el trigo como otros piensan en los dioses: con respeto y un poco de miedo. Si el trigo falla, falla todo. A sus {edad} años ha visto tres años malos, y se acuerda de cada uno por su nombre."),
        v("Piensa a veces en los que vivían aquí antes, debajo de su casa, y debajo de esos, otros. El pueblo es una pila de casas muertas, y él vive en la de arriba. Algún día alguien vivirá encima de la suya, y no sabrá cómo se llamaba."),
        v("{mundo} Él piensa en cosas más cercanas: en la grieta de la pared, en el corral, en si alcanzará el grano hasta la siega. Las cosas grandes, dicen los viejos, son para los que pintan las paredes."),
        v("{mundo} A sus {edad} años, {nombre} ha enterrado a dos hijos bajo la plataforma, chiquitos, envueltos en estera. Duermen ahí todavía, y él duerme encima. No le parece triste: le parece que así no se fueron del todo."),
        v("No tiene más que su casa, sus campos y sus ovejas, y no le hace falta más. Su padre tuvo lo mismo, y el padre de su padre, en la casa de abajo. Hay días en que eso le parece mucho, y días en que le parece una cuerda atada al tobillo."),
        v("{mundo} Por las noches, desde el techo, mira el monte de dos picos cuando hay luna, y piensa en el fuego que dicen que tiene dentro. Nadie del pueblo ha subido nunca a verlo. {nombre} tampoco piensa hacerlo: hay cosas que es mejor mirar de lejos."),
    )

    private val MUNDOS = mapOf(
        "paz" to listOf(
            "En el pueblo las cosas van como siempre, que es lo mejor que pueden ir.",
            "Este año nadie habla de hambre, y eso ya es mucho decir.",
            "Las cosechas han sido buenas, y la gente anda de buen humor.",
        ),
        "humo" to listOf(
            "Desde que el monte empezó a echar humo, la gente anda inquieta.",
            "Se dice que el monte está enojado, aunque nadie sabe con quién.",
        ),
        "ceniza" to listOf(
            "La ceniza del monte estropeó parte del trigo, y se nota en los cuencos.",
            "La gente mira el cielo más de lo normal, por si vuelve a caer algo.",
        ),
    )

    private val ANOCHECER = listOf(
        v("Cuando oscurece, la familia baja a la casa por la escalera y el horno se vuelve el centro del mundo. Se cena pan con queso, se cuenta lo del día y se apaga pronto: el estiércol cuesta juntarlo. {nombre} baja el último y tapa el agujero con una estera."),
        v("Al caer la tarde, el pueblo entero está en los techos mirando cómo se pone el sol detrás de la llanura. Los niños corren de techo en techo y las madres los llaman por su nombre, todas a la vez, de modo que no se entiende a nadie."),
        v("Anochece sobre la llanura, y desde el techo se ve el resplandor de los hornos subir por los agujeros de todas las casas, como si el pueblo respirara fuego. {nombre} se queda arriba un rato, mirándolo, hasta que el frío lo hace bajar."),
        v("De noche el pueblo se queda en silencio de golpe, como si alguien hubiera tapado todos los agujeros a la vez. {nombre} se acuesta en la plataforma con el cuerpo molido. Lo último que oye son los perros, que se cuentan cosas de techo en techo."),
        v("Se cena en silencio, porque la boca está ocupada. Después Nesu cuenta lo que se dice en los techos y {nombre} lo que se dice en los campos, y casi siempre es lo mismo. Luego se cubre el horno con ceniza para que amanezca vivo."),
        v("La última luz se queda un rato en las paredes blancas de la casa, entrando por el agujero, cuando abajo ya está oscuro. {nombre} la mira desde la plataforma. Por un momento las pinturas de la pared parecen moverse, y luego se apagan."),

        v("Al caer la tarde encienden un fuego entre unas rocas, al resguardo del viento, y se sientan alrededor con las manos cortadas de tanto tocar obsidiana. Se untan grasa en los cortes. {nombre} se duerme pronto, con la cabeza sobre el fardo.", MONTANA),
        v("De noche en la montaña hace frío de verdad, aunque abajo sea verano. Los hombres se aprietan unos contra otros y se cuentan los pueblos que conocen. {nombre} cuenta el suyo, que es el más grande, y nadie le lleva la contraria.", MONTANA),
    )

    // =====================================================================
    //  Los días especiales de la temporada 1
    // =====================================================================

    private fun dia(vararg parrafos: Pair<Parrafo, String>) = mapOf(*parrafos)
    private fun unDia(vararg parrafos: Pair<Parrafo, String>) = TextoEspecial(listOf(mapOf(*parrafos)))

    private val ESPECIALES = mapOf(
        "corderos" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy nacieron los primeros corderos en los corrales del borde del pueblo, dos de la misma oveja. Los niños bajan corriendo a verlos y los mayores les gritan que no los toquen, que la madre los rechaza. Los tocan igual, con un dedo.",
                Parrafo.PUEBLO to "Estos días los corrales están llenos de balidos de noche y de día, y siempre hay alguien despierto con una oveja que no termina de parir. Los cabritos nacen más listos que los corderos y se escapan antes. Los perros los vigilan sin que nadie se lo pida.",
                Parrafo.GENTE to "Ilka le pone nombre a cada cordero que nace, aunque nadie más los use. {nombre} le explica que no conviene encariñarse con lo que uno se va a comer. Ilka le contesta que entonces él no se encariñe, y sigue poniendo nombres.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días hay leche, que el resto del año escasea: se toma fresca, se cuaja en queso y se guarda en odres. Los niños tienen bigote blanco, y los perros también, porque alguien siempre deja algo destapado.",
                "Cada mañana hay corderos nuevos, y cada mañana alguien cuenta los que hay. Este año vienen bien, dicen los viejos, y lo dicen como si fuera mérito suyo.",
                "Algún cordero nace débil y no aguanta, y los niños lo entierran con mucha ceremonia detrás de los corrales. Los mayores los dejan, porque así aprenden lo que es un entierro antes de necesitarlo.",
            )),
        ),
        "enlucido" to unDia(
            Parrafo.MANANA to "Hoy toca enlucir la casa. Se saca todo al techo: las esteras, las cestas, los cuencos, hasta el fuego. La casa vacía parece más grande y más vieja, con las paredes grises del humo de todo el invierno.",
            Parrafo.TRABAJO to "A esta hora {nombre} cubre las paredes con barro blanco, una capa fina y lisa, alisándola con la mano mojada. Debajo quedan las capas de los años pasados, tantas que si se rascara se podrían contar como los anillos de un árbol. Nadie las rasca.",
            Parrafo.PENSAMIENTO to "{nombre} tapa de blanco una mano pintada que hizo su padre cuando él era niño. Duda un momento, y la tapa igual: así se hace. La mano sigue ahí, debajo, aunque nadie la vea. Él sabe dónde está, y con eso basta.",
        ),
        "lentejas" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empiezan a arrancar las lentejas y los chícharos, que maduran antes que el trigo. Las matas se sacan enteras, con raíz, y se ponen a secar en los techos. En unos días el pueblo entero huele a heno.",
                Parrafo.PUEBLO to "Las lentejas secas se golpean con palos sobre una estera para que suelten el grano, y el viento se lleva las vainas. Es trabajo de todos, hasta de los niños, que golpean más fuerte de lo necesario porque les gusta el ruido.",
                Parrafo.GENTE to "Nesu separa las lentejas buenas de las malas una por una, sentada en el techo, con una cesta a cada lado. Dice que es el mejor trabajo del año, porque se puede hacer hablando. Habla, y separa, y no se equivoca nunca.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días todos los techos tienen matas de lentejas secándose, y el pueblo parece cubierto de un pelo verde y pardo. Los gorriones están felices.",
                "En los techos se golpean lentejas desde temprano, y el ruido de los palos se oye en toda la llanura, como un aguacero que no termina.",
                "Con las lentejas nuevas se hace el primer guiso espeso del año, y se come con pan caliente. Hay quien dice que sabe a verano, y quien dice que sabe a lentejas.",
            )),
        ),
        "siega" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siega. Antes de que salga el sol el pueblo entero va hacia los campos con las hoces de obsidiana, y en los techos solo quedan los viejos y los niños de pecho. Este mes no se habla de otra cosa que de trigo.",
                Parrafo.PUEBLO to "En los campos el trigo está dorado y cruje. Se corta a la altura de la mano, puñado a puñado, y se ata en gavillas que las mujeres llevan al pueblo en la cabeza. Los pájaros siguen a los segadores y se comen lo que se cae.",
                Parrafo.ANOCHECER to "Al anochecer los segadores vuelven con las manos cortadas y la espalda doblada. Cenan poco y se duermen en los techos, sobre las gavillas, sin bajar a la casa. {nombre} se queda dormido con la hoz todavía en el cinturón.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días en los techos se trilla: el trigo se golpea y se pisa, y luego se lanza al aire para que el viento se lleve la paja. El pueblo entero tiene polvo de trigo en el pelo, y estornuda.",
                "En los arcones de barro de las casas se va guardando el grano nuevo, que tiene que durar hasta la siega que viene. Las mujeres lo miden con cestas, y cuentan, y vuelven a contar.",
                "Los campos segados quedan pelados y amarillos, y las ovejas entran a comerse el rastrojo. Desde los techos se ve la llanura cambiar de color en pocos días, de dorado a pardo.",
                "Hay campos que dan más y campos que dan menos, y en los techos todos saben quién tuvo qué. Se comenta en voz baja, porque mañana le puede tocar a uno.",
            )),
        ),
        "techos" to unDia(
            Parrafo.MANANA to "Hoy es la fiesta del pan nuevo. Desde temprano se encienden los hornos grandes de los techos, los que son de varias casas a la vez, y cada familia lleva su masa del primer trigo del año. Huele a pan en toda la llanura.",
            Parrafo.PUEBLO to "El pueblo entero está en los techos, de punta a punta. Se pasa de casa en casa probando el pan de cada una y diciendo que es el mejor, y en cada casa es verdad. Los niños corren entre los mayores con la boca llena.",
            Parrafo.GENTE to "Hay música de flautas de hueso y de palmas, y alguien baila con una piel de leopardo encima, imitando al animal. Los viejos cuentan otra vez la historia del primer pan, que cada año es un poco distinta. Nadie corrige a nadie.",
            Parrafo.ANOCHECER to "Esta noche se duerme en los techos, todos, con la barriga llena y el fresco encima. {nombre} se queda despierto un rato mirando las estrellas, con Tamu dormido sobre el brazo. Hoy le parece que el pueblo es una sola familia, y casi lo es.",
        ),
        "obsidiana" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy sale el grupo que sube a la montaña de la obsidiana: unos cuantos hombres de distintas casas, con fardos vacíos, comida para muchos días y pieles para dormir. El pueblo entero sube a los techos a verlos irse, haciéndose pequeños en la llanura.",
                Parrafo.PUEBLO to "La montaña está lejos, a varios días de camino hacia donde sale el sol, en una tierra de montes que alguna vez echaron fuego. Allí la piedra negra sale del suelo en vetas que brillan. Sin ella no hay hoces ni cuchillos, y sin hoces no hay siega.",
                Parrafo.PENSAMIENTO to "{nombre} piensa en lo raro que es depender de algo que está tan lejos. El trigo crece aquí y las ovejas pastan aquí, pero lo que corta el trigo viene de una montaña que la mayoría del pueblo no ha visto nunca.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Subir a la montaña de la obsidiana lleva días: cruzar la llanura, rodear los pantanos, trepar entre piedras sueltas. Volver lleva más, porque se vuelve cargado. Ninguna piedra pesa tanto como la que se lleva muchos días a la espalda.",
                "En la montaña la piedra negra sale del suelo entre la hierba, y hay que saber cuál sirve. La que tiene burbujas se rompe mal; la que tiene vetas, también. La buena es negra y lisa, y suena distinto al golpearla.",
                "Cada año los de la montaña traen piedra para todo el pueblo, y cada casa paga su parte con grano, con queso o con esteras. Lo que sobra se cambia con gente de lejos por conchas del mar y por pedernal.",
            )),
        ),
        "siembra" to TextoEspecial(
            listOf(dia(
                Parrafo.MANANA to "Hoy empieza la siembra del trigo de invierno. Las primeras lluvias ablandaron la tierra, y el pueblo sale a los campos con palos de cavar, cestas de semilla y la cara de quien sabe que el año empieza ahora.",
                Parrafo.PUEBLO to "En los campos se remueve la tierra con palos y azadas de asta, y se echa la semilla a puñados. Se siembran varios trigos distintos en campos distintos, por si uno falla. Los viejos dicen que el que siembra un solo trigo se merece el hambre.",
                Parrafo.PENSAMIENTO to "{nombre} echa la semilla y piensa que está enterrando la comida de todo el año siguiente, y que hasta el verano no sabrá si hizo bien. Le parece un trato raro con la tierra. Pero la tierra, hasta ahora, casi siempre ha cumplido.",
            )),
            sigue = mapOf(Parrafo.PUEBLO to listOf(
                "Estos días los campos se llenan de gente encorvada y de pájaros que esperan. Los niños los espantan con hondas, y a veces le aciertan a un pájaro y a veces a un vecino.",
                "Cada tarde los sembradores vuelven con barro hasta las rodillas y lo dejan en la escalera, que acaba siendo de barro también. Las mujeres protestan y barren, y al día siguiente, vuelta a empezar.",
                "Por los campos ya sembrados pasa alguien cada día a mirar si asoma algo, aunque se sabe que todavía no. Mirar no hace crecer el trigo, pero tampoco lo estorba.",
            )),
        ),
        "uro" to TextoEspecial(listOf(
            dia(
                Parrafo.MANANA to "Hoy los hombres salieron antes del alba a cazar un uro, uno de los toros salvajes que pastan junto al pantano, más grande que dos hombres juntos y con unos cuernos que no caben en una casa. Volvieron al mediodía con uno muerto y un herido que cojea.",
                Parrafo.PUEBLO to "El uro se descuartiza en el borde del pueblo, y la carne se reparte entre muchas casas, porque un uro no se lo come una familia ni en una luna entera. Los huesos se guardan para herramientas, y los cuernos, con la frente, para una pared.",
                Parrafo.GENTE to "Hoy hay festín, y se come uro asado en los hornos de los techos hasta que nadie puede más. Los cazadores cuentan la caza muchas veces, cada vez con un uro más grande. Los niños imitan la embestida con dos palos en la cabeza.",
            ),
            dia(
                Parrafo.PUEBLO to "Hoy los cuernos del uro se clavan en la pared de la casa del que lo mató, en un pilar de barro, y se enlucen de blanco. Toda la vecindad baja a verlos. Desde hoy esa casa es un poco más importante, y su dueño camina un poco más derecho.",
                Parrafo.ANOCHECER to "Esta noche todavía se come uro, frío, y lo que queda se seca en tiras al humo del horno. {nombre} se acuesta con la barriga llena y piensa en los cuernos de la pared, que mañana lo mirarán como miran todo, desde arriba.",
            ),
        )),
        "nieve" to unDia(
            Parrafo.MANANA to "Hoy amaneció nevado. {nombre} sube la escalera y asoma la cabeza por el agujero: el pueblo entero es un solo techo blanco, con agujeros negros que echan humo. No se oye nada. Hasta los perros se han quedado callados.",
            Parrafo.TIEMPO to "La primera nieve del año cae despacio, en copos grandes que se quedan en el pelo sin derretirse. Los niños salen a los techos a pisarla antes que nadie, y dejan huellas que van de una casa a otra. En la llanura no se ve ni un camino.",
            Parrafo.ANOCHECER to "Esta noche la familia se aprieta alrededor del horno, todos, y nadie sube. Por el agujero del techo entra de vez en cuando un copo, que se derrite antes de tocar el suelo. {nombre} echa al fuego más estiércol del que se debe, y nadie protesta.",
        ),
        "cuernos_padre" to unDia(
            Parrafo.MANANA to "Hoy {nombre} saca de la cesta grande los cuernos del uro que cazó su padre, envueltos en esteras desde que él murió. Los limpia, los mira y decide que ya es hora de ponerlos en la pared. La casa sin ellos parece de otro.",
            Parrafo.GENTE to "Dosh le ayuda a hacer el pilar de barro donde se clavan los cuernos, y Uma viene a enlucirlo de blanco, porque nadie lo hace mejor. Los niños miran desde la escalera sin atreverse a bajar. Ari dice que su marido era más alto que todos ellos.",
            Parrafo.PENSAMIENTO to "{nombre} se acuerda de su padre volviendo de esa caza, cubierto del barro del pantano, con una herida en la pierna que lo dejó cojo. Tenía la edad que él tiene ahora. Desde hoy los cuernos lo miran desde la pared, y no sabe si es para cuidarlo o para vigilarlo.",
        ),
        "estera" to unDia(
            Parrafo.MANANA to "Hoy Ilka terminó su primera estera de juncos, ella sola, sin que su madre le deshiciera ni una vuelta. La extiende en el techo para que la vean todos, y se queda al lado, de guardia, por si alguien la pisa.",
            Parrafo.GENTE to "La estera tiene un borde torcido y un agujero donde se le acabaron los juncos, y es la mejor estera del pueblo. Se lo dice Nesu, se lo dice Ari y se lo dice {nombre}, cada uno sin saber que los otros ya se lo dijeron.",
            Parrafo.ANOCHECER to "Esta noche Ilka duerme sobre su estera, aunque pica y es pequeña. {nombre} la tapa con la piel al pasar y ve que la niña sonríe dormida. Piensa que en unos años esa niña hará esteras mejores que su madre, y que no se lo dirá a su madre.",
        ),
        "casa_dosh" to unDia(
            Parrafo.MANANA to "Hoy el vecino Dosh empezó a tumbar su casa vieja. No la tira entera: la baja hasta la rodilla, saca lo que vale y rellena el hueco con tierra y escombro. Encima levantará la nueva, sobre los mismos muros, como se hace en el pueblo desde siempre.",
            Parrafo.PUEBLO to "Antes de rellenar, Dosh deja a sus muertos donde estaban, bajo las plataformas, y pone una cosa en cada rincón: un cuenco, una cuenta, un hueso. Para que no se sientan olvidados, dice. Los muertos se quedan abajo y la casa nueva sube.",
            Parrafo.GENTE to "{nombre} le ayuda a cargar escombro toda la tarde. Por la pared que compartían se ve ahora la casa de Dosh por dentro, abierta como un cuenco roto, y da un poco de vergüenza mirar. Dosh se ríe y le dice que ya no tiene secretos.",
        ),
        "granizo" to unDia(
            Parrafo.MANANA to "Hoy, al mediodía, el cielo se puso negro sobre la llanura y empezó a caer granizo, grande como garbanzos, golpeando los techos y los campos. Duró poco. Cuando paró, todo el pueblo salió a los techos a mirar hacia el trigo, y nadie dijo nada.",
            Parrafo.TIEMPO to "El granizo dejó la llanura blanca un rato, como si hubiera nevado en primavera, y luego se derritió. En los campos más bajos el trigo verde quedó tumbado, y en otros apenas se nota. Así es el granizo: elige.",
            Parrafo.PENSAMIENTO to "{nombre} va a ver sus campos por la tarde. Uno está bien y el otro no tanto. Hace cuentas con los dedos y le sale que alcanzará, pero justo. Piensa que por eso siembra en dos campos y no en uno, y se lo agradece a su padre, que se lo enseñó.",
        ),
        "sube_montana" to unDia(
            Parrafo.MANANA to "Hoy {nombre} sale con los de la montaña de la obsidiana. Lleva un fardo vacío, pieles, comida para muchos días y la honda, por si acaso. Nesu le ha metido en la bolsa un pan más del que cabe. En el techo Tamu llora a gritos, e Ilka no llora, para que la vean no llorar.",
            Parrafo.GENTE to "Van seis hombres de distintas casas, el más viejo delante. Caminan todo el día por la llanura, sin parar más que para beber. {nombre} no subía a la montaña desde antes de que naciera Tamu, y se da cuenta de que le cuesta más de lo que recordaba.",
            Parrafo.ANOCHECER to "Esta noche duermen al raso, junto a un fuego de ramas, en una llanura sin nada alrededor. Desde aquí ya no se ve el pueblo, ni su humo. {nombre} se acuesta mirando hacia donde está, y piensa que es la primera noche en años que no duerme bajo su techo.",
        ),
        "baja_montana" to unDia(
            Parrafo.MANANA to "Hoy {nombre} vuelve de la montaña. El pueblo se ve desde lejos, primero el humo y luego los techos, y alguien grita desde arriba que ya vienen. Cuando llegan, medio pueblo está en los techos, y los niños corren por la llanura a encontrarlos.",
            Parrafo.PUEBLO to "El fardo de obsidiana se abre en el techo de la casa y los vecinos se acercan a mirar. Hay piedra buena, negra y lisa, de la que suena limpio al golpearla. Lo que le toca se queda en su casa; lo demás se reparte, o se cambia.",
            Parrafo.GENTE to "Tamu se le cuelga del cuello y no lo suelta en toda la tarde. Ilka le pregunta qué hay en la montaña, y {nombre} le dice que piedras, frío y un cielo con más estrellas. Nesu no pregunta nada: le mira las manos, llenas de cortes, y le trae grasa.",
            Parrafo.ANOCHECER to "Esta noche duerme en su plataforma, con el horno encendido y la familia alrededor, y le parece que nunca había dormido tan blando. Antes de dormirse toca la pared, como para asegurarse. Mañana, piensa, tallará la primera hoz con la piedra nueva.",
        ),
        "escalera_tamu" to unDia(
            Parrafo.MANANA to "Hoy Tamu subió solo la escalera de la casa, de abajo hasta el techo, sin que nadie lo sujetara. Llevaba lunas intentándolo. Cuando asoma la cabeza por el agujero se queda quieto, sorprendido de estar ahí, y luego grita para que todo el pueblo se entere.",
            Parrafo.GENTE to "Nesu sube detrás de él con el corazón en la boca. Ari dice desde abajo que ya era hora, que {nombre} subía solo a esa edad, y {nombre} no se acuerda. Ilka dice que ella subía antes, y nadie la contradice.",
            Parrafo.PENSAMIENTO to "{nombre} piensa que en este pueblo un niño empieza a ser alguien cuando sube solo al techo, porque en el techo está todo. Desde hoy Tamu va a querer subir y bajar sin parar. Habrá que vigilar el borde, y los bordes de todos los techos.",
        ),
    )

    val PIEZAS = Piezas(
        vida = "neolitico", genero = Genero.MASCULINO, temporadasEscritas = setOf(1),
        variantes = mapOf(
            Parrafo.MANANA to MANANA, Parrafo.TIEMPO to TIEMPO, Parrafo.TRABAJO to TRABAJO, Parrafo.PUEBLO to PUEBLO,
            Parrafo.GENTE to GENTE, Parrafo.OBJETO to OBJETO, Parrafo.PENSAMIENTO to PENSAMIENTO, Parrafo.ANOCHECER to ANOCHECER,
        ),
        cielos = CIELOS, estaciones = ESTACIONES, objetos = OBJETOS, mundos = MUNDOS, entornos = ENTORNOS,
        especiales = ESPECIALES,
    )
}
